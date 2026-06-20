# 010_Failure_Rate_And_Slow_Call_Rate.md

# MiniCircuitBreaker — 010 Failure Rate And Slow Call Rate

---

# 1. Why This File Exists

Previous files explained:

```text
count-based windows
time-based windows
failure thresholds
slow call thresholds
```

Now we combine everything together.

Modern circuit breakers do NOT only detect:

```text
exceptions
```

They also detect:

```text
latency degradation
```

because in real distributed systems:

```text
slow services often destroy systems
before complete failure happens
```

This file explains:

```text
failure rate
slow call rate
combined health evaluation
latency amplification
resource exhaustion
early warning detection
production outage behavior
threshold tuning
Resilience4j internals
Java implementation
dry runs
```

---

# 2. One-Line Definition

```text
Failure rate measures failures.
Slow call rate measures latency degradation.
```

Both together determine:

```text
service health
```

---

# 3. Biggest Mental Model

```text
Failures
    +
Slow Responses
    ↓
Overall Health Signal
    ↓
Circuit OPEN Decision
```

---

# 4. Traditional Failure Detection Problem

Older systems only tracked:

```text
exceptions
timeouts
HTTP 500
connection failures
```

But distributed systems discovered:

```text
slow services are equally dangerous
```

Sometimes MORE dangerous.

---

# 5. Why Slow Services Dangerous

Dead service:

```text
fails immediately
```

Threads released quickly.

Slow service:

```text
holds resources for long time
```

Much worse operationally.

---

# 6. Slow Service Consequences

Slow services can cause:

```text
thread pool exhaustion
queue growth
DB connection exhaustion
latency amplification
retry storms
memory pressure
```

---

# 7. Failure Rate Mental Model

Failure rate answers:

```text
How many requests failed?
```

---

# 8. Failure Rate Formula

```text
Failure Rate =
(Failed Calls / Total Calls) × 100
```

---

# 9. Failure Rate Example

Metrics:

```text
Total Calls = 100
Failed Calls = 40
```

Calculation:

```text
Failure Rate =
(40 / 100) × 100 = 40%
```

---

# 10. Healthy Failure Example

Threshold:

```text
50%
```

Current:

```text
40%
```

Result:

```text
stay CLOSED
```

---

# 11. Unhealthy Failure Example

Metrics:

```text
Total Calls = 100
Failed Calls = 70
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
OPEN
```

---

# 12. Slow Call Rate Mental Model

Slow call rate answers:

```text
How many requests became too slow?
```

---

# 13. Slow Call Definition

A slow call means:

```text
request duration exceeded configured threshold
```

Example:

```text
threshold = 2 seconds
```

Request latency:

```text
5 seconds
```

Counts as:

```text
slow call
```

---

# 14. Slow Call Formula

```text
Slow Call Rate =
(Slow Calls / Total Calls) × 100
```

---

# 15. Slow Call Example

Metrics:

```text
Total Calls = 100
Slow Calls = 60
```

Calculation:

```text
Slow Call Rate =
(60 / 100) × 100 = 60%
```

---

# 16. Slow Threshold Example

Threshold:

```text
50%
```

Current:

```text
60%
```

Result:

```text
OPEN
```

---

# 17. Why Slow Calls Matter More Than Failures

Fully dead service:

```text
fails fast
```

Threads released quickly.

Slow service:

```text
holds threads for long time
```

Eventually:

```text
thread pool exhaustion
```

---

# 18. Resource Exhaustion Flow

```text
Slow Dependency
      ↓
Threads Blocked
      ↓
Queue Growth
      ↓
Timeouts
      ↓
Retries
      ↓
Cascading Failure
```

---

# 19. Cascading Failure Mental Model

One slow service can damage:

```text
gateway
Kafka consumers
DB pools
microservice chains
```

---

# 20. Latency Amplification

Suppose service chain:

```text
Gateway
  ↓
Auth
  ↓
Payment
  ↓
Inventory
```

Payment becomes slow.

Entire chain latency increases.

---

# 21. Latency Amplification ASCII

```text
Payment Slow
     ↓
Gateway Threads Block
     ↓
More Requests Queue
     ↓
More Timeouts
     ↓
System Collapse
```

---

# 22. Failure Rate Detects

Failure rate detects:

```text
dead services
unreachable services
hard failures
```

---

# 23. Slow Call Rate Detects

Slow call rate detects:

```text
degrading services
resource pressure
early latency issues
```

before full outage.

---

# 24. Early Warning System

Slow-call rate acts like:

```text
early warning signal
```

before:

```text
timeouts
connection failures
complete outage
```

---

# 25. Real Production Failure Pattern

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
complete outage
```

Slow-call monitoring catches problems earlier.

---

# 26. Combined Health Evaluation

Modern circuit breakers evaluate:

```text
failure rate
OR
slow call rate
```

---

# 27. Combined Logic ASCII

```text
Failure Rate High?
        ↓
       OPEN

OR

Slow Call Rate High?
        ↓
       OPEN
```

---

# 28. Example — Failures Healthy, Slow Calls Dangerous

Metrics:

```text
failure rate = 5%
slow call rate = 80%
```

Result:

```text
OPEN
```

because service dangerously slow.

---

# 29. Example — Slow Healthy, Failures Dangerous

Metrics:

```text
failure rate = 70%
slow call rate = 5%
```

Result:

```text
OPEN
```

because failures too high.

---

# 30. Example — Both Healthy

Metrics:

```text
failure rate = 5%
slow call rate = 10%
```

Thresholds:

```text
50%
```

Result:

```text
CLOSED
```

---

# 31. Example — Both Dangerous

Metrics:

```text
failure rate = 80%
slow call rate = 90%
```

Result:

```text
OPEN immediately
```

---

# 32. Sliding Window Dependency

Both metrics use:

```text
sliding windows
```

Examples:

```text
last 100 calls
last 60 seconds
```

Only recent traffic matters.

---

# 33. Why Recent Metrics Important

Old failures should expire naturally.

If service recovered:

```text
health should improve automatically
```

Sliding windows provide this behavior.

---

# 34. Minimum Calls Problem

Without minimum calls:

```text
1 slow request = 100% slow rate
```

Circuit opens too aggressively.

Need:

```text
minimumNumberOfCalls
```

---

# 35. Minimum Calls Example

Config:

```text
minimum calls = 20
```

If only:

```text
5 calls completed
```

Circuit ignores threshold calculation.

---

# 36. Slow Call Duration Threshold

Need exact definition of:

```text
what is "slow"?
```

Example:

```text
slowCallDurationThreshold = 2 seconds
```

---

# 37. Slow Call Example

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
counted as slow call
```

---

# 38. Why Threshold Tuning Difficult

Thresholds too strict:

```text
false OPEN events
```

Thresholds too relaxed:

```text
system damage before protection
```

Need balance.

---

# 39. Common Production Thresholds

Typical configs:

```text
failure threshold = 50%
slow threshold = 50%
slow duration = 2s
minimum calls = 20
```

---

# 40. External API Strategy

External APIs unstable naturally.

Often tolerate:

```text
more latency
more retries
higher failure percentage
```

---

# 41. Payment System Strategy

Payments sensitive.

Need:

```text
strict latency
short timeout
faster OPEN transition
```

---

# 42. Kafka Consumer Strategy

Kafka consumers sensitive to:

```text
processing delays
```

Slow APIs can cause:

```text
consumer lag growth
```

even without failures.

---

# 43. Internal Metrics Structure

Circuit breaker tracks:

```text
totalCalls
failedCalls
slowCalls
successfulCalls
```

---

# 44. Internal Metrics ASCII

```text
Metrics
 ├── totalCalls
 ├── failedCalls
 ├── slowCalls
 └── successfulCalls
```

---

# 45. Health Evaluation Flow

```text
Request Completes
       ↓
Measure Duration
       ↓
Success or Failure?
       ↓
Slow or Fast?
       ↓
Update Sliding Window
       ↓
Calculate Rates
       ↓
Compare Thresholds
```

---

# 46. Java Metrics Model

```java
public class Metrics {

    private int totalCalls;

    private int failedCalls;

    private int slowCalls;

    private int successfulCalls;
}
```

---

# 47. Java Record Success

```java
public void recordSuccess() {

    totalCalls++;

    successfulCalls++;
}
```

---

# 48. Java Record Failure

```java
public void recordFailure() {

    totalCalls++;

    failedCalls++;
}
```

---

# 49. Java Record Slow Call

```java
public void recordSlowCall() {

    totalCalls++;

    slowCalls++;
}
```

---

# 50. Java Failure Rate

```java
public double failureRate() {

    if (totalCalls == 0) {

        return 0.0;
    }

    return (failedCalls * 100.0)
            / totalCalls;
}
```

---

# 51. Java Slow Call Rate

```java
public double slowCallRate() {

    if (totalCalls == 0) {

        return 0.0;
    }

    return (slowCalls * 100.0)
            / totalCalls;
}
```

---

# 52. Java Threshold Evaluation

```java
public boolean shouldOpen() {

    if (totalCalls < 20) {

        return false;
    }

    return failureRate() >= 50
            || slowCallRate() >= 50;
}
```

---

# 53. Java Duration Check

```java
public void recordCall(long durationMs) {

    totalCalls++;

    if (durationMs > 2000) {

        slowCalls++;
    }
}
```

---

# 54. Java Dry Run — Healthy Service

Metrics:

```text
totalCalls = 100
failedCalls = 5
slowCalls = 10
```

Rates:

```text
failure rate = 5%
slow rate = 10%
```

Result:

```text
CLOSED
```

---

# 55. Java Dry Run — Slow Service

Metrics:

```text
totalCalls = 100
failedCalls = 5
slowCalls = 80
```

Rates:

```text
failure rate = 5%
slow rate = 80%
```

Result:

```text
OPEN
```

---

# 56. Java Dry Run — Failing Service

Metrics:

```text
totalCalls = 100
failedCalls = 70
slowCalls = 20
```

Rates:

```text
failure rate = 70%
slow rate = 20%
```

Result:

```text
OPEN
```

---

# 57. Spring Boot Slow Call Example

Slow REST call:

```text
RestTemplate
WebClient
FeignClient
```

can occupy:

```text
Tomcat threads
Netty threads
connection pools
```

for long duration.

---

# 58. Database Slow Query Example

Slow SQL query:

```text
SELECT ... JOIN ...
```

can block:

```text
DB connections
transactions
request threads
```

even if query succeeds.

---

# 59. Retry Storm Interaction

Slow services trigger:

```text
timeouts
```

which trigger:

```text
retries
```

which create:

```text
more traffic
```

Circuit breaker interrupts this loop.

---

# 60. Retry Storm ASCII

```text
Slow Service
      ↓
Timeouts
      ↓
Retries
      ↓
More Load
      ↓
Even Slower
```

---

# 61. Important Production Metrics

Production dashboards monitor:

```text
failure rate
slow-call rate
p95 latency
p99 latency
OPEN count
fallback count
```

---

# 62. Why p99 Important

Average latency may look healthy.

But:

```text
p99 spikes
```

often indicate:

```text
slow-call explosion
```

before outage.

---

# 63. Resilience4j Config Example

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        slidingWindowType: TIME_BASED
        slidingWindowSize: 60
        minimumNumberOfCalls: 20
        failureRateThreshold: 50
        slowCallRateThreshold: 50
        slowCallDurationThreshold: 2s
```

---

# 64. Config Meaning

```text
Track last 60 seconds
Need minimum 20 calls
Open if 50% failures
Open if 50% slow calls
Call slower than 2 seconds = slow
```

---

# 65. Common Mistakes

## Mistake 1

```text
Tracking only failures
```

Misses latency degradation.

---

## Mistake 2

```text
Ignoring slow services
```

System dies gradually.

---

## Mistake 3

```text
Very strict thresholds
```

Too many false OPEN events.

---

## Mistake 4

```text
Very relaxed thresholds
```

Protection too late.

---

## Mistake 5

```text
No minimum calls
```

False positives.

---

# 66. Most Important Insight

```text
Slow-call rate is often more valuable than failure rate.
```

because systems usually degrade gradually before dying completely.

---

# 67. Distributed Systems Insight

Modern resiliency engineering focuses heavily on:

```text
latency management
```

NOT just:

```text
exception handling
```

Latency is usually first symptom of failure.

---

# 68. Interview Explanation

If interviewer asks:

```text
Why track slow-call rate?
```

Strong answer:

```text
Slow services can exhaust threads, queues, and connection pools before
they completely fail. Slow-call rate acts as an early warning signal for
latency degradation, allowing the circuit breaker to protect the system
before full outages occur.
```

Senior addition:

```text
Modern distributed systems often fail gradually through latency
amplification rather than immediate crashes, making slow-call detection
critical for resiliency engineering.
```

---

# 69. Final Mental Model

```text
Failures
    +
Latency Degradation
    ↓
Overall Health Signal
```

---

# 70. What To Remember

```text
Failure rate measures failures.

Slow-call rate measures latency degradation.

Slow services are often more dangerous than dead services.

Circuit breakers evaluate both metrics together.

Sliding windows track recent health.

Minimum calls prevent false positives.

Slow-call thresholds provide early warning.

Latency amplification causes cascading failures.

Modern resiliency engineering focuses heavily on latency.
```

---

# 71. Next File

```text
011_Bulkhead_Pattern.md
```

Next you learn:

```text
thread pool isolation
resource isolation
bulkhead compartments
preventing shared resource exhaustion
Bulkhead vs Circuit Breaker
Resilience4j bulkheads
```
