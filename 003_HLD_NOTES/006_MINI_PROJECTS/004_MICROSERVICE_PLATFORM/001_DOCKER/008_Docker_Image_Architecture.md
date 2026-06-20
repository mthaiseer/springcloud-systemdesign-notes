# 008_Docker_Image_Architecture.md

# Docker Image Architecture - Understanding First Edition With ASCII Diagrams

## Goal of This Chapter

This chapter is written for backend engineers, system design interviews, Docker, Kubernetes, Spring Boot, and cloud-native development.

The goal is NOT to memorize Docker commands.

The goal is to understand:

- What a Docker image really is
- Why image architecture matters
- Why images are layered
- Why image and container are different
- How Docker build, pull, push, and run work mentally
- How Spring Boot applications become images
- How Kubernetes uses images to run Pods
- Why image size, tags, layers, and registries matter in production

```text
Learning Goal
     |
     +--> Understand Docker image architecture
     +--> Understand image vs container
     +--> Understand image layers
     +--> Understand registry flow
     +--> Avoid production image mistakes
     +--> Explain Docker images in interviews
```

---

# Mental Model

A Docker image is a template.

A container is a running instance created from that template.

```text
Docker Image
     |
     | run
     v
Container
```

Think in Java terms:

```text
Java Class
     |
     | new
     v
Java Object
```

Docker equivalent:

```text
Docker Image
     |
     | docker run
     v
Container
```

The image itself is not running.

It is a packaged filesystem plus metadata.

```text
Docker Image
+----------------------------------+
| Application Files                |
| Runtime                          |
| Libraries                        |
| Environment Defaults             |
| Startup Command                  |
| Image Metadata                   |
+----------------------------------+
```

A container is what happens when Docker takes that image and starts a process from it.

```text
Image
  =
Read-only template

Container
  =
Running process + writable layer + namespaces + cgroups
```

---

# Why Docker Images Exist

Before Docker images, deployment was fragile.

A Spring Boot service needed:

```text
Java version
Linux packages
Certificates
Environment variables
Configuration
Application JAR
Startup script
```

On one server it worked.

On another server it failed.

Classic problem:

```text
Works On My Machine
```

Why?

Because machines were not identical.

```text
Developer Laptop
+----------------------------------+
| Java 21                          |
| Maven 3.9                        |
| libssl version A                 |
| app.jar                          |
+----------------------------------+

Production Server
+----------------------------------+
| Java 17                          |
| Maven missing                    |
| libssl version B                 |
| app.jar                          |
+----------------------------------+
```

Docker images solve this by packaging the runtime environment with the app.

```text
Docker Image
+----------------------------------+
| app.jar                          |
| Java Runtime                     |
| Required Linux Files             |
| Default Startup Command          |
+----------------------------------+
```

Now the deployment unit is no longer:

```text
JAR + Hope Server Is Correct
```

It becomes:

```text
Image = App + Runtime + Filesystem + Metadata
```

---

# Real World Analogy

Think of a food delivery kitchen.

Bad model:

```text
Customer orders biryani.

Chef says:
"Please bring rice, spices, chicken, stove, oil, and plate."
```

Every customer must prepare the environment.

Better model:

```text
Restaurant package
+--------------------------+
| Cooked biryani           |
| Packed box               |
| Spoon                    |
| Label                    |
+--------------------------+
```

Docker image is like the packed box.

It contains what is needed to run the application consistently.

Another analogy:

```text
Recipe
   !=
Cake
```

Recipe:

```text
Image
```

Cake:

```text
Container
```

One recipe can create many cakes.

```text
One Docker Image
        |
        +--> Container 1
        +--> Container 2
        +--> Container 3
```

---

# Image vs Container

This is the first major concept.

```text
Image
  =
Stored package

Container
  =
Running instance
```

Visual:

```text
Docker Image: order-service:1.0
+----------------------------------+
| app.jar                          |
| Java Runtime                     |
| Linux filesystem                 |
| ENTRYPOINT metadata              |
+----------------------------------+
        |
        | docker run
        v

Container
+----------------------------------+
| Running Java Process             |
| Writable Layer                   |
| Network Namespace                |
| PID Namespace                    |
| Memory/CPU Cgroup                |
+----------------------------------+
```

One image can create many containers.

```text
order-service:1.0 image
        |
        +--> order-container-A
        +--> order-container-B
        +--> order-container-C
```

Each container has its own runtime state.

```text
Same image
Different running instances
```

Interview version:

> A Docker image is a read-only layered template. A container is a running instance of that image with an added writable layer and runtime isolation.

---

# What Is Inside A Docker Image?

A Docker image typically contains:

```text
1. Filesystem layers
2. Runtime dependencies
3. Application files
4. Metadata
5. Entrypoint / command
6. Environment defaults
7. Exposed port metadata
8. Labels
```

Visual:

```text
Docker Image
+----------------------------------+
| Metadata                         |
| - env                            |
| - entrypoint                     |
| - command                        |
| - labels                         |
+----------------------------------+
| Application Layer                |
| - app.jar                        |
| - config templates               |
+----------------------------------+
| Runtime Layer                    |
| - Java runtime                   |
| - certificates                   |
+----------------------------------+
| Base Layer                       |
| - Linux filesystem files         |
+----------------------------------+
```

Important:

The image is not a full VM.

It does not contain a running kernel.

It uses the host Linux kernel when started as a container.

```text
Docker Image
     |
Container
     |
Host Linux Kernel
```

---

# Docker Image Is Not A Virtual Machine

A VM image often includes:

```text
Full guest OS
Guest kernel
System services
Virtual disk
```

Docker image contains:

```text
Application filesystem
Runtime
Dependencies
Metadata
```

It does not boot a separate kernel.

Visual comparison:

```text
Virtual Machine

+------------------------------+
| Application                  |
+------------------------------+
| Guest OS                     |
+------------------------------+
| Guest Kernel                 |
+------------------------------+
| Virtual Hardware             |
+------------------------------+

Docker Container

+------------------------------+
| Application                  |
+------------------------------+
| Image Filesystem             |
+------------------------------+
| Host Linux Kernel            |
+------------------------------+
```

This is why containers are lightweight.

---

# Layered Image Architecture

Docker images are built in layers.

Example Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/order-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Conceptual layers:

```text
+----------------------------------+
| ENTRYPOINT metadata              |
+----------------------------------+
| Application JAR layer            |
+----------------------------------+
| WORKDIR metadata                 |
+----------------------------------+
| Java Runtime base layer          |
+----------------------------------+
```

More general picture:

```text
Image: order-service:1.0

+----------------------------------+
| App Layer                        |
| /app/app.jar                     |
+----------------------------------+
| Runtime Layer                    |
| Java 21                          |
+----------------------------------+
| Base OS Layer                    |
| Linux filesystem                 |
+----------------------------------+
```

Docker presents these layers as one filesystem when a container runs.

```text
Multiple Layers
       |
       v
Merged View
       |
       v
Container /
```

---

# Why Layers Matter

Imagine 100 Spring Boot services.

All use:

```text
Java 21 Runtime
Base Linux Files
CA Certificates
```

Only application JAR differs.

Without layers:

```text
Service A
+------------------+
| Linux            |
| Java             |
| app-A.jar        |
+------------------+

Service B
+------------------+
| Linux            |
| Java             |
| app-B.jar        |
+------------------+

Service C
+------------------+
| Linux            |
| Java             |
| app-C.jar        |
+------------------+
```

Huge duplication.

With layers:

```text
Shared Layers
+------------------+
| Linux            |
+------------------+
| Java             |
+------------------+
        |
        +--> app-A.jar layer
        +--> app-B.jar layer
        +--> app-C.jar layer
```

Benefit:

```text
Store common files once.
Reuse common files many times.
```

This affects:

- Build speed
- Registry storage
- Kubernetes pull speed
- CI/CD performance
- Node disk usage

---

# Image Manifest Mental Model

A Docker image is not just one big tarball.

It is closer to:

```text
Image Manifest
      |
      +--> Layer digest 1
      +--> Layer digest 2
      +--> Layer digest 3
      +--> Config metadata
```

Visual:

```text
order-service:1.0
+----------------------------------+
| Manifest                         |
|                                  |
| layers:                          |
|   sha256:aaa -> base layer       |
|   sha256:bbb -> java layer       |
|   sha256:ccc -> app layer        |
|                                  |
| config:                          |
|   entrypoint                     |
|   env                            |
|   working directory              |
+----------------------------------+
```

You do not need to memorize manifest internals.

Just understand:

```text
Tag points to image metadata.
Image metadata points to layers.
Layers store filesystem content.
```

---

# Image Tag Mental Model

A tag is a human-friendly name.

```text
order-service:1.0
order-service:1.1
order-service:latest
```

Mental model:

```text
Tag
 |
 v
Image Digest
 |
 v
Exact Image Content
```

Important:

```text
latest is not a version.
latest is just a tag name.
```

Production mistake:

```text
image: order-service:latest
```

This can make deployments unpredictable.

Better:

```text
image: order-service:1.7.3
```

Even stronger:

```text
image: order-service@sha256:<digest>
```

For your level, remember:

```text
Use meaningful immutable tags in production.
Avoid relying blindly on latest.
```

---

# Dockerfile To Image Flow

The Dockerfile is the recipe.

The image is the built package.

```text
Dockerfile
     |
     | docker build
     v
Docker Image
```

Detailed mental flow:

```text
Dockerfile
     |
Instruction 1 -> Layer / metadata
     |
Instruction 2 -> Layer / metadata
     |
Instruction 3 -> Layer / metadata
     |
Final Image
```

Visual:

```text
FROM eclipse-temurin
        |
        v
Base Runtime Layer

COPY app.jar
        |
        v
Application Layer

ENTRYPOINT
        |
        v
Startup Metadata
```

Important:

Not every Dockerfile instruction creates a filesystem layer in the same way, but for understanding, think:

```text
Instructions build image history.
Filesystem-changing instructions add layers.
```

---

# Docker Build Cache

Docker build cache is based on layer reuse.

Example:

```dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

First build:

```text
Base Layer        Built/Pulled
App Layer         Built
Entrypoint        Set
```

Second build after only app changes:

```text
Base Layer        Cache Hit
App Layer         Rebuilt
Entrypoint        Set/Reused
```

Visual:

```text
Build 1

+------------------+  built
| App Layer        |
+------------------+  pulled/reused
| Java Layer       |
+------------------+

Build 2

+------------------+  rebuilt
| App Layer        |
+------------------+  cache hit
| Java Layer       |
+------------------+
```

This is why Dockerfile order matters.

---

# Spring Boot Image Architecture

A Spring Boot image often contains:

```text
Base OS or distroless base
Java runtime
Application JAR
Startup command
```

Simple image:

```text
Spring Boot Image
+----------------------------------+
| /app/app.jar                     |
+----------------------------------+
| Java Runtime                     |
+----------------------------------+
| Base Linux Files                 |
+----------------------------------+
```

But Spring Boot fat JARs have internal structure too.

```text
app.jar
+----------------------------------+
| Application Classes              |
| Dependency JARs                  |
| Spring Boot Loader               |
| Resources                        |
+----------------------------------+
```

So you have layers at two levels:

```text
Docker Image Layers
        |
        +--> Spring Boot JAR Internals
```

Modern Spring Boot can create layered JARs so Docker cache can reuse dependencies.

Mental model:

```text
Dependencies change rarely.
Application code changes often.
```

Better layering:

```text
+----------------------------------+
| Application Classes              |
+----------------------------------+
| Application Resources            |
+----------------------------------+
| Snapshot Dependencies            |
+----------------------------------+
| Stable Dependencies              |
+----------------------------------+
| Java Runtime                     |
+----------------------------------+
```

This improves build speed and pull efficiency.

---

# Bad Spring Boot Dockerfile

Bad pattern:

```dockerfile
FROM maven:3.9-eclipse-temurin-21
WORKDIR /app
COPY . .
RUN mvn package
CMD ["java", "-jar", "target/app.jar"]
```

Problems:

```text
Build tool included in runtime image
Source code copied into image
Large image
Poor cache behavior
More attack surface
```

Visual:

```text
Runtime Image
+----------------------------------+
| Source Code                      |
| Maven                            |
| Local Build Cache                |
| target/app.jar                   |
| Java                             |
| OS Packages                      |
+----------------------------------+
```

This image is heavy and messy.

---

# Better Spring Boot Image Architecture

Better idea:

```text
Build stage
     |
     v
Runtime stage
```

Visual:

```text
Build Stage
+----------------------------------+
| Maven                            |
| Source Code                      |
| Compile App                      |
+----------------------------------+
              |
              | copy app.jar only
              v
Runtime Stage
+----------------------------------+
| Java Runtime                     |
| app.jar                          |
+----------------------------------+
```

Result:

```text
Smaller image
Cleaner runtime
Less attack surface
Faster deployment
```

This connects to multi-stage builds, which will be deeper in the next chapters.

---

# Docker Registry Flow

A registry stores images.

Examples:

```text
Docker Hub
Amazon ECR
Google Artifact Registry
Azure Container Registry
Harbor
GitHub Container Registry
```

Mental model:

```text
Git stores source code.
Registry stores images.
```

Visual:

```text
Developer Machine
      |
      | build
      v
Docker Image
      |
      | push
      v
Registry
      |
      | pull
      v
Server / Kubernetes Node
```

In production:

```text
CI Pipeline
      |
Build Image
      |
Security Scan
      |
Push Registry
      |
Kubernetes Deploy
```

---

# Kubernetes Image Flow

Kubernetes does not run your source code.

Kubernetes runs containers.

Containers come from images.

```text
Deployment YAML
      |
image: order-service:1.7.3
      |
Kubelet on Node
      |
Pull Image From Registry
      |
Create Container
      |
Start Pod
```

Visual:

```text
Kubernetes Cluster

Control Plane
      |
      v
Node
+----------------------------------+
| kubelet                          |
|      |                           |
|      v                           |
| Pull image                       |
|      |                           |
|      v                           |
| Start container                  |
+----------------------------------+
```

Important:

If the image cannot be pulled, Pod cannot start.

Common status:

```text
ImagePullBackOff
```

---

# ImagePullBackOff Mental Model

Problem:

```text
Pod pending / not starting
```

Possible causes:

```text
Wrong image name
Wrong tag
Registry authentication failure
Image does not exist
Network issue
Private registry permission issue
```

Visual:

```text
Kubernetes Node
      |
      | pull order-service:2.0
      v
Registry
      |
      | image not found
      v
ImagePullBackOff
```

This is one of the most common Kubernetes deployment failures.

---

# Production Failure Story 1: Wrong Tag Deployed

Problem:

```text
Bug fixed locally.
Deployment still shows old behavior.
```

Investigation:

```text
Developer built v1.8
Kubernetes running v1.7
```

Visual:

```text
Developer
+-------------------+
| order-service:1.8 |
+-------------------+

Kubernetes
+-------------------+
| order-service:1.7 |
+-------------------+
```

Root cause:

```text
Wrong image tag in deployment.
```

Fix:

```text
Update deployment image tag.
Use CI/CD-generated immutable tags.
```

Lesson:

```text
Always know exactly which image version is running.
```

---

# Production Failure Story 2: latest Tag Confusion

Problem:

```text
Two environments both use latest.
One behaves differently.
```

Why?

```text
latest in dev  -> digest A
latest in prod -> digest B
```

Visual:

```text
latest
  |
  +--> Yesterday: sha256:aaa
  |
  +--> Today:     sha256:bbb
```

Same tag can point to different content over time.

Fix:

```text
Use versioned tags:
order-service:1.8.4
order-service:2026-06-12-commitabc
```

Lesson:

```text
latest is convenient, not production-safe.
```

---

# Production Failure Story 3: Huge Image Slow Deployment

Problem:

```text
Deployment takes 10 minutes.
```

Architecture:

```text
Image = 3.5 GB
10 Pods need to start
Each Node pulls image
```

Visual:

```text
Registry
   |
   | 3.5 GB image
   v
Node A  slow
Node B  slow
Node C  slow
```

Root causes:

```text
Build tools included
Source files included
Large dependencies
No multi-stage build
Huge base image
```

Fix:

```text
Use smaller runtime base
Use multi-stage builds
Clean unnecessary files
Use layered Spring Boot jars
```

Lesson:

```text
Image size directly affects deployment speed.
```

---

# Production Failure Story 4: Secrets Inside Image

Bad practice:

```dockerfile
ENV DB_PASSWORD=mysecret
COPY private-key.pem /app/private-key.pem
```

Problem:

Secrets become part of image history/layers.

Visual:

```text
Image Layer
+----------------------------------+
| DB_PASSWORD                      |
| private-key.pem                  |
+----------------------------------+
```

Even if later removed, old layers may still contain them.

Fix:

```text
Use Kubernetes Secrets
Use external secret managers
Inject at runtime
Never bake secrets into images
```

Lesson:

```text
Images should be portable artifacts, not secret vaults.
```

---

# Production Failure Story 5: Node Disk Full

Problem:

```text
Kubernetes node disk pressure.
Pods evicted.
```

Cause:

```text
Too many image versions stored on node.
Large layers.
Old unused images.
```

Visual:

```text
Node Disk
+----------------------------------+
| order-service:v1                 |
| order-service:v2                 |
| order-service:v3                 |
| payment-service:v1               |
| payment-service:v2               |
| gateway:v1                       |
| huge old images                  |
+----------------------------------+
```

Fix:

```text
Image garbage collection
Smaller images
Better tag cleanup
Registry retention policy
```

Lesson:

```text
Image architecture affects node reliability.
```

---

# Debugging Mindset

When a container does not behave as expected, ask:

```text
Which image is running?
Which tag?
Which digest?
Which registry?
Which Dockerfile produced it?
Which layers changed?
Was the image pulled or cached?
```

Mental investigation flow:

```text
Bug in Pod
    |
    +--> Is correct image tag deployed?
    |
    +--> Is node using cached image?
    |
    +--> Did CI push image successfully?
    |
    +--> Did deployment reference correct registry?
    |
    +--> Are runtime configs injected correctly?
```

Avoid thinking only in source code.

In Docker/Kubernetes systems, production runs images, not your IDE project.

---

# Performance Tradeoffs

Docker images provide consistency and portability, but image architecture affects performance.

| Area | Good Image Architecture | Bad Image Architecture |
|---|---|---|
| Build speed | Fast cache reuse | Slow rebuilds |
| Pull speed | Small layers | Huge downloads |
| Startup | Faster | Slower |
| Security | Smaller attack surface | Extra tools and secrets |
| Storage | Efficient | Node disk pressure |
| Rollback | Predictable tags | Tag confusion |

Visual:

```text
Good Image
+-----------------------------+
| Small App Layer             |
+-----------------------------+
| Shared Runtime Layer        |
+-----------------------------+

Bad Image
+-----------------------------+
| Source Code                 |
| Build Tools                 |
| Dependencies                |
| Runtime                     |
| Temp Files                  |
| Logs                        |
+-----------------------------+
```

---

# Common Mistakes

## Mistake 1: Image = Container

Wrong:

```text
Image is running.
```

Correct:

```text
Image is template.
Container is running instance.
```

## Mistake 2: latest Means Latest Everywhere

Wrong:

```text
latest always means newest correct version.
```

Correct:

```text
latest is just a mutable tag.
```

## Mistake 3: Put Secrets In Image

Wrong:

```text
Bake password into Dockerfile.
```

Correct:

```text
Inject secrets at runtime.
```

## Mistake 4: Huge Runtime Image Is Fine

Wrong:

```text
If it works, size does not matter.
```

Correct:

```text
Large images slow CI/CD, scaling, and recovery.
```

## Mistake 5: Ignore Layer Order

Wrong:

```text
COPY everything first.
```

Correct:

```text
Put stable dependencies before frequently changing app code.
```

Additional mistakes:

```text
6. Building inside final runtime image
7. Using random tags without traceability
8. Not scanning images
9. Not cleaning old images
10. Not understanding ImagePullBackOff
11. Confusing registry with runtime
12. Thinking Kubernetes builds images
```

---

# System Design Connection

Docker image architecture appears in many system design discussions.

```text
Developer
   |
Source Code
   |
CI/CD Pipeline
   |
Docker Image
   |
Registry
   |
Kubernetes Cluster
   |
Pods
   |
Users
```

Images influence:

```text
Deployment speed
Rollback strategy
Security posture
Scalability
Node storage
CI/CD reliability
Disaster recovery
```

In system design interviews, you may say:

> We package each microservice as a versioned Docker image, push it to a registry, and let Kubernetes pull that image to run Pods. Image layers allow runtime dependencies to be reused across services, reducing pull time and storage overhead.

That is a strong backend/cloud-native answer.

---

# Strong Interview Answers

## Q1: What is a Docker image?

Expected answer:

A Docker image is a read-only layered template that contains application files, runtime dependencies, filesystem content, and metadata required to create a container.

Common wrong answer:

A Docker image is a running application.

---

## Q2: Difference between image and container?

Expected answer:

An image is a stored template. A container is a running instance created from that image, with runtime isolation and a writable layer.

Common wrong answer:

They are basically the same thing.

---

## Q3: Why are Docker images layered?

Expected answer:

Layering allows shared filesystem content to be reused across images, improving storage efficiency, build speed, registry transfer, and Kubernetes pull performance.

Common wrong answer:

Layers are only for readability.

---

## Q4: Why avoid latest in production?

Expected answer:

`latest` is mutable and can point to different image contents over time. Production should use versioned or immutable tags for traceability and rollback safety.

Common wrong answer:

latest always means the newest stable version.

---

## Q5: How does Kubernetes use Docker images?

Expected answer:

Kubernetes Pod specs reference container images. The kubelet pulls the image from a registry onto the node and starts containers from that image.

Common wrong answer:

Kubernetes deploys source code directly.

---

# Production Case Study: Order Service Deployment

Architecture:

```text
Developer
   |
Git Commit
   |
CI Pipeline
   |
Build Docker Image
   |
Push Registry
   |
Kubernetes Deployment
   |
Order Service Pods
```

Initial state:

```text
order-service:latest
```

Problem:

A bug is fixed and merged, but production still behaves incorrectly.

Investigation:

```text
Step 1: Check deployed image tag
Step 2: Check registry digest
Step 3: Compare CI build output
Step 4: Check deployment rollout history
```

Findings:

```text
CI built: order-service:2026-06-12-a1b2c3
Kubernetes still running: order-service:latest
latest points to old digest on one node
```

Failure diagram:

```text
Registry
+----------------------------------+
| latest -> sha256:old             |
| fixed  -> sha256:new             |
+----------------------------------+

Kubernetes Node
+----------------------------------+
| Running old cached image         |
+----------------------------------+
```

Fix:

```text
Use immutable tags
Update deployment image explicitly
Enable rollout tracking
Avoid latest in production
```

Lessons learned:

```text
Images are deployment artifacts.
Tags must be traceable.
Registries are part of production architecture.
Kubernetes runs images, not source code.
```

---

# Final Cheat Sheet

```text
Docker Image
  =
Read-only layered template
```

```text
Container
  =
Running instance of image
```

```text
Image Contains
  =
Filesystem
Runtime
Application
Metadata
Entrypoint
```

```text
Image Does NOT Contain
  =
Separate Linux kernel
Running process state
Permanent container writes
```

```text
Layer
  =
Reusable filesystem snapshot
```

```text
Tag
  =
Human-readable pointer to image
```

```text
Digest
  =
Content-addressed exact image identity
```

```text
Registry
  =
Storage system for images
```

```text
Kubernetes
  =
Pulls image from registry and runs container in Pod
```

Most important takeaway:

```text
Docker image is the portable artifact.
Container is the running process created from it.
```

---

# One Picture To Remember

```text
Source Code
    |
    v
CI/CD Pipeline
    |
    v
Docker Build
    |
    v
Docker Image
+------------------------------------------+
| Metadata: entrypoint, env, labels        |
+------------------------------------------+
| Spring Boot Application Layer            |
| /app/app.jar                             |
+------------------------------------------+
| Java Runtime Layer                       |
+------------------------------------------+
| Base Linux Filesystem Layer              |
+------------------------------------------+
    |
    | push
    v
Registry
    |
    | pull
    v
Kubernetes Node
    |
    v
Container
+------------------------------------------+
| Running Java Process                     |
| Writable Layer                           |
| Namespaces                               |
| Cgroups                                  |
+------------------------------------------+

Image  = portable template
Container = running instance
Registry = image storage
Kubernetes = image runner at scale
```

That picture is the core of Docker image architecture.
