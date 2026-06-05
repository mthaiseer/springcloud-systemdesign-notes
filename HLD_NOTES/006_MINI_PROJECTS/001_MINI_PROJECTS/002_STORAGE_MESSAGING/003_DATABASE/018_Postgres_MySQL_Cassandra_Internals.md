# 018_Postgres_MySQL_Cassandra_Internals.md

# MiniDatabase — 018 PostgreSQL MySQL Cassandra Internals

## 0. Why This File Exists

You already learned:

```text
storage
pages
indexes
WAL
transactions
MVCC
locking
replication
sharding
```

Now we connect everything to real databases.

This file teaches internals of:

- entity["software","PostgreSQL","database"]
- entity["software","MySQL","database"]
- entity["software","Cassandra","database"]

You learn:

```text
heap storage
B+Tree indexes
clustered indexes
MVCC internals
VACUUM
undo/redo
LSM trees
SSTables
compaction
partitioning
replication
write path
read path
```

This file helps connect:

```text
database theory
→
real production database engines
```

Only ASCII diagrams are used.

---

# 1. Biggest Mental Model

Different databases optimize for different goals.

| Database | Optimized For |
|---|---|
| entity["software","PostgreSQL","database"] | correctness + advanced SQL |
| entity["software","MySQL","database"] | OLTP + simplicity + web scale |
| entity["software","Cassandra","database"] | massive distributed scale |

---

# 2. PostgreSQL Mental Model

PostgreSQL architecture:

```text
Heap Storage
+
MVCC
+
WAL
+
B+Tree indexes
+
VACUUM
```

Strong transactional system.

---

# 3. PostgreSQL Storage Model

Postgres stores table rows in:

```text
heap pages
```

Indexes point to:

```text
tuple locations
```

called:

```text
TID (Tuple ID)
```

---

# 4. PostgreSQL Heap ASCII

```text
Table Heap

Page-1
 ├── Row-A
 ├── Row-B
 └── Row-C

Page-2
 ├── Row-D
 └── Row-E
```

Rows stored separately from indexes.

---

# 5. PostgreSQL Index Mental Model

Postgres indexes contain:

```text
key → TID
```

Example:

```text
email → page/slot location
```

---

# 6. PostgreSQL Index ASCII

```text
B+Tree Index

alice@gmail → Page-1 Slot-2
bob@gmail   → Page-5 Slot-1
```

Then heap page fetched.

---

# 7. PostgreSQL Read Flow

```text
Query
   ↓
Use B+Tree index
   ↓
Find TID
   ↓
Read heap page
   ↓
Check MVCC visibility
   ↓
Return row
```

---

# 8. PostgreSQL MVCC

Postgres uses:

```text
xmin
xmax
tuple versions
snapshots
```

UPDATE creates:

```text
new tuple version
```

instead of overwriting.

---

# 9. PostgreSQL MVCC ASCII

Before:

```text
Tuple-V1 balance=100
```

Update:

```text
Tuple-V2 balance=150
```

Old tuple remains temporarily.

---

# 10. PostgreSQL VACUUM

Old row versions create:

```text
dead tuples
```

VACUUM removes them.

---

# 11. PostgreSQL VACUUM ASCII

```text
Heap Page

Tuple-V1 dead
Tuple-V2 live

VACUUM:
remove V1
keep V2
```

---

# 12. PostgreSQL WAL

Postgres durability uses:

```text
Write Ahead Log
```

Flow:

```text
WAL first
then page flush
```

---

# 13. PostgreSQL WAL ASCII

```text
UPDATE row
    ↓
Write WAL
    ↓
Flush WAL
    ↓
Modify heap page
```

---

# 14. PostgreSQL Strengths

Postgres excellent for:

```text
strong consistency
complex SQL
joins
transactions
analytics
JSON support
extensions
GIS/PostGIS
```

---

# 15. PostgreSQL Weaknesses

Challenges:

```text
VACUUM overhead
write amplification
large-scale sharding harder
```

---

# 16. MySQL Mental Model

Most production MySQL uses:

```text
InnoDB storage engine
```

Core architecture:

```text
Clustered Index
+
Buffer Pool
+
Redo Log
+
Undo Log
+
MVCC
```

---

# 17. MySQL Clustered Index

Very important concept.

InnoDB stores:

```text
table rows directly inside primary key B+Tree
```

This is called:

```text
clustered index
```

---

# 18. Clustered Index ASCII

```text
Primary Key B+Tree

Leaf Nodes:
[id=1 row-data]
[id=2 row-data]
[id=3 row-data]
```

Leaf stores full rows.

---

# 19. Difference From PostgreSQL

Postgres:

```text
Index → heap row
```

MySQL InnoDB:

```text
Primary index leaf contains row itself
```

---

# 20. Secondary Index In MySQL

Secondary index stores:

```text
secondary-key → primary-key
```

Then lookup primary key tree.

---

# 21. MySQL Secondary Index ASCII

```text
Secondary Index:
email → id

Then:
id → clustered index row
```

Two-step lookup.

---

# 22. MySQL Read Flow

```text
Query
   ↓
Secondary index lookup
   ↓
Get primary key
   ↓
Clustered index lookup
   ↓
Fetch row
```

---

# 23. InnoDB Buffer Pool

Most reads/writes happen in:

```text
buffer pool
```

Important cache layer.

---

# 24. Buffer Pool ASCII

```text
Disk Pages
      ↓
Buffer Pool (RAM)
      ↓
Queries use cached pages
```

---

# 25. Redo Log

Redo log provides durability.

Flow:

```text
write redo log
then flush pages later
```

Very similar to WAL concept.

---

# 26. Undo Log

Undo log supports:

```text
rollback
MVCC old versions
```

Before update:

```text
store old values in undo log
```

---

# 27. Undo Log ASCII

```text
UPDATE balance 100→150

Undo Log stores:
old balance=100
```

Rollback can restore old value.

---

# 28. MySQL MVCC

InnoDB MVCC uses:

```text
undo log
transaction IDs
read views
```

Readers can access old versions via undo log chain.

---

# 29. MySQL Strengths

Great for:

```text
OLTP
web applications
simple operational systems
high read workloads
```

---

# 30. MySQL Weaknesses

Challenges:

```text
complex analytics weaker
advanced SQL features fewer
cross-shard scaling difficult
```

---

# 31. Cassandra Mental Model

Cassandra architecture:

```text
LSM Trees
+
SSTables
+
MemTables
+
Partitioning
+
Replication
+
Eventual Consistency
```

Designed for:

```text
massive distributed scale
```

---

# 32. Cassandra Core Philosophy

Instead of:

```text
single strong transactional machine
```

Cassandra optimizes for:

```text
many distributed nodes
high write throughput
availability
```

---

# 33. Cassandra Write Path

Write flow:

```text
Client Write
     ↓
Commit Log
     ↓
MemTable
     ↓
Flush to SSTable
```

---

# 34. Cassandra Write ASCII

```text
Write request
      ↓
Commit Log append
      ↓
MemTable update
      ↓
Flush immutable SSTable later
```

---

# 35. MemTable

MemTable:

```text
in-memory sorted structure
```

Temporary write buffer.

---

# 36. SSTable

SSTable means:

```text
Sorted String Table
```

Immutable on-disk sorted files.

---

# 37. SSTable ASCII

```text
SSTable-1
  key1
  key5
  key9

SSTable-2
  key2
  key7
```

Many SSTables may exist simultaneously.

---

# 38. Why Cassandra Uses LSM

LSM trees optimize:

```text
high write throughput
sequential disk writes
```

Instead of expensive in-place B+Tree updates.

---

# 39. Cassandra Read Path

Reads check:

```text
MemTable
Bloom Filter
SSTables
```

Then merge results.

---

# 40. Cassandra Read ASCII

```text
Read key
    ↓
Check MemTable
    ↓
Check Bloom Filter
    ↓
Search SSTables
    ↓
Merge latest version
```

---

# 41. Bloom Filter

Bloom filter quickly estimates:

```text
key probably exists
or definitely not exists
```

Helps avoid unnecessary SSTable reads.

---

# 42. Bloom Filter ASCII

```text
Key lookup
    ↓
Bloom says "not present"
    ↓
Skip SSTable
```

Huge IO optimization.

---

# 43. Cassandra Compaction

Too many SSTables become expensive.

Compaction merges:

```text
multiple SSTables
```

into larger optimized SSTables.

---

# 44. Compaction ASCII

Before:

```text
SSTable-1
SSTable-2
SSTable-3
```

Compaction:

```text
merge
remove tombstones
keep latest versions
```

After:

```text
SSTable-Big
```

---

# 45. Cassandra Partitioning

Cassandra distributes data using:

```text
consistent hashing
partition keys
```

Rows distributed across cluster.

---

# 46. Cassandra Ring ASCII

```text
      Node-A
     /      \
Node-D      Node-B
     \      /
      Node-C
```

Keys mapped around ring.

---

# 47. Cassandra Replication

Each partition replicated across multiple nodes.

Example:

```text
Replication Factor = 3
```

Means:

```text
3 copies of each partition
```

---

# 48. Cassandra Consistency Levels

Client chooses consistency level.

Examples:

```text
ONE
QUORUM
ALL
```

Tradeoff:

```text
consistency vs latency
```

---

# 49. Cassandra Eventual Consistency

Nodes may temporarily differ.

Eventually replicas converge.

---

# 50. PostgreSQL vs MySQL vs Cassandra

| Feature | PostgreSQL | MySQL | Cassandra |
|---|---|---|---|
| Storage | Heap | Clustered B+Tree | LSM |
| Transactions | Strong | Strong | Limited |
| Scaling | Vertical mostly | Vertical mostly | Horizontal |
| Consistency | Strong | Strong | Tunable/Eventual |
| Writes | Good | Good | Excellent |
| Joins | Excellent | Good | Weak |
| Distributed Scale | Moderate | Moderate | Excellent |

---

# 51. PostgreSQL Read Mental Model

```text
Index lookup
      ↓
Heap page fetch
      ↓
MVCC visibility check
      ↓
Return tuple
```

---

# 52. MySQL Read Mental Model

```text
Secondary index lookup
      ↓
Primary key lookup
      ↓
Clustered row fetch
```

---

# 53. Cassandra Read Mental Model

```text
MemTable
      ↓
Bloom Filter
      ↓
SSTables
      ↓
Merge latest version
```

---

# 54. PostgreSQL Write Mental Model

```text
Write WAL
      ↓
Create new tuple version
      ↓
Mark old tuple dead
      ↓
VACUUM cleanup later
```

---

# 55. MySQL Write Mental Model

```text
Write redo log
      ↓
Update clustered index
      ↓
Store undo log
      ↓
Flush pages later
```

---

# 56. Cassandra Write Mental Model

```text
Append commit log
      ↓
Update MemTable
      ↓
Flush immutable SSTable
      ↓
Compaction later
```

---

# 57. Why PostgreSQL Good For Backend Systems

Excellent for:

```text
banking
payments
complex transactions
analytics
multi-table joins
strict correctness
```

---

# 58. Why MySQL Good For Web Systems

Excellent for:

```text
CRUD systems
ecommerce
content systems
OLTP
read-heavy apps
```

---

# 59. Why Cassandra Good For Massive Scale

Excellent for:

```text
time-series
IoT
event ingestion
social feeds
high write systems
global distributed systems
```

---

# 60. Java PostgreSQL Mental Model

```java
SELECT *
FROM users
WHERE email = ?;
```

Execution:

```text
B+Tree lookup
      ↓
Get heap tuple location
      ↓
Read tuple
      ↓
MVCC visibility check
```

---

# 61. Java Cassandra Mental Model

```java
session.execute(
    "SELECT * FROM users WHERE id=?"
);
```

Execution:

```text
Partition key routing
      ↓
Target node
      ↓
MemTable/SSTable lookup
```

---

# 62. Full PostgreSQL Dry Run

Query:

```sql
UPDATE users
SET balance=500
WHERE id=1;
```

Execution:

```text
Write WAL
      ↓
Create new tuple version
      ↓
Old tuple marked dead
      ↓
Commit
      ↓
VACUUM removes dead tuple later
```

---

# 63. Full MySQL Dry Run

```sql
UPDATE users
SET balance=500
WHERE id=1;
```

Execution:

```text
Write redo log
      ↓
Store undo log
      ↓
Update clustered index row
      ↓
Commit
```

---

# 64. Full Cassandra Dry Run

```text
INSERT user
```

Execution:

```text
Append commit log
      ↓
Update MemTable
      ↓
ACK client
      ↓
Flush SSTable later
      ↓
Compaction eventually
```

---

# 65. Production Notes

Use PostgreSQL when you need:

```text
strong transactions
complex SQL
joins
analytics
correctness
```

Use MySQL when you need:

```text
simple operational systems
OLTP
large web ecosystems
```

Use Cassandra when you need:

```text
massive horizontal scale
high write throughput
global distributed workloads
```

---

# 66. Common Mistakes

## Mistake 1

```text
Thinking all databases store rows same way
```

Storage engines differ massively.

---

## Mistake 2

```text
Using Cassandra for heavy joins
```

Cassandra optimized differently.

---

## Mistake 3

```text
Ignoring VACUUM in PostgreSQL
```

Dead tuples accumulate.

---

## Mistake 4

```text
Ignoring compaction in Cassandra
```

Read amplification becomes severe.

---

## Mistake 5

```text
Choosing database only by popularity
```

Choose based on workload pattern.

---

# 67. Interview Explanation — PostgreSQL

If interviewer asks:

```text
How does PostgreSQL store data?
```

Strong answer:

```text
PostgreSQL stores rows in heap pages and uses indexes that point to tuple
locations. MVCC creates new tuple versions during updates, while VACUUM
cleans old dead tuples later.
```

---

# 68. Interview Explanation — MySQL

If interviewer asks:

```text
What is clustered index in MySQL?
```

Strong answer:

```text
InnoDB stores table rows directly inside the primary key B+Tree leaf nodes.
This is called a clustered index because the table data itself is clustered
with the primary key index.
```

---

# 69. Interview Explanation — Cassandra

If interviewer asks:

```text
Why is Cassandra fast for writes?
```

Strong answer:

```text
Cassandra uses an LSM-tree architecture where writes are appended sequentially
to a commit log and stored in MemTables before flushing immutable SSTables.
This avoids expensive random in-place updates.
```

---

# 70. Final Mental Model

```text
PostgreSQL
    =
heap + MVCC + WAL + B+Tree

MySQL InnoDB
    =
clustered index + redo/undo + buffer pool

Cassandra
    =
LSM + MemTable + SSTable + compaction
```

Different databases optimize for different workloads.

---

# 71. What To Remember

```text
PostgreSQL uses heap storage.

MySQL InnoDB uses clustered indexes.

Cassandra uses LSM trees and SSTables.

Postgres MVCC creates tuple versions.

MySQL MVCC uses undo logs.

Cassandra optimizes sequential writes.

VACUUM removes dead tuples.

Compaction merges SSTables.

Bloom filters reduce unnecessary reads.
```

---

# 72. Next File

```text
019_Production_Grade_Database.md
```

Next you learn:

```text
backup
restore
HA
failover
pooling
observability
slow query tuning
connection scaling
monitoring
real production database operations
```
