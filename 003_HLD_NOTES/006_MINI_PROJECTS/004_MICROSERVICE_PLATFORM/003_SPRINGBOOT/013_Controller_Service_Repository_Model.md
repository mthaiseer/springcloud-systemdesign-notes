# 013_Controller_Service_Repository_Model — The Three-Door Request Model

## Core Mental Model

A Spring Boot application should not be imagined as “many classes calling many classes”.
That becomes hard to remember and harder to debug.

The better mental model is:

> **A request passes through three doors. Each door has exactly one responsibility.**

```text
HTTP Request
    |
    v
+-------------------+
|  Door 1           |
|  Controller       |  translates HTTP into application input
+-------------------+
    |
    v
+-------------------+
|  Door 2           |
|  Service          |  owns business rules and transaction boundary
+-------------------+
    |
    v
+-------------------+
|  Door 3           |
|  Repository       |  talks to database/storage
+-------------------+
    |
    v
Database
```

This chapter teaches exactly one idea:

> **Controller, Service, and Repository are not just folders. They are boundaries that protect your system from mixing transport, business, and persistence concerns.**

If you remember only one sentence:

> **Controller speaks HTTP, Service speaks business, Repository speaks database.**

---

## Why This Exists

Without clear layers, code starts like this:

```java
@PostMapping("/orders")
public ResponseEntity<?> create(@RequestBody OrderRequest request) {
    User user = userRepository.findById(request.userId()).orElseThrow();
    if (user.getBalance().compareTo(request.amount()) < 0) {
        return ResponseEntity.badRequest().body("Insufficient balance");
    }
    user.setBalance(user.getBalance().subtract(request.amount()));
    Order order = new Order(user, request.amount());
    orderRepository.save(order);
    userRepository.save(user);
    return ResponseEntity.ok(order);
}
```

At first this looks simple.
In production, it becomes dangerous.

The controller now knows:

- HTTP details
- validation rules
- business rules
- database queries
- transaction assumptions
- entity structure
- error response format

That means one endpoint is doing too many jobs.

When bugs happen, nobody knows where the responsibility belongs.

```text
Bug: Duplicate order created

Is it because...
  - Controller retried badly?
  - Service missed idempotency?
  - Repository saved twice?
  - Database constraint missing?
  - Transaction boundary wrong?
```

The Controller-Service-Repository model exists to make responsibility visible.

---

## Problem Statement

A backend application must do three different kinds of work:

```text
1. Receive external input
2. Apply business decision
3. Persist or retrieve data
```

These are not the same responsibility.

If you mix them, you get:

```text
Fat Controller
    Controller validates business rules, calls DB, handles transactions

Anemic Service
    Service only passes calls through without owning behavior

Leaky Repository
    Repository contains business decisions and application flow

Hard Testing
    Every test needs HTTP + database + business setup

Poor Debugging
    No clear place to inspect the real cause
```

The goal is not to create layers for beauty.

The goal is to create **debuggable ownership**.

---

## Real World Analogy

Imagine a restaurant.

```text
Customer
   |
   v
Waiter          -> receives order, understands customer language
   |
   v
Chef            -> decides how to prepare food correctly
   |
   v
Store Keeper    -> fetches ingredients from storage
```

The waiter should not cook.
The chef should not directly argue with every customer.
The store keeper should not decide the recipe.

In Spring Boot:

```text
Customer HTTP Request  -> Controller
Recipe Decision        -> Service
Storage Access         -> Repository
```

Bad design is like this:

```text
Waiter receives order,
opens freezer,
cooks food,
calculates cost,
updates inventory,
and prints the bill.
```

It may work for one table.
It collapses when the restaurant becomes busy.

---

## The One Mental Model

Think of the application as a protected core.

```text
Outside World
     |
     | HTTP / JSON / Headers / Status Codes
     v
+-----------------------+
| Controller Layer      |
| Translation Boundary  |
+-----------------------+
     |
     | Commands / DTOs
     v
+-----------------------+
| Service Layer         |
| Business Boundary     |
+-----------------------+
     |
     | Repository calls
     v
+-----------------------+
| Repository Layer      |
| Persistence Boundary  |
+-----------------------+
     |
     | SQL / JPA / Mongo / Redis
     v
Database / Storage
```

Each layer converts one language into another:

| Layer | Speaks To | Language It Understands | Should Not Know |
|---|---|---|---|
| Controller | Client | HTTP, JSON, status codes | SQL, transactions, DB schema |
| Service | Domain/application | Business rules, use cases | HTTP status codes, raw SQL |
| Repository | Database | Queries, persistence | Business workflow, HTTP |

The key word is **boundary**.

A boundary says:

> “Beyond this point, another responsibility begins.”

---

## Correct Responsibility Split

### Controller Responsibility

The controller is an adapter from HTTP to application use case.

It should:

- receive request
- validate request shape
- call service
- convert result to HTTP response

It should not:

- decide business rules deeply
- open transactions manually
- call many repositories directly
- contain SQL or JPA logic
- know entity internals unnecessarily

```text
Controller = HTTP translator
```

### Service Responsibility

The service is the business brain.

It should:

- enforce business rules
- coordinate repositories
- define transaction boundary
- decide application flow
- handle idempotency and consistency rules

It should not:

- return HTTP status codes
- parse headers unless business needs them
- build SQL queries directly
- become a meaningless pass-through

```text
Service = business decision + orchestration
```

### Repository Responsibility

The repository is the persistence gateway.

It should:

- load data
- save data
- expose storage operations
- hide query details

It should not:

- decide business workflow
- call external APIs
- know HTTP requests
- contain use-case orchestration

```text
Repository = database access boundary
```

---

## Rich ASCII Diagram — The Clean Request Path

```text
POST /api/orders
Content-Type: application/json

{
  "userId": 42,
  "productId": 1001,
  "quantity": 2
}

        |
        v
+------------------------------------------------+
| Controller                                     |
|------------------------------------------------|
| - Reads JSON                                   |
| - Validates basic request shape                |
| - Calls orderService.createOrder(command)      |
| - Converts result to HTTP response             |
+------------------------------------------------+
        |
        | CreateOrderCommand(userId, productId, quantity)
        v
+------------------------------------------------+
| Service                                        |
|------------------------------------------------|
| - Checks user exists                           |
| - Checks product exists                        |
| - Checks stock                                 |
| - Calculates price                             |
| - Creates order                                |
| - Decreases stock                              |
| - Commits transaction                          |
+------------------------------------------------+
        |
        | findById(), save(), updateStock()
        v
+------------------------------------------------+
| Repository                                     |
|------------------------------------------------|
| - SELECT user                                  |
| - SELECT product                               |
| - INSERT order                                 |
| - UPDATE stock                                 |
+------------------------------------------------+
        |
        v
+------------------------------------------------+
| Database                                       |
+------------------------------------------------+
```

---

## Java Code Example — Bad Version First

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderController(UserRepository userRepository,
                           ProductRepository productRepository,
                           OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateOrderRequest request) {
        User user = userRepository.findById(request.userId()).orElseThrow();
        Product product = productRepository.findById(request.productId()).orElseThrow();

        if (product.getStock() < request.quantity()) {
            return ResponseEntity.badRequest().body("Out of stock");
        }

        product.decreaseStock(request.quantity());
        Order order = new Order(user, product, request.quantity());

        productRepository.save(product);
        orderRepository.save(order);

        return ResponseEntity.ok(order);
    }
}
```

This code works, but it has the wrong ownership.

```text
Controller is now doing:
  HTTP translation       yes
  Business rule          yes  <- bad
  Persistence access     yes  <- bad
  Transaction thinking   hidden and unsafe
```

The controller has become a mini-application.

---

## Java/Spring Boot Example — Correct Layered Version

### Request DTO

```java
public record CreateOrderRequest(
        Long userId,
        Long productId,
        int quantity
) {}
```

This object belongs near the controller because it represents external input shape.

### Command Object

```java
public record CreateOrderCommand(
        Long userId,
        Long productId,
        int quantity
) {}
```

The command represents application input.
It is not HTTP-specific.

### Response DTO

```java
public record OrderResponse(
        Long orderId,
        Long userId,
        Long productId,
        int quantity,
        String status
) {}
```

The response DTO protects clients from internal entity structure.

---

## Controller Layer

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
        CreateOrderCommand command = new CreateOrderCommand(
                request.userId(),
                request.productId(),
                request.quantity()
        );

        OrderResponse response = orderService.createOrder(command);

        return ResponseEntity.status(201).body(response);
    }
}
```

Controller thinking:

```text
Did I receive valid input shape?
Can I translate HTTP request into a command?
Can I translate service result into HTTP response?
```

Controller should not ask:

```text
How much stock exists?
Which SQL query is needed?
Should I update product first or order first?
Where should transaction start?
```

---

## Service Layer

```java
@Service
public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(UserRepository userRepository,
                        ProductRepository productRepository,
                        OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new BusinessException("User not found"));

        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new BusinessException("Product not found"));

        if (command.quantity() <= 0) {
            throw new BusinessException("Quantity must be positive");
        }

        if (!product.hasEnoughStock(command.quantity())) {
            throw new BusinessException("Out of stock");
        }

        product.decreaseStock(command.quantity());

        Order order = Order.create(user, product, command.quantity());
        Order saved = orderRepository.save(order);

        return new OrderResponse(
                saved.getId(),
                user.getId(),
                product.getId(),
                saved.getQuantity(),
                saved.getStatus().name()
        );
    }
}
```

Service thinking:

```text
What is the use case?
What business rules must hold?
What data do I need?
What should happen atomically?
What should be returned to the application boundary?
```

This is the best place for `@Transactional` because the service owns the business unit of work.

---

## Repository Layer

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
```

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
```

Repository thinking:

```text
How do I load or save data?
What query is needed?
How do I hide persistence details from service?
```

Repository should not contain:

```java
if (product.getStock() < quantity) {
    throw new BusinessException("Out of stock");
}
```

That belongs to service or domain model, not repository.

---

## Entity Example

```java
@Entity
public class Product {

    @Id
    private Long id;

    private String name;
    private int stock;

    protected Product() {
    }

    public boolean hasEnoughStock(int quantity) {
        return stock >= quantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (stock < quantity) {
            throw new IllegalStateException("Not enough stock");
        }
        this.stock -= quantity;
    }

    public Long getId() {
        return id;
    }
}
```

Notice the subtle design:

```text
Service decides the use case.
Entity protects its own invariant.
Repository persists the result.
```

Good design is not always “put all business logic in service”.
A better rule:

```text
Use-case orchestration -> Service
Object invariant       -> Entity/domain object
Persistence operation  -> Repository
HTTP conversion        -> Controller
```

---

## Internal Working — What Spring Actually Does

At runtime, Spring creates beans for each layer.

```text
Application Startup

Spring scans classes
     |
     v
@Controller / @RestController bean created
@Service bean created
@Repository proxy created
     |
     v
Dependencies injected
     |
     v
Application ready
```

For a request:

```text
HTTP request
   |
   v
DispatcherServlet
   |
   v
HandlerMapping finds OrderController#create
   |
   v
Controller method invoked
   |
   v
OrderService proxy invoked
   |
   v
@Transactional interceptor opens transaction
   |
   v
Repository proxy invokes EntityManager
   |
   v
Database SQL executed
   |
   v
Transaction commits
   |
   v
Response returned
```

Detailed version:

```text
Client
  |
  | POST /api/orders
  v
DispatcherServlet
  |
  | finds matching handler method
  v
OrderController.create()
  |
  | builds command
  v
OrderService proxy
  |
  | TransactionInterceptor begins transaction
  v
OrderService.createOrder()
  |
  | calls repositories
  v
Spring Data repository proxy
  |
  | delegates to JPA EntityManager
  v
Hibernate
  |
  | generates SQL
  v
Database
```

The service may look like a normal Java object, but in Spring it is often reached through a proxy.
That matters because annotations like `@Transactional` are applied around service method calls.

---

## Step-by-Step Dry Run — Normal Flow

Input:

```json
{
  "userId": 42,
  "productId": 1001,
  "quantity": 2
}
```

Database before:

```text
users
+----+---------+
| id | name    |
+----+---------+
| 42 | Mohamed |
+----+---------+

products
+------+----------+-------+
| id   | name     | stock |
+------+----------+-------+
| 1001 | Keyboard | 10    |
+------+----------+-------+
```

Flow:

```text
1. Controller receives JSON.
2. Controller builds CreateOrderCommand(42, 1001, 2).
3. Controller calls orderService.createOrder(command).
4. Transaction opens at service boundary.
5. Service loads user 42.
6. Service loads product 1001.
7. Service checks quantity > 0.
8. Service checks stock >= 2.
9. Product stock changes from 10 to 8.
10. Service creates Order entity.
11. Repository saves Order.
12. Transaction commits.
13. Controller returns 201 Created.
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
+----+---------+------------+----------+---------+
| id | user_id | product_id | quantity | status  |
+----+---------+------------+----------+---------+
| 1  | 42      | 1001       | 2        | CREATED |
+----+---------+------------+----------+---------+
```

---

## Dry Run — Failure Flow

Input:

```json
{
  "userId": 42,
  "productId": 1001,
  "quantity": 20
}
```

Database:

```text
Product stock = 8
Requested quantity = 20
```

Flow:

```text
1. Controller receives request.
2. Controller builds command.
3. Service transaction starts.
4. Service loads user.
5. Service loads product.
6. Service checks stock.
7. Stock is insufficient.
8. Service throws BusinessException("Out of stock").
9. Transaction rolls back.
10. Global exception handler converts exception to HTTP 400.
11. Client receives error response.
```

Important:

```text
Controller should not know stock logic.
Repository should not decide out-of-stock behavior.
Service owns the use-case failure.
```

---

## Failure Handling With Controller Advice

```java
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        ErrorResponse body = new ErrorResponse(ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }
}
```

```java
public record ErrorResponse(String message) {}
```

This keeps controllers clean.

Bad:

```java
try {
    return ResponseEntity.ok(service.createOrder(command));
} catch (BusinessException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
}
```

Better:

```text
Controller handles happy-path translation.
Service throws meaningful business exceptions.
ControllerAdvice maps exceptions to HTTP responses.
```

---

## Production Scale Example

Imagine an order service handling 5,000 requests per second.

```text
Mobile App / Web
      |
      v
API Gateway
      |
      v
Order Service
      |
      +--> Controller: parse request
      |
      +--> Service: validate stock, pricing, idempotency, transaction
      |
      +--> Repository: Postgres reads/writes
      |
      +--> Kafka Outbox: publish OrderCreated event
```

At scale, clear boundaries matter more, not less.

```text
Controller layer concerns:
  - request payload size
  - auth principal extraction
  - request validation
  - response codes

Service layer concerns:
  - duplicate request prevention
  - transaction boundary
  - stock consistency
  - retry-safe behavior
  - event publishing decision

Repository layer concerns:
  - query performance
  - indexes
  - locking query
  - connection pool usage
```

If these are mixed, performance debugging becomes painful.

Example production symptom:

```text
p99 latency increased from 120ms to 900ms
```

With clean layers, investigation is easier:

```text
Controller metrics: request parsing OK
Service metrics: business flow spends time in reserveStock()
Repository metrics: SELECT FOR UPDATE waits increased
Database metrics: lock contention on product row
```

Without clean layers:

```text
Everything is inside controller.
Good luck.
```

---

## Production Failure Story

A team built a payment endpoint.

The controller did everything:

```text
@PostMapping("/pay")
  validate request
  check user balance
  insert payment
  call external payment provider
  update order status
  send email
  return response
```

It worked in staging.
In production, users started reporting duplicate payments.

Root cause:

```text
Mobile client retried request after timeout.
Controller had no idempotency check.
Payment provider call succeeded once.
Database update timed out.
Retry executed whole controller again.
Second external payment happened.
```

Why the design failed:

```text
Controller owned workflow.
No clear service-level use case.
No transaction boundary around local state.
No idempotency key model.
No repository uniqueness constraint.
```

Fixed design:

```text
Controller:
  read Idempotency-Key header
  create command
  call paymentService.pay(command)

Service:
  check idempotency record
  start transaction
  create payment attempt
  call payment gateway safely or via outbox/saga
  save final state

Repository:
  unique index on idempotency key
```

The lesson:

> **When business workflow lives in the controller, production failures become invisible until money is lost.**

---

## Debugging Mindset

When a bug happens, ask: which boundary owns this bug?

### Bug: Wrong HTTP status code

```text
Likely owner: Controller / ControllerAdvice
```

### Bug: Business rule not applied

```text
Likely owner: Service or domain entity
```

### Bug: Slow query

```text
Likely owner: Repository / database index
```

### Bug: Transaction not rolling back

```text
Likely owner: Service boundary / @Transactional usage
```

### Bug: Entity returned with sensitive fields

```text
Likely owner: Controller response mapping
```

### Bug: Duplicate record under retries

```text
Likely owner: Service idempotency + Repository constraint
```

Use this investigation map:

```text
Symptom
  |
  +-- Is HTTP response wrong?
  |       -> Controller / exception handler
  |
  +-- Is decision wrong?
  |       -> Service / domain rule
  |
  +-- Is data wrong?
  |       -> Repository / transaction / database
  |
  +-- Is performance bad?
          -> measure each boundary separately
```

---

## Common Misconceptions

### Misconception 1: “Controller-Service-Repository is just folder structure”

No.

It is a responsibility model.

Bad:

```text
controller package
service package
repository package
```

but all logic still mixed.

Good:

```text
Controller only translates HTTP.
Service owns use case.
Repository owns persistence.
```

### Misconception 2: “Service should only call repository”

A service that only forwards calls is usually not useful.

Bad:

```java
public Product getProduct(Long id) {
    return productRepository.findById(id).orElseThrow();
}
```

This may be acceptable for very simple reads, but if every service is like this, your service layer is just ceremony.

Good services own behavior:

```text
createOrder
cancelOrder
reserveStock
refundPayment
activateSubscription
```

### Misconception 3: “Repositories can contain business logic”

Repositories can contain query logic.
They should not contain workflow decisions.

Acceptable:

```java
List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
```

Dangerous:

```java
void cancelOrderIfUserIsPremiumAndWithinSevenDays(...)
```

That is business policy.

### Misconception 4: “Entities should never contain logic”

Entities can protect their own invariants.

Good:

```java
product.decreaseStock(quantity);
```

Bad:

```java
product.setStock(product.getStock() - quantity);
```

The first version protects the object.
The second version allows anyone to corrupt state.

### Misconception 5: “DTOs are always unnecessary”

Returning entities directly may leak internal structure.

Bad:

```java
return userEntity;
```

Possible leak:

```text
passwordHash
internalRole
deletedFlag
audit fields
lazy-loaded relations
```

DTOs give control over the API contract.

---

## Performance Considerations

Layering itself is not the performance problem.
Bad boundaries are.

Common performance mistakes:

```text
1. Controller calls repository many times in a loop
2. Service loads huge entity graphs unnecessarily
3. Repository causes N+1 queries
4. DTO mapping triggers lazy loading accidentally
5. Transaction boundary is too large
6. External API call happens inside DB transaction
```

Better pattern:

```text
Controller
  -> one service method
     -> carefully chosen repository queries
        -> return compact DTO/result
```

Example problem:

```java
List<Order> orders = orderRepository.findByUserId(userId);
for (Order order : orders) {
    System.out.println(order.getProduct().getName());
}
```

Possible N+1:

```text
1 query for orders
N queries for products
```

Repository-level fix:

```java
@Query("select o from Order o join fetch o.product where o.user.id = :userId")
List<Order> findByUserIdWithProduct(Long userId);
```

Service chooses the correct repository method for the use case.

---

## Scalability Considerations

At higher scale, the boundaries become operational boundaries.

```text
Controller scaling:
  - request validation cost
  - serialization/deserialization
  - payload size

Service scaling:
  - transaction duration
  - lock contention
  - retry behavior
  - idempotency
  - domain orchestration

Repository scaling:
  - query plans
  - indexes
  - connection pool
  - read/write split
  - batch operations
```

Senior-engineer thinking:

```text
Do not ask only:
  “Where do I put this code?”

Ask:
  “Which boundary should own this responsibility under failure and scale?”
```

---

## Testing Strategy

Clear layers make testing easier.

### Controller Test

Focus:

```text
HTTP request -> HTTP response
```

Test:

```text
Given invalid JSON
When POST /api/orders
Then 400 Bad Request
```

Mock service.
Do not need real database.

### Service Test

Focus:

```text
Business rule -> correct decision
```

Test:

```text
Given product stock is 1
When user orders quantity 2
Then BusinessException is thrown
```

Mock repositories or use test database depending on need.

### Repository Test

Focus:

```text
Query correctness
```

Test:

```text
Given orders exist in database
When findByUserIdWithProduct is called
Then products are loaded correctly
```

Use database slice tests.

Testing map:

```text
Controller test  -> web contract
Service test     -> business behavior
Repository test  -> persistence correctness
```

---

## Interview Q&A

### Q1. Why do we use Controller-Service-Repository layers?

Strong answer:

> We use them to separate responsibilities. The controller handles HTTP translation, the service owns business use cases and transaction boundaries, and the repository hides persistence details. This makes code easier to test, debug, scale, and modify because each layer has a clear reason to change.

### Q2. Should a controller call a repository directly?

Strong answer:

> Usually no for business use cases. A controller calling a repository directly skips the service boundary, so business rules and transaction orchestration may leak into the web layer. For very simple read-only admin endpoints it may be tolerated, but in production systems I prefer controller to service because the service protects application behavior.

### Q3. Where should `@Transactional` go?

Strong answer:

> Usually on the service method because the service represents a complete business unit of work. A transaction should cover the use case, not just a single repository call and not the HTTP layer.

### Q4. What belongs in a repository?

Strong answer:

> Repository should contain persistence operations: loading, saving, and query methods. It can optimize queries, but it should not own business workflow decisions like whether an order can be cancelled.

### Q5. Is DTO mapping necessary?

Strong answer:

> DTO mapping protects API contracts from internal entity design. It avoids leaking sensitive fields, lazy-loading problems, and accidental breaking changes when entity structure changes.

### Q6. What is a fat controller?

Strong answer:

> A fat controller is a controller that contains business logic, repository calls, transaction decisions, and response handling all together. It is hard to test and dangerous in production because workflow behavior is hidden inside HTTP code.

### Q7. Can service contain too much logic?

Strong answer:

> Yes. A service should orchestrate a use case, but domain invariants can live inside domain objects. For example, `Product.decreaseStock()` can protect stock from going negative, while `OrderService.createOrder()` coordinates the full order creation flow.

---

## Production Checklist

Use this checklist when reviewing Spring Boot code.

```text
Controller
[ ] Does it only handle HTTP input/output?
[ ] Does it avoid direct repository calls for business flows?
[ ] Does it return DTOs instead of entities?
[ ] Does it delegate errors to ControllerAdvice?

Service
[ ] Does it represent a real use case?
[ ] Does it own business rules?
[ ] Is @Transactional placed at the right boundary?
[ ] Does it avoid HTTP-specific response codes?
[ ] Does it coordinate repositories cleanly?

Repository
[ ] Does it hide query details?
[ ] Are queries optimized for the use case?
[ ] Are indexes considered?
[ ] Does it avoid business workflow decisions?

Cross-cutting
[ ] Are DTOs separated from entities?
[ ] Are exceptions meaningful?
[ ] Are retries/idempotency handled at service level?
[ ] Are tests aligned by layer?
```

---

## One-Page Cheat Sheet

```text
Controller-Service-Repository Model
===================================

Controller = HTTP adapter
-------------------------
Receives request
Validates request shape
Creates command
Calls service
Returns HTTP response
Does NOT contain business workflow
Does NOT call DB directly for use cases

Service = business brain
------------------------
Owns use case
Applies business rules
Coordinates repositories
Defines transaction boundary
Handles idempotency/retry-safe behavior
Does NOT return HTTP status codes
Does NOT build SQL directly

Repository = persistence gateway
--------------------------------
Loads data
Saves data
Executes queries
Hides persistence implementation
Does NOT decide business policy
Does NOT know HTTP

Best Rule
---------
Controller speaks HTTP.
Service speaks business.
Repository speaks database.

Debugging Rule
--------------
Wrong response?      Controller / ControllerAdvice
Wrong decision?      Service / domain
Wrong data/query?    Repository / DB
Wrong transaction?   Service boundary
Wrong performance?   Measure each boundary
```

---

## Last-Minute Interview Revision

Before interview, remember:

```text
Do not say:
  “We use layers because it is standard.”

Say:
  “We use layers to create responsibility boundaries.”
```

Best sentence:

> In Spring Boot, the controller should translate HTTP, the service should execute the business use case and transaction, and the repository should hide persistence details.

Senior version:

> The real value of Controller-Service-Repository is not folder organization. It is failure isolation. When a production issue happens, clear boundaries tell us whether the bug belongs to request mapping, business orchestration, transaction handling, or persistence.

---

## One Picture To Remember

```text
                 OUTSIDE WORLD
                      |
                      | HTTP / JSON / Headers
                      v
        +-----------------------------+
        |        CONTROLLER           |
        |-----------------------------|
        | Translate web request       |
        | Return web response         |
        +-----------------------------+
                      |
                      | Command / DTO
                      v
        +-----------------------------+
        |          SERVICE            |
        |-----------------------------|
        | Business rules              |
        | Use-case orchestration      |
        | Transaction boundary        |
        +-----------------------------+
                      |
                      | Repository calls
                      v
        +-----------------------------+
        |        REPOSITORY           |
        |-----------------------------|
        | Query database              |
        | Save/load data              |
        +-----------------------------+
                      |
                      | SQL / JPA
                      v
                  DATABASE
```

Remember the three doors:

```text
Door 1: Controller  -> Is this valid HTTP/application input?
Door 2: Service     -> Is this business action allowed and consistent?
Door 3: Repository  -> How do I fetch or persist the data?
```

Final retention sentence:

> **A clean Spring Boot request is not random method calling; it is a controlled handoff through three responsibility doors.**
