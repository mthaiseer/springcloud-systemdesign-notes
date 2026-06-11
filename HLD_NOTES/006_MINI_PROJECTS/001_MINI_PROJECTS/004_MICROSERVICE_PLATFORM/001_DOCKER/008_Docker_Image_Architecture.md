# 008_Docker_Image_Architecture

## Why This Matters

Docker images are the foundation of containerized systems. Every container starts from an image. Understanding image architecture explains why Docker deployments are portable, reproducible, cacheable, and scalable.

A senior engineer should understand:

- What a Docker image really is
- How images are built
- How layers are organized
- How registries store images
- How Kubernetes pulls images
- How image caching works
- Why image size impacts production performance

---

## Mental Model

Think of a Docker image as a frozen snapshot of an application.

```text
Application
    +
Runtime
    +
Libraries
    +
Configuration
    =
Docker Image
```

Then:

```text
Docker Image
      |
docker run
      |
Container
```

Image = Blueprint

Container = Running Instance

---

## Core Concepts

### Image

An immutable package containing:

- Application code
- Runtime
- Libraries
- Configuration
- Metadata

### Layer

Each Dockerfile instruction creates a filesystem layer.

```text
Layer 1 OS
Layer 2 JRE
Layer 3 Dependencies
Layer 4 Application
```

### Manifest

Metadata describing image layers.

### Digest

Immutable SHA256 content hash.

### Tag

Human-readable reference.

```text
v1
v2
latest
```

Tags are mutable.

Digests are immutable.

---

## Internal Architecture

```text
Docker Image
      |
+--------------------+
| Manifest           |
+--------------------+
| Config JSON        |
+--------------------+
| Layer 4 App        |
+--------------------+
| Layer 3 Libraries  |
+--------------------+
| Layer 2 JRE        |
+--------------------+
| Layer 1 Base OS    |
+--------------------+
```

---

## Step-by-Step Flow

1. Developer creates Dockerfile.
2. Docker build begins.
3. Every instruction creates a layer.
4. Layers stored locally.
5. Manifest generated.
6. Image tagged.
7. Image pushed to registry.
8. Kubernetes pulls image.
9. Container starts.

---

## Deep Walkthrough Example

Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY app.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

Build:

```text
FROM      -> Layer A
WORKDIR   -> Layer B
COPY      -> Layer C
ENTRYPOINT-> Metadata
```

Final image:

```text
Layer A
Layer B
Layer C
Manifest
Config
```

---

## Layer-by-Layer Example

Version 1:

```dockerfile
FROM eclipse-temurin:21-jre
COPY app.jar app.jar
```

Layers:

```text
Base Layer
App Layer
```

Version 2:

```dockerfile
FROM eclipse-temurin:21-jre
COPY app-v2.jar app.jar
```

Docker reuses:

```text
Base Layer
```

Rebuilds:

```text
App Layer
```

Storage saved.

---

## Data Structures Used

```java
class Layer {

    String id;

    String digest;

    long size;
}
```

```java
class Image {

    String imageId;

    List<Layer> layers;
}
```

```java
class Manifest {

    String imageDigest;

    List<String> layerDigests;
}
```

---

## Algorithms Used

### Layer Resolution

```text
File Request
      |
Top Layer Search
      |
Parent Layer Search
```

### Build Cache Matching

```text
Instruction Hash
      |
Compare Previous Build
      |
Cache Hit
or
Cache Miss
```

### Registry Deduplication

```text
Digest Exists?
      |
Yes -> Skip Upload
No  -> Upload Layer
```

---

## Production Implementation

```text
Developer
    |
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
Container Startup
```

---

## Java Code Examples

```java
public class DockerImage {

    private String digest;

    private String tag;

    public DockerImage(
            String digest,
            String tag) {

        this.digest = digest;
        this.tag = tag;
    }
}
```

Dry Run:

```text
Create Layers
Generate Digest
Create Manifest
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

Dockerfile

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
Embedded Tomcat
     |
Ready
```

---

## Dockerfile Example

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/app.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

Image Composition:

```text
Base Layer
Metadata Layer
Application Layer
```

---

## Multi-Stage Build Example

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build

COPY . .

RUN mvn clean package

FROM eclipse-temurin:21-jre

COPY --from=build target/app.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

Benefits:

```text
Smaller Image
Faster Pull
Better Security
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

RUN mvn package
```

Good ordering preserves cache.

Bad ordering invalidates cache frequently.

---

## Registry Internals

Push Flow:

```text
Layer A
Layer B
Layer C
      |
Registry
```

Registry checks digest.

If layer exists:

```text
No Upload
```

Digest:

```text
sha256:abc123
```

Immutable.

Tag:

```text
latest
v1
v2
```

Mutable.

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

Image Pull:

```text
Scheduler
     |
Node Selected
     |
Check Local Cache
     |
Pull Missing Layers
     |
Start Pod
```

imagePullPolicy:

```text
Always
IfNotPresent
Never
```

---

## Sequence Diagram (ASCII)

```text
Developer
    |
docker build
    |
Image Layers
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
Ingress
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
Dockerfile
    |
Create Layers
    |
Create Manifest
    |
Build Image
```

### Run Dry Run

```text
Image
  |
Container
  |
JVM
  |
Application
```

### Failure Dry Run

```text
Missing Jar
     |
Container Exit
     |
CrashLoopBackOff
```

### Kubernetes Pull Dry Run

```text
Node
 |
Image Cache?
 |
No
 |
Pull Layers
 |
Create Container
```

---

## Failure Scenarios

### Large Image

```text
2GB Image
```

Problems:

- Slow pull
- Slow deployment

### Cache Miss

Rebuild everything.

### Disk Full

Cannot store layers.

### ImagePullBackOff

Cannot pull image.

### Wrong Tag

Tag not found.

### Missing Config

Startup failure.

### Permission Issue

Cannot access file.

---

## Failure Investigation Playbook

Commands:

```bash
docker history image
docker inspect image
docker images
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
docker image inspect image
docker history image
docker pull image
docker push image
```

---

## Performance Considerations

Best Practices:

- Small base image
- Multi-stage builds
- Layer reuse
- Dependency caching

Avoid:

- Fat images
- Unnecessary files

---

## Scalability Considerations

Benefits:

```text
Shared Layers
Registry Deduplication
Node Cache Reuse
```

Supports thousands of containers efficiently.

---

## Common Interview Questions

Q1 What is a Docker image?
A: Immutable package containing application and dependencies.

Q2 What is a layer?
A: Filesystem change stored separately.

Q3 Why layers?
A: Reuse and efficiency.

Q4 What is a manifest?
A: Metadata describing image.

Q5 What is digest?
A: Immutable SHA256 hash.

Q6 What is tag?
A: Mutable image reference.

Q7 Why immutable images?
A: Predictable deployments.

Q8 Registry stores what?
A: Layers and metadata.

Q9 Why image size matters?
A: Pull speed.

Q10 What is build cache?
A: Reuse of previous build layers.

Q11 Cache hit?
A: Reuse layer.

Q12 Cache miss?
A: Rebuild layer.

Q13 Multi-stage build?
A: Separate build/runtime images.

Q14 Why use digest?
A: Reproducibility.

Q15 Why use tag?
A: Human readability.

Q16 Kubernetes image pull?
A: Pull missing layers.

Q17 ImagePullBackOff?
A: Pull failure.

Q18 Registry deduplication?
A: Store unique layers only.

Q19 Node cache?
A: Reuse downloaded layers.

Q20 Explain image architecture.
A: Manifest + layers + config.

Q21 What creates layers?
A: Dockerfile instructions.

Q22 Can layers be shared?
A: Yes.

Q23 Are layers mutable?
A: No.

Q24 What is writable layer?
A: Container-specific layer.

Q25 Why small images?
A: Faster deployment.

Q26 Why build cache important?
A: Faster CI.

Q27 Registry optimization?
A: Layer reuse.

Q28 Docker vs container?
A: Image vs running instance.

Q29 OCI image?
A: Standardized image format.

Q30 Production image strategy?
A: Small, immutable, scanned.

---

## Strong Interview Answers

### Explain Docker Image Architecture

A Docker image consists of immutable filesystem layers, image metadata, manifests, and configuration. Layers are reused across images, reducing storage and network costs.

### Why Layers Matter

Layers allow caching, sharing, and efficient distribution.

### Digest vs Tag

Digest identifies content immutably. Tags are mutable pointers.

### Why Multi-Stage Builds

Smaller runtime images with fewer vulnerabilities.

### Kubernetes Startup Optimization

Node layer caching significantly reduces image pull time.

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

Shared:

```text
Ubuntu Layer
JRE Layer
```

Unique:

```text
Application Layer
```

Benefits:

- Faster deployment
- Reduced storage
- Faster rollback

---

## FAANG/System Design Discussion

Topics:

- Immutable infrastructure
- Build optimization
- Registry scalability
- CI/CD performance
- Deployment velocity

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
- Vulnerability scan
- Use digests
- Optimize cache
- Minimize layers
- Monitor registry

---

## One-Page Cheat Sheet

```text
Image
 =
Manifest
+ Config
+ Layers

Digest
 =
Immutable

Tag
 =
Mutable

Container
 =
Image + Writable Layer
```

---

## Last-Minute Interview Revision

```text
Layers -> Reuse

Digest -> Immutable

Tag -> Mutable

Registry -> Stores Layers

Build Cache -> Faster CI

Node Cache -> Faster Startup
```

---

## Mental Models Table

| Concept | Mental Model |
|----------|-------------|
| Image | Blueprint |
| Layer | Lego Block |
| Manifest | Table Of Contents |
| Digest | Fingerprint |
| Tag | Nickname |
| Registry | Warehouse |
| Container | Running Machine |

---

## Key Takeaways

1. Images are immutable.
2. Layers enable reuse.
3. Digests are immutable.
4. Tags are mutable.
5. Registries store layers efficiently.
6. Build cache accelerates CI.
7. Node cache accelerates Kubernetes.
8. Multi-stage builds reduce size.
9. Image size affects deployment speed.
10. Image architecture is fundamental to Docker.
