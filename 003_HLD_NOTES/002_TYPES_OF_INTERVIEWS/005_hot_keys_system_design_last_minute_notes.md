# Hot Keys in System Design – Last-Minute Notes

> Fast interview notes for handling **hot keys**: when one key gets a disproportionate amount of traffic and breaks horizontal scaling.

---

## Why Hot Keys Matter
A distributed system may have **100 nodes**, but a single hot key can reduce effective capacity to **1 node**.

Example:
- Cristiano Ronaldo posts to **650M+ followers**
- everyone requests the same `post:12345`
- one cache/database shard gets hammered
- other nodes stay mostly idle

### Core problem
```text
Traffic is not evenly distributed anymore.
One key becomes the bottleneck.
```

---

## Where This Shows Up
- Viral tweets / celebrity posts
- Flash sale product pages
- Ticket release inventory
- Live stream metadata
- Trending topics
- Global rate limit counters
- Breaking news articles

---

# 1) Understanding Hot Keys

## What is a hot key?
A **hot key** is a key that gets far more traffic than others.

Normal:
```text
Node1 8K/s | Node2 9K/s | Node3 7K/s | Node4 8K/s
```

With hot key:
```text
Node1 2K/s | Node2 50K/s | Node3 2K/s | Node4 2K/s
```

### Why scaling breaks
If the hot key lives on one node:
- adding more nodes does **not** help
- that node still owns the key
- the bottleneck remains single-node

---

## Why hot keys are dangerous
They often trigger **cascading failures**:

1. One node overloads
2. Latency spikes
3. Clients retry
4. Load doubles
5. Node crashes
6. Failover node gets overloaded too

### Signal to watch
```text
Cluster average looks fine,
but one node is drowning.
```

---

## Hot keys can happen at multiple layers

| Layer | Example Hot Key | Impact |
|---|---|---|
| CDN | viral image / video | edge cache overload, origin hammered |
| Cache | celebrity post / product page | one Redis node saturated |
| Database | popular user/profile/topic | one shard overloaded |
| Queue | one partition/channel | backlog on single partition |
| Rate limiter | global counter | counter becomes bottleneck |

---

## Predictable vs Unpredictable hot keys

### Predictable
- flash sale
- ticket release
- product launch
- scheduled celebrity event

### Unpredictable
- viral post
- breaking news
- meme/trending topic
- random celebrity mention

### Why it matters
- predictable → prepare in advance
- unpredictable → detect + adapt automatically

---

# 2) Detecting Hot Keys

## A) Proactive detection
Monitor key access frequency continuously.

### Redis monitor example
```bash
redis-cli MONITOR | head -1000
```

Example pattern:
```text
GET tweet:12345
GET tweet:12345
GET tweet:12345
GET user:789
GET tweet:12345
```

### Redis hotkeys sampling
```bash
redis-cli --hotkeys
```

Example:
```text
Hot key 'tweet:12345' found so far with counter 47892
Hot key 'product:567' found so far with counter 12456
```

---

## B) Reactive detection
Detect imbalance when a hot key is already hurting the system.

### Symptoms
- one node CPU much higher than others
- one node has much higher P99 latency
- one node has much higher network I/O
- one node has more cache evictions

| Metric | Normal | Hot Key Pattern |
|---|---|---|
| CPU | similar across nodes | one node much higher |
| P99 latency | similar | one node spikes |
| Network I/O | distributed | one node saturated |
| Evictions | low and even | high on one node |

### Alert example
```text
If any cache node CPU > 70%
AND cluster average CPU < 30%
=> likely hot key / load imbalance
```

---

## C) Predictive detection
For known events:
- identify hot keys before traffic starts
- pre-warm data
- pre-replicate keys
- dedicate resources
- prepare the on-call plan

---

# 3) Solution Patterns

# 3.1 Local Caching

## Idea
Cache hot data in **application server memory** so not every request hits Redis.

```python
def get_data(key):
    if local_cache.has(key):
        return local_cache.get(key)

    value = redis.get(key)

    if value and is_likely_hot(key):
        local_cache.set(key, value, ttl=5)   # 5 sec TTL

    return value
```

## Why it helps
If you have 100 app servers, you now have 100 local caches.

### Pros
- huge reduction in Redis load
- very fast reads
- simple to add
- scales with app server count

### Cons
- stale data
- memory overhead
- inconsistent reads across servers
- TTL tuning needed

### Best for
- tweets
- user profiles
- article content
- product descriptions

Rule:
```text
Use local cache first when reads dominate
and slight staleness is acceptable.
```

---

# 3.2 Key Replication

## Idea
Store the same hot key on multiple cache nodes and spread reads.

```python
NUM_REPLICAS = 5

def set_hot_key(key, value):
    for i in range(NUM_REPLICAS):
        replica_key = f"{key}:replica:{i}"
        redis.set(replica_key, value)

def get_hot_key(key):
    replica_num = random.randint(0, NUM_REPLICAS - 1)
    replica_key = f"{key}:replica:{replica_num}"
    return redis.get(replica_key)
```

## Why it helps
- 5 replicas → ~5x more read capacity for that key

### Pros
- keeps data fresh/consistent across reads
- predictable scaling
- no per-app memory dependency

### Cons
- write amplification
- more storage
- must invalidate/update all replicas
- more key management complexity

### Best for
- celebrity profile
- product details
- config data
- high read / low write keys

---

# 3.3 Key Splitting (Sharded Counters)

## Idea
For write-heavy hot keys, split one logical key into many shards.

```python
NUM_SHARDS = 100

def increment_counter(base_key):
    shard_id = random.randint(0, NUM_SHARDS - 1)
    shard_key = f"{base_key}:{shard_id}"
    redis.incr(shard_key)

def get_counter(base_key):
    total = 0
    for shard_id in range(NUM_SHARDS):
        shard_key = f"{base_key}:{shard_id}"
        total += int(redis.get(shard_key) or 0)
    return total
```

## Why it helps
- writes spread across many shards
- avoids one-node bottleneck

### Pros
- scales write-heavy counters
- simple mental model
- linear improvement with more shards

### Cons
- reads become expensive (aggregate many keys)
- more client logic
- no perfect atomic full read
- ordering can get harder

### Best for
- like counters
- view counters
- rate limit counters
- append-heavy metrics

---

# 3.4 Request Coalescing

## Idea
When many requests arrive for the same missing/expired key, perform **one fetch**, not 1000.

```python
class Singleflight:
    in_flight = {}

    async def get(self, key, fetch_fn):
        if key in self.in_flight:
            return await self.in_flight[key]

        promise = create_promise()
        self.in_flight[key] = promise

        try:
            result = await fetch_fn(key)
            promise.resolve(result)
            return result
        finally:
            del self.in_flight[key]
```

## Why it helps
- prevents cache stampede
- avoids many identical DB/cache fetches

### Pros
- great for bursts
- no extra storage
- transparent to callers

### Cons
- only helps concurrent requests
- often server-local only
- not enough for sustained hot traffic

### Best for
- cache miss storms
- cold cache warming
- bursty traffic

---

# 3.5 Read-Through Cache with Locking

## Idea
Only one process refreshes the key. Others wait or retry.

```python
def get_with_lock(key):
    value = cache.get(key)
    if value:
        return value

    lock_key = f"lock:{key}"
    acquired = redis.set(lock_key, "1", nx=True, ex=5)

    if acquired:
        try:
            value = database.query(key)
            cache.set(key, value, ttl=3600)
            return value
        finally:
            redis.delete(lock_key)
    else:
        sleep(0.05)
        return get_with_lock(key)
```

## Why it helps
- protects DB from stampede
- coordinates across servers

### Pros
- strong protection on cache miss
- useful for expensive DB reads

### Cons
- waiting adds latency
- lock handling complexity
- must avoid deadlock / bad retries

### Best for
- expensive queries
- coordinated cache refill
- cross-server cache misses

---

# 3.6 Per-Key Rate Limiting

## Idea
If one key is too hot, cap the damage.

```python
MAX_REQUESTS_PER_KEY = 10000

def get_with_rate_limit(key):
    rl_key = f"ratelimit:{key}"
    count = redis.incr(rl_key)

    if count == 1:
        redis.expire(rl_key, 1)

    if count > MAX_REQUESTS_PER_KEY:
        stale = local_cache.get(key)
        if stale:
            return stale
        raise Exception("RateLimitExceeded")

    return fetch_data(key)
```

## Why it helps
- prevents one key from taking down the cluster

### Pros
- simple safeguard
- graceful degradation possible

### Cons
- some users get stale data or errors
- does not fix root cause
- requires per-key tuning

### Best for
- final safety layer
- overload protection
- combined with other strategies

---

# 4) Choosing the Right Solution

| Scenario | Primary Solution | Secondary |
|---|---|---|
| Viral tweet (read-heavy, stale OK) | Local cache | Key replication |
| Flash sale inventory (write-heavy) | Key splitting | Approx counters / queue |
| Live score (read + write) | Local cache + write-behind | Key replication |
| Cache stampede | Request coalescing | Locking |
| Celebrity profile (fresh reads) | Key replication | CDN |
| Global rate limiter | Key splitting | Sliding window |

### Quick rule
```text
Read-heavy + stale OK      -> local cache
Read-heavy + fresh needed  -> key replication
Write-heavy counter        -> key splitting
Bursty cache miss          -> coalescing / locking
Overload risk              -> rate limiting
```

---

# 5) Combine Strategies (Real Systems)

In production, use **layers of defense**:

## Example stack for a viral tweet
### Layer 1: CDN
- images, videos, thumbnails
- anonymous API caching if possible

### Layer 2: Local app cache
- 5 sec TTL
- absorbs most repeated reads

### Layer 3: Replicated Redis
- hot keys replicated to multiple nodes
- read from random replica

### Layer 4: Database
- read replica for hot content
- locking for expensive misses

### Layer 5: Protection
- per-user rate limits
- per-key rate limits
- serve stale if needed
- circuit breaker if backend is unhealthy

### Mental model
```text
CDN -> Local Cache -> Replicated Cache -> DB -> Rate Limit / Degrade
```

---

# 6) Interview Answer Template

```text
A hot key happens when traffic concentrates on one key, so one node becomes the bottleneck even though the cluster has spare capacity elsewhere.

I’d first detect it through load imbalance, top-key monitoring, and node-level latency/CPU differences.

For read-heavy hot keys, I’d start with local caching because it is simple and can reduce distributed cache traffic dramatically.
If freshness matters more, I’d replicate the hot key across multiple cache nodes and load-balance reads.

For write-heavy hot keys like counters, I’d split the key into many shards and aggregate on read.
To prevent cache stampedes, I’d add request coalescing or distributed locking.
Finally, I’d protect the system with per-key rate limiting and graceful degradation.
```

---

# 7) Polished Key Takeaways

- **Hot keys break horizontal scaling**  
  Even with many nodes, one hot key can bottleneck the whole system on a single node.

- **Detection comes before mitigation**  
  Watch for node imbalance, top-key frequency, P99 spikes, network saturation, and eviction patterns.

- **Local caching is often the fastest first fix**  
  A short in-process TTL can remove most repeated requests before they ever hit Redis.

- **Replicate read-heavy hot keys**  
  When freshness matters, spread reads across multiple replicas of the same key.

- **Split write-heavy hot keys**  
  Counters and high-write keys should be sharded to distribute update load.

- **Prevent stampedes explicitly**  
  Request coalescing and locking stop many clients from triggering the same expensive fetch.

- **Layer your defenses**  
  Real systems combine CDN, local cache, replicated cache, and rate limiting—not just one trick.

- **Prepare for predictable events, adapt to unexpected ones**  
  Flash sales can be pre-warmed. Viral content requires fast automatic detection and mitigation.

- **Graceful degradation beats outage**  
  Serving stale data or partial results is far better than letting one hot key take down the platform.

---

## Final 1-Line Shortcut
```text
Detect -> Local Cache -> Replicate/Split -> Coalesce -> Protect
```
