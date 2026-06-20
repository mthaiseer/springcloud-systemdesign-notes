# 014_Service_To_Service_Communication

> MiniDocker Deep Production Mode  
> Understanding First • ASCII Visual Learning • Mental Maps • Do Not Memorize  
> Topic: How containers talk to each other in real systems

---

# 0. Why This Chapter Exists

In production, one container almost never works alone.

A Spring Boot `order-service` may need:

```text
order-service
   |
   +--> user-service
   +--> payment-service
   +--> redis
   +--> postgres
   +--> kafka
```

If service-to-service communication is weak, the system fails even when every individual container is healthy.

The goal is not to memorize Docker networking commands. The goal is to understand what happens when one container calls another container.

Think like this:

```text
A microservice call is not magic.

It is always:

Name
  -> DNS resolution
  -> IP address
  -> Port
  -> Network path
  -> Application listener
  -> Response path
```

One missing piece breaks the call.

---

# 1. Not-To-Memorize Model

Do not memorize:

```text
bridge
veth
DNS
iptables
NAT
ports
networks
```

Instead remember this city model:

```text
Docker Network = City
Container      = House
IP Address     = House Address
Port           = Door Number
DNS Name       = Person/Shop Name
Bridge         = Internal Road System
Host Port      = Public Gate
```

Example:

```text
Real World

Customer says: "Go to Redis shop, counter 6379"

Docker World

order-service calls: redis:6379
```

You do not ask the customer to remember the shop latitude and longitude. You use the shop name.

Same in Docker:

```text
Good:
  http://user-service:8080

Bad:
  http://172.18.0.5:8080
```

Container IPs are temporary. Service names are stable.

---

# 2. One Picture First

```text
                      Browser / External Client
                                |
                                v
                         Host Machine Port
                              8080
                                |
                                v
+-------------------------------------------------------------------+
| Docker Host                                                       |
|                                                                   |
|  +--------------------- Docker Network: app-net ----------------+ |
|  |                                                             | |
|  |  +----------------+      DNS name       +----------------+  | |
|  |  | order-service  | ------------------> | user-service   |  | |
|  |  | 172.18.0.2     |   user-service:8080 | 172.18.0.3     |  | |
|  |  +----------------+                     +----------------+  | |
|  |          |                                                |  | |
|  |          | redis:6379                                    |  | |
|  |          v                                                |  | |
|  |  +----------------+                                      |  | |
|  |  | redis          |                                      |  | |
|  |  | 172.18.0.4     |                                      |  | |
|  |  +----------------+                                      |  | |
|  +-------------------------------------------------------------+ |
+-------------------------------------------------------------------+
```

Rule:

```text
Inside Docker network:
  service-name:container-port

From outside Docker:
  localhost:host-port
```

This is one of the most important Docker networking ideas.

---

# 3. Two Types Of Communication

There are two common communication directions.

```text
1. Outside -> Container
2. Container -> Container
```

## Outside To Container

```text
Browser
  |
  v
localhost:8080
  |
  v
Host port 8080
  |
  v
Container port 8080
  |
  v
Spring Boot app
```

This usually needs port publishing:

```bash
docker run -p 8080:8080 order-service
```

## Container To Container

```text
order-service
  |
  v
http://user-service:8080
  |
  v
Docker DNS
  |
  v
user-service container IP
```

This usually does not need `-p` between containers if both are on the same Docker network.

Important mental rule:

```text
-p is for outside world access.
Docker DNS is for internal container access.
```

---

# 4. The Most Common Beginner Mistake

A developer runs two containers:

```text
order-service
user-service
```

Then writes this inside `order-service`:

```java
String url = "http://localhost:8081/users/1";
```

This fails.

Why?

Inside a container, `localhost` means the same container.

```text
Inside order-service container:

localhost
   |
   v
order-service itself

NOT user-service
```

Correct:

```java
String url = "http://user-service:8080/users/1";
```

Visual:

```text
Wrong Mental Model

order-service container
   |
   v
localhost:8081
   |
   v
user-service   X wrong

Correct Mental Model

order-service container
   |
   v
user-service:8080
   |
   v
Docker DNS resolves user-service
```

Remember:

```text
localhost is not your laptop inside container.
localhost is the container itself.
```

---

# 5. Service Name Communication

In Docker Compose, service names become DNS names.

```yaml
services:
  order-service:
    image: order-service

  user-service:
    image: user-service
```

Docker automatically creates DNS entries:

```text
user-service -> container IP
order-service -> container IP
```

So this works:

```text
order-service calls user-service:8080
```

Diagram:

```text
+---------------- Docker Compose Network ----------------+
|                                                        |
|  order-service                                         |
|      |                                                 |
|      | DNS query: user-service                         |
|      v                                                 |
|  Docker embedded DNS                                   |
|      |                                                 |
|      | returns 172.20.0.3                              |
|      v                                                 |
|  user-service:8080                                     |
|                                                        |
+--------------------------------------------------------+
```

You should not care what the IP is.

---

# 6. Container Port vs Host Port

This is another common confusion.

```yaml
ports:
  - "8080:8080"
```

Meaning:

```text
host-port:container-port
```

Visual:

```text
Laptop Browser
   |
   v
localhost:8080       <-- host port
   |
   v
Docker forwarding
   |
   v
order-service:8080   <-- container port
```

But when another container calls it:

```text
user-service -> order-service:8080
```

It uses the container port, not the host port.

Example:

```yaml
services:
  user-service:
    ports:
      - "9001:8080"
```

From laptop:

```text
localhost:9001
```

From another container:

```text
user-service:8080
```

Do not call `user-service:9001` from another container unless the app actually listens on 9001 inside the container.

---

# 7. Real World Analogy: Apartment Building

Imagine Docker host as an apartment building.

```text
Building = Docker Host
Apartments = Containers
Apartment intercom names = Docker DNS names
Room doors = Ports
Main building gate = Host port mapping
```

Inside the building:

```text
Apartment 101 can call Apartment 102 by intercom name.
```

Outside visitor:

```text
Visitor must enter through building gate.
```

Docker equivalent:

```text
Container-to-container:
  user-service:8080

External-to-container:
  localhost:9001
```

This is why internal communication and external exposure are different.

---

# 8. Full Docker Compose Example

```yaml
version: "3.9"

services:
  order-service:
    build: ./order-service
    container_name: order-service
    ports:
      - "8080:8080"
    environment:
      USER_SERVICE_URL: http://user-service:8080
      REDIS_HOST: redis
      REDIS_PORT: 6379
    depends_on:
      - user-service
      - redis
    networks:
      - app-net

  user-service:
    build: ./user-service
    container_name: user-service
    expose:
      - "8080"
    networks:
      - app-net

  redis:
    image: redis:7
    container_name: redis
    expose:
      - "6379"
    networks:
      - app-net

networks:
  app-net:
    driver: bridge
```

Communication paths:

```text
Browser
  |
  v
localhost:8080
  |
  v
order-service:8080
  |
  +--> user-service:8080
  |
  +--> redis:6379
```

Notice:

```text
order-service has ports because browser needs it.
user-service uses expose because only internal containers need it.
redis uses expose because only internal containers need it.
```

---

# 9. Spring Boot Configuration

Never hardcode container IPs.

Use environment variables.

```yaml
# application.yml
services:
  user:
    base-url: ${USER_SERVICE_URL:http://localhost:8081}

spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
```

Local development without Docker:

```text
USER_SERVICE_URL default -> http://localhost:8081
REDIS_HOST default       -> localhost
```

Docker Compose:

```text
USER_SERVICE_URL -> http://user-service:8080
REDIS_HOST       -> redis
```

This gives one application that works in both worlds.

Mental model:

```text
Same code
   |
   +--> local values when running from IDE
   |
   +--> Docker DNS names when running in Compose
```

---

# 10. Java WebClient Example

```java
package com.example.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UserClient {

    private final WebClient webClient;

    public UserClient(
            WebClient.Builder builder,
            @Value("${services.user.base-url}") String userServiceBaseUrl
    ) {
        this.webClient = builder
                .baseUrl(userServiceBaseUrl)
                .build();
    }

    public UserDto getUser(Long userId) {
        return webClient.get()
                .uri("/api/users/{id}", userId)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }
}
```

Inside Docker:

```text
baseUrl = http://user-service:8080
```

Packet flow:

```text
OrderController
   |
   v
UserClient WebClient
   |
   v
DNS lookup: user-service
   |
   v
TCP connect to 172.20.0.3:8080
   |
   v
User Service Tomcat/Netty
```

---

# 11. Java RestTemplate Example

```java
package com.example.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserRestClient {

    private final RestTemplate restTemplate;
    private final String userServiceBaseUrl;

    public UserRestClient(
            RestTemplate restTemplate,
            @Value("${services.user.base-url}") String userServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.userServiceBaseUrl = userServiceBaseUrl;
    }

    public UserDto getUser(Long userId) {
        String url = userServiceBaseUrl + "/api/users/" + userId;
        return restTemplate.getForObject(url, UserDto.class);
    }
}
```

Configuration:

```java
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

Good:

```text
http://user-service:8080/api/users/10
```

Bad:

```text
http://localhost:8081/api/users/10
http://172.20.0.3:8080/api/users/10
```

---

# 12. OpenFeign Example

If you use Spring Cloud OpenFeign:

```java
@FeignClient(
    name = "user-service-client",
    url = "${services.user.base-url}"
)
public interface UserFeignClient {

    @GetMapping("/api/users/{id}")
    UserDto getUser(@PathVariable("id") Long id);
}
```

In Docker Compose:

```yaml
environment:
  USER_SERVICE_URL: http://user-service:8080
```

Flow:

```text
Order Service
   |
   v
Feign Proxy
   |
   v
HTTP request
   |
   v
user-service:8080
```

OpenFeign does not remove networking problems. It only gives a cleaner Java interface.

The network still needs:

```text
correct DNS name
correct port
correct Docker network
healthy target app
```

---

# 13. Redis Service Communication

Spring Boot Redis configuration:

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
```

Docker Compose:

```yaml
redis:
  image: redis:7
  expose:
    - "6379"
```

Order service environment:

```yaml
environment:
  REDIS_HOST: redis
  REDIS_PORT: 6379
```

Visual:

```text
order-service
   |
   | TCP redis:6379
   v
Docker DNS
   |
   v
redis container IP
   |
   v
Redis server process listening on 6379
```

Java example:

```java
@Service
public class CartCacheService {

    private final StringRedisTemplate redisTemplate;

    public CartCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveCartStatus(Long userId, String status) {
        redisTemplate.opsForValue().set("cart:" + userId + ":status", status);
    }

    public String getCartStatus(Long userId) {
        return redisTemplate.opsForValue().get("cart:" + userId + ":status");
    }
}
```

---

# 14. PostgreSQL Service Communication

Docker Compose:

```yaml
postgres:
  image: postgres:16
  environment:
    POSTGRES_DB: orders
    POSTGRES_USER: app
    POSTGRES_PASSWORD: secret
  expose:
    - "5432"
  networks:
    - app-net
```

Spring Boot:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:orders}
    username: ${DB_USER:app}
    password: ${DB_PASSWORD:secret}
```

Docker environment:

```yaml
environment:
  DB_HOST: postgres
  DB_PORT: 5432
  DB_NAME: orders
  DB_USER: app
  DB_PASSWORD: secret
```

Packet path:

```text
OrderRepository
   |
   v
HikariCP connection pool
   |
   v
JDBC URL postgres:5432
   |
   v
Docker DNS
   |
   v
Postgres container
```

Common issue:

```text
Container starts before Postgres is ready.
```

`depends_on` controls start order, not readiness.

---

# 15. Startup Order vs Readiness

This is a production-level mistake.

```yaml
depends_on:
  - postgres
```

This means:

```text
Start postgres container before app container.
```

It does not mean:

```text
Postgres is ready to accept connections.
```

Failure flow:

```text
Docker starts postgres container
   |
   v
Docker starts order-service
   |
   v
Spring Boot starts HikariCP
   |
   v
DB connection attempt
   |
   v
Postgres still initializing
   |
   v
Connection refused
```

Better approach:

```text
Use retries in application
Use healthchecks
Use wait strategy in local Compose
Use Kubernetes readiness probes in K8s
```

Compose healthcheck example:

```yaml
postgres:
  image: postgres:16
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U app -d orders"]
    interval: 5s
    timeout: 3s
    retries: 10

order-service:
  depends_on:
    postgres:
      condition: service_healthy
```

---

# 16. Request Dry Run: Order Calls User

Request:

```text
GET /api/orders/100
```

Flow:

```text
1. Browser calls localhost:8080/api/orders/100
2. Docker forwards host 8080 to order-service container 8080
3. OrderController receives request
4. OrderService needs user details
5. UserClient calls http://user-service:8080/api/users/7
6. Docker DNS resolves user-service to container IP
7. TCP connection opens to user-service:8080
8. UserController returns JSON
9. OrderService combines order + user data
10. Browser receives response
```

ASCII trace:

```text
Browser
  |
  | localhost:8080/api/orders/100
  v
order-service
  |
  | user-service:8080/api/users/7
  v
Docker DNS
  |
  | 172.20.0.3
  v
user-service
  |
  | JSON user details
  v
order-service
  |
  | final order response
  v
Browser
```

---

# 17. Synchronous HTTP Communication

HTTP service-to-service communication is simple and common.

```text
order-service ---> user-service
```

Pros:

```text
Easy to understand
Immediate response
Good for read dependencies
Simple debugging with curl
```

Cons:

```text
Creates runtime dependency
Can increase latency
Can cascade failures
Needs timeout and retry control
```

Bad design:

```text
Order request waits forever for user-service
```

Good design:

```text
Order request has timeout
Fallback possible
Circuit breaker possible
Logs trace correlation ID
```

---

# 18. Timeout Is Mandatory

Without timeout:

```text
order-service thread waits
   |
   v
more requests arrive
   |
   v
thread pool fills
   |
   v
order-service becomes slow
   |
   v
system-wide failure
```

WebClient timeout example:

```java
@Bean
public WebClient.Builder webClientBuilder() {
    HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(2));

    return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient));
}
```

RestTemplate timeout example:

```java
@Bean
public RestTemplate restTemplate() {
    var factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(1000);
    factory.setReadTimeout(2000);
    return new RestTemplate(factory);
}
```

Mental model:

```text
Timeout = do not wait at a locked door forever.
```

---

# 19. Retry Must Be Careful

Retry can help temporary failures.

But retry can also multiply load.

```text
100 requests/sec
   |
   | retry 3 times
   v
300 backend calls/sec
```

If user-service is already struggling, aggressive retries make it worse.

Better:

```text
small retry count
backoff
retry only safe operations
never blindly retry payment mutation
```

Safe retry example:

```text
GET /users/7       usually safe
POST /payments     dangerous unless idempotency key exists
```

Visual:

```text
Bad Retry Storm

order-service
   | retry retry retry
   v
user-service already overloaded
   |
   v
complete failure
```

---

# 20. Idempotency For Service Calls

For write calls, service-to-service communication needs idempotency.

Example payment flow:

```text
order-service -> payment-service -> bank
```

If timeout happens after payment succeeded:

```text
order-service thinks failed
   |
   v
retries payment
   |
   v
customer charged twice
```

Fix:

```text
Idempotency-Key: order-100-payment-1
```

Java example:

```java
webClient.post()
    .uri("/api/payments")
    .header("Idempotency-Key", "order-" + orderId + "-payment")
    .bodyValue(request)
    .retrieve()
    .bodyToMono(PaymentResponse.class)
    .block();
```

Mental model:

```text
Idempotency key = receipt number.
If same receipt appears again, do not charge again.
```

---

# 21. Async Communication With Kafka

Not all service-to-service communication should be HTTP.

Synchronous:

```text
order-service waits for payment-service
```

Asynchronous:

```text
order-service publishes event
payment-service consumes later
```

Diagram:

```text
order-service
   |
   | OrderCreated event
   v
Kafka topic
   |
   +--> payment-service
   +--> inventory-service
   +--> notification-service
```

Use async when:

```text
caller does not need immediate response
high fanout
system should tolerate temporary downstream failure
event history is useful
```

Use HTTP when:

```text
caller needs immediate answer
simple query
low latency dependency
```

---

# 22. Docker Compose Kafka Communication

```yaml
kafka:
  image: bitnami/kafka:latest
  environment:
    KAFKA_CFG_NODE_ID: 1
    KAFKA_CFG_PROCESS_ROLES: broker,controller
    KAFKA_CFG_CONTROLLER_QUORUM_VOTERS: 1@kafka:9093
    KAFKA_CFG_LISTENERS: PLAINTEXT://:9092,CONTROLLER://:9093
    KAFKA_CFG_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
    KAFKA_CFG_CONTROLLER_LISTENER_NAMES: CONTROLLER
    KAFKA_CFG_AUTO_CREATE_TOPICS_ENABLE: true
  expose:
    - "9092"
```

Spring Boot:

```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:kafka:9092}
```

Important:

```text
Kafka advertised listener must be reachable by the client.
```

Inside Docker network:

```text
kafka:9092
```

From laptop:

```text
localhost:mapped-port
```

Kafka networking failures often come from wrong advertised listeners.

---

# 23. Service Communication Patterns

```text
Service-To-Service Communication
        |
        +--> HTTP REST
        |      +--> request/response
        |      +--> simple, direct
        |
        +--> gRPC
        |      +--> binary, fast
        |      +--> strong contract
        |
        +--> Messaging
        |      +--> Kafka/RabbitMQ
        |      +--> async, durable
        |
        +--> Shared Cache
        |      +--> Redis
        |      +--> not main source of truth
        |
        +--> Shared DB
               +--> usually avoid between services
```

Avoid this:

```text
order-service directly reads user-service database
```

Why?

```text
breaks ownership
creates tight coupling
schema changes break other service
hard to scale independently
```

Better:

```text
order-service calls user-service API
or consumes UserUpdated event
```

---

# 24. The Shared Database Trap

Bad:

```text
+----------------+        +----------------+
| order-service  |        | user-service   |
+-------+--------+        +-------+--------+
        |                         |
        +----------+--------------+
                   |
                   v
              same database
```

Problem:

```text
user-service changes users table
order-service query breaks
```

Better:

```text
+----------------+        HTTP/Event        +----------------+
| order-service  | -----------------------> | user-service   |
+----------------+                          +----------------+
```

Or denormalized read model:

```text
user-service publishes UserUpdated
   |
   v
order-service stores needed user snapshot
```

Production rule:

```text
A service owns its data.
Other services communicate through API or events.
```

---

# 25. Network Isolation With Multiple Networks

Not every container should talk to every other container.

Example:

```text
frontend-net
backend-net
data-net
```

```yaml
services:
  gateway:
    networks:
      - frontend-net
      - backend-net

  order-service:
    networks:
      - backend-net
      - data-net

  postgres:
    networks:
      - data-net
```

Diagram:

```text
frontend-net
   |
   v
 gateway
   |
backend-net
   |
   v
order-service
   |
data-net
   |
   v
postgres
```

Browser cannot directly reach Postgres.
Gateway cannot directly reach Postgres unless attached to `data-net`.

This is security by network design.

---

# 26. Debugging: Can The Containers See Each Other?

First check networks:

```bash
docker network ls
```

Inspect network:

```bash
docker network inspect app-net
```

You should see containers attached:

```text
app-net
  |
  +--> order-service
  +--> user-service
  +--> redis
  +--> postgres
```

If two containers are not on the same network, DNS/service calls fail.

Mental model:

```text
Different Docker networks = different private cities.
No road unless connected.
```

---

# 27. Debugging DNS

Enter container:

```bash
docker exec -it order-service sh
```

Test DNS:

```bash
getent hosts user-service
```

Or:

```bash
nslookup user-service
```

Expected:

```text
172.20.0.3 user-service
```

If DNS fails:

```text
wrong service name
not same network
container not running
custom network missing
```

Do not start with Java debugging first. Start with network truth.

---

# 28. Debugging Port Reachability

Inside order-service:

```bash
curl http://user-service:8080/actuator/health
```

Possible results:

```text
200 OK              -> network and app reachable
Connection refused  -> DNS works, app not listening or wrong port
Could not resolve   -> DNS/network problem
Timeout             -> app stuck, firewall, wrong route, overload
404                 -> app reachable, wrong path
```

Visual:

```text
DNS fail:
  name -> X

Connection refused:
  name -> IP -> closed door

Timeout:
  name -> IP -> door not answering

404:
  name -> IP -> app answered, path wrong
```

---

# 29. Debugging Spring Boot Binding

Spring Boot should listen on all interfaces inside a container.

Good:

```yaml
server:
  address: 0.0.0.0
  port: 8080
```

Usually Spring Boot defaults to all interfaces, but explicit config can break it.

Bad:

```yaml
server:
  address: 127.0.0.1
```

Inside container:

```text
App listens only to itself.
Other containers cannot connect.
```

Visual:

```text
user-service container

127.0.0.1:8080  only inside container
0.0.0.0:8080    reachable via container network
```

Check listening ports:

```bash
netstat -tulnp
```

or:

```bash
ss -tulnp
```

---

# 30. Production Failure Story: Localhost Bug

A team had this config:

```yaml
USER_SERVICE_URL: http://localhost:8081
```

It worked on laptop from IDE.

But inside Docker:

```text
order-service called localhost:8081
```

Meaning:

```text
order-service called itself
```

Result:

```text
Connection refused
```

Fix:

```yaml
USER_SERVICE_URL: http://user-service:8080
```

Lesson:

```text
localhost changes meaning depending on where the code runs.
```

Interview-quality explanation:

```text
When code runs inside a container, localhost refers to that container's own network namespace, not the host machine and not another container. For inter-container calls on the same Docker network, use Docker DNS service names such as user-service:8080.
```

---

# 31. Production Failure Story: Wrong Port

Compose:

```yaml
user-service:
  ports:
    - "9001:8080"
```

Developer used:

```text
http://user-service:9001
```

Failure:

```text
Connection refused
```

Why?

Inside the Docker network, service listens on container port 8080.

Correct:

```text
http://user-service:8080
```

Mental model:

```text
9001 = building gate for outside visitors
8080 = apartment door inside the building
```

---

# 32. Production Failure Story: DNS Name Drift

Compose service name:

```yaml
users:
  image: user-service
```

Application config:

```text
http://user-service:8080
```

This fails because Docker DNS name is `users`, not `user-service`.

Fix one of these:

```yaml
services:
  user-service:
```

or:

```text
http://users:8080
```

Rule:

```text
Compose service key becomes DNS name.
container_name is not the main design tool.
Prefer service name.
```

---

# 33. Observability For Service Calls

A service call should be traceable.

Add correlation ID:

```text
X-Correlation-Id: req-abc-123
```

Flow:

```text
Browser
  |
  | X-Correlation-Id=req-abc-123
  v
order-service logs req-abc-123
  |
  v
user-service logs req-abc-123
  |
  v
redis logs/metrics around same time
```

Java filter idea:

```java
@Component
public class CorrelationIdFilter implements Filter {

    private static final String HEADER = "X-Correlation-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String correlationId = httpRequest.getHeader(HEADER);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("correlationId");
        }
    }
}
```

Without correlation IDs, debugging distributed calls becomes guesswork.

---

# 34. Health Endpoint Communication

Each service should expose health:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      probes:
        enabled: true
```

Docker healthcheck:

```yaml
user-service:
  healthcheck:
    test: ["CMD", "wget", "-q", "-O", "-", "http://localhost:8080/actuator/health"]
    interval: 10s
    timeout: 3s
    retries: 5
```

Note:

Inside the container healthcheck, `localhost` is correct because the check runs inside the same container.

```text
Healthcheck inside user-service:
  localhost:8080 = user-service itself

order-service calling user-service:
  user-service:8080 = another container
```

Context decides meaning.

---

# 35. Security Mindset

Do not expose every service to the host.

Bad:

```yaml
user-service:
  ports:
    - "8081:8080"
postgres:
  ports:
    - "5432:5432"
redis:
  ports:
    - "6379:6379"
```

Better:

```yaml
order-service:
  ports:
    - "8080:8080"

user-service:
  expose:
    - "8080"

postgres:
  expose:
    - "5432"

redis:
  expose:
    - "6379"
```

Mental model:

```text
Only public reception should face the street.
Internal rooms should not have public doors.
```

Production principle:

```text
Expose the edge service.
Keep internal services private.
```

---

# 36. Kubernetes Connection

Docker Compose:

```text
order-service -> user-service:8080
```

Kubernetes:

```text
order-service -> user-service.default.svc.cluster.local:8080
```

Or short name in same namespace:

```text
http://user-service:8080
```

Docker Compose service:

```yaml
services:
  user-service:
```

Kubernetes Service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: user-service
spec:
  selector:
    app: user-service
  ports:
    - port: 8080
      targetPort: 8080
```

Mental mapping:

```text
Docker Compose service name ~= Kubernetes Service name
Docker network DNS       ~= Kubernetes CoreDNS
Container port           ~= targetPort
Service port             ~= stable access port
```

---

# 37. Kubernetes Service-To-Pod Flow

```text
order-service Pod
   |
   | http://user-service:8080
   v
CoreDNS
   |
   v
ClusterIP Service
   |
   v
kube-proxy / iptables / ipvs
   |
   v
one healthy user-service Pod
```

Compared to Docker Compose:

```text
order-service container
   |
   | http://user-service:8080
   v
Docker DNS
   |
   v
user-service container
```

Same mental model, different production machinery.

---

# 38. Load Balancing Between Replicas

Docker Compose can scale services:

```bash
docker compose up --scale user-service=3
```

Mental picture:

```text
                 user-service
                      |
        +-------------+-------------+
        |             |             |
        v             v             v
  user-service-1 user-service-2 user-service-3
```

Docker DNS may return multiple IPs.

In Kubernetes, Service load balances across Pods.

```text
user-service ClusterIP
        |
   +----+----+----+
   |         |    |
   v         v    v
 pod-1     pod-2 pod-3
```

Application should not store one resolved IP forever.

---

# 39. Anti-Patterns

## Hardcoded IP

```text
http://172.20.0.3:8080
```

Bad because IP changes after restart.

## Localhost For Another Container

```text
http://localhost:8080
```

Bad because it calls itself.

## Public Ports For Everything

```text
Expose Redis/Postgres to laptop unnecessarily.
```

Bad because security risk.

## No Timeout

```text
One slow service can freeze caller.
```

## Shared Database Between Services

```text
Breaks service ownership.
```

## Retry Without Idempotency

```text
Can duplicate writes.
```

---

# 40. Command Cheat Sheet

```bash
# List containers
docker ps

# List networks
docker network ls

# Inspect a network
docker network inspect app-net

# Enter a container
docker exec -it order-service sh

# Test DNS from inside container
getent hosts user-service

# Test HTTP reachability
curl http://user-service:8080/actuator/health

# View logs
docker logs order-service

# Follow logs
docker logs -f order-service

# Start compose stack
docker compose up --build

# Scale service
docker compose up --scale user-service=3
```

---

# 41. Debugging Decision Tree

```text
Service call failed
        |
        v
Is URL using localhost?
        |
        +-- yes --> replace with service name
        |
        no
        |
        v
Can DNS resolve service name?
        |
        +-- no --> check same Docker network and service name
        |
        yes
        |
        v
Can curl service:port?
        |
        +-- connection refused --> wrong port or app not listening
        |
        +-- timeout --> app stuck/network issue/overload
        |
        +-- 404 --> wrong path but network okay
        |
        +-- 200 --> app-level issue in Java code/config
```

This tree saves hours.

---

# 42. Strong Interview Answer

Question:

```text
How do containers communicate with each other in Docker?
```

Answer:

```text
Containers communicate over Docker networks. In a user-defined bridge network, Docker provides embedded DNS so containers can reach each other using service names instead of hardcoded IP addresses. For example, order-service can call http://user-service:8080 if both containers are attached to the same network. The call resolves through Docker DNS to the target container IP and connects to the container port. Host port mappings are only needed for access from outside Docker, not for container-to-container calls.
```

Question:

```text
Why does localhost fail inside Docker?
```

Answer:

```text
Inside a container, localhost refers to that container's own network namespace. It does not refer to the host machine or another container. For another container, use the Docker service name such as user-service:8080.
```

Question:

```text
How do you debug service-to-service failures?
```

Answer:

```text
I first verify the configured URL, then check whether both containers are on the same Docker network. From the caller container, I test DNS resolution using getent hosts or nslookup, then test port reachability with curl. The error type tells me the layer: DNS failure means naming/network issue, connection refused means wrong port or app not listening, timeout means slow/unreachable target, and 404 means network is fine but route is wrong.
```

---

# 43. Final Cheat Sheet

```text
Inside Docker:
  service-name:container-port

Outside Docker:
  localhost:host-port

localhost inside container:
  same container

Docker Compose service key:
  DNS name

ports:
  publish to host

expose:
  document/internal port

Same network:
  containers can discover each other

Different network:
  no communication unless connected

Timeout:
  mandatory

Retry:
  careful

Idempotency:
  required for write retries
```

---

# 44. One Picture To Remember

```text
                         Outside World
                              |
                              | localhost:8080
                              v
+----------------------------------------------------------------+
| Docker Host                                                    |
|                                                                |
|  Published Host Port                                           |
|        |                                                       |
|        v                                                       |
|  +------------------- Docker Network: app-net ---------------+ |
|  |                                                          | |
|  |  +----------------+                                      | |
|  |  | order-service  |                                      | |
|  |  | :8080          |                                      | |
|  |  +-------+--------+                                      | |
|  |          |                                               | |
|  |          | http://user-service:8080                      | |
|  |          v                                               | |
|  |  +----------------+                                      | |
|  |  | user-service   |                                      | |
|  |  | :8080          |                                      | |
|  |  +----------------+                                      | |
|  |          ^                                               | |
|  |          | redis:6379                                    | |
|  |          | postgres:5432                                 | |
|  |  +----------------+    +----------------+                | |
|  |  | redis          |    | postgres       |                | |
|  |  | :6379          |    | :5432          |                | |
|  |  +----------------+    +----------------+                | |
|  |                                                          | |
|  +----------------------------------------------------------+ |
+----------------------------------------------------------------+

Remember:

Outside uses host port.
Inside uses service name + container port.
Do not memorize commands; follow the packet.
```

---

# 45. Final Takeaways

1. Service-to-service communication starts with a name, not an IP.
2. Docker Compose service names become DNS names.
3. `localhost` inside a container means the same container.
4. Container-to-container communication uses container ports.
5. Host port mappings are for outside access.
6. Use environment variables for URLs and hosts.
7. Add timeouts to all network calls.
8. Retry only with care and idempotency.
9. Use healthchecks and readiness thinking.
10. Debug in layers: URL, DNS, network, port, app, logs.
11. Kubernetes keeps the same mental model but uses Services and CoreDNS.
12. The best engineers follow the packet path instead of guessing.
