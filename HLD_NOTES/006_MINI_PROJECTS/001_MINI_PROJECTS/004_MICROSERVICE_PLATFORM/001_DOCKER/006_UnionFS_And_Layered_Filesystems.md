# 006_UnionFS_And_Layered_Filesystems

# Title

## Why This Matters

UnionFS and layered filesystems are the reason Docker images are practical at scale. Without layers, every image would contain a complete copy of the operating system, runtime, libraries, and application. Layering allows reuse, caching, fast deployments, smaller downloads, and immutable infrastructure.

A senior engineer should understand:

- Why Docker images are composed of layers
- Why builds are fast or slow
- Why cache hits matter
- Why image size matters in Kubernetes
- How registries store images efficiently

---

## Mental Model

Think of Docker images as Git commits for filesystems.

```text
Layer 1: Ubuntu Base
Layer 2: Java Runtime
Layer 3: Dependencies
Layer 4: Application

Combined View
      |
      V
Single Filesystem
```

Application sees one filesystem.

Internally Docker sees many layers.

---

## Core Concepts

### UnionFS

A union filesystem merges multiple directories into one logical view.

Advantages:

- Layer reuse
- Storage efficiency
- Fast distribution

Disadvantages:

- Layer management complexity

Interview Explanation:

UnionFS presents multiple filesystem layers as one filesystem.

### Layered Images

Each Dockerfile instruction creates a new layer.

Example:

```dockerfile
FROM eclipse-temurin:21-jre
COPY app.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
```

Results:

```text
Base Layer
Java Layer
Application Layer
```

### Immutable Layers

Layers never change.

Instead:

```text
Old Layer
     |
New Layer
```

is created.

Benefits:

- Safe sharing
- Predictable deployments
- Cache reuse

---

## Internal Architecture

```text
Container Filesystem
        |
+-------------------+
| Writable Layer    |
+-------------------+
| Application Layer |
+-------------------+
| Dependency Layer  |
+-------------------+
| JRE Layer         |
+-------------------+
| OS Layer          |
+-------------------+
```

---

## Step-by-Step Flow

1. Developer writes Dockerfile.
2. Docker processes instructions.
3. Each instruction creates a layer.
4. Layers are stored locally.
5. Image pushed to registry.
6. Registry stores unique layers.
7. Kubernetes node pulls only missing layers.
8. Container adds writable layer.
9. Application starts.

---

## Deep Walkthrough Example

Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
```

Layer Creation:

```text
Layer 1 -> Base Image
Layer 2 -> WORKDIR Metadata
Layer 3 -> Application JAR
Layer 4 -> Startup Metadata
```

If app.jar changes:

```text
Layer 1 Reused
Layer 2 Reused
Layer 3 Rebuilt
Layer 4 Rebuilt
```

This is why Docker builds are efficient.

---

## Layer-by-Layer Example

Bad Dockerfile:

```dockerfile
COPY . .
RUN mvn clean package
```

Every code change invalidates cache.

Good Dockerfile:

```dockerfile
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src src
RUN mvn package
```

Result:

```text
Dependencies Layer Reused
Only Source Layer Rebuilt
```

---

## Data Structures Used

```java
class Layer {
    String id;
    String parentId;
    long size;
    String digest;
}
```

```java
class Image {
    String imageId;
    List<Layer> layers;
}
```

---

## Algorithms Used

### Layer Resolution

```text
Request File
     |
Search Top Layer
     |
Not Found
     |
Search Parent Layer
```

### Cache Matching

```text
Instruction Hash
      |
Compare Previous Build
      |
Cache Hit / Cache Miss
```

---

## Production Implementation

Production flow:

```text
Git Push
   |
CI Build
   |
Docker Build
   |
Registry Push
   |
Kubernetes Pull
   |
Pod Startup
```

---

## Java Code Examples

### Layer Representation

```java
public class Layer {

    private final String id;
    private final long size;

    public Layer(String id,long size){
        this.id=id;
        this.size=size;
    }
}
```

Dry Run:

```text
Create Layer
Store Metadata
Attach To Image
```

### Image Representation

```java
import java.util.*;

public class Image {

    private List<Layer> layers =
        new ArrayList<>();
}
```

---

## Spring Boot Example

application.yml

```yaml
server:
  port: 8080

spring:
  application:
    name: user-service
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

Startup Lifecycle:

```text
Container Start
      |
JVM Start
      |
Spring Context
      |
Tomcat
      |
Application Ready
```

---

## Dockerfile Example

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/app.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

Layer Analysis:

```text
FROM      -> Layer A
WORKDIR   -> Layer B
COPY      -> Layer C
ENTRYPOINT-> Metadata
```

---

## Multi-Stage Build Example

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /src

COPY . .

RUN mvn clean package

FROM eclipse-temurin:21-jre

COPY --from=builder /src/target/app.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

Benefits:

```text
Smaller Image
No Maven Runtime
Better Security
```

---

## Docker Build Cache Example

Cache Hit:

```text
pom.xml unchanged
Dependencies reused
```

Cache Miss:

```text
pom.xml changed
Dependencies redownloaded
```

Good Ordering:

```dockerfile
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src src
```

Bad Ordering:

```dockerfile
COPY . .
RUN mvn package
```

---

## Registry Internals

Image Push:

```text
Layer A
Layer B
Layer C
```

Registry checks:

```text
Already Exists?
```

If yes:

```text
Skip Upload
```

Digest:

```text
sha256:xxxxx
```

Digest is immutable.

Tag:

```text
latest
v1
v2
```

Tag can point to different images.

Interview Answer:

```text
Tag Mutable
Digest Immutable
```

---

## Kubernetes Example

Deployment:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  replicas: 3
```

Image Pull Flow:

```text
Scheduler
   |
Node Selected
   |
Check Layer Cache
   |
Download Missing Layers
   |
Create Container
```

imagePullPolicy:

```text
Always
IfNotPresent
Never
```

ImagePullBackOff Causes:

- Wrong image
- Wrong credentials
- Network issue
- Missing tag

---

## Sequence Diagram (ASCII)

```text
Developer
   |
docker build
   |
Layers Created
   |
Registry Push
   |
Kubernetes Pull
   |
Container Start
```

---

## Request Lifecycle

```text
Client
   |
Load Balancer
   |
Pod
   |
Container
   |
Spring Boot
   |
Database
```

---

## Multiple Dry Runs

### Build Dry Run

```text
Read Dockerfile
Create Layers
Store Cache
Build Image
```

### Run Dry Run

```text
Image
 |
Writable Layer
 |
Container
 |
Application
```

### Failure Dry Run

```text
Wrong Jar
 |
Container Exit
 |
CrashLoopBackOff
```

### Kubernetes Dry Run

```text
Deployment
 |
Node
 |
Pull Image
 |
Start Pod
 |
Ready
```

---

## Failure Scenarios

### Large Image

```text
2GB Image
```

Effects:

- Slow build
- Slow deployment

### Cache Miss

Every build downloads dependencies.

### Disk Full

Cannot create new layers.

### ImagePullBackOff

Image unavailable.

### Wrong Tag

```text
v5 requested
v5 absent
```

### Missing Config

Application startup failure.

### Permission Issue

Cannot read files.

---

## Failure Investigation Playbook

### Large Image

Commands:

```bash
docker history image
docker image inspect image
```

### Cache Problems

```bash
docker build --progress=plain .
```

### Registry Issue

```bash
docker pull image
```

### Kubernetes

```bash
kubectl describe pod
kubectl logs pod
```

---

## Debugging Guide

```bash
docker images
docker history image
docker inspect image
docker system df
docker logs container
```

Kubernetes:

```bash
kubectl get pods
kubectl describe pod
```

---

## Performance Considerations

- Small base image
- Multi-stage builds
- Layer reuse
- Cache optimization

Avoid:

- Huge COPY commands
- Unused dependencies

---

## Scalability Considerations

Layer reuse enables:

```text
100 Services
Shared Runtime Layers
```

Network traffic reduced.

Storage reduced.

---

## Common Interview Questions

Q1: What is UnionFS?
A: Filesystem that merges multiple layers.

Q2: Why layers?
A: Storage and network efficiency.

Q3: Are layers mutable?
A: No.

Q4: What is writable layer?
A: Container-specific layer.

Q5: What is copy-on-write?
A: Copy only on modification.

Q6: Why image caching?
A: Faster builds.

Q7: What invalidates cache?
A: Instruction or input change.

Q8: Why multi-stage builds?
A: Smaller images.

Q9: What is image digest?
A: Immutable content hash.

Q10: What is image tag?
A: Mutable reference.

Q11: Registry stores what?
A: Layers and metadata.

Q12: Why image size matters?
A: Startup speed.

Q13: Kubernetes layer reuse?
A: Pull only missing layers.

Q14: ImagePullBackOff?
A: Image cannot be pulled.

Q15: Docker history?
A: Layer history.

Q16: Layer sharing?
A: Yes.

Q17: Build cache benefit?
A: Faster CI.

Q18: Why immutable images?
A: Predictability.

Q19: Registry optimization?
A: Deduplicate layers.

Q20: Explain layers in one minute.
A: Images are immutable stacks of filesystem changes.

Q21-Q30:
Senior discussions on caching, registries, digests, supply chain security, canary deployments, and image governance.

---

## Strong Interview Answers

### Explain UnionFS

UnionFS combines multiple immutable filesystem layers into a single logical filesystem. Containers add a writable layer on top. This design enables image sharing, caching, and efficient distribution.

### Why Docker Layers Matter

Layers allow reuse. Hundreds of services can share the same Java runtime layer, reducing storage and network costs.

### Why Multi-Stage Builds

They separate build-time dependencies from runtime dependencies, producing smaller and safer images.

### Tag vs Digest

Tags are mutable references. Digests uniquely identify image content and never change.

### Why Kubernetes Starts Faster On Warm Nodes

Nodes already have common layers cached locally, so only missing layers are downloaded.

---

## Real World Production Case Study

Microservice Platform:

```text
Gateway
User Service
Order Service
Payment Service
Notification Service
```

All use:

```text
Ubuntu Base
Java Runtime
```

Only application layers differ.

Benefits:

- Smaller registry
- Faster deployments
- Faster rollbacks

---

## FAANG/System Design Discussion

Topics:

- Immutable infrastructure
- CI/CD optimization
- Image caching
- Deployment speed
- Registry scalability
- Kubernetes startup optimization

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
- Scan image
- Use digests
- Minimize layers
- Optimize cache
- Monitor registry usage

---

## One-Page Cheat Sheet

```text
Image = Layers

Container =
Image + Writable Layer

UnionFS =
Merged View

Digest =
Immutable

Tag =
Mutable

Multi-stage =
Smaller Images
```

---

## Last-Minute Interview Revision

```text
Layers -> Reuse

UnionFS -> Merge

Digest -> Immutable

Tag -> Mutable

Registry -> Layer Store

Kubernetes -> Pull Missing Layers
```

---

## Mental Models Table

| Concept | Mental Model |
|----------|-------------|
| Layer | Lego Block |
| Image | Stack of Lego Blocks |
| UnionFS | Transparent Stack |
| Registry | Warehouse |
| Digest | Fingerprint |
| Tag | Nickname |
| Writable Layer | Scratch Pad |

---

## Key Takeaways

1. UnionFS enables layered images.
2. Layers are immutable.
3. Containers add writable layers.
4. Registries store layers efficiently.
5. Digests are immutable.
6. Tags are mutable.
7. Build cache accelerates CI.
8. Multi-stage builds reduce size.
9. Kubernetes benefits from cached layers.
10. Layering is fundamental to modern container platforms.
