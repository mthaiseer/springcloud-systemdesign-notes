# 003_Cluster_Node_Pod_Model.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Cluster, Node, and Pod Matter

Most Kubernetes confusion starts because people memorize words before understanding the physical picture.

They hear:

```text
Cluster
Node
Pod
Container
Deployment
Service
```

Then they try to remember definitions.

That is the wrong path.

The correct path is to ask:

```text
Where does my Spring Boot app actually run?
Who owns the machine?
Who starts the Java process?
What happens when the machine dies?
What happens when one app instance dies?
```

Kubernetes is not mainly a YAML tool. It is a distributed operating model for running applications across many machines.

The first picture to build in your head is this:

```text
Cluster
  |
  +-- Node
  |     |
  |     +-- Pod
  |           |
  |           +-- Container
  |                 |
  |                 +-- java -jar order-service.jar
  |
  +-- Node
  |     |
  |     +-- Pod
  |
  +-- Node
        |
        +-- Pod
```

If you understand this picture, Kubernetes becomes much easier.

A cluster is the whole production playground.

A node is one machine inside that playground.

A pod is the smallest deployable runtime unit placed on a node.

A container is the actual application process environment inside the pod.

Do not memorize these as dictionary definitions.

Map them to real life.

```text
Cluster = whole hospital
Node    = one building / ward
Pod     = one patient room
Container = actual patient/equipment inside the room
```

Kubernetes manages the hospital, assigns rooms, replaces failed rooms, and keeps the desired number of rooms active.

---

# 2. One Picture Before Any Definition

Before learning individual terms, look at the full runtime stack.

```text
+----------------------------------------------------+
|                    KUBERNETES CLUSTER              |
|                                                    |
|   +----------------------+   +------------------+   |
|   | Node-1               |   | Node-2           |   |
|   |                      |   |                  |   |
|   |  +----------------+  |   | +--------------+ |   |
|   |  | Pod A          |  |   | | Pod C        | |   |
|   |  |                |  |   | |              | |   |
|   |  | Container      |  |   | | Container    | |   |
|   |  | Spring Boot    |  |   | | Spring Boot  | |   |
|   |  +----------------+  |   | +--------------+ |   |
|   |                      |   |                  |   |
|   |  +----------------+  |   | +--------------+ |   |
|   |  | Pod B          |  |   | | Pod D        | |   |
|   |  +----------------+  |   | +--------------+ |   |
|   +----------------------+   +------------------+   |
|                                                    |
+----------------------------------------------------+
```

This is runtime reality.

Your users do not hit YAML files.

Your users hit applications running in containers inside pods on nodes inside a cluster.

A common beginner mistake is thinking:

```text
Kubernetes runs my container directly.
```

More accurate:

```text
Kubernetes creates a Pod object.
Scheduler assigns the Pod to a Node.
Kubelet on that Node asks the container runtime to start containers.
```

That means the Pod is the unit Kubernetes schedules.

The container is the unit the runtime executes.

This distinction matters in debugging.

If a Pod is Pending, the container may not even exist yet.

If a container is CrashLoopBackOff, the Pod exists but the app process keeps failing.

If a Node is NotReady, Pods on it may become unavailable.

---

# 3. Cluster Mental Model

A Kubernetes cluster is a group of machines controlled as one system.

Without Kubernetes, you may think of servers separately.

```text
server-1
server-2
server-3
```

You manually decide where to deploy.

```text
Put order-service on server-1.
Put payment-service on server-2.
Put inventory-service on server-3.
```

With Kubernetes, you think in terms of a cluster.

```text
Dear Kubernetes,
Run 3 copies of order-service somewhere healthy.
Run 2 copies of payment-service somewhere healthy.
Keep them alive.
```

Diagram:

```text
Manual Server Thinking

Developer
   |
   +--> server-1: start order-service
   +--> server-2: start order-service
   +--> server-3: start order-service

Kubernetes Cluster Thinking

Developer
   |
   | desired state: replicas = 3
   v
Cluster
   |
   +--> chooses suitable nodes
   +--> starts pods
   +--> replaces failures
```

The cluster hides individual machine complexity behind one control system.

This does not mean machines disappear.

It means you stop managing machines one by one for normal application deployment.

A cluster contains:

```text
Control plane components
Worker nodes
Networking
Storage integrations
Runtime agents
Application pods
```

The cluster is the boundary where Kubernetes makes scheduling and reconciliation decisions.

If you say:

```text
kubectl get pods
```

You are asking the cluster state.

If you say:

```text
kubectl get nodes
```

You are asking which machines belong to the cluster.

---

# 4. Real World Analogy: City, Buildings, Rooms

Think of Kubernetes like a city.

```text
Cluster = city
Node    = building
Pod     = apartment room
Container = person living in room
```

ASCII:

```text
City / Cluster
+------------------------------------------------+
|                                                |
| Building / Node A        Building / Node B     |
| +----------------+       +----------------+    |
| | Room / Pod 1   |       | Room / Pod 3   |    |
| | App Container  |       | App Container  |    |
| +----------------+       +----------------+    |
|                                                |
| +----------------+       +----------------+    |
| | Room / Pod 2   |       | Room / Pod 4   |    |
| | App Container  |       | App Container  |    |
| +----------------+       +----------------+    |
|                                                |
+------------------------------------------------+
```

The city planner does not care about one specific room unless there is a problem.

The owner says:

```text
I need 3 rooms occupied by order-service workers.
```

The city planner finds buildings with enough space.

If one building burns down, rooms in that building are lost.

The planner opens new rooms in other buildings.

This is how Kubernetes thinks.

You ask for app capacity.

Kubernetes places that capacity across nodes.

Important mental hook:

```text
A Pod belongs to exactly one Node at a time.
A Node can run many Pods.
A Cluster contains many Nodes.
```

Do not memorize.

Just remember:

```text
City -> Building -> Room -> Person
Cluster -> Node -> Pod -> Container
```

---

# 5. Node Mental Model

A node is a machine that can run pods.

It can be:

```text
Physical server
Virtual machine
Cloud VM
Local Docker Desktop VM
Minikube VM
Kind container node
```

Kubernetes does not require you to treat nodes as precious pets.

Production mindset:

```text
Node is replaceable capacity.
```

A node provides:

```text
CPU
Memory
Disk
Network
Container runtime
Kubelet
kube-proxy / networking agent
```

Diagram:

```text
+------------------------------------------------+
| Node                                           |
|                                                |
|  CPU / Memory / Disk / Network                 |
|                                                |
|  +--------------------+                        |
|  | kubelet            | watches API Server     |
|  +--------------------+                        |
|                                                |
|  +--------------------+                        |
|  | container runtime  | starts containers      |
|  +--------------------+                        |
|                                                |
|  +--------------------+                        |
|  | pods               | app runtime units      |
|  +--------------------+                        |
+------------------------------------------------+
```

The kubelet is the local agent.

It asks:

```text
Which Pods are assigned to me?
```

Then it tries to make them real.

The scheduler does not SSH into the node and start containers.

The scheduler only decides placement.

Kubelet performs the local execution.

This separation is very important.

```text
Scheduler = choose where
Kubelet   = run here
Runtime   = start container
```

---

# 6. Pod Mental Model

A Pod is the smallest deployable unit in Kubernetes.

Kubernetes schedules Pods, not individual containers.

A Pod usually contains one application container.

Example:

```text
Pod: order-service-abc123
  Container: order-service
    Process: java -jar order-service.jar
```

Diagram:

```text
+----------------------------------------+
| Pod                                    |
|                                        |
|  Shared network namespace              |
|  Shared volume mounts                  |
|  Shared lifecycle boundary             |
|                                        |
|  +----------------------------------+  |
|  | Container                        |  |
|  | java -jar order-service.jar      |  |
|  +----------------------------------+  |
+----------------------------------------+
```

Why does Kubernetes use Pod instead of directly running containers?

Because sometimes containers must live together.

Examples:

```text
Main app + log sidecar
Main app + proxy sidecar
Main app + metrics exporter
Main app + init container before startup
```

A Pod gives these containers shared local context.

Containers in the same Pod can talk through localhost.

```text
Container A -> localhost -> Container B
```

But most beginner Spring Boot services use one container per Pod.

So the practical beginner model is:

```text
Pod = wrapper around your app container
```

The advanced model is:

```text
Pod = one scheduled unit with shared network, storage, and lifecycle for one or more containers
```

---

# 7. Container Mental Model Inside Pod

A container is where your application process actually runs.

For Spring Boot:

```text
java -jar app.jar
```

For Node.js:

```text
node server.js
```

For Go:

```text
./server
```

Kubernetes does not run Java directly.

It runs a container image that contains Java, your JAR, and startup command.

Example Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/order-service.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Pod YAML references image:

```yaml
containers:
  - name: order-service
    image: order-service:1.0.0
    ports:
      - containerPort: 8080
```

Runtime picture:

```text
Pod
 |
 +-- Container from image order-service:1.0.0
       |
       +-- Java runtime
       +-- app.jar
       +-- process: java -jar app.jar
```

Debugging meaning:

```text
Pod Pending        -> container not started yet
ImagePullBackOff   -> image cannot be pulled
CrashLoopBackOff   -> container starts then crashes
Running not Ready  -> process alive but not traffic-safe
```

Do not say:

```text
My Pod image is broken.
```

More precise:

```text
The container image used by the Pod may be broken.
```

Precision helps debugging.

---

# 8. Cluster vs Node vs Pod vs Container

Here is the memory table.

```text
+-----------+------------------------------+------------------------------+
| Concept   | Simple Meaning               | Production Question          |
+-----------+------------------------------+------------------------------+
| Cluster   | whole Kubernetes system       | Is the platform healthy?     |
| Node      | machine in the cluster        | Does this machine have room? |
| Pod       | scheduled runtime unit        | Is this app instance alive?  |
| Container | actual process environment    | Did the process start?       |
+-----------+------------------------------+------------------------------+
```

Relationship:

```text
Cluster has Nodes
Node has Pods
Pod has Containers
Container has Processes
```

Expanded:

```text
Kubernetes Cluster
    |
    +-- node-a
    |     |
    |     +-- pod/order-service-1
    |     |      |
    |     |      +-- container/order-service
    |     |
    |     +-- pod/payment-service-1
    |
    +-- node-b
          |
          +-- pod/order-service-2
          |
          +-- pod/inventory-service-1
```

A common interview answer:

```text
A cluster is a collection of nodes. Nodes provide compute resources. Kubernetes schedules Pods onto Nodes. Pods wrap one or more containers that share network and storage context. Containers run the actual application processes.
```

But do not only memorize this sentence.

Visualize the tree.

---

# 9. Spring Boot App Placement Example

Suppose you have three services:

```text
order-service
payment-service
inventory-service
```

Each is a Spring Boot app.

You build three images:

```text
order-service:1.0.0
payment-service:1.0.0
inventory-service:1.0.0
```

Kubernetes may place Pods like this:

```text
Cluster
+------------------------------------------------------+
|                                                      |
| Node A                                               |
|   Pod order-1      -> container order-service        |
|   Pod payment-1    -> container payment-service      |
|                                                      |
| Node B                                               |
|   Pod order-2      -> container order-service        |
|   Pod inventory-1  -> container inventory-service    |
|                                                      |
| Node C                                               |
|   Pod order-3      -> container order-service        |
|   Pod payment-2    -> container payment-service      |
|                                                      |
+------------------------------------------------------+
```

You did not manually choose every placement.

You declared desired replicas.

```yaml
spec:
  replicas: 3
```

Scheduler selected nodes based on available resources and constraints.

Your application-level architecture is still your responsibility.

Kubernetes knows:

```text
CPU requests
Memory requests
Labels
Taints
Affinity
Node availability
```

Kubernetes does not know:

```text
This payment service has bad SQL
This order service has wrong business logic
This inventory API is too chatty
```

Kubernetes places workloads.

It does not design your domain model.

---

# 10. Pod Is Not A Mini VM

Many beginners think:

```text
Pod = small virtual machine
```

This is misleading.

A VM has its own guest OS kernel.

A Pod does not have its own kernel.

Containers inside Pods share the node kernel.

Simple picture:

```text
Virtual Machine Model

Hardware
  |
  +-- Host OS
       |
       +-- Hypervisor
            |
            +-- Guest OS
                 |
                 +-- App Process

Pod/Container Model

Node Kernel
  |
  +-- Container namespaces/cgroups
       |
       +-- App Process
```

A Pod is not a full machine.

It is a scheduling and isolation boundary around containers.

Important consequences:

```text
Pod startup is usually faster than VM startup
Pod isolation is weaker than full VM isolation
Pod shares node kernel
Pod can be recreated easily
Pod is disposable
```

Production mindset:

```text
Do not store critical state inside a Pod filesystem.
Pods are cattle, not pets.
```

If a Pod dies, its local filesystem may disappear.

Use external storage, database, object storage, or PersistentVolumes for durable data.

For Spring Boot stateless services, this is ideal.

```text
Pod dies -> new Pod starts -> service continues
```

For stateful systems, you need extra design.

---

# 11. Pod Lifecycle Simple Model

A Pod moves through phases.

Useful simplified lifecycle:

```text
Pending -> Running -> Ready -> Terminating -> Gone
```

More detailed:

```text
1. Pod object created
2. Scheduler assigns Node
3. Kubelet sees assignment
4. Image pulled
5. Container created
6. Container started
7. Readiness checked
8. Pod receives traffic
```

ASCII:

```text
Pod Object
   |
   v
Pending
   |
   | scheduled to node
   v
ContainerCreating
   |
   | image pulled, container started
   v
Running
   |
   | readiness passes
   v
Ready
   |
   | deletion/update/failure
   v
Terminating
```

Important:

```text
Running != Ready
```

Running means container process exists.

Ready means Kubernetes can send traffic to it.

For Spring Boot, startup can take time.

```text
JVM starts
Spring context loads
DB pool initializes
Flyway/Liquibase migration may run
Actuator health becomes UP
Readiness passes
```

Do not route traffic just because Java process exists.

Use readiness probes.

---

# 12. Node Lifecycle Simple Model

A node also has health.

Typical states:

```text
Ready
NotReady
SchedulingDisabled
Unknown
```

A node must regularly report status.

Kubelet sends heartbeats to the API Server.

Diagram:

```text
Node
 |
 | kubelet heartbeat
 v
API Server
 |
 v
Node status updated
```

If heartbeat stops:

```text
Control plane suspects node problem
Node becomes NotReady/Unknown
Pods on that node become unavailable
Controllers create replacement Pods if needed
```

Failure picture:

```text
Before:

Node A: order-1, payment-1
Node B: order-2, payment-2
Node C: order-3

Node B dies:

Node A: order-1, payment-1
Node B: X
Node C: order-3

Replacement:

Node A: order-1, payment-1, order-4
Node C: order-3, payment-3
```

Kubernetes does not repair the dead physical machine.

It restores application desired state somewhere else.

That is self-healing at workload level.

---

# 13. Scheduling: How Pod Gets A Node

The scheduler watches for Pods without assigned nodes.

```text
Pod.spec.nodeName is empty
```

Then it chooses a node.

Simplified flow:

```text
Pending Pod
   |
   v
Filter nodes
   |
   | enough CPU?
   | enough memory?
   | taints tolerated?
   | node selector matches?
   v
Score nodes
   |
   | best fit?
   | spreading?
   | affinity?
   v
Bind Pod to Node
```

ASCII:

```text
Unscheduled Pod
      |
      v
+-----------------------+
| Scheduler             |
| filter + score nodes  |
+----------+------------+
           |
           v
       node-b selected
           |
           v
API Server updates Pod binding
           |
           v
kubelet on node-b starts Pod
```

Important separation:

```text
Scheduler does not start containers.
Kubelet starts containers.
```

If Pod is Pending for long time, common reasons:

```text
Not enough CPU
Not enough memory
Node selector mismatch
Taints without tolerations
PVC not bound
Image pull secret issue comes later, after scheduling
```

Debug:

```bash
kubectl describe pod <pod-name>
```

Look at Events.

Events usually tell why scheduling failed.

---

# 14. Resource Requests: How Scheduler Thinks

Scheduler uses requests, not actual usage, for placement.

Example:

```yaml
resources:
  requests:
    cpu: "500m"
    memory: "512Mi"
  limits:
    cpu: "1000m"
    memory: "1Gi"
```

Meaning:

```text
Please reserve at least 0.5 CPU and 512 Mi memory for this container.
Do not allow more than 1 CPU and 1 Gi memory.
```

Node capacity example:

```text
Node A capacity: 4 CPU, 8 Gi memory
Already requested: 3.5 CPU, 7 Gi memory
New Pod request: 1 CPU, 2 Gi memory

Result: cannot schedule on Node A
```

Diagram:

```text
Node Capacity
CPU:    [####----] 4 cores
Memory: [######--] 8 Gi

Requested already
CPU:    [###-----] 3.5 cores
Memory: [#######-] 7 Gi

New Pod needs
CPU:    [##------] 1 core
Memory: [##------] 2 Gi

Not enough remaining request space
```

Production lesson:

```text
No requests = scheduler cannot make smart placement.
Wrong requests = cluster becomes inefficient or unstable.
```

For Spring Boot, memory requests matter because JVM memory behavior can surprise beginners.

A Java app with 512Mi limit may crash if heap, metaspace, threads, direct buffers, and native memory exceed the container limit.

---

# 15. Pod Networking Mental Model

Each Pod gets its own IP address.

Inside a Pod, containers share the same network namespace.

For one-container Pod:

```text
Pod IP = app IP
```

For multi-container Pod:

```text
Both containers share same Pod IP
Both can use localhost to talk
```

Diagram:

```text
Pod
+-------------------------------------+
| IP: 10.244.1.25                     |
|                                     |
| Container A: app on port 8080       |
| Container B: sidecar on port 15001  |
|                                     |
| A -> localhost:15001 -> B           |
| B -> localhost:8080  -> A           |
+-------------------------------------+
```

Across Pods:

```text
Pod A IP: 10.244.1.25
Pod B IP: 10.244.2.18
```

They can communicate if network policy allows.

But Pod IP is not stable.

A restarted Pod may get a new IP.

That is why clients should use Service names.

```text
order-service.default.svc.cluster.local
```

Do not build production systems using direct Pod IPs.

Use Services, DNS, and labels.

---

# 16. Multi-Container Pod Model

Most Spring Boot services use one container per Pod.

But Kubernetes allows multiple containers in one Pod when they are tightly coupled.

Common examples:

```text
App container + logging sidecar
App container + Envoy proxy sidecar
App container + metrics exporter
App container + file sync sidecar
```

Diagram:

```text
+------------------------------------------------+
| Pod                                            |
|                                                |
|  Shared IP: 10.244.1.50                        |
|  Shared volumes                                |
|                                                |
|  +--------------------+   +----------------+   |
|  | Spring Boot App    |   | Envoy Sidecar  |   |
|  | port 8080          |   | port 15001     |   |
|  +--------------------+   +----------------+   |
|                                                |
|  app -> localhost:15001 -> envoy               |
+------------------------------------------------+
```

Sidecar rule:

```text
Use same Pod only when containers must share lifecycle and local context.
```

Bad use:

```text
Put order-service and payment-service in same Pod.
```

Why bad?

They scale differently.

They deploy differently.

They fail differently.

They should be separate Deployments and separate Pods.

Good use:

```text
order-service + local proxy sidecar
```

Because proxy is part of the order-service runtime behavior.

Mental model:

```text
A Pod is one deployable cell.
Do not put unrelated organs in one cell.
```

---

# 17. Init Containers

An init container runs before the main app container.

Use it for startup preparation.

Examples:

```text
Wait for dependency
Run migration check
Download config file
Prepare mounted directory
Check secret exists
```

Flow:

```text
Pod created
   |
   v
Init container 1 runs successfully
   |
   v
Init container 2 runs successfully
   |
   v
Main container starts
```

ASCII:

```text
+------------------------------------+
| Pod                                |
|                                    |
|  init-db-check   -> completed      |
|  init-config     -> completed      |
|  order-service   -> running        |
+------------------------------------+
```

Example YAML:

```yaml
initContainers:
  - name: wait-for-db
    image: busybox
    command: ['sh', '-c', 'echo checking-db; sleep 5']
containers:
  - name: order-service
    image: order-service:1.0.0
```

Production caution:

```text
Do not hide real dependency design problems with long sleeps.
```

Better:

```text
Application should handle retry/backoff.
Readiness should protect traffic.
Init containers should prepare environment, not replace resilience.
```

For Spring Boot, DB may not be ready at exact startup time.

Your app should not assume perfect startup order.

Kubernetes starts Pods, but distributed systems need retry logic.

---

# 18. Replica Mental Model

When you say replicas = 3, Kubernetes creates 3 Pods.

```text
Deployment: order-service replicas=3

Creates:
order-service-abc
order-service-def
order-service-ghi
```

Each Pod is separate.

Each has its own IP.

Each may run on a different node.

Diagram:

```text
Deployment order-service
          |
          v
ReplicaSet order-service-rs
          |
          +--> Pod order-abc on Node A
          +--> Pod order-def on Node B
          +--> Pod order-ghi on Node C
```

These Pods are interchangeable if your application is stateless.

For stateless Spring Boot APIs, this is ideal.

```text
Any request can go to any replica.
```

For stateful apps, replicas may not be interchangeable.

Example:

```text
Redis primary and replica are not identical roles.
Postgres leader and follower differ.
Kafka brokers have partition assignments.
```

That is why Kubernetes has StatefulSet for some workloads.

But for normal backend services:

```text
Deployment + replicas + Service
```

is the common pattern.

---

# 19. Why Pod Names Are Weird

You may see:

```text
order-service-7c9dbf5b6d-kx82p
```

This is not random noise.

It reflects ownership.

```text
Deployment name: order-service
ReplicaSet hash: 7c9dbf5b6d
Pod suffix: kx82p
```

Mental model:

```text
Deployment creates ReplicaSet
ReplicaSet creates Pods
Pod name includes generated identity
```

Example:

```text
order-service
  |
  +-- order-service-7c9dbf5b6d
        |
        +-- order-service-7c9dbf5b6d-kx82p
        +-- order-service-7c9dbf5b6d-m91ab
        +-- order-service-7c9dbf5b6d-zx10q
```

Do not manually depend on Pod names.

Pods are disposable.

Use labels and Services.

Bad idea:

```text
Call http://order-service-7c9dbf5b6d-kx82p:8080
```

Good idea:

```text
Call http://order-service:8080
```

Pod names help debugging.

They are not stable application identity for stateless workloads.

---

# 20. Labels Connect Cluster Objects

Pods are selected by labels.

Deployment template gives labels to Pods.

```yaml
template:
  metadata:
    labels:
      app: order-service
      version: v1
```

Service selects labels.

```yaml
selector:
  app: order-service
```

Diagram:

```text
Service: order-service
selector app=order-service
        |
        v
+-------------------------------+
| Matching Pods                 |
| order-abc app=order-service   |
| order-def app=order-service   |
| order-ghi app=order-service   |
+-------------------------------+
```

If labels mismatch:

```text
Service exists
Pods exist
No endpoints
Traffic fails
```

Broken example:

```yaml
Pod label:
  app: order

Service selector:
  app: order-service
```

Debug:

```bash
kubectl get pods --show-labels
kubectl describe svc order-service
kubectl get endpoints order-service
```

Production mindset:

```text
In Kubernetes, relationships are often label queries, not hard-coded references.
```

This is powerful but dangerous.

One typo can disconnect traffic.

---

# 21. Node Capacity And Pod Density

A node can run many Pods.

But not unlimited Pods.

Limits come from:

```text
CPU
Memory
Disk
Network
Max pods per node
IP address availability
DaemonSet overhead
System reserved resources
```

Picture:

```text
Node A
CPU:    8 cores
Memory: 32 Gi

Pods:
  order-service      request 1 CPU / 1 Gi
  payment-service    request 1 CPU / 2 Gi
  inventory-service  request 0.5 CPU / 1 Gi
  logging-agent      request 0.2 CPU / 256 Mi
```

Packing view:

```text
Node A Capacity
CPU    [########]
Memory [################################]

Allocated requests
CPU    [###-----]
Memory [#####---------------------------]
```

If too many heavy Pods land on one node, performance suffers.

If requests are too high, cluster wastes capacity.

If requests are too low, node gets overloaded.

This is why production Kubernetes needs measurement.

```text
requests = scheduling promise
limits   = hard container boundary
actual   = real runtime usage
```

For Java services, set resources based on load testing and JVM behavior.

Do not copy random YAML from the internet.

---

# 22. Cluster Is Not One Big Machine

A dangerous beginner model:

```text
Cluster = one huge server
```

Better:

```text
Cluster = many machines coordinated by one control system
```

Why it matters:

```text
Node can fail
Network between nodes can fail
Pod placement affects latency
Storage may be node-specific
DaemonSets run per node
Pod IP ranges differ by node
```

Diagram:

```text
Wrong mental model:

+-------------------+
| giant machine     |
| all apps inside   |
+-------------------+

Correct mental model:

+---------+   network   +---------+   network   +---------+
| Node A  | <---------> | Node B  | <---------> | Node C  |
+---------+             +---------+             +---------+
      \                     |                     /
       \                    |                    /
        +------------- Control Plane ------------+
```

Kubernetes gives a unified API.

It does not remove distributed systems reality.

If two Pods communicate across nodes, network is involved.

If a node dies, Pods on that node die.

If zone fails, all nodes in that zone may disappear.

Production design must still consider failure domains.

---

# 23. Failure Domain Mental Model

A failure domain is a group of things that can fail together.

Examples:

```text
Same node
Same rack
Same availability zone
Same region
Same power supply
Same network switch
```

If all replicas run on one node, node failure kills all replicas.

Bad placement:

```text
Node A:
  order-1
  order-2
  order-3

Node B:
  empty
Node C:
  empty
```

Better placement:

```text
Node A:
  order-1
Node B:
  order-2
Node C:
  order-3
```

Kubernetes can help with spreading.

Tools:

```text
pod anti-affinity
topology spread constraints
node labels
zones
```

Simple mental model:

```text
Do not put all eggs in one node.
```

For a product company backend, availability often depends on replica spreading.

Having 3 replicas is not enough if all 3 replicas are on the same weak failure domain.

---

# 24. Pod Disposability

Pods are temporary.

They are created, killed, replaced, rescheduled.

You should design apps assuming:

```text
Pod can disappear anytime.
Pod IP can change.
Pod local disk can disappear.
Pod name can change.
Pod restart may happen during deploy.
```

Spring Boot service should therefore:

```text
Store durable data in database/object storage
Use external Redis/Kafka/Postgres correctly
Expose health/readiness endpoints
Handle SIGTERM gracefully
Use idempotency for important operations
Avoid in-memory-only critical state
```

Bad design:

```text
User uploads file
App stores only in /tmp inside Pod
Pod restarts
File gone
```

Good design:

```text
User uploads file
App stores in S3/object storage
DB stores metadata
Pod can die safely
```

Pod motto:

```text
A Pod is not your home.
It is a rented room.
```

---

# 25. Graceful Shutdown For Spring Boot Pods

When Kubernetes terminates a Pod, it sends SIGTERM to the container process.

Spring Boot should stop accepting new work and finish existing work if possible.

Flow:

```text
kubectl rollout restart / scale down / node drain
        |
        v
Pod marked Terminating
        |
        v
Removed from Service endpoints after readiness changes
        |
        v
SIGTERM sent to Java process
        |
        v
Spring Boot graceful shutdown
        |
        v
Container exits
```

Spring Boot config:

```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

Kubernetes termination grace:

```yaml
terminationGracePeriodSeconds: 45
```

Mental model:

```text
Kubernetes gives your app a chance to leave cleanly.
Your app must cooperate.
```

Bad behavior:

```text
Pod killed while processing payment
No idempotency
Request retried
Double charge risk
```

Good behavior:

```text
Readiness goes false
No new traffic
Existing request finishes
Idempotency key protects retry
App exits cleanly
```

Kubernetes orchestration and application correctness must work together.

---

# 26. YAML Example: Pod Only

A raw Pod YAML:

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
      image: order-service:1.0.0
      ports:
        - containerPort: 8080
```

This creates one Pod.

But in production, you usually do not create naked Pods directly.

Why?

If the Pod dies, who recreates it?

A naked Pod has no higher-level controller protecting desired replica count.

Better:

```text
Deployment -> ReplicaSet -> Pod
```

Raw Pod is useful for learning and debugging.

Deployment is useful for real applications.

Mental model:

```text
Naked Pod = one manually requested room
Deployment = manager that keeps rooms alive
```

If you delete a naked Pod:

```bash
kubectl delete pod order-service-pod
```

It is gone.

If you delete a Deployment-managed Pod:

```bash
kubectl delete pod order-service-abc
```

ReplicaSet creates a replacement.

That is the difference between object and controller-owned object.

---

# 27. YAML Example: Deployment Creates Pods

Production-like Deployment:

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
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
        - name: order-service
          image: order-service:1.0.0
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "500m"
              memory: "512Mi"
            limits:
              cpu: "1000m"
              memory: "1Gi"
```

Object chain:

```text
Deployment
  |
  +-- ReplicaSet
        |
        +-- Pod 1
        +-- Pod 2
        +-- Pod 3
```

The Pod template is important.

```text
Deployment does not directly contain running containers.
It contains a template for Pods.
```

Controller uses the template to create Pods.

When you update the template, Deployment creates a new ReplicaSet.

```text
image: order-service:1.0.0 -> order-service:1.1.0
```

This triggers rollout.

Mental hook:

```text
Deployment = rollout brain
ReplicaSet = replica count guard
Pod = runtime cell
Container = process box
```

---

# 28. Java Code: Know Your Pod And Node

A Spring Boot app can read environment variables injected from Kubernetes.

YAML:

```yaml
env:
  - name: POD_NAME
    valueFrom:
      fieldRef:
        fieldPath: metadata.name
  - name: POD_NAMESPACE
    valueFrom:
      fieldRef:
        fieldPath: metadata.namespace
  - name: NODE_NAME
    valueFrom:
      fieldRef:
        fieldPath: spec.nodeName
```

Java controller:

```java
package com.example.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RuntimeInfoController {

    @GetMapping("/runtime")
    public Map<String, String> runtime() {
        return Map.of(
            "pod", env("POD_NAME"),
            "namespace", env("POD_NAMESPACE"),
            "node", env("NODE_NAME"),
            "javaVersion", System.getProperty("java.version")
        );
    }

    private String env(String key) {
        return System.getenv().getOrDefault(key, "unknown");
    }
}
```

Why useful?

When load balancing across replicas, you can see which Pod handled a request.

Example response:

```json
{
  "pod": "order-service-7c9dbf5b6d-kx82p",
  "namespace": "prod",
  "node": "worker-node-2",
  "javaVersion": "21.0.2"
}
```

This helps debug traffic distribution.

---

# 29. Java Code: Graceful Request Tracking

For production, you may want to know whether requests are still active during shutdown.

Simple filter:

```java
package com.example.order;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ActiveRequestFilter extends OncePerRequestFilter {

    private final AtomicInteger activeRequests = new AtomicInteger();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        activeRequests.incrementAndGet();
        try {
            filterChain.doFilter(request, response);
        } finally {
            activeRequests.decrementAndGet();
        }
    }

    public int activeRequests() {
        return activeRequests.get();
    }
}
```

Expose it:

```java
@RestController
class RequestDebugController {
    private final ActiveRequestFilter filter;

    RequestDebugController(ActiveRequestFilter filter) {
        this.filter = filter;
    }

    @GetMapping("/debug/active-requests")
    Map<String, Integer> active() {
        return Map.of("activeRequests", filter.activeRequests());
    }
}
```

This is not mandatory for every service.

But it teaches a production idea:

```text
Pod termination is not only Kubernetes behavior.
Application must understand lifecycle too.
```

---

# 30. Readiness And Liveness For Pods

Kubernetes needs to know two things.

```text
Liveness: should this container be restarted?
Readiness: should this Pod receive traffic?
```

Do not mix them.

Bad:

```text
DB down -> liveness fails -> Kubernetes restarts app repeatedly
```

Better:

```text
DB down -> readiness fails -> traffic stops
App stays alive and retries DB
```

Spring Boot Actuator config:

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
```

Diagram:

```text
Container alive?
   |
   +-- no  -> restart container
   |
   +-- yes -> check readiness

Ready for traffic?
   |
   +-- no  -> remove from Service endpoints
   |
   +-- yes -> allow traffic
```

---

# 31. Service Routes To Ready Pods

A Service gives stable access to a group of Pods.

```text
Client -> Service -> Ready Pods
```

Diagram:

```text
Client
  |
  | http://order-service
  v
Service order-service
  |
  +--> Pod A Ready
  +--> Pod B Ready
  +--> Pod C NotReady  X no traffic
```

Service selects Pods by labels.

Readiness decides whether selected Pods become endpoints.

That means two things must be correct:

```text
1. Labels must match Service selector.
2. Readiness must pass.
```

Debug order:

```bash
kubectl get svc order-service
kubectl get endpoints order-service
kubectl get pods -l app=order-service
kubectl describe pod <pod>
```

If endpoints are empty:

```text
Possible cause 1: label mismatch
Possible cause 2: Pods not Ready
Possible cause 3: wrong targetPort
```

Mental model:

```text
Service is not magic.
It is a stable front door pointing to currently Ready matching Pods.
```

---

# 32. Dry Run: Deploy Order Service To Cluster

You apply Deployment:

```bash
kubectl apply -f order-service-deployment.yaml
```

Internal story:

```text
1. API Server validates Deployment.
2. Deployment object stored in etcd.
3. Deployment controller creates ReplicaSet.
4. ReplicaSet controller creates 3 Pod objects.
5. Scheduler sees Pods with no node.
6. Scheduler selects nodes.
7. API Server stores Pod bindings.
8. Kubelet on selected nodes sees assigned Pods.
9. Container runtime pulls image.
10. Container starts Java process.
11. Spring Boot starts.
12. Readiness probe begins.
13. Ready Pods become Service endpoints.
```

ASCII:

```text
Deployment YAML
      |
      v
Deployment object
      |
      v
ReplicaSet
      |
      v
Pods created
      |
      v
Scheduler assigns Nodes
      |
      v
Kubelet starts Containers
      |
      v
Spring Boot Ready
      |
      v
Service sends traffic
```

This one flow explains many production problems.

When something fails, identify which step failed.

---

# 33. Dry Run: Pod Crash

Initial desired state:

```text
order-service replicas = 3
```

Actual:

```text
Pod A Running Ready
Pod B Running Ready
Pod C Running Ready
```

Then Pod B container crashes because DB password is wrong after config update.

Flow:

```text
Container exits with error
   |
   v
Kubelet notices
   |
   v
Restart policy applies
   |
   v
Container restarted
   |
   v
App crashes again
   |
   v
CrashLoopBackOff
```

ReplicaSet may still see the Pod object exists.

Kubelet keeps trying to restart the container.

Debug:

```bash
kubectl get pods
kubectl logs pod-b
kubectl logs pod-b --previous
kubectl describe pod pod-b
```

Mental distinction:

```text
Pod exists.
Container inside Pod is unhealthy.
```

Kubernetes can restart the process.

It cannot guess the correct DB password.

---

# 34. Dry Run: Node Failure

Initial:

```text
Node A: order-1
Node B: order-2
Node C: order-3
```

Node B fails.

Timeline:

```text
1. Kubelet heartbeat from Node B stops.
2. Control plane marks Node B NotReady/Unknown.
3. Pods on Node B are considered unavailable.
4. ReplicaSet sees desired 3 but available 2.
5. New Pod order-4 is created.
6. Scheduler places order-4 on Node A or Node C.
7. Kubelet starts container.
8. Readiness passes.
9. Service sends traffic to healthy Pods.
```

ASCII:

```text
Before failure:

Node A [order-1]
Node B [order-2]
Node C [order-3]

After Node B failure:

Node A [order-1]
Node B [X]
Node C [order-3]

After reconciliation:

Node A [order-1, order-4]
Node B [X]
Node C [order-3]
```

Production lesson:

```text
Kubernetes restores desired Pod count.
It does not save in-memory state from the dead Pod.
```

---

# 35. Dry Run: Rolling Update Across Pods

Current version:

```text
order-service:1.0.0
```

New version:

```text
order-service:1.1.0
```

Deployment changes Pod template.

Kubernetes creates a new ReplicaSet.

Flow:

```text
Old ReplicaSet has 3 Pods.
New ReplicaSet starts with 0 Pods.
Deployment gradually scales new up and old down.
```

Picture:

```text
Step 0: v1 v1 v1
Step 1: v1 v1 v1 v2-starting
Step 2: v1 v1 v2
Step 3: v1 v2 v2
Step 4: v2 v2 v2
```

If new Pods fail readiness:

```text
New Pod starts
Readiness fails
Service does not route traffic
Rollout stalls
Old Pods may remain
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
Deployment safely changes Pod templates over time.
```

---

# 36. Production Story: All Pods On One Node

A team runs 3 replicas of order-service.

They think:

```text
We are safe because replicas = 3.
```

But actual placement:

```text
Node A:
  order-1
  order-2
  order-3

Node B:
  empty
Node C:
  empty
```

Node A crashes.

Result:

```text
All order-service replicas unavailable.
```

The team had replica count but poor failure-domain spreading.

Fix options:

```text
Use topology spread constraints
Use pod anti-affinity
Use multiple nodes/zones
Monitor pod placement
```

Debug command:

```bash
kubectl get pods -o wide
```

Look at NODE column.

Mindset:

```text
Replica count answers how many.
Placement answers where.
Availability needs both.
```

---

# 37. Production Story: Pending Pods

Symptoms:

```text
order-service-abc   Pending
```

Beginner says:

```text
Kubernetes is broken.
```

Better question:

```text
Why could the scheduler not place this Pod?
```

Debug:

```bash
kubectl describe pod order-service-abc
```

Common events:

```text
0/3 nodes are available: insufficient memory
0/3 nodes are available: insufficient cpu
node(s) had untolerated taint
pod has unbound immediate PersistentVolumeClaims
```

Meaning:

```text
Pod object exists.
No suitable node found yet.
Container has not started.
```

Fix depends on cause:

```text
Lower wrong requests
Add nodes
Fix tolerations
Fix PVC/storage
Fix node selector
```

Mental model:

```text
Pending is usually a placement problem, not an app crash.
```

---

# 38. Production Story: OOMKilled Java Pod

Symptoms:

```text
order-service-abc restarted
Last State: Terminated
Reason: OOMKilled
Exit Code: 137
```

Meaning:

```text
Container exceeded memory limit.
Kernel killed it.
```

Spring Boot memory includes more than heap.

```text
Heap
Metaspace
Thread stacks
Direct buffers
JIT/code cache
Native memory
Libraries
```

Bad assumption:

```text
memory limit 512Mi means set -Xmx512m
```

This may fail because heap uses all memory and leaves no room for native overhead.

Better:

```text
limit 1Gi
heap maybe 60-70% depending app
measure under load
```

Example:

```yaml
resources:
  requests:
    memory: "1Gi"
  limits:
    memory: "1Gi"
```

JVM option example:

```text
-XX:MaxRAMPercentage=70
```

Debug:

```bash
kubectl describe pod <pod>
kubectl top pod <pod>
kubectl logs <pod> --previous
```

Mental model:

```text
Kubernetes enforces the container boundary.
Java must be sized for that boundary.
```

---

# 39. Production Story: Service Has No Endpoints

Symptoms:

```text
curl http://order-service fails
kubectl get svc shows service exists
kubectl get pods shows pods running
```

Check endpoints:

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
Pods are not Ready
targetPort does not match container port
Wrong namespace
```

Debug sequence:

```bash
kubectl get svc order-service -o yaml
kubectl get pods --show-labels
kubectl describe pod <pod>
kubectl get endpoints order-service
```

ASCII:

```text
Service selector app=order-service
        |
        v
Pods labels app=order
        |
        v
No match
        |
        v
No endpoints
        |
        v
No traffic
```

Production lesson:

```text
Running Pods do not guarantee reachable service.
Service routing needs labels + readiness + ports.
```

---

# 40. Debugging Mindset: Cluster To Container

When debugging Kubernetes, follow the hierarchy.

```text
Cluster
  |
  v
Node
  |
  v
Pod
  |
  v
Container
  |
  v
Application logs
```

Do not jump randomly.

Good command path:

```bash
kubectl get nodes
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl get svc
kubectl get endpoints
```

Interpretation:

```text
Node NotReady      -> machine/agent/network problem
Pod Pending        -> scheduling/resource problem
ImagePullBackOff   -> image/registry problem
CrashLoopBackOff   -> app/container startup problem
Running 0/1        -> readiness or container issue
Service no endpoint-> label/readiness/port issue
```

One picture:

```text
Is cluster healthy?
   |
Are nodes ready?
   |
Are pods scheduled?
   |
Did containers start?
   |
Is app ready?
   |
Does service route?
```

This is production debugging.

---

# 41. Commands Cheat Sheet

Cluster and nodes:

```bash
kubectl cluster-info
kubectl get nodes
kubectl describe node <node-name>
kubectl top nodes
```

Pods:

```bash
kubectl get pods
kubectl get pods -o wide
kubectl get pods --show-labels
kubectl describe pod <pod-name>
kubectl logs <pod-name>
kubectl logs <pod-name> --previous
kubectl exec -it <pod-name> -- sh
```

Deployments and ReplicaSets:

```bash
kubectl get deployments
kubectl describe deployment order-service
kubectl get rs
kubectl rollout status deployment/order-service
kubectl rollout history deployment/order-service
kubectl rollout undo deployment/order-service
```

Services:

```bash
kubectl get svc
kubectl describe svc order-service
kubectl get endpoints order-service
```

Resource usage:

```bash
kubectl top pod
kubectl top pod <pod-name>
kubectl top node
```

Placement:

```bash
kubectl get pods -o wide
```

The `NODE` column tells where each Pod is running.

---

# 42. Interview Questions

## What is a Kubernetes cluster?

A Kubernetes cluster is a set of machines managed as one orchestration system. It includes a control plane and worker nodes. The cluster stores desired state, schedules workloads, runs Pods, and continuously reconciles actual state toward desired state.

## What is a node?

A node is a machine in the cluster that provides CPU, memory, disk, and network resources for running Pods. Each node runs kubelet and a container runtime. Kubelet watches for Pods assigned to that node and starts containers locally.

## What is a Pod?

A Pod is the smallest deployable and schedulable unit in Kubernetes. It wraps one or more containers that share network and storage context. Kubernetes schedules Pods onto Nodes, and containers inside the Pod run the actual application processes.

## Is a Pod the same as a container?

No. A Pod is a Kubernetes runtime unit that can contain one or more containers. The container runs the actual process. Kubernetes schedules the Pod, while the container runtime starts containers inside that Pod.

## Can a Pod run on multiple nodes?

No. A Pod is assigned to exactly one node at a time. If that node fails, Kubernetes may create a replacement Pod on another node, but the original Pod does not stretch across nodes.

## Why should we not call Pod IPs directly?

Pod IPs are temporary. When Pods restart or are recreated, their IPs can change. A Service provides a stable name and virtual IP that routes traffic to currently Ready matching Pods.

## What happens when a node dies?

The control plane eventually marks the node NotReady or Unknown. Pods on that node become unavailable. Controllers such as ReplicaSet create replacement Pods to restore the desired replica count on healthy nodes.

## What is the difference between Pending and CrashLoopBackOff?

Pending usually means the Pod has not been scheduled or containers have not started, often due to resource, taint, selector, or storage issues. CrashLoopBackOff means the container starts but repeatedly crashes, so kubelet restarts it with backoff.

## Why do we need readiness probes?

A container can be running before the application is ready for traffic. Readiness probes tell Kubernetes whether a Pod should be included in Service endpoints. This prevents traffic from reaching apps that are still starting or temporarily unable to serve.

## Why are resource requests important?

Resource requests tell the scheduler how much CPU and memory to reserve for a Pod. Without correct requests, Kubernetes cannot place Pods safely. Bad requests can cause poor packing, Pending Pods, node pressure, or unstable performance.

---

# 43. Cluster Node Pod Cheat Sheet

```text
Cluster
  Whole Kubernetes environment.
  Contains control plane and worker nodes.

Node
  Machine that runs Pods.
  Provides CPU, memory, disk, network.
  Runs kubelet and container runtime.

Pod
  Smallest deployable/schedulable unit.
  Assigned to one node.
  Has its own IP.
  Contains one or more containers.

Container
  Actual application process environment.
  Built from image.
  Runs java -jar app.jar for Spring Boot.
```

Hierarchy:

```text
Cluster -> Node -> Pod -> Container -> Process
```

Failure mapping:

```text
Cluster API down      -> cannot manage cluster normally
Node NotReady         -> machine/agent/network issue
Pod Pending           -> scheduling/resource issue
ImagePullBackOff      -> image/registry issue
CrashLoopBackOff      -> app/container crash issue
Running not Ready     -> health/readiness issue
Service no endpoints  -> label/readiness/port issue
```

Production rules:

```text
Do not store durable data in Pod local disk.
Do not call Pod IPs directly.
Do not put unrelated services in one Pod.
Do not rely only on replica count; check placement.
Set realistic resource requests and limits.
Use readiness and liveness correctly.
Design Spring Boot shutdown gracefully.
```

---

# 44. One Picture To Remember

```text
                         KUBERNETES CLUSTER

+------------------------------------------------------------------+
|                                                                  |
|  Control Plane                                                   |
|  +----------------+   +-------------+   +--------------------+   |
|  | API Server     |   | Scheduler   |   | Controllers        |   |
|  +----------------+   +-------------+   +--------------------+   |
|                                                                  |
|              desired state -> placement -> reconciliation         |
|                                                                  |
|  Worker Nodes                                                    |
|                                                                  |
|  +--------------------------+   +-----------------------------+   |
|  | Node A                   |   | Node B                      |   |
|  |                          |   |                             |   |
|  | kubelet                  |   | kubelet                     |   |
|  | container runtime        |   | container runtime           |   |
|  |                          |   |                             |   |
|  | +----------------------+ |   | +-------------------------+ |   |
|  | | Pod order-1          | |   | | Pod order-2             | |   |
|  | | IP 10.244.1.10       | |   | | IP 10.244.2.20          | |   |
|  | | +------------------+ | |   | | +---------------------+ | |   |
|  | | | Container        | | |   | | | Container           | | |   |
|  | | | java -jar app    | | |   | | | java -jar app       | | |   |
|  | | +------------------+ | |   | | +---------------------+ | |   |
|  | +----------------------+ |   | +-------------------------+ |   |
|  |                          |   |                             |   |
|  | +----------------------+ |   | +-------------------------+ |   |
|  | | Pod payment-1        | |   | | Pod inventory-1        | |   |
|  | +----------------------+ |   | +-------------------------+ |   |
|  +--------------------------+   +-----------------------------+   |
|                                                                  |
+------------------------------------------------------------------+

Rule:

Cluster is the whole system.
Node is the machine.
Pod is the scheduled runtime cell.
Container is where your app process runs.
```

---

# 45. Final Memory Hook

Do not memorize Kubernetes as random objects.

Remember this physical chain:

```text
Your Java code
   |
   v
JAR file
   |
   v
Container image
   |
   v
Container
   |
   v
Pod
   |
   v
Node
   |
   v
Cluster
```

And this operational chain:

```text
You declare desired replicas
   |
   v
Kubernetes creates Pods
   |
   v
Scheduler places Pods on Nodes
   |
   v
Kubelet starts Containers
   |
   v
Spring Boot serves traffic when Ready
```

Final sentence:

```text
A Kubernetes cluster is not where you memorize objects.
It is where nodes provide capacity, pods become disposable runtime cells, and containers run your real application processes under continuous orchestration.
```

