# 058_Grafana_Dashboards.md
# MiniURLShortener — Grafana Dashboards

> Core mental model: **Prometheus collects measurements; Grafana turns those measurements into operational eyesight. A dashboard is not a wall of charts. A good dashboard answers: Is the system healthy? What changed? Where is the bottleneck? What should I check next?**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Grafana In The Observability Stack](#3-grafana-in-the-observability-stack)
- [4. Dashboard vs Alert vs Log](#4-dashboard-vs-alert-vs-log)
- [5. MiniURLShortener Dashboard Goals](#5-miniurlshortener-dashboard-goals)
- [6. Golden Signals Dashboard](#6-golden-signals-dashboard)
- [7. RED Method Dashboard](#7-red-method-dashboard)
- [8. USE Method Dashboard](#8-use-method-dashboard)
- [9. Dashboard Layout Mental Model](#9-dashboard-layout-mental-model)
- [10. Prometheus Data Source Setup](#10-prometheus-data-source-setup)
- [11. Spring Boot Metrics Panels](#11-spring-boot-metrics-panels)
- [12. URL Shortener Business Metrics Panels](#12-url-shortener-business-metrics-panels)
- [13. Latency Percentile Panels](#13-latency-percentile-panels)
- [14. Error Rate Panels](#14-error-rate-panels)
- [15. Throughput Panels](#15-throughput-panels)
- [16. JVM Dashboard Panels](#16-jvm-dashboard-panels)
- [17. Database Dashboard Panels](#17-database-dashboard-panels)
- [18. Redis Dashboard Panels](#18-redis-dashboard-panels)
- [19. Kafka Dashboard Panels](#19-kafka-dashboard-panels)
- [20. Kubernetes Dashboard Panels](#20-kubernetes-dashboard-panels)
- [21. Dashboard Variables](#21-dashboard-variables)
- [22. PromQL Query Patterns](#22-promql-query-patterns)
- [23. Panel Types](#23-panel-types)
- [24. Step-by-Step Grafana Dashboard Build](#24-step-by-step-grafana-dashboard-build)
- [25. Example Dashboard JSON Skeleton](#25-example-dashboard-json-skeleton)
- [26. Production Dashboard Design Rules](#26-production-dashboard-design-rules)
- [27. Failure Stories](#27-failure-stories)
- [28. Debugging Playbooks Using Grafana](#28-debugging-playbooks-using-grafana)
- [29. Interview-Ready Explanation](#29-interview-ready-explanation)
- [30. Senior Engineer Checklist](#30-senior-engineer-checklist)
- [31. One-Page Cheat Sheet](#31-one-page-cheat-sheet)
- [32. One Picture To Remember](#32-one-picture-to-remember)

---

## 1. Why This Exists

In chapter `057_Prometheus_Metrics.md`, the system learned how to expose and scrape metrics.

Prometheus answers:

```text
What is the value of this metric over time?
```

Grafana answers:

```text
What is happening to my system right now?
Is the user experience healthy?
Where should I look first?
```

Without Grafana, metrics exist but engineers cannot quickly understand them.

Example Prometheus raw query:

```promql
rate(http_server_requests_seconds_count{uri="/api/v1/urls"}[5m])
```

Useful, but not friendly during an incident.

Grafana turns it into:

```text
Create URL RPS: 850 req/s
p95 latency: 120ms
5xx error rate: 0.3%
Postgres connections: 82%
Redis hit ratio: 91%
Kafka consumer lag: 12,000
```

Production mental model:

```text
Prometheus is the memory.
Grafana is the eyes.
Alerts are the alarm.
Logs are the microscope.
Traces are the request journey.
```

---

## 2. The One Core Mental Model

Grafana dashboard is a:

```text
CONTROL ROOM FOR PRODUCTION
```

Bad dashboard:

```text
100 random graphs
no grouping
no user impact view
no clear next action
```

Good dashboard:

```text
Top row: user health
Middle row: service behavior
Lower row: dependencies
Bottom row: infrastructure
```

ASCII:

```text
+--------------------------------------------------------------+
|                      GRAFANA DASHBOARD                       |
+--------------------------------------------------------------+
| USER EXPERIENCE                                              |
| RPS | p50 | p95 | p99 | 4xx | 5xx | Availability            |
+--------------------------------------------------------------+
| APPLICATION                                                  |
| create-url latency | redirect latency | exceptions | JVM     |
+--------------------------------------------------------------+
| DEPENDENCIES                                                 |
| Postgres | Redis | Kafka | External APIs                         |
+--------------------------------------------------------------+
| INFRASTRUCTURE                                               |
| CPU | Memory | Pods | Restarts | Network | Disk                  |
+--------------------------------------------------------------+
```

One-line memory:

```text
A Grafana dashboard should guide your eyes from symptom to suspected cause.
```

---

## 3. Grafana In The Observability Stack

Grafana usually does not store application metrics itself.

It queries data sources.

Common data sources:

```text
Prometheus    -> metrics
Loki          -> logs
Tempo/Jaeger  -> traces
Postgres      -> SQL data
CloudWatch    -> AWS metrics
Elasticsearch -> logs/search
```

For MiniURLShortener:

```text
Spring Boot Actuator /metrics
        |
        v
Prometheus scrapes metrics
        |
        v
Grafana queries Prometheus
        |
        v
Engineer sees dashboard
```

ASCII:

```text
+-------------------+      scrape       +-------------+
| Spring Boot App   | <---------------  | Prometheus  |
| /actuator/metrics |                   | TSDB        |
| /actuator/prom... |                   +------+------+ 
+-------------------+                          |
                                               | query PromQL
                                               v
                                        +-------------+
                                        | Grafana     |
                                        | dashboards  |
                                        +-------------+
```

Grafana is not only pretty charts.

Grafana is a decision system.

It should help answer:

```text
1. Are users impacted?
2. Which endpoint is unhealthy?
3. Is the problem app, DB, Redis, Kafka, or Kubernetes?
4. Did the issue start after deployment?
5. Is the system recovering?
```

---

## 4. Dashboard vs Alert vs Log

These are different tools.

Dashboard:

```text
Used when humans are looking.
Shows trends, relationships, and current state.
```

Alert:

```text
Used when humans are not looking.
Wakes or notifies engineers when action is needed.
```

Log:

```text
Used to inspect details of specific events or errors.
```

ASCII:

```text
Something bad happens
        |
        v
+----------------+       +----------------+       +----------------+
| Alert          | ----> | Dashboard      | ----> | Logs/Traces    |
| tells you      |       | shows where    |       | show details   |
| something bad  |       | to look        |       | of request     |
+----------------+       +----------------+       +----------------+
```

Rule:

```text
Alert should wake you.
Dashboard should orient you.
Logs should explain details.
```

---

## 5. MiniURLShortener Dashboard Goals

MiniURLShortener has two critical user flows:

```text
1. Create short URL
2. Redirect short URL
```

Create API cares about:

```text
latency
validation failures
alias conflicts
DB writes
short code generation retries
```

Redirect API cares about:

```text
very low latency
high throughput
cache hit ratio
DB fallback latency
not found rate
expired/blocked rate
```

Dashboard must show:

```text
1. Overall service health
2. Create API health
3. Redirect API health
4. JVM health
5. Postgres health
6. Redis health
7. Kafka analytics health
8. Kubernetes pod health
```

ASCII:

```text
MiniURLShortener Dashboard
        |
        +-- User-facing APIs
        |       +-- Create URL
        |       +-- Redirect URL
        |
        +-- App runtime
        |       +-- JVM
        |       +-- Thread pools
        |       +-- GC
        |
        +-- Dependencies
        |       +-- Postgres
        |       +-- Redis
        |       +-- Kafka
        |
        +-- Infrastructure
                +-- Kubernetes
                +-- CPU / memory
                +-- restarts
```

---

## 6. Golden Signals Dashboard

Golden signals are a strong production mental model.

For services, track:

```text
1. Latency
2. Traffic
3. Errors
4. Saturation
```

ASCII:

```text
+----------------------+-----------------------------------+
| Golden Signal        | URL Shortener Meaning             |
+----------------------+-----------------------------------+
| Latency              | How slow create/redirect is       |
| Traffic              | Requests per second               |
| Errors               | 4xx, 5xx, domain failures         |
| Saturation           | CPU, memory, DB pool, threads     |
+----------------------+-----------------------------------+
```

Top dashboard row:

```text
+-----------+-----------+-----------+-----------+-----------+
| RPS       | p95       | p99       | 5xx Rate  | Saturation|
+-----------+-----------+-----------+-----------+-----------+
```

Why top row matters:

```text
During incident, you first need user impact, not JVM internals.
```

Good first question:

```text
Are users failing or just internal metrics noisy?
```

---

## 7. RED Method Dashboard

RED is excellent for request-driven services.

RED means:

```text
Rate
Errors
Duration
```

For MiniURLShortener:

```text
Rate      -> requests per second
Errors    -> failed requests per second or percentage
Duration  -> p50/p95/p99 latency
```

ASCII:

```text
              RED METHOD

Request Stream
      |
      +-- Rate     -> how many requests?
      +-- Errors   -> how many failed?
      +-- Duration -> how long did they take?
```

Create API RED:

```promql
rate(http_server_requests_seconds_count{uri="/api/v1/urls",method="POST"}[5m])
```

Redirect API RED:

```promql
rate(http_server_requests_seconds_count{uri="/{shortCode}",method="GET"}[5m])
```

Error percentage:

```promql
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
/
sum(rate(http_server_requests_seconds_count[5m]))
* 100
```

---

## 8. USE Method Dashboard

USE is better for resources.

USE means:

```text
Utilization
Saturation
Errors
```

For dependencies:

```text
Postgres connection pool
Redis connection pool
Kafka consumer lag
CPU
memory
thread pool
```

ASCII:

```text
              USE METHOD

Resource
  |
  +-- Utilization -> how busy is it?
  +-- Saturation  -> is there queueing/waiting?
  +-- Errors      -> is it failing?
```

Example:

```text
HikariCP pool
  Utilization -> active / max connections
  Saturation  -> pending connection waiters
  Errors      -> connection timeout errors
```

Use RED for services.

Use USE for resources.

---

## 9. Dashboard Layout Mental Model

A 10/10 dashboard flows from business/user impact to machine internals.

Recommended layout:

```text
Row 1: Executive health
Row 2: API RED metrics
Row 3: Business metrics
Row 4: JVM/runtime
Row 5: Dependencies
Row 6: Kubernetes/infrastructure
```

ASCII:

```text
+--------------------------------------------------------------+
| ROW 1: SERVICE HEALTH                                        |
| Availability | RPS | p95 | p99 | 5xx % | SLO burn           |
+--------------------------------------------------------------+
| ROW 2: API HEALTH                                            |
| Create RED | Redirect RED | Top slow endpoints              |
+--------------------------------------------------------------+
| ROW 3: BUSINESS HEALTH                                       |
| URLs created | redirects | cache hit ratio | alias conflicts |
+--------------------------------------------------------------+
| ROW 4: JVM HEALTH                                            |
| heap | GC | threads | CPU | exceptions                      |
+--------------------------------------------------------------+
| ROW 5: DEPENDENCIES                                          |
| Postgres | Redis | Kafka                                    |
+--------------------------------------------------------------+
| ROW 6: INFRA                                                 |
| pods | restarts | node CPU | memory | network                  |
+--------------------------------------------------------------+
```

Bad layout:

```text
CPU first, business metrics last.
```

Better:

```text
User impact first, CPU later.
```

Reason:

```text
High CPU without user impact may not be urgent.
Small 5xx spike on redirect may be urgent.
```

---

## 10. Prometheus Data Source Setup

Grafana needs Prometheus as a data source.

Typical Docker Compose mental model:

```text
app -> exposes /actuator/prometheus
prometheus -> scrapes app
Grafana -> queries prometheus
```

Example Docker Compose services:

```yaml
services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=admin
```

Prometheus data source URL from Grafana container:

```text
http://prometheus:9090
```

From browser on host:

```text
http://localhost:9090
```

Common mistake:

```text
Using localhost:9090 inside Grafana container.
```

Why wrong?

```text
Inside Grafana container, localhost means Grafana container itself, not Prometheus.
```

Correct:

```text
Use Docker service name: prometheus:9090
```

ASCII:

```text
Host browser:
  localhost:3000 -> Grafana
  localhost:9090 -> Prometheus

Grafana container:
  prometheus:9090 -> Prometheus container
```

---

## 11. Spring Boot Metrics Panels

Spring Boot Micrometer exposes many useful metrics.

Important default metrics:

```text
http_server_requests_seconds_count
http_server_requests_seconds_sum
http_server_requests_seconds_bucket
jvm_memory_used_bytes
jvm_gc_pause_seconds_count
jvm_threads_live_threads
system_cpu_usage
process_cpu_usage
hikaricp_connections_active
hikaricp_connections_max
```

Core panels:

```text
1. Request rate by endpoint
2. Latency by endpoint
3. Error rate by status
4. JVM heap used
5. GC pause time
6. Live threads
7. Hikari active connections
8. CPU usage
```

ASCII:

```text
Spring Boot App
   |
   +-- HTTP metrics
   +-- JVM metrics
   +-- Hikari metrics
   +-- system/process metrics
   |
   v
Prometheus
   |
   v
Grafana panels
```

---

## 12. URL Shortener Business Metrics Panels

Technical metrics are not enough.

Add business metrics.

Examples:

```text
url_create_total
url_redirect_total
url_redirect_cache_hit_total
url_redirect_cache_miss_total
url_alias_conflict_total
url_not_found_total
url_expired_total
url_blocked_total
short_code_generation_retry_total
```

Business dashboard panels:

```text
URLs created per minute
Redirects per second
Cache hit ratio
Alias conflict rate
Not found rate
Expired link rate
Blocked link rate
Short code generation retry rate
```

ASCII:

```text
Business Health Row

+----------------+----------------+----------------+
| URLs Created   | Redirects/sec  | Cache Hit %    |
+----------------+----------------+----------------+
| Alias Conflict | Not Found Rate | Expired Rate   |
+----------------+----------------+----------------+
```

Why business metrics matter:

```text
A 200 OK service can still be unhealthy if redirects collapse or cache hit ratio drops.
```

Example:

```text
HTTP 200 rate looks fine.
But url_redirect_cache_hit_ratio drops from 95% to 30%.
Postgres load explodes.
Latency rises 10 minutes later.
```

Business metrics provide early warning.

---

## 13. Latency Percentile Panels

Average latency is dangerous.

Bad metric:

```text
average latency = 80ms
```

But users may experience:

```text
p50 = 40ms
p95 = 300ms
p99 = 2s
```

Use percentiles.

PromQL for p95:

```promql
histogram_quantile(
  0.95,
  sum(rate(http_server_requests_seconds_bucket[5m])) by (le, uri, method)
)
```

PromQL for p99:

```promql
histogram_quantile(
  0.99,
  sum(rate(http_server_requests_seconds_bucket[5m])) by (le, uri, method)
)
```

ASCII:

```text
Requests sorted by latency:

fast                                                  slow
|-------------------------------------------------------|
 p50                         p95                    p99

p50: typical user
p95: slow user experience
p99: tail latency / production pain
```

For URL Shortener:

```text
Redirect p99 matters more than create p99.
```

Why?

```text
Redirect is high-volume user-facing hot path.
Create API is lower-volume write path.
```

Suggested targets:

```text
Redirect p95 < 50ms from Redis
Redirect p99 < 150ms under normal load
Create p95 < 200ms
Create p99 < 500ms
```

These are learning targets, not universal laws.

---

## 14. Error Rate Panels

Error rate should be split by status class and endpoint.

Panels:

```text
5xx percentage
4xx percentage
error rate by endpoint
error rate by exception type
```

5xx percentage:

```promql
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
/
sum(rate(http_server_requests_seconds_count[5m]))
* 100
```

4xx percentage:

```promql
sum(rate(http_server_requests_seconds_count{status=~"4.."}[5m]))
/
sum(rate(http_server_requests_seconds_count[5m]))
* 100
```

Endpoint 5xx:

```promql
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) by (uri, method)
```

ASCII:

```text
Errors
  |
  +-- 4xx -> mostly client/domain behavior
  |          invalid URL, not found, expired
  |
  +-- 5xx -> server/dependency failure
             DB down, bug, timeout
```

Important:

```text
4xx spikes are not always bad.
```

Example:

```text
A bot scans random short codes.
404 rate increases.
Service is not broken.
```

But:

```text
Sudden 409 alias conflict spike may indicate abuse or broken client retries.
```

---

## 15. Throughput Panels

Throughput shows traffic volume.

Request rate:

```promql
sum(rate(http_server_requests_seconds_count[1m]))
```

By endpoint:

```promql
sum(rate(http_server_requests_seconds_count[1m])) by (uri, method)
```

Create throughput:

```promql
sum(rate(http_server_requests_seconds_count{uri="/api/v1/urls",method="POST"}[1m]))
```

Redirect throughput:

```promql
sum(rate(http_server_requests_seconds_count{method="GET"}[1m])) by (uri)
```

ASCII:

```text
Traffic flow:

Low RPS + high latency:
  likely dependency issue or cold path

High RPS + high latency:
  saturation / overload possible

High RPS + low latency:
  healthy scaling

Low RPS + high errors:
  bug, config, dependency outage
```

Throughput is context for every other graph.

---

## 16. JVM Dashboard Panels

Spring Boot production issues often appear in JVM metrics.

Important panels:

```text
Heap used
Heap max
Non-heap memory
GC pause rate
GC pause duration
Live threads
Daemon threads
CPU usage
Class loaded count
```

Heap usage:

```promql
sum(jvm_memory_used_bytes{area="heap"})
```

Heap max:

```promql
sum(jvm_memory_max_bytes{area="heap"})
```

Heap percentage:

```promql
sum(jvm_memory_used_bytes{area="heap"})
/
sum(jvm_memory_max_bytes{area="heap"})
* 100
```

GC pause rate:

```promql
sum(rate(jvm_gc_pause_seconds_count[5m]))
```

GC pause p95:

```promql
histogram_quantile(
  0.95,
  sum(rate(jvm_gc_pause_seconds_bucket[5m])) by (le)
)
```

Thread count:

```promql
jvm_threads_live_threads
```

ASCII:

```text
JVM symptoms:

Heap rising continuously
        |
        v
possible memory leak

GC pause rising
        |
        v
latency spikes

Thread count rising
        |
        v
blocked calls / pool leak
```

Debug mindset:

```text
If p99 latency spikes and GC pause also spikes, check memory allocation.
If latency spikes but GC is normal, check DB/Redis/Kafka/network.
```

---

## 17. Database Dashboard Panels

Postgres is critical for create API and cache-miss redirects.

Panels:

```text
DB query latency
Hikari active connections
Hikari idle connections
Hikari pending connections
Connection usage percentage
DB error count
Slow query count
Read/write QPS
```

Hikari active connections:

```promql
hikaricp_connections_active
```

Hikari max:

```promql
hikaricp_connections_max
```

Connection usage percentage:

```promql
hikaricp_connections_active
/
hikaricp_connections_max
* 100
```

Pending threads:

```promql
hikaricp_connections_pending
```

ASCII:

```text
Request -> Spring Boot -> Hikari Pool -> Postgres
                         |
                         +-- active connections
                         +-- idle connections
                         +-- pending waiters
                         +-- timeout errors
```

Interpretation:

```text
High active + high pending:
    DB pool saturated or queries slow

Low active + high app latency:
    bottleneck likely elsewhere

High active + low DB CPU:
    connection leak or lock waits possible

High DB CPU + high latency:
    missing index or heavy query
```

For URL Shortener:

```text
Redirect should mostly hit Redis.
If Postgres QPS rises with redirect RPS, cache is failing or hit ratio dropped.
```

---

## 18. Redis Dashboard Panels

Redis is critical for fast redirects.

Panels:

```text
Cache hit ratio
Cache hits/sec
Cache misses/sec
Redis latency
Redis connected clients
Redis memory usage
Evictions
Key count
Command rate
```

Cache hit ratio from app counters:

```promql
sum(rate(url_redirect_cache_hit_total[5m]))
/
(
  sum(rate(url_redirect_cache_hit_total[5m]))
  +
  sum(rate(url_redirect_cache_miss_total[5m]))
)
* 100
```

ASCII:

```text
Redirect request
      |
      v
+-------------+
| Redis cache |
+-------------+
   |       |
   | hit   | miss
   v       v
 fast    Postgres lookup
```

Interpretation:

```text
Hit ratio drops:
    cache expired too aggressively
    Redis unavailable
    key format changed
    deployment bug
    hot keys evicted

Redis latency rises:
    network issue
    Redis CPU/memory pressure
    large keys
    too many commands
```

Dashboard row:

```text
+--------------+--------------+--------------+--------------+
| Hit Ratio %  | Hits/sec     | Misses/sec   | Evictions    |
+--------------+--------------+--------------+--------------+
```

---

## 19. Kafka Dashboard Panels

Kafka is used for click analytics.

Redirect should not block on Kafka if analytics is async.

Panels:

```text
Producer send rate
Producer error rate
Producer latency
Consumer lag
Consumer processing rate
DLQ message rate
Retry topic rate
```

Consumer lag mental model:

```text
Lag = messages produced but not yet consumed
```

ASCII:

```text
Redirect API
   |
   v
Kafka topic: click-events
   |
   +-- partition 0: produced offset 1000, consumed 990, lag 10
   +-- partition 1: produced offset 2000, consumed 1200, lag 800
   +-- partition 2: produced offset 1500, consumed 1500, lag 0
```

Interpretation:

```text
Lag rising continuously:
    consumers slower than producers

Lag spikes then drops:
    burst handled successfully

DLQ rising:
    bad event format or downstream failure

Producer errors rising:
    Kafka unavailable or timeout
```

Important URL shortener rule:

```text
Kafka analytics failure should not break redirect path unless product requires strict click tracking.
```

---

## 20. Kubernetes Dashboard Panels

In EKS/Kubernetes, Grafana should show pod and node health.

Panels:

```text
Pod CPU usage
Pod memory usage
Pod restarts
Pod count
Deployment replicas available
Container OOM kills
Node CPU
Node memory
Network receive/transmit
HPA current replicas
```

ASCII:

```text
Kubernetes health
   |
   +-- Pod level
   |     +-- CPU
   |     +-- memory
   |     +-- restarts
   |
   +-- Deployment level
   |     +-- desired replicas
   |     +-- available replicas
   |
   +-- Node level
         +-- CPU pressure
         +-- memory pressure
         +-- network
```

Interpretation:

```text
Pod restarts increase:
    crash loop, OOM, failed liveness probe

Memory near limit:
    OOM risk

CPU throttling:
    latency spikes under load

HPA scaling up:
    traffic or CPU pressure increased
```

Production incident example:

```text
p99 latency spikes.
CPU usage looks moderate.
But CPU throttling is high.
Reason: CPU limit too low.
```

So do not only watch CPU usage.

Watch throttling too.

---

## 21. Dashboard Variables

Variables make dashboards reusable.

Useful variables:

```text
environment: dev/stage/prod
service: miniurl-shortener
instance: pod name
uri: endpoint
method: HTTP method
status: HTTP status
namespace: Kubernetes namespace
```

ASCII:

```text
Dashboard variable:

environment = prod
service     = miniurl-shortener
uri         = /api/v1/urls

Panel query uses variables:

rate(http_server_requests_seconds_count{
  application="$service",
  uri="$uri"
}[5m])
```

Example variable query for URI:

```promql
label_values(http_server_requests_seconds_count, uri)
```

Example variable query for instance:

```promql
label_values(up{job="miniurl-shortener"}, instance)
```

Why variables matter:

```text
One dashboard can inspect all pods, endpoints, and environments.
```

Common mistake:

```text
Creating separate dashboards for every pod manually.
```

Better:

```text
Use variables and filters.
```

---

## 22. PromQL Query Patterns

### Pattern 1: Counter rate

For counters, use `rate`.

```promql
rate(url_redirect_total[5m])
```

Why not raw counter?

```text
Counter only increases.
Rate shows speed per second.
```

### Pattern 2: Sum across pods

```promql
sum(rate(url_redirect_total[5m]))
```

### Pattern 3: Group by label

```promql
sum(rate(http_server_requests_seconds_count[5m])) by (uri, status)
```

### Pattern 4: Percentage

```promql
errors / total * 100
```

Example:

```promql
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
/
sum(rate(http_server_requests_seconds_count[5m]))
* 100
```

### Pattern 5: Histogram percentile

```promql
histogram_quantile(
  0.95,
  sum(rate(http_server_requests_seconds_bucket[5m])) by (le)
)
```

### Pattern 6: Per-endpoint percentile

```promql
histogram_quantile(
  0.95,
  sum(rate(http_server_requests_seconds_bucket[5m])) by (le, uri, method)
)
```

ASCII:

```text
Counter      -> rate(counter[5m])
Gauge        -> use directly
Histogram    -> histogram_quantile(... bucket ...)
Percentage   -> numerator / denominator * 100
By endpoint  -> sum(...) by (uri, method)
```

---

## 23. Panel Types

Grafana has many panel types.

Use the right one.

```text
Time series:
    latency over time
    RPS over time
    error rate over time

Stat:
    current p95
    current RPS
    current availability

Gauge:
    CPU percentage
    memory percentage
    connection pool usage

Table:
    top slow endpoints
    error codes by count

Heatmap:
    latency distribution
```

ASCII:

```text
Question                              Panel Type
------------------------------------------------
How is it changing over time?          Time series
What is current value?                 Stat
How full is resource?                  Gauge
Which endpoint is worst?               Table
How is latency distributed?            Heatmap
```

Bad example:

```text
Using gauge for request rate trend.
```

Better:

```text
Use time series for request rate.
```

---

## 24. Step-by-Step Grafana Dashboard Build

### Step 1: Add Prometheus data source

```text
Grafana UI
  -> Connections
  -> Data sources
  -> Add data source
  -> Prometheus
  -> URL: http://prometheus:9090
  -> Save & test
```

### Step 2: Create dashboard

```text
Dashboards
  -> New dashboard
  -> Add visualization
```

### Step 3: Add top-level stat panels

Panels:

```text
Current RPS
Current p95
Current p99
5xx percentage
Redis hit ratio
```

### Step 4: Add API RED row

Panels:

```text
RPS by endpoint
p95 by endpoint
p99 by endpoint
5xx by endpoint
4xx by endpoint
```

### Step 5: Add business row

Panels:

```text
URLs created/min
Redirects/sec
Alias conflicts/min
Not found/sec
Expired/sec
Blocked/sec
```

### Step 6: Add dependency row

Panels:

```text
Hikari pool usage
Postgres query latency
Redis hit ratio
Kafka consumer lag
```

### Step 7: Add runtime row

Panels:

```text
Heap usage
GC pause
Live threads
CPU usage
```

### Step 8: Add Kubernetes row

Panels:

```text
Pod restarts
CPU throttling
Memory usage
HPA replicas
```

### Step 9: Add variables

```text
environment
service
instance
uri
```

### Step 10: Save and version dashboard JSON

```text
infra/grafana/dashboards/miniurl-shortener-overview.json
```

Rule:

```text
Dashboards should be code-reviewed like application code.
```

---

## 25. Example Dashboard JSON Skeleton

This is not a full exported dashboard.

It shows how production teams store dashboard-as-code.

```json
{
  "title": "MiniURLShortener - Overview",
  "tags": ["miniurl", "spring-boot", "prometheus"],
  "timezone": "browser",
  "schemaVersion": 39,
  "version": 1,
  "refresh": "30s",
  "templating": {
    "list": [
      {
        "name": "service",
        "type": "query",
        "datasource": "Prometheus",
        "query": "label_values(up, job)",
        "refresh": 1
      },
      {
        "name": "uri",
        "type": "query",
        "datasource": "Prometheus",
        "query": "label_values(http_server_requests_seconds_count, uri)",
        "refresh": 1
      }
    ]
  },
  "panels": [
    {
      "title": "Request Rate",
      "type": "timeseries",
      "targets": [
        {
          "expr": "sum(rate(http_server_requests_seconds_count{job=\"$service\"}[5m]))"
        }
      ]
    },
    {
      "title": "p95 Latency",
      "type": "timeseries",
      "targets": [
        {
          "expr": "histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{job=\"$service\"}[5m])) by (le))"
        }
      ]
    }
  ]
}
```

Dashboard-as-code benefits:

```text
version controlled
reviewable
repeatable across environments
easy rollback
consistent between teams
```

---

## 26. Production Dashboard Design Rules

### Rule 1: Start with user impact

First row should answer:

```text
Are users okay?
```

### Rule 2: Do not graph everything

Too many panels create blindness.

Prefer:

```text
important, grouped, actionable panels
```

### Rule 3: Use consistent time windows

Common windows:

```text
1m for fast incident signal
5m for stable rate
30m/1h for trends
24h for daily patterns
```

### Rule 4: Label units clearly

Bad:

```text
Latency = 0.12
```

Better:

```text
Latency = 120 ms
```

### Rule 5: Put related panels together

```text
RPS, latency, errors side by side
```

### Rule 6: Avoid high-cardinality labels

Danger labels:

```text
userId
shortCode
longUrl
requestId
ipAddress
```

Never build dashboard queries using unbounded labels.

### Rule 7: Add descriptions

Each panel should explain:

```text
what it means
why it matters
what bad looks like
```

### Rule 8: Dashboard should support incident flow

```text
Symptom -> endpoint -> dependency -> pod -> logs/traces
```

ASCII:

```text
Dashboard design path:

User pain
   |
   v
Endpoint health
   |
   v
Dependency health
   |
   v
Runtime health
   |
   v
Infrastructure health
```

---

## 27. Failure Stories

### Failure Story 1: Beautiful dashboard, useless incident

Dashboard had:

```text
CPU
memory
disk
network
JVM classes
thread count
```

Incident:

```text
Redirect API p99 latency was 3 seconds.
```

Problem:

```text
No endpoint latency panel.
```

Lesson:

```text
Infrastructure graphs are not enough. User-facing RED metrics must be first.
```

---

### Failure Story 2: Average latency hid p99 pain

Dashboard showed:

```text
average latency = 70ms
```

Users complained.

Real values:

```text
p50 = 40ms
p95 = 500ms
p99 = 4s
```

Root cause:

```text
DB connection pool saturation affected tail requests.
```

Lesson:

```text
Use percentiles, not only averages.
```

---

### Failure Story 3: Cache miss storm not visible

Redirect latency increased slowly.

Dashboard showed:

```text
RPS normal
5xx normal
CPU rising
Postgres QPS rising
```

Missing panel:

```text
Redis cache hit ratio
```

Actual issue:

```text
Redis key prefix changed during deployment.
All redirects became cache misses.
```

Lesson:

```text
Business/dependency bridge metrics catch hidden problems early.
```

---

### Failure Story 4: Wrong Docker URL for Prometheus

Grafana data source configured:

```text
http://localhost:9090
```

Grafana container could not reach Prometheus.

Fix:

```text
http://prometheus:9090
```

Lesson:

```text
Inside Docker, localhost means current container.
```

---

### Failure Story 5: Dashboard overloaded with high-cardinality labels

Team added labels:

```text
shortCode
userId
ipAddress
```

Prometheus memory exploded.
Grafana became slow.

Lesson:

```text
Do not put unbounded IDs into metric labels.
```

---

## 28. Debugging Playbooks Using Grafana

### Playbook 1: Redirect latency spike

Check in order:

```text
1. Redirect p95/p99
2. Redirect RPS
3. Redis hit ratio
4. Redis latency/errors
5. Postgres QPS and Hikari pool
6. JVM GC pause
7. Pod CPU throttling/restarts
```

ASCII:

```text
Redirect p99 high
   |
   +-- Redis hit ratio low? -> cache issue
   |
   +-- Hikari pending high? -> DB pool saturation
   |
   +-- GC pause high? -> JVM memory pressure
   |
   +-- CPU throttling high? -> K8s limits issue
```

---

### Playbook 2: 5xx error spike

Check:

```text
1. Which endpoint has 5xx?
2. Did deployment happen?
3. Exception type metric/logs
4. DB/Redis/Kafka errors
5. Pod restarts
6. Config changes
```

Interpretation:

```text
Only create API 5xx:
    likely DB/write path issue

Only redirect API 5xx:
    Redis/DB fallback/service bug

All endpoints 5xx:
    app config, dependency outage, deployment bug
```

---

### Playbook 3: Kafka lag rising

Check:

```text
1. Producer rate
2. Consumer rate
3. Consumer lag by partition
4. Consumer errors
5. DLQ rate
6. Consumer pod CPU/memory
```

Interpretation:

```text
Producer rate > consumer rate:
    scale consumers or optimize processing

DLQ rising:
    bad event or downstream failure

One partition lag high:
    hot key or partition imbalance
```

---

### Playbook 4: DB connection pool saturation

Check:

```text
1. Hikari active/max
2. Hikari pending
3. DB query latency
4. Postgres CPU
5. Slow queries
6. Recent traffic increase
7. Redis miss rate
```

Interpretation:

```text
Active near max + pending rising:
    requests are waiting for DB connections

Redis miss rate rising before DB saturation:
    cache problem caused DB overload
```

---

## 29. Interview-Ready Explanation

If interviewer asks:

```text
How would you design Grafana dashboards for your URL shortener?
```

Strong answer:

```text
I would design the dashboard from user impact downward. The top row would show
availability, request rate, p95/p99 latency, and 5xx percentage. Then I would add
RED panels for the two key flows: create short URL and redirect short URL. For the
redirect path, I would include cache hit ratio, Redis latency, and Postgres fallback
rate because redirect latency depends heavily on cache behavior. For the create
path, I would show DB write latency, alias conflict rate, and short code generation
retry count. Then I would add JVM panels for heap, GC pause, threads, and CPU, plus
Hikari connection pool metrics, Kafka consumer lag for analytics, and Kubernetes
pod restarts and CPU throttling. I would use variables for environment, service,
instance, and endpoint, and store dashboards as JSON in Git. The goal is not to
show every metric, but to guide debugging from symptom to root cause.
```

Senior one-liner:

```text
A good Grafana dashboard starts with user pain and progressively reveals the dependency or resource causing it.
```

---

## 30. Senior Engineer Checklist

Before calling dashboard production-ready:

```text
[ ] Prometheus data source works
[ ] Dashboard has environment/service variables
[ ] Top row shows RPS, p95, p99, 5xx, availability
[ ] Create API has RED metrics
[ ] Redirect API has RED metrics
[ ] Cache hit ratio panel exists
[ ] Redis miss rate panel exists
[ ] Hikari active/max panel exists
[ ] Hikari pending panel exists
[ ] JVM heap panel exists
[ ] GC pause panel exists
[ ] Thread count panel exists
[ ] Kafka consumer lag panel exists
[ ] DLQ/retry panel exists if Kafka retries exist
[ ] Kubernetes pod restart panel exists
[ ] CPU throttling panel exists
[ ] Panels have correct units
[ ] Percentiles used instead of only averages
[ ] Dashboard avoids high-cardinality labels
[ ] Dashboard JSON is version controlled
[ ] Panel descriptions explain what bad looks like
[ ] Dashboard supports incident flow from symptom to cause
```

---

## 31. One-Page Cheat Sheet

```text
Core mental model:
Grafana is the production control room.
Prometheus stores metrics; Grafana visualizes them.

Dashboard order:
1. User impact
2. API RED metrics
3. Business metrics
4. JVM/runtime
5. Dependencies
6. Infrastructure

Golden signals:
Latency
Traffic
Errors
Saturation

RED:
Rate
Errors
Duration

USE:
Utilization
Saturation
Errors

Must-have MiniURLShortener panels:
RPS
p95 latency
p99 latency
5xx rate
4xx rate
create API RED
redirect API RED
cache hit ratio
Postgres pool usage
Redis latency
Kafka lag
JVM heap
GC pause
pod restarts
CPU throttling

PromQL patterns:
Counter -> rate(counter[5m])
Total across pods -> sum(rate(...[5m]))
Group -> sum(...) by (label)
Percent -> numerator / denominator * 100
p95 -> histogram_quantile(0.95, sum(rate(bucket[5m])) by (le))

Panel types:
Trend -> time series
Current value -> stat
Resource fullness -> gauge
Top offenders -> table
Distribution -> heatmap

Production rules:
Do not use average latency alone.
Do not graph everything.
Do not use high-cardinality labels.
Use variables.
Version dashboard JSON.
Start with user impact.
```

---

## 32. One Picture To Remember

```text
                    GRAFANA DASHBOARD MENTAL MODEL

                         "From pain to cause"

+-------------------------------------------------------------------+
| 1. USER PAIN                                                       |
| RPS | p95 | p99 | 5xx% | availability                            |
+-------------------------------------------------------------------+
                  |
                  v
+-------------------------------------------------------------------+
| 2. API FLOWS                                                       |
| Create URL RED        | Redirect URL RED                          |
+-------------------------------------------------------------------+
                  |
                  v
+-------------------------------------------------------------------+
| 3. BUSINESS SIGNALS                                                |
| URLs created | redirects | cache hit % | alias conflicts         |
+-------------------------------------------------------------------+
                  |
                  v
+-------------------------------------------------------------------+
| 4. DEPENDENCIES                                                    |
| Postgres pool | Redis latency | Kafka lag | DLQ                   |
+-------------------------------------------------------------------+
                  |
                  v
+-------------------------------------------------------------------+
| 5. RUNTIME + INFRA                                                 |
| JVM heap | GC | threads | pod restarts | CPU throttling          |
+-------------------------------------------------------------------+

FINAL MEMORY:

Prometheus collects numbers.
Grafana turns numbers into operational eyesight.
A 10/10 dashboard guides debugging from symptom to root cause.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Grafana is the visual control room for Prometheus metrics.
2. A dashboard should start with user impact, not CPU graphs.
3. RED metrics explain services; USE metrics explain resources.
4. Percentiles, cache hit ratio, DB pool usage, and Kafka lag are critical for URL shortener debugging.
5. A production dashboard should be actionable, version-controlled, low-cardinality, and designed for incident flow.
```

Next chapter:

```text
059_Alerting_Rules.md
```
