# 099_Redis_Final_Revision_CheatSheet

# MiniRedis + Redis System Design Final Revision

> Last-minute high-impact Redis interview revision notes  
> Covers MiniRedis journey + real production Redis concepts

---

# Clickable Index

- [1. Core Redis Mental Model](#1-core-redis-mental-model)
- [2. RESP Protocol](#2-resp-protocol)
- [3. Event Loop Architecture](#3-event-loop-architecture)
- [4. Core Data Structures](#4-core-data-structures)
- [5. Thread Safety](#5-thread-safety)
- [6. Persistence](#6-persistence)
- [7. Replication](#7-replication)
- [8. Distributed Systems Concepts](#8-distributed-systems-concepts)
- [9. Redis Cluster](#9-redis-cluster)
- [10. GEO / GeoHash](#10-geo--geohash)
- [11. Streams](#11-streams)
- [12. Distributed Locking](#12-distributed-locking)
- [13. Redlock](#13-redlock)
- [14. Scaling Bottlenecks](#14-scaling-bottlenecks)
- [15. Monitoring And Metrics](#15-monitoring-and-metrics)
- [16. Load Testing](#16-load-testing)
- [17. Most Important Interview Tradeoffs](#17-most-important-interview-tradeoffs)
- [18. Redis Internal Components](#18-redis-internal-components)
- [19. Real Production Use Cases](#19-real-production-use-cases)
- [20. Final Redis Interview Summary](#20-final-redis-interview-summary)

---

# 1. Core Redis Mental Model

Redis is:

```text
single-threaded
in-memory
networked
key-value datastore
```

Core philosophy:

```text
keep operations extremely fast
avoid disk in request path
```

Redis internally is:

```text
TCP server
   ->
RESP parser
   ->
command executor
   ->
in-memory engine
```

---

# Redis Strengths

```text
ultra low latency
simple data model
fast reads/writes
high throughput
rich data structures
```

---

# Redis Weaknesses

```text
memory expensive
single-thread bottlenecks
persistence tradeoffs
hot key problems
```

---

# 2. RESP Protocol

RESP means:

```text
Redis Serialization Protocol
```

Client command:

```text
SET user:1 Mohamed
```

RESP format:

```text
*3
$3
SET
$6
user:1
$7
Mohamed
```

RESP advantages:

```text
simple
binary-safe
easy parsing
stream-friendly
```

---

# RESP Types

| Type | Prefix |
|---|---|
| Simple String | + |
| Error | - |
| Integer | : |
| Bulk String | $ |
| Array | * |

---

# 3. Event Loop Architecture

Redis core architecture:

```text
single-threaded event loop
```

Mental model:

```text
one chef
many customer orders
very fast order processing
```

Flow:

```text
socket accept
   ->
read command
   ->
parse
   ->
execute
   ->
write response
```

---

# Why Single Thread?

Avoid:

```text
locks
mutex contention
context switching
```

Redis performance comes from:

```text
memory locality
simple execution
fast syscalls
```

---

# 4. Core Data Structures

Redis internally uses:

| Redis Type | Internal DS |
|---|---|
| String | SDS |
| Hash | HashTable |
| List | QuickList |
| Set | HashTable / IntSet |
| Sorted Set | SkipList + HashMap |
| Stream | Radix Tree |

---

# HashMap

Used for:

```text
O(1) key lookup
```

Example:

```java
Map<String, String>
```

---

# SkipList

Used in:

```text
Sorted Sets (ZSET)
```

Supports:

```text
sorted ranking
range queries
leaderboards
```

Complexity:

```text
O(log n)
```

---

# QuickList

Used for:

```text
Redis LIST
```

Combination of:

```text
linked list + ziplist
```

Optimized for:

```text
memory + traversal
```

---

# 5. Thread Safety

Redis mostly avoids locks by:

```text
single-thread command execution
```

But MiniRedis Java versions needed:

```java
ConcurrentHashMap
AtomicLong
synchronized
```

Important interview point:

```text
thread-safe DS != scalable architecture
```

---

# 6. Persistence

Redis persistence types:

# RDB

```text
snapshot persistence
```

Flow:

```text
fork
save memory snapshot
write dump.rdb
```

Advantages:

```text
compact
fast restart
good backups
```

Disadvantages:

```text
possible data loss between snapshots
```

---

# AOF

```text
Append Only File
```

Flow:

```text
every write appended to log
```

Advantages:

```text
better durability
```

Disadvantages:

```text
larger disk usage
slower than RDB
```

---

# Hybrid Persistence

Production often uses:

```text
RDB + AOF
```

---

# 7. Replication

Architecture:

```text
Master
   ->
Replica
```

Flow:

```text
master executes write
   ->
replicate command
   ->
replica ACK offset
```

---

# Replication Offset

Tracks:

```text
how synchronized replica is
```

Example:

```text
master offset = 100
replica offset = 95
lag = 5
```

---

# Partial Resync

Instead of:

```text
full sync
```

Redis can replay:

```text
only missing commands
```

using:

```text
replication backlog
```

---

# 8. Distributed Systems Concepts

MiniRedis covered:

```text
replication
sharding
consistent hashing
distributed lock
Redlock
GeoHash
monitoring
```

Most important mindset:

```text
everything eventually becomes distributed systems
```

---

# 9. Redis Cluster

Redis Cluster uses:

```text
16384 hash slots
```

Routing:

```text
CRC16(key) % 16384
```

Flow:

```text
key
   ->
slot
   ->
node
```

---

# MOVED Response

Wrong node contacted:

```text
MOVED 3999 127.0.0.1:7002
```

Cluster-aware client updates routing table.

---

# Cluster Goals

```text
horizontal scaling
high availability
partitioned keyspace
```

---

# 10. GEO / GeoHash

Purpose:

```text
nearby search
```

Use cases:

```text
Uber
food delivery
fleet tracking
maps
```

Flow:

```text
lat/lon
   ->
GeoHash cell
   ->
candidate filtering
   ->
distance calculation
```

---

# Real Redis GEO Commands

```text
GEOADD
GEODIST
GEOSEARCH
```

---

# GeoHash Mental Model

```text
2D map
   ->
small grid cells
```

Nearby search:

```text
same cell + neighboring cells
```

---

# 11. Streams

Redis Streams support:

```text
durable event streaming
```

Features:

```text
message IDs
consumer groups
ACKs
replay
pending entries
```

Mental model:

```text
mini Kafka inside Redis
```

---

# Stream ID

Example:

```text
1700000000-1
```

Meaning:

```text
timestamp-sequence
```

---

# Consumer Groups

Allow:

```text
multiple consumers
distributed processing
message ownership
```

---

# 12. Distributed Locking

Goal:

```text
only one client owns resource
```

Redis lock:

```text
SET lockKey token NX PX 30000
```

Meaning:

```text
NX -> only if absent
PX -> expiration
```

---

# Why TTL Needed?

Prevent:

```text
deadlock after client crash
```

---

# Why Token Needed?

Prevent:

```text
wrong client unlock
```

---

# Safe Unlock

Use:

```text
Lua script
```

Reason:

```text
check + delete must be atomic
```

---

# 13. Redlock

Redlock:

```text
distributed lock across multiple Redis nodes
```

Flow:

```text
acquire lock on majority nodes
```

Example:

```text
5 nodes
quorum = 3
```

Success condition:

```text
successCount >= quorum
AND
elapsed < TTL
```

---

# Redlock Weaknesses

Distributed timing assumptions are hard.

Problems:

```text
clock drift
GC pauses
network partition
```

---

# 14. Scaling Bottlenecks

Most common Redis bottlenecks:

# Hot Keys

One key receives huge traffic.

Fix:

```text
replication
local cache
sharding
```

---

# Big Keys

Huge values:

```text
slow serialization
high memory
network spikes
```

---

# Replication Lag

Replica behind master.

Fix:

```text
faster disk
better networking
smaller writes
```

---

# Memory Pressure

Redis is memory-heavy.

Fix:

```text
eviction
compression
tiered storage
```

---

# 15. Monitoring And Metrics

Most important metrics:

| Metric | Meaning |
|---|---|
| QPS | throughput |
| latency | response time |
| p99 | tail latency |
| hits | cache success |
| misses | cache failure |
| replication lag | replica delay |
| memory usage | RAM consumption |

---

# Hit Ratio

Formula:

```text
hits / (hits + misses)
```

High hit ratio means:

```text
good cache efficiency
```

---

# p99 Latency

Most important latency metric.

Why?

Because users feel:

```text
tail spikes
```

---

# 16. Load Testing

Tools:

```text
k6
wrk
redis-benchmark
JMeter
```

Load testing goals:

```text
throughput
latency
breaking point
resource bottlenecks
```

---

# k6 Example

```javascript
export const options = {
  vus: 100,
  duration: '30s',
};
```

Meaning:

```text
100 concurrent virtual users
```

---

# Important Performance Concepts

```text
throughput != latency
```

Higher throughput often increases latency.

---

# 17. Most Important Interview Tradeoffs

# RDB vs AOF

| RDB | AOF |
|---|---|
| faster | safer |
| compact | durable |
| possible data loss | larger files |

---

# Single Thread vs Multi Thread

| Single Thread | Multi Thread |
|---|---|
| simple | parallel |
| fewer locks | harder concurrency |
| predictable | contention |

---

# Cache Aside Pattern

Flow:

```text
GET cache
miss
   ->
DB query
   ->
populate cache
```

---

# Write Through vs Write Back

| Strategy | Meaning |
|---|---|
| Write Through | sync cache + DB |
| Write Back | async DB flush |

---

# 18. Redis Internal Components

Important internal Redis pieces:

```text
event loop
RESP parser
command table
dict/hashmap
SDS strings
allocator
AOF buffer
RDB snapshotter
replication backlog
cluster manager
```

---

# SDS (Simple Dynamic String)

Redis does NOT use plain C strings.

Uses:

```text
SDS
```

Advantages:

```text
length tracking
binary safe
efficient append
```

---

# Memory Allocator

Redis uses:

```text
jemalloc
```

Why?

```text
better fragmentation control
```

---

# 19. Real Production Use Cases

Redis commonly used for:

```text
cache
session store
rate limiter
leaderboard
pub/sub
streaming
distributed lock
geo search
feature flags
real-time analytics
```

---

# Example Architectures

# Rate Limiter

```text
API Gateway
   ->
Redis counter
```

---

# Feed Cache

```text
user timeline
   ->
Redis sorted set
```

---

# Real-Time Chat

```text
Redis pub/sub
```

---

# Job Queue

```text
Redis streams
```

---

# 20. Final Redis Interview Summary

# Most Important Redis Mental Models

```text
Redis = in-memory event-driven datastore
```

```text
Single thread avoids lock contention
```

```text
Persistence always has durability tradeoffs
```

```text
Replication gives availability
```

```text
Sharding gives horizontal scaling
```

```text
GeoHash reduces spatial search space
```

```text
Streams provide durable event processing
```

```text
Distributed locks require TTL + token
```

```text
Monitoring is mandatory in production
```

```text
Load testing reveals real bottlenecks
```

---

# Ultimate Redis Interview Flow

When explaining any Redis topic:

```text
1. Start with problem
2. Explain data structure
3. Explain flow
4. Explain complexity
5. Explain bottlenecks
6. Explain scaling
7. Explain production tradeoffs
```

This is the exact thinking expected in:

```text
Senior Backend
Staff Engineer
Distributed Systems
System Design interviews
```


---

# Redis End-to-End Request Flow Diagram

```text
Client
   |
   v
TCP Socket
   |
   v
RESP Parser
   |
   v
Command Executor
   |
   +----------------------+
   |                      |
   v                      v
In-Memory Store      Persistence Layer
(HashMap/SDS)         (RDB/AOF)
   |
   v
Response Writer
   |
   v
Client
```

---

# Redis Replication Diagram

```text
                WRITE
Client -----------------> Master Redis
                               |
                               |
                               v
                     Replication Stream
                               |
          +--------------------+--------------------+
          |                                         |
          v                                         v
     Replica-1                                Replica-2
```

---

# Redis Cluster Hash Slot Diagram

```text
               CRC16(key) % 16384
                        |
                        v

+----------------+----------------+----------------+
| Node-A         | Node-B         | Node-C         |
| Slots 0-5000   | Slots 5001-10k | Slots 10k-16k |
+----------------+----------------+----------------+
```

---

# GeoHash Mental Model Diagram

```text
+---------+---------+---------+
| Cell A  | Cell B  | Cell C  |
+---------+---------+---------+
| Cell D  | USER    | Cell F  |
+---------+---------+---------+
| Cell G  | Cell H  | Cell I  |
+---------+---------+---------+

Search:
same cell + neighboring cells
```

---

# Distributed Lock Flow Diagram

```text
Client-A
   |
   | acquire lock
   v

+----------------------+
| Redis DistributedLock|
+----------------------+
   ^
   |
Client-B blocked
```

---

# Redlock Quorum Diagram

```text
          Client
             |
   +---------+---------+
   |         |         |
   v         v         v

 Redis-1  Redis-2  Redis-3
    |         |
    v         v

 Redis-4  Redis-5


Quorum Needed:
3 out of 5
```

---

# Redis Streams Consumer Group Diagram

```text
Producer
   |
   v
Redis Stream
   |
   +----------------------+
   |                      |
   v                      v
Consumer-A           Consumer-B
```

---

# Cache Aside Pattern Diagram

```text
Application
     |
     v
GET Cache
     |
     +------ HIT ------> Return Data
     |
     +------ MISS ----->
                          DB Query
                              |
                              v
                         Populate Cache
                              |
                              v
                         Return Data
```

---

# Monitoring Pipeline Diagram

```text
Client Traffic
      |
      v
MiniRedis
      |
      +--------------------+
      |                    |
      v                    v
 Metrics Registry      Logs
      |
      v
Prometheus
      |
      v
Grafana Dashboard
```

---

# Load Testing Diagram

```text
k6 Virtual Users
        |
        v
MiniRedis Server
        |
        +----------------------+
        |                      |
        v                      v
Latency Metrics          Throughput Metrics
```

---

# Redis Persistence Diagram

```text
                WRITE
                  |
                  v
             Redis Memory
              /        \
             /          \
            v            v

      RDB Snapshot     AOF Log
```

