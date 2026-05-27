# 001_TCP_Echo_Server.md

# MiniRedis Phase 001 — TCP Echo Server

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

Build the first blocking TCP server on port 6379.

This phase is part of a progressive MiniRedis implementation. The goal is to understand how Redis-like systems evolve from simple code into production-ready infrastructure.

---

# 2. Previous Limitation

No server exists yet.

This limitation matters because a production cache/database needs networking, correctness, concurrency, durability, observability, and scaling behavior.

---

# 3. What Changed From Previous Phase

We add ServerSocket, Socket, BufferedReader, BufferedWriter, and echo response.

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
  -> Execute TCP Echo Server
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
Networking, socket lifecycle, request-response
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
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
Logic before class:
1. Open TCP port 6379.
2. Wait for a client.
3. Read one line at a time.
4. Echo the line back.
5. This verifies the network foundation before Redis commands.
*/
public class Phase001Driver {
    public static void main(String[] args) throws Exception {
        try (ServerSocket server = new ServerSocket(6379)) {
            System.out.println("MiniRedis Echo Server started on port 6379");
            while (true) {
                Socket client = server.accept();
                handle(client);
            }
        }
    }

    private static void handle(Socket client) throws IOException {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                out.write("ECHO: " + line + "\r\n");
                out.flush();
            }
        }
    }
}
```

---

# 8. How To Run

Simple mode:

```bash
javac Phase001Driver.java
java Phase001Driver
```

Package mode:

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out com.miniredis.phase001.Phase001Driver
```

---

# 9. Dry Run

Example:

```text
Input operation for TCP Echo Server
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
After : new TCP Echo Server behavior available
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
