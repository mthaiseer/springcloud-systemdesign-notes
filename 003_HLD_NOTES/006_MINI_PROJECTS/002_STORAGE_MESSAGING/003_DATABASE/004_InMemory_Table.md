# 004_InMemory_Table.md

# MiniDatabase — 004 InMemory Table

# 0. Why This File Exists

Before learning:

- disk pages
- indexes
- WAL
- transactions
- MVCC
- replication

you must first understand the simplest database:

in-memory table.

A table is the core unit of most databases.

This file teaches:

- what a table is
- what a row is
- what a schema is
- how rows are stored in memory
- how insert/select/update/delete works
- why primary key matters
- how this becomes the foundation of real databases

This is the first implementation-style file in MiniDatabase.

---

# 1. One-Line Definition

An in-memory table is a table stored inside RAM using data structures.

Simple meaning:

Table = collection of rows stored temporarily in memory.

---

# 2. Big Mental Model

Database
↓
Tables
↓
Rows
↓
Columns
↓
Values

Example:

Database: ecommerce
Table: users
Row: one user
Column: name, email, age
Value: Mohamed, email, 30

---

# 3. Table Mental Model

A table is like an Excel sheet, but database table has:

- schema
- types
- constraints
- indexes
- transactions
- storage engine

---

# 4. Example Table

| id | name | city | age |
|---|---|---|---|
| 1 | Mohamed | Bucharest | 30 |
| 2 | John | London | 28 |
| 3 | Alice | Paris | 25 |

---

# 5. ASCII Table View

```text
+----+---------+-----------+-----+
| id | name    | city      | age |
+----+---------+-----------+-----+
| 1  | Mohamed | Bucharest | 30  |
| 2  | John    | London    | 28  |
| 3  | Alice   | Paris     | 25  |
+----+---------+-----------+-----+
```

---

# 6. Database Storage Hierarchy

```text
Database
│
├── users table
│   ├── row id=1
│   ├── row id=2
│   └── row id=3
│
├── orders table
│   ├── row order_id=101
│   └── row order_id=102
│
└── payments table
    ├── row payment_id=501
    └── row payment_id=502
```

---

# 7. What Is Schema?

Schema defines:

- columns
- data types
- constraints

Example:

```text
users(
  id INT PRIMARY KEY,
  name STRING,
  city STRING,
  age INT
)
```

Schema tells database what shape each row must follow.

---

# 8. Schema Mental Model

Schema = blueprint  
Row = actual object built from blueprint

Like Java class:

```java
class User {
    int id;
    String name;
    String city;
    int age;
}
```

---

# 9. Row Mental Model

A row represents one entity record.

Examples:

- one user
- one order
- one payment
- one product

---

# 10. Column Mental Model

A column represents one attribute.

Examples:

- id
- name
- email
- price
- created_at
- status

---

# 11. Value Mental Model

A value is actual data in one row-column intersection.

Example:

row id=1, column name = Mohamed

---

# 12. In-Memory Storage Model

Simplest way to store table:

```text
Map<PrimaryKey, Row>
```

Example:

```text
1 → {id=1, name=Mohamed, city=Bucharest}
2 → {id=2, name=John, city=London}
```

---

# 13. ASCII Memory Layout

```text
RAM Memory

usersTable
    |
    +-- key=1 → Row{id=1, name=Mohamed, city=Bucharest, age=30}
    |
    +-- key=2 → Row{id=2, name=John, city=London, age=28}
    |
    +-- key=3 → Row{id=3, name=Alice, city=Paris, age=25}
```

---

# 14. Why Use Map?

Because primary key lookup becomes fast:

```text
O(1) average lookup
```

Example:

find user where id = 2

Map directly jumps to key 2.

---

# 15. Without Map

If table stored as list:

```text
[Row1, Row2, Row3, ...]
```

To find id=999:

```text
scan one by one
```

Slow for large table.

---

# 16. With Map

```text
HashMap
    ↓
key lookup
    ↓
row found quickly
```

This is the first simple index.

---

# 17. Important Connection To Real Databases

Real databases do not usually store tables as Java HashMap only.

But conceptually:

```text
primary key → row location
```

is exactly what indexes help with.

---

# 18. Java Implementation — Row

```java
import java.util.HashMap;
import java.util.Map;

public class Row {

    private final Map<String, Object> columns =
            new HashMap<>();

    public void put(String columnName, Object value) {
        columns.put(columnName, value);
    }

    public Object get(String columnName) {
        return columns.get(columnName);
    }

    public Map<String, Object> getColumns() {
        return columns;
    }

    @Override
    public String toString() {
        return columns.toString();
    }
}
```

---

# 19. Row Code Explanation

```java
Map<String, Object> columns
```

means:

```text
column name → value
```

Example:

```text
id   → 1
name → Mohamed
age  → 30
```

This gives flexible row structure.

---

# 20. Java Implementation — InMemoryTable

```java
import java.util.HashMap;
import java.util.Map;

public class InMemoryTable {

    private final Map<Object, Row> rows =
            new HashMap<>();

    private final String primaryKeyColumn;

    public InMemoryTable(String primaryKeyColumn) {
        this.primaryKeyColumn = primaryKeyColumn;
    }

    public void insert(Row row) {

        Object primaryKeyValue =
                row.get(primaryKeyColumn);

        if (primaryKeyValue == null) {
            throw new IllegalArgumentException(
                    "Primary key missing: " + primaryKeyColumn
            );
        }

        if (rows.containsKey(primaryKeyValue)) {
            throw new IllegalArgumentException(
                    "Duplicate primary key: " + primaryKeyValue
            );
        }

        rows.put(primaryKeyValue, row);
    }

    public Row findById(Object id) {
        return rows.get(id);
    }

    public void update(Object id,
                       String columnName,
                       Object newValue) {

        Row row = rows.get(id);

        if (row == null) {
            throw new IllegalArgumentException(
                    "Row not found: " + id
            );
        }

        row.put(columnName, newValue);
    }

    public void delete(Object id) {
        rows.remove(id);
    }

    public void printAll() {
        for (Row row : rows.values()) {
            System.out.println(row);
        }
    }
}
```

---

# 21. Java Implementation — Demo

```java
public class MiniDatabaseDemo {

    public static void main(String[] args) {

        InMemoryTable users =
                new InMemoryTable("id");

        Row user1 = new Row();
        user1.put("id", 1);
        user1.put("name", "Mohamed");
        user1.put("city", "Bucharest");
        user1.put("age", 30);

        Row user2 = new Row();
        user2.put("id", 2);
        user2.put("name", "John");
        user2.put("city", "London");
        user2.put("age", 28);

        users.insert(user1);
        users.insert(user2);

        System.out.println(users.findById(1));

        users.update(1, "age", 31);

        System.out.println(users.findById(1));

        users.delete(2);

        users.printAll();
    }
}
```

---

# 22. Insert Dry Run

Initial table:

```text
rows = {}
```

Insert user1:

```text
id = 1
row = {id=1, name=Mohamed, city=Bucharest, age=30}
```

Flow:

```text
check primary key exists
      ↓
check duplicate key
      ↓
put into HashMap
```

Result:

```text
rows = {
  1 → Row{id=1, name=Mohamed, city=Bucharest, age=30}
}
```

---

# 23. Insert ASCII Flow

```text
Row{id=1, name=Mohamed}
        ↓
extract primary key id=1
        ↓
HashMap.put(1, row)
        ↓
table updated
```

---

# 24. Find Dry Run

Operation:

```java
users.findById(1)
```

Flow:

```text
HashMap.get(1)
      ↓
returns Row{id=1, name=Mohamed}
```

Result:

```text
O(1) average lookup
```

---

# 25. Find ASCII Flow

```text
Query: find id=1
        ↓
Primary Key Map
        ↓
1 → Row
        ↓
Return row
```

---

# 26. Update Dry Run

Operation:

```java
users.update(1, "age", 31)
```

Flow:

```text
find row by id=1
      ↓
row found
      ↓
update age column
      ↓
age 30 → 31
```

Result:

```text
{id=1, name=Mohamed, city=Bucharest, age=31}
```

---

# 27. Delete Dry Run

Operation:

```java
users.delete(2)
```

Flow:

```text
HashMap.remove(2)
      ↓
row removed from table
```

Result:

only user1 remains.

---

# 28. Full CRUD Flow

```text
INSERT
  ↓
Map.put(primaryKey, row)

SELECT by PK
  ↓
Map.get(primaryKey)

UPDATE
  ↓
Map.get(primaryKey)
  ↓
modify row

DELETE
  ↓
Map.remove(primaryKey)
```

---

# 29. What This Mini Table Supports

This simple in-memory table supports:

- insert
- find by primary key
- update
- delete
- print all

Very basic database behavior.

---

# 30. What This Mini Table Does NOT Support Yet

It does NOT support:

- SQL parser
- secondary indexes
- transactions
- disk persistence
- WAL
- concurrency safety
- range queries
- joins
- MVCC
- replication

These come later.

---

# 31. Why This Is Still Powerful

This simple model teaches:

- table
- row
- column
- primary key
- CRUD
- storage map
- basic index idea

This is the foundation.

---

# 32. Primary Key

Primary key means:

```text
unique identifier for a row
```

Example:

- user id
- order id
- payment id

---

# 33. Primary Key Mental Model

```text
Primary key = row identity
```

Without primary key:

```text
hard to uniquely find row
```

---

# 34. Duplicate Primary Key Problem

Suppose two users have same id:

```text
id = 1
```

Impossible to know which one is correct.

So database rejects duplicate primary key.

---

# 35. Primary Key Insert Flow

```text
Insert row
   ↓
extract primary key
   ↓
does key exist?
   ├── yes → reject duplicate
   └── no  → insert row
```

---

# 36. Table Scan

If query is:

```text
find all users where city = Bucharest
```

Our primary key map does not help.

Need:

```text
scan all rows
```

Flow:

```text
Row-1 city?
Row-2 city?
Row-3 city?
...
```

This is full table scan.

---

# 37. Full Table Scan Example

```java
public void findByColumn(String columnName,
                         Object value) {

    for (Row row : rows.values()) {

        if (value.equals(row.get(columnName))) {
            System.out.println(row);
        }
    }
}
```

This is:

```text
O(N)
```

---

# 38. Why Secondary Indexes Exist

Query:

```sql
SELECT * FROM users WHERE city = 'Bucharest';
```

If no index on city:

```text
scan full table
```

If index exists:

```text
jump directly to matching rows
```

This comes later.

---

# 39. In-Memory vs Persistent Database

Our table is in RAM.

If app stops:

```text
data lost
```

Real databases persist data to:

- disk
- WAL
- snapshots
- SSTables
- pages

---

# 40. In-Memory Table Limitation

```text
fast but not durable
```

Redis is in-memory but adds durability using:

- RDB
- AOF

Relational databases use:

- WAL
- disk pages

---

# 41. Thread Safety Warning

Our current HashMap is NOT thread-safe.

If multiple threads insert/update:

```text
race conditions possible
```

Need:

- synchronized
- ConcurrentHashMap
- locks
- transactions

This connects to MiniConcurrency.

---

# 42. Thread-Safe Version Idea

```java
private final Map<Object, Row> rows =
        new ConcurrentHashMap<>();
```

But even this is not enough for multi-step transaction logic.

Need transaction management later.

---

# 43. Real Database Mapping

This file:

```text
Map<PrimaryKey, Row>
```

Real database:

```text
BTree index → page id → row location
```

Mental connection:

```text
primary key lookup maps to row
```

---

# 44. Backend Example — User Service

Spring Boot endpoint:

```text
GET /users/1
```

Flow:

```text
Controller
   ↓
UserService
   ↓
users.findById(1)
   ↓
Row returned
```

Same mental model as real DB.

---

# 45. Backend Example — Order Service

```text
POST /orders
```

Flow:

```text
create order row
      ↓
insert into orders table
      ↓
return order id
```

---

# 46. Production Mapping

Real production databases add:

- disk persistence
- indexes
- query optimizer
- transactions
- locking
- replication
- sharding
- connection pooling

But base model remains:

```text
store rows
retrieve rows
update rows
delete rows
```

---

# 47. Interview Explanation

If interviewer asks:

```text
How would you build a simple in-memory table?
```

Strong answer:

```text
I would represent a table as a map from primary key to row.
Each row can be represented as a map from column name to value.
Insert validates primary key uniqueness, select uses map lookup,
update modifies row columns, and delete removes the key.
```

Strong backend addition:

```text
This gives fast primary-key lookup, but secondary column queries still
require full table scans unless additional indexes are built.
It is also not durable unless persisted to disk using WAL or snapshots.
```

---

# 48. Common Mistakes

## Mistake 1

Thinking table is just list of rows.

For primary key lookup, map/index is better.

## Mistake 2

Ignoring duplicate primary keys.

Breaks row identity.

## Mistake 3

Thinking in-memory means production-ready.

Data lost after restart.

## Mistake 4

Ignoring thread safety.

Concurrent writes can corrupt state.

## Mistake 5

Thinking primary key index solves all queries.

Only helps primary key lookup.

---

# 49. Final Mental Model

```text
InMemoryTable
     ↓
Map<PrimaryKey, Row>
     ↓
Row = Map<ColumnName, Value>
     ↓
CRUD operations
```

This is the smallest possible database engine.

---

# 50. What To Remember

- Table stores rows.
- Row stores column values.
- Primary key uniquely identifies row.
- HashMap gives fast primary-key lookup.
- Full table scan needed for non-indexed columns.
- In-memory table is fast but not durable.
- Real databases add persistence, indexes, transactions, and recovery.

---

# 51. Next File

```text
005_Table_Row_Record_Model.md
```

Next you learn:

- how rows become records
- how records are serialized
- how database stores row bytes
- how schema maps to physical storage
- why page/block storage comes later
