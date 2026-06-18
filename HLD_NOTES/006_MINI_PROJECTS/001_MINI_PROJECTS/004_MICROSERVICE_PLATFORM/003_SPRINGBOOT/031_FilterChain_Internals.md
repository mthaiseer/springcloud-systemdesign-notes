# 031_FilterChain_Internals — The Pre-Controller Conveyor Belt Model

## Core Mental Model

Do not imagine a Spring Boot request as:

```text
HTTP request -> Controller
```

That skips a critical production layer.

The better mental model is:

> **The servlet filter chain is a pre-controller conveyor belt. Each filter gets a chance to inspect, modify, allow, or stop the request before it reaches Spring MVC.**

```text
HTTP Request
    |
    v
+-------------------+
| Filter 1          |  logging / request id
+-------------------+
    |
    v
+-------------------+
| Filter 2          |  CORS
+-------------------+
    |
    v
+-------------------+
| Filter 3          |  security
+-------------------+
    |
    v
+-------------------+
| DispatcherServlet |
+-------------------+
    |
    v
Controller
```

This chapter teaches exactly one idea:

> **A filter chain is an ordered set of gates around the request. A filter can pass the request forward with `chain.doFilter()` or stop it by writing a response.**

If you remember only one sentence:

> **No `chain.doFilter()` means the request stops before the controller.**

---

## Why This Exists

Many cross-cutting concerns must happen before controller logic.

Examples:

```text
request ID
logging
CORS
authentication
authorization
rate limiting
compression
encoding
tracing
tenant resolution
IP allowlist
maintenance mode
```

You do not want this repeated in every controller:

```java
@GetMapping("/orders")
public List<OrderResponse> orders(HttpServletRequest request) {
    validateCors(request);
    validateToken(request);
    checkRateLimit(request);
    putRequestIdInLogs(request);
    return orderService.orders();
}
```

That is duplicated and unsafe.

Filters exist to centralize request-level concerns.

```text
Controller should handle endpoint logic.
Filters should handle request pipeline concerns.
```

A filter can protect all endpoints without touching every controller.

---

## Problem Statement

A request arrives:

```http
GET /api/orders
Authorization: Bearer token
Origin: https://app.example.com
```

Before controller runs, the system may need to answer:

```text
1. Is this origin allowed?
2. Is this request authenticated?
3. Should this client be rate limited?
4. What request ID should be attached to logs?
5. Should this tenant be resolved?
6. Should the request continue?
7. Should the response be rejected immediately?
```

The core problem:

> **How can Spring Boot apply these cross-cutting web rules consistently before controller code runs?**

The answer:

```text
Servlet filters form a chain.
Each filter receives request/response.
Each filter decides:
  - continue to next filter
  - stop and write response
```

---

## Real World Analogy

Imagine entering a secure office building.

You pass through multiple gates:

```text
Front door
    |
    v
Reception check
    |
    v
ID card scanner
    |
    v
Security guard
    |
    v
Elevator access
    |
    v
Office room
```

Each gate can:

```text
allow you forward
ask for more info
deny entry
log your visit
attach visitor badge
```

Mapping:

```text
Building gate                 Servlet filter
Visitor                       HTTP request
Visitor badge                 request attribute / SecurityContext
Allow next gate               chain.doFilter()
Deny entry                    write response and stop
Office room                   controller
```

Important:

> **If security stops you at the gate, the office worker never sees you.**

If a filter stops the request, the controller never runs.

---

## The One Mental Model

A filter has two halves:

```text
Before chain.doFilter()
  pre-processing before downstream

chain.doFilter()
  pass request to next filter/controller

After chain.doFilter()
  post-processing after downstream response returns
```

ASCII:

```text
Filter A before
    |
    v
Filter B before
    |
    v
Filter C before
    |
    v
Controller
    |
    v
Filter C after
    |
    v
Filter B after
    |
    v
Filter A after
```

This is extremely important.

Request travels forward.
Response unwinds backward.

---

## Core Concepts

## Servlet Filter

A servlet filter is a component from the Java servlet model.

It can intercept requests before servlet/controller execution.

Core method:

```java
void doFilter(ServletRequest request,
              ServletResponse response,
              FilterChain chain)
```

In Spring Boot, filters commonly use:

```java
OncePerRequestFilter
```

## FilterChain

`FilterChain` represents the remaining chain.

```java
chain.doFilter(request, response);
```

This means:

```text
Continue to next filter.
If no filters remain, continue to DispatcherServlet.
```

If you do not call it:

```text
Request stops here.
```

## Pre-Processing

Code before `chain.doFilter()`.

Examples:

```text
generate request ID
check token
check CORS
check rate limit
start timer
put values in MDC
```

## Post-Processing

Code after `chain.doFilter()` returns.

Examples:

```text
log response status
record latency
cleanup MDC
add response header
release request resources
```

## Short-Circuit

A filter short-circuits when it writes a response and does not call the next filter.

Example:

```text
rate limit exceeded -> return 429
invalid token -> return 401
forbidden IP -> return 403
maintenance mode -> return 503
```

## Ordering

Filters are ordered.

Order matters because one filter may depend on another.

Example:

```text
RequestIdFilter before LoggingFilter
AuthenticationFilter before AuthorizationFilter
CorsFilter before SecurityFilter
```

Bad order can create subtle bugs.

## DispatcherServlet

DispatcherServlet is reached only after filters allow the request.

```text
Filters -> DispatcherServlet -> Controller
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
Servlet Filter Chain
  |
  +--> Filter 1
  |
  +--> Filter 2
  |
  +--> Filter 3
  |
  v
DispatcherServlet
  |
  v
HandlerMapping
  |
  v
Controller
```

Spring Security adds its own internal chain:

```text
Servlet Filter Chain
  |
  v
DelegatingFilterProxy
  |
  v
Spring Security FilterChainProxy
  |
  v
Security filters
```

So there can be:

```text
container filter chain
Spring Security internal filter chain
Spring MVC handler chain
```

Do not mix them mentally.

This chapter focuses on servlet filter chain.

---

## Internal Working

Given a filter:

```java
@Component
public class RequestIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();
        response.setHeader("X-Request-Id", requestId);

        try {
            chain.doFilter(request, response);
        } finally {
            // cleanup
        }
    }
}
```

Runtime:

```text
1. Tomcat receives HTTP request.
2. Tomcat enters servlet filter chain.
3. RequestIdFilter runs before code.
4. It generates requestId.
5. It sets response header.
6. It calls chain.doFilter().
7. Next filter runs.
8. Eventually DispatcherServlet/controller runs.
9. Response returns back to RequestIdFilter.
10. finally block executes cleanup.
11. Response is sent to client.
```

A filter is like a wrapper around everything after it.

---

## Rich ASCII Diagram — Request Forward, Response Backward

```text
REQUEST DIRECTION
-----------------

Client
  |
  v
Filter A: before
  |
  v
Filter B: before
  |
  v
Filter C: before
  |
  v
DispatcherServlet
  |
  v
Controller


RESPONSE DIRECTION
------------------

Controller returns
  |
  v
DispatcherServlet returns
  |
  v
Filter C: after
  |
  v
Filter B: after
  |
  v
Filter A: after
  |
  v
Client receives response
```

Mental picture:

```text
Filters nest like Russian dolls.
```

---

## Rich ASCII Diagram — Short-Circuit

```text
HTTP Request
    |
    v
RequestIdFilter
    |
    v
RateLimitFilter
    |
    | too many requests
    v
Writes 429 response
Does NOT call chain.doFilter()
    |
    v
Response to client

Controller never runs.
Service never runs.
Database never touched.
```

Short-circuit is not an error by itself.
It is intentional gatekeeping.

---

## Java Code Example — Request Logging Filter

```java
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();

        try {
            log.info("request started method={} uri={}",
                    request.getMethod(),
                    request.getRequestURI());

            chain.doFilter(request, response);

        } finally {
            long duration = System.currentTimeMillis() - start;

            log.info("request completed method={} uri={} status={} durationMs={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);
        }
    }
}
```

Execution:

```text
Before chain:
  log request start

During chain:
  other filters + controller execute

After chain:
  log status and duration
```

Why `finally`?

```text
Even if controller throws exception,
cleanup/logging should still happen.
```

---

## Java Code Example — Rate Limit Filter

Simple conceptual filter:

```java
@Component
public class SimpleRateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;

    public SimpleRateLimitFilter(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();

        if (!rateLimiterService.allow(clientIp)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Too many requests\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}
```

Key point:

```text
return without chain.doFilter() stops request.
```

This protects downstream resources.

---

## Java Code Example — Tenant Filter

Multi-tenant apps often resolve tenant before controller.

```java
@Component
public class TenantFilter extends OncePerRequestFilter {

    public static final String TENANT_ATTRIBUTE = "tenantId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String tenantId = request.getHeader("X-Tenant-Id");

        if (tenantId == null || tenantId.isBlank()) {
            response.setStatus(400);
            response.getWriter().write("Missing tenant");
            return;
        }

        request.setAttribute(TENANT_ATTRIBUTE, tenantId);

        chain.doFilter(request, response);
    }
}
```

Controller:

```java
@GetMapping("/api/orders")
public List<OrderResponse> orders(HttpServletRequest request) {
    String tenantId = (String) request.getAttribute(TenantFilter.TENANT_ATTRIBUTE);
    return orderService.ordersForTenant(tenantId);
}
```

Better production design may use a dedicated context object and cleanup strategy.

But the mental model is:

```text
Filter enriches request before controller.
```

---

## Step-by-Step Dry Run — Normal Flow

Filters:

```text
1. RequestIdFilter
2. LoggingFilter
3. RateLimitFilter
4. SecurityFilter
```

Request:

```http
GET /api/orders
Authorization: Bearer valid-token
```

Flow:

```text
1. Tomcat receives request.
2. RequestIdFilter creates request ID.
3. LoggingFilter logs start.
4. RateLimitFilter checks client quota.
5. Quota allowed.
6. SecurityFilter validates token.
7. Token valid.
8. DispatcherServlet receives request.
9. Controller handles /api/orders.
10. Service and repository execute.
11. Controller returns response.
12. SecurityFilter post-processing returns.
13. RateLimitFilter returns.
14. LoggingFilter logs status/duration.
15. RequestIdFilter cleans up.
16. Client receives response.
```

Response:

```text
200 OK
```

---

## Step-by-Step Dry Run — Filter Stops Request

Request:

```http
GET /api/orders
Authorization: missing
```

Flow:

```text
1. RequestIdFilter runs.
2. LoggingFilter logs start.
3. RateLimitFilter allows.
4. SecurityFilter checks credentials.
5. Credentials missing.
6. SecurityFilter writes 401.
7. SecurityFilter does not call chain.doFilter().
8. DispatcherServlet is never reached.
9. Controller is never called.
10. LoggingFilter logs status 401.
11. Client receives 401.
```

Debug point:

```text
If controller breakpoint is not hit, check filters.
```

---

## Step-by-Step Dry Run — Exception Flow

Controller throws:

```java
throw new RuntimeException("boom");
```

Flow:

```text
1. Filter A before runs.
2. Filter B before runs.
3. Controller throws exception.
4. DispatcherServlet exception handling may map it.
5. Response starts returning.
6. Filter B finally block runs.
7. Filter A finally block runs.
8. Response sent.
```

If exception is not handled:

```text
container error handling returns 500
```

Filters can still run cleanup.

This is why cleanup belongs in `finally`.

---

## Step-by-Step Dry Run — Missing `chain.doFilter()` Bug

Bad filter:

```java
@Component
public class BrokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        response.setHeader("X-App", "shop");

        // forgot chain.doFilter(request, response)
    }
}
```

Result:

```text
Request stops silently.
Controller never runs.
Response may be empty.
Status may be 200 depending container/default behavior.
```

This bug is painful because logs may not show an exception.

Fix:

```java
chain.doFilter(request, response);
```

Unless intentionally stopping.

---

## Spring Boot Filter Ordering

Use `@Order`:

```java
@Component
@Order(1)
public class RequestIdFilter extends OncePerRequestFilter {
}
```

```java
@Component
@Order(2)
public class LoggingFilter extends OncePerRequestFilter {
}
```

Lower number means earlier.

Alternative registration:

```java
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> loggingFilterRegistration(
            RequestLoggingFilter filter
    ) {
        FilterRegistrationBean<RequestLoggingFilter> registration =
                new FilterRegistrationBean<>();

        registration.setFilter(filter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(10);

        return registration;
    }
}
```

Use this when you need:

```text
specific URL patterns
specific order
enabled/disabled control
```

---

## Filter vs Interceptor vs AOP

Do not mix these.

```text
Filter:
  servlet layer, before DispatcherServlet

HandlerInterceptor:
  Spring MVC layer, after handler mapping, before controller

AOP:
  Spring bean method layer, around service/repository methods
```

ASCII:

```text
HTTP Request
  |
  v
Filter
  |
  v
DispatcherServlet
  |
  v
HandlerInterceptor
  |
  v
Controller
  |
  v
Service Proxy / AOP
  |
  v
Service Method
```

Use filter for:

```text
request ID
CORS
security
raw request concerns
rate limiting
```

Use interceptor for:

```text
MVC handler-specific logic
controller timing
locale/theme
```

Use AOP for:

```text
service method transactions
method-level logging
business operation metrics
```

---

## Production Scale Example

Imagine a service with 20,000 RPS.

Filters:

```text
RequestIdFilter
LoggingFilter
CorsFilter
SecurityFilter
RateLimitFilter
TracingFilter
```

Each filter adds small cost.

If one filter performs a remote call:

```text
SecurityFilter calls user-service for every request
user-service latency increases
all requests slow down
Tomcat threads block
p99 explodes
```

Production rule:

> **Filters run on the hot path of every matching request. Keep them fast, local, bounded, and observable.**

Good filter behavior:

```text
constant-time checks
local token validation
short timeouts
no heavy DB calls
no large body reads unless required
clean up ThreadLocal/MDC
clear error handling
```

Bad filter behavior:

```text
remote API call without timeout
database query for every request
reading full request body accidentally
blocking long operation
forgetting cleanup
logging huge payloads
```

---

## Production Failure Story

A team added a custom audit filter.

It logged request body and response body for all APIs.

In staging, it looked useful.

In production:

```text
CPU increased
memory increased
large uploads failed
p99 latency doubled
some controllers saw empty request body
```

Root causes:

```text
Filter read request InputStream directly.
Request body stream can be consumed only once unless wrapped.
Large body logging created memory pressure.
Sensitive data was logged.
Filter ran for every endpoint, including file uploads.
```

Fix:

```text
Do not log full bodies by default.
Use ContentCachingRequestWrapper carefully.
Limit body size.
Mask sensitive fields.
Exclude upload endpoints.
Use sampling.
Move detailed audit to business events.
```

Lesson:

> **A filter is powerful because it sees every request; that also makes it dangerous.**

---

## Debugging Mindset

When request behavior is strange, ask:

```text
1. Did the request reach the filter chain?
2. Which filters ran?
3. In what order?
4. Did any filter short-circuit?
5. Did every non-short-circuit filter call chain.doFilter()?
6. Did a filter modify headers/body/attributes?
7. Did a filter consume request body?
8. Did a filter throw exception?
9. Did cleanup run?
10. Did post-processing change response?
```

### Symptom Map

```text
Controller not called
  -> filter short-circuit
  -> missing chain.doFilter()
  -> security/CORS/rate limit

Empty request body in controller
  -> filter consumed InputStream

Unexpected 401/403
  -> security filter decision

Unexpected 429
  -> rate limit filter

Missing request ID in logs
  -> filter order or MDC cleanup issue

Wrong CORS behavior
  -> CORS filter order/config

Latency before controller
  -> slow filter

Response header missing
  -> filter not applied or overwritten later
```

---

## Common Misconceptions

## Misconception 1 — “Filters are Spring MVC controllers”

No.

Filters run before DispatcherServlet.
They are servlet-level components.

## Misconception 2 — “Filter order does not matter”

Order matters a lot.

Authentication before logging may affect logged user.
CORS before security may affect preflight.
Request ID before logging helps correlation.

## Misconception 3 — “If a filter writes a response, controller still runs”

No.

If the filter does not call `chain.doFilter()`, downstream does not run.

## Misconception 4 — “Reading request body in a filter is harmless”

No.

The request body stream may be consumed.
Controller may then see empty body unless wrapper is used.

## Misconception 5 — “Filters are good for business logic”

No.

Filters are for request pipeline concerns.
Business logic belongs in service/domain.

## Misconception 6 — “Filters should call external services freely”

Dangerous.

Filters are on the hot path and can block all requests.
Use timeouts, caching, local validation, or move logic elsewhere.

---

## Performance Considerations

Filters affect every matching request.

Cost per request:

```text
filter cost × request rate
```

Example:

```text
5 ms extra filter work
10,000 RPS
= 50 seconds of CPU/wait work per second across fleet
```

Performance rules:

```text
Keep filters lightweight.
Avoid blocking IO.
Avoid full body logging.
Avoid large allocations.
Use MDC carefully.
Clean ThreadLocal.
Set timeouts.
Measure latency by filter/stage.
```

If filter must call external service:

```text
use timeout
use circuit breaker
use cache
use fallback
consider moving to controller/service
```

---

## Scalability Considerations

At scale, filters become admission-control gates.

Good uses:

```text
rate limiting
authentication
request size protection
tenant isolation
maintenance mode
request tracing
```

They can protect downstream systems.

Example:

```text
RateLimitFilter rejects abusive client before DB.
```

Bad use:

```text
Filter performs DB lookup for every request before rate limit.
```

Correct order:

```text
cheap rejection first
expensive validation later
```

Example order:

```text
RequestId
CORS
Request size check
Rate limit
Authentication
Authorization
Controller
```

Exact order depends on app needs.

---

## Failure Investigation Playbook

## Step 1 — Add request-stage visibility

Log safely:

```text
request id
filter name
enter/exit
status
duration
```

Do not log tokens or sensitive body.

## Step 2 — Confirm order

Check:

```text
@Order
FilterRegistrationBean
Spring Security filter chain
container registration
```

## Step 3 — Check short-circuit paths

Search:

```text
return;
response.setStatus(...)
sendError(...)
```

before `chain.doFilter()`.

## Step 4 — Check body handling

If controller body is empty:

```text
Did filter call request.getInputStream()?
Did filter call request.getReader()?
Is ContentCachingRequestWrapper used correctly?
```

## Step 5 — Check ThreadLocal/MDC cleanup

Use:

```java
try {
    chain.doFilter(request, response);
} finally {
    MDC.clear();
}
```

Thread pools reuse threads.
Leaking context can pollute later requests.

## Step 6 — Check production cost

Measure:

```text
filter latency
error rate by filter
external dependency latency
allocation/memory
payload logging size
```

---

## Interview Q&A

### Q1. What is a servlet filter?

Strong answer:

> A servlet filter is a component that intercepts HTTP requests and responses before they reach the servlet or controller. It can inspect, modify, continue, or stop the request by deciding whether to call `chain.doFilter()`.

### Q2. What happens if a filter does not call `chain.doFilter()`?

Strong answer:

> The request stops at that filter. Downstream filters, DispatcherServlet, controllers, services, and repositories are not called. This is correct for intentional short-circuit responses like 401, 403, 429, or 503.

### Q3. What is the difference between filter and interceptor?

Strong answer:

> A filter runs at the servlet layer before DispatcherServlet. A HandlerInterceptor runs inside Spring MVC after handler mapping and before controller invocation. Filters are better for raw request concerns like security, CORS, tracing, and rate limiting.

### Q4. Why is filter order important?

Strong answer:

> Filters run sequentially on the way in and unwind in reverse on the way out. If order is wrong, one filter may not see data prepared by another, CORS/security may behave incorrectly, or logging may miss request identity.

### Q5. Why can reading request body in a filter be dangerous?

Strong answer:

> The request body input stream can usually be consumed only once. If a filter reads it directly, the controller's `@RequestBody` may receive an empty stream unless the request is wrapped and cached properly.

### Q6. How does Spring Security use filters?

Strong answer:

> Spring Security is integrated into the servlet filter chain through DelegatingFilterProxy, which delegates to Spring’s FilterChainProxy. That internal security chain authenticates and authorizes requests before they reach controllers.

### Q7. What should filters not do?

Strong answer:

> Filters should not contain business logic, heavy blocking work, unbounded external calls, unsafe body logging, or long-running operations. They run on the request hot path and can affect every endpoint.

---

## Production Checklist

```text
Filter Design
[ ] Does this concern belong before controller?
[ ] Is the filter lightweight?
[ ] Does it call chain.doFilter() unless intentionally stopping?
[ ] Are short-circuit responses clear?
[ ] Are sensitive headers/body not logged?

Ordering
[ ] Request ID before logging
[ ] CORS order correct
[ ] Rate limit before expensive work if possible
[ ] Security order understood
[ ] FilterRegistrationBean/@Order explicit where needed

Safety
[ ] Timeouts on external calls
[ ] No heavy DB work in filter
[ ] Request body not consumed accidentally
[ ] MDC/ThreadLocal cleaned in finally
[ ] Large payloads protected

Observability
[ ] Filter latency visible
[ ] Rejection count visible
[ ] Status codes visible
[ ] Request ID propagated
[ ] Errors logged with correlation ID
```

---

## One-Page Cheat Sheet

```text
Filter Chain Internals
======================

Core Idea
---------
Pre-controller conveyor belt.

Filter can:
  inspect request
  modify request/response
  add attributes/headers
  reject request
  continue chain

Key Method
----------
chain.doFilter(request, response)

If called:
  next filter/controller runs

If not called:
  request stops here

Flow
----
Filter A before
  Filter B before
    Controller
  Filter B after
Filter A after

Short-Circuit
-------------
write response
return without chain.doFilter()

Common Uses
-----------
security
CORS
request ID
logging
tracing
rate limiting
tenant resolution
encoding

Danger Zones
------------
wrong order
missing chain.doFilter
reading request body
slow external calls
business logic in filter
ThreadLocal/MDC leaks

Best Sentence
-------------
A filter is a gate before controller:
continue with chain.doFilter(),
or stop by writing response.
```

---

## Last-Minute Interview Revision

Do not say:

```text
Filters are like controllers.
```

Say:

```text
Filters are servlet-level components that run before DispatcherServlet and controllers. Each filter can perform pre-processing, call `chain.doFilter()` to continue, and then perform post-processing as the response unwinds. If it does not call `chain.doFilter()`, the request stops at that filter.
```

Senior version:

```text
I treat filters as hot-path gates. They are excellent for cross-cutting request concerns like security, tracing, CORS, and rate limiting, but they must be ordered, lightweight, observable, and careful with request body consumption and ThreadLocal cleanup.
```

---

## One Picture To Remember

```text
                    FILTER CHAIN MODEL

Request ->
  Filter A before
    |
    v
  Filter B before
    |
    v
  Filter C before
    |
    v
  DispatcherServlet
    |
    v
  Controller
    |
    v
  Filter C after
    |
    v
  Filter B after
    |
    v
  Filter A after
    |
    v
Response

Rule:
  chain.doFilter() = continue
  no chain.doFilter() = stop before controller
```

Final retention sentence:

> **The filter chain is the pre-controller conveyor belt where each filter either passes the request forward or stops it before business code ever runs.**
