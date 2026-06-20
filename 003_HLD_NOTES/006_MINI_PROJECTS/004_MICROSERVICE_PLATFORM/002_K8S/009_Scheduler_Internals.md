# 009_Scheduler_Internals.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why The Scheduler Exists

A Kubernetes cluster is not one machine.

It is many machines acting like one compute platform.

```text
Cluster
  |
  +-- node-1  CPU: 8 cores   Memory: 32 GB
  +-- node-2  CPU: 4 cores   Memory: 16 GB
  +-- node-3  CPU: 16 cores  Memory: 64 GB
```

When you create a Pod, Kubernetes must answer one very important question:

```text
Where should this Pod run?
```

That is the scheduler's job.

The scheduler does **not** start containers.

The scheduler does **not** pull images.

The scheduler does **not** restart crashed apps.

It only decides placement.

```text
Pod without node
      |
      v
Scheduler chooses best node
      |
      v
Pod assigned to node
      |
      v
Kubelet starts container on that node
```

The simplest mental model:

```text
Scheduler = Kubernetes placement brain
```

If Kubernetes had no scheduler, every human would manually decide:

```text
Run order-service on node-1.
Run payment-service on node-3.
Run batch-worker on node-2.
Do not put memory-heavy services together.
Keep replicas spread across nodes.
Avoid GPU node unless needed.
```

That does not scale.

Kubernetes scheduler automates this placement decision using rules, resources, constraints, priorities, and scoring.

---

# 2. The Wrong Way To Think About Scheduling

Wrong mental model:

```text
Kubernetes randomly puts Pods on nodes.
```

Another wrong mental model:

```text
Scheduler starts my container.
```

Correct mental model:

```text
Scheduler chooses a node.
Kubelet on that node starts the Pod.
```

ASCII:

```text
Wrong:

Scheduler
   |
   v
Starts container

Correct:

Scheduler
   |
   | writes pod.spec.nodeName = node-2
   v
API Server / etcd
   |
   v
Kubelet on node-2
   |
   v
Starts container
```

The scheduler is a decision maker, not a worker.

Think of an airport.

```text
Air traffic controller decides which runway.
Ground crew actually moves the airplane.
```

Kubernetes:

```text
Scheduler decides which node.
Kubelet actually starts the Pod.
```

This distinction matters during debugging.

If a Pod is Pending because no node fits, scheduler is involved.

If a Pod is assigned to a node but image cannot pull, scheduler is no longer the problem.

```text
Pending + no nodeName       -> scheduling problem
Assigned + ImagePullBackOff -> kubelet/image/registry problem
Assigned + CrashLoopBackOff -> app/runtime/config problem
```

---

# 3. One Picture To Start

```text
                    +------------------+
                    | New Pod created  |
                    | nodeName empty   |
                    +--------+---------+
                             |
                             v
                    +------------------+
                    | Scheduler Queue  |
                    +--------+---------+
                             |
                             v
                    +------------------+
                    | Filter Nodes     |
                    | Can this pod run?|
                    +--------+---------+
                             |
                             v
                    +------------------+
                    | Score Nodes      |
                    | Which is best?   |
                    +--------+---------+
                             |
                             v
                    +------------------+
                    | Bind Pod         |
                    | nodeName=node-X  |
                    +--------+---------+
                             |
                             v
                    +------------------+
                    | Kubelet starts   |
                    | containers       |
                    +------------------+
```

The scheduler pipeline is mostly:

```text
Queue -> Filter -> Score -> Bind
```

Remember this before memorizing every scheduling feature.

---

# 4. Real World Analogy: Hotel Room Assignment

Imagine a hotel receptionist.

A guest arrives and asks for a room.

The receptionist checks:

```text
Does the guest need a smoking-free room?
Does the guest need a king bed?
Does the guest need wheelchair access?
Does the guest have VIP priority?
Is the room available?
Is the room clean?
Should family members be nearby?
Should noisy guests be separated?
```

The receptionist does not randomly assign any room.

They first remove impossible rooms.

```text
Filter phase:
Room has no bed? reject.
Room already occupied? reject.
Room does not support wheelchair access? reject.
```

Then they choose the best room among valid rooms.

```text
Score phase:
Room near elevator: +5
Room with sea view: +3
Room on quiet floor: +2
```

Finally they write the assignment.

```text
Guest Mohamed -> Room 407
```

Kubernetes scheduler behaves similarly.

```text
Pod arrives
      |
      v
Reject nodes that cannot run it
      |
      v
Score remaining nodes
      |
      v
Assign Pod to best node
```

---

# 5. Scheduler In The Kubernetes Control Plane

The scheduler is part of the control plane.

```text
CONTROL PLANE

+------------------+
| API Server       |
+------------------+
| etcd             |
+------------------+
| Controller Mgr   |
+------------------+
| Scheduler        |
+------------------+

DATA PLANE

+------------------+
| Worker Nodes     |
| kubelet          |
| Pods             |
+------------------+
```

The scheduler watches the API Server for Pods that do not yet have a node.

A newly created Pod initially looks like this conceptually:

```yaml
spec:
  containers:
    - name: order-service
      image: order-service:1.0.0
  nodeName: null
```

After scheduling:

```yaml
spec:
  nodeName: node-2
```

That is the scheduler's main visible output.

```text
Before scheduling:
Pod exists but has no home.

After scheduling:
Pod has a selected node.
```

---

# 6. Full Pod Placement Flow

When you apply a Deployment:

```text
Deployment -> ReplicaSet -> Pod objects
```

The scheduler only sees the Pod stage.

Full flow:

```text
kubectl apply deployment.yaml
        |
        v
API Server stores Deployment
        |
        v
Deployment Controller creates ReplicaSet
        |
        v
ReplicaSet Controller creates Pod
        |
        v
Pod has no nodeName
        |
        v
Scheduler watches unscheduled Pod
        |
        v
Scheduler chooses node
        |
        v
Scheduler binds Pod to node
        |
        v
Kubelet on node starts container
```

ASCII:

```text
Deployment
    |
    v
ReplicaSet
    |
    v
Pod: order-service-abc
spec.nodeName = <empty>
    |
    v
Scheduler
    |
    v
Pod: order-service-abc
spec.nodeName = node-2
    |
    v
node-2 kubelet starts it
```

Important:

```text
Scheduler does not create the Pod.
ReplicaSet creates the Pod.
Scheduler assigns the Pod.
Kubelet runs the Pod.
```

---

# 7. Scheduler Watches Unscheduled Pods

The scheduler continuously watches for Pods where:

```text
spec.nodeName is empty
```

Pseudo-code mental model:

```text
while true:
    pod = get_next_unscheduled_pod()
    nodes = list_all_nodes()
    feasible = filter(nodes, pod)
    best = score(feasible, pod)
    bind(pod, best)
```

This is not exact production code, but it gives the operating idea.

```text
Unscheduled Pod Queue

+----------------------+
| payment-pod          |
| order-pod            |
| report-job-pod       |
| email-worker-pod     |
+----------------------+
          |
          v
      Scheduler
```

The scheduler works on one placement decision at a time, but the cluster constantly produces more scheduling work.

---

# 8. The Four Core Phases

A simplified scheduler pipeline:

```text
1. Queue
2. Filter
3. Score
4. Bind
```

Detailed:

```text
Queue:
  Which Pod should be scheduled next?

Filter:
  Which nodes are possible?

Score:
  Which possible node is best?

Bind:
  Write the final node assignment.
```

ASCII:

```text
All Nodes:
node-1 node-2 node-3 node-4

Filter:
node-1 rejected: not enough CPU
node-2 accepted
node-3 rejected: taint not tolerated
node-4 accepted

Score:
node-2 = 70
node-4 = 85

Bind:
Pod -> node-4
```

This mental model explains almost every scheduling behavior.

When debugging, ask:

```text
Did the Pod enter scheduling queue?
Did any node pass filtering?
Which scoring preference won?
Did binding succeed?
```

---

# 9. Filter Phase: Can This Pod Run Here?

Filtering removes nodes that cannot run the Pod.

Common filter reasons:

```text
Not enough CPU
Not enough memory
Node has taint not tolerated by Pod
Node selector does not match
Node affinity does not match
Pod anti-affinity conflict
Volume cannot attach to that node
Node is NotReady
Too many Pods already on node
Required port unavailable
```

ASCII:

```text
Pod requirements:
CPU: 2
Memory: 4Gi
nodeSelector: disk=ssd

Nodes:
node-1 CPU free 1   Mem free 8Gi  disk=ssd   -> reject CPU
node-2 CPU free 4   Mem free 8Gi  disk=hdd   -> reject label
node-3 CPU free 8   Mem free 16Gi disk=ssd   -> accept
```

The scheduler does not say:

```text
node-1 is almost okay
```

Filter is strict.

Either the node can run the Pod or it cannot.

---

# 10. Score Phase: Which Node Is Best?

After filtering, multiple nodes may be valid.

Scoring chooses the best one.

Example:

```text
Feasible nodes:
node-2
node-3
node-5
```

Scheduler scores them based on priorities such as:

```text
Better resource balance
Spread Pods across nodes
Honor preferred affinity
Prefer less loaded node
Avoid too much concentration
Prefer topology spread
```

ASCII:

```text
              CPU Balance   Spread   Affinity   Total
node-2            30          20        10        60
node-3            40          25        20        85
node-5            35          10        15        60

Winner: node-3
```

Do not memorize exact scoring plugins first.

Remember:

```text
Filter decides possible.
Score decides best.
```

---

# 11. Bind Phase: The Final Assignment

Binding writes the Pod-to-node decision.

Conceptually:

```yaml
spec:
  nodeName: node-3
```

Once bound, kubelet on node-3 sees:

```text
A Pod is assigned to me.
I must start it.
```

ASCII:

```text
Scheduler
   |
   | bind order-pod -> node-3
   v
API Server
   |
   v
Pod object updated
   |
   v
node-3 kubelet watches assigned Pod
   |
   v
Container runtime pulls image
   |
   v
Spring Boot process starts
```

If binding succeeds, scheduling is done.

If the container later crashes, that is not scheduling.

---

# 12. Requests: Scheduler Uses Reservations, Not Actual Usage

This is one of the most important concepts.

The scheduler uses resource **requests**, not live CPU usage.

Example Pod:

```yaml
resources:
  requests:
    cpu: "500m"
    memory: "512Mi"
  limits:
    cpu: "1"
    memory: "1Gi"
```

Scheduler mainly asks:

```text
Does the node have 500m CPU and 512Mi memory allocatable space remaining?
```

It does not ask:

```text
Is the app currently using only 30m CPU?
```

Mental model:

```text
requests = reservation used for scheduling
limits   = maximum allowed at runtime
usage    = actual live consumption
```

ASCII:

```text
Node allocatable CPU: 4 cores

Scheduled requests:
Pod A: 1 core
Pod B: 1 core
Pod C: 500m

Remaining request capacity:
1.5 cores

New Pod request:
2 cores

Result:
Rejected, even if actual usage is low.
```

Production lesson:

```text
Wrong requests cause wrong scheduling.
```

Too high requests waste cluster capacity.

Too low requests overpack nodes and create noisy-neighbor problems.

---

# 13. Allocatable vs Capacity

Node capacity is raw machine size.

Node allocatable is what Pods can use after system reservation.

```text
Capacity:
  CPU: 8 cores
  Memory: 32Gi

Reserved for kube/system:
  CPU: 1 core
  Memory: 2Gi

Allocatable:
  CPU: 7 cores
  Memory: 30Gi
```

Scheduler uses allocatable.

ASCII:

```text
Physical Node
+----------------------------------+
| Capacity: 8 CPU / 32Gi           |
|                                  |
|  Reserved for OS/Kube: 1CPU/2Gi  |
|  +----------------------------+  |
|  | Allocatable to Pods         |  |
|  | 7 CPU / 30Gi                |  |
|  +----------------------------+  |
+----------------------------------+
```

Command:

```bash
kubectl describe node <node-name>
```

Look for:

```text
Capacity
Allocatable
Allocated resources
```

---

# 14. Spring Boot Example: Bad Requests

Suppose you deploy `order-service`.

Bad YAML:

```yaml
resources:
  requests:
    cpu: "50m"
    memory: "128Mi"
  limits:
    cpu: "2"
    memory: "2Gi"
```

This may look cheap.

But if the app actually needs 700Mi just to start, many Pods may be packed onto one node.

```text
Scheduler thinks:
Each Pod needs only 128Mi.
I can pack many here.

Runtime reality:
Each Spring Boot app uses 700Mi.
Node memory pressure appears.
Pods get evicted or OOMKilled.
```

ASCII:

```text
Scheduler View
node-1 memory allocatable: 8Gi
request per pod: 128Mi
looks like many pods fit

Runtime Reality
Spring Boot actual memory: 700Mi each
node becomes overloaded
```

Better baseline:

```yaml
resources:
  requests:
    cpu: "500m"
    memory: "768Mi"
  limits:
    cpu: "1"
    memory: "1Gi"
```

This is not universal.

Measure your app.

Use real metrics.

---

# 15. Java Code: Memory Behavior Endpoint

A simple Spring Boot endpoint to observe memory pressure during learning:

```java
package com.example.schedulerdemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MemoryController {
    private final List<byte[]> allocations = new ArrayList<>();

    @GetMapping("/allocate")
    public String allocate() {
        // Allocates roughly 50 MB.
        allocations.add(new byte[50 * 1024 * 1024]);

        Runtime rt = Runtime.getRuntime();
        long usedMb = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        long maxMb = rt.maxMemory() / (1024 * 1024);

        return "usedMb=" + usedMb + ", maxMb=" + maxMb;
    }
}
```

Use this only in a learning app.

Kubernetes YAML:

```yaml
resources:
  requests:
    memory: "256Mi"
    cpu: "250m"
  limits:
    memory: "512Mi"
    cpu: "500m"
```

Dry run:

```text
1. Scheduler checks request: 256Mi.
2. Pod is placed on a node with enough allocatable memory.
3. App starts.
4. /allocate increases live memory usage.
5. If usage exceeds limit, container may be OOMKilled.
```

Important:

```text
Scheduler used request.
Runtime enforcement uses limit.
```

---

# 16. Node Selector

`nodeSelector` is the simplest way to force Pods onto nodes with specific labels.

Node label:

```bash
kubectl label node node-1 disk=ssd
```

Pod spec:

```yaml
nodeSelector:
  disk: ssd
```

Meaning:

```text
Only schedule this Pod on nodes where disk=ssd.
```

ASCII:

```text
Pod requires: disk=ssd

node-1 labels: disk=ssd   -> allowed
node-2 labels: disk=hdd   -> rejected
node-3 labels: gpu=true   -> rejected
```

Good use cases:

```text
Run IO-heavy service on SSD nodes
Run GPU workload on GPU nodes
Run compliance workload on special nodes
```

Danger:

```text
Too strict selector can make Pod Pending forever.
```

Debug:

```bash
kubectl get nodes --show-labels
kubectl describe pod <pod>
```

Look for event:

```text
0/3 nodes are available: node(s) didn't match Pod's node affinity/selector.
```

---

# 17. Node Affinity: More Expressive Node Selection

Node affinity is a richer version of nodeSelector.

It supports required and preferred rules.

Required rule:

```yaml
affinity:
  nodeAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      nodeSelectorTerms:
        - matchExpressions:
            - key: disk
              operator: In
              values:
                - ssd
```

Meaning:

```text
The Pod must run on a node where disk is ssd.
```

Preferred rule:

```yaml
affinity:
  nodeAffinity:
    preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 80
        preference:
          matchExpressions:
            - key: zone
              operator: In
              values:
                - zone-a
```

Meaning:

```text
Prefer zone-a if possible, but do not block scheduling.
```

Mental model:

```text
required = filter phase
preferred = score phase
```

ASCII:

```text
requiredDuringScheduling -> Can this node run the Pod?
preferredDuringScheduling -> Is this node better?
```

---

# 18. Taints And Tolerations

Taints are placed on nodes.

Tolerations are placed on Pods.

Taint means:

```text
Do not schedule normal Pods here unless they tolerate this.
```

Example taint:

```bash
kubectl taint nodes node-gpu workload=gpu:NoSchedule
```

Pod toleration:

```yaml
tolerations:
  - key: "workload"
    operator: "Equal"
    value: "gpu"
    effect: "NoSchedule"
```

ASCII:

```text
Node-gpu has taint:
workload=gpu:NoSchedule

Pod A tolerates gpu -> allowed
Pod B no toleration -> rejected
```

Real world analogy:

```text
VIP room has a restricted sign.
Only guests with VIP pass can enter.
```

Kubernetes:

```text
Taint = restricted sign on node
Toleration = pass carried by Pod
```

Important:

```text
Toleration allows scheduling.
It does not force scheduling.
```

To force scheduling to GPU node, combine toleration with nodeSelector or affinity.

---

# 19. Taint Effects

Common taint effects:

```text
NoSchedule
PreferNoSchedule
NoExecute
```

Meaning:

```text
NoSchedule:
  New Pods that do not tolerate are not scheduled there.

PreferNoSchedule:
  Avoid scheduling if possible, but not strict.

NoExecute:
  Existing Pods may be evicted if they do not tolerate.
```

ASCII:

```text
NoSchedule
  affects future placement

NoExecute
  affects future placement + existing Pods
```

Example:

```text
Node becomes unhealthy.
Kubernetes may add taints.
Pods without toleration may not stay there.
```

Debug command:

```bash
kubectl describe node <node>
```

Look for:

```text
Taints:
  node.kubernetes.io/not-ready:NoSchedule
```

---

# 20. Pod Affinity

Pod affinity says:

```text
Place this Pod near Pods matching certain labels.
```

Example use case:

```text
Run cache-warming side service near API service in same zone.
```

Simplified YAML idea:

```yaml
affinity:
  podAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchLabels:
            app: order-service
        topologyKey: kubernetes.io/hostname
```

Meaning:

```text
Schedule this Pod on a node that already has app=order-service.
```

ASCII:

```text
Pod wants to be near order-service

node-1: order-service exists -> allowed
node-2: no order-service      -> rejected
```

Use carefully.

Too many hard affinity rules can block scheduling.

---

# 21. Pod Anti-Affinity

Pod anti-affinity says:

```text
Do not place this Pod near Pods matching certain labels.
```

This is very useful for high availability.

Example:

```text
Do not put all replicas of payment-service on same node.
```

ASCII:

```text
Bad placement:
node-1: payment-1 payment-2 payment-3
node-2: empty
node-3: empty

Better placement:
node-1: payment-1
node-2: payment-2
node-3: payment-3
```

Simplified YAML:

```yaml
affinity:
  podAntiAffinity:
    preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 100
        podAffinityTerm:
          labelSelector:
            matchLabels:
              app: payment-service
          topologyKey: kubernetes.io/hostname
```

Meaning:

```text
Prefer not to place payment-service replicas on the same node.
```

For strict production HA, use required rules carefully.

Strict anti-affinity can make rollouts stuck if there are not enough nodes.

---

# 22. Topology Spread Constraints

Topology spread constraints distribute Pods across failure domains.

Failure domains can be:

```text
Nodes
Zones
Racks
Regions
```

Example goal:

```text
Spread frontend replicas evenly across zones.
```

ASCII:

```text
Zones:
zone-a: frontend-1 frontend-4
zone-b: frontend-2
zone-c: frontend-3

Balanced enough? depends on maxSkew.
```

YAML:

```yaml
topologySpreadConstraints:
  - maxSkew: 1
    topologyKey: topology.kubernetes.io/zone
    whenUnsatisfiable: DoNotSchedule
    labelSelector:
      matchLabels:
        app: frontend
```

Meaning:

```text
Keep frontend Pods spread across zones with skew at most 1.
If impossible, do not schedule.
```

Mental model:

```text
Anti-affinity says avoid same place.
Topology spread says keep distribution balanced.
```

---

# 23. Priority And Preemption

Some Pods are more important than others.

Examples:

```text
Critical platform DNS
Payment service
Low-priority batch job
Dev test workload
```

PriorityClass lets Kubernetes know importance.

```yaml
apiVersion: scheduling.k8s.io/v1
kind: PriorityClass
metadata:
  name: high-priority
value: 100000
preemptionPolicy: PreemptLowerPriority
globalDefault: false
description: "Important production services"
```

Pod:

```yaml
priorityClassName: high-priority
```

Preemption means:

```text
If a high-priority Pod cannot fit, Kubernetes may evict lower-priority Pods to make room.
```

ASCII:

```text
Node full:
low-priority-job-a
low-priority-job-b

High priority payment Pod arrives.

Scheduler may preempt low priority Pod.
```

Use carefully.

Preemption can protect critical services but can also disrupt workloads.

---

# 24. Scheduling And Volumes

Volumes can affect scheduling.

Example:

```text
Pod uses PVC backed by zone-a disk.
Pod must run on a node in zone-a.
```

ASCII:

```text
Persistent Disk in zone-a
        |
        v
PVC
        |
        v
Pod must run on node in zone-a

node-1 zone-a -> possible
node-2 zone-b -> rejected
```

This commonly affects StatefulSets.

If a Pod is Pending, always check volume events.

Commands:

```bash
kubectl describe pod <pod>
kubectl get pvc
kubectl describe pvc <pvc>
```

Possible symptoms:

```text
volume node affinity conflict
pod has unbound immediate PersistentVolumeClaims
```

Mental model:

```text
Scheduler places compute, but storage location may restrict where compute can go.
```

---

# 25. Scheduler And DaemonSet

DaemonSet Pods are special.

A DaemonSet means:

```text
Run one Pod on each matching node.
```

Examples:

```text
Log collector
Node exporter
CNI agent
Security agent
```

Modern Kubernetes still uses scheduler behavior for DaemonSet Pods, but the conceptual model is:

```text
DaemonSet controller decides which nodes need a Pod.
Scheduler/kubelet make placement real.
```

ASCII:

```text
Nodes:
node-1 node-2 node-3

DaemonSet desired:
log-agent on every node

Result:
node-1: log-agent
node-2: log-agent
node-3: log-agent
```

DaemonSet scheduling constraints still matter:

```text
nodeSelector
nodeAffinity
taints/tolerations
```

For infrastructure agents, tolerations are often required so they can run on special nodes.

---

# 26. Scheduler And StatefulSet

StatefulSet Pods often have persistent storage and stable identity.

Example:

```text
postgres-0
postgres-1
postgres-2
```

Each Pod may have its own PVC.

```text
postgres-0 -> pvc-postgres-0
postgres-1 -> pvc-postgres-1
postgres-2 -> pvc-postgres-2
```

Scheduling impact:

```text
The Pod may need to run where its volume can attach.
```

ASCII:

```text
postgres-0 PVC in zone-a
        |
        v
postgres-0 Pod can schedule only to compatible nodes
```

Production lesson:

```text
Stateless apps mostly care about CPU/memory/spread.
Stateful apps also care deeply about storage topology.
```

---

# 27. Scheduler And Job / CronJob

Jobs and CronJobs create Pods too.

The scheduler does not care whether the Pod came from:

```text
Deployment
StatefulSet
DaemonSet
Job
CronJob
```

It schedules Pods.

ASCII:

```text
CronJob
   |
   v
Job
   |
   v
Pod with nodeName empty
   |
   v
Scheduler
   |
   v
Assigned node
```

Batch workloads often need special scheduling attention:

```text
Lower priority
Large CPU/memory requests
Run on cheap/preemptible nodes
Avoid production service nodes
```

Example:

```yaml
nodeSelector:
  workload: batch

tolerations:
  - key: "workload"
    operator: "Equal"
    value: "batch"
    effect: "NoSchedule"
```

Mental model:

```text
Keep heavy batch away from latency-sensitive services unless intentionally shared.
```

---

# 28. Production Story: Pod Pending Due To Insufficient CPU

Symptom:

```bash
kubectl get pods
```

Output:

```text
order-service-abc   0/1   Pending
```

Describe:

```bash
kubectl describe pod order-service-abc
```

Event:

```text
0/3 nodes are available: 3 Insufficient cpu.
```

Meaning:

```text
No node has enough remaining allocatable CPU request capacity.
```

ASCII:

```text
New Pod requests: 2 CPU

node-1 remaining: 500m -> reject
node-2 remaining: 1 CPU -> reject
node-3 remaining: 1.5 CPU -> reject

No feasible node.
Pod stays Pending.
```

Fix options:

```text
Reduce request if overestimated
Scale cluster with more nodes
Move workloads
Use autoscaler if configured
Check if old Pods are consuming requests unnecessarily
```

Do not randomly delete Pods unless you understand ownership.

---

# 29. Production Story: Wrong Node Selector

YAML:

```yaml
nodeSelector:
  disk: nvme
```

But nodes have:

```text
disk=ssd
```

Result:

```text
Pod Pending
```

Event:

```text
node(s) didn't match Pod's node affinity/selector
```

ASCII:

```text
Pod wants: disk=nvme

node-1: disk=ssd -> reject
node-2: disk=ssd -> reject
node-3: no disk label -> reject
```

Debug:

```bash
kubectl get nodes --show-labels
kubectl describe pod <pod>
```

Fix:

```text
Correct label on nodes
or correct selector in Pod spec
```

Mindset:

```text
Scheduler can only match facts visible as labels.
Wrong labels create invisible capacity.
```

---

# 30. Production Story: Taint Not Tolerated

Node:

```text
node-gpu taints: workload=gpu:NoSchedule
```

Pod has no toleration.

Scheduler event:

```text
node(s) had untolerated taint {workload: gpu}
```

ASCII:

```text
Pod without pass
       |
       v
GPU node has restricted sign
       |
       v
Rejected
```

Fix:

```yaml
tolerations:
  - key: "workload"
    operator: "Equal"
    value: "gpu"
    effect: "NoSchedule"
```

But remember:

```text
Toleration only allows.
It does not attract.
```

To attract:

```yaml
nodeSelector:
  workload: gpu
```

or node affinity.

---

# 31. Production Story: Overpacked Spring Boot Nodes

Symptoms:

```text
Pods scheduled successfully
After traffic starts, latency spikes
Some Pods OOMKilled
Node memory pressure appears
```

Common reason:

```text
Requests too low for actual Spring Boot memory usage.
```

Bad:

```yaml
requests:
  memory: "128Mi"
limits:
  memory: "2Gi"
```

Reality:

```text
Each app uses 700Mi to 1.2Gi.
Scheduler packed too many Pods together.
```

ASCII:

```text
Scheduler packing by request:
128Mi + 128Mi + 128Mi + ... looks okay

Runtime:
900Mi + 850Mi + 1Gi + ... node pressure
```

Debug:

```bash
kubectl top pod
kubectl top node
kubectl describe node <node>
kubectl describe pod <pod>
```

Better approach:

```text
Measure baseline memory after warmup.
Set request near normal steady usage.
Set limit with safe headroom.
Tune JVM MaxRAMPercentage.
```

Example JVM env:

```yaml
env:
  - name: JAVA_TOOL_OPTIONS
    value: "-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=50"
```

---

# 32. Production Story: Anti-Affinity Blocks Rollout

You set strict anti-affinity:

```text
Do not place two replicas of app=payment on same node.
```

Cluster has only two nodes.

Deployment wants three replicas.

Result:

```text
Two Pods scheduled.
Third Pod Pending forever.
```

ASCII:

```text
Nodes:
node-1: payment-1
node-2: payment-2

payment-3 arrives
Cannot go node-1 because anti-affinity
Cannot go node-2 because anti-affinity
No node-3 exists
Pending
```

Fix options:

```text
Add more nodes
Use preferred anti-affinity instead of required
Reduce replicas
Use topology spread with realistic maxSkew
```

Production mindset:

```text
Hard constraints protect availability only when the cluster has enough capacity.
Otherwise they block progress.
```

---

# 33. Debugging Pending Pods: The Golden Path

When Pod is Pending:

```bash
kubectl get pod <pod> -o wide
```

Check if node assigned:

```text
NODE column empty -> scheduling not complete
NODE column filled -> scheduling done, problem is elsewhere
```

Then:

```bash
kubectl describe pod <pod>
```

Look at Events.

Common event patterns:

```text
Insufficient cpu
Insufficient memory
node(s) didn't match Pod's node affinity/selector
node(s) had untolerated taint
volume node affinity conflict
pod has unbound immediate PersistentVolumeClaims
Too many pods
```

ASCII decision tree:

```text
Pod Pending
   |
   v
NODE empty?
   |
   +-- yes -> scheduling problem
   |          check Events
   |
   +-- no  -> kubelet/runtime problem
              check image, volumes, container start
```

Never start with random YAML changes.

Start with scheduler events.

---

# 34. Debugging Node Capacity

Commands:

```bash
kubectl describe node <node-name>
```

Focus areas:

```text
Conditions
Taints
Allocatable
Allocated resources
Events
```

Example output idea:

```text
Allocatable:
  cpu: 4
  memory: 8Gi

Allocated resources:
  CPU Requests: 3900m (97%)
  Memory Requests: 6Gi (75%)
```

If a new Pod requests 500m CPU:

```text
Remaining CPU request space: 100m
New Pod request: 500m
Result: cannot schedule here
```

ASCII:

```text
Node CPU allocatable: 4000m
Already requested:    3900m
Remaining:             100m
New Pod request:       500m

Rejected
```

---

# 35. Debugging Labels And Selectors

Node labels:

```bash
kubectl get nodes --show-labels
```

Pod scheduling rules:

```bash
kubectl get pod <pod> -o yaml
```

Look for:

```text
nodeSelector
affinity
tolerations
topologySpreadConstraints
priorityClassName
```

Mental checklist:

```text
Does the node actually have required labels?
Is the key spelled correctly?
Is the value spelled correctly?
Is the topologyKey present on nodes?
Are required rules too strict?
```

Tiny typo example:

```text
Node label:
zone=eu-west-1a

Pod requires:
zone=eu-west-la
```

Looks similar.

Completely different to Kubernetes.

---

# 36. Scheduling Dry Run: Simple Deployment

Deployment:

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
          resources:
            requests:
              cpu: "500m"
              memory: "512Mi"
            limits:
              cpu: "1"
              memory: "1Gi"
```

Dry run:

```text
1. Deployment Controller creates ReplicaSet.
2. ReplicaSet creates 3 Pods.
3. Each Pod has no nodeName.
4. Scheduler takes Pod-1.
5. Scheduler filters nodes with enough CPU/memory.
6. Scheduler scores feasible nodes.
7. Scheduler binds Pod-1 to best node.
8. Same process repeats for Pod-2 and Pod-3.
9. Kubelets start containers.
10. Readiness determines whether Service sends traffic.
```

ASCII:

```text
Pod-1 -> node-1
Pod-2 -> node-2
Pod-3 -> node-3

Not because Deployment directly chose nodes.
Because scheduler placed each Pod.
```

---

# 37. Scheduling Dry Run: CPU Failure

Cluster:

```text
node-1 remaining CPU request: 300m
node-2 remaining CPU request: 400m
node-3 remaining CPU request: 200m
```

New Pod:

```yaml
requests:
  cpu: "500m"
```

Filter phase:

```text
node-1: reject, 300m < 500m
node-2: reject, 400m < 500m
node-3: reject, 200m < 500m
```

Result:

```text
No feasible nodes.
Pod remains Pending.
```

Event:

```text
0/3 nodes are available: 3 Insufficient cpu.
```

Fix thinking:

```text
Do we need 500m request?
Can autoscaler add node?
Are old workloads reserving too much?
Do we need bigger nodes?
```

---

# 38. Scheduling Dry Run: Preferred Zone

Pod prefers zone-a but can run elsewhere.

```yaml
affinity:
  nodeAffinity:
    preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 100
        preference:
          matchExpressions:
            - key: topology.kubernetes.io/zone
              operator: In
              values:
                - zone-a
```

Nodes:

```text
node-1 zone-a feasible
node-2 zone-b feasible
node-3 zone-c feasible
```

Scoring:

```text
node-1 gets preference bonus
node-2 no bonus
node-3 no bonus
```

Result likely:

```text
Pod -> node-1
```

But if node-1 is full:

```text
node-1 rejected in filter
node-2 and node-3 can still win
```

Mental model:

```text
Preferred does not block scheduling.
Required can block scheduling.
```

---

# 39. Scheduling Dry Run: Required Zone

Pod requires zone-a.

```yaml
affinity:
  nodeAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      nodeSelectorTerms:
        - matchExpressions:
            - key: topology.kubernetes.io/zone
              operator: In
              values:
                - zone-a
```

Nodes:

```text
node-1 zone-a but no CPU -> reject
node-2 zone-b enough CPU -> reject zone
node-3 zone-c enough CPU -> reject zone
```

Result:

```text
Pod Pending
```

Even though node-2 and node-3 have CPU, they do not match required zone.

This is the cost of hard constraints.

---

# 40. Cluster Autoscaler Connection

The scheduler itself does not create cloud nodes.

But scheduler failures can signal autoscaler.

Flow:

```text
Pod Pending due to insufficient resources
        |
        v
Cluster Autoscaler observes unschedulable Pod
        |
        v
Cloud node group can add node
        |
        v
New node joins cluster
        |
        v
Scheduler retries Pod
        |
        v
Pod scheduled
```

ASCII:

```text
Scheduler: no node fits
        |
        v
Autoscaler: can I add a node that fits?
        |
        v
Cloud provider: create VM
        |
        v
New Kubernetes Node
        |
        v
Scheduler places Pod
```

Important:

```text
Autoscaler needs correct requests.
```

If requests are missing or wrong, autoscaling decisions become wrong.

---

# 41. Horizontal Pod Autoscaler Connection

HPA changes replica count.

Scheduler places new replicas.

Flow:

```text
Traffic increases
      |
      v
CPU metric high
      |
      v
HPA increases replicas from 3 to 8
      |
      v
ReplicaSet creates 5 new Pods
      |
      v
Scheduler places those Pods
```

ASCII:

```text
HPA decides how many Pods.
Scheduler decides where Pods run.
Kubelet starts them.
```

Do not confuse:

```text
HPA = scaling decision
Scheduler = placement decision
```

---

# 42. Real Production Placement Strategy

For a serious Spring Boot microservice:

```text
order-service
payment-service
notification-worker
report-job
```

You usually want:

```text
API services spread across nodes
Payment service higher priority
Batch jobs isolated or lower priority
Stateful services storage-aware
Infra agents on every node
```

Example policy thinking:

```text
payment-service:
  - replicas spread across zones
  - requests measured carefully
  - high priority
  - preferred anti-affinity

report-job:
  - run on batch nodes
  - lower priority
  - larger CPU request
  - can tolerate preemption

log-agent:
  - DaemonSet
  - tolerates infra taints
```

ASCII:

```text
node-prod-1: payment, order, log-agent
node-prod-2: payment, order, log-agent
node-prod-3: payment, order, log-agent

node-batch-1: report-job, analytics-job, log-agent
node-batch-2: report-job, analytics-job, log-agent
```

---

# 43. Example: Payment Service Scheduling YAML

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payment-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: payment-service
  template:
    metadata:
      labels:
        app: payment-service
    spec:
      priorityClassName: high-priority
      topologySpreadConstraints:
        - maxSkew: 1
          topologyKey: topology.kubernetes.io/zone
          whenUnsatisfiable: ScheduleAnyway
          labelSelector:
            matchLabels:
              app: payment-service
      affinity:
        podAntiAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
            - weight: 100
              podAffinityTerm:
                labelSelector:
                  matchLabels:
                    app: payment-service
                topologyKey: kubernetes.io/hostname
      containers:
        - name: payment-service
          image: payment-service:1.0.0
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "700m"
              memory: "768Mi"
            limits:
              cpu: "1500m"
              memory: "1536Mi"
```

Why this is production-minded:

```text
priorityClassName protects importance.
topologySpreadConstraints improves zone distribution.
podAntiAffinity avoids same-node concentration.
requests guide scheduler realistically.
limits protect node from runaway app.
```

---

# 44. Example: Batch Job Scheduling YAML

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: monthly-report-job
spec:
  template:
    spec:
      restartPolicy: Never
      nodeSelector:
        workload: batch
      tolerations:
        - key: "workload"
          operator: "Equal"
          value: "batch"
          effect: "NoSchedule"
      containers:
        - name: report-worker
          image: report-worker:1.0.0
          resources:
            requests:
              cpu: "2"
              memory: "2Gi"
            limits:
              cpu: "4"
              memory: "4Gi"
```

Mental model:

```text
Keep heavy batch work on batch nodes.
Do not let report generation steal CPU from payment-service.
```

ASCII:

```text
Production nodes:
  payment/order APIs

Batch nodes:
  report jobs
  analytics workers
```

---

# 45. Example: Dedicated Node Pool

Cloud clusters often use node pools.

```text
Node pool: general
  labels: workload=general

Node pool: batch
  labels: workload=batch
  taints: workload=batch:NoSchedule

Node pool: gpu
  labels: workload=gpu
  taints: workload=gpu:NoSchedule
```

ASCII:

```text
Cluster
  |
  +-- general pool
  |     order-service
  |     payment-service
  |
  +-- batch pool
  |     report-job
  |     ETL-job
  |
  +-- gpu pool
        ML inference
```

To use a dedicated pool:

```text
Label attracts.
Taint repels.
Toleration permits.
```

One-line memory hook:

```text
Taint keeps others out; selector brings me in.
```

---

# 46. Scheduler Is Not A Load Balancer

The scheduler places Pods when they are created.

It does not continuously move running Pods based on live CPU.

Wrong expectation:

```text
Node is hot, scheduler will move Pods away automatically.
```

Correct:

```text
Scheduler chooses placement at scheduling time.
Other systems may evict, autoscale, or reschedule after failures.
```

ASCII:

```text
Pod created -> scheduler chooses node
Pod running -> scheduler mostly done
```

If a node becomes overloaded:

```text
HPA may create more Pods.
Cluster Autoscaler may add nodes.
Descheduler may move Pods if configured.
Kubelet may evict Pods under pressure.
```

But default scheduler is not continuously rebalancing live workloads like a smart traffic router.

---

# 47. Scheduler Is Not Service Load Balancing

Service load balancing decides where requests go.

Scheduler decides where Pods live.

```text
Scheduler:
Pod -> Node

Service:
Request -> Ready Pod
```

ASCII:

```text
Scheduling time:
order-pod-1 -> node-1
order-pod-2 -> node-2

Runtime request time:
client -> Service -> one ready order Pod
```

If traffic is uneven, scheduler may not be the issue.

Check:

```text
Service endpoints
Ingress/load balancer behavior
Application latency
HPA metrics
Readiness probes
```

---

# 48. Common Beginner Mistakes

```text
Mistake 1:
Thinking scheduler starts containers.
Correct:
Scheduler binds Pod to node. Kubelet starts containers.

Mistake 2:
Ignoring resource requests.
Correct:
Requests drive scheduling.

Mistake 3:
Setting requests too low.
Correct:
Measure real baseline usage.

Mistake 4:
Using hard affinity everywhere.
Correct:
Use required only when truly required.

Mistake 5:
Thinking toleration forces placement.
Correct:
Toleration only allows placement.

Mistake 6:
Blaming scheduler for CrashLoopBackOff.
Correct:
CrashLoopBackOff is usually app/runtime/config.

Mistake 7:
Forgetting storage topology.
Correct:
PVC/PV can restrict node placement.
```

---

# 49. Interview Answers

## What does the Kubernetes scheduler do?

The scheduler watches for Pods without an assigned node and chooses the most suitable node for each Pod. It evaluates node feasibility using constraints like resource requests, node selectors, affinity, taints, tolerations, topology, and volumes. After choosing a node, it binds the Pod to that node. The kubelet then starts the containers.

## Does the scheduler start containers?

No. The scheduler only assigns a Pod to a node. The kubelet running on that node starts the containers through the container runtime.

## What is the difference between filter and score?

Filter removes nodes that cannot run the Pod. Score ranks the remaining feasible nodes and chooses the best one.

## What resources does scheduler use for placement?

The scheduler uses resource requests, not live usage, for CPU and memory placement decisions. Limits are runtime constraints, while requests are scheduling reservations.

## What happens if no node can run a Pod?

The Pod remains Pending. The scheduler records events explaining why, such as insufficient CPU, insufficient memory, untolerated taints, node selector mismatch, affinity mismatch, or volume binding issues.

## What is nodeSelector?

nodeSelector is a simple hard constraint that schedules a Pod only onto nodes with matching labels.

## What is node affinity?

Node affinity is a more expressive way to influence node placement. Required node affinity acts during filtering, while preferred node affinity influences scoring.

## What are taints and tolerations?

Taints repel Pods from nodes. Tolerations allow Pods to be scheduled onto tainted nodes. A toleration does not force placement; it only permits it.

## What is Pod anti-affinity used for?

Pod anti-affinity is used to avoid placing similar Pods together, often to improve availability by spreading replicas across nodes or zones.

## How do topology spread constraints help?

They keep Pods balanced across topology domains such as zones or nodes, reducing the risk that one failure domain contains too many replicas.

## How do you debug a Pending Pod?

Check whether the Pod has a node assigned, then run `kubectl describe pod`. Look at Events for scheduler messages. Then inspect node capacity, labels, taints, affinity rules, PVCs, and topology constraints.

---

# 50. Scheduler Debugging Cheat Sheet

```bash
# See pending pods
kubectl get pods -A | grep Pending

# Check assigned node
kubectl get pod <pod> -o wide

# Main scheduler failure reason
kubectl describe pod <pod>

# See node labels
kubectl get nodes --show-labels

# Inspect node capacity and taints
kubectl describe node <node>

# Check resource usage if metrics server exists
kubectl top nodes
kubectl top pods -A

# Check YAML scheduling rules
kubectl get pod <pod> -o yaml

# Check PVC issues
kubectl get pvc
kubectl describe pvc <pvc>
```

Common events:

```text
Insufficient cpu
Insufficient memory
node(s) didn't match Pod's node affinity/selector
node(s) had untolerated taint
volume node affinity conflict
pod has unbound immediate PersistentVolumeClaims
Too many pods
```

---

# 51. Final Cheat Sheet

```text
Scheduler                    = Kubernetes placement brain
Unscheduled Pod              = Pod with empty spec.nodeName
Scheduling output            = Pod bound to selected node
Kubelet                      = starts containers after scheduling
Filter                       = remove impossible nodes
Score                        = rank possible nodes
Bind                         = write final node assignment
Requests                     = scheduling reservation
Limits                       = runtime maximum
Capacity                     = raw node resources
Allocatable                  = resources available to Pods
nodeSelector                 = simple hard node label match
Node affinity required       = hard node rule
Node affinity preferred      = scoring preference
Taint                        = node repels Pods
Toleration                   = Pod is allowed onto tainted node
Pod affinity                 = place near matching Pods
Pod anti-affinity            = avoid matching Pods
Topology spread              = balance Pods across domains
Priority                     = importance of Pod
Preemption                   = evict lower priority Pods if needed
PVC topology                 = storage can restrict scheduling
Pending                      = often scheduling or volume problem
```

Core flow:

```text
Pod created
   |
   v
Scheduler queue
   |
   v
Filter nodes
   |
   v
Score nodes
   |
   v
Bind Pod to node
   |
   v
Kubelet starts containers
```

---

# 52. One Picture To Remember

```text
                         NEW POD
                    spec.nodeName = empty
                              |
                              v
                    +-------------------+
                    | Scheduler Queue   |
                    +---------+---------+
                              |
                              v
                    +-------------------+
                    | FILTER            |
                    | Can it run here?  |
                    +---------+---------+
                              |
                 feasible nodes only
                              |
                              v
                    +-------------------+
                    | SCORE             |
                    | Which is best?    |
                    +---------+---------+
                              |
                              v
                    +-------------------+
                    | BIND              |
                    | Pod -> Node       |
                    +---------+---------+
                              |
                              v
                    +-------------------+
                    | Kubelet           |
                    | Start container   |
                    +---------+---------+
                              |
                              v
                    +-------------------+
                    | Spring Boot App   |
                    | Running on Node   |
                    +-------------------+

Memory hook:

Scheduler does not run your app.
Scheduler chooses where your app should run.
Kubelet runs it.
```

---

# 53. Final Production Checklist

```text
[ ] I know scheduler watches Pods without nodeName.
[ ] I know scheduler chooses nodes, not containers.
[ ] I know kubelet starts containers after binding.
[ ] I know requests drive placement.
[ ] I know limits drive runtime enforcement.
[ ] I know filter means impossible nodes are removed.
[ ] I know score means feasible nodes are ranked.
[ ] I know nodeSelector is hard selection.
[ ] I know required affinity can block scheduling.
[ ] I know preferred affinity influences scoring only.
[ ] I know taints repel and tolerations permit.
[ ] I know toleration does not attract.
[ ] I know anti-affinity spreads replicas.
[ ] I know topology spread balances across zones/nodes.
[ ] I know PVC topology can block scheduling.
[ ] I know Pending + empty NODE means scheduling is not complete.
[ ] I can debug Pending Pods using kubectl describe pod events.
[ ] I can inspect node allocatable and allocated resources.
[ ] I can explain HPA vs scheduler vs autoscaler.
```

---

# 54. Final Memory Hook

Do not memorize scheduler features as random YAML fields.

Remember the production question:

```text
Where should this Pod run safely?
```

Then map features to that question:

```text
Resources answer:
Does the node have enough reserved capacity?

Selectors and affinity answer:
Is this the right kind of node?

Taints and tolerations answer:
Is this Pod allowed here?

Anti-affinity and topology spread answer:
Are replicas safely distributed?

Priority answers:
How important is this Pod compared to others?

Volumes answer:
Can storage attach here?
```

Final sentence:

```text
Kubernetes scheduling is not random placement.
It is a controlled decision pipeline that turns an unscheduled Pod into a safe node assignment.
```
