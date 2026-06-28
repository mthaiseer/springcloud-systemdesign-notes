# 004_CPU_RAM_Disk_Network_CheatSheet.md

# CPU, RAM, Disk, Network Cheat Sheet

> **Goal:** Build a simple, interview-ready and production-ready mental model for estimating CPU, RAM, Disk, and Network capacity in backend systems.

---

## 0. One-Line Definition

Every backend system is limited by one of four physical resources:

```text
CPU      -> computation
RAM      -> hot working memory
Disk     -> durable storage and I/O
Network  -> data movement
```

Back-of-the-envelope estimation helps you quickly decide:

```text
Can this machine handle the traffic?
Will Redis fit in memory?
Will Postgres become slow?
Is network bandwidth enough?
Do we need cache, queue, shard, CDN, or more servers?
```

---

## 1. The Core Mental Model

A request moves through a backend like this:

```text
Client
  |
  v
Network
  |
  v
CPU
  |
  v
RAM
  |
  v
Disk / DB / Cache
  |
  v
Network
  |
  v
Client
```

One picture:

```text
+----------+       +------+       +------+       +------+
| Network  | --->  | CPU  | --->  | RAM  | --->  | Disk |
+----------+       +------+       +------+       +------+
     |                |             |              |
     v                v             v              v
 bandwidth         compute       working set     persistence
 latency           parsing       cache           IOPS/throughput
 transfer          logic         buffers         fsync/read/write
```

A system is slow when **one box becomes the bottleneck**.

---

## 2. Why This Skill Matters

In system design interviews, saying:

```text
"Use Redis, Kafka, Kubernetes, Postgres, CDN"
```

is not enough.

A senior answer connects architecture to hardware limits:

```text
Peak QPS is 20K.
Each response is 50 KB.
Network egress is ~1 GB/sec.
This is too high for one service node.
Use CDN for static/media content and scale API instances horizontally.
```

This is what interviewers want:

```text
Numbers -> Bottleneck -> Design decision
```

---

## 3. The Four Resource Questions

For every backend system, ask:

```text
CPU:      How much work per request?
RAM:      How much hot data must stay in memory?
Disk:     How much data is stored and how fast is it read/written?
Network:  How much data moves per second?
```

ASCII decision flow:

```text
Request comes in
      |
      v
Is it compute-heavy? ---- yes ---> CPU bottleneck
      |
      no
      v
Does it need lots of hot state? -- yes ---> RAM bottleneck
      |
      no
      v
Does it read/write durable data? -- yes ---> Disk/DB bottleneck
      |
      no
      v
Large response or media? -------- yes ---> Network bottleneck
```

---

## 4. High ROI Numbers To Memorize

These are rough numbers, not exact hardware guarantees.

```text
Time
----
1 day                         ~= 100K seconds
1 ms                          = 1/1000 second
100 ms                        = human-noticeable API delay
1 second                      = slow for interactive API

Data size
---------
1 KB                          = small record / metadata
1 MB                          = image / batch / medium payload
1 GB                          = 1,000 MB
1 TB                          = 1,000 GB

Traffic
-------
1M requests/day               ~= 10 QPS average
10M requests/day              ~= 100 QPS average
100M requests/day             ~= 1K QPS average
1B requests/day               ~= 10K QPS average

Peak traffic
------------
Peak QPS                      ~= average QPS * 5 to 10
```

---

## 5. CPU Mental Model

CPU handles computation:

```text
JSON parsing
authentication
validation
business logic
serialization
compression
encryption
hashing
sorting/filtering
query processing
```

Simple model:

```text
CPU time per request * QPS = total CPU time needed per second
```

Formula:

```text
CPU cores needed ~= Peak QPS * CPU_ms_per_request / 1000
```

Example:

```text
Peak QPS = 10,000
CPU work/request = 2 ms

CPU needed = 10,000 * 2 / 1000
           = 20 CPU cores
```

Add buffer:

```text
20 cores raw
+ overhead, GC, spikes, context switching
= maybe 30 to 40 cores total
```

---

## 6. CPU ASCII Diagram

```text
10K requests/sec
       |
       v
+-------------------+
| App Service       |
+-------------------+
| request parsing   |
| auth              |
| validation        |
| business logic    |
| JSON response     |
+-------------------+
       |
       v
CPU time consumed per request
```

If each request is cheap:

```text
1 ms CPU/request at 10K QPS = 10 cores
```

If each request is expensive:

```text
20 ms CPU/request at 10K QPS = 200 cores
```

Small CPU cost matters at high QPS.

---

## 7. RAM Mental Model

RAM stores the active working set:

```text
in-memory cache
connection pools
thread stacks
JVM heap
request buffers
hot objects
Redis keys
database buffer cache
OS page cache
```

Core formula:

```text
RAM needed = hot_items * size_per_item * overhead_factor
```

Example:

```text
10M hot URL mappings
Each mapping raw size = 200 bytes
Raw memory = 10M * 200 bytes = 2 GB
Overhead factor = 2x to 3x
Redis memory needed ~= 4 GB to 6 GB
```

---

## 8. RAM ASCII Diagram

```text
Database has all data
+--------------------------------------------------+
| cold | cold | warm | warm | HOT | HOT | HOT      |
+--------------------------------------------------+
                              |
                              v
                         RAM / Cache
                    +----------------+
                    | hot working set|
                    +----------------+
```

RAM is not for everything.
RAM is for what must be fast.

---

## 9. Disk Mental Model

Disk is about two things:

```text
1. Capacity     -> how much data can be stored?
2. I/O          -> how fast can data be read/written?
```

Storage formula:

```text
Storage = records * record_size
```

Disk write throughput formula:

```text
Disk write MB/sec = writes_per_sec * write_size
```

Disk read throughput formula:

```text
Disk read MB/sec = reads_per_sec * read_size
```

Example:

```text
5K writes/sec
Each write = 4 KB

Disk write = 5K * 4 KB
           = 20,000 KB/sec
           = ~20 MB/sec
```

This sounds fine for sequential writes, but random writes, indexes, fsync, and replication make it harder.

---

## 10. Disk IOPS vs Throughput

Disk has two important limits:

```text
IOPS       -> number of read/write operations per second
Throughput -> MB/sec read/write
```

Mental model:

```text
Small random reads/writes  -> IOPS matters
Large sequential files     -> throughput matters
```

ASCII:

```text
Random I/O
----------
read block here
       read block there
              write block elsewhere

Disk jumps around -> IOPS bottleneck


Sequential I/O
--------------
read block 1 -> block 2 -> block 3 -> block 4

Disk streams data -> throughput bottleneck
```

---

## 11. Network Mental Model

Network is data movement.

Formula:

```text
Bandwidth = QPS * payload_size
```

Example:

```text
Peak QPS = 10K
Response size = 20 KB

Bandwidth = 10K * 20 KB
          = 200,000 KB/sec
          = ~200 MB/sec
```

Convert to bits:

```text
200 MB/sec * 8 = 1,600 Mbps = 1.6 Gbps
```

So a "small" 20 KB response at 10K QPS can require serious bandwidth.

---

## 12. Network ASCII Diagram

```text
             10K QPS
Client ----------------> API
       request 2 KB

API -------------------> Client
       response 20 KB

Total response bandwidth:
10K * 20 KB = 200 MB/sec
```

Usually response traffic is larger than request traffic.

---

## 13. Latency Mental Model

Latency is not only CPU.

```text
Total latency =
network time
+ queue waiting
+ CPU processing
+ cache lookup
+ DB query
+ disk I/O
+ serialization
+ response transfer
```

ASCII:

```text
Request timeline

| network | queue | CPU | Redis | DB | CPU | network |
|  10 ms  |  5ms  | 2ms | 1 ms  |20ms| 2ms |  10 ms  |

Total ~= 50 ms
```

A slow API usually has one dominant delay.

---

## 14. CPU vs RAM vs Disk vs Network

```text
CPU high
--------
Symptoms:
- high CPU %
- slow JSON processing
- high GC if JVM allocation heavy
- request latency increases with traffic

Fix:
- optimize code
- reduce serialization
- cache computed results
- add instances
- async/batch expensive work


RAM high
--------
Symptoms:
- OOM
- JVM GC pressure
- Redis eviction
- DB buffer cache misses
- swapping

Fix:
- reduce object size
- expire cache keys
- increase memory
- split hot/cold data
- use pagination/streaming


Disk high
---------
Symptoms:
- slow DB queries
- high write latency
- high IOPS
- WAL/fsync bottleneck
- storage fills quickly

Fix:
- indexes
- batching
- partitioning
- read replicas
- archival
- move blobs to object storage


Network high
------------
Symptoms:
- high egress
- slow large responses
- timeout between services
- load balancer saturation

Fix:
- compression
- pagination
- CDN
- reduce payload
- colocate services
- cache responses
```

---

## 15. One Full Dry Run: URL Shortener

Assume:

```text
100M redirects/day
Peak factor = 10x
Each redirect response = 1 KB
Each click event = 200 bytes
10M hot URLs in Redis
Each cached mapping = 200 bytes raw
```

### Step 1: QPS

```text
100M/day / 100K seconds
= ~1K QPS average

Peak QPS = 1K * 10
= ~10K QPS
```

### Step 2: Network

```text
10K QPS * 1 KB response
= 10 MB/sec response bandwidth
```

This is manageable.

### Step 3: Redis RAM

```text
10M hot URLs * 200 bytes
= 2 GB raw

With 3x overhead:
2 GB * 3 = 6 GB
```

Redis should have more than 6 GB memory, ideally with safety buffer.

### Step 4: Disk/Event Storage

```text
100M click events/day * 200 bytes
= 20 GB/day
= ~7.3 TB/year
```

Click analytics should not be stored only in the transactional DB forever.

### Conclusion

```text
CPU: mostly light redirect logic
RAM: Redis hot mapping cache important
Disk: click events grow fast
Network: redirect response small, okay
Architecture: Redis + Postgres + Kafka + analytics storage
```

ASCII:

```text
Client
  |
  v
Redirect API
  |
  +--> Redis hot mapping
  |
  +--> Postgres fallback
  |
  +--> Kafka click event ---> Analytics storage
```

---

## 16. One Full Dry Run: News Feed

Assume:

```text
10M DAU
Each user opens feed 10 times/day
Each feed response has 20 posts
Each post metadata = 1 KB
Peak factor = 10x
```

### Step 1: QPS

```text
10M users * 10 opens/day
= 100M feed requests/day

100M / 100K seconds
= ~1K average QPS

Peak = ~10K QPS
```

### Step 2: Network

```text
Each response = 20 posts * 1 KB
= 20 KB

Peak bandwidth = 10K * 20 KB
= 200 MB/sec
= 1.6 Gbps
```

### Step 3: CPU

Assume:

```text
CPU per request = 3 ms
```

```text
CPU cores = 10K * 3 / 1000
= 30 cores raw
```

With buffer:

```text
~45 to 60 cores across service instances
```

### Step 4: RAM

If caching timelines:

```text
10M users
Each cached timeline = 100 post IDs
Each post ID = 8 bytes

Raw = 10M * 100 * 8
= 8 GB raw

With overhead maybe 2x to 3x:
= 16 GB to 24 GB
```

### Conclusion

```text
Network: large because feed responses are bigger
CPU: moderate
RAM: timeline cache useful but must be sized
Disk: posts and events grow over time
Architecture: Redis timeline cache + post store + CDN for media
```

---

## 17. One Full Dry Run: Image Upload

Assume:

```text
1M image uploads/day
Average image size = 2 MB
Metadata per image = 1 KB
```

### Storage

```text
Image storage/day = 1M * 2 MB
                  = 2 TB/day

Image storage/year = 2 TB * 365
                   = 730 TB/year
```

### Metadata storage

```text
1M * 1 KB = 1 GB/day
```

### Conclusion

```text
Do not store images in database.
Store images in object storage.
Store only metadata in DB.
Use CDN for serving images.
Use async workers for resizing/compression.
```

ASCII:

```text
Client
  |
  v
Upload API
  |
  +--> Object Storage / S3
  |
  +--> Metadata DB
  |
  +--> Queue ---> Image Resize Worker
                  |
                  v
                CDN
```

---

## 18. Resource Bottleneck Decision Tree

```text
Start with peak QPS
        |
        v
Large CPU work per request?
        |
   yes  v
      CPU bottleneck
      optimize/add instances/async
        |
        no
        v
Large hot working set?
        |
   yes  v
      RAM bottleneck
      cache sizing/eviction/sharding
        |
        no
        v
Large durable reads/writes?
        |
   yes  v
      Disk/DB bottleneck
      indexes/replicas/partitioning/batching
        |
        no
        v
Large payloads/media?
        |
   yes  v
      Network bottleneck
      CDN/compression/pagination
```

---

## 19. Quick Capacity Formula Sheet

```text
QPS
---
Average QPS ~= requests_per_day / 100K
Peak QPS    ~= average QPS * 5 to 10

CPU
---
CPU cores ~= peak_QPS * CPU_ms_per_request / 1000

RAM
---
RAM ~= hot_items * item_size * overhead_factor

Disk capacity
-------------
Storage ~= records * record_size
Storage/year ~= storage/day * 365

Disk throughput
---------------
Write MB/sec ~= writes_per_sec * write_size
Read MB/sec  ~= reads_per_sec * read_size

Network
-------
Bandwidth ~= QPS * payload_size
Mbps ~= MB/sec * 8
Gbps ~= MB/sec * 8 / 1000

Servers
-------
Instances ~= peak_QPS / safe_QPS_per_instance
```

---

## 20. Common Backend Sizes

```text
Small metadata record             200 bytes to 1 KB
User profile                      ~1 KB
Chat message                      500 bytes to 2 KB
Click/log event                   200 bytes to 1 KB
Feed post metadata                1 KB to 5 KB
API JSON response                 1 KB to 100 KB
Thumbnail image                   20 KB to 200 KB
Normal image                      500 KB to 5 MB
Short video                       5 MB to 100+ MB
```

Use rounded numbers in interviews:

```text
URL record      ~= 500 bytes
User record     ~= 1 KB
Message         ~= 1 KB
Event           ~= 500 bytes
Image           ~= 1 MB to 2 MB
```

---

## 21. Architecture Hints Based On Resource Pressure

```text
CPU pressure
------------
- add app instances
- optimize expensive code
- cache computed values
- use async workers
- avoid heavy synchronous compression/encryption when possible


RAM pressure
------------
- reduce hot working set
- expire cache entries
- split cache by tenant/user/region
- avoid loading huge result sets
- stream large files instead of buffering


Disk pressure
-------------
- add indexes carefully
- use read replicas
- partition large tables
- batch writes
- move logs/events to analytics storage
- move blobs to object storage


Network pressure
----------------
- compress responses
- paginate
- reduce payload fields
- use CDN
- use object storage direct upload
- avoid chatty service-to-service calls
```

---

## 22. Production Failure Stories

### Failure 1: CPU Looks Fine In Dev, Fails In Production

```text
Dev traffic: 10 QPS
Production traffic: 10K QPS
CPU/request: 5 ms
```

```text
CPU cores needed = 10K * 5 / 1000 = 50 cores
```

One small service instance cannot handle it.

Lesson:

```text
Tiny CPU work becomes huge at high QPS.
```

---

### Failure 2: Redis OOM After Feature Launch

```text
Expected:
1M hot keys * 200 bytes = 200 MB

Reality:
20M hot keys * 500 bytes * 3x overhead = 30 GB
```

Lesson:

```text
Cache size must include object overhead and key growth.
```

---

### Failure 3: Database Slow Because Of Random Reads

```text
Query pattern:
1000 QPS
Each request reads 50 random rows
= 50K random reads/sec
```

Even if storage capacity is enough, random I/O can become the bottleneck.

Lesson:

```text
Storage size and disk speed are different problems.
```

---

### Failure 4: API Gateway Saturated By Large Responses

```text
20K QPS
Response = 100 KB

Bandwidth = 20K * 100 KB
          = 2,000 MB/sec
          = 16 Gbps
```

Lesson:

```text
Large payloads can break network before CPU breaks.
```

---

## 23. Common Interview Red Flags

Avoid:

```text
❌ No peak QPS
❌ No payload size estimate
❌ No cache memory estimate
❌ Ignoring CPU per request
❌ Ignoring DB random reads/writes
❌ Storing images/videos in relational DB
❌ Assuming average QPS is enough
❌ Adding Kafka/Redis/sharding without bottleneck reason
❌ Forgetting replication, backup, and index size
```

Strong signals:

```text
✅ Estimate average and peak QPS
✅ Estimate CPU cores from CPU/request
✅ Estimate RAM from hot working set
✅ Estimate storage/day and storage/year
✅ Estimate bandwidth from payload size
✅ Identify the bottleneck
✅ Choose architecture based on numbers
```

---

## 24. Interview Speaking Template

Use this answer shape:

```text
1. Let me estimate traffic first.
2. Daily requests convert to average QPS.
3. Peak QPS is probably 5x to 10x.
4. Now I estimate payload size and bandwidth.
5. Then I estimate hot data for RAM/cache.
6. Then I estimate durable storage and I/O.
7. Based on the bottleneck, I choose the architecture.
```

Example:

```text
Assume 100M requests/day.
That is roughly 1K average QPS.
Peak may be 10K QPS.
If each response is 20 KB, peak bandwidth is about 200 MB/sec.
If each request needs 2 ms CPU, we need around 20 CPU cores raw.
If 10M hot records must be cached and each is 200 bytes, raw cache is 2 GB,
but Redis overhead may make it 4 to 6 GB.
So I would use multiple app instances, Redis for hot reads, Postgres for source
of truth, and Kafka only if events can be processed asynchronously.
```

---

## 25. Practice Table

For every system design problem, fill this:

```text
System: ____________________________

Requests/day: ______________________
Average QPS: _______________________
Peak QPS: __________________________
CPU ms/request: ____________________
CPU cores needed: __________________

Hot items: _________________________
Size per hot item: _________________
RAM/cache needed: _________________

Writes/sec: ________________________
Write size: ________________________
Disk write MB/sec: ________________

Reads/sec: _________________________
Read size: _________________________
Disk read MB/sec: _________________

Response size: _____________________
Network MB/sec: ____________________

Main bottleneck: ___________________
Architecture decision: _____________
```

---

## 26. Mini Practice Problems

### Problem A: Chat System

```text
1M active users
50 messages/user/day
Message size = 1 KB
Peak factor = 10x
```

Estimate:

```text
Messages/day = 1M * 50 = 50M/day
Average QPS = 50M / 100K = 500 QPS
Peak QPS = 5K QPS
Storage/day = 50M * 1 KB = 50 GB/day
Storage/year = 18.25 TB/year
```

Decision:

```text
Need partitioned message storage.
Use queue for fanout/notifications.
Cache recent conversations.
```

---

### Problem B: Rate Limiter

```text
100M requests/day
Need per-user counters
10M active users/day
Counter entry = 100 bytes
```

Estimate:

```text
Average QPS = 1K
Peak QPS = 10K
RAM raw = 10M * 100 bytes = 1 GB
With overhead = 2 GB to 3 GB
```

Decision:

```text
Redis can work if memory is sized correctly.
Use TTL for counters.
Shard if key count/QPS grows.
```

---

### Problem C: Logging Platform

```text
50K events/sec
Each log event = 500 bytes
```

Estimate:

```text
Write throughput = 50K * 500 bytes
                 = 25 MB/sec

Storage/day = 25 MB/sec * 86,400 sec
            ~= 2.16 TB/day
```

Decision:

```text
Do not store all logs in OLTP DB.
Use Kafka + object storage/search analytics.
Apply retention policy.
```

---

## 27. Final Cheat Sheet

```text
CPU
---
Question: How much compute per request?
Formula:  cores ~= peak_QPS * CPU_ms / 1000
Fixes:    optimize, cache, async, add instances

RAM
---
Question: What hot data must fit in memory?
Formula:  RAM ~= hot_items * item_size * overhead
Fixes:    TTL, eviction, smaller objects, sharding

Disk
----
Question: How much durable data and I/O?
Formula:  storage ~= records * size
Formula:  MB/sec ~= ops/sec * size
Fixes:    indexes, replicas, partitioning, batching, archival

Network
-------
Question: How much data moves per second?
Formula:  bandwidth ~= QPS * payload_size
Fixes:    compression, pagination, CDN, reduce payload
```

---

## 28. Final One-Picture Summary

```text
                       BACKEND CAPACITY

                           Users
                             |
                             v
                       Requests/day
                             |
                             v
                    Average QPS -> Peak QPS
                             |
        +--------------------+--------------------+
        |                    |                    |
        v                    v                    v
      CPU                  RAM                  Disk
 CPU/request          hot working set       records + I/O
 cores needed         cache size            storage/year
        |                    |                    |
        +--------------------+--------------------+
                             |
                             v
                          Network
                    QPS * response size
                             |
                             v
                  Architecture Decision
        cache / queue / shard / replica / CDN / more nodes
```

---

## 29. What You Should Remember

CPU, RAM, Disk, and Network are not separate interview trivia.
They are the physical limits behind every architecture decision.

```text
CPU      decides compute capacity
RAM      decides hot data capacity
Disk     decides durable storage and I/O capacity
Network  decides transfer capacity
```

The best system design answers are simple:

```text
Estimate numbers.
Find the bottleneck.
Pick the simplest architecture that survives peak load.
```

That is the entire game.
