# 010_MultiStage_Builds

> MiniDocker Deep Production Mode  
> Goal: understand multi-stage builds deeply, not memorize Dockerfile syntax.

---

## 1. Understanding First: Why Multi-Stage Builds Exist

A Docker image is not just your application. It is a packaged filesystem plus metadata. If you build a Java service using Maven inside the same image that you later run in production, then the final image may contain the JDK, Maven, source code, dependency cache, test files, build plugins, temporary files, and the final JAR. Production does not need most of that.

Multi-stage builds exist to separate **building** from **running**. The builder stage is allowed to be heavy because it is a temporary workshop. The runtime stage must be small because it is what Kubernetes pulls, starts, scans, and runs thousands of times.

```text
Single-stage thinking

Source + Maven + JDK + Tests + Target JAR + Runtime
          |
          v
   Final production image is heavy
```

```text
Multi-stage thinking

Builder image                    Runtime image
+-------------------+            +-------------------+
| JDK               |            | JRE               |
| Maven             |            | app.jar           |
| Source code       | --copy-->  | config/env only   |
| Test tools        |            +-------------------+
| target/app.jar    |
+-------------------+

Only the final runtime stage becomes the production image.
```

The idea to remember is simple: **build with tools, ship only the result**.

---

## 2. Not-To-Memorize Model

Do not memorize multi-stage builds as `FROM ... AS build` plus `COPY --from=build`. That is only syntax. Instead, memorize the production rule:

```text
Anything needed only before startup must not be in the runtime image.
```

Use this question for every file in your image:

```text
Does this file help the container start and serve traffic?

YES  -> runtime image
NO   -> builder stage only
```

Examples:

```text
pom.xml                  builder mostly
src/main/java            builder only
Maven                    builder only
JDK compiler             builder only
target/order.jar         runtime
JRE                      runtime
application.yml          usually runtime or external config
unit test reports        builder only
.git directory           never runtime
```

Mental compression:

```text
Factory        -> Builder stage
Product        -> JAR / binary
Delivery box   -> Runtime image
Warehouse      -> Registry
Truck          -> Kubernetes image pull
Customer       -> Running pod
```

You do not send the factory to the customer. You send the product.

---

## 3. Large Image Problem

Large images hurt production in many ways. They are slower to push in CI, slower to pull in Kubernetes, more expensive to store in registries, and usually contain more vulnerable packages. A large image also makes debugging harder because it is not obvious which files are actually needed.

```text
Large image blast radius

+-----------------------------+
| Maven cache                 |
| JDK compiler                |
| Source code                 |
| Test reports                |
| Shell tools                 |
| OS packages                 |
| app.jar                     |
+-----------------------------+
          |
          v
  More bytes, more CVEs, slower rollout
```

In Kubernetes, startup is not only application startup. It includes scheduling, image pull, filesystem extraction, container creation, JVM startup, Spring context initialization, readiness checks, and traffic routing.

```text
Pod startup path

Scheduler -> Node chosen -> Pull image -> Extract layers -> Create container
          -> Start JVM -> Spring Boot -> Readiness probe -> Service traffic
```

If the image is huge, the `Pull image` and `Extract layers` steps become expensive. This is especially painful during node autoscaling, rolling deployment, or incident recovery when many pods must start quickly.

---

## 4. Traditional Dockerfile Problem

A traditional single-stage Dockerfile often looks clean, but it mixes build-time and runtime responsibilities.

```dockerfile
FROM maven:3.9-eclipse-temurin-21
WORKDIR /app
COPY . .
RUN mvn clean package
ENTRYPOINT ["java", "-jar", "target/order-service.jar"]
```

What actually happens:

```text
Layer 1: Maven + JDK base image
Layer 2: copied source code
Layer 3: downloaded dependencies
Layer 4: compiled classes and target JAR
Layer 5: runtime command
```

Final image contains:

```text
Maven        not needed at runtime
JDK compiler not needed at runtime
src/         not needed at runtime
target/*.jar needed
```

This is the classic mistake: the container runs only one JAR, but the image carries the whole workshop.

---

## 5. Multi-Stage Architecture

A multi-stage Dockerfile has multiple `FROM` instructions. Each `FROM` starts a separate stage. Only the last stage is exported as the final image unless you explicitly build a target stage.

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/order-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Architecture:

```text
+------------------------------+
| Stage 1: builder             |
| base: maven + JDK            |
|                              |
| /app/pom.xml                 |
| /app/src                     |
| /root/.m2 dependencies       |
| /app/target/order.jar        |
+--------------+---------------+
               |
               | COPY --from=builder /app/target/order.jar
               v
+------------------------------+
| Stage 2: runtime             |
| base: JRE                    |
|                              |
| /app/app.jar                 |
+------------------------------+
```

Only stage 2 is shipped.

---

## 6. Stage Dependency Graph

Multi-stage builds are not only a sequence. They form a dependency graph. A runtime stage can copy from one or more previous stages. BuildKit can skip stages that are not needed for the final target.

```text
                 +----------------+
                 | deps stage     |
                 | mvn go-offline |
                 +--------+-------+
                          |
                          v
+-------------+    +------+-------+    +----------------+
| test stage  | <- | build stage  | -> | runtime stage  |
| run tests   |    | package jar  |    | copy jar only  |
+-------------+    +--------------+    +----------------+
```

A production Dockerfile may have these stages:

```text
deps     -> download dependencies
build    -> compile application
test     -> run tests
runtime  -> minimal final image
debug    -> runtime with shell/tools, used only locally
```

The runtime stage should not depend on the debug stage. Debug tools are useful, but they should not leak into production images.

---

## 7. COPY --from Deep Dive

`COPY --from` copies files from one build stage into another. It does not copy layers directly. It copies selected filesystem content from the source stage snapshot.

```dockerfile
COPY --from=builder /app/target/order-service.jar /app/app.jar
```

Meaning:

```text
Source stage filesystem:
/app/target/order-service.jar

Destination stage filesystem:
/app/app.jar
```

Important mental model:

```text
COPY --from is a controlled artifact bridge.
It is not inheritance.
It is not sharing the whole builder image.
It copies selected files only.
```

Bad transfer:

```dockerfile
COPY --from=builder /app /app
```

This may accidentally copy source code, reports, temporary files, and more.

Good transfer:

```dockerfile
COPY --from=builder /app/target/order-service.jar /app/app.jar
```

Production rule:

```text
Copy exact artifacts, not directories, unless you fully trust the directory contents.
```

---

## 8. Docker Build Lifecycle Review

When Docker builds an image, each instruction creates or reuses a layer depending on cache. The build context is sent to the builder, instructions execute in order, and final metadata is written.

```text
Build command
   |
   v
Read Dockerfile
   |
   v
Send build context
   |
   v
Execute stage instructions
   |
   v
Compute layer digests
   |
   v
Export final stage image
```

For multi-stage builds:

```text
Stage 1 executes -> creates builder filesystem snapshot
Stage 2 executes -> copies selected files from stage 1
Final image     -> stage 2 only
```

This is why a huge builder stage does not automatically make the final image huge. The final image includes only its own base layers and files explicitly copied into it.

---

## 9. Build Cache Behavior

Cache is one of the biggest real-world reasons to structure Dockerfiles carefully. If you copy the full source before downloading dependencies, every source code change may invalidate the dependency download layer.

Bad cache pattern:

```dockerfile
COPY . .
RUN mvn clean package
```

Problem:

```text
Change one Java file
     |
     v
COPY . . changes
     |
     v
mvn package layer cache miss
     |
     v
Dependencies may be rechecked/redownloaded
```

Better cache pattern:

```dockerfile
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests
```

Better flow:

```text
pom.xml unchanged -> dependency layer reused
src changed       -> compile layer reruns
```

ASCII cache decision:

```text
Instruction + input files + build args
          |
          v
   Hash/digest check
      /          \
 cache hit     cache miss
    |              |
 reuse layer     execute instruction
```

---

## 10. Spring Boot Multi-Stage Example

A production-style Spring Boot Dockerfile:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS deps
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline

FROM deps AS builder
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
RUN addgroup --system spring && adduser --system spring --ingroup spring
COPY --from=builder /app/target/order-service.jar app.jar
USER spring:spring
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
```

Spring Boot startup:

```text
Container process
      |
      v
java -jar app.jar
      |
      v
SpringApplication.run()
      |
      v
Create ApplicationContext
      |
      v
Load beans, controllers, repositories
      |
      v
Start embedded Tomcat/Netty
      |
      v
Readiness endpoint becomes healthy
```

Key production detail: `MaxRAMPercentage` helps JVM size heap according to container memory limits instead of assuming host memory.

---

## 11. Gradle Example

```dockerfile
FROM gradle:8.8-jdk21 AS builder
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true
COPY src ./src
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Flow:

```text
Gradle wrapper/settings copied first
        |
Dependency resolution cached
        |
Source copied later
        |
bootJar generated
        |
JAR copied to runtime
```

The same principle applies: keep slow-changing dependency descriptors separate from fast-changing source files.

---

## 12. Layered Spring Boot JAR Optimization

Spring Boot can split a fat JAR into logical layers. This improves Docker cache reuse because dependencies change less often than application classes.

```text
Spring Boot layered JAR

BOOT-INF/lib/            dependencies
BOOT-INF/classes/        application code
BOOT-INF/lib/snapshot/   snapshot dependencies
META-INF/                metadata
```

Typical optimization idea:

```text
Dependencies layer       changes rarely
Spring boot loader       changes rarely
Snapshot dependencies    changes sometimes
Application classes      changes often
```

When application code changes, Docker can reuse dependency layers. This is powerful in CI/CD pipelines where many services share similar dependencies.

```text
Change controller class
      |
      v
Rebuild app classes layer only
      |
      v
Reuse dependency layers
```

For many teams, a normal JAR-copy multi-stage build is already good. Layered JARs become valuable when CI speed and registry efficiency matter at scale.

---

## 13. Runtime Image Choices: JRE, Alpine, Debian, Distroless

Choosing the runtime image is a production decision.

```text
JDK image
  large, useful for building, not ideal for runtime

JRE image
  smaller, good default for Java runtime

Alpine image
  small, musl libc, sometimes compatibility surprises

Debian/Ubuntu slim
  larger than Alpine, often more compatible

Distroless
  very small attack surface, harder to debug inside container
```

Decision table:

| Image type | Best use | Risk |
|---|---|---|
| Maven/JDK | builder stage | too large for runtime |
| JRE slim | common Java runtime | moderate size |
| Alpine | tiny services | native library surprises |
| Distroless | hardened production | no shell for debugging |

Mental model:

```text
More tools inside image  -> easier debugging, bigger attack surface
Fewer tools inside image -> safer runtime, harder debugging
```

A strong production pattern is to use a minimal runtime image and rely on logs, metrics, ephemeral debug containers, or Kubernetes debugging tools instead of shipping shell tools in every production container.

---

## 14. Kubernetes Connection

Kubernetes does not care how the image was built. It cares about pulling and running the final image. Multi-stage builds help Kubernetes because the final image is smaller and cleaner.

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
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
          resources:
            requests:
              cpu: "500m"
              memory: "512Mi"
            limits:
              cpu: "1"
              memory: "1Gi"
```

Pod startup with image size impact:

```text
Large image
Node pull: slow
Layer extract: slow
Rollout: slow
Incident recovery: slow
```

```text
Small image
Node pull: faster
Layer extract: faster
Rollout: faster
Incident recovery: faster
```

---

## 15. CI/CD Production Flow

In real companies, the Docker build happens inside CI.

```text
Developer push
      |
      v
CI checkout
      |
      v
Run unit tests
      |
      v
Build multi-stage image
      |
      v
Scan image
      |
      v
Push to registry
      |
      v
Deploy to Kubernetes
```

A pipeline may use BuildKit cache, remote registry cache, SBOM generation, vulnerability scanning, and immutable digest deployment.

```text
Tag deployment
image: order-service:latest
Risk: tag can move
```

```text
Digest deployment
image: order-service@sha256:abc123...
Benefit: exact immutable image
```

Production mindset: tags are human-friendly labels; digests are truth.

---

## 16. Registry Internals

A registry stores image manifests and layers. Layers are content-addressed by digest. If two images share the same base layer, the registry can store it once.

```text
Image manifest
+-----------------------------+
| config digest               |
| layer digest 1              |
| layer digest 2              |
| layer digest 3              |
+-----------------------------+
```

Push flow:

```text
Docker client
   |
   | checks which layers registry already has
   v
Registry
   |
   | upload missing layers only
   v
Store manifest + layer blobs
```

Multi-stage helps because only runtime layers are pushed for the final image. Builder-only filesystem layers are not part of the final manifest.

```text
Builder layers      not referenced by final manifest
Runtime layers      referenced by final manifest
```

This reduces registry storage and speeds up pulls.

---

## 17. Debugging Mindset

When a multi-stage build fails, ask: did it fail during build, copy, runtime startup, or Kubernetes pull?

```text
Failure location map

Docker build
  |
  +-- dependency download failed
  +-- compile failed
  +-- tests failed
  +-- COPY --from path wrong

Docker run
  |
  +-- JAR missing
  +-- wrong ENTRYPOINT
  +-- permission denied
  +-- port/config missing

Kubernetes
  |
  +-- ImagePullBackOff
  +-- CrashLoopBackOff
  +-- readiness probe failed
```

Commands:

```bash
docker build --progress=plain -t order-service:local .
docker history order-service:local
docker image inspect order-service:local
docker run --rm -p 8080:8080 order-service:local
```

Kubernetes:

```bash
kubectl describe pod <pod>
kubectl logs <pod>
kubectl get events --sort-by=.lastTimestamp
kubectl exec -it <pod> -- sh
```

If using distroless, `kubectl exec -- sh` will not work because there is no shell. Use logs, probes, or ephemeral debug containers.

---

## 18. Common Failure: COPY Path Wrong

Problem:

```dockerfile
COPY --from=builder /app/target/app.jar app.jar
```

But Maven produced:

```text
/app/target/order-service-0.0.1-SNAPSHOT.jar
```

Build error:

```text
failed to compute cache key: failed to calculate checksum: file not found
```

Debug:

```dockerfile
FROM builder AS inspect
RUN ls -lah /app/target
```

Better Maven configuration:

```xml
<build>
  <finalName>order-service</finalName>
</build>
```

Then Dockerfile becomes predictable:

```dockerfile
COPY --from=builder /app/target/order-service.jar app.jar
```

Production rule:

```text
Make artifact names deterministic.
Do not depend on wildcard magic unless you control the output directory.
```

---

## 19. Common Failure: Works Locally, Fails In Container

Typical causes:

```text
Local machine has JDK/tools/config
Container runtime image has only JRE/app.jar
```

Examples:

```text
Missing timezone data
Missing CA certificates
Missing native library
Missing environment variable
Wrong file permission
Different working directory
```

Debug flow:

```text
Compare local assumptions with container filesystem
      |
      v
Check env variables
      |
      v
Check mounted config/secrets
      |
      v
Check logs and exit code
      |
      v
Rebuild with plain progress
```

Useful command:

```bash
docker run --rm order-service:local env
```

For Spring Boot:

```bash
docker run --rm -e SPRING_PROFILES_ACTIVE=local order-service:local
```

---

## 20. Security Mindset

Multi-stage builds improve security because build tools are removed from the runtime image. But multi-stage alone does not guarantee security.

Security checklist:

```text
Use minimal runtime image
Run as non-root
Do not copy source code
Do not copy secrets
Pin important base image versions
Scan final image
Generate SBOM if required
Use read-only filesystem when possible
```

Bad example:

```dockerfile
COPY . .
```

This may copy:

```text
.env
.git
private keys
test data
local config
```

Use `.dockerignore`:

```dockerignore
.git
.env
*.pem
target
build
.idea
.vscode
node_modules
```

Mental model:

```text
Dockerfile controls image instructions.
.dockerignore controls what enters the build context.
Both matter.
```

---

## 21. Production Story

A company had 80 Spring Boot services. Each service used a single-stage Maven image. Average image size was around 850 MB. Rolling deployments during peak hours were slow. When a Kubernetes node was replaced, pods took longer to recover because every node had to pull large images.

They moved to multi-stage builds:

```text
Before
80 services x ~850 MB = painful registry + slow node pulls

After
80 services x ~180 MB = faster rollout + smaller attack surface
```

The biggest improvement was not only size. The team also standardized artifact names, added `.dockerignore`, used non-root users, and improved Docker cache layout. CI became more predictable because dependency layers were reused more often.

Production lesson:

```text
Multi-stage builds are not only optimization.
They create a clean boundary between engineering workflow and runtime operations.
```

---

## 22. BuildKit Optimizations

BuildKit improves Docker builds with better caching, parallelism, secret mounts, SSH mounts, and more efficient build execution.

Example cache mount for Maven:

```dockerfile
# syntax=docker/dockerfile:1.7
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B clean package -DskipTests
```

Why useful:

```text
Without cache mount
Dependencies live inside build layers or are downloaded again

With cache mount
Maven repository cache persists across builds without being copied to final image
```

Do not confuse BuildKit cache with runtime image content. Cache mount helps the build machine; it does not automatically enter the final image.

---

## 23. Dry Run: Build

```text
docker build -t order-service:1.0 .
```

Dry run:

```text
1. Docker reads Dockerfile
2. Starts deps stage from Maven image
3. Copies pom.xml
4. Downloads dependencies
5. Starts builder stage
6. Copies source code
7. Compiles Java files
8. Creates target/order-service.jar
9. Starts runtime stage from JRE image
10. Copies JAR from builder snapshot
11. Sets user, port, entrypoint
12. Exports runtime image only
```

Picture:

```text
[deps] -> [builder] -> [runtime]
                    copy jar only
```

---

## 24. Dry Run: Kubernetes Rollout

```text
kubectl apply -f deployment.yaml
```

Dry run:

```text
1. Deployment controller sees new ReplicaSet
2. Scheduler assigns pod to node
3. Kubelet asks container runtime to pull image
4. Runtime checks local layer cache
5. Missing layers pulled from registry
6. Layers extracted
7. Container process starts
8. Spring Boot starts
9. Readiness probe passes
10. Service sends traffic
```

Where multi-stage helps:

```text
smaller image -> fewer bytes pulled -> faster node cold start
cleaner image -> fewer CVEs -> easier platform approval
```

---

## 25. Interview Answers

**What is a multi-stage build?**  
A Docker build pattern where one stage builds the artifact and another stage runs it. Only the final stage is shipped as the production image.

**Why not use Maven image in production?**  
Maven and the JDK compiler are build-time tools. Runtime only needs the application artifact and a compatible JVM/JRE.

**What does `COPY --from` do?**  
It copies selected files from a previous stage filesystem into the current stage. It does not inherit the whole previous image.

**How does it help Kubernetes?**  
It reduces image size, pull time, extraction time, and vulnerability surface. This improves rollout speed and node recovery.

**What is the biggest mistake?**  
Copying too much from the builder stage or using `COPY . .` without `.dockerignore`.

**How do you optimize cache?**  
Copy dependency descriptors first, download dependencies, then copy source code. This keeps dependency layers reusable when only source files change.

---

## 26. Senior-Level Discussion Points

A senior engineer should connect multi-stage builds to platform operations:

```text
Developer productivity
  -> faster CI builds

Platform reliability
  -> faster rollout and recovery

Security
  -> fewer packages and smaller attack surface

Cost
  -> less registry storage and bandwidth

Debuggability
  -> clearer boundary between build and runtime
```

Strong answer:

> Multi-stage builds are not just about smaller images. They enforce separation of concerns. The builder stage contains compilers and dependency managers. The runtime stage contains only the executable artifact and runtime dependencies. This improves security, cache efficiency, deployment velocity, and Kubernetes cold-start behavior.

---

## 27. Cheat Sheet

```text
Builder stage
  Purpose: compile/package/test
  Contains: source, Maven/Gradle, JDK, build cache
  Shipped: no

Runtime stage
  Purpose: run app
  Contains: JRE, app.jar, runtime config only
  Shipped: yes

COPY --from
  Purpose: controlled artifact transfer
  Best practice: copy exact output file
```

Commands:

```bash
docker build -t order-service:local .
docker build --target builder -t order-service-builder .
docker history order-service:local
docker run --rm -p 8080:8080 order-service:local
```

Checklist:

```text
[ ] .dockerignore exists
[ ] dependencies copied before source
[ ] runtime image is not Maven/JDK builder image
[ ] non-root user
[ ] deterministic JAR name
[ ] exact COPY --from path
[ ] image scanned
[ ] Kubernetes probes configured
```

---

## 28. One Picture To Remember

```text
                    DOCKER MULTI-STAGE BUILD

        BUILD WORLD                              RUNTIME WORLD
+----------------------------+           +----------------------------+
| Maven / Gradle             |           | JRE                        |
| JDK compiler               |           | app.jar                    |
| Source code                |           | application config          |
| Unit tests                 |           | non-root user              |
| Dependency cache           |           | exposed port               |
| target/order-service.jar   |           | entrypoint                 |
+-------------+--------------+           +-------------+--------------+
              |                                        ^
              | COPY exact artifact only                |
              +----------------------------------------+

Rule:
Build with everything you need.
Run with only what you must have.
```

---

## 29. Final Takeaways

1. Multi-stage builds separate build-time tools from runtime artifacts.
2. The final image is only the final stage, not all stages combined.
3. `COPY --from` is an artifact bridge, not full image inheritance.
4. Cache-friendly Dockerfiles copy dependency descriptors before source code.
5. Smaller runtime images improve Kubernetes rollout and recovery.
6. Minimal images reduce attack surface but may require better external debugging tools.
7. `.dockerignore` is part of production image hygiene.
8. Senior engineers explain multi-stage builds through deployment speed, security, cache behavior, and operational reliability.
