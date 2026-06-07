# 003_CLOSED_State.md

# MiniCircuitBreaker — 003 CLOSED State

---

# 1. Why This File Exists

A circuit breaker has three main states:

```text
CLOSED
OPEN
HALF_OPEN
```

The first and normal state is:

```text
CLOSED
```

This file explains:

```text
what CLOSED state means
how requests flow normally
how success/failure is recorded
how failure counters update
how sliding window begins
when CLOSED becomes OPEN
how this maps to Resilience4j
```

CLOSED state is important because:

```text
this is where the system behaves normally
but quietly watches for danger
```

---

# 2. One-Line Definition

```text
CLOSED state means the circuit breaker allows requests to pass through normally.
```

---

# 3. Biggest Mental Model

CLOSED means:

```text
service considered healthy
```

So:

```text
requests are allowed
```

But the circuit breaker still monitors:

```text
successes
failures
slow calls
timeouts
exceptions
```

---

# 4. CLOSED State ASCII

```text
Client Request
      ↓
Circuit Breaker CLOSED
      ↓
Request Allowed
      ↓
Remote Service Call
      ↓
Record Result
```

---

# 5. Electrical Analogy

In electrical systems:

```text
closed circuit = current flows
```

In software:

```text
CLOSED circuit = requests flow
```

So CLOSED does NOT mean blocked.

It means:

```text
traffic is flowing normally
```

---

# 6. CLOSED vs OPEN

```text
CLOSED:
requests allowed

OPEN:
requests blocked
```

---

# 7. CLOSED Flow

Normal request flow:

```text
check breaker state
      ↓
state is CLOSED
      ↓
allow remote call
      ↓
remote call succeeds/fails
      ↓
record metrics
      ↓
maybe stay CLOSED or move OPEN
```

---

# 8. CLOSED State Responsibilities

In CLOSED state, circuit breaker must:

```text
allow requests
record successes
record failures
record slow calls
track sliding window
calculate failure rate
decide whether to OPEN
```

---

# 9. Why Monitoring Happens In CLOSED

CLOSED does not mean:

```text
blind trust
```

It means:

```text
allow traffic but observe health
```

The breaker is always watching.

---

# 10. CLOSED Monitoring ASCII

```text
Allowed Request
      ↓
Success? Failure? Slow?
      ↓
Update Metrics
      ↓
Check Threshold
```

---

# 11. Successful Call In CLOSED

If remote call succeeds:

```text
record success
keep circuit CLOSED
```

Example:

```text
Payment API returns 200 OK
```

Circuit remains healthy.

---

# 12. Success Flow ASCII

```text
Request
  ↓
CLOSED
  ↓
Remote Call Success
  ↓
Record Success
  ↓
Stay CLOSED
```

---

# 13. Failed Call In CLOSED

If remote call fails:

```text
record failure
update failure rate
check threshold
```

If threshold not reached:

```text
stay CLOSED
```

If threshold reached:

```text
move to OPEN
```

---

# 14. Failure Flow ASCII

```text
Request
  ↓
CLOSED
  ↓
Remote Call Fails
  ↓
Record Failure
  ↓
Failure Rate Too High?
       ↓
    yes/no
```

---

# 15. Slow Call In CLOSED

Modern circuit breakers also track:

```text
slow calls
```

A call may technically succeed but still be dangerous.

Example:

```text
HTTP 200 OK after 10 seconds
```

This can still exhaust threads.

---

# 16. Slow Call Mental Model

```text
failure is not only exception
slow response is also dangerous
```

---

# 17. Slow Call ASCII

```text
Remote Call
   ↓
Returns Success
   ↓
But Took Too Long
   ↓
Record Slow Call
```

---

# 18. Timeout In CLOSED

If call exceeds timeout:

```text
timeout recorded as failure
```

Timeout protects the thread from waiting forever.

---

# 19. Timeout ASCII

```text
Remote Call
   ↓
No Response In Time
   ↓
Timeout
   ↓
Record Failure
```

---

# 20. Sliding Window Begins In CLOSED

Circuit breaker does not usually store all history.

It stores recent calls using:

```text
sliding window
```

Example:

```text
last 100 calls
```

or:

```text
last 60 seconds
```

---

# 21. Why Sliding Window

All-time history is bad.

Example:

```text
service failed yesterday
but healthy now
```

Should not keep punishing it forever.

Sliding window focuses on:

```text
recent health
```

---

# 22. Sliding Window ASCII

```text
Recent Calls:
[S, S, F, S, F, F, S]

S = success
F = failure
```

Circuit calculates health from recent calls only.

---

# 23. Count-Based Window

Example:

```text
last 10 calls
```

Track:

```text
success/failure among last 10
```

---

# 24. Time-Based Window

Example:

```text
last 60 seconds
```

Track:

```text
success/failure during last 60 seconds
```

---

# 25. Failure Rate Formula

```text
failure rate =
failed calls / total calls
```

Example:

```text
5 failures out of 10 calls
```

Failure rate:

```text
50%
```

---

# 26. Failure Rate ASCII

```text
Total Calls: 10
Failures:    5

Failure Rate:
5 / 10 = 50%
```

---

# 27. When CLOSED Transitions To OPEN

CLOSED becomes OPEN when:

```text
minimum calls reached
+
failure rate threshold exceeded
```

Example:

```text
minimum calls = 10
failure threshold = 50%
```

If:

```text
10 calls completed
6 failed
```

Then:

```text
failure rate = 60%
```

Circuit opens.

---

# 28. Threshold Transition ASCII

```text
CLOSED
  ↓
Record Calls
  ↓
Failure Rate >= Threshold
  ↓
OPEN
```

---

# 29. Why Minimum Calls Needed

Without minimum calls:

```text
1 failure out of 1 call = 100%
```

Circuit would open too early.

Minimum calls prevent false alarms.

---

# 30. Minimum Calls Example

Bad config:

```text
minimum calls = 1
threshold = 50%
```

One failure:

```text
100%
```

Circuit opens too aggressively.

Better:

```text
minimum calls = 20
```

---

# 31. CLOSED State In Resilience4j

In Resilience4j, CLOSED state:

```text
permits all calls
records metrics
uses sliding window
checks thresholds
```

Then transitions to OPEN if threshold crossed.

---

# 32. Resilience4j Config Example

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        slowCallRateThreshold: 50
        slowCallDurationThreshold: 2s
```

---

# 33. Config Meaning

```text
slidingWindowSize = track last 10 calls
minimumNumberOfCalls = need at least 5 calls before deciding
failureRateThreshold = open if 50% failed
slowCallRateThreshold = open if 50% slow
slowCallDurationThreshold = call slower than 2s is slow
```

---

# 34. CLOSED Dry Run — Healthy Service

Config:

```text
window size = 5
minimum calls = 5
failure threshold = 50%
```

Calls:

```text
S S S S S
```

Failure rate:

```text
0 / 5 = 0%
```

Result:

```text
stay CLOSED
```

---

# 35. CLOSED Dry Run — Some Failures

Calls:

```text
S S F S F
```

Failures:

```text
2
```

Total:

```text
5
```

Failure rate:

```text
2 / 5 = 40%
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

# 36. CLOSED Dry Run — Threshold Crossed

Calls:

```text
S F F F S
```

Failures:

```text
3
```

Total:

```text
5
```

Failure rate:

```text
3 / 5 = 60%
```

Threshold:

```text
50%
```

Result:

```text
transition to OPEN
```

---

# 37. CLOSED State Internal Data

CLOSED state needs:

```text
current state
sliding window
success count
failure count
slow call count
minimum call config
threshold config
```

---

# 38. Internal Data ASCII

```text
CircuitBreaker
 ├── state = CLOSED
 ├── slidingWindow
 ├── successCount
 ├── failureCount
 ├── slowCallCount
 └── thresholds
```

---

# 39. Java Enum For State

```java
public enum CircuitBreakerState {

    CLOSED,
    OPEN,
    HALF_OPEN
}
```

---

# 40. Java Call Result Model

```java
public enum CallResult {

    SUCCESS,
    FAILURE,
    SLOW_SUCCESS,
    SLOW_FAILURE
}
```

---

# 41. Java Simple Metrics

```java
public class CircuitMetrics {

    private int totalCalls;
    private int failureCalls;

    public void recordSuccess() {
        totalCalls++;
    }

    public void recordFailure() {
        totalCalls++;
        failureCalls++;
    }

    public int getTotalCalls() {
        return totalCalls;
    }

    public double failureRate() {

        if (totalCalls == 0) {
            return 0.0;
        }

        return (failureCalls * 100.0)
                / totalCalls;
    }
}
```

---

# 42. Java CLOSED State Logic

```java
public class ClosedStateHandler {

    private final CircuitMetrics metrics =
            new CircuitMetrics();

    private final int minimumCalls = 5;

    private final double failureThreshold =
            50.0;

    public CircuitBreakerState recordSuccess() {

        metrics.recordSuccess();

        return CircuitBreakerState.CLOSED;
    }

    public CircuitBreakerState recordFailure() {

        metrics.recordFailure();

        if (metrics.getTotalCalls()
                < minimumCalls) {

            return CircuitBreakerState.CLOSED;
        }

        if (metrics.failureRate()
                >= failureThreshold) {

            return CircuitBreakerState.OPEN;
        }

        return CircuitBreakerState.CLOSED;
    }
}
```

---

# 43. Java Dry Run

Config:

```text
minimumCalls = 5
failureThreshold = 50%
```

Calls:

```text
S F F F S
```

Execution:

```text
call1 success → total=1 failures=0
call2 failure → total=2 failures=1
call3 failure → total=3 failures=2
call4 failure → total=4 failures=3
call5 success → total=5 failures=3
```

Failure rate:

```text
3 / 5 = 60%
```

Result:

```text
OPEN
```

---

# 44. Why CLOSED State Must Be Fast

Every request passes through CLOSED state.

So CLOSED logic must be:

```text
very cheap
low latency
thread-safe
non-blocking
```

---

# 45. Performance Rule

Circuit breaker check should be:

```text
microseconds
```

not:

```text
milliseconds
```

Otherwise it becomes bottleneck.

---

# 46. Thread Safety In CLOSED

Multiple threads record results concurrently.

Need safe updates using:

```text
AtomicInteger
LongAdder
AtomicReference
synchronized blocks
or lock-free structures
```

---

# 47. Common Mistakes

## Mistake 1

```text
Thinking CLOSED means circuit disabled
```

Wrong.

CLOSED means:

```text
traffic allowed but monitored
```

---

## Mistake 2

```text
Opening after one failure
```

Need minimum calls.

---

## Mistake 3

```text
Ignoring slow calls
```

Slow calls can kill systems.

---

## Mistake 4

```text
Using all-time failure history
```

Need sliding window.

---

## Mistake 5

```text
Making CLOSED state logic slow
```

Every request pays this cost.

---

# 48. Interview Explanation

If interviewer asks:

```text
What happens in CLOSED state?
```

Strong answer:

```text
In CLOSED state, the circuit breaker allows all requests to pass through
while recording successes, failures, timeouts, and slow calls. It uses
a sliding window to calculate failure rate and transitions to OPEN if
configured thresholds are exceeded.
```

Senior addition:

```text
CLOSED state must be thread-safe and extremely lightweight because it
runs on every protected call.
```

---

# 49. Final Mental Model

```text
CLOSED =
traffic allowed
+
health monitored
+
threshold checked
```

---

# 50. What To Remember

```text
CLOSED is normal healthy state.

Requests are allowed.

Success/failure metrics are recorded.

Sliding window tracks recent calls.

Failure rate threshold decides transition.

Slow calls can also trigger opening.

Minimum call count prevents false opening.

CLOSED must be thread-safe and fast.
```

---

# 51. Next File

```text
004_OPEN_State.md
```

Next you learn:

```text
why requests are rejected
fast failure
wait duration
cooldown period
fallback response
transition from OPEN to HALF_OPEN
```
