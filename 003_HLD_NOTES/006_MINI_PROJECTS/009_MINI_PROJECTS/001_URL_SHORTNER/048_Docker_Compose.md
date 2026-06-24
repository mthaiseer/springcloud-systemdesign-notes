# 048_Docker_Compose.md
# MiniURLShortener — Docker Compose

> Core mental model: **Docker Compose is a local mini-production network where multiple containers run together as named services. Each service is a containerized process, and services talk to each other by service name, not localhost.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What Is Docker Compose?](#4-what-is-docker-compose)
- [5. Compose vs Dockerfile](#5-compose-vs-dockerfile)
- [6. Service Mental Model](#6-service-mental-model)
- [7. Network Mental Model](#7-network-mental-model)
- [8. localhost Trap](#8-localhost-trap)
- [9. Ports Mental Model](#9-ports-mental-model)
- [10. Volumes Mental Model](#10-volumes-mental-model)
- [11. Environment Variables](#11-environment-variables)
- [12. depends_on Mental Model](#12-depends_on-mental-model)
- [13. Health Checks](#13-health-checks)
- [14. MiniURLShortener Compose Architecture](#14-miniurlshortener-compose-architecture)
- [15. Complete docker-compose.yml](#15-complete-docker-composeyml)
- [16. Spring Boot Configuration For Compose](#16-spring-boot-configuration-for-compose)
- [17. Postgres Service](#17-postgres-service)
- [18. Redis Service](#18-redis-service)
- [19. Kafka Service](#19-kafka-service)
- [20. App Service](#20-app-service)
- [21. Optional Config Server And Eureka](#21-optional-config-server-and-eureka)
- [22. Startup Flow](#22-startup-flow)
- [23. Running The Stack](#23-running-the-stack)
- [24. Useful Compose Commands](#24-useful-compose-commands)
- [25. Step-by-Step Dry Runs](#25-step-by-step-dry-runs)
- [26. Internal Execution Walkthrough](#26-internal-execution-walkthrough)
- [27. Debugging Mindset](#27-debugging-mindset)
- [28. Production-Like Local Testing](#28-production-like-local-testing)
- [29. Compose Profiles](#29-compose-profiles)
- [30. Logs And Observability](#30-logs-and-observability)
- [31. Production Failure Stories](#31-production-failure-stories)
- [32. Common Mistakes](#32-common-mistakes)
- [33. Interview-Ready Explanation](#33-interview-ready-explanation)
- [34. Senior Engineer Checklist](#34-senior-engineer-checklist)
- [35. One-Page Cheat Sheet](#35-one-page-cheat-sheet)
- [36. One Picture To Remember](#36-one-picture-to-remember)

---

## 1. Why This Exists

A real backend is not only one Spring Boot process.

MiniURLShortener needs:

```text
Spring Boot app
Postgres
Redis
Kafka
Config Server
Eureka
Gateway
Observability tools later
```

Running everything manually is painful.

Without Docker Compose:

```text
Start Postgres manually.
Start Redis manually.
Start Kafka manually.
Remember ports.
Remember usernames.
Remember networks.
Start app with correct env variables.
Debug random localhost issues.
```

With Docker Compose:

```text
One YAML file describes the whole local system.
One command starts everything.
```

Command:

```bash
docker compose up -d
```

Now you can run:

```text
app
postgres
redis
kafka
```

as one local environment.

ASCII:

```text
docker-compose.yml
      |
      v
+----------------------------+
| app                        |
| postgres                   |
| redis                      |
| kafka                      |
+----------------------------+
      |
      v
local mini-production stack
```

Production mindset:

```text
Compose is not Kubernetes.
But it is excellent for local integration testing.
```

---

## 2. The One Core Mental Model

Docker Compose creates a private network.

Inside that network:

```text
each service gets a DNS name
service name becomes hostname
containers talk using service names
```

ASCII:

```text
Docker Compose Network: miniurl-network

+-------------+       postgres:5432       +-------------+
| app         | -------------------------> | postgres    |
+-------------+                            +-------------+

+-------------+       redis:6379          +-------------+
| app         | -------------------------> | redis       |
+-------------+                            +-------------+

+-------------+       kafka:9092          +-------------+
| app         | -------------------------> | kafka       |
+-------------+                            +-------------+
```

One-line memory:

```text
In Compose, service name is hostname.
```

So from app container:

```text
postgres:5432
redis:6379
kafka:9092
```

Not:

```text
localhost:5432
localhost:6379
localhost:9092
```

`localhost` inside a container means that same container.

---

## 3. Problem Statement

Create a Docker Compose setup for MiniURLShortener.

It must run:

```text
1. Spring Boot app
2. Postgres
3. Redis
4. Kafka
```

It should support:

```text
1. Service-to-service networking.
2. Environment-based Spring Boot config.
3. Persistent Postgres data.
4. Redis cache.
5. Kafka bootstrap server.
6. Health checks.
7. Startup ordering.
8. Logs and debugging commands.
9. Local production-like testing.
```

Out of scope:

```text
1. Full Kubernetes migration.
2. Production-grade Kafka cluster.
3. HA Postgres.
4. Redis cluster.
5. Secret management deep dive.
6. Observability stack deep dive.
```

This is local integration infrastructure.

---

## 4. What Is Docker Compose?

Docker Compose is a tool for defining and running multiple Docker containers.

You write:

```text
docker-compose.yml
```

Then run:

```bash
docker compose up
```

Example:

```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"

  postgres:
    image: postgres:16
```

Meaning:

```text
Start app container.
Start postgres container.
Put them on same Compose network.
```

ASCII:

```text
YAML file
  |
  v
Compose engine
  |
  v
Containers + network + volumes
```

Compose manages:

```text
services
images
builds
networks
volumes
environment variables
ports
health checks
dependencies
```

It is excellent for:

```text
local development
integration testing
demo environments
CI test stack
```

It is usually not the final production orchestrator for large systems.

Production usually uses:

```text
Kubernetes
ECS
Nomad
Docker Swarm
cloud platform orchestrators
```

---

## 5. Compose vs Dockerfile

Dockerfile answers:

```text
How do I build one image?
```

Docker Compose answers:

```text
How do I run many containers together?
```

ASCII:

```text
Dockerfile
   |
   v
app image
   |
   v
docker-compose.yml
   |
   v
app + postgres + redis + kafka
```

Table:

```text
+----------------+--------------------------------------+
| Tool           | Purpose                              |
+----------------+--------------------------------------+
| Dockerfile     | Build one application image          |
| Docker Compose | Run multiple services together       |
+----------------+--------------------------------------+
```

Example:

```dockerfile
Dockerfile:
    Build MiniURLShortener image.
```

```yaml
docker-compose.yml:
    Run MiniURLShortener with Postgres, Redis, Kafka.
```

Mental model:

```text
Dockerfile is recipe for one container image.
Compose is wiring diagram for many containers.
```

---

## 6. Service Mental Model

In Compose, a service is a container definition.

Example:

```yaml
services:
  postgres:
    image: postgres:16
```

This defines one service named:

```text
postgres
```

Compose creates:

```text
container from postgres:16 image
DNS name postgres
network connection
optional volumes
optional ports
optional env vars
```

ASCII:

```text
Service definition
      |
      v
Container instance
      |
      v
DNS name in network
```

You can scale services:

```bash
docker compose up --scale app=3
```

Then:

```text
app service has 3 containers
```

But local scaling has limitations.

For MiniURLShortener, normal local stack:

```text
1 app
1 postgres
1 redis
1 kafka
```

---

## 7. Network Mental Model

Compose creates a default network.

If project folder is:

```text
miniurl
```

Default network may be:

```text
miniurl_default
```

All services join it unless configured otherwise.

ASCII:

```text
+------------------------------------------------+
| Compose Network                                |
|                                                |
|  app       postgres       redis       kafka    |
|   |           |             |           |       |
|   +-----------+-------------+-----------+       |
+------------------------------------------------+
```

Inside this network:

```text
app can resolve postgres
app can resolve redis
app can resolve kafka
```

DNS examples:

```text
postgres -> Postgres container IP
redis    -> Redis container IP
kafka    -> Kafka container IP
```

You usually do not need container IPs.

Use names.

Important:

```text
Container IPs can change.
Service names are stable.
```

---

## 8. localhost Trap

This is the most common Docker Compose mistake.

From your laptop:

```text
localhost:5432 may mean Postgres if port is published.
```

From app container:

```text
localhost:5432 means app container itself.
```

ASCII:

```text
HOST MACHINE
+--------------------------------------+
| localhost:5432 -> mapped to postgres |
+--------------------------------------+

APP CONTAINER
+--------------------------------------+
| localhost:5432 -> app container      |
| postgres:5432  -> postgres service   |
+--------------------------------------+
```

Wrong Spring config inside app container:

```text
spring.datasource.url=jdbc:postgresql://localhost:5432/miniurl
```

Correct:

```text
spring.datasource.url=jdbc:postgresql://postgres:5432/miniurl
```

Wrong Redis:

```text
spring.data.redis.host=localhost
```

Correct:

```text
spring.data.redis.host=redis
```

Wrong Kafka:

```text
spring.kafka.bootstrap-servers=localhost:9092
```

Correct inside Compose:

```text
spring.kafka.bootstrap-servers=kafka:9092
```

One-line memory:

```text
localhost is local to where the code is running.
```

---

## 9. Ports Mental Model

Compose port mapping:

```yaml
ports:
  - "8080:8080"
```

Means:

```text
hostPort:containerPort
```

ASCII:

```text
Your Browser
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

Example:

```yaml
postgres:
  ports:
    - "5432:5432"
```

This allows host tools to connect:

```text
DBeaver / IntelliJ / psql on laptop -> localhost:5432
```

But app container still uses:

```text
postgres:5432
```

Important distinction:

```text
ports:
    expose container to host

internal service name:
    container-to-container communication
```

You do not need to publish every service port.

For app-to-Postgres communication, this is enough:

```text
same Compose network
```

Publishing Postgres port is useful for local debugging.

---

## 10. Volumes Mental Model

Containers are disposable.

If Postgres container is removed without volume:

```text
data disappears
```

Volume persists data outside container lifecycle.

ASCII:

```text
Postgres Container
      |
      v
/data inside container
      |
      v
Named Volume: postgres-data
      |
      v
data survives container recreation
```

Compose:

```yaml
volumes:
  postgres-data:
```

Service:

```yaml
postgres:
  volumes:
    - postgres-data:/var/lib/postgresql/data
```

Meaning:

```text
Store Postgres data in named volume.
```

Without volume:

```text
docker compose down
container removed
data may be lost
```

With volume:

```text
docker compose down
container removed
volume remains
```

To delete volume:

```bash
docker compose down -v
```

Warning:

```text
down -v deletes data volumes.
```

---

## 11. Environment Variables

Compose injects env vars into containers.

Example:

```yaml
app:
  environment:
    SPRING_PROFILES_ACTIVE: docker
    SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/miniurl
```

Spring Boot maps:

```text
SPRING_DATASOURCE_URL
    -> spring.datasource.url

SPRING_DATA_REDIS_HOST
    -> spring.data.redis.host

SPRING_KAFKA_BOOTSTRAP_SERVERS
    -> spring.kafka.bootstrap-servers
```

ASCII:

```text
docker-compose.yml
      |
      v
container environment
      |
      v
Spring Boot property binding
      |
      v
DataSource / Redis / Kafka config
```

Use `.env` for local variables:

```env
POSTGRES_DB=miniurl
POSTGRES_USER=miniurl
POSTGRES_PASSWORD=miniurlpass
```

Compose can read them:

```yaml
environment:
  POSTGRES_DB: ${POSTGRES_DB}
  POSTGRES_USER: ${POSTGRES_USER}
  POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
```

Important:

```text
.env is convenient for local development.
Do not commit real production secrets.
```

---

## 12. depends_on Mental Model

`depends_on` controls startup order.

Example:

```yaml
app:
  depends_on:
    - postgres
    - redis
```

This means:

```text
Start postgres and redis containers before app container.
```

But basic `depends_on` does not mean:

```text
Postgres is ready to accept connections.
```

It only means:

```text
Postgres container started.
```

ASCII:

```text
depends_on:
postgres container started
        |
        v
app container starts

But:
postgres may still be booting
```

Better:

```text
Use health checks and depends_on condition.
```

Example:

```yaml
depends_on:
  postgres:
    condition: service_healthy
```

Mental model:

```text
started is not ready.
ready means health check passes.
```

---

## 13. Health Checks

Health check tests whether service is actually ready.

Postgres:

```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U miniurl -d miniurl"]
  interval: 10s
  timeout: 5s
  retries: 5
```

Redis:

```yaml
healthcheck:
  test: ["CMD", "redis-cli", "ping"]
  interval: 10s
  timeout: 5s
  retries: 5
```

App:

```yaml
healthcheck:
  test: ["CMD-SHELL", "wget -qO- http://localhost:8080/actuator/health || exit 1"]
  interval: 15s
  timeout: 5s
  retries: 5
```

ASCII:

```text
Container starts
      |
      v
Health check runs
      |
      +-- fail --> unhealthy
      |
      +-- pass --> healthy
```

Why useful:

```text
1. Better startup order.
2. Faster debugging.
3. CI can wait for readiness.
4. Compose status becomes meaningful.
```

Command:

```bash
docker compose ps
```

Shows health state.

---

## 14. MiniURLShortener Compose Architecture

Target local architecture:

```text
Browser / Postman
      |
      v
localhost:8080
      |
      v
app container
      |
      +--> postgres:5432
      +--> redis:6379
      +--> kafka:9092
```

ASCII:

```text
HOST
+------------------------------------------------------+
| Browser/Postman                                      |
| http://localhost:8080                                |
+----------------------------|-------------------------+
                             |
                             v
COMPOSE NETWORK              |
+------------------------------------------------------+
| +-------------------+                                |
| | app               |                                |
| | Spring Boot       |                                |
| +---------+---------+                                |
|           |                                          |
|           +----> postgres:5432                       |
|           +----> redis:6379                          |
|           +----> kafka:9092                          |
|                                                      |
| +-------------------+  +-------------------+         |
| | postgres          |  | redis             |         |
| +-------------------+  +-------------------+         |
|                                                      |
| +-------------------+                                |
| | kafka             |                                |
| +-------------------+                                |
+------------------------------------------------------+
```

App does not need host machine IP.

App uses Compose service names.

---

## 15. Complete docker-compose.yml

Create:

```text
docker-compose.yml
```

Production-like local stack:

```yaml
services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    image: mini-url-shortener:048
    container_name: miniurl-app
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/miniurl
      SPRING_DATASOURCE_USERNAME: miniurl
      SPRING_DATASOURCE_PASSWORD: miniurlpass
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      JAVA_OPTS: "-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
      kafka:
        condition: service_started
    networks:
      - miniurl-network

  postgres:
    image: postgres:16
    container_name: miniurl-postgres
    environment:
      POSTGRES_DB: miniurl
      POSTGRES_USER: miniurl
      POSTGRES_PASSWORD: miniurlpass
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U miniurl -d miniurl"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - miniurl-network

  redis:
    image: redis:7
    container_name: miniurl-redis
    ports:
      - "6379:6379"
    command: ["redis-server", "--appendonly", "yes"]
    volumes:
      - redis-data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - miniurl-network

  kafka:
    image: apache/kafka:3.8.0
    container_name: miniurl-kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_LISTENERS: PLAINTEXT://:9092,CONTROLLER://:9093
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka:9093
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
    volumes:
      - kafka-data:/var/lib/kafka/data
    networks:
      - miniurl-network

volumes:
  postgres-data:
  redis-data:
  kafka-data:

networks:
  miniurl-network:
    driver: bridge
```

This stack provides:

```text
app        -> Spring Boot service
postgres   -> database
redis      -> cache
kafka      -> event streaming
network    -> service discovery by name
volumes    -> persistent local data
```

---

## 16. Spring Boot Configuration For Compose

In `application-docker.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}

  data:
    redis:
      host: ${SPRING_DATA_REDIS_HOST}
      port: ${SPRING_DATA_REDIS_PORT:6379}

  kafka:
    bootstrap-servers: ${SPRING_KAFKA_BOOTSTRAP_SERVERS}

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

Why use environment variables?

```text
Same image can run in local, staging, and production.
```

ASCII:

```text
Compose env vars
      |
      v
application-docker.yml placeholders
      |
      v
Spring Boot beans
      |
      v
DataSource / Redis / Kafka clients
```

Rule:

```text
Image should not know environment.
Environment configures image at runtime.
```

---

## 17. Postgres Service

Postgres Compose service:

```yaml
postgres:
  image: postgres:16
  environment:
    POSTGRES_DB: miniurl
    POSTGRES_USER: miniurl
    POSTGRES_PASSWORD: miniurlpass
  ports:
    - "5432:5432"
  volumes:
    - postgres-data:/var/lib/postgresql/data
```

Meaning:

```text
Create database miniurl.
Create user miniurl.
Set password miniurlpass.
Persist data in postgres-data volume.
Expose Postgres to host on localhost:5432.
```

App connection:

```text
jdbc:postgresql://postgres:5432/miniurl
```

Host connection:

```text
jdbc:postgresql://localhost:5432/miniurl
```

ASCII:

```text
Host tool -> localhost:5432 -> postgres container
App       -> postgres:5432  -> postgres container
```

Useful command:

```bash
docker exec -it miniurl-postgres psql -U miniurl -d miniurl
```

---

## 18. Redis Service

Redis Compose service:

```yaml
redis:
  image: redis:7
  ports:
    - "6379:6379"
  command: ["redis-server", "--appendonly", "yes"]
  volumes:
    - redis-data:/data
```

Meaning:

```text
Run Redis.
Expose to host on localhost:6379.
Enable append-only persistence.
Store data in redis-data volume.
```

App config:

```text
SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_REDIS_PORT=6379
```

Host command:

```bash
docker exec -it miniurl-redis redis-cli
```

Inside redis-cli:

```text
PING
KEYS *
GET short:abc123
```

ASCII:

```text
App container
   |
   v
redis:6379
   |
   v
Redis cache
```

For production, avoid `KEYS *` on large Redis.

For local debugging, it is acceptable.

---

## 19. Kafka Service

Kafka is more complex because clients need advertised listeners.

For app inside Compose:

```text
KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092
```

Meaning:

```text
Kafka tells clients: connect to kafka:9092
```

App config:

```text
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

ASCII:

```text
App
 |
 v
bootstrap kafka:9092
 |
 v
Kafka broker
 |
 v
returns advertised listener kafka:9092
 |
 v
App continues using kafka:9092
```

Common Kafka mistake:

```text
Advertised listener is localhost:9092 inside Compose.
```

Then app container tries:

```text
localhost:9092
```

Which means app container itself, not Kafka.

Correct for internal Compose app:

```text
kafka:9092
```

For both host and container access, you may configure dual listeners later.

For MiniURLShortener local stack, internal access is enough.

---

## 20. App Service

App service builds from Dockerfile:

```yaml
app:
  build:
    context: .
    dockerfile: Dockerfile
  image: mini-url-shortener:048
```

Meaning:

```text
Build image using local Dockerfile.
Tag it as mini-url-shortener:048.
```

Ports:

```yaml
ports:
  - "8080:8080"
```

Meaning:

```text
Host localhost:8080 -> container 8080.
```

Environment:

```yaml
environment:
  SPRING_PROFILES_ACTIVE: docker
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/miniurl
```

Dependency readiness:

```yaml
depends_on:
  postgres:
    condition: service_healthy
```

Important:

```text
The app should still handle retry during startup.
Even with health checks, real systems can restart dependencies.
```

ASCII:

```text
app startup
   |
   v
read env vars
   |
   v
connect postgres/redis/kafka
   |
   v
start Tomcat
   |
   v
ready on 8080
```

---

## 21. Optional Config Server And Eureka

If using Spring Cloud Config and Eureka locally:

```yaml
config-server:
  image: mini-config-server:latest
  ports:
    - "8888:8888"
  networks:
    - miniurl-network

eureka-server:
  image: mini-eureka-server:latest
  ports:
    - "8761:8761"
  networks:
    - miniurl-network
```

App env:

```yaml
SPRING_CONFIG_IMPORT: optional:configserver:http://config-server:8888
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka
```

ASCII:

```text
app
 |
 +--> config-server:8888
 |
 +--> eureka-server:8761
 |
 +--> postgres:5432
 |
 +--> redis:6379
 |
 +--> kafka:9092
```

Startup issue:

```text
Config Server or Eureka may not be ready when app starts.
```

Possible solutions:

```text
1. depends_on with health checks.
2. optional config import for local.
3. app retry/backoff.
4. restart policy.
```

For local learning, keep core stack simple first:

```text
app + postgres + redis + kafka
```

Then add Spring Cloud services.

---

## 22. Startup Flow

When you run:

```bash
docker compose up -d
```

Flow:

```text
1. Compose reads YAML.
2. Compose creates network.
3. Compose creates volumes.
4. Compose pulls/builds images.
5. Compose starts postgres.
6. Compose starts redis.
7. Compose starts kafka.
8. Health checks begin.
9. Compose starts app based on depends_on.
10. App reads env variables.
11. App connects to dependencies.
12. App exposes port 8080.
```

ASCII:

```text
docker compose up
      |
      v
network + volumes
      |
      v
postgres + redis + kafka
      |
      v
health checks
      |
      v
app
      |
      v
localhost:8080
```

Important:

```text
Startup order is not full resilience.
Services can fail after startup.
```

Your app should still use:

```text
timeouts
retries
circuit breakers
health checks
connection pool validation
```

---

## 23. Running The Stack

Start:

```bash
docker compose up -d
```

Start and rebuild app:

```bash
docker compose up -d --build
```

View status:

```bash
docker compose ps
```

View logs:

```bash
docker compose logs -f
```

View app logs only:

```bash
docker compose logs -f app
```

Stop:

```bash
docker compose down
```

Stop and delete volumes:

```bash
docker compose down -v
```

Warning:

```text
down -v deletes Postgres/Redis/Kafka local data.
```

Test app:

```bash
curl http://localhost:8080/actuator/health
```

Create URL example:

```bash
curl -X POST http://localhost:8080/api/v1/urls \
  -H "Content-Type: application/json" \
  -d '{"longUrl":"https://example.com"}'
```

---

## 24. Useful Compose Commands

Build images:

```bash
docker compose build
```

Start services:

```bash
docker compose up -d
```

Rebuild and start:

```bash
docker compose up -d --build
```

Stop services:

```bash
docker compose down
```

Stop and remove volumes:

```bash
docker compose down -v
```

See logs:

```bash
docker compose logs -f
```

See one service logs:

```bash
docker compose logs -f app
```

Run command inside app:

```bash
docker compose exec app sh
```

Run psql:

```bash
docker compose exec postgres psql -U miniurl -d miniurl
```

Run redis-cli:

```bash
docker compose exec redis redis-cli
```

Restart app only:

```bash
docker compose restart app
```

List containers:

```bash
docker compose ps
```

---

## 25. Step-by-Step Dry Runs

### Dry Run 1: App Connects To Postgres Correctly

Config:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/miniurl
```

Flow:

```text
1. App starts inside Compose network.
2. Spring creates DataSource.
3. DNS resolves postgres to Postgres container IP.
4. App connects to port 5432.
5. Hikari pool starts.
```

ASCII:

```text
app container
    |
    v
DNS lookup: postgres
    |
    v
postgres container IP
    |
    v
connect 5432
```

Result:

```text
App starts successfully.
```

---

### Dry Run 2: App Uses localhost Wrongly

Config:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/miniurl
```

Flow:

```text
1. App starts.
2. App tries localhost:5432.
3. localhost means app container.
4. No Postgres inside app container.
5. Connection refused.
```

ASCII:

```text
app container
    |
    v
localhost:5432
    |
    v
app container itself
    |
    v
no postgres
```

Fix:

```text
Use postgres:5432
```

---

### Dry Run 3: Host Connects To Postgres

Compose:

```yaml
ports:
  - "5432:5432"
```

From laptop:

```bash
psql -h localhost -p 5432 -U miniurl -d miniurl
```

Flow:

```text
host localhost:5432
    |
    v
Docker port mapping
    |
    v
postgres container:5432
```

This is correct because command runs on host.

---

### Dry Run 4: Postgres Data Survives Restart

Volume:

```yaml
postgres-data:/var/lib/postgresql/data
```

Flow:

```text
1. Insert URL row.
2. Run docker compose down.
3. Run docker compose up -d.
4. Query row.
5. Row still exists.
```

Why:

```text
Named volume survived container recreation.
```

Delete data:

```bash
docker compose down -v
```

---

### Dry Run 5: App Starts Before DB Ready

Without health check:

```text
postgres container started
but database still initializing
app starts immediately
app connection fails
```

With health check:

```text
postgres starts
pg_isready runs
only when healthy app starts
```

ASCII:

```text
Without:
postgres started -> app starts -> DB not ready -> fail

With:
postgres started -> health pass -> app starts -> ok
```

---

## 26. Internal Execution Walkthrough

Compose internal sequence:

```text
1. Read docker-compose.yml.
2. Resolve variables from .env and shell.
3. Create default/custom network.
4. Create named volumes.
5. Pull images if missing.
6. Build images if build section exists.
7. Create containers.
8. Attach containers to network.
9. Apply environment variables.
10. Apply port mappings.
11. Start containers.
12. Run health checks.
13. Stream logs.
```

Network DNS:

```text
Service name registered in Docker DNS.
Container asks for postgres.
Docker DNS returns postgres container IP.
```

ASCII:

```text
app wants postgres
      |
      v
Docker embedded DNS
      |
      v
IP of postgres container
      |
      v
TCP connection
```

Container lifecycle:

```text
created -> running -> healthy/unhealthy -> stopped
```

Compose is a local orchestrator, but simpler than Kubernetes.

---

## 27. Debugging Mindset

When Compose stack fails, ask:

```text
Did image build?
Did container start?
Is service healthy?
Are env vars correct?
Is app using service name, not localhost?
Are ports mapped correctly?
Is volume stale?
Is Kafka advertised listener correct?
Are logs showing startup failure?
Is dependency ready or only started?
Is app profile correct?
```

Debug map:

```text
App cannot connect to Postgres:
    check datasource URL
    check postgres health
    check username/password
    check network
    check logs

Host cannot access app:
    check ports mapping
    check app listening port
    check docker compose ps
    check app logs

Redis connection refused:
    use redis host, not localhost
    check redis container health

Kafka connection fails:
    check bootstrap server
    check advertised listeners
    check kafka logs

Data disappeared:
    check if docker compose down -v was used
    check volume mapping
```

Useful commands:

```bash
docker compose ps
docker compose logs -f app
docker compose logs -f postgres
docker compose exec app sh
docker compose exec postgres psql -U miniurl -d miniurl
docker network ls
docker volume ls
```

Golden question:

```text
Is the failure in build, network, config, readiness, or application code?
```

---

## 28. Production-Like Local Testing

Compose lets you test production-like behavior locally.

Test:

```text
1. App starts with env vars.
2. App connects to real Postgres.
3. App caches in Redis.
4. App publishes Kafka events.
5. App survives dependency restart.
6. App logs to stdout.
7. Health endpoint works.
```

Example failure test:

```bash
docker compose stop redis
```

Observe:

```text
Does app fail?
Does cache fallback work?
Do errors stay controlled?
```

Restart:

```bash
docker compose start redis
```

Test Postgres restart:

```bash
docker compose restart postgres
```

Observe:

```text
Does Hikari reconnect?
Do requests recover?
```

Test Kafka down:

```bash
docker compose stop kafka
```

Observe:

```text
Does create API fail or continue?
Are Kafka publishes async?
Are errors logged?
```

ASCII:

```text
Kill dependency
      |
      v
Observe app behavior
      |
      v
Fix resilience
      |
      v
Retest
```

This is how you build production instincts.

---

## 29. Compose Profiles

Profiles allow optional services.

Example:

```yaml
services:
  app:
    build: .

  prometheus:
    image: prom/prometheus
    profiles:
      - observability

  grafana:
    image: grafana/grafana
    profiles:
      - observability
```

Run normal stack:

```bash
docker compose up -d
```

Run with observability:

```bash
docker compose --profile observability up -d
```

Use profiles for:

```text
observability
debug tools
admin UI
load testing
extra Spring Cloud services
```

Example MiniURLShortener profiles:

```text
core:
    app, postgres, redis, kafka

cloud:
    config-server, eureka, gateway

observability:
    prometheus, grafana, loki

loadtest:
    k6
```

ASCII:

```text
Base stack
   |
   +-- profile cloud
   |
   +-- profile observability
   |
   +-- profile loadtest
```

Profiles keep local stack manageable.

---

## 30. Logs And Observability

Compose logs:

```bash
docker compose logs -f
```

App logs should go to:

```text
stdout/stderr
```

Not only local files.

Why?

```text
Container platforms collect stdout/stderr.
```

Good log flow:

```text
Spring Boot app logs -> stdout
Docker captures logs
docker compose logs displays logs
future: Loki/ELK collects logs
```

ASCII:

```text
App
 |
 v
stdout/stderr
 |
 v
Docker logging driver
 |
 v
docker compose logs
```

For local debugging, include:

```text
request path
status
error code
correlation ID
latency
dependency errors
```

Useful health endpoint:

```bash
curl http://localhost:8080/actuator/health
```

Useful metrics endpoint:

```bash
curl http://localhost:8080/actuator/metrics
```

---

## 31. Production Failure Stories

### Failure Story 1: localhost Misconfiguration

App in container uses:

```text
jdbc:postgresql://localhost:5432/miniurl
```

Result:

```text
Connection refused.
```

Root cause:

```text
localhost inside app container is not Postgres container.
```

Fix:

```text
Use postgres:5432 in Compose.
```

Lesson:

```text
Service names are hostnames inside Compose.
```

---

### Failure Story 2: depends_on Without Readiness

Compose starts app after Postgres container starts.

But Postgres is still initializing.

Result:

```text
App fails at startup.
```

Fix:

```text
Add Postgres health check.
Use depends_on condition service_healthy.
Also make app resilient to reconnect.
```

Lesson:

```text
Started does not mean ready.
```

---

### Failure Story 3: Data Lost After down -v

Developer runs:

```bash
docker compose down -v
```

Result:

```text
Postgres volume deleted.
All local data gone.
```

Fix:

```text
Use down without -v unless you intentionally reset data.
```

Lesson:

```text
Volumes are data. Treat down -v carefully.
```

---

### Failure Story 4: Kafka Advertised Listener Wrong

Kafka tells app:

```text
connect to localhost:9092
```

App is inside container.

Result:

```text
App tries app-container localhost.
Kafka connection fails.
```

Fix:

```text
Set advertised listener to kafka:9092 for internal Compose clients.
```

Lesson:

```text
Kafka advertised listeners must match client network location.
```

---

### Failure Story 5: Real Secrets Committed

`docker-compose.yml` contains production passwords.

File is pushed to GitHub.

Result:

```text
Secrets leaked.
```

Fix:

```text
Rotate secrets.
Use local dummy credentials.
Use environment variables or secret manager for real environments.
```

Lesson:

```text
Compose files are code. Do not commit real secrets.
```

---

## 32. Common Mistakes

### Mistake 1: Using localhost between containers

Wrong:

```text
app -> localhost:5432
```

Correct:

```text
app -> postgres:5432
```

### Mistake 2: Thinking ports are needed for container-to-container traffic

Wrong:

```text
App cannot reach Postgres unless ports are published.
```

Correct:

```text
Same Compose network is enough.
Ports are for host access.
```

### Mistake 3: depends_on means ready

Wrong:

```text
depends_on guarantees DB ready.
```

Correct:

```text
Use health checks.
```

### Mistake 4: No volumes for database

Wrong:

```text
Postgres data stored only in container.
```

Correct:

```text
Use named volume.
```

### Mistake 5: Real secrets in Compose

Wrong:

```yaml
POSTGRES_PASSWORD: real-prod-password
```

Correct:

```text
Use local dummy secrets or runtime secret management.
```

### Mistake 6: Kafka listener mismatch

Wrong:

```text
KAFKA_ADVERTISED_LISTENERS=localhost:9092
```

for app inside Compose.

Correct:

```text
kafka:9092
```

### Mistake 7: Not checking logs

Wrong:

```text
Guessing why app failed.
```

Correct:

```bash
docker compose logs -f app
```

### Mistake 8: Not rebuilding image

Wrong:

```text
Code changed but old image still running.
```

Correct:

```bash
docker compose up -d --build
```

---

## 33. Interview-Ready Explanation

If interviewer asks:

```text
How would you run your Spring Boot URL shortener locally with dependencies?
```

Strong answer:

```text
I would use Docker Compose to define a local multi-container environment with the
Spring Boot app, Postgres, Redis, and Kafka on the same Compose network. The app
container would connect to dependencies using service names such as postgres:5432,
redis:6379, and kafka:9092, not localhost, because localhost inside a container
means the container itself. I would publish only the ports needed from the host,
such as 8080 for the app and optionally 5432 for local DB tools. Postgres and Redis
would use named volumes so data survives container recreation. I would use
environment variables to inject Spring datasource, Redis, Kafka, and profile config.
For startup, I would add health checks and use depends_on with service_healthy for
dependencies like Postgres. I would use docker compose logs and exec commands for
debugging, and I would avoid committing real secrets in the Compose file.
```

Why this is strong:

```text
1. Explains service names.
2. Explains localhost trap.
3. Explains ports.
4. Explains volumes.
5. Explains env vars.
6. Explains health checks.
7. Explains debugging.
8. Explains secret safety.
```

Senior one-liner:

```text
Docker Compose is the local wiring diagram that makes a multi-service backend run like a small production network.
```

---

## 34. Senior Engineer Checklist

Before using Compose as local integration stack:

```text
[ ] app service builds from Dockerfile
[ ] postgres service has named volume
[ ] redis service has named volume if persistence needed
[ ] app uses postgres, redis, kafka service names
[ ] app does not use localhost for dependencies
[ ] host ports are published only when needed
[ ] env vars configure Spring Boot
[ ] real secrets are not committed
[ ] Postgres health check exists
[ ] Redis health check exists
[ ] app health endpoint exists
[ ] depends_on uses health where useful
[ ] Kafka advertised listener matches app network
[ ] logs go to stdout/stderr
[ ] docker compose commands documented
[ ] down -v warning understood
[ ] profiles used for optional tools
```

If these are checked, your Compose setup is production-shaped for local development.

---

## 35. One-Page Cheat Sheet

```text
Core mental model:
Docker Compose = local multi-container network.
Service name = hostname.

Dockerfile:
build one image

Docker Compose:
run many services together

Common service names:
app
postgres
redis
kafka
config-server
eureka-server
gateway

Inside app container:
Postgres -> postgres:5432
Redis    -> redis:6379
Kafka    -> kafka:9092

From host:
App      -> localhost:8080
Postgres -> localhost:5432 if port mapped
Redis    -> localhost:6379 if port mapped

Ports:
hostPort:containerPort

Volumes:
persist data beyond container lifecycle

depends_on:
controls startup order, not readiness unless health condition used

Health check:
started != ready

Commands:
docker compose up -d
docker compose up -d --build
docker compose down
docker compose down -v
docker compose ps
docker compose logs -f app
docker compose exec postgres psql -U miniurl -d miniurl

Golden rule:
Use service names inside Compose.
Use localhost from host machine.
```

---

## 36. One Picture To Remember

```text
                 DOCKER COMPOSE MENTAL MODEL

                    "Local mini-production network"

HOST MACHINE
+--------------------------------------------------+
| Browser / Postman                                |
| http://localhost:8080                            |
+--------------------------|-----------------------+
                           |
                           v
               port mapping 8080:8080
                           |
                           v
COMPOSE NETWORK
+--------------------------------------------------+
|                                                  |
|  +-------------------+                           |
|  | app               |                           |
|  | Spring Boot       |                           |
|  +---------+---------+                           |
|            |                                     |
|            | jdbc:postgresql://postgres:5432     |
|            v                                     |
|  +-------------------+                           |
|  | postgres          |---- volume postgres-data  |
|  +-------------------+                           |
|                                                  |
|            | redis:6379                          |
|            v                                     |
|  +-------------------+                           |
|  | redis             |---- volume redis-data     |
|  +-------------------+                           |
|                                                  |
|            | kafka:9092                          |
|            v                                     |
|  +-------------------+                           |
|  | kafka             |---- volume kafka-data     |
|  +-------------------+                           |
|                                                  |
+--------------------------------------------------+


FINAL MEMORY:

Inside Compose:
    service name is hostname.

Inside app container:
    localhost means app container.

From host:
    localhost uses published ports.

Volumes preserve data.
Health checks prove readiness.
Compose is local integration infrastructure.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Docker Compose runs multiple containers together on a shared local network.
2. Inside Compose, services communicate using service names like postgres, redis, and kafka.
3. localhost inside a container means that same container, not another service.
4. Ports expose containers to the host, while service names handle container-to-container traffic.
5. Volumes preserve data, health checks prove readiness, and env vars configure runtime behavior.
```

Next possible chapters:

```text
049_Observability_Metrics_Tracing.md
050_Kubernetes_Deployment.md
051_CI_CD_Docker_Image_Build.md
052_Production_Docker_Debugging.md
