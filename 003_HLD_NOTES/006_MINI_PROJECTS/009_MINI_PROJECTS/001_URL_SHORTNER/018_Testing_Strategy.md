# 018_Testing_Strategy.md
# MiniURLShortener — Testing Strategy

> Core mental model: **Testing is the executable safety net of the application. Unit tests protect logic, slice tests protect boundaries, integration tests protect wiring and database truth, and end-to-end tests protect the real user journey.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Testing Pyramid Mental Model](#4-testing-pyramid-mental-model)
- [5. What To Test In MiniURLShortener](#5-what-to-test-in-miniurlshortener)
- [6. Unit Tests](#6-unit-tests)
- [7. Service Tests](#7-service-tests)
- [8. Controller Tests With MockMvc](#8-controller-tests-with-mockmvc)
- [9. Repository Tests](#9-repository-tests)
- [10. Integration Tests With Testcontainers](#10-integration-tests-with-testcontainers)
- [11. Error Handling Tests](#11-error-handling-tests)
- [12. Transaction Boundary Tests](#12-transaction-boundary-tests)
- [13. Redirect Flow Tests](#13-redirect-flow-tests)
- [14. Create URL Flow Tests](#14-create-url-flow-tests)
- [15. Test Data Builder Pattern](#15-test-data-builder-pattern)
- [16. Test Profiles And Configuration](#16-test-profiles-and-configuration)
- [17. Naming Strategy](#17-naming-strategy)
- [18. Step-by-Step Dry Runs](#18-step-by-step-dry-runs)
- [19. Internal Execution Walkthrough](#19-internal-execution-walkthrough)
- [20. Production Failure Stories](#20-production-failure-stories)
- [21. Debugging Mindset](#21-debugging-mindset)
- [22. Common Mistakes](#22-common-mistakes)
- [23. Interview-Ready Explanation](#23-interview-ready-explanation)
- [24. Senior Engineer Checklist](#24-senior-engineer-checklist)
- [25. One-Page Cheat Sheet](#25-one-page-cheat-sheet)
- [26. One Picture To Remember](#26-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener has many moving parts:

```text
Controller
Service
Repository
DTO validation
Global exception handler
Transactions
JPA entity mapping
Postgres constraints
Redirect response
Config properties
Actuator checks
```

A bug in any layer can break production.

Example bug:

```text
POST /api/v1/urls creates a row, but shortCode is null.
```

Another bug:

```text
GET /abc123 returns JSON instead of HTTP 302 Location header.
```

Another bug:

```text
Duplicate custom alias returns 500 instead of 409.
```

Testing exists because code that works once manually is not enough.

Production backend rule:

```text
If it is important behavior, it must be executable as a test.
```

Testing converts your expectations into repeatable checks.

Manual testing says:

```text
I tried it once.
```

Automated testing says:

```text
The system proves this behavior every build.
```

ASCII:

```text
Developer changes code
        |
        v
Automated tests run
        |
        +-- pass --> safe to continue
        |
        +-- fail --> bug caught before production
```

For a URL shortener, tests are not optional because the application has strict contracts:

```text
invalid input -> 400
alias conflict -> 409
missing code -> 404
expired code -> 410
active code -> 302 redirect
DB uniqueness must hold
transaction rollback must hold
```

---

## 2. The One Core Mental Model

Testing is an:

```text
EXECUTABLE SAFETY NET
```

The safety net has layers.

```text
+--------------------------------------------------+
| E2E / Full Integration Tests                     |
| Does the real user flow work?                    |
+--------------------------------------------------+
| API / Controller Tests                           |
| Does HTTP contract work?                         |
+--------------------------------------------------+
| Service Tests                                    |
| Does business logic work?                        |
+--------------------------------------------------+
| Repository / DB Tests                            |
| Does persistence and constraint truth work?      |
+--------------------------------------------------+
| Unit Tests                                       |
| Does one small rule work?                        |
+--------------------------------------------------+
```

One-line memory:

```text
Small tests find logic bugs fast; integration tests prove real wiring and database truth.
```

For MiniURLShortener:

```text
Unit test:
    Base62 encoder returns expected code.

Service test:
    Duplicate custom alias throws domain exception.

Controller test:
    Blank longUrl returns 400 VALIDATION_FAILED.

Repository test:
    UNIQUE(short_code) is enforced.

Integration test:
    POST creates URL and GET redirects to longUrl.
```

ASCII mental picture:

```text
              TESTING STRATEGY

       fast, many, isolated
               ^
               |
        Unit Tests
        Service Tests
        Controller Slice Tests
        Repository Tests
        Integration Tests
               |
               v
       slower, fewer, realistic
```

Do not choose only one kind of test.

A senior backend engineer mixes them intentionally.

---

## 3. Problem Statement

Build a production-level testing strategy for MiniURLShortener.

The testing strategy must verify:

```text
1. Create short URL happy path.
2. Redirect happy path.
3. DTO validation errors.
4. Business validation errors.
5. Duplicate custom alias conflict.
6. Missing short code.
7. Expired short code.
8. Blocked short code.
9. Transaction rollback.
10. Repository constraints.
11. API response shapes.
12. HTTP status code correctness.
13. JPA persistence behavior.
14. Test profile configuration.
15. Testcontainers PostgreSQL behavior.
```

It should avoid:

```text
1. Only testing happy paths.
2. Mocking everything.
3. Depending on local manually installed database.
4. Flaky tests.
5. Tests that know too much about implementation.
6. Tests with unreadable setup.
7. Tests without clear business names.
```

Out of scope for this chapter:

```text
1. Load testing with k6.
2. Chaos engineering.
3. Contract testing with Pact.
4. Browser UI tests.
5. Full CI/CD pipeline.
```

This chapter focuses on backend application tests.

---

## 4. Testing Pyramid Mental Model

The testing pyramid helps decide test quantity.

```text
                     +------------------+
                     | E2E / Full Flow  |
                     | few, slower      |
                     +------------------+
                  +-----------------------+
                  | Integration Tests     |
                  | DB + Spring context   |
                  +-----------------------+
              +------------------------------+
              | Slice Tests                  |
              | WebMvcTest / DataJpaTest     |
              +------------------------------+
          +--------------------------------------+
          | Unit + Service Tests                 |
          | many, fast, focused                  |
          +--------------------------------------+
```

Meaning:

```text
More unit tests.
Some slice tests.
Some integration tests.
Few full end-to-end tests.
```

Why not only E2E tests?

Because they are:

```text
slow
hard to debug
hard to isolate
expensive to run frequently
```

Why not only unit tests?

Because they do not prove:

```text
Spring wiring
JPA mapping
SQL constraints
transaction commit
HTTP serialization
validation annotations
exception handler integration
```

Balanced strategy:

```text
Unit tests: prove small rules.
Slice tests: prove one Spring layer.
Integration tests: prove real wiring.
```

For MiniURLShortener:

```text
Base62EncoderTest              -> pure unit
UrlValidatorServiceTest        -> pure unit/service
ShortUrlServiceTest            -> service with mocks or real DB depending behavior
ShortUrlControllerTest         -> @WebMvcTest
ShortUrlRepositoryTest         -> @DataJpaTest
ShortUrlIntegrationTest        -> @SpringBootTest + Testcontainers
```

---

## 5. What To Test In MiniURLShortener

Test by behavior, not by random methods.

Core behavior table:

```text
+------------------------------+----------------------------+-------------------+
| Behavior                     | Test Type                  | Why               |
+------------------------------+----------------------------+-------------------+
| Base62 encoding              | Unit                       | deterministic     |
| URL validation               | Unit                       | business rule     |
| Alias validation             | Unit                       | business rule     |
| Create URL service flow      | Service                    | orchestration     |
| Redirect service flow        | Service                    | domain decision   |
| DTO validation               | Controller slice           | HTTP boundary     |
| Error response contract      | Controller slice           | API contract      |
| Repository save/find         | DataJpaTest                | persistence truth |
| Unique short code constraint | DataJpaTest/Testcontainer  | DB invariant      |
| Full create + redirect       | SpringBootTest             | real flow         |
+------------------------------+----------------------------+-------------------+
```

Golden rule:

```text
Test public behavior from the nearest meaningful boundary.
```

Examples:

```text
Do not test private methods.
Do not assert every internal line.
Do assert the business outcome.
```

Bad test mindset:

```text
When method X calls method Y, verify exact internal call order.
```

Better test mindset:

```text
Given a valid longUrl, when create is called, then a shortUrl is persisted and returned.
```

---

## 6. Unit Tests

Unit tests test one small component without Spring.

Example target:

```text
Base62Encoder
UrlValidatorService
ShortCodeGenerator
AliasNormalizer
```

Unit tests should be:

```text
fast
simple
isolated
deterministic
no network
no database
no Spring context when not needed
```

Example Base62 encoder:

```java
package com.miniurl.shortener.url.service;

public class Base62Encoder {

    private static final String ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String encode(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("value must be non-negative");
        }

        if (value == 0) {
            return "0";
        }

        StringBuilder result = new StringBuilder();
        long current = value;

        while (current > 0) {
            int remainder = (int) (current % 62);
            result.append(ALPHABET.charAt(remainder));
            current = current / 62;
        }

        return result.reverse().toString();
    }
}
```

Unit test:

```java
package com.miniurl.shortener.url.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    void shouldEncodeZero() {
        assertThat(encoder.encode(0)).isEqualTo("0");
    }

    @Test
    void shouldEncodeBase62Boundary() {
        assertThat(encoder.encode(61)).isEqualTo("Z");
        assertThat(encoder.encode(62)).isEqualTo("10");
    }

    @Test
    void shouldRejectNegativeValue() {
        assertThatThrownBy(() -> encoder.encode(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be non-negative");
    }
}
```

ASCII:

```text
Input number
    |
    v
Base62Encoder
    |
    v
Expected short code
```

Unit test lesson:

```text
When the rule is pure, keep the test pure.
```

Do not start Spring just to test pure Java logic.

---

## 7. Service Tests

Service tests verify business orchestration.

Service layer decides:

```text
validate input
choose custom alias or generated code
check status
check expiry
call repository
throw domain exceptions
return response DTO
```

ASCII:

```text
ShortUrlService
    |
    +-- UrlValidatorService
    +-- ShortCodeGenerator
    +-- ShortUrlRepository
    +-- Clock
```

A service test can use mocks for dependencies when testing decision logic.

Example redirect service behavior:

```text
Given repository returns ACTIVE non-expired short URL
When resolveRedirect(shortCode)
Then return longUrl
```

Example test:

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.url.domain.ShortUrl;
import com.miniurl.shortener.url.domain.ShortUrlStatus;
import com.miniurl.shortener.url.repository.ShortUrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {

    @Mock
    private ShortUrlRepository repository;

    @Mock
    private UrlValidatorService validatorService;

    private final Clock clock = Clock.fixed(
            Instant.parse("2026-06-23T10:00:00Z"),
            ZoneOffset.UTC
    );

    @Test
    void shouldResolveActiveShortCode() {
        ShortUrl entity = new ShortUrl();
        entity.setShortCode("abc123");
        entity.setLongUrl("https://example.com/article");
        entity.setStatus(ShortUrlStatus.ACTIVE);
        entity.setExpiresAt(Instant.parse("2026-12-31T00:00:00Z"));

        when(repository.findByShortCode("abc123"))
                .thenReturn(Optional.of(entity));

        ShortUrlService service = new ShortUrlService(
                repository,
                validatorService,
                clock
        );

        String longUrl = service.resolveRedirectUrl("abc123");

        assertThat(longUrl).isEqualTo("https://example.com/article");
    }

    @Test
    void shouldThrowNotFoundWhenCodeMissing() {
        when(repository.findByShortCode("missing"))
                .thenReturn(Optional.empty());

        ShortUrlService service = new ShortUrlService(
                repository,
                validatorService,
                clock
        );

        assertThatThrownBy(() -> service.resolveRedirectUrl("missing"))
                .isInstanceOf(ShortCodeNotFoundException.class);
    }
}
```

Why inject `Clock`?

Because time-based tests must be deterministic.

Bad:

```java
Instant.now()
```

Better:

```java
clock.instant()
```

Testing expiry with real current time can become flaky.

---

## 8. Controller Tests With MockMvc

Controller tests verify the HTTP contract.

They answer:

```text
Does the endpoint accept the right request?
Does validation trigger?
Does JSON serialize correctly?
Does the correct status code return?
Does the error response shape match contract?
Does redirect return Location header?
```

Use:

```java
@WebMvcTest(ShortUrlController.class)
```

ASCII:

```text
Mock HTTP Request
        |
        v
Controller + Validation + Handler
        |
        v
Mock HTTP Response
```

Example controller:

```java
@RestController
@RequestMapping("/api/v1/urls")
public class ShortUrlController {

    private final ShortUrlService service;

    public ShortUrlController(ShortUrlService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CreateShortUrlResponse> create(
            @Valid @RequestBody CreateShortUrlRequest request
    ) {
        CreateShortUrlResponse response = service.createShortUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

MockMvc test for validation:

```java
package com.miniurl.shortener.url.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniurl.shortener.common.error.GlobalExceptionHandler;
import com.miniurl.shortener.url.service.ShortUrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShortUrlController.class)
@Import(GlobalExceptionHandler.class)
class ShortUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShortUrlService service;

    @Test
    void shouldReturnValidationErrorWhenLongUrlBlank() throws Exception {
        String body = """
                {
                  "longUrl": ""
                }
                """;

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("longUrl"));
    }
}
```

Important:

```text
Controller tests should verify HTTP behavior, not database behavior.
```

Do not make controller tests responsible for everything.

---

## 9. Repository Tests

Repository tests verify persistence behavior.

They answer:

```text
Does entity mapping work?
Does save generate ID?
Does findByShortCode work?
Does UNIQUE(short_code) work?
Does status enum persist correctly?
Does createdAt/updatedAt mapping work?
```

Use:

```java
@DataJpaTest
```

For production-like Postgres behavior, combine with Testcontainers.

Repository:

```java
package com.miniurl.shortener.url.repository;

import com.miniurl.shortener.url.domain.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);
}
```

Repository test:

```java
package com.miniurl.shortener.url.repository;

import com.miniurl.shortener.url.domain.ShortUrl;
import com.miniurl.shortener.url.domain.ShortUrlStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ShortUrlRepositoryTest {

    @Autowired
    private ShortUrlRepository repository;

    @Test
    void shouldFindByShortCode() {
        ShortUrl entity = new ShortUrl();
        entity.setShortCode("abc123");
        entity.setLongUrl("https://example.com");
        entity.setStatus(ShortUrlStatus.ACTIVE);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        repository.saveAndFlush(entity);

        assertThat(repository.findByShortCode("abc123"))
                .isPresent()
                .get()
                .extracting(ShortUrl::getLongUrl)
                .isEqualTo("https://example.com");
    }
}
```

ASCII:

```text
Entity object
    |
    v
Repository.save
    |
    v
Database row
    |
    v
Repository.findByShortCode
    |
    v
Entity object
```

Repository tests should be real database tests whenever constraints matter.

Mocks cannot prove database truth.

---

## 10. Integration Tests With Testcontainers

Integration tests prove the real application wiring.

Use:

```text
@SpringBootTest
@AutoConfigureMockMvc
Testcontainers PostgreSQL
```

Why Testcontainers?

Because H2 is not PostgreSQL.

H2 can hide bugs in:

```text
SQL syntax
indexes
constraints
enum mapping
transaction behavior
JSONB later
timestamp behavior
case sensitivity
```

ASCII:

```text
Test JVM
   |
   v
Spring Boot App Context
   |
   v
Real Repository / JPA / Transaction Manager
   |
   v
PostgreSQL Testcontainer
```

Example:

```java
package com.miniurl.shortener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class ShortUrlIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("miniurl_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateShortUrlAndRedirect() throws Exception {
        String body = """
                {
                  "longUrl": "https://example.com/article",
                  "customAlias": "article1"
                }
                """;

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("article1"));

        mockMvc.perform(get("/article1"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com/article"));
    }
}
```

This test proves:

```text
HTTP endpoint works
DTO deserialization works
validation passes
service works
transaction commits
JPA mapping works
Postgres stores row
redirect lookup works
302 Location works
```

It is slower than unit tests, but much more realistic.

---

## 11. Error Handling Tests

Error tests are as important as happy-path tests.

MiniURLShortener error contract:

```text
blank longUrl -> 400 VALIDATION_FAILED
invalid URL -> 400 INVALID_URL
bad alias -> 400 INVALID_ALIAS
duplicate alias -> 409 ALIAS_ALREADY_EXISTS
missing shortCode -> 404 SHORT_CODE_NOT_FOUND
expired shortCode -> 410 SHORT_CODE_EXPIRED
blocked shortCode -> 403 SHORT_CODE_BLOCKED
unexpected error -> 500 INTERNAL_ERROR
```

ASCII:

```text
Bad request / bad state
        |
        v
Exception thrown
        |
        v
GlobalExceptionHandler
        |
        v
Stable ApiErrorResponse
```

Controller-level error test:

```java
@Test
void shouldReturnConflictWhenAliasAlreadyExists() throws Exception {
    String body = """
            {
              "longUrl": "https://example.com",
              "customAlias": "taken"
            }
            """;

    when(service.createShortUrl(any()))
            .thenThrow(new CustomAliasAlreadyExistsException("taken"));

    mockMvc.perform(post("/api/v1/urls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("ALIAS_ALREADY_EXISTS"));
}
```

Integration-level duplicate alias test:

```java
@Test
void shouldReturnConflictForDuplicateCustomAlias() throws Exception {
    String firstBody = """
            {
              "longUrl": "https://example.com/one",
              "customAlias": "samealias"
            }
            """;

    String secondBody = """
            {
              "longUrl": "https://example.com/two",
              "customAlias": "samealias"
            }
            """;

    mockMvc.perform(post("/api/v1/urls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(firstBody))
            .andExpect(status().isCreated());

    mockMvc.perform(post("/api/v1/urls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(secondBody))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("ALIAS_ALREADY_EXISTS"));
}
```

Testing rule:

```text
Do not only assert HTTP status.
Assert stable error code also.
```

---

## 12. Transaction Boundary Tests

Transactions are not visible directly from outside.

But you can test their effects.

Transaction test asks:

```text
If service fails halfway, does partial data remain?
```

Example failure scenario:

```text
1. Start createShortUrl transaction.
2. Save ShortUrl entity.
3. Throw RuntimeException before method ends.
4. Transaction should rollback.
5. Repository should not find saved row.
```

ASCII:

```text
@Transactional service method
        |
        v
INSERT row
        |
        v
RuntimeException
        |
        v
ROLLBACK
        |
        v
No row in DB
```

Example idea:

```java
@Service
public class TestOnlyFailingShortUrlService {

    private final ShortUrlRepository repository;

    public TestOnlyFailingShortUrlService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void createThenFail() {
        ShortUrl entity = new ShortUrl();
        entity.setShortCode("rollback1");
        entity.setLongUrl("https://example.com");
        entity.setStatus(ShortUrlStatus.ACTIVE);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        repository.save(entity);

        throw new RuntimeException("boom after save");
    }
}
```

Test:

```java
@Test
void shouldRollbackWhenRuntimeExceptionOccurs() {
    assertThatThrownBy(() -> failingService.createThenFail())
            .isInstanceOf(RuntimeException.class)
            .hasMessage("boom after save");

    assertThat(repository.findByShortCode("rollback1"))
            .isEmpty();
}
```

Important nuance:

```text
Do not put @Transactional on the test method if you want to verify real commit/rollback behavior from service boundary.
```

Why?

Because test-level transaction can wrap everything and hide real behavior.

Production lesson:

```text
Test transaction effects from outside the service boundary.
```

---

## 13. Redirect Flow Tests

Redirect is special because success is not JSON.

Success response:

```http
HTTP/1.1 302 Found
Location: https://example.com/article
```

Controller:

```java
@GetMapping("/{shortCode}")
public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
    String longUrl = service.resolveRedirectUrl(shortCode);

    return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(longUrl))
            .build();
}
```

Test:

```java
@Test
void shouldRedirectToLongUrl() throws Exception {
    when(service.resolveRedirectUrl("abc123"))
            .thenReturn("https://example.com/article");

    mockMvc.perform(get("/abc123"))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", "https://example.com/article"));
}
```

Missing code test:

```java
@Test
void shouldReturnNotFoundWhenShortCodeDoesNotExist() throws Exception {
    when(service.resolveRedirectUrl("missing"))
            .thenThrow(new ShortCodeNotFoundException("missing"));

    mockMvc.perform(get("/missing"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("SHORT_CODE_NOT_FOUND"));
}
```

ASCII:

```text
GET /abc123
    |
    v
Controller
    |
    v
Service resolves longUrl
    |
    v
302 + Location header
```

Testing rule:

```text
For redirect happy path, assert Location header.
For redirect failure path, assert error JSON.
```

---

## 14. Create URL Flow Tests

Create API success response:

```http
HTTP/1.1 201 Created
Content-Type: application/json
```

JSON:

```json
{
  "shortCode": "abc123",
  "shortUrl": "http://localhost:8080/abc123",
  "longUrl": "https://example.com/article"
}
```

Controller test:

```java
@Test
void shouldCreateShortUrl() throws Exception {
    CreateShortUrlResponse response = new CreateShortUrlResponse(
            "abc123",
            "http://localhost:8080/abc123",
            "https://example.com/article"
    );

    when(service.createShortUrl(any()))
            .thenReturn(response);

    String body = """
            {
              "longUrl": "https://example.com/article"
            }
            """;

    mockMvc.perform(post("/api/v1/urls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.shortCode").value("abc123"))
            .andExpect(jsonPath("$.longUrl").value("https://example.com/article"));
}
```

Integration test with real DB:

```text
1. POST valid request.
2. Assert 201.
3. Extract shortCode.
4. Query repository or call GET /{shortCode}.
5. Assert redirect works.
```

ASCII:

```text
POST /api/v1/urls
        |
        v
201 response contains shortCode
        |
        v
GET /{shortCode}
        |
        v
302 Location = original longUrl
```

This proves create and redirect are connected.

---

## 15. Test Data Builder Pattern

Tests become messy when object creation repeats everywhere.

Bad setup:

```java
ShortUrl entity = new ShortUrl();
entity.setShortCode("abc123");
entity.setLongUrl("https://example.com");
entity.setStatus(ShortUrlStatus.ACTIVE);
entity.setCreatedAt(Instant.now());
entity.setUpdatedAt(Instant.now());
entity.setExpiresAt(null);
```

Repeated in 20 tests, this becomes noise.

Use a test data builder.

```java
package com.miniurl.shortener.testsupport;

import com.miniurl.shortener.url.domain.ShortUrl;
import com.miniurl.shortener.url.domain.ShortUrlStatus;

import java.time.Instant;

public class ShortUrlTestBuilder {

    private String shortCode = "abc123";
    private String longUrl = "https://example.com";
    private ShortUrlStatus status = ShortUrlStatus.ACTIVE;
    private Instant expiresAt = null;

    public static ShortUrlTestBuilder aShortUrl() {
        return new ShortUrlTestBuilder();
    }

    public ShortUrlTestBuilder withShortCode(String shortCode) {
        this.shortCode = shortCode;
        return this;
    }

    public ShortUrlTestBuilder withLongUrl(String longUrl) {
        this.longUrl = longUrl;
        return this;
    }

    public ShortUrlTestBuilder withStatus(ShortUrlStatus status) {
        this.status = status;
        return this;
    }

    public ShortUrlTestBuilder withExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public ShortUrl build() {
        ShortUrl entity = new ShortUrl();
        entity.setShortCode(shortCode);
        entity.setLongUrl(longUrl);
        entity.setStatus(status);
        entity.setExpiresAt(expiresAt);
        entity.setCreatedAt(Instant.parse("2026-06-23T10:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-06-23T10:00:00Z"));
        return entity;
    }
}
```

Usage:

```java
ShortUrl expired = aShortUrl()
        .withShortCode("expired1")
        .withExpiresAt(Instant.parse("2026-01-01T00:00:00Z"))
        .build();
```

ASCII:

```text
Readable test setup
        |
        v
Clear behavior test
        |
        v
Easy debugging
```

Builder rule:

```text
Test setup should explain the business case, not distract from it.
```

---

## 16. Test Profiles And Configuration

Tests need controlled configuration.

Create:

```text
src/test/resources/application-test.yml
```

Example:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: true
  flyway:
    enabled: true

miniurl:
  app:
    base-url: http://localhost:8080
  short-code:
    length: 7
    max-generation-attempts: 5
```

Use profile:

```java
@ActiveProfiles("test")
```

Why test config matters:

```text
1. Avoid connecting to dev/prod DB accidentally.
2. Use deterministic app base URL.
3. Tune shorter timeouts.
4. Enable schema migration validation.
5. Keep tests repeatable.
```

ASCII:

```text
Test starts
    |
    v
application-test.yml loaded
    |
    v
safe test DB + safe config
```

Never let tests depend on production secrets.

Bad:

```text
Tests read real DB URL from environment accidentally.
```

Better:

```text
Tests use Testcontainers dynamic DB URL.
```

---

## 17. Naming Strategy

Good test names explain behavior.

Bad names:

```text
testCreate()
test1()
shouldWork()
```

Better names:

```text
shouldCreateShortUrlForValidLongUrl()
shouldReturnBadRequestWhenLongUrlIsBlank()
shouldReturnConflictWhenCustomAliasAlreadyExists()
shouldRedirectToLongUrlWhenShortCodeIsActive()
shouldReturnGoneWhenShortCodeIsExpired()
shouldRollbackWhenCreateFailsAfterSave()
```

Use Given/When/Then structure:

```java
@Test
void shouldReturnGoneWhenShortCodeIsExpired() {
    // given
    // when
    // then
}
```

ASCII:

```text
Given known state
    |
    v
When action happens
    |
    v
Then expected outcome
```

Naming rule:

```text
A failing test name should tell you the broken business behavior immediately.
```

---

## 18. Step-by-Step Dry Runs

### Dry Run 1: Unit test catches Base62 bug

Bug:

```text
encode(62) returns "01" instead of "10"
```

Test:

```text
shouldEncodeBase62Boundary
```

Flow:

```text
1. Test calls encoder.encode(62).
2. Encoder divides 62 by base 62.
3. Buggy code does not reverse result.
4. Actual result is 01.
5. Expected result is 10.
6. Test fails immediately.
```

Lesson:

```text
Pure logic bugs should be caught by small fast tests.
```

---

### Dry Run 2: Controller test catches missing @Valid

Controller method:

```java
public ResponseEntity<?> create(@RequestBody CreateShortUrlRequest request)
```

Missing:

```java
@Valid
```

Test sends:

```json
{
  "longUrl": ""
}
```

Expected:

```text
400 VALIDATION_FAILED
```

Actual:

```text
service gets invalid request
```

Flow:

```text
1. MockMvc sends blank longUrl.
2. Bean validation does not run.
3. Controller calls service.
4. Test expected 400 but response differs.
5. Test catches missing @Valid.
```

Lesson:

```text
Controller tests protect HTTP boundary wiring.
```

---

### Dry Run 3: Repository test catches missing unique constraint

Bug:

```text
short_code column has no UNIQUE constraint.
```

Test:

```text
insert same short_code twice
```

Expected:

```text
second insert fails
```

Actual:

```text
both rows inserted
```

Flow:

```text
1. Test saves row with short_code abc123.
2. Test saves second row with same short_code.
3. DB allows duplicate.
4. Test fails.
```

Lesson:

```text
Only real DB tests can prove database constraints.
```

---

### Dry Run 4: Integration test catches wrong redirect response

Bug:

```text
GET /abc123 returns 200 JSON instead of 302 redirect.
```

Test:

```text
POST create URL
GET short code
assert 302 and Location header
```

Flow:

```text
1. POST creates row.
2. GET /abc123 resolves longUrl.
3. Controller returns wrong response type.
4. Test expected 302.
5. Test fails.
```

Lesson:

```text
Full flow tests protect real user-visible behavior.
```

---

## 19. Internal Execution Walkthrough

Full integration test execution:

```text
1. JUnit starts test.
2. Testcontainers starts PostgreSQL container.
3. Spring Boot application context starts.
4. DynamicPropertySource injects DB URL.
5. Flyway migrations create schema.
6. MockMvc sends HTTP request to DispatcherServlet.
7. Controller receives request.
8. Validation runs.
9. Service starts transaction.
10. Repository saves entity.
11. JPA flushes SQL.
12. PostgreSQL stores row.
13. Transaction commits.
14. Response returns to MockMvc.
15. Test asserts status/body/header.
```

ASCII:

```text
JUnit
  |
  v
Testcontainers PostgreSQL
  |
  v
Spring Context
  |
  v
MockMvc Request
  |
  v
Controller
  |
  v
Service Transaction
  |
  v
Repository / JPA
  |
  v
PostgreSQL
  |
  v
MockMvc Assertions
```

This is why integration tests are powerful.

They prove the chain, not only one link.

But they are slower, so keep them focused.

---

## 20. Production Failure Stories

### Failure Story 1: H2 tests passed, PostgreSQL failed

Team used H2 for tests.

Production used PostgreSQL.

A query worked in H2 but failed in PostgreSQL because of SQL dialect differences.

Root cause:

```text
Tests did not use production-like database.
```

Fix:

```text
Use Testcontainers PostgreSQL for repository and integration tests.
```

Lesson:

```text
If database behavior matters, test with the real database engine.
```

---

### Failure Story 2: Duplicate aliases in production

Application checked:

```text
existsByShortCode(alias)
```

Then inserted.

Under concurrent requests, two pods inserted the same alias.

Root cause:

```text
No DB unique constraint and no concurrency/integration test.
```

Fix:

```text
Add UNIQUE(short_code), test duplicate insert, map violation to 409.
```

Lesson:

```text
Application checks are not enough under race conditions.
```

---

### Failure Story 3: Validation annotations existed but did not run

DTO had:

```java
@NotBlank
```

Controller missed:

```java
@Valid
```

Invalid requests entered service.

Root cause:

```text
No controller validation test.
```

Fix:

```text
MockMvc test for blank longUrl -> 400 VALIDATION_FAILED.
```

Lesson:

```text
Annotations are not behavior until the framework runs them.
```

---

### Failure Story 4: Expiry test became flaky

Test used:

```java
Instant.now().minusSeconds(1)
```

Sometimes CI machines were slow and the test behaved unpredictably.

Root cause:

```text
Real system time used in business tests.
```

Fix:

```text
Inject Clock and use Clock.fixed in tests.
```

Lesson:

```text
Time must be controlled in tests.
```

---

### Failure Story 5: Tests passed but API clients broke

Developers changed error response field:

```text
code -> errorCode
```

Tests only checked status code.

Clients depended on `code`.

Root cause:

```text
Tests did not assert response contract.
```

Fix:

```text
Assert JSON fields: code, message, status, path, fieldErrors.
```

Lesson:

```text
API contract tests must check shape, not only status.
```

---

## 21. Debugging Mindset

When a test fails, ask:

```text
Which layer is failing?
Is it logic, HTTP contract, persistence, transaction, or configuration?
Is the test too broad?
Is the setup correct?
Is time controlled?
Is database state clean?
Is the assertion testing business behavior?
Is the failure deterministic or flaky?
```

Debug map:

```text
Unit test failing:
    pure logic bug or wrong expected value

Controller test failing:
    mapping, validation, serialization, exception handler, status code

Repository test failing:
    entity mapping, schema, constraint, query method

Integration test failing:
    wiring, config, transaction, migration, real DB behavior

Flaky test:
    time, order dependency, shared state, async behavior, random data
```

Useful commands:

```bash
mvn test
mvn test -Dtest=ShortUrlControllerTest
mvn test -Dtest=ShortUrlIntegrationTest#shouldCreateShortUrlAndRedirect
```

Useful debugging checks:

```text
Check application-test.yml.
Check active profile.
Check Testcontainers logs.
Check generated SQL.
Check transaction boundaries.
Check if test data is unique.
Check if @MockBean is replacing the right bean.
```

Golden rule:

```text
A good test failure should point toward the broken layer.
```

---

## 22. Common Mistakes

### Mistake 1: Only happy-path testing

Wrong:

```text
Only valid create and redirect tests.
```

Correct:

```text
Test invalid, duplicate, missing, expired, blocked, rollback.
```

### Mistake 2: Mocking the database for persistence rules

Wrong:

```text
Mock repository and assume UNIQUE works.
```

Correct:

```text
Use real PostgreSQL test for DB constraints.
```

### Mistake 3: Starting Spring for pure logic

Wrong:

```java
@SpringBootTest
class Base62EncoderTest
```

Correct:

```java
class Base62EncoderTest
```

### Mistake 4: Not asserting error code

Wrong:

```java
.andExpect(status().isBadRequest())
```

Correct:

```java
.andExpect(status().isBadRequest())
.andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
```

### Mistake 5: Flaky time tests

Wrong:

```java
Instant.now()
```

Correct:

```java
Clock.fixed(...)
```

### Mistake 6: Tests depend on order

Wrong:

```text
Test B requires Test A to run first.
```

Correct:

```text
Each test creates its own data.
```

### Mistake 7: Using production config in tests

Wrong:

```text
Tests connect to shared dev DB.
```

Correct:

```text
Tests use isolated Testcontainers DB.
```

### Mistake 8: Testing private methods

Wrong:

```text
Reflectively call private helper.
```

Correct:

```text
Test public behavior that depends on helper.
```

---

## 23. Interview-Ready Explanation

If interviewer asks:

```text
How would you test your URL shortener backend?
```

Strong answer:

```text
I would use a layered testing strategy. Pure rules like Base62 encoding, alias
validation, and URL validation get fast unit tests without Spring. Service tests
verify business decisions such as not found, blocked, expired, and duplicate alias
behavior, with a fixed Clock for deterministic expiry checks. Controller tests
with MockMvc verify the HTTP contract: validation, status codes, JSON response
shape, and redirect Location headers. Repository tests and integration tests use
PostgreSQL Testcontainers instead of only H2, because database constraints,
JPA mapping, transactions, and SQL behavior must match production. I would also
test error responses deeply, not just happy paths: 400 validation, 409 duplicate
alias, 404 missing code, 410 expired, and 403 blocked. For full confidence, I
would keep a few end-to-end integration tests that create a short URL and then
resolve it through the redirect endpoint.
```

Why this is strong:

```text
1. Shows test pyramid thinking.
2. Separates unit, slice, repository, and integration tests.
3. Mentions Testcontainers and production-like DB behavior.
4. Tests negative paths.
5. Understands API contract testing.
6. Understands deterministic time with Clock.
7. Shows transaction and constraint awareness.
```

Senior one-liner:

```text
I test small rules fast, HTTP contracts at the web boundary, and database truth with real Postgres integration tests.
```

---

## 24. Senior Engineer Checklist

Before moving on, confirm:

```text
[ ] Base62Encoder has unit tests
[ ] UrlValidatorService has unit tests
[ ] ShortCodeGenerator has tests
[ ] Create API has controller tests
[ ] Redirect API has controller tests
[ ] DTO validation errors are tested
[ ] Global exception response shape is tested
[ ] Duplicate alias returns 409 test exists
[ ] Missing code returns 404 test exists
[ ] Expired code returns 410 test exists
[ ] Blocked code returns 403 test exists
[ ] Repository findByShortCode test exists
[ ] UNIQUE short_code constraint test exists
[ ] Full create + redirect integration test exists
[ ] Tests use application-test.yml
[ ] Integration tests use Testcontainers PostgreSQL
[ ] Time-based tests use fixed Clock
[ ] Tests do not depend on execution order
[ ] Test data builders exist for repeated setup
[ ] API tests assert JSON code, not only status
```

If these are checked, your MiniURLShortener testing model is production-shaped.

---

## 25. One-Page Cheat Sheet

```text
Core mental model:
Testing is executable safety net.

Testing layers:
Unit tests:
    pure logic, fast, no Spring

Service tests:
    business decisions, mocks where useful, fixed Clock

Controller tests:
    HTTP status, validation, JSON shape, redirect headers

Repository tests:
    JPA mapping, queries, constraints

Integration tests:
    real Spring context + real PostgreSQL Testcontainer

MiniURLShortener must test:
create success -> 201
redirect success -> 302 Location
blank URL -> 400 VALIDATION_FAILED
invalid URL -> 400 INVALID_URL
bad alias -> 400 INVALID_ALIAS
duplicate alias -> 409 ALIAS_ALREADY_EXISTS
missing shortCode -> 404 SHORT_CODE_NOT_FOUND
expired shortCode -> 410 SHORT_CODE_EXPIRED
blocked shortCode -> 403 SHORT_CODE_BLOCKED
rollback -> no partial row
unique constraint -> duplicate rejected

Rules:
Do not start Spring for pure logic.
Do not mock DB constraints.
Do not test only status code.
Do not use real current time in expiry tests.
Do not depend on test order.
Use Testcontainers for production-like DB tests.
Use test data builders for readability.
```

---

## 26. One Picture To Remember

```text
                 MINIURLSHORTENER TESTING STRATEGY

                         User Behavior
                              |
                              v
+----------------------------------------------------------------+
| Full Integration Tests                                          |
| POST create -> DB commit -> GET redirect -> 302 Location        |
+----------------------------------------------------------------+
                              |
                              v
+----------------------------------------------------------------+
| Controller Tests                                                |
| HTTP status, validation, JSON error shape, redirect headers     |
+----------------------------------------------------------------+
                              |
                              v
+----------------------------------------------------------------+
| Service Tests                                                   |
| create logic, redirect decision, expired/blocked/not found      |
+----------------------------------------------------------------+
                              |
                              v
+----------------------------------------------------------------+
| Repository Tests                                                |
| JPA mapping, findByShortCode, UNIQUE(short_code), constraints   |
+----------------------------------------------------------------+
                              |
                              v
+----------------------------------------------------------------+
| Unit Tests                                                      |
| Base62, validators, generators, pure business rules             |
+----------------------------------------------------------------+

FINAL MEMORY:

Small tests catch logic bugs fast.
Boundary tests protect API contracts.
Real DB tests protect production truth.
Integration tests prove the whole chain.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Testing is the executable safety net of MiniURLShortener.
2. Unit tests prove small rules; integration tests prove real wiring.
3. MockMvc protects HTTP contracts like validation, status, JSON, and Location headers.
4. Testcontainers PostgreSQL protects database truth better than only H2.
5. A production backend must test failure paths as seriously as happy paths.
```

Next chapter:

```text
019_Testcontainers.md
```
