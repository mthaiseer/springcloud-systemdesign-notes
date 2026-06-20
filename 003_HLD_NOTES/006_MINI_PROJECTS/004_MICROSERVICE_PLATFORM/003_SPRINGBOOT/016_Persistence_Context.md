# 016_Persistence_Context — The Transaction Workbench Mental Model

## Core Mental Model

Do not imagine the persistence context as “some Hibernate cache”.
That is too weak and easy to forget.

The better mental model is:

> **The persistence context is Hibernate’s transaction workbench: every managed entity is placed on this workbench, tracked, compared, and synchronized to the database at flush time.**

```text
@Transactional method
        |
        v
+--------------------------------------------------+
|              PERSISTENCE CONTEXT                 |
|--------------------------------------------------|
| Identity Map: Product#1001 -> Product@abc        |
| Snapshot:     stock=10, name="Keyboard"          |
| Current Obj:  stock=8,  name="Keyboard"          |
| Action Queue: UPDATE products SET stock=8        |
+--------------------------------------------------+
        |
        v
Flush / Commit
        |
        v
Database rows updated
```

This chapter teaches exactly one idea:

> **Inside a transaction, Hibernate does not immediately think in SQL. It first thinks in managed objects inside the persistence context.**

If you remember only one sentence:

> **The persistence context is the memory workbench where Hibernate guarantees one Java object per database row and tracks changes until flush.**

---

## Why This Exists

Java code wants to work like this:

```java
Product product = productRepository.findById(1001L).orElseThrow();
product.decreaseStock(2);
```

Database code needs this:

```sql
SELECT id, name, stock FROM products WHERE id = 1001;
UPDATE products SET stock = 8 WHERE id = 1001;
```

The missing bridge is:

```text
Who remembers that Product#1001 was loaded?
Who ensures the same row is represented by the same Java object?
Who remembers the original stock was 10?
Who notices stock became 8?
Who decides UPDATE is needed?
```

That bridge is the persistence context.

Without it, Hibernate would be forced to behave like a simple mapper:

```text
Every find -> new Java object
Every change -> manual save required
Every save -> blindly update database
No identity guarantee
No dirty checking
No unit-of-work behavior
```

The persistence context exists so Hibernate can manage a full unit of work safely.

---

## Problem Statement

Suppose this method runs:

```java
@Transactional
public void reserveProduct(Long productId, int quantity) {
    Product p1 = productRepository.findById(productId).orElseThrow();
    Product p2 = productRepository.findById(productId).orElseThrow();

    p1.decreaseStock(quantity);
}
```

Question:

```text
Are p1 and p2 two different Java objects?
Does the second find hit the database again?
How does Hibernate know p1 changed?
Why does UPDATE happen even without save()?
```

The answer is the persistence context.

Inside one persistence context:

```java
p1 == p2   // true
```

Same database row.
Same managed Java object.
One tracked snapshot.
One final SQL synchronization.

---

## Real World Analogy

Imagine a car repair garage.

```text
Customer cars arrive
Mechanic puts each car on a numbered workbench
Mechanic records original condition
Mechanic modifies the car
Before delivery, mechanic checks what changed
Garage updates final invoice and records
```

Mapping:

```text
Garage workbench              Persistence Context
Car                           Entity object
License plate                 Entity ID
Original inspection sheet     Snapshot
Mechanic modification         Business method changes
Final invoice                 Flush SQL
Garage records                Database
```

Important rule:

> If the car is on the workbench, the garage tracks it. If the car already left the garage, changes outside are not tracked.

Hibernate version:

```text
Managed entity   -> on the workbench, tracked
Detached entity  -> outside the workbench, not tracked
Transient entity -> never entered the workbench
Removed entity   -> marked for deletion on the workbench
```

---

## The One Mental Model

Think of the persistence context as a map plus memory snapshots.

```text
Persistence Context
====================================================

Identity Map
------------
Product#1001  -> Product@abc
Order#5001    -> Order@xyz
User#42       -> User@def

Snapshots
---------
Product#1001  -> { name="Keyboard", stock=10 }
Order#5001    -> { status="CREATED" }
User#42       -> { email="old@mail.com" }

Current Objects
---------------
Product@abc   -> { name="Keyboard", stock=8 }
Order@xyz     -> { status="PAID" }
User@def      -> { email="new@mail.com" }

Dirty Checking
--------------
Product#1001 changed stock 10 -> 8
Order#5001 changed status CREATED -> PAID
User#42 changed email old@mail.com -> new@mail.com

Flush SQL
---------
UPDATE products SET stock=8 WHERE id=1001;
UPDATE orders SET status='PAID' WHERE id=5001;
UPDATE users SET email='new@mail.com' WHERE id=42;
```

The persistence context gives Hibernate four superpowers:

```text
1. Identity guarantee
   Same row -> same Java object inside one context.

2. First-level cache
   Repeated find by ID can return from memory.

3. Dirty checking
   Changed managed objects become SQL updates.

4. Unit of work
   Multiple object changes flush together as one transaction.
```

---

## Core Concepts

### 1. Managed Entity

A managed entity is an entity currently tracked by the persistence context.

```java
@Transactional
public void changeName(Long id) {
    Product product = productRepository.findById(id).orElseThrow();
    product.rename("Mechanical Keyboard");
}
```

`product` is managed because it was loaded inside the active transaction.
Hibernate tracks it.

### 2. Identity Map

The persistence context stores entities by entity type and primary key.

```text
EntityKey(Product, 1001) -> Product@abc
```

So this code:

```java
Product a = entityManager.find(Product.class, 1001L);
Product b = entityManager.find(Product.class, 1001L);
```

returns the same Java object reference:

```text
a == b  true
```

This prevents two in-memory copies of the same row from conflicting inside one transaction.

### 3. First-Level Cache

The persistence context is Hibernate’s first-level cache.
It is always enabled.
It is scoped to the persistence context, usually one transaction in Spring.

```text
First find:
  Persistence context empty -> SELECT database -> store Product#1001

Second find:
  Product#1001 already present -> return Product@abc -> no SELECT needed
```

This is not Redis.
This is not a shared application cache.
This is local memory for one persistence context.

### 4. Snapshot

When Hibernate loads a managed entity, it stores its original database state.

```text
Loaded from DB:
Product{id=1001, name="Keyboard", stock=10}

Snapshot stored:
name="Keyboard", stock=10
```

Later:

```java
product.decreaseStock(2);
```

Current object:

```text
name="Keyboard", stock=8
```

At flush:

```text
snapshot stock=10
current  stock=8
=> dirty
=> UPDATE needed
```

### 5. Flush

Flush means:

> **Synchronize persistence context changes into SQL statements.**

Flush can happen:

```text
- before transaction commit
- before executing some queries
- when entityManager.flush() is called manually
```

Flush is not the same as commit.

```text
flush  -> SQL sent to database
commit -> transaction becomes permanent
rollback -> flushed SQL is undone
```

---

## Internal Architecture

```text
Spring @Transactional Service
        |
        v
+----------------------------+
| TransactionInterceptor     |
| opens transaction          |
+----------------------------+
        |
        v
+----------------------------+
| EntityManager              |
| JPA API                    |
+----------------------------+
        |
        v
+--------------------------------------------------+
| Persistence Context                               |
|--------------------------------------------------|
| entitiesByKey                                    |
| entitySnapshots                                  |
| collectionSnapshots                              |
| actionQueue                                      |
| flushMode                                        |
+--------------------------------------------------+
        |
        v
+----------------------------+
| Hibernate Engine           |
| dirty checking + SQL       |
+----------------------------+
        |
        v
+----------------------------+
| JDBC Connection            |
+----------------------------+
        |
        v
Database
```

The repository is only the doorway.
The persistence context is the room where entity state is managed.

---

## Java Code Example

### Entity

```java
@Entity
@Table(name = "products")
public class Product {

    @Id
    private Long id;

    private String name;
    private int stock;

    protected Product() {
    }

    public Product(Long id, String name, int stock) {
        this.id = id;
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

    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        this.name = newName;
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
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void reserve(Long id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));

        product.decreaseStock(quantity);
    }
}
```

### What You See

```text
load product
change Java object
method returns
```

### What Hibernate Sees

```text
1. Open transaction.
2. Create/bind persistence context.
3. findById checks identity map.
4. Product#1001 not found.
5. SELECT product row.
6. Create Product@abc.
7. Store Product#1001 -> Product@abc.
8. Store snapshot stock=10.
9. Business method changes stock to 8.
10. Commit requested.
11. Flush starts.
12. Dirty check compares snapshot with current object.
13. UPDATE generated.
14. Transaction commits.
15. Persistence context closes.
```

---

## Rich ASCII Diagram — Identity Map

```text
Inside one @Transactional method

Code:
Product p1 = repo.findById(1001L).orElseThrow();
Product p2 = repo.findById(1001L).orElseThrow();

Persistence Context:
+------------------------------------------------+
| Entity Key              | Java Object          |
|------------------------------------------------|
| Product#1001            | Product@7fa          |
+------------------------------------------------+

Result:
p1 ----+
       |
       v
    Product@7fa
       ^
       |
p2 ----+

p1 == p2  true
```

This is why the persistence context is called an identity map.

---

## Rich ASCII Diagram — Dirty Checking

```text
Step 1: Load from database

Database Row:
+------+----------+-------+
| id   | name     | stock |
+------+----------+-------+
| 1001 | Keyboard | 10    |
+------+----------+-------+

Persistence Context:
+----------------+------------------+------------------+
| Entity Key     | Object           | Snapshot          |
+----------------+------------------+------------------+
| Product#1001   | Product@abc      | stock=10          |
+----------------+------------------+------------------+

Step 2: Business change

Product@abc.stock = 8

Step 3: Flush

Compare:
Snapshot stock=10
Current  stock=8

Result:
UPDATE products SET stock = 8 WHERE id = 1001;
```

---

## Step-by-Step Dry Run — Normal Flow

Database before:

```text
products
+------+----------+-------+
| id   | name     | stock |
+------+----------+-------+
| 1001 | Keyboard | 10    |
+------+----------+-------+
```

Code:

```java
@Transactional
public void reserve(Long id, int quantity) {
    Product product = productRepository.findById(id).orElseThrow();
    product.decreaseStock(quantity);
}
```

Flow:

```text
1. Controller calls productService.reserve(1001, 2).
2. Spring transactional proxy opens transaction.
3. EntityManager is bound to current transaction.
4. Persistence context starts empty.
5. Repository calls EntityManager.find(Product.class, 1001).
6. Persistence context checks identity map.
7. Product#1001 not found.
8. Hibernate sends SELECT.
9. Row is converted into Product@abc.
10. Product@abc is stored in identity map.
11. Snapshot stock=10 is stored.
12. Service calls product.decreaseStock(2).
13. Product@abc stock becomes 8.
14. Method returns.
15. Transaction commit triggers flush.
16. Dirty checking sees stock changed.
17. Hibernate sends UPDATE.
18. Database commits.
19. Persistence context closes.
```

Database after:

```text
products
+------+----------+-------+
| id   | name     | stock |
+------+----------+-------+
| 1001 | Keyboard | 8     |
+------+----------+-------+
```

Retention point:

> The persistence context is why the Java field change becomes SQL.

---

## Step-by-Step Dry Run — Repeated Find

Code:

```java
@Transactional
public void repeatedFind(Long id) {
    Product a = productRepository.findById(id).orElseThrow();
    Product b = productRepository.findById(id).orElseThrow();
    Product c = productRepository.findById(id).orElseThrow();
}
```

Expected behavior:

```text
First find:
  identity map miss
  SELECT from database
  store Product#1001 -> Product@abc

Second find:
  identity map hit
  return Product@abc
  no SELECT needed

Third find:
  identity map hit
  return Product@abc
  no SELECT needed
```

Diagram:

```text
find #1 -> DB SELECT -> Product@abc stored
find #2 -> memory hit -> same Product@abc
find #3 -> memory hit -> same Product@abc
```

Important limitation:

```text
This works by primary key lookup inside the same persistence context.
JPQL queries can still hit the database, but returned entities are resolved through the persistence context.
```

---

## Step-by-Step Dry Run — JPQL Query Meets Identity Map

Code:

```java
@Transactional
public void queryExample(Long id) {
    Product a = entityManager.find(Product.class, id);

    Product b = entityManager.createQuery(
            "select p from Product p where p.id = :id",
            Product.class)
        .setParameter("id", id)
        .getSingleResult();

    System.out.println(a == b);
}
```

What happens:

```text
1. entityManager.find loads Product#1001 as Product@abc.
2. JPQL query may still execute SQL.
3. Result row has id=1001.
4. Hibernate checks persistence context.
5. Product#1001 already exists.
6. Hibernate returns Product@abc, not a new object.
7. a == b is true.
```

Mental model:

> Queries may go to the database, but entity identity still goes through the persistence context.

---

## Spring Boot Example With Internal Execution

### Controller

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/{id}/rename")
    public ResponseEntity<Void> rename(@PathVariable Long id,
                                       @RequestParam String name) {
        productService.renameProduct(id, name);
        return ResponseEntity.noContent().build();
    }
}
```

### Service

```java
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void renameProduct(Long id, String name) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));

        product.rename(name);
    }
}
```

### Internal Execution Explanation

```text
HTTP request enters controller.
Controller calls service.
Service call goes through Spring proxy.
Proxy opens transaction.
Hibernate creates/binds persistence context.
Repository loads Product.
Product becomes managed.
Service changes managed object.
Service method returns.
Proxy commits transaction.
Hibernate flushes persistence context.
Dirty checking generates UPDATE.
Database commits.
HTTP 204 returns.
```

The controller has no Hibernate logic.
The service owns the transaction.
The repository loads the entity.
The persistence context tracks it.

---

## Production Scale Example

Imagine an order service at 5,000 requests per second.

```text
API Gateway
   |
   v
Order Service Pod
   |
   +-- @Transactional createOrder()
   |       |
   |       +-- Persistence Context per transaction
   |       +-- load Product
   |       +-- load Customer
   |       +-- create Order
   |       +-- dirty check Product stock
   |       +-- flush INSERT + UPDATE
   |
   v
Postgres
```

At this scale, persistence context behavior directly affects performance.

Common issues:

```text
1. Too many managed entities
   Large list endpoint loads 50,000 entities into one persistence context.

2. Dirty checking cost
   Hibernate must inspect many managed entities during flush.

3. Unexpected updates
   Code modifies managed entity accidentally during read flow.

4. Long transaction
   Persistence context holds entities and DB connection too long.

5. Memory pressure
   Batch job keeps adding entities without clear().
```

Better production habit:

```text
For write use cases:
  keep transaction short
  load only entities needed
  modify intentionally
  flush at commit

For read use cases:
  prefer DTO projections when no entity modification is needed
  avoid loading large graphs as managed entities

For batch jobs:
  flush and clear periodically
```

---

## Production Failure Story

A team built a nightly product import job.

```java
@Transactional
public void importProducts(List<ProductCsvRow> rows) {
    for (ProductCsvRow row : rows) {
        Product product = new Product(row.id(), row.name(), row.stock());
        entityManager.persist(product);
    }
}
```

It worked for 1,000 rows.
It failed for 500,000 rows.

Symptoms:

```text
- JVM memory increased continuously
- GC pauses became huge
- transaction took many minutes
- database commit was slow
- pod eventually restarted
```

Root cause:

```text
Every persisted Product became managed.
The persistence context kept all 500,000 entities.
Hibernate stored snapshots/action state.
Flush at the end had too much work.
```

Fix:

```java
@Transactional
public void importProducts(List<ProductCsvRow> rows) {
    for (int i = 0; i < rows.size(); i++) {
        ProductCsvRow row = rows.get(i);
        entityManager.persist(new Product(row.id(), row.name(), row.stock()));

        if (i % 50 == 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }
}
```

Why it works:

```text
flush -> send pending SQL
clear -> detach managed entities and empty persistence context
```

Lesson:

> **The persistence context is powerful, but it is not free. If you put too many objects on the workbench, the workbench becomes the bottleneck.**

---

## Failure Scenarios

### Failure 1 — Change Not Saved

Code:

```java
public Product load(Long id) {
    return productRepository.findById(id).orElseThrow();
}

Product p = service.load(1001L);
p.rename("New Name");
```

Why change may not save:

```text
Product was managed only during service transaction.
After method returns, persistence context closes.
Product becomes detached.
Renaming detached object is just Java memory change.
Hibernate does not track it.
```

Fix:

```java
@Transactional
public void rename(Long id, String name) {
    Product p = productRepository.findById(id).orElseThrow();
    p.rename(name);
}
```

### Failure 2 — Unexpected Update During Read

Code:

```java
@Transactional(readOnly = true)
public ProductResponse getProduct(Long id) {
    Product p = productRepository.findById(id).orElseThrow();
    p.normalizeName();
    return ProductResponse.from(p);
}
```

Danger:

```text
normalizeName mutates managed entity.
Depending on flush mode and provider behavior, this may become an unexpected update.
Read methods should not mutate managed entities.
```

Better:

```java
String normalized = normalizeForResponse(p.getName());
return new ProductResponse(p.getId(), normalized);
```

### Failure 3 — Memory Spike in Large Read

Code:

```java
@Transactional(readOnly = true)
public List<Product> exportAll() {
    return productRepository.findAll();
}
```

Problem:

```text
All returned entities become managed.
Persistence context stores them.
Large table -> large memory.
```

Better:

```text
Use pagination, streaming carefully, DTO projections, or batch clear.
```

### Failure 4 — Confusing save() With Flush

Code:

```java
@Transactional
public void create(Product product) {
    productRepository.save(product);
    // assume row is permanently committed here
}
```

Reality:

```text
save makes entity managed / schedules insert.
SQL may happen at flush.
Commit happens at transaction end.
If exception occurs later, insert rolls back.
```

---

## Debugging Mindset

When debugging persistence context issues, ask:

```text
1. Is the entity currently managed?
2. Which transaction owns this persistence context?
3. Was the entity loaded by ID or query?
4. How many entities are currently managed?
5. Did the code mutate a managed entity accidentally?
6. Did flush happen earlier than expected?
7. Did clear/detach remove tracking?
```

### Symptom Map

```text
Change not saved
  -> entity detached?
  -> no transaction?
  -> rollback happened?

Same entity loaded twice but different object
  -> different persistence contexts?
  -> detached object mixed with managed object?

Too much memory
  -> too many managed entities in context
  -> batch job missing flush/clear

Unexpected UPDATE
  -> managed entity mutated during read path
  -> dirty checking detected change

Slow commit
  -> huge persistence context
  -> many dirty checks
  -> large action queue

Query returns old value
  -> persistence context already has entity
  -> first-level cache returns managed state
```

### Useful Debug Tools

```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
spring.jpa.properties.hibernate.generate_statistics=true
```

For production, avoid verbose bind logging unless necessary because it can expose sensitive data and create overhead.

---

## Common Misconceptions

### Misconception 1: “Persistence context is the same as second-level cache”

No.

```text
Persistence context / first-level cache:
  per EntityManager / transaction
  always enabled
  stores managed entity instances

Second-level cache:
  optional
  shared across sessions
  provider-specific configuration
```

### Misconception 2: “findById always hits the database”

Not inside the same persistence context if the entity is already managed by ID.

```text
First find -> database
Second find -> persistence context hit
```

### Misconception 3: “save is what updates managed entities”

For already managed entities, dirty checking can update without explicit save.
The key is managed state plus flush.

### Misconception 4: “Flush means commit”

No.

```text
flush sends SQL
commit makes SQL permanent
rollback can undo flushed SQL
```

### Misconception 5: “Read-only transactions mean objects cannot change”

Not exactly.
Read-only is an optimization/hint depending on framework/provider behavior.
Your Java object can still be mutated.
Do not rely on read-only to protect bad design.

### Misconception 6: “Persistence context is always open during the HTTP request”

In good service-layer design, you should think transaction scope, not web request scope.
Open Session in View can keep the context open longer, but it can hide lazy-loading and query-shape problems.

---

## Performance Considerations

The persistence context improves performance by avoiding repeated primary-key loads in one transaction.
But it can hurt performance when it grows too large.

Good uses:

```text
- small transactional writes
- aggregate updates
- repeated access to same entity by ID
- domain behavior with dirty checking
```

Risky uses:

```text
- loading huge tables as entities
- batch inserts without flush/clear
- read endpoints returning large managed graphs
- long transactions that keep many entities tracked
```

### Rule of Thumb

```text
Use managed entities when you want to change business state.
Use DTO projections when you only want to read response data.
```

Example projection:

```java
public record ProductCard(Long id, String name, int stock) {}

@Query("select new com.example.ProductCard(p.id, p.name, p.stock) from Product p")
List<ProductCard> findCards();
```

This avoids placing full entities on the persistence context workbench.

---

## Scalability Considerations

At scale, the persistence context should be small, intentional, and short-lived.

```text
Small
  Load only what the use case needs.

Intentional
  Know which entities are managed and which are DTOs.

Short-lived
  Keep transaction boundaries tight.
```

Bad scalable design:

```text
HTTP request starts
load huge graph
call external API
modify entities
wait
flush enormous context
commit
```

Better scalable design:

```text
validate request
start transaction
load minimal rows
modify business state
flush/commit quickly
publish async event after commit
```

Operational metrics to watch:

```text
- transaction duration
- DB connection hold time
- Hibernate query count
- flush duration
- JVM heap usage
- GC pause time
- p95/p99 endpoint latency
```

---

## Production Checklist

```text
Persistence Context Checklist
=============================

Transaction Boundary
[ ] Is @Transactional on the service use case?
[ ] Is the method called through Spring proxy?
[ ] Is the transaction short?
[ ] Are external calls outside DB transaction?

Managed Entity Usage
[ ] Do we know which entities become managed?
[ ] Are read-only flows avoiding mutation?
[ ] Are detached objects handled carefully?
[ ] Is merge avoided unless needed?

Query Shape
[ ] Do list endpoints use projections when possible?
[ ] Are large relations avoided unless needed?
[ ] Are repeated findById calls understood?
[ ] Is N+1 checked separately?

Batch Processing
[ ] Does batch code flush periodically?
[ ] Does batch code clear periodically?
[ ] Is transaction size controlled?
[ ] Is memory usage tested with production-like volume?

Debugging
[ ] Can SQL be logged in lower env?
[ ] Can query count be measured?
[ ] Are flushes observable?
[ ] Are slow commits investigated?
```

---

## Interview Q&A

### Q1. What is the persistence context?

Strong answer:

> The persistence context is Hibernate’s first-level cache and identity map for managed entities. It stores entity instances, tracks their original snapshots, guarantees one Java object per database row inside the context, and uses dirty checking to flush SQL changes to the database.

### Q2. Why does `findById` sometimes not hit the database?

Strong answer:

> If the entity is already managed in the current persistence context, Hibernate returns it from the first-level cache. A repeated primary-key lookup in the same context can avoid another SELECT.

### Q3. What does “managed entity” mean?

Strong answer:

> A managed entity is currently associated with a persistence context. Hibernate tracks it, stores its snapshot, and can detect changes during flush.

### Q4. What is dirty checking?

Strong answer:

> Dirty checking is the process where Hibernate compares the current state of managed entities with their loaded snapshots. If state changed, Hibernate generates SQL updates during flush.

### Q5. What is the difference between flush and commit?

Strong answer:

> Flush sends SQL statements to the database to synchronize the persistence context. Commit makes the database transaction permanent. A flush can happen before commit, and if the transaction rolls back, flushed changes are undone.

### Q6. Why can large batch jobs fail with JPA?

Strong answer:

> Because every persisted or loaded entity becomes managed and remains in the persistence context until it is cleared or closed. A large batch can create huge memory pressure and slow dirty checking. The fix is usually batching with periodic flush and clear.

### Q7. Why is returning entities from APIs risky?

Strong answer:

> Returned entities may carry lazy relations, expose internal fields, and keep developers thinking in managed object graphs instead of API contracts. DTO projections often make read paths safer and more predictable.

---

## One-Page Cheat Sheet

```text
Persistence Context
===================

Best Mental Model
-----------------
Hibernate's transaction workbench.
Managed entities sit here until flush/commit.

Contains
--------
Identity Map:
  Entity ID -> Java object

Snapshots:
  Original loaded state

Current Objects:
  Mutable managed entities

Action Queue:
  Pending INSERT / UPDATE / DELETE

Main Guarantees
---------------
Same row inside same context -> same Java object.
Managed entity changes -> dirty checking.
Flush -> SQL synchronization.

First-Level Cache
-----------------
Scoped to one persistence context.
Always enabled.
Not shared across transactions.

Flush vs Commit
---------------
flush  = send SQL
commit = make permanent
rollback can undo flushed SQL

Debug Rules
-----------
Change not saved?     Managed or detached?
Repeated SELECT?      Same context or different one?
Memory spike?         Too many managed entities?
Unexpected UPDATE?    Mutated managed object?
Slow commit?          Big dirty checking/action queue?
```

---

## Last-Minute Interview Revision

Do not say:

```text
Persistence context is just a cache.
```

Say:

```text
The persistence context is the first-level cache, identity map, and dirty-checking workbench where Hibernate manages entities during a unit of work.
```

Senior version:

```text
I treat the persistence context as a short-lived unit-of-work memory area. For writes, I use managed entities intentionally. For large reads or reporting, I avoid filling it with unnecessary entities and use projections or controlled batching.
```

---

## One Picture To Remember

```text
                 ONE TRANSACTION / UNIT OF WORK

                        Service Method
                             |
                             v
        +------------------------------------------------+
        |             PERSISTENCE CONTEXT                |
        |------------------------------------------------|
        | Identity Map                                   |
        |   Product#1001  -> Product@abc                 |
        |                                                |
        | Snapshot                                       |
        |   Product#1001 stock=10                        |
        |                                                |
        | Current Managed Object                          |
        |   Product@abc stock=8                          |
        |                                                |
        | Dirty Check                                    |
        |   10 != 8                                      |
        +------------------------------------------------+
                             |
                             | flush
                             v
              UPDATE products SET stock=8 WHERE id=1001
                             |
                             | commit
                             v
                         DATABASE
```

Final retention sentence:

> **The persistence context is Hibernate’s short-lived memory workbench: one row becomes one managed object, changes are tracked by snapshots, and SQL is produced only when the workbench flushes.**
