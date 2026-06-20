# 001_What_Is_Circuit_Breaker.md

# MiniCircuitBreaker — 001 What Is Circuit Breaker

---

# 1. Why This File Exists

Modern distributed systems constantly make:

```text
HTTP calls
database calls
Kafka calls
microservice calls
external API calls
```

But remote services can become:

```text
slow
unavailable
overloaded
partially failing
```

Without protection:

```text
one failing service
can destroy the entire system
```

Circuit Breaker exists to:

```text
protect systems from cascading failures
```

---

# 2. One-Line Definition

```text
Circuit Breaker prevents repeated calls to failing services.
```

---

# 3. Biggest Mental Model

Circuit breaker behaves like:

```text
electrical safety switch
```

Electrical analogy:

```text
too much current
      ↓
breaker opens
      ↓
protects system
```

Software analogy:

```text
too many failures
      ↓
breaker opens
      ↓
stops remote calls
```

---

# 4. Core Problem

Suppose:

```text
Service A
calls
Service B
```

Now Service B becomes:

```text
slow
```

Without circuit breaker:

```text
threads wait forever
```

Eventually:

```text
thread pool exhausted
```

Then:

```text
entire system slows down
```

---

# 5. Cascading Failure ASCII

```text
Service B Slow
       ↓
Threads Blocked
       ↓
Thread Pool Exhausted
       ↓
Requests Queue Up
       ↓
System Collapse
```

---

# 6. Why Retry Alone Dangerous

Naive retry logic:

```text
failure
   ↓
retry
   ↓
retry
   ↓
retry
```

Can make overloaded service EVEN WORSE.

This creates:

```text
retry storm
```

---

# 7. Retry Storm ASCII

```text
Service Slow
      ↓
Clients Retry
      ↓
More Traffic
      ↓
Service Slower
      ↓
More Retries
      ↓
Collapse
```

---

# 8. Circuit Breaker Solution

Circuit breaker detects:

```text
too many failures
```

Then:

```text
temporarily stops requests
```

This gives failing service:

```text
time to recover
```

---

# 9. Circuit Breaker States

Three core states:

```text
CLOSED
OPEN
HALF_OPEN
```

---

# 10. State Mental Model

## CLOSED

```text
normal traffic allowed
```

---

## OPEN

```text
requests blocked immediately
```

---

## HALF_OPEN

```text
small test traffic allowed
```

---

# 11. State Machine ASCII

```text
           failures threshold reached
CLOSED  ----------------------------> OPEN
   ^                                    |
   |                                    |
   |                                    | wait duration elapsed
   |                                    v
   +----------- HALF_OPEN <-------------+
                 |
                 |
                 +---- success → CLOSED
                 |
                 +---- failure → OPEN
```

---

# 12. CLOSED State

Normal operating mode.

All requests allowed.

Circuit breaker monitors:

```text
failures
timeouts
slow calls
```

---

# 13. OPEN State

When failures exceed threshold:

```text
breaker OPENS
```

Now:

```text
remote calls blocked immediately
```

No expensive waiting.

Fast failure.

---

# 14. HALF_OPEN State

After cooldown period:

```text
allow few test requests
```

If successful:

```text
CLOSED
```

If failures continue:

```text
OPEN again
```

---

# 15. Why Fast Failure Important

Without circuit breaker:

```text
every request waits for timeout
```

Very expensive.

With OPEN state:

```text
fail immediately
```

Much faster.

---

# 16. Fast Failure ASCII

Without CB:

```text
Request
   ↓
30 second timeout
```

With OPEN state:

```text
Request
   ↓
Immediate rejection
```

---

# 17. Timeout Problem

Suppose:

```text
100 threads
```

Each blocked for:

```text
30 seconds
```

Very quickly:

```text
thread pool exhausted
```

---

# 18. Thread Exhaustion ASCII

```text
Slow Service
      ↓
Blocked Threads
      ↓
No Free Threads
      ↓
Application Freeze
```

---

# 19. Why Circuit Breakers Critical

Protect:

```text
thread pools
CPU
memory
network
database connections
```

---

# 20. Real Production Example

Example:

```text
Payment Service
calls
Bank API
```

Bank API becomes slow.

Without circuit breaker:

```text
all payment threads blocked
```

Entire checkout system may fail.

---

# 21. With Circuit Breaker

Failures detected.

Breaker opens.

Now:

```text
requests fail fast
fallback response used
```

Checkout system survives.

---

# 22. Fallback Response

Instead of full failure:

```text
cached response
default response
temporary message
```

can be returned.

---

# 23. Fallback ASCII

```text
Service Failure
      ↓
Circuit Open
      ↓
Fallback Response
```

---

# 24. Bulkhead Relationship

Circuit breaker often combined with:

```text
Bulkhead isolation
```

Bulkhead protects:

```text
thread pools/resources
```

Circuit breaker protects:

```text
failing dependencies
```

---

# 25. RateLimiter Relationship

Rate limiter controls:

```text
traffic volume
```

Circuit breaker controls:

```text
failure protection
```

Different problems.

---

# 26. Retry vs Circuit Breaker

Retry:

```text
assumes service may recover quickly
```

Circuit breaker:

```text
assumes service unhealthy
```

Both usually combined carefully.

---

# 27. Retry + CB Flow

```text
Request
   ↓
Retry Few Times
   ↓
Failures Continue
   ↓
Circuit Opens
```

---

# 28. Real Libraries

Popular circuit breaker libraries:

```text
Resilience4j
Netflix Hystrix
Spring Cloud CircuitBreaker
Sentinel
```

---

# 29. Why Hystrix Famous

Netflix created Hystrix to handle:

```text
microservice failures
```

at massive scale.

Modern replacement:

```text
Resilience4j
```

---

# 30. Spring Boot Example

Typical usage:

```java
@CircuitBreaker(name = "payment-service")
public PaymentResponse pay() {

    return paymentClient.call();
}
```

But internally MUCH more happens.

---

# 31. Internal Components

Circuit breaker internally maintains:

```text
state machine
failure counters
sliding window
timers
metrics
atomic state transitions
```

---

# 32. Sliding Window

Circuit breaker usually tracks:

```text
recent requests
```

NOT all-time history.

Example:

```text
last 100 calls
```

---

# 33. Failure Threshold

Suppose threshold:

```text
50% failures
```

If:

```text
60 failures out of 100
```

Circuit opens.

---

# 34. Failure Rate Formula

Failure rate:

```text
failures / total requests
```

Example:

```text
60 / 100 = 60%
```

---

# 35. Slow Call Detection

Modern breakers also detect:

```text
slow responses
```

because:

```text
slow service can kill system
even without hard failures
```

---

# 36. Slow Call Example

Timeout threshold:

```text
2 seconds
```

Service responding in:

```text
10 seconds
```

Counts as dangerous.

---

# 37. Why HALF_OPEN Important

Without HALF_OPEN:

```text
once OPEN
always OPEN
```

Need recovery testing.

HALF_OPEN safely probes service.

---

# 38. HALF_OPEN ASCII

```text
OPEN
  ↓
wait duration
  ↓
HALF_OPEN
  ↓
test few requests
```

---

# 39. Concurrency Challenges

Multiple threads may:

```text
update state simultaneously
```

Need:

```text
thread safety
```

---

# 40. Modern Implementation

Modern circuit breakers use:

```text
AtomicReference
CAS operations
lock-free state transitions
```

for high performance.

---

# 41. Distributed System Importance

In microservices:

```text
everything depends on remote calls
```

Without resilience:

```text
entire architecture fragile
```

---

# 42. API Gateway Use Case

Gateway calling:

```text
auth service
payment service
inventory service
```

If one service fails:

```text
gateway must survive
```

Circuit breaker critical.

---

# 43. Kafka Consumer Use Case

Consumer calling external service.

External service slows down.

Without breaker:

```text
Kafka lag grows infinitely
```

---

# 44. Database Use Case

Database becomes overloaded.

Circuit breaker can:

```text
shed traffic temporarily
```

to protect DB.

---

# 45. Circuit Breaker vs Load Balancer

Load balancer:

```text
distributes traffic
```

Circuit breaker:

```text
blocks unhealthy dependencies
```

Different roles.

---

# 46. Circuit Breaker vs Timeout

Timeout:

```text
limits waiting duration
```

Circuit breaker:

```text
prevents repeated unhealthy calls
```

Usually combined.

---

# 47. Production Metrics

Important metrics:

```text
failure rate
slow call rate
OPEN duration
rejected requests
fallback count
```

---

# 48. Observability

Production systems integrate:

```text
Micrometer
Prometheus
Grafana
distributed tracing
alerts
```

---

# 49. Java Mental Model

Circuit breaker internally:

```text
Request
   ↓
Check State
   ↓
Allowed?
   ↓
Execute Call
   ↓
Record Success/Failure
   ↓
Update Metrics
   ↓
Possibly Transition State
```

---

# 50. Simple Java Mini Example

```java
public class SimpleCircuitBreaker {

    private boolean open = false;

    public String call() {

        if (open) {
            return "Service unavailable";
        }

        try {

            return remoteCall();

        } catch (Exception ex) {

            open = true;

            return "Fallback response";
        }
    }

    private String remoteCall() {

        throw new RuntimeException();
    }
}
```

---

# 51. Dry Run

Execution:

```text
Request arrives
      ↓
Circuit CLOSED
      ↓
Remote call fails
      ↓
Circuit OPENED
      ↓
Next request rejected immediately
```

---

# 52. Real Production Complexity

Real systems additionally handle:

```text
sliding windows
concurrency
timeouts
metrics
half-open limits
distributed tracing
thread safety
```

Much more advanced than simple examples.

---

# 53. Most Important Mental Model

```text
Circuit Breaker =
System Protection Layer
```

NOT merely:

```text
retry utility
```

---

# 54. Interview Explanation

If interviewer asks:

```text
What is circuit breaker?
```

Strong answer:

```text
Circuit breaker is a resiliency pattern that prevents repeated calls to
unhealthy services. It detects failures, opens the circuit after a
threshold, fails requests fast, and later probes recovery using
HALF_OPEN state.
```

Senior-level addition:

```text
Circuit breakers prevent cascading failures, thread exhaustion, and retry
storms in distributed systems.
```

---

# 55. Final Mental Model

```text
Healthy Service
      ↓
CLOSED

Service Unhealthy
      ↓
OPEN

Recovery Probe
      ↓
HALF_OPEN
```

---

# 56. What To Remember

```text
Circuit breaker protects systems from unhealthy dependencies.

Core states:
CLOSED
OPEN
HALF_OPEN

OPEN state fails fast.

HALF_OPEN tests recovery.

Protects thread pools/resources.

Prevents cascading failures.

Retry alone can worsen failures.

Modern systems use Resilience4j.

Circuit breaker is critical in microservices.
```

---

# 57. Next File

```text
002_Cascading_Failure_Problem.md
```

Next you learn:

```text
how cascading failures spread
thread pool exhaustion
retry storms
resource collapse
backpressure
distributed failure propagation
real production outage patterns
```
