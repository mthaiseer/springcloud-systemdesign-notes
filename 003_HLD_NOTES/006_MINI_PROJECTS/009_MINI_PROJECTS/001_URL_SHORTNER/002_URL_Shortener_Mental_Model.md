# 002_URL_Shortener_Mental_Model.md
# MiniURLShortener — URL Shortener Mental Model

> Core mental model: **A URL shortener is not mainly a string-shortening app. It is a read-heavy key-value lookup system where a small unique token resolves to a long destination URL as fast and safely as possible.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. What A URL Shortener Really Does](#3-what-a-url-shortener-really-does)
- [4. Real World Analogy](#4-real-world-analogy)
- [5. The Two Core Flows](#5-the-two-core-flows)
- [6. Create Flow Mental Model](#6-create-flow-mental-model)
- [7. Redirect Flow Mental Model](#7-redirect-flow-mental-model)
- [8. Why Redirect Is The Hot Path](#8-why-redirect-is-the-hot-path)
- [9. The Key-Value Table Mental Model](#9-the-key-value-table-mental-model)
- [10. The Token Mental Model](#10-the-token-mental-model)
- [11. Uniqueness Mental Model](#11-uniqueness-mental-model)
- [12. Collision Mental Model](#12-collision-mental-model)
- [13. Base62 Mental Picture](#13-base62-mental-picture)
- [14. ID Generation Strategy Overview](#14-id-generation-strategy-overview)
- [15. Source Of Truth vs Speed Layer](#15-source-of-truth-vs-speed-layer)
- [16. Redirect Status Mental Model](#16-redirect-status-mental-model)
- [17. Expiry And Status Mental Model](#17-expiry-and-status-mental-model)
- [18. Analytics Mental Model](#18-analytics-mental-model)
- [19. Abuse And Security Mental Model](#19-abuse-and-security-mental-model)
- [20. Scaling Evolution](#20-scaling-evolution)
- [21. Step-by-Step Dry Runs](#21-step-by-step-dry-runs)
- [22. Production Failure Stories](#22-production-failure-stories)
- [23. Debugging Mindset](#23-debugging-mindset)
- [24. Common Misconceptions](#24-common-misconceptions)
- [25. Interview-Ready Explanation](#25-interview-ready-explanation)
- [26. Senior Engineer Checklist](#26-senior-engineer-checklist)
- [27. One-Page Cheat Sheet](#27-one-page-cheat-sheet)
- [28. One Picture To Remember](#28-one-picture-to-remember)

---

## 1. Why This Exists

A URL shortener exists because long URLs are difficult to share, ugly in user interfaces, and sometimes too large for messaging platforms, printed material, QR codes, ads, SMS, or social posts.

Example long URL:

```text
https://www.example.com/products/mobile/iphone-15-pro-max?
campaign=summer-sale&source=email&trackingId=abc123xyz999
```

Short URL:

```text
https://mini.ly/aB91xZ
```

At beginner level, this looks like:

```text
Long string -> short string
```

But that is not the correct engineering model.

The system is not compressing the URL. It is not mathematically reducing the long URL into a reversible shorter form. Instead, it creates a **new identifier** and stores a mapping.

Correct mental model:

```text
shortCode -> longUrl
```

Example:

```text
aB91xZ -> https://www.example.com/products/mobile/iphone-15-pro-max?campaign=summer-sale
```

When someone visits the short URL, the system looks up `aB91xZ`, finds the long URL, and sends an HTTP redirect.

So the real problem is:

```text
Can we create unique keys safely?
Can we resolve keys extremely fast?
Can we keep mappings durable?
Can we survive high redirect traffic?
Can we avoid abuse?
```

That is why this project is powerful for interviews. It looks simple, but it touches backend fundamentals: database design, caching, ID generation, concurrency, latency, HTTP behavior, security, analytics, observability, and scaling.

---

## 2. The One Core Mental Model

A URL shortener is a:

```text
READ-HEAVY KEY-VALUE REDIRECT SYSTEM
```

Break it down:

```text
READ-HEAVY
    Most traffic is redirect traffic.

KEY-VALUE
    shortCode is the key.
    longUrl is the value.

REDIRECT SYSTEM
    The output is not usually JSON.
    The output is HTTP 301/302 redirect.
```

The core table is conceptually:

```text
+-----------+----------------------------------------------+
| shortCode | longUrl                                      |
+-----------+----------------------------------------------+
| aB91xZ    | https://example.com/products/123             |
| k9Lm2Q    | https://github.com/mthaiseer/project         |
| java17    | https://docs.oracle.com/javase/17/docs       |
+-----------+----------------------------------------------+
```

The main runtime operation is:

```text
GET /aB91xZ
    |
    v
Find longUrl for aB91xZ
    |
    v
Return 302 Location: https://example.com/products/123
```

One line summary:

```text
A URL shortener safely creates a unique token once, then resolves that token many times.
```

That “once vs many times” is the key to understanding the whole system.

---

## 3. What A URL Shortener Really Does

A URL shortener performs two business operations.

### Operation 1: Create

Input:

```text
https://leetcode.com/problems/two-sum
```

Output:

```text
https://mini.ly/xY92Ab
```

Internal state:

```text
xY92Ab -> https://leetcode.com/problems/two-sum
```

### Operation 2: Redirect

Input:

```text
https://mini.ly/xY92Ab
```

Output:

```http
302 Found
Location: https://leetcode.com/problems/two-sum
```

The browser then automatically opens the original URL.

Important:

```text
The short URL does not contain the long URL.
It only contains a lookup key.
```

This is like storing a file in a drawer and giving someone a drawer number. The drawer number does not contain the file. It only helps retrieve the file.

---

## 4. Real World Analogy

Think of a hotel cloakroom.

```text
You give your coat to the counter.
The staff stores it somewhere.
They give you token A17.
Later, you show token A17.
They return your coat.
```

Mapping:

```text
Cloakroom token -> coat
URL short code  -> long URL
```

ASCII analogy:

```text
+------------------+       gives coat       +------------------+
| Customer         | ---------------------> | Cloakroom        |
+------------------+                        +------------------+
        |                                           |
        | receives token A17                        |
        v                                           v
+------------------+                        +------------------+
| Token A17        |                        | A17 -> Coat      |
+------------------+                        +------------------+

Later:

+------------------+       shows A17       +------------------+
| Customer         | --------------------> | Cloakroom        |
+------------------+                       +------------------+
        |                                          |
        | receives coat                           |
        v                                          v
+------------------+                       +------------------+
| Coat             |                       | lookup complete  |
+------------------+                       +------------------+
```

URL shortener:

```text
User gives long URL.
System stores long URL.
System gives shortCode.
Visitor gives shortCode.
System returns long URL through redirect.
```

Production lesson:

```text
The token must be unique.
The lookup must be fast.
The storage must not lose mappings.
The system must handle many people asking for the same token.
```

---

## 5. The Two Core Flows

A URL shortener has two main flows:

```text
1. Create flow  — write path
2. Redirect flow — read path
```

High-level view:

```text
                         MINI URL SHORTENER

        WRITE PATH                                  READ PATH
   create short URL once                     redirect many times later

+---------------------+                     +----------------------+
| POST /api/v1/urls   |                     | GET /{shortCode}     |
+---------------------+                     +----------------------+
          |                                             |
          v                                             v
+---------------------+                     +----------------------+
| Validate long URL   |                     | Extract shortCode    |
+---------------------+                     +----------------------+
          |                                             |
          v                                             v
+---------------------+                     +----------------------+
| Generate shortCode  |                     | Lookup longUrl       |
+---------------------+                     +----------------------+
          |                                             |
          v                                             v
+---------------------+                     +----------------------+
| Save mapping        |                     | Return redirect      |
+---------------------+                     +----------------------+
```

Why separate them mentally?

Because they have different priorities.

Create path priorities:

```text
Correctness
Uniqueness
Validation
Durability
Abuse prevention
```

Redirect path priorities:

```text
Speed
Availability
Cacheability
Low latency
High throughput
```

If you mix both paths mentally, you design a slow CRUD app. If you separate them, you design a real production system.

---

## 6. Create Flow Mental Model

Create flow is the write path.

Input:

```json
{
  "longUrl": "https://example.com/article/123"
}
```

Output:

```json
{
  "shortCode": "aB91xZ",
  "shortUrl": "https://mini.ly/aB91xZ"
}
```

Internal flow:

```text
Client
  |
  | POST /api/v1/urls
  v
Controller
  |
  | validate request shape
  v
Service
  |
  | validate URL
  | generate shortCode
  | prepare entity
  v
Repository
  |
  | INSERT into short_urls
  v
Postgres
  |
  | enforce UNIQUE(short_code)
  v
Response
```

ASCII flow:

```text
+--------------------+
| Receive long URL   |
+--------------------+
          |
          v
+--------------------+
| Validate URL       |
+--------------------+
          |
          v
+-----------------------------+
| Custom alias provided?      |
+-----------------------------+
       | yes               | no
       v                   v
+--------------+     +------------------+
| Validate     |     | Generate code    |
| alias        |     | Base62/random/id |
+--------------+     +------------------+
       |                   |
       v                   v
+--------------------------------+
| Insert mapping into Postgres   |
| UNIQUE(short_code) enforced    |
+--------------------------------+
          |
          v
+------------------------------+
| Return short URL to client   |
+------------------------------+
```

The most important create-flow principle:

```text
The database must be the final judge of uniqueness.
```

Application checks are useful, but not enough under concurrency.

Bad approach:

```text
1. Check if code exists.
2. If not, insert.
```

Race:

```text
Thread A checks: code free
Thread B checks: code free
Thread A inserts
Thread B inserts also tries
```

Correct approach:

```text
1. Generate code.
2. Insert with UNIQUE(short_code).
3. If duplicate key happens, retry generated code or return conflict for custom alias.
```

---

## 7. Redirect Flow Mental Model

Redirect flow is the read path.

Request:

```http
GET /aB91xZ
```

Response:

```http
302 Found
Location: https://example.com/article/123
```

Internal flow:

```text
Browser
  |
  | GET /aB91xZ
  v
Controller
  |
  | extract shortCode
  v
Service
  |
  | lookup mapping
  | check status
  | check expiry
  v
Return redirect response
  |
  v
Browser opens long URL
```

ASCII sequence:

```text
+---------+       +-------------+       +----------+       +----------+
| Browser |       | Controller  |       | Service  |       | Postgres |
+---------+       +-------------+       +----------+       +----------+
     |                   |                   |                   |
     | GET /aB91xZ       |                   |                   |
     |------------------>|                   |                   |
     |                   | find(aB91xZ)      |                   |
     |                   |------------------>|                   |
     |                   |                   | SELECT long_url   |
     |                   |                   |------------------>|
     |                   |                   | long_url          |
     |                   |                   |<------------------|
     |                   | 302 Location      |                   |
     |<------------------|                   |                   |
     | opens long URL    |                   |                   |
     |---------------------------------------------------------->|
```

In v1, the lookup can go directly to PostgreSQL.

Later:

```text
Browser -> App -> Redis -> Postgres only on cache miss
```

Production redirect path:

```text
+---------+       +-----------+       +---------+       +----------+
| Browser | ----> | App Pod   | ----> | Redis   | ----> | Postgres |
+---------+       +-----------+       +---------+       +----------+
                      |                  |
                      | cache hit         |
                      v                  |
              return 302 fast            |
```

The redirect endpoint is the most important endpoint in the system because every shared short link depends on it.

---

## 8. Why Redirect Is The Hot Path

A URL is usually created once but clicked many times.

Example:

```text
Create:
1 user creates https://mini.ly/sale2026

Redirect:
100,000 users click it from Twitter, WhatsApp, email, ads, QR code
```

Ratio:

```text
Writes: 1
Reads : 100,000
```

So the system is read-heavy.

ASCII ratio:

```text
Create requests:

POST /api/v1/urls
|

Redirect requests:

GET /sale2026
|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
```

This affects design decisions:

```text
1. Redirect needs caching.
2. Redirect needs low latency.
3. Redirect should avoid heavy DB joins.
4. Redirect should not wait for analytics.
5. Redirect should survive write-path issues if possible.
```

A senior engineer protects the hot path.

Bad redirect path:

```text
GET /code
  -> DB lookup
  -> DB insert analytics
  -> call fraud service
  -> call user service
  -> return redirect
```

Better redirect path:

```text
GET /code
  -> Redis lookup
  -> return redirect
  -> emit analytics asynchronously
```

Rule:

```text
Do only what is necessary before returning 302.
Everything else should be async, cached, or best-effort.
```

---

## 9. The Key-Value Table Mental Model

At the heart of the system is a table.

```text
short_urls
```

Conceptual table:

```text
+----+------------+-----------------------------+--------+------------+
| id | short_code | long_url                    | status | expires_at |
+----+------------+-----------------------------+--------+------------+
| 1  | aB91xZ     | https://example.com/a       | ACTIVE | null       |
| 2  | java17     | https://docs.oracle.com/17  | ACTIVE | null       |
| 3  | offer1     | https://shop.com/deal       | ACTIVE | 2026-12-31 |
+----+------------+-----------------------------+--------+------------+
```

The most important query:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?;
```

This means:

```text
short_code must be indexed.
```

Without index:

```text
Database scans many rows.
Latency grows as table grows.
```

With index:

```text
Database jumps quickly to matching row.
Lookup remains fast.
```

Mental model:

```text
short_code index = dictionary index
```

Like a book index:

```text
Without index:
Read every page.

With index:
Jump directly to page.
```

For URL shortener, `short_code` is the lookup handle. Everything depends on it.

---

## 10. The Token Mental Model

The short code is a token.

Examples:

```text
aB91xZ
xk29Lm
java17
mohamed
```

A token must have these properties:

```text
1. Unique
2. Short enough to share
3. URL-safe
4. Hard enough to guess if privacy matters
5. Efficient to generate
6. Efficient to index
```

Common characters:

```text
Base62 = a-z + A-Z + 0-9
```

That gives:

```text
26 lowercase + 26 uppercase + 10 digits = 62 symbols
```

Capacity intuition:

```text
1 char  = 62 possibilities
2 chars = 62^2
3 chars = 62^3
6 chars = 62^6
7 chars = 62^7
8 chars = 62^8
```

Approximate capacity:

```text
62^6 ≈ 56 billion
62^7 ≈ 3.5 trillion
62^8 ≈ 218 trillion
```

So a 7 or 8 character Base62 code is already very large for many systems.

But capacity is not the only question.

You must also ask:

```text
How do we generate the token?
Can collisions happen?
Can users guess tokens?
Can the DB enforce uniqueness?
Can we shard by token later?
```

---

## 11. Uniqueness Mental Model

Uniqueness means:

```text
One shortCode cannot point to two different long URLs.
```

Bad state:

```text
abc123 -> https://site-a.com
abc123 -> https://site-b.com
```

Impossible to redirect correctly.

Correct invariant:

```text
short_code is globally unique.
```

Database enforcement:

```sql
ALTER TABLE short_urls
ADD CONSTRAINT uk_short_code UNIQUE (short_code);
```

Why DB-level uniqueness is mandatory:

```text
Application instances may run in many pods.
Two pods may generate same code at same time.
Only the database can safely enforce final uniqueness.
```

Multi-pod race:

```text
+---------+           +----------+
| Pod A   |           | Pod B    |
+---------+           +----------+
    |                      |
    | generate k9Lm2Q      | generate k9Lm2Q
    |                      |
    v                      v
+--------------------------------+
| Postgres UNIQUE(short_code)    |
+--------------------------------+
    |                      |
    | insert success       | duplicate key error
```

Application rule:

```text
Generated code collision -> retry.
Custom alias collision -> return 409 Conflict.
```

This difference matters.

Generated code collision is internal and recoverable.

Custom alias conflict is user-visible because user asked for a specific alias.

---

## 12. Collision Mental Model

Collision means:

```text
Two attempts produce same shortCode.
```

Example:

```text
Request A generates xY91Ab
Request B also generates xY91Ab
```

Collisions can happen depending on the generation strategy.

Random code generation:

```text
generate random 7-char Base62 code
insert into DB
if duplicate, retry
```

ID-based generation:

```text
insert row with numeric id
convert id to Base62
update/store shortCode
```

Hash-based generation:

```text
hash long URL
take part of hash
collision possible, still need uniqueness check
```

Collision handling diagram:

```text
+----------------------+
| Generate shortCode   |
+----------------------+
          |
          v
+----------------------+
| Try DB insert        |
+----------------------+
          |
          v
+-------------------------------+
| UNIQUE constraint passes?     |
+-------------------------------+
       | yes                 | no
       v                     v
+-------------+       +----------------------+
| success     |       | generated code?      |
+-------------+       +----------------------+
                          | yes          | custom alias
                          v              v
                  +--------------+   +----------------+
                  | retry code   |   | return 409     |
                  +--------------+   +----------------+
```

Production rule:

```text
Never assume collision probability is zero.
Design collision handling clearly.
```

Even if probability is tiny, the failure mode must be safe.

---

## 13. Base62 Mental Picture

Base62 is commonly used because it produces compact URL-safe codes.

Characters:

```text
0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ
```

or another chosen ordering.

Base62 converts a number into a short string using 62 symbols.

Decimal example:

```text
Number: 125
Base62:
125 / 62 = 2 remainder 1
2 / 62   = 0 remainder 2

digits: 2,1
code: "21" depending on alphabet ordering
```

Mental model:

```text
Decimal uses 10 symbols.
Binary uses 2 symbols.
Hex uses 16 symbols.
Base62 uses 62 symbols.
```

ASCII:

```text
Number grows:

0, 1, 2, 3, ... 61, 62, 63, ...

Base62 grows:

0, 1, 2, 3, ... Z, 10, 11, ...
```

Why useful:

```text
Large numeric ID -> short URL-friendly string
```

Example:

```text
id = 1000000000
Base62 may become a compact code like 15ftgG
```

Important:

```text
Base62 encoding is not encryption.
It is just representation.
```

If IDs are sequential and Base62 encoded directly, users may guess nearby short codes.

For public systems, you may need random IDs, shuffled IDs, or non-sequential generation depending on privacy requirements.

---

## 14. ID Generation Strategy Overview

There are several ways to create short codes.

### Strategy 1: Random Base62

```text
Generate random 7 or 8 chars.
Insert with UNIQUE constraint.
Retry on collision.
```

Pros:

```text
Simple
Harder to guess
No central counter needed
```

Cons:

```text
Collision possible
Need retry logic
Randomness quality matters
```

### Strategy 2: DB auto-increment ID + Base62

```text
Insert row -> get id -> encode id to Base62
```

Pros:

```text
No random collision
Simple mental model
Compact codes
```

Cons:

```text
Sequential codes are guessable
May need extra update step
Can reveal system growth
Harder in distributed ID generation if not planned
```

### Strategy 3: Snowflake-like distributed ID + Base62

```text
timestamp + workerId + sequence -> numeric ID -> Base62
```

Pros:

```text
Scales across nodes
No central DB sequence bottleneck
Sortable by time
```

Cons:

```text
More complex
Clock issues
Worker coordination
```

### Strategy 4: Hash of long URL

```text
hash(longUrl) -> shorten hash
```

Pros:

```text
Same long URL can produce same code if desired
Simple conceptually
```

Cons:

```text
Collisions still possible
Difficult with custom alias
Can leak deterministic behavior
```

For this project, best learning path:

```text
Start simple:
Random Base62 + DB UNIQUE + retry

Later learn:
DB ID + Base62
Then distributed ID strategies
```

---

## 15. Source Of Truth vs Speed Layer

This is one of the most important production mental models.

```text
Postgres = source of truth
Redis    = speed layer
Kafka    = analytics transport
```

Source of truth means:

```text
If data is in Postgres, the mapping exists.
If data is only in Redis, it is not durable enough.
```

Redis is fast but usually used as cache.

Kafka is for event movement, not primary lookup storage.

Correct mental model:

```text
                 +----------------+
                 |   Postgres     |
                 | Source of truth|
                 +----------------+
                         ^
                         |
                  cache miss/load
                         |
+---------+       +------+-------+
| Browser | ----> | Redis Cache  |
+---------+       +--------------+
                         ^
                         |
                    App service
```

Read with cache:

```text
1. Check Redis for shortCode.
2. If found, return redirect.
3. If not found, query Postgres.
4. If found in Postgres, store in Redis.
5. Return redirect.
```

Cache-aside mental model:

```text
App owns cache loading.
Cache is not magic.
```

Important rule:

```text
Cache can be wrong or missing.
Database must remain correct.
```

---

## 16. Redirect Status Mental Model

When redirecting, the server returns a status code and a `Location` header.

Common redirect codes:

```text
301 Moved Permanently
302 Found
307 Temporary Redirect
308 Permanent Redirect
```

For v1, prefer:

```text
302 Found
```

Why?

```text
302 is safer during development.
Browsers and clients are less likely to permanently cache the redirect.
If mapping changes or is blocked later, you avoid some permanent-cache surprises.
```

Example response:

```http
HTTP/1.1 302 Found
Location: https://example.com/article/123
```

Mental model:

```text
The app does not fetch the destination page.
The browser fetches it after receiving Location.
```

Flow:

```text
Browser -> mini.ly/aB91xZ
App     -> 302 Location: original URL
Browser -> original URL
```

Important misconception:

```text
The URL shortener is not proxying the whole target website.
It only tells the browser where to go.
```

---

## 17. Expiry And Status Mental Model

A short URL may not always be active.

Possible statuses:

```text
ACTIVE
EXPIRED
BLOCKED
DELETED
```

Expiry field:

```text
expires_at
```

Redirect decision:

```text
shortCode not found       -> 404 Not Found
shortCode expired         -> 410 Gone
shortCode blocked         -> 403 Forbidden
shortCode active          -> 302 Redirect
```

ASCII decision tree:

```text
GET /code
   |
   v
Find mapping?
   |
   +-- no --> 404 Not Found
   |
   +-- yes
        |
        v
   Status BLOCKED?
        |
        +-- yes --> 403 Forbidden
        |
        +-- no
             |
             v
        Expired?
             |
             +-- yes --> 410 Gone
             |
             +-- no  --> 302 Redirect
```

Why this matters:

```text
A deleted, expired, blocked, and unknown link are not always the same.
```

For v1, keep it simple but clear:

```text
unknown -> 404
expired -> 410
blocked -> 403
active  -> 302
```

This makes debugging, analytics, and user experience much cleaner.

---

## 18. Analytics Mental Model

Analytics means tracking clicks.

Possible click event:

```json
{
  "shortCode": "aB91xZ",
  "clickedAt": "2026-06-21T10:00:00Z",
  "userAgent": "Mozilla/5.0",
  "referer": "https://twitter.com",
  "ipHash": "hashed-ip"
}
```

Wrong mental model:

```text
Redirect must save analytics before redirecting.
```

Correct mental model:

```text
Redirect must succeed even if analytics fails.
```

Analytics is a side effect.

Critical path:

```text
lookup -> redirect
```

Non-critical side path:

```text
emit click event -> process later
```

ASCII:

```text
                 +------------------+
GET /aB91xZ ---> | Lookup long URL  |
                 +------------------+
                          |
                          v
                 +------------------+
                 | Return 302       |
                 +------------------+
                          |
                          v
                  Browser continues

Side effect:

                 +------------------+
                 | Click event      |
                 +------------------+
                          |
                          v
                 +------------------+
                 | Kafka later      |
                 +------------------+
```

Production rule:

```text
Redirect correctness > analytics completeness.
```

If Kafka is slow, analytics may lag. But redirects should continue.

---

## 19. Abuse And Security Mental Model

A URL shortener can hide dangerous links.

Attackers may create short links pointing to:

```text
phishing pages
malware downloads
fake login pages
internal services
spam campaigns
```

Minimum security thinking:

```text
1. Validate URL scheme: only http/https.
2. Reject javascript:, file:, ftp: initially.
3. Block localhost/internal IP targets in production.
4. Rate limit create endpoint.
5. Protect admin APIs.
6. Avoid exposing stack traces.
7. Log suspicious activity.
8. Add malware scanning later.
```

Dangerous examples:

```text
javascript:alert(1)
http://localhost:8080/admin
http://127.0.0.1:5432
http://169.254.169.254/latest/meta-data
```

Why block metadata IP?

In cloud environments, metadata endpoints can expose credentials or instance information if abused in server-side fetch flows.

For this project, the app is not fetching the target page before redirecting. But if later you add preview generation, malware scanning, or title extraction, SSRF risk becomes serious.

Mental model:

```text
Redirect-only is safer than server-fetching.
Preview/scanning features require SSRF protection.
```

---

## 20. Scaling Evolution

Do not build the final distributed system on day one.

Build in layers.

### Stage 1: Single app + Postgres

```text
Browser -> Spring Boot -> Postgres
```

Good for learning:

```text
API
DB schema
validation
redirect
basic tests
```

### Stage 2: Add Redis for redirect speed

```text
Browser -> Spring Boot -> Redis
                         -> Postgres on miss
```

Good for:

```text
low latency
high read throughput
DB protection
```

### Stage 3: Add Kafka for click analytics

```text
Redirect -> emit click event -> Kafka -> analytics worker
```

Good for:

```text
async processing
decoupling
analytics scalability
```

### Stage 4: Add read replicas

```text
Writes -> primary DB
Reads  -> replica DB where safe
```

Good for:

```text
read scalability
DB load distribution
```

### Stage 5: Add sharding

```text
shortCode prefix/hash -> shard selection
```

Good for:

```text
massive dataset
horizontal DB scale
```

### Stage 6: Kubernetes and cloud

```text
Load Balancer -> multiple app pods -> Redis cluster -> DB
```

Final mental evolution:

```text
CRUD app
  ↓
read-heavy lookup service
  ↓
cached redirect system
  ↓
event-driven analytics system
  ↓
distributed high-scale platform
```

---

## 21. Step-by-Step Dry Runs

### Dry Run 1: Create short URL with generated code

Input:

```json
{
  "longUrl": "https://example.com/blog/spring-boot"
}
```

Step-by-step:

```text
1. Controller receives POST /api/v1/urls.
2. Request body contains longUrl.
3. Service validates it starts with http/https.
4. No custom alias provided.
5. Service generates random Base62 code: k9Lm2Q.
6. Service creates entity:
   shortCode = k9Lm2Q
   longUrl   = https://example.com/blog/spring-boot
   status    = ACTIVE
7. Repository inserts entity into Postgres.
8. Postgres checks UNIQUE(short_code).
9. Insert succeeds.
10. API returns https://mini.ly/k9Lm2Q.
```

State:

```text
+------------+--------------------------------------+--------+
| short_code | long_url                             | status |
+------------+--------------------------------------+--------+
| k9Lm2Q     | https://example.com/blog/spring-boot | ACTIVE |
+------------+--------------------------------------+--------+
```

---

### Dry Run 2: Redirect active short URL

Request:

```http
GET /k9Lm2Q
```

Steps:

```text
1. Browser sends GET /k9Lm2Q.
2. Controller extracts shortCode = k9Lm2Q.
3. Service looks up mapping.
4. Mapping exists.
5. Status is ACTIVE.
6. expiresAt is null.
7. Service returns long URL.
8. Controller returns 302 with Location header.
9. Browser opens https://example.com/blog/spring-boot.
```

Response:

```http
HTTP/1.1 302 Found
Location: https://example.com/blog/spring-boot
```

---

### Dry Run 3: Unknown short code

Request:

```http
GET /wrong99
```

Lookup:

```text
wrong99 not found in short_urls
```

Result:

```http
404 Not Found
```

Why not redirect?

```text
No mapping exists.
The system cannot safely guess.
```

---

### Dry Run 4: Expired short URL

Existing row:

```text
shortCode = sale
longUrl = https://shop.com/big-sale
expiresAt = 2026-01-01T00:00:00Z
```

Current time:

```text
2026-06-21T10:00:00Z
```

Request:

```http
GET /sale
```

Decision:

```text
expiresAt < now
```

Result:

```http
410 Gone
```

Mental model:

```text
The mapping existed, but it is no longer valid.
```

---

### Dry Run 5: Generated code collision

Thread A:

```text
generates aB91xZ
```

Thread B:

```text
also generates aB91xZ
```

Postgres:

```text
Thread A insert succeeds.
Thread B insert fails with duplicate key.
```

Application:

```text
Because code was generated internally, retry Thread B with a new code.
```

Result:

```text
Thread B generates kP82Lm and succeeds.
```

---

### Dry Run 6: Custom alias collision

Existing:

```text
mohamed -> https://example.com/profile/mohamed
```

New request:

```json
{
  "longUrl": "https://another.com",
  "customAlias": "mohamed"
}
```

Postgres:

```text
duplicate key on short_code = mohamed
```

Application:

```text
Because user explicitly requested this alias, do not retry silently.
Return 409 Conflict.
```

Result:

```http
409 Conflict
```

---

## 22. Production Failure Stories

### Failure Story 1: Analytics makes redirect slow

Bad design:

```text
GET /aB91xZ
  -> lookup long URL
  -> insert click analytics into DB
  -> return 302
```

One day analytics DB becomes slow.

Result:

```text
Redirect latency increases.
Users wait.
Some requests timeout.
Short links appear broken.
```

Root cause:

```text
Non-critical analytics was placed in the critical redirect path.
```

Fix:

```text
GET /aB91xZ
  -> lookup long URL
  -> return 302
  -> emit analytics asynchronously
```

Lesson:

```text
Do not let analytics availability decide redirect availability.
```

---

### Failure Story 2: Hot key destroys database

A famous influencer shares:

```text
https://mini.ly/deal
```

Millions of clicks arrive.

Bad design:

```text
Every click queries Postgres.
```

Result:

```text
DB CPU spikes.
Connection pool saturates.
p99 latency explodes.
Redirect errors increase.
```

Fix:

```text
Cache deal -> longUrl in Redis.
Most requests hit Redis.
Postgres is protected.
```

Lesson:

```text
Popular short codes become hot keys.
Hot read paths need caching.
```

---

### Failure Story 3: No unique constraint causes corrupted mapping

Bad design:

```text
Application checks if code exists.
No DB unique constraint.
```

During traffic spike:

```text
Two app pods insert same shortCode.
```

Result:

```text
Same shortCode points to multiple rows.
Redirect becomes nondeterministic.
Users go to wrong destination.
```

Fix:

```text
Add UNIQUE(short_code).
Handle duplicate key correctly.
```

Lesson:

```text
Invariants belong in the database, not only in application code.
```

---

### Failure Story 4: 301 used too early

Team uses:

```text
301 Moved Permanently
```

During testing, wrong mapping is created:

```text
abc123 -> wrong-site.com
```

Browser caches permanent redirect.

Even after DB is fixed, some clients keep going to wrong destination.

Fix:

```text
Use 302 initially.
Use 301 only when product semantics and caching behavior are fully understood.
```

Lesson:

```text
HTTP status choices have production consequences.
```

---

## 23. Debugging Mindset

When a redirect fails, ask:

```text
Is the request reaching the app?
Is shortCode extracted correctly?
Does the mapping exist?
Is the mapping active?
Is it expired?
Is Redis stale?
Is Postgres available?
Is the app returning correct Location header?
Is browser caching an old redirect?
```

Debug flow:

```text
User says link is broken
        |
        v
Check HTTP status
        |
        +-- 404 -> mapping missing or wrong code
        |
        +-- 410 -> expired
        |
        +-- 403 -> blocked
        |
        +-- 500 -> app/db/cache issue
        |
        +-- 302 but wrong target -> mapping data issue
```

Useful logs:

```text
correlationId
shortCode
status
cacheHit
lookupLatencyMs
redirectStatus
errorCode
```

Example structured log:

```json
{
  "event": "redirect_lookup",
  "correlationId": "req-123",
  "shortCode": "aB91xZ",
  "cacheHit": true,
  "status": "ACTIVE",
  "latencyMs": 7,
  "result": "REDIRECT"
}
```

Golden rule:

```text
You cannot debug what you did not measure or log.
```

But also:

```text
Do not log sensitive full URLs blindly in production if they may contain tokens.
```

---

## 24. Common Misconceptions

### Misconception 1: URL shortener compresses URLs

Wrong:

```text
The short code mathematically contains the original URL.
```

Correct:

```text
The short code is only a lookup key.
The long URL is stored separately.
```

---

### Misconception 2: Redis can be the only storage

Wrong:

```text
Store everything in Redis only.
```

Correct:

```text
Postgres is source of truth.
Redis is speed layer.
```

Unless Redis is configured deliberately as durable primary storage, treat it as cache.

---

### Misconception 3: Redirect is just another controller

Wrong:

```text
Redirect is a normal read API.
```

Correct:

```text
Redirect is the hot path and needs special care.
```

---

### Misconception 4: Duplicate code is impossible

Wrong:

```text
Random code will never collide.
```

Correct:

```text
Collision may be rare, but system must handle it.
```

---

### Misconception 5: Analytics should be strongly consistent

Wrong:

```text
Every click must be saved before redirect.
```

Correct:

```text
Redirect should not depend on analytics success.
```

---

### Misconception 6: 301 and 302 are interchangeable

Wrong:

```text
Both redirect, so it does not matter.
```

Correct:

```text
Permanent redirects can be cached aggressively.
Use 302 first unless you intentionally want permanent behavior.
```

---

## 25. Interview-Ready Explanation

If an interviewer asks:

```text
Design a URL shortener like Bitly.
```

Strong answer:

```text
I would model the system as a read-heavy key-value redirect service. The core
mapping is shortCode to longUrl. The create path validates the long URL, generates
or accepts a short code, enforces uniqueness at the database level, and stores the
mapping durably. The redirect path extracts the short code, looks up the long URL,
checks status and expiry, and returns an HTTP 302 redirect. Since redirects are much
more frequent than creations, I would optimize the read path with Redis cache and
keep Postgres as the source of truth. Click analytics should be asynchronous through
Kafka or a background worker so that analytics failures do not break redirects.
For scale, I can add indexes, read replicas, sharding by shortCode, rate limiting,
and observability around p95/p99 latency, cache hit ratio, and error rate.
```

Why this is strong:

```text
1. Starts with correct mental model.
2. Separates create and redirect paths.
3. Mentions database uniqueness.
4. Understands read-heavy traffic.
5. Uses Redis correctly.
6. Uses Kafka for async analytics.
7. Talks about scaling evolution.
8. Shows production safety mindset.
```

Senior-level one-liner:

```text
The write path is about safely creating a durable unique mapping; the read path is about resolving that mapping with extremely low latency and high availability.
```

---

## 26. Senior Engineer Checklist

Before coding this system, you should be able to answer:

```text
[ ] What is the core mapping?
[ ] Why is redirect the hot path?
[ ] Why is Postgres the source of truth?
[ ] Why is Redis only a speed layer?
[ ] How do you enforce shortCode uniqueness?
[ ] What happens when generated codes collide?
[ ] What happens when custom alias collides?
[ ] Why use 302 initially?
[ ] What status for unknown code?
[ ] What status for expired code?
[ ] Why should analytics be async?
[ ] What can go wrong with hot keys?
[ ] What metrics prove the system is healthy?
[ ] How can the system evolve to sharding?
[ ] What abuse risks exist?
```

If you can answer these, you understand the system beyond CRUD.

---

## 27. One-Page Cheat Sheet

```text
URL Shortener = read-heavy key-value redirect system

Core mapping:
shortCode -> longUrl

Create path:
POST longUrl
validate
generate code / accept alias
insert into DB
return short URL

Redirect path:
GET /{shortCode}
lookup mapping
check status
check expiry
return 302 Location

Source of truth:
Postgres

Speed layer:
Redis

Analytics:
Kafka/background worker later

Main DB constraint:
UNIQUE(short_code)

Main index:
short_code

Unknown code:
404 Not Found

Expired code:
410 Gone

Blocked code:
403 Forbidden

Active code:
302 Found

Generated collision:
retry

Custom alias collision:
409 Conflict

Main scaling truth:
Reads >> writes

Main performance metric:
redirect p95/p99 latency

Main cache metric:
cache hit ratio

Main production rule:
Do not let analytics break redirects.

Main security rule:
Validate URLs and prevent abuse.

Main interview phrase:
The write path creates a durable unique token; the read path resolves that token extremely fast.
```

---

## 28. One Picture To Remember

```text
                           MINI URL SHORTENER MENTAL MODEL

                      "Create once, redirect many times"

 ┌────────────────────────────────────┐       ┌────────────────────────────────────┐
 │            WRITE PATH               │       │             READ PATH              │
 │       less frequent, correctness     │       │      very frequent, low latency     │
 └────────────────────────────────────┘       └────────────────────────────────────┘

        POST /api/v1/urls                              GET /aB91xZ
                │                                           │
                v                                           v
      ┌───────────────────┐                      ┌───────────────────┐
      │ Validate long URL │                      │ Extract shortCode  │
      └───────────────────┘                      └───────────────────┘
                │                                           │
                v                                           v
      ┌───────────────────┐                      ┌───────────────────┐
      │ Generate token    │                      │ Lookup in Redis   │
      │ or custom alias   │                      │ later             │
      └───────────────────┘                      └───────────────────┘
                │                                           │
                v                                           v
      ┌───────────────────┐                      ┌───────────────────┐
      │ Save in Postgres  │ <------------------- │ Postgres on miss  │
      │ UNIQUE enforced   │                      │ source of truth   │
      └───────────────────┘                      └───────────────────┘
                │                                           │
                v                                           v
      ┌───────────────────┐                      ┌───────────────────┐
      │ Return short URL  │                      │ Return 302        │
      └───────────────────┘                      └───────────────────┘
                                                            │
                                                            v
                                                   ┌───────────────────┐
                                                   │ Browser opens     │
                                                   │ original long URL │
                                                   └───────────────────┘

 Side path later:
 Redirect event ───────────────> Kafka ───────────────> Analytics Worker

 Final memory:
 A URL shortener is not about shortening strings.
 It is about safely creating a unique token and resolving it extremely fast.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. A URL shortener is a read-heavy key-value redirect system.
2. The short code is a token, not a compressed URL.
3. Postgres is the source of truth; Redis is a speed layer.
4. The database must enforce shortCode uniqueness.
5. Redirect must stay fast, and analytics must not block it.
```

If these five ideas are clear, every future chapter becomes easier:

```text
Base62
ID generation
Postgres schema
Redis cache
Kafka analytics
Sharding
Rate limiting
Kubernetes
Observability
System design interview answer
```
