
# Redis System Design Notes
*Interview-ready, implementation-oriented, and practical for building systems from simple caches to high-throughput real-time services.*

## How to use these notes
This guide is meant to help you:
1. explain Redis clearly in system design interviews
2. choose the right Redis data structure for a problem
3. understand durability, replication, and scaling trade-offs
4. build a real Spring Boot service with Redis

For each major section, you get:
- when to use it
- the key design idea
- Redis command examples
- practical trade-offs
- implementation notes

---

## 1. Why Redis shows up in system design

Redis is one of the most common answers in interviews for:
- rate limiting
- leaderboards
- session stores
- caching
- distributed locking
- lightweight queues
- real-time counters

The reason is simple: Redis keeps data in memory and provides rich data structures with low-latency operations.

### What makes Redis special
- sub-millisecond latency
- atomic single-command operations
- TTL support
- rich data structures
- replication and failover support
- clustering for horizontal scale

### What Redis is not
Redis is usually **not** your main durable database for critical business data. It is best as:
- a cache
- a derived data store
- a session store
- a coordination layer
- a real-time data store where limited loss is acceptable

### Interview answer
Use Redis when you need very fast reads/writes and your access patterns map well to Redis data structures.

---

## 2. Redis architecture overview

A basic Redis deployment usually has:
- one primary node
- zero or more replicas
- optional persistence (RDB, AOF)
- optional Sentinel or Cluster for HA and scaling

### How Redis processes requests
Redis uses a mostly single-threaded command execution model:
- many client connections are multiplexed efficiently
- commands are executed one at a time
- this makes single-key operations atomic by default

### Why it is fast
- all data is in memory
- no heavy query planner
- most operations are O(1) or O(log N)
- simple internal structures

### Practical implication
Redis is excellent when access patterns are known ahead of time and you want predictable low latency.

---

## 3. When to choose Redis

### Choose Redis when you need
- sub-millisecond latency
- cache with TTL
- atomic counters
- rate limiting
- leaderboard/ranking
- session storage
- fast distributed coordination
- lightweight queues or event fanout

### Avoid Redis when you need
- complex queries and joins
- datasets larger than memory
- strong durability as the primary source of truth
- flexible ad hoc analytics
- deep relational integrity

### Common interview systems
- rate limiter
- session store
- leaderboard
- distributed cache
- real-time analytics
- distributed lock service
- queue / lightweight messaging

### Strong interview phrasing
A weak answer is: “I’ll use Redis because it’s fast.”

A strong answer is:
> I’ll use Redis because I need atomic increments with expiration for rate limiting. Redis gives me `INCR`, TTL, and Lua scripting for atomic conditional logic, which fits the access pattern directly.

---

## 4. Core Redis data structures

Choosing the right data structure is the main Redis skill.

---

## 4.1 Strings

Strings are the simplest Redis type. They can store:
- plain text
- JSON
- integers
- serialized blobs
- binary data

### Best for
- simple caching
- counters
- feature flags
- tokens
- lock keys

### Commands
```redis
SET user:123:name "John Doe"
GET user:123:name

SET counter 0
INCR counter
INCRBY counter 10

SETEX session:abc 3600 "user_data"
```

### Good use case
Cache a user profile JSON string for 10 minutes.

```redis
SETEX cache:user:123 600 '{"id":123,"name":"John"}'
GET cache:user:123
```

### Time complexity
- `GET` / `SET`: O(1)
- `INCR`: O(1)

### Trade-off
Strings are easy, but storing many related fields as separate keys can waste memory. Use Hashes when an object has many fields.

---

## 4.2 Hashes

Hashes store multiple field-value pairs under one key.

### Best for
- user profiles
- session attributes
- per-object fields
- counters per entity

### Commands
```redis
HSET user:123 name "John" email "john@example.com" age 30
HGET user:123 name
HGETALL user:123
HINCRBY user:123 age 1
```

### Good use case
Store session state:
```redis
HSET session:abc userId 123 role admin loginAt 1710000000
EXPIRE session:abc 3600
```

### Why Hashes help
Better memory efficiency than many tiny String keys.

### Time complexity
- single field read/write: O(1)
- `HGETALL`: O(N)

---

## 4.3 Lists

Lists maintain order and support push/pop from both ends.

### Best for
- queues
- recent activity feeds
- job dispatch
- notification stacks

### Commands
```redis
LPUSH notifications:user123 "New message"
LPUSH notifications:user123 "Friend request"
LRANGE notifications:user123 0 -1

RPUSH queue:jobs "job1"
LPOP queue:jobs

BRPOP queue:jobs 30
```

### FIFO queue pattern
```redis
RPUSH jobs "job1"
RPUSH jobs "job2"
LPOP jobs
```

### Worker pattern
Workers can block:
```redis
BRPOP jobs 30
```

### Time complexity
- push/pop ends: O(1)
- random access: O(N)

### Trade-off
Lists are fine for simple queues, but Streams are better when you need persistence and consumer groups.

---

## 4.4 Sets

Sets store unique unordered members.

### Best for
- unique tags
- unique visitors
- friend lists
- membership tests
- intersections / unions

### Commands
```redis
SADD tags:article123 "redis" "database" "nosql"
SMEMBERS tags:article123
SISMEMBER tags:article123 "redis"

SADD user:1:friends "alice" "bob"
SADD user:2:friends "bob" "charlie"
SINTER user:1:friends user:2:friends
```

### Good use case
Track unique users who liked a post:
```redis
SADD post:999:likes user1 user2 user3
SCARD post:999:likes
```

### Time complexity
- add/remove/check: O(1)
- `SMEMBERS`: O(N)

---

## 4.5 Sorted Sets (ZSets)

Sorted Sets are one of the most important Redis structures.

Each item is unique and has a numeric score.

### Best for
- leaderboards
- ranking
- priority queues
- time-based feeds
- sliding-window rate limiting

### Commands
```redis
ZADD leaderboard 1500 "alice"
ZADD leaderboard 1200 "bob"
ZADD leaderboard 900 "charlie"

ZRANGE leaderboard 0 -1 WITHSCORES
ZREVRANGE leaderboard 0 2 WITHSCORES
ZRANK leaderboard "bob"
ZINCRBY leaderboard 100 "charlie"
```

### Leaderboard example
```redis
ZADD game:leaderboard 1600 "player:alice"
ZINCRBY game:leaderboard 50 "player:alice"
ZREVRANGE game:leaderboard 0 9 WITHSCORES
ZREVRANK game:leaderboard "player:alice"
```

### Time complexity
- insert/update/rank: O(log N)
- range query: O(log N + M)

### Interview answer
For a leaderboard, use a Sorted Set where member = player ID and score = points. This gives atomic score updates and efficient top-N queries.

---

## 4.6 HyperLogLog

HyperLogLog gives approximate unique counts using fixed memory.

### Best for
- unique visitor estimation
- daily active user estimation
- analytics dashboards

### Commands
```redis
PFADD visitors:2024-01-15 "user1" "user2" "user3"
PFADD visitors:2024-01-15 "user1" "user4"
PFCOUNT visitors:2024-01-15

PFMERGE visitors:week visitors:2024-01-15 visitors:2024-01-16
```

### Trade-off
- tiny memory usage
- approximate count only
- standard error ~0.81%

### Rule
Use Set for exact counts. Use HyperLogLog for analytics-grade approximate counts.

---

## 4.7 Streams

Streams provide an append-only log inside Redis.

### Best for
- event logs
- reliable message processing
- consumer groups
- real-time pipelines

### Commands
```redis
XADD events * user_id 123 action "click" page "/home"
XREAD COUNT 10 STREAMS events 0
XREAD BLOCK 5000 STREAMS events $
```

### Consumer group example
```redis
XGROUP CREATE orders order_workers $ MKSTREAM
XREADGROUP GROUP order_workers worker1 COUNT 10 BLOCK 5000 STREAMS orders >
XACK orders order_workers 1710000000000-0
```

### Why Streams matter
Unlike Pub/Sub:
- messages are persisted
- consumers can acknowledge
- consumers can replay or recover pending messages

### Trade-off
Streams are stronger than Lists or Pub/Sub for reliable processing, but more complex.

---

## 4.8 Pub/Sub

Pub/Sub is simple broadcast messaging.

### Best for
- live notifications
- chat typing indicators
- dashboard push updates
- fire-and-forget events

### Commands
```redis
PUBLISH notifications:user123 '{"type":"message","from":"alice"}'
SUBSCRIBE notifications:user123
```

### Limitation
If the subscriber is disconnected, the message is lost.

### Rule
Use Pub/Sub for real-time signaling, not durable messaging.

---

## 4.9 Data structure quick guide

| Need | Data structure | Why |
|---|---|---|
| Simple cache | String | Fast + TTL |
| Object with fields | Hash | Field-level access |
| Queue | List | O(1) push/pop |
| Unique members | Set | Dedup |
| Leaderboard | Sorted Set | Score ordering |
| Approx unique count | HyperLogLog | Constant memory |
| Event log | Stream | Durable queueing |
| Broadcast | Pub/Sub | Simple push |

---

## 5. Persistence options

Redis is memory-first, but it can persist data for restart recovery.

---

## 5.1 RDB snapshots

RDB creates point-in-time snapshots.

### Config example
```conf
save 900 1
save 300 10
save 60 10000
```

### What it means
- save after 1 change in 900 sec
- or 10 changes in 300 sec
- or 10000 changes in 60 sec

### Good for
- backups
- faster restart
- caches where losing recent data is acceptable

### Trade-offs
- data loss between snapshots
- fork can cause latency/memory pressure on large datasets

### Interview answer
Use RDB when Redis is mostly a cache and losing a few minutes of data is acceptable.

---

## 5.2 AOF (Append Only File)

AOF logs every write command.

### Config example
```conf
appendonly yes
appendfsync everysec
```

### Sync modes
- `always`: safest, slowest
- `everysec`: common default
- `no`: OS-managed flushing, least safe

### Good for
- stronger durability
- minimizing data loss
- preserving write history

### Trade-offs
- more disk overhead
- slower restart if large
- file growth unless rewritten

### Interview answer
Use `AOF everysec` when you can tolerate about one second of data loss but need much stronger durability than snapshots alone.

---

## 5.3 AOF rewrite

AOF rewrite compacts the log.

### Before
```redis
SET counter 1
INCR counter
INCR counter
INCR counter
```

### After rewrite
```redis
SET counter 4
```

### Why it matters
It reduces file size and restart time.

---

## 5.4 Hybrid persistence

Use RDB + AOF together.

### Config
```conf
appendonly yes
appendfsync everysec
aof-use-rdb-preamble yes
```

### Why this is often best
- fast restarts from RDB preamble
- recent writes preserved in AOF tail

### Practical rule
For production Redis that holds important-but-not-primary data, hybrid persistence is often the best compromise.

---

## 6. Replication and high availability

A single Redis node is a single point of failure.

---

## 6.1 Primary-replica replication

One primary handles writes. Replicas copy data asynchronously.

### Replica config
```conf
replicaof <master-ip> <master-port>
```

### Good for
- redundancy
- read scaling
- failover candidates

### Trade-off
Replication is usually asynchronous, so recent writes can be lost if the primary crashes before replicas catch up.

---

## 6.2 WAIT for stronger replication guarantees

Use `WAIT` selectively when a write must reach replicas before returning.

### Example
```redis
SET critical:key "value"
WAIT 2 5000
```

### Meaning
Wait until 2 replicas acknowledge or 5 seconds pass.

### Trade-off
Higher latency.

### Rule
Use `WAIT` only for critical writes, not as a blanket policy.

---

## 6.3 Redis Sentinel

Sentinel provides:
- health monitoring
- automatic failover
- master discovery

### Example config
```conf
sentinel monitor mymaster 127.0.0.1 6379 2
sentinel down-after-milliseconds mymaster 5000
sentinel failover-timeout mymaster 60000
sentinel parallel-syncs mymaster 1
```

### What happens on failure
- Sentinels detect primary failure
- quorum agrees
- best replica is promoted
- clients discover new primary

### Good for
- HA when data fits on one primary node
- simpler than Redis Cluster

### Interview answer
Use Sentinel when one primary can hold the working set, but you need automatic failover.

---

## 7. Clustering and scaling

Sentinel gives HA. Redis Cluster gives HA + sharding.

---

## 7.1 Redis Cluster basics

Redis Cluster partitions data into 16384 hash slots.

### Slot formula
```text
slot = CRC16(key) mod 16384
```

Each key maps to a slot, and slots are distributed across masters.

### Good for
- data larger than one node’s memory
- higher total throughput
- horizontal scaling

### Trade-offs
- more operational complexity
- multi-key ops only work when keys are in same slot
- transactions/scripts are limited to same-slot keys

---

## 7.2 MOVED redirects

Clients may be redirected if they send a key to the wrong node.

### Example
```redis
GET user:123
-MOVED 14785 192.168.1.3:6379
```

Smart Redis clients handle this automatically.

---

## 7.3 Hash tags

Use hash tags to force related keys into the same slot.

### Bad
```redis
MGET user:123:profile user:123:settings
```

### Good
```redis
MGET {user:123}:profile {user:123}:settings
```

Only the text inside `{}` is hashed, so both keys land on the same slot.

### Why this matters
It enables:
- multi-key reads
- transactions
- Lua scripts
- atomic workflows across related keys

---

## 7.4 Sentinel vs Cluster

| Factor | Sentinel | Cluster |
|---|---|---|
| Main goal | HA | HA + horizontal scale |
| Data size | Single primary memory | Aggregate cluster memory |
| Complexity | Lower | Higher |
| Multi-key flexibility | Full | Same-slot only |
| Minimum footprint | Lower | Higher |

### Rule
- use Sentinel when one primary is enough
- use Cluster when you need sharding

---

## 8. Memory management

Memory is Redis’s main constraint.

### Config example
```conf
maxmemory 4gb
maxmemory-policy allkeys-lru
```

---

## 8.1 Eviction policies

| Policy | Meaning |
|---|---|
| `noeviction` | reject writes when full |
| `allkeys-lru` | evict least recently used key |
| `allkeys-lfu` | evict least frequently used key |
| `volatile-lru` | evict only keys with TTL |
| `volatile-ttl` | evict shortest-TTL keys first |
| `allkeys-random` | random eviction |

### Practical guidance
- cache-only Redis: `allkeys-lru` or `allkeys-lfu`
- mixed store with non-cache keys: be careful with `allkeys-*`
- if only cache keys have TTL: `volatile-lru` works well

---

## 8.2 LRU vs LFU

### LRU
Evicts least recently used keys.

Good for:
- most caches
- recency-based access

### LFU
Evicts least frequently used keys.

Good for:
- stable hot keys
- avoiding cache pollution from one-time scans

### Rule
Start with LRU unless you know LFU solves a specific problem.

---

## 8.3 Memory optimization techniques

### Use Hashes instead of many small Strings
```redis
HSET user:1 name "John" email "john@example.com" age "30"
```

### Use shorter keys in production
```redis
SET u:p:123:llt "2024-01-15"
```

### Set TTL on cache keys
```redis
SET cache:product:123 "{...}" EX 3600
```

### Compress large values before storing
Store compressed JSON or binary blobs when needed.

### Monitor memory
```redis
INFO memory
MEMORY USAGE mykey
MEMORY DOCTOR
```

---

## 9. Common Redis patterns

This is where Redis becomes powerful in interviews.

---

## 9.1 Distributed locking

### Basic lock acquire
```redis
SET lock:resource123 "owner_id" NX EX 30
```

### Why it works
- `NX` = only set if missing
- `EX 30` = auto-expire in 30 sec

### Safe release with Lua
```lua
if redis.call("get", KEYS[1]) == ARGV[1] then
    return redis.call("del", KEYS[1])
else
    return 0
end
```

### Rule
Never delete a lock blindly. Delete only if the stored owner matches the caller.

### Interview note
A single-node Redis lock is often enough for moderate coordination tasks. Mention Redlock only when stronger distributed guarantees are explicitly needed.

---

## 9.2 Rate limiting

This is the most common Redis interview problem.

### Fixed window
```redis
INCR rate:user123:202401151030
EXPIRE rate:user123:202401151030 60
GET rate:user123:202401151030
```

### Problem
Boundary burst: users can hit the limit at the end of one window and again at the start of the next.

### Sliding window with Sorted Set
```redis
ZADD rate:user123 1710000001000 req1
ZREMRANGEBYSCORE rate:user123 0 1709999941000
ZCARD rate:user123
EXPIRE rate:user123 60
```

### Why it is better
Counts only requests inside the real rolling window.

### Interview answer
Use a Sorted Set where score = timestamp and member = request ID. Remove old requests and count current-window requests atomically, ideally with Lua.

---

## 9.3 Leaderboard

### Commands
```redis
ZADD leaderboard 1500 "player:alice"
ZINCRBY leaderboard 100 "player:alice"
ZREVRANGE leaderboard 0 9 WITHSCORES
ZREVRANK leaderboard "player:alice"
ZSCORE leaderboard "player:alice"
```

### Why Redis is perfect here
- rank queries are built in
- score updates are atomic
- top N reads are efficient

### Design note
If you need a leaderboard per game:
```redis
ZADD leaderboard:game:42 1500 "player:alice"
```

---

## 9.4 Session store

### Commands
```redis
HSET session:abc123 user_id 456 role admin created_at "2024-01-15"
EXPIRE session:abc123 3600
HGETALL session:abc123
DEL session:abc123
```

### Why Redis fits
- every request needs fast lookup
- inactive sessions expire automatically
- data is small and hot

### Advanced pattern
Track sessions per user:
```redis
SADD user:456:sessions "abc123" "def456"
```

Useful for “log out all sessions”.

---

## 9.5 Cache-aside pattern

### Flow
1. check Redis
2. on miss, read DB
3. write into Redis with TTL
4. return response

### Pseudocode
```python
def get_user(user_id):
    cached = redis.get(f"user:{user_id}")
    if cached:
        return json.loads(cached)

    user = db.query("SELECT * FROM users WHERE id = ?", user_id)
    redis.setex(f"user:{user_id}", 3600, json.dumps(user))
    return user
```

### Good for
- most read-heavy APIs
- product pages
- user profiles
- catalog data

### Risk
Stale cache. You need invalidation strategy.

---

## 9.6 Queueing

### Simple List queue
```redis
RPUSH queue:jobs "job1"
BRPOP queue:jobs 30
```

### Better: Stream queue
```redis
XADD jobs * type email to user@example.com
XREADGROUP GROUP workers w1 COUNT 1 BLOCK 5000 STREAMS jobs >
XACK jobs workers 1710000000000-0
```

### Rule
Use Lists for very simple queues. Use Streams when you need reliability and acking.

---

## 9.7 Transactions and Lua

---

## 9.7.1 MULTI / EXEC

### Example
```redis
MULTI
SET balance:alice 100
SET balance:bob 200
INCR transfer_count
EXEC
```

### What it guarantees
Commands execute as a batch without interleaving.

### What it does not give
- no rollback
- no full SQL-style ACID behavior
- queued commands, not conditional logic by themselves

---

## 9.7.2 WATCH

### Example
```redis
WATCH balance:alice
GET balance:alice

MULTI
DECRBY balance:alice 100
INCRBY balance:bob 100
EXEC
```

If the watched key changes before `EXEC`, the transaction aborts.

### Best for
Optimistic concurrency with retry logic.

---

## 9.7.3 Lua scripting

Lua is the best answer when you need:
- read
- decide
- write
- all atomically

### Example transfer script
```lua
local from_balance = tonumber(redis.call('GET', KEYS[1])) or 0
local amount = tonumber(ARGV[1])

if from_balance >= amount then
    redis.call('DECRBY', KEYS[1], amount)
    redis.call('INCRBY', KEYS[2], amount)
    return 1
else
    return 0
end
```

### Run it
```redis
EVAL "<script>" 2 balance:alice balance:bob 100
```

### Why Lua is powerful
- one round trip
- no race conditions
- atomic conditional logic

### Rule
For complex atomic logic in Redis, Lua is usually the cleanest solution.

---

## 10. Redis vs alternatives

### Redis vs Memcached
Use Redis when:
- you need more than plain caching
- you need TTL + counters + sets + sorted sets
- you want persistence or replication

Use Memcached when:
- you only need very simple key-value caching
- throughput on a simple string cache is the only goal

### Redis vs DynamoDB
Use Redis when:
- latency is critical
- data is transient or derived
- it is a cache / rate limiter / session store

Use DynamoDB when:
- data must be durable
- it is your primary source of truth
- you need managed scale with persistence

### Redis vs Kafka
Use Redis when:
- you need lightweight real-time messaging
- queueing is moderate scale
- low latency matters more than long retention

Use Kafka when:
- you need event streaming at large scale
- replay matters
- long-term retention matters
- throughput is massive

---

## 11. Design checklist for interviews

When proposing Redis, mention:
- what data structure you will use
- why it matches the access pattern
- whether Redis is primary or derived storage
- what happens on restart/failure
- how persistence is configured
- whether HA uses Sentinel or Cluster
- what eviction policy is safe
- how stale/missing data is handled

### Strong interview format
> I would use Redis Sorted Sets for the leaderboard because score updates and rank lookups are both efficient. I would keep Redis as a derived store, rebuildable from the primary database if needed. For HA, I’d use one primary with two replicas under Sentinel. If the dataset grows beyond one node’s memory, I’d move to Redis Cluster and use hash tags for same-user multi-key operations.

---

## 12. Spring Boot example project

This section gives you a working model project structure that uses Redis for:
- cache
- rate limiting
- leaderboard
- session-like token storage
- distributed lock
- Lua-based atomic logic

---

## 12.1 Project structure

```text
redis-demo/
 ├─ pom.xml
 ├─ src/main/java/com/example/redis/
 │   ├─ RedisDemoApplication.java
 │   ├─ config/RedisConfig.java
 │   ├─ controller/UserController.java
 │   ├─ controller/LeaderboardController.java
 │   ├─ controller/RateLimitController.java
 │   ├─ service/UserCacheService.java
 │   ├─ service/LeaderboardService.java
 │   ├─ service/RateLimiterService.java
 │   ├─ service/LockService.java
 │   └─ model/UserProfile.java
 └─ src/main/resources/
     └─ application.yml
```

---

## 12.2 `pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>redis-demo</artifactId>
    <version>1.0.0</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.2</version>
    </parent>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
</project>
```

---

## 12.3 `application.yml`

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2000ms

server:
  port: 8080
```

---

## 12.4 `RedisDemoApplication.java`

```java
package com.example.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedisDemoApplication.class, args);
    }
}
```

---

## 12.5 `RedisConfig.java`

```java
package com.example.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
```

---

## 12.6 `UserProfile.java`

```java
package com.example.redis.model;

public class UserProfile {
    private Long id;
    private String name;
    private String email;

    public UserProfile() {}

    public UserProfile(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
```

---

## 12.7 `UserCacheService.java`

Cache-aside example.

```java
package com.example.redis.service;

import com.example.redis.model.UserProfile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UserCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public UserCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public UserProfile getUser(Long userId) {
        String key = "user:" + userId;

        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof UserProfile userProfile) {
            return userProfile;
        }

        UserProfile fromDb = fakeDbLookup(userId);
        redisTemplate.opsForValue().set(key, fromDb, Duration.ofMinutes(10));
        return fromDb;
    }

    public void evictUser(Long userId) {
        redisTemplate.delete("user:" + userId);
    }

    private UserProfile fakeDbLookup(Long userId) {
        return new UserProfile(userId, "User-" + userId, "user" + userId + "@example.com");
    }
}
```

---

## 12.8 `LeaderboardService.java`

Sorted Set example.

```java
package com.example.redis.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Service
public class LeaderboardService {

    private static final String KEY = "leaderboard:global";

    private final StringRedisTemplate redisTemplate;

    public LeaderboardService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addScore(String playerId, double scoreDelta) {
        redisTemplate.opsForZSet().incrementScore(KEY, playerId, scoreDelta);
    }

    public Map<String, Double> top(int limit) {
        Set<TypedTuple<String>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(KEY, 0, limit - 1);

        Map<String, Double> result = new LinkedHashMap<>();
        if (tuples != null) {
            for (TypedTuple<String> tuple : tuples) {
                result.put(tuple.getValue(), tuple.getScore());
            }
        }
        return result;
    }

    public Long rank(String playerId) {
        return redisTemplate.opsForZSet().reverseRank(KEY, playerId);
    }
}
```

---

## 12.9 `RateLimiterService.java`

Sliding window rate limiter using Sorted Set.

```java
package com.example.redis.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean allow(String userId, int limit, Duration window) {
        String key = "rate:" + userId;
        long now = Instant.now().toEpochMilli();
        long windowStart = now - window.toMillis();

        redisTemplate.opsForZSet().add(key, UUID.randomUUID().toString(), now);
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);
        Long count = redisTemplate.opsForZSet().zCard(key);
        redisTemplate.expire(key, window);

        return count != null && count <= limit;
    }
}
```

### Interview note
This service is simple but not perfectly atomic. In production, use a Lua script for strict correctness under concurrency.

---

## 12.10 `LockService.java`

Distributed lock example.

```java
package com.example.redis.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Service
public class LockService {

    private final StringRedisTemplate redisTemplate;

    public LockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String acquire(String resource, Duration ttl) {
        String key = "lock:" + resource;
        String owner = UUID.randomUUID().toString();

        Boolean ok = redisTemplate.opsForValue().setIfAbsent(key, owner, ttl);
        return Boolean.TRUE.equals(ok) ? owner : null;
    }

    public boolean release(String resource, String owner) {
        String key = "lock:" + resource;

        String lua = """
                if redis.call('get', KEYS[1]) == ARGV[1] then
                    return redis.call('del', KEYS[1])
                else
                    return 0
                end
                """;

        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(lua, Long.class),
                Collections.singletonList(key),
                owner
        );

        return result != null && result == 1L;
    }
}
```

---

## 12.11 Controllers

### `UserController.java`
```java
package com.example.redis.controller;

import com.example.redis.model.UserProfile;
import com.example.redis.service.UserCacheService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserCacheService userCacheService;

    public UserController(UserCacheService userCacheService) {
        this.userCacheService = userCacheService;
    }

    @GetMapping("/{id}")
    public UserProfile getUser(@PathVariable Long id) {
        return userCacheService.getUser(id);
    }

    @DeleteMapping("/{id}/cache")
    public void evict(@PathVariable Long id) {
        userCacheService.evictUser(id);
    }
}
```

### `LeaderboardController.java`
```java
package com.example.redis.controller;

import com.example.redis.service.LeaderboardService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @PostMapping("/{playerId}/score")
    public void addScore(@PathVariable String playerId, @RequestParam double delta) {
        leaderboardService.addScore(playerId, delta);
    }

    @GetMapping("/top")
    public Map<String, Double> top(@RequestParam(defaultValue = "10") int limit) {
        return leaderboardService.top(limit);
    }

    @GetMapping("/{playerId}/rank")
    public Long rank(@PathVariable String playerId) {
        return leaderboardService.rank(playerId);
    }
}
```

### `RateLimitController.java`
```java
package com.example.redis.controller;

import com.example.redis.service.RateLimiterService;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/rate-limit")
public class RateLimitController {

    private final RateLimiterService rateLimiterService;

    public RateLimitController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/{userId}")
    public Map<String, Object> check(@PathVariable String userId) {
        boolean allowed = rateLimiterService.allow(userId, 5, Duration.ofMinutes(1));
        return Map.of("userId", userId, "allowed", allowed);
    }
}
```

---

## 12.12 Redis commands to test manually

### Cache
```redis
GET user:1
TTL user:1
```

### Leaderboard
```redis
ZREVRANGE leaderboard:global 0 9 WITHSCORES
ZREVRANK leaderboard:global player:alice
```

### Rate limiting
```redis
ZRANGE rate:user123 0 -1 WITHSCORES
ZCARD rate:user123
```

### Lock
```redis
GET lock:resource1
```

---

## 12.13 Production improvements

To make this project stronger in production:
- use Lua for atomic rate limiting
- use explicit serializers and versioned payloads
- add metrics and tracing
- configure connection pooling
- set timeouts carefully
- add fallback behavior when Redis is unavailable
- avoid treating Redis as the only copy of critical data
- add Sentinel or Cluster-aware client configuration

---

## 13. Final mental model

### Redis is best when
- access patterns are simple and known
- speed matters more than rich querying
- data is small enough to fit in memory
- atomic counters / rankings / coordination are needed

### Redis becomes dangerous when
- you treat it like a general-purpose database
- you ignore durability trade-offs
- you exceed memory without an eviction strategy
- you rely on cross-key operations in Cluster without hash tags
- you store critical data with no durable source of truth

### Core design rules
1. choose the data structure first
2. keep Redis derived when possible
3. match persistence to the cost of data loss
4. use Sentinel for HA, Cluster for scale
5. think about memory before production does it for you
6. use Lua when correctness depends on read-decide-write atomics

---

## 14. Quick interview recap

If asked, “How would you use Redis in a scalable system?” a strong short answer is:

> I would use Redis only for the parts of the system that need very low latency or atomic in-memory operations, such as caching, rate limiting, leaderboards, sessions, and lightweight coordination. I would choose the data structure based on the access pattern: Strings for cache, Hashes for session objects, Sorted Sets for leaderboards and sliding-window rate limiting, and Streams for durable queue-like workflows. For HA on a single shard, I’d use one primary with replicas and Sentinel. If the dataset or throughput outgrows one node, I’d move to Redis Cluster and use hash tags for related multi-key operations. I would also explicitly choose persistence and eviction policies based on how much data loss and cache eviction the use case can tolerate.
