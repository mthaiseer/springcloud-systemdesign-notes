# 009_Dockerfile_Deep_Dive.md

# Dockerfile Deep Dive - Understanding First Edition With ASCII Diagrams

## Goal of This Chapter

This chapter is written for senior Java backend engineers, Spring Boot developers, system design interviews, Docker, Kubernetes, and production engineering.

The goal is NOT to memorize every Dockerfile instruction.

The goal is to understand:

- What a Dockerfile really represents
- How Dockerfile instructions become image layers
- Why Dockerfile order affects build speed
- Why bad Dockerfiles create huge images
- Why Spring Boot Dockerfiles need special care
- Why multi-stage builds exist
- How Dockerfiles connect to CI/CD and Kubernetes
- How to explain Dockerfile design in interviews

```text
Learning Goal
     |
     +--> Understand Dockerfile as image recipe
     +--> Understand build layers
     +--> Understand cache behavior
     +--> Understand Spring Boot image design
     +--> Avoid production Dockerfile mistakes
     +--> Explain Dockerfile decisions in interviews
```

---

# Mental Model

A Dockerfile is a recipe.

```text
Recipe
   |
Cook
   |
Meal
```

Docker equivalent:

```text
Dockerfile
     |
docker build
     |
Docker Image
     |
docker run
     |
Container
```

Important:

```text
Dockerfile
  =
Instructions

Docker Image
  =
Built artifact

Container
  =
Running instance
```

A Dockerfile is not the image.

A Dockerfile is not the container.

It is the repeatable build plan for creating the image.

```text
Dockerfile
+----------------------------------+
| FROM                             |
| WORKDIR                          |
| COPY                             |
| RUN                              |
| ENTRYPOINT                       |
+----------------------------------+
        |
        v
Docker Image
+----------------------------------+
| Application Layer                |
| Runtime Layer                    |
| Base Layer                       |
| Metadata                         |
+----------------------------------+
```

---

# Why Dockerfiles Exist

Before Dockerfiles, server setup was manual.

A deployment document might say:

```text
1. Install Ubuntu packages
2. Install Java 21
3. Copy app.jar
4. Set environment variables
5. Run java -jar app.jar
```

Problem:

```text
Developer A follows steps slightly differently.
Developer B misses one package.
Production has another Java version.
Staging has old certificates.
```

Result:

```text
Works on my machine.
Fails in production.
```

Dockerfile solves this by making image creation repeatable.

```text
Manual Setup
     |
     v
Inconsistent Servers

Dockerfile
     |
     v
Repeatable Image
```

Visual:

```text
Without Dockerfile

Developer Laptop       Staging Server       Production Server
+---------------+      +---------------+    +---------------+
| Java 21       |      | Java 17       |    | Java 21       |
| app.jar       |      | app.jar       |    | missing cert  |
+---------------+      +---------------+    +---------------+

With Dockerfile

Dockerfile
    |
    +--> Same Image
             |
             +--> Laptop
             +--> Staging
             +--> Production
```

---

# Real World Analogy

Think of a restaurant recipe.

Bad restaurant:

```text
Chef A adds more salt.
Chef B skips spices.
Chef C cooks for less time.
```

Every meal tastes different.

Good restaurant:

```text
Written recipe
Measured ingredients
Defined steps
Repeatable output
```

Dockerfile is the written recipe for your runtime environment.

```text
Recipe
  =
Dockerfile

Meal
  =
Docker Image

Served Dish
  =
Running Container
```

---

# Dockerfile Lifecycle

The full lifecycle:

```text
Developer writes Dockerfile
        |
        v
docker build
        |
        v
Docker image created
        |
        v
Image pushed to registry
        |
        v
Kubernetes node pulls image
        |
        v
Container starts from image
```

Visual:

```text
Git Repository
+----------------------------------+
| src/                             |
| pom.xml                          |
| Dockerfile                       |
+----------------------------------+
        |
        v
CI Pipeline
        |
        v
Docker Build
        |
        v
Image: order-service:1.0
        |
        v
Registry
        |
        v
Kubernetes Pod
```

Production point:

```text
Kubernetes does not run Dockerfiles.
Kubernetes runs images built from Dockerfiles.
```

---

# Dockerfile To Layer Mental Model

Each Dockerfile instruction contributes to image history.

Some instructions create filesystem layers.

Some set metadata.

Example:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/order-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Mental image:

```text
Docker Image
+----------------------------------+
| Metadata: ENTRYPOINT             |
+----------------------------------+
| App Layer: /app/app.jar          |
+----------------------------------+
| Metadata: WORKDIR /app           |
+----------------------------------+
| Base Layer: Java Runtime         |
+----------------------------------+
```

Important simplified rule:

```text
Filesystem-changing instructions -> layers
Metadata instructions -> image configuration
```

For understanding:

```text
FROM    -> foundation
WORKDIR -> default directory
COPY    -> add files
RUN     -> execute during build
CMD     -> default command
ENTRYPOINT -> main executable
```

---

# FROM Instruction

`FROM` chooses the base image.

Example:

```dockerfile
FROM eclipse-temurin:21-jre
```

Mental model:

```text
Choose foundation.
```

Visual:

```text
Base Image
+----------------------------------+
| Java Runtime                     |
| Linux filesystem                 |
| Certificates                     |
+----------------------------------+
```

Your image builds on top of it.

```text
Your Image
+----------------------------------+
| Your App                         |
+----------------------------------+
| Base Image                       |
+----------------------------------+
```

Spring Boot relevance:

Most Spring Boot apps need Java runtime.

Bad choice:

```dockerfile
FROM ubuntu
```

Then manually install Java.

Better:

```dockerfile
FROM eclipse-temurin:21-jre
```

because Java runtime is already prepared.

Interview expectation:

> `FROM` defines the base image and therefore strongly influences image size, security, runtime behavior, and build speed.

Common wrong answer:

> `FROM` just names the OS.

---

# WORKDIR Instruction

`WORKDIR` sets the working directory inside the image.

Example:

```dockerfile
WORKDIR /app
```

Mental model:

```text
cd /app
```

Visual:

```text
Container Filesystem
/
├── app   <-- WORKDIR
├── etc
├── usr
└── tmp
```

After this:

```dockerfile
COPY app.jar app.jar
```

means:

```text
copy into /app/app.jar
```

Without `WORKDIR`, files may land in confusing places.

Good:

```dockerfile
WORKDIR /app
COPY target/app.jar app.jar
```

Bad:

```dockerfile
COPY target/app.jar /app/app.jar
COPY config.yml config.yml
```

Then you may forget where things are.

Interview expectation:

> `WORKDIR` makes the Dockerfile easier and safer by setting a predictable directory for later instructions and runtime execution.

---

# COPY Instruction

`COPY` moves files from build context into the image.

Example:

```dockerfile
COPY target/order-service.jar app.jar
```

Visual:

```text
Build Context
+----------------------------------+
| target/order-service.jar         |
+----------------------------------+
        |
        | COPY
        v
Image Layer
+----------------------------------+
| /app/app.jar                     |
+----------------------------------+
```

Important:

Docker can only copy files from the build context.

If you run:

```text
docker build .
```

then `.` is the build context.

Common mistake:

```dockerfile
COPY /home/user/secret.txt /app/
```

This usually fails because that file is outside the build context.

Spring Boot relevance:

You typically copy:

```text
target/app.jar
```

not the entire project.

Bad:

```dockerfile
COPY . .
```

This copies:

```text
src/
target/
.git/
README
local files
possibly secrets
```

Better:

```dockerfile
COPY target/order-service.jar app.jar
```

or use multi-stage builds where the build happens inside Docker.

---

# RUN Instruction

`RUN` executes during image build.

Example:

```dockerfile
RUN apt-get update && apt-get install -y curl
```

Mental model:

```text
Build-time command
```

Not runtime command.

Visual:

```text
docker build
    |
    v
RUN apt install
    |
    v
New filesystem layer
```

Important distinction:

```text
RUN
  =
happens while building image

CMD / ENTRYPOINT
  =
happens when container starts
```

Common mistake:

```dockerfile
RUN java -jar app.jar
```

This tries to run the app during image build, which is usually wrong.

Correct:

```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Interview expectation:

> `RUN` is used to modify the image at build time, such as installing packages or preparing files.

---

# CMD Instruction

`CMD` provides a default command for the container.

Example:

```dockerfile
CMD ["java", "-jar", "app.jar"]
```

Mental model:

```text
Default startup command.
Can be overridden.
```

Visual:

```text
docker run image
        |
        v
uses CMD if no other command supplied
```

Example:

```dockerfile
CMD ["echo", "hello"]
```

Run:

```text
docker run image
```

executes:

```text
echo hello
```

But if user provides another command, CMD can be replaced.

For Spring Boot production images, many teams prefer `ENTRYPOINT` for the main executable.

---

# ENTRYPOINT Instruction

`ENTRYPOINT` defines the main executable.

Example:

```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Mental model:

```text
This image is meant to run this program.
```

Visual:

```text
Container Starts
      |
      v
ENTRYPOINT
      |
      v
java -jar app.jar
```

Difference:

```text
CMD
  =
default arguments / command

ENTRYPOINT
  =
fixed main executable
```

Common Spring Boot style:

```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]
```

A more flexible style:

```dockerfile
ENTRYPOINT ["java"]
CMD ["-jar", "app.jar"]
```

But for learning:

```text
ENTRYPOINT = main command of the image
CMD = default command/arguments
```

---

# ENV Instruction

`ENV` sets environment variables.

Example:

```dockerfile
ENV SPRING_PROFILES_ACTIVE=prod
```

Mental model:

```text
Default runtime environment value.
```

Visual:

```text
Image Metadata
+----------------------------------+
| ENV SPRING_PROFILES_ACTIVE=prod  |
+----------------------------------+
```

Important:

Do NOT put secrets in `ENV`.

Bad:

```dockerfile
ENV DB_PASSWORD=mysecret
```

Why?

It becomes part of image metadata/history.

Better:

```text
Inject secrets at runtime using Kubernetes Secret, Vault, or cloud secret manager.
```

Spring Boot relevance:

Spring Boot reads environment variables naturally.

Example:

```text
SPRING_PROFILES_ACTIVE
SERVER_PORT
JAVA_OPTS
DATABASE_URL
```

Use ENV for safe defaults, not secrets.

---

# EXPOSE Instruction

`EXPOSE` documents the intended port.

Example:

```dockerfile
EXPOSE 8080
```

Mental model:

```text
This app listens on 8080.
```

But important:

```text
EXPOSE does not publish the port by itself.
```

Visual:

```text
Image Metadata
+-------------------+
| EXPOSE 8080       |
+-------------------+
```

To actually publish:

```text
docker run -p 8080:8080 image
```

Kubernetes ignores Docker `EXPOSE` for actual Service routing. Kubernetes uses containerPort and Service definitions.

Interview expectation:

> `EXPOSE` is documentation/metadata. It does not automatically make the port reachable from outside.

Common wrong answer:

> EXPOSE opens the port to the internet.

---

# ARG Instruction

`ARG` is build-time variable.

Example:

```dockerfile
ARG JAR_FILE=target/app.jar
COPY ${JAR_FILE} app.jar
```

Mental model:

```text
Available during build.
```

Not the same as ENV.

```text
ARG -> build time
ENV -> runtime default
```

Visual:

```text
docker build --build-arg JAR_FILE=target/order.jar
          |
          v
Dockerfile build uses that value
```

Use cases:

```text
Version
Build profile
Artifact name
Base image variant
```

Do not use ARG for secrets either unless using proper build secret mechanisms.

---

# USER Instruction

`USER` changes the user used to run the container.

Bad default:

```text
Run as root
```

Better:

```dockerfile
USER 1001
```

Mental model:

```text
Reduce blast radius.
```

Visual:

```text
Container
+----------------------------------+
| App Process                      |
| User: non-root                   |
+----------------------------------+
```

Spring Boot relevance:

Most Spring Boot services do not need root.

Running as non-root improves security.

Common problem:

```text
Permission denied writing /app/logs
```

Cause:

```text
USER is non-root but directory ownership is wrong.
```

Correct mindset:

```text
Create directories and assign permissions before switching USER.
```

---

# HEALTHCHECK Instruction

`HEALTHCHECK` defines how Docker can check whether the container is healthy.

Example:

```dockerfile
HEALTHCHECK CMD curl -f http://localhost:8080/actuator/health || exit 1
```

Mental model:

```text
Container health signal.
```

In Kubernetes, however, you usually define:

```text
readinessProbe
livenessProbe
startupProbe
```

in YAML instead of relying only on Dockerfile HEALTHCHECK.

Spring Boot relevance:

Use Actuator:

```text
/actuator/health
```

Visual:

```text
Health Check
     |
     v
Spring Boot Actuator
     |
     v
UP / DOWN
```

---

# Dockerfile Build Context

When you run:

```text
docker build .
```

Docker sends the build context to the builder.

Mental model:

```text
Build Context
  =
Files Docker can see during build
```

Visual:

```text
Project Directory
+----------------------------------+
| src/                             |
| target/                          |
| pom.xml                          |
| Dockerfile                       |
| .git/                            |
| logs/                            |
+----------------------------------+
        |
        | docker build .
        v
Docker Builder
```

If your context is huge, builds become slow.

Use `.dockerignore`.

---

# .dockerignore

`.dockerignore` prevents unnecessary files from entering build context.

Example:

```text
.git
target
logs
*.tmp
.env
```

But be careful.

If you ignore `target`, then:

```dockerfile
COPY target/app.jar app.jar
```

will fail.

Mental model:

```text
.dockerignore
  =
Do not send these files to Docker build.
```

Visual:

```text
Project
+--------------------+
| src/       sent    |
| pom.xml    sent    |
| .git/      ignored |
| logs/      ignored |
| .env       ignored |
+--------------------+
```

Production benefit:

```text
Smaller context
Faster build
Less risk of copying secrets
```

---

# Docker Cache Deep Mental Model

Docker cache is based on previous build steps.

If a layer changes, later layers must be rebuilt.

Visual:

```text
Layer 1  FROM base        cache hit
Layer 2  dependencies     cache hit
Layer 3  app code         changed
Layer 4  package          rebuild
Layer 5  metadata         rebuild/reuse
```

Key rule:

```text
Stable things first.
Changing things later.
```

Bad order:

```dockerfile
COPY . .
RUN mvn package
```

Any source change invalidates everything.

Better order:

```dockerfile
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package
```

Why?

Dependencies change less often than source code.

Visual:

```text
pom.xml unchanged
      |
Dependency layer reused

src changed
      |
Only app build layer rebuilt
```

---

# Bad Spring Boot Dockerfile

Bad Dockerfile:

```dockerfile
FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

COPY . .

RUN mvn package

CMD ["java", "-jar", "target/order-service.jar"]
```

Problems:

```text
Maven included in runtime
Source code included in runtime
Large image
Poor separation of build and runtime
More attack surface
Slower pull
```

Visual:

```text
Runtime Image
+----------------------------------+
| target/order-service.jar         |
| source code                      |
| Maven                            |
| local build files                |
| dependencies cache               |
| Java runtime                     |
| OS files                         |
+----------------------------------+
```

This works, but it is not production-friendly.

---

# Good Simple Spring Boot Dockerfile

If JAR is built outside Docker:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/order-service.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Visual:

```text
Runtime Image
+----------------------------------+
| app.jar                          |
+----------------------------------+
| Java Runtime                     |
+----------------------------------+
| Base OS Files                    |
+----------------------------------+
```

Benefits:

```text
Simple
Small enough for many apps
Good for learning
Easy to debug
```

Limitation:

```text
Build depends on local Maven build.
```

---

# Multi-Stage Spring Boot Dockerfile

Production-friendly pattern:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /build/target/order-service.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Visual:

```text
Build Stage
+----------------------------------+
| Maven                            |
| Source Code                      |
| Compile                          |
| target/order-service.jar         |
+----------------------------------+
              |
              | COPY only jar
              v
Runtime Stage
+----------------------------------+
| app.jar                          |
| Java Runtime                     |
+----------------------------------+
```

Why this is better:

```text
Build tools stay in build stage.
Runtime image gets only what it needs.
```

---

# Dockerfile For Layered Spring Boot JARs

Spring Boot can separate dependencies and app code.

Mental model:

```text
Dependencies change slowly.
Application classes change often.
```

Layered structure:

```text
+----------------------------------+
| Application Classes              |
+----------------------------------+
| Spring Boot Loader               |
+----------------------------------+
| Snapshot Dependencies            |
+----------------------------------+
| Stable Dependencies              |
+----------------------------------+
```

Why useful?

When only code changes:

```text
Dependency layers reused.
App layer rebuilt.
```

This is excellent for CI/CD speed and Kubernetes pull efficiency.

---

# Dockerfile And Kubernetes

Kubernetes does not build Dockerfiles.

Kubernetes consumes images.

```text
Dockerfile
     |
CI Build
     |
Image
     |
Registry
     |
Kubernetes Pulls Image
```

Visual:

```text
Developer
   |
Git Push
   |
CI/CD
   |
docker build
   |
docker push
   |
Kubernetes rollout
```

If Dockerfile is bad:

```text
Image huge
Pull slow
Pod startup slow
Node disk pressure
Security scan fails
```

So Dockerfile quality directly affects Kubernetes reliability.

---

# Production Failure Story 1: Build Takes 20 Minutes

Problem:

```text
Every CI build takes 20 minutes.
```

Initial Dockerfile:

```dockerfile
COPY . .
RUN mvn package
```

Root cause:

```text
Every code change invalidates dependency cache.
```

Diagram:

```text
Code Change
     |
COPY . . changes
     |
Maven downloads dependencies again
     |
Slow build
```

Fix:

```dockerfile
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package
```

Result:

```text
Dependency cache reused.
Build becomes much faster.
```

Lesson:

```text
Dockerfile order affects CI/CD speed.
```

---

# Production Failure Story 2: Image Is 2.5 GB

Problem:

```text
Deployment rollout is slow.
```

Investigation:

```text
Image includes Maven, source code, test reports, local cache.
```

Visual:

```text
Image
+----------------------------------+
| Maven                            |
| Source Code                      |
| Test Reports                     |
| target/                          |
| Dependencies Cache               |
| JDK                              |
| OS                               |
+----------------------------------+
```

Fix:

```text
Use multi-stage build.
Only copy final JAR into runtime image.
```

Result:

```text
2.5 GB -> 350 MB
```

Lesson:

```text
Build environment should not be runtime environment.
```

---

# Production Failure Story 3: Secret Baked Into Image

Bad Dockerfile:

```dockerfile
ENV DB_PASSWORD=my-prod-password
```

Problem:

```text
Password becomes part of image metadata.
```

Even worse:

```dockerfile
COPY private-key.pem /app/private-key.pem
```

Visual:

```text
Image Layer
+----------------------------------+
| private-key.pem                  |
| DB_PASSWORD                      |
+----------------------------------+
```

Fix:

```text
Use Kubernetes Secrets
Use Vault
Use cloud secret manager
Inject at runtime
```

Lesson:

```text
Dockerfile should not contain secrets.
```

---

# Production Failure Story 4: Container Runs As Root

Problem:

```text
Security scan fails.
```

Cause:

```text
Container process runs as root.
```

Fix idea:

```dockerfile
RUN addgroup --system app && adduser --system app --ingroup app
USER app
```

Visual:

```text
Before

Container Process
     |
     v
root

After

Container Process
     |
     v
non-root app user
```

Lesson:

```text
Most Spring Boot services do not need root.
```

---

# Production Failure Story 5: Wrong Port Assumption

Dockerfile:

```dockerfile
EXPOSE 8080
```

Developer expects service to be reachable automatically.

Problem:

```text
Cannot access service.
```

Why?

```text
EXPOSE is metadata.
It does not publish the port.
```

Need:

```text
docker run -p 8080:8080
```

or Kubernetes Service.

Visual:

```text
EXPOSE
  =
Documentation

Port Mapping / Service
  =
Actual traffic path
```

Lesson:

```text
EXPOSE does not open network access by itself.
```

---

# Debugging Mindset

When Dockerfile-related issue happens, ask:

```text
1. Which Dockerfile built this image?
2. Which image tag is running?
3. Which layer is large?
4. Which instruction invalidated cache?
5. Are secrets baked in?
6. Is runtime image carrying build tools?
7. Is app running as root?
8. Is the correct JAR copied?
9. Is port exposure misunderstood?
10. Is .dockerignore excluding or including wrong files?
```

Mental debugging flow:

```text
Production Issue
      |
      +--> Runtime issue?
      |
      +--> Image issue?
              |
              +--> Dockerfile
                    |
                    +--> Base image
                    +--> COPY
                    +--> RUN
                    +--> ENTRYPOINT
                    +--> USER
                    +--> ENV
```

---

# Performance Tradeoffs

Good Dockerfile:

```text
Small image
Fast build
Fast pull
Low attack surface
Good cache behavior
```

Bad Dockerfile:

```text
Huge image
Slow build
Slow rollout
More CVEs
Secrets risk
Cache misses
```

Table:

| Area | Bad Dockerfile | Good Dockerfile |
|---|---|---|
| Build speed | Slow | Fast |
| Image size | Large | Small |
| Security | Weak | Better |
| Kubernetes rollout | Slow | Faster |
| Cache reuse | Poor | Strong |
| Debuggability | Messy | Clear |

Visual:

```text
Bad Dockerfile
     |
Huge Image
     |
Slow Pull
     |
Slow Pod Start
     |
Bad Deployment Experience

Good Dockerfile
     |
Small Image
     |
Fast Pull
     |
Fast Pod Start
     |
Reliable Deployment
```

---

# Common Mistakes

## Mistake 1: Confusing RUN and CMD

Wrong:

```text
RUN starts app when container runs.
```

Correct:

```text
RUN executes during build.
CMD/ENTRYPOINT runs when container starts.
```

## Mistake 2: COPY Everything

Wrong:

```dockerfile
COPY . .
```

Correct:

```text
Copy only what image needs.
Use .dockerignore.
```

## Mistake 3: Ignore Cache

Wrong:

```text
Docker cache is random.
```

Correct:

```text
Cache depends on instruction order and inputs.
```

## Mistake 4: Put Secrets In Dockerfile

Wrong:

```dockerfile
ENV PASSWORD=secret
```

Correct:

```text
Inject secrets at runtime.
```

## Mistake 5: Runtime Image Contains Build Tools

Wrong:

```text
Maven in production image.
```

Correct:

```text
Use multi-stage build.
```

More mistakes:

```text
6. Using latest blindly
7. Running as root unnecessarily
8. Not setting WORKDIR
9. Misunderstanding EXPOSE
10. Poor base image choice
11. Ignoring image size
12. Ignoring security scans
13. Not pinning important versions
14. Copying target directory accidentally
15. Assuming Kubernetes uses Dockerfile directly
```

---

# System Design Connection

Dockerfile is the first step of cloud-native deployment.

```text
Source Code
    |
Dockerfile
    |
Image
    |
Registry
    |
Kubernetes
    |
Microservice
```

In large systems:

```text
100 Microservices
      |
100 Dockerfiles
      |
CI/CD Platform
      |
Image Registry
      |
Kubernetes Cluster
```

Good Dockerfiles create:

```text
Fast builds
Small images
Safer deployments
Predictable rollbacks
Efficient scaling
```

Bad Dockerfiles create:

```text
Slow CI/CD
Node disk pressure
Security findings
Unreliable releases
```

---

# Strong Interview Answers

## Q1: What is a Dockerfile?

Expected answer:

A Dockerfile is a declarative build recipe containing instructions to create a Docker image. It defines the base image, files to copy, build-time commands, environment metadata, and the startup command.

Common wrong answer:

A Dockerfile is a running container.

---

## Q2: Difference between RUN and CMD?

Expected answer:

`RUN` executes during image build and creates image changes. `CMD` defines the default command executed when a container starts.

Common wrong answer:

Both run when the container starts.

---

## Q3: Difference between CMD and ENTRYPOINT?

Expected answer:

`ENTRYPOINT` defines the main executable of the container. `CMD` provides default command or arguments and is easier to override.

Common wrong answer:

They are identical.

---

## Q4: Why does Dockerfile order matter?

Expected answer:

Docker builds images layer by layer and reuses cached layers. If an earlier layer changes, later layers must be rebuilt. Stable dependencies should come before frequently changing application code.

Common wrong answer:

Order only affects readability.

---

## Q5: Why use multi-stage builds?

Expected answer:

Multi-stage builds separate build-time dependencies from runtime artifacts. This produces smaller, cleaner, and more secure runtime images.

Common wrong answer:

Multi-stage builds are only for frontend apps.

---

## Q6: Why should secrets not be in Dockerfiles?

Expected answer:

Secrets in Dockerfiles can become part of image metadata or layers and may remain recoverable even if removed later. Secrets should be injected at runtime.

Common wrong answer:

It is safe if the image is private.

---

# Production Case Study: Spring Boot Order Service

Architecture:

```text
Order Service
     |
Spring Boot
     |
Dockerfile
     |
CI Build
     |
Registry
     |
Kubernetes Deployment
```

Initial Dockerfile:

```dockerfile
FROM maven:3.9-eclipse-temurin-21
WORKDIR /app
COPY . .
RUN mvn package
CMD ["java", "-jar", "target/order-service.jar"]
```

Problems found:

```text
Build takes 18 minutes.
Image is 2.2 GB.
Security scanner reports many CVEs.
Deployment rollout is slow.
Source code exists inside runtime image.
```

Investigation diagram:

```text
Dockerfile
   |
   +--> Maven base used at runtime
   |
   +--> COPY . . includes everything
   |
   +--> Dependencies downloaded every build
   |
   +--> No multi-stage separation
```

Improved Dockerfile:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /build/target/order-service.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Improved architecture:

```text
Build Stage
+----------------------------------+
| Maven                            |
| Source Code                      |
| Compile                          |
+----------------------------------+
              |
              | copy only JAR
              v
Runtime Stage
+----------------------------------+
| Java Runtime                     |
| app.jar                          |
+----------------------------------+
```

Results:

```text
Build faster due to dependency cache.
Runtime image smaller.
Source code not included.
Maven not included.
Deployment faster.
Security surface reduced.
```

Lessons:

```text
Dockerfile design affects CI/CD speed.
Dockerfile design affects security.
Dockerfile design affects Kubernetes rollout speed.
Dockerfile design affects production reliability.
```

---

# Final Cheat Sheet

```text
Dockerfile
  =
Recipe for building Docker image
```

```text
FROM
  =
Base image / foundation
```

```text
WORKDIR
  =
Default directory inside image/container
```

```text
COPY
  =
Move files from build context into image
```

```text
RUN
  =
Execute during image build
```

```text
CMD
  =
Default startup command / arguments
```

```text
ENTRYPOINT
  =
Main container executable
```

```text
ENV
  =
Runtime environment default
```

```text
ARG
  =
Build-time variable
```

```text
EXPOSE
  =
Port documentation metadata
```

```text
USER
  =
Run process as specific user
```

```text
.dockerignore
  =
Exclude files from build context
```

```text
Good Dockerfile
  =
Small image
Fast build
Good cache
No secrets
Non-root
Production ready
```

---

# One Picture To Remember

```text
Source Code Repository
+--------------------------------------------------+
| src/                                             |
| pom.xml                                          |
| Dockerfile                                       |
| .dockerignore                                    |
+--------------------------------------------------+
        |
        v
Docker Build
        |
        v
Dockerfile Instructions
+--------------------------------------------------+
| FROM       -> choose base                         |
| WORKDIR    -> set directory                       |
| COPY       -> add app files                       |
| RUN        -> build-time changes                  |
| ENV/ARG    -> config values                       |
| USER       -> security identity                   |
| ENTRYPOINT -> startup executable                  |
+--------------------------------------------------+
        |
        v
Docker Image
+--------------------------------------------------+
| Spring Boot App Layer                            |
+--------------------------------------------------+
| Java Runtime Layer                               |
+--------------------------------------------------+
| Base OS Layer                                    |
+--------------------------------------------------+
        |
        v
Registry
        |
        v
Kubernetes Pod
+--------------------------------------------------+
| Running Spring Boot Container                    |
| Namespaces                                       |
| Cgroups                                          |
| Writable Layer                                   |
+--------------------------------------------------+

Dockerfile quality
        |
        +--> Build speed
        +--> Image size
        +--> Security
        +--> Deployment speed
        +--> Production reliability
```

That picture is the core of Dockerfile deep understanding.
