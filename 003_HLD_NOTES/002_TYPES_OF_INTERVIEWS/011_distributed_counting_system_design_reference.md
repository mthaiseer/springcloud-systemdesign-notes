# Distributed Counting in System Design – One-Stop Reference

> A practical guide to **counting at scale** with architecture, trade-offs, SQL, and **working Spring Boot examples** you can build from scratch.

---

## Why Counting Becomes Hard

At small scale, counting is trivial:

```sql
UPDATE posts SET like_count = like_count + 1 WHERE post_id = 123;
```

At large scale, it breaks because of:

- **Concurrency**: many writers update the same counter simultaneously
- **Hot keys**: one viral entity sends all writes to one row / partition
- **Consistency trade-offs**: exact + real-time vs scalable + available
- **Read freshness**: users expect immediate updates
- **Durability**: in-memory speed vs persistence/recovery

### Example bottleneck
```text
1 viral post -> 10,000 increments/sec -> one row becomes the bottleneck
```

---

# 1) The Main Counting Approaches

| Approach | Accuracy | Freshness | Throughput | Complexity | Best For |
|---|---|---|---|---|---|
| Single Counter | Exact | Real-time | Low–Medium | Low | small/medium systems |
| Sharded Counters | Exact | Near-real-time | High | Medium | hot keys, viral content |
| Write-Behind / Async Aggregation | Exact (delayed) | Seconds | Very High | Medium–High | social counts, views |
| Count-Min Sketch | Approximate | Real-time | Very High | Medium | heavy hitters |
| HyperLogLog | Approximate uniques | Real-time | Very High | Low | unique visitors/users |

---

# 2) Approach 1: Single Counter with Atomic Increment

## When to use
Use this first unless you have clear evidence it is not enough.

Best for:
- non-viral counters
- moderate write rates
- exact real-time counts
- financial/inventory-like correctness (though often with stronger transaction design)

---

## Core idea
Store one row per entity and increment atomically.

### SQL schema
```sql
CREATE TABLE post_counts (
    post_id BIGINT PRIMARY KEY,
    like_count BIGINT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0
);
```

### Atomic increment
```sql
UPDATE post_counts
SET like_count = like_count + 1
WHERE post_id = 123;
```

### Exact decrement
```sql
UPDATE post_counts
SET like_count = CASE WHEN like_count > 0 THEN like_count - 1 ELSE 0 END
WHERE post_id = 123;
```

---

## Optimistic locking version
Useful if your DB/business logic needs version checks.

### Read
```sql
SELECT like_count, version
FROM post_counts
WHERE post_id = 123;
```

### Conditional update
```sql
UPDATE post_counts
SET like_count = 101,
    version = 43
WHERE post_id = 123
  AND version = 42;
```

If `0 rows affected`, retry.

---

## Spring Boot implementation

### Maven dependencies
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### Entity
```java
import jakarta.persistence.*;

@Entity
@Table(name = "post_counts")
public class PostCountEntity {

    @Id
    private Long postId;

    private Long likeCount = 0L;

    @Version
    private Long version = 0L;

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getLikeCount() { return likeCount; }
    public void setLikeCount(Long likeCount) { this.likeCount = likeCount; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
```

### Repository
```java
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PostCountRepository extends JpaRepository<PostCountEntity, Long> {

    @Modifying
    @Query("update PostCountEntity p set p.likeCount = p.likeCount + 1 where p.postId = :postId")
    int increment(@Param("postId") Long postId);

    @Modifying
    @Query("update PostCountEntity p set p.likeCount = case when p.likeCount > 0 then p.likeCount - 1 else 0 end where p.postId = :postId")
    int decrement(@Param("postId") Long postId);
}
```

### Service
```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SingleCounterService {

    private final PostCountRepository repository;

    public SingleCounterService(PostCountRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void createCounterIfMissing(Long postId) {
        repository.findById(postId).orElseGet(() -> {
            PostCountEntity entity = new PostCountEntity();
            entity.setPostId(postId);
            return repository.save(entity);
        });
    }

    @Transactional
    public void like(Long postId) {
        createCounterIfMissing(postId);
        repository.increment(postId);
    }

    @Transactional
    public void unlike(Long postId) {
        createCounterIfMissing(postId);
        repository.decrement(postId);
    }

    @Transactional(readOnly = true)
    public long getCount(Long postId) {
        return repository.findById(postId).map(PostCountEntity::getLikeCount).orElse(0L);
    }
}
```

### Controller
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/single-counter")
public class SingleCounterController {

    private final SingleCounterService service;

    public SingleCounterController(SingleCounterService service) {
        this.service = service;
    }

    @PostMapping("/{postId}/like")
    public void like(@PathVariable Long postId) {
        service.like(postId);
    }

    @PostMapping("/{postId}/unlike")
    public void unlike(@PathVariable Long postId) {
        service.unlike(postId);
    }

    @GetMapping("/{postId}")
    public long count(@PathVariable Long postId) {
        return service.getCount(postId);
    }
}
```

---

# 3) Approach 2: Sharded Counters

## When to use
Use when one counter becomes a hotspot.

Best for:
- viral posts
- celebrity content
- counters with thousands of writes/sec
- exact counts where read aggregation is acceptable

---

## Core idea
Instead of one counter row, keep **N shard rows**.

Each write goes to one shard:
```text
shard = hash(userId or requestId) % N
```

Read:
```text
total = SUM(all shards)
```

---

## SQL schema
```sql
CREATE TABLE counter_shards (
    entity_id VARCHAR(100) NOT NULL,
    shard_id INT NOT NULL,
    count_value BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (entity_id, shard_id)
);
```

### Pre-create 10 shards for one post
```sql
INSERT INTO counter_shards(entity_id, shard_id, count_value)
VALUES
('post_123', 0, 0), ('post_123', 1, 0), ('post_123', 2, 0),
('post_123', 3, 0), ('post_123', 4, 0), ('post_123', 5, 0),
('post_123', 6, 0), ('post_123', 7, 0), ('post_123', 8, 0),
('post_123', 9, 0);
```

### Increment one shard
```sql
UPDATE counter_shards
SET count_value = count_value + 1
WHERE entity_id = 'post_123'
  AND shard_id = 7;
```

### Read total
```sql
SELECT COALESCE(SUM(count_value), 0)
FROM counter_shards
WHERE entity_id = 'post_123';
```

---

## Choosing shard count

| Peak Writes/Sec | Suggested Shards |
|---:|---:|
| < 500 | 1 |
| 500 – 2,000 | 10 |
| 2,000 – 10,000 | 50–100 |
| > 10,000 | 100+ |

---

## Spring Boot implementation

### Entity
```java
import jakarta.persistence.*;

@Entity
@Table(name = "counter_shards")
@IdClass(CounterShardId.class)
public class CounterShardEntity {

    @Id
    private String entityId;

    @Id
    private Integer shardId;

    private Long countValue = 0L;

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public Integer getShardId() { return shardId; }
    public void setShardId(Integer shardId) { this.shardId = shardId; }

    public Long getCountValue() { return countValue; }
    public void setCountValue(Long countValue) { this.countValue = countValue; }
}
```

### Composite key
```java
import java.io.Serializable;
import java.util.Objects;

public class CounterShardId implements Serializable {
    private String entityId;
    private Integer shardId;

    public CounterShardId() {}

    public CounterShardId(String entityId, Integer shardId) {
        this.entityId = entityId;
        this.shardId = shardId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CounterShardId that)) return false;
        return Objects.equals(entityId, that.entityId) && Objects.equals(shardId, that.shardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId, shardId);
    }
}
```

### Repository
```java
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface CounterShardRepository extends JpaRepository<CounterShardEntity, CounterShardId> {

    @Modifying
    @Query("update CounterShardEntity c set c.countValue = c.countValue + 1 where c.entityId = :entityId and c.shardId = :shardId")
    int incrementShard(@Param("entityId") String entityId, @Param("shardId") Integer shardId);

    @Query("select coalesce(sum(c.countValue), 0) from CounterShardEntity c where c.entityId = :entityId")
    Long getTotal(@Param("entityId") String entityId);
}
```

### Service
```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class ShardedCounterService {

    private static final int SHARD_COUNT = 10;
    private final CounterShardRepository repository;

    public ShardedCounterService(CounterShardRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void initializeIfMissing(String entityId) {
        for (int i = 0; i < SHARD_COUNT; i++) {
            CounterShardId id = new CounterShardId(entityId, i);
            if (!repository.existsById(id)) {
                CounterShardEntity shard = new CounterShardEntity();
                shard.setEntityId(entityId);
                shard.setShardId(i);
                shard.setCountValue(0L);
                repository.save(shard);
            }
        }
    }

    @Transactional
    public void increment(String entityId, String userKey) {
        initializeIfMissing(entityId);
        int shard = Math.floorMod(Objects.hash(userKey), SHARD_COUNT);
        repository.incrementShard(entityId, shard);
    }

    @Transactional(readOnly = true)
    public long getTotal(String entityId) {
        return repository.getTotal(entityId);
    }
}
```

### Controller
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sharded-counter")
public class ShardedCounterController {

    private final ShardedCounterService service;

    public ShardedCounterController(ShardedCounterService service) {
        this.service = service;
    }

    @PostMapping("/{entityId}/increment")
    public void increment(@PathVariable String entityId,
                          @RequestParam String userKey) {
        service.increment(entityId, userKey);
    }

    @GetMapping("/{entityId}")
    public long total(@PathVariable String entityId) {
        return service.getTotal(entityId);
    }
}
```

---

# 4) Approach 3: Write-Behind / Async Aggregation

## When to use
Best for:
- likes
- views
- shares
- follower counts
- event-heavy counters where seconds of delay are acceptable

---

## Core idea
Do not increment the main counter on every request.

Instead:
1. accept event
2. store/publish event
3. return success
4. aggregate asynchronously in batches
5. update durable/cache counter periodically

---

## Architecture
```text
Client -> Like/View Service -> event queue
                          -> Aggregator -> Redis / DB counter
```

### Why it scales
10,000 individual writes:
```text
+1 +1 +1 +1 ... +1
```

Become one batched update:
```text
+10000
```

---

## SQL backing table
```sql
CREATE TABLE aggregated_counts (
    entity_id VARCHAR(100) PRIMARY KEY,
    count_value BIGINT NOT NULL DEFAULT 0
);
```

### Upsert batch update
PostgreSQL:
```sql
INSERT INTO aggregated_counts(entity_id, count_value)
VALUES ('post_123', 100)
ON CONFLICT (entity_id)
DO UPDATE SET count_value = aggregated_counts.count_value + EXCLUDED.count_value;
```

---

## Spring Boot implementation (in-memory queue simulation)

This example uses:
- REST event ingestion
- in-memory queue
- scheduled batch flush
- JDBC upsert style logic via JPA/native approach

### Entity
```java
import jakarta.persistence.*;

@Entity
@Table(name = "aggregated_counts")
public class AggregatedCountEntity {

    @Id
    private String entityId;

    private Long countValue = 0L;

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public Long getCountValue() { return countValue; }
    public void setCountValue(Long countValue) { this.countValue = countValue; }
}
```

### Repository
```java
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface AggregatedCountRepository extends JpaRepository<AggregatedCountEntity, String> {

    @Modifying
    @Query("update AggregatedCountEntity a set a.countValue = a.countValue + :delta where a.entityId = :entityId")
    int incrementBy(@Param("entityId") String entityId, @Param("delta") Long delta);
}
```

### Event DTO
```java
public record CountEvent(String entityId, long delta) {}
```

### Service
```java
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class AsyncCountService {

    private final AggregatedCountRepository repository;
    private final ConcurrentLinkedQueue<CountEvent> queue = new ConcurrentLinkedQueue<>();

    public AsyncCountService(AggregatedCountRepository repository) {
        this.repository = repository;
    }

    public void acceptEvent(String entityId, long delta) {
        queue.add(new CountEvent(entityId, delta));
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void flush() {
        Map<String, Long> batch = new ConcurrentHashMap<>();

        CountEvent event;
        while ((event = queue.poll()) != null) {
            batch.merge(event.entityId(), event.delta(), Long::sum);
        }

        for (Map.Entry<String, Long> entry : batch.entrySet()) {
            String entityId = entry.getKey();
            Long delta = entry.getValue();

            int updated = repository.incrementBy(entityId, delta);
            if (updated == 0) {
                AggregatedCountEntity entity = new AggregatedCountEntity();
                entity.setEntityId(entityId);
                entity.setCountValue(delta);
                repository.save(entity);
            }
        }
    }

    @Transactional(readOnly = true)
    public long getCount(String entityId) {
        return repository.findById(entityId)
                .map(AggregatedCountEntity::getCountValue)
                .orElse(0L);
    }
}
```

### Enable scheduling
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CountingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CountingApplication.class, args);
    }
}
```

### Controller
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/async-counter")
public class AsyncCounterController {

    private final AsyncCountService service;

    public AsyncCounterController(AsyncCountService service) {
        this.service = service;
    }

    @PostMapping("/{entityId}/event")
    public void event(@PathVariable String entityId,
                      @RequestParam(defaultValue = "1") long delta) {
        service.acceptEvent(entityId, delta);
    }

    @GetMapping("/{entityId}")
    public long count(@PathVariable String entityId) {
        return service.getCount(entityId);
    }
}
```

---

# 5) Approach 4: Count-Min Sketch

## When to use
Best for:
- heavy hitter detection
- approximate event counts
- rate limiting by IP/API key
- huge cardinality with low memory

### Important
Count-Min Sketch:
- **can overestimate**
- good when approximate counts are okay
- is not ideal for exact user-visible totals

---

## Core idea
Use multiple hash functions and a 2D integer matrix.

Insert:
- hash item `d` times
- increment one cell in each row

Query:
- hash item `d` times
- return the **minimum** counter

---

## Spring Boot implementation

### Sketch class
```java
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class CountMinSketch {

    private final int depth;
    private final int width;
    private final long[][] table;
    private final long[] seeds;

    public CountMinSketch(int depth, int width) {
        this.depth = depth;
        this.width = width;
        this.table = new long[depth][width];
        this.seeds = new long[depth];
        for (int i = 0; i < depth; i++) {
            seeds[i] = 31L + i * 17L;
        }
    }

    public void add(String item, long count) {
        for (int i = 0; i < depth; i++) {
            int idx = hash(item, seeds[i]);
            table[i][idx] += count;
        }
    }

    public long estimate(String item) {
        long min = Long.MAX_VALUE;
        for (int i = 0; i < depth; i++) {
            int idx = hash(item, seeds[i]);
            min = Math.min(min, table[i][idx]);
        }
        return min;
    }

    private int hash(String item, long seed) {
        CRC32 crc = new CRC32();
        crc.update((seed + ":" + item).getBytes(StandardCharsets.UTF_8));
        return (int) (Math.abs(crc.getValue()) % width);
    }
}
```

### Service
```java
import org.springframework.stereotype.Service;

@Service
public class CountMinSketchService {

    private final CountMinSketch sketch = new CountMinSketch(5, 10000);

    public void add(String item, long delta) {
        sketch.add(item, delta);
    }

    public long estimate(String item) {
        return sketch.estimate(item);
    }
}
```

### Controller
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cms")
public class CountMinSketchController {

    private final CountMinSketchService service;

    public CountMinSketchController(CountMinSketchService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public void add(@RequestParam String item,
                    @RequestParam(defaultValue = "1") long delta) {
        service.add(item, delta);
    }

    @GetMapping("/estimate")
    public long estimate(@RequestParam String item) {
        return service.estimate(item);
    }
}
```

---

# 6) Approach 5: HyperLogLog

## When to use
Use this when you need **unique counts**, not total counts.

Examples:
- unique viewers
- unique visitors
- unique devices
- distinct users per day

---

## Why HyperLogLog
Exact unique counting via `Set` is memory-heavy.

### Comparison
| Method | Memory for 1M uniques | Accuracy |
|---|---:|---|
| HashSet | ~64 MB | exact |
| HyperLogLog | ~12 KB | ~99.2% |

---

## Best practical implementation
Use Redis built-in HLL.

### Commands
```redis
PFADD unique_visitors:page_123 user_456
PFADD unique_visitors:page_123 user_789
PFCOUNT unique_visitors:page_123
```

---

## Spring Boot + Redis HyperLogLog

### Add Redis dependency
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### Redis config
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory cf) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(cf);
        return template;
    }
}
```

### Service
```java
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class HyperLogLogService {

    private final RedisTemplate<String, String> redisTemplate;

    public HyperLogLogService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addVisitor(String key, String visitorId) {
        redisTemplate.opsForHyperLogLog().add(key, visitorId);
    }

    public long count(String key) {
        Long value = redisTemplate.opsForHyperLogLog().size(key);
        return value == null ? 0L : value;
    }
}
```

### Controller
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hll")
public class HyperLogLogController {

    private final HyperLogLogService service;

    public HyperLogLogController(HyperLogLogService service) {
        this.service = service;
    }

    @PostMapping("/visit")
    public void visit(@RequestParam String key,
                      @RequestParam String visitorId) {
        service.addVisitor(key, visitorId);
    }

    @GetMapping("/count")
    public long count(@RequestParam String key) {
        return service.count(key);
    }
}
```

---

# 7) Read-Your-Own-Writes Pattern

## Why it matters
If you use async aggregation, users may not immediately see their own like/view reflected.

### Solutions
- optimistic UI update
- session/local overlay
- temporary per-user delta cache

### Backend idea
```text
displayedCount = cachedCount + userPendingDelta
```

This makes eventual consistency feel real-time.

---

# 8) Negative Counts / Decrements

## Problem
If you support unlike / unfollow / decrement:
- counters can go negative
- retries can double-decrement
- approximate methods get tricky

### Recommendations
- protect against negative floor in SQL
- use idempotency keys for actions
- model state transitions explicitly for likes/follows

Example:
```sql
UPDATE post_counts
SET like_count = CASE WHEN like_count > 0 THEN like_count - 1 ELSE 0 END
WHERE post_id = 123;
```

---

# 9) Idempotency and Double Count Protection

## Why needed
Retries happen.
If the same event is processed twice, counts become wrong.

### Common solution
Store processed event IDs.

### Example table
```sql
CREATE TABLE processed_events (
    event_id VARCHAR(100) PRIMARY KEY,
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Flow
1. receive event with `event_id`
2. try insert into `processed_events`
3. if insert succeeds -> process count
4. if duplicate key -> ignore

---

# 10) Durability and Recovery

## Risk
Pure in-memory counters are fast but volatile.

### Recommendations
- persist periodically
- replay from event log if using queue
- snapshot approximate structures if needed
- keep durable source-of-truth events when correctness matters

---

# 11) Choosing the Right Approach

## Decision guide

### Need exact count?
- yes -> Single counter / Sharded counter / Write-behind
- no -> continue

### Need unique count?
- yes -> HyperLogLog
- no -> continue

### Need approximate heavy-hitter/event estimate?
- yes -> Count-Min Sketch

### Very high write rate on hot keys?
- yes -> Sharded counter or async aggregation

### Real-time strict freshness?
- yes -> Single counter or sharded counter
- no -> async aggregation is usually best

---

## Quick recommendation table

| Use Case | Recommended Approach |
|---|---|
| bank/inventory exact count | single counter with strong consistency |
| social likes on small/medium scale | single counter |
| viral post likes | sharded counters |
| views/likes at very high scale | async aggregation |
| unique visitors | HyperLogLog |
| top APIs / top IPs / heavy hitters | Count-Min Sketch |

---

# 12) Common Pitfalls

## 1. Premature optimization
Do not start with sharded counters unless you need them.

## 2. Ignoring read-your-own-writes
Users expect immediate feedback.

## 3. Not planning for decrements
Likes/unlikes need careful design.

## 4. Forgetting durability
In-memory only is risky.

## 5. Over-sharding
Too many shards make reads expensive.

---

# 13) Interview Answer Template

```text
For counting systems, the main trade-off is between accuracy, freshness, throughput, and complexity.

If the write rate is moderate and I need exact real-time counts, I’d start with a single atomic counter because it is the simplest solution and scales farther than people expect.

If I see a hot-key problem, for example a viral post receiving thousands of increments per second, I’d move to sharded counters so write load is distributed across multiple rows or keys and aggregated on read.

If freshness can be delayed by a few seconds, I’d use write-behind aggregation with a queue and batch updates, since that dramatically improves throughput and smooths spikes.

If I only need unique counts, I’d use HyperLogLog, and if I need approximate heavy-hitter counting, I’d use Count-Min Sketch.

I’d also think about idempotency, read-your-own-writes, negative counts, and durability because those are the practical details that make counting systems work in production.
```

---

# 14) Final Summary

- **Start simple**  
  Atomic single counters are enough more often than people think.

- **Shard when hot keys appear**  
  Sharded counters spread write load while preserving exactness.

- **Async aggregation is the main scale unlock**  
  Batch updates let you handle huge throughput.

- **Approximate counting is often the right trade-off**  
  HyperLogLog and Count-Min Sketch give excellent scale with tiny memory.

- **Unique counting is a different problem from total counting**  
  Use HyperLogLog for distinct users/visitors.

- **User experience matters**  
  Even with eventual consistency, implement read-your-own-writes behavior.

- **Operational details matter**  
  Idempotency, durability, anomaly detection, and correction paths are part of the real design.

---

## Final 1-Line Shortcut
```text
Exact + low scale -> single counter | Exact + hot key -> sharded counter | Massive scale -> async aggregation | Unique -> HyperLogLog | Approximate heavy hitters -> Count-Min Sketch
```
