# 017_RateLimiter_Integration.md

# MiniCircuitBreaker — 017 RateLimiter Integration

---

# 1. Why This File Exists

Previous files explained:

```text
Circuit Breaker
Timeout
Retry
Backoff
Jitter
Fallback
Bulkhead
```

But one more protection layer is critical:

```text
Rate Limiter
```

Circuit breaker protects against:

```text
unhealthy dependencies
```

Bulkhead protects against:

```text
resource exhaustion
```

Rate limiter protects against:

```text
too much traffic
```

In production systems, these patterns work together.

This file explains:

```text
rate limiter integration
traffic shaping
token bucket
leaky bucket
burst protection
429 handling
rate limiter vs circuit breaker
rate limiter vs bulkhead
Resilience4j RateLimiter
Java implementation
Spring Boot config
production architecture
gateway-level throttling
service-level throttling
distributed rate limiting
```

---

# 2. One-Line Definition

```text
Rate limiter controls how many requests are allowed during a time period.
```

---

# 3. Biggest Mental Model

```text
Too Much Traffic
        ↓
Rate Limiter
        ↓
Allow Some
Reject/Delay Extra
```

---

# 4. Why Rate Limiter Needed

Without rate limiting:

```text
traffic spike
      ↓
all requests accepted
      ↓
threads exhausted
      ↓
DB overloaded
      ↓
service collapses
```

Rate limiter prevents overload early.

---

# 5. Traffic Spike ASCII

```text
Normal Traffic:
|||||

Spike Traffic:
|||||||||||||||||||||||||||||
```

Rate limiter smooths the spike.

---

# 6. Rate Limiter vs Circuit Breaker

## Rate Limiter

Protects from:

```text
too many requests
```

## Circuit Breaker

Protects from:

```text
unhealthy dependency
```

---

# 7. Rate Limiter vs Bulkhead

## Rate Limiter

Controls:

```text
request rate
```

Example:

```text
100 requests/sec
```

## Bulkhead

Controls:

```text
concurrent resource usage
```

Example:

```text
20 concurrent calls
```

---

# 8. Three Protection Layers

```text
RateLimiter = traffic volume
Bulkhead = concurrency/resources
CircuitBreaker = dependency health
```

---

# 9. Combined ASCII

```text
Request
   ↓
RateLimiter
   ↓
Bulkhead
   ↓
Timeout
   ↓
CircuitBreaker
   ↓
Remote Dependency
```

---

# 10. Why Order Matters

Rate limiter early:

```text
rejects excess traffic before consuming resources
```

Bulkhead next:

```text
limits concurrent execution
```

Circuit breaker:

```text
stops unhealthy dependency calls
```

---

# 11. Rate Limiter Use Cases

Common use cases:

```text
API Gateway protection
per-user limits
per-client limits
external API protection
payment fraud protection
login throttling
Kafka consumer throttling
DB protection
```

---

# 12. API Gateway Rate Limiting

Gateway often applies:

```text
global rate limits
tenant limits
user limits
IP limits
```

---

# 13. Gateway ASCII

```text
Client
  ↓
API Gateway RateLimiter
  ↓
Microservices
```

---

# 14. Service-Level Rate Limiting

Each service may also protect itself.

Example:

```text
payment service allows 500 req/sec
```

Even if gateway misconfigured:

```text
service protects itself
```

---

# 15. Defense In Depth

Production systems use rate limiting at multiple layers:

```text
edge
gateway
service
dependency client
```

---

# 16. Token Bucket Algorithm

Token bucket is common.

Mental model:

```text
bucket fills with tokens over time
each request consumes one token
no token = reject or wait
```

---

# 17. Token Bucket ASCII

```text
Bucket:
[ T ][ T ][ T ]

Request consumes token:
[ T ][ T ][   ]
```

---

# 18. Token Refill

Tokens refill at fixed rate.

Example:

```text
10 tokens per second
```

---

# 19. Token Bucket Allows Bursts

If bucket has saved tokens:

```text
short burst allowed
```

This is useful for real traffic.

---

# 20. Token Bucket Example

Config:

```text
capacity = 10
refill = 5 tokens/sec
```

Meaning:

```text
up to 10 burst requests
average 5 req/sec
```

---

# 21. Leaky Bucket Algorithm

Leaky bucket processes requests at:

```text
constant rate
```

Excess requests queue or drop.

---

# 22. Leaky Bucket ASCII

```text
Requests Enter Bucket
        ↓
Leak Out At Fixed Rate
```

---

# 23. Token Bucket vs Leaky Bucket

## Token Bucket

```text
allows bursts
```

## Leaky Bucket

```text
smooths traffic
```

---

# 24. Fixed Window Counter

Simple algorithm:

```text
count requests per time window
```

Example:

```text
100 requests per minute
```

---

# 25. Fixed Window Problem

At boundary:

```text
100 requests at end of minute
100 requests at start of next minute
```

Total:

```text
200 requests quickly
```

Burst problem.

---

# 26. Sliding Window Rate Limit

More accurate than fixed window.

Tracks:

```text
recent rolling time
```

Better fairness.

---

# 27. 429 Response

When rate limit exceeded, common response:

```text
HTTP 429 Too Many Requests
```

Often include:

```text
Retry-After
```

---

# 28. 429 Handling

Client should:

```text
respect Retry-After
backoff
not retry immediately
```

---

# 29. Retry-After ASCII

```text
429 Too Many Requests
Retry-After: 10 seconds

Client waits 10s
```

---

# 30. Rate Limiter + Retry

Danger:

```text
retrying 429 immediately
```

creates more overload.

Correct:

```text
respect Retry-After
use backoff
```

---

# 31. Rate Limiter + Circuit Breaker

If dependency unhealthy:

```text
CircuitBreaker OPEN
```

If traffic too high:

```text
RateLimiter rejects
```

Both can reject requests but for different reasons.

---

# 32. Rate Limiter + Bulkhead

Rate limiter limits incoming rate.

Bulkhead limits concurrent execution.

Together prevent:

```text
traffic overload
resource exhaustion
```

---

# 33. Java Simple Token Bucket

```java
public class TokenBucket {

    private final int capacity;

    private final int refillRatePerSecond;

    private int tokens;

    private long lastRefillTime;

    public TokenBucket(
            int capacity,
            int refillRatePerSecond) {

        this.capacity = capacity;
        this.refillRatePerSecond =
                refillRatePerSecond;
        this.tokens = capacity;
        this.lastRefillTime =
                System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {

        refill();

        if (tokens > 0) {

            tokens--;

            return true;
        }

        return false;
    }

    private void refill() {

        long now =
                System.currentTimeMillis();

        long elapsedSeconds =
                (now - lastRefillTime) / 1000;

        if (elapsedSeconds > 0) {

            int newTokens =
                    (int) elapsedSeconds
                            * refillRatePerSecond;

            tokens =
                    Math.min(
                            capacity,
                            tokens + newTokens
                    );

            lastRefillTime = now;
        }
    }
}
```

---

# 34. Token Bucket Dry Run

Config:

```text
capacity = 5
refill = 2/sec
```

Initial:

```text
tokens = 5
```

6 requests arrive immediately:

```text
first 5 allowed
6th rejected
```

After 1 second:

```text
2 tokens refilled
```

---

# 35. Java RateLimiter Usage

```java
TokenBucket limiter =
        new TokenBucket(5, 2);

if (limiter.allowRequest()) {

    callService();

} else {

    returnTooManyRequests();
}
```

---

# 36. Thread Safety

Rate limiter state is shared.

Need:

```text
synchronized
locks
atomic counters
Redis Lua
distributed counters
```

depending on scale.

---

# 37. Local Rate Limiter Problem

If app has:

```text
10 instances
```

and each allows:

```text
100 req/sec
```

Total system allows:

```text
1000 req/sec
```

Maybe too high.

---

# 38. Distributed Rate Limiter

Use shared store:

```text
Redis
DynamoDB
central rate-limit service
```

to enforce global limit.

---

# 39. Redis Rate Limiter Mental Model

```text
all app instances share one counter/token state
```

---

# 40. Redis Lua Why

Need atomic operation:

```text
check tokens
decrement token
set TTL
```

Lua ensures:

```text
atomic execution
```

---

# 41. RateLimiter In Resilience4j

```yaml
resilience4j:
  ratelimiter:
    instances:
      paymentService:
        limitForPeriod: 100
        limitRefreshPeriod: 1s
        timeoutDuration: 0
```

---

# 42. Config Meaning

```text
100 calls allowed every 1 second
do not wait when limit exceeded
reject immediately
```

---

# 43. timeoutDuration Meaning

If timeoutDuration > 0:

```text
caller can wait for permission
```

If 0:

```text
fail fast
```

---

# 44. Fail Fast vs Wait

## Fail Fast

```text
protects system aggressively
```

## Wait

```text
may improve success
but consumes caller time/resources
```

---

# 45. Spring Annotation Example

```java
@RateLimiter(
        name = "paymentService",
        fallbackMethod = "rateLimitFallback"
)
public String callPayment() {

    return paymentClient.call();
}

public String rateLimitFallback(
        Exception ex) {

    return "Too many requests";
}
```

---

# 46. Rate Limiter Exception

Resilience4j may throw:

```text
RequestNotPermitted
```

when limit exceeded.

---

# 47. Do Not Retry RequestNotPermitted Immediately

If rate limiter rejects:

```text
respect limit
```

Do not instantly retry.

---

# 48. Rate Limiter Metrics

Monitor:

```text
allowed calls
rejected calls
waiting calls
available permissions
limit saturation
```

---

# 49. Production Dashboards

Important graphs:

```text
requests allowed/sec
requests rejected/sec
429 count
per-user throttling
tenant saturation
```

---

# 50. Login Rate Limiting

For login:

```text
limit attempts per user/IP
```

Protects against:

```text
brute force attacks
```

---

# 51. Payment Rate Limiting

Payment systems use limits for:

```text
fraud protection
external provider protection
traffic shaping
```

---

# 52. External API Protection

If external provider allows:

```text
100 req/sec
```

Your client must enforce:

```text
<= 100 req/sec
```

Otherwise provider may throttle or ban.

---

# 53. RateLimiter + API Quotas

Quotas define:

```text
allowed usage over longer period
```

Example:

```text
10,000 requests/day
```

Rate limiter controls short-term flow.

---

# 54. RateLimiter + CircuitBreaker Flow

```text
Request
   ↓
RateLimiter allows?
   ↓
Bulkhead capacity?
   ↓
CircuitBreaker healthy?
   ↓
Call dependency
```

---

# 55. Rejection Reason Matters

Different rejections:

```text
429 from RateLimiter
503 from CircuitBreaker OPEN
503 from Bulkhead full
```

Do not mix them blindly.

---

# 56. Common Mistakes

## Mistake 1

```text
Only using circuit breaker for traffic spikes
```

Wrong. Need rate limiter.

---

## Mistake 2

```text
Retrying 429 immediately
```

Creates overload.

---

## Mistake 3

```text
Local rate limiter when global limit needed
```

Cluster allows too much traffic.

---

## Mistake 4

```text
No per-user limits
```

One user can abuse system.

---

## Mistake 5

```text
No metrics
```

Throttling invisible.

---

# 57. Most Important Insight

```text
RateLimiter controls traffic before resources are consumed.
```

This is why it usually sits early in request flow.

---

# 58. Distributed Systems Insight

Circuit breakers react to dependency health.

Rate limiters proactively prevent overload.

Both are needed.

---

# 59. Interview Explanation

If interviewer asks:

```text
How does RateLimiter integrate with CircuitBreaker?
```

Strong answer:

```text
RateLimiter limits request volume before calls consume resources, while
CircuitBreaker stops calls to unhealthy dependencies. Together with
bulkhead and timeout, they prevent overload, resource exhaustion, and
cascading failures.
```

Senior addition:

```text
In distributed systems, local rate limiters may not enforce global limits,
so Redis or centralized rate-limit services are used for cluster-wide
traffic control.
```

---

# 60. Final Mental Model

```text
RateLimiter =
Traffic Gate

Bulkhead =
Resource Gate

CircuitBreaker =
Health Gate
```

---

# 61. What To Remember

```text
RateLimiter protects against traffic overload.

CircuitBreaker protects against unhealthy dependency.

Bulkhead protects resources.

Token bucket allows bursts.

Leaky bucket smooths traffic.

HTTP 429 means too many requests.

Respect Retry-After.

Do not retry rate limit rejection immediately.

Distributed rate limiting needs shared state.

RateLimiter should be monitored carefully.
```

---

# 62. Next File

```text
018_Thread_Safe_CircuitBreaker.md
```

Next you learn:

```text
thread-safe state management
race conditions
atomic counters
synchronized vs lock-free
concurrent metrics update
safe state transitions
production concurrency bugs
```
