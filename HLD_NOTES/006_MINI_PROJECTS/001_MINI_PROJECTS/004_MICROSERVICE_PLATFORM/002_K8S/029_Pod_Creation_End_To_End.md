# 029_Pod_Creation_End_To_End.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Pod Creation Needs An End-To-End Mental Model

Most Kubernetes beginners think Pod creation is simple:

```text
kubectl apply -f pod.yaml
        |
        v
Pod starts
```

That picture is too small.

In real Kubernetes, `kubectl apply` does **not** directly start a container. It sends a request to the API Server. The API Server validates it. The object is stored in etcd. Controllers may create child objects. The scheduler chooses a node. Kubelet on that node notices the assigned Pod. The container runtime pulls the image. Networking and volumes are prepared. Only then does your Java/Spring Boot process start.

The correct mental model is:

```text
Pod Creation = Desired State Written + Many Components Cooperating
```

One picture:

```text
Developer
   |
   | kubectl apply
   v
API Server
   |
   v
etcd
   |
   v
Scheduler
   |
   v
Kubelet
   |
   v
Container Runtime
   |
   v
Running Spring Boot Pod
```

Do not memorize component names. Understand the handoff chain.

Each Kubernetes component answers one question:

```text
API Server:        Is this request valid and allowed?
etcd:              What is the stored cluster state?
Scheduler:         Which node should run this Pod?
Kubelet:           What Pods should run on my node?
Container Runtime: How do I create the container?
CNI:               How does this Pod get networking?
CSI/Volume Plugin: How does this Pod get storage?
Service:           Should traffic reach this Pod?
```

If you understand this chain, debugging becomes natural.

---

# 2. The Wrong Way To Think About Pod Creation

Wrong model:

```text
kubectl creates Pod
```

This sounds like Docker:

```bash
docker run order-service:1.0.0
```

Docker directly asks the local machine to create a container.

Kubernetes is different.

```text
kubectl does not talk to worker node directly.
kubectl does not call containerd directly.
kubectl does not choose node directly.
kubectl does not pull image directly.
```

Correct model:

```text
kubectl writes desired state into Kubernetes.
Kubernetes components make reality match that state.
```

ASCII:

```text
Wrong Docker-like Thinking

kubectl
  |
  | start container
  v
Node
  |
  v
Container

Correct Kubernetes Thinking

kubectl
  |
  | create API object
  v
API Server
  |
  v
Stored State
  |
  v
Scheduler + Kubelet + Runtime
  |
  v
Container
```

Why this matters:

```text
If Pod is Pending, kubectl did not fail.
If image pull fails, API Server may still be fine.
If app crashes, scheduler may still be fine.
If Service has no endpoints, Pod creation may still be fine.
```

Production debugging means identifying which handoff failed.

---

# 3. Real World Analogy: Airport Passenger Journey

Think of Pod creation like a passenger taking a flight.

```text
Passenger wants to travel
       |
       v
Ticket booking
       |
       v
Security check
       |
       v
Gate assignment
       |
       v
Boarding
       |
       v
Flight starts
```

Kubernetes equivalent:

```text
Developer wants Pod
       |
       v
API Server request
       |
       v
Admission/security checks
       |
       v
Scheduler assigns node
       |
       v
Kubelet prepares Pod
       |
       v
Container starts
```

If the passenger is stuck, you ask:

```text
Ticket problem?
Security problem?
No gate assigned?
Boarding failed?
Aircraft problem?
```

If a Pod is stuck, ask:

```text
API validation problem?
RBAC/admission problem?
Scheduling problem?
Image pull problem?
Volume mount problem?
App startup problem?
Readiness problem?
```

ASCII:

```text
Airport Journey                  Kubernetes Journey

Book ticket                      kubectl apply
Security check                   API validation + admission
Gate assignment                  scheduler binds node
Boarding                         kubelet sees assigned Pod
Aircraft preparation             runtime + network + volumes
Flight                           application running
```

This analogy helps you avoid random guessing.

A Pod is not born in one step. It travels through a pipeline.

---

# 4. Pod Creation Big Picture

The end-to-end flow:

```text
1. User submits YAML
2. API Server validates request
3. Admission controllers mutate/validate
4. API Server stores object in etcd
5. Scheduler watches unscheduled Pods
6. Scheduler selects a node
7. Scheduler writes binding to API Server
8. Kubelet watches Pods assigned to its node
9. Kubelet prepares sandbox
10. CNI creates networking
11. Volumes are mounted
12. Image is pulled
13. Containers are created and started
14. Probes begin
15. Pod becomes Running/Ready
16. Service endpoints update
17. Traffic reaches Pod
```

ASCII:

```text
+------------+
| Developer  |
+-----+------+
      |
      v
+-------------+
| kubectl     |
+-----+-------+
      |
      v
+-------------+        +-------------+
| API Server  +------->| etcd        |
+-----+-------+        +-------------+
      ^
      |
+-----+-------+
| Scheduler   |
+-----+-------+
      |
      v
+-------------+
| Node chosen |
+-----+-------+
      |
      v
+-------------+        +-------------------+
| Kubelet     +------->| Container Runtime |
+-----+-------+        +-------------------+
      |
      v
+-------------+
| Pod Running |
+-------------+
```

Important:

```text
Every arrow is a possible failure point.
```

That is why production Kubernetes debugging is mostly lifecycle debugging.

---

# 5. Example Spring Boot Application

Imagine a simple Order Service:

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
class OrderController {
    @GetMapping("/orders/health")
    public String health() {
        return "order-service-ok";
    }
}
```

Docker image:

```text
registry.example.com/order-service:1.0.0
```

Pod YAML:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: order-service-pod
  labels:
    app: order-service
spec:
  containers:
    - name: order-service
      image: registry.example.com/order-service:1.0.0
      ports:
        - containerPort: 8080
```

When you apply this YAML, Kubernetes does not run Java immediately.

It first creates a Pod object:

```text
Pod object exists in Kubernetes memory.
Container may not exist yet.
Java process may not exist yet.
```

Mental separation:

```text
Pod Object     = API record in etcd
Pod Sandbox    = runtime environment on node
Container      = Linux process in namespaces/cgroups
Spring Boot    = Java process inside container
```

Diagram:

```text
Kubernetes API Object
        |
        v
Node Runtime Sandbox
        |
        v
Container Process
        |
        v
Java Spring Boot Application
```

This separation prevents confusion.

---

# 6. Step 1: kubectl Sends Request

When you run:

```bash
kubectl apply -f pod.yaml
```

kubectl does not create containers.

It does:

```text
1. Reads local kubeconfig
2. Finds API Server address
3. Authenticates as your user/service account
4. Sends HTTPS request to API Server
5. Includes YAML converted into JSON API object
```

ASCII:

```text
Laptop
+------------------+
| kubectl          |
| kubeconfig       |
+--------+---------+
         |
         | HTTPS request
         v
Cluster
+------------------+
| API Server       |
+------------------+
```

Typical request path:

```text
POST /api/v1/namespaces/default/pods
```

or for apply:

```text
PATCH /api/v1/namespaces/default/pods/order-service-pod
```

Important mental model:

```text
kubectl is only a client.
API Server is the real front door.
```

Production failure example:

```text
Error: Unable to connect to the server
```

This means request did not even reach the Kubernetes API correctly.

Debug:

```bash
kubectl cluster-info
kubectl config current-context
kubectl config get-contexts
kubectl auth can-i create pods
```

Do not debug kubelet if kubectl cannot even reach API Server.

---

# 7. Step 2: Authentication And Authorization

Before Kubernetes accepts the Pod, it asks:

```text
Who are you?
Are you allowed to create this Pod?
```

Authentication answers:

```text
Who is making the request?
```

Authorization answers:

```text
What can this identity do?
```

Flow:

```text
kubectl request
      |
      v
API Server
      |
      +--> Authentication
      |
      +--> Authorization / RBAC
      |
      v
Allowed or rejected
```

RBAC example:

```text
User: mohamed
Verb: create
Resource: pods
Namespace: default
Decision: allowed/denied
```

If denied:

```text
Error from server (Forbidden): pods is forbidden
```

Debug:

```bash
kubectl auth can-i create pods
kubectl auth can-i create pods -n default
kubectl auth can-i create pods --as system:serviceaccount:default:app-sa
```

Mental model:

```text
A valid YAML can still be rejected by security.
```

Production story:

A CI/CD pipeline deploys successfully in dev but fails in prod:

```text
Forbidden: cannot create pods in namespace prod
```

Root cause:

```text
ServiceAccount has RoleBinding only in dev namespace.
```

Fix mindset:

```text
Do not make cluster-admin as quick fix.
Grant minimum verbs/resources/namespaces required.
```

---

# 8. Step 3: API Validation

After security checks, API Server validates the object shape.

It checks:

```text
Is apiVersion valid?
Is kind valid?
Is metadata.name valid?
Is spec valid?
Are fields allowed for this object?
Are required fields present?
```

Example wrong YAML:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: order-service-pod
spec:
  containerz:
    - name: order-service
      image: order-service:1.0.0
```

Problem:

```text
containerz is not a valid field.
Correct field is containers.
```

Failure:

```text
error validating data: unknown field "containerz"
```

Mental model:

```text
API Server protects Kubernetes state from invalid objects.
```

ASCII:

```text
YAML
 |
 v
Schema validation
 |
 +--> invalid -> reject
 |
 +--> valid   -> continue
```

Debug commands:

```bash
kubectl apply --dry-run=server -f pod.yaml
kubectl explain pod.spec.containers
kubectl explain pod.spec.containers.image
```

Production rule:

```text
Validate before deploy.
Do not let CI/CD discover schema errors in production rollout.
```

---

# 9. Step 4: Admission Controllers

Admission controllers run after authentication/authorization and before storing the object.

They can:

```text
Mutate the object
Validate the object
Reject the object
```

Examples:

```text
Add default resource limits
Inject sidecar container
Reject privileged container
Require image from approved registry
Add labels/annotations
Enforce Pod Security rules
```

ASCII:

```text
API Request
    |
    v
Authentication
    |
    v
Authorization
    |
    v
Admission Controllers
    |
    +--> mutate
    |
    +--> validate
    |
    +--> reject
    v
Store in etcd
```

Example mutation:

```text
Original Pod:
  containers: app

After sidecar injection:
  containers: app, istio-proxy
```

Example validation rejection:

```text
Denied: image tag latest is not allowed
```

Mental model:

```text
The Pod you submit may not be exactly the Pod that gets stored.
```

Production story:

A Spring Boot app worked yesterday. Today it starts failing after service mesh sidecar injection.

Symptoms:

```text
Pod has two containers
App cannot connect to external service
Network path changed through sidecar
```

Debug:

```bash
kubectl get pod order-service-pod -o yaml
kubectl describe pod order-service-pod
kubectl logs order-service-pod -c istio-proxy
kubectl logs order-service-pod -c order-service
```

Do not assume your YAML is the final runtime shape.

---

# 10. Step 5: Object Stored In etcd

If the request passes all checks, API Server stores the Pod object in etcd.

etcd stores the cluster state.

```text
etcd does not run Pod.
etcd does not pull image.
etcd does not start Java.
```

It stores records like:

```text
/api/v1/namespaces/default/pods/order-service-pod
```

ASCII:

```text
API Server
    |
    | write Pod object
    v
+------------------------------+
| etcd                         |
|                              |
| Pod: order-service-pod       |
| spec.nodeName: empty         |
| status.phase: Pending        |
+------------------------------+
```

At this moment:

```text
Pod exists in API
Pod is probably Pending
No node may be assigned yet
No container is running yet
```

You can see it:

```bash
kubectl get pod order-service-pod
kubectl get pod order-service-pod -o yaml
```

Important fields:

```yaml
spec:
  nodeName: null
status:
  phase: Pending
```

Mental model:

```text
Stored object first, runtime execution later.
```

This is why Kubernetes is event-driven. Components watch stored state and react.

---

# 11. Step 6: Scheduler Watches Unscheduled Pods

The scheduler continuously watches the API Server for Pods that do not have a node assigned.

It looks for:

```text
Pod with spec.nodeName empty
```

Then it asks:

```text
Which node is the best place to run this Pod?
```

ASCII:

```text
API Server
   |
   | watch Pods where nodeName is empty
   v
+----------------+
| Scheduler      |
+--------+-------+
         |
         v
Choose node
```

Scheduler does not start containers.

It only writes a decision:

```text
Pod order-service-pod -> node-2
```

Scheduling factors:

```text
CPU requests
Memory requests
Node capacity
Taints and tolerations
Node selectors
Affinity / anti-affinity
Topology spread constraints
Volume constraints
Existing workload placement
```

Example Pod with requests:

```yaml
resources:
  requests:
    cpu: "500m"
    memory: "512Mi"
```

Scheduler interprets this as:

```text
This Pod needs at least 0.5 CPU and 512Mi memory reserved.
```

Mental model:

```text
Scheduler is a placement engine, not a runtime engine.
```

---

# 12. Step 7: Scheduler Filtering And Scoring

Scheduling has two main phases:

```text
Filtering: Which nodes are possible?
Scoring: Which possible node is best?
```

ASCII:

```text
All Nodes
  |
  v
Filter impossible nodes
  |
  v
Candidate Nodes
  |
  v
Score candidates
  |
  v
Best Node
```

Example:

```text
Pod requests:
CPU: 2 cores
Memory: 4Gi

Nodes:
node-1 free CPU 1, Memory 8Gi  -> rejected, CPU too low
node-2 free CPU 4, Memory 6Gi  -> candidate
node-3 tainted gpu=true        -> rejected unless tolerated
```

Result:

```text
node-2 selected
```

If no node fits:

```text
Pod remains Pending
```

Describe output:

```text
0/3 nodes are available: insufficient cpu, node(s) had taint that the pod didn't tolerate
```

Debug:

```bash
kubectl describe pod order-service-pod
kubectl get nodes
kubectl describe node <node-name>
kubectl top nodes
```

Production mindset:

```text
Pending usually means scheduling or volume binding issue.
It does not mean your Java app crashed.
```

---

# 13. Step 8: Binding Pod To Node

After selecting a node, scheduler binds the Pod.

Before binding:

```yaml
spec:
  nodeName: null
```

After binding:

```yaml
spec:
  nodeName: node-2
```

ASCII:

```text
Scheduler
   |
   | bind Pod to node-2
   v
API Server
   |
   v
etcd updated
```

Now the Pod object says:

```text
This Pod belongs to node-2.
```

But the container still may not be running.

Next actor:

```text
Kubelet on node-2
```

Important distinction:

```text
Scheduled != Running
```

Scheduled means:

```text
A node has been selected.
```

Running means:

```text
Containers have started successfully.
```

Debug:

```bash
kubectl get pod order-service-pod -o wide
```

Example:

```text
NAME                READY   STATUS              NODE
order-service-pod   0/1     ContainerCreating   node-2
```

This means scheduler succeeded. Now kubelet/runtime is working.

---

# 14. Step 9: Kubelet Watches Assigned Pods

Kubelet runs on every node.

On node-2, kubelet watches the API Server:

```text
Give me Pods where spec.nodeName = node-2
```

ASCII:

```text
API Server
   |
   | watch assigned Pods
   v
Node-2
+------------------+
| kubelet          |
+------------------+
```

Kubelet sees:

```text
order-service-pod assigned to me
```

Then kubelet starts the local work:

```text
1. Prepare Pod sandbox
2. Setup networking
3. Mount volumes
4. Pull images
5. Create containers
6. Start containers
7. Run probes
8. Report status
```

Kubelet is the node-level executor.

Mental model:

```text
Control plane decides.
Kubelet executes locally.
```

Production failure example:

```text
NodeNotReady
```

If kubelet is unhealthy, Pods assigned to that node may not start or report correctly.

Debug:

```bash
kubectl get nodes
kubectl describe node node-2
journalctl -u kubelet
```

If managed cloud hides node logs, use provider console and Kubernetes events.

---

# 15. Step 10: Pod Sandbox Creation

Before app container starts, Kubernetes creates a Pod sandbox.

A Pod sandbox is the shared environment for containers in the Pod.

It includes:

```text
Network namespace
Pod IP
Shared localhost inside Pod
Base cgroup structure
Pause container / infra container
```

ASCII:

```text
Pod
+--------------------------------+
| Network Namespace              |
| Pod IP: 10.1.2.34              |
|                                |
| +------------+  +------------+ |
| | app        |  | sidecar    | |
| | container  |  | container  | |
| +------------+  +------------+ |
|                                |
| Both share localhost/network   |
+--------------------------------+
```

Why sandbox first?

Because containers in same Pod share networking.

Example:

```text
App container listens on localhost:8080
Sidecar can call localhost:8080
```

Mental model:

```text
Pod is not just one container.
Pod is a shared runtime envelope around containers.
```

If sandbox creation fails:

```text
FailedCreatePodSandBox
```

Common causes:

```text
CNI plugin broken
IP address pool exhausted
Node networking issue
Container runtime problem
```

Debug:

```bash
kubectl describe pod order-service-pod
kubectl get events --sort-by=.lastTimestamp
kubectl describe node node-2
```

---

# 16. Step 11: CNI Networking

Kubernetes delegates Pod networking to CNI plugins.

CNI answers:

```text
How does this Pod get an IP?
How can Pods talk to each other?
How is routing configured?
```

Examples:

```text
Calico
Cilium
Flannel
Weave
Cloud provider CNI
```

ASCII:

```text
Kubelet
  |
  | create Pod network
  v
CNI Plugin
  |
  +--> allocate Pod IP
  +--> attach network interface
  +--> configure routes
  +--> apply network policy
  v
Pod gets IP
```

Pod networking model:

```text
Every Pod gets its own IP.
Pods can usually reach other Pods directly.
Containers inside same Pod share localhost.
```

Example:

```text
order-service Pod IP: 10.1.2.34
payment-service Pod IP: 10.1.5.18
```

Failure symptoms:

```text
ContainerCreating stuck
FailedCreatePodSandBox
Pod gets IP but cannot reach service
DNS fails
NetworkPolicy blocks traffic
```

Debug:

```bash
kubectl get pod -o wide
kubectl describe pod <pod>
kubectl exec -it <pod> -- nslookup kubernetes.default
kubectl exec -it <pod> -- curl http://payment-service:8080
```

Mental model:

```text
A Pod cannot be truly alive for production traffic until networking works.
```

---

# 17. Step 12: Volume Mounting

If the Pod needs volumes, kubelet prepares them before starting containers.

Volume examples:

```text
ConfigMap volume
Secret volume
emptyDir
PersistentVolumeClaim
Projected service account token
CSI volume
```

ASCII:

```text
Pod Startup
   |
   v
Prepare volumes
   |
   +--> ConfigMap mounted
   +--> Secret mounted
   +--> PVC attached/mounted
   +--> Token projected
   v
Start containers
```

Example YAML:

```yaml
volumes:
  - name: app-config
    configMap:
      name: order-service-config
containers:
  - name: order-service
    image: order-service:1.0.0
    volumeMounts:
      - name: app-config
        mountPath: /config
```

Failure symptoms:

```text
ContainerCreating
MountVolume.SetUp failed
Unable to attach or mount volumes
configmap not found
secret not found
PVC pending
```

Debug:

```bash
kubectl describe pod order-service-pod
kubectl get configmap
kubectl get secret
kubectl get pvc
kubectl describe pvc <name>
```

Production story:

App deployment fails only in prod:

```text
MountVolume.SetUp failed for volume "app-config": configmap not found
```

Root cause:

```text
ConfigMap created in staging, not in prod namespace.
```

Mental model:

```text
A Pod can be valid and scheduled but still unable to start because its dependencies are missing.
```

---

# 18. Step 13: Image Pull

Kubelet asks container runtime to pull the image.

```text
registry.example.com/order-service:1.0.0
```

Flow:

```text
Kubelet
  |
  v
Container runtime
  |
  v
Registry authentication
  |
  v
Download image layers
  |
  v
Store image on node
```

ASCII:

```text
Node
+-----------------------+
| kubelet               |
+----------+------------+
           |
           v
+-----------------------+
| containerd / CRI-O    |
+----------+------------+
           |
           v
+-----------------------+
| Image Registry        |
+-----------------------+
```

Image pull policy matters:

```yaml
imagePullPolicy: IfNotPresent
```

Common values:

```text
Always       -> always check registry
IfNotPresent -> use local image if available
Never        -> never pull
```

Failure states:

```text
ErrImagePull
ImagePullBackOff
```

Common causes:

```text
Wrong image name
Wrong tag
Private registry auth missing
ImagePullSecret missing
Registry down
Node cannot reach registry
Architecture mismatch
```

Debug:

```bash
kubectl describe pod order-service-pod
kubectl get secret
kubectl describe secret regcred
```

Mental model:

```text
ImagePullBackOff means Kubernetes accepted the Pod, selected a node, but node cannot get the container image.
```

---

# 19. Step 14: Container Creation

After the image exists locally, runtime creates the container.

Container creation includes:

```text
Set command and args
Set environment variables
Attach mounts
Apply CPU/memory cgroups
Apply security context
Connect to Pod namespace
Prepare logs
```

ASCII:

```text
Image Layers
    |
    v
Runtime creates container
    |
    +--> env vars
    +--> mounts
    +--> cgroups
    +--> security settings
    +--> network namespace
    v
Container ready to start
```

Example env:

```yaml
env:
  - name: SPRING_PROFILES_ACTIVE
    value: prod
  - name: DB_HOST
    valueFrom:
      secretKeyRef:
        name: db-secret
        key: host
```

Failure examples:

```text
CreateContainerConfigError
CreateContainerError
```

Common causes:

```text
Referenced Secret key missing
Referenced ConfigMap key missing
Invalid command
Invalid security context
Read-only filesystem issue
Volume mount path problem
```

Debug:

```bash
kubectl describe pod order-service-pod
kubectl get secret db-secret -o yaml
kubectl get configmap app-config -o yaml
```

Mental model:

```text
Image pulled successfully does not mean container config is valid.
```

---

# 20. Step 15: Container Start And Java Process

Now the runtime starts the container process.

For Spring Boot:

```text
java -jar app.jar
```

Dockerfile example:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/order-service.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Runtime start:

```text
containerd starts process
      |
      v
Linux process begins
      |
      v
JVM starts
      |
      v
Spring Boot starts
      |
      v
Tomcat binds port 8080
```

ASCII:

```text
Container Start
   |
   v
PID 1 inside container
   |
   v
JVM
   |
   v
Spring Boot
   |
   v
Embedded Tomcat
   |
   v
HTTP server ready maybe
```

Important:

```text
Process started does not always mean application is ready.
```

Spring Boot may still be:

```text
Connecting to database
Running migrations
Warming caches
Loading configuration
Registering beans
Starting Kafka consumers
```

Failure state:

```text
CrashLoopBackOff
```

Meaning:

```text
Container starts, exits, Kubernetes retries with backoff.
```

Debug:

```bash
kubectl logs order-service-pod
kubectl logs order-service-pod --previous
kubectl describe pod order-service-pod
```

---

# 21. Step 16: Init Containers

Init containers run before app containers.

They are useful for setup tasks:

```text
Wait for database DNS
Run migration check
Download config
Prepare files
Block app start until dependency exists
```

Example:

```yaml
initContainers:
  - name: wait-for-db
    image: busybox
    command: ['sh', '-c', 'until nc -z postgres 5432; do sleep 2; done']
containers:
  - name: order-service
    image: order-service:1.0.0
```

Flow:

```text
Pod assigned
   |
   v
Init container 1 starts
   |
   v
Init container 1 completes successfully
   |
   v
App container starts
```

ASCII:

```text
Pod Startup
   |
   v
[ init: wait-for-db ] -> must complete
   |
   v
[ app: order-service ] -> starts after init success
```

If init container fails:

```text
Init:CrashLoopBackOff
```

Debug:

```bash
kubectl logs order-service-pod -c wait-for-db
kubectl describe pod order-service-pod
```

Mental model:

```text
Init containers are gates before the main application starts.
```

Production warning:

Do not hide bad architecture with infinite wait loops. If your app cannot tolerate dependency restart, fix application resilience too.

---

# 22. Step 17: Startup Probe

Startup probe answers:

```text
Has the application finished starting?
```

It is useful for slow Spring Boot applications.

Without startup probe, liveness probe may kill the app too early.

Example:

```yaml
startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  failureThreshold: 30
  periodSeconds: 10
```

This gives:

```text
30 * 10 seconds = 300 seconds startup window
```

ASCII:

```text
Container starts
   |
   v
Startup probe running
   |
   +--> failing: keep waiting
   |
   +--> passing: enable liveness/readiness normal checks
```

Spring Boot Actuator config:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

Production story:

A service with many beans and DB migrations takes 90 seconds to start. Liveness probe starts after 30 seconds and kills it repeatedly.

Symptom:

```text
CrashLoopBackOff
Liveness probe failed
```

Fix:

```text
Use startupProbe or increase initialDelaySeconds carefully.
```

Mental model:

```text
Startup probe protects slow boot from premature liveness killing.
```

---

# 23. Step 18: Liveness Probe

Liveness answers:

```text
Should Kubernetes restart this container?
```

It detects deadlocked or broken processes.

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
Kubelet
  |
  | checks liveness
  v
Container
  |
  +--> healthy   -> do nothing
  |
  +--> unhealthy -> restart container
```

Do not put deep dependency checks in liveness.

Bad liveness:

```text
Liveness checks database.
Database temporary issue.
All app Pods restart.
Outage becomes worse.
```

Good liveness:

```text
Is JVM/process alive enough to recover?
```

Spring Boot:

```text
/actuator/health/liveness
```

Production rule:

```text
Liveness is for self-healing process death.
Readiness is for traffic safety.
```

Common bug:

```text
Database down -> liveness DOWN -> Kubernetes restarts all Pods -> thundering herd
```

Better:

```text
Database down -> readiness DOWN -> remove traffic, do not restart unnecessarily
```

---

# 24. Step 19: Readiness Probe

Readiness answers:

```text
Can this Pod receive user traffic now?
```

Example:

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
Pod Running
   |
   v
Readiness check
   |
   +--> fail -> NotReady -> Service does not send traffic
   |
   +--> pass -> Ready    -> Service can send traffic
```

Spring Boot readiness may include:

```text
Database reachable
Required cache initialized
Kafka producer available
Critical dependency healthy
```

But be careful:

```text
Do not make readiness too fragile.
```

If readiness depends on optional service, one optional outage can remove all Pods from traffic.

Debug:

```bash
kubectl get pods
kubectl describe pod order-service-pod
kubectl get endpoints order-service
kubectl logs order-service-pod
```

Important distinction:

```text
Running = process started
Ready   = safe for traffic
```

Production symptom:

```text
Pod shows 0/1 Running
```

Meaning:

```text
Container is running but not ready.
```

Do not confuse it with crash.

---

# 25. Step 20: Status Reporting

Kubelet continuously reports Pod status to API Server.

Status includes:

```text
phase
conditions
container statuses
Pod IP
host IP
start time
restart count
readiness state
```

Example:

```yaml
status:
  phase: Running
  podIP: 10.1.2.34
  conditions:
    - type: PodScheduled
      status: "True"
    - type: Initialized
      status: "True"
    - type: ContainersReady
      status: "True"
    - type: Ready
      status: "True"
```

ASCII:

```text
Kubelet on node-2
      |
      | status update
      v
API Server
      |
      v
etcd stores observed state
```

Mental model:

```text
spec = desired state
status = observed state
```

You write spec.

Kubernetes writes status.

Debug:

```bash
kubectl get pod order-service-pod -o yaml
kubectl get pod order-service-pod -o jsonpath='{.status.conditions}'
```

Production mindset:

```text
When debugging, compare spec and status.
Spec tells what was requested.
Status tells what actually happened.
```

---

# 26. Step 21: Service Endpoint Update

A Pod running alone is not enough for normal production traffic.

Usually traffic reaches it through a Service.

Service selects Pods using labels:

```yaml
selector:
  app: order-service
```

Pod labels:

```yaml
labels:
  app: order-service
```

When Pod becomes Ready, EndpointSlice controller updates endpoints.

ASCII:

```text
Pod labels: app=order-service
Pod Ready: true
        |
        v
Service selector matches
        |
        v
EndpointSlice updated
        |
        v
Traffic can route to Pod
```

If labels do not match:

```text
Pod Running
Pod Ready
Service exists
But no endpoints
```

Debug:

```bash
kubectl get svc order-service
kubectl describe svc order-service
kubectl get endpoints order-service
kubectl get endpointslices
kubectl get pods --show-labels
```

Mental model:

```text
Service does not route to Pods because they exist.
Service routes to Ready Pods that match selector labels.
```

Production story:

Deployment labels:

```yaml
app: order
```

Service selector:

```yaml
app: order-service
```

Result:

```text
No endpoints.
Traffic fails.
```

Root cause:

```text
Identity mismatch, not networking failure.
```

---

# 27. Step 22: Traffic Reaches The Pod

After endpoint update, traffic can flow.

Cluster-internal call:

```text
payment-service -> http://order-service:8080/orders
```

Flow:

```text
Client Pod
   |
   | DNS lookup order-service
   v
CoreDNS
   |
   v
Service ClusterIP
   |
   v
kube-proxy / eBPF routing
   |
   v
Ready Pod IP
   |
   v
Spring Boot container:8080
```

ASCII:

```text
Client Pod
   |
   v
order-service.default.svc.cluster.local
   |
   v
Service VIP
   |
   +--> Ready Pod 10.1.2.34:8080
   +--> Ready Pod 10.1.3.22:8080
```

Possible failures after Pod creation:

```text
DNS failure
Service selector mismatch
NetworkPolicy block
Wrong containerPort/targetPort
App listens on wrong port
Ingress misconfiguration
```

Debug:

```bash
kubectl exec -it client-pod -- nslookup order-service
kubectl exec -it client-pod -- curl http://order-service:8080/orders/health
kubectl describe svc order-service
kubectl get endpoints order-service
```

Mental model:

```text
Pod creation success is not equal to user traffic success.
Traffic needs Service, endpoints, DNS, routing, and app port alignment.
```

---

# 28. End-To-End Dry Run: From YAML To User Traffic

You apply Deployment YAML:

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
1. kubectl sends Deployment to API Server.
2. API Server authenticates user.
3. API Server checks RBAC.
4. API Server validates schema.
5. Admission controllers mutate/validate object.
6. API Server stores Deployment in etcd.
7. Deployment controller creates ReplicaSet.
8. ReplicaSet controller creates Pod objects.
9. Scheduler notices Pods with no node.
10. Scheduler filters and scores nodes.
11. Scheduler binds Pod-1 to node-1 and Pod-2 to node-2.
12. Kubelet on node-1 and node-2 notices assigned Pods.
13. Kubelet creates Pod sandbox.
14. CNI assigns Pod IP and networking.
15. Volumes/config/secrets are mounted.
16. Runtime pulls image.
17. Runtime creates containers.
18. JVM starts.
19. Spring Boot starts Tomcat.
20. Startup/liveness/readiness probes run.
21. Pod becomes Ready.
22. EndpointSlice includes Pod IP.
23. Service routes traffic to Ready Pods.
24. User request reaches Spring Boot controller.
```

One-line memory:

```text
Apply -> Store -> Watch -> Schedule -> Execute -> Probe -> Route
```

ASCII:

```text
YAML
 |
 v
API Server -> etcd
 |
 v
Controllers -> ReplicaSet -> Pods
 |
 v
Scheduler -> Node binding
 |
 v
Kubelet -> Sandbox -> Network -> Volumes -> Image -> Container
 |
 v
Spring Boot -> Probes -> Ready
 |
 v
Service Endpoints -> Traffic
```

---

# 29. Deployment vs Direct Pod Creation

In production, you rarely create naked Pods directly.

You usually create Deployments.

Why?

```text
Naked Pod dies -> no controller recreates it
Deployment Pod dies -> ReplicaSet recreates it
```

Direct Pod:

```text
Pod object
   |
   v
One runtime attempt
```

Deployment:

```text
Deployment
   |
   v
ReplicaSet
   |
   v
Pods
   |
   v
Self-healing replicas
```

ASCII:

```text
Naked Pod

Pod
 |
 v
Container

If deleted:
  gone

Deployment

Deployment
   |
   v
ReplicaSet
   |
   v
Pod A, Pod B, Pod C

If Pod B deleted:
  ReplicaSet creates replacement
```

Production rule:

```text
Use Deployment for stateless Spring Boot services.
Use StatefulSet for identity/storage-sensitive workloads.
Use Job/CronJob for finite tasks.
Use DaemonSet for one Pod per node agents.
```

Mental model:

```text
Pod is runtime unit.
Controller is survival strategy.
```

---

# 30. Production Failure: Pod Stuck Pending

Symptom:

```text
kubectl get pods
order-service-abc   0/1   Pending
```

Meaning:

```text
Pod object exists, but it has not successfully started on a node.
```

Possible causes:

```text
No node has enough CPU/memory
Taints not tolerated
Node selector mismatch
Affinity rules impossible
PVC not bound
Cluster autoscaler delay
Namespace quota exceeded
```

Debug:

```bash
kubectl describe pod order-service-abc
kubectl get nodes
kubectl describe nodes
kubectl get events --sort-by=.lastTimestamp
kubectl get pvc
kubectl describe pvc <pvc-name>
```

Event example:

```text
0/5 nodes are available: 5 Insufficient memory.
```

Mental model:

```text
Pending usually means placement is impossible or waiting.
```

Fix examples:

```text
Reduce resource requests
Add nodes
Fix nodeSelector
Add toleration
Bind PVC
Increase quota
```

Do not check Spring Boot logs first. There may be no container yet.

---

# 31. Production Failure: ContainerCreating Forever

Symptom:

```text
order-service-abc   0/1   ContainerCreating
```

Meaning:

```text
Pod is scheduled, kubelet is preparing runtime, but container has not started.
```

Common causes:

```text
Image pull slow
Volume mount stuck
CNI networking issue
Secret/ConfigMap mount problem
Container runtime problem
```

Debug:

```bash
kubectl describe pod order-service-abc
kubectl get events --sort-by=.lastTimestamp
kubectl get pod order-service-abc -o wide
```

Look for:

```text
FailedMount
FailedCreatePodSandBox
Pulling image
Back-off pulling image
```

ASCII:

```text
Scheduled to node
      |
      v
Kubelet preparation
      |
      +--> network
      +--> volumes
      +--> image
      |
      v
Container start
```

Mental model:

```text
ContainerCreating is kubelet/runtime preparation phase.
```

Do not assume application bug until container actually starts.

---

# 32. Production Failure: ImagePullBackOff

Symptom:

```text
order-service-abc   0/1   ImagePullBackOff
```

Meaning:

```text
Node cannot pull the image.
```

Common causes:

```text
Wrong tag
Image not pushed
Private registry secret missing
Registry credentials expired
Wrong registry URL
Node cannot reach registry
Image architecture mismatch
```

Debug:

```bash
kubectl describe pod order-service-abc
kubectl get secret regcred
kubectl describe secret regcred
```

Event examples:

```text
Failed to pull image "registry.example.com/order-service:1.0.0": not found
Failed to pull image: unauthorized
```

Fix examples:

```text
Push correct image tag
Fix imagePullSecrets
Use immutable tags
Verify CI produced image
Check registry availability
```

Production mindset:

```text
ImagePullBackOff is not Kubernetes scheduler failure.
The Pod reached node-level image retrieval.
```

---

# 33. Production Failure: CreateContainerConfigError

Symptom:

```text
order-service-abc   0/1   CreateContainerConfigError
```

Meaning:

```text
Kubelet cannot create container because configuration references are invalid.
```

Common causes:

```text
Secret missing
Secret key missing
ConfigMap missing
ConfigMap key missing
Invalid env var reference
Volume mount config problem
```

Example YAML:

```yaml
env:
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: db-secret
        key: password
```

If `db-secret` exists but key `password` does not:

```text
CreateContainerConfigError
```

Debug:

```bash
kubectl describe pod order-service-abc
kubectl get secret db-secret -o yaml
kubectl get configmap app-config -o yaml
```

Mental model:

```text
Container image may be fine.
Runtime config assembly failed.
```

Production rule:

```text
Deploy ConfigMaps/Secrets before workloads.
Validate keys, not only object names.
```

---

# 34. Production Failure: CrashLoopBackOff

Symptom:

```text
order-service-abc   0/1   CrashLoopBackOff
```

Meaning:

```text
Container starts, then exits repeatedly.
Kubernetes retries with increasing delay.
```

Common Spring Boot causes:

```text
Missing DB env variable
Wrong database password
Cannot connect to database
Flyway/Liquibase migration failure
Port already used inside container
OutOfMemoryError
Invalid Spring profile
Kafka bootstrap server wrong
Application exception during bean creation
```

Debug:

```bash
kubectl logs order-service-abc
kubectl logs order-service-abc --previous
kubectl describe pod order-service-abc
```

ASCII:

```text
Start container
    |
    v
Spring Boot starts
    |
    v
Exception
    |
    v
Process exits
    |
    v
Kubelet restarts
    |
    v
Backoff delay increases
```

Mental model:

```text
Kubernetes can restart a crashing app.
It cannot fix bad configuration or bad code.
```

Production rule:

```text
For CrashLoopBackOff, logs --previous is often more important than current logs.
```

---

# 35. Production Failure: Running But Not Ready

Symptom:

```text
order-service-abc   0/1   Running
```

Meaning:

```text
Container process is running, but readiness probe is failing.
```

Common causes:

```text
Wrong readiness path
Wrong port
Spring Actuator probes disabled
Database not reachable
App still warming up
Readiness includes unstable dependency
NetworkPolicy blocks dependency
```

Debug:

```bash
kubectl describe pod order-service-abc
kubectl logs order-service-abc
kubectl port-forward pod/order-service-abc 8080:8080
curl localhost:8080/actuator/health/readiness
```

Spring Boot config:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
```

Mental model:

```text
Running means process exists.
Ready means Service can send traffic.
```

Production danger:

```text
All Pods Running but none Ready -> Service has no endpoints -> outage.
```

Check:

```bash
kubectl get endpoints order-service
```

---

# 36. Production Failure: Wrong Port

Spring Boot default port:

```text
8080
```

But YAML says:

```yaml
ports:
  - containerPort: 8081
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8081
```

If app listens on 8080, readiness probe fails.

Symptoms:

```text
Connection refused
Readiness probe failed
Pod 0/1 Running
```

Debug:

```bash
kubectl logs order-service-abc
kubectl describe pod order-service-abc
kubectl exec -it order-service-abc -- netstat -tulpn
```

If netstat missing:

```bash
kubectl exec -it order-service-abc -- sh
```

Then inspect process/env.

Mental model:

```text
containerPort is documentation/metadata for Kubernetes users.
The app must actually listen on the expected port.
```

Service targetPort also matters:

```yaml
ports:
  - port: 80
    targetPort: 8080
```

Traffic path:

```text
Service port 80 -> Pod targetPort 8080 -> Spring Boot listens 8080
```

One mismatch breaks traffic.

---

# 37. Production Failure: Resource Limits And OOMKilled

If memory limit is too low, the container may be killed.

YAML:

```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "500m"
  limits:
    memory: "512Mi"
    cpu: "1"
```

Spring Boot JVM may need more than 512Mi depending on heap, metaspace, threads, buffers, and native memory.

Symptom:

```text
Last State: Terminated
Reason: OOMKilled
Exit Code: 137
```

Debug:

```bash
kubectl describe pod order-service-abc
kubectl top pod order-service-abc
kubectl logs order-service-abc --previous
```

ASCII:

```text
Memory usage rises
    |
    v
Hits cgroup limit
    |
    v
Kernel kills process
    |
    v
Kubelet reports OOMKilled
    |
    v
Container restarts
```

Spring Boot JVM tip:

```text
Use container-aware JVM settings.
Set MaxRAMPercentage carefully.
```

Example:

```yaml
env:
  - name: JAVA_TOOL_OPTIONS
    value: "-XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"
```

Mental model:

```text
Kubernetes memory limit is a hard wall.
The JVM must live inside it.
```

---

# 38. Debugging Mindset: Follow The Lifecycle

Do not debug randomly.

Use this chain:

```text
1. Did YAML reach API Server?
2. Was request allowed?
3. Was object stored?
4. Did controller create Pods?
5. Did scheduler assign node?
6. Did kubelet see the Pod?
7. Did sandbox/network succeed?
8. Did volumes mount?
9. Did image pull?
10. Did container config assemble?
11. Did process start?
12. Did probes pass?
13. Did Service endpoints update?
14. Did traffic reach app?
```

Command ladder:

```bash
kubectl apply --dry-run=server -f app.yaml
kubectl get deploy,rs,pods
kubectl describe deploy order-service
kubectl describe rs <rs-name>
kubectl get pods -o wide
kubectl describe pod <pod-name>
kubectl logs <pod-name>
kubectl logs <pod-name> --previous
kubectl get svc,endpoints,endpointslices
kubectl describe svc order-service
kubectl exec -it <client-pod> -- curl http://order-service:8080/orders/health
```

ASCII:

```text
API Layer
   |
Controller Layer
   |
Scheduler Layer
   |
Node/Kubelet Layer
   |
Runtime Layer
   |
Application Layer
   |
Networking/Service Layer
```

Production rule:

```text
The status word tells the layer.
Pending              -> scheduler/volume/quota
ContainerCreating    -> kubelet/network/volume/image preparation
ImagePullBackOff     -> registry/image/auth
CreateConfigError    -> secret/config/env/mount reference
CrashLoopBackOff     -> app starts then exits
Running 0/1          -> readiness
Running 1/1          -> app ready, debug Service/Ingress if traffic fails
```

---

# 39. End-To-End ASCII State Timeline

```text
Time 0
Developer applies YAML

    kubectl apply
          |
          v
    API Server receives request

Time 1
Security + validation

    AuthN -> AuthZ -> Admission -> Schema
          |
          v
    Object accepted

Time 2
Stored desired state

    etcd
     |
     v
    Pod object: Pending, no node

Time 3
Scheduling

    Scheduler watches unscheduled Pod
          |
          v
    node-2 selected
          |
          v
    spec.nodeName = node-2

Time 4
Node execution

    Kubelet on node-2 sees Pod
          |
          v
    Sandbox + CNI + Volumes + Image
          |
          v
    Container starts

Time 5
Application health

    JVM starts
      |
      v
    Spring Boot starts
      |
      v
    Probes pass
      |
      v
    Ready = True

Time 6
Traffic

    EndpointSlice updated
      |
      v
    Service routes traffic
      |
      v
    User request reaches app
```

One memory hook:

```text
Object first. Node later. Container later. Traffic last.
```

---

# 40. Mini Production Story: New Version Never Receives Traffic

Scenario:

A team deploys `order-service:2.0.0`.

Pods show:

```text
order-service-xyz   0/1 Running
```

The team says:

```text
Kubernetes is broken. Pod is running but traffic not going there.
```

Actual issue:

```text
Readiness probe path changed.
Old app exposed /actuator/health/readiness.
New app exposes /health/ready.
Kubernetes still checks old path.
```

Events:

```text
Readiness probe failed: HTTP probe failed with statuscode: 404
```

Service endpoints:

```text
No endpoint for new Pod
```

Fix:

```text
Update readinessProbe path or restore Actuator endpoint.
```

Lesson:

```text
Kubernetes did the correct thing.
It refused to send traffic to a Pod that failed readiness.
```

Mental model:

```text
A Pod can be alive but not trusted.
Readiness is Kubernetes trust signal for traffic.
```

---

# 41. Mini Production Story: CI/CD Deploys Image That Nodes Cannot Pull

Scenario:

CI builds image:

```text
registry.example.com/order-service:latest
```

Deployment uses:

```yaml
image: registry.example.com/order-service:1.2.0
```

Pod status:

```text
ImagePullBackOff
```

Events:

```text
Failed to pull image: manifest unknown
```

Root cause:

```text
CI pushed latest, deployment requested 1.2.0.
```

Better practice:

```text
Use immutable image tags.
Use Git SHA tags.
Make CI update deployment with exact pushed tag.
Avoid latest in production.
```

ASCII:

```text
CI Pipeline
   |
   | pushed :latest
   v
Registry

Kubernetes
   |
   | pulls :1.2.0
   v
Not found
```

Lesson:

```text
ImagePullBackOff often starts outside Kubernetes, in CI/CD and registry discipline.
```

---

# 42. Mini Production Story: All Pods Restart During Database Blip

Scenario:

Spring Boot liveness endpoint checks database.

Temporary DB issue:

```text
DB unavailable for 30 seconds
```

Kubernetes sees:

```text
Liveness failed
```

Action:

```text
Restart all containers
```

Result:

```text
More load on DB
Cold start storm
Kafka consumers rebalance
Cache warmups spike
Outage becomes worse
```

Correct design:

```text
Liveness: Is JVM alive?
Readiness: Can app serve traffic safely?
```

ASCII:

```text
DB blip
  |
  v
Bad liveness fails
  |
  v
Kubernetes restarts Pods
  |
  v
System becomes more unstable
```

Better:

```text
DB blip
  |
  v
Readiness fails
  |
  v
Pod removed from traffic
  |
  v
No unnecessary restart
```

Lesson:

```text
Wrong probe design converts dependency outage into application restart storm.
```

---

# 43. Java/Spring Boot Production Probe Example

Spring Boot Actuator configuration:

```yaml
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
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

Kubernetes probes:

```yaml
startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  failureThreshold: 30
  periodSeconds: 10

livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  periodSeconds: 10
  failureThreshold: 3

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  periodSeconds: 5
  failureThreshold: 3
```

Mental model:

```text
startupProbe: give app time to boot
livenessProbe: restart truly broken process
readinessProbe: protect users from unready app
```

ASCII:

```text
Spring Boot lifecycle

JVM starting
   |
   v
Startup probe waiting
   |
   v
App booted
   |
   v
Liveness checks process health
   |
   v
Readiness checks traffic safety
   |
   v
Service sends traffic
```

Production tip:

```text
Make probes cheap, stable, and meaningful.
Do not make every dependency part of every probe.
```

---

# 44. Interview Questions

## What happens when you create a Pod in Kubernetes?

The request goes to the API Server, which authenticates, authorizes, validates, and runs admission controllers. If accepted, the Pod object is stored in etcd. The scheduler watches for unscheduled Pods and assigns a node. Kubelet on that node sees the assigned Pod, creates the Pod sandbox, sets up networking and volumes, pulls images, starts containers, runs probes, and reports status back to the API Server.

## Does kubectl create the container?

No. kubectl is only a client. It sends the desired state to the API Server. The kubelet and container runtime on the selected node create and start the container.

## What does the scheduler do during Pod creation?

The scheduler selects a suitable node for a Pod that has no `spec.nodeName`. It filters nodes based on constraints and resource availability, scores candidates, and binds the Pod to the chosen node. It does not start containers.

## What does kubelet do during Pod creation?

Kubelet watches for Pods assigned to its node. It prepares the Pod sandbox, invokes CNI networking, mounts volumes, asks the container runtime to pull images and start containers, runs health probes, and reports status.

## Why can a Pod be Pending?

A Pod can be Pending if no suitable node is available, resources are insufficient, taints are not tolerated, node affinity rules cannot be satisfied, PVCs are unbound, or quotas prevent scheduling.

## What is ImagePullBackOff?

ImagePullBackOff means the Pod was scheduled to a node, but the node cannot pull the required container image. Common causes include wrong image tag, missing registry credentials, nonexistent image, or registry/network issues.

## What is CrashLoopBackOff?

CrashLoopBackOff means the container starts and exits repeatedly. Kubernetes restarts it, but the delay increases. The cause is usually application startup failure, bad config, missing dependency, OOM, or runtime exception.

## What is the difference between Running and Ready?

Running means the container process is running. Ready means Kubernetes considers the Pod safe to receive traffic through a Service. A Pod can be Running but not Ready if readiness checks fail.

## Why is Service not sending traffic to my Pod?

The Pod may not be Ready, labels may not match the Service selector, targetPort may be wrong, EndpointSlice may not include the Pod, or network policies/routing may block traffic.

---

# 45. Cheat Sheet

```text
kubectl              = API client
API Server           = Kubernetes front door
Authentication       = who are you?
Authorization/RBAC   = are you allowed?
Admission Controller = mutate/validate/reject request
etcd                 = stores cluster state
Scheduler            = chooses node
Binding              = writes spec.nodeName
Kubelet              = node agent that runs assigned Pods
Pod Sandbox          = shared Pod runtime envelope
CNI                  = Pod networking plugin
CSI/Volumes          = storage/config/secret mounting
Container Runtime    = pulls image and starts container
Startup Probe        = protects slow boot
Liveness Probe       = restart broken process
Readiness Probe      = allow/block traffic
Service              = stable access to Ready matching Pods
EndpointSlice        = list of ready backend Pod IPs
```

Status meanings:

```text
Pending                   = not scheduled or waiting dependency
ContainerCreating         = kubelet preparing runtime
ErrImagePull              = image pull failed once
ImagePullBackOff          = image pull failing repeatedly
CreateContainerConfigError= bad Secret/ConfigMap/env/volume reference
CrashLoopBackOff          = app starts then crashes repeatedly
Running 0/1               = process running, not ready
Running 1/1               = process running and ready
Completed                 = container finished successfully
OOMKilled                 = memory limit killed container
```

Command ladder:

```bash
kubectl apply --dry-run=server -f app.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl get events --sort-by=.lastTimestamp
kubectl get svc,endpoints,endpointslices
kubectl describe svc <service>
kubectl exec -it <pod> -- curl http://localhost:8080/actuator/health
```

---

# 46. One Picture To Remember

```text
                         DEVELOPER
                            |
                            | kubectl apply
                            v
                     +--------------+
                     | API Server   |
                     | auth + valid |
                     +------+-------+
                            |
                            v
                     +--------------+
                     | etcd         |
                     | stores Pod   |
                     +------+-------+
                            |
                            v
                     +--------------+
                     | Scheduler    |
                     | choose node  |
                     +------+-------+
                            |
                            v
                     +--------------+
                     | API Server   |
                     | bind node    |
                     +------+-------+
                            |
                            v
                     +--------------+
                     | Kubelet      |
                     | node agent   |
                     +------+-------+
                            |
        +-------------------+-------------------+
        |                   |                   |
        v                   v                   v
   +----------+        +----------+        +----------+
   | CNI      |        | Volumes  |        | Runtime  |
   | network  |        | mounts   |        | image    |
   +----+-----+        +----+-----+        +----+-----+
        |                   |                   |
        +-------------------+-------------------+
                            |
                            v
                     +--------------+
                     | Container    |
                     | JVM starts   |
                     +------+-------+
                            |
                            v
                     +--------------+
                     | Probes       |
                     | ready?       |
                     +------+-------+
                            |
                            v
                     +--------------+
                     | Service      |
                     | traffic      |
                     +--------------+
```

Final memory sentence:

```text
A Pod is not created by one command.
A Pod is born through a chain: API acceptance, stored state, scheduling, node execution, runtime startup, health verification, and traffic registration.
```

---

# 47. Final Production Checklist

```text
[ ] I know kubectl only talks to API Server.
[ ] I know API Server validates, authorizes, admits, and stores objects.
[ ] I know etcd stores Pod state but does not run Pods.
[ ] I know scheduler selects a node but does not start containers.
[ ] I know kubelet executes Pods assigned to its node.
[ ] I know CNI gives Pod networking.
[ ] I know volumes/secrets/config must mount before container starts.
[ ] I know image pull happens on the selected node.
[ ] I know container creation can fail due to config references.
[ ] I know Spring Boot process can start and still not be ready.
[ ] I know startup, liveness, and readiness have different jobs.
[ ] I know Service routes only to Ready Pods matching selectors.
[ ] I can debug Pending, ContainerCreating, ImagePullBackOff, CreateContainerConfigError, CrashLoopBackOff, and Running 0/1.
[ ] I can follow the lifecycle instead of guessing randomly.
```

---

# 48. Final Memory Hook

Do not memorize Pod creation as a command.

Remember it as a relay race:

```text
kubectl passes request to API Server.
API Server passes stored state to etcd.
Scheduler passes node decision to API Server.
Kubelet takes responsibility on the node.
Runtime starts containers.
Probes decide trust.
Service sends traffic.
```

One-line formula:

```text
Pod Creation = API Object + Scheduling + Kubelet Execution + Runtime Startup + Readiness + Service Registration
```

If production fails, ask:

```text
Which runner dropped the baton?
```

That question is the real Kubernetes debugging mindset.
