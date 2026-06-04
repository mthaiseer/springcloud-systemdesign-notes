# 010_Filter_Projection_Scan.md

# MiniDatabase — 010 Filter Projection Scan

## 0. Why This File Exists

After the query pipeline creates an execution plan, the database must actually execute it.

The most basic execution operators are:

```text
Scan
Filter
Projection
```

These three explain how this query works internally:

```sql
SELECT name, age
FROM users
WHERE age > 25;
```

This file teaches:

```text
how rows are read from storage
how WHERE filters rows
how SELECT chooses columns
how table scan and index scan differ
how rows flow through execution operators
how mini query engines work internally
```

Only ASCII diagrams are used.

No Mermaid.

---

# 1. One-Line Definitions

## Scan

```text
Reads rows from table/index/storage.
```

## Filter

```text
Applies WHERE condition and removes non-matching rows.
```

## Projection

```text
Keeps only requested SELECT columns.
```

---

# 2. Biggest Mental Model

Query:

```sql
SELECT name, age
FROM users
WHERE age > 25;
```

Database execution:

```text
Scan rows
    ↓
Filter rows where age > 25
    ↓
Project only name, age
    ↓
Return result
```

---

# 3. Full Operator Pipeline

```text
+----------------------+
| Scan Operator        |
| reads rows           |
+----------------------+
           ↓
+----------------------+
| Filter Operator      |
| WHERE age > 25       |
+----------------------+
           ↓
+----------------------+
| Projection Operator  |
| SELECT name, age     |
+----------------------+
           ↓
+----------------------+
| Result Set           |
+----------------------+
```

---

# 4. Example Table

```text
users
```

```text
+----+---------+-----------+-----+
| id | name    | city      | age |
+----+---------+-----------+-----+
| 1  | Mohamed | Bucharest | 30  |
| 2  | John    | London    | 20  |
| 3  | Alice   | Paris     | 35  |
+----+---------+-----------+-----+
```

Query:

```sql
SELECT name, age
FROM users
WHERE age > 25;
```

Expected result:

```text
+---------+-----+
| name    | age |
+---------+-----+
| Mohamed | 30  |
| Alice   | 35  |
+---------+-----+
```

---

# 5. Query Execution Tree

Execution plan:

```text
Projection(name, age)
        ↓
Filter(age > 25)
        ↓
Scan(users)
```

Important mental model:

```text
operators form a tree
rows flow from bottom to top
```

---

# 6. Operator Tree ASCII

```text
+----------------------+
| Projection           |
| keep: name, age      |
+----------------------+
           ↑
+----------------------+
| Filter               |
| condition: age > 25  |
+----------------------+
           ↑
+----------------------+
| Scan                 |
| source: users table  |
+----------------------+
```

Rows start from scan and move upward.

---

# 7. Why Rows Flow Upward

Execution engine usually asks top operator:

```text
give me next result row
```

Then projection asks filter.

Filter asks scan.

Scan reads storage.

Then row moves back upward.

```text
request goes down
row comes up
```

---

# 8. Scan Operator

Scan operator is responsible for:

```text
reading rows from storage
```

There are two main scan types:

```text
Table Scan
Index Scan
```

---

# 9. Table Scan

Table scan means:

```text
read every page
read every row
check condition
```

It is also called:

```text
sequential scan
full table scan
```

---

# 10. Table Scan ASCII

```text
Table File: users.data
│
├── Page-1
│   ├── Row-1
│   ├── Row-2
│   └── Row-3
│
├── Page-2
│   ├── Row-4
│   ├── Row-5
│   └── Row-6
│
└── Page-3
    ├── Row-7
    ├── Row-8
    └── Row-9
```

Table scan checks every row.

Cost:

```text
O(N)
```

---

# 11. Table Scan Flow

```text
Start table scan
      ↓
Read first page
      ↓
Decode records
      ↓
Return rows one by one
      ↓
Read next page
      ↓
Repeat until table ends
```

---

# 12. When Table Scan Is Good

Table scan is not always bad.

Optimizer may choose table scan when:

```text
table is small
most rows are needed
predicate is not selective
index not available
index lookup would cause too many random reads
```

Example:

```sql
SELECT * FROM users;
```

Table scan is expected.

---

# 13. When Table Scan Is Bad

Table scan is bad when:

```text
table is huge
query needs very few rows
good index exists but not used
```

Example:

```sql
SELECT * FROM users WHERE id = 10;
```

If users table has 100 million rows:

```text
table scan is very expensive
```

---

# 14. Index Scan

Index scan uses index first.

Flow:

```text
search index
    ↓
get RID(page, slot)
    ↓
load page
    ↓
read slot
    ↓
decode record bytes
```

---

# 15. Index Scan ASCII

```text
Query: id = 10
        ↓
+----------------------+
| BTree / Hash Index   |
+----------------------+
        ↓
RID(page=100, slot=3)
        ↓
+----------------------+
| Buffer Pool          |
| load Page-100        |
+----------------------+
        ↓
Slot-3 -> record offset
        ↓
Read record bytes
        ↓
Decode using schema
        ↓
Return row
```

---

# 16. Table Scan vs Index Scan

```text
Table Scan
=
read everything
```

```text
Index Scan
=
jump to matching rows
```

Comparison:

| Feature | Table Scan | Index Scan |
|---|---|---|
| Reads all rows | Yes | No |
| Uses index | No | Yes |
| Good for small table | Yes | Sometimes |
| Good for exact lookup | No | Yes |
| Good for many matching rows | Sometimes | Maybe not |
| Cost | O(N) | O(log N) / O(1) depending index |

---

# 17. Filter Operator

Filter applies condition from:

```sql
WHERE
```

Example:

```sql
WHERE age > 25
```

Filter decides:

```text
keep row
or
discard row
```

---

# 18. Filter ASCII

```text
Incoming Row
{id=1, name=Mohamed, age=30}
        ↓
Evaluate: age > 25?
        ↓
true
        ↓
Pass row upward
```

For another row:

```text
Incoming Row
{id=2, name=John, age=20}
        ↓
Evaluate: age > 25?
        ↓
false
        ↓
Discard row
```

---

# 19. Predicate

The condition inside `WHERE` is called:

```text
predicate
```

Examples:

```sql
id = 10
age > 25
city = 'London'
price BETWEEN 100 AND 500
status IN ('PAID', 'SHIPPED')
```

---

# 20. Predicate Evaluation

For each row:

```text
read column value
    ↓
apply operator
    ↓
true or false
```

Example:

```text
age = 30
predicate = age > 25
result = true
```

---

# 21. Filter Dry Run

Input rows:

```text
Mohamed age=30
John age=20
Alice age=35
```

Predicate:

```text
age > 25
```

Execution:

```text
Mohamed → 30 > 25 → true  → keep
John    → 20 > 25 → false → discard
Alice   → 35 > 25 → true  → keep
```

---

# 22. Projection Operator

Projection keeps only requested columns from:

```sql
SELECT
```

Query:

```sql
SELECT name, age
```

Input row:

```text
{id=1, name=Mohamed, city=Bucharest, age=30}
```

Output row:

```text
{name=Mohamed, age=30}
```

---

# 23. Projection ASCII

```text
Full Row
+----+---------+-----------+-----+
| id | name    | city      | age |
+----+---------+-----------+-----+
| 1  | Mohamed | Bucharest | 30  |
+----+---------+-----------+-----+
        ↓
Project only name, age
        ↓
+---------+-----+
| name    | age |
+---------+-----+
| Mohamed | 30  |
+---------+-----+
```

---

# 24. Why Projection Matters

Projection reduces:

```text
memory usage
CPU work
network transfer
serialization cost
```

Better:

```sql
SELECT id, name FROM users;
```

Worse when not needed:

```sql
SELECT * FROM users;
```

---

# 25. SELECT * Problem

`SELECT *` may cause:

```text
more columns read
more data decoded
more network transfer
larger DTOs
more memory pressure
```

In production:

```text
select only what you need
```

---

# 26. Full Query Flow

Query:

```sql
SELECT name
FROM users
WHERE age > 25;
```

Execution:

```text
TableScan(users)
      ↓
Filter(age > 25)
      ↓
Projection(name)
      ↓
Result
```

---

# 27. Full Row Flow ASCII

```text
Row from scan:
{id=1, name=Mohamed, city=Bucharest, age=30}
        ↓
Filter age > 25
        ↓ yes
Projection name
        ↓
{name=Mohamed}
        ↓
Result
```

Second row:

```text
Row from scan:
{id=2, name=John, city=London, age=20}
        ↓
Filter age > 25
        ↓ no
Discard
```

Third row:

```text
Row from scan:
{id=3, name=Alice, city=Paris, age=35}
        ↓
Filter age > 25
        ↓ yes
Projection name
        ↓
{name=Alice}
        ↓
Result
```

---

# 28. Iterator Model

Many databases use iterator model.

Each operator has:

```text
next()
```

Execution:

```text
projection.next()
        ↓
filter.next()
        ↓
scan.next()
        ↓
storage reads row
```

---

# 29. Iterator ASCII

```text
Client
  ↓ asks next()
Projection.next()
  ↓ asks next()
Filter.next()
  ↓ asks next()
Scan.next()
  ↓ asks storage
Storage Engine
  ↑ returns row
Scan
  ↑ returns row
Filter
  ↑ returns matching row
Projection
  ↑ returns selected columns
Client
```

---

# 30. Page-To-Row Flow

Scan does not magically produce rows.

It reads:

```text
page
slot
record bytes
schema
row
```

---

# 31. Page Decode ASCII

```text
Page-100 in RAM
│
├── Page Header
│
├── Slot Directory
│   ├── slot-0 -> offset 7900
│   ├── slot-1 -> offset 7600
│   └── slot-3 -> offset 7400
│
└── Record Area
    └── offset 7400 -> [record bytes]
                          ↓
                    decode using schema
                          ↓
                    Row{id=10,name=Mohamed,age=30}
```

---

# 32. Predicate Pushdown

Predicate pushdown means:

```text
apply filter as early as possible
```

Bad:

```text
Storage
  ↓
read all rows
  ↓
Execution engine
  ↓
filter rows
```

Better:

```text
Storage / Scan
  ↓
apply predicate early
  ↓
emit only matching rows
```

---

# 33. Predicate Pushdown ASCII

Without pushdown:

```text
Page
 ↓
all records
 ↓
executor
 ↓
filter age > 25
 ↓
matching rows
```

With pushdown:

```text
Page
 ↓
scan checks age > 25 immediately
 ↓
only matching records emitted
```

Benefit:

```text
less CPU
less memory
less data movement
```

---

# 34. Projection Pushdown

Projection pushdown means:

```text
read only needed columns as early as possible
```

Very important for:

```text
column stores
Parquet
analytics databases
```

Example:

```sql
SELECT salary FROM employees;
```

Column store reads:

```text
salary column only
```

Not:

```text
id, name, address, department, salary, ...
```

---

# 35. Covering Index

A covering index contains all columns needed by query.

Index:

```text
(id, name, age)
```

Query:

```sql
SELECT name, age
FROM users
WHERE id = 10;
```

Database can answer directly from index.

No table page read.

---

# 36. Covering Index ASCII

```text
Query:
SELECT name, age WHERE id=10

Index Entry:
+----+---------+-----+
| id | name    | age |
+----+---------+-----+
| 10 | Mohamed | 30  |
+----+---------+-----+
        ↓
All needed columns already present
        ↓
Return result
```

Very fast.

---

# 37. LIMIT Operator

Query:

```sql
SELECT * FROM users LIMIT 10;
```

Database may stop after:

```text
10 rows
```

No need to scan full table in many cases.

---

# 38. ORDER BY Interaction

Query:

```sql
SELECT * FROM users ORDER BY created_at;
```

Database may:

```text
use index order
or
perform sort operator
```

If no useful index:

```text
sort can be expensive
```

---

# 39. External Sort ASCII

If data does not fit RAM:

```text
Large Input
    ↓
Split into chunks
    ↓
Sort chunk-1 in memory → temp file 1
Sort chunk-2 in memory → temp file 2
Sort chunk-3 in memory → temp file 3
    ↓
Merge sorted temp files
    ↓
Final sorted output
```

---

# 40. Aggregation Operator

Query:

```sql
SELECT city, COUNT(*)
FROM users
GROUP BY city;
```

Database may use:

```text
hash aggregate
sort aggregate
```

Hash aggregate mental model:

```text
HashMap<city, count>
```

Example:

```text
Bucharest → 10
London    → 20
Paris     → 5
```

---

# 41. Parallel Scan

Large queries may scan multiple page ranges in parallel.

```text
Large Table
│
├── Worker-1 -> Pages 1-100
├── Worker-2 -> Pages 101-200
└── Worker-3 -> Pages 201-300
         ↓
    Merge Results
```

Useful for:

```text
analytics
large scans
parallel query execution
```

---

# 42. Backend API Flow

Spring Boot endpoint:

```text
GET /users?minAge=25
```

Internal flow:

```text
HTTP Request
    ↓
Controller
    ↓
Service
    ↓
Repository / JDBC
    ↓
SQL Query
    ↓
Execution Plan
    ↓
Scan / Index Scan
    ↓
Filter age > 25
    ↓
Projection DTO columns
    ↓
JSON Response
```

---

# 43. Java Operator Interface

```java
public interface Operator {

    Row next();
}
```

Each operator returns one row at a time.

---

# 44. Java Row Model

```java
import java.util.HashMap;
import java.util.Map;

public class Row {

    private final Map<String, Object> values =
            new HashMap<>();

    public void put(String column, Object value) {
        values.put(column, value);
    }

    public Object get(String column) {
        return values.get(column);
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
```

---

# 45. Java TableScan Operator

```java
import java.util.Iterator;
import java.util.List;

public class TableScanOperator implements Operator {

    private final Iterator<Row> iterator;

    public TableScanOperator(List<Row> rows) {
        this.iterator = rows.iterator();
    }

    @Override
    public Row next() {

        if (!iterator.hasNext()) {
            return null;
        }

        return iterator.next();
    }
}
```

Explanation:

```text
TableScanOperator simply returns rows one by one.
```

---

# 46. Java Filter Operator

```java
import java.util.function.Predicate;

public class FilterOperator implements Operator {

    private final Operator child;
    private final Predicate<Row> predicate;

    public FilterOperator(Operator child,
                          Predicate<Row> predicate) {
        this.child = child;
        this.predicate = predicate;
    }

    @Override
    public Row next() {

        while (true) {

            Row row = child.next();

            if (row == null) {
                return null;
            }

            if (predicate.test(row)) {
                return row;
            }
        }
    }
}
```

Explanation:

```text
Filter keeps asking child for rows until one matches.
Non-matching rows are skipped.
```

---

# 47. Java Projection Operator

```java
import java.util.List;

public class ProjectionOperator implements Operator {

    private final Operator child;
    private final List<String> columns;

    public ProjectionOperator(Operator child,
                              List<String> columns) {
        this.child = child;
        this.columns = columns;
    }

    @Override
    public Row next() {

        Row input = child.next();

        if (input == null) {
            return null;
        }

        Row output = new Row();

        for (String column : columns) {
            output.put(column, input.get(column));
        }

        return output;
    }
}
```

Explanation:

```text
Projection creates a smaller row containing only selected columns.
```

---

# 48. Java Pipeline Example

Query:

```sql
SELECT name FROM users WHERE age > 25;
```

Pipeline:

```java
Operator scan =
        new TableScanOperator(users);

Operator filter =
        new FilterOperator(
                scan,
                row -> (int) row.get("age") > 25
        );

Operator projection =
        new ProjectionOperator(
                filter,
                List.of("name")
        );
```

Run:

```java
Row row;

while ((row = projection.next()) != null) {
    System.out.println(row);
}
```

---

# 49. Java Pipeline ASCII

```text
projection.next()
        ↓
filter.next()
        ↓
scan.next()
        ↓
row returned from table
        ↑
filter checks predicate
        ↑
projection keeps selected columns
        ↑
client receives row
```

---

# 50. Full Dry Run

Rows:

```text
{id=1, name=Mohamed, age=30}
{id=2, name=John, age=20}
{id=3, name=Alice, age=35}
```

Query:

```sql
SELECT name
FROM users
WHERE age > 25;
```

Dry run:

```text
1. projection.next()
2. projection asks filter.next()
3. filter asks scan.next()
4. scan returns Mohamed row
5. filter checks 30 > 25 → true
6. projection keeps name
7. output {name=Mohamed}

8. next call
9. scan returns John row
10. filter checks 20 > 25 → false
11. filter asks scan again
12. scan returns Alice row
13. filter checks 35 > 25 → true
14. projection keeps name
15. output {name=Alice}
```

Final output:

```text
{name=Mohamed}
{name=Alice}
```

---

# 51. Production Performance Notes

## Filter performance depends on:

```text
predicate selectivity
index availability
data distribution
statistics
```

## Projection performance depends on:

```text
number of columns
row width
network transfer
columnar vs row storage
```

## Scan performance depends on:

```text
table size
page cache
disk speed
parallelism
indexes
```

---

# 52. Common Slow Query Patterns

```sql
SELECT * FROM huge_table;
```

Problem:

```text
too much data
```

```sql
SELECT * FROM users WHERE LOWER(email) = 'x';
```

Problem:

```text
function may prevent index usage
```

```sql
SELECT * FROM orders WHERE status = 'ACTIVE';
```

If most orders are active:

```text
low selectivity
index may not help
```

---

# 53. Interview Explanation

If interviewer asks:

```text
How do SELECT and WHERE work internally?
```

Strong answer:

```text
The execution engine uses operators. A scan operator reads rows from
storage, a filter operator applies WHERE predicates and discards
non-matching rows, and a projection operator returns only requested
SELECT columns. Rows flow upward through the operator tree.
```

Senior addition:

```text
Databases optimize this using index scans, predicate pushdown,
projection pushdown, covering indexes, and parallel scans.
```

---

# 54. Common Mistakes

## Mistake 1

```text
Thinking WHERE runs before scan
```

Rows must be produced by scan first.

---

## Mistake 2

```text
Thinking SELECT * is harmless
```

It can waste IO, memory, and network bandwidth.

---

## Mistake 3

```text
Thinking table scan is always bad
```

Sometimes table scan is optimal.

---

## Mistake 4

```text
Thinking index scan is always best
```

If many rows match, table scan may be cheaper.

---

## Mistake 5

```text
Ignoring pushdown optimizations
```

Pushdown reduces data movement and CPU work.

---

# 55. Final Mental Model

```text
Scan
    =
source of rows

Filter
    =
WHERE condition

Projection
    =
SELECT columns
```

Full execution:

```text
Storage
   ↓
Scan
   ↓
Filter
   ↓
Projection
   ↓
Result
```

---

# 56. What To Remember

```text
Scan reads rows.

Filter removes non-matching rows.

Projection keeps selected columns.

Table scan reads all rows.

Index scan jumps to matching rows.

Predicate pushdown filters early.

Projection pushdown reads fewer columns.

Covering index can avoid table page access.
```

---

# 57. Next File

```text
011_OrderBy_Limit_Aggregation.md
```

Next you learn:

```text
ORDER BY
LIMIT
COUNT
SUM
GROUP BY
sort operator
top-K query
hash aggregation
sort aggregation
```
