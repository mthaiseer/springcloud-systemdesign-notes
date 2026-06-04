# 006_Insert_Update_Delete.md

# MiniDatabase — 006 Insert Update Delete

## 0. Why This File Exists

You already learned:

```text
Table
Row
Record
Binary encoding
Page storage preview
```

Now we learn the most common database write operations:

```text
INSERT
UPDATE
DELETE
```

Most developers only see:

```sql
INSERT INTO users ...
UPDATE users SET ...
DELETE FROM users ...
```

But internally a database must handle:

```text
record creation
page selection
free space
index update
WAL logging
transaction metadata
visibility
tombstones
fragmentation
compaction
crash safety
```

This file teaches the internal write path mental model.

---

# 1. One-Line Definition

```text
Insert, update, and delete are database write operations
that modify table records safely and efficiently.
```

Simple meaning:

```text
INSERT = add record
UPDATE = change record
DELETE = remove or mark record deleted
```

---

# 2. Big Mental Model

```text
SQL Write
    ↓
Database Engine
    ↓
Record Operation
    ↓
Page Modification
    ↓
Index Update
    ↓
WAL Log
    ↓
Commit
```

A database write is much more than changing one value.

---

# 3. CRUD Write Operations

```text
INSERT → create new row
UPDATE → modify existing row
DELETE → remove existing row
```

Read operation:

```text
SELECT → retrieve row
```

This file focuses on writes.

---

# 4. Logical vs Physical Write

## Logical View

```sql
UPDATE users SET age = 31 WHERE id = 1;
```

Looks simple.

## Physical View

```text
find row
check visibility
lock row
create WAL record
modify page or create new version
update index if needed
commit transaction
```

Real database write path is complex.

---

# 5. Example Table

```text
users
```

| id | name | city | age |
|---|---|---|---|
| 1 | Mohamed | Bucharest | 30 |
| 2 | John | London | 28 |

---

# 6. INSERT Mental Model

```text
INSERT = create new record and place it into storage
```

High-level flow:

```text
New row
  ↓
Validate schema
  ↓
Encode as record
  ↓
Find page with space
  ↓
Write record
  ↓
Update indexes
  ↓
Log change
```

---

# 7. INSERT ASCII Flow

```text
INSERT user{id=3, name=Alice}
        ↓
Validate columns/types
        ↓
Serialize row to record bytes
        ↓
Find page with free space
        ↓
Place record into page
        ↓
Add slot entry
        ↓
Update primary key index
        ↓
Write WAL
        ↓
Commit
```

---

# 8. INSERT Step-by-Step

Operation:

```sql
INSERT INTO users(id, name, city, age)
VALUES (3, 'Alice', 'Paris', 25);
```

Steps:

```text
1. Parse SQL
2. Validate table exists
3. Validate column names
4. Validate data types
5. Check primary key duplicate
6. Convert row into record bytes
7. Find target page
8. Insert record into page
9. Update indexes
10. Write WAL
11. Commit transaction
```

---

# 9. INSERT Into In-Memory Table

In our mini version:

```text
Map<PrimaryKey, Row>
```

Insert is simple:

```text
rows.put(primaryKey, row)
```

But real database insert is:

```text
page + record + index + WAL
```

---

# 10. Java Insert Example

```java
public void insert(Row row) {

    Object primaryKeyValue =
            row.get(primaryKeyColumn);

    if (primaryKeyValue == null) {
        throw new IllegalArgumentException(
                "Primary key missing"
        );
    }

    if (rows.containsKey(primaryKeyValue)) {
        throw new IllegalArgumentException(
                "Duplicate primary key"
        );
    }

    rows.put(primaryKeyValue, row);
}
```

---

# 11. Insert Dry Run

Initial:

```text
rows = {
  1 → Mohamed
  2 → John
}
```

Insert:

```text
{id=3, name=Alice}
```

Flow:

```text
extract id = 3
check id exists? no
put 3 → Alice
```

Result:

```text
rows = {
  1 → Mohamed
  2 → John
  3 → Alice
}
```

---

# 12. Real Insert Page Flow

Assume:

```text
Page-10 has free space
```

Insert record:

```text
Record{id=3, name=Alice}
```

Flow:

```text
Page-10
  ↓
free space found
  ↓
record bytes copied into page
  ↓
slot directory updated
```

---

# 13. Slotted Page Insert Diagram

Before:

```text
+----------------------+
| Page Header          |
+----------------------+
| Slot-0 -> Record-A   |
| Slot-1 -> Record-B   |
+----------------------+
| Free Space           |
+----------------------+
| Record-B             |
| Record-A             |
+----------------------+
```

After inserting Record-C:

```text
+----------------------+
| Page Header          |
+----------------------+
| Slot-0 -> Record-A   |
| Slot-1 -> Record-B   |
| Slot-2 -> Record-C   |
+----------------------+
| Free Space           |
+----------------------+
| Record-C             |
| Record-B             |
| Record-A             |
+----------------------+
```

---

# 14. INSERT Failure Cases

Insert can fail because:

```text
primary key duplicate
schema mismatch
type mismatch
constraint violation
page full
disk full
transaction conflict
```

---

# 15. UPDATE Mental Model

```text
UPDATE = find existing record and change its value
```

But internally update is tricky.

Because new record may be:

```text
same size
smaller
larger
```

Different cases.

---

# 16. UPDATE Logical Flow

```text
Find row
  ↓
Lock/check visibility
  ↓
Modify value
  ↓
Write WAL
  ↓
Update indexes if indexed column changed
  ↓
Commit
```

---

# 17. UPDATE SQL Example

```sql
UPDATE users
SET age = 31
WHERE id = 1;
```

Looks simple.

Internally:

```text
find id=1
change age 30 → 31
log update
commit
```

---

# 18. Java Update Example

```java
public void update(Object id,
                   String columnName,
                   Object newValue) {

    Row row = rows.get(id);

    if (row == null) {
        throw new IllegalArgumentException(
                "Row not found"
        );
    }

    row.put(columnName, newValue);
}
```

---

# 19. Update Dry Run

Initial:

```text
1 → {id=1, name=Mohamed, age=30}
```

Update:

```text
age = 31
```

Flow:

```text
find key 1
row found
replace age value
```

Result:

```text
1 → {id=1, name=Mohamed, age=31}
```

---

# 20. Real UPDATE Case 1 — Same Size

Example:

```text
age 30 → 31
```

Both are INT.

Same size.

Database may:

```text
modify in-place
```

Flow:

```text
find record bytes
overwrite age bytes
write WAL
```

---

# 21. Same-Size Update Diagram

Before:

```text
[id=1][name=Mohamed][age=30]
```

After:

```text
[id=1][name=Mohamed][age=31]
```

Only age bytes changed.

---

# 22. Real UPDATE Case 2 — Larger Value

Example:

```text
name = John
```

becomes:

```text
name = Mohamed
```

New value larger.

Old space may not be enough.

Database options:

```text
relocate record
create new version
overflow storage
page split
```

---

# 23. Larger Update Diagram

Before:

```text
[id=2][name_length=4][John][age=28]
```

After:

```text
[id=2][name_length=8][Mohamed][age=28]
```

More bytes needed.

This may not fit in same location.

---

# 24. UPDATE With Relocation

If record does not fit:

```text
old location marked stale
new record written elsewhere
pointer/index updated
```

This creates:

```text
fragmentation
write amplification
```

---

# 25. UPDATE In MVCC Systems

In MVCC databases like Postgres:

```text
UPDATE often creates a new row version
```

Mental model:

```text
old version remains
new version inserted
visibility metadata decides who sees what
```

---

# 26. MVCC Update Diagram

```text
Before:

Tuple-v1: age=30, visible

UPDATE age=31

After:

Tuple-v1: age=30, old version
Tuple-v2: age=31, new version
```

Readers already using old snapshot may still see:

```text
age=30
```

New transactions see:

```text
age=31
```

This enables concurrency.

---

# 27. Why UPDATE Can Be Expensive

Update may require:

```text
row lookup
record rewrite
index update
WAL write
new version creation
cleanup later
```

Not just changing one field.

---

# 28. Indexed Column Update

Suppose index exists on:

```text
email
```

Update:

```sql
UPDATE users SET email='new@email.com' WHERE id=1;
```

Database must update:

```text
table record
email index
WAL
```

---

# 29. Indexed Update Flow

```text
Find row by id
    ↓
old email = old@email.com
    ↓
remove old email from index
    ↓
write new email in record
    ↓
add new email to index
```

More work.

---

# 30. DELETE Mental Model

```text
DELETE = remove row logically or physically
```

Important:

```text
delete does not always immediately remove bytes
```

---

# 31. DELETE SQL Example

```sql
DELETE FROM users
WHERE id = 2;
```

Logical meaning:

```text
remove user id=2
```

Physical meaning may be:

```text
mark record deleted
```

---

# 32. Java Delete Example

```java
public void delete(Object id) {

    rows.remove(id);
}
```

In-memory delete is simple.

Real database delete is more complex.

---

# 33. Delete Dry Run

Initial:

```text
rows = {
  1 → Mohamed
  2 → John
}
```

Delete id=2:

```text
remove key 2
```

Result:

```text
rows = {
  1 → Mohamed
}
```

---

# 34. Real DELETE Strategies

Databases may use:

```text
physical delete
logical delete
tombstone
MVCC delete marker
```

---

# 35. Physical Delete

Immediately remove record bytes.

Pros:

```text
space reclaimed immediately
```

Cons:

```text
hard with concurrent readers
expensive to compact page
```

---

# 36. Logical Delete

Mark record as deleted:

```text
deleted = true
```

Actual cleanup later.

Pros:

```text
safe with concurrency
fast delete
```

Cons:

```text
dead records accumulate
needs cleanup
```

---

# 37. Tombstone

A tombstone is:

```text
delete marker
```

Common in:

```text
Cassandra
LSM tree systems
distributed databases
```

Meaning:

```text
this key was deleted
```

---

# 38. Tombstone Diagram

```text
Original record:
id=2, name=John

Delete:
id=2, TOMBSTONE
```

During reads:

```text
if tombstone exists
treat row as deleted
```

---

# 39. Why Tombstones Are Needed

In distributed systems:

```text
replicas may not all receive delete immediately
```

Tombstone tells late replicas:

```text
this data should be deleted
```

Without tombstone, old value may reappear.

---

# 40. DELETE In MVCC

In MVCC database:

```text
DELETE marks row version as no longer visible
```

Old transaction may still see old row.

New transaction does not.

---

# 41. MVCC Delete Diagram

```text
Tuple-v1: visible to old transaction

DELETE happens

Tuple-v1: marked deleted by transaction X
```

Visibility rules decide who sees it.

---

# 42. Cleanup / Vacuum

Deleted records need cleanup later.

Examples:

```text
Postgres VACUUM
LSM compaction
storage compaction
```

Purpose:

```text
remove dead records
reclaim space
improve performance
```

---

# 43. Write Amplification

One logical write may cause multiple physical writes.

Example update may write:

```text
WAL
data page
index page
metadata
replica log
```

This is:

```text
write amplification
```

---

# 44. Write Amplification Diagram

```text
UPDATE user age
      ↓
WAL write
      ↓
table page write
      ↓
index page write
      ↓
replication write
      ↓
cleanup later
```

One logical update becomes many writes.

---

# 45. WAL Preview

Before changing actual data page, database writes:

```text
WAL record
```

Why?

```text
crash recovery
durability
```

Flow:

```text
write WAL
    ↓
modify page
    ↓
commit
```

Detailed later.

---

# 46. WAL Write Path

```text
User UPDATE
    ↓
Generate WAL record
    ↓
Flush WAL safely
    ↓
Apply page change
    ↓
Commit success
```

If crash happens:

```text
WAL used to recover
```

---

# 47. Insert/Update/Delete With WAL

## Insert

```text
log inserted record
```

## Update

```text
log old/new change
```

## Delete

```text
log delete marker
```

WAL allows replay after crash.

---

# 48. Index Maintenance

Writes often affect indexes.

## Insert

```text
add index entry
```

## Delete

```text
remove or mark index entry dead
```

## Update indexed column

```text
delete old index entry
insert new index entry
```

---

# 49. Why Indexes Slow Writes

Indexes speed up reads.

But every write must update indexes.

Tradeoff:

```text
more indexes = faster reads but slower writes
```

Very important production rule.

---

# 50. Insert With Index Flow

```text
Insert row
   ↓
write table record
   ↓
update primary key index
   ↓
update secondary indexes
   ↓
write WAL
```

---

# 51. Update With Index Flow

```text
Update row
   ↓
check which columns changed
   ↓
if indexed column changed:
      update index
   ↓
write WAL
   ↓
commit
```

---

# 52. Delete With Index Flow

```text
Delete row
   ↓
mark table record deleted
   ↓
remove/mark index entries
   ↓
write WAL
   ↓
cleanup later
```

---

# 53. Concurrency During Writes

Multiple users may write same row.

Need:

```text
locks
MVCC
transactions
isolation levels
```

Example:

```text
two users update same balance
```

Without control:

```text
lost update
```

---

# 54. Lost Update Example

Initial:

```text
balance = 100
```

Transaction A:

```text
read 100
add 50
write 150
```

Transaction B:

```text
read 100
subtract 20
write 80
```

Expected final:

```text
130
```

Possible wrong final:

```text
80 or 150
```

Need isolation.

---

# 55. Database Locking Preview

For writes, databases may use:

```text
row locks
page locks
table locks
intent locks
```

Goal:

```text
prevent conflicting writes
```

---

# 56. Insert/Update/Delete In Real Backend

Spring Boot flow:

```text
Controller
   ↓
Service
   ↓
Repository.save()
   ↓
SQL INSERT/UPDATE/DELETE
   ↓
Database write path
   ↓
commit
```

ORM hides complexity.

Database still does all internal work.

---

# 57. JPA Save Mental Model

```java
repository.save(entity);
```

may become:

```sql
INSERT
```

or:

```sql
UPDATE
```

depending on entity state.

---

# 58. Production Problem — Too Many Updates

Frequent updates cause:

```text
WAL growth
index churn
page dirtying
replication traffic
vacuum pressure
```

This can slow database.

---

# 59. Production Problem — Delete Storm

Mass delete:

```sql
DELETE FROM logs WHERE created_at < ...
```

Can create:

```text
many dead tuples
large WAL
replication lag
vacuum pressure
locks
```

Better sometimes:

```text
partition drop
batch delete
TTL table design
```

---

# 60. Production Problem — Too Many Indexes

Too many indexes:

```text
read queries may improve
writes become slower
storage increases
maintenance cost increases
```

Index design is a tradeoff.

---

# 61. Batch Writes

Instead of:

```text
one insert per request
```

batching can reduce overhead.

Example:

```text
insert 1000 rows together
```

Benefits:

```text
less network roundtrip
better WAL batching
better throughput
```

---

# 62. Write Path Summary

```text
SQL Write
    ↓
Validate
    ↓
Find/allocate record location
    ↓
Modify table data
    ↓
Update indexes
    ↓
Write WAL
    ↓
Commit
    ↓
Cleanup later if needed
```

---

# 63. Java Mini Write Engine

```java
import java.util.HashMap;
import java.util.Map;

public class MiniWriteTable {

    private final Map<Object, Row> rows =
            new HashMap<>();

    private final String primaryKeyColumn;

    public MiniWriteTable(String primaryKeyColumn) {
        this.primaryKeyColumn = primaryKeyColumn;
    }

    public void insert(Row row) {

        Object id = row.get(primaryKeyColumn);

        if (id == null) {
            throw new IllegalArgumentException("Missing PK");
        }

        if (rows.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate PK");
        }

        rows.put(id, row);
    }

    public void update(Object id,
                       String column,
                       Object value) {

        Row row = rows.get(id);

        if (row == null) {
            throw new IllegalArgumentException("Row not found");
        }

        row.put(column, value);
    }

    public void delete(Object id) {
        rows.remove(id);
    }

    public Row find(Object id) {
        return rows.get(id);
    }
}
```

---

# 64. Mini Engine Limitation

This simple engine lacks:

```text
WAL
durability
transaction rollback
index maintenance
MVCC
locking
page storage
replication
```

But it teaches:

```text
core write operation flow
```

---

# 65. Interview Explanation

If interviewer asks:

```text
What happens internally during insert/update/delete?
```

Strong answer:

```text
A database validates the operation, locates or allocates a record,
modifies table storage, updates relevant indexes, writes WAL for durability,
and commits the transaction. Updates and deletes may create new versions or
tombstones depending on the storage engine.
```

Senior addition:

```text
Writes are expensive because one logical change can affect data pages,
index pages, WAL, MVCC metadata, replication logs, and cleanup systems.
```

---

# 66. Common Mistakes

## Mistake 1

```text
Thinking UPDATE only changes one value
```

Wrong.

It may update:

```text
record
indexes
WAL
MVCC metadata
```

---

## Mistake 2

```text
Thinking DELETE immediately removes data
```

Often false.

Many databases mark deleted and clean later.

---

## Mistake 3

```text
Ignoring index write cost
```

Indexes speed reads but slow writes.

---

## Mistake 4

```text
Ignoring WAL
```

WAL is critical for crash safety.

---

## Mistake 5

```text
Ignoring write amplification
```

One write may cause many physical writes.

---

# 67. Final Mental Model

```text
INSERT
  = new record + indexes + WAL

UPDATE
  = find record + change/new version + indexes + WAL

DELETE
  = mark/remove record + indexes + WAL + cleanup later
```

---

# 68. What To Remember

```text
Database writes are not simple memory changes.

Each write may involve:
record bytes
pages
indexes
WAL
locks
MVCC
cleanup
replication

More indexes improve reads but slow writes.

Deletes often create tombstones/dead records.

Updates may create new record versions.
```

---

# 69. Next File

```text
007_Hash_Index.md
```

Next you learn:

```text
why indexes exist
hash index mental model
primary key lookup
exact match search
HashMap-backed index
index maintenance during writes
```
