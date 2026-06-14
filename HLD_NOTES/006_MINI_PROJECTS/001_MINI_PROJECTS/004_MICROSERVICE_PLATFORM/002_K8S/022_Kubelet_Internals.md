# 022_Kubelet_Internals.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Kubelet Exists

Most beginners think Kubernetes runs containers from the control plane.

Wrong mental model:

```text
API Server starts container
Scheduler starts container
Controller starts container
```

Correct mental model:

```text
Control Plane decides.
Kubelet executes on the node.
```

Kubelet is the agent running on every worker node. It watches the API Server for Pods assigned to its node, then makes the local machine match the Pod specification.

One picture:

```text
API Server
   |
   | Pod assigned to node-1
   v
Node-1
+-----------------------------------+
| kubelet                           |
|   watches assigned Pods           |
|   talks to container runtime      |
|   mounts volumes                  |
|   runs probes                     |
|   reports status                  |
+-----------------------------------+
```

If Kubernetes is a restaurant chain, the control plane is headquarters. Kubelet is the branch manager inside each restaurant. Headquarters says what must run in that branch. The local manager actually arranges workers, kitchen, equipment, health checks, and reports back.

Do not memorize kubelet as “node agent.” Understand it as:

```text
Kubelet = local reconciler for one node
```

It continuously asks:

```text
What Pods should run on my node?
What is actually running locally?
What must I create, restart, stop, mount, probe, or report?
```

---

# 2. Kubelet Is Not The Scheduler

The scheduler chooses a node. Kubelet runs the Pod on that chosen node.

```text
Pending Pod
   |
   v
Scheduler chooses node-2
   |
   v
API Server stores nodeName = node-2
   |
   v
Kubelet on node-2 notices it
   |
   v
Kubelet starts containers through runtime
```

ASCII:

```text
+-------------+       choose node       +------------+
| Scheduler   | ----------------------> | API Server |
+-------------+                         +-----+------+
                                                |
                                                | Pod.spec.nodeName=node-2
                                                v
                                      +--------------------+
                                      | kubelet on node-2  |
                                      | execute locally    |
                                      +--------------------+
```

Bad interview answer:

```text
Scheduler runs Pods.
```

Good answer:

```text
Scheduler only binds a Pod to a Node. Kubelet on that Node observes the assigned Pod and asks the container runtime to create the sandbox and containers.
```

This distinction matters in debugging.

If Pod is `Pending`, scheduler may be involved.

If Pod is assigned but stuck in `ContainerCreating`, kubelet/runtime/volume/network/image pull may be involved.

---

# 3. Real World Analogy: Hotel Floor Manager

Imagine a hotel.

Head office decides:

```text
Room 301 should host Guest A.
Room 302 should host Guest B.
Room 303 should be cleaned and ready.
```

But head office does not personally open room doors, carry luggage, change bedsheets, check electricity, or inspect bathrooms.

The floor manager does that.

Kubernetes mapping:

```text
Hotel head office       -> Control plane
Room assignment system  -> Scheduler
Floor manager           -> Kubelet
Rooms                   -> Pods
Guests                  -> Containers
Housekeeping checks     -> Probes
Building utilities      -> CPU, memory, disk, network
```

Diagram:

```text
Control Plane says:
"Pod order-service-abc belongs to node-1"

Node-1 kubelet says:
"I will prepare the room, pull image, mount volume,
 create network, start Java process, check health,
 and report status."
```

Mental hook:

```text
Kubelet is not global brain.
Kubelet is local execution intelligence.
```

---

# 4. The Core Kubelet Picture

```text
                         API SERVER
                             |
                             | watch Pods assigned to this node
                             v
+----------------------------------------------------------------+
| NODE                                                           |
|                                                                |
|  +----------------------+                                      |
|  | kubelet              |                                      |
|  |                      |                                      |
|  | desired pod specs    |                                      |
|  | actual runtime state |                                      |
|  | reconcile loop       |                                      |
|  +----------+-----------+                                      |
|             |                                                  |
|             | CRI calls                                        |
|             v                                                  |
|  +----------------------+                                      |
|  | Container Runtime    |  containerd / CRI-O                 |
|  +----------+-----------+                                      |
|             |                                                  |
|             v                                                  |
|  +----------------------+                                      |
|  | Containers           |  Spring Boot JVM, sidecars, etc.    |
|  +----------------------+                                      |
|                                                                |
+----------------------------------------------------------------+
```

Kubelet has two worlds in its head:

```text
Desired world:
PodSpec from API Server

Actual world:
What exists on this node right now
```

Its job:

```text
Make actual local node state match desired PodSpec.
```

---

# 5. Kubelet As A Reconcile Loop

Kubelet is also a controller-like loop, but scoped to one node.

Pseudo-code:

```text
while true:
    desiredPods = pods_assigned_to_my_node()
    actualPods  = inspect_container_runtime()

    for pod in desiredPods:
        if pod_not_running_locally:
            create_pod_sandbox()
            pull_images()
            start_containers()
            run_probes()

    for localPod in actualPods:
        if localPod_no_longer_desired:
            stop_containers()
            cleanup_resources()

    report_status_to_api_server()
```

ASCII:

```text
+--------------------+
| Watch API Server   |
+---------+----------+
          |
          v
+--------------------+
| Compare Desired    |
| vs Local Runtime   |
+---------+----------+
          |
          v
+--------------------+
| Act Locally        |
| start/stop/probe   |
+---------+----------+
          |
          v
+--------------------+
| Report Status      |
+--------------------+
```

This is why kubelet is critical. If kubelet is unhealthy, the node may still have containers running, but Kubernetes control plane cannot reliably observe or manage them.

---

# 6. Pod Lifecycle From Kubelet View

A Deployment creates a ReplicaSet. ReplicaSet creates Pod objects. Scheduler assigns those Pods. Then kubelet performs local execution.

```text
Deployment
   |
   v
ReplicaSet
   |
   v
Pod object created
   |
   v
Scheduler assigns node
   |
   v
Kubelet executes Pod
```

Kubelet dry run:

```text
1. Notice Pod assigned to my node.
2. Create Pod directory under kubelet state.
3. Prepare secrets/configmaps.
4. Mount volumes.
5. Ask runtime to create Pod sandbox.
6. Configure network through CNI.
7. Pull container images.
8. Start init containers if any.
9. Start app containers.
10. Run startup, readiness, liveness probes.
11. Update Pod status.
12. Keep monitoring.
```

Picture:

```text
Assigned PodSpec
      |
      v
Kubelet syncPod()
      |
      +--> volumes
      +--> secrets/configmaps
      +--> sandbox
      +--> network
      +--> image pull
      +--> containers
      +--> probes
      +--> status
```

This is the real execution path behind `kubectl apply`.

---

# 7. Pod Sandbox Mental Model

Before the app container starts, Kubernetes creates a Pod sandbox.

Think of the sandbox as the shared environment for containers inside one Pod.

```text
Pod
+------------------------------------------------+
| Shared network namespace                       |
| Shared localhost                               |
| Shared volumes                                 |
|                                                |
|  +----------------+    +----------------+      |
|  | app container  |    | sidecar        |      |
|  | Spring Boot    |    | log agent      |      |
|  +----------------+    +----------------+      |
+------------------------------------------------+
```

The sandbox is why containers in the same Pod can communicate using localhost.

```text
Spring Boot app -> localhost:15001 -> service mesh sidecar
```

Kubelet asks container runtime:

```text
RunPodSandbox
CreateContainer
StartContainer
```

Mental model:

```text
Pod is not just one container.
Pod is a small execution apartment.
Sandbox prepares the apartment.
Containers are residents.
```

---

# 8. CRI: How Kubelet Talks To Runtime

Kubelet does not directly manage Linux containers itself.

It talks to a container runtime using CRI: Container Runtime Interface.

```text
kubelet
   |
   | CRI gRPC
   v
containerd / CRI-O
   |
   v
runc / low-level runtime
   |
   v
Linux namespaces + cgroups
```

ASCII:

```text
+-----------+      CRI       +-------------+      OCI      +------+
| kubelet   | -------------> | containerd  | ------------> | runc |
+-----------+                +-------------+               +--+---+
                                                               |
                                                               v
                                                     Linux kernel features
```

Important distinction:

```text
Kubelet = Kubernetes node brain
containerd = container lifecycle manager
runc = low-level container creator
kernel = actual isolation/resource enforcement
```

If image pull fails, containerd may show details.

If cgroup limit kills JVM, kernel OOM events matter.

If kubelet reports `ContainerCreating`, it may be waiting on runtime, network, image, or volume.

---

# 9. Container Runtime Flow

When kubelet starts a Pod, it delegates runtime operations.

```text
Kubelet request:
Start this Pod with image order-service:1.0.0
```

Runtime actions:

```text
1. Pull image layers.
2. Unpack image snapshot.
3. Create container filesystem.
4. Configure namespaces.
5. Apply cgroup limits.
6. Start process.
```

Diagram:

```text
PodSpec image: order-service:1.0.0
          |
          v
Kubelet
          |
          v
containerd
          |
          +--> pull image
          +--> create rootfs
          +--> setup runtime spec
          +--> call runc
          |
          v
java -jar app.jar
```

Kubelet remains responsible for observing and reporting, but the runtime performs container operations.

This is why debugging often crosses layers.

```text
kubectl describe pod
journalctl -u kubelet
crictl ps
crictl logs
crictl inspect
```

---

# 10. Kubelet And CNI Networking

Kubelet coordinates Pod networking, but the actual network setup is done by CNI plugins.

```text
Kubelet creates Pod sandbox
      |
      v
Runtime asks CNI plugin to setup network
      |
      v
Pod gets IP
      |
      v
Kubelet reports PodIP to API Server
```

ASCII:

```text
+---------+        +-------------+        +------------+
| kubelet | -----> | containerd  | -----> | CNI plugin |
+---------+        +-------------+        +-----+------+
                                               |
                                               v
                                      Pod network interface
                                      Pod IP assigned
```

If CNI is broken, you may see:

```text
ContainerCreating
FailedCreatePodSandBox
network plugin not ready
cni config uninitialized
```

Production debugging:

```bash
kubectl describe pod <pod>
kubectl get pods -n kube-system
journalctl -u kubelet
ls /etc/cni/net.d/
```

Mental model:

```text
Kubelet asks for Pod network.
CNI builds the network plumbing.
```

---

# 11. Kubelet And Volumes

Before starting containers, kubelet prepares volumes.

```text
PodSpec
  volumes:
    configMap
    secret
    persistentVolumeClaim
```

Kubelet must ensure the container sees those files or mounts.

Flow:

```text
Pod assigned
   |
   v
Kubelet prepares volume manager
   |
   +--> mount ConfigMap files
   +--> mount Secret files
   +--> attach/mount PVC if needed
   +--> prepare emptyDir
   |
   v
Container starts with mounted paths
```

ASCII:

```text
+-------------------+
| PodSpec           |
| volumeMounts      |
+---------+---------+
          |
          v
+-------------------+
| kubelet           |
| volume manager    |
+---------+---------+
          |
          v
+-------------------+
| container FS      |
| /app/config       |
| /var/data         |
+-------------------+
```

If volume mount fails, container may never start.

Symptoms:

```text
ContainerCreating
MountVolume.SetUp failed
Unable to attach or mount volumes
Secret not found
ConfigMap not found
```

---

# 12. Spring Boot ConfigMap Example

Spring Boot app expects:

```text
/app/config/application.yml
```

ConfigMap:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: order-config
data:
  application.yml: |
    server:
      port: 8080
    spring:
      datasource:
        url: jdbc:postgresql://postgres:5432/orders
```

Deployment mount:

```yaml
volumeMounts:
  - name: config
    mountPath: /app/config
volumes:
  - name: config
    configMap:
      name: order-config
```

Kubelet responsibility:

```text
Before starting Java process:
- fetch ConfigMap data through API Server cache path
- prepare projected files on node
- mount them into container path
```

Dry run:

```text
Pod scheduled to node-1
   |
   v
kubelet sees configMap volume
   |
   v
kubelet prepares /app/config/application.yml
   |
   v
Spring Boot starts and reads config
```

If ConfigMap name is wrong:

```text
Pod exists but container waits.
Kubelet cannot prepare required volume.
```

---

# 13. Kubelet And Secrets

Secrets are also mounted or exposed as environment variables by kubelet.

Example:

```yaml
env:
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: order-db-secret
        key: password
```

Kubelet prepares the container environment before start.

```text
PodSpec references Secret
       |
       v
Kubelet resolves Secret
       |
       v
Container starts with DB_PASSWORD
       |
       v
Spring Boot connects DB
```

Common failure:

```text
secret "order-db-secret" not found
```

Debug:

```bash
kubectl describe pod order-service-abc
kubectl get secret order-db-secret
```

Mental model:

```text
Kubelet is the last-mile delivery agent for config and secrets.
API Server stores references.
Kubelet makes them visible inside the container.
```

---

# 14. Init Containers

Init containers run before app containers.

Use cases:

```text
Wait for database
Run schema preparation
Download config
Check dependency availability
```

Kubelet order:

```text
init-1 starts
init-1 completes
init-2 starts
init-2 completes
app container starts
```

ASCII:

```text
Pod Start
   |
   v
+-------------+
| init-db     |
+------+------+ 
       |
       v
+-------------+
| init-cache  |
+------+------+ 
       |
       v
+-------------+
| app         |
| Spring Boot |
+-------------+
```

If an init container fails, app containers do not start.

Production symptom:

```text
Init:CrashLoopBackOff
```

Kubelet keeps retrying according to restart policy.

Do not blame Spring Boot app before checking init containers.

---

# 15. Startup Probe Mental Model

Startup probe answers:

```text
Has the application finished starting?
```

This is useful for slow JVM apps.

Without startup probe, liveness may kill the app too early.

```text
Spring Boot cold start:
- class loading
- dependency injection
- Hibernate init
- DB pool setup
- cache warmup
```

Diagram:

```text
Container started
   |
   v
Startup probe failing
   |
   v
Kubelet waits, does not apply liveness failure yet
   |
   v
Startup probe passes
   |
   v
Now liveness/readiness matter normally
```

YAML:

```yaml
startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  failureThreshold: 30
  periodSeconds: 10
```

Mental model:

```text
Startup probe protects slow boot.
```

---

# 16. Readiness Probe Mental Model

Readiness answers:

```text
Should this Pod receive traffic?
```

Kubelet runs readiness probes and reports readiness in Pod status. Endpoints/EndpointSlice controllers use that status to decide Service backends.

```text
Kubelet readiness check
        |
        v
Pod Ready=True/False
        |
        v
Service endpoints updated
        |
        v
Traffic allowed or blocked
```

Spring Boot Actuator:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
```

Probe:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  periodSeconds: 5
  failureThreshold: 3
```

Production lesson:

```text
Running != Ready
```

A JVM can be alive while DB is down, cache is cold, or migration is running. Kubelet is the component that continuously verifies readiness and reports it.

---

# 17. Liveness Probe Mental Model

Liveness answers:

```text
Is this container stuck beyond recovery?
```

If liveness fails repeatedly, kubelet restarts the container.

```text
Container running
   |
   v
Liveness probe fails N times
   |
   v
Kubelet kills container
   |
   v
Runtime restarts container
```

Good liveness probe:

```text
Checks process is alive and not deadlocked.
```

Bad liveness probe:

```text
Depends on external DB.
```

Why bad?

```text
DB temporarily down
   |
   v
liveness fails
   |
   v
kubelet restarts all app Pods
   |
   v
traffic storm + cold start storm
```

Mental model:

```text
Readiness protects users.
Liveness repairs dead containers.
Startup protects slow boot.
```

---

# 18. Probe Failure Production Story

A Spring Boot order service had this liveness endpoint:

```text
/actuator/health
```

It included database health.

During a short PostgreSQL failover, health returned DOWN.

Kubelet interpreted:

```text
App is dead
```

So kubelet restarted many containers.

Actual problem:

```text
DB was briefly unavailable.
App process was not dead.
```

Better design:

```text
liveness  -> JVM/process internal health
readiness -> dependency readiness
```

Picture:

```text
Postgres failover
      |
      v
Readiness DOWN  -> remove from Service temporarily
Liveness UP     -> do not restart JVM
```

Lesson:

```text
Do not use liveness as dependency monitor.
```

---

# 19. Kubelet And Pod Status

Kubelet reports Pod status back to API Server.

Status includes:

```text
Pod phase
Container states
Ready condition
Pod IP
Restart count
Reason messages
```

Flow:

```text
Runtime state
   |
   v
Kubelet observes
   |
   v
Kubelet updates Pod.status
   |
   v
kubectl get pods shows status
```

ASCII:

```text
containerd says: container exited code 1
        |
        v
kubelet records: Last State = Terminated
        |
        v
API Server stores status
        |
        v
kubectl shows: CrashLoopBackOff
```

Important:

```text
kubectl output is not magic.
It is mostly status reported by kubelet and controllers.
```

If kubelet stops reporting, node becomes NotReady and Pod statuses become stale or unknown.

---

# 20. Node Heartbeats

Kubelet sends heartbeats to API Server.

This tells Kubernetes:

```text
Node is alive.
Node has this capacity.
Node has this pressure condition.
Node can run Pods.
```

Diagram:

```text
Node kubelet
   |
   | heartbeat / lease update
   v
API Server
   |
   v
Node object status
```

If heartbeats stop:

```text
Node Ready becomes Unknown/False
Pods on node may be marked unavailable
Controllers may create replacements elsewhere
```

Production symptoms:

```bash
kubectl get nodes
kubectl describe node <node>
```

You may see:

```text
Ready=False
KubeletNotReady
node is unreachable
```

Mental model:

```text
Kubelet is also the node reporter.
Without it, the control plane is blind about that node.
```

---

# 21. Node Conditions

Kubelet reports node conditions such as:

```text
Ready
MemoryPressure
DiskPressure
PIDPressure
NetworkUnavailable
```

ASCII:

```text
Node resources
+-------------------------+
| CPU                     |
| Memory                  |
| Disk                    |
| PIDs                    |
| Network                 |
+------------+------------+
             |
             v
        kubelet observes
             |
             v
        Node conditions
```

Example:

```text
MemoryPressure=True
```

Meaning:

```text
Node memory is under pressure.
Kubelet may start evicting Pods.
Scheduler may avoid placing new Pods there.
```

Debug:

```bash
kubectl describe node <node>
kubectl top node
kubectl top pod -A
journalctl -u kubelet
```

Mental model:

```text
Node conditions are kubelet's health report about the machine.
```

---

# 22. Requests, Limits, And Kubelet

Scheduler uses requests to place Pods.

Kubelet enforces resource behavior locally with cgroups through runtime/kernel.

```text
resources:
  requests:
    memory: "512Mi"
    cpu: "500m"
  limits:
    memory: "1Gi"
    cpu: "1"
```

Flow:

```text
PodSpec resources
   |
   v
Scheduler uses requests for placement
   |
   v
Kubelet/runtime configures cgroups
   |
   v
Kernel enforces CPU/memory limits
```

ASCII:

```text
Spring Boot JVM
      |
      v
Container
      |
      v
cgroup memory limit = 1Gi
      |
      v
Kernel OOM kills if exceeded
      |
      v
Kubelet reports OOMKilled
```

Important for Java:

```text
Container memory limit must include heap + metaspace + threads + direct buffers + native memory.
```

Do not set JVM heap equal to container limit.

---

# 23. Java OOMKilled Story

Deployment:

```yaml
resources:
  limits:
    memory: "512Mi"
```

Java args:

```text
-Xmx512m
```

Problem:

```text
Heap can use 512Mi.
But JVM also needs metaspace, thread stacks, direct buffers, GC structures.
```

Result:

```text
Container exceeds cgroup memory limit
Kernel kills process
Kubelet reports OOMKilled
```

Debug:

```bash
kubectl describe pod order-service-abc
kubectl logs order-service-abc --previous
kubectl top pod order-service-abc
```

Better:

```text
Container limit: 1024Mi
JVM max heap: 60-70% of limit
Example: -XX:MaxRAMPercentage=65
```

Mental model:

```text
Kubelet reports the death.
Kernel performs the kill.
Bad Java memory sizing causes the crime.
```

---

# 24. Eviction Mental Model

Kubelet protects the node.

If memory/disk/pid pressure is high, kubelet may evict Pods.

```text
Node memory low
   |
   v
Kubelet detects pressure
   |
   v
Choose Pods to evict based on QoS/priority/usage
   |
   v
Pod terminated
   |
   v
Controller may recreate elsewhere
```

QoS classes:

```text
Guaranteed
Burstable
BestEffort
```

Simplified eviction risk:

```text
BestEffort  -> first risk
Burstable   -> medium risk
Guaranteed  -> safest
```

ASCII:

```text
MemoryPressure
     |
     v
+----------------+
| kubelet        |
| eviction logic |
+-------+--------+
        |
        v
Evict lower priority / lower QoS Pods first
```

Production command:

```bash
kubectl describe node <node>
kubectl get events --sort-by=.lastTimestamp
```

---

# 25. Image Pull Internals

Kubelet asks runtime to pull images.

```text
PodSpec image
   |
   v
Kubelet
   |
   v
containerd pulls layers
   |
   v
Image available locally
   |
   v
Container starts
```

Image pull policy:

```text
Always
IfNotPresent
Never
```

Common failures:

```text
ImagePullBackOff
ErrImagePull
Unauthorized
Not found
TLS/certificate issue
Rate limit
```

Debug:

```bash
kubectl describe pod <pod>
crictl images
crictl pull <image>
journalctl -u kubelet
```

Mental model:

```text
A Pod object can exist perfectly while its image cannot be downloaded.
```

For private registry:

```yaml
imagePullSecrets:
  - name: regcred
```

Kubelet uses that secret to authenticate image pulls.

---

# 26. Restart Policy And CrashLoopBackOff

For normal Deployments, restart policy is usually:

```text
Always
```

If the process exits, kubelet restarts it.

CrashLoopBackOff means:

```text
Container starts
Container exits/crashes
Kubelet restarts
It crashes again
Kubelet waits longer between retries
```

ASCII:

```text
Start container
   |
   v
Java app crashes
   |
   v
Kubelet restart
   |
   v
Crash again
   |
   v
Backoff delay grows
```

Common Spring Boot causes:

```text
Wrong DB password
Missing env var
Port binding failure
Migration failure
Invalid profile
OutOfMemoryError
```

Debug:

```bash
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl describe pod <pod>
```

Mental model:

```text
Kubelet can restart your process.
It cannot fix broken application config.
```

---

# 27. Static Pods

Kubelet can also run static Pods from local manifest files.

Common control-plane components in kubeadm clusters are static Pods:

```text
/etc/kubernetes/manifests/kube-apiserver.yaml
/etc/kubernetes/manifests/kube-scheduler.yaml
/etc/kubernetes/manifests/kube-controller-manager.yaml
```

Flow:

```text
Kubelet watches local manifest directory
      |
      v
Starts static Pod
      |
      v
Creates mirror Pod in API Server
```

ASCII:

```text
Local file on node
/etc/kubernetes/manifests/kube-apiserver.yaml
        |
        v
Kubelet
        |
        v
containerd
        |
        v
kube-apiserver static Pod
```

Important:

```text
Static Pods are managed by kubelet, not by Deployment controller.
```

If API Server is down, kubelet can still keep static control-plane Pods running from local files.

---

# 28. Kubelet Configuration

Kubelet itself has configuration.

Important areas:

```text
cluster DNS
container runtime endpoint
cgroup driver
eviction thresholds
authentication/authorization
TLS certificates
node labels
pod manifest path
```

Example concepts:

```text
--container-runtime-endpoint
--kubeconfig
--config
--pod-manifest-path
```

Systemd view:

```bash
systemctl status kubelet
journalctl -u kubelet
```

Mental model:

```text
Kubelet is a long-running system service.
If this service is unhealthy, the node is unhealthy from Kubernetes point of view.
```

Do not debug kubelet only with `kubectl`. Sometimes the truth is in node logs.

---

# 29. Kubelet Security Mental Model

Kubelet has powerful access on the node.

It can:

```text
start containers
mount volumes
read Pod specs
report status
serve logs/exec/proxy endpoints depending on config
```

Therefore kubelet security matters.

Mental model:

```text
Compromised kubelet = compromised node
```

Security areas:

```text
TLS certs
API Server authentication
kubelet authorization
read-only port disabled
anonymous access disabled
Node authorizer
NodeRestriction admission
```

ASCII:

```text
API Server <---- secure TLS/auth ----> Kubelet
```

Production principle:

```text
Do not expose kubelet ports publicly.
Do not allow anonymous kubelet access.
Protect node credentials.
```

---

# 30. Kubelet Logs And Events

Kubelet emits events through API Server and logs locally.

Events appear in:

```bash
kubectl describe pod <pod>
kubectl get events --sort-by=.lastTimestamp
```

Node logs:

```bash
journalctl -u kubelet -f
```

Runtime inspection:

```bash
crictl ps -a
crictl logs <container-id>
crictl inspect <container-id>
```

Debug stack:

```text
kubectl event       -> Kubernetes-level symptom
kubelet log         -> node agent details
runtime log/crictl  -> container runtime details
kernel messages     -> OOM/cgroup/storage details
```

ASCII:

```text
Symptom in kubectl
       |
       v
Kubelet logs
       |
       v
Runtime inspection
       |
       v
Kernel/node diagnosis
```

---

# 31. Debugging Pending vs ContainerCreating vs Running

Status tells you where to start.

```text
Pending
  Pod not scheduled or waiting for resources/constraints.

ContainerCreating
  Scheduled, kubelet working on image/network/volume/sandbox.

Running 0/1
  Container process started, readiness failing or container not ready.

CrashLoopBackOff
  App process crashes repeatedly.

ImagePullBackOff
  Runtime cannot pull image.
```

Decision tree:

```text
Pod Pending?
   -> scheduler/resources/taints/affinity/PVC binding

Pod ContainerCreating?
   -> kubelet/runtime/CNI/volumes/image

Pod Running but not Ready?
   -> readiness/app dependency/probe

Pod CrashLoopBackOff?
   -> app logs/config/memory
```

This is how you avoid random debugging.

---

# 32. Full Dry Run: Spring Boot Pod Start

Deployment says:

```yaml
containers:
  - name: order-service
    image: registry.example.com/order-service:1.0.0
    ports:
      - containerPort: 8080
    readinessProbe:
      httpGet:
        path: /actuator/health/readiness
        port: 8080
```

Dry run:

```text
1. Scheduler binds Pod to node-1.
2. Kubelet on node-1 sees Pod.
3. Kubelet prepares volumes and secrets.
4. Kubelet asks runtime to create Pod sandbox.
5. CNI gives Pod IP.
6. Runtime pulls order-service image.
7. Runtime starts Java process.
8. Spring Boot starts Tomcat.
9. Kubelet calls readiness endpoint.
10. Endpoint returns DOWN while DB pool connects.
11. Pod stays Running but not Ready.
12. DB connects.
13. Readiness returns UP.
14. Kubelet reports Ready=True.
15. Service sends traffic.
```

Picture:

```text
PodSpec -> kubelet -> runtime -> JVM -> probe -> status -> Service traffic
```

---

# 33. Minimal Java Example For Health Probes

Spring Boot controller:

```java
package com.example.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @GetMapping("/orders/ping")
    public String ping() {
        return "order-service alive";
    }
}
```

Application:

```java
package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

Actuator config:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      probes:
        enabled: true
```

Kubernetes probes:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
```

Kubelet is the component that calls these URLs.

---

# 34. Kubelet Does Not Fix Bad Apps

Kubelet can:

```text
start containers
restart crashed containers
mount volumes
run probes
report status
trigger eviction
```

Kubelet cannot:

```text
fix wrong SQL query
fix memory leak
fix wrong DB password
make slow startup fast
make unsafe liveness correct
make a stateful app stateless
```

Diagram:

```text
Bad app config
      |
      v
Kubelet restart loop
      |
      v
CrashLoopBackOff
```

Kubernetes amplifies automation. It does not remove engineering responsibility.

Mental hook:

```text
Kubelet is a disciplined operator, not a miracle healer.
```

---

# 35. Production Story: Node Disk Pressure

A logging sidecar wrote too much data to local disk.

Node condition became:

```text
DiskPressure=True
```

Kubelet started evicting Pods.

Symptoms:

```text
Pods evicted
Node DiskPressure
Application replicas reduced temporarily
```

Debug:

```bash
kubectl describe node <node>
kubectl get events --sort-by=.lastTimestamp
df -h
journalctl -u kubelet
```

Root cause:

```text
Unbounded local logs and no rotation policy.
```

Lesson:

```text
Kubelet protects the node first.
If your Pod abuses node resources, kubelet may evict it.
```

---

# 36. Production Story: CNI Broken After Node Join

A new node joined the cluster. Pods scheduled there stayed in:

```text
ContainerCreating
```

Events showed:

```text
FailedCreatePodSandBox
network plugin not ready
```

Cause:

```text
CNI config missing on the node.
```

Debug path:

```bash
kubectl describe pod <pod>
kubectl get pods -n kube-system -o wide
ssh node
ls /etc/cni/net.d/
journalctl -u kubelet
```

Mental model:

```text
Scheduler only placed the Pod.
Kubelet tried to create sandbox.
CNI failed to create network.
So the container never started.
```

---

# 37. Production Story: Readiness Misconfigured

Readiness probe path:

```yaml
path: /health
```

But Spring Boot exposed:

```text
/actuator/health/readiness
```

Pod status:

```text
Running 0/1
```

Service had no endpoints.

Debug:

```bash
kubectl describe pod order-service-abc
kubectl logs order-service-abc
kubectl exec -it order-service-abc -- curl localhost:8080/health
kubectl exec -it order-service-abc -- curl localhost:8080/actuator/health/readiness
```

Lesson:

```text
Kubelet did exactly what YAML asked.
The YAML asked the wrong endpoint.
```

Do not blame Kubernetes first. Follow the chain.

---

# 38. Debugging Mindset: Layer By Layer

Use this order:

```text
1. Is Pod scheduled?
2. Which node is assigned?
3. Is kubelet healthy on that node?
4. Did volume mount succeed?
5. Did Pod sandbox creation succeed?
6. Did CNI assign network?
7. Did image pull succeed?
8. Did container start?
9. Did startup probe pass?
10. Did readiness probe pass?
11. Did liveness restart container?
12. Did kubelet report status correctly?
```

Commands:

```bash
kubectl get pod <pod> -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl describe node <node>
kubectl get events --sort-by=.lastTimestamp
journalctl -u kubelet
crictl ps -a
crictl logs <container-id>
```

Mindset:

```text
Do not jump from symptom to conclusion.
Find the layer where desired state failed to become local runtime state.
```

---

# 39. Common Beginner Mistakes

```text
Mistake 1:
Thinking scheduler starts containers.
Correct:
Scheduler binds Pod to Node. Kubelet starts containers.

Mistake 2:
Ignoring ContainerCreating.
Correct:
It often means kubelet/runtime/CNI/volume work is stuck.

Mistake 3:
Using DB health as liveness.
Correct:
Put dependencies in readiness, not liveness.

Mistake 4:
Setting JVM Xmx equal to container memory limit.
Correct:
Leave space for non-heap memory.

Mistake 5:
Debugging only with kubectl.
Correct:
Sometimes you need kubelet logs and runtime inspection.

Mistake 6:
Thinking Running means ready for traffic.
Correct:
Readiness controls Service traffic.

Mistake 7:
Forgetting kubelet protects the node.
Correct:
Evictions happen under pressure.
```

---

# 40. Interview Questions

## What is kubelet?

Kubelet is the node agent that runs on every Kubernetes node. It watches the API Server for Pods assigned to its node, starts containers through the container runtime, mounts volumes, runs probes, monitors containers, handles restarts and evictions, and reports Pod and Node status back to the API Server.

## Does kubelet schedule Pods?

No. The scheduler chooses which node should run a Pod. Kubelet on that chosen node observes the assigned Pod and executes it locally.

## How does kubelet start containers?

Kubelet uses the Container Runtime Interface to talk to runtimes like containerd or CRI-O. The runtime pulls images, creates the container filesystem, applies runtime configuration, and starts containers using low-level runtimes such as runc.

## What is a Pod sandbox?

A Pod sandbox is the shared execution environment for containers in a Pod. It provides the shared network namespace and base environment before individual containers are started.

## What does kubelet do with probes?

Kubelet runs startup, readiness, and liveness probes. Startup protects slow boot. Readiness decides whether a Pod should receive traffic. Liveness decides whether kubelet should restart a stuck container.

## Why can a Pod be Running but not Ready?

The container process may be alive, but readiness probe may be failing. For example, Spring Boot started, but DB connection is not ready or the readiness endpoint is misconfigured.

## What causes CrashLoopBackOff?

The container starts and exits repeatedly. Kubelet restarts it according to policy, but increases delay between restarts. Common causes include bad config, missing environment variables, failed DB connection, migration failure, or OOM.

## What causes ImagePullBackOff?

The runtime cannot pull the container image. Causes include wrong tag, missing imagePullSecret, unauthorized registry access, missing image, network issue, or registry outage.

## What does kubelet report to API Server?

Kubelet reports Pod status, container states, readiness conditions, restart counts, Pod IP, Node conditions, capacity, allocatable resources, and heartbeats.

## What are kubelet evictions?

When node resources such as memory, disk, or PIDs are under pressure, kubelet may evict Pods to protect the node. Eviction decisions consider QoS, priority, and resource usage.

---

# 41. Cheat Sheet

```text
Kubelet                 = node-local Kubernetes agent
Main job                = make assigned Pods run locally
Scope                   = one node
Scheduler               = chooses node
Kubelet                 = executes Pod on node
CRI                     = interface between kubelet and runtime
Runtime                 = containerd / CRI-O
Low-level runtime       = runc
CNI                     = Pod network setup
Pod sandbox             = shared Pod environment
Startup probe           = app finished booting?
Readiness probe         = should receive traffic?
Liveness probe          = should restart container?
Node heartbeat          = kubelet tells API Server node is alive
Node conditions         = Ready, MemoryPressure, DiskPressure, PIDPressure
Eviction                = kubelet removes Pods to protect node
CrashLoopBackOff        = app repeatedly crashes
ImagePullBackOff        = image cannot be pulled
ContainerCreating       = kubelet/runtime/network/volume setup in progress or stuck
OOMKilled               = kernel killed container for memory limit breach
```

Core flow:

```text
Pod scheduled to node
        |
        v
Kubelet sees Pod
        |
        v
Prepare volumes/secrets/config
        |
        v
Create Pod sandbox
        |
        v
Setup network via CNI
        |
        v
Pull image via runtime
        |
        v
Start containers
        |
        v
Run probes
        |
        v
Report status
```

---

# 42. One Picture To Remember

```text
                              CONTROL PLANE

+-------------+      bind Pod      +-------------+
| Scheduler   | -----------------> | API Server  |
+-------------+                    +------+------+ 
                                           |
                                           | watch assigned Pods
                                           v

                                  WORKER NODE
+--------------------------------------------------------------------+
|                                                                    |
|  +------------------+                                              |
|  | kubelet          |                                              |
|  | local reconciler |                                              |
|  +--------+---------+                                              |
|           |                                                        |
|           +--> prepare volumes / secrets / config                  |
|           +--> create Pod sandbox                                  |
|           +--> setup network through CNI                           |
|           +--> pull images through runtime                         |
|           +--> start containers                                    |
|           +--> run startup/readiness/liveness probes               |
|           +--> report Pod and Node status                          |
|                                                                    |
|  +------------------+        +------------------+                  |
|  | containerd       | -----> | Spring Boot JVM  |                  |
|  +------------------+        +------------------+                  |
|                                                                    |
+--------------------------------------------------------------------+

Rule:

Scheduler decides where.
Kubelet makes it real there.
```

---

# 43. Final Production Checklist

```text
[ ] I know kubelet runs on every node.
[ ] I know scheduler chooses node but kubelet starts containers.
[ ] I know kubelet talks to containerd/CRI-O through CRI.
[ ] I know Pod sandbox is created before containers.
[ ] I know CNI gives Pod networking.
[ ] I know kubelet prepares ConfigMaps, Secrets, and volumes.
[ ] I know startup, readiness, and liveness have different jobs.
[ ] I know Running does not mean Ready.
[ ] I know CrashLoopBackOff usually needs app logs and previous logs.
[ ] I know ImagePullBackOff needs image/registry/secret debugging.
[ ] I know OOMKilled often means JVM memory sizing is wrong.
[ ] I know kubelet reports Pod status and Node heartbeats.
[ ] I know node pressure can trigger kubelet evictions.
[ ] I know kubelet logs may be needed beyond kubectl.
```

---

# 44. Final Memory Hook

Do not memorize kubelet as just another Kubernetes component.

Remember this:

```text
Kubelet is the worker-node reality engine.
```

The control plane stores intent.

The scheduler chooses placement.

But kubelet turns the PodSpec into real containers, real mounts, real network, real health checks, real status, and real node behavior.

Final sentence:

```text
Kubelet is where Kubernetes desired state touches the Linux machine.
```
