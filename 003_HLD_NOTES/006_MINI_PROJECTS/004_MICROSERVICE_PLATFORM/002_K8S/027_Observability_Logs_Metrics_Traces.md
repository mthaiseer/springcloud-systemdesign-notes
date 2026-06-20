# 027_Observability_Logs_Metrics_Traces.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Observability Exists

Most developers learn observability as three separate tools:

```text
logs
metrics
traces
```

Then they memorize commands:

```bash
kubectl logs
Prometheus query
Grafana dashboard
Jaeger trace
```

That is not the real model.

The real production problem is this:

```text
User says: payment is slow
Developer asks: where is it slow?
Kubernetes says: pods are running
Database says: maybe not me
Network says: maybe not me
Kafka says: maybe lag
```

Observability exists because production systems are distributed, temporary, and noisy.

In Kubernetes, a request may travel like this:

```text
Mobile App
   |
   v
Ingress
   |
   v
API Gateway
   |
   v
order-service Pod
   |
   +--> payment-service Pod
   |
   +--> inventory-service Pod
   |
   +--> Kafka
   |
   +--> PostgreSQL
```

If the user sees latency, you cannot SSH into one machine and guess.

You need evidence.

Observability is not about collecting everything.

Observability is about answering production questions quickly.

```text
What happened?        -> Logs
How bad is it?        -> Metrics
Where did time go?    -> Traces
```

One picture:

```text
Incident
   |
   v
Question
   |
   v
Signal
   |
   v
Answer
   |
   v
Fix
```

If you remember only one thing:

```text
Observability = ability to explain system behavior from outside evidence
```

---

# 2. The Wrong Way To Think About Observability

Bad mental model:

```text
Logs = print statements
Metrics = CPU charts
Traces = fancy UI
```

This is memorization.

Better model:

```text
Logs    = event story
Metrics = system scoreboard
Traces  = request journey
```

Wrong debugging flow:

```text
Open random logs
Scroll thousands of lines
Guess the error
Restart pod
Hope problem disappears
```

Correct debugging flow:

```text
Start from symptom
Find affected service
Check golden metrics
Jump to trace
Use logs for exact event
Fix root cause
```

ASCII:

```text
Bad Debugging

Alert
  |
  v
Random Logs
  |
  v
Guess
  |
  v
Restart
  |
  v
Problem returns


Good Debugging

Alert
  |
  v
Metrics show blast radius
  |
  v
Trace shows slow dependency
  |
  v
Logs show exact failure
  |
  v
Fix cause
```

Do not memorize observability tools first.

Understand the questions first.

---

# 3. Real World Analogy: Hospital

Imagine a hospital patient.

Doctors do not only ask:

```text
Is the patient alive?
```

They observe:

```text
heart rate
blood pressure
temperature
oxygen level
symptoms
medical history
scan reports
```

Kubernetes `Running` is like saying:

```text
Patient is alive
```

But production needs more:

```text
Is the patient healthy?
Is the patient getting worse?
Which organ is failing?
When did symptoms start?
```

Observability signals map like this:

```text
Metrics = vital signs
Logs    = doctor notes
Traces  = patient journey through hospital departments
```

Diagram:

```text
Patient Care                         Production System

Heart rate      ------------------>  request rate
Blood pressure  ------------------>  latency
Temperature     ------------------>  error rate
Doctor notes    ------------------>  logs
Hospital route  ------------------>  distributed trace
```

A doctor does not memorize numbers only.

A doctor uses signals to reason.

Same with production engineering.

---

# 4. Real World Analogy: Delivery Company

Imagine a delivery company.

Customer says:

```text
My package is late.
```

The company needs to answer:

```text
Was it picked up?
Which warehouse handled it?
Did customs delay it?
Was the truck late?
Was the address wrong?
```

Observability in microservices is similar.

A customer request is like a package.

```text
Request ID = tracking number
Service logs = warehouse notes
Metrics = delivery performance dashboard
Trace = package route map
```

ASCII:

```text
Package Tracking

Customer
   |
   v
Warehouse A
   |
   v
Truck
   |
   v
Warehouse B
   |
   v
Delivery


Distributed Request Tracking

Client
   |
   v
Gateway
   |
   v
Order Service
   |
   v
Payment Service
   |
   v
Database
```

Without tracking number, each warehouse has isolated notes.

With tracking number, the full story becomes visible.

In microservices, that tracking number is usually:

```text
traceId
correlationId
requestId
```

---

# 5. The Core Observability Picture

```text
                         USER REQUEST
                              |
                              v
                       +-------------+
                       | Ingress     |
                       +------+------+ 
                              |
                              v
                       +-------------+
                       | Gateway     |
                       +------+------+ 
                              |
                              v
                       +-------------+
                       | Service A   |
                       +------+------+ 
                              |
                 +------------+------------+
                 |                         |
                 v                         v
          +-------------+            +-------------+
          | Service B   |            | Database    |
          +-------------+            +-------------+

Each component emits signals:

+-------------+     +-------------+     +-------------+
| Logs        |     | Metrics     |     | Traces      |
| events      |     | numbers     |     | spans       |
+-------------+     +-------------+     +-------------+
       |                   |                   |
       v                   v                   v
+-------------------------------------------------------+
| Observability Platform                                |
| Loki / Elasticsearch / Prometheus / Grafana / Jaeger  |
+-------------------------------------------------------+
```

The system is not observable because tools exist.

The system is observable when signals are connected.

```text
Metric alert tells you something is wrong.
Trace tells you where.
Log tells you why.
```

---

# 6. Kubernetes Changes The Observability Problem

In a VM world, you may have stable servers:

```text
server-1
server-2
server-3
```

In Kubernetes, Pods are temporary:

```text
order-service-7c9d8f4b7f-x92la
order-service-7c9d8f4b7f-k31pq
order-service-7c9d8f4b7f-z88mn
```

A Pod can disappear anytime.

```text
Pod crashes
Pod is rescheduled
Node drains
Deployment rolls out
Autoscaler adds replicas
```

If logs only live inside the container filesystem, they vanish with the Pod.

That is why Kubernetes observability needs external collection.

```text
Pod writes stdout/stderr
      |
      v
Node log files
      |
      v
Collector DaemonSet
      |
      v
Central log store
```

ASCII:

```text
Pod A logs locally
   |
   v
Pod deleted
   |
   v
Local view lost


Pod A stdout
   |
   v
Node agent collects
   |
   v
Central storage
   |
   v
Search after pod dies
```

Mental model:

```text
Kubernetes makes compute disposable.
Observability makes evidence durable.
```

---

# 7. The Three Signals Mental Model

Observability has three classic signals.

```text
Logs
Metrics
Traces
```

Do not memorize definitions.

Map them to questions.

```text
Logs:
  What exact event happened?

Metrics:
  How many times and how severe?

Traces:
  Where did this request spend time?
```

Diagram:

```text
Production Question                 Best Signal

Why did order 123 fail?        ---> Logs
Is payment latency increasing? ---> Metrics
Which service is slow?         ---> Traces
How many pods are crashing?    ---> Metrics
What exception occurred?       ---> Logs
Which DB query delayed request?-> Trace + logs
```

Signals are complementary.

Bad practice:

```text
Use logs for everything
```

Better practice:

```text
Metrics for detection
Traces for localization
Logs for explanation
```

---

# 8. Logs: Event Story

A log is an event record.

Example:

```json
{
  "timestamp": "2026-06-14T10:15:30Z",
  "level": "ERROR",
  "service": "order-service",
  "traceId": "abc-123",
  "orderId": "ORD-91",
  "message": "Payment authorization failed",
  "error": "TimeoutException"
}
```

Good log tells a story:

```text
When did it happen?
Where did it happen?
Who was affected?
What was attempted?
What failed?
How can I connect it to other systems?
```

Bad log:

```text
Error occurred
```

Good log:

```text
payment_authorization_failed orderId=ORD-91 provider=stripe timeoutMs=3000 traceId=abc-123
```

ASCII:

```text
Request enters order-service
        |
        v
Log: received order request
        |
        v
Log: calling payment-service
        |
        v
Log: payment timeout
        |
        v
Log: returning 503
```

Logs are excellent for exact evidence.

But logs are terrible as your only monitoring system.

Why?

```text
Too much volume
Hard to aggregate
Expensive to store
Easy to miss pattern
```

---

# 9. Kubernetes Log Flow

Container writes to stdout/stderr.

```text
Spring Boot app
   |
   | System.out / logger
   v
Container stdout/stderr
   |
   v
Container runtime log file on node
   |
   v
Kubelet exposes logs
   |
   v
kubectl logs
```

But production usually adds a collector:

```text
Node
+---------------------------------------+
| Pod A stdout                          |
| Pod B stdout                          |
| Pod C stdout                          |
|                                       |
| Fluent Bit / Vector / Promtail        |
+-------------------+-------------------+
                    |
                    v
          Central Log Storage
```

Command:

```bash
kubectl logs deployment/order-service
kubectl logs pod/order-service-abc
kubectl logs pod/order-service-abc --previous
```

`--previous` is important for CrashLoopBackOff.

```text
Current container restarted
Previous container had crash logs
```

Debug model:

```text
Pod Running but request fails       -> current logs
Pod CrashLoopBackOff                -> previous logs
Pod deleted                         -> central logs needed
Multiple replicas                   -> aggregate by label/service
```

---

# 10. Structured Logging

Plain text logs are easy to write but hard to query.

Bad:

```text
Payment failed for order ORD-91 because timeout
```

Better:

```json
{
  "event": "payment_failed",
  "orderId": "ORD-91",
  "reason": "timeout",
  "timeoutMs": 3000,
  "traceId": "abc-123"
}
```

Why structured logs matter:

```text
Search by orderId
Group by reason
Filter by service
Join with traceId
Create alerts from patterns
```

ASCII:

```text
Unstructured Logs
   |
   v
Human reads text manually

Structured Logs
   |
   v
Machine filters fields
   |
   v
Fast investigation
```

Spring Boot logging mindset:

```text
Log events, not paragraphs.
Log business identifiers carefully.
Log traceId/correlationId.
Do not log passwords or tokens.
Do not log full payloads blindly.
```

Production-safe logs are searchable, minimal, and connected.

---

# 11. Spring Boot Logging Example

Simple controller:

```java
@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
        log.info("order_create_request_received customerId={} itemCount={}",
                request.customerId(), request.items().size());

        OrderResponse response = orderService.createOrder(request);

        log.info("order_created orderId={} customerId={}",
                response.orderId(), request.customerId());

        return ResponseEntity.ok(response);
    }
}
```

Service with failure logging:

```java
@Service
public class PaymentClient {

    private static final Logger log = LoggerFactory.getLogger(PaymentClient.class);
    private final RestTemplate restTemplate;

    public PaymentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PaymentResult authorize(String orderId, long amount) {
        long start = System.currentTimeMillis();
        try {
            PaymentResult result = restTemplate.postForObject(
                    "http://payment-service/payments/authorize",
                    new PaymentRequest(orderId, amount),
                    PaymentResult.class
            );
            log.info("payment_authorized orderId={} amount={} latencyMs={}",
                    orderId, amount, System.currentTimeMillis() - start);
            return result;
        } catch (RestClientException ex) {
            log.error("payment_authorization_failed orderId={} amount={} latencyMs={} error={}",
                    orderId, amount, System.currentTimeMillis() - start, ex.toString());
            throw ex;
        }
    }
}
```

Good logs help answer:

```text
Which order failed?
Which dependency failed?
How long did it take before failure?
```

---

# 12. Metrics: System Scoreboard

Metrics are numeric measurements over time.

Examples:

```text
http_requests_total
http_server_requests_seconds
jvm_memory_used_bytes
process_cpu_usage
kube_pod_container_status_restarts_total
container_cpu_usage_seconds_total
```

Metrics are not stories.

Metrics are scoreboard numbers.

```text
Request rate: 1000 rps
Error rate: 3%
P99 latency: 800 ms
CPU: 82%
Memory: 1.5 GB
Kafka lag: 50,000 messages
```

ASCII:

```text
Every request updates counters/timers
        |
        v
Metrics endpoint exposes numbers
        |
        v
Prometheus scrapes periodically
        |
        v
Grafana shows charts
        |
        v
Alertmanager sends alerts
```

Metrics are best for:

```text
detection
trend
capacity
alerting
SLO tracking
```

They answer:

```text
Is the system healthy?
Is it getting worse?
How many users are impacted?
Do we need scaling?
```

---

# 13. Golden Signals

A strong production dashboard starts with golden signals.

For user-facing services:

```text
Traffic
Errors
Latency
Saturation
```

Mental model:

```text
Traffic    = how many requests?
Errors     = how many failed?
Latency    = how slow?
Saturation = how full?
```

ASCII:

```text
                SERVICE HEALTH
                     |
     +---------------+---------------+
     |               |               |
     v               v               v
  Traffic          Errors         Latency
     |               |               |
     +---------------+---------------+
                     |
                     v
                 Saturation
```

Example for order-service:

```text
Traffic:
  http requests per second

Errors:
  5xx rate, failed payment calls

Latency:
  p50, p95, p99 request duration

Saturation:
  CPU, memory, DB pool usage, thread pool queue
```

Do not start with 100 charts.

Start with these questions:

```text
Are users reaching us?
Are we failing them?
Are we slow?
Are we overloaded?
```

---

# 14. RED and USE Methods

For request-driven services, use RED:

```text
Rate
Errors
Duration
```

For infrastructure resources, use USE:

```text
Utilization
Saturation
Errors
```

Diagram:

```text
Application Service                  Infrastructure Resource

Rate       --------------------      Utilization
Errors     --------------------      Saturation
Duration   --------------------      Errors
```

Example:

```text
order-service RED:
  request rate
  error rate
  request duration

Node USE:
  CPU utilization
  run queue saturation
  disk/network errors
```

Why this matters:

```text
If API latency is high, RED shows user pain.
If CPU/disk/network is exhausted, USE shows resource pressure.
```

Production thinking:

```text
User symptom first.
Resource symptom second.
```

Bad dashboard:

```text
Only CPU and memory
```

Better dashboard:

```text
User-facing RED + resource-facing USE
```

---

# 15. Prometheus Mental Model

Prometheus pulls metrics.

It does not wait for apps to push by default.

```text
Spring Boot Actuator /actuator/prometheus
              ^
              |
        Prometheus scrapes
```

Flow:

```text
Application exposes metrics endpoint
        |
        v
Prometheus periodically scrapes endpoint
        |
        v
Stores time series
        |
        v
PromQL queries calculate rates/latency/errors
        |
        v
Grafana dashboards visualize
        |
        v
Alertmanager notifies
```

ASCII:

```text
+------------------+        scrape        +----------------+
| order-service    | <------------------- | Prometheus     |
| /actuator/prom   |                      | time-series DB |
+------------------+                      +-------+--------+
                                                   |
                                                   v
                                            +-------------+
                                            | Grafana     |
                                            +-------------+
                                                   |
                                                   v
                                            +-------------+
                                            | Alerts      |
                                            +-------------+
```

Mental model:

```text
App exposes facts.
Prometheus records facts over time.
Grafana explains facts visually.
Alerts wake humans only when action is needed.
```

---

# 16. Spring Boot Metrics Example

Dependencies usually include:

```text
spring-boot-starter-actuator
micrometer-registry-prometheus
```

Application config:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  endpoint:
    health:
      probes:
        enabled: true
  metrics:
    tags:
      application: order-service
```

Custom counter:

```java
@Service
public class OrderMetrics {

    private final Counter orderCreatedCounter;
    private final Counter orderFailedCounter;

    public OrderMetrics(MeterRegistry registry) {
        this.orderCreatedCounter = Counter.builder("orders_created_total")
                .description("Total created orders")
                .tag("service", "order-service")
                .register(registry);

        this.orderFailedCounter = Counter.builder("orders_failed_total")
                .description("Total failed orders")
                .tag("service", "order-service")
                .register(registry);
    }

    public void orderCreated() {
        orderCreatedCounter.increment();
    }

    public void orderFailed() {
        orderFailedCounter.increment();
    }
}
```

Timer:

```java
@Service
public class InventoryClient {

    private final Timer inventoryTimer;

    public InventoryClient(MeterRegistry registry) {
        this.inventoryTimer = Timer.builder("inventory_reservation_duration")
                .description("Inventory reservation latency")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    public boolean reserve(String sku, int quantity) {
        return inventoryTimer.record(() -> callInventoryService(sku, quantity));
    }

    private boolean callInventoryService(String sku, int quantity) {
        return true;
    }
}
```

Do not create high-cardinality labels.

Bad metric label:

```text
orderId=ORD-123
userId=U-99
```

Good metric label:

```text
status=success|failure
method=POST
endpoint=/orders
```

---

# 17. Metric Cardinality Trap

Metric cardinality means number of unique time series.

Example:

```text
http_requests_total{method="GET",status="200"}
http_requests_total{method="GET",status="500"}
```

This is okay.

Bad:

```text
http_requests_total{userId="1001"}
http_requests_total{userId="1002"}
http_requests_total{userId="1003"}
...
```

If millions of users exist, millions of time series are created.

ASCII:

```text
Low Cardinality

status=200
status=500
status=404

Few series


High Cardinality

userId=1
userId=2
userId=3
...
userId=10000000

Huge series explosion
```

Impact:

```text
Prometheus memory increases
Query becomes slow
Storage cost increases
Dashboard becomes unstable
```

Rule:

```text
Metrics are for aggregate behavior.
Logs/traces are for individual request identity.
```

Use `orderId` in logs/traces, not metrics labels.

---

# 18. Traces: Request Journey

A trace shows the path of one request across services.

```text
Trace = full request journey
Span  = one operation inside the journey
```

Example:

```text
TraceId: abc-123

gateway                 20 ms
  order-service          180 ms
    inventory-service     40 ms
    payment-service      120 ms
      payment-db          95 ms
```

ASCII:

```text
Client Request
    |
    v
[Gateway Span 20ms]
    |
    v
[Order Service Span 180ms]
    |             |
    |             v
    |       [Inventory Span 40ms]
    |
    v
[Payment Span 120ms]
    |
    v
[Payment DB Span 95ms]
```

Traces answer:

```text
Which service was slow?
Which dependency failed?
Did retries happen?
Was there fan-out?
How much time was spent in DB vs network vs app?
```

Metrics show:

```text
P99 latency is bad
```

Trace shows:

```text
Payment DB call consumed 900 ms
```

Logs show:

```text
SQL timeout on transaction authorization
```

---

# 19. Trace Context Propagation

Tracing works only if context moves with the request.

```text
Client sends request
   |
   | traceparent header
   v
Gateway
   |
   | same traceId passed downstream
   v
Order Service
   |
   | same traceId passed downstream
   v
Payment Service
```

Without propagation:

```text
Gateway trace: abc
Order trace: def
Payment trace: xyz
```

You get broken stories.

With propagation:

```text
Gateway trace: abc
Order trace: abc
Payment trace: abc
```

ASCII:

```text
Broken Trace

Gateway [trace=A]
Order   [trace=B]
Payment [trace=C]

Hard to connect


Connected Trace

Gateway [trace=A]
Order   [trace=A]
Payment [trace=A]

One journey
```

Important headers:

```text
traceparent
tracestate
b3
x-request-id
x-correlation-id
```

Modern stacks often use OpenTelemetry.

Mental model:

```text
Trace context is the thread that stitches distributed work together.
```

---

# 20. OpenTelemetry Mental Model

OpenTelemetry is a vendor-neutral way to collect telemetry.

It helps generate and export:

```text
traces
metrics
logs
```

Typical flow:

```text
Application
   |
   | telemetry SDK / agent
   v
OpenTelemetry Collector
   |
   +--> Jaeger / Tempo for traces
   +--> Prometheus / backend for metrics
   +--> Loki / Elasticsearch for logs
```

ASCII:

```text
+------------------+
| Spring Boot App  |
| OTel SDK/Agent   |
+--------+---------+
         |
         v
+-------------------------+
| OpenTelemetry Collector |
+----+------------+-------+
     |            |
     v            v
 Trace Store    Metric Store
 Jaeger/Tempo   Prometheus
```

Why collector exists:

```text
Central config
Batching
Sampling
Filtering
Retry
Export to many backends
```

Do not think:

```text
OpenTelemetry = only tracing UI
```

Think:

```text
OpenTelemetry = standard pipeline for telemetry signals
```

---

# 21. Sampling Mental Model

Tracing every request can be expensive.

Sampling means:

```text
Keep some traces, drop others
```

Example:

```text
100,000 requests/minute
Sample 10%
Store 10,000 traces/minute
```

ASCII:

```text
Requests
  | | | | | | | | | |
  v v v v v v v v v v
Sampler
  |   |       |   |
  v   v       v   v
Stored Traces
```

Sampling types:

```text
Head sampling:
  decide at request start

Tail sampling:
  decide after seeing full trace
```

Tail sampling is powerful because it can keep interesting traces:

```text
errors
slow requests
specific services
rare paths
```

Mental model:

```text
Metrics should be complete enough for alerting.
Traces can be sampled for investigation.
Logs can be filtered or retained by importance.
```

Never rely only on sampled traces for exact request count.

Use metrics for counts.

---

# 22. Correlation ID vs Trace ID

A correlation ID is often a business/debug ID passed through services.

A trace ID is part of distributed tracing.

They may be the same, but they are conceptually different.

```text
Correlation ID:
  used to connect logs across systems

Trace ID:
  used by tracing system to connect spans
```

ASCII:

```text
Request
  |
  +--> correlationId = request-777
  +--> traceId       = abc123
```

Log line:

```text
level=INFO service=order-service traceId=abc123 correlationId=request-777 orderId=ORD-91
```

Production rule:

```text
Every log from one request should carry a common identifier.
```

Why?

```text
Search traceId in logs
Open trace from log
Move from metric alert to trace
Move from trace to exact log event
```

Connected observability:

```text
Metric alert
   |
   v
Trace sample
   |
   v
traceId
   |
   v
Logs for exact request
```

---

# 23. Spring Boot Trace/Log Correlation

In Spring Boot, MDC can attach IDs to logs.

Filter example:

```java
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Correlation-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

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

Log pattern:

```yaml
logging:
  pattern:
    level: "%5p [correlationId:%X{correlationId}]"
```

Now every log can include:

```text
correlationId: request-777
```

ASCII:

```text
Incoming HTTP Request
        |
        v
Filter reads/generates correlationId
        |
        v
MDC stores ID for current thread
        |
        v
Logger prints ID automatically
        |
        v
Response returns ID to client
```

Caution with async code:

```text
MDC is thread-local.
Async execution may need context propagation.
```

---

# 24. Kubernetes Events Are Not Logs

Kubernetes events are cluster-level records.

Example:

```text
Scheduled pod to node-1
Pulling image
Failed to pull image
Back-off restarting failed container
Readiness probe failed
```

Command:

```bash
kubectl describe pod order-service-abc
kubectl get events --sort-by=.metadata.creationTimestamp
```

Mental model:

```text
Application logs = what app says
Kubernetes events = what cluster says happened to object
```

ASCII:

```text
Pod Not Ready
    |
    +--> App logs:
    |       DB connection refused
    |
    +--> K8s events:
            Readiness probe failed
```

Both are needed.

Example:

```text
Symptom:
  Pod stuck in ImagePullBackOff

Application logs:
  none, app never started

Kubernetes events:
  Failed to pull image, unauthorized
```

Do not debug every issue with app logs.

Sometimes the application never reached execution.

---

# 25. Observability Pipeline In Kubernetes

Typical production setup:

```text
Pods emit logs/metrics/traces
        |
        v
Node collectors and sidecars collect
        |
        v
Central backends store and index
        |
        v
Dashboards/alerts/search/trace UI
```

ASCII:

```text
+------------------- Kubernetes Cluster -------------------+
|                                                          |
|  Node-1                       Node-2                     |
|  +-----------+                +-----------+              |
|  | Pod A     |                | Pod C     |              |
|  | stdout    |                | stdout    |              |
|  +-----------+                +-----------+              |
|       |                             |                    |
|  +-----------+                +-----------+              |
|  | Collector |                | Collector |              |
|  +-----+-----+                +-----+-----+              |
|        \                         /                       |
+---------\-----------------------/------------------------+
           v                     v
       +--------------------------------+
       | Observability Backend          |
       | Logs + Metrics + Traces        |
       +--------------------------------+
```

Common deployment styles:

```text
DaemonSet collector:
  one collector per node

Sidecar collector:
  one collector beside app container

Central collector:
  cluster service receiving telemetry
```

For logs, DaemonSet is common.

For traces, SDK/agent to collector is common.

For metrics, Prometheus scraping is common.

---

# 26. Dashboards: Do Not Build Wall Of Charts

A dashboard should answer questions.

Bad dashboard:

```text
CPU chart
Memory chart
Thread chart
GC chart
Network chart
Disk chart
100 panels
No clear action
```

Good dashboard:

```text
Is service healthy?
Are users impacted?
Which dependency is slow?
Are pods overloaded?
Did this start after deploy?
```

Recommended layout:

```text
Top row:
  request rate, error rate, p95/p99 latency, saturation

Middle row:
  dependency latency and errors

Bottom row:
  pod restarts, CPU, memory, DB pool, JVM GC
```

ASCII:

```text
+--------------------------------------------------+
| Service Overview: order-service                  |
+----------+----------+----------+----------------+
| RPS      | Error %  | P99      | Saturation     |
+----------+----------+----------+----------------+
| Dependency Latency / Error Rates                 |
+--------------------------------------------------+
| Pods / Restarts / CPU / Memory / JVM / DB Pool    |
+--------------------------------------------------+
```

Dashboard rule:

```text
First user pain, then internal cause.
```

---

# 27. Alerting Mental Model

Alerts are not notifications.

Alerts are calls to action.

Bad alerts:

```text
CPU > 80% for 1 minute
Pod restarted once
Memory increased
Disk usage changed
```

Better alerts:

```text
5xx error rate > 2% for 10 minutes
P99 latency > SLO for 15 minutes
Checkout success rate dropped below threshold
Kafka consumer lag growing for 20 minutes
No successful payments in 5 minutes
```

ASCII:

```text
Metric crosses threshold
        |
        v
Alert rule evaluates
        |
        v
Is user impact likely?
        |
        +--> No  -> dashboard only
        |
        +--> Yes -> page human
```

Production alert question:

```text
Can the person receiving this alert do something useful now?
```

If no, it is noise.

Noisy alerts create alert fatigue.

Alert fatigue creates ignored incidents.

---

# 28. SLI, SLO, SLA

Do not memorize these as definitions only.

Map them to promises.

```text
SLI = measurement
SLO = internal target
SLA = external contract
```

Example:

```text
SLI:
  99.9% of checkout requests succeed under 500 ms

SLO:
  Maintain this for 30 days

SLA:
  Contract says customer gets credit if availability drops below 99.5%
```

ASCII:

```text
Reality measured  -> SLI
Target desired    -> SLO
Legal promise     -> SLA
```

Production example:

```text
SLI:
  successful HTTP requests / total HTTP requests

SLO:
  99.95% success over rolling 30 days

SLA:
  99.9% monthly availability promised to customer
```

Why SLOs matter:

```text
They connect engineering work to user experience.
They prevent alerting on meaningless noise.
They guide reliability decisions.
```

---

# 29. Error Budget Mental Model

If your SLO is 99.9%, your allowed failure is 0.1%.

That allowed failure is the error budget.

```text
SLO: 99.9% success
Error budget: 0.1% failure
```

If you have 1,000,000 requests:

```text
Allowed failures = 1,000
```

ASCII:

```text
Total Requests
+--------------------------------------------------+
| Successful requests                              |
|                                                  |
|                                                  |
| small allowed failure slice                      |
+--------------------------------------------------+
```

Mental model:

```text
Error budget is reliability spending money.
```

If budget is healthy:

```text
Ship features faster
Take controlled risk
```

If budget is burned:

```text
Freeze risky changes
Fix reliability
Improve tests/rollouts
```

This connects observability to engineering decisions.

---

# 30. Production Story: Pod Running But Users See 503

Symptom:

```text
Users report 503 from checkout.
kubectl get pods shows Running.
```

Bad reaction:

```text
Restart pods randomly.
```

Observability flow:

```text
1. Metrics:
   checkout 5xx rate increased from 0.1% to 8%.

2. Dashboard:
   order-service latency normal.
   payment-service error rate high.

3. Trace:
   checkout request spends 2 seconds in payment-service.

4. Logs:
   payment-service logs show connection pool exhausted.

5. Kubernetes metrics:
   payment-service pods CPU okay.
   DB pool usage 100%.
```

Root cause:

```text
Database slow query caused connection pool exhaustion.
```

ASCII:

```text
503 alert
  |
  v
Metrics: payment errors high
  |
  v
Trace: payment-service slow
  |
  v
Logs: DB connection pool timeout
  |
  v
Fix DB query / pool / timeout
```

Lesson:

```text
Running pods do not mean healthy service.
```

---

# 31. Production Story: CrashLoopBackOff After Deploy

Symptom:

```text
order-service new version deployed
Pods enter CrashLoopBackOff
```

Observability flow:

```bash
kubectl rollout status deployment/order-service
kubectl get pods
kubectl describe pod order-service-abc
kubectl logs order-service-abc --previous
```

Kubernetes events:

```text
Back-off restarting failed container
```

Previous logs:

```text
Failed to bind properties under 'payment.timeout-ms'
```

Root cause:

```text
New required config missing from ConfigMap.
```

ASCII:

```text
Deployment update
      |
      v
New Pod starts
      |
      v
Spring Boot fails config binding
      |
      v
Container exits
      |
      v
Kubelet restarts with backoff
```

Fix:

```text
Add missing ConfigMap key
or rollback deployment
```

Lesson:

```text
Use events for lifecycle failure.
Use previous logs for crashed container reason.
```

---

# 32. Production Story: Latency Spike After Autoscaling

Symptom:

```text
HPA scaled order-service from 3 to 12 pods.
Latency increased instead of decreasing.
```

Possible reason:

```text
More pods created more database connections.
Database saturated.
```

Observability path:

```text
Metrics:
  request rate high
  pods increased
  DB connection count increased
  DB CPU high
  p99 latency high

Traces:
  most time spent waiting on DB

Logs:
  connection timeout errors
```

ASCII:

```text
More Pods
   |
   v
More DB connections
   |
   v
DB saturation
   |
   v
Slower queries
   |
   v
Higher latency
```

Lesson:

```text
Scaling app pods can move bottleneck downstream.
```

Dashboard must show dependencies, not only app pods.

---

# 33. Production Story: Kafka Consumer Lag

Symptom:

```text
Order events are delayed.
Users receive confirmation emails late.
```

Metrics:

```text
consumer lag increasing
consumer processing rate lower than producer rate
consumer pod CPU normal
error count increasing
```

Logs:

```text
email provider timeout
retrying message
```

Trace or span around message processing:

```text
consume event
  validate payload
  call email provider
  provider timeout
  retry
```

ASCII:

```text
Producer rate: 500 msg/s
Consumer rate: 300 msg/s

Kafka Topic
+-----------------------------------+
| backlog grows                     |
+-----------------------------------+
              |
              v
        Consumer Lag Alert
```

Root cause might be:

```text
slow downstream provider
poison message
too few partitions
consumer retry storm
DB bottleneck
```

Lesson:

```text
Queue systems need lag metrics and processing traces.
```

---

# 34. Debugging Mindset: Metrics First, Logs Later

When production is on fire, start broad.

Bad:

```text
Open logs first
Search random errors
Get overwhelmed
```

Good:

```text
1. What changed?
2. Which service is affected?
3. Is error rate or latency high?
4. Is it all pods or one pod?
5. Is dependency slow?
6. Use trace to localize.
7. Use logs to explain exact event.
```

ASCII:

```text
Incident
   |
   v
Metrics: scope and severity
   |
   v
Traces: request path and bottleneck
   |
   v
Logs: exact error and context
   |
   v
Events: Kubernetes lifecycle issues
```

Commands:

```bash
kubectl get deploy,pods,svc
kubectl describe pod <pod>
kubectl logs <pod> --previous
kubectl top pods
kubectl get events --sort-by=.metadata.creationTimestamp
```

Prometheus/Grafana:

```text
RPS
5xx rate
p95/p99 latency
pod restarts
CPU/memory
DB pool usage
Kafka lag
```

---

# 35. Observability For Java/Spring Boot Services

Spring Boot observability should cover:

```text
HTTP server metrics
HTTP client metrics
JVM memory
GC pauses
thread pools
DB connection pool
cache metrics
business counters
logs with correlation ID
traces across service calls
```

ASCII:

```text
Spring Boot Process
+------------------------------------------------+
| REST Controller                                |
| Service Layer                                  |
| HTTP Client / Kafka / DB                       |
|                                                |
| Emits:                                         |
|  logs                                          |
|  metrics via Micrometer                        |
|  traces via OpenTelemetry                      |
+------------------------------------------------+
```

Important Java metrics:

```text
jvm_memory_used_bytes
jvm_gc_pause_seconds
http_server_requests_seconds
hikaricp_connections_active
hikaricp_connections_pending
executor_active_threads
executor_queued_tasks
```

Production interpretation:

```text
High GC pause       -> memory pressure / allocation issue
Pending DB connects -> pool exhaustion or DB slow
Thread queue grows  -> downstream latency or insufficient workers
P99 high, CPU low   -> waiting on IO
P99 high, CPU high  -> compute saturation
```

---

# 36. Kubernetes Metrics To Know

Cluster-level observability:

```text
Pod restarts
Pod readiness
CPU usage
memory usage
network traffic
node pressure
container OOM kills
HPA desired/current replicas
```

Important views:

```bash
kubectl top nodes
kubectl top pods
kubectl describe node <node>
kubectl get hpa
kubectl describe hpa <name>
```

Mental model:

```text
Application metrics tell user pain.
Kubernetes metrics tell platform condition.
```

ASCII:

```text
User latency high
      |
      +--> App metrics: endpoint slow?
      |
      +--> Dependency metrics: DB/Kafka slow?
      |
      +--> K8s metrics: pods overloaded/restarting?
      |
      +--> Node metrics: CPU/memory/disk pressure?
```

Do not stop at Kubernetes metrics.

A pod can have normal CPU and still be slow because DB is slow.

A pod can have low memory and still fail because config is wrong.

---

# 37. Common Anti-Patterns

```text
Anti-pattern 1:
Logging full request bodies.
Problem:
PII leakage, high cost, security risk.

Anti-pattern 2:
Using userId/orderId as metric labels.
Problem:
Cardinality explosion.

Anti-pattern 3:
Alerting on every pod restart.
Problem:
Noise, alert fatigue.

Anti-pattern 4:
No correlation ID.
Problem:
Cannot connect logs across services.

Anti-pattern 5:
Dashboards only show CPU/memory.
Problem:
No user impact visibility.

Anti-pattern 6:
No dependency metrics.
Problem:
You blame app when DB/provider is slow.

Anti-pattern 7:
No central logs.
Problem:
Pod deleted, evidence lost.

Anti-pattern 8:
No trace sampling strategy.
Problem:
Cost explosion or missing important traces.
```

Correct mindset:

```text
Every signal should help answer a production question.
```

---

# 38. Observability Design Checklist For A Service

For each Spring Boot microservice, ask:

```text
Logs:
[ ] Do logs include service name?
[ ] Do logs include traceId/correlationId?
[ ] Are errors logged with enough context?
[ ] Are secrets/tokens excluded?
[ ] Are logs structured?

Metrics:
[ ] Request rate measured?
[ ] Error rate measured?
[ ] p95/p99 latency measured?
[ ] DB pool measured?
[ ] JVM memory and GC measured?
[ ] Business counters measured?
[ ] Labels are low-cardinality?

Traces:
[ ] Incoming requests traced?
[ ] Outgoing HTTP calls traced?
[ ] DB/Kafka operations traced where useful?
[ ] Context propagated across services?
[ ] Sampling configured?

Kubernetes:
[ ] Pod restarts monitored?
[ ] Readiness failures visible?
[ ] HPA behavior visible?
[ ] Node pressure visible?
[ ] Events checked during incidents?
```

---

# 39. Interview Questions

## What is observability?

Observability is the ability to understand a system's internal behavior from external signals such as logs, metrics, and traces. In Kubernetes, it is critical because Pods are temporary, requests cross multiple services, and failures can happen in application code, dependencies, containers, nodes, or networking.

## Difference between logs, metrics, and traces?

Logs are event records that explain what happened at a point in time. Metrics are numeric time-series values used for trends, dashboards, and alerts. Traces show the journey of a single request across services and help identify where time was spent or where failure occurred.

## Why are metrics usually used for alerting?

Metrics are compact, numeric, and easy to aggregate over time. They are better for detecting conditions like high error rate, high latency, or growing queue lag. Logs are richer but noisier and more expensive for broad alerting.

## What is a trace span?

A span is one operation within a distributed trace, such as an HTTP handler, database query, Kafka publish, or downstream service call. A trace is composed of multiple spans connected by the same trace context.

## Why is correlation ID important?

A correlation ID connects logs across multiple services for the same request or business operation. Without it, debugging distributed systems becomes difficult because each service has isolated log lines.

## What is metric cardinality?

Metric cardinality is the number of unique time series created by metric labels. High-cardinality labels like userId or orderId can create millions of series, increasing memory, cost, and query latency. Individual identifiers belong in logs or traces, not metric labels.

## How do you debug high latency in Kubernetes?

Start with metrics to determine affected service and severity. Check RED metrics: rate, errors, and duration. Use traces to find where the slow request spends time. Use logs to find exact errors. Check Kubernetes events and pod/node metrics for restarts, readiness failures, or resource pressure.

## Why is `kubectl logs --previous` useful?

It shows logs from the previous container instance after a container restarted. This is essential for debugging CrashLoopBackOff, where the current container may not contain the original crash reason.

## What is the role of Prometheus?

Prometheus scrapes metrics endpoints, stores time-series data, supports PromQL queries, and integrates with Grafana and Alertmanager for dashboards and alerting.

## What is OpenTelemetry?

OpenTelemetry is a vendor-neutral standard and toolkit for collecting telemetry such as traces, metrics, and logs. It helps applications emit signals and send them through collectors to different observability backends.

---

# 40. Cheat Sheet

```text
Observability = explain system behavior from external signals

Logs:
  event story
  exact failure reason
  searchable context
  best for debugging specific events

Metrics:
  numeric time series
  rate/errors/latency/saturation
  best for dashboards and alerts

Traces:
  request journey
  spans across services
  best for finding bottlenecks

Golden Signals:
  Traffic
  Errors
  Latency
  Saturation

RED:
  Rate
  Errors
  Duration

USE:
  Utilization
  Saturation
  Errors

Kubernetes commands:
  kubectl logs <pod>
  kubectl logs <pod> --previous
  kubectl describe pod <pod>
  kubectl get events --sort-by=.metadata.creationTimestamp
  kubectl top pods
  kubectl top nodes
  kubectl get hpa

Spring Boot:
  Actuator exposes health/metrics/prometheus
  Micrometer records metrics
  MDC can attach correlationId
  OpenTelemetry can emit traces

Do not:
  log secrets
  use orderId/userId as metric labels
  alert on noisy non-actionable events
  rely only on CPU/memory dashboards
  store logs only inside Pods
```

---

# 41. One Picture To Remember

```text
                         USER PAIN
                            |
                            v
                    +----------------+
                    | What is wrong? |
                    +-------+--------+
                            |
          +-----------------+-----------------+
          |                 |                 |
          v                 v                 v
      +--------+        +---------+        +--------+
      | Metrics|        | Traces  |        | Logs   |
      +---+----+        +----+----+        +---+----+
          |                  |                 |
          v                  v                 v
  How many? How bad?   Where is time?   What exactly happened?
          |                  |                 |
          +------------------+-----------------+
                             |
                             v
                    +----------------+
                    | Root Cause     |
                    +-------+--------+
                            |
                            v
                    +----------------+
                    | Fix + Verify   |
                    +----------------+
```

Final memory hook:

```text
Metrics detect the fire.
Traces show the room.
Logs show the spark.
Kubernetes events show what the platform did.
```

Do not memorize observability as tools.

Remember it as production reasoning:

```text
Question -> Signal -> Evidence -> Root Cause -> Fix
```

---

# 42. Final Production Checklist

```text
[ ] I understand logs as event stories.
[ ] I understand metrics as numeric scoreboards.
[ ] I understand traces as request journeys.
[ ] I can use metrics to detect blast radius.
[ ] I can use traces to locate bottlenecks.
[ ] I can use logs to explain exact failure.
[ ] I know Kubernetes events are different from app logs.
[ ] I know why Pods need central log collection.
[ ] I know why correlation IDs matter.
[ ] I know why high-cardinality metrics are dangerous.
[ ] I know RED and USE methods.
[ ] I know golden signals.
[ ] I can configure Spring Boot Actuator metrics.
[ ] I can add safe business metrics using Micrometer.
[ ] I can add correlation ID to Spring Boot logs.
[ ] I can debug CrashLoopBackOff using --previous logs.
[ ] I can debug latency using metrics -> traces -> logs.
[ ] I can design dashboards around user pain first.
[ ] I can create alerts that require action, not noise.
```

Final sentence:

```text
Observability is not collecting more data.
Observability is reducing the time between user pain and root cause.
```
