# 011_Request_Flow_End_To_End.md

# MiniSpringBoot Deep Production Mode
# 011 - Request Flow End To End

## One Core Mental Model

> A Spring Boot web request is not a direct call from browser to controller.
>
> A Spring Boot web request is a **pipeline handoff**.
>
> Each layer receives the request, adds context, makes one decision, and hands it to the next layer until a response is written back.

This chapter teaches exactly ONE core mental model:

```text
HTTP request flow
        =
Pipeline of responsibility handoffs
```

Do not memorize random Spring MVC classes.

Think like this:

```text
Client
  -> Network
  -> Embedded Server
  -> Servlet Container
  -> Filter Chain
  -> DispatcherServlet
  -> Handler Mapping
  -> Handler Adapter
  -> Controller
  -> Service
  -> Repository
  -> Database
  -> Response Converters
  -> Filters again
  -> Client
```

If you remember only one picture, remember this:

```text
Raw HTTP bytes
     |
     v
Tomcat thread
     |
     v
Filter chain
     |
     v
DispatcherServlet
     |
     v
Controller method
     |
     v
Service / DB / external systems
     |
     v
JSON response written to socket
```

The request survives because each layer follows a simple contract:

```text
Receive request context.
Do exactly one responsibility.
Pass control forward.
Return response backward.
```

---

## Why This Exists

A web application receives messy external input.

Example request:

```http
POST /api/orders HTTP/1.1
Host: shop.example.com
Authorization: Bearer eyJ...
Content-Type: application/json
X-Request-Id: req-123

{
  "productId": 42,
  "quantity": 2
}
```

A controller method wants clean Java objects:

```java
@PostMapping("/orders")
public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
    return orderService.create(request);
}
```

Many things must happen between raw HTTP and this Java method:

```text
Parse socket bytes
Decode HTTP headers
Choose worker thread
Run security filters
Attach tracing context
Find matching controller method
Deserialize JSON
Validate input
Call business code
Handle exceptions
Serialize response JSON
Write bytes to client
```

Without a disciplined pipeline, every controller would need to know everything:

```text
Authentication
Authorization
Logging
Metrics
JSON parsing
Validation
Exception formatting
CORS
Compression
Character encoding
Routing
Database transaction boundaries
```

That would be impossible to maintain.

Spring Boot solves this by turning request handling into a layered pipeline.

The key design:

```text
Generic infrastructure handles generic concerns.
Your controller handles business intent.
```

---

## Problem Statement

Consider this endpoint:

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

Important question:

```text
How does an HTTP request become this Java method call?
```

The method has no socket code.

It does not manually parse JSON.

It does not manually find `/api/orders`.

It does not manually write HTTP response bytes.

Still, the browser receives:

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "orderId": 1001,
  "status": "CREATED"
}
```

The hidden mechanism is the Spring MVC request pipeline.

One-line answer:

```text
Tomcat receives the request, filters wrap it, DispatcherServlet routes it, handler adapters invoke the controller, converters translate body data, and the response travels back through the same pipeline.
```

---

## Mental Model

Think of an airport journey.

A passenger does not directly walk from street to airplane.

They pass through stations:

```text
Entrance
  -> Security gate
  -> Check-in counter
  -> Immigration
  -> Boarding gate
  -> Aircraft
```

Each station does one job:

```text
Security checks danger.
Check-in maps passenger to flight.
Immigration verifies identity.
Boarding gate verifies seat.
Aircraft performs actual travel.
```

Spring Boot request flow is similar.

```text
Network accepts connection.
Tomcat assigns thread.
Filters inspect/wrap request.
DispatcherServlet finds route.
HandlerAdapter invokes controller.
Controller delegates business work.
MessageConverter writes JSON.
Response travels back.
```

The controller is not the airport.

The controller is only the boarding gate for business logic.

Mental hook:

```text
A request is a package moving through a conveyor belt.
Each machine reads labels, adds context, and forwards it.
```

---

## Core Concepts

### Client

The caller of your API.

It may be:

```text
Browser
Mobile app
Another microservice
Postman
Load balancer health check
Kubernetes probe
```

### TCP Connection

Before HTTP can be processed, there is a network connection.

For HTTPS, TLS termination may happen at:

```text
Load balancer
Ingress controller
API Gateway
Application server
```

### Embedded Server

Spring Boot commonly starts embedded Tomcat.

Conceptually:

```text
Spring Boot application
  contains
Tomcat server
```

You do not deploy a WAR into external Tomcat in the common Boot model.

Boot starts the server inside your process.

### Servlet Container

Tomcat implements the Servlet model.

It converts HTTP requests into:

```text
HttpServletRequest
HttpServletResponse
```

### Worker Thread

Tomcat assigns a request to a worker thread.

Example:

```text
http-nio-8080-exec-17
```

This thread carries request execution until completion in traditional Spring MVC.

### Filter Chain

Filters run before and after the main servlet.

Common filters:

```text
Security filter
CORS filter
Logging filter
Tracing filter
Compression filter
Character encoding filter
```

Filter mental model:

```text
Before DispatcherServlet:
    inspect / reject / wrap request
After DispatcherServlet:
    inspect / modify / log response
```

### DispatcherServlet

The central Spring MVC front controller.

It receives all matching web requests and coordinates routing.

Mental model:

```text
DispatcherServlet = traffic controller for Spring MVC
```

### HandlerMapping

Finds which controller method should handle the request.

Example:

```text
POST /api/orders
    -> OrderController.createOrder()
```

### HandlerAdapter

Knows how to invoke the chosen handler.

For annotated controllers, this usually means:

```text
RequestMappingHandlerAdapter
```

It handles:

```text
@RequestBody
@PathVariable
@RequestParam
@Valid
ResponseEntity
```

### Argument Resolvers

Convert request data into method parameters.

Examples:

```java
@PathVariable Long id
@RequestParam String sort
@RequestHeader String requestId
@RequestBody CreateOrderRequest body
Principal user
```

### HttpMessageConverter

Converts between HTTP body bytes and Java objects.

Common example:

```text
JSON body <-> Java DTO
```

With Jackson:

```text
application/json -> MappingJackson2HttpMessageConverter
```

### Controller

Entry point for application-specific web intent.

Controller should:

```text
Receive validated request model
Call service
Return response model
```

Controller should not:

```text
Contain database transaction logic
Contain complex business rules
Call ten repositories directly
Know transport internals deeply
```

### Service

Business operation boundary.

Often transaction starts here, not in controller.

```java
@Transactional
public OrderResponse createOrder(CreateOrderRequest request) { ... }
```

### Repository

Persistence boundary.

It talks to database through JPA, JDBC, MyBatis, etc.

### Exception Resolver

Converts exceptions into HTTP responses.

Example:

```text
MethodArgumentNotValidException -> 400 Bad Request
AccessDeniedException           -> 403 Forbidden
EntityNotFoundException         -> 404 Not Found
Unhandled RuntimeException      -> 500 Internal Server Error
```

### Response Writer

Converts Java response objects into HTTP response bytes.

Example:

```java
OrderResponse(orderId=1001, status="CREATED")
```

becomes:

```json
{"orderId":1001,"status":"CREATED"}
```

---

## Internal Architecture

High-level architecture:

```text
                 Client
                   |
                   v
            Load Balancer / Ingress
                   |
                   v
          Spring Boot Application
+---------------------------------------------------+
|                                                   |
|  Embedded Tomcat                                  |
|      |                                            |
|      v                                            |
|  Servlet Filter Chain                             |
|      |                                            |
|      v                                            |
|  DispatcherServlet                                |
|      |                                            |
|      +--> HandlerMappings                         |
|      |       |                                    |
|      |       v                                    |
|      |   Controller method found                  |
|      |                                            |
|      +--> HandlerAdapter                          |
|              |                                    |
|              +--> ArgumentResolvers               |
|              +--> HttpMessageConverters           |
|              +--> Validator                       |
|              +--> Controller invocation           |
|                                                   |
|  Controller -> Service -> Repository -> Database  |
|                                                   |
+---------------------------------------------------+
```

The hidden center:

```text
DispatcherServlet
```

It is the coordinator that says:

```text
Who should handle this request?
How do I invoke it?
How do I convert the result?
How do I handle exceptions?
```

---

## Internal Working

Spring MVC request lifecycle can be understood in eight phases:

```text
1. Connection accepted
2. Request parsed
3. Filter chain executed
4. DispatcherServlet invoked
5. Handler selected
6. Controller method invoked
7. Response created
8. Response travels back and connection completes
```

### Phase 1 - Connection Accepted

A client sends request bytes to the application port.

```text
Client socket -> server socket on port 8080
```

Tomcat accepts the connection.

For HTTP/1.1 keep-alive, multiple requests may reuse one TCP connection.

### Phase 2 - Request Parsed

Tomcat parses raw bytes into servlet objects:

```text
Request line
Headers
Query parameters
Body stream
Cookies
```

Conceptual conversion:

```text
Raw bytes
  -> HttpServletRequest
  -> HttpServletResponse
```

### Phase 3 - Filter Chain Executed

Tomcat runs filters in configured order.

```text
Filter A
  -> Filter B
      -> Filter C
          -> DispatcherServlet
      <- Filter C after
  <- Filter B after
<- Filter A after
```

Important:

```text
Filters can stop the request before controller.
```

Example:

```text
Invalid JWT -> Security filter returns 401
Controller is never called
```

### Phase 4 - DispatcherServlet Invoked

If filters allow the request, control reaches DispatcherServlet.

DispatcherServlet asks:

```text
Which handler should process this request?
```

### Phase 5 - Handler Selected

HandlerMapping checks registered mappings.

Example mapping table:

```text
GET  /api/orders/{id} -> OrderController.getOrder(id)
POST /api/orders      -> OrderController.createOrder(request)
GET  /actuator/health -> Actuator health endpoint
```

It matches using:

```text
HTTP method
Path pattern
Consumes content type
Produces content type
Headers / params conditions
```

### Phase 6 - Controller Method Invoked

HandlerAdapter prepares arguments.

For this method:

```java
@PostMapping
public ResponseEntity<OrderResponse> createOrder(
        @Valid @RequestBody CreateOrderRequest request) { ... }
```

Spring does:

```text
Read request body stream
Choose JSON message converter
Deserialize JSON into CreateOrderRequest
Run validation
Call method using reflection/invocation infrastructure
```

### Phase 7 - Response Created

Controller returns Java object:

```java
ResponseEntity.status(CREATED).body(orderResponse)
```

Spring converts it:

```text
status = 201
headers = Content-Type: application/json
body = JSON bytes
```

### Phase 8 - Response Travels Back

Control unwinds:

```text
Controller returns
HandlerAdapter returns
DispatcherServlet returns
Filters after-phase run
Tomcat writes response bytes
Client receives response
```

---

## Step-by-Step Flow

Successful POST request:

```text
1. Client sends POST /api/orders.

2. Load balancer forwards request to a Spring Boot pod.

3. Tomcat accepts the connection.

4. Tomcat assigns worker thread http-nio-8080-exec-17.

5. Tomcat parses HTTP request into HttpServletRequest.

6. Filter chain starts.

7. Request ID filter attaches correlation id.

8. Security filter validates JWT.

9. CORS filter checks origin if browser request.

10. DispatcherServlet receives request.

11. HandlerMapping matches POST /api/orders.

12. HandlerAdapter prepares to invoke OrderController.createOrder().

13. @RequestBody resolver reads JSON body.

14. Jackson converts JSON into CreateOrderRequest.

15. Bean Validation validates fields.

16. Controller method is called.

17. Controller calls orderService.createOrder().

18. Service executes business rules.

19. Transaction starts at service boundary if @Transactional exists.

20. Repository writes to database.

21. Service returns OrderResponse.

22. Controller wraps it in ResponseEntity 201.

23. MessageConverter serializes OrderResponse to JSON.

24. DispatcherServlet completes.

25. Filters run after-phase for logging/metrics.

26. Tomcat writes HTTP response bytes.

27. Client receives 201 Created.
```

One-line lifecycle:

```text
Accept -> Parse -> Filter -> Dispatch -> Map -> Adapt -> Invoke -> Convert -> Write
```

---

## Deep Walkthrough Example

### Business Case: Create Order

Request:

```http
POST /api/orders HTTP/1.1
Content-Type: application/json
Authorization: Bearer valid-token
X-Request-Id: req-789

{
  "productId": 42,
  "quantity": 2
}
```

Controller:

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

DTO:

```java
public record CreateOrderRequest(
        @NotNull Long productId,
        @Min(1) int quantity
) {}
```

Service:

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        inventoryRepository.reserve(request.productId(), request.quantity());
        Order order = orderRepository.save(new Order(request.productId(), request.quantity()));
        return new OrderResponse(order.getId(), "CREATED");
    }
}
```

Internal flow:

```text
HTTP body bytes
    |
    v
Jackson ObjectMapper
    |
    v
CreateOrderRequest(productId=42, quantity=2)
    |
    v
Bean Validation
    |
    v
OrderController.createOrder(request)
    |
    v
OrderService proxy
    |
    v
TransactionInterceptor
    |
    v
OrderService real method
    |
    v
Repositories
    |
    v
PostgreSQL
    |
    v
OrderResponse(orderId=1001, status=CREATED)
    |
    v
Jackson ObjectMapper
    |
    v
JSON bytes
```

The controller sees clean Java.

The client sees HTTP.

Spring MVC performs the translation.

---

## Rich ASCII Diagrams

### Diagram 1 - Full End-To-End Request Conveyor

```text
+--------+     +------------+     +---------+     +------------+
| Client | --> | LB/Ingress | --> | Tomcat  | --> | Filters    |
+--------+     +------------+     +---------+     +------------+
                                                       |
                                                       v
+----------+   +------------+   +------------+   +-------------------+
| Database |<--| Repository |<--| Service    |<--| Controller Method |
+----------+   +------------+   +------------+   +-------------------+
                                                       ^
                                                       |
                                             +--------------------+
                                             | DispatcherServlet  |
                                             +--------------------+
```

### Diagram 2 - DispatcherServlet Internals

```text
DispatcherServlet
      |
      +--> HandlerMapping
      |       |
      |       v
      |   Finds OrderController.createOrder()
      |
      +--> HandlerAdapter
      |       |
      |       +--> ArgumentResolvers
      |       +--> MessageConverters
      |       +--> Validator
      |       +--> Invoke controller
      |
      +--> ExceptionResolvers if failure
      |
      +--> View/Response handling
```

### Diagram 3 - Filter Chain Onion

```text
Request enters
    |
    v
+-------------------------+
| RequestIdFilter before  |
|  +-------------------+  |
|  | Security before   |  |
|  |  +-------------+  |  |
|  |  | CORS before |  |  |
|  |  |     |       |  |  |
|  |  |     v       |  |  |
|  |  | Dispatcher  |  |  |
|  |  |     |       |  |  |
|  |  | CORS after  |  |  |
|  |  +-------------+  |  |
|  | Security after    |  |
|  +-------------------+  |
| RequestIdFilter after   |
+-------------------------+
    |
    v
Response leaves
```

### Diagram 4 - Controller Argument Resolution

```text
Controller method:

createOrder(@Valid @RequestBody CreateOrderRequest request,
            @RequestHeader("X-Request-Id") String requestId)

Spring resolves:

HTTP body stream ----------------> @RequestBody resolver
                                      |
                                      v
                                  Jackson JSON -> DTO

HTTP header X-Request-Id --------> @RequestHeader resolver
                                      |
                                      v
                                  String requestId

DTO -----------------------------> Validator
                                      |
                                      v
                                  valid or exception
```

### Diagram 5 - Response Conversion

```text
Controller returns:

ResponseEntity<OrderResponse>
    status = 201
    body   = OrderResponse(1001, CREATED)

        |
        v
HttpMessageConverter
        |
        v
JSON bytes:
{"orderId":1001,"status":"CREATED"}

        |
        v
HttpServletResponse
        |
        v
Socket write
```

### Diagram 6 - Normal Response Path And Error Path

```text
Normal path:

Request -> Filters -> Dispatcher -> Controller -> Service -> DB
                                                   |
                                                   v
Response <- Filters <- Dispatcher <- JSON converter <- return object

Error path:

Request -> Filters -> Dispatcher -> Controller -> Service throws
                                      |
                                      v
                              ExceptionResolver
                                      |
                                      v
Response <- Filters <- Dispatcher <- Error JSON
```

---

## Data Structures & Algorithms Used

Spring request flow is not about hard algorithms.

It is about routing, ordered chains, maps, adapters, and object conversion.

### 1. Thread Pool

Tomcat uses worker threads.

Conceptual structure:

```text
Request queue
    |
    v
Worker thread pool
    |
    +--> http-nio-8080-exec-1
    +--> http-nio-8080-exec-2
    +--> http-nio-8080-exec-3
```

Production formula:

```text
Concurrent requests ≈ RPS × latency
```

Example:

```text
500 RPS × 200 ms = 100 concurrent requests
```

If blocking MVC uses one thread per active request, thread pool sizing matters.

### 2. Ordered Filter Chain

Filters are executed in order.

Conceptual structure:

```java
List<Filter> filters = [requestIdFilter, securityFilter, corsFilter];
```

Algorithm:

```text
Call filter[0]
  filter[0] calls chain.doFilter()
    Call filter[1]
      filter[1] calls chain.doFilter()
        Call filter[2]
          filter[2] calls DispatcherServlet
```

This behaves like nested function calls.

### 3. Handler Mapping Registry

Spring keeps mappings from request patterns to controller methods.

Conceptual map:

```text
POST /api/orders      -> OrderController#createOrder
GET  /api/orders/{id} -> OrderController#getOrder
DELETE /api/orders/{id} -> OrderController#deleteOrder
```

Matching considers:

```text
Path
HTTP method
Consumes
Produces
Headers
Params
```

### 4. Adapter Pattern

DispatcherServlet does not know how to invoke every possible handler type.

It asks HandlerAdapter:

```text
Can you invoke this handler?
```

For annotated controllers:

```text
RequestMappingHandlerAdapter
```

This is why Spring MVC is extensible.

### 5. Chain Of Responsibility

Filters and exception resolvers use chain-like behavior.

Example exception resolvers:

```text
ExceptionHandlerExceptionResolver
ResponseStatusExceptionResolver
DefaultHandlerExceptionResolver
```

Each resolver gets a chance.

### 6. Reflection / Method Invocation

Controller methods are invoked dynamically.

Spring knows:

```text
Bean instance
Method reference
Resolved arguments
```

Then calls:

```text
method.invoke(controllerBean, args)
```

Conceptually, not something you manually write.

### 7. Serialization

Jackson maps JSON fields to Java fields and Java fields to JSON.

Conceptually:

```text
JSON object keys -> DTO constructor parameters / fields
Java object fields -> JSON object keys
```

---

## Java Code Example With Execution Explanation

### Controller

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }
}
```

### Service

```java
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return new ProductResponse(product.getId(), product.getName(), product.getPrice());
    }
}
```

### Exception

```java
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product not found: " + id);
    }
}
```

### Global Exception Handler

```java
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("PRODUCT_NOT_FOUND", ex.getMessage()));
    }
}
```

### Internal Execution For Success

Request:

```http
GET /api/products/10
```

Flow:

```text
Tomcat parses request
Filter chain runs
DispatcherServlet asks HandlerMapping
GET /api/products/{id} matches ProductController.getProduct
@PathVariable resolver converts "10" to Long 10
Controller calls ProductService
Repository finds product
Controller returns ProductResponse
Jackson serializes response
HTTP 200 returned
```

### Internal Execution For Failure

Request:

```http
GET /api/products/999
```

Flow:

```text
Controller calls ProductService
Repository returns empty
Service throws ProductNotFoundException
Exception escapes controller
DispatcherServlet asks exception resolvers
@RestControllerAdvice handler matches exception
ApiError is returned
Jackson serializes error
HTTP 404 returned
```

Important mental model:

```text
Exceptions are also part of the request pipeline.
They do not randomly become HTTP responses.
Exception resolvers translate them.
```

---

## Spring Boot Example With Internal Flow

### Request DTO

```java
public record CreateUserRequest(
        @NotBlank String name,
        @Email String email
) {}
```

### Response DTO

```java
public record CreateUserResponse(
        Long id,
        String name,
        String email
) {}
```

### Controller

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        CreateUserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

### Service

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());

        User saved = userRepository.save(user);

        return new CreateUserResponse(saved.getId(), saved.getName(), saved.getEmail());
    }
}
```

### Debug Filter

```java
@Component
public class RequestDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        long start = System.currentTimeMillis();
        String thread = Thread.currentThread().getName();

        System.out.println("REQUEST START " + request.getMethod() + " "
                + request.getRequestURI() + " thread=" + thread);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;
            System.out.println("REQUEST END status=" + response.getStatus()
                    + " durationMs=" + duration
                    + " thread=" + Thread.currentThread().getName());
        }
    }
}
```

### Internal Debug Output

Expected mental output:

```text
REQUEST START POST /api/users thread=http-nio-8080-exec-5
Controller createUser called
Service createUser called
Repository save called
REQUEST END status=201 durationMs=42 thread=http-nio-8080-exec-5
```

This shows the request stayed on one worker thread in normal blocking MVC.

### Internal Flow Diagram

```text
POST /api/users
    |
    v
RequestDebugFilter before
    |
    v
Spring Security filters
    |
    v
DispatcherServlet
    |
    v
UserController.createUser()
    |
    v
UserService proxy
    |
    v
@Transactional starts
    |
    v
UserRepository.save()
    |
    v
Database insert
    |
    v
Transaction commit
    |
    v
Jackson serializes response
    |
    v
RequestDebugFilter after
    |
    v
HTTP 201
```

---

## Production Architecture Example

### Kubernetes Production Flow

```text
Client
  |
  v
DNS
  |
  v
Cloud Load Balancer
  |
  v
Kubernetes Ingress Controller
  |
  v
Service ClusterIP
  |
  v
Spring Boot Pod
  |
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
Controller -> Service -> Repository
  |
  v
PostgreSQL / Redis / Kafka
```

Inside a pod:

```text
Spring Boot JVM
+-----------------------------------------------------+
|                                                     |
|  Tomcat port 8080                                   |
|      |                                              |
|      v                                              |
|  http-nio worker thread                             |
|      |                                              |
|      v                                              |
|  Filters: requestId, security, metrics, tracing     |
|      |                                              |
|      v                                              |
|  DispatcherServlet                                  |
|      |                                              |
|      v                                              |
|  Controller -> Service -> Repository                |
|                                                     |
+-----------------------------------------------------+
```

Production request context usually carries:

```text
Request ID
Trace ID
User identity
Tenant ID
Locale
Authorization scopes
Transaction context
Security context
MDC logging context
```

Many of these are attached early in filters and used later by services and logs.

---

## Sequence Diagram ASCII

### Successful POST Request

```text
Client      Tomcat      Filters      Dispatcher     HandlerAdapter    Controller    Service      DB
  |           |           |              |                |              |           |          |
  | POST      |           |              |                |              |           |          |
  |---------->|           |              |                |              |           |          |
  |           | parse     |              |                |              |           |          |
  |           |---------->|              |                |              |           |          |
  |           |           | before       |                |              |           |          |
  |           |           |------------->|                |              |           |          |
  |           |           |              | find handler   |              |           |          |
  |           |           |              |--------------->|              |           |          |
  |           |           |              |                | resolve args |           |          |
  |           |           |              |                |------------->|           |          |
  |           |           |              |                |              | call svc  |          |
  |           |           |              |                |              |---------->|          |
  |           |           |              |                |              |           | INSERT   |
  |           |           |              |                |              |           |--------->|
  |           |           |              |                |              | response  |          |
  |           |           |              |                |<-------------|           |          |
  |           |           |              | JSON convert   |              |           |          |
  |           |           |<-------------|                |              |           |          |
  |           | write     | after        |                |              |           |          |
  |<----------|<----------|              |                |              |           |          |
```

### Validation Failure Before Controller

```text
Client      Tomcat      Filters      Dispatcher     HandlerAdapter     Controller
  |           |           |              |                |                |
  | POST bad  |           |              |                |                |
  |---------->|           |              |                |                |
  |           | parse     |              |                |                |
  |           |---------->|              |                |                |
  |           |           |------------->|                |                |
  |           |           |              | find handler   |                |
  |           |           |              |--------------->|                |
  |           |           |              |                | deserialize    |
  |           |           |              |                | validate fails |
  |           |           |              | exception resolver             |
  |           |           |<-------------|                |                |
  |<----------|           | 400 response |                | NOT CALLED     |
```

Important:

```text
Controller may not run if argument resolution or validation fails.
```

---

## Request Lifecycle

Full request lifecycle:

```text
1. Client resolves DNS and connects to load balancer.

2. Load balancer or ingress forwards request to application pod.

3. Tomcat accepts connection on configured port.

4. Tomcat selects or creates worker thread.

5. Tomcat parses HTTP request into HttpServletRequest.

6. Servlet filter chain begins.

7. Infrastructure filters add request id, security context, tracing, CORS, logging, metrics.

8. If a filter rejects request, error response is written immediately.

9. If filters pass, DispatcherServlet receives request.

10. DispatcherServlet asks HandlerMappings for matching handler.

11. HandlerMapping returns controller method metadata.

12. DispatcherServlet selects HandlerAdapter.

13. HandlerAdapter resolves method arguments.

14. Message converters deserialize request body if needed.

15. Bean Validation validates @Valid parameters.

16. Controller method is invoked.

17. Controller delegates to service.

18. Service applies business rules and often opens transaction.

19. Repository accesses database.

20. Service returns business response.

21. Controller returns DTO or ResponseEntity.

22. HandlerAdapter processes return value.

23. Message converter serializes DTO to JSON.

24. DispatcherServlet completes request.

25. Filter chain after-phase executes.

26. Tomcat commits response and writes bytes to socket.

27. Client receives status, headers, and body.
```

Memory hook:

```text
Network -> Servlet -> Filters -> Dispatcher -> Controller -> Service -> DB -> JSON -> Socket
```

---

## Multiple Dry Runs

### Dry Run 1 - Normal GET

Request:

```http
GET /api/products/10
```

Flow:

```text
Tomcat receives request
Filter chain passes
DispatcherServlet matches GET /api/products/{id}
@PathVariable resolver converts 10 to Long
Controller calls service
Service reads database
Controller returns ProductResponse
Jackson writes JSON
HTTP 200 returned
```

Final response:

```http
HTTP/1.1 200 OK
Content-Type: application/json

{"id":10,"name":"Keyboard","price":99.0}
```

### Dry Run 2 - Validation Failure

Request:

```http
POST /api/users
Content-Type: application/json

{
  "name": "",
  "email": "not-email"
}
```

Flow:

```text
Tomcat parses request
Filters pass
DispatcherServlet finds UserController.createUser
Jackson creates CreateUserRequest
Bean Validation checks @NotBlank and @Email
Validation fails
MethodArgumentNotValidException thrown
Exception resolver creates 400 response
Controller method is not called
Service is not called
Database is not touched
```

Final response:

```http
HTTP/1.1 400 Bad Request
Content-Type: application/json

{"error":"VALIDATION_FAILED"}
```

### Dry Run 3 - Unauthorized Request

Request:

```http
GET /api/orders/1
Authorization: Bearer invalid-token
```

Flow:

```text
Tomcat receives request
Security filter checks token
Token invalid
Security filter writes 401 response
DispatcherServlet is not called
Controller is not called
```

Final response:

```http
HTTP/1.1 401 Unauthorized
```

Mental model:

```text
Filters guard the gate before MVC routing.
```

### Dry Run 4 - Controller Throws Business Exception

Flow:

```text
GET /api/products/999
DispatcherServlet routes request
Controller calls service
Service cannot find product
Service throws ProductNotFoundException
Exception bubbles to DispatcherServlet
@RestControllerAdvice handles it
HTTP 404 JSON response returned
```

Final response:

```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{"code":"PRODUCT_NOT_FOUND"}
```

### Dry Run 5 - Slow Database Causes Thread Pressure

Traffic:

```text
1000 RPS
Database latency = 1 second
```

Approximate active request threads:

```text
1000 RPS × 1 second = 1000 concurrent requests
```

If Tomcat max threads is 200:

```text
200 threads busy
New requests queue
Latency increases
Timeouts happen
```

Mental model:

```text
In blocking Spring MVC, slow downstream calls hold request threads.
```

---

## Failure Scenarios

### 1. Controller Not Called Because Filter Rejected Request

Symptom:

```text
You put breakpoint in controller.
It never hits.
Client gets 401 or 403.
```

Likely cause:

```text
Security filter rejected request before DispatcherServlet.
```

Debug:

```text
Enable Spring Security debug logs.
Check Authorization header.
Check filter order.
```

### 2. Controller Not Called Because Mapping Does Not Match

Symptom:

```text
404 Not Found
Controller breakpoint not hit
```

Possible causes:

```text
Wrong HTTP method
Wrong path prefix
Missing context path
Trailing slash mismatch
Consumes/produces mismatch
Controller package not scanned
```

### 3. Request Body Cannot Be Deserialized

Symptom:

```text
400 Bad Request
JSON parse error
Controller not called
```

Possible causes:

```text
Malformed JSON
Wrong Content-Type
Missing no-args constructor for class DTO
Invalid enum value
Date format mismatch
```

### 4. Validation Fails Before Business Logic

Symptom:

```text
400 Bad Request
Service not called
```

Cause:

```text
@Valid fails during argument resolution.
```

### 5. Response Already Committed

Symptom:

```text
Cannot call sendError() after response has been committed
```

Cause:

```text
Some code wrote response bytes early, then later tried to change status.
```

Production fix:

```text
Return structured ResponseEntity.
Avoid writing directly to response unless necessary.
```

### 6. Thread Pool Exhaustion

Symptom:

```text
High request latency
Tomcat busy threads near max
Requests waiting
CPU not necessarily high
```

Cause:

```text
Slow DB / external service / locks / long transactions hold threads.
```

### 7. Missing Request ID In Logs

Symptom:

```text
Cannot trace one request across logs.
```

Cause:

```text
Request ID filter missing or MDC not cleared correctly.
```

Fix:

```text
Add filter that puts requestId into MDC before chain and removes it in finally.
```

### 8. Large Request Body Hurts Memory

Symptom:

```text
Memory pressure
Slow requests
Large JSON upload causes GC spikes
```

Cause:

```text
Request body is fully read/deserialized into memory.
```

Fix:

```text
Limit request size.
Use streaming upload for large files.
Keep DTOs bounded.
```

---

## Failure Investigation Playbook

When request flow surprises you, debug in this order.

### Step 1 - Did Request Reach The App?

Check access logs or a first filter.

```text
If no log appears, request may be stuck at DNS, LB, ingress, service, network policy, or wrong port.
```

### Step 2 - Did It Pass Filters?

Add or inspect filter logs.

```text
Security filter may return 401/403 before controller.
CORS may block browser requests.
Request size filter may reject large body.
```

### Step 3 - Did DispatcherServlet Find A Handler?

Enable mapping logs:

```yaml
logging:
  level:
    org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping: TRACE
```

Look for registered mappings.

### Step 4 - Did Argument Resolution Fail?

Check for:

```text
HttpMessageNotReadableException
MethodArgumentNotValidException
MissingServletRequestParameterException
MethodArgumentTypeMismatchException
```

### Step 5 - Did Controller Run?

Add a log at controller entry:

```java
log.info("createOrder called requestId={}", requestId);
```

If not reached, issue is before controller.

### Step 6 - Did Service / Transaction Run?

Add logs at service entry.

Check transaction logs if needed:

```yaml
logging:
  level:
    org.springframework.transaction: TRACE
```

### Step 7 - Did Exception Resolver Convert Error?

Check `@RestControllerAdvice`.

Common issue:

```text
Exception is thrown but no handler exists, so client gets generic 500.
```

### Step 8 - Did Response Serialization Fail?

Symptoms:

```text
Controller returned successfully but response is 500.
```

Possible causes:

```text
Jackson infinite recursion
Lazy-loaded entity outside transaction
DTO contains unsupported type
```

### Step 9 - Did After-Filter Fail?

A filter can throw after controller returns.

Always inspect full stack trace.

### Step 10 - Check Metrics

Useful metrics:

```text
http.server.requests count/latency
Tomcat busy threads
Tomcat max threads
Hikari active connections
DB query latency
Exception count by status
p95/p99 latency by endpoint
```

---

## Debugging Guide

### Request ID Filter With MDC

```java
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestId = request.getHeader(HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        MDC.put("requestId", requestId);
        response.setHeader(HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("requestId");
        }
    }
}
```

### Log Pattern

```properties
logging.pattern.level=%5p [requestId=%X{requestId}]
```

### Controller Debug Log

```java
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        log.info("create order request productId={} quantity={}",
                request.productId(), request.quantity());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }
}
```

### Useful Spring MVC Logs

```yaml
logging:
  level:
    org.springframework.web: DEBUG
    org.springframework.web.servlet.DispatcherServlet: DEBUG
    org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping: TRACE
    org.springframework.http.converter: DEBUG
```

### Debugging Questions

```text
1. Did request reach the app?
2. Which thread handled it?
3. Which filters ran?
4. Did security reject it?
5. Did DispatcherServlet find a handler?
6. Did body deserialization succeed?
7. Did validation pass?
8. Did controller method run?
9. Did service method run?
10. Did transaction start?
11. Did repository query complete?
12. Did response serialization succeed?
13. Did any after-filter throw?
14. Was the response committed before error handling?
```

---

## Performance Considerations

### Blocking Thread Cost

In Spring MVC, a request commonly occupies one server thread while waiting.

If service calls DB:

```text
Thread waits for DB response.
```

If service calls remote API:

```text
Thread waits for network response.
```

Formula:

```text
Concurrent requests ≈ RPS × average latency
```

Example:

```text
2000 RPS × 100 ms = 200 concurrent requests
```

If p99 becomes 2 seconds:

```text
2000 RPS × 2 seconds = 4000 concurrent requests at p99 pressure
```

That can overwhelm threads, DB connections, and queues.

### JSON Serialization Cost

Large responses cost:

```text
CPU for serialization
Memory for object graph
Network bandwidth
Client parsing time
```

Better:

```text
Paginate large lists
Return DTOs, not entities
Avoid huge nested graphs
Compress where appropriate
```

### Validation Cost

Validation is usually cheap, but complex custom validators can hit database or remote services.

Bad:

```text
@Valid triggers remote call per field
```

Better:

```text
Keep validation local and fast.
Do business existence checks in service.
```

### Filter Cost

Every request passes through filters.

Do not make filters heavy.

Bad filter behavior:

```text
Reads entire request body unnecessarily
Calls database
Calls remote auth service synchronously every time
Logs huge payloads
```

### Controller Should Be Thin

Controller should not do expensive orchestration.

Good split:

```text
Controller = HTTP translation
Service    = business rules
Repository = persistence
```

### Timeouts

Production systems need timeouts at every boundary:

```text
Load balancer timeout
Ingress timeout
Tomcat connection timeout
DB query timeout
HTTP client timeout
Transaction timeout
```

No timeout means stuck requests can hold resources too long.

---

## Scalability Considerations

### Horizontal Scaling

Adding pods increases request capacity only if bottlenecks can scale.

```text
More pods
  -> more Tomcat threads
  -> more DB connections
  -> more pressure on database
```

Danger:

```text
20 pods × 30 Hikari connections = 600 possible DB connections
```

Your database may not handle that.

### Stateless Request Handling

Spring Boot REST services scale best when stateless.

Good:

```text
JWT / session externalized
No local in-memory user session dependency
No local file dependency
No sticky session required
```

### Backpressure

If downstream DB is slow, blindly accepting all requests causes collapse.

Use:

```text
Connection pool limits
Thread pool limits
Rate limiting
Bulkheads
Circuit breakers
Timeouts
Queue limits
```

### Gateway / Ingress Responsibilities

Common edge responsibilities:

```text
TLS termination
Routing
Rate limiting
Authentication pre-checks
Request size limits
Compression
Observability
```

But application must still validate and authorize.

### Readiness And Liveness

Kubernetes should only send traffic to ready pods.

```text
Readiness probe = can receive traffic?
Liveness probe  = should restart app?
```

Bad readiness means requests enter pods before Spring is ready.

---

## Production Failure Story

### Incident: Slow Endpoint Took Down All APIs

A service had this endpoint:

```java
@GetMapping("/reports/monthly")
public MonthlyReport report() {
    return reportService.generateMonthlyReport();
}
```

Internally it did:

```text
Load 300,000 rows
Map entities to nested DTOs
Serialize huge JSON response
No pagination
No timeout
```

During business hours, several clients called it repeatedly.

What happened:

```text
Tomcat threads became busy generating reports.
DB connections stayed active.
Heap usage increased due to large DTO graphs.
GC pauses increased.
Other small APIs waited behind busy threads.
p99 latency increased for unrelated endpoints.
Ingress started returning 504.
```

Initial wrong assumption:

```text
Database is slow.
```

Actual issue:

```text
The request pipeline allowed one expensive endpoint to consume shared server resources.
```

Failure diagram:

```text
/report request
    |
    v
Tomcat worker thread held for 20s
    |
    v
DB connection held for long query
    |
    v
Large DTO in heap
    |
    v
Jackson serializes huge response
    |
    v
Other requests wait for threads / DB connections
```

Fix:

```text
1. Paginated report endpoint.
2. Added async background report generation.
3. Stored generated report in object storage.
4. Returned job id immediately.
5. Added endpoint-specific rate limit.
6. Added query timeout.
7. Added response size monitoring.
8. Reduced Tomcat and Hikari settings to prevent DB overload.
```

Improved model:

```text
Request should initiate work quickly.
Long-running heavy work should not monopolize request threads.
```

Senior lesson:

```text
Every endpoint shares the same request pipeline resources.
One bad endpoint can starve unrelated APIs.
```

---

## Common Misconceptions

### Misconception 1

```text
The request goes directly to controller.
```

Correct:

```text
The request passes through Tomcat, filters, DispatcherServlet, handler mapping, adapters, argument resolvers, and converters before the controller method runs.
```

### Misconception 2

```text
Filters and interceptors are the same.
```

Correct:

```text
Filters are Servlet-level and run before DispatcherServlet.
Spring MVC interceptors run around handler execution inside MVC.
```

### Misconception 3

```text
404 always means controller returned not found.
```

Correct:

```text
404 may mean no route matched before controller was called.
```

### Misconception 4

```text
400 validation errors happen inside controller.
```

Correct:

```text
Many 400 errors happen during argument resolution before controller method invocation.
```

### Misconception 5

```text
@ResponseBody manually creates JSON.
```

Correct:

```text
Spring delegates JSON serialization to HttpMessageConverter, usually backed by Jackson.
```

### Misconception 6

```text
Adding pods always fixes slow APIs.
```

Correct:

```text
If the bottleneck is database, external API, locks, or shared thread pool pressure, adding pods may amplify the bottleneck.
```

### Misconception 7

```text
Controller is the best place for business logic.
```

Correct:

```text
Controller should translate HTTP to application call. Service should own business rules.
```

---

## Java / Spring Boot Code Examples With Internal Execution Explanation

### Example 1 - Path Variable And Request Param

```java
@GetMapping("/api/products/{id}")
public ProductResponse getProduct(
        @PathVariable Long id,
        @RequestParam(defaultValue = "false") boolean includeReviews) {
    return productService.getProduct(id, includeReviews);
}
```

Request:

```http
GET /api/products/10?includeReviews=true
```

Internal execution:

```text
HandlerMapping matches /api/products/{id}
@PathVariable resolver extracts id="10"
ConversionService converts "10" to Long
@RequestParam resolver extracts includeReviews="true"
ConversionService converts "true" to boolean
Controller method invoked with id=10, includeReviews=true
```

### Example 2 - Request Header

```java
@PostMapping("/api/payments")
public PaymentResponse pay(
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @Valid @RequestBody PaymentRequest request) {
    return paymentService.pay(idempotencyKey, request);
}
```

Internal execution:

```text
Header resolver extracts Idempotency-Key
Body resolver reads JSON body
Validator validates PaymentRequest
Controller calls service with clean Java values
```

Production note:

```text
Idempotency keys are usually service-level business safety, not controller-only validation.
```

### Example 3 - Global Error Response

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiError("VALIDATION_FAILED", "Invalid request body"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnknown(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("INTERNAL_ERROR", "Something went wrong"));
    }
}
```

Internal execution:

```text
Exception thrown anywhere inside MVC handling
DispatcherServlet asks exception resolvers
Matching @ExceptionHandler method selected
ApiError returned
MessageConverter serializes ApiError to JSON
```

### Example 4 - Filter Timing Every Request

```java
@Component
public class TimingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        long start = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            System.out.println(request.getMethod() + " "
                    + request.getRequestURI() + " status="
                    + response.getStatus() + " ms=" + elapsedMs);
        }
    }
}
```

Internal execution:

```text
Filter runs before DispatcherServlet
filterChain.doFilter passes control forward
After controller/exception handling completes, finally block runs
Filter sees final response status
```

---

## FAANG/System Design Discussion

### How would you explain Spring request flow in an interview?

Strong answer:

```text
In Spring Boot MVC, an HTTP request first reaches the embedded servlet container such as Tomcat. Tomcat parses it into HttpServletRequest and assigns a worker thread. The request passes through the servlet filter chain, where concerns such as security, CORS, tracing, and logging can run. If filters allow it, DispatcherServlet receives the request. DispatcherServlet uses HandlerMapping to find the matching controller method and HandlerAdapter to invoke it. Argument resolvers and message converters convert path variables, query params, headers, and JSON request bodies into Java method parameters. The controller delegates to service and repository layers. The return value is converted back to JSON by HttpMessageConverter and written to the response. Exceptions are translated by exception resolvers such as @RestControllerAdvice.
```

### What should a senior engineer monitor?

```text
Request rate by endpoint
Latency p50/p95/p99 by endpoint
HTTP status distribution
Tomcat busy threads
Request queueing
DB connection pool active/pending
Downstream latency
Serialization errors
Validation error rates
Security rejection rates
```

### What design mistake causes production outages?

```text
Treating request threads as free.
```

In blocking MVC:

```text
Slow DB, slow HTTP calls, large JSON serialization, and long transactions hold request threads.
```

Senior design principle:

```text
Keep request path short, bounded, observable, and protected by timeouts.
```

---

## Common Interview Questions

### Q1. What is DispatcherServlet?

DispatcherServlet is the central front controller in Spring MVC. It receives web requests after the filter chain, finds the correct handler using HandlerMappings, invokes it using HandlerAdapters, handles exceptions, and coordinates response rendering.

### Q2. What happens before a controller method is called?

The request is accepted by Tomcat, parsed into servlet request/response objects, passed through filters, routed by DispatcherServlet, matched to a handler, and converted into method arguments using argument resolvers and message converters. Validation may also run before the controller method is invoked.

### Q3. What is the difference between Filter and HandlerInterceptor?

A Filter is part of the Servlet layer and runs before the request reaches DispatcherServlet. A HandlerInterceptor is part of Spring MVC and runs after a handler is selected but before/after controller execution.

### Q4. How does `@RequestBody` work internally?

Spring uses an argument resolver that reads the request body and delegates to an HttpMessageConverter. For JSON, Jackson is commonly used to deserialize bytes into a Java DTO. If `@Valid` is present, validation runs after deserialization.

### Q5. Why can a controller not be called even though the endpoint exists?

A filter may reject the request, route matching may fail due to method/path/content type mismatch, body deserialization may fail, or validation may fail during argument resolution before method invocation.

### Q6. How are exceptions converted to HTTP responses?

DispatcherServlet delegates to HandlerExceptionResolvers. `@RestControllerAdvice` with `@ExceptionHandler` is a common way to map exceptions to structured HTTP responses.

### Q7. Why is Spring MVC called blocking?

In the common Servlet MVC model, a request occupies a worker thread while it waits for downstream operations such as database queries or remote HTTP calls. Long waits can exhaust server threads.

### Q8. What is the role of HttpMessageConverter?

It converts HTTP request/response bodies to and from Java objects. For REST APIs, it commonly converts JSON to DTOs and DTOs back to JSON.

### Q9. Where should business logic live?

Business logic should live in services. Controllers should translate HTTP input into application calls and translate application output into HTTP responses.

### Q10. What is the best production rule for request flow?

Keep the request path short, bounded, observable, and protected. Add timeouts, avoid huge responses, use pagination, keep filters lightweight, and monitor p99 latency and thread/connection pool pressure.

---

## Strong Interview Answers

### Explain end-to-end Spring Boot request flow.

```text
A request first reaches the embedded server, usually Tomcat, which parses HTTP and assigns a worker thread. The request passes through servlet filters for cross-cutting concerns like security and tracing. Then DispatcherServlet acts as the front controller. It uses HandlerMapping to find the controller method and HandlerAdapter to invoke it. Argument resolvers extract path variables, query params, headers, and body data. HttpMessageConverters deserialize JSON into DTOs and later serialize DTOs back to JSON. The controller delegates business work to services and repositories. Exceptions are handled by HandlerExceptionResolvers, often backed by @RestControllerAdvice. Finally the response returns back through filters and Tomcat writes bytes to the client.
```

### Explain why 400 can happen before controller.

```text
A 400 can happen during argument resolution. For example, if JSON is malformed, the HttpMessageConverter throws HttpMessageNotReadableException. If @Valid fails, Spring throws MethodArgumentNotValidException. In both cases the controller method is not invoked because Spring could not construct valid method arguments.
```

### Explain production impact of slow downstream calls.

```text
In blocking Spring MVC, each active request usually occupies a server thread. If downstream latency increases, those threads remain busy longer. Concurrent requests are roughly RPS multiplied by latency. If the thread pool or DB pool saturates, unrelated endpoints also slow down. This is why timeouts, bulkheads, pagination, and short transactions are important.
```

---

## Production Checklist

```text
[ ] Request ID is generated or propagated.
[ ] Trace ID is visible in logs.
[ ] Security filters reject invalid requests early.
[ ] CORS is configured intentionally.
[ ] Controller mappings are tested.
[ ] DTO validation is clear and consistent.
[ ] Error responses are standardized with @RestControllerAdvice.
[ ] Controllers are thin.
[ ] Business logic lives in services.
[ ] Transactions start at service boundaries where appropriate.
[ ] Large responses are paginated.
[ ] Request body size limits exist.
[ ] Timeouts exist for DB and remote calls.
[ ] Tomcat thread metrics are monitored.
[ ] Hikari pool metrics are monitored.
[ ] p95/p99 latency is tracked by endpoint.
[ ] 4xx and 5xx rates are tracked.
[ ] Filters are lightweight.
[ ] MDC is cleared in finally blocks.
[ ] Readiness probe prevents traffic before app is ready.
```

---

## One-Page Cheat Sheet

```text
Spring Boot request flow
        =
Pipeline handoff from HTTP bytes to controller and back
```

Main flow:

```text
Client
  -> LB / Ingress
  -> Tomcat
  -> HttpServletRequest
  -> Filter Chain
  -> DispatcherServlet
  -> HandlerMapping
  -> HandlerAdapter
  -> ArgumentResolvers
  -> HttpMessageConverters
  -> Controller
  -> Service
  -> Repository
  -> Database
  -> Response DTO
  -> HttpMessageConverters
  -> HttpServletResponse
  -> Client
```

Core roles:

```text
Tomcat              = accepts and parses HTTP
Filter              = servlet-level cross-cutting gate
DispatcherServlet   = Spring MVC front controller
HandlerMapping      = finds controller method
HandlerAdapter      = invokes controller method
ArgumentResolver    = builds method parameters
MessageConverter    = JSON <-> Java DTO
Controller          = HTTP translation layer
Service             = business logic layer
Repository          = persistence layer
ExceptionResolver   = exception -> HTTP response
```

Failure location memory:

```text
401/403 -> often filter/security before controller
404     -> often no handler mapping
400     -> often argument/body/validation before controller
500     -> often service, repository, serialization, or unhandled exception
```

Performance formula:

```text
Concurrent requests ≈ RPS × latency
```

Main production rule:

```text
Keep request path short, bounded, observable, and protected by timeouts.
```

---

## Last-Minute Interview Revision

```text
1. Request does not go directly to controller.
2. Tomcat converts socket bytes to servlet request/response.
3. Filters run before DispatcherServlet.
4. DispatcherServlet is the Spring MVC traffic controller.
5. HandlerMapping finds the controller method.
6. HandlerAdapter invokes it.
7. ArgumentResolvers build method parameters.
8. HttpMessageConverters convert JSON and Java DTOs.
9. @Valid can fail before controller runs.
10. @RestControllerAdvice converts exceptions to HTTP responses.
11. Controller should be thin.
12. Service should own business logic.
13. Blocking MVC holds a thread per active request.
14. Slow downstream systems create thread pressure.
15. Monitor endpoint latency, Tomcat threads, DB pool, and error rates.
```

---

## Mental Models Table

| Concept | Mental Model | Production Meaning |
|---|---|---|
| Tomcat | Airport entrance | Accepts and parses HTTP |
| Worker thread | Request carrier | Holds request execution in blocking MVC |
| Filter | Security gate | Can reject before controller |
| DispatcherServlet | Traffic controller | Coordinates MVC routing |
| HandlerMapping | Address book | Finds controller method |
| HandlerAdapter | Method invoker | Calls handler correctly |
| ArgumentResolver | Translator | Converts HTTP parts to Java args |
| MessageConverter | JSON machine | Converts body bytes and DTOs |
| Controller | HTTP adapter | Should stay thin |
| Service | Business engine | Owns rules and transactions |
| Repository | DB gateway | Owns persistence access |
| ExceptionResolver | Error translator | Exception to HTTP response |

---

## One Picture To Remember

```text
                       ONE HTTP REQUEST
+---------------------------------------------------------------+
|                                                               |
|  Raw HTTP bytes                                               |
|       |                                                       |
|       v                                                       |
|  Tomcat parses request                                        |
|       |                                                       |
|       v                                                       |
|  Filter Chain                                                 |
|       |                                                       |
|       v                                                       |
|  DispatcherServlet                                            |
|       |                                                       |
|       +--> HandlerMapping finds controller                    |
|       |                                                       |
|       +--> HandlerAdapter resolves args                       |
|       |                                                       |
|       v                                                       |
|  Controller method                                            |
|       |                                                       |
|       v                                                       |
|  Service -> Repository -> Database                            |
|       |                                                       |
|       v                                                       |
|  Response DTO                                                 |
|       |                                                       |
|       v                                                       |
|  JSON bytes written to socket                                 |
|                                                               |
+---------------------------------------------------------------+
```

Final memory hook:

```text
Spring request flow is not controller magic.

It is:

Parse request.
Run gates.
Route to handler.
Translate HTTP to Java.
Run business logic.
Translate Java to HTTP.
Write response.
```

---

## Key Takeaways

1. A Spring Boot request is a pipeline, not a direct browser-to-controller call.

2. Tomcat accepts the network request, parses HTTP, and assigns a worker thread.

3. Filters run before DispatcherServlet and can reject requests early.

4. DispatcherServlet is the central coordinator of Spring MVC.

5. HandlerMapping finds the matching controller method.

6. HandlerAdapter invokes the controller method using argument resolvers.

7. HttpMessageConverters convert JSON bodies into Java DTOs and Java DTOs into JSON responses.

8. Validation can fail before the controller method is called.

9. Exception resolvers convert Java exceptions into HTTP responses.

10. Controllers should translate HTTP; services should own business rules.

11. In blocking MVC, slow downstream calls hold request threads.

12. Production debugging starts by locating where the pipeline stopped: network, filter, mapping, argument resolution, controller, service, repository, serialization, or response write.

13. Senior engineers monitor p99 latency, Tomcat threads, DB pool pressure, error rates, and endpoint-level resource usage.
