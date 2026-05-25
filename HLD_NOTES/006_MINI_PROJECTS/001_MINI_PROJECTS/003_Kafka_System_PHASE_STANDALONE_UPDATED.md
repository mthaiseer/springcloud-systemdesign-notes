# MiniKafka_From_Scratch_Java21.md

# Clickable Index

- [Part 0 — Goal](#part-0--goal)
- [Part 1 — Why Build MiniKafka](#part-1--why-build-minikafka)
- [Part 2 — What Kafka Really Is](#part-2--what-kafka-really-is)
- [Part 3 — Learning Model](#part-3--learning-model)
- [Part 4 — Final MiniKafka Architecture](#part-4--final-minikafka-architecture)
- [Part 5 — Core Kafka Concepts From First Principles](#part-5--core-kafka-concepts-from-first-principles)
- [Part 6 — Final Folder Structure](#part-6--final-folder-structure)
- [Part 7 — Maven Setup](#part-7--maven-setup)
- [Part 8 — Phase 1: Append-Only Log](#part-8--phase-1-append-only-log)
- [Part 9 — Phase 2: Segment Files](#part-9--phase-2-segment-files)
- [Part 10 — Phase 3: Index File](#part-10--phase-3-index-file)
- [Part 11 — Phase 4: Topic and Partition](#part-11--phase-4-topic-and-partition)
- [Part 12 — Phase 5: Producer API](#part-12--phase-5-producer-api)
- [Part 13 — Phase 6: Consumer API](#part-13--phase-6-consumer-api)
- [Part 14 — Phase 7: Offset Manager](#part-14--phase-7-offset-manager)
- [Part 15 — Phase 8: Batching](#part-15--phase-8-batching)
- [Part 16 — Phase 9: Retention](#part-16--phase-9-retention)
- [Part 17 — Phase 10: Backpressure](#part-17--phase-10-backpressure)
- [Part 18 — Phase 11: Consumer Groups](#part-18--phase-11-consumer-groups)
- [Part 19 — Phase 12: Rebalancing](#part-19--phase-12-rebalancing)
- [Part 20 — Phase 13: Broker and Network API](#part-20--phase-13-broker-and-network-api)
- [Part 21 — Phase 14: Replication](#part-21--phase-14-replication)
- [Part 22 — Phase 15: Leader/Follower](#part-22--phase-15-leaderfollower)
- [Part 23 — Phase 16: ISR and Ack Modes](#part-23--phase-16-isr-and-ack-modes)
- [Part 24 — Phase 17: Page Cache, mmap, and Zero-Copy](#part-24--phase-17-page-cache-mmap-and-zero-copy)
- [Part 25 — Phase 18: Metrics and Observability](#part-25--phase-18-metrics-and-observability)
- [Part 26 — Phase 19: Load Testing](#part-26--phase-19-load-testing)
- [Part 27 — Phase 20: Production Hardening](#part-27--phase-20-production-hardening)
- [Part 28 — Complete Java Code Pack](#part-28--complete-java-code-pack)
- [Part 29 — Driver Classes](#part-29--driver-classes)
- [Part 30 — JUnit Tests](#part-30--junit-tests)
- [Part 31 — Performance Tuning Playbook](#part-31--performance-tuning-playbook)
- [Part 32 — Failure Handling](#part-32--failure-handling)
- [Part 33 — Scaling Discussion](#part-33--scaling-discussion)
- [Part 34 — Interview Explanation](#part-34--interview-explanation)
- [Part 35 — What This Teaches For Distributed Systems](#part-35--what-this-teaches-for-distributed-systems)

---

# Part 0 — Goal

Build a MiniKafka from scratch using Java 21.

This updated version adds per-phase step-by-step build instructions, driver classes to test the logic, expected outputs, and verification checklists before/around dry runs.

This is not a production Kafka replacement.

This is a learning system to understand Kafka internals:

```text
append-only logs
segments
indexes
partitions
producers
consumers
offsets
batching
retention
backpressure
consumer groups
rebalance
replication
leader/follower
ISR
ack modes
page cache
zero-copy
metrics
load testing
failure handling
```

Main goal:

```text
Understand WHY Kafka is designed this way.
```

---

# Part 1 — Why Build MiniKafka

Reading Kafka theory gives vocabulary.

Building MiniKafka gives intuition.

Before building:

```text
Kafka has topics and partitions.
```

After building:

```text
Partition exists because one log cannot scale writes and reads.
Offset exists because Kafka does not remove consumed messages.
Batching exists because syscall and network overhead dominate small writes.
Replication exists because a broker can die.
ISR exists because not every replica is safe to acknowledge.
Consumer group exists because one consumer is too slow.
```

That is real system design understanding.

---

# Part 2 — What Kafka Really Is

Kafka is fundamentally:

```text
A distributed append-only commit log.
```

Not just a queue.

Traditional queue:

```text
Producer -> Queue -> Consumer
message removed after consume
```

Kafka:

```text
Producer -> Append-only partition log -> Consumer reads by offset
message remains until retention deletes it
```

Kafka mental model:

```text
Topic: orders

Partition 0:
offset 0 -> order-created
offset 1 -> order-paid
offset 2 -> order-shipped

Partition 1:
offset 0 -> payment-created
offset 1 -> payment-confirmed
```

Key property:

```text
Ordering is guaranteed only inside a partition.
```

---

# Part 3 — Learning Model

Use this cycle in every phase:

```text
BUILD SYSTEM
    ↓
LOAD TEST
    ↓
OBSERVE BOTTLENECK
    ↓
LEARN INTERNALS EXPLAINING BOTTLENECK
    ↓
OPTIMIZE
    ↓
MEASURE AGAIN
```

Example:

```text
Build single append-only log
    ↓
Load test 100k messages
    ↓
Observe slow reads after file grows
    ↓
Learn index file
    ↓
Add offset-to-position index
    ↓
Measure faster reads
```

---

# Part 4 — Final MiniKafka Architecture

```text
                    +------------------+
                    |    Producer      |
                    +---------+--------+
                              |
                              v
                    +------------------+
                    |  Producer Buffer |
                    +---------+--------+
                              |
                              v
+------------------------------------------------------+
|                    MiniKafka Broker                  |
|------------------------------------------------------|
| TopicManager                                         |
| PartitionManager                                     |
| LogStorageEngine                                     |
| SegmentManager                                       |
| OffsetIndex                                          |
| RetentionManager                                     |
| ConsumerGroupCoordinator                             |
| ReplicationManager                                   |
| MetricsRegistry                                      |
+----------------------+-------------------------------+
                       |
                       v
              +------------------+
              | Topic: orders    |
              +------------------+
               /        |       \
              v         v        v
       Partition-0 Partition-1 Partition-2
          |           |          |
          v           v          v
       Segments    Segments   Segments
```

Broker cluster:

```text
Broker-1 leader for orders-0
Broker-2 follower for orders-0

Broker-2 leader for orders-1
Broker-3 follower for orders-1
```

---

# Part 5 — Core Kafka Concepts From First Principles

## Topic

Logical stream of events.

Example:

```text
orders
payments
driver-location
logs
```

## Partition

Physical append-only log.

Why partition?

```text
parallel writes
parallel reads
horizontal scaling
ordering per partition
```

## Offset

Position of message inside partition.

```text
offset = 0, 1, 2, 3...
```

## Producer

Writes messages.

## Consumer

Reads messages from offset.

## Consumer Group

Multiple consumers sharing partitions.

## Broker

Server that stores partitions.

## Segment

Large log file split into smaller files.

Why?

```text
easier deletion
faster recovery
bounded file size
```

## Index

Maps:

```text
offset -> byte position in log file
```

Why?

```text
fast seek to offset
```

## Replication

Copies partition to multiple brokers.

## ISR

In-sync replicas.

Only replicas caught up enough are safe.

## Ack Mode

Producer durability level:

```text
acks=0  -> no wait
acks=1  -> leader written
acks=all -> leader + ISR
```

---

# Part 6 — Final Folder Structure

```text
mini-kafka/
├── pom.xml
├── data/
├── src/main/java/com/minikafka/
│   ├── Main.java
│   ├── common/
│   │   ├── MessageRecord.java
│   │   ├── TopicPartition.java
│   │   ├── MiniKafkaException.java
│   │   └── Time.java
│   ├── storage/
│   │   ├── LogSegment.java
│   │   ├── SegmentFile.java
│   │   ├── OffsetIndex.java
│   │   ├── PartitionLog.java
│   │   └── RetentionManager.java
│   ├── topic/
│   │   ├── Topic.java
│   │   └── TopicManager.java
│   ├── partition/
│   │   └── PartitionRouter.java
│   ├── producer/
│   │   ├── MiniProducer.java
│   │   ├── ProducerConfig.java
│   │   └── AckMode.java
│   ├── consumer/
│   │   ├── MiniConsumer.java
│   │   └── ConsumerRecord.java
│   ├── coordinator/
│   │   ├── OffsetManager.java
│   │   ├── ConsumerGroupCoordinator.java
│   │   └── RebalanceStrategy.java
│   ├── replication/
│   │   ├── BrokerNode.java
│   │   ├── ReplicaState.java
│   │   ├── ReplicationManager.java
│   │   └── InSyncReplicaSet.java
│   ├── broker/
│   │   ├── MiniKafkaBroker.java
│   │   └── BrokerConfig.java
│   └── metrics/
│       └── MetricsRegistry.java
└── src/test/java/com/minikafka/
    ├── LogSegmentTest.java
    ├── PartitionLogTest.java
    ├── ProducerConsumerTest.java
    ├── OffsetManagerTest.java
    └── ConsumerGroupCoordinatorTest.java
```

---

# Part 7 — Maven Setup

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.minikafka</groupId>
    <artifactId>mini-kafka</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.release>21</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>5.10.2</junit.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
            </plugin>
        </plugins>
    </build>
</project>
```

---

# Part 8 — Phase 1: Append-Only Log

## What are we building?

A durable log file where records are only appended.

## Why?

Sequential disk writes are fast.

Random updates are slower.

## Data flow

```text
Producer
   |
   v
append(record)
   |
   v
log file
```

## MessageRecord.java

```java
package com.minikafka.common;

public record MessageRecord(
        long offset,
        long timestampMillis,
        String key,
        String value
) {
}
```

## LogSegment.java

```java
package com.minikafka.storage;

import com.minikafka.common.MessageRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LogSegment {
    private final File file;
    private long nextOffset;

    public LogSegment(String filePath) throws IOException {
        this.file = new File(filePath);
        File parent = file.getParentFile();

        if (parent != null) {
            parent.mkdirs();
        }

        if (!file.exists()) {
            file.createNewFile();
        }

        this.nextOffset = recoverNextOffset();
    }

    public synchronized long append(String key, String value) throws IOException {
        long offset = nextOffset++;

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {

            writer.write(offset + "|" + System.currentTimeMillis() + "|" + key + "|" + value);
            writer.newLine();
        }

        return offset;
    }

    public synchronized List<MessageRecord> readAll() throws IOException {
        List<MessageRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 4);

                if (parts.length == 4) {
                    records.add(new MessageRecord(
                            Long.parseLong(parts[0]),
                            Long.parseLong(parts[1]),
                            parts[2],
                            parts[3]
                    ));
                }
            }
        }

        return records;
    }

    public File file() {
        return file;
    }

    public long nextOffset() {
        return nextOffset;
    }

    private long recoverNextOffset() throws IOException {
        long maxOffset = -1;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);

                if (parts.length > 0 && !parts[0].isBlank()) {
                    maxOffset = Math.max(maxOffset, Long.parseLong(parts[0]));
                }
            }
        }

        return maxOffset + 1;
    }
}
```

## Step-By-Step: What We Are Building

In this phase, we build the smallest Kafka-like storage unit:

```text
LogSegment = one append-only file
```

Step by step:

```text
Step 1: Create MessageRecord model.
Step 2: Create LogSegment class.
Step 3: On startup, create the log file if missing.
Step 4: Recover nextOffset by scanning existing records.
Step 5: append(key, value) writes one line at the end.
Step 6: readAll() loads all records for testing.
Step 7: Verify offsets increase monotonically.
```

What this teaches:

```text
Kafka's foundation is not a queue.
It is a durable append-only log.
```

## Driver Class To Test Phase 1

Create:

```java
package com.minikafka;

import com.minikafka.common.MessageRecord;
import com.minikafka.storage.LogSegment;

public class Phase1AppendOnlyLogDriver {
    public static void main(String[] args) throws Exception {
        LogSegment segment = new LogSegment("data/phase1/orders-0.log");

        long o1 = segment.append("order-1", "created");
        long o2 = segment.append("order-2", "paid");

        System.out.println("offset1=" + o1);
        System.out.println("offset2=" + o2);

        for (MessageRecord record : segment.readAll()) {
            System.out.println(record);
        }
    }
}
```

## Run Phase 1

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase1AppendOnlyLogDriver
```

## Expected Output

```text
offset1=0
offset2=1
MessageRecord[offset=0, ... key=order-1, value=created]
MessageRecord[offset=1, ... key=order-2, value=paid]
```

## What To Verify

```text
1. data/phase1/orders-0.log is created.
2. Records are appended, not overwritten.
3. Offset increases monotonically.
4. Restarting the driver continues from next offset.
```

## Phase 1 JUnit Test

```java
@Test
void phase1ShouldAppendAndReadRecords() throws Exception {
    LogSegment segment = new LogSegment("data/test/phase1.log");

    long offset = segment.append("k1", "v1");

    var records = segment.readAll();

    assertTrue(offset >= 0);
    assertFalse(records.isEmpty());
    assertEquals("k1", records.get(records.size() - 1).key());
    assertEquals("v1", records.get(records.size() - 1).value());
}
```

## Dry run

```text
append("order-1", "created")
    ↓
offset = 0
    ↓
write line:
0|timestamp|order-1|created

append("order-2", "paid")
    ↓
offset = 1
    ↓
write line:
1|timestamp|order-2|paid
```

## Bottleneck

This version scans whole file to read.

Next we need:

```text
segments
index file
```

---

# Part 9 — Phase 2: Segment Files

## What are we building?

Split one giant log into segment files.

## Why?

A single huge file is difficult to:

```text
delete
recover
compact
search
manage
```

Kafka uses log segments.

## Segment layout

```text
orders-0/
├── 00000000000000000000.log
├── 00000000000000000000.index
├── 00000000000000001000.log
└── 00000000000000001000.index
```

## SegmentFile.java

```java
package com.minikafka.storage;

import java.io.File;

public record SegmentFile(
        long baseOffset,
        File logFile,
        File indexFile
) {
}
```

## Rolling rule

```text
if current segment size > maxSegmentBytes:
    create new segment
```

## Production meaning

Segment rolling enables retention:

```text
delete old segment files instead of deleting individual records
```

---

# Part 10 — Phase 3: Index File

## What are we building?

An offset index.

## Why?

Without index:

```text
read offset 900000
    ↓
scan from beginning
```

With index:

```text
offset -> file byte position
```

## OffsetIndex.java

```java
package com.minikafka.storage;

import java.io.*;
import java.util.NavigableMap;
import java.util.TreeMap;

public class OffsetIndex {
    private final File file;
    private final NavigableMap<Long, Long> offsetToPosition = new TreeMap<>();

    public OffsetIndex(File file) throws IOException {
        this.file = file;

        File parent = file.getParentFile();

        if (parent != null) {
            parent.mkdirs();
        }

        if (!file.exists()) {
            file.createNewFile();
        }

        load();
    }

    public synchronized void append(long offset, long position) throws IOException {
        offsetToPosition.put(offset, position);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(offset + "," + position);
            writer.newLine();
        }
    }

    public synchronized long positionFor(long offset) {
        var entry = offsetToPosition.floorEntry(offset);
        return entry == null ? 0L : entry.getValue();
    }

    private void load() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 2) {
                    offsetToPosition.put(
                            Long.parseLong(parts[0]),
                            Long.parseLong(parts[1])
                    );
                }
            }
        }
    }
}
```

## Step-By-Step: What We Are Building

In Phase 1, one log file grows forever.

In this phase, we introduce segment files:

```text
PartitionLog
    ├── segment-0.log
    ├── segment-1.log
    └── segment-2.log
```

Step by step:

```text
Step 1: Define SegmentFile model.
Step 2: Define maxSegmentBytes.
Step 3: Keep one active segment.
Step 4: Append records to active segment.
Step 5: If active segment crosses size limit, roll to new segment.
Step 6: New segment baseOffset = next record offset.
Step 7: Later retention can delete old closed segments.
```

Why this matters:

```text
Kafka deletes old segment files.
It does not delete individual consumed messages.
```

## Driver Class To Test Phase 2

Create this once you implement `PartitionLog`:

```java
package com.minikafka;

import com.minikafka.storage.PartitionLog;

public class Phase2SegmentRollingDriver {
    public static void main(String[] args) throws Exception {
        PartitionLog log = new PartitionLog(
                "data/phase2/orders-0",
                100 // very small segment size for testing
        );

        for (int i = 1; i <= 20; i++) {
            log.append("order-" + i, "event-" + i);
        }

        log.printSegments();
    }
}
```

## Run Phase 2

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase2SegmentRollingDriver
```

## Expected Output

```text
segment baseOffset=0
segment baseOffset=5
segment baseOffset=10
segment baseOffset=15
```

Exact numbers depend on record size.

## What To Verify

```text
1. Multiple .log files are created.
2. Old segment is no longer written after rolling.
3. Active segment receives new records.
4. Segment base offset matches first record in that segment.
```

## Phase 2 JUnit Test Idea

```java
@Test
void phase2ShouldRollSegmentsWhenSizeLimitReached() throws Exception {
    PartitionLog log = new PartitionLog("data/test/phase2", 100);

    for (int i = 0; i < 50; i++) {
        log.append("k" + i, "v" + i);
    }

    assertTrue(log.segmentCount() > 1);
}
```

## Dry run

```text
offset 0 -> file position 0
offset 1 -> file position 37
offset 2 -> file position 71
```

To read offset 2:

```text
find position 71
seek there
read forward
```

---

# Part 11 — Phase 4: Topic and Partition

## What are we building?

Topic with multiple partitions.

## Why?

Single log cannot scale.

Partitions provide:

```text
write parallelism
read parallelism
ordering per partition
```

## TopicPartition.java

```java
package com.minikafka.common;

public record TopicPartition(
        String topic,
        int partition
) {
    public String id() {
        return topic + "-" + partition;
    }
}
```

## PartitionRouter.java

```java
package com.minikafka.partition;

public class PartitionRouter {
    public int route(String key, int partitionCount) {
        if (partitionCount <= 0) {
            throw new IllegalArgumentException("partitionCount must be positive");
        }

        if (key == null) {
            return 0;
        }

        return Math.floorMod(key.hashCode(), partitionCount);
    }
}
```

## Step-By-Step: What We Are Building

Now we create topic-partition routing.

Step by step:

```text
Step 1: Define TopicPartition model.
Step 2: Create PartitionRouter.
Step 3: If key is null, send to default partition.
Step 4: If key exists, calculate hash(key).
Step 5: Convert hash to non-negative using Math.floorMod.
Step 6: Choose partition = hash % partitionCount.
Step 7: Same key always goes to same partition.
```

## Driver Class To Test Phase 4

```java
package com.minikafka;

import com.minikafka.partition.PartitionRouter;

public class Phase4PartitionRouterDriver {
    public static void main(String[] args) {
        PartitionRouter router = new PartitionRouter();

        for (int i = 1; i <= 10; i++) {
            String key = "user-" + i;
            int partition = router.route(key, 3);
            System.out.println(key + " -> partition-" + partition);
        }

        System.out.println("same key test:");
        System.out.println(router.route("user-1", 3));
        System.out.println(router.route("user-1", 3));
    }
}
```

## Run Phase 4

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase4PartitionRouterDriver
```

## Expected Output

```text
user-1 -> partition-X
user-2 -> partition-Y
...
same key test:
X
X
```

## What To Verify

```text
1. Same key maps to same partition.
2. Partition is never negative.
3. Partition is always less than partitionCount.
4. Different keys may distribute across partitions.
```

## Phase 4 JUnit Test

```java
@Test
void phase4ShouldRouteSameKeyToSamePartition() {
    PartitionRouter router = new PartitionRouter();

    int p1 = router.route("user-1", 3);
    int p2 = router.route("user-1", 3);

    assertEquals(p1, p2);
    assertTrue(p1 >= 0);
    assertTrue(p1 < 3);
}
```

## Dry run

```text
key = user-101
partitionCount = 3

hash(user-101) % 3 = 1

message goes to partition-1
```

## Tradeoff

Same key always maps to same partition.

Benefit:

```text
ordering per key
```

Risk:

```text
hot key creates hot partition
```

---

# Part 12 — Phase 5: Producer API

## What are we building?

A MiniProducer with:

```text
send(topic, key, value)
```

## Why?

Producer hides:

```text
partition routing
batching
backpressure
ack handling
```

## ProducerConfig.java

```java
package com.minikafka.producer;

public record ProducerConfig(
        int bufferCapacity,
        int batchSize,
        AckMode ackMode
) {
}
```

## AckMode.java

```java
package com.minikafka.producer;

public enum AckMode {
    NONE,
    LEADER,
    ALL
}
```

## MiniProducer.java

```java
package com.minikafka.producer;

import com.minikafka.broker.MiniKafkaBroker;

public class MiniProducer {
    private final MiniKafkaBroker broker;

    public MiniProducer(MiniKafkaBroker broker) {
        this.broker = broker;
    }

    public long send(String topic, String key, String value) throws Exception {
        return broker.produce(topic, key, value);
    }
}
```

## Flow

```text
Application
   |
producer.send()
   |
route key to partition
   |
append to partition log
   |
return offset
```

---

# Part 13 — Phase 6: Consumer API

## What are we building?

A MiniConsumer that reads from topic partition by offset.

## ConsumerRecord.java

```java
package com.minikafka.consumer;

public record ConsumerRecord(
        String topic,
        int partition,
        long offset,
        String key,
        String value
) {
}
```

## MiniConsumer.java

```java
package com.minikafka.consumer;

import com.minikafka.broker.MiniKafkaBroker;

import java.util.List;

public class MiniConsumer {
    private final MiniKafkaBroker broker;

    public MiniConsumer(MiniKafkaBroker broker) {
        this.broker = broker;
    }

    public List<ConsumerRecord> poll(String topic, int partition, long offset) throws Exception {
        return broker.consume(topic, partition, offset);
    }
}
```

## Key idea

Kafka consumer does not remove messages.

Consumer controls progress using offset.

---

# Part 14 — Phase 7: Offset Manager

## What are we building?

A store for committed offsets.

## Why?

If consumer crashes, it must resume.

## OffsetManager.java

```java
package com.minikafka.coordinator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OffsetManager {
    private final Map<String, Long> offsets = new ConcurrentHashMap<>();

    public void commit(String groupId, String topic, int partition, long nextOffset) {
        offsets.put(key(groupId, topic, partition), nextOffset);
    }

    public long committedOffset(String groupId, String topic, int partition) {
        return offsets.getOrDefault(key(groupId, topic, partition), 0L);
    }

    private String key(String groupId, String topic, int partition) {
        return groupId + ":" + topic + ":" + partition;
    }
}
```

## Delivery semantics

If commit after processing:

```text
at-least-once
```

If commit before processing:

```text
at-most-once
```

Exactly-once needs more coordination.

---

# Part 15 — Phase 8: Batching

## What are we building?

Batch writes.

## Why?

Without batching:

```text
1 message = 1 write
1 message = 1 flush
```

With batching:

```text
1000 messages = 1 write batch
```

## Batch flow

```text
Producer send
   ↓
buffer
   ↓ when batch full or linger timeout
flush batch
```

## Bottleneck solved

```text
syscall overhead
disk flush overhead
network packet overhead
```

## Tradeoff

Batching improves throughput but can increase latency.

---

# Part 16 — Phase 9: Retention

## What are we building?

Delete old segment files.

## Why?

Kafka does not keep logs forever.

Retention policies:

```text
time-based
size-based
```

## RetentionManager.java

```java
package com.minikafka.storage;

import java.io.File;

public class RetentionManager {
    private final long retentionMillis;

    public RetentionManager(long retentionMillis) {
        this.retentionMillis = retentionMillis;
    }

    public void cleanup(File partitionDir) {
        File[] files = partitionDir.listFiles();

        if (files == null) {
            return;
        }

        long now = System.currentTimeMillis();

        for (File file : files) {
            if (file.isFile() && now - file.lastModified() > retentionMillis) {
                file.delete();
            }
        }
    }
}
```

## Production consideration

Never delete active segment.

Never delete data needed by consumers if retention policy promises availability.

---

# Part 17 — Phase 10: Backpressure

## What are we building?

Bounded producer buffer.

## Why?

Unbounded memory is dangerous.

If producer writes faster than broker can persist:

```text
queue grows
heap grows
GC increases
OOM risk
```

Backpressure strategies:

```text
block
reject
drop
slow producer
scale broker
```

## Production insight

Backpressure protects the system.

Without it, system fails catastrophically.

---

# Part 18 — Phase 11: Consumer Groups

## What are we building?

Group of consumers sharing partitions.

## Why?

One consumer may be too slow.

Consumer group:

```text
group = order-service

consumer-1 -> partition-0
consumer-2 -> partition-1
consumer-3 -> partition-2
```

Rule:

```text
One partition can be assigned to only one consumer inside same group.
```

## ConsumerGroupCoordinator.java

```java
package com.minikafka.coordinator;

import java.util.*;

public class ConsumerGroupCoordinator {
    public Map<String, List<Integer>> assignPartitions(
            List<String> consumerIds,
            int partitionCount
    ) {
        Map<String, List<Integer>> assignment = new LinkedHashMap<>();

        for (String consumer : consumerIds) {
            assignment.put(consumer, new ArrayList<>());
        }

        for (int partition = 0; partition < partitionCount; partition++) {
            String consumer = consumerIds.get(partition % consumerIds.size());
            assignment.get(consumer).add(partition);
        }

        return assignment;
    }
}
```

## Step-By-Step: What We Are Building

Now we build a producer facade.

Step by step:

```text
Step 1: Create MiniProducer.
Step 2: MiniProducer receives broker reference.
Step 3: Application calls send(topic, key, value).
Step 4: Producer delegates to broker.
Step 5: Broker routes key to partition.
Step 6: Broker appends record to partition log.
Step 7: Producer receives offset.
```

In real Kafka, producer also handles:

```text
buffering
batching
retries
acks
compression
partition metadata
```

## Driver Class To Test Phase 5

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.producer.MiniProducer;

public class Phase5ProducerDriver {
    public static void main(String[] args) throws Exception {
        MiniKafkaBroker broker = new MiniKafkaBroker();
        broker.createTopic("orders", 3);

        MiniProducer producer = new MiniProducer(broker);

        long offset = producer.send("orders", "order-1", "created");

        System.out.println("produced offset=" + offset);
    }
}
```

## Run Phase 5

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase5ProducerDriver
```

## Expected Output

```text
produced offset=0
```

## What To Verify

```text
1. Topic must exist before producing.
2. Producer writes to one routed partition.
3. Offset is returned.
4. Data file under data/orders-X.log is updated.
```

## Phase 5 JUnit Test

```java
@Test
void phase5ProducerShouldWriteMessage() throws Exception {
    MiniKafkaBroker broker = new MiniKafkaBroker();
    broker.createTopic("orders", 3);

    MiniProducer producer = new MiniProducer(broker);

    long offset = producer.send("orders", "order-1", "created");

    assertTrue(offset >= 0);
}
```

## Dry run

```text
consumers = c1, c2
partitions = 0,1,2,3

c1 -> 0,2
c2 -> 1,3
```

---

# Part 19 — Phase 12: Rebalancing

## What are we building?

Reassign partitions when consumers join/leave.

## Why?

Consumer can die.

New consumers can join.

## Rebalance event

```text
consumer-3 joins
    ↓
coordinator recalculates assignment
    ↓
some partitions move
```

## Problem

Rebalance pauses consumption.

## Production concerns

```text
avoid frequent rebalance
heartbeat
session timeout
sticky assignment
cooperative rebalance
```

---

# Part 20 — Phase 13: Broker and Network API

## What are we building?

Broker facade.

For learning, first use in-process broker.

Later expose TCP/HTTP.

## MiniKafkaBroker.java

```java
package com.minikafka.broker;

import com.minikafka.common.MessageRecord;
import com.minikafka.consumer.ConsumerRecord;
import com.minikafka.partition.PartitionRouter;
import com.minikafka.storage.LogSegment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MiniKafkaBroker {
    private final Map<String, List<LogSegment>> topics = new ConcurrentHashMap<>();
    private final PartitionRouter router = new PartitionRouter();

    public void createTopic(String topic, int partitions) throws IOException {
        List<LogSegment> logs = new ArrayList<>();

        for (int p = 0; p < partitions; p++) {
            logs.add(new LogSegment("data/" + topic + "-" + p + ".log"));
        }

        topics.put(topic, logs);
    }

    public long produce(String topic, String key, String value) throws Exception {
        List<LogSegment> partitions = topics.get(topic);

        if (partitions == null) {
            throw new IllegalArgumentException("Unknown topic: " + topic);
        }

        int partition = router.route(key, partitions.size());

        return partitions.get(partition).append(key, value);
    }

    public List<ConsumerRecord> consume(String topic, int partition, long offset) throws Exception {
        List<LogSegment> partitions = topics.get(topic);

        if (partitions == null) {
            throw new IllegalArgumentException("Unknown topic: " + topic);
        }

        List<MessageRecord> records = partitions.get(partition).readAll();
        List<ConsumerRecord> result = new ArrayList<>();

        for (MessageRecord record : records) {
            if (record.offset() >= offset) {
                result.add(new ConsumerRecord(
                        topic,
                        partition,
                        record.offset(),
                        record.key(),
                        record.value()
                ));
            }
        }

        return result;
    }
}
```

---

# Part 21 — Phase 14: Replication

## What are we building?

Leader writes and follower copies.

## Why?

Broker or disk can fail.

Replication flow:

```text
Producer
   ↓
Leader partition
   ↓
Follower replicas
   ↓
Ack
```

## ReplicaState.java

```java
package com.minikafka.replication;

public enum ReplicaState {
    LEADER,
    FOLLOWER
}
```

## BrokerNode.java

```java
package com.minikafka.replication;

public record BrokerNode(
        String brokerId,
        String host,
        int port
) {
}
```

## ReplicationManager.java

```java
package com.minikafka.replication;

import java.util.HashSet;
import java.util.Set;

public class ReplicationManager {
    private final Set<String> inSyncReplicas = new HashSet<>();

    public void markInSync(String brokerId) {
        inSyncReplicas.add(brokerId);
    }

    public void markOutOfSync(String brokerId) {
        inSyncReplicas.remove(brokerId);
    }

    public boolean isInSync(String brokerId) {
        return inSyncReplicas.contains(brokerId);
    }

    public int isrSize() {
        return inSyncReplicas.size();
    }
}
```

## Production reality

Real replication needs:

```text
fetch requests
replication offsets
leader epoch
high watermark
failure detection
```

---

# Part 22 — Phase 15: Leader/Follower

## Why leader exists?

If all replicas accept writes independently:

```text
conflicting logs
split brain
inconsistent ordering
```

So one leader accepts writes.

Followers copy.

```text
Leader:
accept produce

Follower:
replicate from leader
```

## Failure

If leader dies:

```text
choose new leader from ISR
```

---

# Part 23 — Phase 16: ISR and Ack Modes

## ISR

ISR = in-sync replicas.

```text
leader
follower-1 caught up
follower-2 lagging
```

ISR:

```text
leader, follower-1
```

Not ISR:

```text
follower-2
```

## Ack modes

```text
acks=0
producer does not wait

acks=1
leader written

acks=all
leader and ISR written
```

## Tradeoff

| Ack | Durability | Latency |
|---|---|---|
| 0 | low | lowest |
| 1 | medium | medium |
| all | high | highest |

---

# Part 24 — Phase 17: Page Cache, mmap, and Zero-Copy

## Why Kafka is fast

Kafka is fast because:

```text
append-only writes
sequential IO
batching
OS page cache
zero-copy transfer
```

## Normal copy path

```text
disk -> kernel page cache -> JVM heap -> socket buffer -> network
```

## Zero-copy path

```text
disk -> kernel page cache -> socket
```

## Java concepts

```text
FileChannel
MappedByteBuffer
transferTo()
DirectByteBuffer
```

## Production insight

Huge JVM heap is not always best.

Kafka benefits from leaving memory for OS page cache.

---

# Part 25 — Phase 18: Metrics and Observability

## MetricsRegistry.java

```java
package com.minikafka.metrics;

import java.util.concurrent.atomic.AtomicLong;

public class MetricsRegistry {
    private final AtomicLong messagesIn = new AtomicLong();
    private final AtomicLong messagesOut = new AtomicLong();
    private final AtomicLong bytesIn = new AtomicLong();
    private final AtomicLong bytesOut = new AtomicLong();
    private final AtomicLong rejectedMessages = new AtomicLong();

    public void recordIn(long bytes) {
        messagesIn.incrementAndGet();
        bytesIn.addAndGet(bytes);
    }

    public void recordOut(long bytes) {
        messagesOut.incrementAndGet();
        bytesOut.addAndGet(bytes);
    }

    public void recordRejected() {
        rejectedMessages.incrementAndGet();
    }

    public String snapshot() {
        return "messagesIn=" + messagesIn.get() +
                ", messagesOut=" + messagesOut.get() +
                ", bytesIn=" + bytesIn.get() +
                ", bytesOut=" + bytesOut.get() +
                ", rejected=" + rejectedMessages.get();
    }
}
```

Important metrics:

```text
messages/sec
bytes/sec
producer latency
consumer lag
replication lag
queue depth
disk usage
segment count
GC pause
```

Consumer lag:

```text
latestOffset - committedOffset
```

---

# Part 26 — Phase 19: Load Testing

## What to measure

```text
producer throughput
consumer throughput
p99 produce latency
p99 consume latency
disk MB/sec
GC pauses
queue depth
consumer lag
```

## Simple load test idea

```text
1 producer
1 topic
3 partitions
1 million messages
```

Then test:

```text
batch size 1
batch size 100
batch size 1000
```

Observe:

```text
batching improves throughput
but may increase latency
```

---

# Part 27 — Phase 20: Production Hardening

Features to add:

```text
1. Binary record format instead of text lines
2. CRC checksum per record
3. Segment rolling by size
4. Segment rolling by time
5. Retention cleanup
6. Sparse offset index
7. Time index
8. Compression
9. TCP protocol
10. Backpressure
11. Replica fetcher thread
12. Leader election
13. High watermark
14. Consumer group heartbeat
15. Rebalance protocol
16. Metrics endpoint
17. Graceful shutdown
18. Recovery after crash
```

---

# Part 28 — Per-Phase Standalone Java Code Packs

Use this section as the coding track. Each phase is standalone enough to run by itself, but it also grows naturally from the previous phase. The idea is:

```text
Phase N = copy only the files in Phase N into src/main/java
run the Phase N driver
understand the delta from Phase N-1
then move to Phase N+1
```

Do not build one monster class. Each phase adds small objects with one clear responsibility.

---

## Phase 1 Code Pack — Append-Only Log

### Files added

```text
com/minikafka/common/MessageRecord.java
com/minikafka/storage/LogSegment.java
com/minikafka/Phase1AppendOnlyLogDriver.java
```

### MessageRecord.java

```java
package com.minikafka.common;

public record MessageRecord(
        long offset,
        long timestampMillis,
        String key,
        String value
) {
}
```

### LogSegment.java

```java
package com.minikafka.storage;

import com.minikafka.common.MessageRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LogSegment {
    private final File file;
    private long nextOffset;

    public LogSegment(String filePath) throws IOException {
        this.file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null) parent.mkdirs();
        if (!file.exists()) file.createNewFile();
        this.nextOffset = recoverNextOffset();
    }

    public synchronized long append(String key, String value) throws IOException {
        long offset = nextOffset++;
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            writer.write(offset + "|" + System.currentTimeMillis() + "|" + key + "|" + value);
            writer.newLine();
        }
        return offset;
    }

    public synchronized List<MessageRecord> readFrom(long startOffset) throws IOException {
        List<MessageRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 4);
                if (parts.length == 4) {
                    long offset = Long.parseLong(parts[0]);
                    if (offset >= startOffset) {
                        records.add(new MessageRecord(offset, Long.parseLong(parts[1]), parts[2], parts[3]));
                    }
                }
            }
        }
        return records;
    }

    public File file() {
        return file;
    }

    public long nextOffset() {
        return nextOffset;
    }

    private long recoverNextOffset() throws IOException {
        long maxOffset = -1;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length > 0 && !parts[0].isBlank()) {
                    maxOffset = Math.max(maxOffset, Long.parseLong(parts[0]));
                }
            }
        }
        return maxOffset + 1;
    }
}
```

### Phase1AppendOnlyLogDriver.java

```java
package com.minikafka;

import com.minikafka.common.MessageRecord;
import com.minikafka.storage.LogSegment;

public class Phase1AppendOnlyLogDriver {
    public static void main(String[] args) throws Exception {
        LogSegment segment = new LogSegment("data/phase1/orders-0.log");

        long o1 = segment.append("order-1", "created");
        long o2 = segment.append("order-2", "paid");

        System.out.println("offset1=" + o1);
        System.out.println("offset2=" + o2);

        for (MessageRecord record : segment.readFrom(0)) {
            System.out.println(record);
        }
    }
}
```

---

## Phase 2 Code Pack — Segment Rolling

### Delta from Phase 1

```text
Phase 1: one log file forever
Phase 2: many segment files inside one partition directory
```

### Files added

```text
com/minikafka/storage/SegmentFile.java
com/minikafka/storage/PartitionLog.java
com/minikafka/Phase2SegmentRollingDriver.java
```

Keep `MessageRecord.java` from Phase 1.

### SegmentFile.java

```java
package com.minikafka.storage;

import java.io.File;

public record SegmentFile(long baseOffset, File logFile) {
}
```

### PartitionLog.java

```java
package com.minikafka.storage;

import com.minikafka.common.MessageRecord;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PartitionLog {
    private final File dir;
    private final long maxSegmentBytes;
    private final List<LogSegment> segments = new ArrayList<>();
    private LogSegment activeSegment;

    public PartitionLog(String dirPath, long maxSegmentBytes) throws IOException {
        this.dir = new File(dirPath);
        this.maxSegmentBytes = maxSegmentBytes;
        if (!dir.exists()) dir.mkdirs();
        loadOrCreateSegments();
    }

    public synchronized long append(String key, String value) throws IOException {
        if (activeSegment.file().length() >= maxSegmentBytes) {
            rollSegment();
        }
        return activeSegment.append(key, value);
    }

    public synchronized List<MessageRecord> readFrom(long offset) throws IOException {
        List<MessageRecord> result = new ArrayList<>();
        for (LogSegment segment : segments) {
            result.addAll(segment.readFrom(offset));
        }
        return result;
    }

    public int segmentCount() {
        return segments.size();
    }

    public void printSegments() {
        for (LogSegment segment : segments) {
            System.out.println(segment.file().getName() + " size=" + segment.file().length());
        }
    }

    private void loadOrCreateSegments() throws IOException {
        File[] files = dir.listFiles((d, name) -> name.endsWith(".log"));
        if (files == null || files.length == 0) {
            activeSegment = new LogSegment(segmentPath(0));
            segments.add(activeSegment);
            return;
        }
        List<File> sorted = new ArrayList<>(List.of(files));
        sorted.sort(Comparator.comparing(File::getName));
        for (File file : sorted) {
            segments.add(new LogSegment(file.getPath()));
        }
        activeSegment = segments.get(segments.size() - 1);
    }

    private void rollSegment() throws IOException {
        long newBaseOffset = activeSegment.nextOffset();
        activeSegment = new LogSegment(segmentPath(newBaseOffset));
        segments.add(activeSegment);
    }

    private String segmentPath(long baseOffset) {
        return new File(dir, String.format("%020d.log", baseOffset)).getPath();
    }
}
```

### Phase2SegmentRollingDriver.java

```java
package com.minikafka;

import com.minikafka.storage.PartitionLog;

public class Phase2SegmentRollingDriver {
    public static void main(String[] args) throws Exception {
        PartitionLog log = new PartitionLog("data/phase2/orders-0", 120);

        for (int i = 1; i <= 20; i++) {
            log.append("order-" + i, "event-" + i);
        }

        log.printSegments();
        System.out.println("segmentCount=" + log.segmentCount());
    }
}
```

---

## Phase 3 Code Pack — Offset Index

### Delta from Phase 2

```text
Phase 2: read scans segment files
Phase 3: index maps offset -> byte position
```

### Files added/changed

```text
com/minikafka/storage/OffsetIndex.java
com/minikafka/storage/IndexedLogSegment.java
com/minikafka/Phase3OffsetIndexDriver.java
```

### OffsetIndex.java

```java
package com.minikafka.storage;

import java.io.*;
import java.util.NavigableMap;
import java.util.TreeMap;

public class OffsetIndex {
    private final File file;
    private final NavigableMap<Long, Long> offsetToPosition = new TreeMap<>();

    public OffsetIndex(File file) throws IOException {
        this.file = file;
        File parent = file.getParentFile();
        if (parent != null) parent.mkdirs();
        if (!file.exists()) file.createNewFile();
        load();
    }

    public synchronized void append(long offset, long position) throws IOException {
        offsetToPosition.put(offset, position);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(offset + "," + position);
            writer.newLine();
        }
    }

    public synchronized long positionFor(long offset) {
        var entry = offsetToPosition.floorEntry(offset);
        return entry == null ? 0L : entry.getValue();
    }

    private void load() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    offsetToPosition.put(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
                }
            }
        }
    }
}
```

### IndexedLogSegment.java

```java
package com.minikafka.storage;

import com.minikafka.common.MessageRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class IndexedLogSegment {
    private final File logFile;
    private final OffsetIndex index;
    private long nextOffset;

    public IndexedLogSegment(String logPath, String indexPath) throws IOException {
        this.logFile = new File(logPath);
        File parent = logFile.getParentFile();
        if (parent != null) parent.mkdirs();
        if (!logFile.exists()) logFile.createNewFile();
        this.index = new OffsetIndex(new File(indexPath));
        this.nextOffset = recoverNextOffset();
    }

    public synchronized long append(String key, String value) throws IOException {
        long offset = nextOffset++;
        long position = logFile.length();
        String line = offset + "|" + System.currentTimeMillis() + "|" + key + "|" + value + System.lineSeparator();
        try (FileOutputStream out = new FileOutputStream(logFile, true)) {
            out.write(line.getBytes(StandardCharsets.UTF_8));
        }
        index.append(offset, position);
        return offset;
    }

    public synchronized List<MessageRecord> readFrom(long startOffset) throws IOException {
        List<MessageRecord> result = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(logFile, "r")) {
            raf.seek(index.positionFor(startOffset));
            String line;
            while ((line = raf.readLine()) != null) {
                String[] parts = line.split("\\|", 4);
                if (parts.length == 4) {
                    long offset = Long.parseLong(parts[0]);
                    if (offset >= startOffset) {
                        result.add(new MessageRecord(offset, Long.parseLong(parts[1]), parts[2], parts[3]));
                    }
                }
            }
        }
        return result;
    }

    private long recoverNextOffset() throws IOException {
        long maxOffset = -1;
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length > 0 && !parts[0].isBlank()) {
                    maxOffset = Math.max(maxOffset, Long.parseLong(parts[0]));
                }
            }
        }
        return maxOffset + 1;
    }
}
```

### Phase3OffsetIndexDriver.java

```java
package com.minikafka;

import com.minikafka.storage.IndexedLogSegment;

public class Phase3OffsetIndexDriver {
    public static void main(String[] args) throws Exception {
        IndexedLogSegment segment = new IndexedLogSegment(
                "data/phase3/orders-0.log",
                "data/phase3/orders-0.index"
        );

        for (int i = 0; i < 10; i++) {
            segment.append("order-" + i, "event-" + i);
        }

        System.out.println("Read from offset 5");
        segment.readFrom(5).forEach(System.out::println);
    }
}
```

---

## Phase 4 Code Pack — Topic and Partition Routing

### Files added

```text
com/minikafka/common/TopicPartition.java
com/minikafka/partition/PartitionRouter.java
com/minikafka/Phase4PartitionRouterDriver.java
```

### TopicPartition.java

```java
package com.minikafka.common;

public record TopicPartition(String topic, int partition) {
    public String id() {
        return topic + "-" + partition;
    }
}
```

### PartitionRouter.java

```java
package com.minikafka.partition;

public class PartitionRouter {
    public int route(String key, int partitionCount) {
        if (partitionCount <= 0) throw new IllegalArgumentException("partitionCount must be positive");
        if (key == null) return 0;
        return Math.floorMod(key.hashCode(), partitionCount);
    }
}
```

### Phase4PartitionRouterDriver.java

```java
package com.minikafka;

import com.minikafka.partition.PartitionRouter;

public class Phase4PartitionRouterDriver {
    public static void main(String[] args) {
        PartitionRouter router = new PartitionRouter();
        for (int i = 1; i <= 10; i++) {
            String key = "user-" + i;
            System.out.println(key + " -> partition-" + router.route(key, 3));
        }
        System.out.println("same-key=" + router.route("user-1", 3));
        System.out.println("same-key=" + router.route("user-1", 3));
    }
}
```

---

## Phase 5 Code Pack — Broker + Producer API

### Delta from Phase 4

```text
Phase 4: only routing decision
Phase 5: producer sends to broker, broker routes and appends
```

### Files added

```text
com/minikafka/broker/MiniKafkaBroker.java
com/minikafka/producer/MiniProducer.java
com/minikafka/Phase5ProducerDriver.java
```

Keep `MessageRecord`, `LogSegment`, and `PartitionRouter`.

### MiniKafkaBroker.java

```java
package com.minikafka.broker;

import com.minikafka.partition.PartitionRouter;
import com.minikafka.storage.LogSegment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MiniKafkaBroker {
    private final Map<String, List<LogSegment>> topics = new ConcurrentHashMap<>();
    private final PartitionRouter router = new PartitionRouter();

    public void createTopic(String topic, int partitions) throws IOException {
        List<LogSegment> logs = new ArrayList<>();
        for (int p = 0; p < partitions; p++) {
            logs.add(new LogSegment("data/phase5/" + topic + "-" + p + ".log"));
        }
        topics.put(topic, logs);
    }

    public long produce(String topic, String key, String value) throws Exception {
        List<LogSegment> partitions = topics.get(topic);
        if (partitions == null) throw new IllegalArgumentException("Unknown topic: " + topic);
        int partition = router.route(key, partitions.size());
        return partitions.get(partition).append(key, value);
    }
}
```

### MiniProducer.java

```java
package com.minikafka.producer;

import com.minikafka.broker.MiniKafkaBroker;

public class MiniProducer {
    private final MiniKafkaBroker broker;

    public MiniProducer(MiniKafkaBroker broker) {
        this.broker = broker;
    }

    public long send(String topic, String key, String value) throws Exception {
        return broker.produce(topic, key, value);
    }
}
```

### Phase5ProducerDriver.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.producer.MiniProducer;

public class Phase5ProducerDriver {
    public static void main(String[] args) throws Exception {
        MiniKafkaBroker broker = new MiniKafkaBroker();
        broker.createTopic("orders", 3);

        MiniProducer producer = new MiniProducer(broker);
        long offset = producer.send("orders", "order-1", "created");

        System.out.println("produced offset=" + offset);
    }
}
```

---

## Phase 6 Code Pack — Consumer API

### Delta from Phase 5

```text
Phase 5: write records
Phase 6: read records by topic, partition, and offset
```

### Files added/changed

```text
com/minikafka/consumer/ConsumerRecord.java
com/minikafka/consumer/MiniConsumer.java
MiniKafkaBroker.java gets consume(...)
com/minikafka/Phase6ProducerConsumerDriver.java
```

### ConsumerRecord.java

```java
package com.minikafka.consumer;

public record ConsumerRecord(
        String topic,
        int partition,
        long offset,
        String key,
        String value
) {
}
```

### MiniConsumer.java

```java
package com.minikafka.consumer;

import com.minikafka.broker.MiniKafkaBroker;

import java.util.List;

public class MiniConsumer {
    private final MiniKafkaBroker broker;

    public MiniConsumer(MiniKafkaBroker broker) {
        this.broker = broker;
    }

    public List<ConsumerRecord> poll(String topic, int partition, long offset) throws Exception {
        return broker.consume(topic, partition, offset);
    }
}
```

### Add this method to MiniKafkaBroker.java

```java
public List<ConsumerRecord> consume(String topic, int partition, long offset) throws Exception {
    List<LogSegment> partitions = topics.get(topic);
    if (partitions == null) throw new IllegalArgumentException("Unknown topic: " + topic);
    if (partition < 0 || partition >= partitions.size()) throw new IllegalArgumentException("Invalid partition");

    List<ConsumerRecord> result = new ArrayList<>();
    for (var record : partitions.get(partition).readFrom(offset)) {
        result.add(new ConsumerRecord(topic, partition, record.offset(), record.key(), record.value()));
    }
    return result;
}
```

Also add imports:

```java
import com.minikafka.consumer.ConsumerRecord;
```

### Phase6ProducerConsumerDriver.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.consumer.MiniConsumer;
import com.minikafka.producer.MiniProducer;

public class Phase6ProducerConsumerDriver {
    public static void main(String[] args) throws Exception {
        MiniKafkaBroker broker = new MiniKafkaBroker();
        broker.createTopic("orders", 3);

        MiniProducer producer = new MiniProducer(broker);
        MiniConsumer consumer = new MiniConsumer(broker);

        producer.send("orders", "order-1", "created");
        producer.send("orders", "order-2", "paid");
        producer.send("orders", "order-3", "shipped");

        for (int partition = 0; partition < 3; partition++) {
            consumer.poll("orders", partition, 0).forEach(System.out::println);
        }
    }
}
```

---

## Phase 7 Code Pack — Offset Manager

### Files added

```text
com/minikafka/coordinator/OffsetManager.java
com/minikafka/Phase7OffsetManagerDriver.java
```

### OffsetManager.java

```java
package com.minikafka.coordinator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OffsetManager {
    private final Map<String, Long> committedOffsets = new ConcurrentHashMap<>();

    public void commit(String groupId, String topic, int partition, long nextOffset) {
        committedOffsets.put(key(groupId, topic, partition), nextOffset);
    }

    public long committedOffset(String groupId, String topic, int partition) {
        return committedOffsets.getOrDefault(key(groupId, topic, partition), 0L);
    }

    private String key(String groupId, String topic, int partition) {
        return groupId + ":" + topic + ":" + partition;
    }
}
```

### Phase7OffsetManagerDriver.java

```java
package com.minikafka;

import com.minikafka.coordinator.OffsetManager;

public class Phase7OffsetManagerDriver {
    public static void main(String[] args) {
        OffsetManager offsets = new OffsetManager();
        offsets.commit("order-service", "orders", 0, 5);
        System.out.println("resumeFrom=" + offsets.committedOffset("order-service", "orders", 0));
    }
}
```

---

## Phase 8 Code Pack — Batching Producer

### Delta from Phase 5

```text
Normal producer: send one record immediately
Batching producer: collect records, flush many together
```

### Files added

```text
com/minikafka/producer/ProducerRecord.java
com/minikafka/producer/BatchingMiniProducer.java
com/minikafka/Phase8BatchingDriver.java
```

### ProducerRecord.java

```java
package com.minikafka.producer;

public record ProducerRecord(String topic, String key, String value) {
}
```

### BatchingMiniProducer.java

```java
package com.minikafka.producer;

import com.minikafka.broker.MiniKafkaBroker;

import java.util.ArrayList;
import java.util.List;

public class BatchingMiniProducer {
    private final MiniKafkaBroker broker;
    private final int batchSize;
    private final List<ProducerRecord> buffer = new ArrayList<>();

    public BatchingMiniProducer(MiniKafkaBroker broker, int batchSize) {
        this.broker = broker;
        this.batchSize = batchSize;
    }

    public synchronized void send(String topic, String key, String value) throws Exception {
        buffer.add(new ProducerRecord(topic, key, value));
        if (buffer.size() >= batchSize) {
            flush();
        }
    }

    public synchronized void flush() throws Exception {
        for (ProducerRecord record : buffer) {
            broker.produce(record.topic(), record.key(), record.value());
        }
        System.out.println("flushed records=" + buffer.size());
        buffer.clear();
    }
}
```

### Phase8BatchingDriver.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.producer.BatchingMiniProducer;

public class Phase8BatchingDriver {
    public static void main(String[] args) throws Exception {
        MiniKafkaBroker broker = new MiniKafkaBroker();
        broker.createTopic("orders", 3);

        BatchingMiniProducer producer = new BatchingMiniProducer(broker, 5);
        for (int i = 1; i <= 12; i++) {
            producer.send("orders", "order-" + i, "event-" + i);
        }
        producer.flush();
    }
}
```

---

## Phase 9 Code Pack — Retention

### Files added

```text
com/minikafka/storage/RetentionManager.java
com/minikafka/Phase9RetentionDriver.java
```

### RetentionManager.java

```java
package com.minikafka.storage;

import java.io.File;

public class RetentionManager {
    private final long retentionMillis;

    public RetentionManager(long retentionMillis) {
        this.retentionMillis = retentionMillis;
    }

    public int cleanup(File partitionDir) {
        File[] files = partitionDir.listFiles((dir, name) -> name.endsWith(".log") || name.endsWith(".index"));
        if (files == null) return 0;

        int deleted = 0;
        long now = System.currentTimeMillis();
        for (File file : files) {
            if (now - file.lastModified() > retentionMillis && file.delete()) {
                deleted++;
            }
        }
        return deleted;
    }
}
```

### Phase9RetentionDriver.java

```java
package com.minikafka;

import com.minikafka.storage.RetentionManager;

import java.io.File;

public class Phase9RetentionDriver {
    public static void main(String[] args) {
        RetentionManager retention = new RetentionManager(1);
        int deleted = retention.cleanup(new File("data/phase2/orders-0"));
        System.out.println("deletedFiles=" + deleted);
    }
}
```

---

## Phase 10 Code Pack — Backpressure

### Files added

```text
com/minikafka/producer/BackpressureProducer.java
com/minikafka/Phase10BackpressureDriver.java
```

### BackpressureProducer.java

```java
package com.minikafka.producer;

import com.minikafka.broker.MiniKafkaBroker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BackpressureProducer {
    private final MiniKafkaBroker broker;
    private final BlockingQueue<ProducerRecord> queue;

    public BackpressureProducer(MiniKafkaBroker broker, int capacity) {
        this.broker = broker;
        this.queue = new ArrayBlockingQueue<>(capacity);
    }

    public boolean trySend(String topic, String key, String value) {
        return queue.offer(new ProducerRecord(topic, key, value));
    }

    public void drainToBroker() throws Exception {
        ProducerRecord record;
        while ((record = queue.poll()) != null) {
            broker.produce(record.topic(), record.key(), record.value());
        }
    }

    public int queueSize() {
        return queue.size();
    }
}
```

### Phase10BackpressureDriver.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.producer.BackpressureProducer;

public class Phase10BackpressureDriver {
    public static void main(String[] args) throws Exception {
        MiniKafkaBroker broker = new MiniKafkaBroker();
        broker.createTopic("orders", 3);

        BackpressureProducer producer = new BackpressureProducer(broker, 3);
        for (int i = 1; i <= 5; i++) {
            boolean accepted = producer.trySend("orders", "order-" + i, "event-" + i);
            System.out.println("order-" + i + " accepted=" + accepted);
        }

        System.out.println("queueSizeBeforeDrain=" + producer.queueSize());
        producer.drainToBroker();
        System.out.println("queueSizeAfterDrain=" + producer.queueSize());
    }
}
```

---

## Phase 11 Code Pack — Consumer Groups

### Files added

```text
com/minikafka/coordinator/ConsumerGroupCoordinator.java
com/minikafka/Phase11ConsumerGroupDriver.java
```

### ConsumerGroupCoordinator.java

```java
package com.minikafka.coordinator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConsumerGroupCoordinator {
    public Map<String, List<Integer>> assignPartitions(List<String> consumerIds, int partitionCount) {
        if (consumerIds == null || consumerIds.isEmpty()) {
            throw new IllegalArgumentException("At least one consumer is required");
        }

        Map<String, List<Integer>> assignment = new LinkedHashMap<>();
        for (String consumerId : consumerIds) {
            assignment.put(consumerId, new ArrayList<>());
        }

        for (int partition = 0; partition < partitionCount; partition++) {
            String consumerId = consumerIds.get(partition % consumerIds.size());
            assignment.get(consumerId).add(partition);
        }
        return assignment;
    }
}
```

### Phase11ConsumerGroupDriver.java

```java
package com.minikafka;

import com.minikafka.coordinator.ConsumerGroupCoordinator;

import java.util.List;

public class Phase11ConsumerGroupDriver {
    public static void main(String[] args) {
        ConsumerGroupCoordinator coordinator = new ConsumerGroupCoordinator();
        var assignment = coordinator.assignPartitions(List.of("consumer-1", "consumer-2"), 6);
        System.out.println(assignment);
    }
}
```

---

## Phase 12 Code Pack — Rebalancing

### Delta from Phase 11

```text
Phase 11: assign once
Phase 12: assign again when membership changes
```

### Phase12RebalanceDriver.java

```java
package com.minikafka;

import com.minikafka.coordinator.ConsumerGroupCoordinator;

import java.util.List;

public class Phase12RebalanceDriver {
    public static void main(String[] args) {
        ConsumerGroupCoordinator coordinator = new ConsumerGroupCoordinator();

        var before = coordinator.assignPartitions(List.of("c1", "c2"), 6);
        var afterJoin = coordinator.assignPartitions(List.of("c1", "c2", "c3"), 6);
        var afterLeave = coordinator.assignPartitions(List.of("c1", "c3"), 6);

        System.out.println("before=" + before);
        System.out.println("afterJoin=" + afterJoin);
        System.out.println("afterLeave=" + afterLeave);
    }
}
```

---

## Phase 13 Code Pack — Broker Facade

### What changes?

Now the broker becomes the main object hiding storage, routing, produce, and consume APIs.

### Driver

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.consumer.MiniConsumer;
import com.minikafka.producer.MiniProducer;

public class Phase13BrokerFacadeDriver {
    public static void main(String[] args) throws Exception {
        MiniKafkaBroker broker = new MiniKafkaBroker();
        broker.createTopic("payments", 2);

        MiniProducer producer = new MiniProducer(broker);
        MiniConsumer consumer = new MiniConsumer(broker);

        producer.send("payments", "payment-1", "created");
        producer.send("payments", "payment-2", "confirmed");

        for (int partition = 0; partition < 2; partition++) {
            consumer.poll("payments", partition, 0).forEach(System.out::println);
        }
    }
}
```

---

## Phase 14 Code Pack — Replication Simulation

### Files added

```text
com/minikafka/replication/BrokerNode.java
com/minikafka/replication/ReplicaState.java
com/minikafka/replication/ReplicationManager.java
com/minikafka/Phase14ReplicationDriver.java
```

### BrokerNode.java

```java
package com.minikafka.replication;

public record BrokerNode(String brokerId, String host, int port) {
}
```

### ReplicaState.java

```java
package com.minikafka.replication;

public enum ReplicaState {
    LEADER,
    FOLLOWER
}
```

### ReplicationManager.java

```java
package com.minikafka.replication;

import java.util.LinkedHashSet;
import java.util.Set;

public class ReplicationManager {
    private final Set<String> replicas = new LinkedHashSet<>();
    private final Set<String> inSyncReplicas = new LinkedHashSet<>();

    public void addReplica(String brokerId) {
        replicas.add(brokerId);
        inSyncReplicas.add(brokerId);
    }

    public void markOutOfSync(String brokerId) {
        inSyncReplicas.remove(brokerId);
    }

    public void markInSync(String brokerId) {
        if (replicas.contains(brokerId)) inSyncReplicas.add(brokerId);
    }

    public Set<String> isr() {
        return Set.copyOf(inSyncReplicas);
    }
}
```

### Phase14ReplicationDriver.java

```java
package com.minikafka;

import com.minikafka.replication.ReplicationManager;

public class Phase14ReplicationDriver {
    public static void main(String[] args) {
        ReplicationManager replication = new ReplicationManager();
        replication.addReplica("broker-1");
        replication.addReplica("broker-2");
        replication.addReplica("broker-3");

        System.out.println("initialISR=" + replication.isr());
        replication.markOutOfSync("broker-3");
        System.out.println("afterLag=" + replication.isr());
    }
}
```

---

## Phase 15 Code Pack — Leader/Follower

### Files added

```text
com/minikafka/replication/LeaderElection.java
com/minikafka/Phase15LeaderFollowerDriver.java
```

### LeaderElection.java

```java
package com.minikafka.replication;

import java.util.Set;

public class LeaderElection {
    public String electLeader(Set<String> inSyncReplicas) {
        return inSyncReplicas.stream()
                .sorted()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No ISR available for leader election"));
    }
}
```

### Phase15LeaderFollowerDriver.java

```java
package com.minikafka;

import com.minikafka.replication.LeaderElection;

import java.util.Set;

public class Phase15LeaderFollowerDriver {
    public static void main(String[] args) {
        LeaderElection election = new LeaderElection();
        String leader = election.electLeader(Set.of("broker-2", "broker-3"));
        System.out.println("newLeader=" + leader);
    }
}
```

---

## Phase 16 Code Pack — ISR and Ack Modes

### Files added

```text
com/minikafka/producer/AckMode.java
com/minikafka/replication/AckDecider.java
com/minikafka/Phase16AckModeDriver.java
```

### AckMode.java

```java
package com.minikafka.producer;

public enum AckMode {
    NONE,
    LEADER,
    ALL
}
```

### AckDecider.java

```java
package com.minikafka.replication;

import com.minikafka.producer.AckMode;

public class AckDecider {
    public boolean canAck(AckMode mode, boolean leaderWritten, int requiredIsr, int successfulReplicaWrites) {
        return switch (mode) {
            case NONE -> true;
            case LEADER -> leaderWritten;
            case ALL -> leaderWritten && successfulReplicaWrites >= requiredIsr;
        };
    }
}
```

### Phase16AckModeDriver.java

```java
package com.minikafka;

import com.minikafka.producer.AckMode;
import com.minikafka.replication.AckDecider;

public class Phase16AckModeDriver {
    public static void main(String[] args) {
        AckDecider decider = new AckDecider();
        System.out.println("acks0=" + decider.canAck(AckMode.NONE, false, 2, 0));
        System.out.println("acks1=" + decider.canAck(AckMode.LEADER, true, 2, 1));
        System.out.println("acksAll=" + decider.canAck(AckMode.ALL, true, 2, 2));
    }
}
```

---

## Phase 17 Code Pack — Zero-Copy Demo

### Files added

```text
com/minikafka/storage/ZeroCopyFileTransfer.java
com/minikafka/Phase17ZeroCopyDriver.java
```

### ZeroCopyFileTransfer.java

```java
package com.minikafka.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class ZeroCopyFileTransfer {
    public long copy(String source, String target) throws Exception {
        try (FileChannel from = new FileInputStream(source).getChannel();
             FileChannel to = new FileOutputStream(target).getChannel()) {
            return from.transferTo(0, from.size(), to);
        }
    }
}
```

### Phase17ZeroCopyDriver.java

```java
package com.minikafka;

import com.minikafka.storage.ZeroCopyFileTransfer;

import java.nio.file.Files;
import java.nio.file.Path;

public class Phase17ZeroCopyDriver {
    public static void main(String[] args) throws Exception {
        Files.createDirectories(Path.of("data/phase17"));
        Files.writeString(Path.of("data/phase17/source.log"), "hello-zero-copy\n");

        ZeroCopyFileTransfer transfer = new ZeroCopyFileTransfer();
        long bytes = transfer.copy("data/phase17/source.log", "data/phase17/target.log");

        System.out.println("copiedBytes=" + bytes);
    }
}
```

---

## Phase 18 Code Pack — Metrics

### Files added

```text
com/minikafka/metrics/MetricsRegistry.java
com/minikafka/Phase18MetricsDriver.java
```

### MetricsRegistry.java

```java
package com.minikafka.metrics;

import java.util.concurrent.atomic.AtomicLong;

public class MetricsRegistry {
    private final AtomicLong messagesIn = new AtomicLong();
    private final AtomicLong messagesOut = new AtomicLong();
    private final AtomicLong bytesIn = new AtomicLong();
    private final AtomicLong bytesOut = new AtomicLong();
    private final AtomicLong rejectedMessages = new AtomicLong();

    public void recordIn(long bytes) {
        messagesIn.incrementAndGet();
        bytesIn.addAndGet(bytes);
    }

    public void recordOut(long bytes) {
        messagesOut.incrementAndGet();
        bytesOut.addAndGet(bytes);
    }

    public void recordRejected() {
        rejectedMessages.incrementAndGet();
    }

    public String snapshot() {
        return "messagesIn=" + messagesIn.get()
                + ", messagesOut=" + messagesOut.get()
                + ", bytesIn=" + bytesIn.get()
                + ", bytesOut=" + bytesOut.get()
                + ", rejected=" + rejectedMessages.get();
    }
}
```

### Phase18MetricsDriver.java

```java
package com.minikafka;

import com.minikafka.metrics.MetricsRegistry;

public class Phase18MetricsDriver {
    public static void main(String[] args) {
        MetricsRegistry metrics = new MetricsRegistry();
        metrics.recordIn(100);
        metrics.recordIn(150);
        metrics.recordOut(80);
        metrics.recordRejected();
        System.out.println(metrics.snapshot());
    }
}
```

---

## Phase 19 Code Pack — Simple Load Test Driver

### Files added

```text
com/minikafka/Phase19LoadTestDriver.java
```

### Phase19LoadTestDriver.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.producer.MiniProducer;

public class Phase19LoadTestDriver {
    public static void main(String[] args) throws Exception {
        MiniKafkaBroker broker = new MiniKafkaBroker();
        broker.createTopic("load-test", 3);
        MiniProducer producer = new MiniProducer(broker);

        int messages = 100_000;
        long start = System.nanoTime();
        for (int i = 0; i < messages; i++) {
            producer.send("load-test", "key-" + i, "value-" + i);
        }
        long elapsedNanos = System.nanoTime() - start;
        double seconds = elapsedNanos / 1_000_000_000.0;

        System.out.println("messages=" + messages);
        System.out.println("seconds=" + seconds);
        System.out.println("throughput=" + (long) (messages / seconds) + " msg/sec");
    }
}
```

---

## Phase 20 Code Pack — Graceful Shutdown Hook

### Files added

```text
com/minikafka/broker/BrokerLifecycle.java
com/minikafka/Phase20GracefulShutdownDriver.java
```

### BrokerLifecycle.java

```java
package com.minikafka.broker;

public class BrokerLifecycle {
    private volatile boolean running;

    public void start() {
        running = true;
        System.out.println("broker started");
    }

    public void stop() {
        running = false;
        System.out.println("broker stopped gracefully");
    }

    public boolean isRunning() {
        return running;
    }
}
```

### Phase20GracefulShutdownDriver.java

```java
package com.minikafka;

import com.minikafka.broker.BrokerLifecycle;

public class Phase20GracefulShutdownDriver {
    public static void main(String[] args) {
        BrokerLifecycle lifecycle = new BrokerLifecycle();
        lifecycle.start();

        Runtime.getRuntime().addShutdownHook(new Thread(lifecycle::stop));

        System.out.println("running=" + lifecycle.isRunning());
        lifecycle.stop();
    }
}
```

---

## How To Use These Phase Code Packs

```text
1. Start with Phase 1 only.
2. Create only the files listed in that phase.
3. Run the driver.
4. Understand the output.
5. Move to the next phase.
6. Add only the delta files/classes.
7. Keep older phase code in Git commits so your repository shows the evolution.
```

Recommended Git history:

```bash
git commit -m "phase-1 append only log"
git commit -m "phase-2 segment rolling"
git commit -m "phase-3 offset index"
git commit -m "phase-4 topic partition routing"
git commit -m "phase-5 producer api"
git commit -m "phase-6 consumer api"
```

This is better for FAANG/system-design learning because your repo shows engineering evolution, not just final code.

---

# Part 28 — Complete Java Code Pack

This handbook already includes the core classes.

Minimum working set:

```text
MessageRecord
TopicPartition
LogSegment
OffsetIndex
PartitionRouter
MiniKafkaBroker
MiniProducer
MiniConsumer
OffsetManager
ConsumerGroupCoordinator
ReplicationManager
MetricsRegistry
```

This is enough to create a serious MiniKafka learning project.

---

# Part 29 — Driver Classes

## MiniKafkaDemo.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.consumer.ConsumerRecord;
import com.minikafka.consumer.MiniConsumer;
import com.minikafka.producer.MiniProducer;

import java.util.List;

public class MiniKafkaDemo {
    public static void main(String[] args) throws Exception {
        MiniKafkaBroker broker = new MiniKafkaBroker();
        broker.createTopic("orders", 3);

        MiniProducer producer = new MiniProducer(broker);
        MiniConsumer consumer = new MiniConsumer(broker);

        producer.send("orders", "order-1", "created");
        producer.send("orders", "order-2", "paid");
        producer.send("orders", "order-3", "shipped");

        for (int partition = 0; partition < 3; partition++) {
            List<ConsumerRecord> records =
                    consumer.poll("orders", partition, 0);

            for (ConsumerRecord record : records) {
                System.out.println(record);
            }
        }
    }
}
```

## ConsumerGroupDemo.java

```java
package com.minikafka;

import com.minikafka.coordinator.ConsumerGroupCoordinator;

import java.util.List;
import java.util.Map;

public class ConsumerGroupDemo {
    public static void main(String[] args) {
        ConsumerGroupCoordinator coordinator =
                new ConsumerGroupCoordinator();

        Map<String, List<Integer>> assignment =
                coordinator.assignPartitions(
                        List.of("consumer-1", "consumer-2"),
                        6
                );

        System.out.println(assignment);
    }
}
```

---

# Part 30 — JUnit Tests

## LogSegmentTest.java

```java
package com.minikafka;

import com.minikafka.common.MessageRecord;
import com.minikafka.storage.LogSegment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LogSegmentTest {

    @Test
    void shouldAppendAndReadRecords() throws Exception {
        LogSegment segment =
                new LogSegment("data/test-log-segment.log");

        long offset = segment.append("k1", "v1");

        List<MessageRecord> records = segment.readAll();

        assertTrue(offset >= 0);
        assertFalse(records.isEmpty());
        assertEquals("k1", records.get(records.size() - 1).key());
    }
}
```

## PartitionRouterTest.java

```java
package com.minikafka;

import com.minikafka.partition.PartitionRouter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PartitionRouterTest {

    @Test
    void shouldRouteKeyToValidPartition() {
        PartitionRouter router = new PartitionRouter();

        int partition = router.route("user-1", 3);

        assertTrue(partition >= 0);
        assertTrue(partition < 3);
    }
}
```

## OffsetManagerTest.java

```java
package com.minikafka;

import com.minikafka.coordinator.OffsetManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OffsetManagerTest {

    @Test
    void shouldCommitAndReadOffset() {
        OffsetManager manager = new OffsetManager();

        manager.commit("group-a", "orders", 0, 100);

        assertEquals(100, manager.committedOffset("group-a", "orders", 0));
    }
}
```

## ConsumerGroupCoordinatorTest.java

```java
package com.minikafka;

import com.minikafka.coordinator.ConsumerGroupCoordinator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsumerGroupCoordinatorTest {

    @Test
    void shouldAssignPartitionsToConsumers() {
        ConsumerGroupCoordinator coordinator =
                new ConsumerGroupCoordinator();

        Map<String, List<Integer>> assignment =
                coordinator.assignPartitions(
                        List.of("c1", "c2"),
                        4
                );

        assertEquals(List.of(0, 2), assignment.get("c1"));
        assertEquals(List.of(1, 3), assignment.get("c2"));
    }
}
```

---

# Part 31 — Performance Tuning Playbook

## If producer latency is high

Check:

```text
batch size too small
disk flush too frequent
queue full
GC pause
hot partition
```

## If consumer lag is growing

Check:

```text
consumer too slow
partition count too low
processing latency high
rebalance happening
GC pause
```

## If disk usage grows

Check:

```text
retention not running
segment cleanup broken
consumers need old data
```

## If GC pauses are high

Check:

```text
too many objects
too large heap
too many retained records
unbounded queues
string-heavy record format
```

## If one partition is hot

Check:

```text
bad key distribution
celebrity key
small partition count
```

---

# Part 32 — Failure Handling

Failures to simulate:

```text
broker crash
producer retry
consumer crash before commit
consumer crash after commit
follower lag
leader crash
disk full
corrupt log segment
```

Expected behaviors:

```text
consumer resumes from committed offset
producer retries idempotently
replica catches up
leader re-elected from ISR
corrupt segment detected
```

---

# Part 33 — Scaling Discussion

## Scale writes

```text
increase partitions
batch producer writes
compress messages
use faster disks
```

## Scale reads

```text
consumer groups
more consumers
more partitions
```

## Scale storage

```text
retention
tiered storage
compression
segment cleanup
```

## Scale reliability

```text
replication factor
acks=all
min.insync.replicas
rack awareness
```

---

# Part 34 — Interview Explanation

## What is Kafka?

```text
Kafka is a distributed append-only log system.
Producers write records to topic partitions.
Consumers read records using offsets.
Partitions provide parallelism.
Replication provides fault tolerance.
Consumer groups provide scalable consumption.
```

## Why is Kafka fast?

```text
Sequential disk writes
Batching
OS page cache
Zero-copy
Append-only log
Efficient partitioning
```

## Why offsets?

```text
Offsets allow consumers to track progress, replay messages, and recover after crash.
```

## Why partitions?

```text
A single log cannot scale enough.
Partitions split the log for parallel reads and writes.
```

## Why consumer groups?

```text
They allow multiple consumers to share partitions and process messages in parallel.
```

## Why ISR?

```text
ISR ensures only sufficiently caught-up replicas are considered safe for durability and leader election.
```

---

# Part 35 — What This Teaches For Distributed Systems

MiniKafka teaches:

```text
storage engines
append-only logs
WAL design
partitioning
replication
leader/follower
backpressure
batching
consumer coordination
offset tracking
observability
failure recovery
```

Systems that become easier after MiniKafka:

```text
Kafka
Pulsar
Redpanda
RabbitMQ
Redis Streams
database WAL
event sourcing systems
log aggregation systems
stream processing platforms
```

Final mental model:

```text
Kafka = distributed append-only log

append-only log gives speed
partitioning gives scale
replication gives safety
offsets give replay
consumer groups give parallelism
batching gives throughput
page cache and zero-copy give performance
```

## Step-By-Step: What We Are Building

Now we handle membership change.

Step by step:

```text
Step 1: Start with consumers c1 and c2.
Step 2: Assign partitions.
Step 3: Add c3 or remove c2.
Step 4: Recalculate partition assignment.
Step 5: Verify every partition is still assigned once.
Step 6: Understand that rebalance pauses consumption.
```

## Driver Class To Test Phase 12

```java
package com.minikafka;

import com.minikafka.coordinator.ConsumerGroupCoordinator;

import java.util.List;

public class Phase12RebalanceDriver {
    public static void main(String[] args) {
        ConsumerGroupCoordinator coordinator = new ConsumerGroupCoordinator();

        var before = coordinator.assignPartitions(List.of("c1", "c2"), 6);
        var after = coordinator.assignPartitions(List.of("c1", "c2", "c3"), 6);

        System.out.println("before=" + before);
        System.out.println("after=" + after);
    }
}
```

## Run Phase 12

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase12RebalanceDriver
```

## Expected Output

```text
before={c1=[0, 2, 4], c2=[1, 3, 5]}
after={c1=[0, 3], c2=[1, 4], c3=[2, 5]}
```

## What To Verify

```text
1. Before and after both cover all partitions.
2. Rebalance changes ownership.
3. No duplicate partition assignment.
```

## Phase 12 JUnit Test Idea

```java
@Test
void phase12RebalanceShouldNotLosePartitions() {
    ConsumerGroupCoordinator coordinator = new ConsumerGroupCoordinator();

    var assignment = coordinator.assignPartitions(List.of("c1", "c2", "c3"), 6);

    Set<Integer> all = assignment.values()
            .stream()
            .flatMap(List::stream)
            .collect(Collectors.toSet());

    assertEquals(Set.of(0,1,2,3,4,5), all);
}
```
