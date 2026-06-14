# 018_Volumes_PV_PVC_StorageClass.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Kubernetes Storage Exists

A container is temporary.

A Pod is temporary.

A Node can disappear.

But business data should not disappear.

That is the whole reason Kubernetes storage exists.

Bad beginner model:

```text
Container has filesystem.
App writes file.
File stays forever.
```

Reality:

```text
Pod deleted
    |
    v
Container filesystem gone
```

If your Spring Boot app writes uploaded invoices to:

```text
/app/uploads/invoice-123.pdf
```

and the Pod is deleted, that file may disappear.

Kubernetes is designed for replaceable compute:

```text
Pods are cattle, not pets.
```

But many applications need durable state:

```text
Uploaded files
Database data
Message broker logs
Search indexes
Temporary shared files
ML model files
Batch job output
```

So Kubernetes separates:

```text
Compute lifecycle
from
Storage lifecycle
```

Core idea:

```text
Pod can die.
Volume can survive.
```

One picture:

```text
Pod lifecycle:
create -> run -> delete -> replace

Storage lifecycle:
provision -> attach -> mount -> persist -> reuse/delete
```

Do not memorize PV/PVC/StorageClass first.

Understand the production problem:

```text
How can temporary Pods safely use durable storage?
```

---

# 2. The Wrong Way To Think About Volumes

Wrong model:

```text
Volume is just a folder inside the container.
```

That is incomplete.

A container folder may be:

```text
Temporary container layer
emptyDir volume
hostPath on node
network disk
cloud block volume
NFS share
CSI provisioned storage
```

They behave very differently.

Another wrong model:

```text
PVC creates storage magically.
```

Not exactly.

PVC is a request.

StorageClass explains how to provision.

PV is the actual storage resource.

Pod mounts the PVC.

Correct mental model:

```text
Pod says:
  I need storage.

PVC says:
  I request 20Gi ReadWriteOnce storage.

StorageClass says:
  Use this storage provider and policy.

Provisioner says:
  Create actual disk.

PV says:
  This is the actual storage Kubernetes can bind.

Kubelet says:
  Attach and mount it into the Pod.
```

ASCII:

```text
Pod
 |
 | uses
 v
PVC
 |
 | binds to
 v
PV
 |
 | backed by
 v
Real storage
```

Storage is not one thing.

It is a chain.

---

# 3. Real World Analogy: Apartment, Tenant, Storage Room

Think of Kubernetes storage like an apartment building.

```text
Pod          = tenant
PVC          = tenant request form
PV           = actual storage room
StorageClass = type of storage service
Provisioner  = building manager creating/assigning rooms
```

Tenant says:

```text
I need a 20 square meter storage room.
I need exclusive access.
```

That is PVC.

Building has or creates:

```text
Storage room #A-204
20 square meters
locked
assigned to tenant
```

That is PV.

StorageClass says:

```text
Standard room
Premium room
SSD room
Backup-enabled room
Delete-after-use room
Keep-after-use room
```

ASCII:

```text
Tenant Pod
   |
   | needs storage
   v
PVC Request Form
   |
   | approved / matched
   v
PV Actual Room
   |
   | physically exists in
   v
Building Storage System
```

Important:

```text
The tenant may leave.
The storage room may remain.
```

Similarly:

```text
Pod may die.
PV may remain depending on reclaim policy.
```

---

# 4. Real World Analogy: Laptop vs External Hard Drive

Your laptop has an internal temporary workspace.

If you reinstall the OS, temporary local files may disappear.

But an external hard drive can be unplugged and reused.

```text
Container filesystem = laptop local workspace
Persistent volume    = external durable disk
```

ASCII:

```text
Without PV:

Pod
 |
 v
Container filesystem
 |
 v
Pod deleted -> data gone


With PV:

Pod
 |
 v
Mounted volume
 |
 v
External disk
 |
 v
Pod deleted -> disk may remain
```

This is why production apps should not rely on container writable layers.

For stateless services:

```text
Do not store business data in Pod filesystem.
```

For stateful systems:

```text
Use persistent storage with clear lifecycle.
```

---

# 5. Kubernetes Storage Core Picture

```text
Developer writes PVC
        |
        v
Kubernetes sees storage request
        |
        v
StorageClass selects provisioner
        |
        v
Provisioner creates real disk
        |
        v
PV object appears/binds
        |
        v
Pod references PVC
        |
        v
Kubelet attaches/mounts volume
        |
        v
Container sees mounted path
```

Diagram:

```text
+------------------+
| Pod              |
| mount /data      |
+--------+---------+
         |
         v
+------------------+
| PVC              |
| request 20Gi     |
+--------+---------+
         |
         v
+------------------+
| PV               |
| actual volume    |
+--------+---------+
         |
         v
+------------------+
| Cloud/NFS/CSI    |
| real storage     |
+------------------+
```

The important separation:

```text
Application developer usually asks through PVC.
Cluster/storage platform provides through PV + StorageClass.
```

That is Kubernetes abstraction.

---

# 6. Volume Mental Model

A Kubernetes volume is a directory made available to containers in a Pod.

But the backing source can vary.

Common examples:

```text
emptyDir
configMap
secret
persistentVolumeClaim
hostPath
nfs
csi
```

Simple Pod volume example:

```yaml
volumes:
  - name: app-cache
    emptyDir: {}

containers:
  - name: app
    volumeMounts:
      - name: app-cache
        mountPath: /cache
```

Mental model:

```text
volume = source of files/directories
volumeMount = where it appears inside container
```

ASCII:

```text
Volume source
     |
     v
Pod volume definition
     |
     v
Container mountPath
     |
     v
/app sees files
```

Important:

```text
Volume exists at Pod level.
volumeMount attaches it into a specific container.
```

A Pod can have multiple containers sharing same volume.

```text
Pod
 |
 +-- app container      mounts /data
 |
 +-- sidecar container  mounts /data
```

---

# 7. emptyDir Mental Model

`emptyDir` is created when a Pod is assigned to a Node.

It is deleted when the Pod is removed.

Example:

```yaml
volumes:
  - name: temp-work
    emptyDir: {}
```

Use cases:

```text
Temporary files
Scratch space
Shared files between containers in same Pod
Cache that can be lost
```

Not for:

```text
Database data
User uploads
Critical persistent files
```

ASCII:

```text
Pod starts
   |
   v
emptyDir created
   |
   v
Containers use it
   |
   v
Pod deleted
   |
   v
emptyDir deleted
```

Example sidecar pattern:

```text
App container writes logs to /logs
Sidecar reads /logs and ships them
```

Diagram:

```text
Pod
+----------------------------------+
| App Container                    |
| writes /logs/app.log             |
|        |                         |
|        v                         |
| emptyDir volume                  |
|        ^                         |
|        |                         |
| Log Sidecar reads /logs/app.log  |
+----------------------------------+
```

`emptyDir` is useful but not persistent.

---

# 8. hostPath Mental Model

`hostPath` mounts a file or directory from the Node into the Pod.

Example:

```yaml
volumes:
  - name: host-logs
    hostPath:
      path: /var/log
      type: Directory
```

Mental model:

```text
Pod sees part of the Node filesystem.
```

ASCII:

```text
Node filesystem
/var/log
   |
   v
Pod mount
/container/logs
```

Use carefully.

Good use cases:

```text
Node agents
Log collectors
Monitoring agents
CSI/node plugins
DaemonSets needing node files
```

Bad use cases:

```text
Application data for normal microservices
Database data without strict node pinning
Portable workloads
```

Problem:

```text
If Pod moves to another Node, data may not exist there.
```

Diagram:

```text
Node A has /data/file.txt
Pod runs on Node A -> sees file

Pod rescheduled to Node B
Node B /data may be empty
```

`hostPath` couples Pod to Node.

That breaks the normal Kubernetes portability model.

---

# 9. PersistentVolume Mental Model

A PersistentVolume is a Kubernetes object representing actual storage.

It may be backed by:

```text
Cloud disk
Network disk
NFS
CSI volume
Local disk
Storage appliance
```

PV answers:

```text
What real storage exists or was provisioned?
```

Example:

```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv-orders-data
spec:
  capacity:
    storage: 20Gi
  accessModes:
    - ReadWriteOnce
  persistentVolumeReclaimPolicy: Retain
  storageClassName: manual
  hostPath:
    path: /mnt/data/orders
```

This is a simple learning example.

Production usually uses CSI/cloud provisioners instead of manual hostPath PV.

Mental model:

```text
PV = actual storage resource known to Kubernetes
```

ASCII:

```text
PersistentVolume object
      |
      v
Actual disk/share/storage
```

Important:

```text
PV is cluster-scoped.
PVC is namespace-scoped.
```

A PV is not inside a namespace.

A PVC is.

---

# 10. PersistentVolumeClaim Mental Model

A PVC is a request for storage.

It says:

```text
I need:
- size
- access mode
- storage class
```

Example:

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: order-data-pvc
spec:
  accessModes:
    - ReadWriteOnce
  storageClassName: standard
  resources:
    requests:
      storage: 20Gi
```

Mental model:

```text
PVC = application storage request
```

The app usually does not care about actual disk implementation.

It says:

```text
Give me 20Gi storage with this access behavior.
```

Binding:

```text
PVC Pending
   |
   v
PV found/provisioned
   |
   v
PVC Bound
```

ASCII:

```text
PVC asks:
20Gi RWO standard
      |
      v
Kubernetes searches/provisions
      |
      v
PV matches
      |
      v
PVC Bound
```

A Pod uses PVC:

```yaml
volumes:
  - name: order-data
    persistentVolumeClaim:
      claimName: order-data-pvc
```

---

# 11. StorageClass Mental Model

StorageClass describes a class of storage and how to create it dynamically.

Example:

```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: fast-ssd
provisioner: csi.example.com
parameters:
  type: ssd
reclaimPolicy: Delete
volumeBindingMode: WaitForFirstConsumer
allowVolumeExpansion: true
```

Mental model:

```text
StorageClass = storage recipe
```

It can define:

```text
Provisioner
Disk type
Reclaim policy
Expansion support
Binding behavior
Filesystem options
Cloud-specific parameters
```

ASCII:

```text
PVC requests storageClassName: fast-ssd
             |
             v
StorageClass fast-ssd
             |
             v
CSI provisioner
             |
             v
Creates real disk
             |
             v
PV bound to PVC
```

Without StorageClass, dynamic provisioning may not happen.

PVC can remain Pending.

---

# 12. Dynamic Provisioning Dry Run

You create PVC:

```yaml
kind: PersistentVolumeClaim
metadata:
  name: order-data-pvc
spec:
  storageClassName: fast-ssd
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 20Gi
```

Dry run:

```text
1. API Server stores PVC.

2. PVC controller sees claim Pending.

3. It checks storageClassName = fast-ssd.

4. StorageClass says provisioner = csi.example.com.

5. External CSI provisioner sees PVC request.

6. CSI provisioner asks storage backend to create disk.

7. Storage backend creates disk volume.

8. PV object is created.

9. PVC binds to PV.

10. Pod referencing PVC can be scheduled/mounted.

11. Kubelet attaches disk to Node.

12. Kubelet mounts disk into container path.
```

ASCII:

```text
PVC Pending
    |
    v
StorageClass selected
    |
    v
CSI Provisioner
    |
    v
Real disk created
    |
    v
PV created
    |
    v
PVC Bound
    |
    v
Pod can mount
```

This is why StorageClass is powerful.

Developers request storage without manually creating disks.

---

# 13. Static Provisioning Dry Run

In static provisioning, an admin creates PV first.

```text
Admin creates PV
Developer creates PVC
PVC binds to matching PV
```

ASCII:

```text
Admin:
Real disk exists
   |
   v
PV created manually

Developer:
PVC requests storage
   |
   v
Kubernetes matches PVC to PV
```

Useful when:

```text
Storage already exists
Manual control needed
Legacy NFS share
Pre-created cloud disks
Migration scenarios
```

Flow:

```text
1. Admin creates disk/share.
2. Admin creates PV pointing to that storage.
3. Developer creates PVC.
4. Kubernetes binds matching PVC to PV.
5. Pod mounts PVC.
```

Static provisioning gives control.

Dynamic provisioning gives convenience.

Production clusters usually prefer dynamic provisioning for normal app disks.

---

# 14. PV/PVC Binding Rules

PVC binds to PV based on:

```text
StorageClass
Size
AccessModes
VolumeMode
Selector labels if used
Availability/topology constraints
```

Example:

```text
PVC asks:
20Gi, ReadWriteOnce, storageClass=fast

PV offers:
50Gi, ReadWriteOnce, storageClass=fast

Binding possible.
```

But:

```text
PVC asks:
20Gi, ReadWriteMany

PV offers:
50Gi, ReadWriteOnce

Binding not possible.
```

ASCII:

```text
PVC request
  storage: 20Gi
  access: RWO
  class: fast
        |
        v
Find PV satisfying all constraints
        |
        +-- match --> Bound
        |
        +-- no match --> Pending
```

Important:

```text
PVC may bind to larger PV.
PVC does not bind to smaller PV.
```

Common debugging:

```bash
kubectl get pvc
kubectl describe pvc order-data-pvc
kubectl get pv
kubectl describe pv <pv-name>
```

Look at events.

They usually tell why Pending remains Pending.

---

# 15. Access Modes Deep Model

Access modes describe how a volume can be mounted.

Common modes:

```text
ReadWriteOnce  (RWO)
ReadOnlyMany   (ROX)
ReadWriteMany  (RWX)
ReadWriteOncePod (RWOP)
```

## ReadWriteOnce

```text
Volume can be mounted read-write by one node.
```

Many people misunderstand this.

It usually means:

```text
One node at a time, not necessarily one Pod.
```

If multiple Pods are on same Node, they may share depending on storage/plugin behavior.

For databases, still prefer one writer Pod.

ASCII:

```text
RWO

Node A
  Pod 1 -> mount RW
  Pod 2 -> maybe possible same node

Node B
  Pod 3 -> cannot mount same disk RW simultaneously
```

## ReadWriteMany

```text
Multiple nodes can mount read-write.
```

Useful for:

```text
Shared file storage
NFS
EFS
Some distributed filesystems
```

ASCII:

```text
RWX

Node A Pod -> /shared
Node B Pod -> /shared
Node C Pod -> /shared
```

Important:

```text
Not all storage providers support RWX.
Block disks commonly support RWO, not RWX.
```

---

# 16. VolumeMode: Filesystem vs Block

PVC can request:

```text
Filesystem
Block
```

Filesystem is common:

```text
Pod sees mounted directory.
```

Block means:

```text
Pod gets raw block device.
```

Example:

```yaml
volumeMode: Filesystem
```

Most Spring Boot services use filesystem.

Databases may use filesystem.

Special storage-aware apps may use raw block.

ASCII:

```text
Filesystem mode:

Disk
 |
 v
Formatted filesystem
 |
 v
Mounted directory /data


Block mode:

Disk
 |
 v
Raw device
 |
 v
App manages block access
```

For most developers:

```text
Use Filesystem unless you have a strong reason.
```

---

# 17. Reclaim Policy Mental Model

Reclaim policy decides what happens to PV when PVC is deleted.

Common policies:

```text
Delete
Retain
```

## Delete

```text
PVC deleted
   |
   v
PV deleted
   |
   v
Underlying storage deleted
```

Good for:

```text
Temporary environments
Ephemeral dev databases
CI workloads
```

Danger:

```text
Deleting PVC can delete real data.
```

## Retain

```text
PVC deleted
   |
   v
PV remains
   |
   v
Underlying storage remains
```

Good for:

```text
Important production data
Manual recovery
Forensic investigation
```

ASCII:

```text
Delete policy:
PVC gone -> PV gone -> Disk gone

Retain policy:
PVC gone -> PV released -> Disk kept
```

Production mindset:

```text
Know the reclaim policy before deleting PVC.
```

Command:

```bash
kubectl get pv
```

Check:

```text
RECLAIM POLICY
```

---

# 18. Volume Binding Mode

StorageClass can define:

```text
volumeBindingMode: Immediate
volumeBindingMode: WaitForFirstConsumer
```

## Immediate

Volume is provisioned/bound as soon as PVC is created.

Problem:

```text
Disk may be created in zone A.
Pod may be scheduled to zone B.
Mount fails due to topology mismatch.
```

## WaitForFirstConsumer

Provisioning waits until a Pod uses the PVC.

Then scheduler can consider Pod placement and storage topology together.

ASCII:

```text
Immediate:

PVC created
   |
   v
Disk created in Zone A
   |
   v
Pod scheduled Zone B
   |
   v
Problem


WaitForFirstConsumer:

PVC created
   |
   v
Wait
   |
   v
Pod created
   |
   v
Scheduler chooses zone
   |
   v
Disk created in same zone
```

Production recommendation:

```text
For zonal block storage, WaitForFirstConsumer is safer.
```

---

# 19. Pod Mounting PVC

Pod example:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: order-service
spec:
  containers:
    - name: order-service
      image: registry.example.com/order-service:1.0.0
      volumeMounts:
        - name: order-data
          mountPath: /app/data
  volumes:
    - name: order-data
      persistentVolumeClaim:
        claimName: order-data-pvc
```

Mental model:

```text
PVC is not automatically visible.
Pod must mount it.
```

ASCII:

```text
PVC Bound to PV
      |
      v
Pod volume references PVC
      |
      v
Container volumeMount maps it
      |
      v
/app/data inside container
```

Common mistake:

```text
PVC exists and Bound.
App still writes to /tmp/uploads.
```

The app must write to the mounted path.

Spring Boot property:

```yaml
app:
  upload-dir: /app/data/uploads
```

---

# 20. Spring Boot File Upload Example

Controller:

```java
package com.example.files.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileUploadController {

    private final Path uploadDir;

    public FileUploadController(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir);
    }

    @PostMapping
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
        Files.createDirectories(uploadDir);

        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path target = uploadDir.resolve(filename);

        file.transferTo(target);

        return ResponseEntity.ok(filename);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable String filename) throws Exception {
        Path target = uploadDir.resolve(filename);
        return ResponseEntity.ok(Files.readAllBytes(target));
    }
}
```

Config:

```yaml
app:
  upload-dir: /app/data/uploads
```

Kubernetes mount:

```yaml
volumeMounts:
  - name: upload-storage
    mountPath: /app/data
```

ASCII:

```text
HTTP upload
   |
   v
Spring Controller
   |
   v
/app/data/uploads/file.pdf
   |
   v
PVC mounted volume
   |
   v
Persistent storage
```

Warning:

```text
This is good for learning.
For high-scale production file uploads, object storage like S3/GCS/Azure Blob is often better.
```

---

# 21. Full Deployment With PVC

PVC:

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: order-upload-pvc
spec:
  accessModes:
    - ReadWriteOnce
  storageClassName: standard
  resources:
    requests:
      storage: 20Gi
```

Deployment:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
        - name: order-service
          image: registry.example.com/order-service:1.0.0
          ports:
            - containerPort: 8080
          env:
            - name: APP_UPLOAD_DIR
              value: /app/data/uploads
          volumeMounts:
            - name: upload-storage
              mountPath: /app/data
      volumes:
        - name: upload-storage
          persistentVolumeClaim:
            claimName: order-upload-pvc
```

Why replicas = 1?

Because with RWO volume, multiple replicas across nodes may not mount same disk.

ASCII:

```text
Deployment replicas=1
       |
       v
Pod
       |
       v
PVC RWO
       |
       v
One writable disk
```

For multi-replica file upload services, prefer object storage or RWX storage.

---

# 22. Why StatefulSet Uses PVC Templates

Deployment with one shared PVC is often wrong for databases.

Bad model:

```text
3 database Pods share one RWO disk
```

Correct model:

```text
Each database Pod gets its own disk.
```

StatefulSet supports `volumeClaimTemplates`.

Example idea:

```text
postgres-0 -> pvc data-postgres-0 -> disk 0
postgres-1 -> pvc data-postgres-1 -> disk 1
postgres-2 -> pvc data-postgres-2 -> disk 2
```

ASCII:

```text
StatefulSet postgres
     |
     +-- Pod postgres-0 -> PVC data-postgres-0 -> PV disk-0
     |
     +-- Pod postgres-1 -> PVC data-postgres-1 -> PV disk-1
     |
     +-- Pod postgres-2 -> PVC data-postgres-2 -> PV disk-2
```

This matches stateful identity.

```text
Pod identity + storage identity stay together.
```

Deployment is for replaceable stateless replicas.

StatefulSet is for stable identity + stable storage.

---

# 23. StatefulSet Storage Example

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: mini-postgres
spec:
  serviceName: mini-postgres
  replicas: 1
  selector:
    matchLabels:
      app: mini-postgres
  template:
    metadata:
      labels:
        app: mini-postgres
    spec:
      containers:
        - name: postgres
          image: postgres:16
          ports:
            - containerPort: 5432
          env:
            - name: POSTGRES_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: postgres-secret
                  key: POSTGRES_PASSWORD
          volumeMounts:
            - name: postgres-data
              mountPath: /var/lib/postgresql/data
  volumeClaimTemplates:
    - metadata:
        name: postgres-data
      spec:
        accessModes:
          - ReadWriteOnce
        storageClassName: standard
        resources:
          requests:
            storage: 20Gi
```

Flow:

```text
StatefulSet creates Pod mini-postgres-0
        |
        v
PVC postgres-data-mini-postgres-0
        |
        v
PV provisioned
        |
        v
Mounted at /var/lib/postgresql/data
```

ASCII:

```text
mini-postgres-0
      |
      v
postgres-data-mini-postgres-0
      |
      v
PV / disk
      |
      v
Postgres data directory
```

This is the common pattern for stateful workloads.

---

# 24. Database On Kubernetes Reality

Can you run databases on Kubernetes?

Yes.

Should you always?

No.

Understand tradeoff.

Kubernetes gives:

```text
Scheduling
Restart
Persistent storage attachment
StatefulSet identity
Health checks
Resource limits
```

But databases also need:

```text
Backups
Restore drills
Replication
Failover
Storage performance
Upgrade strategy
Monitoring
Data corruption handling
Operational expertise
```

ASCII:

```text
Database on K8s
      |
      +-- Kubernetes handles Pod orchestration
      |
      +-- Storage system handles durable disk
      |
      +-- DB operator handles DB lifecycle
      |
      +-- Humans still need backup/restore strategy
```

For product companies:

```text
Managed database is often better initially.
```

Examples:

```text
RDS / Cloud SQL / Azure Database
Managed Redis
Managed Kafka
Managed Elasticsearch
```

Kubernetes storage knowledge is still critical because many systems use PVCs for:

```text
Kafka brokers
Elasticsearch data nodes
Prometheus TSDB
Grafana storage
CI runners
Stateful internal tools
```

---

# 25. Volume Expansion

Some StorageClasses allow expansion:

```yaml
allowVolumeExpansion: true
```

PVC request can be increased:

```yaml
resources:
  requests:
    storage: 50Gi
```

Flow:

```text
PVC 20Gi
   |
   v
Change request to 50Gi
   |
   v
Controller expands backend volume
   |
   v
Filesystem resize happens
   |
   v
Pod sees larger space
```

ASCII:

```text
Before:
PVC 20Gi -> PV 20Gi -> Disk 20Gi

After:
PVC 50Gi -> PV 50Gi -> Disk 50Gi
```

Important:

```text
Expansion usually supports increase, not shrink.
```

Common issue:

```text
PVC resized but filesystem inside Pod not updated yet.
```

Debug:

```bash
kubectl describe pvc <pvc>
kubectl exec <pod> -- df -h
```

Production mindset:

```text
Plan storage growth.
Monitor disk usage before emergency.
```

---

# 26. Snapshots and Backups

PVC is not backup.

PV is not backup.

A disk existing does not mean you can recover from:

```text
Accidental delete
Data corruption
Bad migration
Ransomware
Application bug
Wrong batch job
```

Backup mental model:

```text
Persistent volume = current data
Snapshot/backup     = recoverable copy from earlier time
```

ASCII:

```text
PV current data
      |
      +--> snapshot at 10:00
      |
      +--> snapshot at 11:00
      |
      +--> snapshot at 12:00
```

VolumeSnapshot is common with CSI drivers.

But application-consistent backups may require database-level tools.

For Postgres:

```text
pg_dump
WAL archiving
physical backups
managed snapshots with freeze/coordination
```

For Kafka:

```text
Replication is not backup.
Topic retention is not backup.
```

Production rule:

```text
If restore has not been tested, backup is only hope, not proof.
```

---

# 27. Storage Performance Mental Model

Storage has performance dimensions:

```text
IOPS
Throughput
Latency
Filesystem overhead
Network distance
Replication overhead
```

A PVC with 100Gi does not automatically mean fast.

Examples:

```text
Small cloud disks may have low IOPS.
Network filesystems may have higher latency.
RWX storage may be slower than local block.
```

ASCII:

```text
App write
   |
   v
Filesystem
   |
   v
Kernel / container runtime
   |
   v
Node storage plugin
   |
   v
Network / cloud API
   |
   v
Storage backend
```

Every layer adds behavior.

Symptoms of storage bottleneck:

```text
High request latency
Database slow commits
Kafka under-replicated partitions
Elasticsearch indexing slow
Prometheus compaction lag
Pod stuck terminating due to unmount
```

Debug:

```bash
kubectl top pod
kubectl describe pod
kubectl logs
df -h
iostat if available
application metrics
storage provider metrics
```

Kubernetes tells you orchestration state.

Storage backend tells you disk performance.

---

# 28. Production Story: Pod Rescheduled, Data Lost

Developer uses emptyDir:

```yaml
volumes:
  - name: uploads
    emptyDir: {}
```

App stores user uploads:

```text
/app/uploads
```

Everything works for weeks.

Then Node drains:

```bash
kubectl drain node-1
```

Pod is recreated on another Node.

Uploads disappeared.

Root cause:

```text
emptyDir is tied to Pod lifecycle.
```

ASCII:

```text
Pod A on Node 1
  emptyDir contains files

Pod deleted
  emptyDir deleted

Pod B on Node 2
  new emptyDir is empty
```

Correct choices:

```text
Use PVC for durable file storage.
Use object storage for scalable uploads.
```

Lesson:

```text
Running successfully for weeks does not prove storage is durable.
It may only prove the Pod did not die yet.
```

---

# 29. Production Story: PVC Pending

PVC status:

```text
Pending
```

Possible causes:

```text
StorageClass does not exist
No default StorageClass
Provisioner not running
Access mode unsupported
Insufficient storage
Topology constraints
WaitForFirstConsumer but no Pod uses PVC yet
```

Debug:

```bash
kubectl get pvc
kubectl describe pvc order-data-pvc
kubectl get storageclass
kubectl get pods -n kube-system
```

Look at events:

```text
storageclass.storage.k8s.io "fast-ssd" not found
waiting for first consumer to be created before binding
failed to provision volume
```

ASCII:

```text
PVC Pending
    |
    +-- class missing?
    +-- provisioner down?
    +-- unsupported mode?
    +-- waiting for Pod?
    +-- topology issue?
```

Do not guess.

Read PVC events.

They are usually very clear.

---

# 30. Production Story: Multi-Attach Error

Error:

```text
Multi-Attach error for volume
Volume is already exclusively attached to one node
```

Common cause:

```text
RWO disk attached to Node A.
New Pod scheduled on Node B.
Disk cannot attach to both.
```

ASCII:

```text
Node A
  old Pod still using disk

Node B
  new Pod wants same RWO disk

Storage backend:
  no, one writer attachment only
```

Scenarios:

```text
Rolling update with shared PVC
Old Pod terminating slowly
Deployment replicas > 1 using one RWO PVC
Node failure leaves volume attachment stale
```

Fix ideas:

```text
Use replicas=1 for RWO PVC with Deployment.
Use StatefulSet with per-Pod PVC.
Use RWX storage if true sharing is required.
Ensure old Pod terminates before new Pod mounts.
Investigate VolumeAttachment objects if stuck.
```

Command:

```bash
kubectl get volumeattachment
kubectl describe pod <pod>
```

Lesson:

```text
Access mode must match workload topology.
```

---

# 31. Production Story: Permission Denied

Pod starts.

App logs:

```text
java.nio.file.AccessDeniedException: /app/data/uploads
```

Possible cause:

```text
Mounted volume owned by root.
Java process runs as non-root user.
```

ASCII:

```text
PV mounted at /app/data
      |
      v
Owned by root:root
      |
      v
Spring Boot runs as UID 1000
      |
      v
Cannot write
```

Fix options:

```yaml
securityContext:
  fsGroup: 1000
```

or initContainer:

```yaml
initContainers:
  - name: fix-permissions
    image: busybox
    command: ["sh", "-c", "chown -R 1000:1000 /app/data"]
    volumeMounts:
      - name: upload-storage
        mountPath: /app/data
```

Be careful with large volumes:

```text
Recursive chown on huge data can slow startup.
```

Production mindset:

```text
Storage permission is part of deployment design.
```

---

# 32. Production Story: Disk Full

App errors:

```text
No space left on device
```

Inside container:

```bash
df -h /app/data
```

PVC shows:

```text
20Gi used 100%
```

Symptoms:

```text
Uploads failing
Database cannot write WAL
Kafka broker unstable
Prometheus cannot compact
```

Debug:

```bash
kubectl exec <pod> -- df -h
kubectl exec <pod> -- du -sh /app/data/*
kubectl describe pvc <pvc>
```

Fix:

```text
Delete unnecessary files if safe.
Expand PVC if supported.
Move old data to object storage.
Increase retention cleanup.
Add monitoring alert at 70/80/90%.
```

ASCII:

```text
Writes continue
   |
   v
Disk fills
   |
   v
Application errors
   |
   v
Readiness/liveness may fail
```

Rule:

```text
Storage without monitoring becomes future outage.
```

---

# 33. Spring Boot Storage Properties

Better than hardcoding:

```java
Path.of("/app/data/uploads")
```

Use property:

```yaml
app:
  upload-dir: /app/data/uploads
```

Java:

```java
@ConfigurationProperties(prefix = "app")
public class StorageProperties {
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
```

Validation:

```java
@PostConstruct
public void validateStorage() throws Exception {
    Path path = Path.of(uploadDir);
    Files.createDirectories(path);

    if (!Files.isWritable(path)) {
        throw new IllegalStateException("Upload directory is not writable: " + uploadDir);
    }
}
```

Mental model:

```text
Fail fast if volume is not mounted or not writable.
```

ASCII:

```text
App startup
   |
   v
Check /app/data/uploads exists
   |
   +-- writable --> start
   |
   +-- not writable --> fail fast
```

This prevents broken Pods from receiving traffic.

---

# 34. Health Checks For Storage

Storage-aware readiness can be useful.

Example:

```java
package com.example.files.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class StorageHealthIndicator implements HealthIndicator {

    private final Path uploadDir;

    public StorageHealthIndicator(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir);
    }

    @Override
    public Health health() {
        try {
            Files.createDirectories(uploadDir);

            if (!Files.isWritable(uploadDir)) {
                return Health.down()
                        .withDetail("reason", "upload directory not writable")
                        .build();
            }

            return Health.up().build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
```

Kubernetes readiness:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
```

ASCII:

```text
Kubernetes readiness probe
          |
          v
Spring Boot health
          |
          v
Check mounted storage
          |
          +-- OK -> traffic allowed
          |
          +-- DOWN -> no traffic
```

Warning:

```text
Do not perform expensive disk checks every second.
```

---

# 35. PVC With ConfigMap and Secret Together

Real app usually uses all three:

```text
ConfigMap:
  app.upload-dir=/app/data/uploads
  log level
  DB URL

Secret:
  DB password

PVC:
  file storage
```

ASCII:

```text
+-----------+      +--------+      +------+
| ConfigMap |      | Secret |      | PVC  |
+-----+-----+      +---+----+      +--+---+
      |                |              |
      +--------+-------+--------------+
               |
               v
          Spring Boot Pod
               |
               v
          /app/data mounted
```

Deployment fragment:

```yaml
envFrom:
  - configMapRef:
      name: file-service-config
  - secretRef:
      name: file-service-secret

volumeMounts:
  - name: upload-storage
    mountPath: /app/data

volumes:
  - name: upload-storage
    persistentVolumeClaim:
      claimName: file-upload-pvc
```

Mental model:

```text
Config tells app where to write.
Secret tells app how to authenticate.
PVC provides durable place to write.
```

---

# 36. Object Storage vs PVC

For uploads at scale, object storage is often better.

PVC model:

```text
App writes file to mounted disk.
```

Object storage model:

```text
App writes file to S3/GCS/Azure Blob/MinIO.
```

Comparison:

```text
PVC:
  + simple POSIX file path
  + good for single instance tools
  + useful for databases/stateful apps
  - sharing across replicas is hard unless RWX
  - backup/scaling lifecycle is your problem

Object Storage:
  + highly durable
  + scales across app replicas
  + CDN integration
  + easier lifecycle policies
  - app must use SDK/API
  - not normal filesystem semantics
```

ASCII:

```text
PVC upload:

User -> Pod -> /app/data -> PV disk


Object storage upload:

User -> Pod -> S3/GCS bucket
         |
         +-- any replica can serve metadata
```

Production rule:

```text
Use PVC for stateful systems needing filesystem/block semantics.
Use object storage for user-uploaded blobs when scale matters.
```

---

# 37. Local PV Mental Model

Local PersistentVolume uses disk physically attached to a node.

It can be fast.

But it ties storage to node.

ASCII:

```text
Node A
  local disk /mnt/ssd1
      |
      v
  PV local-volume-a
      |
      v
  Pod must run on Node A
```

If Node A fails:

```text
Data is still on Node A disk,
but Pod cannot simply run on Node B with same data.
```

Use cases:

```text
High-performance local storage
Specialized databases with replication
Kafka with broker replication
Elasticsearch with shard replication
```

Not good for:

```text
Single-copy critical data without replication
Apps expecting easy rescheduling anywhere
```

Mental model:

```text
Local PV gives speed but reduces scheduling freedom.
```

Kubernetes can track node affinity for local PVs.

---

# 38. CSI Mental Model

CSI means Container Storage Interface.

It is a standard way for storage providers to plug into Kubernetes.

Before CSI, Kubernetes had many in-tree volume plugins.

CSI makes storage integration external and standardized.

ASCII:

```text
Kubernetes
   |
   v
CSI interface
   |
   v
Storage driver
   |
   v
Cloud / SAN / NAS / storage backend
```

CSI components commonly handle:

```text
Provision volume
Attach volume
Mount volume
Expand volume
Snapshot volume
Delete volume
```

Mental model:

```text
Kubernetes asks.
CSI driver performs storage-specific work.
```

You do not need to memorize CSI internals first.

Know where it fits:

```text
PVC -> StorageClass -> CSI provisioner -> real storage
```

When storage breaks, check CSI components/events.

---

# 39. Full End-To-End Dry Run

You deploy a Spring Boot file service with PVC.

```text
1. Apply StorageClass.
2. Apply PVC.
3. Apply Deployment.
4. PVC waits or provisions depending on binding mode.
5. Pod is created.
6. Scheduler sees Pod uses PVC.
7. Storage is provisioned/bound.
8. Pod scheduled to compatible Node.
9. Attach controller attaches volume to Node.
10. Kubelet mounts filesystem.
11. Container starts.
12. Spring Boot reads APP_UPLOAD_DIR.
13. App checks /app/data/uploads writable.
14. Readiness passes.
15. User uploads file.
16. File is written to mounted PV.
17. Pod crashes.
18. Replacement Pod mounts same PVC.
19. File is still available.
```

ASCII:

```text
User upload
   |
   v
Service
   |
   v
Pod
   |
   v
/app/data/uploads
   |
   v
PVC
   |
   v
PV
   |
   v
Real disk
```

Failure recovery:

```text
Pod dies
   |
   v
New Pod
   |
   v
same PVC
   |
   v
same data
```

This is the value of persistent volumes.

---

# 40. Debugging Mindset: Layer By Layer

Storage debugging chain:

```text
1. Does PVC exist?
2. Is PVC Bound?
3. Does StorageClass exist?
4. Is provisioner healthy?
5. Does PV exist?
6. Is reclaim policy correct?
7. Is access mode compatible?
8. Is Pod referencing correct PVC?
9. Is volume attached?
10. Is volume mounted?
11. Is mount path correct?
12. Are permissions correct?
13. Is disk full?
14. Is app writing to mounted path?
15. Is backup/restore available?
```

ASCII:

```text
PVC
 |
 v
PV
 |
 v
Storage backend
 |
 v
Attach
 |
 v
Mount
 |
 v
Container path
 |
 v
Application write
```

Commands:

```bash
kubectl get storageclass
kubectl get pvc -A
kubectl describe pvc <pvc> -n <ns>
kubectl get pv
kubectl describe pv <pv>
kubectl get pods -o wide -n <ns>
kubectl describe pod <pod> -n <ns>
kubectl logs <pod> -n <ns>
kubectl exec <pod> -n <ns> -- df -h
kubectl exec <pod> -n <ns> -- mount
kubectl exec <pod> -n <ns> -- ls -lah /app/data
```

Do not debug storage only from the app logs.

Follow the chain.

---

# 41. Common Mistakes

```text
Mistake 1:
Using emptyDir for user uploads.
Correct:
Use PVC or object storage.

Mistake 2:
Using one RWO PVC with multiple replicas across nodes.
Correct:
Use one replica, RWX storage, object storage, or StatefulSet per-Pod PVC.

Mistake 3:
Thinking PVC itself is backup.
Correct:
Backups/snapshots are separate recovery mechanisms.

Mistake 4:
Deleting PVC without checking reclaim policy.
Correct:
Know whether data will be deleted or retained.

Mistake 5:
Using hostPath for normal app persistence.
Correct:
Avoid node-coupled storage for portable workloads.

Mistake 6:
Mounting PVC but app writes elsewhere.
Correct:
Align app config with mountPath.

Mistake 7:
Ignoring filesystem permissions.
Correct:
Use securityContext/fsGroup or proper image UID strategy.

Mistake 8:
Not monitoring disk usage.
Correct:
Alert before disk full.

Mistake 9:
Assuming RWX is always available.
Correct:
Check storage provider access mode support.

Mistake 10:
Running production database on PVC without backup/restore drills.
Correct:
Storage durability is not operational recovery.
```

---

# 42. Interview Questions

## What is a Kubernetes volume?

A Kubernetes volume is a directory made available to containers in a Pod. The backing source may be temporary storage, a ConfigMap, a Secret, a host path, a persistent volume claim, or a CSI-provisioned storage backend.

## What is a PersistentVolume?

A PersistentVolume is a cluster-scoped Kubernetes object representing actual storage available to the cluster. It may be manually created or dynamically provisioned by a storage provider.

## What is a PersistentVolumeClaim?

A PersistentVolumeClaim is a namespace-scoped request for storage. It specifies size, access mode, and optionally StorageClass. Pods use PVCs to mount persistent storage.

## What is StorageClass?

A StorageClass defines how storage should be dynamically provisioned. It points to a provisioner, such as a CSI driver, and may include parameters, reclaim policy, expansion support, and binding mode.

## PV vs PVC?

PV is the actual storage resource. PVC is the application’s request for storage. Kubernetes binds a PVC to a suitable PV or dynamically provisions one through a StorageClass.

## What is dynamic provisioning?

Dynamic provisioning means Kubernetes automatically creates storage when a PVC is created, using the specified StorageClass and provisioner.

## What is ReadWriteOnce?

ReadWriteOnce means the volume can be mounted read-write by one node at a time. It does not mean unlimited Pods across many nodes can share it.

## What is ReadWriteMany?

ReadWriteMany means the volume can be mounted read-write by multiple nodes. It usually requires shared/network filesystem storage.

## What happens when PVC is deleted?

It depends on the PV reclaim policy. With Delete, the PV and underlying storage may be deleted. With Retain, the PV/storage remains for manual recovery or reuse.

## Why can a PVC stay Pending?

Common reasons include missing StorageClass, no default StorageClass, unhealthy provisioner, unsupported access mode, topology constraints, insufficient capacity, or WaitForFirstConsumer waiting for a Pod.

## Why use StatefulSet with PVC?

StatefulSet can create stable per-Pod PVCs using volumeClaimTemplates. This is important for workloads where each replica needs its own durable identity and storage, such as databases and brokers.

## Is PVC enough for production data safety?

No. PVC provides persistence, not complete recovery. Production data also needs backups, restore testing, monitoring, security, replication, and operational procedures.

---

# 43. Cheat Sheet

```text
Volume
  Directory made available to containers in a Pod

volumeMount
  Path where the volume appears inside the container

emptyDir
  Temporary Pod-lifetime storage

hostPath
  Mount Node filesystem path into Pod

PV
  Actual storage resource

PVC
  Request for storage

StorageClass
  Storage provisioning recipe

CSI
  Standard storage plugin interface

RWO
  ReadWriteOnce, one node read-write

RWX
  ReadWriteMany, many nodes read-write

ReclaimPolicy Delete
  Delete storage when claim is deleted

ReclaimPolicy Retain
  Keep storage after claim deletion

WaitForFirstConsumer
  Delay storage binding until Pod scheduling
```

Commands:

```bash
kubectl get storageclass
kubectl get pvc -A
kubectl describe pvc <pvc> -n <namespace>
kubectl get pv
kubectl describe pv <pv>
kubectl describe pod <pod> -n <namespace>
kubectl exec <pod> -n <namespace> -- df -h
kubectl exec <pod> -n <namespace> -- ls -lah /app/data
kubectl rollout restart deployment/<name> -n <namespace>
```

Decision hooks:

```text
Temporary scratch data       -> emptyDir
Node agent needs node files  -> hostPath
Single app durable files     -> PVC
Database/broker per replica  -> StatefulSet + volumeClaimTemplates
Many replicas shared files   -> RWX storage or object storage
User uploads at scale        -> object storage
Sensitive cert files         -> Secret volume
```

---

# 44. One Picture To Remember

```text
                         Application Pod
                              |
                              | volumeMount: /app/data
                              v
                     +------------------+
                     | PVC              |
                     | order-data-pvc   |
                     | 20Gi RWO         |
                     +---------+--------+
                               |
                               | bound to
                               v
                     +------------------+
                     | PV               |
                     | actual volume    |
                     +---------+--------+
                               |
                               | provisioned by
                               v
                     +------------------+
                     | StorageClass     |
                     | standard / fast  |
                     +---------+--------+
                               |
                               | via CSI
                               v
                     +------------------+
                     | Real Storage     |
                     | cloud disk/NFS   |
                     +------------------+

Rule:

Pod is temporary.
PVC is the app's storage request.
PV is the actual storage.
StorageClass is the provisioning recipe.
CSI/storage backend creates and attaches the real disk.
```

---

# 45. Final Production Checklist

```text
[ ] I know whether the data is temporary or durable.
[ ] I do not use emptyDir for business-critical data.
[ ] I know whether PVC or object storage is the better fit.
[ ] PVC is Bound before relying on it.
[ ] StorageClass exists and matches access mode needs.
[ ] Reclaim policy is understood before deletion.
[ ] Access mode matches replica strategy.
[ ] RWO PVC is not blindly shared by many replicas across nodes.
[ ] Stateful workloads use StatefulSet with per-Pod PVC when needed.
[ ] App writes to the mounted path, not container local path.
[ ] File permissions allow the container user to read/write.
[ ] Disk usage is monitored.
[ ] PVC expansion plan exists.
[ ] Backup and restore are tested.
[ ] Storage performance metrics are monitored.
[ ] Debugging follows PVC -> PV -> backend -> attach -> mount -> app path.
```

---

# 46. Final Memory Hook

Do not memorize storage YAML first.

Remember the chain:

```text
App needs durable path
        |
        v
Pod mounts PVC
        |
        v
PVC requests storage
        |
        v
PV represents actual storage
        |
        v
StorageClass/CSI provision real disk
        |
        v
Kubelet attaches and mounts
        |
        v
App reads/writes files
```

Final sentence:

```text
Kubernetes storage is the bridge between temporary Pods and durable business data; understand the lifecycle before trusting the mount path.
```
