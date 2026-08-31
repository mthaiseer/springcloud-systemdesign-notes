# CH 09 --- REST API Design

## Senior Java Distributed Backend Interview Series

**Target:** \~30 interview questions\
**Goal:** Master HTTP semantics, resource modeling, idempotency,
pagination, versioning, errors, rate limiting, OpenAPI, concurrency,
security, and production-grade API design.

------------------------------------------------------------------------

# Chapter Map

``` text
CH 09 — REST API DESIGN (~30 Q)
|
+-- 1. HTTP / REST Fundamentals (7)
|   +-- REST
|   +-- Resources
|   +-- HTTP methods
|   +-- Safe vs idempotent
|   +-- Status codes
|   +-- Headers / content negotiation
|   +-- Statelessness
|
+-- 2. Resource & Contract Design (8) ★★★
|   +-- URI design
|   +-- POST
|   +-- PUT vs PATCH
|   +-- Idempotency keys
|   +-- Pagination
|   +-- Filtering / sorting
|   +-- Versioning
|   +-- DTO / compatibility
|
+-- 3. Errors / Concurrency / Security (8) ★★★
|   +-- Error contracts
|   +-- 400 / 404 / 409 / 422 / 500
|   +-- Validation
|   +-- Optimistic concurrency / ETag
|   +-- Authentication / authorization boundary
|   +-- CORS / CSRF
|   +-- Sensitive data
|   +-- Request limits / timeouts
|
+-- 4. Production API Design (7) ★★★
    +-- Rate limiting
    +-- Retry safety
    +-- Async APIs
    +-- Webhooks
    +-- OpenAPI
    +-- Observability
    +-- API design methodology
```

------------------------------------------------------------------------

# Big Picture

``` text
Client
  |
  v
HTTPS
  |
  v
Load Balancer / API Gateway
  |
  +--> Authentication
  +--> Rate Limiting
  +--> Routing
  |
  v
Spring Boot API
  |
  +--> Filter
  +--> DispatcherServlet
  +--> Controller
  +--> Validation
  +--> Service
  |
  v
DB / Redis / Kafka / Other Services
```

A good API contract should remain understandable even if the
implementation behind it changes.

``` text
CLIENT CONTRACT
      |
      X should not depend on
      |
DB table structure
JPA entity graph
internal service topology
```

------------------------------------------------------------------------

# 1. HTTP / REST Fundamentals --- 7 Questions

## Q1. What is REST?

REST is an architectural style centered on resources and a uniform
interface.

For backend interviews, focus on:

``` text
Resources
HTTP semantics
Stateless requests
Representations
Cacheability where applicable
Layered architecture
```

Example:

``` text
Resource: Order

GET    /orders/42
POST   /orders
PATCH  /orders/42
DELETE /orders/42
```

Do not define REST merely as "JSON over HTTP."

------------------------------------------------------------------------

## Q2. What is a resource?

A resource is a conceptual domain object/addressable concept.

``` text
Customer
Order
Payment
Invoice
```

URI identifies the resource:

``` text
/orders/42
```

Representation might be JSON:

``` json
{
  "id": 42,
  "status": "PAID",
  "total": 120.50
}
```

Resource != database row. Your API model should reflect the external
contract/domain.

------------------------------------------------------------------------

## Q3. Explain HTTP methods.

``` text
GET
retrieve representation

POST
submit/create/process according to resource semantics

PUT
replace/create a resource representation at known URI semantics

PATCH
partial modification

DELETE
remove resource
```

Do not design every operation as:

``` text
POST /doSomething
```

Use HTTP semantics where they fit the domain.

------------------------------------------------------------------------

## Q4. Safe vs idempotent methods?

### Safe

A safe method is intended for retrieval without requested state change.

Common example:

``` text
GET
HEAD
```

### Idempotent

Repeating the same request has the same intended effect as executing it
once.

``` text
PUT /users/42
DELETE /users/42
```

Conceptually:

``` text
PUT status=ACTIVE
PUT status=ACTIVE
PUT status=ACTIVE

final intended state:
ACTIVE
```

Important:

``` text
idempotent
!=
response must be byte-for-byte identical
```

------------------------------------------------------------------------

## Q5. Which status codes should a senior backend engineer know?

``` text
2xx
200 OK
201 Created
202 Accepted
204 No Content

3xx
301 / 302 redirects
304 Not Modified

4xx
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
412 Precondition Failed
415 Unsupported Media Type
422 Unprocessable Content
429 Too Many Requests

5xx
500 Internal Server Error
502 Bad Gateway
503 Service Unavailable
504 Gateway Timeout
```

Use status codes consistently with your API contract.

------------------------------------------------------------------------

## Q6. What are important HTTP headers?

Examples:

``` text
Content-Type
Accept
Authorization
Cache-Control
ETag
If-Match
If-None-Match
Location
Retry-After
Idempotency-Key
X-Correlation-Id / trace context
```

Headers carry protocol metadata; do not put every protocol concern into
JSON.

------------------------------------------------------------------------

## Q7. What does stateless API mean?

Each request should carry the information required to process it without
relying on hidden server-side conversational state tied to one
application instance.

``` text
Request 1 -> Pod A
Request 2 -> Pod B
```

Both should work.

This enables:

``` text
horizontal scaling
load balancing
failover
```

Stateless does not mean the system stores no state. Business state lives
in databases/caches/etc.

------------------------------------------------------------------------

# 2. Resource & Contract Design --- 8 Questions

## Q8. How should URIs be designed?

Prefer nouns/resources:

``` text
GET /orders
GET /orders/42
GET /customers/7/orders
```

Avoid implementation-oriented paths:

``` text
/getOrderById
/createNewOrder
/updateOrderStatusNow
```

For domain commands that do not naturally map to CRUD, explicit
sub-resources/actions can be reasonable:

``` text
POST /orders/42/cancellations
POST /payments/42/refunds
```

Clarity is more important than forcing every business operation into
simplistic CRUD.

------------------------------------------------------------------------

## Q9. How should POST creation work?

Example:

``` text
POST /orders
```

Request:

``` json
{
  "customerId": 10,
  "items": [
    {"productId": 100, "quantity": 2}
  ]
}
```

Response:

``` text
201 Created
Location: /orders/987
```

Body can return the created representation/identifier according to
contract.

Flow:

``` text
POST collection
      |
validate
      |
create resource
      |
201 + resource location
```

------------------------------------------------------------------------

## Q10. PUT vs PATCH?

### PUT

Conceptually replaces the target representation.

``` text
PUT /users/42

{
  "name": "Mohamed",
  "city": "Bucharest"
}
```

### PATCH

Partial update:

``` text
PATCH /users/42

{
  "city": "Cluj"
}
```

Important design issue:

``` text
field absent
vs
field explicitly null
```

Your PATCH format must define this clearly.

------------------------------------------------------------------------

## Q11. What is an idempotency key?

Critical for payment/order APIs.

Client:

``` text
POST /payments
Idempotency-Key: abc-123
```

First request:

``` text
key abc-123
    |
process payment
    |
store result against key
```

Retry:

``` text
same key
    |
detect previous operation
    |
return/replay compatible result
    |
DO NOT charge twice
```

Storage model:

``` text
Idempotency Key
      |
      +--> request fingerprint
      +--> status
      +--> result/resource ID
      +--> expiry
```

### Senior considerations

-   same key + different payload?
-   concurrent requests with same key?
-   how long retain keys?
-   failed/in-progress state?
-   transactional relationship to business write?

------------------------------------------------------------------------

## Q12. Offset vs cursor/keyset pagination?

Offset:

``` text
GET /orders?page=100&size=50
```

Simple but deep pages can become expensive and unstable under concurrent
inserts/deletes.

Cursor:

``` text
GET /orders?limit=50&cursor=abc
```

Internally often based on stable ordering:

``` text
(created_at, id)
```

Response:

``` json
{
  "items": [],
  "nextCursor": "..."
}
```

Use cursor/keyset pagination for large feed-style datasets.

------------------------------------------------------------------------

## Q13. How should filtering and sorting work?

Example:

``` text
GET /orders?status=PAID&customerId=42&sort=-createdAt
```

Design considerations:

``` text
allowlisted fields
validated operators
stable ordering
index support
maximum page size
query complexity
```

Never blindly turn arbitrary client strings into SQL fragments.

------------------------------------------------------------------------

## Q14. How do you version APIs?

Options:

``` text
URI
/api/v1/orders

Header
Accept: application/vnd.company.v1+json

Other contract/version negotiation approaches
```

For public/external APIs, URI versioning is often easy to understand
operationally.

### Senior principle

Prefer backward-compatible evolution where possible:

``` text
adding optional response field
often compatible

renaming/removing field
breaking
```

Version only when needed; do not create a new major API version for
every internal implementation change.

------------------------------------------------------------------------

## Q15. Why use API DTOs instead of JPA entities?

``` text
API DTO
   |
stable external contract
   |
Service/domain
   |
JPA Entity
   |
DB
```

Benefits:

-   prevent persistence leakage
-   avoid lazy serialization
-   control sensitive fields
-   support versioning
-   independent evolution
-   explicit validation

This directly connects CH09 to CH07.

------------------------------------------------------------------------

# 3. Errors / Concurrency / Security --- 8 Questions

## Q16. What should a standard API error look like?

Example:

``` json
{
  "code": "ORDER_NOT_FOUND",
  "message": "Order was not found",
  "status": 404,
  "traceId": "abc123"
}
```

Validation example:

``` json
{
  "code": "VALIDATION_FAILED",
  "status": 400,
  "errors": [
    {
      "field": "email",
      "message": "must be a valid email"
    }
  ],
  "traceId": "abc123"
}
```

Good properties:

``` text
stable machine code
safe human message
HTTP status
trace/correlation identifier
structured field errors
```

------------------------------------------------------------------------

## Q17. 400 vs 404 vs 409 vs 422 vs 500?

Useful mental model:

``` text
400
request syntax/binding/validation malformed or invalid

404
target resource does not exist

409
request conflicts with current resource/system state

422
request understood but semantic/content validation cannot be processed
depending on API conventions

500
unexpected server failure
```

Examples:

``` text
invalid JSON               -> 400
order 999 absent           -> 404
duplicate unique username  -> 409
business-invalid content   -> 422 or domain-defined 4xx convention
NullPointerException       -> 500
```

Consistency matters.

------------------------------------------------------------------------

## Q18. Input validation vs business validation?

``` text
HTTP validation
|
+-- required field
+-- email format
+-- range
+-- string length

Business validation
|
+-- account has balance
+-- order may be cancelled
+-- coupon applicable
+-- inventory available
```

Layering:

``` text
Request DTO
   |
structural validation
   |
Service/domain
   |
business rules
```

------------------------------------------------------------------------

## Q19. How do you prevent lost updates through an API?

Two clients:

``` text
Client A reads Order version 5
Client B reads Order version 5

A updates -> version 6
B updates stale version 5
```

Options include application version fields or HTTP conditional requests.

ETag approach:

``` text
GET /orders/42

ETag: "v5"
```

Update:

``` text
PUT /orders/42
If-Match: "v5"
```

Server:

``` text
current = v5?
   |
 YES -> update -> v6
 NO  -> 412 Precondition Failed
```

This connects HTTP concurrency to CH07 optimistic locking.

------------------------------------------------------------------------

## Q20. Where should authentication and authorization happen?

Typical layers:

``` text
API Gateway
   |
coarse authentication/routing controls
   |
Spring Security
   |
endpoint/method authorization
   |
Service/domain
   |
business authorization/invariants
```

Do not rely only on UI hiding buttons.

Example:

``` text
"User can view order"
```

must be enforced server-side.

------------------------------------------------------------------------

## Q21. CORS vs CSRF?

### CORS

Browser-enforced cross-origin request policy.

``` text
frontend.example
      |
      v
api.example
```

Server indicates which origins/methods/headers browsers may allow.

### CSRF

Attack where browser credentials are automatically included in an
unwanted state-changing request.

Risk depends heavily on authentication mechanism.

``` text
cookie-based auth
   |
browser automatically sends cookie
   |
CSRF protection may be needed
```

Bearer tokens explicitly placed in authorization headers have different
threat characteristics.

Covered deeply in CH13 Security.

------------------------------------------------------------------------

## Q22. How do you prevent sensitive-data leakage?

Avoid returning/logging:

``` text
password hashes
access tokens
refresh tokens
full card data
secrets
internal stack traces
SQL
internal hostnames
unnecessary PII
```

Use explicit response DTOs.

``` text
Entity
  |
  X serialize blindly
  |
Response DTO
  |
allowlisted fields
```

------------------------------------------------------------------------

## Q23. Why do APIs need request limits and timeouts?

Without limits:

``` text
client sends 2 GB JSON
      |
memory pressure
      |
GC
      |
OOM
```

Other limits:

``` text
max body size
max upload size
max page size
header limits
query complexity
request timeout
downstream timeout
```

Resource limits are part of API design and abuse resilience.

------------------------------------------------------------------------

# 4. Production API Design --- 7 Questions

## Q24. What is rate limiting?

Rate limiting controls request volume.

Common algorithms:

``` text
Fixed Window
Sliding Window
Token Bucket
Leaky Bucket
```

Example:

``` text
100 requests / minute / user
```

Token bucket mental model:

``` text
Bucket capacity = 10

tokens refill over time

Request
  |
token available?
 +---+---+
 |       |
YES      NO
 |       |
allow   429
```

Distributed rate limiting is covered more deeply in CH10/12/23.

------------------------------------------------------------------------

## Q25. How do retries affect API design?

Retries can multiply traffic.

``` text
Service B slow
    |
Service A timeout
    |
retry x3
    |
B receives more traffic
    |
B slower
```

Retry only when:

``` text
operation safe/idempotent
failure likely transient
retry budget bounded
backoff used
jitter used
```

For POST-like side effects, use idempotency mechanisms when retries are
possible.

------------------------------------------------------------------------

## Q26. How do you design long-running operations?

Bad:

``` text
POST /reports
HTTP connection waits 5 minutes
```

Better async model:

``` text
POST /reports
      |
      v
202 Accepted
Location: /jobs/abc
      |
background processing
      |
GET /jobs/abc
      |
PENDING / RUNNING / COMPLETED / FAILED
```

Optional completion channels:

``` text
polling
webhook
SSE
WebSocket
```

depending on use case.

------------------------------------------------------------------------

## Q27. How do you design webhooks safely?

Producer:

``` text
Event occurs
   |
POST customer webhook
```

Challenges:

``` text
receiver temporarily down
duplicate delivery
timeout
out-of-order events
spoofing
```

Design:

``` text
event ID
timestamp
signature
retry policy
exponential backoff
delivery logs
idempotent receiver
```

Receiver:

``` text
Webhook event ID
      |
already processed?
 +----+----+
 |         |
YES       NO
 |         |
ignore   process
```

------------------------------------------------------------------------

## Q28. What is OpenAPI / Swagger?

OpenAPI is a machine-readable API contract/specification.

It describes:

``` text
paths
methods
parameters
request schemas
response schemas
status codes
security
```

Uses:

-   documentation
-   client generation
-   server stubs
-   testing
-   contract review
-   gateway tooling

Senior principle:

``` text
API contract
should be intentionally designed
not merely auto-generated from accidental controller shape
```

------------------------------------------------------------------------

## Q29. What should you observe for APIs in production?

Use RED:

``` text
Rate
Errors
Duration
```

Also:

``` text
status-code distribution
p50 / p95 / p99
request size
response size
rate-limit rejections
timeouts
dependency latency
Hikari pending
DB latency
cache hit ratio
Kafka lag where relevant
```

Trace:

``` text
Client
 |
API Gateway
 |
Controller
 |
Service
 |
DB
 |
Remote API
```

Use correlation/trace IDs.

------------------------------------------------------------------------

## Q30. Give a senior API-design methodology.

For a new endpoint:

``` text
1. Identify resource / business operation
2. Choose HTTP semantics
3. Define request DTO
4. Define response DTO
5. Define validation
6. Define status codes
7. Define error contract
8. Define idempotency/retry behavior
9. Define concurrency behavior
10. Define pagination/filtering if collection
11. Define authentication/authorization
12. Define rate/resource limits
13. Define backward compatibility
14. Define observability
15. Document in OpenAPI
16. Test failure scenarios
```

This is more important than memorizing annotations.

------------------------------------------------------------------------

# Senior Scenario 1 --- Duplicate Payment After Timeout

``` text
Client
  |
POST /payments
  |
Payment service charges card
  |
response lost
  |
client times out
  |
retries POST
  |
charge again
```

Fix:

``` text
Idempotency-Key: pay-abc
```

Server:

``` text
key + request fingerprint
       |
first request
       |
create payment
       |
store result
       |
retry same key
       |
return same logical result
```

Important: protect against two concurrent requests with the same key.

------------------------------------------------------------------------

# Senior Scenario 2 --- Deep Pagination Kills DB

``` text
GET /orders?page=100000&size=100
```

Internally:

``` sql
OFFSET 10000000
LIMIT 100
```

Better for sequential browsing:

``` text
GET /orders?cursor=...
```

Cursor encodes stable ordering position.

``` text
(createdAt, id)
```

This connects directly to CH08 keyset pagination.

------------------------------------------------------------------------

# Senior Scenario 3 --- API Lost Update

``` text
Order status = CREATED

Client A GET
Client B GET

A -> change address
B -> cancel

A PUT
B PUT stale full representation
```

B may overwrite A's address.

Use:

``` text
ETag / If-Match
```

or explicit version:

``` json
{
  "version": 5,
  "status": "CANCELLED"
}
```

Conflict should be explicit, not silent.

------------------------------------------------------------------------

# Senior Scenario 4 --- Retry Storm

``` text
Gateway retries 3x
Service A retries 3x
HTTP client retries 3x
```

One request can become approximately:

``` text
3 x 3 x 3
```

attempts in a pathological layered design.

Senior rule:

``` text
Retry policy must be coordinated
across layers.
```

Use timeout budgets, bounded attempts, backoff, jitter and circuit
breaking.

------------------------------------------------------------------------

# Senior Scenario 5 --- API Leaks JPA Entity

``` java
@GetMapping("/users/{id}")
public UserEntity get(...) {
    return repository.findById(...).orElseThrow();
}
```

Potential:

``` text
passwordHash leaked
lazy SQL
recursive JSON
persistence coupling
breaking contract after DB refactor
```

Use explicit API DTO.

------------------------------------------------------------------------

# Senior Scenario 6 --- 200 OK for Every Failure

Bad API:

``` text
HTTP 200

{
  "success": false,
  "error": "not found"
}
```

Problems:

``` text
HTTP infrastructure cannot reason correctly
monitoring sees false success
caching/proxies confused
clients need custom interpretation
```

Use HTTP status semantics plus a structured body.

------------------------------------------------------------------------

# Senior Scenario 7 --- Long-Running Export

Requirement:

``` text
Generate 20 GB customer export
```

Do not hold a request thread for the whole job.

``` text
POST /exports
      |
202
      |
jobId
      |
Kafka / worker
      |
object storage
      |
job COMPLETED
      |
signed/authorized download
```

Consider expiry, authorization, cancellation and retry semantics.

------------------------------------------------------------------------

# Mini Coding Drill 1 --- Create Resource

``` java
@PostMapping("/orders")
ResponseEntity<OrderResponse> create(
        @Valid @RequestBody CreateOrderRequest request) {

    OrderResponse result = orderService.create(request);

    URI location =
        URI.create("/orders/" + result.id());

    return ResponseEntity
        .created(location)
        .body(result);
}
```

------------------------------------------------------------------------

# Mini Coding Drill 2 --- Standard Error

``` java
public record ApiError(
    String code,
    String message,
    int status,
    String traceId
) {}
```

``` java
@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<ApiError> handle(...) {
        ...
    }
}
```

------------------------------------------------------------------------

# Mini Coding Drill 3 --- Idempotency

Conceptual service:

``` java
public PaymentResponse create(
        String idempotencyKey,
        PaymentRequest request) {

    // 1. validate key
    // 2. lookup/reserve key atomically
    // 3. verify request fingerprint
    // 4. return existing result if completed
    // 5. process once
    // 6. persist result
}
```

The concurrency/transaction design is more important than the controller
annotation.

------------------------------------------------------------------------

# Mini Coding Drill 4 --- Conditional Update

Request:

``` text
PATCH /orders/42
If-Match: "8"
```

Service:

``` text
current version = 8?
       |
       +-- YES -> update -> version 9
       |
       +-- NO  -> reject stale request
```

HTTP response commonly uses a precondition/conflict status according to
the API contract.

------------------------------------------------------------------------

# Mini Coding Drill 5 --- Cursor Response

``` java
public record PageResponse<T>(
    List<T> items,
    String nextCursor,
    boolean hasMore
) {}
```

Avoid exposing internal DB details directly in cursor tokens. Treat
cursor format as part of the API contract/security surface.

------------------------------------------------------------------------

# API Request Lifecycle

``` text
Client
  |
HTTPS
  |
Gateway
  |
Filter
  |
Authentication
  |
Rate Limit
  |
DispatcherServlet
  |
Controller
  |
DTO Validation
  |
Service
  |
@Transactional
  |
JPA / DB
  |
Response DTO
  |
JSON
  |
Client
```

------------------------------------------------------------------------

# Idempotency State Machine

``` text
              +----------+
new key ----> | IN_FLIGHT|
              +----+-----+
                   |
          +--------+--------+
          |                 |
          v                 v
      COMPLETED           FAILED
          |
          v
same key retry
          |
return stored result
```

Real design must define which failures are retryable and whether failed
keys may be reused.

------------------------------------------------------------------------

# Pagination Decision Tree

``` text
Need random page numbers?
      |
     YES
      |
offset may be acceptable
for bounded datasets

Need scalable sequential navigation?
      |
     YES
      |
cursor / keyset
```

Always use deterministic ordering.

------------------------------------------------------------------------

# Error Mapping Mental Model

``` text
Malformed JSON
      -> 400

Validation error
      -> 400/422 by contract

Resource absent
      -> 404

State/version conflict
      -> 409/412

Rate exceeded
      -> 429

Unexpected bug
      -> 500

Downstream unavailable
      -> 502/503/504 depending on role/failure
```

------------------------------------------------------------------------

# API Compatibility

Generally safer:

``` text
add optional response field
add optional request field with default semantics
add new endpoint
```

Potentially breaking:

``` text
remove field
rename field
change type
change required/optional semantics
change enum handling without compatibility plan
change meaning of existing field
```

Senior principle:

``` text
API evolution
is distributed deployment coordination.
```

Clients do not all upgrade simultaneously.

------------------------------------------------------------------------

# REST + Database Connection

``` text
GET /orders?cursor=X
       |
       v
Keyset API
       |
       v
WHERE (created_at,id) < (...)
       |
       v
Composite index
       |
       v
Fast bounded query
```

API design and DB design are connected.

------------------------------------------------------------------------

# REST + Transactions Connection

``` text
POST /payments
       |
Idempotency Key
       |
Service TX
       |
Payment record
       |
Outbox event
       |
COMMIT
```

This connects CH09 to CH06 distributed transaction concepts.

------------------------------------------------------------------------

# REST + Security Preview

``` text
Internet
   |
TLS
   |
Gateway
   |
Authentication
   |
Authorization
   |
Validation
   |
Business operation
```

Covered deeply in CH13.

------------------------------------------------------------------------

# REST + Microservices Preview

``` text
External Client
      |
Public API
      |
Gateway
      |
Service A
      |
      +--> REST/gRPC Service B
      |
      +--> Kafka
```

Internal service contracts need the same thinking around timeouts,
retries, compatibility and observability.

Covered in CH10.

------------------------------------------------------------------------

# Production Debugging --- 429 Spike

``` text
429 rate ↑
   |
real traffic spike?
   |
client retry loop?
   |
wrong rate-limit key?
   |
shared NAT causing aggregation?
   |
limit configuration changed?
```

------------------------------------------------------------------------

# Production Debugging --- 5xx Spike

``` text
5xx ↑
 |
trace IDs
 |
which endpoint?
 |
which exception?
 |
dependency?
 |
DB?
 |
thread/pool saturation?
 |
deployment correlation?
```

------------------------------------------------------------------------

# Production Debugging --- 504 Gateway Timeout

``` text
Client
 |
Gateway timeout 5s
 |
Service still processing 8s?
 |
DB/remote call?
```

Timeout hierarchy must make sense.

Example concept:

``` text
DB query timeout       < service budget
downstream HTTP timeout < service budget
service budget          < gateway/client budget
```

Leave room for error handling and network overhead.

------------------------------------------------------------------------

# Critical Comparison Sheet

``` text
POST
create/process, not inherently idempotent

PUT
replace at target semantics, idempotent

PATCH
partial modification
```

``` text
401
not authenticated / valid authentication missing

403
authenticated but not allowed
```

``` text
409
resource/state conflict

412
HTTP precondition failed
```

``` text
OFFSET
simple page-number model

CURSOR/KEYSET
scalable sequential navigation
```

``` text
AUTHENTICATION
Who are you?

AUTHORIZATION
Are you allowed?
```

``` text
RATE LIMIT
control request rate

TIMEOUT
bound how long work/wait may take

BULKHEAD
bound concurrency/resource usage
```

------------------------------------------------------------------------

# Senior Rapid-Fire Follow-Ups

1.  What is REST?
2.  Resource vs representation?
3.  GET/POST/PUT/PATCH/DELETE?
4.  Safe vs idempotent?
5.  Is POST idempotent by default?
6.  Important 2xx codes?
7.  401 vs 403?
8.  409 vs 412?
9.  502 vs 503 vs 504?
10. Statelessness?
11. Good URI design?
12. How return newly created resource?
13. PUT vs PATCH?
14. How represent explicit null in PATCH?
15. What is idempotency key?
16. Same key with different payload?
17. Concurrent same-key requests?
18. Offset vs cursor?
19. Stable cursor ordering?
20. Filtering/sorting safety?
21. API versioning?
22. What changes are backward compatible?
23. Why DTO instead of entity?
24. Standard error contract?
25. Input vs business validation?
26. How prevent lost updates?
27. ETag / If-Match?
28. CORS vs CSRF?
29. Rate limiting algorithms?
30. Walk through production-grade API design.

------------------------------------------------------------------------

# Interview Checklist

``` text
HTTP / REST
[ ] 01 REST
[ ] 02 Resource
[ ] 03 HTTP methods
[ ] 04 Safe / idempotent
[ ] 05 Status codes
[ ] 06 Headers
[ ] 07 Statelessness

CONTRACT DESIGN
[ ] 08 URI design
[ ] 09 POST creation
[ ] 10 PUT vs PATCH
[ ] 11 Idempotency key
[ ] 12 Pagination
[ ] 13 Filtering / sorting
[ ] 14 Versioning
[ ] 15 DTO compatibility

ERROR / SECURITY
[ ] 16 Error contract
[ ] 17 Status mapping
[ ] 18 Validation
[ ] 19 Optimistic HTTP concurrency
[ ] 20 AuthN / AuthZ
[ ] 21 CORS / CSRF
[ ] 22 Sensitive data
[ ] 23 Limits / timeouts

PRODUCTION
[ ] 24 Rate limiting
[ ] 25 Retry safety
[ ] 26 Async APIs
[ ] 27 Webhooks
[ ] 28 OpenAPI
[ ] 29 Observability
[ ] 30 Design methodology
```

------------------------------------------------------------------------

# Chapter 09 Visual Summary

``` text
                     REST API DESIGN
                           |
       +-------------------+-------------------+
       |                                       |
    CONTRACT                                PRODUCTION
       |                                       |
       v                                       v
   Resource                                  HTTPS
       |                                       |
 HTTP Method                              Authentication
       |                                       |
 Request DTO                              Rate Limiting
       |                                       |
 Validation                               Timeouts
       |                                       |
 Service                                  Observability
       |                                       |
 Response DTO                             Compatibility
       |
 Status + Headers
```

Reliability chain:

``` text
Client Retry
     |
     v
Idempotency
     |
     v
API Transaction
     |
     v
Database / Outbox
     |
     v
Stable Result
```

------------------------------------------------------------------------

# Chapter 09 Exit Criteria

You are ready for **CH 10 --- Microservices** when you can:

1.  Explain REST beyond "JSON over HTTP."
2.  Model resources and URIs cleanly.
3.  Explain safe vs idempotent.
4.  Select status codes intentionally.
5.  Explain POST/PUT/PATCH semantics.
6.  Design payment-style idempotency.
7.  Handle concurrent same-key requests.
8.  Choose offset vs cursor pagination.
9.  Design safe filtering and sorting.
10. Evolve an API backward-compatibly.
11. Design a standard error contract.
12. Separate input and business validation.
13. Prevent lost updates using version/ETag.
14. Explain authentication vs authorization.
15. Explain CORS vs CSRF.
16. Design rate limits and retry behavior.
17. Design a long-running async API.
18. Design reliable webhooks.
19. Explain OpenAPI's role.
20. Walk through an endpoint from contract to production observability.

``` text
CH 09 REST API DESIGN
        |
        v
HTTP Semantics
        |
        v
Resource Modeling
        |
        v
Idempotency ★★★
        |
        v
Pagination / Versioning
        |
        v
Errors / Concurrency
        |
        v
Rate Limits / Retries ★★★
        |
        v
Async APIs / Webhooks
        |
        v
READY FOR CH 10
MICROSERVICES
```

------------------------------------------------------------------------

## Next Chapter

``` text
CH 10 — MICROSERVICES (~55 Q)
|
+-- Service boundaries
+-- Monolith vs microservices
+-- Database per service
+-- REST vs gRPC vs messaging
+-- API Gateway / discovery
+-- Load balancing
+-- Timeout / retry / backoff
+-- Circuit breaker / bulkhead
+-- Saga
+-- Outbox
+-- Idempotency
+-- Eventual consistency
+-- Distributed locks
+-- Failure scenarios
+-- Production debugging
```
