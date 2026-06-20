
# 099_Database_Final_Revision_CheatSheet.md

# MiniDatabase Final Revision CheatSheet

# 0. Full Database Mental Model

```text
Client Query
    ↓
SQL Parser
    ↓
Optimizer
    ↓
Execution Engine
    ↓
Indexes / Table Scan
    ↓
Buffer Pool
    ↓
Pages / Records
    ↓
WAL
    ↓
Disk
```

Modern database internally contains:

```text
Storage Engine
Indexes
Buffer Pool
Transactions
MVCC
Locks
Replication
Sharding
Recovery
Monitoring
```

---

# 1. SQL vs NoSQL

## SQL

```text
tables
rows
schema
joins
ACID
strong consistency
```

Examples:

- PostgreSQL
- MySQL

Good for:

```text
payments
banking
ERP
transactions
```

---

## NoSQL

```text
flexible schema
horizontal scaling
distributed systems
eventual consistency
```

Examples:

- Cassandra
- MongoDB
- Redis

Good for:

```text
social feed
IoT
event ingestion
massive scale
```

---

# 2. Row vs Column Storage

## Row Storage

```text
[id name salary]
```

Good for:

```text
OLTP
transaction systems
```

---

## Column Storage

```text
id-column
name-column
salary-column
```

Good for:

```text
analytics
aggregation
compression
```

---

# 3. File → Page → Record

```text
Database File
    ↓
Pages
    ↓
Records
    ↓
Bytes
```

ASCII:

```text
File
 ├── Page-1
 │     ├── Row-A
 │     └── Row-B
 │
 └── Page-2
       └── Row-C
```

---

# 4. INSERT UPDATE DELETE

## INSERT

```text
append row
update indexes
write WAL
```

## UPDATE

```text
find row
modify row
update indexes
write WAL
```

MVCC systems:

```text
create new row version
```

## DELETE

```text
mark deleted
write WAL
```

---

# 5. Hash Index

```text
hash(key)
    ↓
bucket
    ↓
row pointer
```

Good for:

```text
exact lookup
```

Bad for:

```text
range queries
ORDER BY
```

---

# 6. B+Tree

Most common DB index.

Supports:

```text
range scan
sorting
prefix search
```

ASCII:

```text
        [50]
      /      \
 [20 30]   [70 90]
```

---

# 7. LSM Tree

Write optimized structure.

Flow:

```text
Write
   ↓
MemTable
   ↓
SSTable
   ↓
Compaction
```

Used in:

- Cassandra
- RocksDB

---

# 8. B+Tree vs LSM

| Feature | B+Tree | LSM |
|---|---|---|
| Writes | Moderate | Excellent |
| Reads | Excellent | Good |
| Compaction | No | Yes |

---

# 9. Query Execution Pipeline

```text
SQL
  ↓
Parser
  ↓
Planner
  ↓
Optimizer
  ↓
Executor
  ↓
Result
```

---

# 10. Filter Projection Scan

## Filter

```sql
WHERE age > 18
```

## Projection

```sql
SELECT name,email
```

## Scan Types

```text
table scan
index scan
bitmap scan
```

---

# 11. ORDER BY LIMIT GROUP BY

ORDER BY:

```text
sorting operation
```

LIMIT:

```text
stop query early
```

GROUP BY:

```text
aggregation
```

---

# 12. Join Algorithms

## Nested Loop

```text
for each row A
    scan B
```

## Hash Join

```text
build hash
probe matches
```

## Merge Join

```text
sorted inputs
```

---

# 13. Buffer Pool

RAM cache for disk pages.

```text
Disk Pages
      ↓
Buffer Pool
      ↓
Serve Queries
```

Benefits:

```text
less disk IO
better performance
```

---

# 14. WAL

Write Ahead Log.

Rule:

```text
write WAL before flushing pages
```

Flow:

```text
UPDATE
   ↓
WAL append
   ↓
COMMIT
   ↓
flush page later
```

Provides:

```text
durability
recovery
```

---

# 15. Transactions

Transaction:

```text
safe unit of work
```

Example:

```text
debit
credit
commit
```

---

# 16. ACID

## Atomicity

```text
all or nothing
```

## Consistency

```text
rules preserved
```

## Isolation

```text
concurrent correctness
```

## Durability

```text
survives crash
```

---

# 17. MVCC

MVCC:

```text
Multi-Version Concurrency Control
```

Readers:

```text
read snapshots
```

Writers:

```text
create new row versions
```

ASCII:

```text
V1 balance=100
V2 balance=150
```

---

# 18. Isolation Problems

## Dirty Read

read uncommitted data

## Non-Repeatable Read

same row changes

## Phantom Read

row set changes

## Lost Update

updates overwrite each other

---

# 19. Isolation Levels

| Isolation | Dirty Read | NonRepeatable | Phantom |
|---|---|---|---|
| Read Uncommitted | Yes | Yes | Yes |
| Read Committed | No | Yes | Yes |
| Repeatable Read | No | No | Sometimes |
| Serializable | No | No | No |

---

# 20. Locks

## Shared Lock

```text
multiple readers
```

## Exclusive Lock

```text
single writer
```

---

# 21. SELECT FOR UPDATE

```sql
SELECT *
FROM account
WHERE id=1
FOR UPDATE;
```

Locks row for update.

---

# 22. Deadlock

```text
T1 waits for T2
T2 waits for T1
```

Database aborts one transaction.

---

# 23. Replication

Replication:

```text
copy same data to many nodes
```

Benefits:

```text
HA
failover
read scaling
```

ASCII:

```text
Primary
   ├── Replica-1
   └── Replica-2
```

---

# 24. Replication Lag

Replica behind primary.

Causes:

```text
stale reads
```

---

# 25. Sharding

Sharding:

```text
split data across nodes
```

Benefits:

```text
write scaling
storage scaling
```

---

# 26. Consistent Hashing

```text
minimal key movement
when adding/removing nodes
```

Used in:

- Cassandra
- Redis Cluster

---

# 27. PostgreSQL Internals

Architecture:

```text
Heap Storage
MVCC
WAL
B+Tree
VACUUM
```

Index stores:

```text
key → tuple location
```

UPDATE creates new tuple version.

---

# 28. MySQL InnoDB Internals

Architecture:

```text
Clustered Index
Buffer Pool
Redo Log
Undo Log
```

Leaf nodes contain full rows.

---

# 29. Cassandra Internals

Architecture:

```text
LSM
MemTable
SSTable
Compaction
Partitioning
```

Write flow:

```text
Commit Log
    ↓
MemTable
    ↓
Flush SSTable
```

---

# 30. Bloom Filter

```text
probably exists
or
definitely not exists
```

Avoids unnecessary SSTable reads.

---

# 31. Production Architecture

```text
App
  ↓
Connection Pool
  ↓
Primary DB
  ↓
Replicas
  ↓
Backups
```

---

# 32. Connection Pooling

Tools:

- HikariCP
- PgBouncer

Benefits:

```text
reuse connections
reduce overhead
```

---

# 33. Slow Query Debugging

Use:

```sql
EXPLAIN
```

Check:

```text
Seq Scan
bad joins
missing indexes
large sorts
```

---

# 34. Cache Layer

```text
Redis
   ↓ miss
Database
```

Reduces DB load.

---

# 35. PITR

Point In Time Recovery:

```text
Base Backup
+
WAL Replay
```

---

# 36. Important Metrics

```text
QPS
P95 latency
replication lag
connections
cache hit ratio
slow queries
disk IO
```

---

# 37. Common Production Problems

```text
missing indexes
hot shards
replication lag
deadlocks
table bloat
too many connections
```

---

# 38. Most Important Concepts

Highest ROI topics:

```text
B+Tree
WAL
Transactions
MVCC
Locks
Replication
Sharding
Buffer Pool
Query Optimization
```

---

# 39. Interview Quick Answers

## What is WAL?

```text
Log changes before flushing pages to disk.
Provides crash recovery and durability.
```

## What is MVCC?

```text
Multiple row versions allow readers to use snapshots while writers
create new versions.
```

## What is replication?

```text
Copy database changes to replicas for HA and read scaling.
```

## What is sharding?

```text
Split dataset across servers for horizontal scaling.
```

---

# 40. Final Backend Mental Model

```text
Application
     ↓
Cache
     ↓
Connection Pool
     ↓
Query Planner
     ↓
Indexes
     ↓
Buffer Pool
     ↓
WAL
     ↓
Disk
     ↓
Replication
     ↓
Backup / Recovery
```
