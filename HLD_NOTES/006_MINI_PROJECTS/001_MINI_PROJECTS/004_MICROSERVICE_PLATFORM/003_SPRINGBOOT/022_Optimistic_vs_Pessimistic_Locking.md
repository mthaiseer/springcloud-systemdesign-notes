# 022_Optimistic_vs_Pessimistic_Locking — The Conflict Strategy Model

## Core Mental Model

Do not memorize optimistic and pessimistic locking as two random annotations.

The better mental model is:

> **Optimistic locking says: “Most conflicts will not happen, so allow work first and detect conflict later.”**  
> **Pessimistic locking says: “Conflict is likely or dangerous, so block others before doing work.”**

```text
Two users update same row

Optimistic Locking
------------------
Both users read
Both users work
First commit wins
Second commit detects version mismatch and fails/retries

Pessimistic Locking
-------------------
First user locks row
Second user waits
First user commits
Second user continues after lock release
```

This chapter teaches exactly one idea:

> **Locking is a conflict strategy. Optimistic detects conflicting writes after they happen logically. Pessimistic prevents conflicting writes by locking database rows before they happen.**

If you remember only one sentence:

> **Optimistic locking protects correctness by version checking; pessimistic locking protects correctness by database blocking.**

---

## Why This Exists

Databases are shared.

Your Spring Boot service may run many instances:

```text
User A request  ---> App Instance 1
User B request  ---> App Instance 2
User C request  ---> App Instance 3
```

All of them may touch the same row:

```text
products
+------+----------+-------+
| id   | name     | stock |
+------+----------+-------+
| 1001 | Keyboard | 1     |
+------+----------+-------+
```

Now two users try to buy the last keyboard at the same time.

Without a conflict strategy:

```text
Transaction A reads stock = 1
Transaction B reads stock = 1

A decreases to 0
B decreases to 0

Both orders succeed
But only one item existed
```

That is a lost update / oversell class of bug.

This is why locking exists.

Not because databases are weak.

Because concurrent business decisions must be protected.

---

## Problem Statement

Imagine this method:

```java
@Transactional
public void buy(Long productId, int quantity) {
    Product product = productRepository.findById(productId)
            .orElseThrow();

    if (product.getStock() < quantity) {
        throw new BusinessException("Out of stock");
    }

    product.decreaseStock(quantity);
}
```

It looks correct in single-user testing.

But under concurrency:

```text
Initial stock = 1

T1 reads stock 1
T2 reads stock 1

T1 says enough stock
T2 says enough stock

T1 writes stock 0
T2 writes stock 0

Two successful orders
One real item
```

The core problem:

> **How do we protect a business decision when multiple transactions read and write the same row concurrently?**

There are two main strategies:

```text
Optimistic:
  Let both transactions proceed.
  At update time, check if row version is still the same.
  If not, reject one transaction.

Pessimistic:
  Lock the row when reading.
  Other transactions must wait or fail before making conflicting decisions.
```

---

## Real World Analogy

### Optimistic Locking Analogy — Google Docs Version Check

Imagine two people open a document.

```text
Version 7 loaded by Alice
Version 7 loaded by Bob
```

Alice saves first:

```text
Expected version: 7
Current version: 7
Save allowed
New version: 8
```

Bob saves later:

```text
Expected version: 7
Current version: 8
Conflict detected
Bob must reload/merge/retry
```

Bob was not blocked while editing.
The conflict was detected later.

That is optimistic locking.

### Pessimistic Locking Analogy — Checkout Counter

Imagine one cashier counter with one physical item.

```text
Customer A holds the item at checkout.
Customer B cannot also buy it at the same time.
Customer B waits.
```

The conflict is prevented early by exclusive control.

That is pessimistic locking.

---

## The One Mental Model

```text
Same row, multiple transactions

                   Conflict Strategy
                           |
          +----------------+----------------+
          |                                 |
          v                                 v
  Optimistic Locking                 Pessimistic Locking
  ------------------                 -------------------
  Detect later                       Block earlier
  Version column                     SELECT ... FOR UPDATE
  Retry on conflict                  Wait / timeout / deadlock risk
  Better when conflicts rare         Better when conflicts likely/dangerous
```

Ask one senior-engineer question:

> **Is it cheaper to retry after conflict, or safer to block before conflict?**

That question decides the locking strategy.

---

## Core Concepts

## Optimistic Locking

Optimistic locking uses a version column.

```java
@Entity
public class Product {

    @Id
    private Long id;

    private String name;

    private int stock;

    @Version
    private Long version;

    protected Product() {}

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (stock < quantity) {
            throw new IllegalStateException("not enough stock");
        }
        this.stock -= quantity;
    }
}
```

Hibernate update becomes conceptually:

```sql
UPDATE products
SET stock = ?, version = version + 1
WHERE id = ? AND version = ?;
```

If another transaction already changed the row, version no longer matches.

```text
Expected version = 3
Database version = 4

Rows updated = 0
Hibernate throws optimistic locking exception
```

Optimistic locking does not stop concurrent reads.
It detects stale writes.

---

## Pessimistic Locking

Pessimistic locking asks the database to lock the row.

```java
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(Long id);
}
```

Database behavior is conceptually:

```sql
SELECT *
FROM products
WHERE id = ?
FOR UPDATE;
```

Transaction A locks the row.

Transaction B trying to lock the same row waits until A commits or rolls back.

```text
T1: SELECT ... FOR UPDATE  -> lock acquired
T2: SELECT ... FOR UPDATE  -> waits

T1: update + commit        -> lock released
T2: continues              -> sees latest row
```

Pessimistic locking prevents conflicting writes by serializing access to that row.

---

## Internal Architecture

```text
Spring Service
    |
    v
@Transactional boundary
    |
    v
Repository method
    |
    +-- Optimistic:
    |      normal SELECT
    |      entity has @Version
    |      UPDATE includes version check
    |
    +-- Pessimistic:
           SELECT ... FOR UPDATE
           database row lock acquired
           other writers wait/fail
```

Hibernate participates, but the database is the final lock enforcer.

```text
Optimistic:
  Hibernate checks update row count/version mismatch.

Pessimistic:
  Database lock manager blocks competing transactions.
```

---

## Internal Working — Optimistic Locking

Service:

```java
@Transactional
public void reserveOptimistic(Long productId, int quantity) {
    Product product = productRepository.findById(productId)
            .orElseThrow();

    product.decreaseStock(quantity);
}
```

Entity:

```java
@Version
private Long version;
```

Database before:

```text
products
+------+-------+---------+
| id   | stock | version |
+------+-------+---------+
| 1001 | 1     | 3       |
+------+-------+---------+
```

Two transactions:

```text
T1 reads stock=1, version=3
T2 reads stock=1, version=3
```

T1 commits:

```sql
UPDATE products
SET stock = 0, version = 4
WHERE id = 1001 AND version = 3;
```

Rows updated:

```text
1
```

T2 commits:

```sql
UPDATE products
SET stock = 0, version = 4
WHERE id = 1001 AND version = 3;
```

Rows updated:

```text
0
```

Hibernate says:

```text
This transaction updated stale data.
Throw OptimisticLockException / ObjectOptimisticLockingFailureException.
```

Correctness is preserved because only one write wins.

---

## Internal Working — Pessimistic Locking

Repository:

```java
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(Long id);
}
```

Service:

```java
@Transactional
public void reservePessimistic(Long productId, int quantity) {
    Product product = productRepository.findByIdForUpdate(productId)
            .orElseThrow();

    product.decreaseStock(quantity);
}
```

Flow:

```text
T1 starts
T1 SELECT product FOR UPDATE
T1 gets database row lock

T2 starts
T2 SELECT product FOR UPDATE
T2 waits

T1 decreases stock from 1 to 0
T1 commits
T1 releases lock

T2 continues
T2 sees stock = 0
T2 throws OutOfStock
T2 rolls back
```

Pessimistic locking makes the second transaction wait before making the decision.

---

## Rich ASCII Diagram — Optimistic Locking

```text
Initial row:
Product#1001 stock=1 version=3

        Transaction A                         Transaction B
        -------------                         -------------
        read stock=1                          read stock=1
        read version=3                        read version=3
              |                                     |
              v                                     v
        decrease stock                         decrease stock
              |                                     |
              v                                     v
 UPDATE ... WHERE version=3              UPDATE ... WHERE version=3
              |                                     |
              v                                     v
        success, version=4                    0 rows updated
                                                  |
                                                  v
                                      Optimistic lock exception
```

Mental picture:

```text
Both enter the room.
Only first one can sign the version sheet.
Second sees the sheet changed and must retry.
```

---

## Rich ASCII Diagram — Pessimistic Locking

```text
Initial row:
Product#1001 stock=1

        Transaction A                         Transaction B
        -------------                         -------------
 SELECT ... FOR UPDATE
        |
        v
  row lock acquired
        |
        |                                 SELECT ... FOR UPDATE
        |                                         |
        |                                         v
        |                                      waits
        |
 decrease stock 1 -> 0
        |
      commit
        |
        v
   lock released
                                                  |
                                                  v
                                           reads stock=0
                                                  |
                                                  v
                                           OutOfStock
```

Mental picture:

```text
Only one person enters the room.
Others wait outside until the room is free.
```

---

## Step-by-Step Dry Run — Optimistic Normal Flow

Input:

```text
reserve product 1001 quantity 1
```

Database before:

```text
stock=10, version=5
```

Flow:

```text
1. Service starts transaction.
2. Product is loaded.
3. Hibernate stores snapshot: stock=10, version=5.
4. Business method changes stock to 9.
5. Flush happens at commit.
6. Hibernate generates UPDATE with WHERE version=5.
7. Database row version is still 5.
8. One row updated.
9. Version becomes 6.
10. Commit succeeds.
```

Database after:

```text
stock=9, version=6
```

No retry needed.

---

## Step-by-Step Dry Run — Optimistic Conflict Flow

```text
Initial: stock=1, version=7
```

```text
1. T1 loads Product#1001 version 7.
2. T2 loads Product#1001 version 7.
3. T1 decreases stock to 0.
4. T1 flushes.
5. Database version 7 matches.
6. T1 update succeeds and version becomes 8.
7. T2 decreases stock to 0 in its memory.
8. T2 flushes.
9. Hibernate tries WHERE version=7.
10. Database version is now 8.
11. Update affects 0 rows.
12. Optimistic lock exception is thrown.
13. T2 transaction rolls back.
14. Application may return conflict or retry.
```

Correct response strategy depends on use case:

```text
For user-facing update:
  return 409 Conflict or ask user to refresh

For background command:
  retry with fresh data if operation is safe

For payment-like operation:
  be careful; retry only with idempotency
```

---

## Step-by-Step Dry Run — Pessimistic Flow

```text
Initial: stock=1
```

```text
1. T1 starts transaction.
2. T1 calls findByIdForUpdate(1001).
3. Database locks product row.
4. T2 starts transaction.
5. T2 calls findByIdForUpdate(1001).
6. T2 waits.
7. T1 decreases stock to 0.
8. T1 commits.
9. Database releases lock.
10. T2 query continues.
11. T2 sees stock=0.
12. T2 fails business rule.
13. T2 rolls back.
```

No optimistic exception.
But there was waiting.

---

## Java/Spring Boot Code Example

### Entity With Version

```java
@Entity
@Table(name = "products")
public class Product {

    @Id
    private Long id;

    private String name;

    private int stock;

    @Version
    private Long version;

    protected Product() {}

    public boolean hasEnoughStock(int quantity) {
        return stock >= quantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (!hasEnoughStock(quantity)) {
            throw new IllegalStateException("not enough stock");
        }
        this.stock -= quantity;
    }

    public Long getVersion() {
        return version;
    }
}
```

### Repository

```java
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}
```

### Optimistic Service

```java
@Service
public class StockService {

    private final ProductRepository productRepository;

    public StockService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void reserveOptimistic(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));

        product.decreaseStock(quantity);
    }
}
```

Execution:

```text
findById loads normal managed entity.
@Version field is remembered.
Dirty checking detects stock change.
Flush UPDATE includes version condition.
Conflict becomes optimistic lock exception.
```

### Pessimistic Service

```java
@Transactional
public void reservePessimistic(Long productId, int quantity) {
    Product product = productRepository.findByIdForUpdate(productId)
            .orElseThrow(() -> new IllegalArgumentException("product not found"));

    product.decreaseStock(quantity);
}
```

Execution:

```text
findByIdForUpdate loads entity with database write lock.
Other transactions trying similar lock must wait.
Business decision happens while row is protected.
Commit releases lock.
```

---

## Spring Boot REST Example

```java
@RestController
@RequestMapping("/api/products")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/{id}/reserve/optimistic")
    public ResponseEntity<Void> reserveOptimistic(@PathVariable Long id,
                                                  @RequestParam int quantity) {
        stockService.reserveOptimistic(id, quantity);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reserve/pessimistic")
    public ResponseEntity<Void> reservePessimistic(@PathVariable Long id,
                                                   @RequestParam int quantity) {
        stockService.reservePessimistic(id, quantity);
        return ResponseEntity.noContent().build();
    }
}
```

Exception handling:

```java
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> optimisticConflict() {
        return ResponseEntity.status(409)
                .body(new ErrorResponse("Resource was changed by another transaction. Please retry."));
    }

    @ExceptionHandler(PessimisticLockException.class)
    public ResponseEntity<ErrorResponse> pessimisticFailure() {
        return ResponseEntity.status(503)
                .body(new ErrorResponse("Resource is busy. Please retry shortly."));
    }
}
```

```java
public record ErrorResponse(String message) {}
```

---

## Production Scale Example

Imagine a flash sale.

```text
Product stock = 100
10,000 users click Buy Now within 5 seconds
```

### With Optimistic Locking

```text
Many users read same version.
Many updates conflict.
Only some succeed.
Many retries may happen.
```

Good:

```text
No long waiting row locks
High throughput when conflicts are rare
Simple for normal profile updates/order edits
```

Bad under extreme hot row:

```text
Retry storm
Many failed transactions
High CPU and DB pressure
Poor user experience if not controlled
```

### With Pessimistic Locking

```text
One transaction locks stock row.
Others queue behind it.
```

Good:

```text
Strong serialization for hot critical row
Fewer failed retries
Business decision sees latest state
```

Bad:

```text
Lock waits
Timeouts
Deadlock risk
Lower concurrency
Long transactions become dangerous
```

Senior approach for flash sale:

```text
Do not blindly lock one product row for everything.
Consider:
  - Redis atomic decrement with DB reconciliation
  - reservation table
  - queue-based ordering
  - inventory bucket sharding
  - idempotency keys
  - optimistic lock with bounded retry
```

But inside JPA/Hibernate, the core choice remains:

```text
detect conflict later vs block conflict earlier
```

---

## Production Failure Story

A team built wallet debit using plain JPA.

```java
@Transactional
public void debit(Long walletId, BigDecimal amount) {
    Wallet wallet = walletRepository.findById(walletId).orElseThrow();

    if (wallet.balance().compareTo(amount) < 0) {
        throw new BusinessException("insufficient balance");
    }

    wallet.debit(amount);
}
```

In testing, it passed.

In production, two payment requests arrived at the same time.

```text
Wallet balance = 100

T1 reads 100
T2 reads 100

T1 debits 80 -> balance 20
T2 debits 80 -> balance 20

Two payments succeeded.
Real balance should not allow both.
```

Root cause:

```text
No concurrency conflict strategy.
Business decision was based on stale concurrent read.
```

Fix option 1:

```text
Add @Version to Wallet.
Second transaction fails optimistic lock.
Payment command retries or returns conflict.
```

Fix option 2:

```text
Use PESSIMISTIC_WRITE for wallet row.
Second transaction waits.
After first commits, second sees balance 20 and fails.
```

Final production lesson:

> **Business invariants that depend on current row state need a concurrency strategy, not just @Transactional.**

---

## Debugging Mindset

When concurrency bugs happen, ask:

```text
1. Which row is the shared contention point?
2. Did multiple transactions read the same old value?
3. Is there a @Version column?
4. Does the UPDATE include version in WHERE?
5. Is a pessimistic lock actually being requested?
6. Is the transaction long enough to hold the lock safely?
7. Are retries bounded and idempotent?
8. Are lock waits/deadlocks visible in DB metrics?
```

### Symptom Map

```text
Overselling stock
  -> missing lock strategy or wrong isolation assumption

ObjectOptimisticLockingFailureException
  -> optimistic conflict detected correctly
  -> handle 409/retry

Lock timeout
  -> pessimistic lock contention or long transaction

Deadlock
  -> multiple locks acquired in inconsistent order

High retry rate
  -> optimistic locking on hot row may be wrong strategy

Low throughput
  -> pessimistic locking may serialize too much work
```

---

## Common Misconceptions

### Misconception 1: “@Transactional prevents all concurrency problems”

No.

`@Transactional` gives atomicity for one transaction.
It does not automatically prevent two transactions from reading the same old value and making conflicting decisions.

### Misconception 2: “Optimistic locking locks the row”

No.

Optimistic locking usually does not block other transactions.
It detects conflict through version mismatch during update/flush.

### Misconception 3: “Pessimistic locking is always safer”

It prevents some conflicts earlier, but it can cause waiting, deadlocks, timeouts, and throughput collapse if used carelessly.

### Misconception 4: “Optimistic locking is always faster”

It is excellent when conflicts are rare.
For hot rows with frequent conflicts, retries may become more expensive than waiting.

### Misconception 5: “Database isolation level alone is enough”

Sometimes isolation helps, but relying blindly on default isolation is dangerous.
For business invariants like stock, wallet balance, or seat reservation, use explicit conflict strategy.

### Misconception 6: “Retry optimistic lock forever”

Never retry forever.

Use bounded retry:

```text
Try 1
Conflict
Reload fresh state
Try 2
Conflict
Return conflict / queue / fail gracefully
```

Retries must be idempotent.

---

## Performance Considerations

### Optimistic Locking Performance

Best when:

```text
- conflicts are rare
- reads are common
- writes are moderate
- user can retry
- stale update is acceptable to reject
```

Cost:

```text
- failed transactions under conflict
- retry logic
- extra version column
- potential retry storm
```

### Pessimistic Locking Performance

Best when:

```text
- conflicts are frequent
- decision must use latest value
- retry is expensive or dangerous
- row-level serialization is acceptable
```

Cost:

```text
- lock wait time
- deadlocks
- blocked transactions holding DB connections
- reduced concurrency
```

Rule:

```text
Rare conflict     -> optimistic
Frequent conflict -> pessimistic or redesign hot spot
Very hot row      -> often redesign beyond simple JPA locking
```

---

## Scalability Considerations

At scale, locking is not only a correctness tool.
It is a throughput design decision.

```text
Optimistic on hot row:
  many transactions do work and fail at the end

Pessimistic on hot row:
  many transactions wait before doing work

Queue:
  serialize work intentionally outside request thread

Atomic counter:
  reduce DB transaction pressure

Shard inventory:
  split one hot row into many smaller contention points
```

For senior system design:

```text
Locking strategy should match contention shape.
```

Contention shape means:

```text
How many users touch the same row?
How long does each transaction hold the row?
Can the operation be retried safely?
Can the business tolerate eventual reservation?
```

---

## Failure Investigation Playbook

### Step 1 — Reproduce with concurrent test

Use two threads or integration tests.

```text
Initial stock = 1
Run two reserve requests at same time
Expected: one success, one failure
Bad: two successes
```

### Step 2 — Inspect SQL

For optimistic locking, check:

```text
UPDATE ... WHERE id=? AND version=?
```

For pessimistic locking, check:

```text
SELECT ... FOR UPDATE
```

### Step 3 — Inspect transaction boundary

Check:

```text
Is @Transactional on public service method?
Is repository lock method called inside transaction?
Is the transaction ending too early?
Is self-invocation bypassing proxy?
```

### Step 4 — Inspect exception handling

Check:

```text
Are optimistic conflicts swallowed?
Are retries unbounded?
Are lock timeouts mapped correctly?
```

### Step 5 — Inspect hot-row pressure

Check:

```text
DB lock wait metrics
deadlock logs
retry count
p99 latency
connection pool usage
```

---

## Interview Q&A

### Q1. What is optimistic locking?

Strong answer:

> Optimistic locking assumes conflicts are rare. It uses a version column, usually with `@Version`. When Hibernate updates the row, it includes the expected version in the `WHERE` clause. If another transaction already changed the row, the version mismatch causes the update to affect zero rows and Hibernate throws an optimistic locking exception.

### Q2. What is pessimistic locking?

Strong answer:

> Pessimistic locking assumes conflict is likely or dangerous. It asks the database to lock the row, commonly using `SELECT FOR UPDATE` through `LockModeType.PESSIMISTIC_WRITE`. Other transactions trying to acquire a conflicting lock wait or fail until the first transaction commits or rolls back.

### Q3. When would you use optimistic locking?

Strong answer:

> I use optimistic locking when conflicts are rare and retry or conflict response is acceptable, such as profile updates, order edits, or moderate stock changes.

### Q4. When would you use pessimistic locking?

Strong answer:

> I use pessimistic locking when conflicting decisions are likely or expensive, such as wallet debit, seat reservation, or small hot inventory, and when the transaction is short enough to safely hold a database lock.

### Q5. Does `@Transactional` solve lost updates?

Strong answer:

> Not by itself. It makes one unit of work atomic, but concurrent transactions can still read the same old state. To protect against lost updates, we need a locking strategy, version check, atomic SQL update, or stronger isolation depending on the case.

### Q6. What are the risks of pessimistic locking?

Strong answer:

> It can cause lock waits, deadlocks, timeouts, lower throughput, and connection pool pressure. It must be used with short transactions and consistent lock ordering.

### Q7. What are the risks of optimistic locking?

Strong answer:

> Under high contention, many transactions may fail at flush time, causing retries, wasted work, and retry storms. It needs bounded retry and idempotency.

---

## Production Checklist

```text
Business Invariant
[ ] What shared row/state is being protected?
[ ] Can two transactions read the same value?
[ ] What happens if both write?

Optimistic Locking
[ ] Does the entity have @Version?
[ ] Is the version column persisted?
[ ] Are optimistic exceptions handled?
[ ] Are retries bounded?
[ ] Are retries idempotent?

Pessimistic Locking
[ ] Is @Lock(PESSIMISTIC_WRITE) used on the correct query?
[ ] Is the method inside @Transactional?
[ ] Is the transaction short?
[ ] Are lock timeouts configured/handled?
[ ] Is lock acquisition order consistent?

Observability
[ ] Can we see optimistic conflict rate?
[ ] Can we see DB lock waits?
[ ] Can we see deadlocks?
[ ] Can we see retry count?
[ ] Can we see p99 latency during contention?
```

---

## One-Page Cheat Sheet

```text
Optimistic vs Pessimistic Locking
=================================

Optimistic Locking
------------------
Assumption:
  Conflicts are rare.

Mechanism:
  @Version column.
  UPDATE uses WHERE id=? AND version=?.

Behavior:
  No blocking during read.
  Conflict detected at flush/update.
  One transaction wins.
  Others fail/retry.

Best for:
  Rare conflicts, user edits, moderate writes.

Risk:
  Retry storm under hot-row contention.

Pessimistic Locking
-------------------
Assumption:
  Conflicts are likely/dangerous.

Mechanism:
  SELECT ... FOR UPDATE.
  @Lock(PESSIMISTIC_WRITE).

Behavior:
  Row locked early.
  Other transactions wait/fail.
  Business decision sees protected row.

Best for:
  Wallet debit, seat reservation, high-value stock decision.

Risk:
  Lock waits, deadlocks, lower throughput.

Best Question
-------------
Is it cheaper to retry after conflict,
or safer to block before conflict?
```

---

## Last-Minute Interview Revision

Do not say:

```text
Optimistic is faster, pessimistic is safer.
```

Say:

```text
Optimistic detects conflicts with version checks and works best when conflicts are rare. Pessimistic prevents conflicts by locking rows and works best when conflicts are likely or the business decision must be protected before proceeding.
```

Senior version:

```text
I choose locking based on contention shape. If conflicts are rare, I prefer optimistic locking with bounded retry. If conflicts are frequent or financially dangerous, I use pessimistic locking or redesign the hot spot using queues, atomic updates, or reservation models.
```

---

## One Picture To Remember

```text
               SAME ROW, TWO TRANSACTIONS
                          |
          +---------------+---------------+
          |                               |
          v                               v
   OPTIMISTIC                         PESSIMISTIC
   Detect later                       Block earlier
   -----------                        -------------
   read version=3                     SELECT FOR UPDATE
   do work                            lock row
   UPDATE WHERE version=3             others wait
   if 0 rows -> conflict              commit releases lock
          |                               |
          v                               v
   retry / 409 conflict              latest state decision
```

Final retention sentence:

> **Optimistic locking is a version-based conflict detector; pessimistic locking is a database row-lock conflict preventer.**
