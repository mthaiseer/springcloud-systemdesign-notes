# 014_Exponential_Backoff_And_Jitter.md

# MiniCircuitBreaker — 014 Exponential Backoff And Jitter

---

# 1. Why This File Exists

Previous files explained:

```text
Retry
Circuit Breaker
Timeout
Exception Classification
```

But retry has a dangerous problem:

```text
if everyone retries immediately,
the failing service receives even more traffic
```

This can create:

```text
retry storm
thundering herd
self-inflicted DDoS
```

To prevent this, production systems use:

```text
exponential backoff
jitter
retry budgets
max retry limits
```

This file explains:

```text
fixed delay
linear backoff
exponential backoff
jitter
thundering herd
retry storm prevention
retry budget
deadline-aware retries
Resilience4j config
Java implementation
dry runs
production tuning
```

---

# 2. One-Line Definition

```text
Exponential backoff increases retry delay after each failure.
```

```text
Jitter adds randomness to retry delays to avoid synchronized retries.
```

---

# 3. Biggest Mental Model

```text
Failure
   ↓
Do NOT retry immediately
   ↓
Wait longer each time
   ↓
Add randomness
   ↓
Protect dependency
```

---

# 4. Why Immediate Retry Is Dangerous

Suppose:

```text
1000 clients call payment service
```

Payment service becomes slow.

All clients fail.

If all retry immediately:

```text
payment service receives another 1000 calls instantly
```

This makes service even slower.

---

# 5. Retry Storm ASCII

```text
Service Slow
      ↓
Requests Fail
      ↓
Clients Retry Immediately
      ↓
More Traffic
      ↓
Service Slower
      ↓
More Failures
```

---

# 6. Retry Storm Mental Model

```text
Retry can amplify failure.
```

Retry must be controlled.

---

# 7. Fixed Delay Retry

Fixed delay means:

```text
wait same duration between retries
```

Example:

```text
retry every 500ms
```

---

# 8. Fixed Delay Example

```text
Attempt 1 → fail
wait 500ms
Attempt 2 → fail
wait 500ms
Attempt 3 → fail
```

---

# 9. Fixed Delay Problem

If many clients use same delay:

```text
they retry together
```

This creates synchronized traffic spikes.

---

# 10. Fixed Delay ASCII

```text
t=0ms     many requests fail
t=500ms   all retry together
t=1000ms  all retry together
```

---

# 11. Linear Backoff

Linear backoff increases delay gradually.

Example:

```text
100ms
200ms
300ms
400ms
```

---

# 12. Linear Backoff Formula

```text
delay = baseDelay × attempt
```

---

# 13. Linear Backoff Example

Base delay:

```text
100ms
```

Retries:

```text
Attempt 1 → 100ms
Attempt 2 → 200ms
Attempt 3 → 300ms
Attempt 4 → 400ms
```

---

# 14. Exponential Backoff

Exponential backoff increases delay faster.

Example:

```text
100ms
200ms
400ms
800ms
1600ms
```

---

# 15. Exponential Backoff Formula

```text
delay = baseDelay × multiplier^(attempt - 1)
```

Example:

```text
baseDelay = 100ms
multiplier = 2
```

---

# 16. Exponential Backoff Dry Run

```text
Attempt 1 delay = 100 × 2^(1-1) = 100ms
Attempt 2 delay = 100 × 2^(2-1) = 200ms
Attempt 3 delay = 100 × 2^(3-1) = 400ms
Attempt 4 delay = 100 × 2^(4-1) = 800ms
```

---

# 17. Why Exponential Backoff Works

It reduces pressure on unhealthy service.

Instead of retrying aggressively:

```text
clients slow down automatically
```

This gives dependency time to recover.

---

# 18. Exponential Backoff ASCII

```text
fail
 ↓ wait 100ms
fail
 ↓ wait 200ms
fail
 ↓ wait 400ms
fail
 ↓ wait 800ms
```

---

# 19. Max Backoff

Backoff should have upper limit.

Example:

```text
maxDelay = 5 seconds
```

Without max delay:

```text
delay can grow too large
```

---

# 20. Max Backoff Example

```text
100ms
200ms
400ms
800ms
1600ms
3200ms
5000ms
5000ms
```

Delay capped at:

```text
5 seconds
```

---

# 21. Thundering Herd Problem

Even with exponential backoff, clients may retry at same time if they started together.

This creates:

```text
thundering herd
```

---

# 22. Thundering Herd ASCII

```text
1000 clients fail at same time

All retry after:
100ms
200ms
400ms

Traffic spikes repeat
```

---

# 23. Jitter

Jitter adds randomness.

Instead of:

```text
retry exactly after 400ms
```

Retry after:

```text
random delay around 400ms
```

---

# 24. Jitter Mental Model

```text
Spread retry traffic across time
```

---

# 25. Jitter ASCII

Without jitter:

```text
|||||||||||||||||||||
```

With jitter:

```text
|  ||   | |   ||  | |
```

---

# 26. Why Jitter Critical

Jitter prevents:

```text
synchronized retry spikes
```

It makes traffic smoother.

---

# 27. Full Jitter

Full jitter chooses random delay between:

```text
0 and calculatedBackoff
```

Example:

```text
calculatedBackoff = 800ms
actualDelay = random(0, 800ms)
```

---

# 28. Equal Jitter

Equal jitter uses:

```text
half backoff + random half
```

Example:

```text
actualDelay = backoff/2 + random(0, backoff/2)
```

---

# 29. Decorrelated Jitter

Decorrelated jitter varies delay based on previous delay.

Useful for:

```text
large distributed systems
```

because it avoids patterns.

---

# 30. Jitter Strategy Summary

```text
No Jitter:
simple but synchronized

Full Jitter:
best spreading

Equal Jitter:
balanced

Decorrelated Jitter:
advanced production strategy
```

---

# 31. Retry Budget

Retry budget limits total retry traffic.

Example:

```text
retries must not exceed 10% of original traffic
```

This prevents retry overload.

---

# 32. Retry Budget Mental Model

```text
Retries are not free.
```

They consume:

```text
CPU
network
threads
dependency capacity
```

---

# 33. Retry Budget ASCII

```text
Original Traffic = 1000 req/s
Retry Budget = 10%

Allowed retries = 100 req/s
```

---

# 34. Deadline-Aware Retry

Retries must fit inside:

```text
overall request deadline
```

Example:

```text
total budget = 5 seconds
```

Do not retry if remaining time too small.

---

# 35. Deadline Retry ASCII

```text
Total Budget = 5s

Attempt 1 = 1s
Wait = 0.2s
Attempt 2 = 1s
Wait = 0.4s
Remaining = 2.4s
```

Stop if next attempt cannot finish safely.

---

# 36. Retry Placement

Retries should usually happen:

```text
at one layer only
```

not:

```text
gateway + service + DB client + SDK
```

---

# 37. Retry Multiplication Problem

If each layer retries 3 times:

```text
Gateway 3 × Service 3 × DB 3 = 27 attempts
```

---

# 38. Retry Multiplication ASCII

```text
1 user request
      ↓
3 gateway attempts
      ↓
9 service attempts
      ↓
27 DB attempts
```

Very dangerous.

---

# 39. Retry + Circuit Breaker

Circuit breaker stops retries when dependency unhealthy.

Correct idea:

```text
small retries for transient issues
circuit breaker for sustained failure
```

---

# 40. Retry + CB ASCII

```text
Failure
   ↓
Retry with backoff
   ↓
Still failing?
   ↓
Circuit records failures
   ↓
OPEN
   ↓
Stop traffic
```

---

# 41. Retry + Timeout

Timeout must exist for each attempt.

Without timeout:

```text
retry may never happen
```

because first call hangs forever.

---

# 42. Timeout Per Attempt

Example:

```text
attempt timeout = 1s
max attempts = 3
```

Worst case:

```text
3 seconds + delays
```

Must fit overall deadline.

---

# 43. Retry + Idempotency

Retries are safest for:

```text
idempotent operations
```

Examples:

```text
GET
PUT
DELETE idempotent by design
```

Risky for:

```text
payment charge
order creation
money transfer
```

---

# 44. Idempotency Key

For non-idempotent operations, use:

```text
idempotency key
```

Example:

```text
Idempotency-Key: order-123
```

---

# 45. Payment Example

Without idempotency:

```text
payment timeout
retry
double charge possible
```

With idempotency:

```text
same key
server deduplicates
safe retry
```

---

# 46. Java Exponential Backoff

```java
public class ExponentialBackoff {

    public long delayMillis(
            int attempt,
            long baseDelayMs,
            int multiplier) {

        return (long) (
                baseDelayMs
                        * Math.pow(
                                multiplier,
                                attempt - 1
                        )
        );
    }
}
```

---

# 47. Java Backoff Dry Run

Input:

```text
baseDelayMs = 100
multiplier = 2
attempt = 3
```

Calculation:

```text
100 × 2^(3-1)
= 100 × 4
= 400ms
```

---

# 48. Java Max Backoff

```java
public long cappedDelay(
        int attempt,
        long baseDelayMs,
        int multiplier,
        long maxDelayMs) {

    long delay =
            (long) (
                    baseDelayMs
                            * Math.pow(
                                    multiplier,
                                    attempt - 1
                            )
            );

    return Math.min(
            delay,
            maxDelayMs
    );
}
```

---

# 49. Java Full Jitter

```java
import java.util.concurrent.ThreadLocalRandom;

public long fullJitter(
        long maxDelayMs) {

    return ThreadLocalRandom
            .current()
            .nextLong(
                    0,
                    maxDelayMs + 1
            );
}
```

---

# 50. Java Retry With Backoff And Jitter

```java
public class RetryWithBackoff {

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

                long backoff =
                        cappedDelay(
                                attempt,
                                100,
                                2,
                                2000
                        );

                long jitter =
                        fullJitter(backoff);

                sleep(jitter);
            }
        }

        throw new IllegalStateException();
    }

    private String remoteCall() {

        throw new RuntimeException(
                "temporary failure"
        );
    }

    private long cappedDelay(
            int attempt,
            long base,
            int multiplier,
            long max) {

        long delay =
                (long) (
                        base * Math.pow(
                                multiplier,
                                attempt - 1
                        )
                );

        return Math.min(delay, max);
    }

    private long fullJitter(long maxDelay) {

        return ThreadLocalRandom
                .current()
                .nextLong(
                        0,
                        maxDelay + 1
                );
    }

    private void sleep(long millis) {

        try {

            Thread.sleep(millis);

        } catch (InterruptedException e) {

            Thread.currentThread()
                    .interrupt();
        }
    }
}
```

---

# 51. Dry Run — Backoff Without Jitter

```text
Client A retry at 100ms
Client B retry at 100ms
Client C retry at 100ms
```

Problem:

```text
synchronized spike
```

---

# 52. Dry Run — Backoff With Jitter

```text
Client A retry at 73ms
Client B retry at 126ms
Client C retry at 41ms
```

Result:

```text
traffic spread
```

---

# 53. Resilience4j Retry Config

```yaml
resilience4j:
  retry:
    instances:
      paymentService:
        maxAttempts: 3
        waitDuration: 100ms
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
```

---

# 54. Resilience4j Config Meaning

```text
3 attempts
start with 100ms delay
multiply delay by 2 after each failure
```

---

# 55. Retry With Randomized Wait

Some systems use:

```text
randomizedWaitFactor
```

to add jitter-like behavior.

Example:

```yaml
resilience4j:
  retry:
    instances:
      paymentService:
        maxAttempts: 3
        waitDuration: 500ms
        enableRandomizedWait: true
        randomizedWaitFactor: 0.5
```

---

# 56. Production Metrics

Monitor:

```text
retry attempts
retry success rate
retry exhaustion count
retry latency overhead
dependency saturation
circuit breaker OPEN count
```

---

# 57. Retry Success Rate

If retry success rate is high:

```text
retries are useful
```

If retry success rate is low:

```text
retries only add load
```

---

# 58. Common Mistakes

## Mistake 1

```text
Immediate retries
```

Creates retry storm.

---

## Mistake 2

```text
No jitter
```

Creates thundering herd.

---

## Mistake 3

```text
Too many retries
```

Increases latency and load.

---

## Mistake 4

```text
No max backoff
```

Delays become unreasonable.

---

## Mistake 5

```text
Ignoring overall deadline
```

User waits too long.

---

## Mistake 6

```text
Retrying non-idempotent operations
```

Duplicates side effects.

---

# 59. Most Important Insight

```text
Backoff slows retries.
Jitter spreads retries.
```

Both are needed.

---

# 60. Distributed Systems Insight

At scale, synchronized clients can create:

```text
massive traffic waves
```

Jitter prevents retry synchronization.

---

# 61. Interview Explanation

If interviewer asks:

```text
Why use exponential backoff and jitter?
```

Strong answer:

```text
Exponential backoff reduces retry pressure by increasing delay after each
failure. Jitter randomizes retry timing so clients do not retry together,
preventing thundering herd and retry storms.
```

Senior addition:

```text
Production systems also cap maximum delay, enforce retry budgets, respect
overall request deadlines, and avoid retrying non-idempotent operations
without idempotency keys.
```

---

# 62. Final Mental Model

```text
Retry Failure
      ↓
Backoff Slows Down
      ↓
Jitter Spreads Out
      ↓
System Survives
```

---

# 63. What To Remember

```text
Immediate retries are dangerous.

Fixed delay can synchronize clients.

Exponential backoff reduces pressure.

Jitter prevents thundering herd.

Retries need max attempts.

Retries need max delay.

Retries must fit deadline budget.

Retry only retryable exceptions.

Retry non-idempotent operations only with idempotency.

Circuit breaker stops retry storms.
```

---

# 64. Next File

```text
015_Fallback_Response.md
```

Next you learn:

```text
fallback response
graceful degradation
cached fallback
default response
stale data
partial availability
fallback anti-patterns
production fallback strategies
```
