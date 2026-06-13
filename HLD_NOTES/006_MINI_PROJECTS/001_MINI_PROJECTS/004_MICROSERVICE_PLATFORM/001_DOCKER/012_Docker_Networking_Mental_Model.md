# 012_Docker_Networking_Mental_Model

> MiniDocker Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • No Memorization  
> Target: Senior Java Backend / Spring Boot / Cloud Native / Product Company Interviews

---

## 0. Why This Chapter Exists

Docker networking looks confusing because people try to memorize words:

```text
bridge
host
none
overlay
veth
iptables
NAT
DNS
port mapping
network namespace
```

That is the wrong way.

Docker networking becomes simple when you think like this:

```text
Container = Small computer
Network Namespace = Its private network room
Docker Network = Private city road system
Bridge = Local router/switch
Veth Pair = Virtual cable
Port = Door
IP = House address
DNS = Phone book
NAT = Reception desk forwarding visitors
```

If you understand this mental model, you do not need to memorize Docker networking commands blindly. You can reason through failures in production.

---

## 1. Real World Mental Model

Imagine a company campus.

```text
+--------------------------------------------------+
| Company Campus / Host Machine                    |
|                                                  |
|   +--------------+       +--------------+        |
|   | Order Team   |       | User Team    |        |
|   | Room A       |       | Room B       |        |
|   +--------------+       +--------------+        |
|          \                       /               |
|           \                     /                |
|            v                   v                 |
|          +-------------------------+             |
|          | Reception / Router      |             |
|          +-------------------------+             |
|                         |                        |
+-------------------------|------------------------+
                          |
                       Internet
```

Docker version:

```text
+--------------------------------------------------+
| Host Machine                                     |
|                                                  |
|   +--------------+       +--------------+        |
|   | order-app    |       | user-app     |        |
|   | container    |       | container    |        |
|   +--------------+       +--------------+        |
|          \                       /               |
|           \                     /                |
|            v                   v                 |
|          +-------------------------+             |
|          | docker0 bridge          |             |
|          +-------------------------+             |
|                         |                        |
+-------------------------|------------------------+
                          |
                       Internet
```

The important idea:

```text
Containers do not magically communicate.
They communicate through Linux networking objects created by Docker.
```

---

## 2. What Problem Docker Networking Solves

Without Docker networking:

```text
order-service container
      |
      X cannot reach
      |
user-service container
```

Microservices fail because they need to talk to each other:

```text
Order Service -> User Service
Order Service -> Payment Service
Order Service -> Redis
Order Service -> PostgreSQL
```

Docker networking provides:

```text
1. Isolation
2. Container-to-container communication
3. Host-to-container communication
4. Container-to-internet communication
5. DNS-based discovery
6. Port exposure
```

---

## 3. One Picture To Remember

```text
                        Internet
                            |
                            v

+---------------------------------------------------------+
| Host Machine                                            |
|                                                         |
|   Browser: localhost:8080                               |
|             |                                           |
|             v                                           |
|      iptables / NAT                                     |
|             |                                           |
|             v                                           |
|      +-------------------+                              |
|      | docker0 bridge    |  172.17.0.1                  |
|      +---------+---------+                              |
|                |                                        |
|      +---------+----------+----------------+            |
|      |                    |                |            |
|      v                    v                v            |
| +-----------+       +-----------+     +-----------+     |
| | order     |       | user      |     | redis     |     |
| | 172.17.0.2|       | 172.17.0.3|     |172.17.0.4 |     |
| +-----------+       +-----------+     +-----------+     |
|                                                         |
+---------------------------------------------------------+
```

Read this picture as:

```text
Host has a virtual bridge.
Containers connect to that bridge using virtual cables.
Docker gives containers IP addresses.
Docker can expose container ports to the host.
Docker DNS can resolve container/service names.
```

---

## 4. Container Network Namespace

A container is just a process, but Docker puts it inside a separate network namespace.

Network namespace means:

```text
This process sees its own:

- network interfaces
- IP address
- routing table
- ports
- firewall view
```

Host namespace:

```text
+---------------- Host Network Namespace ----------------+
|                                                        |
| eth0: 192.168.1.10                                     |
| docker0: 172.17.0.1                                    |
| routing table                                          |
| host ports                                             |
|                                                        |
+--------------------------------------------------------+
```

Container namespace:

```text
+-------------- Container Network Namespace -------------+
|                                                        |
| eth0: 172.17.0.2                                       |
| routing table                                          |
| app listening on 8080                                  |
|                                                        |
+--------------------------------------------------------+
```

The container thinks:

```text
I have my own eth0.
I have my own IP.
I have my own port space.
```

But internally, it is connected back to the host through a veth pair.

---

## 5. Veth Pair Mental Model

A veth pair is a virtual cable with two ends.

```text
One end inside container
One end on host
```

ASCII picture:

```text
+---------------- Container ----------------+
|                                           |
|   eth0                                    |
|    |                                      |
+----|--------------------------------------+
     |
     | virtual cable
     |
+----|--------------------------------------+
|  vethabc                                  |
|    |                                      |
| docker0 bridge                            |
|                                           |
+---------------- Host ---------------------+
```

Mental model:

```text
Container eth0 <==== virtual cable ====> Host veth interface
```

When a packet leaves the container, it travels through this cable to docker0.

---

## 6. docker0 Bridge

Docker creates a default bridge named:

```bash
docker0
```

Think of docker0 as a virtual switch/router inside your machine.

```text
              docker0 bridge
          +---------------------+
          | 172.17.0.1          |
          +----+-----------+----+
               |           |
               |           |
          +----v---+   +---v----+
          | order  |   | user   |
          | .2     |   | .3     |
          +--------+   +--------+
```

When containers are attached to the same bridge network, they can communicate using IP addresses.

Example:

```text
order container: 172.17.0.2
user container : 172.17.0.3
```

Communication:

```text
order -> 172.17.0.3:8080 -> user
```

But in production-like Docker Compose, you should prefer DNS names:

```text
order -> http://user-service:8080
```

---

## 7. Default Bridge vs Custom Bridge

Docker default bridge works, but custom bridge is better for microservices.

Default bridge:

```text
docker run order-service
docker run user-service
```

Custom bridge:

```bash
docker network create app-net

docker run --network app-net --name order-service order:1.0
docker run --network app-net --name user-service user:1.0
```

Why custom bridge is better:

```text
1. Better DNS name resolution
2. Better isolation
3. Cleaner microservice grouping
4. Easier debugging
```

ASCII:

```text
+---------------- app-net ----------------+
|                                         |
|  order-service ---> user-service        |
|        |                 |              |
|        v                 v              |
|      redis           postgres           |
|                                         |
+-----------------------------------------+
```

Another unrelated app can run in another network:

```text
+---------------- payment-net ------------+
| payment-service                         |
| fraud-service                           |
+-----------------------------------------+
```

The two networks are isolated unless explicitly connected.

---

## 8. Port Mapping Mental Model

A container port is internal.

If Spring Boot listens on port 8080 inside container:

```text
Container
  |
  +-- Spring Boot :8080
```

That does not automatically mean your browser can access it from the host.

You need port publishing:

```bash
docker run -p 9090:8080 order-service
```

Meaning:

```text
Host port 9090 -> Container port 8080
```

ASCII:

```text
Browser
  |
  | localhost:9090
  v
+---------------- Host ----------------+
|                                      |
| Host Port 9090                       |
|     |                                |
|     v                                |
| NAT / Port Forward                   |
|     |                                |
|     v                                |
| Container 172.17.0.2:8080            |
|     |                                |
|     v                                |
| Spring Boot                          |
+--------------------------------------+
```

Common mistake:

```bash
docker run order-service
```

Then trying:

```text
localhost:8080
```

Fails because the port was not published.

Correct:

```bash
docker run -p 8080:8080 order-service
```

---

## 9. NAT Mental Model

NAT means network address translation.

Do not memorize it. Think reception desk.

```text
Visitor asks reception:
"I want room 8080"

Reception says:
"Go to internal room 172.17.0.2:8080"
```

Docker NAT:

```text
Host:8080 -> Container:8080
```

ASCII:

```text
External Client
      |
      v
Host IP:8080
      |
      v
iptables NAT rule
      |
      v
172.17.0.2:8080
      |
      v
Spring Boot container
```

In debugging, always ask:

```text
Is the app listening inside the container?
Is the container port published to host?
Is host firewall blocking it?
Is Docker NAT rule present?
```

---

## 10. Packet Journey: Browser To Spring Boot Container

Command:

```bash
docker run -p 8080:8080 order-service
```

Browser:

```text
http://localhost:8080/orders/1
```

Packet journey:

```text
1. Browser sends request to localhost:8080
2. Host receives packet
3. Docker NAT rule matches port 8080
4. Packet is forwarded to container IP
5. Packet crosses docker0 bridge
6. Packet crosses veth pair
7. Container eth0 receives packet
8. Spring Boot Tomcat handles request
9. Response goes back same path
```

ASCII:

```text
Browser
  |
  v
localhost:8080
  |
  v
Host network stack
  |
  v
Docker NAT
  |
  v
docker0 bridge
  |
  v
veth pair
  |
  v
container eth0
  |
  v
Spring Boot Tomcat
```

---

## 11. Container To Container Communication

Two containers on the same custom network:

```yaml
services:
  order-service:
    image: order:1.0

  user-service:
    image: user:1.0
```

Compose creates a network.

```text
+---------------- compose_default network ----------------+
|                                                         |
|  order-service                                          |
|       |                                                 |
|       | http://user-service:8080                        |
|       v                                                 |
|  user-service                                           |
|                                                         |
+---------------------------------------------------------+
```

Inside order-service Java code:

```java
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final WebClient webClient;

    public OrderController(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://user-service:8080")
                .build();
    }

    @GetMapping("/{id}")
    public Mono<String> getOrder(@PathVariable String id) {
        return webClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(String.class)
                .map(user -> "Order for user: " + user);
    }
}
```

Notice:

```text
No IP address.
Use service name.
```

Bad:

```java
baseUrl("http://172.18.0.3:8080")
```

Good:

```java
baseUrl("http://user-service:8080")
```

Why?

```text
Container IPs can change.
Service names remain stable.
```

---

## 12. Docker DNS

Docker provides DNS resolution inside custom networks.

When order-service calls:

```text
http://user-service:8080
```

Docker DNS resolves:

```text
user-service -> container IP
```

ASCII:

```text
Order Container
     |
     | DNS query: user-service?
     v
Docker Embedded DNS
     |
     | answer: 172.18.0.3
     v
Order Container connects to 172.18.0.3:8080
```

Debug commands:

```bash
docker exec -it order-service sh

getent hosts user-service
nslookup user-service
ping user-service
curl http://user-service:8080/actuator/health
```

If DNS fails, check:

```bash
docker network inspect app-net
```

---

## 13. Docker Compose Networking

Compose is the most common local microservice setup.

Example:

```yaml
version: "3.9"

services:
  order-service:
    build: ./order-service
    ports:
      - "8081:8080"
    environment:
      USER_SERVICE_URL: http://user-service:8080
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/orders
    depends_on:
      - user-service
      - redis
      - postgres

  user-service:
    build: ./user-service
    ports:
      - "8082:8080"

  redis:
    image: redis:7
    ports:
      - "6379:6379"

  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: orders
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
    ports:
      - "5432:5432"
```

Network picture:

```text
+---------------- Docker Compose Network ----------------+
|                                                        |
|  order-service                                         |
|      |                                                 |
|      +--> user-service:8080                            |
|      |                                                 |
|      +--> redis:6379                                   |
|      |                                                 |
|      +--> postgres:5432                                |
|                                                        |
+--------------------------------------------------------+

Host access:
localhost:8081 -> order-service:8080
localhost:8082 -> user-service:8080
localhost:6379 -> redis:6379
localhost:5432 -> postgres:5432
```

Important:

```text
Inside containers, use service names.
From host machine, use localhost mapped ports.
```

Inside container:

```text
postgres:5432
redis:6379
user-service:8080
```

From laptop:

```text
localhost:5432
localhost:6379
localhost:8081
```

---

## 14. Spring Boot Configuration Example

application.yml:

```yaml
server:
  port: 8080

user-service:
  base-url: ${USER_SERVICE_URL:http://localhost:8082}

spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/orders}
    username: ${SPRING_DATASOURCE_USERNAME:app}
    password: ${SPRING_DATASOURCE_PASSWORD:secret}

  data:
    redis:
      host: ${SPRING_DATA_REDIS_HOST:localhost}
      port: ${SPRING_DATA_REDIS_PORT:6379}
```

Why this is production-friendly:

```text
Local run uses localhost.
Docker run uses service names through env variables.
Kubernetes run uses Service DNS names.
```

Same app, different environment.

---

## 15. Redis Connectivity Example

Docker Compose:

```yaml
services:
  order-service:
    environment:
      SPRING_DATA_REDIS_HOST: redis

  redis:
    image: redis:7
```

Java:

```java
@Service
public class CartCacheService {

    private final StringRedisTemplate redisTemplate;

    public CartCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheCart(String userId, String json) {
        redisTemplate.opsForValue().set("cart:" + userId, json, Duration.ofMinutes(30));
    }

    public String getCart(String userId) {
        return redisTemplate.opsForValue().get("cart:" + userId);
    }
}
```

Network flow:

```text
Order Service
     |
     | redis:6379
     v
Docker DNS
     |
     v
Redis Container
```

Common failure:

```text
Order service configured with localhost:6379
```

Inside container, localhost means:

```text
The order-service container itself
```

not the Redis container.

Correct:

```text
redis:6379
```

---

## 16. PostgreSQL Connectivity Example

Bad Docker configuration:

```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/orders
```

Why bad?

```text
Inside container, localhost points to the same container.
Postgres is in another container.
```

Good:

```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/orders
```

ASCII:

```text
order-service container
       |
       | jdbc:postgresql://postgres:5432/orders
       v
Docker DNS
       |
       v
postgres container
```

Debug:

```bash
docker exec -it order-service sh
nc -vz postgres 5432
```

If `nc` fails:

```text
1. Is postgres container running?
2. Are both containers on same network?
3. Is Postgres listening on 5432?
4. Is service name correct?
5. Is startup order causing temporary failure?
```

---

## 17. localhost Confusion

This is one of the biggest Docker networking mistakes.

On your laptop:

```text
localhost = your laptop
```

Inside container:

```text
localhost = that container
```

ASCII:

```text
Host Machine
  localhost
     |
     +-- Browser
     +-- Docker daemon

Container A
  localhost
     |
     +-- only Container A

Container B
  localhost
     |
     +-- only Container B
```

So this fails inside order-service:

```text
http://localhost:8080/users
```

because it calls itself, not user-service.

Correct:

```text
http://user-service:8080/users
```

---

## 18. Bridge Network Packet Dry Run

Scenario:

```text
order-service calls user-service
```

Request:

```text
GET http://user-service:8080/users/42
```

Dry run:

```text
1. Java WebClient creates HTTP request
2. Container asks DNS: who is user-service?
3. Docker DNS returns 172.18.0.3
4. Packet leaves order-service eth0
5. Packet crosses veth pair
6. Packet enters Docker bridge
7. Bridge forwards packet to user-service veth
8. Packet enters user-service eth0
9. Tomcat receives request on 8080
10. Response returns through same network path
```

ASCII:

```text
order-service
   |
   | DNS: user-service -> 172.18.0.3
   v
eth0
   |
veth pair
   |
docker bridge
   |
veth pair
   |
eth0
   v
user-service
```

---

## 19. Host Network Mode

Host network mode removes bridge isolation.

```bash
docker run --network host order-service
```

Normal bridge:

```text
Container -> Bridge -> Host network
```

Host mode:

```text
Container -> Host network directly
```

ASCII:

```text
Bridge Mode:

Container 172.17.0.2:8080
      |
      v
docker0
      |
      v
Host

Host Mode:

Container uses Host IP directly
      |
      v
Host network stack
```

Pros:

```text
Less network overhead
Useful for low-level networking tools
```

Cons:

```text
Less isolation
Port conflicts with host
Not portable across Docker Desktop environments
```

For Spring Boot microservices, usually use bridge/Compose/Kubernetes networking, not host mode.

---

## 20. None Network

None network means:

```bash
docker run --network none app
```

Container gets no external network.

```text
Container
  |
  X no internet
  X no other containers
  X no service calls
```

Useful for:

```text
Batch processing
Security isolation
Offline jobs
Static analysis tools
```

Example:

```text
A container that only reads mounted files and writes output.
No network needed.
```

---

## 21. Overlay Network Mental Model

Bridge network is mostly single-host.

Overlay network connects containers across multiple hosts.

```text
Host A                              Host B
+------------------+                +------------------+
| order-service    |                | user-service     |
| 10.0.1.2         |                | 10.0.1.3         |
+--------+---------+                +---------+--------+
         |                                    |
         +----------- Overlay Network --------+
```

Mental model:

```text
Overlay = virtual network stretched across machines
```

Used by:

```text
Docker Swarm
Kubernetes CNI plugins
Cloud networking layers
```

Kubernetes equivalent:

```text
Pod on Node A can talk to Pod on Node B
```

---

## 22. Kubernetes Connection

Docker networking teaches the base ideas.

Kubernetes builds on them.

Docker:

```text
Container IP
Docker bridge
Docker DNS
Port mapping
```

Kubernetes:

```text
Pod IP
CNI network
CoreDNS
Service / Ingress
```

Mapping:

```text
Docker Container       -> Kubernetes Pod Container
Docker Network         -> Kubernetes Pod Network
Docker DNS             -> CoreDNS
Docker Port Mapping    -> Service / Ingress
Docker Compose Service -> Kubernetes Service
```

Kubernetes request flow:

```text
User
  |
Ingress
  |
Service
  |
Pod IP
  |
Spring Boot
```

Docker Compose request flow:

```text
Browser
  |
localhost:8081
  |
Port Mapping
  |
order-service container
```

---

## 23. Production Failure Story 1: Hardcoded Container IP

Bad design:

```java
private static final String USER_SERVICE_URL = "http://172.18.0.3:8080";
```

Works today.

Then user-service restarts.

Old:

```text
user-service = 172.18.0.3
```

New:

```text
user-service = 172.18.0.7
```

Order service still calls old IP.

Failure:

```text
Connection refused
Timeout
5xx errors
```

Correct design:

```java
private static final String USER_SERVICE_URL = "http://user-service:8080";
```

Why:

```text
DNS name remains stable even when IP changes.
```

---

## 24. Production Failure Story 2: localhost In Container

Spring Boot config:

```yaml
spring:
  data:
    redis:
      host: localhost
```

In local IDE:

```text
Works
```

In Docker:

```text
Fails
```

Why?

```text
localhost inside order-service means order-service container.
Redis is another container.
```

Fix:

```yaml
spring:
  data:
    redis:
      host: redis
```

Better:

```yaml
spring:
  data:
    redis:
      host: ${SPRING_DATA_REDIS_HOST:localhost}
```

Then Docker Compose:

```yaml
environment:
  SPRING_DATA_REDIS_HOST: redis
```

---

## 25. Production Failure Story 3: Port Published Wrong

Command:

```bash
docker run -p 8080:9090 order-service
```

But Spring Boot listens on:

```text
8080
```

Meaning of command:

```text
Host 8080 -> Container 9090
```

Wrong because nothing listens on container 9090.

Correct:

```bash
docker run -p 8080:8080 order-service
```

Debug:

```bash
docker ps

docker exec -it order-service sh
netstat -tulnp
curl localhost:8080/actuator/health
```

---

## 26. Debugging Playbook

When service communication fails, do not guess. Follow the path.

```text
Client
  |
DNS
  |
Network route
  |
Port
  |
Application
```

Checklist:

```text
1. Is container running?
2. Is app listening inside container?
3. Is correct port exposed/published?
4. Are containers on same network?
5. Does DNS resolve service name?
6. Can TCP connect?
7. Does HTTP endpoint respond?
8. Are env variables correct?
9. Is app binding to 0.0.0.0, not only 127.0.0.1?
10. Are logs showing startup failure?
```

Commands:

```bash
docker ps

docker logs order-service

docker inspect order-service

docker network ls

docker network inspect app-net

docker exec -it order-service sh

getent hosts user-service

curl http://user-service:8080/actuator/health

nc -vz postgres 5432
```

---

## 27. Debugging Flowchart

```text
Service call failing?
        |
        v
Is caller container running?
        |
        +-- no --> docker logs caller
        |
        v
yes
        |
        v
Can DNS resolve target name?
        |
        +-- no --> check network / service name
        |
        v
yes
        |
        v
Can TCP connect to target port?
        |
        +-- no --> check target port / app binding
        |
        v
yes
        |
        v
HTTP returns error?
        |
        +-- yes --> check app logs / config
        |
        v
Network path is okay
```

---

## 28. Binding To 127.0.0.1 vs 0.0.0.0

Inside a container, applications should listen on:

```text
0.0.0.0
```

not only:

```text
127.0.0.1
```

Meaning:

```text
127.0.0.1 = only inside same container
0.0.0.0 = accept from container network interface
```

Spring Boot usually binds correctly by default, but explicit config can break it.

Bad:

```yaml
server:
  address: 127.0.0.1
  port: 8080
```

Good:

```yaml
server:
  address: 0.0.0.0
  port: 8080
```

ASCII:

```text
Other Container
      |
      v
container eth0:8080
      |
      X app only listening on 127.0.0.1
```

---

## 29. Interview Answers

### What is Docker networking?

Docker networking is the virtual networking layer that allows containers to communicate with each other, with the host, and with external systems while preserving isolation using Linux network namespaces, virtual ethernet pairs, bridges, DNS, NAT, and port publishing.

### What is docker0?

`docker0` is the default Linux bridge created by Docker. Containers connected to the default bridge attach through veth pairs and receive private IP addresses, usually in the 172.17.x.x range.

### What is a veth pair?

A veth pair is like a virtual cable. One end is placed inside the container as eth0, and the other end stays on the host and connects to a bridge.

### Why should containers use service names instead of IP addresses?

Container IPs are dynamic and may change after restart or recreation. Docker DNS keeps service names stable, so `http://user-service:8080` is better than `http://172.18.0.3:8080`.

### Why does localhost fail inside Docker?

Inside a container, localhost points to the container itself, not the host and not another container. To call another container, use the Docker Compose service name or Docker network alias.

### What does `-p 8080:80` mean?

It maps host port 8080 to container port 80. Requests to the host on port 8080 are forwarded to port 80 inside the container.

### Bridge vs host network?

Bridge mode gives isolation and uses NAT/port publishing. Host mode shares the host network stack directly, reducing isolation and creating port conflict risk.

---

## 30. Mini Cheat Sheet

```text
Container              = small computer
Network namespace      = private network room
IP address             = house address
Port                   = door
DNS                    = phone book
Bridge                 = router/switch
Veth pair              = virtual cable
NAT                    = reception desk forwarding
Docker Compose network = private microservice city
```

Commands:

```bash
# list networks
docker network ls

# inspect network
docker network inspect bridge

# create network
docker network create app-net

# run container on network
docker run --network app-net --name order-service order:1.0

# publish port
docker run -p 8080:8080 order-service

# inspect container IP
docker inspect order-service

# test DNS from container
docker exec -it order-service getent hosts user-service

# test HTTP
docker exec -it order-service curl http://user-service:8080/actuator/health
```

---

## 31. One Picture To Remember

```text
                              Internet
                                  |
                                  v

+----------------------------------------------------------------+
| Host Machine                                                   |
|                                                                |
| Browser                                                        |
|   |                                                            |
|   | localhost:8080                                             |
|   v                                                            |
| Host Port 8080                                                 |
|   |                                                            |
|   v                                                            |
| Docker NAT / iptables                                          |
|   |                                                            |
|   v                                                            |
| +------------------------ docker0 / app-net -----------------+ |
| |                                                            | |
| |  +----------------+      +----------------+                | |
| |  | order-service  | ---> | user-service   |                | |
| |  | 172.18.0.2     | DNS  | 172.18.0.3     |                | |
| |  +----------------+      +----------------+                | |
| |          |                                                 | |
| |          +--------------> redis:6379                       | |
| |          |                                                 | |
| |          +--------------> postgres:5432                    | |
| |                                                            | |
| +------------------------------------------------------------+ |
|                                                                |
+----------------------------------------------------------------+
```

Remember:

```text
Use service names inside Docker networks.
Use localhost only from the host machine.
Publish ports only when external access is needed.
Debug from caller container toward target container.
```

---

## 32. Final Takeaways

1. Docker networking is Linux networking made developer-friendly.
2. Containers get isolated network namespaces.
3. Veth pairs connect containers to the host bridge.
4. docker0/custom bridges connect containers together.
5. Port mapping exposes container ports to the host.
6. Docker DNS lets services call each other by name.
7. `localhost` inside a container means the container itself.
8. Spring Boot should use environment-based service URLs.
9. Redis/Postgres should be reached by Compose service name.
10. Debugging should follow packet path: DNS -> network -> port -> app.

