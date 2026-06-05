# 014_Write_Ahead_Log_WAL.md

# MiniDatabase — 014 Write Ahead Log WAL

## 0. Why This File Exists

Databases promise:

```text
Your committed data will survive crashes.
```

But database pages are stored in RAM first.

Question:

```text
What happens if power fails before dirty pages reach disk?
```

Answer:

```text
WAL (Write Ahead Log)
```

WAL is one of the most important concepts in databases.

This file teaches:

```text
why WAL exists
how durability works
redo logging
crash recovery
commit flow
dirty pages
checkpoint
LSN
STEAL / NO-STEAL
FORCE / NO-FORCE
ARIES intuition
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
WAL means log changes before writing data pages.
```

Simple rule:

```text
WAL must reach disk BEFORE dirty data pages.
```

This guarantees crash recovery.

---

# 2. Biggest Mental Model

Without WAL:

```text
update page in RAM
    ↓
crash before disk flush
    ↓
data lost
```

With WAL:

```text
write change to WAL first
    ↓
flush WAL to disk
    ↓
modify page in RAM
    ↓
crash happens
    ↓
recover changes from WAL
```

---

# 3. Why WAL Exists

Disk writes are slow.

Database prefers:

```text
modify pages in RAM
flush later
```

But RAM is volatile.

Crash loses RAM contents.

WAL solves this.

---

# 4. WAL Mental Model

```text
Data pages
    =
actual database state

WAL
    =
history of changes
```

If pages lost:

```text
rebuild using WAL
```

---

# 5. Banking Example

Suppose:

```sql
UPDATE accounts
SET balance = balance - 100
WHERE id = 1;
```

Flow without WAL:

```text
modify page in RAM
    ↓
power failure
    ↓
RAM lost
    ↓
transaction lost
```

Bad.

---

# 6. WAL Fix

With WAL:

```text
write "subtract 100" into WAL
    ↓
flush WAL to disk
    ↓
modify page in RAM
    ↓
power failure
    ↓
replay WAL during recovery
```

Transaction survives.

---

# 7. WAL Rule

Golden rule:

```text
WAL BEFORE DATA PAGE
```

Always.

---

# 8. WAL Architecture

```text
+----------------------+
| Transactions         |
+----------------------+
           ↓
+----------------------+
| WAL Buffer           |
+----------------------+
           ↓ flush
+----------------------+
| WAL File             |
+----------------------+

Data Pages:
Buffer Pool
    ↓ later
Disk Pages
```

---

# 9. WAL File

WAL is append-only log.

Example:

```text
LSN=1 UPDATE user age=30→31
LSN=2 INSERT order id=100
LSN=3 DELETE payment id=10
```

Database appends sequentially.

Sequential disk writes are fast.

---

# 10. WAL ASCII

```text
WAL File

+----------------------+
| LSN=1 UPDATE row     |
+----------------------+
| LSN=2 INSERT row     |
+----------------------+
| LSN=3 DELETE row     |
+----------------------+
```

---

# 11. Why Sequential Logging Is Fast

Random page writes are expensive.

WAL writes:

```text
append sequentially
```

Sequential writes are much faster.

---

# 12. Buffer Pool + WAL

Data pages live in buffer pool.

Changes first modify:

```text
RAM page
```

But before dirty page flush:

```text
WAL must already be durable
```

---

# 13. Full Update Flow

Query:

```sql
UPDATE users
SET age = 31
WHERE id = 10;
```

Execution:

```text
Find page
    ↓
Write WAL record
    ↓
Flush WAL to disk
    ↓
Modify page in buffer pool
    ↓
Mark page dirty
    ↓
Commit transaction
    ↓
Flush dirty page later
```

---

# 14. WAL Update ASCII

```text
UPDATE row
    ↓
Create WAL record
    ↓
WAL buffer
    ↓
Flush WAL to disk
    ↓
Modify page in RAM
    ↓
Page becomes dirty
    ↓
Later flush dirty page
```

---

# 15. Why WAL Before Dirty Page

Imagine wrong order:

```text
write dirty page first
    ↓
crash before WAL written
```

Recovery impossible.

Database would not know:

```text
which transaction created page state
```

So:

```text
WAL first
data page later
```

---

# 16. Commit Does NOT Mean Page Flush

Very important.

Commit usually means:

```text
WAL durable
```

NOT:

```text
all dirty pages flushed
```

---

# 17. Commit Mental Model

```text
Transaction COMMIT
    ↓
WAL safely persisted
    ↓
client gets success
    ↓
dirty pages may still remain only in RAM
```

This surprises many developers.

---

# 18. Dirty Page Example

Disk page:

```text
age = 30
```

RAM page:

```text
age = 31
dirty = true
```

WAL already contains:

```text
UPDATE age 30→31
```

Crash happens.

Recovery can replay WAL.

---

# 19. WAL Recovery Mental Model

```text
Disk pages may be old
WAL contains latest committed changes
```

Recovery:

```text
reapply WAL changes
```

---

# 20. REDO Logging

Most WAL systems use:

```text
REDO logging
```

Meaning:

```text
replay committed changes again
```

during crash recovery.

---

# 21. REDO ASCII

Crash:

```text
Disk still has:
age=30
```

WAL:

```text
SET age=31
```

Recovery:

```text
REDO update
```

Final:

```text
age=31
```

---

# 22. LSN (Log Sequence Number)

Every WAL record has:

```text
LSN
```

LSN means:

```text
position/order in WAL stream
```

Example:

```text
LSN=100
LSN=101
LSN=102
```

---

# 23. Why LSN Important

Database tracks:

```text
which WAL records already applied
```

using LSN.

Each page stores:

```text
pageLSN
```

---

# 24. pageLSN Mental Model

Page stores:

```text
last WAL record applied to page
```

Example:

```text
pageLSN = 101
```

Meaning:

```text
all WAL records up to 101 already applied
```

---

# 25. Recovery Uses pageLSN

During recovery:

```text
If WAL.LSN > pageLSN
    → REDO required
Else
    → skip
```

---

# 26. WAL Recovery ASCII

```text
Disk Page:
pageLSN = 100

WAL contains:
101 UPDATE age=31
102 INSERT order

Recovery:
apply 101
apply 102
```

---

# 27. WAL Buffer

Database usually writes WAL first into memory buffer.

```text
Transactions
    ↓
WAL Buffer
    ↓
flush to WAL file
```

Allows batching writes.

---

# 28. WAL Flush

Flush means:

```text
force WAL bytes to persistent storage
```

Important:

```text
OS memory buffer is not enough
```

Need durable flush.

---

# 29. Group Commit

Multiple transactions may share one WAL flush.

Instead of:

```text
flush per transaction
```

Database may:

```text
batch commits together
```

Huge optimization.

---

# 30. Group Commit ASCII

```text
Txn-1 COMMIT
Txn-2 COMMIT
Txn-3 COMMIT
        ↓
single WAL flush
        ↓
all transactions committed
```

---

# 31. STEAL vs NO-STEAL

STEAL policy:

```text
dirty pages may flush before commit
```

NO-STEAL:

```text
dirty pages never flush before commit
```

Most real databases use:

```text
STEAL
```

because it is more efficient.

---

# 32. FORCE vs NO-FORCE

FORCE:

```text
flush all dirty pages at commit
```

NO-FORCE:

```text
do not force dirty pages at commit
```

Most real databases use:

```text
NO-FORCE
```

for performance.

---

# 33. Why Modern Databases Use STEAL + NO-FORCE

Benefits:

```text
better throughput
less waiting
better memory usage
fewer forced writes
```

But requires:

```text
WAL + recovery system
```

---

# 34. STEAL + NO-FORCE ASCII

```text
Transaction modifies page
        ↓
Dirty page may flush before commit
        ↓
Commit may happen before page flush
        ↓
Recovery uses WAL to fix state
```

---

# 35. Checkpoint

Checkpoint creates recovery starting point.

Database:

```text
flushes some dirty pages
records checkpoint LSN
```

Recovery can start later.

---

# 36. Checkpoint ASCII

```text
WAL:
100
101
102
103
104

Checkpoint at 103
```

Recovery starts near:

```text
103
```

instead of replaying entire history.

---

# 37. Crash Recovery Flow

After crash:

```text
Read checkpoint
    ↓
Scan WAL records
    ↓
Redo committed changes
    ↓
Restore consistent state
```

---

# 38. Crash Recovery ASCII

```text
System crash
      ↓
Restart database
      ↓
Find latest checkpoint
      ↓
Read WAL after checkpoint
      ↓
Replay changes
      ↓
Database consistent again
```

---

# 39. ARIES Intuition

Many databases use ideas from:

```text
ARIES recovery algorithm
```

Main ideas:

```text
WAL
LSN
redo
undo
checkpoint
```

Deep internals later.

---

# 40. WAL vs Replication Log

WAL can also support:

```text
replication
CDC
streaming replicas
backup recovery
```

Replica reads WAL stream.

Replays changes.

---

# 41. WAL Replication ASCII

```text
Primary DB
    ↓ generates WAL
Replica DB
    ↓ reads WAL stream
Replay changes
```

Replica becomes consistent.

---

# 42. Backend API Example

Spring Boot API:

```text
POST /transfer
```

Execution:

```text
Transaction starts
    ↓
UPDATE account A
    ↓
UPDATE account B
    ↓
Generate WAL records
    ↓
Flush WAL
    ↓
COMMIT success
    ↓
Dirty pages flush later
```

---

# 43. Why WAL Is Critical For ACID

WAL mainly guarantees:

```text
Durability
```

from ACID.

Meaning:

```text
committed transaction survives crash
```

---

# 44. WAL Performance Benefit

Without WAL:

```text
random page flush per update
```

Very slow.

With WAL:

```text
append sequential log
```

Much faster.

---

# 45. WAL Write Path

```text
Transaction
    ↓
Generate WAL record
    ↓
Append to WAL buffer
    ↓
Flush WAL to disk
    ↓
Commit acknowledged
```

---

# 46. Data Page Flush Path

Separate flow:

```text
Dirty page
    ↓
Background flusher
    ↓
Write page to disk
    ↓
Mark clean
```

---

# 47. WAL + Buffer Pool Together

```text
WAL
    =
durability history

Buffer Pool
    =
fast in-memory pages
```

Both work together.

---

# 48. Java WAL Record Example

```java
public class WalRecord {

    private long lsn;
    private String operation;
    private int pageId;

    public WalRecord(long lsn,
                     String operation,
                     int pageId) {

        this.lsn = lsn;
        this.operation = operation;
        this.pageId = pageId;
    }
}
```

---

# 49. Java WAL Append Example

```java
List<WalRecord> wal =
        new ArrayList<>();

wal.add(
        new WalRecord(
                100,
                "UPDATE age=31",
                10
        )
);
```

---

# 50. Java Recovery Example

```java
for (WalRecord record : walRecords) {

    apply(record);
}
```

Simple REDO replay.

---

# 51. Full WAL Dry Run

Query:

```sql
UPDATE users
SET age=31
WHERE id=10;
```

Initial:

```text
Disk page:
age=30

Buffer pool:
age=30
```

Execution:

```text
1. Create WAL record
2. Append WAL
3. Flush WAL to disk
4. Modify RAM page age=31
5. Mark page dirty
6. COMMIT success
```

Crash now happens.

Disk page still:

```text
age=30
```

Recovery:

```text
Read WAL
Replay UPDATE age=31
```

Final disk state:

```text
age=31
```

Durability preserved.

---

# 52. Production Performance Notes

Important WAL metrics:

```text
WAL write throughput
flush latency
checkpoint frequency
replication lag
dirty page generation rate
```

---

# 53. Common Slow WAL Problems

```text
slow disk sync
too many commits
checkpoint storms
WAL disk full
replication lag
```

---

# 54. WAL Device Best Practice

Many systems place WAL on:

```text
fast SSD/NVMe
```

Because commit latency depends heavily on WAL flush speed.

---

# 55. Interview Explanation

If interviewer asks:

```text
What is WAL?
```

Strong answer:

```text
Write Ahead Logging is a durability mechanism where database changes are
first written to a sequential log before dirty data pages are flushed to disk.
If the system crashes, recovery replays WAL records to restore committed changes.
```

Senior addition:

```text
Modern databases typically use STEAL + NO-FORCE policies,
which require WAL and recovery algorithms such as ARIES.
```

---

# 56. Common Mistakes

## Mistake 1

```text
Thinking COMMIT means page flushed
```

Usually commit means:

```text
WAL durable
```

---

## Mistake 2

```text
Thinking WAL stores full pages always
```

Often WAL stores logical or physical changes.

---

## Mistake 3

```text
Ignoring WAL flush latency
```

Commit performance heavily depends on WAL flush.

---

## Mistake 4

```text
Confusing WAL with backup
```

WAL helps recovery but is not complete backup system.

---

## Mistake 5

```text
Thinking dirty pages immediately reach disk
```

Usually flushed later.

---

# 57. Final Mental Model

```text
WAL
    =
history of changes

Buffer Pool
    =
latest pages in RAM

Disk Pages
    =
persistent database state
```

Flow:

```text
WAL first
    ↓
modify RAM page
    ↓
flush page later
    ↓
recover using WAL if crash happens
```

---

# 58. What To Remember

```text
WAL means write log before data page.

WAL guarantees durability.

Commit usually means WAL durable.

Dirty pages may still exist only in RAM.

Recovery replays WAL records.

LSN tracks WAL order.

Modern databases use STEAL + NO-FORCE.

WAL sequential writes are fast.
```

---

# 59. Next File

```text
015_Transactions_ACID_MVCC.md
```

Next you learn:

```text
transactions
ACID
MVCC
snapshot isolation
read committed
repeatable read
serializable
phantom reads
```
