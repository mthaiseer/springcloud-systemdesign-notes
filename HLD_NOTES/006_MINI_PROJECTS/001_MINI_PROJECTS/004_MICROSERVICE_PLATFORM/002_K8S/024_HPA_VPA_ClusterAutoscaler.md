# 024_HPA_VPA_ClusterAutoscaler.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Autoscaling Exists

A production system does not receive the same traffic every minute.

Morning may be quiet.

Lunch time may be busy.

A marketing campaign may suddenly send 10x traffic.

A payment partner may retry aggressively.

A batch job may consume CPU at midnight.

Without autoscaling, teams usually choose one of two bad options:

```text
Option 1: Run too few resources
Result: latency spikes, timeouts, failed requests

Option 2: Run too many resources
Result: wasted money, idle CPUs, over-provisioned nodes
```

Kubernetes autoscaling tries to solve this operating problem:

```text
Run enough capacity for current demand,
but do not permanently pay for peak capacity.
```

Autoscaling is not magic.

It is another desired-state control loop.

```text
Traffic / load changes
        |
        v
Metrics change
        |
        v
Autoscaler compares current vs target
        |
        v
Replica count / resource request / node count changes
```

Mental model:

```text
Autoscaling = Kubernetes capacity thermostat
```

Thermostat example:

```text
Room too cold  -> heater ON
Room too hot   -> heater OFF
Room OK        -> do nothing
```

Kubernetes example:

```text
CPU too high   -> add Pods
CPU too low    -> remove Pods
Pods pending   -> add Nodes
Requests wrong -> recommend or adjust CPU/memory requests
```

One picture:

```text
Demand changes
    |
    v
Metrics change
    |
    v
Autoscaler decides
    |
    v
Capacity changes
```

If you remember only one thing from this chapter, remember this:

```text
HPA changes number of Pods.
VPA changes Pod resource requests.
Cluster Autoscaler changes number of Nodes.
```

---

# 2. The Three Autoscalers Mental Model

Kubernetes autoscaling has three common layers.

```text
HPA  = Horizontal Pod Autoscaler
VPA  = Vertical Pod Autoscaler
CA   = Cluster Autoscaler
```

The easiest way to understand them is to ask:

```text
What thing does this autoscaler change?
```

Answer:

```text
HPA changes replicas.
VPA changes CPU/memory requests.
Cluster Autoscaler changes nodes.
```

ASCII:

```text
                 AUTOSCALING STACK

+------------------------------------------------+
| Application traffic increases                  |
+----------------------+-------------------------+
                       |
                       v
+------------------------------------------------+
| HPA                                            |
| changes Deployment replicas                    |
| 3 Pods -> 8 Pods                               |
+----------------------+-------------------------+
                       |
                       v
+------------------------------------------------+
| Scheduler tries to place new Pods              |
+----------------------+-------------------------+
                       |
         enough node capacity?                   |
              yes / no                           |
                       v
+------------------------------------------------+
| Cluster Autoscaler                             |
| adds worker nodes if Pods cannot be scheduled  |
+------------------------------------------------+

VPA works on another axis:

+------------------------------------------------+
| VPA                                            |
| changes Pod CPU/memory requests                |
| 500m CPU -> 1000m CPU                          |
+------------------------------------------------+
```

Real world analogy:

```text
Restaurant traffic increases.

HPA:
Add more chefs.

VPA:
Give each chef a bigger workstation and better tools.

Cluster Autoscaler:
Open another kitchen room because current kitchen has no space.
```

Do not memorize commands first.

Understand which dimension is being changed:

```text
More copies       -> HPA
Bigger copies     -> VPA
More machines     -> Cluster Autoscaler
```

---

# 3. Why HPA Exists

Imagine `order-service` normally handles 100 requests per second with 3 Pods.

Then traffic becomes 500 requests per second.

Without HPA:

```text
3 Pods receive all traffic
CPU rises
request queue grows
latency increases
timeouts appear
users complain
```

With HPA:

```text
CPU rises
HPA notices average CPU above target
HPA increases replicas
more Pods receive traffic
average CPU comes down
latency stabilizes
```

Picture:

```text
Before spike:

Service
  |
  +--> Pod A CPU 45%
  +--> Pod B CPU 50%
  +--> Pod C CPU 48%

After spike:

Service
  |
  +--> Pod A CPU 92%
  +--> Pod B CPU 88%
  +--> Pod C CPU 95%

HPA target = 60%

Action:
Add more Pods
```

After scale out:

```text
Service
  |
  +--> Pod A CPU 58%
  +--> Pod B CPU 61%
  +--> Pod C CPU 57%
  +--> Pod D CPU 60%
  +--> Pod E CPU 55%
  +--> Pod F CPU 59%
```

HPA is useful when:

```text
App is stateless or mostly stateless
Workload can be split across Pods
More Pods means more throughput
Metrics reflect real pressure
```

HPA is less useful when:

```text
Single-threaded bottleneck is database
All Pods block on same external API
App has strong in-memory session dependency
Workload cannot be parallelized
Metric is wrong or delayed
```

Mental model:

```text
HPA does not make one Pod faster.
HPA gives the Service more Pod workers.
```

---

# 4. HPA Control Loop

HPA is a controller.

It repeatedly asks:

```text
What is current metric value?
What is target metric value?
How many replicas are currently running?
How many replicas should exist now?
```

Pseudo-code:

```text
while true:
    current = read_metrics()
    target = read_hpa_target()
    replicas = read_current_replicas()

    desired_replicas = calculate(current, target, replicas)

    if desired_replicas != replicas:
        update_deployment_scale(desired_replicas)
```

ASCII:

```text
+---------------------------+
| Metrics Server / Adapter  |
| CPU, memory, custom metric|
+-------------+-------------+
              |
              v
+---------------------------+
| HPA Controller            |
| compare current vs target |
+-------------+-------------+
              |
              v
+---------------------------+
| Deployment scale subresource|
| replicas: N               |
+-------------+-------------+
              |
              v
+---------------------------+
| ReplicaSet creates/removes|
| Pods                      |
+---------------------------+
```

Important:

```text
HPA does not directly start containers.
```

It updates desired replica count.

Then normal Kubernetes controllers do the rest.

Flow:

```text
HPA updates Deployment replicas
        |
        v
Deployment / ReplicaSet sees desired count changed
        |
        v
Pods are created or removed
        |
        v
Scheduler places Pods
        |
        v
Kubelet starts containers
```

This is the same Kubernetes pattern from the mental model chapter:

```text
Desired State + Reconciliation
```

Autoscaling is just another reconciliation loop.

---

# 5. HPA Formula Without Memorization

The core HPA idea is simple:

```text
If current usage is double the target,
roughly double the replicas.
```

Approximate formula:

```text
Desired replicas = Current replicas × Current metric / Target metric
```

Example:

```text
Current replicas = 4
Current CPU      = 90%
Target CPU       = 60%

Desired replicas = 4 × 90 / 60
                 = 6
```

ASCII:

```text
Current:
4 Pods, average CPU 90%

Target:
average CPU 60%

Need:
more Pods so same traffic spreads wider

4 Pods -> 6 Pods
```

Another example:

```text
Current replicas = 10
Current CPU      = 30%
Target CPU       = 60%

Desired replicas = 10 × 30 / 60
                 = 5
```

Meaning:

```text
Current capacity is too high for the load.
Scale down to save resources.
```

Do not memorize exact Kubernetes implementation details first.

Remember the intuition:

```text
Current above target -> scale out
Current below target -> scale in
Current near target  -> do nothing
```

Picture:

```text
CPU Target = 60%

30%  ---------------- scale down
60%  ---------------- stable
90%  ---------------- scale up
```

HPA includes stabilization windows, tolerance, missing metrics handling, and policies.

But the mental model remains:

```text
Metric pressure drives replica count.
```

---

# 6. Metrics Server Mental Model

HPA needs metrics.

Without metrics, HPA is blind.

```text
HPA without metrics = driver without speedometer
```

Common metric path:

```text
Kubelet collects container CPU/memory usage
        |
        v
Metrics Server scrapes Kubelet summary API
        |
        v
Kubernetes resource metrics API
        |
        v
HPA reads metrics
```

ASCII:

```text
Node
+----------------------------------+
| Kubelet                          |
|  - container CPU                 |
|  - container memory              |
+----------------+-----------------+
                 |
                 v
+----------------------------------+
| Metrics Server                   |
| aggregates resource metrics      |
+----------------+-----------------+
                 |
                 v
+----------------------------------+
| HPA Controller                   |
| reads CPU/memory metrics         |
+----------------------------------+
```

Common production symptom:

```text
HPA shows:
<unknown>/60%
```

Possible causes:

```text
Metrics Server not installed
Metrics Server cannot reach kubelets
TLS/certificate issue
Pod has no CPU request
Metrics are delayed
API aggregation issue
```

Debug commands:

```bash
kubectl top nodes
kubectl top pods
kubectl get apiservices | grep metrics
kubectl describe hpa order-service
kubectl logs -n kube-system deploy/metrics-server
```

Mental model:

```text
If kubectl top does not work,
HPA CPU/memory scaling usually will not work.
```

---

# 7. CPU Request Is Required For CPU-Based HPA

CPU-based HPA compares usage against CPU request.

Example container:

```yaml
resources:
  requests:
    cpu: "500m"
```

If the Pod uses `250m` CPU:

```text
CPU utilization = 250m / 500m = 50%
```

If the Pod uses `500m` CPU:

```text
CPU utilization = 500m / 500m = 100%
```

Picture:

```text
CPU request = 500m

Actual usage:
250m -> 50%
500m -> 100%
750m -> 150%
```

This is very important.

If CPU request is missing, CPU utilization percentage cannot be calculated correctly.

Bad YAML:

```yaml
containers:
  - name: order-service
    image: order-service:1.0.0
```

Better YAML:

```yaml
containers:
  - name: order-service
    image: order-service:1.0.0
    resources:
      requests:
        cpu: "500m"
        memory: "512Mi"
      limits:
        cpu: "1000m"
        memory: "1024Mi"
```

Mental model:

```text
Request = baseline reservation used by scheduler and HPA math.
Limit   = maximum allowed usage before throttling/OOM behavior.
```

Common mistake:

```text
No CPU request
        |
        v
HPA cannot calculate CPU utilization
        |
        v
Autoscaling fails or shows unknown metrics
```

Production lesson:

```text
Autoscaling starts with correct resource requests.
```

---

# 8. HPA YAML For Spring Boot Order Service

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
          image: registry.example.com/order-service:1.0.0
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "500m"
              memory: "512Mi"
            limits:
              cpu: "1000m"
              memory: "1024Mi"
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 20
            periodSeconds: 5
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 10
```

HPA:

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: order-service
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: order-service
  minReplicas: 3
  maxReplicas: 20
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 60
```

Meaning:

```text
Keep at least 3 Pods.
Never exceed 20 Pods.
Try to keep average CPU utilization around 60%.
```

ASCII:

```text
HPA order-service
      |
      | scaleTargetRef
      v
Deployment order-service
      |
      v
ReplicaSet
      |
      v
Pods
```

Command:

```bash
kubectl apply -f deployment.yaml
kubectl apply -f hpa.yaml
kubectl get hpa
kubectl describe hpa order-service
```

Remember:

```text
HPA does not replace Deployment.
HPA modifies Deployment replica count.
```

---

# 9. Spring Boot Actuator And Autoscaling

For CPU-based HPA, Kubernetes does not need Spring Actuator.

CPU metrics come from the container runtime and kubelet.

But Actuator is still important for production autoscaling because:

```text
Readiness protects traffic routing
Liveness detects dead app process
Prometheus metrics expose business/load signals
Custom metrics can drive better HPA decisions
```

Spring Boot config:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,prometheus,metrics
  endpoint:
    health:
      probes:
        enabled: true
  metrics:
    tags:
      application: order-service
```

Example custom metric idea:

```text
orders_in_queue
payment_retry_backlog
http_server_requests_seconds_count
active_checkout_sessions
```

Why custom metrics matter:

```text
CPU may be low but queue backlog high.
CPU may be high due to GC, not user traffic.
Memory may rise slowly before failure.
External API waiting may not consume CPU.
```

ASCII:

```text
HTTP traffic
    |
    v
Spring Boot app
    |
    +--> CPU metric to kubelet
    |
    +--> readiness/liveness via Actuator
    |
    +--> Prometheus custom metrics
```

Production mindset:

```text
Start with CPU-based HPA for simple services.
Use custom metrics when CPU does not represent real demand.
```

---

# 10. HPA With Custom Metrics Mental Model

CPU is not always the right scaling signal.

Example: notification service consumes Kafka messages.

Problem:

```text
Traffic is not HTTP requests.
Work arrives as Kafka lag.
```

If Kafka lag grows, you need more consumers.

Metric:

```text
consumer_lag = 100000 messages
```

Autoscaling target:

```text
Keep average lag per Pod below 5000 messages
```

Picture:

```text
Kafka Topic
+----------------------------------+
| partition 0: lag 20000           |
| partition 1: lag 30000           |
| partition 2: lag 50000           |
+----------------+-----------------+
                 |
                 v
Notification Pods consume messages
                 |
                 v
HPA scales consumers based on lag
```

Custom metrics path:

```text
App / exporter exposes metric
        |
        v
Prometheus collects metric
        |
        v
Prometheus Adapter exposes Kubernetes custom metrics API
        |
        v
HPA reads custom metric
        |
        v
Replica count changes
```

ASCII:

```text
Spring Boot / Kafka Exporter
        |
        v
Prometheus
        |
        v
Prometheus Adapter
        |
        v
HPA
        |
        v
Deployment replicas
```

Mental model:

```text
Scale on the bottleneck signal,
not blindly on CPU.
```

Examples:

```text
HTTP service       -> CPU, RPS, p95 latency
Kafka consumer     -> consumer lag
Worker queue       -> queue depth
WebSocket service  -> active connections
Batch processor    -> pending job count
```

---

# 11. HPA Scale-Up And Scale-Down Behavior

Autoscaling too fast can be dangerous.

Autoscaling too slow can also be dangerous.

Scale-up problem:

```text
Traffic spike happens
Pods take 90 seconds to start
HPA adds Pods slowly
Users timeout during startup delay
```

Scale-down problem:

```text
Traffic briefly drops
HPA removes Pods
Traffic returns
System scales up again
Pods flap up and down
```

This is called thrashing.

ASCII:

```text
Bad scaling pattern:

replicas
20 |       /3\      /3\
15 |      /   \    /   \
10 |_____/     \__/     \____
       time

Result: unstable capacity
```

HPA behavior can be controlled:

```yaml
behavior:
  scaleUp:
    stabilizationWindowSeconds: 0
    policies:
      - type: Percent
        value: 100
        periodSeconds: 60
  scaleDown:
    stabilizationWindowSeconds: 300
    policies:
      - type: Percent
        value: 50
        periodSeconds: 60
```

Meaning:

```text
Scale up quickly when pressure appears.
Scale down more carefully after pressure disappears.
```

Production lesson:

```text
Fast scale-up protects users.
Slow scale-down protects stability.
```

---

# 12. HPA Dry Run: Traffic Spike

Initial state:

```text
Deployment replicas = 3
HPA min = 3
HPA max = 20
Target CPU = 60%
Current average CPU = 45%
```

No action:

```text
Current below target but minReplicas already reached.
```

Then campaign starts.

```text
Traffic: 100 RPS -> 600 RPS
Average CPU: 45% -> 120%
```

HPA calculates:

```text
Desired replicas = 3 × 120 / 60 = 6
```

Flow:

```text
1. Metrics Server reports high CPU.
2. HPA reads average CPU 120%.
3. HPA updates Deployment replicas from 3 to 6.
4. ReplicaSet creates 3 new Pods.
5. Scheduler places Pods.
6. Kubelet pulls image and starts containers.
7. Readiness probe passes.
8. Service routes traffic to 6 Pods.
9. Average CPU falls near target.
```

ASCII:

```text
Before:

Service -> [Pod A] [Pod B] [Pod C]
              120%    118%    122%

After:

Service -> [A] [B] [C] [D] [E] [F]
           62% 58% 61% 55% 60% 59%
```

Important delay:

```text
Scale decision is not equal to immediate capacity.
New Pods need scheduling, image pull, startup, readiness.
```

Therefore:

```text
Autoscaling must be paired with fast startup and correct readiness.
```

---

# 13. Why VPA Exists

HPA answers:

```text
How many Pods do I need?
```

VPA answers:

```text
How much CPU and memory should each Pod request?
```

Many teams guess resource requests.

Example bad guess:

```yaml
requests:
  cpu: "100m"
  memory: "128Mi"
```

Reality:

```text
Spring Boot app normally needs:
CPU    600m during load
Memory 700Mi after warmup
```

Bad result:

```text
Scheduler thinks Pod is tiny.
Too many Pods placed on same node.
CPU contention appears.
Memory pressure appears.
OOMKilled may happen.
HPA math becomes misleading.
```

Another bad guess:

```yaml
requests:
  cpu: "4000m"
  memory: "8Gi"
```

Reality:

```text
App normally uses:
CPU    300m
Memory 600Mi
```

Result:

```text
Huge waste
Pods hard to schedule
Cluster scales up unnecessarily
```

VPA observes real usage and recommends better requests.

ASCII:

```text
Pod usage history
      |
      v
VPA recommender
      |
      v
Recommended requests
      |
      v
Pod spec adjusted or recommendation shown
```

Mental model:

```text
VPA is resource sizing intelligence.
```

---

# 14. VPA Components

VPA usually has three conceptual parts:

```text
Recommender
Updater
Admission Controller
```

Recommender:

```text
Looks at historical CPU/memory usage.
Calculates recommended requests.
```

Updater:

```text
Evicts Pods when their resources should be changed.
A recreated Pod gets new resources.
```

Admission Controller:

```text
Applies recommended resources when new Pods are created.
```

ASCII:

```text
+-------------------------+
| Existing Pods           |
| usage history           |
+-----------+-------------+
            |
            v
+-------------------------+
| VPA Recommender         |
| target/lower/upper      |
+-----------+-------------+
            |
            v
+-------------------------+
| VPA Updater             |
| decides eviction        |
+-----------+-------------+
            |
            v
+-------------------------+
| Admission Controller    |
| injects new requests    |
+-----------+-------------+
            |
            v
+-------------------------+
| New Pod with new CPU/mem|
+-------------------------+
```

Important:

```text
Most resource request changes require Pod recreation.
```

A running Pod cannot always be resized safely in the traditional model.

So VPA may evict and recreate Pods.

Production lesson:

```text
VPA can improve sizing,
but eviction behavior must be understood before enabling auto mode.
```

---

# 15. VPA Modes

VPA has modes.

The most important mental model:

```text
Off / Recommendation-only mode = observe and suggest
Auto mode = apply changes, usually by recreating Pods
```

Common modes:

```text
Off      -> only recommendations
Initial  -> apply only at Pod creation time
Auto     -> evict/recreate Pods to apply recommendations
Recreate -> similar idea: recreate Pods when needed
```

Recommendation-only example:

```yaml
apiVersion: autoscaling.k8s.io/v1
kind: VerticalPodAutoscaler
metadata:
  name: order-service
spec:
  targetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: order-service
  updatePolicy:
    updateMode: "Off"
```

Meaning:

```text
Watch order-service.
Recommend CPU/memory.
Do not change running Pods automatically.
```

Production-safe learning approach:

```text
1. Start VPA in Off mode.
2. Observe recommendations for days.
3. Compare with real p95/p99 load windows.
4. Manually adjust requests.
5. Only consider Auto for suitable workloads.
```

ASCII:

```text
Safe VPA adoption:

Observe -> Recommend -> Review -> Manually tune -> Maybe automate
```

Do not enable auto blindly for critical workloads.

Why?

```text
Unexpected Pod evictions during traffic can hurt availability.
```

---

# 16. VPA Recommendation Meaning

VPA recommendations usually include:

```text
lowerBound
 target
 upperBound
 uncappedTarget
```

Mental model:

```text
lowerBound = probably safe minimum
 target     = recommended request
 upperBound = safer higher value
```

Example:

```text
CPU:
  lowerBound: 300m
  target:     700m
  upperBound: 1200m

Memory:
  lowerBound: 512Mi
  target:     900Mi
  upperBound: 1500Mi
```

Meaning:

```text
This app probably should request around 700m CPU and 900Mi memory.
```

Use recommendations carefully.

For Spring Boot memory, consider:

```text
JVM heap
Metaspace
Thread stacks
Direct buffers
Netty buffers
Native memory
GC overhead
Actuator/prometheus overhead
Traffic burst behavior
```

ASCII:

```text
Container Memory
+--------------------------------+
| JVM heap                       |
| metaspace                      |
| thread stacks                  |
| direct/native memory           |
| libraries and runtime          |
+--------------------------------+
```

Production lesson:

```text
For Java services, memory request should not equal only -Xmx.
Container memory includes more than heap.
```

Example JVM setting:

```text
Container limit: 1024Mi
Heap max:        650Mi to 750Mi
Remaining:       native memory, metaspace, stacks, buffers
```

---

# 17. VPA And HPA Conflict

HPA and VPA can conflict if both act on CPU.

Why?

HPA CPU utilization uses:

```text
actual CPU usage / CPU request
```

VPA changes CPU request.

So VPA can change the denominator used by HPA.

Example:

```text
Actual CPU usage = 500m
CPU request      = 500m
Utilization      = 100%
```

HPA may scale up.

Then VPA changes request:

```text
Actual CPU usage = 500m
CPU request      = 1000m
Utilization      = 50%
```

Now HPA may scale down.

ASCII:

```text
VPA changes request
        |
        v
HPA CPU utilization changes
        |
        v
Replica decision changes
```

This can produce unstable behavior if configured badly.

Common safe patterns:

```text
Use HPA on CPU + VPA recommendation-only.
Use HPA on custom external metric + VPA for CPU/memory sizing.
Use VPA for workloads that do not need HPA.
Use HPA for stateless services and manual/VPA-off request tuning.
```

Mental model:

```text
Do not let two controllers fight over the same control signal.
```

Restaurant analogy:

```text
HPA manager says: add more chefs.
VPA manager says: give each chef a bigger station.
If both react to the same symptom without coordination,
the kitchen plan keeps changing.
```

---

# 18. Why Cluster Autoscaler Exists

HPA can create more Pods.

But Pods need Nodes.

If the cluster has no spare CPU/memory, new Pods become Pending.

Example:

```text
HPA wants replicas: 10
Current replicas: 5
New Pods created: 5

But nodes are full.
```

Result:

```text
5 new Pods stay Pending
```

Scheduler says:

```text
0/3 nodes are available: insufficient CPU
```

Cluster Autoscaler watches for unschedulable Pods.

If Pods cannot be scheduled because there is not enough node capacity, it asks cloud provider/node group to add nodes.

ASCII:

```text
HPA creates more Pods
        |
        v
Scheduler tries placement
        |
        v
Pods Pending due to insufficient resources
        |
        v
Cluster Autoscaler detects unschedulable Pods
        |
        v
Cloud provider adds Node
        |
        v
Scheduler places Pods
```

Mental model:

```text
HPA scales app workers.
Cluster Autoscaler scales the floor space for those workers.
```

Without Cluster Autoscaler:

```text
HPA can ask for more Pods,
but the cluster may have nowhere to run them.
```

---

# 19. Cluster Autoscaler Control Loop

Cluster Autoscaler asks two big questions:

```text
1. Are there Pods that cannot be scheduled because nodes are full?
2. Are there nodes that are underutilized and safe to remove?
```

Scale up path:

```text
Pending Pods exist
      |
      v
Check if adding node group capacity would help
      |
      v
Increase desired node count
      |
      v
Cloud provider creates VM
      |
      v
Node joins cluster
      |
      v
Scheduler places Pods
```

Scale down path:

```text
Node is underutilized
      |
      v
Check if its Pods can move elsewhere
      |
      v
Drain node
      |
      v
Delete node from node group
```

ASCII:

```text
+--------------------------+
| Pending Pods             |
+------------+-------------+
             |
             v
+--------------------------+
| Cluster Autoscaler       |
| simulate scheduling      |
+------------+-------------+
             |
             v
+--------------------------+
| Cloud Node Group         |
| desired size +1          |
+------------+-------------+
             |
             v
+--------------------------+
| New Node joins cluster   |
+--------------------------+
```

Important:

```text
Cluster Autoscaler does not scale because CPU is high directly.
It scales mainly because Pods are unschedulable.
```

That is a key interview point.

---

# 20. HPA And Cluster Autoscaler End-To-End Dry Run

Initial:

```text
Nodes: 3
Pods: 6
Traffic: normal
```

Each node is nearly full:

```text
Node 1: 2 Pods
Node 2: 2 Pods
Node 3: 2 Pods
```

Traffic spike:

```text
CPU rises above HPA target
```

HPA action:

```text
Deployment replicas: 6 -> 12
```

New Pods:

```text
Pod 7 Pending
Pod 8 Pending
Pod 9 Pending
Pod 10 Pending
Pod 11 Pending
Pod 12 Pending
```

Scheduler events:

```text
0/3 nodes are available: insufficient cpu
```

Cluster Autoscaler action:

```text
Node group desired size: 3 -> 5
```

Cloud provider creates nodes:

```text
Node 4 Ready
Node 5 Ready
```

Scheduler places Pods:

```text
Node 4: Pod 7, Pod 8, Pod 9
Node 5: Pod 10, Pod 11, Pod 12
```

ASCII:

```text
Traffic spike
    |
    v
HPA: replicas 6 -> 12
    |
    v
Scheduler: no room
    |
    v
Cluster Autoscaler: nodes 3 -> 5
    |
    v
Pods scheduled
    |
    v
Service has more ready endpoints
```

This is the full scaling chain.

Remember:

```text
HPA first creates demand for capacity.
Cluster Autoscaler reacts when cluster capacity is insufficient.
```

---

# 21. Pending Pod Debugging

When autoscaling fails, users often say:

```text
HPA is not working.
```

But the real problem may be:

```text
Pods were created but cannot be scheduled.
```

Debug path:

```bash
kubectl get hpa
kubectl get deploy order-service
kubectl get pods -o wide
kubectl describe pod <pending-pod>
kubectl get events --sort-by=.metadata.creationTimestamp
```

Look for:

```text
Insufficient cpu
Insufficient memory
node(s) had taint that the pod didn't tolerate
node affinity rules not matched
max node group size reached
persistent volume zone mismatch
PodDisruptionBudget blocking scale down
```

ASCII debugging chain:

```text
HPA desired replicas correct?
        |
        v
Deployment replicas updated?
        |
        v
Pods created?
        |
        v
Pods scheduled?
        |
        v
Nodes available?
        |
        v
Cluster Autoscaler active?
```

Mental model:

```text
Autoscaling has multiple layers.
Do not debug only the HPA object.
Follow the chain from metric to replica to Pod to node.
```

---

# 22. Resource Requests Connect Everything

Resource requests are not just documentation.

They affect:

```text
Scheduler placement
HPA CPU utilization math
VPA recommendation quality
Cluster Autoscaler decisions
Node utilization
Cost efficiency
QoS class
```

ASCII:

```text
resources.requests
        |
        +--> Scheduler: can this Pod fit?
        |
        +--> HPA: CPU usage percentage denominator
        |
        +--> VPA: what should be adjusted?
        |
        +--> Cluster Autoscaler: how many nodes needed?
        |
        +--> Cost: reserved cluster capacity
```

Bad requests cause bad scaling.

Too low:

```text
Scheduler overpacks nodes
CPU contention
Memory pressure
HPA utilization looks very high
Cluster may behave noisily
```

Too high:

```text
Pods cannot schedule
Cluster scales up unnecessarily
Low utilization
High cost
```

Production lesson:

```text
Autoscaling quality depends on resource request quality.
```

For Java services:

```text
Start with measured load test values.
Use VPA recommendation in Off mode.
Validate with p95/p99 traffic.
Tune heap vs container memory.
Avoid copying random requests from another service.
```

---

# 23. Java/Spring Boot Resource Sizing Example

Suppose a Spring Boot order service is load tested.

Observed stable load:

```text
300 RPS per Pod
p95 latency: 120ms
CPU usage: 650m
Memory usage: 720Mi
```

Possible starting resources:

```yaml
resources:
  requests:
    cpu: "700m"
    memory: "900Mi"
  limits:
    cpu: "1400m"
    memory: "1200Mi"
```

JVM settings:

```yaml
env:
  - name: JAVA_TOOL_OPTIONS
    value: >-
      -XX:MaxRAMPercentage=65
      -XX:+UseG1GC
      -XX:+ExitOnOutOfMemoryError
```

Why not set heap to full container memory?

```text
Container memory includes:
JVM heap
Metaspace
Thread stacks
Direct buffers
JIT/code cache
Native libraries
Monitoring agent overhead
```

ASCII:

```text
1200Mi container limit
+----------------------------------+
| Heap ~780Mi                      |
| Metaspace                        |
| Thread stacks                    |
| Direct buffers                   |
| Native overhead                  |
| Safety margin                    |
+----------------------------------+
```

HPA:

```yaml
metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 65
```

Meaning:

```text
When average usage exceeds about 65% of 700m,
scale out.
```

That is around:

```text
455m average CPU usage per Pod
```

If your Pod can handle 300 RPS at 650m CPU, target 65% may scale earlier to protect latency.

---

# 24. Autoscaling And Readiness

Autoscaling creates Pods.

But traffic should go only to ready Pods.

If readiness is wrong, autoscaling becomes dangerous.

Bad readiness:

```text
App starts
Readiness immediately passes
But DB pool not ready
Cache not warm
Kafka producer not connected
```

Result:

```text
Service sends traffic too early
New Pods fail requests
Scale-up causes more errors
```

Good readiness:

```text
App starts
Readiness fails during warmup
DB connection validated
Cache loaded enough
Critical dependencies OK
Readiness passes
Traffic starts
```

ASCII:

```text
New Pod created by HPA
        |
        v
Container starts
        |
        v
Readiness fails while warming
        |
        v
No traffic yet
        |
        v
Readiness passes
        |
        v
Service adds endpoint
```

Spring Boot:

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
  initialDelaySeconds: 20
  periodSeconds: 5
```

Production lesson:

```text
HPA scale-up is only useful when new Pods become ready safely and quickly.
```

---

# 25. Autoscaling And Startup Time

HPA is reactive.

It sees metrics after load increases.

Then it creates Pods.

Then Pods need time.

Timeline:

```text
T+00s traffic spike
T+15s metrics reflect pressure
T+30s HPA scales replicas
T+35s scheduler places Pods
T+50s image pulled
T+75s Spring Boot started
T+90s readiness passes
T+95s traffic reaches new Pods
```

ASCII:

```text
Spike -------------------------------------->
     metrics delay
          HPA decision
              Pod scheduling
                  image pull
                      app startup
                          readiness
                              useful capacity
```

This means:

```text
Autoscaling is not instant.
```

To improve:

```text
Keep minReplicas high enough for normal bursts
Optimize Docker image size
Use fast Spring Boot startup practices
Avoid slow migrations on startup
Use startupProbe for slow boot
Use readiness correctly
Pre-pull images if needed
Use cluster overprovisioning for critical workloads
```

Mental model:

```text
Autoscaling protects sustained or predictable growth better than instant micro-spikes.
```

For very sudden spikes, you still need:

```text
Buffer capacity
Rate limiting
Queues
Caching
CDN
Backpressure
```

---

# 26. Production Story: HPA Not Scaling

Symptom:

```text
Traffic is high.
Pods are overloaded.
HPA replicas stay at 3.
```

Debug:

```bash
kubectl get hpa order-service
kubectl describe hpa order-service
kubectl top pods
kubectl get deploy order-service -o yaml | grep -A10 resources
```

Possible output:

```text
Metrics: <unknown>/60%
```

Root cause:

```text
Deployment has no CPU requests.
```

Bad deployment:

```yaml
resources: {}
```

Fix:

```yaml
resources:
  requests:
    cpu: "500m"
    memory: "512Mi"
```

After fix:

```text
HPA can calculate CPU utilization.
```

Production lesson:

```text
Before blaming HPA,
check metrics and resource requests.
```

Debugging mindset:

```text
HPA decision requires:
1. target exists
2. metrics available
3. Pod requests valid
4. min/max allows movement
5. scale target is correct
```

---

# 27. Production Story: HPA Scales But Latency Still Bad

Symptom:

```text
HPA scales order-service from 3 to 20 Pods.
CPU falls.
But p99 latency remains high.
```

Possible reason:

```text
Database is the bottleneck.
```

Before scaling:

```text
3 Pods -> DB connection pool total 60
```

After scaling:

```text
20 Pods -> DB connection pool total 400
```

Database becomes overloaded.

ASCII:

```text
20 Pods
  | | | | | | | | | |
  v v v v v v v v v v
+----------------------+
| PostgreSQL           |
| max connections hit  |
| locks / slow queries |
+----------------------+
```

HPA made app layer wider.

But the bottleneck moved downstream.

Fix ideas:

```text
Tune DB queries and indexes
Use connection pool limits
Use PgBouncer
Add caching
Use queue for async work
Scale database/read replicas if appropriate
Scale on latency or queue length, not only CPU
Protect DB with bulkheads/rate limits
```

Mental model:

```text
Autoscaling one layer can overload another layer.
```

Production lesson:

```text
HPA is not system design.
It is one capacity tool inside system design.
```

---

# 28. Production Story: Cluster Autoscaler Maxed Out

Symptom:

```text
HPA wants 30 Pods.
Only 18 are running.
12 Pods are Pending.
```

Pod event:

```text
0/6 nodes are available: insufficient memory
```

Cluster Autoscaler logs:

```text
max node group size reached
```

Meaning:

```text
Cluster Autoscaler wanted to add nodes,
but node group maximum prevented it.
```

ASCII:

```text
HPA asks for Pods
        |
        v
Scheduler says no room
        |
        v
Cluster Autoscaler asks for node
        |
        v
Node group limit says no
        |
        v
Pods remain Pending
```

Fix options:

```text
Increase node group max size
Use larger instance type
Reduce Pod requests if overestimated
Split workloads across node pools
Use priority classes for critical workloads
Check quotas in cloud account
```

Debug commands:

```bash
kubectl describe pod <pending-pod>
kubectl -n kube-system logs deploy/cluster-autoscaler
kubectl get nodes
kubectl top nodes
```

Production lesson:

```text
Autoscaling has ceilings.
Always know min/max replicas and min/max node group size.
```

---

# 29. Production Story: Scale Down Breaks Availability

Symptom:

```text
Traffic drops.
HPA scales down from 12 to 4 Pods.
During scale down, users see errors.
```

Possible causes:

```text
Pods terminated while handling requests
No graceful shutdown
Readiness not flipped before shutdown
terminationGracePeriodSeconds too short
Load balancer still sends traffic to terminating Pods
Long requests killed midway
```

Spring Boot graceful shutdown:

```yaml
server:
  shutdown: graceful
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

Kubernetes:

```yaml
terminationGracePeriodSeconds: 45
lifecycle:
  preStop:
    exec:
      command: ["sh", "-c", "sleep 10"]
```

ASCII:

```text
Pod selected for removal
        |
        v
Stop accepting new traffic
        |
        v
Drain existing requests
        |
        v
Exit cleanly
```

Production lesson:

```text
Scale-down must be graceful.
Autoscaling is not only adding capacity.
It is also safely removing capacity.
```

---

# 30. PodDisruptionBudget And Autoscaling

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
During voluntary disruptions, keep at least 2 Pods available.
```

This matters for:

```text
Cluster Autoscaler scale-down
Node drains
Maintenance
Rolling updates
VPA evictions
```

ASCII:

```text
Node scale-down wants to evict Pod
        |
        v
PDB checks availability
        |
        +--> safe? allow eviction
        |
        +--> unsafe? block eviction
```

Good:

```text
Prevents taking down too many replicas.
```

Risk:

```text
Too strict PDB can block node scale-down forever.
```

Example problem:

```text
replicas: 1
PDB minAvailable: 1
```

Result:

```text
Pod cannot be voluntarily evicted.
Cluster Autoscaler may not remove node.
```

Mental model:

```text
PDB is an availability guardrail,
but guardrails can also block automation.
```

---

# 31. Choosing MinReplicas And MaxReplicas

`minReplicas` is your baseline safety capacity.

`maxReplicas` is your blast-radius and cost ceiling.

Bad minReplicas:

```text
minReplicas: 1
```

For production critical service, this may be risky:

```text
One Pod restart = no capacity
One node issue = outage
Scale-up delay hurts users
```

Better:

```text
minReplicas: 3
```

Why 3?

```text
Can spread across nodes/zones
Can survive one Pod disruption
Can handle small bursts before HPA reacts
```

MaxReplicas should consider downstream capacity.

Bad:

```text
maxReplicas: 200
```

If each Pod has 20 DB connections:

```text
200 Pods × 20 = 4000 DB connections
```

Maybe your DB cannot handle that.

ASCII:

```text
maxReplicas
    |
    v
max app capacity
    |
    v
max downstream pressure
    |
    v
DB/cache/message broker limits
```

Production formula mindset:

```text
maxReplicas is not only a Kubernetes number.
It is a system capacity contract.
```

---

# 32. Scaling Stateful Workloads

HPA works best for stateless workloads.

Stateful workloads are harder.

Example:

```text
Kafka
PostgreSQL
Elasticsearch
Redis Cluster
Stateful Spring Boot with local sessions
```

Why harder?

```text
New replica may need data rebalancing
Storage may need provisioning
Leader/follower roles matter
Network identity matters
Warmup can be long
Scale-down may require data movement
```

ASCII:

```text
Stateless:
Add Pod -> receives traffic quickly

Stateful:
Add Pod -> attach storage -> join cluster -> rebalance -> ready
```

For StatefulSet:

```text
Pods have stable identity:
app-0
app-1
app-2
```

Scaling down stateful workloads can be dangerous:

```text
Which node owns data?
Is data replicated?
Is shard moved?
Is quorum safe?
```

Production lesson:

```text
Do not blindly use HPA on stateful systems.
Understand the data model and cluster behavior first.
```

For Java backend developers:

```text
Scale stateless API Pods horizontally.
Scale databases and brokers using their own operational model.
```

---

# 33. Autoscaling Architecture For A Real Product

Example product:

```text
E-commerce checkout platform
```

Services:

```text
api-gateway
order-service
payment-service
inventory-service
notification-worker
PostgreSQL
Redis
Kafka
```

Autoscaling design:

```text
api-gateway:
  HPA on CPU/RPS

order-service:
  HPA on CPU + p95 latency alerting

payment-service:
  conservative HPA, strict maxReplicas, circuit breaker

notification-worker:
  HPA on Kafka lag

PostgreSQL:
  not simple HPA; use DB scaling strategy

Redis:
  managed cluster or careful operator-based scaling

Cluster:
  Cluster Autoscaler with multiple node groups
```

ASCII:

```text
Users
  |
  v
Gateway Pods  <--- HPA CPU/RPS
  |
  v
Order Pods    <--- HPA CPU
  |
  +--> Payment Pods <--- controlled HPA max
  |
  +--> Kafka topic
           |
           v
      Notification Workers <--- HPA Kafka lag

Nodes <--- Cluster Autoscaler
```

Mental model:

```text
Each workload needs a scaling signal matching its real bottleneck.
```

Do not use one autoscaling rule for every service.

---

# 34. Debugging Autoscaling Layer By Layer

Use this order.

```text
1. Is load actually reaching the app?
2. Are metrics visible?
3. Does HPA show current metric?
4. Is target reasonable?
5. Did HPA update replicas?
6. Did Deployment create Pods?
7. Are Pods scheduled?
8. Are Nodes available?
9. Did Cluster Autoscaler add nodes?
10. Did new Pods become Ready?
11. Did Service include new endpoints?
12. Did latency improve?
```

Commands:

```bash
kubectl get hpa
kubectl describe hpa order-service
kubectl top pods
kubectl get deploy order-service
kubectl get rs
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl get events --sort-by=.metadata.creationTimestamp
kubectl get nodes
kubectl top nodes
kubectl get endpoints order-service
```

ASCII:

```text
Metric
  |
  v
HPA
  |
  v
Deployment replicas
  |
  v
Pods
  |
  v
Scheduler
  |
  v
Nodes
  |
  v
Readiness
  |
  v
Service endpoints
  |
  v
User latency
```

Mindset:

```text
Never debug autoscaling from only one object.
Autoscaling is a chain.
```

---

# 35. Common Autoscaling Mistakes

```text
Mistake 1:
Using HPA without CPU requests.

Mistake 2:
Scaling on CPU when real bottleneck is database or external API.

Mistake 3:
Setting minReplicas too low for production.

Mistake 4:
Setting maxReplicas without checking DB/cache capacity.

Mistake 5:
Expecting autoscaling to handle instant traffic explosions.

Mistake 6:
Slow Spring Boot startup causing late scale-up usefulness.

Mistake 7:
Wrong readiness probe sending traffic too early.

Mistake 8:
Using VPA Auto with HPA CPU without understanding conflict.

Mistake 9:
Forgetting Cluster Autoscaler max node group limit.

Mistake 10:
No graceful shutdown during scale-down.

Mistake 11:
Treating autoscaling as a replacement for load testing.

Mistake 12:
Ignoring downstream systems when app replicas increase.
```

Correct mental model:

```text
Autoscaling is controlled capacity adjustment.
It must be designed, tested, observed, and bounded.
```

---

# 36. Interview Questions

## What is HPA?

HPA stands for Horizontal Pod Autoscaler. It automatically adjusts the number of Pod replicas for a workload such as a Deployment based on metrics like CPU utilization, memory, or custom metrics.

## What does HPA change?

HPA changes the replica count of the target workload. It does not directly create containers. It updates the scale subresource, and normal Kubernetes controllers create or remove Pods.

## What is VPA?

VPA stands for Vertical Pod Autoscaler. It recommends or adjusts CPU and memory requests for Pods based on observed usage. It helps right-size workloads.

## What does VPA change?

VPA changes resource requests, such as CPU and memory requests. In many cases, applying changes requires Pod recreation.

## What is Cluster Autoscaler?

Cluster Autoscaler adjusts the number of worker nodes in a cluster. It adds nodes when Pods are unschedulable due to insufficient resources and removes underutilized nodes when their Pods can be safely moved elsewhere.

## Difference between HPA, VPA, and Cluster Autoscaler?

HPA changes the number of Pods. VPA changes the size of Pods by tuning resource requests. Cluster Autoscaler changes the number of Nodes.

## Why does CPU-based HPA need CPU requests?

CPU utilization is calculated as actual CPU usage divided by requested CPU. Without CPU requests, Kubernetes cannot calculate utilization percentage properly for CPU-based HPA.

## Can HPA and VPA be used together?

Yes, but carefully. HPA on CPU and VPA changing CPU requests can conflict because VPA changes the denominator used in HPA utilization. Safer patterns include VPA recommendation-only with CPU HPA, or HPA on custom metrics while VPA manages resource requests.

## Does Cluster Autoscaler scale when CPU is high?

Not directly in the usual model. Cluster Autoscaler primarily scales up when Pods are unschedulable due to insufficient cluster resources. High CPU may lead HPA to create more Pods, and those Pending Pods may then trigger Cluster Autoscaler.

## Why can HPA scale but latency remain high?

Because the bottleneck may be elsewhere, such as database connections, locks, slow queries, external APIs, or queue saturation. HPA scales the application layer, not every downstream dependency.

---

# 37. Cheat Sheet

```text
HPA
  Full name: Horizontal Pod Autoscaler
  Changes: replicas
  Best for: stateless scalable workloads
  Signal: CPU, memory, custom/external metrics

VPA
  Full name: Vertical Pod Autoscaler
  Changes: CPU/memory requests
  Best for: right-sizing workloads
  Modes: Off, Initial, Auto/Recreate

Cluster Autoscaler
  Changes: node count
  Scale up trigger: unschedulable Pods
  Scale down trigger: underutilized removable nodes

CPU request
  Used by scheduler
  Used by HPA CPU utilization math
  Used by autoscaling capacity planning

minReplicas
  Baseline safety capacity

maxReplicas
  Cost and downstream protection ceiling

Readiness
  Decides when new scaled Pods receive traffic

Graceful shutdown
  Protects requests during scale-down
```

Core chain:

```text
Load increases
    |
    v
Metrics increase
    |
    v
HPA increases replicas
    |
    v
Pods are created
    |
    v
Scheduler places Pods
    |
    v
If no room, Cluster Autoscaler adds Nodes
    |
    v
Pods become Ready
    |
    v
Service sends traffic
```

---

# 38. One Picture To Remember

```text
                         USER TRAFFIC
                              |
                              v
                     +----------------+
                     | Metrics change |
                     +-------+--------+
                             |
                             v
        +---------------------------------------------+
        | HPA                                         |
        | changes number of Pods                     |
        | replicas: 3 -> 8                            |
        +----------------------+----------------------+
                               |
                               v
                     +----------------+
                     | New Pods       |
                     +-------+--------+
                             |
                  enough node space?
                         /       \
                       yes        no
                       |           |
                       v           v
              +-------------+   +----------------------+
              | Pods run    |   | Cluster Autoscaler   |
              | on nodes    |   | adds Nodes           |
              +-------------+   +----------+-----------+
                                      |
                                      v
                                +-------------+
                                | Pods run    |
                                +-------------+

        +---------------------------------------------+
        | VPA                                         |
        | changes size of Pods                        |
        | CPU/memory requests                         |
        +---------------------------------------------+
```

Final memory hook:

```text
HPA = more Pods
VPA = bigger/smaller Pods
Cluster Autoscaler = more/fewer Nodes
```

---

# 39. Final Production Checklist

```text
[ ] Every production workload has CPU and memory requests.
[ ] HPA target metric represents real pressure.
[ ] minReplicas protects normal bursts and availability.
[ ] maxReplicas respects downstream capacity.
[ ] Metrics Server works: kubectl top pods/nodes.
[ ] HPA current metrics are not <unknown>.
[ ] Readiness probe prevents traffic before warmup.
[ ] Startup time is measured and acceptable.
[ ] Graceful shutdown works during scale-down.
[ ] Cluster Autoscaler node group max is known.
[ ] Pending Pods are monitored.
[ ] VPA starts in recommendation-only mode unless carefully tested.
[ ] HPA/VPA conflicts are avoided.
[ ] Load tests validate scaling behavior.
[ ] Alerts cover HPA maxed out, Pending Pods, node pressure, p99 latency.
```

---

# 40. Final Memory Hook

Do not memorize autoscaling as three random Kubernetes features.

Remember it as capacity control at three levels:

```text
Application replica capacity  -> HPA
Per-Pod resource capacity     -> VPA
Cluster machine capacity      -> Cluster Autoscaler
```

Autoscaling is not:

```text
A magic button that fixes performance.
```

Autoscaling is:

```text
A feedback loop that changes capacity based on signals.
```

Best final picture:

```text
Demand changes.
Metrics reveal pressure.
Controllers adjust capacity.
Kubernetes reconciles reality.
Users should experience stable service.
```

Final sentence:

```text
Autoscaling works well only when metrics, resource requests, readiness, startup time, node capacity, and downstream limits are designed together.
```
