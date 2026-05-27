# 017_Hash_Data_Type.md

# MiniRedis Phase 017 — Hash Data Type

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

Implement Redis hash/map commands.

This phase is part of a progressive MiniRedis implementation. The goal is to understand how Redis-like systems evolve from simple code into production-ready infrastructure.

---

# 2. Previous Limitation

No nested field-value storage.

This limitation matters because a production cache/database needs networking, correctness, concurrency, durability, observability, and scaling behavior.

---

# 3. What Changed From Previous Phase

We add HSET, HGET, HDEL, HGETALL.

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
  -> Execute Hash Data Type
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
Nested HashMap
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
RedisDataStore demonstrates Redis collection types:
List, Set, Hash, and Sorted Set.
*/
class RedisDataStore {
    private final Map<String, Deque<String>> lists = new HashMap<>();
    private final Map<String, Set<String>> sets = new HashMap<>();
    private final Map<String, Map<String, String>> hashes = new HashMap<>();
    private final Map<String, TreeMap<Double, String>> zsets = new HashMap<>();

    void lpush(String key, String value) {
        lists.computeIfAbsent(key, k -> new LinkedList<>()).addFirst(value);
    }

    String lpop(String key) {
        Deque<String> q = lists.get(key);
        return q == null || q.isEmpty() ? null : q.removeFirst();
    }

    void sadd(String key, String member) {
        sets.computeIfAbsent(key, k -> new HashSet<>()).add(member);
    }

    boolean sismember(String key, String member) {
        return sets.getOrDefault(key, Set.of()).contains(member);
    }

    void hset(String key, String field, String value) {
        hashes.computeIfAbsent(key, k -> new HashMap<>()).put(field, value);
    }

    String hget(String key, String field) {
        return hashes.getOrDefault(key, Map.of()).get(field);
    }

    void zadd(String key, double score, String member) {
        zsets.computeIfAbsent(key, k -> new TreeMap<>()).put(score, member);
    }

    List<String> zrange(String key) {
        return new ArrayList<>(zsets.getOrDefault(key, new TreeMap<>()).values());
    }
}

/*
Logic before class:
Driver shows Redis data structures in action.
*/
public class Phase017Driver {
    public static void main(String[] args) {
        RedisDataStore store = new RedisDataStore();
        store.lpush("queue", "job1");
        store.lpush("queue", "job2");
        System.out.println(store.lpop("queue"));

        store.sadd("users", "mohamed");
        System.out.println(store.sismember("users", "mohamed"));

        store.hset("user:1", "name", "Mohamed");
        System.out.println(store.hget("user:1", "name"));

        store.zadd("leaderboard", 99, "alice");
        store.zadd("leaderboard", 100, "bob");
        System.out.println(store.zrange("leaderboard"));
    }
}
```

---

# 8. How To Run

Simple mode:

```bash
javac Phase017Driver.java
java Phase017Driver
```

Package mode:

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out com.miniredis.phase017.Phase017Driver
```

---

# 9. Dry Run

Example:

```text
Input operation for Hash Data Type
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
After : new Hash Data Type behavior available
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
