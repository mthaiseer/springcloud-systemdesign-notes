# 057_Prometheus_Metrics.md
# MiniURLShortener — Prometheus Metrics

> Core mental model: **Prometheus metrics are the numeric heartbeat of a production system. Logs explain individual events, traces explain request journeys, but metrics tell you whether the system is healthy, fast, saturated, failing, or about to break.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Metrics vs Logs vs Traces](#4-metrics-vs-logs-vs-traces)
- [5. Prometheus Pull Model](#5-prometheus-pull-model)
- [6. Spring Boot Actuator And Micrometer](#6-spring-boot-actuator-and-micrometer)
- [7. Metric Types](#7-metric-types)
- [8. Golden Signals For URL Shortener](#8-golden-signals-for-url-shortener)
- [9. HTTP Metrics](#9-http-metrics)
- [10. Business Metrics](#10-business-metrics)
- [11. Redis Metrics](#11-redis-metrics)
- [12. PostgreSQL And HikariCP Metrics](#12-postgresql-and-hikaricp-metrics)
- [13. Kafka Metrics](#13-kafka-metrics)
- [14. JVM Metrics](#14-jvm-metrics)
- [15. Custom Metrics In Java](#15-custom-metrics-in-java)
- [16. Prometheus Configuration](#16-prometheus-configuration)
- [17. Docker Compose Setup](#17-docker-compose-setup)
- [18. PromQL Mental Model](#18-promql-mental-model)
- [19. Important PromQL Queries](#19-important-promql-queries)
- [20. Grafana Dashboard Layout](#20-grafana-dashboard-layout)
- [21. Alerting Rules](#21-alerting-rules)
- [22. Step-by-Step Dry Runs](#22-step-by-step-dry-runs)
- [23. Internal Execution Walkthrough](#23-internal-execution-walkthrough)
- [24. Production Failure Stories](#24-production-failure-stories)
- [25. Debugging Mindset](#25-debugging-mindset)
- [26. Common Mistakes](#26-common-mistakes)
- [27. Interview-Ready Explanation](#27-interview-ready-explanation)
- [28. Senior Engineer Checklist](#28-senior-engineer-checklist)
- [29. One-Page Cheat Sheet](#29-one-page-cheat-sheet)
- [30. One Picture To Remember](#30-one-picture-to-remember)

---

## 1. Why This Exists

By now MiniURLShortener has moved from local API coding to production deployment.

You have:

```text
Client -> Gateway -> URL Service -> Redis -> PostgreSQL -> Kafka -> Analytics Worker
```

But production systems fail silently before users complain.

Without metrics, you only know this:

```text
"Users say redirects are slow."
```

With Prometheus metrics, you can answer:

```text
How many requests per second?
What is p95 latency?
What is p99 latency?
Which endpoint is slow?
Are 5xx errors increasing?
Is Redis cache hit ratio dropping?
Is PostgreSQL connection pool exhausted?
Is Kafka consumer lag growing?
Is JVM GC pausing the app?
Is CPU saturated?
```

Production mental model:

```text
Logs are stories.
Traces are journeys.
Metrics are vital signs.
```

A doctor does not start with surgery.

A doctor first checks:

```text
heart rate
blood pressure
oxygen level
temperature
```

A senior backend engineer checks:

```text
RPS
error rate
latency
saturation
queue lag
cache hit ratio
DB pool usage
GC pause
```

Prometheus is the machine that continuously records those vital signs.

---

## 2. The One Core Mental Model

Prometheus metrics are:

```text
NUMERIC TIME-SERIES MEMORY
```

They answer:

```text
What number changed over time?
```

ASCII:

```text
MiniURLShortener App
       |
       | exposes /actuator/prometheus
       v
+-----------------------+
| Numeric Measurements  |
| requests_total        |
| http_latency_seconds  |
| db_connections        |
| redis_cache_hits      |
+-----------------------+
       ^
       |
       | Prometheus scrapes every N seconds
       |
+-----------------------+
| Prometheus Server     |
| stores time series    |
+-----------------------+
       |
       v
+-----------------------+
| PromQL / Grafana      |
| dashboards + alerts   |
+-----------------------+
```

One-line memory:

```text
Prometheus repeatedly asks your app: "What are your current numbers?"
```

The app does not usually push metrics to Prometheus.

Prometheus pulls them.

```text
Prometheus -> HTTP GET /actuator/prometheus -> App returns metrics text
```

That text becomes time-series data.

---

## 3. Problem Statement

Build production-grade Prometheus metrics for MiniURLShortener.

It must measure:

```text
1. HTTP request rate.
2. HTTP error rate.
3. HTTP latency p95 and p99.
4. Redirect success and failure counts.
5. Short URL creation count.
6. Redis cache hits and misses.
7. PostgreSQL connection pool usage.
8. Kafka producer/consumer health.
9. Analytics worker lag.
10. JVM memory, thread, and GC behavior.
11. System CPU and process health.
```

It should support:

```text
Prometheus scraping
Grafana dashboards
alerting rules
incident debugging
capacity planning
interview explanation
```

It should avoid:

```text
high-cardinality labels
user IDs as labels
long URLs as labels
shortCode as label
too many custom metrics
metrics without dashboards
alerts without action
```

Out of scope for this chapter:

```text
Full OpenTelemetry tracing
Centralized logging stack
Grafana Cloud setup
Kubernetes ServiceMonitor deep dive
Long-term metric storage
```

This chapter gives the production metric foundation.

---

## 4. Metrics vs Logs vs Traces

Observability has three common signals.

```text
Metrics: numbers over time
Logs: timestamped events
Traces: request path across services
```

ASCII:

```text
One redirect request
       |
       +-- Metric: http_requests_total{status="302"} +1
       |
       +-- Log: "redirect success shortCode=abc latency=12ms"
       |
       +-- Trace: Gateway -> URL Service -> Redis -> DB
```

Comparison:

```text
+---------+--------------------------+------------------------------+
| Signal  | Best For                 | Example                      |
+---------+--------------------------+------------------------------+
| Metrics | health, trends, alerts   | p99 latency > 300ms          |
| Logs    | details of one event     | DB timeout stack trace       |
| Traces  | cross-service path       | Redis slow inside request    |
+---------+--------------------------+------------------------------+
```

Mental model:

```text
Metrics tell you something is wrong.
Logs tell you what happened.
Traces tell you where time went.
```

Example incident:

```text
Metric:
    redirect p99 latency jumped from 40ms to 900ms

Trace:
    Redis is fast, DB lookup is slow

Log:
    PostgreSQL timeout acquiring connection from Hikari pool
```

Prometheus mainly handles metrics.

---

## 5. Prometheus Pull Model

Prometheus works mostly by pulling metrics.

Flow:

```text
1. App exposes metrics endpoint.
2. Prometheus has scrape config.
3. Prometheus calls endpoint every 15s.
4. App returns current metric values.
5. Prometheus stores values with timestamp.
6. PromQL queries calculate rate, percentiles, error ratio.
```

ASCII:

```text
             scrape every 15s
+------------+   HTTP GET   +-------------------------+
| Prometheus | -----------> | MiniURLShortener App    |
| Server     |              | /actuator/prometheus    |
+------------+ <----------- +-------------------------+
       |
       | stores samples
       v
+----------------------------+
| Time Series DB             |
| metric + labels + time     |
+----------------------------+
```

Example metric sample:

```text
http_server_requests_seconds_count{method="GET",uri="/{shortCode}",status="302"} 15400
```

This means:

```text
Metric name: http_server_requests_seconds_count
Labels: method=GET, uri=/{shortCode}, status=302
Value: 15400
```

Prometheus stores many samples over time:

```text
10:00:00 -> 15400
10:00:15 -> 15620
10:00:30 -> 15890
```

Then PromQL calculates:

```text
requests per second = rate(counter[1m])
```

Important:

```text
Prometheus does not store raw request logs.
It stores numeric samples.
```

---

## 6. Spring Boot Actuator And Micrometer

Spring Boot uses Micrometer as the metrics facade.

Mental model:

```text
Your code records metrics using Micrometer.
Micrometer exports them in Prometheus format.
Prometheus scrapes them.
```

ASCII:

```text
Spring Boot App
      |
      v
+------------------+
| Micrometer       |
| metrics facade   |
+------------------+
      |
      v
+--------------------------+
| Actuator Prometheus      |
| /actuator/prometheus     |
+--------------------------+
      |
      v
+------------------+
| Prometheus       |
+------------------+
```

Maven dependencies:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

Application YAML:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  endpoint:
    health:
      show-details: always
  metrics:
    tags:
      application: mini-url-shortener
```

Endpoint:

```text
GET /actuator/prometheus
```

Example output:

```text
# HELP http_server_requests_seconds Duration of HTTP server request handling
# TYPE http_server_requests_seconds summary
http_server_requests_seconds_count{method="GET",uri="/{shortCode}",status="302"} 15400
http_server_requests_seconds_sum{method="GET",uri="/{shortCode}",status="302"} 230.55
```

Spring Boot gives many metrics automatically:

```text
HTTP server metrics
JVM memory metrics
GC metrics
thread metrics
process CPU metrics
system CPU metrics
HikariCP metrics
Logback metrics
```

Custom business metrics are added by you.

---

## 7. Metric Types

Prometheus has four important metric types.

```text
Counter
Gauge
Histogram
Summary
```

### Counter

Counter only goes up.

Use for:

```text
total requests
total errors
total redirects
total short URLs created
total cache misses
```

Example:

```text
url_redirect_total 1000
url_redirect_total 1001
url_redirect_total 1002
```

Never decrease a counter manually.

Use `rate()` to convert counter growth into per-second rate.

```promql
rate(url_redirect_total[1m])
```

### Gauge

Gauge can go up and down.

Use for:

```text
active DB connections
Kafka consumer lag
queue size
JVM memory used
active requests
```

Example:

```text
hikaricp_connections_active 18
hikaricp_connections_active 7
hikaricp_connections_active 23
```

### Histogram

Histogram measures distribution.

Use for latency.

```text
How many requests completed under 50ms?
How many under 100ms?
How many under 300ms?
How many under 1s?
```

Prometheus stores buckets:

```text
http_request_duration_seconds_bucket{le="0.05"} 1000
http_request_duration_seconds_bucket{le="0.1"} 1800
http_request_duration_seconds_bucket{le="0.3"} 2400
http_request_duration_seconds_bucket{le="1.0"} 2500
```

Then query p95:

```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
```

### Summary

Summary also tracks quantiles, but histograms are usually better for Prometheus server-side aggregation.

Simple rule:

```text
Counter -> how many happened?
Gauge -> what is current value?
Histogram -> how long / how large distribution?
```

ASCII:

```text
+-----------+---------------------+-----------------------------+
| Type      | Changes             | Use                         |
+-----------+---------------------+-----------------------------+
| Counter   | only up             | total redirects/errors      |
| Gauge     | up and down         | active DB connections       |
| Histogram | bucketed samples    | latency percentiles         |
+-----------+---------------------+-----------------------------+
```

---

## 8. Golden Signals For URL Shortener

For MiniURLShortener, use the four golden signals.

```text
1. Traffic
2. Errors
3. Latency
4. Saturation
```

ASCII:

```text
                 GOLDEN SIGNALS

        +-----------------------------+
        | Traffic                     |
        | How many requests/sec?      |
        +-----------------------------+
                      |
        +-----------------------------+
        | Errors                      |
        | How many failed?            |
        +-----------------------------+
                      |
        +-----------------------------+
        | Latency                     |
        | How long does it take?      |
        +-----------------------------+
                      |
        +-----------------------------+
        | Saturation                  |
        | How full are resources?     |
        +-----------------------------+
```

For URL shortener:

```text
Traffic:
    redirect RPS
    create URL RPS

Errors:
    5xx rate
    4xx rate
    redirect not found rate
    alias conflict rate

Latency:
    redirect p50/p95/p99
    create p50/p95/p99
    Redis latency
    DB latency

Saturation:
    CPU
    memory
    DB pool active connections
    Redis connections
    Kafka lag
    worker backlog
```

Senior rule:

```text
Dashboard starts with golden signals, not random JVM graphs.
```

---

## 9. HTTP Metrics

Spring Boot automatically records HTTP metrics.

Common metric:

```text
http_server_requests_seconds
```

Labels usually include:

```text
method
uri
status
exception
outcome
application
```

Example:

```text
http_server_requests_seconds_count{
  method="GET",
  uri="/{shortCode}",
  status="302",
  outcome="REDIRECTION"
}
```

Useful questions:

```text
How many redirect requests per second?
How many create requests per second?
What is 5xx error rate?
What is p99 latency by endpoint?
```

Important endpoint grouping:

```text
Good label:
    uri="/{shortCode}"

Bad label:
    uri="/abc123"
    uri="/sale2026"
    uri="/mohamed"
```

Why bad?

```text
Every shortCode becomes a new time series.
Millions of short codes destroy Prometheus.
```

This is called high cardinality.

Correct mental model:

```text
Labels should describe low-cardinality dimensions.
```

Good labels:

```text
method=GET
uri=/{shortCode}
status=302
outcome=REDIRECTION
```

Bad labels:

```text
shortCode=abc123
longUrl=https://...
userId=12345
email=...
requestId=...
```

---

## 10. Business Metrics

HTTP metrics tell system behavior.

Business metrics tell product behavior.

For URL shortener:

```text
short_urls_created_total
redirects_total
redirect_failures_total
custom_alias_conflicts_total
expired_redirects_total
blocked_redirects_total
cache_hits_total
cache_misses_total
```

ASCII:

```text
Business event happens
       |
       v
Service increments counter
       |
       v
Micrometer exposes metric
       |
       v
Prometheus scrapes metric
       |
       v
Dashboard shows trend
```

Example business questions:

```text
Are redirects increasing after marketing campaign?
Are alias conflicts increasing?
Are expired links causing too many 410 responses?
Is cache miss rate too high?
Are blocked links being hit repeatedly?
```

Business metric design:

```text
Metric name: url_redirects_total
Labels:
    result=success|not_found|expired|blocked
    source=cache|database|none
```

Good:

```text
url_redirects_total{result="success",source="cache"}
url_redirects_total{result="success",source="database"}
url_redirects_total{result="not_found",source="none"}
```

Bad:

```text
url_redirects_total{shortCode="abc123"}
```

Rule:

```text
A metric label should have a small known set of possible values.
```

---

## 11. Redis Metrics

Redis is critical for redirect speed.

Cache-aside flow:

```text
GET /abc123
   |
   v
Redis lookup
   |
   +-- hit  -> redirect fast
   |
   +-- miss -> DB lookup -> populate Redis
```

Metrics to watch:

```text
cache_hits_total
cache_misses_total
cache_hit_ratio
redis_command_latency
redis_connection_failures
redis_memory_used
redis_evictions
```

ASCII:

```text
Redirect request
      |
      v
+------------+
| Redis GET  |
+------------+
   |      |
 hit    miss
   |      |
   v      v
fast    DB fallback
```

Cache hit ratio:

```promql
sum(rate(url_cache_access_total{result="hit"}[5m]))
/
sum(rate(url_cache_access_total[5m]))
```

Interpretation:

```text
95% hit ratio:
    Redis protects DB well.

40% hit ratio:
    DB receives too much redirect traffic.
```

Bad sign:

```text
cache hit ratio drops
DB QPS rises
redirect p99 rises
Hikari active connections rise
```

This usually means:

```text
Redis down
cache TTL too low
hot keys evicted
bad cache key format
deployment changed cache namespace
```

---

## 12. PostgreSQL And HikariCP Metrics

PostgreSQL is the source of truth.

HikariCP is the connection pool.

Important Hikari metrics:

```text
hikaricp_connections_active
hikaricp_connections_idle
hikaricp_connections_pending
hikaricp_connections_timeout_total
hikaricp_connections_max
hikaricp_connections_min
```

ASCII:

```text
App Threads
   |
   v
+-----------------------+
| HikariCP Pool         |
| active / idle / wait  |
+-----------------------+
   |
   v
+-----------------------+
| PostgreSQL            |
+-----------------------+
```

Connection pool states:

```text
active:
    currently borrowed by requests

idle:
    ready to use

pending:
    app threads waiting for a connection

timeout:
    request waited too long and failed
```

Production smell:

```text
hikaricp_connections_active ~= max
hikaricp_connections_pending > 0
redirect latency rising
DB CPU rising
```

This means:

```text
DB is bottleneck
queries are slow
pool too small
pool too large and DB overloaded
Redis miss storm
transaction held too long
```

Important query:

```promql
hikaricp_connections_active / hikaricp_connections_max
```

Alert idea:

```text
DB pool usage > 90% for 5 minutes
```

But do not blindly increase pool size.

Senior mindset:

```text
A bigger pool can make DB slower if DB is already saturated.
```

---

## 13. Kafka Metrics

Kafka handles click analytics asynchronously.

Flow:

```text
Redirect API -> Kafka topic click-events -> Analytics Worker -> DB/OLAP
```

Metrics to watch:

```text
producer send rate
producer error rate
producer latency
consumer lag
consumer records consumed rate
consumer processing failures
dead-letter topic count
retry topic count
```

ASCII:

```text
Redirect Service
      |
      | publish click event
      v
+------------------+
| Kafka Topic      |
| click-events     |
+------------------+
      |
      v
+------------------+
| Analytics Worker |
| consumer group   |
+------------------+
```

Consumer lag mental model:

```text
lag = messages produced - messages consumed
```

Example:

```text
Produced offset: 1,000,000
Consumed offset:   940,000
Lag:                60,000
```

If lag grows:

```text
worker too slow
worker crashed
DB writes slow
partition count too low
consumer rebalance loop
poison message causing retries
```

Key alert:

```text
consumer lag increasing for 10 minutes
```

Important:

```text
Redirect API should not fail only because analytics Kafka is slow,
if analytics is non-critical.
```

Metric helps verify this:

```text
Kafka producer failures rising
redirect success remains stable
```

That is graceful degradation.

---

## 14. JVM Metrics

Spring Boot exposes JVM metrics automatically.

Important JVM metrics:

```text
jvm_memory_used_bytes
jvm_memory_committed_bytes
jvm_memory_max_bytes
jvm_gc_pause_seconds_count
jvm_gc_pause_seconds_sum
jvm_threads_live_threads
jvm_threads_daemon_threads
process_cpu_usage
system_cpu_usage
```

ASCII:

```text
Spring Boot JVM
      |
      +-- Heap memory
      +-- Non-heap memory
      +-- GC pauses
      +-- Threads
      +-- CPU
      +-- Classes
```

Heap usage smell:

```text
heap used keeps climbing
GC runs frequently
memory does not return down
```

Possible causes:

```text
memory leak
large cache inside app
unbounded queue
too much response buffering
large Kafka batch
high-cardinality metric labels
```

GC pause smell:

```text
p99 latency spikes
jvm_gc_pause_seconds_sum increases sharply
CPU high
```

Thread smell:

```text
threads keep increasing
Tomcat threads busy
Hikari pending connections rising
```

Senior debugging:

```text
Latency spike + GC pause spike = JVM memory pressure.
Latency spike + DB pool pending = DB bottleneck.
Latency spike + CPU high = CPU saturation.
Latency spike + Kafka lag only = async worker issue, not redirect path.
```

---

## 15. Custom Metrics In Java

Use Micrometer `MeterRegistry`.

Create a metrics service:

```java
package com.miniurl.shortener.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class UrlMetrics {

    private final MeterRegistry meterRegistry;

    public UrlMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordShortUrlCreated(String type) {
        Counter.builder("url_short_urls_created_total")
                .description("Total short URLs created")
                .tag("type", type) // generated or custom_alias
                .register(meterRegistry)
                .increment();
    }

    public void recordRedirect(String result, String source) {
        Counter.builder("url_redirects_total")
                .description("Total redirect attempts")
                .tag("result", result) // success, not_found, expired, blocked
                .tag("source", source) // cache, database, none
                .register(meterRegistry)
                .increment();
    }

    public void recordCacheAccess(String result) {
        Counter.builder("url_cache_access_total")
                .description("Total cache access attempts")
                .tag("result", result) // hit or miss
                .register(meterRegistry)
                .increment();
    }
}
```

Use it in service:

```java
@Service
public class RedirectService {

    private final UrlMetrics urlMetrics;

    public RedirectService(UrlMetrics urlMetrics) {
        this.urlMetrics = urlMetrics;
    }

    public String redirect(String shortCode) {
        Optional<String> cachedUrl = cache.get(shortCode);

        if (cachedUrl.isPresent()) {
            urlMetrics.recordCacheAccess("hit");
            urlMetrics.recordRedirect("success", "cache");
            return cachedUrl.get();
        }

        urlMetrics.recordCacheAccess("miss");

        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    urlMetrics.recordRedirect("not_found", "none");
                    return new ShortCodeNotFoundException(shortCode);
                });

        if (mapping.isExpired()) {
            urlMetrics.recordRedirect("expired", "database");
            throw new ShortCodeExpiredException(shortCode);
        }

        urlMetrics.recordRedirect("success", "database");
        cache.put(shortCode, mapping.getLongUrl());
        return mapping.getLongUrl();
    }
}
```

Better optimization:

```text
Create counters once as fields for fixed labels.
Avoid rebuilding dynamic counters for every request if labels are stable.
```

Bad custom metric:

```java
.tag("shortCode", shortCode)
```

Why bad?

```text
Millions of short codes = millions of time series.
Prometheus memory explodes.
```

Correct:

```java
.tag("result", "success")
.tag("source", "cache")
```

---

## 16. Prometheus Configuration

Basic `prometheus.yml`:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: "mini-url-shortener"
    metrics_path: "/actuator/prometheus"
    static_configs:
      - targets: ["mini-url-service:8080"]
```

Mental model:

```text
job_name:
    logical service name

metrics_path:
    endpoint Prometheus calls

targets:
    host:port of app instances
```

For local machine:

```yaml
targets: ["host.docker.internal:8080"]
```

For Docker Compose service network:

```yaml
targets: ["mini-url-service:8080"]
```

For Kubernetes:

```text
Prometheus discovers pods/services using labels.
```

But the same concept remains:

```text
Prometheus needs to know where /actuator/prometheus lives.
```

---

## 17. Docker Compose Setup

Example local setup:

```yaml
services:
  mini-url-service:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker

  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    depends_on:
      - mini-url-service

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    depends_on:
      - prometheus
```

Flow:

```text
Browser -> localhost:9090 -> Prometheus UI
Browser -> localhost:3000 -> Grafana UI
Prometheus -> mini-url-service:8080/actuator/prometheus
```

ASCII:

```text
+----------------+        scrape        +----------------------+
| Prometheus     | -------------------> | mini-url-service     |
| localhost:9090 |                      | :8080/actuator/...   |
+----------------+                      +----------------------+
        |
        | data source
        v
+----------------+
| Grafana        |
| localhost:3000 |
+----------------+
```

Check target status:

```text
Prometheus UI -> Status -> Targets
```

Healthy target:

```text
State = UP
Last scrape = recent
```

Unhealthy target:

```text
State = DOWN
Error = connection refused / timeout / 404
```

---

## 18. PromQL Mental Model

PromQL is the query language for Prometheus.

Core idea:

```text
Select time series.
Filter by labels.
Calculate over time.
Aggregate.
```

ASCII:

```text
Metric name
   |
   v
Filter labels
   |
   v
Range window
   |
   v
Function
   |
   v
Aggregation
```

Example:

```promql
sum(rate(http_server_requests_seconds_count{uri="/{shortCode}"}[1m]))
```

Read it step by step:

```text
1. Pick request count metric.
2. Filter only redirect endpoint.
3. Look at last 1 minute.
4. Convert counter growth to per-second rate.
5. Sum all instances/statuses.
```

Important PromQL functions:

```text
rate(counter[window])
    per-second average growth

increase(counter[window])
    total growth in window

sum(...)
    aggregate many series

avg(...)
    average values

max(...)
    maximum value

histogram_quantile(0.95, ...)
    p95 from histogram buckets
```

Rule:

```text
Use rate() for counters in dashboards.
Use increase() when you need total count over a window.
```

---

## 19. Important PromQL Queries

### Redirect RPS

```promql
sum(rate(http_server_requests_seconds_count{uri="/{shortCode}"}[1m]))
```

### Create URL RPS

```promql
sum(rate(http_server_requests_seconds_count{uri="/api/v1/urls"}[1m]))
```

### 5xx Error Rate

```promql
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
```

### 5xx Error Ratio

```promql
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
/
sum(rate(http_server_requests_seconds_count[5m]))
```

### Redirect p95 Latency

```promql
histogram_quantile(
  0.95,
  sum(rate(http_server_requests_seconds_bucket{uri="/{shortCode}"}[5m])) by (le)
)
```

### Redirect p99 Latency

```promql
histogram_quantile(
  0.99,
  sum(rate(http_server_requests_seconds_bucket{uri="/{shortCode}"}[5m])) by (le)
)
```

### Cache Hit Ratio

```promql
sum(rate(url_cache_access_total{result="hit"}[5m]))
/
sum(rate(url_cache_access_total[5m]))
```

### DB Pool Usage

```promql
sum(hikaricp_connections_active)
/
sum(hikaricp_connections_max)
```

### JVM Heap Usage Ratio

```promql
sum(jvm_memory_used_bytes{area="heap"})
/
sum(jvm_memory_max_bytes{area="heap"})
```

### GC Pause Rate

```promql
sum(rate(jvm_gc_pause_seconds_sum[5m]))
```

### Kafka Consumer Lag

Metric name depends on exporter/client, but concept:

```promql
sum(kafka_consumer_records_lag_max)
```

### Redirect Business Success Rate

```promql
sum(rate(url_redirects_total{result="success"}[5m]))
/
sum(rate(url_redirects_total[5m]))
```

---

## 20. Grafana Dashboard Layout

A production dashboard should be ordered by debugging priority.

Recommended layout:

```text
Row 1: Golden Signals
    RPS
    error ratio
    p95 latency
    p99 latency

Row 2: Endpoint Breakdown
    redirect RPS
    create RPS
    4xx by endpoint
    5xx by endpoint

Row 3: Cache
    cache hit ratio
    cache hits/sec
    cache misses/sec
    Redis latency/errors

Row 4: Database
    Hikari active connections
    pending connections
    DB query latency
    DB errors

Row 5: Kafka / Worker
    producer errors
    consumer lag
    worker processing rate
    DLQ count

Row 6: JVM / Host
    heap usage
    GC pause
    CPU
    threads
```

ASCII:

```text
+--------------------------------------------------+
| GOLDEN SIGNALS: RPS | Errors | p95 | p99         |
+--------------------------------------------------+
| ENDPOINTS: Redirect | Create | 4xx | 5xx         |
+--------------------------------------------------+
| CACHE: Hit Ratio | Hits | Misses | Redis Errors |
+--------------------------------------------------+
| DB: Active Conn | Pending | Timeout | Latency     |
+--------------------------------------------------+
| KAFKA: Lag | Producer Errors | DLQ | Retry        |
+--------------------------------------------------+
| JVM: Heap | GC | CPU | Threads                   |
+--------------------------------------------------+
```

Dashboard principle:

```text
Top rows answer: Is user experience bad?
Middle rows answer: Which dependency is failing?
Bottom rows answer: Is the runtime saturated?
```

---

## 21. Alerting Rules

Alerts should be actionable.

Bad alert:

```text
CPU is 70%
```

Better alert:

```text
Redirect p99 latency > 500ms for 10 minutes and traffic > 100 RPS
```

Example rules:

```yaml
groups:
  - name: mini-url-shortener-alerts
    rules:
      - alert: HighHttp5xxErrorRate
        expr: |
          sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
          /
          sum(rate(http_server_requests_seconds_count[5m])) > 0.02
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High 5xx error rate"
          description: "More than 2% of HTTP requests are failing with 5xx."

      - alert: HighRedirectP99Latency
        expr: |
          histogram_quantile(
            0.99,
            sum(rate(http_server_requests_seconds_bucket{uri="/{shortCode}"}[5m])) by (le)
          ) > 0.5
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "High redirect p99 latency"
          description: "Redirect p99 latency is above 500ms."

      - alert: LowCacheHitRatio
        expr: |
          sum(rate(url_cache_access_total{result="hit"}[5m]))
          /
          sum(rate(url_cache_access_total[5m])) < 0.80
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "Low cache hit ratio"
          description: "Redis cache hit ratio is below 80%."

      - alert: HikariPoolAlmostFull
        expr: |
          sum(hikaricp_connections_active)
          /
          sum(hikaricp_connections_max) > 0.90
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "Database connection pool almost full"
          description: "Hikari active connections are above 90% of max."
```

Alert design rule:

```text
Alert on user impact first.
Alert on root-cause signals second.
Avoid alerting on noisy symptoms alone.
```

---

## 22. Step-by-Step Dry Runs

### Dry Run 1: Normal redirect traffic

Situation:

```text
Traffic: 1000 RPS
Redis hit ratio: 96%
p99 latency: 45ms
5xx ratio: 0.01%
```

Flow:

```text
1. Redirect requests hit app.
2. Spring Boot records HTTP request metrics.
3. Custom metric records cache hit or miss.
4. Prometheus scrapes every 15s.
5. Grafana shows stable RPS and latency.
6. No alerts fire.
```

Interpretation:

```text
System is healthy.
Redis protects DB.
DB pool stays low.
```

---

### Dry Run 2: Redis outage

Situation:

```text
Redis unavailable.
App falls back to PostgreSQL.
```

Metric changes:

```text
cache hit ratio drops to 0%
DB pool active connections rise
DB query rate rises
redirect p99 latency rises
5xx may rise if DB saturates
```

Debug flow:

```text
1. Start with p99 alert.
2. Check cache hit ratio.
3. See sudden drop.
4. Check Redis errors.
5. Check DB pool saturation.
6. Decide whether to restore Redis or scale DB/read replicas temporarily.
```

ASCII:

```text
Redis down
   |
   v
cache misses rise
   |
   v
DB load rises
   |
   v
latency rises
   |
   v
possible 5xx
```

---

### Dry Run 3: DB connection pool exhaustion

Situation:

```text
Traffic spike + low cache hit ratio.
```

Metrics:

```text
hikaricp_connections_active near max
hikaricp_connections_pending > 0
redirect p99 > 1s
5xx timeout errors rising
```

Interpretation:

```text
Requests are waiting for DB connections.
```

Possible fixes:

```text
restore cache hit ratio
optimize slow query
add index
reduce transaction time
increase read replicas
carefully tune pool size
```

Do not blindly do:

```text
maxPoolSize 20 -> 200
```

Because:

```text
DB may become even more overloaded.
```

---

### Dry Run 4: Kafka worker lag

Situation:

```text
Redirect API works, but analytics dashboard is delayed.
```

Metrics:

```text
redirect latency normal
HTTP 5xx normal
Kafka consumer lag growing
worker processing rate low
DLQ count rising maybe
```

Interpretation:

```text
User redirect path is healthy.
Async analytics pipeline is behind.
```

Fix direction:

```text
scale analytics workers
increase partitions
fix slow analytics DB writes
inspect poison messages
check retry/DLQ rate
```

---

### Dry Run 5: High-cardinality metric mistake

Bad code:

```java
Counter.builder("url_redirects_total")
        .tag("shortCode", shortCode)
        .register(meterRegistry)
        .increment();
```

Traffic:

```text
10 million unique short codes per day
```

Result:

```text
Prometheus memory grows
scrapes become slow
queries time out
Grafana dashboards break
```

Fix:

```text
Remove shortCode label.
Use result/source labels only.
Use logs/traces for individual shortCode debugging.
```

Lesson:

```text
Metrics are for aggregation, not per-entity storage.
```

---

## 23. Internal Execution Walkthrough

Request path with metrics:

```text
1. HTTP request enters Tomcat.
2. Spring MVC handles controller mapping.
3. Micrometer HTTP timer starts automatically.
4. Service executes redirect/create logic.
5. Custom metrics increment business counters.
6. Micrometer HTTP timer stops.
7. Actuator stores current meter values in registry.
8. Prometheus scrapes /actuator/prometheus.
9. Prometheus stores samples.
10. Grafana queries Prometheus.
11. Alertmanager evaluates rules and sends alerts.
```

ASCII:

```text
Client
  |
  v
Spring Boot App
  |
  +-- auto HTTP metrics
  +-- custom business metrics
  +-- JVM metrics
  +-- Hikari metrics
  |
  v
/actuator/prometheus
  ^
  |
Prometheus scrape
  |
  v
Time-series storage
  |
  +-- Grafana dashboard
  +-- Alert rules
```

Important distinction:

```text
Request execution records metrics in memory.
Prometheus scraping reads current metric state later.
```

If Prometheus is temporarily down:

```text
App still works.
Metrics may have gaps.
```

If app is down:

```text
Prometheus target becomes DOWN.
```

---

## 24. Production Failure Stories

### Failure Story 1: No metrics, only logs

Users complain:

```text
Redirect is slow.
```

Team searches logs manually.

Problem:

```text
No p95/p99 dashboard.
No cache hit ratio.
No DB pool metric.
```

Fix:

```text
Add golden-signal dashboard.
Add Redis and Hikari metrics.
```

Lesson:

```text
Logs are not enough for production health.
```

---

### Failure Story 2: Cache hit ratio drop caused DB incident

Deployment changed Redis key prefix:

```text
old key: short:abc123
new key: url:abc123
```

Result:

```text
All requests miss cache.
DB QPS increases 20x.
DB pool saturates.
p99 latency explodes.
```

Metrics reveal:

```text
cache hit ratio drops first
DB pool active rises next
p99 latency rises after
5xx rises last
```

Lesson:

```text
Good metrics show the failure chain.
```

---

### Failure Story 3: High-cardinality labels killed Prometheus

Developer adds:

```text
shortCode as metric label
```

Result:

```text
millions of time series
Prometheus memory high
scrape duration high
query timeout
```

Fix:

```text
Remove high-cardinality labels.
Use logs for shortCode-specific search.
```

Lesson:

```text
Metrics are not logs.
```

---

### Failure Story 4: Alert storm

Team creates alerts for:

```text
CPU > 60%
memory > 60%
any 404
any cache miss
any Kafka retry
```

Result:

```text
Too many alerts.
Engineers ignore alerts.
Real incident gets missed.
```

Fix:

```text
Alert on user-impact symptoms and sustained root-cause signals.
```

Lesson:

```text
A noisy alert is worse than no alert.
```

---

### Failure Story 5: Average latency looked fine

Dashboard showed:

```text
average latency = 40ms
```

But users complained.

Actual:

```text
p50 = 20ms
p95 = 300ms
p99 = 2s
```

Average hid the tail.

Fix:

```text
Dashboard p50, p95, p99.
Alert on p99 for redirect path.
```

Lesson:

```text
For user experience, percentiles beat averages.
```

---

## 25. Debugging Mindset

When production is slow, ask in this order:

```text
1. Is traffic higher than normal?
2. Is error rate higher than normal?
3. Is p95/p99 latency higher than normal?
4. Which endpoint is affected?
5. Is Redis hit ratio normal?
6. Is DB pool saturated?
7. Is Kafka lag growing?
8. Is JVM GC pausing?
9. Is CPU/memory saturated?
10. Did deployment happen recently?
```

Debug map:

```text
High p99 + low cache hit ratio:
    Redis/cache issue

High p99 + Hikari pending:
    DB pool/query bottleneck

High p99 + GC pause:
    JVM memory pressure

Normal redirect latency + Kafka lag:
    async analytics issue

High 5xx + DB timeout logs:
    DB availability/pool issue

High 404 but low 5xx:
    user/client invalid short codes, not server failure
```

Prometheus-first workflow:

```text
1. Start with dashboard.
2. Identify affected endpoint.
3. Check dependency metrics.
4. Check logs for exact exceptions.
5. Check traces if cross-service latency unclear.
6. Apply fix.
7. Confirm metrics return to baseline.
```

Golden rule:

```text
Do not debug production only from intuition.
Follow the metrics chain.
```

---

## 26. Common Mistakes

### Mistake 1: Using shortCode as label

Wrong:

```text
url_redirects_total{shortCode="abc123"}
```

Correct:

```text
url_redirects_total{result="success",source="cache"}
```

### Mistake 2: Only average latency

Wrong:

```text
avg latency = healthy
```

Correct:

```text
p95 and p99 latency show tail pain
```

### Mistake 3: Too many custom metrics

Wrong:

```text
Create metric for every tiny internal step.
```

Correct:

```text
Start with golden signals + key dependencies + business counters.
```

### Mistake 4: No alert action

Wrong:

```text
Alert: Something is weird.
```

Correct:

```text
Alert includes symptom, threshold, duration, and runbook direction.
```

### Mistake 5: Missing endpoint grouping

Wrong:

```text
Every real URL path becomes a different metric uri.
```

Correct:

```text
Use templated URI: /{shortCode}
```

### Mistake 6: Confusing counter value with rate

Wrong:

```text
http_requests_total is 1,000,000, so RPS is high.
```

Correct:

```text
rate(http_requests_total[1m]) shows current RPS.
```

### Mistake 7: Alerting on every 404

Wrong:

```text
404 occurred -> page engineer
```

Correct:

```text
404 may be normal. Alert only if abnormal ratio or attack pattern.
```

### Mistake 8: Exposing actuator publicly

Wrong:

```text
/actuator/prometheus open to internet
```

Correct:

```text
Restrict actuator endpoints to internal network or protected path.
```

---

## 27. Interview-Ready Explanation

If interviewer asks:

```text
How would you add Prometheus metrics to your URL shortener?
```

Strong answer:

```text
I would expose Spring Boot Actuator metrics through Micrometer and the Prometheus registry at /actuator/prometheus. Prometheus would scrape each service instance periodically and store the metrics as time series. My first dashboard would focus on the four golden signals: traffic, error rate, latency, and saturation. For the redirect endpoint, I would track RPS, 5xx ratio, p95/p99 latency, cache hit ratio, Redis errors, HikariCP active and pending connections, and JVM GC pauses. For business metrics, I would add counters such as url_redirects_total with low-cardinality labels like result=success/not_found/expired/blocked and source=cache/database/none. I would avoid labels like shortCode, longUrl, userId, or requestId because they create high cardinality and can overload Prometheus. Alerts would be based on sustained user impact, such as high redirect p99 latency, high 5xx ratio, low cache hit ratio, DB pool saturation, or growing Kafka consumer lag. During incidents, metrics would identify the failing layer, logs would provide exact errors, and traces would explain cross-service latency.
```

Why this is strong:

```text
1. Mentions Actuator + Micrometer + Prometheus.
2. Starts with golden signals.
3. Includes URL shortener-specific metrics.
4. Understands Redis, DB, Kafka, JVM health.
5. Avoids high-cardinality labels.
6. Uses p95/p99 instead of average only.
7. Connects dashboards to alerts and debugging.
8. Shows production incident thinking.
```

Senior one-liner:

```text
Prometheus gives the production nervous system: it continuously measures traffic, errors, latency, and saturation so we can detect and debug failures before users suffer badly.
```

---

## 28. Senior Engineer Checklist

Before calling metrics production-ready, confirm:

```text
[ ] spring-boot-starter-actuator added
[ ] micrometer-registry-prometheus added
[ ] /actuator/prometheus enabled
[ ] actuator endpoint protected from public internet
[ ] Prometheus scrape config works
[ ] target status is UP
[ ] HTTP metrics visible
[ ] JVM metrics visible
[ ] HikariCP metrics visible
[ ] Redis metrics or custom cache metrics visible
[ ] Kafka producer/consumer metrics visible
[ ] custom business counters added
[ ] no high-cardinality labels
[ ] no shortCode/userId/longUrl/requestId labels
[ ] redirect RPS dashboard exists
[ ] create URL RPS dashboard exists
[ ] p95 and p99 latency dashboards exist
[ ] 5xx error ratio dashboard exists
[ ] cache hit ratio dashboard exists
[ ] DB pool dashboard exists
[ ] Kafka lag dashboard exists
[ ] JVM heap/GC dashboard exists
[ ] alerts have thresholds and durations
[ ] alerts are actionable
[ ] runbook links or notes exist
[ ] dashboards are checked after deployment
```

If these are checked, your metrics layer is production-shaped.

---

## 29. One-Page Cheat Sheet

```text
Core mental model:
Prometheus metrics are numeric time-series memory.
Prometheus repeatedly asks your app: what are your numbers now?

Prometheus flow:
App exposes /actuator/prometheus
Prometheus scrapes endpoint
Prometheus stores samples
Grafana visualizes
Alertmanager alerts

Metric types:
Counter   -> only goes up       -> requests/errors/redirects
Gauge     -> up and down        -> active connections/lag/memory
Histogram -> bucketed samples   -> latency percentiles

Golden signals:
Traffic
Errors
Latency
Saturation

URL shortener key metrics:
redirect RPS
create URL RPS
5xx ratio
redirect p95/p99
cache hit ratio
DB pool active/pending
Kafka consumer lag
JVM heap/GC

Good labels:
method
uri template
status
result
source

Bad labels:
shortCode
longUrl
userId
email
requestId

Important PromQL:
RPS:
sum(rate(http_server_requests_seconds_count[1m]))

5xx ratio:
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
/
sum(rate(http_server_requests_seconds_count[5m]))

p99:
histogram_quantile(0.99, sum(rate(http_server_requests_seconds_bucket[5m])) by (le))

cache hit ratio:
sum(rate(url_cache_access_total{result="hit"}[5m]))
/
sum(rate(url_cache_access_total[5m]))

Production rules:
Use p95/p99, not only average.
Use low-cardinality labels.
Alert on sustained user impact.
Metrics identify problem layer.
Logs explain exact event.
Traces show request journey.
```

---

## 30. One Picture To Remember

```text
                     PROMETHEUS METRICS MENTAL MODEL

                             "What are your numbers?"

+------------------+       request        +---------------------------+
| Client           | -------------------> | MiniURLShortener App     |
+------------------+                      |                           |
                                          | redirect/create APIs      |
                                          | Redis / DB / Kafka calls  |
                                          | JVM runtime               |
                                          +-------------+-------------+
                                                        |
                                                        | Micrometer records
                                                        v
                                          +---------------------------+
                                          | Metrics Registry          |
                                          | counters/gauges/timers    |
                                          +-------------+-------------+
                                                        |
                                                        | exposed as text
                                                        v
                                          +---------------------------+
                                          | /actuator/prometheus      |
                                          +-------------+-------------+
                                                        ^
                                                        |
                                      scrape every 15s  |
                                                        |
+------------------+       query          +-------------+-------------+
| Grafana          | <------------------- | Prometheus Server        |
| dashboards       |                      | time-series database     |
+------------------+                      +-------------+-------------+
                                                        |
                                                        | evaluates rules
                                                        v
                                          +---------------------------+
                                          | Alerts                    |
                                          | p99 high, 5xx high, lag   |
                                          +---------------------------+

FINAL MEMORY:

Metrics show health.
Logs show events.
Traces show journeys.
Prometheus stores numeric time-series.
Good labels save Prometheus; bad labels destroy it.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Prometheus metrics are numeric time-series data scraped from your application.
2. Spring Boot exposes metrics through Actuator and Micrometer at /actuator/prometheus.
3. The first production dashboard should show traffic, errors, latency, and saturation.
4. URL shortener-specific metrics include redirect RPS, p99 latency, cache hit ratio, DB pool usage, Kafka lag, and JVM health.
5. Never use high-cardinality labels like shortCode, longUrl, userId, or requestId in Prometheus metrics.
```

After this chapter, the observability phase can answer:

```text
Is the system healthy?
Where is it slow?
Which dependency is saturated?
Is the cache protecting the database?
Is async analytics falling behind?
Did the last deployment hurt production?
```

Next possible chapters:

```text
058_Grafana_Dashboards.md
059_Alertmanager_Alerts.md
060_Distributed_Tracing_OpenTelemetry.md
061_Production_Incident_Debugging.md
```
