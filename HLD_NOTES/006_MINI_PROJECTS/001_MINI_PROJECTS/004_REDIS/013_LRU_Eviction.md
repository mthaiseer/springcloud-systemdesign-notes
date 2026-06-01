# 013_LRU_Eviction.md

# MiniRedis Phase 13 — LRU Eviction

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
LRU Eviction
```

Purpose:

```text
Add least-recently-used eviction when memory capacity is reached.
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
Memory could grow forever and eventually cause JVM OOM.
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
We add LRU policy using LinkedHashMap access order.
```

Commands or operations covered:

```text
SET
GET
evict least recently used
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
| LRU Eviction     |
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
  -> execute LRU Eviction
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


## 8.1 `EvictionCache.java`

### Logic Summary before this class

`EvictionCache` is the core in-memory cache component.

It has one main job:

```text
Keep only a limited number of keys in memory.
When capacity is full, remove the key that is least useful.
```

For LRU:

```text
Least Recently Used = key that was not accessed for the longest time.
```

Implementation idea:

```text
HashMap gives O(1) lookup.
LinkedHashMap with accessOrder=true keeps keys ordered by recent access.
First key = least recently used.
Last key = most recently used.
```

Why this matters in MiniRedis:

```text
Without eviction, keys grow forever and JVM memory can crash.
With eviction, Redis can keep serving requests within memory limit.
```

```java
package com.miniredis.eviction;

import java.util.*;

public class EvictionCache {

    // Maximum number of keys allowed in memory.
    // Example: capacity = 2 means cache can store only 2 keys.
    private final int capacity;

    // LinkedHashMap gives HashMap + linked-list ordering.
    // accessOrder = true means:
    // - whenever get(key) happens, that key moves to the end
    // - whenever put(key) happens, that key also becomes recently used
    // So iteration order becomes:
    // least recently used -> most recently used
    private final LinkedHashMap<String, String> lru;

    // Extra map to count how many times each key is accessed.
    // This is used only to demonstrate LFU also.
    // LFU = Least Frequently Used.
    private final Map<String, Integer> frequency = new HashMap<>();

    public EvictionCache(int capacity) {
        // Store the maximum allowed cache size.
        this.capacity = capacity;

        // Constructor arguments:
        // 16     = initial bucket size
        // 0.75f  = load factor
        // true   = access-order mode
        //
        // access-order mode is the key idea for LRU.
        this.lru = new LinkedHashMap<>(16, 0.75f, true);
    }

    public void set(String key, String value) {
        // Case 1:
        // This is a NEW key and cache is already full.
        // We must remove one old key before inserting the new one.
        if (!lru.containsKey(key) && lru.size() >= capacity) {
            evictLru();
        }

        // Insert or update the key.
        // Because LinkedHashMap uses accessOrder=true,
        // this key becomes the most recently used key.
        lru.put(key, value);

        // Increase access frequency.
        // For SET, we count it as one access/write touch.
        frequency.put(key, frequency.getOrDefault(key, 0) + 1);
    }

    public String get(String key) {
        // lru.get(key) does two things:
        // 1. returns the value
        // 2. moves the key to the end because accessOrder=true
        //
        // End of LinkedHashMap = most recently used.
        String value = lru.get(key);

        // If key exists, increase frequency count.
        if (value != null) {
            frequency.put(key, frequency.getOrDefault(key, 0) + 1);
        }

        // If key does not exist, returns null.
        return value;
    }

    private void evictLru() {
        // In access-order LinkedHashMap:
        // first key = least recently used key.
        String victim = lru.keySet().iterator().next();

        // Remove victim from cache data.
        lru.remove(victim);

        // Remove victim from frequency tracking also.
        frequency.remove(victim);

        // Print so we can see eviction during dry run.
        System.out.println("LRU evicted: " + victim);
    }

    public void evictLfu() {
        // victim will store the least frequently used key.
        String victim = null;

        // Start with infinity so any real frequency is smaller.
        int minFreq = Integer.MAX_VALUE;

        // Scan all frequency entries.
        // This is O(n), unlike LRU which is O(1) using LinkedHashMap ordering.
        for (Map.Entry<String, Integer> entry : frequency.entrySet()) {
            if (entry.getValue() < minFreq) {
                minFreq = entry.getValue();
                victim = entry.getKey();
            }
        }

        // If we found a key, remove it from both structures.
        if (victim != null) {
            lru.remove(victim);
            frequency.remove(victim);
            System.out.println("LFU evicted: " + victim);
        }
    }

    public void printState() {
        // Prints current cache order.
        // Important:
        // order is least recently used -> most recently used.
        System.out.println("cache=" + lru);

        // Prints access count of each key.
        System.out.println("freq=" + frequency);
    }
}
```

---

## 8.2 `Phase013Driver.java`

### Logic Summary before this class

`Phase013Driver` is a small demo program.

It does not implement Redis logic itself.

Its job is to prove that `EvictionCache` works correctly.

Dry-run purpose:

```text
1. Create cache with capacity = 2.
2. Insert a and b.
3. Access a, so a becomes recently used.
4. Insert c.
5. Since cache is full, b is evicted because b is least recently used.
```

Expected final LRU state:

```text
a and c remain in cache.
b is removed.
```

```java
package com.miniredis.driver;

import com.miniredis.eviction.EvictionCache;

public class Phase013Driver {
    public static void main(String[] args) {

        // Create cache with capacity 2.
        // So only 2 keys can stay in memory at a time.
        EvictionCache cache = new EvictionCache(2);

        // Insert key a.
        // Cache order: a
        cache.set("a", "1");

        // Insert key b.
        // Cache order: a -> b
        // a is least recently used, b is most recently used.
        cache.set("b", "2");

        // Access key a.
        // Because a is accessed now, it moves to the end.
        // Cache order becomes: b -> a
        // b is now least recently used.
        cache.get("a");

        // Insert key c.
        // Cache is full, so remove least recently used key.
        // Current LRU key = b.
        // b is evicted, c is inserted.
        // Cache order becomes: a -> c
        cache.set("c", "3");

        // Print current cache and frequency maps.
        cache.printState();

        // Demonstrate LFU separately.
        // It removes the key with smallest access count.
        cache.evictLfu();

        // Print final state after LFU eviction.
        cache.printState();
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
java -cp out com.miniredis.driver.Phase013Driver
```

If your shell does not expand `**`, compile individual files or use IntelliJ.

---


# 10. Step-by-Step Dry Run

Cache capacity:

```text
capacity = 2
```

Important rule:

```text
Left side  = least recently used
Right side = most recently used
```

---

## Step 1 — SET a 1

```java
cache.set("a", "1");
```

Memory state:

```text
LRU order:
[a]

frequency:
a -> 1
```

Explanation:

```text
a is inserted.
Only one key exists.
a is both least recent and most recent.
```

---

## Step 2 — SET b 2

```java
cache.set("b", "2");
```

Memory state:

```text
LRU order:
[a] -> [b]

frequency:
a -> 1
b -> 1
```

Explanation:

```text
b is newly inserted.
b becomes most recently used.
a becomes least recently used.
```

---

## Step 3 — GET a

```java
cache.get("a");
```

Before GET:

```text
[a] -> [b]
```

After GET:

```text
[b] -> [a]
```

Frequency:

```text
a -> 2
b -> 1
```

Explanation:

```text
Accessing a makes a recently used.
LinkedHashMap automatically moves a to the end.
Now b is least recently used.
```

---

## Step 4 — SET c 3

```java
cache.set("c", "3");
```

Before insert:

```text
Cache full because capacity = 2

[b] -> [a]
```

Need to insert:

```text
c
```

Eviction decision:

```text
First key = b
So b is least recently used
Evict b
```

After eviction and insert:

```text
[a] -> [c]
```

Frequency:

```text
a -> 2
c -> 1
```

---

## Diagrammatic Flow

```text
Start:
[]

SET a:
[a]

SET b:
[a] -> [b]
 LRU    MRU

GET a:
[b] -> [a]
 LRU    MRU

SET c:
Need space
Evict b

[a] -> [c]
 LRU    MRU
```

---

## Why This Works

```text
LinkedHashMap with accessOrder=true keeps access order automatically.

get(key) moves key to the end.
put(key,value) moves key to the end.
iterator().next() gives the first key.
first key = least recently used.
```

So LRU eviction becomes simple:

```text
If cache is full:
    remove first key
Then insert new key
```

Complexity:

```text
GET      -> O(1)
SET      -> O(1)
EvictLRU -> O(1)
```


---

# 11. Test Commands

Try these mental or driver-level commands:

```text
SET
GET
evict least recently used
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
HashMap + doubly linked list, recency tracking
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
cache memory management, CDN cache, API response cache
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
014
```

Continue the MiniRedis roadmap until the final production architecture.
