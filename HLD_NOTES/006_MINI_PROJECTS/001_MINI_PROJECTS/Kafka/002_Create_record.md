# MiniKafka Step 2 — Complete Step-by-Step Path to a Working Kafka-Like Model

## Goal

We are not building only one small append example.

We are building MiniKafka gradually:

```text
Step 1  -> Create log file
Step 2  -> Append records to log
Step 3  -> Read records from log
Step 4  -> Convert raw log line to MessageRecord object
Step 5  -> Create Partition
Step 6  -> Create Topic
Step 7  -> Create Broker
Step 8  -> Create Producer
Step 9  -> Create Consumer
Step 10 -> Read from offset
Step 11 -> Commit consumer offset
Step 12 -> Add multiple partitions
Step 13 -> Key-based partitioning
Step 14 -> Consumer group basics
Step 15 -> Replication basics
```

At the end, we want this mental model:

```text
Producer
   |
   v
Broker
   |
   v
Topic: orders
   |
   +--> Partition 0 -> append-only log
   |
   +--> Partition 1 -> append-only log
   |
   +--> Partition 2 -> append-only log
   |
   v
Consumer reads using offset
```

---

# Step 2 Focus

In Step 1, we created the physical log file:

```text
data/phase1/orders-0.log
```

In Step 2, we learn the most important Kafka storage idea:

```text
Kafka is append-only.
```

That means:

```text
Old messages are not updated.
Old messages are not deleted immediately.
New messages are added at the end of the log.
```

---

# Step 2.1 — Folder Structure

Create this structure:

```text
MiniKafka/
├── data/
│   └── phase1/
│       └── orders-0.log
└── src/
    └── main/
        └── java/
            └── com/
                └── minikafka/
                    └── step2/
                        ├── Step2AppendOneMessage.java
                        ├── Step2AppendMultipleMessages.java
                        └── Step2ReadLogFile.java
```

---

# Step 2.2 — What Is a Kafka Record?

A Kafka message is usually called a **record**.

For now, our record has 3 fields:

```text
offset | key | value
```

Example:

```text
0|order-1|created
1|order-2|paid
2|order-3|shipped
```

Meaning:

```text
offset = message position inside partition log
key    = used later for partition selection
value  = actual message body
```

---

# Step 2.3 — Why Offset Matters

Offset is the position of a message inside one partition.

```text
orders-0.log

offset 0 -> order-1 created
offset 1 -> order-2 paid
offset 2 -> order-3 shipped
```

Consumer can say:

```text
Give me messages from offset 1.
```

Then MiniKafka returns:

```text
1|order-2|paid
2|order-3|shipped
```

This is how Kafka allows consumers to resume from where they stopped.

---

# Step 2.4 — Code: Append One Message

File:

```text
src/main/java/com/minikafka/step2/Step2AppendOneMessage.java
```

```java
package com.minikafka.step2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Step2AppendOneMessage {

    public static void main(String[] args) throws IOException {

        // This is the physical partition log file.
        Path logPath = Path.of("data/phase1/orders-0.log");

        // Ensure parent folders exist.
        Files.createDirectories(logPath.getParent());

        // Ensure file exists.
        if (!Files.exists(logPath)) {
            Files.createFile(logPath);
        }

        // In Kafka, each message inside a partition has an offset.
        long offset = 0;

        // Key can later decide which partition this message goes to.
        String key = "order-1";

        // Value is the real message payload.
        String value = "created";

        // Our simple text record format.
        String record = offset + "|" + key + "|" + value;

        // Append means write at the end of the file.
        Files.writeString(
                logPath,
                record + System.lineSeparator(),
                StandardOpenOption.APPEND
        );

        System.out.println("Appended record: " + record);
        System.out.println("Log file: " + logPath.toAbsolutePath());
    }
}
```

---

# Step 2.5 — What Happens in Background?

This line:

```java
Path logPath = Path.of("data/phase1/orders-0.log");
```

points to:

```text
MiniKafka/
└── data/
    └── phase1/
        └── orders-0.log
```

This line:

```java
Files.writeString(logPath, record + System.lineSeparator(), StandardOpenOption.APPEND);
```

does this:

```text
1. Open orders-0.log
2. Move cursor to end of file
3. Write bytes
4. Add line separator
5. Close file
```

Visual:

```text
Before append:

orders-0.log
(empty)
^

After append:

orders-0.log
0|order-1|created
                   ^
```

The `^` means file cursor/end position.

---

# Step 2.6 — Run Command

From project root:

```bash
javac -d out src/main/java/com/minikafka/step2/Step2AppendOneMessage.java
java -cp out com.minikafka.step2.Step2AppendOneMessage
```

Expected console output:

```text
Appended record: 0|order-1|created
Log file: C:\...\MiniKafka\data\phase1\orders-0.log
```

Expected file content:

```text
0|order-1|created
```

---

# Step 2.7 — Problem With Hardcoded Offset

This is okay for first learning:

```java
long offset = 0;
```

But real Kafka cannot hardcode offset.

If we append another message, offset should become:

```text
0
1
2
3
...
```

So next we calculate offset from existing lines.

---

# Step 2.8 — Code: Append Multiple Messages With Auto Offset

File:

```text
src/main/java/com/minikafka/step2/Step2AppendMultipleMessages.java
```

```java
package com.minikafka.step2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

public class Step2AppendMultipleMessages {

    public static void main(String[] args) throws IOException {

        Path logPath = Path.of("data/phase1/orders-0.log");

        Files.createDirectories(logPath.getParent());

        if (!Files.exists(logPath)) {
            Files.createFile(logPath);
        }

        append(logPath, "order-1", "created");
        append(logPath, "order-2", "paid");
        append(logPath, "order-3", "shipped");

        System.out.println("All messages appended.");
        System.out.println("Open file: " + logPath.toAbsolutePath());
    }

    private static long append(Path logPath, String key, String value) throws IOException {

        long offset = countLines(logPath);

        String record = offset + "|" + key + "|" + value;

        Files.writeString(
                logPath,
                record + System.lineSeparator(),
                StandardOpenOption.APPEND
        );

        System.out.println("Appended: " + record);

        return offset;
    }

    private static long countLines(Path logPath) throws IOException {
        try (Stream<String> lines = Files.lines(logPath)) {
            return lines.count();
        }
    }
}
```

---

# Step 2.9 — Background Execution of Auto Offset

First append:

```text
File has 0 lines.
offset = 0
write 0|order-1|created
```

Second append:

```text
File has 1 line.
offset = 1
write 1|order-2|paid
```

Third append:

```text
File has 2 lines.
offset = 2
write 2|order-3|shipped
```

Final file:

```text
0|order-1|created
1|order-2|paid
2|order-3|shipped
```

---

# Step 2.10 — Important Note About Running Multiple Times

If you run the same program again, the file already has 3 lines.

So the next run adds:

```text
3|order-1|created
4|order-2|paid
5|order-3|shipped
```

That is correct append-only behavior.

If you want clean output, delete:

```text
data/phase1/orders-0.log
```

or clear the file before running.

---

# Step 2.11 — Code: Read Log File

File:

```text
src/main/java/com/minikafka/step2/Step2ReadLogFile.java
```

```java
package com.minikafka.step2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Step2ReadLogFile {

    public static void main(String[] args) throws IOException {

        Path logPath = Path.of("data/phase1/orders-0.log");

        if (!Files.exists(logPath)) {
            System.out.println("Log file does not exist yet.");
            return;
        }

        List<String> records = Files.readAllLines(logPath);

        System.out.println("Reading records from log file");
        System.out.println("-----------------------------");

        for (String record : records) {
            System.out.println(record);
        }
    }
}
```

Run:

```bash
javac -d out src/main/java/com/minikafka/step2/Step2ReadLogFile.java
java -cp out com.minikafka.step2.Step2ReadLogFile
```

Expected output:

```text
Reading records from log file
-----------------------------
0|order-1|created
1|order-2|paid
2|order-3|shipped
```

---

# Step 2.12 — Step 2 Final Mental Model

```text
append(key, value)
       |
       v
count existing lines
       |
       v
offset = line count
       |
       v
create record string
       |
       v
open log file in APPEND mode
       |
       v
write record at end
       |
       v
return offset
```

---

# Step 2.13 — Why This Is Kafka Foundation

Kafka stores records inside partition logs.

A topic is split into partitions.

Each partition is an append-only log.

```text
Topic: orders

Partition 0:
orders-0.log

0|order-1|created
1|order-2|paid
2|order-3|shipped
```

So our file:

```text
orders-0.log
```

is like Kafka partition:

```text
topic = orders
partition = 0
```

---

# Step 2.14 — What We Have Built So Far

```text
MiniKafka storage layer v1

Supported:
[yes] create log file
[yes] append one record
[yes] append many records
[yes] auto-generate offset
[yes] read all records

Not yet supported:
[no] read from specific offset
[no] MessageRecord class
[no] Topic abstraction
[no] Partition abstraction
[no] Broker
[no] Producer API
[no] Consumer API
```

---

# Step 2.15 — Next Step Toward Working Kafka Model

Next we should build:

```text
Step 3 — MessageRecord class
```

Currently records are raw strings:

```text
0|order-1|created
```

But real code should work with objects:

```java
MessageRecord record = new MessageRecord(0, "order-1", "created");
```

Then we can build clean layers:

```text
MessageRecord
     |
     v
LogSegment
     |
     v
Partition
     |
     v
Topic
     |
     v
Broker
     |
     v
Producer / Consumer
```

---

# Step 2 Completion Checklist

You are done with Step 2 if:

```text
[ ] You can create orders-0.log
[ ] You can append one message
[ ] You can append multiple messages
[ ] You understand offset = position inside partition
[ ] You understand append-only storage
[ ] You can read the log file
[ ] You know orders-0.log means topic orders, partition 0
```

---

# Big Picture Journey

```text
Current position:

Producer  -> not yet
Broker    -> not yet
Topic     -> not yet
Partition -> represented by orders-0.log
Log       -> yes
Offset    -> yes
Consumer  -> basic read-all only
```

Next:

```text
Step 3: Convert raw string log lines into MessageRecord objects.
```
