# 013_Bridge_Host_Overlay_Networks

> MiniDocker Deep Production Mode  
> Understanding First • ASCII Visual Learning • Mental Maps • Real Examples • Do Not Memorize

---

## 0. Why This Chapter Exists

In Docker networking, many developers memorize three words:

```text
bridge
host
overlay
```

That is not enough for production work.

A senior backend engineer must understand **where the packet travels**, **who owns the IP**, **where NAT happens**, **how containers discover each other**, and **why a service works locally but fails in Kubernetes or Swarm**.

This chapter teaches Docker network modes as mental models, not definitions.

The goal is simple:

```text
When a request fails, you should be able to draw the path.
```

If you can draw the packet path, you can debug Docker networking.

---

## 1. One Picture Before Everything

```text
                         Internet / Client
                               |
                               v
+----------------------------------------------------------------+
|                         HOST MACHINE                           |
|                                                                |
|   Mode 1: Bridge Network                                       |
|                                                                |
|   Client -> HostPort -> NAT -> docker0 -> veth -> Container     |
|                                                                |
|   +----------------+        +-----------------------------+     |
|   | Host eth0      |        | docker0 bridge              |     |
|   +-------+--------+        +--------------+--------------+     |
|           |                                |                    |
|           |                         +------+-------+            |
|           |                         |              |            |
|           |                         v              v            |
|           |                    Container A     Container B       |
|           |                    172.18.0.2      172.18.0.3        |
|           |                                                    |
|   Mode 2: Host Network                                         |
|                                                                |
|   Container directly uses host network namespace                |
|                                                                |
|   Container Port 8080 == Host Port 8080                         |
|                                                                |
+----------------------------------------------------------------+


Overlay Network Across Hosts

+-----------------------+                 +-----------------------+
| Host 1                |                 | Host 2                |
|                       |                 |                       |
| Order Container       |                 | User Container        |
| 10.0.1.5              |                 | 10.0.1.8              |
|        |              |                 |        ^              |
|        v              |                 |        |              |
|   Overlay Tunnel ====== VXLAN / Encapsulation ===== Overlay     |
|                       |                 |                       |
+-----------------------+                 +-----------------------+
```

Remember this:

```text
Bridge  = private apartment building inside one host
Host    = container lives directly inside host network
Overlay = private city road connecting many hosts
```

---

## 2. Do Not Memorize Model

Do not memorize Docker networking like exam vocabulary.

Instead, use this model:

```text
Container = House
IP        = House address
Port      = Door
DNS       = Phone book
Bridge    = Apartment internal road
Host mode = No apartment wall; directly on main road
Overlay   = Private highway between cities
NAT       = Reception/security desk redirecting visitors
Veth pair = Virtual cable
```

Now the words become easy.

```text
Bridge network:
Houses inside one apartment compound talk through internal roads.
External visitors must enter through reception.

Host network:
The house has no compound boundary. Its door opens directly to the main road.

Overlay network:
Two apartment compounds in different cities are connected by a private tunnel.
```

This model helps because production bugs usually come from misunderstanding boundaries.

Example:

```text
Works inside container network
Fails from laptop browser
```

That means internal road works, but reception/port mapping may be wrong.

Example:

```text
Works on Host 1
Fails from Host 2
```

That means bridge is not enough. You need overlay, routing, load balancer, or Kubernetes Service networking.

---

## 3. Three Questions For Every Docker Network Mode

Before choosing bridge, host, or overlay, ask three questions.

```text
Question 1: Where is the network namespace?
Question 2: Who owns the IP address?
Question 3: How does outside traffic enter?
```

ASCII decision map:

```text
Need container isolation on single machine?
        |
        v
      Bridge

Need maximum performance or host-level network access?
        |
        v
       Host

Need containers on different machines to talk as if same network?
        |
        v
      Overlay
```

Comparison:

```text
+----------+--------------------+----------------------+----------------------+
| Mode     | Scope              | Isolation            | Common Use           |
+----------+--------------------+----------------------+----------------------+
| Bridge   | Single host        | Good                 | Local apps, compose  |
| Host     | Single host        | Weak network isolation| agents, perf cases   |
| Overlay  | Multiple hosts     | Good logical network | Swarm, multi-node    |
+----------+--------------------+----------------------+----------------------+
```

---

## 4. Bridge Network Mental Model

Bridge is the default Docker networking model for containers on one machine.

Think of a host machine as an apartment building.

```text
Apartment Building = Host Machine
Internal Road      = docker bridge
Flats              = containers
Reception Desk     = NAT / port mapping
Visitor            = external client
```

Diagram:

```text
External Client
      |
      v
Host Machine Public IP
      |
      v
Port Mapping: 8080 -> 8080
      |
      v
+--------------------------------------------------+
| docker0 / custom bridge network                  |
|                                                  |
|   +----------------+      +----------------+     |
|   | order-service  | ---> | user-service   |     |
|   | 172.18.0.2     |      | 172.18.0.3     |     |
|   +----------------+      +----------------+     |
|          |                         |             |
|          v                         v             |
|       redis                    postgres          |
|     172.18.0.4                172.18.0.5         |
+--------------------------------------------------+
```

Inside the same bridge network, containers can talk using service names.

```text
order-service -> http://user-service:8080
order-service -> redis:6379
order-service -> postgres:5432
```

From outside, your browser cannot directly use container IPs safely.

Bad idea:

```text
Browser -> 172.18.0.3:8080
```

Good idea:

```text
Browser -> localhost:8080
Docker NAT -> container:8080
```

---

## 5. Default Bridge vs Custom Bridge

Docker has a default bridge named `bridge`, but custom bridge networks are better for real applications.

Default bridge:

```text
docker run app1
Docker attaches app1 to default bridge
```

Custom bridge:

```bash
docker network create app-net
docker run --network app-net --name user-service user-service:latest
docker run --network app-net --name order-service order-service:latest
```

Why custom bridge is better:

```text
Default bridge:
- weaker name-based discovery behavior
- messy when many apps run on same machine
- less explicit isolation

Custom bridge:
- cleaner DNS by container name
- app-specific network boundary
- easier debugging
- closer to Docker Compose behavior
```

Diagram:

```text
Bad: everything dumped into one default bridge

+---------------- default bridge ----------------+
| app-a-db app-b-db app-c-redis old-test nginx    |
+------------------------------------------------+

Good: each system owns its private network

+------------- payment-net -------------+
| payment-api payment-db redis           |
+----------------------------------------+

+------------- search-net --------------+
| search-api elasticsearch kibana        |
+----------------------------------------+
```

Production mindset:

```text
A network is not just connectivity.
A network is also a boundary.
```

---

## 6. Bridge Packet Journey: Browser To Spring Boot

Command:

```bash
docker run --name order-service -p 8080:8080 --network app-net order-service:latest
```

Request:

```text
Browser -> http://localhost:8080/api/orders
```

Packet journey:

```text
1. Browser sends request to localhost:8080
        |
        v
2. Host receives packet on port 8080
        |
        v
3. Docker NAT / iptables rule matches 8080
        |
        v
4. Packet is translated to container IP:8080
        |
        v
5. Packet enters docker bridge
        |
        v
6. Packet crosses veth pair
        |
        v
7. Container eth0 receives packet
        |
        v
8. Spring Boot embedded Tomcat handles request
```

Visual path:

```text
Browser
  |
  v
localhost:8080
  |
  v
+---------------------- Host ----------------------+
| NAT rule: host:8080 -> 172.18.0.2:8080           |
|                                                  |
| docker bridge                                    |
|     |                                            |
|     v                                            |
|  veth-host <================> eth0-container     |
|                              |                   |
|                              v                   |
|                         Spring Boot              |
+--------------------------------------------------+
```

Important point:

```text
EXPOSE 8080 does not publish the port.
-p 8080:8080 publishes the port.
```

`EXPOSE` is metadata. `-p` creates the actual host entry point.

---

## 7. Bridge Packet Journey: Container To Container

Docker Compose example:

```yaml
services:
  order-service:
    image: order-service:latest
    ports:
      - "8080:8080"
    networks:
      - app-net

  user-service:
    image: user-service:latest
    networks:
      - app-net

  redis:
    image: redis:7
    networks:
      - app-net

networks:
  app-net:
    driver: bridge
```

Order service calls user service:

```text
http://user-service:8080/api/users/10
```

Packet journey:

```text
order-service container
      |
      v
Docker DNS resolves user-service -> 172.18.0.3
      |
      v
order eth0
      |
      v
veth pair
      |
      v
bridge network
      |
      v
user-service veth pair
      |
      v
user-service eth0
      |
      v
Spring Boot UserController
```

Diagram:

```text
+---------------------- app-net bridge ----------------------+
|                                                            |
|  +------------------+           +------------------+        |
|  | order-service   |           | user-service    |        |
|  | 172.18.0.2      |           | 172.18.0.3      |        |
|  |                  | DNS      |                  |        |
|  | GET user-service +---------> | /api/users/{id} |        |
|  +------------------+           +------------------+        |
|                                                            |
+------------------------------------------------------------+
```

Notice:

```text
No host port is needed for order-service to call user-service.
```

Ports are needed only when traffic must enter from outside the Docker network.

---

## 8. Java/Spring Boot Example: Correct Docker DNS Usage

Bad Spring Boot code:

```java
String url = "http://172.18.0.3:8080/api/users/" + userId;
```

Why bad?

```text
Container IP can change after restart.
Scaling creates many IPs.
Hardcoding IP breaks portability.
```

Good configuration:

```yaml
# application.yml
user:
  service:
    base-url: http://user-service:8080
```

Java client:

```java
package com.example.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {

    private final RestTemplate restTemplate;
    private final String userServiceBaseUrl;

    public UserClient(RestTemplate restTemplate,
                      @Value("${user.service.base-url}") String userServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.userServiceBaseUrl = userServiceBaseUrl;
    }

    public UserDto getUser(Long userId) {
        String url = userServiceBaseUrl + "/api/users/" + userId;
        return restTemplate.getForObject(url, UserDto.class);
    }
}
```

RestTemplate bean:

```java
package com.example.order.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class HttpClientConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(3))
                .build();
    }
}
```

Mental model:

```text
Java code should know service names, not container IP addresses.
```

---

## 9. Redis Example In Bridge Network

Compose:

```yaml
services:
  order-service:
    image: order-service:latest
    environment:
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379
    depends_on:
      - redis
    networks:
      - app-net

  redis:
    image: redis:7
    networks:
      - app-net

networks:
  app-net:
    driver: bridge
```

Spring Boot config:

```yaml
spring:
  data:
    redis:
      host: redis
      port: 6379
```

Packet path:

```text
Order Service
    |
    | redis:6379
    v
Docker DNS
    |
    | redis -> 172.18.0.4
    v
Bridge network
    |
    v
Redis container
```

Why this matters in production:

```text
Local: redis container IP = 172.18.0.4
After restart: redis container IP = 172.18.0.7
Name stays: redis
```

So your application must depend on names.

---

## 10. Host Network Mental Model

Host network mode means the container does not get a separate network namespace.

It directly uses the host's network stack.

```text
Bridge mode:

Container Namespace
   |
   v
Bridge
   |
   v
Host Namespace

Host mode:

Container process runs using Host Network Namespace directly
```

Real-world analogy:

```text
Bridge mode:
A shop inside a mall. Customers enter mall reception first.

Host mode:
A shop directly on the street. No mall boundary.
```

Diagram:

```text
Bridge Mode

+---------------- Host ----------------+
|                                      |
|  +------------+      +------------+  |
|  | container  | ---> | docker0    |  |
|  +------------+      +------------+  |
|          |                 |         |
|          v                 v         |
|      private IP        host network  |
+--------------------------------------+

Host Mode

+---------------- Host ----------------+
|                                      |
|  container process                   |
|       |                              |
|       v                              |
|  host eth0 / host ports directly     |
|                                      |
+--------------------------------------+
```

In host mode:

```text
Container port 8080 is host port 8080.
```

No `-p` is needed.

---

## 11. Host Network Example

Command:

```bash
docker run --network host order-service:latest
```

If Spring Boot listens on port 8080, it binds directly to host port 8080.

```text
Browser -> localhost:8080 -> Spring Boot inside container
```

There is no Docker port publishing step.

Visual:

```text
Browser
   |
   v
localhost:8080
   |
   v
+---------------- Host Network Namespace ----------------+
|                                                        |
|  Spring Boot process inside container                   |
|  listening directly on host port 8080                   |
|                                                        |
+--------------------------------------------------------+
```

Problem:

```text
Only one process can bind host port 8080.
```

If host already has an app on 8080:

```text
Error: Address already in use
```

Bridge would allow:

```text
app1 container:8080 -> host:8081
app2 container:8080 -> host:8082
app3 container:8080 -> host:8083
```

Host mode cannot safely multiplex ports like that.

---

## 12. When Host Network Is Useful

Host network is not the default choice for normal microservices.

Use it when you need:

```text
- very low network overhead
- host-level packet visibility
- monitoring agents
- log collectors
- service mesh / network probes
- some high-performance networking cases
```

Example: node exporter, packet collector, or local agent.

```text
+---------------- Host ----------------+
|                                      |
|  App containers on bridge networks    |
|                                      |
|  Monitoring agent on host network     |
|      |                               |
|      v                               |
|  sees host-level network details      |
|                                      |
+--------------------------------------+
```

But for standard Spring Boot services:

```text
Prefer bridge for local single-host development.
Prefer Kubernetes networking in clusters.
Use host mode only when you know why.
```

---

## 13. Host Network Risk: Port Collision

Suppose you run two Spring Boot services.

```bash
docker run --network host order-service:latest
docker run --network host user-service:latest
```

Both try to bind port 8080.

Result:

```text
order-service starts
user-service fails
```

ASCII:

```text
Host Port Table

+------+----------------+
| Port | Owner          |
+------+----------------+
| 8080 | order-service  |
+------+----------------+

user-service also wants 8080
        |
        v
Address already in use
```

With bridge:

```text
Host Port Table

+------+----------------+
| 8081 | order-service  |
| 8082 | user-service   |
+------+----------------+

Both containers internally can still use 8080.
```

So host mode reduces isolation and flexibility.

---

## 14. Overlay Network Mental Model

Bridge works on one host.

But what if containers run on different machines?

```text
Host 1 has order-service
Host 2 has user-service
```

A normal bridge cannot cross machines.

```text
Bridge network boundary = one host
```

Overlay creates a virtual network across multiple Docker hosts.

Real-world analogy:

```text
Bridge:
Internal road inside one apartment compound.

Overlay:
Private tunnel connecting apartment compounds in different cities.
```

Diagram:

```text
+-----------------------+                 +-----------------------+
| Host 1                |                 | Host 2                |
|                       |                 |                       |
| +-------------------+ |                 | +-------------------+ |
| | order-service     | |                 | | user-service      | |
| | overlay IP        | |                 | | overlay IP        | |
| | 10.0.0.5          | |                 | | 10.0.0.8          | |
| +---------+---------+ |                 | +---------^---------+ |
|           |           |                 |           |           |
|           v           |                 |           |           |
|     overlay driver ===================== overlay driver        |
|           |          VXLAN tunnel        |                       |
+-----------+-----------------------------+-----------------------+
```

The containers feel like they are on the same network even though they are on different hosts.

---

## 15. Overlay Packet Journey

Request:

```text
order-service -> http://user-service:8080
```

Actual journey:

```text
1. order-service asks Docker DNS for user-service
2. DNS returns overlay IP
3. packet leaves order container
4. host overlay driver wraps packet inside another packet
5. outer packet travels through real host network
6. Host 2 receives packet
7. overlay driver unwraps packet
8. packet enters user-service container
```

Visual:

```text
Original packet:

order-container -> user-container

Encapsulated packet on real network:

Host1 -> [ inner packet: order -> user ] -> Host2
```

Detailed diagram:

```text
+---------------- Host 1 ----------------+       +---------------- Host 2 ----------------+
|                                         |       |                                         |
| order-service                           |       | user-service                            |
| 10.0.0.5                                |       | 10.0.0.8                                |
|    |                                    |       |    ^                                    |
|    v                                    |       |    |                                    |
| overlay virtual NIC                     |       | overlay virtual NIC                     |
|    |                                    |       |    ^                                    |
|    v                                    |       |    |                                    |
| encapsulate inner packet                |       | decapsulate inner packet                |
|    |                                    |       |    ^                                    |
|    v                                    |       |    |                                    |
| real eth0 -----------------------------+-------+ real eth0                               |
| Host1 IP                               network  Host2 IP                                |
+-----------------------------------------+       +-----------------------------------------+
```

Overlay is powerful, but it adds complexity.

---

## 16. Overlay In Docker Swarm

Overlay is commonly associated with Docker Swarm.

Example:

```bash
docker network create \
  --driver overlay \
  app-overlay
```

Service example:

```bash
docker service create \
  --name user-service \
  --network app-overlay \
  --replicas 3 \
  user-service:latest
```

Mental model:

```text
Docker service = desired number of containers
Overlay network = shared private network across nodes
```

Diagram:

```text
Swarm Cluster

+------------------+       +------------------+       +------------------+
| Node 1           |       | Node 2           |       | Node 3           |
| user replica 1   |       | user replica 2   |       | order replica 1  |
| 10.0.0.11        |       | 10.0.0.12        |       | 10.0.0.20        |
+--------+---------+       +--------+---------+       +--------+---------+
         |                          |                          |
         +==========================+==========================+
                         overlay network
```

DNS can resolve service names across the overlay network.

```text
order-service -> user-service
```

Docker can route to one of the replicas.

---

## 17. Bridge vs Overlay: The Important Difference

Bridge:

```text
Single host only
```

Overlay:

```text
Multiple hosts
```

Do not confuse them.

```text
Bridge

+------------- Host 1 -------------+
| order-service <-> user-service    |
+-----------------------------------+

Host 2 cannot directly join this same bridge.
```

```text
Overlay

+------------- Host 1 -------------+        +------------- Host 2 -------------+
| order-service                     | <----> | user-service                      |
+-----------------------------------+        +-----------------------------------+
```

Table:

```text
+-----------------------+---------------------+---------------------+
| Feature               | Bridge              | Overlay             |
+-----------------------+---------------------+---------------------+
| Scope                 | One Docker host     | Multiple hosts      |
| Typical local dev     | Yes                 | No                  |
| Compose default       | Yes                 | No                  |
| Swarm service network | Not enough          | Yes                 |
| Complexity            | Lower               | Higher              |
| Encapsulation         | No                  | Yes                 |
+-----------------------+---------------------+---------------------+
```

---

## 18. Bridge vs Host vs Overlay: One Big Picture

```text
BRIDGE

External Client
      |
      v
Host Port
      |
      v
NAT
      |
      v
Docker Bridge
      |
      v
Container

HOST

External Client
      |
      v
Host Port
      |
      v
Container process directly using host network

OVERLAY

Container on Host 1
      |
      v
Overlay tunnel
      |
      v
Container on Host 2
```

Memorize less. Draw more.

```text
Bridge answers: How do containers talk inside one host?
Host answers: How can container use host network directly?
Overlay answers: How do containers talk across hosts?
```

---

## 19. Spring Boot Microservice Example With Bridge

Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/order-service.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Compose:

```yaml
services:
  order-service:
    build: ./order-service
    ports:
      - "8080:8080"
    environment:
      USER_SERVICE_BASE_URL: http://user-service:8080
      SPRING_DATA_REDIS_HOST: redis
    networks:
      - app-net

  user-service:
    build: ./user-service
    expose:
      - "8080"
    networks:
      - app-net

  redis:
    image: redis:7
    expose:
      - "6379"
    networks:
      - app-net

networks:
  app-net:
    driver: bridge
```

Important difference:

```text
ports:
  - "8080:8080"

Means expose to host.
```

```text
expose:
  - "8080"

Means document/internal visibility, not host publishing.
```

Traffic:

```text
Browser -> localhost:8080 -> order-service
order-service -> user-service:8080
order-service -> redis:6379
```

ASCII:

```text
Browser
  |
  v
localhost:8080
  |
  v
+-------------------------- app-net --------------------------+
|                                                            |
| +---------------+       +---------------+                  |
| | order-service | ----> | user-service  |                  |
| | host:8080     |       | internal only |                  |
| +-------+-------+       +---------------+                  |
|         |                                                  |
|         v                                                  |
|      +------+                                               |
|      |Redis |                                               |
|      +------+                                               |
+------------------------------------------------------------+
```

---

## 20. Java Controller Example For Network Testing

User service:

```java
package com.example.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/users/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return new UserDto(id, "user-" + id);
    }
}
```

Order service:

```java
package com.example.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OrderController {

    private final RestTemplate restTemplate;
    private final String userBaseUrl;

    public OrderController(RestTemplate restTemplate,
                           @Value("${user.service.base-url}") String userBaseUrl) {
        this.restTemplate = restTemplate;
        this.userBaseUrl = userBaseUrl;
    }

    @GetMapping("/api/orders/{id}")
    public OrderDto getOrder(@PathVariable Long id) {
        UserDto user = restTemplate.getForObject(
                userBaseUrl + "/api/users/" + id,
                UserDto.class
        );

        return new OrderDto(id, user.id(), "CREATED");
    }
}
```

Records:

```java
public record UserDto(Long id, String name) {}
public record OrderDto(Long orderId, Long userId, String status) {}
```

Config:

```yaml
user:
  service:
    base-url: ${USER_SERVICE_BASE_URL:http://localhost:8081}
```

Why default localhost?

```text
Local IDE run: localhost:8081
Docker Compose run: user-service:8080
```

This lets the same app run in both modes.

---

## 21. Dry Run: Bridge Startup

When you run:

```bash
docker compose up
```

Docker roughly does this:

```text
1. Create app-net bridge network
2. Start redis container
3. Attach redis to app-net
4. Give redis an IP
5. Register DNS name redis
6. Start user-service
7. Attach user-service to app-net
8. Register DNS name user-service
9. Start order-service
10. Publish host port 8080 to order-service:8080
11. Register DNS name order-service
```

Visual:

```text
Before compose up

Host
└── no app-net

After compose up

Host
└── app-net bridge
    ├── redis
    ├── user-service
    └── order-service
```

Request dry run:

```text
curl localhost:8080/api/orders/10
        |
        v
order-service
        |
        v
GET user-service:8080/api/users/10
        |
        v
Docker DNS
        |
        v
user-service container IP
        |
        v
response returned
```

---

## 22. Failure Story 1: Localhost Inside Container

Common mistake:

```yaml
USER_SERVICE_BASE_URL: http://localhost:8080
```

Inside order-service container, `localhost` means:

```text
order-service itself
```

Not the host. Not user-service.

Diagram:

```text
Inside order-service container

localhost
   |
   v
same container only
```

Wrong path:

```text
order-service -> localhost:8080 -> order-service
```

Correct path:

```text
order-service -> user-service:8080 -> user-service
```

Rule:

```text
Inside Docker network, use service names.
From host browser, use localhost only for published ports.
```

---

## 23. Failure Story 2: Port Published On Wrong Service

Compose:

```yaml
user-service:
  ports:
    - "8080:8080"

order-service:
  # no ports
```

Browser request:

```text
localhost:8080/api/orders
```

Fails because port 8080 points to user-service, not order-service.

Diagram:

```text
Browser
  |
  v
localhost:8080
  |
  v
user-service
  |
  v
404 /api/orders not found
```

Debug mindset:

```text
A port is a door.
Check which room the door opens into.
```

Command:

```bash
docker ps
```

Look for:

```text
0.0.0.0:8080->8080/tcp
```

Then check which container owns it.

---

## 24. Failure Story 3: Container On Different Bridge Network

Problem:

```text
order-service is on app-net
user-service is on user-net
```

Diagram:

```text
+------------- app-net -------------+
| order-service                      |
+-----------------------------------+

+------------- user-net ------------+
| user-service                       |
+-----------------------------------+
```

Order tries:

```text
http://user-service:8080
```

DNS fails because user-service is not on the same network.

Error examples:

```text
UnknownHostException: user-service
Name or service not known
```

Fix:

```yaml
services:
  order-service:
    networks:
      - app-net

  user-service:
    networks:
      - app-net
```

Or attach manually:

```bash
docker network connect app-net user-service
```

---

## 25. Failure Story 4: Host Mode Breaks Scaling

You try to run 3 replicas with host network.

```text
replica 1 -> host port 8080
replica 2 -> host port 8080
replica 3 -> host port 8080
```

Only one can win.

Diagram:

```text
Host Network Port 8080
        |
        +--> replica 1 started
        +--> replica 2 failed
        +--> replica 3 failed
```

Bridge mode can solve this with different host ports:

```text
host:8081 -> replica1:8080
host:8082 -> replica2:8080
host:8083 -> replica3:8080
```

But true production scaling usually uses:

```text
Load Balancer -> Service -> Replicas
```

In Kubernetes:

```text
Service stable IP -> multiple Pods
```

---

## 26. Failure Story 5: Overlay Works But Is Slow

Overlay adds encapsulation.

That means each packet gets wrapped.

```text
Original packet
    |
    v
Wrapped inside outer packet
    |
    v
Sent over real network
```

This can add overhead.

Symptoms:

```text
- higher latency between services
- lower throughput than same-host bridge
- MTU-related packet fragmentation
- random connection resets under load
```

MTU mental model:

```text
Truck capacity = MTU
Overlay wrapping = extra packaging
If package becomes too big, it must be split
Splitting increases delay and failure risk
```

Diagram:

```text
Normal packet size:        [ payload ]
Overlay packet size: [ overlay header ][ payload ]

If total > network MTU
        |
        v
fragmentation / drops / retries
```

Debugging overlay performance means checking not only Docker but also real host networking.

---

## 27. Debugging Playbook: Bridge

When bridge networking fails, ask:

```text
1. Is the container running?
2. Is it on the expected network?
3. Can DNS resolve the service name?
4. Is the target port listening inside container?
5. Is the port published if traffic comes from host?
6. Is the app bound to 0.0.0.0 instead of 127.0.0.1?
```

Commands:

```bash
docker ps
docker network ls
docker network inspect app-net
docker inspect order-service
docker exec -it order-service sh
```

Inside container:

```bash
getent hosts user-service
nc -vz user-service 8080
wget -qO- http://user-service:8080/actuator/health
```

Packet map:

```text
Caller container
   |
   v
DNS resolve?
   |
   v
Network route?
   |
   v
Port open?
   |
   v
Application healthy?
```

---

## 28. Debugging Playbook: Host Network

For host network:

```text
No Docker NAT
No bridge boundary
No container-only port mapping
```

Check host ports:

```bash
ss -lntp
lsof -i :8080
```

Check if app is listening:

```bash
curl localhost:8080/actuator/health
```

Common issue:

```text
Port already used by another process.
```

Common misunderstanding:

```text
-p is ignored or unnecessary with host mode.
```

Mental model:

```text
The container is wearing the host's network jacket.
```

So debug as if the app is running directly on the host.

---

## 29. Debugging Playbook: Overlay

Overlay failures require two layers of debugging:

```text
Layer 1: Container-level virtual network
Layer 2: Real host-to-host network
```

Checklist:

```text
[ ] Are both nodes in the cluster?
[ ] Is the overlay network created?
[ ] Are services attached to the overlay?
[ ] Can DNS resolve service names?
[ ] Can Host 1 reach Host 2 on required ports?
[ ] Are firewall rules blocking overlay traffic?
[ ] Is MTU causing fragmentation?
```

Visual:

```text
Container thinks:
order -> user

Reality:
order -> Host1 overlay -> physical network -> Host2 overlay -> user
```

So if overlay fails, do not only inspect the container.

Also inspect:

```text
host firewall
routing
node membership
cluster state
MTU
security groups
```

---

## 30. Application Binding: 127.0.0.1 vs 0.0.0.0

A very common Spring Boot/container bug is binding to localhost only.

Wrong inside container:

```yaml
server:
  address: 127.0.0.1
  port: 8080
```

This means:

```text
Only accept traffic from inside same container.
```

Docker NAT sends traffic to the container network interface, but app listens only on loopback.

Result:

```text
Port published, container running, but request fails.
```

Correct:

```yaml
server:
  address: 0.0.0.0
  port: 8080
```

Or simply omit `server.address`, because Spring Boot usually listens on all interfaces by default.

Diagram:

```text
Wrong

Docker packet -> container eth0 -> app not listening there
                            |
                            v
                         connection refused

Correct

Docker packet -> container eth0 -> app listening on 0.0.0.0:8080
                            |
                            v
                         request handled
```

---

## 31. Security Mindset

Bridge is not just networking; it is also isolation.

Bad Compose:

```yaml
postgres:
  image: postgres
  ports:
    - "5432:5432"
```

This exposes Postgres to the host network.

Better for local microservices:

```yaml
postgres:
  image: postgres
  expose:
    - "5432"
```

Only services inside the Docker network can reach it.

Security diagram:

```text
Bad

Laptop / LAN
    |
    v
postgres:5432 exposed

Good

Laptop / LAN
    |
    x
postgres not published

order-service inside app-net
    |
    v
postgres:5432 works
```

Rule:

```text
Publish only edge services.
Keep databases internal.
```

For example:

```text
Publish API gateway
Do not publish Redis
Do not publish Postgres
Do not publish Kafka unless needed
```

---

## 32. Kubernetes Connection

Docker bridge helps you understand Kubernetes networking, but Kubernetes changes the model.

Docker Compose:

```text
container -> service name -> container
```

Kubernetes:

```text
pod -> Kubernetes Service DNS -> Service virtual IP -> pod endpoint
```

Diagram:

```text
Docker Compose

order-service -> user-service:8080 -> user container

Kubernetes

order pod -> user-service.default.svc.cluster.local
          -> ClusterIP
          -> one of user pods
```

Kubernetes does not use Docker Compose bridge networking for service-to-service communication.

But the mental ideas remain:

```text
DNS name over IP
Stable service endpoint over changing container/pod IP
Network boundary matters
Debug packet path
```

---

## 33. Production Decision Guide

Choose bridge when:

```text
- local development
- single-host deployment
- Docker Compose microservices
- you want isolation and simple service DNS
```

Choose host when:

```text
- you need host network visibility
- you run monitoring/network agents
- you need low overhead
- you understand port collision risk
```

Choose overlay when:

```text
- containers run across multiple Docker hosts
- using Docker Swarm
- services need one logical network across nodes
```

Do not choose overlay just because it sounds advanced.

```text
Wrong mindset:
Overlay is advanced, so use it.

Correct mindset:
I need cross-host container communication, so use overlay.
```

---

## 34. Real Production Story: Payment System Local Works, Deployment Fails

A payment team runs services locally using Docker Compose.

```text
payment-api
fraud-service
redis
postgres
```

Local config:

```yaml
FRAUD_URL: http://fraud-service:8080
REDIS_HOST: redis
POSTGRES_HOST: postgres
```

Everything works.

Then someone deploys containers manually on two VMs.

```text
VM1: payment-api
VM2: fraud-service
```

They keep the same URL:

```text
http://fraud-service:8080
```

It fails.

Why?

```text
Docker bridge DNS works only inside the same Docker network on the same host.
VM1 bridge and VM2 bridge are different private networks.
```

Diagram:

```text
VM1 bridge                     VM2 bridge
+-------------+                +-------------+
| payment-api |                | fraud       |
+-------------+                +-------------+
       |                              ^
       | fraud-service DNS?           |
       x no shared DNS/network        |
```

Solutions:

```text
- run both services on same custom bridge if single host
- use overlay if Docker Swarm multi-host
- use Kubernetes Service if Kubernetes
- use external load balancer / service discovery
```

Lesson:

```text
Service names are scoped to the network system that created them.
```

---

## 35. Interview Answer: Bridge Network

Strong answer:

```text
Docker bridge networking gives containers isolated network namespaces connected through a virtual bridge on the host. Each container gets its own IP on that bridge, and Docker connects the container namespace to the bridge using a veth pair. Containers on the same custom bridge can communicate using Docker DNS names. External traffic reaches containers through published ports, where Docker configures NAT rules from host ports to container ports.
```

Simple version:

```text
Bridge is a private virtual LAN inside one Docker host.
```

Production add-on:

```text
For real applications, I prefer custom bridge networks over the default bridge because they give clearer isolation and service-name based communication between containers in the same application boundary.
```

---

## 36. Interview Answer: Host Network

Strong answer:

```text
In host network mode, the container does not get a separate network namespace. It uses the host's network stack directly, so a process inside the container binds directly to host ports. This reduces network isolation and can improve performance or visibility for special cases, but it creates port collision risks and is usually not the default choice for normal microservices.
```

Simple version:

```text
Host mode means container networking is host networking.
```

Production add-on:

```text
I would use it for agents or low-level network tools, but for normal Spring Boot services I usually prefer bridge, Compose networking, or Kubernetes Services.
```

---

## 37. Interview Answer: Overlay Network

Strong answer:

```text
An overlay network creates a logical network across multiple Docker hosts. Containers on different hosts can communicate as if they are on the same private network. Under the hood, packets are encapsulated and sent through the real host network, often using tunneling mechanisms such as VXLAN. Overlay is useful for Docker Swarm or multi-host container communication, but it introduces extra complexity around firewall rules, MTU, routing, and cluster membership.
```

Simple version:

```text
Overlay is a bridge-like network stretched across multiple machines.
```

Production add-on:

```text
If I see overlay issues, I debug both the container network and the physical host-to-host network.
```

---

## 38. Cheat Sheet

```text
Bridge
------
Scope: one host
Use: local apps, Compose
Traffic: container -> bridge -> container
External access: port publishing / NAT
Risk: wrong port mapping, wrong network, localhost confusion

Host
----
Scope: one host
Use: agents, performance-sensitive special cases
Traffic: direct host network
External access: app binds host port directly
Risk: port collision, less isolation

Overlay
-------
Scope: multiple hosts
Use: Swarm / multi-node Docker
Traffic: encapsulated across hosts
External access: service routing / published ports
Risk: firewall, MTU, node membership, routing
```

Command cheat sheet:

```bash
# list networks
docker network ls

# create custom bridge
docker network create app-net

# inspect network
docker network inspect app-net

# run container on bridge
docker run --network app-net --name user-service user-service:latest

# publish port
docker run -p 8080:8080 --network app-net order-service:latest

# host network
docker run --network host order-service:latest

# enter container
docker exec -it order-service sh

# test DNS inside container
getent hosts user-service

# test port inside container
nc -vz user-service 8080
```

---

## 39. One Picture To Remember

```text
                           Docker Network Modes

+--------------------------------------------------------------------------------+
|                                                                                |
|  BRIDGE                                                                        |
|                                                                                |
|  Client -> Host Port -> NAT -> Bridge -> Container                             |
|                                                                                |
|  Best mental model: private apartment road inside one host                      |
|                                                                                |
+--------------------------------------------------------------------------------+

+--------------------------------------------------------------------------------+
|                                                                                |
|  HOST                                                                          |
|                                                                                |
|  Client -> Host Port -> Container process directly                             |
|                                                                                |
|  Best mental model: shop directly on main road                                  |
|                                                                                |
+--------------------------------------------------------------------------------+

+--------------------------------------------------------------------------------+
|                                                                                |
|  OVERLAY                                                                       |
|                                                                                |
|  Container on Host 1 -> Tunnel -> Container on Host 2                           |
|                                                                                |
|  Best mental model: private highway between cities                              |
|                                                                                |
+--------------------------------------------------------------------------------+
```

Final rule:

```text
Do not ask: which Docker network should I memorize?
Ask: where is my packet going, and what boundary must it cross?
```

---

## 40. Final Takeaways

1. Bridge is the default single-host private network model.
2. Custom bridge networks are better than dumping everything into the default bridge.
3. Bridge uses container namespaces, virtual Ethernet pairs, Docker DNS, and NAT for published ports.
4. Host network removes container network isolation and uses the host network stack directly.
5. Host mode is useful for special cases but risky for normal microservices because of port collisions.
6. Overlay connects containers across multiple hosts using a logical network and packet encapsulation.
7. Overlay bugs require debugging both Docker-level networking and real host-to-host networking.
8. In Spring Boot, use service names like `user-service`, `redis`, and `postgres`, not container IPs.
9. Inside a container, `localhost` means the same container, not another service.
10. Publish only edge services. Keep Redis, Postgres, and internal services private unless exposure is required.
11. Kubernetes networking uses different implementation details, but the same mental rules apply: stable names, changing endpoints, and packet-path debugging.
12. The best Docker networking skill is not memorization. It is drawing the packet journey.
