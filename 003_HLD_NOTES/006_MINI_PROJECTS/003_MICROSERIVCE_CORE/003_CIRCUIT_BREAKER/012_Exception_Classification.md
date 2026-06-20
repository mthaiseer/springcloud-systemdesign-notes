# 012_Exception_Classification.md


# MiniCircuitBreaker — 012 Exception Classification

---

# 1. Why This File Exists

Previous files explained:

```text
timeouts
slow calls
circuit breaker thresholds
```

But real distributed systems face another critical problem:

```text
not all exceptions are equal
```

Some failures are:

```text
temporary
```

Some are:

```text
permanent
```

Some should:

```text
trigger retries
```

Some should:

```text
be ignored
```

Some should:

```text
OPEN the circuit immediately
```

This file explains:

```text
exception classification
retryable exceptions
ignored exceptions
business exceptions
system exceptions
HTTP status mapping
Resilience4j filtering
custom predicates
production exception handling
```

---

# 2. One-Line Definition

```text
Exception classification decides how the system reacts to different failures.
```

---

# 3. Biggest Mental Model

```text
Exception Occurs
       ↓
Classify Exception
       ↓
Retry?
Ignore?
Fallback?
Open Circuit?
```

---

# 4. Why Classification Important

Treating all exceptions equally causes:

```text
bad retries
false circuit opening
resource waste
traffic amplification
```

Need intelligent handling.

---

# 5. Temporary Failure

Temporary failures may recover automatically.

Examples:

```text
network timeout
temporary overload
short DB lock
connection reset
```

Retries may help.

---

# 6. Permanent Failure

Permanent failures will NOT succeed after retry.

Examples:

```text
validation error
illegal argument
bad request
missing data
```

Retries useless.

---

# 7. Temporary vs Permanent Mental Model

```text
Temporary Problem
      ↓
Retry May Help

Permanent Problem
      ↓
Retry Wastes Resources
```

---

# 8. System Exceptions

System exceptions indicate:

```text
infrastructure instability
dependency problems
resource exhaustion
network failure
```

Usually important for CB.

---

# 9. Business Exceptions

Business exceptions indicate:

```text
invalid business operation
```

Examples:

```text
invalid coupon
insufficient balance
user already exists
```

These are NOT infrastructure failures.

---

# 10. Business Failure Mental Model

```text
Business Failure ≠ System Failure
```

---

# 11. Why Business Exceptions Dangerous For CB

Suppose:

```text
InvalidCouponException
```

If counted as system failure:

```text
circuit opens incorrectly
```

Healthy service becomes blocked.

---

# 12. Wrong Classification ASCII

```text
Bad User Input
      ↓
Counted As Failure
      ↓
Circuit Opens
      ↓
Healthy Service Blocked
```

---

# 13. Retryable Exceptions

Retryable exceptions usually:

```text
timeouts
temporary network failures
HTTP 503
connection reset
```

---

# 14. Non-Retryable Exceptions

Non-retryable exceptions:

```text
HTTP 400
validation error
authentication failure
illegal argument
```

Retry wastes resources.

---

# 15. HTTP Status Classification

Common mapping:

```text
2xx → success
4xx → client/business issue
5xx → server/system issue
```

---

# 16. HTTP 400 Example

```text
Bad Request
```

Usually:

```text
NOT retryable
```

---

# 17. HTTP 503 Example

```text
Service Unavailable
```

Usually:

```text
retryable
```

---

# 18. Retry Explosion Problem

If every exception retried:

```text
traffic multiplies
```

System becomes slower.

---

# 19. Retry Explosion ASCII

```text
Failure
   ↓
Retries
   ↓
More Traffic
   ↓
More Failures
```

---

# 20. Circuit Breaker Classification

Circuit breaker supports:

```text
record exceptions
ignore exceptions
```

---

# 21. Record Exceptions

Recorded exceptions contribute to:

```text
failure rate
OPEN transition
metrics
```

---

# 22. Ignore Exceptions

Ignored exceptions:

```text
do NOT affect circuit health
```

Still returned to caller.

---

# 23. Java Business Exception

```java
public class InvalidCouponException
        extends RuntimeException {

    public InvalidCouponException(
            String message) {

        super(message);
    }
}
```

---

# 24. Java System Exception

```java
public class DependencyTimeoutException
        extends RuntimeException {

    public DependencyTimeoutException(
            String message) {

        super(message);
    }
}
```

---

# 25. Java Retryable Check

```java
public static boolean isRetryable(
        Exception ex) {

    return ex instanceof
            TimeoutException
            ||
            ex instanceof
            ConnectException;
}
```

---

# 26. Retryable Dry Run

Exception:

```text
SocketTimeoutException
```

Result:

```text
retryable = true
```

---

# 27. Non-Retryable Dry Run

Exception:

```text
IllegalArgumentException
```

Result:

```text
retryable = false
```

---

# 28. Java Ignore Rule

```java
public static boolean shouldIgnore(
        Exception ex) {

    return ex instanceof
            InvalidCouponException;
}
```

---

# 29. Ignore Dry Run

Exception:

```text
InvalidCouponException
```

Result:

```text
ignored by CB
```

---

# 30. HTTP Classification Example

```java
public static boolean retryableHttp(
        int status) {

    return status >= 500;
}
```

---

# 31. HTTP Dry Run

Status:

```text
503
```

Result:

```text
retryable = true
```

---

# 32. HTTP Dry Run 2

Status:

```text
400
```

Result:

```text
retryable = false
```

---

# 33. Resilience4j Record Exceptions

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        recordExceptions:
          - java.net.SocketTimeoutException
          - java.io.IOException
```

---

# 34. Record Config Meaning

```text
these exceptions contribute to failure rate
```

---

# 35. Resilience4j Ignore Exceptions

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        ignoreExceptions:
          - com.demo.InvalidCouponException
```

---

# 36. Ignore Config Meaning

```text
business exceptions ignored
```

Circuit remains healthy.

---

# 37. Retry Config Example

```yaml
resilience4j:
  retry:
    instances:
      paymentService:
        retryExceptions:
          - java.net.SocketTimeoutException
```

---

# 38. Payment System Example

Payment timeout:

```text
retry may help
```

Invalid card number:

```text
retry useless
```

---

# 39. Kafka Consumer Example

Temporary DB timeout:

```text
retry reasonable
```

Invalid message schema:

```text
send to DLT
```

NOT infinite retry.

---

# 40. Exception Taxonomy

Large systems maintain:

```text
business
retryable
security
validation
system
fatal
```

categories.

---

# 41. Why Taxonomy Important

Without standard classification:

```text
different teams retry differently
```

Creates instability.

---

# 42. Security Exceptions

Security failures:

```text
authentication failure
authorization failure
```

Usually:

```text
NOT retryable
```

---

# 43. Predicate Mental Model

Advanced systems use:

```text
custom predicates
```

to decide:

```text
should this failure count?
```

---

# 44. Predicate Example

```java
recordException(ex ->
        ex instanceof IOException)
```

---

# 45. Production Metrics

Monitor:

```text
retryable exceptions
ignored exceptions
system failures
business failures
fallback count
retry count
```

---

# 46. Common Mistakes

## Mistake 1

```text
Retrying all exceptions
```

Traffic explosion.

---

## Mistake 2

```text
Counting business failures as infrastructure failures
```

False OPEN.

---

## Mistake 3

```text
Ignoring infrastructure instability
```

CB never opens.

---

## Mistake 4

```text
Infinite retry on permanent errors
```

Wasted resources.

---

# 47. Most Important Insight

```text
Not all failures are infrastructure failures.
```

Classification is critical.

---

# 48. Distributed Systems Insight

Distributed resiliency engineering depends heavily on:

```text
understanding failure semantics
```

NOT just catching exceptions.

---

# 49. Interview Explanation

If interviewer asks:

```text
Why classify exceptions?
```

Strong answer:

```text
Different exceptions require different resiliency strategies. Temporary
infrastructure failures may need retries, while business or validation
errors should usually be ignored by retries and circuit breakers.
```

---

# 50. Final Mental Model

```text
Exception
    ↓
Classify Meaning
    ↓
Choose Correct Strategy
```

---

# 51. What To Remember

```text
Not all exceptions are equal.

Business failures ≠ infrastructure failures.

Retry only temporary failures.

Ignore business exceptions in CB.

HTTP 5xx often retryable.

HTTP 4xx usually not retryable.

Classification prevents retry storms.

Production systems maintain exception taxonomy.
```
