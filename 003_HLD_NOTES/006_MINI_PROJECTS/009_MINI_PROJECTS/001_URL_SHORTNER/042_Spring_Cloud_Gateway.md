# 042_Spring_Cloud_Gateway.md
# MiniURLShortener — Spring Cloud Gateway

> Core mental model: **Spring Cloud Gateway is the programmable front door of your microservice system. It accepts outside traffic, applies cross-cutting rules once, and safely routes each request to the correct internal service.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Gateway vs Load Balancer vs Service Discovery](#4-gateway-vs-load-balancer-vs-service-discovery)
- [5. Request Flow Big Picture](#5-request-flow-big-picture)
- [6. Spring Cloud Gateway Internal Model](#6-spring-cloud-gateway-internal-model)
- [7. Route Predicate Filter Mental Model](#7-route-predicate-filter-mental-model)
- [8. Gateway Project Setup](#8-gateway-project-setup)
- [9. Static Routing Configuration](#9-static-routing-configuration)
- [10. Discovery-Based Routing With Eureka](#10-discovery-based-routing-with-eureka)
- [11. Gateway For MiniURLShortener](#11-gateway-for-miniurlshortener)
- [12. Path Rewriting And Prefix Strategy](#12-path-rewriting-and-prefix-strategy)
- [13. Global Filters](#13-global-filters)
- [14. Correlation ID Filter](#14-correlation-id-filter)
- [15. Authentication Boundary](#15-authentication-boundary)
- [16. Rate Limiting At Gateway](#16-rate-limiting-at-gateway)
- [17. Resilience Timeout Retry Circuit Breaker](#17-resilience-timeout-retry-circuit-breaker)
- [18. CORS And Browser Boundary](#18-cors-and-browser-boundary)
- [19. Error Handling At Gateway](#19-error-handling-at-gateway)
- [20. Observability And Access Logs](#20-observability-and-access-logs)
- [21. Step-by-Step Dry Runs](#21-step-by-step-dry-runs)
- [22. Internal Execution Walkthrough](#22-internal-execution-walkthrough)
- [23. Production Failure Stories](#23-production-failure-stories)
- [24. Debugging Mindset](#24-debugging-mindset)
- [25. Common Mistakes](#25-common-mistakes)
- [26. Interview-Ready Explanation](#26-interview-ready-explanation)
- [27. Senior Engineer Checklist](#27-senior-engineer-checklist)
- [28. One-Page Cheat Sheet](#28-one-page-cheat-sheet)
- [29. One Picture To Remember](#29-one-picture-to-remember)

---

## 1. Why This Exists

After adding service discovery, your system no longer has only one backend.

MiniURLShortener can evolve into multiple services:

```text
url-service
analytics-service
user-service
auth-service
admin-service
notification-service
```

Without a gateway, clients need to know too much:

```text
Frontend calls url-service directly.
Mobile app calls analytics-service directly.
Admin panel calls admin-service directly.
Each client knows internal ports, hostnames, auth rules, retry rules, and paths.
```

That is fragile.

Bad shape:

```text
Browser/Mobile
   |       |       |
   v       v       v
URL Svc  User Svc  Analytics Svc
```

Problems:

```text
1. Internal services are exposed directly.
2. Authentication logic gets copied everywhere.
3. Rate limiting is duplicated.
4. CORS is duplicated.
5. Paths become inconsistent.
6. Clients break when service topology changes.
7. Observability is scattered.
8. Public API contract becomes tied to internal service names.
```

Spring Cloud Gateway gives one programmable entry point:

```text
Browser/Mobile
      |
      v
+-----------------------+
| Spring Cloud Gateway  |
| auth / route / filter |
+-----------------------+
      |
      +----> url-service
      +----> analytics-service
      +----> user-service
      +----> admin-service
```

Production memory:

```text
Gateway hides internal topology and centralizes edge concerns.
```

For MiniURLShortener:

```text
POST /api/v1/urls        -> url-service
GET  /r/{shortCode}      -> url-service redirect endpoint
GET  /api/v1/analytics   -> analytics-service
GET  /api/v1/admin/*     -> admin-service
```

Clients see one domain:

```text
https://miniurl.com
```

Internal services remain private:

```text
http://url-service:8081
http://analytics-service:8082
http://admin-service:8083
```

---

## 2. The One Core Mental Model

Spring Cloud Gateway is:

```text
A PROGRAMMABLE FRONT DOOR
```

It does three core jobs:

```text
1. Match request.
2. Apply filters.
3. Route to destination.
```

ASCII:

```text
External Request
      |
      v
+------------------------------+
| Spring Cloud Gateway         |
|                              |
|  1. Predicate: does route fit?|
|  2. Filters: modify/check     |
|  3. URI: send to service      |
+------------------------------+
      |
      v
Internal Service
```

The gateway is not your business logic layer.

Wrong mental model:

```text
Gateway = place to implement URL creation, analytics, user management.
```

Correct mental model:

```text
Gateway = edge policy + routing layer.
```

One-line memory:

```text
Gateway decides how traffic enters; services decide what business action happens.
```

For MiniURLShortener:

```text
Gateway may check JWT.
Gateway may rate limit by IP/user.
Gateway may add correlation ID.
Gateway may rewrite /api/v1/urls to /urls.
Gateway may route to url-service.
Gateway should not decide how short codes are generated.
```

---

## 3. Problem Statement

Build a production-shaped Spring Cloud Gateway for MiniURLShortener.

It must support:

```text
1. One public entry point for clients.
2. Route /api/v1/urls/** to url-service.
3. Route /r/** redirect traffic to url-service.
4. Route /api/v1/analytics/** to analytics-service later.
5. Route /api/v1/admin/** to admin-service later.
6. Add correlation ID to every request.
7. Support service discovery with Eureka.
8. Support path rewrite.
9. Support basic gateway-level rate limiting.
10. Prepare for JWT authentication.
11. Provide safe gateway error behavior.
12. Expose health and metrics.
```

It should avoid:

```text
1. Hardcoding every service instance IP in clients.
2. Duplicating auth/rate-limit/CORS logic in every service.
3. Putting business logic inside gateway filters.
4. Exposing internal service names publicly.
5. Infinite retries at the edge.
6. Large request body processing in gateway.
7. Silent 404 due to wrong route predicates.
```

Out of scope for this chapter:

```text
1. Full OAuth2 resource server implementation.
2. Full Kubernetes ingress controller setup.
3. Advanced WAF.
4. Complete distributed tracing stack.
5. Production Redis cluster details for rate limiting.
```

This chapter creates the gateway mental model and a clean implementation foundation.

---

## 4. Gateway vs Load Balancer vs Service Discovery

These three are often confused.

```text
Gateway:
    Public entry point + routing + edge policies.

Load balancer:
    Distributes traffic across instances.

Service discovery:
    Registry that tells where service instances are.
```

ASCII:

```text
Client
  |
  v
+------------------+
| Gateway          |
| route by path    |
+------------------+
  |
  v
+------------------+
| Load Balancer    |
| choose instance  |
+------------------+
  |
  v
+------------------+
| Service Instance |
+------------------+
```

With Eureka + Spring Cloud LoadBalancer:

```text
Gateway route URI:
    lb://URL-SERVICE

Gateway asks discovery:
    where is URL-SERVICE?

Discovery returns:
    instance-1:8081
    instance-2:8081

Load balancer chooses one instance.
```

ASCII:

```text
              +------------------+
              | Eureka Registry  |
              | URL-SERVICE x 2  |
              +------------------+
                       ^
                       |
Client ---> Gateway ---+
             |
             v
       chooses one URL-SERVICE instance
```

Important difference:

```text
Gateway decides WHICH SERVICE.
Load balancer decides WHICH INSTANCE of that service.
Service discovery tells WHERE instances are.
```

Interview one-liner:

```text
The gateway is the edge router, Eureka is the phonebook, and the load balancer chooses a healthy phone number from that phonebook.
```

---

## 5. Request Flow Big Picture

Example request:

```http
POST /api/v1/urls
Host: api.miniurl.com
X-Request-Id: abc-123
Authorization: Bearer jwt...
```

Flow:

```text
1. Request reaches gateway.
2. Gateway finds route matching /api/v1/urls/**.
3. Gateway applies pre-filters.
4. Correlation ID is created or propagated.
5. JWT can be checked at edge.
6. Rate limit can be checked.
7. Path may be rewritten.
8. Request is forwarded to url-service.
9. url-service creates short URL.
10. Response comes back through gateway.
11. Gateway applies post-filters.
12. Client receives response.
```

ASCII:

```text
Client
  |
  | POST /api/v1/urls
  v
+--------------------------------------------------+
| Gateway                                          |
|                                                  |
| Predicate: Path=/api/v1/urls/**                  |
| Pre filters: correlation, auth, rate limit       |
| Rewrite: /api/v1/urls -> /urls                   |
| Route URI: lb://URL-SERVICE                      |
+--------------------------------------------------+
  |
  v
+-------------------+
| URL-SERVICE       |
| POST /urls        |
+-------------------+
  |
  v
Response through gateway
```

Gateway is in the request path for every call.

Therefore:

```text
Gateway must be fast.
Gateway must be stateless.
Gateway must not block unnecessarily.
Gateway must not do heavy business computation.
```

---

## 6. Spring Cloud Gateway Internal Model

Spring Cloud Gateway is built on the reactive stack.

Important pieces:

```text
Route
Predicate
Filter
Gateway Handler Mapping
Gateway Web Handler
Netty HTTP client
```

Internal flow:

```text
HTTP Request
   |
   v
RoutePredicateHandlerMapping
   |
   v
Find matching Route
   |
   v
FilteringWebHandler
   |
   v
Apply GlobalFilters + GatewayFilters
   |
   v
NettyRoutingFilter
   |
   v
Downstream service
```

ASCII:

```text
+-----------------------------+
| Incoming HTTP Request       |
+-----------------------------+
              |
              v
+-----------------------------+
| RoutePredicateHandlerMapping|
| Which route matches?        |
+-----------------------------+
              |
              v
+-----------------------------+
| FilteringWebHandler         |
| Build filter chain          |
+-----------------------------+
              |
              v
+-----------------------------+
| Pre Filters                 |
| auth, logging, rate limit   |
+-----------------------------+
              |
              v
+-----------------------------+
| NettyRoutingFilter          |
| forward to service          |
+-----------------------------+
              |
              v
+-----------------------------+
| Post Filters                |
| log response, add headers   |
+-----------------------------+
```

Key idea:

```text
Filters are chained around the route.
```

Like this:

```text
Filter A before
  Filter B before
    Filter C before
      call downstream
    Filter C after
  Filter B after
Filter A after
```

That is why filter order matters.

---

## 7. Route Predicate Filter Mental Model

A route has three main parts:

```text
id
predicates
filters
uri
```

Example:

```yaml
- id: url-service-route
  uri: lb://URL-SERVICE
  predicates:
    - Path=/api/v1/urls/**
  filters:
    - StripPrefix=2
```

Meaning:

```text
id:
    route name for debugging

predicate:
    when request path matches /api/v1/urls/**

filter:
    remove first 2 path segments

uri:
    forward to URL-SERVICE through load balancer
```

ASCII:

```text
Request: /api/v1/urls
          |
          v
Predicate Path=/api/v1/urls/** ?
          |
          +-- no  -> try next route
          |
          +-- yes -> apply filters
                       |
                       v
                  route to lb://URL-SERVICE
```

Predicates answer:

```text
Should this route handle the request?
```

Filters answer:

```text
What should be done before or after forwarding?
```

URI answers:

```text
Where should the request go?
```

Common predicates:

```text
Path
Method
Host
Header
Query
Cookie
After/Before/Between time windows
```

Common filters:

```text
StripPrefix
RewritePath
AddRequestHeader
AddResponseHeader
RequestRateLimiter
CircuitBreaker
Retry
```

---

## 8. Gateway Project Setup

Create a separate Spring Boot project:

```text
miniurl-gateway
```

Recommended package:

```text
com.miniurl.gateway
```

Maven dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
    </dependency>
</dependencies>
```

Dependency management:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Main class:

```java
package com.miniurl.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MiniUrlGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniUrlGatewayApplication.class, args);
    }
}
```

Basic properties:

```yaml
server:
  port: 8080

spring:
  application:
    name: miniurl-gateway

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,gateway
```

Production rule:

```text
Gateway is its own deployable service.
Do not merge gateway code into url-service.
```

---

## 9. Static Routing Configuration

Start simple before discovery routing.

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: url-service-static
          uri: http://localhost:8081
          predicates:
            - Path=/api/v1/urls/**
          filters:
            - StripPrefix=2
```

Request:

```text
/api/v1/urls
```

With `StripPrefix=2`:

```text
/api/v1/urls
 ^   ^
 1   2
```

Result sent to service:

```text
/urls
```

ASCII:

```text
Client path:
    /api/v1/urls

StripPrefix=2 removes:
    /api/v1

Downstream path:
    /urls
```

Static routing is useful for local testing.

But production with multiple instances prefers discovery:

```text
http://localhost:8081      -> one fixed host
lb://URL-SERVICE           -> discover and load balance instances
```

Static route limitation:

```text
If url-service moves from port 8081 to another host, gateway config must change.
```

---

## 10. Discovery-Based Routing With Eureka

After Eureka is available, route using service names.

Gateway config:

```yaml
server:
  port: 8080

spring:
  application:
    name: miniurl-gateway
  cloud:
    gateway:
      routes:
        - id: url-service-create-api
          uri: lb://URL-SERVICE
          predicates:
            - Path=/api/v1/urls/**
          filters:
            - StripPrefix=2

        - id: url-service-redirect
          uri: lb://URL-SERVICE
          predicates:
            - Path=/r/**
          filters:
            - RewritePath=/r/(?<code>.*), /${code}

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
```

Service must register as:

```yaml
spring:
  application:
    name: url-service
```

Eureka may show service name as uppercase:

```text
URL-SERVICE
```

ASCII:

```text
Gateway route URI:
    lb://URL-SERVICE

Gateway asks:
    Eureka, give me URL-SERVICE instances.

Eureka returns:
    10.0.1.10:8081
    10.0.1.11:8081

Load balancer picks:
    10.0.1.11:8081
```

Important:

```text
lb:// means use Spring Cloud LoadBalancer.
```

Without `lb://`, service discovery is not used.

---

## 11. Gateway For MiniURLShortener

Recommended public paths:

```text
POST /api/v1/urls
GET  /api/v1/urls/{shortCode}
GET  /r/{shortCode}
GET  /api/v1/analytics/{shortCode}
GET  /api/v1/admin/urls
```

Internal service paths can be simpler:

```text
url-service:
    POST /urls
    GET  /urls/{shortCode}
    GET  /{shortCode}       for redirect

analytics-service:
    GET /analytics/{shortCode}

admin-service:
    GET /admin/urls
```

Gateway mapping:

```text
+------------------------------+---------------------+-----------------------+
| Public Path                  | Internal Service    | Internal Path         |
+------------------------------+---------------------+-----------------------+
| /api/v1/urls/**              | URL-SERVICE         | /urls/**              |
| /r/{shortCode}               | URL-SERVICE         | /{shortCode}          |
| /api/v1/analytics/**         | ANALYTICS-SERVICE   | /analytics/**         |
| /api/v1/admin/**             | ADMIN-SERVICE       | /admin/**             |
+------------------------------+---------------------+-----------------------+
```

ASCII:

```text
Public API Contract
      |
      v
+----------------------+
| Gateway              |
| normalize paths      |
+----------------------+
      |
      +--> URL-SERVICE
      +--> ANALYTICS-SERVICE
      +--> ADMIN-SERVICE
```

Why keep public and internal paths separate?

```text
Public API should be stable.
Internal services can evolve.
Gateway absorbs path changes.
```

Example:

```text
Today url-service has POST /urls.
Tomorrow url-service changes to POST /internal/url-records.
Public client can still call POST /api/v1/urls if gateway rewrites correctly.
```

---

## 12. Path Rewriting And Prefix Strategy

Two common strategies:

```text
1. StripPrefix
2. RewritePath
```

### StripPrefix

Use when you simply remove path segments.

```yaml
filters:
  - StripPrefix=2
```

Input:

```text
/api/v1/urls/abc123
```

Output:

```text
/urls/abc123
```

ASCII:

```text
/api/v1/urls/abc123
 |   |
 1   2 removed

=> /urls/abc123
```

### RewritePath

Use when you need regex-based transformation.

```yaml
filters:
  - RewritePath=/r/(?<code>.*), /${code}
```

Input:

```text
/r/abc123
```

Output:

```text
/abc123
```

ASCII:

```text
/r/abc123
   |
   code = abc123
   |
   v
/abc123
```

Common mistake:

```text
Wrong StripPrefix count.
```

Example wrong config:

```yaml
Path=/api/v1/urls/**
StripPrefix=1
```

Input:

```text
/api/v1/urls
```

Sent to service:

```text
/v1/urls
```

If service expects `/urls`, it returns 404.

Debug rule:

```text
When gateway returns unexpected 404, check the downstream path after rewrite.
```

---

## 13. Global Filters

A global filter runs for all routes.

Use global filters for cross-cutting edge behavior:

```text
correlation ID
request logging
response time logging
security headers
basic request validation
```

Do not use global filters for service-specific business rules.

Wrong:

```text
Gateway filter checks whether short code is expired.
```

Correct:

```text
url-service checks whether short code is expired.
```

Filter lifecycle:

```text
Pre-filter code runs before downstream call.
Post-filter code runs after downstream response.
```

Java example:

```java
package com.miniurl.gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RequestTimingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(
            org.springframework.web.server.ServerWebExchange exchange,
            org.springframework.cloud.gateway.filter.GatewayFilterChain chain
    ) {
        long start = System.currentTimeMillis();

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    long durationMs = System.currentTimeMillis() - start;
                    String path = exchange.getRequest().getURI().getPath();
                    int status = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : 0;

                    System.out.println("path=" + path
                            + " status=" + status
                            + " durationMs=" + durationMs);
                }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
```

Important reactive idea:

```text
Do not block inside gateway filters.
```

Avoid:

```java
Thread.sleep(1000);
blockingJdbcCall();
restTemplate.getForObject(...);
```

Gateway runs on event-loop style processing.
Blocking code can damage throughput.

---

## 14. Correlation ID Filter

Correlation ID connects logs across services.

Without it:

```text
Gateway log says request failed.
url-service log says DB timeout.
analytics-service log says Kafka publish failed.
No easy way to connect them.
```

With it:

```text
X-Correlation-Id: 8f1a-222
```

Every service log includes the same ID.

ASCII:

```text
Client
  |
  | X-Correlation-Id: abc
  v
Gateway log: abc
  |
  v
URL service log: abc
  |
  v
DB/Kafka logs can be linked by app log context
```

Gateway filter:

```java
package com.miniurl.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest()
                .getHeaders()
                .getFirst(CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header(CORRELATION_ID_HEADER, correlationId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        mutatedExchange.getResponse()
                .getHeaders()
                .add(CORRELATION_ID_HEADER, correlationId);

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
```

Why add it to response too?

```text
Client can report: request X-Correlation-Id failed.
Engineer can search logs by that ID.
```

Production improvement:

```text
Validate maximum header length.
Avoid accepting huge user-provided correlation IDs.
```

---

## 15. Authentication Boundary

Gateway is a good place for edge authentication.

Common setup:

```text
Gateway validates JWT.
Gateway forwards identity headers to internal services.
Services still enforce authorization for sensitive actions.
```

ASCII:

```text
Client
  |
  | Authorization: Bearer JWT
  v
+----------------------+
| Gateway              |
| verify token         |
| extract userId/roles |
+----------------------+
  |
  | X-User-Id: 123
  | X-Roles: USER
  v
+----------------------+
| Internal Service     |
| business auth checks |
+----------------------+
```

Important distinction:

```text
Authentication = who are you?
Authorization = are you allowed to do this?
```

Gateway can authenticate the token.

But service should still protect business rules:

```text
User can update only their own links.
Admin endpoint requires ADMIN role.
Analytics may require owner or admin.
```

Do not blindly trust user-supplied identity headers.

Gateway should remove incoming spoofed headers:

```text
X-User-Id
X-Roles
X-Email
```

Then gateway re-adds trusted versions after JWT validation.

ASCII:

```text
Client sends fake X-User-Id: admin
          |
          v
Gateway removes fake identity headers
          |
          v
Gateway validates JWT
          |
          v
Gateway adds trusted X-User-Id from token
```

Security rule:

```text
Only gateway should be publicly reachable.
Internal services should not be reachable directly from the internet.
```

---

## 16. Rate Limiting At Gateway

Rate limiting is often placed at the gateway because all traffic passes through it.

Goal:

```text
Protect backend services from abusive clients.
```

Example policies:

```text
anonymous IP: 30 requests/minute
logged-in user: 300 requests/minute
admin: separate policy
redirect endpoint: high but protected from bot storms
create endpoint: lower because writes are expensive
```

Spring Cloud Gateway supports Redis-backed rate limiting.

Config example:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: url-service-create-api
          uri: lb://URL-SERVICE
          predicates:
            - Path=/api/v1/urls/**
          filters:
            - StripPrefix=2
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
                key-resolver: "#{@ipKeyResolver}"
```

Key resolver:

```java
package com.miniurl.gateway.ratelimit;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest()
                    .getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";

            return Mono.just(ip);
        };
    }
}
```

ASCII token bucket:

```text
Bucket capacity = 20
Refill rate     = 10 tokens/sec

Request arrives:
    token available -> allow
    no token        -> 429 Too Many Requests
```

Gateway response when limited:

```text
HTTP 429 Too Many Requests
```

Production warning:

```text
IP-based limiting behind proxies needs trusted X-Forwarded-For handling.
```

If you use IP blindly, all users behind one NAT may share one bucket.

Better later:

```text
authenticated user ID key
API key key
IP + route key
```

---

## 17. Resilience Timeout Retry Circuit Breaker

Gateway should fail fast when downstream services are broken.

Important tools:

```text
timeout
retry
circuit breaker
fallback
```

### Timeout

Timeout prevents hanging requests.

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 1000
        response-timeout: 3s
```

Meaning:

```text
connect-timeout: cannot connect quickly -> fail
response-timeout: downstream too slow -> fail
```

### Retry

Retry can help transient failures.

But retries are dangerous for writes.

Safe-ish:

```text
GET /r/{shortCode}
GET /api/v1/urls/{id}
```

Dangerous:

```text
POST /api/v1/urls
POST /api/v1/payment
```

Unless idempotency key exists.

Example:

```yaml
filters:
  - name: Retry
    args:
      retries: 2
      statuses: BAD_GATEWAY,SERVICE_UNAVAILABLE,GATEWAY_TIMEOUT
      methods: GET
```

### Circuit Breaker

Circuit breaker prevents repeatedly calling broken service.

```text
Closed:
    calls pass normally

Open:
    calls fail fast

Half-open:
    test a few calls
```

ASCII:

```text
many failures
Closed -------------> Open
  ^                    |
  |                    | wait
  |                    v
  +---------------- Half-open
        success -> close
        fail    -> open
```

Gateway resilience rule:

```text
Fail fast at the edge, but do not hide every backend failure with fake success.
```

For URL shortener:

```text
Redirect endpoint should be highly available.
Create endpoint should avoid unsafe retries unless idempotent.
```

---

## 18. CORS And Browser Boundary

CORS matters when browser frontend and API domain differ.

Example:

```text
Frontend: https://app.miniurl.com
API:      https://api.miniurl.com
```

Browser asks:

```text
Is app.miniurl.com allowed to call api.miniurl.com?
```

Gateway can centralize CORS.

Example:

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins:
              - "https://app.miniurl.com"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
            allowedHeaders:
              - Authorization
              - Content-Type
              - X-Correlation-Id
            exposedHeaders:
              - X-Correlation-Id
            allowCredentials: true
```

ASCII:

```text
Browser
  |
  | OPTIONS preflight
  v
Gateway CORS config
  |
  +-- allowed -> browser sends real request
  +-- denied  -> browser blocks request
```

Common mistake:

```text
allowedOrigins: "*" with credentials enabled
```

This is unsafe and often invalid.

Production rule:

```text
Be explicit with allowed origins.
```

---

## 19. Error Handling At Gateway

Gateway errors are different from service errors.

Service error:

```text
url-service returns 400 INVALID_URL
```

Gateway should pass it through unless you intentionally standardize edge errors.

Gateway error examples:

```text
No route matched -> 404
Rate limit exceeded -> 429
Downstream unavailable -> 503
Gateway timeout -> 504
JWT invalid -> 401
Forbidden route -> 403
```

ASCII:

```text
Request
  |
  v
Gateway
  |
  +-- no route --------> 404
  +-- rate limited ----> 429
  +-- auth failed -----> 401
  +-- service down ----> 503/504
  |
  v
Service
  |
  +-- business error --> 400/404/409/410
```

Gateway should not expose:

```text
Java stack traces
internal hostnames
Netty exception details
Eureka internals
```

Recommended edge error shape:

```json
{
  "timestamp": "2026-06-24T10:00:00Z",
  "status": 503,
  "error": "Service Unavailable",
  "code": "DOWNSTREAM_SERVICE_UNAVAILABLE",
  "message": "Service temporarily unavailable",
  "path": "/api/v1/urls"
}
```

Production idea:

```text
Keep gateway error contract similar to service ApiErrorResponse.
```

This makes clients simpler.

---

## 20. Observability And Access Logs

Gateway is an excellent place to measure edge traffic.

Log fields:

```text
correlationId
method
path
routeId
status
durationMs
clientIp
userAgent
userId if authenticated
downstreamService
```

Metrics:

```text
request count by route
latency by route
4xx rate
5xx rate
429 rate
downstream timeout count
circuit breaker open count
```

ASCII:

```text
Gateway sees every request
          |
          v
+----------------------+
| Edge metrics         |
| route latency        |
| error rate           |
| traffic volume       |
+----------------------+
```

Actuator useful endpoints:

```text
/actuator/health
/actuator/metrics
/actuator/gateway/routes
/actuator/prometheus
```

Enable gateway actuator endpoint:

```yaml
management:
  endpoint:
    gateway:
      enabled: true
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,gateway
```

Debug command:

```bash
curl http://localhost:8080/actuator/gateway/routes
```

What to inspect:

```text
Is route loaded?
Is predicate correct?
Is filter attached?
Is URI correct?
```

---

## 21. Step-by-Step Dry Runs

### Dry Run 1: Create short URL

Request:

```http
POST /api/v1/urls
Content-Type: application/json

{
  "longUrl": "https://example.com"
}
```

Flow:

```text
1. Gateway receives POST /api/v1/urls.
2. Route predicate Path=/api/v1/urls/** matches.
3. CorrelationIdFilter adds X-Correlation-Id.
4. Rate limiter checks IP/user bucket.
5. StripPrefix=2 converts /api/v1/urls to /urls.
6. Gateway uses lb://URL-SERVICE.
7. Load balancer selects one url-service instance.
8. url-service handles POST /urls.
9. Response returns through gateway.
10. Gateway adds response correlation ID.
```

ASCII:

```text
POST /api/v1/urls
        |
        v
Gateway route match
        |
        v
StripPrefix=2
        |
        v
POST /urls -> URL-SERVICE
```

---

### Dry Run 2: Redirect short code

Request:

```http
GET /r/abc123
```

Flow:

```text
1. Gateway receives /r/abc123.
2. Redirect route predicate Path=/r/** matches.
3. RewritePath extracts abc123.
4. Path becomes /abc123.
5. Gateway forwards to URL-SERVICE.
6. url-service returns 302 Location: https://example.com.
7. Gateway passes response to client.
```

ASCII:

```text
/r/abc123
   |
   v
RewritePath
   |
   v
/abc123
   |
   v
URL-SERVICE redirect controller
```

---

### Dry Run 3: Unknown route

Request:

```http
GET /unknown/path
```

Flow:

```text
1. Gateway receives request.
2. No route predicate matches.
3. Gateway returns 404.
4. No internal service is called.
```

Debug:

```text
Check route predicates.
Check public path.
Check actuator gateway routes.
```

---

### Dry Run 4: Service unavailable

Situation:

```text
URL-SERVICE has no healthy instances in Eureka.
```

Request:

```http
POST /api/v1/urls
```

Flow:

```text
1. Gateway route matches.
2. Gateway asks load balancer for URL-SERVICE instance.
3. No instance available.
4. Gateway returns 503.
5. Client sees service unavailable.
```

Production action:

```text
Check Eureka registration.
Check url-service health.
Check service name mismatch.
```

---

### Dry Run 5: Rate limit exceeded

Policy:

```text
10 requests/second, burst 20
```

Client sends:

```text
100 create requests quickly
```

Flow:

```text
1. First requests consume bucket tokens.
2. Bucket becomes empty.
3. RequestRateLimiter rejects extra requests.
4. Gateway returns 429.
5. url-service is protected from traffic spike.
```

ASCII:

```text
Client burst
   |
   v
Gateway token bucket
   |
   +-- token yes -> URL-SERVICE
   +-- token no  -> 429
```

---

## 22. Internal Execution Walkthrough

Detailed route execution:

```text
1. Netty receives TCP/HTTP request.
2. Spring WebFlux creates ServerWebExchange.
3. RoutePredicateHandlerMapping checks configured routes.
4. First matching route is selected.
5. Gateway builds combined filter chain.
6. Global filters and route filters execute in order.
7. Path may be mutated.
8. Headers may be added or removed.
9. Load balancer resolves lb:// service URI.
10. NettyRoutingFilter sends request to downstream service.
11. Downstream service response returns.
12. Post-filter logic executes.
13. Gateway writes response to client.
```

ASCII:

```text
Netty Server
    |
    v
ServerWebExchange
    |
    v
RoutePredicateHandlerMapping
    |
    v
Matching Route
    |
    v
Global Filters + Route Filters
    |
    v
LoadBalancerClientFilter
    |
    v
NettyRoutingFilter
    |
    v
Downstream Service
    |
    v
Response Filters
    |
    v
Client
```

Important internal idea:

```text
The request object is immutable-ish.
To change headers/path, you mutate and create a new exchange/request.
```

Example:

```java
ServerHttpRequest mutatedRequest = exchange.getRequest()
        .mutate()
        .header("X-Correlation-Id", correlationId)
        .build();
```

Then:

```java
ServerWebExchange mutatedExchange = exchange.mutate()
        .request(mutatedRequest)
        .build();
```

This is normal in reactive gateway code.

---

## 23. Production Failure Stories

### Failure Story 1: Wrong StripPrefix caused all APIs to return 404

Config:

```yaml
Path=/api/v1/urls/**
StripPrefix=1
```

Client called:

```text
/api/v1/urls
```

Gateway sent:

```text
/v1/urls
```

url-service expected:

```text
/urls
```

Result:

```text
404 from url-service
```

Fix:

```yaml
StripPrefix=2
```

Lesson:

```text
Always know the exact downstream path after rewrite.
```

---

### Failure Story 2: Gateway route used wrong service name

Gateway:

```yaml
uri: lb://URLSERVICE
```

Eureka registered:

```text
URL-SERVICE
```

Result:

```text
503 Service Unavailable
```

Root cause:

```text
Service name mismatch.
```

Fix:

```yaml
uri: lb://URL-SERVICE
```

Lesson:

```text
When lb:// route fails, compare gateway URI with Eureka registry name.
```

---

### Failure Story 3: Direct service exposure bypassed gateway security

Gateway checked JWT.

But url-service was still publicly reachable:

```text
https://url-service.company.com/urls
```

Attackers called service directly and bypassed gateway filters.

Fix:

```text
Only expose gateway publicly.
Keep services private inside VPC/Kubernetes cluster.
Use network policies/security groups.
```

Lesson:

```text
Gateway security is useless if clients can bypass the gateway.
```

---

### Failure Story 4: Retry storm made outage worse

Gateway retried every failed request three times.

Downstream database was slow.

Original traffic:

```text
1,000 RPS
```

With retries:

```text
up to 4,000 backend attempts/sec
```

Result:

```text
Downstream collapsed harder.
```

Fix:

```text
Use small bounded retries only for safe methods.
Use timeout and circuit breaker.
Do not retry non-idempotent writes blindly.
```

Lesson:

```text
Retries are load multipliers.
```

---

### Failure Story 5: Blocking call inside global filter damaged throughput

A global filter called an external user service synchronously for every request.

Result:

```text
Gateway event loop blocked.
Latency increased.
Throughput collapsed.
```

Fix:

```text
Use non-blocking reactive clients or move heavy checks to services.
Cache small edge metadata if needed.
```

Lesson:

```text
Gateway must remain lightweight and non-blocking.
```

---

## 24. Debugging Mindset

When gateway routing fails, ask:

```text
1. Did the request reach gateway?
2. Did any route predicate match?
3. Which route ID matched?
4. What path was sent downstream after filters?
5. Is the target service registered in Eureka?
6. Are there healthy instances?
7. Did load balancer choose an instance?
8. Did downstream return the error or gateway return it?
9. Is the failure 404, 429, 503, or 504?
10. Is correlation ID present in gateway and service logs?
```

Status debugging map:

```text
404 from gateway:
    no route matched

404 from service:
    route matched, but path rewrite may be wrong

429:
    rate limiter rejected request

401:
    authentication failed

403:
    authorization/filter rejected request

503:
    no downstream instance or service unavailable

504:
    downstream timeout

500:
    gateway bug or unexpected filter exception
```

Commands:

```bash
curl -i http://localhost:8080/api/v1/urls
curl http://localhost:8080/actuator/gateway/routes
curl http://localhost:8761
```

Log fields to check:

```text
routeId
pathBeforeRewrite
pathAfterRewrite
serviceId
status
durationMs
correlationId
```

Golden rule:

```text
First identify whether the error came from gateway or downstream service.
```

---

## 25. Common Mistakes

### Mistake 1: Putting business logic in gateway

Wrong:

```text
Gateway checks if short code is expired.
```

Correct:

```text
url-service checks expiry.
Gateway routes request.
```

### Mistake 2: Exposing internal services publicly

Wrong:

```text
Gateway is public and url-service is also public.
```

Correct:

```text
Only gateway is public.
Services are private.
```

### Mistake 3: Wrong path rewrite

Wrong:

```text
Public path and internal path are not clearly mapped.
```

Correct:

```text
Maintain a route table with input path, filters, and downstream path.
```

### Mistake 4: Retrying unsafe writes

Wrong:

```text
Retry POST /api/v1/urls without idempotency.
```

Correct:

```text
Retry safe GETs carefully. Use idempotency keys for writes before retrying.
```

### Mistake 5: Blocking inside reactive gateway

Wrong:

```text
JDBC call, Thread.sleep, RestTemplate call inside GlobalFilter.
```

Correct:

```text
Keep filters lightweight and non-blocking.
```

### Mistake 6: Trusting spoofed identity headers

Wrong:

```text
Service trusts X-User-Id from internet.
```

Correct:

```text
Gateway strips spoofed headers and adds trusted identity after JWT validation.
```

### Mistake 7: No route observability

Wrong:

```text
No route ID in logs.
```

Correct:

```text
Log routeId, status, latency, correlationId.
```

### Mistake 8: Using wildcard CORS carelessly

Wrong:

```text
Allow all origins in production.
```

Correct:

```text
Allow only known frontend origins.
```

---

## 26. Interview-Ready Explanation

If interviewer asks:

```text
Why do you need Spring Cloud Gateway in your microservice system?
```

Strong answer:

```text
Spring Cloud Gateway acts as the programmable front door for the system. Instead
of exposing every internal service to clients, clients call one public API domain.
The gateway matches requests using route predicates, applies filters such as
correlation ID, authentication, CORS, rate limiting, path rewrite, timeouts, and
then routes traffic to the correct service. With Eureka integration, the gateway
can use lb://SERVICE-NAME so it does not need fixed hostnames; Spring Cloud
LoadBalancer chooses a healthy instance from service discovery. I keep business
logic inside services and use the gateway only for edge concerns. In production,
I make services private so the gateway cannot be bypassed, keep filters
non-blocking, use bounded retries only for safe methods, expose actuator route
information, and log route ID, latency, status, and correlation ID for debugging.
```

Why this is strong:

```text
1. Explains gateway purpose.
2. Separates gateway from business logic.
3. Mentions predicates, filters, and routing.
4. Connects gateway with Eureka and load balancing.
5. Includes security boundary.
6. Includes resilience.
7. Includes observability.
8. Shows production debugging mindset.
```

Senior one-liner:

```text
Gateway centralizes edge policy and routing, while services remain private and own business behavior.
```

---

## 27. Senior Engineer Checklist

Before calling gateway production-shaped, confirm:

```text
[ ] Gateway is a separate Spring Boot service
[ ] Only gateway is publicly exposed
[ ] Internal services are private
[ ] Routes have clear IDs
[ ] Public path to internal path mapping is documented
[ ] /api/v1/urls/** routes to URL-SERVICE
[ ] /r/** routes to URL-SERVICE redirect endpoint
[ ] lb:// service names match Eureka names
[ ] StripPrefix and RewritePath are tested
[ ] Correlation ID is propagated to downstream services
[ ] Gateway logs routeId, status, latency, path, correlationId
[ ] CORS allows only known frontend origins
[ ] Rate limiting protects write and redirect endpoints
[ ] Auth boundary removes spoofed identity headers
[ ] Gateway filters do not contain business logic
[ ] Gateway filters do not block event loop
[ ] Timeouts are configured
[ ] Retries are bounded and only for safe/idempotent calls
[ ] 503 and 504 behavior is understood
[ ] /actuator/gateway/routes is available in safe environments
[ ] Gateway metrics are exported
```

If these are checked, your gateway model is interview-ready and production-shaped.

---

## 28. One-Page Cheat Sheet

```text
Core mental model:
Gateway = programmable front door.

Three gateway steps:
1. Predicate matches request.
2. Filters apply edge behavior.
3. URI routes to service.

Gateway vs discovery vs load balancer:
Gateway chooses service.
Discovery tells service instances.
Load balancer chooses instance.

Route shape:
id
predicates
filters
uri

Common predicates:
Path
Method
Host
Header
Query

Common filters:
StripPrefix
RewritePath
AddRequestHeader
RequestRateLimiter
Retry
CircuitBreaker

MiniURLShortener routes:
/api/v1/urls/**      -> URL-SERVICE
/r/**                -> URL-SERVICE
/api/v1/analytics/** -> ANALYTICS-SERVICE later
/api/v1/admin/**     -> ADMIN-SERVICE later

Important configs:
lb://URL-SERVICE means use service discovery + load balancer.
StripPrefix removes path segments.
RewritePath transforms path using regex.

Edge concerns:
correlation ID
authentication
rate limiting
CORS
timeout
basic resilience
logging

Do not put here:
short code generation
expiry business logic
analytics aggregation
DB transaction logic

Status map:
404 gateway = no route matched
404 service = route matched but service/path failed
429 = rate limited
503 = no service/downstream unavailable
504 = timeout

Production rules:
Only gateway public.
Services private.
No blocking code in filters.
No unsafe retries for writes.
Log routeId and correlationId.
```

---

## 29. One Picture To Remember

```text
                     SPRING CLOUD GATEWAY MENTAL MODEL

                         "Programmable Front Door"

External Clients
Browser / Mobile / Partner API
        |
        v
+-------------------------------------------------------------+
|                    SPRING CLOUD GATEWAY                     |
|                                                             |
|  1. MATCH                                                  |
|     Path=/api/v1/urls/**                                   |
|     Path=/r/**                                             |
|                                                             |
|  2. FILTER                                                 |
|     Correlation ID                                         |
|     JWT Auth                                               |
|     Rate Limit                                             |
|     CORS                                                   |
|     Timeout                                                |
|     Rewrite Path                                           |
|                                                             |
|  3. ROUTE                                                  |
|     lb://URL-SERVICE                                       |
|     lb://ANALYTICS-SERVICE                                 |
|     lb://ADMIN-SERVICE                                     |
+-------------------------------------------------------------+
        |
        v
+-------------------------+        +--------------------------+
| Eureka Service Registry | <----> | Spring Cloud LoadBalancer|
| service phonebook       |        | chooses instance         |
+-------------------------+        +--------------------------+
        |
        v
+------------------+   +---------------------+   +---------------+
| URL-SERVICE      |   | ANALYTICS-SERVICE   |   | ADMIN-SERVICE |
| business logic   |   | click stats         |   | admin actions |
+------------------+   +---------------------+   +---------------+

FINAL MEMORY:

Gateway controls how traffic enters.
Eureka tells where services live.
Load balancer chooses an instance.
Services own business logic.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Spring Cloud Gateway is the programmable front door of a microservice system.
2. A route is built from predicates, filters, and a target URI.
3. Gateway handles edge concerns like auth, CORS, rate limiting, correlation IDs, and path rewriting.
4. Eureka plus lb://SERVICE-NAME lets the gateway route without hardcoded service instance addresses.
5. Keep business logic inside services; keep the gateway lightweight, stateless, observable, and non-blocking.
```

After this chapter, MiniURLShortener has a clean public entry layer:

```text
041 Eureka Service Discovery
042 Spring Cloud Gateway
```

Next recommended chapters:

```text
043_Gateway_Auth_Integration.md
044_Gateway_Rate_Limiting.md
045_Distributed_Tracing_Correlation.md
046_Docker_Compose_Microservices_Runbook.md
```
