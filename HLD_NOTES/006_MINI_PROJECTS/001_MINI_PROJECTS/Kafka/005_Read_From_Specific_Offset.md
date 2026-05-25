# 005_Read_From_Specific_Offset

# MiniKafka Step 5 — Read From Specific Offset

## Goal

In Step 4, we added:

```java
readFromOffset(offset)
```

Now in Step 5, we deeply understand how Kafka consumers actually work.

This step is very important because:

```text
Kafka consumers DO NOT read entire log every time.
Kafka consumers continue from their last offset.
```

This is the foundation of:

```text
consumer polling
resume consumption
consumer groups
offset commits
```

---

# Big Picture

Before:

```text
Consumer reads entire file
```

After:

```text
Consumer reads incrementally
using offsets
```

---

# Real Kafka Consumer Idea

Consumer remembers:

```text
last processed offset = 5
```

Next poll:

```text
Read from offset 6
```

Kafka returns only new messages.

This is why Kafka consumers scale efficiently.

---

# Step 5.1 — Folder Structure

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
                    └── step5/
                        ├── MessageRecord.java
                        ├── RecordSerializer.java
                        ├── LogSegment.java
                        └── Step5Driver.java
```

---

# Step 5.2 — Reuse Previous Classes

Reuse:

```text
MessageRecord.java
RecordSerializer.java
```

From previous step.

---

# Step 5.3 — Problem With readAll()

Suppose log file contains:

```text
0|order-1|created
1|order-2|paid
2|order-3|shipped
3|order-4|delivered
```

If consumer repeatedly calls:

```java
readAll()
```

Every poll returns:

```text
all records again and again
```

This is inefficient.

---

# Step 5.4 — Offset-Based Reading

Consumer tracks:

```text
last processed offset
```

Example:

```text
Consumer already processed:
0
1
```

Next poll should return:

```text
2
3
```

This is exactly how Kafka works.

---

# Step 5.5 — LogSegment Class

File:

```text
LogSegment.java
```

Code:

```java
package com.minikafka.step5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LogSegment {

    private final Path logPath;

    public LogSegment(String filePath)
            throws IOException {

        this.logPath = Path.of(filePath);

        Files.createDirectories(logPath.getParent());

        if (!Files.exists(logPath)) {
            Files.createFile(logPath);
        }
    }

    public long append(String key,
                       String value)
            throws IOException {

        long offset = countLines();

        MessageRecord record =
                new MessageRecord(offset, key, value);

        String line =
                RecordSerializer.serialize(record);

        Files.writeString(
                logPath,
                line + System.lineSeparator(),
                StandardOpenOption.APPEND
        );

        return offset;
    }

    public List<MessageRecord> readFromOffset(
            long startOffset)
            throws IOException {

        List<MessageRecord> result =
                new ArrayList<>();

        List<String> lines =
                Files.readAllLines(logPath);

        for (String line : lines) {

            MessageRecord record =
                    RecordSerializer.deserialize(line);

            if (record.getOffset() >= startOffset) {
                result.add(record);
            }
        }

        return result;
    }

    private long countLines()
            throws IOException {

        try (Stream<String> lines =
                     Files.lines(logPath)) {

            return lines.count();
        }
    }
}
```

---

# Step 5.6 — Understanding readFromOffset()

This code:

```java
if (record.getOffset() >= startOffset)
```

means:

```text
skip old messages
return only new messages
```

Example:

```text
File:

0|created
1|paid
2|shipped
3|delivered
```

Call:

```java
readFromOffset(2)
```

Result:

```text
2|shipped
3|delivered
```

---

# Step 5.7 — Driver Program

File:

```text
Step5Driver.java
```

Code:

```java
package com.minikafka.step5;

import java.util.List;

public class Step5Driver {

    public static void main(String[] args)
            throws Exception {

        LogSegment segment =
                new LogSegment(
                        "data/phase1/orders-0.log"
                );

        segment.append("order-1", "created");
        segment.append("order-2", "paid");
        segment.append("order-3", "shipped");
        segment.append("order-4", "delivered");

        System.out.println("---- FIRST POLL ----");

        long consumerOffset = 0;

        List<MessageRecord> firstPoll =
                segment.readFromOffset(
                        consumerOffset
                );

        for (MessageRecord record : firstPoll) {

            System.out.println(record);

            consumerOffset =
                    record.getOffset() + 1;
        }

        System.out.println();

        System.out.println(
                "Consumer next offset = "
                        + consumerOffset
        );

        System.out.println();

        System.out.println(
                "Appending new messages..."
        );

        segment.append("order-5", "returned");

        segment.append("order-6", "refund");

        System.out.println();

        System.out.println("---- SECOND POLL ----");

        List<MessageRecord> secondPoll =
                segment.readFromOffset(
                        consumerOffset
                );

        for (MessageRecord record : secondPoll) {

            System.out.println(record);

            consumerOffset =
                    record.getOffset() + 1;
        }

        System.out.println();

        System.out.println(
                "Consumer final offset = "
                        + consumerOffset
        );
    }
}
```

---

# Step 5.8 — First Poll Flow

Consumer starts with:

```text
consumerOffset = 0
```

Calls:

```java
readFromOffset(0)
```

Kafka returns:

```text
0
1
2
3
```

Consumer processes records one by one.

After processing:

```text
consumerOffset = 4
```

Meaning:

```text
Next read should start from offset 4
```

---

# Step 5.9 — Second Poll Flow

New messages appended:

```text
4|returned
5|refund
```

Consumer calls:

```java
readFromOffset(4)
```

Kafka returns:

```text
4|returned
5|refund
```

Consumer does NOT re-read old messages.

This is the core Kafka consumer model.

---

# Step 5.10 — Visual Polling Flow

```text
Consumer offset = 0
        |
        v
Poll broker
        |
        v
Read offsets >= 0
        |
        v
Process messages
        |
        v
Update consumer offset
        |
        v
Poll again later
```

---

# Step 5.11 — Run Command

```bash
javac -d out src/main/java/com/minikafka/step5/*.java

java -cp out com.minikafka.step5.Step5Driver
```

---

# Step 5.12 — Expected Output

```text
---- FIRST POLL ----

MessageRecord{offset=0, key='order-1', value='created'}
MessageRecord{offset=1, key='order-2', value='paid'}
MessageRecord{offset=2, key='order-3', value='shipped'}
MessageRecord{offset=3, key='order-4', value='delivered'}

Consumer next offset = 4

Appending new messages...

---- SECOND POLL ----

MessageRecord{offset=4, key='order-5', value='returned'}
MessageRecord{offset=5, key='order-6', value='refund'}

Consumer final offset = 6
```

---

# Step 5.13 — What We Just Built

We now built:

```text
Incremental consumer polling
```

This is a huge milestone.

Before:

```text
Read everything repeatedly
```

Now:

```text
Read only new messages
```

---

# Step 5.14 — Similarity With Real Kafka

Real Kafka consumers:

```text
remember committed offsets
poll incrementally
resume after restart
```

We are now building the same model.

Later we will add:

```text
offset commit storage
consumer groups
partition assignment
rebalancing
```

---

# Step 5.15 — Current Architecture

```text
Producer
   |
   v
LogSegment.append()
   |
   v
Partition log file
   |
   v
Consumer reads using offsets
```

This is now a real event-streaming architecture foundation.

---

# Step 5.16 — Concepts Learned

```text
consumer polling
offset checkpoints
incremental reads
resume consumption
stream processing basics
```

---

# Step 5.17 — Current MiniKafka State

```text
Supported:
[yes] append-only storage
[yes] offsets
[yes] serialization
[yes] LogSegment abstraction
[yes] offset-based reads
[yes] incremental consumption

Not yet:
[no] Partition class
[no] Topic class
[no] Broker
[no] Producer API
[no] Consumer API abstraction
[no] multiple partitions
```

---

# Step 5 Completion Checklist

```text
[ ] You understand offset-based reads
[ ] You understand consumer polling
[ ] You understand incremental reads
[ ] You understand consumer checkpoints
[ ] You understand why Kafka scales
```

---

# Step 5 Final Mental Model

```text
Consumer stores offset
         |
         v
Poll broker
         |
         v
Broker reads from offset
         |
         v
Return only new records
         |
         v
Consumer processes messages
         |
         v
Update offset
```

---

# Next Step

Next we build:

```text
006_Partition_Abstraction
```

This is where MiniKafka becomes truly Kafka-like.

We will create:

```text
Topic
   |
   +--> Partition 0
   |
   +--> Partition 1
   |
   +--> Partition 2
```

and learn:

```text
parallelism
ordering guarantees
partition ownership
```
