# 006_Partition_Abstraction

# MiniKafka Step 6 — Partition Abstraction

## Goal

Until now, we directly worked with one log file:

```text
orders-0.log
```

But real Kafka does not expose raw log files directly.

Kafka organizes data using:

```text
Topic
   |
   +--> Partition 0
   |
   +--> Partition 1
   |
   +--> Partition 2
```

In this step, we build:

```java
Partition
```

This becomes the next major abstraction layer above LogSegment.

---

# Big Picture

Before:

```text
Driver
   |
   v
LogSegment
   |
   v
Log file
```

After:

```text
Driver
   |
   v
Partition
   |
   v
LogSegment
   |
   v
Log file
```

Partition becomes the owner of one log segment.

---

# Why Partitions Exist

Kafka partitions solve:

```text
parallelism
scalability
ordering
distributed ownership
```

Example:

```text
Topic: orders

Partition 0
Partition 1
Partition 2
```

Different consumers can process different partitions simultaneously.

---

# Important Kafka Rule

Ordering is guaranteed:

```text
inside ONE partition only
```

Example:

```text
Partition 0

0 -> created
1 -> paid
2 -> shipped
```

Order is preserved.

Across partitions:

```text
NO global ordering guarantee
```

Very important system design concept.

---

# Step 6.1 — Folder Structure

```text
MiniKafka/
├── data/
│   └── phase1/
│       ├── orders-0.log
│       ├── orders-1.log
│       └── orders-2.log
└── src/
    └── main/
        └── java/
            └── com/
                └── minikafka/
                    └── step6/
                        ├── MessageRecord.java
                        ├── RecordSerializer.java
                        ├── LogSegment.java
                        ├── Partition.java
                        └── Step6Driver.java
```

---

# Step 6.2 — Reuse Previous Classes

Reuse:

```text
MessageRecord.java
RecordSerializer.java
LogSegment.java
```

No major changes required.

---

# Step 6.3 — What Is Partition Responsible For?

Partition should manage:

```text
[yes] partition id
[yes] one LogSegment
[yes] append records
[yes] read records
[yes] offset-based reads
```

Partition becomes a logical Kafka abstraction.

---

# Step 6.4 — Partition Class

File:

```text
Partition.java
```

Code:

```java
package com.minikafka.step6;

import java.io.IOException;
import java.util.List;

public class Partition {

    private final int partitionId;

    private final LogSegment segment;

    public Partition(String topicName,
                     int partitionId)
            throws IOException {

        this.partitionId = partitionId;

        String filePath =
                "data/phase1/"
                        + topicName
                        + "-"
                        + partitionId
                        + ".log";

        this.segment =
                new LogSegment(filePath);
    }

    public long append(String key,
                       String value)
            throws IOException {

        return segment.append(key, value);
    }

    public List<MessageRecord> readAll()
            throws IOException {

        return segment.readAll();
    }

    public List<MessageRecord> readFromOffset(
            long offset)
            throws IOException {

        return segment.readFromOffset(offset);
    }

    public int getPartitionId() {
        return partitionId;
    }
}
```

---

# Step 6.5 — What Happens Internally?

This:

```java
Partition partition =
    new Partition("orders", 0);
```

creates:

```text
Partition Object
       |
       +--> partitionId = 0
       |
       +--> LogSegment
                |
                v
         orders-0.log
```

Visual:

```text
Partition 0
     |
     v
LogSegment
     |
     v
orders-0.log
```

Partition now owns storage.

---

# Step 6.6 — Why This Abstraction Matters

Before:

```text
Driver knew storage details
```

Now:

```text
Driver talks to Partition
Partition manages storage internally
```

This is abstraction layering.

Very important backend design principle.

---

# Step 6.7 — Driver Program

File:

```text
Step6Driver.java
```

Code:

```java
package com.minikafka.step6;

import java.util.List;

public class Step6Driver {

    public static void main(String[] args)
            throws Exception {

        Partition partition0 =
                new Partition("orders", 0);

        Partition partition1 =
                new Partition("orders", 1);

        partition0.append(
                "order-1",
                "created"
        );

        partition0.append(
                "order-2",
                "paid"
        );

        partition1.append(
                "order-3",
                "shipped"
        );

        partition1.append(
                "order-4",
                "delivered"
        );

        System.out.println(
                "---- PARTITION 0 ----"
        );

        List<MessageRecord> p0 =
                partition0.readAll();

        for (MessageRecord record : p0) {
            System.out.println(record);
        }

        System.out.println();

        System.out.println(
                "---- PARTITION 1 ----"
        );

        List<MessageRecord> p1 =
                partition1.readAll();

        for (MessageRecord record : p1) {
            System.out.println(record);
        }
    }
}
```

---

# Step 6.8 — What Files Are Created?

This creates:

```text
data/phase1/orders-0.log
data/phase1/orders-1.log
```

Visual:

```text
orders topic
    |
    +--> partition 0 -> orders-0.log
    |
    +--> partition 1 -> orders-1.log
```

Now MiniKafka has multiple partitions.

Huge milestone.

---

# Step 6.9 — Expected Output

```text
---- PARTITION 0 ----

MessageRecord{offset=0, key='order-1', value='created'}
MessageRecord{offset=1, key='order-2', value='paid'}

---- PARTITION 1 ----

MessageRecord{offset=0, key='order-3', value='shipped'}
MessageRecord{offset=1, key='order-4', value='delivered'}
```

---

# Important Observation

Offsets are:

```text
partition-local
```

Partition 0:

```text
0
1
```

Partition 1:

```text
0
1
```

This is exactly how Kafka works.

There is NO global offset across all partitions.

Very important concept.

---

# Step 6.10 — Visual Partition Architecture

```text
Topic: orders
    |
    +-------------------+
    |                   |
    v                   v

Partition 0         Partition 1
    |                   |
    v                   v

orders-0.log       orders-1.log
```

---

# Step 6.11 — Why Partitions Enable Scaling

Without partitions:

```text
one consumer
one log
limited throughput
```

With partitions:

```text
multiple consumers
parallel reads
parallel writes
higher throughput
```

Kafka scales horizontally using partitions.

---

# Step 6.12 — Current Architecture

```text
Partition
    |
    v
LogSegment
    |
    v
Physical log file
```

Next:

```text
Topic
   |
   +--> Partition 0
   |
   +--> Partition 1
```

---

# Step 6.13 — Similarity With Real Kafka

Real Kafka topics contain:

```text
multiple partitions
multiple segment files
partition leaders
replicas
```

We are now modeling the same architecture.

---

# Step 6.14 — Concepts Learned

```text
partitioning
parallelism
ordering guarantees
partition-local offsets
abstraction layering
```

---

# Step 6.15 — Current MiniKafka State

```text
Supported:
[yes] append-only storage
[yes] offsets
[yes] incremental reads
[yes] LogSegment abstraction
[yes] Partition abstraction
[yes] multiple partition logs

Not yet:
[no] Topic abstraction
[no] Broker
[no] Producer API
[no] Consumer API
[no] key-based routing
```

---

# Step 6 Completion Checklist

```text
[ ] You created Partition abstraction
[ ] You understand partition-local offsets
[ ] You understand ordering guarantee
[ ] You understand why Kafka uses partitions
[ ] You understand partition scaling
```

---

# Step 6 Final Mental Model

```text
Producer sends message
         |
         v
Partition receives message
         |
         v
Partition delegates to LogSegment
         |
         v
LogSegment appends to log file
         |
         v
Consumer reads from partition
```

---

# Next Step

Next we build:

```text
007_Topic_Abstraction
```

This is where MiniKafka becomes a real Kafka-like hierarchy.

We will build:

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
topic metadata
partition registry
topic-level operations
```
