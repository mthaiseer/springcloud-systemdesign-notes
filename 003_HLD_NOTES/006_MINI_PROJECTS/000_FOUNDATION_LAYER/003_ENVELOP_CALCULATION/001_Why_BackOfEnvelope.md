# 001_Why_BackOfEnvelope.md

# Why Back-of-the-Envelope Estimation Matters

> **Goal:** Learn why back-of-the-envelope estimation is one of the highest ROI skills for system design, backend engineering, FAANG interviews, production debugging, capacity planning, and real-world architecture decisions.

---

## 0. One-Line Definition

**Back-of-the-envelope estimation** means using simple numbers, rough assumptions, and fast arithmetic to decide whether a system design is realistic.

You are not trying to be perfectly accurate.
You are trying to avoid being wildly wrong.

```text
Perfect answer      ❌ not required
Reasonable answer   ✅ required
Clearly explained   ✅ required
Fast enough         ✅ required
```

---

## 1. Why This Skill Exists

In real systems, before building anything, engineers must answer questions like:

```text
Can one database handle this traffic?
How much storage do we need?
How many servers are required?
Will Redis fit in memory?
Can Kafka handle this event volume?
Is this API latency target realistic?
Will this design survive peak traffic?
```

Without estimation, system design becomes guessing.

With estimation, you can quickly say:

```text
This design is safe.
This design is risky.
This needs cache.
This needs sharding.
This needs async processing.
This DB will become the bottleneck.
```

---

## 2. The Core Mental Model

Every backend system is mostly about **4 numbers**:

```text
Users  ->  Requests  ->  Data  ->  Machines
```

ASCII view:

```text
+--------+      +-----------+      +---------+      +----------+
| Users  | ---> | Requests  | ---> | Storage | ---> | Servers  |
+--------+      +-----------+      +---------+      +----------+
     |                |                 |                |
     v                v                 v                v
 DAU/MAU          QPS / peak        GB / TB          CPU / RAM
```

Back-of-envelope is the skill of moving through this chain quickly.

---

## 3. Why It Is Important For FAANG/System Design Interviews

In interviews, the interviewer wants to know whether you can think like a production engineer.

They are not checking only if you know Redis, Kafka, Cassandra, or Kubernetes.
They are checking if you know **when they are needed**.

Example:

```text
Question: Design URL Shortener
Weak answer:
"I will use Redis, Kafka, Postgres, Kubernetes, CDN."

Strong answer:
"If we have 100M redirects/day, average QPS is around 1,157.
Peak may be 10x, around 12K QPS.
Reads dominate writes, so Redis cache helps.
Postgres can store metadata, Kafka can handle async click analytics."
```

The second answer sounds senior because it connects design choices to numbers.

---

## 4. The Big Idea: Approximation Beats Silence

You do not need exact numbers.
You need useful numbers.

```text
Exact:       1,157.407407 QPS
Useful:      ~1.2K QPS
Interview:   ~1K average QPS, ~10K peak QPS
```

Good engineers round aggressively.

```text
86,400 seconds/day  ->  ~100,000 seconds/day
1 KB                ->  small record
1 MB                ->  image/log batch size
1 GB                ->  1 billion bytes, roughly
1 TB                ->  1,000 GB
```

---

## 5. The Three Questions You Always Ask

For any system, start with these:

```text
1. How many users?
2. How many requests?
3. How much data?
```

Then convert them into:

```text
QPS
Peak QPS
Storage/day
Storage/year
Memory/cache size
Bandwidth
Server count
```

ASCII decision path:

```text
                +-------------------+
                | System Design Ask |
                +-------------------+
                          |
                          v
          +-------------------------------+
          | Estimate traffic and storage  |
          +-------------------------------+
                          |
                          v
        +-----------------------------------+
        | Pick architecture based on scale  |
        +-----------------------------------+
             |              |             |
             v              v             v
          Cache          Queue          Shard
```

---

## 6. Simple Example: URL Shortener

Assume:

```text
100M redirects/day
1M new short URLs/day
Each URL metadata record = 500 bytes
Each click event = 200 bytes
```

### Step 1: Convert daily traffic to QPS

```text
100M redirects/day / 100K seconds/day
= 1,000 redirects/second average
```

Peak traffic is often 5x to 10x average.

```text
Peak QPS = 1K * 10 = 10K QPS
```

### Step 2: Estimate metadata storage

```text
1M URLs/day * 500 bytes
= 500M bytes/day
= ~500 MB/day
```

Per year:

```text
500 MB/day * 365
= ~182 GB/year
```

### Step 3: Estimate click event storage

```text
100M clicks/day * 200 bytes
= 20,000M bytes/day
= ~20 GB/day
```

Per year:

```text
20 GB/day * 365
= ~7.3 TB/year
```

### Conclusion

```text
URL metadata: manageable in Postgres initially
Click events: large, better async via Kafka + object storage / analytics DB
Redirects: read-heavy, Redis cache useful
```

ASCII architecture from estimation:

```text
Client
  |
  v
API Gateway
  |
  v
Redirect Service
  |
  +---- cache hit ----> Redis ----> Original URL
  |
  +---- cache miss ---> Postgres
  |
  +---- async event --> Kafka --> Analytics Worker --> Data Lake / OLAP
```

---

## 7. Why Estimation Changes Architecture

Same feature, different scale, different design.

### Small scale

```text
1K users
10K requests/day
Small DB
No cache needed
No Kafka needed
Single server is fine
```

### Medium scale

```text
1M users
100M requests/day
Redis cache needed
Read replica may help
Async analytics needed
```

### Large scale

```text
100M users
10B requests/day
Sharding likely needed
Kafka required
Multi-region may be required
Hot key protection needed
```

One diagram:

```text
Scale increases

Single DB  --->  DB + Cache  --->  Cache + Queue + Shards  --->  Multi-region
   |              |                    |                          |
 simple        read-heavy          high throughput              global
```

---

## 8. The Most Important Back-of-Envelope Formula

```text
QPS = total requests / total seconds
```

For daily traffic:

```text
QPS = requests per day / 100,000
```

Because:

```text
1 day = 24 * 60 * 60 = 86,400 seconds ~= 100,000 seconds
```

Fast examples:

```text
1M requests/day     ~= 10 QPS
10M requests/day    ~= 100 QPS
100M requests/day   ~= 1,000 QPS
1B requests/day     ~= 10,000 QPS
10B requests/day    ~= 100,000 QPS
```

This one table alone is extremely high ROI.

---

## 9. Average QPS vs Peak QPS

Average QPS is not enough.
Real systems have spikes.

```text
Average QPS = normal traffic spread across the full day
Peak QPS    = traffic during busy periods
```

Rule of thumb:

```text
Peak QPS = Average QPS * 5 to 10
```

Example:

```text
100M requests/day ~= 1K average QPS
Peak ~= 5K to 10K QPS
```

ASCII view:

```text
Traffic during day

QPS
^
|                         peak
|                         /\
|                        /  \
|         normal        /    \       normal
|________/\____________/      \______________> time

Average is useful.
Peak decides capacity.
```

---

## 10. Storage Estimation Mental Model

Storage usually means:

```text
Number of records * size per record
```

Formula:

```text
Storage = count * record_size
```

Example:

```text
10M users * 1 KB/user
= 10M KB
= 10 GB
```

Common record sizes:

```text
User profile small:       1 KB
URL metadata:             500 bytes to 1 KB
Chat message:             500 bytes to 2 KB
Log/event row:            200 bytes to 1 KB
Image:                    100 KB to several MB
Video:                    very large, often MB to GB
```

---

## 11. Cache Estimation Mental Model

Cache estimation asks:

```text
What hot data must fit in memory?
```

Example:

```text
10M hot URLs
Each cached mapping = 200 bytes
Cache size = 10M * 200 bytes = 2 GB
```

Add overhead:

```text
Redis overhead can be significant.
So estimate 2x to 3x.
Required Redis memory ~= 4 GB to 6 GB
```

ASCII:

```text
All data in DB
+--------------------------------------+
| cold | cold | warm | HOT HOT HOT     |
+--------------------------------------+
                         |
                         v
                    Redis cache
```

---

## 12. Bandwidth Estimation Mental Model

Bandwidth asks:

```text
How much data moves per second?
```

Formula:

```text
Bandwidth = QPS * response_size
```

Example:

```text
10K QPS
Each response = 10 KB
Bandwidth = 100,000 KB/s = 100 MB/s
```

This matters for:

```text
API Gateway capacity
Load balancer capacity
Network cost
CDN decision
Service-to-service traffic
```

---

## 13. Server Count Estimation Mental Model

Server count asks:

```text
How many machines do we need to handle peak QPS?
```

Simple formula:

```text
Server count = Peak QPS / QPS per server
```

Example:

```text
Peak QPS = 10K
One service instance handles 1K QPS safely
Required instances = 10
Add buffer = 12 to 15 instances
```

ASCII:

```text
             10K peak QPS
                  |
                  v
        +-------------------+
        | Load Balancer     |
        +-------------------+
          |   |   |   |   |
          v   v   v   v   v
        S1  S2  S3  S4  S5 ... S15
```

---

## 14. Production Debugging: Why This Skill Saves You

Back-of-envelope is not only for interviews.
It helps in production incidents.

Example incident:

```text
Problem: Redis memory suddenly high
Question: Is this normal or a leak?
```

Estimate:

```text
Expected hot keys = 20M
Average key+value size = 300 bytes
Raw memory = 6 GB
With Redis overhead 2x = 12 GB
```

If Redis is using 13 GB, it may be normal.
If Redis is using 80 GB, something is wrong.

This skill helps you avoid panic and debug logically.

---

## 15. Common Mistake: Adding Technology Without Numbers

Bad design habit:

```text
Use Kafka because system is large.
Use Cassandra because FAANG uses it.
Use Kubernetes because production uses it.
Use Redis because cache is fast.
```

Better design habit:

```text
Use Kafka because writes generate 100M events/day and analytics can be async.
Use Redis because 90% reads hit hot data and DB cannot handle 10K peak QPS alone.
Use sharding because one DB cannot store or serve the required scale safely.
Use CDN because media bandwidth is too high for origin servers.
```

Numbers justify architecture.

---

## 16. Interview Template: How To Speak Estimation

Use this structure:

```text
1. State assumptions
2. Convert users to requests
3. Convert requests/day to QPS
4. Estimate peak QPS
5. Estimate storage
6. Identify bottlenecks
7. Pick architecture
8. Mention trade-offs
```

Example interview answer:

```text
Let me assume 10M DAU and each user performs 20 actions/day.
That gives 200M requests/day.
Using 100K seconds/day, average QPS is about 2K.
Peak could be 10x, so around 20K QPS.
If each event is 500 bytes, daily event storage is around 100 GB.
So I would keep the transactional path simple, use Redis for hot reads,
and send events asynchronously to Kafka for analytics.
```

This answer is strong because it is simple, structured, and number-driven.

---

## 17. Mini Real-World Examples

### Example A: Notification System

```text
10M users
2 notifications/user/day
= 20M notifications/day
= ~200 notifications/sec average
Peak maybe 2K/sec
```

Conclusion:

```text
Do not send all synchronously from API.
Use queue + worker model.
```

ASCII:

```text
API ---> Kafka/Queue ---> Email Worker
                    |---> SMS Worker
                    |---> Push Worker
```

---

### Example B: Chat System

```text
1M active users
50 messages/user/day
= 50M messages/day
= ~500 messages/sec average
Peak maybe 5K/sec
```

If each message is 1 KB:

```text
50M * 1 KB = 50 GB/day
```

Conclusion:

```text
Message storage grows quickly.
Need partitioning by conversation/user/time.
```

---

### Example C: Image Upload System

```text
1M uploads/day
Average image = 2 MB
Storage/day = 2 TB/day
Storage/year = 730 TB/year
```

Conclusion:

```text
Do not store images in database.
Use object storage + CDN.
Store metadata only in DB.
```

ASCII:

```text
Client ---> Upload API ---> Object Storage/S3
              |
              v
          Metadata DB
```

---

## 18. Back-of-Envelope Decision Tree

```text
Start
 |
 v
How many requests/day?
 |
 v
Convert to QPS
 |
 +--> QPS low?
 |       |
 |       v
 |   Simple API + DB may work
 |
 +--> QPS high?
         |
         v
   Read-heavy or write-heavy?
         |
         +--> Read-heavy ---> Cache / read replica / CDN
         |
         +--> Write-heavy --> Queue / batching / partitioning
```

Storage decision tree:

```text
How much data/year?
 |
 +--> GB scale  ---> Single DB likely fine
 |
 +--> TB scale  ---> Partitioning / archival / object storage
 |
 +--> PB scale  ---> Distributed storage / data lake / aggressive lifecycle
```

---

## 19. Numbers Worth Memorizing

```text
1 day                  = 86,400 sec ~= 100K sec
1M requests/day         ~= 10 QPS
10M requests/day        ~= 100 QPS
100M requests/day       ~= 1K QPS
1B requests/day         ~= 10K QPS

1 KB * 1M records       ~= 1 GB
1 KB * 1B records       ~= 1 TB
1 MB * 1M files         ~= 1 TB

Peak QPS                ~= average QPS * 5 to 10
Cache memory estimate   ~= raw size * 2 to 3
Replication storage     ~= raw storage * replication factor
```

---

## 20. Common Interview Red Flags

Avoid these:

```text
❌ No assumptions
❌ No QPS estimate
❌ No peak traffic estimate
❌ No storage estimate
❌ Adding Kafka/Redis/sharding without reason
❌ Over-engineering small scale
❌ Ignoring media/log/event storage
❌ Forgetting replication and backup cost
❌ Confusing average traffic with peak traffic
```

Good signals:

```text
✅ Clear assumptions
✅ Rounded numbers
✅ Fast QPS conversion
✅ Storage/day and storage/year
✅ Bottleneck identification
✅ Technology justified by scale
✅ Simple first, scale later
```

---

## 21. One Full Dry Run: News Feed

Problem:

```text
Design a simple news feed.
```

Assumptions:

```text
10M DAU
Each user opens feed 10 times/day
Each feed request returns 20 posts
Average post metadata = 1 KB
```

### Read QPS

```text
10M users * 10 feed opens/day = 100M feed requests/day
100M / 100K seconds = ~1K QPS average
Peak = ~10K QPS
```

### Response bandwidth

```text
Each response = 20 posts * 1 KB = 20 KB
Peak bandwidth = 10K QPS * 20 KB = 200 MB/sec
```

### Storage

Assume:

```text
1M posts/day
Each post metadata = 1 KB
Storage/day = 1 GB/day
Storage/year = 365 GB/year
```

If posts contain images/videos, object storage is needed.

### Architecture conclusion

```text
Feed reads are heavy.
Use Redis for cached timelines.
Use DB/Cassandra for post storage.
Use Kafka for fanout/event processing.
Use CDN/object storage for media.
```

ASCII:

```text
User ---> Feed API ---> Redis Timeline
              |
              +---- miss ---> Feed Builder ---> Post Store
              |
              +---- media ---> CDN/Object Storage

Post Create API ---> Kafka ---> Fanout Worker ---> Redis Timelines
```

---

## 22. How To Practice This Skill

For every system design problem, force yourself to fill this table:

```text
System: ______________________

DAU: _________________________
Requests/user/day: ___________
Total requests/day: __________
Average QPS: _________________
Peak QPS: ____________________
Record size: _________________
Storage/day: _________________
Storage/year: ________________
Cache size: __________________
Bandwidth: ___________________
Bottleneck: __________________
Architecture decision: _______
```

Practice systems:

```text
URL Shortener
Rate Limiter
Chat System
Notification System
News Feed
Payment System
Search Autocomplete
File Upload System
Ride Matching System
Logging Platform
```

---

## 23. Production Mindset

Back-of-envelope is not about math talent.
It is about engineering judgment.

A senior engineer thinks:

```text
What is the order of magnitude?
What will break first?
What can stay simple?
What must be distributed?
What can be async?
What must be strongly consistent?
What can be eventually consistent?
```

Back-of-envelope gives you the confidence to answer these.

---

## 24. Cheat Sheet

```text
Core formulas
-------------
QPS = requests / seconds
Daily QPS ~= requests_per_day / 100K
Peak QPS ~= average QPS * 5 to 10
Storage = records * record_size
Bandwidth = QPS * response_size
Servers = peak QPS / safe QPS per server
Cache = hot_items * item_size * overhead_factor

Core conversions
----------------
1 day ~= 100K seconds
1 KB * 1M ~= 1 GB
1 KB * 1B ~= 1 TB
1 MB * 1M ~= 1 TB
1 GB/day ~= 365 GB/year
10 GB/day ~= 3.65 TB/year
100 GB/day ~= 36.5 TB/year

Architecture hints
------------------
High reads        -> cache, CDN, read replicas
High writes       -> queue, batching, partitioning
High storage      -> partitioning, object storage, archival
High events       -> Kafka/streaming
Hot keys          -> replication, local cache, request coalescing
Global users      -> multi-region/CDN
```

---

## 25. Final One-Picture Summary

```text
                  BACK-OF-THE-ENVELOPE

        +----------+     +---------+     +----------+
        |  Users   | --> |   QPS   | --> |  Peak    |
        +----------+     +---------+     +----------+
              |               |               |
              v               v               v
        +----------+     +---------+     +----------+
        | Storage  | --> | Memory  | --> | Servers  |
        +----------+     +---------+     +----------+
              |               |               |
              v               v               v
        +------------------------------------------+
        | Architecture Decision                     |
        | DB / Cache / Queue / Shard / CDN / Region |
        +------------------------------------------+
```

---

## 26. What You Should Remember

Back-of-envelope estimation helps you answer:

```text
Can this design work?
Where will it break?
How much will it cost?
Which component is the bottleneck?
Which technology is justified?
```

The goal is not perfect math.
The goal is **engineering confidence**.

```text
Numbers -> Bottlenecks -> Design decisions
```

That is the entire game.
