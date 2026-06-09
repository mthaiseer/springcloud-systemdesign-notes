# 099_CircuitBreaker_Final_CheatSheet.md

# MiniCircuitBreaker — Final CheatSheet + Mental Models

---

# 1. Core Goal

```text
Prevent one failing dependency
from collapsing the entire system.
```

---

# 2. Biggest Mental Model

```text
Failures are normal.
Systems must survive them.
```

---

# 3. CircuitBreaker Mental Model

```text
Dependency unhealthy?
STOP sending traffic temporarily.
```

---

# 4. Real World Mental Model

```text
Electric circuit overload
        ↓
Fuse opens
        ↓
Current stops
        ↓
System protected
```

Same in software:

```text
Dependency failing
        ↓
Circuit OPEN
        ↓
Requests blocked
        ↓
System protected
```

---

# 5. Three Main States

```text
CLOSED
OPEN
HALF_OPEN
```

---

# 6. CLOSED State

```text
Normal traffic allowed.
Metrics collected.
```

Mental model:

```text
Healthy dependency
```

---

# 7. OPEN State

```text
Requests rejected immediately.
```

Mental model:

```text
Dependency considered unhealthy.
```

---

# 8. HALF_OPEN State

```text
Allow few test requests.
```

Mental model:

```text
Testing recovery carefully.
```

---

# 9. State Transition Mental Model

```text
CLOSED
   ↓ failures
OPEN
   ↓ wait time
HALF_OPEN
   ↓ success
CLOSED
```

---

# 10. Why CircuitBreaker Exists

Without CB:

```text
slow dependency
     ↓
threads blocked
     ↓
timeouts increase
     ↓
retries increase
     ↓
system collapse
```

---

# 11. Cascading Failure Mental Model

```text
One failure spreads everywhere.
```

---

# 12. Failure Containment Mental Model

```text
Keep failure local.
Do not let it spread.
```

---

# 13. Sliding Window Mental Model

```text
Recent history decides health.
```

---

# 14. COUNT_BASED Window

```text
Last N requests
```

Example:

```text
last 20 calls
```

---

# 15. TIME_BASED Window

```text
Last N seconds
```

Example:

```text
last 60 seconds
```

---

# 16. Failure Rate Formula

```text
failureRate =
failedCalls / totalCalls * 100
```

---

# 17. Slow Call Rate Mental Model

```text
Dependency may not fail,
but can still kill system if too slow.
```

---

# 18. Slow Call Example

```text
2 second timeout threshold

500ms = healthy
5 sec = slow call
```

---

# 19. Timeout Mental Model

```text
Never wait forever.
```

---

# 20. Retry Mental Model

```text
Temporary failure?
Try again carefully.
```

---

# 21. Retry Danger

```text
Retries can amplify outages.
```

---

# 22. Retry Storm Mental Model

```text
dependency slow
      ↓
clients retry
      ↓
more traffic
      ↓
dependency slower
```

---

# 23. Exponential Backoff

```text
100ms
200ms
400ms
800ms
```

---

# 24. Jitter Mental Model

```text
Add randomness
to avoid synchronized retry spikes.
```

---

# 25. Bulkhead Mental Model

```text
Separate compartments in ship.
```

One flooded section:

```text
ship still survives
```

---

# 26. Bulkhead In Software

```text
Separate thread pools
for separate dependencies.
```

---

# 27. Why Bulkhead Important

```text
Payment failure should not kill inventory.
```

---

# 28. RateLimiter Mental Model

```text
Protect dependency from too much traffic.
```

---

# 29. Fallback Mental Model

```text
Provide degraded behavior
instead of total failure.
```

---

# 30. Good Fallback Examples

```text
PAYMENT_PENDING
cached data
empty recommendations
retry later
```

---

# 31. Bad Fallback

```text
Fake success
```

Dangerous for:

```text
payments
banking
transactions
```

---

# 32. Graceful Degradation

```text
Some features degrade,
core system survives.
```

---

# 33. Brownout Mental Model

```text
Disable non-critical features
during overload.
```

---

# 34. Load Shedding Mental Model

```text
Reject excess traffic intentionally
to protect system stability.
```

---

# 35. Thread Pool Mental Model

```text
Threads are limited resources.
```

---

# 36. Little's Law

```text
Concurrency =
Throughput × Latency
```

---

# 37. Example

```text
100 req/sec
Latency = 0.2 sec

100 × 0.2 = 20 threads
```

---

# 38. HALF_OPEN Mental Model

```text
Do NOT send full traffic immediately.
```

---

# 39. HALF_OPEN Goal

```text
Controlled recovery.
```

---

# 40. Recovery Storm

Bad:

```text
OPEN → full traffic
```

Dependency crashes again.

---

# 41. Thread Safety Mental Model

```text
Many threads access breaker simultaneously.
```

Need:

```text
safe state transitions
```

---

# 42. CAS Mental Model

```text
Only one thread wins update.
```

---

# 43. AtomicReference Mental Model

```text
Store shared state safely across threads.
```

---

# 44. Resilience4j Mental Model

```text
Thread-safe state machine
around function execution.
```

---

# 45. Decorator Mental Model

```text
Wrap original function
with resiliency behavior.
```

---

# 46. Spring AOP Mental Model

```text
Proxy intercepts method
before real execution.
```

---

# 47. Event Publisher Mental Model

```text
Important lifecycle events emitted internally.
```

Examples:

```text
OPEN
HALF_OPEN
failure
slow call
rejected call
```

---

# 48. Metrics Mental Model

```text
Metrics show WHAT happening.
```

---

# 49. Logs Mental Model

```text
Logs show WHY happening.
```

---

# 50. Tracing Mental Model

```text
Tracing shows WHERE request traveled.
```

---

# 51. Prometheus Mental Model

```text
Prometheus pulls metrics
from application.
```

---

# 52. Grafana Mental Model

```text
Visualize metrics using dashboards.
```

---

# 53. OpenTelemetry Mental Model

```text
Track request across distributed services.
```

---

# 54. TraceId Mental Model

```text
Single request identifier
across all services.
```

---

# 55. Kafka Resiliency Mental Model

```text
Never lose messages.
```

---

# 56. Kafka Retry Architecture

```text
main-topic
   ↓
retry-topic
   ↓
DLT
```

---

# 57. DLT Mental Model

```text
Store poison messages separately.
```

---

# 58. Poison Message

```text
Message that always fails.
```

---

# 59. Idempotency Mental Model

```text
Safe to process same message multiple times.
```

---

# 60. Outbox Pattern Mental Model

```text
DB write + Kafka publish consistency.
```

---

# 61. DB Protection Mental Model

Protect DB using:

```text
timeouts
pool limits
bulkheads
breaker
cache fallback
```

---

# 62. Queue Protection Mental Model

```text
Unlimited queues eventually kill system.
```

---

# 63. Bounded Queue Mental Model

```text
Reject work instead of crashing.
```

---

# 64. Cache Fallback Mental Model

```text
Better stale data than outage.
```

---

# 65. Stale-While-Revalidate

```text
Serve stale cache temporarily.
Refresh later.
```

---

# 66. Multi-Region Mental Model

```text
One region fails,
system still survives.
```

---

# 67. Active-Active

```text
All regions serve traffic.
```

---

# 68. Active-Passive

```text
Backup region activated during outage.
```

---

# 69. Chaos Engineering Mental Model

```text
Intentionally break system
to test resiliency.
```

---

# 70. SRE Golden Signals

```text
Latency
Traffic
Errors
Saturation
```

---

# 71. P99 Mental Model

```text
Worst-case latency matters more than average.
```

---

# 72. Consumer Lag Mental Model

```text
Consumers falling behind producers.
```

---

# 73. Connection Pool Exhaustion

```text
All DB connections busy.
New requests blocked.
```

---

# 74. Production Resiliency Stack

```text
Timeout
Retry
CircuitBreaker
Bulkhead
RateLimiter
Fallback
Caching
Observability
Autoscaling
```

---

# 75. Production Goal

```text
Controlled degradation,
not perfect uptime.
```

---

# 76. Most Important Insight

```text
Distributed systems fail constantly.
Good systems survive failures gracefully.
```

---

# 77. Interview Mental Model

If interviewer asks:

```text
Why CircuitBreaker?
```

Answer mentally:

```text
Prevent cascading failures.
Protect resources.
Enable graceful degradation.
```

---

# 78. Senior-Level Insight

```text
Resiliency is resource management under failure.
```

---

# 79. Final Mental Model

```text
Failures are inevitable.

Survival is architecture.
```

---

# 80. Quick Recall Summary

```text
Timeout → stop waiting forever

Retry → retry transient failures carefully

Backoff → spread retries over time

Jitter → avoid retry synchronization

CircuitBreaker → stop repeated bad calls

Bulkhead → isolate resources

RateLimiter → protect dependencies

Fallback → degraded response

Cache → reduce dependency pressure

Tracing → follow request path

Metrics → observe system health

DLT → isolate poison messages

Idempotency → safe retries

Outbox → DB + Kafka consistency
```

---

# 81. Ultimate Production Philosophy

```text
You cannot eliminate failures.

You engineer systems
that continue operating during failures.
```
