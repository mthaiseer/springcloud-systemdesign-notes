# 006_Redirect_API.md
# MiniURLShortener — Redirect API

> Core mental model: **The Redirect API is the hot read path. It receives a short code, resolves it to the original long URL, checks whether redirect is allowed, and returns an HTTP redirect as fast and safely as possible.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Redirect API Contract](#4-redirect-api-contract)
- [5. Redirect Flow Big Picture](#5-redirect-flow-big-picture)
- [6. HTTP Redirect Mental Model](#6-http-redirect-mental-model)
- [7. 301 vs 302 vs 307 vs 308](#7-301-vs-302-vs-307-vs-308)
- [8. Controller Design](#8-controller-design)
- [9. Service Design](#9-service-design)
- [10. Repository Lookup Design](#10-repository-lookup-design)
- [11. Status And Expiry Decision](#11-status-and-expiry-decision)
- [12. Error Model](#12-error-model)
- [13. Full Code Skeleton](#13-full-code-skeleton)
- [14. Step-by-Step Dry Runs](#14-step-by-step-dry-runs)
- [15. Internal Execution Walkthrough](#15-internal-execution-walkthrough)
- [16. Redirect Is The Hot Path](#16-redirect-is-the-hot-path)
- [17. Why Analytics Must Not Block Redirect](#17-why-analytics-must-not-block-redirect)
- [18. Cache-Ready Design](#18-cache-ready-design)
- [19. Testing Strategy](#19-testing-strategy)
- [20. Production Failure Stories](#20-production-failure-stories)
- [21. Debugging Mindset](#21-debugging-mindset)
- [22. Common Mistakes](#22-common-mistakes)
- [23. Performance And Scalability Notes](#23-performance-and-scalability-notes)
- [24. Interview-Ready Explanation](#24-interview-ready-explanation)
- [25. Senior Engineer Checklist](#25-senior-engineer-checklist)
- [26. One-Page Cheat Sheet](#26-one-page-cheat-sheet)
- [27. One Picture To Remember](#27-one-picture-to-remember)

---

## 1. Why This Exists

The previous chapter created the write path:

```text
longUrl -> shortCode
```

Now we build the read path:

```text
shortCode -> longUrl -> HTTP redirect
```

Example:

```text
User opens:
https://mini.ly/aB91xZ
```

The application must find:

```text
aB91xZ -> https://example.com/articles/spring-boot
```

Then return:

```http
HTTP/1.1 302 Found
Location: https://example.com/articles/spring-boot
```

The browser then automatically opens the original URL.

This endpoint is more important than it looks.

Create API may be called once.
Redirect API may be called thousands, millions, or billions of times.

```text
Create:
1 request

Redirect:
10,000+ requests for the same short link
```

So Redirect API is the **hot path**.

The engineering goal is:

```text
Resolve short code correctly.
Return redirect quickly.
Avoid unnecessary work.
Keep failure behavior clear.
Prepare for Redis later.
Keep analytics off the critical path.
```

---

## 2. The One Core Mental Model

The Redirect API is a:

```text
FAST TOKEN RESOLVER
```

It receives a public token:

```text
aB91xZ
```

It resolves the token to a long URL:

```text
https://example.com/articles/spring-boot
```

Then it tells the browser:

```text
Go there.
```

ASCII mental model:

```text
                        REDIRECT API

Browser
   |
   | GET /aB91xZ
   v
+----------------------+
| Extract shortCode    |
+----------------------+
   |
   v
+----------------------+
| Lookup mapping       |
+----------------------+
   |
   v
+----------------------+
| Check status/expiry  |
+----------------------+
   |
   v
+----------------------+
| Return 302 Location  |
+----------------------+
   |
   v
Browser opens long URL
```

One-line memory:

```text
Redirect API does not fetch the destination page; it only returns a Location header telling the browser where to go.
```

This distinction is critical.

Bad mental model:

```text
MiniURLShortener downloads original website and sends it to user.
```

Correct mental model:

```text
MiniURLShortener returns a redirect response.
Browser makes the next request to the original website.
```

---

## 3. Problem Statement

Build the API that redirects from short URL to long URL.

Functional requirements:

```text
1. Accept GET /{shortCode}.
2. Extract shortCode from path.
3. Validate shortCode shape lightly.
4. Lookup shortCode in PostgreSQL.
5. If not found, return 404.
6. If blocked, return 403.
7. If deleted, return 404 or 410 depending on policy.
8. If expired, return 410.
9. If active and valid, return 302 Location header.
10. Keep code cache-ready for Redis later.
```

Non-functional requirements:

```text
1. Very low latency.
2. Minimal database work.
3. No heavy side effects before redirect.
4. Clear logging.
5. Clean error responses.
6. No analytics blocking.
```

Out of scope for this chapter:

```text
1. Redis cache implementation.
2. Kafka click analytics.
3. Rate limiting.
4. Browser error page UI.
5. Malware blocking integration.
6. CDN-level redirects.
```

This chapter focuses on the correct redirect behavior with PostgreSQL as source of truth.

---

## 4. Redirect API Contract

Endpoint:

```http
GET /{shortCode}
```

Example:

```http
GET /aB91xZ
```

Success:

```http
HTTP/1.1 302 Found
Location: https://example.com/articles/spring-boot
```

Error cases:

```text
404 Not Found:
    shortCode does not exist

403 Forbidden:
    shortCode exists but is blocked

410 Gone:
    shortCode exists but expired

400 Bad Request:
    shortCode shape invalid, optional decision

500 Internal Server Error:
    unexpected failure
```

Decision table:

```text
+-----------------------------+------------------+
| Condition                   | HTTP Response    |
+-----------------------------+------------------+
| shortCode not found         | 404 Not Found    |
| status = BLOCKED            | 403 Forbidden    |
| status = DELETED            | 404 or 410       |
| expiresAt in past           | 410 Gone         |
| active and not expired      | 302 Found        |
+-----------------------------+------------------+
```

Recommended v1 behavior:

```text
not found -> 404
deleted   -> 404
blocked   -> 403
expired   -> 410
active    -> 302
```

Why deleted returns 404?

```text
It avoids revealing that the link used to exist.
```

Why expired returns 410?

```text
410 Gone communicates that the resource existed but is no longer available.
```

---

## 5. Redirect Flow Big Picture

Full flow:

```text
Browser
  |
  | GET /aB91xZ
  v
RedirectController
  |
  | extracts shortCode
  v
RedirectService
  |
  | validates shape
  | lookup mapping
  | check status
  | check expiry
  v
ShortUrlRepository
  |
  | SELECT by short_code
  v
PostgreSQL
  |
  | returns row or empty
  v
RedirectService
  |
  | returns long URL or throws domain error
  v
RedirectController
  |
  | builds 302 response
  v
Browser
  |
  | opens Location URL
  v
Original website
```

ASCII sequence:

```text
+---------+       +------------+       +----------+       +----------+
| Browser |       | Controller |       | Service  |       | Postgres |
+---------+       +------------+       +----------+       +----------+
     |                  |                   |                  |
     | GET /aB91xZ      |                   |                  |
     |----------------->|                   |                  |
     |                  | resolve(aB91xZ)   |                  |
     |                  |------------------>|                  |
     |                  |                   | SELECT by code   |
     |                  |                   |----------------->|
     |                  |                   | row              |
     |                  |                   |<-----------------|
     |                  | longUrl           |                  |
     |                  |<------------------|                  |
     | 302 Location     |                   |                  |
     |<-----------------|                   |                  |
     | opens long URL   |                   |                  |
     |-------------------------------------------------------->|
```

Important:

```text
The app is involved only until it sends 302.
After that, browser talks to original website directly.
```

---

## 6. HTTP Redirect Mental Model

HTTP redirect is just a response with:

```text
status code
Location header
```

Example:

```http
HTTP/1.1 302 Found
Location: https://example.com/articles/spring-boot
```

Browser behavior:

```text
1. Browser requests https://mini.ly/aB91xZ.
2. Server responds 302 Location: https://example.com/articles/spring-boot.
3. Browser automatically sends new request to https://example.com/articles/spring-boot.
```

ASCII:

```text
Step 1:
Browser --------------------> mini.ly/aB91xZ

Step 2:
Browser <-------------------- 302 Location: example.com/article

Step 3:
Browser --------------------> example.com/article
```

This means MiniURLShortener does not need to:

```text
download original page
parse HTML
stream response
proxy target site
```

It only needs to return:

```text
Location header
```

Spring way:

```java
return ResponseEntity
        .status(HttpStatus.FOUND)
        .location(URI.create(longUrl))
        .build();
```

`HttpStatus.FOUND` means:

```text
302 Found
```

---

## 7. 301 vs 302 vs 307 vs 308

Common redirect codes:

```text
301 Moved Permanently
302 Found
307 Temporary Redirect
308 Permanent Redirect
```

### 301

```text
Permanent redirect.
Browsers and caches may remember it aggressively.
```

Risk:

```text
If target URL changes or was wrong, clients may keep old redirect.
```

### 302

```text
Temporary redirect.
Safer default for URL shortener v1.
```

Why prefer 302 now?

```text
1. Safer during development.
2. Avoids permanent browser caching surprises.
3. Allows future blocking/expiry behavior.
4. Easier to debug when mapping changes.
```

### 307

```text
Temporary redirect preserving HTTP method.
```

For GET redirect, not usually necessary.

### 308

```text
Permanent redirect preserving HTTP method.
```

Not needed for v1.

Recommendation:

```text
Use 302 Found for MiniURLShortener v1.
```

Memory:

```text
302 first.
301 only when you intentionally want permanent caching behavior.
```

---

## 8. Controller Design

Create:

```text
src/main/java/com/miniurl/shortener/url/controller/RedirectController.java
```

Code:

```java
package com.miniurl.shortener.url.controller;

import com.miniurl.shortener.url.service.RedirectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class RedirectController {

    private final RedirectService redirectService;

    public RedirectController(RedirectService redirectService) {
        this.redirectService = redirectService;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode
    ) {
        String longUrl = redirectService.resolveLongUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }
}
```

Controller responsibilities:

```text
1. Map GET /{shortCode}.
2. Extract shortCode.
3. Ask service to resolve longUrl.
4. Return 302 Location response.
```

Controller should not:

```text
1. Query repository directly.
2. Decide expiry.
3. Decide blocked status.
4. Insert analytics synchronously.
5. Contain cache logic.
```

Controller mental model:

```text
HTTP adapter that turns service result into HTTP redirect.
```

ASCII:

```text
GET /aB91xZ
    |
    v
RedirectController
    |
    v
RedirectService.resolveLongUrl()
    |
    v
ResponseEntity 302 Location
```

---

## 9. Service Design

Create:

```text
src/main/java/com/miniurl/shortener/url/service/RedirectService.java
```

Code:

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.url.entity.ShortUrlEntity;
import com.miniurl.shortener.url.entity.ShortUrlStatus;
import com.miniurl.shortener.url.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RedirectService {

    private final ShortUrlRepository shortUrlRepository;

    public RedirectService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    @Transactional(readOnly = true)
    public String resolveLongUrl(String shortCode) {
        validateShortCodeShape(shortCode);

        ShortUrlEntity entity = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));

        if (entity.getStatus() == ShortUrlStatus.BLOCKED) {
            throw new ShortCodeBlockedException(shortCode);
        }

        if (entity.getStatus() == ShortUrlStatus.DELETED) {
            throw new ShortCodeNotFoundException(shortCode);
        }

        if (isExpired(entity.getExpiresAt())) {
            throw new ShortCodeExpiredException(shortCode);
        }

        return entity.getLongUrl();
    }

    private void validateShortCodeShape(String shortCode) {
        if (shortCode == null || shortCode.isBlank()) {
            throw new InvalidShortCodeException("shortCode is required");
        }

        if (!shortCode.matches("^[a-zA-Z0-9_-]{4,32}$")) {
            throw new InvalidShortCodeException("shortCode has invalid format");
        }
    }

    private boolean isExpired(Instant expiresAt) {
        return expiresAt != null && !expiresAt.isAfter(Instant.now());
    }
}
```

Why `@Transactional(readOnly = true)`?

```text
1. Communicates this is read-only.
2. Allows transaction manager/provider to optimize.
3. Prevents accidental write intention.
4. Keeps persistence context behavior clear.
```

Service responsibilities:

```text
1. Validate short code shape.
2. Lookup entity.
3. Apply business decisions.
4. Return longUrl.
```

Service should not build HTTP response directly.

Why?

```text
HTTP belongs to controller.
Business decision belongs to service.
```

---

## 10. Repository Lookup Design

Basic repository:

```java
package com.miniurl.shortener.url.repository;

import com.miniurl.shortener.url.entity.ShortUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrlEntity, Long> {

    Optional<ShortUrlEntity> findByShortCode(String shortCode);
}
```

For v1, this is enough.

Hot-path optimized projection later:

```java
package com.miniurl.shortener.url.repository;

import com.miniurl.shortener.url.entity.ShortUrlStatus;
import java.time.Instant;

public interface RedirectLookupView {
    String getLongUrl();
    ShortUrlStatus getStatus();
    Instant getExpiresAt();
}
```

Repository query:

```java
@Query("""
       select s.longUrl as longUrl,
              s.status as status,
              s.expiresAt as expiresAt
       from ShortUrlEntity s
       where s.shortCode = :shortCode
       """)
Optional<RedirectLookupView> findRedirectByShortCode(
        @Param("shortCode") String shortCode
);
```

Why projection?

```text
Redirect path does not need full entity.
Only needs longUrl, status, expiresAt.
```

SQL mental model:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?;
```

This uses:

```text
UNIQUE index on short_code
```

ASCII:

```text
short_code = aB91xZ
       |
       v
+----------------------------+
| unique index lookup        |
+----------------------------+
       |
       v
+----------------------------+
| row found                  |
| long_url/status/expires_at |
+----------------------------+
```

For this chapter:

```text
Use simple entity lookup.
Know projection is the next optimization.
```

---

## 11. Status And Expiry Decision

Redirect decision tree:

```text
GET /{shortCode}
       |
       v
Find row by shortCode
       |
       +-- no row --> 404
       |
       v
Check status
       |
       +-- BLOCKED --> 403
       |
       +-- DELETED --> 404
       |
       v
Check expiresAt
       |
       +-- expired --> 410
       |
       v
Return 302 Location
```

ASCII:

```text
                  Redirect Decision

                       GET /code
                           |
                           v
                    +--------------+
                    | Row exists?  |
                    +--------------+
                     /            \
                   no              yes
                   |                |
                   v                v
                 404         +--------------+
                             | status?      |
                             +--------------+
                              /      |      \
                         BLOCKED  DELETED  ACTIVE
                            |       |        |
                            v       v        v
                           403     404   +------------+
                                         | expired?   |
                                         +------------+
                                          /          \
                                        yes           no
                                        |             |
                                        v             v
                                       410           302
```

Why check blocked before expiry?

```text
Blocked means security/admin decision.
Even if also expired, blocked is more important for investigation.
```

But order can be product-specific.

Recommended v1 order:

```text
1. not found
2. blocked
3. deleted
4. expired
5. redirect
```

---

## 12. Error Model

Domain exceptions:

```java
package com.miniurl.shortener.url.service;

public class ShortCodeNotFoundException extends RuntimeException {
    public ShortCodeNotFoundException(String shortCode) {
        super("shortCode not found: " + shortCode);
    }
}
```

```java
package com.miniurl.shortener.url.service;

public class ShortCodeExpiredException extends RuntimeException {
    public ShortCodeExpiredException(String shortCode) {
        super("shortCode expired: " + shortCode);
    }
}
```

```java
package com.miniurl.shortener.url.service;

public class ShortCodeBlockedException extends RuntimeException {
    public ShortCodeBlockedException(String shortCode) {
        super("shortCode blocked: " + shortCode);
    }
}
```

```java
package com.miniurl.shortener.url.service;

public class InvalidShortCodeException extends RuntimeException {
    public InvalidShortCodeException(String message) {
        super(message);
    }
}
```

Global handler additions:

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
@ExceptionHandler(ShortCodeNotFoundException.class)
public Map<String, Object> handleNotFound(RuntimeException ex) {
    return Map.of(
            "timestamp", Instant.now().toString(),
            "status", 404,
            "error", "Not Found",
            "message", ex.getMessage()
    );
}

@ResponseStatus(HttpStatus.GONE)
@ExceptionHandler(ShortCodeExpiredException.class)
public Map<String, Object> handleExpired(RuntimeException ex) {
    return Map.of(
            "timestamp", Instant.now().toString(),
            "status", 410,
            "error", "Gone",
            "message", ex.getMessage()
    );
}

@ResponseStatus(HttpStatus.FORBIDDEN)
@ExceptionHandler(ShortCodeBlockedException.class)
public Map<String, Object> handleBlocked(RuntimeException ex) {
    return Map.of(
            "timestamp", Instant.now().toString(),
            "status", 403,
            "error", "Forbidden",
            "message", ex.getMessage()
    );
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(InvalidShortCodeException.class)
public Map<String, Object> handleInvalidShortCode(RuntimeException ex) {
    return Map.of(
            "timestamp", Instant.now().toString(),
            "status", 400,
            "error", "Bad Request",
            "message", ex.getMessage()
    );
}
```

Production note:

```text
For browser users, you may later return HTML error pages.
For backend learning, JSON errors are fine.
```

---

## 13. Full Code Skeleton

Recommended files:

```text
src/main/java/com/miniurl/shortener/url
│
├── controller
│   ├── UrlController.java
│   └── RedirectController.java
│
├── service
│   ├── UrlService.java
│   ├── RedirectService.java
│   ├── ShortCodeNotFoundException.java
│   ├── ShortCodeExpiredException.java
│   ├── ShortCodeBlockedException.java
│   └── InvalidShortCodeException.java
│
├── repository
│   └── ShortUrlRepository.java
│
└── entity
    ├── ShortUrlEntity.java
    └── ShortUrlStatus.java
```

Common error handler:

```text
src/main/java/com/miniurl/shortener/common/error
└── GlobalExceptionHandler.java
```

Architecture:

```text
GET /{shortCode}
     |
     v
RedirectController
     |
     v
RedirectService
     |
     v
ShortUrlRepository
     |
     v
PostgreSQL
```

Layer responsibility:

```text
Controller:
    HTTP response shape

Service:
    redirect business decision

Repository:
    database lookup

Database:
    durable mapping
```

---

## 14. Step-by-Step Dry Runs

### Dry Run 1: Active link redirects

Database row:

```text
short_code = aB91xZ
long_url = https://example.com/articles/spring
status = ACTIVE
expires_at = null
```

Request:

```http
GET /aB91xZ
```

Steps:

```text
1. Browser calls /aB91xZ.
2. RedirectController extracts shortCode = aB91xZ.
3. RedirectService validates format.
4. Repository finds row.
5. Status is ACTIVE.
6. expiresAt is null.
7. Service returns longUrl.
8. Controller returns 302 Location.
9. Browser opens original URL.
```

Response:

```http
HTTP/1.1 302 Found
Location: https://example.com/articles/spring
```

---

### Dry Run 2: Unknown short code

Request:

```http
GET /wrong99
```

Database:

```text
No row with short_code = wrong99
```

Steps:

```text
1. Repository returns Optional.empty().
2. Service throws ShortCodeNotFoundException.
3. GlobalExceptionHandler returns 404.
```

Response:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "shortCode not found: wrong99"
}
```

---

### Dry Run 3: Blocked short code

Row:

```text
short_code = bad1
status = BLOCKED
```

Request:

```http
GET /bad1
```

Steps:

```text
1. Row exists.
2. Service sees BLOCKED.
3. Service throws ShortCodeBlockedException.
4. Handler returns 403.
```

Result:

```http
403 Forbidden
```

Why not redirect?

```text
Security/admin decision blocks the link.
```

---

### Dry Run 4: Deleted short code

Row:

```text
short_code = old1
status = DELETED
```

Request:

```http
GET /old1
```

Result:

```http
404 Not Found
```

Why 404?

```text
Deleted links can be hidden as if they do not exist.
```

---

### Dry Run 5: Expired short code

Row:

```text
short_code = sale
status = ACTIVE
expires_at = 2026-01-01T00:00:00Z
```

Current time:

```text
2026-06-21T10:00:00Z
```

Decision:

```text
expiresAt <= now
```

Result:

```http
410 Gone
```

---

### Dry Run 6: Invalid short code shape

Request:

```http
GET /a/b
```

In practice, slash affects path routing, but conceptually:

```text
shortCode contains invalid character
```

Request:

```http
GET /abc$123
```

Decision:

```text
format invalid
```

Result:

```http
400 Bad Request
```

Why validate shape?

```text
Avoid unnecessary DB lookups for clearly invalid codes.
```

---

## 15. Internal Execution Walkthrough

Spring internal flow:

```text
1. Browser sends GET /aB91xZ.
2. Embedded Tomcat receives request.
3. DispatcherServlet receives request.
4. HandlerMapping matches RedirectController.redirect.
5. PathVariable shortCode is extracted.
6. Controller calls RedirectService.
7. readOnly transaction starts.
8. Repository executes SELECT by short_code.
9. PostgreSQL uses unique index.
10. Row returns.
11. Service checks status and expiry.
12. Service returns longUrl.
13. Controller creates ResponseEntity with 302 and Location.
14. Browser receives redirect.
15. Browser calls original longUrl.
```

ASCII:

```text
+---------+       +--------+       +-------------------+
| Browser | ----> | Tomcat | ----> | DispatcherServlet |
+---------+       +--------+       +-------------------+
                                             |
                                             v
                                     +-------------------+
                                     | RedirectController|
                                     +-------------------+
                                             |
                                             v
                                     +-------------------+
                                     | RedirectService   |
                                     | readOnly tx       |
                                     +-------------------+
                                             |
                                             v
                                     +-------------------+
                                     | Repository        |
                                     +-------------------+
                                             |
                                             v
                                     +-------------------+
                                     | PostgreSQL index  |
                                     +-------------------+
```

Hot-path rule:

```text
Every extra operation here matters.
```

Avoid adding slow work before the 302.

---

## 16. Redirect Is The Hot Path

Redirect is read-heavy.

Example:

```text
Create one link:
POST /api/v1/urls -> 1 request

Share link on social media:
GET /aB91xZ -> 500,000 requests
```

Ratio:

```text
writes: 1
reads : 500,000
```

So redirect path must be:

```text
fast
simple
cacheable
observable
available
```

Bad redirect design:

```text
GET /code
  -> lookup DB
  -> insert analytics row
  -> call fraud service
  -> update click count
  -> call user service
  -> return redirect
```

Better v1:

```text
GET /code
  -> lookup DB
  -> check status/expiry
  -> return redirect
```

Better later:

```text
GET /code
  -> Redis lookup
  -> return redirect
  -> emit analytics asynchronously
```

Memory:

```text
Critical path should contain only critical work.
```

---

## 17. Why Analytics Must Not Block Redirect

Analytics is useful but non-critical.

Bad design:

```text
Redirect waits for analytics insert.
```

Failure:

```text
Analytics DB slow -> redirects slow
Analytics DB down -> redirects fail
```

Correct design:

```text
Redirect success should not depend on analytics success.
```

ASCII:

```text
BAD:

GET /code
   |
   v
lookup longUrl
   |
   v
insert click analytics
   |
   v
return 302

GOOD:

GET /code
   |
   v
lookup longUrl
   |
   v
return 302
   |
   v
async click event later
```

For this chapter:

```text
Do not implement analytics in redirect path yet.
```

Later chapter:

```text
032_Kafka_Click_Analytics.md
```

will move click events asynchronously.

Rule:

```text
Redirect correctness > analytics completeness.
```

---

## 18. Cache-Ready Design

Today:

```text
App -> Postgres
```

Later:

```text
App -> Redis -> Postgres on miss
```

Design service so cache can be inserted later without changing controller.

Current:

```java
String longUrl = redirectService.resolveLongUrl(shortCode);
```

Later service internals can become:

```text
1. Check Redis.
2. If hit, return longUrl.
3. If miss, query Postgres.
4. Store in Redis.
5. Return longUrl.
```

ASCII:

```text
Current:

Controller -> RedirectService -> Postgres

Later:

Controller -> RedirectService -> Redis
                              -> Postgres on miss
```

Cache-aside future:

```text
GET /aB91xZ
    |
    v
Redis has code?
    |
    +-- yes --> return 302
    |
    +-- no --> query Postgres
                |
                v
             store in Redis
                |
                v
             return 302
```

Why this chapter matters:

```text
A clean service boundary lets us add Redis later without rewriting controller.
```

---

## 19. Testing Strategy

### Controller tests

Cases:

```text
GET /abc123 active -> 302
GET /missing -> 404
GET /blocked -> 403
GET /expired -> 410
GET /invalid$ -> 400
```

Mock service example:

```java
@Test
void shouldReturn302WhenShortCodeIsActive() throws Exception {
    when(redirectService.resolveLongUrl("abc123"))
            .thenReturn("https://example.com");

    mockMvc.perform(get("/abc123"))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", "https://example.com"));
}
```

### Service tests

Cases:

```text
row not found -> ShortCodeNotFoundException
status BLOCKED -> ShortCodeBlockedException
status DELETED -> ShortCodeNotFoundException
expired -> ShortCodeExpiredException
active -> returns longUrl
```

### Repository integration test later

Use Testcontainers later to verify:

```text
findByShortCode uses real Postgres row
unique index exists
queries work
```

### Manual curl test

Create first:

```bash
curl -X POST http://localhost:8080/api/v1/urls \
  -H "Content-Type: application/json" \
  -d '{"longUrl":"https://example.com"}'
```

Then redirect:

```bash
curl -i http://localhost:8080/aB91xZ
```

Expected:

```http
HTTP/1.1 302
Location: https://example.com
```

Use `-L` to follow redirect:

```bash
curl -L http://localhost:8080/aB91xZ
```

But for debugging redirect response, prefer:

```bash
curl -i
```

because it shows the 302 header.

---

## 20. Production Failure Stories

### Failure Story 1: Analytics blocks redirect

Team inserts click analytics before returning 302.

Analytics DB slows down.

Result:

```text
Redirect p99 jumps from 50ms to 3s.
Users think links are broken.
```

Fix:

```text
Move analytics to async event pipeline.
```

Lesson:

```text
Do not place non-critical writes in hot read path.
```

---

### Failure Story 2: Missing short_code index

At 10,000 rows, everything works.

At 50 million rows, redirect query becomes slow.

Root cause:

```text
No index on short_code.
```

Fix:

```text
UNIQUE(short_code), which creates index.
```

Lesson:

```text
Hot lookup columns must be indexed from day one.
```

---

### Failure Story 3: 301 used too early

Team returns:

```http
301 Moved Permanently
```

During testing, wrong long URL is saved.

Browser caches permanent redirect.

Even after DB fix, some users keep going to wrong destination.

Fix:

```text
Use 302 initially.
Use 301 only intentionally.
```

Lesson:

```text
Redirect status codes have caching consequences.
```

---

### Failure Story 4: Expiry not checked

System stores expires_at but redirect service ignores it.

Expired campaign links continue redirecting.

Root cause:

```text
Schema had field, service did not enforce behavior.
```

Fix:

```text
Check expiresAt before returning redirect.
```

Lesson:

```text
Stored data does nothing unless business logic reads it.
```

---

### Failure Story 5: Deleted link still resolves from stale cache

Later after Redis is added, deleted link still redirects because cache was not invalidated.

Root cause:

```text
Cache consistency issue.
```

Fix:

```text
On delete/block/update, evict Redis key.
Use TTL as safety net.
```

Lesson:

```text
Cache is derived state and must be invalidated.
```

This chapter uses Postgres only, so it avoids this problem for now.

---

## 21. Debugging Mindset

When redirect fails, ask:

```text
What HTTP status is returned?
Is shortCode extracted correctly?
Does row exist in DB?
Is status ACTIVE?
Is expiresAt in the future or null?
Is Location header correct?
Is browser caching an old redirect?
Is the target website itself down?
```

Debug decision tree:

```text
User says short link broken
        |
        v
curl -i short URL
        |
        +-- 302 -> check Location header and target site
        |
        +-- 404 -> DB row missing/deleted/wrong code
        |
        +-- 403 -> blocked
        |
        +-- 410 -> expired
        |
        +-- 500 -> app/DB/unhandled error
```

Useful SQL:

```sql
SELECT id, short_code, long_url, status, expires_at
FROM short_urls
WHERE short_code = 'aB91xZ';
```

Useful log fields:

```text
correlationId
shortCode
result
status
expired
lookupLatencyMs
httpStatus
```

Example structured log:

```json
{
  "event": "redirect_lookup",
  "shortCode": "aB91xZ",
  "result": "REDIRECT",
  "httpStatus": 302,
  "lookupLatencyMs": 12
}
```

Avoid logging full long URL blindly.

Why?

```text
URLs may contain tokens, emails, session IDs, or sensitive query params.
```

Safer:

```text
log host
log URL hash
log URL length
```

---

## 22. Common Mistakes

### Mistake 1: Returning long URL as JSON

Wrong:

```json
{
  "longUrl": "https://example.com"
}
```

Correct for redirect endpoint:

```http
302 Found
Location: https://example.com
```

### Mistake 2: Controller queries repository directly

Wrong:

```text
Controller -> Repository
```

Correct:

```text
Controller -> Service -> Repository
```

### Mistake 3: Ignoring expiry

Wrong:

```text
If row exists, redirect.
```

Correct:

```text
If row exists, check status and expiresAt.
```

### Mistake 4: Using 301 casually

Wrong:

```text
301 because it is redirect.
```

Correct:

```text
Use 302 initially.
```

### Mistake 5: Doing analytics synchronously

Wrong:

```text
Insert analytics before redirect.
```

Correct:

```text
Redirect first.
Async analytics later.
```

### Mistake 6: Using SELECT *

Wrong:

```sql
SELECT * FROM short_urls WHERE short_code = ?
```

Better later:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?
```

### Mistake 7: Not testing Location header

Wrong test:

```text
Only assert status is 302.
```

Better:

```text
Assert status is 302 and Location header is correct.
```

---

## 23. Performance And Scalability Notes

Redirect performance depends on:

```text
1. short_code index
2. DB connection pool health
3. query latency
4. application CPU
5. response creation overhead
6. future cache hit ratio
```

Local v1 target:

```text
p95 redirect < 100-200 ms
```

Production with Redis target later:

```text
p95 redirect < 50 ms on cache hit
```

Metrics to track later:

```text
redirect.count
redirect.latency.p95
redirect.latency.p99
redirect.not_found.count
redirect.expired.count
redirect.blocked.count
db.lookup.latency
cache.hit.ratio later
```

Scaling evolution:

```text
Stage 1:
App -> Postgres

Stage 2:
App -> Redis -> Postgres on miss

Stage 3:
Multiple app pods + Redis cluster + DB read replicas

Stage 4:
Sharding by shortCode
```

ASCII:

```text
Stage 1:

Browser -> App -> Postgres

Stage 2:

Browser -> App -> Redis
              -> Postgres on miss

Stage 3:

Browser -> Load Balancer -> App Pods -> Redis Cluster
                                      -> Postgres Primary/Replica
```

Rule:

```text
Do not add Redis before the DB-backed redirect path is correct.
```

---

## 24. Interview-Ready Explanation

If interviewer asks:

```text
How would you implement the redirect API?
```

Strong answer:

```text
I would expose GET /{shortCode}. The controller extracts the short code and delegates
to a service. The service validates the code shape, looks up the mapping by
short_code, checks lifecycle state such as BLOCKED or DELETED, checks expiresAt,
and returns the long URL only if the link is active and not expired. The controller
then returns HTTP 302 Found with the Location header set to the long URL. For missing
codes I would return 404, for blocked links 403, and for expired links 410. I would
use 302 initially instead of 301 to avoid permanent browser caching surprises. Since
redirect is the hot path, I would keep it minimal, avoid synchronous analytics, and
later add Redis cache in the service layer while keeping PostgreSQL as the source of
truth.
```

Why this is strong:

```text
1. Correct HTTP redirect behavior.
2. Thin controller.
3. Service owns business decision.
4. Proper status/expiry handling.
5. Understands 302 vs 301.
6. Understands hot path.
7. Avoids blocking analytics.
8. Prepares for Redis without premature complexity.
```

Senior one-liner:

```text
The redirect API is the latency-critical read path that resolves a token, validates lifecycle state, and returns a 302 Location response with minimal work.
```

---

## 25. Senior Engineer Checklist

Before moving to Base62 chapter, confirm:

```text
[ ] GET /{shortCode} exists
[ ] Controller returns ResponseEntity<Void>
[ ] Response status is 302 Found
[ ] Location header is set correctly
[ ] Service validates shortCode shape
[ ] Repository looks up by shortCode
[ ] short_code has unique index
[ ] missing code returns 404
[ ] blocked code returns 403
[ ] deleted code returns 404
[ ] expired code returns 410
[ ] active code redirects
[ ] service is readOnly transactional
[ ] no analytics write blocks redirect
[ ] no Redis yet unless later chapter
[ ] curl -i shows correct Location
[ ] tests assert both status and Location
[ ] logs include shortCode/result/latency
[ ] full long URL is not logged carelessly
```

If these are done, the hot read path is correct for v1.

---

## 26. One-Page Cheat Sheet

```text
Endpoint:
GET /{shortCode}

Success:
302 Found
Location: longUrl

Core lookup:
shortCode -> longUrl

Controller role:
extract path variable
call service
return 302 Location

Service role:
validate shortCode
lookup mapping
check status
check expiry
return longUrl

Repository role:
findByShortCode

Database:
Postgres source of truth
UNIQUE index on short_code

HTTP decisions:
not found -> 404
blocked   -> 403
deleted   -> 404
expired   -> 410
active    -> 302

Use:
302 for v1

Avoid:
301 initially
analytics before redirect
controller business logic
SELECT * in optimized hot path
logging sensitive full URLs

Future:
Redis cache
Kafka analytics
rate limiting
observability
```

---

## 27. One Picture To Remember

```text
                     REDIRECT API MENTAL MODEL

                   "Short code becomes Location header"

Browser
  |
  | GET /aB91xZ
  v
+--------------------------------------------------+
| RedirectController                               |
| - extracts shortCode                             |
| - later returns 302                              |
+--------------------------------------------------+
  |
  v
+--------------------------------------------------+
| RedirectService                                  |
| - validate shortCode shape                       |
| - lookup mapping                                 |
| - check status                                   |
| - check expiry                                   |
+--------------------------------------------------+
  |
  v
+--------------------------------------------------+
| ShortUrlRepository                               |
| findByShortCode(aB91xZ)                          |
+--------------------------------------------------+
  |
  v
+--------------------------------------------------+
| PostgreSQL short_urls                            |
| UNIQUE index on short_code                       |
+--------------------------------------------------+
  |
  v
+----------------------+---------------------------+
| Result               | HTTP behavior             |
+----------------------+---------------------------+
| no row               | 404 Not Found             |
| BLOCKED              | 403 Forbidden             |
| DELETED              | 404 Not Found             |
| expired              | 410 Gone                  |
| ACTIVE valid         | 302 Location: longUrl     |
+----------------------+---------------------------+
  |
  v
Browser opens original long URL

FINAL MEMORY:

Redirect API does not return the long URL as data.
It returns an HTTP redirect instruction:
302 + Location header.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Redirect API is the hot read path: shortCode -> longUrl -> 302.
2. The app does not fetch the target page; it returns a Location header.
3. Missing, blocked, deleted, expired, and active links must behave differently.
4. Use 302 initially to avoid permanent caching surprises.
5. Analytics and other side effects must not block redirect.
```

After this chapter, the next natural step is:

```text
007_Base62_Encoding.md
```

Because now we can create and redirect links, and next we should deeply understand how compact short codes are generated.
