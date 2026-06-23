# 022_Cache_Aside_Pattern.md
# MiniURLShortener — Cache-Aside Pattern

> Core mental model: **Cache-aside means the application owns the cache decision. The app first asks Redis, falls back to PostgreSQL on miss, then fills Redis for the next request. PostgreSQL remains the source of truth; Redis is a fast copy built lazily by real traffic.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What Cache-Aside Means](#4-what-cache-aside-means)
- [5. Cache-Aside vs Other Cache Patterns](#5-cache-aside-vs-other-cache-patterns)
- [6. Why Cache-Aside Fits URL Redirect](#6-why-cache-aside-fits-url-redirect)
- [7. Read Flow Mental Model](#7-read-flow-mental-model)
- [8. Write Flow Mental Model](#8-write-flow-mental-model)
- [9. Cache Hit Path](#9-cache-hit-path)
- [10. Cache Miss Path](#10-cache-miss-path)
- [11. Cache Fill Path](#11-cache-fill-path)
- [12. Cache Invalidation Path](#12-cache-invalidation-path)
- [13. TTL And Bounded Staleness](#13-ttl-and-bounded-staleness)
- [14. Negative Cache-Aside](#14-negative-cache-aside)
- [15. Thundering Herd Problem](#15-thundering-herd-problem)
- [16. Cache Penetration, Breakdown, Avalanche](#16-cache-penetration-breakdown-avalanche)
- [17. Redis Failure Strategy](#17-redis-failure-strategy)
- [18. Consistency Tradeoffs](#18-consistency-tradeoffs)
- [19. Java/Spring Boot Implementation](#19-javaspring-boot-implementation)
- [20. Redirect Service Code](#20-redirect-service-code)
- [21. Step-by-Step Dry Runs](#21-step-by-step-dry-runs)
- [22. Internal Execution Walkthrough](#22-internal-execution-walkthrough)
- [23. Testing Strategy](#23-testing-strategy)
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

MiniURLShortener has a redirect API:

```http
GET /{shortCode}
```

The redirect API is read-heavy.

A single short URL may be created once but clicked thousands or millions of times.

If every click hits PostgreSQL:

```text
GET /abc123 -> DB
GET /abc123 -> DB
GET /abc123 -> DB
GET /abc123 -> DB
```

The database gets repeated reads for the same data.

Redis helps, but Redis must be used with a pattern.

The most common pattern for this use case is:

```text
Cache-Aside
```

Cache-aside answers:

```text
Who checks Redis?
Who queries DB on miss?
Who fills Redis?
Who decides TTL?
Who handles Redis failure?
```

Answer:

```text
The application does.
```

ASCII:

```text
Client
  |
  v
Spring Boot Application
  |
  +-- first asks Redis
  |
  +-- if miss, asks PostgreSQL
  |
  +-- then fills Redis
  |
  v
Redirect response
```

Production memory:

```text
Cache-aside is lazy caching controlled by the application.
```

---

## 2. The One Core Mental Model

The core mental model:

```text
APP READS CACHE BESIDE THE DATABASE
```

The cache sits beside the source of truth.

The application chooses which one to read.

ASCII:

```text
                 +------------------+
                 | Redis Cache      |
                 | fast copy        |
                 +---------+--------+
                           ^
                           |
                           | fill on miss
                           |
Client -> App -------------+
          |
          | fallback on miss
          v
                 +------------------+
                 | PostgreSQL       |
                 | source of truth  |
                 +------------------+
```

One-line memory:

```text
Cache-aside means: check cache, miss DB, refill cache.
```

Important:

```text
Redis does not automatically know about PostgreSQL.
PostgreSQL does not automatically update Redis.
The application coordinates both.
```

This gives control, but also responsibility.

The app must handle:

```text
misses
TTL
stale data
invalidation
Redis failure
duplicate cache fills
metrics
```

---

## 3. Problem Statement

Implement and understand cache-aside for MiniURLShortener redirect.

The design must support:

```text
1. Redis hit returns redirect without DB.
2. Redis miss queries PostgreSQL.
3. DB result fills Redis with safe TTL.
4. Missing DB row can be negative cached briefly.
5. Expired URLs do not remain cached beyond expiry.
6. Block/delete/update evicts cache.
7. Redis failure falls back to DB.
8. Metrics distinguish hit, miss, fill, error, fallback.
9. Hot key and thundering herd are considered.
10. Code remains clean and testable.
```

Main data:

```text
shortCode -> longUrl, status, expiresAt
```

Success:

```text
ACTIVE + not expired -> 302
```

Errors:

```text
missing/deleted -> 404
blocked -> 403
expired -> 410
```

Non-goals:

```text
Redis cluster internals
full distributed locking
CDN caching
rate limiting
Kafka analytics
```

---

## 4. What Cache-Aside Means

Cache-aside is also called:

```text
lazy loading cache
```

Because cache is filled only when data is requested.

Flow:

```text
1. App receives read request.
2. App checks cache.
3. If cache hit, return cached data.
4. If cache miss, query database.
5. App stores database result in cache.
6. App returns data.
```

ASCII:

```text
READ request
   |
   v
Check cache
   |
   +-- HIT --> return data
   |
   +-- MISS
         |
         v
      Query DB
         |
         v
      Store in cache
         |
         v
      Return data
```

In MiniURLShortener:

```text
GET /abc123
   |
   v
Redis GET redirect:v1:abc123
   |
   +-- hit -> 302
   |
   +-- miss -> PostgreSQL SELECT -> Redis SET -> 302
```

The cache is not preloaded with everything.

It grows based on traffic.

This is ideal for skewed traffic:

```text
hot links get cached
cold links may never consume Redis memory
```

---

## 5. Cache-Aside vs Other Cache Patterns

Common cache patterns:

```text
Cache-aside
Read-through
Write-through
Write-behind
Refresh-ahead
```

Comparison:

```text
+----------------+-----------------------------+----------------------------+
| Pattern        | Who manages cache?           | Use case                   |
+----------------+-----------------------------+----------------------------+
| Cache-aside    | application                  | common app-level caching   |
| Read-through   | cache/load library           | cache knows loader         |
| Write-through  | write cache and DB together  | strong read freshness      |
| Write-behind   | write cache, DB later        | high write throughput      |
| Refresh-ahead  | cache refresh before expiry  | predictable hot data       |
+----------------+-----------------------------+----------------------------+
```

Cache-aside:

```text
App explicitly talks to Redis and DB.
```

Read-through:

```text
App asks cache; cache internally loads DB.
```

Write-through:

```text
Writes update DB and cache together.
```

For MiniURLShortener, cache-aside is best first because:

```text
simple
explicit
easy to reason
easy to test
works well with Redis
PostgreSQL remains truth
cache can be rebuilt lazily
```

Senior note:

```text
Cache-aside is not always strongest consistency, but it is practical and widely used for read-heavy systems.
```

---

## 6. Why Cache-Aside Fits URL Redirect

URL redirect lookup is:

```text
shortCode -> redirect metadata
```

This is a small key-value read.

Cache-aside fits because:

```text
1. Data is read repeatedly.
2. Not every short URL is hot.
3. Cache can be filled lazily.
4. DB can reconstruct cache after Redis restart.
5. TTL limits stale data.
6. App can apply domain rules after hit or miss.
```

ASCII:

```text
Traffic pattern:

Cold link:
    1 click
    maybe DB once, cache may expire

Hot link:
    1 DB lookup
    thousands of Redis hits
```

Why not preload all URLs?

```text
too much memory
many URLs may never be clicked
startup complexity
cache rebuild overhead
```

Cache-aside only caches what users actually request.

This is efficient for URL shorteners because real traffic is uneven.

---

## 7. Read Flow Mental Model

Cache-aside read flow for redirect:

```text
GET /{shortCode}
```

Steps:

```text
1. Validate shortCode format.
2. Build Redis key.
3. Read Redis.
4. If Redis hit, validate cached state.
5. If Redis miss, query DB.
6. If DB row missing, return 404 and maybe negative cache.
7. If DB row found, validate state.
8. Cache result with TTL.
9. Return redirect.
```

ASCII:

```text
GET /abc123
   |
   v
Validate shortCode
   |
   v
Redis GET
   |
   +-- HIT -> check status/expiry -> 302 or error
   |
   +-- MISS
          |
          v
       DB SELECT
          |
          +-- no row -> negative cache -> 404
          |
          +-- row -> check status/expiry -> cache fill -> 302 or error
```

Important:

```text
Cache hit does not mean blindly redirect.
```

The app still checks:

```text
status
expiresAt
```

Because cache is a copy and may be stale.

---

## 8. Write Flow Mental Model

Write flow changes source of truth.

Examples:

```text
create short URL
update destination
block short URL
delete short URL
change expiry
```

Cache-aside read is lazy.

But writes must think about cache.

Write strategies:

```text
Create:
    DB insert
    optionally Redis set

Update/block/delete:
    DB update
    Redis evict
```

ASCII:

```text
CREATE
  |
  v
PostgreSQL INSERT
  |
  v
optional Redis SET
  |
  v
201 Created


BLOCK / DELETE / UPDATE
  |
  v
PostgreSQL UPDATE
  |
  v
Redis DELETE
  |
  v
success
```

Why evict instead of update?

Eviction is simpler and safer.

After eviction:

```text
next read misses Redis
DB returns latest truth
Redis refills with latest data
```

Memory:

```text
On write, change truth first; then remove stale copy.
```

---

## 9. Cache Hit Path

Cache hit path should be very fast.

Flow:

```text
1. Redis returns value.
2. App deserializes value.
3. App checks state.
4. App returns response.
```

ASCII:

```text
+--------+      +-------------+      +-------+
| Client | ---> | Spring Boot | ---> | Redis |
+--------+      +-------------+      +---+---+
                                      |
                                      | HIT
                                      v
                                cached metadata
                                      |
                                      v
                                302 redirect
```

Cache hit benefits:

```text
no DB connection needed
no SQL query
lower latency
less DB CPU
better p99 under load
```

What cache hit must avoid:

```text
DB verification every time
synchronous analytics write
external safety API call
large logs
unnecessary transaction
```

If you verify DB every time:

```text
cache becomes useless
```

Use TTL and invalidation instead.

---

## 10. Cache Miss Path

Cache miss is the slow path.

Flow:

```text
1. Redis key absent.
2. App queries PostgreSQL.
3. DB returns row or empty.
4. App handles result.
```

ASCII:

```text
+--------+      +-------------+      +-------+
| Client | ---> | Spring Boot | ---> | Redis |
+--------+      +-------------+      +---+---+
                                      |
                                      | MISS
                                      v
                                +------------+
                                | PostgreSQL |
                                +------------+
```

Cache miss reasons:

```text
first request
TTL expired
manual eviction
Redis restart
memory eviction
key naming changed
cache not warmed
```

Miss is normal.

But too many misses are dangerous.

Symptoms:

```text
DB read QPS rises
Hikari pool waits increase
redirect p99 increases
cache hit rate drops
```

Measure:

```text
cache.miss.count
db.fallback.count
```

---

## 11. Cache Fill Path

Cache fill means storing DB result in Redis after miss.

Flow:

```text
DB row found
   |
   v
map row to cache value
   |
   v
calculate TTL
   |
   v
Redis SET key value TTL
```

ASCII:

```text
PostgreSQL row
    |
    v
RedirectCacheValue
    |
    v
TTL calculation
    |
    v
Redis SET redirect:v1:abc123 EX 600
```

Cache value:

```json
{
  "longUrl": "https://example.com/article",
  "status": "ACTIVE",
  "expiresAt": null
}
```

Important:

```text
Do not cache data blindly if URL is already expired.
```

For active link:

```text
cache with positive TTL
```

For missing link:

```text
optional negative cache with short TTL
```

For blocked/deleted:

```text
usually do not cache longUrl
optionally cache blocked marker briefly
```

---

## 12. Cache Invalidation Path

Invalidation removes stale cache.

When source of truth changes:

```text
DB update
Redis delete
```

ASCII:

```text
Before:

PostgreSQL:
    abc123 status=ACTIVE

Redis:
    abc123 status=ACTIVE


Admin blocks:

PostgreSQL:
    abc123 status=BLOCKED

Redis:
    DELETE redirect:v1:abc123
```

Next read:

```text
Redis miss
DB says BLOCKED
return 403
```

Why delete instead of update?

```text
simpler
avoids partial wrong cache value
forces re-read from source of truth
```

Risk:

```text
DB update succeeds but Redis delete fails.
```

Then:

```text
stale value remains until TTL
```

Mitigation:

```text
short TTL
retry deletion
alert on eviction failure
event-based invalidation later
```

For abuse/blocking path:

```text
keep TTL short
consider strong invalidation retries
```

---

## 13. TTL And Bounded Staleness

Cache-aside accepts possible stale data.

TTL bounds the stale window.

Example:

```text
TTL = 10 minutes
```

Worst case:

```text
a stale value may live up to 10 minutes if invalidation fails
```

ASCII:

```text
DB changes at 10:00

Redis stale value expires at 10:10

Stale window:
10 minutes
```

This is bounded staleness.

Choose TTL based on:

```text
data volatility
risk of stale data
traffic volume
DB capacity
cache memory
```

For URL shortener:

```text
active redirect mapping:
    5-15 minutes initially

not found:
    15-60 seconds

blocked/security-sensitive:
    short TTL or immediate eviction
```

Expiry-aware TTL:

```text
ttl = min(defaultTtl, expiresAt - now)
```

This prevents:

```text
cache redirecting after the URL expires
```

Senior phrase:

```text
TTL is not only performance tuning; TTL is a correctness boundary.
```

---

## 14. Negative Cache-Aside

Negative cache-aside caches missing results.

Flow:

```text
1. Redis miss.
2. DB miss.
3. Cache NOT_FOUND marker briefly.
4. Return 404.
```

ASCII:

```text
GET /bad999
   |
   v
Redis MISS
   |
   v
DB MISS
   |
   v
Redis SET notfound:v1:bad999 TTL 30s
   |
   v
404
```

Why useful?

Bots may request random codes:

```text
/a1x9zz
/promo1234
/admin
/random
```

Without negative cache:

```text
each random code hits DB
```

With negative cache:

```text
repeated bad code avoids DB briefly
```

Risk:

```text
User creates alias during negative TTL.
```

Mitigation:

```text
short negative TTL
delete negative key on create
```

Simple design:

```text
positive key:
    redirect:v1:{shortCode}

negative key:
    redirect:notfound:v1:{shortCode}
```

---

## 15. Thundering Herd Problem

Thundering herd means many requests miss cache at the same time and all hit DB.

Example:

```text
hot key expires at 10:00
10,000 requests arrive at 10:00
all see cache miss
all query DB
```

ASCII:

```text
Cache key expires
      |
      v
Many app pods miss
      |
      v
Many DB queries for same key
      |
      v
DB spike
```

This is also called:

```text
cache stampede
```

Mitigations:

```text
1. Add TTL jitter.
2. Use local short cache.
3. Use single-flight request coalescing.
4. Use distributed lock for hot keys.
5. Refresh-ahead for known hot keys.
6. CDN/browser cache for viral links.
```

TTL jitter:

```text
base TTL = 600 seconds
random jitter = 0-60 seconds
actual TTL = 600 + random(0, 60)
```

Why?

```text
not all keys expire at same time
```

For MiniURLShortener initial version:

```text
use TTL jitter
monitor DB fallback spikes
```

Advanced mitigation later.

---

## 16. Cache Penetration, Breakdown, Avalanche

These are common cache failure forms.

### Cache Penetration

Request for data that does not exist.

```text
Redis miss
DB miss
again and again
```

Fix:

```text
negative caching
rate limiting
input validation
```

### Cache Breakdown

A hot key expires and many requests hit DB.

```text
hot key TTL expires
massive DB fallback
```

Fix:

```text
single-flight
lock
refresh-ahead
local cache
CDN
```

### Cache Avalanche

Many keys expire together or Redis fails.

```text
massive cache miss storm
DB overloaded
```

Fix:

```text
TTL jitter
Redis HA
circuit breaker
rate limiting
fallback limits
```

ASCII:

```text
Penetration:
    nonexistent key -> DB every time

Breakdown:
    one hot key expires -> DB storm

Avalanche:
    many keys expire / Redis down -> DB flood
```

Senior memory:

```text
Caching is not just about hits; it is about controlling misses.
```

---

## 17. Redis Failure Strategy

Cache-aside must handle Redis failure.

Redis can fail during:

```text
GET
SET
DELETE
```

Strategies:

```text
GET failure:
    log metric
    fallback to DB

SET failure:
    log metric
    still return response

DELETE failure:
    log metric
    maybe retry
    rely on TTL as backup
```

ASCII:

```text
Redis GET fails
   |
   v
Do not 500 immediately
   |
   v
Query DB
   |
   v
Return response
```

Why?

```text
Redis is cache.
PostgreSQL is truth.
```

But:

```text
If Redis is down under very high traffic, DB may be overwhelmed.
```

So you also need:

```text
short timeouts
alerts
rate limiting later
circuit breaker later
operational runbook
```

Principle:

```text
Fallback is good, but unlimited fallback can kill the database.
```

---

## 18. Consistency Tradeoffs

Cache-aside is usually eventually consistent.

Example stale read:

```text
1. Cache has ACTIVE.
2. DB changes to BLOCKED.
3. Cache eviction fails.
4. User may get ACTIVE redirect until TTL expires.
```

Tradeoff:

```text
More cache = faster reads but possible stale data.
Less cache = fresher reads but more DB load.
```

Consistency tools:

```text
TTL
explicit invalidation
short TTL for risky states
write-through on create
event-based invalidation later
manual cache purge
```

ASCII:

```text
Speed side:
    Redis hit, no DB

Freshness side:
    DB check every time

Balanced:
    Redis hit + TTL + invalidation
```

For MiniURLShortener:

```text
Most active links can tolerate small staleness.
Blocked malicious links need aggressive invalidation.
Expired links need expiry-aware TTL.
```

Senior answer:

```text
I use bounded staleness with TTL, and explicit invalidation for critical state changes.
```

---

## 19. Java/Spring Boot Implementation

Cache value:

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

Cache service:

```java
package com.miniurl.shortener.url.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedirectCacheService {

    private static final Logger log =
            LoggerFactory.getLogger(RedirectCacheService.class);

    private final RedisTemplate<String, RedirectCacheValue> redisTemplate;

    public RedirectCacheService(RedisTemplate<String, RedirectCacheValue> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Optional<RedirectCacheValue> get(String shortCode) {
        try {
            RedirectCacheValue value =
                    redisTemplate.opsForValue().get(positiveKey(shortCode));

            return Optional.ofNullable(value);
        } catch (Exception ex) {
            log.warn("Redis cache get failed for shortCode={}", shortCode, ex);
            return Optional.empty();
        }
    }

    public void put(String shortCode, RedirectCacheValue value, Duration ttl) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            return;
        }

        try {
            redisTemplate.opsForValue().set(positiveKey(shortCode), value, ttl);
        } catch (Exception ex) {
            log.warn("Redis cache put failed for shortCode={}", shortCode, ex);
        }
    }

    public void evict(String shortCode) {
        try {
            redisTemplate.delete(positiveKey(shortCode));
        } catch (Exception ex) {
            log.warn("Redis cache evict failed for shortCode={}", shortCode, ex);
        }
    }

    private String positiveKey(String shortCode) {
        return "redirect:v1:" + shortCode;
    }
}
```

This code follows cache-aside:

```text
get may hit or miss
put fills after DB read
evict removes stale copy
```

---

## 20. Redirect Service Code

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.url.cache.RedirectCacheService;
import com.miniurl.shortener.url.cache.RedirectCacheValue;
import com.miniurl.shortener.url.entity.ShortUrl;
import com.miniurl.shortener.url.entity.ShortUrlStatus;
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

    public String resolveRedirect(String shortCode) {
        return cacheService.get(shortCode)
                .map(value -> resolveFromCache(shortCode, value))
                .orElseGet(() -> resolveFromDatabase(shortCode));
    }

    private String resolveFromCache(String shortCode, RedirectCacheValue value) {
        validateState(shortCode, value.getStatus(), value.getExpiresAt());
        return value.getLongUrl();
    }

    private String resolveFromDatabase(String shortCode) {
        ShortUrl entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));

        validateState(shortCode, entity.getStatus().name(), entity.getExpiresAt());

        RedirectCacheValue value = new RedirectCacheValue(
                entity.getLongUrl(),
                entity.getStatus().name(),
                entity.getExpiresAt()
        );

        cacheService.put(shortCode, value, calculateTtl(entity.getExpiresAt()));

        return entity.getLongUrl();
    }

    private void validateState(String shortCode, String status, Instant expiresAt) {
        if (ShortUrlStatus.BLOCKED.name().equals(status)) {
            throw new ShortCodeBlockedException(shortCode);
        }

        if (ShortUrlStatus.DELETED.name().equals(status)) {
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

        return untilExpiry.compareTo(DEFAULT_TTL) < 0
                ? untilExpiry
                : DEFAULT_TTL;
    }
}
```

Controller stays simple:

```java
@GetMapping("/{shortCode}")
public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
    String longUrl = redirectService.resolveRedirect(shortCode);

    return ResponseEntity
            .status(302)
            .location(URI.create(longUrl))
            .build();
}
```

Important architecture rule:

```text
Controller does not know cache-aside exists.
Service owns redirect decision.
Cache service owns Redis operations.
Repository owns DB access.
```

---

## 21. Step-by-Step Dry Runs

### Dry Run 1: Cache Hit

Redis:

```text
redirect:v1:abc123 -> ACTIVE, https://example.com, null
```

Request:

```http
GET /abc123
```

Steps:

```text
1. Controller calls service.
2. Service asks cache.
3. Cache returns value.
4. Service checks status ACTIVE.
5. Service checks no expiry.
6. Service returns longUrl.
7. Controller returns 302.
8. Repository is never called.
```

ASCII:

```text
GET /abc123
   |
   v
Redis HIT
   |
   v
302
```

---

### Dry Run 2: Cache Miss And Fill

Redis:

```text
no key
```

PostgreSQL:

```text
abc123 exists
```

Steps:

```text
1. Cache miss.
2. Service calls repository.
3. DB returns ShortUrl entity.
4. Service checks state.
5. Service maps entity to cache value.
6. Service calculates TTL.
7. Redis SET.
8. Return 302.
```

ASCII:

```text
MISS -> DB -> SET CACHE -> 302
```

---

### Dry Run 3: Expired URL

DB row:

```text
shortCode = old1
expiresAt = yesterday
status = ACTIVE
```

Steps:

```text
1. Redis miss.
2. DB returns row.
3. Service sees expiresAt before now.
4. Service throws ShortCodeExpiredException.
5. Handler returns 410.
6. Active redirect is not cached.
```

---

### Dry Run 4: Cache Stale But Defensive Check Catches Expiry

Redis value:

```text
status = ACTIVE
expiresAt = yesterday
```

Steps:

```text
1. Redis hit.
2. Service checks expiresAt.
3. Service sees expired.
4. Service returns 410.
```

Lesson:

```text
Even on cache hit, domain validation matters.
```

---

### Dry Run 5: Redis Failure

Redis GET throws timeout.

Steps:

```text
1. Cache service catches Redis exception.
2. Cache service returns Optional.empty().
3. Service treats as miss.
4. Service queries DB.
5. Redirect succeeds.
```

Result:

```text
slower but available
```

---

## 22. Internal Execution Walkthrough

Full cache-aside request:

```text
1. HTTP request enters Spring Boot.
2. Controller extracts shortCode.
3. Service calls cacheService.get().
4. RedisTemplate sends GET.
5. Redis hit:
       deserialize value
       return Optional(value)
   Redis miss:
       return Optional.empty()
   Redis failure:
       log and return Optional.empty()
6. Service decides:
       cached value -> validate -> return longUrl
       empty -> repository lookup
7. Repository queries PostgreSQL.
8. DB row found:
       validate
       calculate TTL
       cacheService.put()
       return longUrl
9. Controller sends 302.
```

ASCII:

```text
Controller
   |
   v
Service
   |
   +-- cache GET
   |      |
   |      +-- hit -> validate -> return
   |      |
   |      +-- miss/fail
   |
   +-- DB SELECT
          |
          +-- found -> cache SET -> return
          |
          +-- missing -> 404
```

Key learning:

```text
Cache-aside is not hidden magic.
It is explicit branching in the service layer.
```

---

## 23. Testing Strategy

Test cache-aside behavior, not only output.

### Unit test: cache hit avoids DB

```text
Given cache returns value
When resolveRedirect is called
Then repository is never called
```

### Unit test: cache miss calls DB

```text
Given cache empty
When resolveRedirect is called
Then repository.findByShortCode is called
Then cache.put is called
```

### Unit test: expired cached value returns 410

```text
Given cached expiresAt is past
Then ShortCodeExpiredException is thrown
```

### Integration test: first request fills Redis

```text
1. Save row in DB.
2. GET /abc123.
3. Assert 302.
4. Check Redis key exists.
```

### Integration test: second request survives DB delete temporarily

This is optional and must be used carefully.

```text
1. First request fills cache.
2. Delete DB row manually.
3. Second request hits cache.
4. Shows bounded stale behavior.
```

Better test:

```text
evict on delete/update path
```

### Failure test: Redis unavailable

```text
Given Redis throws exception
Then DB fallback still returns redirect
```

Testing checklist:

```text
hit
miss
fill
expiry TTL
stale cached expiry
blocked
deleted
Redis failure
eviction
negative cache
```

---

## 24. Metrics And Observability

Cache-aside must be observable.

Metrics:

```text
cache.hit
cache.miss
cache.put.success
cache.put.failure
cache.evict.success
cache.evict.failure
cache.negative.hit
cache.negative.put
db.fallback
redirect.latency
redis.latency
```

ASCII:

```text
Request
  |
  +-- hit counter
  |
  +-- miss counter
  |
  +-- DB fallback counter
  |
  +-- cache fill counter
  |
  +-- error counter
```

Important ratios:

```text
hit rate = hits / (hits + misses)
fallback rate = DB fallbacks / redirect requests
```

Healthy read-heavy system:

```text
high hit rate
low DB fallback
low Redis latency
low p99 redirect latency
```

Danger signals:

```text
hit rate drops suddenly
DB fallback spikes
Redis errors increase
eviction failures increase
p99 increases
```

Senior rule:

```text
If you do not measure cache hit rate, you do not know whether the cache works.
```

---

## 25. Production Failure Stories

### Failure Story 1: Cache Added But DB Load Did Not Drop

Team added Redis.

DB CPU stayed high.

Root cause:

```text
Cache was written using one key format and read using another.
```

Fix:

```text
centralize key builder
add cache hit-rate metric
test second request avoids DB
```

Lesson:

```text
A cache without metrics can be fake confidence.
```

---

### Failure Story 2: Hot Key Expired And DB Spiked

A viral link had high traffic.

Its Redis key expired.

Thousands of requests missed at the same time.

Root cause:

```text
cache breakdown / thundering herd
```

Fix:

```text
TTL jitter
local tiny cache
single-flight for hot keys later
CDN cache
```

Lesson:

```text
Miss control is as important as hit optimization.
```

---

### Failure Story 3: Deleted Link Still Redirected

User deleted short URL.

DB row became DELETED.

Redis still had ACTIVE value.

Root cause:

```text
delete path forgot cache eviction
```

Fix:

```text
evict on delete/update/block
test invalidation path
```

Lesson:

```text
Every write path must answer: what happens to cache?
```

---

### Failure Story 4: Redis Timeout Became API Timeout

Redis slowed down.

App waited too long on Redis GET.

Redirect p99 exploded.

Root cause:

```text
cache call timeout too high
```

Fix:

```text
short Redis timeout
fallback to DB
circuit breaker later
alert on Redis latency
```

Lesson:

```text
A slow cache can be worse than no cache.
```

---

### Failure Story 5: Random Bot Codes Hit DB

Bots requested random codes.

Each random code missed Redis and DB.

Root cause:

```text
no negative cache
```

Fix:

```text
negative cache missing codes for 30 seconds
rate limit later
```

Lesson:

```text
Cache penetration can destroy DB with non-existing keys.
```

---

## 26. Debugging Mindset

When cache-aside system misbehaves, ask:

```text
Is request a cache hit, miss, or cache error fallback?
What key was used?
Does Redis contain that key?
What is TTL?
Is value deserializing correctly?
Did DB fallback happen?
Was cache fill attempted?
Did cache fill fail?
Did invalidation happen after update/delete/block?
Are many keys expiring together?
Is a hot key causing breakdown?
Are bots causing penetration?
```

Useful Redis commands:

```bash
redis-cli GET redirect:v1:abc123
redis-cli TTL redirect:v1:abc123
redis-cli EXISTS redirect:v1:abc123
redis-cli DEL redirect:v1:abc123
redis-cli INFO stats
redis-cli INFO memory
```

Debug map:

```text
High DB load:
    low hit rate
    Redis down
    key mismatch
    TTL too short
    penetration attack

Stale redirect:
    invalidation missing
    TTL too long
    CDN/browser cache
    stale local cache

High p99:
    Redis slow
    DB fallback slow
    connection pool waiting
    thundering herd

Unexpected 404 after create:
    negative cache not evicted
    replica lag
    create did not write-through cache
```

Golden rule:

```text
Trace one shortCode through Redis key, DB row, TTL, and service decision.
```

---

## 27. Common Mistakes

### Mistake 1: Thinking Cache-Aside Is Automatic

Wrong:

```text
Adding Redis dependency automatically caches DB queries.
```

Correct:

```text
Application explicitly checks, fills, and evicts cache.
```

### Mistake 2: No Cache Invalidation On Writes

Wrong:

```text
Update DB only.
```

Correct:

```text
Update DB, then evict cache.
```

### Mistake 3: No TTL

Wrong:

```text
Cache forever.
```

Correct:

```text
Use TTL to bound stale data and memory.
```

### Mistake 4: Blind Redirect On Cache Hit

Wrong:

```text
cache hit -> return longUrl
```

Correct:

```text
cache hit -> check status and expiry -> return or error
```

### Mistake 5: Redis Failure Returns 500

Wrong:

```text
Redis down -> app down
```

Correct:

```text
Redis down -> DB fallback if safe
```

### Mistake 6: No Negative Cache

Wrong:

```text
Unknown short codes hit DB forever.
```

Correct:

```text
Cache NOT_FOUND briefly.
```

### Mistake 7: Ignoring Thundering Herd

Wrong:

```text
All hot keys expire at same time.
```

Correct:

```text
TTL jitter and hot-key strategy.
```

### Mistake 8: No Hit Rate Metric

Wrong:

```text
Assume cache works.
```

Correct:

```text
Measure hit rate, miss rate, fallback rate.
```

---

## 28. Interview-Ready Explanation

If interviewer asks:

```text
Explain cache-aside pattern.
```

Strong answer:

```text
Cache-aside is a lazy caching pattern where the application manages the cache explicitly.
For a read request, the app first checks Redis. If the data is present, it uses it and avoids
the database. If the data is missing, the app queries PostgreSQL, stores the result in Redis
with a TTL, and returns the response. PostgreSQL remains the source of truth and Redis is only
a fast copy. On writes like update, block, or delete, the app updates the database and evicts
the cache key so the next read reloads fresh data. For a URL shortener redirect API, this works
well because the lookup is key-value shaped: shortCode maps to longUrl, status, and expiresAt.
I would also use expiry-aware TTL, short negative caching for missing codes, Redis failure
fallback to DB, and metrics for cache hit rate, miss rate, DB fallback, and p99 latency.
```

Senior version:

```text
Cache-aside optimizes read-heavy systems by shifting repeated reads from the database to a fast
cache, while accepting bounded staleness. Correctness is protected with TTL, invalidation on
critical writes, and source-of-truth fallback to the database.
```

Why this is strong:

```text
1. Explains flow clearly.
2. Identifies app responsibility.
3. Keeps DB as source of truth.
4. Handles writes and invalidation.
5. Mentions TTL and stale data.
6. Mentions negative caching.
7. Mentions failure fallback.
8. Mentions metrics and p99.
```

---

## 29. Senior Engineer Checklist

Before calling cache-aside production-shaped, confirm:

```text
[ ] Service checks Redis before DB
[ ] Cache hit avoids DB
[ ] Cache miss queries DB
[ ] DB result fills Redis
[ ] Cache value contains enough decision data
[ ] TTL exists
[ ] TTL respects expiresAt
[ ] Writes evict cache
[ ] Delete/block/update paths are tested
[ ] Redis get failure falls back to DB
[ ] Redis set failure does not fail successful read
[ ] Redis delete failure is logged/alerted
[ ] Negative cache exists for repeated misses
[ ] TTL jitter considered
[ ] Hot key strategy considered
[ ] Cache hit/miss metrics exist
[ ] DB fallback metric exists
[ ] p99 is tracked separately
[ ] Tests prove hit, miss, fill, stale, eviction, failure
```

---

## 30. One-Page Cheat Sheet

```text
Core mental model:
Cache-aside = app checks cache beside DB.

Read flow:
1. Redis GET
2. Hit -> validate -> return
3. Miss -> DB SELECT
4. DB found -> Redis SET TTL -> return
5. DB missing -> optional negative cache -> 404

Write flow:
1. Update DB
2. Evict Redis key
3. Next read reloads fresh data

Source of truth:
PostgreSQL

Fast copy:
Redis

TTL:
bounds stale data
controls memory
must respect expiresAt

Negative cache:
protects DB from repeated missing keys

Main failure patterns:
cache penetration = missing keys hit DB
cache breakdown = hot key expires
cache avalanche = many keys expire / Redis down

Mitigations:
negative cache
TTL jitter
short Redis timeout
DB fallback
cache invalidation
metrics
hot-key strategy

Must measure:
hit rate
miss rate
DB fallback
cache errors
redirect p99
Redis latency
```

---

## 31. One Picture To Remember

```text
                    CACHE-ASIDE PATTERN

                  "The app owns the decision"

                         Client
                           |
                           v
                  +----------------+
                  | Spring Boot    |
                  | RedirectService|
                  +--------+-------+
                           |
                           v
                  +----------------+
                  | Redis GET      |
                  | redirect:key   |
                  +---+--------+---+
                      |        |
             HIT -----+        +----- MISS
              |                        |
              v                        v
    +-------------------+      +-------------------+
    | Validate cached   |      | PostgreSQL SELECT |
    | status / expiry   |      | source of truth   |
    +---------+---------+      +---------+---------+
              |                          |
              v                          v
        302 / error             row found / not found
                                         |
                                         v
                                +-------------------+
                                | Redis SET TTL     |
                                | cache fill        |
                                +---------+---------+
                                          |
                                          v
                                    302 / error


WRITE SIDE:

DB UPDATE / BLOCK / DELETE
          |
          v
Redis DELETE key
          |
          v
Next read reloads truth


FINAL MEMORY:

Check cache.
Miss database.
Fill cache.
Evict on writes.
Bound staleness with TTL.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Cache-aside means the application checks Redis first, then PostgreSQL on miss.
2. PostgreSQL remains the source of truth; Redis is only a fast lazy-loaded copy.
3. On cache miss, the app fills Redis with a TTL so future reads are faster.
4. On update, block, or delete, the app evicts Redis so stale data is not reused.
5. Production cache-aside must handle negative caching, TTL, Redis failure, thundering herd, metrics, and stale-data tradeoffs.
```

Next chapter:

```text
023_DB_Indexing_For_URL_Lookup.md
```
