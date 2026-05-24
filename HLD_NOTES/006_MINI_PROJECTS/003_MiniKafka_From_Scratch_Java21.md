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
