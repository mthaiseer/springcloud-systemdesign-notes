# 020_Read_Heavy_System_Design.md
# MiniURLShortener — Read Heavy System Design

> Core mental model: **A URL shortener is a read-heavy system because one short URL may be created once but redirected thousands, millions, or billions of times. The production design goal is to keep the redirect path extremely fast, cache-friendly, safe, and resilient while protecting the database from repeated hot reads.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Read Heavy vs Write Heavy](#4-read-heavy-vs-write-heavy)
- [5. MiniURLShortener Traffic Shape](#5-miniurlshortener-traffic-shape)
- [6. Redirect Hot Path](#6-redirect-hot-path)
- [7. Baseline Simple Design](#7-baseline-simple-design)
- [8. Why Baseline Breaks](#8-why-baseline-breaks)
- [9. Read Heavy Target Architecture](#9-read-heavy-target-architecture)
- [10. Cache-Aside Mental Model](#10-cache-aside-mental-model)
- [11. Redis Cache For Redirects](#11-redis-cache-for-redirects)
- [12. Database Indexing For Reads](#12-database-indexing-for-reads)
- [13. Read Replicas](#13-read-replicas)
- [14. CDN And Browser Cache](#14-cdn-and-browser-cache)
- [15. Negative Caching](#15-negative-caching)
- [16. Cache TTL Strategy](#16-cache-ttl-strategy)
- [17. Hot Key Problem](#17-hot-key-problem)
- [18. Consistency Tradeoffs](#18-consistency-tradeoffs)
- [19. Write Path vs Read Path Separation](#19-write-path-vs-read-path-separation)
- [20. Step-by-Step Dry Runs](#20-step-by-step-dry-runs)
- [21. Internal Execution Walkthrough](#21-internal-execution-walkthrough)
- [22. Capacity Thinking](#22-capacity-thinking)
- [23. p99 Latency Thinking](#23-p99-latency-thinking)
- [24. Production Failure Stories](#24-production-failure-stories)
- [25. Debugging Mindset](#25-debugging-mindset)
- [26. Common Mistakes](#26-common-mistakes)
- [27. Java/Spring Boot Implementation Sketch](#27-javaspring-boot-implementation-sketch)
- [28. Interview-Ready Explanation](#28-interview-ready-explanation)
- [29. Senior Engineer Checklist](#29-senior-engineer-checklist)
- [30. One-Page Cheat Sheet](#30-one-page-cheat-sheet)
- [31. One Picture To Remember](#31-one-picture-to-remember)

---

## 1. Why This Exists

A URL shortener has two main APIs:

```text
Create API:
POST /api/v1/urls

Redirect API:
GET /{shortCode}
```

The create API is usually write-heavy only for creators.

The redirect API is public, open, and extremely read-heavy.

Example:

```text
One user creates:
    https://sho.rt/sale2026

Then shares it on:
    Twitter / X
    LinkedIn
    WhatsApp
    Email campaign
    Ads
    Blog
    QR code

That one row may receive:
    10 reads
    10,000 reads
    10,000,000 reads
```

If every redirect hits PostgreSQL:

```text
GET /sale2026 -> DB
GET /sale2026 -> DB
GET /sale2026 -> DB
GET /sale2026 -> DB
...
```

The database becomes the bottleneck.

Production goal:

```text
Redirect must be extremely fast.
Redirect must avoid database whenever possible.
Redirect must remain correct enough for expiry, blocked links, and deleted links.
Redirect must survive spikes.
```

Read-heavy system design is about moving repeated reads away from expensive core storage.

Simple memory:

```text
Create once.
Redirect many times.
Cache the many.
Protect the database.
```

---

## 2. The One Core Mental Model

The core mental model is:

```text
READ PATH MUST BE SHORTER THAN WRITE PATH
```

Write path can do more work:

```text
validate URL
generate code
check alias
insert DB row
publish event later
return response
```

Read path should do minimum work:

```text
receive shortCode
lookup cached mapping
return redirect
```

ASCII:

```text
WRITE PATH - slower is acceptable

Client
  |
  v
Validate -> Generate -> DB Insert -> Response


READ PATH - must be tiny

Client
  |
  v
Cache Lookup -> Redirect
```

A read-heavy system is designed by asking:

```text
Can I answer this request without touching the main database?
```

For URL redirect:

```text
shortCode -> longUrl
```

This mapping is ideal for caching.

ASCII:

```text
                  HOT READ MENTAL MODEL

                  shortCode
                     |
                     v
              +--------------+
              | Redis Cache  |
              +------+-------+
                     |
          hit        | miss
       +-------------+-------------+
       |                           |
       v                           v
  return longUrl              PostgreSQL lookup
                                   |
                                   v
                              refill cache
                                   |
                                   v
                              return longUrl
```

One-line memory:

```text
The database is the source of truth, but the cache is the read accelerator.
```

---

## 3. Problem Statement

Design the read-heavy architecture for MiniURLShortener redirect API.

The system must support:

```text
1. Very high redirect read traffic.
2. Low p99 latency.
3. Low database load.
4. Correct redirect for active links.
5. 404 for missing/deleted links.
6. 403 for blocked links.
7. 410 for expired links.
8. Safe cache invalidation or bounded staleness.
9. Protection against hot keys.
10. Observability for cache hit rate, DB fallback, p99 latency, and errors.
```

Input:

```http
GET /abc123
```

Output:

```http
302 Found
Location: https://example.com/article
```

Non-goals for this chapter:

```text
1. Full Redis cluster implementation.
2. Kafka click analytics pipeline.
3. Full CDN provider setup.
4. Multi-region active-active.
5. Sharding implementation.
```

This chapter builds the system design mental model first.

Later chapters implement each part deeply.

---

## 4. Read Heavy vs Write Heavy

A system is read-heavy when reads dominate writes.

Example ratios:

```text
1 write : 10 reads
1 write : 1,000 reads
1 write : 1,000,000 reads
```

URL shortener is naturally read-heavy.

Create flow:

```text
User creates short link once.
```

Redirect flow:

```text
Many users click it repeatedly.
```

ASCII:

```text
Create:
    1 request
      |
      v
    1 row

Redirect:
    same row
      |
      +--> click 1
      +--> click 2
      +--> click 3
      +--> click 4
      +--> click N
```

Read-heavy design priorities:

```text
1. Cache hot data.
2. Index lookup keys.
3. Reduce database round trips.
4. Avoid expensive joins.
5. Separate read and write load.
6. Add replicas when database reads grow.
7. Track p95 and p99 latency.
8. Handle cache failures gracefully.
```

Write-heavy design priorities are different:

```text
1. durable writes
2. idempotency
3. transaction safety
4. queues
5. partitioning
6. write throughput
```

For MiniURLShortener redirect, read-heavy is the main scaling problem.

---

## 5. MiniURLShortener Traffic Shape

Typical URL shortener traffic:

```text
Create URL:
    lower volume
    authenticated or semi-controlled
    heavier validation
    DB write required

Redirect URL:
    very high volume
    anonymous/public
    latency-sensitive
    cacheable
    read optimized
```

Traffic table:

```text
+-----------------------+------------------+----------------------------+
| API                   | Traffic Shape    | Main Bottleneck            |
+-----------------------+------------------+----------------------------+
| POST /api/v1/urls     | lower write load | validation + DB insert     |
| GET /{shortCode}      | huge read load   | cache/DB lookup latency    |
+-----------------------+------------------+----------------------------+
```

Read amplification example:

```text
100,000 URLs created per day
100 redirects per URL average

Total redirects:
    100,000 * 100 = 10,000,000 redirects/day
```

Some links are much hotter:

```text
average link:
    100 clicks

viral link:
    5,000,000 clicks
```

This distribution is usually skewed.

ASCII:

```text
Traffic distribution:

Most links:
    small traffic
    ####

Few hot links:
    massive traffic
    ############################################################
```

This matters because:

```text
A few hot keys can dominate your system load.
```

---

## 6. Redirect Hot Path

The redirect hot path is the most important path in the system.

Request:

```http
GET /abc123
```

Minimal ideal flow:

```text
1. Extract shortCode.
2. Validate shortCode format.
3. Lookup shortCode -> redirect metadata.
4. Check status and expiry.
5. Return 302 Location.
```

Metadata needed:

```text
shortCode
longUrl
status
expiresAt
```

ASCII:

```text
GET /abc123
   |
   v
+----------------------+
| Validate code format |
+----------+-----------+
           |
           v
+----------------------+
| Lookup redirect data |
+----------+-----------+
           |
           v
+----------------------+
| status / expiry check|
+----------+-----------+
           |
           v
+----------------------+
| 302 Location         |
+----------------------+
```

What should not happen on hot path:

```text
heavy joins
click analytics writes synchronously
complex user permission lookup
slow external API call
large object loading
blocking queue publish with long timeout
unnecessary transactions
```

Golden rule:

```text
Redirect path should do only what is required to redirect safely.
```

Click analytics should be asynchronous later.

---

## 7. Baseline Simple Design

Initial design:

```text
Client -> Spring Boot -> PostgreSQL
```

ASCII:

```text
+--------+       +-------------+       +------------+
| Client | ----> | Spring Boot | ----> | PostgreSQL |
+--------+       +-------------+       +------------+
                      |
                      v
               SELECT long_url
               FROM short_urls
               WHERE short_code = ?
```

SQL:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = 'abc123';
```

This is fine for small scale.

Example:

```text
10 RPS
100 RPS
maybe 1,000 RPS if DB and indexes are good
```

Benefits:

```text
simple
strong consistency
easy debugging
no cache invalidation
```

Problems at high read scale:

```text
database gets too many repeated reads
connection pool saturates
p99 latency grows
hot links overload DB
read traffic competes with writes
```

The baseline is correct but not enough.

---

## 8. Why Baseline Breaks

Imagine:

```text
10,000 redirect RPS
```

If every request hits PostgreSQL:

```text
10,000 SELECTs per second
```

Even with index:

```text
DB CPU increases
buffer cache pressure increases
connection pool waits increase
network round trips increase
p99 latency increases
```

ASCII bottleneck:

```text
Clients
  | | | | | | | | | |
  v v v v v v v v v v
+----------------------+
| Spring Boot Pods     |
+----------+-----------+
           |
           | 10,000 SELECT/s
           v
+----------------------+
| PostgreSQL Primary   |
| CPU/IO/Connections   |
+----------------------+
```

Connection pool problem:

```text
Spring Boot has Hikari pool size = 30
10 pods = 300 DB connections
traffic spike causes waiting for connection
```

Symptoms:

```text
Hikari connection timeout
slow queries
high DB CPU
p99 > 500ms
timeouts
5xx errors
```

Important:

```text
Indexing makes each lookup faster.
Caching reduces number of lookups.
```

Both are needed, but they solve different problems.

---

## 9. Read Heavy Target Architecture

Target read-heavy architecture:

```text
Client
  |
  v
CDN / Edge cache where possible
  |
  v
Load Balancer
  |
  v
Spring Boot Redirect Service
  |
  v
Redis Cache
  |
  +-- hit  -> return redirect
  |
  +-- miss -> PostgreSQL read
               |
               v
          refill Redis
               |
               v
          return redirect
```

ASCII:

```text
+---------+
| Client  |
+----+----+
     |
     v
+-------------------+
| CDN / Edge Layer  |
| optional redirect |
+----+--------------+
     |
     v
+-------------------+
| Load Balancer     |
+----+--------------+
     |
     v
+-------------------+
| Spring Boot Pods  |
+----+--------------+
     |
     v
+-------------------+        miss        +----------------------+
| Redis Cache       | -----------------> | PostgreSQL           |
| shortCode->data   |                    | source of truth      |
+----+--------------+                    +----------------------+
     |
     | hit
     v
302 Redirect
```

Read path hierarchy:

```text
Fastest:
    CDN/browser cache

Fast:
    Redis cache

Slower:
    PostgreSQL index lookup

Slowest:
    PostgreSQL overloaded / no index / joins
```

Principle:

```text
Every cache layer should reduce load on the layer below.
```

---

## 10. Cache-Aside Mental Model

Cache-aside means application controls cache population.

Flow:

```text
1. Check cache.
2. If cache hit, use data.
3. If cache miss, query DB.
4. Store DB result in cache.
5. Return result.
```

ASCII:

```text
GET /abc123
   |
   v
Redis GET url:abc123
   |
   +-- found ----------------------+
   |                               |
   v                               |
return redirect                    |
                                   |
   +-- not found                   |
          |                        |
          v                        |
     PostgreSQL SELECT             |
          |                        |
          v                        |
     Redis SET url:abc123          |
          |                        |
          v                        |
     return redirect <-------------+
```

Why cache-aside fits URL shortener:

```text
shortCode lookup is key-value shaped
data is small
hot reads repeat often
cache miss can fallback to DB
source of truth remains DB
```

Cache key:

```text
url:redirect:{shortCode}
```

Cache value:

```json
{
  "longUrl": "https://example.com/article",
  "status": "ACTIVE",
  "expiresAt": "2026-12-31T00:00:00Z"
}
```

The cache should contain enough data to decide redirect without DB.

---

## 11. Redis Cache For Redirects

Redis is useful because it is:

```text
in-memory
fast
network accessible
supports TTL
good for key-value lookup
```

Data model:

```text
Key:
    redirect:abc123

Value:
    longUrl + status + expiresAt

TTL:
    based on configured redirect cache duration
```

ASCII:

```text
Redis

+--------------------+---------------------------------------------+
| Key                | Value                                       |
+--------------------+---------------------------------------------+
| redirect:abc123    | longUrl=https://example.com, ACTIVE, null   |
| redirect:sale2026  | longUrl=https://shop.com/sale, ACTIVE, date |
| redirect:old1      | EXPIRED marker or absent                    |
+--------------------+---------------------------------------------+
```

Cache hit:

```text
1 network round trip to Redis
no DB query
low latency
```

Cache miss:

```text
1 Redis miss
1 DB query
1 Redis set
```

Important:

```text
Cache miss is slower than direct DB once, but future hits are faster.
```

At high hit rate, average latency improves.

Example:

```text
Cache hit rate = 95%
Only 5% of reads hit DB
```

DB load reduction:

```text
10,000 RPS total
95% cache hit
DB reads = 500 RPS
```

That is the core power.

---

## 12. Database Indexing For Reads

Even with Redis, DB fallback must be fast.

Critical index:

```sql
CREATE UNIQUE INDEX uk_short_urls_short_code
ON short_urls(short_code);
```

Lookup query:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?;
```

With index:

```text
PostgreSQL can find row quickly by short_code.
```

Without index:

```text
PostgreSQL may scan many rows.
```

ASCII:

```text
No index:

short_urls table
[ row1 ][ row2 ][ row3 ]...[ row999999 ]
   scan scan scan scan scan


With index:

short_code index
abc123 -> pointer to row
```

Covering index idea:

```sql
CREATE INDEX idx_short_urls_redirect_lookup
ON short_urls(short_code)
INCLUDE (long_url, status, expires_at);
```

This can help PostgreSQL answer from index pages in some cases.

But first rule:

```text
Have a unique index on short_code.
```

Index tradeoff:

```text
Reads become faster.
Writes become slightly more expensive.
Storage increases.
```

For URL shortener, this tradeoff is worth it.

---

## 13. Read Replicas

Read replicas are copies of the primary database used for read traffic.

Architecture:

```text
Writes -> primary
Reads  -> replicas
```

ASCII:

```text
             Write Path
Client --------> Spring Boot --------> PostgreSQL Primary
                                           |
                                           | replication
                                           v
             Read Path                 PostgreSQL Replica
Client --------> Spring Boot --------> read queries
```

When to use read replicas:

```text
Redis hit rate is not enough
DB read load still high
analytics/admin read queries grow
primary CPU overloaded by reads
```

For redirect path:

```text
Cache hit -> Redis
Cache miss -> read replica or primary
```

Tradeoff:

```text
Replica lag
```

Example:

```text
User creates short URL.
Immediately clicks it.
Replica may not have row yet.
```

Options:

```text
1. Read misses from primary for newly created links.
2. Write-through cache on create.
3. Use primary fallback if replica misses.
4. Accept tiny delay depending on product requirement.
```

For MiniURLShortener:

```text
After create, put redirect mapping into Redis.
Then immediate redirect can hit cache even if replica lags.
```

---

## 14. CDN And Browser Cache

Redirects can sometimes be cached by browsers or CDN.

HTTP redirect status codes:

```text
302 Found:
    temporary redirect

301 Moved Permanently:
    permanent redirect

307 Temporary Redirect:
    temporary, preserves method

308 Permanent Redirect:
    permanent, preserves method
```

For URL shortener, use 302 by default.

Why?

```text
Destination may be edited, expired, blocked, or deleted later.
Permanent caching can make changes hard to apply.
```

Browser/CDN caching:

```http
Cache-Control: public, max-age=60
```

This means:

```text
Cache redirect for 60 seconds.
```

ASCII:

```text
Client
  |
  v
Browser/CDN cache
  |
  +-- cached redirect -> no app request
  |
  +-- miss -----------> app/Redis/DB
```

Benefits:

```text
reduces app traffic
reduces Redis traffic
improves latency globally
```

Risks:

```text
blocked/deleted link may keep redirecting until cache expires
incorrect long TTL causes stale redirects
```

Safe strategy:

```text
Use short TTL at CDN/browser for public redirect.
Use Redis TTL internally.
Use 302 for mutable short links.
```

For high-risk links:

```text
Cache-Control: no-store
```

For known immutable links:

```text
longer TTL possible
```

---

## 15. Negative Caching

Negative caching means caching failures.

Example:

```text
shortCode not found
```

Without negative cache:

```text
Bot requests /bad1 repeatedly.
Every request hits DB.
```

With negative cache:

```text
Cache "NOT_FOUND" for short TTL.
Repeated bad requests avoid DB.
```

ASCII:

```text
GET /unknown
   |
   v
Redis miss
   |
   v
DB miss
   |
   v
Redis SET negative:unknown = NOT_FOUND TTL 30s
   |
   v
404

Next request /unknown
   |
   v
Redis says NOT_FOUND
   |
   v
404 without DB
```

Benefits:

```text
protects DB from random/bot invalid codes
reduces repeated misses
```

Risk:

```text
User creates that alias during negative TTL.
Cache still says NOT_FOUND.
```

Solutions:

```text
short TTL for negative cache
delete negative cache on create
avoid negative caching for custom alias paths if needed
```

Recommended:

```text
negative cache 15-60 seconds
positive cache longer
```

---

## 16. Cache TTL Strategy

TTL means time-to-live.

Positive cache TTL:

```text
How long active redirect mapping stays in Redis.
```

Negative cache TTL:

```text
How long missing/invalid result stays in Redis.
```

Expiry-aware TTL:

```text
If URL expires in 5 minutes, do not cache it for 1 hour.
```

TTL formula:

```text
cacheTtl = min(defaultRedirectTtl, timeUntilExpiresAt)
```

Example:

```text
default TTL = 1 hour
URL expires in 10 minutes

cache TTL = 10 minutes
```

ASCII:

```text
URL expires at 10:30

Current time 10:20
default cache TTL 60 min

Wrong:
    cache until 11:20

Correct:
    cache until 10:30
```

For no expiry:

```text
use default TTL
```

For blocked/deleted:

```text
evict cache immediately or cache blocked marker with short TTL
```

TTL table:

```text
+--------------------+------------------------------+
| Result Type         | Suggested TTL                |
+--------------------+------------------------------+
| ACTIVE no expiry    | 5 min - 1 hour initially     |
| ACTIVE with expiry  | min(default, time to expiry) |
| NOT_FOUND           | 15 - 60 seconds              |
| BLOCKED             | short TTL or no-store        |
| EXPIRED             | short TTL or DB final state  |
+--------------------+------------------------------+
```

Start simple:

```text
positive TTL = 10 minutes
negative TTL = 30 seconds
```

Then tune using metrics.

---

## 17. Hot Key Problem

A hot key is one cache key receiving huge traffic.

Example:

```text
redirect:worldcup
```

Traffic:

```text
100,000 RPS to same shortCode
```

Even Redis can become stressed if one key is extremely hot.

ASCII:

```text
All traffic
   |
   v
same Redis key
redirect:worldcup
   |
   v
one Redis shard/node overloaded
```

Symptoms:

```text
Redis CPU high
Redis network high
p99 Redis GET latency increases
one shard hotter than others
application p99 increases
```

Mitigations:

```text
1. CDN/browser cache for hot redirects.
2. Local in-memory cache with tiny TTL.
3. Redis cluster with careful key distribution.
4. Hot key replication strategy.
5. Rate limiting abusive traffic.
6. Pre-warming cache for known campaigns.
```

Local cache idea:

```text
Spring Boot pod keeps very hot mapping for 1-5 seconds.
```

ASCII:

```text
Pod local cache
   |
   +-- hit -> redirect
   |
   +-- miss -> Redis
```

Risk:

```text
stale data for a few seconds
```

For redirect, tiny staleness may be acceptable unless link is blocked for abuse.

For blocked links, evict/update aggressively.

---

## 18. Consistency Tradeoffs

Read-heavy systems often trade immediate consistency for speed.

Source of truth:

```text
PostgreSQL
```

Fast read cache:

```text
Redis
CDN
browser
local memory
```

Problem:

```text
Database changes.
Cache still has old value.
```

Example:

```text
Admin blocks shortCode abc123.
Redis still contains ACTIVE mapping.
User gets redirected for TTL duration.
```

Solutions:

```text
1. Short TTL.
2. Cache eviction on update/block/delete.
3. Write-through cache update.
4. Event-based invalidation later.
5. No-cache for suspicious links.
```

ASCII:

```text
DB updated to BLOCKED
      |
      v
Need to remove/update cache
      |
      +-- if done    -> next read blocked
      |
      +-- if missed  -> stale redirect until TTL
```

Consistency choices:

```text
Strong consistency:
    Always check DB.
    Slower, more DB load.

Eventual consistency:
    Use cache TTL/invalidation.
    Faster, possible short stale window.
```

For URL shortener:

```text
Most links can tolerate small stale window.
Abuse/security block should minimize stale window.
```

Senior answer:

```text
I use bounded staleness with TTL and explicit invalidation for critical state changes.
```

---

## 19. Write Path vs Read Path Separation

Write path:

```text
POST /api/v1/urls
```

Responsibilities:

```text
validate input
generate code
insert DB
maybe write-through cache
return created response
```

Read path:

```text
GET /{shortCode}
```

Responsibilities:

```text
lookup mapping
check status/expiry
return redirect
```

ASCII:

```text
                  WRITE PATH

Client -> Controller -> Service -> DB Primary
                              |
                              v
                         Redis SET optional


                  READ PATH

Client -> Controller -> Redis GET
                         |
                         +-- hit -> redirect
                         |
                         +-- miss -> DB read -> Redis SET -> redirect
```

Why separate thinking matters:

```text
Write path optimizes correctness.
Read path optimizes speed.
```

Do not add write-heavy work to redirect path.

Bad redirect path:

```text
DB lookup
insert click event
update click count
call analytics API
publish Kafka synchronously
return redirect
```

Better redirect path:

```text
lookup mapping
return redirect
fire-and-forget analytics later
```

Click count should not block redirect.

---

## 20. Step-by-Step Dry Runs

### Dry Run 1: Cache Hit Redirect

Request:

```http
GET /abc123
```

Redis:

```text
redirect:abc123 -> ACTIVE, https://example.com, no expiry
```

Flow:

```text
1. Request reaches redirect controller.
2. shortCode format is valid.
3. Service checks Redis.
4. Redis returns redirect metadata.
5. Service checks status ACTIVE.
6. Service checks expiresAt null.
7. Controller returns 302 Location.
8. PostgreSQL is not touched.
```

ASCII:

```text
GET /abc123
   |
   v
Redis HIT
   |
   v
302 Location
```

Result:

```text
fast path
low DB load
```

---

### Dry Run 2: Cache Miss Then DB Hit

Redis:

```text
no key redirect:abc123
```

DB:

```text
abc123 -> https://example.com, ACTIVE
```

Flow:

```text
1. Redis miss.
2. Service queries PostgreSQL by shortCode.
3. DB returns row.
4. Service verifies ACTIVE and not expired.
5. Service stores metadata in Redis with TTL.
6. Controller returns 302.
```

ASCII:

```text
GET /abc123
   |
   v
Redis MISS
   |
   v
PostgreSQL SELECT
   |
   v
Redis SET
   |
   v
302 Location
```

Future request:

```text
Redis HIT
```

---

### Dry Run 3: Unknown Code With Negative Cache

Request:

```http
GET /unknown99
```

Redis:

```text
miss
```

DB:

```text
no row
```

Flow:

```text
1. Redis miss.
2. DB lookup returns empty.
3. Service caches NOT_FOUND marker for 30 seconds.
4. Service throws ShortCodeNotFoundException.
5. Handler returns 404.
```

Next request:

```text
1. Redis returns NOT_FOUND marker.
2. Service returns 404 without DB.
```

---

### Dry Run 4: Expiring URL

Current time:

```text
10:00
```

URL expires:

```text
10:05
```

Default TTL:

```text
60 minutes
```

Correct TTL:

```text
5 minutes
```

Flow:

```text
1. DB returns active row with expiresAt 10:05.
2. Service calculates timeUntilExpiry = 5 minutes.
3. Redis SET TTL = min(60 min, 5 min).
4. Cache expires when link expires.
```

Avoids:

```text
redirecting expired link from stale cache
```

---

### Dry Run 5: Blocked Link

DB:

```text
shortCode = bad1
status = BLOCKED
```

Flow:

```text
1. Redis miss or blocked marker.
2. DB returns row.
3. Service sees BLOCKED.
4. Service does not return longUrl.
5. Handler returns 403 SHORT_CODE_BLOCKED.
```

Security rule:

```text
Do not leak blocked longUrl unnecessarily.
```

---

## 21. Internal Execution Walkthrough

Spring Boot redirect flow with Redis:

```text
1. HTTP request enters embedded Tomcat.
2. DispatcherServlet routes GET /{shortCode}.
3. Controller calls RedirectService.
4. RedirectService validates shortCode.
5. RedirectService checks Redis using cache key.
6. If cache value exists, deserialize RedirectCacheValue.
7. If cache value missing, repository queries PostgreSQL.
8. Repository returns entity or empty.
9. Service maps entity to redirect metadata.
10. Service stores metadata in Redis with TTL.
11. Service returns longUrl.
12. Controller returns ResponseEntity.status(302).location(longUrl).
```

ASCII:

```text
+--------+      +------------+      +----------+      +-------+      +------+
| Client | ---> | Controller | ---> | Service  | ---> | Redis | ---> | DB   |
+--------+      +------------+      +----------+      +-------+      +------+
                                      |
                                      v
                              status/expiry check
                                      |
                                      v
                               302 or error
```

Important:

```text
The service owns the redirect decision.
The cache only stores data.
The database remains source of truth.
```

Do not let controller contain cache/DB decision logic.

---

## 22. Capacity Thinking

Assume:

```text
Redirect traffic = 20,000 RPS
Cache hit rate = 95%
```

DB read load:

```text
20,000 * 5% = 1,000 DB reads/sec
```

If hit rate improves to 99%:

```text
20,000 * 1% = 200 DB reads/sec
```

ASCII:

```text
20,000 RPS total
 |
 +-- 19,800 RPS Redis hit if 99%
 |
 +--    200 RPS DB fallback
```

Cache hit rate is a key metric.

Capacity signals:

```text
cache hit rate
Redis CPU
Redis memory
Redis p99 latency
DB read QPS
DB CPU
Hikari active connections
application p99 latency
5xx rate
```

Memory estimate:

```text
1 cache entry approx:
    key 20-50 bytes
    value 200-500 bytes
    Redis overhead

1 million entries maybe:
    hundreds of MB depending on encoding/overhead
```

Start practical:

```text
cache only active redirect metadata
use TTL
monitor memory
evict least recently used if needed
```

---

## 23. p99 Latency Thinking

Average latency hides tail problems.

Example:

```text
p50 = 8ms
p95 = 40ms
p99 = 600ms
```

Users feel p99 during spikes.

Read-heavy redirect target:

```text
Cache hit:
    very low latency

Cache miss:
    acceptable latency

DB overloaded:
    high p99
```

Latency stack:

```text
Client network
Load balancer
Spring Boot queue/wait
Redis network
DB network if miss
DB query
serialization
response
```

ASCII:

```text
p99 grows when one layer waits:

Request
  |
  +-- app thread wait
  +-- Redis wait
  +-- DB connection wait
  +-- DB query wait
  +-- GC pause
```

p99 improvement levers:

```text
increase cache hit rate
avoid DB on hot path
tune Hikari pool
index short_code
keep response small
avoid synchronous analytics
use timeouts
monitor Redis latency
```

Senior mindset:

```text
For read-heavy systems, p99 is usually killed by fallback paths and resource queues.
```

---

## 24. Production Failure Stories

### Failure Story 1: Viral Link Took Down DB

A marketing link went viral.

Every redirect hit PostgreSQL.

DB CPU reached 100%.

Response latency increased.

Then Hikari pool exhausted.

Root cause:

```text
No Redis cache for redirect hot path.
```

Fix:

```text
Add cache-aside redirect cache.
Pre-warm campaign links.
Monitor cache hit rate.
```

Lesson:

```text
A URL shortener must assume one short code can become extremely hot.
```

---

### Failure Story 2: Blocked Link Still Redirected

Admin blocked malicious link.

Redis still had ACTIVE mapping for 1 hour.

Users kept getting redirected.

Root cause:

```text
Cache TTL too long and no explicit invalidation on block.
```

Fix:

```text
Evict cache on block/delete.
Use shorter TTL for risky links.
Use blocked marker if needed.
```

Lesson:

```text
Caching improves speed but creates stale data risk.
```

---

### Failure Story 3: Bot Attack On Random Codes

Bots generated random short codes.

Every invalid code caused DB lookup.

DB load spiked even though links did not exist.

Root cause:

```text
No negative caching and no rate limiting.
```

Fix:

```text
Cache NOT_FOUND for short TTL.
Add rate limiting later.
Add abuse detection.
```

Lesson:

```text
Cache misses can be more dangerous than cache hits.
```

---

### Failure Story 4: Replica Lag Broke New Links

User created link and clicked immediately.

Read path used replica.

Replica had not received new row yet.

User got 404.

Root cause:

```text
Read-after-write consistency issue.
```

Fix:

```text
Write-through Redis on create.
Fallback to primary on replica miss for fresh links.
```

Lesson:

```text
Read replicas introduce lag. Design around it.
```

---

### Failure Story 5: Analytics Made Redirect Slow

Redirect path synchronously inserted click event into DB.

During spike, click table writes slowed down.

Redirect p99 exploded.

Root cause:

```text
Analytics write was on critical redirect path.
```

Fix:

```text
Publish click event asynchronously.
Do not block redirect on analytics.
```

Lesson:

```text
Redirect must be protected from non-critical work.
```

---

## 25. Debugging Mindset

When redirect latency is high, ask:

```text
Is traffic hitting cache?
What is cache hit rate?
Is Redis slow or unavailable?
Are DB fallbacks increasing?
Is short_code indexed?
Are hot keys overloading Redis?
Is Hikari pool waiting?
Is DB CPU high?
Are redirects blocked by analytics writes?
Are p99 spikes correlated with cache miss spikes?
Are bots causing random misses?
```

Debug map:

```text
High DB CPU:
    cache miss rate high
    no index
    bot random codes
    Redis down

High Redis CPU:
    hot key
    too many cache lookups
    large values
    no CDN/browser cache

High app latency:
    DB pool wait
    Redis timeout
    thread pool saturation
    GC pause

Unexpected stale redirect:
    TTL too long
    invalidation missing
    CDN/browser cached permanent redirect

New link 404:
    replica lag
    cache not populated on create
```

Metrics to add:

```text
redirect.requests.total
redirect.cache.hit
redirect.cache.miss
redirect.db.lookup
redirect.not_found
redirect.blocked
redirect.expired
redirect.latency.p95
redirect.latency.p99
redis.command.latency
db.query.latency
hikari.connections.active
hikari.connections.pending
```

Golden debugging rule:

```text
For read-heavy systems, always separate cache-hit path from cache-miss path.
```

---

## 26. Common Mistakes

### Mistake 1: Every Redirect Hits DB

Wrong:

```text
GET /abc123 -> PostgreSQL every time
```

Correct:

```text
GET /abc123 -> Redis first -> DB only on miss
```

---

### Mistake 2: No Index On short_code

Wrong:

```text
SELECT without index on lookup key.
```

Correct:

```sql
CREATE UNIQUE INDEX uk_short_urls_short_code
ON short_urls(short_code);
```

---

### Mistake 3: Long Cache TTL Without Invalidation

Wrong:

```text
Cache active redirect for 24 hours.
Admin block does not evict.
```

Correct:

```text
Use bounded TTL.
Evict/update cache on block/delete.
```

---

### Mistake 4: Synchronous Analytics On Redirect Path

Wrong:

```text
Insert click analytics before redirect response.
```

Correct:

```text
Redirect first.
Send analytics asynchronously.
```

---

### Mistake 5: No Negative Cache

Wrong:

```text
Every invalid code hits DB.
```

Correct:

```text
Cache NOT_FOUND briefly.
```

---

### Mistake 6: Permanent Redirect For Mutable Links

Wrong:

```text
301 for all links.
```

Correct:

```text
302 by default unless truly immutable.
```

---

### Mistake 7: Ignoring p99

Wrong:

```text
Average latency is fine.
```

Correct:

```text
Track p95/p99 separately for cache hit and miss.
```

---

### Mistake 8: Read Replica Without Lag Strategy

Wrong:

```text
All reads from replica, no read-after-write handling.
```

Correct:

```text
Write-through cache on create or primary fallback.
```

---

## 27. Java/Spring Boot Implementation Sketch

This is a simplified sketch.

### Redirect Cache Value

```java
package com.miniurl.shortener.url.cache;

import java.time.Instant;

public class RedirectCacheValue {

    private String longUrl;
    private String status;
    private Instant expiresAt;

    public RedirectCacheValue() {
    }

    public RedirectCacheValue(String longUrl, String status, Instant expiresAt) {
        this.longUrl = longUrl;
        this.status = status;
        this.expiresAt = expiresAt;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public String getStatus() {
        return status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
```

### Redirect Cache Service

```java
package com.miniurl.shortener.url.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedirectCacheService {

    private final RedisTemplate<String, RedirectCacheValue> redisTemplate;

    public RedirectCacheService(RedisTemplate<String, RedirectCacheValue> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Optional<RedirectCacheValue> get(String shortCode) {
        String key = key(shortCode);
        RedirectCacheValue value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value);
    }

    public void put(String shortCode, RedirectCacheValue value, Duration ttl) {
        redisTemplate.opsForValue().set(key(shortCode), value, ttl);
    }

    public void evict(String shortCode) {
        redisTemplate.delete(key(shortCode));
    }

    private String key(String shortCode) {
        return "redirect:" + shortCode;
    }
}
```

### Redirect Service

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.url.cache.RedirectCacheService;
import com.miniurl.shortener.url.cache.RedirectCacheValue;
import com.miniurl.shortener.url.entity.ShortUrl;
import com.miniurl.shortener.url.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class RedirectService {

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);

    private final RedirectCacheService cacheService;
    private final ShortUrlRepository repository;

    public RedirectService(
            RedirectCacheService cacheService,
            ShortUrlRepository repository
    ) {
        this.cacheService = cacheService;
        this.repository = repository;
    }

    public String resolve(String shortCode) {
        return cacheService.get(shortCode)
                .map(value -> resolveFromCache(shortCode, value))
                .orElseGet(() -> resolveFromDatabase(shortCode));
    }

    private String resolveFromCache(String shortCode, RedirectCacheValue value) {
        validateStatusAndExpiry(shortCode, value.getStatus(), value.getExpiresAt());
        return value.getLongUrl();
    }

    private String resolveFromDatabase(String shortCode) {
        ShortUrl entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));

        validateStatusAndExpiry(shortCode, entity.getStatus().name(), entity.getExpiresAt());

        RedirectCacheValue value = new RedirectCacheValue(
                entity.getLongUrl(),
                entity.getStatus().name(),
                entity.getExpiresAt()
        );

        cacheService.put(shortCode, value, calculateTtl(entity.getExpiresAt()));

        return entity.getLongUrl();
    }

    private void validateStatusAndExpiry(String shortCode, String status, Instant expiresAt) {
        if ("BLOCKED".equals(status)) {
            throw new ShortCodeBlockedException(shortCode);
        }

        if ("DELETED".equals(status)) {
            throw new ShortCodeNotFoundException(shortCode);
        }

        if (expiresAt != null && expiresAt.isBefore(Instant.now())) {
            throw new ShortCodeExpiredException(shortCode);
        }
    }

    private Duration calculateTtl(Instant expiresAt) {
        if (expiresAt == null) {
            return DEFAULT_TTL;
        }

        Duration untilExpiry = Duration.between(Instant.now(), expiresAt);

        if (untilExpiry.isNegative() || untilExpiry.isZero()) {
            return Duration.ZERO;
        }

        return untilExpiry.compareTo(DEFAULT_TTL) < 0 ? untilExpiry : DEFAULT_TTL;
    }
}
```

### Controller

```java
package com.miniurl.shortener.url.controller;

import com.miniurl.shortener.url.service.RedirectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class RedirectController {

    private final RedirectService redirectService;

    public RedirectController(RedirectService redirectService) {
        this.redirectService = redirectService;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String longUrl = redirectService.resolve(shortCode);

        return ResponseEntity
                .status(302)
                .location(URI.create(longUrl))
                .build();
    }
}
```

Important:

```text
This is only the design sketch.
Redis serialization, config, metrics, and negative cache are separate implementation details.
```

---

## 28. Interview-Ready Explanation

If interviewer asks:

```text
How would you scale the redirect API of a URL shortener?
```

Strong answer:

```text
A URL shortener is read-heavy because each short link is created once but may be
redirected many times. I would keep the redirect path extremely small: validate
shortCode, lookup redirect metadata, check status and expiry, then return 302.
The database remains the source of truth, but I would put Redis in front using
cache-aside. On cache hit, the service redirects without touching PostgreSQL. On
cache miss, it queries PostgreSQL by an indexed short_code, fills Redis with an
expiry-aware TTL, and returns the redirect. I would also use short negative caching
for missing codes to protect the DB from bot traffic. For further scale, I would add
read replicas for fallback reads and CDN/browser caching with careful TTLs. I would
track cache hit rate, DB fallback QPS, Redis latency, Hikari pool waits, and p99
redirect latency. For blocked or deleted links, I would evict or update cache to
reduce stale redirects.
```

Senior version:

```text
The key is to separate source of truth from read acceleration. PostgreSQL owns correctness,
Redis accelerates hot reads, CDN/browser cache absorbs edge traffic, and async analytics
keeps non-critical writes off the redirect path. The tradeoff is bounded staleness, managed
with TTLs and explicit invalidation for critical state changes.
```

Why this is strong:

```text
1. Recognizes read-heavy traffic shape.
2. Keeps redirect hot path small.
3. Uses Redis cache-aside correctly.
4. Mentions DB index.
5. Mentions negative caching.
6. Mentions stale cache risk.
7. Mentions read replica lag.
8. Mentions p99 and metrics.
9. Keeps analytics off critical path.
```

---

## 29. Senior Engineer Checklist

Before calling the read-heavy design production-shaped, confirm:

```text
[ ] Redirect path is minimal
[ ] short_code has unique index
[ ] Redis cache-aside is planned
[ ] Cache value includes longUrl, status, expiresAt
[ ] Positive TTL exists
[ ] TTL respects expiresAt
[ ] Negative cache exists for not found
[ ] Cache eviction exists for block/delete/update
[ ] 302 is default redirect status
[ ] 301 is avoided for mutable links
[ ] Analytics is async, not blocking redirect
[ ] Cache hit rate metric exists
[ ] DB fallback metric exists
[ ] Redis latency metric exists
[ ] Redirect p95/p99 metrics exist
[ ] Hikari pending connection metric exists
[ ] Hot key mitigation plan exists
[ ] Read replica lag strategy exists if replicas are used
[ ] Bot/random code protection is considered
```

---

## 30. One-Page Cheat Sheet

```text
Core mental model:
Read path must be shorter than write path.

URL shortener:
Create once.
Redirect many times.

Baseline:
Client -> Spring Boot -> PostgreSQL

Read-heavy target:
Client -> CDN/browser cache -> Spring Boot -> Redis -> PostgreSQL

Cache-aside:
1. Redis GET
2. Hit -> redirect
3. Miss -> DB SELECT
4. Redis SET
5. Redirect

DB:
short_code must be indexed and unique.

TTL:
positive cache: minutes
negative cache: seconds
expiry-aware: min(default TTL, time until expiresAt)

Errors:
not found -> 404
blocked -> 403
expired -> 410
active -> 302

Hot path must avoid:
joins
sync analytics writes
external API calls
unnecessary transactions
large payloads

Metrics:
cache hit rate
cache miss rate
DB fallback QPS
Redis p99
redirect p99
Hikari pending
5xx rate

Main tradeoff:
Speed vs freshness.
Use bounded staleness + invalidation.
```

---

## 31. One Picture To Remember

```text
                 READ HEAVY URL SHORTENER DESIGN

                         "Protect the database"

                             Client
                               |
                               v
                    +----------------------+
                    | Browser / CDN Cache  |
                    | short TTL redirect   |
                    +----------+-----------+
                               |
                         miss  |
                               v
                    +----------------------+
                    | Load Balancer        |
                    +----------+-----------+
                               |
                               v
                    +----------------------+
                    | Spring Boot Redirect |
                    | tiny hot path        |
                    +----------+-----------+
                               |
                               v
                    +----------------------+
                    | Redis Cache          |
                    | shortCode -> data    |
                    +----+-------------+---+
                         |             |
                    hit  |             | miss
                         v             v
                    302 Redirect   +----------------------+
                                   | PostgreSQL           |
                                   | source of truth      |
                                   | indexed short_code   |
                                   +----------+-----------+
                                              |
                                              v
                                      refill Redis
                                              |
                                              v
                                      302 Redirect


FINAL MEMORY:

PostgreSQL owns truth.
Redis protects PostgreSQL.
CDN protects Redis.
The redirect path must stay tiny.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. A URL shortener is read-heavy because one created link can be redirected many times.
2. The redirect hot path should be tiny: lookup, validate status/expiry, return 302.
3. Redis cache-aside reduces repeated database reads and protects PostgreSQL from hot links.
4. TTL, negative caching, and invalidation are required to control stale data and bot traffic.
5. Production readiness is measured by cache hit rate, DB fallback rate, Redis latency, Hikari waits, and redirect p99 latency.
```

Next chapters can deepen this architecture:

```text
021_Redis_Cache_For_Redirect.md
022_Cache_Aside_Pattern.md
023_DB_Indexing_For_URL_Lookup.md
024_Connection_Pooling_Hikari.md
025_Rate_Limiting_For_Create_API.md
026_Circuit_Breaker_For_Dependencies.md
027_Async_Click_Logging.md
028_CDN_And_Browser_Caching.md
029_Observability_For_URLShortener.md
030_Production_Readiness_Checklist.md
```
