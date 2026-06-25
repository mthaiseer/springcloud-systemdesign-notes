# 062_Performance_Tuning.md
# MiniURLShortener — Performance Tuning

> Core mental model: **Performance tuning is not random optimization. It is a bottleneck hunt. Measure the request path, find the slowest constrained resource, fix one bottleneck, verify with load tests, and repeat.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Performance Tuning Is A Feedback Loop](#4-performance-tuning-is-a-feedback-loop)
- [5. Latency vs Throughput vs Saturation](#5-latency-vs-throughput-vs-saturation)
- [6. URL Shortener Hot Paths](#6-url-shortener-hot-paths)
- [7. Bottleneck Map](#7-bottleneck-map)
- [8. Baseline Before Tuning](#8-baseline-before-tuning)
- [9. JVM And Spring Boot Tuning](#9-jvm-and-spring-boot-tuning)
- [10. Tomcat Thread Pool Tuning](#10-tomcat-thread-pool-tuning)
- [11. HikariCP Connection Pool Tuning](#11-hikaricp-connection-pool-tuning)
- [12. PostgreSQL Query And Index Tuning](#12-postgresql-query-and-index-tuning)
- [13. Redis Cache Tuning](#13-redis-cache-tuning)
- [14. Kafka Producer And Consumer Tuning](#14-kafka-producer-and-consumer-tuning)
- [15. HTTP Client And Downstream Tuning](#15-http-client-and-downstream-tuning)
- [16. Serialization And Payload Tuning](#16-serialization-and-payload-tuning)
- [17. Logging Tuning](#17-logging-tuning)
- [18. Kubernetes Resource Tuning](#18-kubernetes-resource-tuning)
- [19. Autoscaling Tuning](#19-autoscaling-tuning)
- [20. Database Connection Storm Problem](#20-database-connection-storm-problem)
- [21. Step-by-Step Dry Runs](#21-step-by-step-dry-runs)
- [22. Internal Execution Walkthrough](#22-internal-execution-walkthrough)
- [23. Production Tuning Playbook](#23-production-tuning-playbook)
- [24. Performance Anti-Patterns](#24-performance-anti-patterns)
- [25. Testing Strategy](#25-testing-strategy)
- [26. Production Failure Stories](#26-production-failure-stories)
- [27. Debugging Mindset](#27-debugging-mindset)
- [28. Interview-Ready Explanation](#28-interview-ready-explanation)
- [29. Senior Engineer Checklist](#29-senior-engineer-checklist)
- [30. One-Page Cheat Sheet](#30-one-page-cheat-sheet)
- [31. One Picture To Remember](#31-one-picture-to-remember)

---

## 1. Why This Exists

By now MiniURLShortener has many production parts:

```text
Spring Boot API
PostgreSQL
Redis cache
Kafka click analytics
Docker / Kubernetes
Prometheus metrics
Grafana dashboards
Distributed tracing
SLO / SLA / error budget
Load testing with k6
```

But after adding all these parts, the system still needs one crucial skill:

```text
How do we make it faster without guessing?
```

Bad tuning looks like this:

```text
increase all thread pools
increase all DB connections
add more pods
add Redis everywhere
add bigger machines
hope latency improves
```

Good tuning looks like this:

```text
measure baseline
find bottleneck
change one thing
load test again
compare p50 / p95 / p99 / error rate
keep or revert
repeat
```

Production reality:

```text
Performance tuning is not about making every number bigger.
Performance tuning is about balancing the whole request path.
```

For URL shortener:

```text
Redirect API must be extremely fast.
Create API must be reliable and safe.
Analytics should not slow redirects.
Database should not be hit for every redirect.
Logs should not become the bottleneck.
Kafka should absorb click events asynchronously.
```

---

## 2. The One Core Mental Model

Performance tuning is a:

```text
BOTTLENECK HUNT
```

A request is like water flowing through pipes.

The narrowest pipe controls the total flow.

ASCII:

```text
Client
  |
  v
+---------+     +---------+     +---------+     +---------+
| Nginx   | --> | Spring  | --> | Redis   | --> | Postgres|
|  wide   |     | medium  |     | wide    |     | narrow? |
+---------+     +---------+     +---------+     +---------+
                                                       |
                                                       v
                                             bottleneck controls speed
```

If PostgreSQL is saturated, increasing Tomcat threads may make the system worse.

```text
More threads -> more DB pressure -> more queueing -> higher p99 -> more timeouts
```

Senior mental model:

```text
Tune the bottleneck, not your favorite technology.
```

One-line memory:

```text
Measure first, tune one bottleneck, verify with load test.
```

---

## 3. Problem Statement

Build a production performance tuning model for MiniURLShortener.

It must cover:

```text
1. Redirect API latency tuning.
2. Create API throughput tuning.
3. Spring Boot thread tuning.
4. HikariCP connection pool tuning.
5. PostgreSQL index/query tuning.
6. Redis cache tuning.
7. Kafka analytics tuning.
8. Logging overhead tuning.
9. Kubernetes CPU/memory tuning.
10. Autoscaling and connection storm control.
```

It should answer:

```text
Why is p99 high?
Why is CPU high?
Why are DB connections exhausted?
Why did Redis not improve performance?
Why did adding pods make DB worse?
Why are Kafka consumers lagging?
Why does everything look fine at p50 but bad at p99?
```

Out of scope:

```text
1. Full JVM garbage collector textbook.
2. Deep PostgreSQL internals.
3. Deep Kafka broker administration.
4. Cloud cost optimization.
```

This chapter gives the production debugging and tuning mindset.

---

## 4. Performance Tuning Is A Feedback Loop

Never tune blindly.

Use this loop:

```text
1. Baseline
2. Observe
3. Hypothesize
4. Change one thing
5. Load test
6. Compare
7. Keep or revert
```

ASCII:

```text
+----------+
| Baseline |
+----------+
     |
     v
+----------+
| Observe  |
+----------+
     |
     v
+------------+
| Hypothesis |
+------------+
     |
     v
+-------------+
| One Change  |
+-------------+
     |
     v
+-----------+
| Load Test |
+-----------+
     |
     v
+----------+
| Compare  |
+----------+
     |
     +---- improved? ---- yes ---> keep
     |
     +---- worse? ------- yes ---> revert
```

Example:

```text
Observation:
    p99 redirect latency = 480ms
    DB CPU = 92%
    Redis hit rate = 35%

Hypothesis:
    Too many redirects miss Redis and hit DB.

Change:
    Fix cache warming and TTL policy.

Verification:
    Redis hit rate 35% -> 92%
    DB CPU 92% -> 40%
    p99 480ms -> 80ms
```

Important rule:

```text
If you change 5 things at once, you do not know which one helped.
```

---

## 5. Latency vs Throughput vs Saturation

Performance has three main dimensions.

### Latency

```text
How long one request takes.
```

Example:

```text
Redirect p99 = 90ms
```

### Throughput

```text
How many requests the system handles per second.
```

Example:

```text
10,000 redirects per second
```

### Saturation

```text
How full the system resources are.
```

Example:

```text
CPU 85%
DB connections 100%
Kafka lag growing
Tomcat threads busy
Redis memory 90%
```

ASCII:

```text
          Throughput increases
                  |
                  v
Latency low  --------------------> Latency explodes
                  saturation point
```

Before saturation:

```text
system accepts more load
latency grows slowly
```

After saturation:

```text
queues grow
timeouts happen
p99 explodes
errors rise
```

Golden production metric set:

```text
RPS
p50 / p95 / p99 latency
error rate
CPU
memory
GC pauses
Tomcat busy threads
Hikari active/pending connections
Postgres CPU / slow queries
Redis hit rate / latency
Kafka producer error / consumer lag
```

---

## 6. URL Shortener Hot Paths

Not all APIs have equal performance requirements.

### Redirect API

```http
GET /{shortCode}
```

This is the hottest path.

It should be:

```text
read-heavy
cache-first
very low latency
minimal DB access
non-blocking analytics path
```

ASCII:

```text
GET /abc123
   |
   v
Redis lookup
   |
   +-- hit --> return 302 quickly
   |
   +-- miss -> DB lookup -> cache fill -> return 302
   |
   v
Kafka click event async
```

### Create API

```http
POST /api/v1/urls
```

This path is less frequent but needs correctness.

It should be:

```text
validated
transactional
unique short_code safe
not over-optimized before correctness
```

ASCII:

```text
POST longUrl
   |
   v
validate -> generate code -> insert DB -> cache optional -> return 201
```

Performance priority:

```text
Redirect API > Create API > Analytics worker
```

Because users and browsers hit redirects far more than creates.

---

## 7. Bottleneck Map

A slow request can be slow in many places.

```text
Client network
Load balancer
Nginx
Spring Boot threads
Hikari pool
PostgreSQL query
Redis latency
Kafka producer
Logging
Garbage collection
CPU throttling
```

ASCII request map:

```text
+--------+
| Client |
+--------+
    |
    v
+-------------+
| LoadBalancer|
+-------------+
    |
    v
+-------+
| Nginx |
+-------+
    |
    v
+----------------------+
| Spring Boot / Tomcat |
| threads, CPU, GC     |
+----------------------+
    |          |
    |          +----------------+
    |                           |
    v                           v
+--------+                +-----------+
| Redis  |                | Kafka     |
+--------+                +-----------+
    |
    v
+------------+
| PostgreSQL |
+------------+
```

Bottleneck symptoms:

```text
High Tomcat busy threads:
    app is waiting or CPU-bound

High Hikari pending threads:
    DB connection pool exhausted

High Postgres CPU:
    slow queries, missing indexes, too much DB traffic

High Redis latency:
    network issue, hot key, big payload, CPU saturation

High Kafka lag:
    consumers slower than producers

High GC pauses:
    memory allocation pressure

High CPU throttling in Kubernetes:
    CPU limit too low
```

---

## 8. Baseline Before Tuning

Baseline means current performance before changes.

Record:

```text
commit hash
config values
pod count
CPU/memory limits
DB size
Redis state
Kafka partitions
load test script
RPS target
latency numbers
error rate
```

Example baseline table:

```text
+--------------------------+----------------+
| Metric                   | Baseline       |
+--------------------------+----------------+
| Redirect RPS             | 3,000          |
| Redirect p50             | 18ms           |
| Redirect p95             | 140ms          |
| Redirect p99             | 620ms          |
| Error rate               | 1.8%           |
| Redis hit rate           | 42%            |
| Postgres CPU             | 88%            |
| Hikari active connections| 30/30          |
| Hikari pending threads   | high           |
+--------------------------+----------------+
```

This baseline suggests:

```text
DB is overloaded.
Connection pool is maxed.
Redis hit rate is too low.
```

Do not start by increasing Hikari blindly.

Better first question:

```text
Why are so many redirects hitting DB?
```

---

## 9. JVM And Spring Boot Tuning

For Spring Boot, JVM tuning starts with observability.

Track:

```text
heap used
heap max
GC pause duration
GC frequency
thread count
CPU usage
allocation rate
```

Common JVM issue:

```text
p99 latency spikes every few seconds
```

Possible cause:

```text
GC pause
large allocations
too many temporary objects
big JSON payloads
excessive logging strings
```

Basic production JVM flags example:

```bash
java \
  -XX:MaxRAMPercentage=75 \
  -XX:+UseG1GC \
  -XX:+ExitOnOutOfMemoryError \
  -jar app.jar
```

Mental model:

```text
Heap too small:
    frequent GC, latency spikes

Heap too large:
    longer GC pauses, wasted memory, fewer pods per node

No memory limit awareness:
    container OOM kills
```

Spring Boot actuator metrics to watch:

```text
jvm.memory.used
jvm.gc.pause
jvm.threads.live
process.cpu.usage
system.cpu.usage
http.server.requests
```

Do not tune GC first unless metrics show GC is the bottleneck.

Rule:

```text
Most web API performance problems are DB, network, thread, or connection problems before they are GC problems.
```

---

## 10. Tomcat Thread Pool Tuning

Tomcat threads process HTTP requests.

Config:

```yaml
server:
  tomcat:
    threads:
      max: 200
      min-spare: 20
    accept-count: 100
```

Mental model:

```text
Tomcat thread = worker standing at API counter
```

ASCII:

```text
Incoming requests
       |
       v
+-------------------+
| Tomcat queue      |
+-------------------+
       |
       v
+-------------------+
| Worker threads    |
| max = 200         |
+-------------------+
       |
       v
Controller / Service
```

If max threads too low:

```text
requests queue early
throughput limited
CPU may be underused
```

If max threads too high:

```text
too many concurrent requests
DB pool pressure
context switching
memory pressure
p99 worse
```

Important relationship:

```text
Tomcat max threads should not blindly exceed what downstream resources can handle.
```

Example bad config:

```text
Tomcat threads = 500
Hikari pool = 20
```

Result:

```text
500 requests enter app
20 get DB connections
480 wait
p99 explodes
```

Better:

```text
Tomcat threads sized with CPU and blocking profile.
Hikari sized with DB capacity.
Rate limiting protects overload.
```

---

## 11. HikariCP Connection Pool Tuning

HikariCP controls database connections from Spring Boot to PostgreSQL.

Config:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 30
      minimum-idle: 10
      connection-timeout: 2000
      idle-timeout: 600000
      max-lifetime: 1800000
```

Mental model:

```text
DB connections are scarce checkout lanes.
More checkout lanes help only until the store staff cannot handle more.
```

ASCII:

```text
Spring requests
    |
    v
+-----------------------+
| Hikari pool           |
| connections = 30      |
+-----------------------+
    |
    v
+-----------------------+
| PostgreSQL            |
| can only do so much   |
+-----------------------+
```

Bad assumption:

```text
More DB connections always means more throughput.
```

Reality:

```text
Too many connections can increase DB context switching, lock contention, memory usage, and latency.
```

Watch these metrics:

```text
hikaricp.connections.active
hikaricp.connections.idle
hikaricp.connections.pending
hikaricp.connections.timeout
```

Interpretation:

```text
active near max + pending high:
    pool is saturated

active low + pending high:
    possible connection leak or slow acquisition

timeouts increasing:
    app cannot get DB connections fast enough
```

For URL shortener redirect path:

```text
High Redis hit rate should reduce DB connection usage.
```

If Redis hit rate is 95% but Hikari still maxed:

```text
check cache bypass
check create API burst
check analytics worker hitting DB
check health checks or background jobs
```

---

## 12. PostgreSQL Query And Index Tuning

For redirect, most important query:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?;
```

Required index:

```sql
CREATE UNIQUE INDEX uk_short_urls_short_code
ON short_urls(short_code);
```

Without index:

```text
Postgres scans many rows.
Latency grows with table size.
```

With index:

```text
Postgres jumps directly to matching short_code.
```

ASCII:

```text
Without index:
abc123?  row1 -> row2 -> row3 -> ... -> row900000

With index:
abc123 -> index lookup -> exact row
```

Use EXPLAIN:

```sql
EXPLAIN ANALYZE
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = 'abc123';
```

Good sign:

```text
Index Scan using uk_short_urls_short_code
```

Bad sign:

```text
Seq Scan on short_urls
```

Other DB tuning ideas:

```text
select only needed columns
avoid N+1 queries
avoid loading full entity when projection is enough
keep transactions short
avoid long locks
batch writes where safe
use read replicas for read-heavy paths later
```

Spring Data projection example:

```java
public interface RedirectProjection {
    String getLongUrl();
    String getStatus();
    Instant getExpiresAt();
}
```

Repository:

```java
Optional<RedirectProjection> findByShortCode(String shortCode);
```

Why projection helps:

```text
less data from DB
less object mapping
less memory allocation
faster hot path
```

---

## 13. Redis Cache Tuning

Redis is used to avoid DB hit on redirect.

Cache-aside flow:

```text
GET /abc123
   |
   v
Redis GET short:abc123
   |
   +-- hit  -> return longUrl
   |
   +-- miss -> DB lookup -> Redis SET -> return longUrl
```

ASCII:

```text
             +-----------------+
             | Redis cache     |
             | hot redirects   |
             +-----------------+
                ^          |
                | miss     | hit
                |          v
Client -> Spring Boot -> 302 Redirect
                |
                v
          PostgreSQL only on miss
```

Important Redis metrics:

```text
cache hit rate
GET latency
SET latency
evictions
memory usage
connected clients
network latency
hot keys
```

Bad cache tuning:

```text
very short TTL -> too many misses
very long TTL -> stale blocked/expired links
large values -> network and memory overhead
no negative caching -> repeated DB hits for unknown codes
```

Example key design:

```text
url:redirect:abc123 -> {longUrl,status,expiresAt}
```

TTL strategy:

```text
If link expires in 2 hours:
    Redis TTL <= 2 hours

If permanent link:
    Redis TTL = 1 day or more depending on invalidation strategy

If code not found:
    negative cache for 30-60 seconds
```

Negative cache mental model:

```text
Unknown short codes can become DB attack traffic.
Short negative TTL protects DB from repeated misses.
```

ASCII:

```text
Attacker requests random codes
        |
        v
Redis negative cache
        |
        +-- repeated random code -> quick 404
        |
        +-- first time -> DB check once
```

---

## 14. Kafka Producer And Consumer Tuning

Redirect should not wait for analytics processing.

Bad flow:

```text
GET /abc123 -> write click to DB synchronously -> return redirect
```

Good flow:

```text
GET /abc123 -> publish click event to Kafka -> return redirect
consumer processes later
```

ASCII:

```text
Redirect request
     |
     v
Resolve longUrl
     |
     +-------> Kafka click event -------> Analytics Worker
     |
     v
Return 302 quickly
```

Producer tuning examples:

```yaml
spring:
  kafka:
    producer:
      properties:
        linger.ms: 5
        batch.size: 32768
        compression.type: snappy
        acks: 1
```

Meaning:

```text
linger.ms:
    wait briefly to batch messages

batch.size:
    larger batch can improve throughput

compression:
    reduce network cost

acks:
    durability vs latency tradeoff
```

Consumer tuning examples:

```yaml
spring:
  kafka:
    listener:
      concurrency: 3
    consumer:
      properties:
        max.poll.records: 500
```

Watch:

```text
producer send latency
producer error rate
consumer lag
records consumed per second
DLQ count
retry topic count
```

Important tradeoff:

```text
Do not make redirect latency depend on slow analytics.
```

If Kafka is temporarily slow:

```text
Option 1: fail redirect? bad user experience
Option 2: drop analytics? maybe acceptable for non-critical clicks
Option 3: buffer locally? risky under memory pressure
```

Senior answer:

```text
Analytics is eventually consistent and should not block redirect availability.
```

---

## 15. HTTP Client And Downstream Tuning

MiniURLShortener may later call downstream services:

```text
malware scanner
phishing detector
billing service
user service
admin service
```

Never call downstream without timeouts.

Bad:

```text
HTTP call waits forever
Tomcat thread blocked
p99 explodes
```

Good:

```text
connect timeout
read timeout
retry with backoff for safe operations
circuit breaker
bulkhead
fallback where acceptable
```

Example WebClient timeout idea:

```java
HttpClient httpClient = HttpClient.create()
        .responseTimeout(Duration.ofMillis(500));
```

Mental model:

```text
Every downstream call borrows your request thread budget.
```

ASCII:

```text
Request thread
    |
    v
Downstream call slow
    |
    v
Thread waits
    |
    v
More requests queue
    |
    v
p99 and errors rise
```

Rule:

```text
No timeout means infinite latency risk.
```

---

## 16. Serialization And Payload Tuning

Serialization can matter on hot paths.

For redirect:

```text
Response should usually be 302 with Location header.
No large JSON body needed.
```

For create:

```json
{
  "shortCode": "abc123",
  "shortUrl": "https://sho.rt/abc123",
  "longUrl": "https://example.com/article",
  "expiresAt": null
}
```

Avoid:

```text
returning full entity with internal fields
returning debug data
serializing lazy relations
large nested JSON
```

Bad JPA pattern:

```java
return shortUrlEntity;
```

Better:

```java
return CreateShortUrlResponse.from(entity);
```

Why:

```text
DTO controls payload size.
DTO avoids lazy-loading surprises.
DTO prevents leaking internal columns.
```

---

## 17. Logging Tuning

Logging can become a hidden performance bottleneck.

Bad hot path logging:

```java
log.info("Redirect request shortCode={} longUrl={}", shortCode, longUrl);
```

Problems:

```text
too many logs at high RPS
I/O pressure
log shipping cost
sensitive URL exposure
higher latency
```

Better:

```java
log.debug("Redirect resolved shortCode={}", shortCode);
```

For errors:

```java
log.warn("Redirect not found shortCode={} correlationId={}", shortCode, correlationId);
```

For unexpected errors:

```java
log.error("Unexpected redirect error correlationId={}", correlationId, ex);
```

Production logging rules:

```text
Do not log every successful redirect at INFO.
Do log errors and slow requests.
Use structured logs.
Sample noisy events.
Avoid full long URLs with secrets.
```

ASCII:

```text
High RPS + INFO logs every request
        |
        v
Log queue grows
        |
        v
CPU/I/O used by logging
        |
        v
Application latency rises
```

---

## 18. Kubernetes Resource Tuning

Kubernetes resources affect performance strongly.

Example:

```yaml
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
CPU request = scheduling guarantee
CPU limit = throttle ceiling
Memory limit = kill boundary
```

CPU throttling problem:

```text
App wants CPU.
Container hits CPU limit.
Kernel throttles it.
p99 latency spikes.
CPU graph may look not fully used.
```

Watch:

```text
container_cpu_cfs_throttled_seconds_total
container_memory_working_set_bytes
pod restarts
OOMKilled
```

For Java apps:

```text
Memory limit must leave room for heap + metaspace + thread stacks + direct buffers.
```

Bad config:

```text
memory limit = 512Mi
heap max = 512Mi
```

Why bad:

```text
JVM needs non-heap memory too.
Pod may be OOMKilled.
```

Better:

```text
memory limit = 1024Mi
MaxRAMPercentage = 70-75
```

---

## 19. Autoscaling Tuning

HPA can add pods, but more pods are not always better.

Simple HPA:

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: miniurl-api-hpa
spec:
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
```

ASCII:

```text
Traffic rises
    |
    v
CPU rises
    |
    v
HPA adds pods
    |
    v
More app capacity
```

But hidden risk:

```text
Each pod has Hikari pool = 30.
10 pods = 300 possible DB connections.
Postgres max safe connections maybe 120.
```

Autoscaling can create DB overload.

Correct thinking:

```text
Total DB connections = pod count * Hikari max pool size
```

Example:

```text
max pods = 10
Hikari max = 20
Total possible DB connections = 200
```

If DB cannot handle 200:

```text
reduce Hikari pool
use PgBouncer
increase DB capacity
use Redis/read replicas
limit max pods
```

Senior mental model:

```text
Autoscaling the application tier can move the bottleneck to the database tier.
```

---

## 20. Database Connection Storm Problem

Connection storm happens when many pods open many DB connections.

Scenario:

```text
traffic spike
HPA scales 3 pods -> 12 pods
each pod opens 30 Hikari connections
DB sees 360 connections
Postgres slows down
p99 explodes
```

ASCII:

```text
Pod1  ----30 conn----+
Pod2  ----30 conn----+
Pod3  ----30 conn----+
...                  +----> PostgreSQL overwhelmed
Pod12 ----30 conn----+
```

Symptoms:

```text
DB CPU high
DB memory high
connection acquisition slow
Hikari pending high
Postgres max_connections near limit
errors: too many clients
```

Fixes:

```text
1. Calculate total possible connections.
2. Reduce Hikari maximum-pool-size per pod.
3. Add PgBouncer for pooling.
4. Increase DB capacity if needed.
5. Improve cache hit rate.
6. Add read replicas for read-heavy queries.
7. Avoid scaling app beyond DB capacity.
```

Formula:

```text
total_app_connections = max_pods * hikari_max_pool_size
```

Example safe planning:

```text
Postgres safe app connections: 100
Max API pods: 5
Hikari max per pod: 20
Total = 100
```

---

## 21. Step-by-Step Dry Runs

### Dry Run 1: Redis Hit Redirect

Request:

```http
GET /abc123
```

Flow:

```text
1. Request enters Spring Boot.
2. Controller validates shortCode shape.
3. Service checks Redis key url:redirect:abc123.
4. Redis returns longUrl and metadata.
5. Service checks status and expiry.
6. App publishes click event to Kafka asynchronously.
7. Response returns 302 Location quickly.
8. PostgreSQL is not touched.
```

Performance result:

```text
low latency
low DB CPU
low Hikari usage
```

---

### Dry Run 2: Redis Miss Redirect

Flow:

```text
1. Redis key missing.
2. Service queries PostgreSQL by short_code.
3. PostgreSQL uses unique index.
4. Service caches result in Redis.
5. Response returns 302.
6. Next request becomes Redis hit.
```

If p99 is high here, check:

```text
DB query plan
Hikari pending
DB CPU
Redis SET latency
network latency
```

---

### Dry Run 3: Missing Index

Table:

```text
short_urls has 10 million rows
```

Query:

```sql
SELECT long_url FROM short_urls WHERE short_code = 'abc123';
```

Without index:

```text
Postgres scans many rows.
CPU rises.
DB latency rises.
Hikari connections stay busy longer.
p99 rises.
```

Fix:

```sql
CREATE UNIQUE INDEX uk_short_urls_short_code ON short_urls(short_code);
```

Verify:

```text
EXPLAIN ANALYZE shows Index Scan.
```

---

### Dry Run 4: Too Many Tomcat Threads

Config:

```text
Tomcat max threads = 500
Hikari max pool = 30
```

Traffic spike:

```text
400 concurrent requests enter app.
30 get DB connections.
370 wait.
```

Result:

```text
p99 explodes
request timeout
connection timeout
CPU context switching
```

Fix options:

```text
raise cache hit rate
control concurrency
right-size Tomcat and Hikari
add rate limiting
scale DB/read path carefully
```

---

### Dry Run 5: Kafka Consumer Lag

Observation:

```text
Redirect API is fast.
Kafka lag is growing.
Analytics dashboard delayed by 20 minutes.
```

Meaning:

```text
Producer is faster than consumer.
```

Fix options:

```text
increase partitions
increase consumer concurrency
batch writes to analytics DB
optimize consumer DB writes
avoid one-message-one-transaction if too slow
```

Important:

```text
Do not slow redirect API just because analytics is lagging.
```

---

## 22. Internal Execution Walkthrough

Redirect performance path:

```text
1. Client sends GET /abc123.
2. Load balancer routes request to a pod.
3. Tomcat worker thread accepts request.
4. Controller parses shortCode.
5. Service performs Redis GET.
6. If cache hit, service avoids DB.
7. If cache miss, service borrows Hikari connection.
8. PostgreSQL uses index on short_code.
9. Service returns redirect metadata.
10. Kafka producer receives click event.
11. HTTP response returns 302.
12. Tomcat thread becomes free.
```

ASCII:

```text
Client
  |
  v
Tomcat thread acquired
  |
  v
Redis GET
  |
  +-- hit --------------------+
  |                           |
  +-- miss -> Hikari -> DB ---+
                              |
                              v
                      Build 302 response
                              |
                              +--> Kafka click event
                              |
                              v
                         thread released
```

Where latency can hide:

```text
Tomcat queue wait
Redis network wait
Hikari connection wait
DB query wait
Kafka send wait
logging wait
GC pause
CPU throttling
```

To tune, identify which wait dominates.

---

## 23. Production Tuning Playbook

### Case 1: High p99, low CPU

Possible causes:

```text
thread waiting
DB connection pool wait
downstream latency
lock contention
network delay
```

Check:

```text
Hikari pending
Tomcat busy threads
trace spans
DB slow queries
Redis latency
```

### Case 2: High p99, high DB CPU

Possible causes:

```text
missing index
cache miss storm
slow query
too many connections
analytics writes competing with reads
```

Fix:

```text
EXPLAIN ANALYZE
improve Redis hit rate
add index
separate analytics DB path
reduce DB connections if overloaded
```

### Case 3: High p99, high app CPU

Possible causes:

```text
serialization overhead
excessive logging
regex heavy validation
GC pressure
crypto/compression overhead
```

Fix:

```text
profile CPU
reduce payload
reduce hot path logs
use DTO projections
check GC metrics
```

### Case 4: Error rate rises during load test

Possible causes:

```text
connection timeout
request timeout
rate limit
pod OOMKilled
DB too many clients
Kafka send errors
```

Check:

```text
HTTP status distribution
app logs
pod restarts
Hikari timeout metrics
DB connection count
Kafka producer errors
```

### Case 5: Adding pods made latency worse

Possible cause:

```text
DB connection storm
cache cold start
Kafka producer burst
CPU throttling on nodes
```

Fix:

```text
reduce per-pod Hikari pool
warm cache
limit max pods
add PgBouncer
scale DB/read replicas
```

---

## 24. Performance Anti-Patterns

### Anti-pattern 1: Tuning without baseline

Wrong:

```text
Change config because blog said so.
```

Correct:

```text
Measure baseline first.
```

### Anti-pattern 2: Increasing every pool

Wrong:

```text
Tomcat 1000, Hikari 300, Kafka concurrency 100
```

Correct:

```text
Size each pool according to downstream capacity.
```

### Anti-pattern 3: Cache added but hit rate ignored

Wrong:

```text
We use Redis, so DB is safe.
```

Correct:

```text
Check Redis hit rate and miss behavior.
```

### Anti-pattern 4: Logging every hot request

Wrong:

```text
INFO log for every redirect.
```

Correct:

```text
Log errors, slow requests, sampled success events.
```

### Anti-pattern 5: Optimizing create API before redirect API

Wrong:

```text
Spend all tuning effort on POST /urls.
```

Correct:

```text
Tune hottest path first: GET /{shortCode}.
```

### Anti-pattern 6: Ignoring p99

Wrong:

```text
Average latency is fine.
```

Correct:

```text
Users feel tail latency. Watch p95 and p99.
```

### Anti-pattern 7: No timeout

Wrong:

```text
Downstream call can wait forever.
```

Correct:

```text
Every external call needs timeout and resilience policy.
```

---

## 25. Testing Strategy

Performance testing should be repeatable.

Test types:

```text
smoke test
baseline load test
stress test
spike test
soak test
regression test
```

For every performance change, compare:

```text
before vs after
same dataset
same RPS
same duration
same environment
same pod count
same DB size
```

Minimum k6 checks:

```text
http_req_duration p95
http_req_duration p99
http_req_failed
RPS
```

Also check system metrics:

```text
CPU
memory
GC
Tomcat threads
Hikari connections
Postgres CPU
Redis hit rate
Kafka lag
pod restarts
```

Performance regression rule:

```text
A tuning change is not accepted unless p95/p99/error rate improve or stay within budget.
```

Example acceptance gate:

```text
Redirect API:
    p95 < 100ms
    p99 < 250ms
    error rate < 0.1%
    Redis hit rate > 90%
    Hikari pending near zero
```

---

## 26. Production Failure Stories

### Failure Story 1: More Hikari connections made DB slower

Team observed:

```text
Hikari pool maxed at 30.
```

They changed:

```text
maximum-pool-size 30 -> 200
```

Result:

```text
DB CPU increased.
Query latency worsened.
p99 got worse.
```

Root cause:

```text
DB was already saturated. More connections increased contention.
```

Lesson:

```text
Pool saturation is a symptom, not always the root cause.
```

---

### Failure Story 2: Redis existed but DB still died

Observation:

```text
Redis was deployed.
DB still hit 95% CPU.
```

Root cause:

```text
Redis hit rate was only 30% because TTL was too short and keys were inconsistent.
```

Fix:

```text
standardize key format
increase TTL safely
add negative caching
monitor hit rate
```

Lesson:

```text
Having cache is not the same as using cache effectively.
```

---

### Failure Story 3: HPA caused outage

During traffic spike:

```text
pods scaled from 4 to 15
Hikari max = 30
possible DB connections = 450
```

Postgres could handle around 120 safely.

Result:

```text
connection storm
timeouts
p99 explosion
```

Fix:

```text
reduce Hikari per pod
limit HPA max pods
add PgBouncer
improve cache hit rate
```

Lesson:

```text
Autoscaling must respect downstream capacity.
```

---

### Failure Story 4: INFO logs slowed redirect API

At 10k RPS:

```text
app logged every redirect at INFO
log pipeline saturated
CPU high
latency unstable
```

Fix:

```text
remove success logs from hot path
sample logs
keep error logs
add metrics instead
```

Lesson:

```text
Metrics are for high-volume behavior. Logs are for events and debugging.
```

---

### Failure Story 5: Average latency hid bad user experience

Dashboard showed:

```text
average latency = 40ms
```

Users complained.

p99 showed:

```text
p99 latency = 2.5 seconds
```

Root cause:

```text
occasional DB connection waits and GC spikes.
```

Lesson:

```text
Average latency lies. Tail latency tells production truth.
```

---

## 27. Debugging Mindset

When performance is bad, ask in order:

```text
1. Which endpoint is slow?
2. Is p50 slow or only p99 slow?
3. Did error rate increase?
4. Which resource is saturated?
5. Are requests waiting for threads?
6. Are requests waiting for DB connections?
7. Is DB query slow or DB overloaded?
8. Is Redis hit rate high enough?
9. Is Kafka affecting the request path?
10. Are pods CPU throttled or OOMKilled?
11. Did recent deploy/config change cause it?
12. Did traffic pattern change?
```

Fast diagnosis map:

```text
p50 good, p99 bad:
    queueing, GC, connection wait, noisy neighbor, slow dependency

p50 and p99 both bad:
    core path slow, DB slow, cache ineffective, CPU overloaded

errors with Hikari timeout:
    DB pool or DB overloaded

errors with 5xx after scale-out:
    connection storm or cold cache

Kafka lag only:
    analytics worker too slow, redirect may still be fine
```

Useful commands:

```bash
kubectl top pods
kubectl describe pod miniurl-api-xxx
kubectl logs miniurl-api-xxx
kubectl get hpa
```

Useful SQL:

```sql
EXPLAIN ANALYZE
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = 'abc123';
```

Useful metrics:

```text
http.server.requests p99
hikaricp.connections.pending
jvm.gc.pause
process.cpu.usage
redis hit ratio
postgres slow queries
kafka consumer lag
container CPU throttling
```

Golden rule:

```text
Find where the request is waiting.
```

---

## 28. Interview-Ready Explanation

If interviewer asks:

```text
How would you tune performance for your URL shortener?
```

Strong answer:

```text
I would start with a baseline load test and measure p50, p95, p99, error rate,
CPU, memory, Tomcat busy threads, Hikari active and pending connections,
Postgres slow queries, Redis hit rate, and Kafka lag. I would tune the hottest
path first, which is redirect. Redirect should be cache-first with Redis, use an
indexed lookup on short_code only on cache miss, return a 302 quickly, and publish
click analytics asynchronously to Kafka so analytics does not block user latency.
I would not blindly increase Tomcat or Hikari pools because that can overload the
database. I would size thread pools and DB pools according to downstream capacity,
watch total DB connections across all pods, and use PgBouncer or read replicas if
needed. For PostgreSQL, I would verify EXPLAIN ANALYZE uses the short_code index
and avoid loading unnecessary columns. For Kubernetes, I would check CPU throttling,
OOMKills, and HPA behavior because adding pods can create a DB connection storm.
Every tuning change should be one change at a time and verified with the same k6
load test before accepting it.
```

Why this is strong:

```text
1. Starts with measurement.
2. Prioritizes hot path.
3. Mentions p95/p99 and error rate.
4. Understands Redis and DB tradeoff.
5. Understands Hikari/Tomcat relationship.
6. Understands autoscaling danger.
7. Separates analytics from redirect latency.
8. Uses verification, not guessing.
```

Senior one-liner:

```text
I tune by finding the saturated bottleneck in the request path, not by increasing every pool blindly.
```

---

## 29. Senior Engineer Checklist

Before calling the system performance-tuned, confirm:

```text
[ ] Baseline load test exists
[ ] p50 / p95 / p99 tracked
[ ] Error rate tracked
[ ] Redirect API Redis hit rate tracked
[ ] PostgreSQL short_code index exists
[ ] EXPLAIN ANALYZE verified for redirect query
[ ] Hikari active/pending/timeout metrics tracked
[ ] Tomcat busy thread metrics tracked
[ ] JVM GC pause metrics tracked
[ ] Kafka consumer lag tracked
[ ] Logging does not spam hot path
[ ] No synchronous analytics write in redirect path
[ ] Kubernetes CPU throttling checked
[ ] Memory limits leave room for JVM non-heap
[ ] HPA max pods aligned with DB capacity
[ ] total DB connections calculated
[ ] Slow query logging enabled in non-noisy way
[ ] k6 regression test run after tuning changes
[ ] Only one performance variable changed at a time
[ ] Rollback plan exists for bad tuning config
```

If these are checked, your system has a production-shaped performance tuning model.

---

## 30. One-Page Cheat Sheet

```text
Core mental model:
Performance tuning = bottleneck hunt.
Measure -> find bottleneck -> change one thing -> load test -> compare.

Hot path:
GET /{shortCode} redirect is most important.
Use Redis first, DB only on miss, Kafka analytics async.

Main metrics:
RPS
p50 / p95 / p99
error rate
CPU / memory
GC pauses
Tomcat busy threads
Hikari active / pending / timeout
Postgres CPU / slow queries
Redis hit rate / latency
Kafka lag
CPU throttling

Tomcat:
Too low = queue early.
Too high = overload downstream.

Hikari:
More connections are not always better.
Total DB connections = max pods * Hikari max pool.

Postgres:
short_code needs unique index.
Use EXPLAIN ANALYZE.
Select only needed columns.

Redis:
Hit rate matters.
TTL must balance freshness and DB protection.
Use negative caching for unknown codes.

Kafka:
Analytics should not block redirect.
Watch lag and producer errors.

Kubernetes:
CPU limits can throttle.
Memory limits can OOMKill Java.
HPA can overload DB.

Golden rule:
Find where the request is waiting.
```

---

## 31. One Picture To Remember

```text
                    PERFORMANCE TUNING MENTAL MODEL

                         "Find the narrowest pipe"

Traffic
  |
  v
+----------------+       Metrics: RPS, p95, p99, errors
| Load Test k6   |---------------------------------------------+
+----------------+                                             |
  |                                                              |
  v                                                              v
+----------------+     +----------------+     +----------------+     +----------------+
| Nginx / LB     | --> | Spring Boot    | --> | Redis Cache    | --> | PostgreSQL     |
| queue / network|     | threads / CPU  |     | hit rate       |     | index / CPU    |
+----------------+     +----------------+     +----------------+     +----------------+
                             |                         |
                             |                         v
                             |                  cache miss only
                             v
                       +-------------+
                       | Kafka       |
                       | analytics   |
                       +-------------+

Tuning loop:

Baseline
   |
   v
Observe bottleneck
   |
   v
Change one thing
   |
   v
Run same load test
   |
   v
Compare p95/p99/errors
   |
   v
Keep or revert

FINAL MEMORY:

Do not tune blindly.
Do not increase every pool.
Do not trust averages.
Tune the bottleneck that p99 and saturation metrics reveal.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Performance tuning is a bottleneck hunt, not random optimization.
2. Redirect is the hottest path and should be Redis-first, DB-on-miss, Kafka-async.
3. p99, error rate, and saturation metrics matter more than average latency.
4. Tomcat threads, Hikari pool, pod count, and DB capacity must be tuned together.
5. Every tuning change must be verified with the same repeatable load test.
```

After this chapter, the observability and performance phase becomes practical:

```text
057_Prometheus_Metrics.md
058_Grafana_Dashboards.md
059_Distributed_Tracing.md
060_SLO_SLA_Error_Budget.md
061_Load_Testing_k6.md
062_Performance_Tuning.md
```
