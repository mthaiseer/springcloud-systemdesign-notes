# 005_Pod_Lifecycle.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Pod Lifecycle Matters

A Pod is not just a container wrapper.

A Pod is a small production machine with a life story.

Most beginners memorize:

```text
Pending
Running
Succeeded
Failed
Unknown
```

That is not enough.

In real production, you need to understand:

```text
Who creates the Pod?
Who schedules it?
Who pulls the image?
Who starts containers?
Who runs init containers?
Who mounts volumes?
Who checks health?
Who sends traffic?
Who restarts crashed containers?
Who kills the Pod during rollout?
Who replaces it?
```

The correct mental model:

```text
Pod lifecycle = desired Pod object becoming real running workload
```

One picture:

```text
Pod Object
   |
   v
Scheduled to Node
   |
   v
Prepared by Kubelet
   |
   v
Containers Started
   |
   v
Health Checked
   |
   v
Ready for Traffic
   |
   v
Terminated Gracefully
```

Do not memorize phase names first.

Understand the journey first.

A Pod lifecycle is Kubernetes turning a written record into a running application process, then protecting, observing, restarting, and eventually terminating it.

---

# 2. The Big Picture

Pod lifecycle has two worlds.

```text
CONTROL PLANE WORLD
- Pod object exists
- Scheduler chooses Node
- API Server stores status

NODE WORLD
- Kubelet sees assigned Pod
- Container runtime pulls image
- Containers start
- Probes run
- Logs and status reported
```

ASCII:

```text
Developer / Controller
        |
        | create Pod object
        v
+-------------------+
| API Server        |
+---------+---------+
          |
          | Pod has no nodeName
          v
+-------------------+
| Scheduler         |
| chooses Node      |
+---------+---------+
          |
          | Pod assigned to node-1
          v
+-------------------+
| Kubelet on node-1 |
| makes Pod real    |
+---------+---------+
          |
          v
+-------------------+
| Container Runtime |
| pulls + starts    |
+---------+---------+
          |
          v
+-------------------+
| Running App       |
+-------------------+
```

Important rule:

```text
Scheduler places the Pod.
Kubelet runs the Pod.
Controller replaces the Pod.
Service sends traffic only when Pod is Ready.
```

Each role is separate.

---

# 3. Real World Analogy: Hotel Guest Lifecycle

Think of a Pod as a hotel guest.

```text
Reservation created       = Pod object created
Room assigned             = Scheduler assigns Node
Room prepared             = Kubelet mounts volumes, pulls image
Guest enters room         = Container starts
Guest is available        = Readiness probe passes
Guest has emergency       = Liveness probe fails / container crashes
Guest checks out          = Pod termination starts
Room cleaned/reused       = Pod removed, replacement may be created
```

Diagram:

```text
Reservation
   |
   v
Room Assignment
   |
   v
Room Preparation
   |
   v
Guest Enters
   |
   v
Ready For Service
   |
   v
Checkout
```

A reservation is not the same as a guest sleeping in the room.

Similarly:

```text
Pod object exists != container running
Container running != app ready
App ready != app healthy forever
Pod deleted != instantly gone
```

This is the first production-grade lesson.

---

# 4. Pod Is A Shared Sandbox

A Pod can contain one or more containers.

They share:

```text
Network namespace
Pod IP
Volumes
Lifecycle fate
```

They do not share:

```text
Process memory
Filesystem image layers by default
Java heap
Main process PID namespace unless configured
```

ASCII:

```text
Pod: order-service-pod
+------------------------------------------------+
| Shared network namespace                       |
| Pod IP: 10.1.2.15                              |
|                                                |
|  +------------------+   +------------------+   |
|  | app container    |   | sidecar container|   |
|  | Spring Boot      |   | log agent/proxy  |   |
|  | port 8080        |   | port 15000       |   |
|  +------------------+   +------------------+   |
|                                                |
| Shared volumes: /config /logs                  |
+------------------------------------------------+
```

Mental model:

```text
Pod = small machine
Container = process inside that machine
```

For most Java backend services:

```text
One Pod = one Spring Boot application container
```

Sidecars appear later for:

```text
Service mesh proxy
Log shipper
Config reloader
Security agent
```

---

# 5. Who Creates Pods?

Usually you do not create Pods directly in production.

You create higher-level objects:

```text
Deployment
ReplicaSet
StatefulSet
DaemonSet
Job
CronJob
```

They create Pods.

Diagram:

```text
Deployment
    |
    v
ReplicaSet
    |
    v
Pod
    |
    v
Container
```

If a Pod dies, you usually do not repair that exact Pod.

The owning controller creates a replacement.

```text
Old Pod died
   |
   v
ReplicaSet sees count mismatch
   |
   v
New Pod object created
```

This is why Pod names look unstable:

```text
order-service-7c9d8f9c6b-x2p9m
order-service-7c9d8f9c6b-h7kq1
```

The Pod is disposable.

The application identity should come from:

```text
Deployment name
Service name
Labels
Config
Persistent identity only when using StatefulSet
```

Do not build application logic around Pod names.

---

# 6. Pod Lifecycle In One Line

```text
Created -> Scheduled -> Prepared -> Started -> Ready -> Serving -> Terminating -> Gone
```

Kubernetes phase names are fewer:

```text
Pending
Running
Succeeded
Failed
Unknown
```

But real production lifecycle is richer.

ASCII:

```text
Pod Object Created
        |
        v
Pending: waiting for schedule / images / volumes
        |
        v
Running: at least one container running/starting
        |
        v
Ready: readiness probe passed
        |
        v
Serving: Service endpoints include Pod
        |
        v
Terminating: deletion requested, grace period active
        |
        v
Succeeded / Failed / Removed
```

Key lesson:

```text
Phase is a broad summary.
Conditions and container states reveal the real story.
```

When debugging, do not stop at:

```bash
kubectl get pods
```

Go deeper:

```bash
kubectl describe pod <pod>
kubectl get pod <pod> -o yaml
kubectl logs <pod>
kubectl logs <pod> --previous
```

---

# 7. Pod Phase vs Pod Condition vs Container State

Kubernetes exposes different layers of truth.

```text
Pod Phase:
  broad lifecycle summary

Pod Conditions:
  scheduling/readiness details

Container State:
  waiting/running/terminated details
```

ASCII:

```text
Pod
 |
 +-- phase: Pending / Running / Succeeded / Failed / Unknown
 |
 +-- conditions:
 |     PodScheduled
 |     Initialized
 |     ContainersReady
 |     Ready
 |
 +-- containerStatuses:
       state:
         waiting
         running
         terminated
       lastState:
         terminated
       restartCount
```

Example:

```text
Phase: Running
Ready: False
Container State: Waiting
Reason: CrashLoopBackOff
```

Meaning:

```text
The Pod reached a Node.
But the app container is repeatedly crashing.
It is not ready for traffic.
```

Another example:

```text
Phase: Pending
PodScheduled: False
Reason: Unschedulable
```

Meaning:

```text
Scheduler cannot find a suitable Node.
```

Production debugging is reading these layers correctly.

---

# 8. Pod Object Creation

A Pod object can be created by a ReplicaSet.

Example Deployment:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
        - name: app
          image: order-service:1.0.0
          ports:
            - containerPort: 8080
```

Flow:

```text
Deployment Controller
   |
   v
ReplicaSet Controller
   |
   v
Creates Pod objects
```

At this moment:

```text
Pod exists in API Server
Pod is stored in etcd
Pod may not be running anywhere yet
Pod has no Node assigned yet
```

ASCII:

```text
etcd has record:

/pods/default/order-service-abc
  spec:
    containers:
      image: order-service:1.0.0
  status:
    phase: Pending
```

This is like a work order.

The work order is not the finished work.

---

# 9. Scheduling Stage

Scheduler watches for Pods with no assigned Node.

```text
Pod.spec.nodeName is empty
```

Scheduler asks:

```text
Which Node can run this Pod?
```

It considers:

```text
CPU request
Memory request
Node capacity
Taints and tolerations
Node selectors
Affinity/anti-affinity
Volume constraints
Topology spread
Existing workload pressure
```

ASCII:

```text
Pending Pod
   |
   v
Scheduler
   |
   +--> node-1: enough CPU? yes
   +--> node-2: taint? no toleration
   +--> node-3: not enough memory
   |
   v
Pick node-1
   |
   v
Bind Pod to node-1
```

Important:

```text
Scheduler does not pull images.
Scheduler does not start containers.
Scheduler only binds Pod to Node.
```

After binding:

```text
spec.nodeName: node-1
condition PodScheduled=True
```

Then kubelet on node-1 takes over.

---

# 10. Why Pods Stay Pending

Pending means:

```text
Pod accepted by Kubernetes, but not fully running yet
```

Common causes:

```text
No Node has enough CPU/memory
Taints block scheduling
PVC cannot bind
Image is still being pulled
Init container is running
Network plugin is not ready
Volume mount is slow
```

ASCII:

```text
Pending
 |
 +-- not scheduled yet
 |     reason: Unschedulable
 |
 +-- scheduled but preparing
       reason: ContainerCreating
```

Commands:

```bash
kubectl describe pod order-service-abc
kubectl get events --sort-by=.lastTimestamp
kubectl describe node <node>
kubectl get pvc
```

Examples:

```text
0/3 nodes are available: insufficient memory
```

Means:

```text
Your resource requests do not fit current nodes.
```

Example:

```text
pod has unbound immediate PersistentVolumeClaims
```

Means:

```text
Storage is not ready.
```

Never treat Pending as one problem.

Pending is a waiting room with many doors.

---

# 11. Kubelet Preparation Stage

Once the Pod is assigned to a Node, kubelet starts preparing it.

Kubelet does not directly run your Java process first.

It prepares the environment:

```text
Create Pod sandbox
Set up networking
Attach Pod IP
Mount volumes
Fetch Secrets/ConfigMaps
Pull images
Run init containers
Start app containers
Run probes
Report status
```

ASCII:

```text
Assigned Pod
   |
   v
Kubelet
   |
   +--> create sandbox
   +--> setup CNI networking
   +--> mount volumes
   +--> pull image
   +--> run init containers
   +--> start containers
   +--> run probes
```

This explains why you may see:

```text
ContainerCreating
```

for a while.

It may not be Java startup yet.

It may be:

```text
waiting for image
waiting for volume
waiting for network
waiting for Secret
waiting for ConfigMap
```

Kubelet is the local site engineer.

---

# 12. ContainerCreating Deep Dive

`ContainerCreating` means kubelet is still preparing container execution.

Common hidden work:

```text
Pulling image layers
Creating container filesystem
Mounting projected volumes
Attaching ConfigMaps
Attaching Secrets
Setting up service account token
Configuring network namespace
Setting DNS config
```

ASCII:

```text
ContainerCreating
       |
       +-- image pull
       +-- volume mount
       +-- CNI setup
       +-- secret/config projection
       +-- container runtime create
```

Debug:

```bash
kubectl describe pod <pod>
```

Look at Events:

```text
Pulling image "order-service:1.0.0"
Successfully pulled image
Created container app
Started container app
```

If stuck:

```text
FailedMount
FailedCreatePodSandBox
ErrImagePull
ImagePullBackOff
```

This tells where the lifecycle is blocked.

Mental model:

```text
ContainerCreating is infrastructure preparation, not application execution.
```

---

# 13. Image Pull Stage

Kubelet asks the container runtime to pull the image.

```text
image: registry.example.com/order-service:1.0.0
```

Flow:

```text
Kubelet
  |
  v
Container Runtime
  |
  v
Registry
  |
  v
Image layers downloaded
```

ASCII:

```text
Node
+----------------------------------+
| kubelet                          |
|   |                              |
|   v                              |
| containerd                       |
|   | pull image                   |
|   v                              |
| registry.example.com             |
+----------------------------------+
```

Failure examples:

```text
ErrImagePull
ImagePullBackOff
```

Reasons:

```text
Wrong tag
Image not pushed
Private registry auth missing
Registry unavailable
Network/DNS failure
Architecture mismatch
```

Debug:

```bash
kubectl describe pod <pod>
kubectl get secret
kubectl describe secret <image-pull-secret>
```

Production story:

```text
CI builds order-service:latest locally.
Deployment points to order-service:1.2.0.
Tag was never pushed.
Pods stay ImagePullBackOff.
```

Lesson:

```text
A Pod cannot start a container without a pullable image.
```

---

# 14. Init Containers

Init containers run before app containers.

They run one by one.

App containers start only after all init containers succeed.

Use init containers for:

```text
Wait for dependency
Run schema check
Prepare config file
Download certificate
Verify network/DNS
```

ASCII:

```text
Pod Start
   |
   v
Init Container 1
   |
   v
Init Container 2
   |
   v
App Container
```

Example:

```yaml
initContainers:
  - name: wait-for-db
    image: busybox:1.36
    command:
      - sh
      - -c
      - |
        until nc -z postgres 5432; do
          echo waiting for postgres
          sleep 2
        done
containers:
  - name: app
    image: order-service:1.0.0
```

Be careful.

Bad init container logic can block Pod startup forever.

Debug:

```bash
kubectl logs <pod> -c wait-for-db
kubectl describe pod <pod>
```

Mental model:

```text
Init container is a gate.
If the gate never opens, the app never starts.
```

---

# 15. App Container Start

After preparation and init containers, kubelet starts the app container.

For Spring Boot:

```text
Container process:
java -jar app.jar
```

Docker image entrypoint may look like:

```dockerfile
ENTRYPOINT ["java", "-jar", "/app/order-service.jar"]
```

Lifecycle:

```text
Container created
   |
   v
Process started
   |
   v
JVM starts
   |
   v
Spring context loads
   |
   v
Tomcat starts
   |
   v
Actuator health endpoints available
```

ASCII:

```text
Kubelet
   |
   v
containerd
   |
   v
java process
   |
   v
Spring Boot startup
   |
   v
HTTP server listens on 8080
```

Important:

```text
Container started does not mean application is ready.
```

Spring Boot may still be:

```text
loading beans
connecting to DB
running migrations
warming cache
loading secrets
```

This is why probes matter.

---

# 16. Container States

Each container has a state:

```text
Waiting
Running
Terminated
```

ASCII:

```text
Waiting
   |
   v
Running
   |
   v
Terminated
```

Examples:

```text
Waiting:
  reason: ContainerCreating
  reason: CrashLoopBackOff
  reason: ImagePullBackOff

Running:
  startedAt: timestamp

Terminated:
  exitCode: 0
  exitCode: 1
  reason: Completed
  reason: Error
  reason: OOMKilled
```

Check:

```bash
kubectl get pod <pod> -o yaml
```

Look at:

```yaml
status:
  containerStatuses:
    - name: app
      state:
        waiting:
          reason: CrashLoopBackOff
      lastState:
        terminated:
          exitCode: 1
      restartCount: 7
```

This tells the truth.

Pod phase alone is not enough.

---

# 17. Restart Policy

Pod restart policy controls what kubelet does when a container exits.

Values:

```text
Always
OnFailure
Never
```

For Deployments:

```text
restartPolicy: Always
```

This means:

```text
If the app container exits, kubelet restarts it.
```

For Jobs:

```text
restartPolicy: OnFailure or Never
```

ASCII:

```text
Container exits with code 1
       |
       v
restartPolicy?
       |
       +-- Always    -> restart
       +-- OnFailure -> restart if non-zero
       +-- Never     -> do not restart
```

Production example:

```text
Spring Boot exits because DB password is wrong.
Kubelet restarts it.
It exits again.
Backoff increases.
Status becomes CrashLoopBackOff.
```

Important:

```text
Restart policy restarts containers inside the same Pod.
ReplicaSet replaces missing Pods.
These are different mechanisms.
```

---

# 18. CrashLoopBackOff

CrashLoopBackOff means:

```text
Container starts, crashes, starts again, crashes again.
Kubernetes waits longer between restarts.
```

ASCII:

```text
Start
  |
  v
Crash
  |
  v
Restart after small delay
  |
  v
Crash
  |
  v
Restart after bigger delay
  |
  v
CrashLoopBackOff
```

Common Spring Boot causes:

```text
Missing environment variable
Wrong DB URL
Wrong profile
Liquibase/Flyway migration failure
Port mismatch
OutOfMemoryError
Bad JVM flags
Missing truststore
Kafka bootstrap unreachable during fail-fast startup
```

Debug:

```bash
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl describe pod <pod>
```

`--previous` is critical because current container may have restarted.

Mental model:

```text
Kubernetes is doing its job.
The app is failing during startup or runtime.
```

Do not fix CrashLoopBackOff by increasing replicas.

Fix the root cause.

---

# 19. OOMKilled

OOMKilled means the container exceeded its memory limit.

Example:

```yaml
resources:
  requests:
    memory: "512Mi"
  limits:
    memory: "512Mi"
```

Spring Boot starts with:

```text
JVM heap + metaspace + thread stacks + direct memory + native memory
```

If total exceeds limit:

```text
Linux kills the process
Kubernetes reports OOMKilled
Container restarts
```

ASCII:

```text
Memory Limit: 512Mi
        |
        v
JVM uses 700Mi
        |
        v
Kernel OOM killer
        |
        v
Container terminated
        |
        v
Reason: OOMKilled
```

Debug:

```bash
kubectl describe pod <pod>
kubectl top pod <pod>
kubectl logs <pod> --previous
```

Spring Boot fix examples:

```text
Set realistic memory limit
Use container-aware JVM
Tune -XX:MaxRAMPercentage
Reduce thread pools
Fix memory leak
```

Example JVM option:

```yaml
env:
  - name: JAVA_TOOL_OPTIONS
    value: "-XX:MaxRAMPercentage=70 -XX:+UseContainerSupport"
```

---

# 20. Pod Conditions

Useful Pod conditions:

```text
PodScheduled
Initialized
ContainersReady
Ready
```

ASCII:

```text
PodScheduled=True
        |
        v
Initialized=True
        |
        v
ContainersReady=True
        |
        v
Ready=True
```

Meaning:

```text
PodScheduled:
  Scheduler assigned Node.

Initialized:
  Init containers completed.

ContainersReady:
  All app containers are ready.

Ready:
  Pod can receive traffic.
```

Example:

```text
Running but Ready=False
```

Could mean:

```text
Readiness probe failing
Sidecar not ready
App container not ready
```

Commands:

```bash
kubectl describe pod <pod>
kubectl get pod <pod> -o jsonpath='{.status.conditions}'
```

Important:

```text
Service endpoints use readiness.
Traffic should go only to Ready Pods.
```

---

# 21. Readiness Probe

Readiness probe answers:

```text
Can this Pod receive traffic now?
```

For Spring Boot:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
```

Kubernetes:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
  failureThreshold: 3
```

ASCII:

```text
Service
  |
  | sends traffic only to Ready Pods
  v

Pod A Ready=True   -> receives traffic
Pod B Ready=False  -> no traffic
Pod C Ready=True   -> receives traffic
```

Use readiness for:

```text
DB connected
Cache warmed
Migration done
App can serve requests
Dependency gateway reachable if required
```

Do not make readiness too strict.

If readiness depends on every downstream service, a downstream failure can remove all Pods from traffic and cause full outage.

Production mindset:

```text
Readiness should answer: should this Pod receive traffic?
Not: is the entire universe healthy?
```

---

# 22. Liveness Probe

Liveness probe answers:

```text
Is this container stuck and should it be restarted?
```

Example:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
  failureThreshold: 3
```

ASCII:

```text
Liveness fails repeatedly
        |
        v
Kubelet kills container
        |
        v
Container restarted
```

Use liveness for:

```text
Deadlock
Event loop stuck
Application cannot recover without restart
```

Do not use liveness for temporary dependency failures.

Bad design:

```text
DB down -> liveness DOWN -> all Pods restart
```

That makes outage worse.

Correct thinking:

```text
DB down may make readiness fail.
But liveness should usually stay UP if process is alive and can recover.
```

Liveness is a restart trigger.

Use it carefully.

---

# 23. Startup Probe

Startup probe protects slow-starting applications.

Spring Boot services can be slow when:

```text
Many beans load
JPA scans entities
Flyway/Liquibase runs migrations
Large caches warm up
JVM cold starts
```

Without startup probe:

```text
Liveness may kill app before startup completes
```

Startup probe:

```yaml
startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  failureThreshold: 30
  periodSeconds: 5
```

Meaning:

```text
Allow up to 150 seconds for startup.
Only after startup succeeds, liveness begins.
```

ASCII:

```text
Container starts
   |
   v
Startup probe active
   |
   +-- failing? wait, do not run liveness yet
   |
   v
Startup succeeds
   |
   v
Liveness + readiness take over
```

Production lesson:

```text
Use startup probe for slow Java apps.
Do not fake it by setting huge liveness initialDelay forever.
```

---

# 24. Probe Types

Kubernetes supports different probe mechanisms.

```text
HTTP GET
TCP Socket
Exec command
gRPC
```

HTTP example:

```yaml
httpGet:
  path: /actuator/health/readiness
  port: 8080
```

TCP example:

```yaml
tcpSocket:
  port: 8080
```

Exec example:

```yaml
exec:
  command:
    - sh
    - -c
    - test -f /tmp/healthy
```

Mental model:

```text
HTTP probe:
  application-level check

TCP probe:
  port is open

Exec probe:
  command inside container
```

For Spring Boot, HTTP actuator probes are usually best.

TCP only proves that a port accepts connections.

It does not prove the app can serve correctly.

---

# 25. Service Endpoint Lifecycle

A Pod does not receive Service traffic just because it exists.

It must match labels and be Ready.

Flow:

```text
Pod labels match Service selector
        |
        v
Readiness probe passes
        |
        v
EndpointSlice includes Pod IP
        |
        v
Service routes traffic
```

ASCII:

```text
Service: order-service
selector: app=order-service

      |
      v

Pod A label match + Ready=True   -> endpoint
Pod B label match + Ready=False  -> not endpoint
Pod C wrong label + Ready=True   -> not endpoint
```

Debug:

```bash
kubectl get endpoints order-service
kubectl get endpointslice
kubectl describe svc order-service
kubectl get pods --show-labels
```

Production bug:

```text
Pod is healthy.
Readiness passes.
But Service has no endpoints.
```

Likely cause:

```text
Label selector mismatch.
```

This is lifecycle + networking combined.

---

# 26. Graceful Termination

Pod termination is not instant.

When a Pod is deleted:

```text
1. API Server marks Pod with deletionTimestamp.
2. Pod enters Terminating.
3. Endpoint controller removes Pod from Service endpoints.
4. Kubelet sends SIGTERM to containers.
5. App gets grace period to shut down.
6. Kubelet sends SIGKILL if still alive after grace period.
7. Pod object is removed.
```

ASCII:

```text
Delete Pod
   |
   v
Terminating
   |
   +--> removed from Service endpoints
   |
   +--> SIGTERM sent to app
   |
   +--> app finishes in-flight requests
   |
   v
Exit cleanly
```

Default grace period:

```text
30 seconds
```

YAML:

```yaml
terminationGracePeriodSeconds: 45
```

For Java services, graceful shutdown matters.

Without it:

```text
User request gets cut
Kafka message processing duplicates
DB transaction interrupted
Payment flow becomes inconsistent
```

---

# 27. Spring Boot Graceful Shutdown

Spring Boot supports graceful shutdown.

Example:

```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

When SIGTERM arrives:

```text
Spring Boot stops accepting new requests
Lets active requests finish
Closes application context
Shuts down connectors
```

ASCII:

```text
SIGTERM
  |
  v
Stop accepting new traffic
  |
  v
Finish in-flight HTTP requests
  |
  v
Close DB/Kafka/Redis clients
  |
  v
Exit
```

Kubernetes YAML:

```yaml
spec:
  terminationGracePeriodSeconds: 45
```

Important:

```text
terminationGracePeriodSeconds must be longer than app graceful shutdown timeout.
```

Bad setup:

```text
Spring wants 30s to shutdown.
Kubernetes kills after 10s.
Result: SIGKILL before graceful finish.
```

Production mindset:

```text
Graceful termination is part of correctness, not just cleanliness.
```

---

# 28. preStop Hook

A `preStop` hook runs before Kubernetes sends SIGTERM or as part of termination handling.

Example:

```yaml
lifecycle:
  preStop:
    exec:
      command:
        - sh
        - -c
        - "sleep 10"
```

Why sleep?

```text
Give load balancers and endpoints time to stop routing traffic.
```

ASCII:

```text
Pod deletion
   |
   v
preStop sleep 10
   |
   v
SIGTERM
   |
   v
App graceful shutdown
```

Be careful:

```text
preStop time counts inside termination grace period.
```

If grace period is 30 seconds and preStop sleeps 20 seconds, app may only have around 10 seconds left.

Better design:

```text
Use readiness failure + endpoint removal + graceful shutdown.
Avoid huge blind sleeps unless needed for external load balancer delay.
```

---

# 29. Pod Deletion vs Replacement

When a Deployment needs 3 replicas and one Pod is deleted:

```text
ReplicaSet creates replacement Pod.
```

ASCII:

```text
Desired replicas = 3

Pod A Running
Pod B Deleted
Pod C Running

ReplicaSet:
actual = 2
desired = 3
action = create Pod D
```

Important:

```text
The same Pod does not come back.
A new Pod is created.
```

This affects:

```text
Pod IP
Pod name
local filesystem data
in-memory sessions
temporary cache
```

Therefore:

```text
Do not store important state inside a Pod filesystem.
Do not bind users to Pod IPs.
Do not depend on Pod name for identity unless StatefulSet pattern.
```

Pod lifecycle teaches stateless design.

---

# 30. Rolling Update Pod Lifecycle

During a rolling update, old Pods terminate and new Pods start gradually.

Example:

```text
Old version: order-service:1.0.0
New version: order-service:1.1.0
```

Flow:

```text
Create one new Pod
Wait until Ready
Remove one old Pod
Create another new Pod
Wait until Ready
Repeat
```

ASCII:

```text
Step 0: Old Old Old
Step 1: New(starting) Old Old
Step 2: New(ready)    Old Old
Step 3: New           New(starting) Old
Step 4: New           New(ready)    Old
Step 5: New           New           New
```

If new Pod readiness fails:

```text
Rollout pauses / does not progress
Old Pods may continue serving
```

Debug:

```bash
kubectl rollout status deployment/order-service
kubectl describe deployment order-service
kubectl get rs
kubectl get pods
```

Mental model:

```text
Readiness gates rollout safety.
```

---

# 31. Job Pod Lifecycle

Not all Pods are long-running services.

Job Pods are expected to complete.

Example:

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: invoice-export
spec:
  template:
    spec:
      restartPolicy: OnFailure
      containers:
        - name: exporter
          image: invoice-exporter:1.0.0
```

Lifecycle:

```text
Pending
Running
Succeeded
```

For Jobs:

```text
Succeeded is healthy completion.
```

For Deployments:

```text
Succeeded usually means app exited, which is bad.
```

ASCII:

```text
Job Pod
   |
   v
Run task
   |
   v
Exit 0
   |
   v
Succeeded
```

Mental model:

```text
Same Pod phase can mean different things depending on owner.
```

A Spring Boot batch job may exit after completing work.

A Spring Boot REST API should usually keep running.

---

# 32. Sidecar Lifecycle

Sidecars run in the same Pod.

Common examples:

```text
Istio Envoy proxy
Log collector
Metrics exporter
Config reloader
```

ASCII:

```text
Pod
+-----------------------------------+
| app container                     |
| sidecar container                 |
+-----------------------------------+
```

Readiness may depend on both containers.

Problem:

```text
App is ready.
Sidecar is not ready.
Pod Ready=False.
```

Another problem:

```text
App exits.
Sidecar keeps running.
Pod may not terminate as expected.
```

Modern Kubernetes has sidecar improvements, but the mental model remains:

```text
Pod lifecycle is the combined lifecycle of its containers.
```

Debug:

```bash
kubectl logs <pod> -c app
kubectl logs <pod> -c sidecar
kubectl describe pod <pod>
```

Do not look only at the app container.

---

# 33. Complete Spring Boot Deployment Example

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: order-service
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 0
      maxSurge: 1
  template:
    metadata:
      labels:
        app: order-service
    spec:
      terminationGracePeriodSeconds: 45
      containers:
        - name: app
          image: registry.example.com/order-service:1.0.0
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: prod
            - name: JAVA_TOOL_OPTIONS
              value: "-XX:MaxRAMPercentage=70 -XX:+UseContainerSupport"
          resources:
            requests:
              cpu: "500m"
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
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 5
            periodSeconds: 5
            failureThreshold: 3
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            periodSeconds: 10
            failureThreshold: 3
```

This YAML is not just syntax.

It expresses lifecycle rules.

```text
startupProbe    = give Java time to start
readinessProbe  = route traffic only when ready
livenessProbe   = restart if stuck
resources       = schedule and protect memory/CPU
grace period    = shutdown cleanly
rolling strategy= replace safely
```

---

# 34. Java Code: Graceful Shutdown Signal Awareness

Spring Boot handles SIGTERM, but understanding the signal helps.

Example bean:

```java
package com.example.order.lifecycle;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class ShutdownLogger {

    @PreDestroy
    public void onShutdown() {
        System.out.println("SIGTERM received. Closing resources gracefully...");
    }
}
```

Example controller:

```java
package com.example.order.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthDemoController {

    @GetMapping("/demo/slow")
    public String slowRequest() throws InterruptedException {
        Thread.sleep(5000);
        return "finished";
    }
}
```

What happens during termination:

```text
Request enters /demo/slow
Pod receives SIGTERM
Spring graceful shutdown starts
New requests are rejected
Existing slow request can finish
Application exits cleanly
```

This prevents user-facing failures during rollout.

Do not memorize graceful shutdown.

Think:

```text
Pod death should not cut business transactions in half.
```

---

# 35. Java Code: Readiness Depends On Local App State

Sometimes you want readiness to fail until warmup completes.

Example:

```java
package com.example.order.lifecycle;

import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class CacheWarmup {

    private final ApplicationEventPublisher publisher;

    public CacheWarmup(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void markNotReady() {
        AvailabilityChangeEvent.publish(
                publisher,
                this,
                ReadinessState.REFUSING_TRAFFIC
        );
    }

    public void markReady() {
        AvailabilityChangeEvent.publish(
                publisher,
                this,
                ReadinessState.ACCEPTING_TRAFFIC
        );
    }
}
```

Mental model:

```text
Readiness is a traffic switch.
```

During startup:

```text
Load config
Warm cache
Verify essential local dependencies
Then accept traffic
```

Do not abuse readiness as a full dependency dashboard.

It should protect traffic routing.

---

# 36. Dry Run: Healthy Pod Startup

```text
1. Deployment wants 3 replicas.

2. ReplicaSet creates Pod order-service-abc.

3. API Server stores Pod object.

4. Scheduler sees unscheduled Pod.

5. Scheduler chooses node-2.

6. API Server updates Pod binding.

7. Kubelet on node-2 sees assigned Pod.

8. Kubelet creates Pod sandbox.

9. CNI plugin assigns Pod IP.

10. Kubelet mounts ConfigMaps and Secrets.

11. Runtime pulls image.

12. Init containers run successfully.

13. App container starts.

14. JVM starts.

15. Spring Boot starts Tomcat.

16. Startup probe passes.

17. Readiness probe passes.

18. Pod Ready=True.

19. EndpointSlice includes Pod IP.

20. Service sends traffic.
```

ASCII:

```text
Pod Object
  -> Scheduled
  -> Sandbox
  -> Image Pull
  -> Init Done
  -> App Running
  -> Startup OK
  -> Readiness OK
  -> Service Traffic
```

This is the happy path.

---

# 37. Dry Run: Bad Image Tag

Deployment:

```yaml
image: registry.example.com/order-service:9.9.9
```

But tag does not exist.

Lifecycle:

```text
1. Pod object created.
2. Scheduler assigns Node.
3. Kubelet tries image pull.
4. Registry returns not found.
5. Pod shows ErrImagePull.
6. Kubelet retries.
7. Status becomes ImagePullBackOff.
8. App container never starts.
```

ASCII:

```text
Scheduled
   |
   v
Image Pull
   |
   v
Not Found
   |
   v
ImagePullBackOff
```

Debug:

```bash
kubectl describe pod <pod>
```

Look for:

```text
Failed to pull image
manifest unknown
pull access denied
```

Fix:

```text
Push correct image tag
Update deployment image
Fix registry credentials
```

Wrong fix:

```text
Restart Pod repeatedly
```

The lifecycle is blocked before application startup.

---

# 38. Dry Run: Readiness Failure

Spring Boot starts but DB is unavailable.

Lifecycle:

```text
1. Container starts.
2. JVM starts.
3. Spring Boot starts.
4. Liveness is UP.
5. Readiness returns DOWN.
6. Pod phase may be Running.
7. Pod Ready=False.
8. Service does not send traffic.
```

ASCII:

```text
Container Running
      |
      v
App process alive
      |
      v
Readiness DOWN
      |
      v
No Service traffic
```

Debug:

```bash
kubectl get pods
kubectl describe pod <pod>
kubectl logs <pod>
curl /actuator/health/readiness
```

Good behavior:

```text
Pod does not receive user traffic until safe.
```

Bad behavior:

```text
No readiness probe.
Pod receives traffic while DB connection is still failing.
Users see 500 errors.
```

Readiness is rollout safety.

---

# 39. Dry Run: Liveness Misconfiguration Outage

Bad liveness probe:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
```

If `/actuator/health` includes DB health and DB goes down:

```text
DB down
   |
   v
Health DOWN
   |
   v
Liveness fails
   |
   v
Kubelet restarts all Pods
   |
   v
Pods restart repeatedly
   |
   v
Outage becomes worse
```

ASCII:

```text
Temporary DB failure
        |
        v
Bad liveness design
        |
        v
Mass restarts
        |
        v
Bigger outage
```

Correct approach:

```text
Liveness = process can recover
Readiness = should receive traffic
Dependency health = metrics/alerts/dashboard
```

For Spring Boot:

```text
Use /actuator/health/liveness for liveness.
Use /actuator/health/readiness for readiness.
```

---

# 40. Production Story: Rolling Update Cut Payments

A payment service was deployed with no graceful shutdown.

During rollout:

```text
Old Pod receives payment request.
Deployment deletes old Pod.
Kubelet sends SIGTERM.
App exits immediately.
Request is cut halfway.
Client retries.
Payment provider receives duplicate attempt.
```

Root causes:

```text
No graceful shutdown
No idempotency key
terminationGracePeriod too short
Readiness not removed early enough
```

Correct design:

```text
Payment request uses idempotency key.
Pod has readiness probe.
Pod supports graceful shutdown.
terminationGracePeriodSeconds is long enough.
Kafka/outbox handles retry safely.
```

ASCII:

```text
Rollout
  |
  v
Pod termination
  |
  v
Finish in-flight payment
  |
  v
Exit safely
```

Kubernetes lifecycle is part of business correctness.

---

# 41. Production Story: Slow Java Startup Killed By Liveness

A Spring Boot app takes 90 seconds to start because:

```text
JPA scans entities
Flyway migration checks DB
Large config loads
JVM cold start
```

Liveness config:

```yaml
livenessProbe:
  initialDelaySeconds: 20
  periodSeconds: 10
```

Result:

```text
20s: liveness starts
30s: fail
40s: fail
50s: fail
Kubelet kills container
App never gets 90s to start
CrashLoopBackOff
```

Fix:

```yaml
startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  failureThreshold: 30
  periodSeconds: 5
```

Mental model:

```text
Startup probe protects boot time.
Liveness protects runtime stuck state.
Readiness protects traffic.
```

---

# 42. Production Story: Pod Ready But Service Still Fails

Symptoms:

```text
Pod Ready=True
Service exists
DNS resolves
Requests timeout
```

Possible lifecycle/network causes:

```text
Service targetPort wrong
Container listens on different port
NetworkPolicy blocks traffic
App binds to localhost only
Sidecar proxy issue
```

Example bad app config:

```text
server.address=127.0.0.1
```

Inside container:

```text
App listens only on localhost.
Service traffic to Pod IP cannot reach it.
```

Fix:

```text
Bind to 0.0.0.0 or default container interface.
```

Debug:

```bash
kubectl exec -it <pod> -- sh
netstat -tulpn
kubectl describe svc order-service
kubectl get endpoints order-service
```

Lesson:

```text
Ready means probe passed.
It does not guarantee every network path is correct.
```

---

# 43. Debugging Order: Pod Lifecycle Checklist

Follow the lifecycle chain.

```text
1. Was Pod object created?
2. Was Pod scheduled?
3. Was sandbox created?
4. Was image pulled?
5. Were volumes mounted?
6. Did init containers complete?
7. Did app container start?
8. Did startup probe pass?
9. Did readiness probe pass?
10. Did liveness keep passing?
11. Was Pod added to Service endpoints?
12. Did graceful termination work?
```

Commands:

```bash
kubectl get pod <pod> -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl logs <pod> -c <container>
kubectl get events --sort-by=.lastTimestamp
kubectl get endpoints <service>
kubectl get endpointslice
kubectl top pod <pod>
```

Mindset:

```text
Do not jump to logs first always.
Read Events first when Pod did not start.
Read logs when app started and failed.
Read endpoints when traffic fails.
```

---

# 44. Common Status Reasons

```text
Pending:
  Pod waiting for schedule or preparation.

ContainerCreating:
  Kubelet preparing sandbox/image/volumes/network.

ErrImagePull:
  First image pull failed.

ImagePullBackOff:
  Repeated image pull failures with backoff.

CrashLoopBackOff:
  App starts then crashes repeatedly.

RunContainerError:
  Runtime could not start container.

CreateContainerConfigError:
  Bad config, missing Secret/ConfigMap, invalid env reference.

OOMKilled:
  Container exceeded memory limit.

Completed:
  Container exited successfully.

Terminating:
  Pod deletion in progress.

Unknown:
  Node communication problem.
```

ASCII memory hook:

```text
Pending            = not fully born
ContainerCreating  = body being prepared
Running            = process alive
Ready              = can serve traffic
CrashLoopBackOff   = born, dies, born, dies
Terminating        = shutting down
Succeeded/Failed   = lifecycle ended
```

---

# 45. CreateContainerConfigError

This means Kubernetes cannot create the container config.

Common causes:

```text
Secret missing
ConfigMap missing
Invalid env var reference
Volume reference missing
Bad command/args config
```

Example:

```yaml
env:
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: order-db-secret
        key: password
```

If Secret does not exist:

```text
CreateContainerConfigError
```

Debug:

```bash
kubectl describe pod <pod>
kubectl get secret order-db-secret
kubectl get configmap
```

Important:

```text
The image may be correct.
The app may be fine.
The container cannot even be configured.
```

Mental model:

```text
Kubelet cannot build the container start contract.
```

---

# 46. FailedMount

FailedMount means volume setup failed.

Examples:

```text
PVC not bound
Secret volume missing
ConfigMap volume missing
CSI driver issue
Cloud disk attach problem
Permission problem
```

ASCII:

```text
Pod assigned to Node
        |
        v
Kubelet mounts volumes
        |
        v
FailedMount
        |
        v
Container cannot start
```

Debug:

```bash
kubectl describe pod <pod>
kubectl get pvc
kubectl describe pvc <pvc>
kubectl get pv
```

Production example:

```text
App uses mounted config file.
ConfigMap name changed.
Deployment still points to old ConfigMap.
Pods stuck before app starts.
```

Lifecycle lesson:

```text
Volume failures happen before Java code runs.
```

---

# 47. Evicted Pods

A Pod may be evicted when Node is under pressure.

Pressure types:

```text
MemoryPressure
DiskPressure
PIDPressure
Node not enough resources
```

ASCII:

```text
Node under pressure
       |
       v
Kubelet evicts lower priority Pods
       |
       v
Pod status: Evicted
       |
       v
Controller creates replacement elsewhere if needed
```

Debug:

```bash
kubectl describe pod <pod>
kubectl describe node <node>
kubectl get events
kubectl top node
```

Production causes:

```text
No resource requests/limits
Too many logs filling disk
Memory leaks
Image garbage collection issue
No node autoscaling
```

Lesson:

```text
Pod lifecycle depends on Node health.
```

A healthy app can still die because the Node is unhealthy.

---

# 48. Unknown Pod State

Unknown means Kubernetes cannot determine Pod status, often because Node communication is broken.

```text
API Server cannot get fresh status from kubelet.
```

ASCII:

```text
API Server
   |
   X cannot reach node/kubelet
   |
Node with Pods
```

Possible causes:

```text
Node crashed
Network partition
Kubelet stopped
Node overloaded
Control plane communication issue
```

Debug:

```bash
kubectl get nodes
kubectl describe node <node>
kubectl get pod <pod> -o wide
```

Mental model:

```text
Unknown is not an app state.
It is an observation problem.
```

Kubernetes does not know the truth because the Node stopped reporting.

---

# 49. Pod Lifecycle And Resource Requests

Requests influence scheduling.

Limits influence runtime enforcement.

```yaml
resources:
  requests:
    cpu: "500m"
    memory: "512Mi"
  limits:
    cpu: "1000m"
    memory: "1024Mi"
```

Scheduling:

```text
Scheduler checks requests.
```

Runtime:

```text
Kubelet/container runtime enforces limits.
```

ASCII:

```text
requests -> Can this Pod fit on a Node?
limits   -> How much can it use at runtime?
```

Wrong request too high:

```text
Pod stuck Pending.
```

Wrong memory limit too low:

```text
Pod starts then OOMKilled.
```

Wrong CPU limit too low:

```text
App throttled.
Latency increases.
Readiness may fail.
```

For Java:

```text
Memory limit must account for total JVM memory, not just heap.
```

---

# 50. Pod Lifecycle And Stateful Apps

For stateless services:

```text
Pod is disposable.
Replacement is normal.
```

For stateful apps:

```text
Identity and storage matter.
```

StatefulSet gives:

```text
Stable Pod names
Stable network identity
Stable PVC mapping
Ordered startup/shutdown
```

Example:

```text
redis-0
redis-1
redis-2
```

ASCII:

```text
StatefulSet
   |
   +-- redis-0 -> pvc-redis-0
   +-- redis-1 -> pvc-redis-1
   +-- redis-2 -> pvc-redis-2
```

Lifecycle difference:

```text
Deployment Pod:
  replace freely

StatefulSet Pod:
  replace with same identity
```

Do not run databases as simple Deployments unless you truly understand storage, identity, and failure behavior.

---

# 51. Pod Lifecycle And Observability

A production Pod lifecycle should be observable.

Collect:

```text
Pod phase
Container restarts
Probe failures
OOMKilled events
CPU/memory usage
Readiness transitions
Termination duration
Image pull latency
Startup duration
```

Useful metrics:

```text
kube_pod_status_phase
kube_pod_container_status_restarts_total
kube_pod_container_status_last_terminated_reason
kube_pod_status_ready
container_memory_working_set_bytes
container_cpu_usage_seconds_total
```

ASCII:

```text
Pod lifecycle event
       |
       v
Kubernetes status/events
       |
       v
Metrics/logs/traces
       |
       v
Alert/debug decision
```

Alert examples:

```text
CrashLoopBackOff > 5 minutes
Restart count increasing
Ready replicas below desired
OOMKilled detected
ImagePullBackOff detected
```

Do not wait for users to report lifecycle failures.

---

# 52. Interview Questions

## What is a Pod lifecycle?

A Pod lifecycle is the journey of a Pod from object creation to scheduling, node preparation, container startup, readiness, serving traffic, termination, and removal. It includes both control plane actions and kubelet actions on the Node.

## What is the difference between Pod phase and container state?

Pod phase is a broad summary such as Pending, Running, Succeeded, Failed, or Unknown. Container state is more specific and shows whether a container is Waiting, Running, or Terminated, including reasons such as CrashLoopBackOff, ImagePullBackOff, or OOMKilled.

## What happens after a Pod is created?

The Pod is stored through the API Server. The scheduler assigns it to a Node. The kubelet on that Node prepares the sandbox, network, volumes, images, init containers, app containers, probes, and reports status.

## Does the scheduler start containers?

No. The scheduler only chooses a Node and binds the Pod. Kubelet starts containers on the assigned Node.

## What is Pending?

Pending means the Pod has been accepted by Kubernetes but is not fully running yet. It may be unscheduled, waiting for resources, waiting for PVCs, pulling images, running init containers, or preparing volumes/network.

## What is CrashLoopBackOff?

CrashLoopBackOff means the container starts and then exits repeatedly. Kubernetes restarts it with increasing delay. Common causes include bad config, missing secrets, DB startup failure, migration failure, or OOM.

## What is ImagePullBackOff?

ImagePullBackOff means kubelet cannot pull the container image after repeated attempts. Causes include wrong tag, missing image, registry authentication issue, or network problem.

## What is readiness probe?

Readiness probe tells Kubernetes whether the Pod should receive traffic. If readiness fails, the Pod is removed from Service endpoints.

## What is liveness probe?

Liveness probe tells kubelet whether the container should be restarted because it is unhealthy or stuck.

## What is startup probe?

Startup probe gives slow-starting containers time to boot before liveness checks begin. It is useful for Java/Spring Boot apps with long startup.

## What happens during Pod termination?

Kubernetes marks the Pod terminating, removes it from endpoints, kubelet runs preStop if configured, sends SIGTERM, waits for the grace period, then sends SIGKILL if the process does not exit.

## Why is graceful shutdown important?

It lets the application finish in-flight requests, close resources, and avoid cutting business operations like payments or Kafka processing during rollout or scale-down.

---

# 53. Cheat Sheet

```text
Pod lifecycle:
  Created -> Scheduled -> Prepared -> Started -> Ready -> Serving -> Terminating -> Gone

Pod phase:
  Pending
  Running
  Succeeded
  Failed
  Unknown

Container state:
  Waiting
  Running
  Terminated

Important conditions:
  PodScheduled
  Initialized
  ContainersReady
  Ready

Common reasons:
  ContainerCreating
  ErrImagePull
  ImagePullBackOff
  CrashLoopBackOff
  CreateContainerConfigError
  FailedMount
  OOMKilled
  Completed
  Evicted
```

Probe memory:

```text
startupProbe   = give app time to start
readinessProbe = should receive traffic?
livenessProbe  = should restart container?
```

Ownership memory:

```text
Deployment -> ReplicaSet -> Pod -> Container
```

Debug memory:

```text
Events first for startup infrastructure problems.
Logs for application problems.
Endpoints for traffic problems.
Metrics for resource problems.
```

Commands:

```bash
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl logs <pod> -c <container>
kubectl get events --sort-by=.lastTimestamp
kubectl get endpoints <service>
kubectl top pod <pod>
```

---

# 54. One Picture To Remember

```text
                         POD LIFECYCLE

                   +------------------+
                   | Pod object       |
                   | stored in API    |
                   +--------+---------+
                            |
                            v
                   +------------------+
                   | Scheduler        |
                   | choose Node      |
                   +--------+---------+
                            |
                            v
                   +------------------+
                   | Kubelet          |
                   | prepare Pod      |
                   +--------+---------+
                            |
             +--------------+--------------+
             |                             |
             v                             v
      +--------------+              +--------------+
      | Network      |              | Volumes      |
      | Pod IP       |              | Secrets/CM   |
      +------+-------+              +------+-------+
             |                             |
             +--------------+--------------+
                            |
                            v
                   +------------------+
                   | Pull image       |
                   +--------+---------+
                            |
                            v
                   +------------------+
                   | Init containers  |
                   +--------+---------+
                            |
                            v
                   +------------------+
                   | App container    |
                   | Spring Boot      |
                   +--------+---------+
                            |
                            v
                   +------------------+
                   | Startup probe    |
                   +--------+---------+
                            |
                            v
                   +------------------+
                   | Readiness probe  |
                   +--------+---------+
                            |
                            v
                   +------------------+
                   | Service traffic  |
                   +--------+---------+
                            |
                            v
                   +------------------+
                   | Termination      |
                   | SIGTERM + grace  |
                   +------------------+

Rule:

Pod object is not the app.
Running is not Ready.
Ready is not permanent.
Terminating is not instant.
```

---

# 55. Final Production Checklist

```text
[ ] I know who creates Pods in production.
[ ] I know scheduler only chooses Node.
[ ] I know kubelet makes the Pod real.
[ ] I know Pending can mean many different things.
[ ] I know ContainerCreating is infrastructure preparation.
[ ] I know ImagePullBackOff happens before app startup.
[ ] I know CrashLoopBackOff means app starts and crashes repeatedly.
[ ] I know OOMKilled means memory limit was exceeded.
[ ] I know Running does not mean Ready.
[ ] I know readiness controls Service traffic.
[ ] I know liveness restarts containers.
[ ] I know startup probe protects slow boot.
[ ] I know init containers block app startup until success.
[ ] I know termination sends SIGTERM before SIGKILL.
[ ] I know graceful shutdown protects in-flight requests.
[ ] I know Service endpoints require matching labels and Ready=True.
[ ] I can debug Pod lifecycle using describe, logs, events, endpoints, and metrics.
```

---

# 56. Final Memory Hook

Do not memorize Pod lifecycle as five phase names.

Remember it as one production journey:

```text
Kubernetes writes a work order.
Scheduler assigns a machine.
Kubelet prepares the room.
Container runtime starts the process.
Probes decide traffic safety.
Service routes only to ready Pods.
Termination gives the app a chance to leave cleanly.
Controllers replace disposable Pods.
```

Final sentence:

```text
A Pod is not just running or not running; a Pod is continuously being scheduled, prepared, checked, routed, restarted, and gracefully removed by Kubernetes.
```
