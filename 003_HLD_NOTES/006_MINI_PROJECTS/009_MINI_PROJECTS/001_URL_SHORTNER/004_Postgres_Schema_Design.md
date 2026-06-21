# 004_Postgres_Schema_Design.md
# MiniURLShortener — PostgreSQL Schema Design

> Core mental model: **PostgreSQL is the durable source of truth for the mapping `shortCode -> longUrl`. The schema is not just storage; it protects correctness using constraints, indexes, timestamps, status fields, and query-friendly structure.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What The Database Must Guarantee](#4-what-the-database-must-guarantee)
- [5. Source Of Truth Mental Model](#5-source-of-truth-mental-model)
- [6. Core Entity: short_urls](#6-core-entity-short_urls)
- [7. First Schema Version](#7-first-schema-version)
- [8. Column-By-Column Design](#8-column-by-column-design)
- [9. Primary Key vs short_code](#9-primary-key-vs-short_code)
- [10. Unique Constraint Mental Model](#10-unique-constraint-mental-model)
- [11. Index Design Mental Model](#11-index-design-mental-model)
- [12. Redirect Query Path](#12-redirect-query-path)
- [13. Create Query Path](#13-create-query-path)
- [14. Expiry And Status Design](#14-expiry-and-status-design)
- [15. Custom Alias Design](#15-custom-alias-design)
- [16. Timestamp Design](#16-timestamp-design)
- [17. Analytics Schema Boundary](#17-analytics-schema-boundary)
- [18. Flyway Migration Mindset](#18-flyway-migration-mindset)
- [19. Spring Boot JPA Entity Mapping](#19-spring-boot-jpa-entity-mapping)
- [20. Repository Query Design](#20-repository-query-design)
- [21. Step-by-Step Dry Runs](#21-step-by-step-dry-runs)
- [22. Query Execution Mental Model](#22-query-execution-mental-model)
- [23. Production Failure Stories](#23-production-failure-stories)
- [24. Debugging Schema Problems](#24-debugging-schema-problems)
- [25. Common Mistakes](#25-common-mistakes)
- [26. Interview-Ready Explanation](#26-interview-ready-explanation)
- [27. Senior Engineer Checklist](#27-senior-engineer-checklist)
- [28. One-Page Cheat Sheet](#28-one-page-cheat-sheet)
- [29. One Picture To Remember](#29-one-picture-to-remember)

---

## 1. Why This Exists

In the previous chapters, the URL shortener mental model became clear:

```text
shortCode -> longUrl
```

Now we need to store that mapping durably.

Redis can make redirect fast later.
Kafka can move analytics events later.
Kubernetes can run many app pods later.

But the actual mapping must survive:

```text
application restart
Redis restart
pod crash
deployment rollout
temporary cache loss
```

That durable place is PostgreSQL.

The database schema is not just a table.

It defines the system’s most important invariants:

```text
1. A short code must be unique.
2. A long URL must be stored safely.
3. A short URL can be active, blocked, expired, or deleted.
4. Redirect lookup must be fast.
5. Expiry checks must be possible.
6. Creation timestamps must be traceable.
7. Future analytics and ownership must have extension points.
```

Bad schema design creates long-term pain:

```text
duplicate short codes
slow redirects
unclear expired-link behavior
hard migrations
no audit fields
wrong indexes
cache inconsistency
data cleanup problems
```

Good schema design makes the rest of the project stable.

---

## 2. The One Core Mental Model

PostgreSQL is the:

```text
DURABLE SOURCE OF TRUTH
```

for:

```text
shortCode -> longUrl
```

The schema is the database-level contract.

Application code may have bugs.
Multiple app pods may race.
Redis may lose cached data.
Kafka may lag.

But PostgreSQL must protect the core mapping.

ASCII mental model:

```text
                    MINI URL SHORTENER DATA TRUTH

+------------------+       cache hit        +------------------+
| Redis            | ---------------------> | Fast redirect    |
| speed layer      |                        | optional later   |
+------------------+                        +------------------+
        ^
        |
        | cache miss / warmup
        |
+------------------+
| PostgreSQL       |
| source of truth  |
| UNIQUE(short_code)
| indexed lookup   |
+------------------+
        ^
        |
        | write path
        |
+------------------+
| Spring Boot App  |
| create/redirect  |
+------------------+
```

One-line memory:

```text
The database schema is the place where business truth becomes enforceable structure.
```

For URL shortener, the most important enforceable truth is:

```text
one short_code maps to at most one long_url
```

---

## 3. Problem Statement

Design the PostgreSQL schema for version 1 of MiniURLShortener.

The schema must support:

```text
1. Creating a short URL.
2. Redirecting by short code.
3. Enforcing unique short codes.
4. Supporting optional custom alias.
5. Supporting optional expiry.
6. Supporting blocked/deleted/active status.
7. Supporting timestamps.
8. Supporting fast lookup by short_code.
9. Being clean enough for future Redis, Kafka, users, analytics, and sharding.
```

Version 1 table:

```text
short_urls
```

Future tables may include:

```text
users
click_events
api_keys
domains
blocked_urls
```

But do not build all of them now.

In this chapter, focus on the one table that makes the system work.

---

## 4. What The Database Must Guarantee

The database must guarantee invariants.

An invariant is a rule that must always remain true.

For URL shortener:

```text
Invariant 1:
short_code must be unique.

Invariant 2:
long_url must not be null.

Invariant 3:
status must be one of known values.

Invariant 4:
created_at must always exist.

Invariant 5:
updated_at must always exist.

Invariant 6:
expires_at can be null, but if present it affects redirect behavior.

Invariant 7:
id must uniquely identify the row internally.
```

Why database guarantees matter:

```text
Application validation is useful but not enough.
```

Multiple app instances can race:

```text
Pod A generates code abc123
Pod B generates code abc123
Both try to insert
```

Only the database can safely enforce:

```text
UNIQUE(short_code)
```

ASCII:

```text
+---------+       insert abc123       +---------------------+
| Pod A   | ------------------------> | PostgreSQL          |
+---------+                           | UNIQUE(short_code)  |
                                      +---------------------+
+---------+       insert abc123                 |
| Pod B   | -------------------------- duplicate key error
+---------+
```

Senior mindset:

```text
Business-critical invariants belong in the database, not only in service code.
```

---

## 5. Source Of Truth Mental Model

Postgres should answer:

```text
Does this short code exist?
What long URL does it map to?
Is it active?
Is it expired?
When was it created?
```

Redis later should answer the same thing faster, but Redis is derived.

Correct data ownership:

```text
Postgres owns truth.
Redis copies truth for speed.
Kafka receives events for analytics.
```

Wrong mental model:

```text
Redis is the main database.
Postgres is backup.
```

Correct mental model:

```text
Postgres is main database.
Redis is disposable speed layer.
```

If Redis is empty:

```text
System should still work by reading Postgres.
```

If Postgres loses mapping:

```text
System is broken even if Redis still has temporary value.
```

ASCII:

```text
                Truth vs Speed

+----------------------+           +----------------------+
| PostgreSQL           |           | Redis                |
| durable              |           | fast                 |
| authoritative        |           | derived              |
| constraint-protected |           | may be stale/missing |
+----------------------+           +----------------------+
           |                                  ^
           | load on cache miss               |
           +----------------------------------+
```

Rule:

```text
Never design a cache before designing the source of truth.
```

---

## 6. Core Entity: short_urls

The first core entity is:

```text
short_urls
```

It stores one row per short link.

Conceptual table:

```text
+----+------------+----------------------------+--------+------------+---------------------+
| id | short_code | long_url                   | status | expires_at | created_at          |
+----+------------+----------------------------+--------+------------+---------------------+
| 1  | aB91xZ     | https://example.com/a      | ACTIVE | null       | 2026-06-21 10:00:00 |
| 2  | java17     | https://docs.oracle.com/17 | ACTIVE | null       | 2026-06-21 10:01:00 |
| 3  | sale       | https://shop.com/deal      | ACTIVE | 2026-12-31 | 2026-06-21 10:02:00 |
+----+------------+----------------------------+--------+------------+---------------------+
```

This row answers redirect:

```text
GET /aB91xZ
```

Lookup:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = 'aB91xZ';
```

Then app decides:

```text
ACTIVE and not expired -> 302 redirect
not found              -> 404
expired                -> 410
blocked                -> 403
```

The table is small conceptually, but powerful.

---

## 7. First Schema Version

Recommended v1 SQL:

```sql
CREATE TABLE short_urls (
    id BIGSERIAL PRIMARY KEY,

    short_code VARCHAR(32) NOT NULL,
    long_url TEXT NOT NULL,

    is_custom_alias BOOLEAN NOT NULL DEFAULT false,

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    expires_at TIMESTAMPTZ NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uk_short_urls_short_code UNIQUE (short_code),
    CONSTRAINT chk_short_urls_status
        CHECK (status IN ('ACTIVE', 'BLOCKED', 'DELETED')),
    CONSTRAINT chk_short_urls_short_code_length
        CHECK (char_length(short_code) BETWEEN 4 AND 32)
);

CREATE INDEX idx_short_urls_expires_at
    ON short_urls (expires_at);
```

Important note:

```text
The UNIQUE constraint on short_code automatically creates a unique index in PostgreSQL.
```

So do not create a duplicate normal index on `short_code`.

Why not include `EXPIRED` as a status?

Because expiry can be derived from:

```text
expires_at < now()
```

For v1:

```text
status controls business blocking/deletion.
expires_at controls time expiry.
```

Decision:

```text
ACTIVE + expires_at in past = expired behavior
```

---

## 8. Column-By-Column Design

### id

```sql
id BIGSERIAL PRIMARY KEY
```

Purpose:

```text
Internal database identity.
Useful for joins, auditing, admin operations, future analytics references.
```

Why BIGSERIAL?

```text
URL shorteners may store many rows.
BIGINT gives huge capacity.
```

### short_code

```sql
short_code VARCHAR(32) NOT NULL
```

Purpose:

```text
Public lookup key used in short URL.
```

Example:

```text
aB91xZ
java17
mohamed
```

Why VARCHAR(32)?

```text
Allows generated codes and custom aliases.
Prevents unbounded values.
Keeps index reasonable.
```

### long_url

```sql
long_url TEXT NOT NULL
```

Purpose:

```text
Original target URL.
```

Why TEXT?

```text
URLs can be long.
PostgreSQL TEXT is appropriate for variable long strings.
```

### is_custom_alias

```sql
is_custom_alias BOOLEAN NOT NULL DEFAULT false
```

Purpose:

```text
Tracks whether user selected alias or system generated it.
```

Useful later for:

```text
premium feature
abuse detection
analytics
admin filtering
```

### status

```sql
status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
```

Purpose:

```text
Business control over whether redirect is allowed.
```

Values:

```text
ACTIVE
BLOCKED
DELETED
```

### expires_at

```sql
expires_at TIMESTAMPTZ NULL
```

Purpose:

```text
Optional time after which redirect should not work.
```

Null means:

```text
No expiry.
```

### created_at

```sql
created_at TIMESTAMPTZ NOT NULL DEFAULT now()
```

Purpose:

```text
Audit when link was created.
```

### updated_at

```sql
updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
```

Purpose:

```text
Audit last update.
```

Later can be updated by app or DB trigger.

---

## 9. Primary Key vs short_code

You may ask:

```text
If short_code is unique, why need id?
```

Because they serve different purposes.

```text
id:
    internal stable database identity

short_code:
    public lookup token
```

Comparison:

```text
+----------------+----------------------------+--------------------------+
| Field          | Purpose                    | Exposed to user?         |
+----------------+----------------------------+--------------------------+
| id             | internal row identity       | usually no               |
| short_code     | public URL token            | yes                      |
+----------------+----------------------------+--------------------------+
```

Why not use short_code as primary key?

Using `id` as primary key:

```text
Pros:
1. Stable numeric joins.
2. Smaller foreign keys later.
3. Easier analytics references.
4. Allows short_code policy changes.
5. Common JPA pattern.

Cons:
1. Need separate unique constraint on short_code.
```

Using `short_code` as primary key:

```text
Pros:
1. Natural key.
2. Directly represents lookup identity.

Cons:
1. Larger FK if referenced.
2. Public key becomes DB primary key.
3. Harder if alias rules change.
```

For this project:

```text
Use id as primary key.
Use short_code as unique business key.
```

ASCII:

```text
                 short_urls row

+-------------------+      +---------------------+
| id                |      | short_code          |
| internal identity |      | public lookup key   |
+-------------------+      +---------------------+
          |                           |
          v                           v
 future joins/admin          GET /{shortCode}
```

---

## 10. Unique Constraint Mental Model

The most important constraint:

```sql
CONSTRAINT uk_short_urls_short_code UNIQUE (short_code)
```

It prevents:

```text
abc123 -> https://site-a.com
abc123 -> https://site-b.com
```

Bad world without unique constraint:

```text
+----+------------+--------------------+
| id | short_code | long_url           |
+----+------------+--------------------+
| 1  | abc123     | https://a.com      |
| 2  | abc123     | https://b.com      |
+----+------------+--------------------+
```

Redirect query:

```sql
SELECT long_url FROM short_urls WHERE short_code = 'abc123';
```

Problem:

```text
Which URL should be returned?
```

Correct world:

```text
Database rejects second row.
```

Race diagram:

```text
Time ─────────────────────────────────────────>

Pod A: generate abc123 ───── insert ───── success

Pod B: generate abc123 ───── insert ───── duplicate key error

Postgres:
+---------------------------------------------+
| UNIQUE(short_code) protects the invariant   |
+---------------------------------------------+
```

Application behavior:

```text
Generated code duplicate:
    retry with new generated code.

Custom alias duplicate:
    return 409 Conflict.
```

This distinction is very important for clean user experience.

---

## 11. Index Design Mental Model

An index is a lookup accelerator.

Without index:

```text
Postgres may scan many rows.
```

With index:

```text
Postgres can jump to matching row quickly.
```

Book analogy:

```text
Without index:
    Search every page to find "short_code = abc123".

With index:
    Use dictionary/index to jump directly.
```

For URL shortener, main query:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?;
```

So `short_code` must be indexed.

The unique constraint already creates an index.

ASCII:

```text
short_code unique index

abc123  --------> row id 101
java17  --------> row id 102
sale    --------> row id 103
xY91Ab  --------> row id 104
```

Redirect lookup:

```text
GET /sale
   |
   v
index lookup sale
   |
   v
row id 103
   |
   v
long_url
```

Index for expiry:

```sql
CREATE INDEX idx_short_urls_expires_at
ON short_urls (expires_at);
```

Useful for:

```text
1. cleanup expired URLs
2. reporting expired URLs
3. background jobs later
```

Potential future composite index:

```sql
CREATE INDEX idx_short_urls_status_expires_at
ON short_urls (status, expires_at);
```

Do not add too many indexes early.

Rule:

```text
Index the queries you actually run.
```

Indexes speed reads but slow writes because every insert/update must update indexes.

---

## 12. Redirect Query Path

Redirect is the hot path.

Query:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?;
```

Why select only needed columns?

Bad:

```sql
SELECT *
FROM short_urls
WHERE short_code = ?;
```

Better:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?;
```

Reason:

```text
Redirect does not need all fields.
Keep hot path lean.
```

ASCII path:

```text
GET /aB91xZ
    |
    v
+------------------------+
| short_code index       |
+------------------------+
    |
    v
+------------------------+
| row found              |
+------------------------+
    |
    v
+-----------------------------+
| long_url/status/expires_at  |
+-----------------------------+
    |
    v
Return 302 / 403 / 404 / 410
```

Decision table:

```text
+----------------------------+----------------+
| DB result                  | HTTP result    |
+----------------------------+----------------+
| no row                     | 404 Not Found  |
| status = BLOCKED           | 403 Forbidden  |
| status = DELETED           | 404 or 410     |
| expires_at < now           | 410 Gone       |
| active and not expired     | 302 Found      |
+----------------------------+----------------+
```

Recommendation:

```text
DELETED can return 404 to avoid exposing previous existence.
EXPIRED can return 410 because product may want explicit expiry.
```

---

## 13. Create Query Path

Create path inserts a new row.

Example insert:

```sql
INSERT INTO short_urls (
    short_code,
    long_url,
    is_custom_alias,
    status,
    expires_at
) VALUES (
    'aB91xZ',
    'https://example.com/article',
    false,
    'ACTIVE',
    NULL
);
```

With DB defaults, you can omit:

```text
id
created_at
updated_at
status if default ACTIVE is accepted
is_custom_alias if false
```

Minimal insert:

```sql
INSERT INTO short_urls (
    short_code,
    long_url,
    expires_at
) VALUES (
    'aB91xZ',
    'https://example.com/article',
    NULL
);
```

Create flow diagram:

```text
+----------------------+
| App generates code   |
+----------------------+
          |
          v
+----------------------+
| INSERT row           |
+----------------------+
          |
          v
+-----------------------------+
| Does UNIQUE pass?           |
+-----------------------------+
       | yes              | no
       v                  v
+-------------+     +--------------------------+
| success     |     | duplicate key exception  |
+-------------+     +--------------------------+
                         |
                         v
              generated code? custom alias?
                    |             |
                    v             v
                  retry       return 409
```

Important:

```text
Do not rely on pre-check alone.
```

Bad:

```sql
SELECT COUNT(*) FROM short_urls WHERE short_code = 'abc123';
-- if zero, insert
```

This can race.

Correct:

```text
Try insert.
Let DB unique constraint decide.
Handle duplicate safely.
```

---

## 14. Expiry And Status Design

There are two separate concepts:

```text
status:
    business state

expires_at:
    time-based validity
```

Status values:

```text
ACTIVE
BLOCKED
DELETED
```

Expiry:

```text
expires_at is null:
    never expires

expires_at > now:
    still valid

expires_at <= now:
    expired
```

Decision diagram:

```text
              GET /{shortCode}
                     |
                     v
              Row exists?
               /                    no         yes
             |           |
             v           v
            404     status check
                       |
        +--------------+--------------+
        |                             |
     BLOCKED                       ACTIVE
        |                             |
        v                             v
       403                       expiry check
                                      |
                         +------------+------------+
                         |                         |
                   expired                    not expired
                         |                         |
                         v                         v
                        410                       302
```

Why not automatically update status to EXPIRED?

You can, but it is not necessary for redirect correctness.

Derived expiry:

```text
ACTIVE + expires_at in past = expired behavior
```

This avoids needing background jobs just to mark rows expired.

Later, a cleanup job can archive/delete expired rows.

---

## 15. Custom Alias Design

Custom alias uses the same `short_code` column.

Example request:

```json
{
  "longUrl": "https://example.com/profile/mohamed",
  "customAlias": "mohamed"
}
```

Stored:

```text
short_code = mohamed
long_url = https://example.com/profile/mohamed
is_custom_alias = true
```

No separate alias column is required.

Why track `is_custom_alias`?

```text
1. Premium feature later.
2. Abuse detection.
3. Analytics.
4. Admin filtering.
5. Product reporting.
```

Alias validation still belongs in application code too.

DB can enforce:

```text
not null
length
unique
```

Application enforces:

```text
allowed characters
reserved words
business policy
```

Reserved aliases:

```text
api
admin
actuator
health
metrics
login
logout
```

Why not only DB check for characters?

Because character policies can evolve and become complex.

Good boundary:

```text
DB enforces permanent safety invariants.
App enforces flexible business validation.
```

---

## 16. Timestamp Design

Use:

```sql
TIMESTAMPTZ
```

instead of:

```sql
TIMESTAMP
```

Why?

```text
TIMESTAMPTZ stores timestamp with timezone awareness.
Production systems run across machines, containers, regions, and logs.
```

Columns:

```sql
created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
```

Mental model:

```text
created_at = when row was born
updated_at = when row last changed
expires_at = when redirect should stop working
```

Timeline:

```text
created_at                expires_at
    |                         |
    v                         v
----+-------------------------+------------ time
 link created              no redirect after this
```

Important:

```text
updated_at will not automatically update on row update unless application or trigger changes it.
```

Options:

```text
1. Update in application using JPA @PreUpdate.
2. Use database trigger.
3. Use Hibernate annotations.
```

For v1, JPA can manage timestamps.

Later production-grade DB trigger can be considered.

---

## 17. Analytics Schema Boundary

Do not put all click analytics into `short_urls`.

Bad:

```text
short_urls table:
    id
    short_code
    long_url
    click_timestamp_1
    click_timestamp_2
    click_timestamp_3
    ...
```

Also bad for every redirect:

```sql
UPDATE short_urls
SET click_count = click_count + 1
WHERE short_code = ?;
```

Why?

```text
High redirect traffic causes write amplification.
Popular links become write hot spots.
```

For v1, a simple `click_count` may be acceptable for learning, but understand the limitation.

Recommended for clean design:

```text
short_urls:
    stores mapping and lifecycle

click_events later:
    stores analytics events

Kafka later:
    decouples redirect from analytics writes
```

Boundary diagram:

```text
+--------------------+          +----------------------+
| short_urls         |          | click_events later   |
| mapping truth      |          | analytics history    |
+--------------------+          +----------------------+
| short_code         |          | short_code           |
| long_url           |          | clicked_at           |
| status             |          | user_agent           |
| expires_at         |          | ip_hash              |
+--------------------+          +----------------------+
```

Rule:

```text
Do not let analytics corrupt or slow the mapping table.
```

For chapter 004, keep schema focused on mapping.

---

## 18. Flyway Migration Mindset

In production, schema should be versioned.

Do not rely on Hibernate to create/modify schema automatically in production.

Bad production mindset:

```yaml
spring.jpa.hibernate.ddl-auto: update
```

Why dangerous?

```text
1. Unexpected schema changes.
2. Hard to review.
3. Can fail during deployment.
4. Not audit-friendly.
```

Better:

```text
Use Flyway migration files.
```

Add dependency:

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

Migration file path:

```text
src/main/resources/db/migration/V1__create_short_urls_table.sql
```

Content:

```sql
CREATE TABLE short_urls (
    id BIGSERIAL PRIMARY KEY,

    short_code VARCHAR(32) NOT NULL,
    long_url TEXT NOT NULL,

    is_custom_alias BOOLEAN NOT NULL DEFAULT false,

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    expires_at TIMESTAMPTZ NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uk_short_urls_short_code UNIQUE (short_code),
    CONSTRAINT chk_short_urls_status
        CHECK (status IN ('ACTIVE', 'BLOCKED', 'DELETED')),
    CONSTRAINT chk_short_urls_short_code_length
        CHECK (char_length(short_code) BETWEEN 4 AND 32)
);

CREATE INDEX idx_short_urls_expires_at
    ON short_urls (expires_at);
```

Migration mental model:

```text
Database schema evolves like code.
Every change gets a versioned file.
```

ASCII:

```text
V1__create_short_urls_table.sql
        |
        v
V2__add_click_events_table.sql
        |
        v
V3__add_user_ownership.sql
```

Flyway tracks applied migrations in:

```text
flyway_schema_history
```

---

## 19. Spring Boot JPA Entity Mapping

Entity:

```java
package com.miniurl.shortener.url.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "short_urls",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_short_urls_short_code",
            columnNames = "short_code"
        )
    },
    indexes = {
        @Index(
            name = "idx_short_urls_expires_at",
            columnList = "expires_at"
        )
    }
)
public class ShortUrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", nullable = false, length = 32)
    private String shortCode;

    @Column(name = "long_url", nullable = false, columnDefinition = "TEXT")
    private String longUrl;

    @Column(name = "is_custom_alias", nullable = false)
    private boolean customAlias;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ShortUrlStatus status = ShortUrlStatus.ACTIVE;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ShortUrlEntity() {
        // Required by JPA
    }

    public ShortUrlEntity(
            String shortCode,
            String longUrl,
            boolean customAlias,
            Instant expiresAt
    ) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.customAlias = customAlias;
        this.expiresAt = expiresAt;
        this.status = ShortUrlStatus.ACTIVE;
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
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

    public boolean isCustomAlias() {
        return customAlias;
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void block() {
        this.status = ShortUrlStatus.BLOCKED;
    }

    public void markDeleted() {
        this.status = ShortUrlStatus.DELETED;
    }
}
```

Enum:

```java
package com.miniurl.shortener.url.entity;

public enum ShortUrlStatus {
    ACTIVE,
    BLOCKED,
    DELETED
}
```

Why `EnumType.STRING`?

Bad:

```java
@Enumerated(EnumType.ORDINAL)
```

Stores:

```text
ACTIVE = 0
BLOCKED = 1
DELETED = 2
```

If enum order changes, data meaning can break.

Good:

```java
@Enumerated(EnumType.STRING)
```

Stores:

```text
ACTIVE
BLOCKED
DELETED
```

Readable and safer.

---

## 20. Repository Query Design

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

Important:

```text
existsByShortCode is useful for user-friendly checks,
but UNIQUE constraint is still mandatory.
```

For hot redirect path, later you may optimize with projection:

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

Repository projection query:

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
Redirect does not need full entity.
Projection reduces unnecessary loading.
```

But for v1:

```text
findByShortCode is fine.
```

Senior mindset:

```text
Start simple, know how to optimize the hot path later.
```

---

## 21. Step-by-Step Dry Runs

### Dry Run 1: Insert generated short URL

Input:

```text
longUrl = https://example.com/article
generated shortCode = aB91xZ
expiresAt = null
```

SQL:

```sql
INSERT INTO short_urls (
    short_code,
    long_url,
    is_custom_alias,
    status,
    expires_at
) VALUES (
    'aB91xZ',
    'https://example.com/article',
    false,
    'ACTIVE',
    NULL
);
```

Database checks:

```text
1. id generated automatically.
2. short_code is not null.
3. long_url is not null.
4. status is allowed.
5. short_code uniqueness is checked.
6. created_at default set.
7. updated_at default set.
```

Result row:

```text
+----+------------+-----------------------------+-----------------+--------+------------+
| id | short_code | long_url                    | is_custom_alias | status | expires_at |
+----+------------+-----------------------------+-----------------+--------+------------+
| 1  | aB91xZ     | https://example.com/article | false           | ACTIVE | null       |
+----+------------+-----------------------------+-----------------+--------+------------+
```

---

### Dry Run 2: Insert duplicate generated code

Existing:

```text
aB91xZ -> https://example.com/article
```

New insert:

```sql
INSERT INTO short_urls (short_code, long_url)
VALUES ('aB91xZ', 'https://another.com');
```

Database:

```text
UNIQUE(short_code) fails.
```

Error concept:

```text
duplicate key value violates unique constraint "uk_short_urls_short_code"
```

Application behavior:

```text
Generated code:
    catch duplicate key
    generate another code
    retry limited times
```

---

### Dry Run 3: Insert duplicate custom alias

Existing:

```text
mohamed -> https://example.com/profile
```

User requests:

```text
customAlias = mohamed
```

Database rejects duplicate.

Application behavior:

```text
Custom alias:
    return 409 Conflict
```

Why no retry?

```text
The user asked specifically for "mohamed".
Silently changing it would violate user expectation.
```

---

### Dry Run 4: Redirect active link

Request:

```text
GET /aB91xZ
```

Query:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = 'aB91xZ';
```

Result:

```text
long_url = https://example.com/article
status = ACTIVE
expires_at = null
```

Decision:

```text
status ACTIVE
not expired
return 302
```

---

### Dry Run 5: Redirect expired link

Row:

```text
short_code = sale
long_url = https://shop.com/deal
status = ACTIVE
expires_at = 2026-01-01T00:00:00Z
```

Current time:

```text
2026-06-21T10:00:00Z
```

Decision:

```text
expires_at < now
```

Result:

```text
410 Gone
```

No DB update is needed to know it is expired.

---

### Dry Run 6: Blocked link

Row:

```text
short_code = bad1
status = BLOCKED
```

Request:

```text
GET /bad1
```

Decision:

```text
BLOCKED links must not redirect.
```

Result:

```text
403 Forbidden
```

This allows admin/security systems to disable abusive links without deleting data.

---

## 22. Query Execution Mental Model

For redirect:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = 'aB91xZ';
```

Postgres roughly does:

```text
1. Receives query.
2. Planner checks available indexes.
3. Finds unique index on short_code.
4. Uses index to locate row quickly.
5. Reads needed columns.
6. Returns row.
```

ASCII:

```text
Query:
WHERE short_code = 'aB91xZ'

        |
        v

+-----------------------------+
| Unique index on short_code  |
+-----------------------------+
        |
        v
+-----------------------------+
| aB91xZ -> table row pointer |
+-----------------------------+
        |
        v
+-----------------------------+
| table row                   |
| long_url/status/expires_at  |
+-----------------------------+
```

Without index:

```text
Check row 1
Check row 2
Check row 3
...
Check row N
```

With index:

```text
Jump close to row immediately.
```

For a URL shortener, redirect lookup must stay fast even when table has millions of rows.

That is why `short_code` uniqueness/indexing is non-negotiable.

---

## 23. Production Failure Stories

### Failure Story 1: Duplicate short codes without DB constraint

A team generates random codes in app code.

They check:

```sql
SELECT COUNT(*) WHERE short_code = ?
```

If zero, they insert.

During traffic spike, two pods generate same code.

Both checks see zero.
Both insert.

Now:

```text
same short code maps to two different URLs
```

Users sometimes redirect to wrong destination.

Root cause:

```text
Uniqueness enforced only in application logic.
```

Fix:

```text
Add UNIQUE(short_code).
Handle duplicate key.
Repair duplicate data manually.
```

Lesson:

```text
Database constraints are production safety rails.
```

---

### Failure Story 2: Missing index makes redirect slow

At first, table has 1,000 rows.
Everything is fast.

Later, table has 50 million rows.
Redirect query has no index.

Result:

```text
DB CPU spikes
queries become slow
connection pool saturates
p99 redirect latency explodes
```

Root cause:

```text
Hot lookup column not indexed.
```

Fix:

```sql
CREATE UNIQUE INDEX CONCURRENTLY uk_short_urls_short_code
ON short_urls(short_code);
```

Lesson:

```text
Know your hot query before the table grows.
```

---

### Failure Story 3: Storing analytics in mapping table

Every redirect does:

```sql
UPDATE short_urls
SET click_count = click_count + 1
WHERE short_code = ?;
```

For one viral link, thousands of updates/sec hit same row.

Result:

```text
row lock contention
write amplification
slow redirects
DB replication lag
```

Root cause:

```text
Analytics write coupled to redirect hot path.
```

Fix:

```text
Send click events asynchronously to Kafka.
Aggregate later.
```

Lesson:

```text
Mapping table should protect redirect correctness, not become analytics bottleneck.
```

---

### Failure Story 4: Wrong timestamp type creates confusion

Team stores local timestamps without timezone.

Servers run in different regions.
Logs show UTC.
Database values look local.
Expiry behaves confusingly.

Root cause:

```text
TIMESTAMP without clear timezone strategy.
```

Fix:

```text
Use TIMESTAMPTZ and Instant in Java.
Use UTC thinking everywhere.
```

Lesson:

```text
Time bugs are production bugs.
```

---

## 24. Debugging Schema Problems

### Problem 1: Duplicate key exception

Error:

```text
duplicate key value violates unique constraint "uk_short_urls_short_code"
```

Ask:

```text
Was this generated code or custom alias?
```

If generated:

```text
retry with new code
```

If custom alias:

```text
return 409 Conflict
```

---

### Problem 2: Redirect returns 404 for existing code

Checklist:

```text
[ ] Is short_code stored exactly as requested?
[ ] Is lookup case-sensitive?
[ ] Did the app trim or transform code?
[ ] Is query using wrong column?
[ ] Is data in correct database/schema?
[ ] Is local profile pointing to correct DB?
```

Useful SQL:

```sql
SELECT id, short_code, long_url, status, expires_at
FROM short_urls
WHERE short_code = 'aB91xZ';
```

---

### Problem 3: Expired link still redirects

Checklist:

```text
[ ] Is expires_at null?
[ ] Is Java comparing Instant correctly?
[ ] Is timezone interpreted correctly?
[ ] Is app server clock correct?
[ ] Is DB value in expected timezone?
```

SQL:

```sql
SELECT short_code, expires_at, now()
FROM short_urls
WHERE short_code = 'sale';
```

---

### Problem 4: Hibernate schema validation fails

Error:

```text
Schema-validation: missing table [short_urls]
```

Meaning:

```text
JPA entity exists but DB table does not.
```

Fix:

```text
Run migration.
Check DB connection.
Check schema name.
```

---

### Problem 5: Slow redirect query

Use:

```sql
EXPLAIN ANALYZE
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = 'aB91xZ';
```

Good sign:

```text
Index Scan using uk_short_urls_short_code
```

Bad sign:

```text
Seq Scan on short_urls
```

If sequential scan appears on large table:

```text
index missing
query not using indexed column
type mismatch
function applied to column
```

Bad query example:

```sql
WHERE lower(short_code) = lower(?)
```

This may not use normal index unless functional index exists.

---

## 25. Common Mistakes

### Mistake 1: Treating schema as an afterthought

Wrong:

```text
Just create id, code, url.
```

Correct:

```text
Design constraints, indexes, status, expiry, timestamps.
```

---

### Mistake 2: No unique constraint

Wrong:

```sql
short_code VARCHAR(32)
```

Correct:

```sql
short_code VARCHAR(32) NOT NULL UNIQUE
```

---

### Mistake 3: Over-indexing early

Wrong:

```text
Create indexes on every column.
```

Problem:

```text
Slower writes
More storage
More maintenance
```

Correct:

```text
Index hot lookup and real query patterns.
```

---

### Mistake 4: Using ordinal enum

Wrong:

```java
@Enumerated(EnumType.ORDINAL)
```

Correct:

```java
@Enumerated(EnumType.STRING)
```

---

### Mistake 5: Letting Hibernate update production schema

Wrong:

```yaml
ddl-auto: update
```

Correct:

```text
Use Flyway/Liquibase migrations.
Use ddl-auto validate.
```

---

### Mistake 6: Making analytics part of core row

Wrong:

```text
Update click_count on every redirect.
```

Correct:

```text
Emit click event asynchronously.
Aggregate later.
```

---

### Mistake 7: Confusing deleted and expired

Deleted:

```text
Business/user/admin removed it.
```

Expired:

```text
Time validity ended.
```

They may return different HTTP statuses and have different audit meanings.

---

## 26. Interview-Ready Explanation

If an interviewer asks:

```text
How would you design the database schema for a URL shortener?
```

Strong answer:

```text
I would keep PostgreSQL as the durable source of truth for the shortCode-to-longUrl
mapping. The core table would be short_urls with an internal BIGSERIAL id, a
short_code column with a unique constraint, long_url as TEXT, status for business
state like ACTIVE/BLOCKED/DELETED, optional expires_at, and created_at/updated_at
timestamps using TIMESTAMPTZ. The redirect hot path queries by short_code, so that
column must be indexed; the unique constraint already gives us that index in
PostgreSQL. I would avoid putting heavy analytics writes into this table because
redirect traffic can be much higher than create traffic. Click analytics should be
handled asynchronously later through Kafka or a separate click_events table. I would
manage schema changes with Flyway migrations and use Hibernate validate rather than
letting Hibernate update production schema automatically.
```

Why this answer is strong:

```text
1. Identifies source of truth.
2. Separates internal id and public short_code.
3. Enforces uniqueness at DB level.
4. Mentions hot-path indexing.
5. Handles status and expiry.
6. Avoids analytics write bottleneck.
7. Mentions schema migration discipline.
8. Shows production mindset.
```

Senior one-liner:

```text
The schema protects the mapping invariant and optimizes the redirect lookup path.
```

---

## 27. Senior Engineer Checklist

Before moving to the create API chapter, confirm:

```text
[ ] short_urls table exists
[ ] id is BIGSERIAL primary key
[ ] short_code is NOT NULL
[ ] short_code is UNIQUE
[ ] long_url is NOT NULL
[ ] is_custom_alias exists
[ ] status exists
[ ] status has allowed values
[ ] expires_at is nullable
[ ] created_at exists
[ ] updated_at exists
[ ] timestamps use TIMESTAMPTZ
[ ] expiry index exists if cleanup/query needed
[ ] redirect query uses short_code
[ ] short_code lookup uses unique index
[ ] duplicate generated code behavior is planned
[ ] duplicate custom alias behavior is planned
[ ] JPA entity uses EnumType.STRING
[ ] schema is managed by migration, not random Hibernate update
[ ] analytics is not tightly coupled to mapping table
```

If all are checked, the database foundation is ready.

---

## 28. One-Page Cheat Sheet

```text
Core table:
short_urls

Core mapping:
short_code -> long_url

Source of truth:
PostgreSQL

Speed layer later:
Redis

Analytics later:
Kafka / click_events

Primary key:
id BIGSERIAL

Business key:
short_code VARCHAR(32) UNIQUE NOT NULL

Target URL:
long_url TEXT NOT NULL

Custom alias:
is_custom_alias BOOLEAN NOT NULL DEFAULT false

Status:
ACTIVE
BLOCKED
DELETED

Expiry:
expires_at TIMESTAMPTZ NULL

Timestamps:
created_at TIMESTAMPTZ NOT NULL DEFAULT now()
updated_at TIMESTAMPTZ NOT NULL DEFAULT now()

Main redirect query:
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?;

Unknown:
404

Blocked:
403

Expired:
410

Active:
302

Most important constraint:
UNIQUE(short_code)

Most important index:
short_code unique index

Migration:
V1__create_short_urls_table.sql

Production rule:
Do not rely on Hibernate ddl-auto update in production.

Analytics rule:
Do not update mapping row on every redirect at high scale.

Senior phrase:
The schema protects correctness and keeps the hot redirect lookup fast.
```

---

## 29. One Picture To Remember

```text
                      POSTGRES SCHEMA MENTAL MODEL

                         "Truth lives here"

+------------------------------------------------------------------+
|                            short_urls                            |
+------------------------------------------------------------------+
| id             BIGSERIAL PRIMARY KEY                             |
| short_code     VARCHAR(32) NOT NULL UNIQUE   <--- hot lookup key  |
| long_url       TEXT NOT NULL                 <--- redirect target |
| is_custom_alias BOOLEAN NOT NULL DEFAULT false                    |
| status         ACTIVE / BLOCKED / DELETED                         |
| expires_at     TIMESTAMPTZ NULL              <--- time validity   |
| created_at     TIMESTAMPTZ NOT NULL                              |
| updated_at     TIMESTAMPTZ NOT NULL                              |
+------------------------------------------------------------------+

CREATE PATH:

App generates/accepts code
        |
        v
INSERT INTO short_urls
        |
        v
UNIQUE(short_code)?
        |
   +----+----+
   |         |
 success   duplicate
   |         |
   v         v
 return    retry generated / 409 custom alias


REDIRECT PATH:

GET /aB91xZ
        |
        v
Use unique index on short_code
        |
        v
Find row
        |
        v
+------------------------------+
| status? expiry?              |
+------------------------------+
        |
        +-- not found --> 404
        +-- blocked   --> 403
        +-- expired   --> 410
        +-- active    --> 302 Location: long_url


TRUTH VS SPEED:

+------------------+          derived/cache          +------------------+
| PostgreSQL       | ------------------------------> | Redis later      |
| source of truth  |                                 | speed layer      |
+------------------+                                 +------------------+

FINAL MEMORY:

The database schema is not just storage.
It is the correctness boundary for the whole URL shortener.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. PostgreSQL is the durable source of truth for shortCode -> longUrl.
2. short_code must have a database-level UNIQUE constraint.
3. Redirect lookup must be indexed because it is the hot path.
4. status and expires_at decide whether redirect is allowed.
5. Analytics should not overload the core mapping table at high scale.
```

After this chapter, the next natural step is:

```text
005_Create_Short_URL_API.md
```

Because now the durable schema exists, and we can build the write path that safely creates:

```text
longUrl -> shortCode
```
