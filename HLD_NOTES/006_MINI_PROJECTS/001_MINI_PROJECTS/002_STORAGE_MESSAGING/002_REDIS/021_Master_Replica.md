# MiniRedis Phase 21 — Master Replica (Rich Version)

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Replication Exists](#2-why-replication-exists)
- [3. Single Node Problem](#3-single-node-problem)
- [4. Master Replica Architecture](#4-master-replica-architecture)
- [5. Internal Replication Flow](#5-internal-replication-flow)
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
Master Replica Replication
```

Main objective:

```text
Copy write operations from master node
to replica nodes automatically.
```

Mental model:

```text
Primary database
    ->
Secondary replicas
```

Master node:

```text
accepts writes
```

Replica node:

```text
copies data from master
```

Real-world analogy:

```text
Teacher writes notes on board.

Multiple students copy notes.
```

Teacher:

```text
master
```

Students:

```text
replicas
```

Redis commands conceptually involved:

```text
SET
GET
REPLICATION
SYNC
```

Example:

```text
Client:
SET user:1 Mohamed

Master:
stores value

Replica:
receives same SET command
```

Production systems using this idea:

```text
Redis
PostgreSQL replication
MySQL replication
Mongo replica set
Kafka followers
```

---

# 2. Why Replication Exists

Initially MiniRedis was:

```text
single node
```

Problem:

```text
single point of failure
```

If node crashes:

```text
entire cache disappears
```

Another problem:

```text
all reads hit one server
```

Result:

```text
CPU bottleneck
memory bottleneck
network bottleneck
```

Replication solves this.

Master handles:

```text
writes
```

Replicas handle:

```text
reads
```

Benefits:

```text
high availability
read scaling
backup copy
faster recovery
```

Example:

```text
1 master
3 replicas
```

Read traffic distributed across:

```text
replica-1
replica-2
replica-3
```

instead of overloading master.

---

# 3. Single Node Problem

Without replication:

```text
Client
   |
   v
Single Redis Node
```

Problems:

# Problem 1 — Crash

```text
Machine failure
```

Result:

```text
service unavailable
```

---

# Problem 2 — Heavy Read Traffic

Millions of GET requests:

```text
single CPU overloaded
```

---

# Problem 3 — Maintenance Downtime

Server restart:

```text
entire cache unavailable
```

---

# Problem 4 — Data Loss

If memory lost before persistence:

```text
all data disappears
```

Replication reduces these risks.

---

# 4. Master Replica Architecture

Architecture:

```text
                +-------------+
                |   Client    |
                +------+------+
                       |
                 WRITE |
                       v
                +-------------+
                |   MASTER    |
                +------+------+
                       |
         replicate SET |
                       |
        +--------------+--------------+
        |                             |
        v                             v
 +-------------+              +-------------+
 |  REPLICA-1  |              |  REPLICA-2  |
 +-------------+              +-------------+
```

Flow:

```text
1. client sends SET
2. master stores data
3. master forwards write
4. replicas apply same write
```

Very important:

```text
replicas usually become read-only
```

Why?

Because:

```text
master is source of truth
```

---

# 5. Internal Replication Flow

Example:

```text
SET user:1 Mohamed
```

Master execution:

```text
1. store locally
2. append replication event
3. send event to replicas
```

Replica execution:

```text
1. receive command
2. replay command
3. update local memory
```

Result:

```text
all nodes converge to same state
```

This is called:

```text
eventual consistency
```

---

# 6. Deep Internal Data Structure Explanation

MiniRedis implementation mental model:

```java
Map<String, String>
```

plus:

```text
replica node list
```

Master internally stores:

```text
connected replicas
```

Example:

```text
replicas
 ├── replica-1
 ├── replica-2
 └── replica-3
```

Write operation:

```text
SET key value
```

becomes:

```text
1. update master memory
2. iterate replicas
3. replay SET on replicas
```

Complexity:

| Operation | Complexity |
|---|---|
| Master SET | O(1) |
| Replication fanout | O(number_of_replicas) |

---

# Why Replication Cost Matters

If:

```text
100 replicas exist
```

One write becomes:

```text
101 writes
```

That becomes expensive.

Production Redis optimizes this heavily.

---

# Synchronous vs Asynchronous Replication

# Synchronous

Master waits:

```text
until replicas acknowledge
```

Pros:

```text
strong consistency
```

Cons:

```text
higher latency
```

---

# Asynchronous

Master immediately responds.

Replica sync happens later.

Pros:

```text
fast writes
```

Cons:

```text
small replication lag possible
```

Redis mainly uses:

```text
asynchronous replication
```

---

# 7. Complete Java Code

## 7.1 ReplicatedStore.java

### Logic Before Code

This class simulates:

```text
master node
replica nodes
write replication
```

Core responsibilities:

```text
1. store master data
2. maintain replicas
3. forward writes
```

```java
package com.miniredis.replication;

import java.util.*;

/**
 * ReplicatedStore simulates
 * Redis master-replica replication.
 */
public class ReplicatedStore {

    /**
     * Master data storage.
     */
    private final Map<String, String> masterStore =
            new HashMap<>();

    /**
     * Replica storage list.
     *
     * Each replica:
     * independent HashMap copy.
     */
    private final List<Map<String, String>> replicas =
            new ArrayList<>();

    /**
     * Add new replica node.
     */
    public void addReplica() {

        replicas.add(
                new HashMap<>()
        );
    }

    /**
     * SET key value
     *
     * Write into master,
     * then replicate to replicas.
     */
    public void set(String key, String value) {

        // --------------------------------
        // STEP 1
        // WRITE TO MASTER
        // --------------------------------

        masterStore.put(key, value);

        // --------------------------------
        // STEP 2
        // REPLICATE TO ALL REPLICAS
        // --------------------------------

        for (Map<String, String> replica : replicas) {

            replica.put(key, value);
        }
    }

    /**
     * Read from master.
     */
    public String getFromMaster(String key) {

        return masterStore.get(key);
    }

    /**
     * Read from replica.
     */
    public String getFromReplica(
            int replicaIndex,
            String key
    ) {

        return replicas
                .get(replicaIndex)
                .get(key);
    }
}
```

---

## 7.2 Phase021Driver.java

### Logic Before Code

This driver simulates:

```text
master write
replica synchronization
read scaling
```

```java
package com.miniredis.driver;

import com.miniredis.replication.ReplicatedStore;

public class Phase021Driver {

    public static void main(String[] args) {

        /**
         * Create replicated store.
         */
        ReplicatedStore store =
                new ReplicatedStore();

        // --------------------------------
        // ADD REPLICAS
        // --------------------------------

        store.addReplica();
        store.addReplica();

        // --------------------------------
        // CLIENT WRITE
        // --------------------------------

        store.set(
                "user:1",
                "Mohamed"
        );

        // --------------------------------
        // READ FROM MASTER
        // --------------------------------

        System.out.println(
                "master -> "
                        + store.getFromMaster("user:1")
        );

        // --------------------------------
        // READ FROM REPLICA
        // --------------------------------

        System.out.println(
                "replica-1 -> "
                        + store.getFromReplica(
                        0,
                        "user:1"
                )
        );

        System.out.println(
                "replica-2 -> "
                        + store.getFromReplica(
                        1,
                        "user:1"
                )
        );
    }
}
```

---

# 8. Step-by-Step Dry Run

# Step 1

Code:

```java
store.addReplica();
```

Meaning:

```text
new replica joins cluster
```

Memory:

```text
replicas
 └── replica-1 -> {}
```

---

# Step 2

Code:

```java
store.set(
    "user:1",
    "Mohamed"
);
```

Execution:

```text
1. write to master
2. iterate replicas
3. replay SET on replicas
```

Master memory:

```text
masterStore
 └── user:1 -> Mohamed
```

Replica memory:

```text
replica-1
 └── user:1 -> Mohamed

replica-2
 └── user:1 -> Mohamed
```

---

# Step 3

Code:

```java
store.getFromReplica(0, "user:1");
```

Execution:

```text
1. locate replica
2. lookup key
3. return value
```

Result:

```text
Mohamed
```

---

# 9. Internal Memory Visualization

```text
MASTER
 └── user:1 -> Mohamed

REPLICA-1
 └── user:1 -> Mohamed

REPLICA-2
 └── user:1 -> Mohamed
```

---

# 10. Complexity Analysis

| Operation | Complexity | Reason |
|---|---|---|
| Master SET | O(1) | HashMap write |
| Replica fanout | O(r) | iterate replicas |
| GET | O(1) | HashMap lookup |

Where:

```text
r = number of replicas
```

---

# 11. Real Production Use Cases

# Read Scaling

Heavy GET traffic distributed across replicas.

---

# High Availability

If master crashes:

```text
replica promoted
```

---

# Disaster Recovery

Replica acts as backup copy.

---

# Geo Replication

Different regions maintain copies.

Example:

```text
Europe replica
US replica
Asia replica
```

---

# 12. Redis Production Internals

Real Redis replication includes:

```text
PSYNC
Replication backlog
RDB snapshot transfer
AOF replay
Replica offset tracking
Partial resynchronization
```

MiniRedis version:

```text
simple in-memory fanout
```

Production Redis:

```text
networked replication engine
```

---

# 13. Failure Cases And Bottlenecks

# Problem 1 — Replica Lag

Replica slower than master.

Result:

```text
stale reads
```

---

# Problem 2 — Network Partition

Replica disconnected.

Result:

```text
replication delay
```

---

# Problem 3 — Split Brain

Two masters accidentally active.

Result:

```text
data inconsistency
```

Production fix:

```text
Sentinel
Raft
Leader election
```

---

# Problem 4 — Replication Storm

Too many replicas.

Result:

```text
master overloaded
```

---

# 14. Interview Questions

# Q1

Why use replicas?

Answer:

```text
high availability + read scaling
```

---

# Q2

Why asynchronous replication?

Answer:

```text
faster write latency
```

---

# Q3

What is replication lag?

Answer:

```text
replica behind master updates
```

---

# Q4

What happens if master crashes?

Answer:

```text
replica promoted to master
```

---

# Q5

Difference between Redis replication and Kafka replication?

Redis:

```text
memory replication
```

Kafka:

```text
partition log replication
```

---

# 15. Final Mental Model

```text
Single Redis
   -> fast but risky

Replicated Redis
   -> scalable and resilient
```

Replication becomes the foundation for:

```text
high availability
distributed systems
large-scale caching
fault tolerance
global infrastructure
```
