# 031_Read_Replica_Design.md
# MiniURLShortener — Read Replica Design

> Core mental model: **A read replica is a read-scaling copy of the primary database. The primary owns truth for writes; replicas absorb read traffic, but they may be slightly behind.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Primary vs Replica](#4-primary-vs-replica)
- [5. Read-Heavy URL Shortener Reality](#5-read-heavy-url-shortener-reality)
- [6. Replication Flow Mental Model](#6-replication-flow-mental-model)
- [7. Synchronous vs Asynchronous Replication](#7-synchronous-vs-asynchronous-replication)
- [8. Replication Lag](#8-replication-lag)
- [9. Read-After-Write Consistency](#9-read-after-write-consistency)
- [10. Routing Reads and Writes](#10-routing-reads-and-writes)
- [11. Spring Boot DataSource Routing Design](#11-spring-boot-datasource-routing-design)
- [12. Transaction ReadOnly Routing](#12-transaction-readonly-routing)
- [13. Redirect API With Replica](#13-redirect-api-with-replica)
- [14. Create API With Primary](#14-create-api-with-primary)
- [15. Cache + Read Replica Together](#15-cache--read-replica-together)
- [16. Failure Modes](#16-failure-modes)
- [17. Replica Health Checks](#17-replica-health-checks)
- [18. Lag-Aware Read Routing](#18-lag-aware-read-routing)
- [19. Load Balancing Across Replicas](#19-load-balancing-across-replicas)
- [20. Analytics Reads vs Redirect Reads](#20-analytics-reads-vs-redirect-reads)
- [21. Step-by-Step Dry Runs](#21-step-by-step-dry-runs)
- [22. Internal Execution Walkthrough](#22-internal-execution-walkthrough)
- [23. Testing Strategy](#23-testing-strategy)
- [24. Production Failure Stories](#24-production-failure-stories)
- [25. Debugging Mindset](#25-debugging-mindset)
- [26. Common Mistakes](#26-common-mistakes)
- [27. Interview-Ready Explanation](#27-interview-ready-explanation)
- [28. Senior Engineer Checklist](#28-senior-engineer-checklist)
- [29. One-Page Cheat Sheet](#29-one-page-cheat-sheet)
- [30. One Picture To Remember](#30-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener is naturally read-heavy.

Create API:

```text
POST /api/v1/urls
```

Redirect API:

```text
GET /{shortCode}
```

In real traffic, one shortened link may be created once and clicked thousands or millions of times.

Example:

```text
1 create request
100,000 redirect requests
```

If every redirect hits the primary database, the primary becomes overloaded.

Bad design:

```text
All writes + all reads -> one primary DB
```

ASCII:

```text
                 +-------------------+
Create API ----> |                   |
Redirect API --> |   Primary DB      |
Redirect API --> |   writes + reads  |
Redirect API --> |                   |
                 +-------------------+
                         |
                         v
                  CPU / IOPS / locks
                    become bottleneck
```

Better design:

```text
Writes go to primary.
Reads can go to replicas.
```

ASCII:

```text
Create API  ---> Primary DB
                  |
                  | replication
                  v
Redirect API ---> Read Replica 1
Redirect API ---> Read Replica 2
Redirect API ---> Read Replica 3
```

This chapter teaches how to design read replicas without blindly memorizing.

Production memory:

```text
Read replicas increase read capacity, but introduce replica lag and consistency tradeoffs.
```

---

## 2. The One Core Mental Model

A read replica is a **follower copy**.

The primary is the writer.
The replica follows the primary.

ASCII:

```text
                 writes
Client/API -----------------> Primary DB
                                  |
                                  | WAL / binlog / replication stream
                                  v
                            Read Replica
                                  |
                                  v
                                reads
```

One-line memory:

```text
Primary owns truth now; replica owns a copy slightly later.
```

For MiniURLShortener:

```text
Create short URL    -> primary
Redirect short URL  -> cache first, then replica
Admin update status -> primary
Analytics reports   -> replica / analytics store
```

The tradeoff:

```text
More read capacity
       vs
Possible stale reads
```

A senior engineer never says:

```text
Use replicas and problem solved.
```

A senior engineer says:

```text
Use replicas for scalable reads, but define which reads can tolerate lag and which reads require primary consistency.
```

---

## 3. Problem Statement

Design read replica support for MiniURLShortener.

The design must support:

```text
1. All writes go to primary.
2. Read-heavy redirect lookups can use replicas.
3. Admin and consistency-sensitive reads can use primary.
4. Application avoids writing to replicas.
5. Replica lag is monitored.
6. If replica is unhealthy, reads fall back safely.
7. Cache and replicas work together.
8. Spring Boot code can route readOnly transactions to replicas.
9. Interview explanation is clear and production-shaped.
```

Out of scope for this chapter:

```text
1. Full Patroni failover setup.
2. Kubernetes operator-based Postgres management.
3. Deep WAL internals implementation.
4. Multi-region active-active databases.
5. Cassandra/DynamoDB replacement design.
```

This chapter focuses on backend application design and system design thinking.

---

## 4. Primary vs Replica

Primary database:

```text
Accepts writes.
Stores source of truth.
Handles INSERT / UPDATE / DELETE.
Streams changes to replicas.
```

Read replica:

```text
Usually read-only.
Receives changes from primary.
Serves SELECT queries.
May lag behind primary.
```

ASCII:

```text
+--------------------------+
| Primary                  |
|                          |
| INSERT short_urls        |
| UPDATE status            |
| DELETE / soft delete     |
| SELECT if strong needed  |
+------------+-------------+
             |
             | replication stream
             v
+--------------------------+
| Replica                  |
|                          |
| SELECT redirects         |
| SELECT analytics         |
| SELECT read dashboards   |
| no application writes    |
+--------------------------+
```

Command split:

```text
Writes:
    create short URL
    update click count if stored synchronously
    block short code
    expire/delete short code

Reads:
    redirect lookup
    dashboard list
    public preview
    analytics query
```

Golden rule:

```text
Never send writes to a read replica.
```

---

## 5. Read-Heavy URL Shortener Reality

A URL shortener is mostly redirects.

Example traffic:

```text
Create requests:       100 RPS
Redirect requests:  50,000 RPS
Admin requests:         10 RPS
Analytics queries:      50 RPS
```

Without replicas:

```text
Primary handles 50,160 RPS.
```

With cache + replicas:

```text
Cache handles hot redirects.
Replicas handle cache misses and read dashboards.
Primary handles writes.
```

ASCII:

```text
                         +----------------+
Redirect Request ------> | Redis Cache    |
                         +-------+--------+
                                 |
                    hit          | miss
                    |            v
                    |     +---------------+
                    |     | Read Replica  |
                    |     +---------------+
                    |
                    v
                302 Redirect

Create Request --------> Primary DB
```

This matters because the primary must preserve write performance.

If redirect reads overload the primary:

```text
writes slow down
connection pool saturates
transactions wait longer
replication may lag more
p99 latency explodes
```

Read replicas protect the write path.

---

## 6. Replication Flow Mental Model

In Postgres-style replication, the primary writes changes to WAL.

WAL means Write-Ahead Log.

Simple mental model:

```text
Database first records what changed in a log.
Replica replays that log.
```

ASCII:

```text
INSERT INTO short_urls(...)
        |
        v
+-----------------------+
| Primary DB            |
| table updated         |
| WAL record generated  |
+-----------+-----------+
            |
            | stream WAL
            v
+-----------------------+
| Replica DB            |
| receives WAL          |
| replays WAL           |
| table becomes updated |
+-----------------------+
```

Important consequence:

```text
The replica does not magically update at the exact same instant.
It catches up by replaying changes.
```

This catching-up delay is replica lag.

---

## 7. Synchronous vs Asynchronous Replication

### Asynchronous Replication

Primary commits without waiting for replica confirmation.

ASCII:

```text
Client write
   |
   v
Primary commits quickly
   |
   +---- response success to client
   |
   +---- later replica receives change
```

Pros:

```text
fast writes
less coupling
common for read scaling
```

Cons:

```text
replica can lag
recent writes may not appear immediately on replica
```

### Synchronous Replication

Primary waits for replica acknowledgment before commit is considered successful.

ASCII:

```text
Client write
   |
   v
Primary writes
   |
   v
Replica confirms
   |
   v
Client gets success
```

Pros:

```text
stronger durability/consistency
less data loss during failover
```

Cons:

```text
slower writes
replica/network problem can affect primary writes
```

For MiniURLShortener read scaling, common choice:

```text
asynchronous replicas for read traffic
```

Why?

```text
Redirect reads can usually tolerate small lag if cache/read-after-write rules are designed properly.
```

---

## 8. Replication Lag

Replica lag means the replica is behind the primary.

Example:

```text
T=10:00:00 create shortCode abc123 on primary
T=10:00:01 user clicks abc123
Replica has not replayed abc123 yet
Replica returns not found
```

ASCII:

```text
Time --->

Primary:  insert abc123  -------------------- has abc123
Replica:  no abc123 ---- delay ---- replay --- has abc123
                    ^
                    |
             bad read window
```

Symptoms:

```text
new link returns 404 briefly
blocked link still redirects briefly
updated destination not visible immediately
analytics dashboard stale
```

Replica lag can be caused by:

```text
high write volume
slow replica disk
network delay
long-running queries on replica
replica CPU pressure
vacuum/replay conflicts
large transactions
```

Senior rule:

```text
Replica lag is not a bug by itself. Ignoring replica lag in product behavior is the bug.
```

---

## 9. Read-After-Write Consistency

Read-after-write consistency means:

```text
After I create/update something, I can immediately read it.
```

Problem:

```text
Create API writes to primary.
Redirect API reads from replica.
Replica may not have the new row yet.
```

Bad user experience:

```text
User creates short URL.
User immediately opens it.
Redirect returns 404.
```

Solutions:

### Option 1: Read Own Writes From Primary Briefly

After create, route reads for that shortCode to primary for a short time.

```text
new shortCode -> primary read for 5-30 seconds
```

### Option 2: Populate Cache On Write

After create, write the mapping into Redis.

```text
Create API writes primary
Create API writes Redis cache
Immediate redirect hits cache
```

ASCII:

```text
Create abc123
   |
   +--> Primary DB
   |
   +--> Redis cache abc123 -> longUrl

Immediate Redirect abc123
   |
   v
Redis hit
   |
   v
302 success
```

### Option 3: Client Wait / Retry

Usually not ideal for URL shortener redirect path.

Best for MiniURLShortener:

```text
Use cache-on-write for create response, and optionally primary fallback for very new codes.
```

---

## 10. Routing Reads and Writes

The application must choose the correct database.

Simple routing rule:

```text
write transaction  -> primary
readOnly transaction -> replica
strong read -> primary
```

ASCII:

```text
                 +----------------------+
HTTP Request --> | Spring Service Layer |
                 +----------+-----------+
                            |
              +-------------+-------------+
              |                           |
              v                           v
      @Transactional              @Transactional(readOnly=true)
      write needed                read-only okay
              |                           |
              v                           v
         Primary DB                 Read Replica
```

But be careful:

```text
readOnly=true does not automatically mean safe to use replica.
```

Some reads need primary:

```text
read immediately after create
admin verifies just-updated status
payment/order-like consistency workflows
security block enforcement immediately after block
```

For URL shortener:

```text
Redirect lookup can use cache + replica.
Admin update/read can use primary.
```

---

## 11. Spring Boot DataSource Routing Design

Spring Boot can route queries using an `AbstractRoutingDataSource`.

Mental model:

```text
One logical DataSource.
Internally chooses primary or replica based on context.
```

ASCII:

```text
Repository
   |
   v
RoutingDataSource
   |
   +-- context = WRITE --> primaryDataSource
   |
   +-- context = READ  --> replicaDataSource
```

High-level classes:

```text
DataSourceType enum
DataSourceContextHolder
RoutingDataSource
DataSourceConfig
Transaction/readOnly detector
```

Example enum:

```java
public enum DataSourceType {
    PRIMARY,
    REPLICA
}
```

Context holder:

```java
public final class DataSourceContextHolder {

    private static final ThreadLocal<DataSourceType> CONTEXT = new ThreadLocal<>();

    private DataSourceContextHolder() {
    }

    public static void usePrimary() {
        CONTEXT.set(DataSourceType.PRIMARY);
    }

    public static void useReplica() {
        CONTEXT.set(DataSourceType.REPLICA);
    }

    public static DataSourceType get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
```

Routing datasource:

```java
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType type = DataSourceContextHolder.get();
        return type == null ? DataSourceType.PRIMARY : type;
    }
}
```

Default should be primary.

Why?

```text
If routing context is missing, accidental primary reads are safer than accidental replica writes.
```

---

## 12. Transaction ReadOnly Routing

A common strategy:

```text
@Transactional(readOnly = true) -> replica
@Transactional                  -> primary
```

But routing must happen before the connection is acquired.

Service example:

```java
@Service
public class UrlRedirectService {

    private final ShortUrlRepository repository;

    public UrlRedirectService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public RedirectTarget findRedirectTarget(String shortCode) {
        ShortUrlEntity entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));

        if (entity.isBlocked()) {
            throw new ShortCodeBlockedException(shortCode);
        }

        if (entity.isExpired()) {
            throw new ShortCodeExpiredException(shortCode);
        }

        return new RedirectTarget(entity.getLongUrl());
    }
}
```

Create service:

```java
@Service
public class ShortUrlCreateService {

    private final ShortUrlRepository repository;

    public ShortUrlCreateService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CreateShortUrlResponse create(CreateShortUrlRequest request) {
        ShortUrlEntity entity = new ShortUrlEntity();
        entity.setLongUrl(request.getLongUrl());
        entity.setShortCode(generateShortCode());

        ShortUrlEntity saved = repository.save(entity);

        return new CreateShortUrlResponse(saved.getShortCode());
    }
}
```

Important warning:

```text
@Transactional(readOnly=true) is not a magic read-replica guarantee.
You must configure routing correctly and test it.
```

---

## 13. Redirect API With Replica

Redirect path priority:

```text
1. Validate shortCode format.
2. Check Redis cache.
3. If cache hit, redirect.
4. If cache miss, read from replica.
5. If replica misses and shortCode is very new, optional primary fallback.
6. Cache result.
7. Redirect.
```

ASCII:

```text
GET /abc123
   |
   v
Validate shortCode
   |
   v
Redis lookup
   |
   +-- hit -----------------------> 302
   |
   v
Read Replica lookup
   |
   +-- found -> cache -> 302
   |
   +-- not found
          |
          v
   optional primary fallback
          |
          +-- found -> cache -> 302
          +-- missing -> 404
```

Why optional primary fallback?

```text
It protects read-after-write when replica lag exists.
```

But do not fallback for every miss blindly at huge scale.

Risk:

```text
Bot scans random short codes.
Every replica miss hits primary.
Primary gets attacked indirectly.
```

Better fallback rule:

```text
Only fallback to primary for recently created codes if known from cache/token/context.
For random public misses, return 404 from replica/cache path.
```

For early project version:

```text
Cache-on-write is simpler and safer than primary fallback on every miss.
```

---

## 14. Create API With Primary

Create path must use primary.

Flow:

```text
POST /api/v1/urls
   |
   v
validate request
   |
   v
generate shortCode
   |
   v
insert into primary
   |
   v
populate Redis cache
   |
   v
return response
```

ASCII:

```text
Create Request
     |
     v
+----------------+
| Service        |
+-------+--------+
        |
        v
+----------------+
| Primary DB     |
| INSERT row     |
+-------+--------+
        |
        v
+----------------+
| Redis Cache    |
| code -> URL    |
+-------+--------+
        |
        v
Response to client
```

Why cache after DB commit?

```text
Do not cache a mapping that failed to commit.
```

Best production approach:

```text
write DB in transaction
publish/cache after commit
```

In Spring, you can use transaction synchronization or outbox/event pattern later.

Simple version:

```text
Save DB first.
Then cache.
If cache write fails, DB is still correct.
Redirect may read from replica later.
```

---

## 15. Cache + Read Replica Together

Cache and replicas solve different problems.

Cache:

```text
Fast in-memory hot key lookup.
Reduces database reads.
Can become stale.
```

Read replica:

```text
Durable database copy.
Scales read queries.
Can lag behind primary.
```

Together:

```text
Cache first.
Replica second.
Primary for writes and strong reads.
```

ASCII:

```text
Redirect read path:

Client
  |
  v
Redis Cache
  |
  +-- hit -> redirect
  |
  +-- miss
        |
        v
    Read Replica
        |
        +-- found -> cache -> redirect
        +-- not found -> 404 / controlled fallback
```

Design rule:

```text
Use cache for hot redirects.
Use replicas for cache misses and non-hot reads.
Use primary for writes and strong consistency.
```

Common misunderstanding:

```text
If we have Redis, we do not need replicas.
```

Wrong.

Why replicas still matter:

```text
cache cold starts
cache evictions
dashboard queries
analytics reads
backup read workload
cache failure fallback
```

---

## 16. Failure Modes

Read replica design introduces new failure modes.

### Failure 1: Replica Down

```text
Application cannot connect to replica.
```

Options:

```text
route reads to another replica
fallback to primary carefully
return degraded error for non-critical dashboards
```

### Failure 2: Replica Lag High

```text
Replica is alive but stale.
```

Options:

```text
remove replica from read pool
route strong reads to primary
alert engineers
reduce heavy analytics queries
```

### Failure 3: Accidental Writes to Replica

```text
Application sends INSERT/UPDATE to replica.
```

Result:

```text
SQL error
or worse, split-brain-like confusion in bad setups
```

### Failure 4: Primary Overloaded by Fallback

```text
Replica misses cause massive primary fallback.
```

Bot scan example:

```text
GET /aaaaaa
GET /aaaaab
GET /aaaaac
...
```

Every miss going to primary destroys the primary.

### Failure 5: Stale Block Status

Admin blocks malicious shortCode on primary.
Replica lags.
Redirect still succeeds briefly.

Fix options:

```text
cache invalidation
blocklist cache
strong primary read for blocked/security-sensitive codes
short TTL
```

---

## 17. Replica Health Checks

Health check must ask more than:

```text
Can I connect?
```

Better checks:

```text
1. Can connect to replica.
2. Replica is not in recovery problem state.
3. Replication lag is below threshold.
4. Query latency is acceptable.
5. Connection pool is not exhausted.
```

ASCII:

```text
Replica candidate
      |
      v
+------------------+
| connection okay? |
+--------+---------+
         |
         v
+------------------+
| lag acceptable?  |
+--------+---------+
         |
         v
+------------------+
| latency okay?    |
+--------+---------+
         |
         v
Use for reads
```

Example thresholds:

```text
redirect reads: lag <= 1-2 seconds preferred
analytics reads: lag <= 30-120 seconds may be okay
admin strong reads: primary only
```

Threshold depends on product behavior.

---

## 18. Lag-Aware Read Routing

Lag-aware routing means:

```text
Do not send sensitive reads to stale replicas.
```

Routing table:

```text
+-------------------------+----------------------+----------------+
| Read Type               | Lag Tolerance        | Target         |
+-------------------------+----------------------+----------------+
| immediate after create  | near zero            | cache/primary  |
| normal redirect         | low, cache first     | cache/replica  |
| admin status check      | zero                 | primary        |
| analytics dashboard     | medium/high          | replica        |
| reports                 | high                 | replica        |
+-------------------------+----------------------+----------------+
```

ASCII:

```text
Read request
   |
   v
Is strong consistency needed?
   |
   +-- yes -> primary
   |
   +-- no
        |
        v
Is replica lag acceptable?
        |
        +-- yes -> replica
        +-- no  -> primary or degraded response
```

Senior design point:

```text
Different reads have different consistency requirements.
Do not route all SELECT queries the same way.
```

---

## 19. Load Balancing Across Replicas

One replica may not be enough.

Multiple replicas:

```text
Replica 1
Replica 2
Replica 3
```

Routing strategy options:

```text
round robin
least connections
latency-aware
lag-aware
weighted routing
```

ASCII:

```text
                 +-------------------+
Read Request --> | Replica Router    |
                 +----+-------+------+
                      |       |
          +-----------+       +-------------+
          v                         v
   Replica 1                   Replica 2
   lag 100ms                   lag 700ms

Router prefers healthy + low-lag replicas.
```

For first implementation:

```text
one replica datasource is enough
```

For production design:

```text
use PgBouncer/HAProxy/cloud proxy or application-level replica pool
```

Do not overcomplicate early code.

But understand the interview answer:

```text
I would keep writes on primary, distribute reads over healthy replicas, and remove replicas from the pool when lag or errors cross threshold.
```

---

## 20. Analytics Reads vs Redirect Reads

Not all reads are equal.

Redirect read:

```text
latency critical
p99 important
usually one key lookup
cache-friendly
```

Analytics read:

```text
less latency sensitive
may scan/aggregate many rows
can hurt database if run on primary
```

ASCII:

```text
Redirect read:
short_code -> long_url
small indexed lookup

Analytics read:
GROUP BY day, country, device
large aggregation
```

Design:

```text
Redirect reads -> Redis + replica
Analytics reads -> replica / OLAP store later
Writes -> primary
```

Production warning:

```text
Heavy analytics queries can make replica lag worse.
```

Later evolution:

```text
Kafka click events -> analytics store
Postgres primary remains for core URL metadata
```

This avoids making replicas do every job.

---

## 21. Step-by-Step Dry Runs

### Dry Run 1: Normal Create

Request:

```http
POST /api/v1/urls
```

Flow:

```text
1. Controller receives request.
2. Service validates longUrl.
3. Transaction starts on primary.
4. Row is inserted into primary.
5. Transaction commits.
6. Cache is populated with shortCode -> longUrl.
7. Response returns shortCode.
8. Primary replication stream later sends row to replica.
```

Result:

```text
Primary has row immediately.
Cache has row immediately if cache write succeeds.
Replica has row after replication catches up.
```

---

### Dry Run 2: Immediate Redirect After Create

Timeline:

```text
T0: create abc123
T1: click abc123 immediately
```

Flow:

```text
1. Redirect API checks Redis.
2. Cache contains abc123 because create populated it.
3. Redirect returns 302 without needing replica.
```

This avoids replica lag problem.

ASCII:

```text
Replica behind? yes
Cache has value? yes
Redirect succeeds.
```

---

### Dry Run 3: Cache Miss, Replica Hit

Situation:

```text
shortCode old123 exists.
Redis evicted it.
Replica is caught up.
```

Flow:

```text
1. Redirect checks Redis.
2. Cache miss.
3. Service uses readOnly transaction.
4. Query goes to replica.
5. Replica returns row.
6. Service checks status and expiry.
7. Cache is repopulated.
8. Redirect returns 302.
```

---

### Dry Run 4: Replica Lag Causes Miss

Situation:

```text
new code abc123 is in primary.
not yet in replica.
cache write failed.
```

Flow:

```text
1. Redirect checks Redis: miss.
2. Replica lookup: not found.
3. If no fallback, user receives 404 briefly.
4. After lag clears, replica returns row.
```

Lesson:

```text
Cache-on-write and controlled primary fallback protect user experience.
```

---

### Dry Run 5: Admin Blocks Code

Situation:

```text
Admin blocks spam1.
Primary updated immediately.
Replica lags by 2 seconds.
Cache still has old ACTIVE mapping.
```

Bad flow:

```text
Redirect hits stale cache.
User still redirects to blocked URL.
```

Correct design:

```text
1. Admin update writes primary.
2. Invalidate cache key spam1.
3. Optional blocklist cache is updated.
4. Redirect path checks blocklist/cache before redirecting.
```

Lesson:

```text
Security-sensitive updates need cache invalidation and stronger consistency.
```

---

## 22. Internal Execution Walkthrough

Redirect with routing:

```text
1. HTTP request enters Spring Boot.
2. Controller extracts shortCode.
3. Service checks cache.
4. Cache miss happens.
5. Service method annotated @Transactional(readOnly=true) starts.
6. Routing logic selects replica datasource.
7. Repository executes SELECT on replica.
8. Entity is returned.
9. Service checks status and expiry.
10. Service caches result.
11. Controller returns 302 Location header.
```

ASCII:

```text
+--------+      +------------+      +-------+      +---------------+
| Client | ---> | Controller | ---> | Cache | ---> | RedirectSvc   |
+--------+      +------------+      +-------+      +-------+-------+
                                                            |
                                                            v
                                                   RoutingDataSource
                                                            |
                                                     readOnly=true
                                                            |
                                                            v
                                                       Read Replica
```

Create with routing:

```text
1. HTTP request enters controller.
2. Service method uses normal @Transactional.
3. Routing defaults to primary.
4. Repository INSERT runs on primary.
5. Commit succeeds.
6. Cache is populated.
7. Primary streams WAL/binlog to replica.
```

ASCII:

```text
+--------+      +------------+      +-------------+      +------------+
| Client | ---> | Controller | ---> | CreateSvc   | ---> | Primary DB |
+--------+      +------------+      +-------------+      +-----+------+
                                                               |
                                                               v
                                                         Read Replica
```

---

## 23. Testing Strategy

Test the routing, not only business result.

### Unit Tests

```text
readOnly service method should select replica context
write service method should select primary context
context should clear after request
fallback should not happen for random misses unless allowed
```

### Integration Tests

Use two test databases or datasource wrappers:

```text
primary datasource records writes
replica datasource records reads
```

Test cases:

```text
createShortUrl -> primary
redirect cache miss -> replica
admin update -> primary
analytics query -> replica
strong read -> primary
```

### Lag Simulation Tests

Simulate:

```text
primary has row
replica does not have row yet
cache missing
```

Expected behavior depends on policy:

```text
return 404
or controlled primary fallback
or retry once
```

### Cache-on-Write Test

```text
create URL
verify Redis contains shortCode mapping
immediate redirect uses cache
```

Testing rule:

```text
Do not assume routing works because code compiles.
Prove which datasource receives which query.
```

---

## 24. Production Failure Stories

### Failure Story 1: New Links Return 404

Users create short URLs and immediately click them.
Some receive 404.

Root cause:

```text
Redirect reads from async replica.
Replica lags behind primary.
Cache was not populated on create.
```

Fix:

```text
populate cache after create
or route new-code reads to primary briefly
or use controlled retry/fallback
```

Lesson:

```text
Read replicas need read-after-write design.
```

---

### Failure Story 2: Primary Dies From Fallback Storm

Bot scans random short codes.
Replica returns not found.
Application falls back to primary for every miss.

Result:

```text
primary connection pool saturates
writes slow down
p99 explodes
```

Fix:

```text
no primary fallback for random public misses
negative caching for not-found codes
rate limiting
bot protection
```

Lesson:

```text
Fallback must be controlled, not automatic.
```

---

### Failure Story 3: Blocked URL Still Redirects

Admin blocks malicious URL.
Users still redirect for a few seconds.

Root cause:

```text
stale cache or lagging replica
```

Fix:

```text
invalidate cache on block
maintain blocklist cache
route security-sensitive checks strongly
use short TTL for risky entries
```

Lesson:

```text
Security updates need stronger consistency than normal reads.
```

---

### Failure Story 4: Analytics Query Slows Replica

Dashboard runs heavy GROUP BY query on replica.
Replica CPU spikes.
Replication replay slows.
Redirect reads become stale.

Fix:

```text
separate analytics replica
move analytics to OLAP store later
limit query windows
add indexes/materialized views
```

Lesson:

```text
A replica is not infinite capacity. Heavy reads can create lag.
```

---

### Failure Story 5: Wrong Transaction Annotation

Developer forgets `readOnly=true` on redirect service.
All redirect cache misses go to primary.

Result:

```text
read replica exists but is unused
primary overloaded
```

Fix:

```text
routing tests
query metrics by datasource
code review checklist
```

Lesson:

```text
Architecture must be verified by metrics and tests.
```

---

## 25. Debugging Mindset

When read replica behavior looks wrong, ask:

```text
Is the query going to primary or replica?
Is the service method inside @Transactional(readOnly=true)?
Was the connection acquired before routing context was set?
Is replica lag high?
Is Redis stale or missing?
Is cache invalidation happening on updates?
Is fallback hitting primary too often?
Are bots causing miss storms?
Are analytics queries slowing replay?
```

Debug map:

```text
New link 404:
    check cache-on-write
    check replica lag
    check primary fallback policy

Blocked link still redirects:
    check cache invalidation
    check blocklist cache
    check replica lag

Primary overloaded:
    check read routing
    check fallback rate
    check cache hit ratio

Replica stale:
    check write volume
    check replica CPU/disk
    check long-running queries
```

Useful metrics:

```text
cache_hit_ratio
replica_lag_seconds
primary_connections_active
replica_connections_active
primary_qps
replica_qps
redirect_p99_latency
fallback_to_primary_count
not_found_count
```

Golden rule:

```text
Replica bugs are often routing, lag, cache, or fallback bugs.
```

---

## 26. Common Mistakes

### Mistake 1: Thinking Replica Is Always Fresh

Wrong:

```text
I wrote to primary, so replica immediately has it.
```

Correct:

```text
Async replica may lag. Design read-after-write behavior.
```

### Mistake 2: Sending All SELECTs to Replica

Wrong:

```text
Any read can use replica.
```

Correct:

```text
Some reads need primary consistency.
```

### Mistake 3: Fallback to Primary on Every Miss

Wrong:

```text
Replica miss -> primary miss check always.
```

Correct:

```text
Fallback only when justified. Avoid bot-driven primary overload.
```

### Mistake 4: No Cache Invalidation

Wrong:

```text
Update DB and forget cache.
```

Correct:

```text
Invalidate/update cache on block, delete, destination change, expiry policy changes.
```

### Mistake 5: Heavy Analytics on Same Replica

Wrong:

```text
Use redirect replica for huge reports.
```

Correct:

```text
Separate traffic classes or move analytics to event pipeline/OLAP.
```

### Mistake 6: No Routing Tests

Wrong:

```text
Assume @Transactional(readOnly=true) works.
```

Correct:

```text
Test and monitor which datasource receives queries.
```

### Mistake 7: No Lag Alerts

Wrong:

```text
Replica connection is healthy, so everything is fine.
```

Correct:

```text
Healthy but stale is still dangerous.
```

---

## 27. Interview-Ready Explanation

If interviewer asks:

```text
How would you use read replicas in a URL shortener?
```

Strong answer:

```text
A URL shortener is highly read-heavy, so I keep all writes on the primary database and route read-heavy traffic, especially redirect cache misses and dashboards, to read replicas. The primary remains the source of truth for create, update, block, and delete operations. The redirect path should check Redis first, then use a replica on cache miss. Because replicas are usually asynchronous, I explicitly handle replica lag. For read-after-write, I populate Redis after creating a short URL, and for consistency-sensitive admin/security reads I use primary or invalidate cache immediately. I also avoid falling back to primary on every replica miss because random short-code scans can overload the primary. In production I monitor replica lag, cache hit ratio, primary/replica QPS, connection pools, p99 latency, and fallback count. Replicas increase read capacity, but they do not remove consistency design.
```

Why this is strong:

```text
1. Separates primary writes from replica reads.
2. Mentions read-heavy redirect workload.
3. Includes cache-first design.
4. Understands replica lag.
5. Handles read-after-write.
6. Avoids unsafe primary fallback.
7. Mentions security-sensitive reads.
8. Mentions production metrics.
```

Senior one-liner:

```text
Read replicas scale reads, but every replica design must define what can be stale and what must be strongly consistent.
```

---

## 28. Senior Engineer Checklist

Before calling this production-shaped, confirm:

```text
[ ] All writes route to primary
[ ] Redirect cache misses can route to replica
[ ] Admin writes route to primary
[ ] Consistency-sensitive reads route to primary
[ ] Redis is checked before replica for redirect path
[ ] Cache is populated after create
[ ] Cache invalidation exists for block/delete/update
[ ] Replica lag is measured
[ ] Lag threshold is defined per read type
[ ] Replica health includes lag, not only connection
[ ] Primary fallback is controlled
[ ] Random public misses do not all hit primary
[ ] Negative caching considered for not-found scans
[ ] Heavy analytics separated or controlled
[ ] Routing tests prove datasource selection
[ ] Metrics separate primary and replica QPS
[ ] Alerts exist for high replica lag
[ ] Runbook explains stale-read debugging
```

---

## 29. One-Page Cheat Sheet

```text
Core mental model:
Primary owns truth now.
Replica owns a copy slightly later.

Why replicas:
Scale read-heavy traffic.
Protect primary write capacity.

URL shortener split:
Create/update/block/delete -> primary
Redirect cache miss -> replica
Immediate strong read -> primary/cache
Analytics -> replica/OLAP later

Main danger:
Replica lag.

Read-after-write fixes:
1. Cache-on-write
2. Primary read briefly for new object
3. Controlled retry/fallback

Redirect path:
Validate shortCode
Redis cache
Replica lookup
Optional controlled primary fallback
Cache result
302 redirect

Never:
Fallback to primary for every random miss
Assume replica is fresh
Send writes to replica
Forget cache invalidation
Run huge analytics on redirect replica blindly

Metrics:
cache_hit_ratio
replica_lag_seconds
primary_qps
replica_qps
fallback_count
p99_redirect_latency
connection_pool_usage
```

---

## 30. One Picture To Remember

```text
                 READ REPLICA DESIGN MENTAL MODEL

                         "Writes to truth, reads to copies"

                         +--------------------+
Create / Update / Block  |                    |
-----------------------> |     PRIMARY DB     |
                         |  source of truth   |
                         +---------+----------+
                                   |
                                   | replication stream
                                   | may lag
                                   v
                         +--------------------+
                         |   READ REPLICA     |
                         |  scalable reads    |
                         +---------+----------+
                                   ^
                                   |
                         cache miss|
                                   |
Client Redirect ---> Redis Cache --+
        |              |
        |              +-- hit --> 302 Redirect
        |
        +-- create just happened?
                |
                +-- cache-on-write protects read-after-write

FINAL MEMORY:

Primary is truth.
Replica is scale.
Cache is speed.
Lag is the tradeoff.
Routing is the discipline.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Read replicas scale read traffic by copying data from the primary.
2. The primary must handle writes; replicas should handle safe reads.
3. Async replicas can lag, so read-after-write must be designed explicitly.
4. Cache-on-write is the best first protection for immediate redirects after create.
5. Never use uncontrolled primary fallback, because misses and bot scans can overload the primary.
```

Next chapter suggestion:

```text
032_Multi_Replica_Routing_And_Lag_Aware_Reads.md
```
