# 015_List_Data_Type.md

# MiniRedis Phase 15 — List Data Type

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
List Data Type
```

Purpose:

```text
Add Redis List commands using a deque.
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
Only string values existed; no queue-like data structure was available.
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

We add a small Redis-like data structure store.

Although the phase name is `List Data Type`, the current code also demonstrates:

```text
List        -> LPUSH / LPOP
Set         -> SADD / SISMEMBER
Hash        -> HSET / HGET
Sorted Set  -> ZADD / ZRANGE
```

Main focus of this phase:

```text
Redis key
  -> points to an internal data structure
  -> operation modifies only that structure
```

Commands or operations covered by the code:

```text
LPUSH jobs job-1
LPUSH jobs job-2
LPOP jobs

SADD users mohamed
SISMEMBER users mohamed

HSET user:1 name Mohamed
HGET user:1 name

ZADD leaderboard 100 bob
ZADD leaderboard 90 alice
ZRANGE leaderboard
```

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
+----------------------+
| Phase015Driver       |
| creates operations   |
+----------+-----------+
           |
           v
+----------------------+
| RedisDataStore       |
| routes by data type  |
+----------+-----------+
           |
           +--------------------+
           |                    |
           v                    v
+------------------+   +------------------+
| lists Map        |   | sets Map         |
| key -> Deque     |   | key -> Set       |
+------------------+   +------------------+

           +--------------------+
           |                    |
           v                    v
+------------------+   +----------------------------+
| hashes Map       |   | zsets Map                  |
| key -> Map       |   | key -> TreeMap<score,list> |
+------------------+   +----------------------------+
```

Phase flow:

```text
Driver command
  -> call RedisDataStore method
  -> find/create internal structure for key
  -> mutate or read structure
  -> return result
```

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

### Logic summary before this class

`RedisDataStore` is the in-memory engine for Redis-style data types.

Instead of storing only:

```text
key -> string
```

this class stores:

```text
key -> list
key -> set
key -> hash
key -> sorted set
```

This is how Redis becomes more powerful than a normal key-value cache.

Important internal mappings:

```text
lists  : Map<String, Deque<String>>
sets   : Map<String, Set<String>>
hashes : Map<String, Map<String, String>>
zsets  : Map<String, TreeMap<Double, List<String>>>
```

Mental model:

```text
Redis command
  -> choose correct map
  -> get data structure for key
  -> create structure if missing
  -> perform operation
```

```java
package com.miniredis.storage;

import java.util.*;

/**
 * RedisDataStore is a simplified in-memory Redis engine.
 *
 * It supports multiple Redis-style data structures:
 *
 * 1. List       -> key maps to Deque<String>
 * 2. Set        -> key maps to Set<String>
 * 3. Hash       -> key maps to Map<field, value>
 * 4. Sorted Set -> key maps to TreeMap<score, members>
 *
 * This is not production Redis.
 * It is a learning model to understand how Redis maps a key
 * to different internal data structures.
 */
public class RedisDataStore {

    /**
     * LIST storage.
     *
     * Example:
     * key = "jobs"
     * value = deque ["job-2", "job-1"]
     *
     * Deque is used because Redis lists need fast operations
     * at both ends:
     *
     * LPUSH -> addFirst()
     * LPOP  -> removeFirst()
     * RPUSH -> addLast()    // can be added later
     * RPOP  -> removeLast() // can be added later
     */
    private final Map<String, Deque<String>> lists = new HashMap<>();

    /**
     * SET storage.
     *
     * Example:
     * key = "users"
     * value = {"mohamed", "alice"}
     *
     * HashSet gives average O(1) membership check.
     */
    private final Map<String, Set<String>> sets = new HashMap<>();

    /**
     * HASH storage.
     *
     * Example:
     * key = "user:1"
     * value = {
     *   "name" -> "Mohamed",
     *   "city" -> "Bucharest"
     * }
     *
     * This behaves like Redis HSET / HGET.
     */
    private final Map<String, Map<String, String>> hashes = new HashMap<>();

    /**
     * SORTED SET storage.
     *
     * Example:
     * key = "leaderboard"
     * value = {
     *   90.0  -> ["alice"],
     *   100.0 -> ["bob"]
     * }
     *
     * TreeMap keeps scores sorted automatically.
     * That is why ZRANGE can return members ordered by score.
     */
    private final Map<String, TreeMap<Double, List<String>>> zsets = new HashMap<>();

    /**
     * LPUSH key value
     *
     * Adds value to the LEFT/head of the list.
     *
     * Example:
     * LPUSH jobs job-1
     * jobs = [job-1]
     *
     * LPUSH jobs job-2
     * jobs = [job-2, job-1]
     */
    public void lpush(String key, String value) {

        // If list does not exist for this key,
        // create a new LinkedList.
        Deque<String> list =
                lists.computeIfAbsent(key, k -> new LinkedList<>());

        // Add new value to the front.
        // This is why LPUSH behaves like stack push from the left.
        list.addFirst(value);
    }

    /**
     * LPOP key
     *
     * Removes and returns the LEFT/head element.
     *
     * Example:
     * jobs = [job-2, job-1]
     * LPOP jobs -> job-2
     * jobs = [job-1]
     */
    public String lpop(String key) {

        // Get the list stored at this Redis key.
        Deque<String> list = lists.get(key);

        // If key does not exist or list is empty,
        // Redis-like behavior is nil/null.
        if (list == null || list.isEmpty()) {
            return null;
        }

        // Remove from the front of the deque.
        return list.removeFirst();
    }

    /**
     * SADD key member
     *
     * Adds member into a set.
     * Duplicate members are automatically ignored by HashSet.
     */
    public void sadd(String key, String member) {

        // Create set if this key does not exist.
        Set<String> set =
                sets.computeIfAbsent(key, k -> new HashSet<>());

        // HashSet add is average O(1).
        set.add(member);
    }

    /**
     * SISMEMBER key member
     *
     * Checks whether member exists inside the set.
     */
    public boolean sismember(String key, String member) {

        // If key is missing, use empty set.
        // Then contains() safely returns false.
        return sets.getOrDefault(key, Set.of()).contains(member);
    }

    /**
     * HSET key field value
     *
     * Stores field-value pair inside a Redis hash.
     *
     * Example:
     * HSET user:1 name Mohamed
     *
     * hashes:
     * user:1 -> { name -> Mohamed }
     */
    public void hset(String key, String field, String value) {

        // Create inner hash map if key does not exist.
        Map<String, String> hash =
                hashes.computeIfAbsent(key, k -> new HashMap<>());

        // Put or overwrite the field.
        hash.put(field, value);
    }

    /**
     * HGET key field
     *
     * Reads value of one field from a Redis hash.
     */
    public String hget(String key, String field) {

        // If hash key does not exist, use empty map.
        // Then get(field) returns null.
        return hashes.getOrDefault(key, Map.of()).get(field);
    }

    /**
     * ZADD key score member
     *
     * Adds member with score into sorted set.
     *
     * TreeMap keeps all scores sorted.
     */
    public void zadd(String key, double score, String member) {

        // Get sorted set for key, or create it.
        TreeMap<Double, List<String>> sortedSet =
                zsets.computeIfAbsent(key, k -> new TreeMap<>());

        // Multiple members can have same score.
        // So score maps to List<String>.
        List<String> membersWithSameScore =
                sortedSet.computeIfAbsent(score, s -> new ArrayList<>());

        // Add member under this score.
        membersWithSameScore.add(member);
    }

    /**
     * ZRANGE key
     *
     * Returns all members sorted by score ascending.
     *
     * Because TreeMap stores keys in sorted order,
     * iterating over values gives ascending score order.
     */
    public List<String> zrange(String key) {

        List<String> result = new ArrayList<>();

        // If key does not exist, use empty TreeMap.
        TreeMap<Double, List<String>> sortedSet =
                zsets.getOrDefault(key, new TreeMap<>());

        // TreeMap values are already in sorted score order.
        for (List<String> members : sortedSet.values()) {
            result.addAll(members);
        }

        return result;
    }
}
```

---

## 8.2 `Phase015Driver.java`

### Logic summary before this class

`Phase015Driver` is a small executable demo.

It creates one `RedisDataStore`, runs commands on it, and prints the output.

The driver shows four Redis data-type behaviors:

```text
List       -> LPUSH + LPOP
Set        -> SADD + SISMEMBER
Hash       -> HSET + HGET
Sorted Set -> ZADD + ZRANGE
```

This driver is the exact source for the dry run in section 10.

```java
package com.miniredis.driver;

import com.miniredis.storage.RedisDataStore;

/**
 * Phase015Driver demonstrates how RedisDataStore works in memory.
 *
 * It does not start a TCP server.
 * It directly calls Java methods so we can understand the data structures.
 */
public class Phase015Driver {

    public static void main(String[] args) {

        // Create one in-memory Redis-like store.
        RedisDataStore store = new RedisDataStore();

        // ------------------------------
        // LIST example
        // ------------------------------

        // LPUSH jobs job-1
        // jobs list becomes: [job-1]
        store.lpush("jobs", "job-1");

        // LPUSH jobs job-2
        // LPUSH inserts at the LEFT side.
        // jobs list becomes: [job-2, job-1]
        store.lpush("jobs", "job-2");

        // LPOP jobs removes from LEFT side.
        // returns job-2
        // jobs list becomes: [job-1]
        System.out.println("LPOP jobs = " + store.lpop("jobs"));

        // ------------------------------
        // SET example
        // ------------------------------

        // SADD users mohamed
        // users set becomes: {mohamed}
        store.sadd("users", "mohamed");

        // SISMEMBER checks if mohamed exists in users set.
        // returns true
        System.out.println(
                "SISMEMBER users mohamed = "
                        + store.sismember("users", "mohamed")
        );

        // ------------------------------
        // HASH example
        // ------------------------------

        // HSET user:1 name Mohamed
        // user:1 hash becomes: {name=Mohamed}
        store.hset("user:1", "name", "Mohamed");

        // HGET user:1 name returns Mohamed.
        System.out.println(
                "HGET user:1 name = "
                        + store.hget("user:1", "name")
        );

        // ------------------------------
        // SORTED SET example
        // ------------------------------

        // ZADD leaderboard 100 bob
        // leaderboard becomes: 100 -> [bob]
        store.zadd("leaderboard", 100, "bob");

        // ZADD leaderboard 90 alice
        // TreeMap keeps score 90 before score 100.
        // leaderboard becomes:
        // 90  -> [alice]
        // 100 -> [bob]
        store.zadd("leaderboard", 90, "alice");

        // ZRANGE leaderboard returns members by score ascending:
        // [alice, bob]
        System.out.println(
                "ZRANGE leaderboard = "
                        + store.zrange("leaderboard")
        );
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
java -cp out com.miniredis.driver.Phase015Driver
```

If your shell does not expand `**`, compile individual files or use IntelliJ.

---


# 10. Step-by-Step Dry Run

This dry run follows the exact code from `Phase015Driver.java`.

---

## 10.1 Initial Memory

```text
RedisDataStore
├── lists  = {}
├── sets   = {}
├── hashes = {}
└── zsets  = {}
```

No key exists yet.

---

## 10.2 LIST Dry Run — LPUSH / LPOP

### Step 1

Code:

```java
store.lpush("jobs", "job-1");
```

Meaning:

```text
LPUSH jobs job-1
```

Internal logic:

```text
lists does not contain "jobs"
create new LinkedList
add job-1 to LEFT
```

Memory:

```text
lists
└── jobs -> [job-1]
```

---

### Step 2

Code:

```java
store.lpush("jobs", "job-2");
```

Meaning:

```text
LPUSH jobs job-2
```

`LPUSH` adds to the left/front.

Before:

```text
jobs -> [job-1]
```

After:

```text
jobs -> [job-2, job-1]
```

Memory:

```text
lists
└── jobs -> [job-2, job-1]
```

---

### Step 3

Code:

```java
store.lpop("jobs");
```

Meaning:

```text
LPOP jobs
```

`LPOP` removes from the left/front.

Before:

```text
jobs -> [job-2, job-1]
```

Returned:

```text
job-2
```

After:

```text
jobs -> [job-1]
```

Console output:

```text
LPOP jobs = job-2
```

---

## 10.3 SET Dry Run — SADD / SISMEMBER

### Step 4

Code:

```java
store.sadd("users", "mohamed");
```

Meaning:

```text
SADD users mohamed
```

Internal logic:

```text
sets does not contain "users"
create new HashSet
add "mohamed"
```

Memory:

```text
sets
└── users -> {mohamed}
```

---

### Step 5

Code:

```java
store.sismember("users", "mohamed");
```

Meaning:

```text
SISMEMBER users mohamed
```

Lookup:

```text
users set contains mohamed?
yes
```

Returned:

```text
true
```

Console output:

```text
SISMEMBER users mohamed = true
```

---

## 10.4 HASH Dry Run — HSET / HGET

### Step 6

Code:

```java
store.hset("user:1", "name", "Mohamed");
```

Meaning:

```text
HSET user:1 name Mohamed
```

Internal logic:

```text
hashes does not contain "user:1"
create inner HashMap
put field "name" -> "Mohamed"
```

Memory:

```text
hashes
└── user:1
    └── name -> Mohamed
```

---

### Step 7

Code:

```java
store.hget("user:1", "name");
```

Meaning:

```text
HGET user:1 name
```

Lookup:

```text
hashes["user:1"]["name"]
```

Returned:

```text
Mohamed
```

Console output:

```text
HGET user:1 name = Mohamed
```

---

## 10.5 SORTED SET Dry Run — ZADD / ZRANGE

### Step 8

Code:

```java
store.zadd("leaderboard", 100, "bob");
```

Meaning:

```text
ZADD leaderboard 100 bob
```

Internal logic:

```text
zsets does not contain "leaderboard"
create TreeMap
score 100 does not exist
create list for score 100
add bob
```

Memory:

```text
zsets
└── leaderboard
    └── 100.0 -> [bob]
```

---

### Step 9

Code:

```java
store.zadd("leaderboard", 90, "alice");
```

Meaning:

```text
ZADD leaderboard 90 alice
```

TreeMap keeps scores sorted.

Memory:

```text
zsets
└── leaderboard
    ├── 90.0  -> [alice]
    └── 100.0 -> [bob]
```

---

### Step 10

Code:

```java
store.zrange("leaderboard");
```

Meaning:

```text
ZRANGE leaderboard
```

TreeMap iteration order:

```text
90.0 first
100.0 second
```

Result:

```text
[alice, bob]
```

Console output:

```text
ZRANGE leaderboard = [alice, bob]
```

---

## 10.6 Final Memory State

```text
RedisDataStore
├── lists
│   └── jobs -> [job-1]
│
├── sets
│   └── users -> {mohamed}
│
├── hashes
│   └── user:1
│       └── name -> Mohamed
│
└── zsets
    └── leaderboard
        ├── 90.0  -> [alice]
        └── 100.0 -> [bob]
```

---

## 10.7 Full Output

```text
LPOP jobs = job-2
SISMEMBER users mohamed = true
HGET user:1 name = Mohamed
ZRANGE leaderboard = [alice, bob]
```

# 11. Test Commands

These test commands match the current Java driver and code.

```text
LPUSH jobs job-1
LPUSH jobs job-2
LPOP jobs

SADD users mohamed
SISMEMBER users mohamed

HSET user:1 name Mohamed
HGET user:1 name

ZADD leaderboard 100 bob
ZADD leaderboard 90 alice
ZRANGE leaderboard
```

Expected output from `Phase015Driver`:

```text
LPOP jobs = job-2
SISMEMBER users mohamed = true
HGET user:1 name = Mohamed
ZRANGE leaderboard = [alice, bob]
```

For this phase, the driver directly calls Java methods.

Later, when these commands are connected to the TCP server, you can test with:

```bash
telnet localhost 6379
```

or:

```bash
nc localhost 6379
```

# 12. DSA / CP Concepts Used

```text
Deque
HashSet
HashMap
TreeMap
List
```

Mapping:

| Redis Feature | Java Data Structure | Why |
|---|---|---|
| List | `Deque<String>` | fast push/pop from ends |
| Set | `HashSet<String>` | fast membership check |
| Hash | `HashMap<String, String>` | field-value lookup |
| Sorted Set | `TreeMap<Double, List<String>>` | sorted scores |

Complexities in this implementation:

| Operation | Average Complexity |
|---|---|
| LPUSH | O(1) |
| LPOP | O(1) |
| SADD | O(1) average |
| SISMEMBER | O(1) average |
| HSET | O(1) average |
| HGET | O(1) average |
| ZADD | O(log n) for TreeMap score insert |
| ZRANGE | O(n) to collect all members |

This is exactly how DSA connects to Redis internals.

# 13. System Design Relevance

This phase maps to real backend use cases:

| Data Type | Real System Design Use |
|---|---|
| List | job queue, recent events, activity feed buffer |
| Set | unique users, permissions, online user IDs |
| Hash | user profile, session object, cart metadata |
| Sorted Set | leaderboard, ranking, delayed jobs, priority queue |

System design thinking:

```text
Requirement:
Need ordered queue?
Use List / Deque.

Requirement:
Need uniqueness?
Use Set.

Requirement:
Need object fields?
Use Hash.

Requirement:
Need ranking by score/time?
Use Sorted Set.
```

This is why Redis is common in:
- queues
- caching
- sessions
- leaderboards
- rate limiting
- feed systems

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
016
```

Continue the MiniRedis roadmap until the final production architecture.
