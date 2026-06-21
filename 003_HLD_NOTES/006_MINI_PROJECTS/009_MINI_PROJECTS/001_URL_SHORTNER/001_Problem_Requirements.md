# 001_Problem_Requirements.md
# MiniURLShortener — Problem Requirements

> Core mental model: **A URL shortener is a read-heavy redirect system where the product looks simple, but the real engineering comes from controlling ID generation, lookup latency, durability, abuse, observability, and scale.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Functional Requirements](#4-functional-requirements)
- [5. Non-Functional Requirements](#5-non-functional-requirements)
- [6. Out of Scope For Version 1](#6-out-of-scope-for-version-1)
- [7. Actors And Use Cases](#7-actors-and-use-cases)
- [8. API Requirements](#8-api-requirements)
- [9. Data Requirements](#9-data-requirements)
- [10. Redirect Flow Mental Model](#10-redirect-flow-mental-model)
- [11. Create Short URL Flow](#11-create-short-url-flow)
- [12. Step-by-Step Dry Runs](#12-step-by-step-dry-runs)
- [13. Edge Cases](#13-edge-cases)
- [14. Validation Rules](#14-validation-rules)
- [15. Error Model](#15-error-model)
- [16. Security And Abuse Requirements](#16-security-and-abuse-requirements)
- [17. Analytics Requirements](#17-analytics-requirements)
- [18. Performance Targets](#18-performance-targets)
- [19. Interview-Ready Requirement Answer](#19-interview-ready-requirement-answer)
- [20. Common Mistakes](#20-common-mistakes)
- [21. Production Failure Thinking](#21-production-failure-thinking)
- [22. Requirement Checklist](#22-requirement-checklist)
- [23. One Picture To Remember](#23-one-picture-to-remember)

---

## 1. Why This Exists

A URL shortener converts a long URL into a short code that is easy to share.

Example:

```text
Long URL:
https://example.com/products/mobile/iphone-15-pro-max?campaign=summer&source=email

Short URL:
https://mini.ly/aB91xZ
```

But the real system is not about string shortening.

The real system is about this:

```text
WRITE path: create a short code safely
READ path : redirect users extremely fast
```

The redirect path is usually much hotter than the creation path.

For one short URL, you may create it once but redirect it thousands or millions of times.

```text
Create short URL: 1 time
Redirect short URL: 10,000+ times
```

So the system must be designed as a **read-heavy lookup system**, not just a CRUD app.

---

## 2. The One Core Mental Model

A URL shortener is a **key-value lookup system with redirect behavior**.

```text
shortCode  --->  longUrl

abc123     --->  https://google.com/search?q=spring+boot
xY9zQ1     --->  https://github.com/mthaiseer/project
```

At runtime:

```text
User clicks short URL
        |
        v
Extract shortCode
        |
        v
Find original long URL
        |
        v
HTTP redirect to long URL
```

The short code is the key.
The original URL is the value.
The redirect endpoint is the hot path.

### Real-world analogy

Think of a hotel cloakroom token.

```text
You give coat  ----------> cloakroom stores coat
You receive token -------> A17
Later you show A17 ------> cloakroom returns coat
```

URL shortener:

```text
You give long URL -------> system stores long URL
System gives shortCode --> aB91xZ
Later user visits aB91xZ -> system returns long URL via redirect
```

The token must be unique.
The lookup must be fast.
The storage must not lose mappings.

---

## 3. Problem Statement

Build a backend system that allows users to create short URLs and redirect users from short URLs to original long URLs.

### Version 1 goal

Build a clean Spring Boot backend with:

```text
1. Create short URL API
2. Redirect API
3. PostgreSQL persistence
4. Basic validation
5. Basic error handling
6. Basic observability hooks
```

### Later scale goal

Evolve the same system into:

```text
1. Redis-backed read path
2. Rate-limited create path
3. Sharded storage
4. Kafka-based click analytics
5. Kubernetes deployment
6. Production monitoring
```

---

## 4. Functional Requirements

Functional requirements define **what the system does**.

### FR1: Create short URL

User sends a long URL and receives a short URL.

```http
POST /api/v1/urls
Content-Type: application/json

{
  "longUrl": "https://example.com/articles/system-design",
  "customAlias": null,
  "expiresAt": null
}
```

Response:

```json
{
  "shortCode": "aB91xZ",
  "shortUrl": "https://mini.ly/aB91xZ",
  "longUrl": "https://example.com/articles/system-design",
  "expiresAt": null
}
```

### FR2: Redirect short URL

When a user visits:

```http
GET /aB91xZ
```

The system should return:

```http
HTTP/1.1 302 Found
Location: https://example.com/articles/system-design
```

### FR3: Support custom alias

User may request a custom short code.

```json
{
  "longUrl": "https://example.com/profile/mohamed",
  "customAlias": "mohamed"
}
```

Then:

```text
https://mini.ly/mohamed
```

If alias already exists, return conflict.

### FR4: Expiry support

Short URLs may optionally expire.

```json
{
  "longUrl": "https://example.com/offer",
  "expiresAt": "2026-12-31T23:59:59Z"
}
```

After expiry, redirect should fail with a clear error page/API response.

### FR5: Basic analytics event

On redirect, system should record or emit click information.

For version 1, this can be simple DB/log-based.
For later phases, it moves to Kafka.

Analytics fields may include:

```text
shortCode
clickedAt
ipHash
userAgent
referer
country/city later
```

### FR6: User ownership later

Version 1 may allow anonymous creation.
Later versions can support authenticated users.

---

## 5. Non-Functional Requirements

Non-functional requirements define **how well the system behaves**.

### NFR1: Low redirect latency

Redirect must be very fast because it is the hot path.

Target for mature version:

```text
p50 redirect latency: < 20 ms from cache
p95 redirect latency: < 80 ms
p99 redirect latency: < 150 ms
```

For local v1 with Postgres only:

```text
p95 redirect latency: < 100–200 ms locally
```

### NFR2: High availability

Redirect should continue working even under high load.

Reason:

```text
If create API is down, bad.
If redirect API is down, all shared links break.
```

Redirect path has higher availability priority.

### NFR3: Durability

Once a short URL is created successfully, its mapping must not be lost.

```text
shortCode -> longUrl must survive app restart
```

So v1 needs PostgreSQL persistence.

### NFR4: Uniqueness

Two long URLs may map to different short codes, but one short code must map to only one active long URL.

```text
abc123 -> URL_A
abc123 -> URL_B  ❌ not allowed
```

### NFR5: Scalability

The design should support evolution from:

```text
Single app + Postgres
```

to:

```text
Load balancer
Multiple app pods
Redis cache
Postgres primary + replicas
Kafka analytics
Sharded storage
```

### NFR6: Abuse resistance

The system should prevent:

```text
spam links
malware links
brute-force scanning
custom alias squatting
high-frequency create abuse
```

Version 1 can implement validation and rate limiting later.

---

## 6. Out of Scope For Version 1

Do not build everything on day one.

For v1, avoid:

```text
❌ Multi-region deployment
❌ Full user dashboard
❌ Billing
❌ Advanced geo analytics
❌ QR code generation
❌ AI malware detection
❌ Complex custom domains
❌ Cassandra/sharding from day one
❌ Kafka-first analytics from day one
```

Why?

Because first you need the core working system:

```text
Create mapping
Store mapping
Redirect mapping
Handle errors
```

Everything else is an optimization or product extension.

---

## 7. Actors And Use Cases

### Actor 1: Anonymous user

Can create a short URL and share it.

```text
POST long URL -> receive short URL
```

### Actor 2: Link visitor

Clicks a short URL and gets redirected.

```text
GET /abc123 -> redirect to original URL
```

### Actor 3: Authenticated user later

Can manage URLs, view analytics, delete links, set expiry.

### Actor 4: Admin later

Can block malicious URLs and inspect abuse patterns.

---

## 8. API Requirements

### API 1: Create short URL

```http
POST /api/v1/urls
```

Request:

```json
{
  "longUrl": "https://example.com/articles/java",
  "customAlias": "java-guide",
  "expiresAt": "2026-12-31T23:59:59Z"
}
```

Response success:

```http
201 Created
```

```json
{
  "id": 101,
  "shortCode": "java-guide",
  "shortUrl": "https://mini.ly/java-guide",
  "longUrl": "https://example.com/articles/java",
  "expiresAt": "2026-12-31T23:59:59Z",
  "createdAt": "2026-06-21T10:00:00Z"
}
```

Possible errors:

```text
400 Bad Request     invalid long URL
409 Conflict        custom alias already exists
429 Too Many Requests later
500 Internal Error  unexpected failure
```

---

### API 2: Redirect

```http
GET /{shortCode}
```

Success:

```http
302 Found
Location: https://example.com/articles/java
```

Possible errors:

```text
404 Not Found       short code does not exist
410 Gone            short code expired
403 Forbidden       short code blocked
500 Internal Error  unexpected failure
```

### 301 vs 302 redirect

For early version, prefer **302 Found**.

Reason:

```text
302 = temporary redirect
Browser/CDN less likely to permanently cache wrong mapping
Safer during development
```

Later, for permanent public links, 301 may be considered carefully.

---

## 9. Data Requirements

Minimum table:

```sql
CREATE TABLE short_urls (
    id BIGSERIAL PRIMARY KEY,
    short_code VARCHAR(32) NOT NULL UNIQUE,
    long_url TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    expires_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_short_urls_short_code ON short_urls(short_code);
CREATE INDEX idx_short_urls_expires_at ON short_urls(expires_at);
```

Mental model:

```text
short_urls table is the source of truth.
Redis later is only a speed layer.
Kafka later is only analytics transport.
```

Important invariant:

```text
short_code must be unique forever or at least unique among active mappings.
```

For beginner production quality, choose unique forever.
It avoids dangerous reuse bugs.

---

## 10. Redirect Flow Mental Model

Redirect is the main hot path.

```text
Browser
  |
  | GET /aB91xZ
  v
Spring Boot Controller
  |
  | extract shortCode
  v
URL Service
  |
  | lookup shortCode
  v
Repository / Cache later
  |
  | found longUrl
  v
Return 302 Location header
  |
  v
Browser requests original long URL
```

ASCII sequence:

```text
+---------+        +-------------+        +----------+        +----------+
| Browser |        | Controller  |        | Service  |        | Postgres |
+---------+        +-------------+        +----------+        +----------+
     |                    |                    |                    |
     | GET /abc123        |                    |                    |
     |------------------->|                    |                    |
     |                    | findByCode(abc123) |                    |
     |                    |------------------->|                    |
     |                    |                    | SELECT long_url    |
     |                    |                    |------------------->|
     |                    |                    | long_url           |
     |                    |                    |<-------------------|
     |                    | 302 Location       |                    |
     |<-------------------|                    |                    |
     |                    |                    |                    |
```

---

## 11. Create Short URL Flow

```text
Client
  |
  | POST longUrl
  v
Validate URL
  |
  v
Generate shortCode or use customAlias
  |
  v
Check uniqueness
  |
  v
Save mapping
  |
  v
Return shortUrl
```

ASCII flow:

```text
+------------------+
| Receive request  |
+------------------+
          |
          v
+------------------+
| Validate long URL|
+------------------+
          |
          v
+---------------------------+
| custom alias provided?    |
+---------------------------+
       | yes              | no
       v                  v
+-------------+     +----------------+
| validate it |     | generate code  |
+-------------+     +----------------+
       |                  |
       v                  v
+-------------------------------+
| insert into DB with UNIQUE key|
+-------------------------------+
          |
          v
+------------------+
| Return short URL |
+------------------+
```

Important production thinking:

Do not only check uniqueness in application code.
The database must enforce uniqueness.

Bad:

```text
if not exists(code): insert(code)
```

Race condition:

```text
Thread A checks code available
Thread B checks code available
Thread A inserts
Thread B inserts -> conflict
```

Correct:

```text
UNIQUE(short_code) at DB level
Handle duplicate key exception
Retry generated code if needed
```

---

## 12. Step-by-Step Dry Runs

### Dry run 1: Normal create

Input:

```json
{
  "longUrl": "https://example.com/blog/123"
}
```

Steps:

```text
1. Controller receives request
2. Service validates URL format
3. No custom alias provided
4. Service generates shortCode = k9Ab2Q
5. Repository inserts row
6. DB unique constraint passes
7. API returns https://mini.ly/k9Ab2Q
```

State after create:

```text
short_code | long_url
-----------|-----------------------------
k9Ab2Q     | https://example.com/blog/123
```

---

### Dry run 2: Normal redirect

Request:

```http
GET /k9Ab2Q
```

Steps:

```text
1. Controller extracts k9Ab2Q
2. Service searches mapping
3. Mapping exists
4. Status is ACTIVE
5. expiresAt is null
6. System returns 302 Location: https://example.com/blog/123
```

Browser then calls the original URL.

---

### Dry run 3: Expired link

State:

```text
short_code | long_url              | expires_at
-----------|-----------------------|----------------------
offer1     | https://shop.com/deal | 2026-01-01T00:00:00Z
```

Current time:

```text
2026-06-21T10:00:00Z
```

Request:

```http
GET /offer1
```

Result:

```text
expires_at < now
return 410 Gone
```

---

### Dry run 4: Custom alias conflict

Existing row:

```text
short_code = mohamed
```

New request:

```json
{
  "longUrl": "https://another-site.com",
  "customAlias": "mohamed"
}
```

Result:

```text
DB unique constraint fails
service converts duplicate key into 409 Conflict
```

---

## 13. Edge Cases

Handle these early:

```text
1. Empty URL
2. URL without scheme: example.com
3. Unsupported scheme: ftp://example.com
4. Very long URL
5. Invalid custom alias characters
6. Custom alias already taken
7. Short code not found
8. Expired link
9. Blocked link
10. Database timeout
11. Duplicate generated code collision
12. Bot repeatedly creating links
13. Redirect loop to same short domain
14. URL pointing to localhost/internal IP
```

For v1, at least cover 1–8.
For production, cover all.

---

## 14. Validation Rules

### Long URL validation

Accept:

```text
https://example.com
http://example.com/page
https://sub.domain.com/path?a=1
```

Reject:

```text
not-a-url
javascript:alert(1)
ftp://example.com/file
http://localhost:8080/admin       production reject
http://127.0.0.1:5432             production reject
```

### Custom alias validation

Suggested rules:

```text
Length: 4 to 32 chars
Allowed: a-z A-Z 0-9 - _
Not allowed: spaces, slash, ?, #, %, unicode confusables initially
Reserved words blocked: api, admin, actuator, health, metrics
```

Why reserved words matter:

```text
/health
/api
/admin
```

These paths may conflict with system endpoints.

---

## 15. Error Model

Use consistent error response for API errors.

```json
{
  "timestamp": "2026-06-21T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "INVALID_URL",
  "message": "longUrl must be a valid http or https URL",
  "path": "/api/v1/urls"
}
```

Recommended domain error codes:

```text
INVALID_URL
INVALID_ALIAS
ALIAS_ALREADY_EXISTS
SHORT_CODE_NOT_FOUND
SHORT_CODE_EXPIRED
SHORT_CODE_BLOCKED
RATE_LIMIT_EXCEEDED
INTERNAL_ERROR
```

Redirect endpoint can return either:

```text
1. JSON error for API clients
2. HTML error page for browser users
```

For backend learning, JSON is enough initially.

---

## 16. Security And Abuse Requirements

A URL shortener can be abused easily.

Attackers may use it to hide malicious links.

Minimum security thinking:

```text
1. Validate URL scheme
2. Block internal/private network targets in production
3. Rate limit create API
4. Rate limit suspicious redirects if needed
5. Do not expose stack traces
6. Add audit logs
7. Later integrate malware/phishing checks
8. Protect admin endpoints
```

Private network blocking examples:

```text
127.0.0.1
localhost
10.0.0.0/8
172.16.0.0/12
192.168.0.0/16
169.254.0.0/16
```

Why?

Because attackers can create short URLs pointing to internal services and trick systems/users into accessing them.

---

## 17. Analytics Requirements

Analytics should not slow down redirect.

Bad design:

```text
Redirect request waits for analytics insert
```

Better later:

```text
Redirect request emits event to Kafka and returns quickly
```

Version 1 simple approach:

```text
Log click event asynchronously or store simple count
```

Future analytics event:

```json
{
  "eventType": "URL_CLICKED",
  "shortCode": "aB91xZ",
  "clickedAt": "2026-06-21T10:00:00Z",
  "ipHash": "hash-value",
  "userAgent": "Mozilla/5.0",
  "referer": "https://twitter.com"
}
```

Important principle:

```text
Redirect correctness > analytics completeness
```

If analytics fails, redirect should still work.

---

## 18. Performance Targets

Start with realistic local targets.

### Local v1

```text
Create URL: p95 < 300 ms
Redirect:   p95 < 200 ms using Postgres
```

### Production v2 with Redis

```text
Create URL: p95 < 150 ms
Redirect:   p95 < 50 ms cache hit
```

### High-scale target

```text
Reads are 100x to 1000x writes
Cache hit ratio > 95%
DB lookup only on cache miss
```

Example ratio:

```text
100 creates/sec
100,000 redirects/sec
```

This is why design must be read-heavy.

---

## 19. Interview-Ready Requirement Answer

If interviewer asks: “Design a URL shortener like Bitly.”

Strong answer:

```text
I will first clarify the scope. The core features are creating a short URL,
redirecting from short code to long URL, optional custom aliases, optional expiry,
and basic click analytics. The redirect path is much hotter than the creation path,
so I will optimize reads more aggressively than writes. For v1, Postgres can be the
source of truth. For scale, Redis can cache shortCode-to-longUrl mappings, Kafka can
handle click analytics asynchronously, and the database can be indexed or sharded by
shortCode. I will use 302 redirects initially to avoid dangerous permanent browser
caching during early stages. I will also enforce uniqueness at the database level and
handle collision retries in the application.
```

That answer shows:

```text
1. Requirement clarity
2. Read-heavy understanding
3. Correct redirect behavior
4. DB uniqueness awareness
5. Evolution path to scale
6. Production safety thinking
```

---

## 20. Common Mistakes

### Mistake 1: Treating it like simple CRUD

Wrong mental model:

```text
URL shortener = CRUD app
```

Correct:

```text
URL shortener = read-heavy redirect lookup system
```

### Mistake 2: No DB unique constraint

Application-only uniqueness check fails under concurrency.

Always enforce:

```sql
UNIQUE(short_code)
```

### Mistake 3: Analytics blocking redirect

Never make redirect depend strongly on analytics.

```text
Analytics down should not mean redirects down.
```

### Mistake 4: Wrong redirect status casually

Use 302 first.
Use 301 only when you deeply understand permanent caching effects.

### Mistake 5: No abuse thinking

Public URL shorteners attract spam, phishing, bots, and scanners.

---

## 21. Production Failure Thinking

### Failure story: cache/database hot key

One celebrity shares:

```text
https://mini.ly/mega-sale
```

Millions of users click it.

If every request hits Postgres:

```text
DB CPU spikes
Connection pool saturates
Redirect p99 becomes seconds
Some requests timeout
```

Better design later:

```text
Redis caches mega-sale -> longUrl
Application serves redirects from memory/cache quickly
Postgres protected from read flood
```

### Failure story: analytics dependency outage

Bad design:

```text
Redirect -> insert click analytics -> return 302
```

Analytics DB slows down.
Redirects also slow down.

Correct design:

```text
Redirect -> return 302
        -> async analytics event best effort
```

Main lesson:

```text
Never let non-critical side effects break the critical user path.
```

---

## 22. Requirement Checklist

Before coding, confirm:

```text
[ ] Can create short URL
[ ] Can redirect using short code
[ ] shortCode is unique
[ ] longUrl validation exists
[ ] custom alias conflict handled
[ ] expired URL returns 410
[ ] unknown code returns 404
[ ] DB schema has correct indexes
[ ] errors are consistent
[ ] redirect uses 302 initially
[ ] analytics is not on critical path later
[ ] abuse/security concerns are documented
[ ] system can evolve to Redis/Kafka/sharding later
```

---

## 23. One Picture To Remember

```text
                         MINI URL SHORTENER

        WRITE PATH                                  READ PATH
   less frequent, safer                         very frequent, fastest

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
| Save in Postgres    |                     | Return 302 redirect  |
+---------------------+                     +----------------------+
          |                                             |
          v                                             v
+---------------------+                     +----------------------+
| Return short URL    |                     | Browser opens target |
+---------------------+                     +----------------------+

                 Source of truth: Postgres
                 Speed layer later: Redis
                 Analytics later: Kafka
                 Scale later: Sharding + replicas
```

Final mental model:

```text
A URL shortener is not about making strings short.
It is about safely creating a unique token and resolving that token extremely fast.
```
