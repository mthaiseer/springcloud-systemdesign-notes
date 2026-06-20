# 007_Rolling_Update_Rollback.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Rolling Update Exists

A production application cannot be treated like a local Java process.

On your laptop, deployment feels simple:

```bash
java -jar order-service-1.0.0.jar
# stop it
java -jar order-service-1.1.0.jar
```

But production has users, traffic, database connections, queues, retries, caches, and downstream services. If you stop all old instances first and then start all new instances, you create downtime.

Bad deployment model:

```text
Old Version Running
        |
        | kill everything
        v
No Pods Running
        |
        | start new version
        v
New Version Running
```

User experience:

```text
HTTP 503
Timeout
Retry storm
Order failed
Payment duplicate risk
```

Rolling update exists because production needs **safe replacement**, not sudden replacement.

Mental model:

```text
Rolling Update = Replace old workers gradually while keeping enough workers serving traffic
```

Kubernetes Deployment does not simply say:

```text
Delete old Pods. Create new Pods.
```

It says:

```text
Create a new ReplicaSet for the new version.
Gradually scale new Pods up.
Gradually scale old Pods down.
Respect availability rules.
Use readiness to protect traffic.
```

One picture:

```text
Before:
[old] [old] [old]

During:
[new] [old] [old]
[new] [new] [old]

After:
[new] [new] [new]
```

Do not memorize commands first.

Understand the production problem first:

```text
How do I change a running service without breaking users?
```

Kubernetes rolling update is one answer.

---

# 2. The Wrong Way To Think About Deployment Updates

Wrong mental model:

```text
kubectl set image immediately changes all containers
```

This is not how Deployment works.

When you update a Deployment image, Kubernetes does not mutate containers inside existing Pods. Pods are mostly immutable runtime units. Instead, Kubernetes creates a new ReplicaSet with a new Pod template.

Bad thinking:

```text
Pod A image changes from v1 to v2
Pod B image changes from v1 to v2
Pod C image changes from v1 to v2
```

Correct thinking:

```text
Old ReplicaSet owns old Pods.
New ReplicaSet owns new Pods.
Deployment moves traffic capacity from old RS to new RS gradually.
```

ASCII:

```text
Deployment
   |
   +--> ReplicaSet old-template: order-service:v1
   |        +--> Pod old-a
   |        +--> Pod old-b
   |        +--> Pod old-c
   |
   +--> ReplicaSet new-template: order-service:v2
            +--> Pod new-x
```

A Deployment rollout is not an in-place patch of running containers.

It is a controlled replacement of Pods.

This matters because debugging becomes easier:

```text
If rollout is stuck, inspect ReplicaSets.
If new Pods fail, inspect new ReplicaSet Pods.
If old Pods remain, check availability constraints.
```

Core rule:

```text
Deployment update = new Pod template = new ReplicaSet = gradual replacement
```

---

# 3. Restaurant Analogy: Replacing Chefs Without Closing Kitchen

Imagine a restaurant has three chefs cooking orders.

```text
Kitchen Capacity:
Chef A
Chef B
Chef C
```

The owner wants to replace old chefs with newly trained chefs.

Bad plan:

```text
Fire all old chefs immediately.
Then hire new chefs.
```

Result:

```text
No one cooks.
Customers wait.
Orders fail.
```

Better plan:

```text
Bring one new chef.
Wait until he can cook correctly.
Then release one old chef.
Repeat.
```

Diagram:

```text
Step 0:
[Old Chef] [Old Chef] [Old Chef]

Step 1:
[New Chef Training] [Old Chef] [Old Chef] [Old Chef]

Step 2:
[New Chef Ready] [Old Chef] [Old Chef]

Step 3:
[New Chef] [New Chef Training] [Old Chef] [Old Chef]

Step 4:
[New Chef] [New Chef Ready] [Old Chef]

Step 5:
[New Chef] [New Chef] [New Chef]
```

Kubernetes rolling update is this kitchen process.

Mapping:

```text
Old Chef        = old Pod
New Chef        = new Pod
Can cook safely = readiness probe passes
Kitchen capacity = desired replicas
Manager         = Deployment controller
```

Important point:

```text
New Pod existing is not enough.
New Pod must be Ready.
```

A Spring Boot app may start the JVM but still be unable to serve traffic because DB connection, Redis cache, Kafka producer, or migrations are not ready.

That is why readiness is central to rolling updates.

---

# 4. Core Deployment Rollout Picture

A Deployment manages rollout through ReplicaSets.

```text
                 Deployment
            order-service
           replicas = 3
                |
                |
      +---------+----------+
      |                    |
      v                    v
ReplicaSet v1         ReplicaSet v2
image: 1.0.0          image: 1.1.0
replicas: 3 -> 0      replicas: 0 -> 3
      |                    |
      v                    v
 old pods              new pods
```

During rollout, both versions may temporarily exist.

```text
Time T0:
RS-v1 desired=3 actual=3
RS-v2 desired=0 actual=0

Time T1:
RS-v1 desired=3 actual=3
RS-v2 desired=1 actual=1

Time T2:
RS-v1 desired=2 actual=2
RS-v2 desired=1 actual=1

Time T3:
RS-v1 desired=2 actual=2
RS-v2 desired=2 actual=2

Time T4:
RS-v1 desired=1 actual=1
RS-v2 desired=2 actual=2

Time T5:
RS-v1 desired=0 actual=0
RS-v2 desired=3 actual=3
```

The exact sequence depends on `maxSurge`, `maxUnavailable`, readiness, scheduling, image pull time, and resource availability.

Do not think:

```text
Kubernetes updates pods randomly.
```

Think:

```text
Kubernetes follows a rollout strategy under availability constraints.
```

---

# 5. Deployment Strategy Field

A Deployment has a rollout strategy.

Common type:

```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1
    maxUnavailable: 0
```

Meaning in simple words:

```text
maxSurge: 1
Kubernetes may temporarily run 1 extra Pod above desired replicas.

maxUnavailable: 0
Kubernetes must keep all desired replicas available during update.
```

For a service with 3 replicas:

```text
Desired replicas = 3
maxSurge = 1
maxUnavailable = 0

During rollout:
maximum total Pods allowed = 4
minimum available Pods required = 3
```

Diagram:

```text
Desired capacity: 3 ready Pods

Allowed during update:
[Ready] [Ready] [Ready] [Starting]

Not allowed:
[Ready] [Ready] [Starting]
Only 2 available if maxUnavailable=0
```

This is the safety contract.

Kubernetes will not delete old Pods too aggressively if doing so violates availability.

---

# 6. maxSurge Mental Model

`maxSurge` controls how many extra Pods Kubernetes can create during a rolling update.

Imagine desired replicas are 4.

```yaml
replicas: 4
strategy:
  rollingUpdate:
    maxSurge: 1
```

Maximum Pods during rollout:

```text
4 desired + 1 surge = 5 total Pods
```

ASCII:

```text
Normal:
[old] [old] [old] [old]

Surge allowed:
[new-starting] [old] [old] [old] [old]
```

Why useful?

Because Kubernetes can start a new Pod before deleting an old Pod.

This is safer when you do not want capacity to drop.

But surge needs cluster resources.

If your nodes do not have CPU or memory capacity, new Pods may remain Pending.

```text
Deployment wants surge Pod
        |
        v
Scheduler checks nodes
        |
        v
No free CPU / memory
        |
        v
New Pod Pending
        |
        v
Rollout stuck
```

Production lesson:

```text
maxSurge improves availability but requires spare capacity.
```

If the cluster is already full, rolling update may not progress until Cluster Autoscaler adds nodes or you reduce constraints.

---

# 7. maxUnavailable Mental Model

`maxUnavailable` controls how many desired Pods may be unavailable during rollout.

Example:

```yaml
replicas: 4
strategy:
  rollingUpdate:
    maxUnavailable: 1
```

Minimum available Pods:

```text
4 desired - 1 unavailable = 3 available Pods
```

ASCII:

```text
Allowed:
[ready] [ready] [ready] [starting]

Not allowed if only 2 are ready:
[ready] [ready] [starting] [terminating]
```

This setting is a business decision.

For low traffic internal tools, allowing one unavailable Pod may be fine.

For payment, order, login, or checkout services, you may prefer stricter availability:

```yaml
maxUnavailable: 0
```

But strict settings can slow or block rollouts.

Trade-off:

```text
maxUnavailable higher -> faster rollout, less safety
maxUnavailable lower  -> safer rollout, may need more resources
```

Do not memorize values.

Ask:

```text
Can my service tolerate temporary capacity reduction?
```

---

# 8. Rolling Update Dry Run With 3 Replicas

Deployment:

```yaml
replicas: 3
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1
    maxUnavailable: 0
```

Initial state:

```text
RS-v1 desired=3
Pods: old-a Ready, old-b Ready, old-c Ready
```

You update image:

```bash
kubectl set image deployment/order-service order-service=order-service:1.1.0
```

Dry run:

```text
1. API Server updates Deployment pod template.

2. Deployment controller detects template change.

3. New ReplicaSet is created for image 1.1.0.

4. Because maxSurge=1, controller creates one new Pod.

5. New Pod pulls image and starts Spring Boot.

6. Readiness probe checks /actuator/health/readiness.

7. If new Pod becomes Ready, available capacity is now 4.

8. Controller safely scales old ReplicaSet down by 1.

9. Repeat until new ReplicaSet has 3 Ready Pods.

10. Old ReplicaSet becomes scaled to 0.
```

Picture:

```text
Step 0:
old-a old-b old-c

Step 1:
old-a old-b old-c new-x(starting)

Step 2:
old-a old-b old-c new-x(ready)

Step 3:
old-b old-c new-x

Step 4:
old-b old-c new-x new-y(starting)

Step 5:
old-b old-c new-x new-y(ready)

Step 6:
old-c new-x new-y

Step 7:
old-c new-x new-y new-z(starting)

Step 8:
new-x new-y new-z
```

This is the rolling shape.

---

# 9. Spring Boot Example Application

Simple Spring Boot service:

```java
package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

@RestController
class VersionController {
    @GetMapping("/version")
    public String version() {
        return "order-service version 1.0.0";
    }
}
```

When releasing version 1.1.0, the code may change:

```java
@RestController
class VersionController {
    @GetMapping("/version")
    public String version() {
        return "order-service version 1.1.0";
    }
}
```

Docker image tags:

```text
order-service:1.0.0
order-service:1.1.0
```

Kubernetes does not know whether your code is good.

It only knows:

```text
Did container start?
Did readiness pass?
Did liveness pass?
Did rollout progress?
```

Therefore Spring Boot health endpoints become rollout gates.

---

# 10. Spring Boot Actuator Readiness Setup

For production-safe rolling updates, expose readiness and liveness.

`pom.xml` dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

`application.yml`:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
  endpoints:
    web:
      exposure:
        include: health,info
```

Kubernetes probes:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
  failureThreshold: 3

livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
  failureThreshold: 3
```

Mental model:

```text
Liveness = Should Kubernetes restart this container?
Readiness = Should Service send traffic to this Pod?
```

During rolling update, readiness is especially important.

```text
New Pod starts
    |
    v
Readiness failing
    |
    v
Service does not send traffic
    |
    v
Old Pods continue serving
```

Without readiness, Kubernetes may send traffic to a Pod whose JVM started but whose app is not actually usable.

---

# 11. Full Deployment YAML

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
  progressDeadlineSeconds: 120
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
        - name: order-service
          image: registry.example.com/order-service:1.0.0
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 5
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
          resources:
            requests:
              cpu: "250m"
              memory: "512Mi"
            limits:
              cpu: "1000m"
              memory: "1Gi"
```

Service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: order-service
spec:
  selector:
    app: order-service
  ports:
    - port: 80
      targetPort: 8080
```

Connection:

```text
Deployment creates Pods with label app=order-service
Service selects Pods with label app=order-service
Readiness decides which selected Pods receive traffic
```

---

# 12. Rollout Commands

Update image:

```bash
kubectl set image deployment/order-service \
  order-service=registry.example.com/order-service:1.1.0
```

Check rollout:

```bash
kubectl rollout status deployment/order-service
```

View history:

```bash
kubectl rollout history deployment/order-service
```

View ReplicaSets:

```bash
kubectl get rs
```

View Pods:

```bash
kubectl get pods -l app=order-service -o wide
```

Pause rollout:

```bash
kubectl rollout pause deployment/order-service
```

Resume rollout:

```bash
kubectl rollout resume deployment/order-service
```

Undo rollout:

```bash
kubectl rollout undo deployment/order-service
```

Undo to a specific revision:

```bash
kubectl rollout undo deployment/order-service --to-revision=2
```

Mental model:

```text
rollout status  = Is Deployment progressing successfully?
rollout history = Which Deployment revisions exist?
rollout undo    = Move pod template back to previous revision
```

---

# 13. What Is A Revision?

Every meaningful change to a Deployment Pod template creates a new revision.

Examples that create new revision:

```text
image tag changes
container command changes
environment variables change
probe changes
resource requests change
labels inside pod template change
```

Example:

```text
Revision 1 -> image order-service:1.0.0
Revision 2 -> image order-service:1.1.0
Revision 3 -> image order-service:1.2.0
```

Diagram:

```text
Deployment history

rev 1: template hash aaa111 -> RS old
rev 2: template hash bbb222 -> RS newer
rev 3: template hash ccc333 -> RS current
```

Kubernetes stores old ReplicaSets up to `revisionHistoryLimit`.

```yaml
revisionHistoryLimit: 5
```

This means Kubernetes can keep rollback history for recent versions.

But rollback is not time travel for your database.

It only reverts the Deployment pod template.

Important:

```text
Rollback can restore old container image.
Rollback cannot automatically undo database migrations, Kafka messages, Redis writes, or external side effects.
```

This is where production engineering matters.

---

# 14. Rollback Mental Model

Rollback means:

```text
Make the Deployment use an older Pod template again.
```

It does not mean:

```text
Undo everything that happened in production.
```

ASCII:

```text
Current bad rollout:
Deployment -> ReplicaSet v2 -> Pods image 1.1.0

Rollback:
Deployment -> ReplicaSet v1 -> Pods image 1.0.0
```

The Deployment controller again performs a rollout, but in reverse direction.

```text
Before rollback:
[new-bad] [new-bad] [new-bad]

During rollback:
[old-good] [new-bad] [new-bad]
[old-good] [old-good] [new-bad]

After rollback:
[old-good] [old-good] [old-good]
```

Rollback is still controlled by `maxSurge` and `maxUnavailable`.

It is not an instant kill switch unless you explicitly scale or delete things manually.

Production lesson:

```text
Rollback is safest when versions are backward compatible.
```

---

# 15. Dangerous Rollback: Database Migration Problem

Suppose version 1.1.0 adds a new database column and removes old behavior.

Migration:

```sql
ALTER TABLE orders ADD COLUMN payment_status VARCHAR(30);
```

That is usually safe.

But imagine version 1.1.0 also renames a column:

```sql
ALTER TABLE orders RENAME COLUMN status TO order_status;
```

Old version 1.0.0 expects:

```sql
SELECT status FROM orders;
```

After rollback, old code fails because `status` no longer exists.

Diagram:

```text
App v1 expects DB shape A
App v2 changes DB to shape B
Rollback to App v1
        |
        v
App v1 cannot understand DB shape B
```

Safe deployment mindset:

```text
Database changes should be backward compatible across rollout windows.
```

Better pattern:

```text
Release 1: Add new column, old code still works.
Release 2: Write to both old and new columns.
Release 3: Read from new column.
Release 4: Remove old column later.
```

This is called expand-and-contract migration.

Kubernetes rollback protects Pods.

It does not protect bad schema strategy.

---

# 16. Production-Safe Release Pattern

For Spring Boot services with databases, use staged changes.

Bad one-shot release:

```text
Code change + destructive DB migration + config change + message format change
```

If something fails, rollback becomes risky.

Better pattern:

```text
1. Expand database schema safely.
2. Deploy code that can work with old and new schema.
3. Shift traffic gradually.
4. Observe metrics and logs.
5. Remove old schema only after confidence.
```

ASCII:

```text
Phase 1: DB supports old + new
         App old still works

Phase 2: App new writes compatible data
         App old still works

Phase 3: All Pods new
         Metrics healthy

Phase 4: Cleanup old fields later
```

Rollback-safe rule:

```text
During rollout, old and new versions may run together.
Therefore they must be compatible.
```

This is often forgotten.

Rolling update means mixed-version reality.

Your app design must tolerate that.

---

# 17. Mixed Version Reality

During rolling update, old and new Pods can serve traffic at the same time.

```text
Client requests
      |
      v
Service
   |       |       |
   v       v       v
Pod v1   Pod v1   Pod v2
```

This means:

```text
Request 1 may hit v1.
Request 2 may hit v2.
Request 3 may hit v1 again.
```

Dangerous assumptions:

```text
All Pods switch together.
All caches have same format.
All message producers use same schema.
All downstream services receive same payload shape.
```

Safer assumptions:

```text
For some time, both versions exist.
Both must understand shared database state.
Both must tolerate message compatibility.
Both must expose compatible APIs.
```

For Java services, think carefully about:

```text
DTO fields
JSON serialization
Kafka event schema
Redis key format
Database schema
Feature flags
```

Rolling update is an infrastructure mechanism.

Backward compatibility is an application responsibility.

---

# 18. Service Traffic During Rollout

A Service routes traffic to Ready Pods that match its selector.

```text
Service selector: app=order-service

Endpoints:
10.1.1.10:8080 old Ready
10.1.1.11:8080 old Ready
10.1.1.20:8080 new Ready
```

If a new Pod is not Ready, it should not receive traffic.

```text
10.1.1.21:8080 new NotReady  -> not in ready endpoints
```

Picture:

```text
Client
  |
  v
Service
  |
  +--> old-a Ready
  +--> old-b Ready
  +--> new-x Ready
  X--> new-y NotReady
```

Debug command:

```bash
kubectl get endpoints order-service
```

or newer endpoint slices:

```bash
kubectl get endpointslices -l kubernetes.io/service-name=order-service
```

Production rule:

```text
If readiness is wrong, traffic safety is wrong.
```

If readiness always returns UP, bad Pods receive traffic.

If readiness is too strict, rollout may never progress.

---

# 19. Readiness Failure Dry Run

Suppose image 1.1.0 has wrong DB password.

New Pod starts:

```text
Container Running
Spring Boot starts
DB connection fails
Readiness returns DOWN
```

Rollout state:

```text
old-a Ready
old-b Ready
old-c Ready
new-x NotReady
```

Because `maxUnavailable=0`, Kubernetes cannot delete old Pods.

So rollout pauses naturally.

```text
Deployment wants progress
        |
        v
New Pod not Ready
        |
        v
Available replicas not enough to remove old Pods
        |
        v
Old Pods continue serving
```

This is good.

Users are protected if old Pods are healthy.

Commands:

```bash
kubectl rollout status deployment/order-service
kubectl describe deployment order-service
kubectl get pods -l app=order-service
kubectl logs <new-pod>
```

Expected symptom:

```text
Waiting for deployment "order-service" rollout to finish...
```

Production mindset:

```text
A stuck rollout is not always bad.
It may be Kubernetes protecting availability.
```

---

# 20. progressDeadlineSeconds

`progressDeadlineSeconds` tells Kubernetes how long a Deployment can fail to make progress before it is marked failed.

```yaml
progressDeadlineSeconds: 120
```

If new Pods cannot become Ready within the deadline, Deployment condition becomes:

```text
Progressing=False
Reason=ProgressDeadlineExceeded
```

Mental model:

```text
Kubernetes says: I tried to roll out, but the new version is not becoming healthy fast enough.
```

Diagram:

```text
New ReplicaSet created
        |
        v
New Pods starting
        |
        v
Readiness not passing
        |
        v
Deadline exceeded
        |
        v
Deployment marked failed
```

Important:

```text
Kubernetes marks rollout failed.
It does not always automatically rollback.
```

You usually decide:

```bash
kubectl rollout undo deployment/order-service
```

or fix the issue and apply a new version.

---

# 21. Production Story: Wrong Image Tag

You deploy:

```yaml
image: registry.example.com/order-service:1.1.O
```

But the real tag is:

```text
1.1.0
```

Notice the difference:

```text
letter O vs zero 0
```

New Pods:

```text
ImagePullBackOff
```

Rollout:

```text
Old Pods remain running.
New Pods never become Ready.
Deployment does not complete.
```

Debug:

```bash
kubectl describe pod <new-pod>
```

Events:

```text
Failed to pull image
manifest not found
```

Mental model:

```text
Deployment object is valid.
ReplicaSet exists.
Pod object exists.
Kubelet cannot pull container image.
```

Fix:

```bash
kubectl set image deployment/order-service \
  order-service=registry.example.com/order-service:1.1.0
```

Then watch:

```bash
kubectl rollout status deployment/order-service
```

---

# 22. Production Story: CrashLoopBackOff During Rollout

New image starts but crashes.

Symptoms:

```text
order-service-new-abc   0/1   CrashLoopBackOff
```

Possible Spring Boot causes:

```text
Missing environment variable
Wrong profile
Flyway migration failure
Kafka broker URL wrong
Redis password missing
OutOfMemoryError
Invalid application.yml
Port mismatch
```

Debug:

```bash
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl describe pod <pod>
```

Rollout effect:

```text
New Pod never Ready
Old Pods remain
Rollout stuck or deadline exceeded
```

ASCII:

```text
Deployment update
      |
      v
New ReplicaSet
      |
      v
New Pod starts
      |
      v
Spring Boot crashes
      |
      v
Readiness never passes
      |
      v
Old version continues serving
```

Rollback:

```bash
kubectl rollout undo deployment/order-service
```

But first inspect logs. Do not rollback blindly if the problem is a cluster-wide dependency, because old Pods may also fail after restart.

---

# 23. Production Story: Bad Readiness Probe

New version is healthy, but readiness path is wrong.

YAML:

```yaml
readinessProbe:
  httpGet:
    path: /health/readiness
    port: 8080
```

Spring Boot exposes:

```text
/actuator/health/readiness
```

Result:

```text
HTTP 404 from readiness probe
Pod Running but NotReady
Rollout stuck
```

Debug:

```bash
kubectl describe pod <pod>
```

Events may show:

```text
Readiness probe failed: HTTP probe failed with statuscode: 404
```

Mental model:

```text
Application may be fine.
Kubernetes health contract is wrong.
```

Fix YAML and apply again:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
```

Production lesson:

```text
Probes are not decoration.
They are deployment control signals.
```

---

# 24. Production Story: Too Strict Readiness

A team adds readiness that checks every dependency:

```text
Postgres
Redis
Kafka
Email provider
Payment gateway
Analytics service
```

Now rollout gets stuck because analytics is temporarily slow.

Question:

```text
Should analytics failure stop order-service from receiving traffic?
```

Maybe not.

Readiness should answer:

```text
Can this Pod safely serve its core traffic?
```

Not:

```text
Is every optional dependency perfect?
```

Better design:

```text
Critical dependency: database for order writes -> readiness relevant
Optional dependency: analytics -> degrade gracefully
```

ASCII:

```text
Readiness Gate
   |
   +--> DB reachable? yes/no
   +--> required config loaded? yes/no
   +--> app initialized? yes/no

Optional dependency failures
   |
   v
metrics/logs/circuit breaker, not always readiness DOWN
```

Production lesson:

```text
Too weak readiness causes bad traffic.
Too strict readiness causes false outages.
```

---

# 25. Rollout Pause And Resume

Sometimes you want to make multiple changes but avoid triggering multiple rollouts.

Pause:

```bash
kubectl rollout pause deployment/order-service
```

Apply changes:

```bash
kubectl set image deployment/order-service order-service=order-service:1.2.0
kubectl set resources deployment/order-service \
  --requests=cpu=500m,memory=768Mi
```

Resume:

```bash
kubectl rollout resume deployment/order-service
```

Mental model:

```text
Pause = collect template changes without rolling them out immediately
Resume = start rollout using accumulated changes
```

Diagram:

```text
Deployment paused
      |
      +--> image change stored
      +--> resource change stored
      +--> env change stored
      |
      v
resume
      |
      v
one rollout starts
```

Use cases:

```text
Controlled release window
Batching safe config changes
Preparing rollout before traffic shift
```

Do not leave production Deployments paused accidentally.

Check:

```bash
kubectl rollout status deployment/order-service
kubectl describe deployment order-service
```

---

# 26. Recreate Strategy

Deployment can also use `Recreate` strategy.

```yaml
strategy:
  type: Recreate
```

Meaning:

```text
Delete old Pods first.
Then create new Pods.
```

ASCII:

```text
Before:
[old] [old] [old]

During:
[empty] [empty] [empty]

After:
[new] [new] [new]
```

This causes downtime for normal web services.

So why use it?

Possible cases:

```text
App cannot run two versions at same time
Single-writer legacy service
Development environments
Stateful apps with special constraints
```

For most Spring Boot stateless microservices, prefer RollingUpdate.

Mental model:

```text
RollingUpdate = safe gradual replacement
Recreate      = stop old world, then start new world
```

Interview answer:

```text
Recreate is simpler but causes downtime. RollingUpdate is safer for highly available services because it keeps old Pods serving while new Pods become Ready.
```

---

# 27. Rolling Update And Resource Capacity

A rollout may fail even when the new image is correct.

Example:

```yaml
replicas: 3
maxSurge: 1
resources:
  requests:
    cpu: "2"
    memory: "4Gi"
```

Cluster has no room for the extra surge Pod.

Result:

```text
New Pod Pending
Rollout stuck
```

Debug:

```bash
kubectl describe pod <pending-pod>
```

Events:

```text
0/3 nodes are available: insufficient cpu
0/3 nodes are available: insufficient memory
```

Mental model:

```text
maxSurge asks for temporary extra capacity.
Scheduler must find that capacity.
```

Fix options:

```text
Add nodes
Enable cluster autoscaler
Reduce resource requests if too high
Use maxUnavailable > 0 if capacity reduction is acceptable
Schedule rollout during lower traffic
```

Production rule:

```text
Rolling update design must match cluster capacity planning.
```

---

# 28. Rolling Update And PodDisruptionBudget

PodDisruptionBudget protects availability during voluntary disruptions.

Example:

```yaml
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata:
  name: order-service-pdb
spec:
  minAvailable: 2
  selector:
    matchLabels:
      app: order-service
```

Meaning:

```text
At least 2 matching Pods should remain available during voluntary disruptions.
```

PDB is more relevant to node drains and voluntary evictions, but the same mental model matters:

```text
Availability constraints can block disruptive actions.
```

Diagram:

```text
Drain Node
   |
   v
Would available Pods drop below minAvailable?
   |
   +--> yes: block eviction
   +--> no: allow eviction
```

For rolling updates, Deployment strategy is the main control.

For maintenance operations, PDB helps protect the app.

Production mindset:

```text
Availability is not one setting.
It is strategy + readiness + resources + PDB + autoscaling + application behavior.
```

---

# 29. HPA Interaction

Horizontal Pod Autoscaler can change replica count while rollouts happen.

Example:

```text
Deployment desired replicas: controlled by HPA
Traffic spike during rollout
HPA increases replicas from 3 to 6
```

Now rollout works with a moving target.

```text
Old ReplicaSet scaling down
New ReplicaSet scaling up
HPA changing desired total replicas
```

ASCII:

```text
HPA observes CPU/RPS
      |
      v
Deployment replicas: 3 -> 6
      |
      v
Deployment controller balances old/new ReplicaSets
```

Possible issue:

```text
New version has CPU bug
HPA sees high CPU
HPA adds more bad Pods
Rollout continues unless readiness or metrics detect problem
```

Production advice:

```text
Watch rollout metrics, not only Kubernetes status.
```

Important metrics:

```text
HTTP 5xx
p95 / p99 latency
CPU / memory
restart count
readiness failures
business error rate
Kafka lag
DB connection pool saturation
```

Kubernetes can tell you Pods are Ready.

Your observability tells you whether users are happy.

---

# 30. Canary vs Rolling Update

Rolling update gradually replaces Pods, but usually every new Pod is the same version and traffic distribution follows normal Service load balancing.

Canary release means intentionally sending a small percentage of traffic to new version and observing before wider rollout.

Rolling update:

```text
Infrastructure-level gradual replacement
```

Canary:

```text
Traffic-level controlled experiment
```

ASCII:

```text
Rolling Update:
Service -> old old new -> old new new -> new new new

Canary:
Service / Ingress / Mesh
   |
   +--> 95% traffic -> stable v1
   +-->  5% traffic -> canary v2
```

Kubernetes Deployment alone does rolling update.

Canary often needs:

```text
Ingress controller
Service mesh
Progressive delivery tool
Separate Deployments
Traffic weights
```

Do not confuse them.

Rolling update protects availability.

Canary protects business risk.

---

# 31. Blue-Green vs Rolling Update

Blue-green deployment runs two complete environments.

```text
Blue  = current production
Green = new production candidate
```

Traffic switches when Green is ready.

ASCII:

```text
Before switch:
Users -> Blue v1
         Green v2 warming

After switch:
Users -> Green v2
         Blue v1 standby
```

Rolling update changes Pods inside the same Deployment gradually.

```text
Users -> Service -> mix of old/new Pods
```

Trade-off:

```text
Rolling Update:
+ resource efficient
+ native Deployment behavior
- mixed versions during rollout

Blue-Green:
+ cleaner cutover
+ easier instant switch back
- needs double capacity
- traffic switch must be managed carefully
```

For many Spring Boot services, rolling update is enough.

For high-risk releases, payment flows, or major migrations, blue-green or canary may be safer.

---

# 32. Debugging A Stuck Rollout: Layer By Layer

Use a chain, not random guessing.

```text
1. Is Deployment updated?
2. Did new ReplicaSet appear?
3. Did new Pods get created?
4. Are new Pods scheduled?
5. Did image pull?
6. Did container start?
7. Are probes passing?
8. Are endpoints updated?
9. Are metrics healthy?
10. Is rollout progressing?
```

Commands:

```bash
kubectl get deployment order-service
kubectl describe deployment order-service

kubectl get rs -l app=order-service
kubectl describe rs <new-rs>

kubectl get pods -l app=order-service -o wide
kubectl describe pod <pod>

kubectl logs <pod>
kubectl logs <pod> --previous

kubectl get endpoints order-service
kubectl rollout status deployment/order-service
kubectl rollout history deployment/order-service
```

Mental model:

```text
Deployment -> ReplicaSet -> Pod -> Container -> Probe -> Endpoint -> Traffic
```

If you follow this chain, most rollout problems become visible.

---

# 33. Common Rollout Failure Table

```text
Symptom                         Likely Cause
---------------------------------------------------------------
ImagePullBackOff                Wrong tag, registry auth, image missing
CrashLoopBackOff                App crash, config issue, migration failure
Pod Pending                     Insufficient CPU/memory, scheduling constraints
Running but NotReady            Readiness failing, wrong path, dependency down
Rollout deadline exceeded       New Pods not becoming available
Old Pods not scaling down       New Pods not Ready or availability constraint
Traffic 503                     Service endpoints empty or app not Ready
Some users see old behavior     Mixed versions during rollout
Rollback fails                  DB/schema/message incompatibility
```

Do not memorize the table.

Map symptoms to the rollout chain:

```text
Cannot create Pod?       -> ReplicaSet / scheduler
Cannot start container?  -> image / config / runtime
Cannot become Ready?     -> probe / dependency
Cannot receive traffic?  -> Service / endpoints / labels
Cannot rollback safely?  -> app compatibility / database migration
```

---

# 34. Java/Spring Boot Production Checklist Before Rollout

Before rolling update, check:

```text
[ ] Image tag is immutable and exists in registry.
[ ] App has readiness and liveness probes.
[ ] Readiness checks core readiness, not optional systems.
[ ] New version is backward compatible with old DB schema.
[ ] Old version can survive rollback after migration.
[ ] Kafka/JSON event schema is backward compatible.
[ ] Redis key format changes are compatible.
[ ] Feature flags are safe.
[ ] Resource requests are realistic.
[ ] Cluster has surge capacity.
[ ] Logs include version/build info.
[ ] Metrics dashboard can compare old vs new behavior.
[ ] Rollback command is known.
```

Version endpoint example:

```java
@RestController
class BuildInfoController {
    @GetMapping("/build")
    Map<String, String> build() {
        return Map.of(
            "service", "order-service",
            "version", "1.1.0",
            "commit", System.getenv().getOrDefault("GIT_COMMIT", "unknown")
        );
    }
}
```

Why useful?

During rollout, you can confirm which Pod is serving which version.

---

# 35. Observability During Rollout

A rollout is not successful just because Kubernetes says complete.

Kubernetes success:

```text
Desired replicas available
Pods Ready
Deployment progressed
```

Business success:

```text
Users can place orders
Payments succeed
Latency acceptable
Error rate stable
No duplicate messages
No queue lag explosion
```

Watch:

```text
HTTP 5xx rate
HTTP 4xx unexpected spike
p95/p99 latency
JVM heap usage
GC pause
DB connection pool active/waiting
Kafka consumer lag
Redis errors
Thread pool saturation
Circuit breaker open rate
```

ASCII:

```text
Rollout status says:
Pods Ready

Metrics may say:
p99 latency high
5xx increasing
Kafka lag growing

Trust both, but investigate user-facing metrics first.
```

Production mindset:

```text
Deployment controller protects availability mechanics.
Observability protects customer experience.
```

---

# 36. Rollout With Slow Spring Boot Startup

Spring Boot apps can take time to start.

Reasons:

```text
Large classpath
JPA initialization
Flyway/Liquibase migrations
Cache warmup
Remote config loading
Connection pool initialization
```

If probes start too early, rollout may fail unnecessarily.

Bad probe:

```yaml
readinessProbe:
  initialDelaySeconds: 2
  periodSeconds: 2
  failureThreshold: 3
```

If app needs 30 seconds, this causes early failures.

Better:

```yaml
startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  failureThreshold: 30
  periodSeconds: 2

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  periodSeconds: 5
```

Mental model:

```text
startupProbe = give slow app time to start
readinessProbe = decide traffic eligibility
livenessProbe = restart deadlocked/broken app
```

This is very useful for Java services.

---

# 37. Graceful Shutdown During Rolling Update

Rolling update also terminates old Pods.

When old Pod is removed, Kubernetes sends SIGTERM.

Spring Boot should stop accepting new work and finish current requests.

`application.yml`:

```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

Deployment:

```yaml
terminationGracePeriodSeconds: 45
```

Flow:

```text
Deployment scales old ReplicaSet down
        |
        v
Pod receives SIGTERM
        |
        v
Readiness becomes false / endpoint removed
        |
        v
Existing requests drain
        |
        v
Process exits before grace period
```

Without graceful shutdown:

```text
In-flight requests may fail
Kafka messages may be interrupted
DB transactions may rollback unexpectedly
```

Production rule:

```text
Rolling update safety needs both startup readiness and shutdown grace.
```

---

# 38. preStop Hook

Sometimes you add a `preStop` hook to delay shutdown while load balancers remove endpoints.

Example:

```yaml
lifecycle:
  preStop:
    exec:
      command: ["sh", "-c", "sleep 10"]
```

Mental model:

```text
Give networking systems a small window to stop sending new traffic before process exits.
```

ASCII:

```text
Pod termination requested
       |
       v
Endpoint removal begins
       |
       v
preStop sleep
       |
       v
SIGTERM / graceful shutdown
       |
       v
Pod exits
```

Do not abuse preStop as a magic fix.

Use it when you understand traffic draining behavior.

For Spring Boot APIs, combine:

```text
readiness handling
server.shutdown=graceful
terminationGracePeriodSeconds
preStop if needed
```

---

# 39. Interview Questions

## What happens during a Kubernetes rolling update?

A Deployment creates a new ReplicaSet for the changed Pod template, gradually scales the new ReplicaSet up and the old ReplicaSet down, while respecting `maxSurge`, `maxUnavailable`, readiness, and availability constraints.

## Does Kubernetes update containers in place?

No. A Deployment update creates new Pods from a new Pod template. Existing Pods are replaced; their container images are not mutated in place.

## What is maxSurge?

`maxSurge` is the number or percentage of extra Pods allowed above desired replicas during rolling update. It lets Kubernetes start new Pods before deleting old ones, improving availability but requiring spare capacity.

## What is maxUnavailable?

`maxUnavailable` is the number or percentage of desired Pods that may be unavailable during rollout. Lower values improve availability but can slow or block rollouts.

## Why is readiness important during rolling update?

Readiness decides whether a Pod receives traffic through a Service. During rollout, new Pods should not receive traffic until they are truly ready. If readiness is wrong, Kubernetes may send traffic to broken Pods or block healthy Pods.

## What does rollback do?

Rollback changes the Deployment back to a previous Pod template revision and performs another controlled rollout. It can restore an older image or configuration, but it does not undo database migrations or external side effects.

## Why can rollback be dangerous?

Rollback is dangerous if the new version made incompatible database, message, cache, or API changes. The old code may not understand the new state.

## Rolling update vs recreate?

Rolling update gradually replaces Pods while keeping the service available. Recreate deletes old Pods first and then starts new Pods, usually causing downtime.

## Rolling update vs canary?

Rolling update is native gradual Pod replacement. Canary is controlled traffic exposure to a new version, usually requiring traffic splitting through ingress, service mesh, or progressive delivery tools.

---

# 40. Cheat Sheet

```text
RollingUpdate             = gradually replace old Pods with new Pods
Deployment                = manages rollout
ReplicaSet                = owns Pods for one Pod template
Revision                  = stored Deployment template version
Rollback                  = return to older Pod template
maxSurge                  = extra Pods allowed during rollout
maxUnavailable            = unavailable Pods allowed during rollout
Readiness                 = controls traffic eligibility
Liveness                  = controls restart decision
startupProbe              = protects slow-starting apps
progressDeadlineSeconds   = rollout progress timeout
revisionHistoryLimit      = number of old ReplicaSets kept
Recreate                  = delete old Pods before new Pods
Canary                    = small traffic percentage to new version
Blue-Green                = switch between two full environments
```

Core flow:

```text
Update Deployment image
        |
        v
New Pod template
        |
        v
New ReplicaSet
        |
        v
New Pods start
        |
        v
Readiness passes
        |
        v
Old Pods removed gradually
        |
        v
Rollout complete
```

Rollback flow:

```text
Bad rollout detected
        |
        v
kubectl rollout undo
        |
        v
Deployment uses previous revision
        |
        v
Old-good ReplicaSet scales up
        |
        v
Bad ReplicaSet scales down
```

---

# 41. One Picture To Remember

```text
                         Deployment
                    order-service replicas=3
                              |
                              |
                 image changes from v1 -> v2
                              |
                              v
             +--------------------------------+
             | Deployment Controller          |
             | safe rollout brain             |
             +---------------+----------------+
                             |
             +---------------+----------------+
             |                                |
             v                                v
      ReplicaSet v1                    ReplicaSet v2
      old template                     new template
      image: 1.0.0                     image: 1.1.0
             |                                |
             v                                v
      [old] [old] [old]          [new starting]
             |                                |
             |       readiness passes         |
             +----------------+---------------+
                              |
                              v
                  old down, new up gradually
                              |
                              v
                        [new] [new] [new]

Safety gates:

maxSurge        -> how many extra Pods may exist
maxUnavailable  -> how much capacity may be lost
readiness        -> can this Pod receive traffic?
resources        -> can scheduler place new Pods?
observability    -> are users actually healthy?
rollback         -> can we return to old template safely?
```

Final memory hook:

```text
Rolling update is not about changing containers.
It is about safely moving production traffic capacity from one ReplicaSet to another.
```

---

# 42. Final Production Checklist

```text
[ ] I understand Deployment creates a new ReplicaSet for a changed Pod template.
[ ] I understand old and new versions may run together.
[ ] I understand maxSurge and maxUnavailable as availability controls.
[ ] I understand readiness is the rollout traffic gate.
[ ] I know how to inspect rollout status and history.
[ ] I know rollback changes Pod template, not database state.
[ ] I design DB migrations to be rollback-safe.
[ ] I know how to debug ImagePullBackOff, CrashLoopBackOff, Pending, and NotReady.
[ ] I know that Kubernetes rollout success is not equal to business success.
[ ] I monitor metrics during and after rollout.
[ ] I configure graceful shutdown for Java services.
[ ] I can explain RollingUpdate vs Recreate vs Canary vs Blue-Green.
```

Final sentence:

```text
Kubernetes rolling update is a production safety mechanism, but real zero-downtime depends on application compatibility, probes, resources, graceful shutdown, and observability.
```
