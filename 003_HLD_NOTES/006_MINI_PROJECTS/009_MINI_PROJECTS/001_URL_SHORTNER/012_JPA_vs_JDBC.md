# 012_JPA_vs_JDBC.md
# MiniURLShortener — JPA vs JDBC

> Core mental model: **JPA is an object-state manager; JDBC is direct SQL execution. Use JPA when domain objects and transaction state matter. Use JDBC when exact SQL control, bulk operations, or very high-performance read/write paths matter.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. JDBC Mental Model](#4-jdbc-mental-model)
- [5. JPA Mental Model](#5-jpa-mental-model)
- [6. JPA vs JDBC In One Picture](#6-jpa-vs-jdbc-in-one-picture)
- [7. Where They Fit In MiniURLShortener](#7-where-they-fit-in-miniurlshortener)
- [8. Controller-Service-Repository Placement](#8-controller-service-repository-placement)
- [9. JDBC Request Flow](#9-jdbc-request-flow)
- [10. JPA Request Flow](#10-jpa-request-flow)
- [11. Entity vs Row Mental Model](#11-entity-vs-row-mental-model)
- [12. Repository Design With JPA](#12-repository-design-with-jpa)
- [13. Repository Design With JDBC](#13-repository-design-with-jdbc)
- [14. Create Short URL With JPA](#14-create-short-url-with-jpa)
- [15. Create Short URL With JDBC](#15-create-short-url-with-jdbc)
- [16. Redirect Lookup With JPA](#16-redirect-lookup-with-jpa)
- [17. Redirect Lookup With JDBC](#17-redirect-lookup-with-jdbc)
- [18. Transaction Behavior](#18-transaction-behavior)
- [19. Dirty Checking vs Explicit SQL](#19-dirty-checking-vs-explicit-sql)
- [20. Performance Tradeoffs](#20-performance-tradeoffs)
- [21. When JPA Is Better](#21-when-jpa-is-better)
- [22. When JDBC Is Better](#22-when-jdbc-is-better)
- [23. Hybrid Production Strategy](#23-hybrid-production-strategy)
- [24. Step-by-Step Dry Runs](#24-step-by-step-dry-runs)
- [25. Internal Execution Walkthrough](#25-internal-execution-walkthrough)
- [26. Production Failure Stories](#26-production-failure-stories)
- [27. Debugging Mindset](#27-debugging-mindset)
- [28. Common Mistakes](#28-common-mistakes)
- [29. Testing Strategy](#29-testing-strategy)
- [30. Interview-Ready Explanation](#30-interview-ready-explanation)
- [31. Senior Engineer Checklist](#31-senior-engineer-checklist)
- [32. One-Page Cheat Sheet](#32-one-page-cheat-sheet)
- [33. One Picture To Remember](#33-one-picture-to-remember)

---

## 1. Why This Exists

In MiniURLShortener, the repository layer talks to PostgreSQL.

At this point, your application has clear layers:

```text
Client
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
PostgreSQL
```

The important question is:

```text
How should repository talk to the database?
```

There are two common choices:

```text
1. JPA / Hibernate
2. JDBC / JdbcTemplate
```

Both can build the same feature.

Example feature:

```text
POST /api/v1/urls
```

It must:

```text
1. Validate longUrl.
2. Generate shortCode.
3. Save row into short_urls.
4. Return created response.
```

With JPA, you usually think:

```text
Create entity object.
Save entity.
Let Hibernate generate SQL.
```

With JDBC, you usually think:

```text
Write SQL string.
Bind parameters.
Execute SQL.
Map result manually.
```

Both are valid.

But they optimize for different thinking styles.

JPA gives you:

```text
object mapping
entity lifecycle
dirty checking
repository abstraction
less repetitive CRUD code
```

JDBC gives you:

```text
exact SQL control
predictable queries
lower abstraction cost
better bulk operation control
clear performance behavior
```

Production mental model:

```text
JPA is convenient when your write/read path matches entity lifecycle.
JDBC is powerful when your path is SQL-first, performance-first, or query-first.
```

---

## 2. The One Core Mental Model

The core difference:

```text
JPA manages object state.
JDBC executes SQL commands.
```

ASCII:

```text
JPA:

Java Object
    |
    v
Persistence Context
    |
    v
Hibernate decides SQL
    |
    v
Database
```

```text
JDBC:

SQL String + Parameters
    |
    v
Database
    |
    v
Manual Row Mapping
    |
    v
Java Object / DTO
```

One-line memory:

```text
JPA asks: what changed in my objects?
JDBC asks: what SQL should I run?
```

For MiniURLShortener:

```text
JPA mindset:
    ShortUrl entity becomes managed.
    Hibernate inserts/updates when transaction flushes.

JDBC mindset:
    INSERT INTO short_urls ...
    SELECT long_url FROM short_urls WHERE short_code = ?
```

This is the heart of the decision.

---

## 3. Problem Statement

Design the persistence strategy for MiniURLShortener.

We need to support:

```text
1. Create short URL.
2. Lookup by shortCode for redirect.
3. Check custom alias uniqueness.
4. Update click count later.
5. Filter by user later.
6. Handle duplicate shortCode conflicts.
7. Keep code clean and testable.
8. Avoid hidden performance problems.
```

We must decide:

```text
Should repository use JPA, JDBC, or both?
```

Out of scope for this chapter:

```text
1. Full Redis cache implementation.
2. Sharding implementation.
3. Read replica routing.
4. Cassandra migration.
5. Advanced Hibernate tuning.
```

Goal:

```text
Understand the tradeoff deeply enough to choose correctly in interviews and production.
```

---

## 4. JDBC Mental Model

JDBC is the low-level Java database API.

Spring usually makes it nicer with `JdbcTemplate` or `NamedParameterJdbcTemplate`.

The mindset:

```text
I know the SQL.
I know the parameters.
I know how to map rows.
```

ASCII:

```text
Service
  |
  v
Jdbc Repository
  |
  | SQL: SELECT * FROM short_urls WHERE short_code = ?
  | param: abc123
  v
PostgreSQL
  |
  | row
  v
RowMapper
  |
  v
ShortUrl object / DTO
```

JDBC code is explicit.

Example:

```java
String sql = """
        SELECT id, short_code, long_url, status, expires_at
        FROM short_urls
        WHERE short_code = ?
        """;
```

You see the exact query.

That is powerful because performance debugging starts with SQL.

JDBC is close to the database.

It does not manage entity lifecycle.
It does not dirty-check.
It does not automatically persist object changes.

If you want an update, you write an update.

```java
jdbcTemplate.update(
        "UPDATE short_urls SET click_count = click_count + 1 WHERE short_code = ?",
        shortCode
);
```

JDBC is like driving manual gear.

```text
More control.
More responsibility.
Fewer hidden decisions.
```

---

## 5. JPA Mental Model

JPA is a persistence specification.
Hibernate is the common implementation.
Spring Data JPA adds repository convenience.

The mindset:

```text
I work with entities.
The persistence context tracks them.
Hibernate generates SQL.
```

ASCII:

```text
Service
  |
  v
Jpa Repository
  |
  v
EntityManager / Persistence Context
  |
  | managed entity state
  v
Hibernate
  |
  | generated SQL
  v
PostgreSQL
```

Example:

```java
ShortUrl shortUrl = new ShortUrl();
shortUrl.setShortCode("abc123");
shortUrl.setLongUrl("https://example.com");

repository.save(shortUrl);
```

You did not write SQL.

Hibernate may run:

```sql
INSERT INTO short_urls (short_code, long_url, status, created_at)
VALUES (?, ?, ?, ?);
```

JPA is not just SQL generation.

It also has:

```text
persistence context
entity states
managed entities
dirty checking
flush
lazy loading
relationships
first-level cache
```

For MiniURLShortener, this matters when an entity changes inside a transaction.

Example:

```java
ShortUrl shortUrl = repository.findByShortCode(shortCode)
        .orElseThrow(...);

shortUrl.incrementClickCount();
```

If the entity is managed and transaction commits, Hibernate can detect the change and run update SQL.

JPA is like driving automatic gear.

```text
Less repetitive code.
More hidden behavior.
Requires understanding internals.
```

---

## 6. JPA vs JDBC In One Picture

```text
                         DATABASE ACCESS CHOICE

                 +-----------------------------+
                 | Repository Layer            |
                 +-----------------------------+
                       /                 \
                      /                   \
                     v                     v
        +---------------------+   +----------------------+
        | JPA / Hibernate     |   | JDBC / JdbcTemplate  |
        +---------------------+   +----------------------+
        | Object-first        |   | SQL-first            |
        | Entity lifecycle    |   | Direct statements    |
        | Dirty checking      |   | Explicit updates     |
        | Less CRUD code      |   | More SQL control     |
        | Hidden SQL possible |   | Query visible        |
        +---------------------+   +----------------------+
                     \                   /
                      \                 /
                       v               v
                    PostgreSQL short_urls table
```

The decision is not religious.

Strong engineers ask:

```text
What does this path need?
```

For example:

```text
Admin CRUD screen        -> JPA is good.
Simple create endpoint   -> JPA is good.
Complex reporting query  -> JDBC is good.
Hot redirect lookup      -> JDBC can be better.
Bulk click aggregation   -> JDBC is often better.
```

---

## 7. Where They Fit In MiniURLShortener

MiniURLShortener has different access patterns.

### Create Short URL

```text
Write one row.
Apply domain rules.
Handle duplicate short_code.
Return response.
```

JPA is fine.

Why?

```text
Entity maps cleanly to short_urls row.
Repository save is simple.
Transaction handling is natural.
```

### Redirect Lookup

```text
GET /abc123
Find longUrl fast.
Return redirect.
Very high read volume later.
```

JPA works in early phase.

But for very high RPS, JDBC projection can be better.

Why?

```text
Redirect only needs longUrl, status, expiresAt.
It does not need full entity lifecycle.
It should avoid loading unnecessary columns.
It should avoid accidental lazy loading later.
```

### Click Count Update

Naive JPA:

```text
load entity
increment field
flush update
```

Better high-scale path:

```text
Kafka event / async aggregation
or direct SQL atomic increment
```

JDBC is often clearer:

```sql
UPDATE short_urls
SET click_count = click_count + 1
WHERE short_code = ?;
```

### Analytics Queries

JDBC is usually better.

Because analytics is query-first.

```text
top links by day
clicks by country
owner dashboard
bulk export
```

---

## 8. Controller-Service-Repository Placement

Do not put JPA or JDBC decision in controller.

Correct layering:

```text
Controller knows HTTP.
Service knows business use case.
Repository knows database access.
```

ASCII:

```text
+------------+
| Controller |
| HTTP DTOs  |
+------------+
      |
      v
+------------+
| Service    |
| Use cases  |
+------------+
      |
      v
+----------------------+
| Repository Interface |
+----------------------+
      |
      +-----------------------+
      |                       |
      v                       v
+-------------+        +---------------+
| JPA Impl    |        | JDBC Impl     |
+-------------+        +---------------+
      |                       |
      v                       v
               PostgreSQL
```

Best practice:

```text
Service depends on repository abstraction.
Repository implementation can use JPA or JDBC.
```

Example service should not care:

```java
ShortUrl saved = shortUrlRepository.save(shortUrl);
```

Whether this uses JPA or JDBC is a repository detail.

This keeps your service clean.

---

## 9. JDBC Request Flow

Create URL with JDBC:

```text
POST /api/v1/urls
  |
  v
Controller maps JSON to DTO
  |
  v
Service validates and creates domain object
  |
  v
JDBC Repository builds INSERT SQL
  |
  v
JdbcTemplate binds params
  |
  v
PostgreSQL executes INSERT
  |
  v
Repository maps result
  |
  v
Service builds response
```

ASCII:

```text
Java value object
      |
      v
SQL + params
      |
      v
DB row
      |
      v
mapped Java object
```

JDBC is explicit both ways.

You explicitly move data from object to SQL.
You explicitly move data from row to object.

That is more code but easier to reason about under pressure.

---

## 10. JPA Request Flow

Create URL with JPA:

```text
POST /api/v1/urls
  |
  v
Controller maps JSON to DTO
  |
  v
Service validates and creates entity
  |
  v
JpaRepository.save(entity)
  |
  v
Entity becomes managed/persisted
  |
  v
Hibernate generates INSERT
  |
  v
Transaction flush/commit sends SQL
```

ASCII:

```text
Entity object
    |
    v
Persistence Context
    |
    v
Hibernate SQL generation
    |
    v
Database row
```

The key hidden object:

```text
Persistence Context
```

It is like an in-memory tracking room inside a transaction.

It knows:

```text
which entities are new
which entities are loaded
which fields changed
what SQL should be sent on flush
```

This is powerful.

But if you do not understand it, JPA becomes magical and dangerous.

---

## 11. Entity vs Row Mental Model

A database row is stored data.

A JPA entity is a Java object representing a database row.

They are related but not identical.

ASCII:

```text
PostgreSQL row:

+----+------------+---------------------+--------+
| id | short_code | long_url            | status |
+----+------------+---------------------+--------+
| 1  | abc123     | https://example.com | ACTIVE |
+----+------------+---------------------+--------+

JPA entity:

ShortUrl {
    id = 1
    shortCode = "abc123"
    longUrl = "https://example.com"
    status = ACTIVE
}
```

JPA manages entity state.

Common states:

```text
Transient: new Java object, not known by JPA.
Managed: tracked by persistence context.
Detached: was managed before, now outside context.
Removed: scheduled for delete.
```

ASCII:

```text
new ShortUrl()
   |
   | repository.save()
   v
Managed Entity
   |
   | transaction commit
   v
Database Row
```

JDBC does not care about entity states.

JDBC sees:

```text
SQL in, rows out.
```

---

## 12. Repository Design With JPA

Entity:

```java
package com.miniurl.shortener.url.domain;

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

    @Column(name = "short_code", nullable = false, length = 32, unique = true)
    private String shortCode;

    @Column(name = "long_url", nullable = false, length = 2048)
    private String longUrl;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ShortUrlStatus status;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "click_count", nullable = false)
    private long clickCount;

    protected ShortUrl() {
        // JPA requires protected/default constructor.
    }

    public ShortUrl(String shortCode, String longUrl, Instant expiresAt) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.expiresAt = expiresAt;
        this.status = ShortUrlStatus.ACTIVE;
        this.createdAt = Instant.now();
        this.clickCount = 0;
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

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getClickCount() {
        return clickCount;
    }

    public void incrementClickCount() {
        this.clickCount++;
    }
}
```

Enum:

```java
package com.miniurl.shortener.url.domain;

public enum ShortUrlStatus {
    ACTIVE,
    BLOCKED,
    DELETED
}
```

Spring Data JPA repository:

```java
package com.miniurl.shortener.url.repository;

import com.miniurl.shortener.url.domain.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlJpaRepository extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);
}
```

This gives you CRUD with little code.

But remember:

```text
Less code does not mean no SQL.
SQL still happens.
Hibernate writes it for you.
```

---

## 13. Repository Design With JDBC

JDBC repository needs SQL and mapping.

```java
package com.miniurl.shortener.url.repository;

import com.miniurl.shortener.url.domain.ShortUrl;
import com.miniurl.shortener.url.domain.ShortUrlStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Repository
public class ShortUrlJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public ShortUrlJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ShortUrlRecord> rowMapper = (rs, rowNum) -> new ShortUrlRecord(
            rs.getLong("id"),
            rs.getString("short_code"),
            rs.getString("long_url"),
            ShortUrlStatus.valueOf(rs.getString("status")),
            toInstant(rs.getTimestamp("expires_at")),
            toInstant(rs.getTimestamp("created_at")),
            rs.getLong("click_count")
    );

    public Optional<ShortUrlRecord> findByShortCode(String shortCode) {
        String sql = """
                SELECT id, short_code, long_url, status, expires_at, created_at, click_count
                FROM short_urls
                WHERE short_code = ?
                """;

        return jdbcTemplate.query(sql, rowMapper, shortCode)
                .stream()
                .findFirst();
    }

    public boolean existsByShortCode(String shortCode) {
        String sql = "SELECT COUNT(1) FROM short_urls WHERE short_code = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, shortCode);
        return count != null && count > 0;
    }

    private static Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }
}
```

Record projection:

```java
package com.miniurl.shortener.url.repository;

import com.miniurl.shortener.url.domain.ShortUrlStatus;
import java.time.Instant;

public record ShortUrlRecord(
        Long id,
        String shortCode,
        String longUrl,
        ShortUrlStatus status,
        Instant expiresAt,
        Instant createdAt,
        long clickCount
) {
}
```

JDBC requires more code.

But every query is visible.

That is a major advantage for high-scale systems.

---

## 14. Create Short URL With JPA

Service using JPA repository:

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.url.domain.ShortUrl;
import com.miniurl.shortener.url.dto.CreateShortUrlRequest;
import com.miniurl.shortener.url.dto.CreateShortUrlResponse;
import com.miniurl.shortener.url.repository.ShortUrlJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateShortUrlJpaService {

    private final ShortUrlJpaRepository repository;
    private final UrlValidatorService validatorService;
    private final ShortCodeGenerator shortCodeGenerator;

    public CreateShortUrlJpaService(
            ShortUrlJpaRepository repository,
            UrlValidatorService validatorService,
            ShortCodeGenerator shortCodeGenerator
    ) {
        this.repository = repository;
        this.validatorService = validatorService;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    @Transactional
    public CreateShortUrlResponse create(CreateShortUrlRequest request) {
        validatorService.validateLongUrl(request.getLongUrl());

        String shortCode = request.getCustomAlias();

        if (shortCode == null || shortCode.isBlank()) {
            shortCode = shortCodeGenerator.generate();
        } else {
            validatorService.validateCustomAlias(shortCode);
        }

        ShortUrl entity = new ShortUrl(
                shortCode,
                request.getLongUrl().trim(),
                request.getExpiresAt()
        );

        try {
            ShortUrl saved = repository.save(entity);

            return new CreateShortUrlResponse(
                    saved.getShortCode(),
                    saved.getLongUrl()
            );
        } catch (DataIntegrityViolationException ex) {
            if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {
                throw new CustomAliasAlreadyExistsException(request.getCustomAlias());
            }
            throw ex;
        }
    }
}
```

JPA benefits here:

```text
1. Simple save.
2. Entity object carries domain state.
3. Transaction boundary is natural.
4. Less manual SQL.
```

But know what happens internally:

```text
repository.save(entity)
  -> EntityManager persist/merge
  -> Hibernate prepares INSERT
  -> SQL executes on flush/commit
```

---

## 15. Create Short URL With JDBC

JDBC version:

```java
package com.miniurl.shortener.url.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;

@Repository
public class ShortUrlJdbcWriterRepository {

    private final JdbcTemplate jdbcTemplate;

    public ShortUrlJdbcWriterRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(String shortCode, String longUrl, Instant expiresAt) {
        String sql = """
                INSERT INTO short_urls
                    (short_code, long_url, status, expires_at, created_at, click_count)
                VALUES
                    (?, ?, 'ACTIVE', ?, ?, 0)
                """;

        jdbcTemplate.update(
                sql,
                shortCode,
                longUrl,
                expiresAt == null ? null : Timestamp.from(expiresAt),
                Timestamp.from(Instant.now())
        );
    }
}
```

Service:

```java
@Transactional
public CreateShortUrlResponse create(CreateShortUrlRequest request) {
    validatorService.validateLongUrl(request.getLongUrl());

    String shortCode = request.getCustomAlias();

    if (shortCode == null || shortCode.isBlank()) {
        shortCode = shortCodeGenerator.generate();
    } else {
        validatorService.validateCustomAlias(shortCode);
    }

    try {
        jdbcWriterRepository.insert(
                shortCode,
                request.getLongUrl().trim(),
                request.getExpiresAt()
        );
    } catch (DataIntegrityViolationException ex) {
        if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {
            throw new CustomAliasAlreadyExistsException(request.getCustomAlias());
        }
        throw ex;
    }

    return new CreateShortUrlResponse(shortCode, request.getLongUrl().trim());
}
```

JDBC benefits here:

```text
1. Exact INSERT is visible.
2. No entity lifecycle overhead.
3. Easy to tune SQL.
4. Easy to add database-specific clauses later.
```

Example later PostgreSQL improvement:

```sql
INSERT INTO short_urls (...)
VALUES (...)
ON CONFLICT (short_code) DO NOTHING;
```

This is SQL-first thinking.

---

## 16. Redirect Lookup With JPA

JPA repository lookup:

```java
Optional<ShortUrl> findByShortCode(String shortCode);
```

Service:

```java
@Transactional(readOnly = true)
public RedirectResult resolve(String shortCode) {
    ShortUrl shortUrl = repository.findByShortCode(shortCode)
            .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));

    if (shortUrl.getStatus() == ShortUrlStatus.BLOCKED) {
        throw new ShortCodeBlockedException(shortCode);
    }

    if (shortUrl.getStatus() == ShortUrlStatus.DELETED) {
        throw new ShortCodeNotFoundException(shortCode);
    }

    if (shortUrl.getExpiresAt() != null && shortUrl.getExpiresAt().isBefore(Instant.now())) {
        throw new ShortCodeExpiredException(shortCode);
    }

    return new RedirectResult(shortUrl.getLongUrl());
}
```

This is clean.

But for high-RPS redirect path, think carefully.

Redirect only needs:

```text
long_url
status
expires_at
```

If entity has many fields later:

```text
owner
campaign
analytics config
security scan result
created metadata
relations
```

Then loading full entity may be unnecessary.

JPA projection can improve this:

```java
public interface RedirectLookupView {
    String getLongUrl();
    ShortUrlStatus getStatus();
    Instant getExpiresAt();
}
```

Repository:

```java
Optional<RedirectLookupView> findProjectedByShortCode(String shortCode);
```

But JDBC projection is often even clearer.

---

## 17. Redirect Lookup With JDBC

JDBC projection:

```java
public record RedirectLookupRow(
        String longUrl,
        ShortUrlStatus status,
        Instant expiresAt
) {
}
```

Repository:

```java
@Repository
public class RedirectJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public RedirectJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<RedirectLookupRow> findRedirectByShortCode(String shortCode) {
        String sql = """
                SELECT long_url, status, expires_at
                FROM short_urls
                WHERE short_code = ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new RedirectLookupRow(
                        rs.getString("long_url"),
                        ShortUrlStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("expires_at") == null
                                ? null
                                : rs.getTimestamp("expires_at").toInstant()
                ), shortCode)
                .stream()
                .findFirst();
    }
}
```

Service:

```java
@Transactional(readOnly = true)
public RedirectResult resolve(String shortCode) {
    RedirectLookupRow row = redirectJdbcRepository.findRedirectByShortCode(shortCode)
            .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));

    if (row.status() == ShortUrlStatus.BLOCKED) {
        throw new ShortCodeBlockedException(shortCode);
    }

    if (row.status() == ShortUrlStatus.DELETED) {
        throw new ShortCodeNotFoundException(shortCode);
    }

    if (row.expiresAt() != null && row.expiresAt().isBefore(Instant.now())) {
        throw new ShortCodeExpiredException(shortCode);
    }

    return new RedirectResult(row.longUrl());
}
```

This is excellent for hot read path.

Why?

```text
Only required columns are selected.
No entity state tracking is needed.
No accidental relationship loading.
SQL is visible.
Index tuning is straightforward.
```

Hot path memory:

```text
For redirect, you do not need an object graph.
You need one fast lookup.
```

---

## 18. Transaction Behavior

Both JPA and JDBC can use Spring transactions.

```java
@Transactional
public void doWork() {
    // repository calls
}
```

But they behave differently inside the transaction.

### JDBC transaction

```text
Open connection.
Execute SQL immediately.
Commit or rollback.
```

ASCII:

```text
@Transactional
   |
   v
INSERT SQL executes now
   |
   v
UPDATE SQL executes now
   |
   v
commit
```

### JPA transaction

```text
Open persistence context.
Manage entities.
Queue/detect changes.
Flush SQL before commit.
Commit or rollback.
```

ASCII:

```text
@Transactional
   |
   v
Entity loaded/created
   |
   v
Entity state changed
   |
   v
Hibernate dirty checking
   |
   v
flush SQL
   |
   v
commit
```

Important JPA detail:

```text
SQL may execute on save, flush, query, or commit depending on ID strategy and flush mode.
```

This is why JPA must be learned deeply.

JDBC is more direct:

```text
The SQL you call is the SQL that runs.
```

---

## 19. Dirty Checking vs Explicit SQL

Dirty checking is JPA's ability to detect changed managed entities.

Example:

```java
@Transactional
public void block(String shortCode) {
    ShortUrl shortUrl = repository.findByShortCode(shortCode)
            .orElseThrow(...);

    shortUrl.block();
}
```

No explicit save.

Hibernate can still run:

```sql
UPDATE short_urls
SET status = 'BLOCKED'
WHERE id = ?;
```

ASCII:

```text
Load entity
  |
  v
snapshot stored by Hibernate
  |
  v
change field in Java
  |
  v
commit
  |
  v
compare current state vs snapshot
  |
  v
UPDATE changed columns
```

JDBC version:

```java
jdbcTemplate.update(
        "UPDATE short_urls SET status = 'BLOCKED' WHERE short_code = ?",
        shortCode
);
```

Comparison:

```text
JPA:
    Object changed -> SQL generated.

JDBC:
    SQL written -> row changed.
```

JPA is expressive for domain behavior.
JDBC is explicit for database behavior.

---

## 20. Performance Tradeoffs

Performance is not simply:

```text
JPA slow, JDBC fast.
```

That is too shallow.

Better view:

```text
JPA can be fast when used correctly.
JDBC is easier to make predictable.
```

### JPA performance risks

```text
1. Loading full entities when projection is enough.
2. N+1 queries due to lazy relationships.
3. Unexpected flush before query.
4. Too many managed entities in persistence context.
5. Batch inserts not configured.
6. Complex generated SQL not reviewed.
7. Misuse of entity relationships.
```

### JDBC performance risks

```text
1. Manual mapping bugs.
2. Duplicate SQL strings everywhere.
3. Harder CRUD maintenance.
4. SQL injection if parameters are not bound.
5. Poor abstraction if repositories become messy.
6. Vendor-specific SQL can reduce portability.
```

For MiniURLShortener:

```text
Create path:
    JPA is fine.

Redirect hot path:
    JPA projection or JDBC projection is better than full entity load.

Analytics/bulk:
    JDBC is usually better.
```

ASCII:

```text
Cold/simple path       Warm business path       Hot SQL path
      |                      |                       |
      v                      v                       v
     JPA                    JPA               JDBC / projection
```

---

## 21. When JPA Is Better

Use JPA when:

```text
1. You have domain entities with lifecycle.
2. CRUD is common.
3. You want less boilerplate.
4. You need transaction-level object state tracking.
5. You want repository query methods.
6. You have moderate read/write volume.
7. You want clean domain behavior methods.
```

MiniURLShortener examples:

```text
create link
block link
delete link
admin update metadata
owner dashboard CRUD
```

JPA shines when code reads like business behavior.

Example:

```java
shortUrl.block();
shortUrl.extendExpiry(newExpiry);
shortUrl.changeDestination(newLongUrl);
```

This is domain-first.

Good for maintainability.

---

## 22. When JDBC Is Better

Use JDBC when:

```text
1. Query shape matters more than entity lifecycle.
2. You need exact SQL control.
3. You need high-throughput hot paths.
4. You perform bulk updates/inserts.
5. You use database-specific features.
6. You need lightweight projections.
7. You want predictable performance.
```

MiniURLShortener examples:

```text
redirect lookup by shortCode
atomic click_count increment
bulk analytics export
expired link cleanup
custom reporting queries
read-replica optimized queries
```

Example JDBC hot-path query:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?;
```

That query is simple, index-friendly, and clear.

For 100k RPS design, you will later put Redis before DB.

But when DB is hit, you want the DB query to be lean.

---

## 23. Hybrid Production Strategy

Best production answer is often:

```text
Use both, intentionally.
```

Recommended MiniURLShortener strategy:

```text
JPA:
    normal CRUD
    create link
    admin update
    domain lifecycle operations

JDBC:
    hot redirect lookup projection
    click count atomic update
    bulk cleanup
    analytics queries
```

ASCII:

```text
                  Service Layer
                       |
       +---------------+----------------+
       |                                |
       v                                v
+----------------+              +----------------+
| JPA Repository |              | JDBC Repository|
| Domain CRUD    |              | Hot queries    |
+----------------+              +----------------+
       |                                |
       +---------------+----------------+
                       |
                       v
                    PostgreSQL
```

Important rule:

```text
Do not mix randomly.
Mix by access pattern.
```

Bad hybrid:

```text
Some developers use JPA, some use JDBC, no reason.
```

Good hybrid:

```text
JPA for entity lifecycle.
JDBC for hot/query-specific paths.
```

This is senior-level reasoning.

---

## 24. Step-by-Step Dry Runs

### Dry Run 1: Create URL With JPA

Request:

```json
{
  "longUrl": "https://example.com/article"
}
```

Flow:

```text
1. Controller receives request.
2. Service validates longUrl.
3. Service generates shortCode abc123.
4. Service creates ShortUrl entity.
5. repository.save(entity) is called.
6. Entity becomes managed/persisted.
7. Hibernate generates INSERT.
8. Transaction commits.
9. Response returns abc123.
```

ASCII:

```text
DTO -> Entity -> Persistence Context -> INSERT -> DB
```

---

### Dry Run 2: Create URL With JDBC

Request:

```json
{
  "longUrl": "https://example.com/article"
}
```

Flow:

```text
1. Controller receives request.
2. Service validates longUrl.
3. Service generates shortCode abc123.
4. Service calls jdbc repository insert.
5. Repository builds INSERT SQL.
6. JdbcTemplate binds values.
7. PostgreSQL executes INSERT.
8. Response returns abc123.
```

ASCII:

```text
DTO -> SQL params -> INSERT -> DB -> response
```

---

### Dry Run 3: Redirect Lookup With JPA Entity

Request:

```http
GET /abc123
```

Flow:

```text
1. Controller extracts shortCode.
2. Service calls findByShortCode.
3. Hibernate runs SELECT.
4. Full ShortUrl entity is loaded.
5. Service checks status and expiry.
6. Service returns longUrl.
7. Controller returns 302 redirect.
```

Potential issue:

```text
Full entity may be more than needed later.
```

---

### Dry Run 4: Redirect Lookup With JDBC Projection

Request:

```http
GET /abc123
```

Flow:

```text
1. Controller extracts shortCode.
2. Service calls JDBC projection repository.
3. SQL selects only long_url, status, expires_at.
4. Row maps to RedirectLookupRow.
5. Service checks status and expiry.
6. Controller returns 302 redirect.
```

Benefit:

```text
Lean query. No entity tracking. Predictable hot path.
```

---

### Dry Run 5: Click Count Increment

Naive JPA:

```text
1. Load entity by shortCode.
2. incrementClickCount().
3. Commit.
4. Hibernate updates row.
```

Possible problem:

```text
Many redirects cause many read-modify-write cycles.
```

Better JDBC:

```sql
UPDATE short_urls
SET click_count = click_count + 1
WHERE short_code = ?;
```

Best high-scale later:

```text
Redirect emits Kafka event.
Worker aggregates clicks asynchronously.
```

---

## 25. Internal Execution Walkthrough

### JPA create internals

```text
1. @Transactional opens transaction.
2. EntityManager/persistence context starts.
3. Service creates ShortUrl entity.
4. repository.save calls EntityManager.persist or merge.
5. Entity becomes managed.
6. Hibernate prepares SQL.
7. Flush sends INSERT.
8. Commit finalizes transaction.
9. Entity may now have generated id.
```

ASCII:

```text
@Transactional
     |
     v
Persistence Context starts
     |
     v
save(entity)
     |
     v
entity tracked
     |
     v
flush
     |
     v
INSERT SQL
     |
     v
commit
```

### JDBC create internals

```text
1. @Transactional opens transaction.
2. JdbcTemplate obtains connection from HikariCP.
3. PreparedStatement is created.
4. Parameters are bound safely.
5. SQL executes immediately.
6. Transaction commits.
7. Connection returns to pool.
```

ASCII:

```text
@Transactional
     |
     v
Hikari connection
     |
     v
PreparedStatement
     |
     v
bind params
     |
     v
execute INSERT
     |
     v
commit
```

Key difference:

```text
JPA has object tracking between service and SQL.
JDBC goes directly from SQL call to database execution.
```

---

## 26. Production Failure Stories

### Failure Story 1: JPA N+1 in admin dashboard

Admin dashboard lists URLs and owner data.

Code loads 100 URLs.

Each URL lazily loads owner.

Result:

```text
1 query for URLs
100 queries for owners
101 total queries
```

Root cause:

```text
Entity relationships used without fetch planning.
```

Fix:

```text
Use fetch join, entity graph, projection, or JDBC query.
```

Lesson:

```text
JPA is powerful, but generated SQL must be inspected.
```

---

### Failure Story 2: Hot redirect path loads full entity

Redirect API becomes high traffic.

Each request loads full entity with many columns.

Later entity includes relations and metadata.

Latency increases.

Root cause:

```text
Using domain entity for a read path that only needed longUrl.
```

Fix:

```text
Use Redis cache first.
On DB miss, use JDBC/JPA projection selecting only required columns.
```

Lesson:

```text
Hot path should be projection-first, not object-graph-first.
```

---

### Failure Story 3: JDBC SQL duplicated everywhere

Multiple repositories write similar SQL.

A column is renamed.

Half the queries break.

Root cause:

```text
No repository discipline and no integration tests.
```

Fix:

```text
Keep SQL in focused repositories.
Use named constants carefully.
Add Testcontainers integration tests.
```

Lesson:

```text
JDBC gives control, but you must manage consistency.
```

---

### Failure Story 4: Dirty checking surprise

Developer loads entity in transaction and changes field for temporary calculation.

At commit, Hibernate persists the change.

Root cause:

```text
Managed entity was modified unintentionally.
```

Fix:

```text
Use read-only transaction, DTO projection, or avoid mutating managed entities.
```

Lesson:

```text
With JPA, changing managed objects can change the database.
```

---

### Failure Story 5: Manual JDBC mapping bug

SQL selects `expires_at`.

Mapper accidentally reads `created_at` into expiresAt.

Expired links behave incorrectly.

Root cause:

```text
Manual mapping error.
```

Fix:

```text
Integration tests with realistic rows.
Prefer small record projections.
```

Lesson:

```text
JDBC is explicit, but explicit mapping must be tested.
```

---

## 27. Debugging Mindset

When something goes wrong, ask different questions depending on JPA or JDBC.

### JPA debugging questions

```text
What SQL did Hibernate generate?
Was the entity managed or detached?
Did flush happen earlier than expected?
Was a lazy relation triggered?
Did dirty checking update something unexpectedly?
Is @Transactional active?
Is the query loading more columns/rows than needed?
```

Useful config for development:

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```

### JDBC debugging questions

```text
What exact SQL ran?
Were parameters bound correctly?
Is the RowMapper correct?
Is the query using the expected index?
Does the SQL return too many columns?
Is there SQL injection risk?
Are exceptions translated correctly?
```

Useful SQL:

```sql
EXPLAIN ANALYZE
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = 'abc123';
```

Debug map:

```text
Unexpected query count -> JPA fetch/lazy issue
Unexpected update      -> JPA dirty checking issue
Wrong data             -> JDBC mapper or SQL issue
Slow query             -> index/query plan issue
Duplicate key          -> DB constraint + exception mapping issue
```

Golden rule:

```text
Do not debug persistence only from Java code. Always inspect SQL.
```

---

## 28. Common Mistakes

### Mistake 1: Thinking JPA means no SQL

Wrong:

```text
I use JPA, so I do not need to understand SQL.
```

Correct:

```text
JPA generates SQL. Senior engineers inspect and tune it.
```

### Mistake 2: Using entities for every read

Wrong:

```text
Every query returns ShortUrl entity.
```

Correct:

```text
Hot reads can use projections.
```

### Mistake 3: Using JDBC everywhere without structure

Wrong:

```text
SQL scattered across services.
```

Correct:

```text
SQL belongs in repository classes.
```

### Mistake 4: Putting persistence logic in controller

Wrong:

```text
Controller calls JdbcTemplate directly.
```

Correct:

```text
Controller calls service. Service calls repository.
```

### Mistake 5: Ignoring transaction boundary

Wrong:

```text
Assume save always means committed.
```

Correct:

```text
Commit happens when transaction completes.
```

### Mistake 6: Using JPA dirty checking unknowingly

Wrong:

```text
Modify managed entity casually.
```

Correct:

```text
Understand that managed entity changes can flush to DB.
```

### Mistake 7: Not testing SQL behavior

Wrong:

```text
Only mock repository tests.
```

Correct:

```text
Use integration tests for repository behavior.
```

### Mistake 8: Blindly saying JDBC is always faster

Wrong:

```text
JDBC is always better.
```

Correct:

```text
JDBC is more predictable; JPA can be fast when used properly.
```

---

## 29. Testing Strategy

Test persistence with real database behavior.

Use Testcontainers PostgreSQL later.

### JPA tests

Test:

```text
save ShortUrl inserts row
findByShortCode returns entity
existsByShortCode works
duplicate shortCode throws DataIntegrityViolationException
expired timestamp persists correctly
status enum persists as string
```

Example:

```java
@DataJpaTest
class ShortUrlJpaRepositoryTest {

    @Autowired
    private ShortUrlJpaRepository repository;

    @Test
    void shouldSaveAndFindByShortCode() {
        ShortUrl shortUrl = new ShortUrl(
                "abc123",
                "https://example.com",
                null
        );

        repository.save(shortUrl);

        Optional<ShortUrl> found = repository.findByShortCode("abc123");

        assertThat(found).isPresent();
        assertThat(found.get().getLongUrl()).isEqualTo("https://example.com");
    }
}
```

### JDBC tests

Test:

```text
insert writes correct row
projection returns only needed data
RowMapper maps expires_at correctly
missing shortCode returns empty
atomic increment updates count
```

Example:

```java
@Test
void shouldFindRedirectProjection() {
    jdbcTemplate.update("""
            INSERT INTO short_urls
                (short_code, long_url, status, created_at, click_count)
            VALUES
                (?, ?, 'ACTIVE', now(), 0)
            """, "abc123", "https://example.com");

    Optional<RedirectLookupRow> row = redirectJdbcRepository.findRedirectByShortCode("abc123");

    assertThat(row).isPresent();
    assertThat(row.get().longUrl()).isEqualTo("https://example.com");
    assertThat(row.get().status()).isEqualTo(ShortUrlStatus.ACTIVE);
}
```

Testing rule:

```text
Mock service logic.
Integration-test repository logic.
```

Because persistence bugs often hide in:

```text
SQL syntax
column mapping
constraints
transactions
indexes
PostgreSQL-specific behavior
```

---

## 30. Interview-Ready Explanation

If interviewer asks:

```text
What is the difference between JPA and JDBC, and what would you use in a URL shortener?
```

Strong answer:

```text
JDBC is SQL-first database access. I write the SQL, bind parameters, execute it,
and map rows manually. It gives exact control and predictable performance, which
is useful for hot paths, projections, bulk updates, and database-specific queries.

JPA is object-first persistence. I work with entities, and Hibernate tracks entity
state inside the persistence context, generates SQL, performs dirty checking, and
flushes changes during the transaction. It reduces CRUD boilerplate and is good
for domain lifecycle operations.

For a URL shortener, I would use JPA for normal CRUD and lifecycle operations such
as creating, blocking, deleting, or updating links. For the hot redirect path, I
would avoid loading a full entity and use either a JPA projection or JDBC query
selecting only long_url, status, and expires_at. For click counters and analytics,
I would prefer JDBC or async aggregation because those paths are SQL-first and
performance-sensitive. The main principle is not JPA versus JDBC as a religion,
but choosing based on access pattern.
```

Why this answer is strong:

```text
1. Defines both accurately.
2. Mentions persistence context and dirty checking.
3. Mentions SQL control and projections.
4. Applies tradeoff to URL shortener.
5. Separates CRUD path from hot path.
6. Sounds production-aware, not framework-biased.
```

Senior one-liner:

```text
JPA is great when object lifecycle is the problem; JDBC is great when SQL shape is the product.
```

---

## 31. Senior Engineer Checklist

Before choosing persistence approach, ask:

```text
[ ] Is this path CRUD-heavy or query-heavy?
[ ] Does it need full entity lifecycle?
[ ] Does it need only a small projection?
[ ] Is it a hot path?
[ ] Do I need exact SQL control?
[ ] Will this query run at very high RPS?
[ ] Could JPA lazy loading create N+1 issues?
[ ] Do I understand the generated SQL?
[ ] Is this a bulk operation?
[ ] Do I need database-specific features?
[ ] Are repository tests using real database behavior?
[ ] Are transaction boundaries clear?
[ ] Is SQL hidden where it should be visible?
[ ] Is manual mapping tested if using JDBC?
```

MiniURLShortener recommended choice:

```text
[ ] JPA for create/admin CRUD.
[ ] JDBC or projection for redirect lookup.
[ ] JDBC or async worker for click count updates.
[ ] JDBC for analytics/reporting.
[ ] Always inspect SQL for performance-sensitive paths.
```

---

## 32. One-Page Cheat Sheet

```text
Core mental model:
JPA manages object state.
JDBC executes SQL commands.

JPA:
- Object-first
- Entity lifecycle
- Persistence context
- Dirty checking
- Generated SQL
- Less CRUD boilerplate
- Hidden performance risks

JDBC:
- SQL-first
- Explicit queries
- Manual mapping
- Predictable SQL
- Good for hot paths
- More boilerplate
- Mapping must be tested

MiniURLShortener usage:
Create URL:
    JPA is good.

Redirect lookup:
    JDBC or projection is better for hot path.

Click count:
    JDBC atomic update or async aggregation.

Analytics:
    JDBC is usually better.

JPA danger:
    N+1, full entity loading, unexpected flush, dirty checking surprises.

JDBC danger:
    duplicated SQL, mapper bugs, messy repositories, SQL injection if not parameterized.

Best answer:
    Use both intentionally by access pattern.
```

---

## 33. One Picture To Remember

```text
                    JPA vs JDBC MENTAL MODEL

                         Repository Layer
                               |
              +----------------+----------------+
              |                                 |
              v                                 v
      +------------------+             +-------------------+
      | JPA / Hibernate  |             | JDBC / JdbcTemplate|
      +------------------+             +-------------------+
      | Object-first     |             | SQL-first          |
      | Entity managed   |             | SQL explicit       |
      | Dirty checking   |             | Manual mapping     |
      | CRUD friendly    |             | Hot path friendly  |
      +------------------+             +-------------------+
              |                                 |
              v                                 v
      Persistence Context                 PreparedStatement
              |                                 |
              v                                 v
       Hibernate SQL                    PostgreSQL SQL
              |                                 |
              +---------------+-----------------+
                              |
                              v
                         short_urls table

FINAL MEMORY:

Use JPA when the use case is about entity lifecycle.
Use JDBC when the use case is about exact SQL shape.
Use both intentionally, never randomly.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. JPA is object-state management; JDBC is direct SQL execution.
2. JPA reduces CRUD boilerplate but introduces persistence context behavior.
3. JDBC gives exact SQL control but requires manual mapping and discipline.
4. For MiniURLShortener, JPA is good for create/admin CRUD, while JDBC/projections are better for hot redirect and analytics paths.
5. Senior engineers choose persistence style by access pattern, not by framework loyalty.
```

Next chapter:

```text
013_Transactions.md
```
