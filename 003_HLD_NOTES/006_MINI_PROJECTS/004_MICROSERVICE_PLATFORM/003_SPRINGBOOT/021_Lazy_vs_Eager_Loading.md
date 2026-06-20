# 021_Lazy_vs_Eager_Loading — The Database Boundary Timing Model

## Core Mental Model

Do not think of Lazy and Eager loading as:

```text
LAZY  = good
EAGER = bad
```

That is too shallow.

The better mental model is:

> **Lazy vs Eager loading decides WHEN Hibernate crosses the database boundary to load related data.**

```text
Order object
   |
   +-- product
   |
   +-- customer
   |
   +-- items

Question:
Should Hibernate load these related objects NOW or LATER?
```

```text
EAGER loading
-------------
Load relationship immediately with parent.

LAZY loading
------------
Load relationship only when code touches it.
```

If you remember only one sentence:

> **Lazy and Eager are fetch timing strategies; the real skill is choosing the right fetch plan for each use case.**

---

## Why This Exists

Object-oriented code naturally walks relationships:

```java
Order order = orderRepository.findById(orderId).orElseThrow();
String productName = order.getProduct().getName();
String customerName = order.getCustomer().getName();
```

In Java, this looks like simple object navigation.

But in a relational database, relationships are not already inside the object.
They live behind foreign keys and joins.

```text
orders table
+----+-------------+-------------+
| id | product_id  | customer_id |
+----+-------------+-------------+
| 10 | 1001        | 501         |
+----+-------------+-------------+

products table
+------+----------+
| id   | name     |
+------+----------+
| 1001 | Keyboard |
+------+----------+

customers table
+-----+---------+
| id  | name    |
+-----+---------+
| 501 | Mohamed |
+-----+---------+
```

To get `order.getProduct().getName()`, Hibernate may need another SQL query.

That is the hidden boundary:

```text
Java field access may become database access.
```

Lazy and Eager loading exist because Hibernate must answer this question:

> **When should related rows be loaded into memory?**

---

## Problem Statement

Imagine this endpoint:

```text
GET /api/orders/10
```

The response needs:

```json
{
  "orderId": 10,
  "productName": "Keyboard",
  "customerName": "Mohamed"
}
```

The service loads the order:

```java
Order order = orderRepository.findById(10L).orElseThrow();
```

But the response needs related data:

```java
order.getProduct().getName();
order.getCustomer().getName();
```

Hibernate has two broad choices.

### Choice 1 — Load relationships immediately

```text
Load order + product + customer now.
```

This avoids later surprise queries, but may load data you do not need.

### Choice 2 — Load relationships later

```text
Load order now.
Load product/customer only if code touches them.
```

This avoids unnecessary loading, but may create N+1 queries or LazyInitializationException.

So the problem is not simply performance.

The real problem is:

> **How do we control when relationship data crosses from database into Java memory?**

---

## Real World Analogy

Imagine ordering files from an archive room.

```text
You ask for Order File #10.
```

The archive clerk can do two things.

### Eager style

```text
Clerk brings:
- order file
- product file
- customer file
- invoice file
- shipment file

Even if you only needed the order number.
```

### Lazy style

```text
Clerk brings only order file.
Later, when you ask for product details, clerk goes back to archive.
Later, when you ask for customer details, clerk goes back again.
```

Both can be correct.
Both can be terrible.

```text
Eager problem:
  Carries too many files upfront.

Lazy problem:
  Too many trips back to archive.
```

Senior-engineer model:

> **Do not choose based on emotion. Choose based on use case access pattern.**

---

## The One Mental Model

Think of a relationship field as a database door.

```java
order.getProduct()
```

This line may mean:

```text
Door already open -> return object from persistence context
Door closed       -> Hibernate may execute SQL
Door locked       -> LazyInitializationException
```

```text
                    Relationship Field
                            |
                            v
              +-----------------------------+
              | Is related object loaded?   |
              +-----------------------------+
                    |                 |
                  yes                 no
                    |                 |
                    v                 v
              Return object     Is context open?
                                      |
                               +------+------+
                               |             |
                              yes            no
                               |             |
                               v             v
                         Run SQL load   LazyInitializationException
```

Lazy vs Eager is therefore a timing decision:

```text
EAGER -> cross DB boundary during parent load
LAZY  -> cross DB boundary when relationship is accessed
```

---

## Core Concepts

## Fetch Timing

Fetch timing means when Hibernate loads a relationship.

```text
Immediate loading -> EAGER
Delayed loading   -> LAZY
```

Example:

```java
@ManyToOne(fetch = FetchType.LAZY)
private Product product;
```

This says:

```text
When loading Order, do not fully load Product immediately.
Create a lazy reference/proxy.
Load Product only when needed.
```

Example:

```java
@ManyToOne(fetch = FetchType.EAGER)
private Product product;
```

This says:

```text
When loading Order, also load Product immediately.
```

## Lazy Proxy

For many relationships, Hibernate can place a proxy object instead of the real object.

```text
Order
  product -> ProductProxy(id=1001)
```

The proxy knows the ID, but not all fields yet.

When code calls:

```java
order.getProduct().getName();
```

Hibernate initializes the proxy:

```sql
select * from products where id = 1001;
```

## Persistence Context Requirement

Lazy loading requires an open persistence context.

```text
Lazy proxy + open persistence context   -> can load
Lazy proxy + closed persistence context -> exception
```

That is why this fails:

```java
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

## Fetch Plan

A fetch plan is the intentional decision of what data this use case needs.

```text
Endpoint: order detail page
Need: order + product + customer
Fetch plan: join fetch product and customer

Endpoint: order list page
Need: order id + status + date only
Fetch plan: projection, no full entities needed
```

The best mindset:

> **Default mappings are not enough. Each use case deserves an intentional fetch plan.**

---

## Internal Architecture

```text
Spring Service Method
        |
        v
Repository Query
        |
        v
Hibernate Loader
        |
        +----------------------------+
        |                            |
        v                            v
Load parent row               Decide relationship handling
        |                            |
        |                      LAZY: proxy/reference
        |                      EAGER: load now
        v                            |
Persistence Context <----------+-----+
        |
        v
Java entity graph returned
```

For lazy loading:

```text
Order loaded
  |
  v
Product not loaded yet
  |
  v
Product proxy placed in Order
  |
  v
Later product field accessed
  |
  v
Hibernate uses persistence context/session
  |
  v
SQL query loads product
```

For eager loading:

```text
Order loaded
  |
  v
Product loaded immediately
  |
  v
Order returned with Product already initialized
```

---

## Java Code Example

## Entities

```java
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    protected Order() {}

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Product getProduct() {
        return product;
    }

    public Customer getCustomer() {
        return customer;
    }
}
```

```java
@Entity
@Table(name = "products")
public class Product {

    @Id
    private Long id;

    private String name;

    protected Product() {}

    public String getName() {
        return name;
    }
}
```

```java
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    private Long id;

    private String name;

    protected Customer() {}

    public String getName() {
        return name;
    }
}
```

The important part:

```java
@ManyToOne(fetch = FetchType.LAZY)
```

This means related `Product` and `Customer` are not loaded until accessed.

---

## Spring Boot Example

## Bad Endpoint — Accidental Lazy Loading

```java
@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public OrderQueryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderCardResponse> listOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> new OrderCardResponse(
                        order.getId(),
                        order.getProduct().getName(),
                        order.getCustomer().getName(),
                        order.getStatus()
                ))
                .toList();
    }
}
```

This works.
But it may produce N+1 queries.

```text
1 query  -> load all orders
N queries -> load products
N queries -> load customers
```

Possible SQL pattern:

```sql
select * from orders;
select * from products where id = ?;
select * from customers where id = ?;
select * from products where id = ?;
select * from customers where id = ?;
...
```

## Better Endpoint — Use-Case Fetch Plan

```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        select o
        from Order o
        join fetch o.product
        join fetch o.customer
        where o.status = :status
    """)
    List<Order> findByStatusWithProductAndCustomer(String status);
}
```

Service:

```java
@Transactional(readOnly = true)
public List<OrderCardResponse> listCreatedOrders() {
    return orderRepository.findByStatusWithProductAndCustomer("CREATED")
            .stream()
            .map(order -> new OrderCardResponse(
                    order.getId(),
                    order.getProduct().getName(),
                    order.getCustomer().getName(),
                    order.getStatus()
            ))
            .toList();
}
```

Now the fetch plan matches the use case.

```text
Need product and customer names?
Load them intentionally.
```

## Best for Read-Only Listing — Projection

```java
public record OrderCardResponse(
        Long orderId,
        String productName,
        String customerName,
        String status
) {}
```

```java
@Query("""
    select new com.example.OrderCardResponse(
        o.id,
        p.name,
        c.name,
        o.status
    )
    from Order o
    join o.product p
    join o.customer c
    where o.status = :status
""")
List<OrderCardResponse> findOrderCards(String status);
```

For read-only screens, projection is often cleaner than loading full entities.

---

## Internal Working

When this runs:

```java
Order order = orderRepository.findById(10L).orElseThrow();
```

With lazy relationships:

```text
1. Hibernate selects order row.
2. Hibernate creates Order object.
3. Hibernate sees product_id = 1001.
4. Instead of loading full Product, it creates Product proxy/reference.
5. Hibernate sees customer_id = 501.
6. Instead of loading full Customer, it creates Customer proxy/reference.
7. Order is stored in persistence context.
8. Service receives Order.
```

Persistence context:

```text
+----------------+--------------------------+
| Entity Key     | Object                   |
+----------------+--------------------------+
| Order#10       | Order@aaa                |
| Product#1001   | ProductProxy@bbb         |
| Customer#501   | CustomerProxy@ccc        |
+----------------+--------------------------+
```

Later:

```java
order.getProduct().getName();
```

Hibernate checks:

```text
Is Product proxy initialized?
  no
Is persistence context open?
  yes
Run SELECT products WHERE id = 1001
Fill proxy with real values
Return name
```

If persistence context is closed:

```text
Is Product proxy initialized?
  no
Is persistence context open?
  no
Throw LazyInitializationException
```

---

## Rich ASCII Diagram — Lazy Loading

```text
@Transactional method starts
        |
        v
find Order#10
        |
        v
+-------------------------------------------+
| Persistence Context                        |
|-------------------------------------------|
| Order#10     -> Order@aaa                 |
| Product#1001 -> ProductProxy@bbb          |
| Customer#501 -> CustomerProxy@ccc         |
+-------------------------------------------+
        |
        v
Service code touches product name
        |
        v
order.getProduct().getName()
        |
        v
+-------------------------------------------+
| ProductProxy checks session/context        |
+-------------------------------------------+
        |
        v
SELECT * FROM products WHERE id = 1001
        |
        v
ProductProxy initialized
        |
        v
Return product name
```

---

## Rich ASCII Diagram — Eager Loading

```text
find Order#10
        |
        v
Hibernate sees EAGER relationship
        |
        v
Load relationship immediately
        |
        v
+-------------------------------------------+
| Persistence Context                        |
|-------------------------------------------|
| Order#10     -> Order@aaa                 |
| Product#1001 -> Product@bbb initialized   |
| Customer#501 -> Customer@ccc initialized  |
+-------------------------------------------+
        |
        v
Later access product name
        |
        v
No extra SQL needed
```

But eager may load data even when not used.

```text
Endpoint only needs order id/status
        |
        v
EAGER still loads product/customer
        |
        v
Wasted memory + SQL + joins/selects
```

---

## Step-by-Step Dry Run — Lazy Loading Works

Database:

```text
orders
+----+---------+------------+-------------+
| id | status  | product_id | customer_id |
+----+---------+------------+-------------+
| 10 | CREATED | 1001       | 501         |
+----+---------+------------+-------------+

products
+------+----------+
| id   | name     |
+------+----------+
| 1001 | Keyboard |
+------+----------+
```

Service:

```java
@Transactional(readOnly = true)
public String productName(Long orderId) {
    Order order = orderRepository.findById(orderId).orElseThrow();
    return order.getProduct().getName();
}
```

Flow:

```text
1. Transaction starts.
2. Persistence context opens.
3. Order is loaded.
4. Product is represented as lazy proxy.
5. Code calls order.getProduct().getName().
6. Proxy sees context is open.
7. Hibernate loads product row.
8. Product name is returned.
9. Transaction ends.
```

SQL:

```sql
select * from orders where id = 10;
select * from products where id = 1001;
```

This is fine for one order.

---

## Step-by-Step Dry Run — Lazy Loading Fails

Service:

```java
public Order findOrder(Long orderId) {
    return orderRepository.findById(orderId).orElseThrow();
}
```

Controller:

```java
@GetMapping("/orders/{id}")
public String productName(@PathVariable Long id) {
    Order order = orderService.findOrder(id);
    return order.getProduct().getName();
}
```

Flow:

```text
1. Repository loads Order.
2. Product is lazy proxy.
3. Repository method returns.
4. Persistence context closes.
5. Controller accesses product name.
6. Proxy tries to load product.
7. No open context/session.
8. LazyInitializationException.
```

Bug:

```text
Relationship was lazy.
Data was accessed outside persistence context.
```

Fix:

```text
Map DTO inside transaction using an intentional fetch plan.
```

---

## Step-by-Step Dry Run — Eager Loading Overfetches

Mapping:

```java
@ManyToOne(fetch = FetchType.EAGER)
private Product product;
```

Endpoint:

```java
@GetMapping("/orders/{id}/status")
public String status(@PathVariable Long id) {
    return orderService.status(id);
}
```

Service:

```java
@Transactional(readOnly = true)
public String status(Long id) {
    Order order = orderRepository.findById(id).orElseThrow();
    return order.getStatus();
}
```

The endpoint needs only:

```text
order.status
```

But eager relationship may load:

```text
order + product
```

Problem:

```text
Extra data loaded for no business reason.
```

At high traffic, overfetching becomes real cost.

---

## Production Scale Example

Imagine:

```text
GET /api/orders?page=0&size=100
```

Each order has:

```text
product
customer
payment
shipment
items
```

If everything is EAGER:

```text
One simple order list endpoint may load a huge object graph.
```

```text
Order list request
   |
   +-- 100 orders
       |
       +-- 100 products
       +-- 100 customers
       +-- 100 payments
       +-- 100 shipments
       +-- many order items
```

If everything is LAZY and mapping touches relationships in a loop:

```text
1 query for orders
+ 100 product queries
+ 100 customer queries
+ 100 payment queries
+ 100 shipment queries
```

Both are bad.

Correct production approach:

```text
For list page:
  Use projection query returning exactly fields needed.

For detail page:
  Use join fetch/entity graph for needed relationships.

For write use case:
  Load aggregate root and required relations only.
```

Senior rule:

> **Fetch strategy in entity mapping is a default. Fetch plan in repository query is the real production decision.**

---

## Production Failure Story

A team changed a mapping to fix `LazyInitializationException`:

```java
@ManyToOne(fetch = FetchType.EAGER)
private Customer customer;
```

The exception disappeared.

But after deployment, order listing latency increased from 150ms to 1.8s.
Database CPU spiked.

Root cause:

```text
Order list loaded 200 orders per request.
Each order eagerly loaded customer.
Customer had eager address.
Address had eager region.
The endpoint only needed order id and status.
```

The team fixed one bug by creating a hidden overfetching bug.

Correct fix:

```java
@Query("""
    select new com.example.OrderStatusRow(o.id, o.status)
    from Order o
    where o.createdAt >= :from
""")
List<OrderStatusRow> findOrderStatusRows(Instant from);
```

Lesson:

> **Do not solve lazy loading errors by blindly making relationships eager. Solve them by designing the fetch plan.**

---

## Debugging Mindset

When debugging lazy/eager problems, ask:

```text
1. What endpoint/use case is this?
2. What exact fields does it need?
3. Which relationships are accessed?
4. Are those relationships loaded intentionally or accidentally?
5. How many SQL queries happen?
6. Is access happening inside or outside transaction?
7. Is the mapping default controlling too much behavior?
```

## Symptom Map

```text
LazyInitializationException
  -> Lazy relation accessed after persistence context closed
  -> Map DTO inside transaction or fetch needed relation intentionally

N+1 queries
  -> Lazy relation accessed in loop
  -> join fetch, projection, entity graph, batch fetching

Slow endpoint with few lines of code
  -> Hidden eager loading or accidental lazy loads
  -> Inspect SQL count and query shape

Huge JSON response / recursion
  -> Returning entities directly with relationships
  -> Use DTOs

Database CPU spike after mapping change
  -> EAGER relationship caused overfetching
  -> Revert and use fetch plans
```

## Useful Logging

```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
spring.jpa.properties.hibernate.generate_statistics=true
```

In lower environments, count SQL queries per endpoint.

```text
Endpoint should have expected SQL shape.
Unexpected SQL = hidden fetch problem.
```

---

## Common Misconceptions

## Misconception 1 — “Lazy is always better”

No.
Lazy can create N+1 queries or fail outside the persistence context.

Lazy is good when you do not always need the relationship and you fetch intentionally when needed.

## Misconception 2 — “Eager fixes LazyInitializationException”

It may hide the exception, but often creates overfetching.

The real fix is not eager everywhere.
The real fix is use-case-specific fetching.

## Misconception 3 — “Field access is just Java memory access”

Not always.

```java
order.getProduct().getName();
```

This may trigger SQL.

## Misconception 4 — “Entity mapping should decide every query”

Mapping gives defaults.
Repository queries should define fetch plans for important use cases.

## Misconception 5 — “Returning entities from controllers is fine”

Dangerous.
It can trigger lazy loading during JSON serialization, expose internal fields, or create infinite recursion.

Use DTOs.

---

## Performance Considerations

## Prefer LAZY by default for large relationships

Especially:

```text
@OneToMany
@ManyToMany
large collections
rarely used relationships
```

## Avoid EAGER on collections

Eager collections can explode query size and memory.

```text
Order -> items -> product -> category
```

A simple load can become a huge graph.

## Use projections for read screens

If the endpoint only needs five fields, do not load full entities.

```java
@Query("""
    select new com.example.OrderCard(o.id, p.name, o.status)
    from Order o
    join o.product p
""")
List<OrderCard> findCards();
```

## Use join fetch for entity use cases

When you need entities and relationships together:

```java
@Query("""
    select o
    from Order o
    join fetch o.product
    where o.id = :id
""")
Optional<Order> findByIdWithProduct(Long id);
```

## Watch pagination with collection join fetch

Joining fetch collections with pagination can create duplicate rows and inefficient memory pagination.
Be careful with:

```text
@OneToMany join fetch + Pageable
```

Often better:

```text
1. Page parent IDs
2. Fetch needed details by IDs
```

---

## Scalability Considerations

At scale, lazy/eager affects:

```text
Database round trips
Query count
Payload size
Memory usage
Connection pool pressure
p99 latency
Lock duration
Serialization cost
```

Example:

```text
100 RPS endpoint
Each request accidentally executes 201 queries

Total SQL/sec = 20,100 queries/sec
```

A small mapping mistake becomes infrastructure pressure.

Senior production habit:

```text
For every endpoint, write expected SQL shape:

Order list:
  one projection query

Order detail:
  one join query for order + product + customer

Create order:
  select product, insert order, update stock
```

If you cannot describe the expected SQL shape, Hibernate will surprise you.

---

## Failure Investigation Playbook

## Step 1 — Reproduce with SQL logging

Check:

```text
How many queries?
Which relationships trigger queries?
Are queries repeated?
```

## Step 2 — Identify access location

Ask:

```text
Is relationship accessed inside service transaction?
Is it accessed during controller serialization?
Is it accessed after transaction closes?
```

## Step 3 — Define use-case fields

Write:

```text
This endpoint needs:
- order id
- product name
- customer name
- status
```

Then choose fetch plan.

## Step 4 — Choose fix

```text
Need DTO only?
  -> projection query

Need entity graph for business logic?
  -> join fetch or @EntityGraph

Need avoid N+1 for many lazy relations?
  -> batch fetching / join fetch / projection

Lazy exception in controller?
  -> map DTO inside transaction
```

## Step 5 — Add regression test

Test query count for important endpoints if your stack supports it.

```text
Order list should not execute more than expected query count.
```

---

## Interview Q&A

### Q1. What is lazy loading?

Strong answer:

> Lazy loading means Hibernate does not load a related entity immediately. It creates a proxy or delayed reference and loads the relation only when the code accesses it, as long as the persistence context is still open.

### Q2. What is eager loading?

Strong answer:

> Eager loading means Hibernate loads the relationship immediately when the parent entity is loaded. It avoids later lazy access but can cause overfetching and heavy queries if used blindly.

### Q3. Which is better, LAZY or EAGER?

Strong answer:

> Neither is universally better. Lazy is usually safer as a default, especially for collections, but the real production solution is an intentional fetch plan per use case using projections, join fetch, entity graphs, or batch fetching.

### Q4. What is LazyInitializationException?

Strong answer:

> It occurs when code accesses a lazy relationship after the persistence context/session has closed. Hibernate cannot go back to the database to initialize the proxy. The fix is to fetch or map the needed data inside the transaction, usually into a DTO.

### Q5. Why is EAGER dangerous?

Strong answer:

> EAGER can load relationships even when the use case does not need them. This causes overfetching, larger queries, more memory usage, and hidden performance problems at scale.

### Q6. How do you fix N+1 caused by lazy loading?

Strong answer:

> First identify what the use case actually needs. Then use a projection query, join fetch, entity graph, or batch fetching. For read-only lists, projection is often best.

### Q7. Should controllers return JPA entities?

Strong answer:

> Usually no. Returning entities can trigger lazy loading during serialization, leak internal fields, and create recursion problems. Controllers should return DTOs designed for the API contract.

---

## Production Checklist

```text
Mapping Defaults
[ ] Are large relationships LAZY?
[ ] Are collections not blindly EAGER?
[ ] Are defaults understood as defaults, not final fetch plans?

Use Case Fetch Plan
[ ] Does each endpoint know exactly what fields it needs?
[ ] Are read-only screens using projections where possible?
[ ] Are detail screens using join fetch/entity graph intentionally?
[ ] Are write use cases loading only required aggregate data?

Debugging
[ ] Is SQL query count visible in lower environment?
[ ] Are lazy relations accessed inside transaction?
[ ] Are DTOs mapped before leaving service boundary?
[ ] Are entities avoided in controller responses?

Performance
[ ] Is N+1 tested for list endpoints?
[ ] Is overfetching checked after mapping changes?
[ ] Is pagination safe with fetch joins?
[ ] Are p99 latency and DB round trips monitored?
```

---

## One-Page Cheat Sheet

```text
Lazy vs Eager Loading
=====================

Core Question
-------------
When should Hibernate load related data?

LAZY
----
Load relation later when accessed.
Needs open persistence context.
Can cause N+1.
Can throw LazyInitializationException.
Good default for many relationships.

EAGER
-----
Load relation immediately with parent.
Avoids later lazy access.
Can cause overfetching.
Dangerous if used blindly.

Best Rule
---------
Mapping fetch type is only the default.
Repository query should define fetch plan for important use cases.

Common Fixes
------------
Need few fields?       Projection DTO
Need entity + relation? Join fetch / EntityGraph
N+1 in list?           Projection / join fetch / batch fetch
Lazy exception?        Fetch/map inside transaction
Overfetching?          Avoid EAGER, design query

Debug Rule
----------
Every endpoint should have expected SQL shape.
Unknown SQL shape = production surprise.
```

---

## Last-Minute Interview Revision

Do not say:

```text
Lazy is good and eager is bad.
```

Say:

```text
Lazy and eager are fetch timing strategies. Lazy loads relationships when accessed, eager loads them immediately. In production, I prefer intentional fetch plans per use case rather than relying blindly on entity defaults.
```

Senior version:

```text
The real problem is not choosing Lazy or Eager globally. The real problem is controlling database boundary crossings. For every endpoint, I want to know exactly which relationships are loaded and how many SQL queries happen.
```

---

## One Picture To Remember

```text
                       ORDER ENTITY
                            |
          +-----------------+-----------------+
          |                                   |
          v                                   v
     product field                      customer field
          |                                   |
          v                                   v
+-------------------+              +-------------------+
| LAZY              |              | EAGER             |
|-------------------|              |-------------------|
| Keep door closed  |              | Open door now     |
| Load when touched |              | Load with parent  |
+-------------------+              +-------------------+
          |                                   |
          v                                   v
May cause N+1 or exception          May cause overfetching
```

Final retention sentence:

> **Lazy vs Eager is not a morality choice; it is a database boundary timing choice. Design the fetch plan for the use case.**
