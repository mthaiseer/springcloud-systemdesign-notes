# 002_Cascading_Failure_Problem.md

# MiniCircuitBreaker — 002 Cascading Failure Problem

---

# 1. Why This File Exists

Most distributed system outages happen NOT because:

```text
one service failed
```

but because:

```text
failure spread across the system
```

This spreading failure is called:

```text
Cascading Failure
```

Understanding cascading failure is the MOST important reason for learning:

```text
Circuit Breakers
Bulkheads
Timeouts
Retries
Backpressure
Resiliency Engineering
```

---

# 2. One-Line Definition

```text
Cascading failure happens when one failing component causes other components to fail.
```

---

# 3. Biggest Mental Model

```text
One Slow Service
        ↓
Threads Block
        ↓
Resources Exhausted
        ↓
More Services Fail
        ↓
Entire System Collapse
```

---

# 4. Real Production Scenario

Suppose:

```text
API Gateway
    ↓
Payment Service
    ↓
Bank API
```

Now:

```text
Bank API becomes slow
```

This single slowdown can destroy:

```text
entire checkout system
```

---

# 5. Distributed Call Chain ASCII

```text
User Request
      ↓
API Gateway
      ↓
Payment Service
      ↓
Bank API
```

---

# 6. Slow Service Problem

Suppose:

```text
Bank API normally:
100ms
```

Suddenly:

```text
30 seconds
```

Now all payment requests wait.

---

# 7. Thread Blocking Problem

Every request occupies:

```text
one application thread
```

While waiting:

```text
thread blocked
```

Blocked threads cannot process other requests.

---

# 8. Thread Blocking ASCII

```text
Request Arrives
      ↓
Thread Assigned
      ↓
Waiting For Remote Service
      ↓
Thread Frozen
```

---

# 9. Thread Pool Exhaustion

Suppose application has:

```text
200 threads
```

Each blocked for:

```text
30 seconds
```

Soon:

```text
all 200 threads occupied
```

Now:

```text
new requests cannot execute
```

---

# 10. Thread Exhaustion ASCII

```text
Slow Dependency
       ↓
Blocked Threads
       ↓
No Free Threads
       ↓
Application Stops Responding
```

---

# 11. Queue Growth Problem

Incoming requests continue.

But no free threads.

Requests start:

```text
queueing
```

Queue grows infinitely.

---

# 12. Queue Explosion ASCII

```text
Incoming Requests
       ↓
Request Queue
       ↓
Queue Size Explodes
```

---

# 13. Retry Storm

Many systems automatically retry failures.

Example:

```text
timeout
   ↓
retry
   ↓
retry
   ↓
retry
```

This creates:

```text
retry storm
```

---

# 14. Retry Storm Mental Model

Service already overloaded.

Retries create:

```text
EVEN MORE traffic
```

making situation worse.

---

# 15. Retry Storm ASCII

```text
Service Slow
      ↓
Clients Retry
      ↓
Traffic Increases
      ↓
Service Slower
      ↓
More Retries
      ↓
Collapse
```

---

# 16. Cascading Failure Spread

Now other services calling Payment Service also block.

Example:

```text
Order Service
Notification Service
Inventory Service
```

Now THEY begin failing too.

---

# 17. Cascading Spread ASCII

```text
Bank API Slow
      ↓
Payment Service Slow
      ↓
Gateway Slow
      ↓
Entire Platform Slow
```

---

# 18. Why Distributed Systems Fragile

Microservices depend heavily on:

```text
network calls
```

Every dependency introduces:

```text
failure propagation risk
```

---

# 19. Latency Amplification

Suppose:

```text
Gateway calls 5 services
```

Each service slightly slower.

Combined latency becomes huge.

---

# 20. Latency Amplification ASCII

```text
100ms + 100ms + 100ms + 100ms
               ↓
          Huge Delay
```

---

# 21. Timeout Problem

Without timeout:

```text
thread may wait forever
```

Very dangerous.

---

# 22. Long Timeout Problem

Even timeout:

```text
30 seconds
```

can still destroy thread pools.

Timeouts must be:

```text
carefully tuned
```

---

# 23. CPU Exhaustion

Retries + queue growth increase:

```text
CPU context switching
GC pressure
serialization work
logging overhead
```

Eventually CPU overloads too.

---

# 24. Memory Exhaustion

Queued requests consume:

```text
memory
buffers
objects
request payloads
```

Large queues may cause:

```text
OOM
```

(Out Of Memory)

---

# 25. Database Cascading Failure

Suppose DB slows down.

Application threads wait.

Connection pool fills.

Eventually:

```text
all DB connections exhausted
```

Now entire application blocked.

---

# 26. DB Failure ASCII

```text
Slow Database
      ↓
Connection Pool Full
      ↓
Threads Waiting
      ↓
Application Freeze
```

---

# 27. Kafka Consumer Example

Consumer processing messages.

External API slows down.

Consumers process slower.

Now:

```text
Kafka lag grows infinitely
```

Eventually system unstable.

---

# 28. Kafka Lag ASCII

```text
Slow API
    ↓
Consumers Slow
    ↓
Kafka Lag Increases
```

---

# 29. Gateway Collapse Example

Gateway depends on:

```text
auth service
payment service
inventory service
```

If one service hangs:

```text
gateway threads block
```

Eventually gateway unavailable.

---

# 30. Gateway Collapse ASCII

```text
One Dependency Slow
       ↓
Gateway Threads Exhausted
       ↓
Entire API Platform Down
```

---

# 31. Cascading Failure In Cloud Systems

Cloud systems especially vulnerable because:

```text
massive service interdependencies
```

Thousands of services may depend on each other.

---

# 32. Why Small Failures Become Big

Distributed systems amplify failures because:

```text
everything connected
```

One unhealthy node affects many callers.

---

# 33. Cascading Failure Formula Mental Model

```text
Slow Dependency
      +
Retries
      +
Blocked Threads
      +
Queues
      +
Shared Resources
      =
System Collapse
```

---

# 34. Shared Resource Problem

Shared resources include:

```text
thread pools
DB pools
network sockets
CPU
memory
```

Failures spread through shared resources.

---

# 35. Why Circuit Breaker Exists

Circuit breaker stops:

```text
repeated unhealthy calls
```

before resource exhaustion happens.

---

# 36. Circuit Breaker Protection Flow

```text
Dependency Slow
      ↓
Failures Detected
      ↓
Circuit Opens
      ↓
Requests Rejected Fast
      ↓
Resources Protected
```

---

# 37. Fast Failure Concept

Instead of:

```text
30 second wait
```

Circuit breaker does:

```text
instant failure
```

Very important optimization.

---

# 38. Fast Failure ASCII

Without CB:

```text
Request
   ↓
30s wait
```

With CB:

```text
Request
   ↓
Immediate rejection
```

---

# 39. Bulkhead Protection

Bulkhead isolates resources.

Example:

```text
payment threads separate
notification threads separate
```

One subsystem failure cannot destroy others.

---

# 40. Bulkhead ASCII

```text
Payment Pool
Notification Pool
Inventory Pool
```

Isolation prevents spreading failure.

---

# 41. Backpressure

System should reject excess work.

Instead of:

```text
accept infinite requests
```

Healthy systems:

```text
shed load
```

---

# 42. Backpressure ASCII

```text
Too Many Requests
        ↓
Reject Early
        ↓
Protect System
```

---

# 43. Rate Limiter Relationship

Rate limiter protects:

```text
traffic volume
```

Circuit breaker protects:

```text
dependency health
```

---

# 44. Timeout Relationship

Timeout protects:

```text
waiting duration
```

Circuit breaker protects:

```text
system stability
```

---

# 45. Retry Relationship

Retry useful for:

```text
temporary/transient failures
```

But dangerous when:

```text
dependency overloaded
```

---

# 46. Real Outage Example Mental Model

Typical outage pattern:

```text
small slowdown
      ↓
timeouts
      ↓
retries
      ↓
thread exhaustion
      ↓
queue explosion
      ↓
service crash
      ↓
cascading failures
```

---

# 47. Why Observability Critical

Need monitoring for:

```text
latency
thread usage
queue size
timeouts
failure rate
retry count
```

Otherwise cascading failures hard to detect early.

---

# 48. Production Metrics

Most important metrics:

```text
p95 latency
p99 latency
thread pool utilization
request queue size
timeout rate
failure rate
```

---

# 49. Java Simulation Mental Model

Without CB:

```java
public String call() {

    return remoteService.call(); // may hang
}
```

Many threads block here.

---

# 50. Java Thread Pool Example

```java
ExecutorService pool =
        Executors.newFixedThreadPool(5);

for (int i = 0; i < 100; i++) {

    pool.submit(() -> {

        Thread.sleep(30000);

        return null;
    });
}
```

---

# 51. Dry Run

Execution:

```text
5 threads occupied
95 tasks waiting
queue grows
system slows
```

Classic cascading failure pattern.

---

# 52. Circuit Breaker Improved Flow

```text
Failures increase
      ↓
Circuit Opens
      ↓
No More Blocking Calls
      ↓
Thread Pool Protected
```

---

# 53. Why HALF_OPEN Important

Need controlled recovery testing.

Otherwise:

```text
service may never recover
```

HALF_OPEN probes carefully.

---

# 54. Distributed Systems Lesson

Most distributed system engineering is actually:

```text
failure management
```

NOT just feature building.

---

# 55. Most Important Mental Model

```text
Distributed Systems Fail Gradually
Before They Fail Completely
```

Circuit breaker detects early warning signals.

---

# 56. Real Production Insight

At scale:

```text
slow services often MORE dangerous
than fully dead services
```

Dead services fail fast.

Slow services consume resources slowly and destroy systems silently.

---

# 57. Interview Explanation

If interviewer asks:

```text
What is cascading failure?
```

Strong answer:

```text
Cascading failure occurs when one unhealthy dependency causes resource
exhaustion such as blocked threads, retries, queue growth, and connection
pool exhaustion, leading failures to spread across the system.
```

Senior-level addition:

```text
Circuit breakers, bulkheads, timeouts, retries, and backpressure are
core resiliency patterns used to stop cascading failures.
```

---

# 58. Final Mental Model

```text
One Slow Dependency
        ↓
Resource Exhaustion
        ↓
Failure Spreads
        ↓
Entire System Collapse
```

Circuit breaker interrupts this chain.

---

# 59. What To Remember

```text
Cascading failure spreads across services.

Slow services are extremely dangerous.

Blocked threads exhaust thread pools.

Retries can worsen overload.

Queues can explode infinitely.

Shared resources propagate failures.

Circuit breakers stop repeated unhealthy calls.

Bulkheads isolate failures.

Backpressure rejects excess work.

Distributed systems require resiliency engineering.
```

---

# 60. Next File

```text
003_CLOSED_State.md
```

Next you learn:

```text
normal traffic flow
success/failure recording
request monitoring
failure counters
sliding window basics
internal metrics collection
```
