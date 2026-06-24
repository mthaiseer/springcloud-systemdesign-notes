# 034_Backpressure_Handling.md
# MiniURLShortener — Backpressure Handling

> Core mental model: **Backpressure is the system saying “slow down before I collapse.” It protects the weakest downstream component by controlling intake, buffering carefully, shedding non-critical work, and scaling consumers only when the bottleneck can safely handle more load.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What Backpressure Means](#4-what-backpressure-means)
- [5. Where Backpressure Appears In MiniURLShortener](#5-where-backpressure-appears-in-miniurlshortener)
- [6. Traffic Spike Mental Model](#6-traffic-spike-mental-model)
- [7. Queue-Based Backpressure](#7-queue-based-backpressure)
- [8. Kafka Lag Backpressure](#8-kafka-lag-backpressure)
- [9. API-Level Backpressure](#9-api-level-backpressure)
- [10. Worker-Level Backpressure](#10-worker-level-backpressure)
- [11. Database Backpressure](#11-database-backpressure)
- [12. Redis Backpressure](#12-redis-backpressure)
- [13. Thread Pool Backpressure](#13-thread-pool-backpressure)
- [14. Rate Limiting vs Backpressure](#14-rate-limiting-vs-backpressure)
- [15. Circuit Breaker vs Backpressure](#15-circuit-breaker-vs-backpressure)
- [16. Load Shedding](#16-load-shedding)
- [17. Graceful Degradation](#17-graceful-degradation)
- [18. Kafka Consumer Pause And Resume](#18-kafka-consumer-pause-and-resume)
- [19. Batch Processing Backpressure](#19-batch-processing-backpressure)
- [20. Retry Storm Backpressure](#20-retry-storm-backpressure)
- [21. Dead Letter Topic Strategy](#21-dead-letter-topic-strategy)
- [22. Metrics To Watch](#22-metrics-to-watch)
- [23. Spring Boot Implementation](#23-spring-boot-implementation)
- [24. Kafka Listener Backpressure Code](#24-kafka-listener-backpressure-code)
- [25. Async Worker Backpressure Code](#25-async-worker-backpressure-code)
- [26. API Response Design Under Pressure](#26-api-response-design-under-pressure)
- [27. Step-by-Step Dry Runs](#27-step-by-step-dry-runs)
- [28. Internal Execution Walkthrough](#28-internal-execution-walkthrough)
- [29. Testing Strategy](#29-testing-strategy)
- [30. Production Failure Stories](#30-production-failure-stories)
- [31. Debugging Mindset](#31-debugging-mindset)
- [32. Common Mistakes](#32-common-mistakes)
- [33. Interview-Ready Explanation](#33-interview-ready-explanation)
- [34. Senior Engineer Checklist](#34-senior-engineer-checklist)
- [35. One-Page Cheat Sheet](#35-one-page-cheat-sheet)
- [36. One Picture To Remember](#36-one-picture-to-remember)
- [37. Final Retention Summary](#37-final-retention-summary)

---

## 1. Why This Exists

By now MiniURLShortener has grown beyond simple synchronous APIs.

We now have:

```text
Create short URL API
Redirect API
Redis cache
Postgres persistence
Kafka click analytics topic
Async analytics worker
Possibly future notification, fraud, abuse, and reporting workers
```

At low traffic, everything looks simple.

```text
1 request arrives
1 DB write happens
1 Kafka event is produced
1 worker consumes the event
1 analytics row is stored
```

But production traffic does not arrive politely.

It arrives like this:

```text
normal traffic     : 1,000 redirects/sec
marketing campaign : 20,000 redirects/sec
bot attack         : 80,000 redirects/sec
consumer slowdown  : worker processes only 2,000 events/sec
database slowdown  : analytics writes become 10x slower
retry storm        : failed messages come back again and again
```

Without backpressure, the system behaves like this:

```text
More requests
  -> more Kafka messages
  -> more consumer lag
  -> more worker threads
  -> more DB connections
  -> more retries
  -> more CPU
  -> more memory
  -> crash
```

Backpressure is how a senior engineer prevents this chain reaction.

The goal is not:

```text
Process infinite traffic.
```

The real goal is:

```text
Keep the system alive under overload.
Protect critical paths.
Recover when pressure reduces.
Avoid turning one slow dependency into full platform failure.
```

For MiniURLShortener, the critical path is:

```text
Redirect must remain fast.
Analytics can be delayed.
```

So we design the system accordingly.

---

## 2. The One Core Mental Model

Backpressure is a pressure valve.

```text
When downstream is slower than upstream,
do not blindly keep accepting work.
```

ASCII mental model:

```text
                   FAST UPSTREAM
                        |
                        v
              +-------------------+
              | Incoming Traffic  |
              | 50k events/sec    |
              +-------------------+
                        |
                        v
              +-------------------+
              | Buffer / Queue    |
              | Kafka topic       |
              +-------------------+
                        |
                        v
              +-------------------+
              | Slow Consumer     |
              | 5k events/sec     |
              +-------------------+
                        |
                        v
              +-------------------+
              | Downstream DB     |
              | limited writes    |
              +-------------------+

Problem:
50k/sec enters, 5k/sec leaves.
Pressure grows inside the queue.
```

Backpressure asks:

```text
Where is pressure building?
Who is the bottleneck?
Can we slow intake?
Can we buffer safely?
Can we drop non-critical work?
Can we pause consumers?
Can we scale safely?
Can we degrade features?
```

One-line memory:

```text
Backpressure protects the system by matching input speed to safe processing speed.
```

For MiniURLShortener:

```text
Redirect path must not die because analytics worker is slow.
Analytics worker must not kill Postgres by opening too many writes.
Kafka lag must not grow forever.
Retries must not become a second traffic spike.
```

---

## 3. Problem Statement

Build a backpressure strategy for MiniURLShortener.

It must handle:

```text
1. Redirect traffic spikes.
2. Kafka click analytics event spikes.
3. Slow analytics database writes.
4. Slow Redis operations.
5. Worker thread pool saturation.
6. Consumer lag growth.
7. Retry storms.
8. Dead letter routing.
9. Graceful degradation.
10. Clear observability and debugging.
```

The design should protect:

```text
critical redirect latency
Postgres connection pool
Redis latency
Kafka consumer stability
worker memory
application pod survival
```

It should avoid:

```text
unbounded queues
unbounded retries
unbounded thread creation
unbounded DB writes
blind autoscaling
message loss without decision
silent lag growth
cascading failure
```

Out of scope:

```text
1. Full autoscaling implementation.
2. Full Kubernetes HPA chapter.
3. Full Kafka internals chapter.
4. Complete observability stack.
5. Stream processing framework design.
```

This chapter gives the production mental model and Spring Boot/Kafka implementation shape.

---

## 4. What Backpressure Means

Backpressure means:

```text
A slower component sends pressure upstream so the upstream does not overwhelm it.
```

In physical life:

```text
A pipe can carry only limited water.
If you push too much water, pressure rises.
A valve reduces flow.
A tank buffers some water.
An overflow path prevents explosion.
```

System version:

```text
API can receive more requests than workers can process.
Kafka can store more events than consumers can handle.
Workers can read faster than DB can write.
Retry logic can produce more load than original traffic.
```

Backpressure tools:

```text
1. Limit input rate.
2. Bound queue size.
3. Pause consumption.
4. Reduce concurrency.
5. Batch intelligently.
6. Drop low-value events.
7. Retry with delay.
8. Send poison messages to DLT.
9. Return 429/503 when needed.
10. Degrade non-critical features.
```

ASCII:

```text
No backpressure:

Client -> API -> Kafka -> Worker -> DB
 100k     100k   100k     100k    5k capacity
                                      |
                                      v
                                    collapse


With backpressure:

Client -> API -> Kafka -> Worker -> DB
 100k     controlled buffered paused 5k capacity
             |       |       |
             v       v       v
           429     lag     pause/resume
```

Important:

```text
Backpressure is not failure.
Backpressure is controlled survival.
```

---

## 5. Where Backpressure Appears In MiniURLShortener

MiniURLShortener has multiple pressure points.

```text
1. Client -> API
2. API -> Redis
3. API -> Postgres
4. API -> Kafka producer
5. Kafka topic -> Analytics worker
6. Analytics worker -> Postgres analytics table
7. Worker -> retry topic
8. Retry topic -> worker again
9. Application thread pool
10. Hikari connection pool
```

ASCII system map:

```text
                         +--------------------+
                         |      Clients       |
                         +--------------------+
                                  |
                                  v
                         +--------------------+
                         |  URL Shortener API |
                         +--------------------+
                           |        |        |
                           |        |        |
                           v        v        v
                      +-------+ +--------+ +--------+
                      | Redis | |Postgres| | Kafka  |
                      +-------+ +--------+ +--------+
                                              |
                                              v
                                  +----------------------+
                                  | Analytics Worker     |
                                  | consumer group       |
                                  +----------------------+
                                      |          |
                                      v          v
                                 +---------+ +-----------+
                                 |Analytics| | Retry/DLT |
                                 | DB      | | Topics    |
                                 +---------+ +-----------+
```

Backpressure examples:

```text
Redis slow:
    redirect cache lookup slows.
    API threads pile up.

Postgres slow:
    create API slows.
    analytics worker writes slow.

Kafka broker slow:
    producer send latency increases.
    API can block if not designed carefully.

Analytics DB slow:
    worker consumes slowly.
    Kafka lag grows.

Retry topic hot:
    failed events re-enter repeatedly.
    worker spends all time retrying bad events.
```

Senior mental model:

```text
Every arrow can become a pressure point.
Every queue needs a limit and an owner.
Every retry needs a budget.
```

---

## 6. Traffic Spike Mental Model

Traffic spike:

```text
Input rate suddenly becomes higher than processing capacity.
```

Example:

```text
redirect events produced: 50,000/sec
analytics worker capacity: 5,000/sec
difference: 45,000/sec backlog growth
```

Backlog growth formula:

```text
lag_growth_per_second = producer_rate - consumer_rate
```

If:

```text
producer_rate = 50,000/sec
consumer_rate = 5,000/sec
```

Then:

```text
lag_growth_per_second = 45,000/sec
lag_growth_per_minute = 2,700,000 events
lag_growth_per_10_minutes = 27,000,000 events
```

ASCII:

```text
Time 0:
Kafka lag = 0

After 1 minute:
Kafka lag = 2.7M

After 10 minutes:
Kafka lag = 27M

After 1 hour:
Kafka lag = 162M
```

Why this matters:

```text
Even if Kafka survives, your analytics freshness is destroyed.
A dashboard that used to be 5 seconds behind becomes hours behind.
```

Backpressure question:

```text
Do we need every click event?
Do we need them immediately?
Can we sample?
Can we aggregate?
Can we drop bot traffic?
Can we keep redirect alive and delay analytics?
```

For URL shortener:

```text
Redirect success is critical.
Analytics exactness is important but less critical.
Real-time analytics freshness is nice but not always required.
```

So under pressure:

```text
protect redirects first
delay analytics second
drop/summarize low-value analytics if necessary
```

---

## 7. Queue-Based Backpressure

A queue absorbs short spikes.

But a queue is not magic.

```text
Queue = shock absorber.
Queue != infinite capacity.
```

ASCII:

```text
Without queue:

API -> Worker -> DB

If DB slow:
API waits directly.


With queue:

API -> Kafka -> Worker -> DB

If DB slow:
Kafka absorbs temporary backlog.
```

Good queue use:

```text
short burst
consumer catches up later
messages are durable
processing is asynchronous
latency can increase safely
```

Bad queue use:

```text
permanent overload
consumer never catches up
queue grows forever
storage fills
lag hides real failure
```

Queue pressure signs:

```text
Kafka consumer lag increasing continuously
oldest message age increasing
worker CPU high
DB write latency high
retry topic growing
DLT growing
```

ASCII pressure curve:

```text
Healthy burst:

lag
 ^
 |        /\ 
 |       /  \ 
 |______/    \________ time
      spike  catch-up


Unhealthy overload:

lag
 ^
 |        /
 |       /
 |      /
 |_____/
       time
```

Rule:

```text
A queue helps with temporary mismatch.
Backpressure handles sustained mismatch.
```

---

## 8. Kafka Lag Backpressure

Kafka consumer lag is the main pressure signal for analytics.

Lag means:

```text
messages produced but not yet consumed
```

If lag grows:

```text
producer is faster than consumer
```

Kafka lag states:

```text
Healthy:
    lag small or returns to zero after bursts

Warning:
    lag grows during traffic spike but later decreases

Critical:
    lag grows continuously and oldest message age increases

Emergency:
    lag grows, retry topic grows, DB latency grows, pods restart
```

ASCII:

```text
Kafka Topic Partition

Produced offset:  1000000
Committed offset:  970000
Lag:                30000

[processed..............][unprocessed messages........]
0                    970000                       1000000
```

Backpressure options for Kafka:

```text
1. Increase consumer instances up to partition count.
2. Increase batch size if DB supports it.
3. Reduce worker concurrency if DB is bottleneck.
4. Pause partitions when downstream is unhealthy.
5. Route bad messages to DLT.
6. Delay retries.
7. Sample low-value analytics.
8. Stop non-critical producers if lag is too high.
```

Important:

```text
Scaling consumers only helps if the bottleneck is CPU or consumer count.
Scaling consumers hurts if the bottleneck is Postgres.
```

Bad scaling:

```text
DB can handle 5k writes/sec.
You scale workers from 5 pods to 50 pods.
Now 50 pods fight for DB connections.
DB latency explodes.
Entire system becomes worse.
```

Senior rule:

```text
Scale the bottleneck, not the symptom.
```

---

## 9. API-Level Backpressure

API-level backpressure means the API stops accepting unlimited work.

For synchronous endpoints:

```text
POST /api/v1/urls
GET /{shortCode}
```

Tools:

```text
1. Rate limiting.
2. Request queue limits.
3. Timeout limits.
4. Bulkheads.
5. 429 Too Many Requests.
6. 503 Service Unavailable.
7. Retry-After header.
8. Disable non-critical work.
```

For MiniURLShortener:

```text
Redirect should stay fast.
Click analytics publishing should not block redirect forever.
```

Bad redirect design:

```text
GET /abc123
  -> lookup longUrl
  -> publish Kafka event synchronously with long timeout
  -> return redirect only after Kafka ack

If Kafka slows:
redirect latency increases
threads block
API collapses
```

Better redirect design:

```text
GET /abc123
  -> lookup longUrl
  -> try publish click event quickly
  -> if analytics publish fails or times out, log/drop/degrade
  -> return redirect
```

ASCII:

```text
Critical path:

Client -> Redirect API -> Redis/Postgres -> 302

Non-critical side path:

                 -> Kafka click analytics
                    should not kill redirect
```

Decision:

```text
If analytics is non-critical:
    do not let Kafka pressure break redirect.

If analytics is billing-critical:
    use durable event outbox and accept more latency.
```

For this MiniURLShortener chapter:

```text
analytics is important but not redirect-critical
```

So under pressure:

```text
keep redirect alive
degrade analytics freshness/accuracy if needed
```

---

## 10. Worker-Level Backpressure

Worker-level backpressure protects the worker from reading more messages than it can process.

Bad worker:

```text
while true:
    poll Kafka
    submit each message to unbounded executor
```

Problem:

```text
Kafka poll is fast.
Executor queue grows.
Memory grows.
GC grows.
Pod dies.
Kafka rebalances.
Messages are retried.
Lag gets worse.
```

ASCII:

```text
Kafka -> Worker poll loop -> Executor queue -> DB

If executor queue unbounded:

[message][message][message][message][message][message]...
memory explosion
```

Good worker:

```text
bounded executor
limited concurrency
pause Kafka partitions when queue is full
resume when queue drains
manual acknowledgment after successful processing
retry with delay
DLT after max attempts
```

Worker backpressure tools:

```text
1. max.poll.records
2. bounded thread pool
3. small queue capacity
4. consumer pause/resume
5. manual ack
6. database connection limit awareness
7. batch processing
8. retry budget
```

Rule:

```text
A worker should pull only what it can safely finish.
```

---

## 11. Database Backpressure

Database is often the real bottleneck.

MiniURLShortener has two DB paths:

```text
Create API writes short URL rows.
Analytics worker writes click analytics rows.
```

Analytics can become heavy:

```text
click_id
short_code
clicked_at
ip_hash
user_agent
country
referer
device
```

At high click volume:

```text
one click = one DB insert
50k clicks/sec = 50k inserts/sec
```

This can overwhelm Postgres.

DB backpressure signs:

```text
Hikari active connections near max
Hikari pending threads increasing
DB CPU high
DB locks/waits increasing
insert latency high
checkpoint pressure
disk IO high
Kafka lag increasing
```

ASCII:

```text
Workers
  | | | | | | | | |
  v v v v v v v v v
+---------------------+
| Hikari Pool max=20  |
+---------------------+
  | | | | | | | | |
  v v v v v v v v v
+---------------------+
| Postgres            |
| finite write power  |
+---------------------+
```

Bad reaction:

```text
Increase worker pods.
Increase worker threads.
Increase Hikari max pool.
```

This may create:

```text
more DB contention
more context switching
more locks
more latency
```

Better reaction:

```text
batch writes
reduce concurrency
pause Kafka consumption
sample analytics
aggregate clicks per short code per minute
move analytics to OLAP pipeline later
```

Senior mental model:

```text
When DB is slow, fewer controlled writes can outperform many chaotic writes.
```

---

## 12. Redis Backpressure

Redis is fast, but not infinite.

Redirect path often uses Redis:

```text
GET /{shortCode}
  -> Redis GET shortCode
  -> cache hit returns longUrl
  -> cache miss DB lookup
```

Redis pressure signs:

```text
latency increases
timeouts increase
connection pool exhausted
CPU high
network bandwidth high
hot keys
evictions
blocked clients
```

Backpressure options:

```text
1. set short Redis timeouts
2. use connection pool limits
3. fallback to DB only carefully
4. local short-lived cache for hot keys
5. circuit breaker around Redis
6. avoid retry storms
7. protect DB from cache failure
```

Important failure mode:

```text
Redis slow -> app falls back to DB for every redirect -> DB overload -> full outage
```

ASCII:

```text
Normal:

API -> Redis hit -> 302


Redis outage with naive fallback:

API -> Redis timeout -> DB lookup
API -> Redis timeout -> DB lookup
API -> Redis timeout -> DB lookup
                 |
                 v
              DB overload
```

Better:

```text
Redis timeout quickly.
Use local hot-key cache for top links.
Use rate limit / circuit breaker.
Avoid unlimited DB fallback under pressure.
```

Rule:

```text
Fallbacks also need limits.
```

---

## 13. Thread Pool Backpressure

Thread pools are hidden queues.

In Spring Boot:

```text
Tomcat request threads
Kafka listener threads
@Async executor threads
Scheduler threads
Hikari DB connections
HTTP client connection pools
```

Each pool has:

```text
thread count
queue size
timeout
rejection policy
```

Bad configuration:

```text
unbounded queue
huge max threads
no timeout
CallerRunsPolicy on request thread without thinking
```

ASCII:

```text
Incoming tasks
      |
      v
+-----------------------+
| Thread Pool           |
| active threads: 20    |
| queue capacity: 1000  |
+-----------------------+
      |
      v
Downstream DB
```

If downstream slows:

```text
tasks take longer
threads stay busy
queue fills
latency increases
memory increases
```

Good backpressure config:

```text
bounded queue
sensible max threads
clear rejection policy
metrics
timeouts
bulkhead per dependency
```

Example:

```text
analytics-worker-executor:
    corePoolSize: 4
    maxPoolSize: 8
    queueCapacity: 500
    rejection: reject and pause Kafka
```

Rule:

```text
Never use unbounded queues in production worker paths.
```

---

## 14. Rate Limiting vs Backpressure

Rate limiting and backpressure are related but not identical.

Rate limiting:

```text
Predefined limit on caller behavior.
Example: user can create 100 short URLs/minute.
```

Backpressure:

```text
Dynamic response to system pressure.
Example: DB is slow, reduce accepted create traffic temporarily.
```

Table:

```text
+-------------------+-----------------------------+-----------------------------+
| Concept           | Trigger                     | Purpose                     |
+-------------------+-----------------------------+-----------------------------+
| Rate limiting     | caller exceeds allowed rate | fairness / abuse protection |
| Backpressure      | system under pressure       | survival / stability        |
| Circuit breaker   | dependency failing          | stop repeated calls         |
| Load shedding     | overload                    | drop low-priority work      |
+-------------------+-----------------------------+-----------------------------+
```

Example:

```text
A normal user sending 10k create requests/minute:
    rate limit

All users normal but DB is overloaded:
    backpressure

Redis failing repeatedly:
    circuit breaker

Analytics too far behind:
    shed/sample analytics
```

Senior explanation:

```text
Rate limiting protects from bad callers.
Backpressure protects from overloaded systems.
```

---

## 15. Circuit Breaker vs Backpressure

Circuit breaker stops calls to a failing dependency.

Backpressure slows or stops intake when pressure is high.

Redis example:

```text
Redis timeout rate high
  -> circuit breaker opens
  -> API stops waiting on Redis
```

Kafka worker example:

```text
DB insert latency high
  -> backpressure triggers
  -> pause Kafka consumption
```

ASCII:

```text
Circuit breaker:

API -> Redis
       |
       v
   too many failures
       |
       v
   open circuit
       |
       v
   fail fast / fallback


Backpressure:

Kafka -> Worker -> DB
                  |
                  v
              DB too slow
                  |
                  v
              pause Kafka
```

They work together.

Example:

```text
If analytics DB is failing:
    circuit breaker can stop DB writes temporarily
    backpressure can pause consumer
    retry topic can delay reprocessing
```

Rule:

```text
Circuit breaker protects dependency calls.
Backpressure protects flow rate.
```

---

## 16. Load Shedding

Load shedding means deliberately dropping or rejecting lower-priority work.

In MiniURLShortener:

```text
Critical:
    redirect to longUrl
    create short URL

Important but less critical:
    click analytics
    user-agent parsing
    geo enrichment
    real-time dashboard
```

Under extreme pressure:

```text
keep redirect
skip enrichment
sample analytics
delay dashboard updates
drop bot analytics
return 429 for create API if DB saturated
```

ASCII priority map:

```text
Priority 0: Redirect correctness
Priority 1: Create URL correctness
Priority 2: Durable click event capture
Priority 3: Real-time analytics freshness
Priority 4: Enrichment/reporting
```

Load shedding decisions:

```text
If Kafka lag > threshold:
    sample analytics events

If worker queue full:
    pause consumption

If DB pool saturated:
    stop analytics writes temporarily

If create API DB pool saturated:
    return 503/429 with Retry-After

If redirect path pressure high:
    skip analytics publish
```

Important:

```text
Load shedding must be intentional and visible in metrics.
```

Do not silently drop important data without metrics.

Track:

```text
analytics.events.dropped
analytics.events.sampled
backpressure.active
backpressure.reason
```

---

## 17. Graceful Degradation

Graceful degradation means the system continues with reduced features.

Full feature:

```text
Redirect + click event + user-agent parse + geo lookup + real-time dashboard
```

Degraded mode:

```text
Redirect only
Click event sampled
No geo lookup
No real-time dashboard update
```

ASCII:

```text
Normal mode:

GET /abc
  -> resolve URL
  -> publish click event
  -> parse device
  -> geo lookup
  -> dashboard update
  -> 302


Pressure mode:

GET /abc
  -> resolve URL
  -> best-effort analytics
  -> 302
```

Graceful degradation rules:

```text
1. Know what is critical.
2. Know what can be delayed.
3. Know what can be skipped.
4. Make degradation observable.
5. Automatically recover when pressure drops.
```

MiniURLShortener degradation table:

```text
+-------------------------+------------------------+-----------------------------+
| Feature                 | Under Pressure         | Reason                      |
+-------------------------+------------------------+-----------------------------+
| Redirect                | keep                   | core product                |
| Create URL              | rate limit if needed   | DB correctness required     |
| Click analytics         | delay/sample/drop some | not always critical path    |
| Geo enrichment          | disable                | expensive non-critical work |
| Real-time dashboard     | stale allowed          | freshness can degrade       |
+-------------------------+------------------------+-----------------------------+
```

Senior mental model:

```text
A good system does not fail all at once.
It loses optional features first.
```

---

## 18. Kafka Consumer Pause And Resume

Kafka consumers can pause partitions.

This is useful when:

```text
worker queue is full
DB is slow
downstream circuit breaker is open
retry backlog is too high
```

Pause means:

```text
consumer stops fetching records from assigned partitions
```

Resume means:

```text
consumer starts fetching again
```

ASCII:

```text
Normal:

Kafka Partition -> Consumer -> Worker -> DB


Pressure:

Kafka Partition -X-> Consumer paused
                  |
                  v
             DB recovers
                  |
                  v
Kafka Partition -> Consumer resumed
```

Important:

```text
Pause does not delete messages.
Messages remain in Kafka.
```

But be careful:

```text
If consumer stays paused too long, max.poll.interval.ms can cause rebalance
unless polling still happens correctly.
```

Spring Kafka gives listener container controls.

Practical design:

```text
1. Monitor downstream health.
2. If pressure high, pause listener container.
3. Continue health checks.
4. Resume when pressure below safe threshold.
5. Emit metrics.
```

Pseudo-state machine:

```text
NORMAL
  |
  | pressure high
  v
PAUSED
  |
  | pressure low for N checks
  v
RESUMED
```

Rule:

```text
Pause early enough to prevent local memory explosion.
Resume slowly to avoid pressure oscillation.
```

---

## 19. Batch Processing Backpressure

Batching can improve throughput.

Instead of:

```text
1 DB insert per click
```

Use:

```text
batch insert 100 clicks at a time
```

Benefits:

```text
fewer DB round trips
better throughput
less transaction overhead
lower CPU per row
```

Risk:

```text
large batches increase latency
large batches increase memory
large batches fail as a group
```

ASCII:

```text
Without batch:

event -> insert
event -> insert
event -> insert
event -> insert


With batch:

event \
event  \
event   -> batch insert
event  /
event /
```

Batch tuning:

```text
batch size: 100-1000 depending on DB
flush interval: 100ms-1000ms
max memory limit
retry strategy
partial failure strategy
```

Backpressure rule:

```text
Batching helps only if DB can process batches efficiently.
```

If DB is saturated:

```text
bigger batches may worsen lock time and latency
```

For MiniURLShortener analytics:

```text
Use batching for click_events inserts.
Pause consumption if batch flush latency exceeds threshold.
```

Example thresholds:

```text
DB insert p95 < 100ms:
    normal

DB insert p95 100-500ms:
    warning

DB insert p95 > 1s:
    pause/reduce consumption

DB insert errors:
    retry with delay or DLT
```

---

## 20. Retry Storm Backpressure

Retries can create more load than original traffic.

Example:

```text
DB temporary failure
10,000 messages fail
each retries immediately
DB receives 10,000 more writes
they fail again
retry again
```

ASCII:

```text
Original load:
Kafka -> Worker -> DB fails

Bad retry:
Kafka -> Worker -> DB fails
        ^              |
        |______________|
          immediate retry loop
```

Retry storm symptoms:

```text
retry topic grows
same message fails repeatedly
DB receives repeated writes
worker CPU high
successful throughput drops
DLT may grow
```

Good retry strategy:

```text
1. retry with exponential backoff
2. retry with jitter
3. max retry attempts
4. delayed retry topics
5. DLT after attempts exhausted
6. do not retry validation/poison errors forever
```

Retry classification:

```text
Transient:
    DB timeout
    network timeout
    temporary unavailable
    retry with delay

Permanent:
    malformed message
    invalid schema
    impossible shortCode
    send to DLT quickly
```

ASCII:

```text
click-events
    |
    v
worker
    |
    +-- success -> commit
    |
    +-- transient fail -> retry-5s -> retry-1m -> retry-10m -> DLT
    |
    +-- permanent fail -> DLT
```

Rule:

```text
Retries need brakes.
```

---

## 21. Dead Letter Topic Strategy

Dead letter topic stores messages that cannot be processed safely.

DLT is not a trash bin.

It is an investigation queue.

DLT reasons:

```text
malformed JSON
missing required fields
schema incompatible
max retry attempts exceeded
permanent business invalidity
unexpected bug after retry budget
```

ASCII:

```text
Main Topic
   |
   v
Worker
   |
   +-- success -----------------> commit offset
   |
   +-- transient fail ----------> Retry Topic
   |
   +-- max attempts exceeded ---> Dead Letter Topic
   |
   +-- poison message ----------> Dead Letter Topic
```

DLT message should include:

```text
original payload
original topic
partition
offset
error class
error message
failure timestamp
attempt count
correlation id
```

DLT monitoring:

```text
dlt.message.count
dlt.oldest.message.age
dlt.reason.count
```

Important:

```text
Do not let DLT grow silently.
```

Operational actions:

```text
1. inspect message
2. identify reason
3. fix code or data
4. replay if safe
5. discard if invalid
```

Senior rule:

```text
DLT gives you controlled failure instead of infinite retry.
```

---

## 22. Metrics To Watch

Backpressure without metrics is guesswork.

Core metrics:

```text
API:
    request rate
    p95/p99 latency
    429 count
    503 count
    Tomcat busy threads

Kafka producer:
    send latency
    send error count
    buffer available bytes
    record queue time

Kafka consumer:
    consumer lag
    records consumed/sec
    processing latency
    commit latency
    rebalance count

Worker:
    active threads
    queue size
    rejected tasks
    batch size
    batch flush latency

DB:
    Hikari active connections
    Hikari pending threads
    query latency
    insert latency
    DB CPU
    locks/waits
    disk IO

Redis:
    command latency
    timeout count
    hit ratio
    connection pool usage

Backpressure:
    backpressure.active
    backpressure.reason
    pause count
    resume count
    dropped events
    sampled events
```

ASCII dashboard view:

```text
+----------------+      +----------------+
| Kafka Lag      | ---> | Worker Queue   |
+----------------+      +----------------+
        |                       |
        v                       v
+----------------+      +----------------+
| DB Latency     | ---> | Backpressure   |
+----------------+      +----------------+
        |
        v
+----------------+
| API p99        |
+----------------+
```

Important derived metrics:

```text
lag growth rate
oldest unprocessed message age
processing capacity/sec
producer-consumer gap
```

Formula:

```text
catch_up_time = current_lag / (consumer_rate - producer_rate)
```

If:

```text
current_lag = 1,000,000
consumer_rate = 10,000/sec
producer_rate = 5,000/sec
```

Then:

```text
catch_up_time = 1,000,000 / 5,000 = 200 seconds
```

If consumer_rate <= producer_rate:

```text
you will never catch up
```

---

## 23. Spring Boot Implementation

We design a simple backpressure service.

Package:

```text
com.miniurl.shortener.analytics.backpressure
```

Components:

```text
BackpressureState
BackpressureReason
BackpressureMonitor
BackpressureDecision
AnalyticsWorkerProperties
```

Backpressure reasons:

```java
package com.miniurl.shortener.analytics.backpressure;

public enum BackpressureReason {
    NONE,
    WORKER_QUEUE_FULL,
    DB_POOL_SATURATED,
    DB_LATENCY_HIGH,
    KAFKA_LAG_TOO_HIGH,
    RETRY_STORM,
    DOWNSTREAM_UNHEALTHY
}
```

Backpressure state:

```java
package com.miniurl.shortener.analytics.backpressure;

public class BackpressureState {

    private final boolean active;
    private final BackpressureReason reason;
    private final String message;

    public BackpressureState(
            boolean active,
            BackpressureReason reason,
            String message
    ) {
        this.active = active;
        this.reason = reason;
        this.message = message;
    }

    public static BackpressureState inactive() {
        return new BackpressureState(false, BackpressureReason.NONE, "No pressure");
    }

    public static BackpressureState active(
            BackpressureReason reason,
            String message
    ) {
        return new BackpressureState(true, reason, message);
    }

    public boolean isActive() {
        return active;
    }

    public BackpressureReason getReason() {
        return reason;
    }

    public String getMessage() {
        return message;
    }
}
```

Properties:

```java
package com.miniurl.shortener.analytics.backpressure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "miniurl.analytics.backpressure")
public class AnalyticsBackpressureProperties {

    private int maxWorkerQueueSize = 500;
    private int maxHikariActiveConnections = 18;
    private long maxDbInsertLatencyMs = 1000;
    private long maxKafkaLag = 1_000_000;
    private int resumeQueueSize = 200;

    public int getMaxWorkerQueueSize() {
        return maxWorkerQueueSize;
    }

    public void setMaxWorkerQueueSize(int maxWorkerQueueSize) {
        this.maxWorkerQueueSize = maxWorkerQueueSize;
    }

    public int getMaxHikariActiveConnections() {
        return maxHikariActiveConnections;
    }

    public void setMaxHikariActiveConnections(int maxHikariActiveConnections) {
        this.maxHikariActiveConnections = maxHikariActiveConnections;
    }

    public long getMaxDbInsertLatencyMs() {
        return maxDbInsertLatencyMs;
    }

    public void setMaxDbInsertLatencyMs(long maxDbInsertLatencyMs) {
        this.maxDbInsertLatencyMs = maxDbInsertLatencyMs;
    }

    public long getMaxKafkaLag() {
        return maxKafkaLag;
    }

    public void setMaxKafkaLag(long maxKafkaLag) {
        this.maxKafkaLag = maxKafkaLag;
    }

    public int getResumeQueueSize() {
        return resumeQueueSize;
    }

    public void setResumeQueueSize(int resumeQueueSize) {
        this.resumeQueueSize = resumeQueueSize;
    }
}
```

Configuration:

```java
package com.miniurl.shortener.analytics.config;

import com.miniurl.shortener.analytics.backpressure.AnalyticsBackpressureProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AnalyticsBackpressureProperties.class)
public class AnalyticsBackpressureConfig {
}
```

Application YAML:

```yaml
miniurl:
  analytics:
    backpressure:
      max-worker-queue-size: 500
      resume-queue-size: 200
      max-hikari-active-connections: 18
      max-db-insert-latency-ms: 1000
      max-kafka-lag: 1000000
```

Why separate thresholds?

```text
Pause threshold prevents overload.
Resume threshold prevents flapping.
```

Flapping example:

```text
queue size 499 -> resume
queue size 501 -> pause
queue size 499 -> resume
queue size 501 -> pause
```

Better:

```text
pause at 500
resume at 200
```

This gap is called hysteresis.

---

## 24. Kafka Listener Backpressure Code

A practical Spring Kafka design:

```text
1. Consume click events.
2. Before processing, check pressure.
3. If pressure active, pause listener container.
4. Do not submit unlimited tasks.
5. Process only when worker has capacity.
6. Ack only after successful processing.
```

Kafka listener:

```java
package com.miniurl.shortener.analytics.worker;

import com.miniurl.shortener.analytics.backpressure.BackpressureMonitor;
import com.miniurl.shortener.analytics.model.ClickEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class ClickAnalyticsListener {

    private final ClickAnalyticsWorker worker;
    private final BackpressureMonitor backpressureMonitor;

    public ClickAnalyticsListener(
            ClickAnalyticsWorker worker,
            BackpressureMonitor backpressureMonitor
    ) {
        this.worker = worker;
        this.backpressureMonitor = backpressureMonitor;
    }

    @KafkaListener(
            id = "click-analytics-listener",
            topics = "click-events",
            groupId = "analytics-worker",
            containerFactory = "clickKafkaListenerContainerFactory"
    )
    public void onMessage(
            ClickEvent event,
            Acknowledgment acknowledgment
    ) {
        if (backpressureMonitor.isBackpressureActive()) {
            throw new BackpressureActiveException("Backpressure active; message should not be processed now");
        }

        worker.process(event);

        acknowledgment.acknowledge();
    }
}
```

Backpressure exception:

```java
package com.miniurl.shortener.analytics.backpressure;

public class BackpressureActiveException extends RuntimeException {

    public BackpressureActiveException(String message) {
        super(message);
    }
}
```

But throwing from listener is not enough.

Better production design controls the container:

```java
package com.miniurl.shortener.analytics.backpressure;

import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KafkaBackpressureController {

    private static final String LISTENER_ID = "click-analytics-listener";

    private final KafkaListenerEndpointRegistry registry;
    private final BackpressureMonitor monitor;

    private boolean paused = false;

    public KafkaBackpressureController(
            KafkaListenerEndpointRegistry registry,
            BackpressureMonitor monitor
    ) {
        this.registry = registry;
        this.monitor = monitor;
    }

    @Scheduled(fixedDelay = 2000)
    public void controlListener() {
        MessageListenerContainer container =
                registry.getListenerContainer(LISTENER_ID);

        if (container == null) {
            return;
        }

        BackpressureState state = monitor.currentState();

        if (state.isActive() && !paused) {
            container.pause();
            paused = true;
            return;
        }

        if (!state.isActive() && paused) {
            container.resume();
            paused = false;
        }
    }
}
```

Important:

```text
The listener container pauses fetching.
Messages stay in Kafka.
Worker gets time to drain.
```

Production improvement:

```text
emit logs and metrics on pause/resume
include reason
avoid pause/resume flapping
```

---

## 25. Async Worker Backpressure Code

A bounded executor protects memory.

Executor config:

```java
package com.miniurl.shortener.analytics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class AnalyticsExecutorConfig {

    @Bean
    public ThreadPoolExecutor analyticsWorkerExecutor() {
        int coreThreads = 4;
        int maxThreads = 8;
        int queueCapacity = 500;

        return new ThreadPoolExecutor(
                coreThreads,
                maxThreads,
                30,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                new ThreadFactory() {
                    private final ThreadFactory defaultFactory =
                            Executors.defaultThreadFactory();

                    @Override
                    public Thread newThread(Runnable runnable) {
                        Thread thread = defaultFactory.newThread(runnable);
                        thread.setName("analytics-worker-" + thread.getId());
                        return thread;
                    }
                },
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
```

Why `AbortPolicy`?

```text
When queue is full, fail fast.
Do not silently grow memory.
Do not hide pressure.
```

Worker:

```java
package com.miniurl.shortener.analytics.worker;

import com.miniurl.shortener.analytics.model.ClickEvent;
import org.springframework.stereotype.Service;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ClickAnalyticsWorker {

    private final ThreadPoolExecutor executor;
    private final ClickAnalyticsProcessor processor;

    public ClickAnalyticsWorker(
            ThreadPoolExecutor analyticsWorkerExecutor,
            ClickAnalyticsProcessor processor
    ) {
        this.executor = analyticsWorkerExecutor;
        this.processor = processor;
    }

    public void process(ClickEvent event) {
        try {
            executor.execute(() -> processor.process(event));
        } catch (RejectedExecutionException ex) {
            throw new WorkerQueueFullException("Analytics worker queue is full");
        }
    }

    public int queueSize() {
        return executor.getQueue().size();
    }

    public int activeCount() {
        return executor.getActiveCount();
    }
}
```

Queue full exception:

```java
package com.miniurl.shortener.analytics.worker;

public class WorkerQueueFullException extends RuntimeException {

    public WorkerQueueFullException(String message) {
        super(message);
    }
}
```

Processor:

```java
package com.miniurl.shortener.analytics.worker;

import com.miniurl.shortener.analytics.model.ClickEvent;
import org.springframework.stereotype.Service;

@Service
public class ClickAnalyticsProcessor {

    private final ClickAnalyticsRepository repository;

    public ClickAnalyticsProcessor(ClickAnalyticsRepository repository) {
        this.repository = repository;
    }

    public void process(ClickEvent event) {
        repository.insertClick(event);
    }
}
```

Important issue:

```text
If listener acknowledges before async task finishes, message can be lost.
```

Better:

```text
For strict durability, process synchronously inside listener or use a handoff system that acks only after completion.
```

Simple safer version:

```java
public void onMessage(ClickEvent event, Acknowledgment ack) {
    processor.process(event);
    ack.acknowledge();
}
```

Why show executor then?

```text
Because many production systems use worker pools.
But the senior engineer must understand ack semantics.
```

Rule:

```text
Never acknowledge Kafka messages before the work that must be guaranteed has completed.
```

If analytics is best-effort:

```text
ack early may be acceptable.
```

If analytics is billing/compliance:

```text
ack only after durable processing.
```

---

## 26. API Response Design Under Pressure

When the API is overloaded, responses should be honest.

For create API:

```text
If DB pool saturated:
    503 Service Unavailable
    Retry-After: 2
```

For user-specific excessive calls:

```text
429 Too Many Requests
Retry-After: 60
```

For redirect API analytics pressure:

```text
still return 302 if URL resolution succeeds
skip/degrade analytics
```

Error response shape should match the chapter 009 contract.

Example:

```json
{
  "timestamp": "2026-06-24T10:00:00Z",
  "status": 503,
  "error": "Service Unavailable",
  "code": "SYSTEM_UNDER_PRESSURE",
  "message": "The service is temporarily overloaded. Please retry later.",
  "path": "/api/v1/urls",
  "fieldErrors": []
}
```

Add exception:

```java
package com.miniurl.shortener.common.error;

import org.springframework.http.HttpStatus;

public class SystemUnderPressureException extends ApiException {

    public SystemUnderPressureException(String message) {
        super("SYSTEM_UNDER_PRESSURE", HttpStatus.SERVICE_UNAVAILABLE, message);
    }
}
```

Add `Retry-After` in handler when needed.

Concept:

```text
429:
    caller is too aggressive

503:
    system is overloaded or dependency unavailable
```

ASCII:

```text
Create API pressure:

Client -> POST /api/v1/urls
              |
              v
         DB pool full?
              |
        yes --+--> 503 SYSTEM_UNDER_PRESSURE
              |
        no  --+--> create short URL
```

Rule:

```text
Do not let clients wait forever.
Fail fast with retry guidance.
```

---

## 27. Step-by-Step Dry Runs

### Dry Run 1: Small Traffic Burst

Scenario:

```text
producer rate: 10k/sec for 20 seconds
consumer rate: 5k/sec
then producer returns to 1k/sec
```

Flow:

```text
1. Kafka lag grows during burst.
2. Worker processes at 5k/sec.
3. After burst, producer rate drops to 1k/sec.
4. Consumer now has 4k/sec spare capacity.
5. Lag drains.
6. No load shedding needed.
```

ASCII:

```text
lag
 ^
 |      /\
 |     /  \
 |____/    \________
       burst catch-up
```

Conclusion:

```text
Queue handled temporary burst.
Backpressure did not need to reject traffic.
```

---

### Dry Run 2: Sustained Overload

Scenario:

```text
producer rate: 50k/sec
consumer rate: 5k/sec
duration: 10 minutes
```

Flow:

```text
1. Lag grows by 45k/sec.
2. After 1 minute lag is 2.7M.
3. Oldest message age increases.
4. Backpressure activates.
5. Worker checks DB latency.
6. DB is saturated.
7. Scaling workers would hurt.
8. System samples analytics and pauses some consumption.
9. Redirect continues.
```

Conclusion:

```text
Backpressure protects core redirect path and DB.
```

---

### Dry Run 3: Worker Queue Full

Scenario:

```text
worker queue capacity: 500
current queue size: 500
new Kafka message arrives
```

Flow:

```text
1. Listener receives message.
2. Worker executor rejects task.
3. WorkerQueueFullException thrown.
4. Backpressure monitor marks WORKER_QUEUE_FULL.
5. Kafka listener container pauses.
6. Worker drains existing queue.
7. Queue drops below resume threshold 200.
8. Listener resumes.
```

ASCII:

```text
Queue size:
500 -> pause
400
300
200 -> resume
```

Conclusion:

```text
Bounded queue prevents memory explosion.
```

---

### Dry Run 4: DB Latency High

Scenario:

```text
analytics DB insert p95 = 2 seconds
threshold = 1 second
```

Flow:

```text
1. Metrics show DB latency high.
2. Hikari active connections near max.
3. Backpressure reason becomes DB_LATENCY_HIGH.
4. Kafka consumption pauses.
5. Current in-flight writes finish.
6. DB latency slowly decreases.
7. System resumes after stable low latency.
```

Conclusion:

```text
Backpressure protects DB instead of increasing DB pressure.
```

---

### Dry Run 5: Retry Storm

Scenario:

```text
DB down for 2 minutes
10k messages fail
retry is immediate
```

Bad flow:

```text
1. All messages fail.
2. All retry immediately.
3. DB gets hammered while still down.
4. Worker spends all CPU retrying.
5. Lag grows faster.
```

Better flow:

```text
1. Messages fail.
2. Retry goes to delayed retry topic.
3. Retry after 5s, then 1m, then 10m.
4. After max attempts, DLT.
5. Main worker keeps capacity for fresh messages when DB recovers.
```

Conclusion:

```text
Retry delay is backpressure.
```

---

### Dry Run 6: Redis Slow During Redirect

Scenario:

```text
Redis latency = 2 seconds
redirect p99 target = 50ms
```

Bad flow:

```text
1. Every redirect waits 2 seconds.
2. Tomcat threads block.
3. Request queue fills.
4. API becomes unavailable.
```

Better flow:

```text
1. Redis timeout set to 30-50ms.
2. Circuit breaker opens after failures.
3. Hot local cache serves popular links.
4. DB fallback is limited.
5. Redirect stays alive for many requests.
```

Conclusion:

```text
Fallback needs limits.
```

---

## 28. Internal Execution Walkthrough

Backpressure execution for analytics worker:

```text
1. Click events are produced to Kafka by redirect API.
2. Analytics worker consumes from click-events topic.
3. BackpressureMonitor checks queue size, DB latency, DB pool, lag, retry state.
4. If no pressure, listener processes events.
5. Processor writes to analytics DB.
6. Acknowledgment commits offset after successful processing.
7. If queue/DB pressure rises, KafkaBackpressureController pauses listener.
8. Kafka stores new messages while consumer pauses.
9. Worker drains existing in-flight work.
10. Monitor observes pressure falling below resume threshold.
11. KafkaBackpressureController resumes listener.
```

ASCII:

```text
+-------------+      +-------+      +------------------+      +----------+
| Redirect API| ---> | Kafka | ---> | Analytics Worker | ---> | DB       |
+-------------+      +-------+      +------------------+      +----------+
                          |                  |                    |
                          | lag              | queue size          | latency
                          v                  v                    v
                    +-----------------------------------------------+
                    |          Backpressure Monitor                 |
                    +-----------------------------------------------+
                                      |
                        +-------------+-------------+
                        |                           |
                        v                           v
                 pause listener               resume listener
```

Key point:

```text
Backpressure is a control loop.
```

Control loop:

```text
measure pressure
make decision
apply control
measure again
recover carefully
```

ASCII:

```text
+---------+
| Measure |
+---------+
     |
     v
+---------+
| Decide  |
+---------+
     |
     v
+---------+
| Control |
+---------+
     |
     v
+---------+
| Recover |
+---------+
     |
     v
   repeat
```

---

## 29. Testing Strategy

Test backpressure like a production failure.

### Unit tests

Test `BackpressureMonitor`:

```text
queue size below threshold -> inactive
queue size above threshold -> WORKER_QUEUE_FULL
DB latency above threshold -> DB_LATENCY_HIGH
Kafka lag above threshold -> KAFKA_LAG_TOO_HIGH
resume threshold prevents flapping
```

Example:

```java
@Test
void shouldActivateBackpressureWhenQueueIsFull() {
    FakeWorkerMetrics workerMetrics = new FakeWorkerMetrics();
    workerMetrics.setQueueSize(600);

    BackpressureState state = monitor.currentState();

    assertThat(state.isActive()).isTrue();
    assertThat(state.getReason()).isEqualTo(BackpressureReason.WORKER_QUEUE_FULL);
}
```

### Integration tests

Use Testcontainers:

```text
Kafka container
Postgres container
Spring Boot app
```

Test cases:

```text
1. produce many click events
2. slow down DB insert artificially
3. assert listener pauses
4. restore DB speed
5. assert listener resumes
6. assert messages eventually processed
```

### Load tests

Use k6/JMeter/Gatling:

```text
normal traffic
burst traffic
sustained overload
Kafka broker slowdown
DB latency injection
Redis latency injection
```

### Assertions

Check:

```text
redirect p99 does not explode due to analytics
Kafka lag is visible
backpressure.active metric turns true
pause/resume logs exist
worker queue never exceeds bound
DB pool does not exceed safe usage
retry topic does not grow forever
DLT receives poison messages
```

Testing rule:

```text
Do not only test happy throughput.
Test survival under overload.
```

---

## 30. Production Failure Stories

### Failure Story 1: Unbounded Executor Kills Worker

A team creates analytics worker:

```text
Executors.newFixedThreadPool(20)
unbounded LinkedBlockingQueue
```

During campaign:

```text
millions of click events arrive
DB slows
executor queue grows
memory grows
GC pauses
pod OOMKilled
Kafka rebalances
same messages replay
lag grows more
```

Root cause:

```text
unbounded queue hid backpressure until memory died
```

Fix:

```text
bounded queue
pause Kafka when queue full
batch DB writes
metrics on queue size
```

Lesson:

```text
Unbounded queues turn pressure into memory explosions.
```

---

### Failure Story 2: Scaling Consumers Makes DB Worse

Kafka lag grows.

Team scales analytics workers:

```text
5 pods -> 50 pods
```

Result:

```text
DB connection pool explodes
Postgres CPU hits 100%
insert latency rises
timeouts increase
retry storm starts
lag grows faster
```

Root cause:

```text
DB was bottleneck, not consumer count
```

Fix:

```text
batch writes
limit concurrency
pause consumers
increase DB capacity only if needed
move analytics to better storage later
```

Lesson:

```text
Scaling the wrong layer amplifies failure.
```

---

### Failure Story 3: Retry Storm After DB Outage

DB unavailable for 2 minutes.

Worker retry config:

```text
immediate retry
infinite attempts
```

After DB recovers:

```text
old retries flood DB
new messages also flood DB
system remains down even after DB is healthy
```

Root cause:

```text
retry had no backoff, jitter, or max attempts
```

Fix:

```text
delayed retry topics
exponential backoff
max attempts
DLT
```

Lesson:

```text
Bad retries extend outages.
```

---

### Failure Story 4: Analytics Breaks Redirect

Redirect API waits for Kafka send confirmation with long timeout.

Kafka broker becomes slow.

Result:

```text
redirect latency rises from 20ms to 5s
Tomcat threads block
load balancer marks app unhealthy
core product fails
```

Root cause:

```text
non-critical analytics was inside critical redirect path
```

Fix:

```text
short timeout
best-effort publish
outbox if durability required
do not block redirect on analytics
```

Lesson:

```text
Keep critical path clean.
```

---

### Failure Story 5: Cache Fallback Overloads DB

Redis goes down.

API fallback:

```text
on Redis failure, query DB
```

At normal load:

```text
100k redirects/sec
```

DB cannot handle 100k lookup/sec.

Result:

```text
DB collapses
create API fails
redirect fails
entire system down
```

Root cause:

```text
fallback had no limit
```

Fix:

```text
Redis timeout
circuit breaker
local hot cache
DB fallback rate limit
serve stale where safe
```

Lesson:

```text
Fallback paths need backpressure too.
```

---

## 31. Debugging Mindset

When system is slow, ask in order:

```text
1. Is the pressure at API, Kafka, worker, DB, or Redis?
2. Is the bottleneck CPU, IO, connection pool, lock, queue, or dependency latency?
3. Is lag growing or draining?
4. Is the oldest message age increasing?
5. Is worker queue bounded and full?
6. Are retries increasing load?
7. Are we dropping or sampling intentionally?
8. Are critical paths protected?
9. Did autoscaling help or hurt?
10. Is backpressure active and why?
```

Debug map:

```text
High API p99 + Tomcat busy:
    request threads blocked
    dependency timeout too high
    rate limit/backpressure needed

High Kafka lag + normal DB:
    add consumers if partitions allow
    check worker CPU

High Kafka lag + high DB latency:
    DB is bottleneck
    do not blindly add consumers

High retry topic growth:
    downstream failing or poison messages
    add delay / DLT

High DLT:
    schema/data/code bug
    inspect message reasons

High Redis timeout + DB overload:
    fallback storm
    circuit breaker and fallback limit needed
```

Useful commands and checks:

```text
Kafka consumer lag:
    consumer group lag by topic/partition

DB:
    active connections
    slow queries
    locks
    insert latency

App:
    thread dump
    executor queue size
    Hikari metrics
    GC logs
    pod restarts
```

Golden rule:

```text
Before increasing capacity, identify the bottleneck.
```

---

## 32. Common Mistakes

### Mistake 1: Thinking Kafka Solves Backpressure Automatically

Wrong:

```text
We use Kafka, so traffic spikes are solved.
```

Correct:

```text
Kafka buffers messages, but consumers and downstream systems still need backpressure.
```

### Mistake 2: Infinite Retries

Wrong:

```text
Retry until success forever.
```

Correct:

```text
Retry with backoff, jitter, max attempts, then DLT.
```

### Mistake 3: Unbounded Queues

Wrong:

```text
Use unbounded executor queue to avoid rejection.
```

Correct:

```text
Use bounded queue and explicit rejection/backpressure.
```

### Mistake 4: Acknowledging Too Early

Wrong:

```text
Ack Kafka message before async DB write finishes.
```

Correct:

```text
Ack after durable processing if message must not be lost.
```

### Mistake 5: Blind Autoscaling

Wrong:

```text
Lag high means add more consumers.
```

Correct:

```text
Check DB, CPU, partitions, and downstream capacity first.
```

### Mistake 6: No Hysteresis

Wrong:

```text
pause at queue size 500, resume at 499
```

Correct:

```text
pause at 500, resume at 200
```

### Mistake 7: Analytics Blocks Redirect

Wrong:

```text
Redirect waits indefinitely for analytics publish.
```

Correct:

```text
Redirect critical path should not depend on non-critical analytics.
```

### Mistake 8: Silent Dropping

Wrong:

```text
Drop events without metrics.
```

Correct:

```text
Track dropped/sampled events and reasons.
```

### Mistake 9: Fallback Without Limits

Wrong:

```text
Redis failed, send everything to DB.
```

Correct:

```text
Limit fallback, use circuit breaker, protect DB.
```

### Mistake 10: Confusing Rate Limiting With Backpressure

Wrong:

```text
We have user rate limit, so system overload is solved.
```

Correct:

```text
Rate limit handles caller abuse; backpressure handles system pressure.
```

---

## 33. Interview-Ready Explanation

If interviewer asks:

```text
How would you handle backpressure in a URL shortener with Kafka analytics?
```

Strong answer:

```text
I would treat backpressure as a control loop that protects the slowest downstream
component. The redirect path is critical, while click analytics is asynchronous
and less critical. Redirect should not block on slow analytics publishing. Click
events can go to Kafka, but Kafka is only a buffer, not a full solution. I would
monitor consumer lag, oldest message age, worker queue size, DB write latency,
Hikari pool usage, retry topic growth, and DLT count. The analytics worker would
use bounded concurrency, bounded queues, manual acknowledgments, batch writes,
and Kafka pause/resume when downstream pressure is high. If DB latency or worker
queue size crosses thresholds, consumption pauses; when pressure drops below a
lower resume threshold, it resumes. Retries should use backoff, jitter, max
attempts, delayed retry topics, and DLT to avoid retry storms. Under extreme
pressure, I would degrade non-critical analytics by sampling or disabling
enrichment while keeping redirects fast. I would avoid blindly scaling consumers
unless CPU or consumer count is the bottleneck, because if Postgres is the
bottleneck, more consumers make the outage worse.
```

Why this is strong:

```text
1. Protects critical redirect path.
2. Understands Kafka lag and buffering.
3. Explains bounded workers.
4. Mentions DB bottleneck.
5. Uses pause/resume.
6. Handles retries safely.
7. Includes graceful degradation.
8. Avoids blind autoscaling.
9. Mentions metrics.
10. Shows production failure awareness.
```

Senior one-liner:

```text
Backpressure means the system slows intake, pauses consumers, or sheds optional work before overload becomes an outage.
```

---

## 34. Senior Engineer Checklist

Before calling the analytics pipeline production-shaped, confirm:

```text
[ ] Redirect path does not depend on slow analytics.
[ ] Kafka producer has short timeout and failure behavior defined.
[ ] Kafka consumer lag is monitored.
[ ] Oldest unprocessed message age is monitored.
[ ] Worker executor is bounded.
[ ] Worker queue size is monitored.
[ ] Kafka listener can pause and resume.
[ ] Pause threshold and resume threshold are different.
[ ] Hikari pool usage is monitored.
[ ] DB insert latency is monitored.
[ ] Batch writes are considered for analytics.
[ ] Retries use backoff and jitter.
[ ] Retry attempts are capped.
[ ] DLT exists.
[ ] DLT is monitored.
[ ] Poison messages do not retry forever.
[ ] Load shedding policy exists.
[ ] Sampling policy exists if analytics can degrade.
[ ] Dropped/sampled events are counted.
[ ] Redis fallback is limited.
[ ] API returns 429/503 with Retry-After when appropriate.
[ ] Autoscaling decisions consider real bottleneck.
[ ] Alerts exist for lag growth and backpressure active.
[ ] Runbook exists for pressure incidents.
```

If these are checked, your system has survival behavior, not only happy-path throughput.

---

## 35. One-Page Cheat Sheet

```text
Core mental model:
Backpressure is the pressure valve that protects the slowest downstream component.

Main pressure points:
Client -> API
API -> Redis
API -> Postgres
API -> Kafka
Kafka -> Worker
Worker -> DB
Retry -> Worker again

Signals:
Kafka lag
oldest message age
worker queue size
DB latency
Hikari active connections
retry topic growth
DLT count
API p99
Tomcat busy threads

Tools:
rate limiting
bounded queues
timeouts
pause/resume consumers
batch writes
backoff retries
DLT
load shedding
graceful degradation
circuit breakers
sampling

Important distinctions:
Rate limit = caller fairness/abuse control
Backpressure = system overload control
Circuit breaker = stop failing dependency calls
Load shedding = drop lower-priority work

Critical rule:
Redirect path is critical.
Analytics path is async and degradable.

Kafka:
Kafka buffers spikes.
Kafka does not remove the need for backpressure.

DB:
If DB is bottleneck, more workers may worsen outage.

Retry:
Retry with backoff + jitter + max attempts.
Never infinite immediate retry.

Executor:
Never use unbounded queues.
Bound queue and expose pressure.

Ack:
Ack Kafka message only after required durable work finishes.

Degradation:
Keep redirect.
Delay analytics.
Disable enrichment.
Sample/drop low-value events if necessary.

Best interview line:
Backpressure is a control loop: measure pressure, decide, slow intake, recover carefully.
```

---

## 36. One Picture To Remember

```text
                   BACKPRESSURE MENTAL MODEL

              "Do not let fast input kill slow output"


Clients
  |
  v
+-----------------------+
| URL Shortener API     |
| Redirect is critical  |
+-----------------------+
  |              |
  |              +------------------+
  |                                 |
  v                                 v
Redis/Postgres                 Kafka click-events
critical path                  async buffer
                                      |
                                      v
                             +------------------+
                             | Analytics Worker |
                             | bounded queue    |
                             +------------------+
                                      |
                                      v
                             +------------------+
                             | Analytics DB     |
                             | finite capacity  |
                             +------------------+


Pressure Signals:
    Kafka lag
    worker queue full
    DB latency high
    Hikari pool saturated
    retry storm
    Redis timeout

Control Actions:
    pause Kafka consumer
    reduce concurrency
    batch writes
    retry later
    send to DLT
    sample analytics
    shed optional work
    return 429/503 when needed


FINAL MEMORY:

Queue absorbs short spikes.
Backpressure handles sustained overload.
Critical path survives.
Optional work degrades first.
```

---

## 37. Final Retention Summary

Remember these five sentences:

```text
1. Backpressure protects the system when upstream is faster than downstream.
2. Kafka buffers events, but workers and databases still need explicit pressure control.
3. Bounded queues, pause/resume, timeouts, retries with backoff, and DLT prevent overload from becoming collapse.
4. Redirect is the critical path; analytics can be delayed, sampled, or degraded under pressure.
5. Before scaling consumers, identify the bottleneck, because scaling the wrong layer can make the outage worse.
```

After this chapter, the high-scale analytics phase has a survival model:

```text
032_Kafka_Click_Analytics.md
033_Async_Analytics_Worker.md
034_Backpressure_Handling.md
```

Next possible chapter:

```text
035_Idempotent_Analytics_Processing.md
```
