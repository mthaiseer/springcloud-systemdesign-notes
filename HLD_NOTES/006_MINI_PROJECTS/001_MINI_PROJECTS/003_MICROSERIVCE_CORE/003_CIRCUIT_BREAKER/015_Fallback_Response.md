# 015_Fallback_Response.md

# MiniCircuitBreaker — 015 Fallback Response

---

# 1. Why This File Exists

Previous files explained:

```text
Retry
Circuit Breaker
Timeout
Backoff
Jitter
```

But when dependency fails, a critical question remains:

```text
what should user receive?
```

Should application:

```text
crash?
show error?
return cached data?
degrade feature?
```

Production systems try to remain:

```text
partially available
```

instead of:

```text
completely broken
```

This is solved using:

```text
Fallback Response
```

This file explains:

```text
fallback response
graceful degradation
cached fallback
default response
stale cache
partial availability
fallback hierarchy
fallback anti-patterns
Resilience4j fallback
Java implementation
production strategies
```

---

# 2. One-Line Definition

```text
Fallback provides alternative behavior when primary dependency fails.
```

---

# 3. Biggest Mental Model

```text
Primary Path Failed
        ↓
Use Safe Alternative
        ↓
Keep System Usable
```

---

# 4. Why Fallback Important

Without fallback:

```text
dependency failure
    ↓
feature failure
    ↓
application unusable
```

With fallback:

```text
dependency failure
    ↓
reduced functionality
    ↓
application still usable
```

---

# 5. Graceful Degradation

Fallback enables:

```text
graceful degradation
```

Meaning:

```text
system continues working with reduced capabilities
```

---

# 6. Graceful Degradation ASCII

```text
Recommendation Service Down
        ↓
Show Cached Recommendations
        ↓
User Still Uses App
```

---

# 7. Hard Failure vs Graceful Failure

## Hard Failure

```text
HTTP 500
blank page
system crash
```

## Graceful Failure

```text
cached data
partial data
limited functionality
friendly message
```

---

# 8. Fallback Mental Model

```text
Better degraded experience
than complete outage
```

---

# 9. Common Fallback Types

Production systems use:

```text
cached fallback
default fallback
empty fallback
static fallback
secondary dependency
stale data fallback
queue-and-process-later
```

---

# 10. Cached Fallback

Most common fallback.

Example:

```text
Recommendation API fails
```

Return:

```text
last cached recommendations
```

---

# 11. Cached Fallback ASCII

```text
Primary API Down
      ↓
Use Redis Cache
      ↓
Return Older Data
```

---

# 12. Why Cached Fallback Powerful

Users usually prefer:

```text
slightly stale data
```

instead of:

```text
complete failure
```

---

# 13. Default Fallback

Return predefined safe value.

Example:

```text
Inventory API failed
```

Return:

```text
"inventory temporarily unavailable"
```

---

# 14. Empty Fallback

Return empty response safely.

Example:

```text
recommendations unavailable
```

Return:

```json
[]
```

instead of failure.

---

# 15. Static Fallback

Return hardcoded response.

Example:

```text
News API down
```

Return:

```text
Top headlines from static source
```

---

# 16. Secondary Dependency Fallback

Use backup provider.

Example:

```text
Primary payment gateway fails
```

Fallback:

```text
secondary payment provider
```

---

# 17. Secondary Provider ASCII

```text
Stripe Down
     ↓
Fallback To Adyen
```

---

# 18. Stale Data Fallback

Use expired but usable cache.

Example:

```text
weather cache expired 5 minutes ago
```

Still acceptable temporarily.

---

# 19. Stale-While-Revalidate Mental Model

```text
Return stale data immediately
      ↓
Refresh asynchronously
```

Very common production strategy.

---

# 20. Queue-And-Process-Later

Example:

```text
email service unavailable
```

Instead of failing request:

```text
store event in queue
```

Process later.

---

# 21. Queue Fallback ASCII

```text
Email API Down
      ↓
Save To Kafka
      ↓
Retry Later
```

---

# 22. Partial Availability

Large systems prioritize:

```text
partial availability
```

instead of:

```text
all-or-nothing availability
```

---

# 23. Example — E-Commerce

If recommendation service fails:

```text
checkout should still work
```

Recommendations degraded only.

---

# 24. Example — Search Engine

If typo correction fails:

```text
search results should still return
```

---

# 25. Example — Payment

Payment fallback dangerous.

Never:

```text
fake payment success
```

Correct fallback:

```text
payment pending
retry later
manual verification
```

---

# 26. Unsafe Fallback

Bad fallback:

```text
pretend operation succeeded
```

This causes:

```text
data inconsistency
financial loss
hidden corruption
```

---

# 27. Safe Fallback Principle

Fallback must preserve:

```text
correctness
```

even if degraded.

---

# 28. Fallback Hierarchy

Good systems use layered fallback.

Example:

```text
Primary API
    ↓
Cache
    ↓
Secondary Provider
    ↓
Static Response
    ↓
Friendly Error
```

---

# 29. Fallback Hierarchy ASCII

```text
Try Best Option
      ↓
If Fails
      ↓
Use Simpler Alternative
```

---

# 30. Circuit Breaker + Fallback

Most common integration:

```text
Circuit OPEN
      ↓
Return fallback immediately
```

---

# 31. Fallback Trigger

Fallback triggered by:

```text
timeout
circuit OPEN
retry exhausted
dependency unavailable
```

---

# 32. Fallback And Retry

Retries happen BEFORE fallback usually.

Flow:

```text
Call
 ↓
Retry
 ↓
Still failing?
 ↓
Fallback
```

---

# 33. Fallback And Timeout

Timeout prevents hanging forever.

Fallback provides alternative response after timeout.

---

# 34. Fallback And Bulkhead

Bulkhead isolates failures.

Fallback provides degraded response.

Together:

```text
contain failure + preserve usability
```

---

# 35. Fallback Response Quality

Good fallback should be:

```text
fast
safe
predictable
cheap
stable
```

---

# 36. Dangerous Fallbacks

Bad fallbacks:

```text
calling another overloaded dependency
heavy DB query
complex synchronous recovery
```

Fallback itself must NOT become bottleneck.

---

# 37. Fallback Loop Problem

Dangerous pattern:

```text
fallback calls another failing service
```

Creates:

```text
failure loop
```

---

# 38. Fallback Loop ASCII

```text
Service A fails
    ↓
Fallback calls Service B
    ↓
Service B fails
    ↓
Fallback calls Service C
```

Complex cascading failure.

---

# 39. User Experience Fallback

Sometimes best fallback is:

```text
clear friendly message
```

instead of technical failure.

Example:

```text
Recommendations temporarily unavailable
```

Better UX.

---

# 40. Real Production Example — Netflix

Netflix heavily uses:

```text
fallback + graceful degradation
```

If personalization fails:

```text
show popular content
```

instead of blank page.

---

# 41. Real Production Example — Amazon

If recommendation engine fails:

```text
shopping cart and checkout still work
```

Critical flows prioritized.

---

# 42. Real Production Example — Banking

If transaction history unavailable:

```text
balance inquiry may still work
```

Partial functionality preserved.

---

# 43. Java Simple Fallback

```java
public class ProductService {

    public String getProduct() {

        try {

            return callRemoteApi();

        } catch (Exception ex) {

            return fallback();
        }
    }

    private String callRemoteApi() {

        throw new RuntimeException(
                "API failure"
        );
    }

    private String fallback() {

        return "Default Product";
    }
}
```

---

# 44. Dry Run

```text
Remote API throws exception
       ↓
fallback() called
       ↓
"Default Product" returned
```

---

# 45. Cached Fallback Java Example

```java
public class RecommendationService {

    private final Map<String, String> cache =
            new HashMap<>();

    public String getRecommendations(
            String userId) {

        try {

            return remoteCall(userId);

        } catch (Exception ex) {

            return cache.getOrDefault(
                    userId,
                    "Popular Products"
            );
        }
    }

    private String remoteCall(
            String userId) {

        throw new RuntimeException(
                "Service Down"
        );
    }
}
```

---

# 46. Resilience4j Fallback Example

```java
@CircuitBreaker(
        name = "paymentService",
        fallbackMethod = "paymentFallback"
)
public String makePayment() {

    return remoteCall();
}

public String paymentFallback(
        Exception ex) {

    return "PAYMENT_PENDING";
}
```

---

# 47. Resilience4j Mental Model

```text
Primary call fails
      ↓
fallback method invoked
```

---

# 48. WebClient Fallback

```java
webClient.get()
    .retrieve()
    .bodyToMono(String.class)
    .onErrorReturn("fallback");
```

---

# 49. Kafka Fallback

If external API unavailable:

```text
store event in Kafka
retry asynchronously later
```

Very common production strategy.

---

# 50. Async Fallback Mental Model

```text
Do not block user
      ↓
Process eventually
```

---

# 51. Eventual Consistency Fallback

Fallback sometimes trades:

```text
immediate consistency
```

for:

```text
availability
```

Example:

```text
notification delivered later
```

instead of immediate failure.

---

# 52. Fallback Metrics

Monitor:

```text
fallback count
fallback rate
cache-hit fallback
secondary-provider usage
circuit OPEN fallback
```

---

# 53. Why Metrics Important

High fallback rate means:

```text
dependency unhealthy
```

Even if users not seeing errors.

---

# 54. Fallback Saturation

Fallback systems can overload too.

Example:

```text
all traffic shifts to secondary provider
```

Need capacity planning.

---

# 55. Common Mistakes

## Mistake 1

```text
Fake success fallback
```

Dangerous corruption.

---

## Mistake 2

```text
Heavy fallback logic
```

Fallback becomes bottleneck.

---

## Mistake 3

```text
No fallback metrics
```

Hidden degradation.

---

## Mistake 4

```text
Fallback calling failing dependency
```

Failure loop.

---

## Mistake 5

```text
Using stale data forever
```

Incorrect information.

---

## Mistake 6

```text
Blocking critical flows with non-critical failure
```

Poor architecture.

---

# 56. Most Important Insight

```text
Fallback is about preserving usability,
NOT pretending failure never happened.
```

---

# 57. Distributed Systems Insight

Large distributed systems prioritize:

```text
partial availability
```

because total availability impossible during failures.

---

# 58. Interview Explanation

If interviewer asks:

```text
What is fallback response?
```

Strong answer:

```text
Fallback provides alternative behavior when the primary dependency fails,
allowing the system to continue operating in degraded mode instead of
completely failing.
```

Senior addition:

```text
Production systems use cached fallback, stale data, secondary providers,
queue-based recovery, and graceful degradation while preserving
correctness and observability.
```

---

# 59. Final Mental Model

```text
Primary Failure
      ↓
Graceful Alternative
      ↓
System Still Usable
```

---

# 60. What To Remember

```text
Fallback preserves usability.

Graceful degradation better than outage.

Cached fallback very common.

Never fake success.

Fallback must be lightweight.

Fallback should preserve correctness.

Partial availability is production reality.

Fallback loops dangerous.

Monitor fallback usage carefully.
```

---

# 61. Next File

```text
016_Bulkhead_Isolation.md
```

Next you learn:

```text
resource isolation
thread pool isolation
semaphore bulkhead
thread starvation
Tomcat exhaustion
queue saturation
blast radius reduction
production bulkhead strategies
```
