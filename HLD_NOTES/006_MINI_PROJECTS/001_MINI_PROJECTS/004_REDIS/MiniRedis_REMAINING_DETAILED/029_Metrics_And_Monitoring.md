# 029_Metrics_And_Monitoring.md

# MiniRedis Phase 29 — Metrics And Monitoring

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
Metrics And Monitoring
```

Purpose:

```text
Add metrics for commands, latency, hits, misses, errors, and memory.
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
The system worked but had no visibility.
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
We add MetricsRegistry and monitoring output.
```

Commands or operations covered:

```text
INFO
METRICS
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
| Metrics And Monitoring |
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
  -> execute Metrics And Monitoring
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


## 8.1 `MetricsRegistry.java`

### Logic before this class

Production systems need observability.

Metrics tell us:

```text
how many commands
how many hits
how many misses
how many errors
average latency
```

```java
package com.miniredis.metrics;

import java.util.concurrent.atomic.AtomicLong;

public class MetricsRegistry {
    private final AtomicLong commands = new AtomicLong();
    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();
    private final AtomicLong errors = new AtomicLong();

    public void recordCommand() {
        commands.incrementAndGet();
    }

    public void recordHit() {
        hits.incrementAndGet();
    }

    public void recordMiss() {
        misses.incrementAndGet();
    }

    public void recordError() {
        errors.incrementAndGet();
    }

    public String report() {
        return "commands=" + commands.get() +
                ", hits=" + hits.get() +
                ", misses=" + misses.get() +
                ", errors=" + errors.get();
    }
}
```

---

## 8.2 `Phase029Driver.java`

### Logic before this class

The driver simulates runtime metrics.

```java
package com.miniredis.driver;

import com.miniredis.metrics.MetricsRegistry;

public class Phase029Driver {
    public static void main(String[] args) {
        MetricsRegistry metrics = new MetricsRegistry();

        metrics.recordCommand();
        metrics.recordHit();

        metrics.recordCommand();
        metrics.recordMiss();

        metrics.recordCommand();
        metrics.recordError();

        System.out.println(metrics.report());
    }
}
```

---

## 8.3 k6 Load Test Script

### Logic before code

This script sends repeated TCP-like HTTP requests if you expose MiniRedis through a simple HTTP wrapper.

For raw TCP testing, use custom Java/JMH/netcat clients.

```javascript
import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
  vus: 50,
  duration: '30s',
};

export default function () {
  const res = http.get('http://localhost:8080/ping');

  check(res, {
    'status is 200': r => r.status === 200,
  });

  sleep(1);
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
java -cp out com.miniredis.driver.Phase029Driver
```

If your shell does not expand `**`, compile individual files or use IntelliJ.

---


# 10. Step-by-Step Dry Run

Example commands:

```text
INFO
METRICS
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
Metrics And Monitoring

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
INFO
METRICS
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
Counters, histograms concept, moving averages
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
Prometheus/Grafana, production observability, SRE debugging
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
030
```

Continue the MiniRedis roadmap until the final production architecture.
