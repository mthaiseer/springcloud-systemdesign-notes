# 006_UnionFS_And_Layered_Filesystems.md

# UnionFS And Layered Filesystems – Understanding First Edition With ASCII Diagrams

## Goal Of This Chapter

This chapter is written for backend engineers who want to understand:

- Why Docker images are layered
- Why containers are storage efficient
- Why Docker builds are fast
- Why Kubernetes image pulls are efficient
- Why Spring Boot images can be optimized dramatically
- Why Docker cache exists
- Why image layering is one of Docker's biggest innovations

The goal is NOT to learn filesystem implementation details.

The goal is to understand the ideas that make Docker practical.

---

# Mental Model

Imagine 100 Spring Boot services.

Without layers:

```text
Service A
+-------------+
| Ubuntu      |
| JDK         |
| App         |
+-------------+

Service B
+-------------+
| Ubuntu      |
| JDK         |
| App         |
+-------------+

Service C
+-------------+
| Ubuntu      |
| JDK         |
| App         |
+-------------+
```

Everything is duplicated.

With layers:

```text
Shared Ubuntu Layer
Shared JDK Layer
        |
        +------ App A
        |
        +------ App B
        |
        +------ App C
```

The idea:

```text
Store common files once.
Reuse them everywhere.
```

That is the core idea behind UnionFS.

---

# Why This Problem Exists

Before layered images, distributing applications was expensive.

Imagine:

```text
Ubuntu = 100 MB
JDK    = 300 MB
App    = 50 MB
```

One service:

```text
450 MB
```

100 services:

```text
45 GB
```

Most of those files are identical.

The same:

```text
Ubuntu Files
Java Runtime
Certificates
Libraries
```

are copied repeatedly.

This wastes:

- Storage
- Network bandwidth
- CI/CD time
- Deployment time

Engineers needed a way to share common files.

---

# Real World Analogy

Think of an apartment building.

```text
Building
+--------------------------------+
| Foundation                     |
| Elevator                       |
| Roof                           |
| Hallways                       |
+--------------------------------+
```

Every apartment uses them.

Nobody builds:

```text
100 elevators
100 roofs
100 foundations
```

for 100 apartments.

Instead:

```text
Shared Structure
      +
Private Furniture
```

Docker images work similarly.

```text
Shared Layers
      +
Application Layer
```

---

# What Is UnionFS?

UnionFS means:

```text
Take Multiple Layers
        |
        v
Present Them As One Filesystem
```

Example:

```text
Layer 1
Ubuntu

Layer 2
Java

Layer 3
Application
```

Application sees:

```text
/
├── bin
├── etc
├── lib
├── usr
└── app.jar
```

It does NOT see:

```text
Layer 1
Layer 2
Layer 3
```

separately.

UnionFS merges them.

---

# Layer Mental Model

Each Docker instruction usually creates a layer.

Example:

```dockerfile
FROM ubuntu
```

creates:

```text
Layer 1
```

Then:

```dockerfile
RUN apt install openjdk
```

creates:

```text
Layer 2
```

Then:

```dockerfile
COPY app.jar app.jar
```

creates:

```text
Layer 3
```

Visual:

```text
+---------------------+
| Application Layer   |
+---------------------+
| OpenJDK Layer       |
+---------------------+
| Ubuntu Layer        |
+---------------------+
```

---

# Why Layers Are Powerful

Imagine 50 Spring Boot services.

All use:

```text
Ubuntu
Java 21
```

Only application code differs.

```text
Service A
Service B
Service C
```

Docker stores:

```text
Ubuntu Layer -> Once
Java Layer   -> Once
```

Only application layers vary.

Storage drops dramatically.

---

# Read Only Layers

Image layers are immutable.

Meaning:

```text
Cannot Change
```

Diagram:

```text
Ubuntu Layer
     |
Read Only
```

```text
Java Layer
     |
Read Only
```

Benefits:

- Safe sharing
- Faster caching
- Reliable builds

---

# Writable Container Layer

When a container starts:

Docker adds:

```text
Writable Layer
```

Visual:

```text
Container

+----------------------+
| Writable Layer       |
+----------------------+
| Application Layer    |
+----------------------+
| Java Layer           |
+----------------------+
| Ubuntu Layer         |
+----------------------+
```

Any write goes here.

Example:

```text
logs.txt
temp files
cache files
```

are stored only in writable layer.

---

# Why Containers Lose Data

Many beginners see:

```text
Container Restart
```

then:

```text
Data Missing
```

Why?

Because:

```text
Writable Layer
```
is temporary.

Visual:

```text
Container A

+---------------------+
| Writable Layer      |
| user-upload.txt     |
+---------------------+

Container Deleted
        |
Writable Layer Deleted
```

Lesson:

```text
Container storage is not persistence.
```

Use volumes.

---

# Docker Build Cache

One of the biggest productivity wins.

Imagine:

```dockerfile
FROM ubuntu
RUN install java
COPY app.jar
```

First build:

```text
Layer 1 Built
Layer 2 Built
Layer 3 Built
```

Now code changes.

Only:

```text
app.jar
```

changes.

Docker reuses:

```text
Layer 1
Layer 2
```

and rebuilds:

```text
Layer 3
```

Visual:

```text
Layer 1  CACHE HIT
Layer 2  CACHE HIT
Layer 3  REBUILD
```

Huge time savings.

---

# Spring Boot Example

Bad Dockerfile:

```dockerfile
COPY . .
RUN mvn package
```

Every change:

```text
Invalidates everything
```

Visual:

```text
Code Change
      |
Layer 1 Rebuild
Layer 2 Rebuild
Layer 3 Rebuild
Layer 4 Rebuild
```

Slow.

Better Dockerfile:

```dockerfile
COPY pom.xml .
RUN mvn dependency:resolve

COPY src ./src
RUN mvn package
```

Now dependencies stay cached.

Visual:

```text
Dependency Layer  CACHE HIT
Application Layer REBUILD
```

Fast.

---

# Docker Connection

Docker depends heavily on layered filesystems.

Without UnionFS:

```text
Every image contains everything.
```

With UnionFS:

```text
Shared Layers
      +
Small Differences
```

Diagram:

```text
Registry

Ubuntu Layer
     |
     +---- Service A
     |
     +---- Service B
     |
     +---- Service C
```

---

# Kubernetes Connection

Imagine a node running:

```text
Order Service
Payment Service
User Service
Gateway
```

All use:

```text
Java 21 Layer
```

Node downloads:

```text
Java Layer Once
```

Then reuses it.

Benefits:

```text
Faster Scaling
Lower Bandwidth
Faster Deployments
```

Visual:

```text
Kubernetes Node

+-------------------------+
| Shared Java Layer       |
+-------------------------+

Order Pod
Payment Pod
User Pod
Gateway Pod
```

---

# Production Failure Story #1

Problem:

```text
Docker Image = 4 GB
```

Deployment takes forever.

Investigation:

```text
Image contains:

Build Tools
Source Code
Logs
Temp Files
Unused Packages
```

Fix:

```text
Multi-stage Build
```

Result:

```text
4 GB -> 300 MB
```

---

# Production Failure Story #2

Problem:

```text
CI Pipeline Slow
```

Every build:

```text
10 Minutes
```

Cause:

```text
Cache Always Invalidated
```

Fix:

Reorder Dockerfile.

Result:

```text
10 min -> 1 min
```

---

# Production Failure Story #3

Problem:

```text
Kubernetes Node Disk Full
```

Cause:

```text
Too Many Large Images
```

Visual:

```text
Node

Image A  2 GB
Image B  2 GB
Image C  2 GB
```

Fix:

```text
Shared Base Images
Smaller Layers
Image Cleanup
```

---

# Debugging Mindset

When image is large:

Ask:

```text
What layer is large?
```

When deployment is slow:

Ask:

```text
Cache miss?
```

When node storage is full:

Ask:

```text
Too many images?
Too many layers?
```

Think in layers.

---

# Performance Tradeoffs

Benefits:

```text
Storage Savings
Fast Pulls
Fast Builds
Layer Reuse
```

Costs:

```text
Layer Complexity
Cache Management
Image Optimization Work
```

Table:

| Area | No Layers | Layers |
|--------|-----------|---------|
| Storage | High | Low |
| Pull Speed | Slow | Fast |
| Build Speed | Slow | Fast |
| Reuse | Poor | Excellent |

---

# Common Mistakes

Wrong:

```text
Every container has full OS copy
```

Correct:

```text
Containers share image layers
```

Wrong:

```text
Container storage is permanent
```

Correct:

```text
Writable layer is temporary
```

Wrong:

```text
Docker cache is magic
```

Correct:

```text
Docker cache works because layers are reused
```

Additional mistakes:

1. Huge base images
2. Using latest blindly
3. Storing secrets in layers
4. Ignoring image size
5. Copying unnecessary files
6. Not using multi-stage builds
7. Breaking cache frequently

---

# System Design Connection

```text
Microservice
      |
Docker Image
      |
Layers
      |
Registry
      |
Kubernetes
```

Layering enables:

```text
Fast CI/CD
Fast Scaling
Efficient Storage
Cloud Native Deployments
```

Without layered filesystems:

Cloud-native platforms would be much slower.

---

# Strong Interview Answers

Q: Why are Docker images layered?

Expected Answer:

Docker images are layered so common filesystem content can be shared and reused. This reduces storage, network transfer, and build time.

Common Wrong Answer:

Layers are only for organization.

---

Q: What is UnionFS?

Expected Answer:

UnionFS combines multiple filesystem layers and presents them as a single merged filesystem view.

Common Wrong Answer:

UnionFS is Docker storage.

---

Q: Why are Docker builds fast?

Expected Answer:

Docker reuses unchanged layers using build cache. Only changed layers are rebuilt.

---

# One Picture To Remember

```text
Container Filesystem

+--------------------------------+
| Writable Layer                 |
| Logs                           |
| Temp Files                     |
+--------------------------------+
| Spring Boot App Layer          |
+--------------------------------+
| Java Runtime Layer             |
+--------------------------------+
| Ubuntu Layer                   |
+--------------------------------+
               |
               v
         UnionFS Merge
               |
               v
      One Filesystem View

Benefits:

Storage Savings
Fast Builds
Fast Pulls
Fast Scaling
```
