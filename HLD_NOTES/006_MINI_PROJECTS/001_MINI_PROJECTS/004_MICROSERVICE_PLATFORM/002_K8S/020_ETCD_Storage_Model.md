# 020_ETCD_Storage_Model.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why etcd Exists

Kubernetes needs memory.

Not memory like RAM.

Memory like:

```text
What Deployments exist?
How many replicas were requested?
Which Pods are assigned to which Nodes?
Which Services exist?
Which Secrets exist?
Which Nodes are Ready?
What is the latest resourceVersion of an object?
```

Without this memory, Kubernetes would be only a temporary process.

If the API Server restarted, the cluster would forget everything.

That cannot work in production.

So Kubernetes uses `etcd` as its durable cluster state store.

Mental model:

```text
Kubernetes API Server = front desk
etcd                  = official record book
Controllers           = workers reading the record book
Kubelets              = node agents reporting reality
```

Do not memorize etcd as only a database.

Understand the production need:

```text
Kubernetes must remember desired state and observed state reliably.
```

One picture:

```text
User applies YAML
      |
      v
API Server validates it
      |
      v
etcd stores it durably
      |
      v
Controllers watch changes
      |
      v
Cluster moves toward desired state
```

If etcd is unhealthy, Kubernetes control plane becomes unhealthy.

Your existing Pods may keep running, but the cluster loses its ability to safely accept, store, and coordinate changes.

---

# 2. The Wrong Way To Think About etcd

Bad mental model:

```text
Pods run inside etcd.
Services run inside etcd.
etcd sends traffic.
etcd creates containers.
```

Wrong.

etcd does not run your application.

etcd stores records about your application.

Correct model:

```text
Pod object stored in etcd
Actual container runs on a Node
Kubelet starts the container
Container runtime executes it
```

Diagram:

```text
+-------------------+          +-----------------------------+
| etcd              |          | Worker Node                 |
|                   |          |                             |
| /pods/order-abc   |          | kubelet                     |
| spec/status       |          | container runtime           |
+---------+---------+          | java -jar order-service.jar |
          |                    +-----------------------------+
          |
          | API object says what should exist
          v
Kubernetes components act on that object
```

A Pod record is like a birth certificate.

The person is not living inside the certificate.

The certificate records identity and state.

Similarly:

```text
etcd stores cluster truth.
Nodes run workloads.
```

---

# 3. Real World Analogy: Government Land Registry

Imagine a city.

People own houses.

The government maintains a land registry.

The registry stores:

```text
Owner name
Plot number
Boundary
Legal status
Transfer history
```

The house is not inside the registry.

But if there is a dispute, the registry is the official source of truth.

Kubernetes is similar.

```text
Actual Pods        = houses in the city
etcd records       = land registry
API Server         = government office counter
Controllers        = officials acting on records
Kubelet            = local building manager
```

ASCII:

```text
Developer request
      |
      v
+--------------------+
| API Server Counter |
+---------+----------+
          |
          v
+--------------------+
| etcd Registry      |
| official records   |
+---------+----------+
          |
          v
+--------------------+
| Controllers        |
| act on records     |
+--------------------+
```

If the land registry is corrupted, the city still physically exists, but legal coordination collapses.

If etcd is corrupted, Pods may still run for some time, but Kubernetes loses trusted cluster state.

---

# 4. What etcd Stores

etcd stores Kubernetes API objects.

Examples:

```text
Namespaces
Deployments
ReplicaSets
Pods
Services
Endpoints / EndpointSlices
ConfigMaps
Secrets
Nodes
PersistentVolumeClaims
Ingresses
Custom Resources
Lease objects
Events
```

But remember:

```text
etcd stores object data, not running containers.
```

A simplified view:

```text
/registry/deployments/default/order-service
/registry/replicasets/default/order-service-6d9f
/registry/pods/default/order-service-abc
/registry/services/default/order-service
/registry/configmaps/default/order-config
/registry/secrets/default/order-secret
/registry/nodes/node-1
```

ASCII:

```text
+------------------------------------------------+
| etcd key-value store                           |
+------------------------------------------------+
| key                                            |
| /registry/pods/default/order-abc              |
|                                                |
| value                                          |
| apiVersion: v1                                 |
| kind: Pod                                      |
| metadata: ...                                  |
| spec: ...                                      |
| status: ...                                    |
+------------------------------------------------+
```

Do not think SQL tables first.

Think:

```text
Hierarchical keys -> serialized Kubernetes objects
```

---

# 5. Key-Value Store Mental Model

etcd is a distributed key-value store.

A key-value store maps:

```text
key -> value
```

Simple example:

```text
key:   /registry/services/default/order-service
value: serialized Service object
```

The value is not usually read by humans directly during normal Kubernetes use.

You use Kubernetes APIs:

```bash
kubectl get service order-service -o yaml
```

kubectl talks to API Server.

API Server reads from etcd.

Flow:

```text
kubectl
  |
  v
API Server
  |
  v
etcd
```

Not:

```text
kubectl -> etcd directly
```

Correct access pattern:

```text
All normal Kubernetes state access goes through API Server.
```

ASCII:

```text
Correct:

kubectl ---> API Server ---> etcd

Wrong:

kubectl ------------------> etcd
```

This protects validation, authorization, admission, audit, and API consistency.

---

# 6. API Server And etcd Relationship

The API Server is the only Kubernetes component that directly talks to etcd in normal cluster operation.

Controllers do not usually directly write etcd.

Scheduler does not directly write etcd.

Kubelet does not directly write etcd.

They all communicate through API Server.

Diagram:

```text
                 +------------------+
                 |      etcd        |
                 | cluster storage  |
                 +---------+--------+
                           ^
                           |
                    direct storage API
                           |
+-----------+      +-------+--------+
| kubectl   | ---> | API Server     |
+-----------+      +-------+--------+
                           ^
                           |
        +------------------+-------------------+
        |                  |                   |
        v                  v                   v
  Controllers          Scheduler            Kubelets
```

Why this matters:

```text
API Server centralizes:
- authentication
- authorization
- admission control
- validation
- conversion
- defaulting
- optimistic concurrency
- watch streams
- audit logs
```

If every component wrote directly to etcd, cluster consistency would be difficult to enforce.

---

# 7. Desired State And Observed State In etcd

A Kubernetes object often contains:

```text
spec   = desired state
status = observed state
```

For Deployment:

```yaml
spec:
  replicas: 3
status:
  replicas: 3
  availableReplicas: 2
```

Both can be stored as part of the object, but they are updated by different actors.

Human or automation writes desired state:

```text
spec.replicas = 3
```

Controller writes observed state:

```text
status.availableReplicas = 2
```

ASCII:

```text
Human / CI
   |
   | writes spec
   v
API Server
   |
   v
etcd object
   |
   ^
   | writes status
Controller
```

This is not a minor detail.

This is the heart of Kubernetes reconciliation.

etcd remembers both what you want and what the system currently observes.

---

# 8. Object Versioning And resourceVersion

Every Kubernetes object has metadata.

One important field is:

```text
metadata.resourceVersion
```

Mental model:

```text
resourceVersion = etcd-backed version marker for this object state
```

When an object changes, its version changes.

This lets clients watch changes safely.

Example:

```text
Pod order-abc resourceVersion = 100
Pod updated to Running
Pod order-abc resourceVersion = 105
Pod updated to Ready
Pod order-abc resourceVersion = 109
```

Watch flow:

```text
Client: I have seen up to version 100.
API Server: Here are changes after version 100.
```

ASCII:

```text
Object timeline:

rv=100  Pod Pending
   |
   v
rv=105  Pod Running
   |
   v
rv=109  Pod Ready
```

This is why watches are efficient.

Controllers do not need to continuously list every object from scratch.

They can watch incremental changes.

---

# 9. Watch Mental Model

Controllers depend heavily on watch.

Instead of repeatedly asking:

```text
Do you have new Deployments?
Do you have new Deployments?
Do you have new Deployments?
```

They watch:

```text
Tell me when Deployment objects change.
```

Flow:

```text
Controller
   |
   | WATCH deployments
   v
API Server
   |
   | reads/streams from storage change history
   v
etcd-backed state
```

ASCII:

```text
+-------------+        watch stream        +-------------+
| Controller  | <------------------------- | API Server  |
+-------------+                            +------+------+ 
                                                   |
                                                   v
                                                +--+--+
                                                |etcd |
                                                +-----+
```

When you create a Deployment:

```text
1. API Server stores Deployment.
2. Watch stream sends event to Deployment controller.
3. Controller creates ReplicaSet.
```

Without watch, Kubernetes would be slower and wasteful.

With watch, the system reacts quickly to state changes.

---

# 10. List-Watch Pattern

Kubernetes controllers usually use a list-watch pattern.

Mental model:

```text
List current state once.
Then watch future changes.
```

Why not only watch?

Because a controller may start after objects already exist.

It needs the current baseline first.

Flow:

```text
1. LIST all Deployments
2. Remember latest resourceVersion
3. WATCH changes after that version
4. Reconcile when events arrive
```

ASCII:

```text
Controller starts
      |
      v
LIST current objects
      |
      v
Build local cache
      |
      v
WATCH future changes
      |
      v
Reconcile loop
```

This pattern reduces load.

Controllers do not hammer the API Server with full reads every second.

They keep a local cache and react to events.

Production note:

```text
If API Server watch performance is poor, controllers lag.
If controllers lag, actual state drifts from desired state longer.
```

---

# 11. Raft Mental Model

etcd is distributed.

A production Kubernetes control plane usually runs multiple etcd members.

etcd uses Raft consensus.

Do not memorize Raft first.

Understand the problem:

```text
If there are 3 etcd servers, how do they agree on the latest cluster state?
```

Raft answer:

```text
One leader accepts writes.
Followers replicate the log.
A write is committed when majority agrees.
```

ASCII:

```text
              write request
                   |
                   v
             +-----------+
             | Leader    |
             | etcd-1    |
             +-----+-----+
                   |
        replicate  |  replicate
        log entry  |  log entry
          +--------+--------+
          |                 |
          v                 v
     +---------+       +---------+
     | etcd-2  |       | etcd-3  |
     | follower|       | follower|
     +---------+       +---------+
```

Majority rule:

```text
3 members -> need 2 for quorum
5 members -> need 3 for quorum
```

This is why odd numbers are common.

---

# 12. Quorum Mental Model

Quorum means enough members agree to make progress safely.

For 3 etcd members:

```text
Healthy:
etcd-1 + etcd-2 + etcd-3 = quorum

One failure:
etcd-1 + etcd-2 = quorum still exists

Two failures:
etcd-1 alone = no quorum
```

ASCII:

```text
3-node etcd cluster

Case A: all healthy
[etcd-1] [etcd-2] [etcd-3]
   yes      yes      yes       -> quorum

Case B: one down
[etcd-1] [etcd-2] [  X   ]
   yes      yes                -> quorum

Case C: two down
[etcd-1] [  X   ] [  X   ]
   yes                         -> no quorum
```

If quorum is lost:

```text
Writes cannot safely proceed.
API Server write operations fail or hang.
Cluster control plane becomes degraded.
```

Existing workloads may continue running because containers already running on nodes do not need etcd every millisecond.

But operations like creating Pods, updating Deployments, or leader elections may fail.

---

# 13. Why Three etcd Nodes Is Common

A single etcd node is simple but risky.

```text
1 member -> tolerates 0 failures
```

Three members:

```text
3 members -> tolerates 1 failure
```

Five members:

```text
5 members -> tolerates 2 failures
```

But more members are not always better.

More members mean more replication and coordination.

ASCII:

```text
Write path with 3 members:
Leader -> follower A
       -> follower B
Need 2 acknowledgements total

Write path with 5 members:
Leader -> follower A
       -> follower B
       -> follower C
       -> follower D
Need 3 acknowledgements total
```

Production mindset:

```text
Use enough members for fault tolerance.
Do not add members randomly thinking it always improves speed.
```

For many clusters, 3 or 5 etcd members are common.

The exact choice depends on cluster criticality, latency, hardware, and operational maturity.

---

# 14. Write Path: kubectl apply

When you run:

```bash
kubectl apply -f deployment.yaml
```

The write path is:

```text
kubectl
  |
  v
API Server
  |
  | authenticate
  | authorize
  | admission
  | validate
  | default fields
  v
etcd leader
  |
  | replicate through Raft
  v
etcd quorum commit
  |
  v
API Server returns success
```

ASCII:

```text
kubectl apply
    |
    v
+------------+
| API Server |
+-----+------+
      |
      v
+------------+       replicate       +------------+
| etcd leader| --------------------> | follower   |
+-----+------+                       +------------+
      |
      | replicate
      v
+------------+
| follower   |
+------------+
```

Important:

```text
kubectl apply success means desired state was accepted and stored.
It does not mean all Pods are already running.
```

That is why rollout status exists:

```bash
kubectl rollout status deployment/order-service
```

Storage success and workload readiness are different stages.

---

# 15. Read Path: kubectl get

When you run:

```bash
kubectl get pods
```

Flow:

```text
kubectl
  |
  v
API Server
  |
  v
reads state from cache/storage path
  |
  v
returns Pod list
```

The API Server may serve reads using efficient caching mechanisms, but the truth is backed by etcd.

Mental model:

```text
kubectl get = ask API Server for current recorded cluster state
```

ASCII:

```text
Human
  |
  v
kubectl get pods
  |
  v
API Server
  |
  v
Recorded Pod objects
  |
  v
Table output
```

Example output:

```text
NAME                    READY   STATUS    RESTARTS
order-service-abc       1/1     Running   0
order-service-def       1/1     Running   0
```

This output is not produced by directly inspecting Linux processes on every node.

It is based on Kubernetes object status reported through the control plane.

---

# 16. Status Updates: Kubelet To API Server To etcd

Kubelet runs on each node.

It observes local reality:

```text
Container started
Readiness probe passed
Container crashed
Image pull failed
Node has memory pressure
```

Then it reports status to API Server.

API Server stores updated status in etcd.

Flow:

```text
Node reality
   |
   v
Kubelet
   |
   v
API Server
   |
   v
etcd object status
```

ASCII:

```text
+------------------+
| Worker Node      |
|                  |
| container alive? |
| readiness ok?    |
+--------+---------+
         |
         v
+------------------+
| Kubelet          |
+--------+---------+
         |
         v
+------------------+
| API Server       |
+--------+---------+
         |
         v
+------------------+
| etcd             |
| Pod.status       |
+------------------+
```

This is why `kubectl get pods` can show you status.

The status was reported from nodes into Kubernetes state.

---

# 17. Lease Objects And Heartbeats

Kubernetes uses Lease objects for efficient heartbeats and leader election.

Instead of constantly updating huge Node objects, lightweight Lease objects can represent liveness signals.

Mental model:

```text
Lease = small heartbeat record
```

Example:

```text
kubelet periodically updates node lease
control plane observes lease freshness
```

ASCII:

```text
Node kubelet
    |
    | renew lease
    v
API Server
    |
    v
etcd Lease object
```

Why this matters:

```text
Many nodes updating full Node objects frequently would create heavy etcd load.
Leases reduce write amplification.
```

Production connection:

```text
Large clusters produce many status and heartbeat updates.
Poor etcd performance can slow control plane responsiveness.
```

Kubernetes scalability is not only about Pods.

It is also about the rate of object writes, status updates, watches, and stored history.

---

# 18. Secrets In etcd

Kubernetes Secrets are stored as API objects.

That means their data is stored in etcd.

Important mental model:

```text
Secret object is not magically outside the cluster database.
It is stored in Kubernetes state.
```

By default, Secret values are base64 encoded in YAML, not truly encrypted just because they are base64.

Production requirement:

```text
Enable encryption at rest for Secrets.
Use RBAC carefully.
Avoid putting long-lived powerful credentials everywhere.
```

ASCII:

```text
Secret YAML
   |
   v
API Server
   |
   v
etcd storage
   |
   v
Readable by authorized API clients
```

Bad mindset:

```text
It is a Secret, so it is always safe.
```

Correct mindset:

```text
A Secret is a Kubernetes object with access controls and optional encryption-at-rest protection.
```

For Spring Boot apps:

```text
DB_PASSWORD from Secret
JWT_SIGNING_KEY from Secret
Kafka SASL password from Secret
```

Protect etcd backups because backups can contain Secrets too.

---

# 19. ConfigMaps In etcd

ConfigMaps are also stored in etcd.

They hold non-sensitive configuration:

```text
SPRING_PROFILES_ACTIVE
LOG_LEVEL
FEATURE_FLAG
EXTERNAL_API_URL
```

Spring Boot example:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: order-config
data:
  SPRING_PROFILES_ACTIVE: prod
  LOG_LEVEL: INFO
```

Deployment usage:

```yaml
envFrom:
  - configMapRef:
      name: order-config
```

Storage flow:

```text
ConfigMap YAML
   |
   v
API Server
   |
   v
etcd
   |
   v
Kubelet mounts/injects to Pod
   |
   v
Spring Boot reads env/config
```

ASCII:

```text
etcd record
   |
   v
Kubelet materializes config
   |
   v
Container environment / files
   |
   v
Spring Boot application
```

Production lesson:

```text
Changing ConfigMap updates stored desired config.
Your application may still need restart or reload logic to pick it up.
```

---

# 20. Storage Size And Object Explosion

etcd is not meant to be a general application database.

Do not store application business data in Kubernetes objects.

Bad idea:

```text
Store every customer order as a Custom Resource.
Store every payment event as a ConfigMap.
Store every request log as a Kubernetes Event.
```

Kubernetes API objects are for cluster control.

Application data belongs in application databases:

```text
PostgreSQL
MySQL
Cassandra
Redis
Elasticsearch
Object storage
Kafka
```

ASCII:

```text
Correct separation:

Kubernetes state            Application data
----------------            ----------------
Deployments                 Orders
Pods                        Payments
Services                    User sessions
Secrets                     Product catalog
ConfigMaps                  Audit logs
Nodes                       Search index
```

If you abuse etcd as app storage, you hurt control plane performance.

Mental model:

```text
etcd is the brain memory of the cluster.
Do not fill the brain with application logs.
```

---

# 21. Kubernetes Events And etcd Pressure

Kubernetes Events record things like:

```text
Scheduled Pod
Pulled image
Started container
Failed readiness probe
Back-off restarting failed container
FailedMount
FailedScheduling
```

Events are useful for debugging.

But high event volume can create pressure.

Example failure loop:

```text
10,000 Pods failing readiness every few seconds
      |
      v
Many Events generated
      |
      v
API Server and etcd load increases
```

ASCII:

```text
Bad rollout
   |
   v
many failing Pods
   |
   v
many status updates + events
   |
   v
control plane pressure
```

Production lesson:

```text
A broken workload can indirectly stress the control plane.
```

Debug commands:

```bash
kubectl get events --sort-by=.lastTimestamp
kubectl describe pod <pod-name>
```

Events are not a replacement for logs, metrics, or tracing.

Use them as Kubernetes-level clues.

---

# 22. Compaction Mental Model

etcd stores revisions of keys.

As objects change, revisions accumulate.

If history grows forever, storage and performance suffer.

Compaction removes old revisions that are no longer needed.

Mental model:

```text
Compaction = remove old history after safe point
```

ASCII:

```text
Before compaction:

rv=100 Pod Pending
rv=105 Pod Running
rv=109 Pod Ready
rv=115 Pod Ready + new condition
rv=120 Pod Ready + updated timestamp

After compaction:

old revisions removed up to compacted revision
recent state/history retained
```

Why it matters:

```text
Watch clients cannot ask for extremely old resourceVersions after compaction.
They may need to relist.
```

Controller behavior:

```text
Watch too old
   |
   v
API Server says resourceVersion unavailable
   |
   v
Client performs LIST again
   |
   v
Starts WATCH from fresh version
```

This is normal.

---

# 23. Defragmentation Mental Model

Compaction removes old revisions logically.

But disk files may not shrink immediately.

Defragmentation reclaims physical disk space.

Mental model:

```text
Compaction = mark old history unnecessary
Defrag     = reclaim disk layout space
```

Analogy:

```text
Compaction is deleting old papers from a folder.
Defrag is reorganizing the cabinet so the empty space is usable again.
```

ASCII:

```text
Before defrag:
[used][old removed][used][empty gaps][used]

After defrag:
[used][used][used][free free free]
```

Production note:

```text
Defrag should be planned carefully.
Do not randomly run heavy maintenance during peak control-plane load.
```

Symptoms that may point toward etcd storage maintenance needs:

```text
Large etcd database size
Slow API writes
High disk latency
Frequent control-plane warnings
```

Always follow your platform's operational guidance.

Managed Kubernetes providers often handle many of these details for you.

---

# 24. Snapshots Mental Model

etcd snapshot is a backup of cluster state at a point in time.

Mental model:

```text
Snapshot = save Kubernetes memory
```

Why snapshots matter:

```text
If etcd data is lost, cluster state is lost.
Backups are the recovery path.
```

ASCII:

```text
+------------------+
| etcd live state  |
+--------+---------+
         |
         | snapshot
         v
+------------------+
| backup file      |
| timestamped      |
+------------------+
```

What snapshot may contain:

```text
Deployments
Services
Secrets
ConfigMaps
Nodes
Custom Resources
RBAC objects
```

Because Secrets may be included, etcd backups must be protected.

Production mindset:

```text
A backup you never restore-tested is hope, not recovery planning.
```

Recovery requires careful cluster-specific procedure.

Do not treat etcd restore like restoring a random text file.

It affects the entire Kubernetes control plane.

---

# 25. etcd Failure Modes

Common etcd/control-plane storage problems:

```text
Quorum lost
Disk full
High disk latency
Network partition between members
Certificate expiration
Too many writes
Large object count
Huge watch fan-out
Corrupt member data
Slow leader election
```

ASCII:

```text
Failure sources:

Disk        -> fsync slow / full
Network     -> members cannot agree
CPU         -> leader overloaded
Objects     -> too many writes/watches
Security    -> TLS/cert issues
Operations  -> bad restore / bad member replace
```

Symptoms:

```text
kubectl commands slow
kubectl apply times out
controllers lag
Pods remain Pending longer
leader election issues
API Server errors
control-plane alerts firing
```

Important distinction:

```text
etcd failure mostly affects cluster control operations.
It does not instantly kill already-running containers.
```

But over time, inability to coordinate becomes dangerous.

---

# 26. Production Story: Disk Latency Breaks Control Plane

A team runs Kubernetes on VMs.

etcd is placed on slow shared storage.

Most days everything seems fine.

Then a large deployment happens:

```text
500 Pods created
many status updates
many watches
many events
```

Symptoms:

```text
kubectl apply is slow
controllers react late
scheduler binding delayed
API Server logs show storage latency
```

Root cause:

```text
etcd needs fast durable writes.
Slow fsync makes writes slow.
```

ASCII:

```text
API Server write
      |
      v
etcd leader
      |
      v
fsync to disk
      |
      v
slow disk = slow commit
      |
      v
slow Kubernetes writes
```

Lesson:

```text
Control plane storage is not a cheap afterthought.
```

For production, etcd needs reliable low-latency disk and stable network between members.

---

# 27. Production Story: No Quorum After Maintenance

Cluster has 3 etcd members:

```text
etcd-1
etcd-2
etcd-3
```

Operator drains/reboots one control-plane node.

Then another node has a network issue.

Result:

```text
Only one etcd member reachable
```

Quorum lost.

Symptoms:

```text
kubectl get may be inconsistent or fail
kubectl apply fails
controllers cannot update state
leader elections fail
```

ASCII:

```text
Before:
[etcd-1] [etcd-2] [etcd-3]
   up       up       up

Maintenance + failure:
[etcd-1] [ down ] [ network X ]
   up

Only 1 of 3 reachable -> no quorum
```

Lesson:

```text
Do not perform control-plane maintenance without understanding quorum.
```

Always know:

```text
How many members exist?
How many failures can be tolerated?
Which nodes host etcd?
Is backup healthy?
```

---

# 28. Production Story: Secrets Leaked Through Backup

A team enables Kubernetes Secrets.

They think:

```text
Secrets are safe because they are Kubernetes Secrets.
```

But etcd backups are copied to a shared bucket with weak access control.

Later, someone discovers the backups contain cluster state, including Secret data.

Root issue:

```text
Secret protection must include etcd storage and backup protection.
```

ASCII:

```text
Secret object
   |
   v
etcd
   |
   v
snapshot backup
   |
   v
object storage bucket
   |
   v
access risk
```

Production lesson:

```text
Protect live etcd.
Protect etcd snapshots.
Protect encryption keys.
Protect RBAC.
```

For Spring Boot systems, leaked Secrets may mean leaked:

```text
DB passwords
Kafka credentials
JWT signing keys
OAuth client secrets
API tokens
```

A Secret leak is often an application security incident, not only a Kubernetes issue.

---

# 29. Spring Boot Example: Config Flow Through etcd

Suppose your Spring Boot Order Service needs:

```text
SPRING_PROFILES_ACTIVE=prod
ORDER_TIMEOUT_MS=2000
DB_HOST=postgres.default.svc.cluster.local
DB_PASSWORD=secret value
```

Kubernetes objects:

```text
ConfigMap -> non-sensitive config
Secret    -> sensitive config
Deployment -> references both
```

Flow:

```text
ConfigMap/Secret YAML
      |
      v
API Server validates and stores in etcd
      |
      v
Kubelet sees Pod spec referencing them
      |
      v
Kubelet injects env vars/files
      |
      v
Spring Boot reads configuration
```

ASCII:

```text
+------------+     +-------------+
| ConfigMap  |     | Secret      |
+-----+------+     +------+------+ 
      |                   |
      +---------+---------+
                |
                v
             +------+
             |etcd  |
             +--+---+
                |
                v
             Kubelet
                |
                v
             Container env
                |
                v
             Spring Boot
```

Simple Spring Boot property usage:

```java
package com.example.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigDebugController {

    @Value("${ORDER_TIMEOUT_MS:1000}")
    private int orderTimeoutMs;

    @Value("${DB_HOST:missing}")
    private String dbHost;

    @GetMapping("/debug/config")
    public String config() {
        return "timeout=" + orderTimeoutMs + ", dbHost=" + dbHost;
    }
}
```

Do not expose real secrets in debug endpoints.

This example is only to understand configuration flow.

---

# 30. Java Mental Model: Optimistic Concurrency

Kubernetes often uses optimistic concurrency.

Meaning:

```text
Read object version.
Modify object.
Write back only if version still matches.
```

If someone else updated the object first, your update conflicts.

Simplified Java mental model:

```java
class K8sObject {
    String name;
    long resourceVersion;
    int replicas;
}

class ApiServerStorage {
    private K8sObject stored = new K8sObject();

    synchronized void update(K8sObject requested) {
        if (requested.resourceVersion != stored.resourceVersion) {
            throw new RuntimeException("Conflict: object changed");
        }
        requested.resourceVersion = stored.resourceVersion + 1;
        stored = requested;
    }
}
```

ASCII:

```text
Client A reads rv=10
Client B reads rv=10

Client A updates -> stored rv=11
Client B updates with rv=10 -> conflict
```

Why this matters:

```text
Multiple controllers and users may touch objects.
resourceVersion helps prevent blind overwrites.
```

When you see update conflicts in controllers, it often means:

```text
Your controller tried to update stale object state.
Relist/retry with fresh object.
```

---

# 31. Custom Resources And etcd

Custom Resource Definitions let you extend Kubernetes API.

Example:

```text
kind: DatabaseCluster
kind: KafkaTopic
kind: Certificate
kind: BackupJob
```

These custom objects are also stored in etcd.

ASCII:

```text
CRD installed
   |
   v
API Server understands new resource type
   |
   v
Custom Resource objects stored in etcd
   |
   v
Operator watches them
   |
   v
Operator reconciles external/system state
```

This is powerful, but dangerous if abused.

Bad idea:

```text
Create millions of tiny custom resources for high-volume business events.
```

Good idea:

```text
Use CRDs to represent cluster-level desired state.
```

Example:

```text
Certificate desired state
Database backup policy
Kafka topic desired config
Ingress certificate request
```

Mental model:

```text
CRDs extend Kubernetes control plane state.
They should not replace your application database.
```

---

# 32. etcd And Operators

Operators work by watching Kubernetes objects and reconciling reality.

They depend on API Server and etcd-backed state.

Example:

```text
You create DatabaseCluster object.
Operator watches it.
Operator creates StatefulSet, Services, Secrets, PVCs.
Operator updates status.
```

ASCII:

```text
DatabaseCluster custom resource
        |
        v
API Server -> etcd
        |
        v
Operator watch event
        |
        v
Create child Kubernetes objects
        |
        v
Update status in API object
```

If etcd is slow, operators may lag.

If the API Server is unavailable, operators cannot receive new desired state or update status.

Production mindset:

```text
Operators are only as healthy as the Kubernetes API control loop they depend on.
```

Do not run critical operators without monitoring API Server and etcd health.

---

# 33. What Happens If etcd Goes Down?

Scenario:

```text
etcd quorum unavailable
```

What may continue:

```text
Already running containers keep running
Node-local processes continue
Existing network dataplane may continue for some time
```

What breaks or degrades:

```text
kubectl apply
new Pod creation
Deployment updates
controller status updates
scheduler bindings
leader elections
new Service endpoint updates
some autoscaling operations
```

ASCII:

```text
Running workloads
     |
     | may continue
     v
Data plane survives temporarily

Control plane writes
     |
     | need etcd quorum
     v
fail / hang / degrade
```

Important:

```text
Kubernetes is not a single binary.
Data plane and control plane fail differently.
```

But if control plane remains down long enough, operational risk grows quickly.

---

# 34. Debugging etcd-Related Symptoms

You usually start with symptoms, not with etcd directly.

Symptoms:

```text
kubectl slow
apply timeout
Pods stuck Pending
controllers delayed
API Server errors
leader election flapping
control-plane alerts
```

Layered debugging mindset:

```text
1. Is API Server reachable?
2. Are control-plane nodes healthy?
3. Are etcd members healthy?
4. Is quorum available?
5. Is disk latency high?
6. Is disk full?
7. Are certificates valid?
8. Is object/event churn unusually high?
9. Are watches overloaded?
10. Are backups/snapshots healthy?
```

Useful commands vary by environment, but common Kubernetes-level clues include:

```bash
kubectl get --raw='/readyz?verbose'
kubectl get nodes
kubectl get events --sort-by=.lastTimestamp
kubectl -n kube-system get pods
kubectl -n kube-system logs <apiserver-pod>
```

On self-managed clusters, etcd-specific tooling may be available on control-plane nodes.

Managed clusters may hide direct etcd access and expose health through provider metrics.

---

# 35. API Server Readiness And etcd

API Server health endpoints can reveal storage readiness issues.

Conceptually:

```text
API Server ready only if critical dependencies are ready.
```

etcd is one of the most important dependencies.

ASCII:

```text
kubectl get --raw /readyz
        |
        v
API Server checks
  - post-start hooks
  - auth config
  - storage readiness
  - informer sync
        |
        v
ready / not ready
```

If storage is unhealthy:

```text
API Server may not be ready for normal traffic.
```

Production mindset:

```text
When kubectl is slow, do not immediately blame worker nodes.
First separate API/control-plane issue from workload issue.
```

Example separation:

```text
App latency high but kubectl fast -> likely app/data plane issue
kubectl slow and many controllers lag -> likely control plane issue
```

---

# 36. Metrics To Watch

For production, monitor etcd and API Server.

Important categories:

```text
etcd leader status
leader changes
proposal failures
commit/apply latency
fsync latency
database size
network latency between members
API Server request latency
API Server error rate
watch counts
object counts
```

Mental model:

```text
etcd health = storage consensus health
API Server health = Kubernetes API traffic health
Controller health = reconciliation health
```

ASCII:

```text
User/API traffic
      |
      v
API Server metrics
      |
      v
etcd metrics
      |
      v
Controller lag symptoms
```

Production warning signs:

```text
Frequent etcd leader changes
High disk fsync duration
Growing DB size without maintenance
API write latency spikes
Increasing watch errors
Slow controller queues
```

You do not need to memorize metric names first.

Understand what each metric family represents.

---

# 37. Spring Boot Production Connection

As a Java backend engineer, why should you care about etcd?

Because your Spring Boot services depend on Kubernetes control decisions:

```text
Deployment rollout
ConfigMap update
Secret injection
Service endpoint updates
Readiness state
HPA scaling
Pod rescheduling
Ingress changes
```

If etcd/control plane is unhealthy:

```text
Your app may still serve current traffic.
But rollout, scaling, recovery, and config updates may fail.
```

Example incident:

```text
Payment service has a bug.
Team tries rollback.
API Server writes are timing out because etcd is unhealthy.
Rollback is delayed.
Customer impact increases.
```

ASCII:

```text
Bad release
   |
   v
Need rollback
   |
   v
Deployment update requires API Server + etcd
   |
   v
etcd unhealthy = rollback delayed
```

Lesson:

```text
Application reliability depends on platform reliability.
```

For senior engineers, Kubernetes internals are not optional trivia.

They shape production recovery.

---

# 38. Full Dry Run: Create Deployment Stored In etcd

You apply:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 3
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
          image: order-service:1.0.0
```

Dry run:

```text
1. kubectl sends request to API Server.
2. API Server authenticates user.
3. API Server authorizes operation.
4. Admission controllers may mutate/validate object.
5. API Server defaults missing fields.
6. API Server validates final object schema.
7. API Server writes object to etcd.
8. etcd leader replicates write to followers.
9. Quorum commits the write.
10. API Server returns success.
11. Deployment controller receives watch event.
12. Deployment controller creates ReplicaSet.
13. ReplicaSet object is stored in etcd.
14. ReplicaSet controller creates Pod objects.
15. Pod objects are stored in etcd.
16. Scheduler watches unscheduled Pods.
17. Scheduler binds Pods to Nodes through API Server.
18. Kubelets watch assigned Pods.
19. Kubelets start containers.
20. Kubelets report Pod status.
21. Pod status updates are stored in etcd.
```

ASCII:

```text
Deployment YAML
      |
      v
API Server
      |
      v
etcd Deployment record
      |
      v
Controller watch event
      |
      v
ReplicaSet record
      |
      v
Pod records
      |
      v
Scheduler binding records
      |
      v
Kubelet status records
```

---

# 39. Full Dry Run: Pod Status Update

Initial etcd record:

```text
Pod order-abc
status.phase = Pending
```

Scheduler binds Pod:

```text
spec.nodeName = node-1
```

Kubelet on node-1 sees it:

```text
This Pod is assigned to me.
```

Kubelet actions:

```text
Pull image
Create container
Start container
Run probes
Report status
```

Status update path:

```text
Kubelet
  |
  v
API Server
  |
  v
etcd
```

Updated record:

```text
status.phase = Running
conditions:
  Ready = True
```

ASCII:

```text
Node local reality
   |
   v
Kubelet observes
   |
   v
API Server status update
   |
   v
etcd stores status
   |
   v
kubectl get pods shows Running
```

This explains why `kubectl get pods` reflects reported state, not direct SSH inspection.

---

# 40. Common Beginner Mistakes

```text
Mistake 1:
Thinking etcd runs Pods.
Correct:
etcd stores Pod objects. Kubelet runs containers.

Mistake 2:
Thinking kubectl talks directly to etcd.
Correct:
kubectl talks to API Server.

Mistake 3:
Thinking Secrets are safe because they are base64.
Correct:
Base64 is encoding, not encryption.

Mistake 4:
Using etcd as an app database through CRDs.
Correct:
etcd is for cluster state, not high-volume business data.

Mistake 5:
Ignoring quorum during maintenance.
Correct:
Know member count and failure tolerance.

Mistake 6:
Thinking running Pods mean control plane is healthy.
Correct:
Data plane may continue while control plane is broken.

Mistake 7:
Ignoring etcd backups.
Correct:
etcd backup is cluster memory backup.
```

---

# 41. Debugging Mindset: Control Plane Storage Chain

When Kubernetes feels stuck, follow the chain:

```text
kubectl request
   |
   v
API Server reachable?
   |
   v
API Server healthy?
   |
   v
etcd reachable?
   |
   v
etcd quorum healthy?
   |
   v
etcd disk/network healthy?
   |
   v
controllers receiving watch events?
   |
   v
scheduler/kubelet updating status?
```

ASCII:

```text
Problem: Deployment not progressing

Check:
YAML valid?
API Server accepted object?
Deployment exists?
ReplicaSet created?
Pods created?
Pods scheduled?
Kubelet reporting status?
Events show failure?
Control plane slow?
etcd healthy?
```

Do not jump randomly.

Kubernetes debugging is chain debugging.

etcd is near the root of the control-plane chain.

---

# 42. Interview Questions

## What is etcd in Kubernetes?

etcd is the distributed key-value store used as Kubernetes' durable cluster state database. It stores API objects such as Pods, Deployments, Services, ConfigMaps, Secrets, Nodes, and Custom Resources.

## Does etcd run Pods?

No. etcd stores Pod records. Kubelet on worker nodes starts containers using the container runtime.

## Who talks directly to etcd?

In normal Kubernetes operation, the API Server talks directly to etcd. Other components communicate with the API Server.

## Why does Kubernetes use etcd?

Kubernetes needs a reliable, consistent, durable store for desired state and observed state. etcd provides distributed consensus and a watchable key-value storage model.

## What is quorum?

Quorum is the majority of etcd members required to safely commit changes. In a 3-member cluster, 2 members are required. In a 5-member cluster, 3 members are required.

## What happens if etcd quorum is lost?

The control plane cannot safely commit writes. Operations such as creating Pods, updating Deployments, scheduling, and controller status updates may fail or stall. Already running workloads may continue temporarily.

## What is resourceVersion?

resourceVersion is a version marker associated with Kubernetes object state. It supports optimistic concurrency and watch behavior so clients can observe changes from a known version.

## What is list-watch?

List-watch is the controller pattern where a controller first lists current objects to build a baseline and then watches future changes from that point.

## Are Kubernetes Secrets encrypted in etcd?

They can be encrypted at rest if encryption is configured. Base64 encoding in YAML is not encryption. etcd backups must also be protected because they may contain Secret data.

## Why should etcd not be used as an application database?

etcd is optimized for Kubernetes control-plane state and consensus, not high-volume application business data. Using it as an app database can damage control-plane performance and reliability.

---

# 43. Cheat Sheet

```text
etcd                         = Kubernetes durable cluster state store
Key-value store              = key -> serialized object
API Server                   = only normal direct client of etcd
Pod record                   = stored in etcd
Running container            = runs on worker node
spec                         = desired state
status                       = observed state
resourceVersion              = object version marker
watch                        = stream object changes
list-watch                   = list baseline, then watch changes
Raft                         = consensus algorithm used by etcd
Leader                       = etcd member handling writes
Follower                     = etcd member replicating leader log
Quorum                       = majority required for safe commit
Snapshot                     = backup of etcd state
Compaction                   = remove old revisions
Defrag                       = reclaim physical disk space
Secret in etcd               = sensitive object requiring protection
CRD                          = custom API type stored in etcd
Operator                     = controller watching objects and reconciling
```

Core flow:

```text
kubectl apply
   |
   v
API Server
   |
   v
etcd leader
   |
   v
Raft replication
   |
   v
quorum commit
   |
   v
watch event
   |
   v
controller reconcile
```

Failure memory:

```text
No etcd quorum = no safe Kubernetes writes
Slow etcd      = slow control plane
Lost etcd data = lost cluster memory
Leaked backup  = possible Secret leak
```

---

# 44. One Picture To Remember

```text
                         KUBERNETES MEMORY MODEL

                              Developer
                                  |
                                  | kubectl apply
                                  v
                          +---------------+
                          | API Server    |
                          | front door    |
                          +-------+-------+
                                  |
                                  | validated state write
                                  v
                          +---------------+
                          | etcd Leader   |
                          | official log  |
                          +-------+-------+
                                  |
                  +---------------+---------------+
                  |                               |
                  v                               v
           +-------------+                 +-------------+
           | etcd member |                 | etcd member |
           | follower    |                 | follower    |
           +-------------+                 +-------------+

                         quorum commits state
                                  |
                                  v
                          +---------------+
                          | Watch events  |
                          +-------+-------+
                                  |
            +---------------------+----------------------+
            |                     |                      |
            v                     v                      v
      Controllers              Scheduler               Kubelets
            |                     |                      |
            v                     v                      v
      Create/update          Bind Pods              Report status
      desired objects         to Nodes               from Nodes

Rule:

etcd does not run your applications.
etcd remembers the cluster so Kubernetes can continuously reconcile reality.
```

---

# 45. Final Production Checklist

```text
[ ] I understand etcd stores Kubernetes API objects.
[ ] I understand etcd does not run Pods.
[ ] I understand kubectl talks to API Server, not directly to etcd.
[ ] I understand API Server is the normal direct client of etcd.
[ ] I understand spec is desired state and status is observed state.
[ ] I understand resourceVersion supports watch and concurrency.
[ ] I understand list-watch controller behavior.
[ ] I understand Raft leader/follower replication.
[ ] I understand quorum and why 3 members tolerate 1 failure.
[ ] I understand control plane can fail while existing Pods keep running.
[ ] I understand Secrets and backups must be protected.
[ ] I understand etcd should not be used for application business data.
[ ] I understand compaction and defrag at a high level.
[ ] I understand snapshots are cluster memory backups.
[ ] I know etcd health affects rollouts, scaling, scheduling, and recovery.
[ ] I can debug Kubernetes issues through API Server -> etcd -> controllers -> nodes.
```

---

# 46. Final Memory Hook

Do not memorize etcd as just another database.

Remember this:

```text
etcd = Kubernetes memory
API Server = controlled access to memory
Controllers = workers reacting to memory changes
Kubelets = node agents reporting reality back into memory
```

Final sentence:

```text
Kubernetes can only reconcile reality because etcd reliably remembers what reality should be.
```
