# 005_Table_Row_Record_Model.md

# MiniDatabase — 005 Table Row Record Model

## 0. Why This File Exists

In the previous file we built:

```text
InMemoryTable
    ↓
Map<PrimaryKey, Row>
    ↓
CRUD operations
```

That is good for understanding logical tables.

But real databases do NOT store rows as:

```text
Java objects
HashMaps
JSON objects
```

Real databases store:

```text
binary records
inside pages
inside files
on disk
```

This file teaches the bridge between:

```text
logical table row
        ↓
physical database record
```

This is one of the most important storage-engine foundations.

You need this before learning:

```text
006_Insert_Update_Delete.md
007_Hash_Index.md
008_BTree_vs_LSMTree.md
013_Page_Block_Storage.md
014_BufferPool_Cache.md
015_Write_Ahead_Log_WAL.md
017_Isolation_MVCC_Locking.md
```

---

# 1. One-Line Definition

```text
A row is the logical view of data.
A record is the physical binary representation stored by the database.
```

Simple meaning:

```text
Row = what user sees
Record = what database stores
```

---

# 2. Biggest Mental Model

Application thinks:

```text
{id=1, name=Mohamed, age=30}
```

Database thinks:

```text
bytes + offsets + metadata + page location
```

That mindset shift is VERY important.

---

# 3. Logical vs Physical View

## Logical View

```text
Table
 ↓
Rows
 ↓
Columns
 ↓
Values
```

## Physical View

```text
File
 ↓
Pages
 ↓
Records
 ↓
Bytes
```

---

# 4. Full Storage Flow

```text
Application Object
        ↓
Logical Row
        ↓
Record Encoding
        ↓
Binary Record
        ↓
Page
        ↓
Database File
        ↓
Disk
```

This is the core storage-engine path.

---

# 5. Example Logical Table

```text
users
```

| id | name | city | age |
|---|---|---|---|
| 1 | Mohamed | Bucharest | 30 |
| 2 | John | London | 28 |

Application sees:

```text
rows and columns
```

Database stores:

```text
encoded records
```

---

# 6. Row Mental Model

A row is:

```text
one logical entity in a table
```

Examples:

```text
one user
one order
one payment
one product
one transaction
```

Example row:

```text
{id=1, name=Mohamed, city=Bucharest, age=30}
```

---

# 7. Record Mental Model

A record is:

```text
row converted into storage-friendly format
```

Example:

```text
[record header][null bitmap][fixed fields][variable fields]
```

Real databases need this structure because:

```text
data must be read/written as bytes
```

---

# 8. Why Database Cannot Store Java Objects Directly

Java objects contain:

```text
object headers
references
JVM-specific memory layout
pointers
GC metadata
```

These are not suitable for:

```text
portable storage
disk persistence
crash recovery
cross-language access
compact layout
```

So database creates its own binary format.

---

# 9. Basic Record Layout

For table:

```text
users(id INT, name VARCHAR, age INT)
```

A simple record format:

```text
[id][name_length][name_bytes][age]
```

Example:

```text
[1][8][Mohamed][30]
```

---

# 10. ASCII Record Diagram

```text
+---------+-------------+-------------+---------+
| id      | name_length | name_bytes  | age     |
| 4 bytes | 4 bytes     | 8 bytes     | 4 bytes |
+---------+-------------+-------------+---------+
```

This is simplified but gives the core idea.

---

# 11. Why Field Types Matter

Each column type maps to storage size.

Examples:

| Type | Typical Size |
|---|---:|
| INT | 4 bytes |
| BIGINT | 8 bytes |
| BOOLEAN | 1 byte |
| DOUBLE | 8 bytes |
| VARCHAR | variable |
| TEXT | variable |
| JSON | variable |

Fixed-length fields are easier.

Variable-length fields require metadata.

---

# 12. Fixed-Length Field Model

Example:

```text
id INT
age INT
```

Each is known size:

```text
4 bytes
```

So database can calculate offsets easily.

---

# 13. Fixed-Length Record Example

Table:

```text
users(id INT, age INT)
```

Record:

```text
[id][age]
```

Layout:

```text
+---------+---------+
| id      | age     |
| 4 bytes | 4 bytes |
+---------+---------+
```

Total:

```text
8 bytes
```

Very easy.

---

# 14. Variable-Length Field Problem

Table:

```text
users(id INT, name VARCHAR, city VARCHAR)
```

Rows:

```text
{id=1, name=Mohamed, city=Bucharest}
{id=2, name=Li, city=Paris}
```

Different string lengths.

Database must know:

```text
where name ends
where city starts
where city ends
```

---

# 15. Variable-Length Record Layout

Common strategy:

```text
[header][fixed fields][offset table][variable data]
```

Example:

```text
[id][age][name_offset][city_offset][Mohamed][Bucharest]
```

The offsets tell where variable fields start.

---

# 16. ASCII Variable-Length Layout

```text
+--------+--------+-------------+-------------+----------+-----------+
| id     | age    | name_offset | city_offset | name     | city      |
+--------+--------+-------------+-------------+----------+-----------+
| 1      | 30     | 20          | 28          | Mohamed  | Bucharest |
+--------+--------+-------------+-------------+----------+-----------+
```

This makes parsing possible.

---

# 17. Null Values Problem

Rows may contain null values.

Example:

```text
{id=1, name=Mohamed, city=NULL, age=30}
```

Database must know:

```text
city is not stored because it is NULL
```

Need:

```text
null bitmap
```

---

# 18. Null Bitmap Mental Model

A null bitmap says:

```text
which columns are null
```

Example:

```text
columns: id, name, city, age
bitmap:  0   0     1     0
```

Meaning:

```text
city is NULL
```

---

# 19. ASCII Null Bitmap Layout

```text
+---------------+---------+----------+---------+
| Null Bitmap   | id      | name     | age     |
| 0010          | 1       | Mohamed  | 30      |
+---------------+---------+----------+---------+
```

The city value is absent because city is null.

---

# 20. Record Header

Real records usually include a header.

Header may store:

```text
record length
flags
null bitmap position
transaction metadata
version metadata
delete marker
```

Example simplified layout:

```text
[record_header][null_bitmap][field_data]
```

---

# 21. Complete Record Layout Mental Model

```text
+------------------+
| Record Header    |
+------------------+
| Null Bitmap      |
+------------------+
| Fixed Fields     |
+------------------+
| Offset Array     |
+------------------+
| Variable Fields  |
+------------------+
```

This is closer to real storage.

---

# 22. Why Metadata Exists

Metadata helps database answer:

```text
How long is this record?
Which fields are null?
Where are variable fields?
Is this row visible to this transaction?
Was this row deleted?
```

Without metadata, database cannot safely parse records.

---

# 23. Row ID / Record ID

Databases often identify records using:

```text
page id + slot id
```

Example:

```text
(page=100, slot=5)
```

This tells database:

```text
go to page 100
find slot 5
read record
```

---

# 24. Record ID Mental Model

```text
RecordID = physical address of record
```

Common abstraction:

```text
RID = PageId + SlotId
```

ASCII:

```text
RID(100, 5)
   ↓
Page 100
   ↓
Slot 5
   ↓
Record bytes
```

---

# 25. Disk Page Preview

Databases do not read single records from disk one by one.

They read:

```text
pages / blocks
```

Example:

```text
8 KB page
16 KB page
```

A page contains many records.

---

# 26. Page Mental Model

```text
Database File
      ↓
Page-1
Page-2
Page-3
      ↓
Records inside each page
```

---

# 27. ASCII Page With Records

```text
+--------------------------------------+
| Page Header                          |
+--------------------------------------+
| Record-1                             |
| Record-2                             |
| Record-3                             |
| Record-4                             |
+--------------------------------------+
```

This is simplified.

Real pages often use slot directories.

---

# 28. Slotted Page Model

Very important database concept.

A page contains:

```text
page header
slot directory
free space
record data
```

---

# 29. ASCII Slotted Page Diagram

```text
+------------------------------------------------+
| Page Header                                    |
+------------------------------------------------+
| Slot Directory                                 |
| slot 0 -> offset 7800                          |
| slot 1 -> offset 7650                          |
| slot 2 -> offset 7480                          |
+------------------------------------------------+
| Free Space                                     |
|                                                |
|                                                |
+------------------------------------------------+
| Record-2 bytes                                 |
| Record-1 bytes                                 |
| Record-0 bytes                                 |
+------------------------------------------------+
```

Slots grow from top.

Records grow from bottom.

Free space remains in middle.

---

# 30. Why Slotted Page Is Useful

Records may be variable length.

If records move inside page, index can still refer to:

```text
slot id
```

not exact byte offset.

Slot directory updates offset.

This gives stable references.

---

# 31. Insert Into Slotted Page Flow

```text
New record arrives
       ↓
check free space
       ↓
write record bytes at free-space end
       ↓
add slot entry pointing to record offset
       ↓
return RecordID(pageId, slotId)
```

---

# 32. Insert ASCII Flow

```text
Before:

+------------------+
| Header           |
| Slots            |
| Free Space       |
| Record-A         |
+------------------+

Insert Record-B:

+------------------+
| Header           |
| Slots + slot-B   |
| Free Space       |
| Record-B         |
| Record-A         |
+------------------+
```

---

# 33. Select By RecordID Flow

```text
RecordID(page=10, slot=2)
        ↓
load page 10
        ↓
read slot 2
        ↓
get byte offset
        ↓
decode record bytes
        ↓
return row
```

This is how physical lookup works.

---

# 34. Update Problem

Suppose record:

```text
name = John
```

becomes:

```text
name = Mohamed
```

The record becomes larger.

Database options:

```text
update in-place if space available
move record to new location
create new version
mark old record dead
```

This depends on storage engine.

---

# 35. Update In-Place

If new record fits same space:

```text
overwrite bytes
```

Fast.

But if new value longer:

```text
may not fit
```

---

# 36. Update With Relocation

If record grows too large:

```text
move record elsewhere
update pointer
leave forwarding reference
or create new version
```

This can increase complexity and fragmentation.

---

# 37. Delete Problem

Deleting record can be done by:

```text
immediate physical removal
logical delete marker
tombstone
MVCC dead tuple
```

Immediate removal is not always safe because:

```text
other transactions may still need old version
```

---

# 38. Tombstone Mental Model

```text
delete does not remove immediately
delete marks record as deleted
cleanup happens later
```

Used heavily in:

```text
Cassandra
LSM trees
MVCC systems
```

---

# 39. Fragmentation

Repeated operations:

```text
insert
update
delete
```

create holes in pages.

This is called:

```text
fragmentation
```

---

# 40. Fragmentation ASCII

```text
+------------------+
| Record-A         |
| FREE GAP         |
| Record-C         |
| FREE GAP         |
| Record-E         |
+------------------+
```

This wastes space.

---

# 41. Vacuum / Compaction

Databases need cleanup.

Examples:

```text
Postgres VACUUM
LSM compaction
storage defragmentation
```

Goal:

```text
reclaim space
remove dead records
improve scan efficiency
```

---

# 42. Postgres Heap Tuple Mental Model

Postgres stores rows as:

```text
heap tuples
```

Tuple contains:

```text
tuple header
transaction metadata
null bitmap
column values
```

Important MVCC fields:

```text
xmin
xmax
```

These help decide:

```text
which transaction can see this row
```

---

# 43. Postgres MVCC Preview

When updating a row, Postgres often creates:

```text
new tuple version
```

Old tuple may remain until cleanup.

Mental model:

```text
UPDATE = create new version + mark old version obsolete
```

This enables readers and writers to work concurrently.

---

# 44. InnoDB Record Mental Model

MySQL InnoDB stores rows inside:

```text
clustered index pages
```

Primary key affects physical ordering.

Mental model:

```text
table data is organized by primary key
```

This is why primary key choice matters in InnoDB.

---

# 45. Cassandra / LSM Record Mental Model

Cassandra does not update in-place like classic row store.

Instead:

```text
append new writes
flush SSTables
compact later
```

Delete uses:

```text
tombstones
```

This is why Cassandra is write-optimized.

---

# 46. Record Layout vs Performance

Record design affects:

```text
disk IO
CPU cache efficiency
compression
scan performance
update cost
storage size
```

Good storage layout is critical.

---

# 47. Cache Locality

If related bytes are close together:

```text
CPU cache works better
```

If scattered randomly:

```text
more cache misses
slower execution
```

This is why layout matters.

---

# 48. Alignment and Padding

Low-level systems may align values.

Example:

```text
8-byte values may be aligned on 8-byte boundaries
```

This can improve CPU access but may waste space.

---

# 49. Compression Preview

Databases compress records/pages using:

```text
dictionary compression
delta encoding
run-length encoding
prefix compression
```

Columnar databases compress especially well because similar values are stored together.

---

# 50. Row Store vs Column Store Connection

## Row Store

```text
Record contains all column values together
```

Good for:

```text
OLTP
full-row reads
frequent updates
```

## Column Store

```text
Each column stored separately
```

Good for:

```text
OLAP
analytics
compression
aggregations
```

---

# 51. Java User Class

```java
public class User {

    private final int id;
    private final String name;
    private final int age;

    public User(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
```

Application-friendly object.

Database still needs bytes.

---

# 52. Java Serializer

```java
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class UserRecordSerializer {

    public byte[] serialize(User user) {

        byte[] nameBytes =
                user.getName().getBytes(StandardCharsets.UTF_8);

        int recordSize =
                4 +                 // id
                4 +                 // name length
                nameBytes.length +  // name bytes
                4;                  // age

        ByteBuffer buffer =
                ByteBuffer.allocate(recordSize);

        buffer.putInt(user.getId());

        buffer.putInt(nameBytes.length);

        buffer.put(nameBytes);

        buffer.putInt(user.getAge());

        return buffer.array();
    }
}
```

---

# 53. Java Deserializer

```java
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class UserRecordDeserializer {

    public User deserialize(byte[] recordBytes) {

        ByteBuffer buffer =
                ByteBuffer.wrap(recordBytes);

        int id = buffer.getInt();

        int nameLength = buffer.getInt();

        byte[] nameBytes =
                new byte[nameLength];

        buffer.get(nameBytes);

        String name =
                new String(nameBytes, StandardCharsets.UTF_8);

        int age = buffer.getInt();

        return new User(id, name, age);
    }
}
```

---

# 54. Serialization Dry Run

Input:

```text
User{id=1, name=Mohamed, age=30}
```

Steps:

```text
id = 1
    ↓
write 4 bytes

name = Mohamed
    ↓
convert to UTF-8 bytes
    ↓
length = 8
    ↓
write length 4 bytes
    ↓
write name bytes

age = 30
    ↓
write 4 bytes
```

Final record:

```text
[id][name_length][name_bytes][age]
```

---

# 55. Deserialization Dry Run

Input bytes:

```text
[1][8][Mohamed][30]
```

Steps:

```text
read first 4 bytes → id
read next 4 bytes → name length
read next 8 bytes → name bytes
decode name bytes → Mohamed
read next 4 bytes → age
construct User object
```

---

# 56. Mini Page Implementation Idea

A simple page can be modeled as:

```java
byte[] page = new byte[8192];
```

This represents:

```text
8 KB disk page in memory
```

Real databases read/write pages like this.

---

# 57. Simple Page Class

```java
public class Page {

    private final byte[] data;

    public Page(int pageSize) {
        this.data = new byte[pageSize];
    }

    public byte[] getData() {
        return data;
    }
}
```

This is extremely simplified.

---

# 58. Real Backend Flow

```text
Spring Boot Entity
        ↓
JPA / JDBC
        ↓
SQL Row
        ↓
Database Record
        ↓
Page
        ↓
Disk
```

ORM hides storage details.

But database still works this way internally.

---

# 59. Why This Matters In System Design

Understanding row/record model helps explain:

```text
why updates can be expensive
why indexes point to row locations
why page splits happen
why WAL logs binary changes
why MVCC needs tuple metadata
why storage engines differ
```

This gives deeper design intuition.

---

# 60. Interview Explanation

If interviewer asks:

```text
How are rows stored internally in a database?
```

Strong answer:

```text
A logical row is serialized into a physical binary record.
The record contains encoded field values plus metadata such as length,
null information, and sometimes transaction visibility data.
Records are packed into disk pages, often using a slotted page layout,
so the database can efficiently locate, update, and scan records.
```

Senior-level addition:

```text
Variable-length fields require offset metadata,
updates may cause relocation or new versions,
and MVCC systems store transaction metadata with records.
```

---

# 61. Common Mistakes

## Mistake 1

```text
Thinking database stores Java objects directly
```

Wrong.

Database stores bytes.

---

## Mistake 2

```text
Ignoring variable-length fields
```

Strings and JSON require offsets/length metadata.

---

## Mistake 3

```text
Thinking update always overwrites same location
```

May create new version or relocate record.

---

## Mistake 4

```text
Ignoring page-level storage
```

Databases operate heavily around pages.

---

## Mistake 5

```text
Ignoring record metadata
```

Metadata powers null handling, MVCC, visibility, and recovery.

---

# 62. Final Mental Model

```text
Logical World:

Table
 ↓
Row
 ↓
Column Values

Physical World:

Database File
 ↓
Page
 ↓
Slot
 ↓
Record Bytes
```

---

# 63. What To Remember

```text
Rows are logical.
Records are physical.

Databases serialize rows into binary records.

Records contain:
data
metadata
null information
lengths
offsets

Records are packed inside pages.

Pages are the real unit of disk IO.

Storage layout affects performance, updates, indexes, WAL, and MVCC.
```

---


---

# 64A. How Each Concept Looks In Memory

This section connects the logical database concepts to actual memory representation.

The goal:

```text
Table / Row / Record / Page
should not feel abstract.
You should visualize where each thing lives in memory.
```

---

# 64B. Logical Table vs In-Memory Table

## Logical View

```text
users table

+----+---------+-----------+-----+
| id | name    | city      | age |
+----+---------+-----------+-----+
| 1  | Mohamed | Bucharest | 30  |
| 2  | John    | London    | 28  |
+----+---------+-----------+-----+
```

## In-Memory View

In Java, a simple table can be represented as:

```text
Map<PrimaryKey, Row>
```

ASCII memory model:

```text
Heap Memory

usersTable
   |
   +-- rows HashMap
          |
          +-- key: 1
          |      |
          |      +-- Row Object
          |             |
          |             +-- values Map
          |                    |
          |                    +-- "id"   -> 1
          |                    +-- "name" -> "Mohamed"
          |                    +-- "city" -> "Bucharest"
          |                    +-- "age"  -> 30
          |
          +-- key: 2
                 |
                 +-- Row Object
                        |
                        +-- values Map
                               |
                               +-- "id"   -> 2
                               +-- "name" -> "John"
                               +-- "city" -> "London"
                               +-- "age"  -> 28
```

Important:

```text
This is application-level memory representation.
Real databases use compact binary pages instead.
```

---

# 64C. Java Object Representation Mental Model

A Java object is not stored like a compact database record.

A Java object has:

```text
object header
field references
alignment/padding
GC metadata
separate String objects
```

Example:

```java
User user = new User(1, "Mohamed", "Bucharest", 30);
```

Approximate JVM heap mental model:

```text
Heap Memory

+----------------------+
| User Object          |
+----------------------+
| object header        |
| id = 1               |
| nameRef ------------ | ----+
| cityRef ------------ | --+ |
| age = 30             |   | |
+----------------------+   | |
                           | |
                           | v
                +------------------+
                | String "Mohamed" |
                +------------------+
                | object header    |
                | char/byte array  |
                +------------------+

                           v
                +--------------------+
                | String "Bucharest" |
                +--------------------+
                | object header      |
                | char/byte array    |
                +--------------------+
```

This is flexible for Java.

But it is bad for database storage because:

```text
too much overhead
many pointers
not compact
JVM-specific
not portable to disk
```

---

# 64D. Database Record In Memory

Database prefers compact bytes.

Same user:

```text
{id=1, name=Mohamed, city=Bucharest, age=30}
```

can become:

```text
[record_header][null_bitmap][id][age][name_len][city_len][name_bytes][city_bytes]
```

ASCII:

```text
Record Bytes In Memory

+---------------+-------------+---------+---------+----------+----------+----------+------------+
| record_header | null_bitmap | id      | age     | name_len | city_len | name     | city       |
+---------------+-------------+---------+---------+----------+----------+----------+------------+
| metadata      | 0000        | 4 bytes | 4 bytes | 4 bytes  | 4 bytes  | Mohamed  | Bucharest  |
+---------------+-------------+---------+---------+----------+----------+----------+------------+
```

This is compact and disk-friendly.

---

# 64E. Object vs Record Memory Comparison

## Java Object Style

```text
User object
   |
   +-- id value
   +-- reference to String object
   +-- reference to String object
   +-- age value
```

## Database Record Style

```text
continuous byte array
   |
   +-- header
   +-- bitmap
   +-- field bytes
   +-- variable data
```

Comparison:

| Feature | Java Object | Database Record |
|---|---|---|
| Format | object + references | compact bytes |
| Storage | JVM heap | page/disk |
| Portability | JVM-specific | database-defined |
| Disk-friendly | No | Yes |
| Compact | No | Yes |
| Good for | application logic | storage engine |

---

# 64F. Row As Map vs Record As Bytes

## Row as Map

```text
Row Object
   |
   +-- HashMap<String, Object>
          |
          +-- "id"   -> Integer(1)
          +-- "name" -> String("Mohamed")
          +-- "city" -> String("Bucharest")
          +-- "age"  -> Integer(30)
```

Pros:

```text
easy to use
flexible
good for learning
```

Cons:

```text
memory overhead
not compact
slow to serialize repeatedly
not disk layout friendly
```

## Record as Bytes

```text
byte[]
   |
   +-- [header][bitmap][id][age][name][city]
```

Pros:

```text
compact
fast disk IO
portable
storage-engine friendly
```

Cons:

```text
harder to parse
needs schema metadata
needs offset management
```

---

# 64G. Serialized Byte Array Representation

Java serializer output:

```java
byte[] recordBytes
```

Memory view:

```text
recordBytes
   |
   v
+------+------+------+------+------+------+------+------+------+------+
| 00   | 00   | 00   | 01   | 00   | 00   | 00   | 08   | M    | o    | ...
+------+------+------+------+------+------+------+------+------+------+
   \__________________/   \__________________/   \____________________
          id=1                 name_len=8            name bytes
```

Simplified logical interpretation:

```text
[ id=1 ][ name_length=8 ][ Mohamed ][ age=30 ]
```

The database must know the schema to decode these bytes.

---

# 64H. Schema In Memory

Schema metadata tells database how to interpret bytes.

Example schema:

```text
users(
  id INT,
  name VARCHAR,
  city VARCHAR,
  age INT
)
```

In memory:

```text
Schema Object
   |
   +-- Column[0] name="id",   type=INT,     fixed=4 bytes
   +-- Column[1] name="name", type=VARCHAR, variable
   +-- Column[2] name="city", type=VARCHAR, variable
   +-- Column[3] name="age",  type=INT,     fixed=4 bytes
```

Why schema needed?

```text
bytes alone are meaningless.
schema gives meaning to bytes.
```

---

# 64I. Record Decode Flow In Memory

Input:

```text
byte[] recordBytes
Schema users
```

Flow:

```text
recordBytes
    ↓
read header
    ↓
read null bitmap
    ↓
schema says column 0 = INT
    ↓
read 4 bytes as id
    ↓
schema says column 1 = VARCHAR
    ↓
read length + string bytes
    ↓
reconstruct row
```

ASCII:

```text
Bytes + Schema
      ↓
Deserializer
      ↓
Row{id=1, name=Mohamed, city=Bucharest, age=30}
```

---

# 64J. Page In Memory

Database reads disk pages into RAM.

Example:

```text
byte[] page = new byte[8192];
```

Memory view:

```text
RAM Buffer Pool

Page-100
+------------------------------------------------+
| byte[0]                                        |
| byte[1]                                        |
| byte[2]                                        |
| ...                                            |
| byte[8191]                                     |
+------------------------------------------------+
```

But logically the page contains:

```text
header
slot directory
free space
record bytes
```

---

# 64K. Slotted Page In Memory

```text
Page-100 In RAM

+------------------------------------------------+
| Page Header                                    |
| - pageId = 100                                 |
| - freeStart = 120                              |
| - freeEnd = 7600                               |
| - slotCount = 3                                |
+------------------------------------------------+
| Slot Directory                                 |
| slot 0 -> offset 7900, length 80               |
| slot 1 -> offset 7800, length 90               |
| slot 2 -> offset 7600, length 120              |
+------------------------------------------------+
| Free Space                                     |
|                                                |
|                                                |
+------------------------------------------------+
| Record-2 bytes                                 |
| Record-1 bytes                                 |
| Record-0 bytes                                 |
+------------------------------------------------+
```

Mental model:

```text
slots grow downward from top
records grow upward from bottom
free space stays in middle
```

---

# 64L. Page + Slot + Record Lookup

RecordID:

```text
RID(page=100, slot=1)
```

Lookup flow:

```text
Buffer Pool
   ↓
Find Page-100
   ↓
Read slot directory
   ↓
slot 1 -> offset 7800
   ↓
Read record bytes at offset 7800
   ↓
Decode bytes using schema
   ↓
Return row
```

ASCII:

```text
RID(100, 1)
    ↓
+------------------+
| Page-100         |
|                  |
| Slot-1 -> 7800   |
|                  |
| offset 7800      |
|   ↓              |
| Record Bytes     |
+------------------+
```

---

# 64M. Buffer Pool Representation

Real database does not read disk every time.

It keeps pages in RAM.

```text
Buffer Pool Memory

+-------------+      +-------------+      +-------------+
| Frame-1     |      | Frame-2     |      | Frame-3     |
| Page-100    |      | Page-101    |      | Page-205    |
+-------------+      +-------------+      +-------------+
```

Mapping:

```text
pageId -> memory frame
```

Example:

```text
100 -> Frame-1
101 -> Frame-2
205 -> Frame-3
```

This is like:

```text
cache for disk pages
```

---

# 64N. Disk File Representation

On disk, database file is mostly pages.

```text
users.data file

+---------+---------+---------+---------+---------+
| Page-0  | Page-1  | Page-2  | Page-3  | Page-4  |
+---------+---------+---------+---------+---------+
```

Each page may contain many records.

When database needs row:

```text
find page
load page into buffer pool
read record from page
```

---

# 64O. Full End-To-End Memory Representation

Logical query:

```sql
SELECT * FROM users WHERE id = 1;
```

Internal path:

```text
SQL Query
   ↓
Index lookup finds RID(100, 1)
   ↓
Buffer Pool checks Page-100
   ↓
If missing, read Page-100 from disk
   ↓
Page-100 loaded into RAM frame
   ↓
Slot-1 gives record offset
   ↓
Record bytes decoded using schema
   ↓
Row returned to application
```

ASCII full flow:

```text
Application
   ↓
SQL Engine
   ↓
Index
   ↓
RID(page=100, slot=1)
   ↓
Buffer Pool
   ↓
Page-100 in RAM
   ↓
Slot Directory
   ↓
Record Bytes
   ↓
Deserializer
   ↓
Row Object / ResultSet
```

---

# 64P. INSERT Representation In Memory

Insert row:

```text
{id=3, name=Alice, city=Paris, age=25}
```

Flow:

```text
Row Object
   ↓
Serialize to record bytes
   ↓
Find page with free space
   ↓
Copy record bytes into page byte[]
   ↓
Add slot entry
   ↓
Index maps id=3 to RID(page, slot)
```

ASCII:

```text
Before Page:

+----------------------+
| Header               |
| Slot-0               |
| Slot-1               |
| Free Space           |
| Record-1             |
| Record-0             |
+----------------------+

After Insert:

+----------------------+
| Header               |
| Slot-0               |
| Slot-1               |
| Slot-2 -> Record-2   |
| Free Space           |
| Record-2             |
| Record-1             |
| Record-0             |
+----------------------+
```

---

# 64Q. UPDATE Representation In Memory

Update:

```text
name = John → Jonathan
```

If record grows:

```text
old record may not fit
```

Possible memory representation:

```text
Old Slot-1
   ↓
old record marked dead / moved

New Slot-3
   ↓
new larger record bytes
```

ASCII:

```text
Before:

Slot-1 -> [id=2][name=John][age=28]

After:

Slot-1 -> DEAD / old version
Slot-3 -> [id=2][name=Jonathan][age=28]
```

This connects to:

```text
MVCC
fragmentation
vacuum
compaction
```

---

# 64R. DELETE Representation In Memory

Delete does not always erase bytes immediately.

Possible representation:

```text
Slot-1 -> record marked deleted
```

ASCII:

```text
Before:

slot 1 -> [id=2][name=John][age=28]

After delete:

slot 1 -> [DELETED][id=2][name=John][age=28]
```

Later cleanup:

```text
vacuum/compaction removes dead record
```

---

# 64S. How Concepts Map Together

```text
Table
  logical collection of rows

Row
  logical record seen by application

Record
  physical binary representation of row

Page
  fixed-size block containing many records

Slot
  pointer inside page to a record

RID
  page id + slot id

Buffer Pool
  RAM cache of disk pages

Disk File
  persistent storage of pages
```

---

# 64T. Final Diagram — Whole Storage Stack

```text
Application Object
        ↓
Logical Row
        ↓
Serialized Record Bytes
        ↓
Slot inside Page
        ↓
Page inside Buffer Pool
        ↓
Page inside Disk File
        ↓
Persistent Storage
```

This is the complete memory/storage mental model.


# 64. Next File

```text
006_Insert_Update_Delete.md
```

Next you learn:

```text
insert path
update path
delete path
write amplification
tombstones
record relocation
database write flow
```
