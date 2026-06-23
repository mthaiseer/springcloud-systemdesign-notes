# 011_Controller_Service_Repository.md
# MiniURLShortener — Controller, Service & Repository

> Core mental model: **Controller, Service, and Repository are three clean boundaries in a backend request. Controller speaks HTTP, Service owns business decisions, and Repository speaks database. A production backend stays maintainable when each layer does only its own job.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Layer Responsibility Map](#4-layer-responsibility-map)
- [5. Request Flow Big Picture](#5-request-flow-big-picture)
- [6. Controller Mental Model](#6-controller-mental-model)
- [7. Service Mental Model](#7-service-mental-model)
- [8. Repository Mental Model](#8-repository-mental-model)
- [9. DTO vs Entity Boundary](#9-dto-vs-entity-boundary)
- [10. Package Structure](#10-package-structure)
- [11. Domain Entity](#11-domain-entity)
- [12. Repository Design](#12-repository-design)
- [13. Service Design](#13-service-design)
- [14. Controller Design](#14-controller-design)
- [15. Mapper Design](#15-mapper-design)
- [16. Create Short URL Flow](#16-create-short-url-flow)
- [17. Redirect Flow](#17-redirect-flow)
- [18. Dependency Direction](#18-dependency-direction)
- [19. Transaction Boundary](#19-transaction-boundary)
- [20. Validation Boundary](#20-validation-boundary)
- [21. Error Handling Boundary](#21-error-handling-boundary)
- [22. Testing Strategy](#22-testing-strategy)
- [23. Step-by-Step Dry Runs](#23-step-by-step-dry-runs)
- [24. Internal Execution Walkthrough](#24-internal-execution-walkthrough)
- [25. Production Failure Stories](#25-production-failure-stories)
- [26. Debugging Mindset](#26-debugging-mindset)
- [27. Common Mistakes](#27-common-mistakes)
- [28. Interview-Ready Explanation](#28-interview-ready-explanation)
- [29. Senior Engineer Checklist](#29-senior-engineer-checklist)
- [30. One-Page Cheat Sheet](#30-one-page-cheat-sheet)
- [31. One Picture To Remember](#31-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener now has core API behavior:

```text
POST /api/v1/urls     -> create short URL
GET /{shortCode}      -> redirect to original URL
```

But if we put everything in one controller, the code quickly becomes messy.

Bad controller style:

```java
@PostMapping("/api/v1/urls")
public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
    // validate URL
    // generate short code
    // check alias
    // build entity
    // save to DB
    // handle duplicates
    // build response
    // handle exceptions
    // log everything
}
```

This works for a demo, but fails in production.

Why?

```text
1. Controller becomes too large.
2. Business rules are mixed with HTTP rules.
3. Database logic is mixed with API response logic.
4. Testing becomes painful.
5. Changing one rule breaks many things.
6. Reusing business logic from another API is hard.
7. Transaction boundaries become unclear.
8. Debugging becomes slower.
```

A production backend needs separation.

The basic Spring Boot backend shape is:

```text
Controller -> Service -> Repository -> Database
```

But this is not just a folder pattern.

It is a thinking pattern.

```text
Controller:
    What did the client ask through HTTP?

Service:
    What should the system do according to business rules?

Repository:
    How do we read/write persistent data?
```

For URL shortener:

```text
Controller receives JSON.
Service validates business rules and creates short code.
Repository stores and loads short URL rows.
```

Golden memory:

```text
Controller should be thin.
Service should be meaningful.
Repository should be boring.
```

---

## 2. The One Core Mental Model

The core mental model is:

```text
THREE BOUNDARIES OF A BACKEND REQUEST
```

ASCII:

```text
External World
     |
     v
+-----------------------------+
| Controller Boundary         |
| HTTP, JSON, status, headers |
+-----------------------------+
     |
     v
+-----------------------------+
| Service Boundary            |
| business decision making    |
+-----------------------------+
     |
     v
+-----------------------------+
| Repository Boundary         |
| database access             |
+-----------------------------+
     |
     v
Database
```

One-line memory:

```text
Controller translates HTTP, Service executes use case, Repository hides storage.
```

A request should flow downward:

```text
Controller -> Service -> Repository
```

A response should flow upward:

```text
Repository -> Service -> Controller
```

What should not happen:

```text
Controller -> Repository directly for business use cases
Repository -> Controller
Entity -> ResponseEntity
Service -> HttpServletRequest everywhere
```

Clean direction:

```text
HTTP details stay near controller.
Business rules stay in service.
Persistence details stay in repository.
```

Mental picture:

```text
Controller is the receptionist.
Service is the decision maker.
Repository is the record keeper.
```

---

## 3. Problem Statement

Build a clean Controller-Service-Repository design for MiniURLShortener.

It must support:

```text
1. Create short URL.
2. Use optional custom alias.
3. Generate short code when alias is absent.
4. Validate long URL and alias.
5. Store URL in PostgreSQL.
6. Redirect from short code to long URL.
7. Handle not found, expired, and blocked links.
8. Keep HTTP logic out of service.
9. Keep database logic out of controller.
10. Keep entity objects away from API response contract.
```

Out of scope for this chapter:

```text
1. Redis cache.
2. Kafka analytics logging.
3. Distributed ID generation.
4. Rate limiting.
5. Authentication.
6. Advanced clean architecture ports/adapters.
```

This chapter creates the production coding skeleton.

After this chapter, your project should have a clear structure:

```text
url/
  controller/
  service/
  repository/
  entity/
  dto/
  mapper/
common/
  error/
```

---

## 4. Layer Responsibility Map

The most important thing is knowing what belongs where.

```text
+-------------+-----------------------------------------------+
| Layer       | Responsibility                                |
+-------------+-----------------------------------------------+
| Controller  | HTTP route, request body, status, headers     |
| Service     | use case, business validation, transactions   |
| Repository  | database read/write queries                   |
| Entity      | database table mapping                         |
| DTO         | API input/output shape                         |
| Mapper      | convert DTO <-> domain/entity                  |
+-------------+-----------------------------------------------+
```

### Controller should do

```text
1. Define endpoint path.
2. Accept request DTO.
3. Trigger @Valid.
4. Call service.
5. Return response DTO.
6. Set HTTP status code.
7. Set redirect header for redirect endpoint.
```

### Controller should not do

```text
1. Generate short code.
2. Check database directly.
3. Write SQL.
4. Decide if alias is reserved.
5. Open transactions manually.
6. Catch every domain exception.
```

### Service should do

```text
1. Execute business use case.
2. Validate business rules.
3. Decide generated code vs custom alias.
4. Decide expired/blocked/not found behavior.
5. Own transaction boundary.
6. Coordinate repository calls.
```

### Service should not do

```text
1. Parse HTTP JSON.
2. Return ResponseEntity.
3. Know servlet request path.
4. Build HTTP Location header directly.
5. Contain raw SQL unless using JDBC intentionally.
```

### Repository should do

```text
1. Save entity.
2. Find by short code.
3. Check existence.
4. Execute persistence queries.
```

### Repository should not do

```text
1. Validate business URL rules.
2. Return API DTOs.
3. Know HTTP status codes.
4. Decide if expired link should be 410.
```

ASCII summary:

```text
Controller:  HTTP language
Service:     Business language
Repository:  Database language
```

---

## 5. Request Flow Big Picture

Create flow:

```text
POST /api/v1/urls
       |
       v
Controller receives CreateShortUrlRequest
       |
       v
Service creates short URL
       |
       v
Repository saves ShortUrl entity
       |
       v
Service returns business result
       |
       v
Controller returns CreateShortUrlResponse
```

ASCII:

```text
+--------+      +------------+      +---------+      +------------+      +----------+
| Client | ---> | Controller | ---> | Service | ---> | Repository | ---> | Database |
+--------+      +------------+      +---------+      +------------+      +----------+
    ^                 |                 |                 |                 |
    |                 v                 v                 v                 v
    |             HTTP DTO        business rules       JPA method          row
    |                                                                         
    +---------------- response DTO / status / headers ------------------------+
```

Redirect flow:

```text
GET /abc123
       |
       v
Controller receives shortCode path variable
       |
       v
Service resolves destination URL
       |
       v
Repository finds row by shortCode
       |
       v
Service checks status and expiry
       |
       v
Controller returns 302 Location header
```

ASCII:

```text
Browser
  |
  | GET /abc123
  v
Controller
  |
  | resolveRedirect("abc123")
  v
Service
  |
  | findByShortCode("abc123")
  v
Repository
  |
  | SELECT * FROM short_urls WHERE short_code = ?
  v
Database
  |
  v
ShortUrl row
  |
  v
Service checks ACTIVE + not expired
  |
  v
Controller returns 302 Location: https://example.com
```

---

## 6. Controller Mental Model

Controller is the HTTP adapter.

It converts external HTTP input into internal service calls.

For MiniURLShortener:

```text
POST JSON -> CreateShortUrlRequest DTO
GET path  -> shortCode string
```

Controller output:

```text
Create API -> 201 Created + JSON body
Redirect   -> 302 Found + Location header
```

Controller should be thin.

Thin means:

```text
Controller has routing, request/response, and small translation logic only.
```

Thin controller example:

```java
@PostMapping
public ResponseEntity<CreateShortUrlResponse> create(
        @Valid @RequestBody CreateShortUrlRequest request
) {
    CreateShortUrlResult result = urlService.createShortUrl(request);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(urlMapper.toResponse(result));
}
```

Bad controller example:

```java
@PostMapping
public ResponseEntity<?> create(@RequestBody CreateShortUrlRequest request) {
    if (!request.getLongUrl().startsWith("http")) {
        return ResponseEntity.badRequest().body("bad url");
    }

    String code = generateRandomCode();
    ShortUrl entity = new ShortUrl();
    repository.save(entity);

    return ResponseEntity.ok(entity);
}
```

Why bad?

```text
1. Business validation in controller.
2. Code generation in controller.
3. Repository used directly.
4. Entity returned to client.
5. Error format inconsistent.
```

Controller rule:

```text
A controller method should read like a table of contents, not like a business algorithm.
```

---

## 7. Service Mental Model

Service is the use-case owner.

A use case is one meaningful business action:

```text
Create a short URL.
Resolve a redirect.
Block a short URL.
Expire a short URL.
```

For this chapter, we need two main service methods:

```java
CreateShortUrlResult createShortUrl(CreateShortUrlRequest request);
RedirectResult resolveRedirect(String shortCode);
```

The service decides:

```text
1. Is longUrl acceptable?
2. Is customAlias acceptable?
3. Should we use alias or generated code?
4. Does shortCode already exist?
5. Should collision retry happen?
6. Is redirect active?
7. Is redirect expired?
8. What domain exception should be thrown?
```

ASCII:

```text
Service = Business Brain

Input from controller
       |
       v
+----------------------------+
| Validate business rules    |
+----------------------------+
       |
       v
+----------------------------+
| Make business decision     |
+----------------------------+
       |
       v
+----------------------------+
| Coordinate persistence     |
+----------------------------+
       |
       v
Return result / throw domain exception
```

Service should be meaningful.

Not too thin:

```java
public ShortUrl save(ShortUrl url) {
    return repository.save(url);
}
```

This is mostly useless.

Better:

```java
public CreateShortUrlResult createShortUrl(CreateShortUrlRequest request) {
    validator.validateLongUrl(request.getLongUrl());
    String shortCode = chooseShortCode(request);
    ShortUrl saved = repository.save(buildEntity(request, shortCode));
    return toResult(saved);
}
```

Service rule:

```text
If a rule matters to the business, it belongs near the service.
```

---

## 8. Repository Mental Model

Repository is the database gateway.

It hides persistence details from service.

For Spring Data JPA:

```java
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    Optional<ShortUrl> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
}
```

Service should not write SQL for simple cases.

It should say:

```java
repository.findByShortCode(shortCode)
```

Not:

```java
entityManager.createQuery("...")
```

Repository translates business-friendly method names into persistence operations.

ASCII:

```text
Service asks:
    findByShortCode("abc123")

Repository performs:
    SELECT * FROM short_urls WHERE short_code = 'abc123'
```

Repository should be boring.

Boring is good.

```text
Boring repository = easy to trust.
Clever repository = hard to debug.
```

Repository rule:

```text
Repository methods should describe data access, not business policy.
```

Good method names:

```text
findByShortCode
existsByShortCode
findByStatus
save
```

Bad method names:

```text
findValidRedirectOrThrow410
createUrlAndReturnHttpResponse
validateAliasAndSave
```

Those contain business or HTTP thinking.

---

## 9. DTO vs Entity Boundary

DTO and Entity are different.

DTO:

```text
API contract object.
Used for request/response.
Can change based on client needs.
```

Entity:

```text
Database mapping object.
Used for persistence.
Can change based on schema needs.
```

Never confuse them.

Bad:

```java
@PostMapping
public ShortUrl create(@RequestBody ShortUrl entity) {
    return repository.save(entity);
}
```

Problems:

```text
1. Client can set internal fields.
2. Entity leaks database schema.
3. API response changes when database changes.
4. Security risk.
5. Validation becomes messy.
```

Good:

```text
Request DTO -> Service -> Entity -> Repository
Repository -> Entity -> Service Result -> Response DTO
```

ASCII:

```text
HTTP JSON
   |
   v
CreateShortUrlRequest DTO
   |
   v
Service business logic
   |
   v
ShortUrl Entity
   |
   v
Database row

Database row
   |
   v
ShortUrl Entity
   |
   v
Service result
   |
   v
CreateShortUrlResponse DTO
   |
   v
HTTP JSON
```

Memory:

```text
DTO protects API shape.
Entity protects database shape.
Mapper protects the boundary between them.
```

---

## 10. Package Structure

Recommended structure:

```text
src/main/java/com/miniurl/shortener/
│
├── MiniUrlShortenerApplication.java
│
├── common/
│   └── error/
│       ├── ApiException.java
│       ├── ApiErrorResponse.java
│       ├── FieldErrorDetail.java
│       └── GlobalExceptionHandler.java
│
└── url/
    ├── controller/
    │   └── UrlController.java
    │
    ├── dto/
    │   ├── CreateShortUrlRequest.java
    │   ├── CreateShortUrlResponse.java
    │   └── RedirectResult.java
    │
    ├── entity/
    │   ├── ShortUrl.java
    │   └── ShortUrlStatus.java
    │
    ├── mapper/
    │   └── UrlMapper.java
    │
    ├── repository/
    │   └── ShortUrlRepository.java
    │
    └── service/
        ├── UrlService.java
        ├── UrlValidatorService.java
        ├── ShortCodeGenerator.java
        ├── CreateShortUrlResult.java
        ├── InvalidUrlException.java
        ├── InvalidAliasException.java
        ├── CustomAliasAlreadyExistsException.java
        ├── ShortCodeNotFoundException.java
        ├── ShortCodeExpiredException.java
        └── ShortCodeBlockedException.java
```

Why this structure?

```text
1. Feature-first: url package owns URL shortener feature.
2. Common errors stay reusable.
3. Controller/service/repository boundaries are visible.
4. DTO/entity separation is obvious.
5. New developers can navigate quickly.
```

Alternative package style:

```text
controller/
service/
repository/
entity/
dto/
```

Feature-first is usually better as project grows:

```text
url/
user/
analytics/
billing/
admin/
```

Each feature owns its own layers.

---

## 11. Domain Entity

Entity maps to `short_urls` table.

File:

```text
src/main/java/com/miniurl/shortener/url/entity/ShortUrlStatus.java
```

```java
package com.miniurl.shortener.url.entity;

public enum ShortUrlStatus {
    ACTIVE,
    BLOCKED,
    DELETED
}
```

File:

```text
src/main/java/com/miniurl/shortener/url/entity/ShortUrl.java
```

```java
package com.miniurl.shortener.url.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "short_urls",
        indexes = {
                @Index(name = "idx_short_urls_short_code", columnList = "short_code")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_short_urls_short_code", columnNames = "short_code")
        }
)
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", nullable = false, unique = true, length = 32)
    private String shortCode;

    @Column(name = "long_url", nullable = false, length = 2048)
    private String longUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ShortUrlStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    protected ShortUrl() {
        // JPA needs a no-args constructor.
    }

    public ShortUrl(String shortCode, String longUrl, ShortUrlStatus status,
                    Instant createdAt, Instant expiresAt) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public ShortUrlStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired(Instant now) {
        return expiresAt != null && expiresAt.isBefore(now);
    }

    public boolean isBlocked() {
        return status == ShortUrlStatus.BLOCKED;
    }

    public boolean isDeleted() {
        return status == ShortUrlStatus.DELETED;
    }
}
```

Why include methods like `isExpired`?

Because they are entity-state questions.

```text
ShortUrl knows its own expiry state.
Service decides what to do with that state.
```

Good:

```java
if (shortUrl.isExpired(now)) {
    throw new ShortCodeExpiredException(shortCode);
}
```

Avoid making entity know HTTP:

```java
public HttpStatus getHttpStatus() {
    return HttpStatus.GONE;
}
```

That would mix entity with API layer.

---

## 12. Repository Design

File:

```text
src/main/java/com/miniurl/shortener/url/repository/ShortUrlRepository.java
```

```java
package com.miniurl.shortener.url.repository;

import com.miniurl.shortener.url.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);
}
```

This small interface gives us:

```text
save(entity)
findById(id)
findAll()
delete(entity)
findByShortCode(shortCode)
existsByShortCode(shortCode)
```

Spring Data JPA derives queries from method names.

```text
findByShortCode
        |
        v
SELECT * FROM short_urls WHERE short_code = ?
```

```text
existsByShortCode
        |
        v
SELECT count or exists check WHERE short_code = ?
```

Repository should return `Optional<ShortUrl>` for lookup.

Why?

```text
A missing short code is normal.
Optional forces service to handle absence clearly.
```

Good service usage:

```java
ShortUrl shortUrl = repository.findByShortCode(shortCode)
        .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));
```

Bad service usage:

```java
ShortUrl shortUrl = repository.findByShortCode(shortCode).get();
```

This throws `NoSuchElementException`, which becomes wrong 500 if not handled.

---

## 13. Service Design

Service owns two use cases:

```text
createShortUrl
resolveRedirect
```

Support classes:

```text
UrlValidatorService
ShortCodeGenerator
UrlMapper
ShortUrlRepository
```

File:

```text
src/main/java/com/miniurl/shortener/url/service/ShortCodeGenerator.java
```

```java
package com.miniurl.shortener.url.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {

    private static final String ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int DEFAULT_LENGTH = 7;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder sb = new StringBuilder(DEFAULT_LENGTH);

        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            int index = random.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }

        return sb.toString();
    }
}
```

File:

```text
src/main/java/com/miniurl/shortener/url/service/CreateShortUrlResult.java
```

```java
package com.miniurl.shortener.url.service;

import java.time.Instant;

public class CreateShortUrlResult {

    private final String shortCode;
    private final String longUrl;
    private final Instant createdAt;
    private final Instant expiresAt;

    public CreateShortUrlResult(String shortCode, String longUrl,
                                Instant createdAt, Instant expiresAt) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
```

File:

```text
src/main/java/com/miniurl/shortener/url/service/RedirectResult.java
```

```java
package com.miniurl.shortener.url.service;

public class RedirectResult {

    private final String longUrl;

    public RedirectResult(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }
}
```

Main service:

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.url.dto.CreateShortUrlRequest;
import com.miniurl.shortener.url.entity.ShortUrl;
import com.miniurl.shortener.url.entity.ShortUrlStatus;
import com.miniurl.shortener.url.repository.ShortUrlRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class UrlService {

    private static final int MAX_GENERATION_ATTEMPTS = 5;

    private final ShortUrlRepository repository;
    private final UrlValidatorService validator;
    private final ShortCodeGenerator shortCodeGenerator;
    private final Clock clock;

    public UrlService(ShortUrlRepository repository,
                      UrlValidatorService validator,
                      ShortCodeGenerator shortCodeGenerator,
                      Clock clock) {
        this.repository = repository;
        this.validator = validator;
        this.shortCodeGenerator = shortCodeGenerator;
        this.clock = clock;
    }

    @Transactional
    public CreateShortUrlResult createShortUrl(CreateShortUrlRequest request) {
        validator.validateLongUrl(request.getLongUrl());

        String shortCode;

        if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {
            shortCode = createUsingCustomAlias(request);
        } else {
            shortCode = createUsingGeneratedCode();
        }

        Instant now = Instant.now(clock);

        ShortUrl entity = new ShortUrl(
                shortCode,
                request.getLongUrl().trim(),
                ShortUrlStatus.ACTIVE,
                now,
                request.getExpiresAt()
        );

        ShortUrl saved = saveSafely(entity, request.getCustomAlias());

        return new CreateShortUrlResult(
                saved.getShortCode(),
                saved.getLongUrl(),
                saved.getCreatedAt(),
                saved.getExpiresAt()
        );
    }

    @Transactional(readOnly = true)
    public RedirectResult resolveRedirect(String shortCode) {
        validator.validateShortCode(shortCode);

        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));

        if (shortUrl.isDeleted()) {
            throw new ShortCodeNotFoundException(shortCode);
        }

        if (shortUrl.isBlocked()) {
            throw new ShortCodeBlockedException(shortCode);
        }

        if (shortUrl.isExpired(Instant.now(clock))) {
            throw new ShortCodeExpiredException(shortCode);
        }

        return new RedirectResult(shortUrl.getLongUrl());
    }

    private String createUsingCustomAlias(CreateShortUrlRequest request) {
        String alias = request.getCustomAlias().trim();
        validator.validateCustomAlias(alias);

        if (repository.existsByShortCode(alias)) {
            throw new CustomAliasAlreadyExistsException(alias);
        }

        return alias;
    }

    private String createUsingGeneratedCode() {
        for (int attempt = 1; attempt <= MAX_GENERATION_ATTEMPTS; attempt++) {
            String candidate = shortCodeGenerator.generate();

            if (!repository.existsByShortCode(candidate)) {
                return candidate;
            }
        }

        throw new ShortCodeGenerationFailedException(
                "Could not generate unique short code after retries"
        );
    }

    private ShortUrl saveSafely(ShortUrl entity, String customAlias) {
        try {
            return repository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            if (customAlias != null && !customAlias.isBlank()) {
                throw new CustomAliasAlreadyExistsException(customAlias.trim());
            }
            throw ex;
        }
    }
}
```

Need a `Clock` bean:

```java
package com.miniurl.shortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
```

Why inject `Clock`?

```text
It makes expiry logic testable.
Tests can use fixed time.
```

---

## 14. Controller Design

DTO request:

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

DTO response:

```java
package com.miniurl.shortener.url.dto;

import java.time.Instant;

public class CreateShortUrlResponse {

    private String shortCode;
    private String shortUrl;
    private String longUrl;
    private Instant createdAt;
    private Instant expiresAt;

    public CreateShortUrlResponse(String shortCode, String shortUrl, String longUrl,
                                  Instant createdAt, Instant expiresAt) {
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
```

Controller:

```java
package com.miniurl.shortener.url.controller;

import com.miniurl.shortener.url.dto.CreateShortUrlRequest;
import com.miniurl.shortener.url.dto.CreateShortUrlResponse;
import com.miniurl.shortener.url.mapper.UrlMapper;
import com.miniurl.shortener.url.service.CreateShortUrlResult;
import com.miniurl.shortener.url.service.RedirectResult;
import com.miniurl.shortener.url.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {

    private final UrlService urlService;
    private final UrlMapper urlMapper;

    public UrlController(UrlService urlService, UrlMapper urlMapper) {
        this.urlService = urlService;
        this.urlMapper = urlMapper;
    }

    @PostMapping("/api/v1/urls")
    public ResponseEntity<CreateShortUrlResponse> createShortUrl(
            @Valid @RequestBody CreateShortUrlRequest request
    ) {
        CreateShortUrlResult result = urlService.createShortUrl(request);
        CreateShortUrlResponse response = urlMapper.toCreateResponse(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        RedirectResult result = urlService.resolveRedirect(shortCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(result.getLongUrl()));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
```

Notice how controller reads:

```text
1. Receive request.
2. Call service.
3. Map response.
4. Return HTTP response.
```

It does not explain how short codes are generated.

That is correct.

---

## 15. Mapper Design

Mapper converts service result to API response.

File:

```text
src/main/java/com/miniurl/shortener/url/mapper/UrlMapper.java
```

```java
package com.miniurl.shortener.url.mapper;

import com.miniurl.shortener.url.dto.CreateShortUrlResponse;
import com.miniurl.shortener.url.service.CreateShortUrlResult;
import org.springframework.stereotype.Component;

@Component
public class UrlMapper {

    private static final String BASE_URL = "http://localhost:8080";

    public CreateShortUrlResponse toCreateResponse(CreateShortUrlResult result) {
        String shortUrl = BASE_URL + "/" + result.getShortCode();

        return new CreateShortUrlResponse(
                result.getShortCode(),
                shortUrl,
                result.getLongUrl(),
                result.getCreatedAt(),
                result.getExpiresAt()
        );
    }
}
```

For production, `BASE_URL` should come from config:

```yaml
app:
  base-url: https://sho.rt
```

Then mapper can use properties.

But for this chapter, the simple version is enough.

Why use mapper?

```text
1. Controller remains thin.
2. Service does not know public base URL formatting.
3. Entity does not leak to response.
4. Response shape can evolve independently.
```

ASCII:

```text
Service Result
     |
     v
UrlMapper
     |
     v
API Response DTO
```

---

## 16. Create Short URL Flow

Request:

```http
POST /api/v1/urls
Content-Type: application/json
```

Body:

```json
{
  "longUrl": "https://example.com/articles/spring-boot",
  "customAlias": "spring101",
  "expiresAt": "2026-12-31T23:59:59Z"
}
```

Flow:

```text
1. Controller receives JSON.
2. Jackson maps JSON to CreateShortUrlRequest.
3. @Valid checks DTO annotations.
4. Controller calls urlService.createShortUrl(request).
5. Service validates longUrl.
6. Service sees customAlias exists.
7. Service validates alias.
8. Service checks repository.existsByShortCode("spring101").
9. Service builds ShortUrl entity.
10. Repository saves entity.
11. Service returns CreateShortUrlResult.
12. Mapper builds CreateShortUrlResponse.
13. Controller returns 201 Created.
```

ASCII:

```text
Client
  |
  | POST /api/v1/urls
  v
Controller
  |
  | @Valid DTO
  v
Service
  |
  | validate URL + alias
  v
Repository
  |
  | existsByShortCode + save
  v
Database
  |
  | INSERT row
  v
Repository
  |
  v
Service
  |
  v
Mapper
  |
  v
Controller
  |
  | 201 Created JSON
  v
Client
```

Response:

```json
{
  "shortCode": "spring101",
  "shortUrl": "http://localhost:8080/spring101",
  "longUrl": "https://example.com/articles/spring-boot",
  "createdAt": "2026-06-23T10:00:00Z",
  "expiresAt": "2026-12-31T23:59:59Z"
}
```

---

## 17. Redirect Flow

Request:

```http
GET /spring101
```

Flow:

```text
1. Controller extracts path variable spring101.
2. Controller calls urlService.resolveRedirect("spring101").
3. Service validates shortCode shape.
4. Repository searches by shortCode.
5. If missing, service throws ShortCodeNotFoundException.
6. If deleted, service throws ShortCodeNotFoundException.
7. If blocked, service throws ShortCodeBlockedException.
8. If expired, service throws ShortCodeExpiredException.
9. If active, service returns RedirectResult(longUrl).
10. Controller builds Location header.
11. Controller returns 302 Found.
```

ASCII:

```text
GET /spring101
     |
     v
+------------+
| Controller |
+------------+
     |
     v
+-------------------------------+
| Service                       |
| validate + lookup + decide    |
+-------------------------------+
     |
     v
+-------------------------------+
| Repository                    |
| findByShortCode("spring101") |
+-------------------------------+
     |
     v
+-------------------------------+
| Database                      |
+-------------------------------+
     |
     v
ACTIVE and not expired?
     |
     +-- no  -> domain exception -> GlobalExceptionHandler
     |
     +-- yes -> 302 Location header
```

Successful response:

```http
HTTP/1.1 302 Found
Location: https://example.com/articles/spring-boot
```

No JSON body is needed for browser redirect.

---

## 18. Dependency Direction

Dependency direction matters.

Correct direction:

```text
Controller depends on Service.
Service depends on Repository.
Repository depends on JPA/Spring Data.
```

ASCII:

```text
+------------+        +---------+        +------------+
| Controller | -----> | Service | -----> | Repository |
+------------+        +---------+        +------------+
```

Wrong direction:

```text
Repository depends on Service
Service depends on Controller
Entity depends on Response DTO
```

Why wrong?

```text
1. Lower layers should not know upper delivery mechanisms.
2. Database code should not know HTTP.
3. Business code should be reusable from REST, queue worker, CLI, tests.
```

Imagine later you add Kafka consumer:

```text
Kafka message -> Service -> Repository
```

If service is clean, reuse is easy.

If service returns `ResponseEntity`, Kafka consumer becomes awkward.

Good service return:

```java
CreateShortUrlResult
```

Bad service return:

```java
ResponseEntity<CreateShortUrlResponse>
```

Rule:

```text
Service returns business result, not HTTP result.
```

---

## 19. Transaction Boundary

Transaction belongs in service layer.

Why?

Because service owns the use case.

Create URL use case may involve:

```text
1. Check alias.
2. Generate code.
3. Save entity.
4. Later publish event.
5. Later write audit row.
```

These steps belong to one business operation.

So transaction annotation goes here:

```java
@Transactional
public CreateShortUrlResult createShortUrl(CreateShortUrlRequest request) {
    ...
}
```

Read-only transaction for redirect:

```java
@Transactional(readOnly = true)
public RedirectResult resolveRedirect(String shortCode) {
    ...
}
```

Why not put transaction in controller?

```text
Controller is HTTP adapter, not business unit-of-work owner.
```

Why not only repository?

```text
Repository transaction wraps one database operation, but a use case may need multiple operations.
```

ASCII:

```text
Controller
  |
  v
Service method starts transaction
  |
  +-- repository.existsByShortCode
  +-- repository.save
  |
  v
Service method ends transaction
```

Important Spring note:

```text
@Transactional works through Spring proxy.
External calls to service bean go through proxy.
Self-invocation inside same class does not start a new transaction.
```

For this chapter, controller calls service bean, so transaction works.

---

## 20. Validation Boundary

Validation is layered.

From previous chapter:

```text
DTO validation catches shape problems.
Business validation catches domain rules.
DB constraints catch final invariants.
```

In Controller-Service-Repository design:

```text
Controller triggers DTO validation.
Service calls business validator.
Repository/DB enforces persistence constraints.
```

ASCII:

```text
Controller
  |
  | @Valid
  v
Service
  |
  | validator.validateLongUrl()
  | validator.validateCustomAlias()
  v
Repository/DB
  |
  | UNIQUE(short_code)
  v
```

Example:

```text
longUrl = ""
```

Handled by:

```text
Controller DTO validation -> 400 VALIDATION_FAILED
```

Example:

```text
longUrl = "ftp://example.com"
```

Handled by:

```text
Service business validation -> 400 INVALID_URL
```

Example:

```text
Two requests create same alias concurrently.
```

Handled by:

```text
Database UNIQUE constraint -> translated to 409 conflict
```

Rule:

```text
Each layer validates what only that layer can know best.
```

---

## 21. Error Handling Boundary

Error handling should be centralized.

Controller should not catch every domain exception.

Bad:

```java
try {
    return ResponseEntity.ok(service.createShortUrl(request));
} catch (InvalidUrlException ex) {
    return ResponseEntity.badRequest().body(...);
} catch (CustomAliasAlreadyExistsException ex) {
    return ResponseEntity.status(409).body(...);
}
```

This duplicates error handling across controllers.

Good:

```java
CreateShortUrlResult result = service.createShortUrl(request);
return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toCreateResponse(result));
```

If service throws exception:

```text
GlobalExceptionHandler catches it.
```

ASCII:

```text
Controller -> Service -> exception
                    |
                    v
          GlobalExceptionHandler
                    |
                    v
          ApiErrorResponse JSON
```

Boundary rule:

```text
Service throws domain exceptions.
Global handler converts exceptions to HTTP error responses.
```

This keeps controller clean.

---

## 22. Testing Strategy

Each layer gets different tests.

### Controller test

Focus:

```text
HTTP status
request validation
response shape
controller-service wiring
```

Mock service.

Example:

```java
@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UrlMapper urlMapper;

    @Test
    void createShortUrl_returns201() throws Exception {
        // arrange service and mapper mocks
        // perform POST
        // assert 201 and JSON fields
    }
}
```

### Service test

Focus:

```text
business rules
alias logic
generation retry
expired/blocked/not found behavior
transactional decisions indirectly
```

Mock repository and generator.

Example checks:

```text
custom alias exists -> throws CustomAliasAlreadyExistsException
generated collision -> retries
unknown redirect -> throws ShortCodeNotFoundException
expired redirect -> throws ShortCodeExpiredException
blocked redirect -> throws ShortCodeBlockedException
```

### Repository test

Focus:

```text
JPA mapping
findByShortCode
existsByShortCode
unique constraint
```

Use `@DataJpaTest`.

### Integration test

Focus:

```text
full flow with real Spring context and test database
```

Use Testcontainers later.

Test pyramid:

```text
          +------------------+
          | E2E / Integration|
          +------------------+
        +----------------------+
        | Controller/Repository|
        +----------------------+
      +--------------------------+
      | Service Unit Tests       |
      +--------------------------+
```

Most business edge cases should be service tests.

---

## 23. Step-by-Step Dry Runs

### Dry Run 1: Create with generated short code

Request:

```json
{
  "longUrl": "https://example.com/a"
}
```

Flow:

```text
1. Controller receives POST /api/v1/urls.
2. @Valid passes because longUrl is not blank.
3. Controller calls service.
4. Service validates URL scheme and host.
5. customAlias is absent.
6. Service asks generator for candidate code.
7. Generator returns A7xPq2Z.
8. Repository checks existsByShortCode(A7xPq2Z), returns false.
9. Service creates ShortUrl entity.
10. Repository saves entity.
11. Service returns CreateShortUrlResult.
12. Mapper builds shortUrl = http://localhost:8080/A7xPq2Z.
13. Controller returns 201.
```

Layer trace:

```text
Controller: POST + DTO + 201
Service: validate + generate + save decision
Repository: exists + save
Database: insert row
```

---

### Dry Run 2: Create with custom alias

Request:

```json
{
  "longUrl": "https://example.com/java",
  "customAlias": "java101"
}
```

Flow:

```text
1. DTO validation passes.
2. Service validates longUrl.
3. Service trims customAlias.
4. Business validator checks alias pattern.
5. Repository checks if java101 exists.
6. It does not exist.
7. Service uses java101 as shortCode.
8. Entity is saved.
9. Response returns shortCode java101.
```

Important distinction:

```text
customAlias is user-chosen.
generated shortCode is system-chosen.
```

Conflict response differs for custom alias.

---

### Dry Run 3: Duplicate custom alias

Existing row:

```text
short_code = java101
```

Request:

```json
{
  "longUrl": "https://another.com",
  "customAlias": "java101"
}
```

Flow:

```text
1. Controller passes request to service.
2. Service validates URL and alias.
3. Repository existsByShortCode(java101) returns true.
4. Service throws CustomAliasAlreadyExistsException.
5. GlobalExceptionHandler returns 409 ALIAS_ALREADY_EXISTS.
```

Controller does not catch this.

That is correct.

---

### Dry Run 4: Redirect active short code

Database:

```text
short_code = go123
long_url = https://example.com
status = ACTIVE
expires_at = null
```

Request:

```http
GET /go123
```

Flow:

```text
1. Controller extracts go123.
2. Service validates shortCode.
3. Repository finds row.
4. Entity is not deleted.
5. Entity is not blocked.
6. Entity is not expired.
7. Service returns RedirectResult.
8. Controller returns 302 Location.
```

Response:

```http
302 Found
Location: https://example.com
```

---

### Dry Run 5: Redirect expired code

Database:

```text
short_code = sale99
status = ACTIVE
expires_at = 2026-01-01T00:00:00Z
```

Current time:

```text
2026-06-23T10:00:00Z
```

Flow:

```text
1. Repository finds row.
2. Service checks deleted: false.
3. Service checks blocked: false.
4. Service checks expired: true.
5. Service throws ShortCodeExpiredException.
6. Handler returns 410 SHORT_CODE_EXPIRED.
```

---

### Dry Run 6: Repository returns empty

Request:

```http
GET /missing7
```

Flow:

```text
1. Controller calls service.
2. Service validates format.
3. Repository findByShortCode returns Optional.empty().
4. Service throws ShortCodeNotFoundException.
5. Handler returns 404.
```

Bad alternative:

```java
repository.findByShortCode(shortCode).get();
```

Would throw generic exception and likely become 500.

---

## 24. Internal Execution Walkthrough

Spring Boot runtime flow:

```text
1. HTTP request enters embedded Tomcat.
2. DispatcherServlet receives request.
3. Spring finds matching controller method.
4. Jackson converts JSON to DTO.
5. Bean Validation runs because @Valid exists.
6. Controller method executes.
7. Controller calls UrlService bean.
8. Because UrlService is a Spring bean, transactional proxy can intercept call.
9. Transaction starts for @Transactional method.
10. Service executes business logic.
11. Service calls repository bean.
12. Spring Data JPA creates query or save operation.
13. Hibernate interacts with PostgreSQL.
14. Repository returns entity/result.
15. Service returns business result.
16. Transaction commits.
17. Controller maps response.
18. Jackson serializes response DTO to JSON.
19. HTTP response goes to client.
```

ASCII:

```text
Tomcat
  |
  v
DispatcherServlet
  |
  v
UrlController
  |
  v
UrlService Proxy
  |
  | starts transaction
  v
UrlService target object
  |
  v
ShortUrlRepository proxy
  |
  v
Hibernate EntityManager
  |
  v
PostgreSQL
```

Important proxy detail:

```text
Controller does not call raw UrlService object directly.
It calls Spring-managed bean, which may be a proxy.
That proxy applies @Transactional behavior.
```

Mental model:

```text
Controller -> Service proxy -> real service method -> Repository proxy -> DB
```

---

## 25. Production Failure Stories

### Failure Story 1: Fat controller becomes untestable

A team puts validation, generation, database access, and response mapping inside controller.

At first it works.

Then requirements change:

```text
1. Add mobile API.
2. Add admin API.
3. Add batch import worker.
4. Add Kafka event consumer.
```

All need same create-short-url logic.

Because logic is inside controller, code is copied.

Bug appears:

```text
Mobile API validates alias differently from web API.
Batch worker bypasses duplicate handling.
```

Root cause:

```text
Business logic lived in delivery layer.
```

Fix:

```text
Move business logic to service.
Keep controller thin.
```

Lesson:

```text
Service layer protects reuse.
```

---

### Failure Story 2: Entity leaked to API client

Controller returns `ShortUrl` entity directly.

Later entity gets new fields:

```text
ownerUserId
internalRiskScore
blockedReason
createdByIpHash
```

Suddenly API exposes internal fields.

Root cause:

```text
Entity used as response DTO.
```

Fix:

```text
Return response DTO only.
Use mapper.
```

Lesson:

```text
Never let database shape become public API shape by accident.
```

---

### Failure Story 3: Repository contains business rules

Repository method:

```java
findActiveNonExpiredUrlOrThrow(...)
```

At first this seems convenient.

Then product changes:

```text
Expired links should return custom landing page for browser,
but API clients should receive 410.
```

Repository now has policy mixed with query.

Root cause:

```text
Business decision hidden in persistence layer.
```

Fix:

```text
Repository fetches data.
Service decides behavior.
```

Lesson:

```text
Repositories retrieve facts. Services make decisions.
```

---

### Failure Story 4: No transaction around use case

Create flow later adds two writes:

```text
1. Insert short URL.
2. Insert audit log.
```

Without service-level transaction:

```text
Short URL inserted.
Audit insert fails.
System has incomplete business operation.
```

Root cause:

```text
Transaction was not placed around the complete use case.
```

Fix:

```text
@Transactional on service method.
```

Lesson:

```text
Transaction should wrap the business unit of work.
```

---

### Failure Story 5: Static time makes expiry tests flaky

Service uses:

```java
Instant.now()
```

Tests sometimes fail around boundary times.

Root cause:

```text
Time not injectable.
```

Fix:

```text
Inject Clock.
Use fixed Clock in tests.
```

Lesson:

```text
Time is a dependency. Treat it like one.
```

---

## 26. Debugging Mindset

When a request fails, identify the layer first.

Ask:

```text
Did request reach controller?
Did @Valid fail before service?
Did service throw domain exception?
Did repository return empty?
Did DB constraint fail?
Did transaction commit?
Did mapper create wrong response?
Did controller set wrong status/header?
```

Debug map:

```text
400 VALIDATION_FAILED:
    Controller/DTO validation boundary.

400 INVALID_URL / INVALID_ALIAS:
    Service business validation boundary.

409 ALIAS_ALREADY_EXISTS:
    Service/repository uniqueness boundary.

404 SHORT_CODE_NOT_FOUND:
    Repository lookup empty or service hides deleted link.

410 SHORT_CODE_EXPIRED:
    Service expiry decision.

403 SHORT_CODE_BLOCKED:
    Service status decision.

500 INTERNAL_ERROR:
    unexpected bug, infrastructure failure, unmapped exception.
```

Layer-specific debug tools:

```text
Controller:
    MockMvc test, request/response logs, JSON body.

Service:
    unit test with mocked repository, domain logs.

Repository:
    SQL logs, @DataJpaTest, DB constraints.

Database:
    psql query, indexes, constraint names.
```

Useful SQL:

```sql
SELECT id, short_code, long_url, status, created_at, expires_at
FROM short_urls
WHERE short_code = 'abc123';
```

Useful application log fields:

```text
method
path
shortCode
errorCode
exceptionClass
correlationId
latencyMs
```

Debugging rule:

```text
Do not randomly inspect all code. First locate the layer where the failure belongs.
```

---

## 27. Common Mistakes

### Mistake 1: Controller calls repository directly

Wrong:

```java
@PostMapping
public ResponseEntity<?> create(@RequestBody CreateShortUrlRequest request) {
    repository.save(...);
}
```

Correct:

```java
CreateShortUrlResult result = service.createShortUrl(request);
```

Why:

```text
Controller should not own business use case.
```

### Mistake 2: Service returns ResponseEntity

Wrong:

```java
public ResponseEntity<CreateShortUrlResponse> createShortUrl(...) {
    ...
}
```

Correct:

```java
public CreateShortUrlResult createShortUrl(...) {
    ...
}
```

Why:

```text
Service should not know HTTP.
```

### Mistake 3: Repository returns DTO

Wrong:

```java
CreateShortUrlResponse findByShortCode(String shortCode);
```

Correct:

```java
Optional<ShortUrl> findByShortCode(String shortCode);
```

Why:

```text
Repository should know persistence, not API response contract.
```

### Mistake 4: Returning entity to client

Wrong:

```java
return ResponseEntity.ok(shortUrlEntity);
```

Correct:

```java
return ResponseEntity.ok(responseDto);
```

Why:

```text
Avoid leaking DB schema and internal fields.
```

### Mistake 5: No service-level transaction

Wrong:

```text
Rely on individual repository methods only.
```

Correct:

```java
@Transactional
public CreateShortUrlResult createShortUrl(...) { ... }
```

Why:

```text
Use case is the transaction boundary.
```

### Mistake 6: Business validation only in DTO annotations

Wrong:

```java
@Pattern(regexp = "...")
private String longUrl;
```

And no service validation.

Correct:

```text
DTO validates shape.
Service validates business meaning.
```

### Mistake 7: Optional.get()

Wrong:

```java
repository.findByShortCode(code).get();
```

Correct:

```java
repository.findByShortCode(code)
        .orElseThrow(() -> new ShortCodeNotFoundException(code));
```

Why:

```text
Missing short code is domain behavior, not accidental runtime crash.
```

### Mistake 8: Mapper grows business logic

Wrong:

```text
Mapper decides if URL is expired.
```

Correct:

```text
Service decides expiry.
Mapper only converts shapes.
```

### Mistake 9: Hardcoding base URL everywhere

Wrong:

```java
"http://localhost:8080/" + code
```

in many places.

Correct:

```text
Centralize in mapper/config.
```

### Mistake 10: Testing only end-to-end

Wrong:

```text
Only call API and test happy path.
```

Correct:

```text
Controller tests + service tests + repository tests + integration tests.
```

---

## 28. Interview-Ready Explanation

If interviewer asks:

```text
How do you structure Controller, Service, and Repository in your Spring Boot URL shortener?
```

Strong answer:

```text
I keep the controller thin, the service meaningful, and the repository focused on persistence. The controller owns HTTP concerns: endpoint mapping, request DTO validation, response status, and redirect headers. It never talks to the database directly. The service owns the use case: validating business rules, deciding whether to use a custom alias or generated code, handling collisions, checking expired or blocked URLs, and throwing domain exceptions. The repository is a Spring Data JPA interface that only reads and writes ShortUrl entities using methods like findByShortCode and existsByShortCode. I do not expose entities as API responses; I use DTOs and a mapper so the API contract is independent from the database schema. Transactions are placed on service methods because the service method represents the business unit of work. This structure keeps the code testable, reusable, and easier to debug in production.
```

Why this answer is strong:

```text
1. Shows layer separation.
2. Explains thin controller.
3. Explains service as business owner.
4. Explains repository as persistence owner.
5. Mentions DTO/entity boundary.
6. Mentions transaction boundary.
7. Mentions testability and production debugging.
8. Avoids framework-only answer.
```

Senior one-liner:

```text
Controller handles delivery, service handles decisions, repository handles storage.
```

---

## 29. Senior Engineer Checklist

Before moving forward, confirm:

```text
[ ] Controller does not call repository directly.
[ ] Controller does not contain business rules.
[ ] Controller uses @Valid for request DTO.
[ ] Controller returns response DTO, not entity.
[ ] Service owns createShortUrl use case.
[ ] Service owns resolveRedirect use case.
[ ] Service validates business rules.
[ ] Service throws domain exceptions.
[ ] Service has @Transactional on write use case.
[ ] Service has @Transactional(readOnly = true) on read use case.
[ ] Repository extends JpaRepository.
[ ] Repository returns Optional for findByShortCode.
[ ] Repository method names describe data access only.
[ ] Entity maps to database table.
[ ] DTO maps to API contract.
[ ] Mapper converts service result to response DTO.
[ ] No ResponseEntity inside service.
[ ] No HTTP status inside repository.
[ ] No entity leaked to API client.
[ ] Clock is injectable for expiry tests.
[ ] Duplicate alias is handled as domain conflict.
[ ] Missing short code is handled as 404 domain error.
[ ] Expired short code is handled as 410 domain error.
[ ] Blocked short code is handled as 403 domain error.
```

If these are true, your Controller-Service-Repository design is production-shaped.

---

## 30. One-Page Cheat Sheet

```text
Core mental model:
Controller = HTTP boundary
Service = business boundary
Repository = database boundary

Flow:
Client -> Controller -> Service -> Repository -> DB
DB -> Repository -> Service -> Controller -> Client

Controller does:
endpoint mapping
request DTO
@Valid
response DTO
HTTP status
Location header

Controller does not:
generate code
query DB directly
make business decisions
return entity

Service does:
use case
business validation
short code decision
alias conflict decision
expired/blocked/not-found decision
transaction boundary

Service does not:
return ResponseEntity
know servlet details
write random SQL for simple CRUD

Repository does:
save
findByShortCode
existsByShortCode
simple persistence queries

Repository does not:
know HTTP
return DTO
make business policy decisions

DTO vs Entity:
DTO = API shape
Entity = DB shape
Mapper = conversion boundary

Transaction:
@Transactional on service write method
@Transactional(readOnly = true) on service read method

Testing:
Controller -> MockMvc
Service -> unit tests with mocked repository
Repository -> @DataJpaTest
Integration -> full Spring + DB

Golden rule:
Thin controller, meaningful service, boring repository.
```

---

## 31. One Picture To Remember

```text
                 CONTROLLER - SERVICE - REPOSITORY

                         External Client
                               |
                               v
                  +--------------------------+
                  | Controller               |
                  | HTTP path, JSON, status  |
                  | @Valid, Location header  |
                  +--------------------------+
                               |
                               v
                  +--------------------------+
                  | Service                  |
                  | Business use case        |
                  | validation, decisions    |
                  | transactions, exceptions |
                  +--------------------------+
                               |
                               v
                  +--------------------------+
                  | Repository               |
                  | save, find, exists       |
                  | persistence only         |
                  +--------------------------+
                               |
                               v
                          PostgreSQL


Create URL:

Client JSON
   -> Controller DTO
      -> Service validates + chooses code
         -> Repository saves entity
            -> DB row
         <- Service result
      <- Mapper response DTO
   <- 201 Created JSON

Redirect:

GET /abc123
   -> Controller path variable
      -> Service lookup + active/blocked/expired decision
         -> Repository findByShortCode
            -> DB row
         <- Entity
      <- RedirectResult
   <- 302 Location header

FINAL MEMORY:

Controller speaks HTTP.
Service speaks business.
Repository speaks database.
DTO protects API.
Entity protects persistence.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Controller should be thin: receive HTTP, call service, return HTTP response.
2. Service should be meaningful: own business rules, decisions, exceptions, and transactions.
3. Repository should be boring: read and write entities only.
4. DTOs are API contracts; entities are database contracts; do not mix them.
5. Clean layer boundaries make the code easier to test, reuse, debug, and scale.
```

Previous chapter style reference:

```text
009_Error_Handling_Validation.md established the validation and exception boundary.
011_Controller_Service_Repository.md places that boundary into clean Spring Boot layers.
```

Next phase:

```text
012_JPA_vs_JDBC.md
013_Transactions.md
014_Global_Exception_Handling.md
015_Config_Properties.md
```
