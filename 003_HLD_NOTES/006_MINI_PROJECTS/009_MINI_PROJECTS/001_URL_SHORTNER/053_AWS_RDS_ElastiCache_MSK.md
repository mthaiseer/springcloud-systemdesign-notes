# 053_AWS_RDS_ElastiCache_MSK.md
# MiniURLShortener — AWS RDS, ElastiCache & MSK

> Core mental model: **RDS, ElastiCache, and MSK are managed data infrastructure services. Your Spring Boot app should stay stateless in Kubernetes/ECS, while AWS manages the heavy operational parts of database, cache, and Kafka infrastructure.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. From Docker Compose To AWS Managed Services](#4-from-docker-compose-to-aws-managed-services)
- [5. Big Architecture Picture](#5-big-architecture-picture)
- [6. AWS RDS Mental Model](#6-aws-rds-mental-model)
- [7. RDS For MiniURLShortener](#7-rds-for-miniurlshortener)
- [8. RDS Multi-AZ](#8-rds-multi-az)
- [9. RDS Read Replicas](#9-rds-read-replicas)
- [10. RDS Connection Pool Warning](#10-rds-connection-pool-warning)
- [11. RDS Security Groups](#11-rds-security-groups)
- [12. RDS Spring Boot Configuration](#12-rds-spring-boot-configuration)
- [13. ElastiCache Mental Model](#13-elasticache-mental-model)
- [14. ElastiCache For MiniURLShortener](#14-elasticache-for-miniurlshortener)
- [15. Redis Cluster Mode Disabled vs Enabled](#15-redis-cluster-mode-disabled-vs-enabled)
- [16. ElastiCache Multi-AZ Failover](#16-elasticache-multi-az-failover)
- [17. ElastiCache Endpoints](#17-elasticache-endpoints)
- [18. Redis Cache-Aside Flow](#18-redis-cache-aside-flow)
- [19. ElastiCache Spring Boot Configuration](#19-elasticache-spring-boot-configuration)
- [20. MSK Mental Model](#20-msk-mental-model)
- [21. MSK For MiniURLShortener](#21-msk-for-miniurlshortener)
- [22. Kafka Topics And Partitions](#22-kafka-topics-and-partitions)
- [23. MSK Brokers And Bootstrap Servers](#23-msk-brokers-and-bootstrap-servers)
- [24. MSK Producer Flow](#24-msk-producer-flow)
- [25. MSK Consumer Group Flow](#25-msk-consumer-group-flow)
- [26. MSK Spring Boot Configuration](#26-msk-spring-boot-configuration)
- [27. VPC And Private Networking](#27-vpc-and-private-networking)
- [28. Security Group Design](#28-security-group-design)
- [29. Secrets And Configuration](#29-secrets-and-configuration)
- [30. Kubernetes/ECS App Environment Variables](#30-kubernetesecs-app-environment-variables)
- [31. Step-by-Step Dry Runs](#31-step-by-step-dry-runs)
- [32. Internal Execution Walkthrough](#32-internal-execution-walkthrough)
- [33. Scaling Strategy](#33-scaling-strategy)
- [34. Observability And CloudWatch](#34-observability-and-cloudwatch)
- [35. Cost Mindset](#35-cost-mindset)
- [36. Debugging Mindset](#36-debugging-mindset)
- [37. Production Failure Stories](#37-production-failure-stories)
- [38. Common Mistakes](#38-common-mistakes)
- [39. Interview-Ready Explanation](#39-interview-ready-explanation)
- [40. Senior Engineer Checklist](#40-senior-engineer-checklist)
- [41. One-Page Cheat Sheet](#41-one-page-cheat-sheet)
- [42. One Picture To Remember](#42-one-picture-to-remember)

---

## 1. Why This Exists

In local development, MiniURLShortener can run with Docker Compose:

```text
Spring Boot app
Postgres container
Redis container
Kafka container
```

That is good for learning.

But production infrastructure should not usually run like this:

```text
one manually managed Postgres container
one manually managed Redis container
one manually managed Kafka container
```

Production needs:

```text
backups
failover
monitoring
patching
encryption
private networking
high availability
scaling
maintenance windows
IAM/security controls
```

AWS managed services reduce operational burden:

```text
Amazon RDS:
    managed relational database

Amazon ElastiCache:
    managed Redis/Valkey cache

Amazon MSK:
    managed Apache Kafka
```

AWS describes Amazon MSK as a fully managed service for building and running applications that use Apache Kafka, while still allowing Apache Kafka data-plane operations such as producing and consuming records. citeturn285988search1

For MiniURLShortener:

```text
RDS        -> source of truth for short URLs
ElastiCache -> fast cache for redirect lookups
MSK        -> event stream for clicks/analytics
```

Production memory:

```text
Your app should be stateless. Managed services hold durable/shared state.
```

---

## 2. The One Core Mental Model

Think of the application as stateless workers.

Think of AWS managed services as durable/shared infrastructure.

ASCII:

```text
                   +----------------------+
                   | Users / Clients      |
                   +----------+-----------+
                              |
                              v
                   +----------------------+
                   | Load Balancer / ALB  |
                   +----------+-----------+
                              |
                              v
             +-------------------------------+
             | Stateless Spring Boot Pods    |
             | mini-url-shortener            |
             +----+------------+-------------+
                  |            |
                  |            |
                  v            v
        +--------------+   +----------------+
        | ElastiCache  |   | Amazon RDS     |
        | Redis cache  |   | Postgres       |
        +--------------+   +----------------+
                  |
                  v
             +-----------+
             | Amazon MSK|
             | Kafka     |
             +-----------+
```

One-line memory:

```text
App scales horizontally; RDS stores truth; ElastiCache serves hot reads; MSK carries events.
```

Do not put durable state inside application Pods.

Bad:

```text
Pod memory stores all short URLs.
```

Good:

```text
RDS stores durable data.
Redis caches hot redirects.
Kafka carries click events.
```

---

## 3. Problem Statement

Design AWS managed infrastructure for MiniURLShortener.

We need to understand:

```text
1. Why use RDS instead of self-managed Postgres.
2. Why use ElastiCache instead of local Redis.
3. Why use MSK instead of self-managed Kafka.
4. How Kubernetes/ECS app connects privately.
5. How Multi-AZ failover works conceptually.
6. How read replicas differ from Multi-AZ.
7. How Redis cluster mode affects clients.
8. How Kafka topics/partitions work in MSK.
9. How to configure Spring Boot.
10. How to debug connection, latency, and failover issues.
```

Out of scope:

```text
1. Terraform full implementation.
2. AWS console click-by-click guide.
3. Deep IAM policy writing.
4. Aurora deep dive.
5. MSK Connect deep dive.
6. Redis Global Datastore deep dive.
```

This chapter is infrastructure mental model plus production application wiring.

---

## 4. From Docker Compose To AWS Managed Services

Local Docker Compose:

```text
app -> postgres
app -> redis
app -> kafka
```

AWS production-like:

```text
app -> RDS endpoint
app -> ElastiCache endpoint
app -> MSK bootstrap brokers
```

ASCII:

```text
LOCAL
+------------------------------+
| app container                |
| postgres container           |
| redis container              |
| kafka container              |
+------------------------------+

AWS
+------------------------------+
| app Pods / ECS Tasks         |
| Amazon RDS                   |
| Amazon ElastiCache           |
| Amazon MSK                   |
+------------------------------+
```

Mapping:

```text
Docker Compose postgres -> Amazon RDS PostgreSQL
Docker Compose redis    -> Amazon ElastiCache Redis/Valkey
Docker Compose kafka    -> Amazon MSK
```

Application code should not care much.

Only connection configuration changes:

```text
SPRING_DATASOURCE_URL
SPRING_DATA_REDIS_HOST
SPRING_KAFKA_BOOTSTRAP_SERVERS
```

This is why environment variables matter.

---

## 5. Big Architecture Picture

Production-style AWS architecture:

```text
Route53
  |
  v
ALB / Ingress
  |
  v
EKS / ECS app service
  |
  +--> RDS private endpoint
  +--> ElastiCache private endpoint
  +--> MSK private bootstrap brokers
```

ASCII:

```text
AWS VPC
+-------------------------------------------------------------+
| Public Subnets                                               |
|                                                             |
|   +------------------+                                      |
|   | ALB / Ingress    |  <--- Internet                       |
|   +--------+---------+                                      |
|            |                                                |
|            v                                                |
| Private App Subnets                                         |
|   +------------------+    +------------------+              |
|   | App Pod / Task   |    | App Pod / Task   |              |
|   +----+------+------+    +----+------+------+              |
|        |      |                |      |                     |
|        |      |                |      |                     |
|        v      v                v      v                     |
| Private Data Subnets                                        |
|   +-----------+   +--------------+   +------------------+   |
|   | RDS       |   | ElastiCache  |   | MSK Brokers       |   |
|   | Postgres  |   | Redis/Valkey |   | Kafka             |   |
|   +-----------+   +--------------+   +------------------+   |
+-------------------------------------------------------------+
```

Key production idea:

```text
Database/cache/Kafka should usually be private, not public.
```

Only edge load balancer is public.

---

## 6. AWS RDS Mental Model

Amazon RDS is managed relational database.

For MiniURLShortener, choose:

```text
Amazon RDS for PostgreSQL
```

RDS manages:

```text
database instance provisioning
storage
backups
snapshots
minor version patching options
monitoring metrics
Multi-AZ failover option
read replicas option
parameter groups
subnet groups
security groups
```

Your app still owns:

```text
schema design
indexes
queries
transactions
connection pooling
migrations
data correctness
```

ASCII:

```text
Spring Boot App
     |
     v
RDS Endpoint
     |
     v
PostgreSQL Engine
     |
     v
Tables / Indexes / Transactions
```

RDS is not a magic performance fix.

Bad SQL remains bad SQL.

Missing index remains missing index.

Too many connections still hurts.

---

## 7. RDS For MiniURLShortener

RDS stores durable data:

```text
short_urls table
users table
api_keys table
audit logs
billing records later
```

Important table:

```text
short_urls
```

Example columns:

```text
id
short_code
long_url
status
created_at
expires_at
owner_user_id
```

Critical indexes:

```text
UNIQUE(short_code)
INDEX(owner_user_id, created_at)
INDEX(expires_at)
```

Redirect flow:

```text
GET /abc123
   |
   v
Redis lookup
   |
   +-- cache hit -> redirect
   |
   +-- cache miss -> RDS lookup by short_code
```

Create flow:

```text
POST /api/v1/urls
   |
   v
validate
   |
   v
insert into RDS
   |
   v
publish event to MSK
```

ASCII:

```text
Create URL:
App -> RDS write -> MSK event

Redirect:
App -> Redis read
        |
        +-- miss -> RDS read -> Redis set
```

RDS is source of truth.

Redis is cache.

MSK is event stream.

---

## 8. RDS Multi-AZ

Multi-AZ is for high availability.

Conceptual setup:

```text
Primary DB in one AZ
Standby DB in another AZ
```

If primary fails:

```text
RDS fails over to standby
endpoint remains same
app reconnects
```

ASCII:

```text
AZ-1                          AZ-2
+------------+                +------------+
| RDS Primary|  replication   | RDS Standby|
| read/write | -------------> | standby    |
+------------+                +------------+

Failure:
Primary X
   |
   v
Standby promoted
```

Application uses one endpoint:

```text
miniurl-db.xxxxxx.region.rds.amazonaws.com
```

After failover:

```text
same endpoint points to new primary
```

Your app must handle:

```text
temporary connection errors
connection pool reconnect
transaction retry where safe
short outage window
```

Important:

```text
Multi-AZ is not read scaling.
It is availability/failover.
```

For read scaling, use read replicas.

---

## 9. RDS Read Replicas

Read replicas help read scaling.

Primary handles:

```text
writes
strong source of truth
```

Read replica handles:

```text
read queries
reporting
some read-heavy traffic
```

ASCII:

```text
                writes
App Writer ----------------> RDS Primary
                                |
                                | async replication
                                v
App Reader ----------------> Read Replica
                reads
```

Important:

```text
Read replicas are asynchronous.
They can lag.
```

Do not read immediately-after-write from replica if you need fresh data.

MiniURLShortener redirect read:

```text
May use primary first.
Later read replica can help reporting/admin reads.
```

For redirect API, better first optimization:

```text
Redis cache
```

Read replica is useful for:

```text
analytics queries
admin dashboards
reports
bulk lookup jobs
```

Senior memory:

```text
Multi-AZ protects availability. Read replicas scale reads.
```

---

## 10. RDS Connection Pool Warning

Every app Pod has Hikari pool.

Example:

```text
HPA max replicas = 20
Hikari maxPoolSize = 20
```

Total potential DB connections:

```text
20 * 20 = 400
```

If RDS max connections is lower:

```text
connection failures
timeouts
high DB memory
```

ASCII:

```text
Pod 1 -> 20 connections
Pod 2 -> 20 connections
...
Pod 20 -> 20 connections

Total -> 400 connections to RDS
```

Production fixes:

```text
1. Tune Hikari maxPoolSize per Pod.
2. Set HPA maxReplicas based on DB budget.
3. Use PgBouncer/RDS Proxy where appropriate.
4. Use Redis cache to reduce DB reads.
5. Optimize queries and indexes.
```

Golden formula:

```text
max_app_db_connections = max_pods * hikari_max_pool_size
```

This must fit database capacity.

---

## 11. RDS Security Groups

Security Group is a virtual firewall.

Recommended:

```text
App Security Group:
    attached to EKS nodes/Pods/ECS tasks

RDS Security Group:
    allows inbound 5432 only from App Security Group
```

ASCII:

```text
App SG
  |
  | allow PostgreSQL 5432
  v
RDS SG
```

Bad:

```text
RDS publicly accessible
RDS allows 0.0.0.0/0 on 5432
```

Good:

```text
RDS private subnet
RDS not publicly accessible
RDS inbound only from app SG
```

Debug connection:

```text
DNS resolves?
Security group allows?
NACL allows?
Route table correct?
DB username/password correct?
SSL requirement?
```

Production memory:

```text
Network access should be least privilege.
```

---

## 12. RDS Spring Boot Configuration

Environment variables:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://miniurl-db.xxxxxx.eu-central-1.rds.amazonaws.com:5432/miniurl
SPRING_DATASOURCE_USERNAME=miniurl_app
SPRING_DATASOURCE_PASSWORD=from-secret-manager
```

Application YAML:

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    hikari:
      maximum-pool-size: ${DB_POOL_MAX_SIZE:10}
      minimum-idle: ${DB_POOL_MIN_IDLE:2}
      connection-timeout: 3000
      validation-timeout: 1000
      max-lifetime: 1800000
```

For failover:

```text
App must tolerate broken connections.
Hikari should create new connections after old ones fail.
Requests in-flight may fail and need safe retry at API/client level.
```

Do not set huge pool.

Start with:

```text
5-10 connections per Pod
```

then measure.

---

## 13. ElastiCache Mental Model

Amazon ElastiCache is managed in-memory cache.

Common engines:

```text
Redis OSS
Valkey
Memcached
```

For MiniURLShortener:

```text
Redis/Valkey style cache is natural.
```

ElastiCache can manage:

```text
nodes
replication groups
failover
backups depending on engine/config
monitoring
scaling operations
subnet groups
security groups
```

AWS docs describe ElastiCache replication groups as improving scalability and guarding against data loss for Valkey or Redis OSS, and note that with replicas and Multi-AZ enabled, a primary can fail over to a read replica if it fails. citeturn285988search2

ASCII:

```text
App
 |
 v
ElastiCache endpoint
 |
 v
Redis primary/replicas/shards
```

Cache is not source of truth.

If Redis loses data:

```text
App can rebuild from RDS
```

for URL mappings.

---

## 14. ElastiCache For MiniURLShortener

Use Redis for:

```text
shortCode -> longUrl cache
rate limiting counters
hot URL metadata
temporary idempotency keys
distributed locks only if carefully designed
```

Redirect cache key:

```text
url:code:abc123 -> https://example.com
```

TTL:

```text
based on link expiry
or fixed TTL like 1 hour/24 hours
```

Cache-aside flow:

```text
1. App checks Redis.
2. If hit, return redirect.
3. If miss, read RDS.
4. Store result in Redis.
5. Return redirect.
```

ASCII:

```text
GET /abc123
   |
   v
Redis GET url:code:abc123
   |
   +-- hit -> redirect
   |
   +-- miss -> RDS SELECT -> Redis SET -> redirect
```

This reduces RDS read load.

---

## 15. Redis Cluster Mode Disabled vs Enabled

Cluster mode disabled:

```text
one primary shard
optional read replicas
simpler client config
```

Cluster mode enabled:

```text
multiple shards
data partitioned by hash slot
higher write/read scale
client must support cluster mode
```

ASCII:

```text
Cluster mode disabled:
          +---------+
App ----> | Primary |
          +---------+
              |
              v
          Replicas

Cluster mode enabled:
App
 |
 +--> Shard 1 primary + replicas
 +--> Shard 2 primary + replicas
 +--> Shard 3 primary + replicas
```

Start simple:

```text
cluster mode disabled + replica + Multi-AZ
```

Scale later:

```text
cluster mode enabled when one shard is not enough
```

Client warning:

```text
Cluster mode enabled needs Redis cluster-aware client.
```

Spring Boot with Lettuce can support Redis cluster, but config differs.

---

## 16. ElastiCache Multi-AZ Failover

Multi-AZ improves fault tolerance.

AWS docs state that enabling ElastiCache Multi-AZ for Valkey or Redis OSS improves fault tolerance, particularly when the primary becomes unreachable or fails; Multi-AZ requires more than one node in each shard. citeturn285988search0

Concept:

```text
Primary in AZ-1
Replica in AZ-2
```

If primary fails:

```text
Replica promoted to primary
endpoint updates
client reconnects
```

ASCII:

```text
AZ-1                         AZ-2
+--------------+             +--------------+
| Redis Primary| replication | Redis Replica|
+--------------+-----------> +--------------+

Failure:
Primary X
   |
   v
Replica promoted
```

During failover:

```text
brief errors/timeouts possible
client must reconnect
some async replication loss possible
```

Because Redis replication is asynchronous, a small amount of recently written cache data may be lost.

For cache:

```text
acceptable if RDS is source of truth
```

For critical counters:

```text
understand loss/consistency implications
```

---

## 17. ElastiCache Endpoints

Cluster mode disabled commonly has:

```text
primary endpoint
reader endpoint
node endpoints
```

Use primary endpoint for writes.

Use reader endpoint for read replicas when appropriate.

For URL redirect cache:

```text
GET can use Redis primary or reader depending consistency/client setup.
SET must go to primary.
```

Simple app config:

```text
SPRING_DATA_REDIS_HOST=miniurl-redis.xxxxxx.cache.amazonaws.com
SPRING_DATA_REDIS_PORT=6379
```

If TLS enabled:

```text
rediss:// endpoint or SSL config depending client
```

ASCII:

```text
Spring Boot Redis client
      |
      v
ElastiCache primary endpoint
      |
      v
current primary node
```

Important:

```text
Use endpoint DNS, not node IP.
```

Endpoints can change during failover.

---

## 18. Redis Cache-Aside Flow

Cache-aside is best first model.

Read flow:

```text
1. Try Redis.
2. If found, use cached value.
3. If not found, query RDS.
4. Store in Redis.
5. Return response.
```

ASCII:

```text
Request /abc123
      |
      v
Redis GET
      |
      +-- hit ----------------> redirect
      |
      +-- miss
             |
             v
          RDS SELECT
             |
             v
          Redis SET
             |
             v
          redirect
```

Write flow:

```text
Create short URL
      |
      v
RDS INSERT
      |
      v
Redis SET optional warm cache
```

Delete/block flow:

```text
Update RDS status
      |
      v
Delete/update Redis key
```

Rule:

```text
RDS first for truth.
Redis second for speed.
```

---

## 19. ElastiCache Spring Boot Configuration

Basic:

```yaml
spring:
  data:
    redis:
      host: ${SPRING_DATA_REDIS_HOST}
      port: ${SPRING_DATA_REDIS_PORT:6379}
      timeout: 2s
```

Environment:

```text
SPRING_DATA_REDIS_HOST=miniurl-redis.xxxxxx.cache.amazonaws.com
SPRING_DATA_REDIS_PORT=6379
```

Lettuce pool config if using pooling:

```yaml
spring:
  data:
    redis:
      lettuce:
        pool:
          max-active: 16
          max-idle: 8
          min-idle: 2
          max-wait: 100ms
```

Production mindset:

```text
1. Set timeout.
2. Do not let Redis hang requests forever.
3. Use circuit breaker/bulkhead for cache if needed.
4. Cache failure should often degrade to RDS, not fail all redirects.
```

But be careful:

```text
If Redis fails and all traffic goes to RDS, RDS may collapse.
```

Use:

```text
rate limiting
bulkheads
fallback protection
hot key strategy
```

---

## 20. MSK Mental Model

Amazon MSK is managed Apache Kafka.

AWS docs describe MSK as a fully managed service that creates, updates, and deletes clusters while letting clients use Kafka data-plane operations like producing and consuming records. citeturn285988search1

Kafka mental model:

```text
Topic = named event log
Partition = ordered shard of topic
Producer = writes events
Consumer group = reads events in parallel
Broker = Kafka server
```

ASCII:

```text
Producer App
    |
    v
Topic: click-events
+-------------+-------------+-------------+
| Partition 0 | Partition 1 | Partition 2 |
+-------------+-------------+-------------+
      |             |             |
      v             v             v
 Consumer A     Consumer B     Consumer C
```

MSK manages brokers.

Your app still owns:

```text
topic design
partition key
producer config
consumer config
schema/versioning
idempotency
retry/DLT strategy
lag monitoring
```

---

## 21. MSK For MiniURLShortener

Use MSK for events:

```text
url-created
url-clicked
url-expired
abuse-detected
analytics-aggregated
```

Most important:

```text
click-events
```

Redirect flow:

```text
GET /abc123
   |
   v
return redirect quickly
   |
   v
publish click event asynchronously
```

Do not make redirect depend strongly on Kafka success.

Better:

```text
Redirect user first.
Publish event async/best-effort.
```

ASCII:

```text
User GET /abc123
       |
       v
App returns 302
       |
       v
Async producer sends click event to MSK
       |
       v
Analytics worker consumes later
```

Kafka gives decoupling:

```text
redirect path does not directly write analytics database
```

---

## 22. Kafka Topics And Partitions

Topic:

```text
click-events
```

Partitions:

```text
parallel ordered logs
```

Partition key:

```text
shortCode
```

If key is shortCode:

```text
all clicks for same shortCode go to same partition
```

ASCII:

```text
click-events topic

key abc123 -> partition 1
key xyz999 -> partition 2
key abc123 -> partition 1 again
```

Ordering:

```text
Kafka preserves order within one partition.
Not across all partitions.
```

Partition count affects:

```text
producer parallelism
consumer group parallelism
throughput
future scaling
```

If topic has 12 partitions:

```text
maximum active consumers in one group = 12
```

Extra consumers beyond partition count stay idle.

---

## 23. MSK Brokers And Bootstrap Servers

Kafka client starts with bootstrap brokers.

Example:

```text
b-1.mskcluster.x.kafka.region.amazonaws.com:9092,
b-2.mskcluster.x.kafka.region.amazonaws.com:9092,
b-3.mskcluster.x.kafka.region.amazonaws.com:9092
```

Client uses bootstrap to discover cluster metadata.

ASCII:

```text
Spring Kafka Client
      |
      v
Bootstrap broker list
      |
      v
Fetch metadata
      |
      v
Know topic partitions and leaders
      |
      v
Produce/consume records
```

Do not hardcode one broker only.

Use multiple bootstrap brokers.

Environment:

```text
SPRING_KAFKA_BOOTSTRAP_SERVERS=b-1:9092,b-2:9092,b-3:9092
```

Security may require:

```text
TLS
SASL/IAM
SASL/SCRAM
mTLS
```

depending MSK configuration.

---

## 24. MSK Producer Flow

Click event producer:

```text
Redirect request
   |
   v
Build ClickEvent
   |
   v
KafkaTemplate.send("click-events", shortCode, event)
   |
   v
MSK topic partition
```

ASCII:

```text
App Producer
  |
  | key = shortCode
  v
MSK Broker
  |
  v
Topic click-events
  |
  v
Partition selected by key hash
```

Producer config concepts:

```text
acks
retries
delivery.timeout.ms
linger.ms
batch.size
compression.type
enable.idempotence
```

For analytics events:

```text
At-least-once is usually acceptable.
Duplicates possible.
Consumer should be idempotent.
```

Do not block redirect too long on Kafka.

Use async send and handle errors.

---

## 25. MSK Consumer Group Flow

Analytics worker:

```text
consumer group = click-analytics-worker
topic = click-events
```

If topic has 6 partitions and 3 consumers:

```text
each consumer gets about 2 partitions
```

ASCII:

```text
click-events partitions:
P0 P1 P2 P3 P4 P5

Consumer group:
Worker A -> P0 P1
Worker B -> P2 P3
Worker C -> P4 P5
```

If one worker dies:

```text
Kafka rebalances partitions to remaining workers
```

Consumer responsibilities:

```text
process event
write analytics store
commit offset after success
retry failures safely
send poison messages to DLT
```

Offset:

```text
position in partition
```

Lag:

```text
events produced but not yet consumed
```

Lag is key metric.

---

## 26. MSK Spring Boot Configuration

Basic:

```yaml
spring:
  kafka:
    bootstrap-servers: ${SPRING_KAFKA_BOOTSTRAP_SERVERS}

    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
      properties:
        enable.idempotence: true
        delivery.timeout.ms: 30000
        linger.ms: 5
        compression.type: lz4

    consumer:
      group-id: click-analytics-worker
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      auto-offset-reset: earliest
      properties:
        spring.json.trusted.packages: "com.miniurl.shortener.analytics"
```

Environment:

```text
SPRING_KAFKA_BOOTSTRAP_SERVERS=b-1:9092,b-2:9092,b-3:9092
```

For MSK security:

```text
additional security protocol/SASL/TLS config may be needed
```

Keep secrets outside image.

---

## 27. VPC And Private Networking

RDS, ElastiCache, and MSK usually live inside a VPC.

Recommended:

```text
Public subnets:
    ALB / NAT Gateway

Private app subnets:
    EKS worker nodes / ECS tasks

Private data subnets:
    RDS / ElastiCache / MSK
```

ASCII:

```text
VPC
+--------------------------------------------------+
| Public Subnet                                    |
|   ALB                                            |
|                                                  |
| Private App Subnet                               |
|   EKS Pods / ECS Tasks                           |
|                                                  |
| Private Data Subnet                              |
|   RDS + ElastiCache + MSK                        |
+--------------------------------------------------+
```

App connects privately:

```text
app -> private RDS endpoint
app -> private Redis endpoint
app -> private MSK brokers
```

Do not expose:

```text
RDS public
Redis public
Kafka public
```

unless there is a strong reason and strict controls.

---

## 28. Security Group Design

Simple SG design:

```text
ALB SG:
    inbound 443 from internet
    outbound to app SG

App SG:
    inbound from ALB SG on app port
    outbound to data services

RDS SG:
    inbound 5432 from app SG

ElastiCache SG:
    inbound 6379 from app SG

MSK SG:
    inbound Kafka ports from app SG
```

ASCII:

```text
Internet
  |
  v
ALB SG
  |
  v
App SG
  |
  +--> RDS SG:5432
  +--> Redis SG:6379
  +--> MSK SG:9092/9094/etc
```

Do not use:

```text
0.0.0.0/0 to database/cache/brokers
```

Debug SG:

```text
source SG allowed?
correct port?
correct subnet route?
NACL blocking?
DNS resolves private IP?
```

Security group referencing is cleaner than hardcoded IP ranges.

---

## 29. Secrets And Configuration

Do not bake AWS endpoints/secrets into Docker image.

Use runtime config.

Secrets:

```text
DB username/password
Redis auth token if enabled
Kafka SASL credentials if used
API keys
JWT secrets
```

Store in:

```text
AWS Secrets Manager
AWS Systems Manager Parameter Store
Kubernetes Secret integrated with external secrets
ECS task secrets
```

Non-secret config:

```text
RDS endpoint
Redis endpoint
MSK bootstrap brokers
profile
timeouts
pool sizes
```

Can be in:

```text
ConfigMap
SSM Parameter Store
ECS environment variables
Helm values
```

ASCII:

```text
Secrets Manager
      |
      v
Kubernetes Secret / ECS Task Secret
      |
      v
App environment
      |
      v
Spring Boot config
```

Production memory:

```text
Image is immutable artifact.
Environment injects configuration.
Secrets are not code.
```

---

## 30. Kubernetes/ECS App Environment Variables

Example env:

```text
SPRING_PROFILES_ACTIVE=aws-prod

SPRING_DATASOURCE_URL=jdbc:postgresql://miniurl-db.xxxxxx.rds.amazonaws.com:5432/miniurl
SPRING_DATASOURCE_USERNAME=miniurl_app
SPRING_DATASOURCE_PASSWORD=<secret>

DB_POOL_MAX_SIZE=10

SPRING_DATA_REDIS_HOST=miniurl-cache.xxxxxx.cache.amazonaws.com
SPRING_DATA_REDIS_PORT=6379

SPRING_KAFKA_BOOTSTRAP_SERVERS=b-1:9092,b-2:9092,b-3:9092
```

Kubernetes ConfigMap/Secret split:

```text
ConfigMap:
    endpoints, profile, pool size

Secret:
    usernames/passwords/tokens
```

ECS task definition:

```text
environment:
    non-secret values

secrets:
    values pulled from Secrets Manager/SSM
```

ASCII:

```text
Deployment/ECS Task
   |
   +-- Config values
   +-- Secret values
   |
   v
Spring Boot starts with AWS endpoints
```

---

## 31. Step-by-Step Dry Runs

### Dry Run 1: Redirect Cache Hit

Request:

```text
GET /abc123
```

Flow:

```text
1. ALB/Ingress sends request to app Pod.
2. App checks ElastiCache Redis.
3. Key url:code:abc123 exists.
4. App gets long URL.
5. App returns 302 redirect.
6. App publishes click event to MSK asynchronously.
```

ASCII:

```text
User -> App -> Redis hit -> 302
              |
              v
             MSK click event
```

RDS is not touched.

This is ideal fast path.

---

### Dry Run 2: Redirect Cache Miss

Flow:

```text
1. App checks Redis.
2. Cache miss.
3. App queries RDS by short_code.
4. RDS returns long URL.
5. App writes Redis cache.
6. App returns 302.
7. App publishes click event to MSK.
```

ASCII:

```text
App
 |
 v
Redis MISS
 |
 v
RDS SELECT
 |
 v
Redis SET
 |
 v
302 redirect
```

This is slower but rebuilds cache.

---

### Dry Run 3: RDS Failover

Scenario:

```text
RDS primary fails.
```

Flow:

```text
1. Existing DB connections break.
2. RDS promotes standby.
3. DNS endpoint points to new primary.
4. Hikari detects broken connections.
5. App creates new connections.
6. Some in-flight requests fail.
7. Service recovers.
```

ASCII:

```text
Primary X
  |
  v
Standby promoted
  |
  v
App reconnects
```

Application must tolerate transient DB errors.

---

### Dry Run 4: ElastiCache Failover

Scenario:

```text
Redis primary fails.
```

Flow:

```text
1. Redis primary unreachable.
2. Replica promoted.
3. Endpoint updates.
4. Client reconnects.
5. Cache may lose recent async writes.
6. App rebuilds missing keys from RDS.
```

ASCII:

```text
Redis primary X
   |
   v
Replica promoted
   |
   v
App reconnects
   |
   v
Cache rebuild from RDS if needed
```

Because Redis is cache, this is acceptable if RDS is healthy.

---

### Dry Run 5: MSK Broker Failure

Scenario:

```text
One broker fails.
```

Flow:

```text
1. Kafka client detects connection issue.
2. Metadata refresh happens.
3. Partition leaders may move depending cluster state.
4. Producer retries if configured.
5. Consumers rebalance if needed.
6. Event flow continues if cluster has enough healthy brokers.
```

ASCII:

```text
Broker 1 X
  |
  v
Clients refresh metadata
  |
  v
Continue with healthy brokers
```

Producer and consumer configs matter.

---

## 32. Internal Execution Walkthrough

Startup:

```text
1. App Pod starts.
2. Spring Boot reads environment variables.
3. Hikari creates connections to RDS endpoint.
4. Redis client connects to ElastiCache endpoint.
5. Kafka producer/consumer connects to MSK bootstrap brokers.
6. App readiness turns healthy.
7. ALB/Ingress sends traffic.
```

ASCII:

```text
Pod startup
   |
   v
Read env config
   |
   +--> connect RDS
   +--> connect Redis
   +--> connect MSK
   |
   v
Readiness OK
   |
   v
Serve traffic
```

Request path:

```text
User -> ALB/Ingress -> App -> Redis/RDS/MSK
```

Control plane:

```text
AWS manages service infrastructure.
App manages application correctness.
```

---

## 33. Scaling Strategy

App scaling:

```text
HPA/ECS autoscaling increases app replicas.
```

RDS scaling:

```text
vertical instance size
storage scaling
read replicas
Aurora options
query/index optimization
connection pooling
```

ElastiCache scaling:

```text
bigger node
more replicas for read scale
cluster mode shards for write/data scale
```

MSK scaling:

```text
broker size
broker count
partition count
storage
producer/consumer tuning
```

ASCII:

```text
More app Pods
   |
   +--> more DB connections
   +--> more Redis connections/QPS
   +--> more Kafka producers/consumers
```

Scale plan:

```text
1. Increase app replicas only within dependency budget.
2. Watch RDS CPU/IO/connections.
3. Watch Redis CPU/memory/evictions/latency.
4. Watch Kafka broker CPU/disk/under-replicated partitions/consumer lag.
5. Tune bottleneck.
```

---

## 34. Observability And CloudWatch

Watch RDS:

```text
CPUUtilization
DatabaseConnections
FreeStorageSpace
ReadLatency
WriteLatency
ReadIOPS
WriteIOPS
Deadlocks
ReplicaLag
```

Watch ElastiCache:

```text
CPUUtilization
EngineCPUUtilization
CurrConnections
Evictions
CacheHits
CacheMisses
ReplicationLag
SwapUsage
NetworkBytesIn/Out
```

Watch MSK:

```text
broker CPU
disk usage
under-replicated partitions
offline partitions
consumer lag
bytes in/out
produce/fetch latency
```

Watch App:

```text
p95/p99 latency
error rate
Hikari active connections
Redis latency
Kafka producer errors
Kafka consumer lag
cache hit ratio
```

ASCII:

```text
App Metrics + AWS Metrics
        |
        v
CloudWatch / Prometheus / Grafana
        |
        v
Alert before outage
```

Golden rule:

```text
If you cannot see it, you cannot operate it.
```

---

## 35. Cost Mindset

Managed services cost money.

Cost drivers:

```text
RDS:
    instance class
    storage
    IOPS
    Multi-AZ
    read replicas
    backups

ElastiCache:
    node type
    number of nodes
    replicas
    shards
    data transfer

MSK:
    broker type
    broker count
    storage
    throughput
    data transfer
```

Cost mistakes:

```text
oversized DB
too many app Pods causing bigger DB need
too many Redis shards too early
Kafka over-partitioning
keeping dev clusters running 24/7
no retention control
```

Practical learning setup:

```text
Use Docker Compose locally.
Use small AWS dev environment only when needed.
Destroy after practice.
```

Production mindset:

```text
Optimize after measuring.
Do not overbuild before traffic exists.
```

---

## 36. Debugging Mindset

When app cannot connect to AWS managed service, ask:

```text
Is endpoint correct?
Is DNS resolving?
Is app in same VPC or connected VPC?
Are subnets/routes correct?
Does security group allow traffic?
Is service publicly inaccessible by design?
Are credentials correct?
Is TLS/auth required?
Is the port correct?
Is failover happening?
Are connection pools exhausted?
```

Debug RDS:

```text
Can app resolve endpoint?
Can app reach port 5432?
Are username/password correct?
Are max connections reached?
Are queries slow?
Is failover in progress?
```

Debug Redis:

```text
Can app reach port 6379?
Is auth token/TLS required?
Are keys evicting?
Is CPU high?
Is connection count high?
Is failover happening?
```

Debug MSK:

```text
Can app reach all broker ports?
Are bootstrap brokers correct?
Is security protocol correct?
Are topics created?
Are partitions healthy?
Is consumer lag increasing?
```

Golden question:

```text
Is this network, auth, capacity, failover, client config, or application logic?
```

---

## 37. Production Failure Stories

### Failure Story 1: RDS Publicly Exposed

RDS is created as public.

Security group allows:

```text
0.0.0.0/0:5432
```

Risk:

```text
Internet can attempt DB access.
```

Fix:

```text
Private subnet.
Not publicly accessible.
Inbound only from app SG.
Rotate credentials.
```

Lesson:

```text
Data services should be private by default.
```

---

### Failure Story 2: HPA Overloads RDS

App scales:

```text
5 Pods -> 50 Pods
```

Hikari:

```text
20 connections each
```

Potential connections:

```text
1000
```

RDS max safe connections much lower.

Result:

```text
connection storms
timeouts
high DB CPU
```

Fix:

```text
Reduce Hikari pool.
Cap HPA.
Use RDS Proxy/PgBouncer.
Optimize queries.
```

Lesson:

```text
Autoscaling without connection budgeting is dangerous.
```

---

### Failure Story 3: Redis Failover Exposes Bad Cache Assumption

Redis fails over.

Some cache writes lost.

App assumed Redis was source of truth.

Result:

```text
missing redirects
wrong behavior
```

Fix:

```text
RDS remains source of truth.
Redis miss should rebuild from RDS.
```

Lesson:

```text
Cache is cache, not truth.
```

---

### Failure Story 4: Kafka Event Duplicates

Producer retries after timeout.

Original write actually succeeded.

Consumer receives duplicate click event.

Result:

```text
analytics double-counts
```

Fix:

```text
Use idempotent producer where appropriate.
Use event id.
Make consumer idempotent.
Deduplicate in analytics.
```

Lesson:

```text
At-least-once delivery means duplicates are normal.
```

---

### Failure Story 5: MSK Bootstrap Uses One Broker

App configured:

```text
SPRING_KAFKA_BOOTSTRAP_SERVERS=b-1:9092
```

Broker b-1 unavailable.

App cannot bootstrap.

Fix:

```text
Use multiple bootstrap brokers.
```

Lesson:

```text
Bootstrap list should include multiple brokers.
```

---

## 38. Common Mistakes

### Mistake 1: Treating managed service as magic

Wrong:

```text
RDS will fix bad SQL.
```

Correct:

```text
You still need schema, indexes, query tuning.
```

### Mistake 2: Public database/cache/Kafka

Wrong:

```text
Expose data services publicly.
```

Correct:

```text
Private subnets + security groups.
```

### Mistake 3: Huge Hikari pool

Wrong:

```text
50 connections per Pod with 50 Pods.
```

Correct:

```text
Pool size based on DB connection budget.
```

### Mistake 4: Redis as source of truth

Wrong:

```text
Only store URL mapping in Redis.
```

Correct:

```text
RDS truth, Redis cache.
```

### Mistake 5: No Redis timeout

Wrong:

```text
Redis outage hangs redirect requests.
```

Correct:

```text
Set Redis timeout and fallback strategy.
```

### Mistake 6: Kafka publish blocks redirect path too long

Wrong:

```text
User redirect waits for Kafka success.
```

Correct:

```text
Redirect first or keep Kafka async with bounded timeout.
```

### Mistake 7: One Kafka partition

Wrong:

```text
click-events has one partition.
```

Correct:

```text
Partition based on throughput and consumer parallelism needs.
```

### Mistake 8: Secrets in Docker image

Wrong:

```text
Bake DB password into image.
```

Correct:

```text
Use Secrets Manager/Kubernetes Secret/ECS secrets.
```

---

## 39. Interview-Ready Explanation

If interviewer asks:

```text
How would you deploy data infrastructure for a URL shortener on AWS?
```

Strong answer:

```text
I would keep the Spring Boot application stateless and run it on EKS or ECS behind an
ALB or Ingress. The durable source of truth would be Amazon RDS for PostgreSQL, where
short_code has a unique index and the app uses a carefully sized Hikari connection
pool. I would enable Multi-AZ for availability and use read replicas only for read
scaling or reporting, not as a replacement for Multi-AZ. For fast redirects I would
use ElastiCache Redis or Valkey with cache-aside: check cache by shortCode, fall back
to RDS on miss, then repopulate cache. Redis is not the source of truth. For click
analytics and asynchronous processing I would use Amazon MSK with topics like
click-events, partitioned by shortCode or another suitable key. Producers should be
bounded and consumers should be idempotent because duplicates are possible. All
managed services should live in private subnets with security groups allowing access
only from the app. Secrets should come from Secrets Manager or Kubernetes Secrets, not
from the Docker image. I would monitor RDS connections and latency, Redis hit ratio
and evictions, and MSK consumer lag and broker health.
```

Why this is strong:

```text
1. Clear service responsibility.
2. Correct RDS truth model.
3. Correct Redis cache model.
4. Correct Kafka event model.
5. Mentions Multi-AZ vs read replicas.
6. Mentions connection pool budgeting.
7. Mentions private networking.
8. Mentions secrets and metrics.
```

Senior one-liner:

```text
Use managed AWS data services for operational reliability, but still design application correctness, capacity, networking, and failure behavior yourself.
```

---

## 40. Senior Engineer Checklist

Before production:

```text
[ ] RDS in private subnets
[ ] RDS security group allows only app SG
[ ] RDS Multi-AZ enabled for production
[ ] RDS backups configured
[ ] RDS indexes reviewed
[ ] Hikari pool sized by max Pods
[ ] ElastiCache in private subnets
[ ] Redis timeout configured
[ ] Redis is cache, not truth
[ ] Cache miss falls back to RDS safely
[ ] Redis failover tested
[ ] MSK in private subnets
[ ] multiple bootstrap brokers configured
[ ] Kafka topics and partitions planned
[ ] producer retries/idempotence considered
[ ] consumers idempotent
[ ] DLT/retry strategy planned
[ ] secrets not baked into image
[ ] CloudWatch metrics and alerts configured
[ ] HPA max replicas respects dependency capacity
```

If these are checked, your AWS managed infrastructure design is production-shaped.

---

## 41. One-Page Cheat Sheet

```text
Core mental model:
App stateless.
RDS stores truth.
ElastiCache serves hot cache.
MSK carries events.

RDS:
source of truth
transactions
unique constraints
Multi-AZ = availability
read replica = read scaling
watch connections/latency/CPU

ElastiCache:
cache-aside
shortCode -> longUrl
primary/replica/shards
Multi-AZ failover
cache miss -> RDS
watch hit ratio/evictions/latency

MSK:
managed Kafka
topic = event log
partition = ordered shard
producer sends events
consumer group processes events
watch lag/broker health

Networking:
private subnets
security groups
no public DB/cache/Kafka
app SG -> data SG only

Spring env:
SPRING_DATASOURCE_URL
SPRING_DATA_REDIS_HOST
SPRING_KAFKA_BOOTSTRAP_SERVERS

Golden warning:
HPA app scaling multiplies DB/Redis/Kafka load.
```

---

## 42. One Picture To Remember

```text
                 AWS MANAGED DATA INFRA MENTAL MODEL

                          "Stateless app, managed state"

Internet
   |
   v
ALB / Ingress
   |
   v
+---------------------------------------+
| Spring Boot App Pods / ECS Tasks      |
| stateless                             |
+-----+------------------+--------------+
      |                  |
      |                  |
      v                  v
+------------+     +-------------------+
| ElastiCache|     | Amazon RDS        |
| Redis      |     | PostgreSQL        |
| hot cache  |     | source of truth   |
+-----+------+     +-------------------+
      |
      v
+----------------+
| Amazon MSK     |
| Kafka events   |
| click-events   |
+----------------+

Redirect:
    App -> Redis hit -> 302
    App -> Redis miss -> RDS -> Redis -> 302
    App -> MSK click event async

Create:
    App -> RDS insert
    App -> Redis warm optional
    App -> MSK url-created event

FINAL MEMORY:

RDS is truth.
Redis is speed.
MSK is decoupling.
VPC/security groups protect access.
Connection budgets protect dependencies.
