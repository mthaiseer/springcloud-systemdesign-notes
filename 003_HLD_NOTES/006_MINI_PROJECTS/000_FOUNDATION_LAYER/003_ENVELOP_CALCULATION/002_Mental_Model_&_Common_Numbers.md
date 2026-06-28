# Mental Model & Common Numbers For Back-of-the-Envelope Estimation

> **Goal:** Build a simple mental model for estimation and memorize the few numbers that unlock most system design calculations: QPS, peak traffic, storage, cache, bandwidth, latency, and server count.

---

## 0. One-Line Definition

A **mental model for estimation** is a repeatable way to convert vague product scale into engineering numbers.

```text
Vague product ask  ->  concrete numbers  ->  bottleneck  ->  architecture decision
```

You are not trying to predict the future perfectly.
You are trying to quickly answer:

```text
Can this design work?
What will break first?
Which component needs scaling?
Which technology is justified?
```

---

## 1. The Core Mental Model

Every backend estimation can be reduced to this chain:

```text
Users  ->  Actions  ->  Requests  ->  Data  ->  Machines
```

ASCII view:

```text
+--------+      +---------+      +----------+      +---------+      +----------+
| Users  | ---> | Actions | ---> | Requests | ---> |  Data   | ---> | Machines |
+--------+      +---------+      +----------+      +---------+      +----------+
    |               |               |                |                |
    v               v               v                v                v
 DAU/MAU      actions/user/day      QPS          GB/TB/PB        CPU/RAM/IO
```

This is the entire estimation game.

If you can move through this chain calmly, you can estimate almost any system.

---

## 2. The 5 Questions You Always Ask

For any system design problem, ask these in order:

```text
1. How many users?
2. How many actions per user?
3. How many requests per day?
4. How much data per request or record?
5. What is the peak load?
```

Then convert the answers into:

```text
Average QPS
Peak QPS
Storage/day
Storage/year
Cache memory
Bandwidth
Server count
Database load
Queue/event volume
```

ASCII flow:

```text
             +-------------------+
             | Product Question  |
             +-------------------+
                       |
                       v
              +----------------+
              | User Assumptions |
              +----------------+
                       |
                       v
              +----------------+
              | Traffic Numbers |
              +----------------+
                       |
                       v
              +----------------+
              | Storage Numbers |
              +----------------+
                       |
                       v
              +-----------------------+
              | Architecture Decision |
              +-----------------------+
```

---

## 3. The Most Important Approximation

In interviews and real design discussions, use:

```text
1 day = 24 * 60 * 60 = 86,400 seconds
1 day ~= 100,000 seconds
```

So:

```text
Daily QPS ~= requests_per_day / 100K
```

This shortcut is high ROI because most traffic numbers are given per day.

```text
1M requests/day      ~= 10 QPS
10M requests/day     ~= 100 QPS
100M requests/day    ~= 1K QPS
1B requests/day      ~= 10K QPS
10B requests/day     ~= 100K QPS
```

ASCII memory ladder:

```text
Requests/day      Average QPS
------------      -----------
1M         ---->       10
10M        ---->      100
100M       ---->    1,000
1B         ---->   10,000
10B        ---->  100,000
```

---

## 4. Average QPS vs Peak QPS

Average QPS is spread across the whole day.
Peak QPS is what your system must survive during busy periods.

Rule of thumb:

```text
Peak QPS ~= Average QPS * 5 to 10
```

Example:

```text
100M requests/day ~= 1K average QPS
Peak ~= 5K to 10K QPS
```

ASCII traffic curve:

```text
QPS
^
|                         peak traffic
|                            /\
|                           /  \
|       normal             /    \         normal
|______/\_________________/      \________________> time

Average QPS helps understanding.
Peak QPS decides capacity.
```

Interview phrasing:

```text
I will estimate average QPS first, then multiply by 10 for peak capacity.
```

---

## 5. The Golden Estimation Formula Set

Memorize these formulas:

```text
Requests/day      = users * actions_per_user_per_day
Average QPS       = requests_per_day / 100K
Peak QPS          = average_QPS * 5 to 10
Storage           = number_of_records * size_per_record
Storage/day       = daily_records * size_per_record
Bandwidth         = QPS * response_size
Cache memory      = hot_items * item_size * overhead_factor
Server count      = peak_QPS / safe_QPS_per_server
Replication size  = raw_storage * replication_factor
```

One picture:

```text
Users
  |
  v
Requests/day = users * actions
  |
  v
QPS = requests/day / 100K
  |
  +--> Peak QPS = QPS * 10
  |
  +--> Bandwidth = QPS * response size
  |
  +--> Servers = Peak QPS / capacity per server

Records
  |
  v
Storage = count * record size
  |
  +--> Replication
  +--> Backup
  +--> Cache hot subset
```

---

## 6. Common Time Numbers

```text
1 second      = 1,000 ms
1 minute      = 60 seconds
1 hour        = 3,600 seconds ~= 4K seconds
1 day         = 86,400 seconds ~= 100K seconds
1 week        = 7 days
1 month       ~= 30 days
1 year        ~= 365 days
```

High ROI conversions:

```text
1 request/sec for 1 day  ~= 100K requests/day
10 request/sec           ~= 1M requests/day
100 request/sec          ~= 10M requests/day
1K request/sec           ~= 100M requests/day
10K request/sec          ~= 1B requests/day
```

Reverse mental model:

```text
QPS * 100K ~= requests/day
```

Example:

```text
2K QPS average * 100K seconds/day
= 200M requests/day
```

---

## 7. Common Data Size Numbers

Use decimal approximations for speed:

```text
1 KB ~= 1,000 bytes
1 MB ~= 1,000 KB
1 GB ~= 1,000 MB
1 TB ~= 1,000 GB
1 PB ~= 1,000 TB
```

Useful shortcuts:

```text
1 KB * 1M records  ~= 1 GB
1 KB * 1B records  ~= 1 TB
1 MB * 1M files    ~= 1 TB
1 GB/day           ~= 365 GB/year
10 GB/day          ~= 3.65 TB/year
100 GB/day         ~= 36.5 TB/year
1 TB/day           ~= 365 TB/year
```

ASCII ladder:

```text
Bytes -> KB -> MB -> GB -> TB -> PB
        x1000 x1000 x1000 x1000 x1000
```

---

## 8. Common Record Size Numbers

You do not need exact sizes. Use safe rough sizes.

```text
Tiny ID / counter row             50 to 100 bytes
Simple event/log row              200 bytes to 1 KB
URL mapping metadata              500 bytes to 1 KB
User profile                      1 KB to 5 KB
Chat message                      500 bytes to 2 KB
Feed post metadata                1 KB to 5 KB
Notification record               500 bytes to 1 KB
Payment transaction metadata      1 KB to 5 KB
Small thumbnail                   10 KB to 100 KB
Image                             100 KB to 5 MB
Short video                       5 MB to 100 MB+
```

Safe interview defaults:

```text
Metadata row        -> 1 KB
Event row           -> 500 bytes
Chat message        -> 1 KB
Feed post metadata  -> 1 KB
Image               -> 1 MB
```

---

## 9. Storage Estimation Mental Model

Formula:

```text
Storage = count * record_size
```

Example:

```text
10M users
1 KB/user
Storage = 10M KB = 10 GB
```

With growth:

```text
1M new records/day
1 KB/record
= 1 GB/day
= 365 GB/year
```

With replication:

```text
Raw storage = 365 GB/year
Replication factor = 3
Actual storage ~= 1.1 TB/year
```

ASCII:

```text
Raw data       Replication       Backup/index overhead
365 GB   --->  1.1 TB      --->  maybe 1.5 TB to 2 TB
```

Production mindset:

```text
Always ask: raw size or actual stored size?
Indexes, replicas, backups, compression, and retention change the number.
```

---

## 10. Index Size Mental Model

Indexes are not free.

Rough rule:

```text
Index size ~= 10% to 100% of table size
```

Depends on:

```text
Number of indexed columns
Column size
Cardinality
B-tree overhead
Composite indexes
```

Simple interview estimate:

```text
Table size = 500 GB
Indexes = 30% to 50%
Total DB storage ~= 650 GB to 750 GB
```

ASCII:

```text
Database storage
+------------------------------+
| Table data       500 GB       |
+------------------------------+
| Indexes          150-250 GB   |
+------------------------------+
| Free space/WAL   extra        |
+------------------------------+
```

---

## 11. Cache Size Mental Model

Cache is usually for hot data, not all data.

Formula:

```text
Cache memory = hot_items * item_size * overhead_factor
```

Redis/object overhead matters.

Safe rule:

```text
Redis memory ~= raw_data_size * 2 to 3
```

Example:

```text
10M hot URL mappings
200 bytes each
Raw size = 2 GB
Redis actual memory ~= 4 GB to 6 GB
```

ASCII:

```text
Full DB
+--------------------------------------------------+
| cold cold cold warm warm HOT HOT HOT HOT HOT     |
+--------------------------------------------------+
                                |
                                v
                           Redis cache
```

Common mistake:

```text
❌ Caching all data without checking memory
✅ Cache hot data and estimate memory with overhead
```

---

## 12. Bandwidth Mental Model

Formula:

```text
Bandwidth = QPS * response_size
```

Example:

```text
10K QPS
20 KB response
Bandwidth = 200,000 KB/sec = 200 MB/sec
```

This matters for:

```text
API gateway
Load balancer
Service-to-service traffic
CDN
Cloud network cost
Client latency
```

ASCII:

```text
Clients ---> API ---> Service ---> DB/Cache
            200 MB/s    ? MB/s      ? MB/s
```

Interview phrasing:

```text
At 10K peak QPS and 20 KB response size, the service sends roughly 200 MB/s, so CDN/cache becomes important if responses are media-heavy.
```

---

## 13. Latency Numbers Worth Knowing

Use these as rough mental anchors:

```text
Memory access                 very fast
Local SSD read                fast
Network call inside region    usually milliseconds
Database query                milliseconds to tens of milliseconds
Cross-region network call     tens to hundreds of milliseconds
Cold object storage access    slower than cache/DB
Human-visible delay           100 ms feels fast, 1 sec feels slow
```

Practical backend thinking:

```text
Cache hit     -> fastest common backend path
DB query      -> okay if indexed and controlled
Remote call   -> risky if chained many times
Cross-region  -> avoid in critical synchronous path
```

ASCII latency chain:

```text
Client -> API -> Service -> Cache -> DB -> External API
 20ms     5ms     2ms      1ms     20ms      200ms

Total latency is the sum of synchronous steps.
```

Key lesson:

```text
One slow dependency can dominate the whole request.
```

---

## 14. Server Count Mental Model

Formula:

```text
Server count = Peak QPS / safe QPS per server
```

Example:

```text
Peak QPS = 20K
One service instance safely handles 1K QPS
Required = 20 instances
Add buffer = 25 to 30 instances
```

ASCII:

```text
              20K peak QPS
                    |
                    v
              Load Balancer
        /     /     |     \      \
      S1    S2     S3     S4     ... S30
```

Important:

```text
Safe QPS per server depends on endpoint complexity.
A simple cached read can handle much more than a heavy DB write.
```

---

## 15. Database Capacity Mental Model

A database can fail because of different bottlenecks:

```text
CPU
IOPS
Locks
Connections
Memory/cache misses
Index bloat
Hot rows
Slow queries
Replication lag
```

Simple interview thinking:

```text
Low QPS + small data       -> single DB is fine
High read QPS              -> add cache/read replicas
High write QPS             -> partition/shard/batch/queue
Huge data                  -> partition/archive/shard
Hot key or hot row         -> redesign key/access pattern
```

ASCII decision:

```text
DB pressure
 |
 +--> reads high?  ---> Redis / read replicas / CDN
 |
 +--> writes high? ---> queue / batch / shard
 |
 +--> data huge?   ---> partition / archive / object storage
 |
 +--> locks high?  ---> reduce contention / async / split rows
```

---

## 16. Queue/Event Volume Mental Model

Event systems are estimated like requests.

Formula:

```text
Events/day = users * events/user/day
Event QPS  = events/day / 100K
Storage/day = events/day * event_size
```

Example:

```text
100M click events/day
500 bytes/event
Average event rate ~= 1K/sec
Storage/day = 50 GB/day
Storage/year = 18.25 TB/year
```

Conclusion:

```text
Kafka can decouple write path from analytics.
Raw events may need lifecycle policy or data lake storage.
```

ASCII:

```text
API Request
   |
   +--> critical DB update
   |
   +--> async event ---> Kafka ---> Consumer ---> Analytics Store
```

---

## 17. Read-Heavy vs Write-Heavy Mental Model

Before choosing technology, classify workload:

```text
Read-heavy   -> many reads, fewer writes
Write-heavy  -> many writes/events, fewer reads
Mixed        -> both high
```

Examples:

```text
URL redirect       -> read-heavy
News feed read     -> read-heavy
Chat messages      -> write-heavy + read recent
Payment system     -> write-sensitive, consistency-heavy
Logging platform   -> write-heavy
Search autocomplete -> read-heavy, low latency
```

Architecture mapping:

```text
Read-heavy  -> cache, CDN, read replica, denormalization
Write-heavy -> queue, batching, partitioning, append-only logs
Mixed       -> separate read path and write path
```

ASCII:

```text
Read-heavy path:
Client -> API -> Cache -> DB fallback

Write-heavy path:
Client -> API -> Queue/Log -> Workers -> Storage
```

---

## 18. Hot Data Mental Model

Not all data is equally important.

Usually:

```text
Small percentage of data receives most traffic.
```

This is often called the 80/20 rule:

```text
20% of items may receive 80% of traffic
```

ASCII:

```text
All items
+------------------------------------------------+
| cold cold cold cold warm warm HOT HOT HOT      |
+------------------------------------------------+
                              |
                              v
                        cache these first
```

Example:

```text
Total URLs = 1B
Hot URLs = 10M
Each hot mapping = 200 bytes
Raw hot cache = 2 GB
With overhead = 4 GB to 6 GB
```

Conclusion:

```text
You do not need Redis for everything.
You need Redis for hot data.
```

---

## 19. Fanout Mental Model

Fanout means one action creates many downstream operations.

Example:

```text
1 user posts
1M followers receive it
```

That one write can create 1M timeline updates.

ASCII:

```text
Post Create
    |
    v
Fanout Worker
 /  /  /  /  /  /  /  /
F1 F2 F3 F4 F5 ... F1M timelines
```

Estimation:

```text
10K celebrity posts/day
1M followers each
= 10B fanout writes/day
= 100K fanout writes/sec average
```

Conclusion:

```text
Pure push fanout may be too expensive for celebrities.
Use hybrid fanout: push for normal users, pull for celebrities.
```

---

## 20. Full Dry Run 1: URL Shortener

Assumptions:

```text
100M redirects/day
1M new short URLs/day
URL metadata = 500 bytes
Click event = 200 bytes
Peak = 10x average
```

### Step 1: Redirect QPS

```text
100M/day / 100K seconds/day = 1K average QPS
Peak = 10K QPS
```

### Step 2: Metadata storage

```text
1M URLs/day * 500 bytes = 500 MB/day
500 MB/day * 365 = 182 GB/year
```

### Step 3: Click event storage

```text
100M clicks/day * 200 bytes = 20 GB/day
20 GB/day * 365 = 7.3 TB/year
```

### Step 4: Architecture conclusion

```text
Metadata fits in relational DB initially.
Redirects are read-heavy, so Redis helps.
Click analytics is large, so use Kafka and async processing.
```

ASCII:

```text
Client -> Redirect API -> Redis
              |             |
              | miss        v
              +----------> Postgres
              |
              +-- click event --> Kafka --> Analytics
```

---

## 21. Full Dry Run 2: Notification System

Assumptions:

```text
20M users
5 notifications/user/day
Notification record = 500 bytes
Peak = 10x average
```

### Step 1: Event volume

```text
20M * 5 = 100M notifications/day
100M/day / 100K = 1K notifications/sec average
Peak = 10K/sec
```

### Step 2: Storage

```text
100M * 500 bytes = 50 GB/day
50 GB/day * 30 = 1.5 TB/month
```

### Step 3: Architecture conclusion

```text
Do not send notifications synchronously inside user request.
Use queue + workers.
Store notification metadata separately.
Apply retry and DLQ for failed delivery.
```

ASCII:

```text
Business Event -> Kafka/Queue -> Notification Workers
                                 |       |       |
                                 v       v       v
                               Email    SMS     Push
```

---

## 22. Full Dry Run 3: Image Upload System

Assumptions:

```text
1M uploads/day
Average image = 2 MB
Metadata per image = 1 KB
```

### Step 1: Object storage

```text
1M * 2 MB = 2M MB/day = 2 TB/day
2 TB/day * 365 = 730 TB/year
```

### Step 2: Metadata storage

```text
1M * 1 KB = 1 GB/day
1 GB/day * 365 = 365 GB/year
```

### Step 3: Architecture conclusion

```text
Store images in object storage, not database.
Store only metadata in DB.
Use CDN for serving images.
Use async workers for thumbnail generation.
```

ASCII:

```text
Client -> Upload API -> Object Storage
             |
             +-------> Metadata DB
             |
             +-------> Queue -> Thumbnail Worker

Read path:
Client -> CDN -> Object Storage
```

---

## 23. Architecture Decision Table

```text
Observed number/problem              Likely decision
-----------------------              ---------------
High read QPS                        Redis / CDN / read replicas
High write QPS                       Queue / batching / partitioning
Huge event volume                    Kafka / streaming / data lake
Huge media storage                   Object storage / CDN
Huge relational table                Partitioning / sharding / archival
Hot keys                             local cache / replication / key splitting
Slow external API                    async processing / timeout / circuit breaker
High fanout                          queue workers / hybrid fanout
Large global user base               CDN / multi-region / regional routing
Strict money consistency             ACID DB / idempotency / transactions
```

Remember:

```text
Numbers justify architecture.
Architecture without numbers is guessing.
```

---

## 24. Common Mistakes

Avoid these:

```text
❌ Using exact 86,400 everywhere and getting stuck
❌ Forgetting peak QPS
❌ Estimating only traffic, not storage
❌ Estimating raw storage but forgetting replicas/indexes/backups
❌ Caching all data without memory estimate
❌ Adding Kafka without event volume
❌ Adding sharding before proving single DB is insufficient
❌ Ignoring bandwidth for large responses
❌ Ignoring fanout amplification
❌ Mixing KB, MB, GB incorrectly
```

Better habits:

```text
✅ Round aggressively
✅ State assumptions clearly
✅ Convert daily traffic to QPS
✅ Estimate peak separately
✅ Estimate storage per day and per year
✅ Add overhead for cache/index/replication
✅ Connect every technology to a bottleneck
```

---

## 25. Interview Speaking Template

Use this exact structure:

```text
Let me make simple assumptions.

1. Users: ___ DAU
2. Actions: ___ actions/user/day
3. Requests/day: users * actions = ___
4. Average QPS: requests/day / 100K = ___
5. Peak QPS: average * 10 = ___
6. Storage: records/day * record size = ___/day
7. Bottleneck: read/write/storage/bandwidth/fanout
8. Decision: cache/queue/shard/CDN/read replica/etc.
```

Example:

```text
Let me assume 10M DAU and 20 actions per user per day.
That gives 200M requests/day.
Using 100K seconds/day, average QPS is about 2K.
Peak could be 10x, around 20K QPS.
If each event is 500 bytes, daily event storage is 100 GB.
So I would keep the write path lightweight, publish events to Kafka,
and process analytics asynchronously.
```

---

## 26. Practice Template

Use this for every system design problem:

```text
System: _________________________________

Users
-----
DAU: ____________________________________
Actions/user/day: _______________________
Requests/day: ___________________________

Traffic
-------
Average QPS: ____________________________
Peak multiplier: ________________________
Peak QPS: _______________________________

Storage
-------
Records/day: ____________________________
Record size: ____________________________
Storage/day: ____________________________
Storage/month: __________________________
Storage/year: ___________________________
Replication/index overhead: _____________

Cache
-----
Hot items: ______________________________
Item size: ______________________________
Raw cache size: _________________________
With overhead: __________________________

Bandwidth
---------
Response size: __________________________
Peak bandwidth: _________________________

Decision
--------
Main bottleneck: ________________________
Architecture decision: __________________
Trade-off: ______________________________
```

---

## 27. Mini Drill Problems

### Drill A: Chat System

```text
5M DAU
40 messages/user/day
Message size = 1 KB
```

Calculation:

```text
Messages/day = 5M * 40 = 200M/day
Average write QPS = 200M / 100K = 2K/sec
Peak write QPS = 20K/sec
Storage/day = 200M * 1 KB = 200 GB/day
Storage/year = 73 TB/year
```

Decision:

```text
Partition messages by conversation/user/time.
Use async delivery and durable message storage.
Cache recent conversations.
```

### Drill B: Search Autocomplete

```text
10M DAU
20 searches/user/day
Each response = 5 KB
```

Calculation:

```text
Searches/day = 200M/day
Average QPS = 2K
Peak QPS = 20K
Peak bandwidth = 20K * 5 KB = 100 MB/sec
```

Decision:

```text
Low latency matters.
Use precomputed index/trie/search service and cache hot queries.
```

### Drill C: Rate Limiter

```text
50K peak requests/sec
Each counter key = 100 bytes
Active keys in window = 5M
```

Calculation:

```text
Raw Redis memory = 5M * 100 bytes = 500 MB
With overhead 3x = 1.5 GB
```

Decision:

```text
Redis can hold counters, but hot keys and atomic increments must be handled carefully.
```

---

## 28. One-Page Common Numbers Cheat Sheet

```text
Time
----
1 day ~= 100K seconds
1 month ~= 30 days
1 year ~= 365 days

Traffic
-------
1M/day      ~= 10 QPS
10M/day     ~= 100 QPS
100M/day    ~= 1K QPS
1B/day      ~= 10K QPS
10B/day     ~= 100K QPS
Peak QPS    ~= average QPS * 5 to 10

Data Size
---------
1 KB * 1M   ~= 1 GB
1 KB * 1B   ~= 1 TB
1 MB * 1M   ~= 1 TB
1 GB/day    ~= 365 GB/year
10 GB/day   ~= 3.65 TB/year
100 GB/day  ~= 36.5 TB/year
1 TB/day    ~= 365 TB/year

Safe Default Sizes
------------------
Event row          ~= 500 bytes
Metadata row       ~= 1 KB
Chat message       ~= 1 KB
User profile       ~= 1 KB to 5 KB
Small image        ~= 1 MB

Capacity Formulas
-----------------
QPS = requests/day / 100K
Storage = count * record_size
Bandwidth = QPS * response_size
Cache = hot_items * item_size * 2 to 3
Servers = peak_QPS / safe_QPS_per_server
Actual storage = raw * replicas + indexes + backups
```

---

## 29. Final One-Picture Summary

```text
                 ESTIMATION MENTAL MODEL

      +--------+      +---------+      +----------+
      | Users  | ---> | Actions | ---> | Requests |
      +--------+      +---------+      +----------+
                                            |
                                            v
                                  +----------------+
                                  | QPS / Peak QPS |
                                  +----------------+
                                      |        |
                                      |        v
                                      |   +-----------+
                                      |   | Bandwidth |
                                      |   +-----------+
                                      v
                              +---------------+
                              | Server Count  |
                              +---------------+

      +---------+      +-------------+      +----------------+
      | Records | ---> | Record Size | ---> | Storage Growth |
      +---------+      +-------------+      +----------------+
                                               |
                                               v
                                  +-------------------------+
                                  | Index / Replica / Cache |
                                  +-------------------------+
                                               |
                                               v
                                  +-------------------------+
                                  | Architecture Decision   |
                                  +-------------------------+
```

---

## 30. What You Should Remember

The whole skill is this:

```text
Users -> Requests -> QPS -> Peak -> Storage -> Bottleneck -> Design
```

Memorize only a few numbers:

```text
1 day ~= 100K seconds
1M/day ~= 10 QPS
1 KB * 1M ~= 1 GB
Peak ~= average * 10
Redis overhead ~= raw * 2 to 3
```

Then practice applying them to every system.

Back-of-the-envelope estimation is not about being a math genius.
It is about becoming a calm production engineer who can reason with numbers.
