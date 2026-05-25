# MiniKafka From Scratch — Phase-by-Phase Java 21 Code Packs

This version fixes the previous problem:

```text
No monster class per phase.
No "add this method" incomplete patches.
Every phase has complete Java files + driver.
Every Java code block includes comments.
```

## Clickable Index

- [How To Use This File](#how-to-use-this-file)
- [Maven Setup](#maven-setup)
- [Phase 1 — Append-Only Log](#phase-1--append-only-log)
- [Phase 2 — Segment Rolling](#phase-2--segment-rolling)
- [Phase 3 — Offset Index](#phase-3--offset-index)
- [Phase 4 — Topic Partition Routing](#phase-4--topic-partition-routing)
- [Phase 5 — Broker + Producer API](#phase-5--broker--producer-api)
- [Phase 6 — Consumer API](#phase-6--consumer-api)
- [Phase 7 — Offset Manager](#phase-7--offset-manager)
- [Phase 8 — Batching Producer](#phase-8--batching-producer)
- [Phase 9 — Retention Manager](#phase-9--retention-manager)
- [Phase 10 — Backpressure Producer](#phase-10--backpressure-producer)
- [Phase 11 — Consumer Group Assignment](#phase-11--consumer-group-assignment)
- [Phase 12 — Rebalancing](#phase-12--rebalancing)
- [Phase 13 — Broker Facade](#phase-13--broker-facade)
- [Phase 14 — Replication Simulation](#phase-14--replication-simulation)
- [Phase 15 — Leader Election](#phase-15--leader-election)
- [Phase 16 — ISR and Ack Modes](#phase-16--isr-and-ack-modes)
- [Phase 17 — Zero-Copy File Transfer Demo](#phase-17--zero-copy-file-transfer-demo)
- [Phase 18 — Metrics](#phase-18--metrics)
- [Phase 19 — Load Test Driver](#phase-19--load-test-driver)
- [Phase 20 — Graceful Shutdown](#phase-20--graceful-shutdown)

---

# How To Use This File

Use one Git commit per phase:

```bash
git init
git add .
git commit -m "phase 1 append only log"
git commit -m "phase 2 segment rolling"
git commit -m "phase 3 offset index"
```

For each phase:

```text
1. Copy the files listed in that phase.
2. Run mvn clean compile.
3. Run the phase driver.
4. Understand the output.
5. Move to the next phase.
```

Important rule:

```text
Each phase gives complete files.
Do not copy only a method.
Do not create one giant class.
```

---

# Maven Setup

Create `pom.xml` once.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.minikafka</groupId>
    <artifactId>mini-kafka</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.release>21</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
</project>
```

Run pattern:

```bash
mvn clean compile
java -cp target/classes com.minikafka.Phase1AppendOnlyLogDriver
```

---

# Phase 1 — Append-Only Log

## What we build

```text
One durable file.
Every message is appended at the end.
Consumer can read from an offset.
```

## Files

```text
src/main/java/com/minikafka/common/MessageRecord.java
src/main/java/com/minikafka/storage/LogSegment.java
src/main/java/com/minikafka/Phase1AppendOnlyLogDriver.java
```

## MessageRecord.java

```java
package com.minikafka.common;

/**
 * Immutable record stored inside the log.
 *
 * offset          = position of the message inside one log.
 * timestampMillis = append time.
 * key             = routing/ordering key.
 * value           = actual event payload.
 */
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

/**
 * Phase 1 storage object.
 *
 * This class owns exactly one log file.
 * It supports:
 * 1. append record
 * 2. read records from offset
 * 3. recover next offset after restart
 */
public class LogSegment {
    private final File file;

    // Next offset to assign when a new message is appended.
    private long nextOffset;

    public LogSegment(String filePath) throws IOException {
        this.file = new File(filePath);

        // Create parent directory like data/phase1 if it does not exist.
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        // Create empty log file if this is the first run.
        if (!file.exists()) {
            file.createNewFile();
        }

        // On restart, scan existing records and continue from the next offset.
        this.nextOffset = recoverNextOffset();
    }

    /**
     * Append a new key-value message to the end of the file.
     */
    public synchronized long append(String key, String value) throws IOException {
        long offset = nextOffset++;

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {

            // Simple text format for learning:
            // offset|timestamp|key|value
            writer.write(offset + "|" + System.currentTimeMillis() + "|" + key + "|" + value);
            writer.newLine();
        }

        return offset;
    }

    /**
     * Read all records whose offset >= startOffset.
     */
    public synchronized List<MessageRecord> readFrom(long startOffset) throws IOException {
        List<MessageRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 4);

                if (parts.length == 4) {
                    long offset = Long.parseLong(parts[0]);

                    if (offset >= startOffset) {
                        records.add(new MessageRecord(
                                offset,
                                Long.parseLong(parts[1]),
                                parts[2],
                                parts[3]
                        ));
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

    /**
     * Recover offset by scanning the file.
     *
     * Example:
     * last record offset = 9
     * next offset must be 10
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

## Phase1AppendOnlyLogDriver.java

```java
package com.minikafka;

import com.minikafka.common.MessageRecord;
import com.minikafka.storage.LogSegment;

/**
 * Driver for Phase 1.
 *
 * Run:
 * java -cp target/classes com.minikafka.Phase1AppendOnlyLogDriver
 */
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

## Expected output

```text
offset1=0
offset2=1
MessageRecord[offset=0, ... key=order-1, value=created]
MessageRecord[offset=1, ... key=order-2, value=paid]
```

---

# Phase 2 — Segment Rolling

## What we build

```text
Phase 1: one file grows forever.
Phase 2: one partition has many smaller segment files.
```

## Files

```text
src/main/java/com/minikafka/common/MessageRecord.java
src/main/java/com/minikafka/storage/LogSegment.java
src/main/java/com/minikafka/storage/PartitionLog.java
src/main/java/com/minikafka/Phase2SegmentRollingDriver.java
```

Use the same `MessageRecord.java` from Phase 1.

## LogSegment.java

```java
package com.minikafka.storage;

import com.minikafka.common.MessageRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * One segment file.
 *
 * Phase 2 keeps this class small:
 * it only knows how to append/read one physical file.
 */
public class LogSegment {
    private final File file;
    private final long baseOffset;
    private long nextOffset;

    public LogSegment(String filePath, long baseOffset) throws IOException {
        this.file = new File(filePath);
        this.baseOffset = baseOffset;

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

    public synchronized List<MessageRecord> readFrom(long startOffset) throws IOException {
        List<MessageRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

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

    public long baseOffset() {
        return baseOffset;
    }

    public long nextOffset() {
        return nextOffset;
    }

    private long recoverNextOffset() throws IOException {
        long maxOffset = baseOffset - 1;

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

## PartitionLog.java

```java
package com.minikafka.storage;

import com.minikafka.common.MessageRecord;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A partition is a sequence of segment files.
 *
 * This class decides WHEN to roll to a new segment.
 * It does not know about topics/producers/consumers.
 */
public class PartitionLog {
    private final File dir;
    private final long maxSegmentBytes;
    private final List<LogSegment> segments = new ArrayList<>();

    private LogSegment activeSegment;

    public PartitionLog(String dirPath, long maxSegmentBytes) throws IOException {
        this.dir = new File(dirPath);
        this.maxSegmentBytes = maxSegmentBytes;

        if (!dir.exists()) {
            dir.mkdirs();
        }

        loadOrCreateSegments();
    }

    /**
     * Append to the current active segment.
     * Roll first if the active segment is already too large.
     */
    public synchronized long append(String key, String value) throws IOException {
        if (activeSegment.file().length() >= maxSegmentBytes) {
            rollSegment();
        }

        return activeSegment.append(key, value);
    }

    /**
     * Read from all segments.
     * Later Phase 3 will optimize this using indexes.
     */
    public synchronized List<MessageRecord> readFrom(long startOffset) throws IOException {
        List<MessageRecord> result = new ArrayList<>();

        for (LogSegment segment : segments) {
            result.addAll(segment.readFrom(startOffset));
        }

        return result;
    }

    public int segmentCount() {
        return segments.size();
    }

    public void printSegments() {
        for (LogSegment segment : segments) {
            System.out.println(
                    "baseOffset=" + segment.baseOffset()
                            + ", file=" + segment.file().getName()
                            + ", size=" + segment.file().length()
            );
        }
    }

    private void loadOrCreateSegments() throws IOException {
        File[] files = dir.listFiles((d, name) -> name.endsWith(".log"));

        if (files == null || files.length == 0) {
            activeSegment = new LogSegment(segmentPath(0), 0);
            segments.add(activeSegment);
            return;
        }

        List<File> sorted = new ArrayList<>(List.of(files));
        sorted.sort(Comparator.comparing(File::getName));

        for (File file : sorted) {
            long baseOffset = parseBaseOffset(file.getName());
            segments.add(new LogSegment(file.getPath(), baseOffset));
        }

        activeSegment = segments.get(segments.size() - 1);
    }

    /**
     * New segment starts at the next offset of the old active segment.
     */
    private void rollSegment() throws IOException {
        long newBaseOffset = activeSegment.nextOffset();

        activeSegment = new LogSegment(segmentPath(newBaseOffset), newBaseOffset);
        segments.add(activeSegment);
    }

    private String segmentPath(long baseOffset) {
        return new File(dir, String.format("%020d.log", baseOffset)).getPath();
    }

    private long parseBaseOffset(String fileName) {
        return Long.parseLong(fileName.replace(".log", ""));
    }
}
```

## Phase2SegmentRollingDriver.java

```java
package com.minikafka;

import com.minikafka.storage.PartitionLog;

/**
 * Driver for segment rolling.
 *
 * We use a very small maxSegmentBytes so rolling happens quickly.
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

## Expected output

```text
baseOffset=0, file=00000000000000000000.log, size=...
baseOffset=4, file=00000000000000000004.log, size=...
segmentCount=...
```

---

# Phase 3 — Offset Index

## What we build

```text
offset -> byte position
```

This avoids always scanning from the beginning.

## Files

```text
src/main/java/com/minikafka/common/MessageRecord.java
src/main/java/com/minikafka/storage/OffsetIndex.java
src/main/java/com/minikafka/storage/IndexedLogSegment.java
src/main/java/com/minikafka/Phase3OffsetIndexDriver.java
```

Use the same `MessageRecord.java` from Phase 1.

## OffsetIndex.java

```java
package com.minikafka.storage;

import java.io.*;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Small offset index.
 *
 * Key   = message offset.
 * Value = byte position in the log file.
 */
public class OffsetIndex {
    private final File file;

    // TreeMap allows floorEntry(offset), which is useful for sparse indexes.
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

    /**
     * Store new index entry in memory and append it to disk.
     */
    public synchronized void append(long offset, long position) throws IOException {
        offsetToPosition.put(offset, position);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(offset + "," + position);
            writer.newLine();
        }
    }

    /**
     * Return exact position if present.
     * If exact offset is missing, return nearest lower offset position.
     */
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

## IndexedLogSegment.java

```java
package com.minikafka.storage;

import com.minikafka.common.MessageRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Log segment with an offset index.
 *
 * This class is still only one physical segment.
 * The new thing is RandomAccessFile seek using OffsetIndex.
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

        // Capture file size before writing.
        // That is the byte position where this record starts.
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
            // Jump near the offset instead of scanning from byte 0.
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

## Phase3OffsetIndexDriver.java

```java
package com.minikafka;

import com.minikafka.storage.IndexedLogSegment;

/**
 * Driver for offset index.
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

---

# Phase 4 — Topic Partition Routing

## Files

```text
src/main/java/com/minikafka/common/TopicPartition.java
src/main/java/com/minikafka/partition/PartitionRouter.java
src/main/java/com/minikafka/Phase4PartitionRouterDriver.java
```

## TopicPartition.java

```java
package com.minikafka.common;

/**
 * Identifies one physical partition of one logical topic.
 *
 * Example:
 * topic = orders
 * partition = 2
 * id = orders-2
 */
public record TopicPartition(String topic, int partition) {
    public String id() {
        return topic + "-" + partition;
    }
}
```

## PartitionRouter.java

```java
package com.minikafka.partition;

/**
 * Decides which partition receives a message.
 *
 * Same key should map to the same partition.
 * That gives ordering per key.
 */
public class PartitionRouter {
    public int route(String key, int partitionCount) {
        if (partitionCount <= 0) {
            throw new IllegalArgumentException("partitionCount must be positive");
        }

        // Null key goes to partition 0 in this simple version.
        if (key == null) {
            return 0;
        }

        // floorMod protects us from negative hash values.
        return Math.floorMod(key.hashCode(), partitionCount);
    }
}
```

## Phase4PartitionRouterDriver.java

```java
package com.minikafka;

import com.minikafka.partition.PartitionRouter;

/**
 * Driver for key-based partition routing.
 */
public class Phase4PartitionRouterDriver {
    public static void main(String[] args) {
        PartitionRouter router = new PartitionRouter();

        for (int i = 1; i <= 10; i++) {
            String key = "user-" + i;
            System.out.println(key + " -> partition-" + router.route(key, 3));
        }

        System.out.println("same-key-test-1=" + router.route("user-1", 3));
        System.out.println("same-key-test-2=" + router.route("user-1", 3));
    }
}
```

---

# Phase 5 — Broker + Producer API

## What we build

```text
Producer sends to broker.
Broker routes key to partition.
Partition stores record.
```

## Files

```text
src/main/java/com/minikafka/common/MessageRecord.java
src/main/java/com/minikafka/storage/LogSegment.java
src/main/java/com/minikafka/partition/PartitionRouter.java
src/main/java/com/minikafka/broker/MiniKafkaBroker.java
src/main/java/com/minikafka/producer/MiniProducer.java
src/main/java/com/minikafka/Phase5ProducerDriver.java
```

Use `MessageRecord.java`, `LogSegment.java`, and `PartitionRouter.java` from previous phases.

## MiniKafkaBroker.java

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
 * Small broker facade for Phase 5.
 *
 * Responsibility:
 * 1. create topic
 * 2. hold partition logs
 * 3. route producer message to correct partition
 */
public class MiniKafkaBroker {
    private final Map<String, List<LogSegment>> topics = new ConcurrentHashMap<>();
    private final PartitionRouter router = new PartitionRouter();

    public void createTopic(String topic, int partitions) throws IOException {
        if (partitions <= 0) {
            throw new IllegalArgumentException("partitions must be positive");
        }

        List<LogSegment> logs = new ArrayList<>();

        for (int p = 0; p < partitions; p++) {
            // Each partition is one log file in this phase.
            logs.add(new LogSegment("data/phase5/" + topic + "-" + p + ".log"));
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
}
```

## MiniProducer.java

```java
package com.minikafka.producer;

import com.minikafka.broker.MiniKafkaBroker;

/**
 * Producer API exposed to application code.
 *
 * In real Kafka this class would also handle:
 * batching, retry, compression, metadata, and acknowledgements.
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

## Phase5ProducerDriver.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.producer.MiniProducer;

/**
 * Driver for producer API.
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

---

# Phase 6 — Consumer API

## What we build

```text
Consumer reads from topic + partition + offset.
Messages are not deleted after read.
```

## Files

```text
src/main/java/com/minikafka/common/MessageRecord.java
src/main/java/com/minikafka/storage/LogSegment.java
src/main/java/com/minikafka/partition/PartitionRouter.java
src/main/java/com/minikafka/consumer/ConsumerRecord.java
src/main/java/com/minikafka/consumer/MiniConsumer.java
src/main/java/com/minikafka/broker/MiniKafkaBroker.java
src/main/java/com/minikafka/producer/MiniProducer.java
src/main/java/com/minikafka/Phase6ProducerConsumerDriver.java
```

Use `MessageRecord.java`, `LogSegment.java`, `PartitionRouter.java`, and `MiniProducer.java` from previous phases.

## ConsumerRecord.java

```java
package com.minikafka.consumer;

/**
 * Record returned to consumers.
 *
 * It includes topic and partition because the same consumer
 * can read from many topic-partitions.
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

## MiniConsumer.java

```java
package com.minikafka.consumer;

import com.minikafka.broker.MiniKafkaBroker;

import java.util.List;

/**
 * Consumer API exposed to application code.
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

## MiniKafkaBroker.java

```java
package com.minikafka.broker;

import com.minikafka.consumer.ConsumerRecord;
import com.minikafka.partition.PartitionRouter;
import com.minikafka.storage.LogSegment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Complete Phase 6 broker.
 *
 * This is NOT a monster class.
 * It has only broker-level responsibilities:
 * create topic, produce, consume.
 */
public class MiniKafkaBroker {
    private final Map<String, List<LogSegment>> topics = new ConcurrentHashMap<>();
    private final PartitionRouter router = new PartitionRouter();

    public void createTopic(String topic, int partitions) throws IOException {
        if (partitions <= 0) {
            throw new IllegalArgumentException("partitions must be positive");
        }

        List<LogSegment> logs = new ArrayList<>();

        for (int p = 0; p < partitions; p++) {
            logs.add(new LogSegment("data/phase6/" + topic + "-" + p + ".log"));
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

        if (partition < 0 || partition >= partitions.size()) {
            throw new IllegalArgumentException("Invalid partition: " + partition);
        }

        List<ConsumerRecord> result = new ArrayList<>();

        for (var record : partitions.get(partition).readFrom(offset)) {
            result.add(new ConsumerRecord(
                    topic,
                    partition,
                    record.offset(),
                    record.key(),
                    record.value()
            ));
        }

        return result;
    }
}
```

## Phase6ProducerConsumerDriver.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.consumer.MiniConsumer;
import com.minikafka.producer.MiniProducer;

/**
 * Driver showing producer and consumer together.
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

        // Read all partitions because keys may be routed to different partitions.
        for (int partition = 0; partition < 3; partition++) {
            consumer.poll("orders", partition, 0).forEach(System.out::println);
        }
    }
}
```

---

# Phase 7 — Offset Manager

## Files

```text
src/main/java/com/minikafka/coordinator/OffsetManager.java
src/main/java/com/minikafka/Phase7OffsetManagerDriver.java
```

## OffsetManager.java

```java
package com.minikafka.coordinator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks committed offsets per consumer group and topic-partition.
 *
 * If a consumer crashes, it can resume from committedOffset.
 */
public class OffsetManager {
    private final Map<String, Long> committedOffsets = new ConcurrentHashMap<>();

    /**
     * Store next offset to read, not the last processed offset.
     */
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

## Phase7OffsetManagerDriver.java

```java
package com.minikafka;

import com.minikafka.coordinator.OffsetManager;

/**
 * Driver for offset commit/resume.
 */
public class Phase7OffsetManagerDriver {
    public static void main(String[] args) {
        OffsetManager offsets = new OffsetManager();

        offsets.commit("order-service", "orders", 0, 5);

        System.out.println("resumeFrom=" + offsets.committedOffset("order-service", "orders", 0));
    }
}
```

---

# Phase 8 — Batching Producer

## Files

```text
src/main/java/com/minikafka/producer/ProducerRecord.java
src/main/java/com/minikafka/producer/BatchingMiniProducer.java
src/main/java/com/minikafka/Phase8BatchingDriver.java
```

This phase uses `MiniKafkaBroker` from Phase 6.

## ProducerRecord.java

```java
package com.minikafka.producer;

/**
 * Record waiting inside producer buffer before flush.
 */
public record ProducerRecord(String topic, String key, String value) {
}
```

## BatchingMiniProducer.java

```java
package com.minikafka.producer;

import com.minikafka.broker.MiniKafkaBroker;

import java.util.ArrayList;
import java.util.List;

/**
 * Batching producer.
 *
 * Instead of writing immediately, it buffers records.
 * When batchSize is reached, it flushes to broker.
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

    public synchronized int bufferedCount() {
        return buffer.size();
    }
}
```

## Phase8BatchingDriver.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.producer.BatchingMiniProducer;

/**
 * Driver for batching producer.
 */
public class Phase8BatchingDriver {
    public static void main(String[] args) throws Exception {
        MiniKafkaBroker broker = new MiniKafkaBroker();
        broker.createTopic("orders", 3);

        BatchingMiniProducer producer = new BatchingMiniProducer(broker, 5);

        for (int i = 1; i <= 12; i++) {
            producer.send("orders", "order-" + i, "event-" + i);
        }

        // Flush remaining records not reaching batch size.
        producer.flush();

        System.out.println("bufferedAfterFlush=" + producer.bufferedCount());
    }
}
```

---

# Phase 9 — Retention Manager

## Files

```text
src/main/java/com/minikafka/storage/RetentionManager.java
src/main/java/com/minikafka/Phase9RetentionDriver.java
```

## RetentionManager.java

```java
package com.minikafka.storage;

import java.io.File;

/**
 * Deletes old segment files.
 *
 * Real Kafka never deletes active segments.
 * This learning version keeps the rule simple.
 */
public class RetentionManager {
    private final long retentionMillis;

    public RetentionManager(long retentionMillis) {
        this.retentionMillis = retentionMillis;
    }

    public int cleanup(File partitionDir) {
        File[] files = partitionDir.listFiles((dir, name) ->
                name.endsWith(".log") || name.endsWith(".index")
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

## Phase9RetentionDriver.java

```java
package com.minikafka;

import com.minikafka.storage.RetentionManager;

import java.io.File;

/**
 * Driver for retention cleanup.
 *
 * Run Phase 2 first to create segment files.
 */
public class Phase9RetentionDriver {
    public static void main(String[] args) {
        RetentionManager retention = new RetentionManager(1);

        int deleted = retention.cleanup(new File("data/phase2/orders-0"));

        System.out.println("deletedFiles=" + deleted);
    }
}
```

---

# Phase 10 — Backpressure Producer

## Files

```text
src/main/java/com/minikafka/producer/ProducerRecord.java
src/main/java/com/minikafka/producer/BackpressureProducer.java
src/main/java/com/minikafka/Phase10BackpressureDriver.java
```

This phase uses `MiniKafkaBroker` from Phase 6.

## BackpressureProducer.java

```java
package com.minikafka.producer;

import com.minikafka.broker.MiniKafkaBroker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Producer with bounded queue.
 *
 * If queue is full, trySend returns false.
 * This protects memory from unbounded growth.
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
     * true  = accepted into buffer
     * false = rejected because queue is full
     */
    public boolean trySend(String topic, String key, String value) {
        return queue.offer(new ProducerRecord(topic, key, value));
    }

    /**
     * Simulates background sender thread draining buffer to broker.
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

## Phase10BackpressureDriver.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.producer.BackpressureProducer;

/**
 * Driver for backpressure.
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

---

# Phase 11 — Consumer Group Assignment

## Files

```text
src/main/java/com/minikafka/coordinator/ConsumerGroupCoordinator.java
src/main/java/com/minikafka/Phase11ConsumerGroupDriver.java
```

## ConsumerGroupCoordinator.java

```java
package com.minikafka.coordinator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Assigns partitions to consumers in the same group.
 *
 * Rule:
 * one partition is owned by only one consumer in the same group.
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

        for (int partition = 0; partition < partitionCount; partition++) {
            String owner = consumerIds.get(partition % consumerIds.size());
            assignment.get(owner).add(partition);
        }

        return assignment;
    }
}
```

## Phase11ConsumerGroupDriver.java

```java
package com.minikafka;

import com.minikafka.coordinator.ConsumerGroupCoordinator;

import java.util.List;

/**
 * Driver for consumer group assignment.
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

---

# Phase 12 — Rebalancing

## What we build

```text
Same coordinator.
Different membership.
New assignment.
```

## Files

```text
src/main/java/com/minikafka/coordinator/ConsumerGroupCoordinator.java
src/main/java/com/minikafka/Phase12RebalanceDriver.java
```

Use `ConsumerGroupCoordinator.java` from Phase 11.

## Phase12RebalanceDriver.java

```java
package com.minikafka;

import com.minikafka.coordinator.ConsumerGroupCoordinator;

import java.util.List;

/**
 * Driver for rebalance simulation.
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

---

# Phase 13 — Broker Facade

## What we build

```text
Same small classes.
Broker is the single facade used by Producer and Consumer.
```

## Files

```text
Use Phase 6 files.
Add:
src/main/java/com/minikafka/Phase13BrokerFacadeDriver.java
```

## Phase13BrokerFacadeDriver.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.consumer.MiniConsumer;
import com.minikafka.producer.MiniProducer;

/**
 * Driver showing the broker as the main facade.
 */
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

# Phase 14 — Replication Simulation

## Files

```text
src/main/java/com/minikafka/replication/BrokerNode.java
src/main/java/com/minikafka/replication/ReplicaState.java
src/main/java/com/minikafka/replication/ReplicationManager.java
src/main/java/com/minikafka/Phase14ReplicationDriver.java
```

## BrokerNode.java

```java
package com.minikafka.replication;

/**
 * Represents one broker in the cluster.
 */
public record BrokerNode(String brokerId, String host, int port) {
}
```

## ReplicaState.java

```java
package com.minikafka.replication;

/**
 * Replica role for one partition.
 */
public enum ReplicaState {
    LEADER,
    FOLLOWER
}
```

## ReplicationManager.java

```java
package com.minikafka.replication;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Tracks replicas and ISR.
 *
 * ISR = in-sync replicas.
 * Only ISR replicas are safe for leader election.
 */
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
        if (replicas.contains(brokerId)) {
            inSyncReplicas.add(brokerId);
        }
    }

    public Set<String> isr() {
        return Set.copyOf(inSyncReplicas);
    }
}
```

## Phase14ReplicationDriver.java

```java
package com.minikafka;

import com.minikafka.replication.ReplicationManager;

/**
 * Driver for ISR simulation.
 */
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

# Phase 15 — Leader Election

## Files

```text
src/main/java/com/minikafka/replication/LeaderElection.java
src/main/java/com/minikafka/Phase15LeaderElectionDriver.java
```

## LeaderElection.java

```java
package com.minikafka.replication;

import java.util.Set;

/**
 * Elects a leader from ISR.
 *
 * Simple deterministic rule:
 * pick lexicographically smallest broker id.
 */
public class LeaderElection {
    public String electLeader(Set<String> inSyncReplicas) {
        return inSyncReplicas.stream()
                .sorted()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No ISR available for leader election"));
    }
}
```

## Phase15LeaderElectionDriver.java

```java
package com.minikafka;

import com.minikafka.replication.LeaderElection;

import java.util.Set;

/**
 * Driver for leader election.
 */
public class Phase15LeaderElectionDriver {
    public static void main(String[] args) {
        LeaderElection election = new LeaderElection();

        String leader = election.electLeader(Set.of("broker-2", "broker-3"));

        System.out.println("newLeader=" + leader);
    }
}
```

---

# Phase 16 — ISR and Ack Modes

## Files

```text
src/main/java/com/minikafka/producer/AckMode.java
src/main/java/com/minikafka/replication/AckDecider.java
src/main/java/com/minikafka/Phase16AckModeDriver.java
```

## AckMode.java

```java
package com.minikafka.producer;

/**
 * Producer durability mode.
 */
public enum AckMode {
    NONE,   // producer does not wait
    LEADER, // wait only for leader append
    ALL     // wait for required ISR replicas
}
```

## AckDecider.java

```java
package com.minikafka.replication;

import com.minikafka.producer.AckMode;

/**
 * Decides whether producer can receive success acknowledgement.
 */
public class AckDecider {
    public boolean canAck(AckMode ackMode, boolean leaderWritten, int isrWritten, int minInsyncReplicas) {
        return switch (ackMode) {
            case NONE -> true;
            case LEADER -> leaderWritten;
            case ALL -> leaderWritten && isrWritten >= minInsyncReplicas;
        };
    }
}
```

## Phase16AckModeDriver.java

```java
package com.minikafka;

import com.minikafka.producer.AckMode;
import com.minikafka.replication.AckDecider;

/**
 * Driver for ack decision.
 */
public class Phase16AckModeDriver {
    public static void main(String[] args) {
        AckDecider decider = new AckDecider();

        System.out.println("acks=NONE  -> " + decider.canAck(AckMode.NONE, false, 0, 2));
        System.out.println("acks=LEADER -> " + decider.canAck(AckMode.LEADER, true, 1, 2));
        System.out.println("acks=ALL ok -> " + decider.canAck(AckMode.ALL, true, 2, 2));
        System.out.println("acks=ALL bad -> " + decider.canAck(AckMode.ALL, true, 1, 2));
    }
}
```

---

# Phase 17 — Zero-Copy File Transfer Demo

## Files

```text
src/main/java/com/minikafka/storage/ZeroCopyFileSender.java
src/main/java/com/minikafka/Phase17ZeroCopyDriver.java
```

## ZeroCopyFileSender.java

```java
package com.minikafka.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Demonstrates Java FileChannel.transferTo.
 *
 * Real Kafka uses zero-copy ideas to avoid copying data
 * from kernel page cache into JVM heap before sending to socket.
 */
public class ZeroCopyFileSender {
    public long copyFile(File source, File target) throws Exception {
        try (
                FileChannel in = FileChannel.open(source.toPath());
                FileChannel out = new FileOutputStream(target).getChannel()
        ) {
            long size = in.size();

            // transferTo asks OS to copy from file channel to another channel.
            return in.transferTo(0, size, out);
        }
    }
}
```

## Phase17ZeroCopyDriver.java

```java
package com.minikafka;

import com.minikafka.storage.ZeroCopyFileSender;

import java.io.File;
import java.nio.file.Files;

/**
 * Driver for zero-copy demo.
 */
public class Phase17ZeroCopyDriver {
    public static void main(String[] args) throws Exception {
        File dir = new File("data/phase17");
        dir.mkdirs();

        File source = new File(dir, "source.log");
        File target = new File(dir, "target.log");

        Files.writeString(source.toPath(), "event-1\nevent-2\nevent-3\n");

        ZeroCopyFileSender sender = new ZeroCopyFileSender();
        long bytes = sender.copyFile(source, target);

        System.out.println("bytesCopied=" + bytes);
        System.out.println("targetContent=");
        System.out.println(Files.readString(target.toPath()));
    }
}
```

---

# Phase 18 — Metrics

## Files

```text
src/main/java/com/minikafka/metrics/MetricsRegistry.java
src/main/java/com/minikafka/Phase18MetricsDriver.java
```

## MetricsRegistry.java

```java
package com.minikafka.metrics;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe metric counters.
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
        return "messagesIn=" + messagesIn.get()
                + ", messagesOut=" + messagesOut.get()
                + ", bytesIn=" + bytesIn.get()
                + ", bytesOut=" + bytesOut.get()
                + ", rejected=" + rejectedMessages.get();
    }
}
```

## Phase18MetricsDriver.java

```java
package com.minikafka;

import com.minikafka.metrics.MetricsRegistry;

/**
 * Driver for metrics.
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

---

# Phase 19 — Load Test Driver

## Files

```text
Use Phase 6 broker/producer files.
Add:
src/main/java/com/minikafka/Phase19LoadTestDriver.java
```

## Phase19LoadTestDriver.java

```java
package com.minikafka;

import com.minikafka.broker.MiniKafkaBroker;
import com.minikafka.producer.MiniProducer;

/**
 * Simple local throughput test.
 *
 * This is not a scientific benchmark.
 * It gives first intuition about write throughput.
 */
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

# Phase 20 — Graceful Shutdown

## Files

```text
src/main/java/com/minikafka/broker/BrokerLifecycle.java
src/main/java/com/minikafka/Phase20GracefulShutdownDriver.java
```

## BrokerLifecycle.java

```java
package com.minikafka.broker;

/**
 * Minimal lifecycle object.
 *
 * Later this can close files, flush buffers,
 * stop background threads, and expose health status.
 */
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

## Phase20GracefulShutdownDriver.java

```java
package com.minikafka;

import com.minikafka.broker.BrokerLifecycle;

/**
 * Driver for graceful shutdown.
 */
public class Phase20GracefulShutdownDriver {
    public static void main(String[] args) {
        BrokerLifecycle lifecycle = new BrokerLifecycle();

        lifecycle.start();

        // This hook runs when JVM is stopped by Ctrl+C or normal shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(lifecycle::stop));

        System.out.println("running=" + lifecycle.isRunning());

        // Explicit stop for demo.
        lifecycle.stop();
    }
}
```

---

# Final Development Rule

Do not build MiniKafka as one giant class.

Use this mental model:

```text
MessageRecord          = data model
LogSegment             = one physical file
PartitionLog           = many segments
OffsetIndex            = offset -> byte position
PartitionRouter        = key -> partition
MiniKafkaBroker        = facade over topics and partitions
MiniProducer           = write API
MiniConsumer           = read API
OffsetManager          = consumer progress
ConsumerGroupCoordinator = partition ownership
ReplicationManager     = ISR state
LeaderElection         = choose leader from ISR
AckDecider             = durability decision
MetricsRegistry        = observability
```

This is the same way production systems are easier to understand:

```text
small objects
clear responsibility
driver per phase
measure and evolve
```
