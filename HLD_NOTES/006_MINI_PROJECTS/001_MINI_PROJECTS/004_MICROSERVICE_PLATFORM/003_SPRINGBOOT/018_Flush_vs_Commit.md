# 018_Flush_vs_Commit — The Synchronize vs Make Permanent Model

## Core Mental Model

Do not imagine `flush` and `commit` as the same thing.
That misunderstanding causes many Hibernate/JPA production bugs.

The better mental model is:

> **Flush sends SQL to the database inside the current transaction. Commit makes that transaction permanent.**

```text
Java Entity Changes
        |
        v
+-------------------------+
| Persistence Context     |
| managed objects changed |
+-------------------------+
        |
        | flush
        v
+-------------------------+
| Database Transaction    |
| SQL executed, not final |
+-------------------------+
        |
        | commit
        v
+-------------------------+
| Permanent Database State|
| visible as committed    |
+-------------------------+
```

This chapter teaches exactly one idea:

> **Flush is synchronization. Commit is finalization.**

If you remember only one sentence:

> **Flush writes pending SQL to the database, but commit decides whether those writes survive.**

---

## Why This Exists

In Hibernate, you usually change Java objects:

```java
product.decreaseStock(2);
order.markPaid();
customer.changeAddress(address);
```

But the database understands SQL:

```sql
UPDATE products SET stock = 8 WHERE id = 1001;
UPDATE orders SET status = 'PAID' WHERE id = 501;
UPDATE customers SET address = ? WHERE id = 42;
```

Hibernate needs a moment when it converts changed managed entities into SQL.
That moment is called **flush**.

But executing SQL is not the same as permanently saving it.
Databases run inside transactions.
A transaction can still be committed or rolled back.

```text
SQL executed inside transaction
        |
        +-- commit   -> changes become permanent
        |
        +-- rollback -> changes disappear
```

So Hibernate needs two different concepts:

```text
flush  -> synchronize persistence context to DB transaction
commit -> permanently complete DB transaction
```

Without this distinction, developers ask confusing questions:

```text
I called flush. Why did rollback undo it?
I did not call save. Why did SQL run before my query?
Why did constraint violation happen before method end?
Why did commit take so long?
Why did insert happen early with IDENTITY id?
```

All these become simple when you separate flush and commit.

---

## Problem Statement

Suppose this service method runs:

```java
@Transactional
public void reserve(Long productId, int quantity) {
    Product product = productRepository.findById(productId).orElseThrow();
    product.decreaseStock(quantity);

    auditRepository.save(new AuditLog("reserved stock"));

    if (quantity > 10) {
        throw new BusinessException("large reservation blocked");
    }
}
```

Questions:

```text
When does UPDATE product happen?
When does INSERT audit_log happen?
If SQL already executed, can rollback still undo it?
If flush happens before commit, is data visible to everyone?
```

The answer:

```text
Hibernate may flush SQL before commit.
But until commit succeeds, the transaction is not permanent.
If rollback happens, flushed SQL is undone.
```

The core problem:

> **Developers need to know when Hibernate sends SQL and when the database actually makes it durable.**

That is the whole flush vs commit model.

---

## Real World Analogy

Imagine a bank teller preparing a money transfer.

```text
Customer asks to transfer money.
Teller enters debit and credit into the banking terminal.
System validates account balance.
Manager approves final transaction.
```

The teller entering the data is like **flush**.
The bank final approval is like **commit**.

```text
Bank analogy                 Hibernate/JPA
------------                 -------------
Write transfer in terminal   Flush SQL
Pending transaction          DB transaction
Final approval               Commit
Cancel before approval       Rollback
```

Important:

```text
The bank system may already see the pending entries.
But if the transaction is cancelled, they are not permanent.
```

Hibernate is the same.

```text
flush  -> database sees SQL inside current transaction
commit -> database makes it permanent
rollback -> database discards it
```

---

## The One Mental Model

Think of a transaction as a two-stage pipeline.

```text
Stage 1: Hibernate Workbench
---------------------------
Java objects are changed.
Persistence context tracks dirty entities.
No SQL may be sent yet.

Stage 2: Database Transaction
-----------------------------
Flush sends SQL to DB transaction.
Commit makes DB transaction permanent.
Rollback removes all uncommitted work.
```

ASCII picture:

```text
+----------------------------+
| Java / Hibernate Memory     |
|----------------------------|
| Product stock: 10 -> 8      |
| Order status: NEW -> PAID   |
+----------------------------+
             |
             | FLUSH
             v
+----------------------------+
| Database Transaction        |
|----------------------------|
| UPDATE products ...         |
| UPDATE orders ...           |
| Not permanent yet           |
+----------------------------+
             |
       +-----+------+
       |            |
       | COMMIT     | ROLLBACK
       v            v
+-------------+  +----------------+
| Permanent   |  | Changes erased |
| DB state    |  | from tx        |
+-------------+  +----------------+
```

The retention sentence:

> **Flush moves changes from Hibernate memory to the database transaction. Commit moves changes from transaction to permanent database state.**

---

## Core Concepts

## Flush

Flush means:

```text
Synchronize persistence context changes with the database transaction.
```

During flush, Hibernate:

```text
1. Checks managed entities for changes.
2. Builds SQL action queue.
3. Orders INSERT / UPDATE / DELETE statements.
4. Sends SQL through JDBC connection.
5. Keeps transaction open.
```

Flush can happen:

```text
- automatically before transaction commit
- automatically before some queries
- manually via entityManager.flush()
- earlier due to ID generation strategy
```

Flush does **not** mean:

```text
- transaction committed
- data permanently saved
- rollback impossible
- other transactions can always see the data
```

---

## Commit

Commit means:

```text
Make all changes in the current database transaction permanent.
```

During commit:

```text
1. Hibernate flushes pending changes if needed.
2. Database validates transaction completion.
3. Database makes changes durable.
4. Locks are released.
5. Other transactions can observe committed state depending on isolation.
```

Commit is the final gate.

```text
Before commit: changes are pending.
After commit: changes are permanent.
```

---

## Rollback

Rollback means:

```text
Undo all uncommitted changes in the transaction.
```

Even if SQL was already flushed, rollback can undo it.

```text
flush happened
UPDATE products SET stock = 8
exception thrown
rollback happened
product stock returns to previous committed value
```

This is the part many developers miss.

> **Flushed does not mean committed.**

---

## Flush Mode

Hibernate normally uses flush mode `AUTO` in JPA.

Meaning:

```text
Hibernate flushes automatically when needed to keep query results consistent.
```

Example:

```java
@Transactional
public long countAfterInsert() {
    Product p = new Product("Keyboard", 10);
    productRepository.save(p);

    return productRepository.count();
}
```

Hibernate may flush the insert before the count query so the count query sees the new product inside the same transaction.

```text
save new product
        |
        v
count query requested
        |
        v
Hibernate flushes INSERT first
        |
        v
SELECT COUNT(*) runs
```

---

## Internal Architecture

```text
Spring @Transactional Proxy
        |
        v
+----------------------------+
| Service Method             |
| business changes entities  |
+----------------------------+
        |
        v
+----------------------------+
| Persistence Context        |
| identity map               |
| snapshots                  |
| dirty entities             |
+----------------------------+
        |
        | flush
        v
+----------------------------+
| Hibernate Action Queue     |
| INSERT / UPDATE / DELETE   |
+----------------------------+
        |
        v
+----------------------------+
| JDBC Connection            |
| sends SQL                  |
+----------------------------+
        |
        v
+----------------------------+
| Database Transaction       |
| uncommitted changes        |
+----------------------------+
        |
        | commit / rollback
        v
+----------------------------+
| Permanent DB State         |
+----------------------------+
```

Flush belongs to Hibernate synchronization.
Commit belongs to the database transaction.

---

## Java Code Example

```java
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final EntityManager entityManager;

    public ProductService(ProductRepository productRepository,
                          EntityManager entityManager) {
        this.productRepository = productRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public void reserveThenFail(Long id) {
        Product product = productRepository.findById(id).orElseThrow();

        product.decreaseStock(2);

        entityManager.flush();

        throw new RuntimeException("fail after flush");
    }
}
```

What many beginners think:

```text
flush happened, so stock is permanently changed.
```

What actually happens:

```text
1. Product loaded as managed.
2. Stock changed from 10 to 8.
3. flush sends UPDATE to database transaction.
4. Exception is thrown.
5. Spring marks transaction rollback-only.
6. Database rolls back transaction.
7. Stock remains 10 in committed database state.
```

SQL may have been sent.
But it did not survive rollback.

---

## Spring Boot Example

### Entity

```java
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int stock;

    protected Product() {}

    public Product(String name, int stock) {
        this.name = name;
        this.stock = stock;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (stock < quantity) {
            throw new IllegalStateException("not enough stock");
        }
        this.stock -= quantity;
    }

    public int getStock() {
        return stock;
    }
}
```

### Repository

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

### Service

```java
@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final EntityManager entityManager;

    public InventoryService(ProductRepository productRepository,
                            EntityManager entityManager) {
        this.productRepository = productRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public void reserveWithManualFlush(Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow();

        product.decreaseStock(quantity);

        entityManager.flush();

        // If something fails after this line,
        // rollback still undoes the flushed UPDATE.
    }
}
```

### Internal Execution Explanation

```text
1. @Transactional opens database transaction.
2. Repository loads product.
3. Product becomes managed in persistence context.
4. product.decreaseStock mutates Java object.
5. entityManager.flush triggers dirty checking.
6. Hibernate sends UPDATE to database.
7. Transaction is still open.
8. If method completes normally, commit happens.
9. If runtime exception happens, rollback happens.
```

Key point:

```text
flush decides WHEN SQL is sent.
commit decides WHETHER SQL survives.
```

---

## Rich ASCII Diagram — Flush Is Not Commit

```text
@Transactional method
        |
        v
+----------------------------+
| product.stock = 10         |
| product.decreaseStock(2)   |
| product.stock = 8          |
+----------------------------+
        |
        | dirty entity in persistence context
        v
+----------------------------+
| entityManager.flush()      |
+----------------------------+
        |
        | SQL sent
        v
+----------------------------+
| DB transaction             |
| UPDATE stock = 8           |
| still uncommitted          |
+----------------------------+
        |
        +-----------------------------+
        |                             |
        | method succeeds             | exception thrown
        v                             v
+----------------------------+   +----------------------------+
| COMMIT                     |   | ROLLBACK                   |
| stock = 8 permanent        |   | stock returns to 10        |
+----------------------------+   +----------------------------+
```

---

## Internal Working — Normal Commit Flow

```java
@Transactional
public void reserve(Long id) {
    Product p = productRepository.findById(id).orElseThrow();
    p.decreaseStock(2);
}
```

Execution:

```text
1. Spring transactional proxy starts transaction.
2. EntityManager is bound to transaction.
3. Product is loaded and managed.
4. Snapshot stored: stock=10.
5. Business method changes stock to 8.
6. Method returns normally.
7. Transaction interceptor prepares to commit.
8. Hibernate flushes before commit.
9. Dirty checking detects stock 10 -> 8.
10. UPDATE SQL is sent.
11. Database commits transaction.
12. Persistence context closes.
```

SQL timing:

```text
SELECT product happens during findById.
UPDATE product may happen at commit-time flush.
COMMIT happens after flush succeeds.
```

---

## Internal Working — Flush Before Query

```java
@Transactional
public List<Product> updateThenSearch(Long id) {
    Product p = productRepository.findById(id).orElseThrow();
    p.decreaseStock(2);

    return productRepository.findByStockLessThan(5);
}
```

Why might Hibernate flush before the query?

```text
Because the query result could depend on the changed stock.
Hibernate wants the database query to see changes already made inside the same transaction.
```

Flow:

```text
1. Product stock changed in memory.
2. New query is about stock.
3. Hibernate flushes pending UPDATE first.
4. Query runs against database transaction.
5. Query sees updated stock inside same transaction.
6. Commit still happens later.
```

Important:

```text
A SELECT query can trigger flush.
Flush is not only at commit.
```

---

## Step-by-Step Dry Run — Flush Then Commit

Database before:

```text
products
+------+----------+-------+
| id   | name     | stock |
+------+----------+-------+
| 1001 | Keyboard | 10    |
+------+----------+-------+
```

Service:

```java
@Transactional
public void reserve(Long id) {
    Product p = productRepository.findById(id).orElseThrow();
    p.decreaseStock(2);
}
```

Dry run:

```text
1. Transaction starts.
2. Persistence context is empty.
3. SELECT product id=1001.
4. Product becomes managed.
5. Snapshot: stock=10.
6. Current object changed: stock=8.
7. Method returns normally.
8. Hibernate flushes.
9. UPDATE products SET stock=8 WHERE id=1001.
10. Database transaction commits.
11. Stock=8 becomes permanent.
```

After commit:

```text
products
+------+----------+-------+
| id   | name     | stock |
+------+----------+-------+
| 1001 | Keyboard | 8     |
+------+----------+-------+
```

---

## Step-by-Step Dry Run — Flush Then Rollback

Service:

```java
@Transactional
public void reserveAndFail(Long id) {
    Product p = productRepository.findById(id).orElseThrow();
    p.decreaseStock(2);
    entityManager.flush();
    throw new RuntimeException("boom");
}
```

Dry run:

```text
1. Transaction starts.
2. Product loaded.
3. Snapshot: stock=10.
4. Current object: stock=8.
5. Manual flush happens.
6. UPDATE products SET stock=8 WHERE id=1001 is sent.
7. Transaction is still open.
8. RuntimeException thrown.
9. Spring rolls back transaction.
10. Database discards UPDATE.
11. Committed stock remains 10.
```

Retention point:

> **Flush can execute SQL that later disappears.**

---

## Step-by-Step Dry Run — Constraint Violation at Flush

Suppose table has unique constraint:

```sql
ALTER TABLE users ADD CONSTRAINT uk_email UNIQUE (email);
```

Service:

```java
@Transactional
public void createDuplicateUsers() {
    userRepository.save(new User("a@example.com"));
    userRepository.save(new User("a@example.com"));

    entityManager.flush();
}
```

Flow:

```text
1. Two new User entities become managed.
2. INSERT actions are queued.
3. flush sends first INSERT.
4. flush sends second INSERT.
5. Database detects unique constraint violation.
6. Exception happens during flush, before commit.
7. Transaction is marked rollback-only.
8. Nothing is committed.
```

Lesson:

```text
Some database errors appear at flush time, not at save() line.
```

---

## Production Scale Example

Imagine an order service at 5,000 RPS.

```text
OrderService.createOrder()
  - load product
  - decrease stock
  - save order
  - publish outbox event
  - commit transaction
```

Expected flow:

```text
Hibernate memory changes
        |
        v
flush SQL:
  UPDATE product
  INSERT order
  INSERT outbox_event
        |
        v
commit transaction
        |
        v
Kafka relay later publishes event
```

Production risks:

```text
1. Long flush
   Many dirty entities create many SQL statements.

2. Constraint violation at flush
   Duplicate idempotency key fails before commit.

3. Lock wait during flush
   UPDATE product waits because another transaction holds lock.

4. Slow commit
   Database fsync/replication/commit latency is high.

5. External call before commit
   Payment provider called before transaction success.
```

Senior debugging separates two timings:

```text
Flush time high?
  Hibernate/SQL/action queue/locks/query count problem.

Commit time high?
  database commit, WAL, replication, disk, transaction finalization problem.
```

---

## Production Failure Story

A team built a payment service.

```java
@Transactional
public void pay(Order order) {
    paymentRepository.save(new Payment(order.getId(), amount));
    entityManager.flush();

    paymentGateway.charge(order.getCard(), amount);

    order.markPaid();
}
```

The developer believed:

```text
I flushed Payment, so it is safely saved before calling gateway.
```

Production incident:

```text
1. Payment row was flushed.
2. External gateway charged card.
3. order.markPaid() failed due to optimistic lock conflict.
4. Transaction rolled back.
5. Payment row disappeared from database.
6. Card was charged externally.
7. Internal database had no committed payment record.
```

Root cause:

```text
Developer confused flush with commit.
Flush sent SQL but did not make it permanent.
External side effect happened before transaction was committed.
```

Better design:

```text
1. Save payment attempt and outbox event in one transaction.
2. Commit transaction.
3. Outbox relay calls payment gateway or publishes event after commit.
4. Record gateway result in another transaction.
```

Lesson:

> **Never treat flush as a durability boundary for external side effects. Commit is the durability boundary.**

---

## Debugging Mindset

When a Hibernate write behaves strangely, ask:

```text
1. Did SQL get generated?
2. Did flush happen?
3. Did commit happen?
4. Did rollback happen after flush?
5. Was exception thrown during flush or commit?
6. Was an external side effect executed before commit?
```

### Symptom Map

```text
SQL appears in logs but DB has no change
  -> transaction probably rolled back after flush

Constraint violation happens before method ends
  -> flush was triggered before commit

SELECT query unexpectedly causes INSERT/UPDATE first
  -> AUTO flush before query

Manual flush did not persist after exception
  -> flush is not commit

Data visible inside same transaction but not outside
  -> uncommitted transaction isolation

External API succeeded but DB row missing
  -> external side effect happened before commit and transaction rolled back
```

---

## Failure Investigation Playbook

## Step 1 — Check Transaction Boundary

Ask:

```text
Is @Transactional active?
Is method public and called through Spring proxy?
Did runtime exception cause rollback?
Is transaction marked rollback-only?
```

## Step 2 — Check SQL Logs

Enable in lower environment:

```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```

Look for:

```text
Did UPDATE/INSERT happen?
Did it happen before query?
Did it happen at method end?
```

## Step 3 — Separate Flush Error From Commit Error

Flush errors:

```text
constraint violation
foreign key violation
not null violation
lock wait during UPDATE
SQL grammar issue
```

Commit errors:

```text
transaction finalization failure
deadlock detected late
connection failure during commit
replication/durability latency
```

## Step 4 — Check External Side Effects

Ask:

```text
Did we call Kafka, email, payment gateway, or another service before commit?
What happens if rollback occurs after that call?
```

Use after-commit hooks or outbox for serious production flows.

---

## Common Misconceptions

## Misconception 1: “flush commits the transaction”

No.

```text
flush sends SQL.
commit makes SQL permanent.
```

## Misconception 2: “If SQL is in logs, data must be saved”

No.

SQL logs only prove SQL was sent.
Rollback can still erase it.

## Misconception 3: “save immediately writes and commits”

No.

`save()` usually makes an entity managed or schedules work.
Flush sends SQL.
Commit finalizes.

## Misconception 4: “flush is useless because commit flushes anyway”

Manual flush is useful when:

```text
- you want constraint errors earlier
- you need generated database effects before continuing
- you are batching and want to control memory
```

But it should be used carefully.

## Misconception 5: “After flush, it is safe to call external systems”

Dangerous.

External systems cannot be rolled back by your database transaction.
Commit first, or use outbox/saga patterns.

## Misconception 6: “Rollback only undoes unflushed changes”

Wrong.

Rollback undoes flushed but uncommitted database changes too.

---

## Performance Considerations

Flush cost depends on:

```text
number of managed dirty entities
number of inserts/updates/deletes
SQL batching settings
foreign key ordering
constraint checks
database locks
indexes updated by writes
```

Bad pattern:

```java
@Transactional
public void importProducts(List<Product> products) {
    for (Product product : products) {
        productRepository.save(product);
    }
}
```

If list has 500,000 products:

```text
Persistence context grows huge.
Flush at commit becomes massive.
Memory pressure increases.
Commit appears slow.
```

Better batch pattern:

```java
@Transactional
public void importProducts(List<Product> products) {
    for (int i = 0; i < products.size(); i++) {
        entityManager.persist(products.get(i));

        if (i % 50 == 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }
}
```

Mental model:

```text
flush sends batch SQL
clear detaches entities
transaction may still commit later
```

For very large imports, separate transactions per chunk may be safer.

---

## Scalability Considerations

At scale, distinguish:

```text
Hibernate flush pressure
  - dirty checking cost
  - action queue size
  - SQL count
  - batch size

Database transaction pressure
  - lock duration
  - WAL volume
  - commit latency
  - replication lag
  - deadlocks
```

Production rule:

> **Small, predictable flushes are easier to scale than giant surprise flushes at commit.**

For high-RPS services:

```text
Keep transactions short.
Avoid loading unnecessary entities.
Use projections for read paths.
Avoid external calls inside transactions.
Know expected SQL per endpoint.
Measure flush/commit latency separately where possible.
```

---

## Java/Spring Boot Code Examples With Internal Execution

### Example 1 — Auto Flush Before Commit

```java
@Transactional
public void updateName(Long id, String name) {
    Product p = productRepository.findById(id).orElseThrow();
    p.rename(name);
}
```

Internal execution:

```text
findById -> SELECT
rename -> dirty managed entity
method return -> flush UPDATE
commit -> permanent
```

### Example 2 — Manual Flush for Early Constraint Check

```java
@Transactional
public void createUser(String email) {
    userRepository.save(new User(email));
    entityManager.flush();

    // If duplicate email exists,
    // exception happens here instead of method end.
}
```

Internal execution:

```text
save -> new managed entity
flush -> INSERT sent
unique constraint checked
commit still later
```

### Example 3 — Flush Before Query

```java
@Transactional
public long createAndCount() {
    productRepository.save(new Product("Mouse", 5));
    return productRepository.count();
}
```

Internal execution:

```text
save new product
count query requested
Hibernate flushes INSERT first
SELECT COUNT runs
commit later
```

### Example 4 — Flush Does Not Protect External Side Effects

```java
@Transactional
public void badPayment(Long orderId) {
    paymentRepository.save(new Payment(orderId));
    entityManager.flush();

    paymentGateway.charge(orderId);

    throw new RuntimeException("rollback after external charge");
}
```

Result:

```text
Payment INSERT flushed.
Gateway charged.
Exception triggers rollback.
Payment row gone.
External charge remains.
```

Correct direction:

```text
Use transactional outbox or after-commit processing.
```

---

## Interview Q&A

### Q1. What is the difference between flush and commit?

Strong answer:

> Flush synchronizes the persistence context with the database by sending SQL statements inside the current transaction. Commit makes the database transaction permanent. Flushed changes can still be rolled back if the transaction fails before commit.

### Q2. Does `entityManager.flush()` commit the transaction?

Strong answer:

> No. `flush()` only sends SQL to the database within the current transaction. The transaction remains open, and rollback can still undo those changes.

### Q3. When does Hibernate flush automatically?

Strong answer:

> Hibernate commonly flushes before transaction commit and may flush before queries when using automatic flush mode, so query results remain consistent with changes already made in the persistence context.

### Q4. Why can a SELECT trigger UPDATE SQL first?

Strong answer:

> Because Hibernate may flush pending changes before executing a query if the query could be affected by those changes. This ensures the query sees the transaction’s current state.

### Q5. Why did I see SQL logs but no data in the database?

Strong answer:

> SQL logs show that flush sent SQL. If the transaction rolled back after that, the database discarded the uncommitted changes. SQL execution is not the same as commit.

### Q6. Is manual flush bad?

Strong answer:

> Manual flush is not bad, but it should be intentional. It can detect constraint violations earlier or control batch memory. But it must not be treated as a durability boundary.

### Q7. What is the production danger of confusing flush and commit?

Strong answer:

> The biggest danger is performing external side effects after flush but before commit. If the transaction later rolls back, the database changes disappear but the external side effect remains, causing inconsistency.

---

## Production Checklist

```text
Flush vs Commit Review
======================

Transaction Boundary
[ ] Is @Transactional on the service use case?
[ ] Is the method called through Spring proxy?
[ ] Are rollback rules understood?

Flush Behavior
[ ] Do we know when SQL is flushed?
[ ] Can any query trigger early flush?
[ ] Are constraint violations expected at flush?
[ ] Is manual flush used only intentionally?

Commit Behavior
[ ] Do we know when transaction commits?
[ ] Could rollback happen after flush?
[ ] Are external side effects delayed until after commit?

Performance
[ ] Is persistence context size bounded?
[ ] Are batch jobs using flush/clear?
[ ] Is flush generating predictable SQL count?
[ ] Are locks held for too long?

Production Safety
[ ] No payment/email/Kafka call depends only on flush.
[ ] Outbox or after-commit hook used for side effects.
[ ] SQL logs checked in lower environment.
[ ] p99 latency separates SQL execution and commit delay.
```

---

## One-Page Cheat Sheet

```text
Flush vs Commit
===============

Flush
-----
Meaning:
  Send SQL from Hibernate persistence context to database transaction.

Does:
  Dirty checking
  Generate INSERT / UPDATE / DELETE
  Execute SQL through JDBC
  Detect DB constraints early

Does NOT:
  Commit transaction
  Make data permanent
  Prevent rollback
  Guarantee external consistency

Commit
------
Meaning:
  Make current database transaction permanent.

Does:
  Flush pending changes if needed
  Finalize transaction
  Release locks
  Make changes durable/visible as committed

Rollback
--------
Meaning:
  Undo all uncommitted work.

Important:
  Rollback can undo already flushed SQL.

Best Sentence
-------------
Flush sends SQL.
Commit saves transaction.
Rollback erases uncommitted SQL.

Debugging Rules
---------------
SQL in logs but no data?       Rollback after flush.
Constraint error before end?   Flush happened early.
SELECT caused UPDATE?          Auto flush before query.
External call inconsistent?    Side effect before commit.
Slow method end?               Flush or commit latency.
```

---

## Last-Minute Interview Revision

Do not say:

```text
flush saves data to the database.
```

Say:

```text
flush synchronizes Hibernate changes with the database transaction, but commit is what makes those changes permanent.
```

Senior version:

```text
I treat flush as a SQL synchronization boundary and commit as a durability boundary. This distinction is critical when debugging constraint violations, rollback behavior, unexpected SQL before queries, and external side effects.
```

---

## One Picture To Remember

```text
             Managed Entity Changes
                      |
                      v
          +------------------------+
          | Persistence Context    |
          | Product stock 10 -> 8  |
          +------------------------+
                      |
                      | FLUSH
                      v
          +------------------------+
          | DB Transaction         |
          | UPDATE sent            |
          | Not permanent yet      |
          +------------------------+
                 /              \
                /                \
           COMMIT              ROLLBACK
              |                   |
              v                   v
   +----------------------+   +----------------------+
   | Stock 8 permanent    |   | Stock remains 10     |
   +----------------------+   +----------------------+
```

Final retention sentence:

> **Flush is Hibernate saying “database, here is my pending SQL”; commit is the database saying “this transaction is now permanent.”**
