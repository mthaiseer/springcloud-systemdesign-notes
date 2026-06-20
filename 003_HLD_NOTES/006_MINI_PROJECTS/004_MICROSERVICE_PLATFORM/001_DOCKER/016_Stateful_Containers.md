# 016_Stateful_Containers.md

> MiniDocker Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Models • Do Not Memorize • Java/Spring Boot Where Useful

---

# 1. Why Stateful Containers Exist

A container is usually taught as something disposable:

```text
Create container
Run process
Delete container
Create again
```

That is true for many application services, but not for every workload.

A Spring Boot order-service can usually be killed and recreated because the important business data is stored somewhere else:

```text
Order Service Container
        |
        v
PostgreSQL / Redis / Kafka / S3
```

But what about PostgreSQL itself? What about Redis when persistence is enabled? What about a file-processing service that writes uploaded documents to disk?

Now the container is not just running code. It is also carrying important state.

```text
Stateless container:
Code matters, container can die.

Stateful container:
Data matters, container death must not destroy data.
```

This chapter is about understanding the danger zone: running containers that manage data.

Do not memorize commands first. Understand this one idea:

```text
Container filesystem is temporary.
Business data must live outside the container lifecycle.
```

---

# 2. Not-To-Memorize Model

Do not memorize:

```text
volume
bind mount
tmpfs
named volume
anonymous volume
StatefulSet
PersistentVolume
```

Use this mental model instead:

```text
Container = rented hotel room
Image     = room design blueprint
Container writable layer = temporary table inside room
Volume    = locker assigned to you
Bind mount = your own house folder connected to hotel room
Database data = passport / gold / legal document
```

If you leave the hotel room, the temporary table is cleaned.

```text
Delete container
      |
      v
Writable layer gone
```

But the locker can remain.

```text
Delete container
      |
      v
Volume still exists
```

That is the entire stateful container story.

---

# 3. One Picture To Remember

```text
                         Docker Host
+-------------------------------------------------------------+
|                                                             |
|  Container Writable Layer                                   |
|  +----------------------+                                   |
|  | /app                 |  temporary                         |
|  | /tmp                 |  disposable                        |
|  +----------------------+                                   |
|                                                             |
|  Persistent Volume                                          |
|  +----------------------+                                   |
|  | /var/lib/postgresql  |  survives container restart/delete  |
|  | /data/redis          |  real business state                |
|  +----------------------+                                   |
|                                                             |
+-------------------------------------------------------------+
```

Rule:

```text
Container dies.
Volume survives.
```

---

# 4. Stateless vs Stateful Containers

A stateless service does not depend on local disk to remember business facts.

```text
HTTP Request
    |
    v
Spring Boot API
    |
    +--> PostgreSQL
    +--> Redis
    +--> Kafka
    +--> S3
```

If the API container dies:

```text
New API container starts
        |
        v
Reads same external DB
```

A stateful container owns data locally.

```text
PostgreSQL Container
        |
        v
/var/lib/postgresql/data
```

If this path is inside the container writable layer and the container is deleted, the database is gone.

```text
Wrong:
Postgres data inside container layer
Container deleted
Data deleted
```

Correct:

```text
Postgres data mounted to volume
Container deleted
Volume remains
New Postgres container reuses same data
```

---

# 5. Container Writable Layer Problem

Every running container gets a writable layer on top of the image layers.

```text
Container View
+-----------------------------+
| Writable Layer              |  <-- changes made at runtime
+-----------------------------+
| Image Layer: app.jar        |
+-----------------------------+
| Image Layer: JDK/JRE        |
+-----------------------------+
| Image Layer: Linux base     |
+-----------------------------+
```

If the app writes a file:

```text
/app/logs/app.log
```

That file may live in the writable layer unless mounted elsewhere.

Problem:

```text
docker rm container
        |
        v
Writable layer removed
        |
        v
Runtime files lost
```

This is okay for temporary cache. It is not okay for database files, uploaded documents, audit records, or anything legally important.

---

# 6. Real World Analogy: Restaurant Kitchen

Imagine a restaurant kitchen.

```text
Chef       = process inside container
Kitchen    = container filesystem
Recipe     = Docker image
Fridge     = Docker volume
Orders     = user data
```

If the chef leaves, no problem.

If the kitchen is cleaned, temporary dirt and notes disappear. Fine.

But if all food stock and customer orders were stored on a disposable kitchen table, disaster.

Correct design:

```text
Disposable table  -> temp files
Fridge            -> persistent volume
Recipe book       -> image
Chef              -> container process
```

For production systems:

```text
Never store business truth on the disposable table.
```

---

# 7. Volume Mental Model

A Docker volume is storage managed by Docker and mounted into a container.

```text
Docker Volume
    |
    v
Mounted path inside container
```

Example:

```bash
docker volume create pgdata

docker run -d \
  --name postgres \
  -e POSTGRES_PASSWORD=secret \
  -v pgdata:/var/lib/postgresql/data \
  postgres:16
```

Visual:

```text
+--------------------------+       +-----------------------------+
| Docker Volume: pgdata    | ----> | Container path              |
| Stored on host by Docker |       | /var/lib/postgresql/data    |
+--------------------------+       +-----------------------------+
```

PostgreSQL thinks it is writing inside its container, but Docker redirects that path to persistent storage.

---

# 8. Bind Mount Mental Model

A bind mount connects a specific host folder into a container.

```bash
docker run -v /home/me/app-config:/config myapp
```

Visual:

```text
Host Folder                         Container Path
/home/me/app-config  -------------> /config
```

Bind mounts are common for development:

```text
Edit code on laptop
        |
        v
Container sees changes immediately
```

But in production, bind mounts can be risky because they depend on the exact host path.

```text
Works on Host A:
/opt/app/config exists

Fails on Host B:
/opt/app/config missing
```

Named volumes are more portable within Docker-managed environments. Bind mounts are more explicit and powerful, but easier to misuse.

---

# 9. Volume vs Bind Mount

```text
+----------------+----------------------------+----------------------------+
| Feature        | Volume                     | Bind Mount                 |
+----------------+----------------------------+----------------------------+
| Managed by     | Docker                     | User / host OS             |
| Portability    | Better                     | Host-path dependent        |
| Dev usage      | Good                       | Very common                |
| Prod DB usage  | Common                     | Possible, but careful      |
| Backup         | Docker volume backup       | Normal filesystem backup   |
| Risk           | Hidden location            | Permission/path issues     |
+----------------+----------------------------+----------------------------+
```

Mental shortcut:

```text
Volume     = Docker-managed locker
Bind mount = Exact host folder connected to container
```

---

# 10. Stateful Container Startup Flow

PostgreSQL with volume:

```text
1. Docker creates container
2. Docker mounts pgdata volume
3. Postgres process starts
4. Postgres checks /var/lib/postgresql/data
5. If empty: initialize database
6. If existing: reuse old database files
7. Container becomes ready
```

ASCII flow:

```text
Docker Engine
    |
    v
Mount Volume pgdata
    |
    v
Start postgres process
    |
    v
Check data directory
    |
    +--> Empty? initialize DB
    |
    +--> Existing? recover and start
```

This explains why deleting and recreating a Postgres container can still keep the data if the same volume is reused.

---

# 11. Dry Run: Wrong PostgreSQL Container

Command:

```bash
docker run -d --name pg-bad -e POSTGRES_PASSWORD=secret postgres:16
```

No volume.

Flow:

```text
Postgres writes data
        |
        v
Container writable layer
        |
        v
docker rm -f pg-bad
        |
        v
Data gone
```

Visual:

```text
Before delete:
+------------------------------+
| pg-bad container             |
| /var/lib/postgresql/data     |
| orders table exists          |
+------------------------------+

After delete:
+------------------------------+
| container removed            |
| writable layer removed       |
| orders table gone            |
+------------------------------+
```

This is the beginner disaster.

---

# 12. Dry Run: Correct PostgreSQL Container

Command:

```bash
docker volume create pgdata

docker run -d --name pg-good \
  -e POSTGRES_PASSWORD=secret \
  -v pgdata:/var/lib/postgresql/data \
  postgres:16
```

Flow:

```text
Postgres writes data
        |
        v
Mounted volume pgdata
        |
        v
docker rm -f pg-good
        |
        v
Container gone, volume remains
        |
        v
New container mounts pgdata
        |
        v
Data returns
```

Visual:

```text
Container 1                 Docker Volume
+-------------+             +----------------+
| postgres    | ----------> | pgdata         |
| data path   |             | real DB files  |
+-------------+             +----------------+

Container deleted

Container 2                 Same Docker Volume
+-------------+             +----------------+
| postgres    | ----------> | pgdata         |
| data path   |             | old DB files   |
+-------------+             +----------------+
```

---

# 13. Redis Stateful Container

Redis can be stateless cache or stateful data store depending on how you use it.

Stateless cache:

```text
Redis loses data
System still works
DB reloads cache
```

Stateful Redis:

```text
Redis used for sessions
Redis used for queues
Redis used for counters
Redis used for locks
Redis persistence enabled
```

Then storage matters.

Redis persistence paths usually involve `/data`.

```bash
docker volume create redisdata

docker run -d --name redis \
  -v redisdata:/data \
  redis:7 redis-server --appendonly yes
```

Visual:

```text
Redis Container
    |
    v
/data/appendonly.aof
    |
    v
Docker Volume redisdata
```

If AOF is enabled and volume survives, Redis can replay data after restart.

---

# 14. Stateful Spring Boot File Upload Example

Imagine an upload service.

Bad design:

```java
Path uploadPath = Paths.get("/app/uploads");
```

If `/app/uploads` is inside the container writable layer, uploaded files disappear when the container is deleted.

Better:

```yaml
app:
  upload-dir: /data/uploads
```

Docker:

```bash
docker volume create uploads

docker run -d --name upload-service \
  -v uploads:/data/uploads \
  -p 8080:8080 \
  upload-service:1.0
```

Visual:

```text
HTTP Upload
    |
    v
Spring Boot Controller
    |
    v
/data/uploads/file.pdf
    |
    v
Docker Volume uploads
```

But production warning:

```text
For serious distributed systems, object storage like S3/MinIO is usually better than local container volume for user files.
```

---

# 15. Java Upload Controller Example

```java
@RestController
@RequestMapping("/files")
public class FileUploadController {

    private final Path uploadDir;

    public FileUploadController(@Value("${app.upload-dir}") String uploadDir) throws IOException {
        this.uploadDir = Paths.get(uploadDir);
        Files.createDirectories(this.uploadDir);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        String safeName = Path.of(file.getOriginalFilename()).getFileName().toString();
        Path target = uploadDir.resolve(safeName);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return ResponseEntity.ok("Stored at " + target);
    }
}
```

`application.yml`:

```yaml
app:
  upload-dir: /data/uploads
```

Docker run:

```bash
docker run -p 8080:8080 -v uploads:/data/uploads upload-service:1.0
```

Mental model:

```text
Java writes to /data/uploads
Docker redirects to persistent volume
```

---

# 16. Compose Example: Spring Boot + Postgres + Redis

```yaml
services:
  order-service:
    image: order-service:1.0
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/orders
      SPRING_DATASOURCE_USERNAME: app
      SPRING_DATASOURCE_PASSWORD: secret
      SPRING_REDIS_HOST: redis
    depends_on:
      - postgres
      - redis

  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: orders
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
    volumes:
      - pgdata:/var/lib/postgresql/data

  redis:
    image: redis:7
    command: redis-server --appendonly yes
    volumes:
      - redisdata:/data

volumes:
  pgdata:
  redisdata:
```

Visual:

```text
             Docker Compose Network
+------------------------------------------------+
|                                                |
| order-service                                  |
|   |                                            |
|   +--> postgres:5432 ----> volume pgdata       |
|   |                                            |
|   +--> redis:6379 -------> volume redisdata    |
|                                                |
+------------------------------------------------+
```

Service containers are replaceable. Data volumes are protected.

---

# 17. Stateful Container Crash Dry Run

Scenario:

```text
Postgres container crashes due to host reboot
```

Flow:

```text
Host reboot
    |
    v
Container stops
    |
    v
Volume still on disk
    |
    v
Docker restarts container
    |
    v
Postgres sees existing data
    |
    v
WAL recovery runs if needed
    |
    v
Database accepts connections
```

ASCII:

```text
Crash
  |
  v
+------------------+        +----------------+
| container dead   |        | pgdata volume  |
| process stopped  |        | still exists   |
+------------------+        +----------------+
          |
          v
Restart container
          |
          v
Mount same pgdata
          |
          v
Recover database
```

Important: volume survival does not replace backups. If the disk corrupts or someone deletes rows, the volume faithfully stores the bad state too.

---

# 18. Persistence Is Not Backup

This is a common production misunderstanding.

```text
Volume != Backup
Replication != Backup
Container restart policy != Backup
```

Volume protects against container deletion.

Backup protects against:

```text
Human mistake
SQL delete without WHERE
Disk corruption
Ransomware
Bad migration
Host loss
Volume deletion
```

Visual:

```text
Volume:
Container dies  ---> data survives

Backup:
Data destroyed  ---> restore old copy
```

For Postgres:

```bash
docker exec postgres pg_dump -U app orders > orders_backup.sql
```

Restore:

```bash
cat orders_backup.sql | docker exec -i postgres psql -U app orders
```

---

# 19. Backup Container Pattern

You can run a temporary helper container to access a volume.

```bash
docker run --rm \
  -v pgdata:/data \
  -v $(pwd):/backup \
  busybox \
  tar czf /backup/pgdata.tar.gz /data
```

Visual:

```text
pgdata volume ----> temporary backup container ----> host backup file
```

```text
+--------------+      +-------------------+      +------------------+
| pgdata       | ---> | busybox container | ---> | pgdata.tar.gz    |
+--------------+      +-------------------+      +------------------+
```

This pattern is useful for file-level backups, but databases often need database-aware backup tools because raw file copying during writes can be inconsistent.

---

# 20. Permissions Problem

One common stateful container failure is permission mismatch.

```text
Container process runs as UID 999
Mounted host folder owned by root
Postgres cannot write
```

Symptoms:

```text
permission denied
could not create directory
read-only file system
operation not permitted
```

Debug flow:

```bash
docker logs postgres

docker inspect postgres

docker exec -it postgres sh
id
ls -ld /var/lib/postgresql/data
```

Visual:

```text
Container user UID 999
        |
        v
Mounted folder owner root
        |
        v
Write denied
```

Fix depends on environment:

```bash
sudo chown -R 999:999 ./pgdata
```

But be careful: changing ownership blindly on production paths can break other services.

---

# 21. Logs: Stateful or Not?

Logs are runtime state, but they should usually not be treated like application data inside the container.

Container-native model:

```text
Application writes to stdout/stderr
        |
        v
Docker captures logs
        |
        v
Log driver / collector
        |
        v
ELK / Loki / Cloud logging
```

Bad:

```text
App writes only to /app/logs/app.log inside container
Container removed
Logs lost
```

Better:

```text
Spring Boot console logs
Docker logs
Fluent Bit / Promtail / Filebeat
Central log system
```

Spring Boot:

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n"
```

Do not make local log files your only observability source.

---

# 22. Stateful Containers and Scaling

Stateless service scaling is easy:

```text
order-service x 5 replicas
All connect to same DB
```

Stateful scaling is harder:

```text
postgres x 5 replicas?
Who owns writes?
Who has latest data?
How is replication handled?
How is failover handled?
```

Visual:

```text
Stateless:
+------+  +------+  +------+
| API1 |  | API2 |  | API3 |
+--+---+  +--+---+  +--+---+
   |         |         |
   +---------+---------+
             |
             v
          Database

Stateful:
+----------+     +----------+
| DB Node1 | --> | DB Node2 |
+----------+     +----------+
 Need replication, leader, backups, consistency
```

You cannot scale stateful systems like simple REST APIs.

---

# 23. Stateful Container Anti-Patterns

Avoid these:

```text
1. Running production DB without volume
2. Treating Docker restart as backup
3. Storing user uploads inside container layer
4. Hardcoding host bind mount paths everywhere
5. Running multiple DB containers against same volume without DB support
6. Ignoring permissions and UID/GID
7. Deleting volumes casually with docker compose down -v
8. Assuming local volume works across multiple hosts
```

Danger command:

```bash
docker compose down -v
```

Meaning:

```text
Stop containers
Remove network
Remove named volumes
```

For development it may be fine. For important data, it can be disaster.

---

# 24. Docker Compose Down vs Down -v

```bash
docker compose down
```

Usually removes:

```text
containers
network
```

But keeps named volumes.

```bash
docker compose down -v
```

Removes:

```text
containers
network
volumes
```

Visual:

```text
compose down:
[containers gone] [network gone] [volumes remain]

compose down -v:
[containers gone] [network gone] [volumes gone]
```

Mnemonic:

```text
-v = vanish volumes
```

---

# 25. Kubernetes Connection: StatefulSet

Docker teaches the local mental model. Kubernetes extends it.

Docker:

```text
Container + Volume
```

Kubernetes:

```text
Pod + PersistentVolumeClaim + PersistentVolume
```

For stateless apps:

```text
Deployment
```

For stateful apps:

```text
StatefulSet
```

Why StatefulSet?

```text
Stable identity
Stable network name
Stable storage claim
Ordered startup/shutdown
```

Visual:

```text
postgres-0 ---> pvc-postgres-0 ---> persistent disk 0
postgres-1 ---> pvc-postgres-1 ---> persistent disk 1
postgres-2 ---> pvc-postgres-2 ---> persistent disk 2
```

Each replica gets its own identity and storage.

---

# 26. Kubernetes StatefulSet Mental Model

Deployment pods are like replaceable workers:

```text
api-abc12
api-x9k22
api-pq777
```

StatefulSet pods are like named bank branches:

```text
postgres-0
postgres-1
postgres-2
```

If `postgres-0` dies, Kubernetes tries to bring back `postgres-0`, not a random new identity.

```text
postgres-0 dies
      |
      v
new postgres-0 attaches same PVC
```

This matters because stateful systems often depend on identity:

```text
leader
replica
partition owner
broker id
shard id
```

Kafka, ZooKeeper, Cassandra, Postgres clusters, and Redis clusters care about stable identity.

---

# 27. Local Volume Multi-Host Problem

A Docker local volume exists on one host.

```text
Host A
  pgdata volume

Host B
  no pgdata volume
```

If a scheduler moves the container from Host A to Host B:

```text
Container starts on Host B
        |
        v
Volume missing or empty
        |
        v
Data unavailable
```

Visual:

```text
Host A                         Host B
+------------------+           +------------------+
| postgres         |           | new postgres     |
| pgdata exists    |           | pgdata missing   |
+------------------+           +------------------+
```

That is why production orchestration needs proper storage systems:

```text
cloud disks
network storage
CSI drivers
replication-aware databases
managed databases
```

---

# 28. Production Story: Lost Uploads

A team built an upload service.

Code:

```java
Files.copy(file.getInputStream(), Paths.get("/app/uploads", name));
```

Dockerfile:

```dockerfile
WORKDIR /app
COPY target/upload.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

It worked in testing.

Then production deployment replaced the container.

```text
Old container removed
New container started
/app/uploads empty
Users reported missing files
```

Root cause:

```text
Files were stored in container writable layer.
```

Fix options:

```text
Short term: mount volume to /data/uploads
Better: store files in S3/MinIO and keep metadata in DB
```

Lesson:

```text
Container filesystem is not a database.
```

---

# 29. Production Story: Redis Session Loss

A login system stored sessions only in Redis.

Redis container was started without volume and without AOF/RDB persistence.

```text
Redis restart
    |
    v
All sessions gone
    |
    v
All users logged out
```

For some products, that may be acceptable. For others, it is a production incident.

Better design:

```text
Decide Redis role clearly:

Cache only? data loss acceptable.
Session store? persistence or alternative strategy needed.
Queue? use Kafka/RabbitMQ or durable Redis configuration.
Locking? restart behavior must be understood.
```

Do not say “Redis is fast” and ignore durability semantics.

---

# 30. Debugging Playbook

When data disappears, ask:

```text
1. Was the data written inside container layer?
2. Was the expected volume mounted?
3. Was the correct volume name used?
4. Did someone run docker compose down -v?
5. Is this the same Docker host?
6. Are permissions blocking writes?
7. Did app write to a different path than expected?
8. Is the DB using a different data directory?
9. Is backup available?
```

Commands:

```bash
docker ps -a

docker volume ls

docker volume inspect pgdata

docker inspect postgres

docker exec -it postgres sh
mount
ls -lah /var/lib/postgresql/data

docker logs postgres
```

Mental path:

```text
Application path
    |
    v
Container mount table
    |
    v
Docker volume
    |
    v
Host storage
```

---

# 31. Debugging: Is My Volume Mounted?

Use `docker inspect`:

```bash
docker inspect postgres
```

Look for mounts:

```json
"Mounts": [
  {
    "Type": "volume",
    "Name": "pgdata",
    "Destination": "/var/lib/postgresql/data"
  }
]
```

Interpretation:

```text
Type        = volume or bind
Name/Source = where data comes from
Destination = path inside container
```

ASCII:

```text
Mount record
    |
    +--> Source / Name
    |
    +--> Destination inside container
    |
    +--> RW or read-only
```

If destination does not match where the app writes, persistence will fail.

---

# 32. Debugging: Wrong Path Problem

Compose:

```yaml
volumes:
  - uploads:/data/uploads
```

App config:

```yaml
app:
  upload-dir: /app/uploads
```

Problem:

```text
Volume mounted at /data/uploads
App writes to /app/uploads
```

Visual:

```text
Mounted persistent path: /data/uploads
                           ^
                           |
App writes here:        /app/uploads
```

They are different directories.

Fix:

```yaml
app:
  upload-dir: /data/uploads
```

Lesson:

```text
Mounting a volume is not enough.
The application must write to the mounted path.
```

---

# 33. Spring Boot DataSource Example

Stateful container often means external DB, not embedded local DB.

`application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/orders
    username: app
    password: secret
  jpa:
    hibernate:
      ddl-auto: validate
```

Why not H2 file DB inside container?

```text
/app/data/orders.mv.db
```

Because:

```text
Container deleted -> file lost unless mounted
Multiple containers -> file DB unsafe
Production durability -> weak setup
```

Good production mental model:

```text
Spring Boot is stateless
Database is stateful
Database data is persisted and backed up
```

---

# 34. Java Health Check For Stateful Dependency

A stateless Spring Boot container may depend on a stateful database.

```java
@Component
public class DatabaseHealthProbe {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHealthProbe(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean canReachDatabase() {
        Integer result = jdbcTemplate.queryForObject("select 1", Integer.class);
        return result != null && result == 1;
    }
}
```

Better: use Spring Boot Actuator.

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

Flow:

```text
Kubernetes / Docker health check
        |
        v
Spring Boot health endpoint
        |
        v
DB connectivity check
        |
        v
ready or not ready
```

---

# 35. Stateful Readiness Problem

A container can be running but not ready.

Postgres:

```text
Process started
WAL recovery running
Database not ready yet
```

Spring Boot:

```text
Container started
Trying DB connection
DB not ready
Startup fails
```

Compose `depends_on` only controls startup ordering in many setups, not true readiness.

Better:

```text
Use health checks
Retry DB connection
Use migration tools carefully
```

ASCII:

```text
postgres container started
        |
        v
not ready yet
        |
        v
order-service starts too early
        |
        v
connection refused
```

Spring Boot should tolerate dependency startup delays where appropriate.

---

# 36. Migration Risk With Stateful Containers

Stateful services need schema migration discipline.

Bad deployment:

```text
New app starts
Auto alters schema unexpectedly
Old app still running
Queries break
```

Better:

```text
1. Backward-compatible DB migration
2. Deploy new app
3. Verify
4. Remove old columns later
```

Tools:

```text
Flyway
Liquibase
Manual DBA-controlled migration
```

Mental model:

```text
Container images are immutable.
Database state evolves.
Schema changes must be controlled.
```

Never treat database schema like disposable container code.

---

# 37. Stateful Container Security

Stateful containers hold sensitive data.

Checklist:

```text
[ ] Do not bake secrets into image
[ ] Do not store DB password in Dockerfile
[ ] Limit volume permissions
[ ] Encrypt backups
[ ] Protect host paths
[ ] Do not expose database port publicly unless needed
[ ] Use least-privilege DB users
[ ] Rotate credentials
```

Bad Dockerfile:

```dockerfile
ENV POSTGRES_PASSWORD=secret
```

Better:

```text
Use environment injection, secrets manager, Docker secrets, Kubernetes secrets, or cloud secret manager.
```

Network visual:

```text
Internet
   |
   X  do not expose DB publicly by default
   |
Docker Network
   |
   +--> app
   +--> postgres
```

---

# 38. Interview Answer: What Is A Stateful Container?

A stateful container is a container whose correctness depends on data that must survive container restarts, recreation, or rescheduling. The process can still be replaced, but the state must be preserved through volumes, external storage, replication, or managed services.

Strong answer:

```text
A stateless API container can be recreated anytime because the business state lives in a database or external service. A stateful container, like Postgres or Redis with persistence, needs stable storage. In Docker this means volumes or bind mounts. In Kubernetes this usually means StatefulSets with PersistentVolumeClaims. The important point is that container writable layers are disposable, so production data must not depend on them.
```

---

# 39. Interview Answer: Volume vs Backup

Strong answer:

```text
A volume keeps data outside the container writable layer, so data survives container deletion or recreation. But it is not a backup. If the data inside the volume is corrupted, deleted, encrypted by malware, or lost with the host disk, the volume does not help. Backups are separate point-in-time copies that can be restored after logical or physical data loss.
```

ASCII:

```text
Volume solves:
container removed -> data survives

Backup solves:
data damaged -> restore old copy
```

---

# 40. Interview Answer: Why Stateful Containers Are Hard To Scale

Strong answer:

```text
Stateless containers can be replicated freely because every instance is equivalent. Stateful containers need identity, storage ownership, consistency, replication, and failover rules. For example, five API containers can all talk to the same database, but five database containers cannot blindly write to the same files. The database engine must support clustering or replication, and the platform must provide stable storage and network identity.
```

---

# 41. Production Checklist

```text
[ ] Identify whether container is stateless or stateful
[ ] Never store business data only in writable layer
[ ] Mount volumes for DB data directories
[ ] Use correct mount destination path
[ ] Avoid accidental docker compose down -v
[ ] Verify volume with docker inspect
[ ] Configure backups separately
[ ] Test restore process, not just backup creation
[ ] Understand Redis durability mode if used for state
[ ] Use object storage for distributed user uploads
[ ] Monitor disk usage
[ ] Check permissions and UID/GID
[ ] Avoid public DB port exposure
[ ] Use health checks for stateful dependencies
[ ] Plan schema migrations carefully
```

---

# 42. Commands Cheat Sheet

```bash
# Create volume
docker volume create pgdata

# List volumes
docker volume ls

# Inspect volume
docker volume inspect pgdata

# Run Postgres with persistent volume
docker run -d --name postgres \
  -e POSTGRES_PASSWORD=secret \
  -v pgdata:/var/lib/postgresql/data \
  postgres:16

# Run Redis with AOF persistence
docker run -d --name redis \
  -v redisdata:/data \
  redis:7 redis-server --appendonly yes

# See mounts
docker inspect postgres

# Remove container only
docker rm -f postgres

# Remove volume carefully
docker volume rm pgdata

# Compose without deleting volumes
docker compose down

# Compose and delete volumes - dangerous for data
docker compose down -v
```

---

# 43. One Picture To Remember

```text
                    Docker Host
+------------------------------------------------------+
|                                                      |
|   Stateless App Container                            |
|   +-------------------+                              |
|   | Spring Boot API   |                              |
|   | disposable        |                              |
|   +---------+---------+                              |
|             |                                        |
|             v                                        |
|   Stateful DB Container                              |
|   +-------------------+       Docker Volume          |
|   | PostgreSQL        | ----> +------------------+    |
|   | process can die   |       | pgdata           |    |
|   +-------------------+       | real DB files    |    |
|                               +------------------+    |
|                                                      |
+------------------------------------------------------+

Remember:

Code container can be replaced.
Data must be protected.
Volume survives container death.
Backup survives data disaster.
```

---

# 44. Final Mental Model

Do not memorize stateful containers as a Docker feature. Understand the lifecycle mismatch.

```text
Container lifecycle:
short-lived, replaceable, disposable

Data lifecycle:
long-lived, valuable, recoverable, audited
```

The whole design problem is connecting these two safely.

```text
Disposable compute
        +
Persistent storage
        +
Backup/restore
        +
Controlled migration
        +
Monitoring
        =
Production-ready stateful container
```

If you remember only one sentence:

```text
A stateful container is safe only when the data is not depending on the container writable layer.
```
