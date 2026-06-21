# 008_Id_Generation_Strategies.md
# MiniURLShortener — ID Generation Strategies

> Core mental model: **ID generation is the strategy for choosing the unique token behind a short URL. Base62 only changes representation; ID generation decides where the uniqueness, scalability, guessability, ordering, and collision behavior come from.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What ID Generation Must Guarantee](#4-what-id-generation-must-guarantee)
- [5. Base62 vs ID Generation](#5-base62-vs-id-generation)
- [6. Strategy Map](#6-strategy-map)
- [7. Strategy 1: Database Auto-Increment ID + Base62](#7-strategy-1-database-auto-increment-id--base62)
- [8. Strategy 2: Random Base62 Code](#8-strategy-2-random-base62-code)
- [9. Strategy 3: UUID-Based Code](#9-strategy-3-uuid-based-code)
- [10. Strategy 4: NanoID-Style Random Token](#10-strategy-4-nanoid-style-random-token)
- [11. Strategy 5: Snowflake-Like Distributed ID](#11-strategy-5-snowflake-like-distributed-id)
- [12. Strategy 6: Hash-Based Code](#12-strategy-6-hash-based-code)
- [13. Strategy Comparison Table](#13-strategy-comparison-table)
- [14. Collision Mental Model](#14-collision-mental-model)
- [15. Guessability Mental Model](#15-guessability-mental-model)
- [16. Ordering And Sortability](#16-ordering-and-sortability)
- [17. Sharding Readiness](#17-sharding-readiness)
- [18. Recommended Strategy For MiniURLShortener](#18-recommended-strategy-for-miniurlshortener)
- [19. Java Implementation: Random Base62 Generator](#19-java-implementation-random-base62-generator)
- [20. Java Implementation: DB ID + Base62](#20-java-implementation-db-id--base62)
- [21. Java Implementation: Snowflake-Like Generator](#21-java-implementation-snowflake-like-generator)
- [22. Step-by-Step Dry Runs](#22-step-by-step-dry-runs)
- [23. Production Failure Stories](#23-production-failure-stories)
- [24. Debugging Mindset](#24-debugging-mindset)
- [25. Common Mistakes](#25-common-mistakes)
- [26. Interview-Ready Explanation](#26-interview-ready-explanation)
- [27. Senior Engineer Checklist](#27-senior-engineer-checklist)
- [28. One-Page Cheat Sheet](#28-one-page-cheat-sheet)
- [29. One Picture To Remember](#29-one-picture-to-remember)

---

## 1. Why This Exists

In chapter 007, we learned Base62.

Base62 answers:

```text
How do I represent a number compactly?
```

But it does not answer:

```text
Where does the number come from?
How do I guarantee uniqueness?
How do I avoid collisions?
Can users guess the next code?
Can multiple app pods generate IDs safely?
Can this scale beyond one database?
```

Those questions belong to **ID generation strategy**.

A URL shortener needs short codes:

```text
aB91xZ
k9Lm2Q
4c92
mohamed
```

These codes can come from different strategies:

```text
1. Database auto-increment ID encoded with Base62.
2. Random Base62 characters.
3. UUID-derived token.
4. NanoID-style secure random token.
5. Snowflake-like distributed ID encoded with Base62.
6. Hash of long URL.
```

Each strategy has tradeoffs.

There is no universal best strategy.

A senior engineer chooses based on:

```text
traffic scale
collision tolerance
guessability
simplicity
operational complexity
database dependency
distributed deployment
security requirements
interview constraints
```

This chapter teaches how to think, not just what to code.

---

## 2. The One Core Mental Model

ID generation is a:

```text
UNIQUENESS SOURCE DECISION
```

Base62 is representation.

ID generation is identity creation.

ASCII:

```text
                 SHORT CODE CREATION

+-------------------------+
| ID generation strategy  |
| "Where uniqueness comes |
| from"                   |
+-------------------------+
            |
            v
+-------------------------+
| Raw ID / token           |
| 1000000 or random chars |
+-------------------------+
            |
            v
+-------------------------+
| Optional Base62 encode  |
| compact representation  |
+-------------------------+
            |
            v
+-------------------------+
| shortCode               |
| 4c92 / aB91xZ           |
+-------------------------+
            |
            v
+-------------------------+
| DB UNIQUE(short_code)   |
| final safety net        |
+-------------------------+
```

One-line memory:

```text
ID generation chooses the token; Base62 formats it; the database proves uniqueness.
```

The final invariant remains:

```text
short_code must be unique
```

No matter which strategy is used.

---

## 3. Problem Statement

We need to choose and understand short code generation strategies for MiniURLShortener.

A good strategy should answer:

```text
1. Does it generate unique codes?
2. Can collisions happen?
3. How are collisions handled?
4. Are codes guessable?
5. Is it simple to implement?
6. Does it scale across multiple app pods?
7. Does it depend on the database?
8. Does it work with sharding later?
9. Does it produce short enough URLs?
10. Is it easy to explain in interviews?
```

For version 1, we want:

```text
simple
correct
production-shaped
not overengineered
easy to evolve
```

For high-scale design, we want to know:

```text
when simple strategy stops being enough
how distributed IDs work
how sharding affects IDs
how security changes the choice
```

---

## 4. What ID Generation Must Guarantee

ID generation itself may or may not fully guarantee uniqueness.

But the system must guarantee:

```text
1. No two active mappings share the same short_code.
2. Collision handling is deterministic.
3. Custom alias conflict returns 409.
4. Generated collision retries safely.
5. Code length is controlled.
6. Code uses URL-safe characters.
7. Code generation does not become a bottleneck.
```

The database must still enforce:

```sql
UNIQUE(short_code)
```

Why?

Because distributed systems have races.

ASCII race:

```text
+----------+     generates abc123      +--------------------+
| App Pod1 | ------------------------> | Postgres           |
+----------+                           | UNIQUE(short_code) |
                                       +--------------------+
+----------+     generates abc123                |
| App Pod2 | -------------------------- duplicate rejected
+----------+
```

Even if collision probability is tiny, the failure must be safe.

Rule:

```text
Probability reduces risk.
Constraints eliminate corruption.
```

---

## 5. Base62 vs ID Generation

Base62 and ID generation are often confused.

They are different.

```text
ID generation:
    creates an identity

Base62:
    represents a number compactly
```

Example 1:

```text
DB generates id = 1000000
Base62 encodes it as 4c92
shortCode = 4c92
```

Example 2:

```text
Random generator directly creates aB91xZ
No numeric ID needed
shortCode = aB91xZ
```

Example 3:

```text
Snowflake generator creates 739201928374982
Base62 encodes it as E9xK2mQ
shortCode = E9xK2mQ
```

ASCII:

```text
Strategy A: numeric ID strategy

DB/Snowflake ID
      |
      v
Base62 encode
      |
      v
shortCode


Strategy B: random token strategy

Random characters
      |
      v
shortCode
```

Base62 does not guarantee uniqueness.

The input source must be unique, or collision must be handled.

---

## 6. Strategy Map

Six major strategies:

```text
1. DB auto-increment ID + Base62
2. Random Base62 code
3. UUID-based code
4. NanoID-style random token
5. Snowflake-like distributed ID
6. Hash-based code
```

ASCII map:

```text
                         ID GENERATION STRATEGIES

               +-----------------------------------+
               | Need shortCode                    |
               +-----------------------------------+
                            |
        +-------------------+-------------------+
        |                                       |
        v                                       v
  Numeric ID source                       Direct string token
        |                                       |
        v                                       v
+------------------+                 +----------------------+
| DB sequence      |                 | Random Base62        |
| Snowflake        |                 | UUID-derived         |
+------------------+                 | NanoID-style         |
        |                            | Hash-based           |
        v                            +----------------------+
+------------------+
| Base62 encode    |
+------------------+
        |
        v
+------------------+
| shortCode        |
+------------------+
```

Each strategy answers a different engineering priority.

```text
DB sequence:
    simplest and collision-free but guessable

Random Base62:
    simple and less guessable but collisions possible

UUID:
    globally unique but long unless truncated

NanoID:
    compact random secure token

Snowflake:
    distributed, sortable, scalable but more complex

Hash:
    deterministic but collision and privacy concerns
```

---

## 7. Strategy 1: Database Auto-Increment ID + Base62

Mental model:

```text
Let the database create a unique numeric ID.
Then encode that ID using Base62.
```

Flow:

```text
1. Insert row.
2. Database assigns id.
3. Convert id to Base62.
4. Store/use shortCode.
```

ASCII:

```text
Long URL
   |
   v
+----------------------+
| INSERT into DB       |
+----------------------+
   |
   v
+----------------------+
| DB generates id=1000 |
+----------------------+
   |
   v
+----------------------+
| Base62.encode(1000)  |
+----------------------+
   |
   v
+----------------------+
| shortCode = g8       |
+----------------------+
```

Pros:

```text
1. Very simple.
2. No generated-code collision if DB ID is unique.
3. Short codes grow naturally.
4. Easy to explain.
5. Strong for v1 and interviews.
```

Cons:

```text
1. Codes are guessable.
2. Reveals creation order.
3. Can reveal business growth.
4. Harder if multiple DB shards generate overlapping IDs.
5. May require insert then update.
```

Example sequence:

```text
id 1  -> 1
id 2  -> 2
id 3  -> 3
id 62 -> 10
id 63 -> 11
```

Attacker can guess:

```text
/1
/2
/3
/4
```

Best use:

```text
1. Internal tools.
2. Public non-sensitive short URLs.
3. Interview v1.
4. Systems where simplicity matters more than guessability.
```

Implementation challenge:

```text
How to store shortCode if it depends on DB id?
```

Options:

```text
Option A:
insert row with temporary code, get id, update shortCode

Option B:
use DB sequence first, encode id, then insert row with shortCode

Option C:
use application-managed sequence service
```

For Spring Boot v1, Option B can be clean if you call a sequence manually.

---

## 8. Strategy 2: Random Base62 Code

Mental model:

```text
Generate random characters from Base62 alphabet.
Use DB UNIQUE constraint to catch collisions.
Retry if needed.
```

Flow:

```text
1. Generate 7-char random Base62 code.
2. Insert into DB.
3. If unique, success.
4. If duplicate, retry.
```

ASCII:

```text
+-------------------------+
| SecureRandom            |
+-------------------------+
          |
          v
+-------------------------+
| k9Lm2Qx                 |
+-------------------------+
          |
          v
+-------------------------+
| INSERT short_urls       |
+-------------------------+
          |
          v
+------------------------------+
| UNIQUE(short_code) passes?   |
+------------------------------+
       | yes              | no
       v                  v
    success             retry
```

Pros:

```text
1. Simple.
2. Less guessable than sequential IDs.
3. No DB sequence dependency.
4. Works across multiple app pods.
5. Easy to generate before insert.
```

Cons:

```text
1. Collision possible.
2. Needs retry logic.
3. Collision probability grows as code space fills.
4. Randomness quality matters.
```

Best use:

```text
1. Public URL shortener v1/v2.
2. Less guessable links.
3. Multi-pod app where DB uniqueness catches collisions.
```

Typical length:

```text
7 or 8 chars
```

Why?

```text
62^7 ≈ 3.5 trillion
62^8 ≈ 218 trillion
```

For most systems, 7-8 chars is enough.

Important:

```text
Use SecureRandom or a strong random generator.
Do not use Math.random for production tokens.
```

---

## 9. Strategy 3: UUID-Based Code

Mental model:

```text
Generate UUID, then convert or truncate it into a shorter code.
```

UUID example:

```text
550e8400-e29b-41d4-a716-446655440000
```

Direct UUID is too long for a short URL.

You may:

```text
1. Use full UUID for internal ID.
2. Encode UUID bytes in Base62/Base64-like format.
3. Truncate part of UUID, but then collision risk appears.
```

ASCII:

```text
UUID
  |
  v
550e8400-e29b-41d4-a716-446655440000
  |
  v
take bytes / encode / truncate
  |
  v
shortCode candidate
```

Pros:

```text
1. Globally unique if full UUID used.
2. No central coordinator.
3. Easy in Java.
```

Cons:

```text
1. Full UUID is long.
2. Truncation reintroduces collision risk.
3. Less human-friendly.
4. Not always shortest.
```

Bad approach:

```text
UUID.randomUUID().toString().substring(0, 7)
```

Why?

```text
You are no longer using full UUID uniqueness.
You are using a 7-char truncated random token.
Collision is possible and must be handled.
```

If truncating UUID:

```text
still use UNIQUE(short_code)
still retry on collision
```

Best use:

```text
Internal IDs, distributed systems, not necessarily short URL code directly.
```

---

## 10. Strategy 4: NanoID-Style Random Token

Mental model:

```text
Generate a compact random URL-safe token using a configurable alphabet and length.
```

NanoID-style approach:

```text
alphabet = URL-safe characters
length = 7, 8, 10, etc.
randomly choose chars securely
```

This is similar to random Base62, but often with careful randomness and flexible alphabets.

ASCII:

```text
Alphabet:
0-9 a-z A-Z

Secure random bytes
      |
      v
map to alphabet indices
      |
      v
shortCode = V8xQ2Lm
```

Pros:

```text
1. Compact.
2. Less guessable.
3. No central coordinator.
4. Works across app pods.
5. Length configurable.
```

Cons:

```text
1. Collision possible.
2. Needs retry or large enough space.
3. Requires careful implementation/library choice.
```

Best use:

```text
Modern public token generation.
```

For this project:

```text
Random Base62 generator is enough to learn the same core idea.
```

Senior note:

```text
Random token generation is often preferred for public short links when guessability matters.
```

---

## 11. Strategy 5: Snowflake-Like Distributed ID

Mental model:

```text
Create a unique numeric ID using timestamp + worker ID + sequence.
Then encode it with Base62.
```

Classic layout idea:

```text
timestamp bits | worker id bits | sequence bits
```

ASCII bit layout:

```text
+----------------------+------------+--------------+
| timestamp            | workerId   | sequence     |
| time component       | machine id | per-ms count |
+----------------------+------------+--------------+
```

Example:

```text
timestamp = current milliseconds since custom epoch
workerId  = app/node/shard id
sequence  = counter within same millisecond
```

Flow:

```text
1. Read current timestamp.
2. Check worker ID.
3. Increment sequence for same millisecond.
4. Compose long number using bit shifting.
5. Base62 encode.
```

ASCII:

```text
App Pod with workerId=17

current time
    |
    v
timestamp part
    |
    +---- workerId=17
    |
    +---- sequence=42
    |
    v
unique 64-bit ID
    |
    v
Base62 encode
    |
    v
shortCode
```

Pros:

```text
1. Distributed generation.
2. No DB sequence bottleneck.
3. Sortable by time.
4. Very high throughput.
5. Numeric ID can be Base62 encoded.
```

Cons:

```text
1. More complex.
2. Requires unique worker IDs.
3. Clock rollback can break uniqueness/order.
4. Operational coordination needed.
5. Overkill for v1.
```

Best use:

```text
High-scale distributed systems.
Multiple data centers/pods needing IDs without DB round trip.
```

Failure concern:

```text
If two workers use same workerId, duplicates are possible.
```

Clock concern:

```text
If system clock moves backward, timestamp ordering breaks.
```

---

## 12. Strategy 6: Hash-Based Code

Mental model:

```text
Hash the long URL and use part of the hash as the short code.
```

Flow:

```text
longUrl
  |
  v
hash(longUrl)
  |
  v
take first N chars / encode
  |
  v
shortCode
```

ASCII:

```text
https://example.com/article
        |
        v
SHA-256 hash
        |
        v
a8f93b72c...
        |
        v
a8f93b
```

Pros:

```text
1. Same long URL can produce same code.
2. Deterministic.
3. No sequence needed.
```

Cons:

```text
1. Collisions possible if truncated.
2. Same URL always maps same way, which may leak behavior.
3. Custom aliases still separate.
4. Harder to handle multiple users wanting separate links to same URL.
5. Hash codes may be longer if collision risk is controlled.
```

Important:

```text
Full cryptographic hash is too long for short URL.
Truncation means collision risk.
```

Collision example:

```text
URL A -> abc123
URL B -> abc123
```

Still need:

```text
UNIQUE(short_code)
collision handling
possibly salt
```

Best use:

```text
deduplication scenarios
content-addressed systems
not usually first choice for public shorteners
```

For MiniURLShortener:

```text
Do not use hash-based code for v1.
```

---

## 13. Strategy Comparison Table

```text
+----------------------+-------------+-------------+-------------+----------------+----------------+
| Strategy             | Collision?  | Guessable?  | Complexity  | Distributed?   | Best For       |
+----------------------+-------------+-------------+-------------+----------------+----------------+
| DB ID + Base62       | No*         | Yes         | Low         | DB-centered    | Simple v1      |
| Random Base62        | Yes         | Lower       | Low-Medium  | Yes            | Public v1/v2   |
| UUID full            | Very low    | Low         | Low         | Yes            | Internal IDs   |
| UUID truncated       | Yes         | Low         | Low         | Yes            | If retried     |
| NanoID-style         | Yes         | Low         | Medium      | Yes            | Public tokens  |
| Snowflake + Base62   | No**        | Somewhat    | High        | Yes            | High scale     |
| Hash-based           | Yes         | Determin.   | Medium      | Yes            | Dedup/content  |
+----------------------+-------------+-------------+-------------+----------------+----------------+
```

Notes:

```text
No*:
    DB ID is unique if generated by one source of truth.

No**:
    Snowflake is unique if worker IDs and clocks are managed correctly.
```

Decision summary:

```text
Need easiest correct version?
    DB ID + Base62 or Random Base62.

Need less guessable public links?
    Random Base62 / NanoID-style.

Need high-scale distributed numeric IDs?
    Snowflake-like.

Need deterministic same URL same code?
    Hash-based, but be careful.
```

---

## 14. Collision Mental Model

Collision means:

```text
two generation attempts produce the same shortCode
```

Collision example:

```text
Attempt A -> k9Lm2Q
Attempt B -> k9Lm2Q
```

Different strategies have different collision behavior.

```text
DB sequence:
    no collision if one DB sequence

Random Base62:
    possible

UUID truncated:
    possible

Snowflake:
    no collision if worker IDs/clocks correct

Hash truncated:
    possible
```

ASCII collision handling:

```text
Generate code
     |
     v
Try INSERT
     |
     v
+----------------------+
| UNIQUE passes?       |
+----------------------+
   | yes           | no
   v               v
success       generated or custom?
                  |
          +-------+-------+
          |               |
          v               v
       generated        custom
          |               |
          v               v
        retry           409
```

Core rule:

```text
All strategies still need DB unique constraint unless uniqueness is mathematically and operationally guaranteed.
```

Even then, DB constraint is a safety net.

---

## 15. Guessability Mental Model

Guessability means:

```text
Can someone predict or enumerate other short codes?
```

Sequential DB ID + Base62:

```text
1, 2, 3, 4, 5
```

Base62:

```text
1, 2, 3, 4, 5 ... Z, 10, 11
```

Still guessable.

Random token:

```text
k9Lm2Qx
V8aP10z
0Xb91Lm
```

Much less guessable.

ASCII:

```text
Sequential:

/1  -> exists
/2  -> exists
/3  -> exists
/4  -> exists

Random:

/k9Lm2Qx -> maybe
/V8aP10z -> maybe
/0Xb91Lm -> maybe
```

If links are public marketing links:

```text
guessability may be acceptable
```

If links are private documents, invoices, reset-like resources:

```text
guessability is dangerous
```

Security rule:

```text
Do not use short code secrecy as authorization.
```

Private content needs:

```text
authentication
authorization
expiration
rate limiting
audit logs
```

---

## 16. Ordering And Sortability

Some IDs preserve time order.

DB auto-increment:

```text
id 100 created before id 101
```

Snowflake:

```text
timestamp part makes IDs roughly sortable by creation time
```

Random Base62:

```text
no natural ordering
```

Hash-based:

```text
no creation ordering
```

Why ordering matters:

```text
1. Pagination/admin UI
2. Debugging
3. Data partitioning
4. Time-based analytics
5. Storage locality
```

But ordering also leaks:

```text
system growth
creation rate
business activity
```

Tradeoff:

```text
Sortable IDs are operationally useful but more guessable.
Random IDs are less guessable but less ordered.
```

Senior decision:

```text
Use internal created_at/id for ordering.
Use random public shortCode if public guessability matters.
```

This gives both:

```text
internal order
public unpredictability
```

---

## 17. Sharding Readiness

At very high scale, data may be sharded.

Question:

```text
Can shortCode help route to the right shard?
```

Strategies:

### DB ID + Base62

If one central DB sequence:

```text
simple but central bottleneck at massive scale
```

If each shard has its own sequence:

```text
shard 1 id=1
shard 2 id=1
```

Potential duplicate if encoded directly.

Need:

```text
shard ID included
global sequence
range allocation
Snowflake-like ID
```

### Random Base62

Shard by hash of shortCode:

```text
shard = hash(shortCode) % N
```

Pros:

```text
even distribution
simple routing
```

Cons:

```text
resizing shards needs consistent hashing
```

### Snowflake

ID can include worker/shard bits.

```text
timestamp | shard/worker | sequence
```

Good for distributed uniqueness.

ASCII sharding:

```text
shortCode
   |
   v
hash(shortCode)
   |
   v
+-------------------+
| shard = hash % N  |
+-------------------+
   |
   +-- 0 -> DB shard 0
   +-- 1 -> DB shard 1
   +-- 2 -> DB shard 2
```

For MiniURLShortener v1:

```text
Do not shard.
Design shortCode and schema so sharding later is possible.
```

---

## 18. Recommended Strategy For MiniURLShortener

For this project, recommended learning path:

```text
Phase 1:
Random Base62 code + DB UNIQUE + retry

Phase 2:
Learn DB ID + Base62 as alternative

Phase 3:
Understand Snowflake for high-scale interview

Phase 4:
Use sharding by shortCode hash later
```

Why Random Base62 for implementation?

```text
1. Easy to create before DB insert.
2. Less guessable than sequential IDs.
3. Works across multiple app pods.
4. Teaches collision handling.
5. Fits current schema.
```

Recommended v1 settings:

```text
alphabet = 0-9 a-z A-Z
length = 7
generator = SecureRandom
DB constraint = UNIQUE(short_code)
retry attempts = 5
```

ASCII final v1:

```text
POST longUrl
    |
    v
Generate random 7-char Base62
    |
    v
INSERT short_urls
    |
    v
UNIQUE passes?
    |
    +-- yes -> return shortUrl
    |
    +-- no  -> retry new code
```

Interview note:

```text
You can say: For v1 I choose random Base62 with DB uniqueness. At higher scale I can move to Snowflake-like IDs or shard-aware ID generation.
```

This shows practical judgment.

---

## 19. Java Implementation: Random Base62 Generator

```java
package com.miniurl.shortener.url.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {

    private static final String ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int CODE_LENGTH = 7;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        StringBuilder builder = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = secureRandom.nextInt(ALPHABET.length());
            builder.append(ALPHABET.charAt(index));
        }

        return builder.toString();
    }
}
```

Why SecureRandom?

```text
Better randomness for public tokens.
```

Why 7 chars?

```text
62^7 ≈ 3.5 trillion possible codes.
```

But still:

```text
Use DB UNIQUE.
Retry on duplicate.
```

Service usage:

```java
for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
    String code = shortCodeGenerator.generate();

    try {
        save(code);
        return response;
    } catch (DataIntegrityViolationException duplicate) {
        // retry generated collision
    }
}

throw new ShortCodeGenerationFailedException("Could not generate unique code");
```

---

## 20. Java Implementation: DB ID + Base62

Alternative approach:

```text
Use database sequence to get numeric ID first.
Encode it with Base62.
Use that as shortCode.
```

SQL sequence:

```sql
CREATE SEQUENCE short_url_code_seq START WITH 1 INCREMENT BY 1;
```

Repository for sequence:

```java
package com.miniurl.shortener.url.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ShortCodeSequenceRepository {

    private final JdbcTemplate jdbcTemplate;

    public ShortCodeSequenceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long nextId() {
        Long value = jdbcTemplate.queryForObject(
                "SELECT nextval('short_url_code_seq')",
                Long.class
        );

        if (value == null) {
            throw new IllegalStateException("Could not get next sequence value");
        }

        return value;
    }
}
```

Use Base62:

```java
long id = sequenceRepository.nextId();
String shortCode = base62Encoder.encode(id);
```

Flow:

```text
get sequence id
    |
    v
Base62 encode
    |
    v
insert row with shortCode
```

Pros:

```text
No random collision.
```

Cons:

```text
Guessable.
Extra sequence dependency.
```

This is excellent for interviews because it is simple and deterministic.

---

## 21. Java Implementation: Snowflake-Like Generator

A simplified educational version:

```java
package com.miniurl.shortener.url.service;

import org.springframework.stereotype.Component;

@Component
public class SnowflakeLikeIdGenerator {

    private static final long CUSTOM_EPOCH = 1704067200000L; // 2024-01-01 UTC

    private static final long WORKER_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = (1L << WORKER_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    private final long workerId;

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeLikeIdGenerator() {
        this.workerId = 1L; // In production, inject unique worker ID from config.
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("Invalid workerId");
        }
    }

    public synchronized long nextId() {
        long currentTimestamp = currentTimeMillis();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;

            if (sequence == 0) {
                currentTimestamp = waitUntilNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - CUSTOM_EPOCH) << TIMESTAMP_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    private long waitUntilNextMillis(long currentTimestamp) {
        while (currentTimestamp <= lastTimestamp) {
            currentTimestamp = currentTimeMillis();
        }
        return currentTimestamp;
    }

    private long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
```

Then:

```java
long id = snowflakeLikeIdGenerator.nextId();
String shortCode = base62Encoder.encode(id);
```

Production warning:

```text
This is educational.
Real production needs worker ID coordination, clock monitoring, metrics, and failure policy.
```

Snowflake mental model:

```text
timestamp gives time uniqueness
workerId gives node uniqueness
sequence gives same-millisecond uniqueness
```

ASCII:

```text
+----------------------+------------+--------------+
| timestamp            | workerId   | sequence     |
+----------------------+------------+--------------+
```

---

## 22. Step-by-Step Dry Runs

### Dry Run 1: DB ID + Base62

Input:

```text
longUrl = https://example.com/article
```

Steps:

```text
1. DB sequence returns id = 1000000.
2. Base62.encode(1000000) = 4c92.
3. shortCode = 4c92.
4. Insert row:
   short_code = 4c92
   long_url = https://example.com/article
5. Return https://mini.ly/4c92.
```

Diagram:

```text
DB sequence
    |
    v
1000000
    |
    v
Base62
    |
    v
4c92
```

---

### Dry Run 2: Random Base62 success

Steps:

```text
1. Generator chooses chars randomly.
2. Code = k9Lm2Qx.
3. App inserts row.
4. DB UNIQUE passes.
5. Return short URL.
```

State:

```text
k9Lm2Qx -> https://example.com/article
```

---

### Dry Run 3: Random Base62 collision

Existing:

```text
aB91xZ2 -> https://old.com
```

New request:

```text
longUrl = https://new.com
```

Steps:

```text
1. Generator returns aB91xZ2.
2. Insert fails due to UNIQUE.
3. App retries.
4. Generator returns P8xLm02.
5. Insert succeeds.
6. Return P8xLm02.
```

Key:

```text
Generated collision is internal and retryable.
```

---

### Dry Run 4: Custom alias conflict

Existing:

```text
mohamed -> https://profile.com
```

User requests:

```text
customAlias = mohamed
```

Steps:

```text
1. App does not generate random code.
2. App tries to insert mohamed.
3. DB UNIQUE fails.
4. App returns 409 Conflict.
```

Key:

```text
Custom alias conflict is user-visible.
```

---

### Dry Run 5: Snowflake-like ID

Given:

```text
timestamp part = 123456
workerId = 17
sequence = 3
```

Combined:

```text
timestamp | workerId | sequence
```

Produces:

```text
large numeric ID
```

Then:

```text
Base62.encode(id) -> compact code
```

Flow:

```text
time + worker + sequence
        |
        v
unique long number
        |
        v
Base62 code
```

---

## 23. Production Failure Stories

### Failure Story 1: Two workers use same Snowflake worker ID

Two app pods both configured:

```text
workerId = 7
```

They generate IDs in same millisecond with same sequence.

Result:

```text
duplicate IDs
duplicate short codes
DB unique violations
possible outage if not handled
```

Root cause:

```text
worker ID coordination failure.
```

Fix:

```text
Assign worker IDs through config, service discovery, Kubernetes StatefulSet ordinal, or central coordinator.
Monitor duplicate failures.
```

Lesson:

```text
Distributed uniqueness depends on operational uniqueness too.
```

---

### Failure Story 2: Sequential IDs are scraped

System uses:

```text
id 1 -> /1
id 2 -> /2
id 3 -> /3
```

Bot scans:

```text
GET /1
GET /2
GET /3
...
```

Result:

```text
Links discovered.
Abuse traffic increases.
Analytics polluted.
```

Fix:

```text
Use random codes, rate limiting, abuse detection, or authorization for private links.
```

Lesson:

```text
Base62 is not security.
```

---

### Failure Story 3: Random code length too short

Team uses 4-character random Base62.

Capacity:

```text
62^4 = 14.7 million
```

After many links, collisions increase.

Result:

```text
more retries
slower creation
eventual generation failures
```

Fix:

```text
Increase length to 7 or 8.
Monitor collision count.
```

Lesson:

```text
Capacity planning matters even for random strings.
```

---

### Failure Story 4: Infinite retry loop

Random generator has bug and always returns same code.

Service does:

```text
while true retry
```

Result:

```text
request hangs
threads blocked
CPU waste
```

Fix:

```text
limit retry count
fail clearly
alert on collision spikes
```

Lesson:

```text
Every retry loop needs a limit.
```

---

### Failure Story 5: Hash truncation causes wrong redirect

Two different long URLs produce same truncated hash code.

Without proper collision handling, system maps incorrectly.

Result:

```text
User clicks link and lands on wrong URL.
```

Fix:

```text
DB UNIQUE, collision resolution, or avoid hash truncation strategy.
```

Lesson:

```text
Hashing does not remove collision risk when truncated.
```

---

## 24. Debugging Mindset

When ID generation fails, ask:

```text
Which strategy is used?
Is shortCode generated before DB insert or after DB ID?
Is DB unique constraint firing?
Is this a generated collision or custom alias conflict?
Is retry count exhausted?
Is random generator healthy?
Is code length too short?
Are multiple pods using same worker ID?
Did clock move backward?
Is alphabet consistent?
```

Debug flow:

```text
Create API fails
      |
      v
Check error
      |
      +-- 409 -> custom alias conflict
      |
      +-- duplicate + generated -> retry path issue
      |
      +-- retry exhausted -> generator/capacity issue
      |
      +-- Snowflake clock error -> system time issue
      |
      +-- DB sequence error -> database/permission issue
```

Useful metrics:

```text
short_code.generated.count
short_code.collision.count
short_code.retry.count
short_code.generation.failure.count
custom_alias.conflict.count
snowflake.clock_rollback.count
```

Useful logs:

```text
strategy=random_base62
attempt=1
result=duplicate
codeLength=7
```

Do not log too much:

```text
Generated code is public enough, but logs can still be abused.
Avoid logging sensitive long URLs.
```

---

## 25. Common Mistakes

### Mistake 1: Confusing Base62 with ID generation

Wrong:

```text
Base62 generates unique IDs.
```

Correct:

```text
Base62 formats IDs.
A strategy generates IDs.
```

### Mistake 2: Removing DB unique constraint

Wrong:

```text
Random collision is unlikely, so no unique constraint needed.
```

Correct:

```text
UNIQUE(short_code) is mandatory.
```

### Mistake 3: Using short random length

Wrong:

```text
4 chars for large public system.
```

Correct:

```text
7-8 chars for healthy capacity.
```

### Mistake 4: Retrying custom aliases

Wrong:

```text
custom alias taken -> generate another code
```

Correct:

```text
custom alias taken -> 409 Conflict
```

### Mistake 5: Infinite generated retry

Wrong:

```text
retry forever
```

Correct:

```text
retry limited times
```

### Mistake 6: Assuming Snowflake is easy

Wrong:

```text
Just use timestamp and worker ID.
```

Correct:

```text
Need worker coordination, clock rollback handling, monitoring.
```

### Mistake 7: Using sequential IDs for private data

Wrong:

```text
/1, /2, /3 are fine for private links.
```

Correct:

```text
Private links need unguessable tokens and authorization.
```

---

## 26. Interview-Ready Explanation

If interviewer asks:

```text
How would you generate short codes for a URL shortener?
```

Strong answer:

```text
There are multiple strategies. The simplest is to use a database auto-increment ID
and encode it with Base62. That gives compact, collision-free codes as long as the
database ID is unique, but the codes are sequential and guessable. Another practical
option is to generate a random Base62 token of length 7 or 8 using a secure random
generator, insert it with a UNIQUE constraint on short_code, and retry on duplicate.
This is less guessable and works across multiple app pods, but collisions are
possible and must be handled. At very high scale, I can use a Snowflake-like
distributed ID made from timestamp, worker ID, and sequence, then Base62 encode it.
That avoids a central DB sequence but requires worker ID coordination and clock
rollback handling. For my v1, I would choose random Base62 with DB uniqueness and
limited retries because it is simple, less guessable, and production-shaped.
```

Why this is strong:

```text
1. Shows multiple strategies.
2. Explains tradeoffs.
3. Mentions Base62 correctly.
4. Mentions uniqueness constraint.
5. Handles collision.
6. Discusses guessability.
7. Discusses distributed scale.
8. Gives a practical recommendation.
```

Senior one-liner:

```text
ID generation is a tradeoff between simplicity, uniqueness source, guessability, distribution, and operational complexity.
```

---

## 27. Senior Engineer Checklist

Before moving to error handling chapter, confirm:

```text
[ ] You know Base62 is representation, not generation.
[ ] You can explain DB ID + Base62.
[ ] You can explain random Base62.
[ ] You can explain UUID truncation risk.
[ ] You can explain NanoID-style tokens.
[ ] You can explain Snowflake-like IDs.
[ ] You can explain hash-based collision risk.
[ ] You know generated collision should retry.
[ ] You know custom alias conflict should return 409.
[ ] You know DB UNIQUE(short_code) is mandatory.
[ ] You understand guessability.
[ ] You understand code length capacity.
[ ] You know retry loops need limits.
[ ] You know Snowflake needs worker coordination.
[ ] You can justify the v1 strategy.
```

If all are checked, ID generation strategy is clear.

---

## 28. One-Page Cheat Sheet

```text
ID generation:
chooses unique token source

Base62:
formats numeric ID compactly

Main strategies:
1. DB ID + Base62
2. Random Base62
3. UUID-based
4. NanoID-style
5. Snowflake-like
6. Hash-based

DB ID + Base62:
simple
no collision if DB ID unique
guessable

Random Base62:
less guessable
collision possible
needs DB UNIQUE + retry

UUID:
globally unique if full
too long if raw
truncation causes collision risk

NanoID-style:
compact random token
good for public links
collision still possible

Snowflake:
timestamp + workerId + sequence
distributed
sortable
complex

Hash:
deterministic
collision when truncated
not ideal for v1

Recommended v1:
Random Base62 length 7
SecureRandom
UNIQUE(short_code)
retry max 5

Generated collision:
retry

Custom alias collision:
409 Conflict

Security:
Base62 is not encryption.
Sequential IDs are guessable.

Production:
measure collision count
limit retries
do not remove DB constraint
```

---

## 29. One Picture To Remember

```text
                    ID GENERATION STRATEGY MENTAL MODEL

                 "Where does the short code come from?"

                         +------------------+
                         | Need shortCode   |
                         +------------------+
                                  |
              +-------------------+-------------------+
              |                                       |
              v                                       v
    +---------------------+                 +----------------------+
    | Numeric ID source   |                 | Direct token source  |
    +---------------------+                 +----------------------+
              |                                       |
      +-------+-------+                       +-------+---------+
      |               |                       |                 |
      v               v                       v                 v
+-----------+   +-------------+        +-------------+     +-------------+
| DB seq ID |   | Snowflake   |        | Random      |     | UUID/NanoID |
+-----------+   +-------------+        +-------------+     +-------------+
      |               |                       |                 |
      v               v                       v                 v
+-----------------------------+        +-------------------------------+
| Base62 encode numeric ID    |        | Already short-code candidate |
+-----------------------------+        +-------------------------------+
              |                                       |
              +-------------------+-------------------+
                                  |
                                  v
                         +------------------+
                         | shortCode        |
                         +------------------+
                                  |
                                  v
                         +------------------+
                         | INSERT DB        |
                         | UNIQUE enforced  |
                         +------------------+
                                  |
                       +----------+----------+
                       |                     |
                       v                     v
                    success               duplicate
                       |                     |
                       v                     v
                  return URL       generated retry / custom 409


FINAL MEMORY:

Base62 answers:
"How do I write the ID compactly?"

ID generation answers:
"Where does the unique ID/token come from?"

Postgres answers:
"Is this shortCode truly unique?"
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. ID generation decides uniqueness; Base62 only formats numeric IDs.
2. DB ID + Base62 is simple and collision-free but guessable.
3. Random Base62 is less guessable but needs UNIQUE constraint and retry.
4. Snowflake-like IDs scale across nodes but require worker and clock discipline.
5. For MiniURLShortener v1, random Base62 + DB UNIQUE + limited retry is the best practical choice.
```

After this chapter, the next natural step is:

```text
009_Error_Handling_Validation.md
```

Because create and redirect APIs now exist, and the next production skill is making all failures predictable, clean, and debuggable.
