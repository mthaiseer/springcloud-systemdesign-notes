# 028_Redis_Cache_Integration — The Fast Side Shelf Model

## Core Mental Model

Do not imagine Redis cache as:

```text
Add Redis and database becomes fast.
```

That is too shallow and dangerous.

The better mental model is:

> **Redis is a fast side shelf placed in front of the database. The application checks the shelf first; if the item is missing, it goes to the database and refills the shelf.**

```text
Client Request
      |
      v
Application
      |
      v
+-------------------------+
| Redis Cache             |
| fast side shelf         |
+-------------------------+
      |
      | cache miss
      v
+-------------------------+
| Database                |
| source of truth         |
+-------------------------+
```

This chapter teaches exactly one idea:

> **Redis cache integration is not just storing data in Redis; it is designing the read/write path so cache hits are fast, cache misses are safe, and stale data is controlled.**

If you remember only one sentence:

> **The database owns truth; Redis owns speed. Your application owns consistency decisions between them.**

---

## Why This Exists

Databases are powerful, but not every request should hit the database.

Example product API:

```http
GET /api/products/1001
```

If this endpoint receives 10,000 requests per second and every request runs SQL:

```sql
SELECT id, name, price, stock FROM products WHERE id = 1001;
```

The database becomes overloaded.

But many reads are repeated:

```text
same product
same user profile
same catalog page
same configuration
same permission set
same exchange rate
same feature flag
```

Redis exists to serve repeated reads from memory.

```text
Memory access is much faster than disk/database query path.
```

But Redis introduces new problems:

```text
What if cache has old value?
What if cache miss causes DB stampede?
What if Redis is down?
When should cache expire?
When should cache be invalidated?
What should be cached?
What should never be cached?
```

So Redis cache integration exists to improve read performance while explicitly managing correctness risk.

---

## Problem Statement

Imagine this service:

```java
public ProductResponse getProduct(Long id) {
    Product product = productRepository.findById(id)
            .orElseThrow();

    return ProductResponse.from(product);
}
```

At scale:

```text
10,000 RPS
same 1,000 popular products
database query latency = 20 ms
database cannot handle all repeated reads
```

The core problem:

> **How can we avoid repeatedly querying the database for the same read-heavy data without making the system return incorrect stale data?**

Redis cache integration solves this by using patterns:

```text
Cache-aside:
  app checks Redis
  if miss, app loads DB
  app writes Redis
  app returns result

TTL:
  cached value expires automatically

Invalidation:
  update/delete cache when source data changes

Fallback:
  if Redis fails, app can still use DB carefully

Protection:
  avoid cache stampede, penetration, avalanche
```

The mental model:

```text
Redis is not source of truth.
Redis is a fast copy with an expiration and invalidation strategy.
```

---

## Real World Analogy

Imagine a restaurant kitchen.

The main warehouse is far away but has the true inventory.

The chef keeps a small side shelf near the cooking station:

```text
Salt
pepper
oil
popular sauces
```

When the chef needs salt:

```text
1. Check side shelf.
2. If present, use it immediately.
3. If missing, go to warehouse.
4. Refill side shelf.
```

Mapping:

```text
Restaurant side shelf          Redis cache
Main warehouse                 Database
Chef                           Application service
Shelf item                     Cached value
Missing item                   Cache miss
Expired item                   TTL expired
Warehouse truth                Source of truth
```

Important:

> **The shelf is fast, but the warehouse is authoritative.**

If warehouse inventory changes, the shelf may become stale unless someone updates or expires it.

That is cache integration.

---

## The One Mental Model

Think of every cached read as a fork:

```text
Request for data
      |
      v
Check Redis
      |
      +-- HIT  -> return cached value
      |
      +-- MISS -> query DB
                 store in Redis with TTL
                 return value
```

ASCII:

```text
GET product:1001
      |
      v
+------------------+
| Redis GET key    |
+------------------+
      |
  +---+---+
  |       |
 HIT     MISS
  |       |
  v       v
return   DB SELECT
cached     |
value      v
        Redis SET key value TTL
           |
           v
        return value
```

This is cache-aside.

Most Spring Boot Redis integration starts here.

---

## Core Concepts

## Source of Truth

The source of truth is where correct durable data lives.

Usually:

```text
PostgreSQL
MySQL
Oracle
MongoDB
```

Redis cache is usually not source of truth for normal application data.

```text
Database = truth
Redis = fast copy
```

## Cache Hit

A cache hit means the value was found in Redis.

```text
key exists
value returned
database not touched
```

Good:

```text
low latency
less database load
```

## Cache Miss

A cache miss means Redis does not have the value.

Reasons:

```text
key never cached
TTL expired
key evicted
cache invalidated
Redis restarted
wrong key used
```

Miss flow usually goes to database.

## TTL

TTL means time-to-live.

```text
product:1001 expires in 5 minutes
```

TTL controls how long stale data may survive.

```text
Short TTL:
  fresher data
  more DB misses

Long TTL:
  better hit ratio
  more stale-data risk
```

## Invalidation

Invalidation means removing or updating cache when data changes.

Example:

```text
Product price changes.
Delete product:1001 cache key.
Next read reloads fresh DB value.
```

## Cache Key

The cache key identifies cached data.

Examples:

```text
product:1001
user:42:profile
catalog:page:1:size:20
permissions:user:42
```

Bad keys cause bugs.

```text
product:1001 is fine.
product is too broad.
```

## Serialization

Redis stores bytes/strings.

Your Java object must be serialized.

Common options:

```text
JSON
String
Java serialization
MessagePack/CBOR
```

For most APIs, JSON is understandable and debuggable.

## Eviction

Redis has limited memory.

When memory is full, Redis may evict keys depending on policy.

Do not assume a cached key exists forever.

---

## Internal Architecture

```text
Controller
   |
   v
Service
   |
   +--> RedisTemplate / CacheManager
   |
   +--> Repository
   |
   v
Database
```

Cache-aside service:

```text
Service owns read strategy:
  check cache
  load DB on miss
  write cache
```

Spring Cache abstraction:

```text
@Cacheable
@CacheEvict
@CachePut
```

Manual RedisTemplate:

```text
redisTemplate.opsForValue().get(key)
redisTemplate.opsForValue().set(key, value, ttl)
```

Both are useful.

The important part is the mental model, not the annotation.

---

## Internal Working — Cache-Aside

Service code conceptually:

```java
public ProductResponse getProduct(Long id) {
    String key = "product:" + id;

    ProductResponse cached = redis.get(key);
    if (cached != null) {
        return cached;
    }

    Product product = database.findById(id);
    ProductResponse response = ProductResponse.from(product);

    redis.set(key, response, Duration.ofMinutes(5));

    return response;
}
```

Runtime flow:

```text
1. Request asks for product 1001.
2. App computes key product:1001.
3. App asks Redis for key.
4. If Redis returns value, app returns immediately.
5. If Redis returns null, app queries database.
6. App maps DB entity to response DTO.
7. App stores DTO in Redis with TTL.
8. App returns response.
```

The cache is populated lazily.

Only requested data is cached.

---

## Rich ASCII Diagram — Cache-Aside Read

```text
Client
  |
  | GET /api/products/1001
  v
ProductController
  |
  v
ProductService
  |
  | key = product:1001
  v
+----------------------+
| Redis GET            |
+----------------------+
  |
  +--------------------+
  |                    |
  v                    v
HIT                  MISS
  |                    |
  v                    v
Return cached       ProductRepository
response                |
                        v
                    Database SELECT
                        |
                        v
                    ProductResponse
                        |
                        v
                    Redis SET key TTL
                        |
                        v
                    Return response
```

---

## Rich ASCII Diagram — Write With Invalidation

```text
Update product price
        |
        v
ProductService.updatePrice()
        |
        v
+----------------------+
| Database UPDATE      |
| source of truth      |
+----------------------+
        |
        v
+----------------------+
| Redis DEL product:id |
| invalidate old copy  |
+----------------------+
        |
        v
Next read reloads fresh value
```

Mental model:

```text
Write truth first.
Remove stale copy.
Let next read refill.
```

---

## Step-by-Step Dry Run — First Read Miss

Database:

```text
products
+------+----------+-------+
| id   | name     | price |
+------+----------+-------+
| 1001 | Keyboard | 80.00 |
+------+----------+-------+
```

Redis before:

```text
empty
```

Request:

```text
GET /api/products/1001
```

Flow:

```text
1. Controller receives request.
2. Service builds key: product:1001.
3. Redis GET product:1001.
4. Redis returns null.
5. Service calls ProductRepository.
6. Database returns product.
7. Service maps ProductResponse.
8. Service writes Redis key with TTL 5 minutes.
9. Service returns response.
```

Redis after:

```text
product:1001 -> {"id":1001,"name":"Keyboard","price":80.00}
TTL: 5 minutes
```

Latency:

```text
Redis miss + DB query
```

---

## Step-by-Step Dry Run — Second Read Hit

Redis:

```text
product:1001 exists
```

Request:

```text
GET /api/products/1001
```

Flow:

```text
1. Service builds key.
2. Redis GET product:1001.
3. Redis returns JSON value.
4. Service deserializes it.
5. Service returns response.
6. Database is not touched.
```

Latency:

```text
Redis only
```

This is the main benefit.

---

## Step-by-Step Dry Run — Update Then Invalidate

Current Redis:

```text
product:1001 price=80.00
```

Admin updates price:

```http
PATCH /api/products/1001/price
{
  "price": 75.00
}
```

Flow:

```text
1. Service starts transaction.
2. Product loaded from DB.
3. Price changed to 75.00.
4. Transaction commits.
5. Service deletes Redis key product:1001.
6. Next GET sees cache miss.
7. Next GET loads DB price=75.00.
8. Redis refilled with fresh value.
```

Why delete instead of update?

```text
Delete is simple and safe.
Next read rebuilds canonical response from DB.
```

But for high traffic, you may update cache immediately if needed.

---

## Step-by-Step Dry Run — Stale Cache Bug

Bad flow:

```text
1. Product price in DB = 80.
2. Redis caches price = 80 for 1 hour.
3. Admin updates DB price = 75.
4. App forgets to delete/update Redis.
5. Users keep seeing 80 until TTL expires.
```

Root cause:

```text
Write path did not handle cache invalidation.
```

Lesson:

```text
Caching is easy.
Invalidation is the hard part.
```

---

## Java Code Example — Manual RedisTemplate

Configuration:

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, ProductResponse> productRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper
    ) {
        RedisTemplate<String, ProductResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<ProductResponse> serializer =
                new Jackson2JsonRedisSerializer<>(ProductResponse.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }
}
```

Response DTO:

```java
public record ProductResponse(
        Long id,
        String name,
        BigDecimal price
) {}
```

Service:

```java
@Service
public class ProductQueryService {

    private final RedisTemplate<String, ProductResponse> redisTemplate;
    private final ProductRepository productRepository;

    public ProductQueryService(RedisTemplate<String, ProductResponse> redisTemplate,
                               ProductRepository productRepository) {
        this.redisTemplate = redisTemplate;
        this.productRepository = productRepository;
    }

    public ProductResponse getProduct(Long id) {
        String key = "product:" + id;

        ProductResponse cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));

        ProductResponse response = new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice()
        );

        redisTemplate.opsForValue().set(key, response, Duration.ofMinutes(5));

        return response;
    }
}
```

Execution explanation:

```text
Redis GET first.
If present, no DB call.
If missing, DB call.
After DB result, Redis SET with TTL.
Return response.
```

---

## Spring Boot Code Example — Spring Cache Abstraction

Enable caching:

```java
@Configuration
@EnableCaching
public class CacheConfig {
}
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
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice()
        );
    }
}
```

Update service:

```java
@Service
public class ProductCommandService {

    private final ProductRepository productRepository;

    public ProductCommandService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @CacheEvict(cacheNames = "products", key = "#id")
    public void updatePrice(Long id, BigDecimal newPrice) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));

        product.changePrice(newPrice);
    }
}
```

Mental model:

```text
@Cacheable:
  check cache before method
  if hit, skip method
  if miss, execute method and cache result

@CacheEvict:
  remove cache entry when command runs
```

Important:

```text
Spring cache works through proxies.
Self-invocation can bypass caching.
```

Bad:

```java
public ProductResponse getAndLog(Long id) {
    return getProduct(id); // same-class call may bypass @Cacheable
}
```

Better:

```text
Call cached method through another Spring bean,
or structure service boundaries clearly.
```

---

## Spring Boot Redis Properties

Example:

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379

spring.cache.type=redis
spring.cache.redis.time-to-live=5m
spring.cache.redis.cache-null-values=false
```

Meaning:

```text
spring.cache.type=redis
  use Redis as Spring cache backend

time-to-live=5m
  entries expire after 5 minutes

cache-null-values=false
  do not cache null values by default
```

Production note:

```text
Cache null values only intentionally.
It can protect DB from cache penetration,
but can also hide newly created records until TTL expires.
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
  | value found
  v
Service
  |
  v
Controller
  |
  v
Client
```

No database access.

---

## Sequence Diagram — Cache Miss

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
  | product row
  v
Service
  |
  | SET product:1001 with TTL
  v
Redis
  |
  v
Client
```

---

## Production Scale Example

Imagine catalog service:

```text
Read traffic: 20,000 RPS
Write traffic: 50 RPS
Popular product reads repeat heavily
Database safe read capacity: 3,000 QPS
```

Without cache:

```text
20,000 DB reads/sec
DB overloaded
p99 latency rises
```

With Redis cache:

```text
cache hit ratio = 90%

DB read load:
20,000 * 10% = 2,000 DB reads/sec
```

Now DB survives.

But write path must invalidate:

```text
Product update -> DB commit -> Redis delete product key
```

Production metrics to watch:

```text
cache hit ratio
cache miss ratio
Redis latency
Redis errors
DB query count
cache evictions
memory usage
key count
hot keys
```

Senior view:

```text
Cache reduces read load only if hit ratio is high and invalidation is correct.
```

---

## Production Failure Story

A team cached user permissions:

```text
user:42:permissions
TTL = 24 hours
```

It improved performance.

Then a user lost admin access in the database.

But Redis still had:

```text
ADMIN permission
```

For hours, the user could access admin operations.

Root cause:

```text
Security-sensitive data cached with long TTL and no invalidation on permission update.
```

Fix:

```text
Evict permission cache immediately when roles change.
Use short TTL for sensitive authorization data.
Consider versioned permissions.
Add audit logs.
Avoid caching high-risk decisions too long.
```

Lesson:

> **Caching stale product names is annoying; caching stale permissions is dangerous. Cache correctness depends on business risk.**

---

## Debugging Mindset

When Redis cache causes issues, ask:

```text
1. What is the source of truth?
2. What key is used?
3. Was this a hit or miss?
4. What TTL does the key have?
5. Was cache invalidated on write?
6. Is stale data acceptable for this feature?
7. Is Redis failure handled?
8. Is cache stampede possible?
9. Are null values cached?
10. Is serialization compatible?
```

### Symptom Map

```text
Old value returned
  -> stale cache
  -> missing invalidation
  -> TTL too long
  -> wrong key

Database still overloaded
  -> low hit ratio
  -> wrong cache keys
  -> TTL too short
  -> too many unique reads

Redis memory high
  -> too many keys
  -> TTL missing
  -> large values
  -> eviction policy issue

Cache miss storm
  -> popular key expired
  -> many threads rebuild same key

Serialization error
  -> DTO changed
  -> incompatible serializer
  -> class metadata issue

Redis down
  -> fallback path missing
  -> app depends too hard on cache
```

---

## Common Misconceptions

## Misconception 1 — “Redis makes data consistent and fast”

Redis makes reads fast.
Consistency is your design responsibility.

## Misconception 2 — “Cache and database always match”

No.

Cache is a copy.
Copies can become stale.

## Misconception 3 — “Long TTL is always better”

Long TTL improves hit ratio but increases stale-data window.

## Misconception 4 — “Short TTL solves everything”

Short TTL reduces staleness but can increase DB load and cache stampede.

## Misconception 5 — “Cache every query”

No.

Cache data that is:

```text
read-heavy
expensive to compute/load
safe to serve slightly stale
reused often
not too large
```

Do not blindly cache:

```text
highly personalized one-off data
rapidly changing critical data
large unbounded result sets
security decisions without invalidation
```

## Misconception 6 — “Redis unavailable means app must fail”

For many caches, Redis failure should degrade to database fallback.

But for Redis used as primary storage, queue, lock, or rate limiter, the answer differs.

This chapter is about Redis as cache.

---

## Cache Stampede

Cache stampede happens when many requests miss the same hot key at once.

Example:

```text
product:1001 expires
10,000 requests arrive
all see miss
all query DB
DB overloaded
```

Protection options:

```text
single-flight locking
randomized TTL jitter
early refresh
background warming
serve stale while refreshing
request coalescing
```

Simple TTL jitter:

```text
base TTL = 5 minutes
random extra = 0-60 seconds
```

This avoids many keys expiring at the exact same moment.

---

## Cache Penetration

Cache penetration happens when requests repeatedly ask for data that does not exist.

Example:

```text
GET /products/999999999
not in Redis
not in DB
repeat many times
```

Every request hits DB.

Protection:

```text
cache null/empty result with short TTL
validate IDs
Bloom filter for valid IDs
rate limit suspicious requests
```

Careful:

```text
Caching null can hide newly created data until null TTL expires.
Use short TTL.
```

---

## Cache Avalanche

Cache avalanche happens when many keys expire around the same time.

Example:

```text
10,000 catalog keys all TTL 5 minutes
all expire together
DB receives sudden burst
```

Protection:

```text
TTL jitter
staggered warming
separate TTLs
capacity planning
bulk reload protection
```

---

## Performance Considerations

Redis cache improves performance when:

```text
hit ratio is high
cached value is small enough
serialization cost is low
Redis latency is lower than DB/query cost
cache does not create extra complexity on write path
```

Bad cache:

```text
low hit ratio
large values
high serialization cost
wrong TTL
stale data bugs
extra network hop without benefit
```

Measure:

```text
cache hit rate
cache miss rate
average Redis latency
p99 Redis latency
DB query reduction
payload size
serialization time
```

Rule:

```text
Do not assume cache helps.
Measure before and after.
```

---

## Scalability Considerations

At scale, Redis becomes shared infrastructure.

Consider:

```text
memory limit
eviction policy
hot keys
network bandwidth
cluster/sharding
replication
failover
connection pool
timeout settings
serialization size
key cardinality
```

Hot key problem:

```text
One key receives huge traffic.
Redis node CPU/network becomes bottleneck.
```

Options:

```text
local in-memory cache near app
replicate value under multiple keys carefully
CDN for public data
split hot data
use client-side caching if supported
```

Redis is fast, but not infinite.

---

## Failure Investigation Playbook

## Step 1 — Confirm hit/miss behavior

Log or metric:

```text
cache hit
cache miss
cache evict
cache error
```

## Step 2 — Inspect key

Check:

```text
key format
tenant/user dimension
request parameters
version prefix
TTL
value size
```

Example bad key:

```text
catalog:page:1
```

If query includes language and currency, better:

```text
catalog:lang:en:currency:EUR:page:1
```

## Step 3 — Check invalidation path

For every write, ask:

```text
Which cache keys become stale?
Are they deleted?
Are they updated?
Is eviction after transaction commit?
```

Evicting before DB commit can cause inconsistent refill.

## Step 4 — Check Redis health

Look at:

```text
latency
memory
evicted keys
expired keys
connected clients
CPU
network
slowlog
```

## Step 5 — Check database fallback

Ask:

```text
If Redis fails, does DB survive fallback traffic?
Is there rate limiting?
Is circuit breaker needed?
```

---

## Interview Q&A

### Q1. What is cache-aside pattern?

Strong answer:

> In cache-aside, the application checks Redis first. If the value exists, it returns it. If missing, the application queries the database, stores the result in Redis with a TTL, and returns it. The application owns cache population and invalidation.

### Q2. Is Redis the source of truth?

Strong answer:

> In typical cache integration, no. The database is the source of truth and Redis is a fast copy. Redis may expire or evict data, so the application must be able to reload from the database.

### Q3. How do you handle updates with cache?

Strong answer:

> Usually update the database first, commit the transaction, then evict or update the cache key. Eviction is simple and safe because the next read reloads the fresh value from the database.

### Q4. What is cache stampede?

Strong answer:

> Cache stampede happens when a hot key expires and many requests miss at the same time, causing all of them to query the database. It can be reduced with locking, TTL jitter, background refresh, or serving stale data while refreshing.

### Q5. What should you cache?

Strong answer:

> Cache read-heavy, frequently reused, expensive-to-load data that can tolerate some staleness. Avoid caching rapidly changing critical data, huge result sets, or security-sensitive decisions unless invalidation and TTL are carefully designed.

### Q6. What is the risk of long TTL?

Strong answer:

> Long TTL improves hit ratio but increases the stale-data window. If the data changes and invalidation fails, users may see old values for a long time.

### Q7. How do you debug stale cache?

Strong answer:

> I check the key, TTL, cache value, source DB value, write path invalidation, whether eviction happens after commit, and whether the request is using the correct key dimensions like tenant, language, currency, or user ID.

---

## Production Checklist

```text
Cache Design
[ ] Is database still source of truth?
[ ] Is cache key precise?
[ ] Is TTL chosen by business staleness tolerance?
[ ] Is value size reasonable?
[ ] Is serialization stable?

Read Path
[ ] Cache hit path avoids DB
[ ] Cache miss path loads DB safely
[ ] Cache miss storm protection considered
[ ] Null caching decision intentional

Write Path
[ ] DB update happens first
[ ] Cache evicted/updated after successful commit
[ ] All affected keys are known
[ ] Sensitive data has short TTL/invalidation

Failure Handling
[ ] Redis timeout configured
[ ] Redis failure fallback designed
[ ] DB can survive fallback or has protection
[ ] Cache errors are observable

Observability
[ ] Hit/miss ratio
[ ] Redis latency
[ ] Redis memory
[ ] Evictions
[ ] DB query reduction
[ ] Stale-data incidents tracked
```

---

## One-Page Cheat Sheet

```text
Redis Cache Integration
=======================

Core Idea
---------
Redis is a fast side shelf.
Database is source of truth.

Cache-Aside Read
----------------
GET key from Redis
  hit  -> return cached value
  miss -> query DB
          SET key with TTL
          return value

Write Strategy
--------------
Update DB first.
After commit, evict/update cache.

TTL
---
Controls stale-data window.
Short TTL -> fresher, more DB load.
Long TTL  -> faster, more stale risk.

Cache Hit
---------
Fast path, no DB call.

Cache Miss
----------
Slow path, DB call + refill.

Main Risks
----------
stale data
stampede
penetration
avalanche
serialization bugs
Redis outage
wrong keys

Best Sentence
-------------
Database owns truth.
Redis owns speed.
Application owns consistency decisions.
```

---

## Last-Minute Interview Revision

Do not say:

```text
I use Redis to make database fast.
```

Say:

```text
I use Redis as a cache-aside fast copy for read-heavy data. The application checks Redis first, falls back to the database on miss, stores the result with TTL, and evicts or updates the cache when the source data changes. The database remains the source of truth.
```

Senior version:

```text
I design Redis caching around correctness risk: key design, TTL, invalidation after commit, stampede protection, Redis failure fallback, and observability for hit ratio, stale data, and DB load reduction.
```

---

## One Picture To Remember

```text
                 REDIS CACHE INTEGRATION

                   Request for product:1001
                              |
                              v
                    +------------------+
                    | Check Redis      |
                    +------------------+
                              |
                 +------------+------------+
                 |                         |
                 v                         v
              CACHE HIT                 CACHE MISS
                 |                         |
                 v                         v
        Return cached DTO          Query Database
                                           |
                                           v
                                  Build response DTO
                                           |
                                           v
                                  Store in Redis + TTL
                                           |
                                           v
                                      Return DTO

                 Write path:
                 Update DB -> Commit -> Evict Redis key
```

Final retention sentence:

> **Redis cache is a fast side shelf: read from it when possible, refill from the database on miss, and invalidate it when truth changes.**
