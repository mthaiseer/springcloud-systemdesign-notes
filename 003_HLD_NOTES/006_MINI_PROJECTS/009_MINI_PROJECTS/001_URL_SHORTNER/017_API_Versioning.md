# 017_API_Versioning.md
# MiniURLShortener — API Versioning

> Core mental model: **API versioning is a compatibility contract between backend and clients. It lets the server evolve without breaking existing mobile apps, frontend releases, partners, scripts, and integrations.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What Is API Versioning?](#4-what-is-api-versioning)
- [5. Why URL Shortener Needs Versioning](#5-why-url-shortener-needs-versioning)
- [6. Versioning Strategies](#6-versioning-strategies)
- [7. Recommended Strategy For MiniURLShortener](#7-recommended-strategy-for-miniurlshortener)
- [8. URI Versioning Mental Model](#8-uri-versioning-mental-model)
- [9. Controller Package Structure](#9-controller-package-structure)
- [10. V1 Create API Contract](#10-v1-create-api-contract)
- [11. V2 Create API Contract](#11-v2-create-api-contract)
- [12. DTO Evolution Rules](#12-dto-evolution-rules)
- [13. Backward Compatible vs Breaking Changes](#13-backward-compatible-vs-breaking-changes)
- [14. Versioned Controller Code](#14-versioned-controller-code)
- [15. Shared Service With Versioned Adapters](#15-shared-service-with-versioned-adapters)
- [16. Request And Response Mapping](#16-request-and-response-mapping)
- [17. Redirect API Versioning Nuance](#17-redirect-api-versioning-nuance)
- [18. Error Response Versioning](#18-error-response-versioning)
- [19. Deprecation Strategy](#19-deprecation-strategy)
- [20. API Versioning With Gateway](#20-api-versioning-with-gateway)
- [21. Step-by-Step Dry Runs](#21-step-by-step-dry-runs)
- [22. Internal Execution Walkthrough](#22-internal-execution-walkthrough)
- [23. Testing Strategy](#23-testing-strategy)
- [24. Production Failure Stories](#24-production-failure-stories)
- [25. Debugging Mindset](#25-debugging-mindset)
- [26. Common Mistakes](#26-common-mistakes)
- [27. Interview-Ready Explanation](#27-interview-ready-explanation)
- [28. Senior Engineer Checklist](#28-senior-engineer-checklist)
- [29. One-Page Cheat Sheet](#29-one-page-cheat-sheet)
- [30. One Picture To Remember](#30-one-picture-to-remember)

---

## 1. Why This Exists

In the first version of MiniURLShortener, the create API may be simple:

```http
POST /api/v1/urls
```

Request:

```json
{
  "longUrl": "https://example.com/article"
}
```

Response:

```json
{
  "shortCode": "aB12xZ",
  "shortUrl": "https://sho.rt/aB12xZ"
}
```

Later, production requirements grow:

```text
custom aliases
expiration time
campaign source
owner user id
QR code generation
workspace id
password protection
analytics flags
mobile deep-link metadata
bulk URL creation
```

If you change the existing API carelessly, old clients break.

Example breaking change:

```json
// Old response field
{
  "shortUrl": "https://sho.rt/aB12xZ"
}
```

Changed to:

```json
// New response field
{
  "redirectUrl": "https://sho.rt/aB12xZ"
}
```

Old frontend expects `shortUrl`.

Result:

```text
Frontend shows blank link.
Mobile app crashes.
Partner integration fails.
Support tickets increase.
Rollback becomes urgent.
```

API versioning exists to solve this.

```text
Without versioning:
    backend changes can break clients suddenly.

With versioning:
    backend can evolve while old clients keep working.
```

Production memory:

```text
API versioning is not about URLs.
It is about trust.
```

---

## 2. The One Core Mental Model

The core mental model:

```text
API VERSION = CLIENT CONTRACT
```

A version is a promise.

```text
/api/v1/urls means:
    clients know request shape
    clients know response shape
    clients know error shape
    clients know behavior

/api/v2/urls means:
    new contract may exist
    old contract still survives until deprecated
```

ASCII:

```text
                  CLIENTS
                     |
       +-------------+-------------+
       |                           |
       v                           v
+----------------+          +----------------+
| Old Mobile App |          | New Web App    |
| uses /v1       |          | uses /v2       |
+----------------+          +----------------+
       |                           |
       v                           v
+-----------------------------------------+
|             Backend API                 |
|                                         |
|  /api/v1/urls  -> old contract adapter  |
|  /api/v2/urls  -> new contract adapter  |
+-----------------------------------------+
                     |
                     v
+-----------------------------------------+
|          Shared Business Service        |
|          create short URL               |
+-----------------------------------------+
```

One-line memory:

```text
Version at the edge, share logic in the core.
```

That means:

```text
Controller/DTO can be versioned.
Business rules should be reused where possible.
Domain model should not duplicate blindly for every API version.
```

---

## 3. Problem Statement

Design API versioning for MiniURLShortener.

It must support:

```text
1. Current stable V1 create API.
2. Future V2 create API with extra fields.
3. Old clients continuing to use V1.
4. New clients using V2.
5. Shared service logic where possible.
6. Versioned DTOs.
7. Clear deprecation strategy.
8. Test coverage for both versions.
9. Safe error response compatibility.
10. Gateway-friendly routing.
```

It should avoid:

```text
breaking old response fields
removing fields without migration
changing field meaning silently
putting version logic everywhere
copy-pasting entire services unnecessarily
versioning database tables too early
making redirect short links ugly
```

Out of scope:

```text
GraphQL schema evolution
full OpenAPI generation
consumer-driven contract testing deep dive
multi-tenant API gateway implementation
```

---

## 4. What Is API Versioning?

API versioning means exposing different API contracts for different client generations.

A contract includes:

```text
URL path
HTTP method
request fields
response fields
error format
status codes
business behavior
validation rules
pagination format
sorting/filtering behavior
```

Example:

```text
V1:
POST /api/v1/urls
Request: longUrl, customAlias, expiresAt
Response: shortCode, shortUrl

V2:
POST /api/v2/urls
Request: longUrl, alias, expiresAt, tags, analyticsEnabled
Response: id, code, shortUrl, analyticsUrl, createdAt
```

Important:

```text
Versioning is not only when URL changes.
A breaking behavior change also needs versioning.
```

Example behavior change:

```text
V1 allows http:// links.
V2 only allows https:// links.
```

This may break clients sending HTTP links.

So it may need a new version or controlled rollout.

---

## 5. Why URL Shortener Needs Versioning

A URL shortener looks simple, but the API can evolve quickly.

Initial API:

```text
create short URL
redirect short URL
```

Future features:

```text
custom aliases
expiration
user ownership
workspaces
analytics
QR codes
bulk creation
password protection
abuse metadata
country-based routing
A/B testing URLs
```

ASCII evolution:

```text
Phase 1:
longUrl -> shortCode

Phase 2:
longUrl + customAlias + expiresAt -> shortUrl

Phase 3:
longUrl + workspace + analytics + tags -> rich link object

Phase 4:
bulk requests + async creation + tracking -> job based API
```

Without API versioning, every feature risks breaking clients.

Typical clients:

```text
web frontend
mobile app
browser extension
partner API consumer
internal admin dashboard
scheduled scripts
CLI tools
```

Mobile apps are especially important.

```text
Backend deploys today.
Mobile users may update app weeks later.
```

So old API versions must live longer.

---

## 6. Versioning Strategies

There are four common strategies.

### Strategy 1: URI Path Versioning

```http
/api/v1/urls
/api/v2/urls
```

Pros:

```text
simple
visible
easy to test
easy for gateway routing
easy for logs
beginner-friendly
```

Cons:

```text
version is in URL
some REST purists dislike it
```

### Strategy 2: Header Versioning

```http
GET /api/urls
X-API-Version: 1
```

Pros:

```text
clean URL
version metadata separated from resource
```

Cons:

```text
less visible
harder to test manually
harder for simple clients
routing may be less obvious
```

### Strategy 3: Media Type Versioning

```http
Accept: application/vnd.miniurl.v1+json
```

Pros:

```text
very explicit content negotiation
useful for public enterprise APIs
```

Cons:

```text
complex for many teams
harder for beginners
more annoying with browser/manual tests
```

### Strategy 4: Query Parameter Versioning

```http
/api/urls?version=1
```

Pros:

```text
easy to try
```

Cons:

```text
less clean
can be cached incorrectly
not ideal as primary strategy
```

Comparison:

```text
+-------------------+------------+--------------+------------------+
| Strategy          | Visibility | Gateway Easy | Recommended Here |
+-------------------+------------+--------------+------------------+
| URI /v1           | High       | High         | Yes              |
| Header            | Medium     | Medium       | Later possible   |
| Media Type        | Low        | Medium       | Advanced         |
| Query Param       | Medium     | Low          | No               |
+-------------------+------------+--------------+------------------+
```

---

## 7. Recommended Strategy For MiniURLShortener

Use URI path versioning for APIs:

```http
/api/v1/urls
/api/v2/urls
```

But keep public redirect links unversioned:

```http
/{shortCode}
```

Why?

```text
API endpoints are contracts for API clients.
Short links are user-facing resources.
```

Good:

```http
POST /api/v1/urls
GET /api/v1/urls/{id}
GET /abc123
```

Bad:

```http
GET /api/v1/r/abc123
GET /v1/abc123
```

Why bad?

```text
Short links should stay short.
A user-facing redirect link should not expose backend API version.
```

Recommended structure:

```text
API management endpoints:
    /api/v1/urls
    /api/v2/urls

Public redirect endpoint:
    /{shortCode}
```

ASCII:

```text
Admin/API Clients
       |
       v
/api/v1/urls  /api/v2/urls
       |
       v
Create and manage links

End Users
       |
       v
/abc123
       |
       v
Redirect to longUrl
```

---

## 8. URI Versioning Mental Model

URI versioning means the first part of the API path tells which contract to use.

```text
/api/v1/urls
     ^^
     contract version
```

Request flow:

```text
HTTP request
   |
   v
Path matching
   |
   +-- /api/v1/urls --> V1 controller
   |
   +-- /api/v2/urls --> V2 controller
   |
   +-- /abc123      --> Redirect controller
```

ASCII:

```text
                 Spring DispatcherServlet
                           |
        +------------------+------------------+
        |                  |                  |
        v                  v                  v
+----------------+  +----------------+  +----------------+
| V1 Controller  |  | V2 Controller  |  | Redirect Ctrl   |
| /api/v1/urls   |  | /api/v2/urls   |  | /{shortCode}    |
+----------------+  +----------------+  +----------------+
        |                  |                  |
        v                  v                  v
+--------------------------------------------------------+
|                 Shared URL Service                    |
+--------------------------------------------------------+
```

Important design:

```text
Version controllers and DTOs.
Do not duplicate core business logic unless behavior truly differs.
```

---

## 9. Controller Package Structure

Recommended package structure:

```text
src/main/java/com/miniurl/shortener/

url/
  controller/
    v1/
      UrlControllerV1.java
      CreateShortUrlRequestV1.java
      CreateShortUrlResponseV1.java
    v2/
      UrlControllerV2.java
      CreateShortUrlRequestV2.java
      CreateShortUrlResponseV2.java

  service/
    UrlService.java
    command/
      CreateShortUrlCommand.java
    result/
      CreateShortUrlResult.java

  repository/
    ShortUrlRepository.java

  entity/
    ShortUrlEntity.java
```

Why this structure is strong:

```text
1. API contracts are isolated by version.
2. Service receives internal command, not external DTO.
3. Domain logic stays stable.
4. V1 and V2 can map differently.
5. Testing becomes clean.
```

Bad structure:

```text
UrlServiceV1
UrlServiceV2
UrlRepositoryV1
UrlRepositoryV2
ShortUrlEntityV1
ShortUrlEntityV2
```

This creates duplication too early.

Better rule:

```text
Version the edge first.
Version the core only when behavior truly diverges.
```

---

## 10. V1 Create API Contract

V1 request:

```json
{
  "longUrl": "https://example.com/article",
  "customAlias": "article1",
  "expiresAt": "2026-12-31T23:59:59Z"
}
```

V1 response:

```json
{
  "shortCode": "article1",
  "shortUrl": "https://sho.rt/article1"
}
```

V1 behavior:

```text
1. longUrl required.
2. customAlias optional.
3. expiresAt optional.
4. response is minimal.
5. analytics not exposed.
```

V1 DTO:

```java
package com.miniurl.shortener.url.controller.v1;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public class CreateShortUrlRequestV1 {

    @NotBlank(message = "longUrl is required")
    @Size(max = 2048, message = "longUrl must not exceed 2048 characters")
    private String longUrl;

    @Size(min = 4, max = 32, message = "customAlias must be between 4 and 32 characters")
    private String customAlias;

    @Future(message = "expiresAt must be in the future")
    private Instant expiresAt;

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
```

Response DTO:

```java
package com.miniurl.shortener.url.controller.v1;

public class CreateShortUrlResponseV1 {

    private final String shortCode;
    private final String shortUrl;

    public CreateShortUrlResponseV1(String shortCode, String shortUrl) {
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }
}
```

---

## 11. V2 Create API Contract

V2 may add fields:

```json
{
  "longUrl": "https://example.com/article",
  "alias": "article1",
  "expiresAt": "2026-12-31T23:59:59Z",
  "analyticsEnabled": true,
  "tags": ["marketing", "springboot"]
}
```

V2 response:

```json
{
  "id": "01HXURLABC123",
  "code": "article1",
  "shortUrl": "https://sho.rt/article1",
  "analyticsEnabled": true,
  "createdAt": "2026-06-23T10:00:00Z"
}
```

Notice differences:

```text
V1 uses customAlias.
V2 uses alias.
V1 returns shortCode.
V2 returns code.
V2 returns more metadata.
```

This is exactly why versioning helps.

V2 DTO:

```java
package com.miniurl.shortener.url.controller.v2;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public class CreateShortUrlRequestV2 {

    @NotBlank(message = "longUrl is required")
    @Size(max = 2048, message = "longUrl must not exceed 2048 characters")
    private String longUrl;

    @Size(min = 4, max = 32, message = "alias must be between 4 and 32 characters")
    private String alias;

    @Future(message = "expiresAt must be in the future")
    private Instant expiresAt;

    private Boolean analyticsEnabled;

    @Size(max = 10, message = "maximum 10 tags are allowed")
    private List<String> tags;

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getAnalyticsEnabled() {
        return analyticsEnabled;
    }

    public void setAnalyticsEnabled(Boolean analyticsEnabled) {
        this.analyticsEnabled = analyticsEnabled;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
```

V2 response:

```java
package com.miniurl.shortener.url.controller.v2;

import java.time.Instant;

public class CreateShortUrlResponseV2 {

    private final String id;
    private final String code;
    private final String shortUrl;
    private final boolean analyticsEnabled;
    private final Instant createdAt;

    public CreateShortUrlResponseV2(
            String id,
            String code,
            String shortUrl,
            boolean analyticsEnabled,
            Instant createdAt
    ) {
        this.id = id;
        this.code = code;
        this.shortUrl = shortUrl;
        this.analyticsEnabled = analyticsEnabled;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public boolean isAnalyticsEnabled() {
        return analyticsEnabled;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
```

---

## 12. DTO Evolution Rules

Safe changes usually include:

```text
adding optional request fields
adding response fields
adding new endpoints
adding new enum value if clients tolerate unknowns
making validation more permissive
```

Dangerous changes include:

```text
removing request fields
renaming response fields
changing field type
making optional field required
changing meaning of a field
changing status code behavior
changing error response shape
changing pagination format
```

ASCII:

```text
Existing Client expects:
    shortUrl: string

Backend changes to:
    shortUrl: object

Client parser:
    boom
```

Example safe response addition:

```json
{
  "shortCode": "abc123",
  "shortUrl": "https://sho.rt/abc123",
  "createdAt": "2026-06-23T10:00:00Z"
}
```

Old clients usually ignore unknown fields.

Example breaking rename:

```json
{
  "code": "abc123",
  "url": "https://sho.rt/abc123"
}
```

Old clients expecting `shortCode` and `shortUrl` break.

Rule:

```text
Additive changes are usually safe.
Mutating existing meaning is dangerous.
```

---

## 13. Backward Compatible vs Breaking Changes

Backward compatible means old clients keep working.

Breaking means old clients may fail.

Table:

```text
+------------------------------------------+-----------------------+
| Change                                   | Type                  |
+------------------------------------------+-----------------------+
| Add optional request field               | Usually compatible    |
| Add response field                       | Usually compatible    |
| Add new endpoint                         | Compatible            |
| Remove response field                    | Breaking              |
| Rename response field                    | Breaking              |
| Change string to object                  | Breaking              |
| Make optional request field required     | Breaking              |
| Change 404 to 200 with error body        | Breaking              |
| Change error code names                  | Breaking              |
| Tighten validation rules silently        | Potentially breaking  |
+------------------------------------------+-----------------------+
```

MiniURLShortener examples:

```text
Compatible:
    add createdAt to V1 response

Breaking:
    rename shortCode to code in V1

Compatible:
    add /api/v1/urls/{id}/analytics

Breaking:
    change POST /api/v1/urls response from 201 to 202
```

Production decision:

```text
If old clients can break, create a new version or run a migration plan.
```

---

## 14. Versioned Controller Code

V1 controller:

```java
package com.miniurl.shortener.url.controller.v1;

import com.miniurl.shortener.url.service.UrlService;
import com.miniurl.shortener.url.service.command.CreateShortUrlCommand;
import com.miniurl.shortener.url.service.result.CreateShortUrlResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlControllerV1 {

    private final UrlService urlService;

    public UrlControllerV1(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateShortUrlResponseV1 createShortUrl(
            @Valid @RequestBody CreateShortUrlRequestV1 request
    ) {
        CreateShortUrlCommand command = new CreateShortUrlCommand(
                request.getLongUrl(),
                request.getCustomAlias(),
                request.getExpiresAt(),
                false
        );

        CreateShortUrlResult result = urlService.createShortUrl(command);

        return new CreateShortUrlResponseV1(
                result.shortCode(),
                result.shortUrl()
        );
    }
}
```

V2 controller:

```java
package com.miniurl.shortener.url.controller.v2;

import com.miniurl.shortener.url.service.UrlService;
import com.miniurl.shortener.url.service.command.CreateShortUrlCommand;
import com.miniurl.shortener.url.service.result.CreateShortUrlResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/urls")
public class UrlControllerV2 {

    private final UrlService urlService;

    public UrlControllerV2(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateShortUrlResponseV2 createShortUrl(
            @Valid @RequestBody CreateShortUrlRequestV2 request
    ) {
        boolean analyticsEnabled = Boolean.TRUE.equals(request.getAnalyticsEnabled());

        CreateShortUrlCommand command = new CreateShortUrlCommand(
                request.getLongUrl(),
                request.getAlias(),
                request.getExpiresAt(),
                analyticsEnabled
        );

        CreateShortUrlResult result = urlService.createShortUrl(command);

        return new CreateShortUrlResponseV2(
                result.id(),
                result.shortCode(),
                result.shortUrl(),
                result.analyticsEnabled(),
                result.createdAt()
        );
    }
}
```

Important:

```text
Both controllers call same service.
Both map external DTO to internal command.
Each returns its own response shape.
```

---

## 15. Shared Service With Versioned Adapters

Internal command:

```java
package com.miniurl.shortener.url.service.command;

import java.time.Instant;

public record CreateShortUrlCommand(
        String longUrl,
        String customAlias,
        Instant expiresAt,
        boolean analyticsEnabled
) {
}
```

Internal result:

```java
package com.miniurl.shortener.url.service.result;

import java.time.Instant;

public record CreateShortUrlResult(
        String id,
        String shortCode,
        String shortUrl,
        boolean analyticsEnabled,
        Instant createdAt
) {
}
```

Shared service:

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.url.service.command.CreateShortUrlCommand;
import com.miniurl.shortener.url.service.result.CreateShortUrlResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlService {

    private final UrlValidatorService validatorService;
    private final ShortCodeGenerator shortCodeGenerator;
    private final ShortUrlRepository shortUrlRepository;
    private final ShortUrlFactory shortUrlFactory;

    public UrlService(
            UrlValidatorService validatorService,
            ShortCodeGenerator shortCodeGenerator,
            ShortUrlRepository shortUrlRepository,
            ShortUrlFactory shortUrlFactory
    ) {
        this.validatorService = validatorService;
        this.shortCodeGenerator = shortCodeGenerator;
        this.shortUrlRepository = shortUrlRepository;
        this.shortUrlFactory = shortUrlFactory;
    }

    @Transactional
    public CreateShortUrlResult createShortUrl(CreateShortUrlCommand command) {
        validatorService.validateLongUrl(command.longUrl());

        String shortCode = command.customAlias();
        if (shortCode == null || shortCode.isBlank()) {
            shortCode = shortCodeGenerator.generate();
        } else {
            validatorService.validateCustomAlias(shortCode);
        }

        ShortUrlEntity entity = shortUrlFactory.create(
                command.longUrl(),
                shortCode,
                command.expiresAt(),
                command.analyticsEnabled()
        );

        ShortUrlEntity saved = shortUrlRepository.save(entity);

        return new CreateShortUrlResult(
                saved.getId().toString(),
                saved.getShortCode(),
                "https://sho.rt/" + saved.getShortCode(),
                saved.isAnalyticsEnabled(),
                saved.getCreatedAt()
        );
    }
}
```

Why this design is clean:

```text
Controllers know API version.
Service knows business use case.
Repository knows persistence.
Database knows storage.
```

ASCII:

```text
V1 Request DTO       V2 Request DTO
     |                    |
     v                    v
V1 Controller        V2 Controller
     |                    |
     +---------+----------+
               |
               v
     CreateShortUrlCommand
               |
               v
          UrlService
               |
               v
          Repository
               |
               v
             DB
```

---

## 16. Request And Response Mapping

Mapping prevents external API contracts from leaking into service logic.

Bad:

```java
public CreateShortUrlResponseV1 createShortUrl(CreateShortUrlRequestV1 request) {
    // service depends on V1 DTO
}
```

Why bad?

```text
Service becomes tied to V1.
V2 requires duplicate service method.
Testing service needs API DTOs.
Domain logic becomes polluted by transport contract.
```

Better:

```text
Controller maps request DTO -> command.
Service returns result.
Controller maps result -> response DTO.
```

ASCII:

```text
External world
    |
    v
Request DTO  <--- versioned
    |
    v
Command      <--- internal stable shape
    |
    v
Service      <--- business logic
    |
    v
Result       <--- internal stable output
    |
    v
Response DTO <--- versioned
```

This is the adapter pattern in simple form.

```text
Versioned controllers adapt external contracts to internal use cases.
```

---

## 17. Redirect API Versioning Nuance

The redirect API is special.

User-facing short link:

```http
GET /abc123
```

This should usually stay unversioned.

Why?

```text
1. Short links must remain short.
2. Links may be printed in QR codes.
3. Links may be shared in emails.
4. Links should survive backend API evolution.
5. Users do not care about API version.
```

Do not do this:

```http
GET /api/v1/r/abc123
```

For management APIs, version is good:

```http
GET /api/v1/urls/{id}
GET /api/v2/urls/{id}
```

For public redirect, stable path is better:

```http
GET /{shortCode}
```

ASCII:

```text
Public User:
    /abc123 ---------------> RedirectController

API Client:
    /api/v1/urls ----------> UrlControllerV1
    /api/v2/urls ----------> UrlControllerV2
```

If redirect behavior changes, handle carefully.

Example:

```text
V1 redirect returns 302.
Future redirect may use 307 for some methods.
```

For simple browser redirects, keep behavior stable.

---

## 18. Error Response Versioning

Error response is also an API contract.

V1 error:

```json
{
  "timestamp": "2026-06-23T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "INVALID_URL",
  "message": "longUrl must start with http or https",
  "path": "/api/v1/urls"
}
```

If you rename `code` to `errorCode`, clients may break.

Bad silent change:

```json
{
  "errorCode": "INVALID_URL"
}
```

Better:

```text
Keep V1 error shape stable.
If V2 needs new error shape, version it deliberately.
```

Possible V2 error:

```json
{
  "type": "https://docs.miniurl/errors/invalid-url",
  "title": "Invalid URL",
  "status": 400,
  "detail": "longUrl must start with http or https",
  "instance": "/api/v2/urls",
  "code": "INVALID_URL"
}
```

But do not change V1 unexpectedly.

Rule:

```text
Error shape is part of the contract.
Treat it like response DTO.
```

---

## 19. Deprecation Strategy

Versioning is incomplete without deprecation.

Deprecation means:

```text
A version still works, but clients should migrate away from it.
```

Good lifecycle:

```text
1. Release V1.
2. Add V2 while V1 remains active.
3. Announce V1 deprecation date.
4. Add deprecation headers to V1 responses.
5. Monitor V1 traffic.
6. Help clients migrate.
7. Disable V1 only after agreed sunset.
```

Headers:

```http
Deprecation: true
Sunset: Wed, 31 Dec 2026 23:59:59 GMT
Link: <https://docs.miniurl.com/migration/v1-to-v2>; rel="deprecation"
```

ASCII:

```text
Time ------------------------------------------------------>

V1 Active       V1 Deprecated                 V1 Removed
|---------------|-----------------------------|

        V2 Released and supported
        |-------------------------------------->
```

Production rule:

```text
Never remove old API just because new API exists.
Remove only after migration evidence and communication.
```

---

## 20. API Versioning With Gateway

In production, a gateway may sit before the service.

```text
Client
  |
  v
API Gateway
  |
  +-- /api/v1/urls -> miniurl-service v1 route
  +-- /api/v2/urls -> miniurl-service v2 route
  +-- /abc123      -> redirect route
```

Gateway can help with:

```text
routing
rate limiting per version
authentication
deprecation headers
traffic metrics
migration monitoring
canary routing
```

ASCII:

```text
+--------+      +-------------+      +------------------+
| Client | ---> | API Gateway | ---> | MiniURL Service  |
+--------+      +-------------+      +------------------+
                     |
        +------------+-------------+
        |            |             |
        v            v             v
     /api/v1      /api/v2       /{code}
```

Useful metrics:

```text
requests by api_version
errors by api_version
latency by api_version
client_id by api_version
V1 usage trend
```

This tells when it is safe to sunset V1.

---

## 21. Step-by-Step Dry Runs

### Dry Run 1: Old client uses V1

Request:

```http
POST /api/v1/urls
```

Body:

```json
{
  "longUrl": "https://example.com"
}
```

Flow:

```text
1. DispatcherServlet matches /api/v1/urls.
2. UrlControllerV1 handles request.
3. V1 DTO validation runs.
4. Controller maps V1 DTO to internal command.
5. UrlService creates short URL.
6. Service returns internal result.
7. V1 controller maps result to V1 response.
8. Client receives shortCode and shortUrl only.
```

Response:

```json
{
  "shortCode": "aB12xZ",
  "shortUrl": "https://sho.rt/aB12xZ"
}
```

---

### Dry Run 2: New client uses V2

Request:

```http
POST /api/v2/urls
```

Body:

```json
{
  "longUrl": "https://example.com",
  "alias": "hello1",
  "analyticsEnabled": true
}
```

Flow:

```text
1. DispatcherServlet matches /api/v2/urls.
2. UrlControllerV2 handles request.
3. V2 DTO validation runs.
4. alias maps to internal customAlias.
5. analyticsEnabled maps into command.
6. Shared service creates short URL.
7. V2 controller returns richer response.
```

Response:

```json
{
  "id": "01HXURLABC123",
  "code": "hello1",
  "shortUrl": "https://sho.rt/hello1",
  "analyticsEnabled": true,
  "createdAt": "2026-06-23T10:00:00Z"
}
```

---

### Dry Run 3: V1 client sends V2 field

Request:

```json
{
  "longUrl": "https://example.com",
  "alias": "hello1"
}
```

To:

```http
POST /api/v1/urls
```

What happens?

```text
1. V1 DTO does not define alias.
2. Jackson usually ignores unknown fields by default in Spring Boot.
3. customAlias is null.
4. Service generates random short code.
5. Client may be surprised.
```

Lesson:

```text
Clients must use the correct version contract.
API docs and tests matter.
```

Optional strict mode can reject unknown fields, but that is a design choice.

---

### Dry Run 4: V1 deprecated but still working

Request:

```http
POST /api/v1/urls
```

Response headers:

```http
Deprecation: true
Sunset: Wed, 31 Dec 2026 23:59:59 GMT
```

Flow:

```text
1. V1 still works.
2. Client receives normal response.
3. Headers warn client to migrate.
4. Gateway logs V1 traffic.
5. Team tracks remaining clients.
```

Lesson:

```text
Deprecation should warn, not suddenly break.
```

---

## 22. Internal Execution Walkthrough

Full request path:

```text
1. Client sends HTTP request.
2. API Gateway receives request.
3. Gateway routes based on path.
4. Spring DispatcherServlet receives request.
5. RequestMapping selects V1 or V2 controller.
6. Jackson deserializes JSON into versioned DTO.
7. Bean Validation validates versioned DTO.
8. Controller maps DTO to internal command.
9. Service executes use case.
10. Repository writes to database.
11. Service returns internal result.
12. Controller maps result to versioned response DTO.
13. Jackson serializes response JSON.
14. Client receives version-specific contract.
```

ASCII:

```text
Client
  |
  v
Gateway
  |
  v
DispatcherServlet
  |
  +-- /api/v1/urls --> V1 DTO -> V1 Controller --+
  |                                                |
  +-- /api/v2/urls --> V2 DTO -> V2 Controller --+
                                                   |
                                                   v
                                      CreateShortUrlCommand
                                                   |
                                                   v
                                             UrlService
                                                   |
                                                   v
                                             Repository
                                                   |
                                                   v
                                                 DB
                                                   |
                                                   v
                                      CreateShortUrlResult
                                                   |
                     +-----------------------------+
                     |
       +-------------+-------------+
       |                           |
       v                           v
 V1 Response DTO              V2 Response DTO
```

The important senior idea:

```text
Versioning belongs at API boundary.
The core use case should stay stable and reusable.
```

---

## 23. Testing Strategy

Test each version as a separate contract.

### V1 tests

```text
POST /api/v1/urls returns shortCode and shortUrl
V1 accepts customAlias
V1 does not require analyticsEnabled
V1 validation messages remain stable
V1 error response shape remains stable
```

### V2 tests

```text
POST /api/v2/urls returns id, code, shortUrl, analyticsEnabled, createdAt
V2 accepts alias
V2 supports analyticsEnabled
V2 validation messages for alias are correct
V2 error response shape is correct
```

### Compatibility tests

```text
V1 still works after V2 is added
V2 change does not alter V1 response
V1 old clients still parse response
V1 error code names remain unchanged
```

MockMvc example:

```java
mockMvc.perform(post("/api/v1/urls")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"longUrl\":\"https://example.com\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.shortCode").exists())
        .andExpect(jsonPath("$.shortUrl").exists())
        .andExpect(jsonPath("$.code").doesNotExist());
```

V2 test:

```java
mockMvc.perform(post("/api/v2/urls")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"longUrl\":\"https://example.com\",\"analyticsEnabled\":true}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.code").exists())
        .andExpect(jsonPath("$.analyticsEnabled").value(true));
```

Testing rule:

```text
A version is not safe until tests prove its contract remains stable.
```

---

## 24. Production Failure Stories

### Failure Story 1: Field renamed without version bump

Backend changed:

```text
shortCode -> code
```

Frontend still expected:

```text
shortCode
```

Result:

```text
New links created successfully.
UI displayed blank short code.
Support thought creation failed.
```

Root cause:

```text
Breaking response change inside same API version.
```

Fix:

```text
Restore old field in V1.
Add new field in V2.
Add contract tests.
```

Lesson:

```text
Renaming fields is breaking.
```

---

### Failure Story 2: Mobile app broke after validation tightened

V1 accepted:

```text
http://example.com
```

Backend changed V1 to require:

```text
https://example.com
```

Old mobile app still sent HTTP links.

Result:

```text
Sudden spike in 400 errors.
Mobile users could not create links.
```

Root cause:

```text
Validation behavior changed silently.
```

Fix:

```text
Keep V1 behavior.
Apply stricter rule in V2.
Monitor usage.
```

Lesson:

```text
Behavior is part of the API contract.
```

---

### Failure Story 3: V1 removed too early

Team released V2 and deleted V1 controllers.

Some partner scripts still used:

```http
/api/v1/urls
```

Result:

```text
Partner integrations failed with 404.
Business escalation happened.
```

Root cause:

```text
No deprecation lifecycle and no traffic monitoring.
```

Fix:

```text
Restore V1.
Add Sunset header.
Track traffic by version.
Communicate migration date.
```

Lesson:

```text
Deprecation is a process, not a code deletion.
```

---

### Failure Story 4: Service duplicated for every version

Team created:

```text
UrlServiceV1
UrlServiceV2
UrlRepositoryV1
UrlRepositoryV2
```

Bug fix applied only to V2.

V1 still had security bug.

Root cause:

```text
Too much duplication between versions.
```

Fix:

```text
Version controller/DTO only.
Share service and domain logic where possible.
```

Lesson:

```text
Duplicate contracts when needed, not business bugs.
```

---

## 25. Debugging Mindset

When a versioning issue occurs, ask:

```text
Which API version is affected?
Which client is affected?
Did request route to correct controller?
Did DTO mapping change?
Did response shape change?
Did error shape change?
Was validation tightened?
Was behavior changed silently?
Are gateway routes correct?
Are deprecation headers present?
What percentage of traffic still uses old version?
```

Debug map:

```text
404 on /api/v1/urls:
    V1 controller missing or gateway route wrong

V1 response has V2 fields only:
    wrong controller mapping or shared response DTO leak

Old client parse error:
    response field removed, renamed, or type changed

Spike in 400 for old clients:
    validation changed in same version

Partner still uses deprecated version:
    migration communication or traffic monitoring issue
```

Useful logs:

```text
apiVersion
path
clientId
userAgent
status
errorCode
latencyMs
controllerName
```

Golden rule:

```text
Always debug API bugs from the client contract backward.
```

---

## 26. Common Mistakes

### Mistake 1: Versioning too late

Wrong:

```text
Keep changing /api/urls forever.
```

Correct:

```text
Start with /api/v1/urls for public or serious APIs.
```

### Mistake 2: Versioning everything

Wrong:

```text
UrlEntityV1, UrlEntityV2, UrlRepositoryV1, UrlRepositoryV2 for simple field additions.
```

Correct:

```text
Version the API boundary first.
```

### Mistake 3: Breaking old responses

Wrong:

```text
Rename shortUrl to url inside V1.
```

Correct:

```text
Keep shortUrl in V1. Add new response in V2.
```

### Mistake 4: Forgetting error response compatibility

Wrong:

```text
Change error code field from code to errorCode in V1.
```

Correct:

```text
Keep V1 error shape stable.
```

### Mistake 5: Versioning public redirect links

Wrong:

```text
/v1/abc123
```

Correct:

```text
/abc123
```

### Mistake 6: No deprecation plan

Wrong:

```text
Delete V1 after V2 release.
```

Correct:

```text
Deprecate, monitor, migrate, then sunset.
```

### Mistake 7: No contract tests

Wrong:

```text
Only service unit tests.
```

Correct:

```text
MockMvc/API tests for each version response shape.
```

---

## 27. Interview-Ready Explanation

If interviewer asks:

```text
How would you version APIs in a URL shortener?
```

Strong answer:

```text
I would use URI path versioning for API management endpoints, such as
/api/v1/urls and /api/v2/urls, because it is simple, visible, gateway-friendly,
and easy to test. I would not version the public redirect link like /abc123,
because short links are user-facing resources and should stay stable. I would
version controllers and DTOs at the API boundary, then map them into internal
commands and shared service logic. This prevents external contract changes from
polluting the domain layer. I would treat request fields, response fields, error
shape, status codes, and validation behavior as part of the API contract. Additive
changes may stay in the same version, but breaking changes like renaming fields,
changing types, removing fields, or tightening validation unexpectedly should go
into a new version or a controlled migration. Finally, I would use deprecation
headers, traffic metrics by version, and contract tests before sunsetting old
versions.
```

Why this is senior:

```text
1. Separates API boundary from domain core.
2. Mentions redirect URL nuance.
3. Understands backward compatibility.
4. Includes error contracts.
5. Includes gateway and testing.
6. Includes deprecation lifecycle.
```

Senior one-liner:

```text
I version the contract at the edge, not the entire application blindly.
```

---

## 28. Senior Engineer Checklist

Before finishing this chapter, confirm:

```text
[ ] API endpoints use /api/v1 prefix
[ ] Future V2 can be added without breaking V1
[ ] Public redirect endpoint remains unversioned
[ ] V1 request DTO is separate
[ ] V1 response DTO is separate
[ ] V2 request DTO can evolve separately
[ ] V2 response DTO can evolve separately
[ ] Controllers map DTOs to internal commands
[ ] Service does not depend on versioned DTOs
[ ] Error response compatibility is considered
[ ] Breaking changes are identified clearly
[ ] Additive changes are understood
[ ] Deprecation headers are understood
[ ] Gateway routing impact is understood
[ ] Tests assert response shape per version
[ ] Logs can include apiVersion
[ ] V1 traffic can be monitored before sunset
```

If these are true, your API versioning model is production-shaped.

---

## 29. One-Page Cheat Sheet

```text
Core mental model:
API version = client contract.

Recommended MiniURLShortener style:
/api/v1/urls
/api/v2/urls
/{shortCode} stays unversioned

Version at boundary:
controller
request DTO
response DTO
error contract if needed

Avoid unnecessary duplication:
service shared where possible
repository shared where possible
entity shared where possible

Compatible changes:
add optional request field
add response field
add new endpoint

Breaking changes:
rename field
remove field
change field type
make optional field required
change error shape
change status code meaning
tighten validation silently

Deprecation lifecycle:
release new version
keep old version running
add deprecation headers
monitor traffic
communicate sunset
remove after migration

Testing:
test V1 response shape
test V2 response shape
test old clients still work
test error codes stay stable
```

---

## 30. One Picture To Remember

```text
                  API VERSIONING MENTAL MODEL

                       "Version the contract"

                           CLIENTS
                              |
             +----------------+----------------+
             |                                 |
             v                                 v
   +--------------------+            +--------------------+
   | Old Client         |            | New Client         |
   | knows V1 contract  |            | knows V2 contract  |
   +--------------------+            +--------------------+
             |                                 |
             v                                 v
      /api/v1/urls                       /api/v2/urls
             |                                 |
             v                                 v
   +--------------------+            +--------------------+
   | V1 Controller      |            | V2 Controller      |
   | V1 Request DTO     |            | V2 Request DTO     |
   | V1 Response DTO    |            | V2 Response DTO    |
   +--------------------+            +--------------------+
             |                                 |
             +---------------+-----------------+
                             |
                             v
                   Internal Command/Result
                             |
                             v
                       Shared Service
                             |
                             v
                       Repository + DB

Public redirect link:

    /abc123  ---> RedirectController ---> longUrl

Do not make it:

    /v1/abc123

FINAL MEMORY:

Version the API edge.
Keep the business core stable.
Never break old clients silently.
Deprecate with data, not hope.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. API versioning is a compatibility contract with clients.
2. URI path versioning is simple and strong for MiniURLShortener APIs.
3. Public short links should usually stay unversioned.
4. Version controllers and DTOs first; share service logic when behavior is same.
5. Breaking changes need a new version or a controlled migration plan.
```

Next chapter:

```text
018_Testing_Strategy.md
```
