# Resilient Distributed Systems – Failure Handling (Complete Guide)

> Scenario: Your payment service crashes during Black Friday. Orders stuck. Revenue bleeding.  
> Reality: Failures are **inevitable**. Resilience = how you handle them.

---

## 🧠 Core Principle

```
Failures are not exceptions. They are normal.
```

---

# 1. Why Failures Are Inevitable

System flow:
Client → API → Order → Payment → Inventory → Bank → Notification

Each adds failure probability.

If each = 99.9%:

```
Total success ≈ 0.999^5 ≈ 99.5%
```

👉 More services → more failures

---

# 2. Types of Failures

## Transient
- Temporary
- Fix: Retry

## Permanent
- Won’t recover
- Fix: Fail fast

## Intermittent
- Flaky
- Fix: Retry + circuit breaker

## Cascading
- Domino failure
- Fix: Isolation + circuit breaker

---

# 3. Core Strategies

## 3.1 Retries (Exponential Backoff + Jitter)

Example:
```
1 → 2 → 4 → 8 sec
```

### Use when:
- 503, 504, network errors

### Avoid:
- 400, 404, auth errors

### Spring Boot Example
```java
RetryTemplate template = new RetryTemplate();
template.execute(ctx -> callService());
```

---

## 3.2 Circuit Breaker

States:
- CLOSED → normal
- OPEN → fail fast
- HALF-OPEN → test

### Spring Boot (Resilience4j)
```java
@CircuitBreaker(name = "payment", fallbackMethod = "fallback")
public String call() {
    return restTemplate.getForObject(url, String.class);
}
```

---

## 3.3 Timeouts

```
timeout = p99 latency × 2–3
```

Example:
- p99 = 200ms → timeout = 600ms

---

## 3.4 Fallbacks

Examples:
- cache
- default values
- degraded UI

```java
public String fallback(Exception e) {
    return "cached-response";
}
```

---

## 3.5 Bulkheads

Separate resources:

```
Payments pool
Inventory pool
Notification pool
```

### Spring Boot
```java
@Bulkhead(name = "payment")
```

---

## 3.6 Idempotency

Prevent duplicate actions.

### SQL
```sql
CREATE TABLE payments (
  idempotency_key VARCHAR PRIMARY KEY,
  status TEXT
);
```

---

## 3.7 Graceful Degradation

| Load | Action |
|------|--------|
| Normal | Full features |
| High | Disable heavy features |
| Critical | Core only |

---

## 3.8 Failover

Types:
- Cold
- Warm
- Hot
- Active-active

---

## 3.9 Replication

| Type | Tradeoff |
|------|----------|
| Sync | safe but slow |
| Async | fast but risk |

---

# 4. Combined Flow

```
Timeout → Retry → Circuit Breaker → Bulkhead → Fallback
```

---

# 5. SQL Example (Idempotent Payment)

```sql
INSERT INTO payments(idempotency_key, status)
VALUES ('abc', 'SUCCESS')
ON CONFLICT DO NOTHING;
```

---

# 6. Spring Boot Mini System

## Controller
```java
@GetMapping("/pay")
public String pay() {
    return service.process();
}
```

## Service
```java
@CircuitBreaker(name="payment", fallbackMethod="fallback")
public String process() {
    return restTemplate.getForObject(url, String.class);
}
```

---

# 7. Best Practices

- Fail fast
- Add timeouts everywhere
- Monitor everything
- Test failures (chaos engineering)

---

# 🔥 Interview One-Liner

```
Timeout + Retry + Circuit Breaker + Bulkhead + Fallback + Idempotency = Resilient System
```


---

# 8. Complete Spring Boot Examples (Appendix)

> The content above stays intact. This section adds a more complete, runnable Spring Boot reference showing how to implement retries, circuit breakers, bulkheads, timeouts, fallbacks, and idempotency.

---

## 8.1 What This Demo Covers

This sample app shows:

- `POST /api/payments` → idempotent payment endpoint
- outbound call to a payment provider
- retries for transient failures
- circuit breaker for repeated failures
- bulkhead isolation for payment calls
- fallback response when provider is unavailable
- HTTP client timeouts
- SQL table for idempotency

---

## 8.2 Maven Dependencies

```xml
<dependencies>
    <!-- Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- JPA + DB -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Resilience4j -->
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot3</artifactId>
        <version>2.2.0</version>
    </dependency>

    <!-- Optional: Spring Retry -->
    <dependency>
        <groupId>org.springframework.retry</groupId>
        <artifactId>spring-retry</artifactId>
    </dependency>

    <!-- Lombok optional -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 8.3 application.yml

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

resilience4j:
  circuitbreaker:
    instances:
      paymentProvider:
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
        permittedNumberOfCallsInHalfOpenState: 3
  retry:
    instances:
      paymentProvider:
        maxAttempts: 3
        waitDuration: 500ms
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
        retryExceptions:
          - java.net.SocketTimeoutException
          - org.springframework.web.client.ResourceAccessException
          - org.springframework.web.client.HttpServerErrorException
  bulkhead:
    instances:
      paymentProvider:
        maxConcurrentCalls: 10
        maxWaitDuration: 100ms

payment:
  provider:
    base-url: http://localhost:9090
    connect-timeout-ms: 1000
    read-timeout-ms: 2000
```

---

## 8.4 Main Application

```java
package com.example.resilience;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResilienceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResilienceApplication.class, args);
    }
}
```

---

## 8.5 Database Schema for Idempotency

### SQL
```sql
CREATE TABLE payment_requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    idempotency_key VARCHAR(100) NOT NULL UNIQUE,
    order_id VARCHAR(100) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(30) NOT NULL,
    response_body CLOB,
    created_at TIMESTAMP NOT NULL
);
```

---

## 8.6 Entity

```java
package com.example.resilience.payment;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment_requests", uniqueConstraints = {
        @UniqueConstraint(name = "uk_idempotency_key", columnNames = "idempotencyKey")
})
public class PaymentRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idempotencyKey;
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String status;

    @Lob
    private String responseBody;

    private Instant createdAt;

    public Long getId() { return id; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getResponseBody() { return responseBody; }
    public void setResponseBody(String responseBody) { this.responseBody = responseBody; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
```

---

## 8.7 Repository

```java
package com.example.resilience.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequestEntity, Long> {
    Optional<PaymentRequestEntity> findByIdempotencyKey(String idempotencyKey);
}
```

---

## 8.8 DTOs

```java
package com.example.resilience.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PaymentCreateRequest {

    @NotBlank
    private String orderId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotBlank
    private String currency;

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
```

```java
package com.example.resilience.payment;

public class PaymentResponse {
    private String status;
    private String message;
    private String providerReference;

    public PaymentResponse() {}

    public PaymentResponse(String status, String message, String providerReference) {
        this.status = status;
        this.message = message;
        this.providerReference = providerReference;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getProviderReference() { return providerReference; }
    public void setProviderReference(String providerReference) { this.providerReference = providerReference; }
}
```

```java
package com.example.resilience.payment;

import java.math.BigDecimal;

public class ProviderChargeRequest {
    private String orderId;
    private BigDecimal amount;
    private String currency;

    public ProviderChargeRequest() {}

    public ProviderChargeRequest(String orderId, BigDecimal amount, String currency) {
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
```

```java
package com.example.resilience.payment;

public class ProviderChargeResponse {
    private String providerReference;
    private String status;

    public String getProviderReference() { return providerReference; }
    public void setProviderReference(String providerReference) { this.providerReference = providerReference; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

---

## 8.9 HTTP Client Configuration with Timeouts

```java
package com.example.resilience.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class HttpClientConfig {

    @Bean
    public RestTemplate paymentRestTemplate(
            RestTemplateBuilder builder,
            @Value("${payment.provider.connect-timeout-ms}") long connectTimeoutMs,
            @Value("${payment.provider.read-timeout-ms}") long readTimeoutMs
    ) {
        return builder
                .setConnectTimeout(Duration.ofMillis(connectTimeoutMs))
                .setReadTimeout(Duration.ofMillis(readTimeoutMs))
                .build();
    }
}
```

---

## 8.10 Payment Provider Client with Retry + Circuit Breaker + Bulkhead + Fallback

```java
package com.example.resilience.payment;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentProviderClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PaymentProviderClient(RestTemplate paymentRestTemplate,
                                 @Value("${payment.provider.base-url}") String baseUrl) {
        this.restTemplate = paymentRestTemplate;
        this.baseUrl = baseUrl;
    }

    @Retry(name = "paymentProvider")
    @CircuitBreaker(name = "paymentProvider", fallbackMethod = "fallbackCharge")
    @Bulkhead(name = "paymentProvider", type = Bulkhead.Type.SEMAPHORE)
    public ProviderChargeResponse charge(ProviderChargeRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProviderChargeRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ProviderChargeResponse> response = restTemplate.exchange(
                baseUrl + "/provider/charge",
                HttpMethod.POST,
                entity,
                ProviderChargeResponse.class
        );

        return response.getBody();
    }

    public ProviderChargeResponse fallbackCharge(ProviderChargeRequest request, Throwable throwable) {
        ProviderChargeResponse response = new ProviderChargeResponse();
        response.setStatus("PENDING");
        response.setProviderReference("fallback-" + request.getOrderId());
        return response;
    }
}
```

---

## 8.11 Payment Service with Idempotency

```java
package com.example.resilience.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PaymentService {

    private final PaymentRequestRepository repository;
    private final PaymentProviderClient providerClient;
    private final ObjectMapper objectMapper;

    public PaymentService(PaymentRequestRepository repository,
                          PaymentProviderClient providerClient,
                          ObjectMapper objectMapper) {
        this.repository = repository;
        this.providerClient = providerClient;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PaymentResponse process(String idempotencyKey, PaymentCreateRequest request) {
        var existing = repository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return readExistingResponse(existing.get());
        }

        PaymentRequestEntity entity = new PaymentRequestEntity();
        entity.setIdempotencyKey(idempotencyKey);
        entity.setOrderId(request.getOrderId());
        entity.setAmount(request.getAmount());
        entity.setCurrency(request.getCurrency());
        entity.setStatus("PROCESSING");
        entity.setCreatedAt(Instant.now());

        try {
            repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            // another request with same idempotency key won the race
            return repository.findByIdempotencyKey(idempotencyKey)
                    .map(this::readExistingResponse)
                    .orElseThrow(() -> e);
        }

        ProviderChargeResponse providerResponse = providerClient.charge(
                new ProviderChargeRequest(request.getOrderId(), request.getAmount(), request.getCurrency())
        );

        PaymentResponse response = new PaymentResponse(
                providerResponse.getStatus(),
                "Payment processed",
                providerResponse.getProviderReference()
        );

        entity.setStatus(providerResponse.getStatus());
        entity.setResponseBody(writeJson(response));
        repository.save(entity);

        return response;
    }

    private PaymentResponse readExistingResponse(PaymentRequestEntity entity) {
        if (entity.getResponseBody() == null) {
            return new PaymentResponse(entity.getStatus(), "Already processing", null);
        }
        try {
            return objectMapper.readValue(entity.getResponseBody(), PaymentResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize stored response", e);
        }
    }

    private String writeJson(PaymentResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize payment response", e);
        }
    }
}
```

---

## 8.12 REST Controller

```java
package com.example.resilience.payment;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody PaymentCreateRequest request
    ) {
        PaymentResponse response = service.process(idempotencyKey, request);
        return ResponseEntity.ok(response);
    }
}
```

---

## 8.13 Example Fallback Behavior

If payment provider:
- times out
- returns 5xx repeatedly
- trips circuit breaker

Then fallback returns:

```json
{
  "status": "PENDING",
  "message": "Payment processed",
  "providerReference": "fallback-order-123"
}
```

Typical production improvement:
- write failed payment attempt to queue
- reconcile later asynchronously
- notify user that payment is pending confirmation

---

## 8.14 Optional Dummy Provider Controller for Local Testing

Run this in the same app or a second local app on `9090`.

```java
package com.example.resilience.provider;

import com.example.resilience.payment.ProviderChargeRequest;
import com.example.resilience.payment.ProviderChargeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/provider")
public class DummyPaymentProviderController {

    @PostMapping("/charge")
    public ResponseEntity<ProviderChargeResponse> charge(@RequestBody ProviderChargeRequest request) throws Exception {
        int random = ThreadLocalRandom.current().nextInt(10);

        if (random < 3) {
            Thread.sleep(3000); // simulate timeout/slow dependency
        }

        if (random < 2) {
            return ResponseEntity.internalServerError().build();
        }

        ProviderChargeResponse response = new ProviderChargeResponse();
        response.setStatus("SUCCESS");
        response.setProviderReference(UUID.randomUUID().toString());
        return ResponseEntity.ok(response);
    }
}
```

This helps you observe:
- retries
- circuit breaker opening
- fallback response
- timeout behavior

---

## 8.15 cURL Test Commands

### First request
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: order-123-key" \
  -d '{
    "orderId":"order-123",
    "amount":199.99,
    "currency":"USD"
  }'
```

### Retry same logical request
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: order-123-key" \
  -d '{
    "orderId":"order-123",
    "amount":199.99,
    "currency":"USD"
  }'
```

Expected:
- second request returns same stored result
- no duplicate payment created

---

## 8.16 Where Each Resilience Pattern Appears in This Example

| Pattern | Where Used |
|---|---|
| Timeout | RestTemplate connect/read timeout |
| Retry | `@Retry(name = "paymentProvider")` |
| Circuit Breaker | `@CircuitBreaker(...)` |
| Bulkhead | `@Bulkhead(...)` |
| Fallback | `fallbackCharge(...)` |
| Idempotency | `Idempotency-Key` + unique DB constraint |
| Graceful degradation | fallback returns `PENDING` instead of hard failure |

---

## 8.17 Suggested Production Improvements

To make this more production-grade, add:

1. **Jittered retry**
   - Resilience4j supports interval tuning; use randomized backoff to avoid retry storms.

2. **Queue-based async reconciliation**
   - If fallback returns `PENDING`, push event to Kafka/RabbitMQ/SQS.

3. **Metrics and dashboards**
   - export circuit breaker state, retry counts, timeout counts, bulkhead rejection counts.

4. **Bulkheads per dependency**
   - separate pools for payment, inventory, recommendation, notification.

5. **Readiness checks**
   - if critical dependency is unhealthy, mark service not-ready to reduce incoming traffic.

6. **Feature flags for graceful degradation**
   - disable expensive optional features first under load.

7. **Outbox pattern**
   - if payment success triggers notifications/events, write them transactionally to outbox table.

---

## 8.18 Short Interview Summary for the Spring Boot Example

```text
The payment API uses idempotency keys to avoid duplicate charges.
Outbound provider calls use timeout + retry + circuit breaker + bulkhead.
If the provider is unhealthy, we fail gracefully with a fallback response.
This protects the service from cascading failures while preserving a usable customer experience.
```
