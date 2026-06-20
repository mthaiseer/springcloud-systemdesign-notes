# 021_CircuitBreaker_With_RestClient_WebClient.md

# MiniCircuitBreaker — 021 CircuitBreaker With RestClient WebClient

---

# 1. Why This File Exists

Previous files explained the internal engine:

```text
states
sliding windows
failure rate
slow call rate
thread safety
CAS
HALF_OPEN permit control
```

Now we connect circuit breaker to real Spring Boot HTTP clients:

```text
RestTemplate
RestClient
WebClient
```

In microservices, most production failures happen at:

```text
remote call boundaries
```

Examples:

```text
order-service → payment-service
payment-service → bank API
gateway → auth-service
booking-service → inventory-service
notification-service → email provider
```

These calls can fail because of:

```text
network timeout
connection refusal
DNS failure
HTTP 500
HTTP 503
slow response
connection pool exhaustion
SSL handshake failure
remote service overload
```

This file explains:

```text
CircuitBreaker with RestTemplate
CircuitBreaker with RestClient
CircuitBreaker with WebClient
blocking vs reactive client behavior
timeouts
fallbacks
retry interaction
bulkhead interaction
exception classification
slow-call tracking
Resilience4j annotations
Spring Boot production configs
observability
anti-patterns
```

---

# 2. One-Line Definition

```text
CircuitBreaker protects outbound HTTP calls so one unhealthy remote service does not collapse the caller service.
```

---

# 3. Biggest Mental Model

```text
Your Service
    ↓
HTTP Client
    ↓
CircuitBreaker
    ↓
Remote Service
```

---

# 4. Why HTTP Calls Are Dangerous

A local method call is usually:

```text
fast
in-memory
same process
predictable
```

A remote HTTP call is:

```text
network-based
slow
unreliable
outside your control
```

This is why remote calls need protection.

---

# 5. Remote Call Failure Modes

Common failures:

```text
connect timeout
read timeout
response timeout
HTTP 500
HTTP 502
HTTP 503
HTTP 504
connection reset
DNS lookup failure
SSL handshake error
connection pool full
slow response
```

---

# 6. Cascading Failure Example

```text
Order Service
    ↓
Payment Service slow
    ↓
Order threads block
    ↓
Order queue grows
    ↓
Order service unavailable
```

Circuit breaker interrupts this chain.

---

# 7. Protection Stack

Production HTTP clients usually need:

```text
RateLimiter
Bulkhead
Timeout
Retry
CircuitBreaker
Fallback
```

---

# 8. Protection Stack ASCII

```text
Request
   ↓
RateLimiter
   ↓
Bulkhead
   ↓
Timeout
   ↓
Retry
   ↓
CircuitBreaker
   ↓
HTTP Client
   ↓
Remote Service
```

---

# 9. Client Types In Spring

Spring commonly uses:

```text
RestTemplate
RestClient
WebClient
```

---

# 10. RestTemplate

RestTemplate is:

```text
blocking
older Spring client
thread waits for response
```

---

# 11. RestTemplate Mental Model

```text
Thread sends HTTP request
        ↓
Thread waits
        ↓
Response returns
        ↓
Thread continues
```

---

# 12. RestClient

RestClient is:

```text
modern blocking HTTP client
introduced as newer Spring style
cleaner API than RestTemplate
```

Still:

```text
blocking
```

---

# 13. WebClient

WebClient is:

```text
reactive
non-blocking
built on Reactor
often uses Netty
```

---

# 14. WebClient Mental Model

```text
Send request
    ↓
do not block thread
    ↓
resume when response arrives
```

---

# 15. Blocking vs Reactive

## Blocking

```text
one request can occupy one thread while waiting
```

## Reactive

```text
thread can handle other work while waiting
```

---

# 16. Blocking Thread Exhaustion

Suppose:

```text
Tomcat threads = 200
remote latency = 10 seconds
```

If many calls wait:

```text
all threads become busy
```

Service stops responding.

---

# 17. Blocking ASCII

```text
Request
   ↓
Tomcat Thread
   ↓
HTTP call waits 10 seconds
   ↓
Thread unavailable
```

---

# 18. Reactive Warning

Reactive does not mean:

```text
no failure risk
```

If you block Netty event loop:

```text
entire app can stall
```

Never do blocking work on event loop.

---

# 19. Bad Reactive Example

```java
webClient.get()
        .uri("/payment")
        .retrieve()
        .bodyToMono(String.class)
        .map(value -> {
            Thread.sleep(1000); // bad
            return value;
        });
```

Problem:

```text
blocks event loop
```

---

# 20. Correct Reactive Blocking Isolation

For unavoidable blocking work:

```java
Mono.fromCallable(() -> blockingCall())
        .subscribeOn(Schedulers.boundedElastic());
```

---

# 21. CircuitBreaker Placement

Circuit breaker should wrap:

```text
outbound dependency call
```

Not the entire controller.

---

# 22. Correct Placement ASCII

```text
Controller
   ↓
Service Method
   ↓
CircuitBreaker wraps only HTTP call
   ↓
Remote Service
```

---

# 23. Why Separate Breakers Per Dependency

Bad:

```text
one circuit breaker for all remote calls
```

If payment fails:

```text
inventory also blocked
```

Good:

```text
paymentCircuitBreaker
inventoryCircuitBreaker
shippingCircuitBreaker
```

---

# 24. Separate Breaker ASCII

```text
Payment API   → paymentCB
Inventory API → inventoryCB
Shipping API  → shippingCB
```

Each dependency isolated.

---

# 25. RestTemplate Basic Example

```java
import org.springframework.web.client.RestTemplate;

public class PaymentClient {

    private final RestTemplate restTemplate =
            new RestTemplate();

    public String callPayment() {

        return restTemplate.getForObject(
                "http://payment-service/api/pay",
                String.class
        );
    }
}
```

---

# 26. Problem Without Protection

If payment service is slow:

```text
thread waits
timeout may be missing
queue grows
service degrades
```

---

# 27. RestTemplate With Timeout

```java
@Bean
public RestTemplate restTemplate() {

    SimpleClientHttpRequestFactory factory =
            new SimpleClientHttpRequestFactory();

    factory.setConnectTimeout(1000);
    factory.setReadTimeout(2000);

    return new RestTemplate(factory);
}
```

---

# 28. Timeout Meaning

```text
connect timeout:
maximum time to establish connection

read timeout:
maximum time waiting for response data
```

---

# 29. RestTemplate With CircuitBreaker

```java
@Service
public class PaymentService {

    private final RestTemplate restTemplate;

    public PaymentService(
            RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(
            name = "paymentService",
            fallbackMethod = "paymentFallback"
    )
    public String callPayment() {

        return restTemplate.getForObject(
                "http://payment-service/api/pay",
                String.class
        );
    }

    public String paymentFallback(
            Exception ex) {

        return "PAYMENT_UNAVAILABLE";
    }
}
```

---

# 30. Flow Dry Run — CLOSED

State:

```text
CLOSED
```

Request:

```text
allowed
```

Remote call succeeds.

Circuit records:

```text
success
```

---

# 31. Flow Dry Run — Failure

State:

```text
CLOSED
```

Remote call fails with:

```text
HTTP 503
```

Circuit records:

```text
failure
```

If threshold crossed:

```text
CLOSED → OPEN
```

---

# 32. Flow Dry Run — OPEN

State:

```text
OPEN
```

Request arrives.

Circuit:

```text
does not call remote service
```

Fallback returns:

```text
PAYMENT_UNAVAILABLE
```

---

# 33. Flow Dry Run — HALF_OPEN

State:

```text
HALF_OPEN
```

Only limited probe calls allowed.

If probe succeeds:

```text
HALF_OPEN → CLOSED
```

If probe fails:

```text
HALF_OPEN → OPEN
```

---

# 34. RestClient Example

RestClient is modern blocking client.

```java
import org.springframework.web.client.RestClient;

@Service
public class InventoryClient {

    private final RestClient restClient =
            RestClient.create();

    @CircuitBreaker(
            name = "inventoryService",
            fallbackMethod = "inventoryFallback"
    )
    public String getInventory() {

        return restClient.get()
                .uri("http://inventory-service/api/items")
                .retrieve()
                .body(String.class);
    }

    public String inventoryFallback(
            Exception ex) {

        return "INVENTORY_UNAVAILABLE";
    }
}
```

---

# 35. RestClient Mental Model

```text
cleaner API
blocking execution
same resiliency needs as RestTemplate
```

---

# 36. WebClient Basic Example

```java
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PaymentReactiveClient {

    private final WebClient webClient =
            WebClient.create();

    public Mono<String> callPayment() {

        return webClient.get()
                .uri("http://payment-service/api/pay")
                .retrieve()
                .bodyToMono(String.class);
    }
}
```

---

# 37. WebClient With CircuitBreaker Annotation

```java
@CircuitBreaker(
        name = "paymentService",
        fallbackMethod = "paymentFallback"
)
public Mono<String> callPayment() {

    return webClient.get()
            .uri("http://payment-service/api/pay")
            .retrieve()
            .bodyToMono(String.class);
}

public Mono<String> paymentFallback(
        Exception ex) {

    return Mono.just("PAYMENT_UNAVAILABLE");
}
```

---

# 38. WebClient Timeout

```java
public Mono<String> callPayment() {

    return webClient.get()
            .uri("http://payment-service/api/pay")
            .retrieve()
            .bodyToMono(String.class)
            .timeout(Duration.ofSeconds(2));
}
```

---

# 39. WebClient HTTP Status Handling

```java
return webClient.get()
        .uri("http://payment-service/api/pay")
        .retrieve()
        .onStatus(
                status -> status.is5xxServerError(),
                response -> Mono.error(
                        new RuntimeException("Server error")
                )
        )
        .bodyToMono(String.class);
```

---

# 40. Why HTTP Status Mapping Important

Not every HTTP status should count as dependency failure.

Usually:

```text
5xx = dependency/system failure
4xx = caller/business issue
```

But business context matters.

---

# 41. HTTP Classification Table

```text
400 Bad Request       → usually no retry / maybe ignore CB
401 Unauthorized      → no retry
403 Forbidden         → no retry
404 Not Found         → usually no retry
409 Conflict          → maybe retry depending on operation
429 Too Many Requests → retry only with backoff / Retry-After
500 Internal Error    → maybe retry / count failure
502 Bad Gateway       → retry / count failure
503 Unavailable       → retry / count failure
504 Gateway Timeout   → retry / count failure
```

---

# 42. Exception Classification Config

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        recordExceptions:
          - java.io.IOException
          - java.net.SocketTimeoutException
        ignoreExceptions:
          - com.demo.BusinessException
```

---

# 43. Slow Call Tracking

Circuit breaker can open due to:

```text
slow responses
```

even if calls succeed.

---

# 44. Slow Call Example

```text
100 calls
90 succeed
but 70 take more than 2 seconds
```

Slow call rate:

```text
70%
```

Circuit may open.

---

# 45. Slow Call Config

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        slowCallDurationThreshold: 2s
        slowCallRateThreshold: 50
```

---

# 46. Resilience4j Full HTTP Config

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        slidingWindowType: TIME_BASED
        slidingWindowSize: 60
        minimumNumberOfCalls: 20
        failureRateThreshold: 50
        slowCallDurationThreshold: 2s
        slowCallRateThreshold: 50
        waitDurationInOpenState: 30s
        permittedNumberOfCallsInHalfOpenState: 5

  retry:
    instances:
      paymentService:
        maxAttempts: 3
        waitDuration: 200ms
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2

  bulkhead:
    instances:
      paymentService:
        maxConcurrentCalls: 20

  timelimiter:
    instances:
      paymentService:
        timeoutDuration: 3s
```

---

# 47. Why Use Timeout With CircuitBreaker

Circuit breaker observes failures.

Timeout decides:

```text
when slow call becomes failure
```

Without timeout:

```text
thread may wait too long
```

---

# 48. Why Use Bulkhead With HTTP Client

Bulkhead prevents:

```text
one slow dependency consuming all threads
```

Example:

```text
payment API slow
```

Only payment resources saturate.

---

# 49. Why Use Retry Carefully

Retry helps:

```text
temporary network glitches
```

But hurts:

```text
overloaded dependencies
```

Use:

```text
small attempts
backoff
jitter
exception classification
```

---

# 50. Why Use Fallback

Fallback gives:

```text
graceful degradation
```

Examples:

```text
cached inventory
payment pending
empty recommendations
friendly error
```

---

# 51. Programmatic CircuitBreaker

Sometimes annotations are not enough.

Use programmatic style:

```java
CircuitBreaker circuitBreaker =
        circuitBreakerRegistry
                .circuitBreaker("paymentService");

Supplier<String> decorated =
        CircuitBreaker.decorateSupplier(
                circuitBreaker,
                () -> restClient.get()
                        .uri("http://payment/api")
                        .retrieve()
                        .body(String.class)
        );

String result = Try.ofSupplier(decorated)
        .recover(ex -> "fallback")
        .get();
```

---

# 52. When Programmatic Style Useful

Useful when:

```text
dynamic dependency names
custom fallback logic
manual composition order
testing internals
library code
```

---

# 53. Reactive Programmatic Pattern

Reactive systems often use operators/decorators.

Mental model:

```text
Mono pipeline
   ↓
CircuitBreaker operator
   ↓
fallback operator
```

---

# 54. WebClient Production Pattern

```text
WebClient
   +
response timeout
   +
connection pool limits
   +
CircuitBreaker
   +
Retry with backoff
   +
fallback
```

---

# 55. Connection Pool Exhaustion

HTTP clients maintain:

```text
connection pools
```

If remote service slow:

```text
connections remain occupied
```

New calls wait.

---

# 56. Connection Pool ASCII

```text
HTTP Connection Pool

[C1][C2][C3][C4][C5]

All waiting on slow remote service
```

---

# 57. Connection Pool Protection

Use:

```text
connection timeout
response timeout
max connections
bulkhead
circuit breaker
```

---

# 58. WebClient Netty Connection Pool

For high scale, tune:

```text
max connections
pending acquire timeout
response timeout
```

Mental model:

```text
do not allow infinite pending HTTP calls
```

---

# 59. Anti-Pattern: One Breaker For All

Bad:

```text
@CircuitBreaker(name = "default")
```

used for all dependencies.

Problem:

```text
one bad dependency opens breaker for all
```

---

# 60. Anti-Pattern: No Timeout

Bad:

```text
CircuitBreaker without timeout
```

Problem:

```text
slow calls consume resources before breaker reacts
```

---

# 61. Anti-Pattern: Fallback Fake Success

Bad payment fallback:

```text
return "PAYMENT_SUCCESS"
```

Correct:

```text
PAYMENT_PENDING
PAYMENT_UNAVAILABLE
```

---

# 62. Anti-Pattern: Blocking In WebClient

Bad:

```java
webClient.get()
        .retrieve()
        .bodyToMono(String.class)
        .block();
```

inside reactive flow.

This defeats non-blocking model.

---

# 63. Observability Metrics

Monitor:

```text
HTTP status codes
timeout count
failure rate
slow call rate
circuit OPEN count
fallback count
retry count
p95 latency
p99 latency
connection pool saturation
```

---

# 64. Dashboard Example

For payment dependency:

```text
payment_http_latency_p99
payment_cb_state
payment_cb_open_total
payment_fallback_total
payment_timeout_total
payment_retry_total
payment_http_5xx_total
```

---

# 65. Production Debugging Flow

If payment calls failing:

```text
check p99 latency
check timeout count
check 5xx rate
check circuit state
check fallback count
check connection pool saturation
check retry volume
```

---

# 66. Real Production Scenario

Payment provider becomes slow.

Without protection:

```text
checkout threads block
orders queue
users retry manually
traffic increases
system collapses
```

With protection:

```text
timeouts fire
circuit opens
fallback returns PAYMENT_PENDING
provider gets recovery time
system stays usable
```

---

# 67. Interview Explanation

If interviewer asks:

```text
How do you protect REST calls in Spring Boot?
```

Strong answer:

```text
I wrap outbound HTTP calls with timeout, circuit breaker, fallback, and
optionally retry and bulkhead. RestTemplate/RestClient are blocking, so
timeouts and bulkheads are critical. WebClient is non-blocking, but we
must avoid blocking Netty event-loop threads.
```

Senior addition:

```text
Each dependency should have its own circuit breaker and metrics. I also
classify HTTP statuses and exceptions so business 4xx errors do not
incorrectly open the circuit while 5xx/timeouts count as dependency
failures.
```

---

# 68. Final Mental Model

```text
Remote HTTP Call
      ↓
Unreliable Boundary
      ↓
Protect With:
timeout
bulkhead
retry
circuit breaker
fallback
observability
```

---

# 69. What To Remember

```text
HTTP calls are failure boundaries.

RestTemplate and RestClient are blocking.

WebClient is reactive and non-blocking.

Do not block Netty event loops.

Always configure timeouts.

Use separate circuit breakers per dependency.

Classify HTTP status codes.

Track slow-call rate, not only failures.

Fallback must be safe.

Bulkhead prevents resource exhaustion.

Retry must use backoff/jitter.

Observe everything with metrics.
```

---

# 70. Next File

```text
022_CircuitBreaker_With_Kafka_DB_Calls.md
```

Next you learn:

```text
Kafka consumer protection
DB call protection
consumer retries
DLT
DB timeout
transaction safety
idempotency
async failure handling
production message resiliency
```
