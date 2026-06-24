# 047_Dockerfile.md
# MiniURLShortener — Dockerfile

> Core mental model: **A Dockerfile is a repeatable recipe that turns your source code into a portable application image. Each instruction creates a layer, and good Dockerfiles optimize layers for speed, size, security, and production reliability.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What Is A Dockerfile?](#4-what-is-a-dockerfile)
- [5. Image vs Container](#5-image-vs-container)
- [6. Dockerfile Build Flow](#6-dockerfile-build-flow)
- [7. Layer Mental Model](#7-layer-mental-model)
- [8. Docker Build Cache](#8-docker-build-cache)
- [9. Important Dockerfile Instructions](#9-important-dockerfile-instructions)
- [10. Bad First Dockerfile](#10-bad-first-dockerfile)
- [11. Good Simple Spring Boot Dockerfile](#11-good-simple-spring-boot-dockerfile)
- [12. Multi-Stage Dockerfile](#12-multi-stage-dockerfile)
- [13. Maven Dependency Cache Optimization](#13-maven-dependency-cache-optimization)
- [14. Spring Boot Layered JAR Mental Model](#14-spring-boot-layered-jar-mental-model)
- [15. Production Dockerfile For MiniURLShortener](#15-production-dockerfile-for-miniurlshortener)
- [16. .dockerignore](#16-dockerignore)
- [17. Running The Image](#17-running-the-image)
- [18. Environment Variables](#18-environment-variables)
- [19. Dockerfile For Local Development](#19-dockerfile-for-local-development)
- [20. Docker Compose Connection Mental Model](#20-docker-compose-connection-mental-model)
- [21. Security Best Practices](#21-security-best-practices)
- [22. Image Size Optimization](#22-image-size-optimization)
- [23. JVM Container Settings](#23-jvm-container-settings)
- [24. Health Check Mindset](#24-health-check-mindset)
- [25. Step-by-Step Dry Runs](#25-step-by-step-dry-runs)
- [26. Internal Execution Walkthrough](#26-internal-execution-walkthrough)
- [27. Common Docker Commands](#27-common-docker-commands)
- [28. Debugging Mindset](#28-debugging-mindset)
- [29. Production Failure Stories](#29-production-failure-stories)
- [30. Common Mistakes](#30-common-mistakes)
- [31. Interview-Ready Explanation](#31-interview-ready-explanation)
- [32. Senior Engineer Checklist](#32-senior-engineer-checklist)
- [33. One-Page Cheat Sheet](#33-one-page-cheat-sheet)
- [34. One Picture To Remember](#34-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener is becoming a real production-style backend.

So far we have:

```text
Spring Boot APIs
Postgres
Redis
Kafka
Spring Cloud pieces
Gateway
Config Server
Eureka
Resilience4j
```

But production systems cannot rely on:

```text
"works on my machine"
```

A backend should run the same way on:

```text
developer laptop
CI pipeline
Docker Compose
Kubernetes
staging
production
```

Dockerfile solves this by packaging:

```text
application code
runtime
dependencies
startup command
environment expectations
```

Without Dockerfile:

```text
Developer A uses Java 17.
Developer B uses Java 21.
CI has missing Maven.
Production server has wrong JDK.
App fails randomly.
```

With Dockerfile:

```text
Build once.
Run anywhere Docker-compatible.
```

ASCII:

```text
Source Code
    |
    v
Dockerfile Recipe
    |
    v
Docker Image
    |
    v
Container Running App
```

Production memory:

```text
Dockerfile is not just packaging.
It is part of your deployment contract.
```

---

## 2. The One Core Mental Model

A Dockerfile is like a recipe.

```text
Recipe ingredients:
    base image
    app files
    dependencies
    commands
    environment
    startup instruction

Recipe result:
    immutable image
```

ASCII:

```text
+-------------------------+
| Dockerfile              |
|                         |
| FROM java runtime       |
| COPY app.jar            |
| EXPOSE 8080             |
| ENTRYPOINT java -jar    |
+-------------------------+
             |
             v
+-------------------------+
| Docker Image            |
| read-only filesystem    |
| app + runtime           |
+-------------------------+
             |
             v
+-------------------------+
| Container               |
| running process         |
+-------------------------+
```

One-line memory:

```text
Dockerfile builds the image; image starts the container; container runs one main process.
```

For MiniURLShortener:

```text
Dockerfile should produce a Spring Boot application image.
That image should connect to Postgres, Redis, Kafka, Config Server, and Eureka using environment variables.
```

---

## 3. Problem Statement

Create a production-quality Dockerfile model for MiniURLShortener.

It must support:

```text
1. Build Spring Boot application reliably.
2. Produce small runtime image.
3. Use multi-stage build.
4. Avoid copying unnecessary files.
5. Use Docker build cache correctly.
6. Run as non-root user.
7. Configure JVM for containers.
8. Use environment variables for runtime config.
9. Work with Docker Compose and Kubernetes later.
10. Be easy to debug.
```

Out of scope:

```text
1. Full Kubernetes deployment YAML.
2. Full CI/CD pipeline.
3. Docker networking deep dive.
4. Docker volume deep dive.
5. Image signing and SBOM deep dive.
```

This chapter focuses on Dockerfile.

---

## 4. What Is A Dockerfile?

A Dockerfile is a text file containing instructions.

Example:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/mini-url-shortener.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Meaning:

```text
FROM:
    Start from Java runtime image.

WORKDIR:
    Use /app as working directory.

COPY:
    Copy built JAR into image.

EXPOSE:
    Document that app listens on port 8080.

ENTRYPOINT:
    Start app when container runs.
```

ASCII:

```text
Dockerfile
   |
   v
docker build
   |
   v
image: mini-url-shortener:latest
   |
   v
docker run
   |
   v
Spring Boot app process
```

Important:

```text
Dockerfile does not run your app during build.
It creates an image that can run your app later.
```

---

## 5. Image vs Container

This is critical.

Image:

```text
Read-only package.
Like a class.
```

Container:

```text
Running instance of image.
Like an object.
```

ASCII:

```text
              docker run
Image --------------------------> Container

mini-url-shortener:1.0           running JVM process
read-only layers                 writable container layer
```

Analogy:

```text
Image = cake mold / recipe result
Container = actual running cake served
```

Better programming analogy:

```text
Image = class
Container = object
```

You can run many containers from one image:

```text
               +--> container A
Image: app ----+--> container B
               +--> container C
```

Each container has:

```text
own process
own network namespace
own filesystem writable layer
own environment variables
```

---

## 6. Dockerfile Build Flow

Command:

```bash
docker build -t mini-url-shortener:047 .
```

Flow:

```text
1. Docker reads Dockerfile.
2. Docker sends build context.
3. Docker executes instructions from top to bottom.
4. Each instruction creates/reuses a layer.
5. Final image is tagged.
```

ASCII:

```text
Project folder
     |
     v
Build Context
     |
     v
Dockerfile instructions
     |
     v
Layer 1: base image
Layer 2: workdir
Layer 3: copied files
Layer 4: app metadata
     |
     v
Final Image
```

Build context means:

```text
Files Docker can see during build.
```

If your project has large files:

```text
target/
.git/
logs/
node_modules/
```

They may be sent to Docker unless ignored.

That is why `.dockerignore` matters.

---

## 7. Layer Mental Model

Every important Dockerfile instruction creates a layer.

Example:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Layer view:

```text
+--------------------------------------+
| Layer 4: ENTRYPOINT metadata         |
+--------------------------------------+
| Layer 3: app.jar copied              |
+--------------------------------------+
| Layer 2: /app workdir metadata       |
+--------------------------------------+
| Layer 1: eclipse-temurin:21-jre base |
+--------------------------------------+
```

Layers are cached.

If a layer does not change, Docker can reuse it.

This is why instruction order matters.

Bad:

```dockerfile
COPY . .
RUN mvn package
```

Every code change invalidates everything.

Better:

```dockerfile
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package
```

Dependencies change less often than source code.

ASCII cache idea:

```text
pom.xml changed?
   |
   +-- no --> reuse dependency layer
   |
   +-- yes -> download dependencies again
```

---

## 8. Docker Build Cache

Docker cache speeds up builds.

Suppose:

```dockerfile
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package
```

If only Java code changes:

```text
COPY pom.xml unchanged
dependency layer reused
COPY src changed
mvn package reruns
```

ASCII:

```text
Build 1:
FROM                executed
COPY pom.xml        executed
RUN dependencies    executed
COPY src            executed
RUN package         executed

Build 2 after code change:
FROM                cached
COPY pom.xml        cached
RUN dependencies    cached
COPY src            executed
RUN package         executed
```

Why this matters:

```text
Fast CI builds.
Fast local rebuilds.
Less network download.
Less developer waiting.
```

Cache rule:

```text
Put rarely changing instructions first.
Put frequently changing files later.
```

---

## 9. Important Dockerfile Instructions

### FROM

Base image.

```dockerfile
FROM eclipse-temurin:21-jre
```

### WORKDIR

Set working directory.

```dockerfile
WORKDIR /app
```

### COPY

Copy files from build context into image.

```dockerfile
COPY target/app.jar app.jar
```

### RUN

Run command during image build.

```dockerfile
RUN mkdir -p /app/logs
```

### ENV

Set environment variable.

```dockerfile
ENV SPRING_PROFILES_ACTIVE=prod
```

### EXPOSE

Document container port.

```dockerfile
EXPOSE 8080
```

Important:

```text
EXPOSE does not publish the port to host.
```

You still need:

```bash
docker run -p 8080:8080 image
```

### ENTRYPOINT

Main command.

```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### CMD

Default arguments.

```dockerfile
CMD ["--spring.profiles.active=prod"]
```

ENTRYPOINT + CMD:

```text
ENTRYPOINT = executable
CMD = default arguments
```

ASCII:

```text
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--server.port=8080"]

Final:
java -jar app.jar --server.port=8080
```

---

## 10. Bad First Dockerfile

Beginner Dockerfile:

```dockerfile
FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/mini-url-shortener.jar"]
```

Problems:

```text
1. Runtime image includes Maven.
2. Runtime image includes source code.
3. Image is large.
4. Build cache is poor.
5. Runs as root.
6. Copies .git, logs, target, local files if not ignored.
7. Uses CMD where ENTRYPOINT may be clearer.
8. Production image has unnecessary attack surface.
```

ASCII:

```text
Bad Image
+--------------------------+
| Maven                    |
| JDK                      |
| source code              |
| tests                    |
| target                   |
| local junk               |
| app runtime              |
+--------------------------+
```

Production goal:

```text
Build tools in build stage.
Only runtime and app in final stage.
```

---

## 11. Good Simple Spring Boot Dockerfile

If JAR is already built locally or in CI:

```dockerfile
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY target/mini-url-shortener.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build:

```bash
mvn clean package -DskipTests
docker build -t mini-url-shortener:simple .
```

Run:

```bash
docker run --rm -p 8080:8080 mini-url-shortener:simple
```

Pros:

```text
1. Simple.
2. Smaller than Maven runtime image.
3. Good if CI builds JAR first.
```

Cons:

```text
1. Docker build depends on prebuilt target JAR.
2. Less self-contained.
3. Local mistake if JAR missing.
```

ASCII:

```text
Host Maven Build
      |
      v
target/app.jar
      |
      v
Docker COPY
      |
      v
Runtime Image
```

This is acceptable for simple CI pipelines.

But multi-stage is better for repeatable Docker builds.

---

## 12. Multi-Stage Dockerfile

Multi-stage build separates build environment from runtime environment.

ASCII:

```text
Stage 1: Builder
+--------------------------+
| Maven + JDK              |
| source code              |
| mvn package              |
| creates app.jar          |
+--------------------------+
             |
             | copy only jar
             v
Stage 2: Runtime
+--------------------------+
| JRE only                 |
| app.jar                  |
| no Maven                 |
| no source code           |
+--------------------------+
```

Dockerfile:

```dockerfile
# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build:

```bash
docker build -t mini-url-shortener:multistage .
```

Benefits:

```text
1. Final image smaller.
2. Build is repeatable.
3. Runtime does not include Maven.
4. Runtime does not include source code.
5. Better production shape.
```

---

## 13. Maven Dependency Cache Optimization

Better multi-stage Dockerfile:

```dockerfile
# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Why better?

```text
pom.xml changes less often than src.
Docker can cache dependency downloads.
```

ASCII:

```text
COPY pom.xml
     |
     v
Download dependencies
     |
     v
COPY src
     |
     v
Compile app
```

If source changes:

```text
dependency layer reused
only compile layer reruns
```

CI build speed improves.

---

## 14. Spring Boot Layered JAR Mental Model

Spring Boot JAR contains different parts:

```text
dependencies
spring boot loader
snapshot dependencies
application classes
```

These change at different speeds.

ASCII:

```text
Spring Boot JAR
+----------------------------+
| dependencies               | changes rarely
+----------------------------+
| spring-boot-loader         | changes rarely
+----------------------------+
| snapshot-dependencies      | changes sometimes
+----------------------------+
| application classes        | changes often
+----------------------------+
```

Layered JAR helps Docker cache better.

Build plugin can enable layers.

Maven plugin example:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <layers>
                    <enabled>true</enabled>
                </layers>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Extract layers:

```bash
java -Djarmode=layertools -jar app.jar extract
```

Layered Dockerfile idea:

```dockerfile
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY dependencies/ ./
COPY spring-boot-loader/ ./
COPY snapshot-dependencies/ ./
COPY application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

Benefit:

```text
If only code changes, dependency layers are reused.
```

For interview:

```text
Layered JAR optimizes Docker cache by separating dependencies from frequently changing application classes.
```

---

## 15. Production Dockerfile For MiniURLShortener

Production-shaped Dockerfile:

```dockerfile
# syntax=docker/dockerfile:1

# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Create non-root user and group
RUN groupadd --system appgroup && \
    useradd --system --gid appgroup --home-dir /app appuser

COPY --from=builder /build/target/*.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

Why this is production-shaped:

```text
1. Multi-stage build.
2. Maven not present in final image.
3. Runs as non-root user.
4. Uses Java 21 runtime.
5. JVM memory is container-aware.
6. Environment variables configure runtime.
7. Final image contains only app runtime.
```

ASCII:

```text
Builder Stage
  Maven + JDK + source
        |
        v
     app.jar
        |
        v
Runtime Stage
  JRE + app.jar + non-root user
```

Important nuance:

```text
ENTRYPOINT with sh -c allows JAVA_OPTS expansion.
```

If using JSON exec form directly:

```dockerfile
ENTRYPOINT ["java", "$JAVA_OPTS", "-jar", "app.jar"]
```

This will not expand `$JAVA_OPTS` as expected.

---

## 16. .dockerignore

`.dockerignore` prevents unnecessary files from entering build context.

Create:

```text
.dockerignore
```

Recommended:

```dockerignore
.git
.gitignore
target
*.log
logs
.idea
.vscode
.DS_Store
*.iml
README.md
docker-compose*.yml
.env
```

But be careful:

```text
If Dockerfile expects target/*.jar, do not ignore target.
```

For multi-stage build:

```text
Ignoring target is fine because Maven builds inside Docker.
```

For simple Dockerfile that copies target JAR:

```text
Do not ignore target.
```

ASCII:

```text
Project Folder
   |
   v
.dockerignore filters junk
   |
   v
Build Context
   |
   v
Docker Build
```

Why it matters:

```text
1. Faster build context upload.
2. Smaller accidental copy.
3. Avoid leaking secrets.
4. Avoid cache invalidation from irrelevant files.
```

Never copy `.env` into production image.

---

## 17. Running The Image

Build:

```bash
docker build -t mini-url-shortener:047 .
```

Run basic:

```bash
docker run --rm -p 8080:8080 mini-url-shortener:047
```

Run with profile:

```bash
docker run --rm \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=local \
  mini-url-shortener:047
```

Run with database config:

```bash
docker run --rm \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/miniurl \
  -e SPRING_DATASOURCE_USERNAME=miniurl \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  mini-url-shortener:047
```

ASCII port mapping:

```text
Host Machine             Container
localhost:8080  ----->   container:8080
```

Important:

```text
EXPOSE 8080 documents the port.
-p 8080:8080 actually publishes it.
```

---

## 18. Environment Variables

Container images should be reusable.

Do not hardcode production config into image.

Bad:

```dockerfile
ENV SPRING_DATASOURCE_PASSWORD=my-prod-password
```

Good:

```bash
docker run -e SPRING_DATASOURCE_PASSWORD=secret ...
```

Spring Boot maps environment variables:

```text
SPRING_DATASOURCE_URL
    -> spring.datasource.url

SPRING_DATASOURCE_USERNAME
    -> spring.datasource.username

SPRING_DATASOURCE_PASSWORD
    -> spring.datasource.password

SPRING_PROFILES_ACTIVE
    -> spring.profiles.active
```

MiniURLShortener environment examples:

```text
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/miniurl
SPRING_DATASOURCE_USERNAME=miniurl
SPRING_DATASOURCE_PASSWORD=secret
SPRING_DATA_REDIS_HOST=redis
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka
SPRING_CONFIG_IMPORT=optional:configserver:http://config-server:8888
```

ASCII:

```text
Same Image
   |
   +-- local env  --> local container
   |
   +-- staging env -> staging container
   |
   +-- prod env ----> prod container
```

Rule:

```text
Build once. Configure per environment.
```

---

## 19. Dockerfile For Local Development

For production, use optimized image.

For local development, you may prefer faster iteration.

Example dev Dockerfile:

```dockerfile
FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

EXPOSE 8080

CMD ["mvn", "spring-boot:run"]
```

Pros:

```text
1. Easy to run source inside container.
2. Maven available.
3. Useful for local debugging.
```

Cons:

```text
1. Large.
2. Not production image.
3. Slower startup.
4. Contains build tools.
```

Production rule:

```text
Do not use dev Dockerfile for production.
```

ASCII:

```text
Dev Image:
Maven + source + live build

Prod Image:
JRE + built JAR only
```

---

## 20. Docker Compose Connection Mental Model

Inside Docker Compose, services talk by service name.

Example:

```yaml
services:
  app:
    image: mini-url-shortener:047
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/miniurl
      SPRING_DATA_REDIS_HOST: redis
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092

  postgres:
    image: postgres:16

  redis:
    image: redis:7

  kafka:
    image: apache/kafka:latest
```

Important:

```text
From host:
    localhost:5432

From app container:
    postgres:5432
```

ASCII:

```text
Docker Compose Network

+-------------+        postgres:5432       +-------------+
| app         | -------------------------> | postgres    |
+-------------+                            +-------------+

+-------------+        redis:6379          +-------------+
| app         | -------------------------> | redis       |
+-------------+                            +-------------+
```

Common mistake:

```text
Inside container, using localhost for Postgres.
```

Why wrong?

```text
localhost inside app container means app container itself, not Postgres container.
```

Correct:

```text
Use Compose service name: postgres
```

---

## 21. Security Best Practices

Production Dockerfile security:

```text
1. Run as non-root.
2. Use minimal runtime image.
3. Do not copy secrets into image.
4. Do not include source code in runtime image.
5. Pin versions where possible.
6. Scan images for vulnerabilities.
7. Keep base image updated.
8. Avoid unnecessary packages.
9. Use read-only filesystem where possible.
10. Do not expose unnecessary ports.
```

Non-root user:

```dockerfile
RUN groupadd --system appgroup && \
    useradd --system --gid appgroup --home-dir /app appuser

USER appuser
```

Why non-root?

```text
If attacker escapes app process, root inside container increases damage.
```

Secret mistake:

```dockerfile
COPY .env .env
ENV PASSWORD=prod-secret
```

Correct:

```text
Inject secrets at runtime using:
Docker secrets
Kubernetes secrets
Cloud secret manager
Environment variables with care
```

ASCII:

```text
Bad:
secret -> image layer -> registry -> leaked

Good:
secret -> runtime injection -> not baked into image
```

Important:

```text
Even deleted files may remain in previous image layers.
```

So never copy secrets during build.

---

## 22. Image Size Optimization

Large images are bad because:

```text
1. Slower pull.
2. Slower deployment.
3. More vulnerabilities.
4. More storage.
5. Bigger attack surface.
```

Optimization techniques:

```text
1. Multi-stage build.
2. Use JRE instead of full JDK in runtime.
3. Use .dockerignore.
4. Avoid package managers in runtime stage.
5. Use layered JAR.
6. Avoid copying source code into runtime.
7. Remove temporary files in same RUN layer.
```

Bad:

```dockerfile
RUN apt-get update
RUN apt-get install -y curl
RUN rm -rf /var/lib/apt/lists/*
```

Better:

```dockerfile
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*
```

Why?

```text
Each RUN creates layer.
Deleting in later layer does not remove size from earlier layer.
```

ASCII:

```text
Layer 1: install package      +100MB
Layer 2: delete package cache - visible deleted, but old layer still has data
```

Better same layer:

```text
install and clean in one RUN
```

---

## 23. JVM Container Settings

Modern Java is container-aware, but you should still configure memory intentionally.

Useful options:

```text
-XX:MaxRAMPercentage=75.0
-XX:+ExitOnOutOfMemoryError
```

Example:

```dockerfile
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

Why MaxRAMPercentage?

```text
Container may have memory limit.
JVM heap should not consume all memory.
Native memory, metaspace, threads, buffers also need memory.
```

ASCII:

```text
Container Memory Limit
+--------------------------------+
| Heap                           |
| Metaspace                      |
| Thread stacks                  |
| Direct buffers                 |
| OS / native                    |
+--------------------------------+
```

If container memory = 512MB:

```text
MaxRAMPercentage=75%
Heap around 384MB
Remaining for non-heap memory
```

Why ExitOnOutOfMemoryError?

```text
If JVM hits OOM, exit clearly.
Container orchestrator can restart it.
```

Without it:

```text
App may stay half-dead.
```

---

## 24. Health Check Mindset

Dockerfile can include HEALTHCHECK, but in Kubernetes health checks are usually configured in deployment YAML.

Dockerfile healthcheck example:

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1
```

But this requires `wget` in image.

If runtime image does not include wget/curl:

```text
healthcheck fails
or image becomes larger if you install tools
```

For Kubernetes:

```text
Prefer readiness/liveness probes in Kubernetes YAML.
```

Spring Boot Actuator endpoint:

```text
GET /actuator/health
```

ASCII:

```text
Container started
      |
      v
App booting
      |
      v
Readiness false
      |
      v
DB/Redis connected
      |
      v
Readiness true
      |
      v
Traffic allowed
```

Health check mindset:

```text
Startup does not mean ready.
Process alive does not mean healthy.
```

---

## 25. Step-by-Step Dry Runs

### Dry Run 1: Simple Build

Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Flow:

```text
1. Docker pulls base image.
2. Docker creates /app working directory.
3. Docker copies app.jar.
4. Docker stores entrypoint metadata.
5. Image is created.
```

ASCII:

```text
Base Image
   |
   v
/app
   |
   v
app.jar copied
   |
   v
entrypoint set
```

---

### Dry Run 2: Multi-Stage Build

Flow:

```text
1. Builder stage starts from Maven image.
2. pom.xml copied.
3. Dependencies downloaded.
4. src copied.
5. Maven packages JAR.
6. Runtime stage starts from JRE image.
7. Only JAR copied from builder.
8. Runtime image created.
```

ASCII:

```text
Maven Builder
   |
   v
target/app.jar
   |
   v
JRE Runtime
   |
   v
final image
```

Final image does not contain:

```text
Maven
source code
test files
builder cache
```

---

### Dry Run 3: Cache After Source Code Change

First build:

```text
dependency layer created
source layer created
package layer created
```

Change:

```text
src/main/java/UrlService.java
```

Second build:

```text
pom.xml unchanged -> cache
dependency download -> cache
src changed -> rerun
package -> rerun
```

ASCII:

```text
pom.xml layer     CACHED
dependencies      CACHED
src layer         REBUILT
package layer     REBUILT
```

This is why copying `pom.xml` before `src` is powerful.

---

### Dry Run 4: Running Container With Wrong DB Host

Environment:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/miniurl
```

Inside container:

```text
localhost = app container
```

Result:

```text
Connection refused
```

Fix in Docker Compose:

```text
jdbc:postgresql://postgres:5432/miniurl
```

ASCII:

```text
Wrong:
app container -> localhost -> app container itself

Correct:
app container -> postgres -> postgres container
```

---

### Dry Run 5: Port Not Published

Dockerfile:

```dockerfile
EXPOSE 8080
```

Run:

```bash
docker run mini-url-shortener:047
```

Problem:

```text
App runs, but host cannot access localhost:8080.
```

Why:

```text
EXPOSE only documents port.
It does not publish it.
```

Correct:

```bash
docker run -p 8080:8080 mini-url-shortener:047
```

---

## 26. Internal Execution Walkthrough

When container starts:

```text
1. Docker creates container from image.
2. Docker adds writable container layer.
3. Docker sets environment variables.
4. Docker creates network namespace.
5. Docker applies port mapping if provided.
6. Docker runs ENTRYPOINT.
7. JVM starts.
8. Spring Boot starts.
9. App reads environment variables.
10. App connects to dependencies.
11. App listens on port 8080.
```

ASCII:

```text
docker run
   |
   v
container filesystem
   |
   v
environment variables
   |
   v
ENTRYPOINT
   |
   v
java -jar app.jar
   |
   v
Spring Boot
   |
   v
Tomcat port 8080
```

Important:

```text
Container stays alive only while main process stays alive.
```

If Java process exits:

```text
container stops
```

That is why Docker containers usually run one main foreground process.

---

## 27. Common Docker Commands

Build image:

```bash
docker build -t mini-url-shortener:047 .
```

List images:

```bash
docker images
```

Run container:

```bash
docker run --rm -p 8080:8080 mini-url-shortener:047
```

Run in background:

```bash
docker run -d --name miniurl -p 8080:8080 mini-url-shortener:047
```

See logs:

```bash
docker logs -f miniurl
```

Exec into container:

```bash
docker exec -it miniurl sh
```

Stop container:

```bash
docker stop miniurl
```

Inspect image layers:

```bash
docker history mini-url-shortener:047
```

Check running containers:

```bash
docker ps
```

Check all containers:

```bash
docker ps -a
```

Remove image:

```bash
docker rmi mini-url-shortener:047
```

---

## 28. Debugging Mindset

When Dockerized app fails, ask:

```text
Did image build successfully?
Did container start?
Did Java process exit?
Are environment variables correct?
Is port published?
Is app listening on expected port?
Can app resolve dependency hostnames?
Is DB hostname localhost by mistake?
Is profile correct?
Is JAR copied correctly?
Is ENTRYPOINT correct?
Is non-root user missing permissions?
Is memory limit too low?
```

Debug map:

```text
Container exits immediately:
    check docker logs
    check ENTRYPOINT
    check app startup error

Cannot access app from host:
    check -p port mapping
    check app server.port
    check firewall

Cannot connect to Postgres:
    check datasource URL
    check Compose service name
    check network
    check credentials

Permission denied:
    check USER
    check file ownership
    check chown

OutOfMemoryError:
    check container memory limit
    check JAVA_OPTS
```

Useful commands:

```bash
docker logs miniurl
docker inspect miniurl
docker exec -it miniurl sh
docker history mini-url-shortener:047
```

Golden question:

```text
Is this a Docker packaging problem, runtime config problem, or application problem?
```

---

## 29. Production Failure Stories

### Failure Story 1: App Works Locally But Fails In Container

Local config:

```text
localhost:5432
```

Container config:

```text
localhost:5432
```

Result:

```text
App container tries to connect to itself, not Postgres.
```

Fix:

```text
Use postgres service name in Docker Compose.
Use environment-specific config.
```

Lesson:

```text
localhost changes meaning inside containers.
```

---

### Failure Story 2: Secret Baked Into Image

Dockerfile:

```dockerfile
ENV SPRING_DATASOURCE_PASSWORD=prod-password
```

Image pushed to registry.

Result:

```text
Anyone with image access can inspect secret.
Secret may remain in image history.
```

Fix:

```text
Inject secrets at runtime.
Rotate leaked secret.
```

Lesson:

```text
Images are not secret stores.
```

---

### Failure Story 3: Runtime Image Contains Maven And Source

Bad multi-purpose image includes:

```text
Maven
source code
test files
local configs
```

Result:

```text
Huge image.
Slow deployment.
More vulnerabilities.
Possible source exposure.
```

Fix:

```text
Use multi-stage build.
Only copy final JAR into runtime image.
```

Lesson:

```text
Build environment and runtime environment should be separate.
```

---

### Failure Story 4: Container Runs As Root

App vulnerability allows file write.

Because container runs as root:

```text
Attacker has more privilege inside container.
```

Fix:

```text
Create non-root user.
Use USER appuser.
Use read-only filesystem where possible.
```

Lesson:

```text
Least privilege applies inside containers too.
```

---

### Failure Story 5: No Memory Tuning

Container memory limit:

```text
512MB
```

JVM not tuned well.

Result:

```text
OOMKilled or unstable GC.
```

Fix:

```text
Use container-aware JVM options.
Set memory requests/limits in Kubernetes.
Observe heap/native memory.
```

Lesson:

```text
Container memory is a contract. JVM must respect it.
```

---

## 30. Common Mistakes

### Mistake 1: Using Maven image as runtime

Wrong:

```dockerfile
FROM maven:3.9-eclipse-temurin-21
```

Correct:

```text
Use Maven only in builder stage.
Use JRE in runtime stage.
```

### Mistake 2: Copying everything

Wrong:

```dockerfile
COPY . .
```

Correct:

```text
Use .dockerignore.
Copy only needed files.
```

### Mistake 3: Bad cache order

Wrong:

```dockerfile
COPY . .
RUN mvn package
```

Correct:

```dockerfile
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package
```

### Mistake 4: Hardcoding secrets

Wrong:

```dockerfile
ENV PASSWORD=secret
```

Correct:

```text
Inject secrets at runtime.
```

### Mistake 5: Running as root

Wrong:

```text
Default root user.
```

Correct:

```dockerfile
USER appuser
```

### Mistake 6: Thinking EXPOSE publishes port

Wrong:

```text
EXPOSE 8080 means host can access it.
```

Correct:

```bash
docker run -p 8080:8080 image
```

### Mistake 7: Using localhost for other containers

Wrong:

```text
jdbc:postgresql://localhost:5432/db
```

Correct:

```text
jdbc:postgresql://postgres:5432/db
```

### Mistake 8: No JVM memory config

Wrong:

```text
Let JVM guess everything.
```

Correct:

```text
Set memory percentage and container limits intentionally.
```

---

## 31. Interview-Ready Explanation

If interviewer asks:

```text
How would you Dockerize a Spring Boot service?
```

Strong answer:

```text
I would use a multi-stage Dockerfile. The first stage uses a Maven + JDK image to
build the Spring Boot JAR. I would copy pom.xml first and download dependencies so
Docker can cache Maven dependencies, then copy src and run mvn package. The second
stage uses a smaller JRE runtime image and copies only the built JAR from the builder
stage. I would run the app as a non-root user, expose port 8080, configure JVM memory
for containers using options like MaxRAMPercentage, and pass environment-specific
settings such as database URL, Redis host, Kafka bootstrap servers, and active profile
through environment variables instead of baking them into the image. I would also use
.dockerignore to avoid copying unnecessary files or secrets. In Docker Compose,
services should communicate by service name, not localhost.
```

Why this is strong:

```text
1. Explains multi-stage build.
2. Explains cache optimization.
3. Mentions small runtime image.
4. Mentions non-root user.
5. Mentions environment variables.
6. Mentions .dockerignore.
7. Mentions JVM container memory.
8. Mentions Compose networking.
```

Senior one-liner:

```text
A production Dockerfile should create a small, secure, repeatable runtime image with no build tools, no secrets, and clear runtime configuration.
```

---

## 32. Senior Engineer Checklist

Before production:

```text
[ ] Multi-stage build used
[ ] Runtime image does not contain Maven
[ ] Runtime image does not contain source code
[ ] .dockerignore exists
[ ] Secrets are not copied into image
[ ] App runs as non-root user
[ ] Port is documented with EXPOSE
[ ] Runtime config injected via environment variables
[ ] JVM memory settings are container-aware
[ ] Image builds from clean checkout
[ ] Docker cache optimized
[ ] Base image version is intentional
[ ] Image vulnerability scanning planned
[ ] Logs go to stdout/stderr
[ ] App handles SIGTERM gracefully
[ ] Docker Compose uses service names
[ ] Health endpoint available
[ ] No local-only assumptions remain
```

If these are checked, your Dockerfile is production-shaped.

---

## 33. One-Page Cheat Sheet

```text
Core mental model:
Dockerfile = recipe
Image = packaged app
Container = running image instance

Important instructions:
FROM        base image
WORKDIR     working directory
COPY        copy files into image
RUN         execute during build
ENV         default env vars
EXPOSE      document port
ENTRYPOINT  main startup command
CMD         default arguments

Good Spring Boot production shape:
1. Multi-stage build
2. Maven builder stage
3. JRE runtime stage
4. Copy only JAR
5. Run as non-root
6. Use .dockerignore
7. Use env vars
8. Configure JVM memory

Cache optimization:
COPY pom.xml first
RUN dependency download
COPY src later
RUN package

Docker Compose:
app container uses postgres:5432, not localhost:5432

Security:
do not bake secrets
do not run as root
keep image small
scan images
update base images

Commands:
docker build -t app .
docker run -p 8080:8080 app
docker logs -f container
docker exec -it container sh
docker history image
```

---

## 34. One Picture To Remember

```text
                     DOCKERFILE MENTAL MODEL

                         "Recipe to Image"

Source Code
    |
    v
+-------------------------+
| Dockerfile              |
| FROM runtime/build base |
| COPY files              |
| RUN build commands      |
| ENTRYPOINT start app    |
+-------------------------+
    |
    v
docker build
    |
    v
+-------------------------+
| Docker Image            |
| read-only layers        |
| app + runtime           |
+-------------------------+
    |
    v
docker run
    |
    v
+-------------------------+
| Container               |
| running JVM process     |
| env vars                |
| network                 |
| writable layer          |
+-------------------------+


PRODUCTION SPRING BOOT IMAGE:

Builder Stage:
Maven + JDK + source -> app.jar

Runtime Stage:
JRE + app.jar + non-root user -> container


FINAL MEMORY:

Build tools stay in builder.
Only runtime goes to production.
Config comes from environment.
Secrets never go into image.
Container runs one main process.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Dockerfile is a repeatable recipe for creating an application image.
2. Multi-stage builds keep Maven/source code out of the production runtime image.
3. Docker layers and cache depend heavily on instruction order.
4. Environment-specific configuration should be injected at runtime, not baked into the image.
5. A production Dockerfile should be small, secure, non-root, cache-friendly, and easy to debug.
```

Next possible chapters:

```text
048_Docker_Compose.md
049_Observability_Metrics_Tracing.md
050_Kubernetes_Deployment.md
051_CI_CD_Docker_Image_Build.md
```
