# Design a Rate Limiter — FAANG Interview Notes

## 1. What is a Rate Limiter?

A **rate limiter** controls how many requests a client, user, IP, device, or service can send within a specific time window.

Examples:

- A user can create at most **2 posts per second**.
- An IP can create at most **10 accounts per day**.
- A device can claim rewards at most **5 times per week**.

When the limit is exceeded, extra requests are rejected, usually with:

```http
HTTP 429 Too Many Requests
```

---

## 2. Why Use Rate Limiting?

### Main Benefits

| Benefit | Explanation |
|---|---|
| Prevent DoS abuse | Blocks excessive traffic from malicious or buggy clients |
| Reduce cost | Avoids unnecessary calls to expensive third-party APIs |
| Protect backend services | Prevents database, cache, and API servers from overload |
| Improve fairness | Ensures one user cannot consume all resources |
| Improve reliability | Keeps the system stable during spikes |

---

## 3. Requirements

### Functional Requirements

- Limit requests accurately.
- Support limits by:
  - user ID
  - IP address
  - API key
  - device ID
  - endpoint
- Return clear error when throttled.
- Support different rules for different APIs.

### Non-Functional Requirements

- Low latency.
- Memory efficient.
- Distributed support.
- Highly available.
- Fault tolerant.
- Easy to update rules.
- Observable with metrics and logs.

---

## 4. Where to Place the Rate Limiter?

### Option 1: Client Side

Not recommended for strict enforcement.

Clients can be modified, bypassed, or forged.

### Option 2: Server Side

Rate limiting logic is implemented inside API servers.

```text
Client ---> API Server with Rate Limiter ---> Business Logic
```

Pros:

- Full control over logic.
- Easy to customize.

Cons:

- Code duplicated across services.
- Harder to manage globally.

### Option 3: API Gateway / Middleware

Most common for large systems.

```text
Client ---> API Gateway / Rate Limiter ---> API Servers
```

Pros:

- Centralized enforcement.
- Works well with microservices.
- Can combine with authentication, SSL termination, logging, and routing.

Cons:

- Gateway can become a bottleneck.
- Some third-party gateways limit customization.

---

## 5. Basic High-Level Design

```text
              +--------+
              | Client |
              +--------+
                  |
                  v
        +--------------------+
        | Rate Limiter       |
        | Middleware/Gateway |
        +--------------------+
             |          |
     allowed |          | rejected
             v          v
      +-------------+   +----------------+
      | API Servers |   | 429 Response   |
      +-------------+   +----------------+
             |
             v
        +---------+
        | Redis   |
        | Counters|
        +---------+
```

Flow:

1. Client sends request.
2. Rate limiter checks rule and request count.
3. If under limit, request goes to API server.
4. If over limit, return `429 Too Many Requests`.
5. Counters are stored in Redis for speed and expiration support.

---

## 6. Common Rate Limiting Algorithms

## 6.1 Token Bucket

A bucket has tokens. Each request consumes one token. Tokens refill at a fixed rate.

```text
          refill tokens
              |
              v
        +-----------+
        |  Bucket   | capacity = N
        |  tokens   |
        +-----------+
              |
       request consumes token
              |
              v
          allow / reject
```

### Parameters

- `bucketSize`: maximum tokens in bucket.
- `refillRate`: tokens added per second.

### Pros

- Simple.
- Memory efficient.
- Allows short bursts.
- Common in real systems.

### Cons

- Requires tuning bucket size and refill rate.

### Best For

- APIs that should allow bursts but enforce average rate.
- Public APIs.
- Login attempts.
- Posting/commenting APIs.

---

## 6.2 Leaking Bucket

Requests enter a queue and are processed at a fixed rate.

```text
Requests ---> [ FIFO Queue ] ---> Process at fixed rate
                 |
                 v
            drop if full
```

### Pros

- Smooth, stable output rate.
- Memory efficient with fixed queue size.

### Cons

- Bursts may cause new requests to be dropped behind old queued requests.
- Adds queueing delay.

### Best For

- Systems that need stable downstream traffic.
- Payment processing.
- Background jobs.

---

## 6.3 Fixed Window Counter

Divide time into fixed windows and count requests per window.

```text
Window 1: 12:00:00 - 12:00:59 -> max 100 requests
Window 2: 12:01:00 - 12:01:59 -> max 100 requests
```

### Pros

- Very simple.
- Memory efficient.
- Easy to implement with Redis `INCR` and `EXPIRE`.

### Cons

- Boundary problem.
- A client can send 100 requests at the end of one window and 100 at the start of the next window.

```text
Allowed limit = 100/min

12:00:59 -> 100 requests
12:01:00 -> 100 requests

Result: 200 requests in 2 seconds
```

---

## 6.4 Sliding Window Log

Store timestamps of requests in a rolling window.

```text
For each request:
1. Remove timestamps older than currentTime - windowSize
2. Count remaining timestamps
3. If count < limit, allow and add timestamp
4. Else reject
```

### Pros

- Very accurate.
- No fixed-window boundary issue.

### Cons

- High memory usage.
- Stores timestamp for every request.

### Best For

- Strict rate limiting.
- Security-sensitive endpoints.

---

## 6.5 Sliding Window Counter

Hybrid of fixed window and sliding window log.

Formula:

```text
estimatedCount = currentWindowCount + previousWindowCount * overlapPercentage
```

Example:

```text
Limit = 7 requests/minute
Previous window count = 5
Current window count = 3
Overlap with previous window = 70%

estimatedCount = 3 + 5 * 0.7 = 6.5
```

### Pros

- Memory efficient.
- Smoother than fixed window.
- Good approximation.

### Cons

- Not perfectly accurate.

---

## 7. Algorithm Comparison

| Algorithm | Accuracy | Memory | Burst Support | Complexity | Use Case |
|---|---:|---:|---:|---:|---|
| Token Bucket | Medium-High | Low | Yes | Low | Public APIs, bursty traffic |
| Leaking Bucket | Medium | Low | No | Medium | Stable processing rate |
| Fixed Window | Low-Medium | Very Low | Poor boundary handling | Very Low | Simple systems |
| Sliding Window Log | Very High | High | Controlled | Medium | Strict limits |
| Sliding Window Counter | High Approximation | Low | Some | Medium | Large distributed APIs |

---

## 8. Redis-Based Architecture

Redis is commonly used because it is:

- fast
- in-memory
- supports TTL
- supports atomic operations
- supports Lua scripts
- works well in distributed environments

```text
Client
  |
  v
Load Balancer
  |
  v
Rate Limiter Service --------------+
  |                                |
  | check/increment counter         |
  v                                v
API Server                    Redis Cluster
```

---

## 9. Rate Limiting Rules

Rules are usually stored in config files, databases, or configuration services.

Example rule:

```yaml
domain: auth
descriptors:
  - key: auth_type
    value: login
    rate_limit:
      unit: minute
      requests_per_unit: 5
```

Another example:

```yaml
domain: messaging
descriptors:
  - key: message_type
    value: marketing
    rate_limit:
      unit: day
      requests_per_unit: 5
```

Rule loading flow:

```text
Rules on disk/config service
          |
          v
       Workers
          |
          v
   In-memory rule cache
          |
          v
    Rate limiter middleware
```

---

## 10. Detailed Design

```text
                      +----------------+
                      | Rule Store     |
                      | YAML / DB      |
                      +----------------+
                              |
                              v
                       +-------------+
                       | Rule Worker |
                       +-------------+
                              |
                              v
+--------+        +---------------------+        +-------------+
| Client | -----> | Rate Limiter        | -----> | API Servers |
+--------+        | Middleware/Gateway  |        +-------------+
                  +---------------------+
                     |             |
                     |             |
                     v             v
              +-----------+   +----------------+
              | Redis     |   | Message Queue  |
              | Counters  |   | Optional Retry |
              +-----------+   +----------------+
                     |
                     v
             429 Too Many Requests
```

Request flow:

1. Request enters rate limiter.
2. Limiter loads matching rule from local cache.
3. Limiter checks Redis counter/tokens.
4. If allowed, request is forwarded.
5. If rejected, return `429` or enqueue for later processing.

---

## 11. Response Headers

When returning responses, include useful rate limit headers.

```http
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 42
X-RateLimit-Retry-After: 18
```

Meaning:

| Header | Meaning |
|---|---|
| `X-RateLimit-Limit` | Max allowed requests in window |
| `X-RateLimit-Remaining` | Remaining requests before throttling |
| `X-RateLimit-Retry-After` | Seconds to wait before retrying |

Rejected response:

```http
HTTP/1.1 429 Too Many Requests
X-RateLimit-Retry-After: 30
```

---

# 12. Small Java Code References

## 12.1 Fixed Window Rate Limiter in Java

Simple in-memory implementation for learning and interviews.

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowRateLimiter {
    private final int limit;
    private final long windowSizeMillis;
    private final ConcurrentHashMap<String, Window> userWindows = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int limit, long windowSizeMillis) {
        this.limit = limit;
        this.windowSizeMillis = windowSizeMillis;
    }

    public boolean allowRequest(String userId) {
        long now = System.currentTimeMillis();

        Window window = userWindows.compute(userId, (key, oldWindow) -> {
            if (oldWindow == null || now - oldWindow.windowStart >= windowSizeMillis) {
                return new Window(now, new AtomicInteger(1));
            }

            oldWindow.counter.incrementAndGet();
            return oldWindow;
        });

        return window.counter.get() <= limit;
    }

    private static class Window {
        long windowStart;
        AtomicInteger counter;

        Window(long windowStart, AtomicInteger counter) {
            this.windowStart = windowStart;
            this.counter = counter;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(3, 1000);

        String userId = "user-123";

        for (int i = 1; i <= 5; i++) {
            System.out.println("Request " + i + ": " + limiter.allowRequest(userId));
        }
    }
}
```

Expected behavior:

```text
Request 1: true
Request 2: true
Request 3: true
Request 4: false
Request 5: false
```

Interview note:

- This is simple but has the fixed-window boundary problem.
- In production, counters should be stored in Redis, not local memory.

---

## 12.2 Token Bucket Rate Limiter in Java

Good for allowing controlled bursts.

```java
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketRateLimiter {
    private final int capacity;
    private final double refillTokensPerSecond;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(int capacity, double refillTokensPerSecond) {
        this.capacity = capacity;
        this.refillTokensPerSecond = refillTokensPerSecond;
    }

    public boolean allowRequest(String userId) {
        long now = System.nanoTime();

        Bucket bucket = buckets.computeIfAbsent(userId, key -> new Bucket(capacity, now));

        synchronized (bucket) {
            refill(bucket, now);

            if (bucket.tokens >= 1) {
                bucket.tokens -= 1;
                return true;
            }

            return false;
        }
    }

    private void refill(Bucket bucket, long now) {
        double elapsedSeconds = (now - bucket.lastRefillTime) / 1_000_000_000.0;
        double tokensToAdd = elapsedSeconds * refillTokensPerSecond;

        bucket.tokens = Math.min(capacity, bucket.tokens + tokensToAdd);
        bucket.lastRefillTime = now;
    }

    private static class Bucket {
        double tokens;
        long lastRefillTime;

        Bucket(double tokens, long lastRefillTime) {
            this.tokens = tokens;
            this.lastRefillTime = lastRefillTime;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(3, 1);
        String userId = "user-123";

        for (int i = 1; i <= 5; i++) {
            System.out.println("Request " + i + ": " + limiter.allowRequest(userId));
        }

        Thread.sleep(1500);
        System.out.println("After refill: " + limiter.allowRequest(userId));
    }
}
```

Interview note:

- `capacity = 3` allows a burst of 3 requests.
- `refillTokensPerSecond = 1` means one token is added per second.
- In distributed production systems, this logic should be atomic in Redis, often using Lua script.

---

## 12.3 Redis Fixed Window Pseudocode

```java
boolean allowRequest(String key, int limit, int windowSeconds) {
    long current = redis.incr(key);

    if (current == 1) {
        redis.expire(key, windowSeconds);
    }

    return current <= limit;
}
```

Important issue:

```text
INCR and EXPIRE should ideally be atomic.
Use Redis Lua script to avoid race conditions.
```

---

## 13. Redis Lua Script Idea

Atomic operation:

```lua
local current = redis.call('INCR', KEYS[1])

if current == 1 then
  redis.call('EXPIRE', KEYS[1], ARGV[1])
end

if current > tonumber(ARGV[2]) then
  return 0
else
  return 1
end
```

Where:

- `KEYS[1]` = rate limit key
- `ARGV[1]` = window size in seconds
- `ARGV[2]` = request limit

---

## 14. Distributed Rate Limiter Challenges

## 14.1 Race Conditions

Bad flow:

```text
Request A reads counter = 3
Request B reads counter = 3
Request A writes counter = 4
Request B writes counter = 4

Expected counter = 5
Actual counter = 4
```

Solutions:

- Redis atomic `INCR`.
- Redis Lua script.
- Redis sorted sets for sliding window log.
- Avoid distributed locks if possible because they add latency.

---

## 14.2 Synchronization Across Rate Limiter Servers

Bad design:

```text
Client A ---> Rate Limiter 1 ---> Local counter
Client A ---> Rate Limiter 2 ---> Different local counter
```

This fails because counters are not shared.

Better design:

```text
Client A ---> Rate Limiter 1 ---+
                                 +---> Shared Redis
Client A ---> Rate Limiter 2 ---+
```

---

## 15. Fault Tolerance

Rate limiter failures should not bring down the whole system.

Two common modes:

### Fail Open

If Redis/rate limiter fails, allow requests.

Pros:

- Better availability.

Cons:

- Backend may be overloaded.

Best for:

- Consumer-facing APIs.
- Availability-first systems.

### Fail Closed

If Redis/rate limiter fails, reject requests.

Pros:

- Protects backend strictly.

Cons:

- Valid users may be blocked.

Best for:

- Payment APIs.
- Security-sensitive APIs.

---

## 16. Performance Optimization

Use:

- Redis cluster.
- Local rule cache.
- Lua scripts for atomicity.
- Edge rate limiting near users.
- Shard Redis keys by user ID or API key.
- Avoid database calls in rate limiter hot path.
- Keep rate limiter stateless except shared Redis.

Global architecture:

```text
Users
  |
  v
Nearest Edge / CDN / API Gateway
  |
  v
Regional Rate Limiter
  |
  v
Regional Redis Cluster
  |
  v
API Servers
```

---

## 17. Monitoring Metrics

Track:

| Metric | Why it matters |
|---|---|
| Allowed request count | Normal traffic volume |
| Rejected request count | Detect abuse or overly strict rules |
| Redis latency | Rate limiter performance |
| 429 response rate | Client throttling visibility |
| Per-user/IP rejection | Abuse detection |
| API endpoint throttling | Rule tuning |
| Queue size | Delayed request pressure |

Important alerts:

- Sudden spike in rejected requests.
- Redis latency increase.
- Rate limiter error rate increase.
- One customer/API key consuming unusual traffic.

---

## 18. Hard vs Soft Rate Limiting

### Hard Limit

Requests must never exceed the threshold.

Example:

```text
Max 100 requests/minute. The 101st request is rejected.
```

### Soft Limit

Requests may exceed the threshold briefly.

Example:

```text
Allow short burst, but enforce average rate over time.
```

Token bucket is good for soft/bursty limits.

---

## 19. Rate Limiting by Layer

| Layer | Example |
|---|---|
| Layer 3 - Network | IP-based limit with firewall/Iptables |
| Layer 4 - Transport | TCP connection limiting |
| Layer 7 - Application | HTTP API rate limiting |

This design focuses mostly on **Layer 7 HTTP API rate limiting**.

---

## 20. Client Best Practices

Clients should:

- Cache responses locally.
- Respect rate limit headers.
- Use exponential backoff.
- Avoid aggressive retries.
- Add jitter to retry delays.
- Handle `429` gracefully.

Example retry logic:

```text
Request -> 429 received -> read Retry-After -> wait -> retry
```

---

## 21. FAANG Interview Talking Points

Mention these during discussion:

- Start simple with Redis fixed window.
- Discuss boundary problem.
- Move to token bucket or sliding window counter for production.
- Store counters in Redis, not database.
- Use Lua script for atomic operations.
- Keep rate limiter stateless.
- Use shared Redis for distributed correctness.
- Return `429` with headers.
- Support rule configs per endpoint/user/IP/API key.
- Add monitoring and alerting.
- Discuss fail-open vs fail-closed tradeoff.
- Use API gateway if microservices already exist.
- Use edge rate limiting for global systems.

---

## 22. Final Design Summary

```text
                    +-------------------+
                    | Rule Config Store |
                    +-------------------+
                              |
                              v
                       +--------------+
                       | Rule Workers |
                       +--------------+
                              |
                              v
+--------+       +------------------------+       +-------------+
| Client | ----> | API Gateway /          | ----> | API Servers |
+--------+       | Rate Limiter           |       +-------------+
                 +------------------------+
                    |       |        |
                    |       |        +--> 429 Too Many Requests
                    |       |
                    |       +-----------> Optional Queue
                    |
                    v
              +-------------+
              | Redis       |
              | Counters    |
              +-------------+
```

Recommended production approach:

```text
Algorithm: Token Bucket or Sliding Window Counter
Storage: Redis Cluster
Atomicity: Lua script
Placement: API Gateway or middleware
Failure mode: Depends on business criticality
Monitoring: 429s, Redis latency, allowed/rejected counts
```

---

## 23. Quick Interview Answer Template

> I would place the rate limiter at the API gateway or middleware layer. The limiter loads rules from a config store and keeps them in local memory for fast lookup. For each request, it builds a key using user ID, IP, API key, or endpoint. It checks and updates counters in Redis using atomic operations or Lua scripts. If the request is allowed, it forwards it to API servers. If not, it returns HTTP 429 with rate limit headers. For production, I would prefer token bucket or sliding window counter because fixed window has boundary issues. The system is stateless, horizontally scalable, and uses shared Redis for distributed synchronization.

---

## 24. Common Follow-Up Questions

### Q: Why not store counters in SQL?

Because SQL is slower for high-frequency counters and does not naturally support cheap TTL-based expiration at massive scale.

### Q: Why Redis?

Redis is fast, in-memory, supports TTL, atomic increments, sorted sets, clustering, and Lua scripts.

### Q: What happens if Redis is down?

Choose fail-open or fail-closed depending on business need.

### Q: How do you avoid race conditions?

Use Redis atomic commands or Lua scripts.

### Q: How do you support multiple rate limiter servers?

Keep rate limiter servers stateless and store shared counters in Redis.

### Q: Which algorithm would you choose?

For most APIs, token bucket is a strong default because it is simple, efficient, and supports bursts. For stricter limits, use sliding window log or sliding window counter.
