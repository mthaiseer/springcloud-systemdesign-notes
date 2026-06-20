# 004_ControlPlane_vs_DataPlane.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why This Chapter Exists

Most Kubernetes confusion comes from mixing two different worlds:

```text
1. The world that makes decisions
2. The world that runs real user traffic
```

Kubernetes calls these two worlds:

```text
Control Plane = decision making and cluster brain
Data Plane    = actual application runtime and traffic path
```

If you do not separate them, Kubernetes feels like magic.

You may ask:

```text
Why is my Pod Pending?
Why is my Service reachable even if API Server is slow?
Why can app traffic continue when scheduler is down?
Why does kubectl fail but existing applications still run?
```

These questions become easy when you understand the split.

One picture:

```text
                 CONTROL PLANE
          decides, stores, watches, schedules
                       |
                       | desired decisions
                       v
                   DATA PLANE
          runs pods, handles traffic, executes work
```

Do not memorize component names first.

Understand the job split first.

```text
Control Plane asks: What should happen?
Data Plane asks: How do I keep running what was assigned to me?
```

In production, this distinction helps you debug faster.

Example:

```text
kubectl get pods fails
```

This may be a control plane problem.

But:

```text
Customer traffic still works
```

because the data plane may still be healthy.

That is the core model.

---

# 2. The Wrong Mental Model

Bad mental model:

```text
Kubernetes Cluster
    |
    +-- API Server runs apps
    +-- Scheduler runs apps
    +-- etcd runs apps
    +-- Pods are inside control plane
```

This is wrong.

The API Server does not run your Spring Boot process.

The scheduler does not execute your Java code.

etcd does not serve customer requests.

Correct model:

```text
CONTROL PLANE
  - API Server
  - etcd
  - Scheduler
  - Controller Manager

DATA PLANE
  - Worker Nodes
  - kubelet
  - container runtime
  - Pods
  - Services / kube-proxy / CNI rules
  - Your Spring Boot containers
```

ASCII:

```text
Wrong Thinking

User Traffic
    |
    v
API Server  X

Correct Thinking

User Traffic
    |
    v
Service / Ingress / Pod
    |
    v
Spring Boot Container
```

`kubectl` traffic and customer traffic are different paths.

```text
kubectl apply -> API Server -> etcd -> controllers

HTTP request -> Load Balancer -> Service -> Pod -> Java app
```

If you remember this, many debugging cases become simple.

---

# 3. Real World Analogy: Airport

Airport control tower:

```text
- decides runway usage
- coordinates landings
- coordinates takeoffs
- prevents conflicts
```

Airplanes and runways:

```text
- carry passengers
- burn fuel
- move physically
- deliver real-world value
```

Mapping:

```text
Airport Control Tower = Kubernetes Control Plane
Airplanes + Runways   = Kubernetes Data Plane
```

Diagram:

```text
            AIRPORT CONTROL TOWER
        +---------------------------+
        | decide which plane lands  |
        | decide runway allocation  |
        | coordinate movement       |
        +-------------+-------------+
                      |
                      | instructions
                      v
        +---------------------------+
        | runway + airplanes        |
        | passengers actually move  |
        +---------------------------+
```

Kubernetes:

```text
            KUBERNETES CONTROL PLANE
        +------------------------------+
        | API Server                   |
        | Scheduler                    |
        | Controllers                  |
        | etcd                         |
        +--------------+---------------+
                       |
                       | assignments
                       v
        +------------------------------+
        | Worker Nodes                 |
        | Pods                         |
        | Containers                   |
        | Services                     |
        +------------------------------+
```

Important:

```text
The tower does not carry passengers.
The control plane does not serve your app traffic.
```

If the control tower has a temporary communication issue, planes already in the air do not instantly disappear.

Similarly, if the Kubernetes API Server is temporarily unavailable, already running Pods do not instantly stop.

---

# 4. Real World Analogy: Restaurant Chain

Imagine a restaurant chain.

Head office:

```text
- decides each branch should have 5 chefs
- decides menu version
- decides opening hours
- tracks branch status
```

Branch kitchen:

```text
- cooks food
- serves customers
- handles local execution
```

Mapping:

```text
Head Office       = Control Plane
Branch Kitchens   = Data Plane
Customers         = User Traffic
Chefs             = Pods
Branch Manager    = kubelet
```

ASCII:

```text
                 HEAD OFFICE
        +--------------------------+
        | desired chefs = 5        |
        | menu version = v2        |
        | branch health dashboard  |
        +------------+-------------+
                     |
                     | instruction
                     v
                 BRANCH KITCHEN
        +--------------------------+
        | chef-1 cooking           |
        | chef-2 cooking           |
        | chef-3 cooking           |
        | customers eating         |
        +--------------------------+
```

If head office network is temporarily down:

```text
Existing chefs do not vanish.
Existing customers can still eat.
New menu rollout may pause.
New branch scheduling may pause.
```

Kubernetes behaves similarly:

```text
Existing Pods may continue running.
Existing Service traffic may continue.
New scheduling may stop.
Rollouts may pause.
kubectl may fail.
```

This is why separating control and data plane is not theory.

It is production survival.

---

# 5. The Core Picture

```text
                          DEVELOPER
                             |
                             | kubectl apply / get / logs
                             v
+----------------------------------------------------------------+
|                         CONTROL PLANE                          |
|                                                                |
|  +------------+     +------------+     +-------------------+   |
|  | API Server |<--->|   etcd     |     | Controller Manager|   |
|  +-----+------+     +------------+     +---------+---------+   |
|        ^                                      ^                |
|        |                                      | watch          |
|        |                                      v                |
|  +-----+------+                         +------------------+   |
|  | Scheduler  |                         | Reconciliation   |   |
|  +-----+------+                         +------------------+   |
+--------|-------------------------------------------------------+
         |
         | Pod assigned to node
         v
+----------------------------------------------------------------+
|                           DATA PLANE                           |
|                                                                |
|  +-------------------+       +-------------------+             |
|  | Worker Node-1     |       | Worker Node-2     |             |
|  | kubelet           |       | kubelet           |             |
|  | container runtime |       | container runtime |             |
|  | Pod: order-api    |       | Pod: payment-api  |             |
|  +-------------------+       +-------------------+             |
|                                                                |
|  Services, kube-proxy/eBPF rules, CNI networking, volumes       |
+----------------------------------------------------------------+
```

Customer request path:

```text
Customer
   |
   v
Load Balancer / Ingress
   |
   v
Service
   |
   v
Pod
   |
   v
Spring Boot Controller
```

Control request path:

```text
Developer
   |
   v
kubectl
   |
   v
API Server
   |
   v
etcd / controllers / scheduler
```

Two different paths.

One cluster.

Different responsibilities.

---

# 6. Control Plane Responsibilities

The control plane is responsible for cluster decisions and cluster memory.

It handles:

```text
1. Accepting Kubernetes API requests
2. Validating YAML/object schemas
3. Enforcing authentication and authorization
4. Storing desired and observed state
5. Watching for state changes
6. Running controllers
7. Scheduling Pods to Nodes
8. Maintaining object relationships
9. Triggering reconciliation
```

The control plane does not normally:

```text
- serve your customer HTTP requests
- run your Spring Boot container
- store your application database rows
- execute your business logic
```

Diagram:

```text
Control Plane Jobs

+-----------------------+
| receive desired state |
+-----------+-----------+
            |
            v
+-----------------------+
| store cluster state   |
+-----------+-----------+
            |
            v
+-----------------------+
| compare desired/actual|
+-----------+-----------+
            |
            v
+-----------------------+
| make decisions        |
+-----------+-----------+
            |
            v
+-----------------------+
| instruct data plane   |
+-----------------------+
```

One sentence:

```text
Control plane is the brain and memory of Kubernetes.
```

---

# 7. Data Plane Responsibilities

The data plane is responsible for actual workload execution.

It handles:

```text
1. Running Pods
2. Pulling container images
3. Starting containers
4. Restarting containers according to policy
5. Exposing app ports
6. Passing traffic to Pods
7. Mounting volumes
8. Reporting node and pod status
9. Enforcing network rules
10. Using CPU and memory resources
```

Data plane components include:

```text
Worker Node
kubelet
container runtime
CNI plugin
kube-proxy or eBPF datapath
Pods
Containers
Volumes
Node OS/kernel
```

ASCII:

```text
Worker Node
+------------------------------------------------+
| kubelet                                        |
|   watches assigned Pods                        |
|   talks to container runtime                   |
|                                                |
| container runtime                              |
|   pulls image                                  |
|   starts container                             |
|                                                |
| Pods                                           |
|   order-service                                |
|   payment-service                              |
|                                                |
| networking                                     |
|   CNI + Service rules                          |
+------------------------------------------------+
```

One sentence:

```text
Data plane is where applications actually live and serve traffic.
```

---

# 8. Component Mapping Table

```text
+----------------------+----------------+--------------------------------+
| Component            | Plane          | Main Job                       |
+----------------------+----------------+--------------------------------+
| API Server           | Control Plane  | Kubernetes front door          |
| etcd                 | Control Plane  | Cluster state database         |
| Scheduler            | Control Plane  | Chooses node for pending Pod   |
| Controller Manager   | Control Plane  | Runs reconciliation loops      |
| Cloud Controller     | Control Plane  | Talks to cloud provider APIs   |
| kubelet              | Data Plane     | Node agent, starts Pods        |
| Container Runtime    | Data Plane     | Runs containers                |
| Pod                  | Data Plane     | Runtime unit                   |
| Service datapath     | Data Plane     | Routes traffic to Pods         |
| CNI plugin           | Data Plane     | Pod networking                 |
| Node OS/kernel       | Data Plane     | CPU, memory, network, storage  |
+----------------------+----------------+--------------------------------+
```

Do not memorize the table blindly.

Ask this question:

```text
Does this component decide cluster state?
Or does it execute workload traffic/runtime?
```

Examples:

```text
Scheduler chooses node       -> Control Plane
kubelet starts container     -> Data Plane
etcd stores Pod object       -> Control Plane
Pod handles HTTP request     -> Data Plane
Service routes traffic       -> Data Plane
API Server validates YAML    -> Control Plane
```

This reasoning is more useful than memorization.

---

# 9. API Server In The Control Plane

The API Server is the front door.

Everything that changes Kubernetes state goes through it.

```text
kubectl
controllers
scheduler
kubelet
operators
admission webhooks
```

All talk to API Server.

Diagram:

```text
                    +-------------+
Developer -------->|             |
Controller ------->| API Server  |<------ Scheduler
Kubelet ---------->|             |
Operator --------->|             |
                    +------+------+ 
                           |
                           v
                         etcd
```

Important:

```text
Nobody should directly edit etcd.
```

API Server handles:

```text
authentication
authorization
validation
admission control
object versioning
watch streams
state persistence through etcd
```

Production meaning:

```text
If API Server is down:
- kubectl may fail
- new deployments may fail
- controllers may not observe changes
- scheduler may not bind new Pods

But existing Pods may keep serving traffic.
```

Why?

Because running containers are already executing on worker nodes.

---

# 10. etcd In The Control Plane

etcd is Kubernetes memory.

It stores:

```text
Deployments
ReplicaSets
Pods
Services
ConfigMaps
Secrets
Nodes
Events
Leases
Controller state
```

Mental model:

```text
API Server = front door
etcd       = memory behind the door
```

ASCII:

```text
kubectl apply deployment.yaml
        |
        v
+----------------+
| API Server     |
+-------+--------+
        |
        | persist object
        v
+----------------+
| etcd           |
| /deployments   |
| /pods          |
| /services      |
+----------------+
```

Wrong thinking:

```text
Pod runs inside etcd
```

Correct thinking:

```text
Pod object is stored in etcd.
Pod container runs on a Node.
```

If etcd is unhealthy, the control plane cannot reliably remember or update cluster state.

But etcd does not contain your app memory heap, Java threads, HTTP requests, or database connections.

Your Spring Boot runtime is in the data plane.

---

# 11. Scheduler In The Control Plane

Scheduler answers one question:

```text
Where should this Pod run?
```

It does not start containers.

Input:

```text
Pod object exists
spec.nodeName is empty
```

Scheduler checks:

```text
CPU requests
memory requests
node capacity
taints and tolerations
node affinity
pod affinity/anti-affinity
topology spread
volume constraints
```

Then it binds the Pod to a Node.

ASCII:

```text
Pending Pod
  name: order-service-abc
  nodeName: <empty>
        |
        v
Scheduler
  filter nodes
  score nodes
  pick best node
        |
        v
Bound Pod
  nodeName: worker-2
```

After scheduling:

```text
kubelet on worker-2 sees the assigned Pod
```

Then kubelet starts the container.

Important split:

```text
Scheduler decides placement.
Kubelet executes placement.
```

If scheduler is down:

```text
Existing Pods continue running.
New unscheduled Pods remain Pending.
```

---

# 12. Controller Manager In The Control Plane

Controllers are reconciliation loops.

They continuously compare desired state and actual state.

Pseudo-code:

```text
while true:
    desired = read_spec()
    actual  = read_status()

    if desired != actual:
        take_action()
```

Controller examples:

```text
Deployment Controller
ReplicaSet Controller
Node Controller
EndpointSlice Controller
Job Controller
Namespace Controller
ServiceAccount Controller
```

Diagram:

```text
+-------------------+
| Watch API Server  |
+---------+---------+
          |
          v
+-------------------+
| Compare state     |
+---------+---------+
          |
          v
+-------------------+
| Create/update/delete objects |
+---------+---------+
          |
          v
+-------------------+
| Repeat forever    |
+-------------------+
```

Example:

```text
Deployment spec says replicas = 3
ReplicaSet actual has 2 Pods
Controller creates one Pod object
```

The controller does not SSH into nodes.

It writes desired changes to the API Server.

The data plane reacts later.

---

# 13. Kubelet In The Data Plane

Kubelet is the local node manager.

It runs on every worker node.

It asks:

```text
Which Pods are assigned to my Node?
```

Then it works locally:

```text
pull image
create container
mount volumes
configure Pod sandbox
run liveness/readiness/startup probes
restart failed containers
report status
```

ASCII:

```text
Control Plane
    |
    | Pod assigned to worker-1
    v
Worker Node
+----------------------------------+
| kubelet                          |
|   sees Pod assigned to this node |
|   asks runtime to start it       |
|                                  |
| container runtime                |
|   pulls image                    |
|   starts container               |
|                                  |
| Pod                              |
|   java -jar order-service.jar    |
+----------------------------------+
```

Important:

```text
kubelet belongs to the data plane,
but it communicates with the control plane.
```

It is the bridge between decision and execution.

If kubelet cannot reach API Server temporarily:

```text
Existing containers may keep running.
Status updates may stop.
New instructions may not arrive.
```

---

# 14. Container Runtime In The Data Plane

The container runtime actually runs containers.

Examples:

```text
containerd
CRI-O
```

Kubelet does not directly execute your Java process.

It asks the runtime.

Flow:

```text
kubelet
  |
  | CRI request
  v
container runtime
  |
  | create container
  v
Linux kernel namespaces + cgroups
  |
  v
java -jar app.jar
```

ASCII:

```text
Pod Spec
  image: order-service:1.0.0
        |
        v
kubelet
        |
        v
container runtime
        |
        v
container process
        |
        v
Spring Boot application
```

This is data plane work because it consumes CPU, memory, disk, network, and serves actual application logic.

If image pull fails, the problem is data plane execution, even though the Pod object exists in the control plane.

Status example:

```text
ImagePullBackOff
```

Meaning:

```text
Control plane accepted the desired Pod.
Data plane could not pull/start the container image.
```

---

# 15. Service Traffic Is Data Plane

A Kubernetes Service gives stable access to changing Pods.

```text
Client -> Service -> ready Pods
```

Service object is stored in the control plane.

But packet forwarding is data plane.

Diagram:

```text
Control Plane Side

Service object:
  name: order-service
  selector: app=order-service

EndpointSlice object:
  Pod IPs:
    10.1.1.5
    10.1.2.7

Data Plane Side

Client packet
    |
    v
Service virtual IP / DNS
    |
    v
node networking rules
    |
    v
Pod IP
```

Important split:

```text
Service definition    = control plane object
Service packet route  = data plane behavior
```

If API Server is temporarily down, existing Service datapath rules may still route traffic.

If kube-proxy/eBPF datapath is broken, API Server may look healthy but traffic may fail.

So never debug only one plane.

---

# 16. CNI Networking In The Data Plane

CNI means Container Network Interface.

It gives Pods networking.

It handles:

```text
Pod IP allocation
network interfaces
routing
network policy enforcement
cross-node Pod traffic
```

Examples:

```text
Calico
Cilium
Flannel
Weave Net
```

ASCII:

```text
Pod A on Node-1                         Pod B on Node-2
+-------------+                         +-------------+
| 10.1.1.5    |                         | 10.1.2.9    |
+------+------+                         +------+------+
       |                                       ^
       v                                       |
+-------------+      node network       +-------------+
| CNI rules   |------------------------>| CNI rules   |
+-------------+                         +-------------+
```

Control plane stores desired network objects like:

```text
Pods
Services
NetworkPolicies
EndpointSlices
```

Data plane enforces actual packet movement.

If NetworkPolicy blocks traffic, your API Server may still be perfect.

Your failure is in the data plane traffic path.

---

# 17. Spring Boot Order Service Example

Application:

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
    @GetMapping("/orders/health-business")
    public String businessHealth() {
        return "order-service business path ok";
    }
}
```

Kubernetes Deployment:

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
```

Control plane interpretation:

```text
Desired state:
3 Pods should exist for order-service image.
```

Data plane execution:

```text
3 Java processes run on worker nodes.
They listen on port 8080.
They serve HTTP requests.
```

Diagram:

```text
Deployment YAML
    |
    v
CONTROL PLANE stores desired state
    |
    v
Scheduler assigns Pods
    |
    v
DATA PLANE starts containers
    |
    v
java -jar order-service.jar
```

---

# 18. Full Dry Run: kubectl apply

Command:

```bash
kubectl apply -f order-deployment.yaml
```

Step-by-step:

```text
1. kubectl sends request to API Server.

2. API Server authenticates the user.

3. API Server checks authorization.

4. API Server validates Deployment schema.

5. Admission controllers may mutate or reject object.

6. API Server stores Deployment object in etcd.

7. Deployment Controller watches new Deployment.

8. Deployment Controller creates ReplicaSet object.

9. ReplicaSet Controller sees desired replicas = 3.

10. ReplicaSet Controller creates 3 Pod objects.

11. Scheduler sees Pods with no nodeName.

12. Scheduler picks worker nodes.

13. API Server records Pod-to-Node binding.

14. Kubelet on each selected node sees assigned Pod.

15. Kubelet asks container runtime to pull image.

16. Runtime starts container.

17. Spring Boot starts.

18. Kubelet reports Pod status.

19. EndpointSlice controller adds ready Pod IPs.

20. Service datapath can send traffic.
```

Plane view:

```text
Steps 1-13  mostly Control Plane
Steps 14-20 mostly Data Plane + status feedback
```

One picture:

```text
kubectl -> API Server -> etcd -> controllers -> scheduler
                                      |
                                      v
                              kubelet -> runtime -> Pod
```

---

# 19. Full Dry Run: Customer HTTP Request

Customer request:

```text
GET /orders/health-business
```

Flow:

```text
1. Customer reaches external Load Balancer.

2. Load Balancer forwards request to Ingress or NodePort.

3. Ingress controller or gateway chooses Service.

4. Service datapath chooses a ready Pod endpoint.

5. Packet reaches Pod IP.

6. Container receives TCP connection.

7. Spring Boot embedded Tomcat handles request.

8. Controller method returns response.
```

ASCII:

```text
Customer
   |
   v
External LB
   |
   v
Ingress / Gateway
   |
   v
Service: order-service
   |
   v
Pod IP: 10.1.1.5:8080
   |
   v
Spring Boot Controller
```

Notice what is not in the hot path:

```text
API Server is not handling this HTTP request.
etcd is not queried for every request.
Scheduler is not involved per request.
Controller Manager is not routing packets.
```

This is why customer traffic can continue even when some control plane functions are temporarily impaired.

Existing data plane rules and running processes serve the request.

---

# 20. Spec, Status, And Plane Boundary

In Kubernetes YAML:

```text
spec   = desired state
status = observed state
```

Plane mapping:

```text
Control Plane stores and manages spec.
Data Plane produces reality.
Control Plane records status from reality.
```

Example:

```yaml
spec:
  replicas: 3

status:
  replicas: 3
  readyReplicas: 2
  availableReplicas: 2
```

Meaning:

```text
Control plane knows desired = 3.
Data plane currently has only 2 ready.
Controllers keep reconciling.
```

ASCII:

```text
spec.replicas = 3
       |
       v
Control Plane decision
       |
       v
Data Plane runs Pods
       |
       v
status.readyReplicas = 2
       |
       v
Control Plane sees mismatch
```

This loop crosses the boundary continuously.

Do not think control plane and data plane are disconnected.

They are separate responsibilities, connected by watch/report loops.

---

# 21. What Happens If API Server Goes Down?

Scenario:

```text
API Server unavailable for a short time
```

Likely symptoms:

```text
kubectl get pods fails
kubectl apply fails
controllers cannot reliably update state
scheduler cannot process new pending Pods
kubelets cannot report fresh status
```

But existing workloads may continue:

```text
Existing containers keep running.
Existing Service rules may keep routing.
Existing app traffic may still work.
```

ASCII:

```text
kubectl ----X----> API Server

Customer ---> Service ---> Pod ---> Spring Boot
                still possibly works
```

Why?

```text
A running Linux process does not need API Server for every CPU instruction.
A Service datapath does not query API Server for every packet.
```

But do not misunderstand.

Long API Server outage is serious.

It affects:

```text
new deployments
autoscaling decisions
node status freshness
controller reconciliation
new scheduling
operator behavior
cluster management
```

Production conclusion:

```text
Control plane outage may not instantly stop data plane traffic,
but it reduces the cluster's ability to adapt, heal, and change.
```

---

# 22. What Happens If Scheduler Goes Down?

Scenario:

```text
Scheduler unavailable
```

Existing Pods:

```text
continue running
```

New Pods:

```text
may stay Pending
```

Example:

```text
Deployment scaled from 3 to 5 replicas
ReplicaSet creates 2 Pod objects
Scheduler unavailable
Pods do not get nodeName
Result: Pending
```

Diagram:

```text
ReplicaSet creates Pod
        |
        v
Pending Pod
  nodeName: empty
        |
        X scheduler down
        |
        v
No node assigned
```

Commands:

```bash
kubectl get pods
kubectl describe pod <pending-pod>
```

You may see events like:

```text
0/5 nodes are available
```

or no fresh scheduling events.

Plane lesson:

```text
Scheduler is control plane.
It affects placement, not already-running Java execution.
```

---

# 23. What Happens If Controller Manager Goes Down?

Scenario:

```text
Controller Manager unavailable
```

Existing Pods may continue running.

But reconciliation stops or slows.

Examples:

```text
Deployment rollout may not progress.
ReplicaSet may not create replacement Pods.
Node state updates may be delayed.
EndpointSlice updates may be delayed.
Job completion handling may be delayed.
```

ASCII:

```text
Desired replicas = 3
Actual replicas  = 2
        |
        X controller loop unavailable
        |
        v
Mismatch remains longer
```

Production meaning:

```text
The cluster becomes less self-healing.
```

It is like a restaurant where the branch kitchens are still cooking, but head office is not noticing staffing problems.

Existing orders may continue.

New corrections may not happen.

This is why control plane health matters even when customer traffic still appears fine.

---

# 24. What Happens If etcd Has Problems?

etcd problems are dangerous because etcd is cluster memory.

Symptoms may include:

```text
API Server slow or failing
writes failing
leader election issues
watch instability
controllers lagging
cluster state inconsistencies from client perspective
```

ASCII:

```text
kubectl apply
    |
    v
API Server
    |
    X cannot persist to etcd reliably
    |
    v
Desired state not safely stored
```

Existing running Pods:

```text
may continue running for some time
```

But the cluster cannot safely manage desired state.

Do not treat etcd as just another database.

For Kubernetes, etcd is the memory of the control plane.

Production mindset:

```text
Backup etcd.
Monitor etcd latency.
Protect etcd disk I/O.
Avoid direct writes.
Use highly available control plane design.
```

Data plane can survive short-term memory trouble.

But long-term, a cluster without reliable memory cannot be trusted.

---

# 25. What Happens If A Worker Node Goes Down?

Worker node is data plane.

Scenario:

```text
worker-2 dies
```

Immediate data plane effect:

```text
Pods on worker-2 stop serving traffic.
```

Control plane reaction:

```text
Node heartbeat missing
Node marked NotReady
Pods eventually considered unavailable
ReplicaSet creates replacement Pods
Scheduler places them on healthy nodes
Kubelets start replacements
```

ASCII:

```text
Before:
worker-1: order-A
worker-2: order-B
worker-3: order-C

Failure:
worker-2: X

After reconciliation:
worker-1: order-A, order-D
worker-3: order-C
```

Plane lesson:

```text
Worker failure is data plane failure.
Recovery requires control plane decisions.
```

If control plane is also unhealthy during node failure, recovery is delayed.

That is why production Kubernetes separates:

```text
highly available control plane
multiple worker nodes
pod anti-affinity
proper readiness
replica count > 1
```

---

# 26. What Happens If kubelet Fails?

kubelet is data plane node agent.

Scenario:

```text
kubelet process on worker-1 fails
```

Possible effects:

```text
Node stops reporting fresh status.
Pod status becomes stale.
New Pod instructions may not be executed.
Health probes may stop being managed.
Container restart management may be affected.
```

Existing containers:

```text
may continue running because container runtime already started them
```

But Kubernetes loses local management visibility.

ASCII:

```text
API Server
    |
    X kubelet not reporting
    |
worker-1
+---------------------+
| containers running? |
| maybe yes           |
| kubelet dead        |
+---------------------+
```

Debug:

```bash
kubectl get nodes
kubectl describe node worker-1
ssh worker-1
systemctl status kubelet
journalctl -u kubelet
crictl ps
```

Plane lesson:

```text
The container process and kubelet process are related,
but they are not the same process.
```

---

# 27. Control Plane HA Mental Model

Production clusters usually run multiple control plane replicas.

```text
API Server replicas
etcd members
controller-manager leader election
scheduler leader election
```

ASCII:

```text
                 Load Balancer
                      |
        +-------------+-------------+
        |             |             |
        v             v             v
+--------------+ +--------------+ +--------------+
| API Server 1 | | API Server 2 | | API Server 3 |
+------+-------+ +------+-------+ +------+-------+
       |                |                |
       +----------------+----------------+
                        |
                        v
              +-------------------+
              | etcd cluster      |
              | member1 member2  |
              | member3          |
              +-------------------+
```

Controller manager and scheduler typically use leader election.

```text
Multiple instances exist.
Only one active leader performs certain decisions.
If leader dies, another takes over.
```

This avoids two schedulers fighting over the same work.

Mental model:

```text
High availability means control plane can lose one component
without losing the whole cluster brain.
```

But HA control plane does not remove the need for healthy data plane.

You need both.

---

# 28. Data Plane HA Mental Model

Data plane high availability means your app can survive node and Pod failures.

Use:

```text
multiple replicas
multiple worker nodes
readiness probes
PodDisruptionBudgets
anti-affinity / topology spread
horizontal scaling
safe rollout strategy
```

Bad deployment:

```text
replicas = 1
single worker node
no readiness probe
```

Failure:

```text
one Pod dies = outage
```

Better deployment:

```text
replicas = 3
spread across nodes
readiness protects traffic
rolling update controlled
```

ASCII:

```text
Bad:
worker-1: order-service-A
worker-2:
worker-3:

Better:
worker-1: order-service-A
worker-2: order-service-B
worker-3: order-service-C
```

Data plane HA is about keeping customer traffic alive.

Control plane HA is about keeping cluster decisions alive.

You need both for serious production.

---

# 29. Control Plane vs Data Plane During Rolling Update

You update image:

```text
order-service:1.0.0 -> order-service:1.1.0
```

Control plane work:

```text
Deployment object updated
Deployment Controller creates new ReplicaSet
ReplicaSet Controller creates new Pods
Scheduler assigns new Pods
EndpointSlice updates ready endpoints
```

Data plane work:

```text
kubelet pulls new image
container runtime starts new containers
Spring Boot starts
readiness probe decides traffic eligibility
Service routes only to ready Pods
old Pods terminate gradually
```

ASCII:

```text
CONTROL PLANE
Deployment v2
   |
   v
New ReplicaSet
   |
   v
New Pod objects
   |
   v
Schedule to nodes

DATA PLANE
Pull image
   |
   v
Start Java process
   |
   v
Readiness pass
   |
   v
Receive traffic
```

If new app version fails readiness:

```text
Control plane may show rollout not complete.
Data plane protects traffic by not sending requests to unready Pods.
```

This is why readiness is a data plane safety signal used by control plane objects.

---

# 30. Spring Boot Readiness Example

Spring Boot may be running but not ready.

Example:

```text
Tomcat started
DB connection failed
Kafka not reachable
cache warmup not done
```

Actuator config:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
  health:
    readinessstate:
      enabled: true
    livenessstate:
      enabled: true
```

Kubernetes readiness:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
```

Flow:

```text
Pod starts
   |
   v
Java process starts
   |
   v
readiness endpoint DOWN
   |
   v
Service does not send traffic
   |
   v
DB connected
   |
   v
readiness endpoint UP
   |
   v
Service sends traffic
```

Plane interpretation:

```text
Data plane signal: app is ready or not.
Control plane object/status: Pod Ready condition and endpoints.
Data plane action: traffic goes only to ready endpoints.
```

---

# 31. Debugging Model: First Ask Which Plane

When Kubernetes breaks, ask:

```text
Is this a control plane problem?
Is this a data plane problem?
Or is this a boundary problem?
```

Control plane symptoms:

```text
kubectl cannot connect
API requests timeout
Pods not scheduled
Deployments not reconciling
objects not updating
controller lag
etcd errors
```

Data plane symptoms:

```text
Pod CrashLoopBackOff
ImagePullBackOff
Node NotReady
Service has no traffic
DNS failure inside Pod
NetworkPolicy block
CPU/memory throttling
app latency high
```

Boundary symptoms:

```text
kubelet cannot reach API Server
status stale
endpoints not updated
node heartbeat missing
admission webhook blocks deployments
```

ASCII:

```text
Problem
   |
   +-- kubectl / scheduling / reconciliation? --> Control Plane
   |
   +-- app traffic / Pod runtime / networking? -> Data Plane
   |
   +-- reports/watch/status bridge broken? ----> Boundary
```

This question prevents random debugging.

---

# 32. Debugging Path: kubectl Works But App Down

Symptom:

```text
kubectl get pods works
But customer request fails
```

Likely not full control plane outage.

Check data plane path:

```bash
kubectl get pods -o wide
kubectl get svc
kubectl get endpoints order-service
kubectl describe svc order-service
kubectl logs deploy/order-service
kubectl describe pod <pod>
```

Layer-by-layer:

```text
1. Are Pods Running?
2. Are Pods Ready?
3. Does Service selector match labels?
4. Are endpoints populated?
5. Is targetPort correct?
6. Is app listening on correct port?
7. Is NetworkPolicy blocking?
8. Is Ingress routing correct?
9. Is external load balancer healthy?
```

ASCII:

```text
Client
  |
  v
Ingress
  |
  v
Service
  |
  v
Endpoint
  |
  v
Pod
  |
  v
Container port
  |
  v
Spring Boot
```

If `kubectl` works, control plane visibility exists.

Now follow data plane traffic.

---

# 33. Debugging Path: App Works But kubectl Fails

Symptom:

```text
Customer app works
kubectl get pods fails
```

This points toward control plane access or API issue.

Possible causes:

```text
API Server unavailable
kubeconfig wrong
network path to API Server broken
certificate expired
authorization issue
control plane load balancer problem
```

Commands/checks:

```bash
kubectl cluster-info
kubectl auth can-i get pods
kubectl config current-context
kubectl config view
curl -k https://<api-server>/readyz
```

Plane picture:

```text
kubectl ----X----> API Server

Customer ---> Service ---> Pod ---> App
                works
```

Important lesson:

```text
Do not restart Pods just because kubectl fails.
First identify whether the failure is management-path or traffic-path.
```

This is a senior production habit.

---

# 34. Debugging Path: Pod Pending

Symptom:

```text
Pod stays Pending
```

This is usually a control-plane scheduling or resource issue.

Check:

```bash
kubectl describe pod <pod>
kubectl get nodes
kubectl describe node <node>
```

Common causes:

```text
not enough CPU
not enough memory
taint not tolerated
nodeSelector mismatch
affinity impossible
PVC not bound
scheduler unavailable
quota exceeded
```

ASCII:

```text
Pod object exists
    |
    v
Needs Node
    |
    v
Scheduler checks constraints
    |
    X no valid node
    |
    v
Pending
```

Plane interpretation:

```text
The app has not entered data plane runtime yet.
It is stuck before kubelet can execute it.
```

That is why checking application logs may show nothing.

There is no container yet.

---

# 35. Debugging Path: CrashLoopBackOff

Symptom:

```text
Pod scheduled
Container starts
Container crashes repeatedly
```

This is data plane runtime plus application failure.

Commands:

```bash
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl describe pod <pod>
```

Common Spring Boot causes:

```text
missing environment variable
wrong DB password
cannot reach database
Flyway migration failure
port mismatch
OutOfMemoryError
bad JVM options
wrong active profile
```

ASCII:

```text
Scheduler assigned node
    |
    v
kubelet starts container
    |
    v
Spring Boot starts
    |
    X app crashes
    |
    v
kubelet restarts with backoff
```

Plane interpretation:

```text
Control plane did its job.
Data plane started execution.
Application failed at runtime.
```

Kubernetes can restart bad containers.

It cannot automatically fix bad configuration or broken code.

---

# 36. Debugging Path: Service Has No Endpoints

Symptom:

```bash
kubectl get endpoints order-service

NAME            ENDPOINTS
order-service   <none>
```

This is usually a label/readiness boundary issue.

Check Service selector:

```bash
kubectl describe svc order-service
```

Check Pod labels:

```bash
kubectl get pods --show-labels
```

Example bug:

```yaml
# Pod label
app: order

# Service selector
app: order-service
```

No match.

ASCII:

```text
Service selector: app=order-service
        |
        v
Pods:
  pod-A app=order   X
  pod-B app=order   X
  pod-C app=order   X

Result: no endpoints
```

Another cause:

```text
Pods exist but readiness probe failing.
```

Then Service may not route traffic.

Plane lesson:

```text
Control plane object exists.
Data plane traffic fails because endpoint membership is empty.
```

---

# 37. Debugging Path: DNS Fails Inside Pod

Symptom:

```text
order-service cannot call payment-service
```

Inside Pod:

```bash
nslookup payment-service
curl http://payment-service:8080/health
```

Possible causes:

```text
CoreDNS issue
wrong namespace
Service does not exist
NetworkPolicy blocks DNS
Pod DNS config broken
node networking problem
```

ASCII:

```text
order Pod
   |
   | DNS query payment-service
   v
CoreDNS Service
   |
   v
Kubernetes Service record
   |
   v
payment-service ClusterIP
```

Plane interpretation:

```text
Service object is control plane state.
DNS query and packet movement are data plane behavior.
CoreDNS itself runs as Pods, so it is part of the data plane workload supporting cluster networking.
```

Debug:

```bash
kubectl get svc -n kube-system kube-dns
kubectl get pods -n kube-system -l k8s-app=kube-dns
kubectl logs -n kube-system deploy/coredns
```

---

# 38. Production Story: Control Plane Slow, Traffic Fine

A team sees alerts:

```text
kubectl commands timing out
API Server latency high
etcd disk latency high
```

But customer traffic graphs show:

```text
HTTP 200 rate normal
p99 latency normal
error rate normal
```

Junior reaction:

```text
Restart all Pods?
```

Senior reaction:

```text
Do not touch data plane blindly.
Separate management-path problem from traffic-path problem.
```

Picture:

```text
Management path degraded:
Engineer -> kubectl -> API Server -> etcd   SLOW

Traffic path healthy:
Customer -> LB -> Service -> Pod -> App     OK
```

Actions:

```text
check API Server metrics
check etcd fsync latency
check control plane node CPU/memory
check admission webhooks
avoid unnecessary deploys during control plane instability
```

Lesson:

```text
When the brain is slow, do not randomly disturb healthy muscles.
```

---

# 39. Production Story: Control Plane Healthy, Traffic Broken

Another incident:

```text
kubectl get pods works
Deployments look healthy
API Server is fine
But users get 503
```

Investigation:

```bash
kubectl get endpoints checkout-service
```

Result:

```text
<none>
```

Cause:

```text
New readiness probe path was wrong:
/actuator/health/ready
instead of
/actuator/health/readiness
```

Control plane accepted YAML.

Data plane app started.

Readiness failed.

Service removed Pods from endpoints.

ASCII:

```text
Pod Running
   |
   X readiness failing
   |
   v
Not Ready
   |
   v
No Service endpoint
   |
   v
503 to users
```

Lesson:

```text
A healthy control plane does not guarantee healthy application traffic.
```

You must debug the data plane path.

---

# 40. Production Story: Node Failure During Rollout

Scenario:

```text
Rolling update starts
One worker node dies
New Pods need scheduling
```

If control plane is healthy:

```text
Node marked NotReady
ReplicaSet creates replacements
Scheduler chooses healthy nodes
Rollout may continue if capacity exists
```

If cluster has poor capacity:

```text
Pods remain Pending
rollout stalls
availability may drop
```

ASCII:

```text
worker-1: old pod
worker-2: X failed
worker-3: new pod

Need replacement
    |
    v
Scheduler checks capacity
    |
    +-- enough capacity -> new pod starts
    |
    +-- no capacity ----> Pending
```

Production design:

```text
replica count >= 3
spread across nodes
PodDisruptionBudget
resource requests realistic
cluster autoscaler configured
readiness probes correct
```

Plane lesson:

```text
Data plane failure requires control plane recovery decisions.
Control plane recovery needs enough data plane capacity.
```

---

# 41. Interview Answer: Control Plane vs Data Plane

Strong answer:

```text
In Kubernetes, the control plane is responsible for cluster decisions, state storage, scheduling, and reconciliation. It includes the API Server, etcd, scheduler, and controller manager. The data plane is where workloads actually run and traffic is served. It includes worker nodes, kubelet, container runtime, Pods, CNI networking, and Service datapath.

The control plane decides what should happen. The data plane executes it. For example, when I apply a Deployment, the API Server stores it, controllers create Pods, the scheduler assigns nodes, and then kubelet on the selected nodes starts containers. Customer HTTP traffic usually flows through load balancer, ingress/service, and Pods, not through the API Server.
```

Short version for interview:

```text
Control plane = brain and memory.
Data plane = muscles and traffic path.
```

But always add production nuance:

```text
If control plane has issues, existing workloads may continue, but new scheduling, deployments, reconciliation, and autoscaling can be affected. If data plane has issues, customer traffic and running workloads are affected directly.
```

---

# 42. Common Interview Questions

## What components are in the control plane?

```text
API Server, etcd, scheduler, controller manager, and sometimes cloud controller manager.
```

## What components are in the data plane?

```text
Worker nodes, kubelet, container runtime, Pods, CNI networking, Service datapath, and node OS/kernel resources.
```

## Does API Server serve application traffic?

```text
No. API Server serves Kubernetes API traffic. Application traffic goes through load balancers, ingress/gateway, Services, and Pods.
```

## What happens if the scheduler is down?

```text
Existing Pods continue running, but new Pods that need placement may stay Pending.
```

## What happens if API Server is down?

```text
kubectl and control operations fail or degrade. Existing workloads may keep running for some time, but the cluster cannot reliably accept changes or reconcile new state.
```

## Is kubelet control plane or data plane?

```text
kubelet is data plane. It runs on worker nodes and executes assigned Pods, but it communicates with the API Server.
```

## Is Service control plane or data plane?

```text
The Service object is control plane state. The actual packet routing/load balancing behavior is data plane.
```

## Why is this distinction useful?

```text
It helps debug incidents. If kubectl fails but traffic works, suspect control plane or management path. If kubectl works but users get errors, follow the data plane traffic path.
```

---

# 43. Beginner Mistakes

```text
Mistake 1:
Thinking API Server runs application traffic.
Correct:
API Server handles Kubernetes API traffic.

Mistake 2:
Thinking scheduler starts containers.
Correct:
Scheduler chooses nodes. Kubelet starts containers.

Mistake 3:
Thinking etcd stores application data.
Correct:
etcd stores Kubernetes cluster state, not your order rows.

Mistake 4:
Thinking kubectl failure means all apps are down.
Correct:
Management path may fail while data path still works.

Mistake 5:
Thinking Running means reachable.
Correct:
Service endpoints, readiness, networking, and ports must be correct.

Mistake 6:
Ignoring worker node health.
Correct:
Data plane health directly affects user traffic.

Mistake 7:
Debugging randomly.
Correct:
First classify: control plane, data plane, or boundary.
```

---

# 44. Command Cheat Sheet By Plane

Control plane checks:

```bash
kubectl cluster-info
kubectl get componentstatuses   # older/deprecated in many clusters, but seen in interviews
kubectl get --raw='/readyz?verbose'
kubectl get nodes
kubectl get events -A --sort-by=.lastTimestamp
kubectl auth can-i get pods
kubectl api-resources
kubectl get deployment order-service -o yaml
```

Data plane checks:

```bash
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl exec -it <pod> -- sh
kubectl top pod
kubectl top node
kubectl get svc
kubectl get endpoints
kubectl get endpointslices
```

Node-level checks:

```bash
systemctl status kubelet
journalctl -u kubelet
crictl ps
crictl logs <container-id>
ip route
iptables -t nat -L
```

Traffic checks:

```bash
kubectl run curl --image=curlimages/curl -it --rm -- sh
curl http://order-service:8080/actuator/health
nslookup order-service
```

Mindset:

```text
Use commands according to the plane you are debugging.
```

---

# 45. One Picture To Remember

```text
                            MANAGEMENT PATH

Developer
   |
   v
kubectl
   |
   v
+------------------------------+
|        CONTROL PLANE         |
|                              |
|  API Server                  |
|  etcd                        |
|  Controller Manager          |
|  Scheduler                   |
|                              |
|  decides desired cluster     |
|  state and reconciliation    |
+---------------+--------------+
                |
                | assignments / desired actions
                v
+------------------------------+
|          DATA PLANE          |
|                              |
|  Worker Nodes                |
|  kubelet                     |
|  container runtime           |
|  CNI networking              |
|  Service datapath            |
|  Pods / Containers           |
|                              |
|  runs applications           |
|  serves real traffic         |
+---------------+--------------+
                ^
                |
                v
Customer ---> Service/Ingress ---> Pod ---> Spring Boot

Rule:
Control plane makes decisions.
Data plane serves traffic.
```

---

# 46. Final Production Checklist

```text
[ ] I can explain control plane as Kubernetes brain/memory.
[ ] I can explain data plane as runtime/traffic execution.
[ ] I know API Server does not serve app traffic.
[ ] I know scheduler chooses nodes but does not start containers.
[ ] I know kubelet starts assigned Pods on worker nodes.
[ ] I know etcd stores Kubernetes state, not application business data.
[ ] I know Service object and Service datapath are different concepts.
[ ] I can explain why existing Pods may run during short control plane issues.
[ ] I can explain why new Pods may remain Pending if scheduler is unavailable.
[ ] I can debug kubectl failure separately from customer traffic failure.
[ ] I can debug Pod Pending as scheduling/control-plane path.
[ ] I can debug CrashLoopBackOff as runtime/app/data-plane path.
[ ] I can debug no endpoints using labels and readiness.
[ ] I can follow traffic from Ingress to Service to Endpoint to Pod.
[ ] I can answer interview questions with production nuance.
```

---

# 47. Final Memory Hook

Do not memorize Kubernetes as many moving parts.

Remember it like a body:

```text
Control Plane = brain + memory + decision system
Data Plane    = muscles + roads + running work
```

Or like an airport:

```text
Control Plane = control tower
Data Plane    = runways + airplanes + passengers
```

Final sentence:

```text
Kubernetes control plane decides and remembers what the cluster should do; Kubernetes data plane runs the actual Pods and carries real user traffic.
```
