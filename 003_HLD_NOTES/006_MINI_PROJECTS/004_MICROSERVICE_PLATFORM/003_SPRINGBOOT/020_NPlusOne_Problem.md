# 020_NPlusOne_Problem — The Hidden Loop Query Model

## Core Mental Model

Do not imagine the N+1 problem as “Hibernate being slow”.
That is too vague.

The better mental model is:

> **N+1 happens when one query loads N parent rows, and then your Java loop accidentally triggers one extra query per parent row.**

```text
You think:

    Load orders once
    Print product name for each order

Hibernate may do:

    1 query  -> load all orders
    N queries -> load product for each order

Total = 1 + N queries
```

The chapter teaches exactly one idea:

> **N+1 is a hidden database loop caused by lazy relationship access after loading a list of parent entities.**

If you remember only one sentence:

> **N+1 means your Java loop became a SQL loop.**

---

## Why This Exists

Object-oriented code makes relationships look free.

```java
order.getProduct().getName();
order.getCustomer().getName();
```

In Java, this looks like simple field access.

But in Hibernate, if `product` or `customer` is lazy, this may be a database call.

```text
Java line                         Possible database reality
---------                         -------------------------
order.getProduct()                SELECT product WHERE id = ?
order.getCustomer()               SELECT customer WHERE id = ?
```

That is the dangerous part.

A normal Java loop can secretly become a database loop.

```java
List<Order> orders = orderRepository.findAll();

for (Order order : orders) {
    System.out.println(order.getProduct().getName());
}
```

Looks innocent.

But if there are 1,000 orders:

```text
1 query to fetch orders
1000 queries to fetch products
-----------------------------
1001 total queries
```

That is N+1.

---

## Problem Statement

A backend endpoint must return order cards:

```json
[
  {
    "orderId": 501,
    "productName": "Keyboard",
    "customerName": "Mohamed"
  }
]
```

The developer writes:

```java
@Transactional(readOnly = true)
public List<OrderCardResponse> listOrders() {
    return orderRepository.findAll()
            .stream()
            .map(order -> new OrderCardResponse(
                    order.getId(),
                    order.getProduct().getName(),
                    order.getCustomer().getName()
            ))
            .toList();
}
```

The code is clean.
The endpoint works.
Tests pass.

But production p99 latency explodes.

Why?

Because the code hides this SQL pattern:

```text
SELECT * FROM orders;

For every order:
    SELECT * FROM products WHERE id = ?;
    SELECT * FROM customers WHERE id = ?;
```

For 1,000 orders:

```text
1 order query
1000 product queries
1000 customer queries
---------------------
2001 queries
```

The problem is not that lazy loading exists.

The problem is:

> **Lazy loading inside loops creates unplanned SQL fan-out.**

---

## Real World Analogy

Imagine a teacher needs student report cards.

Bad process:

```text
1. Get list of 100 students.
2. For student 1, walk to office and ask for parent name.
3. For student 2, walk to office and ask for parent name.
4. Repeat 100 times.
```

That is N+1.

Better process:

```text
1. Ask office once:
   “Give me all 100 students with their parent names.”
```

In Hibernate:

```text
Student list             -> parent entities
Parent name per student  -> lazy child query per row
One combined request     -> join fetch / projection / entity graph
```

The real-world lesson:

> **If you already know you need the child data for every parent, fetch it intentionally in one planned database access pattern.**

---

## The One Mental Model

Think of N+1 as a fan-out from Java memory into the database.

```text
Java code:

orders.stream()
      .map(order -> order.getProduct().getName())

Database reality:

+------------------+
| SELECT orders    |  -> returns N orders
+------------------+
        |
        v
+------------------+      +------------------+
| Order #1         | ---> | SELECT product 1 |
+------------------+      +------------------+
| Order #2         | ---> | SELECT product 2 |
+------------------+      +------------------+
| Order #3         | ---> | SELECT product 3 |
+------------------+      +------------------+
| ...              | ---> | ...              |
+------------------+      +------------------+
| Order #N         | ---> | SELECT product N |
+------------------+      +------------------+
```

The database sees many small repeated queries.

The application developer sees only one stream/map.

That mismatch is the N+1 trap.

---

## Core Concepts

## Parent Query

The first query loads a collection of parent entities.

```java
List<Order> orders = orderRepository.findAll();
```

SQL:

```sql
select * from orders;
```

This is the `1` in N+1.

## Lazy Association

A lazy association is not loaded immediately.
Hibernate stores a proxy or placeholder.

```java
@ManyToOne(fetch = FetchType.LAZY)
private Product product;
```

When you call:

```java
order.getProduct().getName();
```

Hibernate may execute:

```sql
select * from products where id = ?;
```

## Child Query Fan-Out

If the loop accesses the lazy association for every parent row, Hibernate repeats the child query.

```text
N parent rows -> N child queries
```

This is the `N` in N+1.

## Fetch Plan

A fetch plan answers:

> **For this use case, exactly what data should be loaded, and in how many queries?**

N+1 usually means there was no intentional fetch plan.

## Query Shape

Senior engineers think in query shape.

```text
Endpoint: GET /orders/cards
Expected SQL shape:
  One query joining orders, products, customers

Bad SQL shape:
  One order query + one product query per order + one customer query per order
```

If you do not know the expected SQL shape, production will surprise you.

---

## Entity Model Example

```java
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    private int quantity;

    protected Order() {}

    public Long getId() {
        return id;
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

This mapping is not wrong.
Lazy loading is usually good.

The bug appears when the use case needs product and customer data for every order, but the query loads only orders.

---

## Bad Spring Boot Example

```java
@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public OrderQueryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderCardResponse> listOrderCards() {
        return orderRepository.findAll()
                .stream()
                .map(order -> new OrderCardResponse(
                        order.getId(),
                        order.getProduct().getName(),
                        order.getCustomer().getName(),
                        order.getQuantity()
                ))
                .toList();
    }
}
```

```java
public record OrderCardResponse(
        Long orderId,
        String productName,
        String customerName,
        int quantity
) {}
```

This is dangerous because the DTO mapper touches lazy relationships inside a loop.

```text
Mapper line:
order.getProduct().getName()

Hidden meaning:
Maybe load product from database now.
```

---

## Internal Working

When `findAll()` runs:

```text
1. Hibernate executes SELECT orders.
2. It creates Order objects.
3. Each Order has product_id and customer_id.
4. Product and Customer objects are not fully loaded because they are LAZY.
5. Hibernate places lazy proxies/placeholders in the entity.
```

Persistence context after loading orders:

```text
+-----------------------------------------------------------+
| Persistence Context                                        |
|-----------------------------------------------------------|
| Order#501 -> Order@a1                                     |
|   product  -> Product proxy(id=1001, not initialized)     |
|   customer -> Customer proxy(id=42, not initialized)      |
|                                                           |
| Order#502 -> Order@a2                                     |
|   product  -> Product proxy(id=1002, not initialized)     |
|   customer -> Customer proxy(id=43, not initialized)      |
+-----------------------------------------------------------+
```

Then the mapper executes:

```java
order.getProduct().getName()
```

Hibernate checks:

```text
Is Product#1001 initialized?
  No.
Do I have an open persistence context?
  Yes.
Then execute SELECT product WHERE id = 1001.
```

Then next order:

```text
Is Product#1002 initialized?
  No.
Execute another SELECT.
```

The loop repeats.

That is the hidden SQL loop.

---

## Rich ASCII Diagram — Hidden SQL Fan-Out

```text
Service Method
    |
    v
orderRepository.findAll()
    |
    v
+------------------------------------------------+
| SQL #1                                         |
| SELECT * FROM orders;                          |
+------------------------------------------------+
    |
    v
+-------------------+----------------------------+
| Java Objects      | Lazy References            |
+-------------------+----------------------------+
| Order#1           | Product proxy #10          |
| Order#2           | Product proxy #11          |
| Order#3           | Product proxy #12          |
| ...               | ...                        |
| Order#N           | Product proxy #N           |
+-------------------+----------------------------+
    |
    | Java loop touches product.getName()
    v
+------------------------------------------------+
| SQL #2    SELECT * FROM products WHERE id=10   |
| SQL #3    SELECT * FROM products WHERE id=11   |
| SQL #4    SELECT * FROM products WHERE id=12   |
| ...                                            |
| SQL #N+1  SELECT * FROM products WHERE id=N    |
+------------------------------------------------+
```

---

## Step-by-Step Dry Run

Database:

```text
orders
+-----+------------+-------------+----------+
| id  | product_id | customer_id | quantity |
+-----+------------+-------------+----------+
| 501 | 1001       | 42          | 2        |
| 502 | 1002       | 43          | 1        |
| 503 | 1003       | 44          | 5        |
+-----+------------+-------------+----------+
```

Code:

```java
List<Order> orders = orderRepository.findAll();
for (Order order : orders) {
    System.out.println(order.getProduct().getName());
}
```

Dry run:

```text
1. findAll() executes.
2. Hibernate sends:
   SELECT * FROM orders;

3. Database returns 3 orders.
4. Hibernate creates 3 Order objects.
5. Product references are lazy proxies.

6. Loop starts.

7. First order:
   order.getProduct().getName()
   Product#1001 not loaded.
   Hibernate sends:
   SELECT * FROM products WHERE id = 1001;

8. Second order:
   Product#1002 not loaded.
   Hibernate sends:
   SELECT * FROM products WHERE id = 1002;

9. Third order:
   Product#1003 not loaded.
   Hibernate sends:
   SELECT * FROM products WHERE id = 1003;
```

Total:

```text
1 order query + 3 product queries = 4 queries
```

With 1,000 orders:

```text
1 + 1000 = 1001 queries
```

That is why small tests do not catch it.

---

## Correct Solution 1 — Join Fetch

Use join fetch when you need entities and their associations in the same use case.

```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        select o
        from Order o
        join fetch o.product
        join fetch o.customer
    """)
    List<Order> findAllWithProductAndCustomer();
}
```

Service:

```java
@Transactional(readOnly = true)
public List<OrderCardResponse> listOrderCards() {
    return orderRepository.findAllWithProductAndCustomer()
            .stream()
            .map(order -> new OrderCardResponse(
                    order.getId(),
                    order.getProduct().getName(),
                    order.getCustomer().getName(),
                    order.getQuantity()
            ))
            .toList();
}
```

Expected SQL shape:

```sql
select o.*, p.*, c.*
from orders o
join products p on o.product_id = p.id
join customers c on o.customer_id = c.id;
```

Mental model:

```text
I know I need product and customer for every order.
So I intentionally fetch them with the parent query.
```

---

## Correct Solution 2 — DTO Projection

For read-only API responses, projection is often better than loading entities.

```java
public record OrderCardResponse(
        Long orderId,
        String productName,
        String customerName,
        int quantity
) {}
```

Repository:

```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        select new com.example.orders.OrderCardResponse(
            o.id,
            p.name,
            c.name,
            o.quantity
        )
        from Order o
        join o.product p
        join o.customer c
    """)
    List<OrderCardResponse> findOrderCards();
}
```

Service:

```java
@Transactional(readOnly = true)
public List<OrderCardResponse> listOrderCards() {
    return orderRepository.findOrderCards();
}
```

Why this is strong:

```text
- No entity graph loaded unnecessarily
- No lazy proxy access in Java loop
- One predictable SQL query
- API response matches query result directly
```

For list/read screens, this is often the cleanest production solution.

---

## Correct Solution 3 — EntityGraph

EntityGraph lets you define fetch intent without writing full JPQL.

```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"product", "customer"})
    List<Order> findAll();
}
```

Use carefully because overriding `findAll()` globally may surprise other use cases.
A named method is clearer:

```java
@EntityGraph(attributePaths = {"product", "customer"})
@Query("select o from Order o")
List<Order> findAllForOrderCards();
```

Mental model:

```text
EntityGraph = tell Hibernate which associations should be loaded for this query.
```

---

## Correct Solution 4 — Batch Fetching

Batch fetching does not make it one query, but reduces many queries into fewer grouped queries.

Configuration:

```properties
spring.jpa.properties.hibernate.default_batch_fetch_size=50
```

Possible SQL pattern:

```text
SELECT * FROM orders;
SELECT * FROM products WHERE id IN (?, ?, ?, ..., 50 ids);
SELECT * FROM products WHERE id IN (?, ?, ?, ..., next 50 ids);
```

For 1,000 orders and batch size 50:

```text
Before: 1 + 1000 queries
After:  1 + 20 queries
```

This is useful when associations are accessed lazily but can be grouped.

However:

> **Batch fetching is mitigation, not a replacement for intentional query design.**

---

## Which Fix Should You Choose?

```text
Use case                                    Best first choice
--------                                    -----------------
Read-only list API                          DTO projection
Need entity behavior after loading           join fetch / entity graph
Multiple optional associations               entity graph / batch fetching
Pagination over one-to-many relation          projection or two-step query
Large report/export                          custom SQL/projection
```

Simple rule:

```text
If returning API cards -> projection.
If modifying domain aggregate -> entity fetch.
If lazy access is occasional -> batch fetching can help.
```

---

## Deep Walkthrough Example — Before and After

### Before

```java
@Transactional(readOnly = true)
public List<OrderCardResponse> listOrderCards() {
    List<Order> orders = orderRepository.findAll();

    return orders.stream()
            .map(order -> new OrderCardResponse(
                    order.getId(),
                    order.getProduct().getName(),
                    order.getCustomer().getName(),
                    order.getQuantity()))
            .toList();
}
```

SQL shape:

```text
SELECT orders
FOR EACH order:
    SELECT product
    SELECT customer
```

### After — Projection

```java
@Transactional(readOnly = true)
public List<OrderCardResponse> listOrderCards() {
    return orderRepository.findOrderCards();
}
```

Repository:

```java
@Query("""
    select new com.example.orders.OrderCardResponse(
        o.id, p.name, c.name, o.quantity
    )
    from Order o
    join o.product p
    join o.customer c
""")
List<OrderCardResponse> findOrderCards();
```

SQL shape:

```text
SELECT order columns + product name + customer name in one query
```

Before:

```text
Clean Java, bad SQL surprise
```

After:

```text
Clear Java, intentional SQL shape
```

---

## Sequence Diagram — N+1 Failure

```text
Client
  |
  | GET /orders/cards
  v
Controller
  |
  v
OrderQueryService
  |
  | orderRepository.findAll()
  v
Hibernate
  |
  | SQL #1: SELECT * FROM orders
  v
Database
  |
  | returns N orders
  v
OrderQueryService
  |
  | loop order.getProduct().getName()
  v
Hibernate
  |
  | SQL #2: SELECT product WHERE id=?
  | SQL #3: SELECT product WHERE id=?
  | SQL #4: SELECT product WHERE id=?
  | ...
  | SQL #N+1
  v
Database
  |
  v
Slow response
```

---

## Sequence Diagram — Fixed Projection

```text
Client
  |
  | GET /orders/cards
  v
Controller
  |
  v
OrderQueryService
  |
  | orderRepository.findOrderCards()
  v
Hibernate
  |
  | SQL #1:
  | SELECT o.id, p.name, c.name, o.quantity
  | FROM orders o
  | JOIN products p
  | JOIN customers c
  v
Database
  |
  | returns DTO rows
  v
OrderQueryService
  |
  | returns response
  v
Client
```

---

## Multiple Dry Runs

## Dry Run 1 — Small Dataset Looks Fine

Data:

```text
3 orders
```

Queries:

```text
1 + 3 = 4 queries
```

Result:

```text
Fast enough.
Developer does not notice.
```

Lesson:

```text
N+1 hides in small data.
```

## Dry Run 2 — Production Dataset Explodes

Data:

```text
1000 orders
```

Queries:

```text
1 + 1000 = 1001 queries
```

Result:

```text
Connection pool pressure
DB CPU increase
p99 latency spike
Timeouts
```

Lesson:

```text
N+1 is a scale bug, not a syntax bug.
```

## Dry Run 3 — First-Level Cache Reduces Some Queries

Suppose 1,000 orders share only 10 products.

Possible query count:

```text
1 order query + 10 product queries
```

Why?

```text
First-level cache ensures Product#1001 is loaded once per persistence context.
```

Still dangerous because:

```text
The SQL shape is data-dependent and accidental.
```

Do not rely on this as the main fix.

## Dry Run 4 — LazyInitializationException Instead of N+1

Code:

```java
public List<Order> listOrders() {
    return orderRepository.findAll();
}

// outside transaction
order.getProduct().getName();
```

Result:

```text
LazyInitializationException
```

Why?

```text
The lazy proxy needs a persistence context to load.
The context is closed.
```

Important difference:

```text
Inside transaction: lazy loop may cause N+1.
Outside transaction: lazy access may fail.
```

---

## Production Scale Example

Endpoint:

```text
GET /api/orders?page=0&size=500
```

Expected:

```text
Return 500 order cards under 150 ms.
```

Bad implementation:

```text
findAll page -> 1 query
product per order -> 500 queries
customer per order -> 500 queries
```

Total:

```text
1001 SQL queries for one HTTP request
```

At 100 RPS:

```text
100 requests/sec * 1001 queries/request
= 100,100 SQL queries/sec
```

The database melts.

Fixed projection:

```text
100 requests/sec * 1 query/request
= 100 SQL queries/sec
```

Same feature.
Different query shape.
Massive production difference.

---

## Production Failure Story

A team had an endpoint:

```text
GET /customers/recent-orders
```

It worked for months.
Then marketing launched a campaign.
Traffic increased and users had more orders.

Symptoms:

```text
- p99 latency jumped from 180 ms to 4 seconds
- HikariCP active connections stayed near max
- Postgres CPU spiked
- Application logs showed no obvious exception
```

Initial wrong guesses:

```text
Maybe Kubernetes needs more pods.
Maybe JVM heap is too small.
Maybe Redis is slow.
```

Actual root cause:

```text
One endpoint loaded 200 customers.
For each customer, it accessed customer.getOrders().
For each order, it accessed order.getProduct().

Nested lazy loading created hundreds/thousands of queries per request.
```

Fix:

```text
- Replaced entity response mapping with DTO projection.
- Added query count tests for the endpoint.
- Added SQL query metrics in lower environment.
- Reviewed all list endpoints for lazy access inside loops.
```

Lesson:

> **N+1 is often invisible in code review unless reviewers think in SQL shape.**

---

## Debugging Mindset

When an endpoint is slow, ask:

```text
1. How many SQL queries does one request execute?
2. Is there a loop touching lazy relationships?
3. Are DTO mappers accessing associations?
4. Are serializers accessing entity relationships?
5. Is the response returning entities directly?
6. Does the query have an intentional fetch plan?
```

## Symptom Map

```text
Slow only with many rows
  -> likely N+1 or over-fetching

Many similar SELECT statements in logs
  -> likely lazy association in loop

Fast locally, slow in production
  -> local dataset too small to reveal N+1

Database CPU high, app CPU normal
  -> many repeated queries

Connection pool exhausted
  -> too many SQL calls per request

Serialization is slow
  -> JSON serializer may trigger lazy loading
```

## Logging To Detect N+1

In local or lower environment:

```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
spring.jpa.properties.hibernate.generate_statistics=true
```

Look for repeated patterns:

```text
select ... from products where id=?
select ... from products where id=?
select ... from products where id=?
select ... from products where id=?
```

That repeated pattern is the smell.

## Query Count Test Mindset

For important endpoints, test expected query count.

```text
GET /orders/cards should execute 1 or 2 queries, not 501.
```

The exact tooling can vary, but the mindset is constant:

> **A production endpoint should have a known query budget.**

---

## Common Misconceptions

## Misconception 1 — “N+1 happens only with LAZY”

Mostly N+1 appears through lazy access, but poor fetch planning can also create repeated queries in other forms.
The core issue is not the annotation.

The core issue is:

```text
Repeated database access per parent row.
```

## Misconception 2 — “Set everything EAGER to fix it”

This is usually worse.

EAGER can load data you do not need.
It can create huge joins, memory pressure, duplicate rows, and unexpected query plans.

Better rule:

```text
Keep mappings conservative.
Choose fetch plan per use case.
```

## Misconception 3 — “Join fetch is always best”

Join fetch is good when you need associated entities.
But for read-only API lists, projection is often better.

```text
Need entity behavior -> join fetch
Need response fields -> projection
```

## Misconception 4 — “N+1 is only a database issue”

It is an application design issue.

The database suffers, but the cause is usually application code that does not define query shape.

## Misconception 5 — “Small test data proves endpoint is fine”

N+1 grows with data size.

```text
3 rows    -> 4 queries, looks fine
1000 rows -> 1001 queries, production issue
```

Test with realistic row counts.

## Misconception 6 — “First-level cache solves N+1”

First-level cache may reduce duplicate child loads inside one persistence context.
But it does not guarantee a good query shape.

Do not rely on accidental cache behavior.
Design the fetch plan.

---

## Java Code Example With Execution Explanation

Bad:

```java
@Transactional(readOnly = true)
public List<OrderCardResponse> badList() {
    List<Order> orders = orderRepository.findAll();

    return orders.stream()
            .map(order -> new OrderCardResponse(
                    order.getId(),
                    order.getProduct().getName(),
                    order.getCustomer().getName(),
                    order.getQuantity()
            ))
            .toList();
}
```

Execution:

```text
1. findAll loads orders only.
2. product and customer are lazy proxies.
3. Stream mapper touches product.
4. Hibernate initializes product with SELECT.
5. Stream mapper touches customer.
6. Hibernate initializes customer with SELECT.
7. Steps 3-6 repeat for every order.
```

Good:

```java
@Transactional(readOnly = true)
public List<OrderCardResponse> goodList() {
    return orderRepository.findOrderCards();
}
```

Repository:

```java
@Query("""
    select new com.example.orders.OrderCardResponse(
        o.id,
        p.name,
        c.name,
        o.quantity
    )
    from Order o
    join o.product p
    join o.customer c
""")
List<OrderCardResponse> findOrderCards();
```

Execution:

```text
1. Hibernate sends one query with joins.
2. Database returns exactly the fields needed.
3. DTOs are constructed directly.
4. No lazy proxy loop exists.
```

---

## Spring Boot Controller Example

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderQueryService orderQueryService;

    public OrderController(OrderQueryService orderQueryService) {
        this.orderQueryService = orderQueryService;
    }

    @GetMapping("/cards")
    public List<OrderCardResponse> cards() {
        return orderQueryService.listOrderCards();
    }
}
```

Service:

```java
@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public OrderQueryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderCardResponse> listOrderCards() {
        return orderRepository.findOrderCards();
    }
}
```

Repository:

```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        select new com.example.orders.OrderCardResponse(
            o.id,
            p.name,
            c.name,
            o.quantity
        )
        from Order o
        join o.product p
        join o.customer c
    """)
    List<OrderCardResponse> findOrderCards();
}
```

Layer responsibility:

```text
Controller -> HTTP endpoint
Service    -> use-case boundary and read transaction
Repository -> intentional query shape
Hibernate  -> executes one planned query
```

---

## Performance Considerations

N+1 hurts performance in multiple ways:

```text
Network overhead
  Many small DB round trips.

Database CPU
  Repeated parsing/planning/execution overhead.

Connection pool pressure
  Each request keeps using DB connection longer.

Lock/transaction duration
  More queries mean longer transactions.

Application latency
  Each query adds waiting time.
```

Rough estimate:

```text
1 query = 3 ms round trip
1001 queries = around 3000 ms just in round trips
```

Even if each query is indexed and fast, thousands of round trips kill p99 latency.

---

## Scalability Considerations

N+1 multiplies with traffic.

```text
Queries per second = requests per second * queries per request
```

Bad endpoint:

```text
100 RPS * 501 queries/request = 50,100 queries/sec
```

Fixed endpoint:

```text
100 RPS * 1 query/request = 100 queries/sec
```

This is why N+1 is a system design problem, not just a JPA problem.

At scale, every endpoint needs:

```text
- expected query count
- expected row count
- expected indexes
- expected fetch plan
- expected p95/p99 latency
```

---

## Failure Investigation Playbook

## Step 1 — Find repeated SQL

Look for repeated query templates:

```text
select p.* from products p where p.id=?
select p.* from products p where p.id=?
select p.* from products p where p.id=?
```

## Step 2 — Find the Java loop

Search for:

```text
.stream().map(... getX().getY() ...)
for (...) entity.getRelation()
DTO mapper accessing relation
JSON serialization of entity relations
```

## Step 3 — Decide actual data need

Ask:

```text
Do I need full associated entities?
Or only a few fields for response?
```

## Step 4 — Choose fetch strategy

```text
Only response fields -> projection
Need associated entity behavior -> join fetch/entity graph
Many repeated lazy references -> batch fetching
Complex report -> custom SQL/read model
```

## Step 5 — Verify query count

Do not trust code appearance.
Trust SQL logs or query metrics.

```text
Before fix: 501 queries
After fix: 1 query
```

---

## Interview Q&A

## Q1. What is the N+1 problem in Hibernate?

Strong answer:

> N+1 happens when one query loads N parent entities and then accessing a lazy association for each parent triggers one additional query per parent. The Java code looks like a normal loop, but it becomes a SQL loop.

## Q2. Why does N+1 often happen with lazy loading?

Strong answer:

> Lazy loading delays association fetching until the relation is accessed. If a list of parent entities is loaded and the code accesses a lazy child relation inside a loop, Hibernate initializes each relation separately, causing many queries.

## Q3. How do you fix N+1?

Strong answer:

> First identify the use case data shape. For read-only API responses, I usually use DTO projections. If I need entities and associations, I use join fetch or EntityGraph. Batch fetching can reduce query count when lazy loading is still appropriate.

## Q4. Should we set all relationships to EAGER?

Strong answer:

> No. EAGER can over-fetch data and create heavy joins or unexpected loading. The better approach is to keep mappings conservative and define fetch plans per use case.

## Q5. What is the difference between join fetch and projection?

Strong answer:

> Join fetch loads entities and associations into the persistence context. Projection selects only the fields needed for a DTO. For read-only list endpoints, projection is often more efficient. For domain behavior requiring entities, join fetch is useful.

## Q6. Can first-level cache solve N+1?

Strong answer:

> It can reduce duplicate loads of the same entity inside one persistence context, but it does not solve the core issue. The query shape is still accidental and data-dependent. A planned fetch strategy is better.

## Q7. How do you detect N+1 in production-like testing?

Strong answer:

> I enable SQL logging or Hibernate statistics in lower environments, test with realistic row counts, and verify query count per endpoint. Repeated similar SELECT statements are a strong sign of N+1.

---

## Production Checklist

```text
N+1 Prevention Checklist
========================

Endpoint Design
[ ] Do we know the expected SQL query count?
[ ] Do we know which associations are needed?
[ ] Is this a read-only DTO use case or domain entity use case?

Code Review
[ ] Any loop touching entity relationships?
[ ] Any stream mapper calling getRelation().getField()?
[ ] Any DTO mapper accessing lazy associations?
[ ] Any controller returning entities directly?

Repository Design
[ ] Projection used for read-only list responses?
[ ] Join fetch used when entities + associations are needed?
[ ] EntityGraph used intentionally, not globally by accident?
[ ] Batch fetching configured where useful?

Testing
[ ] Tested with realistic row count?
[ ] SQL query count inspected?
[ ] Repeated SELECT patterns checked?
[ ] Pagination behavior verified?

Production
[ ] Slow query metrics available?
[ ] Connection pool metrics monitored?
[ ] p95/p99 latency tracked per endpoint?
[ ] DB query volume tracked after releases?
```

---

## One-Page Cheat Sheet

```text
N+1 Problem
===========

Definition
----------
1 query loads N parent rows.
Then N extra queries load child rows one by one.

Best Mental Model
-----------------
Your Java loop became a SQL loop.

Typical Code Smell
------------------
orders.stream()
      .map(o -> o.getProduct().getName())

Typical SQL Smell
-----------------
SELECT * FROM orders;
SELECT * FROM products WHERE id=?;
SELECT * FROM products WHERE id=?;
SELECT * FROM products WHERE id=?;
...

Common Fixes
------------
DTO projection   -> best for read-only API cards
join fetch       -> when entities and associations are needed
EntityGraph      -> fetch plan without full JPQL
batch fetching   -> reduces many lazy queries into fewer IN queries

Do Not
------
Do not set everything EAGER.
Do not return entities directly from controllers.
Do not trust small test data.
Do not ignore SQL shape.

Debug Rule
----------
Slow with many rows? Check for lazy relationship access in loops.
```

---

## Last-Minute Interview Revision

Do not say:

```text
N+1 is when Hibernate makes many queries.
```

Say:

```text
N+1 is when one query loads N parent entities and then a loop over those entities triggers one additional query per parent by accessing lazy associations.
```

Senior version:

```text
I prevent N+1 by designing the fetch plan per use case. For API list views I prefer projections; for domain behavior I use join fetch or EntityGraph; and I verify query count with realistic data.
```

---

## One Picture To Remember

```text
                 BAD: Hidden SQL Loop

        SELECT * FROM orders
                 |
                 v
        +--------+--------+--------+--------+
        | Order1 | Order2 | Order3 | OrderN |
        +--------+--------+--------+--------+
            |        |        |        |
            v        v        v        v
        SELECT   SELECT   SELECT   SELECT
        product  product  product  product

        Total = 1 + N queries


                 GOOD: Planned Query Shape

        SELECT o.id, p.name, c.name, o.qty
        FROM orders o
        JOIN products p
        JOIN customers c
                 |
                 v
        +----------------------------------+
        | DTO rows ready for API response  |
        +----------------------------------+

        Total = 1 query
```

Final retention sentence:

> **N+1 is not a Hibernate mystery; it is a hidden SQL loop created when Java code touches lazy relationships row by row.**
