# 004_SET_GET_DEL_EXISTS.md

# MiniRedis Phase 4 — SET GET DEL EXISTS

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
SET GET DEL EXISTS
```

Purpose:

```text
Implement the first complete core Redis command engine: SET, GET, DEL, EXISTS.
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
In Phase 003, the store supported only SET and GET. There was no delete operation, no existence check, and no Redis-like command result semantics.
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
We add command dispatching for SET, GET, DEL, and EXISTS, and return Redis-like responses such as OK, nil, 1, and 0.
```

Commands or operations covered:

```text
SET key value
GET key
DEL key
EXISTS key
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
| SET GET DEL EXISTS |
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
  -> execute SET GET DEL EXISTS
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


## 8.1 `Command.java`

### Logic before this class

This class represents a parsed command.

Example:

```text
SET name mohamed
```

becomes:

```text
name = SET
args = [name, mohamed]
```

The command object keeps parsing separate from execution.

```java
package com.miniredis.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Command {
    private final String name;
    private final List<String> args;

    public Command(String name, List<String> args) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Command name cannot be empty");
        }
        this.name = name.toUpperCase();
        this.args = new ArrayList<>(args);
    }

    public String getName() {
        return name;
    }

    public List<String> getArgs() {
        return Collections.unmodifiableList(args);
    }

    @Override
    public String toString() {
        return "Command{name='" + name + "', args=" + args + '}';
    }
}
```

---

## 8.2 `RedisStore.java`

### Logic before this class

This is the in-memory database.

It hides the internal `HashMap` behind methods.

Commands should not directly modify the map.

```java
package com.miniredis.storage;

import java.util.HashMap;
import java.util.Map;

public class RedisStore {
    private final Map<String, String> data = new HashMap<>();

    public void set(String key, String value) {
        validateKey(key);
        data.put(key, value);
    }

    public String get(String key) {
        validateKey(key);
        return data.get(key);
    }

    public boolean delete(String key) {
        validateKey(key);
        return data.remove(key) != null;
    }

    public boolean exists(String key) {
        validateKey(key);
        return data.containsKey(key);
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

The executor is the command router.

It receives a `Command` and decides which storage method to call.

```text
SET -> store.set()
GET -> store.get()
DEL -> store.delete()
EXISTS -> store.exists()
```

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
            case "SET" -> executeSet(command.getArgs());
            case "GET" -> executeGet(command.getArgs());
            case "DEL" -> executeDel(command.getArgs());
            case "EXISTS" -> executeExists(command.getArgs());
            default -> "ERR unknown command: " + command.getName();
        };
    }

    private String executeSet(List<String> args) {
        if (args.size() != 2) {
            return "ERR wrong number of arguments for SET";
        }
        store.set(args.get(0), args.get(1));
        return "OK";
    }

    private String executeGet(List<String> args) {
        if (args.size() != 1) {
            return "ERR wrong number of arguments for GET";
        }
        String value = store.get(args.get(0));
        return value == null ? "nil" : value;
    }

    private String executeDel(List<String> args) {
        if (args.size() != 1) {
            return "ERR wrong number of arguments for DEL";
        }
        return store.delete(args.get(0)) ? "1" : "0";
    }

    private String executeExists(List<String> args) {
        if (args.size() != 1) {
            return "ERR wrong number of arguments for EXISTS";
        }
        return store.exists(args.get(0)) ? "1" : "0";
    }
}
```

---

## 8.4 `Phase004SetGetDelExistsDriver.java`

### Logic before this class

This driver tests the complete core command flow without TCP.

That keeps business logic easy to debug.

```java
package com.miniredis.driver;

import com.miniredis.command.CommandExecutor;
import com.miniredis.protocol.Command;
import com.miniredis.storage.RedisStore;

import java.util.List;

public class Phase004SetGetDelExistsDriver {
    public static void main(String[] args) {
        RedisStore store = new RedisStore();
        CommandExecutor executor = new CommandExecutor(store);

        run(executor, new Command("SET", List.of("name", "mohamed")));
        run(executor, new Command("GET", List.of("name")));
        run(executor, new Command("EXISTS", List.of("name")));
        run(executor, new Command("DEL", List.of("name")));
        run(executor, new Command("GET", List.of("name")));
        run(executor, new Command("EXISTS", List.of("name")));

        System.out.println("Store size = " + store.size());
    }

    private static void run(CommandExecutor executor, Command command) {
        System.out.println("Command: " + command);
        System.out.println("Response: " + executor.execute(command));
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
java -cp out com.miniredis.driver.Phase004Driver
```

If your shell does not expand `**`, compile individual files or use IntelliJ.

---


# 10. Step-by-Step Dry Run

Example commands:

```text
SET key value
GET key
DEL key
EXISTS key
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
SET GET DEL EXISTS

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
SET key value
GET key
DEL key
EXISTS key
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
HashMap operations, command dispatch, validation, O(1) average lookup/delete
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
basic cache, session store, feature flags, API response cache
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
005
```

Continue the MiniRedis roadmap until the final production architecture.
