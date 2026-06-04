# 001_TCP_Echo_Server.md

# MiniRedis Phase 1 — TCP Echo Server

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Build](#3-what-we-build)
- [4. Current Architecture](#4-current-architecture)
- [5. Folder Structure](#5-folder-structure)
- [6. Step-by-Step Flow](#6-step-by-step-flow)
- [7. Complete Java Code](#7-complete-java-code)
  - [7.1 MiniRedisTcpEchoServer.java](#71-miniredistcpechoserverjava)
  - [7.2 TcpEchoClient.java](#72-tcpechoclientjava)
  - [7.3 Phase001TcpEchoDriver.java](#73-phase001tcpechodriverjava)
- [8. How To Run](#8-how-to-run)
- [9. Test Using Terminal](#9-test-using-terminal)
- [10. Dry Run](#10-dry-run)
- [11. DSA / CP Concepts Used](#11-dsa--cp-concepts-used)
- [12. System Design Relevance](#12-system-design-relevance)
- [13. Redis Connection With This Phase](#13-redis-connection-with-this-phase)
- [14. Interview Notes](#14-interview-notes)
- [15. Common Bugs](#15-common-bugs)
- [16. Next Step](#16-next-step)

---

# 1. Goal

In this phase, we build the **first networking foundation** of MiniRedis.

We will create a simple TCP server that:

- listens on port `6379`
- accepts a client connection
- reads text from the client
- sends the same text back
- closes connection when client sends `quit`

This is not Redis yet, but this is the first building block of Redis.

Real Redis is also a TCP server.

---

# 2. Why This Phase Matters

Before Redis can understand commands like:

```text
SET name mohamed
GET name
DEL name
```

it first needs to:

```text
1. Open a TCP port
2. Accept client connections
3. Read bytes from socket
4. Write bytes back to socket
```

So this phase teaches the lowest-level foundation:

```text
Client  ---> TCP Socket ---> Server
Client  <--- TCP Socket <--- Server
```

Without this, RESP parser, command executor, key-value store, pub/sub, streams, replication, and clustering cannot exist.

---

# 3. What We Build

We build a blocking TCP echo server.

Example:

```text
Client sends: hello
Server sends: ECHO: hello

Client sends: ping
Server sends: ECHO: ping

Client sends: quit
Server sends: bye
```

This gives us a simple base server.

Later phases will replace `ECHO: hello` with real Redis-style responses.

---

# 4. Current Architecture

```text
+------------------+          TCP          +--------------------------+
|                  | --------------------> |                          |
|   TCP Client     |                       |  MiniRedis TCP Server    |
|                  | <-------------------- |                          |
+------------------+                       +--------------------------+
        |                                               |
        | sends text                                    | reads text
        | receives echo                                 | writes echo
        v                                               v
   Terminal / Java Client                         Blocking Socket IO
```

## Request Flow

```text
1. Server starts on port 6379
2. Client connects
3. Server accepts socket
4. Client sends message
5. Server reads line
6. Server returns same message with ECHO prefix
7. Client sends quit
8. Server closes connection
```

---

# 5. Folder Structure

Create this structure:

```text
MiniRedis/
└── src/
    └── main/
        └── java/
            └── com/
                └── miniredis/
                    ├── server/
                    │   └── MiniRedisTcpEchoServer.java
                    ├── client/
                    │   └── TcpEchoClient.java
                    └── driver/
                        └── Phase001TcpEchoDriver.java
```

For simple learning, you can also keep all files in one folder and compile manually.

Recommended package style:

```text
com.miniredis.server
com.miniredis.client
com.miniredis.driver
```

---

# 6. Step-by-Step Flow

## Step 1 — Start ServerSocket

```java
ServerSocket serverSocket = new ServerSocket(6379);
```

This opens a TCP port.

Redis default port is also:

```text
6379
```

## Step 2 — Wait For Client

```java
Socket clientSocket = serverSocket.accept();
```

This is blocking.

Meaning:

```text
Server waits here until a client connects.
```

## Step 3 — Read Client Message

```java
BufferedReader reader = new BufferedReader(
        new InputStreamReader(clientSocket.getInputStream())
);
```

This reads data coming from the client.

## Step 4 — Write Response

```java
PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
```

This sends data back to the client.

## Step 5 — Echo Loop

```java
while ((line = reader.readLine()) != null) {
    writer.println("ECHO: " + line);
}
```

This is the first server loop.

Later this loop becomes:

```text
read request -> parse command -> execute command -> send response
```

---

# 7. Complete Java Code

---

## 7.1 MiniRedisTcpEchoServer.java

```java
package com.miniredis.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Phase 001: Basic TCP Echo Server.
 *
 * Goal:
 * - Open TCP port 6379
 * - Accept one client at a time
 * - Read text line by line
 * - Echo response back to client
 *
 * This is the networking foundation of MiniRedis.
 */
public class MiniRedisTcpEchoServer {

    private final int port;
    private volatile boolean running = true;

    public MiniRedisTcpEchoServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("MiniRedis TCP Echo Server starting on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Waiting for clients...");

            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
                Socket socket = clientSocket;
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            writer.println("Welcome to MiniRedis TCP Echo Server");
            writer.println("Type any message. Type 'quit' to close connection.");

            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println("Received: " + line);

                if (line.equalsIgnoreCase("quit")) {
                    writer.println("bye");
                    break;
                }

                writer.println("ECHO: " + line);
            }

            System.out.println("Client disconnected.");

        } catch (IOException e) {
            System.err.println("Client handling error: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
    }

    public static void main(String[] args) {
        MiniRedisTcpEchoServer server = new MiniRedisTcpEchoServer(6379);
        server.start();
    }
}
```

---

## 7.2 TcpEchoClient.java

```java
package com.miniredis.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Simple TCP client for testing MiniRedisTcpEchoServer.
 */
public class TcpEchoClient {

    private final String host;
    private final int port;

    public TcpEchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try (
                Socket socket = new Socket(host, port);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connected to MiniRedis server.");

            // Read welcome messages from server
            System.out.println(reader.readLine());
            System.out.println(reader.readLine());

            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();

                writer.println(input);

                String response = reader.readLine();
                System.out.println(response);

                if (input.equalsIgnoreCase("quit")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        TcpEchoClient client = new TcpEchoClient("localhost", 6379);
        client.start();
    }
}
```

---

## 7.3 Phase001TcpEchoDriver.java

This driver starts the server.

```java
package com.miniredis.driver;

import com.miniredis.server.MiniRedisTcpEchoServer;

/**
 * Driver for Phase 001.
 *
 * Run this class first.
 * Then connect using TcpEchoClient, telnet, netcat, or redis-cli later.
 */
public class Phase001TcpEchoDriver {

    public static void main(String[] args) {
        int port = 6379;

        MiniRedisTcpEchoServer server = new MiniRedisTcpEchoServer(port);
        server.start();
    }
}
```

---

# 8. How To Run

## Option A — Using IntelliJ

1. Create Java project `MiniRedis`
2. Create packages:

```text
com.miniredis.server
com.miniredis.client
com.miniredis.driver
```

3. Add the three Java files
4. Run:

```text
Phase001TcpEchoDriver
```

5. Open another run configuration and run:

```text
TcpEchoClient
```

---

## Option B — Using Command Line

From project root:

```bash
javac -d out src/main/java/com/miniredis/server/MiniRedisTcpEchoServer.java \
             src/main/java/com/miniredis/client/TcpEchoClient.java \
             src/main/java/com/miniredis/driver/Phase001TcpEchoDriver.java
```

Start server:

```bash
java -cp out com.miniredis.driver.Phase001TcpEchoDriver
```

Start client in another terminal:

```bash
java -cp out com.miniredis.client.TcpEchoClient
```

---

# 9. Test Using Terminal

## Using telnet

```bash
telnet localhost 6379
```

Then type:

```text
hello
ping
SET name mohamed
quit
```

Expected:

```text
ECHO: hello
ECHO: ping
ECHO: SET name mohamed
bye
```

## Using netcat

```bash
nc localhost 6379
```

Then type:

```text
hello
quit
```

---

# 10. Dry Run

Input:

```text
hello
```

Flow:

```text
Client sends bytes: h e l l o \n
Server reader reads line: "hello"
Server checks: line == "quit" ? no
Server writes: "ECHO: hello"
Client receives: "ECHO: hello"
```

Visual:

```text
Client                         Server
  |                              |
  | ---- connect --------------> |
  |                              |
  | <--- welcome --------------- |
  |                              |
  | ---- hello ----------------> |
  |                              |
  | <--- ECHO: hello ----------- |
  |                              |
  | ---- quit -----------------> |
  |                              |
  | <--- bye ------------------- |
  |                              |
  | ---- close ----------------> |
```

---

# 11. DSA / CP Concepts Used

This phase does not use heavy DSA.

But it introduces important low-level concepts:

| Concept | Usage |
|---|---|
| Loop | Keep reading client messages |
| String processing | Read command line |
| Blocking queue idea | Server waits for input |
| State flag | `running = true` |

The important learning here is not DSA.

The important learning is:

```text
network connection lifecycle
```

---

# 12. System Design Relevance

This phase maps to every backend server:

```text
API Gateway
Redis
Kafka broker
Database server
Load balancer
Message queue
Search server
```

All of them start with the same idea:

```text
Open port -> accept connection -> read request -> write response
```

In HLD interviews, you usually draw boxes like:

```text
Client -> Server -> Database
```

But this phase teaches what actually happens inside the server box.

---

# 13. Redis Connection With This Phase

Real Redis:

```text
redis-server starts
opens port 6379
client connects using TCP
client sends RESP command
server replies using RESP response
```

Our current MiniRedis:

```text
MiniRedisTcpEchoServer starts
opens port 6379
client connects using TCP
client sends plain text
server replies with plain text echo
```

Difference:

| Real Redis | Current Phase |
|---|---|
| RESP protocol | Plain text line protocol |
| Command execution | Echo only |
| In-memory store | Not yet |
| Multi-client support | Not yet fully |
| Persistence | Not yet |

Next phase fixes the protocol part.

---

# 14. Interview Notes

## Q1. What is a TCP server?

A TCP server is a program that listens on a port, accepts client connections, reads bytes from clients, and sends bytes back.

## Q2. What does `accept()` do?

`accept()` blocks until a client connects. When a client connects, it returns a `Socket` representing that connection.

## Q3. Why is this server blocking?

Because:

```java
serverSocket.accept();
reader.readLine();
```

both wait until something happens.

## Q4. What is the limitation of this phase?

It handles one client at a time. If one client stays connected, another client must wait.

We fix this later in:

```text
011_Multi_Client_Server.md
012_Command_Executor_ThreadPool.md
```

## Q5. Why does Redis use port 6379?

Redis default TCP port is 6379. We use the same port to make MiniRedis feel realistic.

---

# 15. Common Bugs

## Bug 1 — Port already in use

Error:

```text
Address already in use: bind
```

Reason:

Another process is already using port `6379`.

Fix:

Use another port:

```java
new MiniRedisTcpEchoServer(6380);
```

or stop existing Redis/server process.

---

## Bug 2 — Client cannot connect

Error:

```text
Connection refused
```

Reason:

Server is not running.

Fix:

Start server first.

---

## Bug 3 — Server waits forever

Reason:

`accept()` is blocking.

This is expected.

The server is waiting for a client connection.

---

## Bug 4 — `readLine()` does not return

Reason:

Client did not send newline.

`readLine()` waits for:

```text
\n
```

Fix:

Use `println()` from client or press Enter in terminal.

---

# 16. Next Step

Next file:

```text
002_RESP_Protocol_Parser.md
```

In the next phase, we stop using simple plain text echo and start building Redis RESP protocol parsing.

We will support inputs like:

```text
*1
$4
PING
```

and later:

```text
*3
$3
SET
$4
name
$7
mohamed
```

The server will move from:

```text
read line -> echo line
```

to:

```text
read RESP bytes -> parse command -> execute command -> write Redis-style response
```

This is where MiniRedis starts becoming real Redis.
