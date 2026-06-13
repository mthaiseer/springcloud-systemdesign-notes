# 018_DockerCompose_Microservice_Stack

> MiniDocker Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize

---

# 1. Why Docker Compose Exists

When you run one container, `docker run` is enough.

But real backend systems are not one container.

A Spring Boot application usually needs:

```text
Spring Boot API
PostgreSQL
Redis
Kafka maybe
Prometheus maybe
Admin UI maybe
```

Without Docker Compose, you manually run many commands:

```bash
docker network create app-net

docker run --name postgres ...
docker run --name redis ...
docker run --name user-service ...
docker run --name order-service ...
```

That becomes painful because each container needs:

```text
Image
Container name
Ports
Environment variables
Network
Volumes
Startup order
Health checks
Restart policy
```

Docker Compose is a **single blueprint file** for a multi-container local system.

Mental model:

```text
Dockerfile  = Recipe for ONE service image
Compose     = City map for MANY services
```

One service:

```text
Dockerfile
   |
   v
Spring Boot Image
```

Many services:

```text
compose.yml
   |
   +--> user-service
   +--> order-service
   +--> postgres
   +--> redis
   +--> rabbitmq
```

Do not memorize Compose syntax first. Understand the problem:

```text
Many containers need to live together.
Compose describes their shared world.
```

---

# 2. Not-To-Memorize Model

Do NOT memorize:

```text
services
networks
volumes
depends_on
environment
ports
healthcheck
```

Instead remember this real-world model:

```text
Microservice Stack = Apartment Complex

Service container = Apartment
Network           = Internal road
Volume            = Storage room
Environment       = Notice board / configuration
Port mapping      = Public gate
DNS name          = Apartment name
Healthcheck       = Security guard checking if alive
Compose file      = Building plan
```

ASCII model:

```text
+----------------------------------------------------+
| Docker Compose Project                             |
|                                                    |
|  Internal Network: app-net                         |
|                                                    |
|  +-------------+      +-------------+              |
|  | user-api    | ---> | postgres    |              |
|  +-------------+      +-------------+              |
|         |                    |                     |
|         v                    v                     |
|  +-------------+      +-------------+              |
|  | order-api   | ---> | redis       |              |
|  +-------------+      +-------------+              |
|                                                    |
+----------------------------------------------------+
```

The important idea:

```text
Containers should not find each other by IP.
They find each other by service name.
```

Bad:

```text
order-service -> 172.18.0.5:5432
```

Good:

```text
order-service -> postgres:5432
order-service -> redis:6379
user-service  -> order-service:8080
```

Because containers restart and IPs change.

Names stay stable inside the Compose network.

---

# 3. One Picture To Remember

```text
                         Browser / Postman
                               |
                               | localhost:8080
                               v
+-------------------------------------------------------------------+
| Host Machine                                                       |
|                                                                   |
|  Port Mapping                                                     |
|  8080 on host  --->  8080 inside api-gateway                      |
|                                                                   |
|  +-------------------------------------------------------------+  |
|  | Docker Compose Network: mini-app-net                        |  |
|  |                                                             |  |
|  |   +---------------+                                         |  |
|  |   | api-gateway   |                                         |  |
|  |   +-------+-------+                                         |  |
|  |           | service DNS: user-service                       |  |
|  |           v                                                 |  |
|  |   +---------------+        +---------------+                |  |
|  |   | user-service  | -----> | postgres      |                |  |
|  |   +-------+-------+        +-------+-------+                |  |
|  |           |                        |                        |  |
|  |           | redis:6379             | volume: pg-data        |  |
|  |           v                        v                        |  |
|  |   +---------------+        +---------------+                |  |
|  |   | redis         |        | disk storage  |                |  |
|  |   +---------------+        +---------------+                |  |
|  |                                                             |  |
|  +-------------------------------------------------------------+  |
+-------------------------------------------------------------------+
```

Remember:

```text
Only public entrypoints need host ports.
Internal services communicate by DNS names.
Databases need volumes.
Configuration comes from environment variables.
```

---

# 4. Docker Compose vs Dockerfile

A common beginner confusion:

```text
Dockerfile and Compose are not the same thing.
```

Dockerfile answers:

```text
How do I build ONE container image?
```

Compose answers:

```text
How do I run MANY containers together?
```

Diagram:

```text
Dockerfile
   |
   | builds
   v
+---------------------+
| user-service image  |
+---------------------+

compose.yml
   |
   | runs
   v
+---------------------+   +-------------+   +---------+
| user-service        |   | postgres    |   | redis   |
+---------------------+   +-------------+   +---------+
```

Spring Boot example:

```text
UserService Dockerfile
OrderService Dockerfile
Gateway Dockerfile

compose.yml connects all of them.
```

Real-world analogy:

```text
Dockerfile = How to build one house
Compose    = How to arrange houses, roads, storage, gates in a city
```

---

# 5. Basic Compose File Mental Model

A Compose file usually has four important areas:

```yaml
services:
  app:
    image: my-app

networks:
  app-net:

volumes:
  pg-data:
```

Mental map:

```text
compose.yml
   |
   +-- services  = running containers
   +-- networks  = communication roads
   +-- volumes   = persistent storage
   +-- configs   = environment values, ports, health checks
```

ASCII:

```text
+--------------------- compose.yml ---------------------+
|                                                       |
| services:                                             |
|   user-service                                        |
|   order-service                                       |
|   postgres                                            |
|   redis                                               |
|                                                       |
| networks:                                             |
|   backend-net                                         |
|                                                       |
| volumes:                                              |
|   pg-data                                             |
|   redis-data                                          |
|                                                       |
+-------------------------------------------------------+
```

Compose is declarative.

You tell Docker:

```text
This is the desired local microservice world.
Please create it.
```

Then Docker creates:

```text
Containers
Networks
Volumes
DNS records
Port mappings
```

---

# 6. First Simple Compose Example

```yaml
services:
  redis:
    image: redis:7
    ports:
      - "6379:6379"
```

This means:

```text
Create one service named redis.
Use redis:7 image.
Expose host port 6379 to container port 6379.
```

Visual:

```text
Host Machine
   |
   | localhost:6379
   v
+-------------------+
| redis container   |
| port 6379         |
+-------------------+
```

Command:

```bash
docker compose up
```

Compose creates a project network automatically even if you do not define one.

```text
Compose Project
   |
   +-- default network
   +-- redis container
```

But for serious learning, define explicit networks.

```yaml
networks:
  backend-net:
```

Because explicit networks make the mental model clear.

---

# 7. Full Microservice Stack Example

We will build this stack:

```text
api-gateway
user-service
order-service
postgres
redis
```

Communication:

```text
Browser -> api-gateway -> user-service -> postgres
                         -> order-service -> redis
```

Architecture:

```text
                       Browser
                          |
                          | localhost:8080
                          v
                   +-------------+
                   | api-gateway |
                   +------+------+ 
                          |
              +-----------+-----------+
              |                       |
              v                       v
       +--------------+        +---------------+
       | user-service |        | order-service |
       +------+-------+        +-------+-------+
              |                        |
              v                        v
       +--------------+        +---------------+
       | postgres     |        | redis         |
       +--------------+        +---------------+
```

Notice:

```text
Only gateway is exposed to host.
Internal services stay private.
```

Why?

Because users should not directly hit:

```text
user-service:8081
order-service:8082
postgres:5432
redis:6379
```

In production, public traffic should enter through controlled entrypoints.

---

# 8. Project Folder Structure

```text
mini-compose-stack/
|
+-- compose.yml
|
+-- gateway/
|   +-- Dockerfile
|   +-- pom.xml
|   +-- src/main/java/...
|
+-- user-service/
|   +-- Dockerfile
|   +-- pom.xml
|   +-- src/main/java/...
|
+-- order-service/
|   +-- Dockerfile
|   +-- pom.xml
|   +-- src/main/java/...
|
+-- .env
```

Mental model:

```text
Each Spring Boot service owns its code and Dockerfile.
The root compose.yml connects them.
```

Diagram:

```text
                compose.yml
                    |
      +-------------+-------------+
      |             |             |
      v             v             v
 gateway/      user-service/   order-service/
 Dockerfile    Dockerfile      Dockerfile
```

This is close to real product company local development.

One command can start the full backend stack:

```bash
docker compose up --build
```

---

# 9. Dockerfile For Spring Boot Services

Simple Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Mental flow:

```text
Base JVM image
   |
Copy Spring Boot JAR
   |
Expose app port
   |
Run java -jar
```

ASCII:

```text
+-------------------------------+
| Spring Boot Image             |
|                               |
|  OS libraries                 |
|  JVM                          |
|  /app/app.jar                 |
|  ENTRYPOINT java -jar app.jar |
+-------------------------------+
```

Better multi-stage Dockerfile:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /src/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Meaning:

```text
Builder stage has Maven.
Runtime stage has only JRE + JAR.
Final image is smaller and cleaner.
```

---

# 10. Complete Compose File

```yaml
services:
  api-gateway:
    build: ./gateway
    container_name: api-gateway
    ports:
      - "8080:8080"
    environment:
      USER_SERVICE_URL: http://user-service:8081
      ORDER_SERVICE_URL: http://order-service:8082
    depends_on:
      user-service:
        condition: service_started
      order-service:
        condition: service_started
    networks:
      - backend-net

  user-service:
    build: ./user-service
    container_name: user-service
    environment:
      SERVER_PORT: 8081
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/appdb
      SPRING_DATASOURCE_USERNAME: app
      SPRING_DATASOURCE_PASSWORD: app
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - backend-net

  order-service:
    build: ./order-service
    container_name: order-service
    environment:
      SERVER_PORT: 8082
      REDIS_HOST: redis
      REDIS_PORT: 6379
    depends_on:
      redis:
        condition: service_healthy
    networks:
      - backend-net

  postgres:
    image: postgres:16
    container_name: postgres
    environment:
      POSTGRES_DB: appdb
      POSTGRES_USER: app
      POSTGRES_PASSWORD: app
    volumes:
      - pg-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U app -d appdb"]
      interval: 5s
      timeout: 3s
      retries: 10
    networks:
      - backend-net

  redis:
    image: redis:7
    container_name: redis
    command: ["redis-server", "--appendonly", "yes"]
    volumes:
      - redis-data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 5s
      timeout: 3s
      retries: 10
    networks:
      - backend-net

networks:
  backend-net:
    driver: bridge

volumes:
  pg-data:
  redis-data:
```

Visual mapping:

```text
services:
  api-gateway   ---> public entrypoint
  user-service  ---> private backend service
  order-service ---> private backend service
  postgres      ---> persistent database
  redis         ---> persistent cache/session/rate-limit store

network:
  backend-net   ---> internal road

volumes:
  pg-data       ---> postgres disk
  redis-data    ---> redis disk
```

---

# 11. Service DNS Deep Dive

Inside Compose, service names become DNS names.

```text
user-service can reach postgres by name:

postgres:5432
```

Docker DNS flow:

```text
user-service
    |
    | DNS lookup: postgres
    v
Docker embedded DNS
    |
    | returns container IP
    v
postgres container
```

ASCII:

```text
+------------------+          DNS          +----------------+
| user-service     | -------------------> | Docker DNS      |
| wants postgres   |                       +--------+-------+
+--------+---------+                                |
         |                                          |
         | IP of postgres                           |
         v                                          v
+------------------+                         +--------------+
| connect to       | ----------------------> | postgres     |
| postgres:5432    |                         | 172.20.0.5   |
+------------------+                         +--------------+
```

Do not use container IPs.

Bad:

```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://172.20.0.5:5432/appdb
```

Good:

```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/appdb
```

Because IPs change after recreate.

---

# 12. Port Mapping Rule

Port mapping format:

```text
HOST_PORT:CONTAINER_PORT
```

Example:

```yaml
ports:
  - "8080:8080"
```

Meaning:

```text
localhost:8080 on host goes to api-gateway:8080 inside container
```

Diagram:

```text
Browser
  |
  | localhost:8080
  v
Host port 8080
  |
  | Docker NAT
  v
api-gateway container port 8080
```

Important rule:

```text
Internal service-to-service calls do NOT need host port mapping.
```

This is enough:

```yaml
user-service:
  expose:
    - "8081"
```

But even `expose` is mostly documentation in Compose. Containers on the same network can talk to each other's listening ports.

Production mindset:

```text
Expose only what must be called from outside.
Keep databases and internal services private.
```

---

# 13. Environment Variables In Compose

Spring Boot config should not be hardcoded inside the image.

Bad:

```java
String dbUrl = "jdbc:postgresql://localhost:5432/appdb";
```

Good:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/appdb
```

Spring Boot automatically maps environment variables:

```text
SPRING_DATASOURCE_URL
        |
        v
spring.datasource.url
```

Diagram:

```text
compose.yml
   |
   | environment variables
   v
Container process
   |
   v
Spring Boot Environment
   |
   v
DataSource configuration
```

Example `application.yml`:

```yaml
server:
  port: ${SERVER_PORT:8080}

spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
```

This makes the same image work in:

```text
local
CI
staging
production
```

Only environment changes.

---

# 14. Using .env File

Instead of hardcoding values in Compose, use `.env`.

`.env`:

```env
POSTGRES_DB=appdb
POSTGRES_USER=app
POSTGRES_PASSWORD=app
REDIS_PORT=6379
```

Compose:

```yaml
postgres:
  image: postgres:16
  environment:
    POSTGRES_DB: ${POSTGRES_DB}
    POSTGRES_USER: ${POSTGRES_USER}
    POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
```

Mental model:

```text
.env = local settings file
compose.yml = stack blueprint
```

ASCII:

```text
.env values
    |
    v
compose.yml interpolation
    |
    v
container environment
    |
    v
application config
```

Warning:

```text
Do not commit real production passwords to Git.
```

For local learning, `.env` is fine.

For real production, use:

```text
Secrets manager
Kubernetes Secrets
Vault
Cloud secret stores
CI/CD secret variables
```

---

# 15. Volumes In Compose

Containers are disposable. Data must survive container deletion.

Postgres without volume:

```text
Container removed
   |
   v
Data gone
```

Postgres with volume:

```text
Container removed
   |
   v
Volume remains
   |
   v
New container reuses data
```

ASCII:

```text
+------------------+       writes        +------------------+
| postgres         | ------------------> | pg-data volume   |
| container        |                     | host-managed disk|
+------------------+                     +------------------+

Delete container? Data remains in pg-data.
```

Compose:

```yaml
volumes:
  - pg-data:/var/lib/postgresql/data
```

Meaning:

```text
Mount named volume pg-data into Postgres data directory.
```

Redis persistence:

```yaml
redis:
  command: ["redis-server", "--appendonly", "yes"]
  volumes:
    - redis-data:/data
```

Mental rule:

```text
Databases need volumes.
Caches may need volumes depending on durability requirement.
Stateless APIs usually do not need volumes.
```

---

# 16. depends_on Is Not Magic

Many developers misunderstand `depends_on`.

It controls startup order, but startup order is not the same as readiness.

Bad assumption:

```text
Postgres container started = Postgres ready for connections
```

Reality:

```text
Postgres process may still be initializing.
```

Without healthcheck:

```text
user-service starts
   |
   | tries DB connection
   v
postgres not ready
   |
   v
connection refused
```

Better:

```yaml
depends_on:
  postgres:
    condition: service_healthy
```

With healthcheck:

```text
Start postgres container
   |
   v
Run pg_isready repeatedly
   |
   v
Mark healthy
   |
   v
Start user-service
```

ASCII:

```text
postgres container started
        |
        v
postgres accepts connections?
        |
   no --+--> wait
        |
       yes
        v
user-service starts
```

Production mindset:

```text
Applications should also retry dependencies.
Do not rely only on Compose startup ordering.
```

---

# 17. Spring Boot Retry For Dependency Startup

Even with healthchecks, microservices should handle temporary failures.

Example using Spring Retry style service logic:

```java
@Service
public class UserWarmupService {

    private final JdbcTemplate jdbcTemplate;

    public UserWarmupService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void verifyDatabase() throws InterruptedException {
        int attempts = 0;

        while (attempts < 10) {
            try {
                Integer result = jdbcTemplate.queryForObject("select 1", Integer.class);
                System.out.println("Database ready: " + result);
                return;
            } catch (Exception ex) {
                attempts++;
                System.out.println("Database not ready, attempt=" + attempts);
                Thread.sleep(2000);
            }
        }

        throw new IllegalStateException("Database not reachable after retries");
    }
}
```

Mental model:

```text
Container orchestration gives best-effort order.
Application resilience handles real distributed timing.
```

Diagram:

```text
Spring Boot starts
   |
   v
Try DB
   |
   +-- success --> ready
   |
   +-- fail ----> wait and retry
```

This matters because in Kubernetes, ECS, Nomad, and real cloud platforms, dependency timing is never perfectly deterministic.

---

# 18. Spring Boot User Service Example

Controller:

```java
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public User create(@RequestBody CreateUserRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        return repository.save(user);
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
```

Entity:

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    // getters and setters
}
```

Repository:

```java
public interface UserRepository extends JpaRepository<User, Long> {
}
```

Compose connection:

```text
UserController
   |
   v
UserRepository
   |
   v
JDBC driver
   |
   v
jdbc:postgresql://postgres:5432/appdb
   |
   v
postgres service
```

---

# 19. Spring Boot Order Service With Redis

Use Redis for idempotency, rate limiting, or temporary order state.

Configuration:

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:redis}
      port: ${REDIS_PORT:6379}
```

Service:

```java
@Service
public class OrderIdempotencyService {

    private final StringRedisTemplate redisTemplate;

    public OrderIdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean claimRequest(String idempotencyKey) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent("idem:" + idempotencyKey, "PROCESSING", Duration.ofMinutes(10));

        return Boolean.TRUE.equals(success);
    }

    public void markCompleted(String idempotencyKey, String orderId) {
        redisTemplate.opsForValue()
                .set("idem:" + idempotencyKey, "COMPLETED:" + orderId, Duration.ofHours(24));
    }
}
```

Flow:

```text
POST /orders
   |
   v
Check idempotency key in Redis
   |
   +-- exists ----> reject duplicate / return previous result
   |
   +-- absent ----> create order
```

Redis DNS:

```text
order-service -> redis:6379
```

Not:

```text
order-service -> localhost:6379
```

Inside a container, `localhost` means the same container, not the host and not Redis.

---

# 20. Gateway Calling Internal Services

Gateway config:

```yaml
environment:
  USER_SERVICE_URL: http://user-service:8081
  ORDER_SERVICE_URL: http://order-service:8082
```

Java client:

```java
@Component
public class UserClient {

    private final WebClient webClient;

    public UserClient(
            WebClient.Builder builder,
            @Value("${clients.user-service-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<UserDto> getUser(Long id) {
        return webClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(UserDto.class);
    }
}
```

Application config:

```yaml
clients:
  user-service-url: ${USER_SERVICE_URL:http://localhost:8081}
```

Flow:

```text
Browser
   |
   v
api-gateway
   |
   | http://user-service:8081/users/10
   v
Docker DNS resolves user-service
   |
   v
user-service container
```

Common mistake:

```text
Gateway inside container calls localhost:8081
```

That fails because `localhost` points to gateway container itself.

Correct:

```text
Gateway inside container calls user-service:8081
```

---

# 21. Localhost Confusion Explained

This is one of the most important Docker Compose lessons.

Inside your laptop:

```text
localhost = your laptop
```

Inside a container:

```text
localhost = that container only
```

Diagram:

```text
Host Machine
  localhost:8080  ---> api-gateway container

api-gateway container
  localhost:8081  ---> api-gateway itself, NOT user-service

Correct:
  user-service:8081 ---> user-service container
```

ASCII:

```text
+---------------- Host ----------------+
|                                       |
|  +-------------+   +--------------+   |
|  | gateway     |   | user-service |   |
|  |             |   |              |   |
|  | localhost   |   | localhost    |   |
|  | means self  |   | means self   |   |
|  +-------------+   +--------------+   |
|        |                    ^         |
|        | user-service:8081  |         |
|        +--------------------+         |
|                                       |
+---------------------------------------+
```

Rule:

```text
From host to container: use localhost + published port.
From container to container: use service name + container port.
```

---

# 22. Build Context Explained

Compose build:

```yaml
user-service:
  build: ./user-service
```

Means:

```text
Use ./user-service directory as Docker build context.
```

Build context is the folder Docker can see during build.

Diagram:

```text
user-service/
  Dockerfile
  pom.xml
  src/

Docker build context = user-service/
```

Dockerfile:

```dockerfile
COPY pom.xml .
COPY src ./src
```

Docker can copy only files inside the build context.

Bad:

```dockerfile
COPY ../shared-lib ./shared-lib
```

May fail because parent directory is outside context.

Better monorepo approach:

```yaml
user-service:
  build:
    context: .
    dockerfile: user-service/Dockerfile
```

Then Docker can access root-level shared modules.

---

# 23. Compose Build Dry Run

Command:

```bash
docker compose up --build
```

What happens internally:

```text
1. Parse compose.yml
2. Read .env values
3. Build service images if needed
4. Create network
5. Create volumes
6. Start postgres
7. Wait for postgres healthcheck
8. Start redis
9. Wait for redis healthcheck
10. Start user-service
11. Start order-service
12. Start api-gateway
13. Attach logs
```

Visual:

```text
compose.yml
   |
   v
[build images]
   |
   v
[create network]
   |
   v
[create volumes]
   |
   v
[start dependencies]
   |
   v
[start applications]
   |
   v
[stream logs]
```

This is why Compose feels like magic, but it is just ordered Docker operations.

---

# 24. Runtime Request Dry Run

User sends request:

```bash
curl http://localhost:8080/api/users/1
```

Flow:

```text
curl
  |
  v
Host localhost:8080
  |
  v
Docker port mapping
  |
  v
api-gateway:8080
  |
  v
WebClient calls http://user-service:8081/users/1
  |
  v
Docker DNS resolves user-service
  |
  v
user-service container
  |
  v
JPA queries jdbc:postgresql://postgres:5432/appdb
  |
  v
Docker DNS resolves postgres
  |
  v
postgres container reads pg-data volume
```

One-line mental model:

```text
External traffic uses host port mapping; internal traffic uses service DNS.
```

---

# 25. Logging In Compose

Compose attaches logs from all services.

```bash
docker compose logs -f
```

Better:

```bash
docker compose logs -f user-service
```

Mental model:

```text
Each container writes stdout/stderr.
Docker collects it.
Compose displays it grouped by service.
```

ASCII:

```text
user-service stdout ----+
order-service stdout ---+---> docker logs ---> compose logs
postgres stdout --------+
redis stdout -----------+
```

Good Spring Boot logging:

```yaml
logging:
  level:
    root: INFO
    org.springframework.web: INFO
    org.hibernate.SQL: WARN
```

Do not write application logs only to files inside the container.

Preferred container model:

```text
Application logs to stdout.
Platform collects logs.
```

In production:

```text
Docker logs -> Fluent Bit / Vector -> Elasticsearch / Loki / Cloud logging
```

---

# 26. Debugging: Service Not Reachable

Problem:

```text
api-gateway cannot call user-service
```

Checklist:

```text
1. Is user-service running?
2. Is it on the same network?
3. Is the app listening on the expected port?
4. Is gateway using service name, not localhost?
5. Does DNS resolve?
6. Does HTTP endpoint respond?
```

Commands:

```bash
docker compose ps
docker compose logs user-service
docker network inspect mini-compose-stack_backend-net
```

Exec into gateway:

```bash
docker compose exec api-gateway sh
```

Test DNS:

```bash
getent hosts user-service
```

Test HTTP:

```bash
wget -qO- http://user-service:8081/actuator/health
```

Packet mindset:

```text
api-gateway
   |
   | DNS user-service
   v
Docker DNS
   |
   | IP result
   v
TCP connect to 8081
   |
   v
Spring Boot endpoint
```

Find where the chain breaks.

---

# 27. Debugging: Database Connection Refused

Error:

```text
Connection refused: postgres:5432
```

Possible causes:

```text
Postgres not ready
Wrong DB URL
Wrong network
Wrong credentials
Postgres crashed
Healthcheck missing
```

Commands:

```bash
docker compose ps
docker compose logs postgres
docker compose exec postgres pg_isready -U app -d appdb
```

From user-service:

```bash
docker compose exec user-service sh
getent hosts postgres
```

If image has no tools, temporarily use a debug container:

```bash
docker run --rm -it --network mini-compose-stack_backend-net postgres:16 \
  psql -h postgres -U app -d appdb
```

Mental model:

```text
Do not debug only from host.
Debug from the same network namespace where the failure happens.
```

Because host connectivity and container connectivity are different.

---

# 28. Debugging: Redis Not Working

Error:

```text
Unable to connect to Redis at localhost:6379
```

Most likely cause:

```text
Application inside container is using localhost.
```

Correct:

```text
REDIS_HOST=redis
```

Check Redis:

```bash
docker compose exec redis redis-cli ping
```

Expected:

```text
PONG
```

Check from order-service network:

```bash
docker run --rm -it --network mini-compose-stack_backend-net redis:7 redis-cli -h redis ping
```

Flow:

```text
order-service
   |
   | redis:6379
   v
Docker DNS
   |
   v
redis container
```

If Redis persistence matters, check:

```bash
docker volume ls
docker volume inspect mini-compose-stack_redis-data
```

---

# 29. Production Story: It Works On My Machine

A team had this local config:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/appdb
```

It worked when the developer ran Spring Boot directly on laptop.

Then they dockerized the service.

Inside container:

```text
localhost = user-service container
```

There was no Postgres inside user-service container.

Failure:

```text
Connection refused localhost:5432
```

Fix:

```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/appdb
```

Lesson:

```text
Localhost changes meaning depending on where code runs.
```

One picture:

```text
Laptop mode:
Spring Boot on host -> localhost:5432 -> Postgres on host

Container mode:
Spring Boot in container -> localhost:5432 -> same container only
Spring Boot in container -> postgres:5432 -> Postgres container
```

---

# 30. Production Story: Exposing Too Many Ports

Bad Compose:

```yaml
postgres:
  ports:
    - "5432:5432"
redis:
  ports:
    - "6379:6379"
user-service:
  ports:
    - "8081:8081"
order-service:
  ports:
    - "8082:8082"
```

Everything is exposed to host.

Risk:

```text
Accidental access
Port conflicts
Security leaks
Wrong clients bypass gateway
Local environment becomes messy
```

Better:

```yaml
api-gateway:
  ports:
    - "8080:8080"

postgres:
  # no host port unless developer explicitly needs DB access

redis:
  # no host port unless developer explicitly needs Redis access
```

Mental model:

```text
Expose the city gate, not every apartment door.
```

Diagram:

```text
Public:
Browser -> gateway

Private:
gateway -> user-service -> postgres
gateway -> order-service -> redis
```

---

# 31. Production Story: Lost Data After Down

Developer ran:

```bash
docker compose down -v
```

The `-v` removed named volumes.

Result:

```text
Postgres data deleted
Redis AOF deleted
```

Mental model:

```text
docker compose down      = remove containers and network
docker compose down -v   = also remove storage rooms
```

ASCII:

```text
compose down:
containers gone
network gone
volumes stay

compose down -v:
containers gone
network gone
volumes gone
DATA GONE
```

Safer local workflow:

```bash
docker compose down
```

Dangerous reset workflow:

```bash
docker compose down -v
```

Use only when you intentionally want a clean database.

---

# 32. Health Endpoints For Compose

Spring Boot Actuator dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Application config:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      probes:
        enabled: true
```

Compose healthcheck:

```yaml
user-service:
  healthcheck:
    test: ["CMD", "wget", "-qO-", "http://localhost:8081/actuator/health"]
    interval: 10s
    timeout: 3s
    retries: 5
```

Flow:

```text
Docker runs healthcheck command
   |
   v
Spring Boot /actuator/health
   |
   v
UP or DOWN
   |
   v
Compose marks service healthy/unhealthy
```

Important:

```text
Healthcheck runs inside the container.
So localhost is correct here.
```

Because the healthcheck is checking the app inside the same container.

---

# 33. Scaling Services With Compose

Compose can scale stateless services:

```bash
docker compose up --scale order-service=3
```

But there is a catch.

If `container_name` is fixed, scaling will fail because names must be unique.

Bad for scaling:

```yaml
container_name: order-service
```

Better:

```yaml
order-service:
  build: ./order-service
```

Compose creates:

```text
project-order-service-1
project-order-service-2
project-order-service-3
```

Visual:

```text
api-gateway
   |
   +--> order-service-1
   +--> order-service-2
   +--> order-service-3
```

DNS may return multiple IPs for scaled service.

But Compose is not a full production orchestrator.

For real production scaling, use:

```text
Kubernetes
ECS
Nomad
Docker Swarm
```

Compose is best for:

```text
Local development
Integration testing
Small demos
Learning
```

---

# 34. Compose Profiles

Sometimes you do not always want every service.

Example:

```yaml
services:
  prometheus:
    image: prom/prometheus
    profiles:
      - observability
```

Run default stack:

```bash
docker compose up
```

Run with observability:

```bash
docker compose --profile observability up
```

Mental model:

```text
Profiles are optional rooms in the building.
Open them only when needed.
```

Diagram:

```text
Default:
api + db + redis

With profile:
api + db + redis + prometheus + grafana
```

This keeps local development lighter.

Useful profiles:

```text
observability
messaging
local-tools
load-test
admin-ui
```

---

# 35. Compose For Integration Tests

A strong Java backend use case:

```text
Run full dependencies before integration tests.
```

Flow:

```text
CI pipeline
   |
   v
docker compose up -d postgres redis
   |
   v
mvn test
   |
   v
docker compose down
```

Example:

```bash
docker compose up -d postgres redis
mvn verify
docker compose down
```

Test config:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/appdb
```

Why localhost here?

Because tests run on the host/CI agent, not inside a container.

Diagram:

```text
Maven tests on host
   |
   | localhost:5432
   v
Postgres container published to host
```

Rule:

```text
If caller runs on host: use localhost + published port.
If caller runs in container: use service name + container port.
```

---

# 36. Compose vs Kubernetes

Compose:

```text
Local multi-container development
Single machine
Simple networking
Simple volumes
Simple startup
```

Kubernetes:

```text
Production orchestration
Many nodes
Self-healing
Rolling deployments
Service discovery
Autoscaling
PersistentVolumeClaims
Secrets and ConfigMaps
```

Mapping:

```text
Docker Compose service  -> Kubernetes Deployment
Compose network         -> Kubernetes cluster networking
Compose volume          -> PersistentVolumeClaim
Compose env             -> ConfigMap / Secret
Compose healthcheck     -> liveness/readiness probes
Compose ports           -> Service / Ingress
```

ASCII:

```text
Compose local:
compose.yml -> containers on laptop

Kubernetes production:
manifests/helm -> pods across cluster nodes
```

Do not skip Compose.

Compose teaches the microservice mental model before Kubernetes adds cluster complexity.

---

# 37. Real World Analogy: Restaurant Chain

Imagine a restaurant system:

```text
Customer entrance = api-gateway
Kitchen           = order-service
Customer records  = user-service
Cold storage      = postgres
Fast counter cache = redis
Internal hallway  = Docker network
Storage room      = Docker volume
Manager board     = environment variables
```

No customer should walk directly into:

```text
Kitchen
Cold storage
Cash locker
```

They enter through the front desk.

Same in Compose:

```text
Public -> gateway
Private -> internal services and databases
```

Diagram:

```text
Customer
   |
   v
Front Desk / Gateway
   |
   +--> Kitchen / Order Service
   |        |
   |        v
   |      Redis
   |
   +--> Customer Desk / User Service
            |
            v
          Postgres
```

This model is easier to remember than YAML keywords.

---

# 38. Common Compose Commands

Start stack:

```bash
docker compose up
```

Start in background:

```bash
docker compose up -d
```

Build and start:

```bash
docker compose up --build
```

Stop containers:

```bash
docker compose stop
```

Remove containers/network:

```bash
docker compose down
```

Remove containers/network/volumes:

```bash
docker compose down -v
```

Show status:

```bash
docker compose ps
```

Show logs:

```bash
docker compose logs -f
```

Run command inside service:

```bash
docker compose exec user-service sh
```

Rebuild one service:

```bash
docker compose build user-service
```

Restart one service:

```bash
docker compose restart user-service
```

---

# 39. Strong Interview Answer

Question:

```text
How do you use Docker Compose for a microservice stack?
```

Strong answer:

```text
Docker Compose lets us describe a multi-container application in one YAML file. Each microservice, database, cache, or broker becomes a service. Compose creates a private network where services can reach each other by DNS name, so the application uses names like postgres, redis, user-service, instead of hardcoded container IPs.

For a Spring Boot stack, I usually expose only the API gateway to the host and keep internal services and databases private on the Compose network. Configuration is injected through environment variables, persistent systems like Postgres use named volumes, and dependencies use healthchecks so services do not start before databases are ready.

Compose is mainly for local development, integration testing, and demos. For production, the same mental model maps to Kubernetes: services become deployments, volumes become PVCs, env vars become ConfigMaps or Secrets, and healthchecks become readiness and liveness probes.
```

---

# 40. Debugging Mindset Summary

When something fails, do not randomly edit YAML.

Walk the packet path.

```text
Caller
  |
  v
Correct hostname?
  |
  v
DNS resolves?
  |
  v
Correct port?
  |
  v
Same network?
  |
  v
Target process listening?
  |
  v
Application healthy?
  |
  v
Credentials/config correct?
```

For DB:

```text
App config -> Docker DNS -> postgres container -> database process -> volume
```

For HTTP:

```text
Gateway -> service DNS -> service port -> controller -> dependency
```

For Redis:

```text
Order service -> redis DNS -> Redis port -> command response
```

Think like a packet, not like a YAML memorizer.

---

# 41. Production Checklist

```text
[ ] Use service names, not container IPs
[ ] Avoid localhost for container-to-container calls
[ ] Expose only public entrypoints
[ ] Keep DB/Redis private unless host access is needed
[ ] Use named volumes for stateful services
[ ] Use healthchecks for databases and important dependencies
[ ] Add application-level retries
[ ] Keep secrets out of Git
[ ] Use .env only for local development
[ ] Log to stdout/stderr
[ ] Do not use fixed container_name if you want scaling
[ ] Test from inside the failing container/network
[ ] Understand down vs down -v
[ ] Map Compose concepts to Kubernetes concepts
```

---

# 42. Cheat Sheet

```text
Compose file      = blueprint for many containers
Service           = one container type
Network           = private road between services
Volume            = persistent storage room
Port mapping      = host door to container door
Environment       = runtime configuration
Healthcheck       = readiness signal
Service name      = DNS name
```

Common URLs:

```text
Host -> gateway:
http://localhost:8080

Gateway container -> user service:
http://user-service:8081

User service container -> Postgres:
jdbc:postgresql://postgres:5432/appdb

Order service container -> Redis:
redis:6379
```

Most common bug:

```text
Using localhost inside a container to call another container.
```

Correct fix:

```text
Use Compose service name.
```

---

# 43. One Picture To Remember

```text
                         OUTSIDE WORLD
                              |
                              | localhost:8080
                              v
+------------------------------------------------------------------+
| HOST MACHINE                                                      |
|                                                                  |
|  +------------------------------------------------------------+  |
|  | COMPOSE NETWORK: backend-net                              |  |
|  |                                                            |  |
|  |      +-------------+                                       |  |
|  |      | api-gateway |  public door                          |  |
|  |      +------+------+                                       |  |
|  |             | service DNS                                  |  |
|  |     +-------+--------+                                     |  |
|  |     |                |                                     |  |
|  |     v                v                                     |  |
|  | +---------+      +----------+                              |  |
|  | | user    |      | order    |                              |  |
|  | | service |      | service  |                              |  |
|  | +----+----+      +----+-----+                              |  |
|  |      |                |                                    |  |
|  |      | postgres:5432  | redis:6379                         |  |
|  |      v                v                                    |  |
|  | +---------+      +----------+                              |  |
|  | |postgres |      | redis    |                              |  |
|  | +----+----+      +----+-----+                              |  |
|  |      |                |                                    |  |
|  |      v                v                                    |  |
|  |  pg-data volume   redis-data volume                        |  |
|  |                                                            |  |
|  +------------------------------------------------------------+  |
+------------------------------------------------------------------+

Rule:
External users use host ports.
Internal services use DNS names.
Stateful services use volumes.
Configuration comes from environment.
```

---

# 44. Final Takeaways

Docker Compose is not just YAML. It is the mental bridge between one-container Docker learning and real microservice systems.

The core idea is simple:

```text
A backend system is a group of containers sharing a private network.
```

Compose gives you:

```text
Service definitions
Private DNS
Networks
Volumes
Environment injection
Startup coordination
Local integration testing
```

For Java/Spring Boot developers, Compose is extremely valuable because it lets you run a real local product-style stack:

```text
Gateway + services + Postgres + Redis + messaging + observability
```

without manually wiring every container.

Do not memorize Compose keywords.

Remember the system picture:

```text
Public gate -> internal services -> stateful dependencies -> persistent volumes
```

That one picture will help you debug most Docker Compose problems.
