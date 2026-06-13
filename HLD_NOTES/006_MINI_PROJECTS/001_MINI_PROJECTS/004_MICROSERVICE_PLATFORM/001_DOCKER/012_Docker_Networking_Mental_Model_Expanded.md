# 012_Docker_Networking_Mental_Model_Expanded

> MiniDocker Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real-World Mental Models • Java/Spring Boot • Production Debugging

---

## 0. How To Read This Chapter

This chapter is not for memorizing Docker commands. It is for building a mental picture that stays with you during interviews, production debugging, and Kubernetes learning.

The old short model was correct but too compressed. This expanded version teaches Docker networking like this:

```text
Mental Model
    -> ASCII Diagram
        -> Packet Dry Run
            -> Real Spring Boot Example
                -> Production Failure Story
                    -> Debugging Mindset
```

Remember one rule:

```text
Docker networking is not magic.
It is Linux networking + namespaces + virtual cables + bridges + NAT + DNS.
```

When a container starts, Docker does not create a fake internet. It wires the container into the host using Linux primitives. The container thinks it owns a network card, IP address, routing table, and ports. In reality, those are isolated inside its network namespace.

---

## 1. Understanding First: Why Docker Networking Exists

A container is just a process. But modern applications are not one process anymore.

A Spring Boot order service usually needs:

```text
Order Service
   |
   +--> User Service
   +--> Payment Service
   +--> Redis
   +--> PostgreSQL
   +--> Kafka
```

Without networking, every container becomes an isolated island.

```text
+----------------+        X        +----------------+
| order-service  |                 | user-service   |
| 172.18.0.2     |                 | 172.18.0.3     |
+----------------+                 +----------------+

Problem:
Order cannot call User.
Microservices cannot exist.
```

Docker networking solves three core problems:

```text
1. Connectivity
   Can container A reach container B?

2. Isolation
   Can unrelated containers be separated?

3. Discovery
   Can order-service find user-service without hardcoding IP?
```

Real-world analogy:

```text
City       = Docker network
House      = Container
Address    = IP address
Door       = Port
Phone book = DNS
Road       = veth pair / bridge path
Security   = firewall / iptables rules
```

Do not memorize bridge, host, overlay first. Ask: "Who needs to talk to whom, and through which door?"

---

## 2. The Not-To-Memorize Model

Many engineers memorize terms like this:

```text
bridge = default network
host = no isolation
none = no network
overlay = multi-host
```

That helps in MCQs, but it fails during real debugging.

A better model:

```text
+--------------------------------------------------+
| Host Machine                                     |
|                                                  |
|  +---------------- Docker City ----------------+ |
|  |                                             | |
|  |  +-------------+       +-------------+      | |
|  |  | Order House | <---> | User House  |      | |
|  |  | IP + Port   |       | IP + Port   |      | |
|  |  +-------------+       +-------------+      | |
|  |                                             | |
|  |  Phone Book: user-service -> 172.18.0.3     | |
|  +---------------------------------------------+ |
+--------------------------------------------------+
```

If order-service calls `http://user-service:8080`, Docker DNS resolves the name to the current container IP. If user-service restarts and gets a new IP, the name still works.

Bad thinking:

```text
I need to remember the IP.
```

Good thinking:

```text
I need a stable name, and Docker DNS gives that inside a user-defined network.
```

---

## 3. One Big Picture To Remember

```text
                         Internet / Browser
                                |
                                | http://localhost:8080
                                v
+----------------------------------------------------------------+
| Host Machine                                                   |
|                                                                |
|  eth0 / wlan0                                                  |
|      |                                                         |
|      v                                                         |
|  Host Port 8080                                                |
|      |                                                         |
|      v                                                         |
|  iptables / NAT rule                                           |
|      |                                                         |
|      v                                                         |
|  docker0 or custom bridge                                      |
|      |                                                         |
|      +--------------------+--------------------+               |
|                           |                    |               |
|                        vethA                vethB              |
|                           |                    |               |
|                 +---------v------+    +-------v--------+       |
|                 | order-service  |    | user-service   |       |
|                 | 172.18.0.2     |    | 172.18.0.3     |       |
|                 | port 8080      |    | port 8080      |       |
|                 +----------------+    +----------------+       |
|                           |                    |               |
|                           +------ DNS ---------+               |
|                                user-service                    |
+----------------------------------------------------------------+
```

This picture explains most Docker networking questions:

```text
External client -> host port -> NAT -> bridge -> veth -> container port
Container A -> DNS -> container B IP -> bridge -> veth -> container B port
```

---

## 4. Network Namespace: The Container’s Private Network World

A network namespace is a separate network universe. Inside it, the process sees its own interfaces, IPs, routes, and ports.

```text
Host Network Namespace
+------------------------------------------------+
| interfaces: eth0, docker0, vethxxxx            |
| routes: default via router                     |
| ports: host ports                              |
+------------------------------------------------+

Container Network Namespace
+------------------------------------------------+
| interfaces: eth0, lo                           |
| routes: default via bridge gateway             |
| ports: container ports                         |
+------------------------------------------------+
```

The container is not lying when it says it has `eth0`. It really has an interface, but that interface exists inside its namespace.

```text
Container View

$ ip addr
lo      127.0.0.1
eth0    172.18.0.2

Host View

$ ip addr
eth0       real host network
br-xxxx    docker bridge
vethabcd   cable endpoint to container
```

Mental model:

```text
Network namespace = private room
Interface         = network socket on the wall
Veth pair         = cable connecting rooms
Bridge            = switch/router in corridor
```

---

## 5. Veth Pair: The Virtual Cable

A veth pair has two ends. Whatever enters one end exits the other end.

```text
+---------------------+        virtual cable        +----------------------+
| Container Namespace | <=========================> | Host Namespace       |
|                     |                             |                      |
| eth0                |                             | veth7ab              |
+---------------------+                             +----------+-----------+
                                                               |
                                                               v
                                                          Docker Bridge
```

When Docker starts a container on a bridge network, it usually does this flow:

```text
1. Create network namespace
2. Create veth pair
3. Move one end into container namespace
4. Rename container side to eth0
5. Attach host side to bridge
6. Assign IP to container eth0
7. Add default route via bridge gateway
```

Dry run:

```text
order-service starts
   |
   v
Docker creates eth0 inside container
   |
   v
Docker connects eth0 to host-side veth
   |
   v
host-side veth joins bridge br-app
   |
   v
container receives IP 172.18.0.2
```

Do not memorize the command. Remember: container gets a private network card connected by a virtual cable to a virtual switch.

---

## 6. Docker Bridge: The Virtual Switch / Router

The Docker bridge connects containers on the same network.

```text
                  Custom Bridge: app-net
                 +----------------------+
                 | Gateway 172.18.0.1   |
                 | DNS enabled          |
                 +----------+-----------+
                            |
         +------------------+------------------+
         |                  |                  |
         v                  v                  v
+----------------+  +----------------+  +----------------+
| order-service  |  | user-service   |  | redis          |
| 172.18.0.2     |  | 172.18.0.3     |  | 172.18.0.4     |
+----------------+  +----------------+  +----------------+
```

Bridge behaves like a small LAN.

```text
Same bridge network:
order-service -> user-service works
order-service -> redis works

Different bridge networks:
order-service -> admin-db may not work unless connected to same network
```

Production lesson:

```text
Use user-defined bridge networks, not only the default bridge.
```

Why?

```text
Default bridge:
- weaker service-name DNS behavior
- more accidental mixing
- less explicit architecture

User-defined bridge:
- clean service discovery
- better isolation
- readable docker compose design
```

---

## 7. Default Bridge vs Custom Bridge

Default bridge:

```bash
docker run nginx
```

Custom bridge:

```bash
docker network create app-net
docker run --network app-net --name user-service user-service:1.0
docker run --network app-net --name order-service order-service:1.0
```

Mental difference:

```text
Default bridge = shared public dormitory
Custom bridge  = private apartment for one application stack
```

ASCII comparison:

```text
Default Bridge
+------------------------------------------------+
| bridge                                         |
| nginx, random-test, old-container, app1, app2  |
+------------------------------------------------+

Custom App Network
+------------------------------------------------+
| app-net                                        |
| order-service, user-service, redis, postgres   |
+------------------------------------------------+
```

For serious local development and production-like Compose setups, prefer explicit networks.

---

## 8. Port Mapping: Host Door To Container Door

Command:

```bash
docker run -p 8080:80 nginx
```

Meaning:

```text
Host Port 8080 ---> Container Port 80
```

ASCII:

```text
Browser
  |
  | http://localhost:8080
  v
+---------------------- Host ----------------------+
| Port 8080                                        |
|    |                                             |
|    v                                             |
| NAT / forwarding rule                            |
|    |                                             |
|    v                                             |
| docker bridge                                    |
|    |                                             |
|    v                                             |
| Container nginx:80                               |
+--------------------------------------------------+
```

Important mistake:

```text
-p 8080:80 does NOT mean container exposes 8080.
It means host 8080 forwards to container 80.
```

Spring Boot example:

```bash
docker run -p 8080:8080 order-service:1.0
```

Here both sides are 8080:

```text
Host 8080 -> Container 8080
```

But they are still different network namespaces. Host port and container port are not the same object.

---

## 9. Packet Dry Run: Browser To Spring Boot Container

Assume:

```text
Host: localhost:8080
Container: order-service:8080
```

Journey:

```text
1. Browser sends HTTP request to localhost:8080
2. Host kernel receives packet on port 8080
3. Docker NAT rule checks mapping
4. Rule says: forward to 172.18.0.2:8080
5. Packet enters bridge network
6. Packet crosses host-side veth
7. Packet exits container eth0
8. Spring Boot Tomcat receives request on port 8080
9. Response travels back through the reverse path
```

Diagram:

```text
Browser
  |
  v
Host Network Namespace
  |
  v
iptables DNAT: localhost:8080 -> 172.18.0.2:8080
  |
  v
Docker Bridge
  |
  v
veth pair
  |
  v
Container eth0
  |
  v
Spring Boot embedded Tomcat
```

Debugging implication:

```text
If browser cannot connect, the failure may be at:
- application not listening
- wrong container port
- missing -p mapping
- firewall / iptables
- container crash
- app bound only to 127.0.0.1 incorrectly
```

---

## 10. Container-To-Container Communication

Inside the same Docker network, containers usually do not need host port mapping to talk to each other.

Bad mental model:

```text
order-service -> localhost:8081 -> user-service
```

This is wrong because inside order-service:

```text
localhost = order-service itself
```

Correct:

```text
order-service -> http://user-service:8080
```

ASCII:

```text
+----------------+       DNS lookup        +----------------+
| order-service  | ----------------------> | Docker DNS     |
|                |                         | user-service   |
+-------+--------+                         +-------+--------+
        |                                          |
        | 172.18.0.3                               |
        v                                          |
+----------------+                                 |
| user-service   | <-------------------------------+
| port 8080      |
+----------------+
```

Rule:

```text
Inside containers:
- use service names for other containers
- use container ports, not host mapped ports
```

Example:

```text
Host mapping:
user-service host 8081 -> container 8080

Order should call:
http://user-service:8080

Not:
http://localhost:8081
```

---

## 11. Docker DNS: The Phone Book

Docker DNS turns service names into IPs.

```text
user-service -> 172.18.0.3
redis        -> 172.18.0.4
postgres     -> 172.18.0.5
```

When a container restarts:

```text
Before restart:
user-service -> 172.18.0.3

After restart:
user-service -> 172.18.0.9
```

If order-service uses the name, it survives the IP change.

```text
order-service
  |
  | http://user-service:8080/api/users/10
  v
Docker DNS
  |
  | 172.18.0.9
  v
user-service
```

Production lesson:

```text
Hardcoded IP is fragile.
Stable DNS name is resilient.
```

---

## 12. Spring Boot Example: Correct Service-To-Service Call

Order service should call user service by DNS name.

```java
package com.example.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String userServiceBaseUrl;

    public UserClient(@Value("${clients.user-service.base-url}") String userServiceBaseUrl) {
        this.userServiceBaseUrl = userServiceBaseUrl;
    }

    public UserDto getUser(Long userId) {
        String url = userServiceBaseUrl + "/api/users/" + userId;
        return restTemplate.getForObject(url, UserDto.class);
    }
}
```

`application.yml`:

```yaml
clients:
  user-service:
    base-url: http://user-service:8080
```

Docker Compose:

```yaml
services:
  order-service:
    image: order-service:1.0
    ports:
      - "8080:8080"
    environment:
      CLIENTS_USER_SERVICE_BASE_URL: http://user-service:8080
    networks:
      - app-net

  user-service:
    image: user-service:1.0
    expose:
      - "8080"
    networks:
      - app-net

networks:
  app-net:
    driver: bridge
```

Notice:

```text
order-service calls user-service:8080
Host browser calls localhost:8080
```

Different callers use different addresses.

---

## 13. Redis Example: Internal Port, Not Host Port

Docker Compose:

```yaml
services:
  order-service:
    image: order-service:1.0
    environment:
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379
    networks:
      - app-net

  redis:
    image: redis:7
    expose:
      - "6379"
    networks:
      - app-net
```

Spring Boot Redis config:

```yaml
spring:
  data:
    redis:
      host: redis
      port: 6379
```

Diagram:

```text
order-service container
  |
  | redis:6379
  v
Docker DNS
  |
  | redis -> 172.18.0.4
  v
redis container port 6379
```

You do not need `ports: "6379:6379"` unless the host machine needs direct Redis access.

Production mindset:

```text
Expose externally only what external clients need.
Internal dependencies should stay internal.
```

---

## 14. PostgreSQL Example: Avoid localhost Trap

Wrong inside container:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/orders
```

Why wrong?

```text
Inside order-service container:
localhost = order-service container
Not postgres container
Not host machine
```

Correct:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/orders
    username: app
    password: secret
```

Compose:

```yaml
services:
  order-service:
    image: order-service:1.0
    depends_on:
      - postgres
    networks:
      - app-net

  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: orders
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
    volumes:
      - pgdata:/var/lib/postgresql/data
    networks:
      - app-net

volumes:
  pgdata:

networks:
  app-net:
```

Dry run:

```text
order-service starts
  |
  v
reads JDBC URL postgres:5432
  |
  v
Docker DNS resolves postgres
  |
  v
TCP connection to postgres container port 5432
  |
  v
PostgreSQL accepts login
```

---

## 15. Expose vs Ports

`expose` documents and makes a port available to linked/internal services, but it does not publish to the host.

```yaml
user-service:
  expose:
    - "8080"
```

`ports` publishes to host.

```yaml
order-service:
  ports:
    - "8080:8080"
```

Mental model:

```text
expose = internal office door visible inside building
ports  = public street entrance visible outside building
```

ASCII:

```text
Host Browser
    |
    | works only if ports is used
    v
order-service

order-service
    |
    | works with internal network + container port
    v
user-service
```

Rule:

```text
Frontend/API gateway: usually ports
Internal microservice: usually expose or no explicit port publication
Redis/Postgres/Kafka: publish only for local debugging, not by default
```

---

## 16. Host Network Mode

Host network mode removes the separate container network namespace for networking.

```text
Bridge mode:
Container network namespace -> bridge -> host

Host mode:
Container uses host network namespace directly
```

Diagram:

```text
Bridge Mode
+---------------- Host ----------------+
| host ports                            |
| docker bridge                         |
|   |                                   |
| container eth0                        |
+---------------------------------------+

Host Mode
+---------------- Host ----------------+
| container process binds directly here |
| no bridge path for that container     |
+---------------------------------------+
```

Pros:

```text
- lower networking overhead
- useful for special network agents
- easier access to host interfaces
```

Cons:

```text
- weaker isolation
- port conflicts with host processes
- less portable across environments
```

Interview answer:

```text
Host networking is useful when performance or direct host network access matters, but it sacrifices isolation and can create port conflicts. Most application containers should use bridge or orchestrator networking instead.
```

---

## 17. None Network Mode

None network means Docker gives the container no external networking.

```text
Container
+-----------------------+
| lo only               |
| no eth0               |
| no bridge             |
| no internet           |
+-----------------------+
```

Useful for:

```text
- offline batch processing
- secure file transformations
- tests that must not call network
- malware/sandbox style isolation
```

Example:

```bash
docker run --network none image-processor:1.0
```

Mental model:

```text
A locked room with no phone and no road.
```

---

## 18. Overlay Network: Multi-Host Mental Model

Bridge works on one host. Overlay connects containers across multiple hosts.

```text
Host A                                      Host B
+-----------------------+                  +-----------------------+
| order-service         |                  | user-service          |
| 10.0.1.2              |                  | 10.0.1.9              |
|        |              |                  |        |              |
|        +---- overlay network tunnel -----+        |              |
+-----------------------+                  +-----------------------+
```

Mental model:

```text
Overlay = private road built above real roads.
```

The packet may travel through the real physical network, but containers see one logical network.

```text
Container view:
order -> user directly

Reality:
order -> host A -> physical network -> host B -> user
```

Kubernetes uses a similar idea through CNI plugins. Docker Swarm overlay and Kubernetes pod networking are not identical, but the mental model transfers well.

---

## 19. Kubernetes Connection: Docker Container vs Kubernetes Pod

Docker local:

```text
Container gets IP
Container joins bridge
Container uses Docker DNS
```

Kubernetes:

```text
Pod gets IP
Pod joins cluster network
Pod uses CoreDNS
Service gives stable virtual IP/name
```

Comparison:

```text
Docker Compose                     Kubernetes
-----------------------------------------------------------
service name: user-service          Service DNS: user-service
container IP can change             pod IP can change
custom bridge network               cluster pod network
ports for host access               NodePort/LoadBalancer/Ingress
```

Kubernetes mental picture:

```text
Client
  |
  v
Kubernetes Service: user-service.default.svc.cluster.local
  |
  +--------+---------+
           |         |
           v         v
        Pod IP     Pod IP
```

The reason Kubernetes Service exists is the same reason Docker DNS names matter: IPs are unstable, names are stable.

---

## 20. Production Story: Hardcoded IP Failure

Bad design:

```yaml
clients:
  user-service:
    base-url: http://172.18.0.3:8080
```

Initial state:

```text
user-service = 172.18.0.3
order-service works
```

After restart:

```text
old user-service removed
new user-service = 172.18.0.8
order-service still calls 172.18.0.3
```

Failure:

```text
Connection refused
Timeout
5xx errors from order API
```

Correct design:

```yaml
clients:
  user-service:
    base-url: http://user-service:8080
```

Now restart is safe:

```text
user-service name -> new IP
order-service still calls same name
```

Debugging lesson:

```text
When container-to-container calls fail, first check whether someone hardcoded localhost, host port, or container IP.
```

---

## 21. Production Story: Wrong Port Failure

Compose:

```yaml
user-service:
  ports:
    - "8081:8080"
```

Host can call:

```text
http://localhost:8081
```

Order container should call:

```text
http://user-service:8080
```

Common bug:

```text
order-service calls http://user-service:8081
```

Why it fails:

```text
8081 is host port.
Inside Docker network, user-service listens on container port 8080.
```

Picture:

```text
Host side:
localhost:8081 -> user-service:8080

Container side:
order-service -> user-service:8080
```

Interview-grade explanation:

```text
Port publishing creates a mapping from host namespace to container namespace. It does not change the port on which the application listens inside the container network.
```

---

## 22. Production Story: localhost Trap

Inside a container:

```text
localhost means this same container.
```

If order-service uses:

```text
http://localhost:8080
```

It calls itself.

```text
+----------------------+
| order-service        |
|                      |
| localhost:8080 ------+---- loops back to order-service
+----------------------+
```

It does not call user-service.

Correct:

```text
http://user-service:8080
```

For host machine access from a container, Docker Desktop often supports:

```text
host.docker.internal
```

But do not design microservice-to-microservice calls around host access. Use service DNS names inside the Docker network.

---

## 23. Debugging Playbook: Find Where Packet Dies

Use this packet chain:

```text
Client
  |
  v
Host port
  |
  v
NAT / forwarding
  |
  v
Docker bridge
  |
  v
veth pair
  |
  v
Container port
  |
  v
Application
```

Ask questions in order:

```text
1. Is the container running?
2. Is the app listening on the expected port?
3. Is the port published if host access is needed?
4. Are both containers on the same network?
5. Does DNS resolve the service name?
6. Is the target port the container port, not host port?
7. Are firewall rules blocking traffic?
8. Is the app healthy but dependency failing?
```

Commands:

```bash
docker ps
docker logs order-service
docker inspect order-service
docker network ls
docker network inspect app-net
docker exec -it order-service sh
```

Inside container:

```bash
getent hosts user-service
nc -vz user-service 8080
curl -v http://user-service:8080/actuator/health
```

If `curl` works inside the container but browser fails, the issue is probably host port mapping. If browser works but container-to-container fails, the issue is probably DNS/network/port confusion.

---

## 24. Docker Compose Full Example

```yaml
version: "3.9"

services:
  order-service:
    build: ./order-service
    container_name: order-service
    ports:
      - "8080:8080"
    environment:
      CLIENTS_USER_SERVICE_BASE_URL: http://user-service:8080
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/orders
      SPRING_DATASOURCE_USERNAME: app
      SPRING_DATASOURCE_PASSWORD: secret
    depends_on:
      - user-service
      - redis
      - postgres
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

  postgres:
    image: postgres:16
    container_name: postgres
    environment:
      POSTGRES_DB: orders
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
    volumes:
      - pgdata:/var/lib/postgresql/data
    expose:
      - "5432"
    networks:
      - app-net

networks:
  app-net:
    driver: bridge

volumes:
  pgdata:
```

Architecture:

```text
Browser
  |
  | localhost:8080
  v
order-service
  |
  +--> user-service:8080
  +--> redis:6379
  +--> postgres:5432
```

Only order-service is published to host. Internal dependencies stay private.

---

## 25. Java Controller Dry Run

Order API:

```java
package com.example.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final UserClient userClient;

    public OrderController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping("/api/orders/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId) {
        Long userId = 101L;
        UserDto user = userClient.getUser(userId);
        return new OrderResponse(orderId, userId, user.name(), "CREATED");
    }
}
```

Dry run:

```text
Browser calls localhost:8080/api/orders/1
  |
  v
Docker forwards host 8080 to order-service container 8080
  |
  v
OrderController receives request
  |
  v
UserClient calls http://user-service:8080/api/users/101
  |
  v
Docker DNS resolves user-service
  |
  v
user-service returns UserDto
  |
  v
order-service returns OrderResponse
```

This is the exact mental model used in real microservices.

---

## 26. Health Checks And Startup Timing

`depends_on` does not always mean the dependency is ready. It often means the dependency container started.

```text
postgres container started
  |
  v
PostgreSQL still initializing
  |
  v
order-service starts too early
  |
  v
connection refused
```

Better approach:

```yaml
postgres:
  image: postgres:16
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U app -d orders"]
    interval: 5s
    timeout: 3s
    retries: 10
```

Spring Boot should also retry database connections through pool configuration and readiness checks.

Production lesson:

```text
Networking success does not mean application readiness.
TCP port open does not mean service is logically ready.
```

Debug in layers:

```text
DNS resolves?        network layer okay
TCP connects?        port layer okay
HTTP health is UP?   application layer okay
Business call works? dependency/data layer okay
```

---

## 27. Security Mindset

Bad Compose design:

```yaml
redis:
  ports:
    - "6379:6379"
postgres:
  ports:
    - "5432:5432"
```

This exposes internal databases to the host network.

Better:

```yaml
redis:
  expose:
    - "6379"
postgres:
  expose:
    - "5432"
```

Security model:

```text
Public entry:
API gateway / order-service only

Private services:
Redis, Postgres, internal microservices
```

ASCII:

```text
Internet / Browser
       |
       v
+---------------+
| API Service   |  public door
+-------+-------+
        |
        | private Docker network
        v
+-------+-------+     +----------+
| PostgreSQL    |     | Redis    |
| no public door|     | internal |
+---------------+     +----------+
```

Principle:

```text
Expose minimum doors.
Keep internal dependencies inside the private network.
```

---

## 28. Interview Answers

### What is Docker networking?

Docker networking is the layer that allows containers to communicate with each other, the host, and external networks using Linux networking primitives such as network namespaces, virtual Ethernet pairs, bridges, NAT, and DNS.

### What is a bridge network?

A bridge network is like a virtual switch or router on the host. Containers connected to the same bridge can communicate using container IPs or DNS names.

### What is a veth pair?

A veth pair is a virtual cable. One end is placed inside the container namespace as `eth0`, and the other end remains on the host and connects to the Docker bridge.

### Why should containers use service names instead of IPs?

Container IPs can change when containers restart. Service names are stable and resolved by Docker DNS to the current IP.

### What is port mapping?

Port mapping forwards traffic from a host port to a container port. For example, `-p 8080:80` forwards host port 8080 to container port 80.

### Why does localhost fail inside containers?

Inside a container, `localhost` refers to the same container, not the host and not another container. For another service, use the service name on the Docker network.

### Bridge vs host network?

Bridge networking gives isolation and explicit port publishing. Host networking gives direct access to the host network but reduces isolation and can cause port conflicts.

### Docker networking vs Kubernetes networking?

Docker usually connects containers through bridge networks locally. Kubernetes gives each pod an IP and uses Services plus CoreDNS for stable discovery across a cluster.

---

## 29. Debugging Cheat Sheet

```text
Symptom: Browser cannot access localhost:8080
Check:
[ ] docker ps shows port mapping?
[ ] app listens on container port?
[ ] logs show app started?
[ ] firewall blocking?

Symptom: order cannot call user-service
Check:
[ ] same Docker network?
[ ] correct DNS name?
[ ] using container port, not host port?
[ ] user-service health endpoint works?

Symptom: Redis connection refused
Check:
[ ] Redis container running?
[ ] host is redis, not localhost?
[ ] port is 6379?
[ ] same network?

Symptom: Works on host but fails in container
Check:
[ ] localhost confusion?
[ ] missing Docker DNS?
[ ] host.docker.internal needed?
```

Commands:

```bash
# list networks
docker network ls

# inspect network members
docker network inspect app-net

# inspect container IP and network
docker inspect order-service

# shell into container
docker exec -it order-service sh

# DNS check
getent hosts user-service

# TCP check
nc -vz user-service 8080

# HTTP check
curl -v http://user-service:8080/actuator/health
```

---

## 30. One Picture To Remember Forever

```text
                         Outside World
                              |
                              | localhost:8080
                              v
+------------------------------------------------------------------+
| Host Machine                                                     |
|                                                                  |
|  Host network namespace                                          |
|       |                                                          |
|       v                                                          |
|  Port publishing / NAT                                           |
|       |                                                          |
|       v                                                          |
|  Docker Bridge: app-net                                          |
|       |                                                          |
|       +-------------------+-------------------+----------------+ |
|                           |                   |                | |
|                         veth                veth             veth|
|                           |                   |                | |
|                           v                   v                v |
|                  +----------------+  +----------------+ +-------+|
|                  | order-service  |  | user-service   | | redis ||
|                  | 172.18.0.2     |  | 172.18.0.3     | | .0.4  ||
|                  | port 8080      |  | port 8080      | | 6379  ||
|                  +----------------+  +----------------+ +-------+|
|                           |                   ^                | |
|                           | DNS: user-service |                | |
|                           +-------------------+                | |
+------------------------------------------------------------------+

Rules:
1. Host uses localhost:published-port.
2. Containers use service-name:container-port.
3. localhost inside a container means that same container.
4. IPs change; names should stay.
5. Expose only public entrypoints.
```

---

## 31. Final Production Checklist

```text
Design
[ ] Use custom bridge network per application stack
[ ] Use service names, not hardcoded IPs
[ ] Use container ports for container-to-container calls
[ ] Publish only public services to host
[ ] Keep Redis/Postgres/internal services private

Spring Boot
[ ] Externalize URLs through application.yml/env vars
[ ] Use http://user-service:8080, not localhost
[ ] Add actuator health endpoints
[ ] Configure retry/timeouts for dependencies
[ ] Do not hide network problems with infinite retries

Debugging
[ ] docker ps
[ ] docker logs
[ ] docker network inspect
[ ] getent hosts service-name
[ ] nc -vz service-name port
[ ] curl actuator health

Interview
[ ] Explain namespace
[ ] Explain veth pair
[ ] Explain bridge
[ ] Explain NAT and port mapping
[ ] Explain DNS and why IPs are fragile
[ ] Explain localhost trap
[ ] Connect Docker model to Kubernetes Service/CoreDNS
```

---

## 32. Final Takeaways

Docker networking is easiest when you stop memorizing names and start tracing packets.

```text
Container = isolated process with private network world
Network namespace = private network room
Veth pair = virtual cable
Bridge = virtual switch/router
DNS = phone book
Port mapping = host door to container door
NAT = address/door translation
```

The most important production rule:

```text
External users call host published ports.
Containers call service names on container ports.
```

If you remember that, most Docker networking bugs become simple.
