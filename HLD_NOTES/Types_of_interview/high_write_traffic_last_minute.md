# High Write Traffic in System Design – Last-Minute Notes

> Fast revision notes for interviews: **why writes are hard, how to scale them, and what to say clearly**.

---

## Why Writes Are Hard
Unlike reads, **writes cannot be cached away**.

- Every write must eventually hit **persistent storage**
- Writes need **durability**
- Writes often need **ordering / consistency**
- A single primary or storage layer becomes the bottleneck

### Core asymmetry
```text
Reads  -> scale with cache + replicas
Writes -> must hit storage, harder to scale
```

---

## Where This Shows Up
- Logging systems
- Analytics / clickstream
- Uber / Lyft location updates
- IoT platforms
- Metrics / monitoring
- Trading systems

---

# 1) Understand the Write Problem

## 1.1 Scale
Example:
- 10M devices
- each sends data every 5 sec
- payload = 500 bytes

```text
10M / 5 = 2M writes/sec
2M × 500B = 1 GB/sec ingestion
≈ 86 TB/day
```

A traditional SQL DB cannot handle this alone.

---

## 1.2 Durability vs Performance
Faster acknowledgment usually means weaker durability.

| Level | Method | Latency | Survives |
|---|---|---:|---|
| 1 | Memory only | ~1μs | nothing |
| 2 | Async disk | ~100μs | process crash |
| 3 | Sync disk (`fsync`) | ~1–10ms | machine crash |
| 4 | Sync + replica ack | ~10–50ms | machine loss |
| 5 | Multi-region sync | ~50–200ms | datacenter loss |

### Rule
- Metrics/logs may accept **lower durability**
- Finance/trading needs **strong durability**

---

# 2) Write Scaling Toolkit

## Strategy order
```text
Batch -> Queue -> Shard -> Write-optimized DB -> CQRS/Event Sourcing -> Backpressure
```

| Technique | Use |
|---|---|
| Batching | Reduce per-write overhead |
| Async processing | Decouple ingestion from persistence |
| Sharding | Scale horizontally |
| Write-optimized storage | Better write throughput |
| CQRS / Event sourcing | Separate write and read concerns |
| Backpressure / rate limiting | Prevent overload |

---

# 3) Batching and Buffering

## Why batching works
One row at a time is expensive:
- network round trip
- parse
- transaction overhead
- commit / fsync

Batching pays that overhead once for many rows.

### Mental model
```text
1000 individual writes  >>  1 batched write of 1000 rows
```

---

## Client-side batching
Flush when:
- batch size reached
- max wait time reached

```python
class WriteBuffer:
    buffer = []
    maxSize = 1000
    maxWaitMs = 100
    lastFlush = now()

    def write(self, data):
        self.buffer.append(data)
        if len(self.buffer) >= self.maxSize or (now() - self.lastFlush) >= self.maxWaitMs:
            self.flush()

    def flush(self):
        if not self.buffer:
            return
        batch = self.buffer
        self.buffer = []
        self.lastFlush = now()
        database.bulkInsert(batch)
```

### DB-side batching
```sql
-- Better than individual INSERTs
INSERT INTO events (user_id, event_type, timestamp) VALUES
  (1, 'click', now()),
  (2, 'view', now()),
  (3, 'purchase', now());

-- Fastest in PostgreSQL for bulk load
COPY events FROM STDIN WITH (FORMAT csv);
```

---

## Write-behind pattern
Write fast to memory / Redis, persist later asynchronously.

```text
App -> Redis buffer -> Background worker -> Database
```

### Pros
- ultra-fast ingestion
- absorbs spikes

### Cons
- risk of data loss before persistence

**Best for:** metrics, analytics, logs  
**Not for:** payments, balances, trades

---

# 4) Asynchronous Processing

## Core pattern
```text
Client/API -> Queue -> Consumers -> Database
```

### Why it helps
- absorbs spikes
- decouples acceptance from processing
- lets consumers scale independently
- supports retries

### Queue options

| Queue | Best For |
|---|---|
| Kafka | very high throughput, partitioned streams |
| RabbitMQ | low-latency messaging, routing |
| SQS | managed/serverless |
| Redis Streams | simple use cases |

---

## Consumer patterns

### Competing consumers
- each message handled by one worker
- easy horizontal scale

### Partitioned consumers
- partition by key (e.g. `user_id`)
- preserves order **within partition**
- scales by adding partitions

---

## Idempotency for retries
Distributed systems will produce duplicates. Make writes safe to retry.

```python
def process_event(event):
    if database.exists("processed_events", event.id):
        return "OK"   # already handled

    database.insert("events", event.data)
    database.insert("processed_events", event.id)
    return "OK"
```

### Rule
```text
Same message processed twice -> same final result
```

---

# 5) Write-Optimized Storage

## B-Tree vs LSM Tree

### B-Tree (PostgreSQL/MySQL)
- good reads
- random I/O on writes
- mixed workload friendly

### LSM Tree (Cassandra/Scylla/RocksDB)
- appends + compaction
- much better write throughput
- reads can be slower

| Type | Best For |
|---|---|
| PostgreSQL / MySQL | transactions, mixed workloads |
| Cassandra / ScyllaDB | high-volume writes, time-series, IoT |
| ClickHouse | analytics/logs |
| InfluxDB | metrics/time-series |

---

## Append-only log
Fastest write pattern:
```text
new write -> append to end of log
```

Used by:
- Kafka
- WAL
- event stores

### Trade-off
- writes are extremely fast
- reads need indexes or projections

---

# 6) Sharding for Writes

## Core idea
```text
Distribute writes across multiple databases
```

Example:
```text
user_id % 4 -> shard 0..3
```

### Benefit
4 shards ≈ 4x write capacity

---

## Choosing shard key

| Key | Good | Risk |
|---|---|---|
| user_id | keeps user data together | viral users can create hot shards |
| timestamp | easy time queries | all current writes hit newest shard |
| random/UUID | even distribution | related data scattered |
| composite key | balanced | more routing complexity |

### Rule
Choose a key that:
- spreads writes evenly
- avoids hot shards
- keeps related data together when possible

---

## Consistent hashing
Use it to reduce data movement when shards are added/removed.

### Why
Naive modulo reshuffles too much data.  
Consistent hashing moves only a smaller fraction.

---

## Cross-shard problem
Some operations touch multiple shards.

Example:
```text
Transfer money:
debit on shard A
credit on shard B
```

Solutions:
- avoid cross-shard writes via data modeling
- Saga pattern
- 2PC only when absolutely necessary

---

# 7) Event Sourcing and CQRS

## Event sourcing
Store **events**, not just current state.

```text
Deposit +100
Withdraw -20
Deposit +70
=> replay = balance 150
```

### Benefits
- append-only writes
- full audit history
- easy replay/debugging
- fewer write conflicts

---

## CQRS
Separate:
- **write model** (commands/events)
- **read model** (optimized views)

```text
Write -> Event Store -> Projections -> Read Stores
```

Read stores may include:
- PostgreSQL
- Redis
- Elasticsearch
- ClickHouse

### Use when
- reads and writes have very different needs
- audit/history matters
- eventual consistency is okay

### Avoid when
- simple CRUD is enough
- team/system is small
- strict read-after-write everywhere is required

---

# 8) Backpressure and Rate Limiting

## Why it matters
If the system accepts more writes than it can process:
- queues grow
- memory fills
- timeouts happen
- cascading failures start

---

## Rate limiting
Reject excess traffic early.

### Token bucket example
```python
class TokenBucket:
    capacity = 500
    refillRate = 100   # tokens/sec
    tokens = 500
    lastRefill = now()

    def tryConsume(self):
        self.refill()
        if self.tokens >= 1:
            self.tokens -= 1
            return True
        return False

    def refill(self):
        elapsed = now() - self.lastRefill
        self.tokens = min(self.capacity, self.tokens + elapsed * self.refillRate)
        self.lastRefill = now()
```

### Common approaches
- token bucket
- leaky bucket
- fixed window
- sliding window

---

## Backpressure
Signal upstream to slow down.

Examples:
- blocking producer
- credits
- HTTP 503 + `Retry-After`
- queue fullness signals

### Rule
```text
Rejecting work safely > accepting work and crashing later
```

---

## Load shedding
Drop some traffic intentionally under overload.

Options:
- random drop
- drop oldest
- priority-based
- client/quota-based

Best practice:
- keep core writes alive
- drop optional/low-priority work first

---

## Graceful degradation
Keep core path, defer extras.

Normal:
```text
write DB + analytics + notifications + cache updates
```

Degraded:
```text
write DB only
delay analytics / notifications
```

---

# 9) Putting It All Together

## Reference architecture
```text
Load Balancer
-> Rate Limiter
-> API Servers
-> Kafka / Queue
-> Consumer Groups
-> Sharded Write-Optimized DB
-> Read Projections (Redis / Elasticsearch / ClickHouse)
```

## Flow
1. **Ingest** quickly
2. **Buffer** spikes in queue
3. **Process** in parallel
4. **Persist** durably
5. **Project** into read models

---

# 10) Interview Answer Template

```text
For high write traffic, I’d first check durability, ordering, and latency requirements.

My first optimization would be batching, because it reduces per-write overhead immediately.
Then I’d decouple ingestion from persistence using a durable queue, so the system can absorb spikes and scale consumers independently.

If a single database still can’t keep up, I’d shard writes across multiple nodes and choose a write-optimized store such as Cassandra for append-heavy workloads.

I’d make consumers idempotent for safe retries, and I’d protect the system with rate limiting, backpressure, and graceful degradation.
If reads need different access patterns, I’d project events into separate read models using CQRS.
```

---

# 11) Polished Key Takeaways

- **Writes are fundamentally harder than reads**  
  You can cache reads, but every write must eventually hit durable storage.

- **Batching is your first and easiest win**  
  Combine many small writes into one larger operation to reduce overhead dramatically.

- **Queues turn spikes into manageable flow**  
  Async processing lets you accept traffic quickly, buffer bursts, and scale workers independently.

- **Choose storage optimized for your workload**  
  B-trees are great for mixed workloads; LSM-tree systems and append-only logs shine for heavy writes.

- **Sharding is how writes scale horizontally**  
  When one node is not enough, distribute writes carefully using a shard key that avoids hot spots.

- **Idempotency is non-negotiable**  
  Retries and duplicates are normal in distributed systems; processing the same event twice must be safe.

- **CQRS and event sourcing help when reads and writes diverge**  
  Keep the write path simple and durable, then build separate read models for query speed.

- **Protect the system under stress**  
  Rate limiting, backpressure, load shedding, and graceful degradation prevent overload from becoming outage.

- **Always design for the worst case, not the average**  
  High write systems fail during spikes, incidents, and replays—not during normal traffic.

---

## Final 1-Line Shortcut
```text
Batch -> Queue -> Shard -> Optimize Storage -> Protect System
```
