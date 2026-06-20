# 034_Transaction_EndToEnd — The Atomic Work Envelope Model

## Core Mental Model

Do not imagine `@Transactional` as:

```text
A magic annotation that saves data.
```

That model causes production bugs.

The better mental model is:

> **A transaction is an atomic work envelope around a service use case. Everything inside either commits together or rolls back together.**

```text
Service Proxy
    |
    | begin transaction
    v
+------------------------------------------------+
| @Transactional Service Method                  |
|------------------------------------------------|
| load entity                                    |
| apply business rule                            |
| create/update/delete entities                  |
| flush SQL                                      |
+------------------------------------------------+
    |
    | commit or rollback
    v
Database durable state
```

This chapter teaches exactly one idea:

> **Spring transactions are not owned by repositories or controllers; they are service-level envelopes created by proxies, bound to a thread, connected to the database, synchronized with Hibernate, and completed by commit or rollback.**

If you remember only one sentence:

> **`@Transactional` means Spring opens a database work envelope before the method, Hibernate works inside it, and Spring commits or rolls back after the method.**

---

## Why This Exists

Business operations are rarely one SQL statement.

Example: create order.

```text
1. Load product
2. Check stock
3. Decrease stock
4. Insert order
5. Insert payment attempt
6. Publish domain event later
```

If step 4 succeeds but step 3 fails, data becomes inconsistent.

Bad state:

```text
order inserted
product stock not reduced
payment maybe charged
```

A transaction exists to protect consistency.

```text
Either all database changes succeed
or none of them become durable.
```

Without a transaction:

```text
UPDATE product succeeds
INSERT order fails
database now has wrong stock
```

With transaction:

```text
UPDATE product succeeds
INSERT order fails
rollback restores product update
```

Transaction is the safety envelope around a unit of work.

---

## Problem Statement

Given this service:

```java
@Transactional
public Long createOrder(Long productId, int quantity) {
    Product product = productRepository.findById(productId)
            .orElseThrow();

    product.decreaseStock(quantity);

    Order order = Order.create(product, quantity);
    orderRepository.save(order);

    return order.getId();
}
```

The system must answer:

```text
1. Who starts the transaction?
2. When is the DB connection borrowed?
3. Where is transaction state stored?
4. How does Hibernate join the transaction?
5. When does dirty checking happen?
6. When does SQL flush?
7. What causes rollback?
8. What happens if commit fails?
9. Why does self-invocation break transactions?
```

The core problem:

> **How does Spring make multiple repository operations behave like one atomic database unit?**

Answer:

```text
Spring AOP proxy intercepts the service method.
Transaction manager opens transaction.
Transaction state binds to current thread.
Hibernate persistence context participates.
Repositories execute inside same transaction.
At method exit, Spring commits or rolls back.
```

---

## Real World Analogy

Imagine a bank vault transaction.

A customer transfers money:

```text
Debit account A
Credit account B
Record audit row
```

The bank does not permanently apply each step separately.

Instead, the bank clerk opens a transaction folder.

```text
Transaction Folder:
  debit A
  credit B
  audit transfer
```

If all checks pass:

```text
stamp COMMIT
```

If any check fails:

```text
stamp ROLLBACK
discard folder
```

Mapping:

```text
Bank transaction folder       Spring transaction
Clerk                         Transaction manager
Steps inside folder           repository/database operations
Stamp commit                  database commit
Discard folder                rollback
```

Important:

> **The folder is opened before the work and closed after the work.**

That is exactly the Spring transaction envelope.

---

## The One Mental Model

`@Transactional` creates this runtime shape:

```text
Caller
  |
  v
Spring Proxy
  |
  | before method:
  |   begin transaction
  |   bind transaction resources to thread
  v
Real Service Method
  |
  | repository calls
  | Hibernate persistence context
  | DB connection
  v
Spring Proxy
  |
  | after method:
  |   if success -> flush + commit
  |   if rollback exception -> rollback
  v
Caller
```

ASCII:

```text
          call service.createOrder()
                    |
                    v
          +----------------------+
          | Transaction Proxy    |
          |----------------------|
          | begin transaction    |
          +----------+-----------+
                     |
                     v
          +----------------------+
          | Real Service Method  |
          | business work        |
          +----------+-----------+
                     |
          +----------+-----------+
          |                      |
          v                      v
      success                exception
          |                      |
          v                      v
      flush SQL              rollback
      commit
```

The proxy is the doorway.
If you bypass the doorway, transaction advice does not run.

---

## Core Concepts

## Transaction

A transaction is a database unit of work with ACID properties.

```text
Atomicity:
  all or nothing

Consistency:
  valid database rules remain valid

Isolation:
  concurrent transactions are controlled

Durability:
  committed data survives failure
```

This chapter focuses on the end-to-end runtime envelope.

## Transaction Manager

Spring component that begins, commits, and rolls back transactions.

Common:

```text
JpaTransactionManager
DataSourceTransactionManager
JtaTransactionManager
```

For JPA/Hibernate apps:

```text
JpaTransactionManager
```

## Transaction Proxy

Spring applies `@Transactional` through AOP proxy.

```text
Controller -> Service proxy -> TransactionInterceptor -> Real service object
```

The proxy starts/ends transaction.

## TransactionSynchronizationManager

Spring internally binds transaction resources to the current thread.

Conceptually:

```text
Current thread:
  EntityManager bound
  JDBC Connection bound
  transaction active
```

This lets repository calls reuse the same transaction context.

## Persistence Context

Hibernate workbench inside transaction.

It tracks managed entities, snapshots, dirty changes, and flushes SQL.

## Flush

Flush sends SQL to database.

```text
Persistence context changes -> INSERT/UPDATE/DELETE SQL
```

Flush often happens before commit.

## Commit

Commit makes database transaction permanent.

Flush and commit are related but not identical.

## Rollback

Rollback cancels uncommitted changes.

Typical Spring default:

```text
rollback on RuntimeException and Error
not rollback on checked exception unless configured
```

## Propagation

Propagation controls what happens if a transaction already exists.

Common:

```text
REQUIRED:
  join existing or create new

REQUIRES_NEW:
  suspend existing, start new

MANDATORY:
  require existing transaction

SUPPORTS:
  join if exists, otherwise non-transactional
```

This chapter focuses on end-to-end REQUIRED flow.

---

## Internal Architecture

```text
Controller
    |
    v
Service Proxy
    |
    v
TransactionInterceptor
    |
    v
PlatformTransactionManager
    |
    +--> begin transaction
    +--> bind resources to thread
    |
    v
Real Service Method
    |
    v
Repositories
    |
    v
EntityManager
    |
    v
Persistence Context
    |
    v
Hibernate
    |
    v
HikariCP / JDBC Connection
    |
    v
Database Transaction
```

Commit path:

```text
Service returns
    |
    v
TransactionInterceptor
    |
    v
flush persistence context
    |
    v
database commit
    |
    v
unbind resources
```

Rollback path:

```text
Service throws rollback exception
    |
    v
TransactionInterceptor
    |
    v
database rollback
    |
    v
clear/close persistence context
    |
    v
unbind resources
```

---

## Internal Working

Given:

```java
@Service
public class OrderService {

    @Transactional
    public Long createOrder(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow();

        product.decreaseStock(quantity);

        Order order = Order.create(product, quantity);
        orderRepository.save(order);

        return order.getId();
    }
}
```

Runtime:

```text
1. Controller calls orderService proxy.
2. TransactionInterceptor sees @Transactional metadata.
3. Transaction manager checks if transaction exists.
4. None exists, so it begins a new transaction.
5. Transaction resources bind to current thread.
6. Real createOrder method runs.
7. Repository uses thread-bound EntityManager/connection.
8. Product is loaded into persistence context.
9. Business code modifies managed Product.
10. New Order is persisted.
11. Method returns normally.
12. Transaction interceptor prepares commit.
13. Hibernate flushes persistence context.
14. SQL INSERT/UPDATE executes.
15. Database commit succeeds.
16. Resources unbind from thread.
17. Response continues.
```

If exception occurs:

```text
1. Exception leaves service method.
2. Transaction interceptor catches it.
3. It checks rollback rules.
4. RuntimeException means rollback.
5. Database rollback executes.
6. Resources unbind.
7. Exception propagates to controller/advice.
```

---

## Rich ASCII Diagram — Transaction Boundary

```text
Controller
   |
   | calls service.createOrder()
   v
+----------------------------------------------------+
| Service Proxy                                      |
|----------------------------------------------------|
| BEFORE:                                            |
|   begin transaction                                |
|   bind EntityManager/Connection to thread          |
|                                                    |
|   +--------------------------------------------+   |
|   | Real Service Method                        |   |
|   |--------------------------------------------|   |
|   | productRepository.findById()               |   |
|   | product.decreaseStock()                    |   |
|   | orderRepository.save()                     |   |
|   +--------------------------------------------+   |
|                                                    |
| AFTER:                                             |
|   success -> flush + commit                        |
|   failure -> rollback                              |
+----------------------------------------------------+
```

---

## Rich ASCII Diagram — Commit vs Rollback

```text
                    Transaction Envelope
                            |
        +-------------------+-------------------+
        |                                       |
        v                                       v
  Method returns                         RuntimeException
  normally                               thrown
        |                                       |
        v                                       v
  Hibernate flush                         rollback DB tx
        |
        v
  SQL executes
        |
        v
  DB commit
        |
        v
  durable changes
```

---

## Java Code Example — Successful Transaction

```java
@Transactional
public OrderResponse createOrder(CreateOrderRequest request) {
    Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new BusinessException("Product not found"));

    product.decreaseStock(request.quantity());

    Order order = Order.create(product, request.quantity());
    Order saved = orderRepository.save(order);

    return OrderResponse.from(saved);
}
```

Execution:

```text
begin transaction
load product
product becomes managed
decrease stock
save new order
flush at commit
commit
```

Possible SQL:

```sql
select * from products where id = ?;
insert into orders (...);
update products set stock = ? where id = ?;
commit;
```

Exact SQL ordering may vary by ID strategy and flush timing.

Mental model:

```text
All SQL belongs to same database transaction.
```

---

## Java Code Example — Rollback on RuntimeException

```java
@Transactional
public void reserveStock(Long productId, int quantity) {
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new BusinessException("Product not found"));

    product.decreaseStock(quantity);

    if (quantity > 10) {
        throw new IllegalStateException("Suspicious quantity");
    }
}
```

Flow:

```text
1. Transaction starts.
2. Product loaded.
3. Stock changed in memory.
4. IllegalStateException thrown.
5. Spring marks transaction rollback.
6. Database rollback happens.
7. Stock change is not committed.
```

Important:

```text
The Java object changed.
The database did not commit the change.
```

---

## Java Code Example — Checked Exception Surprise

```java
@Transactional
public void importOrder() throws IOException {
    orderRepository.save(new Order());

    throw new IOException("file failed");
}
```

Default Spring behavior:

```text
IOException is checked exception.
By default, transaction may not roll back.
```

If you want rollback:

```java
@Transactional(rollbackFor = IOException.class)
public void importOrder() throws IOException {
    orderRepository.save(new Order());

    throw new IOException("file failed");
}
```

Interview point:

```text
Spring rolls back by default on unchecked exceptions.
Checked exceptions need rollbackFor unless configured otherwise.
```

---

## Step-by-Step Dry Run — Successful Order

Database before:

```text
products
+------+-------+
| id   | stock |
+------+-------+
| 1001 | 10    |
+------+-------+
```

Request:

```text
create order product=1001 quantity=2
```

Flow:

```text
1. Controller calls service proxy.
2. Proxy starts transaction T1.
3. Persistence context PC1 opens.
4. productRepository.findById(1001).
5. Hibernate SELECT product.
6. Product#1001 managed with snapshot stock=10.
7. product.decreaseStock(2), current stock=8.
8. orderRepository.save(order).
9. Order becomes managed/new.
10. Service returns.
11. Proxy commits transaction.
12. Hibernate flush:
    INSERT order
    UPDATE product stock=8
13. Database commit.
14. PC1 closes.
```

Database after:

```text
products
+------+-------+
| id   | stock |
+------+-------+
| 1001 | 8     |
+------+-------+

orders
+-----+------------+----------+
| id  | product_id | quantity |
+-----+------------+----------+
| 501 | 1001       | 2        |
+-----+------------+----------+
```

---

## Step-by-Step Dry Run — Failure and Rollback

Database before:

```text
product stock=10
```

Flow:

```text
1. Transaction starts.
2. Product loaded.
3. Stock changed to 8.
4. Order saved.
5. Payment validation throws RuntimeException.
6. Exception exits method.
7. Transaction interceptor catches exception.
8. Rollback rule says rollback.
9. Database rollback executes.
10. Product stock remains 10.
11. Order is not inserted.
```

Database after:

```text
product stock=10
orders unchanged
```

Retention point:

```text
Rollback cancels database changes, not necessarily Java object field changes already made in memory.
```

---

## Step-by-Step Dry Run — Self-Invocation Failure

Code:

```java
@Service
public class OrderService {

    public void createWrapper(Long productId) {
        createOrder(productId); // same class call
    }

    @Transactional
    public void createOrder(Long productId) {
        // database work
    }
}
```

Flow:

```text
1. Controller calls orderService proxy.createWrapper().
2. createWrapper is not transactional.
3. Inside real object, this.createOrder() is called.
4. Call does not go through proxy.
5. TransactionInterceptor not triggered.
6. @Transactional on createOrder is bypassed.
```

Fix:

```text
Move transactional method to another Spring bean.
Call through proxy.
Put @Transactional on external public service method.
```

Mental model:

```text
The proxy is the transaction doorway.
Self-invocation uses back door.
```

---

## Step-by-Step Dry Run — REQUIRES_NEW

Outer:

```java
@Transactional
public void createOrder() {
    orderRepository.save(order);
    auditService.writeAudit();
    throw new RuntimeException("fail order");
}
```

Audit:

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void writeAudit() {
    auditRepository.save(audit);
}
```

Flow:

```text
1. Outer transaction T1 starts.
2. Order saved in T1.
3. auditService proxy called.
4. T1 suspended.
5. New transaction T2 starts.
6. Audit saved in T2.
7. T2 commits.
8. T1 resumes.
9. Outer method throws RuntimeException.
10. T1 rolls back.
```

Result:

```text
Order rolled back.
Audit committed.
```

Use carefully.

---

## Transaction Propagation Overview

```text
REQUIRED
  Use current transaction or create one.
  Most common.

REQUIRES_NEW
  Suspend current and start independent new transaction.

MANDATORY
  Must already have transaction, otherwise error.

SUPPORTS
  Join if exists, otherwise run without transaction.

NOT_SUPPORTED
  Suspend transaction and run without one.

NEVER
  Fail if transaction exists.

NESTED
  Savepoint-based nested transaction if supported.
```

This chapter's default mental model:

```text
REQUIRED service transaction.
```

---

## Sequence Diagram

```text
Controller
  |
  | service.createOrder()
  v
Service Proxy
  |
  | TransactionInterceptor
  | begin transaction
  v
TransactionManager
  |
  | bind resources to thread
  v
Real Service
  |
  | repository.findById()
  v
Repository
  |
  v
EntityManager/Hibernate
  |
  | borrow connection if SQL needed
  v
HikariCP
  |
  v
Database
  |
  | SELECT
  v
Service
  |
  | modify entities
  v
Service Proxy
  |
  | method returns
  | flush + commit
  v
Database
  |
  | COMMIT
  v
Controller
```

---

## Production Scale Example

At 2,000 RPS, transaction duration matters.

```text
Transaction duration = how long DB resources may be held.
```

Bad transaction:

```java
@Transactional
public void checkout() {
    orderRepository.save(order);
    paymentClient.charge();     // 2 seconds
    emailClient.send();         // 500 ms
    inventoryRepository.update();
}
```

Problem:

```text
DB transaction stays open during external calls.
Hikari connection may stay borrowed.
Rows may stay locked.
Other requests wait.
```

Better:

```text
Transaction 1:
  create order
  reserve inventory
  write outbox event
  commit

Async processor:
  read outbox
  call payment
  update payment status in separate transaction
```

Pattern:

```text
Keep transactions short.
Do DB work inside.
Move slow external effects outside or through outbox/saga.
```

---

## Production Failure Story

A team had random `HikariPool connection timeout` during checkout.

They increased pool size:

```text
20 -> 80
```

It helped briefly, then DB slowed more.

Root cause investigation:

```text
checkout transaction included fraud API call
fraud API p99 became 4 seconds
DB connection stayed checked out during entire transaction
80 connections became active and waiting
Postgres had more concurrent sessions
queries slowed further
timeouts increased
```

Real fix:

```text
Move fraud API call outside DB transaction.
Store pending checkout first.
Use async fraud decision.
Shorten transaction from 4 seconds to 80 ms.
Reduce pool size back.
```

Lesson:

> **Connection pool exhaustion is often transaction-duration exhaustion.**

---

## Debugging Mindset

When transaction behavior surprises you, ask:

```text
1. Did the call go through Spring proxy?
2. Is method public?
3. Is @Transactional on the right bean?
4. Is there already a transaction?
5. What propagation is used?
6. What exception was thrown?
7. Does rollback rule include that exception?
8. Did method catch and swallow exception?
9. When did flush happen?
10. Did commit fail after method return?
```

### Symptom Map

```text
@Transactional not applied
  -> self-invocation
  -> private/final method
  -> object not Spring bean
  -> proxy not used

Data committed despite exception
  -> checked exception
  -> exception swallowed
  -> rollbackFor missing
  -> REQUIRES_NEW committed independently

Unexpected rollback
  -> RuntimeException
  -> transaction marked rollback-only
  -> inner method failed

Slow requests
  -> long transaction
  -> external call inside transaction
  -> DB locks
  -> slow flush

Hikari timeout
  -> connections held too long
  -> transaction duration high
```

---

## Common Misconceptions

## Misconception 1 — “`@Transactional` works on any method call”

No.

It works when call goes through Spring proxy.

Self-invocation can bypass it.

## Misconception 2 — “Repository methods are enough transaction boundary”

Repository transactions are too small for business use cases.

Service should usually own the full unit of work.

## Misconception 3 — “Checked exceptions rollback automatically”

By default, Spring rolls back unchecked exceptions.
Checked exceptions need `rollbackFor`.

## Misconception 4 — “Flush equals commit”

No.

Flush sends SQL.
Commit makes transaction permanent.

## Misconception 5 — “`save()` immediately commits”

No.

Commit belongs to transaction boundary.

## Misconception 6 — “Long transaction is okay if SQL is simple”

No.

Long transaction can hold connection, locks, snapshots, and memory.

---

## Java/Spring Boot Code — Correct Transaction Boundary

Bad:

```java
@RestController
public class OrderController {

    @PostMapping("/orders")
    @Transactional
    public OrderResponse create(@RequestBody CreateOrderRequest request) {
        // business logic here
    }
}
```

Better:

```java
@RestController
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public OrderResponse create(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }
}
```

```java
@Service
public class OrderService {

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // complete business use case
    }
}
```

Reason:

```text
Controller owns HTTP.
Service owns transaction.
```

---

## Java/Spring Boot Code — Avoid External Call Inside Transaction

Bad:

```java
@Transactional
public void placeOrder(CreateOrderRequest request) {
    Order order = orderRepository.save(Order.create(request));

    paymentClient.charge(order.getId()); // slow network inside tx

    order.markPaid();
}
```

Better shape:

```java
@Transactional
public void placeOrder(CreateOrderRequest request) {
    Order order = orderRepository.save(Order.create(request));
    outboxRepository.save(OutboxEvent.paymentRequested(order.getId()));
}
```

Then async:

```java
public void processPaymentEvent(PaymentRequested event) {
    paymentClient.charge(event.orderId());

    transactionTemplate.executeWithoutResult(status -> {
        Order order = orderRepository.findById(event.orderId()).orElseThrow();
        order.markPaid();
    });
}
```

Mental model:

```text
Short DB transaction.
External call outside.
State transition in separate transaction.
```

---

## Performance Considerations

Transaction performance depends on:

```text
transaction duration
number of SQL statements
lock duration
connection hold time
flush size
dirty checking cost
isolation level
index quality
contention
```

Golden rule:

```text
Keep transactions short and focused.
```

Avoid inside transaction:

```text
remote HTTP calls
sleep/retry loops
large file processing
large batch without flush/clear
user interaction waits
expensive CPU work not needing DB
```

Measure:

```text
transaction duration
Hikari connection hold time
DB lock wait
commit latency
SQL count per transaction
```

---

## Scalability Considerations

Transactions create pressure on shared resources.

```text
DB connections
row locks
indexes
undo/WAL
MVCC snapshots
connection pool
database CPU
```

At scale:

```text
small increase in transaction duration
  -> large increase in active connections needed
```

Little's Law intuition:

```text
active transactions ≈ throughput × transaction duration
```

Example:

```text
500 tx/sec * 100ms = 50 active tx
500 tx/sec * 1s = 500 active tx
```

A slow downstream call inside transaction can multiply DB pressure by 10x.

---

## Failure Investigation Playbook

## Step 1 — Confirm transaction exists

Use logs in lower env:

```properties
logging.level.org.springframework.transaction=TRACE
```

Look for:

```text
Creating new transaction
Participating in existing transaction
Initiating transaction commit
Initiating transaction rollback
```

## Step 2 — Check proxy boundary

Ask:

```text
Is method public?
Is class a Spring bean?
Is call external through proxy?
Is self-invocation present?
```

## Step 3 — Check rollback rule

Ask:

```text
RuntimeException?
Checked exception?
rollbackFor configured?
Exception swallowed?
Transaction marked rollback-only?
```

## Step 4 — Check SQL timing

Ask:

```text
When does flush happen?
Are queries before commit forcing flush?
Any constraint violation at commit?
```

## Step 5 — Check duration

Ask:

```text
How long is transaction open?
Is connection borrowed during slow external call?
Are locks held?
Is Hikari pending high?
```

---

## Interview Q&A

### Q1. How does `@Transactional` work internally?

Strong answer:

> Spring applies `@Transactional` through AOP proxy. When a caller invokes the proxied method, the transaction interceptor asks the transaction manager to begin or join a transaction, binds resources like EntityManager/Connection to the current thread, runs the real method, and then commits or rolls back based on the outcome and rollback rules.

### Q2. Where should `@Transactional` be placed?

Strong answer:

> Usually on service-layer methods because they represent complete business use cases. The transaction should cover all repository operations that must succeed or fail together.

### Q3. Why does self-invocation break transactions?

Strong answer:

> Because `@Transactional` is applied by proxy. A method call inside the same object uses `this.method()` and bypasses the proxy, so the transaction interceptor does not run.

### Q4. What is the difference between flush and commit?

Strong answer:

> Flush sends pending SQL changes from Hibernate persistence context to the database. Commit makes the database transaction permanent. Flushed changes can still be rolled back if the transaction fails.

### Q5. What exceptions trigger rollback by default?

Strong answer:

> By default, Spring rolls back on unchecked exceptions such as `RuntimeException` and `Error`. Checked exceptions do not trigger rollback unless configured with `rollbackFor`.

### Q6. What is transaction propagation?

Strong answer:

> Propagation defines how a transactional method behaves when another transaction already exists. For example, `REQUIRED` joins or creates a transaction, while `REQUIRES_NEW` suspends the current transaction and starts an independent one.

### Q7. Why are long transactions dangerous?

Strong answer:

> They can hold database connections, locks, MVCC snapshots, and persistence context memory longer. This increases contention, Hikari pool pressure, lock waits, and p99 latency.

---

## Production Checklist

```text
Boundary
[ ] @Transactional on service use case
[ ] Public method called through proxy
[ ] No self-invocation problem
[ ] Controller not owning transaction

Rollback
[ ] Runtime exceptions understood
[ ] Checked exceptions configured with rollbackFor if needed
[ ] Exceptions not swallowed accidentally
[ ] rollback-only behavior understood

Duration
[ ] No external API calls inside transaction
[ ] No long CPU/file work inside transaction
[ ] Batch jobs flush/clear
[ ] Transaction timeout considered

Persistence
[ ] Flush timing understood
[ ] Entity states understood
[ ] Lazy loading boundaries controlled
[ ] DTO mapping safe

Operations
[ ] Hikari pending monitored
[ ] DB lock waits monitored
[ ] Transaction duration measured
[ ] Slow query logs enabled
[ ] Deadlocks observable
```

---

## One-Page Cheat Sheet

```text
Transaction End-to-End
======================

Core Idea
---------
Atomic work envelope around service use case.

Who starts it?
  Spring AOP proxy + TransactionInterceptor.

Who manages it?
  PlatformTransactionManager.

Where is state bound?
  Current thread via transaction synchronization.

Who participates?
  EntityManager, Hibernate, JDBC connection.

Normal Flow
-----------
caller -> proxy
proxy begins transaction
real service method runs
repositories use same transaction
Hibernate flushes
DB commits
resources unbind

Rollback Flow
-------------
exception exits method
rollback rule checked
DB rollback
resources unbind
exception propagates

Default Rollback
----------------
RuntimeException / Error

Checked Exception
-----------------
Needs rollbackFor if rollback desired.

Flush vs Commit
---------------
flush = send SQL
commit = make permanent

Best Sentence
-------------
@Transactional opens a database work envelope before the method
and commits or rolls back after the method.
```

---

## Last-Minute Interview Revision

Do not say:

```text
@Transactional saves data automatically.
```

Say:

```text
`@Transactional` is applied by a Spring proxy. The proxy opens or joins a transaction before the service method, binds transaction resources to the current thread, lets repositories and Hibernate work inside that transaction, then commits on success or rolls back based on exception rules.
```

Senior version:

```text
I design transactions as short service-level units of work. I avoid external calls inside them, understand proxy/self-invocation limitations, configure rollback rules for checked exceptions, and monitor transaction duration, Hikari pressure, SQL count, and DB locks in production.
```

---

## One Picture To Remember

```text
                 TRANSACTION END-TO-END

Controller
   |
   v
Service Proxy
   |
   | begin transaction
   v
+--------------------------------------------+
| Real Service Method                        |
|--------------------------------------------|
| load entity                                |
| modify entity                              |
| save new entity                            |
| business rules                             |
| repository calls                           |
+--------------------------------------------+
   |
   | method exits
   v
Service Proxy
   |
   +---------------------+
   |                     |
   v                     v
success             rollback exception
   |                     |
   v                     v
flush SQL           rollback DB
   |
   v
commit DB
   |
   v
durable state
```

Final retention sentence:

> **A transaction is the atomic envelope around a service use case: Spring opens it through a proxy, Hibernate works inside it, and the database commits or rolls back the whole envelope.**
