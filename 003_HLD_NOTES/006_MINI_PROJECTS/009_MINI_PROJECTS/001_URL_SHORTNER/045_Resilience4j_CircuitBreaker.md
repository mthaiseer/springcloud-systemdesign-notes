# 045_Resilience4j_CircuitBreaker.md
# MiniURLShortener — Resilience4j Circuit Breaker

> Core mental model: **A circuit breaker protects your system from repeatedly calling something that is already failing. It turns uncontrolled repeated failure into controlled fast failure or fallback.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What Problem Circuit Breaker Solves](#4-what-problem-circuit-breaker-solves)
- [5. Circuit Breaker States](#5-circuit-breaker-states)
- [6. CLOSED State](#6-closed-state)
- [7. OPEN State](#7-open-state)
- [8. HALF_OPEN State](#8-half_open-state)
- [9. Sliding Window Mental Model](#9-sliding-window-mental-model)
- [10. Failure Rate Threshold](#10-failure-rate-threshold)
- [11. Slow Call Rate Threshold](#11-slow-call-rate-threshold)
- [12. Where Circuit Breaker Fits In MiniURLShortener](#12-where-circuit-breaker-fits-in-miniurlshortener)
- [13. Circuit Breaker vs Retry vs Timeout vs Rate Limiter](#13-circuit-breaker-vs-retry-vs-timeout-vs-rate-limiter)
- [14. Maven Dependencies](#14-maven-dependencies)
- [15. Application YAML Configuration](#15-application-yaml-configuration)
- [16. External Abuse Check Client Example](#16-external-abuse-check-client-example)
- [17. Using @CircuitBreaker Annotation](#17-using-circuitbreaker-annotation)
- [18. Fallback Method Design](#18-fallback-method-design)
- [19. Programmatic Circuit Breaker Alternative](#19-programmatic-circuit-breaker-alternative)
- [20. Create Short URL Flow With Circuit Breaker](#20-create-short-url-flow-with-circuit-breaker)
- [21. Redirect Flow With Circuit Breaker](#21-redirect-flow-with-circuit-breaker)
- [22. Step-by-Step Dry Runs](#22-step-by-step-dry-runs)
- [23. Internal Execution Walkthrough](#23-internal-execution-walkthrough)
- [24. Production Configuration Strategy](#24-production-configuration-strategy)
- [25. Observability And Actuator](#25-observability-and-actuator)
- [26. Metrics To Watch](#26-metrics-to-watch)
- [27. Testing Strategy](#27-testing-strategy)
- [28. Production Failure Stories](#28-production-failure-stories)
- [29. Debugging Mindset](#29-debugging-mindset)
- [30. Common Mistakes](#30-common-mistakes)
- [31. Interview-Ready Explanation](#31-interview-ready-explanation)
- [32. Senior Engineer Checklist](#32-senior-engineer-checklist)
- [33. One-Page Cheat Sheet](#33-one-page-cheat-sheet)
- [34. One Picture To Remember](#34-one-picture-to-remember)

---

## 1. Why This Exists

In earlier MiniURLShortener chapters, the system was mostly focused on:

```text
create short URL
redirect short URL
validate input
handle errors
discover services
route traffic through gateway
load configuration from config server
```

But real production systems call other systems.

MiniURLShortener may call:

```text
1. Abuse detection service
2. User service
3. Analytics service
4. Notification service
5. Billing service
6. External malware scanner
7. External preview metadata service
8. Redis
9. Database
10. Kafka
```

If one dependency becomes slow or unhealthy, your service can collapse.

Example:

```text
CreateShortUrl API calls AbuseCheckService.

Normally:
AbuseCheckService responds in 50 ms.

During outage:
AbuseCheckService times out after 5 seconds.
```

Now imagine 1000 requests per second.

```text
1000 RPS * 5 seconds wait = 5000 threads/requests stuck
```

Your application becomes slow even if your own code is fine.

Bad production result:

```text
Abuse service fails.
URL shortener waits.
Tomcat threads get blocked.
DB connections stay open longer.
CPU rises.
Queue grows.
p99 latency explodes.
Gateway timeout increases.
Users see random 500/504.
```

Circuit breaker exists to prevent this chain reaction.

Mental model:

```text
If a downstream dependency is burning, stop touching the fire repeatedly.
```

---

## 2. The One Core Mental Model

Circuit breaker is like an electrical circuit breaker in a house.

When too much current flows:

```text
breaker trips
power is cut
house is protected
```

In software:

```text
too many failures or slow calls happen
breaker opens
calls are stopped temporarily
service is protected
```

ASCII:

```text
Normal:
Client ---> URL Service ---> Abuse Service
                     success

Failure storm:
Client ---> URL Service ---> Abuse Service X
Client ---> URL Service ---> Abuse Service X
Client ---> URL Service ---> Abuse Service X

Circuit breaker:
Client ---> URL Service --X--> Abuse Service
                     |
                     v
              fast fallback / fail fast
```

One-line memory:

```text
Circuit breaker protects the caller by stopping calls to a dependency that is already failing.
```

This is not mainly about making the failed dependency healthy.

It is mainly about protecting your own service.

---

## 3. Problem Statement

Build Resilience4j Circuit Breaker support for MiniURLShortener.

We want to protect calls to external or remote dependencies such as:

```text
AbuseCheckService
AnalyticsService
UserService
NotificationService
PreviewMetadataService
```

The design must support:

```text
1. Stop repeated calls when failure rate is high.
2. Stop repeated calls when dependency is too slow.
3. Return safe fallback when possible.
4. Fail fast when fallback is unsafe.
5. Expose health and metrics.
6. Make behavior configurable.
7. Avoid hiding critical correctness failures.
```

Out of scope:

```text
1. Full distributed tracing setup.
2. Full monitoring dashboard.
3. Redis/Kafka deep resilience.
4. Bulkhead deep dive.
5. Retry deep dive.
```

This chapter focuses on Circuit Breaker only.

---

## 4. What Problem Circuit Breaker Solves

Without circuit breaker:

```text
URL Service keeps calling bad dependency.
Every call waits until timeout.
Threads pile up.
Latency grows.
Failures spread.
```

ASCII:

```text
             Dependency is down
                    X
                    |
                    v
+---------+    +-------------+    +----------------+
| Client  | -> | URL Service | -> | Abuse Service  |
+---------+    +-------------+    +----------------+
                    |
                    v
          waits... waits... waits...
                    |
                    v
             thread pool exhaustion
```

With circuit breaker:

```text
After enough failures, stop calling dependency.
Return fallback quickly.
Protect URL Service.
Give dependency time to recover.
```

ASCII:

```text
             Dependency is down
                    X
                    |
                    v
+---------+    +-------------+    +----------------+
| Client  | -> | URL Service | -X | Abuse Service  |
+---------+    +-------------+    +----------------+
                    |
                    v
              fallback quickly
                    |
                    v
              service stays alive
```

Circuit breaker solves:

```text
1. Failure amplification
2. Slow dependency damage
3. Thread starvation
4. Cascading failures
5. Repeated useless calls
6. Long p99 latency
```

Circuit breaker does not solve:

```text
1. Bad database schema
2. Wrong business logic
3. Missing indexes
4. Memory leaks
5. Dependency recovery itself
```

It is a damage-control pattern.

---

## 5. Circuit Breaker States

A circuit breaker has three main states:

```text
CLOSED
OPEN
HALF_OPEN
```

ASCII:

```text
                        failures high
        +------------------------------------------+
        |                                          v
+----------------+                         +----------------+
| CLOSED         |                         | OPEN           |
| allow calls    |                         | block calls    |
| record result  |                         | fast fail      |
+----------------+                         +----------------+
        ^                                          |
        |                                          |
        | enough trial calls succeed               | wait duration passes
        |                                          v
+----------------+ <----------------------- +----------------+
| HALF_OPEN      |        test few calls
| allow limited  |
| trial calls    |
+----------------+
```

Meaning:

```text
CLOSED:
    Everything is normal.
    Calls are allowed.
    Resilience4j records success/failure/slow calls.

OPEN:
    Dependency is considered unhealthy.
    Calls are not allowed.
    Caller gets CallNotPermittedException or fallback.

HALF_OPEN:
    After waiting, breaker allows a few trial calls.
    If trial calls succeed, go CLOSED.
    If trial calls fail, go OPEN again.
```

Simple memory:

```text
CLOSED = trust dependency
OPEN = stop calling dependency
HALF_OPEN = test if dependency recovered
```

---

## 6. CLOSED State

CLOSED is the normal state.

In CLOSED state:

```text
1. Calls go to dependency.
2. Circuit breaker records outcome.
3. Success increases healthy count.
4. Failure increases failure count.
5. Slow call increases slow-call count.
6. If threshold is crossed, breaker opens.
```

ASCII:

```text
CLOSED STATE

Request
  |
  v
CircuitBreaker
  |
  v
Dependency call allowed
  |
  +-- success --> record success
  |
  +-- failure --> record failure
  |
  +-- slow ----> record slow call
  |
  v
If failure rate too high -> OPEN
```

Example:

```text
Sliding window size = 10
Failure threshold = 50%

Last 10 calls:
S S F F F F S F S F

Failures = 6
Failure rate = 60%

60% >= 50%
Breaker opens.
```

CLOSED does not mean no failures.

It means failures are still below threshold.

---

## 7. OPEN State

OPEN means the dependency is considered unhealthy.

In OPEN state:

```text
1. Calls do not go to dependency.
2. Calls fail fast.
3. Fallback may run.
4. After waitDurationInOpenState, breaker moves to HALF_OPEN.
```

ASCII:

```text
OPEN STATE

Request
  |
  v
CircuitBreaker
  |
  v
Is OPEN?
  |
  +-- yes --> do not call dependency
              |
              v
          fallback / fast failure
```

Why OPEN is useful:

```text
1. Saves threads.
2. Reduces useless network calls.
3. Prevents dependency overload.
4. Gives dependency recovery time.
5. Keeps caller responsive.
```

Example:

```text
Abuse service is down.
Circuit breaker opens.

Instead of waiting 5 seconds per request:
URL service returns fallback in 5 ms.
```

This protects p99 latency.

---

## 8. HALF_OPEN State

HALF_OPEN is the recovery test state.

After waiting in OPEN state, Resilience4j allows a small number of calls.

Example:

```text
permittedNumberOfCallsInHalfOpenState = 3
```

ASCII:

```text
HALF_OPEN STATE

OPEN wait time passed
       |
       v
Allow 3 trial calls
       |
       +-- all/most succeed --> CLOSED
       |
       +-- failures high ----> OPEN again
```

Trial calls:

```text
Call 1 -> success
Call 2 -> success
Call 3 -> success
```

Then:

```text
Dependency looks healthy.
Breaker closes.
```

Failure example:

```text
Call 1 -> success
Call 2 -> timeout
Call 3 -> timeout
```

Then:

```text
Dependency still bad.
Breaker opens again.
```

HALF_OPEN prevents a thundering herd after recovery.

Bad idea:

```text
After outage, allow all traffic immediately.
```

Better:

```text
Allow small trial traffic first.
```

---

## 9. Sliding Window Mental Model

Circuit breaker needs memory.

It asks:

```text
How many recent calls failed?
How many recent calls were slow?
```

This memory is the sliding window.

Two common window types:

```text
COUNT_BASED
TIME_BASED
```

COUNT_BASED:

```text
Last N calls
```

TIME_BASED:

```text
Calls during last N seconds
```

ASCII count-based window:

```text
slidingWindowSize = 10

Old                                      New
 |                                        |
 v                                        v
+---+---+---+---+---+---+---+---+---+---+
| S | F | S | F | F | S | S | F | F | F |
+---+---+---+---+---+---+---+---+---+---+

S = success
F = failure

Failures = 6 / 10 = 60%
```

When one new call arrives:

```text
Oldest result drops out.
Newest result enters.
```

ASCII:

```text
Before:
[S F S F F S S F F F]

New call = S

After:
[F S F F S S F F F S]
```

Why not use lifetime failure rate?

Because old history should not punish a recovered system forever.

---

## 10. Failure Rate Threshold

Failure rate threshold decides when breaker opens.

Example config:

```yaml
failure-rate-threshold: 50
minimum-number-of-calls: 10
sliding-window-size: 10
```

Meaning:

```text
Do not calculate until at least 10 calls exist.
If 50% or more of recent calls failed, open breaker.
```

Example:

```text
Last 10 calls:
S S S F S F F F S F

Failures = 5
Failure rate = 50%
Breaker opens.
```

ASCII:

```text
+-------------------+
| Last 10 calls     |
+-------------------+
| 5 failed          |
| 5 succeeded       |
+-------------------+
          |
          v
 failureRate = 50%
          |
          v
 threshold = 50%
          |
          v
       OPEN
```

Minimum number of calls matters.

Bad config:

```yaml
minimum-number-of-calls: 1
failure-rate-threshold: 50
```

Then one failure can open the breaker.

That may be too sensitive.

Production mindset:

```text
Small window = reacts fast, but noisy.
Large window = stable, but slower to react.
```

---

## 11. Slow Call Rate Threshold

Not all damage comes from failures.

Slow calls are also dangerous.

Example:

```text
Dependency responds 200 OK but takes 5 seconds.
```

Technically success.
Operationally harmful.

Resilience4j can treat slow calls as a signal.

Config:

```yaml
slow-call-duration-threshold: 2s
slow-call-rate-threshold: 50
```

Meaning:

```text
If 50% of calls are slower than 2 seconds, open breaker.
```

ASCII:

```text
Dependency returns success but slowly:

Call 1: 80 ms    OK
Call 2: 90 ms    OK
Call 3: 3100 ms  SLOW
Call 4: 3300 ms  SLOW
Call 5: 3400 ms  SLOW
Call 6: 70 ms    OK

Slow calls = 3 / 6 = 50%
Breaker can open.
```

Why this matters:

```text
A slow dependency can kill your service before it fully fails.
```

Senior mindset:

```text
Timeouts protect individual calls.
Circuit breakers protect the system from repeated bad calls.
Slow-call threshold catches brownouts before full outage.
```

Brownout means:

```text
Service is not fully down, but too slow to be useful.
```

---

## 12. Where Circuit Breaker Fits In MiniURLShortener

Do not put circuit breaker around every method blindly.

Use it around remote or unstable dependencies.

Good candidates:

```text
1. AbuseCheckClient
2. UserProfileClient
3. AnalyticsClient
4. NotificationClient
5. PreviewMetadataClient
6. External malware scanner
```

Usually not first choice for:

```text
1. Pure local validation method
2. Simple mapper method
3. In-memory Base62 encoding
4. Domain calculation
```

Maybe for Redis/DB?

```text
Possible, but be careful.
Database and Redis usually need separate timeout, pool, retry, and fallback strategy.
```

MiniURLShortener example:

```text
Create Short URL:
    validate URL
    call abuse checker
    save URL
    publish analytics event

Redirect:
    lookup code
    optionally publish click event
    redirect
```

ASCII create flow:

```text
POST /api/v1/urls
      |
      v
+-------------------+
| URL Service       |
+-------------------+
      |
      v
+---------------------------+
| Circuit Breaker           |
| abuseCheckCircuitBreaker  |
+---------------------------+
      |
      +-- CLOSED ----> Abuse Service
      |
      +-- OPEN ------> fallback decision
      |
      v
Save short URL
```

Fallback decision is business-sensitive.

For abuse check failure:

```text
Option A: fail closed
    reject creation when abuse checker is unavailable

Option B: fail open
    allow creation but mark as PENDING_REVIEW

Option C: degrade
    allow only trusted users
```

For production security, fail-open may be risky.

---

## 13. Circuit Breaker vs Retry vs Timeout vs Rate Limiter

These patterns are related but not the same.

```text
Timeout:
    How long am I willing to wait for one call?

Retry:
    Should I try again after a failed call?

Circuit Breaker:
    Should I stop calling this dependency temporarily?

Rate Limiter:
    How many calls should I allow per time unit?

Bulkhead:
    How many resources should this dependency consume?
```

ASCII:

```text
Request
  |
  v
RateLimiter: too many requests?
  |
  v
Bulkhead: resource slot available?
  |
  v
CircuitBreaker: dependency healthy?
  |
  v
TimeLimiter/Timeout: max wait for one call
  |
  v
Retry: try again if safe
  |
  v
Dependency
```

Table:

```text
+-----------------+------------------------------------------+
| Pattern         | Main Question                            |
+-----------------+------------------------------------------+
| Timeout         | How long should one call wait?           |
| Retry           | Should I repeat this failed call?        |
| Circuit Breaker | Should I stop calling for now?           |
| Rate Limiter    | How many calls are allowed?              |
| Bulkhead        | How many resources can be consumed?      |
+-----------------+------------------------------------------+
```

Important:

```text
Retry without circuit breaker can worsen outages.
```

Bad:

```text
Dependency failing.
Every request retries 3 times.
Traffic to bad dependency triples.
```

Better:

```text
Retry small transient failures.
Circuit breaker stops repeated failure storms.
```

---

## 14. Maven Dependencies

For Spring Boot 3 and Resilience4j:

```xml
<dependencies>
    <!-- Resilience4j Spring Boot integration -->
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot3</artifactId>
    </dependency>

    <!-- Needed for @CircuitBreaker AOP annotations -->
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-annotations</artifactId>
    </dependency>

    <!-- Spring AOP because annotations are applied through proxy -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>

    <!-- Actuator for health/metrics exposure -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

If using Spring Cloud CircuitBreaker abstraction:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
```

For this chapter, direct Resilience4j annotation usage is enough.

Important:

```text
@CircuitBreaker works through Spring AOP proxy.
Self-invocation problem applies.
```

Wrong:

```java
this.callAbuseService();
```

Correct:

```text
Call annotated method through Spring bean proxy.
```

---

## 15. Application YAML Configuration

Example:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      abuseCheckService:
        sliding-window-type: COUNT_BASED
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        slow-call-rate-threshold: 50
        slow-call-duration-threshold: 2s
        wait-duration-in-open-state: 10s
        permitted-number-of-calls-in-half-open-state: 3
        automatic-transition-from-open-to-half-open-enabled: true
        register-health-indicator: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,circuitbreakers,circuitbreakerevents
  endpoint:
    health:
      show-details: always
  health:
    circuitbreakers:
      enabled: true
```

Meaning:

```text
sliding-window-size:
    Number of recent calls remembered.

minimum-number-of-calls:
    Minimum calls before calculating failure rate.

failure-rate-threshold:
    Failure percentage that opens breaker.

slow-call-duration-threshold:
    Call slower than this is considered slow.

slow-call-rate-threshold:
    Slow call percentage that opens breaker.

wait-duration-in-open-state:
    How long breaker stays OPEN before testing recovery.

permitted-number-of-calls-in-half-open-state:
    Number of trial calls in HALF_OPEN.

register-health-indicator:
    Expose circuit breaker health in actuator.
```

ASCII:

```text
Last 10 calls
  |
  v
Failure >= 50%?
  |
  +-- yes --> OPEN for 10s
  |
  v
HALF_OPEN with 3 trial calls
  |
  +-- good --> CLOSED
  +-- bad  --> OPEN again
```

---

## 16. External Abuse Check Client Example

Imagine an external service:

```text
GET http://abuse-service/api/v1/check?url=https://example.com
```

Response:

```json
{
  "safe": true,
  "reason": "clean"
}
```

DTO:

```java
package com.miniurl.shortener.abuse;

public class AbuseCheckResult {

    private boolean safe;
    private String reason;

    public AbuseCheckResult() {
    }

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

Client interface:

```java
package com.miniurl.shortener.abuse;

public interface AbuseCheckClient {
    AbuseCheckResult check(String longUrl);
}
```

RestClient implementation:

```java
package com.miniurl.shortener.abuse;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HttpAbuseCheckClient implements AbuseCheckClient {

    private final RestClient restClient;

    public HttpAbuseCheckClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://abuse-service")
                .build();
    }

    @Override
    public AbuseCheckResult check(String longUrl) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/check")
                        .queryParam("url", longUrl)
                        .build())
                .retrieve()
                .body(AbuseCheckResult.class);
    }
}
```

In production, also configure:

```text
connect timeout
read timeout
service discovery
retry policy if safe
circuit breaker
metrics
```

Do not call external services without timeout.

Circuit breaker without timeout is incomplete.

---

## 17. Using @CircuitBreaker Annotation

Create service wrapper:

```java
package com.miniurl.shortener.abuse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class AbuseCheckService {

    private final AbuseCheckClient abuseCheckClient;

    public AbuseCheckService(AbuseCheckClient abuseCheckClient) {
        this.abuseCheckClient = abuseCheckClient;
    }

    @CircuitBreaker(
            name = "abuseCheckService",
            fallbackMethod = "fallbackAbuseCheck"
    )
    public AbuseCheckResult checkUrlSafety(String longUrl) {
        return abuseCheckClient.check(longUrl);
    }

    public AbuseCheckResult fallbackAbuseCheck(String longUrl, Throwable throwable) {
        /*
         * Security-sensitive choice:
         * For this example, we fail closed.
         * That means if abuse service is unavailable,
         * we do not silently mark URL safe.
         */
        return new AbuseCheckResult(false, "abuse-check-unavailable");
    }
}
```

How this works:

```text
1. Spring creates proxy around AbuseCheckService.
2. Controller/service calls proxy method.
3. Proxy asks circuit breaker if call is allowed.
4. If CLOSED, real method executes.
5. Result/failure is recorded.
6. If OPEN, fallback executes immediately.
```

ASCII:

```text
Caller
  |
  v
Spring Proxy
  |
  v
Resilience4j CircuitBreaker
  |
  +-- call allowed --> real method --> AbuseCheckClient
  |
  +-- call blocked --> fallbackAbuseCheck
```

Important fallback signature:

```java
public ReturnType fallbackMethod(originalArgs..., Throwable throwable)
```

So for:

```java
public AbuseCheckResult checkUrlSafety(String longUrl)
```

Fallback can be:

```java
public AbuseCheckResult fallbackAbuseCheck(String longUrl, Throwable throwable)
```

---

## 18. Fallback Method Design

Fallback is not a magic backup.

Fallback must be correct for business behavior.

Three fallback strategies:

```text
1. Fail closed
2. Fail open
3. Degraded mode
```

### Strategy 1: Fail Closed

For security-sensitive operation:

```text
If abuse checker unavailable, reject or mark unsafe.
```

Code idea:

```java
return new AbuseCheckResult(false, "abuse-check-unavailable");
```

Pros:

```text
Safer.
Avoids malicious URL creation.
```

Cons:

```text
May block legitimate users during dependency outage.
```

### Strategy 2: Fail Open

```text
If abuse checker unavailable, allow URL.
```

Code idea:

```java
return new AbuseCheckResult(true, "abuse-check-skipped");
```

Pros:

```text
Better availability.
```

Cons:

```text
Security risk.
Bad URLs may enter system.
```

### Strategy 3: Degraded Mode

```text
Allow only trusted users or mark URL PENDING_REVIEW.
```

Better production design:

```text
User trusted?
    yes -> allow but mark CHECK_PENDING
    no  -> reject temporarily
```

ASCII:

```text
Abuse Service unavailable
        |
        v
+----------------------+
| fallback decision    |
+----------------------+
        |
        +-- trusted user --> create with PENDING_REVIEW
        |
        +-- anonymous ----> reject 503/400 safe error
```

Senior rule:

```text
Never add fallback just to hide failure.
Fallback must preserve business correctness.
```

---

## 19. Programmatic Circuit Breaker Alternative

Annotation is simple.

Programmatic style gives more control.

Example:

```java
package com.miniurl.shortener.abuse;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class ProgrammaticAbuseCheckService {

    private final AbuseCheckClient client;
    private final CircuitBreaker circuitBreaker;

    public ProgrammaticAbuseCheckService(
            AbuseCheckClient client,
            io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry registry
    ) {
        this.client = client;
        this.circuitBreaker = registry.circuitBreaker("abuseCheckService");
    }

    public AbuseCheckResult check(String longUrl) {
        Supplier<AbuseCheckResult> supplier =
                CircuitBreaker.decorateSupplier(
                        circuitBreaker,
                        () -> client.check(longUrl)
                );

        return Try.ofSupplier(supplier)
                .recover(throwable ->
                        new AbuseCheckResult(false, "abuse-check-unavailable"))
                .get();
    }
}
```

Use annotation when:

```text
1. Simple service method
2. Clear fallback
3. Spring bean proxy is acceptable
```

Use programmatic when:

```text
1. You need dynamic decisions
2. You compose multiple resilience patterns manually
3. You want explicit functional style
4. You are outside Spring-managed bean
```

For most Spring Boot backend services:

```text
Annotation is enough.
```

---

## 20. Create Short URL Flow With Circuit Breaker

Create API with abuse check:

```text
POST /api/v1/urls
```

Flow:

```text
1. Validate request DTO.
2. Validate longUrl format.
3. Call abuse checker through circuit breaker.
4. If unsafe, reject.
5. Generate short code.
6. Save row.
7. Return response.
```

ASCII:

```text
POST /api/v1/urls
       |
       v
+--------------------+
| Controller         |
+--------------------+
       |
       v
+--------------------+
| UrlService         |
+--------------------+
       |
       v
+----------------------------+
| AbuseCheckService Proxy    |
| @CircuitBreaker            |
+----------------------------+
       |
       +-- CLOSED --> call abuse-service
       |
       +-- OPEN ----> fallback result
       |
       v
+--------------------+
| Safety decision    |
+--------------------+
       |
       +-- unsafe --> reject
       |
       +-- safe ----> save short URL
```

Service code:

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.abuse.AbuseCheckResult;
import com.miniurl.shortener.abuse.AbuseCheckService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlService {

    private final AbuseCheckService abuseCheckService;
    private final ShortCodeGenerator shortCodeGenerator;
    private final ShortUrlRepository repository;

    public UrlService(
            AbuseCheckService abuseCheckService,
            ShortCodeGenerator shortCodeGenerator,
            ShortUrlRepository repository
    ) {
        this.abuseCheckService = abuseCheckService;
        this.shortCodeGenerator = shortCodeGenerator;
        this.repository = repository;
    }

    @Transactional
    public CreateShortUrlResponse create(CreateShortUrlCommand command) {
        AbuseCheckResult result =
                abuseCheckService.checkUrlSafety(command.longUrl());

        if (!result.isSafe()) {
            throw new UnsafeUrlException(
                    "URL rejected by abuse check: " + result.getReason()
            );
        }

        String shortCode = command.customAlias() != null
                ? command.customAlias()
                : shortCodeGenerator.generate();

        ShortUrl entity = ShortUrl.create(
                shortCode,
                command.longUrl(),
                command.expiresAt()
        );

        ShortUrl saved = repository.save(entity);

        return CreateShortUrlResponse.from(saved);
    }
}
```

Exception:

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class UnsafeUrlException extends ApiException {

    public UnsafeUrlException(String message) {
        super("UNSAFE_URL", HttpStatus.BAD_REQUEST, message);
    }
}
```

Production nuance:

```text
Do not keep DB transaction open while making slow remote calls if avoidable.
```

Better order:

```text
validate
remote abuse check
then open DB transaction for save
```

If the method is transactional and remote call happens inside it, a slow dependency can hold DB resources longer.

---

## 21. Redirect Flow With Circuit Breaker

Redirect path should be extremely fast.

```text
GET /abc123
```

Do not put a blocking external call in the hot redirect path unless necessary.

Bad:

```text
Every redirect calls remote analytics synchronously.
```

Problem:

```text
Analytics outage slows user redirect.
```

Better:

```text
Redirect immediately.
Publish analytics event async.
Protect analytics publisher separately.
```

ASCII:

```text
GET /abc123
    |
    v
Lookup URL
    |
    v
Return 302 immediately
    |
    v
Async analytics event
    |
    v
CircuitBreaker around analytics dependency
```

If redirect path calls analytics:

```text
User experience depends on analytics health.
```

That is usually wrong.

Correct design:

```text
Redirect path correctness:
    DB/Redis lookup matters.

Analytics:
    best-effort async side effect.
```

If analytics fails:

```text
Do not fail redirect.
```

Fallback:

```text
store event in local queue
send to Kafka later
or drop with metric if acceptable
```

Senior principle:

```text
Do not put non-critical remote dependencies in critical request path.
```

---

## 22. Step-by-Step Dry Runs

### Dry Run 1: Dependency Healthy

Config:

```text
failure-rate-threshold = 50%
sliding-window-size = 10
state = CLOSED
```

Calls:

```text
1. Client creates URL.
2. URL service calls abuse checker.
3. Circuit breaker is CLOSED.
4. Call goes to abuse service.
5. Abuse service returns safe=true.
6. Breaker records success.
7. URL is created.
```

ASCII:

```text
Request -> CircuitBreaker(CLOSED) -> AbuseService OK -> record success
```

Result:

```text
201 Created
```

---

### Dry Run 2: Dependency Starts Failing

Last 10 calls:

```text
S F F S F F S F F F
```

Count:

```text
Failures = 7
Total = 10
Failure rate = 70%
Threshold = 50%
```

Flow:

```text
1. Circuit breaker records failures.
2. Failure rate crosses threshold.
3. State changes CLOSED -> OPEN.
4. Future calls no longer hit abuse service.
```

ASCII:

```text
CLOSED
  |
  | failure rate 70%
  v
OPEN
```

Result:

```text
Calls are short-circuited.
Fallback runs.
```

---

### Dry Run 3: OPEN State Fast Fallback

State:

```text
OPEN
```

Request:

```text
POST /api/v1/urls
```

Flow:

```text
1. URL service calls checkUrlSafety().
2. Proxy asks circuit breaker.
3. Circuit breaker rejects call.
4. Abuse service is not called.
5. fallbackAbuseCheck() runs.
6. Fallback returns safe=false.
7. URL service rejects URL.
```

ASCII:

```text
Request
  |
  v
CircuitBreaker OPEN
  |
  +--X no remote call
  |
  v
fallback safe=false
  |
  v
reject safely
```

Result:

```json
{
  "code": "UNSAFE_URL",
  "message": "URL rejected by abuse check: abuse-check-unavailable"
}
```

---

### Dry Run 4: HALF_OPEN Recovery

State:

```text
OPEN for 10 seconds
```

After wait:

```text
HALF_OPEN
permitted trial calls = 3
```

Trial calls:

```text
Call 1 -> success
Call 2 -> success
Call 3 -> success
```

Flow:

```text
1. Breaker allows limited calls.
2. All trial calls succeed.
3. Breaker closes.
4. Normal traffic resumes.
```

ASCII:

```text
OPEN
 |
 | wait 10s
 v
HALF_OPEN
 |
 | 3 successful trial calls
 v
CLOSED
```

---

### Dry Run 5: Brownout Slow Calls

Abuse service is not failing, but slow.

Calls:

```text
Call 1: 2500 ms
Call 2: 3000 ms
Call 3: 50 ms
Call 4: 2700 ms
Call 5: 60 ms
```

Config:

```text
slow-call-duration-threshold = 2s
slow-call-rate-threshold = 50%
```

Slow calls:

```text
3 out of 5 = 60%
```

Flow:

```text
1. Breaker records slow calls.
2. Slow call rate crosses threshold.
3. Breaker opens.
4. Future calls fail fast/fallback.
```

This catches slow dependency before it fully fails.

---

## 23. Internal Execution Walkthrough

With annotation:

```java
@CircuitBreaker(name = "abuseCheckService", fallbackMethod = "fallbackAbuseCheck")
public AbuseCheckResult checkUrlSafety(String longUrl) {
    return abuseCheckClient.check(longUrl);
}
```

Internal flow:

```text
1. Spring starts application.
2. AOP proxy is created for AbuseCheckService.
3. Caller injects AbuseCheckService bean.
4. Caller invokes checkUrlSafety().
5. Call enters proxy first, not method directly.
6. Proxy asks Resilience4j CircuitBreaker:
       Is call permitted?
7. If not permitted:
       throw CallNotPermittedException internally or invoke fallback.
8. If permitted:
       execute real method.
9. If method succeeds:
       record success.
10. If method throws:
       record failure.
11. If method is slow:
       record slow call.
12. Based on sliding window, state may change.
```

ASCII:

```text
Caller
  |
  v
+--------------------------+
| Spring AOP Proxy         |
+--------------------------+
  |
  v
+--------------------------+
| Resilience4j State Check |
+--------------------------+
  |
  +-- OPEN ------> fallback
  |
  +-- CLOSED ----> real method
  |
  +-- HALF_OPEN -> limited trial
```

Self-invocation problem:

```java
@Service
public class AbuseCheckService {

    public void outer(String url) {
        this.checkUrlSafety(url); // wrong for AOP interception
    }

    @CircuitBreaker(name = "abuseCheckService")
    public AbuseCheckResult checkUrlSafety(String url) {
        return client.check(url);
    }
}
```

Why wrong?

```text
this.checkUrlSafety() bypasses Spring proxy.
Circuit breaker does not run.
```

Correct:

```text
Keep annotated method in separate Spring bean and call it from another bean.
```

---

## 24. Production Configuration Strategy

Do not copy random thresholds blindly.

Think from dependency behavior.

For fast internal service:

```yaml
sliding-window-size: 50
minimum-number-of-calls: 20
failure-rate-threshold: 50
slow-call-duration-threshold: 500ms
wait-duration-in-open-state: 10s
```

For external third-party API:

```yaml
sliding-window-size: 20
minimum-number-of-calls: 10
failure-rate-threshold: 50
slow-call-duration-threshold: 2s
wait-duration-in-open-state: 30s
```

For low traffic service:

```yaml
sliding-window-type: TIME_BASED
sliding-window-size: 60
minimum-number-of-calls: 5
```

Why low traffic may prefer TIME_BASED:

```text
If only 3 calls happen per minute, COUNT_BASED window of 100 may take too long to react.
```

Tuning table:

```text
+-----------------------------+-----------------------------+
| Symptom                     | Config Direction            |
+-----------------------------+-----------------------------+
| Breaker opens too easily    | increase minimum calls/window|
| Breaker reacts too slowly   | decrease window size         |
| Slow brownouts hurt p99     | lower slow duration threshold|
| Dependency needs more rest  | increase open wait duration  |
| Recovery causes traffic rush| lower half-open trial calls  |
+-----------------------------+-----------------------------+
```

Production rule:

```text
Start conservative.
Observe metrics.
Tune based on real p95/p99 and failure patterns.
```

---

## 25. Observability And Actuator

Expose actuator endpoints:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,circuitbreakers,circuitbreakerevents
```

Useful endpoints:

```text
GET /actuator/health
GET /actuator/circuitbreakers
GET /actuator/circuitbreakerevents
GET /actuator/metrics/resilience4j.circuitbreaker.calls
GET /actuator/metrics/resilience4j.circuitbreaker.state
```

Example mental output:

```text
Circuit breaker:
    name = abuseCheckService
    state = OPEN
    failureRate = 70%
    slowCallRate = 20%
```

ASCII:

```text
Runtime calls
    |
    v
Resilience4j records metrics
    |
    v
Micrometer
    |
    v
Actuator endpoint
    |
    v
Prometheus/Grafana later
```

Health indicator:

```text
If circuit breaker is OPEN, health may show degraded.
```

Important:

```text
Do not alert on one temporary OPEN event blindly.
Alert on sustained OPEN state or customer impact.
```

---

## 26. Metrics To Watch

Key circuit breaker metrics:

```text
1. State: CLOSED / OPEN / HALF_OPEN
2. Failure rate
3. Slow call rate
4. Number of successful calls
5. Number of failed calls
6. Number of slow successful calls
7. Number of slow failed calls
8. Number of not permitted calls
9. State transitions
10. Fallback count
```

Most important production signals:

```text
OPEN state duration
not permitted calls
failure rate
slow call rate
p95/p99 latency
downstream timeout count
```

Debug meaning:

```text
High failed calls:
    dependency returns errors or timeouts.

High slow calls:
    dependency brownout.

High not permitted calls:
    breaker is OPEN and blocking calls.

Frequent OPEN -> HALF_OPEN -> OPEN:
    dependency is not recovering.

Always CLOSED but high p99:
    slow-call threshold may be too loose or timeout too high.
```

ASCII diagnosis:

```text
p99 high
 |
 v
Check slow call rate
 |
 +-- high --> dependency brownout
 |
 +-- low ---> check DB/threads/Gateway
```

---

## 27. Testing Strategy

Test circuit breaker behavior at three levels.

### 1. Unit Test Fallback Logic

```text
Given abuse checker unavailable
When fallback runs
Then result is safe=false
```

### 2. Integration Test With Mock Client

Pseudo test:

```java
@Test
void shouldReturnFallbackWhenAbuseServiceFails() {
    // mock client throws RuntimeException
    // call service multiple times
    // assert fallback result is unsafe
}
```

### 3. State Transition Test

Use small config in test:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      abuseCheckService:
        sliding-window-size: 4
        minimum-number-of-calls: 4
        failure-rate-threshold: 50
        wait-duration-in-open-state: 1s
        permitted-number-of-calls-in-half-open-state: 2
```

Test idea:

```text
1. Force 4 calls.
2. Make 2 fail.
3. Breaker opens.
4. Next call should not reach client.
5. After 1s, allow trial calls.
6. Successful trial calls close breaker.
```

Important test assertion:

```text
When breaker is OPEN, dependency client should not be called.
```

Mock verification:

```java
verify(abuseCheckClient, times(4)).check(anyString());
```

Then after OPEN:

```java
verifyNoMoreInteractions(abuseCheckClient);
```

Testing checklist:

```text
[ ] fallback signature correct
[ ] breaker opens after threshold
[ ] OPEN calls do not hit dependency
[ ] HALF_OPEN trial works
[ ] successful recovery closes breaker
[ ] failing recovery reopens breaker
```

---

## 28. Production Failure Stories

### Failure Story 1: Retry Storm Without Circuit Breaker

Scenario:

```text
Abuse service goes down.
URL service retries every request 3 times.
Traffic to abuse service triples.
```

Result:

```text
Dependency cannot recover.
URL service threads blocked.
Gateway starts returning 504.
```

Fix:

```text
Add timeout + circuit breaker.
Use limited retry only for safe transient errors.
```

Lesson:

```text
Retry without circuit breaker can amplify failure.
```

---

### Failure Story 2: Slow 200 OK Kills p99

Scenario:

```text
Abuse service still returns 200 OK.
But response time becomes 4 seconds.
```

Result:

```text
No failure rate spike.
But p99 latency explodes.
Users wait.
Threads pile up.
```

Fix:

```text
Configure slow-call-duration-threshold and slow-call-rate-threshold.
```

Lesson:

```text
Slow success can be as dangerous as failure.
```

---

### Failure Story 3: Bad Fallback Allows Abuse

Scenario:

```text
Abuse checker fails.
Fallback returns safe=true.
Attackers create malicious short URLs.
```

Root cause:

```text
Availability was prioritized without security thinking.
```

Fix:

```text
Fail closed for anonymous users.
Use PENDING_REVIEW for trusted users.
Alert on fallback activation.
```

Lesson:

```text
Fallback must be business-correct, not just convenient.
```

---

### Failure Story 4: Self-Invocation Bypasses Circuit Breaker

Scenario:

```java
this.checkUrlSafety(url);
```

Result:

```text
Circuit breaker annotation never applies.
During outage, all calls still hit dependency.
```

Root cause:

```text
Spring AOP proxy was bypassed.
```

Fix:

```text
Move annotated method to separate bean or call through proxy.
```

Lesson:

```text
Spring annotations often work through proxies.
```

---

### Failure Story 5: Breaker Opens Too Fast

Config:

```yaml
minimum-number-of-calls: 1
failure-rate-threshold: 50
```

One temporary network blip opens breaker.

Result:

```text
Healthy dependency blocked unnecessarily.
```

Fix:

```text
Increase minimum-number-of-calls.
Use realistic sliding window.
```

Lesson:

```text
Too sensitive is also a production bug.
```

---

## 29. Debugging Mindset

When users report failures, ask:

```text
Is dependency actually failing?
Is dependency slow but successful?
Is circuit breaker OPEN?
Are calls being short-circuited?
Is fallback running?
Is fallback business-correct?
Did breaker open from failures or slow calls?
Is timeout configured?
Is retry multiplying traffic?
Is self-invocation bypassing proxy?
```

Debug map:

```text
High 5xx from dependency:
    check failure rate

High latency but low 5xx:
    check slow call rate

Fallback response increasing:
    check circuit breaker state

CallNotPermittedException:
    breaker is OPEN

No circuit breaker metrics:
    annotation may not be applied
    missing dependency
    method not called through Spring bean

Breaker flapping:
    dependency unstable
    half-open trial too aggressive
    wait duration too short
```

Useful logs:

```text
circuitBreakerName
state
stateTransition
exceptionClass
dependencyName
latencyMs
fallbackUsed
correlationId
```

Golden debugging question:

```text
Did the circuit breaker protect us, or did it hide a problem we should fix?
```

---

## 30. Common Mistakes

### Mistake 1: Circuit breaker without timeout

Wrong:

```text
Remote call can hang for 30 seconds.
Circuit breaker only sees result after 30 seconds.
```

Correct:

```text
Set connect/read timeout or TimeLimiter.
```

### Mistake 2: Fallback returns fake success

Wrong:

```java
return new AbuseCheckResult(true, "fallback");
```

Correct:

```text
Choose fallback based on business risk.
```

### Mistake 3: Wrapping local CPU code

Wrong:

```text
Circuit breaker around Base62 encoding.
```

Correct:

```text
Use it around remote/unstable dependencies.
```

### Mistake 4: Self-invocation

Wrong:

```java
this.annotatedMethod();
```

Correct:

```text
Call through Spring-managed proxy.
```

### Mistake 5: No metrics

Wrong:

```text
Breaker opens and nobody knows.
```

Correct:

```text
Expose actuator metrics and alert carefully.
```

### Mistake 6: Too much retry

Wrong:

```text
Retry 5 times before circuit breaker.
```

Correct:

```text
Use small retry only where safe, with timeout and breaker.
```

### Mistake 7: One breaker for all dependencies

Wrong:

```text
Same breaker name for abuse, analytics, notification.
```

Correct:

```text
Separate breakers per dependency.
```

### Mistake 8: Opening breaker on business 4xx

Wrong:

```text
User sends invalid URL.
Count as dependency failure.
```

Correct:

```text
Circuit breaker should measure dependency failures, not normal client errors.
```

---

## 31. Interview-Ready Explanation

If interviewer asks:

```text
How would you protect your URL shortener from a failing downstream service?
```

Strong answer:

```text
I would use timeout plus Resilience4j CircuitBreaker around remote dependency calls,
for example an abuse detection service or notification service. The circuit breaker
starts in CLOSED state and allows calls while recording success, failure, and slow
calls in a sliding window. If the failure rate or slow-call rate crosses a configured
threshold after a minimum number of calls, it moves to OPEN and stops calling the
dependency. Calls then fail fast or go to a business-safe fallback. After a wait
duration, it moves to HALF_OPEN and allows a few trial calls. If those succeed, it
closes again; if not, it opens again. For a URL shortener, I would not blindly return
success in fallback. For security-sensitive abuse checks, I may fail closed or mark
the URL as PENDING_REVIEW. I would expose actuator metrics for state, failure rate,
slow-call rate, and not-permitted calls, and alert on sustained OPEN state.
```

Why this is strong:

```text
1. Mentions timeout.
2. Explains CLOSED, OPEN, HALF_OPEN.
3. Explains sliding window.
4. Mentions slow-call threshold.
5. Discusses fallback correctness.
6. Applies to URL shortener domain.
7. Mentions observability.
8. Avoids blind retry storm.
```

Senior one-liner:

```text
Circuit breaker is not just an error handler; it is a production damage-control mechanism.
```

---

## 32. Senior Engineer Checklist

Before using circuit breaker in production:

```text
[ ] Dependency has timeout configured
[ ] Circuit breaker wraps remote dependency only
[ ] Separate breaker per dependency
[ ] sliding window chosen intentionally
[ ] minimum number of calls is realistic
[ ] failure threshold is realistic
[ ] slow-call threshold is configured
[ ] wait duration in OPEN state is reasonable
[ ] HALF_OPEN trial calls are limited
[ ] fallback preserves business correctness
[ ] fallback is observable
[ ] actuator metrics enabled
[ ] alerts are based on sustained impact
[ ] self-invocation avoided
[ ] tests verify OPEN state does not call dependency
[ ] retry does not amplify outage
[ ] critical path does not depend on non-critical dependency
```

If these are checked, your circuit breaker design is production-shaped.

---

## 33. One-Page Cheat Sheet

```text
Core mental model:
Circuit breaker stops repeatedly calling a failing or slow dependency.

States:
CLOSED:
    allow calls, record results

OPEN:
    block calls, fail fast/fallback

HALF_OPEN:
    allow limited trial calls to test recovery

Main triggers:
failure-rate-threshold
slow-call-rate-threshold

Important config:
sliding-window-size
minimum-number-of-calls
failure-rate-threshold
slow-call-duration-threshold
slow-call-rate-threshold
wait-duration-in-open-state
permitted-number-of-calls-in-half-open-state

Use around:
remote services
external APIs
unstable dependencies

Do not use around:
pure local CPU logic
simple mapper methods
domain calculations

Fallback choices:
fail closed
fail open
degraded mode

URL shortener examples:
abuse checker -> circuit breaker
analytics -> async + circuit breaker
notification -> circuit breaker
redirect path -> avoid non-critical sync dependencies

Production rules:
timeout first
circuit breaker second
retry carefully
observe metrics
do not fake success blindly
```

---

## 34. One Picture To Remember

```text
                 RESILIENCE4J CIRCUIT BREAKER MENTAL MODEL

                              "Stop touching fire"

                                  Requests
                                     |
                                     v
                         +----------------------+
                         | Circuit Breaker      |
                         +----------------------+
                                     |
              +----------------------+----------------------+
              |                                             |
              v                                             v
       CLOSED / HALF_OPEN                                  OPEN
       call dependency                                  block call
              |                                             |
              v                                             v
       +--------------+                              +--------------+
       | Dependency   |                              | Fallback     |
       +--------------+                              | / Fail fast  |
              |                                      +--------------+
              v
       record outcome
              |
              v
+-------------------------------+
| Sliding Window                |
| success / failure / slow call |
+-------------------------------+
              |
              v
+-------------------------------+
| Threshold crossed?            |
+-------------------------------+
              |
        yes   |   no
              v
            OPEN / stay CLOSED


FINAL MEMORY:

Timeout protects one call.
Retry handles small transient failure.
Circuit breaker protects the whole service from repeated downstream failure.
Fallback must be business-correct.
Metrics tell you whether protection is working.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Circuit breaker protects the caller from repeated dependency failure.
2. CLOSED allows calls, OPEN blocks calls, HALF_OPEN tests recovery.
3. Failure rate and slow-call rate decide when the breaker opens.
4. Fallback must preserve business correctness, especially for security-sensitive flows.
5. Timeout, retry, circuit breaker, bulkhead, and rate limiter solve different resilience problems.
```

After this chapter, MiniURLShortener has a production resilience layer for downstream calls.

Next possible chapters:

```text
046_Resilience4j_Retry_TimeLimiter.md
047_Resilience4j_Bulkhead_RateLimiter.md
048_Kafka_Retry_DLT.md
049_Observability_Metrics_Tracing.md
```
