# 009_Query_Execution_Pipeline.md

# MiniDatabase — 009 Query Execution Pipeline

## 0. Why This File Exists

Most developers write:

```sql
SELECT * FROM users WHERE id = 10;
```

and think:

```text
database finds row and returns it
```

But internally a database runs a full pipeline:

```text
SQL text
    ↓
tokenizer
    ↓
parser
    ↓
semantic analyzer
    ↓
logical plan
    ↓
optimizer
    ↓
physical plan
    ↓
execution engine
    ↓
storage engine
    ↓
buffer pool
    ↓
pages / records
    ↓
result
```

This file explains how a query really executes inside a database.

No Mermaid diagrams.

Only ASCII mental models.

---

# 1. One-Line Definition

```text
Query execution pipeline is the internal path a database follows
to convert SQL text into an optimized execution plan and return rows.
```

Simple meaning:

```text
SQL is compiled into a plan,
then the plan is executed.
```

---

# 2. Biggest Mental Model

A database query engine is like:

```text
compiler
+
optimizer
+
runtime execution engine
+
storage engine
```

SQL is not directly executed.

It is processed step by step.

---

# 3. Full Query Pipeline

```text
+----------------------+
| SQL Query            |
+----------------------+
          ↓
+----------------------+
| Tokenizer            |
+----------------------+
          ↓
+----------------------+
| Parser               |
+----------------------+
          ↓
+----------------------+
| Semantic Analyzer    |
+----------------------+
          ↓
+----------------------+
| Logical Plan         |
+----------------------+
          ↓
+----------------------+
| Optimizer            |
+----------------------+
          ↓
+----------------------+
| Physical Plan        |
+----------------------+
          ↓
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
+----------------------+
          ↓
+----------------------+
| Pages / Records      |
+----------------------+
          ↓
+----------------------+
| Result Set           |
+----------------------+
```

---

# 4. Example Query

```sql
SELECT name, age
FROM users
WHERE id = 10;
```

Human meaning:

```text
Get name and age for user id 10.
```

Database meaning:

```text
parse SQL
validate table/columns
choose plan
use index if useful
load page
decode record
project columns
return result
```

---

# 5. SQL Is Like A Programming Language

SQL query:

```text
source code
```

Database engine:

```text
compiler + runtime
```

Internal transformation:

```text
SQL string
   ↓
tokens
   ↓
parse tree
   ↓
logical plan
   ↓
physical plan
   ↓
execution
```

---

# 6. Stage 1 — Tokenizer

Tokenizer splits SQL text into tokens.

Input:

```sql
SELECT name FROM users WHERE id = 10;
```

Tokens:

```text
SELECT
name
FROM
users
WHERE
id
=
10
;
```

---

# 7. Tokenizer Mental Model

```text
Raw SQL string
      ↓
small meaningful words/symbols
```

Example:

```text
"SELECT name FROM users"
      ↓
[SELECT] [name] [FROM] [users]
```

This is similar to lexical analysis in compilers.

---

# 8. Stage 2 — Parser

Parser checks SQL grammar.

Input:

```sql
SELECT name FROM users WHERE id = 10;
```

Parser builds query structure:

```text
SELECT
├── projection: name
├── from: users
└── where: id = 10
```

---

# 9. Parser Error Example

Bad SQL:

```sql
SELEC name FROM users;
```

Parser fails:

```text
SELEC is not valid SELECT keyword
```

This is:

```text
syntax error
```

---

# 10. Parse Tree ASCII

```text
SELECT
│
├── Projection
│   └── name
│
├── From
│   └── users
│
└── Where
    └── id = 10
```

This tree represents the structure of SQL.

---

# 11. Stage 3 — Semantic Analyzer

Syntax may be correct but meaning may be invalid.

Semantic analyzer checks:

```text
table exists?
column exists?
types compatible?
user has permission?
function exists?
```

---

# 12. Semantic Error Example

Query:

```sql
SELECT salary FROM users;
```

If `salary` column does not exist:

```text
syntax is valid
semantic validation fails
```

Error:

```text
column salary does not exist
```

---

# 13. Syntax vs Semantic Error

## Syntax Error

```text
SQL grammar wrong
```

Example:

```sql
SELEC * FROM users;
```

## Semantic Error

```text
SQL grammar correct but meaning wrong
```

Example:

```sql
SELECT unknown_column FROM users;
```

---

# 14. Stage 4 — Logical Plan

Logical plan describes:

```text
what operations are needed
```

It does NOT yet decide exact algorithms.

Query:

```sql
SELECT name, age
FROM users
WHERE id = 10;
```

Logical plan:

```text
Projection(name, age)
        ↓
Filter(id = 10)
        ↓
Scan(users)
```

---

# 15. Logical Plan Tree

```text
+----------------------------+
| Projection                 |
| columns: name, age         |
+----------------------------+
              ↓
+----------------------------+
| Filter                     |
| predicate: id = 10         |
+----------------------------+
              ↓
+----------------------------+
| Scan                       |
| table: users               |
+----------------------------+
```

---

# 16. Logical Plan Mental Model

Logical plan answers:

```text
WHAT should happen?
```

Example:

```text
read users
filter id=10
return name and age
```

But it does not decide:

```text
table scan?
index scan?
hash index?
BTree index?
```

That comes next.

---

# 17. Stage 5 — Optimizer

Optimizer chooses:

```text
best execution strategy
```

It decides:

```text
table scan or index scan?
which index?
join order?
join algorithm?
sort method?
parallel execution?
```

Goal:

```text
lowest estimated cost
```

---

# 18. Optimizer Mental Model

Same SQL can run in many ways.

Query:

```sql
SELECT * FROM users WHERE id = 10;
```

Possible plans:

```text
Plan A:
Full table scan

Plan B:
Hash index lookup

Plan C:
BTree index lookup
```

Optimizer chooses the cheapest.

---

# 19. Optimizer Decision Tree

```text
Query: WHERE id = 10
        ↓
Is useful index available?
        ├── no
        │   └── choose table scan
        │
        └── yes
            ↓
        Is predicate selective?
            ├── yes
            │   └── choose index scan
            │
            └── no
                └── maybe choose table scan
```

---

# 20. Cost-Based Optimization

Optimizer estimates:

```text
CPU cost
disk IO cost
memory cost
rows processed
join cost
sort cost
```

Then chooses plan with lowest estimated cost.

---

# 21. Statistics

Optimizer depends on statistics:

```text
table row count
distinct values
histograms
index selectivity
null count
data distribution
```

Bad statistics can produce bad plans.

---

# 22. Selectivity

Selectivity means:

```text
how much data a predicate filters out
```

High selectivity:

```sql
WHERE id = 10
```

Usually returns:

```text
1 row
```

Great for index.

Low selectivity:

```sql
WHERE country = 'India'
```

May return:

```text
large part of table
```

Index may not help much.

---

# 23. Table Scan vs Index Scan

## Table Scan

```text
read all pages
check all rows
```

Good when:

```text
small table
many rows needed
index not useful
```

## Index Scan

```text
use index
jump to matching rows
```

Good when:

```text
few rows needed
predicate selective
index exists
```

---

# 24. Stage 6 — Physical Plan

Physical plan describes:

```text
HOW exactly to execute
```

Example:

```text
Projection(name, age)
        ↓
IndexScan(users_pkey, id=10)
        ↓
RID(page=100, slot=3)
```

This is executable.

---

# 25. Logical Plan vs Physical Plan

## Logical Plan

```text
Projection
  ↓
Filter
  ↓
Scan
```

## Physical Plan

```text
Projection
  ↓
IndexScan using users_pkey
  ↓
RID lookup
```

Logical plan:

```text
what to do
```

Physical plan:

```text
how to do it
```

---

# 26. Stage 7 — Execution Engine

Execution engine runs the physical plan.

It uses operators:

```text
TableScan
IndexScan
Filter
Projection
Sort
HashJoin
NestedLoopJoin
Aggregate
Limit
```

Operators produce rows.

---

# 27. Volcano Iterator Model

Many databases use iterator-style execution.

Each operator supports:

```text
next()
```

Parent operator asks child:

```text
give me next row
```

Rows flow upward.

Requests flow downward.

---

# 28. Iterator Model ASCII

```text
Client
  ↓ asks next()
Projection.next()
  ↓ asks next()
Filter.next()
  ↓ asks next()
IndexScan.next()
  ↓
Storage Engine
  ↑
Row returned upward
```

Mental model:

```text
request goes down
row comes up
```

---

# 29. Table Scan Operator

Table scan reads all table pages.

Flow:

```text
open table file
    ↓
read page 1
    ↓
decode records
    ↓
return rows
    ↓
read page 2
    ↓
repeat until all pages done
```

---

# 30. Table Scan ASCII

```text
Table File
│
├── Page-1
│   ├── Record-1
│   ├── Record-2
│   └── Record-3
│
├── Page-2
│   ├── Record-4
│   ├── Record-5
│   └── Record-6
│
└── Page-3
    ├── Record-7
    ├── Record-8
    └── Record-9
```

Table scan checks every record.

Cost:

```text
O(N)
```

---

# 31. Index Scan Operator

Index scan uses index first.

Flow:

```text
search index
    ↓
get RID
    ↓
load page
    ↓
read slot
    ↓
decode record
```

---

# 32. Index Scan ASCII

```text
Query: id = 10
        ↓
BTree / Hash Index
        ↓
RID(page=100, slot=3)
        ↓
Buffer Pool loads Page-100
        ↓
Slot-3 gives record offset
        ↓
Read record bytes
        ↓
Decode row
```

---

# 33. Storage Engine Role

Execution engine asks storage engine:

```text
give me row for RID(page=100, slot=3)
```

Storage engine handles:

```text
page lookup
buffer pool
disk read
record decoding
```

---

# 34. Buffer Pool Access

Execution engine does not usually read disk directly.

It asks buffer pool:

```text
give me page 100
```

Buffer pool checks memory.

---

# 35. Buffer Pool ASCII

```text
Need Page-100
      ↓
Buffer Pool
      ↓
Is Page-100 in RAM?
      ├── yes
      │   └── return page immediately
      │
      └── no
          ↓
      read page from disk
          ↓
      put page into buffer pool
          ↓
      return page
```

Cache hit:

```text
fast
```

Cache miss:

```text
slow
```

---

# 36. Page To Row Decode

After page is loaded:

```text
page
 ↓
slot directory
 ↓
record offset
 ↓
record bytes
 ↓
schema decode
 ↓
row object/result
```

---

# 37. Page Decode ASCII

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

# 38. Filter Operator

Filter applies WHERE condition.

Example:

```sql
WHERE age > 25
```

Flow:

```text
row arrives
    ↓
evaluate predicate
    ↓
true? pass upward
false? discard
```

---

# 39. Filter ASCII

```text
Incoming Row
{id=1, name=Mohamed, age=30}
        ↓
Condition: age > 25?
        ↓
true
        ↓
pass row upward
```

For:

```text
{id=2, name=John, age=20}
```

```text
Condition: age > 25?
        ↓
false
        ↓
discard
```

---

# 40. Projection Operator

Projection keeps only requested columns.

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

# 41. Projection ASCII

```text
Full Row
+----+---------+-----------+-----+
| id | name    | city      | age |
+----+---------+-----------+-----+
| 1  | Mohamed | Bucharest | 30  |
+----+---------+-----------+-----+
        ↓
Project name, age
        ↓
+---------+-----+
| name    | age |
+---------+-----+
| Mohamed | 30  |
+---------+-----+
```

---

# 42. Full Query Dry Run

Query:

```sql
SELECT name
FROM users
WHERE age > 25;
```

Table:

```text
+----+---------+-----+
| id | name    | age |
+----+---------+-----+
| 1  | Mohamed | 30  |
| 2  | John    | 20  |
| 3  | Alice   | 35  |
+----+---------+-----+
```

Execution plan:

```text
Projection(name)
        ↓
Filter(age > 25)
        ↓
TableScan(users)
```

Dry run:

```text
Scan row 1: Mohamed age=30
Filter: 30 > 25 true
Projection: keep name
Output: Mohamed

Scan row 2: John age=20
Filter: 20 > 25 false
Discard

Scan row 3: Alice age=35
Filter: 35 > 25 true
Projection: keep name
Output: Alice
```

Final result:

```text
Mohamed
Alice
```

---

# 43. Predicate Pushdown

Predicate pushdown means:

```text
apply filter as early as possible
```

Instead of:

```text
read many rows
send upward
filter later
```

do:

```text
filter during scan
```

This reduces work.

---

# 44. Predicate Pushdown ASCII

Bad:

```text
Storage
  ↓
all rows
  ↓
executor
  ↓
filter
```

Better:

```text
Storage
  ↓
scan + filter
  ↓
only matching rows
  ↓
executor
```

---

# 45. Projection Pushdown

Projection pushdown means:

```text
read only needed columns early
```

Important in:

```text
column stores
Parquet
analytics engines
```

Example:

```sql
SELECT salary FROM employees;
```

Column store can read:

```text
salary column only
```

---

# 46. Covering Index

Covering index contains all columns needed.

Example index:

```text
(id, name, age)
```

Query:

```sql
SELECT name, age FROM users WHERE id = 10;
```

Database can answer from index only.

No table page read.

---

# 47. Covering Index ASCII

```text
Query:
SELECT name, age WHERE id=10

Index Entry:
[id=10][name=Mohamed][age=30]
        ↓
all needed columns already here
        ↓
return result
```

Very fast.

---

# 48. LIMIT Operator

Query:

```sql
SELECT * FROM users LIMIT 10;
```

Database may stop after:

```text
10 rows
```

No need to scan all rows in some cases.

---

# 49. SORT Operator

Query:

```sql
ORDER BY created_at
```

Database may:

```text
use index order
or
sort manually
```

Sort can be expensive.

---

# 50. External Sort ASCII

If data does not fit RAM:

```text
Large Input
    ↓
split into chunks
    ↓
sort chunk-1 in memory → temp file 1
sort chunk-2 in memory → temp file 2
sort chunk-3 in memory → temp file 3
    ↓
merge sorted temp files
    ↓
final sorted output
```

---

# 51. Aggregation Operator

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

# 52. Join Query Preview

Query:

```sql
SELECT *
FROM orders o
JOIN users u
ON o.user_id = u.id;
```

Database must choose:

```text
nested loop join
hash join
merge join
```

Join optimization is a major topic.

---

# 53. Join Plan Tree ASCII

```text
Projection(selected columns)
          ↓
Hash Join
    ┌─────┴─────┐
    ↓           ↓
Scan orders   Scan users
```

---

# 54. Backend API Flow

Spring Boot endpoint:

```text
GET /users/10
```

Internal path:

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
Database Query Pipeline
    ↓
Execution Plan
    ↓
Index Scan
    ↓
Buffer Pool
    ↓
Record Decode
    ↓
ResultSet
    ↓
DTO Response
```

---

# 55. Mini Query Engine Java Idea

A simple mini query engine can model:

```text
TableScan
Filter
Projection
```

Each operator exposes:

```java
next()
```

---

# 56. Java Operator Interface

```java
public interface Operator {

    Row next();
}
```

Each operator returns one row at a time.

---

# 57. Java TableScan Operator

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

---

# 58. Java Filter Operator

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

---

# 59. Java Projection Operator

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

---

# 60. Java Operator Pipeline

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

Execution:

```java
Row row;

while ((row = projection.next()) != null) {
    System.out.println(row);
}
```

---

# 61. Java Pipeline ASCII

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

# 62. EXPLAIN Plan

Databases expose chosen plan.

Example:

```sql
EXPLAIN SELECT * FROM users WHERE id = 10;
```

Possible output:

```text
Index Scan using users_pkey
```

This tells:

```text
database used index
not full table scan
```

---

# 63. EXPLAIN ANALYZE

```sql
EXPLAIN ANALYZE
SELECT * FROM users WHERE id = 10;
```

Shows:

```text
actual execution time
actual rows
actual loops
chosen plan
```

Very important production skill.

---

# 64. Slow Query Causes

Common causes:

```text
missing index
bad statistics
wrong join order
large sort
full table scan
low selectivity predicate
too many rows returned
cold buffer pool
lock waits
```

---

# 65. OLTP vs OLAP Query Execution

## OLTP

```text
small queries
index lookups
low latency
transactions
```

## OLAP

```text
large scans
aggregations
parallel execution
columnar processing
```

---

# 66. Full Final Mental Model

```text
SQL text
   ↓
Tokenize
   ↓
Parse
   ↓
Validate
   ↓
Build logical plan
   ↓
Optimize using statistics
   ↓
Create physical plan
   ↓
Run operators
   ↓
Access indexes/pages
   ↓
Decode records
   ↓
Return result
```

---

# 67. Interview Explanation

If interviewer asks:

```text
How does a database execute a query?
```

Strong answer:

```text
A database parses SQL into a parse tree, validates tables and columns,
builds a logical plan, optimizes it using statistics and cost estimation,
generates a physical execution plan, and then runs execution operators
such as scans, filters, joins, sorts, and aggregates against the storage engine.
```

Senior addition:

```text
The execution engine accesses indexes and pages through the buffer pool,
decodes record bytes using schema metadata, and returns rows to the client.
```

---

# 68. Common Mistakes

## Mistake 1

```text
Thinking SQL directly reads rows
```

Wrong.

SQL goes through parser, planner, optimizer, and executor.

---

## Mistake 2

```text
Thinking index always used
```

Optimizer may choose table scan if index is not useful.

---

## Mistake 3

```text
Ignoring statistics
```

Bad stats can produce bad query plans.

---

## Mistake 4

```text
Ignoring buffer pool
```

Page cache heavily affects performance.

---

## Mistake 5

```text
Thinking slow query always means missing index
```

Could be joins, sorts, bad stats, cold cache, lock waits, or too many rows.

---

# 69. What To Remember

```text
Database query execution is a pipeline.

SQL is parsed, validated, planned, optimized, and executed.

Optimizer chooses table scan or index scan.

Execution engine runs operators.

Storage engine returns pages and records.

Buffer pool decides RAM hit or disk read.

EXPLAIN ANALYZE shows the real plan and timing.
```

---

# 70. Next File

```text
010_Filter_Projection_Scan.md
```

Next you learn:

```text
table scan
index scan
filter operator
projection operator
predicate evaluation
why WHERE and SELECT work internally
```
