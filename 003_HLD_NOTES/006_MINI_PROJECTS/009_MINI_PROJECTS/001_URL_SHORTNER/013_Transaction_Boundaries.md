# 013_Transaction_Boundaries.md
# MiniURLShortener — Transaction Boundaries

> Core mental model: **A transaction boundary is the box around a unit of work that must succeed together or fail together. In Spring Boot, that boundary usually belongs at the service layer, not the controller and not the repository.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What Is A Transaction Boundary?](#4-what-is-a-transaction-boundary)
- [5. Why Service Layer Owns Transactions](#5-why-service-layer-owns-transactions)
- [6. Controller vs Service vs Repository Boundary](#6-controller-vs-service-vs-repository-boundary)
- [7. MiniURLShortener Transaction Use Cases](#7-miniurlshortener-transaction-use-cases)
- [8. Create Short URL Transaction Flow](#8-create-short-url-transaction-flow)
- [9. Redirect API Transaction Decision](#9-redirect-api-transaction-decision)
- [10. Read-Only Transactions](#10-read-only-transactions)
- [11. Rollback Mental Model](#11-rollback-mental-model)
- [12. Checked vs Runtime Exception Rollback](#12-checked-vs-runtime-exception-rollback)
- [13. JPA Persistence Context Inside Transaction](#13-jpa-persistence-context-inside-transaction)
- [14. Flush vs Commit](#14-flush-vs-commit)
- [15. Transaction Propagation](#15-transaction-propagation)
- [16. Self-Invocation Problem](#16-self-invocation-problem)
- [17. Isolation Level Mental Model](#17-isolation-level-mental-model)
- [18. Concurrency Problem: Duplicate Alias](#18-concurrency-problem-duplicate-alias)
- [19. Transaction Boundary With Kafka / Outbox](#19-transaction-boundary-with-kafka--outbox)
- [20. Correct Code Structure](#20-correct-code-structure)
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

In MiniURLShortener, some operations are simple reads, but some operations must be treated as one atomic unit.

Example create API:

```text
POST /api/v1/urls
```

A successful create may need to:

```text
1. Validate long URL.
2. Decide short code.
3. Insert short_urls row.
4. Insert analytics seed row later.
5. Insert outbox event later.
6. Return created response.
```

If the database insert succeeds but the event insert fails, should the API still return success?

If alias availability check succeeds but another request inserts the same alias before commit, what should happen?

If an exception happens after saving the entity but before method returns, should the row remain in the database?

These questions are transaction boundary questions.

Bad mental model:

```text
@Transactional means save to database.
```

Better mental model:

```text
@Transactional defines the success/failure box for a business operation.
Everything inside the box commits together or rolls back together.
```

For high-scale backend interviews, transaction boundaries show whether you understand:

```text
atomicity
consistency
rollback
JPA persistence context
concurrency
service-layer design
outbox consistency
failure recovery
```

This chapter teaches transaction boundaries in the exact shape needed for MiniURLShortener.

---

## 2. The One Core Mental Model

A transaction boundary is a box.

Inside the box:

```text
All database changes belong to one unit of work.
```

At the end of the box:

```text
If everything is OK -> COMMIT
If failure happens -> ROLLBACK
```

ASCII:

```text
                 TRANSACTION BOUNDARY
        +--------------------------------------+
        | validate business rule               |
        | create entity                        |
        | repository.save(entity)              |
        | maybe insert outbox event later      |
        +--------------------------------------+
                         |
              success? --+-- yes --> COMMIT
                         |
                         +-- no  --> ROLLBACK
```

One-line memory:

```text
A transaction boundary says: this business operation is all-or-nothing.
```

For MiniURLShortener:

```text
Create short URL:
    should be transactional

Redirect lookup:
    usually read-only, not a write transaction unless recording click synchronously

Click analytics:
    better async later, not inside redirect critical path

Outbox insert:
    must be inside same transaction as URL create
```

---

## 3. Problem Statement

Design transaction boundaries for MiniURLShortener.

We need to decide:

```text
1. Where to place @Transactional.
2. Which methods need read-write transactions.
3. Which methods need read-only transactions.
4. What should rollback.
5. What should not be inside transaction.
6. How JPA persistence context behaves inside transaction.
7. How to avoid duplicate custom alias race.
8. How to avoid transaction + Kafka inconsistency later.
9. How to test rollback behavior.
```

Out of scope for this chapter:

```text
1. Full distributed transaction implementation.
2. Kafka exactly-once deep dive.
3. Multi-database transactions.
4. XA / two-phase commit.
5. Full outbox worker implementation.
```

This chapter focuses on Spring Boot + JPA + PostgreSQL transaction boundaries.

---

## 4. What Is A Transaction Boundary?

A transaction boundary has a start and an end.

```text
start transaction
    do database work
commit or rollback
end transaction
```

In Spring Boot, `@Transactional` usually creates this boundary.

Example:

```java
@Transactional
public CreateShortUrlResponse createShortUrl(CreateShortUrlCommand command) {
    ShortUrl entity = new ShortUrl(...);
    ShortUrl saved = repository.save(entity);
    return mapper.toResponse(saved);
}
```

Spring roughly does this behind the scenes:

```text
open database connection
set autoCommit = false
start transaction
call your service method
if success -> commit
if RuntimeException -> rollback
close/release connection
```

ASCII:

```text
Client
  |
  v
Controller
  |
  v
Transactional Proxy
  |
  +-- begin transaction
  |
  v
Service Method
  |
  v
Repository / EntityManager
  |
  v
Database
  |
  v
Proxy
  |
  +-- commit or rollback
```

Important:

```text
The annotation is not magic inside the method itself.
Spring wraps the method call using a proxy.
```

That proxy concept explains many transaction bugs.

---

## 5. Why Service Layer Owns Transactions

The service layer knows the business unit of work.

Controller knows HTTP.
Repository knows one table/entity.
Service knows the full use case.

ASCII:

```text
Controller:
    HTTP request/response boundary

Service:
    business transaction boundary

Repository:
    database access boundary
```

Why not controller?

```text
Controller should not control database unit of work.
It should translate HTTP to application command and response.
```

Why not repository only?

```text
One business operation may call multiple repositories.
If each repository has separate transaction, partial commits can happen.
```

Bad example:

```text
repository.saveShortUrl() commits
repository.saveOutboxEvent() fails

Result:
short URL exists but event missing
```

Correct example:

```text
service createShortUrl() starts transaction
    repository.saveShortUrl()
    repository.saveOutboxEvent()
commit together
```

ASCII:

```text
BAD: repository-level independent commits

Service
  |
  +--> Repo A transaction -> COMMIT
  |
  +--> Repo B transaction -> FAIL

Partial state possible.

GOOD: service-level transaction

Service Transaction
+------------------------------+
| Repo A save                  |
| Repo B save                  |
+------------------------------+
        |
        +-- all ok -> COMMIT
        +-- any fail -> ROLLBACK ALL
```

Rule:

```text
Put @Transactional on service methods that represent business use cases.
```

---

## 6. Controller vs Service vs Repository Boundary

Each layer has its own boundary.

```text
+----------------+--------------------------------------+
| Layer          | Boundary Type                        |
+----------------+--------------------------------------+
| Controller     | HTTP/API boundary                    |
| Service        | business + transaction boundary      |
| Repository     | persistence operation boundary       |
+----------------+--------------------------------------+
```

Controller responsibility:

```text
parse request
validate DTO using @Valid
call service
return HTTP response
```

Service responsibility:

```text
business validation
transaction boundary
orchestration
entity creation/update
exception mapping
```

Repository responsibility:

```text
find by short code
save entity
exists by short code
custom query
```

ASCII:

```text
HTTP Request
    |
    v
+------------------+
| Controller       |  No business transaction thinking
+------------------+
    |
    v
+------------------+
| Service          |  @Transactional lives here
+------------------+
    |
    v
+------------------+
| Repository       |  DB operations
+------------------+
    |
    v
+------------------+
| Database         |  ACID guarantees
+------------------+
```

This separation keeps your code interview-clean and production-debuggable.

---

## 7. MiniURLShortener Transaction Use Cases

Transaction decisions:

```text
+-------------------------------+----------------------+--------------------------+
| Use Case                      | Transaction?         | Why                      |
+-------------------------------+----------------------+--------------------------+
| Create short URL              | yes, read-write      | insert must be atomic    |
| Redirect lookup only          | read-only optional   | only read long URL       |
| Redirect + sync click update  | yes, but avoid later | adds latency/contention  |
| Admin block URL               | yes, read-write      | update status atomically |
| Delete URL                    | yes, read-write      | update status/deleted_at |
| Create URL + outbox event     | yes, same tx         | consistency with event   |
| Bulk cleanup expired URLs     | yes, batched         | controlled updates       |
+-------------------------------+----------------------+--------------------------+
```

Create API:

```text
Must be transactional because a new row is created.
```

Redirect API:

```text
Usually should be fast and mostly read-only.
Do not put heavy analytics writes directly in redirect transaction.
```

Why?

```text
Redirect is your hottest path.
Every millisecond matters.
Database writes on every redirect reduce scalability.
```

Better future design:

```text
GET /{shortCode}
    read URL from Redis/DB
    return 302 quickly
    publish click event async
```

---

## 8. Create Short URL Transaction Flow

Create short URL should be one transaction.

Flow:

```text
POST /api/v1/urls
    |
    v
Controller validates DTO
    |
    v
Service transaction starts
    |
    v
validate business rules
    |
    v
generate or accept short code
    |
    v
save short_urls row
    |
    v
commit
    |
    v
return 201 Created
```

ASCII:

```text
                 CREATE SHORT URL TRANSACTION

Controller
   |
   v
Service Proxy
   |
   +-- BEGIN TX -----------------------------------+
   |                                              |
   |  validate URL                                |
   |  validate alias                              |
   |  generate code                               |
   |  repository.save(shortUrl)                   |
   |  flush/commit later                          |
   |                                              |
   +-- COMMIT -------------------------------------+
   |
   v
Response 201
```

If duplicate alias happens:

```text
BEGIN
    insert short_code = customAlias
    DB unique violation
ROLLBACK
return 409
```

Important:

```text
The transaction should not include slow external calls.
```

Do not do this inside create transaction:

```text
call remote malware scanner synchronously
send email
call Kafka broker directly and depend on it
call payment API
long network calls
```

Reason:

```text
Long transactions hold DB connection longer.
They increase lock duration.
They reduce throughput.
```

---

## 9. Redirect API Transaction Decision

Redirect is read-heavy.

```text
GET /abc123
```

Flow:

```text
1. Validate short code format.
2. Find active URL.
3. Check blocked/deleted/expired.
4. Return 302 Location.
```

Should this be transactional?

Option A: no explicit transaction

```text
Good for simple read.
Repository method uses short DB interaction.
```

Option B: `@Transactional(readOnly = true)`

```text
Good when using JPA lazy fields or multiple consistent reads.
Can optimize dirty checking.
```

Option C: read-write transaction for analytics update

```text
Avoid for hot redirect path if possible.
```

ASCII:

```text
Redirect Hot Path

Client
  |
  v
GET /abc123
  |
  v
Read short URL ---------------> return 302 quickly
  |
  +-- async click event later -> Kafka/queue/worker
```

Bad hot path:

```text
GET /abc123
  |
  v
BEGIN TX
  read URL
  update click_count = click_count + 1
  insert click row
  COMMIT
  return 302
```

Why bad at high RPS?

```text
more DB writes
row contention
higher latency
more deadlock risk
harder scaling
```

For phase 1, redirect can be simple read-only.
For high-scale phase, move analytics to Kafka.

---

## 10. Read-Only Transactions

Spring supports:

```java
@Transactional(readOnly = true)
```

Meaning:

```text
This method is intended only for reading.
```

Benefits:

```text
1. Communicates intent.
2. Can reduce JPA dirty checking overhead.
3. Helps some databases/drivers optimize.
4. Prevents accidental write in some setups, but do not rely on it as full security.
```

Example:

```java
@Transactional(readOnly = true)
public RedirectResult resolveRedirect(String shortCode) {
    ShortUrl shortUrl = repository.findByShortCode(shortCode)
            .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));

    validateRedirectState(shortUrl);

    return new RedirectResult(shortUrl.getLongUrl());
}
```

Mental model:

```text
readOnly = true means: I promise this use case should not change data.
```

But remember:

```text
It is not a replacement for database permissions.
It is not a magic guarantee in every database.
```

Use it for query service methods.

---

## 11. Rollback Mental Model

Rollback means:

```text
All uncommitted database changes inside this transaction are undone.
```

ASCII:

```text
BEGIN TX
   |
   +-- insert short_url row
   |
   +-- insert outbox event
   |
   +-- exception happens
   v
ROLLBACK
   |
   v
Database looks like transaction never happened
```

Rollback does not undo everything in the universe.

It does not automatically undo:

```text
email already sent
Kafka message already published outside transaction
HTTP call already made to another service
file uploaded to S3
log line already written
```

Very important production rule:

```text
Only database changes on the same transaction/connection rollback together.
External side effects do not rollback automatically.
```

Bad flow:

```text
BEGIN TX
save short_url
send Kafka event
throw exception
ROLLBACK DB

Kafka event still exists.
Consumer sees URL_CREATED for URL that does not exist.
```

Correct future flow:

```text
BEGIN TX
save short_url
save outbox_event in same DB
COMMIT
outbox worker publishes Kafka later
```

This is why transaction boundaries matter for distributed systems.

---

## 12. Checked vs Runtime Exception Rollback

By default, Spring rolls back on unchecked exceptions.

Unchecked exceptions:

```text
RuntimeException
Error
```

Checked exceptions:

```text
Exception but not RuntimeException
IOException
SQLException if thrown directly as checked
```

Default behavior:

```text
RuntimeException -> rollback
Checked Exception -> commit unless configured
```

Example:

```java
@Transactional
public void create() throws IOException {
    repository.save(entity);
    throw new IOException("file problem");
}
```

By default:

```text
Transaction may commit because IOException is checked.
```

To rollback for checked exceptions:

```java
@Transactional(rollbackFor = Exception.class)
public void create() throws Exception {
    repository.save(entity);
    throw new Exception("fail");
}
```

For MiniURLShortener, most domain exceptions extend `RuntimeException` through `ApiException`.

Good:

```java
public abstract class ApiException extends RuntimeException {
    // ...
}
```

Why good?

```text
Known domain failures can trigger rollback naturally when needed.
```

But be careful:

```text
Not every domain exception means data was changed.
For validation, usually exception happens before write.
```

---

## 13. JPA Persistence Context Inside Transaction

Inside a transaction, JPA uses a persistence context.

Mental model:

```text
Persistence Context = first-level memory map of managed entities inside a transaction.
```

ASCII:

```text
@Transactional method
+------------------------------------------------+
| Persistence Context                            |
|                                                |
|  id=10 -> ShortUrl managed object              |
|  id=11 -> ShortUrl managed object              |
|                                                |
+------------------------------------------------+
        |
        v
      Database
```

When you load an entity:

```java
ShortUrl url = repository.findByShortCode("abc123").orElseThrow();
```

JPA puts it into persistence context.

When you modify it:

```java
url.block();
```

You may not need explicit save.

At flush/commit, JPA detects changes and sends SQL update.

```text
load entity -> managed
change field -> dirty
commit -> update SQL
```

ASCII:

```text
DB row
  |
  v
Entity loaded into Persistence Context
  |
  v
Java object changed
  |
  v
Dirty checking
  |
  v
UPDATE sent before commit
```

This is powerful but can surprise beginners.

Rule:

```text
Inside transaction, changing a managed entity can update the database even without repository.save().
```

---

## 14. Flush vs Commit

Flush and commit are different.

Flush:

```text
Send pending SQL statements to database, but transaction is still not final.
```

Commit:

```text
Make transaction changes permanent.
```

ASCII:

```text
Java entity change
    |
    v
Persistence Context dirty
    |
    v
FLUSH
    |
    v
SQL sent to DB, locks/constraints may happen
    |
    v
COMMIT
    |
    v
Changes permanent
```

Example:

```java
@Transactional
public void create() {
    repository.save(shortUrl);
    entityManager.flush();
    throw new RuntimeException("fail after flush");
}
```

Result:

```text
SQL insert may be sent.
But transaction rolls back.
Row does not remain after rollback.
```

Important:

```text
Flush is not final persistence.
Commit is final persistence.
```

Why flush matters?

```text
1. Constraint violations may appear at flush time.
2. SQL may be delayed until commit.
3. Debugging insert/update timing requires understanding flush.
```

For duplicate alias:

```text
UNIQUE violation may happen during save, flush, or commit depending on provider timing.
```

So catch conflict at a reliable boundary and map it properly.

---

## 15. Transaction Propagation

Propagation decides what happens when one transactional method calls another.

Most common:

```java
@Transactional(propagation = Propagation.REQUIRED)
```

`REQUIRED` means:

```text
Use existing transaction if one exists.
Otherwise start a new one.
```

ASCII:

```text
Service A @Transactional
+--------------------------------+
| methodA starts TX              |
|                                |
| calls methodB REQUIRED         |
| methodB joins same TX          |
|                                |
+--------------------------------+
```

Other common propagation:

```text
REQUIRES_NEW:
    suspend current transaction and start a new independent one

MANDATORY:
    must already have transaction

SUPPORTS:
    join transaction if exists, otherwise run without
```

MiniURLShortener phase 1 should mostly use default `REQUIRED`.

Use `REQUIRES_NEW` carefully.

Bad use:

```text
createShortUrl transaction
    auditLog with REQUIRES_NEW commits
    createShortUrl fails and rolls back

Result:
audit says created, but URL does not exist
```

Sometimes that is desired for audit.
Often it is not.

Senior rule:

```text
Do not use REQUIRES_NEW unless you intentionally want independent commit behavior.
```

---

## 16. Self-Invocation Problem

Spring transactions work through proxies.

That means this works:

```text
Controller -> Service proxy -> @Transactional method
```

This may not work:

```text
Service method A -> this.methodB()
```

Because internal self-call bypasses proxy.

ASCII:

```text
WORKS:

Controller
   |
   v
Service Proxy
   |
   +-- starts transaction
   v
Real Service Method

DOES NOT APPLY NEW TRANSACTION:

Real Service Object
   |
   v
this.transactionalMethod()
   |
   v
No proxy crossing
```

Example:

```java
@Service
public class UrlService {

    public void outer() {
        innerTransactional(); // self-invocation
    }

    @Transactional
    public void innerTransactional() {
        repository.save(...);
    }
}
```

If `outer()` is called from controller and `outer()` is not transactional, then `innerTransactional()` annotation may not activate.

Correct options:

```text
1. Put @Transactional on public service method called from outside.
2. Move inner transactional method to another bean.
3. Avoid designing transaction boundaries around internal self calls.
```

For MiniURLShortener:

```java
@Transactional
public CreateShortUrlResponse createShortUrl(...) {
    // full use case here
}
```

Keep it simple.

---

## 17. Isolation Level Mental Model

Isolation controls how much one transaction can see another transaction's uncommitted or changing data.

Common isolation levels:

```text
READ_UNCOMMITTED
READ_COMMITTED
REPEATABLE_READ
SERIALIZABLE
```

PostgreSQL default:

```text
READ COMMITTED
```

Mental model:

```text
Higher isolation = stronger consistency but potentially lower concurrency.
```

ASCII:

```text
Low Isolation                         High Isolation
more concurrency                      more consistency
less blocking                         more blocking/retries
     |                                      |
     v                                      v
READ COMMITTED  -> REPEATABLE READ -> SERIALIZABLE
```

For MiniURLShortener, do not start by changing isolation globally.

Use:

```text
1. DB unique constraints for duplicate short_code.
2. Proper exception mapping for conflict.
3. Optimistic/pessimistic locking only when needed later.
```

Duplicate alias problem is better solved with:

```sql
UNIQUE(short_code)
```

Not with:

```text
manual exists check only
higher isolation everywhere
```

Senior answer:

```text
I rely on database constraints for uniqueness under concurrency, then translate unique violations into 409 Conflict.
```

---

## 18. Concurrency Problem: Duplicate Alias

Two users request same alias at same time.

Request A:

```json
{ "longUrl": "https://a.com", "customAlias": "sale" }
```

Request B:

```json
{ "longUrl": "https://b.com", "customAlias": "sale" }
```

Bad logic:

```java
if (!repository.existsByShortCode(alias)) {
    repository.save(new ShortUrl(alias));
}
```

Race:

```text
Time  Request A                    Request B
----  ---------------------------  ---------------------------
T1    existsByShortCode(sale)=no
T2                                 existsByShortCode(sale)=no
T3    insert sale
T4                                 insert sale
```

If no DB unique constraint:

```text
data corruption
```

If DB unique exists:

```text
one insert succeeds
one insert fails safely
```

ASCII:

```text
              DB UNIQUE(short_code)

Request A ---- insert sale ---- COMMIT success

Request B ---- insert sale ---- UNIQUE violation
                                 |
                                 v
                           409 ALIAS_ALREADY_EXISTS
```

Correct mindset:

```text
Application check improves user message.
Database constraint guarantees truth.
```

For custom alias:

```text
exists check optional
unique constraint mandatory
```

For generated code collision:

```text
catch unique violation
retry generation a few times
then fail with generation error
```

---

## 19. Transaction Boundary With Kafka / Outbox

Later MiniURLShortener will emit events:

```text
URL_CREATED
URL_REDIRECTED
URL_BLOCKED
```

Wrong approach:

```text
BEGIN TX
save short_url
publish Kafka URL_CREATED
COMMIT
```

Why wrong?

Failure combinations:

```text
1. Kafka publish succeeds, DB commit fails.
2. DB commit succeeds, Kafka publish fails.
3. App crashes between DB save and Kafka publish.
```

ASCII bad:

```text
DB transaction                  Kafka
+-------------------+           +------------------+
| save short_url    | --------> | publish event    |
| commit fails      |           | already visible  |
+-------------------+           +------------------+

Inconsistent world.
```

Better outbox approach:

```text
BEGIN TX
save short_url
save outbox_event in same DB
COMMIT

Separate worker reads outbox_event and publishes Kafka.
```

ASCII good:

```text
Same DB Transaction
+---------------------------------------+
| INSERT short_urls                     |
| INSERT outbox_events(URL_CREATED)     |
+---------------------------------------+
                 |
                 v
              COMMIT
                 |
                 v
Outbox Worker -> Kafka
```

Memory:

```text
Transaction boundary can protect DB + outbox row.
It cannot directly protect DB + external Kafka publish without special design.
```

For phase 1, do not add Kafka inside transaction.
For high-scale phase, use outbox.

---

## 20. Correct Code Structure

Package idea:

```text
src/main/java/com/miniurl/shortener/url/
    controller/
        UrlController.java
        RedirectController.java
    service/
        UrlApplicationService.java
        UrlValidatorService.java
    repository/
        ShortUrlRepository.java
    entity/
        ShortUrl.java
    dto/
        CreateShortUrlRequest.java
        CreateShortUrlResponse.java
```

Service with transaction:

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.url.dto.CreateShortUrlRequest;
import com.miniurl.shortener.url.dto.CreateShortUrlResponse;
import com.miniurl.shortener.url.entity.ShortUrl;
import com.miniurl.shortener.url.repository.ShortUrlRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UrlApplicationService {

    private final ShortUrlRepository repository;
    private final UrlValidatorService validatorService;
    private final ShortCodeGenerator shortCodeGenerator;

    public UrlApplicationService(
            ShortUrlRepository repository,
            UrlValidatorService validatorService,
            ShortCodeGenerator shortCodeGenerator
    ) {
        this.repository = repository;
        this.validatorService = validatorService;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    @Transactional
    public CreateShortUrlResponse createShortUrl(CreateShortUrlRequest request) {
        validatorService.validateLongUrl(request.getLongUrl());

        String shortCode;

        if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {
            validatorService.validateCustomAlias(request.getCustomAlias());
            shortCode = request.getCustomAlias();
            return createWithCustomAlias(request, shortCode);
        }

        return createWithGeneratedCode(request);
    }

    private CreateShortUrlResponse createWithCustomAlias(
            CreateShortUrlRequest request,
            String shortCode
    ) {
        try {
            ShortUrl entity = ShortUrl.create(
                    request.getLongUrl().trim(),
                    shortCode,
                    request.getExpiresAt(),
                    Instant.now()
            );

            ShortUrl saved = repository.save(entity);

            return CreateShortUrlResponse.from(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new CustomAliasAlreadyExistsException(shortCode);
        }
    }

    private CreateShortUrlResponse createWithGeneratedCode(CreateShortUrlRequest request) {
        int maxAttempts = 5;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            String generatedCode = shortCodeGenerator.generate();

            try {
                ShortUrl entity = ShortUrl.create(
                        request.getLongUrl().trim(),
                        generatedCode,
                        request.getExpiresAt(),
                        Instant.now()
                );

                ShortUrl saved = repository.saveAndFlush(entity);

                return CreateShortUrlResponse.from(saved);
            } catch (DataIntegrityViolationException ex) {
                if (attempt == maxAttempts) {
                    throw new ShortCodeGenerationFailedException(
                            "Could not generate unique short code after retries"
                    );
                }
            }
        }

        throw new ShortCodeGenerationFailedException("Could not generate unique short code");
    }

    @Transactional(readOnly = true)
    public RedirectResult resolveRedirect(String shortCode) {
        validatorService.validateShortCode(shortCode);

        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));

        if (shortUrl.isBlocked()) {
            throw new ShortCodeBlockedException(shortCode);
        }

        if (shortUrl.isDeleted()) {
            throw new ShortCodeNotFoundException(shortCode);
        }

        if (shortUrl.isExpired(Instant.now())) {
            throw new ShortCodeExpiredException(shortCode);
        }

        return new RedirectResult(shortUrl.getLongUrl());
    }
}
```

Why `saveAndFlush` for generated code?

```text
It can surface unique collision inside the retry loop.
Without flush, violation may appear only at commit after method exits.
```

But do not overuse flush.

```text
Flush can reduce batching efficiency.
Use it when you need immediate constraint feedback.
```

Repository:

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

Controller stays clean:

```java
@PostMapping("/api/v1/urls")
public ResponseEntity<CreateShortUrlResponse> create(
        @Valid @RequestBody CreateShortUrlRequest request
) {
    CreateShortUrlResponse response = service.createShortUrl(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

Notice:

```text
Controller has no @Transactional.
Repository has no business transaction orchestration.
Service owns the boundary.
```

---

## 21. Step-by-Step Dry Runs

### Dry Run 1: Successful generated short URL

Request:

```json
{
  "longUrl": "https://example.com/article"
}
```

Flow:

```text
1. Controller receives request.
2. DTO validation passes.
3. Controller calls service proxy.
4. Proxy starts transaction.
5. Service validates long URL.
6. No custom alias found.
7. Generator creates code: aB91xZ.
8. Entity is created.
9. repository.saveAndFlush inserts row.
10. DB unique constraint passes.
11. Response DTO created.
12. Service method returns.
13. Proxy commits transaction.
14. Controller returns 201.
```

ASCII:

```text
Controller -> Proxy -> BEGIN -> validate -> generate -> insert -> COMMIT -> 201
```

---

### Dry Run 2: Custom alias duplicate

Existing row:

```text
short_code = sale
```

Request:

```json
{
  "longUrl": "https://example.com/new-sale",
  "customAlias": "sale"
}
```

Flow:

```text
1. Transaction starts.
2. URL validation passes.
3. Alias validation passes.
4. Entity with shortCode=sale is saved.
5. DB unique constraint fails.
6. DataIntegrityViolationException is caught.
7. Service throws CustomAliasAlreadyExistsException.
8. Transaction rolls back.
9. Global handler returns 409.
```

Result:

```text
No new row committed.
Client gets ALIAS_ALREADY_EXISTS.
```

---

### Dry Run 3: Exception after save

Code:

```java
@Transactional
public void createAndFail() {
    repository.save(shortUrl);
    throw new RuntimeException("boom");
}
```

Flow:

```text
1. Transaction starts.
2. Entity saved into persistence context.
3. SQL may be flushed now or later.
4. RuntimeException thrown.
5. Spring marks transaction rollback-only.
6. Rollback happens.
7. Row does not remain committed.
```

Memory:

```text
save inside transaction does not mean final commit.
```

---

### Dry Run 4: Read-only redirect

Request:

```http
GET /aB91xZ
```

Flow:

```text
1. Controller calls service.
2. readOnly transaction starts.
3. Repository finds row.
4. Service checks status and expiry.
5. No modification happens.
6. Transaction ends.
7. Controller returns 302 Location.
```

Good because:

```text
read operation is clear and lightweight.
```

---

### Dry Run 5: Self-invocation bug

Code:

```java
public void outer() {
    inner();
}

@Transactional
public void inner() {
    repository.save(entity);
}
```

Flow:

```text
1. Controller calls outer through proxy.
2. outer is not transactional.
3. outer calls this.inner().
4. Call does not pass through proxy.
5. @Transactional on inner is ignored.
```

Fix:

```text
Put @Transactional on outer if outer is the use case.
```

---

## 22. Internal Execution Walkthrough

When controller calls a transactional service method:

```text
1. Controller has a reference to Spring proxy, not raw service object.
2. Proxy intercepts method call.
3. TransactionInterceptor checks @Transactional metadata.
4. PlatformTransactionManager obtains DB connection.
5. autoCommit is disabled.
6. EntityManager/persistence context is bound to current thread.
7. Real service method executes.
8. Repository uses same EntityManager/connection context.
9. If method returns normally, transaction manager commits.
10. If RuntimeException occurs, transaction manager rolls back.
11. EntityManager is closed/cleared.
12. Connection is returned to pool.
```

ASCII:

```text
Controller Thread
     |
     v
Service Proxy
     |
     +-- TransactionInterceptor
             |
             +-- get connection from Hikari
             +-- bind EntityManager to thread
             +-- call real service
             +-- commit/rollback
             +-- release connection
```

Important performance note:

```text
A transaction holds a DB connection for its duration.
Long transaction = DB connection occupied longer.
```

At high RPS:

```text
slow transaction -> connection pool exhaustion -> API latency spikes
```

So keep transaction boundaries:

```text
short
focused
DB-only when possible
free from slow external calls
```

---

## 23. Testing Strategy

Test transaction behavior, not just status codes.

### Test 1: rollback on runtime exception

```java
@Test
void shouldRollbackWhenRuntimeExceptionHappens() {
    assertThrows(RuntimeException.class, () -> service.createAndFail());

    assertThat(repository.findByShortCode("abc123")).isEmpty();
}
```

### Test 2: duplicate alias returns conflict

```java
@Test
void shouldReturnConflictForDuplicateAlias() {
    service.createShortUrl(requestWithAlias("sale"));

    assertThrows(CustomAliasAlreadyExistsException.class,
            () -> service.createShortUrl(requestWithAlias("sale")));
}
```

### Test 3: redirect should not mutate row

```text
1. Create short URL.
2. Call resolveRedirect.
3. Assert longUrl returned.
4. Assert click count unchanged if analytics is async/not implemented.
```

### Test 4: generated collision retries

```text
1. Mock generator to return duplicate code first.
2. Then return unique code.
3. Assert service succeeds after retry.
```

### Test 5: read-only method behavior

```text
Use integration test to ensure resolveRedirect works inside transaction and does not update entity.
```

Testing rule:

```text
For transactions, integration tests with real database/Testcontainers are more trustworthy than pure mocks.
```

Why?

```text
Rollback, unique constraints, flush timing, and isolation are database behaviors.
```

---

## 24. Production Failure Stories

### Failure Story 1: Transaction placed on repository only

Team added transactions to repository methods.

Flow:

```text
save short_url commits
save outbox_event fails
```

Result:

```text
URL exists but no event published.
Downstream cache never warms.
```

Root cause:

```text
Transaction boundary was too small.
```

Fix:

```text
Move @Transactional to service use case wrapping both repository calls.
```

Lesson:

```text
Repository transaction is usually too narrow for business consistency.
```

---

### Failure Story 2: Long external API call inside transaction

Create URL flow called malware scanner while transaction was open.

During scanner slowness:

```text
DB connections stayed occupied.
Hikari pool exhausted.
All APIs slowed down.
```

Root cause:

```text
Transaction boundary included slow network call.
```

Fix:

```text
Move external call before transaction if it must happen before write, or use async review after commit.
```

Lesson:

```text
Transactions should be short and database-focused.
```

---

### Failure Story 3: Kafka event published then DB rollback

Service did:

```text
save URL
publish Kafka URL_CREATED
throw exception
rollback DB
```

Consumer received event for missing URL.

Root cause:

```text
External side effect happened inside DB transaction but was not part of rollback.
```

Fix:

```text
Use transactional outbox.
```

Lesson:

```text
DB rollback does not rollback Kafka.
```

---

### Failure Story 4: Self-invocation disabled transaction

Developer put `@Transactional` on `innerCreate()` but called it from same class.

Result:

```text
No transaction applied.
Lazy loading failed.
Rollback behavior unexpected.
```

Root cause:

```text
Spring proxy was bypassed.
```

Fix:

```text
Put transaction on externally called service method or move method to another bean.
```

Lesson:

```text
@Transactional works when method call crosses Spring proxy.
```

---

### Failure Story 5: Duplicate alias race

App checked:

```text
if alias does not exist, insert
```

Under concurrency, two requests inserted same alias.

Root cause:

```text
No DB unique constraint.
```

Fix:

```text
Add UNIQUE(short_code), catch violation, return 409.
```

Lesson:

```text
Application checks are not enough under concurrency.
```

---

## 25. Debugging Mindset

When transaction behavior is wrong, ask:

```text
1. Is @Transactional on the service method called from outside the bean?
2. Is the method public?
3. Is the exception RuntimeException or checked?
4. Was exception swallowed inside the method?
5. Did the transaction actually commit before error happened?
6. Is flush happening earlier than expected?
7. Is the DB constraint present?
8. Is an external side effect being confused with DB rollback?
9. Is the transaction too long and holding connection?
10. Is read-only method accidentally writing?
```

Useful logs/config:

```properties
logging.level.org.springframework.transaction=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```

Useful SQL:

```sql
SELECT short_code, long_url, status, created_at
FROM short_urls
WHERE short_code = 'sale';
```

Hikari symptoms of bad transaction boundaries:

```text
Connection is not available, request timed out
HikariPool starvation
slow API even when CPU is low
high active DB connections
```

Debug map:

```text
Data committed despite exception:
    checked exception? exception swallowed? no transaction?

Transaction not active:
    self-invocation? method not public? bean not managed by Spring?

Duplicate alias created:
    missing unique constraint?

Kafka event without DB row:
    external publish inside transaction without outbox?

Latency spike:
    long transaction? external call inside transaction?
```

---

## 26. Common Mistakes

### Mistake 1: Putting @Transactional on controller

Wrong:

```java
@PostMapping
@Transactional
public ResponseEntity<?> create(...) { ... }
```

Correct:

```java
@Transactional on service use case.
```

Why:

```text
Controller is HTTP boundary, not business transaction boundary.
```

---

### Mistake 2: Making every method transactional

Wrong:

```text
Add @Transactional everywhere just to be safe.
```

Correct:

```text
Use transactions intentionally around business units of work.
```

---

### Mistake 3: Slow external call inside transaction

Wrong:

```text
BEGIN TX
save DB
call external API for 2 seconds
COMMIT
```

Correct:

```text
Keep transaction short.
Use async/outbox for external work when possible.
```

---

### Mistake 4: Believing save means committed

Wrong:

```text
repository.save() means row is permanently in DB.
```

Correct:

```text
Commit makes it permanent.
Save only makes it managed/pending inside transaction.
```

---

### Mistake 5: Catching exception and not rethrowing

Wrong:

```java
@Transactional
public void create() {
    try {
        repository.save(entity);
        risky();
    } catch (Exception ex) {
        log.error("failed", ex);
    }
}
```

Result:

```text
Method returns normally, transaction may commit.
```

Correct:

```java
catch (Exception ex) {
    throw ex;
}
```

Or mark rollback-only intentionally.

---

### Mistake 6: Relying on exists check for uniqueness

Wrong:

```text
existsByShortCode false means safe to insert.
```

Correct:

```text
UNIQUE constraint is final protection.
```

---

### Mistake 7: Publishing Kafka directly inside DB transaction

Wrong:

```text
DB save + Kafka publish assumed atomic.
```

Correct:

```text
Use outbox for DB/event consistency.
```

---

### Mistake 8: Forgetting self-invocation

Wrong:

```text
this.transactionalMethod() activates transaction.
```

Correct:

```text
Proxy crossing activates transaction.
```

---

## 27. Interview-Ready Explanation

If interviewer asks:

```text
Where do you put transaction boundaries in a Spring Boot URL shortener?
```

Strong answer:

```text
I put transaction boundaries at the service layer because the service method represents the business unit of work. For example, createShortUrl is a read-write transaction because URL validation, short code selection, entity creation, and future outbox insertion should succeed or fail together. The controller only handles HTTP concerns, and the repository only handles persistence operations. Redirect lookup is a hot read path, so I keep it read-only or avoid a write transaction; click analytics should be async instead of synchronously updating the database on every redirect. I rely on the database UNIQUE constraint for short_code correctness under concurrency, then map unique violations to 409 Conflict. I also avoid slow external calls inside transactions because transactions hold DB connections and locks. For Kafka consistency, I would not publish directly inside the DB transaction; I would write an outbox event in the same transaction and publish asynchronously after commit.
```

If interviewer asks:

```text
Does repository.save() immediately commit?
```

Answer:

```text
No. Inside a transaction, save makes the entity managed and schedules SQL. SQL may be flushed before commit, but the change becomes permanent only at commit. If a RuntimeException occurs before commit, Spring rolls back and the row is not committed.
```

If interviewer asks:

```text
Why can @Transactional fail?
```

Answer:

```text
The most common reason is Spring proxy bypass, especially self-invocation where one method in the same class calls another @Transactional method. The call must pass through the Spring proxy. Other reasons include non-public methods, checked exceptions not configured for rollback, swallowed exceptions, or the class not being a Spring-managed bean.
```

Senior one-liner:

```text
A transaction boundary should wrap the business invariant, not just a database call.
```

---

## 28. Senior Engineer Checklist

Before moving ahead, confirm:

```text
[ ] @Transactional is on service use cases, not controllers.
[ ] createShortUrl is read-write transactional.
[ ] redirect lookup is read-only or simple non-mutating read.
[ ] external network calls are not inside DB transactions.
[ ] short_code has DB UNIQUE constraint.
[ ] duplicate alias maps to 409 Conflict.
[ ] generated code collision retries safely.
[ ] domain exceptions extend RuntimeException.
[ ] checked exception rollback behavior is understood.
[ ] self-invocation problem is avoided.
[ ] readOnly=true is used for query methods where useful.
[ ] flush vs commit difference is understood.
[ ] persistence context dirty checking is understood.
[ ] Kafka/event publishing is planned with outbox, not direct atomic assumption.
[ ] integration tests verify rollback and uniqueness.
[ ] transaction logs can be enabled for debugging.
```

---

## 29. One-Page Cheat Sheet

```text
Core mental model:
Transaction boundary = all-or-nothing box around a business operation.

Best location:
Controller  -> HTTP boundary
Service     -> transaction boundary
Repository  -> DB operation boundary

Create short URL:
@Transactional
validate -> generate/alias -> save -> commit

Redirect:
@Transactional(readOnly = true) optional
read -> validate state -> return 302
avoid sync analytics write in hot path

Rollback:
RuntimeException -> rollback by default
Checked exception -> no rollback unless rollbackFor configured

JPA:
Transaction contains persistence context
managed entity changes can be auto-updated by dirty checking
save != commit
flush != commit

Concurrency:
exists check is not enough
UNIQUE(short_code) is mandatory
catch unique violation -> 409

Propagation:
REQUIRED is default and enough for most use cases
REQUIRES_NEW means independent commit; use carefully

Self-invocation:
@Transactional works through proxy
this.method() bypasses proxy

External side effects:
DB rollback does not rollback Kafka/email/S3
Use outbox for DB + event consistency

Performance:
Keep transactions short
Do not hold DB connection during slow external calls
```

---

## 30. One Picture To Remember

```text
                 TRANSACTION BOUNDARY MENTAL MODEL

HTTP Request
    |
    v
+---------------------------+
| Controller                |
| API boundary              |
| DTO validation            |
+---------------------------+
    |
    v
+--------------------------------------------------+
| Service Proxy                                    |
| starts @Transactional                            |
|                                                  |
|   +------------------------------------------+   |
|   | BUSINESS UNIT OF WORK                    |   |
|   |                                          |   |
|   | validate business rules                  |   |
|   | create/update entity                     |   |
|   | repository.save                          |   |
|   | optional outbox insert later             |   |
|   |                                          |   |
|   +------------------------------------------+   |
|              |                                   |
|              +-- success -> COMMIT               |
|              |                                   |
|              +-- failure -> ROLLBACK             |
+--------------------------------------------------+
    |
    v
+---------------------------+
| Repository                |
| DB operations             |
+---------------------------+
    |
    v
+---------------------------+
| PostgreSQL                |
| ACID + constraints        |
+---------------------------+

FINAL MEMORY:

Controller owns HTTP.
Service owns business transaction.
Repository owns persistence operation.
Database owns final truth.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. A transaction boundary is the all-or-nothing box around a business operation.
2. In Spring Boot, transaction boundaries usually belong on service methods.
3. repository.save() is not the same as commit; commit makes changes permanent.
4. Runtime exceptions rollback by default, but checked exceptions need rollbackFor.
5. Database transactions do not rollback external side effects like Kafka, email, or S3.
```

After this chapter, you understand how MiniURLShortener protects consistency during create, redirect, duplicate alias handling, and future event publishing.

Next chapter:

```text
014_Global_Exception_Handling.md
```
