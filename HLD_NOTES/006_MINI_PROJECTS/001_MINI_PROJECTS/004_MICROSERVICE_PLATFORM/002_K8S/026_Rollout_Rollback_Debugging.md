# 026_Rollout_Rollback_Debugging.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Rollout And Rollback Exist

In real production, deployment is not:

```text
Build JAR
Copy to server
Restart application
Hope nothing breaks
```

That model works only for one machine and low traffic.

At product-company scale, deployment means:

```text
Change running software while users are still using it.
```

That is dangerous.

A new version can have:

```text
Bad code
Wrong environment variable
Broken database migration
Slow startup
Bad health endpoint
Memory leak
Wrong image tag
Missing secret
```

If you replace all old Pods at once, one bad deployment can take the full service down.

Kubernetes rollout exists to make version change gradual.

Mental model:

```text
Deployment is not "start new app".
Deployment is "safely replace old reality with new desired reality".
```

One picture:

```text
Old Stable Version
        |
        | controlled replacement
        v
New Stable Version
```

The key word is controlled.

Kubernetes does not simply kill everything and recreate.

It uses the Deployment controller to move the cluster from one state to another.

```text
Before:
3 old Pods serve traffic

During:
some old Pods + some new Pods serve traffic

After:
3 new Pods serve traffic
```

If the new Pods are not healthy or not ready, Kubernetes slows down or blocks the rollout.

If you understand this, rollout/rollback becomes easy.

Do not memorize commands first.

Remember:

```text
Rollout = version transition under control
Rollback = return desired state to a previous known-good ReplicaSet
Debugging = find where the transition got stuck
```

---

# 2. The Wrong Way To Think About Deployment

Wrong mental model:

```text
kubectl apply -f deployment.yaml
    =
Kubernetes updates all containers immediately
```

That is not what happens.

Kubernetes stores a new desired state.

Then controllers reconcile gradually.

Bad model:

```text
Human
 |
 | deploy v2
 v
All old Pods killed
All new Pods started
```

Correct model:

```text
Human changes desired image
      |
      v
API Server stores new Deployment spec
      |
      v
Deployment Controller creates new ReplicaSet
      |
      v
ReplicaSet creates new Pods slowly
      |
      v
Readiness decides traffic safety
      |
      v
Old Pods removed gradually
```

ASCII:

```text
WRONG

old old old
     |
     | update
     v
new new new immediately


CORRECT

old old old
new old old
new new old
new new new
```

This is why Kubernetes Deployment is powerful.

It gives a production-safe transition instead of a risky process restart.

---

# 3. Real World Analogy: Replacing Engines During Flight

Imagine an airline wants to upgrade airplane engines.

Bad approach:

```text
Remove all engines at once.
Install new engines.
Hope plane keeps flying.
```

That is impossible.

Safer approach:

```text
Take one plane out.
Replace engine.
Test it.
Return it.
Then move to next plane.
```

Kubernetes rolling update is similar.

```text
Old Pods are planes currently serving passengers.
New Pods are upgraded planes.
Readiness probe is safety inspection.
Service traffic is passengers.
```

Diagram:

```text
Fleet before upgrade

Plane A old  serving
Plane B old  serving
Plane C old  serving

Upgrade process

Plane A new  testing
Plane B old  serving
Plane C old  serving

After test passes

Plane A new  serving
Plane B old  serving
Plane C old  serving
```

Kubernetes does not trust the new Pod just because the container started.

It waits for readiness.

That is the safety inspection.

```text
Container started != safe for traffic
Readiness passed  = safe for traffic
```

Production lesson:

```text
Zero downtime is not a Deployment feature alone.
Zero downtime requires correct app startup, probes, capacity, and rollout settings.
```

---

# 4. Core Objects In A Rollout

A Deployment does not directly run Pods.

It manages ReplicaSets.

ReplicaSets manage Pods.

```text
Deployment
    |
    | owns rollout strategy
    v
ReplicaSet
    |
    | owns exact Pod count
    v
Pods
    |
    | run containers
    v
Spring Boot process
```

During a rollout, there are usually two ReplicaSets:

```text
Old ReplicaSet -> old image
New ReplicaSet -> new image
```

Example:

```text
Deployment: order-service

Old ReplicaSet:
  order-service-6fd7c9
  image: order-service:1.0.0
  replicas: 3

New ReplicaSet:
  order-service-9ab82d
  image: order-service:1.1.0
  replicas: increasing
```

ASCII:

```text
                 Deployment
              order-service
                    |
        +-----------+-----------+
        |                       |
        v                       v
 Old ReplicaSet            New ReplicaSet
 image 1.0.0               image 1.1.0
 replicas 3 -> 2 -> 1      replicas 0 -> 1 -> 2 -> 3
        |                       |
        v                       v
 old Pods                  new Pods
```

Do not memorize this as object names.

Understand the production reason:

```text
Old ReplicaSet keeps stable capacity.
New ReplicaSet gradually proves itself.
Deployment coordinates the shift.
```

---

# 5. Deployment Spec Fields That Control Rollout

Important rollout fields live under:

```yaml
spec:
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 25%
      maxSurge: 25%
  minReadySeconds: 10
  progressDeadlineSeconds: 600
```

Mental model:

```text
maxUnavailable = how much capacity can be missing during update?
maxSurge       = how much extra capacity can be temporarily added?
minReadySeconds = how long should a Pod stay ready before counted stable?
progressDeadlineSeconds = how long before rollout is considered stuck?
```

Diagram:

```text
Desired replicas = 4

maxUnavailable = 1
maxSurge       = 1

During rollout, Kubernetes may allow:

minimum available Pods = 3
maximum total Pods     = 5
```

ASCII:

```text
Normal:
[old] [old] [old] [old]       total=4 available=4

During rollout:
[new] [old] [old] [old] [new starting]   total may be 5

But available should not drop below 3.
```

These are safety rails.

If you set them badly, Kubernetes can technically do a rollout but still hurt users.

Bad example:

```yaml
maxUnavailable: 100%
maxSurge: 0
```

Meaning:

```text
It is allowed to make all old Pods unavailable before new Pods work.
```

That is close to downtime.

Good default thinking:

```text
Keep enough old Pods alive while new Pods prove readiness.
```

---

# 6. RollingUpdate vs Recreate

Deployment strategy can be:

```text
RollingUpdate
Recreate
```

RollingUpdate:

```text
Gradually replace old Pods with new Pods.
Usually preferred for stateless services.
```

Recreate:

```text
Kill old Pods first.
Then create new Pods.
Usually causes downtime.
```

ASCII:

```text
RollingUpdate

old old old
new old old
new new old
new new new


Recreate

old old old
--- --- ---     downtime window
new new new
```

When would Recreate be used?

Sometimes for apps that cannot run two versions at the same time.

Examples:

```text
Single-writer legacy process
App cannot tolerate old and new schema together
A service with exclusive lock semantics
```

But for modern Spring Boot stateless services, RollingUpdate is normally the right model.

Rule:

```text
If old and new versions can serve traffic together, use RollingUpdate.
If not, fix compatibility if possible.
```

Production-quality systems are designed to allow overlapping versions.

That means:

```text
Backward-compatible database changes
Stable APIs
Graceful shutdown
Idempotent processing
Readiness probes
```

Kubernetes rollout is only safe if the application is compatible with rolling behavior.

---

# 7. What Happens When You Change Image Version

You start with:

```yaml
image: registry.example.com/order-service:1.0.0
replicas: 3
```

Then you apply:

```yaml
image: registry.example.com/order-service:1.1.0
replicas: 3
```

Internal flow:

```text
1. kubectl sends updated Deployment to API Server.

2. API Server validates the object.

3. etcd stores new Deployment spec.

4. Deployment Controller sees Pod template changed.

5. Deployment Controller creates a new ReplicaSet.

6. New ReplicaSet creates new Pods based on rollout limits.

7. Scheduler places new Pods.

8. Kubelet pulls new image.

9. Container starts.

10. Readiness probe runs.

11. Ready Pods enter Service endpoints.

12. Old ReplicaSet is scaled down gradually.
```

Diagram:

```text
kubectl set image
       |
       v
Deployment spec changes
       |
       v
New ReplicaSet created
       |
       v
New Pods created
       |
       v
Readiness passes
       |
       v
Old Pods removed
```

Important:

```text
A rollout starts only when the Pod template changes.
```

Changing Deployment metadata outside the Pod template may not create a new ReplicaSet.

The Pod template includes:

```text
container image
environment variables
ports
labels inside template
annotations inside template
probes
resources
volumes
```

That is why teams sometimes add an annotation to force restart:

```bash
kubectl rollout restart deployment/order-service
```

This changes the Pod template annotation and triggers a new ReplicaSet.

---

# 8. Full Dry Run: Successful Rollout

Desired replicas:

```text
3
```

Initial state:

```text
old-1 Ready
old-2 Ready
old-3 Ready
```

Rollout config:

```yaml
maxUnavailable: 1
maxSurge: 1
```

Step-by-step:

```text
Step 0:
Old RS replicas = 3
New RS replicas = 0

Pods:
old old old
```

```text
Step 1:
Kubernetes creates one extra new Pod because maxSurge allows +1.

Pods:
old old old new-starting

Available:
old old old = 3
```

```text
Step 2:
new Pod passes readiness.

Pods:
old old old new-ready

Available:
4
```

```text
Step 3:
Kubernetes removes one old Pod.

Pods:
old old new-ready

Available:
3
```

```text
Step 4:
Kubernetes creates another new Pod.

Pods:
old old new-ready new-starting

Available:
3
```

```text
Step 5:
new Pod becomes ready, old Pod removed.

Pods:
old new new

Available:
3
```

```text
Final:
new new new
```

ASCII flow:

```text
[O] [O] [O]
[O] [O] [O] [N?]
[O] [O] [O] [N]
[O] [O] [N]
[O] [O] [N] [N?]
[O] [N] [N]
[N] [N] [N]
```

Legend:

```text
O  = old ready Pod
N? = new Pod starting/not ready
N  = new ready Pod
```

The core idea:

```text
New capacity is added before old capacity is removed.
Readiness controls when old capacity can be reduced.
```

---

# 9. Service Behavior During Rollout

A Service does not care about ReplicaSet names.

It cares about labels and readiness.

```yaml
selector:
  app: order-service
```

If both old and new Pods have the same label:

```yaml
labels:
  app: order-service
```

Then the Service can route to both old and new ready Pods during rollout.

Diagram:

```text
Service: order-service
selector app=order-service

          |
          v

Endpoints during rollout:

old-pod-1 Ready
old-pod-2 Ready
new-pod-1 Ready
```

ASCII:

```text
Client
  |
  v
Service
  |
  +--> old Pod 1
  +--> old Pod 2
  +--> new Pod 1
```

Important production rule:

```text
During rolling update, users may hit old and new versions at the same time.
```

Therefore your versions must be compatible.

Bad example:

```text
v2 writes new JSON format
v1 cannot read it
```

During rollout:

```text
User request 1 -> v2 writes data
User request 2 -> v1 reads same data and fails
```

This is not a Kubernetes bug.

This is an application compatibility bug.

Real rollout-safe engineering requires:

```text
Backward-compatible APIs
Backward-compatible DB schema
Feature flags
Expand-and-contract migrations
Graceful shutdown
Idempotent consumers
```

---

# 10. Spring Boot Example: Rollout-Safe App

A rollout-safe Spring Boot service should expose health probes separately.

Example dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Application config:

```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s

management:
  endpoint:
    health:
      probes:
        enabled: true
  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true
```

Kubernetes probes:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5

startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  failureThreshold: 30
  periodSeconds: 5
```

Mental model:

```text
startupProbe   = give slow Spring Boot startup enough time
livenessProbe  = should Kubernetes restart this container?
readinessProbe = should Service send traffic here?
```

ASCII:

```text
Pod starts
   |
   v
startupProbe protects slow boot
   |
   v
livenessProbe watches dead app
   |
   v
readinessProbe controls traffic
   |
   v
Service endpoint added
```

For rollout, readiness is the most important safety gate.

---

# 11. Graceful Shutdown During Rollout

A rollout removes old Pods.

But removing a Pod is not instant death if configured correctly.

Kubernetes sends SIGTERM first.

Flow:

```text
1. Pod selected for termination.
2. Endpoint is removed from Service.
3. Kubelet sends SIGTERM to container.
4. App should stop accepting new work.
5. App finishes in-flight requests.
6. After grace period, Kubernetes sends SIGKILL if still running.
```

Diagram:

```text
Old Pod terminating
       |
       v
Removed from endpoints
       |
       v
SIGTERM sent
       |
       v
Spring Boot graceful shutdown
       |
       v
In-flight requests finish
       |
       v
Container exits
```

YAML:

```yaml
terminationGracePeriodSeconds: 45
```

Spring Boot config:

```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

Optional preStop hook:

```yaml
lifecycle:
  preStop:
    exec:
      command: ["sh", "-c", "sleep 10"]
```

Why sleep?

Sometimes there is propagation delay before all traffic stops reaching the terminating Pod.

A short preStop delay can reduce dropped requests.

But do not blindly add it everywhere.

Understand the reason:

```text
Endpoint removal and external load balancer propagation are not always instantaneous.
```

---

# 12. Rollback Mental Model

Rollback does not mean Kubernetes magically fixes your code.

Rollback means:

```text
Change desired state back to an older ReplicaSet template.
```

Deployment keeps rollout history.

Each Pod template change creates a revision.

```text
Revision 1 -> image 1.0.0
Revision 2 -> image 1.1.0
Revision 3 -> image 1.2.0
```

If revision 3 is bad:

```text
rollback to revision 2
```

ASCII:

```text
Good v1.1.0
     |
     | deploy
     v
Bad v1.2.0
     |
     | rollback
     v
Good v1.1.0
```

Command:

```bash
kubectl rollout undo deployment/order-service
```

Rollback to specific revision:

```bash
kubectl rollout undo deployment/order-service --to-revision=2
```

View history:

```bash
kubectl rollout history deployment/order-service
kubectl rollout history deployment/order-service --revision=2
```

Mental model:

```text
Rollback is another rollout, but toward an older template.
```

It still obeys rollout rules.

It still needs image availability.

It still needs readiness.

It still can fail if the old version is no longer compatible with current database state.

---

# 13. Rollback Is Not Always Safe

People think rollback is a universal undo button.

It is not.

Rollback is safe only if the old version can still run with current environment and data.

Danger cases:

```text
Database migration changed schema destructively
Old image was deleted from registry
Old config Secret was changed
Old app depends on old API contract
Kafka message format changed incompatibly
External dependency changed
```

Example bad migration:

```sql
ALTER TABLE orders DROP COLUMN status;
```

v1 uses:

```text
orders.status
```

v2 removes it.

If v2 fails and you rollback to v1:

```text
v1 starts
v1 queries orders.status
database column missing
v1 crashes
```

Rollback fails.

Safer migration model:

```text
Expand -> Deploy -> Backfill -> Switch -> Contract
```

ASCII:

```text
Step 1: Add new column, keep old column
Step 2: Deploy app that can use both
Step 3: Backfill data
Step 4: Switch reads/writes
Step 5: Later remove old column
```

Kubernetes can rollback Pods.

It cannot rollback unsafe database changes unless you design for it.

Production rule:

```text
Every deployment should have a rollback plan before deployment starts.
```

---

# 14. Deployment Revision History

Deployment stores old ReplicaSets up to:

```yaml
revisionHistoryLimit: 10
```

Example:

```yaml
spec:
  revisionHistoryLimit: 5
```

Meaning:

```text
Keep enough old ReplicaSets to allow rollback.
```

If set too low:

```text
Old ReplicaSet may be garbage collected.
Rollback options reduced.
```

But too high means:

```text
Many old ReplicaSets remain in cluster metadata.
```

View:

```bash
kubectl get rs
```

Example output:

```text
NAME                       DESIRED CURRENT READY
order-service-5dd88c       0       0       0
order-service-6ff9b8       0       0       0
order-service-7ab21d       3       3       3
```

Mental model:

```text
Old ReplicaSets are rollout memory.
```

They usually have zero replicas, but they keep Pod template history.

ASCII:

```text
Deployment
   |
   +-- RS revision 1 replicas=0
   +-- RS revision 2 replicas=0
   +-- RS revision 3 replicas=3
```

---

# 15. Status Commands You Must Understand

Basic:

```bash
kubectl rollout status deployment/order-service
```

History:

```bash
kubectl rollout history deployment/order-service
```

Undo:

```bash
kubectl rollout undo deployment/order-service
```

Pause:

```bash
kubectl rollout pause deployment/order-service
```

Resume:

```bash
kubectl rollout resume deployment/order-service
```

Restart:

```bash
kubectl rollout restart deployment/order-service
```

Get objects:

```bash
kubectl get deploy,rs,pods
kubectl get pods -o wide
```

Describe:

```bash
kubectl describe deployment order-service
kubectl describe rs <replicaset>
kubectl describe pod <pod>
```

Watch:

```bash
kubectl get pods -w
```

Events:

```bash
kubectl get events --sort-by=.lastTimestamp
```

Mental model:

```text
rollout status = high-level transition view
get rs         = old/new generation view
get pods       = runtime view
describe       = decision + event view
logs           = application truth
```

Do not debug rollout only from one command.

Follow the chain.

---

# 16. Debugging Mindset: Where Is The Rollout Stuck?

A rollout can get stuck at many layers.

Layer model:

```text
Deployment spec changed?
        |
        v
New ReplicaSet created?
        |
        v
New Pods created?
        |
        v
Pods scheduled?
        |
        v
Image pulled?
        |
        v
Containers started?
        |
        v
Startup passed?
        |
        v
Readiness passed?
        |
        v
Service endpoints updated?
```

ASCII:

```text
Rollout Debug Ladder

1 Deployment
2 ReplicaSet
3 Pod scheduling
4 Image pull
5 Container startup
6 App logs
7 Probes
8 Endpoints
9 Traffic
```

Commands by layer:

```bash
kubectl describe deployment order-service
kubectl get rs
kubectl describe rs <new-rs>
kubectl get pods -o wide
kubectl describe pod <new-pod>
kubectl logs <new-pod>
kubectl logs <new-pod> --previous
kubectl get endpoints order-service
```

Rule:

```text
Never guess "Kubernetes issue" first.
Find the exact stuck layer.
```

Most rollout failures are not Kubernetes bugs.

They are usually:

```text
Bad image
Bad config
Bad secret
Bad readiness
Bad resources
Bad migration
Bad label selector
```

---

# 17. Failure Case: ImagePullBackOff During Rollout

You deploy:

```yaml
image: registry.example.com/order-service:2.0.0
```

But image tag does not exist.

Symptoms:

```text
kubectl rollout status deployment/order-service
Waiting for deployment "order-service" rollout to finish...
```

Pods:

```text
order-service-new-abc   ImagePullBackOff
```

Events:

```text
Failed to pull image
manifest unknown
unauthorized
```

Debug:

```bash
kubectl describe pod order-service-new-abc
```

Possible causes:

```text
Wrong image tag
Image not pushed
Wrong registry path
imagePullSecret missing
Registry token expired
Private registry unavailable
```

ASCII:

```text
New ReplicaSet created
       |
       v
New Pod object created
       |
       v
Kubelet tries image pull
       |
       v
Registry says no
       |
       v
ImagePullBackOff
       |
       v
Rollout stuck
```

Fix options:

```text
Push correct image
Correct image tag
Restore imagePullSecret
Rollback to old image
```

Important:

```text
Old Pods may still be serving if rolling update settings are safe.
```

This is why maxUnavailable matters.

---

# 18. Failure Case: CrashLoopBackOff During Rollout

Image pulls successfully.

Container starts.

Then app crashes.

Symptoms:

```text
CrashLoopBackOff
```

Spring Boot common causes:

```text
Missing environment variable
Bad DB URL
Wrong password
Flyway migration failure
Port already used
OutOfMemoryError
Invalid profile
ClassNotFound because image build is bad
```

Debug:

```bash
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl describe pod <pod>
```

ASCII:

```text
Image pulled
    |
    v
Container starts
    |
    v
Spring Boot bootstraps
    |
    v
Exception thrown
    |
    v
Process exits
    |
    v
Kubelet restarts
    |
    v
Backoff increases
```

Do not only look at current logs.

For fast crashes, use:

```bash
kubectl logs <pod> --previous
```

That shows the previous terminated container logs.

Production lesson:

```text
Kubernetes retries crashes.
It does not understand your Java exception.
```

You must fix the app or configuration.

---

# 19. Failure Case: New Pods Running But Not Ready

Pod status:

```text
Running
```

But readiness:

```text
0/1 Ready
```

Rollout status:

```text
Waiting for deployment "order-service" rollout to finish:
1 of 3 updated replicas are available...
```

Common reasons:

```text
Readiness endpoint returns DOWN
Wrong readiness path
App cannot connect to DB
Cache warmup not done
Slow startup but no startupProbe
Actuator probes disabled
Management port mismatch
```

Debug:

```bash
kubectl describe pod <pod>
kubectl logs <pod>
kubectl exec -it <pod> -- curl localhost:8080/actuator/health/readiness
```

ASCII:

```text
Container Running
      |
      v
Readiness probe calls endpoint
      |
      v
Endpoint returns 503
      |
      v
Pod not Ready
      |
      v
Service does not route traffic
      |
      v
Rollout cannot safely remove more old Pods
```

This is often good.

It means Kubernetes protected traffic.

Bad readiness is better than no readiness if the app is actually unsafe.

But wrong readiness can block healthy apps.

Rule:

```text
Readiness should represent "can serve user traffic", not "all optional dependencies perfect".
```

---

# 20. Failure Case: ProgressDeadlineExceeded

Deployment has:

```yaml
progressDeadlineSeconds: 600
```

If rollout does not progress within that time, status may show:

```text
ProgressDeadlineExceeded
```

Meaning:

```text
Kubernetes expected rollout progress but did not see enough available updated replicas.
```

Possible causes:

```text
New Pods not created
Pods pending
ImagePullBackOff
CrashLoopBackOff
Readiness failing
Insufficient resources
Quota limit
Bad scheduling constraints
```

Debug:

```bash
kubectl describe deployment order-service
kubectl get rs
kubectl get pods
kubectl describe pod <new-pod>
```

ASCII:

```text
Deployment wants new version
        |
        v
New version not becoming available
        |
        v
Time passes
        |
        v
Deadline exceeded
```

Important:

```text
ProgressDeadlineExceeded does not automatically rollback by itself.
```

You need to decide:

```bash
kubectl rollout undo deployment/order-service
```

or fix the issue and let rollout continue.

Production workflow:

```text
Alert fires
Check rollout status
Inspect new Pods
Decide fix-forward or rollback
Communicate impact
Verify endpoints and traffic
```

---

# 21. Failure Case: Pending Pods During Rollout

New Pods are created but remain:

```text
Pending
```

Common causes:

```text
Insufficient CPU/memory
Node selector matches no nodes
Taints not tolerated
PVC cannot bind
Cluster autoscaler delay
Namespace quota exceeded
Pod anti-affinity too strict
```

Debug:

```bash
kubectl describe pod <pending-pod>
```

Look at events:

```text
0/5 nodes are available: insufficient memory
node(s) had untolerated taint
persistentvolumeclaim is not bound
```

ASCII:

```text
New ReplicaSet
      |
      v
Pod object created
      |
      v
Scheduler tries placement
      |
      v
No suitable node
      |
      v
Pending
      |
      v
Rollout stuck
```

Production mistake:

```text
Increasing replicas or deploying heavier image without enough cluster capacity.
```

Rollout settings can temporarily increase total Pods through maxSurge.

If you have no headroom, even safe rollout can become stuck.

Capacity thinking:

```text
replicas = 10
maxSurge = 25%
temporary pods may be 13
cluster must have room for 3 extra Pods
```

---

# 22. Failure Case: Bad maxUnavailable Causes Downtime

Suppose replicas:

```text
2
```

Rollout config:

```yaml
maxUnavailable: 1
maxSurge: 0
```

Kubernetes can remove one old Pod before creating a new one.

If traffic is high, you now have only one available Pod.

If that one is overloaded, users see errors.

Worse config:

```yaml
maxUnavailable: 100%
maxSurge: 0
```

ASCII:

```text
Before:
[old] [old]

During bad rollout:
[---] [---]    no capacity

Then:
[new?] [new?]
```

This is not a Kubernetes failure.

It is an unsafe deployment budget.

Better thinking for small replica counts:

```yaml
replicas: 2
maxUnavailable: 0
maxSurge: 1
```

Flow:

```text
Keep both old Pods.
Start one new extra Pod.
Only remove old Pod after new Pod is ready.
```

ASCII:

```text
[old] [old]
[old] [old] [new starting]
[old] [old] [new ready]
[old] [new]
[new] [new]
```

For critical services, do not blindly rely on defaults.

Think in capacity numbers, not percentages only.

---

# 23. Failure Case: Label Selector Mistake During Rollout

Service selector:

```yaml
selector:
  app: order-service
```

New Pod template accidentally changes label:

```yaml
labels:
  app: order
```

New Pods may become Running and Ready.

But Service does not select them.

Symptoms:

```text
New Pods ready
Deployment may look okay
Traffic still goes only to old Pods or to no Pods
```

Debug:

```bash
kubectl get pods --show-labels
kubectl describe svc order-service
kubectl get endpoints order-service
```

ASCII:

```text
Service selector:
app=order-service

Old Pod:
app=order-service  MATCH

New Pod:
app=order          NO MATCH
```

Danger:

If old Pods are removed and new Pods are not endpoints:

```text
Service has zero endpoints
```

Production result:

```text
DNS works
Service exists
Pods run
Traffic fails
```

Mental model:

```text
Labels are the wiring.
Wrong labels break invisible wiring.
```

---

# 24. Pause And Resume Rollout

Sometimes you want to make multiple changes before continuing rollout.

Pause:

```bash
kubectl rollout pause deployment/order-service
```

Apply changes:

```bash
kubectl set image deployment/order-service order-service=order-service:1.2.0
kubectl set resources deployment/order-service -c=order-service --limits=memory=1Gi
```

Resume:

```bash
kubectl rollout resume deployment/order-service
```

Mental model:

```text
Pause = stop Deployment controller from progressing rollout
Resume = allow controller to continue
```

ASCII:

```text
Deployment spec changes
      |
      v
Paused
      |
      v
No rollout progress
      |
      v
Resume
      |
      v
Controller reconciles
```

Use cases:

```text
Batch multiple template changes into one revision
Investigate partial rollout
Stop further replacement while debugging
```

Important:

```text
Pause does not stop already running Pods.
Pause does not freeze the whole cluster.
It only pauses Deployment rollout progression.
```

---

# 25. Canary Thinking With Plain Deployment

True canary often uses service mesh, ingress controller, or progressive delivery tools.

But you can understand the idea with simple Kubernetes.

Canary means:

```text
Send small traffic to new version first.
Observe.
Then increase.
```

Plain Deployment rolling update gives Pod-count based gradual rollout, not exact traffic percentage.

Example:

```text
10 Pods total
1 new Pod ready
9 old Pods ready
```

Approximate traffic:

```text
~10% if Service load balances evenly
```

ASCII:

```text
Service endpoints:

Old Old Old Old Old Old Old Old Old New

Traffic roughly spreads across all ready endpoints.
```

But this is not precise canary.

Why?

```text
Connection reuse
Client-side load balancing
Sticky sessions
Uneven request duration
HPA scaling
Readiness timing
```

Production canary tools:

```text
Argo Rollouts
Flagger
Istio traffic splitting
NGINX/Ingress weighted routing
Service mesh
Feature flags
```

Still, the mental model is useful:

```text
Reduce blast radius.
Observe new version before full rollout.
```

---

# 26. Blue-Green Deployment Mental Model

Blue-green means:

```text
Blue = current production version
Green = new full environment
Switch traffic when green is verified
```

ASCII:

```text
Before switch:

Users
  |
  v
Service
  |
  v
Blue Pods v1

Green Pods v2 exist but receive no production traffic
```

After switch:

```text
Users
  |
  v
Service
  |
  v
Green Pods v2
```

In Kubernetes, this is often done using labels/selectors.

Blue Deployment:

```yaml
labels:
  app: order-service
  version: blue
```

Green Deployment:

```yaml
labels:
  app: order-service
  version: green
```

Service selector before:

```yaml
selector:
  app: order-service
  version: blue
```

Service selector after:

```yaml
selector:
  app: order-service
  version: green
```

Mental model:

```text
Rolling update mixes old and new.
Blue-green switches between two complete pools.
```

Tradeoff:

```text
Blue-green needs more capacity.
Rollback can be fast if blue is still alive.
```

---

# 27. Database Migration During Rollout

This is where many production incidents happen.

Kubernetes can safely replace Pods.

But if versions share the same database, database compatibility matters.

Bad rollout:

```text
v1 expects column old_status
v2 migration removes old_status
v1 still running during rollout
v1 crashes
```

Correct model: expand and contract.

```text
1. Expand schema:
   Add new nullable column/table.
   Do not remove old yet.

2. Deploy app version that writes both old and new if needed.

3. Backfill data.

4. Deploy app version that reads new.

5. Later remove old column after no old app uses it.
```

ASCII:

```text
Phase 1:
DB supports v1 and v2

Phase 2:
v1 + v2 run together safely

Phase 3:
all traffic on v2

Phase 4:
cleanup old schema later
```

Spring Boot/Flyway rule:

```text
Migrations must be forward-compatible and rollback-aware.
```

Danger signs:

```text
DROP COLUMN
RENAME COLUMN
CHANGE TYPE
NOT NULL without default on huge table
Long blocking index creation
Deleting enum values
```

Production lesson:

```text
A rollback plan includes database state, not only Pod image.
```

---

# 28. Kafka Consumers During Rollout

Spring Boot services often consume Kafka.

Rolling update affects consumers too.

If you have 3 Pods in one consumer group:

```text
order-service-1
order-service-2
order-service-3
```

During rollout, Pods terminate and join.

Kafka rebalances partitions.

ASCII:

```text
Before:

Partition 0 -> old-1
Partition 1 -> old-2
Partition 2 -> old-3

During rollout:

old-1 terminates
new-1 joins
consumer group rebalance
```

Risks:

```text
Duplicate processing
Long rebalance pause
In-flight message lost if not committed correctly
Out-of-order side effects
```

Safe patterns:

```text
Idempotent message handling
Transactional outbox
Manual ack after processing
Graceful shutdown
Enough terminationGracePeriodSeconds
Avoid slow shutdown hooks
```

Spring Kafka graceful idea:

```text
Stop accepting new records.
Finish current records.
Commit offsets.
Then exit.
```

Mental model:

```text
Rolling update is not only HTTP traffic.
It also changes background workers.
```

Debug:

```bash
kubectl logs <pod>
kubectl describe pod <pod>
kubectl get events
Check Kafka consumer lag
Check rebalance logs
```

---

# 29. HPA Interaction With Rollout

Horizontal Pod Autoscaler can change replica count while rollout is happening.

Deployment wants:

```text
replicas managed by HPA
```

Rollout wants:

```text
replace old Pods with new Pods
```

HPA wants:

```text
scale based on CPU/memory/custom metrics
```

During rollout:

```text
New version may use more CPU.
HPA scales up.
More new Pods are created.
Cluster capacity may become tight.
```

ASCII:

```text
Rollout controller:
old -> new

HPA:
replicas 5 -> 8

Scheduler:
needs room for surge + scale-up
```

Common issues:

```text
Metrics delay
CPU spikes during startup
HPA scales up on bad version
Cluster autoscaler not fast enough
Pods pending
```

Production mindset:

```text
Observe HPA during rollout.
Do not assume rollout and autoscaling are independent.
```

Debug:

```bash
kubectl get hpa
kubectl describe hpa order-service
kubectl top pods
kubectl get pods -w
```

---

# 30. Observability During Rollout

A safe rollout needs signals.

Minimum signals:

```text
HTTP 5xx rate
Latency p95/p99
Request throughput
Pod restarts
Readiness failures
CPU/memory
GC pauses
DB errors
Kafka lag
Business metric impact
```

ASCII:

```text
Rollout starts
      |
      v
Metrics dashboard
      |
      +--> errors?
      +--> latency?
      +--> restarts?
      +--> saturation?
      +--> business failures?
```

Kubernetes-only status is not enough.

A Pod can be Ready but still bad:

```text
Returns 200 on readiness
But real API returns 500
```

Therefore production rollout should combine:

```text
Kubernetes health
Application metrics
Logs
Tracing
Business KPIs
```

Example Prometheus queries conceptually:

```text
rate(http_server_requests_seconds_count{status=~"5.."}[5m])
histogram_quantile(0.99, rate(http_server_requests_seconds_bucket[5m]))
rate(container_cpu_usage_seconds_total[5m])
```

Mental model:

```text
Readiness protects obvious unsafe Pods.
Observability detects bad behavior after traffic starts.
```

---

# 31. Production Debugging Scenario: Bad Readiness Path

You deploy Spring Boot v2.

Deployment YAML:

```yaml
readinessProbe:
  httpGet:
    path: /health
    port: 8080
```

But Spring Boot exposes:

```text
/actuator/health/readiness
```

Symptoms:

```text
Pods Running 0/1
Rollout stuck
Old Pods still serving
```

Events:

```text
Readiness probe failed: HTTP probe failed with statuscode: 404
```

Debug flow:

```bash
kubectl describe pod <new-pod>
kubectl logs <new-pod>
kubectl exec -it <new-pod> -- curl localhost:8080/health
kubectl exec -it <new-pod> -- curl localhost:8080/actuator/health/readiness
```

ASCII:

```text
Kubelet probe
    |
    | GET /health
    v
Spring Boot
    |
    v
404
    |
    v
Pod not ready
```

Fix:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
```

Lesson:

```text
A rollout stuck on readiness is often Kubernetes protecting you.
```

---

# 32. Production Debugging Scenario: Good App, Bad Resource Limit

You deploy v2.

It starts but restarts under traffic.

YAML:

```yaml
resources:
  limits:
    memory: 256Mi
```

Spring Boot needs more memory.

Symptoms:

```text
OOMKilled
CrashLoopBackOff
Restart count increasing
```

Debug:

```bash
kubectl describe pod <pod>
kubectl logs <pod> --previous
kubectl top pod <pod>
```

Events:

```text
Reason: OOMKilled
Exit Code: 137
```

ASCII:

```text
Java heap/native memory grows
        |
        v
Container exceeds memory limit
        |
        v
Kernel kills process
        |
        v
Kubernetes restarts container
        |
        v
Rollout unstable
```

Fix options:

```text
Increase memory limit
Tune JVM MaxRAMPercentage
Reduce startup memory
Check memory leak
Use realistic load test
```

Spring Boot container JVM example:

```yaml
env:
  - name: JAVA_TOOL_OPTIONS
    value: "-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=50"
```

Lesson:

```text
Resource limits are part of rollout safety.
A version can fail only under production memory constraints.
```

---

# 33. Production Debugging Scenario: Service Has No Endpoints

After rollout, users get:

```text
503 Service Unavailable
```

Pods are Running.

Service exists.

Debug:

```bash
kubectl get endpoints order-service
```

Output:

```text
<none>
```

Possible causes:

```text
Service selector does not match Pod labels
Pods not Ready
Wrong namespace
TargetPort mismatch
```

Check labels:

```bash
kubectl get pods --show-labels
kubectl describe svc order-service
```

ASCII:

```text
Service
 selector app=order-service
       |
       v
No matching Ready Pods
       |
       v
No endpoints
       |
       v
Traffic fails
```

If endpoints exist but traffic still fails:

```text
Check targetPort
Check containerPort
Check app listening address
Check NetworkPolicy
Check Ingress
```

Mental model:

```text
Service is not magic load balancing.
Service needs matching Ready endpoints.
```

---

# 34. Production Debugging Scenario: Rollout Looks Complete But Users Fail

This is the scary case.

Kubernetes says:

```text
deployment successfully rolled out
```

But users see failures.

Why can this happen?

Because readiness may be too shallow.

Example readiness:

```text
GET /actuator/health/readiness returns UP
```

But real API fails because:

```text
Payment provider credentials wrong
Feature flag wrong
Specific endpoint broken
Only certain tenant affected
Database query slow
Kafka publishing failing
```

ASCII:

```text
Readiness:
Can app process basic health request? YES

Real traffic:
Can app complete checkout? NO
```

Kubernetes cannot know your business transaction unless you encode meaningful health and observe real metrics.

Debug:

```text
Check 5xx by endpoint
Check logs with correlation id
Check traces
Check DB errors
Check external dependency errors
Compare old vs new version metrics
```

Rollback decision:

```text
If customer impact is high and cause unknown:
rollback first, debug after stabilization.
```

Production rule:

```text
Rollout complete means Kubernetes completed replacement.
It does not guarantee business correctness.
```

---

# 35. Safe Deployment Checklist Before Rollout

Before deploying, ask:

```text
[ ] Is image tag immutable?
[ ] Is image already pushed and pullable?
[ ] Are ConfigMaps/Secrets correct?
[ ] Are database migrations backward-compatible?
[ ] Can old and new versions run together?
[ ] Are readiness/liveness/startup probes correct?
[ ] Is graceful shutdown enabled?
[ ] Are resource requests/limits realistic?
[ ] Is there enough cluster capacity for maxSurge?
[ ] Are dashboards ready?
[ ] Is rollback image still available?
[ ] Is rollback safe with current DB state?
[ ] Are Kafka consumers idempotent?
[ ] Are alerts watched during rollout?
```

Mental model:

```text
A safe rollout is prepared before kubectl apply.
```

---

# 36. Rollout Debugging Checklist

When rollout is stuck:

```text
1. Check high-level status
   kubectl rollout status deployment/order-service

2. Check Deployment events
   kubectl describe deployment order-service

3. Check ReplicaSets
   kubectl get rs

4. Check Pods
   kubectl get pods -o wide

5. Check stuck Pod details
   kubectl describe pod <pod>

6. Check app logs
   kubectl logs <pod>
   kubectl logs <pod> --previous

7. Check readiness
   kubectl exec -it <pod> -- curl localhost:8080/actuator/health/readiness

8. Check Service endpoints
   kubectl get endpoints order-service

9. Check metrics
   errors, latency, restarts, CPU, memory

10. Decide:
   fix-forward or rollback
```

ASCII:

```text
Status -> Events -> ReplicaSet -> Pod -> Logs -> Probes -> Endpoints -> Metrics
```

Do not jump randomly.

Follow the chain.

---

# 37. Fix-Forward vs Rollback

When rollout fails, you have two choices.

Fix-forward:

```text
Deploy a new corrected version.
```

Rollback:

```text
Return to previous known-good version.
```

Decision model:

```text
Customer impact high?
    |
    +-- yes --> rollback if safe
    |
    +-- no --> fix-forward may be okay

Root cause obvious and quick?
    |
    +-- yes --> fix-forward
    |
    +-- no --> rollback/stabilize

DB migration unsafe for rollback?
    |
    +-- yes --> fix-forward may be required
```

ASCII:

```text
Bad rollout
    |
    v
Can old version still run safely?
    |
    +-- yes --> rollback
    |
    +-- no --> fix-forward / mitigation
```

Examples:

```text
Wrong image tag          -> fix-forward or rollback
Bad readiness path       -> fix-forward
Code bug causing 500s    -> rollback
Dropped DB column        -> rollback may fail
Bad config secret        -> fix config, restart rollout
```

Production mindset:

```text
The goal is not emotional attachment to new version.
The goal is restore safe service.
```

---

# 38. Java Code: Version Endpoint For Rollout Debugging

A simple version endpoint helps during rollout.

```java
package com.example.order.web;

import java.time.Instant;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    @Value("${app.version:unknown}")
    private String version;

    @Value("${HOSTNAME:local}")
    private String hostname;

    @GetMapping("/version")
    public Map<String, String> version() {
        return Map.of(
            "service", "order-service",
            "version", version,
            "pod", hostname,
            "time", Instant.now().toString()
        );
    }
}
```

Kubernetes env:

```yaml
env:
  - name: APP_VERSION
    value: "1.1.0"
```

Spring config:

```yaml
app:
  version: ${APP_VERSION:dev}
```

During rollout:

```bash
kubectl exec -it <client-pod> -- curl http://order-service/version
```

Possible responses:

```json
{"service":"order-service","version":"1.0.0","pod":"order-service-old-abc"}
{"service":"order-service","version":"1.1.0","pod":"order-service-new-def"}
```

Mental model:

```text
Version endpoint lets you see which version answered.
```

Useful for:

```text
Rollout verification
Canary observation
Debugging mixed old/new traffic
Checking service routing
```

---

# 39. Java Code: Readiness With Real Dependency Check

Spring Boot Actuator can manage readiness state.

But sometimes you need custom health indicators.

Example:

```java
package com.example.order.health;

import javax.sql.DataSource;
import java.sql.Connection;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("databaseReadiness")
public class DatabaseReadinessIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public DatabaseReadinessIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up().withDetail("db", "reachable").build();
            }
            return Health.down().withDetail("db", "not valid").build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
```

Be careful.

Do not put too many optional dependencies in readiness.

Bad readiness:

```text
App not ready if optional analytics provider is down.
```

Result:

```text
All Pods removed from Service even though core order API can work.
```

Better:

```text
Readiness checks critical dependencies needed to serve core traffic.
```

Mental model:

```text
Readiness should prevent bad traffic routing.
It should not create unnecessary outages.
```

---

# 40. Complete Deployment YAML Example

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
  labels:
    app: order-service
spec:
  replicas: 3
  revisionHistoryLimit: 5
  minReadySeconds: 10
  progressDeadlineSeconds: 300
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 0
      maxSurge: 1
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      terminationGracePeriodSeconds: 45
      containers:
        - name: order-service
          image: registry.example.com/order-service:1.1.0
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
          env:
            - name: APP_VERSION
              value: "1.1.0"
            - name: JAVA_TOOL_OPTIONS
              value: "-XX:MaxRAMPercentage=75"
          resources:
            requests:
              cpu: "250m"
              memory: "512Mi"
            limits:
              cpu: "1000m"
              memory: "1024Mi"
          startupProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            failureThreshold: 30
            periodSeconds: 5
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            periodSeconds: 10
            timeoutSeconds: 2
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            periodSeconds: 5
            timeoutSeconds: 2
```

Why this is safer:

```text
maxUnavailable: 0      -> do not reduce available capacity during update
maxSurge: 1            -> add new Pod before removing old
minReadySeconds: 10    -> wait before counting Pod stable
startupProbe           -> slow boot does not trigger liveness restart
readinessProbe         -> traffic only after ready
grace period           -> old Pods exit cleanly
revisionHistoryLimit   -> rollback memory
```

---

# 41. End-To-End Rollout Command Flow

Build and push:

```bash
docker build -t registry.example.com/order-service:1.1.0 .
docker push registry.example.com/order-service:1.1.0
```

Deploy:

```bash
kubectl set image deployment/order-service \
  order-service=registry.example.com/order-service:1.1.0
```

Watch:

```bash
kubectl rollout status deployment/order-service
kubectl get pods -w
```

Inspect ReplicaSets:

```bash
kubectl get rs
```

Verify endpoints:

```bash
kubectl get endpoints order-service
```

Check version:

```bash
kubectl exec -it debug-pod -- curl http://order-service/version
```

Rollback:

```bash
kubectl rollout undo deployment/order-service
kubectl rollout status deployment/order-service
```

History:

```bash
kubectl rollout history deployment/order-service
```

One command flow picture:

```text
build -> push -> set image -> watch rollout -> verify metrics -> keep or rollback
```

---

# 42. Interview Answers

## What is a Kubernetes rollout?

A rollout is the process where a Deployment transitions Pods from one Pod template version to another. Usually this means moving from one container image version to another by creating a new ReplicaSet and gradually scaling it up while scaling the old ReplicaSet down.

## What happens internally during a rolling update?

The Deployment controller detects that the Pod template changed. It creates a new ReplicaSet for the new template. Based on maxSurge and maxUnavailable, it creates new Pods and removes old Pods gradually. Readiness probes determine when new Pods are available for Service traffic.

## What is rollback?

Rollback changes the Deployment back to a previous revision. Kubernetes scales up the previous ReplicaSet template and scales down the bad one. It is effectively another rollout toward an older known-good Pod template.

## Is rollback always safe?

No. Rollback is unsafe if database schema, configuration, message formats, or external dependencies changed incompatibly. Kubernetes can roll back Pods, but it cannot automatically roll back destructive database migrations or business state.

## Difference between maxSurge and maxUnavailable?

maxSurge controls how many extra Pods above desired replicas may exist during rollout. maxUnavailable controls how many desired Pods may be unavailable during rollout. Together they define capacity safety during replacement.

## Why can a rollout get stuck?

Common reasons include ImagePullBackOff, CrashLoopBackOff, readiness failures, Pending Pods due to scheduling constraints, insufficient resources, bad probes, quota limits, and progressDeadlineSeconds being exceeded.

## What is ProgressDeadlineExceeded?

It means the Deployment did not make expected progress within progressDeadlineSeconds. Usually updated Pods failed to become available. Kubernetes marks the rollout as failed, but it does not automatically rollback.

## How do Services behave during rolling update?

A Service routes to all matching Ready Pods. During rollout, old and new Pods may both receive traffic if they match the Service selector and are Ready. Therefore old and new versions must be compatible.

## Why is readiness important for rolling update?

Readiness controls whether a Pod is added to Service endpoints. During rollout, Kubernetes waits for new Pods to become Ready before safely removing more old Pods. Without correct readiness, traffic may go to unprepared Pods or rollout may get stuck.

## How do you debug a stuck rollout?

Start with rollout status and Deployment describe. Then inspect ReplicaSets, Pods, Pod events, logs, previous logs, readiness endpoints, scheduling events, image pull errors, and Service endpoints. Debug layer by layer instead of guessing.

---

# 43. Cheat Sheet

```text
Rollout                  = transition from old Pod template to new Pod template
Rollback                 = transition back to previous revision
Deployment               = rollout coordinator
ReplicaSet               = Pod count manager for one template
Revision                 = stored Deployment template version
RollingUpdate            = gradual old->new replacement
Recreate                 = kill old first, then create new
maxSurge                 = extra Pods allowed above desired replicas
maxUnavailable           = unavailable Pods allowed during rollout
minReadySeconds          = time Pod must stay ready before counted available
progressDeadlineSeconds  = time before rollout considered failed
revisionHistoryLimit     = number of old ReplicaSets kept
readinessProbe           = controls Service traffic
livenessProbe            = controls restart
startupProbe             = protects slow startup
ImagePullBackOff         = kubelet cannot pull image
CrashLoopBackOff         = app starts then crashes repeatedly
ProgressDeadlineExceeded = rollout did not progress in time
```

Command cheat sheet:

```bash
kubectl rollout status deployment/order-service
kubectl rollout history deployment/order-service
kubectl rollout undo deployment/order-service
kubectl rollout undo deployment/order-service --to-revision=2
kubectl rollout pause deployment/order-service
kubectl rollout resume deployment/order-service
kubectl rollout restart deployment/order-service

kubectl get deploy,rs,pods
kubectl describe deployment order-service
kubectl describe pod <pod>
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl get events --sort-by=.lastTimestamp
kubectl get endpoints order-service
kubectl get pods --show-labels
```

---

# 44. One Picture To Remember

```text
                         Developer
                            |
                            | change image/config/probe
                            v
                    +----------------+
                    | Deployment     |
                    | desired v2     |
                    +-------+--------+
                            |
                            v
              +----------------------------+
              | Deployment Controller      |
              | safe transition manager    |
              +-------------+--------------+
                            |
              +-------------+--------------+
              |                            |
              v                            v
      +---------------+            +---------------+
      | Old ReplicaSet|            | New ReplicaSet|
      | image v1      |            | image v2      |
      +-------+-------+            +-------+-------+
              |                            |
              v                            v
          Old Pods                     New Pods
          Ready                        Starting
          Serving                      Probing
              |                            |
              +-------------+--------------+
                            |
                            v
                    +----------------+
                    | Service        |
                    | Ready endpoints|
                    +----------------+
                            |
                            v
                         Users

Rule:

Kubernetes does not blindly replace old with new.
It moves traffic only through Ready Pods,
while Deployment controls how fast old reality becomes new reality.
```

---

# 45. Final Production Memory Hook

Do not memorize rollout commands as isolated commands.

Remember the machine:

```text
Deployment changes desired Pod template.
New ReplicaSet appears.
New Pods start.
Readiness proves safety.
Service sends traffic only to Ready Pods.
Old Pods disappear gradually.
Rollback points desired state back to an older template.
```

Final sentence:

```text
A rollout is Kubernetes asking the new version to earn production traffic before the old version is removed.
```
