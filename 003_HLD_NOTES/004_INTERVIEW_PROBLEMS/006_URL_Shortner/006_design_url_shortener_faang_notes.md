# 09 — Design a URL Shortener

> Goal: design a TinyURL-like service that converts long URLs into short aliases and redirects users back to the original URL.

---

## 1. Requirements

### Functional

- `POST /api/v1/data/shorten`
  - Input: long URL
  - Output: short URL
- `GET /{shortCode}`
  - Input: short code
  - Output: redirect to original long URL
- Short URLs are not updated or deleted for this design.

### Non-functional

- High availability
- Low latency redirects
- Scalable reads and writes
- Fault tolerant
- Short URL should be as short as possible
- Allowed characters: `0-9`, `a-z`, `A-Z`

---

## 2. Back-of-the-envelope Estimation

Given:

- 100 million new URLs per day
- Read/write ratio = 10:1
- Average long URL length = 100 bytes
- Retention = 10 years

```text
Writes per second = 100,000,000 / 24 / 3600 ≈ 1,160/sec
Reads per second  = 1,160 * 10 ≈ 11,600/sec
Total records     = 100M * 365 * 10 = 365B URLs
Storage           = 365B * 100 bytes ≈ 36.5 TB
```

Interview line:

> Reads dominate writes, so cache is very important for redirect latency.

---

## 3. High-Level Architecture

```text
                 +----------------+
                 |     Client     |
                 +-------+--------+
                         |
                         v
                 +----------------+
                 |  Load Balancer |
                 +-------+--------+
                         |
             +-----------+-----------+
             |                       |
             v                       v
      +-------------+          +-------------+
      | Web Server  |          | Web Server  |
      +------+------+          +------+
             |                        |
             +-----------+------------+
                         |
                         v
                 +---------------+
                 | Cache: Redis  |
                 +-------+-------+
                         |
                  cache miss
                         v
                 +---------------+
                 | URL Database  |
                 +---------------+
```

Main idea:

- Web servers are stateless.
- Cache stores hot `shortCode -> longUrl` mappings.
- Database stores durable mapping.
- Load balancer distributes requests.

---

## 4. API Design

### Shorten URL

```http
POST /api/v1/data/shorten
Content-Type: application/json

{
  "longUrl": "https://en.wikipedia.org/wiki/Systems_design"
}
```

Response:

```json
{
  "shortUrl": "https://tinyurl.com/zn9edcu"
}
```

### Redirect URL

```http
GET /zn9edcu
```

Response:

```http
302 Found
Location: https://en.wikipedia.org/wiki/Systems_design
```

---

## 5. 301 vs 302 Redirect

| Redirect | Meaning | Pros | Cons |
|---|---|---|---|
| `301` | Permanent redirect | Browser/CDN can cache; less server load | Harder to track analytics after first hit |
| `302` | Temporary redirect | Every click hits service; better analytics | More server load |

Interview answer:

> Use `302` if analytics are important. Use `301` if reducing load is more important.

---

## 6. Data Model

```text
Table: url_mapping

+----------------+----------------+-----------------------------+
| id             | short_code     | long_url                    |
+----------------+----------------+-----------------------------+
| 2009215674938  | zn9edcu        | https://en.wikipedia...     |
+----------------+----------------+-----------------------------+
```

Suggested columns:

```sql
id BIGINT PRIMARY KEY
short_code VARCHAR(16) UNIQUE NOT NULL
long_url TEXT NOT NULL
created_at TIMESTAMP
expire_at TIMESTAMP NULL
user_id BIGINT NULL
```

Indexes:

```text
PRIMARY KEY(id)
UNIQUE(short_code)
INDEX(long_url_hash) optional, for duplicate detection
```

---

## 7. Short Code Length

Allowed characters:

```text
0-9 = 10
a-z = 26
A-Z = 26
Total = 62 characters
```

Capacity:

```text
62^6 ≈ 56.8 billion
62^7 ≈ 3.5 trillion
```

Since we need about `365 billion` URLs over 10 years:

```text
7 characters are enough.
```

---

## 8. URL Shortening Approaches

### Approach A — Hash + Collision Resolution

```text
longUrl -> hash(longUrl) -> first 7 chars -> shortCode
```

Problem:

- Collision is possible.
- Need DB lookup to check whether generated short code already exists.

Visual:

```text
+----------+     +-----------+     +------------+
| long URL | --> | hash func | --> | short code |
+----------+     +-----------+     +------------+
                                      |
                                      v
                              +---------------+
                              | exists in DB? |
                              +-------+-------+
                                      |
                      +---------------+---------------+
                      |                               |
                     yes                              no
                      |                               |
                      v                               v
          append salt and retry                 save mapping
```

### Approach B — Unique ID + Base62

```text
unique numeric ID -> Base62 encode -> shortCode
```

Example:

```text
ID: 2009215674938
Base62: zn9edcu
Short URL: https://tinyurl.com/zn9edcu
```

Pros:

- No collision if ID generator is unique.
- Fast and simple.
- Short code grows gradually.

Cons:

- Needs distributed unique ID generator.
- Sequential IDs may reveal business volume.

Interview recommendation:

> Use distributed unique ID generation plus Base62 encoding.

---

## 9. Base62 Conversion Visual

Decimal `11157` to Base62:

```text
11157 / 62 = 179 remainder 59 -> X
  179 / 62 =   2 remainder 55 -> T
    2 / 62 =   0 remainder  2 -> 2

Read remainders backward: 2TX
```

```text
Base62 chars:
0-9  -> 0..9
a-z  -> 10..35
A-Z  -> 36..61
```

---

## 10. URL Shortening Flow

```text
+------------------+
| Input long URL   |
+--------+---------+
         |
         v
+------------------+
| Validate URL     |
+--------+---------+
         |
         v
+-----------------------------+
| Already exists in database? |
+-----------+-----------------+
            |
   +--------+--------+
   |                 |
  yes                no
   |                 |
   v                 v
return existing   generate unique ID
short URL             |
                      v
              Base62 encode ID
                      |
                      v
              save id/code/url
                      |
                      v
              return short URL
```

---

## 11. Redirect Flow

```text
User clicks short URL
        |
        v
+------------------+
| Load Balancer    |
+--------+---------+
         |
         v
+------------------+
| Web Server       |
+--------+---------+
         |
         v
+-----------------------------+
| Redis cache lookup          |
| key = shortCode             |
+-------------+---------------+
              |
      +-------+-------+
      |               |
    hit              miss
      |               |
      v               v
return long URL   query database
      |               |
      v               v
  HTTP 302       store in cache
      |               |
      +-------+-------+
              v
        redirect user
```

---

## 12. Java Code — Base62 Encoder

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

    public static long decode(String code) {
        long result = 0;
        for (char c : code.toCharArray()) {
            result = result * BASE + CHARS.indexOf(c);
        }
        return result;
    }

    public static void main(String[] args) {
        long id = 11157;
        String code = encode(id);
        System.out.println(code);          // 2TX
        System.out.println(decode(code));  // 11157
    }
}
```

---

## 13. Java Code — Simple In-memory URL Shortener

This is not production-ready, but useful for interview understanding.

```java
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class UrlShortener {
    private static final String DOMAIN = "https://tinyurl.com/";
    private final AtomicLong idGenerator = new AtomicLong(1_000_000);
    private final Map<String, String> codeToLongUrl = new HashMap<>();
    private final Map<String, String> longUrlToCode = new HashMap<>();

    public String shorten(String longUrl) {
        if (longUrl == null || longUrl.isBlank()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }

        if (longUrlToCode.containsKey(longUrl)) {
            return DOMAIN + longUrlToCode.get(longUrl);
        }

        long id = idGenerator.incrementAndGet();
        String code = Base62.encode(id);

        codeToLongUrl.put(code, longUrl);
        longUrlToCode.put(longUrl, code);

        return DOMAIN + code;
    }

    public String getLongUrl(String shortCode) {
        String longUrl = codeToLongUrl.get(shortCode);
        if (longUrl == null) {
            throw new RuntimeException("Short URL not found");
        }
        return longUrl;
    }

    public static void main(String[] args) {
        UrlShortener service = new UrlShortener();
        String shortUrl = service.shorten("https://en.wikipedia.org/wiki/Systems_design");

        System.out.println(shortUrl);

        String code = shortUrl.substring(shortUrl.lastIndexOf('/') + 1);
        System.out.println(service.getLongUrl(code));
    }
}
```

---

## 14. Java Code — Redirect Controller Style

Spring-style pseudo code:

```java
@RestController
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/api/v1/data/shorten")
    public Map<String, String> shorten(@RequestBody Map<String, String> request) {
        String longUrl = request.get("longUrl");
        String shortUrl = urlService.shorten(longUrl);
        return Map.of("shortUrl", shortUrl);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String longUrl = urlService.resolve(code);

        return ResponseEntity
                .status(HttpStatus.FOUND) // 302
                .header(HttpHeaders.LOCATION, longUrl)
                .build();
    }
}
```

---

## 15. Java Code — Service with Cache Idea

```java
public class UrlService {
    private final UrlRepository repository;
    private final Cache<String, String> cache;
    private final IdGenerator idGenerator;

    public String shorten(String longUrl) {
        // 1. Optional: check if long URL already exists
        UrlMapping existing = repository.findByLongUrl(longUrl);
        if (existing != null) {
            return "https://tinyurl.com/" + existing.shortCode();
        }

        // 2. Generate unique ID
        long id = idGenerator.nextId();

        // 3. Convert to Base62
        String code = Base62.encode(id);

        // 4. Save mapping
        repository.save(new UrlMapping(id, code, longUrl));

        // 5. Warm cache
        cache.put(code, longUrl);

        return "https://tinyurl.com/" + code;
    }

    public String resolve(String code) {
        // 1. Cache first
        String longUrl = cache.get(code);
        if (longUrl != null) {
            return longUrl;
        }

        // 2. DB fallback
        UrlMapping mapping = repository.findByShortCode(code);
        if (mapping == null) {
            throw new NotFoundException("Invalid short URL");
        }

        // 3. Cache for future reads
        cache.put(code, mapping.longUrl());
        return mapping.longUrl();
    }
}
```

---

## 16. Cache Strategy

```text
Cache key:   shortCode
Cache value: longUrl
TTL:         optional, for expiring URLs
```

Cache flow:

```text
GET /abc123
   |
   v
Redis lookup: abc123
   |
   +-- hit  --> return longUrl
   |
   +-- miss --> DB lookup --> update Redis --> return longUrl
```

Use cache because:

- Redirect traffic is high.
- Popular links are clicked many times.
- Cache reduces DB load.

---

## 17. Database Scaling

### Replication

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

Use primary for writes and replicas for reads.

### Sharding

Shard by `shortCode` or `id`:

```text
shard = hash(shortCode) % numberOfShards
```

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

---

## 18. Handling Invalid URLs

For an unknown short code:

```http
404 Not Found
```

For malformed long URL during creation:

```http
400 Bad Request
```

For abusive clients:

```http
429 Too Many Requests
```

---

## 19. Analytics Extension

To track clicks:

```text
Redirect request
      |
      v
Return 302 immediately
      |
      v
Send click event asynchronously
      |
      v
Message Queue -> Analytics Workers -> Analytics DB
```

Visual:

```text
+--------+      +------------+      +------------+
| Client | ---> | URL Server | ---> | Long URL   |
+--------+      +-----+------+      +------------+
                      |
                      v
              +---------------+
              | Message Queue |
              +-------+-------+
                      |
                      v
              +---------------+
              | Analytics     |
              +---------------+
```

Why async?

- Redirect should stay fast.
- Analytics should not block user experience.

---

## 20. Security Considerations

- Rate limit URL creation by IP/user.
- Validate URL format.
- Block malicious domains if required.
- Prevent short-code enumeration if business-sensitive.
- Add CAPTCHA for suspicious traffic.
- Use random-looking IDs if sequential Base62 leaks volume.

---

## 21. Availability and Fault Tolerance

| Component | Strategy |
|---|---|
| Web servers | Stateless horizontal scaling |
| Cache | Redis cluster / replication |
| Database | Replication + sharding |
| ID generator | Snowflake-style distributed ID generator |
| Analytics | Message queue with retries |
| Traffic | Load balancer + health checks |

---

## 22. FAANG Interview Talking Points

Mention these clearly:

1. Reads are much higher than writes, so cache is critical.
2. Use `302` if analytics matter; use `301` if reducing load matters.
3. Use Base62 because short URL uses 62 allowed characters.
4. 7-character short code supports about 3.5 trillion URLs.
5. Use distributed ID generator to avoid collisions.
6. Web tier should be stateless.
7. Use DB replication and sharding for scale.
8. Use message queue for async analytics.
9. Add rate limiter to prevent abuse.
10. Cache popular short-code mappings.

---

## 23. Final Design Summary

```text
Shortening:
Client -> LB -> Web Server -> ID Generator -> Base62 -> DB -> Cache -> Client

Redirect:
Client -> LB -> Web Server -> Cache -> DB if miss -> 302 Redirect

Analytics:
Web Server -> Message Queue -> Workers -> Analytics DB
```

Final architecture:

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
            +------+------+               +------+
                   |                             |
                   +--------------+--------------+
                                  |
              +-------------------+-------------------+
              |                   |                   |
              v                   v                   v
        +-----------+       +------------+       +-------------+
        | Redis     |       | URL DB     |       | ID Generator|
        | Cache     |       | Sharded    |       | Snowflake   |
        +-----------+       +------------+       +-------------+
                                  |
                                  v
                         +----------------+
                         | Message Queue  |
                         +--------+-------+
                                  |
                                  v
                         +----------------+
                         | Analytics DB   |
                         +----------------+
```

---

## 24. Quick Revision

```text
Problem: Long URL -> short URL -> redirect back
Scale: 100M writes/day, 11.6K reads/sec
Code length: 7 Base62 chars
Storage: ~36.5 TB for 10 years
Core technique: unique ID + Base62
Redirect: cache first, DB fallback
Status code: 301 for cache/load, 302 for analytics
Abuse prevention: rate limiter
Analytics: async via queue
```
