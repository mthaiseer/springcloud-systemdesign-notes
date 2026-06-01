# 008_RDB_Snapshot.md

# MiniRedis Phase 8 — RDB Snapshot

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
RDB Snapshot
```

Purpose:

```text
Persist the current in-memory state into a snapshot file.
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
All data was lost when the process stopped.
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
We add RDB-style snapshot save/load, similar to a compact point-in-time backup.
```

Commands or operations covered:

```text
SAVE
LOAD
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
| RDB Snapshot     |
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
  -> execute RDB Snapshot
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

public class RedisStore {
    private final Map<String, String> data = new HashMap<>();

    public void set(String key, String value) {
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }

    public boolean delete(String key) {
        return data.remove(key) != null;
    }

    public Map<String, String> snapshot() {
        return new HashMap<>(data);
    }

    public void load(Map<String, String> restored) {
        data.clear();
        data.putAll(restored);
    }

    public int size() {
        return data.size();
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

public class RdbSnapshot {
    private final File file;

    public RdbSnapshot(String path) {
        this.file = new File(path);
    }

    public void save(RedisStore store) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(store.snapshot());
        }
    }

    @SuppressWarnings("unchecked")
    public void load(RedisStore store) throws IOException, ClassNotFoundException {
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Map<String, String> restored = (HashMap<String, String>) in.readObject();
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

public class AofLog {
    private final File file;

    public AofLog(String path) {
        this.file = new File(path);
    }

    public void append(String command) throws IOException {
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(command);
            writer.write(System.lineSeparator());
        }
    }

    public void recover(RedisStore store) throws IOException {
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");

                if (parts[0].equalsIgnoreCase("SET")) {
                    store.set(parts[1], parts[2]);
                } else if (parts[0].equalsIgnoreCase("DEL")) {
                    store.delete(parts[1]);
                }
            }
        }
    }
}
```

---

## 8.4 `Phase008Driver.java`

### Logic before this class

The driver demonstrates persistence.

For snapshot, it saves the full state.

For AOF, it appends write commands and recovers by replay.

```java
package com.miniredis.driver;

import com.miniredis.persistence.AofLog;
import com.miniredis.persistence.RdbSnapshot;
import com.miniredis.storage.RedisStore;

public class Phase008Driver {
    public static void main(String[] args) throws Exception {
        RedisStore store = new RedisStore();
        RdbSnapshot snapshot = new RdbSnapshot("data/miniredis.rdb");
        AofLog aof = new AofLog("data/miniredis.aof");

        store.set("name", "mohamed");
        aof.append("SET name mohamed");

        store.set("city", "bucharest");
        aof.append("SET city bucharest");

        snapshot.save(store);

        RedisStore recoveredFromSnapshot = new RedisStore();
        snapshot.load(recoveredFromSnapshot);
        System.out.println("Snapshot name = " + recoveredFromSnapshot.get("name"));

        RedisStore recoveredFromAof = new RedisStore();
        aof.recover(recoveredFromAof);
        System.out.println("AOF city = " + recoveredFromAof.get("city"));
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
java -cp out com.miniredis.driver.Phase008Driver
```

If your shell does not expand `**`, compile individual files or use IntelliJ.

---


# 10. Step-by-Step Dry Run

Example commands:

```text
SAVE
LOAD
```

We will dry run the exact driver flow from `Phase008Driver.java`.

---

## 10.1 Initial State

When the driver starts:

```java
RedisStore store = new RedisStore();
RdbSnapshot snapshot = new RdbSnapshot("data/miniredis.rdb");
AofLog aof = new AofLog("data/miniredis.aof");
```

Memory state:

```text
store = {}
```

Disk state:

```text
data/miniredis.rdb  -> not created yet or old snapshot
data/miniredis.aof  -> not created yet or old log
```

Mental model:

```text
RedisStore = RAM
RdbSnapshot = full memory photo
AofLog = write command diary
```

---

## 10.2 Step 1 — SET name mohamed

Driver executes:

```java
store.set("name", "mohamed");
aof.append("SET name mohamed");
```

What happens in memory:

```java
data.put("name", "mohamed");
```

Memory after operation:

```text
{
  "name": "mohamed"
}
```

What happens in AOF:

```text
SET name mohamed
```

So now:

```text
RAM has latest value
AOF has write history
RDB still not saved yet
```

Important observation:

```text
SET updates memory first.
AOF records the command so it can be replayed later.
```

---

## 10.3 Step 2 — SET city bucharest

Driver executes:

```java
store.set("city", "bucharest");
aof.append("SET city bucharest");
```

What happens in memory:

```java
data.put("city", "bucharest");
```

Memory after operation:

```text
{
  "name": "mohamed",
  "city": "bucharest"
}
```

AOF file after operation:

```text
SET name mohamed
SET city bucharest
```

Now AOF can rebuild the store by replaying commands from top to bottom.

Replay idea:

```text
Start with empty map
Replay SET name mohamed
Replay SET city bucharest
Final map is restored
```

---

## 10.4 Step 3 — SAVE snapshot

Driver executes:

```java
snapshot.save(store);
```

Inside `save()`:

```java
try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
    out.writeObject(store.snapshot());
}
```

Now check `store.snapshot()`:

```java
public Map<String, String> snapshot() {
    return new HashMap<>(data);
}
```

Why return new HashMap?

```text
To create a safe copy of current memory state.
```

Current memory:

```text
{
  "name": "mohamed",
  "city": "bucharest"
}
```

Snapshot copy:

```text
{
  "name": "mohamed",
  "city": "bucharest"
}
```

Then Java serializes this copied map into:

```text
data/miniredis.rdb
```

After SAVE:

```text
RAM:
{
  "name": "mohamed",
  "city": "bucharest"
}

RDB:
snapshot of full map

AOF:
SET name mohamed
SET city bucharest
```

Mental model:

```text
RDB = photo of final memory
AOF = diary of how memory reached that state
```

---

## 10.5 Step 4 — Recover from RDB snapshot

Driver creates a new empty store:

```java
RedisStore recoveredFromSnapshot = new RedisStore();
```

Initial recovered store:

```text
{}
```

Then driver executes:

```java
snapshot.load(recoveredFromSnapshot);
```

Inside `load()`:

```java
if (!file.exists()) {
    return;
}
```

If snapshot file exists, Java reads the saved map:

```java
Map<String, String> restored = (HashMap<String, String>) in.readObject();
```

Restored map from disk:

```text
{
  "name": "mohamed",
  "city": "bucharest"
}
```

Then:

```java
store.load(restored);
```

Inside `RedisStore.load()`:

```java
data.clear();
data.putAll(restored);
```

So recovered store becomes:

```text
{
  "name": "mohamed",
  "city": "bucharest"
}
```

Driver prints:

```java
System.out.println("Snapshot name = " + recoveredFromSnapshot.get("name"));
```

Output:

```text
Snapshot name = mohamed
```

Important observation:

```text
RDB recovery does not replay commands.
It directly loads the final saved state.
```

---

## 10.6 Step 5 — Recover from AOF

Driver creates another empty store:

```java
RedisStore recoveredFromAof = new RedisStore();
```

Initial recovered store:

```text
{}
```

Then:

```java
aof.recover(recoveredFromAof);
```

Inside `recover()`:

```java
while ((line = reader.readLine()) != null) {
    String[] parts = line.split("\\s+");

    if (parts[0].equalsIgnoreCase("SET")) {
        store.set(parts[1], parts[2]);
    } else if (parts[0].equalsIgnoreCase("DEL")) {
        store.delete(parts[1]);
    }
}
```

AOF line 1:

```text
SET name mohamed
```

Parsed as:

```text
parts[0] = SET
parts[1] = name
parts[2] = mohamed
```

Apply:

```java
store.set("name", "mohamed");
```

Recovered AOF store:

```text
{
  "name": "mohamed"
}
```

AOF line 2:

```text
SET city bucharest
```

Parsed as:

```text
parts[0] = SET
parts[1] = city
parts[2] = bucharest
```

Apply:

```java
store.set("city", "bucharest");
```

Recovered AOF store:

```text
{
  "name": "mohamed",
  "city": "bucharest"
}
```

Driver prints:

```java
System.out.println("AOF city = " + recoveredFromAof.get("city"));
```

Output:

```text
AOF city = bucharest
```

Important observation:

```text
AOF recovery rebuilds memory by replaying write commands.
```

---

## 10.7 Full Execution Timeline

```text
Start
  |
  v
Create empty RedisStore
  |
  v
SET name mohamed
  |
  +--> RAM: name=mohamed
  +--> AOF: append SET name mohamed
  |
  v
SET city bucharest
  |
  +--> RAM: city=bucharest
  +--> AOF: append SET city bucharest
  |
  v
SAVE RDB
  |
  +--> RDB stores full map snapshot
  |
  v
Create new empty store
  |
  v
LOAD RDB
  |
  +--> memory restored directly from snapshot
  |
  v
Create another empty store
  |
  v
Replay AOF
  |
  +--> memory restored command by command
```

---

## 10.8 RDB vs AOF In This File

| Feature | RDB Snapshot | AOF Log |
|---|---|---|
| Stores | Full final map | Every write command |
| Recovery | Load saved map | Replay commands |
| File style | Snapshot file | Log file |
| Faster recovery | Usually yes | Can be slower if log is big |
| Data loss risk | Higher between snapshots | Lower if appended frequently |
| MiniRedis method | `snapshot.save()` / `snapshot.load()` | `aof.append()` / `aof.recover()` |

---

## 10.9 Final Output

Expected output from this driver:

```text
Snapshot name = mohamed
AOF city = bucharest
```

Why?

```text
RDB restored name from the saved full snapshot.
AOF restored city by replaying SET city bucharest.
```

---

## 10.10 Key Interview Explanation

You can explain this phase like this:

```text
In MiniRedis, RDB is implemented as a full HashMap snapshot written to disk.
On load, we deserialize the map and replace the in-memory state.

AOF is implemented as an append-only command log.
Every write command is appended to disk.
On recovery, we replay each command to rebuild the HashMap.
```

This gives the same mental model as Redis:

```text
RDB = periodic checkpoint
AOF = write-ahead command history
```

Visual execution:

```text
Command
  -> validate
  -> execute in memory
  -> persist using RDB or AOF
  -> recover by snapshot load or command replay
  -> return response
```


---

# 11. Test Commands

Try these mental or driver-level commands:

```text
SAVE
LOAD
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
Serialization, file IO, snapshotting, full-state checkpoint
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
cache warm restart, backup, disaster recovery checkpoint
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
009
```

Continue the MiniRedis roadmap until the final production architecture.
