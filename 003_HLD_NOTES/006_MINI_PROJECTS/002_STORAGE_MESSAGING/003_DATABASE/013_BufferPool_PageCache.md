# 013_BufferPool_PageCache.md

# MiniDatabase — 013 BufferPool PageCache

## 0. Why This File Exists

Databases are fast because they do NOT read disk for every query.

Disk is slow.

RAM is fast.

So databases keep frequently used pages in memory using:

```text
Buffer Pool
Page Cache
```

This file teaches:

```text
why buffer pool exists
how page cache works
cache hit vs cache miss
dirty pages
page eviction
LRU policy
flush to disk
checkpoint preview
why RAM matters in database performance
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
Buffer Pool is database-managed RAM cache for disk pages.
```

Simple meaning:

```text
Buffer Pool = database memory cache for pages
```

---

# 2. Biggest Mental Model

Database file lives on disk.

Queries need pages.

Instead of reading disk every time:

```text
Database keeps hot pages in RAM.
```

Flow:

```text
Query needs page
    ↓
Check buffer pool
    ↓
If page in RAM → fast
    ↓
If not in RAM → read from disk
```

---

# 3. Disk vs RAM Mental Model

```text
RAM
    fast
    temporary
    limited

Disk
    slower
    persistent
    larger
```

Database uses both:

```text
RAM for speed
Disk for durability
```

---

# 4. Database File To Page To Buffer Pool

```text
Disk File
│
├── Page-0
├── Page-1
├── Page-2
├── Page-3
└── Page-4
```

Buffer pool keeps some pages in memory:

```text
Buffer Pool RAM
│
├── Frame-1 → Page-2
├── Frame-2 → Page-4
└── Frame-3 → Page-0
```

---

# 5. Full Storage Access Flow

```text
Query
  ↓
Execution Engine
  ↓
Storage Engine
  ↓
Buffer Pool
  ↓
Disk File if page missing
```

---

# 6. ASCII — Buffer Pool Architecture

```text
+----------------------+
| Execution Engine     |
+----------------------+
           ↓
+----------------------+
| Storage Engine       |
+----------------------+
           ↓
+----------------------+
| Buffer Pool          |
| RAM page cache       |
+----------------------+
           ↓
+----------------------+
| Disk Files           |
| persistent pages     |
+----------------------+
```

---

# 7. What Is A Page?

A page is fixed-size block of data.

Common sizes:

```text
4 KB
8 KB
16 KB
```

Page contains:

```text
records
slot directory
page header
free space
```

---

# 8. Page Mental Model

```text
Page
│
├── Page Header
├── Slot Directory
├── Free Space
└── Record Bytes
```

Database usually reads/writes whole pages.

---

# 9. Buffer Pool Frame

Buffer pool consists of frames.

Each frame can hold one page.

```text
Buffer Pool
│
├── Frame-0
├── Frame-1
├── Frame-2
└── Frame-3
```

If page size is 8 KB:

```text
each frame = 8 KB
```

---

# 10. Frame ASCII

```text
+----------------------+
| Frame-0              |
| holds Page-100       |
+----------------------+

+----------------------+
| Frame-1              |
| holds Page-205       |
+----------------------+
```

---

# 11. Page Table

Database needs mapping:

```text
pageId → frameId
```

Example:

```text
100 → Frame-0
205 → Frame-1
300 → Frame-2
```

This helps quickly check:

```text
is page already in memory?
```

---

# 12. Page Table ASCII

```text
Page Table

+--------+---------+
| PageId | FrameId |
+--------+---------+
| 100    | 0       |
| 205    | 1       |
| 300    | 2       |
+--------+---------+
```

---

# 13. Cache Hit

Cache hit means:

```text
requested page already exists in buffer pool
```

Flow:

```text
Need Page-100
      ↓
Page Table lookup
      ↓
Page-100 found in Frame-0
      ↓
Return page from RAM
```

Fast.

---

# 14. Cache Hit ASCII

```text
Need Page-100
      ↓
Page Table
      ↓
100 → Frame-0
      ↓
Return Frame-0 from RAM
```

No disk read.

---

# 15. Cache Miss

Cache miss means:

```text
requested page not in buffer pool
```

Flow:

```text
Need Page-500
      ↓
Page Table lookup
      ↓
Not found
      ↓
Read Page-500 from disk
      ↓
Put into buffer pool
      ↓
Return page
```

Slow.

---

# 16. Cache Miss ASCII

```text
Need Page-500
      ↓
Page Table
      ↓
not found
      ↓
Disk read
      ↓
Load into free frame
      ↓
Return page
```

---

# 17. Why Cache Hit Ratio Matters

High cache hit ratio means:

```text
most pages served from RAM
```

Low cache hit ratio means:

```text
many disk reads
slow queries
```

Important metric:

```text
cache hit ratio
```

---

# 18. Cache Hit Ratio Formula

```text
cache_hit_ratio = cache_hits / total_page_requests
```

Example:

```text
900 hits / 1000 requests = 90%
```

Higher is better.

---

# 19. Hot Pages

Hot pages are:

```text
frequently accessed pages
```

Examples:

```text
popular user records
recent orders
index root pages
active BTree leaf pages
```

Buffer pool keeps hot pages in RAM.

---

# 20. Cold Pages

Cold pages are:

```text
rarely accessed pages
```

These are good candidates for eviction.

---

# 21. Eviction

Buffer pool has limited memory.

When full and new page needed:

```text
evict one old page
```

Need replacement policy:

```text
which page should be removed?
```

---

# 22. LRU Policy

LRU means:

```text
Least Recently Used
```

Evict page that was not used recently.

Mental model:

```text
recently used pages stay
old unused pages removed
```

---

# 23. LRU ASCII

Access order:

```text
Page-1 → Page-2 → Page-3
```

If Page-1 not used for long:

```text
evict Page-1 first
```

---

# 24. LRU List Mental Model

```text
Most Recent                           Least Recent
+--------+     +--------+     +--------+     +--------+
| Page-7 | --> | Page-3 | --> | Page-9 | --> | Page-1 |
+--------+     +--------+     +--------+     +--------+
                                                   ↑
                                                evict
```

---

# 25. Buffer Pool Full Example

Buffer pool size:

```text
3 frames
```

Current pages:

```text
Frame-0 → Page-1
Frame-1 → Page-2
Frame-2 → Page-3
```

Need:

```text
Page-4
```

No free frame.

Evict least recently used page.

---

# 26. Eviction Dry Run

Initial:

```text
Buffer Pool:
[Page-1, Page-2, Page-3]

LRU order:
Page-1 oldest
Page-3 newest
```

Need Page-4.

Evict Page-1:

```text
Buffer Pool:
[Page-4, Page-2, Page-3]
```

---

# 27. Dirty Page

Dirty page means:

```text
page modified in memory but not yet written to disk
```

Example:

```sql
UPDATE users SET age=31 WHERE id=10;
```

Database modifies page in buffer pool.

Disk page may still have old value.

---

# 28. Dirty Page ASCII

```text
Disk Page-100:
age = 30

Buffer Pool Page-100:
age = 31
dirty = true
```

Dirty means:

```text
RAM copy newer than disk copy
```

---

# 29. Clean Page

Clean page means:

```text
RAM page same as disk page
```

Can be evicted safely without writing.

---

# 30. Evicting Clean Page

```text
Clean page
    ↓
evict immediately
```

No disk write needed.

---

# 31. Evicting Dirty Page

```text
Dirty page
    ↓
must flush to disk first
    ↓
then evict
```

Otherwise changes are lost.

---

# 32. Dirty Page Eviction ASCII

```text
Need free frame
      ↓
Chosen victim is dirty
      ↓
Write page to disk
      ↓
Mark clean
      ↓
Evict page
      ↓
Load new page
```

---

# 33. WAL Connection

Before dirty page is flushed, database must ensure:

```text
WAL is written first
```

Rule:

```text
WAL before data page
```

This protects crash recovery.

---

# 34. WAL + Dirty Page Flow

```text
UPDATE row
    ↓
Write WAL record
    ↓
Modify page in buffer pool
    ↓
Page becomes dirty
    ↓
Later flush dirty page to disk
```

---

# 35. Why Database Does Not Immediately Write Page

Writing every update immediately to disk is slow.

Instead database:

```text
updates page in RAM
marks dirty
flushes later
```

This improves performance.

---

# 36. Flush

Flush means:

```text
write dirty page from RAM to disk
```

Flow:

```text
Dirty Page in Buffer Pool
        ↓
Write to Disk File
        ↓
Mark page clean
```

---

# 37. Flush ASCII

```text
Buffer Pool Page-100
dirty = true
        ↓
write to disk
        ↓
Disk Page-100 updated
        ↓
dirty = false
```

---

# 38. Checkpoint Preview

Checkpoint is process where database:

```text
flushes dirty pages
records safe recovery point
```

Purpose:

```text
reduce crash recovery time
```

Detailed later.

---

# 39. Checkpoint ASCII

```text
Dirty Pages:
Page-10
Page-20
Page-30
        ↓
Flush to disk
        ↓
Record checkpoint
        ↓
Recovery can start from checkpoint
```

---

# 40. Buffer Pool Read Path

Query:

```sql
SELECT * FROM users WHERE id = 10;
```

Flow:

```text
Index finds RID(page=100, slot=3)
        ↓
Storage engine asks buffer pool for Page-100
        ↓
If hit → return page
        ↓
If miss → read disk
        ↓
Decode slot 3
```

---

# 41. Read Path ASCII

```text
Query id=10
    ↓
Index lookup
    ↓
RID(page=100, slot=3)
    ↓
Buffer Pool
    ├── hit  → return page from RAM
    └── miss → read page from disk
    ↓
Slot-3
    ↓
Record bytes
    ↓
Row
```

---

# 42. Buffer Pool Write Path

Query:

```sql
UPDATE users SET age=31 WHERE id=10;
```

Flow:

```text
Find page
    ↓
Load page into buffer pool
    ↓
Write WAL
    ↓
Modify page in RAM
    ↓
Mark page dirty
    ↓
Commit
    ↓
Flush later
```

---

# 43. Write Path ASCII

```text
UPDATE row
    ↓
Find Page-100
    ↓
Load page into buffer pool
    ↓
Write WAL
    ↓
Modify record bytes in RAM
    ↓
Mark Page-100 dirty
    ↓
Return success after commit
    ↓
Flush dirty page later
```

---

# 44. Why Buffer Pool Is Not Same As OS Page Cache

Operating system also caches disk pages.

But database buffer pool is:

```text
database-controlled
aware of transactions
aware of dirty pages
aware of WAL
aware of eviction policy
aware of page format
```

OS page cache is generic.

---

# 45. Database Buffer Pool vs OS Cache

| Feature | Buffer Pool | OS Page Cache |
|---|---|---|
| Controlled by | Database | Operating system |
| Knows DB pages | Yes | No |
| Knows dirty DB pages | Yes | Generic |
| Knows WAL rule | Yes | No |
| Used for query planning | Indirectly | No |
| Purpose | DB performance | General file caching |

---

# 46. Why Index Root Pages Stay Hot

BTree root page is accessed very frequently.

Example:

```text
every index lookup starts at root
```

So root page usually remains in buffer pool.

---

# 47. Hot Index Pages ASCII

```text
BTree Index

Root Page          ← very hot
    ↓
Internal Pages     ← hot
    ↓
Leaf Pages         ← mixed hot/cold
```

Buffer pool usually keeps:

```text
root/internal pages in RAM
```

---

# 48. Buffer Pool And Sequential Scan

Large table scan may read many pages once.

Problem:

```text
can evict hot pages
```

This is why databases may use special scan strategies.

---

# 49. Sequential Scan Pollution

```text
Hot pages in buffer pool:
A B C

Large scan reads:
P1 P2 P3 P4 P5 P6 ...

If not careful:
hot pages A B C get evicted
```

This hurts future queries.

---

# 50. Production Problem — Buffer Pool Too Small

Symptoms:

```text
low cache hit ratio
high disk read IOPS
slow queries
high latency
```

Fixes:

```text
increase RAM
optimize queries
add indexes
reduce working set
partition data
```

---

# 51. Working Set

Working set means:

```text
data pages actively used by workload
```

If working set fits in buffer pool:

```text
queries are fast
```

If not:

```text
many disk reads
```

---

# 52. Working Set ASCII

```text
Total database size:
1 TB

Frequently used data:
20 GB

Buffer pool:
32 GB

Result:
working set fits → good performance
```

Bad:

```text
working set:
100 GB

buffer pool:
32 GB

Result:
cache misses → slow queries
```

---

# 53. Java Mini Buffer Pool

Simplified model:

```java
import java.util.LinkedHashMap;
import java.util.Map;

public class MiniBufferPool {

    private final int capacity;

    private final Map<Integer, Page> cache;

    public MiniBufferPool(int capacity) {

        this.capacity = capacity;

        this.cache =
                new LinkedHashMap<>(
                        capacity,
                        0.75f,
                        true
                ) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<Integer, Page> eldest) {

                        return size() > MiniBufferPool.this.capacity;
                    }
                };
    }

    public Page getPage(int pageId) {

        return cache.get(pageId);
    }

    public void putPage(int pageId, Page page) {

        cache.put(pageId, page);
    }
}
```

---

# 54. Java Page Class

```java
public class Page {

    private final int pageId;
    private boolean dirty;

    public Page(int pageId) {
        this.pageId = pageId;
    }

    public int getPageId() {
        return pageId;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void markClean() {
        this.dirty = false;
    }
}
```

---

# 55. Java Dry Run

Buffer pool capacity:

```text
3 pages
```

Access:

```text
Page-1
Page-2
Page-3
Page-4
```

Execution:

```text
put Page-1
put Page-2
put Page-3

buffer full:
[1,2,3]

put Page-4

evict least recently used Page-1

buffer:
[2,3,4]
```

---

# 56. Production Tuning Notes

Important settings:

```text
buffer pool size
shared buffers
cache hit ratio
dirty page flush rate
checkpoint frequency
IO capacity
```

Examples:

```text
Postgres shared_buffers
MySQL InnoDB buffer pool
```

---

# 57. Common Slow Query Causes Related To Buffer Pool

```text
cold cache
large table scans
buffer pool too small
too many random reads
working set larger than RAM
bad indexes
```

---

# 58. Interview Explanation

If interviewer asks:

```text
What is buffer pool?
```

Strong answer:

```text
A buffer pool is a database-managed memory area that caches disk pages in RAM.
When a query needs a page, the database first checks the buffer pool.
If the page is present, it is a cache hit; otherwise the page is read from disk.
```

Senior addition:

```text
Modified pages become dirty and are flushed later, but WAL must be persisted
before dirty data pages are written to disk to guarantee crash recovery.
```

---

# 59. Common Mistakes

## Mistake 1

```text
Thinking database reads disk for every query
```

Actually buffer pool caches hot pages.

---

## Mistake 2

```text
Ignoring dirty pages
```

Writes usually modify RAM first and flush later.

---

## Mistake 3

```text
Thinking cache hit ratio does not matter
```

It is critical for performance.

---

## Mistake 4

```text
Confusing OS cache and DB buffer pool
```

They are related but not same.

---

## Mistake 5

```text
Thinking eviction is always safe
```

Dirty pages must be flushed before eviction.

---

# 60. Final Mental Model

```text
Disk File
    ↓
Pages
    ↓
Buffer Pool Frames in RAM
    ↓
Execution Engine reads/writes pages
```

Read:

```text
Need page
    ↓
hit → RAM
miss → disk
```

Write:

```text
WAL
    ↓
modify page in RAM
    ↓
mark dirty
    ↓
flush later
```

---

# 61. What To Remember

```text
Buffer pool caches disk pages in RAM.

Cache hit = page found in RAM.

Cache miss = page must be read from disk.

Dirty page = modified in RAM but not flushed to disk.

LRU evicts least recently used pages.

Dirty pages must be flushed before eviction.

WAL protects dirty page crash recovery.

Buffer pool size heavily affects database performance.
```

---

# 62. Next File

```text
014_Write_Ahead_Log_WAL.md
```

Next you learn:

```text
WAL
durability
redo log
crash recovery
commit flow
checkpoint
why log is written before data page
```
