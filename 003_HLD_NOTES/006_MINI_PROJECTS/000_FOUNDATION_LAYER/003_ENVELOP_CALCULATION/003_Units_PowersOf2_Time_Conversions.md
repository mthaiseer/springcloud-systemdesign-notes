# 003_Units_PowersOf2_Time_Conversions.md

# Units, Powers of Two, and Time Conversions

> **Goal:** Build the fastest mental conversion toolkit for back-of-the-envelope estimation, system design interviews, backend capacity planning, storage sizing, cache sizing, bandwidth estimation, and production debugging.

---

## 0. One-Line Definition

**Units, powers of two, and time conversions** are the basic language of system design numbers.

They help you quickly convert:

```text
Requests/day  -> QPS
Records       -> GB/TB storage
Response size -> bandwidth
Memory items  -> Redis/RAM size
Latency       -> realistic service design
```

You are not memorizing math for exams.
You are memorizing numbers that help you make architecture decisions fast.

```text
Exact conversion       ❌ not always needed
Fast useful estimate   ✅ required
Correct magnitude      ✅ required
Clear reasoning        ✅ required
```

---

## 1. Why This Skill Exists

In real backend systems, most design questions become unit-conversion questions:

```text
100M requests/day is how many QPS?
1B rows of 1 KB each is how much storage?
10K QPS with 20 KB response is how much bandwidth?
50M Redis keys of 200 bytes each need how much memory?
1 ms vs 100 ms changes which design decision?
```

Without unit fluency, estimation becomes slow and error-prone.

With unit fluency, you can quickly say:

```text
This is only GB scale.
This is already TB scale.
This fits in memory.
This cannot fit in memory.
This needs CDN.
This needs async workers.
This DB will not handle peak traffic alone.
```

---

## 2. The Core Mental Model

Back-of-the-envelope estimation is mostly about moving across this chain:

```text
Count  ->  Size  ->  Time  ->  Rate  ->  Machine decision
```

ASCII view:

```text
+--------+      +------+      +------+      +------+      +----------+
| Count  | ---> | Size | ---> | Time | ---> | Rate | ---> | Design   |
+--------+      +------+      +------+      +------+      +----------+
   |              |            |            |              |
   v              v            v            v              v
 records        bytes        seconds       QPS          DB/cache/CDN
 users          KB/MB/GB     days/months   MB/s         queue/shards
```

Most mistakes happen because engineers mix units.

```text
Wrong:  100M requests/day = 100M QPS
Right:  100M requests/day / 100K sec/day = ~1K QPS
```

---

## 3. The Big Idea: Use Approximate Engineering Units

Computers use powers of two internally.
Humans estimate using powers of ten.

For interviews and architecture discussions, use simple rounded numbers:

```text
1 KB  ~= 1,000 bytes
1 MB  ~= 1,000 KB
1 GB  ~= 1,000 MB
1 TB  ~= 1,000 GB
1 day ~= 100,000 seconds
```

Exact binary values are useful when needed:

```text
1 KiB = 1,024 bytes
1 MiB = 1,024 KiB
1 GiB = 1,024 MiB
```

But for fast estimation:

```text
1,024 ~= 1,000
86,400 ~= 100,000
365 ~= 400 when rough, or 365 when storage/year matters
```

---

## 4. Decimal vs Binary Units

There are two common unit systems.

### Decimal units

Used often in product specs, disks, cloud pricing, and rough estimation.

```text
1 KB = 1,000 bytes
1 MB = 1,000,000 bytes
1 GB = 1,000,000,000 bytes
1 TB = 1,000,000,000,000 bytes
```

### Binary units

Used often in RAM, OS memory, low-level systems, and powers of two.

```text
1 KiB = 2^10 bytes = 1,024 bytes
1 MiB = 2^20 bytes ~= 1 million bytes
1 GiB = 2^30 bytes ~= 1 billion bytes
1 TiB = 2^40 bytes ~= 1 trillion bytes
```

Interview rule:

```text
Use decimal units for fast estimation.
Mention binary units only if memory precision matters.
```

ASCII:

```text
Fast estimation path

KB -> MB -> GB -> TB
 |    |    |    |
1000 1000 1000 1000

Binary mental model

2^10 -> 2^20 -> 2^30 -> 2^40
 KiB    MiB    GiB    TiB
```

---

## 5. Must-Memorize Storage Units

```text
1 byte         = 8 bits
1 KB           ~= 1 thousand bytes
1 MB           ~= 1 million bytes
1 GB           ~= 1 billion bytes
1 TB           ~= 1 trillion bytes
1 PB           ~= 1 quadrillion bytes
```

Fast mental table:

```text
1 KB * 1M records  ~= 1 GB
1 KB * 1B records  ~= 1 TB
1 MB * 1M files    ~= 1 TB
1 GB/day           ~= 365 GB/year
10 GB/day          ~= 3.65 TB/year
100 GB/day         ~= 36.5 TB/year
1 TB/day           ~= 365 TB/year
```

This is extremely high ROI.

---

## 6. Must-Memorize Powers of Two

These are the most useful powers of two for backend and CP/DSA thinking.

```text
2^10  = 1,024          ~= 1 thousand
2^20  = 1,048,576      ~= 1 million
2^30  = 1,073,741,824  ~= 1 billion
2^40  ~= 1 trillion
2^50  ~= 1 quadrillion
2^60  ~= 1 quintillion
```

Why this matters:

```text
2^10 ~= KB scale
2^20 ~= MB scale / 1M combinations
2^30 ~= GB scale / 1B combinations
2^40 ~= TB scale
```

ASCII ladder:

```text
2^10        2^20        2^30        2^40
 |           |           |           |
1K          1M          1B          1T
 |           |           |           |
KB-ish      MB-ish      GB-ish      TB-ish
```

---

## 7. Small Powers of Two Worth Knowing

Useful for bitmasks, hashing, partitions, shards, and CP problems.

```text
2^0  = 1
2^1  = 2
2^2  = 4
2^3  = 8
2^4  = 16
2^5  = 32
2^6  = 64
2^7  = 128
2^8  = 256
2^9  = 512
2^10 = 1024
```

Why these matter:

```text
64 shards
128 partitions
256 buckets
512 MB memory chunk
1024 connections
```

ASCII:

```text
2^6   2^7    2^8    2^9    2^10
64 -> 128 -> 256 -> 512 -> 1024
```

---

## 8. Time Conversions You Must Memorize

```text
1 second  = 1,000 milliseconds
1 minute  = 60 seconds
1 hour    = 3,600 seconds
1 day     = 86,400 seconds ~= 100,000 seconds
1 week    = 7 days
1 month   ~= 30 days
1 year    ~= 365 days
```

Fast estimation table:

```text
1 day    ~= 100K seconds
1 week   ~= 700K seconds
1 month  ~= 3M seconds
1 year   ~= 30M seconds
```

Exact-ish:

```text
30 days * 86,400 sec/day = 2,592,000 sec ~= 2.6M sec
365 days * 86,400 sec/day = 31,536,000 sec ~= 31.5M sec
```

Interview shortcut:

```text
Use 100K seconds/day for QPS conversion.
Use 30M seconds/year for long-term rate conversion.
```

---

## 9. The Most Important Time Shortcut

```text
1 day ~= 100,000 seconds
```

This gives the magic QPS table:

```text
1M requests/day      ~= 10 QPS
10M requests/day     ~= 100 QPS
100M requests/day    ~= 1K QPS
1B requests/day      ~= 10K QPS
10B requests/day     ~= 100K QPS
```

Why:

```text
QPS = requests per day / seconds per day
QPS ~= requests per day / 100K
```

ASCII:

```text
Daily requests        divide by 100K        Average QPS
-------------------------------------------------------
1,000,000       ----------------------->       10
10,000,000      ----------------------->       100
100,000,000     ----------------------->       1,000
1,000,000,000   ----------------------->       10,000
```

---

## 10. Latency Units Mental Model

Latency decides whether something can be synchronous or must be async/cached.

```text
1 ns   = CPU-level tiny operation
1 us   = very fast local system operation
1 ms   = fast service/cache/network operation
10 ms  = acceptable fast backend step
100 ms = noticeable but often acceptable API latency
1 sec  = slow for interactive APIs
```

Backend intuition:

```text
< 1 ms       memory/cache/local computation
1-10 ms      Redis/local network/service call
10-100 ms    DB query/API call range
100-500 ms   user-visible backend latency
> 1 sec      async candidate or bad UX
```

Do not treat these as exact constants.
Treat them as smell detectors.

ASCII:

```text
CPU/RAM      Cache      DB/API        User-visible       Too slow
 |            |           |                |               |
ns/us        1ms        10-100ms        100-500ms         >1s
```

---

## 11. Bandwidth Units

Bandwidth is data per second.

```text
Bandwidth = QPS * response_size
```

Common units:

```text
KB/s
MB/s
GB/s
Mbps
Gbps
```

Important conversion:

```text
1 byte = 8 bits
1 MB/s ~= 8 Mbps
100 MB/s ~= 800 Mbps ~= 0.8 Gbps
1 GB/s ~= 8 Gbps
```

Example:

```text
10K QPS
Each response = 20 KB
Bandwidth = 10K * 20 KB = 200,000 KB/s = 200 MB/s
In bits = 200 * 8 = 1,600 Mbps = 1.6 Gbps
```

ASCII:

```text
QPS      Response size      Bandwidth
 |             |                |
10K     *     20 KB      =     200 MB/s
```

---

## 12. Storage Formula

The most common storage formula:

```text
Storage = number_of_records * size_per_record
```

Example:

```text
100M records
Each record = 1 KB
Storage = 100M KB = 100 GB
```

Fast shortcut:

```text
1M records * 1 KB = 1 GB
```

So:

```text
100M records * 1 KB
= 100 * (1M records * 1 KB)
= 100 GB
```

ASCII:

```text
records                  size                 storage
100M        x            1 KB       =         100 GB
```

---

## 13. Cache Memory Formula

Cache size is hot item count times item size, plus overhead.

```text
Cache memory = hot_items * item_size * overhead_factor
```

Rule of thumb:

```text
Redis overhead factor ~= 2x to 3x for rough planning
```

Example:

```text
10M hot URL mappings
Each mapping raw size = 200 bytes
Raw memory = 10M * 200 bytes = 2 GB
With overhead 2x to 3x = 4 GB to 6 GB
```

ASCII:

```text
Raw data
+----------------------+ 2 GB

Redis actual estimate
+----------------------+----------------------+ 4-6 GB
 raw data               metadata/overhead
```

---

## 14. Event Storage Formula

Events/logs grow faster than core database records.

```text
Event storage/day = events/day * event_size
```

Example:

```text
100M click events/day
Each event = 200 bytes
Storage/day = 20 GB/day
Storage/year = 20 * 365 = 7.3 TB/year
```

Conclusion:

```text
Core metadata may fit in Postgres.
Event/log data may need Kafka + object storage + analytics DB.
```

ASCII:

```text
User action ---> API ---> Transaction DB
                    
                     +---> Event log ---> Kafka ---> Data lake / OLAP
```

---

## 15. Time-to-QPS Dry Run

Problem:

```text
A service receives 300M requests/day.
Estimate average and peak QPS.
```

Step 1:

```text
1 day ~= 100K seconds
```

Step 2:

```text
Average QPS = 300M / 100K = 3K QPS
```

Step 3:

```text
Peak QPS ~= average * 10 = 30K QPS
```

Conclusion:

```text
Design for around 30K peak QPS, not only 3K average QPS.
```

ASCII:

```text
300M/day
   |
   | divide by 100K sec/day
   v
3K average QPS
   |
   | multiply by 10 for peak
   v
30K peak QPS
```

---

## 16. Storage Dry Run

Problem:

```text
You store 50M chat messages/day.
Each message is 1 KB.
Estimate storage per day and per year.
```

Step 1:

```text
1M records * 1 KB = 1 GB
```

Step 2:

```text
50M messages * 1 KB = 50 GB/day
```

Step 3:

```text
50 GB/day * 365 = 18,250 GB/year = 18.25 TB/year
```

Conclusion:

```text
Chat storage becomes TB scale quickly.
Partition by conversation/user/time.
Use archival/lifecycle policies for old messages.
```

ASCII:

```text
50M messages/day
       |
       v
50 GB/day
       |
       v
18 TB/year
```

---

## 17. Bandwidth Dry Run

Problem:

```text
Peak QPS = 20K
Average response = 50 KB
Estimate bandwidth.
```

Step 1:

```text
Bandwidth = QPS * response_size
```

Step 2:

```text
20K * 50 KB = 1,000,000 KB/s
```

Step 3:

```text
1,000,000 KB/s = 1,000 MB/s = 1 GB/s
```

Step 4:

```text
1 GB/s ~= 8 Gbps
```

Conclusion:

```text
This is high network traffic.
Use compression, CDN, pagination, caching, or smaller response payloads.
```

ASCII:

```text
20K QPS * 50 KB
        |
        v
1 GB/s ~= 8 Gbps
```

---

## 18. Powers of Two Dry Run

Problem:

```text
A hash key has 64 bits.
How many possible values are there approximately?
```

Step 1:

```text
2^10 ~= 10^3
```

Step 2:

```text
2^60 = (2^10)^6 ~= (10^3)^6 = 10^18
```

Step 3:

```text
2^64 = 2^4 * 2^60 ~= 16 * 10^18 = 1.6 * 10^19
```

Conclusion:

```text
64-bit space is enormous for many practical hashing/id use cases,
but collision probability still depends on how many keys you generate.
```

ASCII:

```text
2^64
 = 2^4 * 2^60
 = 16  * ~10^18
 ~= 1.6e19
```

---

## 19. Practical Backend Examples

### Example A: URL Shortener Metadata

```text
1M new URLs/day
Each metadata row = 500 bytes
Storage/day = 500 MB/day
Storage/year = 182 GB/year
```

Conclusion:

```text
Metadata fits in a relational DB initially.
Need indexes, backups, and growth planning.
```

---

### Example B: URL Shortener Click Events

```text
100M redirects/day
Each click event = 200 bytes
Storage/day = 20 GB/day
Storage/year = 7.3 TB/year
```

Conclusion:

```text
Click analytics should be async and stored outside the main OLTP path.
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
Never store images directly in the database.
Use object storage and CDN.
```

ASCII:

```text
Client ---> Upload API ---> Object Storage
              |
              v
          Metadata DB
```

---

## 20. Common Record Sizes To Remember

These are rough interview-friendly values.

```text
User profile row        ~= 1 KB
URL metadata row        ~= 500 B to 1 KB
Chat message            ~= 500 B to 2 KB
Click/log event         ~= 200 B to 1 KB
Small JSON response     ~= 1 KB to 10 KB
Feed response           ~= 10 KB to 100 KB
Small image             ~= 100 KB to 500 KB
Normal image            ~= 1 MB to 5 MB
Short video             ~= many MB
```

Use these to avoid getting stuck.

---

## 21. Unit Decision Tree

```text
Start with problem number
 |
 v
Is it traffic over time?
 |       
 +--> Yes ---> convert requests/day to QPS
 |
 +--> No
        |
        v
Is it data count * size?
 |
 +--> Yes ---> convert to KB/MB/GB/TB
 |
 +--> No
        |
        v
Is it response size * QPS?
 |
 +--> Yes ---> convert to MB/s or Gbps
 |
 +--> No
        |
        v
Is it hot data in memory?
 |
 +--> Yes ---> raw size * 2x/3x overhead
```

---

## 22. Common Mistakes

```text
❌ Confusing requests/day with QPS
❌ Forgetting peak QPS
❌ Forgetting 1 byte = 8 bits
❌ Saying 1M records * 1 KB = 1 MB
❌ Forgetting storage/year
❌ Forgetting Redis/cache overhead
❌ Forgetting replication factor
❌ Mixing MB/s and Mbps
❌ Treating exact precision as more important than magnitude
```

Correct habits:

```text
✅ Convert daily traffic using 100K seconds/day
✅ Always estimate average and peak QPS
✅ Use 1M * 1 KB = 1 GB shortcut
✅ Use bandwidth = QPS * response size
✅ Add 2x to 3x overhead for cache estimates
✅ Add replication and backup cost for storage
✅ Round aggressively but explain clearly
```

---

## 23. Interview Speaking Template

Use this exact flow:

```text
Let me convert the numbers first.
1 day is about 100K seconds.
So X requests/day becomes about Y average QPS.
Peak can be 5x to 10x, so I will design for Z peak QPS.
For storage, records times record size gives A GB/day.
Yearly storage is about B TB/year.
Based on that, I would use ...
```

Example:

```text
Let me assume 100M feed reads/day.
Using 100K seconds/day, that is about 1K average QPS.
Peak can be 10K QPS.
If each response is 20 KB, peak bandwidth is about 200 MB/s.
So I would cache timelines in Redis and use pagination/compression/CDN where needed.
```

---

## 24. One Full Dry Run: News Feed

Problem:

```text
Design a news feed for 10M DAU.
Each user opens feed 10 times/day.
Each feed response returns 20 posts.
Each post metadata is 1 KB.
```

### Step 1: Requests/day

```text
10M users * 10 opens/day = 100M feed requests/day
```

### Step 2: Average QPS

```text
100M / 100K seconds = 1K QPS average
```

### Step 3: Peak QPS

```text
Peak QPS ~= 1K * 10 = 10K QPS
```

### Step 4: Response size

```text
20 posts * 1 KB/post = 20 KB/response
```

### Step 5: Peak bandwidth

```text
10K QPS * 20 KB = 200 MB/s
200 MB/s * 8 = 1.6 Gbps
```

### Step 6: Architecture conclusion

```text
Read path is heavy.
Use Redis cached timelines.
Use DB/Cassandra for durable posts.
Use Kafka for fanout events.
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

## 25. Production Debugging Example

Incident:

```text
Redis memory increased to 70 GB.
Is it normal or a leak?
```

Estimate expected memory:

```text
Hot keys = 20M
Average key+value size = 500 bytes
Raw memory = 20M * 500 bytes = 10 GB
Redis overhead 3x = 30 GB expected
```

Observation:

```text
Actual memory = 70 GB
Expected memory = ~30 GB
```

Conclusion:

```text
This looks abnormal.
Check TTL missing, key explosion, large values, duplicate cache entries, or bad cache key design.
```

ASCII debugging flow:

```text
Expected memory estimate
          |
          v
Compare with actual Redis memory
          |
          +--> close enough ---> likely normal growth
          |
          +--> much higher ---> investigate leak/key explosion
```

---

## 26. Practice Table

Fill this for every system design problem:

```text
System: __________________________

Users/DAU: _______________________
Requests/user/day: _______________
Requests/day: ____________________
Average QPS: _____________________
Peak QPS: ________________________
Record size: _____________________
Storage/day: _____________________
Storage/year: ____________________
Response size: ___________________
Bandwidth: _______________________
Hot data count: __________________
Cache memory: ____________________
Bottleneck: ______________________
Design decision: _________________
```

Practice systems:

```text
URL Shortener
Chat System
News Feed
Notification System
Rate Limiter
Ride Matching
File Upload
Search Autocomplete
Logging Platform
Payment Events
```

---

## 27. Cheat Sheet

```text
Storage units
-------------
1 byte = 8 bits
1 KB ~= 1 thousand bytes
1 MB ~= 1 million bytes
1 GB ~= 1 billion bytes
1 TB ~= 1 trillion bytes

Binary powers
-------------
2^10 ~= 1K
2^20 ~= 1M
2^30 ~= 1B
2^40 ~= 1T
2^50 ~= 1Q
2^60 ~= 1e18

Time
----
1 sec = 1000 ms
1 min = 60 sec
1 hour = 3600 sec
1 day = 86,400 sec ~= 100K sec
1 month ~= 30 days ~= 2.6M sec ~= 3M sec
1 year ~= 365 days ~= 31.5M sec ~= 30M sec

QPS shortcuts
-------------
1M/day   ~= 10 QPS
10M/day  ~= 100 QPS
100M/day ~= 1K QPS
1B/day   ~= 10K QPS
10B/day  ~= 100K QPS

Storage shortcuts
-----------------
1M * 1 KB = 1 GB
1B * 1 KB = 1 TB
1M * 1 MB = 1 TB
1 GB/day = 365 GB/year
10 GB/day = 3.65 TB/year
100 GB/day = 36.5 TB/year

Bandwidth
---------
Bandwidth = QPS * response_size
1 byte = 8 bits
1 MB/s ~= 8 Mbps
100 MB/s ~= 0.8 Gbps
1 GB/s ~= 8 Gbps

Cache
-----
Cache = hot_items * item_size * overhead
Redis rough overhead = 2x to 3x

Peak
----
Peak QPS ~= average QPS * 5 to 10
```

---

## 28. Final One-Picture Summary

```text
                 UNIT CONVERSION ENGINE

         +----------------+      +----------------+
         | Requests/day   | ---> | QPS / Peak QPS |
         +----------------+      +----------------+
                  |                       |
                  v                       v
         +----------------+      +----------------+
         | Record count   | ---> | GB / TB storage|
         +----------------+      +----------------+
                  |                       |
                  v                       v
         +----------------+      +----------------+
         | Response size  | ---> | MB/s / Gbps    |
         +----------------+      +----------------+
                  |                       |
                  v                       v
         +-----------------------------------------+
         | Architecture Decision                    |
         | DB / Cache / Queue / Shard / CDN / Async |
         +-----------------------------------------+
```

---

## 29. What You Should Remember

The most valuable numbers are:

```text
1 day ~= 100K seconds
1M requests/day ~= 10 QPS
1M records * 1 KB ~= 1 GB
1B records * 1 KB ~= 1 TB
1 MB/s ~= 8 Mbps
2^10 ~= 1K, 2^20 ~= 1M, 2^30 ~= 1B
Peak QPS ~= average QPS * 5 to 10
Cache memory ~= raw size * 2 to 3
```

Final mental model:

```text
Units -> Rates -> Storage -> Bandwidth -> Bottlenecks -> Architecture
```

That is the foundation of fast system design estimation.
