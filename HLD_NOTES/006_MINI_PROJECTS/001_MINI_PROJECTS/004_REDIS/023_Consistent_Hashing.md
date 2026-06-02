# MiniRedis Phase 23 — Consistent Hashing (Rich Version)

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Consistent Hashing Exists](#2-why-consistent-hashing-exists)
- [3. Problem With Normal Modulo Hashing](#3-problem-with-normal-modulo-hashing)
- [4. Consistent Hash Ring Mental Model](#4-consistent-hash-ring-mental-model)
- [5. Virtual Nodes Explained](#5-virtual-nodes-explained)
- [6. Deep Internal Data Structure Explanation](#6-deep-internal-data-structure-explanation)
- [7. Complete Java Code](#7-complete-java-code)
- [8. Step-by-Step Dry Run](#8-step-by-step-dry-run)
- [9. Internal Memory Visualization](#9-internal-memory-visualization)
- [10. Complexity Analysis](#10-complexity-analysis)
- [11. Real Production Use Cases](#11-real-production-use-cases)
- [12. Redis Production Internals](#12-redis-production-internals)
- [13. Failure Cases And Bottlenecks](#13-failure-cases-and-bottlenecks)
- [14. Interview Questions](#14-interview-questions)
- [15. Final Mental Model](#15-final-mental-model)

---

# 1. Goal

In this phase, we build:

```text
Consistent Hashing
```

Main objective:

```text
Distribute keys across cluster nodes
while minimizing key movement.
```

Mental model:

```text
hash ring
```

Every node gets:

```text
position on ring
```

Every key gets:

```text
hash position
```

Key assigned to:

```text
next clockwise node
```

Real-world analogy:

```text
Pizza sliced into sections.

Each server owns one section.
```

When new server added:

```text
only nearby slice changes
```

NOT entire pizza.

Production systems using this idea:

```text
Redis Cluster
Cassandra
DynamoDB
Memcached
CDN routing
distributed caches
```

---

# 2. Why Consistent Hashing Exists

Initially distributed systems used:

```text
hash(key) % number_of_servers
```

Example:

```text
hash(user:1) % 3
```

Problem appears when:

```text
server count changes
```

Example:

Before:

```text
3 servers
```

After:

```text
4 servers
```

Almost ALL keys remap.

This becomes catastrophic.

Why?

Because:

```text
cache misses explode
massive data migration
network spikes
system instability
```

Consistent hashing solves this.

Goal:

```text
minimize remapping
```

Only small portion of keys move.

Huge scalability improvement.

---

# 3. Problem With Normal Modulo Hashing

Suppose:

```text
3 servers
```

Routing:

```text
hash(key) % 3
```

Example:

```text
user:1 -> server-1
user:2 -> server-2
user:3 -> server-0
```

Now add:

```text
server-4
```

Routing changes to:

```text
hash(key) % 4
```

Result:

```text
almost every key changes server
```

Problem:

```text
full cache invalidation
```

Example:

```text
100 million keys
```

Suddenly:

```text
90 million remapped
```

System meltdown possible.

---

# 4. Consistent Hash Ring Mental Model

Instead of:

```text
linear modulo
```

Use:

```text
circular ring
```

Visualization:

```text
                [Node-A]
            /               \
      key-1                   key-2

 [Node-C]                     [Node-B]

      key-5                   key-3
            \               /
                 key-4
```

How lookup works:

```text
1. hash key
2. move clockwise
3. first node found owns key
```

Important advantage:

```text
adding/removing node
affects only neighboring region
```

NOT entire cluster.

---

# 5. Virtual Nodes Explained

Real systems avoid:

```text
one position per node
```

Why?

Because distribution becomes uneven.

Example:

```text
Node-A owns huge region
Node-B owns tiny region
```

Bad load balancing.

Solution:

```text
virtual nodes
```

One physical node appears:

```text
multiple times on ring
```

Example:

```text
redis-1#0
redis-1#1
redis-1#2
```

Benefits:

```text
better load balance
smooth scaling
less hotspotting
```

Production systems heavily use this.

---

# 6. Deep Internal Data Structure Explanation

MiniRedis implementation:

```java
TreeMap<Integer, String>
```

Purpose:

```text
maintain sorted ring positions
```

Key idea:

```text
sorted hash ring
```

Why TreeMap?

Because we need:

```text
ceiling lookup
```

Meaning:

```text
find first node clockwise
```

Core operation:

```java
ceilingEntry(hash)
```

Complexity:

| Operation | Complexity |
|---|---|
| Add node | O(v log n) |
| Lookup key | O(log n) |

Where:

```text
v = virtual nodes
n = total ring entries
```

---

# Why TreeMap Is Perfect

Because TreeMap internally uses:

```text
Red-Black Tree
```

Which maintains:

```text
sorted ordering automatically
```

Needed for:

```text
clockwise traversal
```

---

# Ring Wraparound

Very important concept.

Suppose:

```text
key hash > largest node hash
```

Then:

```text
wrap to first node
```

Like circular clock.

Example:

```text
11 PM -> next hour -> 12 AM
```

This creates:

```text
consistent circular mapping
```

---

# Production Redis Internals

Real distributed systems add:

```text
replication
slot migration
rebalancing
rack awareness
multi-region placement
```

Redis Cluster uses:

```text
16384 hash slots
```

instead of generic TreeMap ring.

Cassandra/Dynamo:

```text
real consistent hash rings
```

---

# 7. Complete Java Code

## 7.1 HashRing.java

### Logic Before Code

This class simulates:

```text
consistent hash ring
```

Core responsibilities:

```text
1. add nodes
2. create virtual nodes
3. route keys
```

Internal structure:

```java
TreeMap<Integer, String>
```

Meaning:

```text
hash position
   ->
physical node
```

```java
package com.miniredis.cluster;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * HashRing simulates consistent hashing.
 */
public class HashRing {

    /**
     * Sorted ring positions.
     *
     * key   -> hash position
     * value -> physical node
     */
    private final TreeMap<Integer, String> ring =
            new TreeMap<>();

    /**
     * Number of virtual nodes
     * per physical node.
     */
    private final int virtualNodes;

    public HashRing(int virtualNodes) {

        this.virtualNodes = virtualNodes;
    }

    /**
     * Add physical node.
     */
    public void addNode(String node) {

        /**
         * Create multiple virtual nodes.
         */
        for (int i = 0; i < virtualNodes; i++) {

            int hash =
                    Objects.hash(node, i);

            ring.put(hash, node);
        }
    }

    /**
     * Route key to node.
     */
    public String getNode(String key) {

        // --------------------------------
        // STEP 1
        // HASH KEY
        // --------------------------------

        int hash =
                Objects.hash(key);

        // --------------------------------
        // STEP 2
        // FIND CLOCKWISE NODE
        // --------------------------------

        Map.Entry<Integer, String> entry =
                ring.ceilingEntry(hash);

        // --------------------------------
        // STEP 3
        // WRAP AROUND IF NEEDED
        // --------------------------------

        if (entry == null) {

            return ring.firstEntry()
                    .getValue();
        }

        return entry.getValue();
    }
}
```

---

## 7.2 Phase023Driver.java

### Logic Before Code

This driver simulates:

```text
cluster node addition
key routing
ring lookup
```

```java
package com.miniredis.driver;

import com.miniredis.cluster.HashRing;

public class Phase023Driver {

    public static void main(String[] args) {

        /**
         * Create ring.
         */
        HashRing ring =
                new HashRing(10);

        // --------------------------------
        // ADD CLUSTER NODES
        // --------------------------------

        ring.addNode("redis-1");
        ring.addNode("redis-2");
        ring.addNode("redis-3");

        // --------------------------------
        // ROUTE KEYS
        // --------------------------------

        System.out.println(
                "user:1 -> "
                        + ring.getNode("user:1")
        );

        System.out.println(
                "order:9 -> "
                        + ring.getNode("order:9")
        );

        System.out.println(
                "payment:77 -> "
                        + ring.getNode("payment:77")
        );
    }
}
```

---

# 8. Step-by-Step Dry Run

# Step 1

Code:

```java
ring.addNode("redis-1");
```

Execution:

```text
1. create 10 virtual nodes
2. hash each vnode
3. insert into TreeMap
```

Ring state:

```text
ring
 ├── 102 -> redis-1
 ├── 301 -> redis-1
 ├── 800 -> redis-1
 ...
```

---

# Step 2

Code:

```java
ring.getNode("user:1");
```

Execution:

```text
1. hash key
2. locate clockwise vnode
3. return owner node
```

Example:

```text
hash(user:1) = 450
```

Clockwise lookup:

```text
first vnode >= 450
```

Result:

```text
redis-2
```

---

# Step 3

Node added:

```java
ring.addNode("redis-4");
```

Important:

```text
only nearby keys move
```

NOT entire cluster.

This is the core advantage.

---

# 9. Internal Memory Visualization

```text
HASH RING

100 -> redis-1
250 -> redis-2
400 -> redis-3
700 -> redis-1
900 -> redis-2
```

Key routing:

```text
user:1 hash=430
```

Clockwise search:

```text
700 -> redis-1
```

Owner:

```text
redis-1
```

---

# 10. Complexity Analysis

| Operation | Complexity | Reason |
|---|---|---|
| Add vnode | O(log n) | TreeMap insert |
| Key lookup | O(log n) | ceilingEntry search |
| Wraparound | O(1) | firstEntry |

---

# 11. Real Production Use Cases

# Redis Cluster

Distribute cache keys.

---

# Cassandra

Partition distributed data.

---

# DynamoDB

Scalable distributed partitioning.

---

# CDN Routing

Map users to edge servers.

---

# Distributed Caches

Memcached clusters.

---

# 12. Redis Production Internals

Redis Cluster uses:

```text
16384 hash slots
```

NOT generic vnode rings.

Cassandra/Dynamo:

```text
true consistent hashing
```

Production systems also support:

```text
replication
rack awareness
rebalancing
failure recovery
```

---

# 13. Failure Cases And Bottlenecks

# Problem 1 — Hot Keys

Some keys receive huge traffic.

Result:

```text
single node overloaded
```

---

# Problem 2 — Uneven Distribution

Too few virtual nodes.

Result:

```text
bad balancing
```

---

# Problem 3 — Node Failure

Server disappears.

Need:

```text
key reassignment
```

---

# Problem 4 — Massive Rebalancing

Cluster scaling too aggressively.

Result:

```text
migration storms
```

---

# 14. Interview Questions

# Q1

Why consistent hashing better than modulo hashing?

Answer:

```text
minimal key remapping
```

---

# Q2

Why virtual nodes matter?

Answer:

```text
better load balancing
```

---

# Q3

Why TreeMap used?

Answer:

```text
sorted ring lookup
```

---

# Q4

What is clockwise lookup?

Answer:

```text
first node >= key hash
```

---

# Q5

Difference between Redis Cluster and Cassandra ring?

Redis:

```text
hash slots
```

Cassandra:

```text
full consistent hashing ring
```

---

# 15. Final Mental Model

```text
Modulo hashing
   -> unstable scaling

Consistent hashing
   -> scalable distributed routing
```

Consistent hashing becomes foundation for:

```text
distributed databases
distributed caches
CDNs
microservices
large-scale infrastructure
```
