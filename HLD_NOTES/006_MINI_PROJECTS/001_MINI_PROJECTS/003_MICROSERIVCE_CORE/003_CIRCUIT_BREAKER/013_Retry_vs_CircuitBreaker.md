# 013_Retry_vs_CircuitBreaker.md

# MiniCircuitBreaker — 013 Retry vs Circuit Breaker

---

# 1. Why This File Exists

Previous files explained:

```text
timeout
slow calls
exception classification
failure rate
sliding windows
```

Now we need to understand a very common confusion:

```text
Retry vs Circuit Breaker
```

Many developers think:

```text
retry solves failure
```

But retry can also:

```text
destroy the system
```

Circuit breaker and retry are both resiliency tools, but they solve opposite problems.

This file explains:

```text
retry pattern
circuit breaker pattern
when retry helps
when retry hurts
retry storm
circuit breaker as safety stop
timeout + retry + CB interaction
exception classification
idempotency
Spring Boot / Resilience4j config
Java implementation
production tradeoffs
interview explanation
```

---

# 2. One-Line Definition

## Retry

```text
Retry tries the failed operation again.
```

## Circuit Breaker

```text
Circuit breaker stops calling an unhealthy dependency.
```

---

# 3. Biggest Mental Model

```text
Retry =
Maybe it will work next time

Circuit Breaker =
Stop trying, dependency is unhealthy
```

---

# 4. Retry Mental Model

Retry assumes:

```text
failure is temporary
```

Example:

```text
network glitch
temporary timeout
short DB deadlock
momentary 503
```

Retry may recover.

---

# 5. Circuit Breaker Mental Model

Circuit breaker assumes:

```text
dependency is unhealthy
```

So it stops calls temporarily.

---

# 6. Retry vs Circuit Breaker ASCII

```text
Retry:
Failure → Try Again

Circuit Breaker:
Too Many Failures → Stop Calling
```

---

# 7. Why Retry Exists

Retries are useful because distributed systems have:

```text
temporary failures
packet loss
short network blips
leader election delay
temporary overload
```

A second attempt may succeed.

---

# 8. Retry Success Example

```text
Attempt 1 → timeout
Attempt 2 → success
```

User gets success.

This is good retry.

---

# 9. Why Circuit Breaker Exists

Circuit breaker exists because repeated retry against bad dependency creates:

```text
retry storm
thread exhaustion
queue explosion
cascading failure
```

---

# 10. Retry Storm

Retry storm happens when many clients retry at the same time.

Example:

```text
1000 requests fail
each retries 3 times
```

Total attempts:

```text
4000
```

The unhealthy service receives even more traffic.

---

# 11. Retry Storm ASCII

```text
Dependency Slow
      ↓
Requests Timeout
      ↓
Clients Retry
      ↓
More Traffic
      ↓
Dependency Slower
      ↓
More Timeouts
      ↓
Collapse
```

---

# 12. Retry Helps When

Retry helps when failure is:

```text
transient
short-lived
idempotent
low volume
backoff enabled
jitter enabled
```

---

# 13. Retry Hurts When

Retry hurts when failure is:

```text
permanent
high volume
dependency overloaded
operation non-idempotent
no backoff
no jitter
```

---

# 14. Circuit Breaker Helps When

Circuit breaker helps when:

```text
failure rate high
slow-call rate high
dependency overloaded
timeouts increasing
retry storm starting
```

---

# 15. Retry and Circuit Breaker Are Not Enemies

They should work together.

Correct mental model:

```text
Retry handles small temporary failures.
Circuit breaker stops large ongoing failures.
```

---

# 16. Combined Flow

```text
Request
   ↓
Timeout
   ↓
Retry small number of times
   ↓
Still failing?
   ↓
Circuit Breaker records failure
   ↓
Failure rate high?
   ↓
OPEN
```

---

# 17. Better Production Flow

```text
Request
   ↓
Bulkhead
   ↓
Timeout
   ↓
Retry With Backoff
   ↓
Circuit Breaker
   ↓
Fallback
```

Note:

```text
actual ordering can differ by framework,
but the mental model is layered protection
```

---

# 18. Retry Count

Bad:

```text
retry forever
```

Good:

```text
2 or 3 attempts
```

---

# 19. Retry Count Example

```text
maxAttempts = 3
```

Means:

```text
initial call + 2 retries
```

depending on library semantics.

Always check framework behavior.

---

# 20. Immediate Retry Problem

Bad:

```text
retry immediately
```

If all clients retry immediately:

```text
traffic spike becomes worse
```

---

# 21. Exponential Backoff

Better:

```text
wait longer after each failure
```

Example:

```text
100ms
200ms
400ms
800ms
```

---

# 22. Jitter

Even with backoff, clients may synchronize.

Jitter adds randomness:

```text
retry after random delay
```

This prevents:

```text
thundering herd
```

---

# 23. Backoff + Jitter ASCII

Without jitter:

```text
|||||||||||||||||||||
```

With jitter:

```text
|  || |   | ||   | |
```

---

# 24. Circuit Breaker As Retry Stopper

When circuit is OPEN:

```text
calls are blocked
```

So retry should not keep calling the dependency.

---

# 25. OPEN State Retry Behavior

If circuit breaker throws:

```text
CallNotPermittedException
```

Do NOT retry that as normal dependency failure.

It means:

```text
circuit is intentionally protecting system
```

---

# 26. Exception Classification Matters

Retry only:

```text
retryable exceptions
```

Do not retry:

```text
business exceptions
validation errors
bad requests
CallNotPermittedException
```

---

# 27. Retryable Examples

```text
SocketTimeoutException
ConnectException
HTTP 503
HTTP 502
HTTP 504
temporary SQL deadlock
```

---

# 28. Non-Retryable Examples

```text
HTTP 400
HTTP 401
HTTP 403
HTTP 404
ValidationException
InvalidCouponException
IllegalArgumentException
CallNotPermittedException
```

---

# 29. HTTP Retry Matrix

```text
HTTP 400 → no retry
HTTP 401 → no retry
HTTP 403 → no retry
HTTP 404 → usually no retry
HTTP 409 → maybe retry depending on operation
HTTP 429 → retry with backoff and respect Retry-After
HTTP 500 → maybe retry
HTTP 502 → retry
HTTP 503 → retry
HTTP 504 → retry
```

---

# 30. Retry Requires Idempotency

Retrying write operations is dangerous.

Example:

```text
charge card
create order
transfer money
```

Retry may duplicate operation.

---

# 31. Idempotency Key

Safe write retries need:

```text
idempotency key
```

Example:

```text
Idempotency-Key: abc-123
```

Server detects duplicate request and returns same result.

---

# 32. Payment Retry ASCII

```text
Charge Request abc-123
      ↓
Timeout
      ↓
Retry abc-123
      ↓
Server detects duplicate
      ↓
No double charge
```

---

# 33. Retry Without Idempotency

```text
Payment timeout
      ↓
Retry
      ↓
Payment charged twice
```

Very dangerous.

---

# 34. Circuit Breaker Does Not Replace Timeout

Circuit breaker needs failures to observe.

Timeout defines:

```text
when call is considered failed/slow
```

Without timeout:

```text
threads may block too long
```

---

# 35. Timeout + Retry

Timeout too short:

```text
false retry
```

Timeout too long:

```text
thread exhaustion
```

Retry must fit inside:

```text
overall deadline
```

---

# 36. Deadline Budget

If total budget:

```text
5 seconds
```

Do not do:

```text
3 retries × 5 seconds each = 15 seconds
```

Retries must share same budget.

---

# 37. Deadline ASCII

```text
Total Budget = 5s

Attempt 1 = 1.5s
Wait      = 0.2s
Attempt 2 = 1.5s
Wait      = 0.2s
Attempt 3 = 1.6s

Total ≈ 5s
```

---

# 38. Retry At Every Layer Problem

If Gateway retries 3 times and Service retries 3 times and DB retries 3 times:

```text
3 × 3 × 3 = 27 attempts
```

This is retry amplification.

---

# 39. Multi-Layer Retry ASCII

```text
Gateway Retry
    ↓
Service Retry
    ↓
DB Retry

Attempts multiply
```

---

# 40. Production Rule

Retry should usually happen:

```text
at one well-defined layer
```

not everywhere.

---

# 41. Retry vs Circuit Breaker Table

```text
Retry:
- attempts again
- useful for transient failures
- increases traffic
- needs backoff/jitter
- dangerous for non-idempotent writes

Circuit Breaker:
- stops calls
- useful for ongoing failures
- reduces traffic
- protects resources
- needs sliding window metrics
```

---

# 42. Resilience4j Retry Config

```yaml
resilience4j:
  retry:
    instances:
      paymentService:
        maxAttempts: 3
        waitDuration: 500ms
        retryExceptions:
          - java.net.SocketTimeoutException
```

---

# 43. Resilience4j CircuitBreaker Config

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 100
        minimumNumberOfCalls: 20
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
```

---

# 44. Combined Resilience4j Mental Model

```text
Retry:
small recovery attempts

CircuitBreaker:
system safety switch
```

---

# 45. Java Retry Example

```java
public class SimpleRetry {

    public String execute() {

        int maxAttempts = 3;

        for (int attempt = 1;
             attempt <= maxAttempts;
             attempt++) {

            try {

                return remoteCall();

            } catch (RuntimeException ex) {

                if (attempt == maxAttempts) {

                    throw ex;
                }
            }
        }

        throw new IllegalStateException();
    }

    private String remoteCall() {

        throw new RuntimeException("temporary failure");
    }
}
```

---

# 46. Java Circuit Breaker Example

```java
public class SimpleCircuitBreaker {

    private boolean open;

    public String execute() {

        if (open) {

            return "fallback";
        }

        try {

            return remoteCall();

        } catch (RuntimeException ex) {

            open = true;

            return "fallback";
        }
    }

    private String remoteCall() {

        throw new RuntimeException("failure");
    }
}
```

---

# 47. Java Combined Example

```java
public class RetryWithCircuitBreaker {

    private final SimpleCircuitBreaker breaker =
            new SimpleCircuitBreaker();

    public String execute() {

        int maxAttempts = 3;

        for (int attempt = 1;
             attempt <= maxAttempts;
             attempt++) {

            try {

                return breaker.execute();

            } catch (RuntimeException ex) {

                if (attempt == maxAttempts) {
                    throw ex;
                }

                sleep(100L * attempt);
            }
        }

        return "fallback";
    }

    private void sleep(long ms) {

        try {

            Thread.sleep(ms);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }
}
```

---

# 48. Dry Run — Retry Helps

Dependency:

```text
Attempt 1 fails
Attempt 2 succeeds
```

Result:

```text
user sees success
circuit remains CLOSED
```

---

# 49. Dry Run — Retry Hurts

Dependency overloaded.

Attempts:

```text
1 fails
2 fails
3 fails
```

Many clients do same.

Result:

```text
traffic triples
dependency worsens
```

Circuit breaker should OPEN.

---

# 50. Dry Run — Circuit Breaker Helps

Failure rate crosses threshold.

Circuit:

```text
OPEN
```

New requests:

```text
rejected immediately
fallback returned
```

Dependency gets time to recover.

---

# 51. Kafka Consumer Example

Retry useful for:

```text
temporary DB deadlock
```

Circuit breaker useful for:

```text
external API down for minutes
```

Bad strategy:

```text
infinite retries block consumer forever
```

Better:

```text
limited retry
backoff
DLT
circuit breaker
```

---

# 52. Payment Example

Retry only safe with:

```text
idempotency key
```

Circuit breaker protects when:

```text
bank API down
```

Fallback may be:

```text
payment pending
```

not:

```text
fake success
```

---

# 53. Search/Recommendation Example

Retry may be unnecessary.

If recommendation service slow:

```text
fallback to cached recommendations
```

Better than repeated retries.

---

# 54. Production Metrics

Monitor:

```text
retry attempts
retry success rate
retry failure rate
circuit OPEN count
CallNotPermitted count
fallback count
dependency latency
```

---

# 55. Retry Success Rate

If retry success rate low:

```text
retries are not helping
```

Reduce retry count or open circuit faster.

---

# 56. Circuit OPEN Frequency

If circuit frequently opens:

```text
dependency unstable
or thresholds too strict
or timeout too low
```

Investigate.

---

# 57. Common Mistakes

## Mistake 1

```text
Retrying everything
```

Causes traffic amplification.

---

## Mistake 2

```text
Retrying non-idempotent operations
```

Can duplicate money/order actions.

---

## Mistake 3

```text
No backoff
```

Retry storm.

---

## Mistake 4

```text
No jitter
```

Thundering herd.

---

## Mistake 5

```text
Retrying CallNotPermittedException
```

Defeats circuit breaker protection.

---

## Mistake 6

```text
Retries at every service layer
```

Exponential attempt explosion.

---

# 58. Most Important Insight

```text
Retry increases traffic.
Circuit breaker reduces traffic.
```

They are opposite forces.

Use retry only when failure is likely temporary.

Use circuit breaker when dependency looks unhealthy.

---

# 59. Distributed Systems Insight

In distributed systems, repeated "helpful" retries can become:

```text
self-inflicted DDoS
```

against your own dependency.

---

# 60. Interview Explanation

If interviewer asks:

```text
Difference between retry and circuit breaker?
```

Strong answer:

```text
Retry attempts an operation again for transient failures, while circuit
breaker stops calls to an unhealthy dependency after failure thresholds
are exceeded. Retry can improve availability for short glitches, but
without backoff and circuit breaker protection it can create retry storms.
```

Senior addition:

```text
Retries increase load, while circuit breakers reduce load. Production
systems combine limited retries, timeout, backoff, jitter, exception
classification, idempotency, and circuit breakers.
```

---

# 61. Final Mental Model

```text
Retry =
small controlled optimism

Circuit Breaker =
system protection pessimism
```

---

# 62. What To Remember

```text
Retry tries again.

Circuit breaker stops trying.

Retry helps transient failures.

Retry hurts overloaded dependencies.

Circuit breaker prevents retry storms.

Use backoff and jitter.

Retry only idempotent operations safely.

Do not retry business/validation errors.

Do not retry CallNotPermittedException.

Use metrics to tune retry and CB behavior.
```

---

# 63. Next File

```text
014_Exponential_Backoff_And_Jitter.md
```

Next you learn:

```text
backoff algorithms
fixed delay
linear backoff
exponential backoff
jitter strategies
thundering herd prevention
production retry timing
```
