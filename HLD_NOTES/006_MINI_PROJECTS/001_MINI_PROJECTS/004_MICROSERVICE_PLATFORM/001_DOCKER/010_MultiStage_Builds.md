# 010_MultiStage_Builds

## Why This Matters

Multi-stage builds are one of the most important Docker optimization techniques used in modern production systems.

Without multi-stage builds:

```text
Source Code
Maven
JDK
Build Tools
Dependencies
Application
```

all end up inside the final image.

Result:

```text
Large Image
Slower Pull
More Security Risks
Longer Startup
```

With multi-stage builds:

```text
Build Environment
      |
Compile
      |
Copy Artifact Only
      |
Small Runtime Image
```

Benefits:

- Smaller images
- Faster deployments
- Lower registry storage
- Faster Kubernetes startup
- Better security posture

---

## Mental Model

Think of building a house.

Construction phase:

```text
Workers
Tools
Cement Mixer
Crane
```

Needed only while building.

After completion:

```text
House
```

You do not ship the crane to the customer.

Multi-stage builds work similarly.

```text
Builder Stage
      |
Create Artifact
      |
Runtime Stage
      |
Copy Artifact Only
```

---

## Core Concepts

### Builder Stage

Contains:

- Maven
- Gradle
- JDK
- Build Tools

Purpose:

Compile application.

Advantages:

- Complete build environment

Disadvantages:

- Large image

---

### Runtime Stage

Contains:

- JRE
- Application

Purpose:

Run application.

Advantages:

- Small
- Secure

---

### Artifact Transfer

```dockerfile
COPY --from=builder
```

moves artifacts between stages.

---

## Internal Architecture

```text
Stage 1 Builder
+----------------+
| Maven          |
| JDK            |
| Source Code    |
+----------------+

        |
        | COPY --from
        v

Stage 2 Runtime
+----------------+
| JRE            |
| app.jar        |
+----------------+
```

---

## Step-by-Step Flow

1. Pull builder image.
2. Copy source code.
3. Download dependencies.
4. Compile application.
5. Produce jar.
6. Start runtime stage.
7. Pull runtime image.
8. Copy jar only.
9. Build final image.

---

## Deep Walkthrough Example

Traditional Build:

```dockerfile
FROM maven:3.9-eclipse-temurin-21

COPY . .

RUN mvn clean package

ENTRYPOINT ["java","-jar","target/app.jar"]
```

Image contains:

```text
Maven
JDK
Source
Dependencies
Target Jar
```

Size:

```text
900MB+
```

Multi-stage:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build

COPY . .

RUN mvn clean package

FROM eclipse-temurin:21-jre

COPY --from=build target/app.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

Final image:

```text
JRE
Application
```

Size:

```text
200MB or less
```

---

## Layer-by-Layer Example

Builder Stage:

```text
Layer 1 Maven Base
Layer 2 Source
Layer 3 Dependencies
Layer 4 Build Output
```

Runtime Stage:

```text
Layer 1 JRE Base
Layer 2 Application Jar
```

Only runtime layers shipped.

---

## Data Structures Used

```java
class BuildStage {

    String name;

    String baseImage;
}
```

```java
class Artifact {

    String path;

    long size;
}
```

---

## Algorithms Used

### Layer Reuse

```text
Digest Match
      |
Reuse Layer
```

### Cache Matching

```text
Instruction Hash
      |
Cache Hit/Miss
```

### Artifact Transfer

```text
Builder Stage
      |
Copy Output
      |
Runtime Stage
```

---

## Production Implementation

```text
Git Push
   |
CI Pipeline
   |
Multi-stage Build
   |
Registry Push
   |
Kubernetes Deploy
```

---

## Java Code Examples

Build Artifact Model:

```java
public class Artifact {

    private String name;

    private long size;

    public Artifact(
            String name,
            long size) {

        this.name = name;
        this.size = size;
    }
}
```

Dry Run:

```text
Compile
Generate Jar
Copy To Runtime
```

---

## Spring Boot Example

application.yml

```yaml
spring:
  application:
    name: order-service
```

Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre

COPY target/order.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

Build:

```bash
docker build -t order-service:v1 .
```

Run:

```bash
docker run -p 8080:8080 order-service:v1
```

Startup:

```text
Container
 |
JVM
 |
Spring Context
 |
Ready
```

---

## Dockerfile Example

Bad:

```dockerfile
FROM maven:3.9

COPY . .

RUN mvn package

ENTRYPOINT ["java","-jar","target/app.jar"]
```

Good:

```dockerfile
Multi-stage
```

Runtime image much smaller.

---

## Multi-Stage Build Example

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src src

RUN mvn clean package

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/app.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

---

## Docker Build Cache Example

Good:

```dockerfile
COPY pom.xml .

RUN mvn dependency:go-offline

COPY src src
```

Bad:

```dockerfile
COPY . .
```

Cache invalidated frequently.

---

## Registry Internals

Push Flow:

```text
Builder Layers
      |
Ignored

Runtime Layers
      |
Uploaded
```

Registry stores:

```text
Manifest
Layers
Digests
```

Digest:

```text
Immutable
```

Tag:

```text
Mutable
```

---

## Kubernetes Example

Deployment:

```yaml
image: order-service:v1
```

Startup:

```text
Node
 |
Pull Image
 |
Start Pod
```

Smaller image:

```text
Faster Startup
```

ImagePullBackOff:

- Wrong image
- Missing tag
- Registry issue

---

## Sequence Diagram (ASCII)

```text
Developer
   |
Git Push
   |
CI Build
   |
Builder Stage
   |
Runtime Stage
   |
Registry
   |
Kubernetes
```

---

## Request Lifecycle

```text
Client
  |
Ingress
  |
Pod
  |
Container
  |
Spring Boot
```

---

## Multiple Dry Runs

### Build Dry Run

```text
Builder Stage
     |
Compile
     |
Jar
     |
Runtime Stage
```

### Run Dry Run

```text
Image
 |
Container
 |
Application
```

### Failure Dry Run

```text
Jar Missing
 |
Container Exit
```

### Kubernetes Dry Run

```text
Pull
 |
Start
 |
Ready
```

---

## Failure Scenarios

### Large Image

Cause:

Single-stage build.

### Cache Miss

Dependency redownload.

### Disk Full

Build fails.

### Wrong Tag

Image pull failure.

### Missing Config

Startup failure.

### Permission Issue

File access denied.

---

## Failure Investigation Playbook

```bash
docker history image

docker image inspect image

docker system df
```

Kubernetes:

```bash
kubectl describe pod

kubectl logs pod
```

---

## Debugging Guide

```bash
docker build --progress=plain .

docker history image

docker inspect image
```

---

## Performance Considerations

Benefits:

- Smaller image
- Faster pull
- Better cache reuse

Avoid:

- Huge builder images
- Copying unnecessary files

---

## Scalability Considerations

At scale:

```text
100 Services
```

Multi-stage builds save:

- Storage
- Bandwidth
- Deployment time

---

## Common Interview Questions

Q1 What is multi-stage build?
A: Multiple build stages in one Dockerfile.

Q2 Why needed?
A: Smaller runtime image.

Q3 Builder stage purpose?
A: Compile application.

Q4 Runtime stage purpose?
A: Run application.

Q5 COPY --from?
A: Copies artifact from another stage.

Q6 Why smaller image?
A: Build tools removed.

Q7 Security benefit?
A: Fewer attack surfaces.

Q8 Startup benefit?
A: Faster pulls.

Q9 Registry benefit?
A: Less storage.

Q10 Kubernetes benefit?
A: Faster scheduling.

Q11 Single-stage downside?
A: Large image.

Q12 Maven in runtime image?
A: Not needed.

Q13 JDK vs JRE?
A: Build vs runtime.

Q14 Cache impact?
A: Faster CI.

Q15 Layer reuse?
A: Shared layers.

Q16 Artifact transfer?
A: COPY --from.

Q17 Digest?
A: Immutable hash.

Q18 Tag?
A: Mutable reference.

Q19 ImagePullBackOff?
A: Pull failure.

Q20 Production best practice?
A: Multi-stage builds.

Q21-Q30:
Senior discussions around security scanning, SBOMs, cache optimization, CI pipelines, registry economics, startup performance, immutable infrastructure, reproducible builds, digest pinning, deployment velocity.

---

## Strong Interview Answers

1. Multi-stage builds separate build concerns from runtime concerns.

2. Runtime images should contain only what is necessary to execute the application.

3. Smaller images improve startup time and security.

4. COPY --from is the key mechanism enabling stage isolation.

5. Multi-stage builds are standard practice in production cloud-native systems.

---

## Real World Production Case Study

Platform:

```text
Gateway
Order
Payment
Inventory
```

Before:

```text
900MB Images
```

After:

```text
180MB Images
```

Benefits:

- Faster CI
- Faster deployments
- Lower registry costs

---

## FAANG/System Design Discussion

Topics:

```text
Deployment Velocity
Image Optimization
Registry Scaling
Startup Performance
Cloud-Native Security
```

Architecture:

```text
Git
 |
CI
 |
Multi-stage Build
 |
Registry
 |
Kubernetes
```

---

## Production Checklist

- Multi-stage build
- Small runtime image
- Non-root user
- Vulnerability scan
- Cache optimization
- Digest pinning

---

## One-Page Cheat Sheet

```text
Builder Stage
    |
Compile

Runtime Stage
    |
Run

COPY --from
    |
Transfer Artifact
```

---

## Last-Minute Interview Revision

```text
Multi-stage

Build Image
      |
Runtime Image

Smaller
Safer
Faster
```

---

## Mental Models Table

| Concept | Mental Model |
|----------|-------------|
| Builder Stage | Factory |
| Runtime Stage | Finished Product |
| Artifact | Manufactured Part |
| COPY --from | Transfer Truck |
| Registry | Warehouse |
| Runtime Image | Delivery Package |

---

## Key Takeaways

1. Multi-stage builds reduce image size.
2. Builder stage compiles code.
3. Runtime stage runs code.
4. Build tools stay out of production image.
5. Faster pulls improve deployments.
6. Smaller images improve security.
7. Registry storage reduced.
8. Kubernetes startup improved.
9. COPY --from enables artifact transfer.
10. Multi-stage builds are a production best practice.
