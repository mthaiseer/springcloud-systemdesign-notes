# 025_Distributed_Lock.md

# MiniRedis Phase 025 — Distributed Lock

## Clickable Index

- [1. Feature Purpose](#1-feature-purpose)
- [2. Previous Limitation](#2-previous-limitation)
- [3. What Changed From Previous Phase](#3-what-changed-from-previous-phase)
- [4. Architecture Diagram](#4-architecture-diagram)
- [5. Flow Diagram](#5-flow-diagram)
- [6. DSA/CP Topics Covered](#6-dsacp-topics-covered)
- [7. Complete Runnable Java Code](#7-complete-runnable-java-code)
- [8. How To Run](#8-how-to-run)
- [9. Dry Run](#9-dry-run)
- [10. Production-Grade Concepts](#10-production-grade-concepts)
- [11. Scalability Discussion](#11-scalability-discussion)
- [12. Real-World Usage Examples](#12-real-world-usage-examples)
- [13. Interview Notes](#13-interview-notes)
- [14. Next Phase](#14-next-phase)

---

# 1. Feature Purpose

Implement SET NX EX style lock.

This phase is part of a progressive MiniRedis implementation. The goal is to understand how Redis-like systems evolve from simple code into production-ready infrastructure.

---

# 2. Previous Limitation

No coordination primitive.

This limitation matters because a production cache/database needs networking, correctness, concurrency, durability, observability, and scaling behavior.

---

# 3. What Changed From Previous Phase

We add lock acquire/release with token and TTL.

Mental model:

```text
Previous phase
   |
   v
New capability
   |
   v
More Redis-like behavior
```

---

# 4. Architecture Diagram

```text
Client / Driver
      |
      v
Protocol / Command Layer
      |
      v
Execution Layer
      |
      v
Storage / Feature Engine
      |
      v
Response / Result
```

Phase-specific view:

```text
Input
  -> Validate
  -> Execute Distributed Lock
  -> Update internal state
  -> Return result
```

---

# 5. Flow Diagram

```text
Request arrives
   |
   v
Parse / validate command
   |
   v
Check current state
   |
   v
Apply operation
   |
   v
Return Redis-style response
```

---

# 6. DSA/CP Topics Covered

```text
Atomic set-if-absent, lease
```

Why this helps your DSA/CP learning:

- You see real use cases of data structures.
- You connect interview patterns to backend systems.
- You understand complexity and tradeoffs.

---

# 7. Complete Runnable Java Code

## Step-by-Step Code Logic

```text
1. Define the data structure needed for this phase.
2. Add operations around that data structure.
3. Keep command handling separate from storage logic.
4. Add a driver/main class.
5. Run small examples and inspect state transitions.
```

```java
import java.util.*;

/*
Logic before class:
HashRing maps keys to nodes with virtual nodes.
This reduces key movement when nodes are added or removed.
*/
class HashRing {
    private final TreeMap<Integer, String> ring = new TreeMap<>();

    void addNode(String node) {
        for (int i = 0; i < 5; i++) {
            ring.put(Objects.hash(node, i), node);
        }
    }

    String getNode(String key) {
        int hash = Objects.hash(key);
        Map.Entry<Integer, String> e = ring.ceilingEntry(hash);
        return e != null ? e.getValue() : ring.firstEntry().getValue();
    }
}

/*
Logic before class:
DistributedLock models SET NX EX with ownership token.
Only the lock owner can safely release it.
*/
class DistributedLock {
    private String token;
    private long expireAt;

    synchronized boolean acquire(String newToken, long ttlMillis) {
        long now = System.currentTimeMillis();
        if (token == null || now >= expireAt) {
            token = newToken;
            expireAt = now + ttlMillis;
            return true;
        }
        return false;
    }

    synchronized boolean release(String ownerToken) {
        if (Objects.equals(token, ownerToken)) {
            token = null;
            return true;
        }
        return false;
    }
}

/*
Logic before class:
Driver demonstrates sharding and distributed lock basics.
*/
public class Phase025Driver {
    public static void main(String[] args) {
        HashRing ring = new HashRing();
        ring.addNode("redis-1");
        ring.addNode("redis-2");
        ring.addNode("redis-3");

        System.out.println("user:1 -> " + ring.getNode("user:1"));
        System.out.println("order:9 -> " + ring.getNode("order:9"));

        DistributedLock lock = new DistributedLock();
        System.out.println(lock.acquire("client-A", 3000));
        System.out.println(lock.release("client-A"));
    }
}
```

---

# 8. How To Run

Simple mode:

```bash
javac Phase025Driver.java
java Phase025Driver
```

Package mode:

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out com.miniredis.phase025.Phase025Driver
```

---

# 9. Dry Run

Example:

```text
Input operation for Distributed Lock
```

Step-by-step:

```text
1. Driver sends input.
2. Feature class receives the operation.
3. Internal data structure is checked.
4. State is updated or read.
5. Output is printed.
```

State transition:

```text
Before: previous phase state
After : new Distributed Lock behavior available
```

---

# 10. Production-Grade Concepts

This phase introduces or prepares for:

- clean separation of responsibilities
- testable driver code
- predictable state transitions
- future extension without rewriting earlier phases
- Redis-like production thinking

Production Redis also considers:

- memory limits
- eviction policies
- persistence safety
- replication lag
- event-loop performance
- cluster failover
- hot keys
- monitoring and alerts

---

# 11. Scalability Discussion

Scaling path:

```text
single JVM
  -> multi-client server
  -> thread pool
  -> event loop
  -> persistence
  -> replicas
  -> sharding
  -> cluster
```

Common bottlenecks:

```text
CPU
memory
network sockets
lock contention
disk fsync
hot keys
large values
replication delay
```

---

# 12. Real-World Usage Examples

This phase connects to:

- API response cache
- session storage
- rate limiter backend
- OTP expiry
- cart expiry
- user profile cache
- leaderboard
- notification fanout
- nearest driver search
- distributed locks

---

# 13. Interview Notes

Use this structure:

```text
Requirement
  -> Data structure
  -> Operation complexity
  -> Failure mode
  -> Scaling plan
```

For FAANG/product interviews, always explain:

```text
simple design first
bottleneck next
production improvement last
```

---

# 14. Next Phase

Continue with the next numbered file in the MiniRedis folder.
