# 019_SpringCloud_Eureka_OpenFeign_Internals.md

> **MiniServiceDiscovery — Deep Note**  
> Topic: Spring Cloud Eureka + OpenFeign Internals  
> Goal: Understand how Spring Cloud uses Eureka for service discovery and OpenFeign for declarative service-to-service HTTP calls.

---

## 0. Why This Chapter Matters

In a microservice system, services rarely call hardcoded IP addresses. They call logical service names like:

```text
order-service -> payment-service
user-service  -> notification-service
api-gateway   -> inventory-service
```

But at runtime, each service may have many instances:

```text
payment-service
  - 10.0.1.12:8080
  - 10.0.1.13:8080
  - 10.0.1.14:8080
```

Spring Cloud Eureka and OpenFeign solve two different but connected problems:

| Problem | Tool |
|---|---|
| Where is `payment-service` running? | Eureka |
| How do I call `payment-service` cleanly from Java code? | OpenFeign |
| Which instance should receive this request? | Spring Cloud LoadBalancer |
| What if call fails or times out? | Resilience4j / Retry / Circuit Breaker |

The strong mental model:

```text
OpenFeign does not magically know IP addresses.
It calls a logical service name.
Spring Cloud LoadBalancer resolves that service name.
Eureka provides the list of healthy-ish instances.
HTTP client finally sends request to one selected instance.
```

---

## 1. Big Picture Architecture

```text
                         +----------------------+
                         |    Eureka Server     |
                         |  Service Registry    |
                         +----------+-----------+
                                    ^
                                    |
                 register + heartbeat + fetch registry
                                    |
+-------------------+       +-------+--------+       +-------------------+
| order-service     |       | payment-service|       | inventory-service |
| Eureka Client     |       | Eureka Client  |       | Eureka Client     |
| Feign Client      |       | REST Controller|       | REST Controller   |
+---------+---------+       +----------------+       +-------------------+
          |
          | Java method call
          v
+-----------------------------+
| @FeignClient("payment-service") |
+-------------+---------------+
              |
              | logical name: payment-service
              v
+-----------------------------+
| Spring Cloud LoadBalancer   |
+-------------+---------------+
              |
              | asks DiscoveryClient
              v
+-----------------------------+
| Eureka Client Registry Cache|
+-------------+---------------+
              |
              | chooses instance
              v
       http://10.0.1.13:8080/payments
```

Important point: Eureka is usually **not called on every request**. Each service keeps a local registry cache and refreshes it periodically. This reduces latency and protects Eureka from being on the hot path.

---

## 2. Core Components

### 2.1 Eureka Server

Eureka Server is a registry. It stores service instance metadata:

```text
serviceId = PAYMENT-SERVICE
instanceId = payment-service-10.0.1.13:8080
host = 10.0.1.13
port = 8080
status = UP
metadata = { zone=eu-west-1a, version=v1 }
leaseInfo = lastHeartbeatTimestamp, renewalInterval, evictionTime
```

It answers questions like:

```text
Give me all instances of PAYMENT-SERVICE.
```

### 2.2 Eureka Client

Every microservice using Eureka Client does three main things:

1. Registers itself with Eureka.
2. Sends periodic heartbeats.
3. Fetches registry data from Eureka.

```text
startup
  -> register instance
  -> start heartbeat scheduler
  -> start registry fetch scheduler
  -> expose local service normally
```

### 2.3 DiscoveryClient

In Spring Cloud, `DiscoveryClient` is the abstraction used to discover service instances.

Conceptually:

```java
List<ServiceInstance> instances = discoveryClient.getInstances("payment-service");
```

When using Eureka, this call is backed by Eureka client data.

### 2.4 OpenFeign

OpenFeign lets you write an interface instead of manual `RestTemplate` or `WebClient` code.

```java
@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/payments")
    PaymentResponse pay(@RequestBody PaymentRequest request);
}
```

Then your business service calls it like normal Java:

```java
PaymentResponse response = paymentClient.pay(request);
```

But internally, Feign builds an HTTP request.

---

## 3. Minimal Eureka Server

### 3.1 Maven dependency

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

### 3.2 Main class

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

### 3.3 application.yml

```yaml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: true
```

Why `register-with-eureka: false`?

Because this service is the registry itself. In a single-node local setup, it does not need to register with itself.

Why `fetch-registry: false`?

Because the server does not need to act as a discovery client in this simple setup.

---

## 4. Minimal Eureka Client Service

Example: `payment-service`.

### 4.1 Maven dependency

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### 4.2 Main class

```java
@SpringBootApplication
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
```

In modern Spring Cloud, explicit `@EnableEurekaClient` is often not required when the starter is present.

### 4.3 application.yml

```yaml
server:
  port: 8082

spring:
  application:
    name: payment-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90
```

Important settings:

| Setting | Meaning |
|---|---|
| `spring.application.name` | Logical service name registered in Eureka |
| `defaultZone` | Eureka server URL |
| `prefer-ip-address` | Register IP instead of hostname |
| `lease-renewal-interval` | Heartbeat interval |
| `lease-expiration-duration` | Time after which missing heartbeat can cause eviction |

---

## 5. Minimal OpenFeign Client

Example: `order-service` calling `payment-service`.

### 5.1 Dependency

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

Also include Eureka client dependency:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### 5.2 Enable Feign

```java
@SpringBootApplication
@EnableFeignClients
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

### 5.3 Feign interface

```java
@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/payments")
    PaymentResponse createPayment(@RequestBody PaymentRequest request);
}
```

### 5.4 Using Feign in service layer

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final PaymentClient paymentClient;

    public OrderResponse placeOrder(OrderRequest orderRequest) {
        PaymentRequest paymentRequest = new PaymentRequest(
                orderRequest.orderId(),
                orderRequest.amount()
        );

        PaymentResponse paymentResponse = paymentClient.createPayment(paymentRequest);

        return new OrderResponse(
                orderRequest.orderId(),
                paymentResponse.status()
        );
    }
}
```

Business code does not know IP, port, or load balancing logic.

---

## 6. What Happens Internally When Feign Is Called?

Suppose this code runs:

```java
paymentClient.createPayment(request);
```

The internal flow:

```text
1. Spring injects a proxy object for PaymentClient.
2. Method call is intercepted by Feign proxy.
3. Feign reads annotations: POST /payments.
4. Feign serializes request body to JSON.
5. Target service name is payment-service.
6. Spring Cloud LoadBalancer resolves payment-service.
7. DiscoveryClient returns instances from local Eureka cache.
8. LoadBalancer chooses one instance.
9. Feign HTTP client sends request to real host:port.
10. Response JSON is decoded into PaymentResponse.
```

Pseudo internal chain:

```text
PaymentClient proxy
  -> Feign InvocationHandler
  -> MethodMetadata
  -> RequestTemplate
  -> Encoder
  -> LoadBalancerFeignClient / FeignBlockingLoadBalancerClient
  -> ServiceInstanceListSupplier
  -> DiscoveryClient
  -> Eureka local cache
  -> ReactorLoadBalancer / RoundRobinLoadBalancer
  -> HTTP client
  -> Decoder
```

---

## 7. Eureka Registration Internals

When `payment-service` starts:

```text
payment-service startup
  -> build InstanceInfo
  -> send registration request to Eureka Server
  -> Eureka stores instance in registry
  -> instance status becomes UP
```

The registration data contains:

```text
appName: PAYMENT-SERVICE
instanceId: host:payment-service:8082
ipAddr: 10.0.1.13
port: 8082
status: UP
healthCheckUrl: http://10.0.1.13:8082/actuator/health
statusPageUrl: http://10.0.1.13:8082/actuator/info
metadata: custom key-values
```

A simplified Java model:

```java
public class ServiceInstanceInfo {
    private String serviceId;
    private String instanceId;
    private String host;
    private int port;
    private String status;
    private Map<String, String> metadata;
    private long lastHeartbeatTime;
}
```

---

## 8. Eureka Heartbeat Internals

Eureka uses lease renewal. A service instance must periodically renew its lease.

Default mental model:

```text
Every 30 seconds:
  payment-service -> Eureka: I am alive

If no heartbeat for around 90 seconds:
  Eureka may evict instance
```

Important: eviction is not always immediate because Eureka has self-preservation mode.

Pseudo code:

```java
class LeaseManager {
    private final Map<String, Lease> leases = new ConcurrentHashMap<>();

    public void renew(String instanceId) {
        Lease lease = leases.get(instanceId);
        if (lease != null) {
            lease.lastRenewalTimestamp = System.currentTimeMillis();
        }
    }

    public boolean isExpired(Lease lease) {
        long now = System.currentTimeMillis();
        return now - lease.lastRenewalTimestamp > lease.expirationMillis;
    }
}
```

Why heartbeat matters:

```text
Without heartbeat, registry cannot distinguish:
- service crashed
- network partition happened
- GC pause happened
- Eureka server temporarily unreachable
```

That is why service discovery is eventually consistent, not instantly consistent.

---

## 9. Eureka Registry Fetch Internals

Clients do not call Eureka for every HTTP request. They fetch registry periodically.

```text
order-service
  local cache:
    PAYMENT-SERVICE -> [10.0.1.13:8082, 10.0.1.14:8082]
```

On schedule:

```text
order-service -> Eureka: give me delta registry changes
Eureka -> order-service: payment-service instance added/removed/changed
order-service updates local cache
```

Simplified cache:

```java
public class LocalRegistryCache {
    private final Map<String, List<ServiceInstanceInfo>> registry = new ConcurrentHashMap<>();

    public List<ServiceInstanceInfo> getInstances(String serviceId) {
        return registry.getOrDefault(serviceId.toUpperCase(), List.of());
    }

    public void refresh(Map<String, List<ServiceInstanceInfo>> latest) {
        registry.clear();
        registry.putAll(latest);
    }
}
```

This design improves performance:

```text
Per request path:
  Feign -> local cache -> selected instance -> HTTP call

Not:
  Feign -> Eureka server -> selected instance -> HTTP call
```

---

## 10. OpenFeign Proxy Internals

Feign creates a runtime implementation of your interface.

Your code:

```java
@FeignClient(name = "payment-service")
public interface PaymentClient {
    @PostMapping("/payments")
    PaymentResponse pay(@RequestBody PaymentRequest request);
}
```

Runtime idea:

```java
class PaymentClientProxy implements PaymentClient {
    public PaymentResponse pay(PaymentRequest request) {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.method = "POST";
        httpRequest.path = "/payments";
        httpRequest.body = jsonEncode(request);

        HttpResponse response = httpClient.execute("payment-service", httpRequest);
        return jsonDecode(response.body, PaymentResponse.class);
    }
}
```

Actual Feign is more generic, but this mental model is accurate enough for interviews.

Feign maps:

| Java concept | HTTP concept |
|---|---|
| interface method | HTTP endpoint |
| method argument | body/query/path/header |
| return type | response decoder target |
| exception | error decoder / client exception |

---

## 11. Service Name Resolution

This is the most important internals point.

```java
@FeignClient(name = "payment-service")
```

The name is not a DNS name in the usual sense. It is a logical service ID.

Resolution path:

```text
payment-service
  -> Spring Cloud LoadBalancer
  -> DiscoveryClient
  -> Eureka Client cache
  -> list of ServiceInstance
  -> selected host:port
```

Without Eureka or another discovery client, Feign can also call fixed URLs:

```java
@FeignClient(name = "paymentClient", url = "http://localhost:8082")
public interface PaymentClient {
    @PostMapping("/payments")
    PaymentResponse pay(@RequestBody PaymentRequest request);
}
```

But this removes dynamic discovery.

---

## 12. Load Balancing Internals

Modern Spring Cloud commonly uses Spring Cloud LoadBalancer.

Conceptually:

```java
public class RoundRobinLoadBalancer {
    private final AtomicInteger position = new AtomicInteger(0);

    public ServiceInstance choose(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            throw new IllegalStateException("No instances available");
        }
        int index = Math.abs(position.getAndIncrement()) % instances.size();
        return instances.get(index);
    }
}
```

Example:

```text
instances = [A, B, C]
requests: 1 2 3 4 5 6
chosen:   A B C A B C
```

But production load balancing may consider:

```text
- health
- zone
- latency
- weight
- retries
- sticky sessions
- connection pool state
```

Spring Cloud LoadBalancer typically receives `ServiceInstance` objects from a `ServiceInstanceListSupplier`.

Flow:

```text
Feign request
  -> LoadBalancer client
  -> ServiceInstanceListSupplier
  -> DiscoveryClientServiceInstanceListSupplier
  -> DiscoveryClient.getInstances(serviceId)
  -> choose instance
```

---

## 13. Feign Encoding and Decoding

Feign needs to convert Java objects to HTTP body and back.

### Request encoding

```java
PaymentRequest request = new PaymentRequest("ord-1", 500);
```

Becomes JSON:

```json
{
  "orderId": "ord-1",
  "amount": 500
}
```

### Response decoding

```json
{
  "paymentId": "pay-123",
  "status": "SUCCESS"
}
```

Becomes:

```java
new PaymentResponse("pay-123", "SUCCESS");
```

Typical Spring Cloud OpenFeign uses Spring MVC annotations and Spring message converters.

Common bug:

```text
Feign call fails because DTO field names do not match JSON.
```

Fix:

```java
public record PaymentResponse(
        String paymentId,
        String status
) {}
```

Or use Jackson annotations when needed:

```java
public record PaymentResponse(
        @JsonProperty("payment_id") String paymentId,
        String status
) {}
```

---

## 14. Error Handling Internals

Feign does not treat all HTTP responses as success.

Common behavior:

| HTTP status | Meaning |
|---|---|
| 2xx | decode response normally |
| 4xx | client error, usually FeignException |
| 5xx | server error, usually FeignException |
| timeout | retry or exception depending config |

Custom error decoder:

```java
@Configuration
public class FeignErrorConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 404) {
                return new PaymentNotFoundException("Payment not found");
            }
            if (response.status() >= 500) {
                return new PaymentServiceUnavailableException("Payment service failed");
            }
            return new RuntimeException("Feign call failed: " + response.status());
        };
    }
}
```

Attach config:

```java
@FeignClient(
        name = "payment-service",
        configuration = FeignErrorConfig.class
)
public interface PaymentClient {
    @PostMapping("/payments")
    PaymentResponse pay(@RequestBody PaymentRequest request);
}
```

Production mental model:

```text
Do not let raw FeignException leak into business code.
Translate it to domain-level failure.
```

---

## 15. Timeouts

A missing timeout can kill production systems.

Example config:

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          payment-service:
            connectTimeout: 1000
            readTimeout: 2000
```

Meanings:

| Timeout | Meaning |
|---|---|
| connect timeout | time to establish TCP connection |
| read timeout | time waiting for response data |

Bad scenario:

```text
order-service thread waits 30 seconds for payment-service
traffic increases
threads blocked
connection pool exhausted
order-service also goes down
```

Correct scenario:

```text
small timeout
fail fast
retry only safe operations
fallback only when meaningful
circuit breaker for repeated failure
```

---

## 16. Retry Rules

Retries are dangerous if the operation is not idempotent.

Safe retry examples:

```text
GET /products/123
GET /inventory/sku-1
PUT /users/123/profile with idempotency key
POST /payments with idempotency key
```

Unsafe retry example:

```text
POST /payments without idempotency key
```

Because duplicate calls may charge customer twice.

Feign retry mental model:

```text
retry only if:
  - timeout or temporary 5xx
  - operation is idempotent
  - retry count is small
  - backoff is used
  - circuit breaker protects downstream
```

Example idempotency header:

```java
@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/payments")
    PaymentResponse pay(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody PaymentRequest request
    );
}
```

---

## 17. Circuit Breaker With Feign

Feign solves calling. It does not automatically make the call resilient.

Common production setup:

```text
Feign + Timeouts + Resilience4j CircuitBreaker + Bulkhead + Retry
```

Example service wrapper:

```java
@Service
@RequiredArgsConstructor
public class PaymentGateway {

    private final PaymentClient paymentClient;

    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    @TimeLimiter(name = "paymentService")
    public CompletableFuture<PaymentResponse> payAsync(PaymentRequest request) {
        return CompletableFuture.supplyAsync(() ->
                paymentClient.pay(UUID.randomUUID().toString(), request)
        );
    }

    private CompletableFuture<PaymentResponse> paymentFallback(
            PaymentRequest request,
            Throwable ex
    ) {
        return CompletableFuture.completedFuture(
                new PaymentResponse(null, "PAYMENT_PENDING")
        );
    }
}
```

Fallback rule:

```text
Fallback must be business-correct.
Do not return fake SUCCESS.
For payment, PENDING is safer than SUCCESS.
```

---

## 18. Feign Interceptors

Interceptors modify outgoing Feign requests.

Use cases:

```text
- add Authorization token
- add correlation ID
- add tenant ID
- add idempotency key
- add tracing headers
```

Example:

```java
@Configuration
public class FeignRequestInterceptorConfig {

    @Bean
    public RequestInterceptor correlationIdInterceptor() {
        return template -> {
            String correlationId = MDC.get("correlationId");
            if (correlationId != null) {
                template.header("X-Correlation-Id", correlationId);
            }
        };
    }
}
```

JWT propagation example:

```java
@Bean
public RequestInterceptor authInterceptor() {
    return template -> {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs != null) {
            String auth = attrs.getRequest().getHeader("Authorization");
            if (auth != null) {
                template.header("Authorization", auth);
            }
        }
    };
}
```

Warning:

```text
Do not blindly forward user tokens to every internal service.
For high-security systems, use service-to-service tokens or token exchange.
```

---

## 19. Metadata and Zone Awareness

Eureka instance metadata can store useful routing data.

```yaml
eureka:
  instance:
    metadata-map:
      version: v1
      zone: eu-central-1a
      weight: "10"
```

Possible uses:

```text
- route same-zone traffic first
- canary deployment
- blue/green routing
- version-aware clients
- weighted load balancing
```

Example service instance filtering:

```java
public List<ServiceInstance> onlyVersion(
        List<ServiceInstance> instances,
        String version
) {
    return instances.stream()
            .filter(i -> version.equals(i.getMetadata().get("version")))
            .toList();
}
```

Production caution:

```text
Do not encode too much business logic in Eureka metadata.
Keep routing rules observable and testable.
```

---

## 20. Health Checks

Eureka registration status is not the same as real business health.

A service may be registered as UP but still fail because:

```text
- database is down
- Kafka producer is blocked
- Redis is unavailable
- thread pool is exhausted
- GC pause is high
- deployment is half-ready
```

Use actuator:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

Readiness vs liveness:

| Check | Meaning |
|---|---|
| liveness | should process be restarted? |
| readiness | should service receive traffic? |

For service discovery, readiness is more important than simple process liveness.

---

## 21. Common Failure Scenario: Stale Registry

Timeline:

```text
T0  payment-service instance A dies
T1  Eureka has not evicted A yet
T2  order-service local cache still contains A
T3  Feign load balancer picks A
T4  request fails
T5  retry may choose B
T6  later registry refresh removes A
```

This is normal in service discovery.

Mitigation:

```text
- short but safe timeouts
- retry only idempotent calls
- circuit breaker
- health-aware load balancing
- graceful shutdown
- readiness checks
```

Graceful shutdown idea:

```text
1. service receives shutdown signal
2. mark readiness DOWN
3. stop accepting new traffic
4. deregister from Eureka
5. wait for in-flight requests
6. shutdown
```

---

## 22. Graceful Deregistration

When a service stops cleanly, it should deregister.

```text
payment-service -> Eureka: cancel my registration
Eureka removes instance from registry
clients remove it after next registry refresh
```

But if the process crashes, deregistration does not happen. Then Eureka relies on lease expiration.

This is why TTL and heartbeat exist.

---

## 23. Mini Implementation: Discovery + Feign-Like Call

This small simulation helps understand the internals.

### 23.1 Service instance model

```java
public record Instance(
        String serviceId,
        String host,
        int port,
        boolean healthy
) {}
```

### 23.2 In-memory registry

```java
public class InMemoryRegistry {
    private final Map<String, List<Instance>> registry = new ConcurrentHashMap<>();

    public void register(Instance instance) {
        registry.computeIfAbsent(instance.serviceId(), k -> new CopyOnWriteArrayList<>())
                .add(instance);
    }

    public List<Instance> getInstances(String serviceId) {
        return registry.getOrDefault(serviceId, List.of())
                .stream()
                .filter(Instance::healthy)
                .toList();
    }
}
```

### 23.3 Round-robin load balancer

```java
public class SimpleLoadBalancer {
    private final AtomicInteger index = new AtomicInteger(0);

    public Instance choose(List<Instance> instances) {
        if (instances.isEmpty()) {
            throw new IllegalStateException("No healthy instances available");
        }
        int pos = Math.floorMod(index.getAndIncrement(), instances.size());
        return instances.get(pos);
    }
}
```

### 23.4 Feign-like client

```java
public class MiniFeignClient {
    private final InMemoryRegistry registry;
    private final SimpleLoadBalancer loadBalancer;

    public MiniFeignClient(InMemoryRegistry registry, SimpleLoadBalancer loadBalancer) {
        this.registry = registry;
        this.loadBalancer = loadBalancer;
    }

    public String post(String serviceId, String path, String jsonBody) {
        List<Instance> instances = registry.getInstances(serviceId);
        Instance selected = loadBalancer.choose(instances);

        String url = "http://" + selected.host() + ":" + selected.port() + path;

        // Real Feign would execute HTTP here.
        return "POST " + url + " body=" + jsonBody;
    }
}
```

### 23.5 Demo

```java
public class Demo {
    public static void main(String[] args) {
        InMemoryRegistry registry = new InMemoryRegistry();
        registry.register(new Instance("payment-service", "10.0.1.10", 8080, true));
        registry.register(new Instance("payment-service", "10.0.1.11", 8080, true));

        MiniFeignClient client = new MiniFeignClient(registry, new SimpleLoadBalancer());

        System.out.println(client.post("payment-service", "/payments", "{amount:100}"));
        System.out.println(client.post("payment-service", "/payments", "{amount:200}"));
        System.out.println(client.post("payment-service", "/payments", "{amount:300}"));
    }
}
```

Output mental model:

```text
POST http://10.0.1.10:8080/payments body={amount:100}
POST http://10.0.1.11:8080/payments body={amount:200}
POST http://10.0.1.10:8080/payments body={amount:300}
```

This is the simplified heart of Feign + Eureka + LoadBalancer.

---

## 24. Production Configuration Example

```yaml
server:
  port: 8081

spring:
  application:
    name: order-service
  cloud:
    openfeign:
      client:
        config:
          payment-service:
            connectTimeout: 1000
            readTimeout: 2000
            loggerLevel: basic

eureka:
  client:
    service-url:
      defaultZone: http://eureka-1:8761/eureka/,http://eureka-2:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
    registry-fetch-interval-seconds: 30
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90

resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        slidingWindowSize: 50
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 5
  retry:
    instances:
      paymentService:
        maxAttempts: 2
        waitDuration: 200ms
```

---

## 25. Observability

For production, every Feign call should be observable.

Track:

```text
- request count by downstream service
- latency p50/p95/p99
- error rate
- timeout count
- retry count
- circuit breaker state
- selected instance / zone
- correlation ID
```

Good log:

```text
correlationId=abc-123 orderId=ord-1 downstream=payment-service method=POST path=/payments status=200 latencyMs=83 instance=10.0.1.13:8082
```

Bad log:

```text
Payment failed
```

Why bad? It lacks service, status, latency, instance, and correlation ID.

---

## 26. Common Interview Questions

### Q1. Does Feign call Eureka on every request?

No. Usually the Eureka client maintains a local registry cache. Feign goes through Spring Cloud LoadBalancer, which uses DiscoveryClient and the local cached instance list. Eureka is refreshed periodically, not queried for every request.

### Q2. What happens if one instance dies?

There is a delay before all clients stop calling it. Eureka needs to detect missed heartbeats, update registry, and clients need to refresh their local cache. During this window, Feign calls may fail. Timeouts, retries, circuit breakers, and graceful shutdown reduce the impact.

### Q3. What is the difference between Eureka and OpenFeign?

Eureka is service discovery. It stores where services are running. OpenFeign is a declarative HTTP client. It converts Java interface method calls into HTTP requests. Feign can use Eureka indirectly through Spring Cloud LoadBalancer.

### Q4. Why not hardcode service URLs?

Hardcoded URLs fail when instances scale up/down, restart, move hosts, deploy across zones, or run in containers. Service discovery allows dynamic instance resolution.

### Q5. Is Eureka strongly consistent?

No. Eureka favors availability and eventual consistency. Clients use cached registry data. During failures, stale instance data may exist temporarily.

### Q6. What is self-preservation mode?

Eureka self-preservation prevents mass eviction when heartbeats suddenly drop, because the cause may be network partition rather than service failure. It protects availability but can keep stale instances longer.

### Q7. How do you make Feign production-ready?

Use timeouts, proper error decoding, idempotency keys for retried writes, circuit breakers, metrics, tracing, request interceptors, and graceful fallback. Also avoid retry storms and avoid fake-success fallbacks.

### Q8. What happens during rolling deployment?

Old and new instances may both be registered. Clients may call either version until registry refresh catches up. Use backward-compatible APIs, metadata-based routing, or gateway-level deployment strategies for safer rollouts.

---

## 27. Common Bugs and Fixes

| Bug | Root Cause | Fix |
|---|---|---|
| `No instances available for payment-service` | service not registered or wrong name | check `spring.application.name` and Eureka dashboard |
| Feign timeout | downstream slow or timeout too low | inspect p99, DB latency, thread pools |
| 404 from Feign | wrong path mapping | compare controller path and Feign path |
| DTO decode error | JSON mismatch | fix fields / Jackson annotations |
| duplicate payment | unsafe retry | use idempotency key |
| stale instance calls | registry delay | timeout + retry + graceful shutdown |
| auth missing | headers not propagated | Feign RequestInterceptor |
| all calls hit same zone badly | no zone-aware routing | metadata + custom supplier/load balancer |

---

## 28. Strong Interview Answer

Spring Cloud Eureka provides dynamic service registration and discovery. Each service registers its instance metadata with Eureka and keeps renewing its lease through heartbeats. Other services fetch the registry periodically and keep a local cache, so Eureka is not on the request hot path.

OpenFeign provides a declarative HTTP client. When I define `@FeignClient(name = "payment-service")`, Spring creates a proxy implementation of that interface. When the method is called, Feign builds an HTTP request from the annotations and DTOs. The logical service name is passed to Spring Cloud LoadBalancer, which asks DiscoveryClient for available instances, usually from the local Eureka cache. The load balancer selects one instance, and Feign sends the HTTP request to that host and port.

This system is eventually consistent. If an instance dies, clients may still have stale registry data for a short time. Therefore, production Feign calls need timeouts, error decoding, retries only for idempotent operations, circuit breakers, metrics, tracing, and graceful shutdown. Eureka solves discovery, Feign solves declarative calls, and LoadBalancer connects them by choosing the actual instance.

---

## 29. Last-Minute Revision

```text
Eureka Server
  -> registry of service instances

Eureka Client
  -> register
  -> heartbeat
  -> fetch registry
  -> local cache

OpenFeign
  -> Java interface proxy
  -> builds HTTP request
  -> encodes request body
  -> decodes response body

LoadBalancer
  -> resolves service name
  -> gets instances from DiscoveryClient
  -> chooses one instance

Production safety
  -> timeout
  -> retry only idempotent calls
  -> circuit breaker
  -> fallback carefully
  -> metrics/tracing
  -> graceful shutdown
```

One-line mental model:

```text
Feign turns Java method calls into HTTP requests; Eureka tells where services live; LoadBalancer chooses which instance receives the request.
```

---

## 30. How This Connects to Previous MiniServiceDiscovery Chapters

| Previous Chapter | Connection |
|---|---|
| Service Registry Mental Model | Eureka is the production framework version |
| Registration / Lookup / Deregistration | Eureka client lifecycle |
| Heartbeat / Lease / TTL | Eureka lease renewal |
| Dead Instance Removal | Eureka eviction |
| Self Preservation | Eureka availability protection |
| Client-side Load Balancing | Feign + Spring Cloud LoadBalancer |
| Health-aware Load Balancing | Actuator + metadata + filtering |
| Network Partition / Stale Registry | Eureka eventual consistency |
| Registry Replication Model | Multi-node Eureka cluster |

---

## 31. Practical Checklist Before Using in Production

```text
[ ] Service names are stable and lowercase-friendly
[ ] Eureka defaultZone points to multiple servers in production
[ ] Actuator health is configured
[ ] Readiness reflects DB/cache/message dependencies
[ ] Feign connect/read timeouts configured
[ ] ErrorDecoder maps downstream errors cleanly
[ ] Retry enabled only for safe calls
[ ] Idempotency key used for write retries
[ ] Circuit breaker around critical downstreams
[ ] Correlation ID propagated
[ ] Metrics and tracing enabled
[ ] Graceful shutdown configured
[ ] APIs backward-compatible during rolling deploys
[ ] No fake success fallback for financial operations
```

---

## 32. Final Mental Model

Think of the system as four layers:

```text
Business code:
  orderService.placeOrder()

Feign layer:
  paymentClient.pay()
  Java method -> HTTP request

Discovery/load-balancing layer:
  payment-service -> [instance A, B, C] -> choose B

Network layer:
  POST http://10.0.1.13:8082/payments
```

If you can explain these four layers clearly, you understand Spring Cloud Eureka + OpenFeign internals well enough for system design, debugging, and production conversations.
