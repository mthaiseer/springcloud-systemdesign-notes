# 063_System_Design_Interview_Answer.md
# MiniURLShortener — System Design Interview Answer

> Core mental model: **A system design interview answer is not a random architecture diagram. It is a controlled conversation that moves from requirements → scale → APIs → data model → high-level design → deep dives → bottlenecks → tradeoffs → failure handling.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. What Interviewers Actually Evaluate](#3-what-interviewers-actually-evaluate)
- [4. The 7-Step System Design Answer Framework](#4-the-7-step-system-design-answer-framework)
- [5. Step 1: Clarify Requirements](#5-step-1-clarify-requirements)
- [6. Step 2: Define Scale And Capacity](#6-step-2-define-scale-and-capacity)
- [7. Step 3: API Design](#7-step-3-api-design)
- [8. Step 4: Data Model](#8-step-4-data-model)
- [9. Step 5: High-Level Architecture](#9-step-5-high-level-architecture)
- [10. Step 6: Deep Dives](#10-step-6-deep-dives)
- [11. Step 7: Bottlenecks, Tradeoffs, And Evolution](#11-step-7-bottlenecks-tradeoffs-and-evolution)
- [12. URL Shortener Full Interview Answer](#12-url-shortener-full-interview-answer)
- [13. Request Flow: Create Short URL](#13-request-flow-create-short-url)
- [14. Request Flow: Redirect Short URL](#14-request-flow-redirect-short-url)
- [15. Cache Strategy Interview Answer](#15-cache-strategy-interview-answer)
- [16. Database Scaling Interview Answer](#16-database-scaling-interview-answer)
- [17. Kafka Analytics Interview Answer](#17-kafka-analytics-interview-answer)
- [18. Consistency And Correctness Answer](#18-consistency-and-correctness-answer)
- [19. Availability And Failure Handling Answer](#19-availability-and-failure-handling-answer)
- [20. Observability Answer](#20-observability-answer)
- [21. Security And Abuse Answer](#21-security-and-abuse-answer)
- [22. Step-by-Step Dry Runs](#22-step-by-step-dry-runs)
- [23. Common Interview Traps](#23-common-interview-traps)
- [24. Senior-Level Tradeoff Language](#24-senior-level-tradeoff-language)
- [25. 30-Minute Interview Timeline](#25-30-minute-interview-timeline)
- [26. 45-Minute Interview Timeline](#26-45-minute-interview-timeline)
- [27. FAANG-Style Follow-Up Questions](#27-faang-style-follow-up-questions)
- [28. Final 10/10 Spoken Answer](#28-final-1010-spoken-answer)
- [29. Senior Engineer Checklist](#29-senior-engineer-checklist)
- [30. One-Page Cheat Sheet](#30-one-page-cheat-sheet)
- [31. One Picture To Remember](#31-one-picture-to-remember)

---

## 1. Why This Exists

Many candidates know Redis, Kafka, Postgres, sharding, load balancers, and Kubernetes.

But in a system design interview, they still fail because they answer like this:

```text
I will use microservices, Redis, Kafka, Kubernetes, Cassandra, CDN, and load balancer.
```

That sounds powerful, but it does not show design thinking.

A strong answer must show:

```text
1. What problem are we solving?
2. What are the requirements?
3. What scale are we designing for?
4. What APIs does the client need?
5. What data must be stored?
6. What is the simplest architecture first?
7. What breaks at scale?
8. What do we add only when needed?
9. What tradeoffs are we making?
10. How do we debug and operate it in production?
```

System design interview is not a memory test.

It is a thinking test.

One-line memory:

```text
Do not throw technologies at the interviewer. Build the system from first principles.
```

---

## 2. The One Core Mental Model

A system design answer is a journey from user need to production reality.

```text
User Need
   |
   v
Requirements
   |
   v
Scale
   |
   v
APIs
   |
   v
Data Model
   |
   v
Architecture
   |
   v
Deep Dives
   |
   v
Failures + Tradeoffs
   |
   v
Production System
```

ASCII mental model:

```text
             SYSTEM DESIGN INTERVIEW ANSWER

+------------------+     +------------------+     +------------------+
| Requirements     | --> | Scale / Numbers  | --> | API Contract     |
+------------------+     +------------------+     +------------------+
          |                         |                         |
          v                         v                         v
+------------------+     +------------------+     +------------------+
| Data Model       | --> | Architecture     | --> | Deep Dive        |
+------------------+     +------------------+     +------------------+
          |                         |                         |
          v                         v                         v
+-------------------------------------------------------------+
| Bottlenecks, Tradeoffs, Failures, Observability, Security    |
+-------------------------------------------------------------+
```

The interviewer is not only asking:

```text
Can you draw boxes?
```

They are asking:

```text
Can you reason like a senior engineer when requirements are incomplete and scale changes?
```

---

## 3. What Interviewers Actually Evaluate

Interviewers usually evaluate five things.

```text
1. Requirement clarity
2. Scalability reasoning
3. Correctness and data modeling
4. Tradeoff thinking
5. Communication structure
```

### Weak signal

```text
I know Redis.
I know Kafka.
I know Kubernetes.
I know Cassandra.
```

### Strong signal

```text
For this use case, redirects are read-heavy and latency-sensitive.
So I would first cache shortCode -> longUrl in Redis using cache-aside.
Postgres remains the source of truth.
Analytics should not block redirects, so click events go asynchronously to Kafka.
If traffic grows, I shard by shortCode because lookup key is shortCode.
```

The second answer is stronger because it explains why.

Senior interviewer listens for:

```text
Why this component?
Why now?
What breaks without it?
What tradeoff does it introduce?
How will you know it is working?
```

---

## 4. The 7-Step System Design Answer Framework

Use this framework for almost every backend system design question.

```text
Step 1: Clarify requirements
Step 2: Estimate scale
Step 3: Define APIs
Step 4: Design data model
Step 5: Draw high-level architecture
Step 6: Deep dive into key bottlenecks
Step 7: Discuss failures, tradeoffs, observability, and evolution
```

ASCII:

```text
Interview Question
      |
      v
+-----------------------+
| 1. Requirements       |
+-----------------------+
      |
      v
+-----------------------+
| 2. Scale              |
+-----------------------+
      |
      v
+-----------------------+
| 3. API                |
+-----------------------+
      |
      v
+-----------------------+
| 4. Data Model         |
+-----------------------+
      |
      v
+-----------------------+
| 5. Architecture       |
+-----------------------+
      |
      v
+-----------------------+
| 6. Deep Dives         |
+-----------------------+
      |
      v
+-----------------------+
| 7. Tradeoffs/Failures |
+-----------------------+
```

Do not skip steps.

If you jump directly to architecture, you may design the wrong system.

---

## 5. Step 1: Clarify Requirements

For URL shortener, start with functional requirements.

```text
Functional requirements:
1. User can create a short URL for a long URL.
2. User can optionally provide a custom alias.
3. User can redirect using short code.
4. System can track click analytics.
5. Short URLs may expire.
6. Admin can block malicious URLs.
```

Then non-functional requirements.

```text
Non-functional requirements:
1. Redirect should be low latency.
2. System should be highly available.
3. Create API can tolerate slightly higher latency.
4. Redirect traffic is much higher than create traffic.
5. Short codes should be unique.
6. Analytics should not slow down redirects.
```

Out of scope clarification:

```text
Out of scope for first version:
1. User billing.
2. Full malware scanning.
3. Social preview generation.
4. Multi-region active-active writes.
5. Advanced dashboard queries.
```

Interview phrase:

```text
Before I design, I want to clarify the core behavior. I assume reads are much higher than writes, redirect latency is critical, and analytics can be eventually consistent. Is that reasonable?
```

This phrase is powerful because it shows you know the shape of the problem.

---

## 6. Step 2: Define Scale And Capacity

You do not need perfect numbers.

You need reasonable numbers that drive design.

Example assumptions:

```text
Create short URL requests: 1,000/sec
Redirect requests: 100,000/sec
Read/write ratio: 100:1
Average long URL size: 500 bytes
Short code length: 7-10 chars
Retention: 5 years
```

Basic math:

```text
Creates per day = 1,000 * 86,400 = 86.4 million/day
Redirects per day = 100,000 * 86,400 = 8.64 billion/day
```

Storage estimate:

```text
Each URL row roughly:
shortCode       16 bytes
longUrl        500 bytes
metadata       200 bytes
indexes        300 bytes
--------------------------------
~1 KB per URL
```

If 86.4 million URLs/day:

```text
86.4 million * 1 KB = ~86 GB/day
5 years = 86 GB * 365 * 5 = ~157 TB
```

This tells us:

```text
1. Redirect path must use cache.
2. Database cannot take every redirect read.
3. Analytics cannot be stored synchronously in primary DB.
4. Long-term storage needs partitioning/sharding/archival.
```

ASCII scale impact:

```text
Small scale:
Client -> App -> Postgres

Large scale:
Client -> LB -> App -> Redis -> Postgres
                       |
                       v
                    Kafka -> Analytics Store
```

Interview phrase:

```text
These numbers suggest the redirect path is the main bottleneck. I will optimize reads first and keep writes strongly consistent enough to guarantee short code uniqueness.
```

---

## 7. Step 3: API Design

Good API design shows contract thinking.

### Create short URL

```http
POST /api/v1/urls
Content-Type: application/json
```

Request:

```json
{
  "longUrl": "https://example.com/products/123",
  "customAlias": "sale2026",
  "expiresAt": "2026-12-31T23:59:59Z"
}
```

Response:

```json
{
  "shortCode": "sale2026",
  "shortUrl": "https://sho.rt/sale2026",
  "longUrl": "https://example.com/products/123",
  "createdAt": "2026-06-25T10:00:00Z",
  "expiresAt": "2026-12-31T23:59:59Z"
}
```

### Redirect

```http
GET /{shortCode}
```

Response:

```http
302 Found
Location: https://example.com/products/123
```

### Analytics

```http
GET /api/v1/urls/{shortCode}/analytics
```

Response:

```json
{
  "shortCode": "sale2026",
  "totalClicks": 120034,
  "uniqueVisitorsEstimate": 85000,
  "topCountries": ["RO", "IN", "DE"]
}
```

### Error response

```json
{
  "status": 404,
  "code": "SHORT_CODE_NOT_FOUND",
  "message": "shortCode not found",
  "path": "/abc123"
}
```

Interview rule:

```text
Do not design database before API. API tells you what data and access patterns are needed.
```

---

## 8. Step 4: Data Model

Main table:

```sql
short_urls
----------
id BIGSERIAL PRIMARY KEY
short_code VARCHAR(32) UNIQUE NOT NULL
long_url TEXT NOT NULL
user_id BIGINT NULL
status VARCHAR(16) NOT NULL
created_at TIMESTAMP NOT NULL
expires_at TIMESTAMP NULL
```

Indexes:

```sql
CREATE UNIQUE INDEX uk_short_code ON short_urls(short_code);
CREATE INDEX idx_user_created_at ON short_urls(user_id, created_at DESC);
CREATE INDEX idx_expires_at ON short_urls(expires_at);
```

Click events should not go into the main relational table synchronously.

Analytics event:

```text
click_events
------------
shortCode
timestamp
ipHash
userAgentHash
country
referrer
```

Storage options:

```text
Hot raw events: Kafka topic
Near real-time aggregation: stream worker
Aggregated counters: Redis / OLAP store / ClickHouse
Historical analytics: S3/data lake
```

ASCII data model:

```text
+-------------------+          +----------------------+
| short_urls        |          | click_events         |
+-------------------+          +----------------------+
| id                |          | shortCode            |
| short_code UNIQUE | <------  | timestamp            |
| long_url          |          | country              |
| status            |          | referrer             |
| expires_at        |          | userAgentHash        |
+-------------------+          +----------------------+
        |                                  |
        | source of truth                  | high volume stream
        v                                  v
   Postgres                          Kafka / Analytics DB
```

Interview phrase:

```text
The main lookup table is optimized for shortCode lookup. Click events are separated because they are high-volume append-only data and should not affect redirect latency.
```

---

## 9. Step 5: High-Level Architecture

Start simple, then evolve.

### Version 1

```text
Client
  |
  v
Load Balancer
  |
  v
Spring Boot App
  |
  v
Postgres
```

This is enough for low scale.

### Version 2: Read-heavy production shape

```text
                         +------------------+
                         | Admin Dashboard  |
                         +------------------+
                                  |
                                  v
+--------+     +----+     +---------------+     +---------+
| Client | --> | LB | --> | Spring Boot   | --> | Redis   |
+--------+     +----+     | URL Service   |     +---------+
                          +---------------+          |
                                  |                  |
                                  v                  |
                              +----------+ <---------+
                              | Postgres |
                              +----------+
                                  |
                                  v
                              +-------+
                              | Kafka |
                              +-------+
                                  |
                                  v
                         +------------------+
                         | Analytics Worker |
                         +------------------+
```

Redirect path:

```text
Client -> LB -> App -> Redis -> 302 redirect
```

Cache miss path:

```text
Client -> LB -> App -> Redis miss -> Postgres -> Redis set -> 302 redirect
```

Analytics path:

```text
App emits click event -> Kafka -> worker aggregates later
```

Senior explanation:

```text
The redirect path is latency-sensitive, so Redis handles hot shortCode lookups. Postgres remains the source of truth. Analytics is asynchronous through Kafka to avoid increasing p99 latency for redirects.
```

---

## 10. Step 6: Deep Dives

Pick deep dives based on bottlenecks.

For URL shortener, best deep dives are:

```text
1. Short code generation and uniqueness
2. Redirect latency and caching
3. Database indexing and sharding
4. Async analytics pipeline
5. Failure handling and fallback
6. Abuse prevention
```

Do not deep dive every component equally.

The interviewer wants judgment.

```text
If system is read-heavy:
    deep dive cache and redirect path

If system has high write volume:
    deep dive ID generation and DB sharding

If analytics is important:
    deep dive Kafka and aggregation

If abuse is important:
    deep dive validation and security
```

Deep dive map:

```text
+---------------------+------------------------------+
| Concern             | Deep Dive                    |
+---------------------+------------------------------+
| uniqueness          | ID generation + DB unique    |
| low latency         | Redis cache-aside            |
| high read traffic   | cache + read replicas        |
| high write traffic  | sharding by shortCode        |
| click analytics     | Kafka async pipeline         |
| abuse               | validation + blocklist       |
| production debugging| metrics + tracing + logs     |
+---------------------+------------------------------+
```

---

## 11. Step 7: Bottlenecks, Tradeoffs, And Evolution

A senior answer always ends with what breaks next.

Possible bottlenecks:

```text
1. Redis hot keys for viral links.
2. Postgres write throughput for massive create traffic.
3. Kafka lag during click spikes.
4. Analytics store overload.
5. Cache stampede after Redis restart.
6. Shard rebalancing complexity.
7. Multi-region consistency issues.
```

Tradeoff examples:

```text
Cache-aside:
    simple and effective, but cache miss adds DB latency.

Async analytics:
    improves redirect latency, but analytics becomes eventually consistent.

Sharding by shortCode:
    scales lookup writes and reads, but cross-shard queries become harder.

Custom aliases:
    user-friendly, but require uniqueness checks and conflict handling.

Multi-region active-active:
    improves global latency, but uniqueness and consistency become harder.
```

Evolution path:

```text
Phase 1: Single Postgres + app
Phase 2: Redis cache + read replicas
Phase 3: Kafka analytics
Phase 4: DB partitioning/sharding
Phase 5: Multi-region read replicas/CDN edge redirects
Phase 6: Active-active with global ID strategy
```

Interview phrase:

```text
I would not start with the most complex architecture. I would start with Postgres as the source of truth, Redis for redirect cache, and Kafka for analytics once click volume becomes large. If writes or storage outgrow one database, I shard by shortCode because that is the dominant lookup key.
```

---

## 12. URL Shortener Full Interview Answer

This is the complete answer shape.

### Opening

```text
I will design a URL shortener like bit.ly. The core operations are creating a short URL and redirecting a short code to the original URL. I will assume redirect traffic is much higher than creation traffic, redirects need low latency and high availability, and analytics can be eventually consistent.
```

### Requirements

```text
Functional:
1. Create short URL.
2. Redirect short URL.
3. Support custom aliases.
4. Support expiry and blocking.
5. Capture click analytics.

Non-functional:
1. Low latency redirects.
2. High availability.
3. Unique short codes.
4. Read-heavy scaling.
5. Safe error handling and abuse prevention.
```

### Architecture

```text
Clients hit a load balancer, which routes requests to stateless Spring Boot application instances. For create requests, the service validates the URL, generates or accepts a short code, and stores it in Postgres with a unique index on short_code. For redirects, the service first checks Redis for shortCode to longUrl mapping. On cache hit, it returns a 302 immediately. On cache miss, it reads from Postgres, validates status and expiry, populates Redis, and returns 302. Click analytics are sent asynchronously to Kafka and processed by analytics workers so redirect latency is not affected.
```

### Diagram

```text
                  CREATE FLOW

Client
  |
  v
+----------------+
| Load Balancer  |
+----------------+
  |
  v
+------------------------+
| Spring Boot URL Service|
+------------------------+
  | validate + generate code
  v
+------------------------+
| Postgres               |
| UNIQUE(short_code)     |
+------------------------+
  |
  v
201 Created
```

```text
                 REDIRECT FLOW

Client
  |
  v
+----------------+
| Load Balancer  |
+----------------+
  |
  v
+------------------------+
| Spring Boot URL Service|
+------------------------+
  |
  v
+----------------+
| Redis Cache    |
+----------------+
  | hit
  v
302 Redirect

Cache miss:
Redis miss -> Postgres -> Redis set -> 302 Redirect
```

```text
                 ANALYTICS FLOW

Redirect Service
      |
      v
+-------------+
| Kafka Topic |
+-------------+
      |
      v
+------------------+
| Analytics Worker |
+------------------+
      |
      v
+------------------+
| Aggregated Store |
+------------------+
```

### Closing

```text
This design keeps the redirect path fast, protects uniqueness at the database layer, separates analytics from the critical path, and can evolve with read replicas, sharding, and multi-region deployment as scale grows.
```

---

## 13. Request Flow: Create Short URL

Detailed create flow:

```text
POST /api/v1/urls
      |
      v
Validate request DTO
      |
      v
Validate URL scheme and host
      |
      v
Check custom alias format/reserved words
      |
      v
Generate short code or use custom alias
      |
      v
Insert into Postgres with UNIQUE(short_code)
      |
      +-- duplicate custom alias -> 409 Conflict
      |
      +-- generated collision -> retry
      |
      v
Return 201 Created
```

ASCII:

```text
+--------+      +-------------+      +-------------+      +----------+
| Client | ---> | Controller  | ---> | Service     | ---> | Postgres |
+--------+      +-------------+      +-------------+      +----------+
                     |                    |                  |
                     | @Valid             | business rules   | unique index
                     v                    v                  v
                  400 error          400/409 error       final truth
```

Important correctness point:

```text
Even if the application checks whether a custom alias exists, the database unique constraint is still required because two requests can race.
```

Race example:

```text
Request A checks alias = free
Request B checks alias = free
Request A inserts alias
Request B inserts alias
```

Without DB unique:

```text
Two rows can map to same shortCode.
```

With DB unique:

```text
One insert succeeds; the other fails safely with 409.
```

---

## 14. Request Flow: Redirect Short URL

Detailed redirect flow:

```text
GET /abc123
      |
      v
Validate short code format
      |
      v
Check Redis
      |
      +-- hit -> return 302 Location
      |
      v
Read Postgres
      |
      +-- not found -> 404
      +-- blocked   -> 403
      +-- expired   -> 410
      |
      v
Set Redis cache
      |
      v
Send click event to Kafka asynchronously
      |
      v
Return 302 Location
```

ASCII:

```text
                REDIRECT DECISION TREE

GET /code
   |
   v
valid format?
   |
   +-- no --> 400 INVALID_SHORT_CODE
   |
   v
Redis hit?
   |
   +-- yes --> 302 Location
   |
   v
Postgres row exists?
   |
   +-- no --> 404 SHORT_CODE_NOT_FOUND
   |
   v
status active?
   |
   +-- blocked --> 403 SHORT_CODE_BLOCKED
   +-- deleted  --> 404 SHORT_CODE_NOT_FOUND
   |
   v
expired?
   |
   +-- yes --> 410 SHORT_CODE_EXPIRED
   |
   v
Redis set + Kafka click event + 302
```

Latency target:

```text
Cache hit redirect should be very fast because it avoids database read.
```

Critical path:

```text
Redis lookup + app logic + HTTP 302
```

Non-critical path:

```text
Click analytics event processing
```

---

## 15. Cache Strategy Interview Answer

Use Redis cache-aside.

```text
Cache key:
url:{shortCode}

Cache value:
longUrl, status, expiresAt

TTL:
min(configured TTL, time until expiry)
```

Cache-aside flow:

```text
Read request
   |
   v
Check Redis
   |
   +-- hit -> return
   |
   v
Read DB
   |
   v
Write Redis
   |
   v
Return
```

ASCII:

```text
+---------+        +-------+        +----------+
| Service | -----> | Redis | -----> | Postgres |
+---------+        +-------+        +----------+
     | hit             | miss             |
     v                 v                 v
  return          query DB          source of truth
```

Cache invalidation:

```text
If URL is blocked/deleted:
1. Update Postgres.
2. Delete Redis key.
3. Future redirects load updated state from DB.
```

Hot key problem:

```text
A celebrity shares one short URL.
Millions of users request the same shortCode.
One Redis key becomes extremely hot.
```

Mitigations:

```text
1. Redis cluster with good capacity.
2. Local in-memory cache for ultra-hot keys.
3. CDN/edge redirect cache if acceptable.
4. Replicated Redis reads.
5. Rate limiting and abuse controls.
```

Interview phrase:

```text
Redis reduces DB load for read-heavy redirects. The tradeoff is cache invalidation complexity, so for status changes like block or delete, I update the DB first and invalidate the Redis key.
```

---

## 16. Database Scaling Interview Answer

Start with Postgres.

Why Postgres first?

```text
1. Strong constraints for uniqueness.
2. Good indexing for shortCode lookup.
3. Transactions for create flow.
4. Simple operational model.
```

Indexes:

```text
UNIQUE(short_code) for redirect lookup and uniqueness.
INDEX(user_id, created_at) for user dashboard.
INDEX(expires_at) for cleanup jobs.
```

When one DB is not enough:

```text
1. Add read replicas for dashboard/admin reads.
2. Keep redirect reads mostly on Redis.
3. Partition old data by created_at or expires_at.
4. Shard by shortCode hash when write/storage exceeds one primary.
```

Sharding by shortCode:

```text
shardId = hash(shortCode) % N
```

ASCII:

```text
                 SHARD ROUTING

shortCode = abc123
      |
      v
hash(abc123) % 4 = 2
      |
      v
+---------+---------+---------+---------+
| Shard 0 | Shard 1 | Shard 2 | Shard 3 |
+---------+---------+---------+---------+
                      ^
                      |
                  lookup here
```

Tradeoff:

```text
Sharding improves write/storage scalability but makes global queries and rebalancing harder.
```

Senior phrase:

```text
I would not shard from day one unless scale requires it. I would start with a single primary plus Redis and replicas. When write volume or storage becomes too large, I shard by shortCode because the dominant access pattern is lookup by shortCode.
```

---

## 17. Kafka Analytics Interview Answer

Analytics should be asynchronous.

Bad design:

```text
Redirect request writes click row synchronously to DB.
```

Problem:

```text
Redirect p99 latency increases.
Database gets overloaded by click writes.
A DB issue can break redirects.
```

Better design:

```text
Redirect service emits click event to Kafka.
Analytics workers process events separately.
```

ASCII:

```text
Redirect Request
      |
      v
+------------------+
| URL Service      |
+------------------+
      |
      +-- return 302 to user
      |
      v
+------------------+
| Kafka click topic|
+------------------+
      |
      v
+------------------+
| Analytics Worker |
+------------------+
      |
      v
+------------------+
| Aggregated Store |
+------------------+
```

Event example:

```json
{
  "eventId": "uuid",
  "shortCode": "abc123",
  "timestamp": "2026-06-25T10:00:00Z",
  "ipHash": "...",
  "userAgentHash": "...",
  "country": "RO",
  "referrer": "google"
}
```

Reliability concerns:

```text
1. Kafka producer failure.
2. Duplicate events.
3. Consumer lag.
4. Poison messages.
5. Analytics store outage.
```

Solutions:

```text
1. Producer timeout and retry.
2. Idempotent event IDs if exactness matters.
3. Consumer lag monitoring.
4. Dead-letter topic for bad events.
5. Backpressure and batch writes.
```

Interview phrase:

```text
Analytics can be eventually consistent, so I keep it off the redirect critical path. If Kafka is temporarily unavailable, I would either degrade analytics while preserving redirects or use a bounded local buffer depending on business requirements.
```

---

## 18. Consistency And Correctness Answer

Strong consistency is required for:

```text
1. shortCode uniqueness
2. custom alias conflict detection
3. block/delete state in source of truth
```

Eventual consistency is acceptable for:

```text
1. click analytics
2. dashboard counters
3. country/referrer aggregation
4. trending reports
```

ASCII:

```text
+--------------------------+--------------------------+
| Strong Consistency       | Eventual Consistency     |
+--------------------------+--------------------------+
| shortCode uniqueness     | click count              |
| custom alias creation    | analytics dashboard      |
| blocked/deleted status   | referrer statistics      |
+--------------------------+--------------------------+
```

Custom alias correctness:

```text
Application check improves user experience.
Database unique constraint guarantees correctness.
```

Cache consistency:

```text
Postgres is source of truth.
Redis is derived state.
On mutation, update DB first, then invalidate cache.
```

Potential race:

```text
Admin blocks URL.
Redis still contains old active mapping.
User redirects before invalidation completes.
```

Mitigation:

```text
1. Delete cache key immediately after DB update.
2. Use short TTLs for sensitive entries.
3. Store status in cache and refresh on admin changes.
4. For high-risk malicious links, optionally check blocklist before redirect.
```

Senior phrase:

```text
I separate correctness-critical paths from analytics paths. URL creation and blocking need stronger consistency, while click analytics can be eventually consistent to protect redirect latency.
```

---

## 19. Availability And Failure Handling Answer

Failure scenarios:

```text
1. Redis down.
2. Postgres primary down.
3. Kafka down.
4. App instance down.
5. Load balancer issue.
6. Analytics worker lag.
```

### Redis down

Options:

```text
Fallback to Postgres.
This protects correctness but increases DB load and latency.
```

Risk:

```text
If all traffic falls back to DB, Postgres may collapse.
```

Mitigation:

```text
1. Circuit breaker around Redis.
2. Rate limiting fallback traffic.
3. Read replicas for fallback reads.
4. Local cache for hot keys.
```

### Postgres down

Create API:

```text
Cannot create new short URLs safely.
Return controlled 503 or 500 depending on policy.
```

Redirect API:

```text
Cache hits can still work.
Cache misses fail or return service unavailable.
```

### Kafka down

Redirect should still work if analytics is non-critical.

```text
Option 1: Drop analytics event after bounded retry.
Option 2: Buffer locally for short time.
Option 3: Fail redirect only if analytics is strict business requirement.
```

Usually for URL shortener:

```text
Do not fail redirect because analytics event failed.
```

ASCII failure mindset:

```text
Critical path:
Client -> App -> Redis/Postgres -> 302

Non-critical path:
App -> Kafka -> Analytics

Failure in non-critical path should not kill critical path.
```

---

## 20. Observability Answer

Production system must be measurable.

Metrics:

```text
1. redirect_requests_total
2. create_requests_total
3. redirect_latency_p50/p95/p99
4. cache_hit_ratio
5. cache_miss_count
6. db_query_latency
7. db_connection_pool_usage
8. kafka_publish_failure_count
9. kafka_consumer_lag
10. error_rate_by_code
```

Logs:

```text
correlationId
shortCode
path
status
latencyMs
cacheHit
errorCode
exceptionClass
```

Traces:

```text
HTTP request
  -> Redis span
  -> Postgres span if cache miss
  -> Kafka publish span
```

ASCII:

```text
Request
  |
  v
+----------+     logs      +-------------+
| Service  | ------------> | Log Store   |
+----------+               +-------------+
  | metrics
  v
+----------+
| Prometheus
+----------+
  | traces
  v
+----------+
| Tracing  |
+----------+
```

Golden signals:

```text
Latency
Traffic
Errors
Saturation
```

Interview phrase:

```text
I would monitor p99 redirect latency, Redis hit ratio, DB latency, Kafka lag, and error rates by code. If p99 increases and cache hit ratio drops, I would immediately suspect Redis misses or cache eviction pressure.
```

---

## 21. Security And Abuse Answer

URL shorteners are abuse targets.

Basic validation:

```text
1. Accept only http/https.
2. Reject javascript:, file:, ftp:.
3. Validate host exists.
4. Limit long URL length.
5. Restrict custom alias pattern.
6. Reserve aliases like admin, api, health.
```

Abuse controls:

```text
1. Rate limit create API.
2. User/account-level quotas.
3. Admin blocklist.
4. Malware/phishing scanning.
5. Spam detection.
6. Audit logs for admin actions.
```

SSRF warning:

```text
Redirecting to a URL is different from server-side fetching.
If the service later fetches URLs for preview/title extraction, SSRF risk becomes serious.
```

Dangerous targets:

```text
http://localhost:8080/admin
http://127.0.0.1:5432
http://169.254.169.254/latest/meta-data
```

Interview phrase:

```text
In v1, I validate scheme and host and restrict aliases. For production, I add rate limiting, admin blocklists, phishing detection, and SSRF protection if the backend ever fetches target URLs.
```

---

## 22. Step-by-Step Dry Runs

### Dry Run 1: Normal create

Input:

```json
{
  "longUrl": "https://example.com/a/b",
  "customAlias": null
}
```

Flow:

```text
1. Client calls POST /api/v1/urls.
2. Controller validates DTO.
3. Service validates URL.
4. ID generator creates code xY92ab.
5. Service inserts row into Postgres.
6. Unique index accepts the code.
7. API returns short URL.
```

Result:

```text
201 Created
shortUrl = https://sho.rt/xY92ab
```

---

### Dry Run 2: Redirect cache hit

Input:

```http
GET /xY92ab
```

Flow:

```text
1. Request reaches URL service.
2. Service checks Redis key url:xY92ab.
3. Redis returns longUrl.
4. Service emits click event asynchronously.
5. Service returns 302 Location.
```

Result:

```text
Fast redirect, no DB read.
```

---

### Dry Run 3: Redirect cache miss

Flow:

```text
1. Redis does not contain url:xY92ab.
2. Service queries Postgres by short_code.
3. Row exists and is active.
4. Service writes mapping to Redis.
5. Service returns 302.
```

Result:

```text
Slightly slower redirect, but future requests become cache hits.
```

---

### Dry Run 4: Duplicate custom alias

Input:

```json
{
  "longUrl": "https://another.com",
  "customAlias": "sale"
}
```

Existing row:

```text
short_code = sale
```

Flow:

```text
1. Request validates successfully.
2. Service tries to insert custom alias.
3. Postgres UNIQUE(short_code) fails.
4. Service maps conflict to ALIAS_ALREADY_EXISTS.
5. API returns 409 Conflict.
```

Interview point:

```text
The DB constraint is the final guard against race conditions.
```

---

### Dry Run 5: Kafka unavailable

Flow:

```text
1. User requests redirect.
2. Redis returns long URL.
3. Service tries to publish click event.
4. Kafka publish fails or times out.
5. Service logs failure and returns redirect anyway.
```

Reason:

```text
Analytics is not critical to redirect correctness.
```

Tradeoff:

```text
Some click events may be lost unless buffered/retried.
```

---

## 23. Common Interview Traps

### Trap 1: Starting with microservices

Weak:

```text
I will create URL service, user service, analytics service, auth service, admin service, notification service...
```

Better:

```text
I will start with one stateless URL service because the core domain is simple. I can split analytics worker separately because it has different scaling and latency needs.
```

### Trap 2: Ignoring requirements

Weak:

```text
I will use Cassandra.
```

Better:

```text
First I need to know read/write ratio, latency target, retention, and whether analytics must be real-time.
```

### Trap 3: No numbers

Weak:

```text
It should scale a lot.
```

Better:

```text
Assuming 100k redirects/sec and 1k creates/sec, redirects are the bottleneck, so cache and async analytics are essential.
```

### Trap 4: Putting analytics in critical path

Weak:

```text
On every redirect, write click row to database before returning.
```

Better:

```text
Emit click event asynchronously and return redirect quickly.
```

### Trap 5: No uniqueness guarantee

Weak:

```text
The app will check if shortCode exists before insert.
```

Better:

```text
The app can check for UX, but the database unique constraint guarantees correctness under race.
```

### Trap 6: No failure discussion

Weak:

```text
Redis makes it fast.
```

Better:

```text
If Redis fails, I can fallback to DB with circuit breakers and rate limits, but this increases DB pressure. Cache hits can survive DB issues temporarily.
```

---

## 24. Senior-Level Tradeoff Language

Use these phrases.

### Simplicity tradeoff

```text
I would not introduce sharding or multi-region active-active at the start because it increases operational complexity. I would add it when metrics show single-primary write/storage limits.
```

### Latency tradeoff

```text
Redis improves redirect latency and reduces DB load, but introduces cache invalidation complexity.
```

### Consistency tradeoff

```text
Short code uniqueness needs strong consistency, while analytics can be eventually consistent.
```

### Availability tradeoff

```text
During Postgres failure, cache-hit redirects may continue, but cache misses and creates cannot be guaranteed.
```

### Analytics tradeoff

```text
Kafka decouples analytics from redirects, but the dashboard becomes eventually consistent and we must monitor consumer lag.
```

### Sharding tradeoff

```text
Sharding by shortCode matches lookup access patterns, but makes global admin queries and rebalancing more complex.
```

One senior pattern:

```text
I choose X because of Y. The tradeoff is Z. I would monitor A to know when to change it.
```

Example:

```text
I choose Redis cache-aside because redirects are read-heavy. The tradeoff is stale cache after block/delete. I would monitor cache hit ratio, p99 latency, and stale redirect incidents.
```

---

## 25. 30-Minute Interview Timeline

For a 30-minute interview:

```text
0-5 min:
    clarify requirements and assumptions

5-8 min:
    quick scale estimation

8-12 min:
    APIs and data model

12-18 min:
    high-level architecture

18-25 min:
    deep dive redirect path, cache, uniqueness, analytics

25-30 min:
    failures, tradeoffs, observability, security
```

Do not spend 20 minutes on requirements.

Do not spend 20 minutes drawing a perfect diagram.

Control the interview.

```text
I will first cover the main architecture, then deep dive into redirect latency and uniqueness because those are the most important parts.
```

---

## 26. 45-Minute Interview Timeline

For a 45-minute interview:

```text
0-7 min:
    requirements and scope

7-12 min:
    scale estimation and bottleneck identification

12-17 min:
    APIs and data model

17-25 min:
    high-level design

25-35 min:
    deep dive 1: redirect cache and DB fallback

35-40 min:
    deep dive 2: analytics pipeline and Kafka

40-45 min:
    failure modes, tradeoffs, monitoring, security
```

What to prioritize:

```text
For URL shortener:
1. Redirect latency
2. Short code uniqueness
3. Analytics decoupling
4. Cache invalidation
5. DB scaling
```

---

## 27. FAANG-Style Follow-Up Questions

### Q1. How do you guarantee short code uniqueness?

Answer:

```text
I can generate short codes using an ID generation strategy such as sequence ID encoded in Base62, Snowflake-like IDs, or random codes with collision retry. Regardless of strategy, I enforce UNIQUE(short_code) in the database. For custom aliases, I rely on the same unique constraint and return 409 on conflict.
```

### Q2. What if a short URL becomes viral?

Answer:

```text
The Redis key becomes hot. I can add local in-memory cache for extremely hot codes, use Redis read replicas, or push redirects closer to edge/CDN if acceptable. I would monitor per-key traffic and Redis CPU/network saturation.
```

### Q3. What if Redis is down?

Answer:

```text
Fallback to Postgres for correctness, but protect DB using circuit breakers and rate limits. Cache hits are unavailable, so p99 latency increases. For very high traffic, local cache can reduce blast radius.
```

### Q4. What if Kafka is down?

Answer:

```text
Redirects should continue because analytics is non-critical. I would retry briefly, log the failure, and possibly use bounded buffering. The tradeoff is possible analytics loss or delayed analytics.
```

### Q5. How do you handle expired links?

Answer:

```text
Store expires_at in Postgres and optionally in Redis value. On redirect, check expiry before returning 302. Cache TTL should not exceed the remaining expiry time. Expired links return 410 Gone.
```

### Q6. How do you block malicious links?

Answer:

```text
Store status as ACTIVE, BLOCKED, or DELETED. Admin updates the DB status and invalidates Redis key. Redirect checks status and returns 403 for blocked links. For high-risk cases, add blocklist checks and phishing scanning.
```

### Q7. Why not use Cassandra from the beginning?

Answer:

```text
Postgres gives strong uniqueness constraints, simple transactions, and easy development. Since Redis handles most reads, Postgres can scale far initially. I would move to sharding or a distributed store when write/storage requirements exceed Postgres capacity.
```

### Q8. How do you make it multi-region?

Answer:

```text
Start with regional read replicas and edge caching for redirects. For active-active writes, uniqueness becomes harder, so I need region-aware ID generation or globally coordinated ID ranges. Analytics can be region-local and aggregated asynchronously.
```

### Q9. How do you debug high p99 redirect latency?

Answer:

```text
Check cache hit ratio, Redis latency, DB query latency, DB connection pool saturation, app thread pool saturation, GC pauses, and network latency. Tracing should show whether slow requests are Redis hits, Redis misses, or DB fallbacks.
```

### Q10. What is the biggest tradeoff in your design?

Answer:

```text
The main tradeoff is keeping redirects fast by using cache and async analytics, while accepting cache invalidation complexity and eventually consistent analytics. Correctness-critical data stays in Postgres with unique constraints.
```

---

## 28. Final 10/10 Spoken Answer

Use this as your polished interview answer.

```text
I will design a URL shortener with two core paths: creating a short URL and redirecting a short code to the original URL. I assume redirects are much higher volume than creates, redirect latency is critical, and analytics can be eventually consistent.

For APIs, I would expose POST /api/v1/urls to create a short URL with longUrl, optional customAlias, and optional expiresAt. Redirect uses GET /{shortCode} and returns 302 with Location header. Analytics can be queried separately.

For storage, I would keep short_urls in Postgres with short_code as a unique indexed column, long_url, status, created_at, and expires_at. The unique index is important because application-level checks are not enough under concurrent custom alias requests. Click events are high volume, so I would not write them synchronously into the main database.

The high-level architecture is a load balancer in front of stateless Spring Boot URL service instances. Create requests validate input, generate or accept a short code, and insert into Postgres. Redirect requests first check Redis for shortCode to longUrl mapping. On cache hit, return 302 immediately. On cache miss, read from Postgres, check status and expiry, populate Redis, and return 302. Click events are published asynchronously to Kafka and processed by analytics workers.

This keeps the hot redirect path fast and avoids overloading Postgres. Redis is a derived cache; Postgres remains the source of truth. On block or delete, I update Postgres and invalidate the Redis key. For expired URLs, cache TTL should not exceed the expiry time.

For scale, if redirects reach very high QPS, Redis absorbs most reads. If one database becomes insufficient for writes or storage, I shard by shortCode hash because shortCode is the dominant lookup key. Analytics can be stored in an OLAP store or aggregated counters because it is append-heavy and eventually consistent.

For failure handling, if Redis is down, I can fallback to Postgres with circuit breakers and rate limits, but latency increases. If Kafka is down, redirects should still succeed because analytics is not on the critical path. If Postgres is down, creates fail and cache-miss redirects may fail, but cache-hit redirects can still work temporarily.

For observability, I would monitor p99 redirect latency, cache hit ratio, Redis latency, DB query latency, DB connection pool usage, Kafka producer failures, consumer lag, and error rates by code. For security, I validate URL scheme, restrict aliases, block reserved words, add rate limits, and later add phishing/blocklist checks.

The key tradeoff is using Redis and Kafka to keep redirects fast while accepting cache invalidation complexity and eventual consistency for analytics. Correctness-critical operations like short code uniqueness stay strongly protected by Postgres constraints.
```

Why this is 10/10:

```text
1. Starts with assumptions.
2. Defines APIs.
3. Designs data model.
4. Uses cache for read-heavy path.
5. Uses DB constraint for correctness.
6. Uses Kafka for async analytics.
7. Discusses failures.
8. Mentions observability.
9. Explains tradeoffs.
10. Does not over-engineer from the first sentence.
```

---

## 29. Senior Engineer Checklist

Before answering, mentally check:

```text
[ ] Did I clarify functional requirements?
[ ] Did I clarify non-functional requirements?
[ ] Did I state read/write ratio?
[ ] Did I identify the bottleneck?
[ ] Did I define APIs?
[ ] Did I define data model?
[ ] Did I mention unique index on shortCode?
[ ] Did I explain create flow?
[ ] Did I explain redirect flow?
[ ] Did I use Redis only where it helps?
[ ] Did I keep Postgres as source of truth?
[ ] Did I keep analytics async?
[ ] Did I discuss cache miss path?
[ ] Did I discuss cache invalidation?
[ ] Did I discuss Redis failure?
[ ] Did I discuss Kafka failure?
[ ] Did I discuss DB scaling?
[ ] Did I discuss sharding key?
[ ] Did I discuss observability?
[ ] Did I discuss security/abuse?
[ ] Did I clearly state tradeoffs?
```

If yes, your answer is senior-level.

---

## 30. One-Page Cheat Sheet

```text
SYSTEM DESIGN ANSWER FRAMEWORK

1. Requirements
   Functional + non-functional + out of scope

2. Scale
   QPS, read/write ratio, storage, latency target

3. APIs
   POST /urls
   GET /{shortCode}
   GET /analytics

4. Data Model
   short_urls(short_code UNIQUE, long_url, status, expires_at)
   click_events asynchronously

5. Architecture
   LB -> stateless app -> Redis -> Postgres
                     |
                     v
                   Kafka -> Analytics Worker

6. Deep Dives
   uniqueness
   redirect cache
   async analytics
   DB scaling
   failures
   observability
   security

URL SHORTENER CORE CHOICES

Postgres:
   source of truth + unique constraint

Redis:
   fast redirect cache

Kafka:
   async click analytics

Sharding key:
   shortCode hash

Consistency:
   strong for creation/blocking
   eventual for analytics

Failure policy:
   Redis down -> DB fallback with protection
   Kafka down -> redirect still works
   DB down -> creates fail, cache hits may continue

Golden tradeoff:
   fast redirects vs cache invalidation complexity
```

---

## 31. One Picture To Remember

```text
                 SYSTEM DESIGN INTERVIEW ANSWER MAP

                         Question
                            |
                            v
                +------------------------+
                | Clarify Requirements  |
                +------------------------+
                            |
                            v
                +------------------------+
                | Estimate Scale        |
                +------------------------+
                            |
                            v
                +------------------------+
                | API + Data Model      |
                +------------------------+
                            |
                            v
                +------------------------+
                | High-Level Design     |
                +------------------------+
                            |
                            v
          +-----------------------------------------+
          | Deep Dive What Actually Breaks          |
          | cache, DB, uniqueness, Kafka, failures  |
          +-----------------------------------------+
                            |
                            v
          +-----------------------------------------+
          | Tradeoffs + Observability + Security    |
          +-----------------------------------------+


                 URL SHORTENER PRODUCTION PICTURE

+--------+     +----+     +-------------------+     +-------+
| Client | --> | LB | --> | Spring Boot Apps  | --> | Redis |
+--------+     +----+     +-------------------+     +-------+
                                |                       |
                                | cache miss            |
                                v                       |
                          +------------+ <-------------+
                          | Postgres   |
                          | source of  |
                          | truth      |
                          +------------+
                                |
                                | click events async
                                v
                          +------------+
                          | Kafka      |
                          +------------+
                                |
                                v
                          +------------+
                          | Analytics |
                          +------------+

FINAL MEMORY:

Requirements decide the system.
Scale reveals the bottleneck.
APIs reveal the data model.
Architecture solves the current bottleneck.
Tradeoffs prove seniority.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. A system design answer is a structured conversation, not a technology dump.
2. Start with requirements and scale before drawing architecture.
3. For URL shortener, redirect is read-heavy and latency-sensitive, so Redis cache is central.
4. Short code uniqueness is correctness-critical, so Postgres UNIQUE constraint is mandatory.
5. Analytics should be asynchronous because it must not slow down redirects.
```

After this chapter, you should be able to answer URL shortener system design in a clear FAANG-style format.

Next possible chapters:

```text
064_URL_Shortener_End_To_End_Final_Review.md
065_Uber_Nearby_Drivers_System_Design.md
066_News_Feed_System_Design.md
```
