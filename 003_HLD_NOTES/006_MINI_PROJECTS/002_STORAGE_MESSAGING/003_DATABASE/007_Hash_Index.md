# 007_Hash_Index.md

# MiniDatabase — 007 Hash Index

# 0. Why This File Exists

Without indexes, databases become slow.

Suppose table has:

```text
100 million rows
```

Query:

```sql
SELECT * FROM users WHERE id = 100;
```

Without index:

```text
scan all rows one by one
```

Very slow.

Indexes solve this problem.

This file teaches:

```text
what index is
why hash index exists
how exact-match lookup works
HashMap mental model
bucket mapping
collisions
index maintenance
insert/update/delete with index
production tradeoffs
```

This is the first real indexing file.

Very important foundation before:

```text
BTree indexes
LSM trees
query optimization
buffer pool
storage engines
```

---

# 1. One-Line Definition

```text
A hash index maps a key directly to record location using hashing.
```

Simple meaning:

```text
key → fast lookup
```

---

# 2. Biggest Mental Model

Without index:

```text
Find row
    ↓
scan entire table
```

With hash index:

```text
Find row
    ↓
hash key
    ↓
jump directly to location
```

Huge speed difference.

---

# 3. Full Table Scan Problem

Suppose:

```text
users table
```

| id | name |
|---|---|
| 1 | Mohamed |
| 2 | John |
| 3 | Alice |
| 4 | Bob |

Query:

```sql
SELECT * FROM users WHERE id = 4;
```

Without index:

```text
check row-1
check row-2
check row-3
check row-4
```

This is:

```text
O(N)
```

---

# 4. Why Index Exists

Index creates shortcut.

Mental model:

```text
book index page
```

Instead of reading whole book.

---

# 5. Hash Index Mental Model

Hash index works like:

```text
HashMap<Key, RecordLocation>
```

Example:

```text
1 → RID(10,1)
2 → RID(11,3)
3 → RID(12,5)
```

RID means:

```text
Record ID
(pageId, slotId)
```

---

# 6. ASCII Hash Index Diagram

```text
Hash Index

+---------+----------------+
| Key     | Record Pointer |
+---------+----------------+
| 1       | RID(10,1)      |
| 2       | RID(11,3)      |
| 3       | RID(12,5)      |
+---------+----------------+
```

---

# 7. Query Flow With Hash Index

Query:

```sql
SELECT * FROM users WHERE id = 3;
```

Flow:

```text
key = 3
   ↓
hash(3)
   ↓
find bucket
   ↓
get RID(12,5)
   ↓
load page 12
   ↓
read slot 5
   ↓
decode record bytes
   ↓
return row
```

---

# 8. Big-O Mental Model

## Without Index

```text
O(N)
```

Need full scan.

---

## With Hash Index

Average:

```text
O(1)
```

Very fast exact lookup.

---

# 9. What Is Hash Function?

Hash function converts:

```text
key → bucket number
```

Example:

```text
hash(101) = 5
hash(202) = 2
hash(303) = 9
```

---

# 10. Bucket Mental Model

Buckets store index entries.

Example:

```text
Bucket-0
Bucket-1
Bucket-2
Bucket-3
...
```

Hash decides:

```text
which bucket stores key
```

---

# 11. Simple Bucket Formula

Simplified example:

```text
bucket = key % totalBuckets
```

Example:

```text
totalBuckets = 10

101 % 10 = 1
205 % 10 = 5
312 % 10 = 2
```

---

# 12. ASCII Bucket Diagram

```text
Buckets

Bucket-0 → []
Bucket-1 → [101]
Bucket-2 → [312]
Bucket-3 → []
Bucket-4 → []
Bucket-5 → [205]
```

---

# 13. Hash Index Internal Structure

Conceptually:

```text
Hash Index
    ↓
Bucket Array
    ↓
Bucket
    ↓
Index Entry
    ↓
RID(pageId, slotId)
```

---

# 14. Index Entry Structure

Example entry:

```text
(key=101, RID=(10,2))
```

Meaning:

```text
key 101 row lives at:
page 10 slot 2
```

---

# 15. Hash Collision Problem

Two different keys may map to same bucket.

Example:

```text
101 % 10 = 1
111 % 10 = 1
121 % 10 = 1
```

All collide.

---

# 16. Collision Mental Model

```text
different keys
same bucket
```

This is collision.

---

# 17. Collision ASCII Diagram

```text
Bucket-1

+-------------------+
| 101 → RID(10,1)   |
| 111 → RID(20,3)   |
| 121 → RID(30,2)   |
+-------------------+
```

---

# 18. Collision Resolution — Chaining

Most common strategy:

```text
bucket stores linked list / array of entries
```

Called:

```text
separate chaining
```

---

# 19. Collision Lookup Flow

Query:

```text
find key=111
```

Flow:

```text
hash(111)=1
    ↓
go to Bucket-1
    ↓
scan entries inside bucket
    ↓
find key=111
```

Still fast if collisions low.

---

# 20. Why Good Hash Function Matters

Bad hash function:

```text
many collisions
```

Then:

```text
hash index becomes slow
```

Good hash function distributes keys evenly.

---

# 21. Hash Table Load Factor

Load factor:

```text
entries / buckets
```

Example:

```text
1000 entries
100 buckets
```

Load factor:

```text
10
```

Higher load factor:

```text
more collisions
slower lookup
```

---

# 22. Rehashing

When hash table grows too large:

```text
create larger bucket array
rehash all keys
```

This is:

```text
rehashing
```

---

# 23. Rehash Flow

```text
Old buckets
    ↓
allocate larger bucket array
    ↓
recompute bucket for each key
    ↓
move entries
```

Expensive operation.

---

# 24. Hash Index Exact Match Strength

Hash indexes excellent for:

```sql
WHERE id = 10
WHERE email = 'abc@gmail.com'
```

Exact equality lookup.

---

# 25. Hash Index Weakness

Hash indexes are BAD for:

```sql
WHERE age > 20
WHERE id BETWEEN 10 AND 100
ORDER BY id
```

Because hash destroys ordering.

---

# 26. Why Hash Index Cannot Handle Range Efficiently

Example:

```text
keys:
1 2 3 4 5 6 7 8
```

After hashing:

```text
5 → bucket 1
1 → bucket 3
8 → bucket 0
2 → bucket 7
```

Ordering lost.

---

# 27. Exact Match vs Range Query

## Excellent

```text
WHERE id = X
```

## Poor

```text
WHERE id > X
BETWEEN
ORDER BY
prefix scans
```

This is why BTree indexes are more common.

---

# 28. Hash Index Memory Representation

Conceptually:

```text
Bucket Array In Memory

+-----------+
| Bucket-0  | ----> entries
+-----------+
| Bucket-1  | ----> entries
+-----------+
| Bucket-2  | ----> entries
+-----------+
```

Each bucket stores index entries.

---

# 29. Java Hash Index Example

```java
import java.util.HashMap;
import java.util.Map;

public class HashIndex {

    private final Map<Integer, String> index =
            new HashMap<>();

    public void put(int key, String rid) {

        index.put(key, rid);
    }

    public String get(int key) {

        return index.get(key);
    }

    public void remove(int key) {

        index.remove(key);
    }
}
```

Simplified idea.

---

# 30. Insert With Hash Index

Insert row:

```text
{id=10, name=Mohamed}
```

Flow:

```text
insert record into page
    ↓
record gets RID(100,2)
    ↓
hash index insert:
10 → RID(100,2)
```

---

# 31. Insert ASCII Flow

```text
New Row
   ↓
Store record in page
   ↓
RID(100,2)
   ↓
Hash Index:
10 → RID(100,2)
```

---

# 32. Update With Hash Index

If indexed column changes:

```sql
UPDATE users SET id=20 WHERE id=10;
```

Need:

```text
remove old index entry
add new index entry
```

---

# 33. Update Index Flow

```text
old key=10
new key=20

remove:
10 → RID(100,2)

insert:
20 → RID(100,2)
```

---

# 34. Delete With Hash Index

Delete row:

```text
remove table record
remove index entry
```

Flow:

```text
delete row RID(100,2)
    ↓
remove key=10 from hash index
```

---

# 35. Hash Index + Table Mental Model

```text
Hash Index
     ↓
RID(page, slot)
     ↓
Table Page
     ↓
Record Bytes
```

Index stores:

```text
location
```

not full row usually.

---

# 36. Unique Hash Index

Unique index prevents duplicates.

Example:

```sql
CREATE UNIQUE INDEX idx_email
ON users(email);
```

Now:

```text
same email cannot exist twice
```

---

# 37. Duplicate Key Check Flow

Insert:

```text
email = abc@gmail.com
```

Flow:

```text
hash(email)
   ↓
lookup bucket
   ↓
existing key found?
   ↓
reject duplicate
```

---

# 38. Composite Hash Index

Index can use multiple columns.

Example:

```text
(firstName, lastName)
```

Key may become:

```text
"Mohamed|Thaiseer"
```

Then hashed.

---

# 39. In-Memory vs Disk Hash Index

## In-Memory

Very fast.

Example:

```text
Java HashMap
Redis dictionary
```

---

## Disk-Based

Needs:

```text
page management
overflow buckets
buffer pool
crash safety
WAL
```

Much more complex.

---

# 40. Extendible Hashing

Advanced hash index design.

Supports:

```text
dynamic growth
```

without full rehash every time.

Uses:

```text
directory
bucket splitting
```

Used in some databases.

---

# 41. Linear Hashing

Another dynamic hashing strategy.

Benefits:

```text
incremental bucket growth
```

Avoids expensive full rehash.

---

# 42. Why BTree Usually More Popular

BTree supports:

```text
exact lookup
range queries
sorting
prefix scans
ordered traversal
```

Hash index only excellent for:

```text
exact equality lookup
```

---

# 43. Real Database Examples

## PostgreSQL

Supports:

```text
Hash Index
BTree
GIN
GiST
BRIN
```

Default:

```text
BTree
```

---

## Redis

Internally heavily uses:

```text
hash tables
```

for key lookup.

---

## Java HashMap

Conceptually same idea:

```text
key
  ↓
hash
  ↓
bucket
  ↓
value
```

---

# 44. Production Tradeoffs

## Advantages

```text
very fast equality lookup
simple concept
average O(1)
excellent for key-value access
```

## Disadvantages

```text
poor range queries
collisions
rehashing overhead
ordering lost
```

---

# 45. Query Examples

## Excellent Query

```sql
SELECT * FROM users
WHERE id = 100;
```

---

## Bad Query

```sql
SELECT * FROM users
WHERE id > 100;
```

---

# 46. Full Query Flow Example

Query:

```sql
SELECT * FROM users WHERE id = 101;
```

Flow:

```text
hash(101)
    ↓
bucket-5
    ↓
find RID(20,3)
    ↓
load page 20
    ↓
read slot 3
    ↓
decode record bytes
    ↓
return row
```

---

# 47. Time Complexity Summary

| Operation | Average |
|---|---|
| Insert | O(1) |
| Lookup | O(1) |
| Delete | O(1) |
| Range Query | Poor |

Worst-case with many collisions:

```text
O(N)
```

---

# 48. Hash Index vs Full Scan

## Full Scan

```text
scan all rows
```

Cost:

```text
O(N)
```

---

## Hash Index

```text
direct bucket lookup
```

Cost:

```text
O(1) average
```

Huge performance gain.

---

# 49. Backend System Design Connection

When API calls:

```text
GET /users/100
```

Database often uses:

```text
primary key index
```

to avoid full scan.

Without indexes:

```text
high latency
high CPU
slow APIs
```

---

# 50. Interview Explanation

If interviewer asks:

```text
What is a hash index?
```

Strong answer:

```text
A hash index uses a hash function to map keys directly to record locations,
allowing very fast exact-match lookups with average O(1) complexity.
```

Senior addition:

```text
Hash indexes are excellent for equality predicates but poor for range queries
because hashing destroys key ordering.
```

---

# 51. Common Mistakes

## Mistake 1

```text
Thinking hash indexes support efficient range queries
```

Wrong.

---

## Mistake 2

```text
Ignoring collisions
```

Collisions heavily affect performance.

---

## Mistake 3

```text
Thinking index stores full row always
```

Usually stores:

```text
key + pointer/RID
```

---

## Mistake 4

```text
Ignoring index maintenance during writes
```

Writes become more expensive with indexes.

---

## Mistake 5

```text
Thinking O(1) always guaranteed
```

Worst-case collisions degrade performance.

---

# 52. Final Mental Model

```text
Hash Index
    ↓
hash(key)
    ↓
bucket
    ↓
index entry
    ↓
RID(page, slot)
    ↓
record bytes
```

---

# 53. What To Remember

```text
Hash indexes optimize exact-match lookup.

key → hash → bucket → RID → record

Advantages:
fast equality search
simple lookup

Disadvantages:
poor range queries
collisions
ordering lost

Indexes improve reads but increase write cost.
```

---

# 54. Next File

```text
008_BTree_vs_LSMTree.md
```

Next you learn:

```text
why BTree dominates SQL databases
why LSM trees dominate write-heavy systems
page splits
sorted indexes
SSTables
compaction
read vs write optimization
```
