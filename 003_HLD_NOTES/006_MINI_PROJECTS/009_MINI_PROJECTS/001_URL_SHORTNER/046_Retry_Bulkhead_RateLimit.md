# 046_Retry_Bulkhead_RateLimit.md
# MiniURLShortener — Retry, Bulkhead & Rate Limiter

> Core mental model: **Retry fixes small temporary failures, Bulkhead limits blast radius, and Rate Limiter controls traffic speed. Together they stop one bad dependency or one noisy client from damaging the whole system.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Retry Mental Model](#4-retry-mental-model)
- [5. Bulkhead Mental Model](#5-bulkhead-mental-model)
- [6. Rate Limiter Mental Model](#6-rate-limiter-mental-model)
- [7. Why These Are Different](#7-why-these-are-different)
- [8. Where They Fit In MiniURLShortener](#8-where-they-fit-in-miniurlshortener)
- [9. Correct Ordering Of Resilience Patterns](#9-correct-ordering-of-resilience-patterns)
- [10. Maven Dependencies](#10-maven-dependencies)
- [11. Retry Configuration](#11-retry-configuration)
- [12. Retry Code Example](#12-retry-code-example)
- [13. Retry Fallback](#13-retry-fallback)
- [14. Retry Dry Runs](#14-retry-dry-runs)
- [15. Retry Production Rules](#15-retry-production-rules)
- [16. Bulkhead Configuration](#16-bulkhead-configuration)
- [17. Semaphore Bulkhead Code Example](#17-semaphore-bulkhead-code-example)
- [18. ThreadPool Bulkhead Code Example](#18-threadpool-bulkhead-code-example)
- [19. Bulkhead Dry Runs](#19-bulkhead-dry-runs)
- [20. Bulkhead Production Rules](#20-bulkhead-production-rules)
- [21. Rate Limiter Configuration](#21-rate-limiter-configuration)
- [22. Rate Limiter Code Example](#22-rate-limiter-code-example)
- [23. Rate Limiter Dry Runs](#23-rate-limiter-dry-runs)
- [24. Rate Limiter Production Rules](#24-rate-limiter-production-rules)
- [25. Combining Retry + Bulkhead + Rate Limiter](#25-combining-retry--bulkhead--rate-limiter)
- [26. Create Short URL Flow](#26-create-short-url-flow)
- [27. Redirect Flow](#27-redirect-flow)
- [28. Internal Execution Walkthrough](#28-internal-execution-walkthrough)
- [29. Observability And Actuator](#29-observability-and-actuator)
- [30. Metrics To Watch](#30-metrics-to-watch)
- [31. Testing Strategy](#31-testing-strategy)
- [32. Production Failure Stories](#32-production-failure-stories)
- [33. Debugging Mindset](#33-debugging-mindset)
- [34. Common Mistakes](#34-common-mistakes)
- [35. Interview-Ready Explanation](#35-interview-ready-explanation)
- [36. Senior Engineer Checklist](#36-senior-engineer-checklist)
- [37. One-Page Cheat Sheet](#37-one-page-cheat-sheet)
- [38. One Picture To Remember](#38-one-picture-to-remember)

---

## 1. Why This Exists

In the previous chapter, Circuit Breaker protected MiniURLShortener from repeatedly calling a failing dependency.

But circuit breaker is only one resilience pattern.

Production systems also need answers to three more questions:

```text
1. Retry:
   If one call fails because of a temporary network blip,
   should I try again?

2. Bulkhead:
   If one dependency becomes slow,
   how do I stop it from consuming all threads/resources?

3. Rate Limiter:
   If too many requests arrive,
   how do I control request speed?
```

MiniURLShortener depends on many things:

```text
Postgres
Redis
Kafka
Abuse detection service
Analytics service
Notification service
User service
Config server
Service registry
```

Each dependency can fail differently.

Example temporary failure:

```text
Abuse service returns 503 once.
Second call succeeds.
```

Retry can help.

Example slow dependency:

```text
Notification service takes 8 seconds.
All app threads get stuck.
```

Bulkhead can help.

Example too much traffic:

```text
One user sends 10,000 create requests per minute.
```

Rate limiter can help.

If you use these patterns blindly, you can make the system worse.

Bad retry:

```text
Dependency is already dying.
Every request retries 3 times.
Traffic triples.
Dependency collapses harder.
```

Bad bulkhead:

```text
Bulkhead too small.
Healthy traffic gets rejected.
```

Bad rate limiter:

```text
Global limit too low.
All customers are punished because of one abusive client.
```

This chapter teaches the production mental model.

---

## 2. The One Core Mental Model

Think of your backend as a building with doors, rooms, and emergency behavior.

```text
Rate Limiter = controls how many people enter the building.
Bulkhead     = separates rooms so fire in one room does not burn all rooms.
Retry        = if a door handle jams once, try again carefully.
```

ASCII:

```text
                        Incoming Requests
                               |
                               v
                    +----------------------+
                    | Rate Limiter         |
                    | control entry speed  |
                    +----------------------+
                               |
                               v
                    +----------------------+
                    | Bulkhead             |
                    | reserve limited room |
                    +----------------------+
                               |
                               v
                    +----------------------+
                    | Retry                |
                    | small second chance  |
                    +----------------------+
                               |
                               v
                         Dependency Call
```

One-line memory:

```text
Retry is second chance, Bulkhead is damage isolation, Rate Limiter is traffic control.
```

Do not memorize annotations.

Understand the failure shape:

```text
temporary failure -> retry
resource starvation -> bulkhead
too much traffic -> rate limiter
repeated dependency failure -> circuit breaker
one call taking too long -> timeout
```

---

## 3. Problem Statement

Build Resilience4j Retry, Bulkhead, and Rate Limiter support for MiniURLShortener.

We want:

```text
1. Retry small transient dependency failures safely.
2. Limit concurrent calls to slow dependencies.
3. Limit request or dependency call rate.
4. Avoid retry storms.
5. Avoid one dependency consuming all resources.
6. Avoid one user/client overloading create API.
7. Expose metrics.
8. Make configuration production-tunable.
```

Example protected flows:

```text
Create short URL:
    abuse check
    user quota check
    save URL
    publish event

Redirect:
    Redis/Postgres lookup
    return 302
    async analytics event

Async worker:
    send notification
    retry external notification API
    isolate notification pool
```

Out of scope:

```text
1. Full distributed token bucket using Redis.
2. Kafka retry topic and DLT deep dive.
3. API Gateway global throttling deep dive.
4. Kubernetes resource isolation.
```

This chapter focuses on application-level resilience using Resilience4j.

---

## 4. Retry Mental Model

Retry means:

```text
A call failed.
Maybe it was temporary.
Try again a limited number of times.
```

ASCII:

```text
Call dependency
      |
      +-- success --> return result
      |
      +-- failure --> wait small time
                     |
                     v
                  try again
```

Retry is useful for:

```text
1. Temporary network glitch
2. Connection reset
3. Short 503 from downstream
4. Brief leader election
5. Temporary timeout
```

Retry is dangerous for:

```text
1. Permanent validation errors
2. Bad request 400
3. Unauthorized 401
4. Forbidden 403
5. Duplicate conflict 409
6. Non-idempotent writes
7. Dependency outage
```

Important idea:

```text
Retry should be small, bounded, and only for safe failures.
```

Bad retry:

```text
Try forever.
```

Good retry:

```text
Try max 2 or 3 attempts with short wait and clear failure rules.
```

ASCII retry timeline:

```text
t=0ms      attempt 1  -> timeout
t=200ms    attempt 2  -> 503
t=400ms    attempt 3  -> success
```

But if dependency is truly down:

```text
attempt 1 -> fail
attempt 2 -> fail
attempt 3 -> fail
fallback / error
```

Retry is a local optimization, not a recovery strategy for full outages.

---

## 5. Bulkhead Mental Model

Bulkhead comes from ships.

A ship has internal compartments.

If one compartment floods, the whole ship does not sink.

ASCII:

```text
Ship without bulkheads:

+---------------------------------------+
| water enters here                     |
| and spreads everywhere                |
+---------------------------------------+

Ship with bulkheads:

+----------+----------+----------+
| room A   | room B   | room C   |
| flooded  | safe     | safe     |
+----------+----------+----------+
```

Software bulkhead:

```text
Limit how many concurrent calls can enter a risky dependency.
```

Without bulkhead:

```text
Notification service slows down.
All app threads wait on notification.
Create API, redirect API, health checks all suffer.
```

With bulkhead:

```text
Only 20 calls can wait on notification.
Other app functionality remains alive.
```

ASCII:

```text
Requests
   |
   v
+-----------------------+
| Bulkhead: max 20      |
+-----------------------+
   |
   +-- slot available --> call dependency
   |
   +-- no slot --------> reject/fallback quickly
```

Bulkhead is not primarily about reducing traffic rate.

It is about limiting concurrent resource usage.

---

## 6. Rate Limiter Mental Model

Rate limiter controls request speed.

It asks:

```text
How many calls are allowed in a time window?
```

Example:

```text
Allow 100 create requests per user per minute.
```

ASCII:

```text
Time window: 1 minute
Limit: 100 permits

User requests:
1, 2, 3, ... 100 -> allowed
101 -> rejected
```

Rate limiter protects against:

```text
1. Abuse
2. Accidental client loops
3. Traffic spikes
4. Cost explosion
5. Downstream overload
```

In URL shortener:

```text
Create API should be rate limited.
Redirect API may need high-scale edge/gateway rate limiting.
Admin APIs should be rate limited.
External dependency calls may be rate limited.
```

ASCII:

```text
Client
  |
  v
RateLimiter
  |
  +-- permit available --> continue
  |
  +-- no permit --------> 429 Too Many Requests
```

Rate limiter answers:

```text
How fast?
```

Bulkhead answers:

```text
How many at the same time?
```

Retry answers:

```text
Should I try again?
```

---

## 7. Why These Are Different

They solve different failure modes.

```text
+----------------+----------------------------+-----------------------------+
| Pattern        | Question                   | Protects Against            |
+----------------+----------------------------+-----------------------------+
| Retry          | Try again?                 | small transient failure      |
| Bulkhead       | How many concurrent calls? | resource starvation          |
| Rate Limiter   | How many per time window?  | traffic flood / abuse        |
| CircuitBreaker | Is dependency healthy?     | repeated dependency failure  |
| Timeout        | How long wait per call?    | stuck call                   |
+----------------+----------------------------+-----------------------------+
```

ASCII decision:

```text
One call failed once?
    -> Retry may help

Dependency keeps failing?
    -> Circuit breaker

Dependency is slow and consuming threads?
    -> Bulkhead + Timeout

Too many requests arrive?
    -> Rate limiter

One call hangs too long?
    -> Timeout
```

Common confusion:

```text
Rate limiter and bulkhead are not the same.
```

Example:

```text
Rate limit:
    100 requests per second.

Bulkhead:
    max 20 concurrent calls.
```

You can have:

```text
low rate but high concurrency
high rate but low concurrency
```

Example low rate, high concurrency:

```text
10 requests per minute, each takes 30 seconds.
5 requests can overlap.
```

Example high rate, low concurrency:

```text
1000 requests per second, each takes 1 ms.
Only a few overlap.
```

---

## 8. Where They Fit In MiniURLShortener

MiniURLShortener flows:

```text
Create Short URL:
    user sends long URL
    validate input
    abuse check
    save in Postgres
    maybe cache
    publish event

Redirect:
    user hits short code
    lookup Redis/Postgres
    redirect to long URL
    async analytics

Analytics worker:
    consume Kafka click events
    write analytics store

Notification worker:
    send email/SMS/webhook
```

Pattern placement:

```text
Create API:
    Rate limiter by user/IP/API key
    Retry for safe transient abuse service call
    Bulkhead for abuse service call
    Circuit breaker for abuse service call

Redirect API:
    Avoid remote non-critical calls
    Maybe rate limit abusive IPs at gateway
    Use timeout around cache/db calls
    Analytics should be async

Analytics worker:
    Bulkhead for external analytics sink
    Retry safe writes
    Circuit breaker if sink unhealthy

Notification worker:
    Rate limit provider calls
    Retry provider transient failures
    Bulkhead provider calls
```

ASCII:

```text
POST /api/v1/urls
      |
      v
RateLimiter(user/ip)
      |
      v
Bulkhead(abuse-check slots)
      |
      v
CircuitBreaker(abuse dependency health)
      |
      v
Retry(small transient failure)
      |
      v
Abuse Service
```

Important:

```text
Do not use one global resilience config for every dependency.
```

Each dependency has different latency, reliability, and business importance.

---

## 9. Correct Ordering Of Resilience Patterns

There is no one perfect order for every system, but there is a practical mental model.

For dependency calls:

```text
RateLimiter
Bulkhead
CircuitBreaker
TimeLimiter/Timeout
Retry
Dependency
```

But annotation nesting can vary.

Conceptual order:

```text
1. RateLimiter:
   Should this call be allowed by speed rules?

2. Bulkhead:
   Is there resource capacity?

3. CircuitBreaker:
   Is dependency currently allowed to be called?

4. Timeout:
   How long can one attempt wait?

5. Retry:
   Should failed attempt be repeated?
```

ASCII:

```text
Request
  |
  v
+-------------+
| RateLimiter |
+-------------+
  |
  v
+-------------+
| Bulkhead    |
+-------------+
  |
  v
+----------------+
| CircuitBreaker |
+----------------+
  |
  v
+-------------+
| Timeout     |
+-------------+
  |
  v
+-------------+
| Retry       |
+-------------+
  |
  v
Dependency
```

Critical warning:

```text
Retry should not multiply traffic when circuit breaker is already OPEN.
```

Another warning:

```text
Retry inside bulkhead means one request may occupy a bulkhead slot longer.
```

So tune carefully.

For most MiniURLShortener use cases:

```text
1. Timeout must exist.
2. Circuit breaker must prevent repeated dependency pain.
3. Retry must be small.
4. Bulkhead must limit concurrency.
5. Rate limiter must protect entry points or external provider quotas.
```

---

## 10. Maven Dependencies

For Spring Boot 3:

```xml
<dependencies>
    <!-- Resilience4j Spring Boot integration -->
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot3</artifactId>
    </dependency>

    <!-- Annotation support: @Retry, @Bulkhead, @RateLimiter -->
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-annotations</artifactId>
    </dependency>

    <!-- AOP proxy support -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>

    <!-- Actuator metrics and health -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

Optional with Spring Cloud abstraction:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
```

Important annotation rule:

```text
Resilience4j annotations work through Spring AOP proxies.
```

So this applies:

```text
Do not call annotated methods through this.method().
Use separate Spring beans.
```

Wrong:

```java
this.sendNotification();
```

Correct:

```java
notificationClient.sendNotification();
```

---

## 11. Retry Configuration

Example configuration:

```yaml
resilience4j:
  retry:
    instances:
      abuseCheckRetry:
        max-attempts: 3
        wait-duration: 200ms
        retry-exceptions:
          - java.io.IOException
          - java.net.SocketTimeoutException
          - org.springframework.web.client.ResourceAccessException
        ignore-exceptions:
          - com.miniurl.shortener.url.service.InvalidUrlException
          - com.miniurl.shortener.url.service.UnsafeUrlException
```

Meaning:

```text
max-attempts: 3
    Total attempts = first try + 2 retries

wait-duration: 200ms
    Wait 200ms between attempts

retry-exceptions:
    Retry only these exception types

ignore-exceptions:
    Never retry these exception types
```

ASCII:

```text
Attempt 1
   |
   +-- success --> done
   |
   +-- retryable failure
          |
          v
      wait 200ms
          |
          v
      Attempt 2
          |
          +-- retryable failure
                 |
                 v
             wait 200ms
                 |
                 v
             Attempt 3
```

For production, you may use exponential backoff:

```yaml
resilience4j:
  retry:
    instances:
      abuseCheckRetry:
        max-attempts: 3
        wait-duration: 100ms
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2
```

Timeline:

```text
Attempt 1 -> fail
wait 100ms
Attempt 2 -> fail
wait 200ms
Attempt 3 -> fail
fallback
```

Do not use huge retries in synchronous APIs.

---

## 12. Retry Code Example

Client interface:

```java
package com.miniurl.shortener.abuse;

public interface AbuseCheckClient {
    AbuseCheckResult check(String longUrl);
}
```

DTO:

```java
package com.miniurl.shortener.abuse;

public class AbuseCheckResult {

    private final boolean safe;
    private final String reason;

    public AbuseCheckResult(boolean safe, String reason) {
        this.safe = safe;
        this.reason = reason;
    }

    public boolean isSafe() {
        return safe;
    }

    public String getReason() {
        return reason;
    }
}
```

Retry wrapper service:

```java
package com.miniurl.shortener.abuse;

import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

@Service
public class RetryingAbuseCheckService {

    private final AbuseCheckClient abuseCheckClient;

    public RetryingAbuseCheckService(AbuseCheckClient abuseCheckClient) {
        this.abuseCheckClient = abuseCheckClient;
    }

    @Retry(name = "abuseCheckRetry", fallbackMethod = "fallbackAfterRetries")
    public AbuseCheckResult checkWithRetry(String longUrl) {
        return abuseCheckClient.check(longUrl);
    }

    public AbuseCheckResult fallbackAfterRetries(String longUrl, Throwable throwable) {
        /*
         * Fail closed because abuse check is security-sensitive.
         */
        return new AbuseCheckResult(false, "abuse-check-retry-exhausted");
    }
}
```

Execution:

```text
Caller calls checkWithRetry().
Spring proxy intercepts.
Resilience4j executes first attempt.
If retryable failure happens, it waits and retries.
If all attempts fail, fallbackAfterRetries() runs.
```

ASCII:

```text
Caller
  |
  v
@Retry Proxy
  |
  +-- attempt 1
  |
  +-- attempt 2 if retryable
  |
  +-- attempt 3 if retryable
  |
  +-- fallback if all fail
```

Fallback signature rule:

```java
public ReturnType fallbackMethod(originalArgs..., Throwable throwable)
```

---

## 13. Retry Fallback

Fallback after retry means:

```text
All retry attempts failed.
Now choose safe behavior.
```

For abuse check:

```text
fail closed
```

For analytics:

```text
do not fail user request
publish to local buffer / Kafka later / drop with metric
```

For notification:

```text
mark notification PENDING_RETRY
```

For payment:

```text
be very careful
idempotency required
do not blindly retry unsafe writes
```

MiniURLShortener fallback examples:

```text
Abuse check unavailable:
    reject URL or mark PENDING_REVIEW

Analytics unavailable:
    continue redirect, record metric

Notification provider unavailable:
    store notification job for retry worker

Preview metadata unavailable:
    create URL without preview
```

ASCII:

```text
Retry exhausted
      |
      v
Is operation critical?
      |
      +-- yes --> fail safely
      |
      +-- no  --> degrade and continue
```

Rule:

```text
Fallback is a business decision, not a technical decoration.
```

---

## 14. Retry Dry Runs

### Dry Run 1: Temporary Network Blip

Config:

```text
max-attempts = 3
wait-duration = 200ms
```

Timeline:

```text
Attempt 1: connection reset
wait 200ms
Attempt 2: success
```

Result:

```text
API succeeds.
Client never sees temporary failure.
```

ASCII:

```text
Attempt 1 -> fail
    |
    v
wait
    |
    v
Attempt 2 -> success
```

---

### Dry Run 2: Dependency Fully Down

Timeline:

```text
Attempt 1: timeout
wait 200ms
Attempt 2: timeout
wait 200ms
Attempt 3: timeout
fallback
```

Result:

```text
Fallback runs.
If abuse check, URL may be rejected.
```

ASCII:

```text
fail -> wait -> fail -> wait -> fail -> fallback
```

---

### Dry Run 3: Non-Retryable Error

Dependency returns:

```text
400 Bad Request
```

This should not be retried.

Flow:

```text
1. Attempt 1 fails with non-retryable business error.
2. Retry does not repeat.
3. Error/fallback happens immediately.
```

Why:

```text
Retrying bad input does not make it valid.
```

---

### Dry Run 4: Retry Storm

Traffic:

```text
1000 RPS
max-attempts = 3
dependency down
```

Actual dependency calls:

```text
1000 * 3 = 3000 calls per second
```

Result:

```text
Outage becomes worse.
```

Lesson:

```text
Retry must be combined with timeout, circuit breaker, and limits.
```

---

## 15. Retry Production Rules

Production retry checklist:

```text
1. Retry only transient failures.
2. Do not retry validation errors.
3. Do not retry permanent 4xx errors.
4. Keep max attempts small.
5. Use timeout per attempt.
6. Use exponential backoff for unstable dependencies.
7. Use jitter when many clients retry.
8. Use circuit breaker to stop retry storms.
9. Retry only idempotent or safely repeatable operations.
10. Observe retry count metrics.
```

Idempotency reminder:

```text
GET is usually safe to retry.
PUT may be safe if idempotent.
POST is dangerous unless idempotency key exists.
```

URL shortener create operation:

```text
Retrying external abuse check:
    usually safe

Retrying database insert:
    careful, unique constraints and transaction behavior matter

Retrying create short URL request from client:
    needs idempotency key if duplicate creation must be avoided
```

Bad retry example:

```text
POST /charge-credit-card retried 3 times without idempotency key.
```

Result:

```text
Customer charged multiple times.
```

For MiniURLShortener, duplicates are less severe than payments, but still matter:

```text
Same long URL may get multiple short codes.
Custom alias may conflict.
Analytics may double count.
```

---

## 16. Bulkhead Configuration

Resilience4j supports two main bulkhead types:

```text
1. Semaphore Bulkhead
2. ThreadPool Bulkhead
```

Semaphore bulkhead:

```text
Limits concurrent calls in current thread.
```

ThreadPool bulkhead:

```text
Runs calls on separate thread pool with queue.
```

Semaphore config:

```yaml
resilience4j:
  bulkhead:
    instances:
      abuseCheckBulkhead:
        max-concurrent-calls: 20
        max-wait-duration: 50ms
```

Meaning:

```text
Only 20 concurrent calls can enter.
If no slot is available, wait max 50ms.
Then reject/fallback.
```

ThreadPool config:

```yaml
resilience4j:
  thread-pool-bulkhead:
    instances:
      notificationBulkhead:
        core-thread-pool-size: 10
        max-thread-pool-size: 20
        queue-capacity: 100
        keep-alive-duration: 20s
```

Meaning:

```text
Notification calls get separate threads.
Main request threads are not fully consumed by notification work.
```

ASCII:

```text
Semaphore Bulkhead:
Caller thread waits/executes directly

ThreadPool Bulkhead:
Caller submits work to separate pool
```

---

## 17. Semaphore Bulkhead Code Example

Use semaphore bulkhead for simple synchronous dependency calls.

```java
package com.miniurl.shortener.abuse;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.springframework.stereotype.Service;

@Service
public class BulkheadAbuseCheckService {

    private final AbuseCheckClient abuseCheckClient;

    public BulkheadAbuseCheckService(AbuseCheckClient abuseCheckClient) {
        this.abuseCheckClient = abuseCheckClient;
    }

    @Bulkhead(
            name = "abuseCheckBulkhead",
            type = Bulkhead.Type.SEMAPHORE,
            fallbackMethod = "fallbackWhenBulkheadFull"
    )
    public AbuseCheckResult checkWithBulkhead(String longUrl) {
        return abuseCheckClient.check(longUrl);
    }

    public AbuseCheckResult fallbackWhenBulkheadFull(String longUrl, Throwable throwable) {
        return new AbuseCheckResult(false, "abuse-check-bulkhead-full");
    }
}
```

Flow:

```text
1. Request enters method through Spring proxy.
2. Bulkhead checks if slot is available.
3. If yes, call proceeds.
4. If no slot, fallback runs.
```

ASCII:

```text
Request
  |
  v
Bulkhead slots:
[1][2][3]...[20]
  |
  +-- free slot --> call abuse service
  |
  +-- full -----> fallback
```

---

## 18. ThreadPool Bulkhead Code Example

ThreadPool bulkhead is useful for slower side effects like notifications.

For thread pool bulkhead, return `CompletionStage`.

```java
package com.miniurl.shortener.notification;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Service
public class NotificationService {

    private final NotificationClient notificationClient;

    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @Bulkhead(
            name = "notificationBulkhead",
            type = Bulkhead.Type.THREADPOOL,
            fallbackMethod = "fallbackNotification"
    )
    public CompletionStage<Boolean> sendNotification(String userId, String message) {
        return CompletableFuture.supplyAsync(() -> {
            notificationClient.send(userId, message);
            return true;
        });
    }

    public CompletionStage<Boolean> fallbackNotification(
            String userId,
            String message,
            Throwable throwable
    ) {
        /*
         * In real production:
         * store notification job as PENDING_RETRY.
         */
        return CompletableFuture.completedFuture(false);
    }
}
```

Important:

```text
ThreadPoolBulkhead isolates slow work from main request threads.
```

But do not hide failures.

Better production flow:

```text
Notification failed or bulkhead full.
Save notification job to DB/Kafka retry topic.
Worker retries later.
```

ASCII:

```text
Main API thread
     |
     v
submit notification work
     |
     v
+---------------------------+
| Notification Thread Pool  |
| max 20 threads            |
| queue 100                 |
+---------------------------+
     |
     v
Notification Provider
```

---

## 19. Bulkhead Dry Runs

### Dry Run 1: Slots Available

Config:

```text
max-concurrent-calls = 3
```

Current calls:

```text
2 active
```

New request:

```text
slot available
```

Flow:

```text
1. Bulkhead grants slot.
2. Dependency call runs.
3. Call completes.
4. Slot released.
```

ASCII:

```text
Slots:
[busy][busy][free]

New call takes free slot.
```

---

### Dry Run 2: Bulkhead Full

Config:

```text
max-concurrent-calls = 3
max-wait-duration = 50ms
```

Current calls:

```text
3 active
```

New request:

```text
no slot
```

Flow:

```text
1. Bulkhead waits up to 50ms.
2. No slot becomes free.
3. Bulkhead rejects call.
4. Fallback runs.
```

ASCII:

```text
Slots:
[busy][busy][busy]

New call:
   |
   v
wait 50ms
   |
   v
fallback
```

---

### Dry Run 3: Without Bulkhead

Traffic:

```text
200 concurrent requests call slow notification service.
```

Without bulkhead:

```text
200 app threads blocked.
Create API slows.
Redirect API slows.
Health checks may fail.
```

With bulkhead:

```text
Only 20 notification calls run.
Others fallback or queue.
Core API remains alive.
```

Lesson:

```text
Bulkhead protects the rest of the system from one slow area.
```

---

## 20. Bulkhead Production Rules

Checklist:

```text
1. Use separate bulkheads per dependency.
2. Size bulkheads based on latency and throughput.
3. Keep max wait duration small for synchronous APIs.
4. Do not let non-critical work consume core request threads.
5. Use thread pool bulkhead for slow async side effects.
6. Use semaphore bulkhead for simple sync calls.
7. Monitor rejected calls.
8. Fallback must be intentional.
9. Avoid queue capacity so large it hides overload.
10. Combine with timeout.
```

Sizing idea:

```text
Required concurrency roughly equals:
throughput * latency
```

Example:

```text
Abuse service:
100 RPS
p95 latency = 100ms = 0.1s

Needed concurrency:
100 * 0.1 = 10

Add safety:
max-concurrent-calls = 20
```

ASCII:

```text
Concurrency = RPS * latency_seconds

100 RPS * 0.1s = 10 concurrent calls
```

If latency becomes 2 seconds:

```text
100 RPS * 2s = 200 concurrent calls
```

That is dangerous.

Bulkhead prevents this explosion.

---

## 21. Rate Limiter Configuration

Example dependency rate limiter:

```yaml
resilience4j:
  ratelimiter:
    instances:
      createUrlRateLimiter:
        limit-for-period: 100
        limit-refresh-period: 1m
        timeout-duration: 0
```

Meaning:

```text
Allow 100 calls per minute.
Do not wait for permit.
If no permit, reject immediately.
```

Example external provider quota:

```yaml
resilience4j:
  ratelimiter:
    instances:
      notificationProviderRateLimiter:
        limit-for-period: 50
        limit-refresh-period: 1s
        timeout-duration: 100ms
```

Meaning:

```text
Allow 50 notification provider calls per second.
Wait up to 100ms for permit.
```

ASCII:

```text
Time Window: 1 second
Permits: 50

Request 1..50 -> allowed
Request 51 -> wait/reject
```

For public APIs, rate limiting often belongs at:

```text
1. API Gateway
2. Load balancer / edge
3. Application
4. Redis distributed limiter
```

Application-level Resilience4j rate limiter is useful, but not enough for multi-pod global limits.

---

## 22. Rate Limiter Code Example

Create API rate limiter:

```java
package com.miniurl.shortener.url.service;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;

@Service
public class RateLimitedUrlCreationService {

    private final UrlCreationService urlCreationService;

    public RateLimitedUrlCreationService(UrlCreationService urlCreationService) {
        this.urlCreationService = urlCreationService;
    }

    @RateLimiter(
            name = "createUrlRateLimiter",
            fallbackMethod = "fallbackRateLimited"
    )
    public CreateShortUrlResponse create(CreateShortUrlCommand command) {
        return urlCreationService.create(command);
    }

    public CreateShortUrlResponse fallbackRateLimited(
            CreateShortUrlCommand command,
            Throwable throwable
    ) {
        throw new TooManyRequestsException(
                "Too many create URL requests. Please try again later."
        );
    }
}
```

Exception:

```java
package com.miniurl.shortener.common.error;

import org.springframework.http.HttpStatus;

public class TooManyRequestsException extends ApiException {

    public TooManyRequestsException(String message) {
        super("TOO_MANY_REQUESTS", HttpStatus.TOO_MANY_REQUESTS, message);
    }
}
```

API response:

```json
{
  "status": 429,
  "error": "Too Many Requests",
  "code": "TOO_MANY_REQUESTS",
  "message": "Too many create URL requests. Please try again later."
}
```

ASCII:

```text
Client
  |
  v
@RateLimiter Proxy
  |
  +-- permit available --> create URL
  |
  +-- no permit -------> 429
```

Important limitation:

```text
This rate limiter is local to one application instance.
```

If you have 5 pods:

```text
100/min per pod = 500/min cluster-wide
```

For true global per-user limit:

```text
Use Redis/token bucket or API Gateway distributed rate limiting.
```

---

## 23. Rate Limiter Dry Runs

### Dry Run 1: Under Limit

Config:

```text
limit-for-period = 5
limit-refresh-period = 1s
```

Requests in current second:

```text
1, 2, 3
```

All allowed.

ASCII:

```text
Permits:
[1][2][3][4][5]

Used:
[1][2][3]

Remaining:
[4][5]
```

---

### Dry Run 2: Limit Exceeded

Same config:

```text
5 requests per second
```

Requests:

```text
1 allowed
2 allowed
3 allowed
4 allowed
5 allowed
6 rejected
```

Result:

```text
Request 6 -> 429 TOO_MANY_REQUESTS
```

ASCII:

```text
Permits:
[X][X][X][X][X]

Request 6:
   |
   v
No permit
   |
   v
429
```

---

### Dry Run 3: Refresh Period

At next second:

```text
Permits refresh.
```

Flow:

```text
t=0s:
    5 permits used

t=1s:
    permits reset to 5
```

ASCII:

```text
t=0s  [X][X][X][X][X]
t=1s  [ ][ ][ ][ ][ ]
```

---

### Dry Run 4: Multi-Pod Limitation

Config:

```text
100/min per app instance
```

Deployment:

```text
5 pods
```

Actual cluster capacity:

```text
5 * 100 = 500/min
```

Lesson:

```text
Local limiter is not global limiter.
```

For per-user global limit:

```text
Use Redis key:
rate:user:123:create-url
```

---

## 24. Rate Limiter Production Rules

Checklist:

```text
1. Use 429 for rate-limited requests.
2. Prefer gateway/distributed limiter for public APIs.
3. Use app-level limiter for dependency quotas or local protection.
4. Rate limit by user/API key/IP, not only globally.
5. Avoid punishing all users for one noisy user.
6. Include Retry-After header when possible.
7. Monitor rejected calls.
8. Keep timeout-duration small for APIs.
9. Remember local limiter multiplies by pod count.
10. Combine with authentication for fair limits.
```

Per-user vs global:

```text
Global limit:
    protects system but may be unfair.

Per-user limit:
    controls noisy client.

Per-IP limit:
    useful for anonymous traffic, but NAT can affect fairness.

Per-API-key limit:
    best for developer APIs.
```

URL shortener suggested policy:

```text
Anonymous create:
    20/min per IP

Logged-in user create:
    100/min per user

Redirect:
    high limit at edge/CDN/gateway

Admin APIs:
    strict limit

Notification provider calls:
    provider quota-based limit
```

---

## 25. Combining Retry + Bulkhead + Rate Limiter

Example for abuse check:

```java
@Service
public class ResilientAbuseCheckService {

    private final AbuseCheckClient abuseCheckClient;

    public ResilientAbuseCheckService(AbuseCheckClient abuseCheckClient) {
        this.abuseCheckClient = abuseCheckClient;
    }

    @RateLimiter(name = "abuseCheckRateLimiter", fallbackMethod = "fallback")
    @Bulkhead(name = "abuseCheckBulkhead", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "fallback")
    @Retry(name = "abuseCheckRetry", fallbackMethod = "fallback")
    public AbuseCheckResult check(String longUrl) {
        return abuseCheckClient.check(longUrl);
    }

    public AbuseCheckResult fallback(String longUrl, Throwable throwable) {
        return new AbuseCheckResult(false, "abuse-check-unavailable");
    }
}
```

Conceptual flow:

```text
Rate limiter:
    Are we allowed to call this dependency now?

Bulkhead:
    Is a concurrency slot available?

Retry:
    If transient failure, try again carefully.

Fallback:
    If still failed or rejected, choose safe behavior.
```

ASCII:

```text
URL Service
    |
    v
RateLimiter
    |
    v
Bulkhead
    |
    v
Retry
    |
    v
Abuse Service
    |
    +-- fail after retries -> fallback
```

Important:

```text
Annotation order can be subtle.
For critical flows, test behavior explicitly.
```

Production composition should often include circuit breaker too:

```text
RateLimiter
Bulkhead
CircuitBreaker
Timeout
Retry
Dependency
```

But do not blindly stack everything everywhere.

Use patterns based on failure mode.

---

## 26. Create Short URL Flow

Create API resilience:

```text
POST /api/v1/urls
```

Recommended protection:

```text
1. Rate limit create requests by user/IP.
2. Validate request.
3. Call abuse checker with timeout.
4. Use bulkhead for abuse checker.
5. Use circuit breaker for abuse checker.
6. Use small retry for transient abuse checker failures.
7. Fail closed or mark pending review.
8. Save URL.
```

ASCII:

```text
Client
  |
  v
Gateway / App Rate Limit
  |
  v
Controller Validation
  |
  v
UrlService
  |
  v
+------------------------------+
| Resilient Abuse Check        |
| - Bulkhead                   |
| - Circuit Breaker            |
| - Retry                      |
| - Timeout                    |
+------------------------------+
  |
  +-- safe ------> save short URL
  |
  +-- unsafe ----> reject
  |
  +-- unavailable -> fail closed / pending review
```

Why rate limit create API?

```text
Create consumes DB writes, short-code generation, validation, abuse checks, and maybe Kafka events.
```

Redirect is read-heavy and should be optimized differently.

---

## 27. Redirect Flow

Redirect path must be fast.

```text
GET /{shortCode}
```

Recommended:

```text
1. Validate short code shape.
2. Lookup Redis.
3. Fallback to Postgres if cache miss.
4. Return 302.
5. Publish analytics asynchronously.
```

Avoid:

```text
Synchronous notification
Synchronous analytics write
Synchronous preview fetch
Synchronous remote user profile lookup
```

ASCII:

```text
GET /abc123
   |
   v
Redis/Postgres lookup
   |
   v
302 redirect
   |
   v
Async analytics event
   |
   v
Worker handles retry/bulkhead/rate limit
```

For analytics worker:

```text
Retry:
    transient sink failure

Bulkhead:
    limit concurrent sink writes

Rate limiter:
    obey external sink quota

Circuit breaker:
    stop calling unhealthy analytics sink
```

Important:

```text
Redirect success should not depend on analytics success.
```

---

## 28. Internal Execution Walkthrough

When using annotations:

```java
@RateLimiter(...)
@Bulkhead(...)
@Retry(...)
public AbuseCheckResult check(String longUrl) {
    return client.check(longUrl);
}
```

Internal flow:

```text
1. Spring creates AOP proxy for bean.
2. Caller invokes method through proxy.
3. Proxy applies resilience aspects.
4. Rate limiter checks permit.
5. Bulkhead checks concurrency slot.
6. Retry executes method attempt.
7. Client calls dependency.
8. Exceptions are classified.
9. Retry may repeat if retryable.
10. Bulkhead slot is released.
11. Metrics are recorded.
12. Fallback runs if needed.
```

ASCII:

```text
Caller
  |
  v
Spring Proxy
  |
  v
RateLimiter Aspect
  |
  v
Bulkhead Aspect
  |
  v
Retry Aspect
  |
  v
Real Method
  |
  v
Dependency
```

Self-invocation problem:

```java
this.check(url);
```

This bypasses proxy.

Correct:

```text
Call annotated method from another Spring bean.
```

Also remember:

```text
Private methods cannot be proxied by Spring AOP in the usual way.
Final methods/classes can cause proxy issues depending on proxy type.
```

---

## 29. Observability And Actuator

Expose endpoints:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,retries,ratelimiters,bulkheads
```

Useful endpoints:

```text
GET /actuator/metrics/resilience4j.retry.calls
GET /actuator/metrics/resilience4j.bulkhead.available.concurrent.calls
GET /actuator/metrics/resilience4j.ratelimiter.available.permissions
```

If supported in your setup:

```text
GET /actuator/retries
GET /actuator/bulkheads
GET /actuator/ratelimiters
```

ASCII:

```text
Runtime events
    |
    v
Resilience4j metrics
    |
    v
Micrometer
    |
    v
Actuator
    |
    v
Prometheus / Grafana later
```

Useful log fields:

```text
resiliencePattern
instanceName
fallbackUsed
exceptionClass
attemptNumber
bulkheadRejected
rateLimited
correlationId
latencyMs
```

Observability rule:

```text
A fallback without metrics is hidden failure.
```

---

## 30. Metrics To Watch

Retry metrics:

```text
successful_without_retry
successful_with_retry
failed_without_retry
failed_with_retry
retry_attempts
```

Bulkhead metrics:

```text
available_concurrent_calls
max_allowed_concurrent_calls
bulkhead_rejected_calls
queue_depth for thread pool bulkhead
```

Rate limiter metrics:

```text
available_permissions
waiting_threads
rate_limited_calls
```

Interpretation:

```text
High successful_with_retry:
    dependency has transient instability

High failed_with_retry:
    retry is not helping; dependency may be down

High bulkhead rejection:
    dependency slow or bulkhead too small

High rate-limited calls:
    traffic spike, abuse, or limit too strict

High waiting threads:
    timeout-duration may be too high
```

ASCII debug:

```text
User sees failures
      |
      v
Check 429?
      |
      +-- yes --> rate limiter
      |
      v
Check bulkhead rejected?
      |
      +-- yes --> dependency concurrency saturation
      |
      v
Check retry failed?
      |
      +-- yes --> dependency repeated failure
```

---

## 31. Testing Strategy

Test each pattern separately before combining.

### Retry Test

Goal:

```text
First call fails, second succeeds.
```

Assert:

```text
method returns success
client called twice
```

Pseudo:

```java
when(client.check(url))
        .thenThrow(new ResourceAccessException("temporary"))
        .thenReturn(new AbuseCheckResult(true, "clean"));

AbuseCheckResult result = service.checkWithRetry(url);

assertTrue(result.isSafe());
verify(client, times(2)).check(url);
```

### Bulkhead Test

Goal:

```text
When max concurrent calls are occupied,
next call falls back.
```

Test idea:

```text
Use dependency mock that blocks.
Start N calls.
Call N+1.
Assert fallback.
```

### Rate Limiter Test

Goal:

```text
After limit is consumed,
next call returns 429.
```

Test config:

```yaml
limit-for-period: 2
limit-refresh-period: 10s
timeout-duration: 0
```

Flow:

```text
call 1 allowed
call 2 allowed
call 3 rejected
```

### Combined Test

Assert:

```text
1. No retry for 400.
2. Retry for timeout.
3. Bulkhead rejection does not call dependency.
4. Rate limiter rejection does not call dependency.
5. Fallback response is safe.
```

Testing rule:

```text
Always verify dependency call count.
```

Why?

```text
It proves whether retry/bulkhead/rate limiter actually changed behavior.
```

---

## 32. Production Failure Stories

### Failure Story 1: Retry Multiplied Outage

Scenario:

```text
Abuse service down.
URL service retries 3 times.
1000 RPS becomes 3000 dependency calls/sec.
```

Result:

```text
Abuse service cannot recover.
URL service latency increases.
Circuit breaker opens late.
```

Fix:

```text
Small retry + timeout + circuit breaker + bulkhead.
```

Lesson:

```text
Retry can be dangerous during real outages.
```

---

### Failure Story 2: No Bulkhead, Notification Kills Create API

Scenario:

```text
Notification provider becomes slow.
Create API sends notification synchronously.
All Tomcat threads wait.
```

Result:

```text
Even redirect API becomes slow.
Health checks fail.
Kubernetes restarts pods.
```

Fix:

```text
Move notification async.
Use thread pool bulkhead.
Use retry worker.
```

Lesson:

```text
Non-critical side effects must not consume critical request resources.
```

---

### Failure Story 3: Local Rate Limiter Misunderstood

Config:

```text
100 requests/min
```

Deployment:

```text
10 pods
```

Expected:

```text
100/min globally
```

Actual:

```text
1000/min globally
```

Fix:

```text
Use Redis/Gateway distributed rate limiter for global per-user limits.
```

Lesson:

```text
Application-local limiter scales with pod count.
```

---

### Failure Story 4: Retrying Non-Idempotent Operation

Scenario:

```text
POST to payment provider times out.
Service retries.
Original request actually succeeded.
Retry charges again.
```

Fix:

```text
Use idempotency key.
Retry only safe operations.
```

MiniURLShortener equivalent:

```text
Client retries create URL.
Without idempotency, multiple short codes may be created.
```

Lesson:

```text
Timeout does not always mean failure. It may mean unknown result.
```

---

### Failure Story 5: Hidden Fallback

Scenario:

```text
Fallback silently returns success.
Metrics are not monitored.
```

Result:

```text
System appears healthy.
But abuse checks have been skipped for hours.
```

Fix:

```text
Fallback emits metric, log, and alert if sustained.
```

Lesson:

```text
Fallback is controlled degradation, not invisibility.
```

---

## 33. Debugging Mindset

When something fails, ask:

```text
Is this a retry issue?
Is this a bulkhead issue?
Is this a rate limiter issue?
Is dependency actually down?
Is dependency slow?
Is traffic too high?
Is fallback running?
Is fallback safe?
Is annotated method called through proxy?
Are retries happening for wrong exceptions?
Are all pods applying local limits separately?
```

Debug map:

```text
429 response:
    rate limiter

BulkheadFullException:
    too many concurrent dependency calls

High retry attempts:
    transient instability or bad retry condition

High failed retries:
    dependency outage or retry not useful

High p99:
    timeout too high, dependency slow, bulkhead too large

No metrics:
    missing actuator/micrometer or annotation not applied

Dependency call count higher than traffic:
    retry multiplication
```

Useful logs:

```text
pattern=retry
attempt=2
dependency=abuse-service
exception=SocketTimeoutException
correlationId=...

pattern=bulkhead
result=rejected
bulkhead=notificationBulkhead

pattern=ratelimiter
result=rejected
limiter=createUrlRateLimiter
```

Golden question:

```text
Is the resilience pattern reducing damage or hiding damage?
```

---

## 34. Common Mistakes

### Mistake 1: Retrying everything

Wrong:

```text
Retry all exceptions.
```

Correct:

```text
Retry only transient dependency failures.
```

### Mistake 2: Retrying too many times

Wrong:

```text
max-attempts: 10
```

Correct:

```text
2 or 3 attempts for synchronous APIs.
```

### Mistake 3: No timeout

Wrong:

```text
Retry waits on calls that hang for 30 seconds.
```

Correct:

```text
Set timeout per attempt.
```

### Mistake 4: Bulkhead around everything

Wrong:

```text
Bulkhead every local method.
```

Correct:

```text
Bulkhead risky dependencies and slow side effects.
```

### Mistake 5: Huge bulkhead queue

Wrong:

```text
queue-capacity: 100000
```

Correct:

```text
Small queue, fail/degrade early.
```

### Mistake 6: Local rate limiter treated as global

Wrong:

```text
100/min app limit means 100/min cluster limit.
```

Correct:

```text
Multiply by pod count unless using distributed limiter.
```

### Mistake 7: Fallback returns fake success

Wrong:

```text
Dependency unavailable, return success.
```

Correct:

```text
Return business-safe degraded result.
```

### Mistake 8: Self-invocation

Wrong:

```java
this.callWithRetry();
```

Correct:

```text
Call through Spring proxy from another bean.
```

### Mistake 9: No observability

Wrong:

```text
Retries/fallbacks happen silently.
```

Correct:

```text
Metrics, logs, alerts.
```

---

## 35. Interview-Ready Explanation

If interviewer asks:

```text
How do Retry, Bulkhead, and Rate Limiter differ?
```

Strong answer:

```text
Retry gives a failed dependency call a small bounded second chance, mainly for
transient failures like connection resets or short 503s. Bulkhead limits how many
concurrent calls can enter a risky dependency, so one slow dependency cannot consume
all application threads and bring down unrelated APIs. Rate limiter controls how
many calls are allowed in a time window, usually to protect against abuse, traffic
spikes, or external provider quotas. In a URL shortener, I would rate limit create
requests by user or IP, use bulkhead around abuse-check or notification dependencies,
and use small retry only for safe transient dependency errors. I would combine these
with timeout and circuit breaker, because retry alone can amplify outages. I would
also expose metrics for retry attempts, bulkhead rejections, and rate-limited calls,
because fallback without observability becomes hidden failure.
```

If interviewer asks:

```text
Where would you use these in URL shortener?
```

Answer:

```text
Create URL is write-heavy and abuse-prone, so I would rate limit it by user/IP/API
key. The abuse detection call is a remote dependency, so I would protect it with
timeout, circuit breaker, bulkhead, and small retry for transient failures. Redirect
should stay very fast, so I would avoid synchronous non-critical calls there and
send analytics asynchronously. Analytics or notification workers can use retry,
bulkhead, and provider quota rate limiting independently.
```

Senior one-liner:

```text
Retry handles small failure, bulkhead isolates damage, and rate limiter controls traffic pressure.
```

---

## 36. Senior Engineer Checklist

Before production:

```text
[ ] Retry only transient errors
[ ] Retry max attempts small
[ ] Timeout configured per attempt
[ ] Retry not used blindly for non-idempotent writes
[ ] Circuit breaker prevents retry storm
[ ] Bulkhead exists for slow/risky dependencies
[ ] Bulkhead size based on RPS * latency
[ ] Bulkhead rejection fallback is safe
[ ] ThreadPoolBulkhead used for slow side effects
[ ] Rate limiter returns 429
[ ] Rate limit key is user/IP/API key where needed
[ ] Local limiter limitation understood
[ ] Distributed limiter used for global limits
[ ] Fallback emits metrics/logs
[ ] Actuator metrics exposed
[ ] Alerts watch sustained degradation
[ ] Self-invocation avoided
[ ] Tests verify dependency call count
```

If these are checked, your resilience layer is production-shaped.

---

## 37. One-Page Cheat Sheet

```text
Core mental model:
Retry = small second chance
Bulkhead = damage isolation
Rate Limiter = traffic speed control

Retry:
Use for transient errors.
Do not retry permanent 4xx.
Keep attempts small.
Use timeout.
Be careful with non-idempotent writes.

Bulkhead:
Limits concurrent calls.
Prevents one dependency from consuming all resources.
Semaphore bulkhead = same thread, concurrency cap.
ThreadPool bulkhead = separate pool and queue.

Rate Limiter:
Limits calls per time window.
Use 429 for rejection.
Local limiter is per pod.
Use Redis/Gateway for global distributed limits.

MiniURLShortener:
Create API:
    rate limit by user/IP
    abuse check with retry/bulkhead/circuit breaker/timeout

Redirect API:
    keep fast
    avoid synchronous non-critical dependencies
    analytics async

Workers:
    retry transient provider failures
    bulkhead provider calls
    rate limit provider quota

Production:
Timeout first.
Retry carefully.
Bulkhead isolation.
Rate limit fairness.
Circuit breaker for repeated dependency failure.
Metrics always.
```

---

## 38. One Picture To Remember

```text
          RETRY + BULKHEAD + RATE LIMITER MENTAL MODEL

                      Incoming Traffic
                             |
                             v
                  +----------------------+
                  | Rate Limiter         |
                  | How fast can enter?  |
                  +----------------------+
                             |
             no permit       | permit
             +---------------+
             |
             v
         429 Too Many
                             |
                             v
                  +----------------------+
                  | Bulkhead             |
                  | Is resource slot free?|
                  +----------------------+
                             |
             no slot         | slot
             +---------------+
             |
             v
          fallback / reject
                             |
                             v
                  +----------------------+
                  | Retry                |
                  | Small second chance  |
                  +----------------------+
                             |
                             v
                        Dependency
                             |
             fail transient  | success
             +---------------+
             |
             v
        retry limited times
                             |
                             v
                         Response


FINAL MEMORY:

Rate limiter controls entry speed.
Bulkhead controls concurrent damage.
Retry handles tiny temporary failure.
Circuit breaker stops repeated dependency pain.
Timeout prevents one call from hanging forever.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Retry should be small, bounded, and only for transient safe failures.
2. Bulkhead prevents one slow dependency from consuming all service resources.
3. Rate limiter controls traffic speed and should usually return 429 when exceeded.
4. Local application rate limits multiply by pod count unless backed by distributed storage.
5. Resilience patterns must be observable, because silent fallback is hidden failure.
```

Next possible chapters:

```text
047_Resilience4j_TimeLimiter_Advanced_Composition.md
048_Kafka_Retry_DLT.md
049_Observability_Metrics_Tracing.md
050_Distributed_Rate_Limiter_Redis.md
```
