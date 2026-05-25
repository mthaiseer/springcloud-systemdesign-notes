# 002_Append_Records_To_Log_File

# MiniKafka Step 2 — Append Records to Log File

## Goal

In Step 1, we created the physical log file:

```text
data/phase1/orders-0.log
```

Now in Step 2, we start storing messages inside it.

This is the heart of Kafka:

```text
Kafka is append-only.
```

Kafka never updates old messages.

Kafka only appends new messages at the end of the log.

---

# Big Picture

We are slowly building this:

```text
Producer
   |
   v
Broker
   |
   v
Topic
   |
   v
Partition
   |
   v
Append-only log file
```

Current progress:

```text
Producer  -> not yet
Broker    -> not yet
Topic     -> not yet
Partition -> represented by one file
Log       -> yes
Append    -> building now
Consumer  -> not yet
```

---

# Step 2.1 — Folder Structure

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

# Step 2.2 — What Is Stored In Kafka?

Kafka stores records inside partition logs.

For now, our record format is:

```text
offset|key|value
```

Example:

```text
0|order-1|created
1|order-2|paid
2|order-3|shipped
```

Meaning:

```text
offset = message position
key    = partitioning key later
value  = actual message
```

---

# Step 2.3 — Why Offset Is Important

Kafka consumers read by offset.

Example:

```text
orders-0.log

0|order-1|created
1|order-2|paid
2|order-3|shipped
```

Consumer can say:

```text
Read from offset 1
```

Kafka returns:

```text
1|order-2|paid
2|order-3|shipped
```

This is how Kafka resumes consumption.

---

# Step 2.4 — Append One Message

File:

```text
Step2AppendOneMessage.java
```

Code:

```java
package com.minikafka.step2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Step2AppendOneMessage {

    public static void main(String[] args) throws IOException {

        // Physical partition log file
        Path logPath = Path.of("data/phase1/orders-0.log");

        // Create folders if missing
        Files.createDirectories(logPath.getParent());

        // Create file if missing
        if (!Files.exists(logPath)) {
            Files.createFile(logPath);
        }

        // First offset
        long offset = 0;

        // Message key
        String key = "order-1";

        // Actual message
        String value = "created";

        // Convert record into text line
        String record = offset + "|" + key + "|" + value;

        // Append at end of file
        Files.writeString(
                logPath,
                record + System.lineSeparator(),
                StandardOpenOption.APPEND
        );

        System.out.println("Appended: " + record);
    }
}
```

---

# Step 2.5 — What Happens Internally?

This line:

```java
String record = offset + "|" + key + "|" + value;
```

creates:

```text
0|order-1|created
```

Then:

```java
Files.writeString(... APPEND)
```

does this:

```text
1. Open file
2. Move cursor to end
3. Write bytes
4. Close file
```

Visual:

```text
Before:

orders-0.log
(empty)

After:

orders-0.log
0|order-1|created
```

---

# Step 2.6 — Why APPEND Matters

Without APPEND:

```text
New write replaces old data
```

With APPEND:

```text
New data added at end
```

Kafka is append-only.

Very important concept.

---

# Step 2.7 — Run Command

```bash
javac -d out src/main/java/com/minikafka/step2/Step2AppendOneMessage.java

java -cp out com.minikafka.step2.Step2AppendOneMessage
```

Expected output:

```text
Appended: 0|order-1|created
```

File content:

```text
0|order-1|created
```

---

# Step 2.8 — Problem With Hardcoded Offset

This works:

```java
long offset = 0;
```

But Kafka offsets must continuously increase:

```text
0
1
2
3
4
...
```

So next we auto-generate offset.

---

# Step 2.9 — Append Multiple Messages

File:

```text
Step2AppendMultipleMessages.java
```

Code:

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
    }

    private static long append(Path logPath,
                               String key,
                               String value) throws IOException {

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

# Step 2.10 — Background Execution

First append:

```text
Current lines = 0
offset = 0
```

File becomes:

```text
0|order-1|created
```

Second append:

```text
Current lines = 1
offset = 1
```

File becomes:

```text
0|order-1|created
1|order-2|paid
```

Third append:

```text
Current lines = 2
offset = 2
```

Final file:

```text
0|order-1|created
1|order-2|paid
2|order-3|shipped
```

---

# Step 2.11 — Read Log File

File:

```text
Step2ReadLogFile.java
```

Code:

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
            System.out.println("Log file missing");
            return;
        }

        List<String> records = Files.readAllLines(logPath);

        System.out.println("Reading records");
        System.out.println("----------------");

        for (String record : records) {
            System.out.println(record);
        }
    }
}
```

---

# Step 2.12 — Run Read Program

```bash
javac -d out src/main/java/com/minikafka/step2/Step2ReadLogFile.java

java -cp out com.minikafka.step2.Step2ReadLogFile
```

Expected output:

```text
Reading records
----------------
0|order-1|created
1|order-2|paid
2|order-3|shipped
```

---

# Step 2.13 — Important Concept Learned

We now built:

```text
append(key, value)
       |
       v
count current records
       |
       v
generate offset
       |
       v
create record string
       |
       v
append at end of file
```

This is the core of Kafka storage.

---

# Step 2.14 — Visual Flow

```text
Producer sends data
       |
       v
Generate offset
       |
       v
Create log line
       |
       v
Open partition log
       |
       v
Move cursor to end
       |
       v
Write bytes
       |
       v
Close file
```

---

# Step 2.15 — Current MiniKafka State

```text
Supported:
[yes] create partition log
[yes] append messages
[yes] auto offset generation
[yes] read records

Not yet:
[no] MessageRecord object
[no] Partition class
[no] Topic class
[no] Broker
[no] Producer API
[no] Consumer API
[no] read by offset
[no] consumer group
```

---

# Step 2.16 — Next Step

Next we should build:

```text
MessageRecord class
```

Currently records are raw strings:

```text
0|order-1|created
```

Next we convert them into objects:

```java
MessageRecord record =
    new MessageRecord(0, "order-1", "created");
```

Then we can build:

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
```

---

# Step 2 Completion Checklist

```text
[ ] You appended one message
[ ] You appended multiple messages
[ ] You understand append-only log
[ ] You understand offset
[ ] You can read records
[ ] You know orders-0.log = partition log
```
