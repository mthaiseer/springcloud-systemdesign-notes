# 019_Integration_Testcontainers.md
# MiniURLShortener — Integration Testing with Testcontainers

> Core mental model: **Integration tests prove that your application works with real external dependencies, not fake memories of them. Testcontainers gives each test run a disposable production-like dependency, such as PostgreSQL, so your Spring Boot code, SQL schema, transactions, repositories, and HTTP APIs are tested together.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Unit Test vs Slice Test vs Integration Test](#3-unit-test-vs-slice-test-vs-integration-test)
- [4. Why H2 Is Not Enough](#4-why-h2-is-not-enough)
- [5. Testcontainers Mental Model](#5-testcontainers-mental-model)
- [6. MiniURLShortener Testing Goal](#6-miniurlshortener-testing-goal)
- [7. Dependency Setup](#7-dependency-setup)
- [8. PostgreSQL Container Wiring](#8-postgresql-container-wiring)
- [9. Spring Boot Dynamic Properties](#9-spring-boot-dynamic-properties)
- [10. Application Test Flow](#10-application-test-flow)
- [11. Create URL Integration Test](#11-create-url-integration-test)
- [12. Redirect Integration Test](#12-redirect-integration-test)
- [13. Error Handling Integration Test](#13-error-handling-integration-test)
- [14. Duplicate Alias Conflict Test](#14-duplicate-alias-conflict-test)
- [15. Transaction Boundary Verification](#15-transaction-boundary-verification)
- [16. Database State Verification](#16-database-state-verification)
- [17. Test Data Strategy](#17-test-data-strategy)
- [18. Cleanup Strategy](#18-cleanup-strategy)
- [19. Full Execution Dry Runs](#19-full-execution-dry-runs)
- [20. Internal Execution Walkthrough](#20-internal-execution-walkthrough)
- [21. Production Failure Stories](#21-production-failure-stories)
- [22. Debugging Mindset](#22-debugging-mindset)
- [23. Common Mistakes](#23-common-mistakes)
- [24. CI/CD Considerations](#24-cicd-considerations)
- [25. Interview-Ready Explanation](#25-interview-ready-explanation)
- [26. Senior Engineer Checklist](#26-senior-engineer-checklist)
- [27. One-Page Cheat Sheet](#27-one-page-cheat-sheet)
- [28. One Picture To Remember](#28-one-picture-to-remember)

---

## 1. Why This Exists

In previous chapters, MiniURLShortener has important production pieces:

```text
Controller
Service
Repository
DTO validation
Global exception handler
JPA / JDBC decisions
Transaction boundaries
Config properties
Actuator health checks
API versioning
Testing strategy
```

But many bugs do not appear in isolated unit tests.

Example unit test passes:

```text
UrlServiceTest with mocked repository
```

But production fails because:

```text
PostgreSQL unique constraint behaves differently
Flyway migration missing
Column name mismatch
Transaction rollback not happening
JSON serialization issue
Validation not triggered
Controller path wrong
Repository query wrong
Database index missing
Wrong profile configuration
```

A unit test often answers:

```text
Does this class behave when dependencies are mocked?
```

An integration test answers:

```text
Does the real Spring Boot application work with real infrastructure?
```

For MiniURLShortener, integration testing must prove:

```text
POST /api/v1/urls stores a row in PostgreSQL.
GET /{shortCode} returns redirect when row exists.
Duplicate custom alias returns 409.
Invalid request returns standard error JSON.
Expired URL returns 410.
Blocked URL returns 403.
Transaction rollback keeps database clean after failure.
```

The goal is not to test every tiny branch.

The goal is to verify the real wiring.

Production mental model:

```text
Unit tests protect logic.
Integration tests protect wiring.
Testcontainers protects dependency realism.
```

---

## 2. The One Core Mental Model

Testcontainers means:

```text
REAL DEPENDENCY, DISPOSABLE LIFETIME
```

Instead of pretending PostgreSQL exists, the test starts a real PostgreSQL container.

ASCII:

```text
                 Integration Test Run

+-----------------------------+
| JUnit Test                  |
+-------------+---------------+
              |
              v
+-----------------------------+
| Testcontainers              |
| starts PostgreSQL Docker    |
+-------------+---------------+
              |
              v
+-----------------------------+
| Spring Boot Test Context    |
| uses container JDBC URL     |
+-------------+---------------+
              |
              v
+-----------------------------+
| MiniURLShortener App        |
| Controller-Service-Repo     |
+-------------+---------------+
              |
              v
+-----------------------------+
| Real PostgreSQL             |
| schema + constraints + SQL  |
+-----------------------------+
```

One-line memory:

```text
Testcontainers turns external dependency assumptions into executable proof.
```

Without Testcontainers:

```text
"I think my repository works."
```

With Testcontainers:

```text
"My repository worked against real PostgreSQL during the build."
```

---

## 3. Unit Test vs Slice Test vs Integration Test

Testing levels:

```text
Unit test:
    one class
    dependencies mocked
    fast
    checks logic

Slice test:
    one Spring slice
    example: @WebMvcTest or @DataJpaTest
    partial context
    checks framework integration for that slice

Integration test:
    full or near-full app context
    real database container
    checks end-to-end behavior
```

ASCII:

```text
+----------------------+-------------------+----------------------------+
| Test Type            | Scope             | Example                    |
+----------------------+-------------------+----------------------------+
| Unit                 | Service only      | UrlService with mocks      |
| Web slice            | Controller layer  | MockMvc + mocked service   |
| JPA slice            | Repository layer  | @DataJpaTest + Postgres    |
| Integration          | Full app flow     | MockMvc + real DB          |
+----------------------+-------------------+----------------------------+
```

MiniURLShortener should use all three.

But this chapter focuses on:

```text
Full Spring Boot integration tests with PostgreSQL Testcontainers.
```

Why?

Because URL shortener correctness depends heavily on database behavior:

```text
unique short_code
status constraint
expires_at comparison
transaction rollback
JPA mapping
SQL dialect
```

---

## 4. Why H2 Is Not Enough

H2 is useful for small experiments.

But H2 is not PostgreSQL.

Problems:

```text
Different SQL dialect.
Different JSON support.
Different index behavior.
Different transaction behavior in edge cases.
Different constraint error messages.
Different timestamp behavior.
Different sequence behavior.
Different locking behavior.
```

Example:

```sql
CREATE INDEX CONCURRENTLY ...
```

This is PostgreSQL-specific.

H2 may not support it.

Another example:

```text
PostgreSQL uses real timestamptz behavior.
H2 may behave differently with timezone conversion.
```

ASCII:

```text
Test with H2:
    App -> H2 imitation

Test with Testcontainers:
    App -> Real PostgreSQL
```

Important rule:

```text
If production uses PostgreSQL, high-value integration tests should use PostgreSQL.
```

H2 can hide bugs.

Testcontainers exposes them earlier.

---

## 5. Testcontainers Mental Model

Testcontainers is a Java library that controls Docker during tests.

Lifecycle:

```text
1. JUnit starts.
2. Testcontainers pulls image if needed.
3. PostgreSQL container starts.
4. Container exposes mapped port.
5. Spring receives dynamic JDBC URL.
6. Application context starts.
7. Test executes HTTP/API calls.
8. Test verifies response and DB state.
9. Container stops or is reused.
```

ASCII:

```text
JUnit
 |
 v
Start PostgreSQL Container
 |
 v
Get runtime JDBC URL
 |
 v
Inject into Spring Boot
 |
 v
Run migrations / create schema
 |
 v
Run tests
 |
 v
Stop container
```

Key point:

```text
The database port is dynamic.
Do not hardcode localhost:5432.
```

Testcontainers gives:

```text
jdbc url
username
password
mapped port
container lifecycle
```

Spring Boot test uses those dynamically.

---

## 6. MiniURLShortener Testing Goal

For MiniURLShortener, Testcontainers should prove these flows:

```text
Create flow:
    HTTP JSON -> controller -> service -> repository -> PostgreSQL -> JSON response

Redirect flow:
    HTTP GET -> controller -> service -> repository -> PostgreSQL -> 302 Location

Validation flow:
    bad request -> validation -> GlobalExceptionHandler -> standard JSON

Conflict flow:
    duplicate alias -> DB unique constraint -> domain exception -> 409 JSON

Expiry flow:
    expired row -> service check -> 410 JSON

Blocked flow:
    blocked row -> service check -> 403 JSON
```

ASCII:

```text
+--------+     +------------+     +----------+     +------------+     +------------+
| MockMvc| --> | Controller | --> | Service  | --> | Repository | --> | PostgreSQL |
+--------+     +------------+     +----------+     +------------+     +------------+
    ^                                                                         |
    |                                                                         |
    +--------------------------- response / DB verification ------------------+
```

The test should catch:

```text
wrong endpoint path
missing @Valid
wrong JSON fields
wrong status code
wrong exception mapping
wrong table/column mapping
wrong unique constraint
wrong transaction behavior
```

---

## 7. Dependency Setup

Maven dependencies:

```xml
<dependencies>
    <!-- Spring Boot integration testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Testcontainers core -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>testcontainers</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- JUnit 5 integration -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- PostgreSQL container support -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

If using Spring Boot dependency management, versions are usually managed.

If not, add Testcontainers BOM:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers-bom</artifactId>
            <version>1.20.4</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Project structure:

```text
src
 ├── main
 │   └── java
 │       └── com/miniurl/shortener
 │
 └── test
     └── java
         └── com/miniurl/shortener
             └── UrlIntegrationTest.java
```

---

## 8. PostgreSQL Container Wiring

Create a reusable base integration test.

```java
package com.miniurl.shortener;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {

    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("miniurl_test")
                    .withUsername("test")
                    .withPassword("test");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

What this does:

```text
1. Starts PostgreSQL container.
2. Reads runtime JDBC URL.
3. Injects URL into Spring Boot before app context starts.
4. Spring Data JPA uses container database.
```

ASCII:

```text
PostgreSQLContainer
      |
      | getJdbcUrl()
      v
DynamicPropertyRegistry
      |
      v
Spring Boot datasource
      |
      v
Repository uses real PostgreSQL
```

Important:

```text
DynamicPropertySource runs before Spring creates DataSource.
```

That is why it can override `application-test.yml`.

---

## 9. Spring Boot Dynamic Properties

The container does not always run on port 5432.

Example runtime URL:

```text
jdbc:postgresql://localhost:49157/miniurl_test
```

The mapped port may change each run.

Wrong:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/miniurl_test
```

Correct:

```java
registry.add("spring.datasource.url", postgres::getJdbcUrl);
```

ASCII:

```text
Docker internal:
    postgres container:5432

Host mapped:
    localhost:49157

Spring Boot must use:
    localhost:49157
```

Dynamic property mapping:

```text
container knows mapped port
        |
        v
Testcontainers creates JDBC URL
        |
        v
Spring Boot receives it
        |
        v
DataSource connects successfully
```

For JPA:

```java
registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
```

If using Flyway:

```java
registry.add("spring.flyway.enabled", () -> "true");
```

If not using Flyway yet for learning:

```java
registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
```

Production-like preference:

```text
Use Flyway migrations + ddl-auto=validate.
```

Learning shortcut:

```text
Use ddl-auto=create-drop initially.
```

---

## 10. Application Test Flow

A full integration test usually uses:

```text
@SpringBootTest
@AutoConfigureMockMvc
Testcontainers PostgreSQL
MockMvc
ObjectMapper
Repository for verification
```

Example skeleton:

```java
package com.miniurl.shortener.url;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniurl.shortener.BaseIntegrationTest;
import com.miniurl.shortener.url.repository.ShortUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public class UrlIntegrationTest extends BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ShortUrlRepository shortUrlRepository;

    @BeforeEach
    void cleanDatabase() {
        shortUrlRepository.deleteAll();
    }
}
```

Flow:

```text
Test method
  |
  v
MockMvc performs HTTP request
  |
  v
Real Spring MVC dispatch
  |
  v
Controller
  |
  v
Service
  |
  v
Repository
  |
  v
PostgreSQL container
  |
  v
Response asserted
```

Why MockMvc?

```text
It exercises Spring MVC without starting a real network server.
```

Alternative:

```text
@SpringBootTest(webEnvironment = RANDOM_PORT)
TestRestTemplate or WebTestClient
```

Both are valid.

MockMvc is fast and excellent for API contract tests.

---

## 11. Create URL Integration Test

Assume API:

```http
POST /api/v1/urls
Content-Type: application/json
```

Request:

```json
{
  "longUrl": "https://example.com/article",
  "customAlias": "article1"
}
```

Expected:

```text
201 Created
response contains shortCode = article1
database has row
```

Test:

```java
package com.miniurl.shortener.url;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UrlCreateIntegrationTest extends UrlIntegrationTest {

    @Test
    void createShortUrl_shouldStoreRowAndReturnResponse() throws Exception {
        Map<String, Object> request = Map.of(
                "longUrl", "https://example.com/article",
                "customAlias", "article1"
        );

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("article1"))
                .andExpect(jsonPath("$.shortUrl").exists())
                .andExpect(jsonPath("$.longUrl").value("https://example.com/article"));

        boolean exists = shortUrlRepository.existsByShortCode("article1");

        assertThat(exists).isTrue();
    }
}
```

What this proves:

```text
Controller path works.
JSON request deserializes.
Validation allows valid input.
Service creates URL.
Repository writes to real PostgreSQL.
Response JSON shape works.
```

ASCII:

```text
POST /api/v1/urls
      |
      v
CreateShortUrlRequest
      |
      v
UrlService.createShortUrl()
      |
      v
INSERT INTO short_urls
      |
      v
201 JSON response
```

---

## 12. Redirect Integration Test

Redirect behavior:

```http
GET /article1
```

Expected:

```text
302 Found
Location: https://example.com/article
```

Setup row first.

Example entity creation:

```java
package com.miniurl.shortener.url;

import com.miniurl.shortener.url.entity.ShortUrl;
import com.miniurl.shortener.url.entity.ShortUrlStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UrlRedirectIntegrationTest extends UrlIntegrationTest {

    @Test
    void redirect_shouldReturn302WithLocationHeader() throws Exception {
        ShortUrl entity = new ShortUrl();
        entity.setShortCode("article1");
        entity.setLongUrl("https://example.com/article");
        entity.setStatus(ShortUrlStatus.ACTIVE);
        entity.setCreatedAt(Instant.now());

        shortUrlRepository.save(entity);

        mockMvc.perform(get("/article1"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com/article"));
    }
}
```

What this proves:

```text
Path variable mapping works.
Repository lookup works.
Status ACTIVE is accepted.
Controller produces redirect response.
Location header is correct.
```

ASCII:

```text
GET /article1
  |
  v
RedirectController
  |
  v
UrlService.resolveRedirect("article1")
  |
  v
SELECT row FROM short_urls
  |
  v
302 Location: longUrl
```

---

## 13. Error Handling Integration Test

Invalid request:

```json
{
  "longUrl": ""
}
```

Expected:

```text
400 Bad Request
code = VALIDATION_FAILED
fieldErrors contains longUrl
```

Test:

```java
package com.miniurl.shortener.url;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UrlValidationIntegrationTest extends UrlIntegrationTest {

    @Test
    void createShortUrl_withBlankLongUrl_shouldReturnValidationError() throws Exception {
        Map<String, Object> request = Map.of(
                "longUrl", ""
        );

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("longUrl"));
    }
}
```

What this proves:

```text
@Valid is active.
Bean Validation dependency works.
GlobalExceptionHandler catches MethodArgumentNotValidException.
API error response contract is stable.
```

ASCII:

```text
Blank longUrl
   |
   v
@Valid fails before service
   |
   v
MethodArgumentNotValidException
   |
   v
GlobalExceptionHandler
   |
   v
400 VALIDATION_FAILED
```

---

## 14. Duplicate Alias Conflict Test

Duplicate alias is important because it touches:

```text
business logic
database unique constraint
exception translation
HTTP conflict response
```

Test:

```java
package com.miniurl.shortener.url;

import com.miniurl.shortener.url.entity.ShortUrl;
import com.miniurl.shortener.url.entity.ShortUrlStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DuplicateAliasIntegrationTest extends UrlIntegrationTest {

    @Test
    void createShortUrl_withDuplicateAlias_shouldReturn409() throws Exception {
        ShortUrl existing = new ShortUrl();
        existing.setShortCode("samealias");
        existing.setLongUrl("https://first.com");
        existing.setStatus(ShortUrlStatus.ACTIVE);
        existing.setCreatedAt(Instant.now());

        shortUrlRepository.save(existing);

        Map<String, Object> request = Map.of(
                "longUrl", "https://second.com",
                "customAlias", "samealias"
        );

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ALIAS_ALREADY_EXISTS"));
    }
}
```

ASCII:

```text
Existing DB row:
    short_code = samealias

New request:
    customAlias = samealias
        |
        v
Insert / availability check
        |
        v
Conflict detected
        |
        v
409 ALIAS_ALREADY_EXISTS
```

This test is high-value because duplicate alias issues often appear only with real DB constraints.

---

## 15. Transaction Boundary Verification

Transactions are not just theory.

Integration tests should prove rollback behavior.

Example scenario:

```text
Service saves ShortUrl.
Then later step throws RuntimeException.
Transaction should rollback.
Database should not contain partial row.
```

Test-only service:

```java
package com.miniurl.shortener.url.testsupport;

import com.miniurl.shortener.url.entity.ShortUrl;
import com.miniurl.shortener.url.entity.ShortUrlStatus;
import com.miniurl.shortener.url.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TransactionTestService {

    private final ShortUrlRepository repository;

    public TransactionTestService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void saveThenFail() {
        ShortUrl entity = new ShortUrl();
        entity.setShortCode("rollback1");
        entity.setLongUrl("https://rollback.com");
        entity.setStatus(ShortUrlStatus.ACTIVE);
        entity.setCreatedAt(Instant.now());

        repository.save(entity);

        throw new RuntimeException("forced failure");
    }
}
```

Test:

```java
package com.miniurl.shortener.url;

import com.miniurl.shortener.url.testsupport.TransactionTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

class TransactionRollbackIntegrationTest extends UrlIntegrationTest {

    @Autowired
    private TransactionTestService transactionTestService;

    @Test
    void saveThenFail_shouldRollbackDatabaseInsert() {
        assertThatThrownBy(() -> transactionTestService.saveThenFail())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("forced failure");

        boolean exists = shortUrlRepository.existsByShortCode("rollback1");

        assertThat(exists).isFalse();
    }
}
```

ASCII:

```text
@Transactional starts
      |
      v
INSERT row
      |
      v
RuntimeException
      |
      v
Transaction marked rollback-only
      |
      v
ROLLBACK
      |
      v
DB row does not exist
```

This proves:

```text
@Transactional proxy works.
Repository participates in transaction.
Rollback happens on unchecked exception.
```

---

## 16. Database State Verification

Do not only assert HTTP response.

For create flows, verify database state.

Bad test:

```text
Assert 201 only.
```

Better test:

```text
Assert 201.
Assert response body.
Assert DB row exists.
Assert DB row fields are correct.
```

Example:

```java
var saved = shortUrlRepository.findByShortCode("article1")
        .orElseThrow();

assertThat(saved.getLongUrl()).isEqualTo("https://example.com/article");
assertThat(saved.getStatus()).isEqualTo(ShortUrlStatus.ACTIVE);
assertThat(saved.getCreatedAt()).isNotNull();
```

ASCII:

```text
HTTP assertion:
    Did API respond correctly?

DB assertion:
    Did system state change correctly?
```

Both matter.

For redirect:

```text
Maybe response returns 302 from mock data.
But DB lookup is broken.
```

DB verification catches real state issues.

---

## 17. Test Data Strategy

Good integration tests need simple, clear data.

Use builders or helper methods.

Example helper:

```java
protected ShortUrl saveActiveUrl(String code, String longUrl) {
    ShortUrl entity = new ShortUrl();
    entity.setShortCode(code);
    entity.setLongUrl(longUrl);
    entity.setStatus(ShortUrlStatus.ACTIVE);
    entity.setCreatedAt(Instant.now());
    return shortUrlRepository.save(entity);
}
```

Then test becomes:

```java
saveActiveUrl("abc123", "https://example.com");

mockMvc.perform(get("/abc123"))
        .andExpect(status().isFound());
```

Why helpers matter:

```text
Less noise.
More readable tests.
Less copy-paste.
```

But avoid hiding too much.

Bad helper:

```text
createComplexProductionScenarioWithEverything()
```

Good helper:

```text
saveActiveUrl()
saveExpiredUrl()
saveBlockedUrl()
```

Test data forms:

```text
active URL
expired URL
blocked URL
duplicate alias
invalid URL input
future expiresAt
past expiresAt
```

---

## 18. Cleanup Strategy

Integration tests share a real database container.

If one test leaves data, another test may fail.

Options:

```text
1. repository.deleteAll() in @BeforeEach
2. @Sql scripts before each test
3. Transaction rollback after each test
4. Fresh schema/container per test class
```

Simple approach:

```java
@BeforeEach
void cleanDatabase() {
    shortUrlRepository.deleteAll();
}
```

ASCII:

```text
Before each test:
    DELETE FROM short_urls

Then:
    test inserts only what it needs
```

Caution:

```text
deleteAll() order matters when tables have foreign keys.
```

For multiple tables:

```text
delete child tables first
delete parent tables last
```

Example future order:

```java
clickEventRepository.deleteAll();
shortUrlRepository.deleteAll();
userRepository.deleteAll();
```

Do not rely on test ordering.

Each test must be independent.

---

## 19. Full Execution Dry Runs

### Dry Run 1: Create Short URL

Request:

```json
{
  "longUrl": "https://example.com/a",
  "customAlias": "abc123"
}
```

Execution:

```text
1. JUnit method starts.
2. PostgreSQL container is already running.
3. MockMvc sends POST /api/v1/urls.
4. DispatcherServlet routes to UrlController.
5. Jackson deserializes JSON into CreateShortUrlRequest.
6. @Valid passes.
7. UrlService validates URL and alias.
8. Repository saves ShortUrl entity.
9. Hibernate executes INSERT into PostgreSQL.
10. Transaction commits.
11. Controller returns 201 response.
12. Test asserts JSON response.
13. Test queries repository to verify DB row.
```

ASCII:

```text
Test -> MockMvc -> Controller -> Service -> Repository -> PostgreSQL
  ^                                                           |
  |                                                           v
  +---------------- assert response + assert DB row ---------+
```

---

### Dry Run 2: Duplicate Alias

Existing DB:

```text
short_code = abc123
```

Request:

```json
{
  "longUrl": "https://second.com",
  "customAlias": "abc123"
}
```

Execution:

```text
1. Test inserts existing row.
2. MockMvc sends create request.
3. Service sees alias or DB unique constraint detects conflict.
4. CustomAliasAlreadyExistsException is thrown.
5. GlobalExceptionHandler converts it.
6. Response is 409.
7. Test asserts code ALIAS_ALREADY_EXISTS.
8. Test verifies only one row exists for abc123.
```

---

### Dry Run 3: Expired Redirect

Existing DB:

```text
short_code = old1
expires_at = yesterday
status = ACTIVE
```

Request:

```http
GET /old1
```

Execution:

```text
1. Controller receives old1.
2. Service loads row.
3. Row exists and status ACTIVE.
4. expiresAt is before now.
5. Service throws ShortCodeExpiredException.
6. Handler returns 410.
7. Test asserts SHORT_CODE_EXPIRED.
```

---

### Dry Run 4: Rollback

Execution:

```text
1. Test calls transactionTestService.saveThenFail().
2. Spring transaction interceptor opens transaction.
3. Repository save happens.
4. RuntimeException thrown.
5. Transaction interceptor rolls back.
6. Test queries DB.
7. Row is absent.
```

Memory:

```text
If rollback test passes with PostgreSQL, transaction boundary is real.
```

---

## 20. Internal Execution Walkthrough

Spring Boot integration test startup:

```text
1. JUnit discovers test class.
2. Testcontainers starts PostgreSQL container.
3. DynamicPropertySource registers datasource properties.
4. Spring Boot builds ApplicationContext.
5. DataSource connects to container.
6. JPA EntityManagerFactory starts.
7. Flyway runs migrations or Hibernate creates schema.
8. MockMvc is configured.
9. Test method runs.
```

ASCII:

```text
JUnit
 |
 v
BaseIntegrationTest
 |
 v
PostgreSQLContainer.start()
 |
 v
DynamicPropertySource
 |
 v
Spring ApplicationContext
 |
 v
DataSource -> PostgreSQL container
 |
 v
MockMvc
 |
 v
Test methods
```

Important nuance:

```text
Container must be ready before Spring creates DataSource.
```

If datasource is created before container properties are registered:

```text
Connection refused
wrong URL
context startup failure
```

---

## 21. Production Failure Stories

### Failure Story 1: H2 Tests Passed, PostgreSQL Failed

The team used H2 for integration tests.

Production deployment failed because PostgreSQL rejected a migration.

Root cause:

```text
Test database was not the same as production database.
```

Fix:

```text
Use PostgreSQL Testcontainers for integration tests.
```

Lesson:

```text
The closer your test dependency is to production, the fewer deployment surprises.
```

---

### Failure Story 2: Missing Unique Constraint

Service checked alias availability.

Unit tests passed.

Under concurrency, two pods created the same alias.

Root cause:

```text
No database UNIQUE(short_code) constraint.
```

Fix:

```text
Add DB unique constraint.
Add Testcontainers integration test for duplicate alias.
```

Lesson:

```text
Application checks are helpful; database constraints are authority.
```

---

### Failure Story 3: Validation Not Triggered

Controller forgot `@Valid`.

Unit test called service directly and passed.

Production accepted blank longUrl.

Root cause:

```text
No controller integration test.
```

Fix:

```text
MockMvc integration test for invalid request.
```

Lesson:

```text
Validation is a framework behavior. Test it through HTTP.
```

---

### Failure Story 4: Transaction Did Not Roll Back

A helper method had `@Transactional`, but it was called through self-invocation.

Rollback did not happen.

Root cause:

```text
Spring proxy was bypassed.
```

Fix:

```text
Put transaction boundary on public service method called through Spring bean.
Add rollback integration test.
```

Lesson:

```text
Transaction behavior must be tested through Spring-managed beans.
```

---

### Failure Story 5: CI Could Not Run Docker

Tests passed locally but failed in CI.

Root cause:

```text
CI runner had no Docker daemon or insufficient permissions.
```

Fix:

```text
Enable Docker service in CI.
Use proper runner image.
Mark integration tests separately if needed.
```

Lesson:

```text
Testcontainers requires Docker-compatible runtime.
```

---

## 22. Debugging Mindset

When Testcontainers test fails, ask:

```text
Did the container start?
Can Spring connect to the dynamic JDBC URL?
Did migrations run?
Is Docker running locally?
Is the image pull blocked?
Is the schema correct?
Did test cleanup run?
Is another test polluting data?
Is transaction rollback hiding DB state?
Is MockMvc hitting the expected path?
Is the error from app logic or test setup?
```

Debug map:

```text
ContainerLaunchException:
    Docker not running, image pull issue, port issue

Connection refused:
    datasource URL wrong or container not ready

relation does not exist:
    migration did not run or ddl-auto disabled

constraint violation:
    test data invalid or production invariant caught

404 in MockMvc:
    wrong endpoint path or controller not loaded

400 in create test:
    request JSON invalid or validation rule failed

500 response:
    inspect application logs and exception mapping
```

Useful logs:

```properties
logging.level.org.testcontainers=INFO
logging.level.org.springframework.test=INFO
logging.level.org.hibernate.SQL=DEBUG
```

Useful local commands:

```bash
docker ps
docker logs <container_id>
docker images
```

Debugging rule:

```text
First separate infrastructure failure from application failure.
```

---

## 23. Common Mistakes

### Mistake 1: Hardcoding PostgreSQL Port

Wrong:

```text
jdbc:postgresql://localhost:5432/miniurl_test
```

Correct:

```java
postgres.getJdbcUrl()
```

---

### Mistake 2: Using H2 For PostgreSQL-Specific App

Wrong:

```text
Production PostgreSQL, tests H2 only.
```

Correct:

```text
Repository/integration tests use PostgreSQL Testcontainers.
```

---

### Mistake 3: No Database Cleanup

Wrong:

```text
Tests depend on previous test state.
```

Correct:

```text
Clean DB before each test or isolate test data.
```

---

### Mistake 4: Testing Only Status Code

Wrong:

```java
.andExpect(status().isCreated());
```

Correct:

```java
.andExpect(status().isCreated())
.andExpect(jsonPath("$.shortCode").value("abc123"));
```

Also verify database state.

---

### Mistake 5: Mocking Repository In Integration Test

Wrong:

```text
@SpringBootTest but repository is mocked.
```

Correct:

```text
Use real repository and real container DB.
```

---

### Mistake 6: Putting Too Many Assertions In One Test

Wrong:

```text
One test checks create, redirect, expiry, duplicate, health, metrics.
```

Correct:

```text
One behavior per test.
```

---

### Mistake 7: Tests Depend On Current Time Too Loosely

Wrong:

```text
expiresAt = Instant.now()
```

This can be flaky.

Correct:

```text
expired = Instant.now().minusSeconds(3600)
future = Instant.now().plusSeconds(3600)
```

---

### Mistake 8: Ignoring CI Runtime

Wrong:

```text
Assume Docker is available everywhere.
```

Correct:

```text
Configure CI with Docker support.
```

---

## 24. CI/CD Considerations

Testcontainers in CI needs Docker.

Common GitHub Actions shape:

```yaml
name: build

on:
  push:
  pull_request:

jobs:
  test:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21

      - name: Run tests
        run: ./mvnw test
```

On GitHub-hosted Linux runners, Docker is usually available.

For corporate CI:

```text
Check whether Docker daemon is accessible.
Check whether privileged mode is allowed.
Check whether image pulls are blocked.
Check whether registry mirror is needed.
```

Splitting tests:

```text
Unit tests:
    fast, every commit

Integration tests:
    slower, every PR or main branch
```

Maven naming strategy:

```text
*Test.java      -> unit/slice tests
*IT.java        -> integration tests
```

With Maven Failsafe plugin:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Simple early-stage project:

```text
Running all tests with mvn test is fine.
```

Production-stage project:

```text
Separate fast and slow test stages.
```

---

## 25. Interview-Ready Explanation

If interviewer asks:

```text
How do you test Spring Boot integration with PostgreSQL?
```

Strong answer:

```text
I use Testcontainers to start a real PostgreSQL container during integration tests.
The container provides a dynamic JDBC URL, username, and password, which I inject into
Spring Boot using @DynamicPropertySource before the application context starts.
Then I run near full-stack tests using @SpringBootTest and MockMvc. For a URL shortener,
I test flows like create short URL, redirect, duplicate custom alias, validation errors,
expired links, and blocked links. I assert both the HTTP contract and the database state.
This catches problems that mocks or H2 can hide, such as PostgreSQL-specific SQL behavior,
unique constraints, JPA mappings, migrations, and transaction rollback behavior.
```

Senior version:

```text
Unit tests verify business logic quickly, but Testcontainers integration tests verify that
the real application wiring works with the same class of infrastructure used in production.
For database-heavy systems, that is essential because correctness depends on schema,
constraints, transaction boundaries, and SQL dialect behavior.
```

Why this is strong:

```text
1. Mentions real PostgreSQL.
2. Explains dynamic property injection.
3. Separates unit and integration test purpose.
4. Mentions HTTP and DB assertions.
5. Mentions constraints and transactions.
6. Shows production realism.
7. Shows CI awareness.
```

---

## 26. Senior Engineer Checklist

Before calling your MiniURLShortener integration tests production-shaped, confirm:

```text
[ ] PostgreSQL Testcontainer is used
[ ] Spring datasource uses dynamic container JDBC URL
[ ] Tests do not hardcode port 5432
[ ] MockMvc tests hit real controller
[ ] Service and repository are real beans
[ ] Database schema is created by Flyway or Hibernate
[ ] Create API test verifies response and DB row
[ ] Redirect API test verifies 302 and Location header
[ ] Invalid request test verifies standard error JSON
[ ] Duplicate alias test verifies 409
[ ] Expired URL test verifies 410
[ ] Blocked URL test verifies 403
[ ] Transaction rollback test exists for critical write flows
[ ] Test data cleanup happens before each test
[ ] Tests are independent of execution order
[ ] CI supports Docker
[ ] Slow integration tests can be separated if needed
```

---

## 27. One-Page Cheat Sheet

```text
Core mental model:
Testcontainers = real dependency + disposable lifetime.

Why:
Mocks do not test wiring.
H2 is not PostgreSQL.
Real DB catches schema, SQL, transaction, and constraint bugs.

Main annotations:
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@DynamicPropertySource

Container:
PostgreSQLContainer<>("postgres:16-alpine")

Dynamic properties:
spring.datasource.url
spring.datasource.username
spring.datasource.password

Integration test should verify:
HTTP status
response JSON
headers
database state
error code
transaction behavior

MiniURLShortener high-value tests:
create URL -> 201 + DB row
redirect active -> 302 Location
blank URL -> 400 VALIDATION_FAILED
duplicate alias -> 409 ALIAS_ALREADY_EXISTS
missing code -> 404 SHORT_CODE_NOT_FOUND
expired code -> 410 SHORT_CODE_EXPIRED
blocked code -> 403 SHORT_CODE_BLOCKED
rollback failure -> no partial DB row

Common failures:
Docker not running
hardcoded port
migration missing
dirty DB between tests
wrong endpoint path
validation not triggered
constraint mismatch
```

---

## 28. One Picture To Remember

```text
             INTEGRATION TESTCONTAINERS MENTAL MODEL

                    "Test the real wiring"

+-------------------------------------------------------------+
|                        JUnit Test                           |
|                                                             |
|  mockMvc.perform(POST /api/v1/urls)                         |
+-----------------------------+-------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                    Spring Boot Test Context                 |
|                                                             |
|  Controller -> Service -> Repository -> Transaction Manager |
+-----------------------------+-------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                 Testcontainers PostgreSQL                   |
|                                                             |
|  real schema                                                |
|  real constraints                                           |
|  real SQL dialect                                           |
|  real transactions                                          |
+-----------------------------+-------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                         Assertions                          |
|                                                             |
|  HTTP response correct?                                     |
|  JSON contract correct?                                     |
|  DB row correct?                                            |
|  rollback correct?                                          |
+-------------------------------------------------------------+


FINAL MEMORY:

Unit tests prove logic.
Integration tests prove wiring.
Testcontainers makes the dependency real.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Testcontainers starts real dependencies during tests.
2. For PostgreSQL applications, this is stronger than H2 because SQL dialect and constraints are real.
3. @DynamicPropertySource connects Spring Boot to the container's runtime JDBC URL.
4. Good integration tests assert both HTTP response and database state.
5. The highest-value MiniURLShortener integration tests cover create, redirect, validation, duplicate alias, expiry, blocked links, and rollback.
```

After this chapter, Phase 2 Production Coding has a real integration testing foundation:

```text
010 Clean Architecture
011 Controller Service Repository
012 JPA vs JDBC
013 Transaction Boundaries
014 Global Exception Handler
015 Config Properties
016 Actuator Health Checks
017 API Versioning
018 Testing Strategy
019 Integration Testcontainers
```
