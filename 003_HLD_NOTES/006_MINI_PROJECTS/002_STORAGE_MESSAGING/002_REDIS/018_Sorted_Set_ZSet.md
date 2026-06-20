# 018_Sorted_Set_ZSet.md

# MiniRedis Phase 18 — Sorted Set ZSet

## Clickable Index

- [1. Goal](#1-goal)
- [2. What We Built Previously](#2-what-we-built-previously)
- [3. Previous Limitation](#3-previous-limitation)
- [4. What We Build In This Phase](#4-what-we-build-in-this-phase)
- [5. Why This Phase Matters](#5-why-this-phase-matters)
- [6. Architecture Diagram](#6-architecture-diagram)
- [7. File Structure](#7-file-structure)
- [8. Complete Java Code](#8-complete-java-code)
- [9. How To Run](#9-how-to-run)
- [10. Step-by-Step Dry Run](#10-step-by-step-dry-run)
- [11. Test Commands](#11-test-commands)
- [12. DSA / CP Concepts Used](#12-dsa--cp-concepts-used)
- [13. System Design Relevance](#13-system-design-relevance)
- [14. Redis Connection With This Phase](#14-redis-connection-with-this-phase)
- [15. Production-Grade Concepts](#15-production-grade-concepts)
- [16. Scalability Discussion](#16-scalability-discussion)
- [17. Interview Notes](#17-interview-notes)
- [18. Common Bugs](#18-common-bugs)
- [19. Next Step](#19-next-step)

---

# 1. Goal

In this phase, we build:

```text
Sorted Set ZSet
```

Purpose:

```text
Add score-based ranking with sorted sets.
```

This continues the MiniRedis journey from a simple parser/store into a real Redis-like backend component.

---

# 2. What We Built Previously

Earlier phases gave us:

```text
001 TCP server
002 RESP parser
003 in-memory store
```

Then each later phase adds one production capability.

Current mental model:

```text
Client command
      |
      v
Parser
      |
      v
Command object
      |
      v
Command executor
      |
      v
Redis-like internal engine
      |
      v
Response
```

---

# 3. Previous Limitation

```text
Set had unique members but no ordering or score.
```

This limitation matters because production Redis is not only a `Map`.

It also needs:

```text
correct command behavior
memory control
expiration
persistence
concurrency
replication
sharding
observability
```

---

# 4. What We Build In This Phase

We add:

```text
We add ZADD and ZRANGE using TreeMap and explain real Redis skip list concept.
```

Commands or operations covered:

```text
ZADD leaderboard 100 bob
ZRANGE leaderboard
```

---

# 5. Why This Phase Matters

This phase matters because it connects implementation to real backend systems.

Real systems need:

```text
feature correctness
clear data structures
predictable complexity
safe failure handling
production debugging
scalability path
```

MiniRedis teaches these in small increments.

---

# 6. Architecture Diagram

```text
+------------------+
| Client / Driver  |
+--------+---------+
         |
         v
+------------------+
| Parser / Command |
+--------+---------+
         |
         v
+------------------+
| Command Executor |
+--------+---------+
         |
         v
+------------------+
| Sorted Set ZSet  |
+--------+---------+
         |
         v
+------------------+
| Response         |
+------------------+
```

Phase flow:

```text
Input
  -> validate
  -> execute Sorted Set ZSet
  -> update internal state
  -> return output
```

---

# 7. File Structure

Recommended structure:

```text
MiniRedis/
└── src/
    └── main/
        └── java/
            └── com/
                └── miniredis/
                    ├── protocol/
                    ├── command/
                    ├── storage/
                    ├── server/
                    ├── persistence/
                    ├── cluster/
                    ├── metrics/
                    └── driver/
```

For this phase, keep only the needed packages.

---

# 8. Complete Java Code


## 8.1 `RedisDataStore.java`

### Logic before this class

Redis is not only string storage.

It supports multiple data structures under different keys.

This class demonstrates list, set, hash, and sorted set operations.

```java
package com.miniredis.storage;

import java.util.*;

public class RedisDataStore {
    private final Map<String, Deque<String>> lists = new HashMap<>();
    private final Map<String, Set<String>> sets = new HashMap<>();
    private final Map<String, Map<String, String>> hashes = new HashMap<>();
    private final Map<String, TreeMap<Double, List<String>>> zsets = new HashMap<>();

    public void lpush(String key, String value) {
        lists.computeIfAbsent(key, k -> new LinkedList<>()).addFirst(value);
    }

    public String lpop(String key) {
        Deque<String> list = lists.get(key);
        return list == null || list.isEmpty() ? null : list.removeFirst();
    }

    public void sadd(String key, String member) {
        sets.computeIfAbsent(key, k -> new HashSet<>()).add(member);
    }

    public boolean sismember(String key, String member) {
        return sets.getOrDefault(key, Set.of()).contains(member);
    }

    public void hset(String key, String field, String value) {
        hashes.computeIfAbsent(key, k -> new HashMap<>()).put(field, value);
    }

    public String hget(String key, String field) {
        return hashes.getOrDefault(key, Map.of()).get(field);
    }

    public void zadd(String key, double score, String member) {
        zsets.computeIfAbsent(key, k -> new TreeMap<>())
             .computeIfAbsent(score, s -> new ArrayList<>())
             .add(member);
    }

    public List<String> zrange(String key) {
        List<String> result = new ArrayList<>();

        for (List<String> members : zsets.getOrDefault(key, new TreeMap<>()).values()) {
            result.addAll(members);
        }

        return result;
    }
}
```

---

## 8.2 `Phase018Driver.java`

### Logic before this class

The driver demonstrates Redis data structure commands.

```java
package com.miniredis.driver;

import com.miniredis.storage.RedisDataStore;

public class Phase018Driver {
    public static void main(String[] args) {
        RedisDataStore store = new RedisDataStore();

        store.lpush("jobs", "job-1");
        store.lpush("jobs", "job-2");
        System.out.println("LPOP jobs = " + store.lpop("jobs"));

        store.sadd("users", "mohamed");
        System.out.println("SISMEMBER users mohamed = " + store.sismember("users", "mohamed"));

        store.hset("user:1", "name", "Mohamed");
        System.out.println("HGET user:1 name = " + store.hget("user:1", "name"));

        store.zadd("leaderboard", 100, "bob");
        store.zadd("leaderboard", 90, "alice");
        System.out.println("ZRANGE leaderboard = " + store.zrange("leaderboard"));
    }
}
```


---

# 9. How To Run

From project root:

```bash
javac -d out src/main/java/com/miniredis/**/*.java
```

Run the phase driver:

```bash
java -cp out com.miniredis.driver.Phase018Driver
```

If your shell does not expand `**`, compile individual files or use IntelliJ.

---


# 10. Step-by-Step Dry Run

Example commands:

```text
ZADD leaderboard 100 bob
ZRANGE leaderboard
```

Internal flow:

```text
1. Client/driver creates input command.
2. Parser converts raw input into Command object.
3. CommandExecutor validates command name and arguments.
4. Executor calls the correct storage/service method.
5. Data structure is updated or queried.
6. Response is returned.
```

State transition:

```text
Before:
previous phase capability only

Operation:
Sorted Set ZSet

After:
new Redis-like behavior is available
```

Visual execution:

```text
Command
  -> validate
  -> execute
  -> update internal state
  -> return response
```


---

# 11. Test Commands

Try these mental or driver-level commands:

```text
ZADD leaderboard 100 bob
ZRANGE leaderboard
```

Expected behavior:

```text
command accepted
state updated or queried
response returned
```

For server phases, test with:

```bash
telnet localhost 6379
```

or:

```bash
nc localhost 6379
```

---

# 12. DSA / CP Concepts Used

```text
Balanced tree, skip list mental model, ordered range query
```

Complexity thinking:

```text
Ask:
1. What is the core data structure?
2. What is lookup complexity?
3. What is update complexity?
4. What happens under high write/read load?
5. What is the memory cost?
```

This is exactly how DSA connects to system design.

---

# 13. System Design Relevance

This phase maps to:

```text
leaderboards, ranking feeds, priority ordering
```

System design pattern:

```text
Requirement
  -> choose data structure
  -> define operation complexity
  -> define failure behavior
  -> define scaling path
```

---

# 14. Redis Connection With This Phase

Real Redis uses the same idea at production scale.

MiniRedis version:

```text
simple Java implementation
```

Real Redis version:

```text
optimized C implementation
event loop
carefully tuned memory layout
persistence configuration
replication protocol
cluster routing
```

This phase gives the mental model before optimization.

---

# 15. Production-Grade Concepts

Production concerns:

```text
correctness
validation
memory usage
latency
thread safety
durability
observability
failure recovery
```

Questions to ask:

```text
What if process crashes?
What if key is hot?
What if memory is full?
What if many clients connect?
What if disk is slow?
What if replica lags?
```

---

# 16. Scalability Discussion

Single-node path:

```text
single JVM
  -> thread-safe store
  -> TTL cleanup
  -> persistence
  -> metrics
```

Distributed path:

```text
replication
  -> sharding
  -> consistent hashing
  -> cluster client
  -> failover
```

Bottlenecks to watch:

```text
CPU
GC
memory
network
lock contention
disk fsync
hot keys
large values
replication backlog
```

---

# 17. Interview Notes

Good explanation structure:

```text
1. Start with the simplest design.
2. Explain the data structure.
3. Give operation complexity.
4. Discuss failure cases.
5. Add production improvements.
6. Explain scaling path.
```

Possible follow-ups:

```text
How do you make it thread-safe?
How do you persist it?
How do you evict keys?
How do you shard it?
How do you recover after crash?
How do you monitor it?
```

---

# 18. Common Bugs

## Bug 1 — Wrong argument count

Cause:

```text
command validation missing
```

Fix:

```text
validate args before executing
```

## Bug 2 — Shared mutable state bug

Cause:

```text
multiple threads update the same data
```

Fix:

```text
ConcurrentHashMap, locks, or atomic operations
```

## Bug 3 — Memory leak

Cause:

```text
expired or unused keys remain forever
```

Fix:

```text
TTL cleanup and eviction
```

## Bug 4 — Inconsistent recovery

Cause:

```text
write applied to memory but not persisted
```

Fix:

```text
AOF/WAL ordering and fsync policy
```

---

# 19. Next Step

Next phase:

```text
019
```

Continue the MiniRedis roadmap until the final production architecture.
