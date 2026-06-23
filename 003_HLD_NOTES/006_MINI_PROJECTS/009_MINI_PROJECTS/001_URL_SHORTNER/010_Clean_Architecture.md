# 010_Clean_Architecture.md
# MiniURLShortener — Clean Architecture

> Core mental model: **Clean Architecture keeps business rules at the center and pushes frameworks, databases, HTTP, Redis, Kafka, and cloud details to the outside. Dependencies should point inward, so the core business logic does not depend on Spring Boot, PostgreSQL, Redis, Kafka, or any external tool.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. The Dependency Rule](#4-the-dependency-rule)
- [5. The Four Layer Mental Model](#5-the-four-layer-mental-model)
- [6. URL Shortener Without Clean Architecture](#6-url-shortener-without-clean-architecture)
- [7. URL Shortener With Clean Architecture](#7-url-shortener-with-clean-architecture)
- [8. Domain Layer](#8-domain-layer)
- [9. Application Layer / Use Cases](#9-application-layer--use-cases)
- [10. Ports](#10-ports)
- [11. Adapters](#11-adapters)
- [12. Infrastructure Layer](#12-infrastructure-layer)
- [13. Presentation Layer](#13-presentation-layer)
- [14. Recommended Package Structure](#14-recommended-package-structure)
- [15. Create Short URL Flow](#15-create-short-url-flow)
- [16. Redirect Flow](#16-redirect-flow)
- [17. Mapping DTOs, Domain Objects, and JPA Entities](#17-mapping-dtos-domain-objects-and-jpa-entities)
- [18. Clean Architecture Code Skeleton](#18-clean-architecture-code-skeleton)
- [19. Transaction Boundary](#19-transaction-boundary)
- [20. Why This Helps Redis Later](#20-why-this-helps-redis-later)
- [21. Why This Helps Kafka Later](#21-why-this-helps-kafka-later)
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

Phase 1 built the core backend:

```text
001 Problem Requirements
002 URL Shortener Mental Model
003 Spring Boot Project Setup
004 Postgres Schema Design
005 Create Short URL API
006 Redirect API
007 Base62 Encoding
008 ID Generation Strategies
009 Error Handling & Validation
```

Now Phase 2 starts: **Production Coding**.

The first production coding skill is architecture.

Without architecture, a Spring Boot project often becomes this:

```text
Controller
   |
   v
Service
   |
   v
Repository
   |
   v
JPA Entity
   |
   v
PostgreSQL
```

That looks clean at first.

But in real projects, logic slowly leaks everywhere:

```text
Controller validates business rules.
Service knows too much about JPA.
Repository contains business decisions.
Entity contains API response logic.
Redis code enters service directly.
Kafka producer is called from controller.
Transaction decisions are unclear.
Tests require full Spring context.
```

After some months, the project becomes hard to change.

Example future changes:

```text
Add Redis cache for redirect.
Add Kafka click analytics.
Add read replica.
Add sharding.
Add admin blocking.
Add API key authentication.
Change persistence from JPA to JDBC for hot path.
Add Testcontainers.
Add retry/circuit breaker.
```

If business logic is tightly coupled to Spring, JPA, and Postgres, every change becomes painful.

Clean Architecture solves this by asking one core question:

```text
What is the business rule, and what is only a tool?
```

For MiniURLShortener:

```text
Business rule:
    A short code maps to a long URL.
    Active non-expired links can redirect.
    Blocked links cannot redirect.
    Duplicate aliases are conflicts.

Tools:
    Spring Boot
    PostgreSQL
    Redis
    Kafka
    Docker
    Kubernetes
    AWS
```

The business should not depend on the tools.

---

## 2. The One Core Mental Model

Clean Architecture means:

```text
BUSINESS IN THE CENTER
TOOLS ON THE OUTSIDE
DEPENDENCIES POINT INWARD
```

ASCII:

```text
+---------------------------------------------------------+
| Frameworks / Drivers                                   |
| Spring Boot, PostgreSQL, Redis, Kafka, Web, Cloud        |
|                                                         |
|   +-------------------------------------------------+   |
|   | Interface Adapters                              |   |
|   | REST Controllers, JPA Repositories, Mappers      |   |
|   |                                                 |   |
|   |   +-----------------------------------------+   |   |
|   |   | Application Use Cases                   |   |   |
|   |   | CreateShortUrl, RedirectShortUrl         |   |   |
|   |   |                                         |   |   |
|   |   |   +---------------------------------+   |   |   |
|   |   |   | Domain Entities                 |   |   |   |
|   |   |   | ShortUrl business rules          |   |   |   |
|   |   |   +---------------------------------+   |   |   |
|   |   +-----------------------------------------+   |   |
|   +-------------------------------------------------+   |
+---------------------------------------------------------+
```

Dependency direction:

```text
Outer layer depends on inner layer.
Inner layer does not depend on outer layer.
```

Wrong direction:

```text
Domain -> Spring Boot
UseCase -> JpaRepository
BusinessRule -> RedisTemplate
```

Correct direction:

```text
Spring Controller -> UseCase
Postgres Adapter -> Repository Port
Redis Adapter -> Cache Port
Kafka Adapter -> Event Publisher Port
```

One-line memory:

```text
Clean Architecture makes frameworks replaceable and business rules stable.
```

---

## 3. Problem Statement

Refactor the MiniURLShortener mental model into a clean architecture shape.

We need to organize:

```text
1. Domain rules.
2. Use cases.
3. Repository ports.
4. Infrastructure adapters.
5. REST controllers.
6. DTOs and mappers.
7. Error boundaries.
8. Transaction boundaries.
9. Future Redis and Kafka extension points.
```

The architecture must support:

```text
Create short URL
Redirect short URL
Custom alias conflict
Generated code collision
Expired link check
Blocked link check
Future Redis cache
Future Kafka analytics
Future rate limiting
Future sharding
```

It should avoid:

```text
business logic in controllers
domain depending on JPA annotations
use cases depending on Spring MVC
use cases depending directly on RedisTemplate
use cases depending directly on KafkaTemplate
JPA entity leaking into API response
DTO becoming domain model
```

Goal:

```text
Build a codebase where the core business can be tested without starting Spring Boot.
```

---

## 4. The Dependency Rule

The most important rule:

```text
Source code dependencies must point inward.
```

That means:

```text
Domain should not import Spring.
Domain should not import JPA.
Domain should not import Redis.
Domain should not import Kafka.
Domain should not import HTTP DTOs.
```

Good domain import list:

```java
import java.time.Instant;
```

Bad domain import list:

```java
import jakarta.persistence.Entity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.jpa.repository.JpaRepository;
```

ASCII dependency direction:

```text
presentation  ----\
                   \
infrastructure -----> application -----> domain
                   /
config -----------/
```

The inner layer should be boring Java.

Why?

Because business rules live longer than tools.

```text
Spring version may change.
JPA may be replaced by JDBC.
Postgres may be sharded.
Redis may be added.
Kafka topic may change.
AWS may become GCP.
```

But this remains stable:

```text
An active non-expired short URL can redirect.
A blocked short URL cannot redirect.
A duplicate custom alias is a conflict.
```

---

## 5. The Four Layer Mental Model

Clean architecture can be understood as four layers.

```text
1. Domain
2. Application
3. Adapters
4. Infrastructure/Frameworks
```

ASCII:

```text
+------------------------------+
| 4. Infrastructure            |
| DB, Redis, Kafka, Spring     |
+------------------------------+
              |
              v
+------------------------------+
| 3. Adapters                  |
| Controllers, JPA adapters    |
+------------------------------+
              |
              v
+------------------------------+
| 2. Application               |
| Use cases                    |
+------------------------------+
              |
              v
+------------------------------+
| 1. Domain                    |
| Entities and business rules  |
+------------------------------+
```

Layer meanings:

```text
Domain:
    pure business concepts

Application:
    business workflows / use cases

Adapters:
    convert external world into application calls

Infrastructure:
    technical tools and framework details
```

MiniURLShortener example:

```text
Domain:
    ShortUrl, ShortUrlStatus

Application:
    CreateShortUrlUseCase, RedirectShortUrlUseCase

Ports:
    ShortUrlRepositoryPort, ShortCodeGeneratorPort

Adapters:
    UrlController, RedirectController, PostgresShortUrlRepositoryAdapter

Infrastructure:
    Spring Boot, Spring Data JPA, PostgreSQL, Redis, Kafka
```

---

## 6. URL Shortener Without Clean Architecture

Typical beginner structure:

```text
controller
service
repository
entity
dto
```

This is not always bad. But it can become messy when responsibilities are unclear.

Bad flow:

```text
UrlController
   |
   | validates URL business rules
   | generates short code
   | checks alias
   v
ShortUrlRepository
   |
   v
Postgres
```

Bad controller:

```java
@PostMapping
public ResponseEntity<?> create(@RequestBody Request request) {
    if (!request.getLongUrl().startsWith("http")) {
        return badRequest();
    }

    String code = randomCode();
    ShortUrlEntity entity = new ShortUrlEntity();
    entity.setShortCode(code);
    entity.setLongUrl(request.getLongUrl());

    repository.save(entity);

    return ok(entity);
}
```

Problems:

```text
1. Controller has business rules.
2. Controller knows persistence.
3. Entity leaks to response.
4. Hard to unit test without Spring.
5. Hard to add Redis/Kafka cleanly.
6. Hard to reuse create logic outside HTTP.
```

ASCII pain:

```text
HTTP Controller
   |
   +-- validation
   +-- code generation
   +-- DB save
   +-- response building
   +-- error mapping

Too many jobs in one class.
```

Rule:

```text
When one class knows too much, change becomes expensive.
```

---

## 7. URL Shortener With Clean Architecture

Clean architecture separates responsibilities.

```text
Controller:
    HTTP input/output only

Use Case:
    application workflow

Domain:
    business rules

Repository Port:
    interface needed by use case

Postgres Adapter:
    actual DB implementation
```

Clean flow:

```text
POST /api/v1/urls
       |
       v
UrlController
       |
       v
CreateShortUrlUseCase
       |
       v
ShortCodeGeneratorPort
       |
       v
ShortUrlRepositoryPort
       |
       v
PostgresShortUrlRepositoryAdapter
       |
       v
Spring Data JPA / PostgreSQL
```

ASCII:

```text
+-------------------+
| HTTP Request      |
+-------------------+
          |
          v
+-------------------+
| Controller        |
| DTO -> Command    |
+-------------------+
          |
          v
+-------------------+
| Use Case          |
| business workflow |
+-------------------+
          |
          v
+-------------------+
| Repository Port   |
| interface         |
+-------------------+
          |
          v
+-------------------+
| Postgres Adapter  |
| JPA details       |
+-------------------+
          |
          v
+-------------------+
| Database          |
+-------------------+
```

The use case does not know Postgres exists.

It only knows:

```text
I need something that can save and find ShortUrl.
```

That "something" is a port.

---

## 8. Domain Layer

Domain is the core business.

For MiniURLShortener, domain includes:

```text
ShortUrl
ShortUrlStatus
ShortCode
LongUrl maybe later
```

Domain object:

```java
package com.miniurl.shortener.domain.model;

import java.time.Instant;

public class ShortUrl {

    private final Long id;
    private final String shortCode;
    private final String longUrl;
    private final boolean customAlias;
    private final ShortUrlStatus status;
    private final Instant expiresAt;
    private final Instant createdAt;

    public ShortUrl(
            Long id,
            String shortCode,
            String longUrl,
            boolean customAlias,
            ShortUrlStatus status,
            Instant expiresAt,
            Instant createdAt
    ) {
        this.id = id;
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.customAlias = customAlias;
        this.status = status;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public boolean canRedirectAt(Instant now) {
        return status == ShortUrlStatus.ACTIVE && !isExpiredAt(now);
    }

    public boolean isExpiredAt(Instant now) {
        return expiresAt != null && !expiresAt.isAfter(now);
    }

    public boolean isBlocked() {
        return status == ShortUrlStatus.BLOCKED;
    }

    public boolean isDeleted() {
        return status == ShortUrlStatus.DELETED;
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
}
```

Status enum:

```java
package com.miniurl.shortener.domain.model;

public enum ShortUrlStatus {
    ACTIVE,
    BLOCKED,
    DELETED
}
```

Important:

```text
No @Entity.
No @Table.
No @RestController.
No Spring annotations.
```

Domain should be pure.

Why?

```text
You can test it with plain JUnit.
You can reuse it if persistence changes.
You can reason about business rules without framework noise.
```

---

## 9. Application Layer / Use Cases

Application layer contains workflows.

For MiniURLShortener:

```text
CreateShortUrlUseCase
RedirectShortUrlUseCase
```

A use case answers:

```text
What does the system do for this user action?
```

Create use case:

```text
Input: CreateShortUrlCommand
Output: CreateShortUrlResult
```

Redirect use case:

```text
Input: shortCode
Output: longUrl
```

Use case should orchestrate:

```text
validation
generation
domain decisions
repository port calls
exception decisions
```

Use case should not know:

```text
HTTP
JSON
JPA
SQL
RedisTemplate
KafkaTemplate
```

ASCII:

```text
Controller DTO
    |
    v
Command
    |
    v
Use Case
    |
    v
Domain + Ports
    |
    v
Result
    |
    v
Controller Response DTO
```

Command example:

```java
package com.miniurl.shortener.application.command;

import java.time.Instant;

public class CreateShortUrlCommand {

    private final String longUrl;
    private final String customAlias;
    private final Instant expiresAt;

    public CreateShortUrlCommand(String longUrl, String customAlias, Instant expiresAt) {
        this.longUrl = longUrl;
        this.customAlias = customAlias;
        this.expiresAt = expiresAt;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
```

Result example:

```java
package com.miniurl.shortener.application.result;

import java.time.Instant;

public class CreateShortUrlResult {

    private final Long id;
    private final String shortCode;
    private final String shortUrl;
    private final String longUrl;
    private final Instant expiresAt;
    private final Instant createdAt;

    public CreateShortUrlResult(
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

---

## 10. Ports

A port is an interface that the application core needs.

It says:

```text
I need this capability.
I do not care who provides it.
```

Repository port:

```java
package com.miniurl.shortener.application.port;

import com.miniurl.shortener.domain.model.ShortUrl;

import java.util.Optional;

public interface ShortUrlRepositoryPort {

    ShortUrl save(ShortUrl shortUrl);

    Optional<ShortUrl> findByShortCode(String shortCode);
}
```

Short code generator port:

```java
package com.miniurl.shortener.application.port;

public interface ShortCodeGeneratorPort {

    String generate();
}
```

Clock port optional:

```java
package com.miniurl.shortener.application.port;

import java.time.Instant;

public interface ClockPort {

    Instant now();
}
```

Why use ports?

Because use cases can depend on interfaces.

```text
Use case -> ShortUrlRepositoryPort
```

Not:

```text
Use case -> JpaRepository
```

ASCII:

```text
Use Case
   |
   v
Repository Port
   |
   v
Implementation chosen outside
```

This allows:

```text
Postgres adapter today
In-memory fake for tests
Redis + Postgres adapter later
Sharded repository later
```

---

## 11. Adapters

An adapter implements a port using a real tool.

Examples:

```text
PostgresShortUrlRepositoryAdapter
RandomShortCodeGeneratorAdapter
SystemClockAdapter
KafkaClickEventPublisherAdapter later
RedisRedirectCacheAdapter later
```

Postgres adapter:

```java
package com.miniurl.shortener.infrastructure.persistence;

import com.miniurl.shortener.application.port.ShortUrlRepositoryPort;
import com.miniurl.shortener.domain.model.ShortUrl;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PostgresShortUrlRepositoryAdapter implements ShortUrlRepositoryPort {

    private final SpringDataShortUrlJpaRepository jpaRepository;
    private final ShortUrlPersistenceMapper mapper;

    public PostgresShortUrlRepositoryAdapter(
            SpringDataShortUrlJpaRepository jpaRepository,
            ShortUrlPersistenceMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ShortUrl save(ShortUrl shortUrl) {
        ShortUrlJpaEntity entity = mapper.toJpaEntity(shortUrl);
        ShortUrlJpaEntity saved = jpaRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ShortUrl> findByShortCode(String shortCode) {
        return jpaRepository.findByShortCode(shortCode)
                .map(mapper::toDomain);
    }
}
```

Spring Data repository stays infrastructure:

```java
package com.miniurl.shortener.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataShortUrlJpaRepository
        extends JpaRepository<ShortUrlJpaEntity, Long> {

    Optional<ShortUrlJpaEntity> findByShortCode(String shortCode);
}
```

Important:

```text
Use case does not know SpringDataShortUrlJpaRepository exists.
```

---

## 12. Infrastructure Layer

Infrastructure contains tool-specific code.

For MiniURLShortener:

```text
PostgreSQL persistence
JPA entities
Spring Data repositories
Redis cache later
Kafka producer later
configuration properties
clock implementation
random generator implementation
```

Infrastructure package:

```text
infrastructure
├── persistence
│   ├── ShortUrlJpaEntity.java
│   ├── SpringDataShortUrlJpaRepository.java
│   ├── PostgresShortUrlRepositoryAdapter.java
│   └── ShortUrlPersistenceMapper.java
│
├── generator
│   └── RandomBase62ShortCodeGenerator.java
│
├── time
│   └── SystemClockAdapter.java
│
├── redis
│   └── later
│
└── kafka
    └── later
```

JPA entity belongs here:

```java
package com.miniurl.shortener.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "short_urls")
public class ShortUrlJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", nullable = false, unique = true, length = 32)
    private String shortCode;

    @Column(name = "long_url", nullable = false, columnDefinition = "TEXT")
    private String longUrl;

    @Column(name = "is_custom_alias", nullable = false)
    private boolean customAlias;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ShortUrlJpaEntity() {
    }

    // getters/setters omitted for brevity
}
```

Clean rule:

```text
JPA entity is not the domain entity.
```

Why?

Because JPA entity is persistence shape.

Domain entity is business shape.

They may look similar now, but they will diverge later.

---

## 13. Presentation Layer

Presentation layer is HTTP/API.

It contains:

```text
REST controllers
request DTOs
response DTOs
HTTP-specific mappers
```

Package:

```text
presentation
├── controller
│   ├── UrlController.java
│   └── RedirectController.java
│
└── dto
    ├── CreateShortUrlRequest.java
    └── CreateShortUrlResponse.java
```

Controller example:

```java
package com.miniurl.shortener.presentation.controller;

import com.miniurl.shortener.application.command.CreateShortUrlCommand;
import com.miniurl.shortener.application.result.CreateShortUrlResult;
import com.miniurl.shortener.application.usecase.CreateShortUrlUseCase;
import com.miniurl.shortener.presentation.dto.CreateShortUrlRequest;
import com.miniurl.shortener.presentation.dto.CreateShortUrlResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private final CreateShortUrlUseCase createShortUrlUseCase;

    public UrlController(CreateShortUrlUseCase createShortUrlUseCase) {
        this.createShortUrlUseCase = createShortUrlUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateShortUrlResponse create(
            @Valid @RequestBody CreateShortUrlRequest request
    ) {
        CreateShortUrlCommand command = new CreateShortUrlCommand(
                request.getLongUrl(),
                request.getCustomAlias(),
                request.getExpiresAt()
        );

        CreateShortUrlResult result = createShortUrlUseCase.execute(command);

        return new CreateShortUrlResponse(
                result.getId(),
                result.getShortCode(),
                result.getShortUrl(),
                result.getLongUrl(),
                result.getExpiresAt(),
                result.getCreatedAt()
        );
    }
}
```

Controller knows:

```text
HTTP
DTO
status code
use case
```

Controller does not know:

```text
JPA
Postgres
Redis
Kafka
SQL
short code collision retry details
```

---

## 14. Recommended Package Structure

Recommended structure for MiniURLShortener:

```text
com.miniurl.shortener
│
├── domain
│   └── model
│       ├── ShortUrl.java
│       └── ShortUrlStatus.java
│
├── application
│   ├── command
│   │   └── CreateShortUrlCommand.java
│   ├── result
│   │   └── CreateShortUrlResult.java
│   ├── port
│   │   ├── ShortUrlRepositoryPort.java
│   │   ├── ShortCodeGeneratorPort.java
│   │   └── ClockPort.java
│   └── usecase
│       ├── CreateShortUrlUseCase.java
│       └── RedirectShortUrlUseCase.java
│
├── infrastructure
│   ├── persistence
│   │   ├── ShortUrlJpaEntity.java
│   │   ├── SpringDataShortUrlJpaRepository.java
│   │   ├── PostgresShortUrlRepositoryAdapter.java
│   │   └── ShortUrlPersistenceMapper.java
│   ├── generator
│   │   └── RandomBase62ShortCodeGenerator.java
│   └── time
│       └── SystemClockAdapter.java
│
├── presentation
│   ├── controller
│   │   ├── UrlController.java
│   │   └── RedirectController.java
│   └── dto
│       ├── CreateShortUrlRequest.java
│       └── CreateShortUrlResponse.java
│
└── common
    └── error
        ├── ApiException.java
        ├── ApiErrorResponse.java
        └── GlobalExceptionHandler.java
```

Mental map:

```text
domain:
    pure rules

application:
    use cases and ports

infrastructure:
    tools implementing ports

presentation:
    HTTP adapter

common:
    shared cross-cutting concerns
```

This structure is slightly more verbose than beginner Spring Boot, but it pays off when the system grows.

---

## 15. Create Short URL Flow

Clean flow:

```text
HTTP Request
    |
    v
CreateShortUrlRequest DTO
    |
    v
UrlController
    |
    v
CreateShortUrlCommand
    |
    v
CreateShortUrlUseCase
    |
    +-- ShortCodeGeneratorPort
    |
    +-- ShortUrlRepositoryPort
    |
    v
Postgres Adapter
    |
    v
PostgreSQL
```

ASCII:

```text
+-------------------------+
| POST /api/v1/urls       |
+-------------------------+
             |
             v
+-------------------------+
| UrlController           |
| DTO -> Command          |
+-------------------------+
             |
             v
+-------------------------+
| CreateShortUrlUseCase   |
| validate + generate     |
| save through port       |
+-------------------------+
             |
             v
+-------------------------+
| ShortUrlRepositoryPort  |
+-------------------------+
             |
             v
+------------------------------+
| Postgres Repository Adapter  |
| maps Domain <-> JPA          |
+------------------------------+
             |
             v
+-------------------------+
| PostgreSQL               |
+-------------------------+
```

Use case pseudo-code:

```java
public CreateShortUrlResult execute(CreateShortUrlCommand command) {
    validate(command);

    String code = chooseAliasOrGenerate(command);

    ShortUrl domain = new ShortUrl(
            null,
            code,
            command.getLongUrl(),
            command.hasCustomAlias(),
            ShortUrlStatus.ACTIVE,
            command.getExpiresAt(),
            clock.now()
    );

    ShortUrl saved = repository.save(domain);

    return toResult(saved);
}
```

The use case speaks business language.

It does not speak HTTP or SQL.

---

## 16. Redirect Flow

Clean redirect flow:

```text
GET /{shortCode}
    |
    v
RedirectController
    |
    v
RedirectShortUrlUseCase
    |
    v
ShortUrlRepositoryPort
    |
    v
Postgres Adapter
    |
    v
Domain ShortUrl
    |
    v
business decision:
    blocked?
    deleted?
    expired?
    active?
    |
    v
longUrl result
    |
    v
302 Location
```

ASCII:

```text
+---------------------+
| GET /aB91xZ         |
+---------------------+
          |
          v
+---------------------+
| RedirectController  |
+---------------------+
          |
          v
+--------------------------+
| RedirectShortUrlUseCase  |
+--------------------------+
          |
          v
+--------------------------+
| Repository Port          |
+--------------------------+
          |
          v
+--------------------------+
| Postgres Adapter         |
+--------------------------+
          |
          v
+--------------------------+
| ShortUrl Domain Object   |
+--------------------------+
          |
          v
+--------------------------+
| canRedirectAt(now)?      |
+--------------------------+
          |
          v
+--------------------------+
| return longUrl           |
+--------------------------+
```

Redirect use case:

```java
package com.miniurl.shortener.application.usecase;

import com.miniurl.shortener.application.port.ClockPort;
import com.miniurl.shortener.application.port.ShortUrlRepositoryPort;
import com.miniurl.shortener.domain.model.ShortUrl;
import com.miniurl.shortener.url.service.ShortCodeBlockedException;
import com.miniurl.shortener.url.service.ShortCodeExpiredException;
import com.miniurl.shortener.url.service.ShortCodeNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RedirectShortUrlUseCase {

    private final ShortUrlRepositoryPort repository;
    private final ClockPort clock;

    public RedirectShortUrlUseCase(
            ShortUrlRepositoryPort repository,
            ClockPort clock
    ) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public String execute(String shortCode) {
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));

        if (shortUrl.isBlocked()) {
            throw new ShortCodeBlockedException(shortCode);
        }

        if (shortUrl.isDeleted()) {
            throw new ShortCodeNotFoundException(shortCode);
        }

        Instant now = clock.now();

        if (shortUrl.isExpiredAt(now)) {
            throw new ShortCodeExpiredException(shortCode);
        }

        return shortUrl.getLongUrl();
    }
}
```

Notice:

```text
The use case depends on ClockPort, not Instant.now() directly.
```

Why?

```text
Tests can control time.
```

---

## 17. Mapping DTOs, Domain Objects, and JPA Entities

There are three models:

```text
DTO:
    API shape

Domain:
    business shape

JPA Entity:
    database shape
```

ASCII:

```text
HTTP JSON
   |
   v
DTO
   |
   v
Command
   |
   v
Domain Object
   |
   v
JPA Entity
   |
   v
Database Row
```

Why not use one object for all?

Because each has a different reason to change.

DTO changes when:

```text
API contract changes
frontend needs new field
versioning changes
```

Domain changes when:

```text
business rules change
```

JPA entity changes when:

```text
database schema changes
indexing/persistence changes
```

If one class does all three, every change risks breaking everything.

Mapper example:

```java
package com.miniurl.shortener.infrastructure.persistence;

import com.miniurl.shortener.domain.model.ShortUrl;
import com.miniurl.shortener.domain.model.ShortUrlStatus;
import org.springframework.stereotype.Component;

@Component
public class ShortUrlPersistenceMapper {

    public ShortUrl toDomain(ShortUrlJpaEntity entity) {
        return new ShortUrl(
                entity.getId(),
                entity.getShortCode(),
                entity.getLongUrl(),
                entity.isCustomAlias(),
                ShortUrlStatus.valueOf(entity.getStatus()),
                entity.getExpiresAt(),
                entity.getCreatedAt()
        );
    }

    public ShortUrlJpaEntity toJpaEntity(ShortUrl domain) {
        ShortUrlJpaEntity entity = new ShortUrlJpaEntity();
        entity.setId(domain.getId());
        entity.setShortCode(domain.getShortCode());
        entity.setLongUrl(domain.getLongUrl());
        entity.setCustomAlias(domain.isCustomAlias());
        entity.setStatus(domain.getStatus().name());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getCreatedAt());
        return entity;
    }
}
```

Production note:

```text
Mappers add code, but reduce coupling.
```

For small CRUD apps, this may feel heavy.
For production systems, it prevents long-term pain.

---

## 18. Clean Architecture Code Skeleton

Create use case:

```java
package com.miniurl.shortener.application.usecase;

import com.miniurl.shortener.application.command.CreateShortUrlCommand;
import com.miniurl.shortener.application.port.ClockPort;
import com.miniurl.shortener.application.port.ShortCodeGeneratorPort;
import com.miniurl.shortener.application.port.ShortUrlRepositoryPort;
import com.miniurl.shortener.application.result.CreateShortUrlResult;
import com.miniurl.shortener.domain.model.ShortUrl;
import com.miniurl.shortener.domain.model.ShortUrlStatus;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateShortUrlUseCase {

    private static final int MAX_RETRIES = 5;
    private static final String BASE_URL = "https://mini.ly/";

    private final ShortUrlRepositoryPort repository;
    private final ShortCodeGeneratorPort generator;
    private final ClockPort clock;

    public CreateShortUrlUseCase(
            ShortUrlRepositoryPort repository,
            ShortCodeGeneratorPort generator,
            ClockPort clock
    ) {
        this.repository = repository;
        this.generator = generator;
        this.clock = clock;
    }

    @Transactional
    public CreateShortUrlResult execute(CreateShortUrlCommand command) {
        if (command.getCustomAlias() != null && !command.getCustomAlias().isBlank()) {
            return createWithCustomAlias(command);
        }

        return createWithGeneratedCode(command);
    }

    private CreateShortUrlResult createWithGeneratedCode(CreateShortUrlCommand command) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            String code = generator.generate();

            try {
                ShortUrl saved = repository.save(new ShortUrl(
                        null,
                        code,
                        command.getLongUrl(),
                        false,
                        ShortUrlStatus.ACTIVE,
                        command.getExpiresAt(),
                        clock.now()
                ));

                return toResult(saved);
            } catch (DataIntegrityViolationException ex) {
                // generated collision, retry
            }
        }

        throw new IllegalStateException("Could not generate unique short code");
    }

    private CreateShortUrlResult createWithCustomAlias(CreateShortUrlCommand command) {
        ShortUrl saved = repository.save(new ShortUrl(
                null,
                command.getCustomAlias(),
                command.getLongUrl(),
                true,
                ShortUrlStatus.ACTIVE,
                command.getExpiresAt(),
                clock.now()
        ));

        return toResult(saved);
    }

    private CreateShortUrlResult toResult(ShortUrl saved) {
        return new CreateShortUrlResult(
                saved.getId(),
                saved.getShortCode(),
                BASE_URL + saved.getShortCode(),
                saved.getLongUrl(),
                saved.getExpiresAt(),
                saved.getCreatedAt()
        );
    }
}
```

Important refinement:

```text
In a perfect clean architecture, Spring's DataIntegrityViolationException should not leak into use case.
```

Better:

```text
Postgres adapter catches DB exception and throws a domain/application exception.
```

But this chapter teaches gradual transition.

Pragmatic rule:

```text
Start cleaner than basic layered architecture.
Then improve boundaries as project grows.
```

---

## 19. Transaction Boundary

In Clean Architecture, transaction boundary usually belongs around use case execution.

Why?

```text
A use case is one application-level unit of work.
```

Create short URL:

```text
validate
generate code
save mapping
return result
```

This should commit or fail as one unit.

Redirect:

```text
lookup mapping
check state
return long URL
```

Read-only transaction is enough.

ASCII:

```text
@Transactional
+--------------------------------+
| Use Case                       |
|                                |
| validate input                 |
| generate/choose shortCode      |
| save through repository port   |
| return result                  |
+--------------------------------+
        |
        v
commit / rollback
```

Transaction should not be controlled by:

```text
Controller
JPA entity
DTO
```

It can be implemented using Spring annotation on use case class because Spring is wiring the application.

Purist note:

```text
A stricter clean architecture may keep @Transactional outside use case using transaction adapter.
```

Practical Spring Boot note:

```text
Using @Transactional on application service/use case is common and acceptable.
```

---

## 20. Why This Helps Redis Later

Without clean architecture, you may add Redis directly inside controller or service:

```java
redisTemplate.opsForValue().get(shortCode);
repository.findByShortCode(shortCode);
```

This couples business to Redis.

Better:

```text
Use a port.
```

Cache port:

```java
package com.miniurl.shortener.application.port;

import java.util.Optional;

public interface RedirectCachePort {

    Optional<String> getLongUrl(String shortCode);

    void putLongUrl(String shortCode, String longUrl);
}
```

Use case later:

```text
check cache port
if hit -> return
if miss -> repository port
put cache
return
```

ASCII:

```text
RedirectUseCase
      |
      +--> RedirectCachePort
      |          |
      |          v
      |     Redis Adapter
      |
      +--> ShortUrlRepositoryPort
                 |
                 v
            Postgres Adapter
```

The use case knows:

```text
cache capability
repository capability
```

It does not know:

```text
RedisTemplate
Redis cluster
TTL syntax
serialization
```

Benefit:

```text
You can test redirect cache behavior with fake cache.
```

---

## 21. Why This Helps Kafka Later

Click analytics should not block redirect.

Later, use a port:

```java
package com.miniurl.shortener.application.port;

public interface ClickEventPublisherPort {

    void publishClick(String shortCode);
}
```

Kafka adapter:

```text
KafkaClickEventPublisherAdapter implements ClickEventPublisherPort
```

Redirect use case can publish asynchronously or best-effort through port.

ASCII:

```text
RedirectUseCase
      |
      +--> returns longUrl
      |
      +--> ClickEventPublisherPort
               |
               v
          Kafka Adapter
```

But hot path rule:

```text
Do not make redirect success depend on Kafka success.
```

Clean architecture benefit:

```text
Kafka can be added without changing controller or domain.
```

Bad design:

```text
RedirectController directly calls KafkaTemplate.
```

Good design:

```text
Use case calls ClickEventPublisherPort.
Kafka adapter handles Kafka details.
```

---

## 22. Testing Strategy

Clean Architecture makes testing easier.

### Domain test

No Spring needed.

```java
@Test
void activeNonExpiredUrlCanRedirect() {
    ShortUrl url = new ShortUrl(
            1L,
            "abc123",
            "https://example.com",
            false,
            ShortUrlStatus.ACTIVE,
            Instant.now().plusSeconds(3600),
            Instant.now()
    );

    assertThat(url.canRedirectAt(Instant.now())).isTrue();
}
```

### Use case test

Use fake repository.

```java
class InMemoryShortUrlRepository implements ShortUrlRepositoryPort {

    private final Map<String, ShortUrl> store = new HashMap<>();

    @Override
    public ShortUrl save(ShortUrl shortUrl) {
        store.put(shortUrl.getShortCode(), shortUrl);
        return shortUrl;
    }

    @Override
    public Optional<ShortUrl> findByShortCode(String shortCode) {
        return Optional.ofNullable(store.get(shortCode));
    }
}
```

Test:

```java
@Test
void shouldRedirectActiveUrl() {
    ShortUrlRepositoryPort repository = new InMemoryShortUrlRepository();
    ClockPort clock = () -> Instant.parse("2026-06-21T10:00:00Z");

    repository.save(new ShortUrl(
            1L,
            "abc123",
            "https://example.com",
            false,
            ShortUrlStatus.ACTIVE,
            null,
            clock.now()
    ));

    RedirectShortUrlUseCase useCase = new RedirectShortUrlUseCase(repository, clock);

    String longUrl = useCase.execute("abc123");

    assertThat(longUrl).isEqualTo("https://example.com");
}
```

### Adapter test

Use Spring/JPA/Testcontainers.

```text
Postgres adapter persists and loads correctly.
```

### Controller test

Use MockMvc.

```text
Controller maps HTTP to use case.
```

Testing pyramid:

```text
              +----------------------+
              | End-to-End           |
              | Full app             |
              +----------------------+
                       ^
                       |
              +----------------------+
              | Adapter tests        |
              | JPA/Testcontainers   |
              +----------------------+
                       ^
                       |
              +----------------------+
              | Use case tests       |
              | fake ports           |
              +----------------------+
                       ^
                       |
              +----------------------+
              | Domain tests         |
              | pure Java            |
              +----------------------+
```

Most tests should be fast.

---

## 23. Step-by-Step Dry Runs

### Dry Run 1: Create URL with Clean Architecture

Request:

```json
{
  "longUrl": "https://example.com",
  "customAlias": null
}
```

Flow:

```text
1. Controller receives JSON.
2. Controller maps DTO to CreateShortUrlCommand.
3. Controller calls CreateShortUrlUseCase.
4. Use case asks ShortCodeGeneratorPort for code.
5. Random generator adapter returns k9Lm2Q.
6. Use case creates ShortUrl domain object.
7. Use case calls ShortUrlRepositoryPort.save.
8. Postgres adapter maps domain to JPA entity.
9. Spring Data JPA saves entity.
10. Adapter maps saved JPA entity back to domain.
11. Use case maps domain to result.
12. Controller maps result to response DTO.
13. Client receives 201.
```

ASCII:

```text
DTO -> Command -> UseCase -> Domain -> Port -> Adapter -> DB
DB -> Adapter -> Domain -> Result -> DTO -> Client
```

---

### Dry Run 2: Redirect URL with Clean Architecture

Request:

```http
GET /abc123
```

Flow:

```text
1. Controller extracts shortCode.
2. Controller calls RedirectShortUrlUseCase.
3. Use case calls ShortUrlRepositoryPort.findByShortCode.
4. Postgres adapter queries JPA repository.
5. Adapter maps JPA entity to domain ShortUrl.
6. Use case checks blocked/deleted/expired.
7. Use case returns longUrl.
8. Controller returns 302 Location.
```

ASCII:

```text
GET /abc123
    |
    v
Controller
    |
    v
UseCase
    |
    v
Repository Port
    |
    v
Postgres Adapter
    |
    v
Domain ShortUrl
    |
    v
Business decision
    |
    v
302 Location
```

---

### Dry Run 3: Add Redis Later

Before Redis:

```text
RedirectUseCase -> RepositoryPort -> PostgresAdapter
```

After Redis:

```text
RedirectUseCase -> CachePort -> RedisAdapter
               -> RepositoryPort -> PostgresAdapter on miss
```

Controller unchanged.

Domain unchanged.

Postgres adapter mostly unchanged.

Only use case and new adapter change.

This is the payoff.

---

## 24. Internal Execution Walkthrough

Spring runtime wiring:

```text
1. Spring scans @Component/@Service/@Repository.
2. CreateShortUrlUseCase needs ShortUrlRepositoryPort.
3. PostgresShortUrlRepositoryAdapter implements ShortUrlRepositoryPort.
4. Spring injects adapter into use case.
5. Controller needs CreateShortUrlUseCase.
6. Spring injects use case into controller.
```

ASCII:

```text
Interface:
ShortUrlRepositoryPort
        ^
        |
Implementation:
PostgresShortUrlRepositoryAdapter
        |
        v
Injected into:
CreateShortUrlUseCase
        |
        v
Injected into:
UrlController
```

At compile time:

```text
Use case depends on interface.
```

At runtime:

```text
Spring provides implementation.
```

This is dependency inversion.

Key principle:

```text
High-level policy should not depend on low-level detail.
Low-level detail should implement interface required by high-level policy.
```

---

## 25. Production Failure Stories

### Failure Story 1: Controller owns business logic

A team puts create logic inside controller.

Later they add:

```text
admin dashboard
batch import
mobile API
internal API
```

Each path duplicates create logic.

One path validates alias.
Another forgets reserved words.
Another forgets expiry.

Result:

```text
inconsistent behavior
bugs
security holes
```

Fix:

```text
Move create workflow into CreateShortUrlUseCase.
All entry points call same use case.
```

Lesson:

```text
Business workflow should live outside transport layer.
```

---

### Failure Story 2: Use case directly depends on JpaRepository

Use case imports:

```java
import org.springframework.data.jpa.repository.JpaRepository;
```

Later hot redirect path needs JDBC projection for performance.

Refactor becomes painful.

Root cause:

```text
Application logic coupled to persistence technology.
```

Fix:

```text
Use ShortUrlRepositoryPort.
JPA adapter implements it.
Later JDBC adapter can replace hot path.
```

Lesson:

```text
Depend on capability, not technology.
```

---

### Failure Story 3: Domain entity is JPA entity

Domain has:

```java
@Entity
@Table
@Column
```

Business tests need JPA behavior.
Persistence concerns leak into business.
Lazy loading causes surprises.
Serialization accidentally exposes fields.

Fix:

```text
Separate domain model from JPA entity.
Map between them.
```

Lesson:

```text
Persistence shape and business shape are not always the same.
```

---

### Failure Story 4: Kafka added directly to redirect service

Redirect service calls:

```java
kafkaTemplate.send(...)
```

Kafka slows down.
Redirect latency increases.

Fix:

```text
Use ClickEventPublisherPort.
Make implementation async/best-effort.
Keep redirect response independent.
```

Lesson:

```text
Infrastructure side effects should not dominate business use case.
```

---

### Failure Story 5: Tests require full Spring context

Every test uses:

```java
@SpringBootTest
```

Test suite takes minutes.

Developers stop running tests.

Bugs increase.

Fix:

```text
Pure domain tests.
Use case tests with fake ports.
Spring tests only for adapters/controllers.
```

Lesson:

```text
Architecture affects test speed.
Test speed affects engineering behavior.
```

---

## 26. Debugging Mindset

When debugging Clean Architecture code, ask:

```text
Which layer owns this bug?
```

Layer diagnosis:

```text
HTTP status wrong:
    presentation/controller or exception mapping

Business decision wrong:
    use case or domain

DB save/load wrong:
    infrastructure adapter or mapper

Redis behavior wrong:
    cache adapter or cache use-case integration

Kafka event wrong:
    event publisher adapter or event mapping

Test hard to write:
    dependency direction may be wrong
```

ASCII debugging map:

```text
Symptom
   |
   v
Which boundary?
   |
   +-- API contract -> presentation
   +-- workflow     -> application
   +-- rule         -> domain
   +-- DB/cache     -> infrastructure
```

Useful question:

```text
Can I test this without Spring?
```

If answer is no for pure business logic, architecture may be too coupled.

Another question:

```text
Does this class import a framework it should not know?
```

Bad imports reveal bad boundaries.

---

## 27. Common Mistakes

### Mistake 1: Thinking Clean Architecture means many folders only

Wrong:

```text
I created domain/application/infrastructure folders, so it is clean.
```

Correct:

```text
Dependency direction and responsibility separation matter.
```

### Mistake 2: Domain imports JPA

Wrong:

```java
@Entity
public class ShortUrl { ... }
```

Correct:

```text
Domain ShortUrl is plain Java.
JPA entity lives in infrastructure.
```

### Mistake 3: Use case imports Spring MVC DTO

Wrong:

```java
public Result execute(CreateShortUrlRequest request)
```

Correct:

```java
public Result execute(CreateShortUrlCommand command)
```

### Mistake 4: Controller contains business rules

Wrong:

```text
Controller checks expiry, alias, status.
```

Correct:

```text
Use case/domain checks business rules.
```

### Mistake 5: Repository port returns JPA entity

Wrong:

```java
Optional<ShortUrlJpaEntity> findByShortCode(...)
```

Correct:

```java
Optional<ShortUrl> findByShortCode(...)
```

### Mistake 6: Overengineering tiny project

Clean Architecture has cost.

Do not create 50 interfaces for no reason.

Practical rule:

```text
Create ports around things likely to change:
database
cache
message broker
clock
ID generator
external services
```

### Mistake 7: No mapper discipline

Wrong:

```text
DTO = Domain = JPA Entity
```

Correct:

```text
DTO maps to Command.
JPA maps to Domain.
Domain maps to Result.
```

---

## 28. Interview-Ready Explanation

If interviewer asks:

```text
How would you structure this URL shortener codebase?
```

Strong answer:

```text
I would keep the business logic independent from Spring and infrastructure. The
domain layer contains core concepts like ShortUrl and rules such as whether a link
can redirect based on status and expiry. The application layer contains use cases
like CreateShortUrlUseCase and RedirectShortUrlUseCase. These use cases depend on
ports such as ShortUrlRepositoryPort, ShortCodeGeneratorPort, and later
ClickEventPublisherPort, not directly on JPA, Redis, or Kafka. The infrastructure
layer implements those ports using PostgreSQL, Redis, Kafka, or any other tool. The
presentation layer contains REST controllers and DTOs that translate HTTP requests
into use-case commands and use-case results into HTTP responses. This keeps
dependencies pointing inward, makes the core easy to unit test, and allows future
changes like adding Redis cache or Kafka analytics without rewriting the business
logic.
```

Why this is strong:

```text
1. Explains dependency direction.
2. Separates domain, application, infrastructure, presentation.
3. Mentions use cases.
4. Mentions ports and adapters.
5. Connects to Redis/Kafka future.
6. Explains testing benefit.
7. Shows production maintainability.
8. Avoids framework-driven design.
```

Senior one-liner:

```text
Clean Architecture lets my business rules stay stable while infrastructure choices evolve.
```

---

## 29. Senior Engineer Checklist

Before moving to `011_Controller_Service_Repository.md`, confirm:

```text
[ ] You can explain dependency rule.
[ ] Domain has no Spring/JPA imports.
[ ] Use cases own workflows.
[ ] Controllers stay thin.
[ ] DTOs do not leak into use cases.
[ ] JPA entities do not leak into domain.
[ ] Repository port exists.
[ ] Postgres adapter implements repository port.
[ ] Generator port exists.
[ ] Clock port exists or is understood.
[ ] Mappers exist between DTO/command/domain/JPA/result.
[ ] Business rules are testable without Spring.
[ ] Redis can be added through a cache port.
[ ] Kafka can be added through an event publisher port.
[ ] Tests can use fake repositories.
[ ] You know where transaction boundary belongs.
```

If these are checked, your codebase is production-shaped.

---

## 30. One-Page Cheat Sheet

```text
Clean Architecture:
business center, tools outside

Main rule:
dependencies point inward

Layers:
Domain
Application
Adapters
Infrastructure

Domain:
ShortUrl
ShortUrlStatus
business rules
no Spring
no JPA

Application:
CreateShortUrlUseCase
RedirectShortUrlUseCase
ports
commands
results

Ports:
ShortUrlRepositoryPort
ShortCodeGeneratorPort
ClockPort
RedirectCachePort later
ClickEventPublisherPort later

Adapters:
PostgresShortUrlRepositoryAdapter
RandomBase62ShortCodeGenerator
SystemClockAdapter
Redis adapter later
Kafka adapter later

Presentation:
REST controllers
request DTOs
response DTOs

Infrastructure:
JPA entity
Spring Data repository
Postgres
Redis
Kafka

DTO != Domain != JPA Entity

Controller:
HTTP only

Use Case:
workflow

Domain:
business rule

Repository Adapter:
DB details

Testing:
domain tests = pure Java
use case tests = fake ports
adapter tests = Testcontainers
controller tests = MockMvc

Golden rule:
Business logic should not depend on Spring Boot.
```

---

## 31. One Picture To Remember

```text
                 MINIURLSHORTENER CLEAN ARCHITECTURE

                         Dependencies point inward

+-------------------------------------------------------------------+
| INFRASTRUCTURE / FRAMEWORKS                                       |
| Spring Boot, PostgreSQL, Redis, Kafka, Docker, Kubernetes          |
|                                                                   |
|   +-----------------------------------------------------------+   |
|   | ADAPTERS                                                  |   |
|   | REST Controllers, JPA Adapters, Redis Adapters, Mappers    |   |
|   |                                                           |   |
|   |   +---------------------------------------------------+   |   |
|   |   | APPLICATION USE CASES                            |   |   |
|   |   | CreateShortUrlUseCase                            |   |   |
|   |   | RedirectShortUrlUseCase                          |   |   |
|   |   | Ports: Repository, Generator, Cache, Publisher    |   |   |
|   |   |                                                   |   |   |
|   |   |   +-------------------------------------------+   |   |   |
|   |   |   | DOMAIN                                    |   |   |   |
|   |   |   | ShortUrl                                  |   |   |   |
|   |   |   | ShortUrlStatus                            |   |   |   |
|   |   |   | canRedirectAt(now)                         |   |   |   |
|   |   |   +-------------------------------------------+   |   |   |
|   |   +---------------------------------------------------+   |   |
|   +-----------------------------------------------------------+   |
+-------------------------------------------------------------------+

CREATE FLOW:

HTTP DTO
   |
   v
Controller
   |
   v
CreateShortUrlCommand
   |
   v
CreateShortUrlUseCase
   |
   v
Repository Port
   |
   v
Postgres Adapter
   |
   v
Database


REDIRECT FLOW:

GET /code
   |
   v
Controller
   |
   v
RedirectShortUrlUseCase
   |
   v
Repository Port
   |
   v
Domain ShortUrl
   |
   v
canRedirectAt(now)?
   |
   v
302 Location


FINAL MEMORY:

Business rules live in the center.
Spring, Postgres, Redis, Kafka are replaceable tools.
Dependencies always point inward.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Clean Architecture keeps business rules independent from frameworks.
2. Dependencies must point inward toward domain and use cases.
3. Controllers are adapters; they should not contain business workflows.
4. Use cases depend on ports, not directly on Postgres, Redis, Kafka, or HTTP.
5. Infrastructure adapters implement ports and can be replaced without rewriting business logic.
```

After this chapter, continue with:

```text
011_Controller_Service_Repository.md
```

Because now you understand the ideal architecture, and next you will deeply understand the common Spring Boot Controller-Service-Repository model and how to use it without creating a messy codebase.
