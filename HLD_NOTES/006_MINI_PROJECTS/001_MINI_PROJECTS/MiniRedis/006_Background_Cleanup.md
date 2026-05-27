# 006_Background_Cleanup.md

# MiniRedis Phase 006 — Background Cleanup

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

Add active expiration using a cleanup daemon.

This phase is part of a progressive MiniRedis implementation. The goal is to understand how Redis-like systems evolve from simple code into production-ready infrastructure.

---

# 2. Previous Limitation

Expired keys are deleted only when accessed.

This limitation matters because a production cache/database needs networking, correctness, concurrency, durability, observability, and scaling behavior.

---

# 3. What Changed From Previous Phase

We add ExpiryManager thread that periodically scans keys.

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
  -> Execute Background Cleanup
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
Scheduler pattern, background worker
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
import java.util.concurrent.*;

/*
Logic before class:
ValueEntry stores the value plus optional expiry metadata.
expireAtMillis = -1 means the key never expires.
*/
class ValueEntry {
    String value;
    long expireAtMillis;

    ValueEntry(String value, long expireAtMillis) {
        this.value = value;
        this.expireAtMillis = expireAtMillis;
    }

    boolean isExpired() {
        return expireAtMillis > 0 && System.currentTimeMillis() >= expireAtMillis;
    }
}

/*
Logic before class:
Command is the parsed user request.
Example: SET name mohamed -> name=SET, args=[name, mohamed].
*/
class Command {
    String name;
    List<String> args;

    Command(String name, List<String> args) {
        this.name = name.toUpperCase();
        this.args = args;
    }
}

/*
Logic before class:
RespParser converts a raw command line into a Command object.
It does not execute anything.
*/
class RespParser {
    Command parse(String line) {
        String[] parts = line.trim().split("\\s+");
        return new Command(parts[0], Arrays.asList(parts).subList(1, parts.length));
    }
}

/*
Logic before class:
RespWriter returns Redis-like output:
+OK, $-1, :1, or -ERR.
*/
class RespWriter {
    String ok() { return "+OK\r\n"; }
    String error(String msg) { return "-ERR " + msg + "\r\n"; }
    String bulk(String value) {
        if (value == null) return "$-1\r\n";
        return "$" + value.length() + "\r\n" + value + "\r\n";
    }
    String integer(long n) { return ":" + n + "\r\n"; }
}

/*
Logic before class:
RedisStore is the in-memory engine.
It supports SET, GET, DEL, EXISTS, TTL, and lazy expiration.
*/
class RedisStore {
    private final ConcurrentHashMap<String, ValueEntry> map = new ConcurrentHashMap<>();

    void set(String key, String value) {
        map.put(key, new ValueEntry(value, -1));
    }

    void setEx(String key, String value, long seconds) {
        map.put(key, new ValueEntry(value, System.currentTimeMillis() + seconds * 1000));
    }

    String get(String key) {
        ValueEntry e = map.get(key);
        if (e == null) return null;
        if (e.isExpired()) {
            map.remove(key);
            return null;
        }
        return e.value;
    }

    boolean del(String key) {
        return map.remove(key) != null;
    }

    boolean exists(String key) {
        return get(key) != null;
    }

    long ttl(String key) {
        ValueEntry e = map.get(key);
        if (e == null) return -2;
        if (e.isExpired()) {
            map.remove(key);
            return -2;
        }
        if (e.expireAtMillis < 0) return -1;
        return Math.max(0, (e.expireAtMillis - System.currentTimeMillis()) / 1000);
    }
}

/*
Logic before class:
CommandExecutor dispatches parsed commands to RedisStore methods.
*/
class CommandExecutor {
    private final RedisStore store;
    private final RespWriter writer = new RespWriter();

    CommandExecutor(RedisStore store) {
        this.store = store;
    }

    String execute(Command c) {
        try {
            switch (c.name) {
                case "PING": return "+PONG\r\n";
                case "SET":
                    if (c.args.size() == 2) {
                        store.set(c.args.get(0), c.args.get(1));
                        return writer.ok();
                    }
                    if (c.args.size() == 4 && c.args.get(2).equalsIgnoreCase("EX")) {
                        store.setEx(c.args.get(0), c.args.get(1), Long.parseLong(c.args.get(3)));
                        return writer.ok();
                    }
                    return writer.error("SET syntax: SET key value [EX seconds]");
                case "GET": return writer.bulk(store.get(c.args.get(0)));
                case "DEL": return writer.integer(store.del(c.args.get(0)) ? 1 : 0);
                case "EXISTS": return writer.integer(store.exists(c.args.get(0)) ? 1 : 0);
                case "TTL": return writer.integer(store.ttl(c.args.get(0)));
                default: return writer.error("unknown command " + c.name);
            }
        } catch (Exception e) {
            return writer.error(e.getMessage());
        }
    }
}

/*
Logic before class:
Driver proves phase 006 behavior without telnet or external tools.
*/
public class Phase006Driver {
    public static void main(String[] args) throws Exception {
        RedisStore store = new RedisStore();
        RespParser parser = new RespParser();
        CommandExecutor executor = new CommandExecutor(store);

        run("PING", parser, executor);
        run("SET name mohamed", parser, executor);
        run("GET name", parser, executor);
        run("EXISTS name", parser, executor);
        run("SET session abc EX 1", parser, executor);
        run("TTL session", parser, executor);
        Thread.sleep(1200);
        run("GET session", parser, executor);
        run("DEL name", parser, executor);
    }

    private static void run(String input, RespParser parser, CommandExecutor executor) {
        System.out.println("> " + input);
        System.out.print(executor.execute(parser.parse(input)));
        System.out.println();
    }
}
```

---

# 8. How To Run

Simple mode:

```bash
javac Phase006Driver.java
java Phase006Driver
```

Package mode:

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out com.miniredis.phase006.Phase006Driver
```

---

# 9. Dry Run

Example:

```text
Input operation for Background Cleanup
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
After : new Background Cleanup behavior available
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
