# 012_Command_Executor_ThreadPool.md

# MiniRedis Phase 12 — Command Executor ThreadPool

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
Command Executor ThreadPool
```

Purpose:

```text
Move command execution into a bounded worker pool.
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
Thread-per-client can explode under many connections and each client thread does both IO and command work.
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
We add ExecutorService so command execution can be controlled with a worker pool.
```

Commands or operations covered:

```text
submit command task
worker executes
response returned
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
| Command Executor ThreadPool |
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
  -> execute Command Executor ThreadPool
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


## 8.1 `MiniRedisServer.java`

### Class Summary

`MiniRedisServer` is the main TCP server for this phase.

It has two separate responsibilities:

```text
1. Accept client connections.
2. Submit each command to a bounded worker pool.
```

Important mental model:

```text
clientPool   = handles connected clients
commandPool  = handles actual command execution
```

Why this design matters:

```text
Without commandPool:
client thread does everything.

With commandPool:
client thread reads input,
worker thread executes command.
```

This is the first step toward controlled concurrency and backpressure.

```java
package com.miniredis.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MiniRedisServer {

    // TCP port where MiniRedis listens.
    // Redis default port is 6379, so we use the same for learning.
    private final int port;

    // This pool accepts many client connections.
    // cachedThreadPool creates threads when needed and reuses idle threads.
    // Mental model:
    // each connected client gets one handler task.
    private final ExecutorService clientPool = Executors.newCachedThreadPool();

    // This pool executes actual Redis commands.
    // Fixed size means only 4 commands execute at the same time.
    // Extra commands wait in the internal queue.
    // This prevents unlimited command execution threads.
    private final ExecutorService commandPool = Executors.newFixedThreadPool(4);

    public MiniRedisServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {

        // ServerSocket opens a TCP server port.
        // Clients can connect using telnet, nc, or a Redis client later.
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("MiniRedis server started on port " + port);

            // Server should keep running forever.
            // Each accept() waits until a new client connects.
            while (true) {

                // Blocking call:
                // waits for a new client TCP connection.
                Socket client = serverSocket.accept();

                // Do NOT handle client in the main server thread.
                // Submit it to clientPool so the server can quickly return
                // to accept more clients.
                clientPool.submit(() -> handleClient(client));
            }
        }
    }

    private void handleClient(Socket client) {

        // Each client has its own input stream and output stream.
        // reader reads commands from client.
        // writer sends response back to same client.
        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(client.getInputStream())
            );
            PrintWriter writer = new PrintWriter(
                client.getOutputStream(),
                true // autoFlush=true, so response is sent immediately
            )
        ) {
            String line;

            // One client can send many commands over same connection.
            // Example:
            // PING
            // SET name mohamed
            // GET name
            while ((line = reader.readLine()) != null) {

                // Must copy line into effectively-final variable
                // because lambda uses it inside commandPool.submit().
                String commandLine = line;

                // Submit command execution to bounded worker pool.
                // This separates IO handling from CPU/command work.
                commandPool.submit(() -> {

                    // Worker thread executes command.
                    String response = execute(commandLine);

                    // Same worker writes result back to the client socket.
                    // In production, writing from multiple workers to same writer
                    // may need ordering guarantees.
                    writer.println(response);
                });
            }

        } catch (IOException e) {
            // Client disconnected, network failed, or stream closed.
            // Server should not crash because one client failed.
            System.err.println("Client error: " + e.getMessage());
        }
    }

    private String execute(String commandLine) {

        // Very small command executor for this phase.
        // Later phases will replace this with real parser + command classes.
        if (commandLine.equalsIgnoreCase("PING")) {
            return "PONG";
        }

        // For now, echo command back to prove the worker executed it.
        return "OK received: " + commandLine;
    }

    public static void main(String[] args) throws Exception {

        // Start server on Redis-like port.
        new MiniRedisServer(6379).start();
    }
}
```

### In-Memory Meaning Of This Class

```text
Main Thread
  owns ServerSocket
  accepts clients

Client Handler Threads
  read commands from each socket

Command Worker Threads
  execute commands from queue
```

Diagram:

```text
Client A ---- socket ----> clientPool thread A ----+
                                                     |
Client B ---- socket ----> clientPool thread B ----+----> commandPool queue
                                                     |
Client C ---- socket ----> clientPool thread C ----+

commandPool queue:
[PING from A] [SET x 1 from B] [GET x from C]

Workers:
Worker-1 executes PING
Worker-2 executes SET
Worker-3 executes GET
Worker-4 idle or executes next command
```

---

## 8.2 `Phase012Driver.java`

### Class Summary

`Phase012Driver` is only a small launcher class.

Its job:

```text
create MiniRedisServer
start it on port 6379
```

It keeps the learning phase clean because you can run this driver directly.

```java
package com.miniredis.driver;

import com.miniredis.server.MiniRedisServer;

public class Phase012Driver {

    public static void main(String[] args) throws Exception {

        // Create and start the MiniRedis TCP server.
        // The program will keep running because server.start()
        // contains an infinite accept loop.
        new MiniRedisServer(6379).start();
    }
}
```

### In-Memory Meaning Of This Class

```text
JVM starts
  |
  v
main() runs
  |
  v
MiniRedisServer object created
  |
  v
server.start()
  |
  v
ServerSocket waits for clients
```

# 9. How To Run

From project root:

```bash
javac -d out src/main/java/com/miniredis/**/*.java
```

Run the phase driver:

```bash
java -cp out com.miniredis.driver.Phase012Driver
```

If your shell does not expand `**`, compile individual files or use IntelliJ.

---


# 10. Step-by-Step Dry Run

This phase is about separating:

```text
client connection handling
from
command execution
```

Before this phase:

```text
Client Thread
  -> read command
  -> execute command
  -> write response
```

Problem:

```text
If command is slow,
that client thread is stuck doing command work.
```

After this phase:

```text
Client Thread
  -> read command
  -> submit command to commandPool

Worker Thread
  -> execute command
  -> write response
```

---

## Dry Run 1 — Server Startup

Run:

```bash
java -cp out com.miniredis.driver.Phase012Driver
```

Memory state:

```text
JVM memory

MiniRedisServer object
├── port = 6379
├── clientPool = cached thread pool
└── commandPool = fixed thread pool with 4 workers
```

Execution:

```text
main()
  -> new MiniRedisServer(6379)
  -> start()
  -> new ServerSocket(6379)
  -> while(true) accept clients
```

Diagram:

```text
+-------------------+
| Main Thread       |
| ServerSocket 6379 |
| accept() loop     |
+---------+---------+
          |
          v
 waits for client connection
```

---

## Dry Run 2 — Client A Connects

Client A:

```bash
nc localhost 6379
```

Server flow:

```text
serverSocket.accept()
  -> returns Socket(Client A)
  -> clientPool.submit(handleClient(Client A))
```

Memory diagram:

```text
clientPool
└── ClientHandler-A thread
    ├── reader = Client A input stream
    └── writer = Client A output stream
```

Important:

```text
Main server thread is free again.
It can accept Client B immediately.
```

---

## Dry Run 3 — Client A Sends PING

Input:

```text
PING
```

Client handler reads:

```java
line = "PING"
```

Then submits task:

```text
commandPool.submit(() -> execute("PING"))
```

Queue state:

```text
commandPool queue
└── Task-1: execute("PING") for Client A
```

Worker executes:

```text
Worker-1 takes Task-1
  -> execute("PING")
  -> returns "PONG"
  -> writer.println("PONG")
```

Client sees:

```text
PONG
```

Diagram:

```text
Client A
  |
  | PING
  v
ClientHandler-A
  |
  | submit task
  v
commandPool queue
  |
  | Worker-1 picks
  v
execute("PING")
  |
  v
PONG returned to Client A
```

---

## Dry Run 4 — Multiple Clients Together

Suppose three clients send commands at almost same time.

```text
Client A sends: PING
Client B sends: SET name mohamed
Client C sends: GET name
```

Client handler threads:

```text
ClientHandler-A reads PING
ClientHandler-B reads SET name mohamed
ClientHandler-C reads GET name
```

They submit tasks:

```text
commandPool queue
├── Task-1: PING from Client A
├── Task-2: SET name mohamed from Client B
└── Task-3: GET name from Client C
```

Workers process:

```text
Worker-1 -> Task-1 -> PONG
Worker-2 -> Task-2 -> OK received: SET name mohamed
Worker-3 -> Task-3 -> OK received: GET name
Worker-4 -> idle
```

Diagram:

```text
          +----------------+
Client A -> ClientHandlerA | -- Task-1 --+
          +----------------+            |
                                        v
          +----------------+     +-------------------+
Client B -> ClientHandlerB | --> | commandPool queue |
          +----------------+     +---------+---------+
                                        |
          +----------------+            v
Client C -> ClientHandlerC |     +-------------------+
          +----------------+     | 4 Worker Threads  |
                                 +-------------------+
```

---

## Dry Run 5 — Why ThreadPool Helps

Without bounded command pool:

```text
1000 clients
-> 1000 client threads
-> each may execute heavy command
-> CPU overload
-> memory pressure
-> unstable server
```

With command pool:

```text
1000 clients
-> many client handlers can read input
-> only 4 commands execute at once
-> extra commands wait in queue
```

This gives controlled concurrency:

```text
more stable server
predictable CPU usage
first step toward backpressure
```

---

## Important Bug Awareness

This phase has one important production warning:

```text
Multiple command tasks from the same client may execute out of order.
```

Example:

```text
Client sends:
SET x 1
GET x
```

If submitted separately to a pool:

```text
GET x might run before SET x 1
```

For learning, this is okay.

For production Redis-like behavior, you need:

```text
per-client ordering
or
single event loop
or
ordered command queue per connection
```

That is why real Redis uses an event-loop model instead of random parallel command execution.

# 11. Test Commands

Try these mental or driver-level commands:

```text
submit command task
worker executes
response returned
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
Producer-consumer, bounded thread pool, backpressure concept
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
backend request processing, async execution, controlled concurrency
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
013
```

Continue the MiniRedis roadmap until the final production architecture.
