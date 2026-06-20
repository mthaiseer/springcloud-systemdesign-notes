# 09 — Design a URL Shortener Step-by-Step with Offline KGS

> Goal: build a TinyURL-like service step by step, then combine everything into one final FAANG-level solution.

---

# Step 0 — Clarify the Problem

## What are we building?

A URL shortener converts a long URL into a short URL.

Example:

```text
Long URL:
https://en.wikipedia.org/wiki/Systems_design

Short URL:
https://tinyurl.com/a8Kz91Q
```

When a user opens the short URL, the system redirects to the original long URL.

---

## Functional Requirements

- Create a short URL from a long URL.
- Redirect short URL to original URL.
- Support optional custom alias.
- Support expiration time.
- Support analytics.
- Support REST APIs.

---

## Non-functional Requirements

- High availability.
- Low redirect latency.
- Scalable reads and writes.
- Fault tolerant.
- Short code should not be predictable.
- Abuse prevention.

---

# Step 1 — Estimate Scale

Assume:

```text
New URLs per month = 500M
Read/write ratio   = 100:1
Object size        = 500 bytes
Retention          = 5 years
```

## Writes

```text
500M / (30 * 24 * 3600) ≈ 200 writes/sec
```

## Reads

```text
200 * 100 = 20,000 redirects/sec
```

## Storage

```text
500M * 12 * 5 = 30B records

30B * 500 bytes = 15TB
```

## Cache

```text
20K redirects/sec * 3600 * 24 ≈ 1.7B redirects/day

Cache 20% hot traffic:
0.2 * 1.7B * 500 bytes ≈ 170GB
```

## Interview Line

> Since this is read-heavy, I will optimize redirect latency using cache.

---

# Step 2 — Define APIs

## Create Short URL

```http
POST /shorten
Content-Type: application/json

{
  "original_url": "https://en.wikipedia.org/wiki/Systems_design",
  "custom_alias": "system-design",
  "expiration_date": "2027-01-01T00:00:00Z",
  "user_id": "user123"
}
```

Response:

```json
{
  "shortened_url": "https://tinyurl.com/a8Kz91Q"
}
```

---

## Redirect API

```http
GET /a8Kz91Q
```

Response:

```http
302 Found
Location: https://en.wikipedia.org/wiki/Systems_design
```

---

## Analytics API

```http
GET /analytics/a8Kz91Q
```

Response:

```json
{
  "click_count": 12003,
  "unique_clicks": 8410
}
```

---

# Step 3 — Start with the Simplest Design

## Simple Architecture

```text
Client
  |
  v
Web Server
  |
  v
Database
```

## Simple Flow

```text
POST /shorten
    |
    v
Generate short code
    |
    v
Save shortCode -> longUrl
    |
    v
Return short URL
```

## Problem

This design is not enough because:

- one web server is a single point of failure,
- database can become bottleneck,
- no cache,
- runtime key generation may add latency,
- generated keys may collide.

---

# Step 4 — Choose Short Code Strategy

We need a short code like:

```text
a8Kz91Q
```

Allowed characters:

```text
0-9 = 10
a-z = 26
A-Z = 26
Total = 62
```

## Capacity

```text
62^6 ≈ 56.8B
62^7 ≈ 3.5T
62^8 ≈ 218T
```

Recommendation:

```text
Use 7 or 8 characters.
```

Why?

- 6 characters can work mathematically for 30B URLs.
- 7 or 8 gives safer growth.
- More space reduces collision risk for random keys.

---

# Step 5 — Compare Key Generation Approaches

| Approach | Pros | Cons |
|---|---|---|
| Hash long URL | Simple | Collision risk |
| Unique ID + Base62 | Fast | Sequential, guessable |
| Snowflake + Base62 | Distributed | Clock sync issue |
| Offline KGS | Fast, random, no hot-path collision | Extra component |

## Best Interview Choice

> Use Offline KGS for random non-guessable keys. Keep Snowflake + Base62 as fallback.

---

# Step 6 — Add Offline KGS

## What is Offline KGS?

KGS = Key Generation Service.

It pre-generates short keys before users request them.

```text
Offline KGS -> generate keys -> store unused keys
```

Then app servers simply lease a key.

```text
App Server -> get unused key -> assign to long URL
```

---

## KGS Visual

```text
+------------------+
| Offline KGS Job  |
+--------+---------+
         |
         v
+------------------+
| Generate random  |
| Base62 keys      |
+--------+---------+
         |
         v
+------------------+
| Store in key DB  |
| or Redis pool    |
+------------------+
```

---

## Runtime Shortening with KGS

```text
Client
  |
  v
App Server
  |
  v
Lease key from KGS pool
  |
  v
Save shortCode -> longUrl
  |
  v
Return short URL
```

---

## Why KGS Helps

- Removes key generation latency from request path.
- Avoids runtime collision checks.
- Makes short links random-looking.
- Prevents predictable sequential IDs.
- App servers become simpler.

---

# Step 7 — KGS Data Model

## Option A — Two Tables

```text
unused_keys

+----------+
| key      |
+----------+
| a8Kz91Q  |
| Xp02QaL  |
| mN72Ls9  |
+----------+
```

```text
used_keys

+----------+---------------------+
| key      | used_at             |
+----------+---------------------+
| p9Kla21  | 2026-04-26 10:00:00 |
+----------+---------------------+
```

---

## Option B — Redis Queue

```text
Redis List:
unused_keys = [a8Kz91Q, Xp02QaL, mN72Ls9]

Redis Set:
used_keys = {p9Kla21, kq82Lm0}
```

Use atomic pop:

```text
LPOP unused_keys
```

This prevents two app servers from getting the same key.

---

# Step 8 — Handle KGS Concurrency

## Problem

Multiple app servers may request keys at the same time.

Bad case:

```text
Server A reads key abc123
Server B reads key abc123
```

Both may assign the same key.

---

## Solution

Use atomic key leasing.

### Redis

```text
LPOP unused_keys
```

### SQL

```sql
BEGIN;

SELECT key
FROM unused_keys
LIMIT 1
FOR UPDATE SKIP LOCKED;

DELETE FROM unused_keys
WHERE key = ?;

INSERT INTO used_keys(key, used_at)
VALUES (?, now());

COMMIT;
```

Interview line:

> KGS must lease keys atomically so one key is assigned to only one URL.

---

# Step 9 — Add Custom Alias Support

If user provides a custom alias:

```text
https://tinyurl.com/my-blog
```

Do not use KGS.

Flow:

```text
Custom alias provided?
        |
        +-- yes -> validate alias -> check uniqueness -> save
        |
        +-- no  -> lease key from KGS -> save
```

## Alias Rules

- Max length: 16 characters.
- Allowed: letters, digits, `_`, `-`.
- Block reserved words:
  - `admin`
  - `login`
  - `api`
  - `analytics`
  - `settings`

---

# Step 10 — Database Design

## Main Table

```sql
CREATE TABLE url_mapping (
    id BIGINT PRIMARY KEY,
    short_code VARCHAR(16) UNIQUE NOT NULL,
    long_url TEXT NOT NULL,
    long_url_hash VARCHAR(64),
    user_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    expire_at TIMESTAMP NULL,
    is_private BOOLEAN DEFAULT FALSE,
    deleted BOOLEAN DEFAULT FALSE
);
```

## Indexes

```text
PRIMARY KEY(id)
UNIQUE(short_code)
INDEX(long_url_hash)
INDEX(user_id, created_at)
INDEX(expire_at)
```

---

## Visual

```text
url_mapping

+----+------------+-------------------------+------------+
| id | short_code | long_url                | expire_at  |
+----+------------+-------------------------+------------+
| 1  | a8Kz91Q    | https://example.com/... | 2027-01-01 |
+----+------------+-------------------------+------------+
```

---

# Step 11 — Add Cache for Redirects

Redirects are much more frequent than writes.

Use Redis or Memcached.

```text
Cache key   = shortCode
Cache value = longUrl + metadata
```

## Redirect Cache Flow

```text
GET /a8Kz91Q
     |
     v
Check Redis
     |
     +-- hit  -> return 302
     |
     +-- miss -> query DB -> update Redis -> return 302
```

---

## Visual

```text
Client
  |
  v
Web Server
  |
  v
Redis Cache
  |
  +-- hit --> Redirect
  |
  +-- miss
        |
        v
      Database
        |
        v
      Update Cache
        |
        v
      Redirect
```

---

# Step 12 — Redirect Status Code

| Code | Meaning | Use When |
|---|---|---|
| `301` | Permanent redirect | reduce load |
| `302` | Temporary redirect | track analytics |

Recommendation:

```text
Use 302 when analytics matter.
Use 301 when reducing server load matters.
```

Interview line:

> For most URL shorteners, I prefer 302 because analytics are usually important.

---

# Step 13 — Add Analytics Asynchronously

Do not update analytics inside the redirect critical path.

Bad:

```text
Redirect -> update analytics DB -> return response
```

Good:

```text
Redirect -> send event to queue -> return response
```

## Visual

```text
Client
  |
  v
URL Server
  |
  +-- return 302 immediately
  |
  v
Message Queue
  |
  v
Analytics Workers
  |
  v
Analytics DB
```

---

# Step 14 — Add Expiration and Cleanup

## Expiration check

During redirect:

```text
If link expired:
    return 404
Else:
    redirect
```

## Cleanup Service

```text
Cleanup Worker
   |
   v
Find expired links
   |
   v
Delete from DB/cache
   |
   v
Optionally recycle key
```

Important:

> Reusing expired keys is possible, but risky if old caches or analytics still reference the old mapping.

Safer answer:

```text
Do not immediately reuse keys.
```

---

# Step 15 — Add Abuse Prevention

## Common Abuse

- Creating too many links.
- Creating spam/phishing links.
- Brute-forcing short codes.
- Abusing custom aliases.

## Protection

```text
Rate limiter by IP/user
URL validation
Malware domain blocklist
CAPTCHA for suspicious clients
Reserved alias list
Auth for custom aliases
```

Return:

```http
429 Too Many Requests
```

---

# Step 16 — Scale the Database

## Replication

```text
              +-------------+
              | Primary DB  |
              +------+------+ 
                     |
        +------------+------------+
        |                         |
        v                         v
+---------------+         +---------------+
| Read Replica  |         | Read Replica  |
+---------------+         +---------------+
```

Use:

- primary for writes,
- replicas for reads.

---

## Sharding

Shard by `short_code`.

```text
shard = hash(shortCode) % numberOfShards
```

Visual:

```text
             +-------------+
             | Web Servers |
             +------+------+
                    |
      +-------------+-------------+
      |             |             |
      v             v             v
+----------+   +----------+   +----------+
| Shard 0  |   | Shard 1  |   | Shard 2  |
+----------+   +----------+   +----------+
```

Better:

> Use consistent hashing to reduce data movement when shards change.

---

# Step 17 — Make KGS Highly Available

KGS should not become a single point of failure.

## KGS HA Design

```text
             +-------------+
             | Primary KGS |
             +------+------+
                    |
                    v
             +-------------+
             | Standby KGS |
             +-------------+
```

## App Server Local Buffer

Each app server can cache some keys locally.

```text
KGS Pool -> App Server Local Key Buffer -> URL creation
```

Visual:

```text
+----------+       +------------------+
| KGS Pool | ----> | App Server       |
+----------+       | local key buffer |
                   +------------------+
```

If app server dies:

```text
Some unused keys are lost.
```

This is acceptable because key space is huge.

---

## KGS Failure Strategy

```text
If KGS is healthy:
    lease keys normally.

If KGS is down but local buffer has keys:
    continue writes.

If KGS is down and local buffer empty:
    fallback to Snowflake + Base62
    or temporarily reject new shortening requests.

Redirects still work because redirects do not need KGS.
```

---

# Step 18 — Final Big Solution

## Final Architecture

```text
                           +----------------+
                           |    Clients     |
                           +--------+-------+
                                    |
                                    v
                           +----------------+
                           | Load Balancer  |
                           +--------+-------+
                                    |
                     +--------------+--------------+
                     |                             |
                     v                             v
              +-------------+               +-------------+
              | Web Server  |               | Web Server  |
              +------+------+               +------+------+
                     |                             |
                     +--------------+--------------+
                                    |
        +---------------------------+---------------------------+
        |                           |                           |
        v                           v                           v
+---------------+          +----------------+          +----------------+
| Redis Cache   |          | URL DB         |          | KGS Key Pool   |
| short->long   |          | Sharded        |          | unused keys    |
+---------------+          +----------------+          +-------+--------+
                                                               ^
                                                               |
                                                        +------+------+
                                                        | Offline KGS |
                                                        | random keys |
                                                        +-------------+

Analytics:
Web Server -> Message Queue -> Analytics Workers -> Analytics DB

Cleanup:
Cleanup Worker -> URL DB + Redis Cache

Security:
Rate Limiter -> App Servers
```

---

## Final Shortening Flow

```text
POST /shorten
   |
   v
Validate URL
   |
   v
Check rate limit
   |
   v
Custom alias provided?
   |
   +-- yes -> validate alias -> check unique -> use alias
   |
   +-- no  -> lease random key from KGS pool
   |
   v
Save shortCode -> longUrl in DB
   |
   v
Warm Redis cache
   |
   v
Return short URL
```

---

## Final Redirect Flow

```text
GET /{shortCode}
   |
   v
Check Redis cache
   |
   +-- hit
   |     |
   |     v
   |   check expiry/permission
   |     |
   |     v
   |   return 302
   |
   +-- miss
         |
         v
       query DB
         |
         v
       if not found/expired -> 404
         |
         v
       update cache
         |
         v
       return 302

Async:
send click event to queue
```

---

# Step 19 — Java Code: Base62 Fallback

```java
public class Base62 {
    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = CHARS.length();

    public static String encode(long number) {
        if (number == 0) return "0";

        StringBuilder sb = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % BASE);
            sb.append(CHARS.charAt(remainder));
            number = number / BASE;
        }

        return sb.reverse().toString();
    }
}
```

---

# Step 20 — Java Code: Random Key Generator

```java
import java.security.SecureRandom;

public class RandomShortKeyGenerator {
    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }

        return sb.toString();
    }
}
```

---

# Step 21 — Java Code: Simple Offline KGS

```java
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class OfflineKgs {
    private final BlockingQueue<String> unusedKeys = new LinkedBlockingQueue<>();
    private final Set<String> generatedKeys = ConcurrentHashMap.newKeySet();

    public OfflineKgs(int initialCount, int keyLength) {
        refill(initialCount, keyLength);
    }

    public synchronized void refill(int count, int keyLength) {
        int added = 0;

        while (added < count) {
            String key = RandomShortKeyGenerator.generate(keyLength);

            if (generatedKeys.add(key)) {
                unusedKeys.offer(key);
                added++;
            }
        }
    }

    public String leaseKey() {
        String key = unusedKeys.poll();

        if (key == null) {
            throw new RuntimeException("KGS key pool exhausted");
        }

        return key;
    }

    public int availableKeys() {
        return unusedKeys.size();
    }
}
```

---

# Step 22 — Java Code: URL Shortener with KGS

```java
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UrlShortenerWithKgs {
    private static final String DOMAIN = "https://tinyurl.com/";

    private final OfflineKgs kgs = new OfflineKgs(10_000, 7);

    private final Map<String, UrlRecord> codeToRecord = new ConcurrentHashMap<>();

    public String shorten(String longUrl, String customAlias, Instant expireAt) {
        validateUrl(longUrl);

        String code;

        if (customAlias != null && !customAlias.isBlank()) {
            validateAlias(customAlias);

            if (codeToRecord.containsKey(customAlias)) {
                throw new RuntimeException("Custom alias already exists");
            }

            code = customAlias;
        } else {
            code = kgs.leaseKey();
        }

        UrlRecord record = new UrlRecord(code, longUrl, Instant.now(), expireAt);
        codeToRecord.put(code, record);

        return DOMAIN + code;
    }

    public String resolve(String code) {
        UrlRecord record = codeToRecord.get(code);

        if (record == null) {
            throw new RuntimeException("404 Not Found");
        }

        if (record.expireAt() != null && Instant.now().isAfter(record.expireAt())) {
            codeToRecord.remove(code);
            throw new RuntimeException("404 Link expired");
        }

        return record.longUrl();
    }

    private void validateUrl(String longUrl) {
        if (longUrl == null || longUrl.isBlank()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }

        if (!longUrl.startsWith("http://") && !longUrl.startsWith("https://")) {
            throw new IllegalArgumentException("Only HTTP/HTTPS URLs allowed");
        }
    }

    private void validateAlias(String alias) {
        if (alias.length() > 16) {
            throw new IllegalArgumentException("Alias too long");
        }

        if (!alias.matches("[0-9a-zA-Z_-]+")) {
            throw new IllegalArgumentException("Alias has invalid characters");
        }
    }

    record UrlRecord(
            String code,
            String longUrl,
            Instant createdAt,
            Instant expireAt
    ) {}

    public static void main(String[] args) {
        UrlShortenerWithKgs service = new UrlShortenerWithKgs();

        String shortUrl = service.shorten(
                "https://en.wikipedia.org/wiki/Systems_design",
                null,
                Instant.now().plusSeconds(3600)
        );

        System.out.println(shortUrl);

        String code = shortUrl.substring(shortUrl.lastIndexOf("/") + 1);
        System.out.println(service.resolve(code));
    }
}
```

---

# Step 23 — Java Code: Controller Style

```java
@RestController
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public Map<String, String> shorten(@RequestBody ShortenRequest request) {
        String shortUrl = urlService.shorten(
                request.originalUrl(),
                request.customAlias(),
                request.expirationDate()
        );

        return Map.of("shortened_url", shortUrl);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String longUrl = urlService.resolve(code);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, longUrl)
                .build();
    }
}
```

---

# Step 24 — FAANG Talking Points

Say these clearly:

1. This is read-heavy, so cache is critical.
2. Redirect path should be very fast.
3. Use `302` if analytics matter.
4. Use Offline KGS to avoid key generation latency.
5. KGS gives random non-guessable short codes.
6. Use atomic key leasing to avoid duplicate keys.
7. Keep web servers stateless.
8. Store mappings in sharded DB.
9. Use Redis for hot links.
10. Use async queue for analytics.
11. Use rate limiter to prevent abuse.
12. Use cleanup worker for expired links.
13. KGS needs standby and local app-server key buffers.
14. Fallback to Snowflake + Base62 if KGS pool is unavailable.

---

# Step 25 — One-Minute Final Answer

> I would design the URL shortener as a read-heavy service with stateless web servers behind a load balancer. For key generation, I would use an offline Key Generation Service that pre-generates random 7 or 8 character Base62 keys and stores them in an unused-key pool. App servers atomically lease keys from this pool, which removes key generation latency from the request path and avoids runtime collisions. For custom aliases, I validate and check uniqueness directly. The URL mappings are stored in a sharded database and hot mappings are cached in Redis. Redirects first check Redis, then fall back to DB, and return 302 if analytics matter. Click analytics are written asynchronously through a message queue. For reliability, KGS has a standby, app servers keep local key buffers, and DB/cache layers are replicated and sharded.

---

# Quick Revision

```text
Create:
Client -> LB -> App -> Rate Limit -> KGS/custom alias -> DB -> Cache -> short URL

Redirect:
Client -> LB -> App -> Cache -> DB if miss -> 302 Redirect

Analytics:
App -> Queue -> Workers -> Analytics DB

KGS:
Offline KGS -> unused key pool -> atomic lease -> app server

Scale:
Cache hot links, shard DB, replicate DB/cache, stateless web tier.
```
