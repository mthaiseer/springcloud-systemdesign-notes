# 027_GeoHash.md

# MiniRedis Phase 027 — GeoHash

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

Store location coordinates and query nearby cells.

This phase is part of a progressive MiniRedis implementation. The goal is to understand how Redis-like systems evolve from simple code into production-ready infrastructure.

---

# 2. Previous Limitation

No geo indexing.

This limitation matters because a production cache/database needs networking, correctness, concurrency, durability, observability, and scaling behavior.

---

# 3. What Changed From Previous Phase

We add simple geohash/cell-based indexing.

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
  -> Execute GeoHash
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
Spatial hashing, grid indexing
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

/*
Logic before class:
GeoIndex stores points in coarse grid cells.
It approximates geohash indexing for nearby search.
*/
class GeoIndex {
    static class Point {
        String id;
        double lat;
        double lon;

        Point(String id, double lat, double lon) {
            this.id = id;
            this.lat = lat;
            this.lon = lon;
        }
    }

    private final Map<String, List<Point>> cells = new HashMap<>();

    void add(String id, double lat, double lon) {
        cells.computeIfAbsent(cell(lat, lon), c -> new ArrayList<>()).add(new Point(id, lat, lon));
    }

    List<String> nearby(double lat, double lon) {
        List<String> result = new ArrayList<>();
        for (Point p : cells.getOrDefault(cell(lat, lon), List.of())) {
            result.add(p.id);
        }
        return result;
    }

    private String cell(double lat, double lon) {
        return ((int)(lat * 10)) + ":" + ((int)(lon * 10));
    }
}

/*
Logic before class:
Driver models nearest driver search.
*/
public class Phase027Driver {
    public static void main(String[] args) {
        GeoIndex geo = new GeoIndex();
        geo.add("driver-1", 44.437, 26.102);
        geo.add("driver-2", 44.438, 26.103);
        System.out.println(geo.nearby(44.437, 26.102));
    }
}
```

---

# 8. How To Run

Simple mode:

```bash
javac Phase027Driver.java
java Phase027Driver
```

Package mode:

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out com.miniredis.phase027.Phase027Driver
```

---

# 9. Dry Run

Example:

```text
Input operation for GeoHash
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
After : new GeoHash behavior available
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
