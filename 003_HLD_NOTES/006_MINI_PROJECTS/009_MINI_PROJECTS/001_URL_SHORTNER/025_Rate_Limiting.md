# 025_Rate_Limiting.md
# MiniURLShortener — Rate Limiting

> Core mental model: **Rate limiting is a traffic safety valve. It protects your system from too many requests from the same client, user, IP, API key, or route within a time window. For MiniURLShortener, rate limiting protects expensive and abuse-prone APIs like create-short-url while keeping redirect traffic fast and resilient.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What Rate Limiting Protects](#4-what-rate-limiting-protects)
- [5. Where Rate Limiting Fits](#5-where-rate-limiting-fits)
- [6. Create API vs Redirect API](#6-create-api-vs-redirect-api)
- [7. Rate Limit Dimensions](#7-rate-limit-dimensions)
- [8. Fixed Window Mental Model](#8-fixed-window-mental-model)
- [9. Sliding Window Mental Model](#9-sliding-window-mental-model)
- [10. Token Bucket Mental Model](#10-token-bucket-mental-model)
- [11. Which Algorithm For MiniURLShortener](#11-which-algorithm-for-miniurlshortener)
- [12. Redis Rate Limiter Design](#12-redis-rate-limiter-design)
- [13. Redis Key Design](#13-redis-key-design)
- [14. 429 Response Contract](#14-429-response-contract)
- [15. Rate Limiting Flow](#15-rate-limiting-flow)
- [16. Spring Boot Filter vs Interceptor](#16-spring-boot-filter-vs-interceptor)
- [17. Java Implementation Sketch](#17-java-implementation-sketch)
- [18. Redis Token Bucket Pseudocode](#18-redis-token-bucket-pseudocode)
- [19. Step-by-Step Dry Runs](#19-step-by-step-dry-runs)
- [20. Internal Execution Walkthrough](#20-internal-execution-walkthrough)
- [21. Distributed Rate Limiting](#21-distributed-rate-limiting)
- [22. Failure Strategy](#22-failure-strategy)
- [23. Testing Strategy](#23-testing-strategy)
- [24. Metrics And Observability](#24-metrics-and-observability)
- [25. Production Failure Stories](#25-production-failure-stories)
- [26. Debugging Mindset](#26-debugging-mindset)
- [27. Common Mistakes](#27-common-mistakes)
- [28. Interview-Ready Explanation](#28-interview-ready-explanation)
- [29. Senior Engineer Checklist](#29-senior-engineer-checklist)
- [30. One-Page Cheat Sheet](#30-one-page-cheat-sheet)
- [31. One Picture To Remember](#31-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener has public APIs:

```text
POST /api/v1/urls
GET /{shortCode}
```

The create API is dangerous if uncontrolled.

Attackers or buggy clients can send:

```text
10,000 create requests/sec
millions of invalid URLs
custom alias brute force
spam short links
phishing URLs
oversized payloads
```

Without rate limiting:

```text
application threads saturate
PostgreSQL writes increase
Redis cache churn increases
Hikari pool fills
logs explode
abuse content grows
cost increases
```

ASCII:

```text
Bad client
   |
   | 10000 req/s
   v
+-------------------+
| Spring Boot       |
+---------+---------+
          |
          v
+-------------------+
| PostgreSQL        |
| writes overloaded |
+-------------------+
```

With rate limiting:

```text
bad client gets 429 Too Many Requests
system stays healthy
legitimate clients continue
```

ASCII:

```text
Bad client
   |
   v
+-------------------+
| Rate Limiter      |
| allow some        |
| reject excess     |
+----+---------+----+
     |         |
     v         v
 allowed     429
```

Production memory:

```text
Rate limiting is controlled rejection before expensive work.
```

---

## 2. The One Core Mental Model

The core mental model:

```text
EVERY CLIENT GETS A LIMITED REQUEST BUDGET
```

A client may be:

```text
IP address
authenticated user
API key
tenant
route
device
```

A budget may be:

```text
60 requests per minute
10 creates per minute
1000 redirects per minute
```

ASCII:

```text
Client Budget

User/IP: 10.10.10.10

Bucket:
+----------------------+
| tokens: 10           |
+----------------------+

Each request consumes 1 token.

If token exists:
    allow

If no token:
    reject 429
```

One-line memory:

```text
Rate limiting answers: has this caller spent their allowed request budget?
```

Good rate limiting happens before expensive work.

For create API:

```text
Rate limit before validation, DB insert, alias generation, or security checks.
```

For redirect API:

```text
Usually higher limits or edge/CDN protection because redirect is public and high-volume.
```

---

## 3. Problem Statement

Design rate limiting for MiniURLShortener.

It must support:

```text
1. Limit create-short-url abuse.
2. Return HTTP 429 for excessive requests.
3. Work across multiple Spring Boot pods.
4. Use Redis as shared rate limit state.
5. Support IP-based limiting initially.
6. Support user/API-key limiting later.
7. Add route-specific rules.
8. Avoid hitting DB for rejected traffic.
9. Expose metrics for allowed/rejected/error.
10. Have safe Redis failure behavior.
```

Initial target:

```text
POST /api/v1/urls:
    10 requests per minute per IP

GET /{shortCode}:
    no strict app-level limit initially
    protect later with CDN/WAF/IP rules
```

Response for excessive create requests:

```http
429 Too Many Requests
Retry-After: 30
```

Body:

```json
{
  "code": "RATE_LIMIT_EXCEEDED",
  "message": "Too many requests. Please retry later."
}
```

---

## 4. What Rate Limiting Protects

Rate limiting protects:

```text
CPU
threads
DB connections
DB writes
Redis
external APIs
logs
money
abuse moderation
user experience
```

For MiniURLShortener create API, expensive operations include:

```text
JSON parsing
validation
URL parsing
custom alias check
short code generation
PostgreSQL insert
unique constraint handling
cache write
audit logging
future malware scan
```

ASCII:

```text
Without limiter:

Bad traffic -> validation -> service -> DB -> logs -> errors


With limiter:

Bad traffic -> limiter -> 429

Good traffic -> service -> DB
```

Rate limiting is not only performance.

It is also security and abuse control.

Examples:

```text
bot creates phishing links
attacker brute-forces aliases
buggy client retries aggressively
script sends invalid URLs forever
competitor floods create API
```

Rule:

```text
Reject cheap before doing expensive.
```

---

## 5. Where Rate Limiting Fits

Request flow:

```text
Client
  |
  v
Load Balancer / Gateway / CDN / WAF
  |
  v
Spring Boot Filter
  |
  v
Controller
  |
  v
Service
  |
  v
Database
```

Rate limiting can exist at multiple layers:

```text
CDN/WAF:
    blocks large attacks close to edge

API Gateway:
    centralized app-level policy

Spring Boot:
    service-specific fine-grained rules

Redis:
    shared counter/bucket state
```

ASCII:

```text
Client
  |
  v
+------------------+
| CDN / WAF        |
| coarse limits    |
+------------------+
  |
  v
+------------------+
| API Gateway      |
| tenant/API limit |
+------------------+
  |
  v
+------------------+
| Spring Boot      |
| route rules      |
+------------------+
  |
  v
+------------------+
| Service/DB       |
+------------------+
```

For MiniURLShortener learning project:

```text
Implement Spring Boot + Redis limiter.
```

Production later:

```text
also add gateway/CDN/WAF limits.
```

---

## 6. Create API vs Redirect API

Not all endpoints need same limits.

### Create API

```http
POST /api/v1/urls
```

Characteristics:

```text
write-heavy
abuse-prone
DB insert
validation cost
security risk
usually authenticated later
```

Limit aggressively.

Example:

```text
anonymous IP:
    10 creates/min

authenticated user:
    100 creates/hour

premium user:
    higher
```

### Redirect API

```http
GET /{shortCode}
```

Characteristics:

```text
public
high volume
read-heavy
cacheable
business-critical
often shared widely
```

If you rate limit redirect too aggressively:

```text
viral legitimate link may break
```

Use different strategy:

```text
CDN caching
bot detection
WAF
IP-based abuse limits
hot-key protection
negative caching
```

ASCII:

```text
Create API:
    strict rate limit

Redirect API:
    cache + edge protection + abuse-specific limit
```

Initial chapter focus:

```text
Rate limit create API first.
```

---

## 7. Rate Limit Dimensions

A rate limit key identifies who is being limited.

Possible dimensions:

```text
IP address
user ID
API key
tenant ID
route
HTTP method
country/region
device ID
```

Examples:

```text
rate:create:ip:10.1.2.3
rate:create:user:42
rate:redirect:ip:10.1.2.3
rate:api-key:abc
```

ASCII:

```text
Same route, different clients:

POST /api/v1/urls

IP 1.1.1.1 -> own bucket
IP 2.2.2.2 -> own bucket
User 42    -> own bucket
```

Initial MiniURLShortener:

```text
anonymous create API:
    key = IP + route
```

Later:

```text
authenticated:
    key = userId + route
```

Important:

```text
IP-based limiting can punish many users behind NAT.
User/API-key limiting is better when authentication exists.
```

For now, IP is simple and useful.

---

## 8. Fixed Window Mental Model

Fixed window counts requests in a fixed time range.

Example:

```text
limit = 10 requests/min
window = 12:00:00 to 12:00:59
```

Flow:

```text
Request increments counter.
If counter <= 10, allow.
Else reject.
Counter expires at window end.
```

ASCII:

```text
12:00 minute window

+--------------------------------+
| request count: 1 2 3 ... 10    |
+--------------------------------+

11th request -> reject
```

Redis key:

```text
rate:create:ip:1.2.3.4:202606231200
```

Pros:

```text
simple
fast
easy with Redis INCR + EXPIRE
```

Cons:

```text
boundary burst problem
```

Boundary burst:

```text
10 requests at 12:00:59
10 requests at 12:01:00
```

That is:

```text
20 requests in 2 seconds
```

Even though limit says 10/min.

Fixed window is simple but less smooth.

---

## 9. Sliding Window Mental Model

Sliding window counts requests in the last N seconds from current time.

Example:

```text
limit = 10 per 60 seconds
```

At time `12:01:30`, count requests from:

```text
12:00:30 to 12:01:30
```

ASCII:

```text
time ---------------------------->

          last 60 seconds
        [-------------------]
              now ^
```

Implementation often uses Redis sorted set:

```text
ZADD timestamp requestId
ZREMRANGEBYSCORE old timestamps
ZCARD count current window
```

Pros:

```text
more accurate
prevents boundary burst
```

Cons:

```text
more Redis operations
more memory
more complex
```

Good for:

```text
strict fairness
security-sensitive APIs
paid API quotas
```

For MiniURLShortener create API:

```text
sliding window is good but token bucket is also practical.
```

---

## 10. Token Bucket Mental Model

Token bucket gives each client a bucket of tokens.

Tokens refill over time.

Each request consumes a token.

If bucket has token:

```text
allow
```

If bucket empty:

```text
reject 429
```

ASCII:

```text
Token Bucket

        refill rate
            |
            v
      +-----------+
      | tokens: 5 |
      +-----------+
          |
          | request consumes 1
          v
      +-----------+
      | tokens: 4 |
      +-----------+
```

Parameters:

```text
capacity:
    max burst size

refill rate:
    tokens added per second/minute
```

Example:

```text
capacity = 10
refill = 10 tokens/min
```

Allows:

```text
small bursts
smooth long-term rate
```

Pros:

```text
handles bursts naturally
widely used
good mental model
```

Cons:

```text
requires atomic update of token count and timestamp
```

Redis Lua is commonly used for atomicity.

For MiniURLShortener:

```text
token bucket is a strong default.
```

---

## 11. Which Algorithm For MiniURLShortener

Recommended practical progression:

```text
Level 1:
    Fixed window Redis INCR
    easiest

Level 2:
    Token bucket with Redis Lua
    better production shape

Level 3:
    Gateway/CDN/WAF + app-level token bucket
    production hardened
```

For this chapter:

```text
Teach token bucket as mental model.
Provide simple Redis implementation sketch.
```

Endpoint policy:

```text
POST /api/v1/urls:
    token bucket
    capacity = 10
    refill = 10/min per IP

GET /{shortCode}:
    no strict create-style limiter initially
    protect with CDN/WAF/rate limit abusive IPs later
```

ASCII:

```text
Create API:

IP 1.2.3.4
   |
   v
Bucket capacity 10
refill 10/min
   |
   +-- token available -> allow create
   |
   +-- empty -> 429
```

Why this fits:

```text
create API is expensive and abuse-prone
token bucket allows small natural bursts
long-term rate is controlled
```

---

## 12. Redis Rate Limiter Design

Redis is used because multiple app pods need shared state.

Without Redis:

```text
Pod 1 has local counter
Pod 2 has local counter
Pod 3 has local counter
```

A client can bypass limits by hitting different pods.

ASCII:

```text
Bad local-only limiter:

Client
  |
  +--> Pod 1 counter = 5
  +--> Pod 2 counter = 5
  +--> Pod 3 counter = 5

Actually allowed = 15
```

With Redis:

```text
all pods use same bucket key
```

ASCII:

```text
Good distributed limiter:

Client
  |
  +--> Pod 1 --+
  +--> Pod 2 --+--> Redis bucket
  +--> Pod 3 --+
```

Redis must update bucket atomically.

Atomic means:

```text
read tokens
refill
consume
write back
```

happens as one operation.

Use:

```text
Lua script
```

or a library.

---

## 13. Redis Key Design

Rate limit key format:

```text
rate:{route}:{dimension}:{value}
```

Examples:

```text
rate:create:ip:1.2.3.4
rate:create:user:42
rate:redirect:ip:1.2.3.4
```

For MiniURLShortener create API:

```text
rate:create:ip:{clientIp}
```

ASCII:

```text
Redis keys:

rate:create:ip:10.0.0.1 -> token bucket state
rate:create:ip:10.0.0.2 -> token bucket state
rate:create:user:42     -> token bucket state
```

Value may store:

```text
tokens
lastRefillTimestamp
```

Example JSON/hash:

```text
tokens = 7
lastRefillMillis = 1782200000000
```

TTL:

```text
expire inactive buckets
```

Example:

```text
bucket key TTL = 2 * time to fully refill
```

Why TTL?

```text
avoid Redis memory growing forever with old IPs
```

---

## 14. 429 Response Contract

When rate limit is exceeded, return:

```http
429 Too Many Requests
```

Useful headers:

```http
Retry-After: 30
X-RateLimit-Limit: 10
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1782200060
```

Response body:

```json
{
  "timestamp": "2026-06-23T10:00:00Z",
  "status": 429,
  "error": "Too Many Requests",
  "code": "RATE_LIMIT_EXCEEDED",
  "message": "Too many requests. Please retry later.",
  "path": "/api/v1/urls"
}
```

ASCII:

```text
Request exceeds budget
        |
        v
Rate limiter rejects
        |
        v
429 + retry info
```

Why stable code matters:

```text
clients can handle RATE_LIMIT_EXCEEDED programmatically
```

For example:

```text
wait and retry
show user-friendly message
stop aggressive retry loop
```

Never return 500 for rate limit.

Rate limiting is expected behavior.

---

## 15. Rate Limiting Flow

Create API flow:

```text
POST /api/v1/urls
   |
   v
RateLimiterFilter
   |
   +-- allowed -> controller -> service -> DB
   |
   +-- rejected -> 429
```

ASCII:

```text
Client
  |
  v
+----------------------+
| RateLimiterFilter    |
+----------+-----------+
           |
     +-----+-----+
     |           |
 allowed      rejected
     |           |
     v           v
Controller     429
     |
     v
Service
     |
     v
PostgreSQL
```

Important:

```text
Rate limiter should run before expensive validation/service work.
```

But it should still be after basic security/gateway layers.

For authenticated APIs later:

```text
authenticate first
then rate limit by user ID
```

For anonymous IP limit:

```text
can limit early by IP
```

---

## 16. Spring Boot Filter vs Interceptor

### Filter

Runs early in servlet chain.

Good for:

```text
cross-cutting request control
auth pre-processing
rate limiting
correlation ID
logging
```

### HandlerInterceptor

Runs after handler mapping.

Good for:

```text
route-aware logic
controller-level checks
```

For rate limiting:

```text
Filter is common for early rejection.
Interceptor is also useful when route info matters.
```

MiniURLShortener simple version:

```text
OncePerRequestFilter
```

It can inspect:

```text
HTTP method
URI path
client IP
```

ASCII:

```text
HTTP request
  |
  v
Filter chain
  |
  v
DispatcherServlet
  |
  v
Controller
```

Use filter for:

```text
POST /api/v1/urls
```

only.

Do not rate limit all endpoints blindly.

---

## 17. Java Implementation Sketch

### Rate Limit Result

```java
package com.miniurl.shortener.ratelimit;

public class RateLimitResult {

    private final boolean allowed;
    private final long remainingTokens;
    private final long retryAfterSeconds;

    private RateLimitResult(boolean allowed, long remainingTokens, long retryAfterSeconds) {
        this.allowed = allowed;
        this.remainingTokens = remainingTokens;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public static RateLimitResult allowed(long remainingTokens) {
        return new RateLimitResult(true, remainingTokens, 0);
    }

    public static RateLimitResult rejected(long retryAfterSeconds) {
        return new RateLimitResult(false, 0, retryAfterSeconds);
    }

    public boolean isAllowed() {
        return allowed;
    }

    public long getRemainingTokens() {
        return remainingTokens;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
```

### Rate Limiter Interface

```java
package com.miniurl.shortener.ratelimit;

public interface RateLimiter {

    RateLimitResult allow(String key, RateLimitPolicy policy);
}
```

### Policy

```java
package com.miniurl.shortener.ratelimit;

import java.time.Duration;

public class RateLimitPolicy {

    private final long capacity;
    private final long refillTokens;
    private final Duration refillPeriod;

    public RateLimitPolicy(long capacity, long refillTokens, Duration refillPeriod) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriod = refillPeriod;
    }

    public long getCapacity() {
        return capacity;
    }

    public long getRefillTokens() {
        return refillTokens;
    }

    public Duration getRefillPeriod() {
        return refillPeriod;
    }
}
```

### Filter Sketch

```java
package com.miniurl.shortener.ratelimit;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class CreateUrlRateLimitFilter extends OncePerRequestFilter {

    private final RateLimiter rateLimiter;

    public CreateUrlRateLimitFilter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (!isCreateUrlRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = extractClientIp(request);
        String key = "rate:create:ip:" + clientIp;

        RateLimitPolicy policy = new RateLimitPolicy(
                10,
                10,
                Duration.ofMinutes(1)
        );

        RateLimitResult result = rateLimiter.allow(key, policy);

        if (!result.isAllowed()) {
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(result.getRetryAfterSeconds()));
            response.setContentType("application/json");
            response.getWriter().write("""
                    {
                      "code": "RATE_LIMIT_EXCEEDED",
                      "message": "Too many requests. Please retry later."
                    }
                    """);
            return;
        }

        response.setHeader("X-RateLimit-Remaining", String.valueOf(result.getRemainingTokens()));
        filterChain.doFilter(request, response);
    }

    private boolean isCreateUrlRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && "/api/v1/urls".equals(request.getRequestURI());
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
```

Important:

```text
In production, trust X-Forwarded-For only from trusted proxies.
```

---

## 18. Redis Token Bucket Pseudocode

Token bucket state:

```text
tokens
lastRefillTime
```

Atomic logic:

```text
now = current time
elapsed = now - lastRefillTime
tokensToAdd = elapsed * refillRate
tokens = min(capacity, tokens + tokensToAdd)

if tokens >= 1:
    tokens = tokens - 1
    allow
else:
    reject
```

ASCII:

```text
Bucket before request:

tokens = 3
capacity = 10

request arrives
   |
   v
refill based on elapsed time
   |
   v
tokens = min(10, tokens + refill)
   |
   +-- tokens >= 1 -> consume 1 -> allow
   |
   +-- tokens = 0  -> reject 429
```

Redis Lua is used because these steps must be atomic.

If not atomic:

```text
two pods may read same token count
both allow
limit is exceeded
```

Pseudo Lua flow:

```text
GET bucket
calculate refill
if enough token:
    decrement
    SET bucket
    return allowed
else:
    SET bucket
    return rejected + retryAfter
```

Production note:

```text
Use a tested library or carefully test Lua script.
```

For learning, the mental model matters more than perfect Lua syntax.

---

## 19. Step-by-Step Dry Runs

### Dry Run 1: Allowed Request

Policy:

```text
capacity = 10
refill = 10/min
current tokens = 5
```

Request:

```http
POST /api/v1/urls
```

Flow:

```text
1. Filter identifies create API.
2. Key = rate:create:ip:1.2.3.4.
3. Redis bucket has 5 tokens.
4. Request consumes 1 token.
5. Remaining = 4.
6. Request proceeds to controller.
```

ASCII:

```text
tokens 5 -> consume 1 -> tokens 4 -> allow
```

---

### Dry Run 2: Rejected Request

State:

```text
tokens = 0
next refill in 6 seconds
```

Flow:

```text
1. Request enters filter.
2. Redis bucket says no token.
3. Filter returns 429.
4. Controller is not called.
5. DB is not touched.
```

ASCII:

```text
tokens 0
   |
   v
429 Too Many Requests
```

Result:

```text
expensive work avoided
```

---

### Dry Run 3: Natural Burst

Policy:

```text
capacity = 10
refill = 10/min
```

Client idle for long time.

Bucket fills to:

```text
10 tokens
```

Then client sends 5 quick requests.

Flow:

```text
request 1 -> tokens 9 allow
request 2 -> tokens 8 allow
request 3 -> tokens 7 allow
request 4 -> tokens 6 allow
request 5 -> tokens 5 allow
```

This is allowed because token bucket supports small bursts.

---

### Dry Run 4: Multiple Pods

Client sends requests through load balancer.

```text
request 1 -> pod A
request 2 -> pod B
request 3 -> pod C
```

All pods use Redis key:

```text
rate:create:ip:1.2.3.4
```

Flow:

```text
Pod A consumes token from Redis.
Pod B consumes token from same Redis bucket.
Pod C consumes token from same Redis bucket.
```

Result:

```text
limit works across pods
```

---

### Dry Run 5: Redis Failure

Redis timeout.

Two possible strategies:

```text
fail-open:
    allow request

fail-closed:
    reject request
```

For create API:

```text
fail-closed may protect system better
fail-open may preserve availability
```

Balanced approach:

```text
anonymous create API -> fail-closed or degraded strict local limit
redirect API -> fail-open or edge protection
```

For MiniURLShortener learning:

```text
create API fail-closed with 503 or 429-like controlled response is safer
```

But be explicit in design.

---

## 20. Internal Execution Walkthrough

Create request:

```text
1. HTTP request enters Tomcat.
2. Filter chain starts.
3. CreateUrlRateLimitFilter checks method/path.
4. Extracts client identity.
5. Builds Redis key.
6. Calls RateLimiter.allow().
7. RateLimiter executes Redis token bucket logic.
8. If allowed:
       add headers
       continue filter chain
9. DispatcherServlet routes to controller.
10. Controller calls service.
11. Service validates and writes DB.
12. If rejected:
       filter writes 429
       request stops before controller
```

ASCII:

```text
Client
  |
  v
Filter
  |
  +-- allowed -> Controller -> Service -> DB
  |
  +-- rejected -> 429
```

Important:

```text
Rejected requests should not reach controller, service, DB, or Kafka.
```

This is how rate limiting saves resources.

---

## 21. Distributed Rate Limiting

A single Spring Boot pod can limit locally.

But production has multiple pods.

Local limiter problem:

```text
limit = 10/min per pod
pods = 5
actual limit = 50/min
```

Distributed limiter:

```text
all pods share Redis state
```

ASCII:

```text
Client
  |
  v
Load Balancer
  |
  +--> Pod A --+
  +--> Pod B --+--> Redis rate bucket
  +--> Pod C --+
```

Requirements:

```text
shared store
atomic updates
low latency
timeouts
metrics
```

Redis is common.

Alternatives:

```text
API gateway rate limiter
Envoy
Nginx
Cloudflare/WAF
Kong
AWS API Gateway
```

Best production design:

```text
edge/gateway coarse limit
app Redis fine-grained limit
```

---

## 22. Failure Strategy

Rate limiter failure must be explicit.

Common choices:

```text
fail-open:
    allow request when limiter unavailable

fail-closed:
    reject request when limiter unavailable
```

Fail-open:

```text
better availability
risk abuse/overload
```

Fail-closed:

```text
better protection
risk blocking legitimate users
```

Endpoint-specific strategy:

```text
Create API:
    fail-closed or strict degraded mode

Redirect API:
    usually fail-open because redirect availability matters
```

ASCII:

```text
Limiter unavailable
       |
       +-- create API -> protect system
       |
       +-- redirect API -> preserve availability
```

For MiniURLShortener create API:

```text
If Redis limiter fails:
    return 503 Service Unavailable
    or 429 with safe message
```

Better later:

```text
local emergency limiter fallback
```

Rule:

```text
Do not silently ignore rate limiter failures without metrics.
```

---

## 23. Testing Strategy

Test rate limiting like core logic.

### Unit Tests

```text
allowed when tokens available
rejected when empty
refill after time
remaining tokens correct
retryAfter calculated
```

### Filter Tests

```text
POST /api/v1/urls hits limiter
GET /abc123 bypasses create limiter
rejected request returns 429
allowed request reaches controller
headers are set
```

### Integration Tests

Use Redis Testcontainers.

```text
1. Send 10 create requests.
2. All allowed.
3. Send 11th.
4. Assert 429.
```

### Multi-Pod Simulation

Harder locally, but can simulate:

```text
same Redis key
multiple service instances or repeated calls
```

### Failure Tests

```text
Redis timeout
limiter exception
fail-open/fail-closed behavior
```

Testing checklist:

```text
allow
reject
refill
headers
route matching
IP extraction
Redis failure
concurrent requests
```

---

## 24. Metrics And Observability

Rate limiter metrics:

```text
rate_limit.allowed
rate_limit.rejected
rate_limit.redis_error
rate_limit.redis_latency
rate_limit.remaining_tokens
rate_limit.retry_after
```

Dimensions:

```text
route
method
limit_type
decision
```

Example:

```text
route=create_url
decision=allowed/rejected
```

ASCII:

```text
Request
  |
  +-- allowed counter
  |
  +-- rejected counter
  |
  +-- Redis error counter
```

Alerts:

```text
rejections spike
Redis limiter errors spike
create API traffic spikes
single IP dominates traffic
429 rate too high after deployment
```

Dashboard should show:

```text
create RPS
429 rate
DB insert rate
Hikari active/pending
Redis latency
error rate
```

Why?

```text
If rate limiter works, bad traffic should increase 429, not DB writes.
```

Golden metric relationship:

```text
Attack traffic rises -> 429 rises -> DB stays stable
```

---

## 25. Production Failure Stories

### Failure Story 1: No Create Limit Caused DB Write Storm

A bot sent thousands of create requests per second.

Every request reached DB.

PostgreSQL writes spiked.

Root cause:

```text
No rate limit on create API.
```

Fix:

```text
Add per-IP and per-user create rate limit.
Add 429 response.
Add metrics.
```

Lesson:

```text
Write APIs need protection before DB.
```

---

### Failure Story 2: Local Limiter Failed After Scaling Pods

One pod limit was 10/min.

After scaling to 10 pods, actual limit became 100/min.

Root cause:

```text
local in-memory counters per pod
```

Fix:

```text
Redis shared distributed limiter.
```

Lesson:

```text
Local rate limits do not work correctly in horizontally scaled systems.
```

---

### Failure Story 3: Fixed Window Boundary Burst

Client sent 10 requests at 12:00:59 and 10 at 12:01:00.

System allowed 20 requests in 2 seconds.

Root cause:

```text
fixed window boundary burst
```

Fix:

```text
token bucket or sliding window.
```

Lesson:

```text
Simple algorithms have edge cases.
```

---

### Failure Story 4: Redis Limiter Down And App Failed Open

Redis went down.

Limiter allowed everything.

Attack traffic reached DB.

Root cause:

```text
fail-open strategy on expensive create API
```

Fix:

```text
fail-closed or local degraded fallback for create API.
```

Lesson:

```text
Failure strategy must depend on endpoint risk.
```

---

### Failure Story 5: Trusted Wrong IP Header

App trusted `X-Forwarded-For` directly.

Attackers spoofed new IPs in header.

Rate limit was bypassed.

Root cause:

```text
untrusted proxy header handling
```

Fix:

```text
trust forwarded headers only from known load balancer/proxy.
```

Lesson:

```text
Client identity extraction is security-sensitive.
```

---

## 26. Debugging Mindset

When rate limiting behaves strangely, ask:

```text
Which key is being rate limited?
Is client identity correct?
Are we behind proxy/load balancer?
Can X-Forwarded-For be trusted?
Is Redis key shared across pods?
Is algorithm fixed window/sliding/token bucket?
Is TTL correct?
Are rejected requests reaching controller accidentally?
Is Redis script atomic?
Is retryAfter correct?
Is fail-open/fail-closed configured correctly?
```

Debug commands:

```bash
redis-cli GET rate:create:ip:1.2.3.4
redis-cli TTL rate:create:ip:1.2.3.4
redis-cli KEYS 'rate:create:*'
```

Debug map:

```text
No requests rejected:
    limiter not applied to route
    key changes every request
    fail-open hiding Redis errors
    limit too high

Too many users rejected:
    NAT/shared IP issue
    trusted proxy header wrong
    limit too low

Limit bypass across pods:
    local memory limiter
    Redis key mismatch

DB still overloaded:
    limiter after DB work
    redirect/cache miss storm not covered
    fail-open during Redis issue
```

Golden rule:

```text
First inspect the rate limit key.
```

---

## 27. Common Mistakes

### Mistake 1: Rate Limiting After Service Work

Wrong:

```text
controller -> service -> DB -> rate limiter
```

Correct:

```text
filter/gateway -> rate limiter -> controller -> service -> DB
```

### Mistake 2: Same Limit For All Endpoints

Wrong:

```text
same limit for create and redirect
```

Correct:

```text
create strict, redirect cache/edge/abuse-specific
```

### Mistake 3: Local In-Memory Limiter In Multi-Pod System

Wrong:

```text
each pod has own counter
```

Correct:

```text
Redis/gateway shared limiter
```

### Mistake 4: Trusting X-Forwarded-For Blindly

Wrong:

```text
accept any client-provided header
```

Correct:

```text
trust only known proxy chain
```

### Mistake 5: No 429 Contract

Wrong:

```text
return 500 or random text
```

Correct:

```text
429 with RATE_LIMIT_EXCEEDED and Retry-After
```

### Mistake 6: No Metrics

Wrong:

```text
cannot tell if limiter works
```

Correct:

```text
track allowed, rejected, Redis errors, latency
```

### Mistake 7: Fail-Open Everywhere

Wrong:

```text
limiter fails, all endpoints allow unlimited traffic
```

Correct:

```text
endpoint-specific failure strategy
```

### Mistake 8: No Negative Cache / Bot Strategy For Redirect

Wrong:

```text
only limit create, ignore random redirect scans
```

Correct:

```text
negative cache, edge protection, abuse limits later
```

---

## 28. Interview-Ready Explanation

If interviewer asks:

```text
How would you add rate limiting to a URL shortener?
```

Strong answer:

```text
I would rate limit the expensive and abuse-prone APIs first, especially POST /api/v1/urls.
Redirect traffic is public and can be legitimately high, so I would protect it differently
with CDN caching, negative caching, WAF/bot protection, and abuse-specific limits. For create,
I would use a distributed Redis-backed token bucket keyed initially by IP and later by user ID
or API key. The limiter would run before controller/service/DB work, usually in a filter or
gateway, and return 429 Too Many Requests with Retry-After and a stable RATE_LIMIT_EXCEEDED
error code. Redis state must be shared across pods and updated atomically, usually using Lua or
a tested library. I would monitor allowed, rejected, Redis errors, limiter latency, create RPS,
DB insert rate, and Hikari pressure. For failure strategy, I would likely fail closed or use a
strict degraded fallback for create APIs, while redirect paths may fail open to preserve
availability.
```

Senior version:

```text
Rate limiting is not only an algorithm. It is a resource-protection boundary. The important
design decisions are what dimension to limit, where to enforce it, whether the state is shared
across pods, how to respond with 429, and how the system behaves when the limiter itself fails.
```

Why this is strong:

```text
1. Separates create and redirect APIs.
2. Uses distributed Redis state.
3. Chooses token bucket.
4. Enforces before expensive work.
5. Mentions 429 and Retry-After.
6. Mentions atomicity.
7. Mentions metrics.
8. Mentions fail-open/fail-closed tradeoff.
9. Shows production abuse thinking.
```

---

## 29. Senior Engineer Checklist

Before calling rate limiting production-shaped, confirm:

```text
[ ] create API has a strict limit
[ ] redirect API has separate strategy
[ ] limiter runs before expensive work
[ ] Redis/shared state is used across pods
[ ] key format is centralized
[ ] IP extraction is proxy-safe
[ ] user/API-key limit is planned for authenticated users
[ ] algorithm is chosen intentionally
[ ] Redis updates are atomic
[ ] 429 response contract exists
[ ] Retry-After header exists
[ ] metrics for allowed/rejected exist
[ ] Redis error metrics exist
[ ] failure strategy is explicit
[ ] tests cover allow/reject/refill/failure
[ ] limits are configurable
[ ] limits are load-tested
```

---

## 30. One-Page Cheat Sheet

```text
Core mental model:
Every client gets a limited request budget.

Protect first:
POST /api/v1/urls

Why:
DB writes
spam links
alias brute force
phishing abuse
buggy clients

Algorithms:
fixed window = simple but boundary burst
sliding window = accurate but heavier
token bucket = allows burst + controls long-term rate

Recommended:
Redis token bucket

Key:
rate:create:ip:{clientIp}
later:
rate:create:user:{userId}

Flow:
request
rate limiter
allowed -> controller/service/DB
rejected -> 429

429:
RATE_LIMIT_EXCEEDED
Retry-After
X-RateLimit-Remaining

Distributed:
all pods share Redis

Failure:
create API often fail-closed/degraded
redirect API often fail-open/edge-protected

Metrics:
allowed
rejected
Redis errors
limiter latency
DB insert rate
Hikari pending
```

---

## 31. One Picture To Remember

```text
                  RATE LIMITING MENTAL MODEL

                   "Reject before expensive work"

Client / IP / User
        |
        v
+-----------------------------+
| Rate Limit Bucket           |
| capacity = 10 tokens        |
| refill = 10 tokens/min      |
+-------------+---------------+
              |
       +------+------+
       |             |
 token available   no token
       |             |
       v             v
 consume 1        429 Too Many Requests
       |
       v
+-----------------------------+
| Controller                  |
+-------------+---------------+
              |
              v
+-----------------------------+
| Service                     |
+-------------+---------------+
              |
              v
+-----------------------------+
| PostgreSQL                  |
+-----------------------------+


DISTRIBUTED VERSION:

Pod A ----+
Pod B ----+---- Redis bucket key
Pod C ----+

FINAL MEMORY:

Rate limiting is a safety valve.
It protects expensive work.
It must be shared across pods.
It must return 429 predictably.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Rate limiting protects the system by rejecting excessive traffic before expensive work.
2. MiniURLShortener should rate limit create API strictly because it writes to DB and is abuse-prone.
3. Redis-backed token bucket is a strong default for distributed Spring Boot pods.
4. Rejected requests should return 429 with RATE_LIMIT_EXCEEDED and Retry-After.
5. Production readiness requires atomic Redis updates, proxy-safe client identity, endpoint-specific failure strategy, metrics, and tests.
```

Next chapter:

```text
026_Circuit_Breaker_For_Dependencies.md
```
