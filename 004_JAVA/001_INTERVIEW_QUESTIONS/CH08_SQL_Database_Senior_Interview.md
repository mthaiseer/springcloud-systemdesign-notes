# CH 08 --- SQL & Database

## Senior Java Distributed Backend Interview Series

**Target:** \~50 interview questions\
**Goal:** Master SQL querying, indexing, query plans, transactions,
locks, connection pooling, replication, partitioning, sharding, and
production database troubleshooting.

------------------------------------------------------------------------

# Chapter Map

``` text
CH 08 — SQL & DATABASE (~50 Q)
|
+-- 1. SQL Querying (10)
|   +-- JOINs
|   +-- GROUP BY / HAVING
|   +-- Subqueries
|   +-- EXISTS
|   +-- CTE
|   +-- Window functions
|   +-- Top-N per group
|   +-- NULL
|   +-- UNION
|   +-- Query execution order
|
+-- 2. Indexes / Query Optimization (14) ★★★
|   +-- B-tree
|   +-- Index lookup
|   +-- Composite indexes
|   +-- Leftmost-prefix idea
|   +-- Covering/index-only access
|   +-- Selectivity/cardinality
|   +-- Clustered concepts
|   +-- Index costs
|   +-- Functions on indexed columns
|   +-- LIKE
|   +-- ORDER BY
|   +-- EXPLAIN
|   +-- Statistics
|   +-- Slow-query methodology
|
+-- 3. Transactions / Concurrency (10) ★★★
|   +-- ACID
|   +-- Isolation
|   +-- MVCC
|   +-- Locks
|   +-- Row vs table locking
|   +-- Deadlocks
|   +-- Lost update
|   +-- Optimistic/pessimistic
|   +-- Long transactions
|   +-- Hot rows
|
+-- 4. HikariCP / Connection Management (6) ★★★
|   +-- Why pool
|   +-- Pool sizing
|   +-- Exhaustion
|   +-- Timeouts
|   +-- Leaks
|   +-- Production metrics
|
+-- 5. Scaling Data (10) ★★★
    +-- Replication
    +-- Primary/replica
    +-- Replication lag
    +-- Read-after-write
    +-- Partitioning
    +-- Partition pruning
    +-- Sharding
    +-- Shard keys
    +-- Resharding
    +-- Choosing scale strategy
```

------------------------------------------------------------------------

# Big Picture

``` text
Spring Service
      |
      v
JPA / Hibernate
      |
      v
JDBC
      |
      v
HikariCP
      |
      v
PostgreSQL / SQL Database
      |
      +--> Parser / Planner
      +--> Statistics
      +--> Indexes
      +--> Buffer Cache
      +--> Locks / MVCC
      +--> WAL / Durability
      |
      v
Storage
```

For a senior backend engineer, the key principle is:

``` text
ORM problem?
   |
   v
Look at generated SQL
   |
   v
Look at query plan
   |
   v
Look at indexes / rows / locks
   |
   v
Look at DB + pool metrics
```

------------------------------------------------------------------------

# 1. SQL Querying --- 10 Questions

## Q1. Explain INNER, LEFT, RIGHT and FULL JOIN.

### INNER JOIN

Returns matching rows.

``` text
A           B

1           1
2           2
3           4

INNER JOIN
-----------
1
2
```

### LEFT JOIN

All rows from left plus matches from right.

``` text
LEFT JOIN
---------
1 -> match
2 -> match
3 -> NULL
```

### RIGHT JOIN

Symmetric concept: preserve the right side.

### FULL OUTER JOIN

Preserves unmatched rows from both sides where supported.

Senior point: understand the semantics, not just syntax.

------------------------------------------------------------------------

## Q2. WHERE vs HAVING?

``` text
WHERE
filters rows BEFORE grouping

HAVING
filters groups AFTER aggregation
```

Example:

``` sql
SELECT customer_id, COUNT(*)
FROM orders
WHERE status = 'PAID'
GROUP BY customer_id
HAVING COUNT(*) >= 10;
```

Mental flow:

``` text
orders
  |
WHERE status='PAID'
  |
GROUP BY customer
  |
COUNT
  |
HAVING count >= 10
```

------------------------------------------------------------------------

## Q3. Subquery vs JOIN?

Example subquery:

``` sql
SELECT *
FROM orders
WHERE customer_id IN (
    SELECT id
    FROM customers
    WHERE country = 'RO'
);
```

Equivalent logic may often be expressed with a join.

Do not memorize "JOIN is always faster." Modern optimizers may transform
logically equivalent forms.

Choose based on:

-   semantics
-   readability
-   plan
-   cardinality
-   actual database behavior

------------------------------------------------------------------------

## Q4. EXISTS vs IN?

`EXISTS` expresses existence.

``` sql
SELECT c.*
FROM customers c
WHERE EXISTS (
    SELECT 1
    FROM orders o
    WHERE o.customer_id = c.id
);
```

Mental model:

``` text
Customer
   |
Does at least one matching Order exist?
   |
 YES -> include
```

`IN` expresses membership in a set.

Do not rely on folklore such as "EXISTS is always faster." Inspect the
plan.

Be especially careful with `NOT IN` and `NULL` semantics.

------------------------------------------------------------------------

## Q5. What is a CTE?

CTE = Common Table Expression.

``` sql
WITH high_value AS (
    SELECT *
    FROM orders
    WHERE total > 1000
)
SELECT *
FROM high_value
WHERE status = 'PAID';
```

Benefits:

-   readability
-   decomposition
-   recursion support where available
-   reusable logical query sections

Whether a CTE is materialized or optimized/inlined depends on the
database/version/query.

------------------------------------------------------------------------

## Q6. What are window functions?

Window functions compute across related rows without collapsing them
like `GROUP BY`.

Example:

``` sql
SELECT
    customer_id,
    id,
    total,
    ROW_NUMBER() OVER (
        PARTITION BY customer_id
        ORDER BY total DESC
    ) AS rn
FROM orders;
```

``` text
Customer A
Order1 500 -> rn=1
Order2 300 -> rn=2

Customer B
Order3 900 -> rn=1
Order4 100 -> rn=2
```

Useful functions include:

``` text
ROW_NUMBER
RANK
DENSE_RANK
LAG
LEAD
SUM(...) OVER
AVG(...) OVER
```

------------------------------------------------------------------------

## Q7. How do you find top N rows per group?

Classic window-function pattern:

``` sql
WITH ranked AS (
    SELECT
        o.*,
        ROW_NUMBER() OVER (
            PARTITION BY customer_id
            ORDER BY total DESC
        ) AS rn
    FROM orders o
)
SELECT *
FROM ranked
WHERE rn <= 3;
```

This is a common SQL interview question.

------------------------------------------------------------------------

## Q8. How does NULL behave?

`NULL` means unknown/missing, not zero or empty string.

Wrong:

``` sql
WHERE deleted_at = NULL
```

Correct:

``` sql
WHERE deleted_at IS NULL
```

Three-valued logic matters:

``` text
TRUE
FALSE
UNKNOWN
```

This is why constructs such as `NOT IN` can behave unexpectedly when the
set contains `NULL`.

------------------------------------------------------------------------

## Q9. UNION vs UNION ALL?

``` text
UNION
combine results
+
remove duplicates

UNION ALL
combine results
+
keep duplicates
```

Duplicate elimination has work/cost.

Use `UNION ALL` when deduplication is not required.

------------------------------------------------------------------------

## Q10. What is logical SQL query processing order?

A useful mental model:

``` text
FROM / JOIN
    |
WHERE
    |
GROUP BY
    |
HAVING
    |
SELECT
    |
DISTINCT
    |
ORDER BY
    |
LIMIT / OFFSET
```

Physical execution can differ because the optimizer rewrites/plans the
query.

The distinction is:

``` text
logical semantics
      !=
physical execution plan
```

------------------------------------------------------------------------

# 2. Indexes / Query Optimization --- 14 Questions

## Q11. What is a B-tree index?

Conceptually:

``` text
                 [40]
               /      \
           [10,20]    [60,80]
           / |  \      / | \
         leaves ...  leaves ...
```

Rather than scanning every row:

``` text
Table Scan
row1
row2
row3
...
row10,000,000
```

an index can navigate to a smaller candidate range.

B-tree-style indexes are useful for equality, ranges and ordered access
depending on query/index design.

------------------------------------------------------------------------

## Q12. How does an index lookup work?

Suppose:

``` sql
SELECT *
FROM users
WHERE email = ?;
```

Index:

``` text
email -> row locator / tuple reference
```

Conceptually:

``` text
B-tree traversal
      |
find email key
      |
locate table row
      |
return row
```

If required data is available from the index alone, additional table
access may sometimes be avoided.

------------------------------------------------------------------------

## Q13. What is a composite index?

``` sql
CREATE INDEX idx_orders_customer_status_created
ON orders(customer_id, status, created_at);
```

Ordered conceptually as:

``` text
customer_id
    |
 status
    |
created_at
```

Useful query shapes often begin with leading indexed columns.

Example:

``` sql
WHERE customer_id = ?
  AND status = ?
ORDER BY created_at
```

------------------------------------------------------------------------

## Q14. Explain the leftmost-prefix idea.

For an index:

``` text
(A, B, C)
```

Commonly useful access patterns include:

``` text
A
A,B
A,B,C
```

A query on only `B` usually cannot exploit the ordered leading structure
in the same way.

But do not reduce indexing to one memorized rule; actual optimizer
choices depend on DB capabilities, statistics and query shape.

------------------------------------------------------------------------

## Q15. What is a covering index / index-only access?

Query:

``` sql
SELECT status, created_at
FROM orders
WHERE customer_id = ?;
```

If the index contains all required search/output data:

``` text
Index
(customer_id, status, created_at)
          |
          v
possibly satisfy query
without normal table-row lookup
```

Database-specific visibility/storage rules still matter.

Benefits can include fewer random accesses and less I/O.

Trade-off: wider indexes cost more storage and writes.

------------------------------------------------------------------------

## Q16. What are selectivity and cardinality?

High selectivity example:

``` text
email = unique-ish
```

Low selectivity:

``` text
is_active = true/false
```

An index returning most of the table may be less useful than a scan.

``` text
Query returns 80% rows
        |
index lookup + table access?
        |
maybe more expensive
than sequential scan
```

The optimizer estimates this using statistics.

------------------------------------------------------------------------

## Q17. What does "clustered index" mean conceptually?

The term varies by database.

General idea:

``` text
Index/storage organization
influences physical row ordering/locality
```

Do not assume SQL Server/InnoDB/PostgreSQL use identical storage/index
terminology.

In senior interviews, state which database you are discussing.

------------------------------------------------------------------------

## Q18. Why not index every column?

Indexes improve reads but cost:

``` text
INSERT
  |
update table
  |
update index A
update index B
update index C
...
```

Costs include:

-   storage
-   write amplification
-   maintenance
-   cache pressure
-   planning complexity
-   slower inserts/updates/deletes

Index based on real access patterns.

------------------------------------------------------------------------

## Q19. Why can functions on indexed columns hurt index usage?

Example:

``` sql
WHERE LOWER(email) = 'a@b.com'
```

A normal index on `email` may not directly match the transformed
expression.

Possible solution in databases supporting expression/function indexes:

``` sql
CREATE INDEX ...
ON users (LOWER(email));
```

Another classic:

``` sql
WHERE DATE(created_at) = ?
```

Often better expressed as a range:

``` sql
WHERE created_at >= :start
  AND created_at < :end
```

This can be more index-friendly.

------------------------------------------------------------------------

## Q20. How does LIKE affect indexing?

Potentially index-friendly:

``` sql
WHERE name LIKE 'Moh%'
```

Leading wildcard:

``` sql
WHERE name LIKE '%hamed'
```

typically cannot use ordinary B-tree prefix ordering effectively.

For substring/full-text search, consider database-specific
full-text/trigram/search-engine approaches.

------------------------------------------------------------------------

## Q21. Can indexes help ORDER BY?

Query:

``` sql
SELECT *
FROM orders
WHERE customer_id = ?
ORDER BY created_at DESC
LIMIT 20;
```

A suitable index such as:

``` text
(customer_id, created_at)
```

may support both filtering and ordered retrieval.

Without suitable order support:

``` text
filter rows
   |
sort
   |
limit
```

With suitable index:

``` text
seek customer range
   |
read in desired order
   |
stop after 20
```

------------------------------------------------------------------------

## Q22. What is EXPLAIN?

`EXPLAIN` shows the database's planned execution strategy.

Look for concepts such as:

``` text
scan type
index usage
join algorithm
estimated rows
actual rows
sort
loops
cost/time
buffers/I/O
```

With execution-enabled variants such as `EXPLAIN ANALYZE`, be careful:
the query actually runs.

Senior question:

> The query is slow. What do you do?

Good answer:

``` text
capture query
   |
EXPLAIN / execution plan
   |
compare estimated vs actual rows
   |
inspect scans / joins / sorts
   |
inspect indexes/statistics
   |
measure after change
```

------------------------------------------------------------------------

## Q23. Why do database statistics matter?

Optimizer decisions depend on estimated data distribution.

``` text
SQL
 |
statistics
 |
estimated cardinality
 |
join order
scan choice
index choice
```

Stale/inaccurate statistics can produce poor plans.

A key diagnostic signal:

``` text
estimated rows = 10
actual rows    = 1,000,000
```

That estimation error can cascade into bad join/scan choices.

------------------------------------------------------------------------

## Q24. Give a slow-query optimization methodology.

``` text
1. Capture exact SQL + parameters/query shape
2. Measure latency and frequency
3. Inspect execution plan
4. Compare estimated vs actual rows
5. Check rows scanned vs returned
6. Check indexes
7. Check join order/algorithms
8. Check sorts/temp work
9. Check lock waits
10. Check I/O/cache
11. Rewrite/index only with evidence
12. Re-measure under realistic load
```

Do not start with "add an index."

------------------------------------------------------------------------

# 3. Transactions / Concurrency --- 10 Questions

## Q25. Explain ACID from the database perspective.

``` text
Atomicity   -> commit all / rollback all
Consistency -> preserve defined invariants
Isolation   -> concurrency semantics
Durability  -> committed changes survive according to DB guarantees
```

This builds directly on CH06.

------------------------------------------------------------------------

## Q26. What are standard isolation levels?

``` text
READ UNCOMMITTED
READ COMMITTED
REPEATABLE READ
SERIALIZABLE
```

Increasing isolation usually trades some concurrency for stronger
anomaly prevention.

Exact behavior is database-specific.

------------------------------------------------------------------------

## Q27. What is MVCC?

MVCC = Multi-Version Concurrency Control.

Conceptually:

``` text
Row versions

v1: balance=100
v2: balance=120
```

Different transactions may observe different versions according to their
snapshots/isolation rules.

Goal:

``` text
readers and writers
interfere less
```

MVCC does not mean "no locks exist."

Writes and certain reads can still lock/conflict.

------------------------------------------------------------------------

## Q28. What database locks should a backend engineer know?

Conceptually:

``` text
Shared/read-related locks
Exclusive/write locks
Row-level locks
Table-level locks
Predicate/range concepts
Advisory locks
```

Exact names/behavior vary by database.

Know especially:

``` text
UPDATE
DELETE
SELECT ... FOR UPDATE
```

and how they interact with transactions.

------------------------------------------------------------------------

## Q29. Row lock vs table lock?

``` text
Row lock
   |
smaller locked scope
   |
better concurrency

Table lock
   |
larger scope
   |
more blocking potential
```

But implementation details are DB-specific, and one SQL statement can
affect many rows.

Poor indexing can increase the work and contention of an update even
when conceptual row locks are used.

------------------------------------------------------------------------

## Q30. What is a deadlock?

``` text
TX A
lock Row1
wait Row2

TX B
lock Row2
wait Row1
```

Cycle:

``` text
TX A ----waits----> TX B
 ^                   |
 |                   |
 +------waits--------+
```

The database detects the cycle and aborts a victim transaction.

Mitigate with consistent lock order, short transactions, appropriate
indexes and safe retry logic.

------------------------------------------------------------------------

## Q31. What is a lost update?

``` text
stock = 10

TX A                  TX B
read 10               read 10
write 9               write 9

Expected 8
Actual   9
```

Solutions:

-   atomic SQL
-   optimistic version
-   pessimistic locking
-   stronger concurrency design

------------------------------------------------------------------------

## Q32. Optimistic vs pessimistic concurrency?

``` text
OPTIMISTIC
read without holding write lock
detect conflict at write/version check

PESSIMISTIC
lock resource
block competitors
```

Use contention level and business semantics to choose.

------------------------------------------------------------------------

## Q33. Why are long transactions dangerous?

``` text
BEGIN
 |
SQL
 |
remote call 5 sec
 |
more SQL
 |
COMMIT
```

Potential impact:

``` text
connection held
locks held
MVCC cleanup/version retention pressure
contention
larger rollback window
pool exhaustion
```

Keep transactions focused and short.

------------------------------------------------------------------------

## Q34. What is a hot row?

Example:

``` text
global_counter row

1000 requests/sec
       |
       v
UPDATE same row
```

Even with a powerful DB:

``` text
all writers
   |
same lock/contention point
   |
serialization
```

Solutions depend on semantics:

-   sharded counters
-   append/event model
-   batching
-   partitioning by key
-   avoid unnecessary shared mutable row
-   Redis/streaming approaches where appropriate

------------------------------------------------------------------------

# 4. HikariCP / Connection Management --- 6 Questions

## Q35. Why use a database connection pool?

Creating DB connections repeatedly is expensive.

Without pool:

``` text
Request
 |
TCP/TLS/auth connection setup
 |
SQL
 |
close
```

With pool:

``` text
Request
 |
borrow existing connection
 |
SQL
 |
return connection
```

HikariCP is commonly used in Spring Boot.

------------------------------------------------------------------------

## Q36. How do you size a connection pool?

Bad rule:

``` text
more connections = more throughput
```

Too many connections can overwhelm the DB.

Consider:

``` text
DB CPU/cores
DB max connections
number of app instances
query duration
transaction duration
traffic/concurrency
other workloads
```

Fleet-level thinking:

``` text
20 pods
x 30 connections
=
600 possible app connections
```

Always size per application *and* across the fleet.

------------------------------------------------------------------------

## Q37. What causes connection-pool exhaustion?

``` text
Hikari active = max
       |
new requests need connection
       |
wait
       |
timeout
```

Common causes:

``` text
slow SQL
lock waits
long transactions
remote calls inside transaction
REQUIRES_NEW
too-small pool
connection leak
DB degradation
traffic spike
```

Do not automatically increase pool size.

------------------------------------------------------------------------

## Q38. What pool timeouts matter conceptually?

Know the roles of:

``` text
connection acquisition timeout
idle timeout
max lifetime
validation/keepalive-related settings
```

Also distinguish them from:

``` text
JDBC query timeout
transaction timeout
HTTP downstream timeout
```

A timeout should bound a specific resource wait, not be one arbitrary
global number.

------------------------------------------------------------------------

## Q39. What is a connection leak?

Application obtains a connection and fails to return/close it correctly.

With normal Spring/JPA usage, infrastructure handles much of this
lifecycle, but manual JDBC mistakes can still happen.

``` text
borrow
 |
work
 |
exception
 |
missing close
 |
connection never returns
```

Use try-with-resources for manual JDBC and pool leak diagnostics when
needed.

------------------------------------------------------------------------

## Q40. Which Hikari metrics do you monitor?

Conceptually:

``` text
active
idle
total
pending/waiting
acquisition time
timeouts
usage/hold duration
```

Correlate:

``` text
Hikari pending ↑
      |
DB query latency ↑ ?
lock waits ↑ ?
transaction duration ↑ ?
traffic ↑ ?
```

Pool metrics are often a symptom map to the actual database problem.

------------------------------------------------------------------------

# 5. Scaling Data --- 10 Questions

## Q41. What is database replication?

Replication copies data from one DB node to another.

``` text
        Primary
           |
     replication stream
       /          \
      v            v
 Replica 1      Replica 2
```

Goals can include:

-   high availability
-   read scaling
-   disaster recovery
-   geographically closer reads

Replication is not the same as backup.

------------------------------------------------------------------------

## Q42. Primary vs replica architecture?

Typical:

``` text
Writes
  |
  v
Primary
  |
replication
  |
  +--> Replica A -> reads
  |
  +--> Replica B -> reads
```

Benefits:

``` text
read load distributed
```

Trade-offs:

``` text
replication lag
failover complexity
consistency issues
routing complexity
```

------------------------------------------------------------------------

## Q43. What is replication lag?

``` text
Primary:
UPDATE order status='PAID'
COMMIT
    |
    | replication delay
    v
Replica still:
status='PENDING'
```

Application:

``` text
write to primary
     |
immediately read replica
     |
stale result
```

This is a major distributed-data interview scenario.

------------------------------------------------------------------------

## Q44. How do you provide read-after-write consistency with replicas?

Options include:

``` text
1. Route critical post-write reads to primary

2. Sticky/session routing for a period

3. Track replication position/version
   and read only sufficiently caught-up replica

4. Accept stale reads where business permits
```

Do not require strong consistency for every read if the product does not
need it.

------------------------------------------------------------------------

## Q45. What is table partitioning?

Partitioning splits one logical table into physical partitions.

Example by date:

``` text
orders
 |
 +-- orders_2025
 +-- orders_2026_q1
 +-- orders_2026_q2
 +-- orders_2026_q3
```

Common strategies:

``` text
range
list
hash
```

Potential benefits:

-   pruning
-   manageability
-   archival
-   maintenance
-   certain large-table workloads

Partitioning does not automatically make every query faster.

------------------------------------------------------------------------

## Q46. What is partition pruning?

Query:

``` sql
SELECT *
FROM orders
WHERE created_at >= '2026-08-01'
  AND created_at <  '2026-09-01';
```

If partitioned by date:

``` text
2026_Q1  X
2026_Q2  X
2026_Q3  -> inspect relevant partition
2026_Q4  X
```

The planner avoids irrelevant partitions when predicates allow pruning.

Partition key and query shape must align.

------------------------------------------------------------------------

## Q47. What is sharding?

Sharding distributes data across independent database nodes.

``` text
Users 1-1M       -> Shard A
Users 1M-2M      -> Shard B
Users 2M-3M      -> Shard C
```

Hash example:

``` text
hash(user_id) % N
```

Unlike table partitioning inside one DB system, application/distributed
sharding introduces major routing and operational complexity.

------------------------------------------------------------------------

## Q48. What makes a good shard key?

Good shard key goals:

``` text
high cardinality
even distribution
common query locality
stable
minimal cross-shard operations
```

Bad:

``` text
country
```

if 70% of users are in one country.

Bad:

``` text
timestamp monotonically routed
```

if it creates one hot shard.

Example candidate:

``` text
customer_id
```

for a multi-tenant system where most queries stay within a customer.

------------------------------------------------------------------------

## Q49. Why is resharding hard?

Suppose:

``` text
2 shards
hash(key) % 2
```

You add a third:

``` text
hash(key) % 3
```

Many keys remap.

Problems:

``` text
data movement
dual reads/writes
routing changes
consistency
downtime risk
backfill
validation
```

Approaches may include:

-   consistent hashing
-   virtual shards
-   shard maps/directories
-   online migration workflows

Design for growth before reaching emergency capacity.

------------------------------------------------------------------------

## Q50. Replication vs partitioning vs sharding: when?

``` text
Problem: READ SCALE / HA
        |
        v
   REPLICATION

Problem: HUGE TABLE MANAGEMENT
        |
        v
   PARTITIONING

Problem: ONE DB NODE CANNOT HANDLE
        data/write/throughput scale
        |
        v
      SHARDING
```

Do not jump to sharding early.

A well-designed single relational database with good indexes, caching,
replicas and partitioning can handle very large workloads.

------------------------------------------------------------------------

# Senior Scenario 1 --- Query Suddenly Becomes Slow

Yesterday:

``` text
20 ms
```

Today:

``` text
4 sec
```

Investigation:

``` text
Same SQL?
   |
same parameters/data distribution?
   |
execution plan changed?
   |
statistics stale?
   |
table grew?
   |
index missing/bloated?
   |
lock wait?
   |
cache cold?
```

Senior lesson: performance can change because data distribution and
optimizer decisions change even when code does not.

------------------------------------------------------------------------

# Senior Scenario 2 --- Index Exists but Query Scans Table

Query:

``` sql
SELECT *
FROM users
WHERE LOWER(email) = ?;
```

Index:

``` text
INDEX(email)
```

Problem:

``` text
query expression
LOWER(email)

!=

indexed expression
email
```

Potential fix:

``` text
expression index
or normalized stored value
```

depending on requirements/database.

------------------------------------------------------------------------

# Senior Scenario 3 --- Hikari Pool Exhausted

``` text
API p99 = 12 sec
Hikari:
active = 30
idle = 0
pending = 170
```

Do not immediately set:

``` text
maximumPoolSize = 200
```

First:

``` text
Which queries/transactions hold connections?
          |
          +--> slow query?
          +--> lock wait?
          +--> remote call in TX?
          +--> DB CPU saturated?
```

Increasing pool size can convert application waiting into database
overload.

------------------------------------------------------------------------

# Senior Scenario 4 --- Read Replica Shows Old Data

``` text
POST /orders
    |
write primary
    |
201 Created
    |
GET /orders/123
    |
read replica
    |
404
```

Cause:

``` text
replication lag
```

Possible policy:

``` text
newly created resource
     |
read primary for consistency window
```

or another consistency-aware routing strategy.

------------------------------------------------------------------------

# Senior Scenario 5 --- Hot Inventory Row

Flash sale:

``` text
product stock row
      |
100,000 buyers
      |
UPDATE same row
```

Potential effects:

``` text
lock queue
deadlocks/retries
DB latency
connection hold time
Hikari exhaustion
API timeout
```

Possible strategies:

-   atomic conditional decrement
-   queue/order serialization
-   partition inventory
-   reservation model
-   admission control
-   cache only with correct durability/consistency design

------------------------------------------------------------------------

# Senior Scenario 6 --- OFFSET Pagination Degrades

``` sql
LIMIT 50 OFFSET 0       -> fast
LIMIT 50 OFFSET 1000    -> okay
LIMIT 50 OFFSET 5000000 -> slow
```

Why?

``` text
DB must locate/skip large preceding result set
```

For feed-like navigation:

``` text
WHERE (created_at,id) < (:lastCreated,:lastId)
ORDER BY created_at DESC, id DESC
LIMIT 50
```

Use a deterministic cursor/order.

------------------------------------------------------------------------

# Mini SQL Drill 1 --- Second Highest Salary

Window approach:

``` sql
WITH ranked AS (
    SELECT
        employee_id,
        salary,
        DENSE_RANK() OVER (
            ORDER BY salary DESC
        ) AS rnk
    FROM employee
)
SELECT *
FROM ranked
WHERE rnk = 2;
```

Know the difference between:

``` text
ROW_NUMBER
RANK
DENSE_RANK
```

------------------------------------------------------------------------

# Mini SQL Drill 2 --- Top 3 Orders per Customer

``` sql
WITH ranked AS (
    SELECT
        o.*,
        ROW_NUMBER() OVER (
            PARTITION BY customer_id
            ORDER BY total DESC
        ) AS rn
    FROM orders o
)
SELECT *
FROM ranked
WHERE rn <= 3;
```

------------------------------------------------------------------------

# Mini SQL Drill 3 --- Customers Without Orders

``` sql
SELECT c.*
FROM customers c
WHERE NOT EXISTS (
    SELECT 1
    FROM orders o
    WHERE o.customer_id = c.id
);
```

Often preferable to tricky `NOT IN` + NULL semantics.

------------------------------------------------------------------------

# Mini SQL Drill 4 --- Duplicate Emails

``` sql
SELECT email, COUNT(*)
FROM users
GROUP BY email
HAVING COUNT(*) > 1;
```

Then senior follow-up:

> How would you prevent duplicates?

Use a database uniqueness constraint/index where the invariant requires
uniqueness.

Do not rely only on:

``` text
SELECT first
then INSERT
```

because concurrent requests can race.

------------------------------------------------------------------------

# Mini SQL Drill 5 --- Atomic Inventory Reservation

``` sql
UPDATE inventory
SET stock = stock - 1
WHERE product_id = ?
  AND stock > 0;
```

Then:

``` text
affected rows = 1 -> reserved
affected rows = 0 -> out of stock / missing
```

This avoids application-level read-then-write lost updates.

------------------------------------------------------------------------

# Index Design Example

Query:

``` sql
SELECT id, total, created_at
FROM orders
WHERE customer_id = ?
  AND status = 'PAID'
ORDER BY created_at DESC
LIMIT 20;
```

Candidate index:

``` text
(customer_id, status, created_at)
```

Reasoning:

``` text
customer_id equality
      |
status equality
      |
created_at ordering/range
```

But verify using the actual execution plan and workload.

------------------------------------------------------------------------

# EXPLAIN Mental Model

``` text
SQL
 |
 v
Planner
 |
 +--> Statistics
 |
 +--> Available indexes
 |
 +--> Join alternatives
 |
 v
Plan
 |
 +--> Scan
 +--> Join
 +--> Sort
 +--> Aggregate
 |
 v
Execute
```

Key question:

``` text
Estimated rows
      vs
Actual rows
```

Large mismatch often explains a poor plan.

------------------------------------------------------------------------

# Query Optimization Decision Tree

``` text
Slow SQL
  |
  v
Waiting on lock?
  |
 +YES -> transaction/locking investigation
 |
 NO
  |
  v
Rows scanned >> rows returned?
  |
 +YES -> index/query selectivity
 |
 NO
  |
  v
Sort/aggregate expensive?
  |
  v
Can index/order/query shape improve?
  |
  v
Join cardinality estimates wrong?
  |
  v
statistics / data distribution
```

------------------------------------------------------------------------

# Hikari Capacity Mental Model

``` text
30 connections
      |
average DB hold = 100 ms
      |
theoretical service capacity
very different from
      |
average DB hold = 5 sec
```

Little's-Law intuition:

``` text
Concurrency ≈ Throughput × Time
```

As connection hold time rises, required concurrency/pool occupancy
rises.

Do not use this as a simplistic sizing formula without measuring DB
capacity.

------------------------------------------------------------------------

# Database + JPA Connection

From CH07:

``` text
JPA Query
   |
Hibernate generates SQL
   |
SQL planner
   |
Index / Scan
   |
Rows
   |
Hibernate maps entities
```

A JPA-level change can alter:

-   SQL shape
-   number of SQL statements
-   join cardinality
-   transaction duration
-   pool occupancy

------------------------------------------------------------------------

# Database + Spring Transaction Connection

From CH06:

``` text
@Transactional
      |
borrow Hikari connection
      |
BEGIN
      |
SQL
      |
locks / MVCC
      |
COMMIT
      |
return connection
```

Long transaction:

``` text
longer lock lifetime
+
longer connection hold
+
higher Hikari occupancy
```

------------------------------------------------------------------------

# Database + Redis Preview

``` text
Request
 |
Cache lookup
 |
 +-- HIT -> fast response
 |
 +-- MISS
       |
       v
     Database
```

Caching can reduce DB load, but introduces invalidation/staleness
problems covered in CH12.

------------------------------------------------------------------------

# Production Debugging --- Slow API

``` text
API p99 ↑
   |
trace
   |
DB span ↑?
   |
 +-- NO -> other layer
 |
 +-- YES
       |
       v
query slow or waiting?
       |
 +-----+------+
 |            |
CPU/I/O      LOCK
 |            |
EXPLAIN      blocking TX
```

------------------------------------------------------------------------

# Production Debugging --- Database CPU 100%

``` text
DB CPU 100%
   |
top SQL by total CPU/time
   |
new query?
   |
plan regression?
   |
full scans?
   |
N+1 volume?
   |
traffic spike?
   |
missing index?
```

Focus on total workload impact, not only the single slowest query.

------------------------------------------------------------------------

# Production Debugging --- Deadlocks

``` text
Deadlock rate ↑
     |
capture deadlock graph/log
     |
identify TX A / TX B
     |
lock acquisition order
     |
which statements?
     |
can order be standardized?
     |
can transactions be shorter?
```

------------------------------------------------------------------------

# Production Debugging --- Replica Lag

``` text
Replica lag ↑
    |
write rate spike?
    |
large transaction?
    |
replica resource saturation?
    |
network?
    |
long-running replica query?
```

Application must also define what stale reads are acceptable.

------------------------------------------------------------------------

# Critical Comparison Sheet

``` text
WHERE                  HAVING
row filtering          group filtering
before grouping        after aggregation
```

``` text
UNION                  UNION ALL
deduplicate            keep duplicates
more work              generally cheaper
```

``` text
INDEX SEEK/RANGE        FULL SCAN
small target set        much/all table
```

``` text
REPLICATION             PARTITIONING             SHARDING
copies data             splits logical table      distributes across DB nodes
HA/read scale           manage/query large table  horizontal data/write scale
```

``` text
OPTIMISTIC              PESSIMISTIC
detect conflict         lock/block
```

``` text
OFFSET                   KEYSET
page by skipped rows     page from last key
simple random pages      scalable sequential navigation
```

------------------------------------------------------------------------

# Senior Rapid-Fire Follow-Ups

1.  INNER vs LEFT JOIN?
2.  WHERE vs HAVING?
3.  Subquery vs JOIN?
4.  EXISTS vs IN?
5.  Why is NOT IN dangerous with NULL?
6.  CTE?
7.  Window functions?
8.  ROW_NUMBER vs RANK vs DENSE_RANK?
9.  UNION vs UNION ALL?
10. Logical query order?
11. B-tree index?
12. Composite index?
13. Leftmost-prefix concept?
14. Covering index?
15. Selectivity?
16. Why not index everything?
17. Function on indexed column?
18. Leading wildcard LIKE?
19. Index for ORDER BY?
20. EXPLAIN?
21. Estimated vs actual rows?
22. Why do statistics matter?
23. Slow-query workflow?
24. ACID?
25. Isolation?
26. MVCC?
27. Does MVCC mean no locks?
28. Row vs table locks?
29. Deadlock?
30. Lost update?
31. Optimistic vs pessimistic?
32. Why long transactions hurt?
33. Hot row?
34. Why connection pooling?
35. How size Hikari?
36. What causes pool exhaustion?
37. Why can increasing pool size hurt?
38. Connection leak?
39. Which Hikari metrics?
40. Replication?
41. Primary vs replica?
42. Replication lag?
43. Read-after-write consistency?
44. Partitioning?
45. Partition pruning?
46. Sharding?
47. Good shard key?
48. Hot shard?
49. Why is resharding hard?
50. Replication vs partitioning vs sharding?

------------------------------------------------------------------------

# Interview Checklist

``` text
SQL
[ ] 01 JOINs
[ ] 02 WHERE / HAVING
[ ] 03 Subquery / JOIN
[ ] 04 EXISTS / IN
[ ] 05 CTE
[ ] 06 Window functions
[ ] 07 Top-N per group
[ ] 08 NULL
[ ] 09 UNION
[ ] 10 Logical processing

INDEXES / OPTIMIZATION
[ ] 11 B-tree
[ ] 12 Index lookup
[ ] 13 Composite index
[ ] 14 Leftmost-prefix
[ ] 15 Covering index
[ ] 16 Selectivity
[ ] 17 Clustered concepts
[ ] 18 Index costs
[ ] 19 Functions and indexes
[ ] 20 LIKE
[ ] 21 ORDER BY
[ ] 22 EXPLAIN
[ ] 23 Statistics
[ ] 24 Optimization methodology

TRANSACTIONS
[ ] 25 ACID
[ ] 26 Isolation
[ ] 27 MVCC
[ ] 28 Locks
[ ] 29 Row/table locks
[ ] 30 Deadlocks
[ ] 31 Lost update
[ ] 32 Optimistic/pessimistic
[ ] 33 Long transactions
[ ] 34 Hot rows

HIKARI
[ ] 35 Pool purpose
[ ] 36 Pool sizing
[ ] 37 Pool exhaustion
[ ] 38 Timeouts
[ ] 39 Connection leaks
[ ] 40 Metrics

SCALING
[ ] 41 Replication
[ ] 42 Primary/replica
[ ] 43 Replication lag
[ ] 44 Read-after-write
[ ] 45 Partitioning
[ ] 46 Partition pruning
[ ] 47 Sharding
[ ] 48 Shard keys
[ ] 49 Resharding
[ ] 50 Strategy selection
```

------------------------------------------------------------------------

# Chapter 08 Visual Summary

``` text
                    SQL / DATABASE
                          |
        +-----------------+-----------------+
        |                                   |
      QUERY                               SCALE
        |                                   |
        v                                   v
      SQL                              Replication
        |                                   |
        v                              Partitioning
     Planner                                |
        |                               Sharding
        v
   Statistics
        |
        v
     Indexes
        |
        v
   Execution Plan
        |
        v
     Locks/MVCC
        |
        v
      Storage
```

Application path:

``` text
Spring
  |
JPA
  |
SQL
  |
HikariCP
  |
Database
  |
Execution Plan
  |
Index / Scan
  |
Locks / I/O
  |
Response
```

------------------------------------------------------------------------

# Chapter 08 Exit Criteria

You are ready for **CH 09 --- REST API Design** when you can:

1.  Write and explain joins, grouping and window functions.
2.  Explain EXISTS/IN and NULL traps.
3.  Draw a B-tree conceptually.
4.  Design a composite index from a query.
5.  Explain selectivity and covering indexes.
6.  Explain why an index may not be used.
7.  Read an execution plan at a useful interview level.
8.  Compare estimated vs actual cardinality.
9.  Optimize a slow query systematically.
10. Explain MVCC and locks.
11. Diagnose deadlocks and lost updates.
12. Explain why long transactions hurt Hikari and DB concurrency.
13. Size a connection pool with fleet-level reasoning.
14. Diagnose Hikari exhaustion.
15. Explain replication and replication lag.
16. Design read-after-write behavior.
17. Explain partition pruning.
18. Explain sharding and shard-key selection.
19. Explain why resharding is difficult.
20. Choose between replication, partitioning and sharding.

``` text
CH 08 SQL / DATABASE
       |
       v
SQL Querying
       |
       v
Indexes ★★★
       |
       v
EXPLAIN / Optimization ★★★
       |
       v
MVCC / Locks
       |
       v
HikariCP ★★★
       |
       v
Replication
       |
       v
Partitioning / Sharding ★★★
       |
       v
READY FOR CH 09
REST API DESIGN
```

------------------------------------------------------------------------

## Next Chapter

``` text
CH 09 — REST API DESIGN (~30 Q)
|
+-- HTTP fundamentals
+-- Methods / status codes
+-- Idempotency
+-- PUT vs PATCH
+-- Resource modeling
+-- Pagination / filtering / sorting
+-- Versioning
+-- Error contracts
+-- Rate limiting
+-- OpenAPI / Swagger
+-- Security / production API design
```
