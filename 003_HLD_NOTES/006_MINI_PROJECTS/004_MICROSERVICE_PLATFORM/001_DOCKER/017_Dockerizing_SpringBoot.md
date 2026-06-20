# 017_Dockerizing_SpringBoot

> MiniDocker Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Docker + Production Debugging

---

# 1. Why Dockerizing Spring Boot Exists

A Spring Boot application is usually born as Java code, then compiled into a `.jar`, then copied to some server, then started using `java -jar app.jar`.

That works on your laptop.

But production is not one laptop.

Production has:

```text
Developer Laptop
CI Server
Staging Server
Production VM
Kubernetes Node
Rollback Environment
```

If each machine has a slightly different Java version, timezone, environment variable, filesystem path, or dependency, the application may behave differently.

The purpose of Dockerizing Spring Boot is simple:

```text
Package application + runtime expectation + startup command
into one repeatable unit.
```

Mental model:

```text
Without Docker

Code ---> JAR ---> "Please install Java correctly" ---> Server

With Docker

Code ---> JAR ---> Image ---> Same Container Everywhere
```

Do not memorize Docker commands first. Understand this:

```text
Docker Image = Delivery Box
Container    = Running Box
Dockerfile   = Packing Instructions
Registry     = Warehouse
Kubernetes   = Delivery Manager
```

---

# 2. Real World Analogy: Restaurant Food Delivery

Imagine you own a restaurant.

Bad delivery model:

```text
Send raw ingredients to customer
Tell customer:
- use this stove
- use this pan
- cook for 20 minutes
- add salt manually
```

Every customer gets different result.

Docker model:

```text
Restaurant cooks food
Packs it safely
Labels it
Sends same package everywhere
```

Spring Boot without Docker:

```text
Server must know:
- Java version
- JAR location
- environment variables
- logs path
- startup command
- timezone
- secrets
```

Spring Boot with Docker:

```text
Image contains:
- base OS layer
- JVM layer
- app jar layer
- startup command
```

Diagram:

```text
+------------------------------+
| Docker Image                 |
|                              |
|  Base OS / Runtime Layer     |
|  Java Runtime Layer          |
|  Spring Boot JAR Layer       |
|  Entrypoint Command          |
|                              |
+------------------------------+
              |
              v
+------------------------------+
| Running Container            |
| java -jar app.jar            |
+------------------------------+
```

The goal is not “Docker command knowledge”. The goal is repeatable deployment.

---

# 3. Not-To-Memorize Model

Do not memorize:

```text
FROM
COPY
EXPOSE
ENTRYPOINT
CMD
```

Understand them as a story:

```text
FROM       = choose starting room
COPY       = put your app inside the room
EXPOSE     = document which door app listens on
ENTRYPOINT = default action when room starts
```

One picture:

```text
Dockerfile
   |
   v
Image Build
   |
   v
Immutable Image
   |
   v
Container Runtime
   |
   v
Spring Boot Process
```

Spring Boot is still just a Java process:

```text
java -jar app.jar
```

Docker does not magically understand Spring Boot. It only starts a process inside an isolated environment.

```text
Container
+-----------------------------------+
| PID 1: java                       |
| Args: -jar app.jar                |
| Port: 8080                        |
| Env: SPRING_PROFILES_ACTIVE=prod  |
+-----------------------------------+
```

Important mindset:

```text
If it fails in Docker, debug it as:
1. Did image build correctly?
2. Did container start correctly?
3. Did Java process start correctly?
4. Did Spring profile/config load correctly?
5. Is the port reachable?
6. Can dependencies be reached?
```

---

# 4. Spring Boot Build Lifecycle Before Docker

Typical Spring Boot Maven build:

```bash
mvn clean package
```

Flow:

```text
Java Source Code
      |
      v
Maven Compile
      |
      v
Unit Tests
      |
      v
Repackage Spring Boot Fat JAR
      |
      v
target/order-service-0.0.1-SNAPSHOT.jar
```

ASCII map:

```text
src/main/java
src/main/resources
pom.xml
     |
     v
+------------------+
| Maven Build      |
+------------------+
     |
     v
+-----------------------------------------+
| target/order-service.jar                |
|                                         |
| BOOT-INF/classes                        |
| BOOT-INF/lib/*.jar                      |
| META-INF                                |
+-----------------------------------------+
```

A Spring Boot fat jar contains your application classes and dependencies.

Run locally:

```bash
java -jar target/order-service.jar
```

Dockerizing means placing this jar into an image and defining how it starts.

---

# 5. Minimal Spring Boot Application

Example controller:

```java
package com.example.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "order-service is running";
    }
}
```

Application class:

```java
package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

Local lifecycle:

```text
main()
  |
  v
SpringApplication.run()
  |
  v
Create ApplicationContext
  |
  v
Start Embedded Tomcat
  |
  v
Listen on 8080
```

Inside Docker, this lifecycle is same. Only the environment changes.

---

# 6. First Dockerfile: Simple But Not Production-Perfect

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/order-service.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Mental model:

```text
FROM eclipse-temurin:21-jre
        |
        v
Container has Java runtime
        |
        v
COPY app.jar into /app
        |
        v
When container starts, run java -jar app.jar
```

Build:

```bash
docker build -t order-service:1.0 .
```

Run:

```bash
docker run -p 8080:8080 order-service:1.0
```

Packet flow:

```text
Browser
  |
  | localhost:8080/health
  v
Host Port 8080
  |
  v
Docker NAT
  |
  v
Container Port 8080
  |
  v
Embedded Tomcat
  |
  v
HealthController
```

This works, but production needs more discipline.

---

# 7. Build Context Mental Model

When you run:

```bash
docker build -t order-service:1.0 .
```

The final `.` means current directory becomes the build context.

```text
Project Folder
+-------------------------+
| Dockerfile              |
| pom.xml                 |
| src/                    |
| target/order-service.jar|
| .git/                   |
| logs/                   |
+-------------------------+
        |
        v
Sent to Docker daemon as build context
```

Problem:

```text
If build context is huge, build becomes slow.
```

Use `.dockerignore`:

```dockerignore
.git
.idea
*.iml
logs
node_modules
target/*.original
```

Mental model:

```text
.dockerignore = luggage filter
Only pack what image build needs
```

---

# 8. Layer Model For Spring Boot Dockerfile

Docker builds images as layers.

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/order-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Layer picture:

```text
+-------------------------------------+
| ENTRYPOINT metadata                 |
+-------------------------------------+
| COPY app.jar                        |
+-------------------------------------+
| WORKDIR /app                        |
+-------------------------------------+
| eclipse-temurin:21-jre              |
+-------------------------------------+
```

If the jar changes, the `COPY app.jar` layer changes.

```text
Code change
   |
   v
New JAR
   |
   v
COPY layer cache miss
   |
   v
New image layer
```

For simple apps, this is okay. For faster CI/CD, multi-stage and layered jars help.

---

# 9. Multi-Stage Dockerfile For Spring Boot

Instead of building jar outside Docker, we can build inside Docker.

```dockerfile
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Diagram:

```text
Stage 1: builder
+--------------------------------+
| Maven + JDK                    |
| Source Code                    |
| mvn clean package              |
| produces target/*.jar          |
+---------------+----------------+
                |
                | COPY --from=builder
                v
Stage 2: runtime
+--------------------------------+
| JRE only                       |
| app.jar                        |
| java -jar app.jar              |
+--------------------------------+
```

Why this is better:

```text
Builder has heavy tools: Maven, JDK, source code
Runtime has only what is needed to run
```

Production mindset:

```text
Do not ship your workshop to customers.
Ship only the finished product.
```

---

# 10. Maven Cache Optimization

Naive multi-stage:

```dockerfile
COPY pom.xml .
COPY src ./src
RUN mvn clean package
```

Every source code change may force dependency checks again.

Better:

```dockerfile
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Cache flow:

```text
pom.xml unchanged
      |
      v
Dependency layer reused
      |
      v
Only src copied again
      |
      v
Only compile/package reruns
```

CI/CD benefit:

```text
Faster builds
Less network download
More predictable pipeline
```

---

# 11. Gradle Multi-Stage Dockerfile

```dockerfile
FROM gradle:8.10-jdk21 AS builder

WORKDIR /workspace
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Flow:

```text
Gradle Image
   |
   v
Build bootJar
   |
   v
Runtime Image receives only final jar
```

Common mistake:

```text
Copying entire Gradle cache into runtime image
```

Good production rule:

```text
Runtime image should not contain build tools.
```

---

# 12. Spring Boot Configuration In Docker

Spring Boot reads configuration from many places:

```text
application.yml
Environment Variables
Command Line Args
Config Server
Kubernetes ConfigMap
Secrets
```

Docker-friendly `application.yml`:

```yaml
server:
  port: ${SERVER_PORT:8080}

spring:
  application:
    name: order-service
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:orders}
    username: ${DB_USER:orders}
    password: ${DB_PASSWORD:orders}
```

Run with env:

```bash
docker run -p 8080:8080 \
  -e DB_HOST=postgres \
  -e DB_PORT=5432 \
  -e DB_NAME=orders \
  -e DB_USER=orders \
  -e DB_PASSWORD=secret \
  order-service:1.0
```

Mental model:

```text
Image = same everywhere
Environment = changes per place
```

Do not bake production passwords into images.

---

# 13. Docker Compose With Spring Boot + Postgres + Redis

```yaml
services:
  order-service:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_HOST: postgres
      DB_PORT: 5432
      DB_NAME: orders
      DB_USER: orders
      DB_PASSWORD: secret
      REDIS_HOST: redis
    depends_on:
      - postgres
      - redis

  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: orders
      POSTGRES_USER: orders
      POSTGRES_PASSWORD: secret
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7

volumes:
  postgres_data:
```

ASCII network:

```text
Docker Compose Network
+------------------------------------------------+
|                                                |
|  order-service                                 |
|      |                                         |
|      | DNS: postgres:5432                      |
|      v                                         |
|  postgres                                      |
|                                                |
|  order-service                                 |
|      |                                         |
|      | DNS: redis:6379                         |
|      v                                         |
|  redis                                         |
|                                                |
+------------------------------------------------+
```

Important:

```text
Inside Compose, use service names.
Do not use localhost for another container.
```

---

# 14. The Localhost Trap

Inside a container:

```text
localhost = same container
```

Not the host. Not Postgres. Not Redis.

Wrong:

```yaml
DB_HOST: localhost
```

Picture:

```text
order-service container
+-----------------------------+
| localhost points here       |
|                             |
| Spring Boot                 |
| No Postgres inside          |
+-----------------------------+
```

Correct:

```yaml
DB_HOST: postgres
```

Picture:

```text
order-service
   |
   | DNS lookup: postgres
   v
postgres container
```

This is one of the most common beginner Docker bugs.

---

# 15. Java Code: Calling Redis From Dockerized Spring Boot

Dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

Configuration:

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
```

Service:

```java
package com.example.order;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderCacheService {
    private final StringRedisTemplate redis;

    public OrderCacheService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void cacheStatus(String orderId, String status) {
        redis.opsForValue().set("order:" + orderId + ":status", status);
    }

    public String getStatus(String orderId) {
        return redis.opsForValue().get("order:" + orderId + ":status");
    }
}
```

Docker environment:

```yaml
REDIS_HOST: redis
```

Flow:

```text
Spring Boot
   |
   | REDIS_HOST=redis
   v
Docker DNS
   |
   v
Redis Container IP
   |
   v
redis:6379
```

---

# 16. Java Code: Calling Another Service

Use WebClient:

```java
package com.example.order;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UserClient {
    private final WebClient webClient;

    public UserClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://user-service:8080")
                .build();
    }

    public String getUser(String userId) {
        return webClient.get()
                .uri("/users/{id}", userId)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
```

Docker Compose service name:

```yaml
user-service:
  image: user-service:1.0
```

Communication:

```text
order-service
   |
   | http://user-service:8080/users/10
   v
Docker DNS
   |
   v
user-service container
```

Do not hardcode container IP.

---

# 17. Health Check Endpoint

Spring Boot Actuator dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Config:

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

Dockerfile healthcheck:

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1
```

Container view:

```text
Container
+--------------------------------+
| Spring Boot                    |
| /actuator/health               |
|                                |
| Docker healthcheck calls it    |
+--------------------------------+
```

Production lesson:

```text
Running process is not equal to healthy app.
```

---

# 18. Startup Order Problem

`depends_on` means start order, not readiness.

```text
docker compose up
   |
   +--> postgres container starts
   |
   +--> order-service container starts
```

But Postgres may not be ready yet.

Failure:

```text
order-service starts
   |
   v
tries DB connection
   |
   v
Postgres still initializing
   |
   v
Connection refused
```

Better options:

```text
1. Application retry connection
2. Use healthchecks
3. Use migration tools carefully
4. Let orchestration restart failed container
```

Spring Boot datasource retry is often handled by connection pool and app retry logic, but schema migration tools like Flyway can fail early if DB is not ready.

---

# 19. Logging In Containers

Bad production habit:

```text
Write logs only to /app/logs/app.log inside container
```

Better:

```text
Write logs to stdout/stderr
Docker collects them
Kubernetes collects them
Log agent ships them
```

Flow:

```text
Spring Boot Logger
   |
   v
stdout/stderr
   |
   v
Docker logs
   |
   v
Fluent Bit / Log Agent
   |
   v
Elasticsearch / Loki / Cloud Logging
```

Command:

```bash
docker logs -f order-service
```

Container principle:

```text
Container filesystem is disposable.
Logs should leave the container.
```

---

# 20. JVM Memory In Docker

Containers have limits.

```bash
docker run -m 512m order-service:1.0
```

JVM must respect container memory.

Recommended runtime options:

```bash
java -XX:MaxRAMPercentage=75.0 -jar app.jar
```

Dockerfile:

```dockerfile
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
```

Memory picture:

```text
Container Limit: 512 MB
+--------------------------------+
| JVM Heap                       |
| Metaspace                      |
| Thread Stacks                  |
| Native Memory                  |
| Direct Buffers                 |
+--------------------------------+
```

If heap uses everything, container may be killed.

Symptom:

```text
Exit code 137 = likely OOM kill
```

---

# 21. Non-Root User In Docker Image

Running as root is risky.

Better Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

RUN addgroup --system spring && adduser --system spring --ingroup spring

COPY target/order-service.jar app.jar

USER spring:spring

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
```

Mental model:

```text
Root container process
   |
   | if exploited
   v
More dangerous permissions

Non-root process
   |
   | if exploited
   v
Reduced blast radius
```

Production checklist item:

```text
Run application as non-root unless there is a strong reason not to.
```

---

# 22. Distroless Runtime Image

Distroless images contain only runtime essentials.

```dockerfile
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM gcr.io/distroless/java21-debian12
WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
```

Tradeoff:

```text
Smaller attack surface
Less shell/debug tooling inside image
```

Debug mindset:

```text
Distroless is good for production.
But debugging requires logs, metrics, sidecars, or temporary debug containers.
```

Do not choose distroless because it sounds fancy. Choose it when your observability is ready.

---

# 23. Layered Spring Boot JAR

Spring Boot can split jar layers:

```text
dependencies
spring-boot-loader
snapshot-dependencies
application
```

Why?

```text
Dependencies change rarely.
Application code changes often.
```

Layered Dockerfile example:

```dockerfile
FROM eclipse-temurin:21-jre AS extractor
WORKDIR /app
COPY target/order-service.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=extractor /app/dependencies/ ./
COPY --from=extractor /app/spring-boot-loader/ ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

Layer cache:

```text
Code-only change
   |
   v
Only application layer changes
   |
   v
Faster image push/pull
```

Useful for large services and frequent deployments.

---

# 24. Image Tagging Strategy

Bad:

```bash
docker build -t order-service:latest .
```

Better:

```bash
docker build -t order-service:1.0.0 .
docker build -t order-service:git-a1b2c3d .
```

Production image identity:

```text
Image Tag = human label
Image Digest = exact immutable identity
```

CI/CD flow:

```text
Git Commit
   |
   v
Build Image
   |
   v
Tag with commit SHA
   |
   v
Push Registry
   |
   v
Deploy exact version
```

Rollback:

```text
Bad rollback: deploy whatever latest means today
Good rollback: deploy previous immutable tag/digest
```

---

# 25. Docker Registry Flow

```bash
docker tag order-service:1.0 registry.example.com/order-service:1.0
docker push registry.example.com/order-service:1.0
```

Picture:

```text
Developer / CI
     |
     | docker push
     v
Registry
     |
     | docker pull
     v
Production Server / Kubernetes Node
```

Registry is not just storage. It is the distribution point for deployments.

Mental model:

```text
Image = package
Registry = warehouse
Deployment = delivery request
Node = machine receiving package
```

---

# 26. Kubernetes Deployment Example

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
        - name: order-service
          image: registry.example.com/order-service:1.0.0
          ports:
            - containerPort: 8080
          env:
            - name: DB_HOST
              value: postgres
            - name: REDIS_HOST
              value: redis
          resources:
            requests:
              memory: "512Mi"
              cpu: "250m"
            limits:
              memory: "1Gi"
              cpu: "1000m"
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 20
            periodSeconds: 10
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 20
```

Flow:

```text
Kubernetes Deployment
   |
   v
ReplicaSet
   |
   v
Pods
   |
   v
Container image pulled
   |
   v
Spring Boot starts
   |
   v
Readiness passes
   |
   v
Traffic allowed
```

---

# 27. Readiness vs Liveness

Readiness:

```text
Can this pod receive traffic now?
```

Liveness:

```text
Is this pod stuck and should be restarted?
```

Diagram:

```text
Pod Starting
   |
   v
Liveness OK? maybe yes
Readiness OK? no
   |
   v
No traffic yet
   |
   v
App ready
   |
   v
Service sends traffic
```

Common mistake:

```text
Aggressive liveness probe kills slow-starting Spring Boot app repeatedly.
```

Better:

```text
Use longer liveness delay.
Use readiness to control traffic.
```

---

# 28. Production Failure Story: Works Locally, Fails In Docker

Scenario:

```text
Developer runs locally:
java -jar order-service.jar
Works.

Docker Compose:
order-service cannot connect to database.
```

Bad config:

```yaml
DB_HOST: localhost
```

Actual meaning:

```text
order-service container tries localhost
localhost points to order-service container itself
No Postgres there
Connection refused
```

Fix:

```yaml
DB_HOST: postgres
```

Debug commands:

```bash
docker compose ps
docker compose logs order-service
docker compose exec order-service sh
```

Inside container:

```bash
getent hosts postgres
nc -vz postgres 5432
```

Debugging mindset:

```text
Do not blame Spring first.
Trace name resolution, port, process, and config.
```

---

# 29. Production Failure Story: Container Starts But App Not Reachable

Symptoms:

```text
docker ps shows running
curl localhost:8080 fails
```

Possible causes:

```text
1. Forgot -p 8080:8080
2. App listens on different port
3. App bound to wrong interface
4. Container crashed after startup
5. Firewall or host issue
```

Packet path:

```text
Client curl localhost:8080
   |
   v
Host port published?
   |
   v
Docker NAT rule exists?
   |
   v
Container port 8080 open?
   |
   v
Spring Boot listening?
```

Commands:

```bash
docker ps
docker port order-service
docker logs order-service
docker exec -it order-service sh
```

Inside:

```bash
wget -qO- http://localhost:8080/actuator/health
```

---

# 30. Production Failure Story: OOMKilled

Symptoms:

```text
Container exits randomly
Kubernetes shows OOMKilled
Docker exit code 137
```

Cause:

```text
JVM + native memory exceeded container memory limit
```

Picture:

```text
1Gi Container Limit
+--------------------------------+
| Heap 800Mi                     |
| Metaspace 120Mi                |
| Thread stacks 100Mi            |
| Direct buffers 100Mi           |
| Native overhead                |
+--------------------------------+
Total > 1Gi -> killed
```

Fix options:

```text
1. Tune MaxRAMPercentage
2. Increase container limit
3. Reduce thread count
4. Investigate memory leak
5. Monitor heap and non-heap
```

Dockerfile:

```dockerfile
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70.0", "-jar", "app.jar"]
```

---

# 31. Debugging Playbook

Use this order:

```text
1. Image exists?
2. Container starts?
3. Logs clean?
4. Port published?
5. App listening?
6. Environment loaded?
7. DNS resolves?
8. Dependency reachable?
9. Health endpoint healthy?
10. Memory/CPU stable?
```

Commands:

```bash
docker images
docker ps -a
docker logs -f order-service
docker inspect order-service
docker exec -it order-service sh
docker network inspect bridge
docker stats
```

For Compose:

```bash
docker compose ps
docker compose logs -f
docker compose exec order-service sh
docker compose down -v
```

Warning:

```text
docker compose down -v removes volumes.
Do not run blindly on important local data.
```

---

# 32. Common Dockerizing Mistakes

```text
Mistake 1: Using localhost for another container
Fix: Use service name

Mistake 2: Baking secrets into image
Fix: Use env/secrets manager

Mistake 3: Shipping Maven/JDK in runtime image
Fix: Multi-stage build

Mistake 4: Running as root
Fix: Non-root user

Mistake 5: No health endpoint
Fix: Actuator readiness/liveness

Mistake 6: Using only latest tag
Fix: Use version/commit tags

Mistake 7: Writing logs only to file
Fix: stdout/stderr

Mistake 8: Ignoring JVM memory
Fix: MaxRAMPercentage + limits
```

---

# 33. CI/CD Pipeline Mental Model

```text
Git Push
   |
   v
CI Checkout
   |
   v
Run Tests
   |
   v
Build JAR
   |
   v
Build Docker Image
   |
   v
Scan Image
   |
   v
Push Registry
   |
   v
Deploy Staging
   |
   v
Smoke Test
   |
   v
Deploy Production
```

GitHub Actions example:

```yaml
name: build-order-service

on:
  push:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'

      - name: Test
        run: mvn test

      - name: Package
        run: mvn clean package -DskipTests

      - name: Build Docker image
        run: docker build -t order-service:${{ github.sha }} .
```

---

# 34. Security Checklist

```text
[ ] Use small trusted base image
[ ] Avoid root user
[ ] Do not copy secrets into image
[ ] Use .dockerignore
[ ] Pin important image versions
[ ] Scan image vulnerabilities
[ ] Expose only required ports
[ ] Use read-only filesystem when possible
[ ] Set memory/CPU limits
[ ] Keep dependencies updated
```

Security mental model:

```text
Container is not a security force field.
It is isolation plus configuration.
Bad image hygiene still creates risk.
```

---

# 35. Strong Interview Answers

## What does Dockerizing a Spring Boot app mean?

It means packaging the Spring Boot jar, Java runtime expectations, filesystem layout, environment configuration style, and startup command into a Docker image so the app can run consistently across developer machines, CI, servers, and Kubernetes.

## Why use multi-stage builds?

Multi-stage builds separate the build environment from the runtime environment. Maven/JDK/source code stay in the builder stage, while the final image contains only the JRE and application artifact. This reduces image size and attack surface.

## Why should containers use environment variables?

The image should be immutable and environment-independent. Runtime differences such as database host, Redis host, profiles, and credentials should be injected through environment variables, ConfigMaps, or Secrets.

## Why is localhost dangerous inside Docker?

Inside a container, localhost points to the same container. If Spring Boot uses `localhost` for Postgres, it searches for Postgres inside the Spring Boot container, not in the Postgres container. In Compose, use service names like `postgres` or `redis`.

## How do you debug a Dockerized Spring Boot app?

Check container status, logs, published ports, environment variables, DNS resolution, dependency reachability, health endpoints, and memory usage. Trace from host request to container port to Spring Boot process.

---

# 36. Cheat Sheet

```text
Dockerfile       = image recipe
Image            = packaged application
Container        = running image
Registry         = image warehouse
Tag              = version label
Digest           = exact image identity
ENTRYPOINT       = startup command
EXPOSE           = documented container port
-p 8080:8080     = host port to container port
Env vars         = runtime configuration
Volume           = persistent data
Compose service  = DNS name inside Compose network
```

Commands:

```bash
mvn clean package

docker build -t order-service:1.0 .
docker run -p 8080:8080 order-service:1.0

docker ps
docker logs -f order-service
docker exec -it order-service sh
docker inspect order-service
docker stats

docker compose up --build
docker compose logs -f
docker compose down
```

---

# 37. One Picture To Remember

```text
Developer Code
     |
     v
Maven / Gradle Build
     |
     v
Spring Boot JAR
     |
     v
Docker Build
     |
     v
Docker Image
     |
     v
Registry
     |
     v
Server / Kubernetes Node
     |
     v
Running Container
     |
     v
java -jar app.jar
     |
     v
Spring Boot API
```

Rule:

```text
Dockerizing is not about memorizing Docker commands.
It is about making Spring Boot deployment repeatable, portable, observable, and production-safe.
```

---

# 38. Final Production Checklist

```text
[ ] Multi-stage Dockerfile used
[ ] Runtime image does not contain build tools
[ ] .dockerignore added
[ ] App configured through environment variables
[ ] No secrets inside image
[ ] Actuator health enabled
[ ] Readiness/liveness configured for Kubernetes
[ ] Logs go to stdout/stderr
[ ] JVM memory tuned for container limits
[ ] Non-root user used
[ ] Image tag uses version or commit SHA
[ ] Compose uses service names, not localhost
[ ] Postgres data stored in volume
[ ] Redis/Postgres dependency connection tested
[ ] Image can be rebuilt from scratch in CI
```

