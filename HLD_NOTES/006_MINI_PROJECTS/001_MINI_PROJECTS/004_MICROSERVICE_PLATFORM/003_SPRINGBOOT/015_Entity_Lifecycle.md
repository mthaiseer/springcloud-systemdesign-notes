# 015_Entity_Lifecycle — The Four-State Tracking Model

## Core Mental Model

Do not imagine a JPA entity as “just a database row represented by a Java object”.
That model is incomplete.

A better mental model is:

> **A JPA entity is a Java object whose database synchronization depends on its lifecycle state.**

```text
Java Object Exists
      |
      v
+-------------------+      persist/save      +-------------------+
| TRANSIENT         | ---------------------> | MANAGED           |
| new object        |                        | tracked by        |
| Hibernate does    |                        | Persistence       |
| not track it      |                        | Context           |
+-------------------+                        +-------------------+
                                                |
                                                | transaction ends
                                                | clear / close
                                                v
+-------------------+       merge             +-------------------+
| REMOVED           | <--------------------   | DETACHED          |
| scheduled delete  |                        | normal Java object|
| while managed     |                        | not tracked       |
+-------------------+                        +-------------------+
```

This chapter teaches exactly one idea:

> **Hibernate can only automatically synchronize entities that are managed inside the persistence context.**

If you remember only one sentence:

> **Before asking “why did Hibernate save or not save this?”, ask: what lifecycle state is this entity in right now?**

---

## Why This Exists

In normal Java, an object is just an object.

```java
Product product = new Product("Keyboard", 10);
product.decreaseStock(2);
```

In Hibernate, this is not enough.
Hibernate needs to know whether this object is currently under its supervision.

```text
Same Java object operation:

product.decreaseStock(2)

Can mean different things:

TRANSIENT  -> Hibernate does not know it exists
MANAGED    -> Hibernate tracks it and can flush UPDATE
DETACHED   -> Hibernate does not track later changes
REMOVED    -> Hibernate plans DELETE
```

That is why entity lifecycle exists.
It explains the most common JPA confusion:

```text
Why did this update happen without save()?
Why did this update not happen even after changing fields?
Why did merge create strange behavior?
Why did delete happen only at commit?
Why did LazyInitializationException happen?
```

All of these are lifecycle-state questions.

---

## Problem Statement

A backend service wants to update a product.

```java
product.decreaseStock(2);
```

But Hibernate must decide:

```text
Should I INSERT this object?
Should I UPDATE this object?
Should I ignore this object?
Should I DELETE this object?
Should I copy its state into another managed object?
```

The Java method call alone does not answer that.
The answer depends on the entity lifecycle state.

The core problem:

> **Hibernate needs a precise tracking model to decide which Java object changes become SQL.**

That tracking model is entity lifecycle.

---

## Real World Analogy

Imagine a hospital patient record system.

```text
Person not registered yet        -> TRANSIENT
Patient admitted and monitored   -> MANAGED
Patient discharged               -> DETACHED
Patient record marked for removal-> REMOVED
```

If the patient is admitted, nurses monitor changes:

```text
Blood pressure changed
Medication changed
Room changed
```

The hospital system records it.

If the patient has already left and tells something to a nurse outside the hospital, the system does not automatically update.

That is Hibernate.

```text
Hospital analogy              Hibernate
----------------              ---------
Admitted patient              Managed entity
Discharged patient            Detached entity
New visitor                   Transient object
Marked for deletion           Removed entity
Hospital monitoring system    Persistence Context
End-of-shift sync             Flush
```

Important lesson:

> **Only admitted patients are monitored. Only managed entities are tracked.**

---

## The One Mental Model

Think of the persistence context as a tracking room.

```text
             Persistence Context
+------------------------------------------------+
|                                                |
|  Managed entities are inside this room.         |
|                                                |
|  Hibernate tracks:                             |
|    - identity                                  |
|    - original snapshot                         |
|    - current field values                      |
|    - pending insert/update/delete actions      |
|                                                |
+------------------------------------------------+
```

Entity lifecycle is simply movement in and out of this room.

```text
Outside room, never entered  -> TRANSIENT
Inside room, tracked         -> MANAGED
Left room                    -> DETACHED
Inside room, marked delete   -> REMOVED
```

That is the whole chapter.

---

## Core Concepts

## 1. Transient Entity

A transient entity is a new Java object that Hibernate does not know about yet.

```java
Product product = new Product("Keyboard", 10);
```

State:

```text
Object exists in JVM heap.
No database row necessarily exists.
Persistence context does not track it.
Dirty checking does not apply.
No SQL will happen automatically.
```

Diagram:

```text
Java Heap
+-------------------------+
| Product@abc             |
| name = Keyboard         |
| stock = 10              |
+-------------------------+

Persistence Context
+-------------------------+
| empty                   |
+-------------------------+

Database
+-------------------------+
| no row yet              |
+-------------------------+
```

If the transaction commits now, nothing happens.
Hibernate cannot flush an object it does not know.

---

## 2. Managed Entity

A managed entity is inside the persistence context.
Hibernate tracks it.

There are two common ways to become managed:

```java
Product product = productRepository.findById(1L).orElseThrow();
```

or

```java
Product product = new Product("Keyboard", 10);
productRepository.save(product);
```

State:

```text
Object is known by EntityManager.
Entity identity is tracked.
Original snapshot is stored.
Dirty checking applies.
Changes can become SQL at flush.
```

Diagram:

```text
Persistence Context
+----------------+------------------+------------------+
| Entity Key     | Object Reference | Snapshot          |
+----------------+------------------+------------------+
| Product#1      | Product@abc      | stock=10          |
+----------------+------------------+------------------+

Java code changes:
Product@abc stock = 8

Flush sees:
Snapshot stock=10, current stock=8

SQL:
UPDATE products SET stock = 8 WHERE id = 1;
```

This is why managed entity updates often do not need explicit `save()`.

---

## 3. Detached Entity

A detached entity used to be managed, but the persistence context is now closed, cleared, or no longer tracking it.

```java
Product product;

@Transactional
public Product loadProduct(Long id) {
    return productRepository.findById(id).orElseThrow();
}

// transaction ended here
product.rename("New Name");
```

State:

```text
Object still exists in Java memory.
It may have a database id.
Hibernate is not tracking it anymore.
Changes are not automatically synchronized.
```

Diagram:

```text
Before transaction ends:

Persistence Context
+----------------+------------------+
| Product#1      | Product@abc      |
+----------------+------------------+

After transaction ends:

Java Heap
+-------------------------+
| Product@abc             |
| id = 1                  |
| name = New Name         |
+-------------------------+

Persistence Context
+-------------------------+
| closed / not tracking   |
+-------------------------+
```

Changing a detached entity is like editing a photocopy.
The database does not know.

---

## 4. Removed Entity

A removed entity is managed but scheduled for deletion.

```java
@Transactional
public void deleteProduct(Long id) {
    Product product = productRepository.findById(id).orElseThrow();
    productRepository.delete(product);
}
```

State:

```text
Entity is managed.
Entity is marked for removal.
DELETE SQL is scheduled.
Actual delete happens during flush.
```

Diagram:

```text
Persistence Context
+----------------+------------------+----------------+
| Product#1      | Product@abc      | REMOVED        |
+----------------+------------------+----------------+

Flush:
DELETE FROM products WHERE id = 1;
```

Important:

```text
remove() does not always mean SQL immediately.
It usually schedules delete for flush.
```

---

## Internal Architecture

```text
Spring Service Method
        |
        v
+--------------------------+
| @Transactional Proxy     |
| opens transaction         |
+--------------------------+
        |
        v
+--------------------------+
| EntityManager            |
| manages entity state      |
+--------------------------+
        |
        v
+--------------------------+
| Persistence Context      |
| lifecycle state tracking |
+--------------------------+
        |
        v
+--------------------------+
| Hibernate Engine         |
| dirty checking + flush   |
+--------------------------+
        |
        v
+--------------------------+
| Database                 |
+--------------------------+
```

The entity lifecycle is not controlled by the entity class itself.
It is controlled by the persistence context.

```text
Entity object does not know:
  “I am managed.”

Persistence context knows:
  “I am tracking Product#1 as Product@abc.”
```

---

## Internal Working

Consider this service:

```java
@Transactional
public void updateStock(Long id, int quantity) {
    Product product = productRepository.findById(id).orElseThrow();
    product.decreaseStock(quantity);
}
```

What you see:

```text
load product
change stock
return
```

What Hibernate sees:

```text
1. Transaction begins.
2. Persistence context opens.
3. findById asks EntityManager for Product#1.
4. Persistence context checks identity map.
5. Product#1 not present.
6. Hibernate executes SELECT.
7. Row becomes Product@abc.
8. Product@abc is registered as MANAGED.
9. Snapshot is stored: stock=10.
10. product.decreaseStock(2) changes stock to 8.
11. Entity remains MANAGED.
12. Commit triggers flush.
13. Dirty checking compares stock 10 vs 8.
14. UPDATE SQL is generated.
15. Database transaction commits.
16. Persistence context closes.
17. Product@abc becomes DETACHED if still referenced outside.
```

The hidden transition:

```text
MANAGED during transaction
DETACHED after transaction
```

This is the source of many bugs.

---

## Java Code Example

```java
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int stock;

    protected Product() {
    }

    public Product(String name, int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("stock cannot be negative");
        }
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
            throw new IllegalArgumentException("name is required");
        }
        this.name = newName;
    }

    public Long getId() {
        return id;
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
    public Long create(String name, int stock) {
        Product product = new Product(name, stock);     // TRANSIENT
        Product saved = productRepository.save(product); // MANAGED
        return saved.getId();
    }

    @Transactional
    public void reserve(Long id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found")); // MANAGED

        product.decreaseStock(quantity); // dirty checked
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found")); // MANAGED

        productRepository.delete(product); // REMOVED
    }
}
```

Execution explanation:

```text
create():
  new Product()      -> transient
  save(product)      -> managed, insert scheduled
  commit/flush       -> INSERT

reserve():
  findById()         -> managed
  decreaseStock()    -> current state differs from snapshot
  commit/flush       -> UPDATE

delete():
  findById()         -> managed
  delete(product)    -> removed
  commit/flush       -> DELETE
```

---

## Spring Boot Example With Controller

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<CreateProductResponse> create(@RequestBody CreateProductRequest request) {
        Long id = productService.create(request.name(), request.stock());
        return ResponseEntity.status(201).body(new CreateProductResponse(id));
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<Void> reserve(@PathVariable Long id,
                                        @RequestParam int quantity) {
        productService.reserve(id, quantity);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

DTOs:

```java
public record CreateProductRequest(String name, int stock) {}
public record CreateProductResponse(Long id) {}
```

Layer ownership:

```text
Controller:
  HTTP request/response only

Service:
  transaction boundary and entity lifecycle transitions

Repository:
  persistence doorway into EntityManager

Hibernate:
  tracks entity states and flushes SQL
```

---

## Rich ASCII Diagram — Lifecycle State Machine

```text
                                   findById()
                           database row loaded
                                  +--------+
                                  |        v
+------------------+ persist/save +------------------+
| TRANSIENT        | -----------> | MANAGED          |
|------------------|              |------------------|
| new Java object  |              | tracked by PC    |
| no DB identity   |              | dirty checked    |
| not tracked      |              | snapshot exists  |
+------------------+              +------------------+
                                      |       |
                                      |       | remove/delete
                                      |       v
                                      |   +------------------+
                                      |   | REMOVED          |
                                      |   |------------------|
                                      |   | delete scheduled |
                                      |   | SQL at flush     |
                                      |   +------------------+
                                      |
                                      | transaction ends
                                      | clear / close
                                      v
                                  +------------------+
                                  | DETACHED         |
                                  |------------------|
                                  | has id maybe     |
                                  | not tracked      |
                                  | changes ignored  |
                                  +------------------+
                                      |
                                      | merge
                                      v
                                  +------------------+
                                  | MANAGED COPY     |
                                  +------------------+
```

Remember:

```text
merge() does not reattach the same object in the simple mental model.
It copies detached state into a managed instance and returns that managed instance.
```

---

## Step-by-Step Dry Run — Normal Managed Update

Database before:

```text
products
+----+----------+-------+
| id | name     | stock |
+----+----------+-------+
| 1  | Keyboard | 10    |
+----+----------+-------+
```

Code:

```java
@Transactional
public void reserve(Long id) {
    Product product = productRepository.findById(id).orElseThrow();
    product.decreaseStock(2);
}
```

Flow:

```text
1. Service method enters through transactional proxy.
2. Transaction starts.
3. Persistence context opens.
4. findById(1) loads database row.
5. Product@abc becomes MANAGED.
6. Snapshot saved: stock=10.
7. decreaseStock(2) changes current stock to 8.
8. Method exits.
9. Flush starts.
10. Dirty checking sees stock changed.
11. UPDATE is sent.
12. Transaction commits.
13. Persistence context closes.
```

Database after:

```text
products
+----+----------+-------+
| id | name     | stock |
+----+----------+-------+
| 1  | Keyboard | 8     |
+----+----------+-------+
```

---

## Step-by-Step Dry Run — Detached Update Does Not Persist

Code:

```java
public Product load(Long id) {
    return productRepository.findById(id).orElseThrow();
}

public void wrongUpdate(Long id) {
    Product product = load(id);
    product.decreaseStock(2);
}
```

Assume no outer transaction around `wrongUpdate`.

Flow:

```text
1. load(id) calls repository.
2. Repository method opens/uses a short persistence context.
3. Product is loaded and managed briefly.
4. Repository method returns.
5. Persistence context is closed.
6. Product becomes DETACHED.
7. wrongUpdate changes detached object.
8. Hibernate is not tracking it.
9. No dirty checking happens for that object.
10. No UPDATE is sent.
```

Mental picture:

```text
Product@abc while managed:
  inside tracking room

Product@abc after load returned:
  outside tracking room

Changing it outside tracking room:
  no automatic database synchronization
```

Fix:

```java
@Transactional
public void correctUpdate(Long id) {
    Product product = productRepository.findById(id).orElseThrow();
    product.decreaseStock(2);
}
```

---

## Step-by-Step Dry Run — Transient Insert

Code:

```java
@Transactional
public Long createProduct() {
    Product product = new Product("Mouse", 50);
    productRepository.save(product);
    return product.getId();
}
```

Flow:

```text
1. new Product(...) creates Java object.
2. State is TRANSIENT.
3. Hibernate does not track it yet.
4. save(product) delegates to EntityManager persist/merge behavior.
5. Entity becomes MANAGED.
6. INSERT is scheduled.
7. Depending on ID strategy, INSERT may happen early or at flush.
8. Transaction commits.
9. Row exists in database.
```

Diagram:

```text
new Product()
    |
    v
TRANSIENT
    |
    | save/persist
    v
MANAGED
    |
    | flush
    v
INSERT SQL
```

---

## Step-by-Step Dry Run — Removed Entity

Code:

```java
@Transactional
public void remove(Long id) {
    Product product = productRepository.findById(id).orElseThrow();
    productRepository.delete(product);
}
```

Flow:

```text
1. findById loads Product#1.
2. Product becomes MANAGED.
3. delete(product) marks entity as REMOVED.
4. Hibernate schedules DELETE.
5. Entity is not deleted from DB immediately in the mental model.
6. Flush sends DELETE SQL.
7. Commit makes deletion permanent.
```

Important:

```text
Removed is not the same as detached.
Removed still belongs to the persistence context until flush/commit.
```

---

## Deep Walkthrough Example — merge() Confusion

`merge()` is one of the most misunderstood JPA operations.

Bad mental model:

```text
merge attaches my detached object again.
```

Better mental model:

```text
merge copies detached object state into a managed object and returns the managed object.
```

Example:

```java
@Transactional
public void updateDetached(Product detachedProduct) {
    Product managedProduct = entityManager.merge(detachedProduct);

    managedProduct.rename("Correct Name");
    detachedProduct.rename("Wrong Name");
}
```

What happens:

```text
1. detachedProduct is not tracked.
2. merge(detachedProduct) finds or creates a managed instance.
3. State from detachedProduct is copied into managedProduct.
4. managedProduct is tracked.
5. Changes to managedProduct are dirty checked.
6. Later changes to detachedProduct are still not tracked.
```

Diagram:

```text
Detached object
+------------------+
| Product@old      |
| id=1             |
| name=A           |
+------------------+
        |
        | merge copies state
        v
Persistence Context
+------------------+
| Product@managed  |
| id=1             |
| name=A           |
+------------------+

After merge:
Product@managed is tracked.
Product@old is still detached.
```

Senior rule:

> **Use merge carefully. Prefer loading the managed entity inside the transaction, then applying changes intentionally.**

Better update pattern:

```java
@Transactional
public void rename(Long id, String newName) {
    Product product = productRepository.findById(id).orElseThrow();
    product.rename(newName);
}
```

This avoids detached-object surprises.

---

## Production Scale Example

Imagine a product catalog service handling 3,000 requests per second.

```text
API Gateway
    |
    v
Product Service
    |
    +-- create product
    +-- update stock
    +-- rename product
    +-- delete product
    |
    v
Postgres
```

At small scale, lifecycle mistakes look like random bugs.
At scale, they become data inconsistency and performance issues.

Common production lifecycle problems:

```text
1. Detached entity updated but not persisted
   Symptom: user sees success but DB unchanged.

2. merge() overwrites newer DB state
   Symptom: lost update from stale detached DTO/entity.

3. Huge batch keeps all entities managed
   Symptom: memory spike and slow flush.

4. Delete scheduled but constraint fails at flush
   Symptom: exception appears at commit, not delete line.

5. Lazy relation accessed after detachment
   Symptom: LazyInitializationException in controller/serializer.
```

At production scale, you must know:

```text
Which objects are managed?
When does the persistence context close?
How large is the persistence context?
When does flush happen?
```

---

## Production Failure Story

A team built an admin endpoint to update product details.

Controller received JSON and converted it directly into an entity:

```java
@PutMapping("/products/{id}")
public void update(@PathVariable Long id, @RequestBody Product product) {
    product.setId(id);
    productService.update(product);
}
```

Service:

```java
@Transactional
public void update(Product product) {
    productRepository.save(product);
}
```

It worked in testing.
Later, production users reported that some fields were randomly becoming null.

Root cause:

```text
Request JSON had only a few fields.
Controller created a detached/incomplete entity.
save/merge copied null fields into managed state.
Hibernate flushed UPDATE.
Database row lost values.
```

The bug was not “Hibernate is bad”.
The bug was wrong lifecycle thinking.

Bad model:

```text
Incoming JSON entity = database entity
```

Correct model:

```text
Incoming JSON is a command/DTO.
Load managed entity inside transaction.
Apply allowed changes.
Dirty checking flushes intentional update.
```

Fixed service:

```java
@Transactional
public void updateProduct(Long id, UpdateProductRequest request) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("not found"));

    product.rename(request.name());
    product.changeDescription(request.description());
}
```

Lesson:

> **Do not treat detached request objects as managed domain objects. Load the managed entity, then mutate it intentionally.**

---

## Debugging Mindset

When a JPA update/delete/insert behaves strangely, do not start with SQL.
Start with lifecycle state.

Ask:

```text
1. Was this entity created with new?
   -> likely TRANSIENT

2. Was this entity loaded in the current transaction?
   -> likely MANAGED

3. Did the transaction already end?
   -> likely DETACHED

4. Was delete/remove called?
   -> likely REMOVED

5. Is save/merge being used with an incomplete detached object?
   -> possible accidental overwrite
```

Symptom map:

```text
Change not saved
  -> entity was detached or no transaction existed

Update happened without save
  -> entity was managed and dirty checking flushed it

Null fields overwritten
  -> detached incomplete entity merged

Delete error appears at commit
  -> delete was scheduled; constraint failed during flush

LazyInitializationException
  -> entity became detached before lazy association was accessed

Memory grows in batch job
  -> too many managed entities retained in persistence context
```

---

## Failure Investigation Playbook

## Step 1 — Identify where entity came from

```text
new Product()                    -> transient
repository.findById() in txn      -> managed
returned from old transaction     -> detached
request body mapped to entity     -> detached-like/incomplete object
```

## Step 2 — Identify transaction boundary

```text
Is @Transactional on public service method?
Is the method called through Spring proxy?
Does self-invocation bypass transaction?
Did exception trigger rollback?
```

## Step 3 — Check SQL timing

Enable SQL logs in lower environment:

```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```

Look for:

```text
INSERT generated?
UPDATE generated?
DELETE generated?
Unexpected update columns?
SQL appears at method call or commit?
```

## Step 4 — Check merge usage

Search for:

```text
save(entityFromRequest)
entityManager.merge(dtoMappedEntity)
BeanUtils.copyProperties(request, entity)
```

These are common accidental overwrite sources.

## Step 5 — Check persistence context size

For batch jobs, avoid keeping thousands of entities managed forever.

```java
for (int i = 0; i < items.size(); i++) {
    entityManager.persist(items.get(i));

    if (i % 50 == 0) {
        entityManager.flush();
        entityManager.clear();
    }
}
```

Mental model:

```text
flush -> synchronize SQL
clear -> detach all managed entities
```

---

## Common Misconceptions

## Misconception 1: “Entity object always equals database row”

Wrong.

An entity object may be:

```text
new but not inserted
managed and synchronized later
detached and stale
removed but not deleted yet
```

The lifecycle state matters.

## Misconception 2: “Calling setters always updates database”

Only if the entity is managed and transaction flushes successfully.

```text
managed setter call   -> dirty checking can update DB
detached setter call  -> normal Java change only
```

## Misconception 3: “save() means SQL immediately happens”

Not always.

`save()` changes lifecycle state and schedules persistence work.
SQL happens at flush, though some ID strategies can force earlier insert.

## Misconception 4: “merge makes my object managed”

Dangerous simplification.

Better:

```text
merge copies detached state into a managed instance and returns the managed instance.
```

The original detached object should not be treated as tracked.

## Misconception 5: “Returning entities from service/controller is harmless”

It often creates lifecycle bugs:

```text
lazy loading after transaction
JSON serialization triggering queries
exposing internal fields
modifying detached entities later
```

Prefer DTOs for API boundaries.

## Misconception 6: “delete removes immediately”

Usually delete is scheduled and executed on flush.
That is why constraint violations may appear at commit time.

---

## Performance Considerations

Entity lifecycle affects performance directly.

### Persistence Context Size

Every managed entity costs memory and dirty checking work.

Bad batch:

```java
@Transactional
public void importProducts(List<Product> products) {
    for (Product product : products) {
        entityManager.persist(product);
    }
}
```

Problem:

```text
100,000 products become managed.
Persistence context grows huge.
Flush becomes expensive.
Memory increases.
```

Better:

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

### Detached DTO Updates

Bad:

```java
productRepository.save(productFromRequest);
```

Risk:

```text
merge may update more columns than intended.
stale detached state may overwrite current DB state.
```

Better:

```java
Product product = productRepository.findById(id).orElseThrow();
product.rename(request.name());
```

### Long Transactions

A long transaction keeps entities managed for longer.

Bad:

```java
@Transactional
public void updateAndCallExternalApi(Long id) {
    Product product = productRepository.findById(id).orElseThrow();
    product.rename("New Name");
    externalClient.notifyWarehouse(product.getId());
}
```

Problem:

```text
DB connection and managed context stay open while external API waits.
Locks may be held longer.
Flush/commit delayed.
```

---

## Scalability Considerations

At scale, lifecycle decisions become architecture decisions.

```text
Write use cases:
  Load managed aggregate.
  Apply domain behavior.
  Let dirty checking flush intentional changes.

Read use cases:
  Prefer DTO projections for list screens.
  Avoid returning managed entities to web layer.

Batch use cases:
  Control persistence context size with flush/clear.

API boundaries:
  Do not accept entities as request bodies.
  Use commands/DTOs, then load managed entities.
```

Senior rule:

> **Use managed entities for transactional domain changes, not as transport objects.**

---

## Interview Q&A

### Q1. What are the main JPA entity lifecycle states?

Strong answer:

> The main states are transient, managed, detached, and removed. Transient means a new object not tracked by persistence context. Managed means Hibernate tracks it and dirty checking applies. Detached means it was once managed but is no longer tracked. Removed means it is managed but scheduled for deletion.

### Q2. What is a managed entity?

Strong answer:

> A managed entity is an entity instance associated with the current persistence context. Hibernate tracks its identity and original snapshot, so changes can be detected and flushed to the database.

### Q3. What is a detached entity?

Strong answer:

> A detached entity is an object that has a database identity but is no longer associated with an active persistence context. It is just a normal Java object. Changes to it are not automatically synchronized unless it is merged or the managed entity is loaded and updated.

### Q4. Why can an update happen without calling save()?

Strong answer:

> Because the entity is managed. Hibernate stores a snapshot when it loads the entity. At flush time, dirty checking compares the snapshot with the current state and generates an UPDATE if needed.

### Q5. What does merge do?

Strong answer:

> merge copies state from a detached entity into a managed instance and returns the managed instance. The original detached object should not be treated as managed after merge.

### Q6. Why is mapping request JSON directly to an entity dangerous?

Strong answer:

> Because the request may represent a partial, detached, or invalid version of the entity. Saving or merging it can overwrite database fields with null or stale values. A safer pattern is to use a DTO, load the managed entity inside a transaction, and apply allowed changes.

### Q7. When does removed entity deletion happen?

Strong answer:

> Calling remove or delete marks the managed entity for deletion. The actual DELETE SQL usually happens during flush, often before commit.

---

## Production Checklist

```text
Entity Creation
[ ] Is new entity explicitly persisted/saved?
[ ] Is generated id strategy understood?
[ ] Are constructors protecting invariants?

Managed Updates
[ ] Is update inside @Transactional service method?
[ ] Is entity loaded in same transaction?
[ ] Are changes applied through domain methods?
[ ] Is explicit save avoided when dirty checking is enough?

Detached Objects
[ ] Are entities avoided as request DTOs?
[ ] Is merge used only intentionally?
[ ] Are stale detached objects prevented from overwriting data?
[ ] Are DTOs mapped into managed entities carefully?

Deletes
[ ] Are FK constraints/cascades understood?
[ ] Is delete expected at flush/commit?
[ ] Are orphanRemoval/cascade rules intentional?

Batch Jobs
[ ] Is persistence context size controlled?
[ ] Are flush and clear used for large imports?
[ ] Is JDBC/bulk query considered for huge operations?
```

---

## One-Page Cheat Sheet

```text
Entity Lifecycle Mental Model
=============================

TRANSIENT
  New Java object.
  Hibernate does not track it.
  No automatic SQL.

MANAGED
  Inside persistence context.
  Hibernate tracks identity and snapshot.
  Dirty checking applies.
  Changes flush to SQL.

DETACHED
  Was managed before, but context closed/cleared.
  Has id maybe, but not tracked.
  Changes do not auto-save.

REMOVED
  Managed entity marked for deletion.
  DELETE scheduled.
  SQL usually happens at flush.

Best Rule
---------
Before debugging JPA behavior, ask:
What state is this entity in right now?

Safe Update Pattern
-------------------
@Transactional
load managed entity
apply allowed changes
let dirty checking flush

Danger Pattern
--------------
request JSON -> entity -> save/merge
Can overwrite fields accidentally.

merge()
-------
Copies detached state into managed instance.
Returns managed instance.
Original object remains detached.
```

---

## Last-Minute Interview Revision

Do not say:

```text
An entity is just an object mapped to a table.
```

Say:

```text
An entity is an object mapped to a table, but Hibernate behavior depends on lifecycle state. Only managed entities are tracked by the persistence context and synchronized through dirty checking at flush time.
```

Senior version:

```text
Most JPA bugs come from lifecycle confusion: updating detached objects, merging incomplete request entities, holding too many managed entities, or assuming delete/update SQL happens immediately. I debug by identifying the entity state first, then transaction boundary, then flush timing.
```

---

## Mental Models Table

```text
Question                              Mental Model
--------                              ------------
Why no update SQL?                    Entity was detached or no transaction.
Why update without save()?            Entity was managed and dirty checked.
Why null fields overwritten?          Incomplete detached entity merged.
Why delete failed at commit?          DELETE scheduled, constraint failed on flush.
Why memory high in batch?             Too many managed entities in persistence context.
Why lazy exception?                   Entity detached before lazy relation access.
```

---

## One Picture To Remember

```text
                     ENTITY LIFECYCLE

      new Product()
           |
           v
+---------------------+
| TRANSIENT           |
| not tracked         |
+---------------------+
           |
           | persist/save
           v
+---------------------+       field change        +---------------------+
| MANAGED             | ------------------------> | DIRTY MANAGED       |
| tracked by PC       |                           | update at flush     |
+---------------------+                           +---------------------+
           |
           | close / clear / transaction ends
           v
+---------------------+
| DETACHED            |
| not tracked         |
| changes ignored     |
+---------------------+
           |
           | merge copies state
           v
+---------------------+
| MANAGED COPY        |
+---------------------+

MANAGED -- remove/delete --> REMOVED -- flush --> DELETE SQL
```

Final retention sentence:

> **Hibernate does not track every entity object forever; it only tracks managed entities inside the persistence context. Entity lifecycle is the map of when Hibernate is watching and when it is not.**
