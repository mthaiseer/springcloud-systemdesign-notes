# 001_Why_Docker

# Why Docker

## Why This Matters

Before Docker, applications were deployed directly on physical servers or virtual machines. Teams frequently faced:

- "Works on my machine" problems
- Dependency conflicts
- Environment drift
- Slow deployments
- Inefficient resource utilization
- Difficult scaling

Docker solved these problems by packaging an application and its dependencies into a portable unit called a container.

In modern backend systems, Docker is one of the foundational technologies behind cloud-native platforms, microservices, CI/CD pipelines, Kubernetes, and large-scale distributed systems.

---

## Mental Model

Think of Docker as a standardized shipping container.

Before containers:

Application A requires Java 17.
Application B requires Java 11.
Application C requires Python 3.12.

All applications compete for the same host environment.

With Docker:

Each application carries everything it needs.

```text
+---------------------+
| Application         |
| Dependencies        |
| Runtime             |
| Libraries           |
+----------+----------+
           |
           v
      Docker Image
           |
           v
      Docker Container
```

The application becomes portable and predictable.

---

## Core Concepts

### Container

A running isolated process.

### Image

A read-only template used to create containers.

### Docker Engine

Runtime responsible for creating and managing containers.

### Dockerfile

Recipe used to build an image.

### Registry

Repository for storing images.

Examples:

- Docker Hub
- ECR
- GCR

---

## Internal Architecture

```text
+----------------------------------+
| Developer                        |
+----------------+-----------------+
                 |
                 v
         Docker CLI
                 |
                 v
         Docker Daemon
                 |
      +----------+----------+
      |                     |
      v                     v
 Container Runtime      Image Store
      |
      v
 Linux Kernel
      |
      +--> Namespaces
      +--> Cgroups
      +--> Filesystem
```

Components:

1. Docker Client
2. Docker Daemon
3. Container Runtime
4. Linux Kernel
5. Registry

---

## Step-by-Step Flow

### Build

```text
Dockerfile
    |
    v
docker build
    |
    v
Docker Image
```

### Run

```text
Docker Image
      |
      v
docker run
      |
      v
Container Process
```

### Deploy

```text
Build
   |
Push
   |
Registry
   |
Pull
   |
Run
```

---

## Data Structures Used

Internally Docker relies on:

### Layer Metadata

```java
class ImageLayer {
    String layerId;
    String parentLayer;
    long size;
}
```

### Container Metadata

```java
class ContainerInfo {
    String id;
    String image;
    String status;
}
```

### Network Tables

Linux networking structures maintain:

- Routes
- Bridges
- Virtual interfaces

---

## Algorithms Used

### Layer Reuse

Docker avoids rebuilding unchanged layers.

```text
Layer1
Layer2
Layer3
```

If Layer2 changes:

```text
Layer1 reused
Layer2 rebuilt
Layer3 rebuilt
```

### Copy-On-Write

Modified files are copied into a writable layer instead of rewriting the base image.

Complexity:

```text
Space efficient
Fast startup
Reduced duplication
```

---

## Production Implementation

Typical production architecture:

```text
Developer
    |
CI Pipeline
    |
Docker Build
    |
Image Registry
    |
Kubernetes Cluster
    |
Containers
```

Benefits:

- Repeatability
- Scalability
- Faster releases

---

## Java Code Examples

### Traditional Java Application

```java
public class Main {

    public static void main(String[] args) {

        System.out.println("Hello Docker");
    }
}
```

Compile:

```bash
javac Main.java
java Main
```

Problem:

Requires:

- JDK installed
- Correct version
- Correct environment

Docker eliminates these assumptions.

---

### Java Docker Example

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY app.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

Explanation:

FROM
- Base runtime

WORKDIR
- Working directory

COPY
- Copies artifact

ENTRYPOINT
- Startup command

---

## Spring Boot Example

```java
@RestController
public class UserController {

    @GetMapping("/users")
    public String users() {

        return "users";
    }
}
```

Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre

COPY target/app.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

Build:

```bash
docker build -t user-service .
```

Run:

```bash
docker run -p 8080:8080 user-service
```

Dry Run:

```text
Request
   |
localhost:8080
   |
Container
   |
Spring Boot
```

---

## Spring Cloud Example

Microservices:

```text
Gateway
User Service
Order Service
Payment Service
```

Containerized deployment:

```text
Gateway Container

User Container

Order Container

Payment Container
```

Advantages:

- Independent deployment
- Isolation
- Version control

---

## Kubernetes Example

Docker creates containers.

Kubernetes manages containers.

```text
Docker
   |
Creates Container
   |
Kubernetes
   |
Schedules Containers
```

Example:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  replicas: 3
```

---

## Sequence Diagram (ASCII)

```text
Developer
    |
docker build
    |
    v
Docker Engine
    |
Creates Image
    |
Push
    |
Registry
    |
Pull
    |
Container Host
    |
Run Container
    |
Application Starts
```

---

## Request Lifecycle

```text
Client
   |
HTTP Request
   |
Container Port
   |
Spring Boot App
   |
Controller
   |
Service
   |
Database
   |
Response
```

---

## Failure Scenarios

### Image Not Found

```text
docker run image
       |
       v
Image Missing
```

Fix:

```bash
docker pull image
```

### Port Conflict

```text
8080 already used
```

Fix:

```bash
docker run -p 9090:8080
```

### Out Of Memory

Container exceeds limit.

Fix:

```bash
docker run --memory=1g
```

### Container Crash Loop

Reasons:

- Missing config
- Startup exception
- DB unavailable

---

## Debugging Guide

### List Containers

```bash
docker ps
```

### View Logs

```bash
docker logs containerId
```

### Enter Container

```bash
docker exec -it containerId bash
```

### Inspect Container

```bash
docker inspect containerId
```

### Resource Usage

```bash
docker stats
```

---

## Performance Considerations

### Small Images

Good:

```dockerfile
eclipse-temurin:21-jre
```

Bad:

```dockerfile
full JDK + build tools
```

### Multi-stage Builds

Reduces image size.

### Layer Caching

Improves build speed.

### Container Limits

Prevents noisy neighbors.

---

## Scalability Considerations

Without Docker:

```text
Manual setup
Manual deployment
```

With Docker:

```text
Immutable deployment
Horizontal scaling
Automated rollout
```

Scaling:

```text
1 Container
     |
     v
10 Containers
     |
     v
100 Containers
```

---

## CAP Tradeoffs

Docker itself is not a distributed database.

However containerized systems participate in CAP discussions.

Example:

```text
Container
    |
Service Discovery
    |
Distributed System
```

Tradeoffs belong to the system built using Docker.

---

## Common Interview Questions

### Q1 What is Docker?
A: Platform for packaging applications and dependencies into containers.

### Q2 What is a container?
A: Isolated process running on the host kernel.

### Q3 Container vs VM?
A: Containers share kernel. VMs run full OS.

### Q4 Why is Docker faster than VMs?
A: No guest OS boot required.

### Q5 What is an image?
A: Read-only template for creating containers.

### Q6 What is Dockerfile?
A: Recipe used to build an image.

### Q7 What is Docker daemon?
A: Background process managing containers.

### Q8 What is Docker Hub?
A: Public image registry.

### Q9 What is a layer?
A: Incremental filesystem change.

### Q10 What is copy-on-write?
A: Writable layer created only when modifications occur.

### Q11 Why use multi-stage builds?
A: Smaller images.

### Q12 What are namespaces?
A: Linux isolation mechanism.

### Q13 What are cgroups?
A: Linux resource control mechanism.

### Q14 How does networking work?
A: Virtual bridges and interfaces.

### Q15 How are logs collected?
A: Stdout/stderr streams.

### Q16 Why use volumes?
A: Persistent storage.

### Q17 What happens when container dies?
A: Process exits and container stops.

### Q18 How does Kubernetes use Docker concepts?
A: Pods run containers created from images.

### Q19 Why immutable infrastructure?
A: Predictable deployments.

### Q20 Explain Docker architecture.
A: CLI -> Daemon -> Runtime -> Linux Kernel.

---

## Strong Interview Answers

### Explain Docker in One Minute

Docker packages application code, runtime, libraries, and dependencies into a portable image. The image can run consistently across environments because the container includes everything needed except the host kernel. Internally Docker relies on Linux namespaces for isolation and cgroups for resource management.

### Why Did Docker Become Popular?

Because deployment became predictable. Instead of documenting environment setup, engineers distribute a container image.

### Why Are Containers Lightweight?

Containers share the host kernel rather than booting separate operating systems.

---

## Real World Example

Imagine:

User Service

```text
Java 21
Spring Boot
PostgreSQL Driver
```

Order Service

```text
Java 17
Different Libraries
```

Without Docker:

Dependency conflicts.

With Docker:

Each service runs independently.

```text
User Container
Order Container
Payment Container
```

---

## FAANG/System Design Discussion

Large companies use containerization because:

- Faster deployments
- Standardized environments
- Better utilization
- Easy rollback

Typical architecture:

```text
Developer
   |
Git
   |
CI/CD
   |
Docker Build
   |
Registry
   |
Kubernetes
   |
Production
```

System design discussion points:

- Immutable deployments
- Blue-green deployment
- Canary release
- Autoscaling
- Container security

---

## Production Checklist

### Build

- Multi-stage build
- Minimal image
- No secrets in image

### Runtime

- Health checks
- Resource limits
- Non-root user

### Monitoring

- Logs
- Metrics
- Traces

### Security

- Vulnerability scanning
- Signed images
- Least privilege

---

## Key Takeaways

1. Docker solves environment consistency.
2. Containers are isolated processes.
3. Images are immutable templates.
4. Namespaces provide isolation.
5. Cgroups provide resource control.
6. Layers improve storage efficiency.
7. Docker enables reproducible deployments.
8. Docker is the foundation for Kubernetes.
9. Containerization is critical in modern microservices.
10. Understanding Docker internals is essential for production-grade backend engineering.

---

# One-Page Cheat Sheet

```text
Docker = Packaging + Isolation + Portability

Image
  -> Template

Container
  -> Running Process

Namespaces
  -> Isolation

CGroups
  -> Resource Limits

UnionFS
  -> Layered Filesystem

Volumes
  -> Persistence

Bridge Network
  -> Communication

Dockerfile
  -> Build Recipe

Registry
  -> Image Storage
```

---

# Last Minute Interview Revision

```text
VM = Full OS

Container = Shared Kernel

Docker Image = Blueprint

Docker Container = Running Instance

Namespaces = Isolation

CGroups = CPU/Memory Limits

Volumes = Persistent Data

Docker Compose = Multi-container Local Environment

Kubernetes = Container Orchestration
```

---

# Mental Models Table

| Concept | Mental Model |
|----------|-------------|
| Image | Class |
| Container | Object |
| Dockerfile | Source Code |
| Registry | Artifact Repository |
| Namespace | Private Room |
| CGroup | Resource Budget |
| Volume | External Hard Disk |
| Network | Virtual Switch |
| Compose | Local Cluster |
| Kubernetes | Data Center Manager |
