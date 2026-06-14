# 010_Requests_Limits_QoS.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Requests, Limits, and QoS Exist

Most Kubernetes beginners think scheduling is only this:

```text
Pod exists
  |
  v
Scheduler chooses any node
```

That is incomplete.

In production, nodes are shared machines. Many applications run on the same worker nodes. Some are small internal APIs. Some are heavy payment services. Some are batch jobs. Some are JVM services with large heap usage. If every Pod could consume unlimited CPU and memory, one bad service could damage the whole node.

Requests and limits exist because Kubernetes must answer two different questions:

```text
1. How much resource should I reserve when placing this Pod?
2. How much resource should this Pod be allowed to consume at runtime?
```

The first question is about scheduling.

The second question is about isolation.

Mental model:

```text
request = reservation for scheduling
limit   = maximum allowed runtime usage
QoS     = eviction priority / survival class
```

One picture:

```text
Node capacity
+------------------------------------------------+
| CPU: 4 cores                                   |
| Memory: 8 Gi                                   |
+------------------------------------------------+
        |
        | scheduler places pods using requests
        v
+------------------------------------------------+
| Pod A request: 1 CPU, 1 Gi                     |
| Pod B request: 2 CPU, 3 Gi                     |
| Pod C request: 1 CPU, 2 Gi                     |
+------------------------------------------------+

Runtime actual usage may be different from request.
Limits decide how far a container may go.
```

Do not memorize `resources.requests` and `resources.limits` as YAML fields. Understand them as the contract between your application and the cluster.

---

# 2. The Wrong Way To Think About Resources

Wrong model:

```text
CPU request means app always gets that CPU.
Memory request means app always uses that memory.
Limit means Kubernetes will politely slow everything down.
```

Correct model:

```text
CPU request influences scheduling and CPU share under contention.
Memory request influences scheduling and eviction priority.
CPU limit can throttle.
Memory limit can kill.
```

This difference is very important for Java/Spring Boot services.

A Spring Boot service may start fine locally. But inside Kubernetes, it may behave differently because the JVM sees container constraints, heap sizing, GC behavior, native memory, thread stacks, direct buffers, metaspace, and loaded classes.

Bad thinking:

```text
My app uses 600 Mi locally.
Set memory limit to 600 Mi.
Done.
```

Better thinking:

```text
Heap + metaspace + thread stacks + direct buffers + native libs + JVM overhead + safety margin.
```

ASCII:

```text
Container Memory Limit
+------------------------------------------------+
| JVM Heap                                       |
| Metaspace                                      |
| Thread stacks                                  |
| Direct buffers                                 |
| Native memory                                  |
| JIT/code cache                                 |
| Spring objects                                 |
| Safety margin                                  |
+------------------------------------------------+
```

When memory limit is too low, Kubernetes does not ask your app nicely to reduce memory. The kernel OOM killer may terminate the container.

This is why resource sizing is production engineering, not YAML decoration.

---

# 3. Real World Analogy: Office Seating and Fire Safety

Imagine an office building.

Each team asks for seats.

```text
Team A requests 10 seats.
Team B requests 20 seats.
Team C requests 5 seats.
```

The office manager assigns floors based on requested seats. That is like Kubernetes scheduling with requests.

But there is also a fire safety maximum.

```text
Room capacity = 30 people maximum
```

Even if a team only requested 10 seats, if 60 people enter the room, the building becomes unsafe. That is like runtime limits.

Mapping:

```text
Office building       -> Node
Team                  -> Pod
Requested seats       -> resources.requests
Room max capacity     -> resources.limits
Office manager        -> Scheduler
Fire safety eviction  -> kernel/Kubelet eviction/OOM behavior
```

Diagram:

```text
                 OFFICE / NODE
+------------------------------------------------+
| Capacity: 40 seats                             |
|                                                |
| Team A requested 10 seats                      |
| Team B requested 20 seats                      |
| Team C requested 5 seats                       |
|                                                |
| Total requested: 35                            |
| Remaining schedulable seats: 5                 |
+------------------------------------------------+
```

The manager does not place teams based on their dreams. The manager places teams based on declared requests.

In Kubernetes, if you do not declare requests, the scheduler has less reliable information. That is how noisy-neighbor problems begin.

---

# 4. The Core Kubernetes Resource Picture

```text
Developer writes Pod spec
          |
          v
resources.requests / resources.limits
          |
          v
API Server stores desired state
          |
          v
Scheduler reads requests
          |
          v
Node chosen if enough allocatable resources
          |
          v
Kubelet starts container with runtime constraints
          |
          v
Linux cgroups enforce CPU/memory behavior
```

Requests are mostly scheduling input.

Limits become runtime constraints.

QoS class is derived from request/limit configuration.

```text
Pod resources section
        |
        +--> Scheduler placement decision
        |
        +--> Container runtime cgroup settings
        |
        +--> QoS class calculation
        |
        +--> Eviction priority under node pressure
```

One picture:

```text
resources:
  requests:
    cpu: "500m"
    memory: "512Mi"
  limits:
    cpu: "1"
    memory: "1Gi"

        |
        v
+-------------------+       +--------------------+
| Scheduler         |       | Kubelet / Runtime   |
| uses requests     |       | uses limits         |
+-------------------+       +--------------------+
```

This split is the heart of the chapter.

---

# 5. CPU Mental Model

CPU in Kubernetes is measured in cores.

```text
1 CPU  = 1 core / 1 vCPU / 1 hyperthread depending on infrastructure
500m   = 0.5 CPU
100m   = 0.1 CPU
```

The `m` means millicpu.

```text
1000m = 1 CPU
500m  = half CPU
250m  = quarter CPU
```

Mental model:

```text
CPU request = how much CPU share I need for scheduling and fairness
CPU limit   = maximum CPU time I am allowed to consume
```

CPU is compressible.

That means if the app wants more CPU than available, it can be slowed down instead of immediately killed.

Diagram:

```text
CPU pressure
+------------------------------------------------+
| App wants 2 CPUs                               |
| Limit is 1 CPU                                 |
| Result: app is throttled                       |
| Symptom: latency rises, p99 gets worse         |
+------------------------------------------------+
```

For Spring Boot:

```text
CPU throttling can cause:
- slow request handling
- slow garbage collection
- timeout spikes
- readiness probe failures
- Kafka consumer lag
- Hikari pool starvation symptoms
```

Important:

```text
CPU limit does not kill the container.
CPU limit throttles execution.
```

But throttling can still produce severe production incidents.

---

# 6. Memory Mental Model

Memory is different from CPU.

CPU is compressible. Memory is not safely compressible.

If a container exceeds memory limit, it can be killed.

```text
Memory request = scheduling reservation and eviction signal
Memory limit   = hard ceiling for container memory usage
```

Diagram:

```text
Container memory usage

0 Mi                request                 limit
|--------------------|------------------------|
                     512Mi                   1Gi

If usage goes above limit:

usage > 1Gi
   |
   v
OOMKilled
```

Memory pressure story:

```text
Node has 8 Gi memory.
Many Pods run on it.
Total actual memory usage grows.
Kubelet detects memory pressure.
Pods with weaker QoS may be evicted first.
If one container crosses its limit, it may be OOMKilled.
```

Spring Boot memory is not only heap:

```text
Total container memory
  = JVM heap
  + metaspace
  + thread stacks
  + direct buffers
  + native memory
  + code cache
  + GC structures
  + libraries
```

Safe Java sizing example:

```text
Container limit: 1Gi
Heap max:        650Mi to 750Mi
Remaining:       JVM/native/headroom
```

Never set `-Xmx` equal to the container memory limit.

---

# 7. Requests vs Limits: The Most Important Difference

```text
Request = promise you ask from cluster
Limit   = boundary cluster enforces at runtime
```

Table:

```text
+----------------+------------------------------+------------------------------+
| Field          | Used By                      | Meaning                      |
+----------------+------------------------------+------------------------------+
| CPU request    | Scheduler, CPU shares        | Minimum expected CPU share    |
| CPU limit      | Runtime cgroup               | Max CPU before throttling     |
| Memory request | Scheduler, eviction ranking  | Expected memory need          |
| Memory limit   | Runtime cgroup / kernel      | Max memory before kill        |
+----------------+------------------------------+------------------------------+
```

Dry example:

```yaml
resources:
  requests:
    cpu: "500m"
    memory: "512Mi"
  limits:
    cpu: "1"
    memory: "1Gi"
```

Meaning:

```text
Please place this Pod only where at least 0.5 CPU and 512Mi memory are available.
At runtime, do not allow this container to use more than 1 CPU and 1Gi memory.
```

Not meaning:

```text
This app will always use exactly 500m CPU and 512Mi memory.
```

Actual usage may be lower or higher than request.

---

# 8. Node Capacity vs Allocatable

A node has capacity.

But not all capacity is available for Pods.

```text
Node capacity = physical/VM resources
Node allocatable = resources available to Pods after system reservation
```

Diagram:

```text
Node Capacity: 8 CPU, 32 Gi
+------------------------------------------------+
| kubelet reserved                               |
| OS reserved                                    |
| system daemons                                 |
| eviction reserved                              |
|                                                |
| Allocatable for Pods                           |
+------------------------------------------------+
```

Example:

```text
Node capacity:
CPU:    8
Memory: 32Gi

Reserved:
CPU:    1
Memory: 3Gi

Allocatable:
CPU:    7
Memory: 29Gi
```

Scheduler uses allocatable resources, not raw marketing capacity.

Check:

```bash
kubectl describe node <node-name>
```

Look for:

```text
Capacity:
Allocatable:
Allocated resources:
```

Production mindset:

```text
When Pods are Pending, do not only ask "node has enough CPU?"
Ask "node has enough allocatable CPU after existing requests?"
```

---

# 9. Scheduler Dry Run With Requests

Suppose a node has:

```text
Allocatable:
CPU:    4
Memory: 8Gi
```

Current scheduled Pods request:

```text
Pod A: 1 CPU, 2Gi
Pod B: 1 CPU, 2Gi
```

Remaining schedulable capacity:

```text
CPU:    2
Memory: 4Gi
```

New Pod requests:

```text
CPU:    1500m
Memory: 3Gi
```

Scheduler checks:

```text
CPU remaining    2 CPU >= 1.5 CPU   OK
Memory remaining 4Gi   >= 3Gi       OK
```

Pod can fit.

Diagram:

```text
Node allocatable
+--------------------------------------+
| CPU 4                                |
| Memory 8Gi                           |
+--------------------------------------+
        |
        v
Already requested
+--------------------------------------+
| Pod A: 1 CPU, 2Gi                    |
| Pod B: 1 CPU, 2Gi                    |
+--------------------------------------+
        |
        v
Remaining
+--------------------------------------+
| CPU 2                                |
| Memory 4Gi                           |
+--------------------------------------+
        |
        v
New Pod request: 1.5 CPU, 3Gi -> FITS
```

If a Pod is Pending, always compare requests against remaining allocatable resources.

---

# 10. Overcommit Mental Model

Kubernetes can allow actual usage to exceed requests, as long as limits and node resources allow it.

Example:

```text
Pod A request: 500m CPU, actual usage: 100m
Pod B request: 500m CPU, actual usage: 900m
```

This is normal.

Requests are not fixed usage.

But if many Pods burst together, contention happens.

```text
Normal time:
Pod A low usage
Pod B low usage
Pod C low usage
Node looks fine

Traffic spike:
Pod A high usage
Pod B high usage
Pod C high usage
CPU pressure begins
Latency increases
```

ASCII:

```text
Requests are reservations:
+-----+-----+-----+
| A   | B   | C   |
+-----+-----+-----+

Actual usage may burst:
+--------------+---------+--------------+
| A burst      | B       | C burst      |
+--------------+---------+--------------+
```

Production lesson:

```text
Requests are capacity planning inputs.
Wrong requests create either wasted nodes or unstable nodes.
```

Too high requests:

```text
Low utilization, expensive cluster
```

Too low requests:

```text
Overpacked nodes, throttling, eviction, noisy neighbor
```

---

# 11. QoS Classes Mental Model

Kubernetes assigns a QoS class to Pods based on resource configuration.

QoS means Quality of Service.

It affects eviction priority under node resource pressure.

Three classes:

```text
Guaranteed
Burstable
BestEffort
```

Survival ranking:

```text
Most protected
  |
  v
Guaranteed
  |
  v
Burstable
  |
  v
BestEffort
Least protected
```

Diagram:

```text
Node memory pressure
        |
        v
Which Pods should be evicted first?
        |
        v
BestEffort -> Burstable -> Guaranteed
```

This does not mean Guaranteed Pods can never die.

It means they are less likely to be evicted before lower QoS Pods.

Mental model:

```text
QoS class = survival priority when the node is stressed
```

---

# 12. Guaranteed QoS

A Pod gets Guaranteed QoS when every container has CPU and memory request equal to limit.

Example:

```yaml
resources:
  requests:
    cpu: "1"
    memory: "1Gi"
  limits:
    cpu: "1"
    memory: "1Gi"
```

Meaning:

```text
I need exactly this resource envelope.
Schedule me only if this is available.
Do not let me exceed it.
```

Diagram:

```text
Request == Limit

CPU:    [ 1 CPU reserved ][ 1 CPU max ]
Memory: [ 1Gi reserved  ][ 1Gi max  ]
```

Use cases:

```text
Critical platform components
Latency-sensitive services with predictable load
Services where eviction is very costly
```

Risk:

```text
If CPU limit is too tight, Java service may throttle.
If memory limit is too tight, service may OOMKill.
```

Guaranteed is not automatically best.

It is best when you understand the workload envelope.

---

# 13. Burstable QoS

A Pod gets Burstable QoS when it has some request/limit configuration but does not qualify for Guaranteed.

Common example:

```yaml
resources:
  requests:
    cpu: "500m"
    memory: "512Mi"
  limits:
    cpu: "2"
    memory: "1Gi"
```

Meaning:

```text
Reserve 500m CPU and 512Mi memory.
Allow bursting up to 2 CPU and 1Gi memory.
```

Diagram:

```text
CPU:
0       request                     limit
|---------|---------------------------|
          500m                        2 CPU

Memory:
0       request                     limit
|---------|---------------------------|
          512Mi                       1Gi
```

This is common for web services.

Reason:

```text
Normal traffic needs modest resources.
Short spikes need burst capacity.
```

But Burstable Pods can be evicted under memory pressure, especially if they exceed their requests and the node is stressed.

Production model:

```text
Burstable = efficient but needs monitoring
```

---

# 14. BestEffort QoS

A Pod gets BestEffort QoS when no CPU or memory requests/limits are set.

Example:

```yaml
resources: {}
```

Meaning:

```text
I did not tell Kubernetes what I need.
Schedule me without reservation.
Let me use leftover resources.
```

Diagram:

```text
Node under pressure
+--------------------------------+
| Guaranteed services             |
| Burstable services              |
| BestEffort pod                  |
+--------------------------------+
        |
        v
BestEffort is first eviction target
```

Use BestEffort only for non-critical workloads.

Examples:

```text
Temporary debug pod
One-off test
Very low importance background task
```

Do not run production Spring Boot APIs as BestEffort.

Bad production smell:

```text
No resources section in production Deployment
```

Because Kubernetes cannot schedule intelligently without resource requests.

---

# 15. Spring Boot Resource Example

Simple controller:

```java
package com.example.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @GetMapping("/orders/health-check-work")
    public String work() {
        long sum = 0;
        for (int i = 0; i < 5_000_000; i++) {
            sum += i;
        }
        return "ok-" + sum;
    }
}
```

This endpoint is CPU-heavy compared with a simple health endpoint.

If CPU limit is too low:

```text
Request enters app
CPU throttling begins
Tomcat worker takes longer
p95/p99 latency increases
Client timeout may happen
Readiness probe may fail under load
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
          resources:
            requests:
              cpu: "500m"
              memory: "512Mi"
            limits:
              cpu: "1"
              memory: "1Gi"
```

Interpretation:

```text
Normal expected CPU: 0.5 core
Maximum CPU:         1 core
Normal memory need:  512Mi
Maximum memory:      1Gi
```

---

# 16. JVM Heap and Container Limit

For Java services, memory sizing is critical.

Bad configuration:

```text
Container memory limit: 512Mi
JVM -Xmx:               512Mi
```

Why bad?

Because heap is not the whole container memory.

Better:

```text
Container memory limit: 1Gi
JVM max heap:           650Mi or 700Mi
Headroom:               remaining memory
```

Example Docker command style:

```bash
java -XX:MaxRAMPercentage=70 -jar app.jar
```

Kubernetes:

```yaml
env:
  - name: JAVA_TOOL_OPTIONS
    value: "-XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"
resources:
  requests:
    memory: "512Mi"
  limits:
    memory: "1Gi"
```

Memory picture:

```text
1Gi container limit
+------------------------------------------------+
| Heap around 70%                                |
| Metaspace                                      |
| Thread stacks                                  |
| Direct buffers                                 |
| Code cache                                     |
| Native memory                                  |
| Safety headroom                                |
+------------------------------------------------+
```

Production rule:

```text
Tune JVM with container limit in mind.
Do not let heap consume the entire limit.
```

---

# 17. Dry Run: Pod Pending Because Requests Are Too High

Deployment:

```yaml
resources:
  requests:
    cpu: "4"
    memory: "8Gi"
```

Cluster nodes:

```text
node-1 allocatable: 2 CPU, 4Gi free by requests
node-2 allocatable: 2 CPU, 6Gi free by requests
node-3 allocatable: 1 CPU, 8Gi free by requests
```

Scheduler tries:

```text
node-1: CPU not enough
node-2: CPU not enough
node-3: CPU not enough
```

Result:

```text
Pod Pending
```

Events:

```text
0/3 nodes are available: insufficient cpu
```

Debug:

```bash
kubectl describe pod <pod-name>
kubectl describe node <node-name>
kubectl top nodes
```

Mental model:

```text
Actual CPU usage may be low.
But scheduler looks at requested resources already placed.
```

This surprises many people.

---

# 18. Dry Run: CPU Throttling

Pod config:

```yaml
resources:
  requests:
    cpu: "500m"
  limits:
    cpu: "500m"
```

Traffic spike arrives.

Spring Boot wants 1.5 CPU worth of execution.

Runtime says:

```text
Limit is 0.5 CPU.
You cannot run faster than that.
```

Result:

```text
Requests queue in Tomcat
Latency increases
Kafka consumer falls behind
Health endpoints respond slowly
Autoscaler may react late
```

Diagram:

```text
Incoming requests
  | | | | | | | |
  v v v v v v v v
+--------------------+
| Spring Boot App    |
| wants more CPU     |
+---------+----------+
          |
          v
+--------------------+
| cgroup CPU limit   |
| throttle           |
+--------------------+
```

Debug signals:

```bash
kubectl top pod
kubectl describe pod <pod>
```

In Prometheus/cAdvisor setups, look for CPU throttling metrics.

Production lesson:

```text
CPU limit protects nodes but can hurt latency-sensitive Java services.
```

---

# 19. Dry Run: Memory OOMKilled

Pod config:

```yaml
resources:
  limits:
    memory: "512Mi"
```

App behavior:

```text
Spring context loads
Hibernate metadata loads
Connection pool starts
Kafka client starts
Traffic creates objects
Heap grows
Native memory grows
```

Memory crosses 512Mi.

Result:

```text
Container killed
Pod restarts
Status: OOMKilled
```

Debug:

```bash
kubectl get pod
kubectl describe pod <pod-name>
kubectl logs <pod-name> --previous
```

Look for:

```text
Last State: Terminated
Reason: OOMKilled
Exit Code: 137
```

Mental picture:

```text
Memory usage
0Mi ------------------------ 512Mi limit ---- X
                                      |
                                      v
                                  killed
```

Important:

```text
OOMKilled is not a normal Java exception.
Your process may not get time to log a beautiful stack trace.
```

---

# 20. Node Pressure and Eviction

Even if a container does not exceed its own memory limit, the node can come under pressure.

Example:

```text
Node memory allocatable: 16Gi
Many Pods burst memory at same time
Node available memory becomes low
Kubelet starts eviction process
```

Eviction order considers QoS and usage above requests.

Simplified mental model:

```text
BestEffort Pods first
Burstable Pods using more than requests next
Guaranteed Pods last
```

Diagram:

```text
Node Memory Pressure
        |
        v
+-----------------------------+
| Kubelet checks Pods         |
+-------------+---------------+
              |
              v
+-----------------------------+
| Evict lower priority victims|
+-----------------------------+
```

Symptoms:

```text
Pod status: Evicted
Reason: The node was low on resource: memory
```

Debug:

```bash
kubectl describe pod <pod>
kubectl describe node <node>
kubectl get events --sort-by=.lastTimestamp
```

Do not confuse:

```text
OOMKilled = container exceeded memory limit or kernel killed process
Evicted   = kubelet removed Pod due to node pressure
```

Both hurt availability, but root cause and remediation differ.

---

# 21. LimitRange Mental Model

A namespace can define default resource requests and limits using LimitRange.

Why?

Because teams may forget resources in Pod specs.

LimitRange lets platform teams say:

```text
In this namespace, containers should have default requests/limits.
Also, do not allow extremely tiny or extremely huge values.
```

Example:

```yaml
apiVersion: v1
kind: LimitRange
metadata:
  name: default-limits
spec:
  limits:
    - type: Container
      defaultRequest:
        cpu: "250m"
        memory: "256Mi"
      default:
        cpu: "1"
        memory: "1Gi"
```

Flow:

```text
Developer submits Pod without resources
        |
        v
API admission applies defaults
        |
        v
Pod gets request/limit values
```

ASCII:

```text
Namespace policy
      |
      v
LimitRange
      |
      v
Default resources added to Pods
```

This prevents BestEffort production workloads by accident.

---

# 22. ResourceQuota Mental Model

ResourceQuota limits total resource usage inside a namespace.

Why?

Because one team should not consume the entire cluster.

Example:

```yaml
apiVersion: v1
kind: ResourceQuota
metadata:
  name: team-quota
spec:
  hard:
    requests.cpu: "20"
    requests.memory: "40Gi"
    limits.cpu: "40"
    limits.memory: "80Gi"
    pods: "100"
```

Meaning:

```text
This namespace may request at most 20 CPU and 40Gi memory total.
It may set limits up to 40 CPU and 80Gi memory total.
It may create at most 100 Pods.
```

Diagram:

```text
Cluster
+---------------------------------------------+
| team-a namespace quota: 20 CPU              |
| team-b namespace quota: 30 CPU              |
| platform namespace quota: 10 CPU            |
+---------------------------------------------+
```

Production benefit:

```text
Prevents accidental cluster exhaustion.
Forces teams to think about capacity.
```

Failure symptom:

```text
Error from server (Forbidden): exceeded quota
```

---

# 23. HPA Connection: Requests Matter

Horizontal Pod Autoscaler often uses CPU utilization relative to CPU request.

Example:

```yaml
resources:
  requests:
    cpu: "500m"
```

If actual CPU is 250m:

```text
CPU utilization = 250m / 500m = 50%
```

If target is 70%, no scale out.

If actual CPU is 700m:

```text
CPU utilization = 700m / 500m = 140%
```

HPA may scale out.

Diagram:

```text
CPU actual usage
      |
      v
compare against CPU request
      |
      v
HPA utilization percentage
      |
      v
scale decision
```

Bad request causes bad scaling.

Too high CPU request:

```text
Actual 500m / Request 2000m = 25%
HPA thinks app is underused
Scale-out may not happen
```

Too low CPU request:

```text
Actual 500m / Request 100m = 500%
HPA may scale aggressively
```

Requests are not only scheduler hints. They affect autoscaling behavior too.

---

# 24. Production Story: Low CPU Limit Creates Fake Database Problem

Symptoms:

```text
Users report slow checkout.
APM shows slow DB calls.
Team blames PostgreSQL.
DB CPU looks normal.
Indexes look fine.
```

Hidden cause:

```text
order-service CPU limit = 300m
Traffic increased
App was CPU throttled
DB calls looked slow because request threads were delayed
```

Real chain:

```text
Low CPU limit
   |
   v
App threads run slowly
   |
   v
Connection pool wait increases
   |
   v
Request latency increases
   |
   v
Looks like DB slowness
```

Debug path:

```bash
kubectl top pod
kubectl describe pod order-service-xxx
Check CPU throttling metrics in monitoring
Compare latency with CPU usage and throttling
```

Lesson:

```text
Not every slow DB graph is a database problem.
Container CPU throttling can create misleading symptoms.
```

---

# 25. Production Story: OOMKilled During Startup

A Spring Boot app starts fine in dev.

Production Pod:

```text
CrashLoopBackOff
Last State: OOMKilled
```

YAML:

```yaml
resources:
  limits:
    memory: "512Mi"
```

Startup does:

```text
Load Spring context
Initialize Hibernate
Create connection pool
Load caches
Start Kafka consumers
Warm up templates
```

Peak startup memory exceeds steady-state memory.

Diagram:

```text
Memory usage over time

startup peak
    /
   /\
  /  \___ steady state
 /        \____
+---------------------> time

Limit set below startup peak -> OOMKilled
```

Fix options:

```text
Increase memory limit
Reduce heap percentage
Lazy-load heavy components if acceptable
Move cache warmup after readiness
Reduce thread count
Profile memory properly
```

Lesson:

```text
Size for startup peak and traffic peak, not only idle memory.
```

---

# 26. Production Story: Missing Requests Overpacked The Node

A team deploys many services without requests.

Scheduler sees:

```text
No requests
Very cheap to schedule
```

Many Pods land on same node.

At 10 AM traffic starts.

```text
All services burst together
Node CPU pressure
Node memory pressure
Latency spikes
Evictions happen
```

Diagram:

```text
Before traffic:
Node looks fine
+--------------------------------+
| Pod Pod Pod Pod Pod Pod Pod    |
| low usage                      |
+--------------------------------+

After traffic:
+--------------------------------+
| CPU pressure / memory pressure |
| noisy neighbor chaos           |
+--------------------------------+
```

Root cause:

```text
Scheduler had no realistic resource contract.
```

Fix:

```text
Set requests based on observed p50/p90 usage plus capacity goals.
Use LimitRange for defaults.
Use ResourceQuota for namespaces.
Monitor node pressure.
```

---

# 27. Debugging Checklist

When a Pod behaves badly, use this order:

```text
1. Is the Pod Pending?
   -> Check requests vs node allocatable.

2. Is the Pod Running but slow?
   -> Check CPU throttling and CPU limit.

3. Is the Pod restarting?
   -> Check OOMKilled, CrashLoopBackOff, app logs.

4. Is the Pod Evicted?
   -> Check node pressure and QoS class.

5. Is HPA scaling strangely?
   -> Check CPU requests and metrics.

6. Is the cluster expensive and underused?
   -> Requests may be too high.

7. Is the cluster unstable and packed?
   -> Requests may be too low or missing.
```

Commands:

```bash
kubectl get pods
kubectl describe pod <pod>
kubectl top pod
kubectl top node
kubectl describe node <node>
kubectl get events --sort-by=.lastTimestamp
kubectl get limitrange
kubectl get resourcequota
```

Useful output fields:

```text
Requests
Limits
QoS Class
Reason: OOMKilled
Reason: Evicted
Events: FailedScheduling
```

---

# 28. Full YAML Example: Production-Minded Spring Boot Deployment

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
      containers:
        - name: payment-service
          image: registry.example.com/payment-service:1.0.0
          ports:
            - containerPort: 8080
          env:
            - name: JAVA_TOOL_OPTIONS
              value: "-XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"
          resources:
            requests:
              cpu: "750m"
              memory: "768Mi"
            limits:
              cpu: "2"
              memory: "1536Mi"
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 20
            periodSeconds: 10
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 20
```

Interpretation:

```text
This service usually needs around 0.75 CPU and 768Mi memory.
It may burst to 2 CPU and 1.5Gi memory.
JVM heap should not consume the entire container memory.
Readiness protects traffic routing.
Liveness restarts truly stuck process.
```

Mental picture:

```text
Scheduler uses:
  cpu request 750m
  memory request 768Mi

Runtime enforces:
  cpu limit 2
  memory limit 1536Mi
```

---

# 29. Requests/Limits Sizing Strategy

Start with observation, not guessing.

For each service, collect:

```text
CPU usage p50, p90, p95, p99
Memory working set
Startup memory peak
Traffic pattern
GC behavior
Latency target
Replica count
HPA behavior
```

Practical starting model:

```text
CPU request  = normal steady usage plus buffer
CPU limit    = burst allowance, or omit carefully if platform allows
Memory request = steady working set
Memory limit   = peak memory plus JVM/native headroom
```

For Java APIs:

```text
Avoid too tiny CPU limits.
Keep memory headroom above heap.
Watch GC and native memory.
Test under load.
```

Capacity thinking:

```text
If one replica handles 100 RPS at 700m CPU p95,
request should not be 100m just to pack nodes.
That creates instability.
```

Do not copy values blindly from another service.

A CRUD API, Kafka consumer, search service, and batch worker have different resource shapes.

---

# 30. Interview Answers

## What is a resource request?

A request is the amount of CPU or memory Kubernetes uses for scheduling and resource reservation. The scheduler places a Pod only on a node where the sum of existing requests plus the new request fits within allocatable capacity.

## What is a resource limit?

A limit is the maximum amount of resource a container is allowed to use at runtime. CPU limit causes throttling when exceeded. Memory limit can cause the container to be killed if memory usage crosses the limit.

## Difference between CPU and memory behavior?

CPU is compressible. If a container exceeds CPU limit, it is throttled. Memory is not safely compressible. If a container exceeds memory limit, it may be OOMKilled.

## What are Kubernetes QoS classes?

QoS classes are Guaranteed, Burstable, and BestEffort. They are derived from resource request/limit settings and influence eviction priority under node pressure.

## When is a Pod Guaranteed QoS?

A Pod is Guaranteed when every container has CPU and memory requests equal to limits.

## When is a Pod BestEffort?

A Pod is BestEffort when no CPU or memory requests and limits are specified.

## Why can a Pod stay Pending?

A Pod may stay Pending if no node has enough remaining allocatable resources to satisfy its requests, or because of other scheduling constraints such as taints, affinity, or volume constraints.

## Why can low CPU limit hurt Spring Boot latency?

Low CPU limit can throttle the JVM. This slows request processing, garbage collection, connection pool handling, Kafka consumers, and health checks, which increases p95/p99 latency.

## Why should Java heap not equal container memory limit?

Container memory includes more than heap: metaspace, thread stacks, direct buffers, native memory, code cache, GC structures, and libraries. If heap consumes the full limit, the container may be OOMKilled.

---

# 31. Cheat Sheet

```text
request.cpu       = CPU used for scheduling and fair share
limit.cpu         = max CPU before throttling
request.memory    = memory used for scheduling and eviction ranking
limit.memory      = max memory before OOM kill
QoS               = eviction priority class
Guaranteed        = requests == limits for CPU and memory on all containers
Burstable         = some resources set, not Guaranteed
BestEffort        = no requests/limits
OOMKilled         = container killed due to memory pressure/limit
Evicted           = Pod removed by kubelet due to node pressure
LimitRange        = namespace defaults/min/max resource policy
ResourceQuota     = namespace total resource budget
Allocatable       = node capacity available for Pods after reservations
```

Command memory:

```bash
kubectl describe pod <pod>
kubectl top pod
kubectl top node
kubectl describe node <node>
kubectl get events --sort-by=.lastTimestamp
kubectl get limitrange
kubectl get resourcequota
```

Debug chain:

```text
Pending?       -> requests too high / constraints
Slow?          -> CPU throttling / saturation
Restarting?    -> OOMKilled / app crash
Evicted?       -> node pressure / QoS
Bad HPA?       -> wrong requests / missing metrics
Expensive?     -> requests too high
Unstable?      -> requests too low/missing
```

---

# 32. One Picture To Remember

```text
                         POD RESOURCE SPEC
                                |
                                v
              +-----------------------------------+
              | requests                          |
              | "What should scheduler reserve?" |
              +----------------+------------------+
                               |
                               v
                       +---------------+
                       | Scheduler     |
                       | choose node   |
                       +---------------+

              +-----------------------------------+
              | limits                            |
              | "What is runtime maximum?"       |
              +----------------+------------------+
                               |
                               v
                       +---------------+
                       | cgroups       |
                       | enforce       |
                       +---------------+

QoS class is derived from requests/limits:

Guaranteed > Burstable > BestEffort

Under pressure:

BestEffort dies first.
Burstable may die next.
Guaranteed is most protected.
```

Final memory hook:

```text
Requests help Kubernetes place your Pod.
Limits control how far your Pod may go.
QoS decides how strongly your Pod is protected when the node suffers.
```

---

# 33. Final Production Checklist

```text
[ ] Every production container has CPU and memory requests.
[ ] Memory limit includes JVM heap plus native/headroom.
[ ] Java heap is not equal to container limit.
[ ] CPU limits are tested under load, not guessed.
[ ] QoS class is understood for critical services.
[ ] Pending Pods are debugged using requests vs allocatable.
[ ] OOMKilled is checked using describe pod and previous logs.
[ ] Evicted Pods are investigated at node pressure level.
[ ] HPA targets are reviewed together with CPU requests.
[ ] LimitRange exists for safe namespace defaults.
[ ] ResourceQuota protects namespace-level cluster fairness.
[ ] Resource sizing is based on metrics and load tests.
```

---

# 34. Final Sentence

Kubernetes resource management is not about memorizing YAML fields.

It is about teaching the cluster three things:

```text
How much my app needs.
How far my app may grow.
How important my app is when the node is under pressure.
```

When you understand that, requests, limits, and QoS stop being syntax and become production survival tools.
