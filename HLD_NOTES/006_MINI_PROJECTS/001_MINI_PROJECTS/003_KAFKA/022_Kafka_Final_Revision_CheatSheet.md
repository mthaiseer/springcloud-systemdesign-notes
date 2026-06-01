# 099_Kafka_Final_Revision_CheatSheet.md

# MiniKafka Final Revision CheatSheet

## 0. One-Line Mental Model

```text
Kafka = distributed append-only commit log
```

Not just a queue.

Traditional queue:

```text
Producer -> Queue -> Consumer
message removed after consume
```

Kafka:

```text
Producer -> Topic -> Partition Log -> Consumer reads by offset
message remains until retention deletes it
```

Most important idea:

```text
Kafka scales by partitioning logs.
Kafka tracks progress by offsets.
Kafka survives failures by replication.
Kafka deletes old data by retention.
```

---

# 1. Final MiniKafka Architecture

```text
Producer
   |
   v
Broker
   |
   v
Topic
   |
   +--> Partition 0 --> Log Segments --> Index File
   |
   +--> Partition 1 --> Log Segments --> Index File
   |
   +--> Partition 2 --> Log Segments --> Index File
   |
   v
Consumer Group
   |
   +--> Consumer A reads Partition 0
   +--> Consumer B reads Partition 1
   +--> Consumer C reads Partition 2
```

Production-level version:

```text
Producer
   |
   v
MiniKafka Broker
   |
   +--> TopicManager
   +--> PartitionManager
   +--> LogStorageEngine
   +--> SegmentManager
   +--> OffsetIndex
   +--> RetentionManager
   +--> ConsumerGroupCoordinator
   +--> ReplicationManager
   +--> MetricsRegistry
```

---

# 2. Core Vocabulary

| Concept | Meaning |
|---|---|
| Topic | Logical stream of events, e.g. `orders`, `payments`, `logs` |
| Partition | Physical append-only log inside a topic |
| Offset | Position of a record inside one partition |
| Producer | Writes records |
| Consumer | Reads records |
| Broker | Server storing topic partitions |
| Consumer Group | Multiple consumers sharing partitions |
| Segment | Smaller file chunk of a partition log |
| Index File | Maps offset to byte position |
| Retention | Deletes old segment files |
| Replication | Copies partition to follower brokers |
| Leader | Broker handling writes for a partition |
| Follower | Replica copying leader data |
| ISR | In-sync replicas |
| Ack Mode | Producer durability setting |

---

# 3. Phase-by-Phase Last-Minute Summary

## Phase 1 — Create Log File

Build one physical file:

```text
data/phase1/orders-0.log
```

Meaning:

```text
topic = orders
partition = 0
```

Why?

```text
Kafka stores messages in partition logs.
```

Mental model:

```text
orders-0.log
(empty first)
```

---

## Phase 2 — Append Records To Log File

Append records sequentially:

```text
0|timestamp|order-1|created
1|timestamp|order-2|paid
2|timestamp|order-3|shipped
```

Important:

```text
Append-only write = fast sequential IO
```

Offset increases monotonically:

```text
nextOffset = maxOffset + 1
```

---

## Phase 3 — MessageRecord Object

Convert raw line into object:

```java
record MessageRecord(
    long offset,
    long timestampMillis,
    String key,
    String value
) {}
```

Why?

```text
Raw string is storage format.
MessageRecord is application format.
```

---

## Phase 4 — LogSegment Abstraction

Encapsulate file operations:

```text
LogSegment
  -> append()
  -> readAll()
  -> recoverNextOffset()
```

Why?

```text
Do not spread file IO logic across broker/producer/consumer code.
```

---

## Phase 5 — Read From Specific Offset

Consumer reads from offset:

```text
readFrom(5)
```

Without index:

```text
scan file from beginning until offset >= 5
```

This is simple but slow for huge logs.

---

## Phase 6 — Partition Abstraction

Partition = one ordered log.

```text
Topic: orders
Partition: 0
Log: orders-0.log
```

Guarantee:

```text
Ordering is guaranteed only inside one partition.
```

---

## Phase 7 — Topic Abstraction

Topic manages multiple partitions:

```text
orders
  -> partition-0
  -> partition-1
  -> partition-2
```

Why?

```text
One topic can scale horizontally through partitions.
```

---

## Phase 8 — Multiple Partitions

Multiple partitions give:

```text
parallel writes
parallel reads
higher throughput
```

But:

```text
global ordering across all partitions is not guaranteed
```

Only:

```text
ordering inside same partition
```

---

## Phase 9 — Key Based Partition Routing

Routing formula:

```java
partition = Math.floorMod(key.hashCode(), partitionCount);
```

Same key goes to same partition:

```text
key=user-1 -> partition 2
key=user-1 -> partition 2
```

Why important?

```text
Keeps ordering for same business entity.
```

Example:

```text
all events for order-123 go to same partition
```

---

## Phase 10 — Broker API

Broker manages:

```text
topics
partitions
produce requests
consume requests
```

Mental model:

```text
Producer/Consumer do not write files directly.
They talk to Broker.
```

---

## Phase 11 — Producer API

Producer hides broker details:

```text
producer.send(topic, key, value)
```

Flow:

```text
Producer
  -> choose partition
  -> send to broker
  -> broker appends to partition log
  -> returns offset
```

---

## Phase 12 — Consumer API

Consumer reads messages:

```text
consumer.poll(topic, partition, offset)
```

Flow:

```text
Consumer
  -> asks broker for records from offset
  -> broker reads log
  -> returns records
```

Kafka does not push messages by default; consumers pull.

---

## Phase 13 — Consumer Offset Commit

Offset commit means:

```text
I processed records up to this offset.
```

Example:

```text
consumer processed offset 0,1,2
commits offset 3 as next read position
```

Why?

```text
After restart, consumer resumes from committed offset.
```

Important failure cases:

```text
Process then commit -> possible duplicate after crash
Commit then process -> possible data loss after crash
```

Safe general rule:

```text
process message successfully
then commit offset
```

This gives at-least-once semantics.

---

## Phase 14 — Consumer Groups

Consumer group lets multiple consumers share work.

```text
Group: payment-service
  Consumer A -> partition 0
  Consumer B -> partition 1
  Consumer C -> partition 2
```

Rule:

```text
One partition can be consumed by only one consumer in same group at a time.
```

But:

```text
Different groups can independently read same topic.
```

---

## Phase 15 — Partition Assignment

Assignment decides:

```text
which consumer owns which partition
```

Example:

```text
3 partitions, 2 consumers

Consumer A -> P0, P2
Consumer B -> P1
```

Goal:

```text
balance load
avoid duplicate ownership
```

---

## Phase 16 — Rebalancing Basics

Rebalance happens when:

```text
consumer joins
consumer leaves
consumer crashes
partition count changes
```

Flow:

```text
pause consumption
revoke partitions
reassign partitions
resume consumption
```

Problem:

```text
rebalance temporarily pauses processing
```

Production concern:

```text
too many rebalances = unstable system
```

---

## Phase 17 — Segment Rolling

A partition log is split into smaller segment files:

```text
orders-0/
  00000000000000000000.log
  00000000000000001000.log
  00000000000000002000.log
```

Why?

```text
bounded file size
faster recovery
easy deletion
retention works at segment level
```

Rolling rule:

```text
if activeSegment.size >= maxSegmentBytes
create new segment
```

---

## Phase 18 — Index File

Index maps:

```text
offset -> byte position
```

Example:

```text
0   -> 0
100 -> 4096
200 -> 8192
```

Without index:

```text
read offset 200
scan from beginning
```

With index:

```text
read offset 200
seek to byte position 8192
read forward
```

This is why Kafka can read from large logs efficiently.

---

## Phase 19 — Retention Cleanup

Kafka does not delete messages when consumed.

It deletes based on retention:

```text
time-based retention
size-based retention
```

Example:

```text
delete segments older than 7 days
delete segments if total size > limit
```

Important:

```text
Retention deletes closed old segments, not individual records.
```

---

## Phase 20 — Replication Basics

Replication copies partition data to multiple brokers.

```text
Broker 1: leader for orders-0
Broker 2: follower for orders-0
Broker 3: follower for orders-0
```

Producer writes to leader.

Followers replicate from leader.

If leader dies:

```text
new leader elected from in-sync replicas
```

Why?

```text
fault tolerance
high availability
durability
```

---

## Phase 21 — Full Production Kafka Concepts

Production extensions:

```text
batching
compression
backpressure
network protocol
leader/follower
ISR
ack modes
page cache
zero-copy
metrics
load testing
failure handling
```

---

# 4. Most Important Data Structures

| Kafka Feature | Data Structure / System Concept |
|---|---|
| Partition log | Append-only file |
| Offset tracking | Monotonic counter |
| Topic registry | HashMap topic -> partitions |
| Partition routing | Hash function |
| Offset index | TreeMap / sparse index |
| Consumer group | Map group -> members |
| Partition assignment | Map consumer -> partitions |
| Retention | Sorted segment metadata |
| Replication | Leader/follower state |
| ISR | Set of caught-up replicas |

---

# 5. Core Java Mental Models

## Message Record

```java
record MessageRecord(
    long offset,
    long timestampMillis,
    String key,
    String value
) {}
```

## Append-Only Log

```java
long offset = nextOffset++;

writer.write(offset + "|" + timestamp + "|" + key + "|" + value);
writer.newLine();
```

## Partition Routing

```java
int partition = Math.floorMod(key.hashCode(), partitionCount);
```

## Offset Index

```java
NavigableMap<Long, Long> offsetToPosition = new TreeMap<>();

offsetToPosition.put(offset, filePosition);

long startPosition = offsetToPosition.floorEntry(requestedOffset).getValue();
```

## Consumer Offset Commit

```java
committedOffsets.put(groupId, nextOffsetToRead);
```

## Segment Rolling

```java
if (activeSegment.size() >= maxSegmentBytes) {
    rollToNewSegment();
}
```

---

# 6. Key Dry Runs

## Dry Run 1 — Produce Message

```text
producer.send("orders", "order-1", "created")
```

Flow:

```text
Producer
  -> Broker
  -> Topic orders
  -> PartitionRouter routes order-1
  -> Partition 2
  -> active LogSegment
  -> append line
  -> return offset
```

Disk:

```text
orders-2.log

0|timestamp|order-1|created
```

---

## Dry Run 2 — Same Key Ordering

```text
send order-1 created
send order-1 paid
send order-1 shipped
```

Because same key hash routes to same partition:

```text
orders-2.log

0|timestamp|order-1|created
1|timestamp|order-1|paid
2|timestamp|order-1|shipped
```

Result:

```text
order-1 events remain ordered
```

---

## Dry Run 3 — Consumer Poll

Consumer starts at offset 1:

```text
consumer.poll("orders", partition=2, offset=1)
```

Log:

```text
0|timestamp|order-1|created
1|timestamp|order-1|paid
2|timestamp|order-1|shipped
```

Returns:

```text
offset 1 -> paid
offset 2 -> shipped
```

---

## Dry Run 4 — Offset Commit

```text
Consumer reads offsets 0,1,2
Processes successfully
Commits offset 3
```

Crash and restart:

```text
read committed offset = 3
resume from offset 3
```

Meaning:

```text
offset commit stores next record to read
```

---

## Dry Run 5 — Segment Rolling

Max segment size reached.

Before:

```text
orders-0/
  00000000000000000000.log
```

Append more messages.

After rolling:

```text
orders-0/
  00000000000000000000.log
  00000000000000001000.log
```

New writes go to latest active segment.

---

## Dry Run 6 — Index Lookup

Index:

```text
0   -> byte 0
100 -> byte 4500
200 -> byte 9200
```

Request:

```text
readFrom(150)
```

Use floor entry:

```text
floorEntry(150) = 100 -> byte 4500
```

Then scan forward until offset >= 150.

---

## Dry Run 7 — Retention

Segments:

```text
00000000000000000000.log  old
00000000000000001000.log  old
00000000000000002000.log  active
```

Retention cleanup deletes only old closed segments:

```text
delete 00000000000000000000.log
delete 00000000000000001000.log
keep active segment
```

---

## Dry Run 8 — Consumer Group

Partitions:

```text
P0 P1 P2
```

Consumers:

```text
C1 C2
```

Assignment:

```text
C1 -> P0, P2
C2 -> P1
```

If C3 joins:

```text
rebalance
C1 -> P0
C2 -> P1
C3 -> P2
```

---

## Dry Run 9 — Replication

Write:

```text
Producer sends order-created
```

Flow:

```text
Producer
  -> Leader broker
  -> append to leader log
  -> followers fetch/copy
  -> ISR updated
  -> ack returned based on acks config
```

For `acks=all`:

```text
leader + ISR must confirm
```

---

# 7. Kafka Guarantees

## Ordering

```text
Guaranteed only inside one partition.
```

To preserve order for same entity:

```text
use same key
```

Example:

```text
orderId as key
```

---

## Delivery Semantics

| Semantics | Meaning |
|---|---|
| At-most-once | May lose, no duplicate |
| At-least-once | No loss, may duplicate |
| Exactly-once | No loss/no duplicate with extra coordination |

Most practical backend systems start with:

```text
at-least-once + idempotent consumer
```

---

## Durability

Durability depends on:

```text
replication factor
acks mode
ISR health
fsync/page cache behavior
```

---

# 8. Ack Modes

```text
acks=0
```

Producer does not wait.

```text
fastest, weakest durability
```

```text
acks=1
```

Leader confirms write.

```text
balanced but can lose if leader dies before followers copy
```

```text
acks=all
```

Leader and ISR confirm.

```text
strongest durability, higher latency
```

---

# 9. Why Kafka Is Fast

Kafka is fast because of:

```text
sequential disk writes
append-only log
batching
page cache
zero-copy transfer
partition parallelism
consumer pull model
```

Important mental model:

```text
Kafka avoids random writes.
Kafka writes sequentially and reads sequentially.
```

---

# 10. Bottlenecks To Watch

| Bottleneck | Symptom |
|---|---|
| Too few partitions | low parallelism |
| Too many partitions | metadata/rebalance overhead |
| Slow consumer | consumer lag grows |
| Large messages | network/disk pressure |
| Under-replicated partitions | durability risk |
| Frequent rebalances | processing pauses |
| Bad key distribution | hot partition |
| Low retention disk | old data deleted too early |
| No idempotency | duplicate processing bugs |

---

# 11. System Design Use Cases

## Payment Events

```text
payment-created
payment-authorized
payment-captured
payment-failed
```

Use key:

```text
paymentId
```

Guarantee:

```text
events for same payment remain ordered
```

---

## Order Pipeline

```text
order-created -> inventory-reserved -> payment-paid -> shipment-created
```

Use Kafka for:

```text
async decoupling
retry
event replay
audit trail
```

---

## Notification System

Kafka topic:

```text
notification-events
```

Consumers:

```text
email-service group
sms-service group
push-service group
```

Each group independently reads the same events.

---

## Logging / Metrics

Kafka topic:

```text
app-logs
metrics
clickstream
```

Why Kafka?

```text
high write throughput
fan-out to multiple consumers
retention-based replay
```

---

# 12. Interview Answers

## What is Kafka?

```text
Kafka is a distributed append-only commit log used for high-throughput event streaming.
It stores records in topic partitions, assigns offsets, lets consumers read by offset,
and keeps data until retention cleanup.
```

## Why partitions?

```text
Partitions provide horizontal scalability, parallel writes, parallel reads,
and ordering within a partition.
```

## Why offset?

```text
Offset is the consumer's position in a partition log.
Kafka does not delete messages after consumption, so consumers track progress using offsets.
```

## Why consumer groups?

```text
A consumer group allows multiple consumers to share work.
Each partition is assigned to one consumer within the group.
Different groups can independently consume the same topic.
```

## Why segment files?

```text
Segment files prevent one huge log file.
They make retention deletion, recovery, and file management efficient.
```

## Why index file?

```text
The index maps offsets to byte positions, allowing Kafka to seek near a requested offset
instead of scanning from the beginning.
```

## What happens during rebalance?

```text
When consumers join or leave, partitions are revoked and reassigned.
This balances work but temporarily pauses consumption.
```

## What is ISR?

```text
ISR means in-sync replicas.
These replicas are sufficiently caught up with the leader and are safe candidates for durable acknowledgments.
```

## What is consumer lag?

```text
Consumer lag = latest partition offset - consumer committed offset.
It shows how far behind the consumer is.
```

---

# 13. Production Checklist

```text
[ ] Choose correct partition key
[ ] Set replication factor >= 3 for critical topics
[ ] Use acks=all for critical writes
[ ] Make consumers idempotent
[ ] Commit offset after successful processing
[ ] Monitor consumer lag
[ ] Monitor under-replicated partitions
[ ] Avoid hot partitions
[ ] Configure retention carefully
[ ] Handle retries and DLQ
[ ] Use batching for throughput
[ ] Use schema/versioning for events
[ ] Load test producers and consumers
```

---

# 14. Most Important Final Mental Models

```text
Kafka = log, not queue
```

```text
Topic = logical stream
```

```text
Partition = physical ordered log
```

```text
Offset = position in one partition
```

```text
Consumer group = work sharing
```

```text
Same key = same partition = ordering
```

```text
Segment = manageable log chunk
```

```text
Index = offset lookup accelerator
```

```text
Retention = delete old segments, not consumed messages
```

```text
Replication = survive broker failure
```

```text
ISR + acks=all = stronger durability
```

---

# 15. MiniKafka Learning Path Recap

```text
001 Create log file
002 Append records
003 MessageRecord object
004 LogSegment abstraction
005 Read from offset
006 Partition abstraction
007 Topic abstraction
008 Multiple partitions
009 Key routing
010 Broker API
011 Producer API
012 Consumer API
013 Offset commit
014 Consumer groups
015 Partition assignment
016 Rebalancing
017 Segment rolling
018 Index file
019 Retention cleanup
020 Replication basics
021 Production-level Kafka concepts
```

Final summary:

```text
MiniKafka teaches how high-scale messaging systems work internally:
append logs, partitions, offsets, producers, consumers, groups, rebalancing,
segments, indexes, retention, replication, and failure handling.
```
