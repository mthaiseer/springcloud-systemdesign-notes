# 008_Transactions_AOP_Proxy_Model.md

# MiniSpringBoot Deep Production Mode
# 008 - Transactions AOP Proxy Model

## One Core Mental Model

> `@Transactional` does not put transaction code inside your method.
>
> `@Transactional` tells Spring to create a proxy boundary around your method call.
>
> The proxy opens the transaction before your method runs and commits or rolls back after your method finishes.

This chapter teaches exactly ONE core mental model:

```text
@Transactional
      =
Proxy-managed transaction boundary
around a method call
```

Do not think:

```text
My method contains transaction logic
```

Think:

```text
My method is called through a transaction gate
```

That gate is the Spring AOP proxy.

---

## Why This Exists

A real backend system is not just code execution.

It is state change.

When you build systems such as:

```text
Bank transfer
Order checkout
Inventory reservation
Wallet debit
Payment capture
Subscription renewal
Invoice generation
```

you are not merely calling methods.

You are changing durable state in a database.

If half the state change succeeds and half fails, the system becomes corrupt.

Example:

```text
Debit user wallet        SUCCESS
Create order             SUCCESS
Reserve inventory        FAILED
Send confirmation email  NOT REACHED
```

Now the user lost money but did not get the order.

That is why transactions exist.

But Spring has a design constraint:

```text
Spring should not rewrite your business method source code.
```

So Spring needs a way to run logic before and after your method.

That mechanism is:

```text
AOP Proxy
```

The transaction itself is managed around the method call.

---

## Problem Statement

Suppose we have this service:

```java
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final LedgerRepository ledgerRepository;

    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {

        walletRepository.debit(fromUserId, amount);

        walletRepository.credit(toUserId, amount);

        ledgerRepository.insertTransferRecord(fromUserId, toUserId, amount);
    }
}
```

Without transaction protection, this can fail halfway.

Failure case:

```text
debit(fromUserId)                 SUCCESS
credit(toUserId)                  FAILED
insertTransferRecord(...)         NOT EXECUTED
```

Result:

```text
Money disappeared.
Ledger does not explain it.
Production incident.
```

What we want:

```text
Either all database changes succeed

OR

all database changes rollback
```

Spring solution:

```java
@Transactional
public void transfer(...) {
    ...
}
```

But the important question is:

```text
Where does the transaction actually start?
```

Not inside your method.

It starts in the proxy before your method is invoked.

---

## Mental Model

Think of a secure bank vault.

A cashier cannot directly modify the final ledger.

The cashier must enter a protected transaction room.

Inside the room:

```text
Step 1: Open temporary worksheet
Step 2: Write all changes
Step 3: If everything valid, publish to ledger
Step 4: If error, destroy worksheet
```

Spring transaction is the same.

```text
Proxy
  |
  v
Open Transaction Workspace
  |
  v
Run Business Method
  |
  v
Commit or Rollback
```

Your method is the cashier's work.

The proxy is the transaction room door.

The database transaction is the protected workspace.

---

## Core Concepts

This chapter uses only the concepts needed for the transaction proxy mental model.

```text
@Transactional
```

A marker that says:

```text
This method should run inside a transaction boundary.
```

```text
Proxy
```

The object Spring exposes to callers instead of exposing the raw target object directly.

```text
TransactionInterceptor
```

The logic that runs before and after your method.

```text
PlatformTransactionManager
```

Spring's abstraction for starting, committing, and rolling back transactions.

```text
Connection
```

The database connection that carries the transaction.

```text
ThreadLocal Transaction Context
```

The per-thread storage Spring uses so repositories on the same thread use the same transaction connection.

```text
Target Method
```

Your real business method.

---

## Internal Architecture

High-level architecture:

```text
                      HTTP REQUEST
                           |
                           v
                     Controller Bean
                           |
                           v
                 Transactional Service Proxy
                           |
                           v
                  TransactionInterceptor
                           |
                           v
              PlatformTransactionManager
                           |
                           v
                   JDBC Connection / EntityManager
                           |
                           v
                    Real Service Method
                           |
                           v
                       Repositories
                           |
                           v
                         Database
```

The key is this:

```text
Controller does not call the raw service directly.

Controller calls the proxy.

Proxy calls the real service.
```

---

## Internal Working

When Spring sees:

```java
@Transactional
public void transfer(...) {
    ...
}
```

Spring mentally creates something like:

```java
public class WalletServiceProxy {

    private final WalletService target;
    private final PlatformTransactionManager txManager;

    public void transfer(...) {

        TransactionStatus status = txManager.begin();

        try {
            target.transfer(...);
            txManager.commit(status);
        }
        catch (RuntimeException ex) {
            txManager.rollback(status);
            throw ex;
        }
    }
}
```

This is simplified, but the mental model is accurate.

The transaction code is not inside `WalletService`.

It is around `WalletService`.

That difference explains:

```text
Why self-invocation fails
Why private methods do not work
Why final methods can break proxying
Why transaction starts before method
Why rollback happens after exception escapes method
```

---

## Step-by-Step Flow

Normal successful transaction:

```text
1. Client calls service method.

2. Call reaches Spring proxy.

3. Proxy detects transactional metadata.

4. Proxy asks transaction manager to begin transaction.

5. Transaction manager obtains or reuses database connection.

6. Spring binds connection to current thread.

7. Proxy invokes real business method.

8. Repository uses thread-bound connection.

9. Method completes successfully.

10. Proxy tells transaction manager to commit.

11. Database makes changes durable.

12. Connection is released.

13. Response returns to caller.
```

ASCII:

```text
Client
  |
  v
Proxy
  |
  v
BEGIN TX
  |
  v
Real Method
  |
  v
Repository
  |
  v
Database
  |
  v
COMMIT
```

Failure transaction:

```text
Client
  |
  v
Proxy
  |
  v
BEGIN TX
  |
  v
Real Method
  |
  v
Exception
  |
  v
ROLLBACK
```

---

## Deep Walkthrough Example

Use an order checkout.

Business logic:

```text
Create Order
Reserve Inventory
Save Payment Attempt
```

Code:

```java
@Transactional
public void checkout(Long userId, Long productId) {

    orderRepository.createOrder(userId, productId);

    inventoryRepository.reserve(productId);

    paymentRepository.createPaymentAttempt(userId, productId);
}
```

What junior developers imagine:

```text
checkout() itself knows transaction.
```

What actually happens:

```text
Proxy knows transaction.
checkout() knows business logic.
```

Deep flow:

```text
Controller
    |
    v
CheckoutServiceProxy.checkout()
    |
    v
TransactionInterceptor
    |
    v
BEGIN
    |
    v
CheckoutService.checkout()
    |
    +--> orderRepository.createOrder()
    |
    +--> inventoryRepository.reserve()
    |
    +--> paymentRepository.createPaymentAttempt()
    |
    v
Return normally
    |
    v
COMMIT
```

Failure:

```text
orderRepository.createOrder()       SUCCESS
inventoryRepository.reserve()       SUCCESS
paymentRepository.createPayment()   THROWS RuntimeException
```

Proxy sees exception.

```text
Exception escapes method
        |
        v
TransactionInterceptor catches it
        |
        v
ROLLBACK
```

Database state returns to before checkout.

---

## Rich ASCII Diagrams

### Diagram 1 - Transaction Boundary

```text
                         TRANSACTION BOUNDARY

                +----------------------------------+
                |                                  |
                |   orderRepository.save()         |
                |                                  |
                |   inventoryRepository.reserve()  |
                |                                  |
                |   paymentRepository.save()       |
                |                                  |
                +----------------------------------+

                         COMMIT OR ROLLBACK
```

The boundary is not drawn by your method.

The boundary is created by the proxy.

---

### Diagram 2 - Proxy Gate

```text
                 CALLER

                   |
                   v

        +-----------------------+
        |  TRANSACTION PROXY    |
        +-----------------------+
        | begin transaction     |
        | call target method    |
        | commit / rollback     |
        +-----------------------+

                   |
                   v

        +-----------------------+
        |   REAL SERVICE        |
        +-----------------------+
        | business logic only   |
        +-----------------------+
```

---

### Diagram 3 - ThreadLocal Context

```text
Thread-42
    |
    v
+-------------------------------+
| Transaction Context            |
|                               |
| Connection = PostgresConn-17  |
| AutoCommit = false            |
| Status = ACTIVE               |
+-------------------------------+

Repository calls on Thread-42
reuse PostgresConn-17
```

This is why transaction context normally stays within the same thread.

---

### Diagram 4 - Self Invocation Failure

```text
Client
  |
  v
Proxy
  |
  v
process()
  |
  v
this.transfer()
  |
  v
Real transfer()

Proxy not involved.
TransactionInterceptor not invoked.
No transaction boundary created.
```

---

### Diagram 5 - External Bean Call Works

```text
OrderService
    |
    v
PaymentService Proxy
    |
    v
BEGIN TX
    |
    v
PaymentService.pay()
    |
    v
COMMIT
```

Different bean call hits proxy.

Internal same-class call bypasses proxy.

---

## Data Structures & Algorithms Used

Spring transaction management is not about complex algorithms.

It is mostly about:

```text
Interception
Context lookup
Resource binding
State transition
```

### ThreadLocal

Spring binds transaction resources to the current thread.

Conceptual map:

```text
ThreadLocal<TransactionContext>

Thread-1 -> TxContext(Connection-1)
Thread-2 -> TxContext(Connection-2)
Thread-3 -> no transaction
```

Lookup complexity:

```text
O(1)
```

### Transaction Status Object

Spring tracks:

```text
Is new transaction?
Rollback-only?
Completed?
Suspended resources?
Savepoint?
```

Conceptually:

```text
TransactionStatus {
    boolean newTransaction;
    boolean rollbackOnly;
    boolean completed;
    Object savepoint;
}
```

### Resource Map

For each thread, Spring can bind resources:

```text
DataSource-A -> Connection-A
EntityManagerFactory -> EntityManager
```

Conceptual map:

```text
ThreadLocal Map<ResourceKey, ResourceHolder>
```

### Transaction State Machine

Normal:

```text
NONE
  |
BEGIN
  |
ACTIVE
  |
COMMITTING
  |
COMMITTED
```

Failure:

```text
ACTIVE
  |
EXCEPTION
  |
ROLLING_BACK
  |
ROLLED_BACK
```

Rollback-only:

```text
ACTIVE
  |
MARK_ROLLBACK_ONLY
  |
METHOD_RETURNS
  |
ROLLBACK_INSTEAD_OF_COMMIT
```

This is the mental state machine behind many confusing transaction bugs.

---

## Java Code Example (with execution explanation)

### Minimal Java Example

```java
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final LedgerRepository ledgerRepository;

    @Transactional
    public void transfer(Long fromUserId,
                         Long toUserId,
                         BigDecimal amount) {

        walletRepository.debit(fromUserId, amount);

        walletRepository.credit(toUserId, amount);

        ledgerRepository.recordTransfer(
                fromUserId,
                toUserId,
                amount
        );
    }
}
```

Execution explanation:

```text
1. Caller calls walletService.transfer(...)

2. walletService is actually a proxy object.

3. Proxy starts transaction.

4. Real WalletService.transfer() executes.

5. All repositories use same transaction-bound DB connection.

6. If method returns normally:
      commit

7. If RuntimeException escapes:
      rollback
```

Important:

```text
@Transactional does not wrap each repository call separately.

It wraps the whole service method.
```

---

### What Spring Mentally Executes

```java
public Object invoke(MethodInvocation invocation) {

    TransactionStatus status =
            transactionManager.getTransaction(txDefinition);

    try {
        Object result = invocation.proceed();

        transactionManager.commit(status);

        return result;
    }
    catch (RuntimeException ex) {
        transactionManager.rollback(status);
        throw ex;
    }
}
```

This is the core mental algorithm.

```text
begin
try method
commit if success
rollback if failure
```

---

## Spring Boot Example (with internal flow)

### Controller

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final CheckoutService checkoutService;

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(
            @RequestBody CheckoutRequest request) {

        checkoutService.checkout(
                request.userId(),
                request.productId()
        );

        return ResponseEntity.ok("order created");
    }
}
```

### Service

```java
@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentAttemptRepository paymentAttemptRepository;

    @Transactional
    public void checkout(Long userId, Long productId) {

        orderRepository.insertOrder(userId, productId);

        inventoryRepository.reserve(productId);

        paymentAttemptRepository.insertAttempt(userId, productId);
    }
}
```

### Repository

```java
@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public void insertOrder(Long userId, Long productId) {

        jdbcTemplate.update(
            "insert into orders(user_id, product_id, status) values (?, ?, ?)",
            userId,
            productId,
            "CREATED"
        );
    }
}
```

Internal flow:

```text
HTTP POST /orders/checkout
        |
        v
DispatcherServlet
        |
        v
OrderController.checkout()
        |
        v
CheckoutService Proxy
        |
        v
TransactionInterceptor
        |
        v
DataSourceTransactionManager.begin()
        |
        v
Connection acquired from HikariCP
        |
        v
Connection autoCommit=false
        |
        v
ThreadLocal binds Connection
        |
        v
Real CheckoutService.checkout()
        |
        v
Repositories use same Connection
        |
        v
commit or rollback
```

---

## Production Architecture Example

Single-service transaction architecture:

```text
                 CLIENT
                   |
                   v
              API GATEWAY
                   |
                   v
          Spring Boot Checkout Service
                   |
                   v
          Transactional Service Proxy
                   |
                   v
        +----------+-----------+
        |          |           |
        v          v           v
     Orders    Inventory    Payments
     Table       Table       Table
        \          |          /
         \         |         /
          v        v        v
              PostgreSQL
```

All tables are in one database.

This is where local ACID transactions shine.

---

Distributed architecture:

```text
Checkout Service
      |
      +--> Order DB
      |
      +--> Payment Service
      |
      +--> Inventory Service
```

A local `@Transactional` boundary cannot safely cover all remote systems.

For that, production systems usually use:

```text
Outbox Pattern
Saga Pattern
Idempotency Keys
Compensation
Eventual Consistency
```

Do not confuse:

```text
Local DB transaction
```

with:

```text
Distributed business transaction
```

This chapter focuses on local Spring transaction boundary.

---

## Sequence Diagram (ASCII)

Successful request:

```text
Client            Controller       Proxy        TxManager        Service        Repository       DB
  |                   |              |              |               |              |            |
  | POST /checkout    |              |              |               |              |            |
  |------------------>|              |              |               |              |            |
  |                   | checkout()   |              |               |              |            |
  |                   |------------->|              |               |              |            |
  |                   |              | begin()      |               |              |            |
  |                   |              |------------->|               |              |            |
  |                   |              |              | get conn      |              |            |
  |                   |              |              |-------------->|              |            |
  |                   |              | invoke real  |               |              |            |
  |                   |              |---------------------------->|              |            |
  |                   |              |              |               | save order   |            |
  |                   |              |              |               |------------->|            |
  |                   |              |              |               |              | INSERT     |
  |                   |              |              |               |              |----------->|
  |                   |              | commit()     |               |              |            |
  |                   |              |------------->|               |              |            |
  |                   | response     |              |               |              |            |
  |<------------------|              |              |               |              |            |
```

Failure request:

```text
Client       Controller       Proxy       TxManager       Service       Repository       DB
  |              |             |             |              |              |            |
  | request      |             |             |              |              |            |
  |------------->|             |             |              |              |            |
  |              | call svc    |             |              |              |            |
  |              |------------>|             |              |              |            |
  |              |             | begin       |              |              |            |
  |              |             |------------>|              |              |            |
  |              |             | invoke      |              |              |            |
  |              |             |-------------------------->|              |            |
  |              |             |             |              | DB write     |            |
  |              |             |             |              |------------->|            |
  |              |             |             |              | exception    |            |
  |              |             |<--------------------------|              |            |
  |              |             | rollback    |              |              |            |
  |              |             |------------>|              |              |            |
  | error        |             |             |              |              |            |
  |<-------------|             |             |              |              |            |
```

---

## Request Lifecycle

Lifecycle from HTTP to database commit:

```text
1. HTTP request enters Spring MVC.

2. Controller method executes.

3. Controller calls transactional service.

4. Because service is proxied, call enters proxy.

5. Proxy invokes TransactionInterceptor.

6. TransactionInterceptor reads @Transactional metadata.

7. TransactionManager starts transaction.

8. Connection is borrowed from pool.

9. Connection is bound to current thread.

10. Real service method executes.

11. Repositories use same thread-bound connection.

12. If success, commit.

13. If failure, rollback.

14. Connection is returned to pool.

15. Response is sent.
```

One picture:

```text
HTTP
 |
Controller
 |
Proxy
 |
TxInterceptor
 |
TxManager
 |
Connection
 |
Service
 |
Repository
 |
DB
 |
Commit/Rollback
```

---

## Multiple Dry Runs (Normal, Edge, Failure)

### Dry Run 1 - Normal Transfer

Initial state:

```text
User A balance = 1000
User B balance = 500
```

Call:

```java
walletService.transfer(A, B, 100);
```

Flow:

```text
Proxy begins TX
Debit A by 100
Credit B by 100
Insert ledger record
Commit
```

Final state:

```text
User A balance = 900
User B balance = 600
Ledger record exists
```

---

### Dry Run 2 - Edge Case: Business Validation Before DB Write

```java
@Transactional
public void transfer(Long from, Long to, BigDecimal amount) {

    if (amount.signum() <= 0) {
        throw new IllegalArgumentException("amount must be positive");
    }

    walletRepository.debit(from, amount);
    walletRepository.credit(to, amount);
}
```

Flow:

```text
Proxy begins TX
Validation fails
Exception thrown
Rollback
```

Even though no DB write happened, rollback is safe.

But optimization:

```text
Cheap validation can often happen before entering transaction
```

For performance, keep transaction scope small.

---

### Dry Run 3 - Failure After First Write

```text
Debit A       SUCCESS
Credit B      FAILS
Ledger Insert NOT RUN
```

Flow:

```text
Proxy begins TX
Debit A
Credit throws RuntimeException
Proxy catches exception
Rollback
```

Final state:

```text
User A balance unchanged
User B balance unchanged
No ledger record
```

---

### Dry Run 4 - Caught Exception Mistake

```java
@Transactional
public void checkout() {

    try {
        paymentRepository.insertAttempt();

        inventoryRepository.reserve();

    } catch (Exception e) {
        log.error("failed", e);
    }
}
```

Problem:

```text
Exception is swallowed.
Method returns normally.
Proxy thinks success.
Proxy commits.
```

Flow:

```text
BEGIN
insert payment attempt
reserve inventory fails
catch exception
return normally
COMMIT
```

This is a dangerous production bug.

Fix:

```java
catch (Exception e) {
    log.error("failed", e);
    throw e;
}
```

or mark rollback-only.

---

### Dry Run 5 - Self Invocation Failure

```java
@Service
public class OrderService {

    public void process() {
        createOrder();
    }

    @Transactional
    public void createOrder() {
        orderRepository.save(...);
    }
}
```

Call:

```text
controller -> orderService.process()
```

Flow:

```text
Controller
   |
Proxy
   |
process()
   |
this.createOrder()
```

`createOrder()` did not pass through proxy.

No transaction boundary.

---

## Failure Scenarios

### 1. Self Invocation

```text
Same class method calls transactional method.
Proxy skipped.
```

### 2. Private Method

```java
@Transactional
private void saveInternal() {}
```

Proxy cannot intercept private method calls from outside.

### 3. Final Method With CGLIB

```java
@Transactional
public final void save() {}
```

CGLIB cannot override final method.

### 4. Final Class With CGLIB

```java
public final class PaymentService {}
```

Cannot subclass final class.

### 5. Exception Swallowed

```java
try {
   doDbWork();
} catch (Exception e) {
   log.error("ignored", e);
}
```

Proxy sees normal return.

Commit happens.

### 6. Checked Exception Does Not Rollback By Default

```java
@Transactional
public void pay() throws IOException {
    throw new IOException();
}
```

By default, Spring rolls back on unchecked exceptions.

For checked exceptions:

```java
@Transactional(rollbackFor = IOException.class)
```

### 7. External API Call Inside Transaction

```text
Begin TX
DB write
Call payment gateway for 5 seconds
Commit
```

Connection is held during remote call.

Can exhaust pool.

### 8. Async Boundary

```java
@Async
@Transactional
public void method() {}
```

Thread changes matter.

Transaction context is thread-bound.

### 9. Multiple Databases

One local transaction manager cannot magically guarantee atomicity across multiple independent systems unless configured for distributed transactions, which are complex and often avoided.

---

## Failure Investigation Playbook

When transaction behavior surprises you, follow this order.

### Step 1 - Did The Call Hit The Proxy?

Check:

```java
System.out.println(service.getClass());
```

Expected proxy-like output:

```text
com.example.CheckoutService$$SpringCGLIB$$0
```

or:

```text
jdk.proxy2.$Proxy54
```

### Step 2 - Is Method Public?

Proxy-based AOP works best with public methods.

### Step 3 - Is It Self Invocation?

Look for:

```java
this.someTransactionalMethod()
```

or unqualified internal call:

```java
someTransactionalMethod()
```

### Step 4 - Is Exception Escaping?

If you catch and swallow exception, Spring may commit.

### Step 5 - Is Exception Type Rollback Eligible?

Unchecked exceptions rollback by default.

Checked exceptions require `rollbackFor`.

### Step 6 - Are Logs Showing Transaction Begin?

Enable:

```yaml
logging:
  level:
    org.springframework.transaction: TRACE
```

### Step 7 - Is There A Connection Pool Issue?

Check Hikari metrics:

```text
active connections
idle connections
pending threads
connection timeout count
```

### Step 8 - Is Transaction Too Large?

Look for:

```text
Remote API call inside transaction
Large batch
Long locks
Slow queries
```

---

## Debugging Guide

### Check Proxy Class

```java
@Component
@RequiredArgsConstructor
public class StartupLogger {

    private final CheckoutService checkoutService;

    @EventListener(ApplicationReadyEvent.class)
    public void logProxy() {
        System.out.println(
            "CheckoutService runtime class = "
            + checkoutService.getClass()
        );
    }
}
```

Output:

```text
CheckoutService runtime class =
class com.example.CheckoutService$$SpringCGLIB$$0
```

This confirms proxy exists.

### Check Transaction Active

```java
import org.springframework.transaction.support.TransactionSynchronizationManager;

System.out.println(
    TransactionSynchronizationManager.isActualTransactionActive()
);
```

Inside transactional method:

```text
true
```

Outside:

```text
false
```

### Check Connection Bound To Thread

```java
System.out.println(
    TransactionSynchronizationManager.getResourceMap()
);
```

This shows thread-bound transaction resources.

### Enable SQL + Transaction Logs

```yaml
logging:
  level:
    org.springframework.transaction.interceptor: TRACE
    org.springframework.jdbc.datasource.DataSourceTransactionManager: DEBUG
    org.hibernate.SQL: DEBUG
```

You want to see:

```text
Creating new transaction
Acquired Connection
Initiating transaction commit
Rolling back transaction
```

### Debugging Mental Model

Always ask:

```text
1. Proxy hit?
2. Transaction active?
3. Connection bound?
4. Exception escaped?
5. Commit or rollback logged?
```

---

## Performance Considerations

Transactions are powerful but not free.

Costs:

```text
Connection borrowed from pool
Locks may be held
Undo/redo logs written
Isolation overhead
Commit fsync / WAL cost
Potential contention
```

The biggest production rule:

```text
Keep transactions short.
```

Bad:

```java
@Transactional
public void checkout() {

    orderRepository.save(...);

    paymentGateway.charge(...);   // remote HTTP call

    inventoryRepository.reserve(...);
}
```

Why bad?

```text
DB connection held while waiting for network.
Locks may be held.
Pool capacity reduced.
p99 latency increases.
```

Better:

```text
Validate
Call remote payment authorization
Start short transaction
Write order + inventory result
Commit
Publish event
```

### Lock Duration

The longer the transaction:

```text
More time locks are held
More chance of deadlock
More blocked requests
More connection pool pressure
```

### Batch Size

Huge transaction:

```text
1 million updates in one transaction
```

Problems:

```text
Large rollback cost
Large WAL volume
Long locks
Replication lag
```

Prefer chunking where business-safe.

---

## Scalability Considerations

### Single Database

Local transactions scale reasonably when:

```text
Transactions are short
Indexes are correct
Lock contention is low
Connection pool is tuned
```

### High RPS Service

At high RPS:

```text
active transactions roughly = RPS * transaction duration
```

Example:

```text
500 RPS * 100 ms = 50 active transactions
```

If each transaction holds one DB connection:

```text
Need enough DB connections
```

But too many connections overload DB.

So the real optimization is:

```text
Reduce transaction duration
```

not blindly increase pool size.

### Microservices

A Spring local transaction covers:

```text
One service
One local database transaction
```

It does not cover:

```text
Kafka publish after commit
Remote payment service
Another service database
Email provider
S3
```

For distributed systems use:

```text
Outbox Pattern
Saga
Idempotency
Retry
DLQ
Compensation
```

### Kubernetes Relevance

Kubernetes does not manage your database transaction.

But it affects transaction failures through:

```text
Pod termination during transaction
Connection pool during autoscaling
Readiness probes during startup
Rolling deploys
Network latency to DB
```

Production setting:

```text
terminationGracePeriodSeconds
```

should allow in-flight requests to finish when possible.

---

## Production Failure Story

### Incident: Checkout DB Pool Exhaustion

A checkout service had this method:

```java
@Transactional
public void checkout(Long userId, Long productId) {

    orderRepository.createOrder(userId, productId);

    paymentGateway.charge(userId, productId);

    inventoryRepository.reserve(productId);

    orderRepository.markPaid(userId, productId);
}
```

During normal traffic, it worked.

During sale traffic:

```text
RPS increased
Payment gateway latency increased to 3-5 seconds
Hikari active connections reached max
Requests queued
p99 latency exploded
Some requests timed out
```

The team first blamed Postgres.

But the real issue:

```text
Transaction stayed open during remote payment API call.
```

The service held database connections while waiting for network I/O.

Failure diagram:

```text
BEGIN TX
  |
DB insert order
  |
HTTP payment call waits 5 sec
  |
DB connection held idle
  |
Pool exhausted
  |
New requests wait
```

Fix:

```text
1. Create pending order in short transaction.
2. Commit.
3. Call payment gateway outside transaction.
4. In new short transaction, mark paid or failed.
5. Use idempotency key to avoid duplicate charge.
6. Use outbox event for async downstream work.
```

Improved flow:

```text
TX 1: create pending order
COMMIT

Call payment gateway

TX 2: update payment result
COMMIT

Publish event via outbox
```

Senior lesson:

```text
Transactions protect database state.

They should not wrap slow external systems.
```

---

## Common Misconceptions

### Misconception 1

```text
@Transactional is inside the method.
```

Correct:

```text
@Transactional is applied by proxy before and after method.
```

### Misconception 2

```text
Any method can be transactional.
```

Correct:

```text
Proxy must be able to intercept the method.
```

### Misconception 3

```text
Self invocation works.
```

Correct:

```text
this.method() bypasses proxy.
```

### Misconception 4

```text
Catching exception still rolls back.
```

Correct:

```text
If exception does not escape and rollback-only is not set, proxy may commit.
```

### Misconception 5

```text
@Transactional covers Kafka, Redis, HTTP and DB together.
```

Correct:

```text
Normal local transaction covers local transactional resource, commonly database connection.
```

### Misconception 6

```text
Bigger transaction means safer system.
```

Correct:

```text
Bigger transaction often means more locks, longer connection holding, worse scalability.
```

### Misconception 7

```text
REQUIRES_NEW is always safer.
```

Correct:

```text
REQUIRES_NEW starts independent transaction and can commit even if outer transaction rolls back.
```

---

## FAANG/System Design Discussion

At system design scale, transactions are a tradeoff.

### Local ACID Transaction

Good for:

```text
Single service
Single database
Strong consistency required
Short operations
```

Examples:

```text
Wallet debit + ledger row
Order row + order item rows
Inventory decrement + reservation row
```

### Distributed Transaction

Hard when:

```text
Multiple services
Multiple databases
Kafka
Redis
External payment provider
Email
S3
```

Two-phase commit exists but is often avoided in modern high-scale microservices due to complexity, blocking, operational risk, and coupling.

### Common Production Alternative

Outbox pattern:

```text
Local transaction:
    save business data
    save event row in outbox table
commit

Background publisher:
    read outbox
    publish Kafka event
    mark event sent
```

Diagram:

```text
Service
  |
  v
Local DB Transaction
  |
  +--> orders table
  |
  +--> outbox table
  |
 COMMIT
  |
  v
Outbox Publisher
  |
  v
Kafka
```

This gives reliable event publishing without distributed transactions.

### Interview Framing

Strong system design answer:

```text
Use local ACID transactions for invariants inside one service database.
Use asynchronous patterns such as outbox and saga across service boundaries.
Keep transaction scope short.
Avoid remote calls inside DB transaction.
Design idempotency for retries.
```

---

## Common Interview Questions

1. How does `@Transactional` work internally?

2. Where does transaction logic live?

3. Why does self-invocation break transactions?

4. What is the role of proxy in transactions?

5. What is `TransactionInterceptor`?

6. What is `PlatformTransactionManager`?

7. How does Spring bind a transaction to the current thread?

8. Why can catching exceptions cause unexpected commit?

9. Which exceptions rollback by default?

10. Why should we avoid remote API calls inside transactions?

11. Does `@Transactional` work on private methods?

12. Does `@Transactional` work on final methods?

13. How would you debug a transaction not rolling back?

14. How does connection pooling relate to transactions?

15. What happens if transaction duration is long?

16. Can one Spring transaction cover multiple microservices?

17. What is the difference between local transaction and distributed transaction?

18. How does the outbox pattern relate to transactions?

19. How do you check if a transaction is active?

20. What is the most important production rule for transactions?

---

## Strong Interview Answers

### Q1. How does `@Transactional` work internally?

`@Transactional` is implemented using Spring AOP proxies. Spring exposes a proxy instead of the raw bean. When a caller invokes the transactional method through the proxy, the proxy runs `TransactionInterceptor`, which asks `PlatformTransactionManager` to begin a transaction, invokes the target method, and commits or rolls back depending on the outcome.

### Q2. Where does transaction logic live?

Transaction logic lives in the proxy/interceptor layer, not inside the business method. The business method contains only business code.

### Q3. Why does self-invocation fail?

Self-invocation calls another method on the same object using `this.method()`. That internal call does not pass through the Spring proxy, so `TransactionInterceptor` is not executed and no transaction boundary is created for that inner method.

### Q4. Why can catching an exception cause commit?

The proxy decides commit or rollback based on what escapes the method. If the method catches the exception and returns normally, the proxy may treat it as success and commit unless rollback-only is explicitly set.

### Q5. Why avoid remote calls inside transactions?

Remote calls increase transaction duration. While waiting for the network, the service may hold DB connections and locks. Under load this can exhaust the connection pool and increase latency.

### Q6. How does Spring ensure all repositories use the same connection?

Spring binds the transaction resources, such as the database connection, to the current thread using transaction synchronization. Repository calls on the same thread reuse that bound connection.

### Q7. Can `@Transactional` cover Kafka and database atomically?

Not by default as a simple local transaction. A database transaction covers database resources. For reliable DB + Kafka publishing, use patterns like transactional outbox.

---

## Production Checklist

Before using `@Transactional`, verify:

```text
[ ] Method is called from another Spring bean through proxy.

[ ] Method is public.

[ ] Bean is managed by Spring.

[ ] No self-invocation for transactional boundary.

[ ] Exceptions that should rollback escape the method.

[ ] Checked exceptions use rollbackFor if needed.

[ ] Transaction scope is small.

[ ] No slow remote API calls inside transaction.

[ ] No unnecessary large batch inside one transaction.

[ ] Connection pool capacity matches transaction duration and RPS.

[ ] Logs confirm transaction begin and commit/rollback.

[ ] Idempotency exists for retryable external operations.

[ ] Outbox pattern considered for DB + Kafka consistency.
```

---

## One-Page Cheat Sheet

```text
@Transactional
      |
      v
Spring Proxy
      |
      v
TransactionInterceptor
      |
      v
PlatformTransactionManager
      |
      v
BEGIN
      |
      v
Target Method
      |
      v
COMMIT or ROLLBACK
```

Success:

```text
Method returns normally
      |
      v
Commit
```

Failure:

```text
RuntimeException escapes
      |
      v
Rollback
```

Danger:

```text
Exception caught and swallowed
      |
      v
Commit may happen
```

Self invocation:

```text
this.method()
      |
      v
Proxy bypassed
      |
      v
No transaction advice
```

Thread model:

```text
Thread
  |
  v
Transaction Context
  |
  v
Database Connection
```

Production rule:

```text
Keep transactions short.
```

---

## Last-Minute Interview Revision

Remember these five lines:

```text
@Transactional is proxy-based.

Transaction logic lives in TransactionInterceptor.

The proxy opens transaction before the method.

The proxy commits or rolls back after the method.

No proxy means no transaction boundary.
```

Remember the failure formula:

```text
Self invocation
+ @Transactional
= no proxy hit
= no transaction
```

Remember the production formula:

```text
Long transaction
= connection held longer
= locks held longer
= lower throughput
= higher p99 latency
```

Remember the debugging formula:

```text
Proxy?
Transaction active?
Exception escaped?
Commit/rollback logs?
Connection pool healthy?
```

---

## Mental Models Table

| Concept | Mental Model |
|---|---|
| `@Transactional` | Transaction gate around method |
| Proxy | Security door before service |
| TransactionInterceptor | Guard that begins/commits/rolls back |
| TransactionManager | Coordinator of transaction state |
| Connection | Vehicle carrying DB transaction |
| ThreadLocal | Backpack attached to current thread |
| Commit | Publish changes |
| Rollback | Destroy temporary changes |
| Self Invocation | Side door bypass |
| Caught Exception | Alarm silenced |
| Long Transaction | Holding a room while doing outside work |
| Outbox | Reliable message notebook committed with data |

---

## Key Takeaways

1. `@Transactional` does not modify your method.

2. Spring transactions are implemented by AOP proxy interception.

3. The proxy creates a transaction boundary around the method call.

4. Transaction logic lives in `TransactionInterceptor`.

5. The transaction manager begins, commits, and rolls back transactions.

6. Repositories participate by using the thread-bound transaction connection.

7. Self-invocation bypasses the proxy, so transaction advice does not run.

8. Catching and swallowing exceptions can accidentally cause commit.

9. Long transactions hurt performance and scalability.

10. A local Spring transaction is not the same as a distributed business transaction.

11. For DB + Kafka reliability, consider the outbox pattern.

12. The one picture to remember:

```text
Caller
  |
  v
Proxy
  |
  v
BEGIN
  |
  v
Business Method
  |
  v
COMMIT / ROLLBACK
```

Final memory hook:

```text
No Proxy
    =
No Transaction Boundary

Proxy Hit
    =
Transaction Boundary Active
```
