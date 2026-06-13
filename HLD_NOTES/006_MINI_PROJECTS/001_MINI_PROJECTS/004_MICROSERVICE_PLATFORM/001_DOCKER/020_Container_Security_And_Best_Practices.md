# 020_Container_Security_And_Best_Practices

> MiniDocker Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Security Model • No Memorization

---

# 1. Why Container Security Exists

A container is not a magic safe box. It is still a Linux process running on a host. Docker gives isolation using namespaces, cgroups, capabilities, filesystem layers, seccomp, AppArmor/SELinux, and network boundaries. If you remember only one thing, remember this:

```text
Container security is not one lock.
It is many smaller locks around one Linux process.
```

Mental model:

```text
                Internet
                   |
                   v
        +---------------------+
        | Exposed API Port    |
        +----------+----------+
                   |
                   v
        +---------------------+
        | Container Process   |
        | java -jar app.jar   |
        +----------+----------+
                   |
        +----------+----------+
        |                     |
        v                     v
 Filesystem             Linux Kernel
 image layers           host shared kernel
```

Real world analogy:

```text
Container = Apartment room
Host      = Apartment building
Kernel    = Building foundation
Docker    = Security guard + room key system
```

If one tenant is careless, the whole building can still be affected if doors, permissions, and rules are weak.

---

# 2. Do Not Memorize Security Rules

Do not memorize random rules like:

```text
Use non-root
Use distroless
Scan images
Do not mount docker.sock
```

Understand the reason:

```text
Attacker Goal
     |
     +--> Run code
     +--> Read secrets
     +--> Write files
     +--> Escape container
     +--> Reach internal services
     +--> Persist after restart
```

Security controls block those paths:

```text
Threat                         Control
------                         -------
Run as root                    USER appuser
Write anywhere                 read_only filesystem
Extra Linux power              drop capabilities
Secret leakage                 secrets manager
Vulnerable packages            image scanning
Lateral movement               network isolation
Persistence                    immutable images + volumes control
```

---

# 3. One Picture To Remember

```text
                         User Request
                              |
                              v
                       +--------------+
                       | API Gateway  |
                       +------+-------+
                              |
                              v
+---------------------------------------------------------------+
| Docker Host                                                   |
|                                                               |
|  +-------------------- Container --------------------------+  |
|  | USER appuser                                           |  |
|  | read-only root FS                                      |  |
|  | no shell if distroless                                 |  |
|  | no NET_ADMIN                                           |  |
|  | secrets mounted safely                                 |  |
|  | app listens only on required port                      |  |
|  +----------------------+----------------------------------+  |
|                         |                                     |
|                         v                                     |
|                  Host Linux Kernel                            |
|             namespaces + cgroups + seccomp                    |
+---------------------------------------------------------------+
```

Security means reducing what the process can see, do, write, and reach.

---

# 4. Container Attack Surface Mental Model

Every container has an attack surface:

```text
                 Container Attack Surface

       +---------------------------------------+
       | 1. Image packages                     |
       | 2. Application code                   |
       | 3. Runtime user permissions           |
       | 4. Network exposure                   |
       | 5. Mounted volumes                    |
       | 6. Secrets / env variables            |
       | 7. Kernel capabilities                |
       | 8. Docker daemon access               |
       +---------------------------------------+
```

For a Spring Boot service:

```text
User HTTP Request
      |
      v
Spring Controller
      |
      +--> DB password from env
      +--> Redis password from env
      +--> Writes logs
      +--> Reads config
      +--> Calls another service
```

If an attacker gets remote code execution, they ask:

```text
Who am I?
What files can I read?
What files can I write?
What network can I reach?
What secrets are available?
Can I control Docker host?
```

Good container security makes every answer boring.

---

# 5. Root Inside Container Is Still Dangerous

Many beginners think root inside a container is harmless. It is less powerful than host root, but still dangerous.

Bad model:

```text
Container root != problem
```

Correct model:

```text
Container root = strongest user inside container boundary
If boundary has weakness, damage becomes larger
```

ASCII flow:

```text
Bad Container

+--------------------------+
| root user                |
| can write app files      |
| can modify runtime dirs  |
| can install tools        |
| can abuse mounted volume |
+-------------+------------+
              |
              v
        Bigger blast radius
```

Better:

```text
Good Container

+--------------------------+
| appuser                  |
| cannot modify system     |
| cannot install packages  |
| limited write path       |
+-------------+------------+
              |
              v
        Smaller blast radius
```

---

# 6. Secure Spring Boot Dockerfile

A simple but safer Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

RUN groupadd -r spring && useradd -r -g spring spring

COPY target/order-service.jar app.jar

RUN chown -R spring:spring /app

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Mental flow:

```text
Build Image
   |
   +--> create /app
   +--> copy jar
   +--> create low privilege user
   +--> run Java as spring user
```

Why this matters:

```text
If app is compromised:
attacker becomes spring user, not root
```

Check inside container:

```bash
docker exec -it order-service id
```

Expected:

```text
uid=... spring gid=... spring
```

---

# 7. Multi-Stage Build For Security

A common mistake is shipping Maven, source code, test files, and build tools in runtime image.

Bad image:

```text
Runtime Image
  |
  +-- JDK
  +-- Maven
  +-- source code
  +-- tests
  +-- build cache
  +-- app jar
```

Better image:

```text
Builder Image                  Runtime Image
+------------------+           +------------------+
| JDK + Maven      |           | JRE only         |
| source code      |  copy     | app.jar          |
| build tools      +---------> | appuser          |
+------------------+           +------------------+
```

Dockerfile:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd -r spring && useradd -r -g spring spring
COPY --from=builder /src/target/*.jar app.jar
RUN chown -R spring:spring /app
USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Production meaning:

```text
Build tools stay in factory.
Only final product goes to shop shelf.
```

---

# 8. Distroless Mental Model

Distroless images remove unnecessary OS tools.

Normal image:

```text
Container
  +-- java
  +-- shell
  +-- package manager
  +-- curl
  +-- many libraries
```

Distroless image:

```text
Container
  +-- java runtime
  +-- app jar
  +-- minimal libraries
```

Security benefit:

```text
Fewer tools for attacker
Fewer packages to patch
Smaller attack surface
```

Tradeoff:

```text
Harder debugging because no shell
```

Production pattern:

```text
Development: normal JRE image easier to debug
Production: distroless or slim image after maturity
```

Example:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM gcr.io/distroless/java21-debian12
WORKDIR /app
COPY --from=builder /src/target/*.jar /app/app.jar
USER nonroot
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

---

# 9. Image Size Is Security Too

Smaller images are not only faster. They usually contain fewer vulnerable packages.

```text
Large Image
  |
  +-- more OS packages
  +-- more CVEs
  +-- slower pull
  +-- bigger scan output

Small Image
  |
  +-- fewer packages
  +-- fewer CVEs
  +-- faster deploy
  +-- simpler patching
```

But do not blindly choose Alpine for Java without testing. Musl vs glibc differences, DNS behavior, native libraries, and performance can surprise teams.

Decision model:

```text
Need easiest compatibility?   Debian/Ubuntu slim
Need smaller runtime?         Distroless
Need tiny image?              Alpine, but test carefully
Need debugging?               Non-distroless debug variant
```

---

# 10. Secrets Are Not Configuration

Configuration can be public-ish:

```text
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

Secrets must be protected:

```text
DB_PASSWORD
JWT_PRIVATE_KEY
REDIS_PASSWORD
OAUTH_CLIENT_SECRET
```

Bad:

```dockerfile
ENV DB_PASSWORD=mysecret
```

Why bad:

```text
Image layer remembers it
docker history may expose it
Anyone with image may inspect it
```

Better Docker Compose pattern:

```yaml
services:
  order-service:
    image: order-service:1.0
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/orders
      SPRING_DATASOURCE_USERNAME: orders
      SPRING_DATASOURCE_PASSWORD_FILE: /run/secrets/db_password
    secrets:
      - db_password

secrets:
  db_password:
    file: ./secrets/db_password.txt
```

Mental model:

```text
Image = public package
Secret = sealed envelope given at runtime
```

---

# 11. Spring Boot Reading Secrets Safely

Spring Boot often reads env vars directly:

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
```

This is common, but environment variables can be visible through inspection in some operational paths.

A simple file-based pattern:

```java
package com.example.security;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SecretReader {
    private SecretReader() {}

    public static String readSecret(String envFilePathName, String fallbackEnvName) {
        String filePath = System.getenv(envFilePathName);
        if (filePath != null && !filePath.isBlank()) {
            try {
                return Files.readString(Path.of(filePath)).trim();
            } catch (IOException e) {
                throw new IllegalStateException("Cannot read secret file: " + filePath, e);
            }
        }
        String value = System.getenv(fallbackEnvName);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing secret: " + fallbackEnvName);
        }
        return value;
    }
}
```

Usage:

```java
String dbPassword = SecretReader.readSecret(
        "DB_PASSWORD_FILE",
        "DB_PASSWORD"
);
```

Production principle:

```text
Prefer runtime secret injection.
Never bake secrets into image.
```

---

# 12. Read-Only Root Filesystem

Many apps do not need to write to the image filesystem.

```text
Normal Container

/app         writable
/tmp         writable
/etc         maybe writable
/logs        writable
```

Safer container:

```text
Read-only root filesystem

/app         read-only
/etc         read-only
/tmp         tmpfs only if needed
/logs        stdout/stderr
```

Docker run:

```bash
docker run \
  --read-only \
  --tmpfs /tmp \
  -p 8080:8080 \
  order-service:1.0
```

Compose:

```yaml
services:
  order-service:
    image: order-service:1.0
    read_only: true
    tmpfs:
      - /tmp
```

ASCII:

```text
Attacker tries to write malware
        |
        v
+-------------------+
| root FS read-only |
+---------+---------+
          |
          v
      Write denied
```

---

# 13. Logs Should Go To stdout

Bad pattern:

```text
Container writes logs to /var/log/app.log
```

Better pattern:

```text
Spring Boot logs -> stdout/stderr -> Docker log driver -> collector
```

Flow:

```text
Spring Boot
    |
    v
stdout/stderr
    |
    v
Docker logging driver
    |
    v
Fluent Bit / Loki / ELK / Cloud Logging
```

Spring Boot `application.yml`:

```yaml
logging:
  level:
    root: INFO
    com.example: DEBUG
```

Do not configure file logging inside containers unless there is a very specific reason.

---

# 14. Linux Capabilities Mental Model

Root power is split into smaller permissions called capabilities.

```text
Old model:
root = all powers

Container model:
root = selected powers only
```

Examples:

```text
CAP_NET_ADMIN       change network settings
CAP_SYS_ADMIN       very powerful, avoid
CAP_CHOWN           change file ownership
CAP_NET_BIND_SERVICE bind low ports
```

Safer Docker run:

```bash
docker run \
  --cap-drop ALL \
  --cap-add NET_BIND_SERVICE \
  order-service:1.0
```

Most Spring Boot apps do not need special capabilities.

Mental model:

```text
Capability = key to one restricted room
Drop keys you do not need
```

---

# 15. Seccomp, AppArmor, SELinux

Containers share the host kernel, so syscall control matters.

```text
Application
    |
    v
System calls
    |
    v
Linux Kernel
```

Seccomp filters dangerous syscalls:

```text
App asks kernel: "let me do X"
        |
        v
Seccomp policy checks
        |
        +--> allowed
        +--> denied
```

AppArmor/SELinux add mandatory access control:

```text
Process wants file/network/action
        |
        v
Security profile decides
```

Production rule:

```text
Use Docker defaults as baseline.
Tighten profiles for high-risk workloads.
Never disable security profiles casually.
```

---

# 16. Docker Socket Is Extremely Dangerous

Mounting Docker socket:

```yaml
volumes:
  - /var/run/docker.sock:/var/run/docker.sock
```

This often gives container control over Docker daemon.

Danger flow:

```text
Container
   |
   v
/var/run/docker.sock
   |
   v
Docker daemon
   |
   v
Start privileged container
   |
   v
Host compromise risk
```

Real world analogy:

```text
You gave a hotel guest the master key room.
```

Avoid unless absolutely required. If required, isolate, monitor, and use least privilege alternatives.

---

# 17. Network Security

A common mistake is putting every service on one flat network.

Bad:

```text
frontend -> postgres
frontend -> redis
frontend -> internal-admin
worker   -> postgres
anything -> anything
```

Better:

```text
                public_net
                    |
                    v
              api-gateway
                    |
                backend_net
              /     |      \
             v      v       v
          order   user    payment
             |      |       |
             +------+-------+
                    |
                 data_net
                    |
             +------+------+
             |             |
             v             v
          postgres       redis
```

Compose example:

```yaml
networks:
  public_net:
  backend_net:
  data_net:

services:
  gateway:
    image: gateway:1.0
    ports:
      - "8080:8080"
    networks:
      - public_net
      - backend_net

  order-service:
    image: order-service:1.0
    networks:
      - backend_net
      - data_net

  postgres:
    image: postgres:16
    networks:
      - data_net
```

Rule:

```text
Only expose the edge service to the host.
Internal services talk on private Docker networks.
```

---

# 18. Port Exposure Mistakes

Dockerfile `EXPOSE` is documentation. Compose or `docker run -p` publishes ports.

Bad production compose:

```yaml
postgres:
  ports:
    - "5432:5432"
redis:
  ports:
    - "6379:6379"
```

This exposes databases to the host network.

Better:

```yaml
postgres:
  expose:
    - "5432"
redis:
  expose:
    - "6379"
```

Mental model:

```text
ports  = open building door to outside
expose = label internal room door
```

---

# 19. Volume Security

Volumes can persist data, but they also increase risk.

Bad mount:

```bash
docker run -v /:/host ubuntu
```

This gives access to the host filesystem.

Safer:

```bash
docker run -v app_data:/data order-service:1.0
```

Read-only bind mount for config:

```yaml
volumes:
  - ./config/application-prod.yml:/app/config/application.yml:ro
```

ASCII:

```text
Container
   |
   +-- safe named volume: /data
   |
   +-- read-only config: /app/config/application.yml
   |
   x-- host root mount: avoid
```

---

# 20. Image Scanning

Scanning finds known vulnerable packages.

Pipeline:

```text
Developer Push
      |
      v
Build Image
      |
      v
Scan Image
      |
      +--> critical CVE? fail build
      |
      v
Push Registry
      |
      v
Deploy
```

Example commands:

```bash
docker scout cves order-service:1.0
trivy image order-service:1.0
```

Scanning does not prove safety. It only finds known issues.

```text
Scanner finds known vulnerable components.
Scanner does not find bad business logic.
```

---

# 21. Supply Chain Security

Your image depends on many things:

```text
Base Image
   |
Maven Dependencies
   |
Build Plugins
   |
Application Code
   |
Container Registry
   |
Runtime Cluster
```

Attackers may target any layer.

Controls:

```text
Pin base image versions
Use trusted registries
Generate SBOM
Scan dependencies
Sign images
Restrict who can push latest
Avoid random curl | sh scripts
```

Bad:

```dockerfile
FROM openjdk:latest
```

Better:

```dockerfile
FROM eclipse-temurin:21.0.3_9-jre
```

Best in strict production:

```text
Pin by digest after validation
```

---

# 22. Image Tags: Latest Is Not A Version

Bad deployment:

```yaml
image: order-service:latest
```

Problem:

```text
Today latest = build A
Tomorrow latest = build B
Rollback becomes unclear
Nodes may run different images
```

Better:

```yaml
image: order-service:1.4.7
```

Even better:

```yaml
image: order-service@sha256:...
```

Mental model:

```text
latest = "some box from warehouse"
version/digest = "exact sealed box number"
```

---

# 23. Spring Boot Actuator Security

Actuator is useful, but dangerous if exposed blindly.

Bad:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

Risk:

```text
/env may expose config
/heapdump may expose memory/secrets
/loggers can change logging
```

Better:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      probes:
        enabled: true
```

Flow:

```text
Public Users ---> /api/orders
Internal Ops ---> /actuator/health, /metrics
```

Keep admin endpoints internal and authenticated.

---

# 24. Java Memory And Container Limits

Security includes stability. A container that ignores memory limits can be killed.

```text
Container Memory Limit: 512MB
        |
        v
JVM heap + metaspace + threads + native memory
        |
        v
OOMKilled if too large
```

Safer options:

```bash
java -XX:MaxRAMPercentage=70 -jar app.jar
```

Dockerfile:

```dockerfile
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70", "-jar", "app.jar"]
```

Mental model:

```text
Do not let JVM eat the whole container.
Leave room for non-heap memory.
```

---

# 25. Health Checks Are Security Adjacent

A broken container may still be running.

```text
Process alive != service healthy
```

Dockerfile:

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1
```

If using distroless, health check tools may not exist. Use orchestrator-level probes in Kubernetes.

Compose:

```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 3s
  retries: 3
```

Flow:

```text
Health check fails
      |
      v
Container marked unhealthy
      |
      v
Orchestrator avoids / restarts it
```

---

# 26. Kubernetes Security Connection

Docker security maps directly to Kubernetes:

```text
Docker                         Kubernetes
------                         ----------
USER appuser                   runAsNonRoot
read_only true                 readOnlyRootFilesystem
cap-drop                       capabilities.drop
secrets                        Secret volume/env
network isolation              NetworkPolicy
resource limit                 resources.limits
```

Kubernetes securityContext:

```yaml
securityContext:
  runAsNonRoot: true
  runAsUser: 10001
  readOnlyRootFilesystem: true
  allowPrivilegeEscalation: false
  capabilities:
    drop:
      - ALL
```

Pod flow:

```text
Pod starts
  |
  +--> container user checked
  +--> capabilities applied
  +--> filesystem mode applied
  +--> app starts with restricted permissions
```

---

# 27. Production Incident: Secret Baked Into Image

Story:

A team added this during debugging:

```dockerfile
ENV DB_PASSWORD=temporary-prod-password
```

They later changed the compose file but forgot the old image layer. The image was pushed to a shared registry. Security scan did not catch it because it was not a CVE. Later, someone noticed the secret in image history.

Failure diagram:

```text
Dockerfile ENV secret
        |
        v
Image layer
        |
        v
Registry
        |
        v
Anyone with pull access can inspect
```

Fix:

```text
Rotate secret immediately
Remove image from registry
Rebuild clean image
Use runtime secrets
Add secret scanning in CI
```

Lesson:

```text
Never put secrets in Dockerfile.
Not even temporarily.
```

---

# 28. Production Incident: Database Exposed By Compose

Bad compose:

```yaml
postgres:
  image: postgres:16
  ports:
    - "5432:5432"
```

This was convenient locally. Later the same compose pattern was reused on a cloud VM. PostgreSQL became reachable from outside.

Better:

```yaml
postgres:
  image: postgres:16
  expose:
    - "5432"
  networks:
    - data_net
```

Only gateway/API should publish ports.

```text
Internet
   |
   v
Gateway :8080   allowed
Postgres :5432  blocked from public
Redis :6379     blocked from public
```

---

# 29. Debugging Security Problems

Security debugging is asking: what does the container have permission to do?

Commands:

```bash
docker inspect order-service

docker exec -it order-service id

docker exec -it order-service sh

docker exec -it order-service ls -lah /app

docker exec -it order-service cat /proc/1/status
```

Check capabilities:

```bash
docker exec order-service grep Cap /proc/1/status
```

Check mounts:

```bash
docker inspect order-service --format '{{json .Mounts}}'
```

Check published ports:

```bash
docker ps
```

Mental flow:

```text
Symptom
  |
  +--> permission denied? check USER + filesystem
  +--> secret missing? check env/secret mount
  +--> cannot write temp? check read-only + tmpfs
  +--> exposed publicly? check ports + firewall
  +--> CVE found? check base image + package
```

---

# 30. Secure Compose Stack Example

```yaml
version: "3.9"

services:
  gateway:
    image: gateway:1.0.0
    ports:
      - "8080:8080"
    networks:
      - public_net
      - backend_net
    read_only: true
    tmpfs:
      - /tmp
    cap_drop:
      - ALL

  order-service:
    image: order-service:1.0.0
    networks:
      - backend_net
      - data_net
    read_only: true
    tmpfs:
      - /tmp
    cap_drop:
      - ALL
    environment:
      DB_PASSWORD_FILE: /run/secrets/db_password
    secrets:
      - db_password

  postgres:
    image: postgres:16
    networks:
      - data_net
    volumes:
      - pgdata:/var/lib/postgresql/data
    secrets:
      - db_password
    expose:
      - "5432"

networks:
  public_net:
  backend_net:
  data_net:

volumes:
  pgdata:

secrets:
  db_password:
    file: ./secrets/db_password.txt
```

Diagram:

```text
Internet
   |
   v
Gateway public port 8080
   |
backend_net
   |
Order Service
   |
data_net
   |
Postgres private only
```

---

# 31. CI/CD Security Pipeline

```text
Developer Commit
      |
      v
Unit Tests
      |
      v
Dependency Scan
      |
      v
Docker Build
      |
      v
Image Scan
      |
      v
Secret Scan
      |
      v
Sign Image
      |
      v
Push Registry
      |
      v
Deploy Only Approved Image
```

Failure gates:

```text
Critical CVE       -> fail
Secret detected    -> fail
Unsigned image     -> fail
latest tag         -> fail in production
Root user          -> fail policy
Privileged mode    -> fail policy
```

---

# 32. Strong Interview Answers

## What is container security?

Container security is reducing the blast radius of a Linux process running in an isolated environment. It includes secure images, non-root users, filesystem restrictions, capability reduction, secret management, network isolation, scanning, and runtime policies.

## Why should containers not run as root?

If the application is compromised, root inside the container gives the attacker more control over files, processes, and mounted volumes. Non-root does not solve every problem, but it reduces blast radius.

## Is Docker a VM-level security boundary?

No. Containers share the host kernel. They provide strong process isolation, but not the same boundary as separate virtual machines. For highly untrusted workloads, stronger isolation such as VMs or sandboxed runtimes may be needed.

## Why is mounting docker.sock dangerous?

Because access to Docker socket can allow the container to control the Docker daemon, start privileged containers, mount host paths, and potentially compromise the host.

## How do you secure a Spring Boot Docker image?

Use multi-stage builds, ship only the runtime artifact, run as non-root, avoid secrets in the image, prefer slim/distroless runtime images, expose only required ports, use read-only filesystem where possible, configure memory limits, and scan the final image.

---

# 33. Production Security Checklist

```text
Image
[ ] Use trusted base image
[ ] Avoid latest tag
[ ] Multi-stage build
[ ] Remove build tools from runtime
[ ] Scan image CVEs
[ ] Generate SBOM if required

Runtime
[ ] Run as non-root
[ ] Drop capabilities
[ ] No privileged mode
[ ] No docker.sock mount
[ ] Read-only filesystem if possible
[ ] tmpfs only for required temp paths

Secrets
[ ] No secrets in Dockerfile
[ ] No secrets committed to Git
[ ] Runtime secret injection
[ ] Rotate leaked secrets immediately

Network
[ ] Publish only gateway/API port
[ ] Keep DB/Redis private
[ ] Separate networks by trust level
[ ] Use TLS where required

Application
[ ] Secure actuator endpoints
[ ] Validate inputs
[ ] Use least-privilege DB user
[ ] Log to stdout, not local files

Operations
[ ] CI scan gates
[ ] Registry access control
[ ] Signed images for production
[ ] Monitor unusual container behavior
```

---

# 34. One Picture To Remember

```text
                  Secure Container Deployment

                         Internet
                            |
                            v
                    +---------------+
                    | API Gateway   |
                    | only exposed  |
                    +-------+-------+
                            |
                         backend_net
                            |
              +-------------+-------------+
              |                           |
              v                           v
      +----------------+          +----------------+
      | order-service  |          | user-service   |
      | non-root       |          | non-root       |
      | read-only FS   |          | read-only FS   |
      | cap-drop ALL   |          | cap-drop ALL   |
      | secrets runtime|          | secrets runtime|
      +-------+--------+          +--------+-------+
              |                            |
              +-------------+--------------+
                            |
                         data_net
                            |
              +-------------+-------------+
              |                           |
              v                           v
        +-----------+               +-----------+
        | Postgres  |               | Redis     |
        | private   |               | private   |
        | volume    |               | password  |
        +-----------+               +-----------+
```

Rule:

```text
Do not trust the container.
Limit the container.
Watch the container.
Rebuild the container safely.
```

---

# 35. Final Takeaways

1. A container is a Linux process, not a magic security box.
2. Security means reducing what the process can see, do, write, and reach.
3. Non-root containers reduce blast radius.
4. Multi-stage builds reduce runtime attack surface.
5. Never bake secrets into images.
6. Do not mount Docker socket unless you fully understand the risk.
7. Keep databases and Redis private inside Docker networks.
8. Prefer read-only filesystems for stateless services.
9. Scan images, but remember scanning is not complete security.
10. Secure defaults must be automated in CI/CD, not remembered manually.
