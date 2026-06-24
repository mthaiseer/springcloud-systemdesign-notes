# 052_HPA_Autoscaling.md
# MiniURLShortener — Kubernetes HPA Autoscaling

> Core mental model: **Horizontal Pod Autoscaler is a feedback controller: it watches live metrics, compares them with a target, and changes Deployment replica count so the application has enough Pods for current load.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What HPA Solves](#4-what-hpa-solves)
- [5. What HPA Does Not Solve](#5-what-hpa-does-not-solve)
- [6. Scaling Up vs Scaling Out](#6-scaling-up-vs-scaling-out)
- [7. HPA Control Loop](#7-hpa-control-loop)
- [8. Metrics Server Mental Model](#8-metrics-server-mental-model)
- [9. CPU-Based Autoscaling](#9-cpu-based-autoscaling)
- [10. Memory-Based Autoscaling](#10-memory-based-autoscaling)
- [11. Why Resource Requests Matter](#11-why-resource-requests-matter)
- [12. HPA Formula Mental Model](#12-hpa-formula-mental-model)
- [13. MiniURLShortener Autoscaling Architecture](#13-miniurlshortener-autoscaling-architecture)
- [14. Deployment YAML With Resources](#14-deployment-yaml-with-resources)
- [15. Basic HPA YAML](#15-basic-hpa-yaml)
- [16. CPU And Memory HPA YAML](#16-cpu-and-memory-hpa-yaml)
- [17. Autoscaling Behavior YAML](#17-autoscaling-behavior-yaml)
- [18. Applying And Testing HPA](#18-applying-and-testing-hpa)
- [19. Load Testing With k6/curl](#19-load-testing-with-k6curl)
- [20. HPA Status Explained](#20-hpa-status-explained)
- [21. Scaling Dry Runs](#21-scaling-dry-runs)
- [22. Scale Down Stabilization](#22-scale-down-stabilization)
- [23. HPA vs Cluster Autoscaler](#23-hpa-vs-cluster-autoscaler)
- [24. HPA vs VPA](#24-hpa-vs-vpa)
- [25. App-Level Bottlenecks](#25-app-level-bottlenecks)
- [26. Database Connection Pool Warning](#26-database-connection-pool-warning)
- [27. Autoscaling Redirect API](#27-autoscaling-redirect-api)
- [28. Autoscaling Create API](#28-autoscaling-create-api)
- [29. Internal Execution Walkthrough](#29-internal-execution-walkthrough)
- [30. Common kubectl Commands](#30-common-kubectl-commands)
- [31. Debugging Mindset](#31-debugging-mindset)
- [32. Production Failure Stories](#32-production-failure-stories)
- [33. Common Mistakes](#33-common-mistakes)
- [34. Interview-Ready Explanation](#34-interview-ready-explanation)
- [35. Senior Engineer Checklist](#35-senior-engineer-checklist)
- [36. One-Page Cheat Sheet](#36-one-page-cheat-sheet)
- [37. One Picture To Remember](#37-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener traffic is not constant.

Morning:

```text
100 requests/sec
```

During marketing campaign:

```text
10,000 requests/sec
```

During night:

```text
50 requests/sec
```

If you always run too few Pods:

```text
high CPU
high latency
timeouts
dropped requests
bad user experience
```

If you always run too many Pods:

```text
wasted money
wasted CPU/memory
too many DB connections
unnecessary infrastructure cost
```

HPA exists to automatically adjust Pod count.

Instead of manually saying:

```bash
kubectl scale deployment mini-url-shortener --replicas=10
```

You say:

```text
Keep CPU around 60%.
Minimum Pods = 3.
Maximum Pods = 20.
```

Kubernetes watches metrics and changes replicas.

ASCII:

```text
Traffic rises
     |
     v
CPU rises
     |
     v
HPA detects above target
     |
     v
Deployment replicas increased
     |
     v
More Pods handle traffic
```

Production memory:

```text
HPA is not magic. It is a feedback loop based on metrics.
```

---

## 2. The One Core Mental Model

HPA is like a thermostat.

Thermostat:

```text
current temperature > target temperature
    turn cooling on

current temperature < target temperature
    reduce cooling
```

HPA:

```text
current CPU > target CPU
    add Pods

current CPU < target CPU
    remove Pods
```

ASCII:

```text
Target CPU: 60%

Current CPU: 90%
      |
      v
Too hot
      |
      v
Add Pods
      |
      v
CPU per Pod reduces
```

One-line memory:

```text
HPA compares current metrics with target metrics and adjusts replica count.
```

Feedback loop:

```text
Measure -> Compare -> Decide -> Scale -> Measure again
```

ASCII:

```text
+-----------+
| Measure   |
+-----+-----+
      |
      v
+-----------+
| Compare   |
+-----+-----+
      |
      v
+-----------+
| Scale     |
+-----+-----+
      |
      v
+-----------+
| Observe   |
+-----------+
```

---

## 3. Problem Statement

Add autoscaling to MiniURLShortener Kubernetes deployment.

We want:

```text
1. Scale Pods up when CPU is high.
2. Scale Pods down when load drops.
3. Keep minimum replicas for availability.
4. Keep maximum replicas to protect dependencies and cost.
5. Use resource requests correctly.
6. Understand metrics-server.
7. Avoid DB connection explosion.
8. Debug why HPA is not scaling.
9. Understand HPA vs Cluster Autoscaler vs VPA.
```

Out of scope:

```text
1. KEDA deep dive.
2. Custom Prometheus metrics deep dive.
3. Event-driven Kafka autoscaling.
4. Predictive autoscaling.
5. Full cloud node autoscaling configuration.
```

This chapter focuses on standard Kubernetes HPA.

---

## 4. What HPA Solves

HPA solves horizontal Pod scaling.

It helps when app replicas need to change based on load.

Examples:

```text
1. CPU-heavy redirect traffic.
2. High create API traffic.
3. Sudden request bursts.
4. Worker Pods processing queues.
5. Seasonal traffic patterns.
```

HPA can scale on:

```text
CPU utilization
memory utilization
custom metrics
external metrics
```

Basic HPA usually starts with CPU.

ASCII:

```text
Load increases
   |
   v
Each Pod busier
   |
   v
CPU above target
   |
   v
HPA increases replicas
```

For MiniURLShortener:

```text
Redirect API:
    high read traffic, good HPA candidate

Create API:
    write-heavy, HPA helps app layer but DB/Kafka must handle load

Analytics worker:
    HPA can scale on CPU or queue lag with custom metrics later
```

---

## 5. What HPA Does Not Solve

HPA does not automatically fix every bottleneck.

If bottleneck is Postgres:

```text
adding more app Pods may make it worse
```

If bottleneck is Redis:

```text
more Pods send more Redis traffic
```

If bottleneck is Kafka:

```text
more producers or consumers may hit partition limits
```

If bottleneck is external API quota:

```text
more Pods may exceed quota faster
```

ASCII:

```text
HPA adds Pods
    |
    v
More app capacity
    |
    v
More pressure on dependencies
    |
    v
DB/Redis/Kafka may break
```

HPA does not solve:

```text
bad SQL queries
missing indexes
too small DB
too many DB connections
memory leaks
slow external APIs
wrong Hikari pool size
bad code path
```

Senior rule:

```text
Scale the bottleneck, not just the app.
```

---

## 6. Scaling Up vs Scaling Out

Scaling up:

```text
Give one Pod more CPU/memory.
```

Scaling out:

```text
Run more Pods.
```

ASCII:

```text
Scale Up:
[ Big Pod ]

Scale Out:
[Pod][Pod][Pod][Pod]
```

HPA does scale out.

It changes:

```text
Deployment replicas
```

VPA does scale up/down resources.

It changes:

```text
CPU/memory requests
```

For stateless Spring Boot apps:

```text
HPA is common.
```

For MiniURLShortener app:

```text
multiple Pods behind Service
```

is natural.

But app must be stateless:

```text
No local session state
No local file-only state
No in-memory-only critical data
Use Redis/DB/Kafka for shared state
```

---

## 7. HPA Control Loop

HPA runs periodically.

Typical loop:

```text
1. Read target object current replicas.
2. Fetch metrics from metrics API.
3. Compute desired replicas.
4. Apply scaling decision.
5. Wait.
6. Repeat.
```

ASCII:

```text
HPA Controller
     |
     v
Metrics Server
     |
     v
Current CPU/memory
     |
     v
Compare with target
     |
     v
Update Deployment replicas
```

Example:

```text
Current replicas = 3
Current avg CPU = 90%
Target CPU = 60%
```

HPA thinks:

```text
90/60 = 1.5
3 * 1.5 = 4.5
desired replicas = 5
```

Then Deployment creates more Pods.

---

## 8. Metrics Server Mental Model

HPA needs metrics.

Metrics Server collects resource metrics from kubelets.

ASCII:

```text
Node Kubelet
  |
  v
Metrics Server
  |
  v
Kubernetes Metrics API
  |
  v
HPA Controller
```

Without metrics-server:

```text
HPA cannot read CPU/memory.
```

You may see:

```text
current CPU: <unknown>
```

Check:

```bash
kubectl top pods -n miniurl
```

If this fails, HPA CPU/memory scaling usually will not work.

Install metrics-server depends on environment.

Minikube:

```bash
minikube addons enable metrics-server
```

Check:

```bash
kubectl get deployment metrics-server -n kube-system
kubectl top nodes
kubectl top pods -n miniurl
```

Memory:

```text
Metrics Server is for resource metrics, not full observability.
```

Prometheus is for deeper metrics.

---

## 9. CPU-Based Autoscaling

CPU-based HPA commonly uses CPU utilization percentage.

Example target:

```text
averageUtilization: 60
```

Meaning:

```text
Average CPU usage should be around 60% of requested CPU.
```

Important:

```text
It is based on CPU request, not CPU limit.
```

If Pod request:

```yaml
cpu: "500m"
```

and actual usage:

```text
300m
```

CPU utilization:

```text
300m / 500m = 60%
```

ASCII:

```text
CPU request per Pod = 500m

Actual usage = 300m
      |
      v
utilization = 60%
```

If CPU request is missing:

```text
HPA cannot calculate CPU utilization percentage correctly.
```

Therefore:

```text
resource requests are mandatory for good HPA.
```

---

## 10. Memory-Based Autoscaling

Memory HPA can scale based on memory utilization.

Example:

```yaml
averageUtilization: 75
```

Meaning:

```text
Average memory usage should be around 75% of requested memory.
```

But memory behaves differently from CPU.

CPU:

```text
goes up and down quickly
```

Memory:

```text
may stay high due to heap allocation/cache
```

For Java apps, memory-based HPA can be tricky.

Example:

```text
JVM heap grows to 700Mi.
Traffic drops.
Heap does not immediately shrink.
HPA thinks memory is still high.
Pods stay scaled up.
```

Memory HPA is useful but must be used carefully.

For MiniURLShortener:

```text
Start with CPU HPA.
Add memory HPA only after observing real behavior.
```

---

## 11. Why Resource Requests Matter

Requests are the baseline for HPA utilization.

Deployment:

```yaml
resources:
  requests:
    cpu: "500m"
    memory: "512Mi"
```

HPA target:

```text
CPU 60%
```

Actual CPU:

```text
300m
```

Calculation:

```text
300m / 500m = 60%
```

If request is too low:

```text
Small CPU usage appears high.
HPA scales too aggressively.
```

If request is too high:

```text
Real pressure may look low.
HPA scales too slowly.
```

ASCII:

```text
Same actual CPU = 300m

Request 300m -> 100%
Request 500m -> 60%
Request 1000m -> 30%
```

So HPA quality depends on resource request quality.

Senior memory:

```text
Bad requests produce bad autoscaling.
```

---

## 12. HPA Formula Mental Model

Simplified formula:

```text
desiredReplicas = currentReplicas * currentMetric / targetMetric
```

Example:

```text
currentReplicas = 4
currentCPU = 90%
targetCPU = 60%
```

Calculation:

```text
4 * 90 / 60 = 6
```

HPA wants:

```text
6 replicas
```

ASCII:

```text
Current:
[Pod][Pod][Pod][Pod]
avg CPU 90%

Target:
60%

Need:
90/60 = 1.5x capacity

Desired:
4 * 1.5 = 6 Pods
```

Scale down example:

```text
currentReplicas = 6
currentCPU = 30%
targetCPU = 60%

6 * 30 / 60 = 3
```

HPA wants:

```text
3 replicas
```

But scale down may be delayed by stabilization to prevent flapping.

---

## 13. MiniURLShortener Autoscaling Architecture

Architecture:

```text
Ingress/Gateway
   |
   v
Service
   |
   v
Deployment Pods
   |
   v
HPA watches CPU/memory metrics
   |
   v
Updates Deployment replicas
```

ASCII:

```text
Traffic
  |
  v
Service
  |
  +-- Pod 1
  +-- Pod 2
  +-- Pod 3
        ^
        |
        |
+------------------+
| HPA Controller   |
| watches metrics  |
| changes replicas |
+------------------+
```

Dependencies:

```text
Postgres
Redis
Kafka
External services
```

must be considered.

If HPA increases Pods:

```text
more Hikari pools
more Redis connections
more Kafka producers
more logs
more CPU/memory demand
```

Production autoscaling requires dependency budgeting.

---

## 14. Deployment YAML With Resources

Deployment must include requests.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mini-url-shortener
  namespace: miniurl
spec:
  replicas: 3
  selector:
    matchLabels:
      app: mini-url-shortener
  template:
    metadata:
      labels:
        app: mini-url-shortener
    spec:
      containers:
        - name: mini-url-shortener
          image: mini-url-shortener:052
          ports:
            - containerPort: 8080
          env:
            - name: JAVA_OPTS
              value: "-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
          resources:
            requests:
              cpu: "500m"
              memory: "512Mi"
            limits:
              cpu: "1000m"
              memory: "1Gi"
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            periodSeconds: 5
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            periodSeconds: 10
```

Important:

```text
HPA CPU utilization uses requests.cpu.
Memory utilization uses requests.memory.
```

For Java:

```text
memory limit and JAVA_OPTS must align.
```

---

## 15. Basic HPA YAML

Create:

```text
k8s/miniurl-hpa.yml
```

HPA:

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: mini-url-shortener-hpa
  namespace: miniurl
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: mini-url-shortener
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
Target Deployment:
    mini-url-shortener

Minimum Pods:
    3

Maximum Pods:
    20

CPU target:
    average CPU should be around 60% of request
```

ASCII:

```text
HPA
 |
 v
Deployment mini-url-shortener
 |
 v
replicas change between 3 and 20
```

Apply:

```bash
kubectl apply -f k8s/miniurl-hpa.yml
```

Check:

```bash
kubectl get hpa -n miniurl
```

---

## 16. CPU And Memory HPA YAML

Example with CPU and memory:

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: mini-url-shortener-hpa
  namespace: miniurl
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: mini-url-shortener
  minReplicas: 3
  maxReplicas: 20
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 60

    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 75
```

How multiple metrics work:

```text
HPA calculates desired replicas for each metric.
Then chooses the highest desired replica count.
```

Example:

```text
CPU says 8 replicas.
Memory says 5 replicas.
HPA chooses 8.
```

Why?

```text
Avoid under-scaling when one resource is pressured.
```

For Java, start with CPU first unless memory behavior is well understood.

---

## 17. Autoscaling Behavior YAML

You can control scale-up and scale-down behavior.

Example:

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: mini-url-shortener-hpa
  namespace: miniurl
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: mini-url-shortener
  minReplicas: 3
  maxReplicas: 20
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 60
  behavior:
    scaleUp:
      stabilizationWindowSeconds: 0
      policies:
        - type: Percent
          value: 100
          periodSeconds: 60
        - type: Pods
          value: 4
          periodSeconds: 60
      selectPolicy: Max

    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
        - type: Percent
          value: 50
          periodSeconds: 60
      selectPolicy: Max
```

Meaning:

```text
Scale up quickly.
Scale down slowly.
```

Why?

```text
Traffic spikes need fast capacity.
Traffic drops may be temporary.
Slow scale down prevents flapping.
```

ASCII:

```text
Traffic spike:
scale up fast

Traffic drop:
wait and scale down carefully
```

Production memory:

```text
Aggressive scale up, conservative scale down is common.
```

---

## 18. Applying And Testing HPA

Apply Deployment:

```bash
kubectl apply -f k8s/miniurl-deployment.yml
```

Apply HPA:

```bash
kubectl apply -f k8s/miniurl-hpa.yml
```

Check HPA:

```bash
kubectl get hpa -n miniurl
```

Watch:

```bash
kubectl get hpa -n miniurl -w
```

Check Pods:

```bash
kubectl get pods -n miniurl -w
```

Check metrics:

```bash
kubectl top pods -n miniurl
```

Describe HPA:

```bash
kubectl describe hpa mini-url-shortener-hpa -n miniurl
```

If current metrics show unknown:

```text
metrics-server problem
missing resource requests
metrics not available yet
```

---

## 19. Load Testing With k6/curl

Simple curl loop:

```bash
while true; do
  curl -s http://localhost:8080/actuator/health > /dev/null
done
```

Better with k6:

```javascript
import http from 'k6/http';

export const options = {
  vus: 100,
  duration: '5m',
};

export default function () {
  http.get('http://localhost:8080/api/v1/urls/abc123');
}
```

Run:

```bash
k6 run load.js
```

Watch HPA:

```bash
kubectl get hpa -n miniurl -w
```

Watch Pods:

```bash
kubectl get pods -n miniurl -w
```

Important:

```text
Health endpoint may be too cheap to create CPU load.
Use realistic endpoints.
```

For CPU testing, you may need:

```text
real API load
or a test endpoint that consumes CPU
```

Do not keep CPU-burn endpoints in production.

---

## 20. HPA Status Explained

Command:

```bash
kubectl get hpa -n miniurl
```

Example:

```text
NAME                     REFERENCE                       TARGETS   MINPODS MAXPODS REPLICAS
mini-url-shortener-hpa   Deployment/mini-url-shortener   75%/60%   3       20      6
```

Meaning:

```text
Current CPU = 75%
Target CPU = 60%
Current replicas = 6
Min = 3
Max = 20
```

Describe:

```bash
kubectl describe hpa mini-url-shortener-hpa -n miniurl
```

Useful fields:

```text
Metrics
Min replicas
Max replicas
Deployment pods
Conditions
Events
```

Conditions may show:

```text
AbleToScale
ScalingActive
ScalingLimited
```

ScalingLimited means:

```text
HPA wanted more/less but hit min or max replicas.
```

---

## 21. Scaling Dry Runs

### Dry Run 1: Scale Up

Current:

```text
replicas = 3
target CPU = 60%
current CPU = 120%
```

Formula:

```text
3 * 120 / 60 = 6
```

HPA desired:

```text
6 replicas
```

ASCII:

```text
Before:
[Pod][Pod][Pod]
CPU 120%

After:
[Pod][Pod][Pod][Pod][Pod][Pod]
CPU should drop
```

---

### Dry Run 2: Scale Down

Current:

```text
replicas = 10
target CPU = 60%
current CPU = 30%
```

Formula:

```text
10 * 30 / 60 = 5
```

HPA desired:

```text
5 replicas
```

But scaleDown stabilization may delay it.

ASCII:

```text
Traffic dropped
  |
  v
HPA wants fewer Pods
  |
  v
wait stabilization window
  |
  v
scale down safely
```

---

### Dry Run 3: Max Replicas Hit

Current:

```text
replicas = 20
maxReplicas = 20
CPU = 150%
```

HPA wants:

```text
more than 20
```

But cannot exceed max.

Status:

```text
ScalingLimited = True
```

Meaning:

```text
HPA is capped.
Application may be under-provisioned.
Need higher max or more efficient app/dependencies.
```

---

### Dry Run 4: Missing CPU Request

Deployment:

```yaml
resources:
  requests:
    memory: "512Mi"
```

No CPU request.

HPA:

```text
target CPU utilization 60%
```

Result:

```text
HPA cannot calculate utilization.
```

Possible status:

```text
unknown
missing request for cpu
```

Fix:

```yaml
resources:
  requests:
    cpu: "500m"
```

---

## 22. Scale Down Stabilization

Scale down stabilization prevents rapid up/down flapping.

Problem without stabilization:

```text
Traffic spikes for 1 minute.
HPA scales up.
Traffic drops for 30 seconds.
HPA scales down.
Traffic spikes again.
HPA scales up again.
```

This causes instability.

ASCII:

```text
Replicas:
3 -> 10 -> 3 -> 10 -> 3
```

With stabilization:

```text
HPA waits before scaling down.
```

YAML:

```yaml
behavior:
  scaleDown:
    stabilizationWindowSeconds: 300
```

Meaning:

```text
Wait 5 minutes before aggressive scale down.
```

Production memory:

```text
Scale up fast. Scale down slow.
```

---

## 23. HPA vs Cluster Autoscaler

HPA:

```text
scales Pods
```

Cluster Autoscaler:

```text
scales Nodes
```

ASCII:

```text
HPA:
Deployment replicas 3 -> 10

Cluster Autoscaler:
Worker nodes 3 -> 5
```

Interaction:

```text
1. HPA wants more Pods.
2. Scheduler tries to place Pods.
3. Not enough node resources.
4. Pods stay Pending.
5. Cluster Autoscaler adds Nodes.
6. Scheduler places Pods.
```

HPA alone cannot create Nodes.

If cluster has no capacity:

```text
new Pods remain Pending
```

Debug:

```bash
kubectl get pods -n miniurl
kubectl describe pod <pending-pod> -n miniurl
```

Look for:

```text
Insufficient cpu
Insufficient memory
```

---

## 24. HPA vs VPA

HPA:

```text
Horizontal Pod Autoscaler
changes number of Pods
```

VPA:

```text
Vertical Pod Autoscaler
changes CPU/memory requests
```

ASCII:

```text
HPA:
[Pod][Pod][Pod] -> [Pod][Pod][Pod][Pod][Pod]

VPA:
[Small Pod] -> [Bigger Pod]
```

For stateless web apps:

```text
HPA is usually first choice.
```

VPA can help find right resource requests.

But be careful combining HPA and VPA on CPU:

```text
HPA uses CPU request as denominator.
VPA changes CPU request.
This affects HPA behavior.
```

Common production pattern:

```text
Use VPA in recommendation mode.
Use HPA for live scaling.
```

---

## 25. App-Level Bottlenecks

Before autoscaling, know bottlenecks.

MiniURLShortener possible bottlenecks:

```text
1. CPU from JSON, validation, crypto, Base62
2. DB writes for create API
3. DB reads on cache miss
4. Redis latency
5. Kafka producer blocking
6. Hikari pool saturation
7. Thread pool exhaustion
8. External abuse check latency
9. Gateway rate limiting
10. Network bandwidth
```

HPA based on CPU helps only if CPU is meaningful signal.

If app is waiting on DB:

```text
CPU may be low
latency high
HPA may not scale
```

ASCII:

```text
High latency
  |
  +-- high CPU --> HPA may help
  |
  +-- low CPU --> likely waiting on dependency
```

For dependency bottleneck, use:

```text
indexes
caching
connection pooling
read replicas
async processing
bulkheads
rate limiting
backpressure
```

---

## 26. Database Connection Pool Warning

Every Pod has its own Hikari pool.

Example:

```text
replicas = 3
Hikari maxPoolSize = 20

Total possible DB connections:
3 * 20 = 60
```

After HPA:

```text
replicas = 20
Hikari maxPoolSize = 20

Total possible DB connections:
20 * 20 = 400
```

ASCII:

```text
Pod1 -> 20 DB connections
Pod2 -> 20 DB connections
...
Pod20 -> 20 DB connections

Total = 400
```

If Postgres max connections:

```text
200
```

System breaks.

Solutions:

```text
1. Reduce Hikari maxPoolSize per Pod.
2. Use PgBouncer.
3. Increase DB capacity carefully.
4. Use read replicas for reads.
5. Cache hot reads.
6. Set HPA maxReplicas based on DB budget.
```

Senior memory:

```text
HPA maxReplicas is also a dependency protection limit.
```

---

## 27. Autoscaling Redirect API

Redirect API is read-heavy.

Flow:

```text
GET /abc123
  |
  v
Redis lookup
  |
  +-- hit -> redirect
  |
  +-- miss -> Postgres lookup -> cache -> redirect
```

HPA helps when:

```text
CPU/request handling becomes bottleneck
network/socket handling needs more Pods
Tomcat threads are busy
```

But redirect performance usually depends on:

```text
Redis hit ratio
Redis latency
DB fallback rate
connection pools
```

Good scaling design:

```text
1. High Redis cache hit ratio.
2. Low DB fallback.
3. HPA for app Pods.
4. Redis capacity monitored.
5. Ingress/Gateway rate limiting for abuse.
```

ASCII:

```text
Traffic spike
  |
  v
More redirect requests
  |
  v
HPA adds app Pods
  |
  v
Redis must handle more read QPS
```

Do not forget Redis capacity.

---

## 28. Autoscaling Create API

Create API is write-heavy.

Flow:

```text
POST /api/v1/urls
  |
  v
validation
  |
  v
abuse check
  |
  v
Postgres insert
  |
  v
Kafka event
```

HPA helps app CPU and request concurrency.

But bottlenecks may be:

```text
Postgres write throughput
unique index contention
ID generation
abuse service latency
Kafka availability
rate limits
```

Create API should also have:

```text
rate limiting
idempotency
timeouts
circuit breakers
bulkheads
backpressure
```

Autoscaling create API blindly can overload DB.

Production rule:

```text
Use HPA, but protect writes with rate limits and DB capacity planning.
```

ASCII:

```text
More Pods
  |
  v
More create writes
  |
  v
More DB pressure
  |
  v
Need DB protection
```

---

## 29. Internal Execution Walkthrough

When HPA exists:

```text
1. Metrics Server collects CPU/memory from kubelets.
2. HPA controller periodically reads metrics.
3. HPA reads target Deployment current replicas.
4. HPA calculates desired replicas.
5. HPA respects minReplicas and maxReplicas.
6. HPA applies behavior policies.
7. HPA updates Deployment scale subresource.
8. Deployment controller updates ReplicaSet desired count.
9. ReplicaSet creates/deletes Pods.
10. Scheduler places new Pods on Nodes.
11. Service endpoints update when Pods become ready.
12. Traffic spreads across more/fewer Pods.
```

ASCII:

```text
Kubelet metrics
    |
    v
Metrics Server
    |
    v
HPA Controller
    |
    v
Deployment replicas
    |
    v
ReplicaSet
    |
    v
Pods
    |
    v
Service endpoints
```

Important:

```text
HPA does not send traffic.
Service/Ingress do that.
HPA changes replica count.
```

---

## 30. Common kubectl Commands

Get HPA:

```bash
kubectl get hpa -n miniurl
```

Watch HPA:

```bash
kubectl get hpa -n miniurl -w
```

Describe HPA:

```bash
kubectl describe hpa mini-url-shortener-hpa -n miniurl
```

Top Pods:

```bash
kubectl top pods -n miniurl
```

Top Nodes:

```bash
kubectl top nodes
```

Get Deployment:

```bash
kubectl get deploy mini-url-shortener -n miniurl
```

Watch Pods:

```bash
kubectl get pods -n miniurl -w
```

Manual scale:

```bash
kubectl scale deployment/mini-url-shortener --replicas=5 -n miniurl
```

Check events:

```bash
kubectl get events -n miniurl --sort-by=.lastTimestamp
```

Check metrics-server:

```bash
kubectl get pods -n kube-system | grep metrics
```

---

## 31. Debugging Mindset

When HPA does not work, ask:

```text
Is metrics-server installed?
Does kubectl top pods work?
Does Deployment have CPU requests?
Does HPA target correct Deployment?
Are Pods ready?
Is current metric above target?
Is HPA already at maxReplicas?
Is HPA already at minReplicas?
Are new Pods Pending due to no node capacity?
Are resource requests too high/low?
Is CPU the right scaling metric?
Is bottleneck DB/Redis instead of CPU?
```

Debug map:

```text
TARGETS <unknown>:
    metrics-server missing
    resource requests missing
    metrics not ready

No scale up:
    CPU below target
    maxReplicas reached
    wrong target deployment
    metrics unavailable

Pods Pending after scale:
    cluster lacks resources
    cluster autoscaler needed

Scale flapping:
    stabilization too short
    target too sensitive
    traffic bursty

High latency but no scaling:
    CPU low, dependency bottleneck likely
```

Golden question:

```text
Is HPA seeing the right metric, and is that metric the real bottleneck?
```

---

## 32. Production Failure Stories

### Failure Story 1: HPA Shows Unknown Metrics

HPA:

```text
TARGETS <unknown>/60%
```

Root cause:

```text
metrics-server not installed
or CPU requests missing
```

Fix:

```text
Install metrics-server.
Set resources.requests.cpu.
```

Lesson:

```text
HPA needs metrics and requests.
```

---

### Failure Story 2: HPA Scales App And Kills Database

Before:

```text
5 Pods * 20 DB connections = 100
```

After traffic spike:

```text
30 Pods * 20 DB connections = 600
```

Postgres max:

```text
250
```

Result:

```text
connection exhaustion
timeouts
500 errors
```

Fix:

```text
Set HPA max based on DB budget.
Tune Hikari.
Use PgBouncer.
Scale DB/read replicas.
```

Lesson:

```text
Autoscaling app multiplies dependency load.
```

---

### Failure Story 3: CPU Target Too Low

Target:

```text
20%
```

Result:

```text
HPA scales too aggressively.
Many Pods mostly idle.
Cost increases.
DB connections increase.
```

Fix:

```text
Use realistic CPU target like 50-70% based on latency tests.
```

Lesson:

```text
Too low target wastes resources.
```

---

### Failure Story 4: Memory HPA Keeps Scaling Up Java App

JVM heap grows and stays allocated.

Memory target crossed.

HPA adds Pods.

Memory per Pod does not drop quickly.

Result:

```text
unnecessary scale-up
cost increase
```

Fix:

```text
Understand JVM memory behavior.
Prefer CPU or request/latency/custom metrics first.
```

Lesson:

```text
Memory is not always a good autoscaling signal for Java.
```

---

### Failure Story 5: HPA Wants More Pods But Cluster Full

HPA desired:

```text
20 Pods
```

Running:

```text
10 Pods
```

Pending:

```text
10 Pods
```

Events:

```text
Insufficient cpu
```

Fix:

```text
Enable Cluster Autoscaler or add nodes.
Reduce requests if too high.
```

Lesson:

```text
HPA scales Pods, not Nodes.
```

---

## 33. Common Mistakes

### Mistake 1: No CPU requests

Wrong:

```yaml
resources:
  limits:
    cpu: "1"
```

No request.

Correct:

```yaml
resources:
  requests:
    cpu: "500m"
```

### Mistake 2: Thinking HPA fixes DB bottlenecks

Wrong:

```text
DB slow, add Pods.
```

Correct:

```text
Fix DB bottleneck, caching, indexes, pool, replicas.
```

### Mistake 3: Max replicas too high

Wrong:

```text
maxReplicas: 100
```

without dependency budget.

Correct:

```text
Set max based on DB/Redis/Kafka capacity.
```

### Mistake 4: Memory scaling blindly

Wrong:

```text
Memory HPA for Java without observation.
```

Correct:

```text
Understand heap behavior first.
```

### Mistake 5: No stabilization

Wrong:

```text
Scale up/down too rapidly.
```

Correct:

```text
Use scaleDown stabilization.
```

### Mistake 6: Wrong metric

Wrong:

```text
CPU low, latency high, expect HPA to help.
```

Correct:

```text
Investigate dependency wait time.
```

### Mistake 7: HPA without load testing

Wrong:

```text
Enable HPA and assume production safe.
```

Correct:

```text
Load test and observe DB/Redis/Kafka impact.
```

---

## 34. Interview-Ready Explanation

If interviewer asks:

```text
How does Kubernetes HPA work?
```

Strong answer:

```text
Horizontal Pod Autoscaler is a Kubernetes controller that watches metrics such as CPU
or memory utilization and updates the replica count of a target workload like a
Deployment. It gets resource metrics through the metrics API, usually backed by
metrics-server. For CPU utilization, the percentage is calculated against the Pod CPU
request, so resource requests must be set correctly. HPA periodically compares current
average utilization with the target and computes desired replicas using a ratio like
currentReplicas multiplied by currentMetric divided by targetMetric, then respects
minReplicas, maxReplicas, and scaling behavior policies. In a Spring Boot URL
shortener, HPA can scale stateless app Pods behind a Service, but it does not solve DB,
Redis, Kafka, or external API bottlenecks. Scaling Pods also multiplies Hikari
connection pools, so maxReplicas and pool sizes must be planned with dependency
capacity.
```

Why this is strong:

```text
1. Explains controller behavior.
2. Mentions metrics-server.
3. Mentions CPU requests.
4. Explains formula.
5. Mentions min/max.
6. Applies to Spring Boot.
7. Warns about dependencies.
8. Mentions Hikari pool multiplication.
```

Senior one-liner:

```text
HPA scales Pods from metrics, but production autoscaling must be limited by downstream capacity.
```

---

## 35. Senior Engineer Checklist

Before production HPA:

```text
[ ] metrics-server installed
[ ] kubectl top pods works
[ ] Deployment has CPU requests
[ ] Deployment has memory requests
[ ] HPA minReplicas ensures availability
[ ] HPA maxReplicas respects DB/Redis/Kafka limits
[ ] CPU target load-tested
[ ] scaleDown stabilization configured
[ ] Hikari pool size adjusted for max replicas
[ ] readiness probes configured
[ ] liveness probes configured safely
[ ] app is stateless
[ ] DB capacity tested
[ ] Redis capacity tested
[ ] Kafka capacity tested
[ ] dashboards show replicas/CPU/memory/latency/errors
[ ] alerts exist for maxReplicas reached
[ ] Pending Pods monitored
```

If these are checked, your HPA setup is production-shaped.

---

## 36. One-Page Cheat Sheet

```text
Core mental model:
HPA = feedback loop for Pod count.

Loop:
measure metrics
compare with target
calculate desired replicas
update Deployment scale
repeat

Needs:
metrics-server
resource requests
target Deployment
minReplicas
maxReplicas

CPU utilization:
actual CPU / requested CPU

Formula:
desiredReplicas = currentReplicas * currentMetric / targetMetric

Good defaults:
minReplicas: 3
maxReplicas: based on dependency budget
CPU target: often 50-70%
scale up fast
scale down slow

HPA scales:
Pods

HPA does not scale:
Nodes
DB
Redis
Kafka
external APIs

HPA vs Cluster Autoscaler:
HPA adds Pods.
Cluster Autoscaler adds Nodes.

HPA vs VPA:
HPA changes replica count.
VPA changes resource requests.

Debug:
kubectl get hpa -n miniurl
kubectl describe hpa name -n miniurl
kubectl top pods -n miniurl
kubectl get pods -n miniurl -w

Golden rule:
Autoscaling app without dependency planning can break the system faster.
```

---

## 37. One Picture To Remember

```text
                    HPA AUTOSCALING MENTAL MODEL

                         "Thermostat for Pods"

Traffic increases
       |
       v
Pods use more CPU
       |
       v
+----------------------+
| Metrics Server       |
| collects CPU/memory  |
+----------+-----------+
           |
           v
+----------------------+
| HPA Controller       |
| target CPU = 60%     |
+----------+-----------+
           |
           v
Current CPU > target?
           |
       yes |
           v
+----------------------+
| Update Deployment    |
| replicas 3 -> 6      |
+----------+-----------+
           |
           v
+----------------------+
| ReplicaSet creates   |
| more Pods            |
+----------+-----------+
           |
           v
Service routes traffic across more ready Pods


BUT REMEMBER:

More Pods
   |
   v
More DB connections
More Redis traffic
More Kafka traffic
More external API calls


FINAL MEMORY:

HPA scales Pods.
Requests define utilization.
Metrics drive decisions.
Max replicas protect dependencies.
Scale up fast, scale down carefully.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. HPA is a feedback controller that changes Deployment replicas based on metrics.
2. CPU HPA depends on CPU requests, so missing or wrong requests break autoscaling quality.
3. HPA scales Pods, not Nodes or databases.
4. Scaling Spring Boot Pods multiplies DB pools, Redis connections, Kafka clients, and external calls.
5. Production HPA needs load testing, stabilization, observability, and dependency capacity planning.
```

Next possible chapters:

```text
053_Kubernetes_ConfigMap_Secret.md
054_Kubernetes_Debugging_Playbook.md
055_Kubernetes_Resource_Limits_QoS.md
056_CI_CD_Docker_Kubernetes.md
