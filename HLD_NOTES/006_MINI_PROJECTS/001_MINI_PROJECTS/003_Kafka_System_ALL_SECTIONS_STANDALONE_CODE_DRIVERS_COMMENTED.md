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


---

## Phase 1 Standalone Code — Append-Only Log

### What this phase builds

```text
One append-only log file.
Every message gets a monotonically increasing offset.
```

### Files for this phase

```text
src/main/java/com/minikafka/common/MessageRecord.java
src/main/java/com/minikafka/storage/LogSegment.java
src/main/java/com/minikafka/Phase1AppendOnlyLogDriver.java
```

### MessageRecord.java

```java
package com.minikafka.common;

/**
 * One record stored inside the MiniKafka log.
 *
 * offset          = logical position of the message inside a partition
 * timestampMillis = when the record was written
 * key             = routing/grouping key
 * value           = actual payload
 */
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

/**
 * Phase 1 storage unit.
 *
 * This class represents one append-only file.
 * It does not update old records.
 * It only appends new records at the end of the file.
 */
public class LogSegment {
    private final File file;

    // Next offset to assign when a new message is appended.
    private long nextOffset;

    public LogSegment(String filePath) throws IOException {
        this.file = new File(filePath);

        // Create parent directory if it does not exist.
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        // Create the actual log file if missing.
        if (!file.exists()) {
            file.createNewFile();
        }

        // After restart, recover next offset from existing file content.
        this.nextOffset = recoverNextOffset();
    }

    /**
     * Appends one record to the end of the log file.
     */
    public synchronized long append(String key, String value) throws IOException {
        long assignedOffset = nextOffset++;

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {

            // Simple text format:
            // offset|timestamp|key|value
            writer.write(assignedOffset + "|" + System.currentTimeMillis() + "|" + key + "|" + value);
            writer.newLine();
        }

        return assignedOffset;
    }

    /**
     * Reads all records from the file.
     * This is simple but inefficient for large logs.
     */
    public synchronized List<MessageRecord> readAll() throws IOException {
        List<MessageRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                MessageRecord record = parseLine(line);
                if (record != null) {
                    records.add(record);
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

    private MessageRecord parseLine(String line) {
        String[] parts = line.split("\\|", 4);
        if (parts.length != 4) {
            return null;
        }

        return new MessageRecord(
                Long.parseLong(parts[0]),
                Long.parseLong(parts[1]),
                parts[2],
                parts[3]
        );
    }

    /**
     * Scans the file and finds max offset.
     * nextOffset = maxOffset + 1.
     */
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

### Phase1AppendOnlyLogDriver.java

```java
package com.minikafka;

import com.minikafka.common.MessageRecord;
import com.minikafka.storage.LogSegment;

/**
 * Driver for Phase 1.
 *
 * Run this to verify:
 * 1. log file is created
 * 2. records are appended
 * 3. offsets increase
 */
public class Phase1AppendOnlyLogDriver {
    public static void main(String[] args) throws Exception {
        LogSegment segment = new LogSegment("data/phase1/orders-0.log");

        long offset1 = segment.append("order-1", "created");
        long offset2 = segment.append("order-2", "paid");

        System.out.println("offset1=" + offset1);
        System.out.println("offset2=" + offset2);

        for (MessageRecord record : segment.readAll()) {
            System.out.println(record);
        }
    }
}
```

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase1AppendOnlyLogDriver
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


---

## Phase 2 Standalone Code — Segment Files

### What this phase builds

```text
One partition directory with multiple segment files.
When active segment becomes too large, we roll to a new file.
```

### Files for this phase

```text
src/main/java/com/minikafka/common/MessageRecord.java
src/main/java/com/minikafka/storage/SegmentFile.java
src/main/java/com/minikafka/storage/LogSegment.java
src/main/java/com/minikafka/storage/PartitionLog.java
src/main/java/com/minikafka/Phase2SegmentRollingDriver.java
```

Use `MessageRecord.java` and `LogSegment.java` from Phase 1.

### SegmentFile.java

```java
package com.minikafka.storage;

import java.io.File;

/**
 * Metadata for one physical log segment file.
 *
 * baseOffset = first logical offset expected in this segment
 * logFile    = actual .log file on disk
 */
public record SegmentFile(
        long baseOffset,
        File logFile
) {
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

/**
 * A partition is a sequence of log segments.
 *
 * Phase 1:
 *   one file grows forever
 *
 * Phase 2:
 *   one partition directory contains many segment files
 */
public class PartitionLog {
    private final File partitionDir;
    private final long maxSegmentBytes;
    private final List<LogSegment> segments = new ArrayList<>();

    // Latest segment where new records are appended.
    private LogSegment activeSegment;

    public PartitionLog(String partitionDirPath, long maxSegmentBytes) throws IOException {
        this.partitionDir = new File(partitionDirPath);
        this.maxSegmentBytes = maxSegmentBytes;

        if (!partitionDir.exists()) {
            partitionDir.mkdirs();
        }

        loadExistingSegmentsOrCreateFirst();
    }

    public synchronized long append(String key, String value) throws IOException {
        // If active segment is too large, create a new segment.
        if (activeSegment.file().length() >= maxSegmentBytes) {
            rollSegment();
        }

        return activeSegment.append(key, value);
    }

    public synchronized List<MessageRecord> readAll() throws IOException {
        List<MessageRecord> result = new ArrayList<>();

        // Read all segments in order.
        for (LogSegment segment : segments) {
            result.addAll(segment.readAll());
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

    private void loadExistingSegmentsOrCreateFirst() throws IOException {
        File[] files = partitionDir.listFiles((dir, name) -> name.endsWith(".log"));

        if (files == null || files.length == 0) {
            activeSegment = new LogSegment(segmentPath(0));
            segments.add(activeSegment);
            return;
        }

        List<File> sortedFiles = new ArrayList<>(List.of(files));
        sortedFiles.sort(Comparator.comparing(File::getName));

        for (File file : sortedFiles) {
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
        return new File(partitionDir, String.format("%020d.log", baseOffset)).getPath();
    }
}
```

### Phase2SegmentRollingDriver.java

```java
package com.minikafka;

import com.minikafka.storage.PartitionLog;

/**
 * Driver for Phase 2.
 *
 * We use a tiny segment size so rolling is visible quickly.
 */
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

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase2SegmentRollingDriver
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


---

## Phase 3 Standalone Code — Offset Index

### What this phase builds

```text
An index file that maps offset -> byte position in log file.
This avoids scanning from the beginning for every read.
```

### Files for this phase

```text
src/main/java/com/minikafka/common/MessageRecord.java
src/main/java/com/minikafka/storage/OffsetIndex.java
src/main/java/com/minikafka/storage/IndexedLogSegment.java
src/main/java/com/minikafka/Phase3OffsetIndexDriver.java
```

Use `MessageRecord.java` from Phase 1.

### OffsetIndex.java

```java
package com.minikafka.storage;

import java.io.*;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Offset index.
 *
 * Stores:
 *   offset -> byte position in log file
 *
 * TreeMap lets us find floorEntry(offset).
 * Example:
 *   requested offset = 105
 *   indexed offsets  = 0, 50, 100, 150
 *   floorEntry       = 100
 */
public class OffsetIndex {
    private final File indexFile;
    private final NavigableMap<Long, Long> offsetToPosition = new TreeMap<>();

    public OffsetIndex(File indexFile) throws IOException {
        this.indexFile = indexFile;

        File parent = indexFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        if (!indexFile.exists()) {
            indexFile.createNewFile();
        }

        load();
    }

    public synchronized void append(long offset, long position) throws IOException {
        offsetToPosition.put(offset, position);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile, true))) {
            writer.write(offset + "," + position);
            writer.newLine();
        }
    }

    public synchronized long positionFor(long offset) {
        var entry = offsetToPosition.floorEntry(offset);
        return entry == null ? 0L : entry.getValue();
    }

    private void load() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(indexFile))) {
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

/**
 * Log segment with an index.
 *
 * append:
 *   1. find current file length
 *   2. write record at end
 *   3. append offset -> position to index
 *
 * readFrom:
 *   1. use index to seek near the offset
 *   2. scan from that position
 */
public class IndexedLogSegment {
    private final File logFile;
    private final OffsetIndex index;
    private long nextOffset;

    public IndexedLogSegment(String logPath, String indexPath) throws IOException {
        this.logFile = new File(logPath);

        File parent = logFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        if (!logFile.exists()) {
            logFile.createNewFile();
        }

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
                        result.add(new MessageRecord(
                                offset,
                                Long.parseLong(parts[1]),
                                parts[2],
                                parts[3]
                        ));
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

/**
 * Driver for Phase 3.
 *
 * Writes 10 records and reads from offset 5.
 */
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

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase3OffsetIndexDriver
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


---

## Phase 4 Standalone Code — Topic and Partition Routing

### What this phase builds

```text
A key-based partition router.
Same key always maps to same partition.
```

### Files for this phase

```text
src/main/java/com/minikafka/common/TopicPartition.java
src/main/java/com/minikafka/partition/PartitionRouter.java
src/main/java/com/minikafka/Phase4PartitionRouterDriver.java
```

### TopicPartition.java

```java
package com.minikafka.common;

/**
 * Identifies one physical partition inside a topic.
 *
 * Example:
 *   topic = orders
 *   partition = 2
 *   id = orders-2
 */
public record TopicPartition(String topic, int partition) {
    public String id() {
        return topic + "-" + partition;
    }
}
```

### PartitionRouter.java

```java
package com.minikafka.partition;

/**
 * Chooses which partition receives a record.
 *
 * Rule:
 *   null key -> partition 0
 *   non-null key -> hash(key) % partitionCount
 */
public class PartitionRouter {
    public int route(String key, int partitionCount) {
        if (partitionCount <= 0) {
            throw new IllegalArgumentException("partitionCount must be positive");
        }

        if (key == null) {
            return 0;
        }

        // floorMod avoids negative partition numbers when hashCode is negative.
        return Math.floorMod(key.hashCode(), partitionCount);
    }
}
```

### Phase4PartitionRouterDriver.java

```java
package com.minikafka;

import com.minikafka.partition.PartitionRouter;

/**
 * Driver for Phase 4.
 *
 * Shows:
 * 1. keys distribute across partitions
 * 2. same key maps to same partition
 */
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

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase4PartitionRouterDriver
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


---

## Phase 5 Standalone Code — Producer API

### What this phase builds

```text
Producer -> Broker -> Partition router -> LogSegment
```

### Files for this phase

```text
src/main/java/com/minikafka/common/MessageRecord.java
src/main/java/com/minikafka/storage/LogSegment.java
src/main/java/com/minikafka/partition/PartitionRouter.java
src/main/java/com/minikafka/broker/MiniKafkaBroker.java
src/main/java/com/minikafka/producer/MiniProducer.java
src/main/java/com/minikafka/Phase5ProducerDriver.java
```

Use `MessageRecord.java`, `LogSegment.java`, and `PartitionRouter.java` from previous phases.

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

/**
 * Small in-process broker.
 *
 * It owns topics.
 * Each topic has multiple LogSegment partitions.
 */
public class MiniKafkaBroker {
    private final Map<String, List<LogSegment>> topics = new ConcurrentHashMap<>();
    private final PartitionRouter router = new PartitionRouter();

    public void createTopic(String topic, int partitionCount) throws IOException {
        if (partitionCount <= 0) {
            throw new IllegalArgumentException("partitionCount must be positive");
        }

        List<LogSegment> partitions = new ArrayList<>();

        for (int p = 0; p < partitionCount; p++) {
            partitions.add(new LogSegment("data/phase5/" + topic + "-" + p + ".log"));
        }

        topics.put(topic, partitions);
    }

    public long produce(String topic, String key, String value) throws Exception {
        List<LogSegment> partitions = topics.get(topic);

        if (partitions == null) {
            throw new IllegalArgumentException("Unknown topic: " + topic);
        }

        int partition = router.route(key, partitions.size());
        return partitions.get(partition).append(key, value);
    }
}
```

### MiniProducer.java

```java
package com.minikafka.producer;

import com.minikafka.broker.MiniKafkaBroker;

/**
 * Producer facade.
 *
 * Application code should call producer.send(...)
 * instead of directly calling broker.produce(...).
 */
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

/**
 * Driver for Phase 5.
 *
 * Creates topic and writes one record through producer.
 */
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

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase5ProducerDriver
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


---

## Phase 6 Standalone Code — Consumer API

### What this phase builds

```text
Consumer reads records from topic partition by offset.
Messages are not removed after reading.
```

### Files for this phase

```text
src/main/java/com/minikafka/common/MessageRecord.java
src/main/java/com/minikafka/storage/LogSegment.java
src/main/java/com/minikafka/partition/PartitionRouter.java
src/main/java/com/minikafka/broker/MiniKafkaBroker.java
src/main/java/com/minikafka/producer/MiniProducer.java
src/main/java/com/minikafka/consumer/ConsumerRecord.java
src/main/java/com/minikafka/consumer/MiniConsumer.java
src/main/java/com/minikafka/Phase6ProducerConsumerDriver.java
```

Use `MessageRecord.java`, `LogSegment.java`, `PartitionRouter.java`, and `MiniProducer.java` from previous phases.

### ConsumerRecord.java

```java
package com.minikafka.consumer;

/**
 * Record returned to consumer code.
 *
 * It includes topic and partition because consumers need to know
 * where the record came from.
 */
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

/**
 * Consumer facade.
 *
 * poll(topic, partition, offset) returns records from that offset onward.
 */
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

### MiniKafkaBroker.java

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

/**
 * Broker with produce and consume APIs.
 */
public class MiniKafkaBroker {
    private final Map<String, List<LogSegment>> topics = new ConcurrentHashMap<>();
    private final PartitionRouter router = new PartitionRouter();

    public void createTopic(String topic, int partitionCount) throws IOException {
        List<LogSegment> partitions = new ArrayList<>();

        for (int p = 0; p < partitionCount; p++) {
            partitions.add(new LogSegment("data/phase6/" + topic + "-" + p + ".log"));
        }

        topics.put(topic, partitions);
    }

    public long produce(String topic, String key, String value) throws Exception {
        List<LogSegment> partitions = getPartitions(topic);
        int partition = router.route(key, partitions.size());
        return partitions.get(partition).append(key, value);
    }

    public List<ConsumerRecord> consume(String topic, int partition, long offset) throws Exception {
        List<LogSegment> partitions = getPartitions(topic);

        if (partition < 0 || partition >= partitions.size()) {
            throw new IllegalArgumentException("Invalid partition: " + partition);
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

    private List<LogSegment> getPartitions(String topic) {
        List<LogSegment> partitions = topics.get(topic);
        if (partitions == null) {
            throw new IllegalArgumentException("Unknown topic: " + topic);
        }
        return partitions;
    }
}
```

### Phase6ProducerConsumerDriver.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.consumer.MiniConsumer;
import com.minikafka.producer.MiniProducer;

/**
 * Driver for Phase 6.
 *
 * Produces records and then consumes from all partitions.
 */
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
            System.out.println("partition=" + partition);
            consumer.poll("orders", partition, 0).forEach(System.out::println);
        }
    }
}
```

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase6ProducerConsumerDriver
```

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


---

## Phase 7 Standalone Code — Offset Manager

### What this phase builds

```text
A committed offset store.
Consumer can resume after crash.
```

### Files for this phase

```text
src/main/java/com/minikafka/coordinator/OffsetManager.java
src/main/java/com/minikafka/Phase7OffsetManagerDriver.java
```

### OffsetManager.java

```java
package com.minikafka.coordinator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores committed offsets per:
 *   groupId + topic + partition
 *
 * In real Kafka, offsets are stored in an internal Kafka topic.
 * Here we keep them in memory for learning.
 */
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

/**
 * Driver for Phase 7.
 *
 * Simulates a consumer committing progress.
 */
public class Phase7OffsetManagerDriver {
    public static void main(String[] args) {
        OffsetManager offsetManager = new OffsetManager();

        offsetManager.commit("order-service", "orders", 0, 5);

        long resumeFrom = offsetManager.committedOffset("order-service", "orders", 0);

        System.out.println("resumeFrom=" + resumeFrom);
    }
}
```

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase7OffsetManagerDriver
```

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


---

## Phase 8 Standalone Code — Batching

### What this phase builds

```text
Producer buffers records and flushes them in batches.
```

### Files for this phase

```text
src/main/java/com/minikafka/producer/ProducerRecord.java
src/main/java/com/minikafka/producer/BatchingMiniProducer.java
src/main/java/com/minikafka/Phase8BatchingDriver.java
```

This phase uses the Phase 6 broker setup.

### ProducerRecord.java

```java
package com.minikafka.producer;

/**
 * Record waiting inside producer buffer before being flushed.
 */
public record ProducerRecord(
        String topic,
        String key,
        String value
) {
}
```

### BatchingMiniProducer.java

```java
package com.minikafka.producer;

import com.minikafka.broker.MiniKafkaBroker;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple batching producer.
 *
 * It does not send immediately.
 * It stores records in memory until batchSize is reached.
 */
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

        // Flush automatically when buffer becomes full.
        if (buffer.size() >= batchSize) {
            flush();
        }
    }

    public synchronized void flush() throws Exception {
        if (buffer.isEmpty()) {
            return;
        }

        for (ProducerRecord record : buffer) {
            broker.produce(record.topic(), record.key(), record.value());
        }

        System.out.println("flushed records=" + buffer.size());
        buffer.clear();
    }

    public synchronized int bufferedRecords() {
        return buffer.size();
    }
}
```

### Phase8BatchingDriver.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.producer.BatchingMiniProducer;

/**
 * Driver for Phase 8.
 *
 * Sends 12 records with batch size 5.
 * Expected flush pattern:
 *   5, 5, 2
 */
public class Phase8BatchingDriver {
    public static void main(String[] args) throws Exception {
        MiniKafkaBroker broker = new MiniKafkaBroker();
        broker.createTopic("orders", 3);

        BatchingMiniProducer producer = new BatchingMiniProducer(broker, 5);

        for (int i = 1; i <= 12; i++) {
            producer.send("orders", "order-" + i, "event-" + i);
        }

        // Flush remaining records.
        producer.flush();

        System.out.println("bufferedRecords=" + producer.bufferedRecords());
    }
}
```

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase8BatchingDriver
```

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


---

## Phase 9 Standalone Code — Retention

### What this phase builds

```text
Deletes old segment/index files from a partition directory.
```

### Files for this phase

```text
src/main/java/com/minikafka/storage/RetentionManager.java
src/main/java/com/minikafka/Phase9RetentionDriver.java
```

### RetentionManager.java

```java
package com.minikafka.storage;

import java.io.File;

/**
 * Deletes old files based on lastModified time.
 *
 * Production Kafka is more careful:
 *   1. never delete active segment
 *   2. delete log + index together
 *   3. respect retention.bytes and retention.ms
 */
public class RetentionManager {
    private final long retentionMillis;

    public RetentionManager(long retentionMillis) {
        this.retentionMillis = retentionMillis;
    }

    public int cleanup(File partitionDir) {
        File[] files = partitionDir.listFiles(
                (dir, name) -> name.endsWith(".log") || name.endsWith(".index")
        );

        if (files == null) {
            return 0;
        }

        int deleted = 0;
        long now = System.currentTimeMillis();

        for (File file : files) {
            boolean expired = now - file.lastModified() > retentionMillis;

            if (expired && file.delete()) {
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
import java.nio.file.Files;

/**
 * Driver for Phase 9.
 *
 * Creates fake segment files and deletes expired files.
 */
public class Phase9RetentionDriver {
    public static void main(String[] args) throws Exception {
        File dir = new File("data/phase9/orders-0");
        dir.mkdirs();

        File oldFile = new File(dir, "00000000000000000000.log");
        File newFile = new File(dir, "00000000000000000010.log");

        Files.writeString(oldFile.toPath(), "old segment");
        Files.writeString(newFile.toPath(), "new segment");

        // Make old file look old.
        oldFile.setLastModified(System.currentTimeMillis() - 10_000);

        RetentionManager retentionManager = new RetentionManager(1_000);
        int deleted = retentionManager.cleanup(dir);

        System.out.println("deletedFiles=" + deleted);
        System.out.println("oldExists=" + oldFile.exists());
        System.out.println("newExists=" + newFile.exists());
    }
}
```

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase9RetentionDriver
```

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


---

## Phase 10 Standalone Code — Backpressure

### What this phase builds

```text
A bounded producer buffer.
When buffer is full, new messages are rejected.
```

### Files for this phase

```text
src/main/java/com/minikafka/producer/ProducerRecord.java
src/main/java/com/minikafka/producer/BackpressureProducer.java
src/main/java/com/minikafka/Phase10BackpressureDriver.java
```

Use `ProducerRecord.java` from Phase 8.

### BackpressureProducer.java

```java
package com.minikafka.producer;

import com.minikafka.broker.MiniKafkaBroker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Producer with bounded queue.
 *
 * This protects the JVM from unbounded memory growth.
 */
public class BackpressureProducer {
    private final MiniKafkaBroker broker;
    private final BlockingQueue<ProducerRecord> queue;

    public BackpressureProducer(MiniKafkaBroker broker, int capacity) {
        this.broker = broker;
        this.queue = new ArrayBlockingQueue<>(capacity);
    }

    /**
     * Non-blocking send.
     *
     * Returns:
     *   true  -> accepted into queue
     *   false -> queue full, rejected
     */
    public boolean trySend(String topic, String key, String value) {
        return queue.offer(new ProducerRecord(topic, key, value));
    }

    /**
     * Simulates broker draining the producer queue.
     */
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

/**
 * Driver for Phase 10.
 *
 * Capacity is 3.
 * We try to send 5 records.
 * Last 2 should be rejected before drain.
 */
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

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase10BackpressureDriver
```

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


---

## Phase 11 Standalone Code — Consumer Groups

### What this phase builds

```text
Assigns partitions across consumers in the same group.
```

### Files for this phase

```text
src/main/java/com/minikafka/coordinator/ConsumerGroupCoordinator.java
src/main/java/com/minikafka/Phase11ConsumerGroupDriver.java
```

### ConsumerGroupCoordinator.java

```java
package com.minikafka.coordinator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Assigns partitions to consumers.
 *
 * Rule:
 *   one partition belongs to only one consumer inside a group
 */
public class ConsumerGroupCoordinator {
    public Map<String, List<Integer>> assignPartitions(List<String> consumerIds, int partitionCount) {
        if (consumerIds == null || consumerIds.isEmpty()) {
            throw new IllegalArgumentException("At least one consumer is required");
        }

        Map<String, List<Integer>> assignment = new LinkedHashMap<>();

        for (String consumerId : consumerIds) {
            assignment.put(consumerId, new ArrayList<>());
        }

        // Round-robin partition assignment.
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

/**
 * Driver for Phase 11.
 */
public class Phase11ConsumerGroupDriver {
    public static void main(String[] args) {
        ConsumerGroupCoordinator coordinator = new ConsumerGroupCoordinator();

        var assignment = coordinator.assignPartitions(
                List.of("consumer-1", "consumer-2"),
                6
        );

        System.out.println(assignment);
    }
}
```

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase11ConsumerGroupDriver
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


---

## Phase 12 Standalone Code — Rebalancing

### What this phase builds

```text
Recalculates partition assignment when consumers join or leave.
```

### Files for this phase

```text
src/main/java/com/minikafka/coordinator/ConsumerGroupCoordinator.java
src/main/java/com/minikafka/Phase12RebalanceDriver.java
```

Use `ConsumerGroupCoordinator.java` from Phase 11.

### Phase12RebalanceDriver.java

```java
package com.minikafka;

import com.minikafka.coordinator.ConsumerGroupCoordinator;

import java.util.List;

/**
 * Driver for Phase 12.
 *
 * Simulates:
 *   1. initial group: c1, c2
 *   2. c3 joins
 *   3. c2 leaves
 */
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

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase12RebalanceDriver
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


---

## Phase 13 Standalone Code — Broker and Network API Facade

### What this phase builds

```text
A broker facade object that exposes produce/consume as the stable API.
Network can be added later without changing storage logic.
```

### Files for this phase

```text
src/main/java/com/minikafka/broker/BrokerConfig.java
src/main/java/com/minikafka/broker/MiniKafkaBroker.java
src/main/java/com/minikafka/Phase13BrokerFacadeDriver.java
```

Use Phase 6 support classes.

### BrokerConfig.java

```java
package com.minikafka.broker;

/**
 * Broker configuration.
 *
 * baseDataDir controls where topic partition logs are stored.
 */
public record BrokerConfig(
        String brokerId,
        String baseDataDir
) {
}
```

### MiniKafkaBroker.java

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

/**
 * Broker facade.
 *
 * This is the boundary between client APIs and storage internals.
 */
public class MiniKafkaBroker {
    private final BrokerConfig config;
    private final Map<String, List<LogSegment>> topics = new ConcurrentHashMap<>();
    private final PartitionRouter router = new PartitionRouter();

    public MiniKafkaBroker(BrokerConfig config) {
        this.config = config;
    }

    public void createTopic(String topic, int partitionCount) throws IOException {
        List<LogSegment> partitions = new ArrayList<>();

        for (int p = 0; p < partitionCount; p++) {
            String path = config.baseDataDir() + "/" + topic + "-" + p + ".log";
            partitions.add(new LogSegment(path));
        }

        topics.put(topic, partitions);
    }

    public long produce(String topic, String key, String value) throws Exception {
        List<LogSegment> partitions = getPartitions(topic);
        int partition = router.route(key, partitions.size());
        return partitions.get(partition).append(key, value);
    }

    public List<ConsumerRecord> consume(String topic, int partition, long offset) throws Exception {
        List<LogSegment> partitions = getPartitions(topic);

        List<ConsumerRecord> result = new ArrayList<>();

        for (MessageRecord record : partitions.get(partition).readAll()) {
            if (record.offset() >= offset) {
                result.add(new ConsumerRecord(topic, partition, record.offset(), record.key(), record.value()));
            }
        }

        return result;
    }

    private List<LogSegment> getPartitions(String topic) {
        List<LogSegment> partitions = topics.get(topic);
        if (partitions == null) {
            throw new IllegalArgumentException("Unknown topic: " + topic);
        }
        return partitions;
    }
}
```

### Phase13BrokerFacadeDriver.java

```java
package com.minikafka;

import com.minikafka.broker.BrokerConfig;
import com.minikafka.broker.MiniKafkaBroker;

/**
 * Driver for Phase 13.
 */
public class Phase13BrokerFacadeDriver {
    public static void main(String[] args) throws Exception {
        MiniKafkaBroker broker = new MiniKafkaBroker(
                new BrokerConfig("broker-1", "data/phase13")
        );

        broker.createTopic("payments", 2);

        broker.produce("payments", "payment-1", "created");
        broker.produce("payments", "payment-2", "confirmed");

        for (int partition = 0; partition < 2; partition++) {
            System.out.println("partition=" + partition);
            broker.consume("payments", partition, 0).forEach(System.out::println);
        }
    }
}
```

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase13BrokerFacadeDriver
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


---

## Phase 14 Standalone Code — Replication

### What this phase builds

```text
Tracks replicas and ISR membership.
```

### Files for this phase

```text
src/main/java/com/minikafka/replication/BrokerNode.java
src/main/java/com/minikafka/replication/ReplicaState.java
src/main/java/com/minikafka/replication/ReplicationManager.java
src/main/java/com/minikafka/Phase14ReplicationDriver.java
```

### BrokerNode.java

```java
package com.minikafka.replication;

/**
 * Represents one broker in a cluster.
 */
public record BrokerNode(
        String brokerId,
        String host,
        int port
) {
}
```

### ReplicaState.java

```java
package com.minikafka.replication;

/**
 * A replica can be a leader or follower.
 */
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

/**
 * Tracks replicas and in-sync replicas.
 *
 * ISR = replicas that are caught up enough to be considered safe.
 */
public class ReplicationManager {
    private final Set<String> replicas = new LinkedHashSet<>();
    private final Set<String> inSyncReplicas = new LinkedHashSet<>();

    public void addReplica(String brokerId) {
        replicas.add(brokerId);

        // For this learning version, new replica starts in sync.
        inSyncReplicas.add(brokerId);
    }

    public void markOutOfSync(String brokerId) {
        inSyncReplicas.remove(brokerId);
    }

    public void markInSync(String brokerId) {
        if (replicas.contains(brokerId)) {
            inSyncReplicas.add(brokerId);
        }
    }

    public Set<String> replicas() {
        return Set.copyOf(replicas);
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

/**
 * Driver for Phase 14.
 */
public class Phase14ReplicationDriver {
    public static void main(String[] args) {
        ReplicationManager replication = new ReplicationManager();

        replication.addReplica("broker-1");
        replication.addReplica("broker-2");
        replication.addReplica("broker-3");

        System.out.println("replicas=" + replication.replicas());
        System.out.println("initialISR=" + replication.isr());

        replication.markOutOfSync("broker-3");

        System.out.println("afterBroker3Lag=" + replication.isr());
    }
}
```

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase14ReplicationDriver
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


---

## Phase 15 Standalone Code — Leader/Follower

### What this phase builds

```text
Chooses one leader from ISR.
Followers replicate from leader.
```

### Files for this phase

```text
src/main/java/com/minikafka/replication/LeaderElectionManager.java
src/main/java/com/minikafka/Phase15LeaderFollowerDriver.java
```

### LeaderElectionManager.java

```java
package com.minikafka.replication;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Very small leader election model.
 *
 * Real Kafka uses controller metadata and leader epochs.
 * Here we choose the first available ISR broker.
 */
public class LeaderElectionManager {
    private final Set<String> isr = new LinkedHashSet<>();
    private String leaderId;

    public void addInSyncReplica(String brokerId) {
        isr.add(brokerId);

        // First ISR broker becomes leader if no leader exists.
        if (leaderId == null) {
            leaderId = brokerId;
        }
    }

    public Optional<String> currentLeader() {
        return Optional.ofNullable(leaderId);
    }

    public void brokerFailed(String brokerId) {
        isr.remove(brokerId);

        if (brokerId.equals(leaderId)) {
            electNewLeader();
        }
    }

    private void electNewLeader() {
        leaderId = isr.stream().findFirst().orElse(null);
    }

    public Set<String> isr() {
        return Set.copyOf(isr);
    }
}
```

### Phase15LeaderFollowerDriver.java

```java
package com.minikafka;

import com.minikafka.replication.LeaderElectionManager;

/**
 * Driver for Phase 15.
 *
 * Simulates leader failure and new leader election.
 */
public class Phase15LeaderFollowerDriver {
    public static void main(String[] args) {
        LeaderElectionManager election = new LeaderElectionManager();

        election.addInSyncReplica("broker-1");
        election.addInSyncReplica("broker-2");
        election.addInSyncReplica("broker-3");

        System.out.println("initialLeader=" + election.currentLeader().orElse("none"));

        election.brokerFailed("broker-1");

        System.out.println("afterLeaderFailure=" + election.currentLeader().orElse("none"));
        System.out.println("isr=" + election.isr());
    }
}
```

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase15LeaderFollowerDriver
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


---

## Phase 16 Standalone Code — ISR and Ack Modes

### What this phase builds

```text
Simulates producer ack decisions:
acks=0, acks=1, acks=all.
```

### Files for this phase

```text
src/main/java/com/minikafka/producer/AckMode.java
src/main/java/com/minikafka/replication/AckDecisionService.java
src/main/java/com/minikafka/Phase16AckModesDriver.java
```

### AckMode.java

```java
package com.minikafka.producer;

/**
 * Producer durability mode.
 */
public enum AckMode {
    NONE,   // producer does not wait
    LEADER, // wait for leader write
    ALL     // wait for all ISR replicas
}
```

### AckDecisionService.java

```java
package com.minikafka.replication;

import com.minikafka.producer.AckMode;

/**
 * Decides whether a produce request can be acknowledged.
 */
public class AckDecisionService {
    public boolean canAcknowledge(
            AckMode ackMode,
            boolean leaderWritten,
            int isrSize,
            int successfulReplicaWrites
    ) {
        return switch (ackMode) {
            case NONE -> true;
            case LEADER -> leaderWritten;
            case ALL -> leaderWritten && successfulReplicaWrites >= isrSize;
        };
    }
}
```

### Phase16AckModesDriver.java

```java
package com.minikafka;

import com.minikafka.producer.AckMode;
import com.minikafka.replication.AckDecisionService;

/**
 * Driver for Phase 16.
 */
public class Phase16AckModesDriver {
    public static void main(String[] args) {
        AckDecisionService service = new AckDecisionService();

        boolean leaderWritten = true;
        int isrSize = 3;
        int successfulWrites = 2;

        for (AckMode mode : AckMode.values()) {
            boolean ack = service.canAcknowledge(mode, leaderWritten, isrSize, successfulWrites);
            System.out.println(mode + " ack=" + ack);
        }
    }
}
```

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase16AckModesDriver
```

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


---

## Phase 17 Standalone Code — Page Cache, mmap, and Zero-Copy

### What this phase builds

```text
Uses FileChannel.transferTo(...) to copy file bytes to an output channel.
This demonstrates the Java API behind zero-copy style transfer.
```

### Files for this phase

```text
src/main/java/com/minikafka/storage/ZeroCopyFileSender.java
src/main/java/com/minikafka/Phase17ZeroCopyDriver.java
```

### ZeroCopyFileSender.java

```java
package com.minikafka.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;

/**
 * Demonstrates zero-copy style transfer.
 *
 * FileChannel.transferTo can transfer bytes from file channel
 * directly to another channel with fewer user-space copies.
 */
public class ZeroCopyFileSender {
    private final File sourceFile;

    public ZeroCopyFileSender(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public long transferTo(WritableByteChannel targetChannel) throws IOException {
        try (FileInputStream input = new FileInputStream(sourceFile)) {
            long position = 0;
            long remaining = sourceFile.length();

            while (remaining > 0) {
                long transferred = input.getChannel().transferTo(position, remaining, targetChannel);
                position += transferred;
                remaining -= transferred;
            }

            return position;
        }
    }
}
```

### Phase17ZeroCopyDriver.java

```java
package com.minikafka;

import com.minikafka.storage.ZeroCopyFileSender;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.Channels;
import java.nio.file.Files;

/**
 * Driver for Phase 17.
 *
 * Copies one file to another using transferTo.
 */
public class Phase17ZeroCopyDriver {
    public static void main(String[] args) throws Exception {
        File source = new File("data/phase17/source.log");
        File target = new File("data/phase17/target.log");

        source.getParentFile().mkdirs();
        Files.writeString(source.toPath(), "message-1\nmessage-2\nmessage-3\n");

        ZeroCopyFileSender sender = new ZeroCopyFileSender(source);

        try (FileOutputStream output = new FileOutputStream(target)) {
            long bytes = sender.transferTo(Channels.newChannel(output));
            System.out.println("transferredBytes=" + bytes);
        }

        System.out.println("targetContent=");
        System.out.println(Files.readString(target.toPath()));
    }
}
```

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase17ZeroCopyDriver
```

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


---

## Phase 18 Standalone Code — Metrics and Observability

### What this phase builds

```text
Tracks broker counters:
messages in, messages out, bytes in, bytes out, rejected messages.
```

### Files for this phase

```text
src/main/java/com/minikafka/metrics/MetricsRegistry.java
src/main/java/com/minikafka/Phase18MetricsDriver.java
```

### MetricsRegistry.java

```java
package com.minikafka.metrics;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe metrics registry.
 *
 * AtomicLong is used because broker code can be accessed by many threads.
 */
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
                ", rejectedMessages=" + rejectedMessages.get();
    }
}
```

### Phase18MetricsDriver.java

```java
package com.minikafka;

import com.minikafka.metrics.MetricsRegistry;

/**
 * Driver for Phase 18.
 */
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

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase18MetricsDriver
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


---

## Phase 19 Standalone Code — Load Testing

### What this phase builds

```text
Simple in-process load test that measures messages/sec.
```

### Files for this phase

```text
src/main/java/com/minikafka/load/LoadTestResult.java
src/main/java/com/minikafka/load/MiniKafkaLoadTester.java
src/main/java/com/minikafka/Phase19LoadTestingDriver.java
```

This phase uses the Phase 6 broker and producer classes.

### LoadTestResult.java

```java
package com.minikafka.load;

/**
 * Result of a simple load test.
 */
public record LoadTestResult(
        int messages,
        long durationMillis,
        double messagesPerSecond
) {
}
```

### MiniKafkaLoadTester.java

```java
package com.minikafka.load;

import com.minikafka.producer.MiniProducer;

/**
 * Sends many records and measures throughput.
 */
public class MiniKafkaLoadTester {
    private final MiniProducer producer;

    public MiniKafkaLoadTester(MiniProducer producer) {
        this.producer = producer;
    }

    public LoadTestResult run(String topic, int messages) throws Exception {
        long startNanos = System.nanoTime();

        for (int i = 0; i < messages; i++) {
            producer.send(topic, "key-" + i, "value-" + i);
        }

        long endNanos = System.nanoTime();
        long durationMillis = Math.max(1, (endNanos - startNanos) / 1_000_000);
        double messagesPerSecond = messages * 1000.0 / durationMillis;

        return new LoadTestResult(messages, durationMillis, messagesPerSecond);
    }
}
```

### Phase19LoadTestingDriver.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.load.MiniKafkaLoadTester;
import com.minikafka.producer.MiniProducer;

/**
 * Driver for Phase 19.
 *
 * This is not a perfect benchmark.
 * It is a learning benchmark to compare changes.
 */
public class Phase19LoadTestingDriver {
    public static void main(String[] args) throws Exception {
        MiniKafkaBroker broker = new MiniKafkaBroker();
        broker.createTopic("orders", 3);

        MiniProducer producer = new MiniProducer(broker);
        MiniKafkaLoadTester loadTester = new MiniKafkaLoadTester(producer);

        var result = loadTester.run("orders", 10_000);

        System.out.println(result);
    }
}
```

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase19LoadTestingDriver
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


---

## Phase 20 Standalone Code — Production Hardening Simulation

### What this phase builds

```text
A small hardening checklist runner.
It verifies important production-readiness flags.
```

### Files for this phase

```text
src/main/java/com/minikafka/hardening/HardeningCheck.java
src/main/java/com/minikafka/hardening/HardeningReport.java
src/main/java/com/minikafka/hardening/ProductionHardeningChecker.java
src/main/java/com/minikafka/Phase20ProductionHardeningDriver.java
```

### HardeningCheck.java

```java
package com.minikafka.hardening;

/**
 * One production-hardening item.
 */
public record HardeningCheck(
        String name,
        boolean passed,
        String recommendation
) {
}
```

### HardeningReport.java

```java
package com.minikafka.hardening;

import java.util.List;

/**
 * Collection of hardening checks.
 */
public record HardeningReport(
        List<HardeningCheck> checks
) {
    public long passedCount() {
        return checks.stream().filter(HardeningCheck::passed).count();
    }

    public long failedCount() {
        return checks.size() - passedCount();
    }
}
```

### ProductionHardeningChecker.java

```java
package com.minikafka.hardening;

import java.util.ArrayList;
import java.util.List;

/**
 * Simulates production hardening checks.
 *
 * This phase is intentionally not a broker feature.
 * It teaches how to think about readiness.
 */
public class ProductionHardeningChecker {
    private boolean hasRetention;
    private boolean hasBackpressure;
    private boolean hasMetrics;
    private boolean hasReplication;
    private boolean hasGracefulShutdown;

    public ProductionHardeningChecker withRetention(boolean value) {
        this.hasRetention = value;
        return this;
    }

    public ProductionHardeningChecker withBackpressure(boolean value) {
        this.hasBackpressure = value;
        return this;
    }

    public ProductionHardeningChecker withMetrics(boolean value) {
        this.hasMetrics = value;
        return this;
    }

    public ProductionHardeningChecker withReplication(boolean value) {
        this.hasReplication = value;
        return this;
    }

    public ProductionHardeningChecker withGracefulShutdown(boolean value) {
        this.hasGracefulShutdown = value;
        return this;
    }

    public HardeningReport run() {
        List<HardeningCheck> checks = new ArrayList<>();

        checks.add(new HardeningCheck(
                "Retention",
                hasRetention,
                "Add time-based and size-based retention."
        ));

        checks.add(new HardeningCheck(
                "Backpressure",
                hasBackpressure,
                "Use bounded queues and reject/block when overloaded."
        ));

        checks.add(new HardeningCheck(
                "Metrics",
                hasMetrics,
                "Expose throughput, latency, lag, errors, and disk usage."
        ));

        checks.add(new HardeningCheck(
                "Replication",
                hasReplication,
                "Replicate partitions and track ISR."
        ));

        checks.add(new HardeningCheck(
                "Graceful shutdown",
                hasGracefulShutdown,
                "Flush producer buffers and close files safely."
        ));

        return new HardeningReport(checks);
    }
}
```

### Phase20ProductionHardeningDriver.java

```java
package com.minikafka;

import com.minikafka.hardening.ProductionHardeningChecker;

/**
 * Driver for Phase 20.
 *
 * Prints a production-readiness checklist.
 */
public class Phase20ProductionHardeningDriver {
    public static void main(String[] args) {
        ProductionHardeningChecker checker = new ProductionHardeningChecker()
                .withRetention(true)
                .withBackpressure(true)
                .withMetrics(true)
                .withReplication(false)
                .withGracefulShutdown(false);

        var report = checker.run();

        report.checks().forEach(check ->
                System.out.println(check.name() + " passed=" + check.passed()
                        + " recommendation=" + check.recommendation())
        );

        System.out.println("passed=" + report.passedCount());
        System.out.println("failed=" + report.failedCount());
    }
}
```

### Run

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase20ProductionHardeningDriver
```

---

## Phase Coding Track Summary

```text
Phase 1  -> append-only file
Phase 2  -> segment rolling
Phase 3  -> offset index
Phase 4  -> partition routing
Phase 5  -> producer API
Phase 6  -> consumer API
Phase 7  -> offset manager
Phase 8  -> batching
Phase 9  -> retention
Phase 10 -> backpressure
Phase 11 -> consumer groups
Phase 12 -> rebalancing
Phase 13 -> broker facade
Phase 14 -> replication tracking
Phase 15 -> leader/follower election
Phase 16 -> ISR and ack modes
Phase 17 -> zero-copy style file transfer
Phase 18 -> metrics
Phase 19 -> load testing
Phase 20 -> production hardening checklist
```

# Part 28 — Complete Java Code Pack

> Note: Complete standalone Java code packs and driver classes are now added directly inside every phase section from Part 8 to Part 27.

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
