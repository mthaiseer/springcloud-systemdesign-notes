# 000_MINIKAFKA_20_PHASE_INDEX

# MiniKafka — Complete 20 Phase Journey

## Goal

Build a Kafka-like distributed messaging system from scratch step by step.

---

# Final Architecture

```text
Producer
   |
   v
Broker
   |
   v
Topic
   |
   +--> Partition 0
   |       |
   |       v
   |    Log Segments
   |
   +--> Partition 1
   |
   +--> Partition 2
   |
   v
Consumers
   |
   v
Consumer Groups
```

---

# Phase 1 — Storage Engine Foundation

## 001_Create_Log_File
- Create physical partition log
- Learn Kafka storage basics
- Understand topic-partition mapping

## 002_Append_Records_To_Log_File
- Append-only storage
- Offset generation
- Sequential writes

## 003_MessageRecord_Object
- Convert raw strings into objects
- Serialization basics
- Record abstraction

## 004_LogSegment_Abstraction
- Encapsulate append/read logic
- Reusable storage layer
- Segment abstraction

## 005_Read_From_Specific_Offset
- Consumer-style reads
- Offset-based consumption
- Sequential scanning

---

# Phase 2 — Partition System

## 006_Partition_Abstraction
- Build Partition class
- Partition metadata
- Partition ownership

## 007_Topic_Abstraction
- Topic manages partitions
- Topic metadata
- Partition registry

## 008_Multiple_Partitions
- Parallel logs
- Ordering guarantees
- Partition scaling

## 009_Key_Based_Partition_Routing
- Hash partitioning
- Consistent routing
- Producer partition selection

---

# Phase 3 — Broker Layer

## 010_Broker_API
- Broker manages topics
- Central messaging layer
- Topic lookup

## 011_Producer_API
- Send records
- Producer abstraction
- Publish workflow

## 012_Consumer_API
- Poll records
- Consumer abstraction
- Sequential consumption

## 013_Consumer_Offset_Commit
- Track consumer progress
- Commit offsets
- Resume consumption

---

# Phase 4 — Consumer Groups

## 014_Consumer_Groups
- Group abstraction
- Shared consumption
- Work distribution

## 015_Partition_Assignment
- Assign partitions to consumers
- Ownership strategy
- Parallel reads

## 016_Rebalancing_Basics
- Consumer join/leave
- Reassign partitions
- Group coordination

---

# Phase 5 — Kafka Internal Optimizations

## 017_Segment_Rolling
- Split large logs into segments
- Rolling policy
- Segment lifecycle

## 018_Index_File
- Fast offset lookup
- Sparse indexing
- Efficient reads

## 019_Retention_Cleanup
- Time-based cleanup
- Size-based cleanup
- Segment deletion

---

# Phase 6 — Distributed Kafka Concepts

## 020_Replication_Basics
- Leader-follower model
- Replica synchronization
- Fault tolerance
- ISR concepts

---

# What You Will Learn

```text
append-only logs
offset management
partitioning
consumer groups
segment architecture
replication
fault tolerance
distributed systems
high-scale messaging systems
```

---

# Final MiniKafka Features

```text
[yes] append-only storage
[yes] offset-based reads
[yes] partitioned topics
[yes] producer API
[yes] consumer API
[yes] consumer groups
[yes] partition assignment
[yes] segment rolling
[yes] retention cleanup
[yes] replication basics
```

---

# Future Extensions

```text
021 TCP socket protocol
022 Real broker networking
023 Async producer batching
024 Compression
025 Retry + DLQ
026 Raft metadata layer
027 Docker deployment
028 Metrics and observability
029 Performance testing
030 Multi-node MiniKafka cluster
```
