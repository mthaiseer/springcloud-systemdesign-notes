# 011_Taints_Tolerations_Affinity.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why This Topic Exists

Kubernetes Scheduler already chooses a Node for each Pod.

But production teams do not always want this simple rule:

```text
Put Pod wherever CPU and memory are available.
```

Real clusters have different kinds of machines:

```text
cheap nodes
expensive nodes
GPU nodes
SSD nodes
database-friendly nodes
spot/preemptible nodes
system nodes
customer-isolated nodes
region/zone-separated nodes
```

Different workloads also have different needs:

```text
frontend-service      -> spread across zones
payment-service       -> avoid spot nodes
ml-inference-service  -> needs GPU node
batch-job             -> can run on cheap spot node
logging-agent         -> run on every node
admin-tool            -> only run on internal node
```

Without scheduling rules, Kubernetes can make technically valid but operationally bad decisions.

Example:

```text
payment-service Pod placed on cheap spot node
spot node disappears
payment availability drops
```

Or:

```text
all replicas placed in same zone
zone fails
whole service goes down
```

So Kubernetes gives you placement controls.

This chapter teaches three important controls:

```text
Taints        -> Node says: do not come here unless allowed
Tolerations   -> Pod says: I am allowed to come there
Affinity      -> Pod says: I prefer/require certain placement
Anti-affinity -> Pod says: keep me away from certain Pods
```

One picture:

```text
Scheduler asks:

Can this Pod run on this Node?
Should this Pod run on this Node?
Should it avoid some other Pod?
```

Do not memorize YAML first.

Understand the placement conversation.

---

# 2. The Wrong Mental Model

Many beginners think:

```text
Taint means Pod label.
Toleration means Node label.
Affinity means same as selector.
```

This is confusing.

Better mental model:

```text
Node has properties.
Node can also have rejection rules.
Pod has requirements.
Pod can also have permissions.
Scheduler matches both sides.
```

Bad thinking:

```text
Taint attracts Pods.
```

Correct:

```text
Taint repels Pods.
```

Bad thinking:

```text
Toleration forces Pod onto tainted Node.
```

Correct:

```text
Toleration only allows Pod to be scheduled there.
It does not force placement.
```

Bad thinking:

```text
Affinity is only for Node selection.
```

Correct:

```text
Affinity can be about Nodes or about other Pods.
```

Bad thinking:

```text
required and preferred are same.
```

Correct:

```text
required = hard rule
preferred = soft preference
```

ASCII:

```text
Wrong:
Pod -> nodeName manually always

Better:
Pod requirements + Node properties + Scheduler rules
       |              |               |
       +--------------+---------------+
                      |
                      v
                Placement decision
```

---

# 3. Real World Analogy: Airport Gates

Imagine an airport.

Different gates have rules:

```text
Gate A: domestic flights only
Gate B: international flights only
Gate C: cargo only
Gate D: VIP/private aircraft only
```

A gate can say:

```text
Do not assign normal passenger flights here.
```

That is like a taint.

A flight can have permission:

```text
This is an international flight.
It can use international gates.
```

That is like a toleration.

A flight can also prefer:

```text
Prefer gates near customs.
```

That is like affinity.

Flights can also avoid each other:

```text
Do not put two huge aircraft next to each other.
```

That is like anti-affinity.

ASCII:

```text
Gate / Node
+-----------------------------+
| Rule: cargo-only            |  <--- taint
+-----------------------------+

Flight / Pod
+-----------------------------+
| Permission: cargo flight    |  <--- toleration
| Preference: near warehouse  |  <--- affinity
+-----------------------------+
```

Scheduler is the airport assignment system.

It must respect hard rules and optimize soft preferences.

---

# 4. Core Placement Picture

When a Pod is created, it starts as unscheduled:

```text
Pod: order-service-abc
nodeName: <empty>
status: Pending
```

Scheduler checks Nodes:

```text
Node A: enough CPU? enough memory? labels match? taints tolerated?
Node B: enough CPU? enough memory? labels match? taints tolerated?
Node C: enough CPU? enough memory? labels match? taints tolerated?
```

Then Scheduler chooses one Node.

```text
Pending Pod
    |
    v
+------------------------------+
| Scheduler                    |
| 1. Filter impossible Nodes   |
| 2. Score remaining Nodes     |
| 3. Bind Pod to best Node     |
+------------------------------+
    |
    v
Pod assigned to node-b
```

Placement rules are part of this filtering/scoring.

```text
Taints/Tolerations -> mostly filter
Required Affinity  -> filter
Preferred Affinity -> score
Anti-affinity      -> filter or score depending on required/preferred
```

Mental model:

```text
Hard rules reduce candidates.
Soft preferences rank candidates.
```

ASCII:

```text
All Nodes:
[node-a] [node-b] [node-c] [node-d]

Filter hard rules:
        [node-b] [node-c]

Score soft rules:
node-b = 80
node-c = 50

Bind:
node-b wins
```

---

# 5. Node Labels: The Foundation

Before affinity, understand labels.

A Node can have labels:

```bash
kubectl label node node-a disk=ssd
kubectl label node node-a workload=critical
kubectl label node node-b disk=hdd
kubectl label node node-c lifecycle=spot
```

Labels describe Node properties.

```text
node-a:
  disk=ssd
  workload=critical
  zone=eu-1a

node-b:
  disk=hdd
  workload=general
  zone=eu-1b
```

Node affinity uses these labels.

Example requirement:

```text
Run payment-service only on workload=critical Nodes.
```

Diagram:

```text
Pod requirement:
workload = critical

Nodes:
node-a [workload=critical]  yes
node-b [workload=general]   no
node-c [workload=critical]  yes
```

Important:

```text
Labels attract through affinity.
Taints repel unless tolerated.
```

---

# 6. Taints Mental Model

A taint is a rejection rule on a Node.

It says:

```text
Pods are not welcome here unless they tolerate this taint.
```

Command:

```bash
kubectl taint nodes node-a dedicated=payments:NoSchedule
```

Meaning:

```text
Node node-a has taint:
key      = dedicated
value    = payments
effect   = NoSchedule
```

Human translation:

```text
Do not schedule normal Pods on this Node.
Only Pods that tolerate dedicated=payments can be considered.
```

ASCII:

```text
Node A
+--------------------------------+
| taint: dedicated=payments      |
| effect: NoSchedule             |
+--------------------------------+
       ^
       |
       | normal pods blocked
```

Taint does not mean:

```text
Only payment Pods will definitely go here.
```

It means:

```text
Other Pods without permission cannot go here.
```

To make payment Pods prefer or require that Node, combine toleration with affinity.

---

# 7. Tolerations Mental Model

A toleration is permission on a Pod.

It says:

```text
I can tolerate this Node taint.
```

Example Pod toleration:

```yaml
tolerations:
  - key: "dedicated"
    operator: "Equal"
    value: "payments"
    effect: "NoSchedule"
```

Human translation:

```text
This Pod is allowed to be scheduled on Nodes tainted dedicated=payments:NoSchedule.
```

Important:

```text
Toleration allows.
It does not attract.
```

Diagram:

```text
Node A taint:
dedicated=payments:NoSchedule

Pod A toleration:
dedicated=payments:NoSchedule

Result:
Pod A can be considered for Node A.
```

But Scheduler may still choose another suitable Node if no affinity forces it.

```text
Toleration = entry pass
Affinity   = destination preference/requirement
```

Real-world analogy:

```text
Having a VIP pass lets you enter VIP lounge.
It does not force you to sit there.
```

---

# 8. Taint Effects

Kubernetes supports three main taint effects:

```text
NoSchedule        -> new Pods cannot schedule unless tolerated
PreferNoSchedule  -> try to avoid, but not strict
NoExecute         -> existing Pods may be evicted unless tolerated
```

Mental model:

```text
NoSchedule:
Door is locked for new Pods.

PreferNoSchedule:
Door is open but Scheduler should avoid it if possible.

NoExecute:
Door is locked and current occupants without permission must leave.
```

ASCII:

```text
Effect             New Pods        Existing Pods
------------------------------------------------
NoSchedule         blocked         stay
PreferNoSchedule   avoid if can    stay
NoExecute          blocked         evicted if not tolerated
```

Example:

```bash
kubectl taint nodes node-a maintenance=true:NoExecute
```

This can evict Pods that do not tolerate maintenance=true.

Use carefully.

Production warning:

```text
NoExecute can move workloads.
Wrong taint can cause sudden service disruption.
```

---

# 9. Dry Run: Taint Without Toleration

Cluster:

```text
node-a: taint dedicated=payments:NoSchedule
node-b: no taint
```

Pod:

```text
order-service
no tolerations
```

Scheduler flow:

```text
1. Pod enters scheduling queue.
2. Scheduler checks node-a.
3. node-a has taint dedicated=payments:NoSchedule.
4. Pod has no matching toleration.
5. node-a rejected.
6. Scheduler checks node-b.
7. node-b has enough resources.
8. Pod scheduled to node-b.
```

Picture:

```text
order-service Pod
      |
      v
Scheduler
      |
      +--> node-a [tainted]  X rejected
      |
      +--> node-b [normal]   OK scheduled
```

If all Nodes are tainted and Pod has no toleration:

```text
Pod remains Pending
```

Common event:

```text
0/3 nodes are available: 3 node(s) had untolerated taint.
```

Debug:

```bash
kubectl describe pod order-service-abc
```

Look at Events section.

---

# 10. Dry Run: Taint With Toleration

Cluster:

```text
node-a: taint dedicated=payments:NoSchedule
node-b: no taint
```

Pod:

```yaml
tolerations:
  - key: "dedicated"
    operator: "Equal"
    value: "payments"
    effect: "NoSchedule"
```

Scheduler flow:

```text
1. Scheduler checks node-a.
2. Node has dedicated=payments taint.
3. Pod has matching toleration.
4. node-a is allowed.
5. Scheduler checks node-b.
6. node-b is also allowed.
7. Scheduler scores both Nodes.
8. Best Node wins.
```

Important result:

```text
Pod may still go to node-b.
```

Why?

Because toleration only says:

```text
node-a is not forbidden.
```

It does not say:

```text
choose node-a.
```

To force placement, add node affinity:

```text
required node affinity: dedicated=payments
```

Final mental model:

```text
Taint/Toleration = access control
Affinity         = placement choice
```

---

# 11. Dedicated Node Pattern

A common production pattern:

```text
Only payment-service should run on payment Nodes.
```

You need two pieces:

```text
1. Taint the Node to repel normal Pods.
2. Add toleration + affinity to payment Pods.
```

Node setup:

```bash
kubectl label node node-pay-1 dedicated=payments
kubectl taint node node-pay-1 dedicated=payments:NoSchedule
```

Payment Deployment:

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
      tolerations:
        - key: "dedicated"
          operator: "Equal"
          value: "payments"
          effect: "NoSchedule"
      affinity:
        nodeAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
            nodeSelectorTerms:
              - matchExpressions:
                  - key: dedicated
                    operator: In
                    values:
                      - payments
      containers:
        - name: payment-service
          image: payment-service:1.0.0
          ports:
            - containerPort: 8080
```

Diagram:

```text
Normal Pod
   |
   +--> payment node [tainted] X

Payment Pod
   |
   +--> has toleration OK
   +--> requires label dedicated=payments OK
   +--> scheduled on payment node
```

This is a powerful pattern.

---

# 12. NodeSelector vs Node Affinity

The simplest placement rule is `nodeSelector`.

```yaml
nodeSelector:
  disk: ssd
```

Meaning:

```text
Run only on Nodes with disk=ssd.
```

This is simple but limited.

Node affinity is more expressive:

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
                - nvme
```

Meaning:

```text
Run only on Nodes where disk is ssd or nvme.
```

Comparison:

```text
nodeSelector:
  simple equality only

nodeAffinity:
  In, NotIn, Exists, DoesNotExist, Gt, Lt
  hard and soft rules
```

ASCII:

```text
nodeSelector
Pod -> must match exact labels

nodeAffinity
Pod -> can express richer logic
```

Use mental model:

```text
nodeSelector = simple lock
nodeAffinity = programmable placement rule
```

---

# 13. Required Node Affinity

Required node affinity is a hard rule.

If no Node matches, Pod stays Pending.

Example:

```yaml
affinity:
  nodeAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      nodeSelectorTerms:
        - matchExpressions:
            - key: workload
              operator: In
              values:
                - critical
```

Human meaning:

```text
This Pod must run only on Nodes labeled workload=critical.
```

Scheduler flow:

```text
Nodes:
node-a workload=critical  OK
node-b workload=general   rejected
node-c no workload label  rejected
```

Diagram:

```text
Required rule:
workload in [critical]

Candidate Nodes after filter:
[node-a]
```

The phrase `IgnoredDuringExecution` means:

```text
Rule is checked during scheduling.
If Node label changes later, Pod is not automatically evicted.
```

That detail matters.

If a Pod is already running and Node label changes, Kubernetes does not necessarily move it.

---

# 14. Preferred Node Affinity

Preferred node affinity is a soft rule.

Example:

```yaml
affinity:
  nodeAffinity:
    preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 80
        preference:
          matchExpressions:
            - key: disk
              operator: In
              values:
                - ssd
```

Meaning:

```text
Prefer Nodes with disk=ssd, but if unavailable, use another Node.
```

Scheduler scoring:

```text
node-a disk=ssd  +80 score
node-b disk=hdd  +0 score
node-c disk=ssd  +80 score
```

ASCII:

```text
Hard filter:
[node-a] [node-b] [node-c]

Soft scoring:
node-a = 80
node-b = 0
node-c = 80

Scheduler chooses best overall score.
```

Preferred is useful when availability matters more than perfect placement.

Example:

```text
Prefer spot nodes for batch jobs,
but allow normal nodes if spot capacity is unavailable.
```

Production mindset:

```text
Use required for correctness.
Use preferred for optimization.
```

---

# 15. Pod Affinity Mental Model

Node affinity is about Node labels.

Pod affinity is about other Pods.

Question:

```text
Should this Pod run near another Pod?
```

Example:

```text
Run cache-sidecar close to app Pods.
Run log-processing Pod near log-producing Pods.
```

Pod affinity uses labels of existing Pods.

Diagram:

```text
Existing Pods:
node-a: app=frontend
node-b: app=backend
node-c: app=backend

New Pod wants:
near app=backend

Candidate Nodes:
node-b, node-c preferred/required
```

Important concept: topology key.

Topology key defines what “near” means.

```text
kubernetes.io/hostname -> same node
topology.kubernetes.io/zone -> same zone
```

ASCII:

```text
Near can mean:

same node:
[node-a]
  pod-1
  pod-2

same zone:
zone-a
  node-a
  node-b
```

---

# 16. Pod Anti-Affinity Mental Model

Pod anti-affinity asks:

```text
Should this Pod avoid other Pods?
```

Common production requirement:

```text
Do not place all replicas of same service on the same Node.
```

Why?

```text
If one Node dies, all replicas die.
```

Example anti-affinity:

```yaml
affinity:
  podAntiAffinity:
    preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 100
        podAffinityTerm:
          labelSelector:
            matchExpressions:
              - key: app
                operator: In
                values:
                  - order-service
          topologyKey: kubernetes.io/hostname
```

Meaning:

```text
Prefer not to schedule order-service Pods on a Node that already has order-service Pods.
```

ASCII:

```text
Without anti-affinity:
node-a: order-1 order-2 order-3
node-b:
node-c:

With anti-affinity:
node-a: order-1
node-b: order-2
node-c: order-3
```

This is one of the most useful real production rules.

---

# 17. Required Anti-Affinity Warning

Required anti-affinity is powerful but dangerous.

Example:

```text
Require every replica to be on a different Node.
```

If you have:

```text
replicas = 5
nodes    = 3
```

Then only 3 Pods can be placed.

Remaining Pods stay Pending.

Diagram:

```text
node-a: pod-1
node-b: pod-2
node-c: pod-3

pod-4: Pending
pod-5: Pending

Reason:
No available Node satisfies anti-affinity.
```

So for many services, prefer:

```text
preferred anti-affinity
```

instead of:

```text
required anti-affinity
```

Use required when separation is mandatory:

```text
critical quorum nodes
license constraints
strict compliance isolation
```

Use preferred when it is best effort:

```text
web service replicas
normal stateless services
batch workers
```

---

# 18. Topology Key Deep Model

Topology key tells Kubernetes the boundary for affinity/anti-affinity.

Common topology keys:

```text
kubernetes.io/hostname
  -> node boundary

topology.kubernetes.io/zone
  -> availability zone boundary

topology.kubernetes.io/region
  -> region boundary
```

Example:

```text
Avoid same hostname
```

Means:

```text
Do not put matching Pods on same Node.
```

Example:

```text
Avoid same zone
```

Means:

```text
Try to distribute Pods across zones.
```

ASCII:

```text
Region eu
+---------------------------------------+
| zone-a                                |
|   node-a1   node-a2                   |
|                                       |
| zone-b                                |
|   node-b1   node-b2                   |
+---------------------------------------+

hostname boundary = one node
zone boundary     = group of nodes
region boundary   = group of zones
```

Production meaning:

```text
The topology key defines your blast-radius boundary.
```

---

# 19. Spring Boot Example: Order Service Placement

Suppose you have a Spring Boot `order-service`.

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

This service is stateless but important.

Production goals:

```text
1. Do not run on spot nodes.
2. Prefer critical nodes.
3. Spread replicas across nodes.
4. Do not receive traffic before ready.
```

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
      affinity:
        nodeAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
            - weight: 80
              preference:
                matchExpressions:
                  - key: workload
                    operator: In
                    values:
                      - critical
        podAntiAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
            - weight: 100
              podAffinityTerm:
                labelSelector:
                  matchExpressions:
                    - key: app
                      operator: In
                      values:
                        - order-service
                topologyKey: kubernetes.io/hostname
      containers:
        - name: order-service
          image: order-service:1.0.0
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
```

This is production-oriented placement.

---

# 20. Spot Node Pattern

Spot nodes are cheaper but unstable.

Good for:

```text
batch jobs
async workers
non-critical processing
CI runners
image processing
report generation
```

Bad for:

```text
payments
checkout
auth
core APIs
stateful databases
```

Node setup:

```bash
kubectl label node spot-node-1 lifecycle=spot
kubectl taint node spot-node-1 lifecycle=spot:NoSchedule
```

Batch worker Pod:

```yaml
tolerations:
  - key: "lifecycle"
    operator: "Equal"
    value: "spot"
    effect: "NoSchedule"
affinity:
  nodeAffinity:
    preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 100
        preference:
          matchExpressions:
            - key: lifecycle
              operator: In
              values:
                - spot
```

Meaning:

```text
This workload may run on spot nodes and prefers them.
```

ASCII:

```text
Batch Job
   |
   +--> spot-node [cheap, tainted] OK + preferred
   +--> normal-node OK fallback
```

This balances cost and availability.

---

# 21. GPU Node Pattern

GPU nodes are expensive and should be protected.

Node:

```bash
kubectl label node gpu-node-1 accelerator=nvidia-gpu
kubectl taint node gpu-node-1 accelerator=nvidia-gpu:NoSchedule
```

ML inference Pod:

```yaml
tolerations:
  - key: "accelerator"
    operator: "Equal"
    value: "nvidia-gpu"
    effect: "NoSchedule"
affinity:
  nodeAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      nodeSelectorTerms:
        - matchExpressions:
            - key: accelerator
              operator: In
              values:
                - nvidia-gpu
containers:
  - name: ml-inference
    image: ml-inference:1.0.0
    resources:
      limits:
        nvidia.com/gpu: 1
```

Why both?

```text
Taint protects GPU node from normal Pods.
Affinity sends GPU workload to GPU node.
GPU resource limit requests actual GPU device.
```

Diagram:

```text
Normal API Pod
   |
   +--> GPU node X blocked by taint

ML Pod
   |
   +--> tolerates GPU taint
   +--> requires GPU label
   +--> requests GPU resource
```

---

# 22. System Node Pattern

Some clusters keep system workloads separate:

```text
CoreDNS
metrics-server
ingress controller
logging agent
monitoring agent
```

Node label:

```bash
kubectl label node sys-node-1 node-role.kubernetes.io/system=true
kubectl taint node sys-node-1 node-role.kubernetes.io/system=true:NoSchedule
```

System component tolerates this taint.

Goal:

```text
Business application Pods should not crowd system Pods.
```

ASCII:

```text
System Nodes:
  CoreDNS
  Metrics
  Ingress

App Nodes:
  order-service
  payment-service
  user-service
```

Production benefit:

```text
If app traffic spikes, DNS and ingress are less likely to be starved.
```

But do not overdo separation.

Too many special node pools can cause poor utilization.

Mental model:

```text
Isolation improves reliability.
Too much isolation wastes capacity.
```

---

# 23. Zone Spread With Anti-Affinity

Suppose you have three zones:

```text
zone-a
zone-b
zone-c
```

You run 3 replicas of checkout-service.

Bad placement:

```text
zone-a: checkout-1 checkout-2 checkout-3
zone-b:
zone-c:
```

If zone-a fails:

```text
checkout-service down
```

Better:

```text
zone-a: checkout-1
zone-b: checkout-2
zone-c: checkout-3
```

Pod anti-affinity can help:

```yaml
affinity:
  podAntiAffinity:
    preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 100
        podAffinityTerm:
          labelSelector:
            matchExpressions:
              - key: app
                operator: In
                values:
                  - checkout-service
          topologyKey: topology.kubernetes.io/zone
```

Meaning:

```text
Prefer not to put checkout-service Pods in the same zone.
```

Production result:

```text
Better zone failure tolerance.
```

---

# 24. Affinity vs TopologySpreadConstraints

Affinity and anti-affinity are older and powerful.

Kubernetes also has `topologySpreadConstraints` for spreading Pods.

For simple spreading, topology spread is often clearer.

Example:

```yaml
topologySpreadConstraints:
  - maxSkew: 1
    topologyKey: topology.kubernetes.io/zone
    whenUnsatisfiable: ScheduleAnyway
    labelSelector:
      matchLabels:
        app: checkout-service
```

Meaning:

```text
Try to keep checkout-service replicas balanced across zones.
```

Mental model:

```text
Anti-affinity says: avoid being with same Pods.
Topology spread says: keep distribution balanced.
```

ASCII:

```text
Anti-affinity:
Do not sit near same app.

Topology spread:
Keep count balanced across buckets.
```

For learning scheduler internals, affinity teaches placement logic.

For production spreading, topology spread may be more readable.

---

# 25. Dry Run: Pod Pending Due To Untolerated Taint

Deployment created:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  replicas: 1
  template:
    spec:
      containers:
        - name: user-service
          image: user-service:1.0.0
```

Cluster:

```text
node-a taint: dedicated=payments:NoSchedule
node-b taint: lifecycle=spot:NoSchedule
```

Pod has no tolerations.

Result:

```text
user-service Pod Pending
```

Debug:

```bash
kubectl describe pod user-service-abc
```

Events:

```text
0/2 nodes are available:
1 node(s) had untolerated taint {dedicated: payments},
1 node(s) had untolerated taint {lifecycle: spot}
```

Fix options:

```text
1. Add normal untainted app nodes.
2. Remove unnecessary taints.
3. Add correct toleration if workload is allowed there.
```

Do not blindly add tolerations.

That is like giving every employee access to every secure room.

---

# 26. Dry Run: Required Affinity Too Strict

Pod requires:

```text
disk=nvme
```

Cluster Nodes:

```text
node-a disk=ssd
node-b disk=hdd
node-c no disk label
```

Scheduler:

```text
node-a rejected: disk not in nvme
node-b rejected: disk not in nvme
node-c rejected: missing disk label
```

Result:

```text
Pod Pending
```

Event:

```text
0/3 nodes are available: 3 node(s) didn't match Pod's node affinity/selector.
```

Debug:

```bash
kubectl get nodes --show-labels
kubectl describe pod <pod>
```

Fix:

```text
1. Correct Node labels.
2. Relax required affinity to preferred affinity.
3. Add matching node pool.
```

Mental model:

```text
Required affinity is a gate.
If no Node passes, Pod cannot run.
```

---

# 27. Dry Run: Required Anti-Affinity Blocks Scale Out

Deployment:

```text
replicas = 4
```

Cluster:

```text
3 nodes
```

Pod anti-affinity:

```text
required: no two Pods with app=api on same hostname
```

Scheduling:

```text
api-1 -> node-a
api-2 -> node-b
api-3 -> node-c
api-4 -> Pending
```

Reason:

```text
No fourth node exists.
Putting api-4 anywhere violates required anti-affinity.
```

Diagram:

```text
node-a: api-1
node-b: api-2
node-c: api-3

api-4: cannot fit
```

Fix options:

```text
1. Add more nodes.
2. Reduce replicas.
3. Change required anti-affinity to preferred.
```

Production lesson:

```text
Hard placement rules reduce capacity.
Use them only when the rule is truly mandatory.
```

---

# 28. Java/Spring Boot Memory And Placement Story

A Spring Boot service is not just a container.

It has runtime behavior:

```text
JVM heap
Metaspace
thread pools
DB connection pools
HTTP server threads
GC behavior
startup warmup
cache usage
```

Placement matters when workloads compete.

Example:

```text
A CPU-heavy reporting service colocated with payment-service.
Reporting job spikes CPU.
Payment latency increases.
```

Better placement:

```text
payment-service -> critical nodes
reporting-service -> batch/spot nodes
```

Spring Boot reporting controller:

```java
@RestController
@RequestMapping("/reports")
public class ReportController {

    @GetMapping("/monthly")
    public String monthlyReport() {
        // Expensive CPU + DB operation in real system
        return "report-generated";
    }
}
```

Kubernetes placement:

```text
payment-service protected with dedicated nodes
report-service allowed on cheaper batch nodes
```

Mental model:

```text
Scheduling is not only infrastructure.
It protects application latency.
```

---

# 29. Production Story: Everything Scheduled On One Node

Incident:

```text
order-service has 5 replicas.
All replicas land on node-a.
node-a crashes.
order-service unavailable.
```

Why did it happen?

Possible reasons:

```text
No pod anti-affinity
No topology spread
Other nodes lacked resources
Scheduler scoring preferred node-a
Node labels/taints accidentally limited choices
```

Debug:

```bash
kubectl get pods -o wide -l app=order-service
kubectl describe pod <pod>
kubectl get nodes --show-labels
kubectl describe node node-a
```

Better design:

```text
Use preferred pod anti-affinity by hostname.
Use topologySpreadConstraints by zone/hostname.
Set correct resource requests.
```

ASCII:

```text
Before:
node-a: order-1 order-2 order-3 order-4 order-5
node-b:
node-c:

After:
node-a: order-1 order-4
node-b: order-2 order-5
node-c: order-3
```

Lesson:

```text
Replicas do not automatically mean resilience.
Placement decides blast radius.
```

---

# 30. Production Story: Toleration Added Too Broadly

A team added this toleration to all Deployments:

```yaml
tolerations:
  - operator: "Exists"
```

Meaning:

```text
Tolerate all taints.
```

Result:

```text
Normal app Pods started landing on system nodes and GPU nodes.
```

Symptoms:

```text
CoreDNS slowed down.
GPU nodes filled with normal Java apps.
ML workload could not schedule.
```

Why bad?

```text
It bypassed node protection.
```

Correct approach:

```text
Add narrow tolerations only for intended taints.
```

Good:

```yaml
tolerations:
  - key: "dedicated"
    operator: "Equal"
    value: "payments"
    effect: "NoSchedule"
```

Bad:

```yaml
tolerations:
  - operator: "Exists"
```

Production rule:

```text
Broad toleration is like master key access.
Use rarely.
```

---

# 31. Production Story: Label Drift

A Pod requires:

```text
nodeAffinity: workload=critical
```

One day, someone relabels Nodes:

```bash
kubectl label node node-a workload-
kubectl label node node-a workload=general
```

Existing Pods may keep running because rule is:

```text
requiredDuringSchedulingIgnoredDuringExecution
```

But new Pods cannot schedule.

Symptoms after rollout:

```text
old Pods running
new Pods Pending
rollout stuck
```

Debug:

```bash
kubectl rollout status deployment payment-service
kubectl describe pod payment-service-new-abc
kubectl get nodes --show-labels
```

Event:

```text
didn't match Pod's node affinity/selector
```

Lesson:

```text
Node labels are part of production contract.
Changing labels can break future scheduling.
```

Use GitOps or automation for node labels where possible.

---

# 32. Debugging Mindset: Scheduling Failures

When Pod is Pending, do not guess.

Follow this chain:

```text
1. Is Pod created?
2. Is it Pending?
3. What do Events say?
4. Are resources available?
5. Are taints blocking it?
6. Does it have matching tolerations?
7. Does nodeSelector match?
8. Does nodeAffinity match?
9. Does pod affinity/anti-affinity block it?
10. Are topology rules too strict?
11. Are PVC zone constraints involved?
12. Is scheduler running?
```

Commands:

```bash
kubectl get pods -o wide
kubectl describe pod <pod-name>

kubectl get nodes
kubectl get nodes --show-labels
kubectl describe node <node-name>

kubectl get events --sort-by=.lastTimestamp
kubectl get deployment <name> -o yaml
```

For taints:

```bash
kubectl describe node <node-name> | grep -i taints
```

For labels:

```bash
kubectl get node <node-name> --show-labels
```

Mental model:

```text
Pending Pod is usually not a container problem.
It is a scheduling decision problem.
```

---

# 33. Common Event Messages

Event:

```text
0/5 nodes are available: 5 node(s) had untolerated taint.
```

Meaning:

```text
Nodes repel this Pod and Pod lacks matching toleration.
```

Event:

```text
0/5 nodes are available: 5 node(s) didn't match Pod's node affinity/selector.
```

Meaning:

```text
Node labels do not satisfy required rules.
```

Event:

```text
0/5 nodes are available: 3 Insufficient cpu, 2 Insufficient memory.
```

Meaning:

```text
Resource requests cannot fit.
```

Event:

```text
0/5 nodes are available: pod anti-affinity rules not match.
```

Meaning:

```text
Anti-affinity is too strict for current cluster layout.
```

Debug pattern:

```text
Read the event.
Map it to the scheduling filter that rejected Nodes.
Fix the correct layer.
```

---

# 34. YAML Pattern: Critical Service

Use case:

```text
Critical Spring Boot service must avoid spot nodes and prefer critical nodes.
```

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
      affinity:
        nodeAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
            nodeSelectorTerms:
              - matchExpressions:
                  - key: workload
                    operator: In
                    values:
                      - critical
          preferredDuringSchedulingIgnoredDuringExecution:
            - weight: 50
              preference:
                matchExpressions:
                  - key: disk
                    operator: In
                    values:
                      - ssd
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
```

Mental model:

```text
Run only on critical nodes.
Prefer SSD nodes.
Spread replicas across hostnames.
```

---

# 35. YAML Pattern: Batch Worker On Spot Nodes

Use case:

```text
Async worker should reduce cost by using spot nodes when possible.
```

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: email-worker
spec:
  replicas: 5
  selector:
    matchLabels:
      app: email-worker
  template:
    metadata:
      labels:
        app: email-worker
    spec:
      tolerations:
        - key: "lifecycle"
          operator: "Equal"
          value: "spot"
          effect: "NoSchedule"
      affinity:
        nodeAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
            - weight: 100
              preference:
                matchExpressions:
                  - key: lifecycle
                    operator: In
                    values:
                      - spot
      containers:
        - name: email-worker
          image: email-worker:1.0.0
```

Mental model:

```text
Can enter spot nodes.
Prefers spot nodes.
Can fall back if needed.
```

For async workloads, design app to handle interruption:

```text
idempotent processing
retry queue
Kafka offset safety
dead letter queue
short graceful shutdown
```

---

# 36. Java Worker Example: Idempotency For Spot Nodes

If a worker runs on spot nodes, it may be interrupted.

So business logic must be idempotent.

Example:

```java
@Service
public class EmailWorker {

    private final SentEmailRepository repository;
    private final EmailClient emailClient;

    public EmailWorker(SentEmailRepository repository, EmailClient emailClient) {
        this.repository = repository;
        this.emailClient = emailClient;
    }

    public void process(String messageId, String recipient, String body) {
        if (repository.existsByMessageId(messageId)) {
            return;
        }

        emailClient.send(recipient, body);
        repository.save(new SentEmail(messageId, recipient));
    }
}
```

Why this matters:

```text
Spot node may disappear after send but before ack.
Message may be retried.
Idempotency prevents duplicate email.
```

Kubernetes placement and Java logic must work together.

```text
Scheduler controls where workload runs.
Application controls correctness under failure.
```

This is production engineering.

---

# 37. When Not To Use Complex Affinity

Avoid complex rules when simple rules are enough.

Bad YAML smell:

```text
Many required affinities
Many anti-affinities
Broad tolerations
Hard zone rules
No capacity planning
```

Problems:

```text
Pods stuck Pending
Cluster autoscaler cannot help easily
Rollouts become fragile
Small label mistakes break deployments
Hard to reason about failures
```

Better approach:

```text
Start simple.
Add rules only for real production needs.
Prefer soft rules when possible.
Use hard rules for correctness/security/compliance.
```

Mental model:

```text
Every placement rule is a constraint.
Every constraint reduces scheduler freedom.
Reduced freedom can reduce availability.
```

ASCII:

```text
More rules
   |
   v
Fewer candidate nodes
   |
   v
Higher chance of Pending Pods
```

---

# 38. Security And Isolation Mindset

Taints and affinity are not full security boundaries.

They influence scheduling.

They do not replace:

```text
RBAC
NetworkPolicy
Pod Security Standards
Secrets management
Node isolation
Runtime security
```

Example:

```text
A Pod not scheduled on payment Node does not automatically mean it cannot call payment-service over network.
```

For network isolation, use NetworkPolicy.

For access control, use RBAC.

For node-level trust, use separate node pools and admission policies.

Mental model:

```text
Scheduling isolation controls placement.
Security isolation controls access.
```

Do not confuse the two.

---

# 39. Interview Questions

## What is a taint?

A taint is a rule applied to a Node that repels Pods. A Pod can only be scheduled onto a tainted Node if it has a matching toleration, depending on the taint effect.

## What is a toleration?

A toleration is a Pod-level permission that allows the Pod to be scheduled on Nodes with matching taints. It does not force the Pod to go there.

## Does toleration attract a Pod to a Node?

No. Toleration only allows scheduling on a tainted Node. To attract or require placement, use node affinity or node selector.

## What are taint effects?

`NoSchedule` prevents new Pods without toleration from scheduling. `PreferNoSchedule` asks the scheduler to avoid the Node if possible. `NoExecute` can evict existing Pods that do not tolerate the taint.

## What is node affinity?

Node affinity is a scheduling rule that places Pods based on Node labels. It can be required as a hard rule or preferred as a soft scoring rule.

## What is pod affinity?

Pod affinity places a Pod near other Pods matching certain labels within a topology domain such as the same Node or zone.

## What is pod anti-affinity?

Pod anti-affinity prevents or discourages placing a Pod near other Pods matching certain labels. It is commonly used to spread replicas across nodes or zones.

## What is topologyKey?

Topology key defines the placement boundary for pod affinity or anti-affinity, such as hostname, zone, or region.

## Why can required anti-affinity be dangerous?

It can make Pods unschedulable if the cluster does not have enough topology domains. For example, requiring five replicas to run on different nodes will fail in a three-node cluster.

## How do you debug a Pending Pod?

Use `kubectl describe pod` and inspect Events. Then check node taints, node labels, resource availability, node affinity, pod affinity, anti-affinity, topology constraints, and PVC zone constraints.

---

# 40. Cheat Sheet

```text
Taint
  Node repels Pods.

Toleration
  Pod can tolerate a Node taint.

Toleration does not attract.
  It only permits.

Affinity
  Pod asks for preferred/required placement.

Node affinity
  Placement based on Node labels.

Pod affinity
  Placement near matching Pods.

Pod anti-affinity
  Placement away from matching Pods.

Required
  Hard rule. If not satisfied, Pod is Pending.

Preferred
  Soft rule. Scheduler tries, but can still schedule elsewhere.

NoSchedule
  New Pods without toleration cannot schedule.

PreferNoSchedule
  Avoid if possible.

NoExecute
  Can evict existing Pods without toleration.

Topology key
  Defines boundary: node, zone, region.
```

Debug commands:

```bash
kubectl describe pod <pod>
kubectl get pods -o wide
kubectl get nodes --show-labels
kubectl describe node <node>
kubectl get events --sort-by=.lastTimestamp
```

Common fixes:

```text
Add correct Node labels.
Add narrow toleration.
Relax required affinity to preferred.
Add more Nodes.
Fix topology spread rules.
Remove accidental taints.
```

---

# 41. One Picture To Remember

```text
                         POD CREATED
                              |
                              v
                       Scheduling Queue
                              |
                              v
                 +---------------------------+
                 | Scheduler                 |
                 |                           |
                 | 1. Filter Nodes           |
                 |    - resources            |
                 |    - taints/tolerations   |
                 |    - required affinity    |
                 |    - anti-affinity        |
                 |                           |
                 | 2. Score Nodes            |
                 |    - preferred affinity   |
                 |    - spreading            |
                 |    - resource balance     |
                 |                           |
                 | 3. Bind Pod               |
                 +-------------+-------------+
                               |
                               v
                        Selected Node
                               |
                               v
                          Kubelet runs Pod
```

Rule:

```text
Taints keep Pods away.
Tolerations give Pods permission.
Affinity tells Scheduler where Pods should go.
Anti-affinity tells Scheduler what Pods should avoid.
```

Final sentence:

```text
Scheduling is not just fitting containers onto machines; it is controlling failure blast radius, cost, performance, and workload isolation.
```

---

# 42. Final Production Checklist

```text
[ ] I know which workloads are critical and which are batch.
[ ] I know which Nodes are special: GPU, spot, system, critical, storage.
[ ] I use taints to protect special Nodes.
[ ] I use narrow tolerations, not broad master-key tolerations.
[ ] I understand toleration does not force placement.
[ ] I combine toleration with affinity for dedicated Node pools.
[ ] I use required affinity only when placement is mandatory.
[ ] I use preferred affinity when placement is an optimization.
[ ] I spread replicas across nodes/zones for resilience.
[ ] I avoid required anti-affinity unless capacity supports it.
[ ] I check Pod events before guessing.
[ ] I understand Pending usually means scheduling constraints, not container startup.
[ ] I connect Kubernetes placement decisions with Java application reliability.
```

---

# 43. Final Memory Hook

Do not memorize YAML names.

Remember the conversation:

```text
Node:
  I have labels describing me.
  I may have taints repelling Pods.

Pod:
  I may tolerate some taints.
  I may require certain Node labels.
  I may prefer certain Nodes.
  I may want to be near or away from other Pods.

Scheduler:
  I filter impossible Nodes.
  I score possible Nodes.
  I bind the Pod to the best Node.
```

One-line mental model:

```text
Taints and tolerations control permission; affinity controls placement preference and requirement.
```
