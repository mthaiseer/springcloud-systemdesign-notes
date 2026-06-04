# 010_Recover_From_AOF.md

# MiniRedis Phase 10 — Recover From AOF

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
Recover From AOF
```

Purpose:

```text
Recover memory state by replaying the append-only log.
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
AOF was written but not used to rebuild state after restart.
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
We add AOF replay during startup and rebuild RedisStore deterministically.
```

Commands or operations covered:

```text
AOF replay
SET
DEL
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
| Recover From AOF |
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
  -> execute Recover From AOF
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

This phase shows **why RDB and AOF work together**.

Mental model:

```text
Redis memory = HashMap
RDB          = snapshot/photo of HashMap at a point in time
AOF          = command journal after writes
Recovery     = replay persisted state back into HashMap
```

---

## 8.1 `RedisStore.java`

### Logic before this class

`RedisStore` is our Redis memory.

Internally it is only:

```text
Map<String, String>
```

Every `SET` changes memory.
Every `DEL` changes memory.
RDB saves this memory.
AOF replays commands to rebuild this memory.

```java
package com.miniredis.storage;

import java.util.HashMap;
import java.util.Map;

public class RedisStore {

    // ---------------------------------------------------------
    // This HashMap represents Redis RAM.
    //
    // Example:
    // name -> mohamed
    // city -> bucharest
    //
    // In real Redis this is a highly optimized in-memory
    // dictionary written in C.
    // ---------------------------------------------------------
    private final Map<String, String> data = new HashMap<>();

    // ---------------------------------------------------------
    // SET key value
    //
    // This updates only memory.
    // Persistence is handled outside by RDB/AOF classes.
    // ---------------------------------------------------------
    public void set(String key, String value) {
        data.put(key, value);
    }

    // ---------------------------------------------------------
    // GET key
    //
    // Reads from memory.
    // Time complexity: O(1) average.
    // ---------------------------------------------------------
    public String get(String key) {
        return data.get(key);
    }

    // ---------------------------------------------------------
    // DEL key
    //
    // Removes key from memory.
    // Returns true if key existed.
    // ---------------------------------------------------------
    public boolean delete(String key) {
        return data.remove(key) != null;
    }

    // ---------------------------------------------------------
    // Used by RDB.
    //
    // RDB needs a full copy of current memory.
    // We return a defensive copy so external code cannot
    // directly mutate internal Redis memory.
    // ---------------------------------------------------------
    public Map<String, String> snapshot() {
        return new HashMap<>(data);
    }

    // ---------------------------------------------------------
    // Used during recovery.
    //
    // When RDB loads data from disk, we replace empty memory
    // with restored memory.
    // ---------------------------------------------------------
    public void load(Map<String, String> restored) {
        data.clear();
        data.putAll(restored);
    }

    // ---------------------------------------------------------
    // Helper for dry run/debugging.
    // ---------------------------------------------------------
    public int size() {
        return data.size();
    }

    // ---------------------------------------------------------
    // Helper to print current memory state.
    // Useful for understanding recovery.
    // ---------------------------------------------------------
    public Map<String, String> debugData() {
        return new HashMap<>(data);
    }
}
```

---

## 8.2 `RdbSnapshot.java`

### Logic before this class

RDB is a **snapshot**.

It stores the current final memory state.

Example memory:

```text
name -> mohamed
city -> bucharest
```

RDB saves:

```text
{name=mohamed, city=bucharest}
```

RDB does **not** store every command.

```java
package com.miniredis.persistence;

import com.miniredis.storage.RedisStore;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RdbSnapshot {

    private final File file;

    public RdbSnapshot(String path) {
        this.file = new File(path);
    }

    // ---------------------------------------------------------
    // Save current memory state to disk.
    //
    // This is like taking a photo of Redis RAM.
    //
    // Memory:
    // name -> mohamed
    // city -> bucharest
    //
    // Disk RDB:
    // {name=mohamed, city=bucharest}
    // ---------------------------------------------------------
    public void save(RedisStore store) throws IOException {

        // Ensure parent directory exists.
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        // ObjectOutputStream is simple for learning.
        // Real Redis uses a compact binary RDB format.
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(file))) {

            // store.snapshot() returns a copy of memory.
            out.writeObject(store.snapshot());
        }
    }

    // ---------------------------------------------------------
    // Load snapshot from disk into memory.
    //
    // Used during restart.
    //
    // If RDB file does not exist, recovery simply starts
    // from empty memory.
    // ---------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void load(RedisStore store)
            throws IOException, ClassNotFoundException {

        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(file))) {

            Map<String, String> restored =
                    (HashMap<String, String>) in.readObject();

            // Replace current empty memory with restored snapshot.
            store.load(restored);
        }
    }
}
```

---

## 8.3 `AofLog.java`

### Logic before this class

AOF is an **append-only command journal**.

Instead of saving the final state only, it saves every write command.

Example:

```text
SET name mohamed
SET city bucharest
DEL city
```

On restart, MiniRedis reads the file from top to bottom and applies each command again.

That rebuilds memory deterministically.

```java
package com.miniredis.persistence;

import com.miniredis.storage.RedisStore;

import java.io.*;

public class AofLog {

    private final File file;

    public AofLog(String path) {
        this.file = new File(path);
    }

    // ---------------------------------------------------------
    // Append one write command to AOF.
    //
    // Important idea:
    // For durability, write command to AOF whenever memory changes.
    //
    // Example:
    // store.set("name", "mohamed");
    // aof.append("SET name mohamed");
    //
    // Disk file becomes:
    // SET name mohamed
    // ---------------------------------------------------------
    public void append(String command) throws IOException {

        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        // true means append mode.
        // We do not overwrite old commands.
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(command);
            writer.write(System.lineSeparator());

            // In real Redis, fsync policy controls when data
            // is forced to disk:
            // always / everysec / no
        }
    }

    // ---------------------------------------------------------
    // Recover memory from AOF.
    //
    // Algorithm:
    // 1. Open AOF file.
    // 2. Read command line by line.
    // 3. Parse command.
    // 4. Apply command to empty RedisStore.
    //
    // This is same idea as replaying database WAL.
    // ---------------------------------------------------------
    public void recover(RedisStore store) throws IOException {

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                // Ignore empty lines.
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Learning format:
                // SET key value
                // DEL key
                String[] parts = line.split("\\s+");

                String command = parts[0].toUpperCase();

                if (command.equals("SET")) {

                    // Validate:
                    // SET needs 3 parts:
                    // SET name mohamed
                    if (parts.length != 3) {
                        throw new IllegalArgumentException(
                                "Invalid SET command in AOF: " + line
                        );
                    }

                    String key = parts[1];
                    String value = parts[2];

                    // Replay operation into memory.
                    store.set(key, value);

                } else if (command.equals("DEL")) {

                    // Validate:
                    // DEL needs 2 parts:
                    // DEL city
                    if (parts.length != 2) {
                        throw new IllegalArgumentException(
                                "Invalid DEL command in AOF: " + line
                        );
                    }

                    String key = parts[1];

                    // Replay delete into memory.
                    store.delete(key);

                } else {
                    throw new IllegalArgumentException(
                            "Unknown command in AOF: " + line
                    );
                }
            }
        }
    }
}
```

---

## 8.4 `Phase010Driver.java`

### Logic before this class

This driver demonstrates:

```text
1. Write data to memory
2. Save RDB snapshot
3. Append later writes to AOF
4. Simulate crash by creating a new empty RedisStore
5. Recover memory from disk
```

```java
package com.miniredis.driver;

import com.miniredis.persistence.AofLog;
import com.miniredis.persistence.RdbSnapshot;
import com.miniredis.storage.RedisStore;

public class Phase010Driver {

    public static void main(String[] args) throws Exception {

        RdbSnapshot rdb = new RdbSnapshot("data/miniredis.rdb");
        AofLog aof = new AofLog("data/miniredis.aof");

        // -----------------------------------------------------
        // STEP 1:
        // Start Redis memory.
        // -----------------------------------------------------
        RedisStore memory = new RedisStore();

        // -----------------------------------------------------
        // STEP 2:
        // Execute writes.
        //
        // Memory changes immediately.
        // AOF records the command history.
        // -----------------------------------------------------
        memory.set("name", "mohamed");
        aof.append("SET name mohamed");

        memory.set("city", "bucharest");
        aof.append("SET city bucharest");

        System.out.println("Before RDB snapshot = " + memory.debugData());

        // -----------------------------------------------------
        // STEP 3:
        // RDB saves current memory photo.
        //
        // RDB now contains:
        // name -> mohamed
        // city -> bucharest
        // -----------------------------------------------------
        rdb.save(memory);

        // -----------------------------------------------------
        // STEP 4:
        // More writes happen after RDB snapshot.
        //
        // These writes may not be inside RDB yet.
        // But they are inside AOF.
        // -----------------------------------------------------
        memory.set("city", "cluj");
        aof.append("SET city cluj");

        memory.set("role", "backend");
        aof.append("SET role backend");

        memory.delete("name");
        aof.append("DEL name");

        System.out.println("Before crash memory = " + memory.debugData());

        // -----------------------------------------------------
        // STEP 5:
        // Simulate crash.
        //
        // Process dies.
        // RAM is gone.
        // New RedisStore starts empty.
        // -----------------------------------------------------
        RedisStore recovered = new RedisStore();

        System.out.println("After crash empty memory = " + recovered.debugData());

        // -----------------------------------------------------
        // STEP 6:
        // Recover from AOF by replaying all commands.
        //
        // The AOF contains command history:
        // SET name mohamed
        // SET city bucharest
        // SET city cluj
        // SET role backend
        // DEL name
        //
        // Final memory becomes:
        // city -> cluj
        // role -> backend
        // -----------------------------------------------------
        aof.recover(recovered);

        System.out.println("After AOF recovery = " + recovered.debugData());
    }
}
```

---

## 8.5 Optional: Combined RDB + AOF Recovery

In production thinking, startup usually uses:

```text
Load compact snapshot
Then replay command log
```

Simplified combined recovery:

```java
package com.miniredis.driver;

import com.miniredis.persistence.AofLog;
import com.miniredis.persistence.RdbSnapshot;
import com.miniredis.storage.RedisStore;

public class CombinedRecoveryDriver {

    public static void main(String[] args) throws Exception {

        RedisStore store = new RedisStore();

        RdbSnapshot rdb = new RdbSnapshot("data/miniredis.rdb");
        AofLog aof = new AofLog("data/miniredis.aof");

        // 1. Load last snapshot first.
        // This is fast because it restores many keys at once.
        rdb.load(store);

        // 2. Replay AOF next.
        // This restores the latest writes not safely captured
        // by the previous snapshot.
        aof.recover(store);

        System.out.println("Recovered memory = " + store.debugData());
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
java -cp out com.miniredis.driver.Phase010Driver
```

If your shell does not expand `**`, compile individual files or use IntelliJ.

---


# 10. Step-by-Step Dry Run

## 10.1 Why RDB Works

RDB works because it saves the **current memory state**.

```text
Time T1: Redis memory

+----------------------+
| RedisStore HashMap   |
+----------------------+
| name -> mohamed      |
| city -> bucharest    |
+----------------------+
```

When RDB snapshot runs:

```text
Redis memory                    Disk
+----------------------+        +----------------------+
| name -> mohamed      | -----> | miniredis.rdb        |
| city -> bucharest    |        | {                    |
+----------------------+        |   name=mohamed,      |
                                |   city=bucharest     |
                                | }                    |
                                +----------------------+
```

After crash:

```text
RAM is gone

+----------------------+
| RedisStore HashMap   |
+----------------------+
| empty                |
+----------------------+
```

Load RDB:

```text
Disk RDB                        New memory
+----------------------+        +----------------------+
| {                    | -----> | name -> mohamed      |
|   name=mohamed,      |        | city -> bucharest    |
|   city=bucharest     |        +----------------------+
| }                    |
+----------------------+
```

So RDB is good for:

```text
fast restore
small backup
point-in-time snapshot
```

But RDB can miss writes after the snapshot.

---

## 10.2 RDB Weakness

Suppose snapshot happened at 10:00.

```text
10:00 RDB snapshot:
name -> mohamed
city -> bucharest
```

Then new writes happen:

```text
10:01 SET city cluj
10:02 SET role backend
10:03 DEL name
10:04 crash
```

RDB still has only old memory:

```text
RDB:
name -> mohamed
city -> bucharest
```

If we recover only from RDB:

```text
Recovered memory:
name -> mohamed
city -> bucharest
```

But real latest memory before crash was:

```text
city -> cluj
role -> backend
```

So RDB alone may lose recent writes.

---

## 10.3 Why AOF Works

AOF works because it stores every write command.

AOF file:

```text
SET name mohamed
SET city bucharest
SET city cluj
SET role backend
DEL name
```

Recovery starts with empty memory:

```text
Memory = {}
```

Replay line by line:

```text
Line 1: SET name mohamed

Before:
{}

After:
{name=mohamed}
```

```text
Line 2: SET city bucharest

Before:
{name=mohamed}

After:
{name=mohamed, city=bucharest}
```

```text
Line 3: SET city cluj

Before:
{name=mohamed, city=bucharest}

After:
{name=mohamed, city=cluj}
```

```text
Line 4: SET role backend

Before:
{name=mohamed, city=cluj}

After:
{name=mohamed, city=cluj, role=backend}
```

```text
Line 5: DEL name

Before:
{name=mohamed, city=cluj, role=backend}

After:
{city=cluj, role=backend}
```

Final recovered memory:

```text
+----------------------+
| RedisStore HashMap   |
+----------------------+
| city -> cluj         |
| role -> backend      |
+----------------------+
```

This matches the final state before crash.

---

## 10.4 Diagram: AOF Recovery

```text
AOF file on disk
+----------------------+
| SET name mohamed     |
| SET city bucharest   |
| SET city cluj        |
| SET role backend     |
| DEL name             |
+----------+-----------+
           |
           | replay one by one
           v
+----------------------+
| RedisStore HashMap   |
+----------------------+
| city -> cluj         |
| role -> backend      |
+----------------------+
```

AOF is good for:

```text
better durability
less data loss
exact command replay
```

But AOF can become large because it stores history.

---

## 10.5 Why Use RDB + AOF Together

Best mental model:

```text
RDB = photo of memory
AOF = video/journal of changes
```

Together:

```text
1. Load RDB snapshot quickly.
2. Replay AOF commands to reach latest state.
```

Diagram:

```text
                    Crash Recovery

Disk RDB                         Disk AOF
+----------------------+         +----------------------+
| name -> mohamed      |         | SET city cluj        |
| city -> bucharest    |         | SET role backend     |
+----------+-----------+         | DEL name             |
           |                     +----------+-----------+
           | load                           |
           v                                | replay
+----------------------+                    |
| Memory after RDB     |                    |
+----------------------+                    |
| name -> mohamed      |                    |
| city -> bucharest    |                    |
+----------+-----------+                    |
           |                                |
           +-------------<------------------+
                         apply commands
                         |
                         v
+----------------------+
| Final memory         |
+----------------------+
| city -> cluj         |
| role -> backend      |
+----------------------+
```

---

## 10.6 Full Timeline Dry Run

```text
T0: Redis starts
Memory = {}

T1: SET name mohamed
Memory = {name=mohamed}
AOF    = SET name mohamed

T2: SET city bucharest
Memory = {name=mohamed, city=bucharest}
AOF    = SET name mohamed
         SET city bucharest

T3: RDB snapshot
RDB    = {name=mohamed, city=bucharest}

T4: SET city cluj
Memory = {name=mohamed, city=cluj}
AOF    = SET name mohamed
         SET city bucharest
         SET city cluj

T5: SET role backend
Memory = {name=mohamed, city=cluj, role=backend}
AOF    = SET name mohamed
         SET city bucharest
         SET city cluj
         SET role backend

T6: DEL name
Memory = {city=cluj, role=backend}
AOF    = SET name mohamed
         SET city bucharest
         SET city cluj
         SET role backend
         DEL name

T7: crash
Memory = lost

T8: restart + AOF replay
Recovered Memory = {city=cluj, role=backend}
```

---

## 10.7 Interview Explanation

Say this:

```text
RDB is a compact snapshot of Redis memory.
It is fast to load and good for backups.

AOF is an append-only command log.
It gives better durability because every write command is recorded.

During recovery, Redis can rebuild memory by loading a snapshot
and/or replaying AOF commands.

This pattern is similar to database checkpoints plus WAL.
```

---

## 10.8 Complexity

```text
SET in memory:       O(1) average
DEL in memory:       O(1) average
AOF append:          O(1) sequential write
AOF recovery:        O(number of commands in AOF)
RDB save:            O(number of keys)
RDB load:            O(number of keys)
```

---

## 10.9 Important Production Note

Real Redis supports different persistence tradeoffs:

```text
RDB only:
fast, compact, but can lose recent writes

AOF only:
more durable, but larger and slower recovery if huge

RDB + AOF:
best practical balance for durability and recovery
```

MiniRedis teaches the same architecture in simple Java.

---

# 11. Test Commands

Try these mental or driver-level commands:

```text
AOF replay
SET
DEL
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
Log replay, deterministic state machine, recovery ordering
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
crash recovery, event sourcing replay, durable Redis-like restart
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
011
```

Continue the MiniRedis roadmap until the final production architecture.
