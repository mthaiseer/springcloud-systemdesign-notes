# 009_Dockerfile_Deep_Dive

## Why This Matters

A Dockerfile is the source code of a Docker image. Every production container begins with a Dockerfile. Understanding Dockerfile design directly impacts:

- Image size
- Build speed
- Security
- CI/CD performance
- Kubernetes startup time
- Deployment reliability

A poorly designed Dockerfile can create multi‑GB images, slow builds, and security risks. A well-designed Dockerfile creates small, reproducible, cache-friendly images.

---

## Mental Model

```text
Java Source
     |
Maven Build
     |
Dockerfile
     |
Docker Image
     |
Registry
     |
Kubernetes
     |
Container
```

Dockerfile = Recipe

Image = Cake

Container = Slice being served

---

## Core Concepts

### FROM

Defines the base image.

```dockerfile
FROM eclipse-temurin:21-jre
```

Why needed:

Provides operating system and runtime.

### WORKDIR

Sets working directory.

```dockerfile
WORKDIR /app
```

### COPY

Copies files.

```dockerfile
COPY app.jar app.jar
```

### RUN

Executes build-time commands.

```dockerfile
RUN apt-get update
```

### ENV

Defines environment variables.

```dockerfile
ENV JAVA_OPTS="-Xmx512m"
```

### ENTRYPOINT

Main process.

```dockerfile
ENTRYPOINT ["java","-jar","app.jar"]
```

---

## Internal Architecture

```text
Dockerfile
     |
Parser
     |
Instruction Executor
     |
Layer Builder
     |
Image Manifest
     |
Docker Image
```

Each major instruction contributes a layer.

---

## Step-by-Step Flow

1. Read Dockerfile.
2. Process FROM.
3. Pull base image.
4. Execute instructions.
5. Create layers.
6. Generate manifest.
7. Tag image.
8. Push registry.
9. Pull on deployment target.

---

## Deep Walkthrough Example

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/app.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

Build Flow:

```text
Pull Base Image
      |
Create WORKDIR Layer
      |
Create COPY Layer
      |
Store Metadata
      |
Build Complete
```

---

## Layer-by-Layer Example

Bad:

```dockerfile
COPY . .
RUN mvn clean package
```

Any source change:

```text
Invalidate Everything
```

Good:

```dockerfile
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src src
RUN mvn package
```

Result:

```text
Dependencies Cached
Source Rebuilt Only
```

---

## Data Structures Used

```java
class DockerInstruction {
    String type;
    String value;
}
```

```java
class DockerLayer {
    String digest;
    long size;
}
```

---

## Algorithms Used

### Cache Matching

```text
Instruction Hash
      |
Compare Previous Build
      |
Hit / Miss
```

### Layer Reuse

```text
Existing Digest?
      |
Reuse Layer
```

---

## Production Implementation

```text
Developer
    |
Git Push
    |
CI Pipeline
    |
Docker Build
    |
Registry Push
    |
Kubernetes Deploy
```

---

## Java Code Examples

```java
public class DockerMetadata {

    private String image;

    private String tag;

    public DockerMetadata(
            String image,
            String tag) {

        this.image = image;
        this.tag = tag;
    }
}
```

Dry Run:

```text
Create Metadata
Attach Tag
Push Registry
```

---

## Spring Boot Example

application.yml

```yaml
spring:
  application:
    name: user-service

server:
  port: 8080
```

Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre

COPY target/user-service.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

Build:

```bash
docker build -t user-service:v1 .
```

Run:

```bash
docker run -p 8080:8080 user-service:v1
```

Startup:

```text
Container
  |
JVM
  |
Spring Context
  |
Tomcat
  |
Ready
```

---

## Dockerfile Example

Production Example:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/user-service.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
```

---

## Multi-Stage Build Example

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS builder

COPY . .

RUN mvn clean package

FROM eclipse-temurin:21-jre

COPY --from=builder target/app.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

Benefits:

- Smaller image
- Faster pull
- Better security

---

## Docker Build Cache Example

Good ordering:

```dockerfile
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src src
```

Bad ordering:

```dockerfile
COPY . .
RUN mvn package
```

Cache Invalidation:

```text
pom.xml change
      |
Dependency Layer Rebuild
```

---

## Registry Internals

Push:

```text
Layer A
Layer B
Layer C
```

Registry stores unique digests.

Digest:

```text
sha256:abcd
```

Immutable.

Tag:

```text
latest
v1
```

Mutable.

---

## Kubernetes Example

```yaml
image: user-service:v1
imagePullPolicy: IfNotPresent
```

Flow:

```text
Node
 |
Layer Cache Check
 |
Pull Missing Layers
 |
Start Container
```

ImagePullBackOff causes:

- Wrong image
- Wrong credentials
- Missing tag

---

## Sequence Diagram (ASCII)

```text
Developer
   |
Docker Build
   |
Image
   |
Registry
   |
Kubernetes
   |
Container
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

### Build

```text
Dockerfile
  |
Layers
  |
Image
```

### Run

```text
Image
  |
Container
```

### Failure

```text
Wrong ENTRYPOINT
      |
Container Exit
```

### Kubernetes

```text
Deployment
  |
Pull
  |
Start
```

---

## Failure Scenarios

- Large image
- Cache miss
- Wrong tag
- Missing config
- Permission denied
- Wrong ENTRYPOINT
- ImagePullBackOff

---

## Failure Investigation Playbook

```bash
docker history image
docker inspect image
docker logs container
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

Use:

- Small base images
- Multi-stage builds
- Cache-friendly ordering

Avoid:

- Huge COPY operations
- Unnecessary tools

---

## Scalability Considerations

Optimized Dockerfiles reduce:

- Registry bandwidth
- Node startup time
- Deployment duration

---

## Common Interview Questions

Q1 What is a Dockerfile?
A: Recipe used to build Docker images.

Q2 What does FROM do?
A: Defines base image.

Q3 What does COPY do?
A: Copies files into image.

Q4 What does RUN do?
A: Executes build-time commands.

Q5 What is ENTRYPOINT?
A: Main container process.

Q6 What is CMD?
A: Default arguments.

Q7 Difference ENTRYPOINT vs CMD?
A: ENTRYPOINT defines executable, CMD provides defaults.

Q8 Why multi-stage builds?
A: Smaller runtime image.

Q9 What creates layers?
A: Dockerfile instructions.

Q10 What is cache hit?
A: Reuse previous layer.

Q11 What is cache miss?
A: Rebuild layer.

Q12 Why small images?
A: Faster deployment.

Q13 Why immutable images?
A: Predictability.

Q14 Registry role?
A: Store images.

Q15 Digest?
A: Immutable hash.

Q16 Tag?
A: Mutable reference.

Q17 ImagePullBackOff?
A: Pull failure.

Q18 Why EXPOSE?
A: Documentation of intended port.

Q19 Best base image?
A: Small trusted image.

Q20 Why use non-root?
A: Security.

Q21-Q30 Senior topics: supply-chain security, reproducible builds, SBOMs, CI optimization, registry caching, deployment strategies, image scanning, rollback safety, digest pinning, build performance.

---

## Strong Interview Answers

1. Dockerfile is infrastructure source code describing how to build immutable images.

2. Multi-stage builds separate build dependencies from runtime dependencies.

3. Cache-friendly Dockerfiles dramatically reduce CI execution time.

4. Digests guarantee immutable deployments; tags are convenience aliases.

5. Production Dockerfiles prioritize security, size, reproducibility, and startup speed.

---

## Real World Production Case Study

Microservice Platform:

```text
Gateway
User Service
Order Service
Payment Service
```

Standardized Dockerfile template:

```text
Common JRE Layer
Common Security Settings
Service-Specific Layer
```

Benefits:

- Faster builds
- Smaller images
- Consistent deployments

---

## FAANG/System Design Discussion

Key topics:

```text
Immutable Infrastructure
CI/CD Optimization
Registry Scalability
Deployment Speed
Container Security
```

Architecture:

```text
Git
 |
CI
 |
Docker Build
 |
Registry
 |
Kubernetes
```

---

## Production Checklist

- Multi-stage build
- Small base image
- Non-root user
- Vulnerability scan
- Health checks
- Cache optimization
- Digest pinning

---

## One-Page Cheat Sheet

```text
FROM -> Base Image

COPY -> Files

RUN -> Build Commands

ENV -> Variables

WORKDIR -> Working Directory

ENTRYPOINT -> Main Process

CMD -> Defaults
```

---

## Last-Minute Interview Revision

```text
Dockerfile = Image Recipe

ENTRYPOINT = Executable

CMD = Arguments

Multi-stage = Smaller Image

Cache = Faster Build

Digest = Immutable

Tag = Mutable
```

---

## Mental Models Table

| Concept | Mental Model |
|----------|-------------|
| Dockerfile | Recipe |
| Image | Cake |
| Layer | Ingredient |
| Registry | Warehouse |
| Digest | Fingerprint |
| Tag | Nickname |
| Container | Running Product |

---

## Key Takeaways

1. Dockerfile defines image creation.
2. Every instruction affects layers.
3. Cache-friendly ordering matters.
4. Multi-stage builds reduce size.
5. Digests provide immutability.
6. Tags are mutable.
7. Small images deploy faster.
8. Registry stores layers.
9. Kubernetes pulls images built from Dockerfiles.
10. Dockerfile quality directly affects production performance.
