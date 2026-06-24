# 041_Eureka_Service_Discovery.md
# MiniURLShortener / MiniSpringCloud — Eureka Service Discovery

> Core mental model: **Eureka is the dynamic phonebook of a microservice system. Services do not hardcode each other’s host and port; they register themselves, keep their address alive through heartbeats, and clients discover healthy instances by service name.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Without Service Discovery](#4-without-service-discovery)
- [5. With Eureka Service Discovery](#5-with-eureka-service-discovery)
- [6. Eureka Components](#6-eureka-components)
- [7. Service Registration Flow](#7-service-registration-flow)
- [8. Service Discovery Flow](#8-service-discovery-flow)
- [9. Heartbeat And Lease Mental Model](#9-heartbeat-and-lease-mental-model)
- [10. Eureka Server Setup](#10-eureka-server-setup)
- [11. URL Service Eureka Client Setup](#11-url-service-eureka-client-setup)
- [12. Gateway Discovery Setup](#12-gateway-discovery-setup)
- [13. Calling Services By Name](#13-calling-services-by-name)
- [14. Load Balancing Mental Model](#14-load-balancing-mental-model)
- [15. Eureka Self-Preservation](#15-eureka-self-preservation)
- [16. Failure Modes](#16-failure-modes)
- [17. Local Docker Compose Setup](#17-local-docker-compose-setup)
- [18. Kubernetes Reality Check](#18-kubernetes-reality-check)
- [19. Step-by-Step Dry Runs](#19-step-by-step-dry-runs)
- [20. Internal Execution Walkthrough](#20-internal-execution-walkthrough)
- [21. Production Failure Stories](#21-production-failure-stories)
- [22. Debugging Mindset](#22-debugging-mindset)
- [23. Common Mistakes](#23-common-mistakes)
- [24. Testing Strategy](#24-testing-strategy)
- [25. Interview-Ready Explanation](#25-interview-ready-explanation)
- [26. Senior Engineer Checklist](#26-senior-engineer-checklist)
- [27. One-Page Cheat Sheet](#27-one-page-cheat-sheet)
- [28. One Picture To Remember](#28-one-picture-to-remember)

---

## 1. Why This Exists

In early MiniURLShortener, everything may run as one Spring Boot application:

```text
Client -> MiniURLShortener App -> PostgreSQL / Redis / Kafka
```

That is simple.

But production systems usually split responsibilities:

```text
api-gateway-service
url-service
redirect-service
analytics-service
auth-service
notification-service
admin-service
```

Now a question appears:

```text
How does api-gateway-service know where url-service is running?
```

Hardcoding is easy at first:

```yaml
url-service:
  base-url: http://localhost:8081
```

But production is dynamic:

```text
url-service instance 1: 10.0.1.11:8080
url-service instance 2: 10.0.2.19:8080
url-service instance 3: 10.0.3.21:8080
```

Instances restart.
Instances scale up.
Instances scale down.
Ports can change.
Containers get new IPs.
Deployments roll gradually.

Without discovery, every service needs to know every other service address manually.
That becomes fragile.

Eureka solves this by acting like a dynamic service phonebook:

```text
Service asks:
    Where is URL-SERVICE?

Eureka answers:
    URL-SERVICE has these live instances:
        10.0.1.11:8080
        10.0.2.19:8080
        10.0.3.21:8080
```

Production memory:

```text
Do not hardcode moving service addresses.
Register services once, discover them by name.
```

---

## 2. The One Core Mental Model

Eureka is a:

```text
DYNAMIC PHONEBOOK FOR SERVICES
```

Normal phonebook:

```text
Mohamed -> +40 xxx xxx xxx
```

Service phonebook:

```text
URL-SERVICE -> 10.0.1.11:8080, 10.0.2.19:8080
REDIRECT-SERVICE -> 10.0.4.15:8080, 10.0.4.16:8080
ANALYTICS-SERVICE -> 10.0.8.20:8080
```

ASCII:

```text
                +----------------------+
                |   EUREKA SERVER      |
                | dynamic phonebook    |
                +----------+-----------+
                           ^
                           |
        register + heartbeat
                           |
+--------------------------+--------------------------+
|                          |                          |
v                          v                          v
URL-SERVICE             REDIRECT-SERVICE           ANALYTICS-SERVICE
10.0.1.11:8080          10.0.2.15:8080             10.0.3.20:8080
10.0.1.12:8080          10.0.2.16:8080

Client asks Eureka:
    Give me instances of URL-SERVICE
```

One-line memory:

```text
Eureka converts unstable IP addresses into stable service names.
```

In MiniURLShortener:

```text
api-gateway routes to URL-SERVICE
redirect-service may call URL-SERVICE
analytics-worker may call ADMIN-SERVICE
auth-service may validate users for URL-SERVICE
```

Instead of:

```text
http://10.0.1.11:8080/api/v1/urls
```

Use:

```text
http://URL-SERVICE/api/v1/urls
```

The actual instance is selected by the discovery client and load balancer.

---

## 3. Problem Statement

Build service discovery for MiniURLShortener using Netflix Eureka and Spring Cloud.

We want this architecture:

```text
Client
  |
  v
API Gateway
  |
  +-- lb://URL-SERVICE
  |
  +-- lb://REDIRECT-SERVICE
  |
  +-- lb://ANALYTICS-SERVICE
  |
  v
Eureka Registry knows all live instances
```

Requirements:

```text
1. Create Eureka Server.
2. Register URL service with Eureka.
3. Register API Gateway with Eureka.
4. Let Gateway route using service name.
5. Understand heartbeat and lease expiration.
6. Understand client-side load balancing.
7. Understand common Eureka failures.
8. Add debugging commands and production checklist.
```

Out of scope:

```text
1. Full Spring Cloud Config.
2. Kubernetes-native service discovery replacement.
3. Service mesh.
4. Advanced multi-region Eureka.
5. Consul/ZooKeeper comparison in depth.
```

---

## 4. Without Service Discovery

Imagine three URL service instances:

```text
url-service-1 -> 10.0.1.11:8080
url-service-2 -> 10.0.1.12:8080
url-service-3 -> 10.0.1.13:8080
```

Gateway config without discovery:

```yaml
url-service-instances:
  - http://10.0.1.11:8080
  - http://10.0.1.12:8080
  - http://10.0.1.13:8080
```

What breaks?

```text
1. New instance starts -> config must be updated.
2. Old instance dies -> gateway may still call dead IP.
3. Rolling deployment -> addresses keep changing.
4. Autoscaling -> impossible to manually track.
5. Multiple environments -> dev/stage/prod all need different config.
```

ASCII failure:

```text
Gateway hardcoded list:

+---------+
|Gateway  |
+----+----+
     |
     +----> 10.0.1.11:8080 alive
     |
     +----> 10.0.1.12:8080 DEAD  X
     |
     +----> 10.0.1.13:8080 alive

Problem:
Gateway does not automatically know 10.0.1.12 is dead.
```

Operational pain:

```text
Every deployment becomes a coordination problem.
Every scale event becomes a config problem.
Every failure becomes a routing problem.
```

Senior engineer rule:

```text
If service instances are dynamic, service addresses must be dynamic too.
```

---

## 5. With Eureka Service Discovery

With Eureka:

```text
Each service registers itself when it starts.
Each service sends heartbeat periodically.
Eureka keeps the registry of live instances.
Clients ask Eureka for service instances.
```

ASCII:

```text
Step 1: Register

URL-SERVICE instance starts
        |
        v
POST register to Eureka
        |
        v
Eureka stores:
URL-SERVICE -> 10.0.1.11:8080

Step 2: Discover

Gateway wants URL-SERVICE
        |
        v
Ask Eureka / local registry cache
        |
        v
Pick one instance
        |
        v
Call URL-SERVICE
```

Dynamic scaling:

```text
Before scale:
URL-SERVICE -> 10.0.1.11:8080

After scale:
URL-SERVICE -> 10.0.1.11:8080
URL-SERVICE -> 10.0.1.12:8080
URL-SERVICE -> 10.0.1.13:8080
```

No gateway config change needed.

Gateway can route using logical name:

```yaml
uri: lb://URL-SERVICE
```

Meaning:

```text
lb:// means use discovery + load balancer.
URL-SERVICE means service id registered in Eureka.
```

---

## 6. Eureka Components

Eureka has three important pieces:

```text
1. Eureka Server
2. Eureka Client
3. Service Registry
```

### Eureka Server

The registry server.

It stores:

```text
service name
instance id
host
port
health metadata
lease information
last heartbeat time
```

### Eureka Client

A Spring Boot service with Eureka client dependency.

It does two things:

```text
register itself with Eureka
fetch registry from Eureka
```

### Service Registry

The in-memory catalog of service instances.

Example:

```text
+-------------------+----------------------+----------+
| Service Name      | Instance             | Status   |
+-------------------+----------------------+----------+
| URL-SERVICE       | 10.0.1.11:8080       | UP       |
| URL-SERVICE       | 10.0.1.12:8080       | UP       |
| REDIRECT-SERVICE  | 10.0.2.20:8080       | UP       |
| API-GATEWAY       | 10.0.3.10:8080       | UP       |
+-------------------+----------------------+----------+
```

ASCII:

```text
+----------------------+       register/fetch       +----------------------+
| URL-SERVICE          | <-------------------------> | EUREKA SERVER        |
| Eureka Client        |                            | Registry             |
+----------------------+                            +----------------------+

+----------------------+       register/fetch       +----------------------+
| API-GATEWAY          | <-------------------------> | EUREKA SERVER        |
| Eureka Client        |                            | Registry             |
+----------------------+                            +----------------------+
```

---

## 7. Service Registration Flow

When `url-service` starts:

```text
1. Spring Boot starts application context.
2. Eureka client reads application name, host, port.
3. Eureka client sends registration request to Eureka Server.
4. Eureka Server stores instance in registry.
5. Service appears in Eureka dashboard.
6. Service keeps sending heartbeat.
```

ASCII:

```text
URL-SERVICE startup
      |
      v
Read config:
spring.application.name=URL-SERVICE
server.port=8081
      |
      v
Register with Eureka:
URL-SERVICE / host / port / status
      |
      v
Eureka registry:
URL-SERVICE -> localhost:8081
```

Important config:

```yaml
spring:
  application:
    name: URL-SERVICE

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

The application name becomes the logical service name.

Bad name:

```yaml
spring.application.name=url-service-local-test-v2
```

Better name:

```yaml
spring.application.name=URL-SERVICE
```

Why?

Because gateway routes depend on service id.

---

## 8. Service Discovery Flow

When Gateway calls URL service:

```text
1. Gateway receives external HTTP request.
2. Route matches /api/v1/urls/**.
3. Gateway sees uri lb://URL-SERVICE.
4. Discovery client finds URL-SERVICE instances.
5. Load balancer chooses one instance.
6. Gateway forwards request.
```

ASCII:

```text
Client
  |
  v
GET /api/v1/urls
  |
  v
+------------------+
| API Gateway      |
| route:           |
| lb://URL-SERVICE |
+--------+---------+
         |
         v
Discovery lookup:
URL-SERVICE -> [A, B, C]
         |
         v
Load balancer chooses B
         |
         v
http://10.0.1.12:8080/api/v1/urls
```

Logical URL:

```text
lb://URL-SERVICE
```

Physical URL after resolution:

```text
http://10.0.1.12:8080
```

Memory:

```text
Developers configure service names.
Runtime chooses real instances.
```

---

## 9. Heartbeat And Lease Mental Model

Registration alone is not enough.

Eureka must know whether an instance is still alive.

That is why clients send heartbeats.

Mental model:

```text
Lease = temporary membership in registry.
Heartbeat = renewal signal.
```

ASCII:

```text
URL-SERVICE instance
      |
      | heartbeat every N seconds
      v
Eureka Server
      |
      | renew lease
      v
Keep instance marked UP
```

If heartbeat stops:

```text
URL-SERVICE dies
      |
      v
No more heartbeat
      |
      v
Lease expires after timeout
      |
      v
Eureka removes instance from registry
```

Table:

```text
+-----------------------+--------------------------------+
| Concept               | Meaning                        |
+-----------------------+--------------------------------+
| registration          | service joins phonebook        |
| heartbeat             | service says I am still alive  |
| lease renewal         | registry extends membership    |
| lease expiration      | registry removes stale member  |
| eviction              | Eureka deletes dead instance   |
+-----------------------+--------------------------------+
```

Important:

```text
Eureka is eventually consistent.
```

That means:

```text
A dead instance may remain visible briefly.
A new instance may take some time to appear in all clients.
```

Design implication:

```text
Use timeouts, retries, circuit breakers, and health checks.
Do not assume discovery is instant perfection.
```

---

## 10. Eureka Server Setup

Create module:

```text
eureka-server
```

Dependencies:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

Application class:

```java
package com.miniurl.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

`application.yml`:

```yaml
server:
  port: 8761

spring:
  application:
    name: EUREKA-SERVER

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://localhost:8761/eureka/
  server:
    enable-self-preservation: true
```

Why server does not register with itself in simple local setup:

```yaml
register-with-eureka: false
fetch-registry: false
```

Because a single local Eureka server is the registry itself.
It does not need to discover others.

Start it:

```bash
mvn spring-boot:run
```

Open dashboard:

```text
http://localhost:8761
```

Expected first view:

```text
No instances available
```

That is correct before clients start.

---

## 11. URL Service Eureka Client Setup

In `url-service`, add dependency:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

Application class:

```java
package com.miniurl.urlservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UrlServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlServiceApplication.class, args);
    }
}
```

In modern Spring Cloud, the annotation is often optional if dependency is present.
But keeping it improves readability for learning.

`application.yml`:

```yaml
server:
  port: 8081

spring:
  application:
    name: URL-SERVICE

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${server.port}
```

After start, Eureka dashboard should show:

```text
Application     AMIs     Availability Zones     Status
URL-SERVICE     n/a      n/a                    UP (1)
```

If running multiple instances locally:

```bash
java -jar url-service.jar --server.port=8081
java -jar url-service.jar --server.port=8082
java -jar url-service.jar --server.port=8083
```

Dashboard:

```text
URL-SERVICE -> UP (3)
```

ASCII:

```text
URL-SERVICE:8081 ---- register ----+
URL-SERVICE:8082 ---- register ----+--> EUREKA
URL-SERVICE:8083 ---- register ----+
```

---

## 12. Gateway Discovery Setup

In `api-gateway-service`, add dependencies:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

`application.yml`:

```yaml
server:
  port: 8080

spring:
  application:
    name: API-GATEWAY

  cloud:
    gateway:
      routes:
        - id: url-service-create-api
          uri: lb://URL-SERVICE
          predicates:
            - Path=/api/v1/urls/**

        - id: redirect-service-api
          uri: lb://URL-SERVICE
          predicates:
            - Path=/{shortCode}

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
```

Request flow:

```text
Client -> http://localhost:8080/api/v1/urls
Gateway route -> lb://URL-SERVICE
Discovery -> URL-SERVICE:8081
Forward -> http://localhost:8081/api/v1/urls
```

ASCII:

```text
Client
  |
  v
localhost:8080/api/v1/urls
  |
  v
API-GATEWAY
  |
  | route uri: lb://URL-SERVICE
  v
Spring Cloud LoadBalancer
  |
  v
Eureka registry cache
  |
  v
URL-SERVICE instance
```

Important:

```text
lb://URL-SERVICE only works when discovery and load balancer are configured.
```

---

## 13. Calling Services By Name

Sometimes one service calls another service directly.

Example:

```text
analytics-service calls url-service to fetch metadata
```

Use service name, not host.

With `WebClient`:

```java
package com.miniurl.analytics.client;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}
```

Client code:

```java
package com.miniurl.analytics.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UrlServiceClient {

    private final WebClient webClient;

    public UrlServiceClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://URL-SERVICE").build();
    }

    public String getUrlInfo(String shortCode) {
        return webClient.get()
                .uri("/internal/v1/urls/{shortCode}", shortCode)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
```

Why `@LoadBalanced` matters:

```text
Without @LoadBalanced:
    WebClient tries DNS lookup for URL-SERVICE.
    It fails because URL-SERVICE is not a real DNS hostname.

With @LoadBalanced:
    Spring intercepts URL-SERVICE.
    It resolves it through Eureka/DiscoveryClient.
```

ASCII:

```text
WebClient call:
http://URL-SERVICE/internal/v1/urls/abc123
        |
        v
@LoadBalanced interceptor
        |
        v
DiscoveryClient lookup
        |
        v
real instance URL
```

---

## 14. Load Balancing Mental Model

Eureka discovers instances.

Load balancer chooses one.

Do not confuse them.

```text
Eureka = phonebook
Load balancer = caller who chooses which number to dial
```

ASCII:

```text
Eureka registry:
URL-SERVICE -> A, B, C

Load balancer decision:
request 1 -> A
request 2 -> B
request 3 -> C
request 4 -> A
```

Spring Cloud LoadBalancer usually does client-side load balancing:

```text
Client has local list of instances.
Client chooses target.
Client sends request directly.
```

Client-side load balancing:

```text
+-----------+       gets list        +----------+
| Gateway   | <--------------------> | Eureka   |
+-----+-----+                        +----------+
      |
      | choose instance locally
      v
+------------+
| URL Service|
+------------+
```

Server-side load balancing:

```text
Client -> Load Balancer -> Service Instance
```

Eureka pattern in Spring Cloud:

```text
Discovery + client-side load balancing
```

Production implication:

```text
Every caller must have sane timeouts, retries, and circuit breakers.
```

Service discovery does not make failed calls impossible.
It only gives the caller a list of possible targets.

---

## 15. Eureka Self-Preservation

Eureka has a feature called self-preservation.

Mental model:

```text
If many services suddenly stop heartbeating, Eureka suspects network partition instead of mass death.
So it avoids aggressively deleting instances.
```

Why?

Imagine this:

```text
Eureka is alive.
Services are alive.
Network between them is broken.
```

If Eureka deletes everyone immediately:

```text
Registry becomes empty.
Clients think no service exists.
System outage becomes worse.
```

ASCII:

```text
Network partition:

URL-SERVICE  X ---- heartbeat cannot reach ----> EUREKA
REDIRECT     X ---- heartbeat cannot reach ----> EUREKA
ANALYTICS    X ---- heartbeat cannot reach ----> EUREKA

Eureka asks:
Are all services dead?
Or is the network broken?
```

Self-preservation behavior:

```text
Do not evict too aggressively during abnormal heartbeat loss.
Keep registry entries longer.
Prefer stale registry over empty registry.
```

Tradeoff:

```text
Good:
    avoids wiping registry during network trouble

Bad:
    dead instances may remain longer
```

For local learning, some tutorials disable it:

```yaml
eureka:
  server:
    enable-self-preservation: false
```

For production, be careful.

Rule:

```text
Do not blindly disable self-preservation in production.
Understand the failure mode first.
```

---

## 16. Failure Modes

### Failure 1: Eureka Server Down

What happens?

```text
Existing clients may continue using cached registry briefly.
New services cannot register.
Fresh discovery updates fail.
```

ASCII:

```text
Gateway cache has URL-SERVICE list
        |
        v
Can call existing known instances for some time

New URL-SERVICE starts
        |
        v
Cannot register because Eureka is down
```

Fix:

```text
Run Eureka in HA mode for serious production.
Use multiple Eureka servers.
Monitor registry availability.
```

### Failure 2: Wrong Service Name

Gateway route:

```yaml
uri: lb://URL-SERVICE
```

Actual service:

```yaml
spring.application.name: urlservice
```

Result:

```text
No instances available for URL-SERVICE
```

Fix:

```text
Make service names consistent.
Use uppercase convention carefully.
Check Eureka dashboard.
```

### Failure 3: Service Registered With Wrong Host

Container registers:

```text
localhost:8081
```

From gateway container, localhost means gateway itself, not URL service.

Result:

```text
Connection refused
```

Fix:

```yaml
eureka:
  instance:
    prefer-ip-address: true
```

Or configure hostname correctly in Docker/Kubernetes.

### Failure 4: Dead Instance Still Returned

A service died recently.
Eureka has not evicted it yet.
Client still calls it.

Fix:

```text
Use low connection timeouts.
Use retries for safe idempotent requests.
Use circuit breaker.
Use health-aware routing where possible.
```

### Failure 5: Version Mismatch

Gateway routes to service instance running old API version.

Fix:

```text
Use backward-compatible APIs.
Use rolling deployment carefully.
Version routes when necessary.
```

---

## 17. Local Docker Compose Setup

Example local service layout:

```text
eureka-server:8761
api-gateway:8080
url-service-1:8081
url-service-2:8082
postgres:5432
redis:6379
```

`docker-compose.yml` sketch:

```yaml
services:
  eureka-server:
    build: ./eureka-server
    ports:
      - "8761:8761"

  url-service-1:
    build: ./url-service
    environment:
      SERVER_PORT: 8081
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
      SPRING_APPLICATION_NAME: URL-SERVICE
    depends_on:
      - eureka-server

  url-service-2:
    build: ./url-service
    environment:
      SERVER_PORT: 8082
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
      SPRING_APPLICATION_NAME: URL-SERVICE
    depends_on:
      - eureka-server

  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    environment:
      SERVER_PORT: 8080
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
      SPRING_APPLICATION_NAME: API-GATEWAY
    depends_on:
      - eureka-server
      - url-service-1
      - url-service-2
```

Important Docker mental model:

```text
Inside Docker Compose network, service names become DNS names.
```

So this is correct inside containers:

```text
http://eureka-server:8761/eureka/
```

This is wrong inside containers:

```text
http://localhost:8761/eureka/
```

Because localhost means the same container.

ASCII:

```text
api-gateway container
      |
      | localhost:8761  WRONG: points to api-gateway itself
      |
      | eureka-server:8761 CORRECT: compose DNS
      v
Eureka container
```

---

## 18. Kubernetes Reality Check

In Kubernetes, service discovery already exists through Kubernetes Services and DNS.

Example:

```text
http://url-service.default.svc.cluster.local
```

So do you need Eureka in Kubernetes?

Usually:

```text
Not always.
```

Kubernetes provides:

```text
Pod discovery
Service abstraction
DNS
Load balancing
Health checks
Rolling updates
```

Eureka provides:

```text
Application-level registry
Spring Cloud ecosystem integration
metadata
client-side discovery pattern
```

ASCII comparison:

```text
Kubernetes native:
Gateway -> Kubernetes Service DNS -> Pods

Eureka style:
Gateway -> Eureka registry -> Service instances
```

When Eureka is useful:

```text
1. Non-Kubernetes Spring Cloud environment.
2. Legacy Netflix OSS architecture.
3. Mixed VM/container environment.
4. Learning Spring Cloud service discovery.
5. Need application-level registry metadata.
```

When Kubernetes service discovery is enough:

```text
1. All services are inside Kubernetes.
2. You use Kubernetes Services and Ingress/Gateway.
3. You want less moving infrastructure.
```

Senior answer:

```text
Eureka is great for learning and Spring Cloud VM/container environments. In Kubernetes-only production, Kubernetes Services often replace Eureka unless there is a specific reason to keep application-level discovery.
```

---

## 19. Step-by-Step Dry Runs

### Dry Run 1: URL Service Starts Successfully

Config:

```yaml
spring.application.name: URL-SERVICE
server.port: 8081
eureka.client.service-url.defaultZone: http://localhost:8761/eureka/
```

Flow:

```text
1. Eureka Server is running on 8761.
2. URL service starts on 8081.
3. Eureka client sends registration.
4. Eureka stores URL-SERVICE instance.
5. Dashboard shows URL-SERVICE UP.
6. URL service sends heartbeats.
```

Registry:

```text
URL-SERVICE -> localhost:8081 -> UP
```

### Dry Run 2: Gateway Calls URL Service

Request:

```http
POST http://localhost:8080/api/v1/urls
```

Gateway route:

```yaml
uri: lb://URL-SERVICE
```

Flow:

```text
1. Gateway receives request.
2. Path predicate matches /api/v1/urls.
3. Gateway asks load balancer for URL-SERVICE instance.
4. Load balancer uses Eureka registry cache.
5. Instance localhost:8081 is selected.
6. Gateway forwards request to URL service.
7. URL service returns response.
8. Gateway returns response to client.
```

### Dry Run 3: Scale URL Service To Three Instances

Start:

```bash
java -jar url-service.jar --server.port=8081
java -jar url-service.jar --server.port=8082
java -jar url-service.jar --server.port=8083
```

Registry:

```text
URL-SERVICE -> 8081, 8082, 8083
```

Requests:

```text
request 1 -> 8081
request 2 -> 8082
request 3 -> 8083
request 4 -> 8081
```

Mental model:

```text
Same logical service name.
Multiple physical instances.
```

### Dry Run 4: One Instance Dies

Instance:

```text
URL-SERVICE:8082 dies
```

Flow:

```text
1. Heartbeats stop from 8082.
2. Eureka waits for lease expiration.
3. Until eviction, some clients may still see 8082.
4. After eviction, registry removes 8082.
5. Future calls use 8081 and 8083.
```

Important:

```text
There is a delay.
Design for it.
```

### Dry Run 5: Wrong Eureka URL In Docker

Bad config inside container:

```yaml
defaultZone: http://localhost:8761/eureka/
```

Flow:

```text
1. url-service container starts.
2. It tries localhost:8761.
3. localhost means url-service container itself.
4. Eureka is not there.
5. Registration fails.
6. Dashboard does not show URL-SERVICE.
```

Fix:

```yaml
defaultZone: http://eureka-server:8761/eureka/
```

---

## 20. Internal Execution Walkthrough

### Startup path

```text
1. Spring Boot starts.
2. Auto-configuration detects Eureka client dependency.
3. DiscoveryClient is created.
4. ApplicationInfoManager prepares instance metadata.
5. Eureka client registers instance with server.
6. Eureka client schedules heartbeat task.
7. Eureka client schedules registry fetch task.
```

ASCII:

```text
Spring Boot startup
      |
      v
EurekaClientAutoConfiguration
      |
      v
Build instance info
      |
      v
Register with Eureka
      |
      v
Start heartbeat scheduler
      |
      v
Start registry fetch scheduler
```

### Request path through Gateway

```text
1. Gateway receives HTTP request.
2. Route predicate matches path.
3. Route URI contains lb://URL-SERVICE.
4. Gateway delegates to LoadBalancerClient.
5. LoadBalancer gets instances from DiscoveryClient.
6. DiscoveryClient uses local registry cache.
7. LoadBalancer chooses instance.
8. Gateway rewrites target URI.
9. Netty sends request to selected service.
```

ASCII:

```text
HTTP Request
    |
    v
Gateway Route Predicate
    |
    v
lb://URL-SERVICE
    |
    v
LoadBalancer
    |
    v
DiscoveryClient registry cache
    |
    v
Actual URL
    |
    v
URL-SERVICE instance
```

Key point:

```text
Most calls do not hit Eureka Server directly every time.
Clients use a local cached registry that refreshes periodically.
```

Why this matters:

```text
Less load on Eureka.
But discovery changes are not instant.
```

---

## 21. Production Failure Stories

### Failure Story 1: Gateway Says No Instances Available

Symptom:

```text
503 Service Unavailable
No servers available for service: URL-SERVICE
```

Root cause:

```text
URL service registered as url-service, but gateway route used URL-SERVICE.
```

Fix:

```text
Standardize service names.
Check Eureka dashboard.
Use consistent naming in gateway routes.
```

Lesson:

```text
Service discovery is name-based. Names are contracts.
```

### Failure Story 2: Works Locally, Fails In Docker

Symptom:

```text
Cannot connect to Eureka at localhost:8761
```

Root cause:

```text
Inside container, localhost is the current container, not host machine and not Eureka container.
```

Fix:

```text
Use Docker Compose service DNS: http://eureka-server:8761/eureka/
```

Lesson:

```text
Networking meaning changes by runtime environment.
```

### Failure Story 3: Dead Instance Receives Traffic

Symptom:

```text
Some requests fail with connection refused after instance crash.
```

Root cause:

```text
Registry cache and lease eviction are not instant.
Gateway still had stale instance briefly.
```

Fix:

```text
Use timeouts, retries for safe requests, circuit breakers, and health checks.
```

Lesson:

```text
Discovery is eventually consistent, not magic.
```

### Failure Story 4: Registry Wiped During Network Issue

Symptom:

```text
Eureka removes many services during temporary network partition.
```

Root cause:

```text
Self-preservation disabled without understanding tradeoff.
```

Fix:

```text
Tune Eureka carefully. Do not blindly disable self-preservation in production.
```

Lesson:

```text
Fail-safe behavior can look strange, but it exists for a reason.
```

### Failure Story 5: Eureka Becomes Single Point Of Failure

Symptom:

```text
New services cannot register when Eureka server is down.
```

Root cause:

```text
Single Eureka server in production.
```

Fix:

```text
Run multiple Eureka servers or use platform-native discovery.
```

Lesson:

```text
The registry itself needs availability planning.
```

---

## 22. Debugging Mindset

When Eureka discovery fails, ask:

```text
1. Is Eureka Server running?
2. Can service reach Eureka URL?
3. Is defaultZone correct for current environment?
4. Is spring.application.name correct?
5. Does Eureka dashboard show the service UP?
6. Does gateway route use exactly the same service id?
7. Is instance registered with reachable host/IP?
8. Is there a stale dead instance?
9. Is service healthy but not registered?
10. Are client and server Spring Cloud versions compatible?
```

Useful URLs:

```text
Eureka dashboard:
http://localhost:8761

Eureka apps endpoint:
http://localhost:8761/eureka/apps

Specific app:
http://localhost:8761/eureka/apps/URL-SERVICE
```

Useful logs to search:

```text
DiscoveryClient_URL-SERVICE
Registering application URL-SERVICE
registration status: 204
Cannot execute request on any known server
No instances available for URL-SERVICE
```

Debug map:

```text
Service not visible in dashboard:
    client registration problem

Visible but gateway cannot call:
    route name / load balancer / network problem

Visible with localhost in Docker:
    wrong hostname registration

Some requests fail only after crash:
    stale registry / lease delay

Everything fails after Eureka down:
    registry availability / client cache behavior
```

Golden rule:

```text
First verify registry truth, then verify caller resolution, then verify network reachability.
```

---

## 23. Common Mistakes

### Mistake 1: Hardcoding service URLs

Wrong:

```yaml
url-service.base-url: http://localhost:8081
```

Correct:

```yaml
uri: lb://URL-SERVICE
```

### Mistake 2: Wrong service name

Wrong:

```yaml
spring.application.name: url-service
uri: lb://URL-SERVICE-V2
```

Correct:

```yaml
spring.application.name: URL-SERVICE
uri: lb://URL-SERVICE
```

### Mistake 3: Using localhost inside Docker

Wrong:

```yaml
defaultZone: http://localhost:8761/eureka/
```

Correct:

```yaml
defaultZone: http://eureka-server:8761/eureka/
```

### Mistake 4: Thinking Eureka load balances by itself

Wrong mental model:

```text
Eureka chooses instance for every request.
```

Correct:

```text
Eureka gives instance list.
Client-side load balancer chooses instance.
```

### Mistake 5: No timeout or retry

Wrong:

```text
Discovery will always return only good instances.
```

Correct:

```text
Discovery can be stale. Use timeouts, retries, circuit breakers.
```

### Mistake 6: Single Eureka server in serious production

Wrong:

```text
One registry server, no monitoring.
```

Correct:

```text
HA registry or platform-native discovery.
```

### Mistake 7: Using Eureka blindly in Kubernetes

Wrong:

```text
Every microservice system must use Eureka.
```

Correct:

```text
In Kubernetes, native Service DNS may be enough.
```

---

## 24. Testing Strategy

### Local startup test

```text
1. Start Eureka server.
2. Start URL service.
3. Open dashboard.
4. Confirm URL-SERVICE is UP.
```

### Gateway route test

```bash
curl -X POST http://localhost:8080/api/v1/urls \
  -H "Content-Type: application/json" \
  -d '{"longUrl":"https://example.com"}'
```

Expected:

```text
Gateway forwards request to URL-SERVICE.
```

### Multi-instance test

Run:

```bash
java -jar url-service.jar --server.port=8081
java -jar url-service.jar --server.port=8082
java -jar url-service.jar --server.port=8083
```

Then send multiple requests.

Expected:

```text
Requests distributed across instances.
```

Add simple log in URL service:

```java
@GetMapping("/instance")
public String instance(@Value("${server.port}") String port) {
    return "URL-SERVICE running on port " + port;
}
```

Gateway route:

```yaml
- id: url-service-instance-test
  uri: lb://URL-SERVICE
  predicates:
    - Path=/instance
```

Call:

```bash
curl http://localhost:8080/instance
curl http://localhost:8080/instance
curl http://localhost:8080/instance
```

Expected sample:

```text
URL-SERVICE running on port 8081
URL-SERVICE running on port 8082
URL-SERVICE running on port 8083
```

### Failure test

```text
1. Kill one URL service instance.
2. Continue sending requests.
3. Observe short period of possible failures.
4. Wait for eviction.
5. Confirm traffic only goes to remaining instances.
```

Learning goal:

```text
Understand eventual consistency and stale registry behavior.
```

---

## 25. Interview-Ready Explanation

If interviewer asks:

```text
Why do we need Eureka in a Spring Cloud microservice system?
```

Strong answer:

```text
In a microservice system, service instances are dynamic. They can scale up, scale down, restart, and move across hosts or containers, so hardcoding host and port is fragile. Eureka acts as a service registry. Each service registers itself with Eureka using a logical service name and keeps its registration alive using heartbeats. Callers such as an API Gateway fetch the registry, resolve a logical name like URL-SERVICE into actual instances, and use a client-side load balancer to choose one. This lets services communicate by stable names instead of unstable IP addresses. I also design for discovery being eventually consistent, so callers still need timeouts, retries, circuit breakers, and good health checks.
```

If interviewer asks:

```text
Does Eureka do load balancing?
```

Strong answer:

```text
Eureka itself is mainly the registry or phonebook. It stores service names and instances. Load balancing is done by the client-side load balancer, such as Spring Cloud LoadBalancer, which uses the instance list from Eureka and chooses a target instance. So Eureka discovers; the load balancer selects.
```

If interviewer asks:

```text
What happens if one service instance dies?
```

Strong answer:

```text
The instance stops sending heartbeats. Eureka waits for lease expiration and then evicts the instance from the registry. Because clients use cached registries and eviction is not instant, a dead instance may still receive traffic briefly. That is why production callers need timeouts, retries for safe operations, and circuit breakers.
```

If interviewer asks:

```text
Do we need Eureka in Kubernetes?
```

Strong answer:

```text
Not always. Kubernetes already provides service discovery through Services and DNS, plus built-in load balancing to pods. Eureka is useful in Spring Cloud or VM-based environments, mixed environments, or when application-level registry metadata is needed. In Kubernetes-only systems, native Kubernetes service discovery often replaces Eureka unless there is a specific reason to keep it.
```

Senior one-liner:

```text
Eureka removes hardcoded service addresses by turning dynamic service instances into stable logical service names.
```

---

## 26. Senior Engineer Checklist

Before using Eureka in MiniURLShortener, confirm:

```text
[ ] Eureka Server runs on 8761
[ ] Eureka dashboard is reachable
[ ] URL-SERVICE registers successfully
[ ] API-GATEWAY registers successfully
[ ] spring.application.name is stable and consistent
[ ] Gateway routes use lb://SERVICE-NAME
[ ] Docker uses eureka-server hostname, not localhost
[ ] Instance host/IP is reachable from callers
[ ] Multiple URL service instances appear under same service name
[ ] Gateway distributes requests across instances
[ ] Dead instance behavior is tested
[ ] Timeouts are configured
[ ] Retry policy is safe and not blind
[ ] Circuit breaker is planned for service calls
[ ] Eureka logs are monitored
[ ] Registry availability is considered
[ ] Kubernetes replacement decision is understood
```

If these are checked, service discovery is production-shaped enough for the next Spring Cloud chapter.

---

## 27. One-Page Cheat Sheet

```text
Core mental model:
Eureka is a dynamic phonebook for services.

Problem:
Service IPs/ports change in dynamic systems.
Hardcoding addresses breaks with scaling and restarts.

Eureka Server:
Stores service registry.

Eureka Client:
Registers itself and fetches registry.

Service name:
spring.application.name=URL-SERVICE

Gateway route:
uri: lb://URL-SERVICE

Registration:
service starts -> sends name/host/port/status to Eureka

Heartbeat:
service periodically renews lease

Lease expiration:
if heartbeat stops, Eureka eventually removes instance

Discovery:
client asks for service name -> gets instance list

Load balancing:
Eureka gives list; client load balancer chooses instance

Common errors:
No instances available -> wrong name or not registered
localhost in Docker -> wrong network config
Dead instance called -> stale registry / eviction delay
Eureka down -> new registration fails

Production rules:
Use timeouts.
Use retries carefully.
Use circuit breakers.
Monitor Eureka.
Do not blindly disable self-preservation.
In Kubernetes, consider native service discovery.
```

---

## 28. One Picture To Remember

```text
                    EUREKA SERVICE DISCOVERY

                         "Dynamic Phonebook"

                         +----------------+
                         | EUREKA SERVER  |
                         | Registry       |
                         +-------+--------+
                                 ^
                                 |
                register + heartbeat + fetch registry
                                 |
        +------------------------+------------------------+
        |                        |                        |
        v                        v                        v
+---------------+        +---------------+        +---------------+
| URL-SERVICE   |        | URL-SERVICE   |        | URL-SERVICE   |
| 10.0.1.11     |        | 10.0.1.12     |        | 10.0.1.13     |
+---------------+        +---------------+        +---------------+

Client request:

External Client
      |
      v
+----------------+
| API GATEWAY    |
| route:         |
| lb://URL-SVC   |
+-------+--------+
        |
        v
Discovery lookup:
URL-SERVICE -> [10.0.1.11, 10.0.1.12, 10.0.1.13]
        |
        v
Load balancer picks one
        |
        v
Selected URL-SERVICE instance

FINAL MEMORY:

Eureka discovers.
Load balancer chooses.
Heartbeat keeps membership alive.
Timeouts protect against stale discovery.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Eureka is the dynamic phonebook of a Spring Cloud microservice system.
2. Services register themselves with a stable service name and renew their lease using heartbeats.
3. Callers use logical names like URL-SERVICE instead of hardcoded host and port.
4. Eureka gives the instance list, while Spring Cloud LoadBalancer chooses the target instance.
5. Discovery is eventually consistent, so production systems still need timeouts, retries, circuit breakers, and monitoring.
```

Next chapter:

```text
042_Spring_Cloud_Gateway_Routing.md
```
