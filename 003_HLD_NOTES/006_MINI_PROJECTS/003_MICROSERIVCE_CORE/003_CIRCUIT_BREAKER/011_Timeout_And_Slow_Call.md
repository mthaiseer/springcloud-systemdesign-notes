# 011_Timeout_And_Slow_Call.md

# MiniCircuitBreaker — 011 Timeout And Slow Call

---

# 1. Why This File Exists

Previous files explained:

```text
failure rate
slow call rate
sliding windows
circuit breaker states
```

But now we must understand:

```text
how systems become slow
```

and:

```text
why timeout is critical
```

because distributed systems usually fail through:

```text
latency amplification
```

NOT immediate crashes.

This file explains:

```text
timeout
slow calls
latency degradation
thread blocking
resource exhaustion
timeout propagation
deadline budgets
fail-fast strategy
slow dependency behavior
Spring Boot timeout handling
Resilience4j TimeLimiter
Java implementation
production tuning
```

---

# 2. One-Line Definition

## Timeout

```text
Maximum duration allowed for request execution.
```

## Slow Call

```text
Request taking longer than acceptable latency threshold.
```

---

# 3. Biggest Mental Model

```text
Dependency Slows Down
        ↓
Threads Wait Longer
        ↓
Resources Exhaust
        ↓
System Collapses
```

Timeout prevents this.

---

# 4. Why Slow Calls Dangerous

A service may still return:

```text
HTTP 200 OK
```

but after:

```text
15 seconds
```

Technically successful.

Operationally dangerous.

---

# 5. Why Slow Services Worse Than Dead Services

Dead service:

```text
fails fast
```

Resources released quickly.

Slow service:

```text
holds threads for long duration
```

Much more dangerous.

---

# 6. Slow Call Consequences

Slow calls can cause:

```text
thread pool exhaustion
queue buildup
connection pool exhaustion
Kafka lag
memory pressure
retry storms
latency amplification
```

---

# 7. Slow Call Mental Model

```text
Slow dependency acts like traffic jam.
```

Everything behind it slows too.

---

# 8. Traffic Jam ASCII

```text
Fast Requests
     ↓
One Slow Dependency
     ↓
Queue Builds
     ↓
Entire System Slows
```

---

# 9. What Is Timeout

Timeout defines:

```text
maximum wait duration
```

Example:

```text
2 seconds
```

If request exceeds:

```text
abort operation
```

---

# 10. Timeout Mental Model

```text
Fail Fast
```

instead of:

```text
Wait Forever
```

---

# 11. Why Fail Fast Important

Fail-fast behavior:

```text
releases resources quickly
protects threads
protects queues
protects DB pools
protects application
```

---

# 12. Without Timeout

Without timeout:

```text
requests can wait forever
```

Eventually:

```text
all threads blocked
```

---

# 13. Without Timeout ASCII

```text
Request Starts
      ↓
Waiting...
      ↓
Waiting...
      ↓
Waiting Forever
```

---

# 14. With Timeout

With timeout:

```text
request aborted automatically
```

after configured duration.

---

# 15. With Timeout ASCII

```text
Request Starts
      ↓
2 Seconds Passed?
    yes/no
      ↓
Abort or Continue
```

---

# 16. Timeout Example

Config:

```text
timeout = 2 seconds
```

Dependency latency:

```text
5 seconds
```

Result:

```text
TimeoutException
```

after:

```text
2 seconds
```

---

# 17. Timeout Types

Distributed systems use multiple timeout types:

```text
connect timeout
read timeout
write timeout
response timeout
overall deadline
```

---

# 18. Connect Timeout

Controls:

```text
maximum TCP connection establishment time
```

---

# 19. Read Timeout

Controls:

```text
maximum waiting time for response data
```

---

# 20. Response Timeout

Controls:

```text
total HTTP response duration
```

---

# 21. Overall Deadline

Overall deadline means:

```text
entire request must complete within budget
```

Example:

```text
5 seconds total
```

including:

```text
network
serialization
DB
downstream services
```

---

# 22. Deadline Mental Model

```text
Total Time Budget
```

---

# 23. Timeout Propagation

Distributed systems should propagate:

```text
remaining timeout budget
```

across services.

---

# 24. Propagation Example

Gateway budget:

```text
5 seconds
```

Gateway uses:

```text
1 second
```

Remaining budget:

```text
4 seconds
```

passed downstream.

---

# 25. Why Propagation Important

Without propagation:

```text
every service waits full timeout
```

Latency multiplies across chain.

---

# 26. Latency Amplification ASCII

```text
Gateway waits 5s
Payment waits 5s
DB waits 5s

Total latency explodes
```

---

# 27. Better Deadline Propagation

```text
Gateway budget = 5s
Payment budget = 4s
DB budget = 2s
```

Total latency bounded.

---

# 28. Slow Call Threshold

Circuit breaker defines:

```text
what latency counts as slow?
```

Example:

```text
2 seconds
```

---

# 29. Slow Call Example

Latency:

```text
3.5 seconds
```

Threshold:

```text
2 seconds
```

Result:

```text
slow call recorded
```

---

# 30. Why Slow Call Tracking Important

Slow calls usually happen BEFORE:

```text
timeouts
failures
complete outage
```

Acts as:

```text
early warning signal
```

---

# 31. Real Production Failure Pattern

Typical outage progression:

```text
healthy
   ↓
slightly slower
   ↓
very slow
   ↓
timeouts
   ↓
failures
   ↓
system collapse
```

---

# 32. Resource Exhaustion Flow

```text
Slow Dependency
      ↓
Threads Block
      ↓
Queue Grows
      ↓
Timeouts Increase
      ↓
Retries Increase
      ↓
Even More Load
```

---

# 33. Retry Storm Interaction

Slow services trigger:

```text
more retries
```

Retries create:

```text
more traffic
```

This causes:

```text
retry storm
```

---

# 34. Retry Storm ASCII

```text
Slow Service
      ↓
Timeouts
      ↓
Retries
      ↓
More Traffic
      ↓
Even Slower
```

---

# 35. Why Circuit Breaker Needed

Circuit breaker interrupts:

```text
traffic to unhealthy dependency
```

preventing:

```text
infinite retry pressure
```

---

# 36. Why Bulkhead Needed

Timeout alone not enough.

Need:

```text
resource isolation
```

Otherwise one slow dependency blocks everything.

---

# 37. Timeout + Bulkhead + CircuitBreaker

Typical protection stack:

```text
Bulkhead
   ↓
Timeout
   ↓
CircuitBreaker
```

---

# 38. Tomcat Thread Exhaustion

Spring Boot MVC:

```text
Tomcat threads = 200
```

Each request waits:

```text
10 seconds
```

Eventually:

```text
all 200 threads blocked
```

---

# 39. Tomcat Exhaustion ASCII

```text
Incoming Requests
      ↓
Tomcat Thread Pool
      ↓
All Threads Waiting
      ↓
Application Stops Responding
```

---

# 40. Queue Explosion

When threads unavailable:

```text
requests queue up
```

Large queues cause:

```text
memory growth
latency spikes
OOM risk
```

---

# 41. Kafka Consumer Slow Dependency

Kafka consumer calls slow API.

Without timeout:

```text
consumer processing stalls
```

Result:

```text
Kafka lag increases
```

---

# 42. Database Slow Query Example

Slow SQL query:

```text
SELECT JOIN ...
```

can block:

```text
DB connections
transactions
request threads
```

even if query succeeds eventually.

---

# 43. Slow Call Rate Formula

```text
Slow Call Rate =
(Slow Calls / Total Calls) × 100
```

---

# 44. Slow Call Example

Metrics:

```text
Total Calls = 100
Slow Calls = 70
```

Calculation:

```text
70%
```

Threshold:

```text
50%
```

Result:

```text
Circuit OPEN
```

---

# 45. Why Timeout Value Difficult

Timeout too short:

```text
false failures
unnecessary retries
```

Timeout too long:

```text
resource exhaustion
```

Need balance.

---

# 46. Typical Timeout Strategy

Common production strategy:

```text
connect timeout = 1s
response timeout = 2s
overall deadline = 5s
```

Depends on business requirements.

---

# 47. External API Timeout Strategy

External APIs unpredictable.

Usually:

```text
strict timeout
small retries
fallback response
```

---

# 48. Payment Timeout Strategy

Payments sensitive.

Need:

```text
strict timeout
limited retries
idempotency
```

---

# 49. Streaming Timeout Strategy

Streaming systems prioritize throughput.

May tolerate:

```text
slightly higher latency
```

but not:

```text
thread starvation
```

---

# 50. Timeout Flow

```text
Request Starts
      ↓
Timer Starts
      ↓
Response Received?
    yes/no
      ↓
Success or Timeout
```

---

# 51. Java Timeout Example

```java
import java.util.concurrent.*;

public class TimeoutExample {

    public static void main(String[] args)
            throws Exception {

        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        Future<String> future =
                executor.submit(() -> {

                    Thread.sleep(5000);

                    return "Success";
                });

        try {

            String result =
                    future.get(
                            2,
                            TimeUnit.SECONDS
                    );

            System.out.println(result);

        } catch (TimeoutException e) {

            System.out.println(
                    "Request timed out"
            );
        }

        executor.shutdown();
    }
}
```

---

# 52. Java Dry Run

Task duration:

```text
5 seconds
```

Timeout:

```text
2 seconds
```

After 2 seconds:

```text
TimeoutException thrown
```

---

# 53. Slow Call Detection Java Example

```java
public class SlowCallDetector {

    public static boolean isSlowCall(
            long durationMs,
            long thresholdMs) {

        return durationMs > thresholdMs;
    }
}
```

---

# 54. Slow Call Dry Run

Threshold:

```text
2000ms
```

Latency:

```text
3500ms
```

Result:

```text
true
```

Counts as slow call.

---

# 55. Spring Boot RestTemplate Timeout

```java
@Bean
public RestTemplate restTemplate() {

    SimpleClientHttpRequestFactory factory =
            new SimpleClientHttpRequestFactory();

    factory.setConnectTimeout(1000);

    factory.setReadTimeout(2000);

    return new RestTemplate(factory);
}
```

---

# 56. WebClient Timeout Example

```java
WebClient.builder()
    .clientConnector(
        new ReactorClientHttpConnector(
            HttpClient.create()
                .responseTimeout(
                    Duration.ofSeconds(2)
                )
        )
    )
    .build();
```

---

# 57. Resilience4j TimeLimiter

```yaml
resilience4j:
  timelimiter:
    instances:
      paymentService:
        timeoutDuration: 2s
```

---

# 58. TimeLimiter Meaning

```text
abort operation after 2 seconds
```

---

# 59. Slow Call Threshold Config

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        slowCallRateThreshold: 50
        slowCallDurationThreshold: 2s
```

---

# 60. Config Meaning

```text
Calls slower than 2s = slow

If 50% calls become slow:
OPEN circuit
```

---

# 61. Production Metrics

Monitor:

```text
timeout count
slow-call count
p95 latency
p99 latency
thread pool saturation
queue depth
```

---

# 62. Why p99 Important

Average latency may look healthy.

But:

```text
p99 spikes
```

usually indicate:

```text
slow-call explosion
```

before outage.

---

# 63. Common Mistakes

## Mistake 1

```text
No timeout
```

Threads wait forever.

---

## Mistake 2

```text
Very large timeout
```

Resource exhaustion risk.

---

## Mistake 3

```text
Very tiny timeout
```

False failures.

---

## Mistake 4

```text
No deadline propagation
```

Latency amplification.

---

## Mistake 5

```text
Ignoring slow calls
```

Misses early warning signals.

---

## Mistake 6

```text
No bulkhead
```

Slow dependency blocks everything.

---

# 64. Most Important Insight

```text
Distributed systems usually fail gradually through latency degradation.
```

NOT immediate crashes.

Timeouts exist to stop latency amplification.

---

# 65. Distributed Systems Insight

Modern resiliency engineering focuses heavily on:

```text
latency management
```

because:

```text
slow systems can destroy resources before failing completely
```

---

# 66. Interview Explanation

If interviewer asks:

```text
Why are slow calls dangerous?
```

Strong answer:

```text
Slow calls occupy threads, queues, and connection pools for long periods,
causing resource exhaustion, queue growth, retry storms, and latency
amplification across distributed systems.
```

Senior addition:

```text
Modern distributed systems often degrade gradually through latency before
complete outages, making timeout and slow-call detection critical.
```

---

# 67. Final Mental Model

```text
Slow Dependency
       ↓
Latency Amplification
       ↓
Resource Exhaustion
       ↓
System Collapse

Timeout Stops This Chain
```

---

# 68. What To Remember

```text
Timeout prevents waiting forever.

Slow calls are dangerous even when technically successful.

Latency amplification causes cascading failures.

Slow calls act as early warning signals.

Timeout protects threads and queues.

Deadline propagation prevents latency multiplication.

Bulkhead + Timeout + CircuitBreaker work together.

Modern resiliency engineering focuses heavily on latency.
```

---

# 69. Next File

```text
012_Exception_Classification.md
```

Next you learn:

```text
retryable exceptions
ignored exceptions
business vs system exceptions
HTTP status mapping
exception filtering
custom predicates
production exception handling
```
