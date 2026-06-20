# 011_Image_Optimization_Strategies

> MiniDocker Deep Production Mode  
> Goal: understand Docker image optimization deeply, not memorize random tricks.

---

## 1. Understanding First: Why Image Optimization Exists

A Docker image is not only a file you push to a registry. It is the packaged filesystem your production platform must pull, unpack, scan, cache, and run. When the image is large, every step becomes heavier: CI builds take longer, registry pushes are slower, Kubernetes node cold starts are slower, vulnerability scans report more noise, and rollbacks are less predictable.

Do not think of image optimization as “make MB number small.” That is only the visible result. The real goal is to ship a runtime package that contains **only what the service needs to start and serve traffic safely**.

```text
Bad production image
+------------------------------------------------+
| OS packages                                    |
| build tools                                    |
| Maven / Gradle                                 |
| source code                                    |
| tests                                          |
| dependency cache                               |
| temporary files                                |
| application JAR                                |
+------------------------------------------------+
             |
             v
slow pull + more CVEs + unclear runtime boundary
```

```text
Good production image
+------------------------------------------------+
| minimal OS / runtime                           |
| application JAR                                |
| certificates / timezone if required            |
| non-root user                                  |
| entrypoint                                     |
+------------------------------------------------+
             |
             v
fast pull + cleaner scan + predictable runtime
```

The one sentence model:

```text
Optimization means removing everything that helped you build but does not help you run.
```

---

## 2. Not-To-Memorize Model

Do not memorize image optimization as a list: Alpine, distroless, multi-stage, cache, `.dockerignore`. Those are tools. The better model is a decision question.

```text
For every byte entering the image, ask:

Does this byte help the container run in production?

YES  -> allow it
NO   -> keep it in build stage, CI workspace, or local machine
```

Examples:

```text
pom.xml                    build input, not runtime
src/main/java              build input, not runtime
Maven                      build tool, not runtime
JDK compiler               build tool, not runtime
JRE/JVM                    runtime dependency
app.jar                    runtime artifact
.git                       never runtime
.env                       never runtime
unit test report           CI artifact, not runtime
curl / bash / vim          debug tools, usually not production
CA certificates            runtime if HTTPS calls are made
Timezone data              runtime if app depends on zone names
```

Mental compression:

```text
Docker image = delivery box
Docker layer = sealed packet inside box
Registry     = warehouse
Kubernetes   = truck + unpacker + runner
Container    = opened box running one process
```

Do not ship the workshop. Ship the product.

---

## 3. Mental Map Of Image Optimization

Use this map before writing any Dockerfile.

```text
                         IMAGE OPTIMIZATION
                                  |
        +-------------------------+-------------------------+
        |                         |                         |
   Build-time cleanup        Runtime cleanup             Delivery speed
        |                         |                         |
 +------+-------+          +------+-------+          +------+-------+
 | multi-stage  |          | small base   |          | cache reuse  |
 | .dockerignore|          | non-root     |          | layer reuse  |
 | cache mounts |          | no secrets   |          | node cache   |
 | exact COPY   |          | no buildtool |          | digest deploy|
 +--------------+          +--------------+          +--------------+
```

A senior engineer sees the same Dockerfile from four angles:

```text
Developer angle  -> Is CI fast?
Security angle   -> Are unnecessary packages removed?
Platform angle   -> Can Kubernetes pull and start quickly?
Debug angle      -> Can failures be diagnosed without bloating prod image?
```

This is why image optimization belongs to production engineering, not only Docker syntax.

---

## 4. Large Image Problem

Large images usually happen because the Dockerfile mixes build-time and runtime worlds.

```dockerfile
FROM maven:3.9-eclipse-temurin-21
WORKDIR /app
COPY . .
RUN mvn clean package
ENTRYPOINT ["java", "-jar", "target/payment-service.jar"]
```

This looks simple, but the final image carries Maven, JDK compiler, source code, test files, dependency cache, and the JAR. Production only needs the runtime and the JAR.

```text
Single-stage final image
+------------------------------------------------+
| maven base image                               |
| JDK compiler                                   |
| /root/.m2 dependency cache                     |
| /app/src                                       |
| /app/target/payment-service.jar                |
| test resources                                 |
| Docker build leftovers                         |
+------------------------------------------------+
```

Large image blast radius:

```text
More bytes
   |
   +--> slower CI push
   +--> slower registry replication
   +--> slower node pull
   +--> slower autoscaling
   +--> more packages to scan
   +--> more CVE noise
   +--> harder incident recovery
```

In Kubernetes, this becomes painful during scale-out or node replacement:

```text
New node joins cluster
        |
        v
Pods scheduled
        |
        v
Images pulled from registry
        |
        v
Huge image delays readiness
        |
        v
Traffic capacity arrives late
```

---

## 5. The Optimization Pipeline

Image optimization is a pipeline. You do not fix everything with one instruction.

```text
Source code
   |
   v
Build context filtering      (.dockerignore)
   |
   v
Dockerfile ordering          (cache-friendly layers)
   |
   v
Builder stage                (heavy tools allowed)
   |
   v
Artifact selection           (exact COPY --from)
   |
   v
Runtime stage                (small base image)
   |
   v
Security hardening           (non-root, no secrets)
   |
   v
Registry push                (deduplicated layers)
   |
   v
Kubernetes rollout           (fast pull/start)
```

If optimization starts only after the image is built, it is already late. Good optimization starts before Docker even sees the files.

---

## 6. Build Context And .dockerignore

The build context is the directory sent to the Docker builder. If your context includes `.git`, target files, local secrets, IDE folders, test dumps, and logs, Docker has to process them. Worse, a careless `COPY . .` may include them in the image.

```text
Local project directory
+------------------------------------------------+
| src/                                           |
| pom.xml                                       |
| target/                                       |
| .git/                                         |
| .env                                          |
| logs/                                         |
| README.md                                     |
+------------------------------------------------+
             |
             v
       docker build context
```

Good `.dockerignore`:

```dockerignore
.git
.env
*.pem
*.key
target
build
logs
.idea
.vscode
node_modules
.DS_Store
```

Mental model:

```text
Dockerfile    = what instructions run
.dockerignore = what files are allowed to enter the build room
```

Production mistake:

```dockerfile
COPY . .
```

If `.dockerignore` is weak, this copies too much. The result can be bigger images, leaked secrets, and unnecessary cache invalidation.

---

## 7. Layer Model Refresher

A Docker image is a stack of read-only layers plus metadata. Each Dockerfile instruction can create a layer.

```text
Image filesystem view
+-----------------------------+  top layer: app.jar
+-----------------------------+  dependency layer
+-----------------------------+  runtime packages
+-----------------------------+  base OS / JVM
```

Layer properties:

```text
Layer content -> digest -> immutable identity
Same content  -> same digest -> can be reused
Changed input -> new digest -> cache miss
```

Optimization uses this fact. Put stable things in earlier layers and frequently changing things in later layers.

```text
Stable first:
base image
JVM runtime
dependencies
application classes
```

Bad ordering:

```dockerfile
COPY . .
RUN mvn package
```

Good ordering:

```dockerfile
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests
```

The goal is not just fewer layers. The goal is reusable layers.

---

## 8. Cache-Friendly Dockerfile Ordering

Docker cache checks whether an instruction and its inputs are unchanged. If unchanged, Docker can reuse the previous layer.

```text
Instruction + input files + build args
             |
             v
          digest check
        /              \
   cache hit        cache miss
      |                 |
 reuse layer       run instruction again
```

Bad cache pattern:

```dockerfile
COPY . .
RUN mvn clean package
```

A small Java change invalidates the full copied context. Then Maven packaging runs again.

Better Maven pattern:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests
```

Flow:

```text
pom.xml unchanged
       |
       v
dependency layer reused
       |
       v
src changed
       |
       v
compile/package layer reruns only
```

This reduces CI time because dependencies change less often than source code.

---

## 9. Multi-Stage Build As Optimization Backbone

Multi-stage builds are the highest ROI optimization for Java services because they cleanly separate builder and runtime.

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY --from=builder /app/target/payment-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Architecture:

```text
Builder stage                         Runtime stage
+---------------------------+         +---------------------------+
| Maven                     |         | JRE                       |
| JDK compiler              |         | app.jar                   |
| source code               | ----->  | entrypoint                |
| dependency cache          | copy    +---------------------------+
| target/payment-service.jar| exact artifact only
+---------------------------+
```

The builder can be large because it is temporary. The runtime must be small because it is shipped.

Rule:

```text
Build with heavy tools.
Run with minimal runtime.
```

---

## 10. Exact Artifact Copy

A common optimization mistake is copying too much from the builder stage.

Bad:

```dockerfile
COPY --from=builder /app /app
```

This may copy source code, Maven files, reports, and temporary files.

Good:

```dockerfile
COPY --from=builder /app/target/payment-service.jar /app/app.jar
```

Mental model:

```text
COPY --from is a narrow bridge.
Only the artifact should cross.
```

```text
Builder filesystem
/app/src
/app/pom.xml
/app/target/payment-service.jar
/root/.m2

Bridge allows:
/app/target/payment-service.jar only

Runtime filesystem
/app/app.jar
```

Make artifact names deterministic in Maven:

```xml
<build>
  <finalName>payment-service</finalName>
</build>
```

Then Dockerfile stays predictable:

```dockerfile
COPY --from=builder /app/target/payment-service.jar app.jar
```

---

## 11. Base Image Selection

Base image choice affects size, compatibility, debugging, and security.

```text
maven / gradle image
  best for builder stage
  bad for runtime stage

JDK image
  useful for tools and diagnostics
  larger than JRE

JRE slim image
  good default for Java runtime
  reasonable compatibility

Alpine image
  very small
  musl libc compatibility surprises possible

Distroless image
  minimal attack surface
  no shell, harder exec debugging
```

Decision map:

```text
Need to compile Java?
   |
   +-- yes -> Maven/Gradle + JDK builder
   |
   +-- no  -> runtime stage
              |
              +-- need easier debugging? -> JRE slim
              +-- hardened prod?        -> distroless
              +-- native compatibility? -> Debian slim often safer
```

Production mindset:

```text
Smallest image is not always best.
Best image is the smallest image that still behaves correctly and is operable.
```

---

## 12. Alpine vs Debian Slim vs Distroless

Alpine is small, but small does not automatically mean safer or easier. Alpine uses musl libc, while many libraries assume glibc. Java applications are usually okay, but native dependencies, font rendering, DNS behavior, timezone data, and observability agents can surprise you.

```text
Alpine advantage
+ small image
+ fewer packages

Alpine risk
- native compatibility surprises
- debugging differences
```

Debian/Ubuntu slim is often larger but more predictable.

```text
Debian slim advantage
+ common production compatibility
+ easier debugging
+ familiar package behavior

Debian slim risk
- larger than Alpine/distroless
```

Distroless removes shell and package managers.

```text
Distroless advantage
+ minimal runtime
+ lower attack surface

Distroless risk
- no sh
- no curl
- no package manager
- debugging needs external tools
```

Senior answer:

```text
Use Debian/JRE slim as a safe default.
Use distroless when observability and debug workflows are mature.
Use Alpine only after compatibility testing.
```

---

## 13. Spring Boot Layered JAR Optimization

Spring Boot fat JARs can be split into logical layers. Dependencies change less often than application classes, so Docker can reuse dependency layers.

```text
Spring Boot layered JAR
+------------------------------+
| dependencies                 | changes rarely
| spring-boot-loader           | changes rarely
| snapshot-dependencies        | changes sometimes
| application classes          | changes often
+------------------------------+
```

Layer extraction idea:

```dockerfile
FROM eclipse-temurin:21-jre AS extractor
WORKDIR /app
COPY target/payment-service.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=extractor /app/dependencies/ ./
COPY --from=extractor /app/spring-boot-loader/ ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

Cache effect:

```text
Change controller class
       |
       v
application layer changes
       |
       v
dependencies layer reused
```

For small teams, copying one JAR is acceptable. For many services and frequent deployments, layered JARs improve CI and registry efficiency.

---

## 14. BuildKit Cache Mounts

BuildKit cache mounts speed up dependency downloads without copying cache into the final image.

```dockerfile
# syntax=docker/dockerfile:1.7
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B clean package -DskipTests
```

Mental model:

```text
Build cache mount
+-----------------------------+
| available during build      |
| reused by future builds     |
| not copied to runtime image |
+-----------------------------+
```

Do not confuse:

```text
Docker layer cache  -> reuses previous instruction output
BuildKit cache mount -> persistent workspace for tools like Maven
Runtime image       -> final shipped filesystem
```

This is very useful in CI where a clean runner would otherwise redownload dependencies often.

---

## 15. Remove Package Manager And Build Tools From Runtime

A runtime image should not contain package managers, compilers, build systems, or source code unless there is a strong reason.

```text
Runtime should not contain:
- gcc
- make
- Maven
- Gradle
- Git
- source code
- test reports
- package manager cache
- private keys
```

Why?

```text
Extra tools
   |
   +--> more CVEs
   +--> bigger image
   +--> bigger attack surface
   +--> more confusing filesystem
```

Minimal runtime:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/payment-service.jar app.jar
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring
ENTRYPOINT ["java", "-jar", "app.jar"]
```

If a production pod needs debugging, use logs, metrics, traces, `kubectl describe`, events, or ephemeral debug containers instead of shipping every image with debugging tools.

---

## 16. Non-Root Runtime User

Running as root inside a container is a common production smell. Containers are isolated, but they are not magic security shields. A non-root user reduces blast radius if the process is compromised.

```text
Root container process
   |
   v
If exploit happens, attacker has stronger permissions inside container
```

```text
Non-root container process
   |
   v
Exploit has reduced permissions inside container filesystem
```

Example:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN addgroup --system spring && adduser --system spring --ingroup spring
COPY --from=builder /app/target/payment-service.jar app.jar
RUN chown -R spring:spring /app
USER spring:spring
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Kubernetes can also enforce this:

```yaml
securityContext:
  runAsNonRoot: true
  runAsUser: 10001
  allowPrivilegeEscalation: false
```

Optimization is not only size. A clean runtime image should also be safer.

---

## 17. Secrets Must Not Enter Images

Never bake secrets into Docker images. Images are copied to registries, cached on nodes, scanned by tools, and often accessible to many systems.

Bad:

```dockerfile
ENV DB_PASSWORD=my-secret-password
COPY service-account.json /app/service-account.json
```

Why bad:

```text
Secret in image
   |
   +--> visible in image history or filesystem
   +--> pushed to registry
   +--> cached on Kubernetes nodes
   +--> hard to rotate
```

Better:

```yaml
env:
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: payment-db-secret
        key: password
```

Mental model:

```text
Image = immutable application package
Secret = runtime environment concern
```

Use `.dockerignore` to prevent accidental secret copy:

```dockerignore
.env
*.pem
*.key
service-account*.json
```

---

## 18. Registry Internals And Layer Deduplication

Registries store image manifests and layer blobs. Layers are content-addressed by digest. If multiple images share the same base layer, the registry stores it once.

```text
Image manifest
+----------------------------+
| config digest              |
| layer digest A             |
| layer digest B             |
| layer digest C             |
+----------------------------+
```

Push flow:

```text
Docker client
   |
   v
Check registry for existing layer digests
   |
   +-- exists  -> skip upload
   +-- missing -> upload layer
   |
   v
Push manifest
```

Optimization helps registry economics:

```text
Stable base layers   -> reused across services
Stable dependency    -> reused across builds
Small app layer      -> fast push per deployment
```

Tags are mutable; digests are immutable.

```text
payment-service:latest       can move
payment-service@sha256:abc   exact content
```

For production, deploy immutable digests when possible.

---

## 19. Kubernetes Pull And Startup Path

Kubernetes starts a pod through several steps. Image size affects the path before your Spring Boot app even starts.

```text
Deployment updated
      |
      v
ReplicaSet creates pod
      |
      v
Scheduler selects node
      |
      v
Kubelet pulls image
      |
      v
Container runtime extracts layers
      |
      v
JVM starts
      |
      v
Spring Boot initializes
      |
      v
Readiness probe passes
      |
      v
Service routes traffic
```

Where optimization helps:

```text
Small image       -> faster pull
Reusable layers   -> node cache hit
Fewer packages    -> cleaner security scan
Correct runtime   -> fewer startup surprises
```

Example deployment:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payment-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: payment-service
  template:
    metadata:
      labels:
        app: payment-service
    spec:
      containers:
        - name: payment-service
          image: registry.example.com/payment-service@sha256:abc123
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
```

---

## 20. ImagePullPolicy And Node Cache

`imagePullPolicy` controls how Kubernetes pulls images.

```text
IfNotPresent
  use local node cache if image exists

Always
  check registry before starting

Never
  only use local image
```

Common production pattern:

```text
Use immutable tags or digests.
Let nodes reuse cached layers.
Avoid mutable latest for production.
```

Node cache picture:

```text
Node filesystem cache
+-----------------------------+
| base runtime layer          |
| JRE layer                   |
| dependencies layer          |
| old app layer               |
+-----------------------------+
          |
          v
New deployment pulls only missing changed layers
```

If every build changes all layers, node cache becomes less useful. Good Dockerfile ordering makes only the top application layer change frequently.

---

## 21. CI/CD Production Flow

A professional CI/CD image pipeline is more than `docker build`.

```text
Developer push
      |
      v
Checkout source
      |
      v
Run unit tests
      |
      v
Build optimized image
      |
      v
Generate SBOM
      |
      v
Vulnerability scan
      |
      v
Push image by digest
      |
      v
Deploy to Kubernetes
      |
      v
Monitor rollout
```

Optimization connection:

```text
Fast build  -> faster feedback
Fast push   -> faster deployment
Fast pull   -> faster rollout
Small image -> less scan noise
Digest      -> reproducible rollback
```

Bad pipeline:

```text
Build huge image -> scan huge image -> push latest -> deploy latest
```

Better pipeline:

```text
Build minimal image -> scan final runtime image -> push digest -> deploy digest
```

Important: scan the final runtime image, not only the builder stage.

---

## 22. Spring Boot Production Dockerfile

A strong default Dockerfile for a Spring Boot service:

```dockerfile
# syntax=docker/dockerfile:1.7

FROM maven:3.9-eclipse-temurin-21 AS deps
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:go-offline

FROM deps AS builder
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
RUN addgroup --system spring && adduser --system spring --ingroup spring
COPY --from=builder /app/target/payment-service.jar app.jar
RUN chown -R spring:spring /app
USER spring:spring
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
```

Why each part exists:

```text
deps stage       -> cache Maven dependencies
builder stage    -> compile and package
runtime stage    -> ship only JRE + app.jar
non-root user    -> reduce runtime risk
MaxRAMPercentage -> make JVM container-aware
```

---

## 23. Docker Compose Example For Local Testing

Local compose should test runtime behavior, not hide image problems.

```yaml
services:
  payment-service:
    build:
      context: .
      dockerfile: Dockerfile
    image: payment-service:local
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: local
      DB_HOST: postgres
    depends_on:
      - postgres

  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: payments
      POSTGRES_USER: payment
      POSTGRES_PASSWORD: payment
    ports:
      - "5432:5432"
```

Dry run:

```text
compose build
   |
   v
multi-stage Dockerfile creates runtime image
   |
   v
compose up starts Postgres
   |
   v
payment container starts with env config
   |
   v
Spring Boot connects to postgres:5432
```

Do not mount local target directories into production-like containers when validating image correctness. That can hide missing artifact problems.

---

## 24. Dry Run: Docker Build

Command:

```bash
docker build --progress=plain -t payment-service:1.0 .
```

Dry run:

```text
1. Docker reads .dockerignore
2. Docker sends filtered build context
3. deps stage starts from Maven image
4. pom.xml copied
5. Maven dependencies downloaded or cache reused
6. builder stage copies source
7. Java code compiles
8. JAR created in target/
9. runtime stage starts from JRE image
10. exact JAR copied from builder
11. non-root user configured
12. final runtime image exported
```

Optimization checkpoints:

```text
Did .git enter context?              no
Did Maven enter final image?         no
Did source code enter final image?   no
Did only app.jar cross stages?       yes
```

---

## 25. Dry Run: Kubernetes Rollout

Command:

```bash
kubectl apply -f deployment.yaml
```

Dry run:

```text
1. Deployment controller creates new ReplicaSet
2. ReplicaSet creates pods
3. Scheduler chooses nodes
4. Kubelet checks local image layers
5. Missing layers pulled from registry
6. Layers extracted by container runtime
7. Container process starts
8. JVM starts with container memory settings
9. Spring Boot starts embedded server
10. Readiness endpoint passes
11. Service routes traffic
```

Optimized image effect:

```text
Smaller image
   |
   +--> faster new node startup
   +--> faster rolling deployment
   +--> faster rollback
   +--> better incident recovery
```

---

## 26. Debugging Image Size

Start with the image history.

```bash
docker history payment-service:1.0
```

What to look for:

```text
Huge COPY layer
Huge RUN apt-get layer
Maven cache inside runtime
target/build copied accidentally
unexpected OS package installs
```

Inspect image:

```bash
docker image inspect payment-service:1.0
```

Check disk usage:

```bash
docker system df
```

Mental debug tree:

```text
Image too large
   |
   +-- base image too large?
   +-- copied too much?
   +-- package manager cache left behind?
   +-- build tools in runtime?
   +-- dependency layer too big?
   +-- fat JAR includes unnecessary libs?
```

Do not randomly switch to Alpine before checking what actually made the image large.

---

## 27. Debugging Cache Misses

Symptoms:

```text
Every build downloads dependencies
Every build recompiles too much
CI build time unstable
Docker output shows no cache reuse
```

Causes:

```text
COPY . . before dependency download
changing build args
timestamp files copied into image
large generated files in context
weak .dockerignore
snapshot dependencies changing often
```

Debug flow:

```text
Check Dockerfile order
       |
       v
Check .dockerignore
       |
       v
Check which files change often
       |
       v
Move stable files earlier
       |
       v
Use BuildKit cache mounts
```

Good rule:

```text
Stable inputs first.
Changing inputs last.
```

---

## 28. Debugging ImagePullBackOff

Image optimization does not remove all pull failures. `ImagePullBackOff` usually means Kubernetes cannot pull the image.

Check:

```bash
kubectl describe pod <pod>
kubectl get events --sort-by=.lastTimestamp
```

Common causes:

```text
wrong image name
wrong tag
private registry secret missing
registry unavailable
architecture mismatch
image digest not found
rate limit
```

Mental map:

```text
Pod pending
   |
   v
Kubelet tries image pull
   |
   +-- auth failed       -> check imagePullSecret
   +-- not found         -> check tag/digest
   +-- timeout           -> check registry/network
   +-- wrong platform    -> check amd64/arm64 build
```

Optimization helps once the pull is possible. It does not fix wrong registry configuration.

---

## 29. Common Failure: Distroless Has No Shell

Distroless is excellent for minimal runtime, but many engineers get surprised during debugging.

```bash
kubectl exec -it payment-pod -- sh
```

Failure:

```text
exec: "sh": executable file not found
```

This is expected. Distroless intentionally removes shell.

Better debugging options:

```text
Use application logs
Use actuator health endpoints
Use metrics and traces
Use kubectl describe/events
Use ephemeral debug containers
Use a separate debug image, not production image
```

Mental model:

```text
Production image -> run safely
Debug image      -> investigate safely
```

Do not add bash to production distroless just because debugging is uncomfortable. Improve the debugging workflow.

---

## 30. Common Failure: Missing CA Certificates Or Timezone

Minimal images sometimes remove things your application assumes exist.

Symptoms:

```text
HTTPS call fails
certificate path error
unknown timezone
date conversion issue
```

Causes:

```text
CA certificates missing
zoneinfo missing
native library missing
font library missing
```

Debug questions:

```text
Does the app call external HTTPS services?
Does it use named timezones like Europe/Bucharest?
Does it generate PDFs or images?
Does it depend on native libraries?
```

Production lesson:

```text
Minimal does not mean empty.
Runtime image must contain the real runtime dependencies of the application.
```

---

## 31. Security Scanning And SBOM

Optimized images reduce vulnerability noise because there are fewer packages. But you still need scanning.

```text
Image scan
   |
   v
Find vulnerable OS packages and libraries
   |
   v
Prioritize runtime-reachable risk
```

SBOM means Software Bill of Materials. It lists what is inside your image or artifact.

```text
SBOM
+------------------------------+
| OS packages                  |
| Java dependencies            |
| versions                     |
| licenses                     |
| checksums                    |
+------------------------------+
```

Production pipeline:

```text
Build final image
      |
      v
Generate SBOM
      |
      v
Scan final image
      |
      v
Block critical issues based on policy
```

Do not scan only local source dependencies. Scan the final image that will actually run.

---

## 32. Production Case Study

A team had 40 Spring Boot services. Each used a single-stage Maven image. Average image size was around 850 MB. Deployments were slow, CI was expensive, and Kubernetes node replacement during incidents caused long recovery times.

Before:

```text
40 services
850 MB average image
Maven + JDK + source in runtime
slow scan and pull
```

After optimization:

```text
40 services
180-250 MB average image
multi-stage builds
JRE runtime
.dockerignore standardized
non-root user
immutable digest deployment
```

Impact:

```text
CI push faster
registry storage lower
node cold-start faster
CVEs easier to triage
rollbacks more predictable
```

Production lesson:

```text
Image optimization is a platform multiplier.
Small improvements repeat across every service and every deployment.
```

---

## 33. FAANG/System Design Discussion

In system design interviews, image optimization connects to deployment velocity and reliability.

Strong explanation:

```text
At small scale, a large image is annoying.
At large scale, it slows down autoscaling, rollouts, node recovery, scanning, and registry replication.
```

For a high-scale platform:

```text
Thousands of pods
   |
   v
Many nodes need image layers
   |
   v
Registry, network, and node disk become part of deployment performance
```

Senior discussion points:

```text
Use multi-stage builds for clean runtime images.
Keep dependencies in stable reusable layers.
Deploy immutable digests.
Use registry and node layer caching.
Scan final runtime images.
Avoid mutable latest tags.
Use distroless only with mature observability.
```

This shows you understand not only Docker, but also production operations.

---

## 34. Interview Answers

**What is Docker image optimization?**  
It is the practice of reducing unnecessary runtime content and improving build/cache structure so images build faster, push faster, pull faster, scan cleaner, and run more safely.

**What is the highest ROI optimization?**  
Multi-stage builds. They allow heavy build tools in the builder stage while shipping only the runtime artifact in the final image.

**Why does Dockerfile ordering matter?**  
Docker cache depends on instruction inputs. Copying stable files like `pom.xml` before source code allows dependency layers to be reused when only application code changes.

**Why avoid `latest` in production?**  
`latest` is mutable. The same deployment config can point to different image contents over time. Digests are immutable and reproducible.

**Why can smaller images improve Kubernetes behavior?**  
Nodes pull and extract fewer bytes, so cold starts, rollouts, and recovery after node replacement become faster.

**Is Alpine always best?**  
No. Alpine is small, but compatibility can surprise you. Debian slim or distroless may be better depending on runtime needs and debugging maturity.

---

## 35. Production Checklist

```text
Build context
[ ] .dockerignore excludes secrets, target, .git, logs, IDE files
[ ] Docker build context is not huge

Dockerfile cache
[ ] dependency descriptors copied before source
[ ] BuildKit cache mount used for Maven/Gradle if useful
[ ] frequently changing files copied late

Runtime image
[ ] builder tools not present
[ ] source code not present
[ ] exact artifact copied
[ ] small compatible base image selected
[ ] non-root user configured
[ ] JVM memory flags container-aware

Security
[ ] no secrets baked into image
[ ] final image scanned
[ ] SBOM generated if required
[ ] digest used for production deployment

Kubernetes
[ ] readiness/liveness probes configured
[ ] imagePullSecret works for private registry
[ ] imagePullPolicy matches tag/digest strategy
[ ] rollout monitored
```

---

## 36. Cheat Sheet

```text
Image optimization = small + safe + cache-friendly + reproducible
```

Most useful tools:

```text
.dockerignore       -> keep junk out of build context
multi-stage build   -> keep build tools out of runtime
small base image    -> reduce bytes and packages
layer ordering      -> maximize cache reuse
BuildKit cache      -> speed dependency downloads
non-root user       -> reduce security risk
digest deployment   -> reproducible rollout
```

Commands:

```bash
docker build --progress=plain -t payment-service:local .
docker history payment-service:local
docker image inspect payment-service:local
docker system df
docker run --rm -p 8080:8080 payment-service:local
kubectl describe pod <pod>
kubectl logs <pod>
kubectl get events --sort-by=.lastTimestamp
```

---

## 37. One Picture To Remember

```text
                    DOCKER IMAGE OPTIMIZATION

        BUILD WORLD                              RUNTIME WORLD
+----------------------------+           +----------------------------+
| source code                |           | minimal JVM/JRE            |
| Maven / Gradle             |           | app.jar                    |
| JDK compiler               |           | certificates if needed     |
| tests                      |           | timezone if needed         |
| dependency cache           |           | non-root user              |
| temporary files            |           | entrypoint                 |
+-------------+--------------+           +-------------+--------------+
              |                                        ^
              | exact artifact only                     |
              +----------------------------------------+

Before Docker build:
.dockerignore keeps junk out.

During Docker build:
cache-friendly layers avoid repeated work.

After Docker build:
registry and Kubernetes reuse stable layers.

Rule:
Build with enough tools.
Ship with only enough runtime.
```

---

## 38. Final Takeaways

1. Image optimization is production engineering, not only Docker syntax.
2. The best mental model is: every byte must justify its runtime existence.
3. `.dockerignore` protects build context hygiene.
4. Multi-stage builds separate build tools from runtime artifacts.
5. Exact `COPY --from` prevents source and temporary files from leaking into runtime.
6. Cache-friendly Dockerfile ordering makes CI faster.
7. Base image choice is a tradeoff between size, compatibility, security, and debugging.
8. Distroless is powerful, but requires mature debugging and observability.
9. Kubernetes benefits from smaller images through faster pull, extraction, rollout, and recovery.
10. Senior engineers explain image optimization through deployment velocity, security posture, registry efficiency, and operational reliability.
