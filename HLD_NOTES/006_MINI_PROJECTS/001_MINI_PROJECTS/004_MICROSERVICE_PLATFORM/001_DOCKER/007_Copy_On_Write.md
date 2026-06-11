# 007_Copy_On_Write

# Title

## Why This Matters

Copy-On-Write (CoW) is one of the most important ideas behind Docker image efficiency. Without CoW, every container would need a complete copy of every file in every image layer. CoW allows Docker to share immutable layers while only copying files when modifications occur.

Understanding CoW explains:

- Why containers start quickly
- Why images remain small
- Why layer reuse works
- Why Docker storage is efficient
- Why Kubernetes can start pods quickly

---

## Mental Model

Think of a shared company handbook.

```text
Master Handbook
      |
Employee A
Employee B
Employee C
```

All employees read the same copy.

When Employee B edits a page:

```text
Master Handbook
      |
Copy Page
      |
Edit Personal Copy
```

Original remains unchanged.

This is Copy-On-Write.

---

## Core Concepts

### What Is Copy-On-Write

A storage optimization technique where data is copied only when modification happens.

Benefits:

- Reduced storage
- Faster startup
- Layer sharing

### Immutable Layers

Docker image layers are read-only.

```text
Layer 1
Layer 2
Layer 3
```

Cannot be modified directly.

### Writable Layer

Container startup creates:

```text
Writable Layer
```

All modifications go here.

### File Modification Flow

```text
Read Existing File
       |
Modify?
       |
Yes
       |
Copy To Writable Layer
       |
Apply Change
```

---

## Internal Architecture

```text
Container Filesystem
        |
+------------------+
| Writable Layer   |
+------------------+
| App Layer        |
+------------------+
| Dependency Layer |
+------------------+
| Runtime Layer    |
+------------------+
| OS Layer         |
+------------------+
```

---

## Step-by-Step Flow

1. Image contains immutable layers.
2. Container starts.
3. Writable layer created.
4. Application reads files.
5. No copy required for reads.
6. Application modifies file.
7. File copied to writable layer.
8. Modification applied.
9. Original layer unchanged.

---

## Deep Walkthrough Example

Image:

```text
Layer 1
  /etc/config.txt
```

Container A:

Reads file.

```text
No Copy
```

Container B:

Modifies file.

```text
Copy config.txt
      |
Writable Layer
      |
Modify
```

Result:

```text
Container A -> Original
Container B -> Modified
```

---

## Layer-by-Layer Example

Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre
COPY app.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
```

Layers:

```text
Layer A Base
Layer B Application
Layer C Metadata
```

Container:

```text
Writable Layer
Layer C
Layer B
Layer A
```

---

## Data Structures Used

```java
class Layer {
    String id;
    boolean readOnly;
}
```

```java
class WritableLayer {
    Map<String,String> modifiedFiles;
}
```

---

## Algorithms Used

### Read Path

```text
Search Writable Layer
      |
Found?
      |
Yes -> Return
      |
No
      |
Search Lower Layers
```

### Write Path

```text
File Change
    |
Copy File
    |
Store In Writable Layer
```

---

## Production Implementation

```text
Docker Image
      |
Multiple Containers
      |
Shared Layers
      |
Separate Writable Layers
```

Storage savings are significant.

---

## Java Code Examples

```java
import java.util.HashMap;
import java.util.Map;

public class CopyOnWriteDemo {

    public static void main(String[] args) {

        Map<String,String> base =
                new HashMap<>();

        base.put("config","v1");

        Map<String,String> writable =
                new HashMap<>(base);

        writable.put("config","v2");

        System.out.println(base);
        System.out.println(writable);
    }
}
```

Dry Run:

```text
Base Data
      |
Copy
      |
Modify Copy
```

---

## Spring Boot Example

application.yml

```yaml
server:
  port: 8080
```

Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre

COPY target/app.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

Container startup:

```text
Image Layers
      |
Writable Layer
      |
JVM
      |
Spring Boot
```

---

## Dockerfile Example

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY app.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
```

Each instruction contributes to image layers.

---

## Multi-Stage Build Example

```dockerfile
FROM maven:3.9 AS builder
COPY . .
RUN mvn package

FROM eclipse-temurin:21-jre
COPY --from=builder target/app.jar app.jar
```

Smaller final image.

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

---

## Registry Internals

Push:

```text
Layer A
Layer B
Layer C
```

Registry stores unique layers.

Digest:

```text
sha256:xxxxx
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

Pod startup:

```text
Node
  |
Check Local Layers
  |
Download Missing Layers
  |
Create Writable Layer
  |
Start Container
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
Container Start
      |
Create Writable Layer
      |
Read Files
      |
Modify File
      |
Copy On Write
```

---

## Request Lifecycle

```text
Client
  |
Container
  |
Spring Boot
  |
Read Config
  |
Writable Layer Lookup
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
Writable Layer
  |
Container
```

### Failure

```text
Disk Full
  |
Cannot Write
```

### Kubernetes

```text
Pull Layers
  |
Writable Layer
  |
Pod Start
```

---

## Failure Scenarios

- Disk Full
- Read-only file issue
- Large writable layer
- Cache invalidation
- Missing image layer
- Registry corruption

---

## Failure Investigation Playbook

```bash
docker inspect
docker history
docker system df
docker image ls
kubectl describe pod
```

---

## Debugging Guide

```bash
docker diff container
docker inspect container
docker history image
```

---

## Performance Considerations

Good:

- Shared layers
- Small images
- Layer reuse

Bad:

- Huge writable layer
- Large image size

---

## Scalability Considerations

```text
100 Containers
      |
Shared Base Layers
```

Minimal storage growth.

---

## Common Interview Questions

Q1 What is Copy-On-Write?
A: Copy only when modification occurs.

Q2 Why needed?
A: Storage efficiency.

Q3 Does Docker use CoW?
A: Yes.

Q4 Are image layers mutable?
A: No.

Q5 Where are modifications stored?
A: Writable layer.

Q6 Why containers small?
A: Shared layers.

Q7 Does read trigger copy?
A: No.

Q8 Does write trigger copy?
A: Yes.

Q9 What is writable layer?
A: Container-specific layer.

Q10 Can containers share layers?
A: Yes.

Q11 Registry stores layers?
A: Yes.

Q12 Digest meaning?
A: Immutable hash.

Q13 Tag meaning?
A: Mutable reference.

Q14 Why cache useful?
A: Faster builds.

Q15 Kubernetes benefit?
A: Faster pulls.

Q16 ImagePullBackOff?
A: Pull failure.

Q17 Large image impact?
A: Slow startup.

Q18 Multi-stage benefit?
A: Smaller image.

Q19 Layer reuse?
A: Storage optimization.

Q20 Explain CoW in one minute.
A: Shared data copied only when changed.

Q21-Q30 Senior discussions around storage drivers, build cache, immutable infrastructure, registry optimization, and Kubernetes startup performance.

---

## Strong Interview Answers

1. CoW allows Docker to share immutable image layers while isolating container modifications.

2. Containers remain lightweight because writes occur only in writable layers.

3. Kubernetes benefits because common layers are cached on nodes.

4. CoW reduces registry storage and network traffic.

5. Immutable images plus CoW create predictable deployments.

---

## Real World Production Case Study

50 Spring Boot services.

Shared:

```text
Ubuntu Layer
JRE Layer
```

Unique:

```text
Application Layer
```

Storage reduced dramatically.

---

## FAANG/System Design Discussion

Topics:

- Immutable infrastructure
- Layer reuse
- Startup optimization
- Registry scaling
- Deployment efficiency

---

## Production Checklist

- Use small images
- Enable cache reuse
- Use multi-stage builds
- Monitor disk usage
- Scan images

---

## One-Page Cheat Sheet

```text
CoW
 |
Copy Only On Modification

Image
 |
Immutable Layers

Container
 |
Writable Layer
```

---

## Last-Minute Interview Revision

```text
Read -> No Copy

Write -> Copy

Image -> Immutable

Container -> Writable Layer
```

---

## Mental Models Table

| Concept | Mental Model |
|----------|-------------|
| Image Layer | Shared Book |
| Writable Layer | Personal Notes |
| CoW | Photocopy Before Edit |
| Registry | Warehouse |
| Digest | Fingerprint |

---

## Key Takeaways

1. CoW improves storage efficiency.
2. Reads do not create copies.
3. Writes trigger copies.
4. Image layers are immutable.
5. Containers use writable layers.
6. Layer reuse reduces storage.
7. Kubernetes benefits from cached layers.
8. Multi-stage builds reduce image size.
9. Registries store shared layers.
10. CoW is fundamental to Docker.
