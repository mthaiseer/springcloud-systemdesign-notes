# 023_Resilience4j_Internal_Model.md

# MiniCircuitBreaker — 023 Resilience4j Internal Model

---

# 1. Why This File Exists

Previous files explained:

```text
CircuitBreaker states
sliding windows
HALF_OPEN
CAS
thread safety
Kafka resiliency
DB resiliency
```

Now we go deeper into:

```text
how Resilience4j internally works
```

Most developers use:

```java
@CircuitBreaker
```

without understanding:

```text
what happens internally
```

But in production interviews and debugging, you must understand:

```text
how requests flow
how metrics calculated
how states transition
how sliding windows work
how Spring AOP intercepts methods
how events published
how decorators work
how thread safety maintained
```

This file explains:

```text
Resilience4j architecture
CircuitBreakerRegistry
CircuitBreakerStateMachine
AtomicReference internals
SlidingWindowMetrics
COUNT_BASED window
TIME_BASED window
ring buffer internals
bucket aggregation
metrics snapshots
event publisher
Spring AOP flow
decorator execution
HALF_OPEN permits
CallNotPermittedException
Micrometer integration
thread safety model
performance tradeoffs
```

---

# 2. One-Line Definition

```text
Resilience4j is a lightweight fault-tolerance library that wraps function execution with a thread-safe state machine and metrics engine.
```

---

# 3. Biggest Mental Model

```text
Your Method
     ↓
Decorator / AOP Proxy
     ↓
CircuitBreaker State Machine
     ↓
Sliding Window Metrics
     ↓
Threshold Evaluation
     ↓
State Transition
```

---

# 4. High-Level Internal Architecture

```text
Application Method
        ↓
Spring AOP / Decorator
        ↓
CircuitBreaker
        ↓
State Machine
        ↓
Sliding Window Metrics
        ↓
Failure/Slow Rate Calculation
        ↓
State Transition Logic
        ↓
Event Publisher
```

---

# 5. Core Internal Components

Main internal components:

```text
CircuitBreakerRegistry
CircuitBreakerConfig
CircuitBreakerStateMachine
SlidingWindowMetrics
CircuitBreakerMetrics
State Objects
EventPublisher
Decorators
Spring AOP Aspect
Micrometer Metrics Exporter
```

---

# 6. CircuitBreakerRegistry

Registry stores all circuit breakers.

Mental model:

```text
Map<String, CircuitBreaker>
```

---

# 7. Registry ASCII

```text
CircuitBreakerRegistry
    ├── paymentService
    ├── inventoryService
    ├── bankApi
    ├── shippingService
    └── emailProvider
```

---

# 8. Registry Example

```java
CircuitBreakerRegistry registry =
        CircuitBreakerRegistry.ofDefaults();

CircuitBreaker paymentBreaker =
        registry.circuitBreaker(
                "paymentService"
        );
```

---

# 9. Why Registry Important

Registry provides:

```text
centralized breaker management
shared configs
metrics lookup
event subscriptions
dynamic breaker retrieval
```

---

# 10. CircuitBreakerConfig

Every breaker has config.

Example:

```text
failure threshold
slow call threshold
window size
wait duration
HALF_OPEN permits
minimum calls
```

---

# 11. Config Example

```java
CircuitBreakerConfig config =
        CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .slidingWindowSize(20)
                .minimumNumberOfCalls(10)
                .waitDurationInOpenState(
                        Duration.ofSeconds(30)
                )
                .build();
```

---

# 12. Config Mental Model

```text
Rules controlling transition behavior
```

---

# 13. CircuitBreakerStateMachine

This is the heart of Resilience4j.

Internally circuit breaker is:

```text
thread-safe finite state machine
```

---

# 14. Internal States

Resilience4j supports:

```text
CLOSED
OPEN
HALF_OPEN
DISABLED
FORCED_OPEN
METRICS_ONLY
```

---

# 15. Extra State Meaning

## DISABLED

```text
always allow requests
no metrics collected
```

## FORCED_OPEN

```text
always reject requests
```

## METRICS_ONLY

```text
collect metrics
never OPEN
```

Useful for testing.

---

# 16. State Machine ASCII

```text
CLOSED
   ↓ failures
OPEN
   ↓ wait duration elapsed
HALF_OPEN
   ↓ success
CLOSED

HALF_OPEN
   ↓ failure
OPEN
```

---

# 17. State Stored Using AtomicReference

Internally:

```java
AtomicReference<State>
```

used because:

```text
many threads access breaker concurrently
```

---

# 18. Why AtomicReference Important

Need:

```text
thread-safe visibility
single-winner transitions
low contention
CAS-based updates
```

---

# 19. CAS Transition

Example:

```java
state.compareAndSet(
        CLOSED,
        OPEN
);
```

Only one thread wins.

---

# 20. Transition Race Problem

Without CAS:

```text
many threads may OPEN simultaneously
duplicate events published
timestamps overwritten
metrics reset incorrectly
```

CAS solves this.

---

# 21. Request Execution Flow

When protected method called:

```text
check current state
↓
if OPEN reject immediately
↓
if HALF_OPEN acquire permit
↓
execute protected function
↓
record metrics
↓
update sliding window
↓
calculate rates
↓
evaluate transition rules
```

---

# 22. Full Flow ASCII

```text
Method Call
    ↓
CircuitBreaker
    ↓
Current State?
    ↓
Allow or Reject
    ↓
Execute Function
    ↓
Record Result
    ↓
Update Metrics
    ↓
Evaluate Thresholds
    ↓
Transition State
```

---

# 23. Sliding Window Metrics

Metrics stored in sliding window.

Two types:

```text
COUNT_BASED
TIME_BASED
```

---

# 24. COUNT_BASED Window

Tracks:

```text
last N calls
```

Example:

```text
last 20 requests
```

---

# 25. COUNT_BASED Dry Run

Window size:

```text
5
```

Requests:

```text
S F S S F
```

Metrics:

```text
success = 3
failure = 2
failure rate = 40%
```

New request enters:

```text
F
```

Oldest removed:

```text
F S S F F
```

Updated metrics:

```text
success = 2
failure = 3
failure rate = 60%
```

---

# 26. TIME_BASED Window

Tracks metrics over:

```text
last N seconds
```

Example:

```text
last 60 seconds
```

---

# 27. TIME_BASED Mental Model

Uses:

```text
ring buffer buckets
```

Each bucket stores aggregated metrics for time slice.

---

# 28. Ring Buffer ASCII

```text
[0][1][2][3][4][5]
```

Each bucket:

```text
1 second metrics
```

---

# 29. Bucket Contents

Each bucket stores:

```text
successful calls
failed calls
slow calls
total duration
total calls
```

---

# 30. Why Bucket Aggregation Efficient

Instead of storing every request:

```text
store aggregated counters
```

Benefits:

```text
low memory
low GC
fast calculation
```

---

# 31. Ring Buffer Rotation

Every second:

```text
current bucket advances
```

Old bucket overwritten.

---

# 32. Rotation ASCII

```text
t0 → bucket0
t1 → bucket1
t2 → bucket2
```

After full cycle:

```text
reuse old bucket
```

---

# 33. Failure Rate Formula

```text
failureRate =
failedCalls / totalCalls * 100
```

---

# 34. Slow Call Rate Formula

```text
slowCallRate =
slowCalls / totalCalls * 100
```

---

# 35. Minimum Number Of Calls

Breaker should not evaluate too early.

Example:

```yaml
minimumNumberOfCalls: 10
```

Until 10 calls:

```text
do not OPEN
```

---

# 36. Why Minimum Calls Needed

Without minimum calls:

```text
1 failure out of 1 call
= 100%
```

Breaker opens too aggressively.

---

# 37. HALF_OPEN Internal Logic

HALF_OPEN tracks:

```text
remaining permits
successful probes
failed probes
```

---

# 38. HALF_OPEN Permit Control

Internally uses:

```text
AtomicInteger
CAS
Semaphore-like logic
```

to prevent:

```text
recovery storms
```

---

# 39. HALF_OPEN Flow

```text
OPEN wait duration expires
      ↓
OPEN → HALF_OPEN
      ↓
allow limited probes
      ↓
all probes success?
      ↓ yes
HALF_OPEN → CLOSED

any failure?
      ↓ yes
HALF_OPEN → OPEN
```

---

# 40. OPEN State Internals

OPEN stores:

```text
openedAt timestamp
wait duration
transition metadata
```

---

# 41. OPEN Expiry Logic

```text
currentTime - openedAt
```

If exceeded:

```text
OPEN → HALF_OPEN
```

---

# 42. Metrics Snapshot

Metrics exposed as immutable snapshot.

Contains:

```text
failure rate
slow call rate
buffered calls
successful calls
failed calls
not permitted calls
```

---

# 43. Metrics Example

```java
CircuitBreaker.Metrics metrics =
        breaker.getMetrics();

float failureRate =
        metrics.getFailureRate();
```

---

# 44. Decorator Pattern

Resilience4j heavily uses:

```text
decorator pattern
```

---

# 45. Decorator Mental Model

```text
Original Function
       ↓
Wrapped With Resiliency Behavior
```

---

# 46. Decorator ASCII

```text
Supplier
   ↓
CircuitBreaker Decorator
   ↓
Retry Decorator
   ↓
Bulkhead Decorator
   ↓
Execution
```

---

# 47. Decorate Supplier Example

```java
Supplier<String> supplier =
        () -> remoteCall();

Supplier<String> decorated =
        CircuitBreaker.decorateSupplier(
                breaker,
                supplier
        );
```

---

# 48. Decorated Flow

```text
Caller executes decorated supplier
      ↓
CircuitBreaker intercepts
      ↓
checks state
      ↓
executes function or rejects
```

---

# 49. Why Decorators Powerful

Works with:

```text
Supplier
Callable
Runnable
CompletionStage
Mono
Flux
```

---

# 50. Spring Annotation Internals

```java
@CircuitBreaker
```

works internally using:

```text
Spring AOP proxy
```

---

# 51. Annotation Flow

```text
Caller
  ↓
Spring Proxy
  ↓
Resilience4j Aspect
  ↓
CircuitBreaker Logic
  ↓
Target Method
```

---

# 52. AOP Mental Model

Proxy intercepts method call before actual execution.

---

# 53. CircuitBreakerAspect

Spring Boot integration contains:

```text
CircuitBreakerAspect
```

This aspect:

```text
intercepts annotated methods
creates decorators
executes fallback
publishes events
```

---

# 54. Fallback Resolution

Fallback chosen using:

```text
method signature
parameter matching
exception matching
```

---

# 55. Fallback Example

```java
@CircuitBreaker(
        name = "payment",
        fallbackMethod = "fallback"
)
public String pay(String id) {

    return callApi(id);
}

public String fallback(
        String id,
        Exception ex) {

    return "PAYMENT_PENDING";
}
```

---

# 56. Event Publisher

Resilience4j publishes events:

```text
success
error
slow call
state transition
call rejected
reset
ignored error
```

---

# 57. Event Flow ASCII

```text
Protected Call
      ↓
Metrics Updated
      ↓
Event Published
      ↓
Listeners Receive Event
```

---

# 58. Event Subscription Example

```java
breaker.getEventPublisher()
       .onStateTransition(
            event -> System.out.println(event)
       );
```

---

# 59. Why Events Important

Used for:

```text
alerts
monitoring
dashboards
audit
debugging
incident investigation
```

---

# 60. CallNotPermittedException

When breaker OPEN:

```text
request rejected immediately
```

Throws:

```java
CallNotPermittedException
```

---

# 61. Rejected Flow

```text
OPEN state
    ↓
No dependency call executed
    ↓
CallNotPermittedException
    ↓
fallback or error response
```

---

# 62. Thread Safety Internals

Resilience4j optimized for:

```text
high concurrency
```

Uses:

```text
AtomicReference
AtomicInteger
LongAdder
CAS
ring buffers
```

---

# 63. Why Lock-Free Important

Circuit breaker executes on:

```text
every protected request
```

Need:

```text
minimal latency
low contention
high throughput
```

---

# 64. LongAdder Usage

Metrics counters often use:

```text
LongAdder
```

instead of synchronized counters.

Better under heavy contention.

---

# 65. TimeLimiter Integration

Often combined with:

```text
TimeLimiter
```

Handles:

```text
async timeout
future timeout
slow response control
```

---

# 66. Retry Integration

Retry may wrap:

```text
inside CB
outside CB
```

Need careful ordering.

Too many retries can amplify outage.

---

# 67. Bulkhead Integration

Bulkhead isolates resources.

Circuit breaker isolates failures.

Together:

```text
resource isolation + failure isolation
```

---

# 68. Full Production Resilience Flow

```text
Request
   ↓
RateLimiter
   ↓
Bulkhead
   ↓
TimeLimiter
   ↓
Retry
   ↓
CircuitBreaker
   ↓
Remote Call
```

---

# 69. Micrometer Integration

Resilience4j exports metrics to:

```text
Micrometer
Prometheus
Grafana
```

---

# 70. Metrics Exposed

Metrics include:

```text
failure rate
slow-call rate
state
buffered calls
successful calls
failed calls
rejected calls
```

---

# 71. Example Metrics

```text
resilience4j_circuitbreaker_calls
resilience4j_circuitbreaker_state
resilience4j_circuitbreaker_failure_rate
```

---

# 72. Dashboard Panels

Useful production dashboards:

```text
OPEN count
HALF_OPEN transitions
fallback rate
slow-call rate
rejected requests
dependency latency p99
```

---

# 73. Memory Efficiency Insight

Resilience4j avoids storing:

```text
every request object
```

Instead stores:

```text
aggregated counters
```

This makes it lightweight.

---

# 74. Hystrix vs Resilience4j

Hystrix:

```text
heavy
thread-isolation focused
maintenance mode
```

Resilience4j:

```text
lightweight
modular
functional
decorator-based
better Spring support
```

---

# 75. Production Scenario

Payment provider becomes slow.

Flow:

```text
slow calls recorded
slow-call rate exceeds threshold
CLOSED → OPEN
requests rejected fast
fallback returns PAYMENT_PENDING
HALF_OPEN probes recovery
provider recovers
HALF_OPEN → CLOSED
```

---

# 76. Common Mistakes

## Mistake 1

```text
One breaker for all dependencies
```

---

## Mistake 2

```text
No timeout configured
```

---

## Mistake 3

```text
Aggressive retry during outage
```

---

## Mistake 4

```text
Fake-success fallback
```

---

## Mistake 5

```text
No observability
```

---

# 77. Most Important Insight

```text
Resilience4j is fundamentally a concurrent state machine around function execution.
```

Everything builds on this.

---

# 78. Distributed Systems Insight

Distributed systems fail mostly at:

```text
communication boundaries
```

Resilience4j standardizes:

```text
how failures are isolated and controlled
```

---

# 79. Interview Explanation

If interviewer asks:

```text
How does Resilience4j work internally?
```

Strong answer:

```text
Internally Resilience4j uses a thread-safe finite state machine backed by
AtomicReference and sliding window metrics. Decorators or Spring AOP
intercept function execution, record metrics, calculate failure and slow
call rates, and trigger state transitions like CLOSED to OPEN.
```

Senior addition:

```text
It uses lock-free concurrency primitives, ring-buffer style aggregated
metrics, event publishers, and decorator composition to provide low
overhead resiliency on every protected request.
```

---

# 80. Final Mental Model

```text
Function Call
      ↓
Decorator / AOP Proxy
      ↓
Thread-Safe State Machine
      ↓
Sliding Window Metrics
      ↓
Threshold Evaluation
      ↓
State Transition
      ↓
Event Publishing
```

---

# 81. What To Remember

```text
Resilience4j uses decorator pattern.

Spring annotations use AOP proxies internally.

CircuitBreaker is a finite state machine.

Transitions use AtomicReference + CAS.

Sliding windows aggregate metrics efficiently.

HALF_OPEN uses permit control.

Metrics integrate with Micrometer.

Events power observability.

Ring buffers reduce memory overhead.

Timeout + Retry + Bulkhead + CB work together.
```

---

# 82. Next File

```text
024_Metrics_Events_Micrometer_Tracing.md
```

Next you learn:

```text
Micrometer integration
Prometheus metrics
Grafana dashboards
OpenTelemetry tracing
distributed tracing
event consumers
SRE monitoring
production observability
```
