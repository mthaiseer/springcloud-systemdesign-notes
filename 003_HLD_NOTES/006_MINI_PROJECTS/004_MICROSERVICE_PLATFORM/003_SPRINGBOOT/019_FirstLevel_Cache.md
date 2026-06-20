# 019_FirstLevel_Cache — The One-Object-Per-Row Memory Map

## Core Mental Model

Do not imagine Hibernate first-level cache as “Redis inside Hibernate”.
That is the wrong mental model.

The better mental model is:

> **The first-level cache is the persistence context’s identity map: inside one persistence context, one database row is represented by exactly one Java object.**

```text
Database Row                         Persistence Context
+-------------------+                +-------------------------------+
| products id=1001  |  SELECT once   | Product#1001 -> Product@abc   |
| stock = 10        | -------------> | snapshot: stock=10            |
+-------------------+                +-------------------------------+
                                           |
                                           | second findById(1001)
                                           v
                                      returns same Product@abc
```

This chapter teaches exactly one idea:

> **Hibernate first-level cache is not mainly about speeding up queries. It is about identity consistency and change tracking inside one persistence context.**

If you remember only one sentence:

> **First-level cache means: within one persistence context, the same database row maps to the same Java object reference.**

---

## Why This Exists

Suppose a service method loads the same product twice:

```java
@Transactional
public void reserveTwice(Long productId) {
    Product p1 = productRepository.findById(productId).orElseThrow();
    Product p2 = productRepository.findById(productId).orElseThrow();

    p1.decreaseStock(1);
    p2.decreaseStock(1);
}
```

Without a first-level cache, Hibernate could create two different Java objects for the same row:

```text
Product row id=1001
        |
        +--> Product@abc stock=10
        |
        +--> Product@xyz stock=10
```

Now what should happen at flush?

```text
Product@abc says stock = 9
Product@xyz says stock = 9

Did we reserve once or twice?
Which object wins?
Should final stock be 9 or 8?
```

That would be dangerous.

Hibernate avoids this by enforcing identity inside the persistence context:

```text
Product row id=1001
        |
        v
Product@abc only
```

So both variables point to the same object:

```java
System.out.println(p1 == p2); // true inside same persistence context
```

That is the heart of first-level cache.

---

## Problem Statement

Hibernate must solve three problems inside a transaction:

```text
1. Avoid loading the same row into multiple Java objects.
2. Remember which objects are already managed.
3. Track changes correctly during flush.
```

The first-level cache solves this by storing managed entities in a map:

```text
Entity Key                Object Reference
----------                ----------------
Product#1001       --->   Product@abc
Customer#42        --->   Customer@def
Order#501          --->   Order@ghi
```

The key idea:

```text
Entity key = entity class + primary key
```

Example:

```text
Product#1001 is different from Order#1001
because the entity type is part of the key.
```

So the first-level cache is not a global cache.
It is not shared between requests.
It does not survive application restart.
It does not replace Redis.

It lives only inside one persistence context.

---

## Real World Analogy

Imagine a bank clerk handling one customer session.

The clerk keeps a temporary folder on the desk:

```text
Desk Folder
+--------------------------+
| Customer 42 form         |
| Account 9001 form        |
| Loan 7001 form           |
+--------------------------+
```

If the clerk needs Customer 42 again, they do not go to the archive room again.
They reuse the same paper already on the desk.

```text
Archive Room       -> Database
Desk Folder        -> First-level cache / persistence context
Clerk              -> EntityManager
Paper form         -> Entity object
End of session     -> Flush + commit + close
```

Important:

> The desk folder is private to that clerk’s current session.

Another clerk has another desk folder.

```text
Transaction A desk folder != Transaction B desk folder
```

That is first-level cache.

---

## The One Mental Model

Think of the persistence context as a map:

```text
Map<EntityKey, EntityObject>
```

```text
+--------------------------------------------------+
| Persistence Context                               |
|--------------------------------------------------|
| First-Level Cache / Identity Map                  |
|                                                  |
| Product#1001  -> Product@abc                      |
| Product#1002  -> Product@bcd                      |
| Customer#42   -> Customer@cde                     |
| Order#501     -> Order@def                        |
|                                                  |
| Snapshots                                         |
| Product#1001  -> original stock=10                |
| Customer#42   -> original email=a@example.com     |
+--------------------------------------------------+
```

When Hibernate loads an entity, it first asks:

```text
Do I already have Product#1001 in the persistence context?
```

If yes:

```text
Return existing Product@abc.
No new SELECT needed for entityManager.find by id.
```

If no:

```text
Send SELECT.
Create object.
Put it into first-level cache.
Store snapshot.
Return object.
```

---

## Core Concepts

### First-Level Cache

The first-level cache is built into the persistence context.
It is always enabled.
You do not configure it like Redis.

```text
EntityManager / Hibernate Session
        |
        v
Persistence Context
        |
        v
First-Level Cache
```

It stores managed entity instances by identity key.

### Identity Map

Identity map means:

```text
Same row + same persistence context = same Java object reference
```

Example:

```java
Product p1 = entityManager.find(Product.class, 1001L);
Product p2 = entityManager.find(Product.class, 1001L);

System.out.println(p1 == p2); // true
```

This is not just an optimization.
It protects consistency.

### Managed Entity

Only managed entities are inside the first-level cache.

```text
Managed = tracked by persistence context
```

Loaded entities become managed.
New persisted entities become managed.
Detached entities are not in the first-level cache.

### Persistence Context Scope

In Spring Boot, the common model is:

```text
@Transactional method starts
        |
        v
Persistence context participates in transaction
        |
        v
Repository operations use same EntityManager context
        |
        v
Method ends
        |
        v
Flush / commit / close
```

A new transaction usually means a new persistence context.

### Not Second-Level Cache

First-level cache:

```text
- mandatory
- per persistence context
- stores actual managed entity objects
- used for identity and dirty checking
```

Second-level cache:

```text
- optional
- shared across sessions
- provider-backed
- stores entity data between persistence contexts
```

This chapter is only about first-level cache.

---

## Internal Architecture

```text
Spring Service Method
        |
        v
@Transactional Proxy
        |
        v
EntityManager bound to transaction
        |
        v
+----------------------------------------------------+
| Persistence Context                                 |
|----------------------------------------------------|
| First-Level Cache                                   |
|                                                    |
| EntityKey(Product, 1001) -> Product@abc             |
| EntityKey(User, 42)      -> User@def                |
|                                                    |
| Loaded State Snapshots                              |
| Product@abc -> [id=1001, name=Keyboard, stock=10]   |
| User@def    -> [id=42, email=a@example.com]         |
|                                                    |
| Action Queue                                        |
| pending inserts / updates / deletes                 |
+----------------------------------------------------+
        |
        v
Hibernate SQL engine
        |
        v
Database
```

The first-level cache is part of the same machinery that enables dirty checking.
Without it, Hibernate could not reliably know which object represents which row.

---

## Internal Working

Consider this code:

```java
@Transactional
public void process(Long productId) {
    Product a = productRepository.findById(productId).orElseThrow();
    Product b = productRepository.findById(productId).orElseThrow();

    a.decreaseStock(1);
    b.rename("Mechanical Keyboard");
}
```

What you see:

```text
find product
find product again
change stock
change name
```

What Hibernate does:

```text
1. Transaction starts.
2. Persistence context is empty.
3. First findById(Product#1001) happens.
4. Hibernate checks first-level cache.
5. Product#1001 is not present.
6. Hibernate sends SELECT.
7. Row becomes Product@abc.
8. Product#1001 -> Product@abc is stored in first-level cache.
9. Snapshot is stored.
10. Second findById(Product#1001) happens.
11. Hibernate checks first-level cache.
12. Product#1001 is already present.
13. Hibernate returns same Product@abc.
14. a and b point to same object.
15. Changes are made on same object.
16. Flush compares snapshot with current object.
17. One UPDATE is generated with final changed columns/state.
```

ASCII flow:

```text
findById(1001)
    |
    v
First-level cache lookup
    |
    +-- miss --> SELECT DB --> create Product@abc --> put in cache
    |
    v
findById(1001) again
    |
    v
First-level cache lookup
    |
    +-- hit --> return Product@abc
```

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

    protected Product() {}

    public void rename(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name;
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
    public void demoFirstLevelCache(Long id) {
        Product p1 = productRepository.findById(id).orElseThrow();
        Product p2 = productRepository.findById(id).orElseThrow();

        System.out.println(p1 == p2); // true

        p1.decreaseStock(1);
        p2.rename("Mechanical Keyboard");
    }
}
```

### Execution Explanation

```text
First findById:
  cache miss
  SELECT product
  Product object created
  Product#id stored in persistence context

Second findById:
  cache hit
  same object returned
  no second SELECT for entity by id

p1.decreaseStock:
  modifies Product@abc

p2.rename:
  also modifies Product@abc

commit:
  dirty checking sees stock and name changed
  UPDATE is generated
```

Important:

```text
p1 and p2 are not two copies.
They are two references to the same managed object.
```

---

## Spring Boot Example With Internal Flow

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/{id}/touch-twice")
    public ResponseEntity<Void> touchTwice(@PathVariable Long id) {
        productService.touchTwice(id);
        return ResponseEntity.noContent().build();
    }
}
```

```java
@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void touchTwice(Long id) {
        Product first = productRepository.findById(id).orElseThrow();
        Product second = productRepository.findById(id).orElseThrow();

        first.rename("Name From First Reference");
        second.decreaseStock(1);
    }
}
```

Internal flow:

```text
Controller
  -> ProductService proxy
     -> transaction starts
        -> persistence context opens
           -> findById first time
              -> cache miss
              -> SELECT
              -> put Product#id into first-level cache
           -> findById second time
              -> cache hit
              -> return same object
           -> modify object through first reference
           -> modify same object through second reference
        -> flush
        -> UPDATE
     -> commit
```

Expected SQL shape:

```text
SELECT product by id once
UPDATE product once at flush/commit if dirty
```

That is the learning checkpoint.

---

## Rich ASCII Diagram — First-Level Cache Hit

```text
@Transactional method
        |
        v
productRepository.findById(1001)
        |
        v
+------------------------------------------------+
| Persistence Context                             |
|------------------------------------------------|
| First-Level Cache                               |
|                                                |
| lookup Product#1001                             |
| result: MISS                                    |
+------------------------------------------------+
        |
        v
SELECT * FROM products WHERE id = 1001
        |
        v
Product@abc created
        |
        v
+------------------------------------------------+
| Persistence Context                             |
|------------------------------------------------|
| Product#1001 -> Product@abc                     |
| Snapshot: stock=10, name=Keyboard               |
+------------------------------------------------+
        |
        v
productRepository.findById(1001) again
        |
        v
+------------------------------------------------+
| Persistence Context                             |
|------------------------------------------------|
| lookup Product#1001                             |
| result: HIT -> return Product@abc               |
+------------------------------------------------+
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

Service:

```java
@Transactional
public void update(Long id) {
    Product p1 = productRepository.findById(id).orElseThrow();
    Product p2 = productRepository.findById(id).orElseThrow();

    p1.decreaseStock(2);
    p2.rename("Gaming Keyboard");
}
```

Dry run:

```text
1. Transaction starts.
2. Persistence context is empty.
3. First findById(1001) checks first-level cache.
4. Cache miss.
5. Hibernate sends SELECT.
6. Product@abc is created.
7. First-level cache stores Product#1001 -> Product@abc.
8. Snapshot stores name=Keyboard, stock=10.
9. Second findById(1001) checks first-level cache.
10. Cache hit.
11. Same Product@abc is returned.
12. p1.decreaseStock(2) changes Product@abc stock to 8.
13. p2.rename(...) changes Product@abc name.
14. Method ends.
15. Flush compares snapshot vs current object.
16. Hibernate generates UPDATE.
17. Transaction commits.
```

Database after:

```text
products
+------+-----------------+-------+
| id   | name            | stock |
+------+-----------------+-------+
| 1001 | Gaming Keyboard | 8     |
+------+-----------------+-------+
```

---

## Multiple Dry Runs

### Dry Run 1 — Same Transaction, Same Object

```java
@Transactional
public void sameTransaction(Long id) {
    Product a = productRepository.findById(id).orElseThrow();
    Product b = productRepository.findById(id).orElseThrow();

    System.out.println(a == b);
}
```

Result:

```text
true
```

Reason:

```text
Same persistence context.
Same entity key.
Same Java object.
```

### Dry Run 2 — Different Transactions, Different Objects

```java
@Transactional
public Product loadOne(Long id) {
    return productRepository.findById(id).orElseThrow();
}

@Transactional
public Product loadTwo(Long id) {
    return productRepository.findById(id).orElseThrow();
}
```

Result conceptually:

```text
loadOne returns Product@abc from persistence context A.
loadTwo returns Product@xyz from persistence context B.

They represent the same row but are different Java objects.
```

Reason:

```text
First-level cache is scoped to one persistence context.
```

### Dry Run 3 — JPQL Query May Still Hit Database

```java
@Transactional
public void queryExample(Long id) {
    Product p1 = entityManager.find(Product.class, id);

    Product p2 = entityManager.createQuery(
        "select p from Product p where p.id = :id", Product.class)
        .setParameter("id", id)
        .getSingleResult();
}
```

Important nuance:

```text
The JPQL query may execute SQL because queries need database results.
But when Hibernate builds the result, it reuses the existing managed object.
```

So:

```text
SQL may run.
p1 == p2 is still true.
```

Do not oversimplify first-level cache as “no SQL ever”.
The safer sentence is:

> **EntityManager.find by id can be served from first-level cache; queries may still hit DB, but managed identity is preserved.**

### Dry Run 4 — Clear Removes Cache Entries

```java
@Transactional
public void clearExample(Long id) {
    Product p1 = entityManager.find(Product.class, id);
    entityManager.clear();
    Product p2 = entityManager.find(Product.class, id);

    System.out.println(p1 == p2); // false
}
```

Reason:

```text
clear() detaches all managed entities.
The first-level cache becomes empty.
Second find loads a new object.
```

### Dry Run 5 — Refresh Overrides In-Memory State

```java
@Transactional
public void refreshExample(Long id) {
    Product p = entityManager.find(Product.class, id);
    p.rename("Temporary Name");

    entityManager.refresh(p);
}
```

Result:

```text
Hibernate reloads database state.
Temporary in-memory change is overwritten.
```

Use carefully.

---

## Production Scale Example

Imagine an order checkout service:

```text
POST /checkout
  - load cart
  - load customer
  - load product
  - calculate price
  - reserve stock
  - create order
```

Within one transaction, several components may ask for the same product:

```text
PricingService needs Product#1001
InventoryPolicy needs Product#1001
OrderFactory needs Product#1001
Audit code needs Product#1001
```

With first-level cache:

```text
Product#1001 loaded once by id.
Same object reused inside the transaction.
All changes converge on one managed instance.
```

This gives consistency.

But at scale, the first-level cache can become a memory problem.

Batch job example:

```java
@Transactional
public void importProducts(List<ProductCsvRow> rows) {
    for (ProductCsvRow row : rows) {
        Product product = new Product(row.name(), row.stock());
        entityManager.persist(product);
    }
}
```

If rows = 500,000:

```text
Persistence context stores 500,000 managed objects.
Memory grows.
Dirty checking becomes expensive.
Flush becomes heavy.
Application may run out of memory.
```

Better:

```java
for (int i = 0; i < rows.size(); i++) {
    Product product = new Product(rows.get(i).name(), rows.get(i).stock());
    entityManager.persist(product);

    if (i % 50 == 0) {
        entityManager.flush();
        entityManager.clear();
    }
}
```

Meaning:

```text
flush -> send pending SQL
clear -> empty first-level cache and detach objects
```

Senior lesson:

> **First-level cache is great for transactional consistency, but dangerous if you let it grow unbounded.**

---

## Production Failure Story

A team built a nightly product import job.

The code looked simple:

```java
@Transactional
public void importFile(List<ProductRow> rows) {
    for (ProductRow row : rows) {
        Product product = new Product(row.name(), row.stock());
        productRepository.save(product);
    }
}
```

It worked with 1,000 rows.
It failed with 700,000 rows.

Symptoms:

```text
- heap memory kept increasing
- GC pressure became high
- job slowed down over time
- eventually OutOfMemoryError
```

Root cause:

```text
Every saved Product became managed.
The persistence context kept all entities in first-level cache until transaction ended.
Hibernate had to track all of them.
```

The team thought:

```text
save() sends row to database and forgets object.
```

Reality:

```text
save() makes entity managed.
Managed entities stay in the persistence context until clear/close/detach.
```

Fix:

```text
- process in chunks
- flush regularly
- clear persistence context regularly
- keep transactions smaller
- use JDBC batch insert for very large imports if needed
```

Final lesson:

> **The first-level cache is not free. It is a memory map of managed entities. If you load or persist too many entities in one persistence context, you pay in memory and dirty checking cost.**

---

## Debugging Mindset

When debugging first-level cache issues, ask:

```text
1. Are two variables pointing to the same managed entity?
2. Are we inside the same persistence context?
3. Did we call clear(), detach(), or close()?
4. Is a query hitting the database but returning an already managed object?
5. Is the persistence context growing too large?
6. Are we expecting fresh database data but seeing cached managed state?
```

### Symptom Map

```text
Second findById does not hit DB
  -> normal first-level cache behavior

Same entity appears to update from two references
  -> both references point to same managed object

Memory grows in batch job
  -> persistence context holds too many managed entities

Stale value inside transaction
  -> first-level cache returns managed object, not fresh DB row

After clear(), changes not saved
  -> entity became detached before flush

JPQL query executes SQL but returns same object
  -> query hits DB for result ids, identity map reuses managed instance
```

### Useful Tools

```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
spring.jpa.properties.hibernate.generate_statistics=true
```

Use logs in local/lower environments to verify SQL shape.
For production, prefer metrics and sampled tracing because SQL logging can be expensive.

---

## Common Misconceptions

### Misconception 1: “First-level cache is like Redis”

No.

Redis is external, shared, and survives across requests.
First-level cache is inside one persistence context and stores managed entity objects.

### Misconception 2: “First-level cache means no queries run again”

Not always.

`find` by id can return from first-level cache.
JPQL/Criteria queries may still execute SQL.
But Hibernate preserves identity when materializing results.

### Misconception 3: “First-level cache is mainly for performance”

Performance is a benefit, but not the main point.
The main point is identity consistency and dirty checking.

### Misconception 4: “save() frees the entity after inserting”

No.

After persist/save, the entity is managed.
It remains in the persistence context until detached, cleared, or the context closes.

### Misconception 5: “Different transactions share first-level cache”

No.

Each persistence context has its own first-level cache.
Different transactions usually have different persistence contexts.

### Misconception 6: “clear() just clears SQL queue safely”

Careful.

`clear()` detaches all managed entities.
If you clear before flushing, pending changes may be lost.

Safer batch pattern:

```java
entityManager.flush();
entityManager.clear();
```

---

## Performance Considerations

First-level cache helps when:

```text
- same entity is loaded multiple times by id in one transaction
- multiple services inside one use case touch the same entity
- dirty checking needs one consistent object state
```

First-level cache hurts when:

```text
- huge batch loads too many entities
- long transaction keeps too many managed objects
- read-only reporting loads full entity graphs
- dirty checking scans many managed entities
```

Rules:

```text
Small transactional use case:
  first-level cache is your friend.

Huge batch job:
  first-level cache must be controlled.

Read-only list endpoint:
  consider DTO projections instead of loading managed entities.
```

Example projection:

```java
public record ProductCard(Long id, String name, int stock) {}

@Query("select new com.example.ProductCard(p.id, p.name, p.stock) from Product p")
List<ProductCard> findProductCards();
```

Why:

```text
Projection avoids filling the persistence context with full managed entities when you only need read-only response data.
```

---

## Scalability Considerations

At scale, ask for each endpoint:

```text
How many managed entities enter the persistence context?
How long does the persistence context live?
How many times are the same entities loaded?
Is this use case read-only or write-heavy?
Do we need entities or only DTO rows?
```

Expected patterns:

```text
Checkout endpoint:
  few entities, transactional consistency important -> entities are okay

Order listing endpoint:
  many rows, read-only -> projection is usually better

Import job:
  many writes -> batch flush/clear or JDBC batching

Analytics report:
  large aggregation -> SQL/native query/read model better
```

Senior production rule:

> **The first-level cache should be small, intentional, and scoped to one business unit of work.**

---

## Failure Investigation Playbook

### Step 1 — Identify Persistence Context Scope

Ask:

```text
Where does @Transactional start?
Where does it end?
Is Open Session in View enabled?
Is this a batch job with one huge transaction?
```

### Step 2 — Count Managed Entities

Look for:

```text
large findAll()
loops loading entities
batch save without clear
large object graphs
unnecessary eager fetches
```

### Step 3 — Check SQL Shape

Ask:

```text
Did second findById skip SQL because of first-level cache?
Did JPQL still execute SQL?
Are list endpoints loading entities unnecessarily?
```

### Step 4 — Check Freshness Expectations

If you expected fresh DB data:

```text
Was the entity already managed?
Did another transaction update the row?
Do you need refresh()?
Should the transaction be shorter?
```

### Step 5 — Fix by Scope Control

Possible fixes:

```text
- shorten transaction
- use projection
- flush and clear in batches
- detach specific entities
- avoid large findAll entity loads
- split huge jobs into chunks
```

---

## Interview Q&A

### Q1. What is Hibernate first-level cache?

Strong answer:

> Hibernate first-level cache is the persistence context’s internal identity map. It stores managed entities by entity type and primary key, ensuring that within one persistence context the same database row is represented by the same Java object.

### Q2. Is first-level cache shared across transactions?

Strong answer:

> No. It is scoped to a persistence context. In typical Spring transactional usage, each transaction has its own persistence context, so first-level cache is not shared across requests or transactions.

### Q3. What is the main purpose of first-level cache?

Strong answer:

> The main purpose is identity consistency and change tracking. Performance improvement from avoiding repeated find-by-id SQL is useful, but the deeper reason is that Hibernate needs one managed object per row to perform dirty checking safely.

### Q4. Does first-level cache prevent all repeated SQL?

Strong answer:

> No. EntityManager.find by id can be served from first-level cache. But JPQL or Criteria queries may still hit the database. When query results are materialized, Hibernate reuses existing managed entities from the persistence context.

### Q5. Why can first-level cache cause memory problems?

Strong answer:

> Because every managed entity remains in the persistence context until detached, cleared, or the context closes. Large batch jobs or huge transactions can accumulate many managed entities, increasing memory usage and dirty checking cost.

### Q6. How do you handle first-level cache in batch processing?

Strong answer:

> Process in chunks and periodically call flush and clear. Flush sends pending SQL to the database, and clear detaches entities so the persistence context does not grow without limit.

### Q7. Difference between first-level and second-level cache?

Strong answer:

> First-level cache is mandatory and scoped to one persistence context. It stores managed entity instances. Second-level cache is optional, shared across sessions, provider-backed, and stores cached entity data between persistence contexts.

---

## Production Checklist

```text
First-Level Cache Review
========================

Scope
[ ] Do I know where the persistence context starts and ends?
[ ] Is @Transactional placed at the service use-case boundary?
[ ] Is the transaction too long?

Identity
[ ] Am I relying on same object identity inside one transaction?
[ ] Do I understand that same row in another transaction is another object?

SQL Shape
[ ] Do repeated findById calls behave as expected?
[ ] Do JPQL queries still execute SQL?
[ ] Are list endpoints accidentally loading entities?

Memory
[ ] Does this use case load many entities?
[ ] Does a batch job flush and clear?
[ ] Are huge object graphs avoided?

Freshness
[ ] Am I seeing cached managed state instead of fresh DB state?
[ ] Do I need refresh, shorter transaction, or different isolation design?

Debugging
[ ] SQL logs checked in lower environment?
[ ] Persistence context size considered?
[ ] Query count measured?
```

---

## One-Page Cheat Sheet

```text
First-Level Cache Mental Model
==============================

Definition
----------
First-level cache is the persistence context identity map.

Core Rule
---------
Same entity type + same primary key + same persistence context
= same Java object reference.

Stored As
---------
EntityKey(Product, 1001) -> Product@abc

Scope
-----
One persistence context.
Usually one transaction in Spring service methods.
Not global.
Not shared.
Not Redis.

Main Purpose
------------
Identity consistency + dirty checking.
Performance is secondary.

Find by ID
----------
Can return from first-level cache without SQL.

JPQL Query
----------
May still hit DB, but result identity is resolved through persistence context.

clear()
-------
Detaches all managed entities.
Flush before clear if you need pending changes saved.

Batch Rule
----------
flush + clear periodically.

Debug Rules
-----------
Same object unexpectedly? Same persistence context.
No second SELECT? First-level cache hit.
Memory growing? Too many managed entities.
Stale inside transaction? Already managed entity returned.
Changes lost after clear? Detached before flush.
```

---

## Last-Minute Interview Revision

Do not say:

```text
Hibernate first-level cache is a cache that improves performance.
```

Say:

```text
Hibernate first-level cache is the persistence context identity map. It guarantees that within one persistence context, one database row maps to one Java object. This supports identity consistency and dirty checking.
```

Senior version:

```text
I treat the first-level cache as a unit-of-work memory map. It is useful for consistency inside a transaction, but I control its size in batch jobs and avoid loading huge read-only result sets as managed entities.
```

---

## One Picture To Remember

```text
                 ONE @Transactional USE CASE
                              |
                              v
+----------------------------------------------------------------+
|                    Persistence Context                          |
|----------------------------------------------------------------|
|                                                                |
|  First-Level Cache / Identity Map                               |
|                                                                |
|  Product#1001  ---------------------> Product@abc               |
|                                        stock=8                  |
|                                        name=Gaming Keyboard     |
|                                                                |
|  Product#1001 requested again -------- returns Product@abc       |
|                                                                |
|  Snapshot                                                       |
|  Product#1001 original: stock=10, name=Keyboard                  |
|                                                                |
+----------------------------------------------------------------+
                              |
                              | flush dirty managed object
                              v
                 UPDATE products SET stock=8, name=? WHERE id=1001
                              |
                              v
                          DATABASE
```

Final retention sentence:

> **First-level cache means Hibernate keeps one managed Java object per database row inside one persistence context, so identity and dirty checking stay consistent.**
