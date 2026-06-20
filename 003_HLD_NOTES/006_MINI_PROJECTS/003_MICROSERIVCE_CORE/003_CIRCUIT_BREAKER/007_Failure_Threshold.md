# 007_Failure_Threshold.md

# MiniCircuitBreaker — 007 Failure Threshold

---

# 1. Why This File Exists

Circuit breaker transitions:

```text
CLOSED → OPEN
```

when service health becomes dangerous.

But important question:

```text
How does circuit breaker decide service unhealthy?
```

Need:

```text
failure threshold logic
```

This file explains:

```text
failure percentage
minimum calls
threshold calculation
slow call thresholds
count-based windows
time-based windows
production tuning
false positives
Resilience4j internals
```

Failure thresholds are the MOST important signal used by circuit breakers.

---

# 2. One-Line Definition

```text
Failure threshold defines when the circuit breaker should OPEN.
```

---

# 3. Biggest Mental Model

```text
Recent Requests
        ↓
Calculate Failure Rate
        ↓
Threshold Exceeded?
       yes/no
```

---

# 4. Why Threshold Needed

Single failures happen normally.

Example:

```text
temporary network issue
random timeout
small DB hiccup
```

Circuit breaker should NOT open immediately.

Need:

```text
statistical health detection
```

---

# 5. Threshold Mental Model

```text
few failures = normal
too many failures = dangerous
```

---

# 6. Failure Rate Formula

Failure rate:

```text
Failure Rate = (Failed Calls / Total Calls) × 100
```

---

# 7. Example Calculation

Suppose:

```text
Total Calls = 10
Failed Calls = 6
```

Failure rate:

```text
Failure Rate = (6 / 10) × 100 = 60%
```

---

# 8. Threshold Example

Suppose threshold:

```text
50%
```

Current failure rate:

```text
60%
```

Result:

```text
OPEN
```

---

# 9. Threshold Decision ASCII

```text
Failure Rate = 60%
Threshold    = 50%

60% > 50%
    ↓
OPEN
```

---

# 10. Why Percentage Better Than Fixed Count

Bad idea:

```text
open after 5 failures
```

Problem:

```text
5 failures out of 10 = dangerous
5 failures out of 1 million = normal
```

Percentage-based thresholds scale better.

---

# 11. Sliding Window Concept

Failure rate calculated over:

```text
recent calls only
```

using:

```text
sliding window
```

---

# 12. Why Sliding Window Important

All-time history bad.

Example:

```text
service failed yesterday
healthy today
```

Should not remain punished forever.

Need:

```text
recent health only
```

---

# 13. Sliding Window ASCII

```text
Recent Calls:
[S F S F F S S]

S = success
F = failure
```

Only recent calls matter.

---

# 14. Count-Based Window

Example:

```text
last 100 calls
```

Threshold calculated over:

```text
last 100 requests only
```

---

# 15. Time-Based Window

Example:

```text
last 60 seconds
```

Threshold calculated over:

```text
recent time period
```

---

# 16. Count-Based vs Time-Based

## Count-Based

```text
fixed number of requests
```

Stable traffic systems.

---

## Time-Based

```text
fixed time duration
```

Variable traffic systems.

---

# 17. Why Minimum Calls Needed

Without minimum calls:

```text
1 failure out of 1 request = 100%
```

Circuit opens too aggressively.

Need:

```text
minimumNumberOfCalls
```

before threshold calculation.

---

# 18. Minimum Calls Example

Config:

```text
minimum calls = 10
threshold = 50%
```

If only:

```text
3 requests completed
```

Circuit breaker ignores threshold calculation.

---

# 19. Minimum Calls ASCII

```text
Calls < Minimum Calls
        ↓
Do Not Evaluate Threshold
```

---

# 20. False Positive Problem

Without minimum calls:

```text
random failures trigger OPEN
```

Very unstable behavior.

---

# 21. False Positive ASCII

```text
One Failure
    ↓
100% Failure Rate
    ↓
OPEN
```

Bad configuration.

---

# 22. Better Configuration

Example:

```text
minimum calls = 20
failure threshold = 50%
```

More stable statistical behavior.

---

# 23. Slow Call Threshold

Modern breakers detect:

```text
slow calls
```

NOT only failures.

Because:

```text
slow services can kill systems
```

---

# 24. Slow Call Mental Model

```text
Slow response
=
resource exhaustion risk
```

Even if technically successful.

---

# 25. Slow Call Example

Suppose:

```text
response time = 10 seconds
```

But threshold:

```text
2 seconds
```

Counts as:

```text
slow call
```

---

# 26. Slow Call Threshold Formula

```text
Slow Call Rate = (Slow Calls / Total Calls) × 100
```

---

# 27. Slow Call Example

Suppose:

```text
Total Calls = 10
Slow Calls = 7
```

Slow call rate:

```text
Slow Call Rate = (7 / 10) × 100 = 70%
```

---

# 28. Slow Threshold Decision

Config:

```text
slow call threshold = 50%
```

Current:

```text
70%
```

Result:

```text
OPEN
```

---

# 29. Why Slow Calls Dangerous

Slow calls cause:

```text
thread blocking
queue growth
connection exhaustion
latency amplification
```

Sometimes worse than failures.

---

# 30. Production Reality

Fully dead services:

```text
fail fast
```

Slow services:

```text
silently consume resources
```

Often MORE dangerous.

---

# 31. Combined Threshold Logic

Circuit breaker may open when:

```text
failure rate exceeded
OR
slow call rate exceeded
```

---

# 32. Combined Logic ASCII

```text
Failure Rate Too High?
        ↓
       OPEN

OR

Slow Call Rate Too High?
        ↓
       OPEN
```

---

# 33. Threshold Tuning Problem

Thresholds too aggressive:

```text
false openings
```

Thresholds too relaxed:

```text
system damage before OPEN
```

Need balance.

---

# 34. Aggressive Threshold Example

Bad config:

```text
minimum calls = 2
threshold = 20%
```

Very unstable.

---

# 35. Relaxed Threshold Example

Bad config:

```text
minimum calls = 1000
threshold = 95%
```

Circuit opens too late.

---

# 36. Production Threshold Strategy

Common configs:

```text
minimum calls = 20–100
failure threshold = 50–70%
slow threshold = 50–70%
```

Depends on system sensitivity.

---

# 37. External API Strategy

External APIs often unstable.

Use:

```text
higher threshold
more retries
longer timeout
```

---

# 38. Payment System Strategy

Payment systems sensitive.

Use:

```text
lower latency threshold
strict timeout
careful retries
```

---

# 39. Threshold Evaluation Flow

```text
Request Completes
       ↓
Record Success/Failure
       ↓
Update Sliding Window
       ↓
Calculate Rates
       ↓
Compare Thresholds
       ↓
OPEN?
```

---

# 40. Internal Metrics Data

Circuit breaker tracks:

```text
total calls
failed calls
slow calls
success calls
```

---

# 41. Internal Metrics ASCII

```text
Metrics
 ├── totalCalls
 ├── failedCalls
 ├── slowCalls
 └── successCalls
```

---

# 42. Java Metrics Model

```java
public class CircuitMetrics {

    private int totalCalls;

    private int failedCalls;

    private int slowCalls;

    public void recordSuccess() {

        totalCalls++;
    }

    public void recordFailure() {

        totalCalls++;
        failedCalls++;
    }

    public void recordSlowCall() {

        totalCalls++;
        slowCalls++;
    }
}
```

---

# 43. Java Failure Rate Calculation

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

# 44. Java Slow Call Rate

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

# 45. Java Threshold Logic

```java
public boolean shouldOpen() {

    if (totalCalls < 10) {
        return false;
    }

    return failureRate() >= 50
            || slowCallRate() >= 50;
}
```

---

# 46. Dry Run — Healthy System

Calls:

```text
S S S F S S S S
```

Failure rate:

```text
1 / 8 = 12.5%
```

Threshold:

```text
50%
```

Result:

```text
stay CLOSED
```

---

# 47. Dry Run — Unhealthy System

Calls:

```text
F F F S F F S F
```

Failures:

```text
6
```

Failure rate:

```text
6 / 8 = 75%
```

Result:

```text
OPEN
```

---

# 48. Dry Run — Slow Call Trigger

Calls:

```text
slow slow slow success slow
```

Slow calls:

```text
4 / 5 = 80%
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

# 49. Resilience4j Config Example

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 100
        minimumNumberOfCalls: 20
        failureRateThreshold: 50
        slowCallRateThreshold: 50
        slowCallDurationThreshold: 2s
```

---

# 50. Config Meaning

```text
Track last 100 calls
Need minimum 20 calls
Open if 50% failures
Open if 50% slow calls
Slow call means > 2 seconds
```

---

# 51. Time-Based Window Example

```yaml
slidingWindowType: TIME_BASED
slidingWindowSize: 60
```

Meaning:

```text
track calls during last 60 seconds
```

---

# 52. Why Production Tuning Difficult

Traffic patterns vary:

```text
low traffic
burst traffic
spiky traffic
night/day traffic
```

Threshold tuning requires real production metrics.

---

# 53. Important Production Metrics

Monitor:

```text
failure rate
slow call rate
OPEN frequency
latency percentiles
rejected calls
```

---

# 54. Observability

Production dashboards:

```text
p95 latency
p99 latency
failure spikes
slow call spikes
OPEN transitions
```

using:

```text
Prometheus
Grafana
Micrometer
```

---

# 55. Common Mistakes

## Mistake 1

```text
No minimum calls
```

Causes false positives.

---

## Mistake 2

```text
Threshold too aggressive
```

Frequent unnecessary OPEN states.

---

## Mistake 3

```text
Ignoring slow calls
```

Slow services kill systems.

---

## Mistake 4

```text
Using all-time history
```

Need sliding window.

---

## Mistake 5

```text
Static tuning without metrics
```

Production tuning requires observation.

---

# 56. Most Important Insight

```text
Failure thresholds are statistical health signals.
```

NOT:

```text
single-event reactions
```

---

# 57. Distributed Systems Insight

At scale:

```text
latency degradation often appears
before complete failure
```

Slow call thresholds provide:

```text
early warning system
```

---

# 58. Interview Explanation

If interviewer asks:

```text
How does a circuit breaker decide to OPEN?
```

Strong answer:

```text
Circuit breakers use sliding windows to calculate failure rate and slow
call rate over recent requests. If configured thresholds are exceeded
after a minimum number of calls, the circuit transitions from CLOSED to
OPEN.
```

Senior addition:

```text
Modern systems also monitor slow-call percentage because latency
degradation can exhaust resources before complete failure occurs.
```

---

# 59. Final Mental Model

```text
Recent Calls
      ↓
Health Calculation
      ↓
Threshold Decision
      ↓
OPEN or CLOSED
```

---

# 60. What To Remember

```text
Failure thresholds determine OPEN transition.

Failure rate uses percentage, not fixed count.

Sliding windows track recent health only.

Minimum calls prevent false positives.

Slow call thresholds are critical.

Slow services often more dangerous than dead services.

Threshold tuning requires balance.

Thresholds are statistical health signals.
```

---

# 61. Next File

```text
008_Sliding_Window_Count_Based.md
```

Next you learn:

```text
count-based sliding window internals
ring buffers
bucket rotation
O(1) metrics updates
efficient window calculation
production performance optimization
```
