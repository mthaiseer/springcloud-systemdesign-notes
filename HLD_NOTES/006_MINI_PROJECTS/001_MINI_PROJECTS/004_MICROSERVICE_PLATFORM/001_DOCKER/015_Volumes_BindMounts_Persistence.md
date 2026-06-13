# 015_Volumes_BindMounts_Persistence

> MiniDocker Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize

---

# 1. Why Persistence Exists

A container is usually treated as disposable. You can stop it, remove it, rebuild it, or deploy a newer version. That is good for application code, but dangerous for data.

Think about a Spring Boot service:

```text
Spring Boot App
   |
   +-- writes uploaded files
   +-- writes logs
   +-- writes temp reports
   +-- connects to PostgreSQL
   +-- connects to Redis
```

If all of that lives only inside the container filesystem, then removing the container can remove important state.

```text
Without Persistence

Container Created
      |
App Writes Data
      |
Container Removed
      |
Data Gone
```

Docker persistence exists because containers are replaceable, but business data is not.

Real-world model:

```text
Container = Hotel Room
Image     = Room Design
Volume    = Locker Outside Room
Data      = Your Documents
```

You can change hotel rooms, repaint the room, or replace the bed. But your documents should stay in the locker, not under the bed.

---

# 2. Not-To-Memorize Model

Do not memorize commands first.

Remember this:

```text
Container filesystem = temporary working table
Volume               = managed storage drawer
Bind mount           = host folder connected directly
```

Mental picture:

```text
+--------------------+        +----------------------+
| Container          |        | Host Machine          |
|                    |        |                      |
| /app               |        | Docker-managed volume |
| /tmp               |        | /var/lib/docker/...   |
| /data  ------------+------> | persistent data       |
+--------------------+        +----------------------+
```

If a file must survive container replacement, move it outside the container lifecycle.

---

# 3. One Picture To Remember

```text
                    Docker Host

+-------------------------------------------------------+
|                                                       |
|   +-------------------+                               |
|   | Container          |                              |
|   |                   |                               |
|   | /app              |   disposable                  |
|   | /logs ------------+----------+                    |
|   | /data ------------+------+   |                    |
|   +-------------------+      |   |                    |
|                              |   |                    |
|                +-------------+   +-------------+      |
|                |                               |      |
|        +-------v-------+               +-------v---+  |
|        | Named Volume  |               | Bind Mount|  |
|        | Docker owns   |               | You own   |  |
|        +---------------+               +-----------+  |
|                                                       |
+-------------------------------------------------------+
```

Rule:

```text
If Docker should manage storage -> volume
If developer wants exact host folder -> bind mount
```

---

# 4. Container Filesystem Problem

A container gets a writable layer on top of the image layers.

```text
Image Layers - read only

Layer 4: app.jar
Layer 3: dependencies
Layer 2: JDK
Layer 1: OS base

Container Writable Layer - temporary

logs.txt
uploads/avatar.png
cache.tmp
```

When the container is removed, the writable layer is removed with it.

```text
docker rm app-container
       |
       v
Writable layer deleted
       |
       v
Files written inside container disappear
```

This is fine for temporary data:

```text
/tmp
compiled temp files
short-lived cache
```

But it is dangerous for:

```text
PostgreSQL data directory
uploaded files
business reports
audit logs
backup files
```

Understanding first:

```text
Image = recipe
Container = running kitchen
Writable layer = dirty table
Volume = pantry outside kitchen
```

A kitchen can be cleaned or replaced. The pantry must survive.

---

# 5. Volume Mental Model

A Docker volume is storage managed by Docker and mounted into a container.

```text
Named Volume

app-data
   |
   v
Mounted inside container at /data
```

Diagram:

```text
+-----------------------+
| Docker Volume         |
| name: app-data        |
| data stored on host   |
+-----------+-----------+
            |
            v
+-----------+-----------+
| Container             |
| /data                 |
| app writes files here |
+-----------------------+
```

Command:

```bash
docker volume create app-data

docker run -d \
  --name app \
  -v app-data:/data \
  my-spring-app:1.0
```

Meaning:

```text
app-data:/data
   |      |
   |      +-- path inside container
   +--------- Docker-managed volume name
```

Do not think of a volume as a magical cloud disk. It is host storage managed by Docker, with a clean lifecycle and Docker commands around it.

---

# 6. Bind Mount Mental Model

A bind mount connects an exact host path into a container.

```text
Host folder
/home/mohamed/projects/app/config
        |
        v
Container path
/config
```

Diagram:

```text
+-----------------------------+
| Host Machine                |
|                             |
| /home/dev/app/application.yml|
+--------------+--------------+
               |
               v
+--------------+--------------+
| Container                   |
| /config/application.yml     |
+-----------------------------+
```

Command:

```bash
docker run -d \
  --name app \
  -v /home/dev/app/config:/config \
  my-spring-app:1.0
```

Use bind mounts when you need direct host visibility:

```text
local development
mount source code
mount config files
mount certificates
mount test data
```

But be careful in production. Bind mounts depend on the host path existing and having correct permissions.

```text
Works on laptop:
/home/dev/config

Fails on server:
/path does not exist
permission denied
wrong SELinux/AppArmor context
```

---

# 7. Volume vs Bind Mount: Real Difference

```text
+----------------+-----------------------+-----------------------------+
| Feature        | Volume                | Bind Mount                  |
+----------------+-----------------------+-----------------------------+
| Managed by     | Docker                | User / host OS              |
| Path choice    | Docker decides        | User gives exact path       |
| Best for       | Persistent app data   | Dev files/configs           |
| Portability    | Better                | Host-specific               |
| Backup         | Docker-friendly       | File-system based           |
| Risk           | Less host coupling    | Can overwrite host files    |
+----------------+-----------------------+-----------------------------+
```

Mental shortcut:

```text
Volume     = Docker storage account
Bind mount = host folder shortcut
```

For production databases, prefer named volumes unless your platform has a better storage layer.

For local Spring Boot development, bind mounts are useful because changes on your laptop immediately appear inside the container.

---

# 8. Dry Run: What Happens During Container Start

Command:

```bash
docker run -d --name postgres-db \
  -v pgdata:/var/lib/postgresql/data \
  -e POSTGRES_PASSWORD=secret \
  postgres:16
```

Dry run:

```text
1. Docker checks if volume pgdata exists
        |
        +-- if missing, create it

2. Docker creates container filesystem
        |
        +-- image layers + writable layer

3. Docker mounts pgdata into container
        |
        +-- container path /var/lib/postgresql/data

4. PostgreSQL starts
        |
        +-- writes database files into /var/lib/postgresql/data

5. Actual data lands in pgdata volume
```

Visual:

```text
Postgres Process
      |
      v
/var/lib/postgresql/data
      |
      v
Docker Volume: pgdata
      |
      v
Host disk
```

If you remove only the container:

```bash
docker rm -f postgres-db
```

The volume remains.

```text
Container gone
Volume still exists
Database files still exist
```

---

# 9. PostgreSQL Persistence Example

Bad command:

```bash
docker run -d --name postgres-db \
  -e POSTGRES_PASSWORD=secret \
  postgres:16
```

Problem:

```text
Postgres writes data inside container writable layer
Container removed
Database gone
```

Good command:

```bash
docker volume create pgdata

docker run -d --name postgres-db \
  -e POSTGRES_PASSWORD=secret \
  -v pgdata:/var/lib/postgresql/data \
  postgres:16
```

ASCII path:

```text
Spring Boot
   |
   v
PostgreSQL container
   |
   v
/var/lib/postgresql/data
   |
   v
pgdata volume
   |
   v
Host disk survives container replacement
```

Restart scenario:

```text
Old container removed
        |
New postgres container created
        |
Same pgdata volume mounted
        |
Database still available
```

This is the core idea of Docker persistence.

---

# 10. Redis Persistence Example

Redis can be used as cache, but sometimes it also uses persistence through RDB or AOF.

```text
Redis memory
   |
   +-- RDB snapshot file
   +-- AOF append log
```

Command:

```bash
docker volume create redisdata

docker run -d --name redis \
  -v redisdata:/data \
  redis:7 redis-server --appendonly yes
```

Diagram:

```text
Redis Container
   |
   v
/data/appendonly.aof
   |
   v
redisdata volume
```

Important mental model:

```text
Redis cache only       -> losing data may be acceptable
Redis durable queue    -> losing data is serious
Redis session store    -> losing data logs users out
Redis rate limiter     -> losing data resets counters
```

Do not blindly persist every Redis. Understand the business meaning of the data.

---

# 11. Spring Boot File Upload Persistence

Imagine a Spring Boot application that accepts profile photos.

Bad design:

```text
UploadController writes to /app/uploads
```

```text
Container
   |
   v
/app/uploads/avatar.png
   |
   v
Container removed
   |
   v
avatar.png lost
```

Better design:

```text
UploadController writes to /data/uploads
/data is mounted to a volume
```

Java example:

```java
@RestController
@RequestMapping("/files")
public class FileUploadController {

    private final Path uploadRoot = Paths.get("/data/uploads");

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        Files.createDirectories(uploadRoot);
        Path target = uploadRoot.resolve(file.getOriginalFilename()).normalize();
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return ResponseEntity.ok("saved=" + target);
    }
}
```

Docker command:

```bash
docker volume create app-uploads

docker run -d --name upload-service \
  -p 8080:8080 \
  -v app-uploads:/data/uploads \
  upload-service:1.0
```

Flow:

```text
Browser uploads file
      |
      v
Spring Boot Controller
      |
      v
/data/uploads/avatar.png
      |
      v
app-uploads Docker volume
      |
      v
Survives container recreation
```

---

# 12. Spring Boot Configuration With Bind Mount

For local development, you may want to edit `application.yml` without rebuilding the image.

Host:

```text
/home/dev/config/application.yml
```

Container:

```text
/config/application.yml
```

Command:

```bash
docker run -d --name order-service \
  -p 8080:8080 \
  -v /home/dev/config:/config \
  -e SPRING_CONFIG_ADDITIONAL_LOCATION=file:/config/ \
  order-service:1.0
```

Diagram:

```text
Developer edits file on host
        |
        v
/home/dev/config/application.yml
        |
        v
Bind mount
        |
        v
Container sees /config/application.yml
        |
        v
Spring Boot reads config
```

This is excellent for local experiments.

But for production, prefer environment variables, secrets managers, Kubernetes ConfigMaps/Secrets, or controlled deployment pipelines.

---

# 13. Docker Compose Persistence Example

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
    volumes:
      - order-uploads:/data/uploads
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
    command: ["redis-server", "--appendonly", "yes"]
    volumes:
      - redisdata:/data

volumes:
  order-uploads:
  pgdata:
  redisdata:
```

Visual:

```text
                docker compose network

+----------------+       +---------------+       +-------------+
| order-service  | ----> | postgres      |       | redis       |
| /data/uploads  |       | /var/lib/...  |       | /data       |
+-------+--------+       +-------+-------+       +------+------+ 
        |                        |                      |
        v                        v                      v
 order-uploads                pgdata                redisdata
```

Compose creates and tracks named volumes automatically.

---

# 14. Mount Shadowing: A Common Confusing Bug

Mounting a volume over a non-empty container directory hides the image content at that path.

Image contains:

```text
/app/config/default.yml
/app/config/logback.xml
```

Run:

```bash
docker run -v empty-config:/app/config my-app
```

Result inside container:

```text
/app/config appears empty
```

Why?

```text
Volume mounted on /app/config
        |
        v
Original image files under /app/config are hidden
```

Diagram:

```text
Before mount:

Container image
/app/config/default.yml
/app/config/logback.xml

After mount:

Volume empty-config
mounted at /app/config

Container sees volume content, not image content
```

Debugging mindset:

```bash
docker exec -it my-app ls -la /app/config

docker inspect my-app
```

If files disappear after adding a volume, suspect mount shadowing.

---

# 15. Permission Problems

A container process may run as a non-root user.

```text
Container user: appuser uid=1000
Mounted host folder owner: root
Permission: read-only
```

Failure:

```text
java.nio.file.AccessDeniedException: /data/uploads/report.pdf
```

ASCII:

```text
Spring Boot process
user=appuser
     |
     v
/data/uploads
     |
     v
Mounted folder owned by root
     |
     v
Write denied
```

Debug commands:

```bash
docker exec -it app id

docker exec -it app ls -ld /data/uploads

docker inspect app
```

Fix options:

```text
1. Set correct ownership on host folder
2. Run container with matching UID/GID
3. Use Docker named volume
4. Avoid writing into read-only config paths
```

Example:

```bash
sudo chown -R 1000:1000 ./uploads
```

Production lesson:

```text
Most volume bugs are not Docker bugs.
They are path, ownership, permission, or lifecycle bugs.
```

---

# 16. Read-Only Mounts

Sometimes a container should read a file but never modify it.

Example:

```text
TLS certificates
static config
license files
reference data
```

Command:

```bash
docker run -d --name app \
  -v /host/certs:/certs:ro \
  my-app:1.0
```

Meaning:

```text
:ro = read only
```

Diagram:

```text
Host /certs
    |
    v
Container /certs
    |
    v
Read allowed
Write denied
```

Spring Boot TLS example:

```yaml
server:
  ssl:
    key-store: /certs/app.p12
    key-store-password: changeit
    key-store-type: PKCS12
```

Security mindset:

```text
If app only needs to read it, mount it read-only.
```

This reduces accidental writes and damage during compromise.

---

# 17. Logs: Volume or stdout?

Old style:

```text
Application writes logs to /var/log/app.log
```

Container-native style:

```text
Application writes logs to stdout/stderr
Docker captures logs
Log collector ships logs
```

Recommended for most Spring Boot apps:

```text
Spring Boot -> console logs -> Docker logging driver -> collector
```

Diagram:

```text
Spring Boot
   |
   v
stdout/stderr
   |
   v
Docker logs
   |
   v
Fluent Bit / Filebeat / Cloud logging
```

Use volumes for logs only when you have a strong reason:

```text
legacy app needs file logs
audit file must be retained locally
sidecar reads log file
```

Spring Boot logging config:

```yaml
logging:
  level:
    root: INFO
```

Avoid:

```yaml
logging:
  file:
    name: /app/logs/app.log
```

Unless `/app/logs` is intentionally mounted and rotated.

---

# 18. Backup Strategy For Volumes

A volume is persistent, but persistence is not backup.

```text
Persistence = survives container restart
Backup      = survives disk failure / deletion / corruption
```

Backup a named volume:

```bash
docker run --rm \
  -v pgdata:/data \
  -v $(pwd):/backup \
  alpine \
  tar czf /backup/pgdata-backup.tar.gz -C /data .
```

Restore:

```bash
docker run --rm \
  -v pgdata:/data \
  -v $(pwd):/backup \
  alpine \
  sh -c "cd /data && tar xzf /backup/pgdata-backup.tar.gz"
```

Visual:

```text
Docker volume pgdata
        |
        v
tar archive
        |
        v
backup file
        |
        v
remote storage / safe location
```

For databases, prefer database-native backups:

```text
PostgreSQL -> pg_dump / pg_basebackup / WAL archiving
MySQL      -> mysqldump / physical backup
Redis      -> RDB/AOF copy with care
```

---

# 19. Production Failure Story: Lost Database

Bad production setup:

```bash
docker run -d --name postgres postgres:16
```

Everything looked fine.

```text
App worked
Users created orders
Database accepted writes
```

Then someone did:

```bash
docker rm -f postgres
```

Result:

```text
Container removed
Writable layer removed
Database files gone
```

Root cause:

```text
Database data was inside container lifecycle
```

Correct design:

```bash
docker run -d --name postgres \
  -v pgdata:/var/lib/postgresql/data \
  postgres:16
```

Production lesson:

```text
A database without an external data mount is a demo, not production.
```

---

# 20. Production Failure Story: Bind Mount Works Locally, Fails On Server

Local command:

```bash
docker run -v /Users/dev/config:/config app
```

Server command copied blindly:

```bash
docker run -v /Users/dev/config:/config app
```

Server does not have `/Users/dev/config`.

Failure:

```text
Spring Boot cannot find application.yml
```

Diagram:

```text
Laptop
/Users/dev/config exists
        |
        v
Works

Server
/Users/dev/config missing
        |
        v
Container sees empty or wrong mount
        |
        v
App fails
```

Fix:

```text
Use environment-specific deployment config
Use Docker Compose with clear relative paths
Use named volumes for app data
Use secrets/config management for production
```

---

# 21. Production Failure Story: Volume Filled Disk

Persistence can create another problem: disk growth.

```text
App writes uploads
Postgres writes WAL
Redis writes AOF
Logs write files
Backups remain on disk
```

Eventually:

```text
No space left on device
```

Symptoms:

```text
Postgres refuses writes
Spring Boot upload fails
Redis cannot rewrite AOF
Docker pulls fail
```

Debug:

```bash
df -h

docker system df

docker volume ls

docker volume inspect pgdata
```

Mental model:

```text
Volume protects data from container deletion.
It does not protect the host disk from filling.
```

Production checklist:

```text
Monitor disk usage
Rotate logs
Set upload limits
Archive old data
Use external storage for large files
```

---

# 22. Docker Volume Commands

List volumes:

```bash
docker volume ls
```

Inspect volume:

```bash
docker volume inspect pgdata
```

Remove unused volume:

```bash
docker volume rm old-volume
```

Dangerous cleanup:

```bash
docker volume prune
```

Meaning:

```text
Remove all unused volumes
```

Be careful:

```text
Unused does not always mean unimportant.
A stopped database may have an unused volume.
```

Safe mindset:

```text
Before deleting a volume:
1. Inspect it
2. Identify owner container/app
3. Check backup
4. Confirm business value
```

---

# 23. Inspecting Mounts

Command:

```bash
docker inspect app
```

Look for:

```json
"Mounts": [
  {
    "Type": "volume",
    "Name": "app-uploads",
    "Destination": "/data/uploads"
  }
]
```

Meaning:

```text
Type        -> volume or bind
Name/Source -> where data comes from
Destination -> path inside container
```

Debug visual:

```text
Container path failing?
        |
        v
Check docker inspect Mounts
        |
        v
Is the expected volume mounted?
        |
        v
Check permissions
        |
        v
Check application path
```

Common mistake:

```text
App writes to /uploads
Volume mounted at /data/uploads
```

The app and Docker mount must agree on the same path.

---

# 24. Mini Debugging Playbook

Problem: uploaded files disappear after redeploy.

Ask:

```text
1. Where does application write files?
2. Is that path mounted?
3. Is it volume or bind mount?
4. Was the volume removed?
5. Is app writing to a different path?
```

Commands:

```bash
docker exec -it app pwd

docker exec -it app ls -la /data/uploads

docker inspect app

docker volume ls

docker volume inspect app-uploads
```

Problem: permission denied.

```bash
docker exec -it app id

docker exec -it app ls -ld /data/uploads
```

Problem: database empty after restart.

```bash
docker inspect postgres

docker volume ls

docker logs postgres
```

Thinking path:

```text
App symptom
   |
   v
Container path
   |
   v
Mount mapping
   |
   v
Host/volume storage
   |
   v
Permissions/lifecycle
```

---

# 25. Kubernetes Connection

Docker volumes prepare your mind for Kubernetes volumes.

Docker:

```text
Container
   |
   v
Docker volume
   |
   v
Host disk
```

Kubernetes:

```text
Pod
   |
   v
Volume mount
   |
   v
PersistentVolumeClaim
   |
   v
PersistentVolume
   |
   v
Cloud disk / NFS / storage class
```

ASCII:

```text
Spring Boot Pod
      |
      v
/data/uploads
      |
      v
PVC: uploads-claim
      |
      v
PV: cloud disk
```

Kubernetes vocabulary is bigger, but the idea is the same:

```text
Container dies.
Data must live somewhere else.
```

For databases in Kubernetes, storage decisions become more serious because pods can move between nodes.

---

# 26. Local Development Pattern

Use bind mount for fast development feedback.

```yaml
services:
  app:
    image: eclipse-temurin:21
    working_dir: /workspace
    command: ./mvnw spring-boot:run
    ports:
      - "8080:8080"
    volumes:
      - .:/workspace
      - maven-cache:/root/.m2

volumes:
  maven-cache:
```

Diagram:

```text
Host source code
      |
      v
Bind mount into container
      |
      v
Maven/Spring Boot runs inside container
      |
      v
Developer edits code on host
      |
      v
Container sees changes
```

Use named volume for Maven cache:

```text
Dependencies survive container recreation
Builds become faster
```

This is a good hybrid:

```text
Source code -> bind mount
Dependency cache -> volume
```

---

# 27. Production Pattern

Production should usually avoid mounting source code.

Good production flow:

```text
Build image with app.jar
        |
        v
Deploy immutable image
        |
        v
Mount only required persistent data/config/secrets
```

Diagram:

```text
CI/CD
  |
  v
Docker image: order-service:1.5.2
  |
  v
Production container
  |
  +-- app.jar inside image
  +-- /data/uploads mounted volume
  +-- config from env/secrets
```

Avoid:

```text
Production container depends on /home/dev/project
Production code changed through bind mount
Database without volume
Writable logs growing forever
```

Production mindset:

```text
Image should be immutable.
Data should be external.
Config should be controlled.
```

---

# 28. Java Service: Safe Storage Abstraction

Instead of hardcoding paths everywhere, isolate file storage behind a service.

```java
@Service
public class LocalFileStorageService {

    private final Path root;

    public LocalFileStorageService(
            @Value("${app.storage.root:/data/uploads}") String rootPath) {
        this.root = Paths.get(rootPath);
    }

    public String save(String fileName, InputStream inputStream) throws IOException {
        Files.createDirectories(root);
        Path target = root.resolve(fileName).normalize();

        if (!target.startsWith(root)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString();
    }
}
```

Configuration:

```yaml
app:
  storage:
    root: /data/uploads
```

Docker:

```bash
docker run -v app-uploads:/data/uploads upload-service:1.0
```

Why this is good:

```text
Application code knows one stable path
Docker decides what backs that path
Local can use bind mount
Production can use named volume or cloud storage
```

---

# 29. Security Mindset

Mounts can expose sensitive host files.

Dangerous:

```bash
docker run -v /:/host ubuntu
```

This gives the container access to the host filesystem.

Diagram:

```text
Container compromised
      |
      v
Can read/write mounted host files
      |
      v
Host damage possible
```

Safer principles:

```text
Mount only required paths
Use read-only mounts where possible
Avoid mounting Docker socket
Avoid mounting host root
Run as non-root
Use least privilege
```

Very dangerous mount:

```bash
-v /var/run/docker.sock:/var/run/docker.sock
```

Why?

```text
Access to Docker socket can mean control over Docker host.
```

Production interview line:

```text
A bind mount is not only a storage decision; it is also a security boundary decision.
```

---

# 30. Strong Interview Answers

## What is a Docker volume?

A Docker volume is Docker-managed persistent storage that can be mounted into containers. It allows data to survive container deletion and recreation.

## Why not store database files inside the container?

Because the container writable layer belongs to the container lifecycle. If the container is removed, the database files can be lost. Database data should be mounted to a volume or external storage.

## Volume vs bind mount?

A volume is managed by Docker and is better for portable persistent data. A bind mount maps a specific host path into the container and is useful for development, configs, certificates, or exact host integration.

## What is mount shadowing?

If you mount a volume over a directory that already contains files in the image, the mounted volume hides those image files at runtime.

## How do you debug missing files inside a container?

Check the application write path, inspect Docker mounts, exec into the container, list the mounted path, verify permissions, and confirm the volume was not removed.

---

# 31. Production Checklist

```text
[ ] Database data mounted outside container writable layer
[ ] Upload directory mounted if files are local
[ ] Logs go to stdout unless file logs are required
[ ] Bind mounts avoided for production app code
[ ] Read-only mounts used for certs/config when possible
[ ] Permissions tested with actual container user
[ ] Volume backup strategy exists
[ ] Disk usage monitored
[ ] docker volume prune never run blindly
[ ] Mount paths match application config
[ ] Sensitive host paths not mounted
```

---

# 32. Cheat Sheet

```text
Container writable layer
  = temporary data tied to container

Volume
  = Docker-managed persistent storage

Bind mount
  = exact host folder/file mounted into container

Mount destination
  = path inside container

Mount source
  = volume name or host path

:ro
  = read-only mount

Best practice
  = immutable image + external data
```

Commands:

```bash
docker volume create app-data

docker volume ls

docker volume inspect app-data

docker run -v app-data:/data my-app

docker run -v /host/path:/container/path my-app

docker inspect my-app
```

---

# 33. One Picture To Remember

```text
                         Docker Host

+-------------------------------------------------------------+
|                                                             |
|  +-----------------------+                                  |
|  | Spring Boot Container |                                  |
|  |                       |                                  |
|  | app.jar               |  from image, replaceable         |
|  | /tmp                  |  temporary                       |
|  | /data/uploads --------+-------------+                    |
|  | /config ------------- +--------+    |                    |
|  +-----------------------+        |    |                    |
|                                   |    |                    |
|                         bind mount|    |named volume        |
|                                   |    |                    |
|                 +-----------------v+  +v----------------+   |
|                 | Host config dir  |  | Docker volume   |   |
|                 | developer owns   |  | Docker owns     |   |
|                 +------------------+  +-----------------+   |
|                                                             |
+-------------------------------------------------------------+

Remember:

Code can be rebuilt.
Containers can be replaced.
Data must be protected.
```

---

# 34. Final Takeaways

1. Containers are disposable, data is not.
2. The container writable layer is not a safe place for business data.
3. Volumes are Docker-managed persistent storage.
4. Bind mounts connect exact host paths and are useful but host-coupled.
5. Databases need persistent mounts.
6. Spring Boot uploads need a mounted storage path or external object storage.
7. Logs usually belong on stdout, not inside a volume.
8. Permission issues are common and must be debugged with user ID and path ownership.
9. Mount shadowing can hide files from the image.
10. Persistence is not backup; backup must be designed separately.
