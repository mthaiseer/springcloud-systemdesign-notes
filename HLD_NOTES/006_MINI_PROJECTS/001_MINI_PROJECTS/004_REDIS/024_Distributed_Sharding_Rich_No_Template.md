# MiniRedis Phase 24 — Distributed Sharding (Rich Version)

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Distributed Sharding Exists](#2-why-distributed-sharding-exists)
- [3. Single Node Storage Problem](#3-single-node-storage-problem)
- [4. Sharding Mental Model](#4-sharding-mental-model)
- [5. Sharding vs Replication](#5-sharding-vs-replication)
- [6. Internal Routing Flow](#6-internal-routing-flow)
- [7. Deep Internal Data Structure Explanation](#7-deep-internal-data-structure-explanation)
- [8. Complete Java Code](#8-complete-java-code)
- [9. Step-by-Step Dry Run](#9-step-by-step-dry-run)
- [10. Internal Memory Visualization](#10-internal-memory-visualization)
- [11. Complexity Analysis](#11-complexity-analysis)
- [12. Real Production Use Cases](#12-real-production-use-cases)
- [13. Redis Production Internals](#13-redis-production-internals)
- [14. Failure Cases And Bottlenecks](#14-failure-cases-and-bottlenecks)
- [15. Interview Questions](#15-interview-questions)
- [16. Final Mental Model](#16-final-mental-model)

---

# 1. Goal

In this phase, we build:

```text
Distributed Sharding
```

Main objective:

```text
Route each Redis key to the correct shard.
```

Mental model:

```text
many Redis nodes
one logical Redis cluster
```

Client thinks:

```text
SET user:1 Mohamed
```

But internally MiniRedis decides:

```text
user:1 belongs to shard-2
```

Real-world analogy:

```text
A large library splits books across multiple rooms.

Book category decides which room stores it.
```

In Redis terms:

```text
key decides shard
```

This phase connects directly to:

```text
horizontal scaling
partition routing
cluster clients
distributed cache design
```

---

# 2. Why Distributed Sharding Exists

A single Redis node has limits:

```text
limited memory
limited CPU
limited network bandwidth
limited write throughput
```

If one node stores everything:

```text
all keys
all reads
all writes
all memory
```

then eventually it becomes overloaded.

Sharding solves this by splitting data:

```text
keyspace
   -> shard-1
   -> shard-2
   -> shard-3
```

Example:

```text
user:1    -> shard-2
order:9   -> shard-1
cart:77   -> shard-3
payment:5 -> shard-2
```

Benefits:

```text
more memory capacity
higher throughput
parallel reads
parallel writes
smaller failure blast radius
```

This is how large systems scale:

```text
not one huge machine
many smaller machines
```

---

# 3. Single Node Storage Problem

Without sharding:

```text
Client
   |
   v
Single Redis Node
```

Problems:

# Problem 1 — Memory Limit

One Redis node may have:

```text
64 GB memory
```

But application needs:

```text
500 GB cache
```

Single node cannot hold it.

---

# Problem 2 — CPU Bottleneck

Millions of commands per second:

```text
single event loop overloaded
```

---

# Problem 3 — Network Bottleneck

All traffic goes to one node.

Result:

```text
latency increases
```

---

# Problem 4 — Failure Impact

If node dies:

```text
entire cache unavailable
```

Sharding reduces the load per node.

---

# 4. Sharding Mental Model

Sharding means:

```text
split data by key
```

Architecture:

```text
                +----------------+
                | Cluster Client |
                +--------+-------+
                         |
          +--------------+--------------+
          |              |              |
          v              v              v
     +---------+    +---------+    +---------+
     | Shard-1 |    | Shard-2 |    | Shard-3 |
     +---------+    +---------+    +---------+
```

Routing rule:

```text
hash(key) -> shard
```

Example:

```text
SET user:1 Mohamed
```

Flow:

```text
1. client hashes user:1
2. client finds owner shard
3. command sent to that shard
4. shard stores value locally
```

Important:

```text
each shard stores only part of the data
```

---

# 5. Sharding vs Replication

These two are different.

# Replication

```text
same data copied to multiple nodes
```

Purpose:

```text
high availability
read scaling
```

Example:

```text
master
   -> replica-1
   -> replica-2
```

---

# Sharding

```text
different data split across nodes
```

Purpose:

```text
horizontal scaling
larger capacity
```

Example:

```text
shard-1 stores users A-H
shard-2 stores users I-P
shard-3 stores users Q-Z
```

---

# Together

Production systems use both:

```text
Shard-1
   -> replica-1A
   -> replica-1B

Shard-2
   -> replica-2A
   -> replica-2B
```

Meaning:

```text
sharding for scale
replication for availability
```

---

# 6. Internal Routing Flow

Command:

```text
SET user:1 Mohamed
```

Routing steps:

```text
1. extract key = user:1
2. pass key to hash ring
3. ring returns shard ID
4. lookup shard object
5. execute SET on that shard
```

Read command:

```text
GET user:1
```

Must go to:

```text
same shard
```

Otherwise:

```text
key not found
```

This is why deterministic routing is critical.

Same key must always route to same shard.

---

# 7. Deep Internal Data Structure Explanation

MiniRedis uses three main internal parts:

```text
1. HashRing
2. RedisShard
3. ClusterClient
```

---

# 7.1 HashRing

Purpose:

```text
decide which shard owns a key
```

Internal DS:

```java
TreeMap<Integer, String>
```

Meaning:

```text
hash position -> shard name
```

Why TreeMap?

Because we need:

```text
clockwise lookup
```

Operation:

```java
ceilingEntry(hash)
```

Complexity:

```text
O(log n)
```

---

# 7.2 RedisShard

Purpose:

```text
store actual key-value data
```

Internal DS:

```java
Map<String, String>
```

Example:

```text
shard-1
 └── order:9 -> paid

shard-2
 └── user:1 -> Mohamed
```

---

# 7.3 ClusterClient

Purpose:

```text
hide distributed routing from application
```

Application calls:

```java
client.set("user:1", "Mohamed");
```

ClusterClient internally does:

```text
find shard
execute command
return response
```

This is exactly how real cluster clients work.

---

# 8. Complete Java Code

## 8.1 HashRing.java

### Logic Before Code

This class provides:

```text
key -> shard mapping
```

It uses consistent hashing so that node changes do not remap every key.

```java
package com.miniredis.cluster;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * HashRing maps keys to shard names using consistent hashing.
 */
public class HashRing {

    /**
     * Sorted hash ring.
     *
     * key   -> hash position
     * value -> shard name
     */
    private final TreeMap<Integer, String> ring =
            new TreeMap<>();

    /**
     * Number of virtual nodes per shard.
     */
    private final int virtualNodes;

    public HashRing(int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    /**
     * Add a shard to the ring.
     */
    public void addShard(String shardName) {

        for (int i = 0; i < virtualNodes; i++) {

            int hash =
                    Objects.hash(shardName, i);

            ring.put(hash, shardName);
        }
    }

    /**
     * Return shard owner for key.
     */
    public String getShard(String key) {

        int keyHash =
                Objects.hash(key);

        Map.Entry<Integer, String> entry =
                ring.ceilingEntry(keyHash);

        if (entry == null) {
            return ring.firstEntry().getValue();
        }

        return entry.getValue();
    }
}
```

---

## 8.2 RedisShard.java

### Logic Before Code

Each shard is an independent Redis-like key-value store.

```java
package com.miniredis.cluster;

import java.util.HashMap;
import java.util.Map;

/**
 * RedisShard stores only the keys assigned to this shard.
 */
public class RedisShard {

    private final String name;

    /**
     * Local shard storage.
     */
    private final Map<String, String> data =
            new HashMap<>();

    public RedisShard(String name) {
        this.name = name;
    }

    public void set(String key, String value) {

        data.put(key, value);
    }

    public String get(String key) {

        return data.get(key);
    }

    public String getName() {
        return name;
    }

    public Map<String, String> snapshot() {
        return new HashMap<>(data);
    }
}
```

---

## 8.3 ClusterClient.java

### Logic Before Code

ClusterClient is the routing layer.

It knows:

```text
which key goes to which shard
```

Application does not talk to shards directly.

```java
package com.miniredis.cluster;

import java.util.HashMap;
import java.util.Map;

/**
 * ClusterClient routes Redis commands
 * to the correct shard.
 */
public class ClusterClient {

    private final HashRing hashRing;

    /**
     * shard name -> shard object
     */
    private final Map<String, RedisShard> shards =
            new HashMap<>();

    public ClusterClient(HashRing hashRing) {
        this.hashRing = hashRing;
    }

    /**
     * Register a shard.
     */
    public void addShard(RedisShard shard) {

        shards.put(
                shard.getName(),
                shard
        );

        hashRing.addShard(
                shard.getName()
        );
    }

    /**
     * SET key value
     *
     * 1. find shard
     * 2. route command
     * 3. write locally on shard
     */
    public void set(String key, String value) {

        String shardName =
                hashRing.getShard(key);

        RedisShard shard =
                shards.get(shardName);

        shard.set(key, value);

        System.out.println(
                "SET "
                        + key
                        + " routed to "
                        + shardName
        );
    }

    /**
     * GET key
     *
     * Must route to same shard as SET.
     */
    public String get(String key) {

        String shardName =
                hashRing.getShard(key);

        RedisShard shard =
                shards.get(shardName);

        System.out.println(
                "GET "
                        + key
                        + " routed to "
                        + shardName
        );

        return shard.get(key);
    }

    /**
     * Debug helper.
     */
    public Map<String, Map<String, String>> clusterSnapshot() {

        Map<String, Map<String, String>> result =
                new HashMap<>();

        for (Map.Entry<String, RedisShard> entry : shards.entrySet()) {

            result.put(
                    entry.getKey(),
                    entry.getValue().snapshot()
            );
        }

        return result;
    }
}
```

---

## 8.4 Phase024Driver.java

### Logic Before Code

This driver demonstrates:

```text
distributed sharding
cluster routing
same-key deterministic lookup
```

```java
package com.miniredis.driver;

import com.miniredis.cluster.ClusterClient;
import com.miniredis.cluster.HashRing;
import com.miniredis.cluster.RedisShard;

public class Phase024Driver {

    public static void main(String[] args) {

        HashRing ring =
                new HashRing(10);

        ClusterClient client =
                new ClusterClient(ring);

        /**
         * Register three shards.
         */
        client.addShard(new RedisShard("shard-1"));
        client.addShard(new RedisShard("shard-2"));
        client.addShard(new RedisShard("shard-3"));

        /**
         * Write keys.
         */
        client.set("user:1", "Mohamed");
        client.set("order:9", "PAID");
        client.set("cart:77", "3-items");

        /**
         * Read keys.
         */
        System.out.println(
                "GET user:1 = "
                        + client.get("user:1")
        );

        System.out.println(
                "GET order:9 = "
                        + client.get("order:9")
        );

        /**
         * Show internal cluster memory.
         */
        System.out.println(
                client.clusterSnapshot()
        );
    }
}
```

---

# 9. Step-by-Step Dry Run

# Step 1 — Create Cluster

Code:

```java
client.addShard(new RedisShard("shard-1"));
client.addShard(new RedisShard("shard-2"));
client.addShard(new RedisShard("shard-3"));
```

Execution:

```text
1. create shard object
2. store it in ClusterClient
3. add shard into HashRing
4. create virtual nodes
```

Ring memory:

```text
ring
 ├── 102 -> shard-1
 ├── 301 -> shard-2
 ├── 777 -> shard-3
```

---

# Step 2 — SET user:1

Code:

```java
client.set("user:1", "Mohamed");
```

Execution:

```text
1. hash user:1
2. find owner shard
3. send SET to that shard
4. shard stores key
```

Example routing:

```text
user:1 -> shard-2
```

Shard memory:

```text
shard-2
 └── user:1 -> Mohamed
```

---

# Step 3 — SET order:9

Code:

```java
client.set("order:9", "PAID");
```

Example routing:

```text
order:9 -> shard-1
```

Shard memory:

```text
shard-1
 └── order:9 -> PAID
```

---

# Step 4 — GET user:1

Code:

```java
client.get("user:1");
```

Execution:

```text
1. hash same key
2. same shard selected
3. read from shard-2
4. return Mohamed
```

Result:

```text
Mohamed
```

Important:

```text
same key must always route to same shard
```

---

# 10. Internal Memory Visualization

```text
CLUSTER

shard-1
 └── order:9 -> PAID

shard-2
 └── user:1 -> Mohamed

shard-3
 └── cart:77 -> 3-items
```

Client view:

```text
one logical Redis cluster
```

Internal reality:

```text
many independent shards
```

---

# 11. Complexity Analysis

| Operation | Complexity | Reason |
|---|---|---|
| Find shard | O(log n) | TreeMap ceilingEntry |
| SET | O(log n) + O(1) | route + HashMap put |
| GET | O(log n) + O(1) | route + HashMap get |
| Add shard | O(v log n) | add virtual nodes |

Where:

```text
n = total virtual nodes
v = virtual nodes per shard
```

---

# 12. Real Production Use Cases

# Distributed Cache

Millions of keys spread across many Redis nodes.

---

# User Session Storage

```text
session:user:1 -> shard-2
```

---

# Product Catalog Cache

```text
product:999 -> shard-1
```

---

# API Rate Limiting

```text
rate:user:77 -> shard-3
```

---

# Feed Cache

```text
feed:user:5 -> shard-2
```

---

# 13. Redis Production Internals

Redis Cluster uses:

```text
16384 hash slots
```

Flow:

```text
key -> slot -> node
```

Example:

```text
CRC16(key) % 16384
```

If client contacts wrong node:

```text
Redis returns MOVED response
```

Example:

```text
MOVED 3999 127.0.0.1:7002
```

Cluster-aware client updates routing table.

MiniRedis version:

```text
HashRing + ClusterClient
```

Real Redis version:

```text
slot map + cluster-aware protocol
```

---

# 14. Failure Cases And Bottlenecks

# Problem 1 — Hot Key

One key receives massive traffic.

Example:

```text
celebrity:user:1
```

Result:

```text
one shard overloaded
```

Fix:

```text
key splitting
local cache
replication
request coalescing
```

---

# Problem 2 — Cross-Shard Operation

Example:

```text
MGET user:1 order:9
```

Keys may live on different shards.

Problem:

```text
multi-shard transaction complexity
```

---

# Problem 3 — Shard Failure

If shard dies:

```text
all keys on that shard unavailable
```

Fix:

```text
replicas per shard
failover
```

---

# Problem 4 — Rebalancing

Adding/removing shards moves keys.

Problem:

```text
migration traffic
```

Fix:

```text
background migration
rate limiting
slot migration
```

---

# 15. Interview Questions

# Q1

Why sharding?

Answer:

```text
horizontal scaling of memory and throughput
```

---

# Q2

Difference between sharding and replication?

Answer:

```text
sharding splits data
replication copies data
```

---

# Q3

Why client-side routing?

Answer:

```text
avoid proxy bottleneck
```

---

# Q4

What is a hot key?

Answer:

```text
one key overloaded despite sharding
```

---

# Q5

How does Redis Cluster route keys?

Answer:

```text
CRC16(key) % 16384 slot
slot maps to node
```

---

# 16. Final Mental Model

```text
Consistent hashing
   -> decides ownership

Distributed sharding
   -> routes commands to owners
```

Sharding is the foundation for:

```text
horizontal scaling
large distributed caches
multi-node databases
Redis Cluster
Cassandra
Dynamo-style systems
```
