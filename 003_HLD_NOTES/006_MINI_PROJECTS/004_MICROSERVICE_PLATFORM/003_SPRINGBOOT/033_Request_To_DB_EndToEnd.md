# 033_Request_To_DB_EndToEnd — The HTTP-to-Database Unit of Work Model

## Core Mental Model

Do not imagine a Spring Boot database request as:

```text
Controller calls repository.
Repository talks to database.
```

That is too shallow for production.

The better mental model is:

> **A request-to-database flow is one controlled unit of work that crosses multiple boundaries: HTTP, security, controller, service transaction, persistence context, connection pool, SQL execution, flush/commit, and response.**

```text
HTTP Request
    |
    v
Controller
    |
    v
Service @Transactional
    |
    v
Persistence Context
    |
    v
HikariCP Connection
    |
    v
Hibernate/JDBC SQL
    |
    v
Database
    |
    v
Commit / Response
```

This chapter teaches exactly one idea:

> **A database-backed request is not “one method call”; it is a pipeline of ownership boundaries, and each boundary has a specific job and failure mode.**

If you remember only one sentence:

> **The controller translates HTTP, the service owns the transaction, Hibernate tracks entities, Hikari lends a connection, the database executes SQL, and commit makes the result durable.**

---

## Why This Exists

Many Spring Boot developers can write this code:

```java
@PostMapping("/orders")
public OrderResponse create(@RequestBody CreateOrderRequest request) {
    return orderService.createOrder(request);
}
```

And this:

```java
@Transactional
public OrderResponse createOrder(CreateOrderRequest request) {
    Product product = productRepository.findById(request.productId())
            .orElseThrow();

    product.decreaseStock(request.quantity());

    Order order = Order.create(product, request.quantity());
    orderRepository.save(order);

    return OrderResponse.from(order);
}
```

But production failures happen when the hidden runtime path is misunderstood:

```text
Why did the controller not run?
Why did @Transactional not apply?
Why did Hibernate update without save?
Why did SQL run only at commit?
Why did Hikari timeout?
Why did DB lock wait block the request?
Why did response serialization trigger lazy loading?
Why did rollback happen?
```

The request-to-DB model exists because backend engineering is boundary engineering.

Each layer owns one responsibility.

```text
Controller:
  HTTP boundary

Service:
  use case + transaction boundary

Repository:
  persistence API boundary

Hibernate:
  object-row synchronization boundary

HikariCP:
  DB connection checkout boundary

Database:
  truth + concurrency boundary
```

If you mix the boundaries, debugging becomes guessing.

---

## Problem Statement

A client sends:

```http
POST /api/orders
Content-Type: application/json
Authorization: Bearer token

{
  "productId": 1001,
  "quantity": 2
}
```

The system must:

```text
1. Authenticate request.
2. Parse JSON.
3. Validate command.
4. Start transaction.
5. Load product.
6. Check stock.
7. Create order.
8. Update stock.
9. Flush SQL.
10. Commit transaction.
11. Serialize response.
```

The core problem:

> **How does one HTTP request become a safe database transaction and then become an HTTP response?**

The answer is the request-to-DB unit of work.

```text
HTTP request enters.
Service transaction begins.
Persistence context tracks entities.
Connection is borrowed only when needed.
SQL executes.
Flush synchronizes changes.
Commit makes changes durable.
Response returns.
```

---

## Real World Analogy

Imagine a package delivery service.

```text
Customer submits delivery request.
Reception verifies identity.
Dispatcher creates delivery job.
Warehouse picks item.
Driver checks out vehicle.
Driver delivers package.
System records delivery.
Customer receives confirmation.
```

Mapping:

```text
Delivery company              Spring Boot request-to-DB
----------------              -------------------------
Customer request              HTTP request
Reception                     controller/filter
Dispatcher                    service
Delivery job                  transaction
Warehouse record              database row
Driver vehicle checkout       Hikari connection borrow
Delivery route                SQL execution
Delivery confirmation         commit + response
```

Important:

> **The driver should not decide business policy, and reception should not update warehouse inventory.**

Similarly:

```text
Controller should not own business transaction.
Repository should not own use case policy.
Service should coordinate the unit of work.
```

---

## The One Mental Model

Think of a database-backed request as a sealed work envelope.

```text
Envelope starts at service transaction.
Inside the envelope:
  load entities
  modify entities
  save new entities
  flush SQL
  commit or rollback

Outside the envelope:
  HTTP parsing before
  response serialization after
```

ASCII:

```text
HTTP boundary
    |
    v
Controller
    |
    v
+------------------------------------------------+
| Service Transaction Envelope                   |
|------------------------------------------------|
| Persistence Context opens                      |
| Entities become managed                        |
| Business methods modify objects                |
| Hibernate dirty checking detects changes       |
| Hikari connection used for SQL                 |
| Flush sends INSERT/UPDATE/DELETE               |
| Commit makes changes durable                   |
+------------------------------------------------+
    |
    v
Response boundary
```

Senior debugging question:

> **Where is the request currently waiting: HTTP thread, security filter, transaction, connection pool, database lock, SQL execution, flush, commit, or serialization?**

---

## Core Concepts

## HTTP Boundary

The HTTP boundary converts network request into Java input.

Responsibilities:

```text
route matching
headers
path variables
query params
request body
validation
status codes
response DTOs
```

Owned by:

```text
Controller
DispatcherServlet
MessageConverters
```

## Security Boundary

Security filters decide whether the request can continue.

Responsibilities:

```text
extract credentials
authenticate identity
authorize endpoint
return 401/403 when needed
```

If security rejects, controller/service/database never run.

## Controller Boundary

Controller translates HTTP into application command.

Good controller:

```text
small
DTO-focused
no deep business logic
no transaction orchestration
no direct EntityManager usage
```

## Service Boundary

Service owns the use case.

Responsibilities:

```text
transaction boundary
business rules
domain operations
repository coordination
external side-effect strategy
```

`@Transactional` usually belongs here.

## Transaction Boundary

A transaction is the atomic database unit.

```text
all changes commit
or all changes rollback
```

Spring opens transaction through proxy around service method.

## Persistence Context

Hibernate’s first-level workbench.

Responsibilities:

```text
identity map
managed entity tracking
dirty checking
flush queue
```

Inside transaction, loaded entities are managed.

## HikariCP Boundary

HikariCP lends database connections.

Responsibilities:

```text
limit connections
reuse connections
make callers wait when pool exhausted
timeout when unavailable
protect database from unlimited connections
```

## Database Boundary

Database owns durable truth and concurrency.

Responsibilities:

```text
execute SQL
enforce constraints
handle locks
apply isolation
commit/rollback
persist data
```

---

## Internal Architecture

```text
Client
  |
  v
Tomcat
  |
  v
Filter Chain / Security
  |
  v
DispatcherServlet
  |
  v
Controller
  |
  v
Service Proxy
  |
  | begin @Transactional
  v
Service Method
  |
  v
Repository Proxy
  |
  v
EntityManager
  |
  v
Persistence Context
  |
  v
Hibernate
  |
  v
HikariDataSource
  |
  v
JDBC Connection
  |
  v
Database
```

Response path:

```text
Database -> JDBC -> Hibernate -> Repository -> Service -> Controller -> JSON -> Client
```

---

## Internal Working

Given:

```java
@Transactional
public OrderResponse createOrder(CreateOrderRequest request) {
    Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new BusinessException("Product not found"));

    product.decreaseStock(request.quantity());

    Order order = Order.create(product, request.quantity());
    Order saved = orderRepository.save(order);

    return OrderResponse.from(saved);
}
```

What happens internally:

```text
1. Controller calls service proxy.
2. Transaction interceptor begins DB transaction.
3. Persistence context is associated with transaction.
4. Repository findById delegates to EntityManager.
5. EntityManager checks persistence context.
6. Product not found in context.
7. Hibernate needs SQL.
8. Hibernate asks Hikari for connection.
9. Hikari returns connection or waits.
10. Hibernate sends SELECT.
11. DB returns product row.
12. Hibernate creates Product entity.
13. Product becomes managed.
14. Snapshot is stored.
15. product.decreaseStock modifies managed entity.
16. Order.create creates new transient entity.
17. orderRepository.save makes Order managed.
18. Method returns response DTO.
19. Transaction interceptor prepares commit.
20. Hibernate flushes persistence context.
21. Dirty checking detects Product stock change.
22. Hibernate sends INSERT order and UPDATE product.
23. DB enforces constraints and locks.
24. DB commit succeeds.
25. Hikari connection returns to pool.
26. Controller response serialized to JSON.
```

---

## Rich ASCII Diagram — Request to Database

```text
POST /api/orders
      |
      v
+----------------------+
| Filter Chain         |
| auth / cors / logs   |
+----------+-----------+
           |
           v
+----------------------+
| DispatcherServlet    |
| route to controller  |
+----------+-----------+
           |
           v
+----------------------+
| Controller           |
| JSON DTO -> command  |
+----------+-----------+
           |
           v
+----------------------+
| Service Proxy        |
| starts transaction   |
+----------+-----------+
           |
           v
+----------------------+
| Service Method       |
| business use case    |
+----------+-----------+
           |
           v
+----------------------+
| Repository           |
| persistence API      |
+----------+-----------+
           |
           v
+----------------------+
| EntityManager        |
| persistence context  |
+----------+-----------+
           |
           v
+----------------------+
| Hibernate            |
| SQL generation       |
+----------+-----------+
           |
           v
+----------------------+
| HikariCP             |
| borrow connection    |
+----------+-----------+
           |
           v
+----------------------+
| Database             |
| SQL + locks + commit |
+----------------------+
```

---

## Rich ASCII Diagram — The Transaction Envelope

```text
Controller
    |
    v
Service Proxy
    |
    | begin transaction
    v
+--------------------------------------------------+
| @Transactional createOrder()                     |
|--------------------------------------------------|
| Persistence Context                              |
|   Product#1001 -> Product@abc snapshot stock=10  |
|                                                  |
| Business changes                                 |
|   Product@abc stock=8                            |
|   Order@xyz status=CREATED                       |
|                                                  |
| Flush at commit                                  |
|   INSERT orders (...)                            |
|   UPDATE products SET stock=8 WHERE id=1001      |
+--------------------------------------------------+
    |
    | commit
    v
Database durable state
```

---

## Java Code Example — Complete Flow

### DTOs

```java
public record CreateOrderRequest(
        Long productId,
        int quantity
) {}
```

```java
public record OrderResponse(
        Long orderId,
        Long productId,
        int quantity,
        String status
) {}
```

### Controller

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(201).body(response);
    }
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
    public OrderResponse createOrder(CreateOrderRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new BusinessException("Product not found"));

        product.decreaseStock(request.quantity());

        Order order = Order.create(product, request.quantity());
        Order saved = orderRepository.save(order);

        return new OrderResponse(
                saved.getId(),
                product.getId(),
                saved.getQuantity(),
                saved.getStatus().name()
        );
    }
}
```

### Entity

```java
@Entity
public class Product {

    @Id
    private Long id;

    private String name;

    private int stock;

    protected Product() {}

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }

        if (stock < quantity) {
            throw new BusinessException("Out of stock");
        }

        this.stock -= quantity;
    }

    public Long getId() {
        return id;
    }
}
```

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

    public Long getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
```

```java
public enum OrderStatus {
    CREATED, CANCELLED
}
```

### Repositories

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

```java
public interface OrderRepository extends JpaRepository<Order, Long> {
}
```

---

## Step-by-Step Dry Run — Successful Request

Initial database:

```text
products
+------+----------+-------+
| id   | name     | stock |
+------+----------+-------+
| 1001 | Keyboard | 10    |
+------+----------+-------+
```

Request:

```http
POST /api/orders

{
  "productId": 1001,
  "quantity": 2
}
```

Flow:

```text
1. Tomcat receives request.
2. Filters allow request.
3. DispatcherServlet maps to OrderController#create.
4. Jackson converts JSON to CreateOrderRequest.
5. Controller calls orderService.createOrder().
6. Spring transaction proxy begins transaction.
7. Persistence context starts empty.
8. productRepository.findById(1001) executes.
9. Hibernate asks Hikari for connection.
10. Hikari lends connection C1.
11. SELECT product WHERE id=1001.
12. Product row loaded.
13. Product becomes managed with snapshot stock=10.
14. product.decreaseStock(2) changes stock to 8.
15. Order created as transient object.
16. orderRepository.save(order) makes order managed.
17. Service builds response DTO.
18. Method returns.
19. Transaction commit begins.
20. Hibernate flushes.
21. INSERT order.
22. UPDATE product stock 8.
23. Database commits.
24. Connection C1 returns to Hikari.
25. Response DTO serialized to JSON.
26. Client receives 201.
```

Final database:

```text
products
+------+----------+-------+
| id   | name     | stock |
+------+----------+-------+
| 1001 | Keyboard | 8     |
+------+----------+-------+

orders
+-----+------------+----------+---------+
| id  | product_id | quantity | status  |
+-----+------------+----------+---------+
| 501 | 1001       | 2        | CREATED |
+-----+------------+----------+---------+
```

---

## Dry Run — Product Not Found

Request:

```json
{
  "productId": 9999,
  "quantity": 2
}
```

Flow:

```text
1. Request reaches service.
2. Transaction begins.
3. Repository queries database.
4. Product not found.
5. BusinessException thrown.
6. Transaction rolls back.
7. ControllerAdvice maps exception to 404/400.
8. No order inserted.
9. Connection returned.
```

Key point:

```text
Rollback happens because exception exits transactional method.
```

---

## Dry Run — Out of Stock

Database:

```text
product 1001 stock=1
```

Request:

```json
{
  "productId": 1001,
  "quantity": 2
}
```

Flow:

```text
1. Product loaded and managed.
2. product.decreaseStock(2) checks stock.
3. BusinessException "Out of stock" thrown.
4. Service method exits with exception.
5. Transaction rolls back.
6. No update committed.
7. Response is error.
```

Important:

```text
Business rule failure happens before flush.
```

---

## Dry Run — Hikari Connection Timeout

Flow:

```text
1. Request reaches service.
2. Transaction starts.
3. Repository needs DB.
4. Hibernate requests connection from Hikari.
5. All connections are active.
6. Request waits.
7. connectionTimeout expires.
8. Hikari throws exception.
9. Transaction fails.
10. Response becomes 503/500 depending exception handling.
```

This request failed before SQL ran.

Root causes may include:

```text
slow queries
long transactions
connection leak
DB lock waits
pool too small
too many pods
external calls inside transaction
```

---

## Dry Run — Flush Failure at Commit

Business method appears successful:

```text
product decreased
order saved
response DTO built
```

But commit fails:

```text
unique constraint violation
foreign key violation
optimistic lock conflict
DB connection failure
deadlock
```

Flow:

```text
1. Service method returns normally.
2. Transaction interceptor tries commit.
3. Hibernate flush sends SQL.
4. Database rejects SQL.
5. Exception thrown during commit.
6. Transaction rolls back.
7. Controller does not send success.
```

Important:

```text
Some failures happen after service method body finishes but before response is committed.
```

This is why understanding flush/commit matters.

---

## Sequence Diagram

```text
Client
  |
  | POST /api/orders
  v
Tomcat
  |
  v
Filter Chain
  |
  v
DispatcherServlet
  |
  v
OrderController
  |
  | createOrder(request)
  v
Transaction Proxy
  |
  | begin transaction
  v
OrderService
  |
  | findById(productId)
  v
ProductRepository
  |
  v
EntityManager
  |
  v
Hibernate
  |
  | getConnection()
  v
HikariCP
  |
  v
Database
  |
  | product row
  v
Hibernate
  |
  | managed Product
  v
OrderService
  |
  | decreaseStock + save order
  v
Transaction Proxy
  |
  | flush + commit
  v
Database
  |
  | INSERT + UPDATE + COMMIT
  v
OrderController
  |
  v
Client
```

---

## Production Scale Example

Imagine checkout service at 2,000 RPS.

```text
Tomcat max threads: 200
Hikari max pool: 30
Postgres safe active queries: 40
Average DB connection hold time: 40ms
```

Healthy:

```text
DB work short
Hikari active around 20-25
pending near 0
p99 stable
```

Now a new feature adds N+1 query:

```text
each order request now executes 100 extra SELECTs
DB hold time becomes 500ms
```

Using Little's Law:

```text
needed active DB connections = throughput * hold time

Before:
2,000 RPS * 0.04s = 80 connection demand
but per pod distribution may be stable with multiple pods and optimized paths

After:
2,000 RPS * 0.5s = 1000 connection demand
```

Result:

```text
Hikari pool saturates.
Requests wait for DB connection.
Tomcat threads pile up.
Latency rises.
Retries increase traffic.
Database gets more overloaded.
```

Fix:

```text
remove N+1
use projection query
cache read-heavy data
shorten transaction
rate limit if needed
```

Senior rule:

> **Request-to-DB performance is mostly about how long each request holds scarce resources.**

---

## Production Failure Story

A team added payment call inside the transactional method:

```java
@Transactional
public OrderResponse createOrder(CreateOrderRequest request) {
    Product product = productRepository.findById(request.productId()).orElseThrow();
    product.decreaseStock(request.quantity());

    Order order = orderRepository.save(Order.create(product, request.quantity()));

    paymentClient.charge(order.getId()); // external HTTP call inside transaction

    return OrderResponse.from(order);
}
```

It worked in testing.

In production, payment provider slowed to 5 seconds.

Impact:

```text
Transactions stayed open for 5 seconds.
DB connections stayed borrowed.
Rows stayed locked longer.
Hikari pool saturated.
Unrelated DB requests timed out.
Checkout p99 exploded.
```

Root cause:

```text
External network call was inside DB transaction envelope.
```

Fix:

```text
Create order in DB.
Commit transaction.
Publish payment command through outbox/Kafka.
Process payment asynchronously or in saga.
Use idempotency key.
Use timeout and circuit breaker.
```

Lesson:

> **Do not hold database transactions and connections while waiting on slow external systems.**

---

## Debugging Mindset

When request-to-DB fails, locate the boundary.

```text
401/403:
  security boundary

400 before controller:
  JSON/validation boundary

Controller log exists but service log missing:
  controller or proxy call issue

@Transactional not working:
  proxy/self-invocation/private method/wrong bean

Hikari timeout:
  connection pool boundary

Slow SQL:
  database/query boundary

Deadlock/lock timeout:
  database concurrency boundary

LazyInitializationException in response:
  serialization after persistence context closed

Unexpected no update:
  detached entity or transaction rollback

Update happens without save:
  managed entity dirty checking
```

### Debug Questions

```text
1. Did the request pass filters?
2. Did controller method execute?
3. Did service method enter through proxy?
4. Did transaction start?
5. Did repository borrow connection?
6. How many SQL statements executed?
7. Did flush happen before commit?
8. Did commit succeed?
9. Was response DTO built from entities safely?
10. Did any exception trigger rollback?
```

---

## Common Misconceptions

## Misconception 1 — “Repository save immediately commits”

No.

`save()` makes entity managed/persisted.
Commit happens at transaction boundary.

SQL may happen before commit depending ID strategy/flush, but durability comes from commit.

## Misconception 2 — “Controller owns transaction”

Usually no.

Service should own use-case transaction.

## Misconception 3 — “Database connection is held from request start”

Not always.

Connection is usually borrowed when DB work is needed, but transaction and JPA behavior can affect timing.

## Misconception 4 — “If service returns, DB commit already succeeded”

Not exactly.

With Spring transaction proxy, commit happens after method returns but before control fully returns to caller.

Flush/commit failure can happen after method body.

## Misconception 5 — “More Tomcat threads fix DB slowness”

No.

If DB/Hikari is bottleneck, more request threads create more waiting.

## Misconception 6 — “Returning entities is fine”

Returning entities can cause lazy loading, serialization loops, data leaks, or failures after session closes.

Prefer DTOs.

---

## Java/Spring Boot Code — Exception Handling

```java
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> business(BusinessException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("BUSINESS_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> database(DataAccessException ex) {
        return ResponseEntity.status(503)
                .body(new ErrorResponse("DATABASE_ERROR", "Temporary database problem"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unknown(Exception ex) {
        return ResponseEntity.status(500)
                .body(new ErrorResponse("INTERNAL_ERROR", "Unexpected error"));
    }
}
```

```java
public record ErrorResponse(String code, String message) {}
```

Production idea:

```text
Expose safe error code.
Log detailed cause internally.
Do not leak SQL or stack traces to client.
```

---

## Performance Considerations

Request-to-DB latency is sum of stages:

```text
filter/security latency
JSON deserialization latency
service logic latency
connection wait time
SQL execution time
DB lock wait time
flush/commit time
response serialization time
```

Measure separately.

Useful metrics:

```text
http.server.requests
hikaricp.connections.active
hikaricp.connections.pending
hikaricp.connections.timeout
hibernate query count
DB slow queries
DB lock waits
transaction duration
Tomcat active threads
p95/p99 latency
```

Performance killers:

```text
N+1 queries
missing indexes
large transactions
external calls inside transaction
huge response DTOs
lazy loading during serialization
oversized connection pool
undersized connection pool
lock contention
```

---

## Scalability Considerations

Each request consumes multiple limited resources:

```text
Tomcat thread
CPU
memory
Hikari connection
DB transaction
DB locks
network sockets
Redis/Kafka/external client pools
```

At high scale, the bottleneck moves.

```text
low traffic:
  code inefficiency hidden

medium traffic:
  DB query count matters

high traffic:
  connection hold time matters

burst traffic:
  queues and backpressure matter

failure traffic:
  retries can amplify overload
```

Patterns:

```text
short transactions
DTO projections
cache read-heavy data
pagination
bulkhead external calls
idempotency
rate limiting
timeouts
circuit breakers
outbox pattern
read replicas
```

---

## Failure Investigation Playbook

## Step 1 — Identify symptom

```text
status code?
timeout?
slow request?
wrong data?
rollback?
duplicate write?
```

## Step 2 — Follow the path

```text
Filter logs
Controller logs
Service transaction logs
SQL logs
Hikari metrics
DB metrics
Response logs
```

## Step 3 — Locate wait time

```text
Tomcat thread wait?
Hikari pending?
SQL execution?
DB lock?
External call?
Serialization?
```

## Step 4 — Check transaction shape

```text
Where does @Transactional start/end?
Are external calls inside it?
How many queries inside?
How long does it hold connection?
Does rollback happen?
```

## Step 5 — Fix by boundary

```text
HTTP issue -> controller/filter
Business issue -> service/domain
Query issue -> repository/DB
Connection issue -> Hikari/transaction duration
Commit issue -> constraints/locks/flush
Serialization issue -> DTO mapping
```

---

## Interview Q&A

### Q1. Explain request-to-database flow in Spring Boot.

Strong answer:

> The request enters the embedded server, passes filters and Spring Security, reaches DispatcherServlet and controller, then the controller calls a service. The service method usually starts a transaction through a Spring proxy. Repositories delegate to EntityManager/Hibernate, which uses the persistence context and borrows a JDBC connection from HikariCP when SQL is needed. The database executes SQL, Hibernate flushes changes, the transaction commits or rolls back, and the response DTO is serialized back to the client.

### Q2. Where should `@Transactional` be placed?

Strong answer:

> Usually on service methods because a service method represents a complete business use case. The transaction should cover the full unit of work across repositories, not just one repository call.

### Q3. When is a DB connection borrowed?

Strong answer:

> Typically when Hibernate/JDBC actually needs to execute SQL. It asks the DataSource, HikariCP lends a connection, and the connection is returned after transaction/session work completes.

### Q4. Why can commit fail after service code seems successful?

Strong answer:

> Hibernate may flush SQL at commit time. Constraint violations, optimistic lock conflicts, deadlocks, or database errors can occur during flush/commit after the service method body has completed.

### Q5. Why should external calls be avoided inside transactions?

Strong answer:

> They extend transaction duration, keep DB connections and locks held while waiting on network, and can cause Hikari exhaustion and cascading failures if the external service slows.

### Q6. Why use DTOs instead of returning entities?

Strong answer:

> DTOs control the response shape and avoid lazy loading during serialization, entity graph leaks, circular references, and exposing internal database model fields.

### Q7. How do you debug slow request-to-DB flow?

Strong answer:

> I trace each stage: filters/security, controller, service transaction, Hikari connection wait, SQL execution, DB locks, flush/commit, and serialization. I correlate HTTP latency with Hikari metrics, SQL logs, DB slow queries, lock waits, and transaction duration.

---

## Production Checklist

```text
Controller
[ ] Uses request/response DTOs
[ ] Validates input
[ ] No business transaction logic
[ ] Clear status codes

Service
[ ] Owns @Transactional boundary
[ ] No slow external calls inside transaction
[ ] Business rules centralized
[ ] Idempotency considered for writes

Repository/DB
[ ] Queries are known
[ ] N+1 avoided
[ ] Indexes aligned
[ ] Locking strategy clear
[ ] Constraints understood

Hibernate
[ ] Entity states understood
[ ] Flush timing understood
[ ] DTO mapping before session closes
[ ] Lazy loading controlled

Hikari
[ ] Pool size tuned with replica count
[ ] Pending connections monitored
[ ] Timeouts monitored
[ ] Connection hold time minimized

Observability
[ ] Request tracing
[ ] SQL visibility in lower env
[ ] Hikari metrics
[ ] DB slow query/lock metrics
[ ] p95/p99 latency dashboards
```

---

## One-Page Cheat Sheet

```text
Request To DB End-to-End
========================

Request Path
------------
Client
 -> Tomcat
 -> Filters/Security
 -> DispatcherServlet
 -> Controller
 -> Service Proxy
 -> @Transactional
 -> Repository
 -> EntityManager
 -> Persistence Context
 -> Hibernate
 -> HikariCP
 -> JDBC Connection
 -> Database
 -> Flush/Commit
 -> Response DTO
 -> JSON response

Layer Ownership
---------------
Controller:
  HTTP translation

Service:
  business use case + transaction

Repository:
  persistence access

Hibernate:
  entity tracking + SQL generation

HikariCP:
  connection checkout

Database:
  durable truth + locks + constraints

Debug Rules
-----------
401/403:
  security

400 before controller:
  JSON/validation

Hikari timeout:
  connection pool pressure

Slow query:
  DB/repository

Commit failure:
  flush/constraint/lock

Lazy serialization:
  DTO/session boundary issue

Best Sentence
-------------
Controller translates HTTP,
service owns transaction,
Hibernate tracks entities,
Hikari lends connection,
database commits truth.
```

---

## Last-Minute Interview Revision

Do not say:

```text
Controller calls repository and DB saves data.
```

Say:

```text
A request passes through server, filters, DispatcherServlet, and controller. The controller calls a service through a transactional proxy. Repositories use EntityManager/Hibernate, which manages a persistence context and borrows a connection from HikariCP to execute SQL. Hibernate flushes changes and the database commits or rolls back before the response is serialized.
```

Senior version:

```text
I debug request-to-DB flows by following ownership boundaries: HTTP parsing, security, controller mapping, service transaction, persistence context, connection checkout, SQL execution, database locks, flush/commit, and response serialization. Each boundary has different failure modes and metrics.
```

---

## One Picture To Remember

```text
                 REQUEST TO DATABASE UNIT OF WORK

HTTP Request
    |
    v
[Filters / Security]
    |
    v
[Controller]
HTTP DTO boundary
    |
    v
[Service Proxy]
begin transaction
    |
    v
[Service Method]
business use case
    |
    v
[Repository]
persistence gateway
    |
    v
[EntityManager / Persistence Context]
managed entities + dirty checking
    |
    v
[Hibernate]
SQL generation + flush
    |
    v
[HikariCP]
borrow JDBC connection
    |
    v
[Database]
locks + constraints + commit
    |
    v
[Response DTO]
JSON response
```

Final retention sentence:

> **A request-to-database flow is a transaction-centered pipeline: HTTP enters, service coordinates, Hibernate tracks, Hikari connects, database commits, and response returns.**
