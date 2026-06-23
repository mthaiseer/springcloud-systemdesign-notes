# 028_Cache_Breakdown_Hotkeys.md
# MiniURLShortener — Cache Breakdown & Hot Keys

> Core mental model: **Cache breakdown happens when a very hot key suddenly misses cache and many requests hit the database at the same time. A hot key is one cache key receiving disproportionate traffic. In a URL shortener, one viral short link can become a hot key and break Redis, PostgreSQL, or the app if you do not design for it.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What Is A Hot Key?](#4-what-is-a-hot-key)
- [5. What Is Cache Breakdown?](#5-what-is-cache-breakdown)
- [6. Cache Breakdown vs Penetration vs Avalanche](#6-cache-breakdown-vs-penetration-vs-avalanche)
- [7. URL Shortener Hot Key Example](#7-url-shortener-hot-key-example)
- [8. Baseline Cache-Aside Failure](#8-baseline-cache-aside-failure)
- [9. TTL Expiry Storm](#9-ttl-expiry-storm)
- [10. TTL Jitter](#10-ttl-jitter)
- [11. Mutex Lock / Redis Lock](#11-mutex-lock--redis-lock)
- [12. Single-Flight Request Coalescing](#12-single-flight-request-coalescing)
- [13. Local In-Memory Cache](#13-local-in-memory-cache)
- [14. CDN And Browser Cache](#14-cdn-and-browser-cache)
- [15. Hot Key Replication / Key Splitting](#15-hot-key-replication--key-splitting)
- [16. Stale-While-Revalidate](#16-stale-while-revalidate)
- [17. Pre-Warming Hot Links](#17-pre-warming-hot-links)
- [18. Protecting PostgreSQL](#18-protecting-postgresql)
- [19. Protecting Redis](#19-protecting-redis)
- [20. Recommended MiniURLShortener Strategy](#20-recommended-miniurlshortener-strategy)
- [21. Java/Spring Boot Implementation Sketch](#21-javaspring-boot-implementation-sketch)
- [22. Step-by-Step Dry Runs](#22-step-by-step-dry-runs)
- [23. Internal Execution Walkthrough](#23-internal-execution-walkthrough)
- [24. Testing Strategy](#24-testing-strategy)
- [25. Metrics And Observability](#25-metrics-and-observability)
- [26. Production Failure Stories](#26-production-failure-stories)
- [27. Debugging Mindset](#27-debugging-mindset)
- [28. Common Mistakes](#28-common-mistakes)
- [29. Interview-Ready Explanation](#29-interview-ready-explanation)
- [30. Senior Engineer Checklist](#30-senior-engineer-checklist)
- [31. One-Page Cheat Sheet](#31-one-page-cheat-sheet)
- [32. One Picture To Remember](#32-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener redirect API is read-heavy:

```http
GET /{shortCode}
```

Most links may get small traffic.

But a few links may go viral:

```text
/sale2026
/worldcup
/breaking-news
/iphone-launch
```

One short link can receive:

```text
10,000 RPS
50,000 RPS
100,000 RPS
```

This one key becomes a hot key.

Redis key:

```text
redirect:v1:sale2026
```

If this key is cached, Redis serves many requests.

But if this key expires or is evicted:

```text
all requests miss together
```

Then many app pods query PostgreSQL for the same short code.

ASCII:

```text
Hot key cached:

Clients
  | | | | | | |
  v v v v v v v
Redis HIT redirect:v1:sale2026
  |
  v
302 fast


Hot key expires:

Clients
  | | | | | | |
  v v v v v v v
Redis MISS MISS MISS MISS
  |
  v
PostgreSQL storm
```

This is cache breakdown.

Production memory:

```text
Caching a hot key is powerful.
Losing a hot key suddenly is dangerous.
```

---

## 2. The One Core Mental Model

The core mental model:

```text
DO NOT LET MANY REQUESTS REBUILD THE SAME MISSING HOT CACHE KEY
```

When a hot cache key expires, only one request should rebuild it.

Other requests should:

```text
wait briefly
serve stale value
use local cache
be absorbed by CDN
or retry after cache is rebuilt
```

ASCII:

```text
Bad:

1000 requests miss
      |
      v
1000 DB queries


Good:

1000 requests miss
      |
      v
1 request rebuilds from DB
999 requests wait/use stale/local/CDN
```

One-line memory:

```text
A hot cache miss must be converted from many DB hits into one DB hit.
```

For URL shortener:

```text
One viral shortCode should not generate thousands of identical PostgreSQL lookups.
```

---

## 3. Problem Statement

Design protection against cache breakdown and hot keys for MiniURLShortener.

The design must support:

```text
1. Detect hot redirect keys.
2. Prevent DB stampede when hot key expires.
3. Reduce Redis pressure for extremely hot keys.
4. Keep redirect p99 low during viral traffic.
5. Avoid stale redirect for too long after block/delete.
6. Use TTL jitter to avoid synchronized expiry.
7. Use local cache or single-flight for hot keys.
8. Use CDN/browser cache where safe.
9. Keep source of truth in PostgreSQL.
10. Expose metrics for hot key/cache breakdown debugging.
```

Main hot path:

```text
GET /sale2026
```

Main risk:

```text
Redis key expires while traffic is huge.
```

Expected outcome:

```text
DB is protected.
Redis is protected.
App p99 remains controlled.
```

---

## 4. What Is A Hot Key?

A hot key is a cache key that receives much more traffic than normal keys.

Example:

```text
redirect:v1:sale2026
```

Normal key traffic:

```text
1 request/min
10 requests/min
100 requests/hour
```

Hot key traffic:

```text
10,000 requests/sec
```

ASCII:

```text
Traffic distribution:

normal keys:
redirect:a1  ##
redirect:b2  #
redirect:c3  ###

hot key:
redirect:sale2026  ##################################################
```

Hot keys happen because traffic distribution is skewed.

URL shortener examples:

```text
celebrity shares link
marketing campaign
viral tweet
news article
QR code on TV
payment link
coupon link
```

Hot key symptoms:

```text
one Redis key dominates GET traffic
one Redis shard CPU high
redirect p99 increases
DB fallback spikes if key expires
network bandwidth high
```

Hot keys are not bugs by themselves.

They are business success plus infrastructure risk.

---

## 5. What Is Cache Breakdown?

Cache breakdown, also called cache stampede for a hot key, happens when:

```text
1. A very hot cache key exists.
2. The key expires or is evicted.
3. Many requests arrive at the same time.
4. All requests miss cache.
5. All requests query database.
6. Database is overloaded.
```

ASCII:

```text
Before expiry:

Redis:
redirect:sale2026 -> value

After expiry:

Request 1 -> miss -> DB
Request 2 -> miss -> DB
Request 3 -> miss -> DB
Request 4 -> miss -> DB
...
Request N -> miss -> DB
```

The problem is not one miss.

The problem is:

```text
many simultaneous misses for the same key
```

A normal miss is fine.

A hot key miss storm is dangerous.

Production memory:

```text
The hotter the key, the more dangerous its expiry.
```

---

## 6. Cache Breakdown vs Penetration vs Avalanche

These terms are related but different.

```text
+--------------------+---------------------------------------------+
| Problem            | Meaning                                     |
+--------------------+---------------------------------------------+
| Cache penetration  | requests for non-existing keys hit DB       |
| Cache breakdown    | one hot key expires and many requests hit DB|
| Cache avalanche    | many keys expire/fail together              |
+--------------------+---------------------------------------------+
```

ASCII:

```text
Penetration:
    /random1 -> not exist -> DB
    /random2 -> not exist -> DB
    /random3 -> not exist -> DB


Breakdown:
    /sale2026 hot key expires
    many requests for same key -> DB


Avalanche:
    many cache keys expire at same time
    many different keys -> DB
```

Solutions differ:

```text
Penetration:
    Bloom filter
    negative cache
    rate limiting

Breakdown:
    mutex lock
    single-flight
    stale-while-revalidate
    local cache
    CDN

Avalanche:
    TTL jitter
    Redis HA
    staged warmup
```

This chapter focuses on:

```text
breakdown and hot keys
```

---

## 7. URL Shortener Hot Key Example

Campaign link:

```text
https://sho.rt/sale2026
```

Traffic:

```text
50,000 RPS
```

Cache key:

```text
redirect:v1:sale2026
```

Normal cached flow:

```text
50,000 RPS -> Redis HIT -> 302
```

DB load:

```text
0 RPS for this key
```

Now key expires.

For 100ms, requests arrive:

```text
50,000 RPS * 0.1 sec = 5,000 requests
```

If all miss:

```text
5,000 DB queries for same row
```

ASCII:

```text
0.1 second after expiry:

5000 requests
   |
   v
5000 Redis misses
   |
   v
5000 PostgreSQL lookups for sale2026
```

Even if each query is indexed, this is wasteful and can hurt p99.

Goal:

```text
1 DB query should refill cache.
All others should avoid DB.
```

---

## 8. Baseline Cache-Aside Failure

Normal cache-aside:

```text
1. Check Redis.
2. Miss.
3. Query DB.
4. Set Redis.
5. Return.
```

This works for normal traffic.

But for hot key expiry:

```text
many requests do step 2 at same time
many requests do step 3 at same time
```

ASCII:

```text
Time T: key expired

Thread A: Redis miss -> DB
Thread B: Redis miss -> DB
Thread C: Redis miss -> DB
Thread D: Redis miss -> DB
Thread E: Redis miss -> DB
```

All threads race to rebuild same value.

Bad effects:

```text
DB QPS spike
Hikari pool fills
app threads wait
Redis set duplicated
p99 increases
possible timeout cascade
```

Therefore cache-aside needs protection for hot keys.

---

## 9. TTL Expiry Storm

TTL expiry storm happens when many keys expire together.

Example:

```text
all redirect keys set with TTL = 10 minutes exactly
```

If many keys were created/warmed together:

```text
they expire together
```

ASCII:

```text
10:00 cache warm:
    key1 TTL 600
    key2 TTL 600
    key3 TTL 600

10:10:
    key1 expires
    key2 expires
    key3 expires
```

For one hot key, this is breakdown.

For many keys, this becomes avalanche.

Solution:

```text
TTL jitter
```

Instead of:

```text
TTL = 600 seconds
```

Use:

```text
TTL = 600 + random(0, 120) seconds
```

or:

```text
TTL = 600 - random(0, 120) seconds
```

This spreads expiry.

---

## 10. TTL Jitter

TTL jitter adds randomness to expiration time.

Base TTL:

```text
10 minutes
```

Jitter:

```text
0 to 2 minutes
```

Actual TTL examples:

```text
10m 05s
10m 47s
11m 10s
9m 30s
```

ASCII:

```text
Without jitter:

key1 expires at 10:10:00
key2 expires at 10:10:00
key3 expires at 10:10:00


With jitter:

key1 expires at 10:09:20
key2 expires at 10:10:45
key3 expires at 10:11:10
```

Java helper:

```java
private Duration addJitter(Duration baseTtl) {
    long jitterSeconds = ThreadLocalRandom.current().nextLong(0, 120);
    return baseTtl.plusSeconds(jitterSeconds);
}
```

For expiry-aware URLs:

```text
finalTtl = min(expiryAwareTtl, baseTtl + jitter)
```

Important:

```text
Do not let jitter extend cache beyond link expiresAt.
```

Correct:

```text
ttl = min(timeUntilExpiry, baseTtl + jitter)
```

---

## 11. Mutex Lock / Redis Lock

Mutex lock means only one request rebuilds cache.

Flow:

```text
1. Redis miss.
2. Try to acquire lock for key.
3. If lock acquired:
       query DB
       set cache
       release lock
4. If lock not acquired:
       wait briefly and retry cache
       or serve stale
       or return controlled fallback
```

ASCII:

```text
Hot key miss
   |
   v
Try lock: lock:redirect:sale2026
   |
   +-- acquired -> one DB query -> fill cache
   |
   +-- not acquired -> wait/retry cache
```

Redis lock command concept:

```text
SET lock:redirect:sale2026 requestId NX PX 3000
```

Meaning:

```text
set lock only if not exists
expire after 3 seconds
```

Important:

```text
lock must have TTL
```

Otherwise if lock owner crashes:

```text
lock remains forever
```

Caution:

```text
Distributed locks are subtle.
Use simple short lock carefully.
```

For this use case, lock protects cache rebuild, not money transfer.

So simple lock is acceptable if designed safely.

---

## 12. Single-Flight Request Coalescing

Single-flight means inside one app instance:

```text
multiple requests for same key share one in-flight DB call
```

ASCII:

```text
Within one Spring Boot pod:

Request A misses cache -> starts DB call
Request B same key -> waits for A
Request C same key -> waits for A

A returns value
B and C reuse result
```

This reduces duplicate DB queries per pod.

But if there are many pods:

```text
single-flight alone is per-pod only
```

You may still need Redis lock across pods.

Combination:

```text
local single-flight + Redis distributed lock
```

Simple learning version:

```text
use Redis lock
```

Advanced version:

```text
use Caffeine async cache / request coalescing
```

Production memory:

```text
Single-flight reduces duplicate work inside a process.
Distributed lock reduces duplicate work across processes.
```

---

## 13. Local In-Memory Cache

Local cache means each Spring Boot pod keeps a tiny cache in memory.

Example:

```text
Caffeine cache
TTL = 1 to 5 seconds
```

Flow:

```text
1. Check local cache.
2. If hit, return.
3. If miss, check Redis.
4. If Redis hit, populate local cache.
```

ASCII:

```text
Request
   |
   v
Local cache
   |
   +-- hit -> 302
   |
   +-- miss -> Redis -> DB if needed
```

Why useful for hot keys?

```text
very hot key can be served inside pod memory
reduces Redis QPS
improves latency
```

Tradeoff:

```text
stale data for tiny TTL
```

For URL shortener:

```text
1-5 seconds local TTL can be acceptable for active links
```

But for blocked malicious links:

```text
stale local cache may continue redirecting briefly
```

Mitigations:

```text
tiny TTL
evict local on block event
do not local-cache suspicious links
```

---

## 14. CDN And Browser Cache

For extremely hot redirects, the best request is the one that never reaches your app.

CDN/browser can cache redirects briefly.

Example response:

```http
302 Found
Location: https://example.com/sale
Cache-Control: public, max-age=30
```

ASCII:

```text
Client
  |
  v
CDN / Browser Cache
  |
  +-- hit -> redirect without app
  |
  +-- miss -> app
```

Benefits:

```text
reduces app RPS
reduces Redis RPS
reduces DB risk
global latency improvement
```

Risks:

```text
blocked/deleted link may remain cached for max-age
```

Therefore use:

```text
short max-age for mutable links
no-store for risky links
longer TTL only for immutable trusted links
```

For MiniURLShortener:

```text
default 302 with short cache headers for safe public links
```

Do not use 301 broadly unless link is immutable.

---

## 15. Hot Key Replication / Key Splitting

In Redis Cluster, a single hot key may overload one shard.

Why?

```text
one key maps to one hash slot
one hash slot belongs to one shard
```

ASCII:

```text
redirect:sale2026
      |
      v
Redis shard 3 only
```

If traffic is huge:

```text
shard 3 becomes hot
other shards idle
```

Key splitting idea:

```text
redirect:sale2026:0
redirect:sale2026:1
redirect:sale2026:2
redirect:sale2026:3
```

Requests randomly choose one replica key.

ASCII:

```text
Requests
  |
  +-- redirect:sale2026:0
  +-- redirect:sale2026:1
  +-- redirect:sale2026:2
  +-- redirect:sale2026:3
```

This spreads reads.

Tradeoffs:

```text
more complex invalidation
more memory
must update all replicas
```

Use only for extreme hot keys.

For most MiniURLShortener stages:

```text
CDN + local cache + Redis is enough
```

---

## 16. Stale-While-Revalidate

Stale-while-revalidate means:

```text
serve stale value briefly while one request refreshes cache in background
```

Cache value stores:

```text
data
softExpireAt
hardExpireAt
```

Flow:

```text
if now < softExpireAt:
    serve fresh

if softExpireAt < now < hardExpireAt:
    serve stale
    trigger one async refresh

if now > hardExpireAt:
    block and rebuild or fallback DB
```

ASCII:

```text
Timeline:

fresh period       stale allowed period       expired
|------------------|--------------------------|
now before soft    now between soft/hard      now after hard
serve fresh        serve stale + refresh      rebuild required
```

Benefits:

```text
hot key never fully disappears suddenly
p99 remains low
DB stampede reduced
```

Risk:

```text
stale redirect for a bounded time
```

For URL shortener:

```text
safe for active stable links
not safe for abuse-blocked links unless invalidation is strong
```

This is an advanced strategy.

---

## 17. Pre-Warming Hot Links

Pre-warming means loading cache before traffic arrives.

Examples:

```text
marketing campaign starts at 10:00
preload /sale2026 at 09:55
```

Flow:

```text
1. Identify upcoming hot shortCode.
2. Read from DB.
3. Set Redis cache.
4. Optionally set local/CDN warmup.
```

ASCII:

```text
Before campaign
   |
   v
DB lookup
   |
   v
Redis SET redirect:sale2026
   |
   v
Campaign starts with cache ready
```

Useful for:

```text
scheduled campaigns
large newsletters
QR code ads
known celebrity/product links
```

Not useful for:

```text
unpredictable viral traffic
```

Still valuable in production operations.

---

## 18. Protecting PostgreSQL

Cache breakdown ultimately hurts PostgreSQL.

Protection layers:

```text
1. Redis cache
2. TTL jitter
3. lock/single-flight
4. local cache
5. CDN cache
6. Hikari pool backpressure
7. DB index on short_code
8. timeouts
9. circuit breaker/rate limits
```

ASCII:

```text
Hot key miss storm
      |
      v
Protection layers

CDN -> local cache -> Redis -> lock -> Hikari -> DB index -> PostgreSQL
```

If all requests reach DB:

```text
Hikari pool limits concurrency
short_code index makes lookup cheap
timeouts prevent infinite wait
```

But the best protection is:

```text
do not let all requests reach DB
```

DB should be fallback, not hot-key serving layer.

---

## 19. Protecting Redis

Hot key can also overload Redis.

Redis protection:

```text
local in-memory cache
CDN/browser cache
hot key replication
short response payload
connection pool tuning
Redis cluster
monitor per-command latency
```

ASCII:

```text
Without local cache:

Every request -> Redis GET same key


With local cache:

Most requests -> app memory
Some requests -> Redis
```

Redis symptoms:

```text
GET latency high
CPU high
network bandwidth high
one shard hot
evictions increase
timeouts
```

If Redis becomes bottleneck:

```text
add local cache for very hot links
cache at CDN/browser
investigate hot key distribution
```

---

## 20. Recommended MiniURLShortener Strategy

Implement progressively.

### Level 1: Basic Protection

```text
Redis cache-aside
positive TTL
negative cache
short_code DB index
Hikari tuning
```

### Level 2: Breakdown Protection

```text
TTL jitter
Redis mutex lock on cache rebuild
short lock TTL
retry cache after lock wait
```

### Level 3: Hot Key Protection

```text
local Caffeine cache 1-5 seconds
CDN/browser short max-age
hot key metrics
pre-warm campaigns
```

### Level 4: Advanced

```text
stale-while-revalidate
single-flight
hot key replication
Redis cluster
gateway/WAF protection
```

ASCII:

```text
Beginner production path:

Cache aside
   |
   v
TTL jitter
   |
   v
Redis lock
   |
   v
Local cache
   |
   v
CDN cache
```

For MiniURLShortener next implementation:

```text
Add TTL jitter + simple Redis lock first.
```

---

## 21. Java/Spring Boot Implementation Sketch

### TTL Jitter

```java
package com.miniurl.shortener.url.cache;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class CacheTtlCalculator {

    private final Duration baseTtl;
    private final Duration maxJitter;

    public CacheTtlCalculator(Duration baseTtl, Duration maxJitter) {
        this.baseTtl = baseTtl;
        this.maxJitter = maxJitter;
    }

    public Duration withJitter() {
        long jitterSeconds = ThreadLocalRandom.current()
                .nextLong(0, maxJitter.toSeconds() + 1);

        return baseTtl.plusSeconds(jitterSeconds);
    }

    public Duration expiryAware(Duration timeUntilExpiry) {
        Duration ttlWithJitter = withJitter();

        if (timeUntilExpiry == null) {
            return ttlWithJitter;
        }

        return timeUntilExpiry.compareTo(ttlWithJitter) < 0
                ? timeUntilExpiry
                : ttlWithJitter;
    }
}
```

### Simple Redis Lock Service

```java
package com.miniurl.shortener.url.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class CacheRebuildLockService {

    private final StringRedisTemplate redisTemplate;

    public CacheRebuildLockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String tryLock(String shortCode, Duration ttl) {
        String token = UUID.randomUUID().toString();
        String key = lockKey(shortCode);

        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(key, token, ttl);

        return Boolean.TRUE.equals(acquired) ? token : null;
    }

    public void unlock(String shortCode, String token) {
        String key = lockKey(shortCode);
        String current = redisTemplate.opsForValue().get(key);

        if (token.equals(current)) {
            redisTemplate.delete(key);
        }
    }

    private String lockKey(String shortCode) {
        return "lock:redirect:" + shortCode;
    }
}
```

Important production note:

```text
Unlock should ideally be atomic using Lua:
if get(key) == token then delete(key)
```

The above is a learning sketch.

### Protected Cache Miss Flow

```java
public String resolveFromDatabaseWithBreakdownProtection(String shortCode) {
    String lockToken = lockService.tryLock(shortCode, Duration.ofSeconds(3));

    if (lockToken != null) {
        try {
            return loadFromDbAndFillCache(shortCode);
        } finally {
            lockService.unlock(shortCode, lockToken);
        }
    }

    sleepBriefly();

    Optional<RedirectCacheValue> cached = cacheService.get(shortCode);

    if (cached.isPresent()) {
        return resolveFromCache(shortCode, cached.get());
    }

    return loadFromDbAndFillCache(shortCode);
}
```

This reduces DB stampede.

---

## 22. Step-by-Step Dry Runs

### Dry Run 1: Normal Cache Hit

Redis:

```text
redirect:v1:sale2026 exists
```

Traffic:

```text
50,000 RPS
```

Flow:

```text
1. Requests check local cache or Redis.
2. Cache hit.
3. Return 302.
4. DB not touched.
```

ASCII:

```text
GET /sale2026 -> cache hit -> 302
```

---

### Dry Run 2: Hot Key Expires Without Protection

Time:

```text
10:00 key expires
```

Requests:

```text
5,000 requests arrive in 100ms
```

Flow:

```text
1. All requests miss Redis.
2. All query DB.
3. Hikari pool saturates.
4. DB gets duplicate queries.
5. p99 increases.
```

ASCII:

```text
5000 misses -> 5000 DB queries
```

This is cache breakdown.

---

### Dry Run 3: Hot Key Expires With Redis Lock

Flow:

```text
1. First request misses cache.
2. It acquires rebuild lock.
3. It queries DB.
4. It fills Redis.
5. Other requests fail to get lock.
6. They wait briefly.
7. They retry Redis.
8. Redis now has value.
9. They return 302 without DB.
```

ASCII:

```text
5000 misses
   |
   +-- 1 gets lock -> DB -> SET cache
   |
   +-- 4999 wait -> retry Redis -> HIT
```

Result:

```text
1 DB query instead of 5000
```

---

### Dry Run 4: TTL Jitter Prevents Avalanche

Without jitter:

```text
key1 expires 10:10:00
key2 expires 10:10:00
key3 expires 10:10:00
```

With jitter:

```text
key1 expires 10:09:44
key2 expires 10:10:37
key3 expires 10:11:12
```

Flow:

```text
misses spread over time
DB fallback is smoother
```

---

### Dry Run 5: Local Cache Protects Redis

Pod local cache:

```text
sale2026 TTL = 2 seconds
```

Traffic:

```text
many requests to same pod
```

Flow:

```text
1. First request reads Redis.
2. Pod stores local value.
3. Next requests hit local memory.
4. Redis QPS drops.
```

ASCII:

```text
Pod
  |
  +-- local hit -> 302
  |
  +-- local miss -> Redis
```

---

## 23. Internal Execution Walkthrough

Protected redirect flow:

```text
1. Request enters controller.
2. Service checks local cache.
3. If local hit, return redirect.
4. If local miss, check Redis.
5. If Redis hit, populate local cache and return.
6. If Redis miss, try rebuild lock.
7. If lock acquired:
       query DB
       set Redis with jittered TTL
       set local cache
       release lock
       return
8. If lock not acquired:
       sleep briefly
       retry Redis
       if hit, return
       if still miss, fallback carefully
```

ASCII:

```text
Request
  |
  v
Local cache
  |
  +-- hit -> 302
  |
  +-- miss
        |
        v
      Redis
        |
        +-- hit -> local set -> 302
        |
        +-- miss
              |
              v
            Lock?
              |
              +-- yes -> DB -> Redis set -> 302
              |
              +-- no  -> wait -> Redis retry -> 302
```

This is how hot-key miss storms are controlled.

---

## 24. Testing Strategy

### Unit Tests

```text
TTL jitter returns values within expected range
expiry-aware TTL never exceeds expiresAt
lock acquired path calls DB once
lock not acquired path retries cache
local cache hit avoids Redis/DB
```

### Integration Tests

Use Redis Testcontainers:

```text
1. Delete cache key.
2. Run many concurrent requests for same shortCode.
3. Verify DB repository called limited times.
4. Verify response success.
```

### Load Tests

With k6/JMeter/Gatling:

```text
1. Warm hot key.
2. Send high RPS.
3. Expire/delete Redis key during test.
4. Observe DB fallback spike.
5. Compare with/without lock.
```

### Failure Tests

```text
lock holder crashes
lock TTL expires
Redis unavailable
DB slow
blocked link with local cache
```

Testing checklist:

```text
cache hit
cache miss
hot key expiry
TTL jitter
lock acquire
lock contention
local cache
stale risk
DB fallback count
p99 behavior
```

---

## 25. Metrics And Observability

Metrics:

```text
redirect.cache.local.hit
redirect.cache.local.miss
redirect.cache.redis.hit
redirect.cache.redis.miss
redirect.cache.rebuild.lock.acquired
redirect.cache.rebuild.lock.wait
redirect.cache.rebuild.lock.timeout
redirect.db.lookup
redirect.hotkey.detected
redirect.latency.p95
redirect.latency.p99
redis.get.latency
db.query.latency
hikari.connections.pending
```

Hot key detection:

```text
top shortCodes by request count
top Redis keys by access
per-key QPS sampling
```

ASCII:

```text
Hot key dashboard:

shortCode       RPS
sale2026        50000
abc123          120
test1           30
```

Alert signs:

```text
Redis miss spike for one key
DB lookup spike for one key
Hikari pending spike
Redis shard CPU high
redirect p99 spike
lock wait high
```

Golden metric relationship:

```text
Hot key expires -> Redis miss spike
Good protection -> lock acquired once, DB lookup small
Bad protection -> DB lookup spike massive
```

---

## 26. Production Failure Stories

### Failure Story 1: Viral Link Expired From Cache

A campaign link got 80,000 RPS.

Cache key expired.

Thousands of app threads queried PostgreSQL.

Root cause:

```text
no cache breakdown protection
```

Fix:

```text
Redis mutex lock
local cache
CDN short cache
pre-warming
```

Lesson:

```text
Hot key expiry can be more dangerous than no cache in normal traffic.
```

---

### Failure Story 2: Same TTL Caused Cache Avalanche

Many campaign links were warmed at the same time with 10-minute TTL.

They expired together.

DB fallback spiked.

Root cause:

```text
no TTL jitter
```

Fix:

```text
add randomized TTL jitter
```

Lesson:

```text
Synchronized expiration creates artificial traffic spikes.
```

---

### Failure Story 3: Redis Shard Hot From One Key

One short link dominated Redis traffic.

Only one Redis cluster shard was overloaded.

Root cause:

```text
single hot key maps to one shard
```

Fix:

```text
CDN cache
local cache
hot key replication/key splitting for extreme case
```

Lesson:

```text
Redis cluster distributes keys, not traffic for one key.
```

---

### Failure Story 4: Blocked Link Served From Local Cache

Admin blocked phishing link.

Redis was evicted.

But app local cache had value for 5 seconds.

Users still redirected briefly.

Root cause:

```text
local cache stale window
```

Fix:

```text
shorter local TTL
evict local cache on block event
disable local cache for suspicious links
```

Lesson:

```text
Every cache layer adds stale-data risk.
```

---

### Failure Story 5: Lock Without TTL Deadlocked Rebuild

Request acquired lock and crashed.

Lock had no TTL.

All future rebuilds waited or bypassed badly.

Root cause:

```text
cache rebuild lock had no expiry
```

Fix:

```text
SET NX PX with short TTL
safe unlock token
fallback path
```

Lesson:

```text
Every distributed lock must expire.
```

---

## 27. Debugging Mindset

When redirect p99 spikes, ask:

```text
Is traffic dominated by one shortCode?
Did Redis miss rate spike?
Did a hot key expire?
Did DB lookup for same key spike?
Is Hikari pending high?
Is Redis CPU/shard hot?
Are local cache hits working?
Is lock acquired once or many times?
Are requests waiting on lock too long?
Did CDN cache stop working?
Was a blocked link cached locally?
```

Debug map:

```text
High DB lookup for one shortCode:
    cache breakdown
    lock missing/failed
    TTL too short

High Redis GET for one key:
    hot key
    add local/CDN cache

Many keys miss together:
    avalanche
    add TTL jitter

Valid blocked link still redirects:
    stale cache layer
    invalidation missing

Lock wait high:
    DB slow
    lock TTL too long/short
    rebuild stuck
```

Useful Redis checks:

```bash
redis-cli TTL redirect:v1:sale2026
redis-cli GET redirect:v1:sale2026
redis-cli GET lock:redirect:sale2026
```

Golden rule:

```text
During cache incidents, debug by key, not only by endpoint.
```

---

## 28. Common Mistakes

### Mistake 1: Thinking Cache-Aside Alone Solves Hot Keys

Wrong:

```text
cache miss just loads DB
```

Correct:

```text
hot key miss needs stampede protection
```

### Mistake 2: No TTL Jitter

Wrong:

```text
all keys TTL = exactly 600 seconds
```

Correct:

```text
base TTL plus random jitter
```

### Mistake 3: Lock Without Expiry

Wrong:

```text
set lock forever
```

Correct:

```text
SET NX with TTL
```

### Mistake 4: Local Cache Too Long

Wrong:

```text
local cache TTL = 10 minutes
```

Correct:

```text
local cache TTL = 1-5 seconds for hot redirect
```

### Mistake 5: Ignoring CDN

Wrong:

```text
all viral traffic must hit app
```

Correct:

```text
short edge/browser caching where safe
```

### Mistake 6: No Per-Key Metrics

Wrong:

```text
only total RPS measured
```

Correct:

```text
track top hot shortCodes / keys
```

### Mistake 7: Serving Stale Blocked Links Too Long

Wrong:

```text
all cache layers ignore block/delete invalidation
```

Correct:

```text
evict Redis/local/CDN where possible, keep TTL short
```

### Mistake 8: Letting All Lock Waiters Hit DB

Wrong:

```text
lock not acquired -> query DB anyway immediately
```

Correct:

```text
wait briefly, retry cache, then fallback carefully
```

---

## 29. Interview-Ready Explanation

If interviewer asks:

```text
How would you handle hot keys and cache breakdown in a URL shortener?
```

Strong answer:

```text
A URL shortener can have extreme hot keys because one viral short link may receive tens of
thousands of redirects per second. If the Redis key for that shortCode expires, many requests
can miss at the same time and all hit PostgreSQL, causing cache breakdown or stampede. I would
first use TTL jitter so many keys do not expire together. For a hot key miss, I would use a
mutex/Redis lock or single-flight so only one request rebuilds the cache from PostgreSQL while
others wait briefly and retry Redis or serve a bounded stale value. I would also add a tiny local
Caffeine cache in each Spring Boot pod to reduce Redis pressure for extremely hot keys, and use
short CDN/browser caching where safe. PostgreSQL remains source of truth, so cache layers must
respect expiry and block/delete invalidation. I would monitor top keys, Redis hit/miss, DB
fallback per key, Hikari pending, Redis latency, and redirect p99.
```

Senior version:

```text
The goal is to convert a hot key cache miss from N identical database queries into one database
query, while pushing as much legitimate hot traffic as possible to local or edge cache.
```

Why this is strong:

```text
1. Identifies hot key risk.
2. Explains cache breakdown.
3. Mentions TTL jitter.
4. Mentions lock/single-flight.
5. Mentions local cache.
6. Mentions CDN/browser cache.
7. Mentions stale-data risk.
8. Mentions per-key observability.
9. Keeps DB as source of truth.
```

---

## 30. Senior Engineer Checklist

Before calling hot-key protection production-shaped, confirm:

```text
[ ] Positive cache TTL has jitter
[ ] Expiry-aware TTL still respects expiresAt
[ ] Hot key miss uses lock or single-flight
[ ] Lock has TTL
[ ] Lock unlock is token-safe
[ ] Waiters retry Redis before DB fallback
[ ] Local cache is considered for very hot keys
[ ] Local cache TTL is tiny
[ ] Block/delete invalidates Redis and local cache where possible
[ ] CDN/browser short caching is considered
[ ] Known campaign links can be pre-warmed
[ ] DB fallback uses indexed short_code
[ ] Hikari protects DB concurrency
[ ] Per-key metrics exist
[ ] Redis shard/hot-key metrics exist
[ ] Load test includes hot key expiry
```

---

## 31. One-Page Cheat Sheet

```text
Hot key:
one cache key gets massive traffic

Cache breakdown:
hot key expires
many requests miss
many DB queries happen

Cache penetration:
non-existing random keys hit DB

Cache avalanche:
many keys expire/fail together

Main protections:
TTL jitter
Redis mutex lock
single-flight
local cache
CDN/browser cache
stale-while-revalidate
pre-warming
hot key replication

Bad:
5000 misses -> 5000 DB queries

Good:
5000 misses -> 1 DB query + 4999 wait/retry cache

Redis lock:
SET lock:key token NX PX 3000

Local cache:
1-5 sec TTL
protects Redis from hot key traffic

CDN:
short max-age for safe redirects

Metrics:
top shortCodes
Redis hit/miss
DB fallback per key
lock acquired/wait
Hikari pending
Redis latency
redirect p99
```

---

## 32. One Picture To Remember

```text
              CACHE BREAKDOWN & HOT KEY MENTAL MODEL

                         "One rebuild, many wait"

HOT KEY:
redirect:v1:sale2026

Traffic:
5000 requests arrive after cache expiry


BAD FLOW:

5000 requests
      |
      v
Redis MISS
      |
      v
5000 DB queries
      |
      v
PostgreSQL/Hikari pain


GOOD FLOW:

5000 requests
      |
      v
Redis MISS
      |
      v
Try rebuild lock
      |
      +-- 1 request gets lock
      |       |
      |       v
      |     DB lookup
      |       |
      |       v
      |     Redis SET
      |
      +-- 4999 requests wait briefly
              |
              v
          retry Redis
              |
              v
             HIT


EXTRA SHIELDS:

CDN cache
local cache
TTL jitter
pre-warming
hot-key metrics


FINAL MEMORY:

A hot key is not just a popular key.
It is a concentration of risk.
Do not let a hot key miss become a database storm.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. A hot key is one cache key receiving disproportionate traffic, such as a viral short URL.
2. Cache breakdown happens when that hot key expires and many requests hit DB at the same time.
3. The main goal is to make only one request rebuild the missing hot key while others wait, retry cache, or use stale/local/CDN cache.
4. TTL jitter prevents synchronized expiry, while local cache and CDN reduce Redis pressure.
5. Production readiness requires per-key metrics, lock safety, stale-data controls, DB fallback protection, and load tests that expire hot keys during high traffic.
```

Next chapter:

```text
029_CDN_And_Browser_Caching.md
```
