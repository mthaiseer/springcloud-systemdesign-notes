# 024_HikariCP_Tuning.md
# MiniURLShortener — HikariCP Tuning

> Core mental model: **HikariCP is the controlled bridge between your Spring Boot threads and PostgreSQL connections. It protects the database from unlimited connections, but if the pool is too small, requests wait; if it is too large, the database gets overloaded. Tuning HikariCP means balancing application concurrency, database capacity, and p99 latency.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What Is A Connection Pool?](#4-what-is-a-connection-pool)
- [5. Why Opening DB Connections Per Request Is Bad](#5-why-opening-db-connections-per-request-is-bad)
- [6. HikariCP In Spring Boot](#6-hikaricp-in-spring-boot)
- [7. Request-To-DB Connection Flow](#7-request-to-db-connection-flow)
- [8. Pool Size Mental Model](#8-pool-size-mental-model)
- [9. Too Small vs Too Large Pool](#9-too-small-vs-too-large-pool)
- [10. Important Hikari Settings](#10-important-hikari-settings)
- [11. Maximum Pool Size](#11-maximum-pool-size)
- [12. Minimum Idle](#12-minimum-idle)
- [13. Connection Timeout](#13-connection-timeout)
- [14. Idle Timeout And Max Lifetime](#14-idle-timeout-and-max-lifetime)
- [15. Leak Detection Threshold](#15-leak-detection-threshold)
- [16. PostgreSQL max_connections](#16-postgresql-max_connections)
- [17. Multi-Pod Pool Sizing](#17-multi-pod-pool-sizing)
- [18. Read-Heavy URL Shortener Pool Strategy](#18-read-heavy-url-shortener-pool-strategy)
- [19. Redis Cache And HikariCP](#19-redis-cache-and-hikaricp)
- [20. Spring Boot Configuration](#20-spring-boot-configuration)
- [21. Step-by-Step Dry Runs](#21-step-by-step-dry-runs)
- [22. Internal Execution Walkthrough](#22-internal-execution-walkthrough)
- [23. Load Testing And Tuning Method](#23-load-testing-and-tuning-method)
- [24. Metrics And Observability](#24-metrics-and-observability)
- [25. Production Failure Stories](#25-production-failure-stories)
- [26. Debugging Mindset](#26-debugging-mindset)
- [27. Common Mistakes](#27-common-mistakes)
- [28. Interview-Ready Explanation](#28-interview-ready-explanation)
- [29. Senior Engineer Checklist](#29-senior-engineer-checklist)
- [30. One-Page Cheat Sheet](#30-one-page-cheat-sheet)
- [31. One Picture To Remember](#31-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener uses PostgreSQL for source-of-truth data:

```text
shortCode
longUrl
status
expiresAt
createdAt
```

Even after Redis cache is added, PostgreSQL is still used when:

```text
creating short URLs
redirect cache misses
duplicate alias checks
admin updates
block/delete operations
testcontainers integration tests
```

Spring Boot does not open unlimited database connections directly.

It uses a connection pool.

By default, Spring Boot commonly uses HikariCP.

The redirect flow may look simple:

```text
GET /abc123
```

But when Redis misses:

```text
Spring Boot needs a DB connection.
```

If many requests miss Redis at the same time:

```text
many application threads ask Hikari for DB connections
```

If pool is exhausted:

```text
threads wait
latency grows
p99 explodes
eventually timeout
```

ASCII:

```text
Requests
  | | | | | | |
  v v v v v v v
+-------------------+
| Spring Boot Threads|
+---------+---------+
          |
          v
+-------------------+
| HikariCP Pool     |
| limited DB conns  |
+---------+---------+
          |
          v
+-------------------+
| PostgreSQL        |
+-------------------+
```

Production memory:

```text
HikariCP is not only a config detail.
It is a backpressure boundary between app and DB.
```

---

## 2. The One Core Mental Model

The core mental model:

```text
DB CONNECTIONS ARE EXPENSIVE, SO BORROW THEM BRIEFLY AND RETURN THEM FAST
```

A connection pool owns a fixed number of reusable database connections.

Application threads borrow a connection when needed.

After query/transaction ends, connection is returned.

ASCII:

```text
Hikari Pool

+------------------------------------------------+
| conn1 | conn2 | conn3 | conn4 | conn5 | conn6  |
+------------------------------------------------+
    ^       ^       ^
    |       |       |
 threadA threadB threadC borrow temporarily
```

One-line memory:

```text
A pool is a limited checkout counter for database connections.
```

Good behavior:

```text
borrow connection
execute short query/transaction
return connection quickly
```

Bad behavior:

```text
borrow connection
do slow external API call
hold transaction open
forget to close resource
return late or never
```

For MiniURLShortener:

```text
redirect DB fallback should borrow connection briefly
create API transaction should be short
analytics must not hold DB connection on redirect path
```

---

## 3. Problem Statement

Tune HikariCP for MiniURLShortener.

The tuning must support:

```text
1. Fast create API DB writes.
2. Fast redirect DB fallback on Redis miss.
3. Protection against DB connection explosion.
4. Predictable p99 latency.
5. Clear timeout behavior when pool is exhausted.
6. Safe multi-pod connection count.
7. Metrics for active/idle/pending connections.
8. Leak detection during development/staging.
9. Load-test-driven tuning.
10. Interview-ready explanation.
```

The goal is not:

```text
largest possible pool
```

The goal is:

```text
right-sized pool
```

A right-sized pool:

```text
keeps DB busy but not overloaded
keeps app waits low
keeps p99 predictable
respects PostgreSQL max_connections
```

---

## 4. What Is A Connection Pool?

A database connection is a network session between application and PostgreSQL.

Opening it is expensive because it may involve:

```text
TCP connection
authentication
session setup
memory on DB side
backend process/thread resources
```

A connection pool avoids doing that per request.

ASCII:

```text
Without pool:

Request 1 -> open connection -> query -> close
Request 2 -> open connection -> query -> close
Request 3 -> open connection -> query -> close


With pool:

Startup -> open reusable connections

Request 1 -> borrow -> query -> return
Request 2 -> borrow -> query -> return
Request 3 -> borrow -> query -> return
```

Pool benefits:

```text
faster DB access
controlled connection count
less DB overhead
backpressure when app is too busy
metrics for DB pressure
```

Connection pool is like:

```text
a taxi stand
```

If taxis are available:

```text
you get one immediately
```

If all taxis are busy:

```text
you wait
```

If wait is too long:

```text
timeout
```

---

## 5. Why Opening DB Connections Per Request Is Bad

Opening one DB connection per request sounds simple.

But at high RPS:

```text
1000 RPS
```

Opening/closing per request creates:

```text
1000 connection handshakes/sec
1000 authentications/sec
high DB CPU
high network churn
slow requests
```

ASCII:

```text
Request storm
  |
  v
open close open close open close
  |
  v
PostgreSQL overwhelmed by connection churn
```

PostgreSQL connections are not free.

Each connection consumes:

```text
memory
backend process resources
locks/session state
CPU scheduling overhead
```

Pool fixes this by reusing connections.

But pool must be sized carefully.

Too few:

```text
app waits
```

Too many:

```text
DB overloads
```

---

## 6. HikariCP In Spring Boot

Spring Boot commonly auto-configures HikariCP when JDBC/JPA is on classpath.

Typical dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

HikariCP becomes the DataSource.

ASCII:

```text
Spring Boot
   |
   v
DataSource bean
   |
   v
HikariDataSource
   |
   v
PostgreSQL connections
```

Application code usually does not call Hikari directly.

It uses:

```text
Spring Data Repository
JdbcTemplate
EntityManager
@Transactional service
```

Hikari works underneath.

Flow:

```text
Repository query
   |
   v
Hibernate/JDBC asks DataSource for connection
   |
   v
Hikari gives connection
   |
   v
SQL executes
   |
   v
Connection returned
```

---

## 7. Request-To-DB Connection Flow

Redirect cache miss flow:

```text
GET /abc123
```

ASCII:

```text
Client
  |
  v
RedirectController
  |
  v
RedirectService
  |
  v
Redis MISS
  |
  v
Repository.findByShortCode()
  |
  v
HikariCP borrow connection
  |
  v
PostgreSQL SELECT
  |
  v
HikariCP return connection
  |
  v
Redis SET
  |
  v
302 redirect
```

Important:

```text
The connection should be held only during DB work.
```

Bad flow:

```text
borrow connection
query DB
call external service
sleep/retry
write Redis slowly
return connection
```

This holds DB connection too long.

Rule:

```text
Do not keep transactions open while doing non-DB work.
```

For redirect:

```text
DB lookup should be short.
Cache fill can happen after DB entity is loaded.
```

---

## 8. Pool Size Mental Model

Pool size controls maximum concurrent DB work per app instance.

Example:

```text
maximum-pool-size = 10
```

Means:

```text
at most 10 active DB connections from this Spring Boot pod
```

If 100 request threads need DB at same time:

```text
10 get connections
90 wait
```

ASCII:

```text
100 app threads need DB

Hikari pool size 10

+-----------------------------------+
| 10 active connections             |
+-----------------------------------+
| 90 waiting threads                |
+-----------------------------------+
```

Pool size is not same as RPS.

A pool of 10 can handle high RPS if each query is fast.

Simple formula intuition:

```text
needed connections ≈ DB work concurrency
```

DB work concurrency depends on:

```text
RPS needing DB
average DB time
```

Example:

```text
DB fallback RPS = 500
DB query time = 10 ms = 0.01 sec

concurrency ≈ 500 * 0.01 = 5 connections
```

Add headroom:

```text
maybe pool size 10-15
```

But always verify by load test.

---

## 9. Too Small vs Too Large Pool

### Pool Too Small

Symptoms:

```text
Hikari pending threads high
connection timeout errors
app p99 high
DB CPU may be low
active connections always maxed
```

ASCII:

```text
App wants DB
  |
  v
Hikari pool full
  |
  v
threads wait
  |
  v
p99 grows
```

### Pool Too Large

Symptoms:

```text
PostgreSQL max_connections pressure
DB CPU high
context switching high
queries slower
locks/contention increase
all pods overload DB together
```

ASCII:

```text
Too many app connections
      |
      v
PostgreSQL overloaded
      |
      v
all queries slower
      |
      v
connections stay busy longer
      |
      v
even more waiting
```

Pool tuning is balance:

```text
enough connections to use DB efficiently
not so many that DB collapses
```

Senior memory:

```text
A bigger pool does not make the database faster.
It only allows more concurrent pressure.
```

---

## 10. Important Hikari Settings

Main settings:

```text
maximumPoolSize
minimumIdle
connectionTimeout
idleTimeout
maxLifetime
leakDetectionThreshold
poolName
```

Table:

```text
+------------------------+------------------------------------------+
| Setting                | Meaning                                  |
+------------------------+------------------------------------------+
| maximumPoolSize        | max connections in pool                  |
| minimumIdle            | idle connections kept ready              |
| connectionTimeout      | max wait for a connection                |
| idleTimeout            | how long idle conn can stay              |
| maxLifetime            | max age of a connection                  |
| leakDetectionThreshold | warn if connection held too long         |
| poolName               | useful name in logs/metrics              |
+------------------------+------------------------------------------+
```

Golden rule:

```text
Tune max pool size first.
Tune timeout behavior second.
Use metrics always.
```

Do not randomly copy settings.

Start with safe baseline.

Load test.

Observe.

Adjust.

---

## 11. Maximum Pool Size

`maximumPoolSize` is the most important setting.

Example:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
```

Meaning:

```text
this app instance can open at most 10 DB connections
```

If you run 5 pods:

```text
5 pods * 10 = 50 app DB connections
```

ASCII:

```text
Pod 1 -> 10 connections
Pod 2 -> 10 connections
Pod 3 -> 10 connections
Pod 4 -> 10 connections
Pod 5 -> 10 connections

Total = 50
```

You must compare total with PostgreSQL capacity.

Do not tune one pod only.

Production tuning is:

```text
per-pod pool size * pod count <= safe DB connection budget
```

Example:

```text
PostgreSQL max_connections = 200
reserve 50 for admin/maintenance/other services
available for app = 150
pods = 10

max per pod <= 15
```

Start conservative.

Increase only if DB can handle it and pending wait exists.

---

## 12. Minimum Idle

`minimumIdle` controls idle connections kept ready.

Example:

```yaml
spring:
  datasource:
    hikari:
      minimum-idle: 5
```

Meaning:

```text
try to keep 5 idle connections ready
```

For fixed-size pool, many teams set:

```text
minimumIdle = maximumPoolSize
```

or leave Hikari defaults.

Simple production approach:

```text
maximumPoolSize: 10
minimumIdle: 10
```

Pros:

```text
connections ready
less cold-start wait
predictable
```

Cons:

```text
keeps DB connections open even when idle
```

For low traffic services:

```text
minimumIdle lower than maximumPoolSize may save resources
```

For latency-sensitive redirect service:

```text
keeping warm connections is often good
```

But remember:

```text
many pods * minimumIdle can consume DB connections even when idle
```

---

## 13. Connection Timeout

`connectionTimeout` is how long a thread waits to borrow a connection.

Example:

```yaml
spring:
  datasource:
    hikari:
      connection-timeout: 200ms
```

If pool is full:

```text
thread waits up to 200ms
```

If still no connection:

```text
SQLTransientConnectionException
```

This may become:

```text
500 or controlled error
```

ASCII:

```text
Thread asks pool
   |
   +-- connection available -> proceed
   |
   +-- none available -> wait
             |
             +-- connection returned before timeout -> proceed
             |
             +-- timeout -> fail fast
```

Too high timeout:

```text
requests wait too long
p99 explodes
threads pile up
```

Too low timeout:

```text
temporary small spike fails too quickly
```

Good starting point depends on SLA.

For redirect:

```text
short timeout is better than hanging
```

Example:

```text
200ms - 500ms
```

But tune with real load.

---

## 14. Idle Timeout And Max Lifetime

### idleTimeout

Controls how long idle connections can stay before being removed.

Example:

```yaml
idle-timeout: 600000 # 10 minutes
```

### maxLifetime

Controls maximum age of a connection.

Example:

```yaml
max-lifetime: 1800000 # 30 minutes
```

Why maxLifetime matters:

```text
network devices
load balancers
database restart policies
server-side connection limits
```

If DB/network kills connections before Hikari does:

```text
app may see broken connection errors
```

Rule:

```text
Hikari maxLifetime should be shorter than database/network connection kill timeout.
```

ASCII:

```text
Bad:

Network kills connection at 30m
Hikari maxLifetime = 60m
App may borrow dead connection


Better:

Network kills at 30m
Hikari maxLifetime = 25m
Hikari retires first
```

For PostgreSQL local development, defaults are often fine.

For production behind proxies/cloud networking, review.

---

## 15. Leak Detection Threshold

A connection leak means:

```text
connection borrowed but not returned quickly
```

In Spring/JPA, leaks usually happen due to:

```text
manual JDBC not closing resources
long transactions
streaming result sets
blocking inside transaction
external calls inside transaction
```

Hikari setting:

```yaml
spring:
  datasource:
    hikari:
      leak-detection-threshold: 2000
```

Meaning:

```text
log warning if connection held more than 2 seconds
```

Use in:

```text
development
staging
debug incidents
```

Be careful in production:

```text
too low threshold creates noisy logs
```

ASCII:

```text
borrow connection at t=0
still not returned at t=2s
Hikari logs possible leak
```

Important:

```text
Leak detection does not fix leak.
It points you to code holding connection too long.
```

---

## 16. PostgreSQL max_connections

PostgreSQL has a server-side connection limit.

Check:

```sql
SHOW max_connections;
```

Example:

```text
max_connections = 200
```

But not all 200 are available for app.

Reserve for:

```text
admin access
migrations
monitoring
other services
replication
maintenance
emergency debugging
```

Safe app budget:

```text
max_connections - reserved
```

Example:

```text
max_connections = 200
reserved = 50
app budget = 150
```

If app has 10 pods:

```text
max pool per pod <= 150 / 10 = 15
```

ASCII:

```text
PostgreSQL max_connections = 200

+--------------------+
| app budget 150     |
+--------------------+
| reserved 50        |
+--------------------+
```

Do not set:

```text
each pod pool = 50
10 pods = 500 connections
```

That exceeds DB capacity.

---

## 17. Multi-Pod Pool Sizing

In Kubernetes, one service may run many pods.

Each pod has its own Hikari pool.

ASCII:

```text
                PostgreSQL
                    ^
                    |
      +-------------+-------------+
      |             |             |
    Pod 1         Pod 2         Pod 3
  pool 10       pool 10       pool 10

Total = 30 DB connections
```

Formula:

```text
total connections = pod count * maximumPoolSize
```

If autoscaling increases pods:

```text
total connections increases automatically
```

Example:

```text
normal pods = 5
max pool = 10
total = 50

autoscale pods = 20
total = 200
```

If DB budget is 150, autoscaling can break DB.

Therefore define:

```text
max pod count
max pool size
DB connection budget
```

Together.

Production rule:

```text
HPA scaling and Hikari pool sizing must be designed together.
```

---

## 18. Read-Heavy URL Shortener Pool Strategy

MiniURLShortener redirect should mostly hit Redis.

Therefore DB traffic should be:

```text
create API writes
redirect cache misses
admin updates
```

If cache hit rate is high:

```text
DB fallback low
pool size can be moderate
```

Example:

```text
redirect RPS = 20,000
cache hit rate = 99%
DB fallback = 200 RPS
DB query avg = 10ms
needed concurrency ≈ 200 * 0.01 = 2
```

Add writes/admin/headroom:

```text
pool size 10 may be enough per pod depending on pod count and workload
```

But if Redis fails:

```text
fallback jumps from 200 RPS to 20,000 RPS
```

No pool can magically save DB.

In Redis failure:

```text
Hikari pool becomes protective backpressure
```

It limits DB concurrency.

That may cause timeouts, but protects DB from unlimited app threads.

ASCII:

```text
Redis healthy:
    most reads -> Redis
    few reads -> Hikari -> DB

Redis down:
    many reads want DB
    Hikari limits concurrency
    some requests timeout
    DB protected somewhat
```

Senior design:

```text
Use Redis, Hikari limits, timeouts, rate limiting, and circuit breaker together.
```

---

## 19. Redis Cache And HikariCP

Redis cache hit:

```text
does not borrow DB connection
```

Redis cache miss:

```text
borrows DB connection
```

ASCII:

```text
GET /abc123
   |
   v
Redis
   |
   +-- HIT -> no Hikari
   |
   +-- MISS -> Hikari -> PostgreSQL
```

This means cache hit rate affects Hikari pressure.

If cache hit rate drops:

```text
Hikari active connections rise
pending threads rise
DB CPU rises
redirect p99 rises
```

Metrics correlation:

```text
cache hit rate down
DB fallback up
Hikari active up
Hikari pending up
p99 up
```

Debugging read-heavy incidents often starts here.

Golden rule:

```text
Hikari metrics reveal when cache misses are reaching the database.
```

---

## 20. Spring Boot Configuration

Example `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/miniurl
    username: miniurl
    password: miniurl
    hikari:
      pool-name: MiniUrlHikariPool
      maximum-pool-size: 10
      minimum-idle: 10
      connection-timeout: 300ms
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 0
```

Development leak detection:

```yaml
spring:
  datasource:
    hikari:
      leak-detection-threshold: 2000
```

Production note:

```text
Set leakDetectionThreshold only when needed or choose a safe non-noisy value.
```

Environment-based config:

```yaml
app:
  db:
    pool-size: ${DB_POOL_SIZE:10}
```

Direct Spring config with env:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: ${DB_POOL_SIZE:10}
      minimum-idle: ${DB_MIN_IDLE:10}
      connection-timeout: ${DB_CONNECTION_TIMEOUT:300ms}
```

For Kubernetes:

```text
DB_POOL_SIZE should be chosen with pod count and DB budget.
```

---

## 21. Step-by-Step Dry Runs

### Dry Run 1: Pool Has Free Connection

State:

```text
maximumPoolSize = 10
active = 3
idle = 7
```

Request needs DB.

Flow:

```text
1. Thread asks Hikari for connection.
2. Idle connection exists.
3. Hikari gives connection immediately.
4. Query runs.
5. Connection returns to pool.
```

ASCII:

```text
idle available
   |
   v
borrow immediately
   |
   v
query
   |
   v
return
```

Result:

```text
low latency
no waiting
```

---

### Dry Run 2: Pool Exhausted But Connection Returns

State:

```text
maximumPoolSize = 10
active = 10
idle = 0
pending = 5
connectionTimeout = 300ms
```

Flow:

```text
1. New request asks for connection.
2. Pool is full.
3. Thread waits.
4. After 50ms another query finishes.
5. Connection returns.
6. Waiting thread borrows it.
7. Query succeeds.
```

Result:

```text
request slower but successful
```

---

### Dry Run 3: Pool Exhausted And Timeout

State:

```text
maximumPoolSize = 10
active = 10
idle = 0
connectionTimeout = 300ms
```

Flow:

```text
1. Request waits for connection.
2. No connection returns within 300ms.
3. Hikari throws timeout exception.
4. API returns controlled error or 500 depending handler.
```

ASCII:

```text
wait for connection
   |
   v
300ms passed
   |
   v
timeout
```

Symptom:

```text
Connection is not available, request timed out
```

This usually means:

```text
pool too small
queries too slow
transactions too long
DB overloaded
cache miss spike
connection leak
```

---

### Dry Run 4: Pool Too Large

State:

```text
10 pods
pool size 50 each
total possible connections = 500
PostgreSQL max_connections = 200
```

Flow:

```text
1. Pods start.
2. Connections grow.
3. DB hits connection limit.
4. New connections fail.
5. App startup/query failures occur.
```

Lesson:

```text
Per-pod pool size must respect total cluster connection budget.
```

---

### Dry Run 5: Connection Leak

Flow:

```text
1. Code borrows connection manually.
2. Exception happens.
3. Connection is not closed.
4. Connection never returns to pool.
5. Active count stays high.
6. Pool slowly exhausts.
7. Hikari leak detection logs warning.
```

Fix:

```text
Use try-with-resources.
Use Spring repositories.
Keep transactions short.
```

---

## 22. Internal Execution Walkthrough

Spring Data JPA query:

```java
repository.findByShortCode("abc123");
```

Internal flow:

```text
1. Service method calls repository.
2. Spring Data invokes EntityManager.
3. Hibernate prepares SQL.
4. Hibernate asks DataSource for connection.
5. Hikari checks pool.
6. If idle connection exists, returns it.
7. SQL executes on PostgreSQL.
8. ResultSet is read.
9. Entity/projection is mapped.
10. Transaction/session completes.
11. Connection returns to Hikari.
```

ASCII:

```text
Repository
   |
   v
Hibernate
   |
   v
HikariDataSource
   |
   +-- idle connection -> give
   |
   +-- none -> wait
   |
   +-- timeout -> exception
   |
   v
PostgreSQL
```

Important:

```text
Connection is usually tied to transaction/session lifecycle.
```

If transaction stays open too long:

```text
connection stays borrowed too long
```

So transaction boundaries and Hikari tuning are connected.

---

## 23. Load Testing And Tuning Method

Do not tune Hikari by guessing.

Use load testing.

Example stages:

```text
100 RPS
500 RPS
1,000 RPS
5,000 RPS
10,000 RPS
```

For each stage observe:

```text
p50/p95/p99 latency
error rate
Hikari active connections
Hikari idle connections
Hikari pending threads
connection timeout count
DB CPU
DB query latency
Redis hit rate
```

ASCII tuning loop:

```text
Run load test
   |
   v
Observe metrics
   |
   v
Identify bottleneck
   |
   v
Change one setting
   |
   v
Run again
```

If Hikari pending is high and DB CPU is low:

```text
pool may be too small
or connectionTimeout too low
```

If DB CPU is high and queries slow:

```text
pool may be too large
or DB/index/query is bottleneck
```

If active connections are low but p99 high:

```text
bottleneck may be Redis, app CPU, GC, network, or external dependency
```

Tuning rule:

```text
Change one variable at a time.
```

---

## 24. Metrics And Observability

Spring Boot Actuator + Micrometer exposes Hikari metrics.

Useful metrics:

```text
hikaricp.connections.active
hikaricp.connections.idle
hikaricp.connections.pending
hikaricp.connections.max
hikaricp.connections.min
hikaricp.connections.timeout
hikaricp.connections.creation
hikaricp.connections.usage
```

Interpretation:

```text
active:
    currently borrowed

idle:
    ready connections

pending:
    threads waiting

timeout:
    failed to get connection

usage:
    how long connection is held
```

ASCII:

```text
Hikari Pool Metrics

+---------+------------------------------+
| active  | DB work happening now        |
| idle    | spare capacity               |
| pending | app threads waiting          |
| timeout | requests failed to borrow    |
| usage   | connection hold duration     |
+---------+------------------------------+
```

Important alerts:

```text
pending > 0 sustained
timeouts > 0
active == max sustained
usage p95 high
DB CPU high
DB connections near limit
```

Dashboard should show together:

```text
redirect p99
cache hit rate
DB fallback count
Hikari active/pending
DB CPU
slow query count
```

---

## 25. Production Failure Stories

### Failure Story 1: Pool Too Small Caused p99 Spike

Traffic increased.

Hikari pool size remained 5.

DB CPU was only 30%.

But app p99 became 2 seconds.

Root cause:

```text
requests waited for DB connections
```

Fix:

```text
increase pool moderately
verify DB CPU
optimize query
monitor pending connections
```

Lesson:

```text
Low DB CPU plus high Hikari pending often means pool bottleneck.
```

---

### Failure Story 2: Pool Too Large Crushed PostgreSQL

Team increased pool from 10 to 100 per pod.

There were 10 pods.

Potential connections became 1000.

PostgreSQL became overloaded.

Root cause:

```text
bigger pool created too much DB concurrency
```

Fix:

```text
reduce pool
use DB connection budget
add PgBouncer later if needed
improve caching
```

Lesson:

```text
A bigger pool can make everything slower.
```

---

### Failure Story 3: Redis Outage Exposed Pool Pressure

Redis went down.

Redirect cache hit rate dropped to 0%.

Every redirect wanted DB.

Hikari pool filled.

Pending threads rose.

Timeouts started.

Root cause:

```text
DB fallback path overwhelmed due to cache outage
```

Fix:

```text
short Redis timeout
Hikari as backpressure
rate limiting/circuit breaker
alert on cache hit drop
```

Lesson:

```text
Cache failure turns into DB pressure. Hikari metrics show it fast.
```

---

### Failure Story 4: Long Transaction Held Connections

Service method opened transaction.

Inside transaction it called external API.

External API was slow.

Connections stayed borrowed.

Pool exhausted.

Root cause:

```text
non-DB work inside transaction
```

Fix:

```text
keep transaction short
move external call outside transaction
use async workflow
```

Lesson:

```text
Connection pool tuning cannot fix bad transaction boundaries.
```

---

### Failure Story 5: Connection Leak In Manual JDBC

A manual JDBC method forgot to close ResultSet/Connection on exception.

Pool slowly drained.

After some hours, app timed out.

Root cause:

```text
connection leak
```

Fix:

```text
try-with-resources
prefer Spring-managed templates/repositories
enable leak detection in staging
```

Lesson:

```text
Leaks look like gradual pool exhaustion.
```

---

## 26. Debugging Mindset

When DB latency or timeout happens, ask:

```text
Are Hikari active connections at max?
Are pending threads rising?
Are connection timeouts happening?
Is DB CPU high or low?
Are queries slow?
Are transactions holding connections too long?
Did Redis hit rate drop?
Did pod count increase?
Did total possible connections exceed DB budget?
Any connection leak warnings?
Was maxLifetime compatible with network/DB timeout?
```

Debug map:

```text
active=max, pending high, DB CPU low:
    pool too small or connections held too long

active=max, pending high, DB CPU high:
    DB overloaded or queries slow

timeouts after Redis outage:
    cache miss storm hitting DB

gradual active growth:
    possible leak

DB max_connections errors:
    total pool across pods too high
```

Useful SQL:

```sql
SHOW max_connections;

SELECT count(*)
FROM pg_stat_activity;

SELECT state, count(*)
FROM pg_stat_activity
GROUP BY state;
```

Useful app metrics:

```text
hikaricp.connections.active
hikaricp.connections.pending
hikaricp.connections.timeout
hikaricp.connections.usage
```

Golden rule:

```text
Debug Hikari with app metrics and DB metrics together.
```

---

## 27. Common Mistakes

### Mistake 1: Increasing Pool Size Blindly

Wrong:

```text
p99 high -> increase pool to 100
```

Correct:

```text
check pending, DB CPU, query latency, cache miss rate
```

### Mistake 2: Ignoring Total Connections Across Pods

Wrong:

```text
pool size 30 looks fine for one pod
```

Correct:

```text
pool size 30 * 20 pods = 600 DB connections
```

### Mistake 3: Long Transactions

Wrong:

```text
hold DB connection while calling Redis/external API
```

Correct:

```text
keep DB transaction short
```

### Mistake 4: No Connection Timeout

Wrong:

```text
requests wait too long
```

Correct:

```text
set reasonable connectionTimeout
```

### Mistake 5: No Hikari Metrics

Wrong:

```text
guess pool behavior
```

Correct:

```text
monitor active, idle, pending, timeout, usage
```

### Mistake 6: Treating Hikari As DB Fix

Wrong:

```text
slow query -> increase pool
```

Correct:

```text
fix query/index first
```

### Mistake 7: Ignoring Redis Hit Rate

Wrong:

```text
Hikari timeouts are only DB problem
```

Correct:

```text
check if cache miss spike increased DB demand
```

### Mistake 8: Leak Detection Always On Too Low In Prod

Wrong:

```text
leak threshold 500ms in production
```

Correct:

```text
use carefully; avoid noisy false positives
```

---

## 28. Interview-Ready Explanation

If interviewer asks:

```text
How do you tune HikariCP in a Spring Boot service?
```

Strong answer:

```text
I treat HikariCP as the controlled bridge between Spring Boot request threads and PostgreSQL.
The most important setting is maximumPoolSize, but it must be sized with the total number of
pods and PostgreSQL max_connections in mind. A larger pool does not make the database faster;
it only allows more concurrent DB pressure. I monitor active, idle, pending, timeout, and usage
metrics. If pending is high while DB CPU is low, the pool may be too small or connections are
held too long. If DB CPU is high and queries slow, increasing pool size can make things worse.
For a read-heavy URL shortener, Redis should keep most redirects away from DB, so Hikari mainly
handles creates and cache misses. I tune by load testing, watching cache hit rate, DB fallback,
Hikari pending, DB CPU, and p99 latency together. I also keep transactions short and use leak
detection in staging to catch connections held too long.
```

Senior version:

```text
Connection pool tuning is capacity coordination, not a magic performance knob. The right pool
size is the one that keeps the database efficiently used without causing app-side waiting or
database-side overload.
```

Why this is strong:

```text
1. Explains pool as concurrency boundary.
2. Mentions total pods.
3. Mentions DB max_connections.
4. Mentions key metrics.
5. Explains too small vs too large.
6. Connects Redis hit rate to DB pressure.
7. Mentions load testing.
8. Mentions transaction boundaries and leaks.
```

---

## 29. Senior Engineer Checklist

Before calling HikariCP production-shaped, confirm:

```text
[ ] maximumPoolSize is explicitly configured
[ ] total connections across pods fit DB budget
[ ] PostgreSQL max_connections is known
[ ] reserved DB connections are considered
[ ] connectionTimeout is reasonable
[ ] maxLifetime is compatible with infra
[ ] transactions are short
[ ] Redis cache hit rate is monitored
[ ] DB fallback count is monitored
[ ] Hikari active connections are monitored
[ ] Hikari pending threads are monitored
[ ] Hikari timeout count is monitored
[ ] connection usage duration is monitored
[ ] load tests include cache miss scenarios
[ ] leak detection is used in staging/debugging
[ ] pool size is not increased blindly
[ ] HPA max pods and pool size are coordinated
```

---

## 30. One-Page Cheat Sheet

```text
Core mental model:
Borrow DB connection briefly. Return fast.

HikariCP:
controlled pool of reusable DB connections

Important settings:
maximumPoolSize
minimumIdle
connectionTimeout
idleTimeout
maxLifetime
leakDetectionThreshold

Pool too small:
pending high
timeouts
DB CPU may be low
p99 high

Pool too large:
DB overloaded
max_connections pressure
queries slower
context switching

Formula:
total app connections = pods * maximumPoolSize

Budget:
PostgreSQL max_connections - reserved = app budget

URL shortener:
Redis hit -> no DB connection
Redis miss -> Hikari -> PostgreSQL

Must monitor:
hikaricp.connections.active
hikaricp.connections.idle
hikaricp.connections.pending
hikaricp.connections.timeout
hikaricp.connections.usage
DB CPU
DB query latency
cache hit rate
redirect p99

Golden rule:
A bigger pool does not make DB faster.
```

---

## 31. One Picture To Remember

```text
                    HIKARICP TUNING MENTAL MODEL

                      "Controlled bridge to DB"

Requests
  | | | | | | | | |
  v v v v v v v v v
+-------------------------+
| Spring Boot Threads     |
| many concurrent requests|
+------------+------------+
             |
             v
+-------------------------+
| HikariCP Pool           |
| limited DB connections  |
|                         |
| [c1][c2][c3][c4][c5]    |
+------------+------------+
             |
             v
+-------------------------+
| PostgreSQL              |
| finite CPU/connections  |
+-------------------------+


IF POOL TOO SMALL:
    app waits -> p99 high

IF POOL TOO LARGE:
    DB overloaded -> all queries slow

RIGHT SIZE:
    enough concurrency
    low pending
    DB not overloaded
    predictable p99


FINAL MEMORY:

Hikari does not create database capacity.
It controls how much application pressure reaches the database.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. HikariCP is the reusable connection pool between Spring Boot and PostgreSQL.
2. Pool size controls DB concurrency per pod, not total RPS directly.
3. Total DB connections equal pod count multiplied by maximumPoolSize, so Kubernetes scaling and DB budget must be planned together.
4. Too small a pool causes app-side waiting; too large a pool overloads PostgreSQL.
5. Tune Hikari using load tests and metrics: active, idle, pending, timeouts, usage, DB CPU, cache hit rate, and p99 latency.
```

Next chapter:

```text
025_Rate_Limiting_For_Create_API.md
```
