# 021_Redis_Cache_For_Redirect.md
# MiniURLShortener — Redis Cache For Redirect

> Core mental model: **Redis is the fast memory layer in front of PostgreSQL. For redirect traffic, PostgreSQL owns the truth, but Redis answers repeated `shortCode -> redirect data` lookups quickly so the database is not hit for every click.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Why Redirect Is Perfect For Redis](#4-why-redirect-is-perfect-for-redis)
- [5. PostgreSQL vs Redis Role](#5-postgresql-vs-redis-role)
- [6. Redirect Cache Data Model](#6-redirect-cache-data-model)
- [7. Redis Key Design](#7-redis-key-design)
- [8. Redis Value Design](#8-redis-value-design)
- [9. Cache Hit Flow](#9-cache-hit-flow)
- [10. Cache Miss Flow](#10-cache-miss-flow)
- [11. TTL Strategy](#11-ttl-strategy)
- [12. Expiry-Aware TTL](#12-expiry-aware-ttl)
- [13. Negative Caching](#13-negative-caching)
- [14. Cache Invalidation](#14-cache-invalidation)
- [15. Write-Through On Create](#15-write-through-on-create)
- [16. Redis Failure Strategy](#16-redis-failure-strategy)
- [17. Hot Key Problem](#17-hot-key-problem)
- [18. Serialization Strategy](#18-serialization-strategy)
- [19. Spring Boot Redis Configuration](#19-spring-boot-redis-configuration)
- [20. Java Implementation](#20-java-implementation)
- [21. Redirect Service With Redis](#21-redirect-service-with-redis)
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

MiniURLShortener has a public redirect API:

```http
GET /{shortCode}
```

Example:

```http
GET /abc123
```

Expected response:

```http
302 Found
Location: https://example.com/article
```

The first simple implementation usually does this:

```text
Every redirect request -> PostgreSQL lookup
```

That works at small scale.

But URL shortener traffic is read-heavy.

One short link can be clicked many times:

```text
1 create request
10,000 redirect requests
```

If every redirect hits PostgreSQL:

```text
10,000 clicks = 10,000 DB reads
```

With Redis:

```text
first click may hit DB
next repeated clicks hit Redis
```

ASCII:

```text
Without Redis:

Client -> Spring Boot -> PostgreSQL
Client -> Spring Boot -> PostgreSQL
Client -> Spring Boot -> PostgreSQL


With Redis:

Client -> Spring Boot -> Redis HIT -> redirect
Client -> Spring Boot -> Redis HIT -> redirect
Client -> Spring Boot -> Redis MISS -> DB -> fill Redis -> redirect
```

Redis exists here to reduce repeated database reads and lower redirect latency.

Production memory:

```text
A redirect lookup is a key-value lookup.
Redis is excellent at key-value lookups.
```

---

## 2. The One Core Mental Model

The core mental model:

```text
REDIS IS A FAST COPY, NOT THE SOURCE OF TRUTH
```

PostgreSQL is authoritative:

```text
shortCode exists?
longUrl?
status?
expiresAt?
owner?
createdAt?
```

Redis is an acceleration layer:

```text
shortCode -> redirect data
```

ASCII:

```text
                    SOURCE OF TRUTH

                 +------------------+
                 | PostgreSQL       |
                 | durable truth    |
                 +---------+--------+
                           ^
                           |
                    refill on miss
                           |
+--------+      +----------+---------+
| Client | ---> | Redis fast copy    |
+--------+      | shortCode -> data  |
                +--------------------+
```

One-line memory:

```text
PostgreSQL decides truth; Redis avoids repeated truth-checking.
```

Correct Redis mindset:

```text
If Redis is empty, app must still work.
If Redis has stale data, TTL/invalidation must limit damage.
If Redis is down, app may be slower but should not fully die for reads.
```

Wrong mindset:

```text
Redis is my only storage.
```

For redirect metadata, Redis is a cache.

---

## 3. Problem Statement

Build Redis cache support for MiniURLShortener redirect API.

The cache must support:

```text
1. Fast lookup by shortCode.
2. Cache hit -> no DB query.
3. Cache miss -> DB query -> cache fill.
4. TTL for cached active links.
5. TTL should not exceed link expiry.
6. Cache invalidation on block/delete/update.
7. Optional negative caching for missing codes.
8. Safe fallback when Redis is unavailable.
9. Metrics for cache hit/miss/error.
10. Simple Spring Boot implementation.
```

Input:

```http
GET /abc123
```

Needed data:

```text
longUrl
status
expiresAt
```

Output:

```text
302 for ACTIVE and not expired
403 for BLOCKED
404 for missing/deleted
410 for expired
```

Non-goals for this chapter:

```text
Redis Cluster deep dive
Lua scripts
distributed locks
rate limiter
click analytics
multi-region cache
```

This chapter focuses only on Redis for redirect lookup.

---

## 4. Why Redirect Is Perfect For Redis

Redis is best when data access looks like:

```text
known key -> small value
```

Redirect lookup is exactly this:

```text
shortCode -> redirect metadata
```

Example:

```text
abc123 -> https://example.com/article
```

The data is:

```text
small
frequently repeated
safe to regenerate from DB
read-heavy
key-addressable
```

ASCII:

```text
Request:
    GET /abc123

Cache key:
    redirect:abc123

Cache value:
    {
      longUrl,
      status,
      expiresAt
    }
```

This is better than querying DB repeatedly:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = 'abc123';
```

Redis helps because:

```text
1. In-memory access is fast.
2. It supports TTL.
3. It reduces DB load.
4. It handles high read QPS.
5. It naturally fits key-value mapping.
```

But Redis is not magic.

You still need:

```text
good key design
good TTL
good invalidation
fallback strategy
metrics
```

---

## 5. PostgreSQL vs Redis Role

PostgreSQL role:

```text
durable source of truth
transactions
constraints
unique short_code
recovery
auditable state
```

Redis role:

```text
fast repeated reads
temporary cached copy
TTL-based expiry
hot-path acceleration
```

Comparison:

```text
+----------------------+----------------------+----------------------+
| Concern              | PostgreSQL           | Redis                |
+----------------------+----------------------+----------------------+
| Durability           | strong               | cache, can be lost   |
| Lookup speed         | good with index      | very fast            |
| Data model           | relational           | key-value            |
| Constraints          | yes                  | no real relational   |
| Transactions         | strong DB tx         | limited for cache    |
| TTL                  | manual logic         | built-in             |
| Use in redirect      | source of truth      | fast read copy       |
+----------------------+----------------------+----------------------+
```

ASCII:

```text
CREATE API:
    must write PostgreSQL

REDIRECT API:
    should read Redis first
    fallback to PostgreSQL
```

Rule:

```text
Never allow Redis-only data that cannot be rebuilt.
```

If Redis flushes:

```text
System should become slower, not incorrect forever.
```

---

## 6. Redirect Cache Data Model

The redirect cache should store exactly what redirect path needs.

Minimum fields:

```text
longUrl
status
expiresAt
```

Why not only longUrl?

Because redirect decision needs status and expiry.

Example:

```text
If cache stores only longUrl:
    cannot know if link is blocked
    cannot know if link expired
```

Better:

```json
{
  "longUrl": "https://example.com/article",
  "status": "ACTIVE",
  "expiresAt": "2026-12-31T00:00:00Z"
}
```

ASCII:

```text
redirect:abc123
      |
      v
+------------------------------+
| longUrl   = https://...      |
| status    = ACTIVE           |
| expiresAt = 2026-12-31...    |
+------------------------------+
```

Optional fields later:

```text
ownerId
createdAt
campaignId
safetyLevel
redirectType
```

But avoid making cache value too large.

Hot path principle:

```text
Store only what is needed to redirect safely.
```

---

## 7. Redis Key Design

Good cache key:

```text
redirect:{shortCode}
```

Example:

```text
redirect:abc123
redirect:sale2026
redirect:mohamed
```

Why prefix?

```text
namespacing
debuggability
avoids collisions with other Redis use cases
easy deletion by pattern in controlled tools
```

Bad keys:

```text
abc123
url
link
```

Because they are ambiguous.

ASCII:

```text
Redis key namespace:

redirect:abc123      -> redirect metadata
notfound:abc123      -> optional negative cache
ratelimit:user:42    -> later rate limiting
session:xyz          -> not this feature
```

For Redis Cluster, key distribution matters.

Basic version:

```text
redirect:{shortCode}
```

Cluster hash-tag version if needed:

```text
redirect:{abc123}
```

But for now:

```text
redirect:abc123
```

is enough.

---

## 8. Redis Value Design

There are multiple ways to store value.

Option 1: JSON string

```json
{"longUrl":"https://example.com","status":"ACTIVE","expiresAt":null}
```

Pros:

```text
easy to inspect
language independent
good for debugging
```

Cons:

```text
serialization overhead
careful versioning needed
```

Option 2: Redis hash

```text
HSET redirect:abc123 longUrl https://example.com status ACTIVE expiresAt ""
```

Pros:

```text
field-level visibility
can update field
```

Cons:

```text
slightly more verbose code
TTL still applies to whole key
```

Option 3: Java binary serialization

Pros:

```text
easy with default templates
```

Cons:

```text
hard to inspect
Java-version coupling
bad for cross-language
```

Recommended learning approach:

```text
JSON value with explicit serializer.
```

ASCII:

```text
Redis key:
    redirect:abc123

Redis value:
    JSON document
```

Senior rule:

```text
Prefer cache values that humans can inspect during production incidents.
```

---

## 9. Cache Hit Flow

Cache hit is the ideal fast path.

Flow:

```text
1. Request GET /abc123.
2. App builds key redirect:abc123.
3. Redis GET returns value.
4. App checks status and expiry.
5. App returns 302 Location.
6. PostgreSQL is not touched.
```

ASCII:

```text
GET /abc123
   |
   v
+----------------+
| Spring Boot    |
+--------+-------+
         |
         v
+----------------+
| Redis GET      |
| redirect:abc123|
+--------+-------+
         |
       HIT
         |
         v
+----------------+
| Check status   |
| Check expiry   |
+--------+-------+
         |
         v
+----------------+
| 302 Location   |
+----------------+
```

Why still check expiry?

Because cached data may include `expiresAt`.

Example:

```text
Redis key TTL might be wrong due to bug.
Check expiresAt defensively.
```

Cache hit should not do:

```text
DB lookup
analytics DB write
external API call
heavy logs
large object fetch
```

Hot path must stay small.

---

## 10. Cache Miss Flow

Cache miss means Redis does not have data.

Flow:

```text
1. Redis GET returns null.
2. App queries PostgreSQL by shortCode.
3. If DB row exists, app checks status/expiry.
4. App writes cache with TTL.
5. App returns redirect or error.
```

ASCII:

```text
GET /abc123
   |
   v
Redis GET redirect:abc123
   |
 MISS
   |
   v
PostgreSQL SELECT by short_code
   |
   +-- row found -> validate -> Redis SET -> redirect
   |
   +-- no row ----> optional negative cache -> 404
```

Cache miss is not failure.

It is normal.

Reasons for miss:

```text
first request after create
TTL expired
Redis restarted
cache evicted under memory pressure
manual invalidation
new deployment with empty cache
```

Important metric:

```text
cache miss rate
```

If miss rate suddenly spikes:

```text
Redis flush
bad TTL
key naming bug
traffic pattern changed
cache serialization failure
```

---

## 11. TTL Strategy

TTL controls how long data lives in Redis.

Without TTL:

```text
stale cache can live forever
Redis memory grows forever
blocked/deleted links may remain active in cache
```

With TTL:

```text
cache eventually refreshes from DB
memory is bounded
stale data window is limited
```

Basic TTL:

```text
redirect cache TTL = 10 minutes
```

ASCII:

```text
SET redirect:abc123 value EX 600

Now:
    10:00

Expires:
    10:10
```

TTL tradeoff:

```text
Long TTL:
    higher cache hit rate
    lower DB load
    higher stale-data risk

Short TTL:
    lower stale-data risk
    more DB fallback
    lower hit rate
```

Starting point:

```text
positive redirect TTL: 5-15 minutes
negative cache TTL: 15-60 seconds
```

Tune using metrics:

```text
cache hit rate
DB fallback QPS
stale redirect incidents
Redis memory
```

---

## 12. Expiry-Aware TTL

If a short URL expires soon, cache must not outlive the URL.

Example:

```text
current time = 10:00
link expires = 10:05
default cache TTL = 60 minutes
```

Wrong:

```text
cache until 11:00
```

Correct:

```text
cache until 10:05
```

Formula:

```text
ttl = min(defaultTtl, expiresAt - now)
```

ASCII:

```text
Timeline:

10:00 now
  |
  |---- 5 minutes ----|
                     10:05 link expires
  |
  |------------------ 60 minutes ------------------|
                                                    11:00 default TTL

Correct cache TTL:
  |---- 5 minutes ----|
```

If `expiresAt` is null:

```text
ttl = defaultTtl
```

If already expired:

```text
do not cache active redirect
return 410
optional short expired marker
```

Why this matters:

```text
A stale cache must not redirect an expired link.
```

---

## 13. Negative Caching

Negative caching stores a short-lived failure result.

Example:

```text
GET /random999
```

DB has no row.

Without negative caching:

```text
Every request hits DB.
```

With negative caching:

```text
First request hits DB.
Next requests return 404 from Redis for short TTL.
```

ASCII:

```text
GET /unknown
   |
   v
Redis GET redirect:unknown -> miss
   |
   v
DB SELECT -> no row
   |
   v
Redis SET redirect-miss:unknown NOT_FOUND TTL 30s
   |
   v
404

Next:
GET /unknown -> Redis says NOT_FOUND -> 404
```

Key design:

```text
redirect:notfound:{shortCode}
```

or same key with marker:

```json
{
  "result": "NOT_FOUND"
}
```

Simple approach:

```text
separate negative key
```

Risk:

```text
User creates custom alias while negative cache exists.
```

Fix:

```text
delete negative key on create
keep negative TTL short
```

Recommended:

```text
negative TTL = 30 seconds
```

Negative caching protects DB from:

```text
bots
random scans
broken clients
typos
```

---

## 14. Cache Invalidation

Cache invalidation means removing or updating stale cache when source of truth changes.

Events requiring invalidation:

```text
short URL updated
short URL blocked
short URL deleted
longUrl changed
expiry changed
status changed
```

ASCII:

```text
Admin blocks abc123

PostgreSQL:
    status = BLOCKED
        |
        v
Redis:
    delete redirect:abc123
        |
        v
Next read:
    DB sees BLOCKED -> 403
```

Methods:

```text
1. Delete cache key.
2. Update cache value.
3. Publish invalidation event later.
4. Use short TTL as backup.
```

For MiniURLShortener:

```text
On block/delete/update:
    update DB transaction
    after success, evict Redis key
```

Important:

```text
Do not evict before DB commit if transaction might rollback.
```

Safer pattern:

```text
DB update succeeds
then evict cache
```

If eviction fails:

```text
stale cache remains until TTL
```

Mitigation:

```text
short TTL
retry eviction
admin block path may use stronger handling later
```

---

## 15. Write-Through On Create

Write-through means writing cache during create.

Create flow:

```text
1. Validate request.
2. Insert row in PostgreSQL.
3. Put redirect metadata into Redis.
4. Return response.
```

ASCII:

```text
POST /api/v1/urls
   |
   v
DB INSERT
   |
   v
Redis SET redirect:abc123
   |
   v
201 Created
```

Why useful?

```text
New link can redirect immediately from Redis.
Avoids first-click DB miss.
Helps with read replica lag.
```

But beware:

```text
If Redis write fails, should create fail?
```

Usually no.

Because PostgreSQL write succeeded.

Option:

```text
DB success + Redis failure = return created, log/cache metric failure
```

Reason:

```text
Redis is cache, not source of truth.
```

Next redirect can fallback to DB.

Senior rule:

```text
Never make optional cache write break critical DB write unless product explicitly requires it.
```

---

## 16. Redis Failure Strategy

Redis can fail.

Examples:

```text
Redis down
network timeout
serialization error
connection pool exhausted
cluster failover
```

What should redirect do?

Option A:

```text
Fail request if Redis fails.
```

Bad for availability.

Option B:

```text
Fallback to PostgreSQL if Redis fails.
```

Better.

ASCII:

```text
GET /abc123
   |
   v
Redis GET
   |
   +-- success -> normal cache logic
   |
   +-- failure -> log metric -> DB fallback
```

This is called graceful degradation.

System becomes:

```text
slower
more DB load
still functional
```

But if Redis is down and traffic is huge:

```text
DB may overload
```

Mitigations:

```text
timeouts
circuit breaker later
rate limiting
fallback limits
alerts
short operational runbook
```

Redis timeout should be small:

```text
example: 50-100 ms
```

Do not let Redis hang the redirect path.

Principle:

```text
Cache failure should not become worse than no cache.
```

---

## 17. Hot Key Problem

A hot key is one key receiving disproportionate traffic.

Example:

```text
redirect:worldcup2026
```

Traffic:

```text
50,000 RPS to same key
```

ASCII:

```text
Thousands of clients
        |
        v
same Redis key
redirect:worldcup2026
        |
        v
one Redis shard/node under pressure
```

Symptoms:

```text
Redis CPU spike
Redis p99 GET latency high
single shard hot
app threads waiting
redirect p99 increases
```

Mitigations:

```text
1. CDN cache for hot links.
2. Browser cache with short max-age.
3. Local in-memory cache in app pod with tiny TTL.
4. Pre-warm Redis for campaigns.
5. Scale Redis / cluster.
6. Rate limit abusive traffic.
```

Local cache idea:

```text
Caffeine cache inside each Spring Boot pod
TTL = 1-5 seconds
```

ASCII:

```text
Spring Boot Pod
   |
   v
Local cache HIT -> redirect
   |
   v
Redis only on local miss
```

Risk:

```text
very short stale window
```

For security-blocked links:

```text
evict local + Redis if possible
keep TTL tiny
```

---

## 18. Serialization Strategy

Spring Redis must serialize keys and values.

Bad default sometimes:

```text
Java binary serialization
```

Problems:

```text
hard to debug
not readable in redis-cli
coupled to Java class structure
larger payloads
cross-language unfriendly
```

Better:

```text
String keys
JSON values
```

ASCII:

```text
Key serializer:
    StringRedisSerializer

Value serializer:
    GenericJackson2JsonRedisSerializer
    or Jackson2JsonRedisSerializer<RedirectCacheValue>
```

Readable Redis:

```bash
GET redirect:abc123
```

Output:

```json
{"longUrl":"https://example.com","status":"ACTIVE","expiresAt":null}
```

Versioning concern:

```text
If you change cache value fields, old cached JSON may still exist.
```

Solutions:

```text
short TTL
backward-compatible fields
cache version prefix
```

Versioned key example:

```text
v1:redirect:abc123
```

If cache shape changes:

```text
v2:redirect:abc123
```

For MiniURLShortener, use:

```text
redirect:v1:{shortCode}
```

This is clean and production-friendly.

---

## 19. Spring Boot Redis Configuration

Add dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

Application config:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 100ms

app:
  redirect-cache:
    positive-ttl: 10m
    negative-ttl: 30s
```

Docker Compose example:

```yaml
services:
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
```

RedisTemplate config:

```java
package com.miniurl.shortener.config;

import com.miniurl.shortener.url.cache.RedirectCacheValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, RedirectCacheValue> redirectRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, RedirectCacheValue> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<RedirectCacheValue> valueSerializer =
                new Jackson2JsonRedisSerializer<>(RedirectCacheValue.class);

        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashKeySerializer(keySerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();

        return template;
    }
}
```

This gives:

```text
String keys
JSON-ish values
typed RedisTemplate
```

---

## 20. Java Implementation

### Cache Properties

```java
package com.miniurl.shortener.url.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.redirect-cache")
public class RedirectCacheProperties {

    private Duration positiveTtl = Duration.ofMinutes(10);
    private Duration negativeTtl = Duration.ofSeconds(30);

    public Duration getPositiveTtl() {
        return positiveTtl;
    }

    public void setPositiveTtl(Duration positiveTtl) {
        this.positiveTtl = positiveTtl;
    }

    public Duration getNegativeTtl() {
        return negativeTtl;
    }

    public void setNegativeTtl(Duration negativeTtl) {
        this.negativeTtl = negativeTtl;
    }
}
```

Enable properties:

```java
package com.miniurl.shortener;

import com.miniurl.shortener.url.cache.RedirectCacheProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RedirectCacheProperties.class)
public class MiniUrlShortenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniUrlShortenerApplication.class, args);
    }
}
```

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedirectCacheService {

    private static final Logger log = LoggerFactory.getLogger(RedirectCacheService.class);

    private final RedisTemplate<String, RedirectCacheValue> redisTemplate;

    public RedirectCacheService(RedisTemplate<String, RedirectCacheValue> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Optional<RedirectCacheValue> get(String shortCode) {
        try {
            RedirectCacheValue value = redisTemplate.opsForValue().get(positiveKey(shortCode));
            return Optional.ofNullable(value);
        } catch (Exception ex) {
            log.warn("Redis redirect cache get failed for shortCode={}", shortCode, ex);
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
            log.warn("Redis redirect cache put failed for shortCode={}", shortCode, ex);
        }
    }

    public void evict(String shortCode) {
        try {
            redisTemplate.delete(positiveKey(shortCode));
        } catch (Exception ex) {
            log.warn("Redis redirect cache evict failed for shortCode={}", shortCode, ex);
        }
    }

    private String positiveKey(String shortCode) {
        return "redirect:v1:" + shortCode;
    }
}
```

Important:

```text
Cache get failure returns empty.
That lets service fallback to DB.
```

---

## 21. Redirect Service With Redis

Example redirect service:

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.url.cache.*;
import com.miniurl.shortener.url.entity.ShortUrl;
import com.miniurl.shortener.url.entity.ShortUrlStatus;
import com.miniurl.shortener.url.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class RedirectService {

    private final ShortUrlRepository repository;
    private final RedirectCacheService cacheService;
    private final RedirectCacheProperties cacheProperties;

    public RedirectService(
            ShortUrlRepository repository,
            RedirectCacheService cacheService,
            RedirectCacheProperties cacheProperties
    ) {
        this.repository = repository;
        this.cacheService = cacheService;
        this.cacheProperties = cacheProperties;
    }

    public String resolveRedirect(String shortCode) {
        return cacheService.get(shortCode)
                .map(value -> resolveFromCache(shortCode, value))
                .orElseGet(() -> resolveFromDatabase(shortCode));
    }

    private String resolveFromCache(String shortCode, RedirectCacheValue value) {
        validateRedirectState(shortCode, value.getStatus(), value.getExpiresAt());
        return value.getLongUrl();
    }

    private String resolveFromDatabase(String shortCode) {
        ShortUrl entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));

        validateRedirectState(shortCode, entity.getStatus().name(), entity.getExpiresAt());

        RedirectCacheValue cacheValue = new RedirectCacheValue(
                entity.getLongUrl(),
                entity.getStatus().name(),
                entity.getExpiresAt()
        );

        cacheService.put(shortCode, cacheValue, calculateTtl(entity.getExpiresAt()));

        return entity.getLongUrl();
    }

    private void validateRedirectState(String shortCode, String status, Instant expiresAt) {
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
        Duration defaultTtl = cacheProperties.getPositiveTtl();

        if (expiresAt == null) {
            return defaultTtl;
        }

        Duration untilExpiry = Duration.between(Instant.now(), expiresAt);

        if (untilExpiry.isNegative() || untilExpiry.isZero()) {
            return Duration.ZERO;
        }

        return untilExpiry.compareTo(defaultTtl) < 0 ? untilExpiry : defaultTtl;
    }
}
```

Flow:

```text
Redis hit:
    return from cache

Redis miss:
    DB lookup
    cache fill
    return

Redis failure:
    cacheService returns Optional.empty()
    DB fallback
```

This is the production-shaped behavior.

---

## 22. Step-by-Step Dry Runs

### Dry Run 1: Redis Hit

Redis contains:

```text
redirect:v1:abc123
{
  "longUrl": "https://example.com/article",
  "status": "ACTIVE",
  "expiresAt": null
}
```

Request:

```http
GET /abc123
```

Execution:

```text
1. Controller receives shortCode abc123.
2. Service calls cacheService.get("abc123").
3. Redis returns cached value.
4. Service checks status ACTIVE.
5. expiresAt is null.
6. Service returns longUrl.
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
status ACTIVE
   |
   v
302 Location
```

---

### Dry Run 2: Redis Miss, DB Hit

Redis:

```text
no key
```

PostgreSQL:

```text
short_code = abc123
long_url = https://example.com/article
status = ACTIVE
expires_at = null
```

Execution:

```text
1. Redis returns null.
2. Service queries repository.
3. PostgreSQL returns entity.
4. Service validates status and expiry.
5. Service calculates TTL = 10 minutes.
6. Service writes Redis key.
7. Service returns longUrl.
8. Controller returns 302.
```

ASCII:

```text
Redis MISS
   |
   v
DB SELECT
   |
   v
Redis SET EX 600
   |
   v
302
```

---

### Dry Run 3: Expiring Link

Current time:

```text
10:00
```

Link expires:

```text
10:03
```

Default TTL:

```text
10 minutes
```

Execution:

```text
1. Redis miss.
2. DB returns active row.
3. Service calculates untilExpiry = 3 minutes.
4. TTL = min(10 minutes, 3 minutes).
5. Redis key expires at 10:03.
6. Redirect succeeds.
```

ASCII:

```text
default TTL 10m
expiry in 3m

cache TTL = 3m
```

---

### Dry Run 4: Redis Down

Redis:

```text
connection timeout
```

DB:

```text
row exists
```

Execution:

```text
1. cacheService.get throws Redis exception.
2. cacheService catches and logs warning.
3. cacheService returns Optional.empty().
4. RedirectService falls back to DB.
5. DB returns row.
6. cacheService.put may also fail but is caught.
7. Redirect returns 302.
```

Result:

```text
system is slower but available
```

---

### Dry Run 5: Blocked Link In Cache

Redis value:

```json
{
  "longUrl": "https://bad.com",
  "status": "BLOCKED",
  "expiresAt": null
}
```

Execution:

```text
1. Redis returns cached value.
2. Service sees status BLOCKED.
3. Service throws ShortCodeBlockedException.
4. GlobalExceptionHandler returns 403.
5. longUrl is not returned to client.
```

---

## 23. Internal Execution Walkthrough

Full redirect request:

```text
1. HTTP request enters Tomcat.
2. DispatcherServlet maps GET /{shortCode}.
3. RedirectController extracts shortCode.
4. RedirectService builds Redis key.
5. RedisTemplate sends GET command.
6. Redis returns cached JSON or null.
7. If cached, Jackson deserializes to RedirectCacheValue.
8. Service validates status/expiry.
9. If valid, controller returns 302.
10. If miss, repository queries PostgreSQL.
11. Entity is mapped to RedirectCacheValue.
12. RedisTemplate sends SET with TTL.
13. Controller returns 302.
```

ASCII:

```text
Client
  |
  v
Controller
  |
  v
RedirectService
  |
  v
RedisTemplate
  |
  +-- HIT -> validate -> 302
  |
  +-- MISS -> Repository -> PostgreSQL -> Redis SET -> 302
```

Important:

```text
The controller does not know whether Redis or DB was used.
The service hides read optimization details.
```

This preserves clean architecture.

---

## 24. Testing Strategy

Test types:

```text
unit tests
integration tests
manual Redis tests
failure/fallback tests
```

### Unit Tests

Mock Redis cache service and repository.

Test:

```text
cache hit returns longUrl without repository call
cache miss calls repository
expired cached value throws 410
blocked cached value throws 403
```

Example:

```java
verify(repository, never()).findByShortCode("abc123");
```

### Integration Tests

Use Testcontainers:

```text
PostgreSQL container
Redis container
Spring Boot context
MockMvc
```

Flow tests:

```text
first redirect hits DB and fills Redis
second redirect uses Redis
blocked link returns 403
expired link returns 410
```

### Redis Failure Test

Mock cache service to throw or return empty.

Expected:

```text
fallback to DB
redirect still succeeds
```

### TTL Test

For expiry-aware TTL:

```text
link expires in 60 seconds
cache TTL should not exceed 60 seconds
```

Testing rule:

```text
Do not only test happy cache hit.
Test miss, stale, failure, invalidation.
```

---

## 25. Metrics And Observability

You cannot operate cache blindly.

Metrics:

```text
redirect.cache.hit.count
redirect.cache.miss.count
redirect.cache.error.count
redirect.cache.put.count
redirect.cache.evict.count
redirect.db.fallback.count
redirect.latency.p95
redirect.latency.p99
redis.command.latency
redis.connection.errors
```

ASCII:

```text
Redirect request
   |
   +-- cache hit metric
   |
   +-- cache miss metric
   |
   +-- cache error metric
   |
   +-- DB fallback metric
```

Important ratios:

```text
cache hit rate = hits / (hits + misses)
DB fallback rate = DB lookups / redirect requests
```

Example:

```text
redirect requests = 10,000/sec
cache hit rate = 98%
DB fallback = 200/sec
```

Alerts:

```text
cache hit rate drops suddenly
Redis error rate increases
DB fallback spikes
redirect p99 increases
Redis memory near max
Redis evictions increase
```

Operational rule:

```text
Cache problems first appear as DB load or p99 latency.
```

---

## 26. Production Failure Stories

### Failure Story 1: Redis Key Bug Caused 0% Hit Rate

Code wrote:

```text
redirect:v1:abc123
```

But read used:

```text
redirect:abc123
```

Result:

```text
Every request missed cache.
DB load spiked.
```

Fix:

```text
Centralize key generation in one method.
Add cache hit-rate metric.
Add integration test for second request cache hit.
```

Lesson:

```text
Key naming must not be duplicated across code.
```

---

### Failure Story 2: Expired Link Redirected From Cache

A link expired at 10:05.

Cache TTL was 1 hour.

At 10:30 users still redirected.

Root cause:

```text
TTL did not respect expiresAt.
```

Fix:

```text
TTL = min(defaultTtl, timeUntilExpiry).
Also check expiresAt after cache hit.
```

Lesson:

```text
Cache TTL must respect domain lifecycle.
```

---

### Failure Story 3: Redis Down Took Down Redirect API

Redis timed out for 2 seconds.

App waited on every redirect.

Threads saturated.

Root cause:

```text
No short timeout and no DB fallback strategy.
```

Fix:

```text
Set Redis command timeout.
Catch cache exceptions.
Fallback to DB.
Add alert.
```

Lesson:

```text
A cache must not become a hard dependency unless designed as one.
```

---

### Failure Story 4: Blocked Link Stayed Active

Admin blocked a malicious URL in DB.

Redis still had ACTIVE value.

Users kept redirecting until TTL expired.

Root cause:

```text
No cache eviction on block.
```

Fix:

```text
Evict redirect cache after successful block/delete/update.
Use shorter TTL for risky links.
```

Lesson:

```text
Critical state changes need invalidation, not TTL-only thinking.
```

---

### Failure Story 5: Bot Random Codes Overloaded DB

Bots generated millions of unknown short codes.

Each miss queried DB.

Root cause:

```text
No negative caching and no rate limiting.
```

Fix:

```text
Short negative cache for NOT_FOUND.
Rate limiting later.
Bot detection later.
```

Lesson:

```text
Cache misses can be weaponized.
```

---

## 27. Debugging Mindset

When redirect cache behaves badly, ask:

```text
Is Redis reachable?
Are keys being written and read with same format?
Is serializer working?
Is TTL too short?
Is TTL too long?
Is cache hit rate healthy?
Are DB fallbacks increasing?
Are cache errors hidden in logs?
Are keys being evicted by Redis memory policy?
Is a hot key overloading Redis?
Is stale data due to missing invalidation?
```

Debug commands:

```bash
redis-cli GET redirect:v1:abc123
redis-cli TTL redirect:v1:abc123
redis-cli EXISTS redirect:v1:abc123
redis-cli INFO memory
redis-cli INFO stats
```

Debug map:

```text
Cache hit rate 0%:
    key mismatch
    serialization failure
    Redis connection wrong
    TTL too short

Stale redirect:
    TTL too long
    invalidation missing
    CDN/browser cache also involved

Redis high CPU:
    hot key
    too much traffic
    large values
    slow network/client issue

DB high CPU:
    Redis down
    cache miss spike
    negative cache missing
    key bug

302 for blocked link:
    stale ACTIVE cache
    block path did not evict
```

Golden rule:

```text
For every redirect incident, identify whether request came from cache hit, cache miss, or cache failure fallback.
```

---

## 28. Common Mistakes

### Mistake 1: Treating Redis As Source Of Truth

Wrong:

```text
If Redis has no key, link does not exist.
```

Correct:

```text
If Redis misses, query PostgreSQL.
```

### Mistake 2: Caching Only longUrl

Wrong:

```text
abc123 -> https://example.com
```

Correct:

```text
abc123 -> longUrl + status + expiresAt
```

### Mistake 3: No TTL

Wrong:

```text
Redis key lives forever.
```

Correct:

```text
Use positive TTL and expiry-aware TTL.
```

### Mistake 4: No Invalidation

Wrong:

```text
Block link in DB only.
```

Correct:

```text
Block link in DB, then evict Redis key.
```

### Mistake 5: Redis Failure Fails Redirect

Wrong:

```text
Redis timeout -> 500
```

Correct:

```text
Redis timeout -> DB fallback if possible
```

### Mistake 6: Java Binary Serialization

Wrong:

```text
Unreadable cache values.
```

Correct:

```text
String keys, JSON values.
```

### Mistake 7: No Negative Cache

Wrong:

```text
Every unknown shortCode hits DB.
```

Correct:

```text
Cache NOT_FOUND briefly.
```

### Mistake 8: No Metrics

Wrong:

```text
Cache added, no hit-rate metric.
```

Correct:

```text
Measure hits, misses, errors, DB fallback, p99.
```

---

## 29. Interview-Ready Explanation

If interviewer asks:

```text
How would you use Redis in a URL shortener?
```

Strong answer:

```text
The redirect API is read-heavy, so I would use Redis as a cache in front of PostgreSQL.
PostgreSQL remains the source of truth, but Redis stores a small redirect metadata object
keyed by shortCode, for example redirect:v1:abc123 -> longUrl, status, expiresAt. On a
redirect request, the service checks Redis first. If it is a hit, it validates status and
expiry and returns 302 without touching the database. If it is a miss, it queries PostgreSQL
by indexed short_code, fills Redis with an expiry-aware TTL, then returns the redirect. I
would evict cache on block/delete/update, use short negative caching for missing codes, and
fallback to DB if Redis is unavailable. I would monitor cache hit rate, Redis errors, DB
fallbacks, and redirect p99 latency.
```

Senior version:

```text
Redis is a read accelerator, not the truth. The design is cache-aside with bounded staleness:
PostgreSQL owns correctness, Redis reduces repeated reads, TTL limits stale data, and explicit
invalidation handles critical state changes like blocked or deleted links.
```

Why this is strong:

```text
1. Identifies read-heavy redirect path.
2. Separates Redis from source of truth.
3. Stores enough data for redirect decision.
4. Uses cache-aside.
5. Handles TTL and expiry.
6. Handles invalidation.
7. Handles Redis failure.
8. Mentions negative caching.
9. Mentions metrics and p99.
```

---

## 30. Senior Engineer Checklist

Before calling Redis redirect cache production-shaped, confirm:

```text
[ ] Redis is cache, PostgreSQL is truth
[ ] Key format is centralized
[ ] Key includes namespace and version
[ ] Cache value includes longUrl, status, expiresAt
[ ] Positive TTL exists
[ ] TTL respects expiresAt
[ ] Cache hit avoids DB
[ ] Cache miss queries DB and fills cache
[ ] Redis failure falls back to DB
[ ] Block/delete/update evicts cache
[ ] Create path optionally write-throughs cache
[ ] Negative cache exists for repeated not found
[ ] Serializer uses readable JSON
[ ] Redis timeout is short
[ ] Cache metrics exist
[ ] DB fallback metric exists
[ ] p99 redirect latency is tracked
[ ] Hot key strategy exists
[ ] Tests cover hit, miss, expired, blocked, Redis failure, invalidation
```

---

## 31. One-Page Cheat Sheet

```text
Core mental model:
Redis is a fast copy, not source of truth.

Source of truth:
PostgreSQL

Cache:
Redis

Key:
redirect:v1:{shortCode}

Value:
longUrl
status
expiresAt

Hit flow:
GET shortCode
Redis hit
validate status/expiry
302 redirect

Miss flow:
Redis miss
DB lookup
validate status/expiry
Redis SET with TTL
302 redirect

TTL:
positive TTL = 5-15 min start
negative TTL = 15-60 sec
expiry-aware TTL = min(default, expiresAt-now)

Invalidation:
block/delete/update -> evict Redis key

Failure:
Redis down -> log metric -> DB fallback

Must monitor:
cache hit rate
cache miss rate
cache error rate
DB fallback QPS
Redis latency
redirect p99

Common bugs:
key mismatch
no TTL
stale blocked link
Redis hard dependency
no negative cache
binary serialization
no metrics
```

---

## 32. One Picture To Remember

```text
                REDIS CACHE FOR REDIRECT MENTAL MODEL

                         "Fast copy before truth"

Client
  |
  v
GET /abc123
  |
  v
+-----------------------------+
| Spring Boot RedirectService |
+-------------+---------------+
              |
              v
+-----------------------------+
| Redis GET redirect:v1:abc123|
+------+----------------------+
       |
       +-- HIT ----------------------------------+
       |                                         |
       v                                         |
+-----------------------------+                  |
| Check status + expiresAt    |                  |
+-------------+---------------+                  |
              |                                  |
              v                                  |
        302 Location                             |
                                                  |
       +-- MISS ---------------------------------+
              |
              v
+-----------------------------+
| PostgreSQL SELECT           |
| WHERE short_code = ?        |
+-------------+---------------+
              |
              v
+-----------------------------+
| Redis SET with safe TTL     |
+-------------+---------------+
              |
              v
        302 Location


FINAL MEMORY:

PostgreSQL owns truth.
Redis owns speed.
TTL controls staleness.
Invalidation handles critical changes.
Fallback keeps system alive.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Redis is used because redirect lookup is a repeated key-value read.
2. PostgreSQL remains the source of truth; Redis is only a fast disposable copy.
3. Cache value must include longUrl, status, and expiresAt so redirect decisions are safe.
4. TTL must be expiry-aware, and block/delete/update must evict cache.
5. Redis failure should degrade to DB fallback, while metrics reveal hit rate, misses, errors, and p99 latency.
```

Next chapter:

```text
022_Cache_Aside_Pattern.md
```
