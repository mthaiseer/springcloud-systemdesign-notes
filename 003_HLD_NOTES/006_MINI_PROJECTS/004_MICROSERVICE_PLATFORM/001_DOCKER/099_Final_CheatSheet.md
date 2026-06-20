# 099_Final_CheatSheet.md

> MiniDocker Final Revision Cheat Sheet  
> Understanding First • ASCII Visual Learning • Real-World Mental Models • Not Memorization

---

# 0. How To Use This Cheat Sheet

This is not a command dump. This is your final mental map for Docker.

Read it like this:

```text
First:   Understand the picture
Second:  Remember the packet/data/process flow
Third:   Use commands only to verify the model
Fourth:  Explain in interviews using simple words
```

Docker is not only a tool to run containers. Docker is a packaging, isolation, networking, storage, and delivery system for applications.

One complete picture:

```text
Developer Code
     |
     v
Dockerfile
     |
     v
Image
     |
     v
Container
     |
     +--> CPU / Memory isolation using cgroups
     +--> Process / network / mount isolation using namespaces
     +--> Filesystem using layers + copy-on-write
     +--> Network using bridge / host / overlay
     +--> Storage using volumes / bind mounts
     +--> Delivery using registry / CI/CD / Kubernetes
```

Do not memorize Docker as commands. Understand Docker as a small operating-system packaging model around your application.

---

# 1. Docker In One Sentence

Docker packages an application with its runtime environment and runs it as an isolated process using Linux kernel features.

```text
Application + Runtime + Libraries + Config
                |
                v
              Image
                |
                v
          Running Container
```

Real-world model:

```text
Application = Food
Image       = Sealed lunch box
Container   = Lunch box opened and being eaten
Registry    = Warehouse of lunch boxes
Dockerfile  = Recipe
Docker host = Kitchen table
```

Interview answer:

Docker is not a lightweight VM. A VM virtualizes hardware. Docker isolates processes on the same host kernel using namespaces and cgroups.

---

# 2. VM vs Container Quick Picture

```text
Virtual Machine Model

+-----------------------------+
| Physical Host               |
| +-------------------------+ |
| | Hypervisor              | |
| | +---------+ +---------+ | |
| | | GuestOS | | GuestOS | | |
| | | App    | | App     | | |
| | +---------+ +---------+ | |
| +-------------------------+ |
+-----------------------------+
```

```text
Container Model

+-----------------------------+
| Host OS Kernel              |
| +---------+ +-------------+ |
| | App A   | | App B       | |
| | libs    | | libs        | |
| +---------+ +-------------+ |
+-----------------------------+
```

Key memory:

```text
VM        = own OS kernel
Container = shared host kernel
```

Practical effect:

```text
VM startup        -> minutes
Container startup -> seconds or less
VM size           -> GBs
Container image   -> MBs to hundreds of MBs
```

---

# 3. Container Mental Model

A container is a normal process with boundaries.

```text
Without Docker:

Host
 └── java -jar app.jar
```

```text
With Docker:

Host
 └── container boundary
      └── java -jar app.jar
```

The process thinks:

```text
I have my own filesystem
I have my own hostname
I have my own network card
I have my own process tree
```

But the host knows:

```text
It is still just a Linux process
```

Debug command:

```bash
docker top myapp
ps aux | grep java
```

Mental model:

```text
Container = Process + Illusion of a machine
```

---

# 4. Linux Namespaces Cheat Sheet

Namespaces answer: what can the process see?

```text
Namespace        What it isolates
-------------------------------------------
PID              Process tree
NET              Network interfaces/routes
MNT              Filesystem mount view
UTS              Hostname
IPC              Shared memory / message queues
USER             User IDs
CGROUP           Resource control visibility
```

ASCII picture:

```text
Host PID Namespace

PID 1 systemd
PID 900 dockerd
PID 1200 java container process

Container PID Namespace

PID 1 java
```

Same process, different view:

```text
Host sees:      PID 1200
Container sees: PID 1
```

Interview line:

Namespaces create isolation of views. They do not limit resource usage. Resource limits come from cgroups.

---

# 5. Cgroups Cheat Sheet

Cgroups answer: how much can the process use?

```text
Container Process
      |
      v
Cgroup Rules
      |
      +--> max CPU
      +--> max memory
      +--> max PIDs
      +--> block I/O limits
```

Example:

```bash
docker run --memory=512m --cpus=1 myapp
```

Meaning:

```text
Java app may want 2 GB RAM
Docker cgroup says only 512 MB
If app crosses limit -> OOMKilled
```

Spring Boot memory pattern:

```bash
docker run -m 512m \
  -e JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75" \
  my-spring-app
```

Why?

```text
Container memory = 512 MB
JVM heap should not take all memory
Native memory, metaspace, threads also need space
```

---

# 6. Image Mental Model

An image is a read-only layered filesystem plus metadata.

```text
Image
 |
 +-- base OS layer
 +-- JRE layer
 +-- dependency layer
 +-- application JAR layer
 +-- metadata: command, env, exposed ports
```

ASCII:

```text
+-----------------------------+
| app.jar                     |  <- top layer
+-----------------------------+
| dependencies                |
+-----------------------------+
| JRE                         |
+-----------------------------+
| base OS                     |
+-----------------------------+
```

Important:

```text
Image  = blueprint
Container = running instance of blueprint
```

Command:

```bash
docker images
docker image inspect myapp:1.0
```

---

# 7. Layer And Copy-On-Write Cheat Sheet

Layers are read-only. Container adds one writable layer on top.

```text
Container Writable Layer
+-----------------------+  <- writes happen here
Image Layer: app.jar
+-----------------------+
Image Layer: JRE
+-----------------------+
Image Layer: OS
+-----------------------+
```

When app writes `/tmp/a.log`:

```text
Write does NOT modify image
Write goes to container writable layer
```

When container is removed:

```text
Writable layer removed
Data lost unless volume/bind mount used
```

Golden rule:

```text
Image data is durable.
Container writable-layer data is disposable.
Volume data is durable.
```

---

# 8. Dockerfile Cheat Sheet

Dockerfile is a recipe to build an image.

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Mental model:

```text
FROM       = choose kitchen base
WORKDIR    = choose working table
COPY       = place files
RUN        = cook during image build
EXPOSE     = document intended port
ENTRYPOINT = what starts when container runs
```

Common mistake:

```dockerfile
COPY . .
RUN mvn package
```

This often breaks cache because every source code change invalidates dependency download.

Better:

```dockerfile
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package
```

---

# 9. Multi-Stage Build Cheat Sheet

Problem:

```text
Build needs Maven + source code + compilers
Runtime only needs JRE + final JAR
```

Multi-stage picture:

```text
Builder Stage
+---------------------------+
| Maven                     |
| source code               |
| builds app.jar            |
+-------------+-------------+
              |
              | COPY --from=builder
              v
Runtime Stage
+---------------------------+
| JRE only                  |
| app.jar only              |
+---------------------------+
```

Example:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Interview answer:

Multi-stage builds reduce image size and attack surface because build tools stay in the builder stage and only the runtime artifact is copied into the final image.

---

# 10. Docker Build Cache Cheat Sheet

Docker caches each Dockerfile instruction.

```text
Instruction 1 -> cache layer
Instruction 2 -> cache layer
Instruction 3 -> cache layer
```

If one instruction changes:

```text
Changed instruction -> cache miss
All later instructions -> rebuilt
```

Good Spring Boot structure:

```text
COPY pom.xml first       -> dependencies cached
COPY src later           -> app code rebuild only
```

Bad structure:

```text
COPY . .                 -> any file change invalidates everything
```

Command:

```bash
docker build -t myapp:1.0 .
docker build --no-cache -t myapp:clean .
```

---

# 11. Docker Run Cheat Sheet

```bash
docker run --name myapp -p 8080:8080 myapp:1.0
```

Picture:

```text
Browser
  |
  v
localhost:8080 on host
  |
  v
Docker port forwarding
  |
  v
container:8080
  |
  v
Spring Boot app
```

Common options:

```bash
-d                       run detached
--name app               container name
-p 8080:8080             hostPort:containerPort
-e KEY=value             environment variable
--network app-net        attach to network
-v data:/var/lib/data    mount volume
--rm                     remove after stop
```

---

# 12. Networking Mental Model

Container networking is a tiny internet inside the host.

```text
Container = house
IP        = address
Port      = door
DNS       = phone book
Bridge    = local router
Network   = city
```

Default bridge:

```text
Host
 |
 +-- docker0 bridge
       |
       +-- container A
       +-- container B
```

Packet path host to container:

```text
Client
  |
Host Port
  |
NAT / iptables
  |
docker0
  |
veth pair
  |
container eth0
  |
application port
```

---

# 13. Bridge vs Host vs Overlay

```text
Bridge Network

Host
 |-- docker0
      |-- app container
      |-- redis container
```

Use for single-host local microservices.

```text
Host Network

Container shares host network namespace
No bridge NAT
Less isolation
```

Use rarely, mostly for performance-sensitive or low-level networking tools.

```text
Overlay Network

Host A                           Host B
+-----------+                    +-----------+
| service A | <--- virtual ----> | service B |
+-----------+     network        +-----------+
```

Use for multi-host container communication, Docker Swarm, and conceptually similar to Kubernetes CNI overlays.

---

# 14. Docker DNS Cheat Sheet

Inside a custom Docker network, containers can reach each other by service/container name.

```yaml
services:
  order-service:
    image: order
  user-service:
    image: user
  redis:
    image: redis
```

Order service should call:

```text
http://user-service:8080
redis:6379
```

Not:

```text
172.18.0.4
```

Why?

```text
Container restart -> IP may change
Service name -> stable
```

Spring Boot config:

```yaml
user:
  service:
    base-url: http://user-service:8080

spring:
  data:
    redis:
      host: redis
      port: 6379
```

---

# 15. Service-To-Service Communication

Bad model:

```text
Order Service -> hardcoded IP -> User Service
```

Good model:

```text
Order Service -> service DNS name -> User Service
```

Java WebClient:

```java
@Service
public class UserClient {
    private final WebClient webClient;

    public UserClient(WebClient.Builder builder,
                      @Value("${user.service.base-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<String> getUser(String id) {
        return webClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(String.class);
    }
}
```

Flow:

```text
OrderController
   |
   v
UserClient
   |
   v
Docker DNS: user-service
   |
   v
User container IP
   |
   v
UserController
```

---

# 16. Volumes vs Bind Mounts

```text
Volume
Docker-managed storage
Good for database data
```

```text
Bind Mount
Host path mounted into container
Good for local development
```

Picture:

```text
Named Volume

Container /var/lib/postgresql/data
       |
       v
Docker volume postgres-data
       |
       v
Host Docker storage area
```

```text
Bind Mount

Container /app/src
       |
       v
Host ./src
```

Golden rule:

```text
Use volumes for production persistence.
Use bind mounts for local development and config injection.
```

---

# 17. Stateful Containers Cheat Sheet

Stateless container:

```text
Can die and restart anywhere
No important local data
```

Stateful container:

```text
Has important data: DB files, Redis AOF/RDB, uploaded files
Needs persistent storage and backup plan
```

Postgres example:

```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: appdb
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
    volumes:
      - postgres-data:/var/lib/postgresql/data

volumes:
  postgres-data:
```

Crash dry run:

```text
docker compose down
container removed
volume remains
compose up
new container attaches old volume
data survives
```

---

# 18. Docker Compose Mental Model

Compose is a local microservice stack launcher.

```text
docker-compose.yml
       |
       v
+-----------------------------+
| network                     |
|  order-service              |
|  user-service               |
|  redis                      |
|  postgres                   |
+-----------------------------+
```

Complete Spring Boot stack:

```yaml
services:
  order-service:
    build: ./order-service
    ports:
      - "8080:8080"
    environment:
      USER_SERVICE_URL: http://user-service:8081
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/orders
    depends_on:
      - user-service
      - redis
      - postgres

  user-service:
    build: ./user-service
    ports:
      - "8081:8081"

  redis:
    image: redis:7

  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: orders
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
    volumes:
      - postgres-data:/var/lib/postgresql/data

volumes:
  postgres-data:
```

Important:

```text
depends_on controls startup order
It does NOT guarantee application readiness
```

Use health checks for readiness.

---

# 19. Container Debugging Flow

Do not randomly run commands. Follow the path.

```text
Symptom
  |
  v
Is container running?
  |
  v
Are logs clean?
  |
  v
Is port listening?
  |
  v
Can DNS resolve?
  |
  v
Can network connect?
  |
  v
Is dependency healthy?
```

Commands:

```bash
docker ps -a
docker logs myapp
docker inspect myapp
docker exec -it myapp sh
docker network inspect app_default
docker compose logs -f
docker compose ps
```

Spring Boot failure:

```text
Connection refused to postgres:5432
```

Possible reasons:

```text
Postgres container down
Wrong service name
Postgres not ready yet
Wrong port
Wrong credentials
Network mismatch
```

---

# 20. Container Security Cheat Sheet

Security model:

```text
Smaller image
  + non-root user
  + minimal capabilities
  + no secrets baked into image
  + read-only filesystem where possible
  + scanned dependencies
  + isolated network
```

Bad Dockerfile:

```dockerfile
FROM ubuntu
RUN apt-get update && apt-get install -y curl vim maven openjdk-21-jdk
COPY . .
CMD java -jar app.jar
```

Better:

```dockerfile
FROM eclipse-temurin:21-jre
RUN useradd -r -u 10001 appuser
WORKDIR /app
COPY target/app.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Security picture:

```text
Attacker inside container
      |
      v
Non-root user limits damage
      |
      v
Dropped capabilities limit kernel actions
      |
      v
Read-only FS limits modification
      |
      v
Network policies limit movement
```

---

# 21. Important Docker Commands

Images:

```bash
docker images
docker pull redis:7
docker build -t myapp:1.0 .
docker rmi myapp:1.0
```

Containers:

```bash
docker ps
docker ps -a
docker run -d --name myapp myapp:1.0
docker stop myapp
docker start myapp
docker rm myapp
```

Logs and exec:

```bash
docker logs -f myapp
docker exec -it myapp sh
```

Networks:

```bash
docker network ls
docker network inspect bridge
docker network create app-net
```

Volumes:

```bash
docker volume ls
docker volume inspect postgres-data
docker volume rm postgres-data
```

Compose:

```bash
docker compose up -d
docker compose down
docker compose logs -f
docker compose ps
docker compose down -v
```

Danger:

```text
docker compose down -v removes volumes
Database data can be deleted
```

---

# 22. Production Troubleshooting Matrix

```text
Problem                         First Checks
-----------------------------------------------------------------
Container exits immediately      docker logs, ENTRYPOINT, env vars
Port not reachable               -p mapping, app bind address, firewall
Service cannot call service      DNS name, network, port, logs
DB connection fails              credentials, readiness, service name
Data lost after restart          missing volume
Image too large                  multi-stage, base image, COPY scope
Build slow                       cache order, dependency layer
OOMKilled                        memory limit, JVM heap settings
Permission denied                USER, file ownership, mounted volume perms
Works locally not in Compose     env vars, localhost misuse, DNS names
```

Most common Docker bug for Spring Boot:

```text
Using localhost inside container
```

Wrong:

```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/orders
```

Correct:

```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/orders
```

Why?

```text
Inside container, localhost means same container
Not the host
Not another container
```

---

# 23. Docker To Kubernetes Mapping

```text
Docker Concept              Kubernetes Concept
-------------------------------------------------
Container                   Container
Docker Compose service      Deployment
Docker network              Pod/Service network
Docker DNS name             Kubernetes Service DNS
Docker volume               PersistentVolumeClaim
.env / environment          ConfigMap / Secret
Port mapping                Service / Ingress
Restart policy              Pod restartPolicy
Healthcheck                 Liveness/Readiness probe
Image                       Image from registry
```

Mental transition:

```text
Docker Compose = local multi-container app
Kubernetes     = production orchestrator for many containers
```

---

# 24. Strong Interview Answers

## What is Docker?

Docker packages and runs applications as isolated processes using Linux namespaces, cgroups, layered filesystems, and container images.

## Is Docker a VM?

No. A VM virtualizes hardware and runs a full guest OS. Docker containers share the host kernel and isolate processes.

## What is an image?

An image is a read-only layered filesystem plus metadata used to create containers.

## What is a container?

A container is a running instance of an image with an additional writable layer and isolated process/network/filesystem views.

## Why use multi-stage builds?

To separate build-time dependencies from runtime artifacts, reducing image size and attack surface.

## Why should containers be stateless?

Because containers are disposable. Important data should live in volumes, databases, or external storage.

## Why should services use DNS names instead of IPs?

Container IPs can change after restart. DNS names remain stable inside Docker networks.

## Why is localhost dangerous in Docker?

Inside a container, localhost points to the same container, not another service or the host machine.

## What causes OOMKilled?

The container exceeded its memory cgroup limit and the kernel killed the process.

## How do you debug container startup failure?

Check `docker ps -a`, then `docker logs`, inspect env vars, entrypoint, file permissions, and dependency readiness.

---

# 25. One Picture To Remember

```text
                    Developer
                       |
                       v
                  Dockerfile
                       |
                       v
                    Image
       +---------------+----------------+
       |                                |
       v                                v
  Registry                         Container
                                      |
              +-----------------------+-----------------------+
              |                       |                       |
              v                       v                       v
         Namespaces                Cgroups              Writable Layer
      isolate views            limit resources          runtime changes
              |                       |                       |
              v                       v                       v
        Network / PID /        CPU / Memory /          Lost unless volume
        Mount / User           PIDs / IO
              |
              v
       Docker Network
              |
      +-------+--------+
      |                |
      v                v
  Service DNS       Port Mapping
      |                |
      v                v
container-to-      host-to-container
container calls    traffic
```

Final memory:

```text
Docker = package + isolate + connect + persist + ship
```

---

# 26. Five-Minute Pre-Interview Revision

Say this clearly:

Docker runs applications as isolated processes, not full virtual machines. It uses namespaces for isolation, cgroups for resource limits, images for packaging, layered filesystems for reuse, Docker networks for communication, and volumes for persistence. In Spring Boot systems, I use multi-stage Dockerfiles to build a small runtime image, environment variables for configuration, Docker Compose for local service stacks, service names for DNS-based communication, and volumes for stateful dependencies like Postgres and Redis. In production, I avoid root containers, avoid hardcoded IPs, avoid storing secrets in images, configure JVM memory according to container limits, and debug using logs, inspect, exec, network inspect, and health checks.

```text
If you remember only one line:

Container = isolated process
Image     = packaged filesystem
Network   = virtual communication layer
Volume    = durable data outside container lifecycle
Compose   = local microservice stack launcher
```

---

# 27. Final Production Checklist

```text
[ ] Dockerfile uses small runtime base image
[ ] Multi-stage build removes Maven/build tools
[ ] App runs as non-root user
[ ] JVM memory respects container limit
[ ] No secrets baked into image
[ ] Config passed through env vars / secrets
[ ] Service uses DNS names, not container IPs
[ ] No localhost for cross-container communication
[ ] Database uses named volume
[ ] Logs go to stdout/stderr
[ ] Healthcheck or readiness endpoint exists
[ ] Compose stack has clear networks and volumes
[ ] Ports exposed only when needed
[ ] Image scanned for vulnerabilities
[ ] Container resource limits configured
[ ] Debug commands understood, not memorized
```

---

# 28. MiniDocker Completion Map

```text
FOUNDATION
001 Why Docker
002 VM vs Container
003 Container Mental Model

INTERNALS
004 Linux Namespaces
005 Cgroups Resource Control
006 UnionFS And Layered Filesystems
007 Copy On Write

IMAGES
008 Docker Image Architecture
009 Dockerfile Deep Dive
010 MultiStage Builds
011 Image Optimization Strategies

NETWORKING
012 Docker Networking Mental Model
013 Bridge Host Overlay Networks
014 Service To Service Communication

STORAGE
015 Volumes BindMounts Persistence
016 Stateful Containers

APPLICATION
017 Dockerizing SpringBoot
018 DockerCompose Microservice Stack

OPERATIONS
019 Container Debugging And Troubleshooting
020 Container Security And Best Practices

FINAL
099 Final CheatSheet
```

End goal:

```text
You should be able to explain Docker from first principles,
write production Dockerfiles,
run Spring Boot microservices locally,
debug container failures,
and map Docker concepts to Kubernetes.
```
