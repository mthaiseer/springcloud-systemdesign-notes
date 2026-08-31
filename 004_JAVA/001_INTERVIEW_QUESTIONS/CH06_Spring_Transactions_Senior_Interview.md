# CH 06 --- Spring Transactions

## Senior Java Distributed Backend Interview Series

**Target:** \~35 interview questions\
**Goal:** Master transaction fundamentals, Spring `@Transactional`
internals, propagation, isolation, rollback behavior, locking,
transaction boundaries, and distributed-system failure scenarios.

------------------------------------------------------------------------

# Chapter Map

``` text
CH 06 — SPRING TRANSACTIONS (~35 Q)
|
+-- 1. Transaction Fundamentals (7)
|   +-- Transaction / ACID
|   +-- Atomicity / consistency
|   +-- Isolation / durability
|   +-- Local vs distributed transaction
|   +-- Transaction boundary
|   +-- Autocommit
|   +-- PlatformTransactionManager
|
+-- 2. @Transactional Internals (8)
|   +-- Proxy model
|   +-- TransactionInterceptor
|   +-- Commit / rollback flow
|   +-- Method/class annotation
|   +-- Self-invocation
|   +-- private/final methods
|   +-- Checked vs unchecked exceptions
|   +-- rollbackFor / noRollbackFor
|
+-- 3. Propagation (7)
|   +-- REQUIRED
|   +-- REQUIRES_NEW
|   +-- NESTED
|   +-- SUPPORTS
|   +-- MANDATORY
|   +-- NOT_SUPPORTED / NEVER
|   +-- Propagation production traps
|
+-- 4. Isolation / Locking (7)
|   +-- Isolation levels
|   +-- Dirty read
|   +-- Non-repeatable read
|   +-- Phantom read
|   +-- Lost update
|   +-- Optimistic / pessimistic locking
|   +-- Deadlocks
|
+-- 5. Boundaries / Distributed Systems (6)
    +-- Long transactions
    +-- Remote calls inside transactions
    +-- @Async / thread boundaries
    +-- Transactional events
    +-- Outbox pattern
    +-- Production debugging
```

------------------------------------------------------------------------

# Big Picture

``` text
HTTP Request
     |
     v
Controller
     |
     v
Service Proxy
     |
     +--> TransactionInterceptor
              |
              v
        BEGIN TRANSACTION
              |
              v
         Service Method
              |
              v
          Repository
              |
              v
           Database
              |
       +------+------+
       |             |
    SUCCESS        FAILURE
       |             |
       v             v
     COMMIT        ROLLBACK
```

The most important Spring transaction mental model:

``` text
@Transactional
      |
      v
Spring detects metadata
      |
      v
creates/intercepts through proxy
      |
      v
TransactionInterceptor
      |
      v
TransactionManager
      |
      v
DB connection transaction
```

------------------------------------------------------------------------

# 1. Transaction Fundamentals --- 7 Questions

## Q1. What is a database transaction?

A transaction is a logical unit of work whose operations should succeed
or fail according to transactional guarantees.

``` text
Transfer €100

BEGIN
  |
  +--> debit Account A
  |
  +--> credit Account B
  |
  +--> insert transfer record
  |
  +--> COMMIT

Failure?
  |
  v
ROLLBACK
```

Without a transaction:

``` text
debit A succeeds
      |
service crashes
      |
credit B never happens
      |
INCONSISTENT STATE
```

------------------------------------------------------------------------

## Q2. Explain ACID.

``` text
A = Atomicity
C = Consistency
I = Isolation
D = Durability
```

### Atomicity

``` text
all operations succeed
        OR
all are rolled back
```

### Consistency

A successful transaction should preserve defined data invariants,
assuming the application and database constraints are correct.

### Isolation

Concurrent transactions should interact according to the chosen
isolation semantics.

### Durability

Once committed, data should survive failures according to the database's
durability guarantees.

------------------------------------------------------------------------

## Q3. Atomicity vs consistency?

Atomicity:

``` text
Operation A
Operation B
Operation C

either:
A+B+C

or:
none
```

Consistency:

``` text
Before transaction
valid state
     |
transaction
     |
After commit
valid state
```

Example:

``` text
Bank invariant:
total money must remain correct
```

Atomic execution helps, but business consistency also depends on
constraints and application logic.

------------------------------------------------------------------------

## Q4. Local vs distributed transaction?

Local:

``` text
Service
   |
   v
PostgreSQL
   |
single transaction manager/resource
```

Distributed workflow:

``` text
Order Service
   |
   +--> Order DB
   |
   +--> Payment Service
   |       |
   |       +--> Payment DB
   |
   +--> Kafka
```

One Spring database transaction cannot automatically make all
independent networked systems commit atomically.

This becomes:

``` text
local ACID
     +
distributed coordination
```

Typical distributed patterns are covered later:

-   Saga
-   transactional outbox
-   idempotency
-   compensation
-   eventual consistency

------------------------------------------------------------------------

## Q5. What is a transaction boundary?

A boundary defines which operations belong to one transaction.

Good example:

``` java
@Transactional
public void createOrder(CreateOrder command) {
    orderRepository.save(...);
    inventoryRepository.reserve(...);
}
```

``` text
Business Use Case
      |
      +--> DB operation 1
      +--> DB operation 2
      |
      v
one transaction boundary
```

### Senior rule

Transaction boundaries should generally align with meaningful business
operations, not arbitrary helper methods.

------------------------------------------------------------------------

## Q6. What is autocommit?

At JDBC/database level, autocommit commonly means each statement commits
automatically unless an explicit transaction is started/configured.

Conceptually:

``` text
AUTOCOMMIT

UPDATE A
   |
COMMIT

UPDATE B
   |
COMMIT
```

vs:

``` text
TRANSACTION

BEGIN
 |
UPDATE A
 |
UPDATE B
 |
COMMIT
```

Spring transaction management coordinates the connection so statements
participate in the same transaction.

------------------------------------------------------------------------

## Q7. What is `PlatformTransactionManager`?

Spring provides a transaction abstraction.

Conceptually:

``` text
@Transactional
      |
TransactionInterceptor
      |
PlatformTransactionManager
      |
      +--> begin
      +--> commit
      +--> rollback
```

The exact transaction manager depends on the underlying technology.

Senior point: `@Transactional` is declarative metadata; actual
transaction behavior is implemented through Spring infrastructure plus
the underlying resource manager/database.

------------------------------------------------------------------------

# 2. `@Transactional` Internals --- 8 Questions

## Q8. How does `@Transactional` work internally?

``` java
@Service
class OrderService {

    @Transactional
    public void createOrder() {
        ...
    }
}
```

Conceptual runtime:

``` text
Caller
  |
  v
OrderService Proxy
  |
  v
TransactionInterceptor
  |
  +--> inspect transaction metadata
  |
  +--> obtain/create transaction
  |
  v
Target createOrder()
  |
  +--> repository operations
  |
  v
return / throw
  |
  +--> commit
  |
  +--> rollback
```

This is why CH04's proxy model matters.

------------------------------------------------------------------------

## Q9. What does TransactionInterceptor do?

Simplified:

``` text
Intercept method
      |
      v
Read @Transactional metadata
      |
      v
Determine transaction manager
      |
      v
Create/join transaction
      |
      v
Invoke target
      |
 +----+----+
 |         |
return    exception
 |         |
 v         v
commit   rollback?
```

Actual Spring internals are more sophisticated, but this is the correct
interview model.

------------------------------------------------------------------------

## Q10. When does commit happen?

``` java
@Transactional
public void update() {
    repository.save(...);
}
```

Conceptually:

``` text
enter proxy
   |
BEGIN
   |
method executes
   |
method returns normally
   |
transaction manager COMMIT
   |
caller receives result
```

### Important JPA point

`save()` is not necessarily the same moment SQL is committed.

``` text
entity change
    |
persistence context
    |
flush
    |
SQL sent
    |
transaction commit
```

Covered deeply in CH07.

------------------------------------------------------------------------

## Q11. Method-level vs class-level `@Transactional`?

Class level:

``` java
@Transactional
@Service
class OrderService {
    public void create() {}
    public void cancel() {}
}
```

Method override:

``` java
@Transactional(readOnly = true)
public Order find() {}
```

Conceptually:

``` text
Class default transaction metadata
           |
           +--> method-specific metadata can specialize behavior
```

Keep boundaries explicit enough that readers can understand
transactional behavior.

------------------------------------------------------------------------

## Q12. Why does self-invocation cause problems?

``` java
@Service
class PaymentService {

    public void checkout() {
        savePayment();
    }

    @Transactional
    public void savePayment() {
        ...
    }
}
```

External call:

``` text
Controller
   |
   v
PaymentService Proxy
   |
   v
checkout()
```

Internal:

``` text
checkout()
   |
   v
this.savePayment()
   |
   X proxy not crossed
```

Therefore proxy-based transaction interception on `savePayment()` is
bypassed.

### Better design

``` text
CheckoutService
     |
     v
PaymentService Proxy
     |
     v
@Transactional savePayment()
```

Often the correct fix is to establish a real service/use-case boundary.

------------------------------------------------------------------------

## Q13. Why can private/final methods be problematic?

Spring's common transaction mechanism is proxy-based.

Class-based proxies need interceptable methods.

``` text
Proxy subclass
     |
     +--> override public/protected method
             |
             v
        transaction advice
```

Private methods are not externally intercepted entry points; final
methods cannot be overridden by subclass proxies.

Senior answer: do not put transactional design on methods that the
selected proxy mechanism cannot meaningfully intercept.

------------------------------------------------------------------------

## Q14. What are default rollback rules?

A critical interview question.

By default, Spring's declarative transaction behavior generally rolls
back for:

``` text
RuntimeException
Error
```

Checked exceptions do not automatically trigger rollback under the
default rule.

Example:

``` java
@Transactional
public void process() throws IOException {
    repository.save(...);
    throw new IOException();
}
```

Do not assume "any exception means rollback."

------------------------------------------------------------------------

## Q15. `rollbackFor` and `noRollbackFor`?

``` java
@Transactional(rollbackFor = IOException.class)
public void process() throws IOException {
    ...
}
```

or:

``` java
@Transactional(
    noRollbackFor = BusinessWarningException.class
)
```

### Senior rule

Do not use rollback configuration to hide poor exception modeling.

Ask:

``` text
Does this failure mean the unit of work
must be atomic and undone?
```

------------------------------------------------------------------------

# 3. Transaction Propagation --- 7 Questions

Propagation answers:

> What should happen if a transactional method calls another
> transactional method?

------------------------------------------------------------------------

## Q16. `REQUIRED`

Default propagation.

``` text
Existing transaction?
       |
   +---+---+
   |       |
  YES      NO
   |       |
 join    create new
```

Example:

``` text
OrderService.create() TX1
       |
       v
InventoryService.reserve()
@Transactional(REQUIRED)
       |
       v
joins TX1
```

``` text
TX1
|
+-- create order
+-- reserve inventory
+-- commit together
```

------------------------------------------------------------------------

## Q17. `REQUIRES_NEW`

``` text
Existing TX1
    |
    v
suspend TX1
    |
    v
start TX2
    |
execute method
    |
commit/rollback TX2
    |
resume TX1
```

Potential use:

``` text
Main transaction
     |
     +--> independent audit operation
```

But be careful.

``` text
TX1 holds DB connection
        |
        +--> REQUIRES_NEW needs another connection
```

Under concurrency, this can pressure the connection pool.

------------------------------------------------------------------------

## Q18. `NESTED`

Conceptually uses a nested transaction/savepoint model where supported.

``` text
TX1
 |
 +--> operation A
 |
 +--> SAVEPOINT
 |       |
 |       +--> operation B
 |              |
 |           failure
 |              |
 |       rollback to savepoint
 |
 +--> continue TX1
```

Do not equate `NESTED` with `REQUIRES_NEW`.

``` text
REQUIRES_NEW
independent physical transaction

NESTED
savepoint inside surrounding transaction
```

Support depends on transaction manager/resource capabilities.

------------------------------------------------------------------------

## Q19. `SUPPORTS`

``` text
Existing transaction?
       |
   +---+---+
   |       |
  YES      NO
   |       |
 join    execute non-transactionally
```

Useful when a method can participate in a transaction but does not
require one.

------------------------------------------------------------------------

## Q20. `MANDATORY`

``` text
Existing transaction?
       |
   +---+---+
   |       |
  YES      NO
   |       |
 join     ERROR
```

It enforces:

> This method must only be called inside a transaction.

------------------------------------------------------------------------

## Q21. `NOT_SUPPORTED` and `NEVER`

`NOT_SUPPORTED`:

``` text
existing TX
    |
 suspend
    |
execute without transaction
    |
resume TX
```

`NEVER`:

``` text
existing TX?
    |
   YES -> ERROR
   NO  -> execute without transaction
```

These are less common but important to understand conceptually.

------------------------------------------------------------------------

## Q22. What are propagation traps in production?

### Trap 1 --- Self invocation

``` text
this.methodWithRequiresNew()
         |
         X
proxy bypassed
```

So expected new transaction may never happen.

### Trap 2 --- Connection pool pressure

``` text
100 outer transactions
      |
each holds connection
      |
each calls REQUIRES_NEW
      |
needs 100 additional connections
```

Possible:

``` text
HikariCP exhausted
       |
threads wait
       |
latency spikes
```

### Trap 3 --- UnexpectedRollbackException-style behavior

An inner participating operation may mark the shared transaction
rollback-only, while outer code catches an exception and tries to
continue.

``` text
TX1
 |
 inner method fails
 |
 transaction marked rollback-only
 |
 outer catches exception
 |
 outer returns "success"
 |
 commit attempted
 |
ROLLBACK
```

Senior lesson: catching an exception does not necessarily restore a
transaction to a healthy state.

------------------------------------------------------------------------

# 4. Isolation / Locking --- 7 Questions

## Q23. What is transaction isolation?

Isolation defines how concurrent transactions observe/interfere with
each other's changes.

Standard isolation levels:

``` text
READ_UNCOMMITTED
       |
READ_COMMITTED
       |
REPEATABLE_READ
       |
SERIALIZABLE
```

Generally:

``` text
stronger isolation
      |
fewer concurrency anomalies
      |
potentially more coordination/contention
```

Exact behavior is database-dependent.

------------------------------------------------------------------------

## Q24. What is a dirty read?

``` text
Transaction A
-------------
UPDATE balance = 0
(not committed)

Transaction B
-------------
READ balance
sees 0

Transaction A
-------------
ROLLBACK
```

B read data that never became committed state.

------------------------------------------------------------------------

## Q25. What is a non-repeatable read?

``` text
TX A                     TX B

READ price = 100

                         UPDATE price = 120
                         COMMIT

READ price again = 120
```

Same row, same transaction, different committed value between reads.

------------------------------------------------------------------------

## Q26. What is a phantom read?

``` text
TX A
SELECT * FROM orders
WHERE status='NEW'

returns 10 rows

TX B
INSERT new order status='NEW'
COMMIT

TX A repeats query

returns 11 rows
```

A new matching row appears.

Again, actual behavior varies by database implementation/isolation
model.

------------------------------------------------------------------------

## Q27. What is a lost update?

Classic application-level race:

``` text
Initial stock = 10

TX A                     TX B
read 10                  read 10

sell 1                   sell 1

write 9                  write 9

Expected = 8
Actual   = 9
```

A transaction alone does not automatically mean your application-level
read-modify-write logic is safe under every isolation level.

Solutions can include:

-   atomic SQL update
-   optimistic locking
-   pessimistic locking
-   appropriate isolation
-   serialization at business-key level

------------------------------------------------------------------------

## Q28. Optimistic vs pessimistic locking?

### Optimistic

Assume conflicts are relatively rare.

``` text
Read row
version = 5
    |
modify
    |
UPDATE ...
WHERE id = ?
AND version = 5
    |
 +-- updated 1 row -> success
 |
 +-- updated 0 rows -> conflict
```

JPA:

``` java
@Version
private long version;
```

### Pessimistic

Acquire database lock.

``` text
TX A
SELECT ... FOR UPDATE
      |
      v
row locked
      |
TX B waits
```

Comparison:

``` text
OPTIMISTIC
+ high concurrency
+ no long lock wait in common case
- conflict requires retry/rejection

PESSIMISTIC
+ prevents competing updates directly
- blocking
- deadlock risk
- lower concurrency
```

------------------------------------------------------------------------

## Q29. How do database deadlocks occur?

``` text
TX A
lock row 1
    |
wait row 2

TX B
lock row 2
    |
wait row 1
```

``` text
TX A -------- waits --------> TX B lock
 ^                              |
 |                              |
 +--------- waits --------------+
```

Databases typically detect a deadlock and abort one transaction.

Mitigation:

-   consistent access order
-   short transactions
-   correct indexes
-   avoid unnecessary lock scope
-   retry deadlock victims carefully
-   reduce contention

------------------------------------------------------------------------

# 5. Boundaries / Distributed Systems --- 6 Questions

## Q30. Why are long transactions dangerous?

``` text
BEGIN
  |
update rows
  |
call remote API 5 seconds
  |
perform CPU work
  |
sleep/retry
  |
COMMIT
```

During this time:

``` text
DB connection held
locks may be held
MVCC/version cleanup pressure may increase
contention grows
failure window grows
```

Preferred:

``` text
Keep DB transaction
focused and short
```

------------------------------------------------------------------------

## Q31. Why is a remote API call inside a DB transaction risky?

``` java
@Transactional
public void createOrder() {
    orderRepository.save(...);

    paymentClient.charge(); // network

    inventoryRepository.update(...);
}
```

Failure possibilities:

``` text
DB TX open
   |
remote call takes 20 sec
   |
connection held
   |
locks held
   |
threads accumulate
```

Worse:

``` text
Payment succeeds
      |
DB commit fails
      |
payment exists but order not committed
```

A local DB transaction cannot roll back the external payment service.

This is a distributed workflow problem.

------------------------------------------------------------------------

## Q32. What happens with `@Async` and transactions?

Transactions are commonly associated with execution context/thread-bound
resources.

``` text
Thread A
@Transactional
    |
    +--> submit @Async work
              |
              v
           Thread B
```

Thread B does not simply inherit Thread A's transaction.

Conceptually:

``` text
TX context
belongs to execution context
        |
cross async boundary
        |
must establish appropriate
transaction semantics separately
```

Do not assume a transaction flows across arbitrary thread boundaries.

------------------------------------------------------------------------

## Q33. What are transactional application events?

You may want event handling tied to transaction outcome.

Example requirement:

``` text
Only send internal follow-up
after DB transaction commits
```

Conceptually:

``` text
TX begins
   |
save order
   |
publish application event
   |
TX commits
   |
AFTER_COMMIT listener
```

Spring provides transaction-aware event listener mechanisms for
in-process handling.

### Critical distinction

``` text
AFTER_COMMIT Spring listener
      !=
durable distributed message guarantee
```

If process crashes at the wrong point, an external message can still be
lost unless you use a durable pattern.

------------------------------------------------------------------------

## Q34. What is the transactional outbox pattern?

Problem:

``` java
@Transactional
void createOrder() {
    orderRepository.save(order);
}

kafka.send("OrderCreated");
```

Failure window:

``` text
DB commit succeeds
      |
process crashes
      |
Kafka send never happens
```

Outbox:

``` text
              ONE DB TRANSACTION
          +-------------------------+
          |                         |
          v                         v
     Orders Table              Outbox Table
     insert order              insert event
          |                         |
          +------------+------------+
                       |
                     COMMIT
                       |
                       v
               Outbox Publisher
                       |
                       v
                     Kafka
```

Now business state and intent-to-publish are atomic in one local
database transaction.

Consumer side should still be designed for duplicate
delivery/idempotency.

This pattern is covered more deeply in CH10/CH11.

------------------------------------------------------------------------

## Q35. How do you debug transaction problems in production?

Use a structured path.

``` text
SYMPTOM
 |
 +--> data committed unexpectedly?
 |
 +--> data rolled back unexpectedly?
 |
 +--> lock timeout?
 |
 +--> deadlock?
 |
 +--> connection pool exhausted?
 |
 +--> duplicate operation?
 |
 +--> transaction too long?
```

Then:

``` text
1. Identify service method / use case
        |
2. Is method actually proxied?
        |
3. What @Transactional metadata applies?
        |
4. Propagation?
        |
5. Isolation?
        |
6. Exception type?
        |
7. Was exception swallowed?
        |
8. rollback-only state?
        |
9. DB locks / transaction duration?
        |
10. Hikari active/pending?
        |
11. remote calls inside transaction?
        |
12. async/thread boundary?
```

------------------------------------------------------------------------

# Senior Scenario 1 --- Checked Exception Commits Unexpectedly

``` java
@Transactional
public void importFile() throws IOException {

    repository.save(record);

    throw new IOException("file failure");
}
```

Developer expects rollback.

But default rollback behavior does not treat every checked exception as
rollback-triggering.

Fix only if semantically correct:

``` java
@Transactional(rollbackFor = IOException.class)
```

Senior question:

> Should an IOException necessarily invalidate the database work?

Answer depends on the business unit of work.

------------------------------------------------------------------------

# Senior Scenario 2 --- Catching Exception Does Not Save Transaction

``` java
@Transactional
public void checkout() {

    try {
        paymentRepository.save(...);
        inventoryService.reserve();
    } catch (RuntimeException ex) {
        log.warn("ignored", ex);
    }

    orderRepository.save(...);
}
```

If a participating transactional operation marks the transaction
rollback-only:

``` text
TX
 |
failure
 |
rollback-only
 |
exception caught
 |
continue work
 |
attempt commit
 |
ROLLBACK
```

The correct solution is not blindly catching transactional failures.

------------------------------------------------------------------------

# Senior Scenario 3 --- `REQUIRES_NEW` Exhausts Hikari

Configuration:

``` text
Hikari max = 20
Request threads = 20
```

Each request:

``` text
Outer TX
  |
holds connection
  |
calls REQUIRES_NEW
  |
needs second connection
```

At concurrency 20:

``` text
20 outer TXs
|
20 connections occupied
|
20 inner TXs ask for connection
|
NONE AVAILABLE
|
all wait
```

Potential self-inflicted pool starvation.

This is why propagation decisions affect capacity planning.

------------------------------------------------------------------------

# Senior Scenario 4 --- Payment + Order Consistency

Naive:

``` text
BEGIN DB TX
   |
insert order
   |
call Payment Service
   |
payment succeeds
   |
COMMIT DB
   |
commit fails
```

Result:

``` text
Payment = SUCCESS
Order DB = MISSING
```

You cannot solve this by increasing the isolation level.

This is a distributed consistency problem.

Possible design:

``` text
Order created PENDING
      |
      v
publish PaymentRequested
      |
      v
Payment Service
      |
  +---+---+
  |       |
success failure
  |       |
  v       v
PAID    FAILED
```

Use Saga/idempotency/outbox as appropriate.

------------------------------------------------------------------------

# Senior Scenario 5 --- Lost Update on Inventory

``` text
stock = 1

Request A                Request B
---------                ---------
read stock=1             read stock=1
stock > 0                stock > 0
write stock=0            write stock=0
commit                   commit
```

Two orders sold one item.

Potential solutions:

### Atomic SQL

``` sql
UPDATE inventory
SET stock = stock - 1
WHERE product_id = ?
  AND stock > 0;
```

Then check affected row count.

### Optimistic locking

``` text
version conflict
    |
retry / reject
```

### Pessimistic locking

``` text
lock inventory row
    |
serialize competing update
```

Choose based on contention and business semantics.

------------------------------------------------------------------------

# Senior Scenario 6 --- Transaction Around Slow Remote Service

``` java
@Transactional
public void updateCustomer() {
    customerRepository.update(...);
    fraudClient.check(...); // 4 seconds
}
```

At 200 concurrent requests:

``` text
200 request threads
        |
        v
transactions stay open
        |
        v
Hikari max 30
        |
        v
30 connections occupied
        |
        v
170 requests wait
        |
        v
p99 explodes
```

Possible redesign:

``` text
remote check first
     |
short DB transaction
     |
commit
```

or redesign the workflow asynchronously if the business permits.

------------------------------------------------------------------------

# Mini Coding Drill 1 --- Basic Transaction Boundary

``` java
@Service
public class TransferService {

    private final AccountRepository accounts;

    public TransferService(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Transactional
    public void transfer(
            long fromId,
            long toId,
            BigDecimal amount) {

        Account from =
            accounts.findById(fromId).orElseThrow();

        Account to =
            accounts.findById(toId).orElseThrow();

        from.debit(amount);
        to.credit(amount);
    }
}
```

With JPA managed entities, dirty checking may persist changes at
flush/commit.

Be prepared to discuss concurrent transfers and locking.

------------------------------------------------------------------------

# Mini Coding Drill 2 --- Optimistic Lock

``` java
@Entity
class Product {

    @Id
    private Long id;

    private int stock;

    @Version
    private long version;
}
```

Conceptual SQL:

``` sql
UPDATE product
SET stock = ?, version = 6
WHERE id = ?
  AND version = 5;
```

``` text
rows updated = 1
      |
success

rows updated = 0
      |
concurrent modification
```

------------------------------------------------------------------------

# Mini Coding Drill 3 --- Outbox

``` java
@Transactional
public void createOrder(CreateOrder command) {

    Order order = Order.create(command);

    orderRepository.save(order);

    outboxRepository.save(
        OutboxEvent.of(
            "OrderCreated",
            order.getId()
        )
    );
}
```

Then separately:

``` text
Outbox Poller / CDC
       |
       v
unpublished events
       |
       v
Kafka
       |
       v
mark/track publication
```

The publisher must tolerate retries/duplicates.

------------------------------------------------------------------------

# Mini Coding Drill 4 --- Explicit Rollback Rule

``` java
@Transactional(
    rollbackFor = PaymentProcessingException.class
)
public void checkout()
        throws PaymentProcessingException {

    ...
}
```

Interview follow-up:

Why is `rollbackFor = Exception.class` everywhere often a poor default?

Because transaction semantics should reflect the business failure model,
and overly broad rules can hide design/exception-boundary problems.

------------------------------------------------------------------------

# Propagation Visual Cheat Sheet

``` text
Caller TX?        REQUIRED
--------------------------
YES               JOIN
NO                NEW


Caller TX?        REQUIRES_NEW
------------------------------
YES               SUSPEND + NEW
NO                NEW


Caller TX?        SUPPORTS
--------------------------
YES               JOIN
NO                NONE


Caller TX?        MANDATORY
---------------------------
YES               JOIN
NO                ERROR


Caller TX?        NOT_SUPPORTED
-------------------------------
YES               SUSPEND + NONE
NO                NONE


Caller TX?        NEVER
-------------------------
YES               ERROR
NO                NONE
```

`NESTED`:

``` text
Outer TX
   |
SAVEPOINT
   |
nested work
   |
rollback to savepoint if required
```

------------------------------------------------------------------------

# Isolation Visual Cheat Sheet

``` text
Isolation             Dirty   Non-repeatable   Phantom
-------------------------------------------------------
READ_UNCOMMITTED       possible possible       possible
READ_COMMITTED         prevented possible      possible
REPEATABLE_READ        prevented prevented*    DB-dependent*
SERIALIZABLE           strongest standard isolation
```

`*` Exact observable behavior depends on the database's
concurrency-control implementation. Do not memorize a table without
knowing your database.

Senior interview answer:

> Isolation level names are standardized, but implementation details and
> anomaly behavior must be verified for the actual database, such as
> PostgreSQL or MySQL/InnoDB.

------------------------------------------------------------------------

# Transaction + JPA Mental Model

``` text
@Transactional Service
        |
        v
Transaction begins
        |
        v
EntityManager / Persistence Context
        |
        +--> load Entity A
        +--> load Entity B
        |
        v
modify Java objects
        |
        v
dirty checking
        |
        v
FLUSH
        |
        v
SQL
        |
        v
COMMIT
```

This becomes central in CH07.

------------------------------------------------------------------------

# Transaction + Connection Pool Mental Model

``` text
Request
   |
@Transactional
   |
   v
Acquire DB Connection
   |
   v
BEGIN
   |
business work
   |
   +--> SQL
   |
   +--> slow remote call?  <-- dangerous
   |
   v
COMMIT
   |
   v
Return connection to Hikari
```

Transaction duration directly affects connection-pool occupancy.

------------------------------------------------------------------------

# Transaction + Thread Boundary

``` text
Thread A
|
@Transactional
|
TX context / DB resources
|
+---- async task ----> Thread B
                       |
                       X not automatically
                         same transaction
```

Never casually assume transactional context crosses executors.

------------------------------------------------------------------------

# Transaction + Distributed System Mental Model

``` text
              Local Transaction Boundary
          +-------------------------------+
          |                               |
          v                               v
       Order DB                      Outbox DB
          |                               |
          +---------------+---------------+
                          |
                        COMMIT
                          |
                          v
                    Message Relay
                          |
                          v
                        Kafka
                          |
                          v
                   Payment Service
                          |
                          v
                      Payment DB
```

Each service can preserve local ACID while the overall workflow reaches
consistency through messaging and business state transitions.

------------------------------------------------------------------------

# Production Debugging --- Lock Wait

``` text
API latency ↑
    |
trace shows DB call slow
    |
DB CPU normal?
    |
    v
check active transactions / locks
    |
    v
query waiting for lock
    |
    v
who owns lock?
    |
    v
long-running transaction
    |
    +--> remote call inside TX?
    +--> batch transaction?
    +--> missing index causing broad lock impact?
    +--> competing update?
```

------------------------------------------------------------------------

# Production Debugging --- Hikari Exhaustion

``` text
Hikari pending threads ↑
       |
active connections = max
       |
       v
What holds connections?
       |
       +--> long SQL
       +--> long transaction
       +--> remote call inside TX
       +--> REQUIRES_NEW
       +--> lock waits
       +--> leaked/manual connection
```

Do not simply increase pool size before understanding database capacity
and connection hold time.

------------------------------------------------------------------------

# Production Debugging --- Unexpected Commit

``` text
Data committed unexpectedly
       |
       v
Was method proxied?
       |
       v
Was transaction active?
       |
       v
Exception propagated?
       |
       v
Checked or runtime?
       |
       v
rollbackFor?
       |
       v
Exception swallowed?
```

------------------------------------------------------------------------

# Production Debugging --- Unexpected Rollback

``` text
Unexpected rollback
      |
      v
Inner participating method failed?
      |
      v
Transaction marked rollback-only?
      |
      v
Exception caught by outer method?
      |
      v
Propagation?
      |
      v
DB constraint/flush failure?
```

------------------------------------------------------------------------

# Critical Comparison Sheet

## `REQUIRED` vs `REQUIRES_NEW`

``` text
REQUIRED
join caller transaction
one atomic unit

REQUIRES_NEW
suspend caller
independent transaction
extra connection/resource pressure possible
```

------------------------------------------------------------------------

## `REQUIRES_NEW` vs `NESTED`

``` text
REQUIRES_NEW
independent transaction

NESTED
savepoint within outer transaction
when supported
```

------------------------------------------------------------------------

## Optimistic vs Pessimistic Locking

``` text
OPTIMISTIC
conflict detected at update
best when conflicts relatively rare

PESSIMISTIC
lock before competing modification
best when conflict cost/probability justifies blocking
```

------------------------------------------------------------------------

## Local Transaction vs Saga

``` text
LOCAL TRANSACTION
one resource boundary
ACID commit/rollback

SAGA
multiple distributed local transactions
business compensation/state transitions
eventual consistency
```

------------------------------------------------------------------------

## Transaction vs Synchronization

``` text
Java synchronized
    |
coordinates threads in one JVM

DB transaction / lock
    |
coordinates database state/concurrency

Distributed workflow
    |
coordinates multiple services/resources
```

Do not confuse these levels.

------------------------------------------------------------------------

# Senior Rapid-Fire Follow-Ups

1.  What is a transaction?
2.  Explain ACID.
3.  Atomicity vs consistency?
4.  What is a transaction boundary?
5.  What is autocommit?
6.  What is `PlatformTransactionManager`?
7.  How does `@Transactional` work?
8.  What role does the proxy play?
9.  When does commit happen?
10. JPA `save()` vs transaction commit?
11. Why does self-invocation matter?
12. Why can private/final transactional methods be problematic?
13. Default rollback rules?
14. Checked exception rollback?
15. What does `rollbackFor` do?
16. `REQUIRED`?
17. `REQUIRES_NEW`?
18. `NESTED`?
19. `SUPPORTS`?
20. `MANDATORY`?
21. `NOT_SUPPORTED`?
22. Why can `REQUIRES_NEW` exhaust Hikari?
23. What does rollback-only mean?
24. What is isolation?
25. Dirty read?
26. Non-repeatable read?
27. Phantom read?
28. Lost update?
29. Optimistic vs pessimistic locking?
30. How do DB deadlocks happen?
31. Why avoid long transactions?
32. Why avoid remote calls inside DB transactions?
33. Does transaction context cross `@Async`?
34. Why doesn't `afterCommit` guarantee Kafka publication?
35. Explain transactional outbox.

------------------------------------------------------------------------

# Interview Checklist

``` text
FUNDAMENTALS
[ ] 01 Transaction
[ ] 02 ACID
[ ] 03 Atomicity vs consistency
[ ] 04 Local vs distributed
[ ] 05 Transaction boundary
[ ] 06 Autocommit
[ ] 07 PlatformTransactionManager

@Transactional INTERNALS
[ ] 08 Proxy mechanism
[ ] 09 TransactionInterceptor
[ ] 10 Commit flow
[ ] 11 Class vs method metadata
[ ] 12 Self-invocation
[ ] 13 private/final limitations
[ ] 14 Default rollback
[ ] 15 rollbackFor/noRollbackFor

PROPAGATION
[ ] 16 REQUIRED
[ ] 17 REQUIRES_NEW
[ ] 18 NESTED
[ ] 19 SUPPORTS
[ ] 20 MANDATORY
[ ] 21 NOT_SUPPORTED / NEVER
[ ] 22 Production traps

ISOLATION / LOCKING
[ ] 23 Isolation
[ ] 24 Dirty read
[ ] 25 Non-repeatable read
[ ] 26 Phantom read
[ ] 27 Lost update
[ ] 28 Optimistic / pessimistic
[ ] 29 Deadlocks

DISTRIBUTED / PRODUCTION
[ ] 30 Long transactions
[ ] 31 Remote calls inside TX
[ ] 32 Async/thread boundary
[ ] 33 Transactional events
[ ] 34 Outbox
[ ] 35 Production debugging
```

------------------------------------------------------------------------

# Chapter 06 Visual Summary

``` text
                    SPRING TRANSACTIONS
                            |
          +-----------------+-----------------+
          |                                   |
       SPRING                               DATABASE
          |                                   |
          v                                   v
   @Transactional                          BEGIN
          |                                   |
          v                                   v
        Proxy                           Connection / TX
          |                                   |
          v                                   v
TransactionInterceptor                  SQL / Locks
          |                                   |
          v                                   v
Propagation / Isolation             Commit / Rollback
          |
          +------------------+
          |                  |
          v                  v
     Exception rules     Transaction boundary
          |                  |
          v                  v
       rollback?        Keep it SHORT
```

Distributed extension:

``` text
Spring DB Transaction
       |
       +--> Order Table
       |
       +--> Outbox Table
       |
       v
     COMMIT
       |
       v
Message Relay
       |
       v
Kafka
       |
       v
Other Service
```

------------------------------------------------------------------------

# Chapter 06 Exit Criteria

You are ready for **CH 07 --- JPA / Hibernate** when you can explain
without notes:

1.  ACID with a real backend example.
2.  Local vs distributed transactions.
3.  The Spring `@Transactional` proxy flow.
4.  `TransactionInterceptor` and transaction manager roles.
5.  Why self-invocation breaks expected transaction advice.
6.  Default rollback rules.
7.  All important propagation modes.
8.  `REQUIRED` vs `REQUIRES_NEW` vs `NESTED`.
9.  Why `REQUIRES_NEW` can affect Hikari capacity.
10. Dirty/non-repeatable/phantom reads.
11. Lost updates.
12. Optimistic vs pessimistic locking.
13. How database deadlocks happen.
14. Why long transactions hurt production systems.
15. Why remote calls inside transactions are dangerous.
16. Why transaction context does not simply cross async threads.
17. Why local ACID cannot atomically commit another microservice.
18. How transactional outbox closes the DB/message failure window.
19. How to debug unexpected commit/rollback.
20. How transactions connect to JPA, HikariCP, Kafka and distributed
    consistency.

``` text
CH 06 SPRING TRANSACTIONS
        |
        v
ACID
        |
        v
@Transactional Proxy ★★★
        |
        v
Propagation ★★★
        |
        v
Isolation
        |
        v
Locking / Concurrency
        |
        v
Transaction Boundaries
        |
        v
Outbox / Distributed Consistency ★★★
        |
        v
READY FOR CH 07
JPA / HIBERNATE
```

------------------------------------------------------------------------

## Next Chapter

``` text
CH 07 — JPA / HIBERNATE (~45 Q)
|
+-- ORM / JPA / Hibernate
+-- Entity lifecycle
+-- Persistence Context
+-- First-level cache
+-- Dirty checking / flush
+-- Relationships
+-- Lazy vs eager
+-- N+1 problem
+-- JOIN FETCH / EntityGraph
+-- Cascade / orphanRemoval
+-- Optimistic / pessimistic locking
+-- @Version
+-- Batch operations
+-- Production performance scenarios
```
