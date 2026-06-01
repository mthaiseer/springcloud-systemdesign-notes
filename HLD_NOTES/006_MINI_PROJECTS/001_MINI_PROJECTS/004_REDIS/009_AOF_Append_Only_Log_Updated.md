# 009_AOF_Append_Only_Log.md

# MiniRedis Phase 9 — AOF Append Only Log

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
AOF Append Only Log
```

Purpose:

```text
Append every write command to disk for better durability.
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
RDB snapshots can lose recent writes between snapshots.
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
We add append-only logging for SET, DEL, and expiring SET commands.
```

Commands or operations covered:

```text
SET
DEL
AOF append
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
| AOF Append Only Log |
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
  -> execute AOF Append Only Log
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


## 8.1 `RedisStore.java`

### Logic before this class

The store keeps key-value pairs in memory.

For persistence phases, we also need a safe way to expose all data for snapshot or replay.

```java
package com.miniredis.storage;

import java.util.HashMap;
import java.util.Map;

/**
 * RedisStore is the in-memory database.
 *
 * Mental model:
 * Redis memory = HashMap
 *
 * Example:
 * SET name mohamed
 *
 * Internal memory:
 * {
 *   "name" : "mohamed"
 * }
 */
public class RedisStore {

    // Core in-memory data structure.
    // Key   = Redis key
    // Value = Redis string value
    private final Map<String, String> data = new HashMap<>();

    /**
     * SET key value
     *
     * If key does not exist:
     *   insert new key
     *
     * If key already exists:
     *   overwrite old value
     */
    public void set(String key, String value) {

        // HashMap put is average O(1).
        data.put(key, value);
    }

    /**
     * GET key
     *
     * Return value if key exists.
     * Return null if key does not exist.
     */
    public String get(String key) {

        // HashMap get is average O(1).
        return data.get(key);
    }

    /**
     * DEL key
     *
     * Remove key from memory.
     *
     * Return:
     * true  -> key existed and was removed
     * false -> key did not exist
     */
    public boolean delete(String key) {

        // remove returns old value.
        // If old value is null, key was missing.
        return data.remove(key) != null;
    }

    /**
     * Create a copy of current memory.
     *
     * Used by RDB snapshot.
     *
     * Important:
     * We return a new HashMap so outside code cannot directly mutate
     * internal Redis memory.
     */
    public Map<String, String> snapshot() {

        return new HashMap<>(data);
    }

    /**
     * Load restored data into memory.
     *
     * Used when Redis restarts from RDB.
     */
    public void load(Map<String, String> restored) {

        // Clear old memory first.
        data.clear();

        // Copy restored key-values into memory.
        data.putAll(restored);
    }

    /**
     * Number of keys currently stored.
     */
    public int size() {

        return data.size();
    }

    /**
     * Debug helper for dry run.
     */
    public String debugMemory() {

        return data.toString();
    }
}
```

---

## 8.2 `RdbSnapshot.java`

### Logic before this class

RDB snapshot writes the full current state to disk.

It is similar to taking a photo of memory.

```java
package com.miniredis.persistence;

import com.miniredis.storage.RedisStore;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * RDB Snapshot stores the FULL memory state at a point in time.
 *
 * Mental model:
 * AOF = command history
 * RDB = memory photo
 *
 * Example memory:
 * {
 *   "name" : "mohamed",
 *   "city" : "bucharest"
 * }
 *
 * RDB writes this full map to disk.
 */
public class RdbSnapshot {

    // File where snapshot is saved.
    private final File file;

    public RdbSnapshot(String path) {
        this.file = new File(path);
    }

    /**
     * Save current RedisStore memory to disk.
     */
    public void save(RedisStore store) throws IOException {

        // Ensure parent folder exists.
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        // ObjectOutputStream serializes Java object to disk.
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(file))) {

            // Save copy of full memory.
            out.writeObject(store.snapshot());
        }
    }

    /**
     * Load snapshot from disk into RedisStore memory.
     */
    @SuppressWarnings("unchecked")
    public void load(RedisStore store)
            throws IOException, ClassNotFoundException {

        // If snapshot file does not exist, nothing to restore.
        if (!file.exists()) {
            return;
        }

        // ObjectInputStream reads serialized Java object from disk.
        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(file))) {

            // Read saved map.
            Map<String, String> restored =
                    (HashMap<String, String>) in.readObject();

            // Replace current memory with restored memory.
            store.load(restored);
        }
    }
}
```

---

## 8.3 `AofLog.java`

### Logic before this class

AOF stores every write command.

Instead of storing the final state only, it stores the history:

```text
SET name mohamed
DEL city
```

On restart, we replay the history.

```java
package com.miniredis.persistence;

import com.miniredis.storage.RedisStore;

import java.io.*;

/**
 * AOF = Append Only File.
 *
 * Instead of only storing final memory state,
 * AOF stores every write command in order.
 *
 * Example AOF file:
 *
 * SET name mohamed
 * SET city bucharest
 * DEL name
 *
 * On restart:
 * 1. Start with empty memory.
 * 2. Replay each command from top to bottom.
 * 3. Final memory becomes same as before crash.
 */
public class AofLog {

    // File where commands are appended.
    private final File file;

    public AofLog(String path) {
        this.file = new File(path);
    }

    /**
     * Append one write command to AOF file.
     *
     * Important production idea:
     * Writes are sequential append operations.
     * Sequential disk writes are faster than random disk writes.
     */
    public void append(String command) throws IOException {

        // Ensure parent folder exists.
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        // true means append mode.
        // Existing file content is not overwritten.
        try (FileWriter writer = new FileWriter(file, true)) {

            // Write command exactly as it happened.
            writer.write(command);

            // One command per line.
            writer.write(System.lineSeparator());

            // In real Redis, fsync policy controls durability:
            // always / everysec / no.
            // Here try-with-resources closes writer and flushes data.
        }
    }

    /**
     * Recover memory by replaying AOF commands.
     *
     * This is similar to:
     * for each command in log:
     *   execute command again
     */
    public void recover(RedisStore store) throws IOException {

        // If no AOF file exists, Redis starts empty.
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            String line;

            // Read AOF file line by line.
            while ((line = reader.readLine()) != null) {

                // Ignore empty lines.
                if (line.isBlank()) {
                    continue;
                }

                // Simple parser:
                // "SET name mohamed" -> ["SET", "name", "mohamed"]
                // "DEL city"        -> ["DEL", "city"]
                String[] parts = line.split("\s+");

                String command = parts[0];

                if (command.equalsIgnoreCase("SET")) {

                    // Validate SET key value
                    if (parts.length != 3) {
                        throw new IllegalStateException(
                                "Invalid SET command in AOF: " + line
                        );
                    }

                    String key = parts[1];
                    String value = parts[2];

                    // Replay SET into memory.
                    store.set(key, value);

                } else if (command.equalsIgnoreCase("DEL")) {

                    // Validate DEL key
                    if (parts.length != 2) {
                        throw new IllegalStateException(
                                "Invalid DEL command in AOF: " + line
                        );
                    }

                    String key = parts[1];

                    // Replay DEL into memory.
                    store.delete(key);

                } else {

                    // Unknown command means AOF file is corrupted or unsupported.
                    throw new IllegalStateException(
                            "Unknown command in AOF: " + line
                    );
                }
            }
        }
    }
}
```

---

## 8.4 `Phase009Driver.java`

### Logic before this class

The driver demonstrates persistence.

For snapshot, it saves the full state.

For AOF, it appends write commands and recovers by replay.

```java
package com.miniredis.driver;

import com.miniredis.persistence.AofLog;
import com.miniredis.persistence.RdbSnapshot;
import com.miniredis.storage.RedisStore;

/**
 * Driver for Phase 009.
 *
 * Goal:
 * Show how memory, RDB, and AOF work together.
 */
public class Phase009Driver {

    public static void main(String[] args) throws Exception {

        // -----------------------------
        // 1. Start Redis with empty memory
        // -----------------------------
        RedisStore store = new RedisStore();

        // RDB stores full memory snapshot.
        RdbSnapshot snapshot =
                new RdbSnapshot("data/miniredis.rdb");

        // AOF stores every write command.
        AofLog aof =
                new AofLog("data/miniredis.aof");

        // -----------------------------
        // 2. Execute first write command
        // -----------------------------
        // Command:
        // SET name mohamed
        store.set("name", "mohamed");

        // Append same write command to AOF.
        // This makes the write recoverable after crash.
        aof.append("SET name mohamed");

        System.out.println("After SET name:");
        System.out.println(store.debugMemory());

        // -----------------------------
        // 3. Execute second write command
        // -----------------------------
        // Command:
        // SET city bucharest
        store.set("city", "bucharest");

        // Append second write command to AOF.
        aof.append("SET city bucharest");

        System.out.println("After SET city:");
        System.out.println(store.debugMemory());

        // -----------------------------
        // 4. Execute delete command
        // -----------------------------
        // Command:
        // DEL name
        store.delete("name");

        // Append delete command to AOF.
        aof.append("DEL name");

        System.out.println("After DEL name:");
        System.out.println(store.debugMemory());

        // -----------------------------
        // 5. Save RDB snapshot
        // -----------------------------
        // This saves final memory:
        // {
        //   city=bucharest
        // }
        snapshot.save(store);

        // -----------------------------
        // 6. Recover from RDB
        // -----------------------------
        RedisStore recoveredFromSnapshot =
                new RedisStore();

        snapshot.load(recoveredFromSnapshot);

        System.out.println("Recovered from RDB:");
        System.out.println(recoveredFromSnapshot.debugMemory());

        // -----------------------------
        // 7. Recover from AOF
        // -----------------------------
        RedisStore recoveredFromAof =
                new RedisStore();

        // AOF replay:
        // SET name mohamed
        // SET city bucharest
        // DEL name
        //
        // Final memory:
        // {
        //   city=bucharest
        // }
        aof.recover(recoveredFromAof);

        System.out.println("Recovered from AOF:");
        System.out.println(recoveredFromAof.debugMemory());
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
java -cp out com.miniredis.driver.Phase009Driver
```

If your shell does not expand `**`, compile individual files or use IntelliJ.

---


# 10. Step-by-Step Dry Run

We will dry run everything fully in memory.

Initial memory:

```text
{}
```

Initial AOF file:

```text
empty
```

---

## Step 1 — SET name mohamed

Code executed:

```java
store.set("name", "mohamed");
aof.append("SET name mohamed");
```

Memory change:

```text
Before:
{}

Operation:
SET name mohamed

After:
{
  name = mohamed
}
```

AOF file after append:

```text
SET name mohamed
```

Why this matters:

```text
Memory gives fast access.
AOF gives crash recovery.
```

---

## Step 2 — SET city bucharest

Code executed:

```java
store.set("city", "bucharest");
aof.append("SET city bucharest");
```

Memory change:

```text
Before:
{
  name = mohamed
}

Operation:
SET city bucharest

After:
{
  name = mohamed,
  city = bucharest
}
```

AOF file:

```text
SET name mohamed
SET city bucharest
```

---

## Step 3 — DEL name

Code executed:

```java
store.delete("name");
aof.append("DEL name");
```

Memory change:

```text
Before:
{
  name = mohamed,
  city = bucharest
}

Operation:
DEL name

After:
{
  city = bucharest
}
```

AOF file:

```text
SET name mohamed
SET city bucharest
DEL name
```

---

## Step 4 — Process crashes

Assume JVM crashes.

Memory is lost:

```text
{}
```

But disk still has AOF:

```text
SET name mohamed
SET city bucharest
DEL name
```

This is the main purpose of AOF.

---

## Step 5 — Restart and recover from AOF

Recovery starts with empty memory:

```text
{}
```

Now replay AOF line by line.

---

### Replay line 1

AOF command:

```text
SET name mohamed
```

Memory:

```text
{
  name = mohamed
}
```

---

### Replay line 2

AOF command:

```text
SET city bucharest
```

Memory:

```text
{
  name = mohamed,
  city = bucharest
}
```

---

### Replay line 3

AOF command:

```text
DEL name
```

Memory:

```text
{
  city = bucharest
}
```

Final recovered memory:

```text
{
  city = bucharest
}
```

This matches the memory before crash.

---

## Important Mental Model

```text
AOF recovery = start empty + replay all write commands in order
```

Like CP prefix simulation:

```text
initial state
  -> operation 1
  -> operation 2
  -> operation 3
  -> final state
```

---

## Why order matters

Correct AOF order:

```text
SET name mohamed
DEL name
```

Final result:

```text
name does not exist
```

Wrong order:

```text
DEL name
SET name mohamed
```

Final result:

```text
name = mohamed
```

So AOF is an ordered log.

---

## Time Complexity

For one command append:

```text
O(1) append
```

For recovery:

```text
O(number_of_commands_in_AOF)
```

If AOF has 1 million commands:

```text
recovery replays 1 million commands
```

That is why real Redis also supports:

```text
AOF rewrite / compaction
```

Example old AOF:

```text
SET name a
SET name b
SET name c
```

Can be compacted to:

```text
SET name c
```

because only latest final state matters.

---

# 11. Test Commands

Try these mental or driver-level commands:

```text
SET
DEL
AOF append
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
Write-ahead log, sequential writes, append-only storage
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
database WAL, Kafka log, event sourcing, durable cache
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
010
```

Continue the MiniRedis roadmap until the final production architecture.
