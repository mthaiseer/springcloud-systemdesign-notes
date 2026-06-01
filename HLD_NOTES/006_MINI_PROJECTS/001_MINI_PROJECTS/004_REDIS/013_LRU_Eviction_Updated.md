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

### Logic summary before this class

This class is the core LRU cache engine.

It solves this problem:

```text
Redis memory is limited.
If cache is full and a new key comes,
we must remove one old key.
```

LRU means:

```text
Least Recently Used
```

So the key that was not touched for the longest time is removed first.

This implementation uses:

```text
LinkedHashMap with accessOrder = true
```

That means Java automatically keeps entries ordered by recent access:

```text
least recently used  --->  most recently used
left side            --->  right side
```

Example:

```text
capacity = 2

SET a 1
SET b 2

LRU order:
a -> b

GET a

LRU order:
b -> a

SET c 3

b is removed because b is least recently used.
```

Important mental model:

```text
HashMap gives O(1) lookup.
Linked list order gives O(1) recency movement.
LinkedHashMap combines both.
```

```java
package com.miniredis.eviction;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class EvictionCache {

    // Maximum number of keys this cache can hold.
    // Example:
    // capacity = 2 means only 2 keys can stay in memory.
    private final int capacity;

    // LinkedHashMap is used instead of normal HashMap because it can remember order.
    //
    // Constructor used later:
    // new LinkedHashMap<>(16, 0.75f, true)
    //
    // The last argument "true" means access-order mode.
    //
    // access-order means:
    // - whenever get(key) happens, that key moves to the most-recent position
    // - whenever put(key, value) happens, that key also becomes most-recent
    //
    // Internal order:
    // left  = least recently used
    // right = most recently used
    private final LinkedHashMap<String, String> lru;

    // This map tracks how many times each key is accessed.
    //
    // It is used only to demonstrate LFU also.
    // Redis supports many eviction policies, not only LRU.
    private final Map<String, Integer> frequency = new HashMap<>();

    public EvictionCache(int capacity) {

        // Store max allowed keys.
        this.capacity = capacity;

        // initialCapacity = 16
        // loadFactor = 0.75f
        // accessOrder = true
        //
        // accessOrder=true is the key idea of LRU here.
        this.lru = new LinkedHashMap<>(16, 0.75f, true);
    }

    public void set(String key, String value) {

        // Case 1:
        // Key is new AND cache is already full.
        //
        // Example:
        // capacity = 2
        // current keys = [a, b]
        // SET c 3
        //
        // Since c is new and size is already 2,
        // we must evict one key before inserting c.
        if (!lru.containsKey(key) && lru.size() >= capacity) {
            evictLru();
        }

        // Insert or update the key.
        //
        // If key already exists:
        // - value is updated
        // - key becomes most recently used
        //
        // If key is new:
        // - key is added at most-recent position
        lru.put(key, value);

        // Increase access frequency.
        //
        // SET also counts as touching the key.
        frequency.put(key, frequency.getOrDefault(key, 0) + 1);
    }

    public String get(String key) {

        // LinkedHashMap accessOrder=true behavior:
        //
        // If key exists:
        // - return value
        // - move key to most-recent position internally
        //
        // If key does not exist:
        // - return null
        // - order does not change
        String value = lru.get(key);

        // If key was found, increase frequency counter.
        if (value != null) {
            frequency.put(key, frequency.getOrDefault(key, 0) + 1);
        }

        return value;
    }

    private void evictLru() {

        // In access-order LinkedHashMap:
        //
        // first key  = least recently used
        // last key   = most recently used
        //
        // keySet().iterator().next() gives the first key.
        String victim = lru.keySet().iterator().next();

        // Remove victim from actual cache.
        lru.remove(victim);

        // Remove victim from frequency map also,
        // otherwise frequency map will keep stale data.
        frequency.remove(victim);

        System.out.println("LRU evicted: " + victim);
    }

    public void evictLfu() {

        // LFU means:
        // remove least frequently used key.
        //
        // This method is added only for comparison.
        // It scans the frequency map and finds the smallest count.
        String victim = null;
        int minFreq = Integer.MAX_VALUE;

        // Find key with minimum frequency.
        //
        // Complexity:
        // O(n), because we scan all entries.
        for (Map.Entry<String, Integer> entry : frequency.entrySet()) {

            if (entry.getValue() < minFreq) {
                minFreq = entry.getValue();
                victim = entry.getKey();
            }
        }

        // If a victim exists, remove it from both structures.
        if (victim != null) {
            lru.remove(victim);
            frequency.remove(victim);
            System.out.println("LFU evicted: " + victim);
        }
    }

    public void printState() {

        // Prints current cache order.
        //
        // Because accessOrder=true:
        // first printed key  = least recently used
        // last printed key   = most recently used
        System.out.println("cache=" + lru);

        // Prints frequency count for each key.
        System.out.println("freq=" + frequency);
    }
}
```

---

## 8.2 `Phase013Driver.java`

### Logic summary before this class

This driver proves that LRU eviction works.

We create a cache with capacity 2:

```text
cache can store only 2 keys
```

Then we run:

```text
SET a 1
SET b 2
GET a
SET c 3
```

Important point:

```text
GET a makes a recently used.
So b becomes least recently used.
When c is inserted, b is evicted.
```

Expected final cache after LRU:

```text
a -> 1
c -> 3
```

Then we call LFU eviction once to show frequency-based removal.

```java
package com.miniredis.driver;

import com.miniredis.eviction.EvictionCache;

public class Phase013Driver {

    public static void main(String[] args) {

        // Create cache with capacity 2.
        //
        // This means:
        // At most 2 keys can stay in memory.
        EvictionCache cache = new EvictionCache(2);

        // Step 1:
        // Insert key a.
        //
        // Cache:
        // a
        cache.set("a", "1");

        // Step 2:
        // Insert key b.
        //
        // Cache order:
        // a -> b
        //
        // a is least recently used.
        // b is most recently used.
        cache.set("b", "2");

        // Step 3:
        // Read key a.
        //
        // Because a is accessed now, it becomes most recently used.
        //
        // Cache order changes:
        // before GET a: a -> b
        // after GET a:  b -> a
        cache.get("a");

        // Step 4:
        // Insert key c.
        //
        // Cache is already full:
        // b -> a
        //
        // Least recently used = b
        //
        // So b is evicted.
        //
        // After SET c:
        // a -> c
        cache.set("c", "3");

        // Print current LRU state.
        //
        // Expected:
        // cache={a=1, c=3}
        // b should not exist anymore.
        cache.printState();

        // Step 5:
        // Demonstrate LFU eviction.
        //
        // Current frequency roughly:
        // a touched by SET and GET
        // c touched by SET
        //
        // c has lower frequency, so LFU removes c.
        cache.evictLfu();

        // Final print after LFU eviction.
        //
        // Expected:
        // only a remains.
        cache.printState();
    }
}
```


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

We use:

```text
capacity = 2
```

That means only two keys can stay in memory.

---

## 10.1 Core LRU Mental Model

`LinkedHashMap` with `accessOrder = true` keeps order like this:

```text
LEFT                                RIGHT
Least Recently Used        Most Recently Used
```

So:

```text
first key  = eviction candidate
last key   = safest key
```

---

## 10.2 Initial State

```text
Cache capacity = 2

LRU cache:
[]

Frequency map:
{}
```

Diagram:

```text
LRU Order
HEAD/LRU                         TAIL/MRU
   |                                  |
   v                                  v
[ empty ]
```

---

## 10.3 Step 1 — SET a 1

Command:

```text
SET a 1
```

What happens:

```text
1. cache is not full
2. insert a
3. a becomes most recently used
4. frequency[a] becomes 1
```

State:

```text
LRU cache:
[a=1]

Frequency:
a -> 1
```

Diagram:

```text
HEAD/LRU                         TAIL/MRU
   |                                  |
   v                                  v
[a]
```

---

## 10.4 Step 2 — SET b 2

Command:

```text
SET b 2
```

What happens:

```text
1. cache size = 1
2. capacity = 2
3. no eviction
4. insert b
5. b becomes most recently used
```

State:

```text
LRU cache:
[a=1, b=2]

Frequency:
a -> 1
b -> 1
```

Diagram:

```text
HEAD/LRU                         TAIL/MRU
   |                                  |
   v                                  v
[a] -----------------------------> [b]
```

Meaning:

```text
a is older
b is newer
```

If a new key comes now without touching anything:

```text
a will be evicted
```

---

## 10.5 Step 3 — GET a

Command:

```text
GET a
```

What happens:

```text
1. lookup a
2. a exists
3. return value 1
4. because accessOrder=true, a moves to most-recent position
5. frequency[a] increases
```

Before:

```text
[a] -> [b]
```

After:

```text
[b] -> [a]
```

State:

```text
LRU cache:
[b=2, a=1]

Frequency:
a -> 2
b -> 1
```

Diagram:

```text
Before GET a:

HEAD/LRU                         TAIL/MRU
   |                                  |
   v                                  v
[a] -----------------------------> [b]


After GET a:

HEAD/LRU                         TAIL/MRU
   |                                  |
   v                                  v
[b] -----------------------------> [a]
```

Important observation:

```text
GET is not a passive read.
GET changes recency.
```

That is the heart of LRU.

---

## 10.6 Step 4 — SET c 3

Command:

```text
SET c 3
```

Current state before insert:

```text
cache = [b=2, a=1]
capacity = 2
```

What happens:

```text
1. c is a new key
2. cache is full
3. evict least recently used key
4. first key in LinkedHashMap = b
5. remove b
6. insert c
7. c becomes most recently used
```

Before eviction:

```text
HEAD/LRU                         TAIL/MRU
   |                                  |
   v                                  v
[b] -----------------------------> [a]
```

Evict:

```text
remove b
```

After inserting c:

```text
HEAD/LRU                         TAIL/MRU
   |                                  |
   v                                  v
[a] -----------------------------> [c]
```

State:

```text
LRU cache:
[a=1, c=3]

Frequency:
a -> 2
c -> 1
```

Output:

```text
LRU evicted: b
```

---

## 10.7 Step 5 — printState()

Command:

```java
cache.printState();
```

Expected output:

```text
cache={a=1, c=3}
freq={a=2, c=1}
```

Meaning:

```text
b was removed because it was least recently used.
```

---

## 10.8 Step 6 — evictLfu()

Command:

```java
cache.evictLfu();
```

Current frequency:

```text
a -> 2
c -> 1
```

LFU means:

```text
remove least frequently used
```

So victim:

```text
c
```

After LFU eviction:

```text
cache={a=1}
freq={a=2}
```

Output:

```text
LFU evicted: c
```

---

## 10.9 Full Visual Timeline

```text
capacity = 2

START
cache = []

SET a 1
cache = [a]
freq  = {a=1}

SET b 2
cache = [a -> b]
freq  = {a=1, b=1}

GET a
cache = [b -> a]
freq  = {a=2, b=1}

SET c 3
cache full
LRU victim = first key = b
cache = [a -> c]
freq  = {a=2, c=1}

LFU eviction
least frequency = c
cache = [a]
freq  = {a=2}
```

---

## 10.10 Why LRU Works

LRU works because recently used keys are more likely to be used again.

This is called:

```text
temporal locality
```

Example:

```text
If user profile 101 was accessed now,
it may be accessed again soon.
```

So cache keeps hot/recent keys and removes cold/old keys.

---

## 10.11 Complexity

| Operation | Complexity | Why |
|---|---:|---|
| `get(key)` | O(1) | Hash lookup + move node |
| `set(key,value)` | O(1) | Hash insert/update |
| `evictLru()` | O(1) | first linked node |
| `evictLfu()` in this simple version | O(n) | scans frequency map |

Production LFU needs a more advanced structure for O(1), but this phase focuses mainly on LRU.

---

## 10.12 System Design Dry Run

Imagine Redis has memory limit:

```text
maxmemory = 2 keys
```

Requests:

```text
SET session:1 A
SET session:2 B
GET session:1
SET session:3 C
```

Memory timeline:

```text
After SET session:1:
[session:1]

After SET session:2:
[session:1 -> session:2]

After GET session:1:
[session:2 -> session:1]

After SET session:3:
evict session:2
[session:1 -> session:3]
```

System design explanation:

```text
When memory is full, Redis removes the least recently used key
so the cache keeps recently active data.
```

This is used in:

```text
Redis cache
CDN cache
API response cache
database buffer pool
browser cache
operating system page cache
```


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
