# 002_RESP_Protocol_Parser.md

# MiniRedis — Phase 002: RESP Protocol Parser

## Clickable Index

- [1. Goal](#1-goal)
- [2. What We Built Previously](#2-what-we-built-previously)
- [3. What We Build In This Phase](#3-what-we-build-in-this-phase)
- [4. Why RESP Protocol Matters](#4-why-resp-protocol-matters)
- [5. RESP Format Basics](#5-resp-format-basics)
- [6. Architecture Diagram](#6-architecture-diagram)
- [7. File Structure](#7-file-structure)
- [8. Complete Java Code](#8-complete-java-code)
- [9. Driver Class](#9-driver-class)
- [10. Step-by-Step Dry Run](#10-step-by-step-dry-run)
- [11. How To Run](#11-how-to-run)
- [12. Test Inputs](#12-test-inputs)
- [13. DSA Concepts Used](#13-dsa-concepts-used)
- [14. System Design Relevance](#14-system-design-relevance)
- [15. Interview Notes](#15-interview-notes)
- [16. What Changes In Next Phase](#16-what-changes-in-next-phase)

---

# 1. Goal

In this phase, we move from a plain TCP echo server to a Redis-style command protocol parser.

Real Redis clients do not usually send commands as plain strings only. They use **RESP**:

```text
Redis Serialization Protocol
```

This phase teaches how a server understands structured commands like:

```text
*2
$4
PING
$5
hello
```

and converts them into a Java command object:

```text
Command{name='PING', args=['hello']}
```

---

# 2. What We Built Previously

In `001_TCP_Echo_Server.md`, we built:

```text
Client  --->  TCP Server  --->  Echo same message back
```

Example:

```text
Client sends: hello
Server replies: ECHO: hello
```

That server could receive bytes, but it did not understand Redis commands yet.

---

# 3. What We Build In This Phase

We will build:

```text
RESP raw message  --->  RespParser  --->  Command object
```

Supported command examples:

```text
PING
SET name mohamed
GET name
DEL name
```

Supported RESP type in this phase:

```text
Array of Bulk Strings
```

Example RESP input:

```text
*3
$3
SET
$4
name
$7
mohamed
```

Parsed as:

```text
Command name = SET
Arguments    = [name, mohamed]
```

---

# 4. Why RESP Protocol Matters

Redis is fast not only because of memory, but also because its protocol is simple.

RESP is:

- easy to parse
- compact
- stream friendly
- language independent
- works over TCP

This is the same idea used in many backend systems:

| System | Protocol Idea |
|---|---|
| Redis | RESP |
| Kafka | Binary protocol |
| HTTP | Text protocol |
| gRPC | Protobuf over HTTP/2 |
| PostgreSQL | Wire protocol |

Protocol parsing is a core backend skill.

---

# 5. RESP Format Basics

## 5.1 Simple String

Starts with `+`:

```text
+OK
```

Meaning:

```text
OK
```

---

## 5.2 Error

Starts with `-`:

```text
-ERR unknown command
```

---

## 5.3 Integer

Starts with `:`:

```text
:1
```

---

## 5.4 Bulk String

Starts with `$`:

```text
$5
hello
```

Meaning:

```text
hello
```

Here `$5` means the next string has length 5.

---

## 5.5 Array

Starts with `*`:

```text
*2
$4
PING
$5
hello
```

Meaning:

```text
[PING, hello]
```

Redis commands are usually sent as an array of bulk strings.

---

# 6. Architecture Diagram

```text
+-------------------+
| Redis Client      |
+-------------------+
          |
          | RESP bytes over TCP
          v
+-------------------+
| RespParser        |
+-------------------+
          |
          | Command object
          v
+-------------------+
| Command Executor  |
+-------------------+
          |
          | Response
          v
+-------------------+
| RespWriter        |
+-------------------+
```

In this phase, we only build:

```text
RespParser + Command model
```

---

# 7. File Structure

```text
MiniRedis/
└── src/
    └── main/
        └── java/
            └── com/
                └── miniredis/
                    ├── protocol/
                    │   ├── Command.java
                    │   └── RespParser.java
                    └── driver/
                        └── Phase002RespParserDriver.java
```

---

# 8. Complete Java Code

## 8.1 Command.java

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

## 8.2 RespParser.java

```java
package com.miniredis.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RespParser {

    public Command parse(BufferedReader reader) throws IOException {
        String firstLine = reader.readLine();

        if (firstLine == null) {
            throw new IOException("Client disconnected");
        }

        if (firstLine.isBlank()) {
            throw new IllegalArgumentException("Empty command");
        }

        if (firstLine.startsWith("*")) {
            return parseArrayOfBulkStrings(firstLine, reader);
        }

        return parseInlineCommand(firstLine);
    }

    private Command parseInlineCommand(String line) {
        String[] parts = line.trim().split("\\s+");

        if (parts.length == 0) {
            throw new IllegalArgumentException("Invalid inline command");
        }

        String commandName = parts[0];
        List<String> args = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            args.add(parts[i]);
        }

        return new Command(commandName, args);
    }

    private Command parseArrayOfBulkStrings(String firstLine, BufferedReader reader) throws IOException {
        int arrayLength = parseLength(firstLine, '*');
        List<String> tokens = new ArrayList<>();

        for (int i = 0; i < arrayLength; i++) {
            String bulkHeader = reader.readLine();

            if (bulkHeader == null) {
                throw new IOException("Unexpected end of stream while reading bulk header");
            }

            if (!bulkHeader.startsWith("$")) {
                throw new IllegalArgumentException("Expected bulk string header, got: " + bulkHeader);
            }

            int bulkLength = parseLength(bulkHeader, '$');
            String value = reader.readLine();

            if (value == null) {
                throw new IOException("Unexpected end of stream while reading bulk value");
            }

            if (value.length() != bulkLength) {
                throw new IllegalArgumentException(
                        "Invalid bulk string length. Expected " + bulkLength + " but got " + value.length()
                );
            }

            tokens.add(value);
        }

        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Command array cannot be empty");
        }

        String commandName = tokens.get(0);
        List<String> args = tokens.subList(1, tokens.size());

        return new Command(commandName, args);
    }

    private int parseLength(String line, char prefix) {
        try {
            return Integer.parseInt(line.substring(1));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid length header: " + line);
        }
    }
}
```

---

# 9. Driver Class

## Phase002RespParserDriver.java

```java
package com.miniredis.driver;

import com.miniredis.protocol.Command;
import com.miniredis.protocol.RespParser;

import java.io.BufferedReader;
import java.io.StringReader;

public class Phase002RespParserDriver {
    public static void main(String[] args) throws Exception {
        RespParser parser = new RespParser();

        String inlineInput = "SET name mohamed\r\n";
        Command inlineCommand = parser.parse(
                new BufferedReader(new StringReader(inlineInput))
        );

        System.out.println("Inline command:");
        System.out.println(inlineCommand);

        String respInput = "*3\r\n" +
                "$3\r\n" +
                "SET\r\n" +
                "$4\r\n" +
                "name\r\n" +
                "$7\r\n" +
                "mohamed\r\n";

        Command respCommand = parser.parse(
                new BufferedReader(new StringReader(respInput))
        );

        System.out.println("RESP command:");
        System.out.println(respCommand);
    }
}
```

---

# 10. Step-by-Step Dry Run

Input:

```text
*3
$3
SET
$4
name
$7
mohamed
```

## Step 1

Parser reads first line:

```text
*3
```

Meaning:

```text
Array has 3 items
```

---

## Step 2

Parser reads:

```text
$3
SET
```

Meaning:

```text
Bulk string length = 3
Value = SET
```

Tokens:

```text
[SET]
```

---

## Step 3

Parser reads:

```text
$4
name
```

Tokens:

```text
[SET, name]
```

---

## Step 4

Parser reads:

```text
$7
mohamed
```

Tokens:

```text
[SET, name, mohamed]
```

---

## Step 5

Parser converts tokens into command:

```text
commandName = SET
args        = [name, mohamed]
```

Final object:

```text
Command{name='SET', args=[name, mohamed]}
```

---

# 11. How To Run

Compile:

```bash
javac -d out src/main/java/com/miniredis/protocol/*.java src/main/java/com/miniredis/driver/*.java
```

Run:

```bash
java -cp out com.miniredis.driver.Phase002RespParserDriver
```

Expected output:

```text
Inline command:
Command{name='SET', args=[name, mohamed]}
RESP command:
Command{name='SET', args=[name, mohamed]}
```

---

# 12. Test Inputs

## Inline input

```text
PING
```

Output:

```text
Command{name='PING', args=[]}
```

---

## Inline SET

```text
SET name mohamed
```

Output:

```text
Command{name='SET', args=[name, mohamed]}
```

---

## RESP PING

```text
*1
$4
PING
```

Output:

```text
Command{name='PING', args=[]}
```

---

## RESP GET

```text
*2
$3
GET
$4
name
```

Output:

```text
Command{name='GET', args=[name]}
```

---

# 13. DSA Concepts Used

| Concept | Where Used |
|---|---|
| Array/List | Store command tokens |
| String parsing | RESP line parsing |
| Validation | Protocol correctness |
| Sequential scan | Reading command parts |

This phase is not algorithm-heavy, but it is very important for backend engineering.

---

# 14. System Design Relevance

Protocol parsing appears in many systems:

```text
Client bytes -> Parser -> Command object -> Executor -> Response
```

This pattern exists in:

- Redis
- Kafka
- PostgreSQL
- HTTP servers
- API gateways
- message brokers
- custom internal RPC systems

This is why building protocol parsing gives strong low-level backend confidence.

---

# 15. Interview Notes

## Q1. Why does Redis use RESP?

Because RESP is:

- simple
- fast to parse
- supports multiple data types
- works well over TCP
- easy for clients in any language

---

## Q2. Why not just send plain strings?

Plain strings are easy for humans but weak for machines.

Example:

```text
SET full_name mohamed thaiseer
```

Is value one word or two words?

RESP removes ambiguity by sending length-prefixed strings:

```text
$16
mohamed thaiseer
```

Now the parser knows the exact size.

---

## Q3. Why are length-prefixed protocols useful?

Because they avoid guessing where data ends.

This is critical for:

- binary payloads
- spaces in values
- streaming over TCP
- partial reads
- high-performance servers

---

# 16. What Changes In Next Phase

Next file:

```text
003_InMemory_KeyValue_Store.md
```

We will add the first real Redis storage engine:

```text
SET name mohamed
GET name
```

Architecture will become:

```text
RESP Parser -> Command -> RedisStore -> Response
```

This is where MiniRedis starts behaving like a real in-memory database.

