# 014_JPA_Hibernate_Mental_Model — The Object–Row Synchronization Engine

## Core Mental Model

Do not imagine JPA/Hibernate as “magic SQL hidden behind repositories”.
That model is too vague and causes production bugs.

The better mental model is:

> **Hibernate is a synchronization engine between Java objects in memory and database rows on disk.**

```text
Java Heap Objects                         Database Rows
+-------------------+                     +-------------------+
| Order object      |  <---sync engine--->| orders table row   |
| Product object    |                     | products row       |
| Customer object   |                     | customers row      |
+-------------------+                     +-------------------+
        ^                                          ^
        |                                          |
        |              Hibernate/JPA               |
        +------------------------------------------+
```

This chapter teaches exactly one idea:

> **JPA/Hibernate tracks entity objects inside a persistence context, compares changes, and synchronizes them to the database at flush/commit time.**

If you remember only one sentence:

> **Hibernate does not simply “save objects”; it manages object identity, tracks changes, and writes SQL when the persistence context flushes.**

---

## Why This Exists

Java developers think in objects:

```java
product.decreaseStock(2);
order.markPaid();
customer.changeAddress(newAddress);
```

Relational databases think in rows:

```sql
UPDATE products SET stock = stock - 2 WHERE id = 1001;
UPDATE orders SET status = 'PAID' WHERE id = 501;
UPDATE customers SET address = '...' WHERE id = 42;
```

These are different worlds.

```text
Java World                         SQL World
----------                         ---------
Objects                            Rows
References                         Foreign keys
Collections                        Join tables
Methods                            Queries
Object identity                    Primary keys
Inheritance                        Tables/columns
```

JPA exists to define a standard way to map between these two worlds.
Hibernate is the most common implementation that does the actual runtime work.

Without a tool like Hibernate, service code becomes full of manual SQL mapping:

```java
Product product = jdbcTemplate.queryForObject(
    "select id, name, stock from products where id = ?",
    mapper,
    productId
);

product.decreaseStock(quantity);

jdbcTemplate.update(
    "update products set stock = ? where id = ?",
    product.getStock(),
    product.getId()
);
```

This is clear, but repetitive.
JPA/Hibernate tries to let you work with objects while still persisting rows.

The danger is that hiding SQL does not remove SQL.
It only delays when you notice it.

---

## Problem Statement

A Spring Boot application needs to perform this use case:

```text
Create order
  1. Load product
  2. Check stock
  3. Decrease stock
  4. Create order
  5. Commit everything atomically
```

Business code wants to say:

```java
Product product = productRepository.findById(productId).orElseThrow();
product.decreaseStock(quantity);
orderRepository.save(order);
```

But the database needs SQL:

```sql
SELECT * FROM products WHERE id = ?;
UPDATE products SET stock = ? WHERE id = ?;
INSERT INTO orders (...);
```

The core problem:

> **How can Java object changes become correct SQL changes without manually writing SQL for every use case?**

Hibernate solves this with five key runtime ideas:

```text
1. Entity mapping       -> Which class maps to which table?
2. EntityManager       -> API for persistence operations
3. Persistence Context -> First-level cache + identity map
4. Dirty Checking      -> Detect changed managed entities
5. Flush               -> Convert detected changes into SQL
```

The mental model is not “repository saves everything”.
The real model is:

```text
Repository -> EntityManager -> Persistence Context -> Hibernate -> SQL -> Database
```

---

## Real World Analogy

Imagine a hotel front desk ledger.

```text
Guest asks to change room
Front desk updates working ledger
At end of shift, ledger is synchronized with central system
```

The front desk working ledger is not the final database.
It is the managed session state.

```text
Hotel analogy                  Hibernate/JPA
-------------                  -------------
Guest record                   Entity object
Working ledger                 Persistence Context
Front desk clerk               EntityManager
End-of-shift sync              Flush
Central reservation database   Database
```

Important lesson:

> If a guest record is inside the working ledger, changes are tracked. If the record is outside the ledger, changes are invisible.

That is exactly Hibernate.

```text
Managed entity   -> Hibernate tracks changes
Detached entity  -> Hibernate does not automatically track changes
New entity       -> needs persist/save to become managed
Removed entity   -> scheduled for delete
```

---

## The One Mental Model

Think of every transaction as a temporary object workbench.

```text
@Transactional service method starts
        |
        v
+--------------------------------------------+
| Persistence Context                         |
|--------------------------------------------|
| Identity map: id -> entity object           |
| Snapshot: original loaded values            |
| Change tracker: what became dirty?          |
| SQL action queue: insert/update/delete      |
+--------------------------------------------+
        |
        v
@Transactional service method ends
        |
        v
Hibernate flushes changes as SQL
        |
        v
Database commit
```

The persistence context answers three questions:

```text
1. Have I already loaded this row in this transaction?
2. Which object represents this row?
3. Has this object changed compared to its original snapshot?
```

That is the heart of JPA/Hibernate.

---

## Core Concepts

### Entity

An entity is a Java object mapped to a database table.

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

    public void decreaseStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        if (stock < quantity) throw new IllegalStateException("not enough stock");
        this.stock -= quantity;
    }
}
```

`@Entity` does not mean “this object is always in the database”.
It means Hibernate knows how to map this class to a table.

### EntityManager

The `EntityManager` is the main JPA API.
Spring Data repositories use it internally.

```text
JpaRepository
    |
    v
EntityManager
    |
    v
Persistence Context
    |
    v
Hibernate Session
    |
    v
Database
```

Common operations:

```text
find     -> load row as managed entity
persist  -> make new entity managed and schedule insert
merge    -> copy detached state into managed entity
remove   -> schedule delete
flush    -> synchronize changes to database
```

### Persistence Context

The persistence context is the most important concept.

It is:

```text
- first-level cache
- identity map
- change tracking area
- unit-of-work memory
```

Inside one persistence context:

```java
Product p1 = entityManager.find(Product.class, 1001L);
Product p2 = entityManager.find(Product.class, 1001L);

System.out.println(p1 == p2); // true
```

Same database row.
Same Java object.

That prevents two in-memory versions of the same row from fighting each other inside the same transaction.

### Dirty Checking

Dirty checking means Hibernate remembers the original loaded state and compares it with the current object state.

```text
Loaded snapshot:
Product{id=1001, stock=10}

After business method:
Product{id=1001, stock=8}

Hibernate sees:
stock changed from 10 to 8

SQL generated:
UPDATE products SET stock = 8 WHERE id = 1001;
```

You do not always need to call `save()` after modifying a managed entity.
If the entity is managed inside a transaction, Hibernate can detect the change during flush.

### Flush

Flush is synchronization.

```text
Persistence Context changes -> SQL statements sent to database
```

Flush can happen:

```text
- before transaction commit
- before some queries
- when entityManager.flush() is called manually
```

Commit and flush are related but not identical.

```text
flush  -> send SQL to database
commit -> make transaction permanent
```

If flush sends SQL but transaction later rolls back, changes are not committed.

---

## Internal Architecture

```text
Spring Boot Application
        |
        v
+-----------------------+
| Service Layer         |
| @Transactional        |
+-----------------------+
        |
        v
+-----------------------+
| Spring Data Repository|
| JpaRepository proxy   |
+-----------------------+
        |
        v
+-----------------------+
| EntityManager         |
| JPA API               |
+-----------------------+
        |
        v
+-----------------------+
| Persistence Context   |
| first-level cache     |
| dirty checking        |
+-----------------------+
        |
        v
+-----------------------+
| Hibernate Engine      |
| SQL generation        |
| flush/action queue    |
+-----------------------+
        |
        v
+-----------------------+
| JDBC Connection       |
+-----------------------+
        |
        v
+-----------------------+
| Database              |
+-----------------------+
```

The repository is not the magic part.
The persistence context is the magic part.

---

## Internal Working

When this service method runs:

```java
@Transactional
public void buyProduct(Long productId, int quantity) {
    Product product = productRepository.findById(productId).orElseThrow();
    product.decreaseStock(quantity);
}
```

What you see:

```text
load product
change Java field
method ends
```

What Hibernate does:

```text
1. Transaction starts.
2. Persistence context opens/binds to transaction.
3. Repository delegates to EntityManager.
4. EntityManager checks persistence context for Product#1001.
5. Not found, so Hibernate sends SELECT.
6. Row is converted into Product object.
7. Product object is stored in persistence context.
8. Original snapshot is stored.
9. Business method changes stock field.
10. Method ends.
11. Transaction interceptor triggers commit.
12. Hibernate flushes persistence context.
13. Dirty checking compares object with snapshot.
14. UPDATE SQL is generated.
15. Database transaction commits.
16. Persistence context closes.
```

Diagram:

```text
@Transactional begin
        |
        v
Repository.findById(1001)
        |
        v
Persistence Context lookup
        |
        +-- found? return same object
        |
        +-- not found
              |
              v
          SELECT row
              |
              v
          create Product object
              |
              v
          store object + snapshot
              |
              v
Business changes object
        |
        v
Flush at commit
        |
        v
Compare snapshot vs current
        |
        v
Generate UPDATE
```

---

## Step-by-Step Dry Run

Database before:

```text
products
+------+----------+-------+
| id   | name     | stock |
+------+----------+-------+
| 1001 | Keyboard | 10    |
+------+----------+-------+
```

Service code:

```java
@Transactional
public void reserveStock(Long productId, int quantity) {
    Product product = productRepository.findById(productId).orElseThrow();
    product.decreaseStock(quantity);
}
```

Dry run:

```text
1. Client requests reserve 2 keyboards.
2. Service method enters through Spring transactional proxy.
3. Database transaction begins.
4. Persistence context starts empty.

Persistence Context:
+----------------+------------------+------------------+
| Entity Key     | Object Reference | Snapshot          |
+----------------+------------------+------------------+
| empty          | empty            | empty             |
+----------------+------------------+------------------+

5. productRepository.findById(1001) executes.
6. Hibernate sends SELECT.
7. Row becomes Product object.
8. Object and snapshot are registered.

Persistence Context:
+----------------+------------------+------------------+
| Product#1001   | Product@abc      | stock = 10        |
+----------------+------------------+------------------+

9. product.decreaseStock(2) changes Java field.

Current object:
Product@abc stock = 8

Snapshot still:
stock = 10

10. Method returns.
11. Hibernate flush begins.
12. Dirty checking detects stock changed.
13. Hibernate sends:

UPDATE products SET stock = 8 WHERE id = 1001;

14. Database commits.
15. Persistence context closes.
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

> The update happened not because you called `save`, but because the managed entity became dirty inside a transaction.

---

## Deep Walkthrough Example

### Entities

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    protected Order() {}

    public static Order create(Product product, int quantity) {
        Order order = new Order();
        order.product = product;
        order.quantity = quantity;
        order.status = OrderStatus.CREATED;
        return order;
    }
}
```

```java
public enum OrderStatus {
    CREATED, CANCELLED
}
```

### Service

```java
@Service
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository,
                        OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Long createOrder(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));

        product.decreaseStock(quantity);

        Order order = Order.create(product, quantity);
        Order saved = orderRepository.save(order);

        return saved.getId();
    }
}
```

### Internal Execution

```text
findById(productId)
  -> SELECT product
  -> product becomes managed

product.decreaseStock(quantity)
  -> changes managed entity field
  -> no SQL immediately required

orderRepository.save(order)
  -> new order becomes managed
  -> INSERT may happen immediately or at flush depending on ID strategy

commit
  -> dirty product detected
  -> UPDATE product
  -> INSERT order if not already inserted
  -> commit
```

Possible SQL:

```sql
select p.id, p.name, p.stock from products p where p.id = ?;
insert into orders (product_id, quantity, status) values (?, ?, ?);
update products set stock = ? where id = ?;
```

Do not memorize exact SQL ordering blindly.
Hibernate may order actions depending on ID strategy, batching, constraints, and flush timing.
Remember the model:

```text
managed changes are collected, then synchronized.
```

---

## Entity Lifecycle States

```text
                 persist/save
NEW / TRANSIENT  ----------->  MANAGED
     |                          |
     |                          | transaction ends / clear / close
     |                          v
     +--------------------->  DETACHED
                                |
                                | merge
                                v
                              MANAGED

MANAGED -- remove --> REMOVED -- flush --> DELETE SQL
```

### Transient/New

```java
Product p = new Product("Mouse", 10);
```

Hibernate does not know this object yet.
No insert will happen unless it becomes managed.

### Managed

```java
Product p = productRepository.findById(1L).orElseThrow();
p.decreaseStock(1);
```

Hibernate tracks this object.
Dirty checking applies.

### Detached

```java
Product p = productRepository.findById(1L).orElseThrow();
// transaction ends
p.decreaseStock(1); // Hibernate is no longer tracking this object
```

A detached object is just a normal Java object.
Changing it does not automatically update the database.

### Removed

```java
entityManager.remove(product);
```

The object is scheduled for delete.
SQL happens at flush.

---

## Spring Boot Example

```java
@RestController
@RequestMapping("/api/products")
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

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

Important:

```text
Controller does not know Hibernate.
Service owns transaction.
Repository loads entity.
Persistence context tracks entity.
Hibernate flushes SQL.
```

This connects directly to your previous Controller-Service-Repository model: the service is the correct place for the transaction boundary, and the repository is the persistence doorway.

---

## Rich ASCII Diagram — The Hibernate Workbench

```text
                  @Transactional Service Method
                              |
                              v
+----------------------------------------------------------------+
|                    Persistence Context                          |
|----------------------------------------------------------------|
|                                                                |
|  Identity Map                                                   |
|  +----------------+------------------+                         |
|  | Product#1001   | Product@abc      |                         |
|  +----------------+------------------+                         |
|                                                                |
|  Snapshots                                                      |
|  +----------------+------------------+                         |
|  | Product#1001   | stock=10         |                         |
|  +----------------+------------------+                         |
|                                                                |
|  Current Objects                                                |
|  +----------------+------------------+                         |
|  | Product@abc    | stock=8          |                         |
|  +----------------+------------------+                         |
|                                                                |
|  Dirty Checking Result                                          |
|  Product#1001 stock changed: 10 -> 8                            |
|                                                                |
+----------------------------------------------------------------+
                              |
                              v
                       Flush generates SQL
                              |
                              v
          UPDATE products SET stock = 8 WHERE id = 1001;
```

---

## Sequence Diagram

```text
Client
  |
  | POST /api/products/1001/reserve?quantity=2
  v
Controller
  |
  | productService.reserve(1001, 2)
  v
Service Proxy
  |
  | begin transaction
  v
ProductService
  |
  | productRepository.findById(1001)
  v
JpaRepository Proxy
  |
  | entityManager.find(Product.class, 1001)
  v
Persistence Context
  |
  | not found
  v
Hibernate
  |
  | SELECT ... WHERE id = 1001
  v
Database
  |
  | row returned
  v
Hibernate
  |
  | create Product object + snapshot
  v
ProductService
  |
  | product.decreaseStock(2)
  v
Service Proxy
  |
  | commit requested
  v
Hibernate
  |
  | dirty check + flush UPDATE
  v
Database
  |
  | commit
  v
Client receives 204
```

---

## Multiple Dry Runs

### Dry Run 1 — Managed Entity Update

```java
@Transactional
public void rename(Long id, String name) {
    Product p = productRepository.findById(id).orElseThrow();
    p.rename(name);
}
```

Result:

```text
Product loaded -> managed
Field changed -> dirty
Commit -> UPDATE generated
```

No explicit `save()` required for the managed object.

### Dry Run 2 — Detached Entity Update Fails Silently

```java
Product p = productRepository.findById(id).orElseThrow();
// transaction ends
p.rename("New Name");
```

Result:

```text
Product was managed only during transaction.
After transaction, it becomes detached.
Changing it later does not update database.
```

### Dry Run 3 — Lazy Loading Failure

```java
@Transactional
public Order getOrder(Long id) {
    return orderRepository.findById(id).orElseThrow();
}

// later outside transaction
order.getProduct().getName();
```

Possible result:

```text
LazyInitializationException
```

Why?

```text
product was lazy proxy
persistence context closed
Hibernate cannot load product anymore
```

### Dry Run 4 — N+1 Query

```java
List<Order> orders = orderRepository.findAll();
for (Order order : orders) {
    System.out.println(order.getProduct().getName());
}
```

Possible SQL:

```text
1 query to load orders
N extra queries to load products
```

Fix intentionally:

```java
@Query("select o from Order o join fetch o.product")
List<Order> findAllWithProduct();
```

Mental model:

```text
Lazy loading is not bad.
Unplanned lazy loading is bad.
```

---

## Production Scale Example

Imagine an ecommerce service at 5,000 RPS.

```text
API Gateway
   |
   v
Order Service
   |
   +-- ProductRepository.findById()
   +-- product.decreaseStock()
   +-- OrderRepository.save()
   +-- Hibernate flush
   |
   v
Postgres
```

At small scale, Hibernate mistakes are invisible.
At production scale, they become database load.

Common production issues:

```text
N+1 queries
  One endpoint accidentally sends 501 SQL queries per request.

Long transactions
  Service holds DB connection while calling external API.

Over-fetching
  Loading entire object graphs when only 5 columns are needed.

Unbounded persistence context
  Batch job loads 500k entities into memory.

Lock contention
  Many transactions update the same product row.
```

Senior engineer thinking:

```text
Hibernate is convenient for transactional domain changes.
Hibernate is dangerous when you forget SQL still exists.
```

---

## Production Failure Story

A team built an order listing endpoint:

```java
@GetMapping("/orders")
public List<OrderResponse> list() {
    return orderService.listOrders();
}
```

Service:

```java
@Transactional(readOnly = true)
public List<OrderResponse> listOrders() {
    return orderRepository.findAll()
            .stream()
            .map(order -> new OrderResponse(
                    order.getId(),
                    order.getProduct().getName(),
                    order.getCustomer().getName()))
            .toList();
}
```

It worked in testing with 10 orders.
In production with 1,000 orders, p99 latency exploded.

Root cause:

```text
findAll() loaded orders.
order.getProduct() triggered lazy query per order.
order.getCustomer() triggered lazy query per order.

1 + 1000 + 1000 SQL queries.
```

The endpoint looked clean.
But Hibernate was quietly doing too much SQL.

Fix:

```java
@Query("""
    select new com.example.OrderResponse(o.id, p.name, c.name)
    from Order o
    join o.product p
    join o.customer c
""")
List<OrderResponse> findOrderResponses();
```

Lesson:

> **JPA is not a replacement for query thinking. It is an object synchronization engine, and you must still design SQL access deliberately.**

---

## Debugging Mindset

When debugging Hibernate, ask these questions:

```text
1. Is this entity managed or detached?
2. Is there an active transaction?
3. When does flush happen?
4. How many SQL queries are executed?
5. Are relationships lazy or eager?
6. Is dirty checking updating more than expected?
7. Is the persistence context growing too large?
```

### Symptom Map

```text
Entity change not saved
  -> Was entity managed?
  -> Was there a transaction?
  -> Did rollback happen?

LazyInitializationException
  -> Lazy relation accessed after persistence context closed
  -> Fetch intentionally or map DTO inside transaction

N+1 queries
  -> Lazy relation accessed in loop
  -> Use join fetch, entity graph, projection, or batch fetching

Slow commit
  -> Flush has many dirty entities
  -> Check SQL logs and action queue

Memory spike in batch job
  -> Persistence context holds too many managed entities
  -> flush and clear periodically
```

### Useful Logging

```properties
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
spring.jpa.properties.hibernate.generate_statistics=true
```

Use these carefully in production because SQL/bind logging can be expensive and may expose sensitive data.

---

## Common Misconceptions

### Misconception 1: “JPA means no SQL knowledge needed”

Wrong.

JPA hides SQL generation but does not remove database behavior.
You still need indexes, joins, transaction isolation, locking, query plans, and batching.

### Misconception 2: “save() always immediately updates the database”

Not always.

`save()` makes an entity managed or delegates merge/persist behavior.
Actual SQL synchronization happens during flush. Some ID strategies may force earlier insert, but the core model is still persistence context then flush.

### Misconception 3: “Changing any entity object updates the DB”

Only managed entities are tracked.
Detached objects are normal Java objects.

### Misconception 4: “EAGER fetch is safer than LAZY fetch”

EAGER often creates worse performance because it loads data even when not needed.
Default to intentional fetching per use case.

### Misconception 5: “Repository is the transaction boundary”

Usually no.
Repository methods may be transactional internally, but business transaction boundaries belong at the service use-case level.

### Misconception 6: “Hibernate bugs are random”

Most Hibernate bugs are mental model bugs:

```text
wrong lifecycle state
wrong transaction boundary
wrong fetch plan
wrong flush assumption
wrong ownership of query shape
```

---

## Java Code Example With Execution Explanation

```java
@Transactional
public void changeProductName(Long id, String newName) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("not found"));

    product.rename(newName);
}
```

Execution:

```text
1. Transaction starts.
2. Product loaded from database.
3. Product becomes managed.
4. Hibernate stores original snapshot.
5. rename changes object field.
6. Method exits.
7. Flush compares snapshot and current state.
8. Hibernate sends UPDATE if changed.
9. Transaction commits.
```

The important line is not visible in code:

```text
Product is managed inside persistence context.
```

That invisible state is what makes dirty checking work.

---

## Performance Considerations

Use JPA/Hibernate for:

```text
- normal CRUD
- transactional aggregate updates
- domain model persistence
- moderate object graphs
- business use cases where object state matters
```

Be careful with JPA/Hibernate for:

```text
- huge analytical queries
- large batch processing
- high-volume writes
- reporting endpoints
- complex joins returning read-only views
```

For read-heavy endpoints, consider projections:

```java
public record ProductCard(Long id, String name, int stock) {}

@Query("select new com.example.ProductCard(p.id, p.name, p.stock) from Product p")
List<ProductCard> findProductCards();
```

This avoids loading full managed entities when you only need a response DTO.

For batch jobs:

```java
for (int i = 0; i < products.size(); i++) {
    entityManager.persist(products.get(i));

    if (i % 50 == 0) {
        entityManager.flush();
        entityManager.clear();
    }
}
```

Why?

```text
flush -> send SQL
clear -> detach managed entities and free persistence context memory
```

---

## Scalability Considerations

At scale, think in layers:

```text
Application layer:
  - transaction length
  - number of repository calls per request
  - DTO mapping behavior

Hibernate layer:
  - persistence context size
  - dirty checking cost
  - fetch plan
  - batch size
  - second-level cache usage if enabled

Database layer:
  - indexes
  - locks
  - connection pool
  - query plans
  - isolation level
```

Senior rule:

> **Every endpoint should have an expected SQL shape.**

For example:

```text
Create order endpoint:
  expected SQL: select product, insert order, update stock

List order cards endpoint:
  expected SQL: one projection query with joins

Bad endpoint:
  expected SQL: unknown
```

Unknown SQL shape becomes production surprise.

---

## Failure Investigation Playbook

### Step 1 — Turn on SQL visibility in lower environment

Look for:

```text
number of queries
unexpected joins
unexpected updates
query parameters
flush timing
```

### Step 2 — Identify entity state

Ask:

```text
Was the object loaded inside this transaction?
Was it returned and used later?
Was merge used unnecessarily?
```

### Step 3 — Check transaction boundary

Ask:

```text
Is @Transactional on service public method?
Is method called through Spring proxy?
Is self-invocation bypassing transaction?
Is exception causing rollback?
```

### Step 4 — Check fetch plan

Ask:

```text
Which relations are lazy?
Which relations are eager?
Is DTO mapping triggering lazy loads?
Should this use join fetch or projection?
```

### Step 5 — Check database reality

Ask:

```text
Are indexes correct?
Are locks blocking?
Is connection pool exhausted?
Is isolation level causing waits?
```

---

## Interview Q&A

### Q1. What is JPA and what is Hibernate?

Strong answer:

> JPA is the Java specification for object-relational persistence. Hibernate is an implementation of that specification. In practice, Hibernate maps entities to tables, manages a persistence context, tracks changes to managed entities, and flushes SQL to the database.

### Q2. What is the persistence context?

Strong answer:

> The persistence context is a first-level cache and identity map managed by the EntityManager. It stores managed entities and their snapshots during a transaction. Hibernate uses it for identity consistency and dirty checking.

### Q3. What is dirty checking?

Strong answer:

> Dirty checking is Hibernate’s process of comparing the current state of managed entities with their original snapshots. If a managed entity changed, Hibernate generates the necessary SQL during flush.

### Q4. Do we need to call save after modifying an entity?

Strong answer:

> If the entity is managed inside an active transaction, usually no. Hibernate will detect changes and flush them. If the entity is detached or new, then it must be persisted, merged, or saved appropriately.

### Q5. What is LazyInitializationException?

Strong answer:

> It happens when code accesses a lazy association after the persistence context/session is closed. Hibernate no longer has an active context to load the relation. The fix is to design the fetch plan intentionally, often with join fetch, projections, or DTO mapping inside the transaction.

### Q6. What causes N+1 queries?

Strong answer:

> N+1 happens when one query loads parent entities and then accessing a lazy child relation triggers one additional query per parent. It is fixed by intentional fetching: join fetch, entity graphs, projections, or batch fetching depending on the use case.

### Q7. Where should `@Transactional` be placed?

Strong answer:

> Usually on service methods because the service represents a complete business unit of work. The transaction should cover the full use case, not just individual repository calls.

---

## Production Checklist

```text
Entity Mapping
[ ] Are entity relationships intentional?
[ ] Are large relations LAZY by default?
[ ] Are equals/hashCode safe for entities?
[ ] Are enum fields stored as STRING, not ORDINAL?

Transaction Boundary
[ ] Is @Transactional on service use case?
[ ] Is the method called through Spring proxy?
[ ] Are external API calls avoided inside DB transaction?
[ ] Is rollback behavior understood?

Query Shape
[ ] Do we know expected SQL per endpoint?
[ ] Are list endpoints using projection when appropriate?
[ ] Are N+1 risks tested?
[ ] Are indexes aligned with queries?

Persistence Context
[ ] Are batch jobs flushing and clearing?
[ ] Are detached entities handled carefully?
[ ] Is merge avoided unless needed?
[ ] Is dirty checking understood?

Production Debugging
[ ] Can SQL be inspected in lower environment?
[ ] Are slow queries observable?
[ ] Are connection pool metrics monitored?
[ ] Are lock waits visible?
```

---

## One-Page Cheat Sheet

```text
JPA/Hibernate Mental Model
==========================

JPA
  Specification for ORM persistence.

Hibernate
  Runtime implementation that maps objects to rows.

Entity
  Java object mapped to table.

EntityManager
  API that manages entity persistence.

Persistence Context
  First-level cache + identity map + dirty checking area.

Managed Entity
  Object tracked by Hibernate.

Detached Entity
  Object no longer tracked.

Dirty Checking
  Compare original snapshot with current object state.

Flush
  Synchronize persistence context changes to database SQL.

Commit
  Make database transaction permanent.

Best Sentence
-------------
Hibernate is an object-row synchronization engine.
It tracks managed entities and writes SQL at flush time.

Debugging Rules
---------------
Change not saved?       Check managed/detached + transaction.
Too many queries?       Check lazy loading/N+1.
Lazy exception?         Accessed relation after context closed.
Slow commit?            Flush/dirty checking/action queue.
Memory spike?           Persistence context too large.
```

---

## Last-Minute Interview Revision

Do not say:

```text
Hibernate saves Java objects into database automatically.
```

Say:

```text
Hibernate manages entities inside a persistence context. It tracks their original and current state, then flushes SQL changes to the database inside a transaction.
```

Senior version:

```text
JPA/Hibernate is convenient when I treat it as a unit-of-work synchronization engine.
I still design transaction boundaries, fetch plans, query shape, and database indexes deliberately.
```

---

## One Picture To Remember

```text
                  SERVICE METHOD @Transactional
                              |
                              v
                 +--------------------------+
                 |   Persistence Context    |
                 |--------------------------|
                 | Product#1001 -> Obj@abc  |
                 | Snapshot: stock=10       |
                 | Current:  stock=8        |
                 +--------------------------+
                              |
                              | flush
                              v
                 +--------------------------+
                 | Hibernate SQL Generator  |
                 +--------------------------+
                              |
                              v
                 UPDATE products SET stock=8
                              |
                              v
                          DATABASE
```

Final retention sentence:

> **JPA/Hibernate is not magic persistence; it is a managed object workbench that synchronizes dirty Java entities into SQL rows at flush time.**
