# 017_Dirty_Checking — The Snapshot Comparison Model

## Core Mental Model

Do not imagine dirty checking as “Hibernate magically knows what changed”.
That model is too vague.

The better mental model is:

> **Dirty checking is Hibernate comparing a managed entity's original snapshot with its current in-memory state at flush time.**

```text
When entity is loaded:

Database row
    |
    v
+-------------------------------+
| Product entity                |
| id = 1001                     |
| name = Keyboard               |
| stock = 10                    |
+-------------------------------+

Hibernate stores two things:

+----------------------+       +----------------------+
| Managed Object       |       | Original Snapshot    |
| Product@abc          |       | id=1001              |
| stock=10             |       | stock=10             |
+----------------------+       +----------------------+
```

Later business code changes the object:

```java
product.decreaseStock(2);
```

Now Hibernate sees:

```text
Original snapshot: stock = 10
Current object:    stock = 8

Difference found -> entity is dirty -> UPDATE SQL required
```

If you remember only one sentence:

> **Dirty checking is not save magic; it is snapshot comparison for managed entities inside the persistence context.**

---

## Why This Exists

Java code naturally changes objects:

```java
product.decreaseStock(2);
order.markPaid();
customer.changeAddress(address);
```

A database cannot store “method calls”.
It only understands SQL:

```sql
UPDATE products SET stock = 8 WHERE id = 1001;
UPDATE orders SET status = 'PAID' WHERE id = 501;
UPDATE customers SET address = ? WHERE id = 42;
```

Without dirty checking, every change would require explicit SQL or explicit save calls everywhere.

Bad mental model:

```java
Product product = repository.findById(id).orElseThrow();
product.decreaseStock(2);
repository.save(product); // I think this is what updates it
```

Better mental model:

```text
findById() makes product managed
Hibernate stores snapshot
business method changes field
flush compares snapshot vs current state
Hibernate generates UPDATE
```

Dirty checking exists so your business code can modify domain objects naturally while Hibernate converts meaningful changes into SQL at synchronization time.

---

## Problem Statement

Suppose this service method runs:

```java
@Transactional
public void reserveStock(Long productId, int quantity) {
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("not found"));

    product.decreaseStock(quantity);
}
```

There is no `save()` call.

But the database still updates.

Why?

Because:

```text
1. Product was loaded inside a persistence context.
2. Product became managed.
3. Hibernate stored its original loaded values.
4. Business code changed the managed object.
5. At flush, Hibernate compared original values with current values.
6. Hibernate generated UPDATE SQL.
```

The problem dirty checking solves:

> **How can Hibernate know which managed objects changed without the developer manually writing update statements?**

Answer:

> **It remembers original snapshots and compares them against current entity state during flush.**

---

## Real World Analogy

Imagine a car rental inspection sheet.

When the customer receives the car, the company records its condition:

```text
Original inspection sheet:
Fuel = full
Scratch = none
Mileage = 10,000
```

When the car is returned, the company compares current state:

```text
Current condition:
Fuel = half
Scratch = left door
Mileage = 10,240
```

The company does not need someone to report every tiny event during the trip.
It compares before vs after.

Hibernate works similarly.

```text
Car rental analogy             Hibernate
-------------------             ---------
Original inspection sheet       Entity snapshot
Current car condition           Current entity object
Difference found                Dirty entity
Final billing/update            SQL UPDATE during flush
```

Important lesson:

> **Dirty checking is a before-and-after comparison, not continuous magic.**

---

## The One Mental Model

Think of the persistence context as a comparison table.

```text
+----------------+---------------------+---------------------+----------+
| Entity Key     | Object Reference    | Original Snapshot   | Dirty?   |
+----------------+---------------------+---------------------+----------+
| Product#1001   | Product@abc         | stock=10,name=Key   | ?        |
| Order#501      | Order@xyz           | status=CREATED      | ?        |
+----------------+---------------------+---------------------+----------+
```

At flush time:

```text
For every managed entity:
    compare original snapshot with current object state
    if different:
        mark dirty
        generate UPDATE
    else:
        do nothing
```

The heart of dirty checking:

```text
Loaded state  -> snapshot
Current state -> entity fields now
Comparison    -> dirty or clean
Flush         -> SQL synchronization
```

---

## Core Concepts

## Managed Entity

Dirty checking only works for managed entities.

```java
@Transactional
public void changeName(Long id) {
    Product product = productRepository.findById(id).orElseThrow();
    product.rename("Mechanical Keyboard");
}
```

Here `product` is managed because it was loaded inside the active persistence context.

```text
Managed entity = Hibernate is tracking it
Detached entity = normal Java object, no automatic tracking
```

## Snapshot

A snapshot is Hibernate's remembered original state.

```text
Database row loaded:
id=1001, name=Keyboard, stock=10

Hibernate snapshot:
id=1001, name=Keyboard, stock=10
```

The snapshot is not your entity object.
It is Hibernate's internal copy of the original values.

## Dirty Entity

An entity is dirty when current state differs from snapshot.

```text
Snapshot: stock=10
Current:  stock=8
Result:   dirty
```

Clean entity:

```text
Snapshot: name=Keyboard
Current:  name=Keyboard
Result:   clean
```

## Flush

Flush is when dirty checking matters.

```text
Before flush:
  Java object changed in memory

During flush:
  Hibernate compares and generates SQL

After commit:
  Database permanently stores the change
```

Flush can happen:

```text
- before transaction commit
- before certain queries
- when entityManager.flush() is called
```

## Update SQL

Dirty checking decides that SQL is needed.
Hibernate then generates update SQL.

Simple form:

```sql
UPDATE products SET stock = ? WHERE id = ?;
```

Depending on mapping and configuration, Hibernate may update multiple columns or only changed columns.
The retention idea is not exact SQL shape.
The retention idea is:

```text
changed managed state -> dirty -> flush -> SQL
```

---

## Java Code Example

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
            throw new IllegalArgumentException("name required");
        }
        this.name = newName;
    }
}
```

Repository:

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

Service:

```java
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void reserve(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));

        product.decreaseStock(quantity);
    }
}
```

Execution explanation:

```text
1. @Transactional opens transaction.
2. Repository loads Product#1001.
3. Product becomes managed.
4. Hibernate stores original snapshot: stock=10.
5. product.decreaseStock(2) changes stock to 8.
6. Method returns.
7. Transaction commit triggers flush.
8. Dirty checking compares snapshot stock=10 with current stock=8.
9. Hibernate generates UPDATE.
10. Database commits.
```

Important:

```text
No save call is required because the entity is managed.
```

---

## Rich ASCII Diagram — Dirty Checking Workbench

```text
                         @Transactional method
                                  |
                                  v
+------------------------------------------------------------------+
|                    Persistence Context                            |
|------------------------------------------------------------------|
|                                                                  |
| Entity Key: Product#1001                                          |
|                                                                  |
|  Original Snapshot                    Current Managed Object      |
|  +----------------------+             +----------------------+    |
|  | id    = 1001         |             | id    = 1001         |    |
|  | name  = Keyboard     |             | name  = Keyboard     |    |
|  | stock = 10           |             | stock = 8            |    |
|  +----------------------+             +----------------------+    |
|                                                                  |
|                       Comparison                                  |
|                       stock: 10 -> 8                              |
|                                                                  |
|                       Result: DIRTY                               |
+------------------------------------------------------------------+
                                  |
                                  v
                         Flush generates SQL
                                  |
                                  v
              UPDATE products SET stock = 8 WHERE id = 1001;
```

---

## Internal Working

Hibernate dirty checking is part of the flush process.

```text
@Transactional begins
        |
        v
Entity loaded
        |
        v
Snapshot stored
        |
        v
Business code mutates object
        |
        v
Flush starts
        |
        v
Hibernate checks managed entities
        |
        v
Compare snapshot vs current state
        |
        +-- same      -> no SQL
        |
        +-- different -> generate UPDATE action
        |
        v
Execute SQL
        |
        v
Commit transaction
```

Pseudo-code mental model:

```java
for (EntityEntry entry : persistenceContext.managedEntities()) {
    Object[] snapshot = entry.getLoadedState();
    Object[] current = extractCurrentState(entry.getEntity());

    if (!Arrays.equals(snapshot, current)) {
        actionQueue.addUpdate(entry.getEntity(), diff(snapshot, current));
    }
}

actionQueue.executeActions();
```

This is simplified, but it gives the correct mental model.

Dirty checking is not usually performed every time you call a setter.
The important comparison happens during flush.

---

## Step-by-Step Dry Run — Normal Update

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
public void reserve(Long id) {
    Product p = productRepository.findById(id).orElseThrow();
    p.decreaseStock(2);
}
```

Dry run:

```text
1. Transaction starts.
2. Persistence context is empty.
3. findById(1001) runs.
4. Hibernate sends SELECT.
5. Product row becomes Product@abc.
6. Hibernate stores snapshot: stock=10.
7. p.decreaseStock(2) changes object: stock=8.
8. Method returns.
9. Flush starts.
10. Hibernate compares:
      snapshot stock=10
      current  stock=8
11. Difference found.
12. UPDATE SQL is sent.
13. Transaction commits.
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

---

## Step-by-Step Dry Run — No Change

Code:

```java
@Transactional
public void viewProduct(Long id) {
    Product p = productRepository.findById(id).orElseThrow();
    p.rename(p.getName());
}
```

Dry run:

```text
1. Product loaded.
2. Snapshot name=Keyboard.
3. Code sets name to same value.
4. Flush compares snapshot vs current.
5. No meaningful difference.
6. No UPDATE is needed.
```

Retention:

> Loading an entity does not automatically mean update SQL will happen. Hibernate updates dirty entities, not every loaded entity.

---

## Step-by-Step Dry Run — Detached Object Change

Code:

```java
public Product load(Long id) {
    return productRepository.findById(id).orElseThrow();
}

public void caller() {
    Product p = load(1001L); // transaction already ended
    p.decreaseStock(2);
}
```

Dry run:

```text
1. Product is loaded in repository transaction.
2. Transaction ends.
3. Persistence context closes.
4. Product becomes detached.
5. Caller changes product field.
6. Hibernate is no longer tracking it.
7. No dirty checking.
8. No UPDATE SQL.
```

Retention:

> Dirty checking requires a managed entity inside an active persistence context.

---

## Spring Boot Example With Internal Flow

Controller:

```java
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<Void> reserve(@PathVariable Long id,
                                        @RequestParam int quantity) {
        productService.reserve(id, quantity);
        return ResponseEntity.noContent().build();
    }
}
```

Service:

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
                .orElseThrow(() -> new IllegalArgumentException("not found"));

        product.decreaseStock(quantity);
    }
}
```

Internal execution:

```text
HTTP request
   |
   v
Controller calls service
   |
   v
Spring transactional proxy opens transaction
   |
   v
Repository loads product
   |
   v
Persistence context stores object + snapshot
   |
   v
Service changes object field
   |
   v
Transaction commit triggers flush
   |
   v
Dirty checking detects changed field
   |
   v
Hibernate sends UPDATE
```

This connects to the previous JPA/Hibernate mental model: Hibernate is an object-row synchronization engine, and dirty checking is the comparison step inside that engine.

---

## Production Scale Example

Imagine an inventory service at 5,000 requests per second.

```text
POST /products/{id}/reserve
   |
   v
ProductService.reserve()
   |
   +-- load product
   +-- decrease stock
   +-- commit
   |
   v
Hibernate dirty checking
   |
   v
UPDATE products SET stock=? WHERE id=?
```

Dirty checking is useful because the service code stays clean.
But at scale, you must understand its cost and behavior.

Production concerns:

```text
1. Large persistence context
   More managed entities means more entities to inspect during flush.

2. Long transaction
   More time holding database connection and locks.

3. Unexpected updates
   Accidental setter call may mark entity dirty.

4. Massive batch update
   Loading 100k entities and changing each one creates huge memory pressure.

5. Lost update risk
   Two transactions may load same snapshot and overwrite each other unless locking/versioning is used.
```

Senior thinking:

```text
Dirty checking is excellent for normal transactional domain changes.
For bulk updates, reporting, and massive writes, deliberate SQL or batch design may be better.
```

---

## Production Failure Story

A team had this endpoint:

```java
@Transactional
public void updateCustomerProfile(Long id, UpdateProfileRequest request) {
    Customer customer = customerRepository.findById(id).orElseThrow();

    customer.setName(request.name());
    customer.setEmail(request.email());
    customer.setPhone(request.phone());
}
```

It looked fine.

Later, a new developer added this line:

```java
customer.setStatus(CustomerStatus.ACTIVE);
```

They thought it was harmless because the customer was already active.
But some old customers were intentionally marked `SUSPENDED`.

Production issue:

```text
Suspended customers became active after profile update.
```

Root cause:

```text
The entity was managed.
The setter changed a field.
Dirty checking detected it.
Flush generated UPDATE.
Business invariant was accidentally violated.
```

The bug was not that Hibernate was wrong.
Hibernate did exactly what it was designed to do.
The bug was treating managed entities as casual DTOs.

Fix:

```text
1. Avoid blind setters.
2. Use intention-revealing domain methods.
3. Update only fields allowed by the use case.
4. Add tests for forbidden state transitions.
5. Consider @DynamicUpdate only for SQL shape, not for business correctness.
```

Better entity method:

```java
public void updateProfile(String name, String email, String phone) {
    this.name = requireName(name);
    this.email = requireEmail(email);
    this.phone = phone;
}
```

Retention:

> Managed entities are live tracked objects. Changing them is not innocent.

---

## Debugging Mindset

When an update happens unexpectedly, ask:

```text
1. Was the entity managed?
2. Which code changed the field?
3. Did a mapper call setters blindly?
4. Did MapStruct or BeanUtils copy null/default values?
5. Did flush happen before a query?
6. Did transaction commit successfully?
7. Did rollback happen after flush?
```

When an update does not happen, ask:

```text
1. Was the entity detached?
2. Was there no @Transactional boundary?
3. Was the method called by self-invocation?
4. Was the entity read-only?
5. Did an exception roll back the transaction?
6. Was the changed field actually mapped?
```

Useful logging in lower environments:

```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
spring.jpa.properties.hibernate.generate_statistics=true
```

Investigation map:

```text
Symptom: update happened unexpectedly
    |
    +-- Check SQL logs
    +-- Find updated columns
    +-- Search code path for setters/domain methods
    +-- Check mappers copying values
    +-- Check flush timing

Symptom: update did not happen
    |
    +-- Check transaction boundary
    +-- Check entity state: managed or detached
    +-- Check rollback
    +-- Check whether field is mapped
```

---

## Common Misconceptions

### Misconception 1: “I must always call save after changing an entity”

Not for managed entities.

If the entity is managed inside a transaction, dirty checking can update it automatically.

```java
@Transactional
public void rename(Long id, String name) {
    Product p = repository.findById(id).orElseThrow();
    p.rename(name);
}
```

No explicit `save()` is required for the managed entity.

### Misconception 2: “Dirty checking works for every object”

No.

Dirty checking works for managed entities in the persistence context.

Detached object changes are invisible.

### Misconception 3: “Flush means commit”

No.

```text
flush  -> SQL sent to database
commit -> transaction becomes permanent
```

A flushed update can still be rolled back.

### Misconception 4: “Hibernate updates only the field I changed”

Not always.

Depending on mapping and configuration, Hibernate may update multiple columns.
The important point is that dirty checking decides whether an update is needed.

### Misconception 5: “Dirty checking is bad for performance”

Dirty checking is usually fine for normal request transactions.
It becomes a problem when the persistence context contains too many managed entities or transactions are too large.

### Misconception 6: “Setters are harmless”

Setters on managed entities can become database updates.
Use intention-revealing methods to protect business rules.

---

## Performance Considerations

Dirty checking has a cost:

```text
number of managed entities x number of mapped fields to inspect
```

For normal service methods, this is small.
For batch jobs, it can become large.

Bad batch pattern:

```java
@Transactional
public void importProducts(List<Product> products) {
    for (Product product : products) {
        entityManager.persist(product);
    }
}
```

If the list has 500,000 items, the persistence context grows huge.

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

Why:

```text
flush -> send SQL
clear -> detach entities and reduce persistence context memory
```

For bulk changes, consider JPQL update:

```java
@Modifying
@Query("update Product p set p.stock = p.stock - :qty where p.id = :id")
int decreaseStockDirectly(Long id, int qty);
```

Tradeoff:

```text
Dirty checking path:
  good for domain rules and entity invariants

Bulk update path:
  good for performance but bypasses entity lifecycle and in-memory state
```

---

## Scalability Considerations

At production scale, dirty checking interacts with:

```text
Transaction size
  bigger transaction -> more managed entities -> more checking

Fetch plan
  loading unnecessary entities increases persistence context size

Mapping style
  blind DTO-to-entity mapping can cause accidental updates

Locking
  dirty checking does not by itself prevent lost updates

Batch jobs
  persistence context must be controlled with flush/clear
```

For concurrent updates, use optimistic locking:

```java
@Version
private long version;
```

Mental model:

```text
Dirty checking detects local object changes.
Optimistic locking detects conflicting concurrent updates.
```

They solve different problems.

---

## Failure Investigation Playbook

### Case 1 — Entity changed but database did not update

Check:

```text
[ ] Was method @Transactional?
[ ] Was method called through Spring proxy?
[ ] Was entity managed?
[ ] Was transaction rolled back?
[ ] Was field mapped with @Column or relationship mapping?
[ ] Was entity marked read-only?
```

### Case 2 — Database updated unexpectedly

Check:

```text
[ ] Which columns were updated?
[ ] Which mapper touched the entity?
[ ] Did DTO copy null/default values?
[ ] Did business method mutate extra fields?
[ ] Did flush happen before query?
[ ] Is Open Session in View keeping entities managed longer than expected?
```

### Case 3 — Slow commit

Check:

```text
[ ] How many managed entities exist?
[ ] How many dirty entities exist?
[ ] Are collections dirty?
[ ] Is batching enabled?
[ ] Are there database lock waits?
```

### Case 4 — Lost update

Check:

```text
[ ] Are two transactions loading same row?
[ ] Is there @Version?
[ ] Is update based on stale snapshot?
[ ] Should SELECT FOR UPDATE or optimistic locking be used?
```

---

## Interview Q&A

### Q1. What is dirty checking in Hibernate?

Strong answer:

> Dirty checking is Hibernate's mechanism for detecting changes to managed entities. Hibernate stores an original snapshot when an entity is loaded, compares it with the current entity state during flush, and generates update SQL if differences are found.

### Q2. Does dirty checking work without `@Transactional`?

Strong answer:

> Dirty checking requires a managed entity inside an active persistence context. In Spring applications, that is normally provided by a transactional service method. Without a proper transaction boundary, the entity may become detached and changes may not be flushed as expected.

### Q3. Do we need to call `save()` after changing a managed entity?

Strong answer:

> Usually no. If the entity is managed inside a transaction, Hibernate tracks it and dirty checking flushes changes automatically. `save()` is needed for new entities or when dealing with detached state through repository semantics.

### Q4. When does dirty checking happen?

Strong answer:

> Dirty checking happens during flush. Flush usually happens before transaction commit, before some queries, or when manually triggered with `entityManager.flush()`.

### Q5. What is the difference between flush and commit?

Strong answer:

> Flush synchronizes persistence context changes to the database by executing SQL. Commit makes the transaction permanent. SQL sent during flush can still be rolled back if the transaction fails.

### Q6. Can dirty checking cause performance problems?

Strong answer:

> Yes, mainly when the persistence context contains too many managed entities, such as in large batch jobs or long transactions. Normal request-sized transactions are usually fine. For large batches, flush and clear periodically or use bulk updates carefully.

### Q7. How do you debug unexpected updates?

Strong answer:

> I enable SQL logging in a lower environment, check which columns were updated, identify whether the entity was managed, inspect DTO mappers and setters, and verify flush timing. Unexpected updates usually come from mutating a managed entity unintentionally.

---

## Production Checklist

```text
Dirty Checking Safety
[ ] Do service methods use clear @Transactional boundaries?
[ ] Are entity changes intentional domain methods, not blind setters?
[ ] Are DTO mappers prevented from overwriting forbidden fields?
[ ] Are detached entities handled explicitly?

Performance
[ ] Are transactions short?
[ ] Is persistence context size controlled?
[ ] Are batch jobs using flush/clear?
[ ] Are bulk updates used only when lifecycle rules are understood?

Correctness
[ ] Is @Version used for concurrent updates where needed?
[ ] Are rollback rules understood?
[ ] Are entity fields properly mapped?
[ ] Are unexpected updates visible in tests/logs?

Debugging
[ ] Can SQL be enabled in lower environment?
[ ] Can updated columns be traced to code?
[ ] Are mapper-generated setters reviewed?
[ ] Are flush timing surprises tested?
```

---

## One-Page Cheat Sheet

```text
Dirty Checking
==============

Core Idea
---------
Hibernate compares original snapshot with current managed entity state.
If different, entity is dirty.
Dirty entity becomes UPDATE SQL during flush.

Works Only When
---------------
Entity is managed.
Persistence context is active.
Transaction/flush occurs.
Field is mapped.

Does Not Work When
------------------
Entity is detached.
No persistence context tracks it.
Transaction rolled back.
Object is not an entity.
Field is not mapped.

Flush vs Commit
---------------
flush  = send SQL to database
commit = make transaction permanent

Best Sentence
-------------
Dirty checking is snapshot comparison for managed entities at flush time.

Debugging Rules
---------------
Change not saved?       Check managed state + transaction.
Unexpected update?      Check setters/mappers on managed entity.
Slow commit?            Check persistence context size.
Lost update?            Add optimistic locking with @Version.
Batch memory issue?     flush and clear periodically.
```

---

## Last-Minute Interview Revision

Do not say:

```text
Hibernate automatically saves objects when values change.
```

Say:

```text
Hibernate tracks managed entities inside the persistence context. It stores original snapshots and compares them with current state during flush. If a difference exists, Hibernate generates update SQL.
```

Senior version:

```text
Dirty checking is convenient, but I treat managed entities as live database-backed objects. I keep transaction boundaries clear, avoid blind setters, control persistence context size, and use optimistic locking when concurrent updates matter.
```

---

## One Picture To Remember

```text
                    ENTITY LOADED
                         |
                         v
              +---------------------+
              | Original Snapshot   |
              | stock = 10          |
              +---------------------+
                         |
                         | business method changes object
                         v
              +---------------------+
              | Current Entity      |
              | stock = 8           |
              +---------------------+
                         |
                         | flush
                         v
              +---------------------+
              | Compare             |
              | 10 != 8             |
              +---------------------+
                         |
                         v
              +---------------------+
              | UPDATE SQL          |
              +---------------------+
                         |
                         v
                      DATABASE
```

Final retention sentence:

> **Dirty checking means Hibernate remembers how a managed entity looked when it entered the persistence context, then compares it with how it looks at flush time.**
