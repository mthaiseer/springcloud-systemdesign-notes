# 008_StatefulSet_DaemonSet_Job_CronJob.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why This Chapter Exists

By now you know the basic Kubernetes application shape:

```text
Deployment
    |
    v
ReplicaSet
    |
    v
Pods
```

A Deployment is excellent for stateless services such as:

```text
order-service
payment-api
user-service
notification-api
search-api
```

For these services, any replica can handle any request.

```text
Request 1 -> Pod A
Request 2 -> Pod C
Request 3 -> Pod B
```

If Pod B dies, Kubernetes creates another Pod. The new Pod does not need the identity of the old Pod.

But production systems are not only stateless APIs.

You also need:

```text
Databases
Kafka brokers
Redis replicas
Log agents
Node monitoring agents
Batch jobs
Cron jobs
One-time migration tasks
Scheduled cleanup tasks
```

A normal Deployment is not the best mental model for all of these.

Kubernetes gives different workload objects for different production promises:

```text
Deployment   -> stateless long-running app
StatefulSet  -> stateful long-running app with stable identity
DaemonSet    -> one pod per node agent
Job          -> run task until completion
CronJob      -> run task on schedule
```

This chapter teaches these objects through production need, not memorization.

One picture:

```text
Workload type is chosen by the promise you need.

Need many interchangeable API pods?       -> Deployment
Need stable identity and disk?            -> StatefulSet
Need one agent on every node?             -> DaemonSet
Need task finish successfully once?       -> Job
Need task repeat by time schedule?        -> CronJob
```

Final mental hook:

```text
Do not memorize Kubernetes workload names.
Ask: what promise must Kubernetes protect?
```

---

# 2. The Wrong Way To Learn These Objects

Bad learning style:

```text
StatefulSet has stable network identity.
DaemonSet runs one pod per node.
Job runs to completion.
CronJob creates Jobs periodically.
```

These statements are correct, but they are not enough.

They become interview memorization.

A better way:

```text
What production problem exists?
Why does Deployment fail for that problem?
What guarantee does the new workload object add?
How does Kubernetes reconcile it?
How do we debug it when it fails?
```

Example:

```text
Problem:
Kafka broker identity matters.

Wrong tool:
Deployment can replace pods with random names.

Better tool:
StatefulSet gives ordered names and stable storage.
```

Example:

```text
Problem:
Every node needs log collector.

Wrong tool:
Deployment with replicas = node count is fragile.

Better tool:
DaemonSet automatically places one pod per node.
```

Example:

```text
Problem:
Run database migration once and stop.

Wrong tool:
Deployment keeps restarting forever.

Better tool:
Job runs until successful completion.
```

Example:

```text
Problem:
Clean expired tokens every night.

Wrong tool:
Manual kubectl run every day.

Better tool:
CronJob creates Jobs by schedule.
```

This chapter is about choosing the right operating contract.

---

# 3. Workload Object Map

```text
+----------------+-----------------------------+---------------------------+
| Object         | Main Promise                | Typical Use               |
+----------------+-----------------------------+---------------------------+
| Deployment     | Keep N stateless replicas   | Spring Boot APIs          |
| StatefulSet    | Stable identity + storage   | DB, Kafka, Redis cluster  |
| DaemonSet      | One pod on each node        | Logs, metrics, agents     |
| Job            | Complete finite task        | Migration, batch import   |
| CronJob        | Run Job by time schedule    | Cleanup, reports, backup  |
+----------------+-----------------------------+---------------------------+
```

Think in terms of lifecycle:

```text
Long-running forever:
  Deployment
  StatefulSet
  DaemonSet

Finite completion:
  Job

Scheduled finite completion:
  CronJob
```

Think in terms of identity:

```text
Identity does not matter:
  Deployment
  Job
  CronJob-created Job

Identity matters:
  StatefulSet

Node identity matters:
  DaemonSet
```

Think in terms of placement:

```text
Any healthy node:
  Deployment
  StatefulSet with scheduling constraints
  Job

Every matching node:
  DaemonSet
```

One mental diagram:

```text
                    Kubernetes Workloads
                            |
        +-------------------+-------------------+
        |                                       |
 Long-running                              Run-to-completion
        |                                       |
 +------+-------+-------------+                 +---------+
 |              |             |                 |         |
Deployment   StatefulSet   DaemonSet           Job     CronJob
stateless    stateful      node agent          once    scheduled
```

---

# 4. Deployment Is The Baseline

Before learning special workloads, remember what Deployment gives.

A Deployment says:

```text
Keep N interchangeable Pods running.
Manage rollout and rollback safely.
```

Diagram:

```text
Deployment: order-service
replicas: 3

       +----------------+
       | Deployment     |
       +-------+--------+
               |
               v
       +----------------+
       | ReplicaSet     |
       +-------+--------+
               |
       +-------+--------+-------+
       |                |       |
       v                v       v
   Pod random-a     Pod random-b Pod random-c
```

The important word is:

```text
interchangeable
```

Any Pod can replace any other Pod.

That is good for stateless APIs.

```text
Pod random-a dies
       |
       v
New Pod random-x appears
       |
       v
Service routes traffic to it when Ready
```

No customer should care that Pod name changed.

No disk should depend on old Pod identity.

No cluster membership should require stable ordinal numbers.

If those assumptions are false, you need another workload object.

---

# 5. StatefulSet Mental Model

A StatefulSet is for Pods that need stable identity.

It gives each Pod:

```text
Stable name
Stable ordinal
Stable network identity
Stable persistent volume claim
Ordered startup/shutdown behavior
```

Example names:

```text
mysql-0
mysql-1
mysql-2
```

Not random names.

Diagram:

```text
StatefulSet: mysql
replicas: 3

+--------------------+
| StatefulSet mysql  |
+---------+----------+
          |
          v
+---------+----------+----------+
|                    |          |
v                    v          v
mysql-0              mysql-1    mysql-2
PVC mysql-data-0     PVC data-1 PVC data-2
```

The key promise:

```text
If mysql-1 dies, replacement is still mysql-1.
It gets the same identity and same disk.
```

This matters for systems where each member has a role.

Examples:

```text
Kafka broker-0 owns partitions
MongoDB member-1 has replica identity
ZooKeeper server-2 has quorum identity
Redis primary/replica nodes need stable discovery
```

Mental model:

```text
Deployment pods are like temporary taxi drivers.
StatefulSet pods are like named employees with assigned desks and lockers.
```

If a taxi driver leaves, any other driver can take the next ride.

If accountant Alice leaves for lunch, you do not give her files to random stranger Bob without process.

StatefulSet protects identity.

---

# 6. StatefulSet vs Deployment

```text
+----------------------+------------------------+-------------------------+
| Feature              | Deployment             | StatefulSet             |
+----------------------+------------------------+-------------------------+
| Pod names            | Random suffix          | Stable ordinal          |
| Pod identity         | Interchangeable        | Sticky                  |
| Storage              | Usually shared/none    | Per-pod PVC             |
| Startup order        | Not guaranteed         | Ordered by default      |
| Shutdown order       | Not identity-focused   | Reverse ordered         |
| Use case             | Stateless APIs         | Databases, brokers      |
+----------------------+------------------------+-------------------------+
```

Picture:

```text
Deployment

order-9xk2a
order-p7d1q
order-m8z3r

Names are disposable.

StatefulSet

redis-0
redis-1
redis-2

Names are meaningful.
```

If a Deployment Pod dies:

```text
old: order-9xk2a
new: order-abcd1
```

If a StatefulSet Pod dies:

```text
old: redis-1
new: redis-1
```

The Pod object may be recreated, but identity is restored.

Important nuance:

```text
StatefulSet does not make your database correct automatically.
It provides Kubernetes identity/storage primitives.
The database still needs correct replication, backup, quorum, and recovery configuration.
```

Bad mental model:

```text
StatefulSet = database solved
```

Correct mental model:

```text
StatefulSet = stable operating foundation for stateful software
```

---

# 7. StatefulSet Headless Service

StatefulSet commonly uses a headless Service.

Normal Service gives one stable virtual IP:

```text
client -> service IP -> any ready pod
```

Headless Service gives DNS records directly for Pods.

```yaml
clusterIP: None
```

Then Pods get stable DNS names like:

```text
mysql-0.mysql.default.svc.cluster.local
mysql-1.mysql.default.svc.cluster.local
mysql-2.mysql.default.svc.cluster.local
```

Diagram:

```text
Headless Service: mysql
clusterIP: None

DNS records:

mysql-0.mysql.default.svc.cluster.local -> mysql-0 Pod IP
mysql-1.mysql.default.svc.cluster.local -> mysql-1 Pod IP
mysql-2.mysql.default.svc.cluster.local -> mysql-2 Pod IP
```

Why is this useful?

Clustered systems need to talk to specific members.

```text
Kafka broker-0 must be reachable as broker-0
ZooKeeper server-1 must be reachable as server-1
Database replica mysql-2 must have stable address
```

A normal Service hides individual Pod identity.

A headless Service exposes stable Pod identity.

Mental model:

```text
Normal Service = reception desk routes to anyone available
Headless Service = directory with each employee's direct extension
```

---

# 8. StatefulSet YAML Example

Simple StatefulSet for a Redis-like service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: redis
spec:
  clusterIP: None
  selector:
    app: redis
  ports:
    - name: redis
      port: 6379
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: redis
spec:
  serviceName: redis
  replicas: 3
  selector:
    matchLabels:
      app: redis
  template:
    metadata:
      labels:
        app: redis
    spec:
      containers:
        - name: redis
          image: redis:7
          ports:
            - containerPort: 6379
          volumeMounts:
            - name: data
              mountPath: /data
  volumeClaimTemplates:
    - metadata:
        name: data
      spec:
        accessModes: ["ReadWriteOnce"]
        resources:
          requests:
            storage: 5Gi
```

What this says in simple words:

```text
Create 3 Redis pods.
Name them redis-0, redis-1, redis-2.
Give each pod its own disk.
Expose stable DNS names through the redis headless service.
```

PVC result:

```text
data-redis-0
data-redis-1
data-redis-2
```

Pod-to-disk mapping:

```text
redis-0 -> data-redis-0
redis-1 -> data-redis-1
redis-2 -> data-redis-2
```

This is the heart of StatefulSet.

---

# 9. StatefulSet Dry Run

You apply StatefulSet YAML.

Internal flow:

```text
1. kubectl sends Service + StatefulSet to API Server.

2. API Server validates objects and stores them in etcd.

3. StatefulSet controller sees desired replicas = 3.

4. It creates Pod redis-0 first.

5. It creates PVC data-redis-0.

6. Scheduler assigns redis-0 to a node.

7. Kubelet mounts volume and starts Redis container.

8. redis-0 becomes Running and Ready.

9. Controller creates redis-1.

10. PVC data-redis-1 is created.

11. redis-1 starts and becomes Ready.

12. Controller creates redis-2.

13. DNS records become available through headless Service.
```

Diagram:

```text
StatefulSet Controller
        |
        v
create redis-0 + PVC
        |
        v
wait until ready
        |
        v
create redis-1 + PVC
        |
        v
wait until ready
        |
        v
create redis-2 + PVC
```

Ordered startup is useful when cluster members depend on earlier members.

But do not blindly rely on it for application correctness.

Your database/broker software must still handle membership safely.

---

# 10. StatefulSet Production Story: Wrong Storage Assumption

A team deploys PostgreSQL using StatefulSet.

They think:

```text
StatefulSet means my database is highly available.
```

They create:

```text
postgres-0
postgres-1
postgres-2
```

But they do not configure replication correctly.

Failure:

```text
postgres-0 disk corruption
postgres-1 not promoted
application keeps writing to bad primary
backup not tested
```

Kubernetes did its job:

```text
It kept identities and volumes stable.
```

But PostgreSQL HA was not solved.

Correct thinking:

```text
StatefulSet gives stable pod identity.
Database HA requires database-level replication, leader election, backup, restore, and failover logic.
```

Production rule:

```text
Do not run critical databases on StatefulSet unless you understand the database operator/HA model.
```

Better approach in many teams:

```text
Use managed database.
Or use a battle-tested Kubernetes operator.
```

Mental model:

```text
Kubernetes can preserve the house address.
It cannot automatically repair the accounting books inside the house.
```

---

# 11. StatefulSet Debugging Mindset

When StatefulSet fails, debug identity and storage first.

Command chain:

```bash
kubectl get statefulset
kubectl describe statefulset redis

kubectl get pods -l app=redis -o wide
kubectl describe pod redis-0
kubectl logs redis-0

kubectl get pvc
kubectl describe pvc data-redis-0

kubectl get svc redis
kubectl get endpoints redis
```

Common problems:

```text
1. PVC Pending
   StorageClass missing or no volume available.

2. Pod Pending
   Volume cannot attach to selected node.

3. Pod CrashLoopBackOff
   App cannot read/write mounted data or config is wrong.

4. DNS not working
   Headless Service selector does not match Pod labels.

5. Cluster members cannot find each other
   Wrong serviceName or wrong DNS names in app config.
```

Debug map:

```text
StatefulSet exists?
       |
       v
Pods created in order?
       |
       v
PVC bound?
       |
       v
Volume mounted?
       |
       v
Container started?
       |
       v
Headless DNS resolves?
       |
       v
Application cluster healthy?
```

Never jump directly to logs only.

For StatefulSet, storage and DNS are first-class suspects.

---

# 12. DaemonSet Mental Model

A DaemonSet says:

```text
Run one copy of this Pod on every matching Node.
```

It is for node-level agents.

Examples:

```text
Log collector
Metrics collector
Security scanner
Network plugin
Storage agent
Node exporter
Fluent Bit
Prometheus node agent
Datadog agent
```

Diagram:

```text
Cluster

Node-1                  Node-2                  Node-3
+---------+             +---------+             +---------+
| app pod |             | app pod |             | app pod |
| log pod |             | log pod |             | log pod |
+---------+             +---------+             +---------+

DaemonSet ensures log pod exists on every node.
```

Why not Deployment with replicas = number of nodes?

Because nodes change.

```text
Today: 3 nodes
Tomorrow: autoscaler adds 7 nodes
Next week: 2 nodes removed
```

Deployment does not naturally mean one per node.

DaemonSet does.

Mental model:

```text
Deployment = hire N workers total
DaemonSet  = put one guard in every building
```

When a new node joins, DaemonSet automatically schedules the agent there.

When a node leaves, that node's daemon pod disappears with it.

---

# 13. DaemonSet YAML Example

Example: simple log agent placeholder.

```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: node-log-agent
  namespace: observability
spec:
  selector:
    matchLabels:
      app: node-log-agent
  template:
    metadata:
      labels:
        app: node-log-agent
    spec:
      containers:
        - name: agent
          image: busybox:1.36
          command: ["sh", "-c"]
          args:
            - while true; do echo collecting-node-logs; sleep 30; done
          resources:
            requests:
              cpu: "50m"
              memory: "64Mi"
            limits:
              cpu: "200m"
              memory: "256Mi"
```

What this means:

```text
For every matching node in the cluster,
make sure one node-log-agent pod exists.
```

If cluster has 4 nodes:

```text
4 daemon pods
```

If autoscaler adds 2 nodes:

```text
6 daemon pods
```

If 1 node is removed:

```text
5 daemon pods
```

The desired number is not written as replicas.

The desired number is derived from nodes.

```text
DaemonSet desired pods = eligible nodes
```

---

# 14. DaemonSet With Node Selector

Sometimes you do not want every node.

Example:

```text
Run GPU monitoring agent only on GPU nodes.
```

Node label:

```bash
kubectl label node node-1 hardware=gpu
```

DaemonSet selection:

```yaml
spec:
  template:
    spec:
      nodeSelector:
        hardware: gpu
      containers:
        - name: gpu-agent
          image: example/gpu-agent:1.0.0
```

Result:

```text
Node-1 [hardware=gpu]      -> gpu-agent runs
Node-2 [hardware=standard] -> no gpu-agent
Node-3 [hardware=gpu]      -> gpu-agent runs
```

Diagram:

```text
DaemonSet wants nodes matching hardware=gpu

node-1  labels: hardware=gpu       YES
node-2  labels: hardware=standard  NO
node-3  labels: hardware=gpu       YES
```

This is important in production because agents may be expensive.

Examples:

```text
GPU agent only on GPU nodes
SSD health agent only on storage nodes
Ingress controller only on edge nodes
Compliance scanner only on regulated workload nodes
```

Mental model:

```text
DaemonSet = one per eligible node, not always one per all nodes.
```

---

# 15. DaemonSet Dry Run

You apply a DaemonSet.

Flow:

```text
1. kubectl sends DaemonSet to API Server.

2. API Server stores desired state in etcd.

3. DaemonSet controller lists eligible Nodes.

4. For each eligible Node, it checks whether matching Pod exists.

5. If missing, it creates a Pod targeted to that Node.

6. Scheduler/kubelet path starts the Pod.

7. New Node joins cluster.

8. DaemonSet controller notices new eligible Node.

9. It creates one daemon Pod for that Node.
```

ASCII:

```text
Nodes: node-1 node-2 node-3
        |      |      |
        v      v      v
      pod    pod    pod

New node-4 joins
        |
        v
DaemonSet creates pod on node-4
```

Unlike Deployment, DaemonSet count follows infrastructure shape.

```text
Deployment desired count comes from spec.replicas.
DaemonSet desired count comes from Node list + constraints.
```

---

# 16. DaemonSet Production Story: Missing Logs From New Nodes

A team uses a log collector as Deployment with replicas = 3.

Cluster starts with 3 nodes.

```text
node-1 -> log collector
node-2 -> log collector
node-3 -> log collector
```

Everything looks fine.

Then autoscaler adds 5 nodes during traffic spike.

```text
node-4 -> no log collector
node-5 -> no log collector
node-6 -> no log collector
node-7 -> no log collector
node-8 -> no log collector
```

Production incident happens on node-7.

But logs are missing.

Root cause:

```text
They used Deployment for a node-level responsibility.
```

Correct fix:

```text
Use DaemonSet for node log collection.
```

After fix:

```text
Every new node automatically gets collector.
```

Lesson:

```text
If the workload must follow nodes, use DaemonSet.
If the workload must follow app replica count, use Deployment.
```

---

# 17. DaemonSet Debugging Mindset

Commands:

```bash
kubectl get daemonset -A
kubectl describe daemonset node-log-agent -n observability

kubectl get pods -n observability -l app=node-log-agent -o wide
kubectl describe pod <daemon-pod> -n observability
kubectl logs <daemon-pod> -n observability

kubectl get nodes --show-labels
```

Common problems:

```text
1. Desired count lower than expected
   Node selector, affinity, taints, or tolerations exclude nodes.

2. Pod not running on control-plane node
   Control-plane taint not tolerated.

3. Agent CrashLoopBackOff
   Missing hostPath, permissions, config, or resource limit too low.

4. Logs missing from some nodes
   DaemonSet not scheduled on those nodes.

5. New nodes missing agent
   Node labels do not match DaemonSet selector.
```

Debug map:

```text
How many nodes exist?
       |
       v
How many are eligible?
       |
       v
Does DaemonSet desired count match eligible nodes?
       |
       v
Which nodes have pods?
       |
       v
Why are missing nodes excluded?
```

Useful command:

```bash
kubectl get pods -o wide -n observability
```

This shows which node each daemon pod runs on.

---

# 18. Job Mental Model

A Job says:

```text
Run this task until it completes successfully.
```

It is not for forever-running services.

It is for finite work.

Examples:

```text
Database migration
Data import
Report generation
One-time cache warmup
Batch payment reconciliation
Image processing batch
Backfill Elasticsearch index
```

Diagram:

```text
Job
 |
 v
Pod starts
 |
 v
Task runs
 |
 v
Exit code 0?
 |
 +-- yes -> Job Complete
 |
 +-- no  -> retry according to backoffLimit
```

Deployment wants Pod alive forever.

Job wants task completed.

This difference is huge.

Wrong:

```text
Run DB migration in Deployment init forever by accident.
```

Correct:

```text
Run DB migration as Job.
Stop after success.
```

Mental model:

```text
Deployment = restaurant open all day
Job        = deliver one package successfully
```

Once package is delivered, the worker can stop.

---

# 19. Job YAML Example

Example: Spring Boot migration runner.

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: order-db-migration
spec:
  backoffLimit: 3
  template:
    spec:
      restartPolicy: Never
      containers:
        - name: migration
          image: order-service:1.0.0
          command: ["java", "-jar", "/app/order-service.jar"]
          args: ["--spring.profiles.active=migration"]
          env:
            - name: DB_HOST
              value: postgres.default.svc.cluster.local
```

Important fields:

```text
restartPolicy: Never
  Failed pod exits. Job controller may create a new pod.

backoffLimit: 3
  Retry failed job up to 3 times before marking Job failed.
```

Simple lifecycle:

```text
Job created
   |
   v
Pod created
   |
   v
Migration runs
   |
   v
Exit code 0
   |
   v
Job Complete
```

If failure:

```text
Pod exits non-zero
   |
   v
Job retries
   |
   v
After backoffLimit exceeded -> Job Failed
```

---

# 20. Java Spring Boot Job Example

A Spring Boot app can behave as a batch job using `CommandLineRunner`.

```java
package com.example.jobs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MigrationJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(MigrationJobApplication.class, args);
    }

    @Bean
    CommandLineRunner runMigration() {
        return args -> {
            System.out.println("Starting migration job...");

            // 1. Connect to database
            // 2. Check current schema version
            // 3. Apply safe migration
            // 4. Write migration audit row

            System.out.println("Migration completed successfully.");
        };
    }
}
```

Important production rule:

```text
A Kubernetes Job must exit when work is done.
```

If your Spring Boot app starts a web server and keeps running forever, the Job never completes.

For batch mode, configure:

```properties
spring.main.web-application-type=none
```

Or pass profile:

```text
--spring.main.web-application-type=none
```

Mental model:

```text
Job success = process exits with code 0
Job failure = process exits with non-zero code
```

Kubernetes does not understand your business result unless your process exit code tells it.

---

# 21. Job Parallelism And Completions

Jobs can run more than one Pod.

Two important fields:

```text
completions  = how many successful completions are needed
parallelism  = how many pods may run at the same time
```

Example:

```yaml
spec:
  completions: 10
  parallelism: 2
```

Meaning:

```text
Need 10 successful task completions.
Run up to 2 Pods at a time.
```

Diagram:

```text
Total needed: 10 completions
Parallelism:  2

Round 1: Pod A + Pod B -> 2 complete
Round 2: Pod C + Pod D -> 4 complete
Round 3: Pod E + Pod F -> 6 complete
Round 4: Pod G + Pod H -> 8 complete
Round 5: Pod I + Pod J -> 10 complete
```

Real use:

```text
Process 10 data partitions
Run 2 workers at a time to avoid DB overload
```

Be careful:

```text
Parallel jobs require idempotent work partitioning.
```

Bad pattern:

```text
10 pods all process same rows.
```

Good pattern:

```text
Each pod claims a unique partition or task row atomically.
```

---

# 22. Job Production Story: Duplicate Payment Reconciliation

A payment reconciliation Job is configured with:

```yaml
parallelism: 5
completions: 5
```

Each Pod runs:

```text
select all pending payments
reconcile each payment
mark as reconciled
```

Incident:

```text
Same payment reconciled multiple times.
External provider receives duplicate calls.
Finance report becomes inconsistent.
```

Root cause:

```text
Parallel Job without idempotent task claiming.
```

Correct design:

```text
Each worker atomically claims rows:

UPDATE payment_task
SET status = 'PROCESSING', worker_id = ?
WHERE id IN (...)
AND status = 'PENDING'
RETURNING id;
```

Or partition by shard:

```text
worker 0 -> payments where hash(id) % 5 = 0
worker 1 -> payments where hash(id) % 5 = 1
...
```

Production lesson:

```text
Kubernetes can run parallel workers.
It cannot automatically make business operations idempotent.
```

For payments, always use:

```text
idempotency key
transaction boundary
retry-safe external calls
outbox/audit table
DLT for failures
```

---

# 23. Job Debugging Mindset

Commands:

```bash
kubectl get jobs
kubectl describe job order-db-migration

kubectl get pods --selector=job-name=order-db-migration
kubectl logs job/order-db-migration
kubectl logs <job-pod-name>
```

Common problems:

```text
1. Job never completes
   Process does not exit. Spring Boot web server still running.

2. Job fails repeatedly
   Container exits non-zero. Check logs and backoffLimit.

3. Job stuck Pending
   Resource request too high, image pull issue, node constraints.

4. Job creates duplicate effects
   Task is not idempotent or parallel partitioning is wrong.

5. Migration job runs against wrong database
   Wrong environment variables, namespace, or secret.
```

Debug map:

```text
Job exists?
   |
   v
Pod created?
   |
   v
Pod scheduled?
   |
   v
Container started?
   |
   v
Exit code?
   |
   +-- 0      -> Complete
   +-- non-0  -> Failed/retry
   +-- none   -> Still running
```

Remember:

```text
For Jobs, logs + exit code are the main truth.
```

---

# 24. CronJob Mental Model

A CronJob says:

```text
Create Jobs according to a schedule.
```

It is Job plus time.

Examples:

```text
Delete expired sessions every night
Generate daily settlement report
Refresh recommendation cache every hour
Archive old logs weekly
Run backup every day at 02:00
Send monthly invoices
```

Diagram:

```text
CronJob schedule
      |
      v
At matching time
      |
      v
Create Job
      |
      v
Job creates Pod
      |
      v
Task runs to completion
```

CronJob does not run the task directly.

It creates Jobs.

```text
CronJob
  |
  +--> Job 1 -> Pod
  +--> Job 2 -> Pod
  +--> Job 3 -> Pod
```

Mental model:

```text
CronJob = alarm clock
Job     = worker who does the task
Pod     = actual process running the task
```

---

# 25. CronJob YAML Example

Example: nightly token cleanup.

```yaml
apiVersion: batch/v1
kind: CronJob
metadata:
  name: cleanup-expired-tokens
spec:
  schedule: "0 2 * * *"
  concurrencyPolicy: Forbid
  successfulJobsHistoryLimit: 3
  failedJobsHistoryLimit: 5
  jobTemplate:
    spec:
      backoffLimit: 2
      template:
        spec:
          restartPolicy: Never
          containers:
            - name: cleanup
              image: auth-service:1.0.0
              command: ["java", "-jar", "/app/auth-service.jar"]
              args:
                - "--spring.profiles.active=cleanup"
                - "--spring.main.web-application-type=none"
```

Schedule:

```text
0 2 * * *
```

Meaning:

```text
At minute 0, hour 2, every day.
```

Important field:

```text
concurrencyPolicy: Forbid
```

This means:

```text
If previous cleanup is still running,
do not start a new one.
```

This is important for jobs like:

```text
payment settlement
database cleanup
report generation
backup
```

You do not want overlapping executions corrupting state.

---

# 26. Cron Schedule Mental Model

Cron format:

```text
* * * * *
| | | | |
| | | | +--- day of week
| | | +----- month
| | +------- day of month
| +--------- hour
+----------- minute
```

Examples:

```text
*/5 * * * *     every 5 minutes
0 * * * *       every hour
0 2 * * *       every day at 02:00
30 1 * * 1      every Monday at 01:30
0 0 1 * *       first day of every month at midnight
```

ASCII timeline:

```text
Time ------------------------------>

02:00        02:00        02:00
 |            |            |
 v            v            v
Job-1        Job-2        Job-3
```

Production warning:

```text
Know the timezone behavior of your cluster/controller configuration.
Do not assume local business timezone without checking.
```

For financial systems, always document:

```text
Business timezone
Cron timezone
Daylight saving behavior
Expected run window
Retry policy
Overlap policy
```

Cron bugs are silent until reports, invoices, or cleanups are wrong.

---

# 27. CronJob Concurrency Policies

CronJob has three important concurrency choices:

```text
Allow
Forbid
Replace
```

Meaning:

```text
Allow:
  New Job starts even if old Job still running.

Forbid:
  Skip new Job if old Job still running.

Replace:
  Stop old Job and start new Job.
```

Diagram:

```text
Schedule every 10 minutes
Job takes 25 minutes

Allow:
00:00 Job A running
00:10 Job B also running
00:20 Job C also running

Forbid:
00:00 Job A running
00:10 skipped
00:20 skipped
00:25 A done
00:30 Job B starts

Replace:
00:00 Job A running
00:10 A stopped, B starts
00:20 B stopped, C starts
```

Choosing policy:

```text
Allow    -> safe independent tasks
Forbid   -> cleanup, reports, settlement, backup
Replace  -> only latest result matters
```

For most backend production jobs, start with:

```text
Forbid
```

Then relax only if you prove overlap is safe.

---

# 28. CronJob Production Story: Overlapping Cleanup

A cleanup task deletes expired records.

Schedule:

```text
Every 5 minutes
```

But during traffic spike, cleanup takes 20 minutes.

concurrencyPolicy:

```text
Allow
```

Result:

```text
Cleanup A running
Cleanup B starts
Cleanup C starts
Cleanup D starts
```

Database symptoms:

```text
High locks
Slow queries
Deadlocks
Connection pool exhaustion
App p99 latency spike
```

Root cause:

```text
CronJob overlap created many cleanup workers.
```

Fix:

```yaml
concurrencyPolicy: Forbid
```

Also improve cleanup query:

```text
Delete in small batches
Use indexed expiration column
Sleep between batches if needed
Avoid long transactions
```

Mental model:

```text
CronJob schedule can amplify load.
A harmless task becomes dangerous when overlapping.
```

Production rule:

```text
Scheduled jobs need the same SRE thinking as APIs.
They consume CPU, DB connections, locks, network, and storage IO.
```

---

# 29. CronJob Debugging Mindset

Commands:

```bash
kubectl get cronjob
kubectl describe cronjob cleanup-expired-tokens

kubectl get jobs
kubectl get pods --selector=job-name=<job-name>
kubectl logs job/<job-name>
```

Common problems:

```text
1. CronJob does not run
   Schedule wrong, suspended, controller issue, timezone misunderstanding.

2. Job runs too often
   Cron expression wrong.

3. Jobs overlap
   concurrencyPolicy Allow and task duration longer than interval.

4. Job history too large
   History limits not configured.

5. Cleanup/report wrong
   App logic, timezone, idempotency, or DB query issue.
```

Debug map:

```text
CronJob exists?
   |
   v
Schedule correct?
   |
   v
Suspended false?
   |
   v
Job created at expected time?
   |
   v
Job pod completed?
   |
   v
Business output correct?
```

Important:

```text
A CronJob can be technically successful but business-wrong.
```

Example:

```text
Job exits 0 but generated report for wrong timezone.
```

Always monitor business result too.

---

# 30. Spring Boot Pattern For Kubernetes Jobs

For Spring Boot APIs, the process usually runs forever.

For Kubernetes Jobs/CronJobs, design a separate execution path.

Pattern:

```text
Same codebase
Different profile
No web server
CommandLineRunner executes task
Exit code tells Kubernetes result
```

Example:

```java
package com.example.cleanup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CleanupJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(CleanupJobApplication.class, args);
    }

    @Bean
    CommandLineRunner cleanup(ExpiredTokenRepository repository) {
        return args -> {
            int deleted = repository.deleteExpiredTokensInBatches(500);
            System.out.println("Deleted expired tokens: " + deleted);
        };
    }
}
```

Repository idea:

```java
package com.example.cleanup;

import org.springframework.stereotype.Repository;

@Repository
public class ExpiredTokenRepository {

    public int deleteExpiredTokensInBatches(int batchSize) {
        // In real production code:
        // 1. Use indexed expires_at column
        // 2. Delete small batches
        // 3. Commit frequently
        // 4. Emit metrics
        // 5. Avoid huge table locks
        return 0;
    }
}
```

Application properties:

```properties
spring.main.web-application-type=none
management.metrics.tags.job=cleanup-expired-tokens
```

Production mindset:

```text
Batch code must be observable.
Log start, end, duration, item count, failure reason.
```

---

# 31. Choosing The Right Workload

Use this decision tree:

```text
Is the process long-running?
       |
       +-- yes --> Is every replica interchangeable?
       |              |
       |              +-- yes --> Deployment
       |              |
       |              +-- no  --> Does stable identity/storage matter?
       |                         |
       |                         +-- yes --> StatefulSet
       |
       +-- no --> Is it scheduled repeatedly?
                      |
                      +-- yes --> CronJob
                      |
                      +-- no  --> Job

Does it need one pod per node?
       |
       +-- yes --> DaemonSet
```

Another view:

```text
Spring Boot REST API              -> Deployment
Redis/Kafka/ZooKeeper             -> StatefulSet
Fluent Bit/Node Exporter          -> DaemonSet
DB migration                      -> Job
Nightly cleanup                   -> CronJob
Monthly invoice generation        -> CronJob
One-time Elasticsearch backfill   -> Job
```

Memory hook:

```text
Deployment  = many replaceable workers
StatefulSet = named workers with personal desks
DaemonSet   = one guard per building
Job         = finish one assignment
CronJob     = assignment triggered by alarm clock
```

---

# 32. Production Comparison Table

```text
+-------------+----------------+----------------+----------------+----------------+
| Workload    | Runs Forever?  | Stable Name?   | Per-Node?      | Completion?    |
+-------------+----------------+----------------+----------------+----------------+
| Deployment  | Yes            | No             | No             | No             |
| StatefulSet | Yes            | Yes            | No             | No             |
| DaemonSet   | Yes            | Node-bound     | Yes            | No             |
| Job         | No             | No             | No             | Yes            |
| CronJob     | No, scheduled  | No             | No             | Yes, repeated  |
+-------------+----------------+----------------+----------------+----------------+
```

Operational risk table:

```text
+-------------+-----------------------------+------------------------------+
| Workload    | Main Risk                   | Debug First                  |
+-------------+-----------------------------+------------------------------+
| StatefulSet | Storage/DNS/cluster config  | PVC, headless service, logs  |
| DaemonSet   | Missing nodes/permissions   | node labels, taints, pods    |
| Job         | Non-zero exit / no exit     | logs, exit code, backoff     |
| CronJob     | schedule/overlap/timezone   | schedule, jobs, concurrency  |
+-------------+-----------------------------+------------------------------+
```

This table is interview gold because it shows practical understanding.

---

# 33. Full Dry Run: Nightly Cleanup CronJob

Scenario:

```text
Auth service stores refresh tokens.
Expired tokens should be deleted every night at 02:00.
```

You create CronJob:

```text
cleanup-expired-tokens
schedule: 0 2 * * *
concurrencyPolicy: Forbid
```

Dry run:

```text
1. CronJob object stored in etcd.

2. CronJob controller checks schedules continuously.

3. At 02:00, controller creates Job cleanup-expired-tokens-xxxxx.

4. Job controller creates Pod.

5. Scheduler assigns Pod to Node.

6. Kubelet pulls auth-service image.

7. Java process starts in cleanup profile.

8. Spring Boot does not start web server.

9. CommandLineRunner deletes expired tokens in batches.

10. App logs deleted count and duration.

11. Java process exits code 0.

12. Job status becomes Complete.

13. CronJob keeps limited successful job history.
```

Diagram:

```text
CronJob
  |
  | 02:00
  v
Job
  |
  v
Pod
  |
  v
Java cleanup process
  |
  v
Exit 0
  |
  v
Job Complete
```

---

# 34. Full Dry Run: Stateful Redis Cluster Foundation

Scenario:

```text
You want Redis-like nodes with stable identity.
```

StatefulSet gives:

```text
redis-0
redis-1
redis-2
```

Flow:

```text
1. Headless Service redis is created.

2. StatefulSet redis is created.

3. Controller creates redis-0 with PVC data-redis-0.

4. redis-0 starts and becomes Ready.

5. Controller creates redis-1 with PVC data-redis-1.

6. redis-1 starts and becomes Ready.

7. Controller creates redis-2 with PVC data-redis-2.

8. Redis nodes can use stable DNS names.
```

Picture:

```text
redis-0.redis -> PVC data-redis-0
redis-1.redis -> PVC data-redis-1
redis-2.redis -> PVC data-redis-2
```

If redis-1 node fails:

```text
redis-1 becomes unavailable
replacement redis-1 is created
same PVC data-redis-1 attached when possible
same DNS identity returns
```

But remember:

```text
StatefulSet does not automatically configure Redis replication or failover.
```

It only gives the stable base.

---

# 35. Full Dry Run: DaemonSet Log Collection

Scenario:

```text
Every node must ship container logs to central system.
```

DaemonSet:

```text
fluent-bit
```

Cluster:

```text
node-1
node-2
node-3
```

Flow:

```text
1. DaemonSet object stored.

2. Controller sees 3 eligible nodes.

3. It creates one fluent-bit Pod per node.

4. Each Pod mounts host log paths.

5. Each agent tails local logs.

6. Logs are shipped to central backend.

7. Autoscaler adds node-4.

8. DaemonSet controller creates fluent-bit Pod on node-4.
```

Diagram:

```text
node-1: app logs -> local fluent-bit -> log backend
node-2: app logs -> local fluent-bit -> log backend
node-3: app logs -> local fluent-bit -> log backend
node-4: app logs -> local fluent-bit -> log backend
```

If logs missing from one node:

```text
Check whether daemon pod exists on that node first.
```

Not every logging issue is application issue.

---

# 36. Common Mistakes

```text
Mistake 1:
Using Deployment for Kafka/Postgres because it is familiar.
Correct:
Use StatefulSet or a proper operator/managed service.

Mistake 2:
Thinking StatefulSet automatically gives database HA.
Correct:
It gives identity and storage, not database intelligence.

Mistake 3:
Using Deployment for log agents.
Correct:
Use DaemonSet for node-level agents.

Mistake 4:
Running migrations inside every app Pod startup.
Correct:
Use controlled Job or migration tool strategy.

Mistake 5:
Writing CronJob with concurrencyPolicy Allow without thinking.
Correct:
Use Forbid when overlap is unsafe.

Mistake 6:
Creating Spring Boot Job that never exits.
Correct:
Disable web server and exit after task.

Mistake 7:
Ignoring idempotency in Jobs.
Correct:
Jobs can retry. Business logic must tolerate retries.

Mistake 8:
Forgetting PVCs remain after StatefulSet deletion.
Correct:
Understand storage lifecycle before deleting stateful apps.
```

---

# 37. Production Debugging Master Flow

When a workload is broken, first identify type.

```text
kubectl get deploy,statefulset,daemonset,job,cronjob -A
```

Then use workload-specific chain.

```text
StatefulSet:
  StatefulSet -> Pods -> PVC -> PV -> DNS -> App cluster logs

DaemonSet:
  DaemonSet -> Eligible nodes -> Pods per node -> Permissions -> Agent logs

Job:
  Job -> Pod -> Logs -> Exit code -> Retry count -> Side effects

CronJob:
  CronJob -> Schedule -> Created Jobs -> Job Pods -> Overlap -> Business result
```

ASCII:

```text
Do not debug Kubernetes randomly.

Workload Type
      |
      v
Expected Promise
      |
      v
Broken Promise
      |
      v
Specific Object Chain
      |
      v
Root Cause
```

Examples:

```text
StatefulSet broken promise:
redis-1 cannot keep its disk.

DaemonSet broken promise:
node-7 has no log agent.

Job broken promise:
migration never completes.

CronJob broken promise:
cleanup overlaps and overloads DB.
```

Debug the promise.

---

# 38. Interview Questions

## When would you use StatefulSet instead of Deployment?

Use StatefulSet when Pods need stable identity, stable network names, ordered lifecycle, and stable per-pod storage. Examples include Kafka, ZooKeeper, Redis clusters, and database-like systems. Deployment is better for stateless interchangeable API replicas.

## Does StatefulSet automatically make a database highly available?

No. StatefulSet provides Kubernetes primitives such as stable Pod names and persistent volumes. Database high availability still requires database-level replication, backup, restore, failover, quorum, and sometimes an operator.

## Why does StatefulSet often need a headless Service?

A headless Service allows stable DNS records for individual StatefulSet Pods, such as redis-0.redis and redis-1.redis. This is useful when cluster members need to address specific peers.

## When would you use DaemonSet?

Use DaemonSet when exactly one Pod should run on every eligible Node. Common examples are log collectors, metrics agents, node exporters, storage agents, and network plugins.

## Why not use Deployment for node agents?

Deployment controls total replica count, not one per node. If the cluster scales up or down, Deployment does not naturally guarantee every node gets one agent. DaemonSet follows node membership.

## What is a Kubernetes Job?

A Job runs one or more Pods until a specified number of successful completions is reached. It is for finite tasks such as migrations, batch processing, imports, and backfills.

## Why must Job code be idempotent?

Jobs can retry after failures. If the task performs external side effects, retries can duplicate work unless the business logic uses idempotency keys, transactions, task claiming, or safe partitioning.

## What is a CronJob?

A CronJob creates Jobs according to a cron schedule. It is used for repeated scheduled tasks such as cleanup, backups, reports, and periodic reconciliation.

## What does concurrencyPolicy mean in CronJob?

It controls what happens if a scheduled time arrives while the previous Job is still running. Allow starts another Job, Forbid skips the new run, and Replace stops the old Job and starts a new one.

## How would you debug a CronJob that did not run?

Check the CronJob exists, schedule expression, suspend flag, events from describe, whether Jobs were created, and then inspect Job Pods and logs. Also verify timezone assumptions and controller health.

---

# 39. Cheat Sheet

```text
Deployment
  Use for stateless long-running applications.
  Pods are interchangeable.

StatefulSet
  Use for stateful long-running applications.
  Gives stable Pod identity, ordinal, DNS, and per-pod PVC.

DaemonSet
  Use for node-level agents.
  Runs one Pod on every eligible Node.

Job
  Use for finite tasks.
  Success means required Pods completed with exit code 0.

CronJob
  Use for scheduled finite tasks.
  Creates Jobs according to cron schedule.
```

Commands:

```bash
# StatefulSet
kubectl get statefulset
kubectl get pods -l app=redis -o wide
kubectl get pvc
kubectl describe pod redis-0

# DaemonSet
kubectl get daemonset -A
kubectl get pods -o wide -n observability
kubectl get nodes --show-labels

# Job
kubectl get jobs
kubectl describe job <job-name>
kubectl logs job/<job-name>

# CronJob
kubectl get cronjob
kubectl describe cronjob <cronjob-name>
kubectl get jobs
```

Workload choice:

```text
Stateless API                   -> Deployment
Kafka/Redis/ZooKeeper           -> StatefulSet
Log/metric/security agent       -> DaemonSet
One-time migration/backfill     -> Job
Nightly cleanup/report/backup   -> CronJob
```

---

# 40. One Picture To Remember

```text
                 KUBERNETES WORKLOAD PROMISES

+-------------------------------------------------------------+
| Deployment                                                  |
| Keep N replaceable app pods running                         |
|                                                             |
| order-a   order-b   order-c                                 |
+-------------------------------------------------------------+

+-------------------------------------------------------------+
| StatefulSet                                                 |
| Keep named pods with stable disks                           |
|                                                             |
| redis-0 -> pvc-0                                            |
| redis-1 -> pvc-1                                            |
| redis-2 -> pvc-2                                            |
+-------------------------------------------------------------+

+-------------------------------------------------------------+
| DaemonSet                                                   |
| Keep one agent on every eligible node                       |
|                                                             |
| node-1 -> agent                                             |
| node-2 -> agent                                             |
| node-3 -> agent                                             |
+-------------------------------------------------------------+

+-------------------------------------------------------------+
| Job                                                         |
| Run task until successful completion                        |
|                                                             |
| pod starts -> work -> exit 0 -> complete                    |
+-------------------------------------------------------------+

+-------------------------------------------------------------+
| CronJob                                                     |
| Create Jobs by schedule                                     |
|                                                             |
| 02:00 -> Job -> Pod -> work -> complete                     |
+-------------------------------------------------------------+
```

Rule:

```text
Choose workload by the production promise:
replaceable, stateful, per-node, finish-once, or scheduled.
```

---

# 41. Final Production Checklist

```text
[ ] I can explain why Deployment is not enough for every workload.
[ ] I can explain StatefulSet stable identity without memorizing definitions.
[ ] I know StatefulSet does not automatically solve database HA.
[ ] I understand headless Service and stable DNS names.
[ ] I can debug StatefulSet using pods, PVCs, services, endpoints, and logs.
[ ] I can explain DaemonSet as one pod per eligible node.
[ ] I know why log and metrics agents are usually DaemonSets.
[ ] I can debug missing DaemonSet pods using node labels, taints, and tolerations.
[ ] I can explain Job as run-to-completion.
[ ] I know Spring Boot Job code must exit after work.
[ ] I understand backoffLimit, completions, and parallelism.
[ ] I know Jobs must be idempotent because retries happen.
[ ] I can explain CronJob as scheduled Job creation.
[ ] I understand cron schedule format.
[ ] I know concurrencyPolicy Allow, Forbid, and Replace.
[ ] I can debug CronJob schedule, created Jobs, Pods, logs, and business output.
```

---

# 42. Final Memory Hook

Do not memorize these objects as definitions.

Remember the promise:

```text
Deployment:
  Keep replaceable app replicas alive.

StatefulSet:
  Keep named stateful members alive with stable disks.

DaemonSet:
  Keep one node agent on every eligible node.

Job:
  Run finite work until success.

CronJob:
  Trigger finite work by schedule.
```

Final sentence:

```text
Kubernetes workload objects are not random YAML kinds.
They are different production contracts for different kinds of work.
```
