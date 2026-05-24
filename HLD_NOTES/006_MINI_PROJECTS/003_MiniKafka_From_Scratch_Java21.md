# 003_MiniKafka_From_Scratch_Java21

# Clickable Index

- [Part 0 — Goal Of This Handbook](#part-0--goal-of-this-handbook)
- [Part 1 — Why Kafka Exists](#part-1--why-kafka-exists)
- [Part 2 — Kafka From First Principles](#part-2--kafka-from-first-principles)
  - [2.1 Queue vs Log](#21-queue-vs-log)
  - [2.2 Topic](#22-topic)
  - [2.3 Partition](#23-partition)
  - [2.4 Offset](#24-offset)
  - [2.5 Producer](#25-producer)
  - [2.6 Consumer](#26-consumer)
  - [2.7 Consumer Group](#27-consumer-group)
  - [2.8 Retention](#28-retention)
  - [2.9 Replication](#29-replication)
  - [2.10 Why Kafka Is Fast](#210-why-kafka-is-fast)
- [Part 3 — Final MiniKafka System](#part-3--final-minikafka-system)
- [Part 4 — Maven Project Setup](#part-4--maven-project-setup)
- [Part 5 — Phase 1: Append-Only Commit Log](#part-5--phase-1-append-only-commit-log)
- [Part 6 — Phase 2: Topic and Partition](#part-6--phase-2-topic-and-partition)
- [Part 7 — Phase 3: Producer API](#part-7--phase-3-producer-api)
- [Part 8 — Phase 4: Consumer Poll By Offset](#part-8--phase-4-consumer-poll-by-offset)
- [Part 9 — Phase 5: Offset Index](#part-9--phase-5-offset-index)
- [Part 10 — Phase 6: Consumer Group Offset Commit](#part-10--phase-6-consumer-group-offset-commit)
- [Part 11 — Phase 7: Retention and Segment Cleanup](#part-11--phase-7-retention-and-segment-cleanup)
- [Part 12 — Phase 8: Broker TCP Server](#part-12--phase-8-broker-tcp-server)
- [Part 13 — Phase 9: Batching and Flush](#part-13--phase-9-batching-and-flush)
- [Part 14 — Phase 10: Replication Model](#part-14--phase-10-replication-model)
- [Part 15 — Phase 11: Metrics and Observability](#part-15--phase-11-metrics-and-observability)
- [Part 16 — Final Working Code](#part-16--final-working-code)
- [Part 17 — Driver Classes](#part-17--driver-classes)
- [Part 18 — JUnit Tests](#part-18--junit-tests)
- [Part 19 — Load Test Ideas](#part-19--load-test-ideas)
- [Part 20 — Failure Handling](#part-20--failure-handling)
- [Part 21 — Scaling Discussion](#part-21--scaling-discussion)
- [Part 22 — Interview Explanation](#part-22--interview-explanation)
- [Part 23 — What This Teaches For Distributed Systems](#part-23--what-this-teaches-for-distributed-systems)
- [Part 24 — Next Advanced Extensions](#part-24--next-advanced-extensions)

---

# Part 0 — Goal Of This Handbook

We are building:

```text
MiniKafka
```

using:

```text
Java 21
Pure Java first
No Spring Boot in the core engine
No real Kafka dependency
```

Goal:

```text
Understand Kafka internals by building a small durable log broker.
```

We are not building full Apache Kafka.

We are building the core mechanics:

```text
append-only log
topic
partition
offset
producer send
consumer poll
consumer group offset commit
segment file
offset index
retention
batching
broker server
basic replication model
metrics
```

Learning model:

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

---

# Part 1 — Why Kafka Exists

Before Kafka, systems often communicated directly:

```text
Order Service ---> Payment Service
Order Service ---> Email Service
Order Service ---> Inventory Service
Order Service ---> Analytics Service
```

Problems:

```text
tight coupling
slow downstream breaks upstream
hard replay
hard auditing
hard fanout
hard scaling
```

Kafka solves this using a durable event log:

```text
Producer writes event
        ↓
Kafka stores event durably
        ↓
Consumers read independently
```

Architecture:

```text
Producer
   |
   v
+-------------------+
| Kafka Topic       |
| append-only log   |
+-------------------+
   |       |       |
   v       v       v
Consumer Consumer Consumer
```

Kafka is useful for:

```text
event-driven architecture
async processing
log aggregation
streaming analytics
audit logs
CDC pipelines
notification systems
microservice decoupling
```

---

# Part 2 — Kafka From First Principles

## 2.1 Queue vs Log

Traditional queue:

```text
Message consumed once
Then removed
```

Log:

```text
Message appended
Consumers read by offset
Message stays until retention deletes it
```

Queue mental model:

```text
[ A ][ B ][ C ]
consume A
[ B ][ C ]
```

Kafka log mental model:

```text
offset 0 -> A
offset 1 -> B
offset 2 -> C

consumer stores: nextOffset = 1
```

Kafka does not delete messages immediately after consumption.

Consumers track offsets.

---

## 2.2 Topic

A topic is a named stream of records.

Example:

```text
orders
payments
driver-location
logs
```

Topic contains partitions.

```text
Topic: orders
├── partition-0
├── partition-1
└── partition-2
```

---

## 2.3 Partition

Partition is an ordered append-only log.

```text
partition-0
offset 0 -> event A
offset 1 -> event B
offset 2 -> event C
```

Ordering guarantee:

```text
Kafka guarantees order inside a partition.
Kafka does not guarantee global order across partitions.
```

Why partition exists:

```text
parallel writes
parallel reads
horizontal scalability
ordering boundary
```

---

## 2.4 Offset

Offset is the position of a record inside a partition.

```text
offset = 0
offset = 1
offset = 2
```

Consumer reads:

```text
poll(topic, partition, offset)
```

Offset gives:

```text
replay
resume
backtracking
at-least-once processing
```

---

## 2.5 Producer

Producer writes records to topic partition.

```text
send(topic, key, value)
```

Partitioning rule:

```text
partition = hash(key) % partitionCount
```

Why key matters:

```text
same key goes to same partition
ordering preserved for that key
```

Example:

```text
orderId=101 -> partition-2
orderId=102 -> partition-0
orderId=101 -> partition-2 again
```

---

## 2.6 Consumer

Consumer reads records from offset.

```text
poll(topic, partition, offset, maxRecords)
```

Consumer controls its own speed.

Kafka uses pull model:

```text
consumer asks broker for messages
broker returns available messages
```

Why pull model?

```text
consumer controls backpressure
slow consumer does not get overwhelmed
consumer can batch reads
```

---

## 2.7 Consumer Group

Consumer group allows multiple consumers to share partitions.

```text
Group: notification-service

Consumer A -> partition-0
Consumer B -> partition-1
Consumer C -> partition-2
```

Rule:

```text
One partition is consumed by only one consumer inside same group.
```

But different groups can read same topic independently.

```text
Group analytics reads all events
Group email reads all events
Group audit reads all events
```

---

## 2.8 Retention

Kafka deletes old data by retention policy:

```text
time-based retention
size-based retention
```

Important:

```text
consumption does not delete records
retention deletes records
```

---

## 2.9 Replication

Replication gives durability and availability.

```text
partition-0 leader: broker-1
partition-0 follower: broker-2
partition-0 follower: broker-3
```

Producer writes to leader.

Follower copies from leader.

If leader fails, follower can become leader.

---

## 2.10 Why Kafka Is Fast

Kafka is fast because of:

```text
append-only sequential writes
OS page cache
batching
zero-copy
partition parallelism
consumer pull model
minimal random disk IO
```

Disk myth:

```text
Random disk IO is slow.
Sequential disk IO can be very fast.
```

MiniKafka will show this by writing records sequentially to log files.

---

# Part 3 — Final MiniKafka System

Architecture:

```text
Producer Client
      |
      v
+-------------------+
| MiniKafkaBroker   |
|-------------------|
| TopicManager      |
| PartitionLog      |
| OffsetIndex       |
| ConsumerOffsets   |
| RetentionManager  |
| Metrics           |
+-------------------+
      |
      v
Disk Storage
```

Storage layout:

```text
data/
└── topics/
    └── orders/
        ├── partition-0/
        │   ├── log.data
        │   └── offset.index
        └── partition-1/
            ├── log.data
            └── offset.index
```

Record format:

```text
offset | timestamp | keyLength | valueLength | key | value
```

Core API:

```java
broker.createTopic("orders", 3);
long offset = broker.send("orders", "order-1", "created");
List<Record> records = broker.poll("orders", 0, 0, 10);
broker.commitOffset("group-a", "orders", 0, 5);
long next = broker.committedOffset("group-a", "orders", 0);
```

---

# Part 4 — Maven Project Setup

```text
mini-kafka/
├── pom.xml
├── README.md
├── data/
├── src/main/java/com/minikafka/
│   ├── broker/
│   │   ├── MiniKafkaBroker.java
│   │   ├── TopicManager.java
│   │   └── BrokerMetrics.java
│   ├── model/
│   │   ├── Record.java
│   │   ├── ProduceResult.java
│   │   └── TopicPartition.java
│   ├── storage/
│   │   ├── PartitionLog.java
│   │   ├── OffsetIndex.java
│   │   └── LogSegment.java
│   ├── consumer/
│   │   └── ConsumerOffsetStore.java
│   ├── retention/
│   │   └── RetentionManager.java
│   ├── server/
│   │   └── MiniKafkaTcpServer.java
│   └── demo/
│       ├── DemoProduceConsume.java
│       ├── DemoConsumerGroup.java
│       └── DemoThroughput.java
└── src/test/java/com/minikafka/
    ├── PartitionLogTest.java
    ├── MiniKafkaBrokerTest.java
    ├── OffsetIndexTest.java
    └── ConsumerOffsetStoreTest.java
```

## pom.xml

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

# Part 5 — Phase 1: Append-Only Commit Log

## What are we building?

A file-backed append-only log.

## Why?

Kafka is fundamentally a durable log.

Before topics, partitions, producers, and consumers, we need:

```text
append record
read from offset
persist to disk
```

## What we care about

```text
sequential writes
durability
offset assignment
record encoding
file position
```

Flow:

```text
append(key,value)
    ↓
assign next offset
    ↓
serialize record
    ↓
write to log.data
    ↓
return offset
```

Dry run:

```text
append A -> offset 0
append B -> offset 1
append C -> offset 2
```

Log:

```text
0 A
1 B
2 C
```

---

# Part 6 — Phase 2: Topic and Partition

## What are we building?

A topic with multiple partitions.

```text
orders
├── partition-0
├── partition-1
└── partition-2
```

## Why?

Single log cannot scale forever.

Partitions give:

```text
parallel writes
parallel reads
key-based ordering
horizontal scalability
```

Producer routing:

```text
partition = abs(hash(key)) % partitionCount
```

---

# Part 7 — Phase 3: Producer API

## What are we building?

A broker-level send API:

```java
send(topic, key, value)
```

## Why?

Producer should not directly know storage files.

Broker handles:

```text
topic lookup
partition selection
append
metrics update
result return
```

Flow:

```text
Producer
   ↓
Broker.send(topic,key,value)
   ↓
choose partition
   ↓
PartitionLog.append
   ↓
return topic, partition, offset
```

---

# Part 8 — Phase 4: Consumer Poll By Offset

## What are we building?

Consumer read API:

```java
poll(topic, partition, offset, maxRecords)
```

## Why?

Kafka consumers control their own progress.

Consumer does not remove messages.

Consumer reads from offset.

Flow:

```text
Consumer has nextOffset=5
    ↓
poll(orders, partition-0, 5, 10)
    ↓
broker reads records from offset >= 5
    ↓
consumer processes
    ↓
consumer commits offset
```

---

# Part 9 — Phase 5: Offset Index

## What are we building?

A mapping:

```text
offset -> byte position in log file
```

## Why?

Without index:

```text
poll from offset 500000
    ↓
scan from beginning
    ↓
slow
```

With index:

```text
offset 500000 -> file position 123456789
seek directly
```

Mini index format:

```text
offset,position
```

Production Kafka uses sparse indexes and segments.

MiniKafka can use in-memory plus file persisted index.

---

# Part 10 — Phase 6: Consumer Group Offset Commit

## What are we building?

Store committed offset per group/topic/partition.

Key:

```text
groupId + topic + partition
```

Value:

```text
next offset to read
```

Example:

```text
notification-service:orders:0 -> 12
analytics-service:orders:0 -> 4
```

Why?

Different applications process same topic independently.

---

# Part 11 — Phase 7: Retention and Segment Cleanup

## What are we building?

Delete old records by size or time.

Simplified MiniKafka retention:

```text
if log file bigger than maxBytes:
    compact by keeping newest records
```

Better future design:

```text
log-000000.data
log-001000.data
log-002000.data
```

Then delete old segments.

Why retention?

```text
disk is finite
Kafka keeps records until retention
not until consumption
```

---

# Part 12 — Phase 8: Broker TCP Server

## What are we building?

A simple TCP protocol.

Commands:

```text
CREATE_TOPIC orders 3
SEND orders key value
POLL orders 0 0 10
COMMIT group1 orders 0 5
OFFSET group1 orders 0
```

Why?

Real Kafka is a network broker.

This teaches:

```text
client-server protocol
request parsing
thread-per-client model
future Netty upgrade
```

---

# Part 13 — Phase 9: Batching and Flush

## What are we building?

Batch writes and controlled flush.

Why?

Writing one record and fsyncing every time is slow.

Batching:

```text
collect records
write together
flush periodically
```

Tradeoff:

```text
higher throughput
but slightly higher latency
and possible data loss if not flushed
```

Kafka uses producer batching and broker IO optimization.

---

# Part 14 — Phase 10: Replication Model

## What are we building?

A simplified leader-follower mental model.

Not full replication code first.

Model:

```text
Producer -> leader partition
Follower fetches from leader
Follower appends same records
```

Concepts:

```text
leader
follower
replication lag
ISR
acks=1
acks=all
leader failover
```

Mini implementation later:

```text
Leader broker exposes POLL
Follower broker periodically polls leader from last offset
```

---

# Part 15 — Phase 11: Metrics and Observability

Metrics:

```text
recordsProduced
recordsConsumed
bytesWritten
bytesRead
activeTopics
activePartitions
logSizeBytes
produceErrors
consumeErrors
```

Why?

Without metrics, you cannot understand bottlenecks.

Production systems need:

```text
throughput
latency
queue depth
consumer lag
disk usage
error rate
```

---

# Part 16 — Final Working Code

## Record.java

```java
package com.minikafka.model;

public record Record(
        long offset,
        long timestampMillis,
        String key,
        String value
) {
}
```

## ProduceResult.java

```java
package com.minikafka.model;

public record ProduceResult(
        String topic,
        int partition,
        long offset
) {
}
```

## TopicPartition.java

```java
package com.minikafka.model;

public record TopicPartition(String topic, int partition) {
    @Override
    public String toString() {
        return topic + "-" + partition;
    }
}
```

## BrokerMetrics.java

```java
package com.minikafka.broker;

import java.util.concurrent.atomic.AtomicLong;

public class BrokerMetrics {
    private final AtomicLong recordsProduced = new AtomicLong();
    private final AtomicLong recordsConsumed = new AtomicLong();
    private final AtomicLong bytesWritten = new AtomicLong();
    private final AtomicLong bytesRead = new AtomicLong();
    private final AtomicLong produceErrors = new AtomicLong();
    private final AtomicLong consumeErrors = new AtomicLong();

    public void recordProduced(long bytes) {
        recordsProduced.incrementAndGet();
        bytesWritten.addAndGet(bytes);
    }

    public void recordConsumed(long bytes) {
        recordsConsumed.incrementAndGet();
        bytesRead.addAndGet(bytes);
    }

    public void produceError() {
        produceErrors.incrementAndGet();
    }

    public void consumeError() {
        consumeErrors.incrementAndGet();
    }

    public long recordsProduced() {
        return recordsProduced.get();
    }

    public long recordsConsumed() {
        return recordsConsumed.get();
    }

    public long bytesWritten() {
        return bytesWritten.get();
    }

    public long bytesRead() {
        return bytesRead.get();
    }

    @Override
    public String toString() {
        return "BrokerMetrics{" +
                "recordsProduced=" + recordsProduced.get() +
                ", recordsConsumed=" + recordsConsumed.get() +
                ", bytesWritten=" + bytesWritten.get() +
                ", bytesRead=" + bytesRead.get() +
                ", produceErrors=" + produceErrors.get() +
                ", consumeErrors=" + consumeErrors.get() +
                '}';
    }
}
```

## OffsetIndex.java

```java
package com.minikafka.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OffsetIndex {
    private final Map<Long, Long> offsetToPosition = new ConcurrentHashMap<>();

    public void add(long offset, long position) {
        offsetToPosition.put(offset, position);
    }

    public Long position(long offset) {
        return offsetToPosition.get(offset);
    }

    public int size() {
        return offsetToPosition.size();
    }
}
```

## PartitionLog.java

```java
package com.minikafka.storage;

import com.minikafka.model.Record;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PartitionLog implements AutoCloseable {
    private final Path dir;
    private final Path logFile;
    private final RandomAccessFile raf;
    private final OffsetIndex index = new OffsetIndex();

    private long nextOffset = 0;

    public PartitionLog(Path dir) {
        try {
            this.dir = dir;
            Files.createDirectories(dir);
            this.logFile = dir.resolve("log.data");
            this.raf = new RandomAccessFile(logFile.toFile(), "rw");
            rebuildIndex();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create partition log: " + dir, e);
        }
    }

    public synchronized long append(String key, String value) {
        try {
            long offset = nextOffset++;
            long position = raf.length();
            raf.seek(position);

            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);

            raf.writeLong(offset);
            raf.writeLong(System.currentTimeMillis());
            raf.writeInt(keyBytes.length);
            raf.writeInt(valueBytes.length);
            raf.write(keyBytes);
            raf.write(valueBytes);

            index.add(offset, position);
            return offset;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to append record", e);
        }
    }

    public synchronized List<Record> readFrom(long startOffset, int maxRecords) {
        try {
            List<Record> records = new ArrayList<>();

            Long position = index.position(startOffset);

            if (position == null) {
                position = findNearestPosition(startOffset);
            }

            if (position == null) {
                return records;
            }

            raf.seek(position);

            while (records.size() < maxRecords && raf.getFilePointer() < raf.length()) {
                long offset = raf.readLong();
                long timestamp = raf.readLong();
                int keyLength = raf.readInt();
                int valueLength = raf.readInt();

                byte[] keyBytes = new byte[keyLength];
                byte[] valueBytes = new byte[valueLength];

                raf.readFully(keyBytes);
                raf.readFully(valueBytes);

                if (offset >= startOffset) {
                    records.add(new Record(
                            offset,
                            timestamp,
                            new String(keyBytes, StandardCharsets.UTF_8),
                            new String(valueBytes, StandardCharsets.UTF_8)
                    ));
                }
            }

            return records;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read records", e);
        }
    }

    public synchronized long sizeBytes() {
        try {
            return raf.length();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public synchronized long nextOffset() {
        return nextOffset;
    }

    private void rebuildIndex() throws IOException {
        raf.seek(0);

        long position = 0;
        long maxOffset = -1;

        while (position < raf.length()) {
            raf.seek(position);

            long offset = raf.readLong();
            raf.readLong(); // timestamp
            int keyLength = raf.readInt();
            int valueLength = raf.readInt();

            index.add(offset, position);
            maxOffset = Math.max(maxOffset, offset);

            long recordSize = 8 + 8 + 4 + 4 + keyLength + valueLength;
            position += recordSize;
        }

        nextOffset = maxOffset + 1;
    }

    private Long findNearestPosition(long startOffset) {
        for (long offset = startOffset; offset >= 0; offset--) {
            Long position = index.position(offset);
            if (position != null) {
                return position;
            }
        }
        return null;
    }

    @Override
    public void close() {
        try {
            raf.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
```

## TopicManager.java

```java
package com.minikafka.broker;

import com.minikafka.storage.PartitionLog;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TopicManager {
    private final Path dataDir;
    private final Map<String, PartitionLog[]> topics = new ConcurrentHashMap<>();

    public TopicManager(Path dataDir) {
        this.dataDir = dataDir;
    }

    public void createTopic(String topic, int partitions) {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("topic cannot be blank");
        }

        if (partitions <= 0) {
            throw new IllegalArgumentException("partitions must be positive");
        }

        topics.computeIfAbsent(topic, t -> {
            PartitionLog[] logs = new PartitionLog[partitions];

            for (int p = 0; p < partitions; p++) {
                Path partitionDir = dataDir
                        .resolve("topics")
                        .resolve(topic)
                        .resolve("partition-" + p);

                logs[p] = new PartitionLog(partitionDir);
            }

            return logs;
        });
    }

    public PartitionLog partition(String topic, int partition) {
        PartitionLog[] logs = topics.get(topic);

        if (logs == null) {
            throw new IllegalArgumentException("Unknown topic: " + topic);
        }

        if (partition < 0 || partition >= logs.length) {
            throw new IllegalArgumentException("Invalid partition: " + partition);
        }

        return logs[partition];
    }

    public int partitionCount(String topic) {
        PartitionLog[] logs = topics.get(topic);

        if (logs == null) {
            throw new IllegalArgumentException("Unknown topic: " + topic);
        }

        return logs.length;
    }
}
```

## ConsumerOffsetStore.java

```java
package com.minikafka.consumer;

import com.minikafka.model.TopicPartition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerOffsetStore {
    private final Map<String, Long> offsets = new ConcurrentHashMap<>();

    public void commit(String groupId, TopicPartition topicPartition, long nextOffset) {
        offsets.put(key(groupId, topicPartition), nextOffset);
    }

    public long committedOffset(String groupId, TopicPartition topicPartition) {
        return offsets.getOrDefault(key(groupId, topicPartition), 0L);
    }

    private String key(String groupId, TopicPartition tp) {
        return groupId + ":" + tp.topic() + ":" + tp.partition();
    }
}
```

## MiniKafkaBroker.java

```java
package com.minikafka.broker;

import com.minikafka.consumer.ConsumerOffsetStore;
import com.minikafka.model.ProduceResult;
import com.minikafka.model.Record;
import com.minikafka.model.TopicPartition;
import com.minikafka.storage.PartitionLog;

import java.nio.file.Path;
import java.util.List;

public class MiniKafkaBroker {
    private final TopicManager topicManager;
    private final ConsumerOffsetStore offsetStore = new ConsumerOffsetStore();
    private final BrokerMetrics metrics = new BrokerMetrics();

    public MiniKafkaBroker(Path dataDir) {
        this.topicManager = new TopicManager(dataDir);
    }

    public void createTopic(String topic, int partitions) {
        topicManager.createTopic(topic, partitions);
    }

    public ProduceResult send(String topic, String key, String value) {
        try {
            int partition = choosePartition(topic, key);
            PartitionLog log = topicManager.partition(topic, partition);
            long offset = log.append(key, value);

            long bytes = key.length() + value.length();
            metrics.recordProduced(bytes);

            return new ProduceResult(topic, partition, offset);
        } catch (RuntimeException e) {
            metrics.produceError();
            throw e;
        }
    }

    public List<Record> poll(String topic, int partition, long offset, int maxRecords) {
        try {
            PartitionLog log = topicManager.partition(topic, partition);
            List<Record> records = log.readFrom(offset, maxRecords);

            for (Record record : records) {
                metrics.recordConsumed(record.key().length() + record.value().length());
            }

            return records;
        } catch (RuntimeException e) {
            metrics.consumeError();
            throw e;
        }
    }

    public void commitOffset(String groupId, String topic, int partition, long nextOffset) {
        offsetStore.commit(groupId, new TopicPartition(topic, partition), nextOffset);
    }

    public long committedOffset(String groupId, String topic, int partition) {
        return offsetStore.committedOffset(groupId, new TopicPartition(topic, partition));
    }

    public BrokerMetrics metrics() {
        return metrics;
    }

    private int choosePartition(String topic, String key) {
        int partitionCount = topicManager.partitionCount(topic);
        return Math.abs(key.hashCode()) % partitionCount;
    }
}
```

---

# Part 17 — Driver Classes

## DemoProduceConsume.java

```java
package com.minikafka.demo;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.model.ProduceResult;
import com.minikafka.model.Record;

import java.nio.file.Path;
import java.util.List;

public class DemoProduceConsume {
    public static void main(String[] args) {
        MiniKafkaBroker broker = new MiniKafkaBroker(Path.of("data"));
        broker.createTopic("orders", 3);

        ProduceResult r1 = broker.send("orders", "order-1", "created");
        ProduceResult r2 = broker.send("orders", "order-2", "paid");
        ProduceResult r3 = broker.send("orders", "order-1", "shipped");

        System.out.println(r1);
        System.out.println(r2);
        System.out.println(r3);

        for (int partition = 0; partition < 3; partition++) {
            List<Record> records = broker.poll("orders", partition, 0, 10);
            System.out.println("partition=" + partition + ", records=" + records);
        }

        System.out.println(broker.metrics());
    }
}
```

## DemoConsumerGroup.java

```java
package com.minikafka.demo;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.model.Record;

import java.nio.file.Path;
import java.util.List;

public class DemoConsumerGroup {
    public static void main(String[] args) {
        MiniKafkaBroker broker = new MiniKafkaBroker(Path.of("data-consumer-group"));
        broker.createTopic("payments", 2);

        for (int i = 1; i <= 5; i++) {
            broker.send("payments", "payment-" + i, "amount=" + (i * 100));
        }

        String group = "billing-service";
        int partition = 0;

        long offset = broker.committedOffset(group, "payments", partition);
        List<Record> records = broker.poll("payments", partition, offset, 10);

        for (Record record : records) {
            System.out.println("Processing " + record);
            broker.commitOffset(group, "payments", partition, record.offset() + 1);
        }

        System.out.println("Next offset = " + broker.committedOffset(group, "payments", partition));
    }
}
```

## DemoThroughput.java

```java
package com.minikafka.demo;

import com.minikafka.broker.MiniKafkaBroker;

import java.nio.file.Path;

public class DemoThroughput {
    public static void main(String[] args) {
        MiniKafkaBroker broker = new MiniKafkaBroker(Path.of("data-throughput"));
        broker.createTopic("logs", 4);

        int records = 100_000;
        long start = System.nanoTime();

        for (int i = 0; i < records; i++) {
            broker.send("logs", "key-" + i, "log-message-" + i);
        }

        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        System.out.println("records=" + records);
        System.out.println("elapsedMs=" + elapsedMs);
        System.out.println("records/sec=" + (records * 1000L / Math.max(1, elapsedMs)));
        System.out.println(broker.metrics());
    }
}
```

---

# Part 18 — JUnit Tests

## PartitionLogTest.java

```java
package com.minikafka;

import com.minikafka.model.Record;
import com.minikafka.storage.PartitionLog;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PartitionLogTest {
    @Test
    void shouldAppendAndReadRecords() throws Exception {
        Path dir = Files.createTempDirectory("partition-log-test");

        try (PartitionLog log = new PartitionLog(dir)) {
            long o1 = log.append("k1", "v1");
            long o2 = log.append("k2", "v2");

            assertEquals(0, o1);
            assertEquals(1, o2);

            List<Record> records = log.readFrom(0, 10);

            assertEquals(2, records.size());
            assertEquals("k1", records.get(0).key());
            assertEquals("v2", records.get(1).value());
        }
    }
}
```

## MiniKafkaBrokerTest.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.model.ProduceResult;
import com.minikafka.model.Record;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MiniKafkaBrokerTest {
    @Test
    void shouldProduceAndConsume() throws Exception {
        Path dir = Files.createTempDirectory("broker-test");
        MiniKafkaBroker broker = new MiniKafkaBroker(dir);

        broker.createTopic("orders", 2);

        ProduceResult result = broker.send("orders", "order-1", "created");

        List<Record> records = broker.poll("orders", result.partition(), 0, 10);

        assertFalse(records.isEmpty());
        assertTrue(records.stream().anyMatch(r -> r.value().equals("created")));
    }
}
```

## ConsumerOffsetStoreTest.java

```java
package com.minikafka;

import com.minikafka.consumer.ConsumerOffsetStore;
import com.minikafka.model.TopicPartition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsumerOffsetStoreTest {
    @Test
    void shouldCommitOffset() {
        ConsumerOffsetStore store = new ConsumerOffsetStore();
        TopicPartition tp = new TopicPartition("orders", 0);

        assertEquals(0, store.committedOffset("group-a", tp));

        store.commit("group-a", tp, 10);

        assertEquals(10, store.committedOffset("group-a", tp));
    }
}
```

## OffsetIndexTest.java

```java
package com.minikafka;

import com.minikafka.storage.OffsetIndex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OffsetIndexTest {
    @Test
    void shouldMapOffsetToPosition() {
        OffsetIndex index = new OffsetIndex();

        index.add(10, 500);

        assertEquals(500, index.position(10));
    }
}
```

---

# Part 19 — Load Test Ideas

## Produce throughput

```text
Send 1 million messages
Measure records/sec
Measure file size
Measure CPU usage
```

## Consume throughput

```text
Poll in batches of 1
Poll in batches of 100
Poll in batches of 1000
Compare throughput
```

Expected:

```text
larger batches improve throughput
too-large batches may increase latency
```

## JVM bottlenecks to observe

```text
allocation rate from Record objects
GC pressure from many String allocations
synchronized append bottleneck
disk write throughput
file flush behavior
```

---

# Part 20 — Failure Handling

Failures to think about:

| Failure | MiniKafka behavior |
|---|---|
| broker crash before flush | possible data loss |
| disk full | append fails |
| corrupted log | rebuild index may fail |
| slow consumer | consumer lag grows |
| huge key/value | memory pressure |
| too many partitions | file handles and memory pressure |

Production Kafka handles these with:

```text
checksums
segments
replication
fsync policy
retries
idempotent producer
controller metadata
leader election
```

---

# Part 21 — Scaling Discussion

Single partition:

```text
one ordered log
limited write parallelism
```

Multiple partitions:

```text
parallel writes
parallel consumers
ordering only per partition
```

Scaling reads:

```text
add consumers in same group
up to number of partitions
```

Scaling writes:

```text
increase partitions
spread keys
batch producer records
```

Scaling storage:

```text
segment files
retention cleanup
compression
replication
```

---

# Part 22 — Interview Explanation

## How does Kafka work internally?

Answer:

```text
Kafka stores messages in append-only partition logs.
A topic is split into partitions.
Each partition is an ordered log where every record gets a monotonically increasing offset.
Producers write to partitions, usually selected by key hash.
Consumers pull records from a given offset and maintain their own progress using committed offsets.
Messages are not deleted after consumption; they are deleted by retention.
Kafka is fast because it writes sequentially to disk, uses batching, OS page cache, and partition-level parallelism.
Replication provides durability and availability using leader-follower replicas.
```

## Why partition?

```text
Partition gives scalability and parallelism.
It is also the unit of ordering.
Kafka guarantees order only inside one partition.
```

## Why offset?

```text
Offset allows replay, resume, and independent consumer progress.
```

## Why append-only log?

```text
Sequential writes are fast and simple.
They avoid random disk updates.
They make replay and audit easy.
```

## Why consumer stores offset?

```text
Broker can keep data independent of consumers.
Multiple consumer groups can read same data at different speeds.
```

---

# Part 23 — What This Teaches For Distributed Systems

| MiniKafka concept | Distributed system lesson |
|---|---|
| append-only log | durability and replay |
| partition | horizontal scale |
| offset | progress tracking |
| batching | throughput optimization |
| retention | finite storage |
| consumer group | parallel processing |
| replication | high availability |
| pull model | backpressure |
| metrics | observability |
| segment/index | storage engine design |

This helps you understand:

```text
event-driven architecture
stream processing
audit logging
CDC
log aggregation
message durability
async microservices
consumer lag
backpressure
```

---

# Part 24 — Next Advanced Extensions

Add later:

```text
1. Segment files: log-000000.data, log-001000.data
2. Sparse offset index
3. CRC checksum per record
4. Producer batching API
5. Compression
6. Async flush thread
7. TCP client library
8. Netty broker
9. Replication follower thread
10. Leader election simulation
11. Consumer group rebalancing
12. Dead letter topic
13. Idempotent producer
14. Transaction-like writes
15. Prometheus metrics endpoint
16. Docker Compose multi-broker demo
17. k6 or custom load generator
18. JFR profiling
19. mmap/FileChannel implementation
20. Zero-copy sendfile experiment
```

---

# Final Mental Model

Kafka is not magic.

At its core:

```text
Kafka = partitioned durable append-only logs + consumer offsets + replication + retention
```

MiniKafka teaches:

```text
how messages are stored
how offsets work
why replay is possible
why partitions scale
why ordering is limited
why batching matters
why slow consumers do not delete data
why replication is needed
why consumer lag happens
```
