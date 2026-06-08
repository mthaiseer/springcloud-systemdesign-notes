# 011_Bulkhead_Pattern.md

# MiniCircuitBreaker — 011 Bulkhead Pattern

---

# 1. Why This File Exists

Circuit breaker protects against:

```text
failing dependencies
slow dependencies
repeated unhealthy calls
```

But there is another major failure mode:

```text
shared resource exhaustion
```

Example:

```text
Payment API becomes slow
```

If all requests share the same thread pool:

```text
payment calls consume all threads
inventory calls also fail
search calls also fail
auth calls also fail
```

This is called:

```text
failure blast radius
```

Bulkhead Pattern exists to:

```text
isolate resources
contain failure
keep rest of system alive
```

This file explains:

```text
bulkhead pattern
ship compartment mental model
thread pool isolation
semaphore isolation
bounded queues
Tomcat thread exhaustion
Kafka consumer isolation
DB pool isolation
WebClient/Reactor caution
Resilience4j Bulkhead
ThreadPoolBulkhead
Java implementations
dry runs
production tuning
observability
interview explanation
```

---

# 2. One-Line Definition

```text
Bulkhead Pattern isolates resources so one failing dependency cannot exhaust the whole system.
```

---

# 3. Biggest Mental Model

```text
One Dependency Fails
        ↓
Only Its Compartment Fails
        ↓
Other Compartments Continue
```

---

# 4. Origin Of Bulkhead

Bulkhead comes from:

```text
ship engineering
```

A ship is divided into:

```text
watertight compartments
```

If one compartment floods:

```text
ship does not sink completely
```

---

# 5. Ship Bulkhead ASCII

```text
Ship Compartments

+----------+----------+----------+----------+
| Engine   | Cargo    | Fuel     | Crew     |
+----------+----------+----------+----------+

Cargo Floods
      ↓
Only Cargo Compartment Damaged
      ↓
Ship Survives
```

---

# 6. Software Bulkhead Mental Model

In software, compartments are:

```text
thread pools
connection pools
semaphores
queues
CPU limits
memory limits
Kafka consumer workers
```

---

# 7. Without Bulkhead

Suppose service calls:

```text
Payment API
Inventory API
Search API
Notification API
```

All use one shared pool.

If Payment API becomes slow:

```text
all shared threads block on Payment
```

Then even healthy dependencies fail.

---

# 8. Without Bulkhead ASCII

```text
Shared Thread Pool

[ T1 ][ T2 ][ T3 ][ T4 ][ T5 ]

All threads waiting on Payment API
        ↓
Inventory cannot execute
Search cannot execute
Notification cannot execute
```

---

# 9. With Bulkhead

Separate pools:

```text
Payment Pool
Inventory Pool
Search Pool
Notification Pool
```

If Payment slows:

```text
only Payment Pool saturates
```

Others continue.

---

# 10. With Bulkhead ASCII

```text
Payment Pool       → Saturated
Inventory Pool     → Healthy
Search Pool        → Healthy
Notification Pool  → Healthy
```

---

# 11. Why Bulkhead Is Critical

Distributed systems cannot prevent every failure.

So the goal is:

```text
failure containment
```

NOT:

```text
failure elimination
```

---

# 12. Failure Blast Radius

Blast radius means:

```text
how far failure spreads
```

Bulkhead reduces blast radius.

---

# 13. Blast Radius ASCII

Without bulkhead:

```text
Payment Failure
      ↓
Whole App Down
```

With bulkhead:

```text
Payment Failure
      ↓
Only Payment Feature Degraded
```

---

# 14. Resource Exhaustion Problem

Microservices share resources:

```text
Tomcat threads
Netty event loop
DB connections
HTTP connection pools
Kafka consumer threads
CPU
memory
queues
```

Any shared resource can become bottleneck.

---

# 15. Tomcat Thread Exhaustion Example

Spring Boot MVC app:

```text
Tomcat max threads = 200
```

Payment API becomes slow.

Each request waits:

```text
10 seconds
```

Soon:

```text
200 threads blocked
```

Now:

```text
new requests cannot be processed
```

---

# 16. Tomcat Exhaustion ASCII

```text
Incoming Requests
      ↓
Tomcat Thread Pool
      ↓
All Threads Waiting On Payment
      ↓
Application Stops Responding
```

---

# 17. Queue Explosion

When no threads available:

```text
requests queue up
```

Large queues cause:

```text
latency spikes
memory pressure
timeouts
```

---

# 18. Queue Explosion ASCII

```text
Threads Full
    ↓
Queue Grows
    ↓
Memory Grows
    ↓
Timeouts Increase
    ↓
System Collapse
```

---

# 19. Bulkhead Types

Main types:

```text
Semaphore Bulkhead
Thread Pool Bulkhead
Connection Pool Bulkhead
Queue Bulkhead
```

Most common in Resilience4j:

```text
SemaphoreBulkhead
ThreadPoolBulkhead
```

---

# 20. Semaphore Bulkhead

Semaphore bulkhead limits:

```text
concurrent executions
```

No separate thread pool.

It allows only:

```text
N calls at once
```

---

# 21. Semaphore Mental Model

```text
maxConcurrentCalls = 5

5 calls allowed
6th call rejected
```

---

# 22. Semaphore ASCII

```text
Permits:

[ P ][ P ][ P ][ P ][ P ]

All permits used:
new request → rejected
```

---

# 23. Semaphore Bulkhead Use Cases

Good for:

```text
non-blocking calls
fast calls
limiting concurrency
protecting DB/API
```

---

# 24. Thread Pool Bulkhead

Thread pool bulkhead uses:

```text
dedicated worker threads
```

for specific dependency.

Example:

```text
Payment calls use paymentExecutor
Inventory calls use inventoryExecutor
```

---

# 25. Thread Pool Bulkhead ASCII

```text
Payment Requests
      ↓
Payment Thread Pool

Inventory Requests
      ↓
Inventory Thread Pool
```

---

# 26. Thread Pool Bulkhead Use Cases

Good for:

```text
blocking I/O
slow external APIs
legacy clients
RestTemplate
JDBC calls
```

---

# 27. Semaphore vs Thread Pool

## Semaphore Bulkhead

```text
limits concurrency
same caller thread
lower overhead
```

## Thread Pool Bulkhead

```text
isolates execution thread
adds queue
higher overhead
better for blocking calls
```

---

# 28. Comparison Table

```text
Semaphore:
- low overhead
- no queue by default
- good for non-blocking/fast calls

ThreadPool:
- dedicated threads
- queue possible
- good for blocking calls
```

---

# 29. Bulkhead vs Circuit Breaker

## Circuit Breaker

Protects from:

```text
unhealthy dependency
```

## Bulkhead

Protects from:

```text
resource exhaustion
```

They solve different problems.

---

# 30. Combined Protection

Production systems usually combine:

```text
Timeout
Bulkhead
Circuit Breaker
Retry
Fallback
```

---

# 31. Recommended Order Mental Model

```text
Request
   ↓
Bulkhead: do we have capacity?
   ↓
Timeout: how long can call run?
   ↓
CircuitBreaker: is dependency healthy?
   ↓
Retry: should we retry safely?
   ↓
Fallback: graceful degradation
```

---

# 32. Why Timeout Needed With Bulkhead

Bulkhead limits concurrency.

But if calls never finish:

```text
permits never release
threads never free
```

Need timeout.

---

# 33. Why Circuit Breaker Needed With Bulkhead

Bulkhead limits damage.

But bad dependency still receives traffic.

Circuit breaker stops traffic when unhealthy.

---

# 34. Why Retry Must Be Controlled

Retry can multiply traffic.

Bulkhead prevents retry storms from consuming all resources.

---

# 35. Backpressure

Bulkhead creates:

```text
backpressure
```

Meaning:

```text
reject excess work early
```

instead of accepting infinite work.

---

# 36. Backpressure ASCII

```text
Too Much Load
      ↓
Bulkhead Full
      ↓
Reject Early
      ↓
System Protected
```

---

# 37. Bounded Queue

Thread pool bulkheads often have queues.

Queue must be:

```text
bounded
```

Unbounded queues are dangerous.

---

# 38. Unbounded Queue Problem

Unbounded queue can cause:

```text
memory growth
long latency
out of memory
late failures
```

---

# 39. Bounded Queue ASCII

```text
Queue Capacity = 100

Request 1..100 → queued
Request 101    → rejected
```

Controlled rejection protects system.

---

# 40. Rejection Strategy

When bulkhead full:

```text
reject request
return fallback
return 429/503
drop low-priority task
```

---

# 41. Controlled Failure Is Good

Controlled rejection is better than:

```text
entire system outage
```

---

# 42. Real Example — Payment Isolation

Order service calls:

```text
Payment
Inventory
Notification
```

Payment API slow.

With bulkhead:

```text
Payment Pool exhausted
Inventory still works
Notification still works
```

---

# 43. Real Example — Kafka Consumer

Kafka consumer calls external API.

External API slow.

Without bulkhead:

```text
all consumer threads blocked
Kafka lag explodes
```

With bulkhead:

```text
only limited calls in-flight
consumer can pause/retry/DLT safely
```

---

# 44. Real Example — Database Isolation

Separate pools:

```text
read pool
write pool
analytics pool
```

Heavy analytics cannot consume write connections.

---

# 45. DB Pool Bulkhead ASCII

```text
Write Pool      → protected
Read Pool       → protected
Analytics Pool  → can saturate alone
```

---

# 46. WebFlux / Netty Warning

In reactive systems:

```text
do not block event loop
```

If blocking work runs on event loop:

```text
entire reactive app stalls
```

Use:

```text
boundedElastic
or dedicated scheduler
```

---

# 47. Reactive Bulkhead Mental Model

```text
Event Loop
    ↓
non-blocking only

Blocking Work
    ↓
separate bounded scheduler
```

---

# 48. Java Semaphore Bulkhead

```java
import java.util.concurrent.Semaphore;

public class SemaphoreBulkhead {

    private final Semaphore semaphore;

    public SemaphoreBulkhead(
            int maxConcurrentCalls) {

        this.semaphore =
                new Semaphore(maxConcurrentCalls);
    }

    public String execute() {

        if (!semaphore.tryAcquire()) {

            return "Bulkhead full";
        }

        try {

            return remoteCall();

        } finally {

            semaphore.release();
        }
    }

    private String remoteCall() {

        return "OK";
    }
}
```

---

# 49. Semaphore Dry Run

Config:

```text
maxConcurrentCalls = 3
```

Requests:

```text
R1 → permit acquired
R2 → permit acquired
R3 → permit acquired
R4 → rejected
```

When R1 finishes:

```text
permit released
```

Now R5 can enter.

---

# 50. Why finally Is Critical

If permit not released:

```text
permit leak
```

Eventually:

```text
bulkhead permanently full
```

Always release in:

```java
finally
```

---

# 51. Java ThreadPool Bulkhead

```java
import java.util.concurrent.*;

public class ThreadPoolBulkhead {

    private final ThreadPoolExecutor executor;

    public ThreadPoolBulkhead() {

        this.executor =
                new ThreadPoolExecutor(
                        5,
                        10,
                        30,
                        TimeUnit.SECONDS,
                        new ArrayBlockingQueue<>(20),
                        new ThreadPoolExecutor.AbortPolicy()
                );
    }

    public Future<String> execute() {

        return executor.submit(() -> remoteCall());
    }

    private String remoteCall() {

        return "OK";
    }
}
```

---

# 52. ThreadPoolExecutor Fields

```text
corePoolSize = minimum worker threads
maxPoolSize = maximum worker threads
queueCapacity = waiting task capacity
keepAlive = extra thread lifetime
rejectionPolicy = behavior when full
```

---

# 53. Thread Pool Dry Run

Config:

```text
core = 5
max = 10
queue = 20
```

Flow:

```text
first 5 tasks → threads
next 20 tasks → queue
next 5 tasks → grow to max threads
after that → reject
```

---

# 54. Rejection Policies

Common Java policies:

```text
AbortPolicy
CallerRunsPolicy
DiscardPolicy
DiscardOldestPolicy
```

---

# 55. AbortPolicy

```text
throw RejectedExecutionException
```

Good when you want:

```text
fail fast
```

---

# 56. CallerRunsPolicy

Caller thread executes task.

Creates natural:

```text
backpressure
```

But may slow caller.

---

# 57. CallerRunsPolicy Mental Model

```text
Pool Full
   ↓
Caller Runs Task
   ↓
Caller Slows Down
   ↓
Load Reduced
```

---

# 58. Spring Boot Executor Example

```java
@Bean("paymentExecutor")
public Executor paymentExecutor() {

    ThreadPoolTaskExecutor executor =
            new ThreadPoolTaskExecutor();

    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(20);
    executor.setThreadNamePrefix("payment-");
    executor.initialize();

    return executor;
}
```

---

# 59. Resilience4j Semaphore Bulkhead Config

```yaml
resilience4j:
  bulkhead:
    instances:
      paymentService:
        maxConcurrentCalls: 10
        maxWaitDuration: 0ms
```

Meaning:

```text
allow 10 concurrent calls
reject immediately when full
```

---

# 60. Resilience4j ThreadPoolBulkhead Config

```yaml
resilience4j:
  thread-pool-bulkhead:
    instances:
      paymentService:
        coreThreadPoolSize: 5
        maxThreadPoolSize: 10
        queueCapacity: 20
        keepAliveDuration: 30s
```

---

# 61. Config Meaning

```text
5 core threads
up to 10 max threads
20 queued tasks
reject after full
```

---

# 62. Bulkhead Sizing Mental Model

Pool size depends on:

```text
expected QPS
average latency
acceptable queueing
dependency capacity
```

---

# 63. Little's Law Intuition

Simple intuition:

```text
concurrency ≈ throughput × latency
```

Example:

```text
50 requests/sec
latency 200ms = 0.2s
needed concurrency ≈ 50 × 0.2 = 10
```

So bulkhead size around:

```text
10–20
```

depending on headroom.

---

# 64. Sizing Formula

```text
requiredConcurrency = QPS × averageLatencySeconds
```

Add headroom:

```text
poolSize = requiredConcurrency × 1.5 or 2
```

---

# 65. Sizing Example

```text
QPS = 100
latency = 100ms = 0.1s
```

Concurrency:

```text
100 × 0.1 = 10
```

With headroom:

```text
15–20
```

---

# 66. CPU-Bound vs IO-Bound

## CPU-Bound

Pool size near:

```text
CPU cores
```

## IO-Bound

Pool size can be larger because threads wait on network.

---

# 67. IO-Bound Example

External HTTP calls are usually:

```text
IO-bound
```

Thread waits on network response.

Bulkhead pool can be larger than CPU cores.

---

# 68. CPU-Bound Example

Image processing:

```text
CPU-heavy
```

Too many threads cause:

```text
context switching
CPU contention
```

---

# 69. Production Metrics

Monitor:

```text
active threads
queue depth
rejected calls
bulkhead saturation
wait duration
dependency latency
```

---

# 70. Saturation Warning

If queue constantly full:

```text
bulkhead too small
or dependency too slow
or traffic too high
```

Need investigation.

---

# 71. Grafana Dashboard Ideas

Track:

```text
bulkhead.available.concurrent.calls
bulkhead.max.allowed.concurrent.calls
bulkhead.rejected.calls
executor.active.threads
executor.queue.size
```

---

# 72. Common Mistakes

## Mistake 1

```text
One global thread pool for all dependencies
```

Bad isolation.

---

## Mistake 2

```text
Unbounded queue
```

OOM risk.

---

## Mistake 3

```text
No timeout
```

Threads never release.

---

## Mistake 4

```text
Pool too large
```

Can overload dependency.

---

## Mistake 5

```text
Pool too small
```

Unnecessary rejections.

---

## Mistake 6

```text
Blocking on Netty event loop
```

Reactive app stalls.

---

# 73. Bulkhead + CircuitBreaker Production Stack

```text
Client Request
      ↓
RateLimiter
      ↓
Bulkhead
      ↓
TimeLimiter / Timeout
      ↓
CircuitBreaker
      ↓
Retry With Backoff
      ↓
Fallback
```

---

# 74. Why Order Matters

If bulkhead first:

```text
reject overload early
```

If timeout missing:

```text
bulkhead resources can leak
```

If retry before bulkhead:

```text
retry storm may fill bulkhead
```

---

# 75. Interview Explanation

If interviewer asks:

```text
What is Bulkhead Pattern?
```

Strong answer:

```text
Bulkhead Pattern isolates resources such as threads, queues, and
connections so one slow or failing dependency cannot exhaust the entire
application. It reduces failure blast radius.
```

Senior addition:

```text
Bulkheads are implemented using semaphores or dedicated thread pools.
Production systems combine them with timeouts, circuit breakers, bounded
queues, and observability metrics.
```

---

# 76. Final Mental Model

```text
Bulkhead =
Resource Isolation
+
Failure Containment
+
Blast Radius Reduction
```

---

# 77. What To Remember

```text
Bulkhead isolates resources.

Semaphore bulkhead limits concurrency.

Thread pool bulkhead isolates worker threads.

Bounded queues prevent memory explosion.

Controlled rejection is better than collapse.

Bulkhead and circuit breaker solve different problems.

Bulkhead protects against resource exhaustion.

Circuit breaker protects against unhealthy dependency calls.

Use separate pools for critical dependencies.

Monitor saturation and rejected calls.

Reactive systems must not block event loops.
```

---

# 78. Next File

```text
012_Timeout_And_Retry.md
```

Next you learn:

```text
timeouts
retry storms
exponential backoff
jitter
deadline propagation
Retry vs Circuit Breaker
production retry strategies
```
