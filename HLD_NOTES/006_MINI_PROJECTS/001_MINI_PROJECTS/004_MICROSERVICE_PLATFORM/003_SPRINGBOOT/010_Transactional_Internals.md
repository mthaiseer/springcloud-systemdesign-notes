# 010_Transactional_Internals.md

# MiniSpringBoot Deep Production Mode
# 010 - Transactional Internals

## One Core Mental Model

> A Spring transaction is not magic around SQL.
>
> A Spring transaction is a **thread-bound resource lifecycle**.
>
> Spring opens a database resource, binds it to the current thread, lets all repository calls reuse it, then commits or rolls back when the method boundary ends.

This chapter teaches exactly ONE core mental model:

```text
@Transactional internals
        =
Thread-bound transaction resource lifecycle
```

Do not memorize transaction annotations.

Think like this:

```text
One request thread
      |
      v
One transaction context
      |
      v
One bound database connection / EntityManager
      |
      v
All repository calls reuse it
      |
      v
Commit or rollback at the boundary
```

If you remember only one picture, remember this:

```text
Thread-42
  |
  +--> TransactionSynchronizationManager
          |
          +--> DataSource -> ConnectionHolder(PostgresConnection-17)
          |
          +--> EntityManagerFactory -> EntityManagerHolder(EntityManager-9)
```

The transaction lives because Spring keeps transaction resources attached to the current thread.

---

## Why This Exists

A business operation usually performs more than one database action.

Example:

```text
Create order row
Decrease inventory count
Insert payment attempt
Insert audit event
```

If each database statement commits independently, production data can become inconsistent.

Failure example:

```text
Order row inserted                COMMITTED
Inventory decreased               COMMITTED
Payment attempt insert failed     ERROR
Audit event not inserted          NOT REACHED
```

Now the system says:

```text
Order exists.
Stock changed.
Payment state missing.
Audit trail broken.
```

So we need one protected unit of work:

```text
Either all changes become durable
OR
all changes disappear
```

Spring provides that protected unit using `@Transactional`, but internally it must solve a harder problem:

```text
How do many repository calls know they belong to the same transaction?
```

The answer is not that every repository receives a transaction object manually.

The answer is:

```text
Spring binds transaction resources to the current thread.
```

Repositories later look up the current thread-bound resource and reuse it.

That is the hidden internal model.

---

## Problem Statement

Consider this service:

```java
@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void checkout(Long userId, Long productId) {
        orderRepository.createOrder(userId, productId);
        inventoryRepository.reserve(productId);
        paymentRepository.createAttempt(userId, productId);
    }
}
```

Important question:

```text
How do these three repository calls use the same transaction?
```

They are separate objects:

```text
OrderRepository
InventoryRepository
PaymentRepository
```

They may each use `JdbcTemplate`, JPA, Hibernate, or Spring Data.

Yet they must share one transaction.

What would be bad:

```text
Repository 1 opens Connection-A and commits.
Repository 2 opens Connection-B and commits.
Repository 3 opens Connection-C and fails.
```

What Spring wants:

```text
Repository 1 uses Connection-17
Repository 2 uses Connection-17
Repository 3 uses Connection-17
Transaction manager commits or rolls back Connection-17 once.
```

The core internal mechanism:

```text
Transaction manager binds Connection-17 to the current thread before repository calls start.
```

---

## Mental Model

Think of a hospital operation room.

Before surgery starts:

```text
Patient file is opened.
Operating room is reserved.
Tools are assigned.
Staff know this room is active.
```

During surgery:

```text
Every doctor entering that room uses the same patient context.
```

After surgery:

```text
If successful, record is finalized.
If failed, emergency protocol runs.
Tools are cleaned and released.
```

Spring transaction internals are similar.

```text
Before method:
    Open transaction resources
    Bind resources to current thread

During method:
    Repositories reuse thread-bound resources

After method:
    Commit or rollback
    Trigger callbacks
    Unbind resources
    Release connection
```

Real-world analogy:

```text
Thread = operation room
Transaction context = patient file
Connection = reserved surgical equipment
Repositories = doctors
Commit = finalize record
Rollback = cancel temporary changes
Cleanup = release room and tools
```

The big idea:

```text
Spring does not pass transaction state through every method parameter.
Spring stores transaction state beside the executing thread.
```

---

## Core Concepts

### TransactionInterceptor

The AOP interceptor that surrounds your method call.

It does this:

```text
1. Read @Transactional metadata
2. Ask transaction manager for transaction
3. Invoke business method
4. Commit or rollback
5. Cleanup resources
```

### PlatformTransactionManager

Spring abstraction for transaction lifecycle.

Common implementations:

```text
DataSourceTransactionManager   -> plain JDBC
JpaTransactionManager          -> JPA / Hibernate
JtaTransactionManager          -> distributed / container-managed transactions
```

### TransactionDefinition

The rules of the transaction:

```text
Propagation
Isolation
Timeout
Read-only
Rollback rules
```

### TransactionStatus

Runtime state of this transaction:

```text
Is this a new transaction?
Is it rollback-only?
Is it completed?
Does it have savepoints?
Were resources suspended?
```

### TransactionSynchronizationManager

The internal ThreadLocal manager.

It stores resources like:

```text
DataSource -> ConnectionHolder
EntityManagerFactory -> EntityManagerHolder
Synchronizations -> callbacks to run before/after commit
```

### ConnectionHolder

A wrapper around a JDBC connection plus transaction metadata.

Conceptually:

```java
class ConnectionHolder {
    Connection connection;
    boolean synchronizedWithTransaction;
    int referenceCount;
}
```

### EntityManagerHolder

For JPA, Spring binds an `EntityManager` to the current thread so repositories share the same persistence context.

### Persistence Context

Hibernate's first-level cache and change-tracking workspace.

Inside a transaction, entities are managed here.

```text
Database row
   -> loaded into Entity object
   -> tracked by persistence context
   -> flushed to DB before commit
```

---

## Internal Architecture

High-level architecture:

```text
                 Caller / Controller
                         |
                         v
              Transactional Proxy Bean
                         |
                         v
              TransactionInterceptor
                         |
                         v
              PlatformTransactionManager
                         |
                         v
        TransactionSynchronizationManager
                  ThreadLocal Resource Map
                         |
                         v
          ConnectionHolder / EntityManagerHolder
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

The hidden center of the model:

```text
TransactionSynchronizationManager
```

It is the thread-local registry that answers:

```text
Does this thread already have a transaction resource?
```

ASCII view:

```text
Thread-42 executing checkout()

+--------------------------------------------------+
| Thread-42                                        |
|                                                  |
|  TransactionSynchronizationManager               |
|  +--------------------------------------------+  |
|  | Resource Map                               |  |
|  |                                            |  |
|  | DataSource-A -> ConnectionHolder(conn-17)  |  |
|  | EMFactory   -> EntityManagerHolder(em-9)   |  |
|  +--------------------------------------------+  |
|                                                  |
|  checkout()                                      |
|    orderRepository.save()                        |
|    inventoryRepository.reserve()                 |
|    paymentRepository.save()                      |
+--------------------------------------------------+
```

All repository calls on `Thread-42` can find the same resources.

---

## Internal Working

Spring's transaction lifecycle can be understood as five phases:

```text
1. Resolve transaction attributes
2. Create or join transaction
3. Bind resources to thread
4. Execute business method
5. Complete and cleanup transaction
```

### Phase 1 - Resolve Transaction Attributes

When the proxy intercepts the method, Spring asks:

```text
Is this method transactional?
What are the rules?
```

Metadata comes from:

```java
@Transactional(
    propagation = Propagation.REQUIRED,
    isolation = Isolation.READ_COMMITTED,
    timeout = 30,
    readOnly = false
)
```

Spring converts annotation metadata into a `TransactionAttribute`.

Conceptually:

```text
method checkout()
  -> propagation REQUIRED
  -> isolation READ_COMMITTED
  -> timeout 30s
  -> readOnly false
  -> rollback on RuntimeException
```

### Phase 2 - Create or Join Transaction

Default propagation is `REQUIRED`.

That means:

```text
If transaction already exists on this thread, join it.
If not, create a new one.
```

ASCII:

```text
Does Thread-42 already have transaction?

       yes                         no
        |                          |
        v                          v
   join existing              start new transaction
```

### Phase 3 - Bind Resources To Thread

For JDBC:

```text
Borrow Connection from pool
Set autoCommit=false
Apply isolation/readOnly/timeout if needed
Wrap in ConnectionHolder
Bind DataSource -> ConnectionHolder in ThreadLocal map
```

For JPA:

```text
Create or reuse EntityManager
Bind EntityManagerFactory -> EntityManagerHolder
Join EntityManager to transaction
```

ThreadLocal map becomes:

```text
Thread-42 Resource Map
+-------------------------+----------------------------+
| Key                     | Value                      |
+-------------------------+----------------------------+
| mainDataSource          | ConnectionHolder(conn-17)  |
| entityManagerFactory    | EntityManagerHolder(em-9)  |
+-------------------------+----------------------------+
```

### Phase 4 - Execute Business Method

Now the real method runs:

```java
checkout(userId, productId);
```

Repositories call database access code.

But instead of randomly opening independent connections, Spring-aware infrastructure checks:

```text
Is there a transaction-bound connection for this DataSource on current thread?
```

If yes:

```text
Reuse Connection-17
```

### Phase 5 - Complete And Cleanup

If method returns normally:

```text
Flush persistence context if JPA
Run beforeCommit callbacks
Commit database transaction
Run afterCommit callbacks
Run afterCompletion callbacks
Unbind resources
Release connection
```

If method throws rollback-eligible exception:

```text
Rollback database transaction
Run afterCompletion callbacks
Unbind resources
Release connection
Rethrow exception
```

The cleanup is as important as commit.

Without cleanup:

```text
Thread pool worker could accidentally keep old transaction resources.
```

Spring carefully unbinds after completion.

---

## Step-by-Step Flow

Successful JDBC transaction:

```text
1. Controller calls checkoutService.checkout().

2. Call enters Spring proxy.

3. TransactionInterceptor reads @Transactional.

4. Transaction manager checks current thread.

5. No existing transaction found.

6. Transaction manager borrows Connection-17 from HikariCP.

7. autoCommit=false is set.

8. ConnectionHolder(Connection-17) is bound to Thread-42.

9. Real checkout() method executes.

10. orderRepository uses Connection-17.

11. inventoryRepository uses Connection-17.

12. paymentRepository uses Connection-17.

13. Method returns normally.

14. Transaction manager commits Connection-17.

15. ConnectionHolder is unbound from Thread-42.

16. Connection is reset and returned to pool.
```

ASCII:

```text
Controller
   |
   v
Proxy
   |
   v
TransactionInterceptor
   |
   v
TxManager.getTransaction()
   |
   v
Borrow Connection-17
   |
   v
Bind to Thread-42
   |
   v
Run checkout()
   |
   v
Repositories reuse Connection-17
   |
   v
Commit
   |
   v
Unbind + Release
```

Failure flow:

```text
checkout()
  |
  +--> orderRepository.insert()      OK
  +--> inventoryRepository.reserve() OK
  +--> paymentRepository.insert()    RuntimeException
```

Spring sees exception escape:

```text
RuntimeException escaped method
       |
       v
Rollback Connection-17
       |
       v
Unbind resources
       |
       v
Rethrow exception
```

---

## Deep Walkthrough Example

### Business Case: Wallet Transfer

```java
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final LedgerRepository ledgerRepository;

    @Transactional
    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        walletRepository.debit(fromUserId, amount);
        walletRepository.credit(toUserId, amount);
        ledgerRepository.record(fromUserId, toUserId, amount);
    }
}
```

### What Actually Happens Internally

```text
Caller calls walletService.transfer()

But walletService is proxy.
```

Internal call stack:

```text
Controller.transfer()
  -> WalletServiceProxy.transfer()
     -> TransactionInterceptor.invoke()
        -> JpaTransactionManager.getTransaction()
           -> create EntityManager
           -> get JDBC Connection lazily or immediately
           -> bind EntityManagerHolder to ThreadLocal
        -> Real WalletService.transfer()
           -> walletRepository.debit()
           -> walletRepository.credit()
           -> ledgerRepository.record()
        -> commit()
           -> flush EntityManager
           -> commit JDBC transaction
           -> close EntityManager
           -> unbind ThreadLocal resources
```

JPA-specific mental model:

```text
@Transactional method starts
        |
        v
Persistence Context opens
        |
        v
Entities loaded / changed
        |
        v
Hibernate tracks dirty entities
        |
        v
Before commit, flush converts changes to SQL
        |
        v
DB commit makes SQL durable
```

Diagram:

```text
@Transactional transfer()

+--------------------------------------------------+
| Persistence Context                              |
|                                                  |
| Wallet#1 balance: 1000 -> 900   DIRTY            |
| Wallet#2 balance:  500 -> 600   DIRTY            |
| Ledger#55 new row                NEW             |
+--------------------------------------------------+
          |
          v
       FLUSH
          |
          v
SQL UPDATE wallet SET balance=900 WHERE id=1
SQL UPDATE wallet SET balance=600 WHERE id=2
SQL INSERT INTO ledger ...
          |
          v
       COMMIT
```

The key point:

```text
JPA may delay SQL until flush/commit.
The transaction still protects the entire unit of work.
```

---

## Rich ASCII Diagrams

### Diagram 1 - The Hidden Thread Backpack

```text
Every request thread carries an invisible backpack.

Thread-42 Backpack
+------------------------------------------------+
| Transaction resources                           |
|                                                |
| DataSource -> ConnectionHolder(conn-17)         |
| EMFactory  -> EntityManagerHolder(em-9)         |
| Sync list  -> beforeCommit/afterCommit hooks    |
+------------------------------------------------+

Repositories do not carry the transaction manually.
They look inside the current thread backpack.
```

### Diagram 2 - Transaction Lifecycle State Machine

```text
          +---------+
          |  NONE   |
          +----+----+
               |
               | begin
               v
          +---------+
          | ACTIVE  |
          +----+----+
               |
        +------+------+
        |             |
        | success     | exception / rollbackOnly
        v             v
 +-------------+   +--------------+
 | COMMITTING  |   | ROLLING BACK |
 +------+------+   +------+-------+
        |                 |
        v                 v
 +-------------+   +--------------+
 | COMMITTED   |   | ROLLED BACK  |
 +------+------+   +------+-------+
        |                 |
        +--------+--------+
                 |
                 v
            +---------+
            | CLEANUP |
            +---------+
```

### Diagram 3 - JDBC Connection Binding

```text
Before transaction:

Thread-42 Resource Map
+----------------+
| empty          |
+----------------+

After begin:

Thread-42 Resource Map
+----------------------------------------+
| mainDataSource -> ConnectionHolder     |
|                   connection=conn-17   |
|                   autoCommit=false     |
+----------------------------------------+

After completion:

Thread-42 Resource Map
+----------------+
| empty          |
+----------------+
```

### Diagram 4 - Repository Reuse

```text
checkout()
  |
  +--> OrderRepository
  |       |
  |       v
  |   DataSourceUtils.getConnection(dataSource)
  |       |
  |       v
  |   Finds conn-17 in ThreadLocal
  |
  +--> InventoryRepository
  |       |
  |       v
  |   Finds conn-17 in ThreadLocal
  |
  +--> PaymentRepository
          |
          v
      Finds conn-17 in ThreadLocal
```

### Diagram 5 - JPA Persistence Context

```text
@Transactional method
        |
        v
+-----------------------------------+
| EntityManager / PersistenceContext |
|                                   |
| User#10        MANAGED             |
| Order#44       NEW                 |
| Inventory#5    DIRTY               |
+-----------------------------------+
        |
        | flush before commit
        v
+-----------------------------------+
| SQL statements sent to database    |
+-----------------------------------+
        |
        | commit
        v
+-----------------------------------+
| Durable database state             |
+-----------------------------------+
```

### Diagram 6 - Nested Call With REQUIRED

```text
OrderService.placeOrder() @Transactional REQUIRED
        |
        | transaction starts: conn-17
        v
PaymentService.createPayment() @Transactional REQUIRED
        |
        | existing transaction found
        | joins conn-17
        v
InventoryService.reserve() @Transactional REQUIRED
        |
        | existing transaction found
        | joins conn-17
        v
Outer method returns
        |
        v
One commit only
```

---

## Data Structures & Algorithms Used

Spring transaction internals are not about advanced algorithms.

They are about disciplined resource management.

### 1. ThreadLocal

Conceptually:

```java
ThreadLocal<Map<Object, Object>> resources;
```

Mental map:

```text
Thread-1 -> { DataSource -> ConnectionHolder(conn-1) }
Thread-2 -> { DataSource -> ConnectionHolder(conn-2) }
Thread-3 -> { }
```

Lookup:

```text
O(1) average map lookup
```

Why it matters:

```text
Repository code can find the transaction resource without passing it through parameters.
```

### 2. Resource Map

Key-value structure:

```text
Key:   DataSource / EntityManagerFactory
Value: ResourceHolder
```

Example:

```text
resources = {
  HikariDataSource@abc -> ConnectionHolder(conn-17),
  EntityManagerFactory@xyz -> EntityManagerHolder(em-9)
}
```

### 3. Stack-like Suspension

For propagation like `REQUIRES_NEW`, Spring must suspend existing resources.

Conceptual flow:

```text
Outer transaction resources active
        |
        | call REQUIRES_NEW
        v
Suspend outer resources
Start inner transaction
Commit inner transaction
Resume outer resources
```

Pseudo structure:

```java
class SuspendedResourcesHolder {
    Object suspendedResources;
    List<TransactionSynchronization> suspendedSynchronizations;
}
```

### 4. Transaction Status State

Conceptual object:

```java
class TransactionStatus {
    boolean newTransaction;
    boolean rollbackOnly;
    boolean completed;
    Object savepoint;
    Object suspendedResources;
}
```

This object explains bugs like:

```text
UnexpectedRollbackException
```

because an inner operation can mark the shared transaction rollback-only.

### 5. Synchronization Callback List

Spring keeps callbacks:

```text
beforeCommit
beforeCompletion
afterCommit
afterCompletion
```

Used by infrastructure such as:

```text
JPA flush
transactional events
resource cleanup
```

---

## Java Code Example With Internal Execution Explanation

### Repository Layer

```java
@Repository
@RequiredArgsConstructor
public class WalletRepository {

    private final JdbcTemplate jdbcTemplate;

    public void debit(Long userId, BigDecimal amount) {
        jdbcTemplate.update(
            "update wallet set balance = balance - ? where user_id = ?",
            amount,
            userId
        );
    }

    public void credit(Long userId, BigDecimal amount) {
        jdbcTemplate.update(
            "update wallet set balance = balance + ? where user_id = ?",
            amount,
            userId
        );
    }
}
```

### Service Layer

```java
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final LedgerRepository ledgerRepository;

    @Transactional
    public void transfer(Long from, Long to, BigDecimal amount) {
        walletRepository.debit(from, amount);
        walletRepository.credit(to, amount);
        ledgerRepository.record(from, to, amount);
    }
}
```

### What Happens Internally

Pseudo-code of transaction interceptor:

```java
public Object invoke(MethodInvocation invocation) throws Throwable {

    TransactionAttribute attr = readTransactionalMetadata(invocation);

    TransactionStatus status =
        transactionManager.getTransaction(attr);

    try {
        Object result = invocation.proceed();

        transactionManager.commit(status);

        return result;
    } catch (Throwable ex) {
        if (shouldRollbackOn(ex, attr)) {
            transactionManager.rollback(status);
        } else {
            transactionManager.commit(status);
        }
        throw ex;
    } finally {
        cleanupThreadLocalResourcesIfNeeded();
    }
}
```

Pseudo-code of JDBC transaction manager:

```java
public TransactionStatus getTransaction(TransactionDefinition definition) {

    ConnectionHolder existing =
        TransactionSynchronizationManager.getResource(dataSource);

    if (existing != null) {
        return joinExistingTransaction(existing);
    }

    Connection connection = dataSource.getConnection();
    connection.setAutoCommit(false);

    ConnectionHolder holder = new ConnectionHolder(connection);

    TransactionSynchronizationManager.bindResource(dataSource, holder);

    return new TransactionStatus(true, holder);
}
```

Pseudo-code of repository connection lookup:

```java
public Connection getConnection(DataSource dataSource) {

    ConnectionHolder holder =
        TransactionSynchronizationManager.getResource(dataSource);

    if (holder != null) {
        return holder.getConnection();
    }

    return dataSource.getConnection();
}
```

This is the most important internal idea:

```text
Transaction manager binds.
Repository lookup reuses.
Transaction manager completes.
```

---

## Spring Boot Example With Internal Flow

### Controller

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<String> checkout(@RequestBody CheckoutRequest request) {
        checkoutService.checkout(request.userId(), request.productId());
        return ResponseEntity.ok("checkout accepted");
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
        orderRepository.insert(userId, productId);
        inventoryRepository.reserve(productId);
        paymentAttemptRepository.insert(userId, productId);
    }
}
```

### Debug Helper

```java
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class TxDebug {

    public static void print(String label) {
        System.out.println("--- " + label + " ---");
        System.out.println("tx active = " +
            TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("resources = " +
            TransactionSynchronizationManager.getResourceMap());
    }
}
```

Use inside service:

```java
@Transactional
public void checkout(Long userId, Long productId) {
    TxDebug.print("before order insert");
    orderRepository.insert(userId, productId);

    TxDebug.print("before inventory reserve");
    inventoryRepository.reserve(productId);

    TxDebug.print("before payment insert");
    paymentAttemptRepository.insert(userId, productId);
}
```

Expected mental output:

```text
--- before order insert ---
tx active = true
resources = {HikariDataSource -> ConnectionHolder@...}

--- before inventory reserve ---
tx active = true
resources = {HikariDataSource -> ConnectionHolder@...}

--- before payment insert ---
tx active = true
resources = {HikariDataSource -> ConnectionHolder@...}
```

Internal flow:

```text
HTTP request
   |
   v
Controller
   |
   v
CheckoutService proxy
   |
   v
TransactionInterceptor
   |
   v
JpaTransactionManager / DataSourceTransactionManager
   |
   v
Bind resources to thread
   |
   v
Real CheckoutService.checkout()
   |
   v
Repositories reuse resources
   |
   v
Commit / rollback
   |
   v
Cleanup ThreadLocal
```

---

## Production Architecture Example

### Local Transaction Boundary

```text
                  Checkout Service Pod
+---------------------------------------------------+
|                                                   |
| HTTP Thread-42                                    |
|   |                                               |
|   v                                               |
| @Transactional checkout()                         |
|   |                                               |
|   +--> orders table                               |
|   +--> inventory table                            |
|   +--> payment_attempt table                      |
|                                                   |
+-------------------------|-------------------------+
                          |
                          v
                    PostgreSQL Primary
```

This is safe when all critical state is in the same transactional database.

### Wrong Distributed Mental Model

```text
@Transactional checkout()
   |
   +--> local DB insert
   +--> call Payment Service over HTTP
   +--> publish Kafka event
   +--> call Inventory Service over HTTP
```

This is not one local transaction.

The local database transaction cannot automatically rollback:

```text
Remote HTTP side effects
Kafka messages already published
External payment capture
Email already sent
S3 upload
```

Senior production model:

```text
Use local transactions for local invariants.
Use outbox, saga, idempotency, and compensation across service boundaries.
```

Better architecture:

```text
@Transactional local DB operation
   |
   +--> orders table
   +--> outbox table
   |
 COMMIT
   |
   v
Outbox Publisher
   |
   v
Kafka
   |
   v
Other services react idempotently
```

---

## Sequence Diagram ASCII

### Successful Transaction

```text
Client        Controller       Proxy       TxInterceptor      TxManager       Repo        DB
  |               |             |              |                 |             |          |
  | POST          |             |              |                 |             |          |
  |-------------->|             |              |                 |             |          |
  |               | checkout()  |              |                 |             |          |
  |               |------------>|              |                 |             |          |
  |               |             | invoke       |                 |             |          |
  |               |             |------------->|                 |             |          |
  |               |             |              | getTransaction  |             |          |
  |               |             |              |---------------->|             |          |
  |               |             |              |                 | bind conn   |          |
  |               |             |              |                 |------------>|          |
  |               |             | call target  |                 |             |          |
  |               |             |------------------------------------------>|          |
  |               |             |              |                 |             | INSERT   |
  |               |             |              |                 |             |--------->|
  |               |             |              | commit          |             |          |
  |               |             |              |---------------->|             |          |
  |               |             |              |                 | COMMIT      |          |
  |               |             |              |                 |---------------------->|
  |               | response    |              |                 |             |          |
  |<--------------|             |              |                 |             |          |
```

### Rollback Transaction

```text
Client        Controller       Proxy       TxInterceptor      TxManager       Service       DB
  |               |             |              |                 |              |         |
  | request       |             |              |                 |              |         |
  |-------------->|             |              |                 |              |         |
  |               | call svc    |              |                 |              |         |
  |               |------------>|              |                 |              |         |
  |               |             | begin        |                 |              |         |
  |               |             |------------->| getTransaction  |              |         |
  |               |             |              |---------------->|              |         |
  |               |             | invoke real  |                 |              |         |
  |               |             |------------------------------------------>|         |
  |               |             |              |                 | exception    |         |
  |               |             |<------------------------------------------|         |
  |               |             | rollback     |                 |              |         |
  |               |             |------------->| rollback        |              |         |
  |               |             |              |---------------->| ROLLBACK     |         |
  |               | error       |              |                 |              |         |
  |<--------------|             |              |                 |              |         |
```

---

## Request Lifecycle

Full request lifecycle:

```text
1. HTTP request enters Tomcat thread.

2. DispatcherServlet routes request to controller.

3. Controller calls service bean.

4. The service bean is actually a proxy.

5. Proxy invokes TransactionInterceptor.

6. Interceptor reads transaction metadata.

7. Transaction manager checks ThreadLocal resources.

8. If no transaction exists, it creates a new one.

9. Connection or EntityManager is bound to current thread.

10. Real service method executes.

11. Repositories use Spring utilities to obtain current thread-bound resource.

12. JPA tracks entity changes in persistence context.

13. Method returns or throws exception.

14. Transaction manager decides commit or rollback.

15. Before commit, JPA flush may send SQL.

16. Database commits or rolls back.

17. Synchronization callbacks execute.

18. ThreadLocal resources are unbound.

19. Connection is returned to pool.

20. HTTP response returns.
```

One-line lifecycle:

```text
Intercept -> Begin/Join -> Bind -> Execute -> Flush -> Commit/Rollback -> Unbind -> Release
```

---

## Multiple Dry Runs

### Dry Run 1 - Normal Commit

Initial state:

```text
wallet A = 1000
wallet B = 500
ledger rows = 0
```

Call:

```java
walletService.transfer(A, B, 100);
```

Flow:

```text
Proxy intercepts
No existing transaction
Borrow conn-17
Bind conn-17 to Thread-42
Debit A using conn-17
Credit B using conn-17
Insert ledger using conn-17
Commit conn-17
Unbind conn-17
Return conn-17 to pool
```

Final state:

```text
wallet A = 900
wallet B = 600
ledger rows = 1
```

### Dry Run 2 - Failure Rollback

Flow:

```text
Debit A using conn-17           success
Credit B using conn-17          success
Insert ledger using conn-17     throws RuntimeException
```

Transaction result:

```text
Exception escapes service method
TransactionInterceptor catches it
Rollback conn-17
Unbind resources
Rethrow exception
```

Final state:

```text
wallet A = 1000
wallet B = 500
ledger rows = 0
```

### Dry Run 3 - REQUIRED Joins Existing Transaction

```java
@Transactional
public void placeOrder() {
    orderService.createOrder();
    paymentService.createPayment();
}
```

Both inner methods also use `@Transactional(REQUIRED)`.

Flow:

```text
placeOrder starts transaction conn-21
createOrder sees existing conn-21 and joins
createPayment sees existing conn-21 and joins
outer method commits once
```

Important:

```text
There are not three commits.
There is one shared transaction.
```

### Dry Run 4 - REQUIRES_NEW Suspends Outer Transaction

```java
@Transactional
public void checkout() {
    orderRepository.createOrder();
    auditService.writeAuditRequiresNew();
    throw new RuntimeException("checkout failed");
}
```

Audit method:

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void writeAuditRequiresNew() {
    auditRepository.insert("checkout attempted");
}
```

Flow:

```text
Outer checkout starts conn-31
Order inserted using conn-31
REQUIRES_NEW called through proxy
Outer conn-31 suspended
Inner transaction starts conn-32
Audit inserted using conn-32
Inner commits conn-32
Outer conn-31 resumes
Outer throws exception
Outer rolls back conn-31
```

Final state:

```text
Order row rolled back
Audit row committed
```

This surprises many developers.

### Dry Run 5 - Rollback-Only Surprise

```java
@Transactional
public void outer() {
    try {
        inner();
    } catch (RuntimeException e) {
        log.warn("ignored");
    }
}

@Transactional
public void inner() {
    throw new RuntimeException("failed");
}
```

If `inner()` participates in the same transaction and marks it rollback-only:

```text
outer catches exception
outer returns normally
Spring tries commit
But transaction is rollback-only
Spring rolls back
UnexpectedRollbackException may be thrown
```

Mental model:

```text
A shared transaction can be poisoned.
Once marked rollback-only, normal return does not guarantee commit.
```

---

## Failure Scenarios

### 1. ThreadLocal Lost Across Threads

```java
@Transactional
public void process() {
    CompletableFuture.runAsync(() -> repository.save(...));
}
```

Problem:

```text
New thread does not inherit transaction resources.
```

Diagram:

```text
Thread-42 has conn-17
   |
   +--> starts async task

Thread-99 has empty TransactionSynchronizationManager
   |
   +--> repository call not in original transaction
```

### 2. Long Transaction Holds Connection

```java
@Transactional
public void checkout() {
    orderRepository.insert();
    paymentGateway.charge(); // 5 seconds
    orderRepository.markPaid();
}
```

Problem:

```text
Connection held while waiting for remote API.
```

### 3. JPA Flush Happens Before Commit

Developers often think SQL only happens at commit.

Actually flush can happen:

```text
Before commit
Before query execution
When flush() is called manually
Depending on flush mode
```

So errors can appear before commit:

```text
Constraint violation
Optimistic locking failure
Foreign key violation
```

### 4. Read-Only Does Not Mean No Writes Everywhere

```java
@Transactional(readOnly = true)
public void method() {
    entity.setName("new");
}
```

Depending on provider and configuration, read-only may optimize flush behavior, but do not treat it as a security rule.

Mental model:

```text
readOnly is an optimization hint, not business authorization.
```

### 5. Wrong Transaction Manager

If multiple data sources exist:

```text
ordersDataSource
billingDataSource
```

Using the wrong transaction manager means the expected resource may not be bound.

Symptom:

```text
One database rolls back, another commits.
```

### 6. Catching Exception Without Rollback-Only

```java
@Transactional
public void method() {
    try {
        repository.save();
        riskyOperation();
    } catch (Exception e) {
        log.error("failed", e);
    }
}
```

If method returns normally:

```text
Spring may commit.
```

Fix:

```java
TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
```

or rethrow.

### 7. Checked Exception Does Not Roll Back By Default

```java
@Transactional
public void importFile() throws IOException {
    repository.save(...);
    throw new IOException("file failed");
}
```

Default rollback rule:

```text
RuntimeException and Error rollback.
Checked exceptions do not rollback unless configured.
```

Fix:

```java
@Transactional(rollbackFor = IOException.class)
```

---

## Failure Investigation Playbook

When transaction internals surprise you, debug in this order.

### Step 1 - Is Transaction Active?

```java
TransactionSynchronizationManager.isActualTransactionActive()
```

Expected inside transaction:

```text
true
```

### Step 2 - What Resources Are Bound?

```java
TransactionSynchronizationManager.getResourceMap()
```

Look for:

```text
DataSource -> ConnectionHolder
EntityManagerFactory -> EntityManagerHolder
```

### Step 3 - Did Call Hit Proxy?

```java
System.out.println(service.getClass());
```

Expected:

```text
CheckoutService$$SpringCGLIB$$0
```

or JDK proxy class.

### Step 4 - Is Same Thread Used?

Log thread name:

```java
System.out.println(Thread.currentThread().getName());
```

If transaction starts on one thread and repository runs on another, the context is lost.

### Step 5 - Did Exception Escape?

Check whether exceptions are swallowed.

Question:

```text
Did the transactional method return normally?
```

### Step 6 - Was Transaction Marked Rollback-Only?

Look for inner failures inside shared transaction.

Symptom:

```text
UnexpectedRollbackException
```

### Step 7 - Enable Logs

```yaml
logging:
  level:
    org.springframework.transaction: TRACE
    org.springframework.transaction.interceptor: TRACE
    org.springframework.jdbc.datasource.DataSourceTransactionManager: DEBUG
    org.springframework.orm.jpa.JpaTransactionManager: DEBUG
    org.hibernate.SQL: DEBUG
```

You want to see:

```text
Creating new transaction
Participating in existing transaction
Suspending current transaction
Resuming suspended transaction
Initiating transaction commit
Initiating transaction rollback
```

### Step 8 - Inspect Pool Metrics

For HikariCP:

```text
hikaricp.connections.active
hikaricp.connections.idle
hikaricp.connections.pending
hikaricp.connections.timeout
```

If active is maxed and pending grows:

```text
Transactions may be too slow or too many.
```

---

## Debugging Guide

### Debug Snippet

```java
public static void printTx(String label) {
    System.out.println("==== " + label + " ====");
    System.out.println("thread = " + Thread.currentThread().getName());
    System.out.println("active = " +
        TransactionSynchronizationManager.isActualTransactionActive());
    System.out.println("readOnly = " +
        TransactionSynchronizationManager.isCurrentTransactionReadOnly());
    System.out.println("name = " +
        TransactionSynchronizationManager.getCurrentTransactionName());
    System.out.println("resources = " +
        TransactionSynchronizationManager.getResourceMap());
}
```

### Debugging Questions

```text
1. Is the method called through proxy?
2. Is the method public and interceptable?
3. Is transaction active inside method?
4. Are resources bound to the current thread?
5. Is repository running on same thread?
6. Is exception escaping?
7. Is rollback-only set?
8. Is the correct transaction manager used?
9. Is commit/rollback visible in logs?
10. Is the transaction holding connection too long?
```

### Common Log Interpretation

```text
Creating new transaction
```

Means:

```text
No existing transaction was found for this thread.
```

```text
Participating in existing transaction
```

Means:

```text
Propagation REQUIRED joined current transaction.
```

```text
Suspending current transaction
```

Means:

```text
A REQUIRES_NEW or NOT_SUPPORTED boundary is changing resource binding.
```

```text
Initiating transaction rollback
```

Means:

```text
Rollback path was selected.
```

---

## Performance Considerations

Transactions consume scarce resources.

Cost list:

```text
Database connection held
Locks held
MVCC versions retained
WAL/redo generated
Persistence context memory used
Dirty checking cost
Commit fsync cost
Potential replication lag
```

### Formula

```text
Active transactions ≈ RPS × transaction duration
```

Example:

```text
1000 RPS × 80 ms = 80 active transactions
```

If each active transaction holds one database connection:

```text
You need enough pool capacity.
```

But blindly increasing pool size can overload the database.

Better optimization:

```text
Shorten transaction duration.
```

### JPA Persistence Context Growth

Bad:

```java
@Transactional
public void importMillionRows(List<Row> rows) {
    for (Row row : rows) {
        entityManager.persist(toEntity(row));
    }
}
```

Problem:

```text
Persistence context stores many managed entities.
Memory grows.
Flush becomes expensive.
Rollback becomes expensive.
```

Better:

```java
for (int i = 0; i < rows.size(); i++) {
    entityManager.persist(toEntity(rows.get(i)));

    if (i % 1000 == 0) {
        entityManager.flush();
        entityManager.clear();
    }
}
```

Or chunk transactions where business-safe.

### Lock Duration

Long transaction:

```text
Start transaction
Update row
Wait 5 seconds
Commit
```

The row may be locked for 5 seconds.

Under load:

```text
blocked requests
long p99 latency
deadlock probability increases
connection pool pressure
```

Production rule:

```text
Do not do slow network I/O inside DB transaction.
```

---

## Scalability Considerations

### Single Service, Single Database

Spring transactions scale well when:

```text
Transaction scope is short
Queries are indexed
Locks are predictable
Connection pool is right-sized
Persistence context is not huge
Retry strategy handles deadlocks safely
```

### Multiple Services

`@Transactional` does not become distributed just because code calls another service.

```text
Service A local transaction
  !=
Service A + Service B + Kafka + Redis atomic transaction
```

At scale, use:

```text
Outbox pattern
Saga pattern
Idempotency keys
Compensating actions
At-least-once event handling
Deduplication
```

### Kubernetes Scale

Kubernetes can scale pods, but it cannot make database transactions cheaper.

If each pod has pool size 30 and you run 20 pods:

```text
Total possible DB connections = 600
```

Your database may not handle that.

Better thinking:

```text
Total DB connections = pod count × Hikari max pool size
```

Senior production checklist:

```text
Limit pool per pod
Autoscale carefully
Use readiness during startup
Use graceful shutdown
Keep transaction p99 low
Monitor DB locks and pool wait
```

---

## Production Failure Story

### Incident: Hidden Long Transaction Caused Pool Exhaustion

A checkout service had this method:

```java
@Transactional
public void checkout(Long userId, Long productId) {
    orderRepository.createPendingOrder(userId, productId);
    fraudClient.check(userId);              // remote API
    paymentGateway.authorize(userId);       // remote API
    inventoryRepository.reserve(productId);
    orderRepository.markConfirmed(userId);
}
```

During normal traffic it worked.

During sale traffic:

```text
Payment gateway latency increased from 200 ms to 4 seconds.
Fraud API occasionally took 2 seconds.
Checkout transactions stayed open for 4-6 seconds.
Hikari active connections reached maximum.
Pending threads grew.
Tomcat threads blocked waiting for DB connection.
p99 latency exploded.
Some requests timed out.
```

The team first blamed PostgreSQL.

But PostgreSQL was mostly waiting.

The real bug:

```text
The transaction held database connections while waiting for external APIs.
```

Failure diagram:

```text
BEGIN TX
   |
   v
Insert pending order
   |
   v
Call fraud API 2s
   |
   v
Call payment gateway 4s
   |
   v
DB connection held for 6s
   |
   v
Pool exhausted under traffic
```

Fix:

```text
1. Validate request without transaction.
2. Create pending order in short transaction.
3. Commit.
4. Call fraud/payment outside DB transaction.
5. Update order status in another short transaction.
6. Use idempotency key for payment retries.
7. Use outbox event for downstream notification.
```

Improved flow:

```text
TX 1: create pending order
COMMIT

External calls outside transaction

TX 2: mark paid/reserved or failed
COMMIT

Outbox publisher emits event
```

Senior lesson:

```text
A transaction protects local state.
It should not become a waiting room for remote systems.
```

---

## Common Misconceptions

### Misconception 1

```text
@Transactional means each repository method gets its own transaction.
```

Correct:

```text
The service method boundary usually creates one transaction shared by all repositories on the same thread.
```

### Misconception 2

```text
Repositories magically know the transaction.
```

Correct:

```text
They reuse resources bound in TransactionSynchronizationManager.
```

### Misconception 3

```text
Commit only means Java method returned.
```

Correct:

```text
Commit also involves flush, database commit, callbacks, cleanup, and connection release.
```

### Misconception 4

```text
Read-only guarantees no writes.
```

Correct:

```text
Read-only is mainly a transaction/provider optimization hint, not a business security boundary.
```

### Misconception 5

```text
Async code participates in the same transaction.
```

Correct:

```text
Transaction context is thread-bound. New thread usually means no same transaction.
```

### Misconception 6

```text
REQUIRES_NEW is just stronger REQUIRED.
```

Correct:

```text
REQUIRES_NEW suspends the outer transaction and starts an independent transaction.
```

### Misconception 7

```text
If I catch an inner exception, outer transaction can always commit.
```

Correct:

```text
If the shared transaction was marked rollback-only, commit can become rollback and throw UnexpectedRollbackException.
```

---

## Java / Spring Boot Code Examples With Internal Execution Explanation

### Example 1 - REQUIRED Joining

```java
@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @Transactional
    public void placeOrder(Long userId) {
        orderService.createOrder(userId);
        paymentService.createPaymentAttempt(userId);
    }
}
```

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public void createOrder(Long userId) {
        orderRepository.insert(userId);
    }
}
```

Internal execution:

```text
OrderFacade.placeOrder starts transaction conn-17.
OrderService.createOrder uses REQUIRED.
Existing conn-17 found.
OrderService joins conn-17.
Only outer method commits.
```

### Example 2 - REQUIRES_NEW Audit

```java
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String message) {
        auditRepository.insert(message);
    }
}
```

Internal execution:

```text
Outer transaction active with conn-17.
Audit record called through proxy.
Spring suspends conn-17.
Spring starts conn-18.
Audit commits conn-18.
Spring resumes conn-17.
```

Production warning:

```text
Audit may commit even if outer business transaction rolls back.
That can be useful, but only if intentional.
```

### Example 3 - Manual Rollback-Only

```java
@Transactional
public void checkout() {
    try {
        paymentRepository.insertAttempt();
        inventoryRepository.reserve();
    } catch (Exception e) {
        TransactionAspectSupport
            .currentTransactionStatus()
            .setRollbackOnly();
    }
}
```

Internal execution:

```text
Method catches exception and returns normally.
But transaction status is marked rollback-only.
Commit attempt becomes rollback.
```

Use carefully.

Usually simpler:

```java
catch (Exception e) {
    throw e;
}
```

---

## Interview Q&A

### Q1. What is the core internal mechanism behind Spring transactions?

Spring binds transaction resources such as JDBC connections or JPA EntityManagers to the current thread using `TransactionSynchronizationManager`. Repository calls on that same thread reuse the bound resources. The transaction manager commits or rolls back those resources when the transactional method completes.

### Q2. What does `PlatformTransactionManager` do?

It coordinates the transaction lifecycle. It starts or joins a transaction, applies transaction settings, binds resources, commits or rolls back, and cleans up resources.

### Q3. How do repositories use the same connection?

Spring-aware data access infrastructure checks the current thread's transaction resource map. If a `ConnectionHolder` is bound for the DataSource, repository code reuses that connection instead of opening an independent one.

### Q4. What is `TransactionSynchronizationManager`?

It is Spring's thread-local transaction context manager. It stores resources and callbacks associated with the current transaction on the current thread.

### Q5. Why does transaction context break with async code?

Because transaction resources are thread-bound. Async code usually runs on a different thread, and that thread does not automatically have the original thread's transaction resources.

### Q6. What happens at commit in JPA?

Before commit, Hibernate usually flushes the persistence context, converting dirty/new entity changes into SQL. Then the database transaction commits. After completion, resources are cleaned up and the EntityManager is closed or unbound.

### Q7. What is rollback-only?

Rollback-only is a transaction status flag meaning the transaction must roll back even if the outer method returns normally. It often appears when an inner participant fails inside a shared transaction.

### Q8. Why can `UnexpectedRollbackException` happen?

An inner operation participating in the same transaction may mark the transaction rollback-only. The outer method catches the exception and returns normally, but when Spring tries to commit, it discovers rollback-only and rolls back instead.

### Q9. What is the difference between REQUIRED and REQUIRES_NEW internally?

`REQUIRED` joins an existing thread-bound transaction if one exists. `REQUIRES_NEW` suspends the current transaction resources, starts a new independent transaction with new resources, completes it, then resumes the outer resources.

### Q10. What is the best production rule for transaction internals?

Keep transaction scope short. Avoid remote calls, large loops, and slow operations inside a transaction because the connection, locks, and persistence context remain active until completion.

---

## Production Checklist

```text
[ ] Transactional method is called through Spring proxy.
[ ] Transaction is active where expected.
[ ] Correct transaction manager is used.
[ ] Same thread is used for repository operations.
[ ] ThreadLocal resources are visible during method execution.
[ ] Exceptions that should rollback are not swallowed.
[ ] Checked exceptions have rollbackFor when needed.
[ ] REQUIRES_NEW usage is intentional.
[ ] Rollback-only scenarios are understood.
[ ] No remote API calls inside DB transaction.
[ ] Transaction duration is monitored.
[ ] Hikari active/pending/timeout metrics are monitored.
[ ] JPA persistence context is not growing unbounded.
[ ] Batch jobs use chunking where safe.
[ ] DB locks and deadlocks are monitored.
[ ] Outbox is used for DB + Kafka reliability.
```

---

## One-Page Cheat Sheet

```text
@Transactional internals
        =
Thread-bound resource lifecycle
```

Main flow:

```text
Proxy
  |
  v
TransactionInterceptor
  |
  v
PlatformTransactionManager
  |
  v
TransactionSynchronizationManager
  |
  v
Bind Connection / EntityManager to ThreadLocal
  |
  v
Run business method
  |
  v
Repositories reuse bound resource
  |
  v
Flush
  |
  v
Commit or rollback
  |
  v
Unbind and release resources
```

Thread model:

```text
Thread-42
  |
  +--> DataSource -> ConnectionHolder(conn-17)
  +--> EMFactory  -> EntityManagerHolder(em-9)
```

Propagation:

```text
REQUIRED:
    join existing transaction or start new one

REQUIRES_NEW:
    suspend existing transaction
    start independent transaction
    commit/rollback inner
    resume outer
```

Rollback:

```text
RuntimeException escapes -> rollback by default
Checked exception escapes -> commit by default unless rollbackFor
rollbackOnly flag -> rollback even if method returns normally
```

Async warning:

```text
Transaction context is thread-bound.
New thread usually means no same transaction.
```

Production formula:

```text
Active transactions ≈ RPS × transaction duration
```

Main rule:

```text
Keep transaction scope short.
```

---

## One Picture To Remember

```text
                 ONE REQUEST THREAD
+------------------------------------------------------+
| Thread-42                                            |
|                                                      |
|  +-----------------------------------------------+   |
|  | TransactionSynchronizationManager              |   |
|  |                                               |   |
|  | DataSource -> ConnectionHolder(conn-17)        |   |
|  | EMFactory  -> EntityManagerHolder(em-9)        |   |
|  +-----------------------------------------------+   |
|                                                      |
|  CheckoutService.checkout()                         |
|      |                                               |
|      +--> OrderRepository      uses conn-17          |
|      +--> InventoryRepository  uses conn-17          |
|      +--> PaymentRepository    uses conn-17          |
|                                                      |
+------------------------------------------------------+
                         |
                         v
                commit / rollback once
                         |
                         v
                 unbind + release resources
```

Final memory hook:

```text
Spring transaction internals are not hidden SQL magic.

They are:

Bind resource to thread.
Reuse resource during method.
Complete resource at boundary.
Clean thread before returning.
```

---

## Key Takeaways

1. `@Transactional` starts a lifecycle, not just a database command.

2. The central internal idea is thread-bound transaction resources.

3. `TransactionSynchronizationManager` stores resources in ThreadLocal maps.

4. Repositories reuse the current thread's bound connection or EntityManager.

5. `PlatformTransactionManager` begins, joins, commits, rolls back, and cleans up.

6. JPA transactions include persistence context, dirty checking, flush, and commit.

7. `REQUIRED` joins an existing transaction; `REQUIRES_NEW` suspends and creates another.

8. Rollback-only can cause rollback even when the outer method returns normally.

9. Async boundaries usually lose transaction context because the thread changes.

10. Long transactions hurt connection pools, locks, p99 latency, and scalability.

11. Local Spring transactions do not automatically cover remote services, Kafka, Redis, email, or S3.

12. Senior engineers debug transactions by checking proxy, active transaction, bound resources, thread, rollback-only state, and commit/rollback logs.
