# CH 10 --- Microservices

## Senior Java Distributed Backend Interview Series

**Target:** \~55 interview questions\
**Priority:** ★★★ Highest\
**Goal:** Master service boundaries, communication, discovery,
resilience, distributed transactions, Saga, Outbox, idempotency,
consistency, failure handling, and production debugging.

------------------------------------------------------------------------

# Chapter Map

``` text
CH 10 — MICROSERVICES (~55 Q)
|
+-- 1. Architecture / Boundaries (10)
|   +-- Monolith vs microservices
|   +-- Modular monolith
|   +-- Service boundaries
|   +-- Bounded context
|   +-- Database per service
|   +-- Shared DB problem
|   +-- Data ownership
|   +-- API contracts
|   +-- Coupling / cohesion
|   +-- When NOT to use microservices
|
+-- 2. Communication / Infrastructure (10) ★★★
|   +-- Sync vs async
|   +-- REST
|   +-- gRPC
|   +-- Messaging
|   +-- API Gateway
|   +-- Service discovery
|   +-- Client/server load balancing
|   +-- Config
|   +-- Correlation/tracing
|   +-- Contract evolution
|
+-- 3. Resilience / Failure Control (12) ★★★
|   +-- Timeout
|   +-- Retry
|   +-- Exponential backoff
|   +-- Jitter
|   +-- Retry storm
|   +-- Circuit breaker
|   +-- Bulkhead
|   +-- Rate limiting
|   +-- Backpressure
|   +-- Fallback
|   +-- Deadline/budget propagation
|   +-- Resilience4j
|
+-- 4. Distributed Transactions / Consistency (13) ★★★
|   +-- Why local @Transactional is insufficient
|   +-- 2PC / XA
|   +-- Saga
|   +-- Choreography
|   +-- Orchestration
|   +-- Compensation
|   +-- Transactional Outbox
|   +-- CDC / polling publisher
|   +-- Dual-write problem
|   +-- Idempotent consumer
|   +-- Deduplication
|   +-- Eventual consistency
|   +-- Distributed locks
|
+-- 5. Production / Senior Scenarios (10) ★★★
    +-- Cascading failure
    +-- Partial failure
    +-- Duplicate messages
    +-- Out-of-order events
    +-- Poison messages
    +-- Hot service
    +-- Dependency latency
    +-- Deployment compatibility
    +-- Observability
    +-- Incident methodology
```

------------------------------------------------------------------------

# Big Picture

``` text
                         INTERNET
                            |
                            v
                     Load Balancer
                            |
                            v
                       API Gateway
                            |
          +-----------------+------------------+
          |                 |                  |
          v                 v                  v
      Order Service    Customer Service   Catalog Service
          |                 |                  |
          v                 v                  v
       Order DB          Customer DB        Catalog DB
          |
          +------ REST/gRPC ------> Payment Service
          |
          +------ Kafka ----------> Notification Service
          |
          +------ Redis ----------> Cache / Rate Limit
```

Core principle:

``` text
MICROSERVICES
=
independently deployable services
+
clear ownership/boundaries
+
distributed-system complexity
```

------------------------------------------------------------------------

# 1. Architecture / Boundaries --- 10 Questions

## Q1. Monolith vs microservices?

Monolith:

``` text
Application
|
+-- Orders
+-- Payments
+-- Customers
+-- Inventory
|
+-- one deployment unit
```

Microservices:

``` text
Order Service      Payment Service
     |                    |
 Order DB              Payment DB

Inventory Service   Customer Service
     |                    |
Inventory DB          Customer DB
```

Microservices can improve independent deployment, ownership and scaling,
but add network failures, observability, deployment, data-consistency
and operational complexity.

## Q2. What is a modular monolith?

``` text
One deployable application
|
+-- Order Module
+-- Payment Module
+-- Inventory Module
+-- Customer Module
```

Strong internal boundaries, but no network between modules.

It can provide much of the architectural discipline without
distributed-system cost.

**Senior rule:** Start with microservices only when
organizational/scale/deployment needs justify them.

## Q3. How do you choose service boundaries?

Good boundaries align with business capabilities and ownership.

``` text
Bad:
UserService
DatabaseService
ValidationService
UtilityService

Better:
Ordering
Payments
Inventory
Shipping
Identity
```

Questions:

``` text
Who owns the data?
Who owns business rules?
Can this capability deploy independently?
Does it change independently?
Does it need independent scaling?
```

## Q4. What is a bounded context?

From Domain-Driven Design:

``` text
Same word
can have different meaning
in different contexts.
```

Example:

``` text
"Customer"

Ordering:
shipping/customer identity

Billing:
payer/account

Support:
case/contact history
```

A bounded context defines a model's semantic boundary and is often a
useful input to service boundaries.

## Q5. Why database-per-service?

``` text
Order Service
     |
 Order DB

Payment Service
     |
Payment DB
```

Benefits:

-   ownership
-   independent schema evolution
-   independent deployment
-   reduced direct coupling
-   technology choice where justified

A service exposes data through its contract, not by letting every
service query its tables.

## Q6. Why is a shared database problematic?

``` text
Service A ----+
              |
Service B ----+--> Shared DB
              |
Service C ----+
```

Problems:

``` text
schema coupling
cross-service joins
hidden dependencies
coordinated releases
ownership ambiguity
one service can break another
```

A shared DB can be a pragmatic migration stage, but it weakens service
autonomy.

## Q7. What does data ownership mean?

``` text
Payment Service
owns:
payment records
payment state transitions
payment business rules
```

Other services should not directly modify payment tables.

Instead:

``` text
Order Service
   |
Payment API/Event
   |
Payment Service
   |
Payment DB
```

## Q8. What is an API contract between services?

A contract includes:

``` text
request schema
response schema
status/error semantics
timeouts
idempotency
authentication
version compatibility
event schema
```

The contract is more than the URL.

## Q9. Coupling vs cohesion?

High cohesion:

``` text
Payment Service
|
+ charge
+ refund
+ payment status
+ payment rules
```

Bad low cohesion:

``` text
CommonService
|
+ payment
+ email
+ customer
+ PDF
+ pricing
```

Goal:

``` text
high internal cohesion
low external coupling
```

## Q10. When should you NOT use microservices?

Avoid premature microservices when:

``` text
small team
simple domain
low scale
unclear boundaries
weak DevOps/observability
rapid early product changes
```

A modular monolith can be better.

------------------------------------------------------------------------

# 2. Communication / Infrastructure --- 10 Questions

## Q11. Synchronous vs asynchronous communication?

Synchronous:

``` text
Service A
   |
 request
   v
Service B
   |
 response
   v
Service A
```

Advantages: simple request/response reasoning.

Costs: temporal coupling, latency propagation, cascading failures.

Asynchronous:

``` text
Service A
   |
 publish
   v
Kafka
   |
   v
Service B
```

Advantages: decoupling, buffering, independent processing.

Costs: eventual consistency, duplicates, ordering and debugging
complexity.

## Q12. REST vs gRPC?

REST:

``` text
HTTP + JSON commonly
human-friendly
broad interoperability
public APIs
```

gRPC:

``` text
HTTP/2
Protocol Buffers
strong schema
efficient binary serialization
streaming support
```

Choose based on interoperability, performance, streaming, ecosystem and
contract needs.

## Q13. When should messaging be used?

Useful when:

``` text
producer should not wait
multiple consumers need event
traffic needs buffering
eventual consistency acceptable
workflow naturally asynchronous
```

Example:

``` text
Order Created
     |
     v
Kafka
 +---+----+------+
 |        |      |
Payment Email Analytics
```

Do not use Kafka just to avoid designing service boundaries.

## Q14. What is an API Gateway?

``` text
Client
  |
API Gateway
  |
  +--> Order
  +--> Customer
  +--> Payment
```

Responsibilities can include:

``` text
routing
authentication
rate limiting
TLS termination
request policy
observability
```

Avoid putting core business logic into the gateway.

## Q15. What is service discovery?

Dynamic service instances:

``` text
Payment Service
|
+ pod A 10.0.0.1
+ pod B 10.0.0.2
+ pod C 10.0.0.3
```

Clients need a way to resolve the service.

In Kubernetes:

``` text
Service DNS
   |
ClusterIP / service abstraction
   |
healthy pods
```

Older Spring architectures often used Eureka-style registries;
Kubernetes provides native discovery primitives.

## Q16. Client-side vs server-side load balancing?

Client-side:

``` text
Client
 |
knows instances
 |
chooses instance
```

Server-side:

``` text
Client
 |
Load Balancer / Service
 |
chooses backend
```

Kubernetes commonly provides service-level routing so application code
does not need to manually track pod addresses.

## Q17. How should configuration work?

Externalize environment-specific config:

``` text
code
 |
 +--> config properties
 +--> secrets
 +--> feature flags
```

Avoid:

``` text
hard-coded DB URL
hard-coded password
hard-coded downstream host
```

Use Spring configuration plus environment/Kubernetes/secret-management
mechanisms.

## Q18. Why correlation IDs and distributed tracing?

Request:

``` text
Gateway
 |
Order Service
 |
Payment Service
 |
Fraud Service
```

Without correlation:

``` text
4 services
thousands of logs
hard to connect
```

With trace context:

``` text
Trace ID = abc

Gateway abc
Order   abc
Payment abc
Fraud   abc
```

Use OpenTelemetry-style trace/span propagation where appropriate.

## Q19. What is contract evolution?

Service A deploys today; Service B deploys tomorrow.

Therefore:

``` text
old producer + new consumer
new producer + old consumer
```

may coexist.

Prefer compatible changes:

``` text
add optional field
tolerate unknown fields
avoid changing meaning/type
version breaking schemas
```

## Q20. What is consumer-driven contract testing?

Consumers define expectations against a provider contract.

``` text
Consumer expectations
        |
        v
Contract
        |
        v
Provider verification
```

It helps detect breaking service API changes before production.

It complements, not replaces, integration/end-to-end tests.

------------------------------------------------------------------------

# 3. Resilience / Failure Control --- 12 Questions

## Q21. Why are timeouts mandatory?

Without timeout:

``` text
Service A
 |
call B
 |
B hangs
 |
A thread waits forever
 |
thread pool fills
 |
A fails too
```

With timeout:

``` text
call B
 |
wait bounded time
 |
fail fast / controlled response
```

Timeouts bound resource occupation.

## Q22. What should determine timeout values?

Not arbitrary constants.

Consider:

``` text
caller latency budget
downstream p95/p99
network overhead
number of sequential calls
retry budget
user SLA
```

Example:

``` text
Gateway budget = 2s

Order Service budget = 1.7s
Payment call budget = 700ms
Inventory call budget = 400ms
```

Leave time for handling and response propagation.

## Q23. When should you retry?

Retry when:

``` text
failure likely transient
operation is safe/idempotent
retry budget exists
```

Good candidates:

``` text
temporary network error
503
some connection reset scenarios
```

Bad candidates:

``` text
validation error
401/403
deterministic business rejection
non-idempotent side effect without protection
```

## Q24. What is exponential backoff?

Instead of:

``` text
retry immediately
retry immediately
retry immediately
```

use:

``` text
attempt1 -> wait 100ms
attempt2 -> wait 200ms
attempt3 -> wait 400ms
```

Conceptually:

``` text
delay = base * 2^attempt
```

with caps and practical policy.

## Q25. Why add jitter?

If 10,000 clients fail simultaneously:

``` text
all retry at 100ms
all retry at 200ms
all retry at 400ms
```

This creates synchronized retry waves.

Jitter randomizes delay:

``` text
100ms -> 73ms, 119ms, 91ms...
```

reducing thundering-herd behavior.

## Q26. What is a retry storm?

``` text
Service B overloaded
      |
requests timeout
      |
Service A retries
      |
more traffic to B
      |
B becomes slower
      |
more retries
```

Feedback loop:

``` text
FAILURE
  |
RETRY
  |
MORE LOAD
  |
MORE FAILURE
```

Retries must be bounded and coordinated across layers.

## Q27. What is a circuit breaker?

States:

``` text
CLOSED
  |
failures exceed threshold
  |
  v
OPEN
  |
requests fail fast
  |
after wait
  v
HALF_OPEN
  |
trial calls
 +----+----+
 |         |
success   failure
 |         |
CLOSED    OPEN
```

Purpose:

``` text
stop hammering unhealthy dependency
+
protect caller resources
```

It does not repair the dependency.

## Q28. What is a bulkhead?

Ship analogy: one flooded compartment should not sink the whole ship.

Backend:

``` text
Service
|
+-- Payment dependency pool
+-- Search dependency pool
+-- Email dependency pool
```

If Email hangs:

``` text
Email concurrency exhausted
        |
        X should not consume
          all service threads
```

Implement with bounded concurrency/thread pools/semaphores/resource
isolation depending on architecture.

## Q29. Rate limiting vs bulkhead?

``` text
RATE LIMIT
controls request arrival rate

BULKHEAD
controls concurrent resource usage
```

Both protect capacity but at different points.

## Q30. What is backpressure?

Backpressure tells upstream:

``` text
I cannot safely process more work
at this rate.
```

Without it:

``` text
producer 100k/s
consumer 10k/s
      |
queue grows
      |
memory/disk/lag grows
```

Mechanisms include bounded queues, rejection, consumer lag control, rate
limits and reactive demand protocols.

## Q31. What is fallback?

Fallback provides degraded behavior.

Example:

``` text
Recommendation service down
       |
       v
return popular products
```

Bad fallback:

``` text
Payment service down
       |
       v
pretend payment succeeded
```

Fallback must preserve correctness.

## Q32. How does Resilience4j fit in Spring?

Common resilience patterns:

``` text
Retry
CircuitBreaker
RateLimiter
Bulkhead
TimeLimiter
```

Mental chain:

``` text
Request
 |
Bulkhead
 |
Circuit Breaker
 |
Retry
 |
Time Limit
 |
Remote Service
```

Decorator ordering matters and must match desired semantics.

Do not stack annotations blindly.

------------------------------------------------------------------------

# 4. Distributed Transactions / Consistency --- 13 Questions

## Q33. Why can't @Transactional cover multiple microservices?

``` text
Order Service
@Transactional
 |
Order DB
 |
HTTP -> Payment Service
         |
       Payment DB
```

Spring's local DB transaction cannot roll back an independent payment
database through a normal HTTP call.

``` text
local transaction
      !=
distributed atomic transaction
```

## Q34. What is two-phase commit (2PC)?

Conceptually:

``` text
Coordinator
   |
PHASE 1: PREPARE
   |
 +----+----+
 |         |
DB A      DB B
ready?    ready?
   |
PHASE 2: COMMIT / ROLLBACK
```

Benefits: stronger atomic coordination.

Costs:

``` text
coordination
blocking/failure complexity
latency
availability trade-offs
operational coupling
```

It exists and can be appropriate in some environments, but many
microservice architectures prefer local transactions plus Saga/outbox.

## Q35. What is Saga?

A Saga is a sequence of local transactions forming a distributed
business workflow.

``` text
Create Order
    |
Reserve Inventory
    |
Charge Payment
    |
Arrange Shipping
```

If a later step fails:

``` text
execute compensating actions
```

Saga provides business-level recovery, not one global ACID rollback.

## Q36. Choreography Saga?

Services react to events.

``` text
Order Service
   |
OrderCreated
   v
Kafka
   |
Inventory Service
   |
InventoryReserved
   v
Kafka
   |
Payment Service
```

Advantages:

``` text
loose central coordination
event-driven
```

Problems:

``` text
workflow hard to visualize
event chains
debugging complexity
cyclic dependencies
```

## Q37. Orchestration Saga?

Central orchestrator controls workflow.

``` text
          Order Saga
         Orchestrator
        /     |      \
       v      v       v
Inventory  Payment  Shipping
```

Advantages:

``` text
workflow explicit
central state
easier reasoning for complex process
```

Trade-off:

``` text
orchestrator becomes important component
must remain business-workflow coordinator
not giant god service
```

## Q38. What is compensation?

Compensation semantically reverses a completed business step.

``` text
Charge Payment
     |
later shipping fails
     |
Refund Payment
```

Important:

``` text
compensation != database rollback
```

A refund is a new business transaction with its own failure
possibilities.

## Q39. What is the dual-write problem?

Naive:

``` text
1. UPDATE database
2. publish Kafka event
```

Failure:

``` text
DB commit succeeds
      |
process crashes
      |
event never published
```

Reverse order:

``` text
event published
      |
DB transaction rolls back
```

Now event describes state that does not exist.

## Q40. What is transactional outbox?

Within one local DB transaction:

``` text
BEGIN
 |
+--> update Order
 |
+--> insert OutboxEvent
 |
COMMIT
```

Then:

``` text
Outbox
  |
Publisher / CDC
  |
Kafka
```

Visual:

``` text
           ONE LOCAL TX
      +--------------------+
      |                    |
      v                    v
   Order DB            Outbox row
      |                    |
      +---------+----------+
                |
              COMMIT
                |
                v
           Event Relay
                |
                v
              Kafka
```

This makes business state and intent-to-publish atomic locally.

## Q41. Polling publisher vs CDC?

Polling:

``` text
Publisher
  |
SELECT unpublished outbox rows
  |
publish
  |
mark processed
```

CDC:

``` text
DB transaction log/WAL
      |
CDC connector
      |
Kafka
```

CDC can reduce custom polling and capture committed changes, but adds
infrastructure/operational considerations.

Both still require duplicate-safe downstream handling.

## Q42. Why can outbox still produce duplicate events?

Scenario:

``` text
publisher sends event
      |
Kafka accepts
      |
publisher crashes
before marking row sent
      |
publisher restarts
      |
sends again
```

Therefore:

``` text
Outbox improves atomic publication intent
but does NOT imply exactly-once business effect.
```

Consumers should be idempotent.

## Q43. What is an idempotent consumer?

Message:

``` text
eventId = E123
```

Consumer:

``` text
E123 received
   |
already processed?
 +---+---+
 |       |
YES      NO
 |       |
ignore   apply business change
         + record E123
```

Ideally the business update and deduplication marker are committed
atomically in the consumer's local DB transaction.

## Q44. What is eventual consistency?

``` text
T0:
Order = CREATED
Payment = absent

T1:
Payment = PAID
Order still CREATED

T2:
Payment event processed
Order = PAID
```

For a window:

``` text
different services disagree
```

Eventually they converge if the workflow succeeds.

Senior question:

> Is eventual consistency acceptable?

Answer depends on business invariant and user experience.

## Q45. Should distributed locks be used for distributed transactions?

Usually no.

Distributed lock:

``` text
lock(resource)
  |
critical section
  |
unlock
```

It can coordinate exclusive access but does not atomically commit
multiple independent databases/services.

Risks:

``` text
lease expiry
process pause
network partition
stale lock owner
fencing
availability
```

Use a distributed lock only when exclusive coordination is truly needed,
and design fencing/lease semantics carefully.

------------------------------------------------------------------------

# 5. Production / Senior Scenarios --- 10 Questions

## Q46. What is cascading failure?

``` text
Database slow
    |
Order Service slow
    |
Gateway threads wait
    |
clients retry
    |
traffic increases
    |
more services saturate
```

One dependency failure propagates across the system.

Controls:

``` text
timeouts
bulkheads
circuit breakers
bounded retries
backpressure
load shedding
```

## Q47. How do you handle partial failure?

Example:

``` text
Order created
Payment succeeded
Notification failed
```

Ask:

``` text
Is notification critical to transaction?
```

Usually:

``` text
NO
 |
retry asynchronously
 |
DLQ/alert if persistent
```

Do not roll back core business state for every optional side effect.

## Q48. How do you handle duplicate messages?

Assume at-least-once delivery can duplicate.

Use:

``` text
event ID
business idempotency key
unique DB constraint
processed-message table
state-machine checks
```

Never assume:

``` text
Kafka/message broker delivered once
therefore business effect occurs once
```

## Q49. How do you handle out-of-order events?

Events:

``` text
OrderCreated version1
OrderPaid    version2
OrderShipped version3
```

Consumer receives:

``` text
v1
v3
v2
```

Strategies:

``` text
partition/order by aggregate key
sequence/version numbers
ignore stale versions
buffer/reconcile if necessary
state-machine validation
```

## Q50. What is a poison message?

A message repeatedly fails processing.

``` text
consume
 |
fail
 |
retry
 |
fail
 |
retry forever
```

Result:

``` text
partition/consumer blocked
lag grows
```

Use bounded retry + dead-letter handling/quarantine + alerting + replay
tooling.

## Q51. What is a hot service/dependency?

``` text
100 services
   |
all call User Profile Service
   |
Profile becomes bottleneck
```

Possible solutions:

``` text
cache
replicate read model
event-driven local copy
batch requests
scale service
reduce chatty calls
```

Avoid turning a "microservice" into a remote shared-library call on
every request.

## Q52. How do you debug downstream latency?

``` text
API p99 = 5s
   |
trace
   |
Order = 4.8s
   |
Payment span = 4.5s
   |
Payment:
  DB?
  remote?
  thread pool?
  Hikari?
  GC?
```

Distributed tracing narrows the dependency path, then use
service-specific metrics/logs.

## Q53. How do you deploy services without breaking consumers?

Use compatibility:

``` text
expand
  |
support old + new
  |
migrate consumers
  |
contract
```

Database example:

``` text
1. add nullable new column
2. deploy code supporting both
3. backfill
4. switch reads/writes
5. remove old later
```

Avoid synchronized "all services must deploy at 2 PM" releases.

## Q54. What should microservice observability include?

Per service:

``` text
RED:
Rate
Errors
Duration
```

Resources:

``` text
CPU
memory
GC
threads
Hikari
```

Dependencies:

``` text
DB latency
Redis latency
Kafka lag
HTTP/gRPC latency
timeouts
retries
circuit state
```

Distributed:

``` text
trace ID
span ID
business IDs
structured logs
```

## Q55. Give a production incident methodology.

``` text
1. Confirm customer impact
2. Check recent deployments/config changes
3. Identify affected endpoints/services
4. Follow traces
5. Find first saturated/slow dependency
6. Check CPU/memory/GC/thread/Hikari
7. Check DB/Redis/Kafka/downstreams
8. Check timeout/retry/circuit behavior
9. Mitigate safely
10. Validate recovery
11. Root-cause analysis
12. Add prevention/observability
```

Do not restart random pods without understanding the failure mode unless
emergency mitigation requires it.

------------------------------------------------------------------------

# Senior Scenario 1 --- Timeout + Retry Amplification

``` text
Client
  |
Gateway retry x2
  |
Order retry x3
  |
Payment retry x3
```

Potential multiplication:

``` text
1 logical request
      |
many physical attempts
```

If Payment is already overloaded, retries worsen it.

Better:

``` text
coordinated retry owner
bounded attempts
exponential backoff
jitter
circuit breaker
```

------------------------------------------------------------------------

# Senior Scenario 2 --- Order + Payment Saga

Happy path:

``` text
Create Order(PENDING)
      |
      v
Reserve Inventory
      |
      v
Charge Payment
      |
      v
Confirm Order
```

Failure:

``` text
Create Order
      |
Reserve Inventory
      |
Payment FAILED
      |
      v
Release Inventory
      |
      v
Mark Order FAILED
```

Compensation:

``` text
ReserveInventory
    <->
ReleaseInventory
```

------------------------------------------------------------------------

# Senior Scenario 3 --- Payment Succeeds, Response Lost

``` text
Order Service
   |
POST Payment
   |
Payment charged
   |
network response lost
   |
Order times out
   |
retry?
```

Without idempotency:

``` text
double charge risk
```

With:

``` text
paymentRequestId = stable
```

Payment Service:

``` text
same request ID
     |
same logical payment
```

------------------------------------------------------------------------

# Senior Scenario 4 --- Outbox + Kafka

``` text
@Transactional
createOrder() {
   INSERT order;
   INSERT outbox;
}
```

Publisher:

``` text
Outbox -> Kafka
```

Consumer:

``` text
Kafka
 |
eventId
 |
dedup
 |
local transaction
 |
business state + processed event
```

End-to-end reliability:

``` text
Local ACID
   +
Outbox
   +
At-least-once delivery
   +
Idempotent consumer
```

------------------------------------------------------------------------

# Senior Scenario 5 --- Circuit Breaker Misconfiguration

Bad:

``` text
failure threshold too low
minimum calls tiny
```

Small transient blip:

``` text
circuit opens
   |
healthy traffic rejected
```

Other bad case:

``` text
circuit opens
but fallback calls same broken dependency
```

Resilience patterns require measurement and correct composition.

------------------------------------------------------------------------

# Senior Scenario 6 --- Shared DB Coupling

``` text
Order Service
directly updates payment.payment_status
```

Payment Service changes schema:

``` text
payment_status -> payment_state
```

Order breaks.

More importantly, Order bypassed Payment business invariants.

Correct:

``` text
Order
 |
Payment API/Event
 |
Payment Service owns state
```

------------------------------------------------------------------------

# Senior Scenario 7 --- Eventual Consistency UX

User pays.

Immediately UI reads Order Service:

``` text
Payment = SUCCESS
Order = PENDING
```

Instead of pretending strong consistency:

``` text
Order status:
PAYMENT_PROCESSING
```

Then event updates:

``` text
PAID
```

Design product states that honestly represent distributed workflow.

------------------------------------------------------------------------

# Senior Scenario 8 --- Poison Event

``` text
Kafka partition
 |
E1 success
E2 poison
E3 waiting
E4 waiting
```

Infinite retry on E2 blocks progress.

Use:

``` text
retry 3
 |
DLT/quarantine E2
 |
continue according to ordering/business policy
 |
alert
```

Ordering requirements determine whether skipping is acceptable.

------------------------------------------------------------------------

# Mini Coding Drill 1 --- Resilience4j Concept

``` java
@CircuitBreaker(
    name = "payment",
    fallbackMethod = "paymentFallback"
)
@Retry(name = "payment")
public PaymentResponse charge(...) {
    return paymentClient.charge(...);
}
```

Senior follow-up:

-   Which decorator executes first?
-   Should this operation be retried?
-   Is it idempotent?
-   What timeout applies?
-   What does fallback mean for payment correctness?

Never answer only with annotations.

------------------------------------------------------------------------

# Mini Coding Drill 2 --- Outbox

``` java
@Transactional
public Order create(CreateOrder command) {

    Order order = orderRepository.save(
        Order.pending(command)
    );

    outboxRepository.save(
        OutboxEvent.of(
            UUID.randomUUID(),
            "OrderCreated",
            order.getId()
        )
    );

    return order;
}
```

Business row and outbox row commit together.

------------------------------------------------------------------------

# Mini Coding Drill 3 --- Idempotent Consumer

Conceptual:

``` java
@Transactional
public void consume(OrderCreated event) {

    if (processedEventRepository.exists(event.id())) {
        return;
    }

    projectionRepository.apply(event);

    processedEventRepository.save(
        new ProcessedEvent(event.id())
    );
}
```

Use a unique constraint on event ID to protect concurrent duplicate
processing.

------------------------------------------------------------------------

# Mini Coding Drill 4 --- Atomic State Transition

Instead of:

``` text
read status
if PENDING
write PAID
```

use conditional state transition where suitable:

``` sql
UPDATE orders
SET status = 'PAID'
WHERE id = ?
  AND status = 'PAYMENT_PENDING';
```

Affected row count tells whether transition occurred.

------------------------------------------------------------------------

# Mini Coding Drill 5 --- Timeout Budget

``` text
Gateway timeout: 2000ms

Order Service internal budget:
1700ms

Payment:
600ms

Inventory:
400ms

DB:
300ms

remaining:
handling + network + serialization
```

Real budgets depend on call graph and whether dependencies execute
sequentially or in parallel.

------------------------------------------------------------------------

# Communication Decision Tree

``` text
Need immediate answer?
      |
 +----+----+
 |         |
YES       NO
 |         |
REST/gRPC Messaging
 |
Need public interoperability?
 +----+----+
 |         |
YES       NO
 |         |
REST      gRPC may fit
```

Then always ask:

``` text
What happens when dependency is slow/down?
```

------------------------------------------------------------------------

# Resilience Stack

``` text
Incoming Request
      |
      v
Rate Limit
      |
      v
Bulkhead
      |
      v
Circuit Breaker
      |
      v
Retry Policy
      |
      v
Timeout / Deadline
      |
      v
Downstream
```

This is conceptual. Exact ordering depends on desired semantics and
library configuration.

------------------------------------------------------------------------

# Distributed Transaction Decision Tree

``` text
One database/resource?
      |
     YES
      |
local ACID transaction

Multiple independent services?
      |
     YES
      |
Need distributed workflow
      |
 +----+----------------+
 |                     |
strong coordinator     business workflow
required/appropriate   eventual consistency
 |                     |
2PC/XA where fit       Saga + Outbox
```

------------------------------------------------------------------------

# Saga State Machine

``` text
ORDER_PENDING
      |
      v
INVENTORY_RESERVED
      |
      v
PAYMENT_PROCESSING
   +--+--+
   |     |
 success failure
   |     |
   v     v
CONFIRMED COMPENSATING
            |
            v
     INVENTORY_RELEASED
            |
            v
          FAILED
```

Persist workflow state. Do not rely only on ephemeral in-memory
orchestration state.

------------------------------------------------------------------------

# Outbox Reliability Chain

``` text
Business TX
   |
   +--> Business Row
   +--> Outbox Row
   |
 COMMIT
   |
Publisher
   |
Kafka
   |
Consumer
   |
Dedup / Idempotency
   |
Local TX
```

------------------------------------------------------------------------

# Failure Matrix

``` text
Failure                          Protection
------------------------------------------------
Slow dependency                  Timeout
Transient failure                Bounded retry
Retry synchronization            Jitter
Repeated dependency failure      Circuit breaker
Resource starvation              Bulkhead
Traffic overload                 Rate limit/load shed
Producer > consumer              Backpressure
DB + event dual write            Outbox
Duplicate event                  Idempotent consumer
Multi-service workflow failure   Saga/compensation
```

------------------------------------------------------------------------

# Microservice Data Rules

``` text
Service owns its data
       |
       X
Other services directly write DB
```

Prefer:

``` text
API
Event
Read model
```

For reporting/analytics, use appropriate replicated/warehouse/read-model
patterns rather than cross-service transactional joins.

------------------------------------------------------------------------

# Production Debugging --- Cascading Failure

``` text
Gateway p99 ↑
   |
trace
   |
Order slow
   |
Payment slow
   |
Payment Hikari pending ↑
   |
DB lock wait
```

Then inspect:

``` text
Why lock?
Which transaction?
Why long?
Retries amplifying?
Circuit breaker state?
```

Trace across layers, then drill down.

------------------------------------------------------------------------

# Production Debugging --- Kafka Lag

``` text
Consumer lag ↑
   |
consumer alive?
   |
processing time ↑?
   |
DB/downstream slow?
   |
partition count?
   |
rebalance?
   |
poison message?
```

Kafka details are CH11.

------------------------------------------------------------------------

# Production Debugging --- Duplicate Business Action

``` text
Customer charged twice
      |
same request?
      |
HTTP retry?
      |
same idempotency key?
      |
message duplicate?
      |
consumer dedup?
      |
unique business constraint?
```

------------------------------------------------------------------------

# Production Debugging --- Version Mismatch

``` text
Service A deploy
    |
new field required
    |
Service B old version
    |
request fails
```

Fix architectural process:

``` text
backward-compatible expansion
      |
deploy consumers/providers safely
      |
later remove old behavior
```

------------------------------------------------------------------------

# Critical Comparison Sheet

``` text
MONOLITH                 MICROSERVICES
one deployment           independent deployments
local calls              network calls
simple transactions      distributed consistency
simpler operations       higher operational complexity
```

``` text
REST                     gRPC                    MESSAGING
request/response         typed RPC               async/event
broad interoperability   efficient internal RPC  temporal decoupling
```

``` text
RETRY                    CIRCUIT BREAKER
try transient failure    stop calling bad dependency
```

``` text
RATE LIMIT               BULKHEAD
control rate             control concurrency
```

``` text
CHOREOGRAPHY             ORCHESTRATION
events drive workflow    coordinator drives workflow
```

``` text
2PC                      SAGA
atomic coordinator       local TX + compensation
strong coupling          eventual business consistency
```

``` text
OUTBOX                   DISTRIBUTED LOCK
reliable DB->event intent exclusive coordination
doesn't prevent dupes     doesn't create multi-DB atomicity
```

------------------------------------------------------------------------

# Senior Rapid-Fire Follow-Ups

1.  Monolith vs microservices?
2.  What is modular monolith?
3.  How choose service boundary?
4.  Bounded context?
5.  Why database per service?
6.  Why shared DB hurts?
7.  What is data ownership?
8.  High cohesion/low coupling?
9.  When not microservices?
10. Sync vs async?
11. REST vs gRPC?
12. When messaging?
13. API Gateway?
14. Service discovery?
15. Client vs server load balancing?
16. Config management?
17. Correlation ID vs trace?
18. Contract evolution?
19. Consumer-driven contracts?
20. Why timeout?
21. How choose timeout?
22. When retry?
23. Exponential backoff?
24. Jitter?
25. Retry storm?
26. Circuit breaker states?
27. Bulkhead?
28. Rate limit vs bulkhead?
29. Backpressure?
30. Safe fallback?
31. Resilience4j patterns?
32. Why @Transactional not distributed?
33. 2PC?
34. Saga?
35. Choreography?
36. Orchestration?
37. Compensation?
38. Dual-write problem?
39. Outbox?
40. Polling vs CDC?
41. Why outbox duplicates?
42. Idempotent consumer?
43. Eventual consistency?
44. Distributed lock limitations?
45. Cascading failure?
46. Partial failure?
47. Duplicate message?
48. Out-of-order event?
49. Poison message?
50. Hot service?
51. Dependency latency debugging?
52. Deployment compatibility?
53. Microservice observability?
54. Retry amplification?
55. Walk through a production incident.

------------------------------------------------------------------------

# Interview Checklist

``` text
ARCHITECTURE
[ ] 01 Monolith vs microservices
[ ] 02 Modular monolith
[ ] 03 Service boundaries
[ ] 04 Bounded context
[ ] 05 Database per service
[ ] 06 Shared DB
[ ] 07 Data ownership
[ ] 08 API contracts
[ ] 09 Coupling/cohesion
[ ] 10 When not microservices

COMMUNICATION
[ ] 11 Sync vs async
[ ] 12 REST vs gRPC
[ ] 13 Messaging
[ ] 14 API Gateway
[ ] 15 Service discovery
[ ] 16 Load balancing
[ ] 17 Configuration
[ ] 18 Tracing
[ ] 19 Contract evolution
[ ] 20 Contract testing

RESILIENCE
[ ] 21 Timeout
[ ] 22 Timeout selection
[ ] 23 Retry
[ ] 24 Backoff
[ ] 25 Jitter
[ ] 26 Retry storm
[ ] 27 Circuit breaker
[ ] 28 Bulkhead
[ ] 29 Rate limit
[ ] 30 Backpressure
[ ] 31 Fallback
[ ] 32 Resilience4j

DISTRIBUTED CONSISTENCY
[ ] 33 Local TX limitation
[ ] 34 2PC
[ ] 35 Saga
[ ] 36 Choreography
[ ] 37 Orchestration
[ ] 38 Compensation
[ ] 39 Dual write
[ ] 40 Outbox
[ ] 41 Polling / CDC
[ ] 42 Outbox duplicates
[ ] 43 Idempotent consumer
[ ] 44 Eventual consistency
[ ] 45 Distributed locks

PRODUCTION
[ ] 46 Cascading failure
[ ] 47 Partial failure
[ ] 48 Duplicate messages
[ ] 49 Out-of-order
[ ] 50 Poison messages
[ ] 51 Hot service
[ ] 52 Dependency latency
[ ] 53 Deployment compatibility
[ ] 54 Observability
[ ] 55 Incident methodology
```

------------------------------------------------------------------------

# Chapter 10 Visual Summary

``` text
                         MICROSERVICES
                              |
          +-------------------+-------------------+
          |                   |                   |
      BOUNDARIES          COMMUNICATION        FAILURES
          |                   |                   |
    Data Ownership       REST / gRPC          Timeout
          |                   |               Retry
    DB per Service          Kafka             Circuit
          |                   |               Bulkhead
          +-------------------+-------------------+
                              |
                              v
                   DISTRIBUTED CONSISTENCY
                              |
                    +---------+---------+
                    |                   |
                   Saga               Outbox
                    |                   |
               Compensation        Kafka/Event
                    |                   |
                    +---------+---------+
                              |
                              v
                     Idempotent Consumer
```

Production chain:

``` text
Client
  |
Gateway
  |
Order Service
  |
  +--> Payment Service
  |       |
  |      DB
  |
  +--> Kafka
          |
       Consumer
```

At every arrow ask:

``` text
What if it is slow?
What if it fails?
What if response is lost?
What if request repeats?
What if message duplicates?
What if events arrive out of order?
```

That is the senior distributed-systems mindset.

------------------------------------------------------------------------

# Chapter 10 Exit Criteria

You are ready for **CH 11 --- Kafka / Messaging** when you can:

1.  Explain when microservices are justified.
2.  Define service boundaries and data ownership.
3.  Explain why shared DB weakens autonomy.
4.  Choose REST vs gRPC vs messaging.
5.  Explain discovery/gateway/load balancing.
6.  Design timeout budgets.
7.  Explain bounded retries, backoff and jitter.
8.  Draw circuit-breaker states.
9.  Explain bulkhead vs rate limit.
10. Explain backpressure.
11. Explain why `@Transactional` cannot span normal service calls.
12. Compare 2PC and Saga.
13. Compare choreography and orchestration.
14. Design compensation.
15. Explain the dual-write problem.
16. Draw transactional outbox.
17. Explain why outbox can duplicate.
18. Design an idempotent consumer.
19. Explain eventual consistency to a product team.
20. Diagnose cascading failures across services.
21. Handle duplicate and out-of-order events.
22. Handle poison messages.
23. Deploy contracts backward-compatibly.
24. Define microservice observability.
25. Walk through a distributed production incident.

``` text
CH 10 MICROSERVICES
        |
Service Boundaries
        |
Data Ownership
        |
REST / gRPC / Messaging
        |
Timeout / Retry ★★★
        |
Circuit / Bulkhead ★★★
        |
Saga / Compensation ★★★
        |
Outbox ★★★
        |
Idempotency ★★★
        |
Eventual Consistency
        |
Production Failures
        |
READY FOR CH 11
KAFKA / MESSAGING
```

------------------------------------------------------------------------

## Next Chapter

``` text
CH 11 — KAFKA / MESSAGING (~50 Q)
|
+-- Broker / cluster / topic
+-- Partitions
+-- Producer
+-- Consumer groups
+-- Offsets
+-- Ordering
+-- Rebalancing
+-- At-most / at-least / exactly-once
+-- Idempotent producer
+-- Kafka transactions
+-- Idempotent consumer
+-- Retry / DLT
+-- Consumer lag
+-- Partition strategy
+-- Outbox + Kafka
+-- Production troubleshooting
```
