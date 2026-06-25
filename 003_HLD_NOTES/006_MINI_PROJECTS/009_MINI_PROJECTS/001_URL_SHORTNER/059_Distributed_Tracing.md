# 059_Distributed_Tracing.md
# MiniURLShortener — Distributed Tracing

> Core mental model: **Distributed tracing is the request’s travel diary across services. Metrics tell you something is slow. Logs tell you what happened inside one service. Traces show the full path of one request across many services and where time was spent.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Metrics vs Logs vs Traces](#3-metrics-vs-logs-vs-traces)
- [4. Trace, Span, Parent Span, Child Span](#4-trace-span-parent-span-child-span)
- [5. Distributed Trace Request Flow](#5-distributed-trace-request-flow)
- [6. Trace Context Propagation](#6-trace-context-propagation)
- [7. OpenTelemetry Mental Model](#7-opentelemetry-mental-model)
- [8. Spring Boot Tracing Setup](#8-spring-boot-tracing-setup)
- [9. Jaeger / Tempo / Zipkin Mental Model](#9-jaeger--tempo--zipkin-mental-model)
- [10. MiniURLShortener Trace Design](#10-miniurlshortener-trace-design)
- [11. Create Short URL Trace](#11-create-short-url-trace)
- [12. Redirect API Trace](#12-redirect-api-trace)
- [13. Kafka Async Trace Propagation](#13-kafka-async-trace-propagation)
- [14. Database and Redis Spans](#14-database-and-redis-spans)
- [15. Sampling Strategy](#15-sampling-strategy)
- [16. Trace IDs in Logs](#16-trace-ids-in-logs)
- [17. Error Traces](#17-error-traces)
- [18. Performance Debugging With Traces](#18-performance-debugging-with-traces)
- [19. Production Dashboard Links](#19-production-dashboard-links)
- [20. Step-by-Step Dry Runs](#20-step-by-step-dry-runs)
- [21. Internal Execution Walkthrough](#21-internal-execution-walkthrough)
- [22. Testing Tracing Locally](#22-testing-tracing-locally)
- [23. Production Failure Stories](#23-production-failure-stories)
- [24. Debugging Mindset](#24-debugging-mindset)
- [25. Common Mistakes](#25-common-mistakes)
- [26. Interview-Ready Explanation](#26-interview-ready-explanation)
- [27. Senior Engineer Checklist](#27-senior-engineer-checklist)
- [28. One-Page Cheat Sheet](#28-one-page-cheat-sheet)
- [29. One Picture To Remember](#29-one-picture-to-remember)

---

## 1. Why This Exists

In earlier observability chapters:

```text
057_Prometheus_Metrics.md
    answered:
        Is the system healthy?
        What is p95/p99 latency?
        How many requests per second?
        How many DB errors?

058_Grafana_Dashboards.md
    answered:
        Can we visualize system health?
        Can we see RED/USE panels?
        Can we see latency, traffic, errors, saturation?
```

But metrics and dashboards still do not answer one painful production question:

```text
For this one slow request, where exactly did the time go?
```

Example MiniURLShortener request:

```text
POST /api/v1/urls
```

It may internally do:

```text
1. Validate URL
2. Check alias
3. Generate short code
4. Write Postgres row
5. Publish Kafka click/creation event
6. Return response
```

If p99 latency jumps from 80ms to 900ms, metrics say:

```text
create_url_latency_p99 is high
```

Logs may say:

```text
request started
DB insert completed
Kafka publish completed
request completed
```

But tracing shows:

```text
POST /api/v1/urls                    920ms
├── validation                         4ms
├── generateShortCode                  1ms
├── postgres INSERT                  820ms   <-- problem
└── kafka publish                     70ms
```

That is the purpose of distributed tracing.

Production memory:

```text
Metrics show the smoke.
Logs show local details.
Traces show the path of the fire.
```

---

## 2. The One Core Mental Model

Distributed tracing is:

```text
REQUEST JOURNEY TIMELINE
```

A single request gets a unique `traceId`.

Every operation inside that request becomes a `span`.

ASCII:

```text
TraceId = abc123

Client Request
    |
    v
+------------------------------+
| Span 1: API Gateway          |
| 15ms                         |
+------------------------------+
    |
    v
+------------------------------+
| Span 2: URL Service          |
| 120ms                        |
+------------------------------+
    |
    +----> Span 3: Redis GET   | 8ms
    |
    +----> Span 4: Postgres    | 90ms
    |
    +----> Span 5: Kafka SEND  | 12ms
```

The trace answers:

```text
What services were involved?
Which operation was slow?
Which dependency failed?
Which service started the request?
Which service called which service?
What error happened?
What trace ID links logs together?
```

One-line memory:

```text
A trace is the full story; spans are the chapters.
```

---

## 3. Metrics vs Logs vs Traces

Observability has three common signals.

```text
Metrics
Logs
Traces
```

### Metrics

Metrics are aggregated numbers.

Examples:

```text
http_server_requests_seconds_count
http_server_requests_seconds_bucket
jvm_memory_used_bytes
hikaricp_connections_active
redis_commands_duration_seconds
```

They answer:

```text
How many?
How fast?
How often?
How saturated?
```

### Logs

Logs are timestamped events.

Examples:

```text
INFO create short URL started
ERROR duplicate alias
WARN DB timeout
```

They answer:

```text
What happened inside this service?
What exception occurred?
What business decision was made?
```

### Traces

Traces are request timelines.

Examples:

```text
Trace: POST /api/v1/urls
Span: Controller
Span: Service
Span: Postgres insert
Span: Kafka send
```

They answer:

```text
Where did this request go?
Where did it spend time?
Which downstream dependency caused latency?
```

ASCII comparison:

```text
+-----------+-------------------------+------------------------------+
| Signal    | Best For                | Weakness                     |
+-----------+-------------------------+------------------------------+
| Metrics   | Trends and alerts       | Not request-specific         |
| Logs      | Detailed events         | Hard to reconstruct journey  |
| Traces    | Request path and timing | Expensive if sampled badly   |
+-----------+-------------------------+------------------------------+
```

Production rule:

```text
Do not choose metrics OR logs OR traces.
Use all three together.
```

---

## 4. Trace, Span, Parent Span, Child Span

### Trace

A trace represents one end-to-end operation.

Example:

```text
User clicks a short URL.
Trace = complete redirect request journey.
```

### Span

A span represents one timed operation inside a trace.

Example:

```text
HTTP GET /abc123
Redis GET short:abc123
Postgres SELECT short_urls
Kafka send click-event
```

### Parent span

The outer operation.

```text
GET /abc123
```

### Child span

An inner operation caused by the parent.

```text
Redis GET
Postgres SELECT
Kafka send
```

ASCII:

```text
TraceId: T-100

GET /abc123                         Parent span
|
+-- Redis GET short:abc123          Child span
|
+-- Postgres SELECT short_urls      Child span
|
+-- Kafka SEND click-events         Child span
```

Span fields usually include:

```text
traceId
spanId
parentSpanId
service.name
operation name
start time
duration
status
attributes/tags
events
error flag
```

Example span data:

```text
traceId: 7f3a...
spanId: a91b...
parentSpanId: 42c...
service.name: miniurl-shortener
operation: POST /api/v1/urls
duration: 132ms
status: OK
http.method: POST
http.route: /api/v1/urls
http.status_code: 201
```

---

## 5. Distributed Trace Request Flow

A distributed trace becomes powerful when requests cross service boundaries.

Example future MiniURLShortener architecture:

```text
Client
  |
  v
API Gateway
  |
  v
URL Service
  |
  +--> Redis
  |
  +--> Postgres
  |
  +--> Kafka
          |
          v
Analytics Worker
          |
          +--> Click DB
```

ASCII trace:

```text
TraceId = T1

Client
  |
  v
+-----------------------+
| API Gateway           |
| spanId=A              |
+-----------------------+
  |
  v traceparent header
+-----------------------+
| URL Service           |
| spanId=B parent=A     |
+-----------------------+
  |
  +--> Redis span C
  |
  +--> Postgres span D
  |
  +--> Kafka produce E
           |
           v trace headers in message
      +-----------------------+
      | Analytics Worker      |
      | spanId=F parent=E     |
      +-----------------------+
```

Without tracing:

```text
Gateway log has one ID.
URL service log has another ID.
Worker log has another ID.
You manually guess connection.
```

With tracing:

```text
Same traceId connects everything.
```

---

## 6. Trace Context Propagation

Tracing only works if context travels with the request.

Context usually includes:

```text
traceId
spanId
sampling decision
baggage values
```

For HTTP, context travels through headers.

Common W3C Trace Context header:

```http
traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01
```

Meaning:

```text
version
trace id
parent span id
sampled flag
```

Simplified:

```text
traceparent = "this request belongs to trace T, parent span is S"
```

ASCII:

```text
Client
  |
  | traceparent: T1-S1
  v
Gateway
  |
  | traceparent: T1-S2
  v
URL Service
  |
  | traceparent: T1-S3
  v
Downstream Service
```

For Kafka, context travels through message headers:

```text
Kafka record headers:
    traceparent = T1-S5
```

Important:

```text
If context propagation breaks, the trace becomes fragmented.
```

Fragmented trace:

```text
Trace A: gateway only
Trace B: url-service only
Trace C: worker only
```

Good trace:

```text
Trace T1:
    gateway -> url-service -> kafka -> worker
```

---

## 7. OpenTelemetry Mental Model

OpenTelemetry is the standard toolkit for collecting telemetry.

It can collect:

```text
traces
metrics
logs
```

For this chapter, focus on traces.

OpenTelemetry components:

```text
Instrumentation
SDK
Exporter
Collector
Backend
```

ASCII:

```text
Spring Boot App
   |
   v
+--------------------------+
| OpenTelemetry Agent/SDK  |
| creates spans            |
+--------------------------+
   |
   v
+--------------------------+
| Exporter                 |
| sends OTLP data          |
+--------------------------+
   |
   v
+--------------------------+
| OpenTelemetry Collector  |
| receives/processes       |
+--------------------------+
   |
   v
+--------------------------+
| Backend                  |
| Jaeger / Tempo / Zipkin  |
+--------------------------+
```

### Instrumentation

Creates spans automatically or manually.

Automatic spans:

```text
HTTP server requests
HTTP client calls
JDBC queries
Redis calls
Kafka producer/consumer
```

Manual spans:

```text
custom business operation
shortCode generation
alias validation
security check
```

### Exporter

Sends spans out of the app.

Common protocol:

```text
OTLP = OpenTelemetry Protocol
```

### Collector

Receives telemetry from many apps.

It can:

```text
batch spans
filter spans
add attributes
send to backend
```

### Backend

Stores and queries traces.

Examples:

```text
Jaeger
Grafana Tempo
Zipkin
```

Memory:

```text
OpenTelemetry creates and exports traces.
Jaeger/Tempo stores and visualizes traces.
```

---

## 8. Spring Boot Tracing Setup

Modern Spring Boot uses Micrometer Observation and tracing bridges.

Common production options:

```text
Option A: OpenTelemetry Java Agent
Option B: Micrometer Tracing with OTel bridge
Option C: Vendor-specific agent
```

For learning, the easiest mental model is:

```text
Use OpenTelemetry Java Agent for auto-instrumentation.
```

### Option A: Java agent startup

Download OpenTelemetry Java agent and run:

```bash
java \
  -javaagent:opentelemetry-javaagent.jar \
  -Dotel.service.name=miniurl-shortener \
  -Dotel.exporter.otlp.endpoint=http://otel-collector:4318 \
  -Dotel.traces.exporter=otlp \
  -jar app.jar
```

What happens:

```text
1. Agent attaches to JVM.
2. It instruments Spring MVC, JDBC, Redis, Kafka.
3. It creates spans automatically.
4. It exports spans to the collector.
```

### Docker Compose service idea

```yaml
services:
  miniurl:
    image: miniurl-shortener:latest
    environment:
      OTEL_SERVICE_NAME: miniurl-shortener
      OTEL_EXPORTER_OTLP_ENDPOINT: http://otel-collector:4318
      OTEL_TRACES_EXPORTER: otlp
      OTEL_METRICS_EXPORTER: none
      OTEL_LOGS_EXPORTER: none
    command:
      - java
      - -javaagent:/otel/opentelemetry-javaagent.jar
      - -jar
      - /app/app.jar
```

### Spring Boot properties idea

```yaml
management:
  tracing:
    sampling:
      probability: 1.0
  observations:
    key-values:
      application: miniurl-shortener
```

For local learning:

```text
sampling probability = 1.0
```

For production:

```text
sampling probability may be 0.01, 0.05, 0.10, or tail-based.
```

---

## 9. Jaeger / Tempo / Zipkin Mental Model

Tracing backend stores traces and lets you search them.

### Jaeger

Good for learning and local debugging.

```text
App -> OTel Collector -> Jaeger -> Jaeger UI
```

### Grafana Tempo

Good with Grafana ecosystem.

```text
App -> OTel Collector -> Tempo -> Grafana Explore
```

### Zipkin

Older, simple tracing backend.

```text
App -> Zipkin -> Zipkin UI
```

ASCII:

```text
+--------------------+       +----------------------+       +----------------+
| Spring Boot App    | ----> | OTel Collector       | ----> | Trace Backend  |
| creates spans      | OTLP  | batches/processes    |       | Jaeger/Tempo   |
+--------------------+       +----------------------+       +----------------+
                                                                     |
                                                                     v
                                                              Trace UI / Grafana
```

For your MiniURLShortener observability stack:

```text
Prometheus = metrics backend
Grafana    = dashboard and explore UI
Tempo      = traces backend
Loki       = logs backend later
```

One integrated picture:

```text
Metrics -> Prometheus -> Grafana dashboards
Traces  -> Tempo      -> Grafana Explore
Logs    -> Loki       -> Grafana Explore
```

---

## 10. MiniURLShortener Trace Design

Trace important paths:

```text
1. Create short URL
2. Redirect short URL
3. Kafka click analytics
4. Admin block URL
5. Health/actuator usually not traced or sampled less
```

### Create API spans

```text
POST /api/v1/urls
├── validate longUrl
├── validate customAlias
├── generate shortCode
├── Postgres INSERT short_urls
└── Kafka SEND url-created
```

### Redirect API spans

```text
GET /{shortCode}
├── validate shortCode
├── Redis GET cache
├── Postgres SELECT fallback
├── Redis SET cache
└── Kafka SEND click-event
```

### Analytics worker spans

```text
Kafka CONSUME click-event
├── validate event
├── enrich event
└── Postgres INSERT click_analytics
```

ASCII:

```text
                MiniURLShortener Trace Map

Create:
Client -> Gateway -> URL Service -> Postgres
                              \-> Kafka

Redirect:
Client -> Gateway -> URL Service -> Redis
                              \-> Postgres
                              \-> Kafka -> Analytics Worker -> Analytics DB
```

What to tag carefully:

```text
http.route
http.status_code
short_code_length
cache.result = hit/miss
db.operation
messaging.topic
error.code
```

What not to tag:

```text
full longUrl with secrets
access token
password
full user PII
large request body
```

---

## 11. Create Short URL Trace

Request:

```http
POST /api/v1/urls
Content-Type: application/json

{
  "longUrl": "https://example.com/article",
  "customAlias": "article1"
}
```

Trace:

```text
TraceId = T-CREATE-1

POST /api/v1/urls                              145ms
├── UrlController.createShortUrl                2ms
├── UrlValidator.validateLongUrl                1ms
├── UrlValidator.validateCustomAlias            1ms
├── ShortCodeService.chooseShortCode            1ms
├── Postgres INSERT short_urls                120ms
└── Kafka SEND url-created                     14ms
```

ASCII waterfall:

```text
0ms                                                          145ms
|-------------------------------------------------------------|
HTTP POST /api/v1/urls

  |-- validate URL --|
      |-- validate alias --|
          |-- generate --|
              |---------------- Postgres INSERT ----------------|
                                                       |-- Kafka --|
```

What this tells you:

```text
Postgres insert dominates latency.
Kafka publish is acceptable.
Business validation is negligible.
```

Good span attributes:

```text
http.method=POST
http.route=/api/v1/urls
http.status_code=201
service.name=miniurl-shortener
db.system=postgresql
db.operation=INSERT
messaging.system=kafka
messaging.destination=url-created
```

Avoid:

```text
longUrl=https://example.com/reset?token=secret
```

Better:

```text
long_url_domain=example.com
long_url_length=27
```

---

## 12. Redirect API Trace

Request:

```http
GET /abc123
```

Fast cache-hit trace:

```text
TraceId = T-REDIRECT-HIT

GET /{shortCode}                     18ms
├── validate shortCode                1ms
├── Redis GET short:abc123            4ms
└── Kafka SEND click-event            7ms
```

Slow cache-miss trace:

```text
TraceId = T-REDIRECT-MISS

GET /{shortCode}                     94ms
├── validate shortCode                1ms
├── Redis GET short:abc123            5ms
├── Postgres SELECT short_urls       62ms
├── Redis SET short:abc123            6ms
└── Kafka SEND click-event           10ms
```

ASCII:

```text
Cache Hit:
Client -> App -> Redis -> 302
               \
                -> Kafka click

Cache Miss:
Client -> App -> Redis miss -> Postgres -> Redis set -> 302
                                  \
                                   -> Kafka click
```

Trace attributes:

```text
short_code_length=6
cache.system=redis
cache.operation=GET
cache.result=hit
http.status_code=302
```

For not found:

```text
GET /unknown                       44ms
├── Redis GET                       4ms
├── Postgres SELECT                35ms
└── error: SHORT_CODE_NOT_FOUND
```

Status:

```text
Span status = ERROR
error.code = SHORT_CODE_NOT_FOUND
http.status_code = 404
```

---

## 13. Kafka Async Trace Propagation

Kafka makes tracing tricky because the request becomes asynchronous.

Redirect API:

```text
GET /abc123
```

App publishes:

```text
click-event
```

Worker consumes later.

Without propagation:

```text
Trace 1:
    GET /abc123 -> Kafka SEND

Trace 2:
    Kafka CONSUME -> DB insert
```

With propagation:

```text
Trace 1:
    GET /abc123
    ├── Kafka SEND click-event
    └── Kafka CONSUME click-event
        └── Postgres INSERT click_analytics
```

ASCII:

```text
URL Service
  |
  | produce click-event
  | headers:
  |   traceparent: T1-S4
  v
Kafka Topic
  |
  | consume message
  | continue trace T1
  v
Analytics Worker
```

Spring Kafka instrumentation can automatically propagate context.

Manual mental model:

```java
ProducerRecord<String, ClickEvent> record =
        new ProducerRecord<>("click-events", event.shortCode(), event);

record.headers().add("traceparent", currentTraceParent.getBytes(StandardCharsets.UTF_8));

kafkaTemplate.send(record);
```

Consumer side:

```java
@KafkaListener(topics = "click-events")
public void consume(ConsumerRecord<String, ClickEvent> record) {
    // tracing library extracts traceparent from headers
    // worker span becomes child/linked span
}
```

Important nuance:

```text
Async work may be represented as child spans or linked spans.
```

For interviews:

```text
For Kafka, I propagate W3C trace context through message headers. The consumer extracts it and continues or links the trace, so async processing remains visible.
```

---

## 14. Database and Redis Spans

Auto-instrumentation usually creates DB spans.

Example Postgres span:

```text
Span name:
    INSERT short_urls

Attributes:
    db.system=postgresql
    db.name=miniurl
    db.operation=INSERT
    db.statement=INSERT INTO short_urls ...
```

Security caution:

```text
Do not expose full SQL with sensitive bind values in production.
```

Redis span:

```text
Span name:
    GET short:abc123

Attributes:
    db.system=redis
    db.operation=GET
```

Better production tag:

```text
redis.key.prefix=short
```

Avoid high-cardinality tag:

```text
redis.key=short:abc123
```

Why?

```text
Every unique short code creates a unique label/cardinality.
This can increase storage and query cost.
```

Better:

```text
short_code_length=6
cache.key_type=short_url_redirect
cache.result=hit
```

Database trace debugging:

```text
If p99 redirect latency is high:
    traces can show whether Redis, Postgres, or Kafka caused it.
```

Example:

```text
GET /{shortCode}                     730ms
├── Redis GET                         3ms
├── Postgres SELECT                 690ms  <-- slow index / DB issue
└── Kafka SEND                       10ms
```

---

## 15. Sampling Strategy

Tracing every request can be expensive at high traffic.

MiniURLShortener redirect API may receive:

```text
100k RPS
```

If every request is traced:

```text
100k traces/sec = huge storage and cost
```

Sampling decides which traces to keep.

### Head-based sampling

Decision happens at request start.

Example:

```text
sample 1% of requests
```

Pros:

```text
cheap
simple
```

Cons:

```text
may miss rare errors
```

### Tail-based sampling

Decision happens after seeing the full trace.

Keep traces if:

```text
latency > 1s
status = ERROR
rare endpoint
random 1% baseline
```

Pros:

```text
keeps important traces
```

Cons:

```text
needs collector/backend support
more complex
```

ASCII:

```text
Head sampling:
Request starts -> choose keep/drop immediately

Tail sampling:
Request completes -> inspect latency/error -> choose keep/drop
```

Recommended production strategy:

```text
Always keep:
    error traces
    very slow traces
    critical endpoint traces

Random sample:
    small percentage of normal traffic
```

For local development:

```text
sample 100%
```

For production:

```text
sample intelligently
```

---

## 16. Trace IDs in Logs

Tracing becomes much stronger when logs contain trace IDs.

Log line:

```text
2026-06-25T10:00:00 INFO traceId=abc123 spanId=def456 create short URL completed
```

Then you can:

```text
1. Open slow trace in Grafana/Jaeger.
2. Copy traceId.
3. Search logs by traceId.
4. See all logs for that request.
```

ASCII:

```text
Trace UI
   |
   | traceId=abc123
   v
Logs
   |
   v
All service logs for same request
```

Spring Boot logging pattern idea:

```yaml
logging:
  pattern:
    level: "%5p [traceId=%X{traceId:-}, spanId=%X{spanId:-}]"
```

Example output:

```text
INFO [traceId=7f3a9, spanId=a91b2] Creating short URL
INFO [traceId=7f3a9, spanId=b11c8] Postgres insert completed
ERROR [traceId=7f3a9, spanId=c87d1] Kafka send failed
```

Golden rule:

```text
Every production log should be searchable by traceId.
```

---

## 17. Error Traces

Errors should be visible in traces.

Example duplicate alias:

```text
POST /api/v1/urls                   35ms ERROR
├── validate URL                     1ms OK
├── validate alias                   1ms OK
└── Postgres INSERT                 28ms ERROR
    error.code=ALIAS_ALREADY_EXISTS
    http.status_code=409
```

Example DB timeout:

```text
POST /api/v1/urls                 3000ms ERROR
└── Postgres INSERT               2995ms ERROR
    exception.type=QueryTimeoutException
    error=true
```

Example redirect expired:

```text
GET /{shortCode}                   12ms ERROR
├── Redis GET                       3ms OK
└── check expiry                    1ms ERROR
    error.code=SHORT_CODE_EXPIRED
    http.status_code=410
```

Important distinction:

```text
Business errors may not be system failures.
```

Example:

```text
404 short code not found
```

This may be a normal client outcome.

But from trace perspective:

```text
span status may show error because request did not succeed.
```

Dashboards should separate:

```text
4xx client/business errors
5xx server errors
```

---

## 18. Performance Debugging With Traces

Scenario:

```text
Grafana dashboard shows redirect p99 = 1.5s.
```

Metrics say:

```text
Something is slow.
```

Trace investigation:

```text
1. Open exemplars or traces for slow requests.
2. Sort traces by duration.
3. Pick one 1.5s trace.
4. Inspect waterfall.
```

Trace waterfall:

```text
GET /{shortCode}                  1500ms
├── Redis GET                        4ms
├── Postgres SELECT               1430ms
└── Kafka SEND                      20ms
```

Conclusion:

```text
Postgres is the bottleneck.
```

Next check:

```text
1. Is index missing?
2. Is connection pool saturated?
3. Is DB CPU high?
4. Is read replica lag high?
5. Is query doing sequential scan?
```

Another trace:

```text
GET /{shortCode}                  1200ms
├── Redis GET                     1100ms
└── Kafka SEND                      10ms
```

Conclusion:

```text
Redis latency/network issue.
```

Another trace:

```text
POST /api/v1/urls                 900ms
├── validation                       2ms
├── Postgres INSERT                 40ms
└── Kafka SEND                     820ms
```

Conclusion:

```text
Kafka broker or producer ack delay.
```

Tracing debugging formula:

```text
High latency alert
    -> open slow trace
    -> find largest span
    -> inspect dependency
    -> compare with metrics
    -> search logs by traceId
```

---

## 19. Production Dashboard Links

Grafana can connect dashboards and traces.

Example flow:

```text
Dashboard panel:
    p99 redirect latency is high

Click exemplar:
    open trace for slow request

Trace:
    shows Postgres SELECT slow

Click traceId:
    open logs for same request
```

ASCII:

```text
Grafana Metrics Panel
       |
       | exemplar / traceId
       v
Grafana Trace View
       |
       | traceId
       v
Grafana Logs View
```

This is the best observability workflow:

```text
Metric -> Trace -> Logs
```

Or:

```text
Alert -> Dashboard -> Trace -> Logs -> Root Cause
```

For MiniURLShortener:

```text
Panel:
    Redirect p99 latency

Trace link:
    slow GET /{shortCode}

Logs link:
    traceId logs across url-service and analytics-worker
```

Interview memory:

```text
I use metrics to detect the issue, traces to localize the slow dependency, and logs to explain the exact failure.
```

---

## 20. Step-by-Step Dry Runs

### Dry Run 1: Fast Redirect Cache Hit

Request:

```http
GET /abc123
```

Flow:

```text
1. User calls GET /abc123.
2. App receives request and starts root span.
3. TraceId T1 is created.
4. Redis GET span is created.
5. Redis returns long URL.
6. Kafka click event span is created.
7. App returns 302.
8. Trace is exported to collector.
9. Jaeger/Tempo shows full request duration.
```

Trace:

```text
GET /{shortCode}          18ms
├── Redis GET              4ms
└── Kafka SEND             7ms
```

---

### Dry Run 2: Slow Redirect Cache Miss

Request:

```http
GET /sale2026
```

Flow:

```text
1. Root span starts.
2. Redis GET returns miss.
3. Postgres SELECT span starts.
4. Postgres takes 400ms.
5. Redis SET span stores result.
6. Kafka SEND span emits click event.
7. Response returns 302.
8. Trace shows DB span as largest contributor.
```

Trace:

```text
GET /{shortCode}          450ms
├── Redis GET               5ms
├── Postgres SELECT       400ms
├── Redis SET               6ms
└── Kafka SEND             12ms
```

Learning:

```text
Metrics showed redirect latency.
Trace showed Postgres caused it.
```

---

### Dry Run 3: Duplicate Alias Error

Request:

```json
{
  "longUrl": "https://example.com",
  "customAlias": "admin2026"
}
```

Flow:

```text
1. Root span starts for POST /api/v1/urls.
2. Validation spans pass.
3. Postgres insert span starts.
4. Unique constraint violation occurs.
5. Service maps it to ALIAS_ALREADY_EXISTS.
6. Root span status becomes error.
7. Response returns 409.
8. Logs include same traceId.
```

Trace:

```text
POST /api/v1/urls          42ms ERROR
├── validate longUrl        1ms OK
├── validate alias          1ms OK
└── Postgres INSERT        30ms ERROR
```

---

### Dry Run 4: Kafka Worker Trace

Request:

```http
GET /abc123
```

Flow:

```text
1. URL service creates trace T1.
2. It sends click-event to Kafka with traceparent header.
3. Analytics worker consumes message.
4. Worker extracts trace context.
5. Worker creates consumer span linked to T1.
6. Worker inserts analytics row.
7. Trace shows async processing.
```

Trace:

```text
GET /{shortCode}                   22ms
└── Kafka SEND click-events         8ms
    └── Kafka CONSUME click-events 15ms
        └── Postgres INSERT        9ms
```

Learning:

```text
Async does not mean invisible.
Propagate trace context through message headers.
```

---

## 21. Internal Execution Walkthrough

Spring Boot tracing path:

```text
1. HTTP request enters embedded Tomcat.
2. OpenTelemetry/Spring observation starts server span.
3. Controller method runs.
4. Service method may create custom spans.
5. JDBC instrumentation creates DB spans.
6. Redis instrumentation creates Redis spans.
7. Kafka instrumentation creates producer span and injects context into headers.
8. Response completes.
9. Span durations and status are finalized.
10. Exporter sends spans to OpenTelemetry Collector.
11. Collector sends spans to Jaeger/Tempo.
12. Engineer opens trace in UI.
```

ASCII:

```text
HTTP Request
   |
   v
+----------------------------+
| Server Span                |
| POST /api/v1/urls          |
+----------------------------+
   |
   +--> Controller code
   |
   +--> Service custom span
   |
   +--> JDBC auto span
   |
   +--> Kafka producer span
   |
   v
Exporter -> OTel Collector -> Tempo/Jaeger -> UI
```

Important:

```text
Auto-instrumentation gives broad coverage.
Manual instrumentation gives business meaning.
```

Auto spans show:

```text
HTTP
JDBC
Redis
Kafka
```

Manual spans show:

```text
validate alias
generate short code
cache decision
redirect lifecycle check
```

---

## 22. Testing Tracing Locally

Local observability stack:

```text
Spring Boot App
Postgres
Redis
Kafka
OpenTelemetry Collector
Jaeger or Tempo
Grafana
```

Minimum local Jaeger docker-compose idea:

```yaml
services:
  jaeger:
    image: jaegertracing/all-in-one:latest
    ports:
      - "16686:16686"
      - "4317:4317"
      - "4318:4318"
```

Run app with:

```bash
java \
  -javaagent:opentelemetry-javaagent.jar \
  -Dotel.service.name=miniurl-shortener \
  -Dotel.exporter.otlp.endpoint=http://localhost:4318 \
  -Dotel.traces.exporter=otlp \
  -jar target/miniurl-shortener.jar
```

Test:

```bash
curl -X POST http://localhost:8080/api/v1/urls \
  -H "Content-Type: application/json" \
  -d '{"longUrl":"https://example.com","customAlias":"abc123"}'

curl -v http://localhost:8080/abc123
```

Open:

```text
Jaeger UI:
http://localhost:16686
```

Search:

```text
Service: miniurl-shortener
Operation: POST /api/v1/urls
```

Expected:

```text
You should see server span, DB span, Redis/Kafka spans if used.
```

---

## 23. Production Failure Stories

### Failure Story 1: Metrics show high p99 but logs look normal

Problem:

```text
Redirect p99 jumps to 2s.
Logs only say request completed.
```

Trace shows:

```text
Postgres SELECT took 1.8s.
```

Root cause:

```text
Missing index on short_code after schema migration.
```

Fix:

```sql
CREATE INDEX idx_short_urls_short_code ON short_urls(short_code);
```

Lesson:

```text
Metrics detect the issue; traces locate the slow operation.
```

---

### Failure Story 2: Kafka worker invisible

Problem:

```text
Click analytics is delayed.
URL service trace ends at Kafka send.
Worker appears as separate trace.
```

Root cause:

```text
Trace context not propagated through Kafka headers.
```

Fix:

```text
Enable Kafka instrumentation or manually pass traceparent header.
```

Lesson:

```text
Async boundaries need explicit context propagation.
```

---

### Failure Story 3: Too many traces overload backend

Problem:

```text
At 50k RPS, tracing backend storage explodes.
```

Root cause:

```text
100% tracing enabled in production.
```

Fix:

```text
Use sampling.
Keep errors and slow traces.
Random sample normal traffic.
```

Lesson:

```text
Trace everything locally, sample intelligently in production.
```

---

### Failure Story 4: Trace tags leak sensitive URL

Problem:

```text
Trace contains full long URL:
https://site.com/reset?token=secret
```

Root cause:

```text
Developer added longUrl as span attribute.
```

Fix:

```text
Store safe attributes only:
domain, URL length, normalized category, not full query parameters.
```

Lesson:

```text
Traces are production data. Treat them like logs.
```

---

### Failure Story 5: Fragmented traces after gateway

Problem:

```text
Gateway has one trace.
Backend service has another trace.
```

Root cause:

```text
Gateway removed traceparent header or backend did not extract it.
```

Fix:

```text
Preserve W3C trace context headers across gateway/proxy.
```

Lesson:

```text
A distributed trace is only as strong as its propagation.
```

---

## 24. Debugging Mindset

When debugging with traces, ask:

```text
Which trace is slow?
Which span is largest?
Is it app code or dependency?
Is it Redis, Postgres, Kafka, HTTP client, or lock contention?
Did the trace cross all expected services?
Did context propagation break?
Is the error code visible?
Can I find logs using traceId?
Is sampling hiding important traces?
Are sensitive fields being captured?
```

Trace reading pattern:

```text
1. Look at total duration.
2. Find widest span.
3. Check span status.
4. Check attributes.
5. Compare with metrics.
6. Search logs by traceId.
7. Confirm root cause.
```

ASCII decision:

```text
Slow request
  |
  v
Largest span?
  |
  +-- Postgres --> check query/index/pool/locks
  |
  +-- Redis ----> check latency/network/cluster
  |
  +-- Kafka ----> check broker/acks/producer buffer
  |
  +-- App code -> check CPU/locks/algorithm
  |
  +-- External -> check downstream SLA/timeouts
```

Golden rule:

```text
Do not guess from symptoms. Follow the trace.
```

---

## 25. Common Mistakes

### Mistake 1: Using traces instead of metrics

Wrong:

```text
Only tracing, no Prometheus alerts.
```

Correct:

```text
Metrics alert. Traces investigate.
```

### Mistake 2: No traceId in logs

Wrong:

```text
Trace exists but logs cannot be correlated.
```

Correct:

```text
Add traceId and spanId to every log line.
```

### Mistake 3: Tracing full request bodies

Wrong:

```text
Span attribute: request.body = full JSON
```

Correct:

```text
Capture safe metadata only.
```

### Mistake 4: 100% sampling in production high traffic

Wrong:

```text
Trace every redirect at 100k RPS.
```

Correct:

```text
Use head/tail sampling.
Always keep slow/error traces.
```

### Mistake 5: Broken Kafka propagation

Wrong:

```text
Consumer trace starts fresh.
```

Correct:

```text
Propagate traceparent through Kafka headers.
```

### Mistake 6: High-cardinality span attributes

Wrong:

```text
shortCode=abc123
userId=999999
redis.key=short:abc123
```

Correct:

```text
short_code_length=6
cache.key_type=short_url
cache.result=hit
```

### Mistake 7: Over-instrumenting tiny methods

Wrong:

```text
Span for every getter/setter.
```

Correct:

```text
Span meaningful boundaries and dependencies.
```

### Mistake 8: Ignoring failed traces

Wrong:

```text
Only check successful slow traces.
```

Correct:

```text
Inspect errors, retries, timeouts, cancellations.
```

---

## 26. Interview-Ready Explanation

If interviewer asks:

```text
How would you implement distributed tracing in your URL shortener?
```

Strong answer:

```text
I would instrument the service using OpenTelemetry, preferably with the Java agent
for automatic HTTP, JDBC, Redis, and Kafka spans, and add small manual spans only
around important business operations like short code generation and redirect
decision logic. Each incoming request gets a traceId, and every internal operation
becomes a span. For HTTP calls, I propagate W3C trace context headers. For Kafka,
I propagate trace context through message headers so async analytics workers remain
connected to the original redirect request. I export spans through the OpenTelemetry
Collector to a backend like Jaeger or Grafana Tempo. In Grafana, metrics alert me
that p99 latency or errors increased, traces show which dependency caused it, and
logs are correlated using traceId and spanId. In production, I avoid putting sensitive
URLs or high-cardinality values in spans and use sampling, especially for high-volume
redirect traffic, while keeping slow and error traces.
```

Why this is strong:

```text
1. Mentions OpenTelemetry.
2. Explains trace/span mental model.
3. Covers HTTP and Kafka propagation.
4. Connects metrics, traces, and logs.
5. Mentions Jaeger/Tempo.
6. Shows production cost awareness through sampling.
7. Shows security awareness.
8. Shows debugging workflow.
```

Senior one-liner:

```text
Distributed tracing turns one request into a timeline, so I can see exactly which service or dependency caused latency or failure.
```

---

## 27. Senior Engineer Checklist

Before saying tracing is production-ready, confirm:

```text
[ ] Service name is configured correctly
[ ] HTTP server spans are created
[ ] HTTP client spans are created if used
[ ] JDBC spans are visible
[ ] Redis spans are visible
[ ] Kafka producer spans are visible
[ ] Kafka consumer spans are visible
[ ] Trace context propagates through HTTP headers
[ ] Trace context propagates through Kafka headers
[ ] Logs include traceId and spanId
[ ] Traces are exported to OTel Collector
[ ] Collector exports to Jaeger/Tempo
[ ] Grafana can search traces
[ ] Error traces are visible
[ ] Slow traces are sampled/kept
[ ] Production sampling is not 100% blindly
[ ] Sensitive data is not stored in span attributes
[ ] High-cardinality attributes are avoided
[ ] Dashboards link metrics to traces where possible
[ ] Runbook explains metric -> trace -> logs workflow
```

---

## 28. One-Page Cheat Sheet

```text
Core mental model:
Distributed tracing = request journey timeline.

Trace:
One end-to-end request.

Span:
One timed operation inside the trace.

Parent span:
Outer operation.

Child span:
Inner operation caused by parent.

Trace context:
traceId + spanId + sampled flag.

HTTP propagation:
traceparent header.

Kafka propagation:
traceparent message header.

OpenTelemetry:
Instrumentation -> SDK/Agent -> Exporter -> Collector -> Backend.

Backends:
Jaeger
Grafana Tempo
Zipkin

MiniURLShortener traces:
POST /api/v1/urls
GET /{shortCode}
Kafka click analytics worker

Useful spans:
HTTP server
Redis GET/SET
Postgres SELECT/INSERT
Kafka SEND/CONSUME
manual business spans

Debug workflow:
Metric alert
    -> open slow trace
    -> find largest span
    -> check dependency metrics
    -> search logs by traceId

Sampling:
100% locally
sample intelligently in production
always keep slow/error traces if possible

Do not capture:
full longUrl
tokens
passwords
PII
high-cardinality IDs
```

---

## 29. One Picture To Remember

```text
                   DISTRIBUTED TRACING MENTAL MODEL

                         "Request travel diary"

Client
  |
  | traceId=T1
  v
+---------------------------+
| API Gateway               |
| span A                    |
+---------------------------+
  |
  | traceparent: T1-A
  v
+---------------------------+
| URL Service               |
| span B                    |
+---------------------------+
  |
  +---- Redis GET ---------- span C
  |
  +---- Postgres SELECT ---- span D
  |
  +---- Kafka SEND --------- span E
                              |
                              | traceparent in Kafka header
                              v
                         +----------------------+
                         | Analytics Worker     |
                         | span F               |
                         +----------------------+
                              |
                              +-- Analytics DB -- span G


FINAL MEMORY:

Metrics tell you something is wrong.
Traces tell you where it went wrong.
Logs tell you why it went wrong.
TraceId connects all three.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. A trace is one request’s complete journey across services.
2. A span is one timed operation inside that journey.
3. Trace context must propagate through HTTP headers and Kafka message headers.
4. OpenTelemetry creates/export traces; Jaeger or Tempo stores and visualizes them.
5. In production, sample intelligently and never put sensitive/high-cardinality data in spans.
```

Next chapter:

```text
060_Centralized_Logging.md
```
