# 005_Create_Short_URL_API.md
# MiniURLShortener — Create Short URL API

> Core mental model: **The Create Short URL API is the write path that safely converts a valid long URL into a durable, unique, shareable short code. It is not just a POST endpoint; it is validation + token decision + uniqueness enforcement + persistence + clean response.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. API Contract](#4-api-contract)
- [5. Create Flow Big Picture](#5-create-flow-big-picture)
- [6. Request DTO Design](#6-request-dto-design)
- [7. Response DTO Design](#7-response-dto-design)
- [8. Validation Mental Model](#8-validation-mental-model)
- [9. Controller Design](#9-controller-design)
- [10. Service Design](#10-service-design)
- [11. Short Code Generation Placeholder](#11-short-code-generation-placeholder)
- [12. Custom Alias Handling](#12-custom-alias-handling)
- [13. Repository And Entity Usage](#13-repository-and-entity-usage)
- [14. Database Uniqueness Handling](#14-database-uniqueness-handling)
- [15. Transaction Boundary](#15-transaction-boundary)
- [16. Error Handling Model](#16-error-handling-model)
- [17. Full Code Skeleton](#17-full-code-skeleton)
- [18. Step-by-Step Dry Runs](#18-step-by-step-dry-runs)
- [19. Internal Execution Walkthrough](#19-internal-execution-walkthrough)
- [20. Testing Strategy](#20-testing-strategy)
- [21. Production Failure Stories](#21-production-failure-stories)
- [22. Debugging Mindset](#22-debugging-mindset)
- [23. Common Mistakes](#23-common-mistakes)
- [24. Performance And Scalability Notes](#24-performance-and-scalability-notes)
- [25. Interview-Ready Explanation](#25-interview-ready-explanation)
- [26. Senior Engineer Checklist](#26-senior-engineer-checklist)
- [27. One-Page Cheat Sheet](#27-one-page-cheat-sheet)
- [28. One Picture To Remember](#28-one-picture-to-remember)

---

## 1. Why This Exists

The previous chapter designed the durable source of truth:

```text
short_urls table
short_code -> long_url
```

Now we need the first real business API:

```http
POST /api/v1/urls
```

This API receives a long URL and returns a short URL.

Example request:

```json
{
  "longUrl": "https://example.com/articles/spring-boot",
  "customAlias": null,
  "expiresAt": null
}
```

Example response:

```json
{
  "id": 1,
  "shortCode": "aB91xZ",
  "shortUrl": "https://mini.ly/aB91xZ",
  "longUrl": "https://example.com/articles/spring-boot",
  "expiresAt": null,
  "createdAt": "2026-06-21T10:00:00Z"
}
```

At beginner level, this looks like:

```text
Receive URL -> generate code -> save
```

At production level, it is more serious:

```text
Receive request
Validate shape
Validate long URL
Validate custom alias if present
Generate code if alias absent
Enforce uniqueness
Handle collisions correctly
Save durable mapping
Return stable API response
Log enough for debugging
Avoid leaking internal errors
```

This chapter is the write-path foundation. If this API is wrong, the redirect API later will be built on corrupted or unreliable data.

---

## 2. The One Core Mental Model

The Create Short URL API is a:

```text
SAFE MAPPING CREATOR
```

It creates this mapping:

```text
shortCode -> longUrl
```

But "safe" means:

```text
1. longUrl is valid
2. shortCode is valid
3. shortCode is unique
4. mapping is durable
5. response is clear
6. errors are predictable
```

ASCII mental model:

```text
                       CREATE SHORT URL API

Client Request
     |
     v
+----------------------+
| Validate input       |
+----------------------+
     |
     v
+----------------------+
| Decide shortCode     |
| custom or generated  |
+----------------------+
     |
     v
+----------------------+
| Insert into Postgres |
| UNIQUE enforced      |
+----------------------+
     |
     v
+----------------------+
| Return shortUrl      |
+----------------------+
```

One-line memory:

```text
Create API converts a valid long URL into a durable unique lookup token.
```

It is the opposite side of the redirect API.

```text
Create path:
longUrl -> shortCode

Redirect path:
shortCode -> longUrl
```

---

## 3. Problem Statement

Build the API that creates a short URL.

Functional requirements:

```text
1. Accept a long URL.
2. Optionally accept a custom alias.
3. Optionally accept an expiry timestamp.
4. Validate the input.
5. Generate a short code if no custom alias is provided.
6. Save the mapping in PostgreSQL.
7. Return the short URL response.
8. Return 409 if custom alias already exists.
9. Retry on generated short code collision.
10. Return clean validation errors.
```

Out of scope for this chapter:

```text
1. Real Base62 deep implementation.
2. Redirect API.
3. Redis caching.
4. Kafka analytics.
5. Authentication.
6. Rate limiting.
7. Malware scanning.
8. Full global exception handler deep dive.
```

The purpose of this chapter is to understand and implement the write path cleanly.

---

## 4. API Contract

Endpoint:

```http
POST /api/v1/urls
Content-Type: application/json
```

Request:

```json
{
  "longUrl": "https://example.com/articles/java",
  "customAlias": "java-guide",
  "expiresAt": "2026-12-31T23:59:59Z"
}
```

Fields:

```text
longUrl:
    required
    must be valid http/https URL

customAlias:
    optional
    must be 4-32 chars if present
    allowed characters: a-z A-Z 0-9 - _

expiresAt:
    optional
    must be future timestamp if present
```

Success:

```http
201 Created
```

Response:

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

Errors:

```text
400 Bad Request:
    invalid long URL
    invalid custom alias
    expiresAt in past

409 Conflict:
    custom alias already exists

500 Internal Server Error:
    unexpected failure
```

API diagram:

```text
+--------+       POST /api/v1/urls       +-----------------------+
| Client | ----------------------------> | Spring Boot API       |
+--------+                               +-----------------------+
    ^                                               |
    |                                               v
    |                                      +--------------------+
    |                                      | PostgreSQL         |
    |                                      | short_urls table   |
    |                                      +--------------------+
    |
    | 201 Created + shortUrl
    +-----------------------------------------------
```

---

## 5. Create Flow Big Picture

The full create flow:

```text
Client
  |
  | POST /api/v1/urls
  v
UrlController
  |
  | validates DTO annotations
  v
UrlService
  |
  | validates URL business rules
  | validates custom alias
  | chooses shortCode
  v
ShortUrlRepository
  |
  | insert ShortUrlEntity
  v
PostgreSQL
  |
  | enforces UNIQUE(short_code)
  v
UrlService
  |
  | builds response
  v
UrlController
  |
  | returns 201 Created
  v
Client
```

ASCII:

```text
+---------+
| Client  |
+---------+
     |
     | POST longUrl
     v
+----------------+
| Controller     |
| request shape  |
+----------------+
     |
     v
+----------------+
| Service        |
| business rules |
+----------------+
     |
     v
+----------------+
| Code decision  |
| alias/generate |
+----------------+
     |
     v
+----------------+
| Repository     |
| save entity    |
+----------------+
     |
     v
+----------------+
| PostgreSQL     |
| unique check   |
+----------------+
     |
     v
+----------------+
| Response DTO   |
+----------------+
```

Two paths inside create:

```text
Path A:
customAlias provided

Path B:
customAlias absent, generate code
```

They share the same database uniqueness constraint but have different error behavior.

---

## 6. Request DTO Design

Create:

```text
src/main/java/com/miniurl/shortener/url/dto/CreateShortUrlRequest.java
```

Code:

```java
package com.miniurl.shortener.url.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class CreateShortUrlRequest {

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

Why DTO?

```text
Do not expose entity directly to API.
```

DTO is the API boundary.

Entity is the database boundary.

Bad:

```text
Controller accepts ShortUrlEntity directly.
```

Why bad?

```text
1. Exposes DB structure to clients.
2. Allows unsafe fields like id/status.
3. Makes API hard to evolve.
4. Couples external contract to internal persistence.
```

Good boundary:

```text
HTTP JSON
   |
   v
Request DTO
   |
   v
Business Service
   |
   v
Entity
   |
   v
Database row
```

---

## 7. Response DTO Design

Create:

```text
src/main/java/com/miniurl/shortener/url/dto/CreateShortUrlResponse.java
```

Code:

```java
package com.miniurl.shortener.url.dto;

import java.time.Instant;

public class CreateShortUrlResponse {

    private Long id;
    private String shortCode;
    private String shortUrl;
    private String longUrl;
    private Instant expiresAt;
    private Instant createdAt;

    public CreateShortUrlResponse(
            Long id,
            String shortCode,
            String shortUrl,
            String longUrl,
            Instant expiresAt,
            Instant createdAt
    ) {
        this.id = id;
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
```

Why response DTO?

```text
The client should receive only what it needs.
```

Response should not expose internal implementation details.

Should response include database id?

```text
For learning project: yes, acceptable.
For public product API: maybe no, because sequential IDs can leak growth.
```

---

## 8. Validation Mental Model

Validation happens in layers.

```text
Layer 1: DTO annotation validation
Layer 2: business validation in service
Layer 3: database constraints
```

ASCII:

```text
HTTP Request
    |
    v
+--------------------------+
| DTO validation           |
| @NotBlank, @Size, @Future|
+--------------------------+
    |
    v
+--------------------------+
| Business validation      |
| URL scheme, alias chars  |
| reserved words           |
+--------------------------+
    |
    v
+--------------------------+
| DB constraints           |
| NOT NULL, UNIQUE, CHECK  |
+--------------------------+
```

Why multiple layers?

Because they protect different things.

```text
@NotBlank catches empty longUrl.
Business validation rejects javascript:alert(1).
DB constraint rejects duplicate short_code.
```

Long URL accepted:

```text
https://example.com
http://example.com/path?q=1
```

Rejected:

```text
not-a-url
javascript:alert(1)
ftp://example.com/file
```

Custom alias accepted:

```text
java-guide
mohamed_123
ABC9
```

Rejected:

```text
ab
hello world
api
admin
a/b
```

---

## 9. Controller Design

Create:

```text
src/main/java/com/miniurl/shortener/url/controller/UrlController.java
```

Code:

```java
package com.miniurl.shortener.url.controller;

import com.miniurl.shortener.url.dto.CreateShortUrlRequest;
import com.miniurl.shortener.url.dto.CreateShortUrlResponse;
import com.miniurl.shortener.url.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateShortUrlResponse createShortUrl(
            @Valid @RequestBody CreateShortUrlRequest request
    ) {
        return urlService.createShortUrl(request);
    }
}
```

Controller responsibilities:

```text
1. Define HTTP route.
2. Accept request body.
3. Trigger validation with @Valid.
4. Call service.
5. Return response with correct HTTP status.
```

Controller should not:

```text
1. Generate short codes.
2. Write repository logic.
3. Catch every DB exception manually.
4. Know SQL details.
5. Contain business rules.
```

Controller mental model:

```text
HTTP adapter
```

Keep it thin.

---

## 10. Service Design

Create:

```text
src/main/java/com/miniurl/shortener/url/service/UrlService.java
```

Service responsibilities:

```text
1. Validate business rules.
2. Decide shortCode.
3. Create entity.
4. Save entity.
5. Handle generated-code collision.
6. Convert entity to response DTO.
```

Service code:

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.url.dto.CreateShortUrlRequest;
import com.miniurl.shortener.url.dto.CreateShortUrlResponse;
import com.miniurl.shortener.url.entity.ShortUrlEntity;
import com.miniurl.shortener.url.repository.ShortUrlRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlService {

    private static final int MAX_GENERATED_CODE_RETRIES = 5;
    private static final String BASE_URL = "https://mini.ly/";

    private final ShortUrlRepository shortUrlRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final UrlValidatorService urlValidatorService;

    public UrlService(
            ShortUrlRepository shortUrlRepository,
            ShortCodeGenerator shortCodeGenerator,
            UrlValidatorService urlValidatorService
    ) {
        this.shortUrlRepository = shortUrlRepository;
        this.shortCodeGenerator = shortCodeGenerator;
        this.urlValidatorService = urlValidatorService;
    }

    @Transactional
    public CreateShortUrlResponse createShortUrl(CreateShortUrlRequest request) {
        urlValidatorService.validateLongUrl(request.getLongUrl());

        String customAlias = normalizeCustomAlias(request.getCustomAlias());

        if (customAlias != null) {
            urlValidatorService.validateCustomAlias(customAlias);
            return createWithCustomAlias(request, customAlias);
        }

        return createWithGeneratedCode(request);
    }

    private CreateShortUrlResponse createWithCustomAlias(
            CreateShortUrlRequest request,
            String customAlias
    ) {
        ShortUrlEntity entity = new ShortUrlEntity(
                customAlias,
                request.getLongUrl().trim(),
                true,
                request.getExpiresAt()
        );

        try {
            ShortUrlEntity saved = shortUrlRepository.saveAndFlush(entity);
            return toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new CustomAliasAlreadyExistsException(customAlias);
        }
    }

    private CreateShortUrlResponse createWithGeneratedCode(
            CreateShortUrlRequest request
    ) {
        for (int attempt = 1; attempt <= MAX_GENERATED_CODE_RETRIES; attempt++) {
            String generatedCode = shortCodeGenerator.generate();

            ShortUrlEntity entity = new ShortUrlEntity(
                    generatedCode,
                    request.getLongUrl().trim(),
                    false,
                    request.getExpiresAt()
            );

            try {
                ShortUrlEntity saved = shortUrlRepository.saveAndFlush(entity);
                return toResponse(saved);
            } catch (DataIntegrityViolationException ex) {
                // Generated code collision. Try again with a new code.
            }
        }

        throw new ShortCodeGenerationFailedException(
                "Failed to generate unique short code after retries"
        );
    }

    private String normalizeCustomAlias(String customAlias) {
        if (customAlias == null || customAlias.isBlank()) {
            return null;
        }
        return customAlias.trim();
    }

    private CreateShortUrlResponse toResponse(ShortUrlEntity entity) {
        return new CreateShortUrlResponse(
                entity.getId(),
                entity.getShortCode(),
                BASE_URL + entity.getShortCode(),
                entity.getLongUrl(),
                entity.getExpiresAt(),
                entity.getCreatedAt()
        );
    }
}
```

Important:

```text
saveAndFlush helps surface DB constraint violation inside this method.
```

If you only use `save`, the exception may happen later at transaction commit.

---

## 11. Short Code Generation Placeholder

Create:

```text
src/main/java/com/miniurl/shortener/url/service/ShortCodeGenerator.java
```

Code:

```java
package com.miniurl.shortener.url.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {

    private static final String ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int CODE_LENGTH = 7;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder builder = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(ALPHABET.length());
            builder.append(ALPHABET.charAt(index));
        }

        return builder.toString();
    }
}
```

Why simple random code now?

```text
1. Easy to understand.
2. Good enough for first create API.
3. DB UNIQUE handles collisions.
4. Dedicated Base62 and ID strategy chapters come later.
```

Capacity intuition:

```text
62^7 is about 3.5 trillion possible codes
```

But probability is not zero.

That is why we still handle duplicate key.

Mental model:

```text
Generator proposes.
Database disposes.
```

ASCII:

```text
ShortCodeGenerator
      |
      | proposes "aB91xZ"
      v
Postgres UNIQUE(short_code)
      |
      +-- available --> save
      |
      +-- duplicate  --> retry
```

---

## 12. Custom Alias Handling

Custom alias is user-selected short code.

Example:

```json
{
  "longUrl": "https://example.com/profile/mohamed",
  "customAlias": "mohamed"
}
```

Stored:

```text
short_code = mohamed
is_custom_alias = true
```

If available:

```http
201 Created
```

If already taken:

```http
409 Conflict
```

Important difference:

```text
Generated code collision:
    retry silently

Custom alias collision:
    return conflict
```

Alias validation service:

```java
package com.miniurl.shortener.url.service;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class UrlValidatorService {

    private static final Pattern ALIAS_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_-]{4,32}$");

    private static final Set<String> RESERVED_ALIASES = Set.of(
            "api",
            "admin",
            "actuator",
            "health",
            "metrics",
            "login",
            "logout"
    );

    public void validateLongUrl(String longUrl) {
        if (longUrl == null || longUrl.isBlank()) {
            throw new InvalidUrlException("longUrl is required");
        }

        URI uri;
        try {
            uri = URI.create(longUrl.trim());
        } catch (IllegalArgumentException ex) {
            throw new InvalidUrlException("longUrl must be a valid URL");
        }

        String scheme = uri.getScheme();

        if (scheme == null ||
                !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            throw new InvalidUrlException("longUrl must start with http or https");
        }

        if (uri.getHost() == null || uri.getHost().isBlank()) {
            throw new InvalidUrlException("longUrl must contain a valid host");
        }
    }

    public void validateCustomAlias(String alias) {
        if (!ALIAS_PATTERN.matcher(alias).matches()) {
            throw new InvalidAliasException(
                    "customAlias may contain only letters, numbers, '-' and '_' and must be 4-32 chars"
            );
        }

        if (RESERVED_ALIASES.contains(alias.toLowerCase())) {
            throw new InvalidAliasException("customAlias is reserved");
        }
    }
}
```

Production note:

```text
URI.create is basic validation.
Later you may add stricter URL normalization and private-network protection.
```

---

## 13. Repository And Entity Usage

Repository:

```java
package com.miniurl.shortener.url.repository;

import com.miniurl.shortener.url.entity.ShortUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrlEntity, Long> {

    Optional<ShortUrlEntity> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);
}
```

Repository is intentionally simple.

Why?

```text
For create API, we mainly need save.
For redirect API later, we will optimize lookup.
```

Entity assumptions from schema chapter:

```text
ShortUrlEntity has:
id
shortCode
longUrl
customAlias
status
expiresAt
createdAt
updatedAt
```

Database must have:

```text
UNIQUE(short_code)
```

That DB constraint is the real concurrency protection.

---

## 14. Database Uniqueness Handling

This is the most important part.

Bad approach:

```java
if (!repository.existsByShortCode(code)) {
    repository.save(entity);
}
```

Why bad?

Race condition:

```text
Pod A checks code: available
Pod B checks code: available
Pod A inserts
Pod B inserts
```

Correct approach:

```text
Insert directly.
Let DB UNIQUE constraint decide.
Handle duplicate key.
```

ASCII:

```text
+-------------+       generated code       +-------------+
| App Pod A   | -------------------------> | Postgres    |
+-------------+                            | UNIQUE      |
                                           +-------------+

+-------------+       same generated code          |
| App Pod B   | ----------------------------- duplicate error
+-------------+
```

Application behavior table:

```text
+----------------------+--------------------------+
| Situation            | Behavior                 |
+----------------------+--------------------------+
| generated collision  | retry with new code      |
| custom alias exists  | return 409 Conflict      |
| DB unavailable       | return 500/503 later     |
+----------------------+--------------------------+
```

Why `saveAndFlush`?

```text
save() may delay SQL until flush/commit.
saveAndFlush() forces insert now.
```

Production note:

```text
Do not retry forever.
Use limited retries.
```

---

## 15. Transaction Boundary

The create operation should be transactional.

```java
@Transactional
public CreateShortUrlResponse createShortUrl(CreateShortUrlRequest request) {
    ...
}
```

Why?

```text
The create operation is one unit of work.
Either the mapping is saved successfully, or it fails.
```

Transaction mental model:

```text
Start transaction
      |
      v
INSERT short_urls
      |
      v
commit success -> row visible
rollback error -> row not saved
```

ASCII:

```text
@Transactional
+-------------------------------------+
| validate request                    |
| decide shortCode                    |
| save entity                         |
| flush SQL                           |
| build response                      |
+-------------------------------------+
          |
          v
      COMMIT / ROLLBACK
```

Important:

```text
@Transactional works through Spring proxy.
Do not call transactional method through self-invocation.
```

This will be covered deeply later, but remember the mental model.

---

## 16. Error Handling Model

For this chapter, define domain exceptions.

```java
package com.miniurl.shortener.url.service;

public class InvalidUrlException extends RuntimeException {
    public InvalidUrlException(String message) {
        super(message);
    }
}
```

```java
package com.miniurl.shortener.url.service;

public class InvalidAliasException extends RuntimeException {
    public InvalidAliasException(String message) {
        super(message);
    }
}
```

```java
package com.miniurl.shortener.url.service;

public class CustomAliasAlreadyExistsException extends RuntimeException {
    public CustomAliasAlreadyExistsException(String alias) {
        super("customAlias already exists: " + alias);
    }
}
```

```java
package com.miniurl.shortener.url.service;

public class ShortCodeGenerationFailedException extends RuntimeException {
    public ShortCodeGenerationFailedException(String message) {
        super(message);
    }
}
```

Simple global handler:

```java
package com.miniurl.shortener.common.error;

import com.miniurl.shortener.url.service.CustomAliasAlreadyExistsException;
import com.miniurl.shortener.url.service.InvalidAliasException;
import com.miniurl.shortener.url.service.InvalidUrlException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({InvalidUrlException.class, InvalidAliasException.class})
    public Map<String, Object> handleBadRequest(RuntimeException ex) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(CustomAliasAlreadyExistsException.class)
    public Map<String, Object> handleConflict(RuntimeException ex) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", 409,
                "error", "Conflict",
                "message", ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed");

        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", 400,
                "error", "Bad Request",
                "message", message
        );
    }
}
```

Dedicated chapter 014 will improve this.

For now:

```text
Good enough to keep API errors clean.
```

---

## 17. Full Code Skeleton

Recommended files:

```text
src/main/java/com/miniurl/shortener/url
│
├── controller
│   └── UrlController.java
│
├── dto
│   ├── CreateShortUrlRequest.java
│   └── CreateShortUrlResponse.java
│
├── entity
│   ├── ShortUrlEntity.java
│   └── ShortUrlStatus.java
│
├── repository
│   └── ShortUrlRepository.java
│
└── service
    ├── UrlService.java
    ├── ShortCodeGenerator.java
    ├── UrlValidatorService.java
    ├── InvalidUrlException.java
    ├── InvalidAliasException.java
    ├── CustomAliasAlreadyExistsException.java
    └── ShortCodeGenerationFailedException.java
```

Common:

```text
src/main/java/com/miniurl/shortener/common/error
└── GlobalExceptionHandler.java
```

ASCII package map:

```text
com.miniurl.shortener
│
├── url
│   ├── controller   HTTP boundary
│   ├── dto          API contract
│   ├── service      business logic
│   ├── repository   DB access
│   └── entity       DB model
│
└── common
    └── error        shared error handling
```

Layer direction:

```text
Controller -> Service -> Repository -> Database
```

Avoid reverse dependencies:

```text
Repository should not call Service.
Entity should not know Controller.
DTO should not contain repository logic.
```

---

## 18. Step-by-Step Dry Runs

### Dry Run 1: Generated code success

Request:

```json
{
  "longUrl": "https://example.com/blog/spring",
  "customAlias": null,
  "expiresAt": null
}
```

Steps:

```text
1. Client sends POST /api/v1/urls.
2. Controller receives request.
3. @Valid checks longUrl is not blank.
4. Controller calls UrlService.
5. Service validates URL scheme and host.
6. customAlias is null.
7. ShortCodeGenerator generates "k9Lm2Q".
8. Service creates ShortUrlEntity.
9. Repository saveAndFlush inserts row.
10. Postgres UNIQUE(short_code) passes.
11. JPA fills id/createdAt.
12. Service builds response.
13. Controller returns 201 Created.
```

Response:

```json
{
  "id": 1,
  "shortCode": "k9Lm2Q",
  "shortUrl": "https://mini.ly/k9Lm2Q",
  "longUrl": "https://example.com/blog/spring",
  "expiresAt": null,
  "createdAt": "2026-06-21T10:00:00Z"
}
```

---

### Dry Run 2: Custom alias success

Request:

```json
{
  "longUrl": "https://example.com/profile/mohamed",
  "customAlias": "mohamed",
  "expiresAt": null
}
```

Steps:

```text
1. DTO validation passes.
2. Service validates longUrl.
3. Service trims customAlias.
4. Alias pattern passes.
5. Alias is not reserved.
6. Entity created with shortCode = mohamed.
7. isCustomAlias = true.
8. Insert succeeds.
9. Response shortUrl = https://mini.ly/mohamed.
```

---

### Dry Run 3: Custom alias conflict

Existing row:

```text
short_code = mohamed
```

Request:

```json
{
  "longUrl": "https://another.com",
  "customAlias": "mohamed"
}
```

Steps:

```text
1. Service validates request.
2. Entity created with shortCode = mohamed.
3. Repository saveAndFlush tries insert.
4. Postgres UNIQUE constraint rejects it.
5. Service catches DataIntegrityViolationException.
6. Service throws CustomAliasAlreadyExistsException.
7. GlobalExceptionHandler returns 409.
```

Response:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "customAlias already exists: mohamed"
}
```

---

### Dry Run 4: Generated code collision

Generated first attempt:

```text
aB91xZ
```

But DB already has:

```text
aB91xZ
```

Steps:

```text
1. Generator returns aB91xZ.
2. Insert fails due to UNIQUE.
3. Service catches exception.
4. Since this was generated, service does not return error.
5. Service retries.
6. Generator returns xK82Lm.
7. Insert succeeds.
8. Client receives xK82Lm.
```

Key principle:

```text
Generated collision is an internal retry.
Custom alias collision is user-visible conflict.
```

---

### Dry Run 5: Invalid long URL

Request:

```json
{
  "longUrl": "javascript:alert(1)"
}
```

Steps:

```text
1. @NotBlank passes because string is not blank.
2. Service parses URI.
3. Scheme is javascript.
4. Service rejects because only http/https allowed.
5. InvalidUrlException thrown.
6. GlobalExceptionHandler returns 400.
```

---

### Dry Run 6: Expiry in past

Request:

```json
{
  "longUrl": "https://example.com",
  "expiresAt": "2020-01-01T00:00:00Z"
}
```

Steps:

```text
1. @Future validates expiresAt.
2. Value is in past.
3. Spring throws MethodArgumentNotValidException.
4. GlobalExceptionHandler returns 400.
```

---

## 19. Internal Execution Walkthrough

When the request hits the system:

```http
POST /api/v1/urls
```

Spring internal flow:

```text
1. Embedded Tomcat receives HTTP request.
2. DispatcherServlet receives request.
3. HandlerMapping finds UrlController.createShortUrl.
4. Jackson converts JSON into CreateShortUrlRequest.
5. Bean Validation runs because @Valid is present.
6. Controller method executes.
7. Service method executes through Spring bean.
8. @Transactional opens transaction.
9. Repository saveAndFlush triggers SQL insert.
10. PostgreSQL enforces constraints.
11. Transaction commits.
12. Jackson serializes response DTO into JSON.
13. HTTP 201 returned.
```

ASCII:

```text
+--------+      +--------+      +-------------------+      +-------------+
| Client | ---> | Tomcat | ---> | DispatcherServlet | ---> | Controller  |
+--------+      +--------+      +-------------------+      +-------------+
                                                               |
                                                               v
                                                        +-------------+
                                                        | Service     |
                                                        | @Transactional
                                                        +-------------+
                                                               |
                                                               v
                                                        +-------------+
                                                        | Repository  |
                                                        +-------------+
                                                               |
                                                               v
                                                        +-------------+
                                                        | PostgreSQL  |
                                                        | UNIQUE check|
                                                        +-------------+
```

Important:

```text
The controller does not open a DB transaction.
The service boundary is the right place for transaction ownership.
```

---

## 20. Testing Strategy

Tests should cover behavior, not just happy path.

### Unit tests for ShortCodeGenerator

Check:

```text
1. code length is 7
2. code uses allowed characters
3. code is not blank
```

Example:

```java
@Test
void generatedCodeShouldHaveExpectedLength() {
    ShortCodeGenerator generator = new ShortCodeGenerator();

    String code = generator.generate();

    assertThat(code).hasSize(7);
}
```

### Unit tests for UrlValidatorService

Cases:

```text
valid https URL -> passes
valid http URL -> passes
blank URL -> fails
ftp URL -> fails
javascript URL -> fails
alias with space -> fails
reserved alias -> fails
```

### Service tests

Cases:

```text
create with generated code -> saves entity
create with custom alias -> saves alias
custom alias conflict -> throws exception
generated collision -> retries
```

### Controller tests with MockMvc

Cases:

```text
POST valid request -> 201
POST blank longUrl -> 400
POST invalid expiresAt -> 400
POST alias conflict -> 409
```

Test pyramid:

```text
          +------------------+
          | Integration      |
          | Testcontainers   |
          +------------------+
                 ^
                 |
          +------------------+
          | Controller tests |
          | MockMvc          |
          +------------------+
                 ^
                 |
          +------------------+
          | Unit tests       |
          | validator/service|
          +------------------+
```

---

## 21. Production Failure Stories

### Failure Story 1: No DB uniqueness

Team checks alias availability before insert:

```text
existsByShortCode(alias)
```

Two users request same alias at same time.

Both checks pass.

Both insert.

Now duplicate alias exists.

Root cause:

```text
Application-only uniqueness check.
```

Fix:

```text
UNIQUE(short_code) in DB.
Handle duplicate key.
```

Lesson:

```text
Database must enforce business-critical uniqueness.
```

---

### Failure Story 2: Generated collision returns 500

Random generator creates duplicate code.

DB rejects insert.

Application does not distinguish generated collision from real failure.

User gets:

```http
500 Internal Server Error
```

Root cause:

```text
No retry logic for generated code collision.
```

Fix:

```text
Retry generated code collision a limited number of times.
```

Lesson:

```text
Rare expected events should not become production incidents.
```

---

### Failure Story 3: Custom alias silently changed

User requests:

```text
customAlias = mohamed
```

Alias exists.

Bad service silently generates:

```text
xY91Ab
```

User expected:

```text
https://mini.ly/mohamed
```

But receives:

```text
https://mini.ly/xY91Ab
```

Root cause:

```text
System treated custom alias like generated code.
```

Fix:

```text
Custom alias conflict must return 409.
```

Lesson:

```text
Respect explicit user intent.
```

---

### Failure Story 4: Invalid URL accepted

System accepts:

```text
javascript:alert(1)
```

Later UI displays it or redirects dangerously.

Root cause:

```text
Weak URL validation.
```

Fix:

```text
Accept only http/https.
Validate host.
Add security checks later.
```

Lesson:

```text
Input validation is security work, not just cleanliness.
```

---

### Failure Story 5: API exposes entity directly

Controller returns entity.

Later entity gets fields:

```text
status
deleted
ownerId
internal flags
```

API accidentally exposes them.

Root cause:

```text
Entity used as response model.
```

Fix:

```text
Use DTOs.
```

Lesson:

```text
Keep API contract separate from persistence model.
```

---

## 22. Debugging Mindset

When Create API fails, ask:

```text
Did request reach controller?
Did JSON parse correctly?
Did @Valid fail?
Did business validation fail?
Was custom alias normalized?
Was short code generated?
Did repository save run?
Did DB unique constraint fail?
Did transaction commit?
Did response DTO build correctly?
```

Debug flow:

```text
POST /api/v1/urls fails
        |
        v
Check HTTP status
        |
        +-- 400 -> DTO/business validation
        |
        +-- 409 -> alias conflict
        |
        +-- 500 -> DB/service/unhandled exception
```

Useful logs:

```text
event=create_short_url
customAliasPresent=true/false
generatedCodeAttempt=1
result=success/conflict/failure
latencyMs=...
```

Example structured log:

```json
{
  "event": "create_short_url",
  "customAliasPresent": false,
  "attempt": 1,
  "result": "SUCCESS",
  "shortCode": "aB91xZ",
  "latencyMs": 42
}
```

Be careful:

```text
Do not log full long URLs blindly if they may contain tokens or sensitive query params.
```

Safer:

```text
log domain
log URL length
log hash of longUrl
```

Useful SQL:

```sql
SELECT id, short_code, long_url, is_custom_alias, status, expires_at, created_at
FROM short_urls
WHERE short_code = 'aB91xZ';
```

---

## 23. Common Mistakes

### Mistake 1: Putting business logic in controller

Wrong:

```text
Controller validates URL, generates code, saves DB.
```

Correct:

```text
Controller delegates to service.
```

### Mistake 2: Using entity as request body

Wrong:

```java
public ShortUrlEntity create(@RequestBody ShortUrlEntity entity)
```

Correct:

```java
public CreateShortUrlResponse create(@Valid @RequestBody CreateShortUrlRequest request)
```

### Mistake 3: Trusting existsByShortCode for uniqueness

Wrong:

```java
if (!existsByShortCode(code)) save();
```

Correct:

```text
Try insert with UNIQUE constraint.
Handle duplicate key.
```

### Mistake 4: Retrying custom alias conflict

Wrong:

```text
customAlias mohamed exists -> generate random alias
```

Correct:

```text
return 409 Conflict
```

### Mistake 5: No retry limit

Wrong:

```java
while (true) {
    try insert
}
```

Correct:

```text
limited retries, then fail clearly
```

### Mistake 6: Weak alias validation

Wrong:

```text
Accept /, ?, spaces, unicode confusables initially.
```

Correct for v1:

```text
a-z A-Z 0-9 - _
4 to 32 chars
reserved words blocked
```

### Mistake 7: Hardcoding base URL everywhere

Wrong:

```java
"https://mini.ly/" + code
```

inside many classes.

Better:

```text
Use config property later:
app.short-url.base-url
```

For this chapter, a constant is acceptable.
Chapter 015 will improve configuration properties.

---

## 24. Performance And Scalability Notes

Create API is usually not the hottest path.

Redirect is hotter.

Still, create API must be safe under bursts.

Potential bottlenecks:

```text
1. DB insert latency
2. unique constraint contention
3. random generator collision if code space too small
4. excessive validation or external calls
5. rate-limit absence under abuse
```

Do not call external malware scanning synchronously in v1.

Future safer design:

```text
create link as PENDING
async scan
activate if safe
```

Create API scaling:

```text
App pods can scale horizontally.
Postgres primary handles writes.
Short code uniqueness is centralized by DB.
Rate limiting protects abuse.
```

ASCII:

```text
+----------+    +----------+    +----------+
| App Pod1 |    | App Pod2 |    | App Pod3 |
+----------+    +----------+    +----------+
      \              |              /
       \             |             /
        v            v            v
              +-------------+
              | Postgres    |
              | UNIQUE code |
              +-------------+
```

At very high scale, ID generation strategy matters more.

That is why later chapters cover:

```text
Base62 encoding
ID generation strategies
sharding
rate limiting
```

---

## 25. Interview-Ready Explanation

If interviewer asks:

```text
How would you implement the create short URL API?
```

Strong answer:

```text
I would expose POST /api/v1/urls with a request DTO containing longUrl, optional
customAlias, and optional expiresAt. The controller should stay thin and delegate
to a service. Validation happens in layers: Bean Validation for simple DTO checks,
business validation for URL scheme, host, alias characters, and reserved aliases,
and finally database constraints for invariants. If a custom alias is provided, I
store it as the short_code and return 409 Conflict if the unique constraint fails.
If no alias is provided, I generate a short code, insert into PostgreSQL, and retry
on duplicate key because generated collisions are internal. The short_code column
must have a database-level unique constraint because application-level checks are
not safe under concurrency. The service method should be transactional, and the API
should return 201 Created with the shortUrl response DTO rather than exposing the
entity directly.
```

Why this is strong:

```text
1. Uses clear API contract.
2. Keeps controller thin.
3. Separates validation layers.
4. Understands DB uniqueness.
5. Handles custom alias vs generated collision differently.
6. Uses transaction boundary.
7. Uses DTO instead of entity.
8. Shows concurrency awareness.
```

Senior one-liner:

```text
The create API is a write-path command that validates user intent, chooses a token, lets the database enforce uniqueness, and returns a stable public URL.
```

---

## 26. Senior Engineer Checklist

Before moving to redirect API, confirm:

```text
[ ] POST /api/v1/urls exists
[ ] Controller is thin
[ ] Request DTO exists
[ ] Response DTO exists
[ ] Entity is not exposed directly
[ ] longUrl is required
[ ] longUrl validates http/https
[ ] customAlias is optional
[ ] customAlias has character rules
[ ] reserved aliases are blocked
[ ] expiresAt must be future
[ ] short code generator exists
[ ] generated code collision retries
[ ] custom alias conflict returns 409
[ ] DB UNIQUE(short_code) exists
[ ] service method is transactional
[ ] saveAndFlush or equivalent surfaces duplicate inside method
[ ] clean errors returned
[ ] tests planned for success and failure paths
[ ] base URL config improvement noted for later
```

If these are done, your create write path is ready.

---

## 27. One-Page Cheat Sheet

```text
Endpoint:
POST /api/v1/urls

Request:
longUrl required
customAlias optional
expiresAt optional

Response:
201 Created
shortCode
shortUrl
longUrl
expiresAt
createdAt

Controller role:
HTTP adapter only

Service role:
business validation
code decision
save mapping
handle conflicts
build response

Repository role:
DB access

Database role:
source of truth
UNIQUE(short_code)

Generated code:
collision -> retry

Custom alias:
collision -> 409 Conflict

Validation layers:
DTO annotations
business validation
DB constraints

Accepted URL schemes:
http
https

Alias chars:
a-z A-Z 0-9 - _

Reserved:
api
admin
actuator
health
metrics
login
logout

Transaction:
@Transactional on service create method

Do not:
return entity directly
trust existsByShortCode only
retry custom alias conflict
log sensitive long URLs blindly
```

---

## 28. One Picture To Remember

```text
                    CREATE SHORT URL API MENTAL MODEL

                      "Long URL becomes durable token"

Client
  |
  | POST /api/v1/urls
  v
+--------------------------------------------------+
| Controller                                       |
| - receives JSON                                  |
| - @Valid DTO                                     |
| - returns 201                                    |
+--------------------------------------------------+
  |
  v
+--------------------------------------------------+
| Service                                          |
| - validate longUrl                               |
| - normalize customAlias                          |
| - validate alias                                 |
| - decide shortCode                               |
+--------------------------------------------------+
  |
  +-----------------------------+
  |                             |
  v                             v
custom alias                  generated code
  |                             |
  v                             v
use alias                    ShortCodeGenerator
  |                             |
  +-------------+---------------+
                |
                v
+--------------------------------------------------+
| Repository.saveAndFlush(entity)                  |
+--------------------------------------------------+
                |
                v
+--------------------------------------------------+
| PostgreSQL short_urls                            |
| UNIQUE(short_code)                               |
+--------------------------------------------------+
                |
        +-------+--------+
        |                |
        v                v
      success          duplicate
        |                |
        v                +-----------------------------+
 return response        | generated? retry             |
                        | custom? return 409           |
                        +-----------------------------+

FINAL MEMORY:

The create API is not just POST + save.
It is validation + token decision + uniqueness enforcement + durable mapping.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. The Create Short URL API is the write path that creates shortCode -> longUrl.
2. Controller should stay thin; service owns business logic.
3. DTO validation, business validation, and DB constraints all protect different layers.
4. Generated code collisions should retry, but custom alias conflicts should return 409.
5. Database UNIQUE(short_code) is mandatory because app-only checks fail under concurrency.
```

After this chapter, the next natural step is:

```text
006_Redirect_API.md
```

Because after creating:

```text
longUrl -> shortCode
```

we must support the hot read path:

```text
shortCode -> longUrl -> HTTP 302 redirect
```
