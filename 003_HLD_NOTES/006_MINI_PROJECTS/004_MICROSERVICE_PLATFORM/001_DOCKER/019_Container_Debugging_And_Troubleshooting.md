# 019_Container_Debugging_And_Troubleshooting

> MiniDocker Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Debugging • Do Not Memorize

---

# 1. Why Container Debugging Exists

A container looks simple from outside:

```text
docker run my-app
```

But production failures are rarely simple.

A Spring Boot service may fail because:

```text
Application bug
Wrong environment variable
Wrong port
Wrong DNS name
Container exited
Image missing file
Volume permission issue
Database unreachable
Redis password mismatch
Memory limit killed JVM
Healthcheck failed
```

The beginner mistake is to memorize commands:

```bash
docker logs
docker ps
docker exec
```

The senior engineer thinks in layers:

```text
User Request
   |
   v
Host Port
   |
   v
Container Network
   |
   v
Container Process
   |
   v
Application Runtime
   |
   v
Dependency: DB / Redis / Kafka
   |
   v
Disk / Volume / Memory / CPU
```

Debugging is not command memorization. Debugging is finding where the chain breaks.

---

# 2. Not-To-Memorize Mental Model

Do not memorize Docker debugging as random commands.

Use this model:

```text
Container = Small rented shop
Image     = Shop blueprint
Process   = Worker inside shop
Port      = Front door
Volume    = Storage room
Network   = Road connecting shops
Logs      = CCTV camera
Env vars  = Instructions given to worker
Healthcheck = Security guard asking: are you alive?
```

If customers cannot buy from the shop, you ask:

```text
1. Is the shop open?
2. Is the worker alive?
3. Is the front door open?
4. Is the road connected?
5. Are instructions correct?
6. Is the storage room accessible?
7. Is the supplier reachable?
```

Docker equivalent:

```text
1. docker ps
2. docker logs
3. docker port / docker inspect
4. docker network inspect
5. docker inspect env
6. docker volume inspect
7. curl / nc / redis-cli / psql
```

This is the debugging brain.

---

# 3. One Picture To Remember

```text
                    USER REQUEST
                         |
                         v
                  localhost:8080
                         |
             +-----------+-----------+
             |       HOST MACHINE    |
             |                       |
             |  Port Mapping         |
             |  8080 -> 8080         |
             |                       |
             |  +-----------------+  |
             |  | Container       |  |
             |  |                 |  |
             |  | PID 1: java     |  |
             |  | Port: 8080      |  |
             |  | Env: DB_HOST    |  |
             |  | Logs: stdout    |  |
             |  +--------+--------+  |
             |           |           |
             |           v           |
             |     Docker Network    |
             |           |           |
             |      Postgres/Redis   |
             +-----------------------+
```

When debugging, move from top to bottom.

---

# 4. Debugging Flow: The 7-Layer Container Stack

```text
Layer 7: Application logic
Layer 6: Runtime config / env vars
Layer 5: Process and PID
Layer 4: Container filesystem / volume
Layer 3: Container network / DNS / ports
Layer 2: Docker image / entrypoint
Layer 1: Host resources / Docker daemon
```

Use the flow:

```text
Symptom
  |
  v
Which layer can cause this?
  |
  v
Check cheapest evidence first
  |
  v
Confirm or eliminate
  |
  v
Move deeper
```

Example symptom:

```text
curl localhost:8080 fails
```

Possible layers:

```text
Host port not mapped
Container not running
Spring Boot not listening
Wrong server.port
Healthcheck restarting container
JVM crashed
Firewall issue
```

Never jump directly to rebuilding the image. First locate the broken layer.

---

# 5. First Command: What Is Running?

```bash
docker ps
```

Shows only running containers.

```bash
docker ps -a
```

Shows running and stopped containers.

Mental model:

```text
docker ps     = shops currently open
docker ps -a  = all shops, including closed ones
```

Output style:

```text
CONTAINER ID   IMAGE        STATUS                     PORTS
abc123         order-app    Up 10 seconds              0.0.0.0:8080->8080/tcp
def456         user-app     Exited (1) 30 seconds ago
```

Interpretation:

```text
Up             = process alive
Exited (0)     = process completed normally
Exited (1)     = app crashed
Restarting     = crash loop
Up unhealthy   = process alive but healthcheck failed
```

Important production mindset:

```text
A running container does not mean a working application.
A healthy container does not always mean correct business behavior.
But a stopped container definitely explains connection failures.
```

---

# 6. Container Lifecycle ASCII

```text
Image
  |
  | docker run
  v
Created Container
  |
  | start process
  v
Running
  |
  +---- app exits 0 ----> Exited Success
  |
  +---- app exits 1 ----> Exited Failure
  |
  +---- OOM killed -----> Exited 137
  |
  +---- docker stop ----> Exited 143
```

Common exit codes:

```text
0   = normal completion
1   = application error
125 = Docker run command failed
126 = command cannot execute
127 = command not found
137 = killed, often OOM
143 = terminated by SIGTERM
```

Spring Boot example:

```text
Exited (1)
```

Often means:

```text
Missing env var
Database connection failed during startup
Port already used inside container
Invalid application.yml
ClassNotFoundException
```

---

# 7. Logs Are The First CCTV Camera

```bash
docker logs order-service
```

Follow live logs:

```bash
docker logs -f order-service
```

Last 100 lines:

```bash
docker logs --tail=100 order-service
```

With timestamps:

```bash
docker logs -f --timestamps order-service
```

Mental model:

```text
Container stdout/stderr
        |
        v
Docker log driver
        |
        v
docker logs
```

ASCII:

```text
Spring Boot logback
      |
      v
System.out / stderr
      |
      v
Docker captures stream
      |
      v
json-file / journald / fluentd
      |
      v
docker logs
```

Production rule:

```text
Containerized apps should log to stdout/stderr.
Do not rely only on files inside the container.
```

---

# 8. Spring Boot Startup Failure Example

Bad `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:5432/orders
    username: ${DB_USER}
    password: ${DB_PASSWORD}
```

Run without env:

```bash
docker run order-service
```

Logs:

```text
Could not resolve placeholder 'DB_HOST'
Application run failed
```

Fix:

```bash
docker run \
  -e DB_HOST=postgres \
  -e DB_USER=orders \
  -e DB_PASSWORD=secret \
  order-service
```

Do not memorize the error. Understand the chain:

```text
Spring Boot config reads DB_HOST
        |
        v
Docker environment does not contain DB_HOST
        |
        v
Placeholder resolution fails
        |
        v
JVM exits
        |
        v
Container exits
```

---

# 9. Inspect: The Container X-Ray

```bash
docker inspect order-service
```

This is too large, so filter it.

Container IP:

```bash
docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' order-service
```

Environment variables:

```bash
docker inspect -f '{{json .Config.Env}}' order-service
```

Restart count:

```bash
docker inspect -f '{{.RestartCount}}' order-service
```

Exit code:

```bash
docker inspect -f '{{.State.ExitCode}}' order-service
```

OOM killed:

```bash
docker inspect -f '{{.State.OOMKilled}}' order-service
```

Port bindings:

```bash
docker inspect -f '{{json .NetworkSettings.Ports}}' order-service
```

Mental model:

```text
docker logs    = what app said
docker inspect = what Docker knows
```

---

# 10. Debugging Port Mapping

Symptom:

```text
curl localhost:8080 fails
```

Check running container:

```bash
docker ps
```

Look for:

```text
0.0.0.0:8080->8080/tcp
```

Correct:

```bash
docker run -p 8080:8080 order-service
```

Wrong:

```bash
docker run order-service
```

No exposed host port.

ASCII:

```text
Wrong

Browser
  |
  v
localhost:8080
  |
  X no host port mapping
  |
Container:8080 hidden
```

Correct:

```text
Browser
  |
  v
Host:8080
  |
  v
Docker NAT
  |
  v
Container:8080
  |
  v
Spring Boot
```

---

# 11. Spring Boot Port Mismatch

Dockerfile:

```dockerfile
EXPOSE 8080
```

Run:

```bash
docker run -p 8080:8080 order-service
```

But `application.yml`:

```yaml
server:
  port: 9090
```

Problem:

```text
Host:8080 -> Container:8080
                    |
                    X Spring Boot is listening on 9090
```

Fix one side.

Option 1:

```bash
docker run -p 8080:9090 order-service
```

Option 2:

```yaml
server:
  port: 8080
```

Debug inside:

```bash
docker exec -it order-service sh
netstat -tulnp
```

If netstat unavailable:

```bash
ss -tulnp
```

---

# 12. EXPOSE Does Not Publish Ports

Common misunderstanding:

```dockerfile
EXPOSE 8080
```

This does not open the app to your host.

Mental model:

```text
EXPOSE = label on the shop door saying "service uses 8080"
-p     = actually opens the street entrance
```

ASCII:

```text
EXPOSE only

Host
  |
  X
Container says: I use 8080
```

With `-p`:

```text
Host:8080
  |
  v
Container:8080
```

Interview answer:

```text
EXPOSE documents the intended container port. Port publishing is done by docker run -p or Compose ports.
```

---

# 13. Exec: Entering The Container

```bash
docker exec -it order-service sh
```

If bash exists:

```bash
docker exec -it order-service bash
```

Mental model:

```text
docker exec = enter the running shop and inspect from inside
```

Inside checks:

```bash
pwd
ls -lah
env
ps aux
cat /etc/resolv.conf
cat /etc/hosts
curl localhost:8080/actuator/health
```

Important:

```text
Minimal images may not contain bash, curl, ps, netstat, or package managers.
```

For distroless images, you often need:

```text
debug sidecar
temporary debug container
logs/metrics/traces
```

---

# 14. Debugging Image Contents

Symptom:

```text
Error: Unable to access jarfile app.jar
```

Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/order-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Maybe the JAR path is wrong.

Check image by shell:

```bash
docker run --rm -it --entrypoint sh order-service
ls -lah /app
```

ASCII:

```text
Dockerfile COPY
      |
      v
Image filesystem
      |
      v
Container starts
      |
      v
ENTRYPOINT expects /app/app.jar
      |
      X file missing
```

Fix Maven build:

```bash
mvn clean package -DskipTests
```

Check target:

```bash
ls target/*.jar
```

---

# 15. ENTRYPOINT vs CMD Debugging

Dockerfile:

```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Command override:

```bash
docker run order-service --spring.profiles.active=prod
```

Final process:

```text
java -jar app.jar --spring.profiles.active=prod
```

If Dockerfile uses shell form:

```dockerfile
ENTRYPOINT java -jar app.jar
```

Problems:

```text
Signal handling weaker
PID 1 behavior less clean
Argument passing can surprise you
```

Recommended:

```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Debug process:

```bash
docker inspect -f '{{json .Config.Entrypoint}} {{json .Config.Cmd}}' order-service
```

---

# 16. PID 1 Mental Model

Inside a container, the main process is PID 1.

```text
Container Namespace

PID 1 -> java -jar app.jar
```

If PID 1 exits, the container exits.

ASCII:

```text
Container life
    |
    v
PID 1 alive? ---- no ----> container stopped
    |
   yes
    |
    v
container running
```

Spring Boot container:

```text
java process alive = container alive
java process crash = container exits
```

Check process:

```bash
docker exec -it order-service ps aux
```

---

# 17. CrashLoop Style Debugging In Docker Compose

Compose file:

```yaml
services:
  order-service:
    image: order-service
    restart: always
```

Symptom:

```bash
docker ps
```

Shows:

```text
Restarting (1) 5 seconds ago
```

Debug:

```bash
docker logs --tail=200 order-service

docker inspect -f '{{.RestartCount}}' order-service
```

ASCII:

```text
Start
  |
  v
Spring Boot reads config
  |
  X fails
  |
  v
Container exits
  |
  v
Docker restart policy restarts
  |
  v
Repeat forever
```

Rule:

```text
Restart policy hides the failure but does not fix it.
Always read the first error in logs.
```

---

# 18. Network Debugging: Can Containers Talk?

Symptom:

```text
Order service cannot call user-service
```

Architecture:

```text
order-service ---> user-service
```

Wrong code:

```java
String url = "http://localhost:8081/users/1";
```

Why wrong?

Inside order-service container:

```text
localhost = order-service container itself
```

Correct:

```java
String url = "http://user-service:8080/users/1";
```

ASCII:

```text
Inside order-service

localhost
   |
   v
order-service itself

user-service
   |
   v
Docker DNS
   |
   v
user-service container IP
```

---

# 19. Docker DNS Debugging

Enter container:

```bash
docker exec -it order-service sh
```

Check DNS config:

```bash
cat /etc/resolv.conf
```

Resolve service name:

```bash
getent hosts user-service
```

If `getent` unavailable:

```bash
nslookup user-service
```

If nslookup unavailable, use temporary debug container:

```bash
docker run --rm -it --network my-network nicolaka/netshoot
```

Then:

```bash
dig user-service
curl http://user-service:8080/actuator/health
```

Mental model:

```text
Service name
   |
   v
Docker embedded DNS
   |
   v
Container IP
```

---

# 20. Network Mismatch Failure

Compose:

```yaml
services:
  order-service:
    networks: [backend]

  user-service:
    networks: [frontend]

networks:
  backend:
  frontend:
```

Problem:

```text
order-service and user-service live in different cities.
DNS cannot find each other.
```

ASCII:

```text
backend network              frontend network
+---------------+            +---------------+
| order-service |            | user-service  |
+---------------+            +---------------+
        |                            |
        X cannot resolve/reach        |
```

Fix:

```yaml
services:
  order-service:
    networks: [app-net]
  user-service:
    networks: [app-net]

networks:
  app-net:
```

Debug:

```bash
docker network inspect app-net
```

---

# 21. Compose Service Name Debugging

Compose service:

```yaml
services:
  postgres-db:
    image: postgres:16
```

Connection string:

```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres-db:5432/orders
```

Wrong:

```yaml
jdbc:postgresql://localhost:5432/orders
```

Wrong unless database is inside same container, which it should not be.

ASCII:

```text
order-service container
       |
       | jdbc:postgresql://postgres-db:5432/orders
       v
Docker DNS resolves postgres-db
       |
       v
postgres container
```

---

# 22. Java WebClient Debug Example

Correct WebClient config:

```java
@Configuration
public class ClientConfig {

    @Bean
    WebClient userWebClient(WebClient.Builder builder,
                            @Value("${clients.user-service.base-url}") String baseUrl) {
        return builder
                .baseUrl(baseUrl)
                .build();
    }
}
```

`application-docker.yml`:

```yaml
clients:
  user-service:
    base-url: http://user-service:8080
```

Service:

```java
@Service
public class UserClient {
    private final WebClient userWebClient;

    public UserClient(WebClient userWebClient) {
        this.userWebClient = userWebClient;
    }

    public Mono<String> getUser(String id) {
        return userWebClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(2));
    }
}
```

Debug idea:

```text
If this fails in Docker but works locally,
check base URL first.
```

Local:

```yaml
http://localhost:8081
```

Docker:

```yaml
http://user-service:8080
```

---

# 23. Database Connection Debugging

Symptom:

```text
Connection refused to postgres:5432
```

Possible meanings:

```text
Postgres container not running
Wrong service name
Wrong network
Postgres still starting
Wrong port
Postgres listening but DB/user missing
Password mismatch
```

Debug flow:

```text
docker ps
  |
  v
Is postgres Up?
  |
  v
docker logs postgres
  |
  v
Is database ready?
  |
  v
From app network: can we connect?
```

Command:

```bash
docker exec -it order-service sh
nc -vz postgres 5432
```

If no `nc`:

```bash
docker run --rm -it --network app-net postgres:16 psql -h postgres -U orders -d orders
```

---

# 24. Redis Connection Debugging

Spring Boot config:

```yaml
spring:
  data:
    redis:
      host: redis
      port: 6379
```

Debug:

```bash
docker exec -it redis redis-cli ping
```

Expected:

```text
PONG
```

From app network:

```bash
docker run --rm -it --network app-net redis:7 redis-cli -h redis ping
```

ASCII:

```text
order-service
   |
   | redis:6379
   v
Docker DNS
   |
   v
redis container
   |
   v
PONG
```

Common issue:

```text
NOAUTH Authentication required
```

Means Redis is reachable, but credentials are wrong or missing.

---

# 25. Startup Ordering vs Readiness

Compose:

```yaml
depends_on:
  - postgres
```

This means:

```text
Start postgres container before app container
```

It does not always mean:

```text
Postgres is ready to accept connections
```

ASCII:

```text
Postgres container started
       |
       v
Postgres initializing data directory
       |
       v
App starts too early
       |
       X connection refused
       |
       v
App exits
```

Better:

```yaml
services:
  postgres:
    image: postgres:16
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U orders"]
      interval: 5s
      timeout: 3s
      retries: 10

  order-service:
    depends_on:
      postgres:
        condition: service_healthy
```

Also implement retry in app.

---

# 26. Spring Boot Retry-Friendly Startup

Avoid fragile startup that dies instantly when DB is late.

Example with Hikari settings:

```yaml
spring:
  datasource:
    hikari:
      initialization-fail-timeout: 60000
      connection-timeout: 30000
      maximum-pool-size: 10
```

Mental model:

```text
Database late by 15 seconds
       |
Fragile app exits immediately
       |
Container restarts

Retry-friendly app waits/retries
       |
Database becomes ready
       |
App starts successfully
```

Production note:

```text
Container orchestration expects services to tolerate dependency startup delay.
```

---

# 27. Healthcheck Debugging

Dockerfile:

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
```

Problem:

```text
Minimal image may not contain curl.
```

Container becomes unhealthy even if app works.

Debug:

```bash
docker inspect -f '{{json .State.Health}}' order-service
```

Better Compose healthcheck:

```yaml
healthcheck:
  test: ["CMD-SHELL", "wget -qO- http://localhost:8080/actuator/health || exit 1"]
  interval: 10s
  timeout: 3s
  retries: 5
```

Or use a base image with required tool, or app-level health integration.

ASCII:

```text
Docker healthcheck
      |
      v
Runs command inside container
      |
      +-- command missing -> unhealthy
      +-- endpoint fails  -> unhealthy
      +-- endpoint OK     -> healthy
```

---

# 28. Actuator Health Meaning

Spring Boot actuator:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

Endpoint:

```text
/actuator/health
```

Possible output:

```json
{
  "status": "DOWN",
  "components": {
    "db": { "status": "DOWN" },
    "redis": { "status": "UP" }
  }
}
```

Interpretation:

```text
App process alive
HTTP server alive
Redis reachable
Database broken
```

This is better than guessing from `docker ps`.

---

# 29. Filesystem Debugging

Symptom:

```text
Application cannot write uploaded file
```

Error:

```text
Permission denied: /app/uploads
```

Check inside:

```bash
docker exec -it order-service sh
ls -lah /app
ls -lah /app/uploads
id
```

ASCII:

```text
Spring Boot
   |
   | write file
   v
/app/uploads
   |
   X permission denied
```

Dockerfile fix:

```dockerfile
RUN addgroup --system app && adduser --system --ingroup app app
RUN mkdir -p /app/uploads && chown -R app:app /app
USER app
```

Compose volume:

```yaml
volumes:
  - uploads-data:/app/uploads
```

---

# 30. Volume Debugging

List volumes:

```bash
docker volume ls
```

Inspect:

```bash
docker volume inspect uploads-data
```

Check mount in container:

```bash
docker inspect -f '{{json .Mounts}}' order-service
```

Mental model:

```text
Container filesystem = temporary apartment
Docker volume        = external storage room
```

ASCII:

```text
Container /app/uploads
          |
          v
Docker volume uploads-data
          |
          v
Host-managed storage path
```

Common mistake:

```bash
docker compose down -v
```

This removes volumes.

Production warning:

```text
Never casually run down -v against stateful local/dev data unless you intend to delete it.
```

---

# 31. Bind Mount Debugging

Compose:

```yaml
volumes:
  - ./config:/app/config
```

Problem:

```text
Works on my machine, fails on colleague machine.
```

Why?

```text
Bind mount depends on host path.
Different OS, permissions, path, file casing, line endings.
```

ASCII:

```text
Host folder ./config
      |
      v
Container /app/config
```

Debug:

```bash
pwd
ls -lah ./config

docker exec -it order-service ls -lah /app/config
```

Rule:

```text
Use bind mounts for development.
Use named volumes or external storage for stable runtime data.
```

---

# 32. Memory Debugging: Exit Code 137

Symptom:

```text
Container exits with code 137
```

Check:

```bash
docker inspect -f '{{.State.OOMKilled}}' order-service
```

If true:

```text
JVM used more memory than container limit.
Kernel killed it.
```

ASCII:

```text
Container memory limit: 512MB
        |
        v
JVM heap + metaspace + threads + native memory
        |
        v
Usage crosses limit
        |
        v
OOM kill
        |
        v
Exit 137
```

Run with limit:

```bash
docker run --memory=512m order-service
```

Safer JVM:

```bash
JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:InitialRAMPercentage=30"
```

Compose:

```yaml
environment:
  JAVA_TOOL_OPTIONS: "-XX:MaxRAMPercentage=70 -XX:InitialRAMPercentage=30"
```

---

# 33. CPU Debugging

Check live usage:

```bash
docker stats
```

Output:

```text
CONTAINER       CPU %     MEM USAGE / LIMIT
order-service   180%     420MiB / 512MiB
```

Interpretation:

```text
180% CPU means using 1.8 CPU cores.
```

Possible causes:

```text
Traffic spike
Infinite loop
GC pressure
Too many logs
Bad retry storm
Database timeout causing thread buildup
```

ASCII:

```text
Slow dependency
     |
     v
Threads wait
     |
     v
Requests pile up
     |
     v
CPU/context switching/logging increases
     |
     v
Latency explodes
```

Use app metrics:

```text
JVM threads
GC pauses
HTTP latency
DB pool usage
Redis latency
```

Docker stats alone is not enough.

---

# 34. Log Storm Debugging

Symptom:

```text
Disk fills quickly
Container slow
High CPU
```

Cause:

```text
App logs same error thousands of times per second.
```

ASCII:

```text
Dependency down
   |
   v
Retry loop
   |
   v
Log error every retry
   |
   v
Huge stdout logs
   |
   v
Docker json log grows
   |
   v
Disk pressure
```

Check log size:

```bash
docker inspect order-service | grep LogPath
```

Configure log rotation:

```yaml
services:
  order-service:
    logging:
      driver: json-file
      options:
        max-size: "10m"
        max-file: "3"
```

Application fix:

```text
Use bounded retries
Use backoff
Avoid logging full stack trace repeatedly for expected transient failures
```

---

# 35. Debugging With Temporary Containers

Sometimes app image is too minimal.

Use netshoot:

```bash
docker run --rm -it --network app-net nicolaka/netshoot
```

Useful commands:

```bash
dig user-service
curl -v http://user-service:8080/actuator/health
nc -vz postgres 5432
traceroute user-service
ss -tulnp
```

Mental model:

```text
Debug container = mobile engineer visiting the same network
```

ASCII:

```text
app-net network

+---------------+    +--------------+
| order-service |    | user-service |
+---------------+    +--------------+
        ^                    ^
        |                    |
        +------ netshoot ----+
```

This avoids installing tools inside production images.

---

# 36. Image Pull Debugging

Symptom:

```text
Unable to find image locally
pull access denied
```

Causes:

```text
Wrong image name
Wrong tag
Private registry auth missing
Image not pushed
Network problem
```

Commands:

```bash
docker images

docker pull my-registry/order-service:1.0.0

docker login my-registry
```

ASCII:

```text
Docker run
  |
  v
Image exists locally? -- yes --> start container
  |
 no
  v
Pull from registry
  |
  +-- auth fail
  +-- tag missing
  +-- network fail
```

Production rule:

```text
Use immutable tags or digests for release tracking.
Avoid relying only on latest.
```

---

# 37. Build Debugging

Symptom:

```text
Docker build slow or cache not used
```

Bad Dockerfile:

```dockerfile
COPY . .
RUN mvn package
```

Any source change invalidates dependency download cache.

Better:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /src
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /src/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

ASCII:

```text
pom.xml layer
   |
   v
dependency cache
   |
   v
source code layer
   |
   v
compile layer
```

Debug build layers:

```bash
docker build --progress=plain -t order-service .
```

---

# 38. Environment Profile Debugging

Spring Boot profiles:

```yaml
spring:
  profiles:
    active: docker
```

Run:

```bash
docker run -e SPRING_PROFILES_ACTIVE=docker order-service
```

Debug:

```bash
docker exec -it order-service env | grep SPRING
```

Log expectation:

```text
The following 1 profile is active: "docker"
```

Common failure:

```text
App accidentally runs with local profile inside Docker.
It tries localhost DB.
```

ASCII:

```text
Wrong profile
   |
   v
DB host = localhost
   |
   v
Inside container localhost = app itself
   |
   X DB unreachable
```

---

# 39. Compose Full Debug Stack Example

```yaml
services:
  order-service:
    build: ./order-service
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      DB_HOST: postgres
      REDIS_HOST: redis
      JAVA_TOOL_OPTIONS: "-XX:MaxRAMPercentage=70"
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_started
    networks:
      - app-net

  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: orders
      POSTGRES_USER: orders
      POSTGRES_PASSWORD: secret
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U orders"]
      interval: 5s
      timeout: 3s
      retries: 10
    networks:
      - app-net

  redis:
    image: redis:7
    networks:
      - app-net

networks:
  app-net:

volumes:
  postgres-data:
```

Debug commands:

```bash
docker compose ps
docker compose logs -f order-service
docker compose logs postgres
docker compose exec order-service env
docker compose exec order-service curl localhost:8080/actuator/health
docker network inspect $(basename "$PWD")_app-net
```

---

# 40. Request Failure Dry Run

Symptom:

```bash
curl http://localhost:8080/orders/1
```

Returns:

```text
Connection refused
```

Dry run:

```text
1. Browser/curl sends request to host:8080
2. Host checks if any process listens on 8080
3. Docker port mapping should forward to container
4. Container should listen on 8080
5. Spring Boot should handle route
```

Debug:

```bash
docker ps
```

If no port mapping:

```text
Fix -p or Compose ports.
```

If mapping exists:

```bash
docker logs order-service
```

If app says started on 9090:

```text
Fix port mismatch.
```

If app crashed:

```text
Fix startup error.
```

---

# 41. Dependency Failure Dry Run

Symptom:

```text
Order API returns 500
```

Logs:

```text
java.net.UnknownHostException: user-service
```

Dry run:

```text
Order receives request
   |
   v
Order calls http://user-service:8080
   |
   v
Docker DNS tries resolving user-service
   |
   X service not found in network
```

Debug:

```bash
docker network inspect app-net
```

Fix:

```text
Put both services in same Docker network.
Use Compose service name.
Avoid container_name dependency unless needed.
```

---

# 42. JVM Thread Dump From Container

When app is alive but stuck:

```bash
docker exec -it order-service jcmd 1 Thread.print
```

If `jcmd` exists. JRE images may not include it; JDK images do.

Alternative:

```bash
docker kill -s QUIT order-service
```

This sends SIGQUIT to JVM and prints thread dump to logs in many JVM setups.

Then:

```bash
docker logs order-service
```

Mental model:

```text
HTTP not responding
   |
   v
Maybe threads blocked
   |
   v
Thread dump shows where
```

Look for:

```text
Blocked DB calls
Deadlock
Waiting on Redis
Too many http-nio threads busy
```

---

# 43. Heap Dump Warning

Heap dumps are useful but dangerous.

```text
They can contain passwords, tokens, personal data, request payloads.
```

If needed:

```bash
docker exec order-service jcmd 1 GC.heap_dump /tmp/heap.hprof

docker cp order-service:/tmp/heap.hprof ./heap.hprof
```

Only do this with permission in production.

ASCII:

```text
JVM heap
  |
  v
heap.hprof
  |
  v
May contain sensitive data
```

Safer first checks:

```text
Metrics
GC logs
Memory usage
Thread dump
Endpoint latency
```

---

# 44. Production Story: The Localhost Bug

A team dockerized three Spring Boot services:

```text
order-service
user-service
payment-service
```

Everything worked locally without Docker.

Inside Docker, order-service failed:

```text
Connection refused: localhost:8081
```

Root cause:

```text
order-service used localhost to call user-service.
```

In local machine:

```text
localhost:8081 = user-service on host
```

Inside container:

```text
localhost:8081 = order-service container itself
```

Fix:

```yaml
clients:
  user-service:
    base-url: http://user-service:8080
```

Lesson:

```text
In containers, localhost means self, not another service.
```

---

# 45. Production Story: The Deleted Volume

Developer ran:

```bash
docker compose down -v
```

Local Postgres data disappeared.

Why?

```text
-v removes named volumes declared by Compose.
```

ASCII:

```text
postgres container
      |
      v
postgres-data volume
      |
      X docker compose down -v removed it
```

Prevention:

```text
Know difference between down and down -v.
Backup important volumes.
Use external volumes for important local state.
Never run destructive cleanup blindly.
```

---

# 46. Production Story: OOM Killed JVM

Symptom:

```text
Container randomly restarted under traffic.
```

Docker showed:

```text
Exit 137
OOMKilled=true
```

Cause:

```text
Container memory limit was 512MB.
JVM heap plus native memory exceeded limit.
```

Fix:

```yaml
environment:
  JAVA_TOOL_OPTIONS: >-
    -XX:MaxRAMPercentage=65
    -XX:+ExitOnOutOfMemoryError
```

Also fixed:

```text
DB pool too large
Too many request threads
Large response buffering
No pagination
```

Lesson:

```text
Memory debugging is application + JVM + container together.
```

---

# 47. Command Cheat Sheet By Symptom

```text
Container not reachable
  docker ps
  docker port <container>
  docker logs <container>

Container exited
  docker ps -a
  docker logs <container>
  docker inspect -f '{{.State.ExitCode}}' <container>

DNS failure
  docker network inspect <network>
  getent hosts <service>
  docker run --rm -it --network <network> nicolaka/netshoot

DB unreachable
  docker logs postgres
  nc -vz postgres 5432
  psql -h postgres -U user -d db

Memory issue
  docker stats
  docker inspect -f '{{.State.OOMKilled}}' <container>

Volume issue
  docker volume ls
  docker volume inspect <volume>
  docker inspect -f '{{json .Mounts}}' <container>
```

---

# 48. Debugging Decision Tree

```text
Problem reported
     |
     v
Is container running?
     |
     +-- no --> docker logs + exit code
     |
     +-- yes
          |
          v
Is port reachable from host?
          |
          +-- no --> check -p / ports / app listening port
          |
          +-- yes
               |
               v
Is app healthy?
               |
               +-- no --> actuator + dependency health
               |
               +-- yes
                    |
                    v
Is dependency reachable?
                    |
                    +-- no --> DNS/network/env
                    |
                    +-- yes
                         |
                         v
Check app logic, data, latency, metrics
```

---

# 49. Interview Answers

## What is your first step when a containerized app fails?

I first classify the failure. Is the container running, exited, restarting, or unhealthy? Then I check logs and inspect exit code, port mappings, environment variables, network, and dependency reachability. I avoid randomly rebuilding until I know which layer failed.

## Why does localhost break inside Docker?

Because localhost inside a container points to that container's own network namespace. To call another container, use the Docker Compose service name or Docker DNS name, not localhost.

## What does exit code 137 mean?

It usually means the process was killed, commonly because the container exceeded its memory limit and the kernel OOM killer terminated it.

## How do you debug DNS between containers?

I verify both containers are on the same Docker network, inspect the network, then use a debug container such as netshoot or commands like getent, nslookup, dig, curl, or nc to test name resolution and connectivity.

## Does EXPOSE publish a port?

No. EXPOSE documents the port used by the image. Publishing to the host requires `docker run -p` or Compose `ports`.

---

# 50. Final Production Checklist

```text
[ ] Check docker ps -a before guessing
[ ] Read logs with timestamps
[ ] Inspect exit code and OOMKilled
[ ] Verify host port mapping
[ ] Verify app listening port
[ ] Verify environment variables
[ ] Verify Docker network membership
[ ] Use service names, not container IPs
[ ] Use localhost only for same-container calls
[ ] Test dependencies from inside same network
[ ] Confirm volumes and permissions
[ ] Check memory and CPU with docker stats
[ ] Use actuator health for Spring Boot
[ ] Add bounded retries and backoff
[ ] Configure log rotation
[ ] Avoid destructive docker compose down -v
[ ] Use debug containers for minimal images
```

---

# 51. One Picture To Remember

```text
                 CONTAINER DEBUGGING MAP

User says: app is not working
             |
             v
+--------------------------------------------------+
| 1. Is container alive?       docker ps -a         |
| 2. Why failed?               docker logs          |
| 3. What did Docker see?      docker inspect       |
| 4. Is port open?             docker port / -p     |
| 5. Is app listening?         ss/netstat/curl      |
| 6. Is DNS working?           network inspect      |
| 7. Are deps reachable?       nc/psql/redis-cli    |
| 8. Is storage OK?            volume inspect       |
| 9. Is JVM healthy?           stats/thread dump    |
+--------------------------------------------------+
             |
             v
     Find broken layer, then fix.
```

Rule:

```text
Do not memorize Docker commands.
Understand the request path, process path, network path, and storage path.
Debugging is simply walking those paths until the truth appears.
```
