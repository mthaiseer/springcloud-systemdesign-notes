# 028_Production_Failure_Playbook.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why A Production Failure Playbook Exists

A production failure playbook exists because incidents are not solved by memory.

They are solved by calm diagnosis.

Most engineers fail during incidents because they jump randomly:

```text
Maybe restart pod?
Maybe rollback?
Maybe scale up?
Maybe database is slow?
Maybe Kubernetes is broken?
```

That is panic debugging.

A production playbook gives you a fixed path:

```text
Impact
  |
  v
Scope
  |
  v
Recent Change
  |
  v
Traffic Path
  |
  v
App Health
  |
  v
Infrastructure Health
  |
  v
Mitigation
  |
  v
Root Cause
  |
  v
Prevention
```

The goal is not to memorize every Kubernetes command.

The goal is to understand where failure can hide.

In Kubernetes production, requests pass through many gates:

```text
User
  |
  v
DNS
  |
  v
Load Balancer / Ingress
  |
  v
Service
  |
  v
EndpointSlice
  |
  v
Pod
  |
  v
Container
  |
  v
Spring Boot
  |
  v
Database / Kafka / Redis
```

A failure can happen at any gate.

Your job during an incident is to find the broken gate quickly.

One sentence:

```text
Production debugging = follow the request path until reality disagrees with expectation.
```

---

# 2. The Wrong Way To Debug Production

Bad debugging looks like this:

```text
Alert fires
   |
   v
Engineer panics
   |
   v
Deletes pods
   |
   v
Restarts deployment
   |
   v
Scales replicas
   |
   v
Changes config
   |
   v
Nobody knows what fixed it
```

This is dangerous because every action changes evidence.

If you restart too early, logs disappear.

If you rollback too late, customers suffer.

If you scale without understanding the bottleneck, you may overload the database.

Bad incident behavior:

```text
Guess first, observe later
```

Good incident behavior:

```text
Observe first, mitigate safely, investigate deeply
```

ASCII model:

```text
Wrong Model

Alert
  |
  v
Random command
  |
  v
Temporary silence
  |
  v
Same issue returns later

Correct Model

Alert
  |
  v
Impact assessment
  |
  v
Evidence collection
  |
  v
Safe mitigation
  |
  v
Root cause
  |
  v
Permanent fix
```

Do not memorize commands.

Memorize the direction of thinking.

---

# 3. Production Failure Mental Model

Every production failure is a mismatch between expected behavior and actual behavior.

Expected:

```text
Order API responds in 200 ms with 99.9% success.
```

Actual:

```text
Order API p99 = 4.8 seconds
HTTP 5xx = 12%
```

Your job is to reduce uncertainty.

```text
What changed?
What is impacted?
Where is the request slowing/failing?
Is failure inside app, platform, network, dependency, or data?
```

Mental model:

```text
                  Production System
                         |
        +----------------+----------------+
        |                |                |
        v                v                v
     Traffic          Runtime          Dependencies
        |                |                |
   DNS / LB / Svc    Pod / JVM       DB / Redis / Kafka
```

You should classify failure into one of five buckets:

```text
1. Release failure
2. Capacity failure
3. Dependency failure
4. Configuration failure
5. Data failure
```

This simple classification prevents panic.

Example:

```text
Only new version failing?        -> release failure
All versions slow at peak?       -> capacity failure
DB timeout everywhere?           -> dependency failure
Only prod profile broken?        -> configuration failure
Only one customer/order broken?  -> data failure
```

---

# 4. The First Five Minutes

The first five minutes are not for root cause analysis.

They are for impact control.

Ask:

```text
1. Is customer traffic affected?
2. Is it all users or partial users?
3. Is error rate increasing?
4. Is latency increasing?
5. Was there a recent deploy/config change?
6. Is rollback safer than investigation?
```

ASCII:

```text
Alert
  |
  v
Is production impact real?
  |
  +-- No --> monitor, verify alert rule
  |
  +-- Yes
        |
        v
   Is there a recent change?
        |
        +-- Yes --> rollback / disable flag if safe
        |
        +-- No
              |
              v
        inspect traffic + dependencies
```

The most useful first commands:

```bash
kubectl get deploy,pods,svc,endpoints -n prod
kubectl get events -n prod --sort-by=.lastTimestamp
kubectl rollout status deploy/order-service -n prod
kubectl describe deploy/order-service -n prod
kubectl top pods -n prod
kubectl top nodes
```

The first dashboard checks:

```text
HTTP 5xx
HTTP 4xx
p50/p95/p99 latency
Request rate
CPU
Memory
Restarts
DB latency
Kafka lag
Redis latency
```

Do not try to be clever first.

Try to be safe first.

---

# 5. Golden Signals Mental Model

The four golden signals are the fastest way to understand production pain.

```text
Latency      -> how slow?
Traffic      -> how much load?
Errors       -> how many failed?
Saturation   -> how full is the system?
```

Diagram:

```text
              User Experience
                    |
   +----------------+----------------+
   |                |                |
   v                v                v
Latency           Errors          Availability
   |
   v
Saturation explains why latency increases
```

Example:

```text
Traffic normal
Errors high
Latency normal
```

Likely:

```text
bad release
bad config
specific dependency returning failure
```

Another example:

```text
Traffic high
Latency high
Errors increasing
CPU high
```

Likely:

```text
capacity exhaustion
thread pool exhaustion
DB connection pool exhaustion
```

Spring Boot Micrometer metrics:

```text
http.server.requests
jvm.memory.used
jvm.threads.live
hikaricp.connections.active
hikaricp.connections.pending
resilience4j.circuitbreaker.calls
kafka.consumer.records.lag
```

The golden signals are not just monitoring theory.

They tell you where to look next.

---

# 6. Request Path Debugging

Never debug Kubernetes as isolated objects.

Debug the request path.

```text
Browser / Client
      |
      v
DNS
      |
      v
External Load Balancer
      |
      v
Ingress Controller
      |
      v
Kubernetes Service
      |
      v
EndpointSlice
      |
      v
Pod IP
      |
      v
Container Port
      |
      v
Spring Boot Controller
      |
      v
Service Layer
      |
      v
DB / Redis / Kafka
```

If a user says:

```text
The API is down.
```

Do not immediately inspect Java code.

Follow the gates:

```bash
nslookup api.example.com
curl -v https://api.example.com/orders/123
kubectl get ingress -n prod
kubectl describe ingress order-ingress -n prod
kubectl get svc order-service -n prod
kubectl get endpoints order-service -n prod
kubectl get pods -l app=order-service -n prod -o wide
kubectl logs deploy/order-service -n prod --tail=200
```

ASCII failure map:

```text
No DNS answer        -> DNS problem
LB 502               -> ingress/upstream problem
Service no endpoints -> label/readiness problem
Pod not ready        -> app/probe/config problem
App timeout          -> dependency/capacity problem
```

This is the core debugging model.

---

# 7. Incident Triage Decision Tree

Use this tree before touching anything.

```text
                 Alert fired
                      |
                      v
             Is customer impact real?
              /                    \
             no                    yes
             |                      |
             v                      v
      fix alert/noise       Was there a recent change?
                              /              \
                            yes              no
                             |                |
                             v                v
                    rollback/flag off   Is traffic abnormal?
                                          /           \
                                        yes           no
                                         |             |
                                         v             v
                                  capacity path   dependency path
```

Questions:

```text
Recent deployment?
Recent config change?
Recent secret rotation?
Recent database migration?
Recent traffic spike?
Recent node autoscaling?
Recent DNS/certificate change?
```

Most incidents are boring.

They are usually caused by:

```text
new release
wrong config
resource limit
dependency timeout
database migration
expired certificate
missing secret
wrong label or selector
```

A playbook turns boring incidents into fast resolution.

---

# 8. Failure Type 1: Bad Deployment

A bad deployment means the new version is unhealthy.

Symptoms:

```text
Error rate rises immediately after rollout
Only new pods failing
Old version was healthy
Rollback improves system
```

ASCII:

```text
Before deploy

v1 Pod  v1 Pod  v1 Pod
  OK      OK      OK

After deploy

v2 Pod  v2 Pod  v1 Pod
 FAIL    FAIL     OK
```

Commands:

```bash
kubectl rollout history deploy/order-service -n prod
kubectl rollout status deploy/order-service -n prod
kubectl get rs -n prod
kubectl get pods -n prod -l app=order-service --show-labels
kubectl logs -n prod deploy/order-service --tail=200
```

Rollback:

```bash
kubectl rollout undo deploy/order-service -n prod
```

Spring Boot causes:

```text
New code path throws exception
New DTO breaks serialization
New config key required but missing
New DB column expected but migration not applied
New feature flag default enabled
```

Golden rule:

```text
If failure starts exactly after deploy, rollback first if safe.
Root cause later.
```

---

# 9. Failure Type 2: CrashLoopBackOff

CrashLoopBackOff means:

```text
Container starts
Container crashes
Kubernetes restarts it
Delay increases
```

Diagram:

```text
Start container
      |
      v
Spring Boot starts
      |
      v
Fatal error
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

Commands:

```bash
kubectl get pods -n prod
kubectl describe pod order-service-abc -n prod
kubectl logs order-service-abc -n prod
kubectl logs order-service-abc -n prod --previous
```

Common Spring Boot causes:

```text
Missing environment variable
Wrong datasource URL
Wrong DB password
Flyway/Liquibase migration failure
Port already used
OutOfMemoryError
Invalid YAML config
Bean creation exception
```

Example failure:

```text
org.springframework.beans.factory.BeanCreationException
Caused by: java.lang.IllegalArgumentException: Could not resolve placeholder 'PAYMENT_URL'
```

Fix thinking:

```text
Pod is not the root cause.
The app process is exiting.
Read previous logs.
```

---

# 10. Failure Type 3: ImagePullBackOff

ImagePullBackOff means kubelet cannot pull the image.

The Pod object exists, but the container image cannot be downloaded.

ASCII:

```text
Pod scheduled to Node
        |
        v
Kubelet asks registry for image
        |
        +-- image found + auth OK --> start container
        |
        +-- image missing/auth fail --> ImagePullBackOff
```

Commands:

```bash
kubectl describe pod order-service-abc -n prod
kubectl get events -n prod --sort-by=.lastTimestamp
```

Look for:

```text
ErrImagePull
ImagePullBackOff
unauthorized
not found
manifest unknown
TLS handshake timeout
```

Causes:

```text
Wrong image tag
CI pushed image to wrong registry
Registry credentials expired
imagePullSecret missing
Private registry unavailable
Node cannot reach registry
```

Fix examples:

```bash
kubectl get secret regcred -n prod
kubectl describe serviceaccount default -n prod
```

Mental model:

```text
Kubernetes can schedule the Pod.
But the node must still fetch bytes from registry.
```

---

# 11. Failure Type 4: Readiness Failure

Readiness failure means the app may be running but is not allowed to receive traffic.

```text
Running != Ready
```

Diagram:

```text
Container running
      |
      v
Readiness probe calls /actuator/health/readiness
      |
      +-- 200 OK  --> Pod endpoint added to Service
      |
      +-- non-200 --> Pod endpoint removed from Service
```

Symptoms:

```text
Pod status: 0/1 Running
Service has no endpoints
Rollout stuck
Traffic goes only to old pods
```

Commands:

```bash
kubectl describe pod order-service-abc -n prod
kubectl get endpoints order-service -n prod
kubectl logs order-service-abc -n prod --tail=200
kubectl exec -it order-service-abc -n prod -- curl localhost:8080/actuator/health/readiness
```

Spring Boot configuration:

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

Bad readiness design:

```text
Readiness depends on optional dependency.
Optional dependency slow makes entire pod unready.
```

Good readiness design:

```text
Readiness checks only what is required to safely serve traffic.
```

---

# 12. Failure Type 5: Liveness Probe Killing Healthy App

Liveness probe answers:

```text
Should Kubernetes restart this container?
```

If liveness is too aggressive, Kubernetes kills a slow but recoverable app.

ASCII:

```text
High GC pause / startup delay
          |
          v
Liveness probe timeout
          |
          v
Kubelet kills container
          |
          v
App restarts
          |
          v
More cold start load
          |
          v
Incident becomes worse
```

Bad probe:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
  initialDelaySeconds: 5
  timeoutSeconds: 1
  failureThreshold: 3
```

Better:

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
  timeoutSeconds: 2
  failureThreshold: 3
```

Mental model:

```text
Readiness protects users.
Liveness protects the container.
Startup probe protects slow startup.
```

---

# 13. Failure Type 6: Service Has No Endpoints

A Service without endpoints is one of the most common Kubernetes production failures.

Symptoms:

```text
DNS resolves
Service exists
Ingress exists
But traffic returns 503/502
```

Check:

```bash
kubectl get svc order-service -n prod
kubectl get endpoints order-service -n prod
kubectl get endpointslice -n prod -l kubernetes.io/service-name=order-service
kubectl get pods -n prod --show-labels
```

ASCII:

```text
Service selector:
app = order-service

Pods:
app = orders
app = orders
app = orders

Result:
No matching endpoints
```

Common causes:

```text
Wrong selector
Wrong pod labels
Pods not Ready
Pods in different namespace
Service targetPort mismatch
Deployment label changed during refactor
```

Debug model:

```text
Service does not send traffic to Pods.
Service sends traffic to matching Ready endpoints.
```

Fix example:

```yaml
service:
  selector:
    app: order-service

pod template:
  labels:
    app: order-service
```

Memory hook:

```text
No endpoints = Service cannot see any Ready Pods.
```

---

# 14. Failure Type 7: Ingress 502 / 503

Ingress 502/503 usually means the edge can be reached, but upstream cannot serve.

Path:

```text
Client
  |
  v
Ingress Controller
  |
  v
Service
  |
  v
Endpoints
  |
  v
Pod
```

Symptoms:

```text
502 Bad Gateway
503 Service Unavailable
504 Gateway Timeout
```

Commands:

```bash
kubectl get ingress -n prod
kubectl describe ingress order-ingress -n prod
kubectl logs -n ingress-nginx deploy/ingress-nginx-controller --tail=200
kubectl get svc order-service -n prod
kubectl get endpoints order-service -n prod
```

Interpretation:

```text
502 -> upstream connection failed or invalid response
503 -> no healthy upstream endpoints
504 -> upstream timeout
```

Common causes:

```text
Service has no endpoints
Wrong service port
App listens on 8080 but service targets 8081
TLS backend mismatch
Ingress path rewrite wrong
Pod readiness failing
App too slow for ingress timeout
```

Mental model:

```text
Ingress is not the app.
Ingress is the route to the app.
```

---

# 15. Failure Type 8: CPU Saturation

CPU saturation means the app needs more CPU time than available.

Symptoms:

```text
p99 latency high
CPU near limit
Throttling high
Requests timeout
Pods not crashing
```

Kubernetes CPU limit can throttle Java heavily.

ASCII:

```text
Incoming requests
      |
      v
Java threads want CPU
      |
      v
Container CPU limit reached
      |
      v
CFS throttling
      |
      v
Latency increases
```

Commands:

```bash
kubectl top pods -n prod
kubectl describe pod order-service-abc -n prod
kubectl top nodes
```

Prometheus metrics:

```text
container_cpu_usage_seconds_total
container_cpu_cfs_throttled_seconds_total
container_cpu_cfs_throttled_periods_total
```

Bad config:

```yaml
resources:
  requests:
    cpu: 100m
  limits:
    cpu: 200m
```

For a busy Spring Boot service, 200m can be too low.

Mitigation:

```text
Scale replicas if app is stateless
Increase CPU limit/request
Reduce expensive code path
Enable caching
Rollback CPU-heavy release
```

Memory hook:

```text
CPU throttling can look like app slowness.
```

---

# 16. Failure Type 9: Memory Pressure / OOMKilled

OOMKilled means the container used more memory than its limit.

ASCII:

```text
JVM heap + metaspace + threads + native memory
                 |
                 v
Container memory limit exceeded
                 |
                 v
Kernel kills process
                 |
                 v
Pod restarts
```

Symptoms:

```text
Pod restarts increase
Last state: OOMKilled
Exit code 137
Latency before crash
```

Commands:

```bash
kubectl describe pod order-service-abc -n prod
kubectl logs order-service-abc -n prod --previous
kubectl top pod order-service-abc -n prod
```

Important Java detail:

```text
Container memory limit is not only heap.
```

Memory includes:

```text
Heap
Metaspace
Thread stacks
Direct buffers
JIT/code cache
Native libraries
Netty buffers
Kafka client buffers
```

Better JVM config:

```bash
JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"
```

Mitigation:

```text
Increase memory limit
Reduce heap pressure
Fix leak
Lower batch size
Limit caches
Check large JSON payloads
```

Memory hook:

```text
OOMKilled is not Kubernetes being bad.
It means the process crossed its memory fence.
```

---

# 17. Failure Type 10: DB Connection Pool Exhaustion

DB pool exhaustion is a classic Spring Boot production incident.

Symptoms:

```text
HTTP latency high
Threads waiting
Hikari pending connections high
DB CPU may be normal or high
App logs show connection timeout
```

ASCII:

```text
Request threads
  | | | | | | | | |
  v v v v v v v v v
Hikari Pool size = 20
  | | | | | | | | |
  v v v v v v v v v
Database connections

If all 20 are busy:
New requests wait -> latency -> timeout
```

Spring Boot log:

```text
HikariPool-1 - Connection is not available, request timed out after 30000ms
```

Metrics:

```text
hikaricp.connections.active
hikaricp.connections.idle
hikaricp.connections.pending
hikaricp.connections.timeout
```

Bad reaction:

```text
Increase pod replicas blindly
```

Why bad?

```text
10 pods x 30 connections = 300 DB connections
Database may collapse.
```

Better thinking:

```text
Is query slow?
Is transaction too long?
Is pool too small?
Is DB max connection too low?
Is there connection leak?
```

Mitigation:

```text
Rollback slow query release
Kill bad traffic path
Add index
Reduce transaction duration
Tune pool carefully
Use PgBouncer if appropriate
```

---

# 18. Failure Type 11: Kafka Consumer Lag

Kafka lag means consumers are not keeping up with producers.

ASCII:

```text
Producer writes messages
          |
          v
Kafka topic partitions
          |
          v
Consumer group reads slower
          |
          v
Lag grows
```

Symptoms:

```text
Delayed notifications
Orders stuck in processing
Consumer lag dashboard increasing
CPU maybe low or high
DB writes slow
```

Check:

```bash
kubectl logs deploy/order-consumer -n prod --tail=200
kubectl top pods -n prod
```

Kafka CLI example:

```bash
kafka-consumer-groups.sh \
  --bootstrap-server kafka:9092 \
  --describe \
  --group order-consumer-group
```

Common causes:

```text
Consumer pod down
Consumer rebalance loop
Poison message retrying forever
DB dependency slow
Too few partitions
Too few consumer instances
Slow processing logic
```

Mental model:

```text
Kafka lag is not always Kafka problem.
It often means downstream processing is slow.
```

Mitigation:

```text
Scale consumers up to partition count
Pause bad topic path
Send poison message to DLT
Optimize downstream DB writes
Increase batch size carefully
```

---

# 19. Failure Type 12: Redis Latency / Cache Meltdown

Redis usually makes systems fast.

But Redis failure can make systems collapse if fallback hits the database.

ASCII:

```text
Normal path

App -> Redis cache -> response

Cache failure path

App -> Redis timeout
    -> DB fallback
    -> DB overload
    -> API timeout
```

Symptoms:

```text
Redis latency high
DB load suddenly increases
API p99 increases
Connection timeout logs
```

Common causes:

```text
Hot key
Large value
KEYS command
Network latency
Redis CPU saturation
Too many connections
Eviction storm
Cache stampede
```

Spring Boot Redis log examples:

```text
Redis command timed out
Unable to connect to Redis server
```

Mitigation:

```text
Short Redis timeout
Circuit breaker around cache
Request coalescing
Jittered TTL
Protect DB fallback
Rate limit expensive endpoint
```

Bad pattern:

```java
// Bad: every miss hits DB at same time
Product p = cache.get(id);
if (p == null) {
    p = repository.findById(id).orElseThrow();
    cache.put(id, p);
}
```

Better mental model:

```text
Cache is an accelerator, not an unlimited shield.
```

---

# 20. Failure Type 13: NetworkPolicy / DNS Failure

In Kubernetes, network problems often look like app problems.

Symptoms:

```text
App cannot call payment-service
UnknownHostException
Connection timed out
Connection refused
Works in one namespace but not another
```

ASCII:

```text
Pod A
  |
  | DNS lookup payment-service
  v
CoreDNS
  |
  v
Service IP
  |
  v
NetworkPolicy allowed?
  |
  +-- yes --> Pod B
  +-- no  --> timeout
```

Commands:

```bash
kubectl exec -it order-pod -n prod -- nslookup payment-service
kubectl exec -it order-pod -n prod -- curl -v http://payment-service:8080/health
kubectl get networkpolicy -n prod
kubectl logs -n kube-system deploy/coredns --tail=100
```

Common causes:

```text
Wrong service name
Wrong namespace DNS name
CoreDNS overloaded
NetworkPolicy denies egress
Service has no endpoints
Pod listens on wrong port
```

DNS names:

```text
payment-service
payment-service.prod
payment-service.prod.svc.cluster.local
```

Memory hook:

```text
Before blaming Java HTTP client, prove DNS and service path work from inside the Pod.
```

---

# 21. Failure Type 14: Node Pressure

Node pressure means the worker machine is unhealthy or overloaded.

Types:

```text
MemoryPressure
DiskPressure
PIDPressure
NetworkUnavailable
```

ASCII:

```text
Node
+--------------------------------+
| kubelet                        |
| pods                           |
| image layers                   |
| logs                           |
| emptyDir volumes               |
+--------------------------------+

If disk fills:
Pod eviction can start.
```

Commands:

```bash
kubectl get nodes
kubectl describe node <node-name>
kubectl top node
kubectl get events --all-namespaces --sort-by=.lastTimestamp
```

Symptoms:

```text
Pods evicted
Pods pending
Image pulls fail
Node NotReady
Random latency from noisy neighbor
```

Common causes:

```text
Too many pods on node
Logs filling disk
Image garbage collection failing
Memory overcommit
DaemonSet consuming resources
Bad requests/limits
```

Mitigation:

```text
Drain bad node if needed
Scale node group
Fix resource requests
Clean disk pressure source
Move noisy workload
```

Memory hook:

```text
A Pod problem can be a Node problem wearing a Pod mask.
```

---

# 22. Failure Type 15: Pending Pods

Pending means Kubernetes has not successfully placed or started the Pod.

Do not treat Pending as app failure.

ASCII:

```text
Pod object created
      |
      v
Scheduler tries to place it
      |
      +-- fits node --> assigned
      |
      +-- no fit    --> Pending
```

Commands:

```bash
kubectl describe pod order-service-abc -n prod
kubectl get events -n prod --sort-by=.lastTimestamp
kubectl get nodes
```

Look for messages:

```text
Insufficient cpu
Insufficient memory
node(s) had taint that the pod didn't tolerate
pod has unbound immediate PersistentVolumeClaims
node affinity rules not matched
```

Common causes:

```text
Requests too high
Cluster full
Missing toleration
Wrong nodeSelector
PVC not bound
Autoscaler cannot add node
ResourceQuota exceeded
```

Mitigation:

```text
Reduce request if wrong
Scale cluster
Fix taints/tolerations
Fix PVC/storage class
Fix quota
```

Mental model:

```text
Pending means the desired Pod exists, but the cluster cannot place it yet.
```

---

# 23. Failure Type 16: ConfigMap / Secret Mistake

Config failures are common because apps depend on environment-specific values.

Symptoms:

```text
Works in staging, fails in prod
CrashLoopBackOff after config change
401 from downstream service
DB connection refused
Feature suddenly enabled
```

ASCII:

```text
Deployment
   |
   v
Env vars from ConfigMap / Secret
   |
   v
Spring Boot property binding
   |
   v
App behavior
```

Commands:

```bash
kubectl describe deploy/order-service -n prod
kubectl get configmap order-config -n prod -o yaml
kubectl get secret order-secret -n prod
kubectl exec -it order-pod -n prod -- env | sort
```

Spring Boot property binding issue:

```text
PAYMENT_TIMEOUT_MS missing
payment.timeout-ms becomes null/default
App uses unsafe default
```

Safer Java config:

```java
@ConfigurationProperties(prefix = "payment")
@Validated
public class PaymentClientProperties {
    @NotBlank
    private String baseUrl;

    @Min(100)
    @Max(5000)
    private int timeoutMs;

    // getters and setters
}
```

Good mindset:

```text
Fail fast on missing critical config.
Never silently use dangerous defaults.
```

---

# 24. Failure Type 17: Database Migration Incident

Database migrations are dangerous because code and schema must remain compatible.

Bad release:

```text
Deploy code expecting new column
But migration failed
App crashes or returns 500
```

ASCII:

```text
Version N app expects table:
orders(id, amount)

Version N+1 app expects:
orders(id, amount, status)

If DB still old:
SELECT status fails
```

Safe migration model:

```text
Expand -> Deploy -> Migrate Data -> Contract
```

Example:

```text
1. Add nullable column
2. Deploy code that can handle old and new
3. Backfill data
4. Make code require new column
5. Remove old column later
```

Bad pattern:

```text
Rename column and deploy code at same time
```

Good pattern:

```text
Backward-compatible schema first
```

Commands/checks:

```bash
kubectl logs deploy/order-service -n prod --tail=200 | grep -i flyway
kubectl logs deploy/order-service -n prod --tail=200 | grep -i liquibase
```

Memory hook:

```text
Application rollback is easy.
Database rollback is often hard.
```

---

# 25. Failure Type 18: Thread Pool Exhaustion

Java services often fail because threads are waiting, not because CPU is high.

Symptoms:

```text
CPU moderate
Latency high
Requests timeout
Tomcat threads busy
DB/HTTP dependency slow
```

ASCII:

```text
Incoming HTTP requests
          |
          v
Tomcat worker threads
          |
          +--> waiting for DB
          +--> waiting for payment API
          +--> waiting for Redis
          |
          v
No free workers
          |
          v
New requests queue
```

Spring Boot metrics:

```text
tomcat.threads.busy
tomcat.threads.current
http.server.requests
executor.active
executor.queued
```

Bad code:

```java
@GetMapping("/orders/{id}")
public OrderResponse getOrder(@PathVariable Long id) {
    Order order = orderRepository.findById(id).orElseThrow();
    Payment payment = paymentClient.getPayment(order.paymentId());
    Shipment shipment = shipmentClient.getShipment(order.shipmentId());
    return mapper.toResponse(order, payment, shipment);
}
```

If payment API slows, request threads pile up.

Mitigation:

```text
Set timeouts
Use circuit breakers
Bulkhead downstream calls
Cache safe data
Fail gracefully
Reduce fan-out
```

Memory hook:

```text
A waiting thread is still a consumed thread.
```

---

# 26. Failure Type 19: Timeout Cascade

A timeout cascade happens when one slow dependency makes many services slow.

ASCII:

```text
Order Service
     |
     v
Payment Service slow
     |
     v
Order threads wait
     |
     v
Order API slow
     |
     v
Gateway threads wait
     |
     v
Whole system slow
```

Bad timeout design:

```text
Gateway timeout: 30s
Order timeout:   30s
Payment timeout: 30s
DB timeout:      30s
```

This makes failures expensive.

Better timeout budget:

```text
Gateway total: 2s
Order internal budget: 1.5s
Payment call: 500ms
DB call: 300ms
Redis call: 50ms
```

Spring Boot WebClient example:

```java
@Bean
WebClient paymentWebClient(WebClient.Builder builder) {
    HttpClient httpClient = HttpClient.create()
        .responseTimeout(Duration.ofMillis(500));

    return builder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl("http://payment-service")
        .build();
}
```

Mental model:

```text
Timeouts are not random numbers.
They are latency budgets.
```

---

# 27. Failure Type 20: HPA Does Not Scale

HPA scales based on metrics.

If metrics are missing or requests are wrong, scaling fails.

ASCII:

```text
HPA
 |
 | reads CPU/memory/custom metrics
 v
Metrics Server / Prometheus Adapter
 |
 v
Desired replicas calculated
 |
 v
Deployment scaled
```

Symptoms:

```text
Traffic high
Pods overloaded
Replicas not increasing
HPA shows unknown metrics
```

Commands:

```bash
kubectl get hpa -n prod
kubectl describe hpa order-service -n prod
kubectl top pods -n prod
kubectl top nodes
```

Common causes:

```text
CPU requests missing
Metrics server broken
Custom metric unavailable
Max replicas too low
Scale-up stabilization too conservative
Cluster autoscaler cannot add nodes
```

Bad config:

```yaml
resources:
  limits:
    cpu: 500m
# no requests
```

HPA CPU utilization depends on requests.

Good config:

```yaml
resources:
  requests:
    cpu: 500m
    memory: 768Mi
  limits:
    memory: 1Gi
```

Memory hook:

```text
HPA cannot scale from metrics it cannot see.
```

---

# 28. Failure Type 21: Rollout Stuck

Rollout stuck means Deployment cannot safely move to the new version.

Symptoms:

```text
kubectl rollout status hangs
New pods not ready
Old pods still serving
ProgressDeadlineExceeded
```

Commands:

```bash
kubectl rollout status deploy/order-service -n prod
kubectl describe deploy/order-service -n prod
kubectl get rs -n prod
kubectl get pods -n prod
kubectl describe pod <new-pod> -n prod
```

ASCII:

```text
Deployment wants v2
       |
       v
Creates v2 ReplicaSet
       |
       v
v2 Pods fail readiness
       |
       v
Deployment cannot remove all v1 Pods
       |
       v
Rollout stuck
```

Common causes:

```text
Readiness failing
Image pull failing
Insufficient resources
CrashLoopBackOff
Bad startup time
maxUnavailable too strict
```

Mitigation:

```text
Rollback if production impact
Fix readiness/probe/config
Check events
Check new ReplicaSet logs
```

Memory hook:

```text
A stuck rollout is usually Kubernetes protecting you from a bad version.
```

---

# 29. Failure Type 22: Certificate / TLS Failure

Certificate failures often look sudden.

Symptoms:

```text
Users see TLS error
Ingress returns SSL handshake failure
Service-to-service HTTPS fails
Java logs PKIX path building failed
```

ASCII:

```text
Client
  |
  v
TLS handshake
  |
  +-- valid cert --> request continues
  +-- expired/untrusted cert --> failure before app logic
```

Java error:

```text
javax.net.ssl.SSLHandshakeException:
PKIX path building failed
```

Kubernetes checks:

```bash
kubectl get secret -n prod | grep tls
kubectl describe ingress order-ingress -n prod
kubectl get certificate -n prod
kubectl describe certificate api-cert -n prod
```

Common causes:

```text
Certificate expired
cert-manager renewal failed
Wrong secret attached to ingress
Missing CA trust in JVM
Hostname mismatch
```

Mitigation:

```text
Renew certificate
Fix cert-manager issuer
Attach correct TLS secret
Update JVM truststore if internal CA
```

Memory hook:

```text
TLS failure happens before your controller method runs.
```

---

# 30. Safe Mitigation Strategies

Mitigation means reducing customer pain before final root cause.

Safe options:

```text
Rollback recent release
Disable feature flag
Scale stateless service
Route traffic away from bad region
Increase timeout only if safe
Reduce traffic with rate limit
Pause consumer temporarily
Move poison message to DLT
```

Unsafe options:

```text
Delete random pods repeatedly
Increase all connection pools blindly
Disable readiness/liveness without understanding
Run manual DB updates during panic
Scale consumers beyond partition/downstream capacity
```

Decision model:

```text
Can this action make blast radius worse?
Can we reverse it quickly?
Will it destroy evidence?
Does it protect customers?
```

ASCII:

```text
Incident action
     |
     v
Reversible?
 /        \
no        yes
|          |
v          v
avoid   consider
```

The best mitigation is often boring:

```bash
kubectl rollout undo deploy/order-service -n prod
```

But only if the recent deployment is likely related.

---

# 31. Evidence Collection Before Restart

Before restarting, collect evidence.

```text
Logs
Events
Pod description
Metrics snapshot
Recent deploy info
Thread dump if needed
Heap dump if safe
```

Commands:

```bash
kubectl describe pod order-service-abc -n prod > pod.describe.txt
kubectl logs order-service-abc -n prod > pod.current.log
kubectl logs order-service-abc -n prod --previous > pod.previous.log
kubectl get events -n prod --sort-by=.lastTimestamp > events.txt
kubectl describe deploy/order-service -n prod > deploy.describe.txt
```

For Java thread dump:

```bash
kubectl exec -it order-service-abc -n prod -- jcmd 1 Thread.print
```

For heap info:

```bash
kubectl exec -it order-service-abc -n prod -- jcmd 1 GC.heap_info
```

Mental model:

```text
Restart can reduce pain.
Restart can also erase clues.
```

Production rule:

```text
Collect fast evidence, then mitigate.
```

---

# 32. Java Spring Boot Incident Controller Example

During incidents, correlation IDs make logs usable.

Simple filter:

```java
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Correlation-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String correlationId = request.getHeader(HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);
        response.setHeader(HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("correlationId");
        }
    }
}
```

Why it matters:

```text
Without correlation ID:
1000 logs from 1000 requests are mixed.

With correlation ID:
One request becomes traceable across services.
```

ASCII:

```text
Request abc-123
   |
   +--> Gateway log abc-123
   +--> Order log abc-123
   +--> Payment log abc-123
   +--> DB error abc-123
```

Incident debugging needs traceability.

---

# 33. Resilience Code Example: Circuit Breaker Boundary

A circuit breaker prevents one dependency failure from consuming the whole service.

Example with Resilience4j style:

```java
@Service
public class PaymentGateway {

    private final WebClient paymentClient;

    public PaymentGateway(WebClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @CircuitBreaker(name = "payment", fallbackMethod = "fallbackPayment")
    @TimeLimiter(name = "payment")
    public CompletableFuture<PaymentStatus> getPaymentStatus(String orderId) {
        return paymentClient.get()
            .uri("/payments/{orderId}", orderId)
            .retrieve()
            .bodyToMono(PaymentStatus.class)
            .timeout(Duration.ofMillis(500))
            .toFuture();
    }

    private CompletableFuture<PaymentStatus> fallbackPayment(String orderId, Throwable ex) {
        return CompletableFuture.completedFuture(PaymentStatus.temporarilyUnavailable(orderId));
    }
}
```

Mental model:

```text
Without circuit breaker:
Dependency burns your threads.

With circuit breaker:
Failure is contained at boundary.
```

ASCII:

```text
Order Service
    |
    v
Circuit Breaker
    |
    +-- dependency healthy --> call Payment
    |
    +-- dependency failing --> fast fallback
```

Do not use circuit breakers to hide bugs.

Use them to protect the system from cascading failure.

---

# 34. Kubernetes Debug Pod Pattern

Sometimes you need a temporary pod inside the cluster network.

Use it to test DNS, Service, and connectivity.

```bash
kubectl run debug-shell \
  -n prod \
  --rm -it \
  --image=curlimages/curl \
  -- sh
```

Inside:

```bash
nslookup order-service
curl -v http://order-service:8080/actuator/health
curl -v http://payment-service:8080/actuator/health
```

ASCII:

```text
Your laptop outside cluster
        |
        v
May not see internal DNS

Debug pod inside cluster
        |
        v
Sees same network as app pods
```

Use this when:

```text
Service DNS suspected
NetworkPolicy suspected
Wrong port suspected
Ingress hides internal issue
```

Memory hook:

```text
Debug from the same place where the failing workload lives.
```

---

# 35. Production Story: The 503 That Was Not Ingress

Incident:

```text
Users report 503 from api.example.com/orders.
```

Initial panic:

```text
Ingress is broken.
```

Investigation:

```bash
kubectl describe ingress order-ingress -n prod
kubectl get svc order-service -n prod
kubectl get endpoints order-service -n prod
```

Result:

```text
endpoints: <none>
```

Pods:

```bash
kubectl get pods -n prod
```

Output:

```text
order-service-abc   0/1 Running
order-service-def   0/1 Running
```

Logs:

```text
Readiness DOWN: database connection timeout
```

Root cause:

```text
Database credentials rotated.
Secret was not updated for order-service.
Pods running but not ready.
Service had no endpoints.
Ingress returned 503.
```

Correct mental model:

```text
Ingress was only the messenger.
Readiness removed all endpoints.
Secret caused readiness failure.
```

Final fix:

```text
Update Secret
Restart deployment safely
Add secret rotation checklist
Add alert for Service endpoint count = 0
```

---

# 36. Production Story: The Rollback That Failed

Incident:

```text
New version caused 500 errors.
Team rolled back application.
Errors continued.
```

Why?

The deployment included a database migration:

```text
ALTER TABLE orders RENAME COLUMN status TO order_status;
```

Old application expected:

```sql
SELECT status FROM orders
```

But column was renamed.

ASCII:

```text
Code rollback
    |
    v
Old code restored
    |
    v
Database schema still new
    |
    v
Old code still fails
```

Lesson:

```text
App rollback does not automatically rollback database schema.
```

Better plan:

```text
Expand schema first
Deploy compatible code
Backfill
Switch reads
Remove old column later
```

Memory hook:

```text
Database migrations must be forward and backward compatible during rollout.
```

---

# 37. Production Story: Scaling Made It Worse

Incident:

```text
Order API latency high.
Engineer scaled replicas from 5 to 20.
```

Result:

```text
Database collapsed.
```

Why?

Before:

```text
5 pods x 20 DB connections = 100 connections
```

After:

```text
20 pods x 20 DB connections = 400 connections
```

Database max comfortable connections:

```text
150
```

ASCII:

```text
More pods
  |
  v
More DB connections
  |
  v
More DB context switching
  |
  v
Slower queries
  |
  v
More app waiting
```

Correct mitigation:

```text
Find slow query
Reduce connection pool
Add index
Rollback bad query release
Use rate limiting
Use queue/backpressure
```

Lesson:

```text
Scaling app replicas does not scale the database.
```

---

# 38. Post-Incident Review Mental Model

A postmortem is not a blame document.

It is a learning document.

Structure:

```text
1. What happened?
2. Customer impact
3. Timeline
4. Detection
5. Root cause
6. Contributing factors
7. What went well?
8. What went poorly?
9. Action items
```

ASCII:

```text
Incident
   |
   v
Timeline
   |
   v
Root cause
   |
   v
Prevention
   |
   v
Better system
```

Bad postmortem:

```text
Engineer forgot to update config.
```

Good postmortem:

```text
The deployment process allowed required config to be missing without validation.
No preflight check existed.
No alert existed for zero endpoints.
```

Good action items:

```text
Add startup validation for required config
Add canary rollout
Add endpoint-count alert
Add secret rotation runbook
Add migration compatibility checklist
```

Memory hook:

```text
Do not ask who failed.
Ask which guardrail was missing.
```

---

# 39. Incident Command Roles

For serious incidents, roles reduce chaos.

```text
Incident Commander  -> coordinates response
Investigator        -> debugs technical cause
Communicator        -> updates stakeholders
Scribe              -> records timeline
Operator            -> performs approved actions
```

ASCII:

```text
                 Incident Commander
                         |
       +-----------------+-----------------+
       |                 |                 |
       v                 v                 v
 Investigator       Communicator         Scribe
       |
       v
   Operator
```

Why this matters:

```text
Without roles, everyone debugs and nobody communicates.
Without timeline, nobody remembers what happened.
Without commander, multiple people make conflicting changes.
```

For small teams, one person can hold multiple roles.

But still keep the responsibilities clear.

Incident rule:

```text
One person decides actions.
Many people can provide evidence.
```

---

# 40. Debugging Checklist: Kubernetes Layer

Use this when production service is failing.

```text
[ ] Is Deployment available?
[ ] Is rollout complete?
[ ] Are ReplicaSets expected?
[ ] Are Pods Running?
[ ] Are Pods Ready?
[ ] Are restarts increasing?
[ ] Any CrashLoopBackOff?
[ ] Any ImagePullBackOff?
[ ] Any OOMKilled?
[ ] Are Pods scheduled on healthy nodes?
[ ] Are requests/limits reasonable?
[ ] Does Service selector match Pod labels?
[ ] Does Service have endpoints?
[ ] Does Ingress point to correct Service/port?
[ ] Are NetworkPolicies blocking traffic?
[ ] Is DNS resolving inside cluster?
```

Commands:

```bash
kubectl get deploy,rs,pods,svc,endpoints,ingress -n prod
kubectl get events -n prod --sort-by=.lastTimestamp
kubectl describe pod <pod> -n prod
kubectl logs <pod> -n prod --previous
kubectl top pods -n prod
kubectl top nodes
```

---

# 41. Debugging Checklist: Spring Boot Layer

Use this when Kubernetes looks healthy but users still suffer.

```text
[ ] Are HTTP 5xx increasing?
[ ] Which endpoint is failing?
[ ] Is p99 high for all endpoints or one endpoint?
[ ] Are Tomcat threads busy?
[ ] Are DB pool pending connections increasing?
[ ] Are downstream HTTP calls timing out?
[ ] Are Kafka consumers lagging?
[ ] Are Redis calls timing out?
[ ] Are GC pauses high?
[ ] Is memory increasing continuously?
[ ] Did config change?
[ ] Did feature flag change?
[ ] Did schema migration run?
[ ] Are logs correlated by request ID?
```

Useful Actuator endpoints:

```text
/actuator/health
/actuator/health/readiness
/actuator/metrics
/actuator/prometheus
/actuator/loggers
```

Useful metrics:

```text
http.server.requests
jvm.memory.used
jvm.gc.pause
tomcat.threads.busy
hikaricp.connections.pending
resilience4j.circuitbreaker.state
```

Memory hook:

```text
If Kubernetes is green but users are red, inspect application internals.
```

---

# 42. Interview Answers

## How do you debug a production outage in Kubernetes?

I start with impact and scope. I check whether customer traffic is affected, which endpoints are failing, and whether there was a recent deployment or config change. Then I follow the request path: Ingress, Service, endpoints, Pods, container logs, readiness, and dependencies. I avoid random restarts and collect evidence before mitigation. If a recent deployment caused impact, I rollback safely first and root-cause afterward.

## What does Service has no endpoints mean?

It means the Service cannot find any matching Ready Pods. The cause may be wrong selectors, wrong Pod labels, failing readiness probes, Pods in another namespace, or no running Pods. The Service may exist and DNS may resolve, but traffic cannot reach application Pods.

## What is CrashLoopBackOff?

CrashLoopBackOff means the container starts, exits, and Kubernetes keeps restarting it with increasing backoff delay. The root cause is usually inside the application process or config, such as missing environment variables, DB connection failure, migration failure, or OutOfMemoryError. I check `kubectl logs --previous` and `kubectl describe pod`.

## What is ImagePullBackOff?

ImagePullBackOff means the node cannot pull the container image from the registry. Common causes are wrong image tag, missing imagePullSecret, registry authentication failure, image not found, or network issues to the registry.

## Why can scaling replicas make an outage worse?

If the bottleneck is a shared dependency like database, scaling app replicas can increase DB connections and load, making the dependency slower. Scaling is safe only if the service is stateless and the downstream dependencies have enough capacity.

## How do you handle a bad deployment?

If error rate or latency increased immediately after deployment and rollback is safe, I rollback first to reduce customer impact. Then I compare old and new versions, check logs, config, migrations, feature flags, and missing compatibility assumptions.

## How do you prevent timeout cascades?

I use strict timeout budgets, circuit breakers, bulkheads, retries with backoff only for safe operations, and graceful fallback. I avoid long default timeouts and ensure upstream timeouts are larger than downstream budgets but still bounded.

---

# 43. Cheat Sheet

```text
Production failure      = expected behavior != actual behavior
First goal              = reduce customer impact
Second goal             = preserve evidence
Third goal              = find root cause
Golden signals          = latency, traffic, errors, saturation
CrashLoopBackOff        = app starts then crashes repeatedly
ImagePullBackOff        = node cannot pull image
Pending Pod             = scheduler cannot place/start Pod
Readiness failure       = app not allowed to receive traffic
Liveness failure        = kubelet restarts container
No endpoints            = Service sees no Ready matching Pods
Ingress 503             = usually no healthy upstream
OOMKilled               = process exceeded memory limit
CPU throttling          = container hit CPU limit
DB pool exhaustion      = requests waiting for DB connections
Kafka lag               = consumers slower than producers
Timeout cascade         = slow dependency spreads failure
Rollback                = safest mitigation for bad deploy
Postmortem              = system learning, not blame
```

Command memory:

```bash
kubectl get deploy,rs,pods,svc,endpoints,ingress -n prod
kubectl describe pod <pod> -n prod
kubectl logs <pod> -n prod --previous
kubectl get events -n prod --sort-by=.lastTimestamp
kubectl rollout history deploy/<name> -n prod
kubectl rollout undo deploy/<name> -n prod
kubectl top pods -n prod
kubectl top nodes
```

---

# 44. One Picture To Remember

```text
                         ALERT
                           |
                           v
                  +----------------+
                  | Is impact real?|
                  +-------+--------+
                          |
                          v
                  +----------------+
                  | Recent change? |
                  +-------+--------+
                          |
        +-----------------+-----------------+
        |                                   |
        v                                   v
  Rollback / flag off              Follow request path
        |                                   |
        v                                   v
 Customer pain reduced       DNS -> LB -> Ingress -> Service
                                            |
                                            v
                                  Endpoints -> Pod -> App
                                            |
                                            v
                                  DB / Redis / Kafka
                                            |
                                            v
                                      Root cause
                                            |
                                            v
                                      Prevention
```

Final memory hook:

```text
Do not debug production by guessing.
Debug by walking the request path and checking each contract.
```

```text
Ingress contract: route exists
Service contract: endpoints exist
Pod contract: ready and healthy
App contract: fast successful response
Dependency contract: responds within budget
```

If you follow contracts, production incidents become understandable.

Not easy.

But understandable.

---

# 45. Final Production Failure Checklist

```text
[ ] I checked customer impact before root cause.
[ ] I checked recent deploy/config/secret/migration change.
[ ] I followed the request path from edge to dependency.
[ ] I checked Service endpoints before blaming Ingress.
[ ] I checked readiness before blaming Service.
[ ] I checked previous logs for CrashLoopBackOff.
[ ] I checked events for scheduling/image/probe failures.
[ ] I checked CPU throttling and memory OOMKilled.
[ ] I checked DB pool metrics before scaling pods.
[ ] I checked Kafka lag and downstream speed.
[ ] I checked Redis latency and fallback pressure.
[ ] I collected evidence before destructive restart.
[ ] I mitigated customer pain safely.
[ ] I wrote root cause and prevention actions.
```

Final sentence:

```text
A senior engineer is not the person who knows every command.
A senior engineer is the person who stays calm, follows the system path, protects customers, and turns every incident into a stronger system.
```
