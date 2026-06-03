# 006_Background_Cleanup.md

# MiniRedis Phase 6 — Background Cleanup

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
Background Cleanup
```

Purpose:

```text
Add an active cleanup worker that periodically removes expired keys.
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
TTL worked only lazily. Expired keys were removed only when accessed again, which can waste memory.
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
We add a background ExpiryCleaner thread that periodically scans keys and deletes expired entries.
```

Commands or operations covered:

```text
SET temp x EX 2
TTL temp
background cleanup
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
| Background Cleanup |
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
  -> execute Background Cleanup
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


## 8.1 `ValueEntry.java`

### Logic before this class

Previously, the store kept only:

```text
key -> value
```

TTL needs metadata:

```text
key -> value + expireAtMillis
```

`expireAtMillis = -1` means the key never expires.

```java
package com.miniredis.storage;

public class ValueEntry {
    private final String value;
    private final long expireAtMillis;

    public ValueEntry(String value, long expireAtMillis) {
        this.value = value;
        this.expireAtMillis = expireAtMillis;
    }

    public String getValue() {
        return value;
    }

    public boolean hasExpiry() {
        return expireAtMillis > 0;
    }

    public boolean isExpired() {
        return hasExpiry() && System.currentTimeMillis() >= expireAtMillis;
    }

    public long ttlSeconds() {
        if (!hasExpiry()) {
            return -1;
        }

        long remaining = expireAtMillis - System.currentTimeMillis();

        if (remaining <= 0) {
            return -2;
        }

        return remaining / 1000;
    }
}
```

---

## 8.2 `RedisStore.java`

### Logic before this class

The store now manages expiry.

Important rule:

```text
Any read checks whether the key has expired.
```

This is called lazy expiration.

```java
package com.miniredis.storage;

import java.util.HashMap;
import java.util.Map;

public class RedisStore {
    private final Map<String, ValueEntry> data = new HashMap<>();

    public void set(String key, String value) {
        validateKey(key);
        data.put(key, new ValueEntry(value, -1));
    }

    public void setWithExpiry(String key, String value, long ttlSeconds) {
        validateKey(key);

        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("TTL must be positive");
        }

        long expireAtMillis = System.currentTimeMillis() + ttlSeconds * 1000;
        data.put(key, new ValueEntry(value, expireAtMillis));
    }

    public String get(String key) {
        validateKey(key);

        ValueEntry entry = data.get(key);

        if (entry == null) {
            return null;
        }

        if (entry.isExpired()) {
            data.remove(key);
            return null;
        }

        return entry.getValue();
    }

    public boolean exists(String key) {
        return get(key) != null;
    }

    public boolean delete(String key) {
        validateKey(key);
        return data.remove(key) != null;
    }

    public long ttl(String key) {
        validateKey(key);

        ValueEntry entry = data.get(key);

        if (entry == null) {
            return -2;
        }

        if (entry.isExpired()) {
            data.remove(key);
            return -2;
        }

        return entry.ttlSeconds();
    }

    public void cleanupExpiredKeys() {
        data.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public int size() {
        return data.size();
    }

    private void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
    }
}
```

---

## 8.3 `CommandExecutor.java`

### Logic before this class

The executor now supports:

```text
SET key value
SET key value EX seconds
GET key
TTL key
DEL key
EXISTS key
```

SET has two valid forms.

```java
package com.miniredis.command;

import com.miniredis.protocol.Command;
import com.miniredis.storage.RedisStore;

import java.util.List;

public class CommandExecutor {
    private final RedisStore store;

    public CommandExecutor(RedisStore store) {
        this.store = store;
    }

    public String execute(Command command) {
        return switch (command.getName()) {
            case "SET" -> set(command.getArgs());
            case "GET" -> get(command.getArgs());
            case "TTL" -> ttl(command.getArgs());
            case "DEL" -> del(command.getArgs());
            case "EXISTS" -> exists(command.getArgs());
            default -> "ERR unknown command";
        };
    }

    private String set(List<String> args) {
        if (args.size() == 2) {
            store.set(args.get(0), args.get(1));
            return "OK";
        }

        if (args.size() == 4 && args.get(2).equalsIgnoreCase("EX")) {
            store.setWithExpiry(args.get(0), args.get(1), Long.parseLong(args.get(3)));
            return "OK";
        }

        return "ERR syntax: SET key value OR SET key value EX seconds";
    }

    private String get(List<String> args) {
        if (args.size() != 1) return "ERR wrong number of arguments for GET";
        String value = store.get(args.get(0));
        return value == null ? "nil" : value;
    }

    private String ttl(List<String> args) {
        if (args.size() != 1) return "ERR wrong number of arguments for TTL";
        return String.valueOf(store.ttl(args.get(0)));
    }

    private String del(List<String> args) {
        if (args.size() != 1) return "ERR wrong number of arguments for DEL";
        return store.delete(args.get(0)) ? "1" : "0";
    }

    private String exists(List<String> args) {
        if (args.size() != 1) return "ERR wrong number of arguments for EXISTS";
        return store.exists(args.get(0)) ? "1" : "0";
    }
}
```

---

## 8.4 `ExpiryCleaner.java`

### Logic before this class

This class runs in the background.

It periodically asks the store to remove expired keys.

Real Redis also combines lazy expiration and active expiration.

```java
package com.miniredis.expiry;

import com.miniredis.storage.RedisStore;

public class ExpiryCleaner implements Runnable {
    private final RedisStore store;
    private final long intervalMillis;
    private volatile boolean running = true;

    public ExpiryCleaner(RedisStore store, long intervalMillis) {
        this.store = store;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void run() {
        while (running) {
            store.cleanupExpiredKeys();

            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                stop();
            }
        }
    }

    public void stop() {
        running = false;
    }
}
```

---

## 8.5 `Phase006Driver.java`

### Logic before this class

The driver proves expiry behavior.

It creates a key with TTL, waits, then verifies the key disappears.

```java
package com.miniredis.driver;

import com.miniredis.command.CommandExecutor;
import com.miniredis.protocol.Command;
import com.miniredis.storage.RedisStore;
import com.miniredis.expiry.ExpiryCleaner;

import java.util.List;

public class Phase006Driver {
    public static void main(String[] args) throws Exception {
        RedisStore store = new RedisStore();
        CommandExecutor executor = new CommandExecutor(store);

        ExpiryCleaner cleaner = new ExpiryCleaner(store, 500);
        Thread cleanerThread = new Thread(cleaner, "expiry-cleaner");
        cleanerThread.setDaemon(true);
        cleanerThread.start();

        run(executor, "SET session abc EX 2");
        run(executor, "GET session");
        run(executor, "TTL session");

        Thread.sleep(2500);

        run(executor, "GET session");
        run(executor, "TTL session");

        System.out.println("Store size = " + store.size());
    }

    private static void run(CommandExecutor executor, String raw) {
        String[] parts = raw.split("\\s+");
        Command command = new Command(parts[0], List.of(parts).subList(1, parts.length));

        System.out.println("> " + raw);
        System.out.println(executor.execute(command));
        System.out.println();
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
java -cp out com.miniredis.driver.Phase006Driver
```

If your shell does not expand `**`, compile individual files or use IntelliJ.

---


# 10. Step-by-Step Dry Run

Example commands:

```text
SET temp x EX 2
TTL temp
background cleanup
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
Background Cleanup

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
SET temp x EX 2
TTL temp
background cleanup
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
Background worker pattern, scheduled scanning, lazy + active deletion
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
production cache memory hygiene, session cleanup, token expiry cleanup
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
007
```

Continue the MiniRedis roadmap until the final production architecture.
