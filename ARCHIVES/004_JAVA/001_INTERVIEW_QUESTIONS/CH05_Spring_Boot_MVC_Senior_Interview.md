# CH 05 --- Spring Boot & Spring MVC

## Senior Java Distributed Backend Interview Series

**Target:** \~40 interview questions\
**Goal:** Master Spring Boot startup and auto-configuration, Spring MVC
request processing, binding/validation, filters/interceptors, exception
handling, Actuator, and production API debugging.

------------------------------------------------------------------------

# Chapter Map

``` text
CH 05 — SPRING BOOT / MVC (~40 Q)
|
+-- 1. Spring Boot Fundamentals / Startup (10)
|   +-- Spring vs Spring Boot
|   +-- @SpringBootApplication
|   +-- Startup flow
|   +-- Auto-configuration
|   +-- Conditional configuration
|   +-- Starters
|   +-- Embedded server
|   +-- Externalized configuration
|   +-- Profiles
|   +-- ConfigurationProperties
|
+-- 2. Spring MVC Request Lifecycle (10)
|   +-- DispatcherServlet
|   +-- HandlerMapping
|   +-- HandlerAdapter
|   +-- Controller
|   +-- Argument resolvers
|   +-- Message converters
|   +-- Response serialization
|   +-- Filter
|   +-- Interceptor
|   +-- Request lifecycle end-to-end
|
+-- 3. Controllers / Binding / Validation (8)
|   +-- @Controller vs @RestController
|   +-- Request mappings
|   +-- @PathVariable / @RequestParam
|   +-- @RequestBody
|   +-- DTO design
|   +-- Bean Validation
|   +-- Custom validation
|   +-- Validation pitfalls
|
+-- 4. Exceptions / Actuator / Production (12)
    +-- Exception resolution
    +-- @ExceptionHandler
    +-- @ControllerAdvice
    +-- Error response design
    +-- Problem Details
    +-- Actuator
    +-- Health / readiness / liveness
    +-- Metrics
    +-- Graceful shutdown
    +-- Slow API debugging
    +-- Request thread exhaustion
    +-- Production MVC troubleshooting
```

------------------------------------------------------------------------

# Big Picture --- One HTTP Request

``` text
Client
  |
  v
Load Balancer / Ingress
  |
  v
Servlet Container
  |
  v
FILTER CHAIN
  |
  v
DispatcherServlet
  |
  v
HandlerMapping
  |
  v
HandlerAdapter
  |
  v
Argument Resolvers
  |
  v
Controller
  |
  v
Service Proxy
  |
  v
Service
  |
  v
Repository / DB / Kafka / Redis
  |
  v
Return value
  |
  v
HttpMessageConverter
  |
  v
JSON Response
  |
  v
FILTER CHAIN
  |
  v
Client
```

For senior interviews, know **where each concern executes**.

------------------------------------------------------------------------

# 1. Spring Boot Fundamentals / Startup --- 10 Questions

## Q1. Spring Framework vs Spring Boot?

``` text
SPRING FRAMEWORK
|
+-- IoC / DI
+-- AOP
+-- MVC
+-- Transactions
+-- Data access
+-- Security integrations
```

Spring Boot builds on Spring and reduces application setup/operational
boilerplate.

``` text
SPRING BOOT
|
+-- Auto-configuration
+-- Starter dependencies
+-- Embedded server
+-- Externalized configuration
+-- Actuator
+-- Opinionated defaults
```

**Senior answer:** Boot does not replace Spring; it provides conventions
and automatic configuration around the Spring ecosystem.

------------------------------------------------------------------------

## Q2. What does `@SpringBootApplication` contain?

Conceptually:

``` java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```

Visual:

``` text
@SpringBootApplication
       |
       +--> Configuration class
       |
       +--> Component scanning
       |
       +--> Auto-configuration
```

Package placement matters because component scanning commonly starts
from the application's package.

------------------------------------------------------------------------

## Q3. Explain Spring Boot startup flow.

``` java
SpringApplication.run(Application.class, args);
```

Simplified flow:

``` text
main()
  |
  v
SpringApplication.run()
  |
  v
Prepare Environment
  |
  v
Create ApplicationContext
  |
  v
Load bean definitions
  |
  v
Apply auto-configuration
  |
  v
Register post-processors
  |
  v
Create singleton beans
  |
  v
Start embedded web server
  |
  v
Publish lifecycle events
  |
  v
Application ready
```

Startup details can vary, but this is the interview mental model.

------------------------------------------------------------------------

## Q4. What is auto-configuration?

Boot configures infrastructure based on what it finds.

Conceptually:

``` text
Classpath
   +
Existing beans
   +
Configuration properties
   +
Application type
   |
   v
Conditional Auto-Configuration
   |
   v
Create sensible default beans
```

Example mental model:

``` text
JDBC classes present?
      |
DataSource dependency present?
      |
No custom DataSource bean?
      |
properties available?
      |
      v
Boot can configure DataSource
```

### Senior point

Auto-configuration is **conditional configuration**, not magic.

------------------------------------------------------------------------

## Q5. How do conditional annotations work?

Common conditions include:

``` text
@ConditionalOnClass
@ConditionalOnMissingBean
@ConditionalOnBean
@ConditionalOnProperty
@ConditionalOnWebApplication
```

Example:

``` java
@Bean
@ConditionalOnMissingBean
PaymentClient paymentClient() {
    return new DefaultPaymentClient();
}
```

``` text
User defines PaymentClient?
       |
   +---+---+
   |       |
  YES      NO
   |       |
back off   create default
```

This "back-off" model is central to Boot extensibility.

------------------------------------------------------------------------

## Q6. What are Spring Boot starters?

A starter is a curated dependency set.

``` text
spring-boot-starter-web
       |
       +--> Spring MVC
       +--> JSON support
       +--> logging dependencies
       +--> embedded servlet stack dependencies
```

Benefits:

-   compatible dependency selection
-   less manual dependency management
-   conventional setup

Do not describe a starter as code generation. It is primarily
dependency/convention packaging.

------------------------------------------------------------------------

## Q7. Why use an embedded server?

Traditional:

``` text
Build WAR
   |
Deploy to external Tomcat
```

Boot style:

``` text
Build executable application
      |
      v
java -jar app.jar
      |
      v
embedded server starts
```

Benefits for services:

-   self-contained deployment unit
-   consistent local/prod runtime model
-   container-friendly
-   easier automation

------------------------------------------------------------------------

## Q8. How does externalized configuration work?

Common sources conceptually:

``` text
Defaults
  |
application.yml / properties
  |
profile-specific config
  |
environment variables
  |
system / command-line / external sources
```

Exact precedence has details; in interviews, emphasize that
higher-precedence property sources can override lower ones.

Example:

``` yaml
payment:
  timeout: 2s
  endpoint: https://example
```

Prefer:

``` java
@ConfigurationProperties(prefix = "payment")
public record PaymentProperties(
    Duration timeout,
    URI endpoint
) {}
```

over scattered string-based lookups.

------------------------------------------------------------------------

## Q9. How should profiles be used?

``` yaml
spring:
  profiles:
    active: prod
```

Conceptual environments:

``` text
local
test
staging
prod
```

Use profiles for environment-specific bean/config activation, but avoid
turning profiles into an enormous set of behavioral switches.

A modern deployment often pushes many environment differences into
external configuration.

------------------------------------------------------------------------

## Q10. `@Value` vs `@ConfigurationProperties`?

`@Value`:

``` java
@Value("${payment.timeout}")
private Duration timeout;
```

Good for a small isolated property.

`@ConfigurationProperties`:

``` java
@ConfigurationProperties("payment")
public record PaymentProperties(
    URI endpoint,
    Duration timeout,
    int retries
) {}
```

Better for grouped configuration:

``` text
payment.*
   |
   +--> endpoint
   +--> timeout
   +--> retries
          |
          v
typed configuration object
```

Benefits include type safety, validation and maintainability.

------------------------------------------------------------------------

# 2. Spring MVC Request Lifecycle --- 10 Questions

## Q11. What is `DispatcherServlet`?

It is Spring MVC's front controller.

``` text
HTTP Request
     |
     v
DispatcherServlet
     |
     +--> find handler
     +--> invoke handler
     +--> process result
     +--> exception resolution
     |
     v
HTTP Response
```

It coordinates MVC infrastructure rather than containing business logic
itself.

------------------------------------------------------------------------

## Q12. What is HandlerMapping?

It maps a request to a handler.

Example:

``` java
@GetMapping("/orders/{id}")
OrderResponse get(@PathVariable long id)
```

Conceptually:

``` text
GET /orders/42
      |
      v
HandlerMapping
      |
      v
OrderController.get(...)
```

------------------------------------------------------------------------

## Q13. What is HandlerAdapter?

DispatcherServlet should not need to know how every possible handler
type is invoked.

``` text
DispatcherServlet
       |
       v
HandlerAdapter
       |
       v
Invoke matched handler/controller
```

It adapts the selected handler to the invocation model.

------------------------------------------------------------------------

## Q14. How are controller method arguments resolved?

``` java
@GetMapping("/orders/{id}")
Order get(
    @PathVariable long id,
    @RequestHeader("X-Tenant") String tenant
) {}
```

Conceptually:

``` text
HTTP request
    |
    v
HandlerMethodArgumentResolvers
    |
    +--> path variable
    +--> request parameter
    +--> header
    +--> principal
    +--> request body
    |
    v
Java method arguments
```

Custom argument resolvers can inject application-specific request
context.

------------------------------------------------------------------------

## Q15. What is `HttpMessageConverter`?

It converts between HTTP body data and Java objects.

Request:

``` text
JSON bytes
   |
   v
HttpMessageConverter
   |
   v
OrderRequest DTO
```

Response:

``` text
OrderResponse DTO
   |
   v
HttpMessageConverter
   |
   v
JSON bytes
```

In typical REST applications, JSON conversion is commonly backed by
Jackson integration.

------------------------------------------------------------------------

## Q16. How is a response serialized?

``` java
@RestController
class OrderController {
    @GetMapping("/orders/{id}")
    OrderResponse get(...) {
        return response;
    }
}
```

Flow:

``` text
Controller returns Java object
        |
        v
Return value handling
        |
        v
Content negotiation
        |
        v
HttpMessageConverter
        |
        v
JSON
        |
        v
HTTP response
```

Serialization can itself cause issues such as accidental lazy-loading,
huge payloads or recursive object graphs---one reason APIs should
normally return DTOs rather than JPA entities.

------------------------------------------------------------------------

## Q17. What is a Servlet Filter?

Filter sits at servlet-container level around requests/responses.

``` text
Request
  |
  v
Filter 1
  |
Filter 2
  |
DispatcherServlet
  |
Controller
  |
Filter 2
  |
Filter 1
  |
Response
```

Typical uses:

-   security infrastructure
-   correlation IDs
-   request/response logging
-   headers
-   compression/infrastructure concerns

------------------------------------------------------------------------

## Q18. What is a Spring MVC Interceptor?

Interceptor works around Spring MVC handler execution.

``` text
DispatcherServlet
       |
       v
Interceptor.preHandle()
       |
       v
Controller
       |
       v
Interceptor.postHandle()
       |
       v
afterCompletion()
```

Typical uses:

-   handler-aware logging
-   request context
-   lightweight authorization checks
-   timing

------------------------------------------------------------------------

## Q19. Filter vs Interceptor?

``` text
FILTER
|
Servlet layer
|
can run before DispatcherServlet
|
works with request/response

INTERCEPTOR
|
Spring MVC layer
|
handler/controller aware
|
runs around handler execution
```

Visual:

``` text
Client
  |
FILTER
  |
DispatcherServlet
  |
INTERCEPTOR
  |
Controller
```

For method-level cross-cutting behavior, AOP may be another layer:

``` text
Filter
  ↓
Interceptor
  ↓
Controller
  ↓
Service Proxy / AOP
  ↓
Service
```

------------------------------------------------------------------------

## Q20. Explain the full MVC request lifecycle.

``` text
1. Client sends HTTP request
          |
2. Servlet container accepts request
          |
3. Filter chain executes
          |
4. DispatcherServlet receives request
          |
5. HandlerMapping finds controller method
          |
6. HandlerAdapter prepares invocation
          |
7. Argument resolvers bind parameters
          |
8. HttpMessageConverter deserializes body
          |
9. Validation executes
          |
10. Controller method executes
          |
11. Service/proxy/repository work
          |
12. Controller returns value
          |
13. Return value handler processes result
          |
14. Message converter serializes response
          |
15. Exception resolvers handle failures if any
          |
16. Interceptor completion callbacks
          |
17. Filter chain unwinds
          |
18. Response sent
```

This is one of the most valuable Spring MVC diagrams to reproduce in an
interview.

------------------------------------------------------------------------

# 3. Controllers / Binding / Validation --- 8 Questions

## Q21. `@Controller` vs `@RestController`?

Conceptually:

``` text
@RestController
      =
@Controller
      +
@ResponseBody semantics
```

`@Controller` commonly participates in MVC view rendering.

`@RestController` is suited to HTTP APIs returning response bodies.

------------------------------------------------------------------------

## Q22. Explain request mapping annotations.

``` java
@GetMapping
@PostMapping
@PutMapping
@PatchMapping
@DeleteMapping
```

Example:

``` java
@RestController
@RequestMapping("/orders")
class OrderController {

    @PostMapping
    ResponseEntity<OrderResponse> create(
        @RequestBody CreateOrderRequest request) {
        ...
    }
}
```

Good API design maps HTTP semantics intentionally rather than choosing
verbs only for convenience.

------------------------------------------------------------------------

## Q23. `@PathVariable` vs `@RequestParam`?

``` text
/orders/42
         |
         +--> PathVariable

/orders?status=PAID
               |
               +--> RequestParam
```

Typical use:

``` text
PathVariable -> identifies resource/path component
RequestParam  -> filtering, sorting, optional controls
```

------------------------------------------------------------------------

## Q24. What does `@RequestBody` do?

``` java
@PostMapping
OrderResponse create(
    @RequestBody CreateOrderRequest request) {
    ...
}
```

Flow:

``` text
HTTP JSON
   |
Content-Type
   |
HttpMessageConverter
   |
CreateOrderRequest
```

Potential failures:

-   malformed JSON
-   unsupported media type
-   type mismatch
-   missing required structure
-   validation failure

These should map to deliberate client-facing errors.

------------------------------------------------------------------------

## Q25. Why use DTOs instead of exposing JPA entities?

Bad coupling:

``` text
API JSON
   |
   v
JPA Entity
   |
   v
Database Model
```

Problems:

-   API tied to persistence
-   accidental lazy loading
-   recursive relationships
-   sensitive fields exposed
-   difficult API versioning
-   over-posting risks
-   persistence annotations leak into contract

Better:

``` text
HTTP DTO
   |
Mapper
   |
Domain / Service
   |
Entity
```

------------------------------------------------------------------------

## Q26. How does Bean Validation work?

DTO:

``` java
public record CreateUserRequest(
    @NotBlank String name,
    @Email String email,
    @Positive int age
) {}
```

Controller:

``` java
@PostMapping
void create(
    @Valid @RequestBody CreateUserRequest request) {
}
```

Flow:

``` text
JSON
 |
deserialize
 |
DTO
 |
@Valid
 |
constraints
 |
+-- valid   -> controller
|
+-- invalid -> validation exception
```

------------------------------------------------------------------------

## Q27. How do you implement custom validation?

Example domain constraint:

``` text
startDate must be before endDate
```

Create a class-level custom constraint rather than forcing complex
cross-field logic into individual field annotations.

Conceptually:

``` text
@ValidDateRange
BookingRequest
    |
    v
ConstraintValidator
    |
    v
boolean valid?
```

### Senior distinction

Input shape validation is not the same as business validation.

``` text
Bean validation:
email format
required field
range

Business validation:
account has sufficient balance
order can transition from PAID to REFUNDED
```

Business rules belong in domain/service logic.

------------------------------------------------------------------------

## Q28. What validation mistakes occur in real APIs?

Common problems:

``` text
No validation
    -> invalid data reaches deep layers

Only frontend validation
    -> API remains unsafe

JPA entity as request DTO
    -> over-posting / coupling

Huge controller validation logic
    -> controller becomes business layer

Returning internal exception text
    -> security / usability issue
```

Preferred layering:

``` text
HTTP structural validation
          |
          v
Controller boundary
          |
          v
Business/domain validation
          |
          v
Persistence constraints
```

Multiple layers protect different invariants.

------------------------------------------------------------------------

# 4. Exceptions / Actuator / Production --- 12 Questions

## Q29. How does Spring MVC handle exceptions?

Conceptually:

``` text
Controller throws exception
        |
        v
DispatcherServlet
        |
        v
HandlerExceptionResolver chain
        |
        +--> @ExceptionHandler
        +--> response status handling
        +--> framework resolvers
        |
        v
HTTP error response
```

Avoid `try/catch` in every controller for the same exception types.

------------------------------------------------------------------------

## Q30. What is `@ExceptionHandler`?

``` java
@ExceptionHandler(OrderNotFoundException.class)
ResponseEntity<ErrorResponse> handle(
        OrderNotFoundException ex) {
    ...
}
```

It maps exceptions to HTTP responses.

Can be local to a controller or used through centralized advice.

------------------------------------------------------------------------

## Q31. What is `@ControllerAdvice` / `@RestControllerAdvice`?

Centralized cross-controller exception handling.

``` java
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(...) {
        ...
    }
}
```

Visual:

``` text
Controller A --+
Controller B --+--> Exception
Controller C --+
                    |
                    v
             Global Advice
                    |
                    v
           Standard Error DTO
```

------------------------------------------------------------------------

## Q32. What should a good API error response contain?

Example:

``` json
{
  "code": "ORDER_NOT_FOUND",
  "message": "Order was not found",
  "status": 404,
  "traceId": "abc-123"
}
```

Useful concepts:

``` text
machine-readable error code
human-readable safe message
HTTP status
correlation/trace ID
field validation errors when applicable
```

Do not leak:

-   SQL
-   stack traces
-   secrets
-   internal hostnames
-   sensitive implementation details

------------------------------------------------------------------------

## Q33. What are Problem Details?

Modern HTTP APIs can use a standardized problem-details representation
for errors.

Conceptually:

``` text
Problem
|
+-- type
+-- title
+-- status
+-- detail
+-- instance
+-- optional extensions
```

The benefit is a consistent error contract instead of every service
inventing an incompatible structure.

------------------------------------------------------------------------

## Q34. What is Spring Boot Actuator?

Actuator exposes production-oriented application information and
management endpoints.

Common capabilities:

``` text
Actuator
|
+-- health
+-- metrics
+-- info
+-- loggers
+-- mappings
+-- beans
+-- environment-related diagnostics
+-- Prometheus integration
```

### Security warning

Do not expose every management endpoint publicly.

------------------------------------------------------------------------

## Q35. Health vs readiness vs liveness?

Mental model:

``` text
LIVENESS
"Should this process/container be restarted?"

READINESS
"Should this instance receive traffic?"

HEALTH
broader application/component health information
```

Example:

``` text
Pod running
   |
   +--> liveness = UP
   |
   +--> readiness = DOWN
           |
           v
remove from service traffic
without necessarily restarting it
```

A temporary downstream problem should not automatically trigger
destructive restart loops.

------------------------------------------------------------------------

## Q36. What metrics matter for a Spring API?

Start with RED:

``` text
R = Rate
E = Errors
D = Duration
```

Then resources:

``` text
CPU
memory
GC
threads
executor queue
HTTP connections
Hikari active/pending
DB latency
Kafka lag
Redis latency
```

Latency:

``` text
average alone is insufficient

p50
p95
p99 ★★★
```

Senior interviews often focus on p99 because tail latency exposes
contention and dependency problems.

------------------------------------------------------------------------

## Q37. What is graceful shutdown?

Bad:

``` text
SIGTERM
   |
process immediately dies
   |
active requests fail
```

Desired:

``` text
SIGTERM
   |
stop accepting new traffic
   |
finish/drain active work
   |
close resources
   |
terminate
```

In Kubernetes:

``` text
Deployment rollout
      |
      v
Pod termination
      |
      +--> readiness changes / endpoint removal
      |
      +--> termination grace period
      |
      +--> application shutdown
```

Also consider long-running async work and message consumers.

------------------------------------------------------------------------

## Q38. How would you debug a slow Spring Boot API?

Use layers.

``` text
p99 increased
    |
    v
Is traffic higher?
    |
    v
Trace slow request
    |
    +--> Filter?
    +--> Controller?
    +--> Service?
    +--> Lock?
    +--> DB?
    +--> Redis?
    +--> Kafka wait?
    +--> remote API?
    |
    v
Check resources
    |
    +--> CPU
    +--> GC
    +--> request threads
    +--> executor queues
    +--> Hikari pool
    |
    v
Identify bottleneck
```

Do not start by randomly tuning JVM or increasing thread counts.

------------------------------------------------------------------------

## Q39. How does request-thread exhaustion happen?

``` text
Incoming HTTP requests
        |
        v
Servlet request threads
[busy][busy][busy][busy]
        |
        v
slow downstream API
        |
        v
threads BLOCKED / WAITING
        |
        v
new requests queue
        |
        v
latency ↑
        |
        v
timeouts
        |
        v
retries
        |
        +----> even more traffic
```

Root cause can be downstream, not the web server.

Possible controls:

-   correct timeouts
-   bulkheads
-   backpressure
-   connection limits
-   circuit breaker
-   capacity planning
-   virtual threads where appropriate, while still limiting downstream
    concurrency

------------------------------------------------------------------------

## Q40. Give a production MVC troubleshooting checklist.

``` text
REQUEST FAILING / SLOW
        |
        v
1. HTTP status / error rate
        |
2. logs + trace ID
        |
3. distributed trace
        |
4. request mapping correct?
        |
5. filter/security issue?
        |
6. binding/validation?
        |
7. controller timing?
        |
8. service/proxy?
        |
9. thread / executor?
        |
10. Hikari / SQL?
        |
11. remote dependency?
        |
12. CPU / memory / GC?
        |
13. Kubernetes/network?
```

The goal is systematic narrowing, not guessing.

------------------------------------------------------------------------

# Senior Scenario 1 --- Filter vs Interceptor vs AOP

Requirement:

> Add correlation ID, controller execution timing, and service-level
> audit.

Choose layers:

``` text
Correlation ID
     |
     v
FILTER
because it should wrap almost the entire HTTP request

Controller timing
     |
     v
INTERCEPTOR
because it is MVC-handler aware

Service audit
     |
     v
AOP
because concern applies to selected service methods
```

Full picture:

``` text
Request
  |
Correlation Filter
  |
DispatcherServlet
  |
Timing Interceptor
  |
Controller
  |
Audit Proxy
  |
Service
```

------------------------------------------------------------------------

# Senior Scenario 2 --- Returning JPA Entity Causes Huge Response

``` java
@GetMapping("/users/{id}")
UserEntity get(...) {
    return repository.findById(id).orElseThrow();
}
```

Entity:

``` text
User
 |
 +--> Orders
       |
       +--> Items
             |
             +--> Product
```

Serialization may trigger:

``` text
lazy associations
      |
      v
extra SQL
      |
      v
N+1 / huge object graph
      |
      v
slow response / recursion
```

Better:

``` text
Repository
   |
fetch exactly needed data
   |
Service
   |
DTO mapping
   |
UserResponse
```

This connects CH05 directly to CH07 JPA/Hibernate.

------------------------------------------------------------------------

# Senior Scenario 3 --- Readiness Designed Incorrectly

Suppose readiness checks 10 downstream systems.

``` text
One optional recommendation service
          |
        DOWN
          |
          v
readiness = DOWN
          |
          v
all API pods removed from traffic
```

You converted a partial dependency failure into a complete outage.

Senior reasoning:

``` text
Dependency
   |
Is it REQUIRED to serve core traffic?
   |
+-- YES -> readiness may care
|
+-- NO -> degrade functionality instead
```

Health checks are architecture decisions, not merely configuration.

------------------------------------------------------------------------

# Senior Scenario 4 --- Retry Storm

``` text
Service A
   |
   v
Service B becomes slow
   |
A request times out
   |
retry x3
   |
traffic to B ~multiplies
   |
B gets slower
   |
more timeout
   |
more retry
```

``` text
      +----------------------+
      |                      |
      v                      |
Slow Dependency -> Timeout -> Retry
      ^                      |
      |                      |
      +---- MORE LOAD <------+
```

Need coordinated:

-   timeout
-   retry budget
-   exponential backoff
-   jitter
-   circuit breaker
-   idempotency
-   capacity awareness

Covered deeper in CH10 Microservices.

------------------------------------------------------------------------

# Senior Scenario 5 --- Startup Works Locally, Fails in Kubernetes

``` text
LOCAL
application.yml
    |
works

KUBERNETES
environment / secret / config
    |
missing property
    |
binding failure
    |
BeanCreationException
    |
pod restart
```

Debug:

``` text
Pod logs
   |
root cause
   |
configuration binding
   |
effective environment
   |
secret/config map mounted?
   |
profile correct?
```

Do not assume "Spring issue" when the actual problem is deployment
configuration.

------------------------------------------------------------------------

# Mini Coding Drill 1 --- Clean REST Controller

``` java
@RestController
@RequestMapping("/orders")
class OrderController {

    private final OrderService orderService;

    OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    ResponseEntity<OrderResponse> create(
            @Valid @RequestBody CreateOrderRequest request) {

        OrderResponse result =
            orderService.create(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result);
    }
}
```

Keep:

``` text
Controller
|
+-- HTTP concerns
+-- binding
+-- validation boundary
+-- status/headers
|
X business workflow
```

------------------------------------------------------------------------

# Mini Coding Drill 2 --- Global Exception Handler

``` java
@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(
            OrderNotFoundException ex) {

        ApiError error = new ApiError(
            "ORDER_NOT_FOUND",
            "Order was not found"
        );

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error);
    }
}
```

Flow:

``` text
Service throws domain exception
        |
        v
Controller call unwinds
        |
        v
Exception resolver
        |
        v
@RestControllerAdvice
        |
        v
stable API error
```

------------------------------------------------------------------------

# Mini Coding Drill 3 --- Type-Safe Configuration

``` java
@ConfigurationProperties(prefix = "client.payment")
public record PaymentClientProperties(
    URI baseUrl,
    Duration connectTimeout,
    Duration readTimeout
) {}
```

``` yaml
client:
  payment:
    base-url: https://payment.internal
    connect-timeout: 500ms
    read-timeout: 2s
```

Visual:

``` text
External Config
      |
      v
Binder
      |
      v
PaymentClientProperties
      |
      v
PaymentClient Bean
```

------------------------------------------------------------------------

# Mini Coding Drill 4 --- Correlation Filter

``` java
@Component
class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        String correlationId =
            Optional.ofNullable(
                request.getHeader("X-Correlation-Id")
            ).orElseGet(() -> UUID.randomUUID().toString());

        response.setHeader(
            "X-Correlation-Id",
            correlationId
        );

        try {
            chain.doFilter(request, response);
        } finally {
            // clear thread-local/MDC state if used
        }
    }
}
```

Senior follow-up:

If using thread-local logging context, understand what happens when work
moves to another executor/thread.

------------------------------------------------------------------------

# MVC + Spring Core Connection

From CH04:

``` text
Controller Bean
     |
     v
Injected Service reference
     |
     v
Spring Proxy
     |
     +--> Transaction
     +--> Security
     +--> Metrics
     |
     v
Service Target
```

From CH05:

``` text
HTTP
 |
Filter
 |
DispatcherServlet
 |
Interceptor
 |
Controller
 |
Service Proxy
```

Combined:

``` text
HTTP Request
     |
     v
Servlet Filter
     |
     v
DispatcherServlet
     |
     v
MVC Interceptor
     |
     v
Controller
     |
     v
Spring AOP Proxy
     |
     v
Service
```

This layer model is extremely important.

------------------------------------------------------------------------

# MVC + Concurrency Connection

Traditional servlet request handling:

``` text
Request 1 ---> Request Thread 1
Request 2 ---> Request Thread 2
Request 3 ---> Request Thread 3
```

If each request waits on a slow DB:

``` text
Request Threads
|||||||||||||||||
       |
       v
    HikariCP
   [20 conns]
       |
       v
   slow database
```

Symptoms:

``` text
active request threads ↑
queue ↑
p99 ↑
timeouts ↑
retries ↑
```

This connects CH05 to CH02 Concurrency and later CH24 Production
Troubleshooting.

------------------------------------------------------------------------

# MVC + Observability Preview

A request should be traceable:

``` text
Client
 |
trace/correlation ID
 |
Spring API
 |
Controller span
 |
Service span
 |
DB span
 |
Remote API span
 |
Response
```

Then:

``` text
"Why was request abc123 slow?"
             |
             v
trace timeline
             |
       +-----+------+
       |            |
     DB 40ms    Payment API 2.8s
```

Observability is covered deeply in CH21.

------------------------------------------------------------------------

# Critical Comparison Sheet

## Spring vs Spring Boot

``` text
Spring
framework capabilities

Spring Boot
conventions + auto-config + operational setup
built on Spring
```

------------------------------------------------------------------------

## Filter vs Interceptor vs AOP

``` text
Filter
  |
Servlet / HTTP boundary

Interceptor
  |
Spring MVC handler boundary

AOP
  |
Spring bean method boundary
```

------------------------------------------------------------------------

## `@PathVariable` vs `@RequestParam`

``` text
/orders/{id}
        |
resource identity/path

/orders?status=PAID
             |
query/filter/control
```

------------------------------------------------------------------------

## `@Value` vs `@ConfigurationProperties`

``` text
@Value
small individual value

@ConfigurationProperties
grouped typed configuration
```

------------------------------------------------------------------------

## DTO vs Entity

``` text
DTO
API contract

Entity
persistence model
```

Do not make them the same merely to save mapping code.

------------------------------------------------------------------------

## Liveness vs Readiness

``` text
Liveness
"restart me?"

Readiness
"send traffic to me?"
```

------------------------------------------------------------------------

# Senior Rapid-Fire Follow-Ups

1.  Spring vs Spring Boot?
2.  What does `@SpringBootApplication` combine?
3.  Explain Boot startup.
4.  What is auto-configuration?
5.  How does Boot back off when you define your own bean?
6.  What are conditional annotations?
7.  What is a starter?
8.  Why embedded server?
9.  How does externalized configuration work?
10. `@Value` vs `@ConfigurationProperties`?
11. What is DispatcherServlet?
12. HandlerMapping vs HandlerAdapter?
13. What is an argument resolver?
14. What is HttpMessageConverter?
15. How does JSON become a DTO?
16. How does a DTO become JSON?
17. Filter vs interceptor?
18. Where does AOP fit relative to filter/interceptor?
19. Explain full MVC lifecycle.
20. `@Controller` vs `@RestController`?
21. Path variable vs request param?
22. Why not return JPA entities?
23. How does `@Valid` work?
24. Bean validation vs business validation?
25. What is `@ExceptionHandler`?
26. Why use `@RestControllerAdvice`?
27. What should an API error contain?
28. What should it never expose?
29. What are Problem Details?
30. What is Actuator?
31. Which Actuator endpoints should be protected?
32. Readiness vs liveness?
33. Why can a bad readiness check cause an outage?
34. What API metrics do you monitor?
35. Why p99 instead of average only?
36. What is graceful shutdown?
37. How do you debug a slow API?
38. How does request-thread exhaustion happen?
39. Why might increasing request threads make things worse?
40. Walk through a production request from ingress to database.

------------------------------------------------------------------------

# Interview Checklist

``` text
BOOT FUNDAMENTALS
[ ] 01 Spring vs Spring Boot
[ ] 02 @SpringBootApplication
[ ] 03 Startup flow
[ ] 04 Auto-configuration
[ ] 05 Conditional configuration
[ ] 06 Starters
[ ] 07 Embedded server
[ ] 08 External configuration
[ ] 09 Profiles
[ ] 10 @ConfigurationProperties

MVC LIFECYCLE
[ ] 11 DispatcherServlet
[ ] 12 HandlerMapping
[ ] 13 HandlerAdapter
[ ] 14 Argument resolvers
[ ] 15 HttpMessageConverter
[ ] 16 Response serialization
[ ] 17 Filter
[ ] 18 Interceptor
[ ] 19 Filter vs interceptor
[ ] 20 Complete request lifecycle

CONTROLLERS / VALIDATION
[ ] 21 Controller vs RestController
[ ] 22 Request mappings
[ ] 23 PathVariable vs RequestParam
[ ] 24 RequestBody
[ ] 25 DTO design
[ ] 26 Bean Validation
[ ] 27 Custom validation
[ ] 28 Validation pitfalls

EXCEPTIONS / PRODUCTION
[ ] 29 Exception resolution
[ ] 30 @ExceptionHandler
[ ] 31 @ControllerAdvice
[ ] 32 Error design
[ ] 33 Problem Details
[ ] 34 Actuator
[ ] 35 Health/readiness/liveness
[ ] 36 Metrics
[ ] 37 Graceful shutdown
[ ] 38 Slow API debugging
[ ] 39 Request-thread exhaustion
[ ] 40 Production troubleshooting
```

------------------------------------------------------------------------

# Chapter 05 Visual Summary

``` text
                    SPRING BOOT / MVC
                           |
             +-------------+-------------+
             |                           |
          STARTUP                      REQUEST
             |                           |
             v                           v
     SpringApplication.run          Servlet Container
             |                           |
             v                           v
         Environment                   Filter
             |                           |
             v                           v
      ApplicationContext         DispatcherServlet
             |                           |
             v                           v
     Auto-Configuration            HandlerMapping
             |                           |
             v                           v
        Create Beans               HandlerAdapter
             |                           |
             v                           v
      Embedded Server            Argument Resolution
             |                           |
             v                           v
          READY                     Controller
                                         |
                                         v
                                   Service Proxy
                                         |
                                         v
                                      Service
                                         |
                                         v
                                   DB / Redis / Kafka
                                         |
                                         v
                                 Message Converter
                                         |
                                         v
                                     RESPONSE
```

------------------------------------------------------------------------

# Production Mental Model

``` text
Client
  |
Ingress / Load Balancer
  |
Servlet Server
  |
Filter
  |
DispatcherServlet
  |
Interceptor
  |
Controller
  |
Service Proxy
  |
Service
  |
  +------> Executor / Virtual Thread
  |
  +------> HikariCP ------> PostgreSQL
  |
  +------> Redis
  |
  +------> Kafka
  |
  +------> Remote Service
  |
Response
```

When latency rises, **follow the request through this tree**.

------------------------------------------------------------------------

# Chapter 05 Exit Criteria

You are ready for **CH 06 --- Spring Transactions** when you can:

1.  Explain Spring vs Spring Boot.
2.  Explain `@SpringBootApplication`.
3.  Draw Boot startup.
4.  Explain auto-configuration as conditional configuration.
5.  Explain `@ConditionalOnMissingBean` back-off.
6.  Explain starters and embedded servers.
7.  Explain typed external configuration.
8.  Draw DispatcherServlet request processing.
9.  Explain HandlerMapping vs HandlerAdapter.
10. Explain argument resolution and message conversion.
11. Draw Filter → DispatcherServlet → Interceptor → Controller → AOP.
12. Explain DTO vs JPA entity.
13. Explain structural vs business validation.
14. Design centralized API exception handling.
15. Explain readiness vs liveness.
16. Explain why health checks can create outages.
17. List the core metrics for an API.
18. Explain graceful shutdown.
19. Diagnose request-thread exhaustion.
20. Walk systematically through a slow Spring API.

``` text
CH 05 SPRING BOOT / MVC
        |
        v
Boot Startup
        |
        v
Auto-Configuration
        |
        v
DispatcherServlet ★★★
        |
        v
Filter / Interceptor
        |
        v
Binding / Validation
        |
        v
Exception Handling
        |
        v
Actuator / Production
        |
        v
READY FOR CH 06
SPRING TRANSACTIONS
```

------------------------------------------------------------------------

## Next Chapter

``` text
CH 06 — SPRING TRANSACTIONS (~35 Q)
|
+-- ACID / transaction fundamentals
+-- @Transactional internals
+-- Spring proxy + transaction interceptor
+-- Propagation
+-- Isolation
+-- Rollback rules
+-- Self-invocation
+-- Transaction boundaries
+-- Locks / concurrency
+-- Distributed transaction limitations
+-- Production failure scenarios
```
