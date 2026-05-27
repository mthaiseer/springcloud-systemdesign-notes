# 003_InMemory_KeyValue_Store.md

# MiniRedis — Phase 003: In-Memory Key-Value Store

## Clickable Index

- [1. Goal](#1-goal)
- [2. What We Built Previously](#2-what-we-built-previously)
- [3. What We Build In This Phase](#3-what-we-build-in-this-phase)
- [4. Why In-Memory Storage Matters](#4-why-in-memory-storage-matters)
- [5. Core Idea](#5-core-idea)
- [6. Architecture Diagram](#6-architecture-diagram)
- [7. File Structure](#7-file-structure)
- [8. Complete Java Code](#8-complete-java-code)
- [9. Driver Class](#9-driver-class)
- [10. Step-by-Step Dry Run](#10-step-by-step-dry-run)
- [11. How To Run](#11-how-to-run)
- [12. Test Commands](#12-test-commands)
- [13. DSA Concepts Used](#13-dsa-concepts-used)
- [14. System Design Relevance](#14-system-design-relevance)
- [15. Interview Notes](#15-interview-notes)
- [16. What Changes In Next Phase](#16-what-changes-in-next-phase)

---

# 1. Goal

In this phase, we add the first real Redis-like component: an **in-memory key-value store**.

Previously, our server could parse commands, but it did not store anything.

Now we build a storage engine that can keep data in memory:

```text
SET name mohamed
GET name
```

Expected behavior:

```text
SET name mohamed  -> OK
GET name          -> mohamed
```

This is the heart of Redis.

---

# 2. What We Built Previously

In `002_RESP_Protocol_Parser.md`, we built:

```text
Raw RESP input  --->  RespParser  --->  Command object
```

Example:

```text
*3
$3
SET
$4
name
$7
mohamed
```

Converted into:

```text
Command{name='SET', args=['name', 'mohamed']}
```

But the command object was not executed yet.

---

# 3. What We Build In This Phase

We will build:

```text
Command object  --->  RedisStore  --->  In-memory HashMap
```

Supported operations in this phase:

```text
SET key value
GET key
```

We are not yet adding:

```text
DEL
EXISTS
TTL
Persistence
Multi-client server
```

Those come in later phases.

---

# 4. Why In-Memory Storage Matters

Redis is fast because most data is stored in RAM.

Disk databases usually work like this:

```text
Request -> Disk read/write -> Response
```

Redis-style in-memory systems work like this:

```text
Request -> Memory read/write -> Response
```

Memory access is much faster than disk access.

This is why Redis is commonly used for:

```text
sessions
cache
rate limit counters
leaderboards
temporary tokens
feature flags
real-time metadata
```

---

# 5. Core Idea

At the simplest level, Redis can be imagined as:

```java
Map<String, String> store = new HashMap<>();
```

Example:

```text
Key      Value
----------------
name     mohamed
city     bucharest
role     backend
```

Then:

```text
GET name  -> mohamed
GET city  -> bucharest
```

In this phase, we keep it simple with string values only.

Later, Redis values become richer:

```text
String
List
Set
Hash
Sorted Set
Stream
Geo
```

---

# 6. Architecture Diagram

```text
+------------------+
| Client Command   |
| SET name mohamed |
+--------+---------+
         |
         v
+------------------+
| Command Object   |
| name = SET       |
| args = name,val  |
+--------+---------+
         |
         v
+------------------+
| CommandExecutor  |
| decides action   |
+--------+---------+
         |
         v
+------------------+
| RedisStore       |
| HashMap storage  |
+--------+---------+
         |
         v
+------------------+
| Response         |
| OK / value / nil |
+------------------+
```

Storage view:

```text
RedisStore
└── HashMap
    ├── name -> mohamed
    ├── city -> bucharest
    └── role -> backend
```

---

# 7. File Structure

```text
MiniRedis/
└── src/main/java/com/miniredis/
    ├── protocol/
    │   └── Command.java
    ├── storage/
    │   └── RedisStore.java
    ├── command/
    │   └── CommandExecutor.java
    └── driver/
        └── Phase003InMemoryStoreDriver.java
```

---

# 8. Complete Java Code

## 8.1 `Command.java`

### Logic before this class

This class represents a parsed Redis command.

Instead of passing raw strings everywhere, we convert input into a clean object:

```text
SET name mohamed
```

becomes:

```text
commandName = SET
args = [name, mohamed]
```

Why this is useful:

```text
1. Parser only parses input.
2. Executor only executes commands.
3. Store only stores data.
```

This separation keeps the design clean.

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
        return "Command{" +
                "name='" + name + '\'' +
                ", args=" + args +
                '}';
    }
}
```

---

## 8.2 `RedisStore.java`

### Logic before this class

This class is the actual in-memory database.

For now, it uses:

```java
HashMap<String, String>
```

Meaning:

```text
key   -> value
name  -> mohamed
city  -> bucharest
```

Important design rule:

```text
CommandExecutor should not know how data is stored internally.
```

So we hide the map behind methods:

```text
set(key, value)
get(key)
```

Later, when we add TTL, persistence, eviction, and thread safety, we can update this class without changing every command.

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

This class connects commands to the storage engine.

It receives:

```text
Command{name='SET', args=['name', 'mohamed']}
```

Then it decides:

```text
If command is SET -> call store.set(key, value)
If command is GET -> call store.get(key)
```

This is the beginning of the Redis command execution layer.

Why not put this logic inside `RedisStore`?

Because storage should only store data. It should not understand command syntax.

Good design:

```text
CommandExecutor = command routing
RedisStore      = data storage
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
        String commandName = command.getName();
        List<String> args = command.getArgs();

        return switch (commandName) {
            case "SET" -> executeSet(args);
            case "GET" -> executeGet(args);
            default -> "ERR unknown command: " + commandName;
        };
    }

    private String executeSet(List<String> args) {
        if (args.size() != 2) {
            return "ERR wrong number of arguments for SET";
        }

        String key = args.get(0);
        String value = args.get(1);

        store.set(key, value);
        return "OK";
    }

    private String executeGet(List<String> args) {
        if (args.size() != 1) {
            return "ERR wrong number of arguments for GET";
        }

        String key = args.get(0);
        String value = store.get(key);

        if (value == null) {
            return "nil";
        }

        return value;
    }
}
```

---

# 9. Driver Class

## 9.1 `Phase003InMemoryStoreDriver.java`

### Logic before this class

This driver is a simple test program.

It does not start a TCP server yet.

It directly creates command objects and sends them to the executor:

```text
Command -> CommandExecutor -> RedisStore
```

This helps us test the storage logic before mixing it with networking.

That is a very important production engineering habit:

```text
Test core logic separately before connecting network/server code.
```

```java
package com.miniredis.driver;

import com.miniredis.command.CommandExecutor;
import com.miniredis.protocol.Command;
import com.miniredis.storage.RedisStore;

import java.util.List;

public class Phase003InMemoryStoreDriver {

    public static void main(String[] args) {
        RedisStore store = new RedisStore();
        CommandExecutor executor = new CommandExecutor(store);

        Command setName = new Command("SET", List.of("name", "mohamed"));
        Command getName = new Command("GET", List.of("name"));
        Command getMissing = new Command("GET", List.of("city"));
        Command setRole = new Command("SET", List.of("role", "backend"));
        Command getRole = new Command("GET", List.of("role"));

        System.out.println("Command: " + setName);
        System.out.println("Response: " + executor.execute(setName));
        System.out.println();

        System.out.println("Command: " + getName);
        System.out.println("Response: " + executor.execute(getName));
        System.out.println();

        System.out.println("Command: " + getMissing);
        System.out.println("Response: " + executor.execute(getMissing));
        System.out.println();

        System.out.println("Command: " + setRole);
        System.out.println("Response: " + executor.execute(setRole));
        System.out.println();

        System.out.println("Command: " + getRole);
        System.out.println("Response: " + executor.execute(getRole));
        System.out.println();

        System.out.println("Store size: " + store.size());
    }
}
```

---

# 10. Step-by-Step Dry Run

## Command 1

```text
SET name mohamed
```

Internal command object:

```text
name = SET
args = [name, mohamed]
```

Executor flow:

```text
CommandExecutor.execute()
        |
        v
commandName == SET
        |
        v
executeSet(args)
        |
        v
store.set("name", "mohamed")
        |
        v
HashMap stores: name -> mohamed
```

Response:

```text
OK
```

Storage after command:

```text
+------+---------+
| Key  | Value   |
+------+---------+
| name | mohamed |
+------+---------+
```

---

## Command 2

```text
GET name
```

Executor flow:

```text
CommandExecutor.execute()
        |
        v
commandName == GET
        |
        v
executeGet(args)
        |
        v
store.get("name")
        |
        v
HashMap returns "mohamed"
```

Response:

```text
mohamed
```

---

## Command 3

```text
GET city
```

Storage does not contain `city`.

```text
HashMap lookup city -> null
```

Response:

```text
nil
```

This is similar to Redis returning nil for missing keys.

---

# 11. How To Run

From project root:

```bash
javac -d out src/main/java/com/miniredis/protocol/Command.java \
             src/main/java/com/miniredis/storage/RedisStore.java \
             src/main/java/com/miniredis/command/CommandExecutor.java \
             src/main/java/com/miniredis/driver/Phase003InMemoryStoreDriver.java
```

Run:

```bash
java -cp out com.miniredis.driver.Phase003InMemoryStoreDriver
```

Expected output:

```text
Command: Command{name='SET', args=[name, mohamed]}
Response: OK

Command: Command{name='GET', args=[name]}
Response: mohamed

Command: Command{name='GET', args=[city]}
Response: nil

Command: Command{name='SET', args=[role, backend]}
Response: OK

Command: Command{name='GET', args=[role]}
Response: backend

Store size: 2
```

---

# 12. Test Commands

For now, test using Java driver objects:

```java
new Command("SET", List.of("name", "mohamed"));
new Command("GET", List.of("name"));
new Command("GET", List.of("missing"));
```

Later, these commands will come from TCP/RESP input:

```text
SET name mohamed
GET name
```

And later still, from real Redis clients:

```bash
redis-cli SET name mohamed
redis-cli GET name
```

---

# 13. DSA Concepts Used

## HashMap

Core operation:

```text
put(key, value)
get(key)
```

Average time complexity:

```text
SET -> O(1)
GET -> O(1)
```

Why average?

Because HashMap uses hashing. In rare collision-heavy cases, lookup may cost more, but in normal usage it is very fast.

## Key idea

```text
key -> hash(key) -> bucket -> value
```

Example:

```text
name -> hash(name) -> bucket 5 -> mohamed
```

---

# 14. System Design Relevance

This phase maps directly to real-world cache systems.

Example use cases:

```text
User session cache:
session:123 -> userId:900

Rate limiter counter:
rate:user:10 -> 45

Feature flag:
feature:new-ui -> enabled

Temporary OTP:
otp:login:123 -> 482991
```

In system design interviews, Redis is often used as:

```text
API Server -> Redis Cache -> Database
```

The basic cache path:

```text
1. Check Redis.
2. If value exists, return fast.
3. If missing, read database.
4. Store result in Redis.
5. Return response.
```

---

# 15. Interview Notes

## Why is Redis fast?

Because Redis stores most data in memory and uses efficient data structures.

## Why not store everything in Redis?

Because memory is expensive and volatile compared to disk.

So Redis is commonly used for:

```text
hot data
temporary data
cache data
real-time counters
session data
```

## What happens if Redis restarts?

In this phase, all data is lost.

That is why later we add:

```text
RDB snapshots
AOF append-only log
recovery
replication
```

## Is HashMap thread-safe?

No.

This phase uses `HashMap` for simplicity.

Later, we upgrade to:

```text
ConcurrentHashMap
locks
atomic operations
thread-safe storage
```

---

# 16. What Changes In Next Phase

Next file:

```text
004_SET_GET_DEL_EXISTS.md
```

We will add more Redis commands:

```text
SET
GET
DEL
EXISTS
```

The storage engine will support:

```text
delete key
check key existence
return deleted count
return exists count
```

Architecture will evolve from:

```text
SET/GET only
```

into:

```text
basic Redis command engine
```
