# 003_Container_Mental_Model

# Container Mental Model

## Why This Matters

Most engineers learn Docker commands before understanding what a container actually is.

This leads to confusion:

- Is a container a VM?
- Is a container a process?
- Where is the operating system?
- How does Kubernetes run containers?
- Why do containers start in milliseconds?

A senior engineer must understand the container mental model because nearly every modern production platform relies on containers.

A container is NOT a mini virtual machine.

A container is an isolated process running on the host operating system.

---

## Mental Model

### Mental Model #1

Container = Process + Isolation + Resource Limits

```text
Normal Process

Java Process
    |
Linux Kernel

Container

Java Process
    |
Namespaces
    |
CGroups
    |
Linux Kernel
```

The application remains a normal Linux process.

The difference is isolation.

---

### Mental Model #2

Image = Class

Container = Object

```java
class User {
}
```

Object:

```java
User user = new User();
```

Docker:

```text
Image
  |
Container
```

One image can create many containers.

---

### Mental Model #3

Container = Private Apartment

```text
Building
 |
Host OS
 |
+ Apartment A
+ Apartment B
+ Apartment C
```

Everyone shares:

- Water
- Electricity
- Building structure

But tenants are isolated.

Equivalent:

```text
Host Kernel
Shared

Namespaces
Isolation
```

---

## Core Concepts

### Process

A container starts with a process.

Example:

```bash
java -jar app.jar
```

Internally:

```text
PID
Memory
Threads
Files
Sockets
```

Why needed:

Applications run as processes.

Advantages:

Simple.

Disadvantages:

No isolation by default.

Interview Explanation:

Container is ultimately a process.

---

### Namespace

Namespaces provide isolation.

Types:

```text
PID Namespace
Network Namespace
Mount Namespace
UTS Namespace
IPC Namespace
User Namespace
```

Why needed:

Containers should not see each other's resources.

Advantages:

Strong logical separation.

Disadvantages:

Not full hardware isolation.

Interview Explanation:

Namespaces make one process believe it owns the machine.

---

### CGroups

Control Groups.

Used for:

```text
CPU
Memory
Disk IO
Network
```

Why needed:

Prevent resource abuse.

Advantages:

Predictable performance.

Disadvantages:

Wrong limits can cause throttling.

Interview Explanation:

Cgroups enforce resource budgets.

---

### Filesystem Layers

Images use layered filesystems.

```text
Layer 1
Layer 2
Layer 3
```

Why needed:

Reuse storage.

Advantages:

Smaller images.

Disadvantages:

Too many layers hurt build efficiency.

Interview Explanation:

Layers enable caching and sharing.

---

## Internal Architecture

```text
Application
     |
Container Process
     |
Namespaces
     |
CGroups
     |
Union Filesystem
     |
Container Runtime
     |
Linux Kernel
     |
Hardware
```

Components:

1. Process
2. Namespace
3. CGroup
4. Filesystem
5. Runtime
6. Kernel

---

## Step-by-Step Flow

Container Startup

```text
docker run image
        |
Load Image Layers
        |
Create Namespace
        |
Create CGroup
        |
Mount Filesystem
        |
Start Process
        |
Container Running
```

---

## Data Structures Used

### Container Metadata

```java
class ContainerMetadata {

    String id;

    String imageId;

    String status;

    long memoryLimit;

    int cpuQuota;
}
```

### Namespace Metadata

```java
class Namespace {

    String namespaceId;

    String type;
}
```

### Layer Metadata

```java
class Layer {

    String id;

    String parentId;
}
```

---

## Algorithms Used

### Copy On Write

```text
Read Layer
Read Layer
Read Layer
Writable Layer
```

When modifying a file:

```text
Copy
Modify
Store
```

Advantages:

- Efficient
- Fast

---

### Layer Cache Reuse

```text
Build Step 1
Build Step 2
Build Step 3
```

Only changed layers rebuilt.

---

## Production Implementation

Typical deployment:

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
 |
Pods
 |
Containers
```

Container lifecycle:

```text
Created
Running
Stopped
Removed
```

---

## Java Code Examples

### Example 1

Container Main Process

```java
public class ContainerApp {

    public static void main(String[] args)
            throws Exception {

        while(true) {

            System.out.println(
                "Container Alive"
            );

            Thread.sleep(5000);
        }
    }
}
```

Dry Run:

```text
Start JVM
Start Main Thread
Loop Forever
Container Stays Alive
```

If main process exits:

```text
Container Stops
```

---

### Example 2

Memory Consumption

```java
public class MemoryDemo {

    public static void main(String[] args) {

        byte[] data =
            new byte[50 * 1024 * 1024];

        System.out.println(
            data.length
        );
    }
}
```

Useful for observing cgroup limits.

---

## Spring Boot Example

```java
@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {

        return "UP";
    }
}
```

Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre

COPY app.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

Flow:

```text
Container
   |
JVM
   |
Spring Boot
   |
Embedded Tomcat
```

---

## Spring Cloud Example

```text
Gateway
User Service
Order Service
Payment Service
```

Each service:

```text
Independent Container
```

Benefits:

- Isolation
- Independent deployment
- Horizontal scaling

---

## Kubernetes Example

Pod Mental Model

```text
Pod
 |
+ Container A
+ Container B
```

Containers share:

```text
Network
Storage
```

But remain separate processes.

---

## Sequence Diagram (ASCII)

```text
Developer
    |
docker run
    |
Docker Runtime
    |
Create Namespace
    |
Create CGroup
    |
Start Process
    |
Application Running
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
   |
Response
```

---

## Failure Scenarios

### Main Process Exits

```text
Process Ends
Container Ends
```

### OOM Kill

```text
Memory Limit Reached
```

Kernel terminates process.

### Disk Full

```text
Writable Layer Full
```

Container may fail.

### CPU Throttling

```text
Exceeded CPU Quota
```

Performance degradation.

---

## Debugging Guide

List Containers

```bash
docker ps
```

Logs

```bash
docker logs id
```

Shell

```bash
docker exec -it id bash
```

Inspect

```bash
docker inspect id
```

Resource Usage

```bash
docker stats
```

---

## Performance Considerations

Good:

```text
Small Images
Few Layers
Multi-stage Build
```

Bad:

```text
Huge Images
Unnecessary Dependencies
```

Monitor:

```text
CPU
Memory
Disk
Network
```

---

## Scalability Considerations

Containers scale horizontally.

```text
1 Container
    |
10 Containers
    |
100 Containers
```

Advantages:

- Fast startup
- Small footprint

---

## CAP Tradeoffs

Containers themselves do not participate in CAP.

Distributed systems built using containers do.

Example:

```text
Container
   |
Redis Cluster
```

CAP belongs to Redis cluster, not container.

---

## Common Interview Questions

### Q1 What is a container?
A: An isolated process running on the host kernel.

### Q2 Is container a VM?
A: No.

### Q3 What is namespace?
A: Isolation mechanism.

### Q4 What is cgroup?
A: Resource control mechanism.

### Q5 Why containers lightweight?
A: Shared kernel.

### Q6 What is PID namespace?
A: Process isolation.

### Q7 What is network namespace?
A: Network isolation.

### Q8 Why cgroups needed?
A: Prevent resource abuse.

### Q9 What is image?
A: Read-only template.

### Q10 What is writable layer?
A: Container-specific layer.

### Q11 What is copy-on-write?
A: Deferred file duplication.

### Q12 Why containers start fast?
A: No guest OS boot.

### Q13 What happens when PID 1 exits?
A: Container stops.

### Q14 Why layers?
A: Reuse and caching.

### Q15 How many containers from one image?
A: Many.

### Q16 What is runtime?
A: Software that launches containers.

### Q17 Docker vs Kubernetes?
A: Runtime vs orchestration.

### Q18 What causes OOM kill?
A: Memory limit exceeded.

### Q19 Why immutable images?
A: Consistent deployments.

### Q20 Explain container in one minute.
A: Process + namespaces + cgroups + layered filesystem.

---

## Strong Interview Answers

### What Is A Container?

A container is a standard Linux process enhanced with namespaces for isolation and cgroups for resource control. Containers share the host kernel while maintaining isolated views of processes, networking, filesystems, and resources.

### Why Containers Over VMs?

Containers avoid running a full guest operating system, reducing startup time, memory usage, and operational complexity.

### Why Did Kubernetes Succeed?

Because containers are lightweight, portable, and easy to schedule at massive scale.

---

## Real World Example

E-Commerce Platform

```text
Gateway
User Service
Order Service
Payment Service
Inventory Service
```

Deployment:

```text
Gateway Container x 3
User Container x 5
Order Container x 5
Payment Container x 3
```

Independent scaling.

---

## FAANG/System Design Discussion

Interview topics:

```text
Container Lifecycle
Namespace Isolation
Resource Limits
Image Layers
Container Density
Kubernetes Pods
Autoscaling
```

Key point:

Containers make microservices economically scalable.

---

## Production Checklist

Build

- Small image
- Multi-stage build
- Dependency scan

Runtime

- Resource limits
- Health checks
- Logging

Operations

- Monitoring
- Metrics
- Tracing

Security

- Non-root user
- Read-only filesystem where possible

---

## Key Takeaways

1. Container is an isolated process.
2. Containers share the host kernel.
3. Namespaces provide isolation.
4. Cgroups provide resource limits.
5. Images are templates.
6. Containers are running instances.
7. Copy-on-write improves efficiency.
8. Containers start quickly.
9. Kubernetes schedules containers.
10. Understanding containers is foundational to cloud-native systems.

---

# One-Page Cheat Sheet

```text
Container
 =
Process
 +
Namespaces
 +
CGroups

Image
 =
Blueprint

Container
 =
Running Object

Namespace
 =
Isolation

CGroup
 =
Budget

Layer
 =
Reusable Filesystem

PID 1 Exit
 =
Container Exit
```

---

# Last Minute Interview Revision

```text
Container != VM

Container = Process

Namespaces = Isolation

CGroups = Limits

Image = Template

Container = Instance

Copy-On-Write = Efficient Storage

Docker = Runtime

Kubernetes = Orchestrator
```

---

# Mental Models Table

| Concept | Mental Model |
|----------|-------------|
| Image | Class |
| Container | Object |
| Namespace | Private Room |
| CGroup | Budget |
| Runtime | Factory |
| Kernel | Building |
| Pod | Shared Apartment |
| Layer | Reusable Lego Block |
