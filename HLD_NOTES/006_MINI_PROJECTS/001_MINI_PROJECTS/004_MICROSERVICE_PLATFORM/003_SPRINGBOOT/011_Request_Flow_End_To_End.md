# 011_Request_Flow_End_To_End — The Spring Boot Request Pipeline Model

## Core Mental Model

Do not imagine a Spring Boot request as:

```text
Browser calls controller.
Controller returns response.
```

That is too shallow.

The better mental model is:

> **A request is a controlled pipeline. Each stage receives the request, adds responsibility, passes it forward, and later the response travels back through the same path.**

```text
Client
  |
  v
Network Socket
  |
  v
Embedded Server
  |
  v
Servlet Container
  |
  v
Filter Chain
  |
  v
DispatcherServlet
  |
  v
Controller
  |
  v
Service
  |
  v
Repository
  |
  v
Database
```

This chapter teaches exactly one idea:

> **A Spring Boot request is not a direct method call; it is an end-to-end pipeline with clear runtime stages, ownership boundaries, and failure points.**

If you remember only one sentence:

> **A request enters through the web server, passes through filters and DispatcherServlet, reaches controller-service-repository, then returns as an HTTP response.**

---

## Why This Exists

When learning Spring Boot, many developers only see this:

```java
@GetMapping("/orders/{id}")
public OrderResponse getOrder(@PathVariable Long id) {
    return orderService.getOrder(id);
}
```

It looks like the request magically appears inside the controller.

But in production, bugs happen before and after the controller:

```text
Request timeout before controller
Authentication filter rejects request
JSON body cannot be parsed
Wrong controller mapping selected
Validation fails
Transaction fails
Repository query is slow
DB connection pool is exhausted
Response serialization fails
Client disconnects before response
```

If your mental model starts at the controller, you miss half the system.

The full request path matters because production debugging asks:

```text
Did the request reach the server?
Did it pass filters?
Was controller mapping found?
Did JSON deserialize?
Did validation pass?
Did service transaction start?
Did repository get DB connection?
Did SQL finish?
Did response serialize?
Did the client receive it?
```

The request-flow model exists so you can debug by stage, not by guessing.

---

## Problem Statement

A user calls this API:

```http
POST /api/orders
Content-Type: application/json
Authorization: Bearer token

{
  "productId": 1001,
  "quantity": 2
}
```

Your code:

```java
@PostMapping("/api/orders")
public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
    OrderResponse response = orderService.createOrder(request);
    return ResponseEntity.status(201).body(response);
}
```

The core problem:

> **How does raw network data become a Java controller method call, then become a database transaction, then become an HTTP response?**

Spring Boot solves this through a layered runtime pipeline:

```text
1. Embedded server accepts socket request.
2. Servlet container creates HttpServletRequest/Response.
3. Filters run before controller.
4. DispatcherServlet finds handler.
5. Argument resolvers build method parameters.
6. Controller invokes service.
7. Service owns business and transaction boundary.
8. Repository talks to persistence.
9. Return value handlers serialize response.
10. Filters complete and server writes bytes back.
```

That is the one flow.

---

## Real World Analogy

Imagine an airport journey.

```text
Passenger arrives at airport.
Security checks documents.
Gate system finds correct gate.
Boarding agent verifies ticket.
Passenger enters aircraft.
Flight reaches destination.
Baggage returns through arrival flow.
```

Mapping:

```text
Airport journey                Spring Boot request
---------------                -------------------
Airport entrance               network socket
Security checks                filters/security filters
Gate lookup                    DispatcherServlet mapping
Boarding agent                 controller
Flight operation               service/business logic
Cargo/baggage system           repository/database
Return journey                 response serialization
```

A passenger does not teleport to the aircraft.

A request does not teleport to the controller.

It passes through gates.

---

## The One Mental Model

Think of a request as a train moving through stations.

```text
Station 1: Network server
Station 2: Servlet container
Station 3: Filter chain
Station 4: DispatcherServlet
Station 5: Handler mapping
Station 6: Controller method
Station 7: Service business logic
Station 8: Repository/database
Station 9: Response serialization
Station 10: Server writes response
```

Each station has one job.

```text
Server:
  accepts connections

Filters:
  cross-cutting checks

DispatcherServlet:
  routes request to controller

Controller:
  translates HTTP into application command

Service:
  executes business use case

Repository:
  loads/saves data

Database:
  persists state

Serializer:
  converts Java object to JSON

Server:
  sends HTTP response bytes
```

Senior debugging question:

> **At which station did the request stop, slow down, or produce the wrong result?**

---

## Core Concepts

## Embedded Server

Spring Boot usually starts an embedded server.

For Spring MVC, commonly:

```text
Tomcat
Jetty
Undertow
```

With default starter web, Tomcat is common.

It listens on a port:

```properties
server.port=8080
```

Mental model:

```text
The embedded server is the front door that accepts HTTP connections.
```

## Servlet Container

In Spring MVC, the servlet container creates:

```text
HttpServletRequest
HttpServletResponse
```

These are Java objects representing the HTTP request and response.

## Filter Chain

Filters run before the request reaches the controller.

Examples:

```text
security filter
CORS filter
logging filter
request ID filter
rate limiting filter
encoding filter
authentication filter
```

Filters can:

```text
allow request
modify request/response
reject request
short-circuit response
```

If a security filter rejects the request, the controller is never called.

## DispatcherServlet

The `DispatcherServlet` is the central front controller of Spring MVC.

It decides:

```text
Which controller method should handle this request?
How should method arguments be created?
Which return value handler should process the result?
Which exception handler should handle errors?
```

Mental model:

```text
DispatcherServlet = traffic controller for MVC requests
```

## Handler Mapping

Handler mapping finds the controller method.

Example:

```java
@PostMapping("/api/orders")
```

For request:

```text
POST /api/orders
```

Spring maps it to:

```text
OrderController.create()
```

If no match:

```text
404 Not Found
```

If method wrong:

```text
405 Method Not Allowed
```

## Argument Resolver

Argument resolvers build controller method parameters.

Examples:

```java
@PathVariable Long id
@RequestParam int page
@RequestBody CreateOrderRequest request
@RequestHeader String token
Authentication authentication
```

For `@RequestBody`, Spring uses message converters.

## Message Converter

Message converters convert HTTP body to Java objects and Java objects to HTTP body.

Common:

```text
Jackson JSON converter
```

Input:

```json
{"productId":1001,"quantity":2}
```

Becomes:

```java
new CreateOrderRequest(1001L, 2)
```

Output:

```java
new OrderResponse(501L, "CREATED")
```

Becomes JSON.

## Controller

Controller translates HTTP into application call.

It should handle:

```text
HTTP path
query params
headers
request body
status code
response DTO
```

It should not own deep business logic.

## Service

Service owns use case and transaction boundary.

```java
@Transactional
public OrderResponse createOrder(CreateOrderCommand command) {
    // business logic
}
```

## Repository

Repository hides persistence access.

```java
public interface OrderRepository extends JpaRepository<Order, Long> {
}
```

## Database

The database executes SQL and stores durable state.

---

## Internal Architecture

```text
Client
  |
  v
+-------------------------+
| Embedded Server         |
| Tomcat                  |
+-------------------------+
  |
  v
+-------------------------+
| Servlet Container       |
| HttpServletRequest      |
+-------------------------+
  |
  v
+-------------------------+
| Filter Chain            |
| Security/CORS/Logging   |
+-------------------------+
  |
  v
+-------------------------+
| DispatcherServlet       |
| Spring MVC front door   |
+-------------------------+
  |
  v
+-------------------------+
| HandlerMapping          |
| find controller method  |
+-------------------------+
  |
  v
+-------------------------+
| HandlerAdapter          |
| invoke method           |
+-------------------------+
  |
  v
+-------------------------+
| Controller              |
| HTTP boundary           |
+-------------------------+
  |
  v
+-------------------------+
| Service                 |
| business boundary       |
+-------------------------+
  |
  v
+-------------------------+
| Repository              |
| persistence boundary    |
+-------------------------+
  |
  v
+-------------------------+
| Database                |
+-------------------------+
```

Response travels back:

```text
Database -> Repository -> Service -> Controller -> DispatcherServlet -> Filters -> Server -> Client
```

---

## Internal Working

Given:

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

When a request arrives:

```text
1. Client opens TCP connection to server.
2. Tomcat accepts connection.
3. Tomcat parses HTTP request bytes.
4. Servlet request/response objects are created.
5. Filter chain starts.
6. Security filter checks authentication.
7. DispatcherServlet receives request.
8. HandlerMapping finds OrderController#create.
9. HandlerAdapter prepares method invocation.
10. RequestBody argument resolver reads JSON body.
11. Jackson converts JSON into CreateOrderRequest.
12. Controller method is invoked.
13. Controller calls service.
14. Transaction proxy opens transaction.
15. Service calls repositories.
16. Hibernate/JDBC executes SQL through HikariCP.
17. Database returns result.
18. Transaction commits.
19. Controller returns ResponseEntity.
20. ReturnValueHandler processes response.
21. Jackson serializes OrderResponse to JSON.
22. Filters finish post-processing.
23. Tomcat writes HTTP response bytes.
24. Client receives response.
```

Important:

```text
The controller is only one station in the journey.
```

---

## Rich ASCII Diagram — End-to-End Request

```text
HTTP Request
POST /api/orders
Authorization: Bearer ...
Content-Type: application/json

{
  "productId": 1001,
  "quantity": 2
}

      |
      v
+----------------------+
| 1. Tomcat            |
| accepts socket       |
+----------+-----------+
           |
           v
+----------------------+
| 2. Servlet Container |
| builds request objs  |
+----------+-----------+
           |
           v
+----------------------+
| 3. Filter Chain      |
| auth, CORS, logging  |
+----------+-----------+
           |
           v
+----------------------+
| 4. DispatcherServlet |
| MVC front controller |
+----------+-----------+
           |
           v
+----------------------+
| 5. HandlerMapping    |
| POST /api/orders     |
| -> OrderController   |
+----------+-----------+
           |
           v
+----------------------+
| 6. Argument Resolver |
| JSON -> Request DTO  |
+----------+-----------+
           |
           v
+----------------------+
| 7. Controller        |
| HTTP translation     |
+----------+-----------+
           |
           v
+----------------------+
| 8. Service           |
| business + tx        |
+----------+-----------+
           |
           v
+----------------------+
| 9. Repository        |
| persistence gateway  |
+----------+-----------+
           |
           v
+----------------------+
| 10. Database         |
| SQL + commit         |
+----------+-----------+
           |
           v
HTTP Response 201 Created
```

---

## Java Code Example

### Request DTO

```java
public record CreateOrderRequest(
        Long productId,
        int quantity
) {}
```

### Response DTO

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

### Repository

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

```java
public interface OrderRepository extends JpaRepository<Order, Long> {
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

Execution explanation:

```text
Controller:
  receives Java DTO after JSON deserialization.

Service:
  starts transaction, applies business rule.

Repository:
  loads/saves entities.

Hibernate:
  tracks changes and flushes SQL.

Database:
  persists order and stock update.

Response:
  Java DTO serialized to JSON.
```

---

## Spring Boot Code Example With Filter

A request ID filter:

```java
@Component
public class RequestIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();
        response.setHeader("X-Request-Id", requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // cleanup logging context if used
        }
    }
}
```

Internal execution:

```text
Request enters filter.
Filter adds request ID.
Filter calls next stage.
Controller/service/repository execute.
Response comes back.
Filter can do final cleanup.
```

If filter does not call:

```java
filterChain.doFilter(request, response);
```

Then request stops there.

This is how security can reject before controller.

---

## Step-by-Step Dry Run — Normal Flow

Input:

```http
POST /api/orders
Content-Type: application/json

{
  "productId": 1001,
  "quantity": 2
}
```

Database before:

```text
products
+------+----------+-------+
| id   | name     | stock |
+------+----------+-------+
| 1001 | Keyboard | 10    |
+------+----------+-------+
```

Flow:

```text
1. Tomcat receives HTTP request.
2. Servlet container creates request/response objects.
3. Filter chain runs.
4. DispatcherServlet receives request.
5. HandlerMapping finds OrderController#create.
6. Jackson converts JSON to CreateOrderRequest.
7. Controller calls orderService.createOrder().
8. Transaction begins.
9. ProductRepository loads product 1001.
10. Product stock decreases 10 -> 8.
11. Order entity is created.
12. OrderRepository saves order.
13. Hibernate flushes SQL.
14. Transaction commits.
15. Service returns OrderResponse.
16. Controller returns 201 Created.
17. Jackson serializes response DTO to JSON.
18. Tomcat writes response bytes.
```

Database after:

```text
products
+------+----------+-------+
| id   | name     | stock |
+------+----------+-------+
| 1001 | Keyboard | 8     |
+------+----------+-------+

orders
+----+------------+----------+---------+
| id | product_id | quantity | status  |
+----+------------+----------+---------+
| 501| 1001       | 2        | CREATED |
+----+------------+----------+---------+
```

Response:

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "orderId": 501,
  "productId": 1001,
  "quantity": 2,
  "status": "CREATED"
}
```

---

## Dry Run — Authentication Failure

Request:

```http
POST /api/orders
Authorization: missing
```

Flow:

```text
1. Tomcat receives request.
2. Servlet request object created.
3. Security filter chain runs.
4. Authentication filter checks token.
5. Token missing.
6. Filter writes 401 response.
7. DispatcherServlet is not called.
8. Controller is not called.
9. Service is not called.
10. Database is not touched.
```

Response:

```http
HTTP/1.1 401 Unauthorized
```

Debug lesson:

```text
If controller logs are absent, request may have stopped in filter chain.
```

---

## Dry Run — JSON Parse Failure

Request:

```http
POST /api/orders
Content-Type: application/json

{
  "productId": 1001,
  "quantity": 
}
```

Flow:

```text
1. Request reaches DispatcherServlet.
2. HandlerMapping finds controller.
3. Argument resolver tries @RequestBody.
4. Jackson tries to parse JSON.
5. JSON is invalid.
6. HttpMessageNotReadableException is thrown.
7. Controller method is not invoked.
8. Exception handler returns 400 Bad Request.
```

Debug lesson:

```text
Controller method can be skipped even after route matches if argument resolution fails.
```

---

## Dry Run — Business Failure

Request:

```json
{
  "productId": 1001,
  "quantity": 20
}
```

Database:

```text
stock = 8
```

Flow:

```text
1. Request reaches controller.
2. JSON becomes DTO.
3. Controller calls service.
4. Transaction starts.
5. Product is loaded.
6. product.decreaseStock(20) throws BusinessException.
7. Transaction rolls back.
8. ControllerAdvice maps exception to 400.
9. Response sent.
```

Response:

```http
HTTP/1.1 400 Bad Request

{
  "message": "Out of stock"
}
```

Debug lesson:

```text
Business failures should usually happen in service/domain layer, not filter or repository.
```

---

## Dry Run — Database Connection Pool Exhaustion

Flow:

```text
1. Request reaches service.
2. Transaction starts.
3. Repository needs DB connection.
4. Hibernate asks HikariCP for connection.
5. All connections are active.
6. Request waits for connection.
7. Wait exceeds connectionTimeout.
8. SQLTransientConnectionException occurs.
9. Transaction fails.
10. Response becomes 500/503 depending on handling.
```

Debug lesson:

```text
A request may reach repository but still fail before SQL executes.
```

Check:

```text
Hikari active connections
Hikari pending threads
DB slow queries
transaction duration
```

---

## Production Scale Example

Imagine an ecommerce service at 2,000 RPS.

```text
Load Balancer
    |
    v
Spring Boot pods
    |
    +-- Tomcat request threads
    +-- Filter chain
    +-- DispatcherServlet
    +-- Controllers
    +-- Services
    +-- HikariCP
    +-- PostgreSQL
```

Each stage has capacity:

```text
Load balancer:
  connection limits

Tomcat:
  max request threads

Filter chain:
  auth/crypto cost

DispatcherServlet:
  mapping/argument resolution cost

Service:
  business CPU + external calls

HikariCP:
  DB connection limit

Database:
  query/lock capacity

Serializer:
  JSON CPU and payload size
```

At scale, bottleneck can be any stage.

Example symptom:

```text
p99 latency = 2 seconds
```

Possible causes by stage:

```text
Tomcat thread saturation
security token verification slow
JSON payload too large
controller doing too much work
service calling slow external API
transaction holding DB connection
N+1 query
DB lock contention
response serialization huge
client network slow
```

Senior thinking:

```text
Trace one request through all stages.
Measure each station.
Do not guess.
```

---

## Production Failure Story

A team saw random checkout timeouts.

Controller logs showed:

```text
Checkout started
```

But sometimes there was no:

```text
Checkout completed
```

Initial guess:

```text
Controller bug.
```

Actual flow investigation:

```text
Controller entered.
Service started transaction.
Repository loaded cart.
Service called payment provider inside transaction.
Payment provider became slow.
DB connection stayed borrowed during external call.
Hikari pool exhausted.
Other requests waited for DB connection.
Tomcat threads piled up.
Health checks slowed.
Pods restarted.
```

Root cause:

```text
External network call inside DB transaction extended the request pipeline and held scarce DB connections.
```

Fix:

```text
Shorten transaction.
Persist payment attempt.
Commit DB transaction.
Call payment provider outside transaction or use outbox/saga.
Use timeouts and circuit breaker.
Monitor Hikari pending threads.
```

Lesson:

> **End-to-end request flow is only as strong as the slowest stage, and holding scarce resources across slow stages causes cascading failure.**

---

## Debugging Mindset

When a request fails, locate the stage.

```text
Did it reach server?
  Check access logs/load balancer.

Did it pass filters?
  Check security/auth logs.

Did DispatcherServlet map it?
  Check route/method/path.

Did argument resolution work?
  Check JSON/validation errors.

Did controller run?
  Check controller logs.

Did service transaction start?
  Check transaction logs/traces.

Did repository get connection?
  Check Hikari metrics.

Did SQL execute?
  Check SQL logs/DB metrics.

Did response serialize?
  Check converter/serialization errors.

Did client receive response?
  Check timeout/client disconnect/access logs.
```

### Symptom Map

```text
404
  -> HandlerMapping did not find route

405
  -> Path exists but HTTP method wrong

400 before controller
  -> JSON parse, validation, parameter binding

401/403
  -> security filter

500 after service starts
  -> business exception not mapped, DB error, serialization error

Timeout before controller log
  -> Tomcat queue, filter, network, security

Timeout after controller log
  -> service/repository/downstream/DB

Hikari timeout
  -> DB connection pool pressure

LazyInitializationException during response
  -> entity serialization after transaction/session closed
```

---

## Common Misconceptions

## Misconception 1 — “Request directly calls controller”

No.

It passes through server, servlet container, filters, DispatcherServlet, handler mapping, argument resolution, then controller.

## Misconception 2 — “If controller is not called, route is wrong”

Not always.

Security filters, CORS filters, malformed HTTP, or server-level rejection can stop request earlier.

## Misconception 3 — “`@RequestBody` is just simple mapping”

No.

It uses message converters, usually Jackson.
Invalid JSON or incompatible DTO fields can fail before controller method body runs.

## Misconception 4 — “Service is only a pass-through”

Service should own business use case and transaction boundary.

## Misconception 5 — “Repository method means SQL immediately finished”

Not necessarily.

JPA may delay flush.
Hikari may wait for connection.
Lazy loading may execute later.
Transaction commit may trigger SQL.

## Misconception 6 — “Returning entity is harmless”

Returning JPA entities can trigger lazy loading, leak fields, or fail serialization.

Prefer response DTOs.

---

## Java/Spring Boot Code Examples With Internal Explanation

## Exception Handler

```java
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation() {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Invalid request"));
    }
}
```

```java
public record ErrorResponse(String message) {}
```

Internal role:

```text
Exception thrown in controller/service/argument resolution.
DispatcherServlet searches exception resolvers.
ControllerAdvice method maps exception to HTTP response.
```

## Validation Example

```java
public record CreateOrderRequest(
        @NotNull Long productId,
        @Min(1) int quantity
) {}
```

Controller:

```java
@PostMapping
public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
    return ResponseEntity.status(201)
            .body(orderService.createOrder(request));
}
```

Flow:

```text
JSON parsed first.
DTO created.
Validation runs.
If invalid, controller method body does not execute.
```

---

## Performance Considerations

Each request stage adds cost.

```text
Network:
  connection setup, TLS

Server:
  request parsing, thread allocation

Filters:
  auth, CORS, logging, tracing

DispatcherServlet:
  mapping, argument resolution

JSON:
  deserialize/serialize

Service:
  business logic, transactions

Repository:
  DB connection, SQL

Database:
  locks, indexes, IO

Response:
  serialization, network write
```

Optimization must target the bottleneck.

Bad optimization:

```text
Tune Jackson while DB query takes 900 ms.
```

Good optimization:

```text
Trace request.
Find slowest stage.
Fix that stage.
```

Common request-flow performance killers:

```text
N+1 queries
large JSON payloads
slow authentication calls
external API call inside transaction
DB connection pool exhaustion
large response serialization
too many filters doing expensive work
missing indexes
lock contention
```

---

## Scalability Considerations

At scale, a request path is a chain of queues.

```text
Load balancer queue
Tomcat accept queue
Tomcat thread pool
Hikari connection pool
DB lock queue
Kafka producer buffer
External API connection pool
```

One slow queue can affect all upstream stages.

```text
DB slows
  -> Hikari connections held longer
  -> request threads wait
  -> Tomcat threads saturate
  -> load balancer retries
  -> traffic increases
  -> system collapses
```

Protection patterns:

```text
timeouts
bulkheads
rate limiting
circuit breakers
bounded thread pools
bounded connection pools
short transactions
DTOs
pagination
caching
read replicas
async queues
```

Request flow thinking helps decide where to protect.

---

## Failure Investigation Playbook

## Step 1 — Start at the user-visible symptom

```text
status code?
timeout?
wrong body?
slow response?
intermittent failure?
```

## Step 2 — Locate the failed stage

Use logs/traces:

```text
access log
filter log
controller log
service span
repository span
SQL log
DB metrics
response log
```

## Step 3 — Check resource pools

```text
Tomcat active threads
Hikari active/pending connections
DB active sessions
thread pool queues
Kafka producer latency
external HTTP client pools
```

## Step 4 — Check boundaries

```text
Controller:
  HTTP mapping/validation/DTO

Service:
  business logic/transaction

Repository:
  query/connection/index

Database:
  locks/slow queries

Response:
  serialization/lazy loading
```

## Step 5 — Fix by ownership

```text
Wrong status code -> ControllerAdvice
Wrong business decision -> Service/domain
Slow query -> Repository/DB
Timeout due to connection pool -> Transaction/query/pool sizing
Auth failure -> Security filter
Serialization failure -> DTO/response mapping
```

---

## Interview Q&A

### Q1. Explain Spring Boot request flow end to end.

Strong answer:

> The request first reaches the embedded server such as Tomcat, which parses HTTP and creates servlet request/response objects. It passes through the filter chain, then reaches `DispatcherServlet`. Spring MVC finds the matching controller method through handler mappings, resolves method arguments using resolvers and message converters, invokes the controller, then the service and repository layers execute business and persistence work. The return value is handled, serialized to JSON, filters complete, and the server writes the HTTP response back to the client.

### Q2. What is the role of DispatcherServlet?

Strong answer:

> `DispatcherServlet` is the central front controller in Spring MVC. It receives requests after filters, finds the correct handler method, invokes it through a handler adapter, applies argument resolvers and return value handlers, and delegates exception handling.

### Q3. What happens before a controller method is called?

Strong answer:

> The server accepts the request, the servlet container creates request/response objects, filters execute, `DispatcherServlet` maps the request to a handler, argument resolvers parse path variables, query params, headers, or request body, and validation may run. If any of these fail, the controller method body may never execute.

### Q4. Where should business logic live in the request flow?

Strong answer:

> Business logic should live in the service/domain layer, not the controller or repository. The controller translates HTTP, the service executes the use case and transaction, and the repository handles persistence access.

### Q5. Why can a request timeout even if controller code is simple?

Strong answer:

> Because the bottleneck may be outside controller code: filters, authentication, DB connection pool, slow SQL, lock waits, external API calls, response serialization, or server thread saturation.

### Q6. What is the role of filters?

Strong answer:

> Filters handle cross-cutting web concerns before and after the controller, such as security, CORS, logging, tracing, rate limiting, and request IDs. A filter can also short-circuit the request and return a response without calling the controller.

### Q7. How do you debug a slow Spring Boot request?

Strong answer:

> I trace the request stage by stage: server, filters, DispatcherServlet mapping, controller, service, repository, DB, serialization, and response write. I check metrics for Tomcat threads, Hikari pool, SQL latency, DB locks, external calls, and payload size to locate the bottleneck.

---

## Production Checklist

```text
HTTP Boundary
[ ] Are routes clear?
[ ] Are DTOs used instead of entities?
[ ] Is validation applied?
[ ] Are errors mapped with ControllerAdvice?

Filters
[ ] Is auth efficient?
[ ] Are request IDs/tracing present?
[ ] Are CORS/security rules correct?
[ ] Are expensive operations avoided in filters?

Service Layer
[ ] Does service own business use case?
[ ] Is @Transactional at service boundary?
[ ] Are external calls avoided inside transactions?
[ ] Are idempotency and retries considered?

Repository/DB
[ ] Are queries expected and measured?
[ ] Are indexes aligned?
[ ] Are N+1 queries avoided?
[ ] Is Hikari pool monitored?

Response
[ ] Are response DTOs used?
[ ] Is payload size controlled?
[ ] Is lazy loading avoided during serialization?
[ ] Are status codes correct?

Observability
[ ] Access logs enabled
[ ] Request tracing enabled
[ ] Stage-level metrics available
[ ] p95/p99 latency monitored
[ ] Error rate by status code monitored
```

---

## One-Page Cheat Sheet

```text
Spring Boot Request Flow
========================

1. Client
   Sends HTTP request.

2. Embedded Server
   Tomcat/Jetty/Undertow accepts connection.

3. Servlet Container
   Creates HttpServletRequest/Response.

4. Filter Chain
   Security, CORS, logging, tracing.

5. DispatcherServlet
   Spring MVC front controller.

6. HandlerMapping
   Finds controller method.

7. Argument Resolvers
   Build @PathVariable, @RequestParam, @RequestBody.

8. Message Converters
   JSON <-> Java DTO.

9. Controller
   HTTP translation boundary.

10. Service
   Business + transaction boundary.

11. Repository
   Persistence boundary.

12. Database
   SQL and durable state.

13. Return Value Handler
   Processes controller return.

14. Response Serialization
   Java DTO -> JSON.

15. Server Writes Response
   Client receives status/body.

Debug Rules
-----------
401/403: filters/security
404/405: mapping/method
400 before controller: binding/validation/body
500 after service: business/DB/serialization
Timeout before controller: server/filter/thread saturation
Timeout after controller: service/DB/external call
```

---

## Last-Minute Interview Revision

Do not say:

```text
Request comes to controller and service calls repository.
```

Say:

```text
A request enters the embedded server, becomes servlet request/response objects, passes through filters, reaches DispatcherServlet, is mapped to a controller method, has arguments resolved through converters, then controller calls service, service calls repository/database, and the return value is serialized back to HTTP response.
```

Senior version:

```text
I debug Spring Boot requests as a staged pipeline. I locate where the request stops or slows: server threads, filters, DispatcherServlet mapping, argument resolution, controller, service transaction, repository query, DB connection pool, database, serialization, or response write.
```

---

## One Picture To Remember

```text
                    SPRING BOOT REQUEST PIPELINE

Client
  |
  v
[Embedded Server]
  |
  v
[Servlet Request/Response]
  |
  v
[Filter Chain]
  |
  v
[DispatcherServlet]
  |
  v
[HandlerMapping + Argument Resolvers]
  |
  v
[Controller]     -> HTTP boundary
  |
  v
[Service]        -> business + transaction boundary
  |
  v
[Repository]     -> persistence boundary
  |
  v
[Database]
  |
  v
[Response DTO]
  |
  v
[JSON Serialization]
  |
  v
[HTTP Response to Client]
```

Final retention sentence:

> **A Spring Boot request is a pipeline, not a jump: server → filters → DispatcherServlet → controller → service → repository → database → response.**
