# 000_Index.md

# MiniDatabase — Complete Learning Roadmap

## Why MiniDatabase Exists

Most developers use:

```text
Postgres
MySQL
Redis
MongoDB
Cassandra
```

without understanding:

```text
how storage works
how indexes work
why queries become slow
how WAL works
how transactions work
how MVCC works
why NoSQL scales differently
why Cassandra uses LSM trees
```

MiniDatabase teaches database internals using:

```text
simple mental models
step-by-step flow
Java implementations
dry runs
backend production mapping
```

Goal:

```text
Understand databases like:
Postgres
MySQL
Redis
Cassandra
MongoDB
Kafka storage model
```

without reading huge academic books first.

---

# Full MiniDatabase Flow

```text
Database Basics
      ↓
Table & Row Storage
      ↓
CRUD Operations
      ↓
Indexes
      ↓
Query Execution
      ↓
Storage Engine
      ↓
Caching
      ↓
WAL & Recovery
      ↓
Transactions
      ↓
MVCC & Isolation
      ↓
Replication & Sharding
      ↓
Production Database Design
```

---

# Complete File Tree

```text
MiniDatabase/
│
├── 001_What_Is_Database.md
├── 002_SQL_vs_NoSQL.md
├── 003_Row_vs_Column_Storage.md
├── 004_InMemory_Table.md
├── 005_Table_Row_Record_Model.md
├── 006_Insert_Update_Delete.md
├── 007_Hash_Index.md
├── 008_BTree_vs_LSMTree.md
├── 009_Query_Execution_Pipeline.md
├── 010_Filter_Projection_Scan.md
├── 011_OrderBy_Limit_Aggregation.md
├── 012_Join_Algorithms.md
├── 013_Page_Block_Storage.md
├── 014_BufferPool_Cache.md
├── 015_Write_Ahead_Log_WAL.md
├── 016_Transaction_ACID.md
├── 017_Isolation_MVCC_Locking.md
├── 018_Replication_Sharding.md
├── 019_Postgres_MySQL_Cassandra_Internals.md
└── 020_Production_Grade_Database.md
```

---

# Foundation Layer

## 001_What_Is_Database.md

Learn:

```text
Why databases exist
Why files are not enough
Persistence
Storage
Retrieval
Consistency
```

Mental model:

```text
Database = organized persistent storage system
```

---

## 002_SQL_vs_NoSQL.md

Learn:

```text
Relational databases
NoSQL databases
Scaling differences
Consistency tradeoffs
```

Mental model:

```text
SQL = structured + strong consistency
NoSQL = scalability + flexibility
```

---

## 003_Row_vs_Column_Storage.md

Learn:

```text
Row storage
Column storage
OLTP vs OLAP
```

Mental model:

```text
Row DB optimized for transactions
Column DB optimized for analytics
```

---

# Storage Basics Layer

## 004_InMemory_Table.md

Build:

```text
Simple in-memory table
```

Learn:

```text
Rows
Columns
Schema
Primary keys
```

---

## 005_Table_Row_Record_Model.md

Learn:

```text
How rows stored internally
Record layout
Serialization basics
```

Mental model:

```text
Table = collection of records
```

---

## 006_Insert_Update_Delete.md

Learn:

```text
CRUD internals
Insert path
Update path
Delete markers
```

---

# Index Layer

## 007_Hash_Index.md

Learn:

```text
Hash-based lookup
O(1) access
Key-value indexing
```

Mental model:

```text
Hash index = fast exact lookup
```

Used in:

```text
Redis
HashMap
Key-value stores
```

---

## 008_BTree_vs_LSMTree.md

MOST IMPORTANT FILE.

Learn:

```text
BTree internals
LSM Tree internals
SSTables
Compaction
```

Mental models:

```text
BTree = optimized reads
LSM Tree = optimized writes
```

Used by:

```text
Postgres → BTree
MySQL InnoDB → BTree
Cassandra → LSM
RocksDB → LSM
```

---

# Query Engine Layer

## 009_Query_Execution_Pipeline.md

Learn:

```text
SQL parsing
Execution pipeline
Query planning basics
```

Flow:

```text
SQL
 ↓
Parser
 ↓
Optimizer
 ↓
Execution Plan
 ↓
Storage Engine
```

---

## 010_Filter_Projection_Scan.md

Learn:

```text
WHERE
SELECT
table scan
index scan
```

Mental model:

```text
Filter rows
Project columns
```

---

## 011_OrderBy_Limit_Aggregation.md

Learn:

```text
Sorting
Top-K
Aggregation
COUNT
SUM
GROUP BY
```

---

## 012_Join_Algorithms.md

Learn:

```text
Nested loop join
Hash join
Merge join
```

Mental model:

```text
Join = combine rows from tables
```

---

# Storage Engine Layer

## 013_Page_Block_Storage.md

Learn:

```text
Disk pages
Blocks
Page cache
Storage layout
```

Mental model:

```text
Database reads pages, not single rows
```

VERY IMPORTANT.

---

## 014_BufferPool_Cache.md

Learn:

```text
Buffer pool
Page cache
LRU
Hot pages
```

Mental model:

```text
RAM cache for disk pages
```

Used by:

```text
Postgres shared buffers
InnoDB buffer pool
```

---

## 015_Write_Ahead_Log_WAL.md

Learn:

```text
Durability
Crash recovery
Append-only logs
```

Mental model:

```text
Write log first
Then update database
```

VERY IMPORTANT.

Used by:

```text
Postgres WAL
Kafka append log
Redis AOF
```

---

# Transaction Layer

## 016_Transaction_ACID.md

Learn:

```text
Transactions
Atomicity
Consistency
Isolation
Durability
```

Mental model:

```text
Transaction = all-or-nothing operation
```

Examples:

```text
Bank transfer
Payment systems
Inventory updates
```

---

## 017_Isolation_MVCC_Locking.md

MOST IMPORTANT CONCURRENCY FILE.

Learn:

```text
Dirty read
Non-repeatable read
Phantom read
Serializable
MVCC
Snapshot isolation
Optimistic locking
Pessimistic locking
```

Mental model:

```text
Multiple transactions safely accessing same data
```

Used heavily in:

```text
Postgres
MySQL InnoDB
Banking systems
Payment systems
```

---

# Scaling Layer

## 018_Replication_Sharding.md

Learn:

```text
Primary replica
Replication lag
Read replicas
Sharding
Consistent hashing
Partitioning
```

Mental model:

```text
Replication = copy data
Sharding = split data
```

---

# Real Database Internals

## 019_Postgres_MySQL_Cassandra_Internals.md

Learn:

```text
Postgres architecture
MySQL InnoDB internals
Cassandra architecture
```

Mental model:

```text
Different databases optimize different workloads
```

---

# Production Layer

## 020_Production_Grade_Database.md

FINAL INTEGRATION FILE.

Learn:

```text
Connection pooling
Slow query debugging
Index tuning
Hot partitions
Caching
Database bottlenecks
Read/write splitting
Scaling strategies
```

Mental model:

```text
Real production databases =
storage + cache + replication + transactions + scaling
```

---

# Most Important Concepts

## Highest ROI Files

```text
007_Hash_Index.md
008_BTree_vs_LSMTree.md
013_Page_Block_Storage.md
014_BufferPool_Cache.md
015_Write_Ahead_Log_WAL.md
016_Transaction_ACID.md
017_Isolation_MVCC_Locking.md
018_Replication_Sharding.md
020_Production_Grade_Database.md
```

---

# Database Mental Models

## Database

```text
Persistent organized storage
```

## Index

```text
Shortcut to locate data quickly
```

## BTree

```text
Optimized for reads + range scans
```

## LSM Tree

```text
Optimized for high write throughput
```

## WAL

```text
Append log before modifying actual data
```

## MVCC

```text
Multiple versions allow concurrent reads/writes
```

## Replication

```text
Copy data to multiple machines
```

## Sharding

```text
Split data across multiple machines
```

---

# Production System Mapping

```text
Postgres
→ BTree + WAL + MVCC

MySQL InnoDB
→ Clustered Index + Buffer Pool + WAL

Redis
→ In-memory + append log

Kafka
→ Append-only distributed log

Cassandra
→ LSM Tree + SSTables

ElasticSearch
→ Inverted Index

MongoDB
→ Document database
```

---

# Final Goal

After MiniDatabase you should understand:

```text
Why queries become slow
Why indexes matter
Why transactions deadlock
Why Cassandra scales writes
Why Redis is fast
Why Kafka append-only
Why MVCC improves concurrency
Why WAL improves durability
```

This massively improves:

```text
System design
Backend architecture
Production debugging
Database interviews
Performance optimization
High-scale backend design
```

---

# Recommended Next Mini Engines

Best order after MiniDatabase:

```text
MiniRedis
      ↓
MiniKafka
      ↓
MiniSearchEngine
      ↓
MiniGateway
      ↓
MiniSpringBootCloud
```

Because all these systems internally depend heavily on:

```text
storage
indexes
WAL
replication
caching
concurrency
partitioning
```
