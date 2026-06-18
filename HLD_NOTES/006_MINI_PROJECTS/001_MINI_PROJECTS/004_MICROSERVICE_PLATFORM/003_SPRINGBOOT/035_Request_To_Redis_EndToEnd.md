# 035_Request_To_Redis_EndToEnd — The Fast-Path Cache Journey Model

## Core Mental Model

Do not imagine a Redis-backed request as:

```text
Controller calls Redis and response becomes fast.
```

That is too shallow.

The better mental model is:

> **A request-to-Redis flow is a fast-path side journey. The request checks Redis before expensive work, returns immediately on hit, or falls back to the source of truth on miss and refills Redis carefully.**

```text
HTTP Request
    |
    v
Controller
    |
    v
Service
    |
    v
Redis GET
    |
    +-- HIT  -> return response
    |
    +-- MISS -> DB/source of truth
                Redis SET with TTL
                return response
```

This chapter teaches exactly one idea:

> **Redis is not the request’s destination; Redis is a fast decision point in the request pipeline that either serves the response or redirects the request to the source of truth.**

If you remember only one sentence:

> **Request-to-Redis means: compute key, check cache, return on hit, rebuild from truth on miss, store with TTL, and protect the system when Redis or DB is under pressure.**

---

## Why This Exists

Many APIs repeatedly read the same data.

Examples:

```text
GET /api/products/1001
GET /api/users/42/profile
GET /api/catalog?page=1
GET /api/config/features
GET /api/restaurants/nearby?city=...
```

Without Redis:

```text
Every request goes to database.
Database handles repeated identical reads.
p99 latency rises.
DB CPU increases.
Connection pool pressure increases.
```

With Redis:

```text
Popular data lives in memory.
Repeated reads avoid database.
Latency drops.
Database load reduces.
```

But Redis adds a new runtime path.

A request can now fail or behave differently because of:

```text
Redis timeout
Redis miss
Redis stale value
wrong cache key
serialization error
expired TTL
cache stampede
cache penetration
Redis memory eviction
DB fallback overload
```

So you need an end-to-end request-to-Redis mental model.

---

## Problem Statement

A product endpoint:

```http
GET /api/products/1001
```

Business requirement:

```text
Return product card quickly.
Data can be stale for up to 5 minutes.
Database is source of truth.
Redis should reduce database load.
If Redis is down, endpoint should degrade safely.
```

The core problem:

> **How does one HTTP request interact with Redis, database fallback, TTL, serialization, and response building without creating stale-data or overload bugs?**

The answer is the request-to-Redis fast-path cache journey:

```text
1. Request enters controller.
2. Service builds deterministic cache key.
3. Service tries Redis GET.
4. If hit, deserialize and return.
5. If miss, load source of truth.
6. Map result to cacheable DTO.
7. Redis SET with TTL.
8. Return response.
9. On writes, invalidate affected keys.
10. On Redis failure, fallback or fail according to business risk.
```

---

## Real World Analogy

Imagine a busy restaurant.

The chef keeps popular ingredients on a side shelf.

```text
Need salt?
  Check side shelf.
  If found, use immediately.
  If missing, go to warehouse.
  Refill side shelf.
```

Mapping:

```text
Restaurant side shelf        Redis
Main warehouse               Database/source of truth
Chef                         Service layer
Ingredient label             Cache key
Ingredient packet            Cached DTO/value
Shelf expiry                 TTL
Warehouse trip               DB fallback
Stale packet                 stale cache value
```

Important:

> **The side shelf improves speed only if it is correctly labeled, refreshed, and not trusted as the source of truth.**

Redis is the side shelf.
Database is the warehouse.

---

## The One Mental Model

Request-to-Redis has two paths.

```text
Fast path:
  request -> Redis hit -> response

Slow path:
  request -> Redis miss -> DB -> Redis refill -> response
```

ASCII:

```text
GET /api/products/1001
        |
        v
ProductService
        |
        v
key = product:1001
        |
        v
Redis GET
        |
   +----+----+
   |         |
   v         v
 HIT       MISS
   |         |
   v         v
return     DB SELECT
cached       |
DTO          v
          map DTO
             |
             v
          Redis SET key TTL
             |
             v
          return DTO
```

The request is not always faster.
It is faster only when cache hit ratio is high and Redis is healthy.

---

## Core Concepts

## Cache Key

The key is the address of cached data.

Good key:

```text
product:1001
user:42:profile
catalog:tenant:t1:lang:en:currency:EUR:page:1:size:20
```

Bad key:

```text
product
catalog
userProfile
```

Bad keys cause collisions and wrong data.

Rule:

> **Every input that changes the response must be represented in the cache key.**

## Cache Value

The value is what Redis stores.

Usually cache DTOs, not JPA entities.

Good:

```java
public record ProductCard(Long id, String name, BigDecimal price) {}
```

Avoid caching:

```text
Hibernate entities
lazy proxies
huge object graphs
sensitive secrets
rapidly changing critical data
```

## TTL

TTL controls expiration.

```text
product:1001 -> expires after 5 minutes
```

TTL is a business decision.

```text
Can users see old product name for 5 minutes?
Can users see old permission for 5 minutes?
Can users see old price for 5 minutes?
```

Different data needs different TTL.

## Cache Hit

Redis has the key.

```text
low latency
no DB call
fast response
```

## Cache Miss

Redis does not have the key.

Reasons:

```text
first request
TTL expired
key evicted
Redis restarted
wrong key
invalidation happened
```

Miss goes to source of truth.

## Serialization

Java object becomes bytes/string in Redis.

Common:

```text
JSON
String
binary format
```

Serialization errors are production bugs.

DTO evolution matters.

## Redis Client

Spring Boot often uses:

```text
Lettuce
RedisTemplate
StringRedisTemplate
Spring Cache abstraction
```

This chapter focuses on the flow, not one client.

## Source of Truth

For cache-aside, source of truth is usually DB.

```text
Redis = fast copy
DB = truth
```

---

## Internal Architecture

Manual cache-aside:

```text
Controller
   |
   v
Service
   |
   +--> RedisTemplate
   |
   +--> Repository
          |
          v
       Database
```

Spring Cache abstraction:

```text
Controller
   |
   v
Service Proxy
   |
   +--> Cache interceptor
   |       |
   |       +--> Redis CacheManager
   |
   v
Real Service Method on miss
   |
   v
Repository/Database
```

Important difference:

```text
Manual RedisTemplate:
  your code explicitly checks Redis

@Cacheable:
  Spring proxy checks Redis before method execution
```

In both cases, mental model is the same:

```text
hit skips expensive work
miss performs expensive work and stores result
```

---

## Internal Working — Manual Redis Flow

Service:

```java
public ProductCard getProduct(Long id) {
    String key = "product:" + id;

    ProductCard cached = redis.get(key);
    if (cached != null) {
        return cached;
    }

    Product product = productRepository.findById(id).orElseThrow();
    ProductCard card = ProductCard.from(product);

    redis.set(key, card, Duration.ofMinutes(5));

    return card;
}
```

Runtime:

```text
1. Controller calls service.
2. Service computes product:1001.
3. Service sends GET product:1001 to Redis.
4. Redis returns value or null.
5. If value exists, deserialize to ProductCard.
6. Return response.
7. If null, query database.
8. Map entity to ProductCard.
9. Store ProductCard in Redis with TTL.
10. Return response.
```

---

## Internal Working — Spring Cache Flow

Service:

```java
@Cacheable(cacheNames = "products", key = "#id")
public ProductCard getProduct(Long id) {
    Product product = productRepository.findById(id).orElseThrow();
    return ProductCard.from(product);
}
```

Runtime:

```text
1. Caller invokes Spring proxy.
2. Cache interceptor builds key.
3. Cache interceptor asks Redis cache.
4. If hit, method body is skipped.
5. If miss, real method runs.
6. Method queries database.
7. Return value stored in Redis.
8. Value returned to caller.
```

Important:

```text
@Cacheable works through proxy.
Self-invocation can bypass cache.
```

---

## Rich ASCII Diagram — End-to-End Request to Redis

```text
Client
  |
  | GET /api/products/1001
  v
Tomcat
  |
  v
Filters / Security
  |
  v
DispatcherServlet
  |
  v
ProductController
  |
  v
ProductService
  |
  | build cache key
  v
+-----------------------+
| Redis                 |
| GET product:1001      |
+----------+------------+
           |
    +------+------+
    |             |
    v             v
  HIT           MISS
    |             |
    v             v
Deserialize    ProductRepository
DTO                 |
    |               v
    |            Database
    |               |
    |               v
    |            Product row
    |               |
    |               v
    |            Map DTO
    |               |
    |               v
    |            Redis SET TTL
    |               |
    +-------+-------+
            |
            v
       JSON Response
```

---

## Rich ASCII Diagram — Write Invalidation Path

```text
PATCH /api/products/1001/price
        |
        v
ProductCommandService
        |
        v
+--------------------------+
| Database transaction     |
| update product price     |
| commit truth             |
+------------+-------------+
             |
             v
+--------------------------+
| Redis invalidation       |
| DEL product:1001         |
+------------+-------------+
             |
             v
Next GET product:1001
        |
        v
Redis MISS -> DB fresh -> Redis refill
```

Rule:

> **Update truth first, then evict stale cache after successful commit.**

---

## Java/Spring Boot Code Example — Manual RedisTemplate

DTO:

```java
public record ProductCard(
        Long id,
        String name,
        BigDecimal price
) {
    public static ProductCard from(Product product) {
        return new ProductCard(
                product.getId(),
                product.getName(),
                product.getPrice()
        );
    }
}
```

Service:

```java
@Service
public class ProductCacheService {

    private final RedisTemplate<String, ProductCard> redisTemplate;
    private final ProductRepository productRepository;

    public ProductCacheService(RedisTemplate<String, ProductCard> redisTemplate,
                               ProductRepository productRepository) {
        this.redisTemplate = redisTemplate;
        this.productRepository = productRepository;
    }

    public ProductCard getProduct(Long id) {
        String key = productKey(id);

        ProductCard cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        ProductCard card = ProductCard.from(product);

        redisTemplate.opsForValue()
                .set(key, card, Duration.ofMinutes(5));

        return card;
    }

    private String productKey(Long id) {
        return "product:" + id;
    }
}
```

Controller:

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductCacheService productCacheService;

    public ProductController(ProductCacheService productCacheService) {
        this.productCacheService = productCacheService;
    }

    @GetMapping("/{id}")
    public ProductCard get(@PathVariable Long id) {
        return productCacheService.getProduct(id);
    }
}
```

Internal explanation:

```text
Controller does not know cache details.
Service owns cache-aside logic.
Redis hit avoids repository.
Redis miss uses repository and refills cache.
```

---

## Java/Spring Boot Code Example — Redis Serialization

Configuration:

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, ProductCard> productCardRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, ProductCard> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(ProductCard.class));

        template.afterPropertiesSet();
        return template;
    }
}
```

Production caution:

```text
Serialization format is part of cache contract.
Changing DTO fields may affect old cached values.
```

Strategies:

```text
short TTL
versioned cache keys
backward-compatible DTOs
cache flush on deploy if needed
```

Versioned key example:

```text
v1:product:1001
v2:product:1001
```

---

## Java/Spring Boot Code Example — Spring Cache

Config:

```java
@Configuration
@EnableCaching
public class CacheConfig {
}
```

Properties:

```properties
spring.cache.type=redis
spring.cache.redis.time-to-live=5m
spring.cache.redis.cache-null-values=false
```

Service:

```java
@Service
public class ProductQueryService {

    private final ProductRepository productRepository;

    public ProductQueryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable(cacheNames = "products", key = "#id")
    public ProductCard getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return ProductCard.from(product);
    }
}
```

Command service:

```java
@Service
public class ProductCommandService {

    private final ProductRepository productRepository;

    public ProductCommandService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @CacheEvict(cacheNames = "products", key = "#id")
    public void changePrice(Long id, BigDecimal newPrice) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.changePrice(newPrice);
    }
}
```

Important production note:

```text
Evicting cache before transaction commit can create stale refill risk.
For strict correctness, evict after commit using transaction synchronization or event listener.
```

---

## Safer Eviction After Commit

Command service publishes event:

```java
@Service
public class ProductCommandService {

    private final ApplicationEventPublisher eventPublisher;
    private final ProductRepository productRepository;

    public ProductCommandService(ApplicationEventPublisher eventPublisher,
                                 ProductRepository productRepository) {
        this.eventPublisher = eventPublisher;
        this.productRepository = productRepository;
    }

    @Transactional
    public void changePrice(Long id, BigDecimal newPrice) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.changePrice(newPrice);

        eventPublisher.publishEvent(new ProductChangedEvent(id));
    }
}
```

Eviction listener:

```java
@Component
public class ProductCacheInvalidator {

    private final RedisTemplate<String, ProductCard> redisTemplate;

    public ProductCacheInvalidator(RedisTemplate<String, ProductCard> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void invalidate(ProductChangedEvent event) {
        redisTemplate.delete("product:" + event.productId());
    }
}
```

Event:

```java
public record ProductChangedEvent(Long productId) {}
```

Mental model:

```text
Only evict after DB commit succeeds.
If DB rolls back, cache remains consistent with old truth.
```

---

## Step-by-Step Dry Run — Cache Hit

Redis:

```text
product:1001 -> ProductCard(id=1001, name=Keyboard, price=80)
TTL remaining: 3 minutes
```

Request:

```http
GET /api/products/1001
```

Flow:

```text
1. Request reaches controller.
2. Controller calls productCacheService.getProduct(1001).
3. Service builds key product:1001.
4. Redis GET product:1001.
5. Redis returns cached ProductCard.
6. Service returns ProductCard.
7. Repository is not called.
8. Database is not touched.
9. JSON response sent.
```

Result:

```text
fast response
low DB load
```

---

## Step-by-Step Dry Run — Cache Miss

Redis:

```text
product:1001 missing
```

Database:

```text
products
+------+----------+-------+
| id   | name     | price |
+------+----------+-------+
| 1001 | Keyboard | 80.00 |
+------+----------+-------+
```

Flow:

```text
1. Request reaches service.
2. Key product:1001 computed.
3. Redis GET returns null.
4. Service calls productRepository.findById(1001).
5. Database returns product.
6. Product mapped to ProductCard.
7. Redis SET product:1001 with TTL 5 minutes.
8. Service returns ProductCard.
9. Response sent.
```

Result:

```text
first request slower
later requests faster
```

---

## Step-by-Step Dry Run — Redis Timeout With DB Fallback

Scenario:

```text
Redis slow/unavailable
Business says product reads can fall back to DB
```

Flow:

```text
1. Request reaches service.
2. Redis GET attempts.
3. Redis times out after configured timeout.
4. Service records cache error metric.
5. Service queries database.
6. Response returned from DB.
7. Optional: skip Redis SET while Redis unhealthy.
```

Important:

```text
Fallback protects user response,
but DB may get sudden extra traffic.
```

Fallback must be paired with:

```text
timeouts
rate limits
circuit breaker
DB capacity protection
alerts
```

---

## Step-by-Step Dry Run — Stale Cache After Write

Bad flow:

```text
1. Redis has product:1001 price=80.
2. Admin updates DB price to 75.
3. App forgets to evict Redis key.
4. GET /api/products/1001 returns price=80.
5. Users see old price until TTL expires.
```

Root cause:

```text
Write path did not update/invalidate cache.
```

Fix:

```text
after successful DB commit:
  delete product:1001
```

---

## Step-by-Step Dry Run — Cache Stampede

Hot key:

```text
product:1001 receives 10,000 RPS
TTL expires at 12:00:00
```

Flow:

```text
1. Key expires.
2. Thousands of requests see Redis miss.
3. All query database.
4. Database overloaded.
5. Hikari pool saturates.
6. API p99 rises.
```

Protection:

```text
per-key lock
single-flight request coalescing
TTL jitter
serve stale while refresh
background refresh
rate limit
```

Simple mental model:

```text
Do not let many requests rebuild the same missing hot key at once.
```

---

## Sequence Diagram — Cache Hit

```text
Client
  |
  v
Controller
  |
  v
Service
  |
  | GET product:1001
  v
Redis
  |
  | value
  v
Service
  |
  v
Controller
  |
  v
Client
```

---

## Sequence Diagram — Cache Miss With Refill

```text
Client
  |
  v
Controller
  |
  v
Service
  |
  | GET product:1001
  v
Redis
  |
  | null
  v
Service
  |
  | findById(1001)
  v
Repository
  |
  v
Database
  |
  | row
  v
Service
  |
  | SET product:1001 TTL
  v
Redis
  |
  v
Client
```

---

## Production Scale Example

Catalog service:

```text
Traffic: 30,000 RPS
Popular 5,000 products receive 80% traffic
Database safe read capacity: 5,000 QPS
Redis p99 latency: 2 ms
DB query p99 latency: 40 ms
```

Without Redis:

```text
30,000 DB reads/sec
DB overload
```

With Redis hit ratio 90%:

```text
DB reads = 30,000 * 10% = 3,000 QPS
DB survives
```

But if Redis fails:

```text
DB suddenly receives 30,000 QPS
DB overload
```

Production design must include:

```text
Redis timeout
fallback limits
circuit breaker
rate limiting
hot key protection
cache warming for critical keys
DB protection
```

Caching is not just speed.
It is load-shaping.

---

## Production Failure Story

A team cached restaurant search results.

Key:

```text
restaurants:nearby
```

But actual response depended on:

```text
latitude
longitude
radius
cuisine
openNow
page
user membership level
```

Bug:

```text
User A searched pizza near Bucharest.
Redis stored restaurants:nearby.
User B searched sushi near Cluj.
Same key returned pizza near Bucharest.
```

Root cause:

```text
Cache key did not include all response-changing inputs.
```

Fix:

```text
restaurants:nearby:latBucket:44.43:lonBucket:26.10:radius:2km:cuisine:pizza:open:true:page:1
```

Or avoid caching if key cardinality is too high and reuse is low.

Lesson:

> **Wrong cache key is worse than no cache because it returns fast incorrect data.**

---

## Debugging Mindset

When request-to-Redis behavior is wrong, ask:

```text
1. What exact key was computed?
2. Does key include tenant/user/query parameters?
3. Was it hit or miss?
4. What value is stored?
5. What TTL remains?
6. Was value serialized/deserialized correctly?
7. Is Redis stale compared to DB?
8. Did write path invalidate after commit?
9. Is Redis timeout causing fallback?
10. Is DB overloaded due to misses?
```

### Symptom Map

```text
Fast but wrong response
  -> wrong key
  -> stale cache
  -> bad serialization
  -> missing tenant/user dimension

Slow response despite Redis
  -> cache miss
  -> Redis timeout
  -> low hit ratio
  -> key too unique
  -> serialization overhead

DB overloaded
  -> low hit ratio
  -> Redis down
  -> cache stampede
  -> TTL too short

Old data returned
  -> missing invalidation
  -> TTL too long
  -> eviction before commit issue

Redis memory high
  -> too many keys
  -> no TTL
  -> large values
  -> high cardinality keys
```

---

## Common Misconceptions

## Misconception 1 — “Redis hit means data is correct”

No.

It only means Redis had a value.
It may be stale or wrong if key/invalidation is wrong.

## Misconception 2 — “Redis miss is harmless”

A miss may trigger DB work.
Many simultaneous misses can overload DB.

## Misconception 3 — “Cache key can be simple”

Only if response depends on simple input.

Every response-changing input must be part of key.

## Misconception 4 — “Fallback to DB solves Redis outage”

Fallback helps users but may overload DB.
Fallback needs rate limiting/backpressure.

## Misconception 5 — “No TTL is safe”

No TTL can create stale data and memory growth.

## Misconception 6 — “Spring @Cacheable always works”

It works through proxy.
Self-invocation, wrong key, wrong cache manager, or serialization issues can break expected behavior.

---

## Performance Considerations

Redis adds network hop and serialization cost.

Good cache candidate:

```text
high read volume
repeated access
expensive DB/query/computation
small response
staleness acceptable
clear invalidation path
```

Bad cache candidate:

```text
unique one-time queries
rapidly changing critical data
huge payloads
unclear invalidation
security-sensitive data with long TTL
```

Measure:

```text
cache hit ratio
Redis p95/p99 latency
DB query reduction
serialization time
value size
miss rate
eviction count
timeout count
```

A cache with low hit ratio may make requests slower:

```text
Redis GET miss + DB query > DB query alone
```

---

## Scalability Considerations

At scale, Redis itself has limits:

```text
CPU per node
memory
network bandwidth
hot keys
connection count
cluster slot distribution
replication lag
eviction policy
```

Hot key:

```text
one key receives huge read traffic
one Redis node becomes bottleneck
```

Options:

```text
local in-memory cache near app
CDN for public data
replicate hot value carefully
short-circuit in app
precompute and push
```

High cardinality:

```text
millions of unique keys with low reuse
memory grows, hit ratio low
```

Better:

```text
cache coarser stable data
use pagination carefully
avoid caching highly personalized infinite combinations
```

---

## Failure Investigation Playbook

## Step 1 — Trace one request

Log safely:

```text
requestId
cache key
hit/miss
Redis latency
DB fallback yes/no
TTL on set
```

Do not log sensitive values.

## Step 2 — Compare Redis vs DB

Check:

```text
Redis value
DB value
TTL
last update time
invalidation event
```

## Step 3 — Inspect key design

Ask:

```text
Does response vary by tenant?
user?
language?
currency?
page?
filters?
roles?
location?
```

If yes, key must reflect it.

## Step 4 — Check Redis health

Metrics:

```text
latency
timeouts
memory
evictions
expired keys
connected clients
CPU
network
slowlog
```

## Step 5 — Check fallback pressure

If Redis fails:

```text
Can DB survive full fallback?
Are rate limits active?
Is circuit breaker open?
Are hot keys protected?
```

## Step 6 — Check write invalidation

For every write:

```text
Which keys become stale?
Are they evicted after commit?
What if transaction rolls back?
What if eviction fails?
```

---

## Interview Q&A

### Q1. Explain request-to-Redis flow.

Strong answer:

> The request reaches the service, which computes a deterministic cache key. It first queries Redis. If Redis returns a value, the service deserializes and returns it without hitting the database. If Redis misses, the service queries the source of truth, maps the result to a cacheable DTO, stores it in Redis with TTL, and returns it.

### Q2. What is cache-aside?

Strong answer:

> Cache-aside means the application owns cache lookup and population. It checks Redis first, falls back to the database on miss, stores the loaded result in cache, and invalidates cache when source data changes.

### Q3. Why is cache key design important?

Strong answer:

> Because the cache key determines which request gets which cached value. Every input that changes the response must be included in the key. A wrong key can return fast but incorrect data.

### Q4. How do you handle Redis failure?

Strong answer:

> It depends on business criticality. For normal cache use, I set short Redis timeouts and fall back to the database with rate limiting/circuit breaker protection. For Redis used as primary state, lock, or rate limiter, behavior must be designed differently.

### Q5. What is cache stampede?

Strong answer:

> Cache stampede happens when a hot key expires and many requests miss at the same time, causing many database queries. It can be reduced using per-key locking, TTL jitter, request coalescing, background refresh, or serving stale while refreshing.

### Q6. Why evict cache after DB commit?

Strong answer:

> If cache is evicted before commit and another request refills from old DB state, stale data can be cached again. Evicting after successful commit ensures the next refill reads committed truth.

### Q7. How do you measure cache effectiveness?

Strong answer:

> I measure hit ratio, miss ratio, Redis latency, timeout count, value size, DB query reduction, eviction count, stale-data incidents, and p95/p99 endpoint latency before and after caching.

---

## Production Checklist

```text
Read Path
[ ] Key includes all response-changing inputs
[ ] Redis timeout configured
[ ] Hit/miss metrics available
[ ] Miss path safe and bounded
[ ] DTO cached instead of entity

Write Path
[ ] Source of truth updated first
[ ] Cache evicted/updated after commit
[ ] All affected keys known
[ ] Eviction failure handled/observable
[ ] TTL aligned with staleness tolerance

Reliability
[ ] Redis outage fallback designed
[ ] DB protected during fallback
[ ] Stampede protection for hot keys
[ ] TTL jitter considered
[ ] Null caching decision intentional

Performance
[ ] Hit ratio high enough
[ ] Values not too large
[ ] Serialization cost acceptable
[ ] Redis p99 monitored
[ ] Hot keys monitored

Security
[ ] No secrets in cached values
[ ] Tenant/user isolation in key
[ ] Sensitive permissions have short TTL/invalidation
[ ] Logs do not expose cached sensitive data
```

---

## One-Page Cheat Sheet

```text
Request To Redis End-to-End
===========================

Core Idea
---------
Redis is a fast-path decision point.

Read Flow
---------
HTTP request
 -> controller
 -> service
 -> compute key
 -> Redis GET

Hit:
  deserialize
  return response

Miss:
  query source of truth
  map DTO
  Redis SET with TTL
  return response

Write Flow
----------
update DB/source of truth
commit
evict/update Redis key
next read refills

Key Rule
--------
Every response-changing input belongs in key.

TTL Rule
--------
TTL = business staleness budget.

Failure Rule
------------
Redis fallback can protect user,
but can overload DB.

Main Risks
----------
wrong key
stale cache
stampede
Redis timeout
DB fallback overload
serialization errors
tenant leakage

Best Sentence
-------------
Check Redis first,
fall back to truth on miss,
refill with TTL,
invalidate after truth changes.
```

---

## Last-Minute Interview Revision

Do not say:

```text
Request goes to Redis and becomes fast.
```

Say:

```text
In a request-to-Redis cache-aside flow, the service computes a cache key, checks Redis, returns immediately on hit, falls back to the database on miss, stores the DTO in Redis with TTL, and invalidates or updates the key after source-of-truth changes.
```

Senior version:

```text
I treat Redis as a fast-path cache, not truth. I design key correctness, TTL, after-commit invalidation, Redis timeout behavior, DB fallback protection, stampede prevention, serialization safety, and hit-ratio observability before calling the cache production-ready.
```

---

## One Picture To Remember

```text
                 REQUEST TO REDIS FAST PATH

GET /api/products/1001
          |
          v
      Controller
          |
          v
       Service
          |
          v
  key = product:1001
          |
          v
      Redis GET
          |
   +------+------+
   |             |
   v             v
 HIT           MISS
   |             |
   v             v
Return DTO     DB/source of truth
                 |
                 v
              Map DTO
                 |
                 v
              Redis SET + TTL
                 |
                 v
              Return DTO

Write:
  DB commit -> Redis evict -> next read refill
```

Final retention sentence:

> **A request-to-Redis flow is a fast-path cache journey: check the side shelf first, rebuild from truth on miss, and invalidate the shelf when truth changes.**
