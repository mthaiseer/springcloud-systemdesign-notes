# 011_Multi_Client_Server.md

# MiniRedis Phase 11 — Multi Client Server

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
Multi Client Server
```

Purpose:

```text
Support many TCP clients concurrently.
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
The server handled one client at a time. A slow client could block everyone else.
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
We create one handler thread per client so multiple clients can issue commands simultaneously.
```

Commands or operations covered:

```text
multiple clients
SET
GET
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
| Multi Client Server |
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
  -> execute Multi Client Server
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

### Summary before this class

This class is the main TCP server.

It is responsible for:

```text
1. Open port 6379.
2. Wait for client connections.
3. Give each client its own handler task.
4. Read commands from that client.
5. Execute command work using a command thread pool.
6. Send response back to the correct client.
```

Important mental model:

```text
ServerSocket = hotel reception desk
Socket       = one connected client room
clientPool   = workers who handle connected clients
commandPool  = workers who execute commands
```

Why we need this class:

```text
Single-client server:
Client-1 connects
Client-2 waits

Multi-client server:
Client-1 handled by one worker
Client-2 handled by another worker
Client-3 handled by another worker
```

Production warning:

```text
This version teaches the idea.
Real Redis does not create one Java thread per client.
Real Redis mainly uses event loop / non-blocking IO.
```

```java
package com.miniredis.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MiniRedisServer {

    // Redis default port is 6379.
    // We keep it configurable so the same server can run on any port.
    private final int port;

    // This pool accepts connected clients.
    //
    // cachedThreadPool means:
    // - create a new thread when needed
    // - reuse idle threads when possible
    //
    // Simple for learning, but dangerous in production if unlimited clients connect.
    private final ExecutorService clientPool =
            Executors.newCachedThreadPool();

    // This pool executes commands.
    //
    // Fixed size = only 4 commands run at the same time.
    //
    // Why separate commandPool from clientPool?
    // - clientPool handles socket reading/writing
    // - commandPool handles actual command execution
    //
    // This separation is useful when command execution becomes expensive.
    private final ExecutorService commandPool =
            Executors.newFixedThreadPool(4);

    public MiniRedisServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {

        // ServerSocket opens a TCP port and listens for new clients.
        //
        // Example:
        // nc localhost 6379
        //
        // When a client connects, serverSocket.accept() returns a Socket.
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("MiniRedis server started on port " + port);

            // Server runs forever.
            //
            // Every loop waits for the next client connection.
            while (true) {

                // Blocking call:
                // waits until a new client connects.
                Socket client = serverSocket.accept();

                // Important:
                // We DO NOT handle the client in the main thread.
                //
                // Instead, we submit it to clientPool.
                //
                // This allows the main thread to immediately go back
                // and accept the next client.
                clientPool.submit(() -> handleClient(client));
            }
        }
    }

    private void handleClient(Socket client) {

        // One connected client reaches this method.
        //
        // This method keeps reading lines from that client's socket.
        //
        // Example client sends:
        // PING
        // SET name mohamed
        // GET name
        try (
                // Reader reads text sent by client.
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(client.getInputStream())
                        );

                // Writer sends response back to same client.
                //
                // autoFlush = true
                // means println() immediately sends data.
                PrintWriter writer =
                        new PrintWriter(client.getOutputStream(), true)
        ) {
            String line;

            // Keep reading commands from this client until:
            // - client disconnects
            // - socket closes
            // - error happens
            while ((line = reader.readLine()) != null) {

                // Capture commandLine as effectively final
                // so lambda can safely use it.
                String commandLine = line;

                // Submit command execution to commandPool.
                //
                // This means the client handler thread is not stuck
                // doing heavy command work.
                commandPool.submit(() -> {

                    // Execute command.
                    //
                    // In later phases this will call:
                    // - RESP parser
                    // - CommandExecutor
                    // - RedisStore
                    // - AOF persistence
                    String response = execute(commandLine);

                    // Send result back to the same connected client.
                    writer.println(response);
                });
            }

        } catch (IOException e) {

            // Client may disconnect suddenly.
            // That should not crash the whole server.
            System.err.println("Client error: " + e.getMessage());
        }
    }

    private String execute(String commandLine) {

        // Very small command executor for this phase.
        //
        // Later this method will be replaced by real command parsing:
        // SET key value
        // GET key
        // DEL key
        if (commandLine.equalsIgnoreCase("PING")) {
            return "PONG";
        }

        // Default response for learning.
        return "OK received: " + commandLine;
    }

    public static void main(String[] args) throws Exception {

        // Start MiniRedis server on Redis default port.
        new MiniRedisServer(6379).start();
    }
}
```

---

## 8.2 `Phase011Driver.java`

### Summary before this class

This class is only a simple launcher.

It does not contain Redis logic.

It starts the server so you can test using multiple terminals.

```text
Terminal 1:
run server

Terminal 2:
nc localhost 6379

Terminal 3:
nc localhost 6379
```

Purpose:

```text
Keep phase execution simple.
Main learning remains inside MiniRedisServer.
```

```java
package com.miniredis.driver;

import com.miniredis.server.MiniRedisServer;

public class Phase011Driver {

    public static void main(String[] args) throws Exception {

        // Create server object with port 6379.
        //
        // 6379 is the default Redis port.
        MiniRedisServer server = new MiniRedisServer(6379);

        // Start listening for clients.
        //
        // This call blocks forever because the server loop is infinite.
        server.start();
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
java -cp out com.miniredis.driver.Phase011Driver
```

If your shell does not expand `**`, compile individual files or use IntelliJ.

---


# 10. Step-by-Step Dry Run

## 10.1 Before This Phase — Single Client Problem

Old server model:

```text
Main Thread
   |
   v
accept Client-1
   |
   v
handle Client-1 fully
   |
   v
Client-2 waits
```

Problem:

```text
If Client-1 is slow,
Client-2 cannot be served.
```

Example:

```text
Client-1 connects and stays idle
Client-2 connects and sends PING
Client-2 waits because main thread is stuck with Client-1
```

This is bad for backend servers.

---

## 10.2 After This Phase — Multi Client Model

New server model:

```text
Main Thread
   |
   v
accept Client-1  ---> clientPool thread-1 handles Client-1
   |
   v
accept Client-2  ---> clientPool thread-2 handles Client-2
   |
   v
accept Client-3  ---> clientPool thread-3 handles Client-3
```

Now one slow client does not block all clients.

---

## 10.3 Thread Flow Diagram

```text
                         +----------------------+
                         | Main Server Thread   |
                         | serverSocket.accept |
                         +----------+-----------+
                                    |
             +----------------------+----------------------+
             |                      |                      |
             v                      v                      v
      +-------------+        +-------------+        +-------------+
      | Client-1    |        | Client-2    |        | Client-3    |
      | Socket      |        | Socket      |        | Socket      |
      +------+------+        +------+------+        +------+------+
             |                      |                      |
             v                      v                      v
      +-------------+        +-------------+        +-------------+
      | Handler T1  |        | Handler T2  |        | Handler T3  |
      | reads lines |        | reads lines |        | reads lines |
      +------+------+        +------+------+        +------+------+
             |                      |                      |
             +----------+-----------+----------+-----------+
                        |                      |
                        v                      v
                 +-------------------------------+
                 | Command Pool                  |
                 | 4 worker threads              |
                 | executes PING / SET / GET     |
                 +---------------+---------------+
                                 |
                                 v
                         Response to same client
```

---

## 10.4 Concrete Dry Run With 2 Clients

Start server:

```bash
java -cp out com.miniredis.driver.Phase011Driver
```

Server memory/thread state:

```text
port 6379 open
clientPool = ready
commandPool = ready with 4 workers
```

---

### Step 1 — Client-1 connects

```bash
nc localhost 6379
```

Internal state:

```text
Main thread:
accepts Client-1 socket

clientPool:
assigns Handler-T1

Handler-T1:
waits for Client-1 commands
```

Diagram:

```text
Client-1
   |
   v
Handler-T1
   |
   v
waiting for input
```

---

### Step 2 — Client-2 connects

Another terminal:

```bash
nc localhost 6379
```

Internal state:

```text
Main thread:
accepts Client-2 socket

clientPool:
assigns Handler-T2

Handler-T2:
waits for Client-2 commands
```

Diagram:

```text
Client-1 ---> Handler-T1
Client-2 ---> Handler-T2
```

Important:

```text
Client-2 did not wait for Client-1 to finish.
```

That is the whole point of this phase.

---

### Step 3 — Client-1 sends PING

Client-1 input:

```text
PING
```

Flow:

```text
Client-1
  -> Handler-T1 reads "PING"
  -> Handler-T1 submits command to commandPool
  -> command worker executes execute("PING")
  -> response = "PONG"
  -> writer sends response to Client-1
```

Output to Client-1:

```text
PONG
```

---

### Step 4 — Client-2 sends SET-like command

Client-2 input:

```text
SET name mohamed
```

Flow:

```text
Client-2
  -> Handler-T2 reads "SET name mohamed"
  -> Handler-T2 submits command to commandPool
  -> command worker executes execute("SET name mohamed")
  -> response = "OK received: SET name mohamed"
  -> writer sends response to Client-2
```

Output to Client-2:

```text
OK received: SET name mohamed
```

Note:

```text
In this phase, SET is not stored yet inside this server code.
It only proves multi-client request handling.
Later phases connect this to RedisStore.
```

---

## 10.5 In-Memory Thread State

After both clients connect:

```text
MiniRedisServer object
│
├── port = 6379
│
├── clientPool
│   ├── Handler-T1 -> Client-1 socket
│   └── Handler-T2 -> Client-2 socket
│
└── commandPool
    ├── Worker-1 -> may execute PING
    ├── Worker-2 -> may execute SET name mohamed
    ├── Worker-3 -> idle
    └── Worker-4 -> idle
```

This is the memory model you should remember.

---

## 10.6 Why This Works

Because accepting clients and handling clients are separated.

```text
Main thread:
only accepts new connections

Client handler threads:
read client commands

Command pool:
executes command logic
```

So the server can do multiple things at once.

---

## 10.7 System Design Mental Model

This phase is similar to:

```text
API server handling many HTTP clients
Database server handling many DB connections
Redis server handling many TCP clients
Gateway handling many incoming requests
```

Core idea:

```text
Do not let one slow client block the whole server.
```

---

# 11. Test Commands

Try these mental or driver-level commands:

```text
multiple clients
SET
GET
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
Thread-per-client, blocking IO, concurrency basics
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
real backend servers, Redis clients, DB connections, API gateway connections
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
012
```

Continue the MiniRedis roadmap until the final production architecture.
