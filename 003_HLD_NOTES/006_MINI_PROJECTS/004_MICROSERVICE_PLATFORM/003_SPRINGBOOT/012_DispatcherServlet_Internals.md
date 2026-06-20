# 012_DispatcherServlet_Internals.md

# DispatcherServlet Internals — The One Front Door Mental Model

> **Core mental model:**  
> In Spring MVC, `DispatcherServlet` is the **single front door** of the web application.  
> Every HTTP request enters through this door, and the servlet **does not do business logic itself**.  
> It only **finds the right handler, prepares the execution path, invokes it, handles the result, and writes the response**.

This chapter teaches exactly one idea:

```text
HTTP Request
    ↓
DispatcherServlet
    ↓
Find handler → run interceptors → call controller → resolve return value → render/write response
```

Do not memorize 20 Spring MVC classes.  
Remember one picture:

```text
Client → [DispatcherServlet: traffic controller] → Controller → Response
```

---

## Why This Exists

Without `DispatcherServlet`, every controller would need to know how to:

- receive raw HTTP requests
- parse URLs
- match request paths
- extract parameters
- bind JSON bodies
- validate input
- call controller methods
- handle exceptions
- choose JSON vs HTML response
- write status codes
- run common logic before and after requests

That would make every controller messy.

Spring MVC solves this with one centralized entry point.

```text
Bad design:

Request /users/1  ──→ UserController does routing + parsing + validation + JSON + errors
Request /orders/9 ──→ OrderController does routing + parsing + validation + JSON + errors
Request /pay      ──→ PaymentController does routing + parsing + validation + JSON + errors

Result: repeated web plumbing everywhere.
```

Spring MVC design:

```text
Request /users/1
Request /orders/9       ┌──────────────────────┐
Request /pay       ────→ │ DispatcherServlet    │
                         │ common web pipeline  │
                         └──────────┬───────────┘
                                    ↓
                              Correct Controller
```

`DispatcherServlet` exists so that controllers can stay focused on application behavior.

---

## Problem Statement

A web framework must answer this question for every request:

> “Given this HTTP request, which application method should run, what inputs should it receive, and how should its output become an HTTP response?”

Example request:

```http
GET /api/users/42 HTTP/1.1
Accept: application/json
```

Spring must decide:

```text
Path: /api/users/42
HTTP method: GET
Controller method: UserController.getUser(42)
Input binding: 42 as Long
Return format: JSON
Status: 200 or error
```

The problem is not only “call a method”.  
The real problem is building a complete HTTP execution pipeline.

---

## Mental Model

Think of `DispatcherServlet` as an airport control tower.

Planes are requests.  
Runways are controllers.  
Ground staff are argument resolvers, message converters, exception handlers, and view resolvers.

The tower does not fly the plane.  
It coordinates the journey.

```text
                HTTP Request
                    │
                    ▼
        ┌──────────────────────────┐
        │   DispatcherServlet       │
        │  "front control tower"    │
        └─────────────┬────────────┘
                      │
      ┌───────────────┼────────────────┐
      │               │                │
      ▼               ▼                ▼
Find handler     Prepare args      Handle result
      │               │                │
      └───────────────┴────────────────┘
                      │
                      ▼
              HTTP Response
```

The servlet owns the pipeline.  
The controller owns business intent.

---

## Real World Analogy

Imagine a hospital reception desk.

A patient arrives and says:

```text
"I have a heart problem."
```

The reception desk does not perform surgery.

It does this:

```text
1. Understand request type
2. Find correct department
3. Check patient records
4. Send patient to the right doctor
5. Collect doctor's result
6. Prepare discharge summary or error handling
```

That is `DispatcherServlet`.

```text
Patient arrival     → HTTP request
Reception desk      → DispatcherServlet
Doctor selection    → HandlerMapping
Doctor visit        → Controller method
Medical report      → Return value
Discharge summary   → HTTP response
```

---

## Core Concepts

### 1. Front Controller

A front controller means one central servlet receives all matching web requests.

```text
Traditional servlet style:

/users/*   → UserServlet
/orders/*  → OrderServlet
/payments/*→ PaymentServlet

Spring MVC:

/* or /     → DispatcherServlet
               ↓
             routes internally
```

### 2. Handler

A handler is the thing that can process a request.

Most commonly, it is a controller method:

```java
@GetMapping("/users/{id}")
public UserDto getUser(@PathVariable Long id) {
    return userService.getUser(id);
}
```

Internally Spring treats this as something like:

```text
Handler = UserController#getUser(Long id)
```

### 3. HandlerMapping

`HandlerMapping` answers:

```text
Which handler matches this request?
```

Example:

```text
GET /api/users/42
    ↓
RequestMappingHandlerMapping
    ↓
UserController#getUser(Long id)
```

### 4. HandlerAdapter

`HandlerAdapter` answers:

```text
How do I invoke this kind of handler?
```

DispatcherServlet does not directly call every possible handler type.  
It delegates invocation to an adapter.

```text
DispatcherServlet
    ↓
HandlerAdapter
    ↓
Actual controller method
```

### 5. Argument Resolver

Argument resolvers convert HTTP information into Java method arguments.

```java
@GetMapping("/users/{id}")
public UserDto getUser(
        @PathVariable Long id,
        @RequestHeader("X-Request-Id") String requestId) {
    ...
}
```

Spring resolves:

```text
{id} from URL path        → Long id
X-Request-Id from header → String requestId
```

### 6. Message Converter

Message converters convert between HTTP body and Java objects.

```text
JSON request body → Java DTO
Java DTO          → JSON response body
```

For REST APIs, this is usually done by Jackson through `MappingJackson2HttpMessageConverter`.

### 7. Exception Resolver

Exception resolvers convert Java exceptions into HTTP responses.

```text
UserNotFoundException → 404 Not Found
ValidationException   → 400 Bad Request
RuntimeException      → 500 Internal Server Error
```

---

## Internal Architecture

At startup, Spring Boot registers `DispatcherServlet` with the embedded servlet container.

```text
Spring Boot Application
        │
        ▼
Embedded Tomcat / Jetty / Undertow
        │
        ▼
Registers DispatcherServlet
        │
        ▼
Maps requests to Spring MVC pipeline
```

High-level architecture:

```text
┌─────────────────────────────────────────────────────────────────┐
│                         Servlet Container                        │
│                    Tomcat / Jetty / Undertow                     │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                       DispatcherServlet                          │
│                                                                 │
│  ┌────────────────────┐    ┌────────────────────┐               │
│  │ HandlerMapping     │    │ HandlerAdapter     │               │
│  │ finds controller   │    │ invokes controller │               │
│  └────────────────────┘    └────────────────────┘               │
│                                                                 │
│  ┌────────────────────┐    ┌────────────────────┐               │
│  │ ArgumentResolvers  │    │ MessageConverters  │               │
│  │ build parameters   │    │ JSON/XML/body I/O  │               │
│  └────────────────────┘    └────────────────────┘               │
│                                                                 │
│  ┌────────────────────┐    ┌────────────────────┐               │
│  │ ExceptionResolvers │    │ ViewResolvers      │               │
│  │ error → response   │    │ view name → page   │               │
│  └────────────────────┘    └────────────────────┘               │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
                         Controller Method
```

For REST APIs, the common path is:

```text
Request → DispatcherServlet → HandlerMapping → HandlerAdapter
        → Controller → HttpMessageConverter → JSON response
```

For MVC pages, the path may be:

```text
Request → DispatcherServlet → Controller → ModelAndView
        → ViewResolver → HTML page
```

---

## Internal Working

The main request flow is conceptually:

```text
1. Servlet container receives HTTP request
2. Container forwards it to DispatcherServlet
3. DispatcherServlet finds matching handler
4. DispatcherServlet finds adapter that can invoke handler
5. Interceptors run preHandle
6. HandlerAdapter resolves method arguments
7. Controller method executes
8. Return value handlers process result
9. Message converters or view resolvers create response
10. Interceptors run postHandle / afterCompletion
11. Response goes back to client
```

ASCII pipeline:

```text
HTTP Request
    │
    ▼
┌──────────────┐
│ Tomcat       │
└──────┬───────┘
       │
       ▼
┌──────────────────────┐
│ DispatcherServlet    │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ HandlerMapping       │  "Who should handle this?"
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ HandlerAdapter       │  "How do I call it?"
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ Argument Resolvers   │  "Build method parameters"
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ Controller Method    │  "Business endpoint"
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ Return Value Handler │  "What does result mean?"
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ Message Converter    │  "Java object → JSON"
└──────┬───────────────┘
       │
       ▼
HTTP Response
```

---

## Step-by-Step Dry Run

Request:

```http
GET /api/users/42 HTTP/1.1
Accept: application/json
```

Controller:

```java
@RestController
@RequestMapping("/api/users")
class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    UserDto getUser(@PathVariable Long id) {
        return userService.findUser(id);
    }
}
```

Dry run:

```text
Step 1:
Tomcat receives GET /api/users/42.

Step 2:
Tomcat sees this URL is mapped to DispatcherServlet.

Step 3:
DispatcherServlet asks HandlerMapping:
"Who handles GET /api/users/42?"

Step 4:
HandlerMapping returns:
UserController#getUser(Long id)

Step 5:
DispatcherServlet asks HandlerAdapter:
"Can you invoke this handler method?"

Step 6:
HandlerAdapter prepares arguments.
@PathVariable Long id = 42

Step 7:
Controller method runs:
userService.findUser(42)

Step 8:
Controller returns UserDto.

Step 9:
Return value handler sees @RestController / @ResponseBody behavior.
It says: write object directly to HTTP response body.

Step 10:
Message converter converts UserDto to JSON.

Step 11:
Client receives 200 OK with JSON body.
```

Result:

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 42,
  "name": "Amina"
}
```

---

## Java Code Example With Execution Explanation

### Minimal Controller

```java
@RestController
@RequestMapping("/api/orders")
class OrderController {

    private final OrderService orderService;

    OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    OrderDto getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }
}
```

### What You Write

You write:

```java
@GetMapping("/{id}")
OrderDto getOrder(@PathVariable Long id)
```

### What Spring Internally Understands

Spring builds a mapping table during startup:

```text
HTTP Method | Path Pattern       | Handler Method
------------|--------------------|-------------------------------
GET         | /api/orders/{id}   | OrderController#getOrder(Long)
```

At runtime:

```text
GET /api/orders/100
    ↓
Matches /api/orders/{id}
    ↓
id = 100
    ↓
Invoke getOrder(100)
```

The controller method does not know about `HttpServletRequest` unless you explicitly ask for it.

This is the power of argument resolution.

---

## Spring Boot Example With Internal Flow

```java
@SpringBootApplication
public class MiniSpringMvcApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiniSpringMvcApplication.class, args);
    }
}
```

When this starts:

```text
1. Spring Boot creates ApplicationContext.
2. MVC auto-configuration activates.
3. DispatcherServlet bean is created.
4. DispatcherServlet is registered with embedded Tomcat.
5. RequestMappingHandlerMapping scans @Controller/@RestController beans.
6. Controller methods are registered as request mappings.
7. HandlerAdapters, converters, and exception resolvers are prepared.
```

Startup mental picture:

```text
Application starts
      │
      ▼
Scan controllers
      │
      ▼
Build routing table
      │
      ▼
Register DispatcherServlet
      │
      ▼
Ready to accept HTTP requests
```

Runtime mental picture:

```text
Request arrives
      │
      ▼
Use routing table
      │
      ▼
Invoke controller
      │
      ▼
Convert result
      │
      ▼
Return response
```

---

## Rich ASCII Diagram: Full REST Flow

```text
┌─────────────────────────────────────────────────────────────────────────────┐
│                                CLIENT                                       │
│                    GET /api/orders/100                                      │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          EMBEDDED TOMCAT                                    │
│  Accept socket → parse HTTP → create HttpServletRequest/Response            │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         DISPATCHERSERVLET                                   │
│                                                                             │
│  1. getHandler(request)                                                     │
│  2. getHandlerAdapter(handler)                                              │
│  3. applyPreHandle(interceptors)                                            │
│  4. invoke handler                                                          │
│  5. process return value                                                    │
│  6. handle exception if needed                                              │
│  7. trigger afterCompletion                                                 │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                 ┌─────────────────┼─────────────────┐
                 ▼                 ▼                 ▼
       ┌──────────────────┐ ┌────────────────┐ ┌──────────────────────┐
       │ HandlerMapping   │ │ HandlerAdapter │ │ ExceptionResolver    │
       │ route lookup     │ │ method invoke  │ │ error to response    │
       └──────────────────┘ └────────────────┘ └──────────────────────┘
                 │                 │                 │
                 └─────────────────┼─────────────────┘
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         OrderController#getOrder                            │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         MessageConverter                                    │
│                         OrderDto → JSON                                     │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                               CLIENT                                        │
│                         200 OK + JSON body                                  │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Production Scale Example

Imagine a shopping application:

```text
/api/products/{id}
/api/cart/items
/api/orders
/api/payments
/api/users/me
```

At 10,000 requests per second, every request still follows the same conceptual Spring MVC path.

```text
Load Balancer
    ↓
Spring Boot pod 1 ── DispatcherServlet ── Controllers
Spring Boot pod 2 ── DispatcherServlet ── Controllers
Spring Boot pod 3 ── DispatcherServlet ── Controllers
Spring Boot pod 4 ── DispatcherServlet ── Controllers
```

`DispatcherServlet` is per application instance, not one global service.

```text
Kubernetes Service
      │
      ├── Pod A: Tomcat + DispatcherServlet + controllers
      ├── Pod B: Tomcat + DispatcherServlet + controllers
      └── Pod C: Tomcat + DispatcherServlet + controllers
```

Important production idea:

```text
DispatcherServlet itself is usually not your bottleneck.
Bottlenecks are usually:
- slow controller logic
- DB calls
- remote service calls
- serialization cost
- thread pool exhaustion
- blocking I/O
- large request/response bodies
```

---

## Production Failure Story

A team deployed a new endpoint:

```java
@PostMapping("/api/orders")
OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
    return orderService.create(request);
}
```

After deployment, clients received:

```http
415 Unsupported Media Type
```

The team first blamed the controller.  
But the controller was never called.

Actual flow:

```text
Request arrives
    ↓
DispatcherServlet finds handler
    ↓
HandlerAdapter tries to resolve @RequestBody
    ↓
MessageConverter checks Content-Type
    ↓
Client sent text/plain instead of application/json
    ↓
No converter can read body
    ↓
415 Unsupported Media Type
```

The bug was not business logic.  
It was request-to-method binding before the controller ran.

Debugging lesson:

> If the controller breakpoint is not hit, the failure is probably before controller invocation: routing, filters, interceptors, argument binding, validation, content type, or message conversion.

---

## Debugging Mindset

When a Spring MVC endpoint fails, ask:

```text
Did the request reach Spring?
Did DispatcherServlet receive it?
Did HandlerMapping find a handler?
Did HandlerAdapter invoke the handler?
Did argument binding succeed?
Did validation fail?
Did controller logic throw?
Did return value conversion fail?
Did exception handling change the response?
```

### Debug Checklist

```text
Symptom: 404 Not Found
Think:
- Wrong URL?
- Wrong HTTP method?
- Controller not scanned?
- Context path mismatch?
- RequestMapping path mismatch?

Symptom: 405 Method Not Allowed
Think:
- Path exists, but HTTP method is wrong.
- Example: POST sent to GET endpoint.

Symptom: 400 Bad Request
Think:
- Path variable conversion failed.
- Request param missing.
- JSON body malformed.
- Validation failed.

Symptom: 415 Unsupported Media Type
Think:
- Content-Type header wrong.
- Message converter cannot read request body.

Symptom: 406 Not Acceptable
Think:
- Accept header asks for format server cannot produce.

Symptom: 500 Internal Server Error
Think:
- Controller/service threw unhandled exception.
- Return value conversion failed.
```

### Useful Logging

For learning and debugging:

```properties
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.web.servlet.DispatcherServlet=DEBUG
logging.level.org.springframework.web.servlet.mvc.method.annotation=TRACE
```

Be careful with TRACE in production because it can be noisy.

---

## Multiple Dry Runs

### Dry Run 1: Normal GET Request

```http
GET /api/products/10
Accept: application/json
```

```text
DispatcherServlet receives request
    ↓
HandlerMapping finds ProductController#getProduct
    ↓
PathVariable id = 10
    ↓
Controller returns ProductDto
    ↓
Jackson converter writes JSON
    ↓
200 OK
```

### Dry Run 2: Wrong HTTP Method

Controller:

```java
@GetMapping("/api/products/{id}")
ProductDto getProduct(@PathVariable Long id) { ... }
```

Request:

```http
POST /api/products/10
```

Flow:

```text
DispatcherServlet receives request
    ↓
HandlerMapping sees path exists
    ↓
But method POST does not match GET
    ↓
405 Method Not Allowed
```

Controller is not executed.

### Dry Run 3: Bad Path Variable

Request:

```http
GET /api/products/abc
```

Controller expects:

```java
@PathVariable Long id
```

Flow:

```text
Handler found
    ↓
Argument resolver tries abc → Long
    ↓
Conversion fails
    ↓
400 Bad Request
```

Again, business logic may not run.

### Dry Run 4: Controller Throws Exception

```java
@GetMapping("/api/users/{id}")
UserDto getUser(@PathVariable Long id) {
    throw new UserNotFoundException(id);
}
```

Flow:

```text
Handler found
    ↓
Arguments resolved
    ↓
Controller executes
    ↓
Exception thrown
    ↓
ExceptionResolver checks @ControllerAdvice
    ↓
Return 404 response if mapped
```

---

## Failure Scenarios

### 1. Controller Not Found

```text
GET /api/customer/1
```

But controller has:

```java
@RequestMapping("/api/customers")
```

Result:

```text
No handler found → 404
```

### 2. Controller Exists But Bean Not Registered

Possible causes:

```text
- Controller package outside component scan
- Missing @RestController / @Controller
- Conditional bean not loaded
- Wrong Spring profile
```

### 3. JSON Cannot Be Parsed

```json
{
  "amount": 100,
```

Malformed body.

Flow:

```text
@RequestBody resolver
    ↓
Jackson parser
    ↓
Parse error
    ↓
400 Bad Request
```

### 4. Return Value Cannot Be Serialized

Example:

```java
class UserDto {
    private InputStream stream;
}
```

Flow:

```text
Controller returns object
    ↓
Message converter tries object → JSON
    ↓
Serialization fails
    ↓
500 Internal Server Error
```

### 5. Interceptor Blocks Request

```java
boolean preHandle(...) {
    response.setStatus(401);
    return false;
}
```

Flow:

```text
DispatcherServlet
    ↓
Interceptor preHandle returns false
    ↓
Controller is not called
```

---

## Failure Investigation Playbook

Use this order:

```text
1. Confirm request reaches application instance.
2. Check access logs.
3. Check URL, HTTP method, context path.
4. Check controller mapping.
5. Check if controller package is scanned.
6. Enable Spring web DEBUG logs.
7. Check argument binding and validation errors.
8. Check message converter errors.
9. Check @ControllerAdvice mappings.
10. Check filters and interceptors.
```

Production investigation picture:

```text
Client error
    │
    ▼
Is it 404?
    │
    ├── yes → mapping problem
    │
    └── no
         ▼
Is it 400/415?
    │
    ├── yes → binding/converter problem
    │
    └── no
         ▼
Is it 500?
    │
    ├── yes → controller/service/serialization problem
    │
    └── no → check filters/interceptors/security
```

---

## Common Misconceptions

### Misconception 1: DispatcherServlet contains business logic

Wrong.

It coordinates request execution.  
Business logic belongs in services.

### Misconception 2: Controller is always called if URL matches

Wrong.

The request can fail before controller execution:

```text
- wrong HTTP method
- missing parameter
- invalid path variable
- malformed JSON
- unsupported media type
- interceptor rejection
- security filter rejection
```

### Misconception 3: @RestController directly writes JSON itself

Wrong.

`@RestController` indicates response-body behavior.  
The actual object-to-JSON conversion is done by message converters.

### Misconception 4: 404 always means application is down

Wrong.

404 often means DispatcherServlet did not find a matching handler.

### Misconception 5: Filters and interceptors are the same

Wrong.

Simplified model:

```text
Filter      → Servlet container level, before DispatcherServlet
Interceptor → Spring MVC level, around controller execution
```

```text
HTTP request
    ↓
Servlet Filter
    ↓
DispatcherServlet
    ↓
Spring MVC Interceptor
    ↓
Controller
```

---

## Java/Spring Boot Code With Internal Execution Explanation

### Controller

```java
@RestController
@RequestMapping("/api/payments")
class PaymentController {

    private final PaymentService paymentService;

    PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    PaymentResponse createPayment(@RequestBody CreatePaymentRequest request) {
        return paymentService.createPayment(request);
    }
}
```

### DTOs

```java
record CreatePaymentRequest(Long orderId, Integer amountInCents) {}
record PaymentResponse(String paymentId, String status) {}
```

### Service

```java
@Service
class PaymentService {
    PaymentResponse createPayment(CreatePaymentRequest request) {
        return new PaymentResponse("pay_123", "SUCCESS");
    }
}
```

### Request

```http
POST /api/payments HTTP/1.1
Content-Type: application/json
Accept: application/json

{
  "orderId": 77,
  "amountInCents": 4999
}
```

### Internal Flow

```text
1. Tomcat receives POST /api/payments.
2. DispatcherServlet receives HttpServletRequest.
3. HandlerMapping finds PaymentController#createPayment.
4. HandlerAdapter prepares invocation.
5. @RequestBody resolver reads request body.
6. Jackson converts JSON into CreatePaymentRequest.
7. Controller method executes.
8. Service returns PaymentResponse.
9. Return value handler sees response body behavior.
10. Jackson converts PaymentResponse into JSON.
11. DispatcherServlet completes request.
```

Response:

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "paymentId": "pay_123",
  "status": "SUCCESS"
}
```

---

## HandlerMapping Mental Model

During startup:

```text
Scan controller beans
    ↓
Read @RequestMapping, @GetMapping, @PostMapping
    ↓
Create mapping registry
```

Registry picture:

```text
┌────────────┬──────────────────────┬────────────────────────────────┐
│ Method     │ Path                 │ Handler                        │
├────────────┼──────────────────────┼────────────────────────────────┤
│ GET        │ /api/users/{id}      │ UserController#getUser         │
│ POST       │ /api/orders          │ OrderController#createOrder    │
│ POST       │ /api/payments        │ PaymentController#createPayment│
└────────────┴──────────────────────┴────────────────────────────────┘
```

Runtime:

```text
Request: POST /api/orders
    ↓
Lookup registry
    ↓
Return OrderController#createOrder
```

---

## HandlerAdapter Mental Model

DispatcherServlet does not want to know how to call every handler type.

So it asks adapters:

```text
Can you invoke this handler?
```

```text
DispatcherServlet
      │
      ▼
┌──────────────────────┐
│ HandlerAdapter list  │
├──────────────────────┤
│ Adapter A            │ no
│ Adapter B            │ yes
│ Adapter C            │ no
└──────────────────────┘
      │
      ▼
Invoke handler through matching adapter
```

This keeps DispatcherServlet generic.

---

## Interceptor Flow

Interceptors are Spring MVC hooks around handler execution.

```text
preHandle
    ↓
controller
    ↓
postHandle
    ↓
afterCompletion
```

Example:

```java
@Component
class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {
        System.out.println("Incoming: " + request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {
        System.out.println("Completed: " + response.getStatus());
    }
}
```

Mental model:

```text
DispatcherServlet
    ↓
Interceptor preHandle
    ↓
Controller
    ↓
Interceptor postHandle
    ↓
Response rendering
    ↓
Interceptor afterCompletion
```

If `preHandle` returns false:

```text
Request stops before controller.
```

---

## Exception Flow

Controller:

```java
@GetMapping("/api/users/{id}")
UserDto getUser(@PathVariable Long id) {
    throw new UserNotFoundException(id);
}
```

Exception handler:

```java
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("USER_NOT_FOUND", ex.getMessage()));
    }
}
```

Flow:

```text
Controller throws UserNotFoundException
    ↓
DispatcherServlet catches exception
    ↓
HandlerExceptionResolver chain runs
    ↓
@ExceptionHandler method selected
    ↓
ErrorResponse converted to JSON
    ↓
404 returned
```

---

## Performance Considerations

`DispatcherServlet` coordination overhead is usually small compared with downstream work.

Main performance concerns around this pipeline:

```text
1. Expensive JSON serialization/deserialization
2. Large request bodies
3. Slow validation
4. Blocking controller logic
5. Remote service latency
6. Database latency
7. Thread pool exhaustion in servlet container
8. Too many interceptors doing heavy work
```

Production rule:

```text
Do not optimize DispatcherServlet first.
Measure controller latency, DB latency, HTTP client latency, and serialization cost.
```

Useful metrics:

```text
http.server.requests
request count
request duration
status code distribution
p95 / p99 latency
Tomcat thread usage
error rate by endpoint
```

---

## Scalability Considerations

The DispatcherServlet scales horizontally with application pods.

```text
                   ┌───────────────┐
                   │ Load Balancer │
                   └───────┬───────┘
                           │
          ┌────────────────┼────────────────┐
          ▼                ▼                ▼
┌────────────────┐ ┌────────────────┐ ┌────────────────┐
│ Spring Pod A   │ │ Spring Pod B   │ │ Spring Pod C   │
│ DispatcherServ │ │ DispatcherServ │ │ DispatcherServ │
└────────────────┘ └────────────────┘ └────────────────┘
```

Each pod has its own servlet container and DispatcherServlet.

Scaling checklist:

```text
- Keep controllers stateless.
- Keep request processing bounded.
- Avoid blocking calls without timeouts.
- Use connection pools correctly.
- Avoid huge DTOs.
- Avoid heavy work inside interceptors.
- Use async/messaging for long-running work.
```

---

## Senior Engineer Debugging Questions

When debugging a request, a senior engineer asks:

```text
1. Is the failure before or after controller invocation?
2. Which layer produced the status code?
3. Did Spring Security/filter chain reject it before MVC?
4. Did HandlerMapping find the correct method?
5. Did argument binding fail?
6. Did validation fail?
7. Did the controller throw?
8. Did serialization fail after the controller returned?
9. Did @ControllerAdvice rewrite the response?
10. Is the observed latency controller time or total request time?
```

This is how you avoid random debugging.

---

## Interview Q&A

### Q1. What is DispatcherServlet?

Strong answer:

> `DispatcherServlet` is the central front controller in Spring MVC. It receives HTTP requests from the servlet container, finds the matching handler using HandlerMappings, invokes it through a HandlerAdapter, processes return values using converters or view resolvers, handles exceptions, and sends the HTTP response.

### Q2. Does DispatcherServlet call controller methods directly?

Strong answer:

> Conceptually it coordinates controller invocation, but internally it uses a HandlerAdapter. This keeps DispatcherServlet independent of specific handler types and lets Spring support annotated controllers and other handler styles.

### Q3. What happens when a request comes to a Spring Boot REST API?

Strong answer:

> The embedded servlet container receives the request and dispatches it to DispatcherServlet. DispatcherServlet uses HandlerMapping to find the controller method, HandlerAdapter to invoke it, argument resolvers to bind inputs, message converters to read or write JSON, and exception resolvers if something fails.

### Q4. Why do we get 404 in Spring MVC?

Strong answer:

> Usually HandlerMapping did not find a matching handler for the path and HTTP method. It can be caused by wrong URL, wrong context path, missing component scan, wrong request mapping, or the controller bean not being registered.

### Q5. Difference between filter and interceptor?

Strong answer:

> A filter belongs to the servlet container chain and runs before the request reaches DispatcherServlet. An interceptor belongs to Spring MVC and runs around handler execution after DispatcherServlet has selected a handler.

### Q6. Who converts Java objects to JSON?

Strong answer:

> The controller returns a Java object, but HTTP message converters, commonly Jackson-based converters, serialize that object into JSON for the response body.

### Q7. Can request fail before controller method runs?

Strong answer:

> Yes. Routing can fail, HTTP method can mismatch, argument binding can fail, validation can fail, content type can be unsupported, JSON can be malformed, filters or interceptors can block the request, and security can reject it before controller execution.

---

## Cheat Sheet

```text
DispatcherServlet
= central Spring MVC front controller

HandlerMapping
= finds which controller method matches request

HandlerAdapter
= knows how to invoke that handler

ArgumentResolver
= builds Java method arguments from HTTP request

HttpMessageConverter
= converts body JSON ↔ Java object

HandlerExceptionResolver
= converts exceptions into HTTP responses

ViewResolver
= converts view name into actual rendered view

Filter
= servlet container-level hook before DispatcherServlet

Interceptor
= Spring MVC-level hook around controller execution
```

Status code memory:

```text
404 → no matching handler
405 → path exists but HTTP method wrong
400 → bad input, binding, validation, malformed body
415 → unsupported Content-Type
406 → cannot produce requested Accept type
500 → unhandled exception or server-side failure
```

---

## One Picture To Remember

```text
                         ONE FRONT DOOR

HTTP Request
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│                    DispatcherServlet                         │
│                                                             │
│  "I do not do business logic.                               │
│   I coordinate the HTTP-to-controller-to-response journey."  │
└───────────────┬─────────────────────────────────────────────┘
                │
                ▼
        ┌───────────────┐
        │ HandlerMapping │  Who handles this URL?
        └───────┬───────┘
                ▼
        ┌───────────────┐
        │ HandlerAdapter │  How do I call it?
        └───────┬───────┘
                ▼
        ┌───────────────┐
        │ Controller     │  Business endpoint
        └───────┬───────┘
                ▼
        ┌───────────────┐
        │ Converter      │  Java object → HTTP response
        └───────┬───────┘
                ▼
HTTP Response
```

Final retention sentence:

> `DispatcherServlet` is the **front door and traffic controller** of Spring MVC: it receives every request, finds the right controller, prepares the call, handles the result, and turns it into an HTTP response.

