# 012_Joins_NestedLoop_HashJoin.md

# MiniDatabase — 012 Joins NestedLoop HashJoin

## 0. Why This File Exists

Real backend systems rarely read from only one table.

Production systems commonly have:

```text
users
orders
payments
products
inventory
addresses
reviews
notifications
```

To combine related data, databases use:

```sql
JOIN
```

This file explains:

```text
INNER JOIN
LEFT JOIN
Nested Loop Join
Index Nested Loop Join
Hash Join
Merge Join
Join order
Join cardinality
Why joins become expensive
How optimizer chooses join algorithm
```

Only ASCII diagrams are used.

---

# 1. Biggest Mental Model

JOIN means:

```text
match rows from two tables using a condition
```

Example:

```sql
SELECT u.name, o.amount
FROM users u
JOIN orders o
ON u.id = o.user_id;
```

Internal flow:

```text
Read users
    ↓
Read orders
    ↓
Match users.id with orders.user_id
    ↓
Combine matching rows
    ↓
Return result
```

---

# 2. Example Tables

## users

```text
+----+---------+
| id | name    |
+----+---------+
| 1  | Mohamed |
| 2  | John    |
| 3  | Alice   |
+----+---------+
```

## orders

```text
+----+---------+--------+
| id | user_id | amount |
+----+---------+--------+
| 10 | 1       | 100    |
| 11 | 1       | 200    |
| 12 | 2       | 300    |
+----+---------+--------+
```

Query:

```sql
SELECT u.name, o.amount
FROM users u
JOIN orders o
ON u.id = o.user_id;
```

Result:

```text
+---------+--------+
| name    | amount |
+---------+--------+
| Mohamed | 100    |
| Mohamed | 200    |
| John    | 300    |
+---------+--------+
```

Alice has no order, so she is not returned in INNER JOIN.

---

# 3. JOIN Mental Model

```text
Table-A row
    ↓
join condition
    ↓
matching Table-B row
    ↓
combined output row
```

Example:

```text
users.id = orders.user_id
```

---

# 4. JOIN Operator Tree

```text
Projection(name, amount)
          ↑
Join(users.id = orders.user_id)
      ┌───┴───┐
      ↑       ↑
Scan users  Scan orders
```

Rows from two tables flow into join operator.

---

# 5. INNER JOIN

INNER JOIN returns:

```text
only matching rows
```

Example:

```sql
SELECT *
FROM users u
INNER JOIN orders o
ON u.id = o.user_id;
```

---

# 6. INNER JOIN ASCII

```text
users                  orders

1 Mohamed              user_id=1 amount=100
2 John                 user_id=1 amount=200
3 Alice                user_id=2 amount=300

Matches:

Mohamed ↔ 100
Mohamed ↔ 200
John    ↔ 300
Alice   ↔ no match, not returned
```

---

# 7. LEFT JOIN

LEFT JOIN returns:

```text
all rows from left table
+
matching rows from right table
```

If no match:

```text
right side columns become NULL
```

---

# 8. LEFT JOIN ASCII

## users

```text
1 Mohamed
2 John
3 Alice
```

## orders

```text
user_id=1 amount=100
user_id=2 amount=300
```

LEFT JOIN result:

```text
Mohamed 100
John    300
Alice   NULL
```

Alice remains because LEFT JOIN preserves the left table.

---

# 9. JOIN Algorithms

Databases do not have only one way to join.

Common algorithms:

```text
Nested Loop Join
Index Nested Loop Join
Hash Join
Merge Join
```

Optimizer chooses one based on:

```text
table size
indexes
statistics
memory
row counts
sort order
```

---

# 10. Nested Loop Join

Simplest join algorithm.

Mental model:

```text
for every row in left table:
    scan every row in right table:
        if keys match:
            output joined row
```

---

# 11. Nested Loop Join ASCII

```text
users:
1 Mohamed
2 John

orders:
10 user=1 amount=100
11 user=1 amount=200
12 user=2 amount=300
```

Execution:

```text
Take Mohamed(id=1)
    compare order 10 user=1 → match
    compare order 11 user=1 → match
    compare order 12 user=2 → no

Take John(id=2)
    compare order 10 user=1 → no
    compare order 11 user=1 → no
    compare order 12 user=2 → match
```

Output:

```text
Mohamed 100
Mohamed 200
John    300
```

---

# 12. Nested Loop Join Complexity

If:

```text
left table = N rows
right table = M rows
```

Cost:

```text
O(N × M)
```

Example:

```text
1 million users × 10 million orders
```

This is impossible without optimization.

---

# 13. Nested Loop Join Flow

```text
Read one left row
      ↓
Read all right rows
      ↓
Compare join condition
      ↓
Emit matching rows
      ↓
Repeat for next left row
```

---

# 14. When Nested Loop Join Is Good

Nested loop can be good when:

```text
left table is very small
right table is small
or right table has index on join key
```

Example:

```text
10 selected users
join with indexed orders table
```

---

# 15. Index Nested Loop Join

Index Nested Loop Join improves nested loop using index.

Instead of scanning right table:

```text
lookup matching rows using index
```

---

# 16. Index Nested Loop ASCII

For each user:

```text
User id = 1
    ↓
orders_user_id_index.lookup(1)
    ↓
returns matching orders quickly
```

Flow:

```text
left row
    ↓
index lookup on right table
    ↓
matching right rows
    ↓
emit joined rows
```

---

# 17. Index Nested Loop Cost

If left has N rows:

```text
N index lookups
```

Cost roughly:

```text
O(N × log M)
```

for BTree index.

Much better than:

```text
O(N × M)
```

---

# 18. Hash Join

Hash Join is one of the most important join algorithms.

Mental model:

```text
build hash table from one table
probe it using rows from other table
```

Usually:

```text
build hash table from smaller table
probe using larger table
```

---

# 19. Hash Join Has Two Phases

```text
1. Build Phase
2. Probe Phase
```

---

# 20. Build Phase

Choose smaller table.

Example:

```text
users
```

Build hash table:

```text
user id → user row
```

---

# 21. Build Phase ASCII

Input users:

```text
1 Mohamed
2 John
3 Alice
```

Hash table:

```text
+---------+-----------+
| key     | value     |
+---------+-----------+
| 1       | Mohamed   |
| 2       | John      |
| 3       | Alice     |
+---------+-----------+
```

---

# 22. Probe Phase

Scan orders table.

For each order:

```text
lookup order.user_id in hash table
```

---

# 23. Probe Phase ASCII

Order row:

```text
order_id=10, user_id=1, amount=100
```

Probe:

```text
hashTable.get(1)
        ↓
Mohamed
        ↓
emit Mohamed 100
```

---

# 24. Full Hash Join Flow

```text
Build hash table from users
        ↓
Scan orders
        ↓
For each order, lookup user_id
        ↓
If found, combine rows
        ↓
Emit result
```

---

# 25. Hash Join Operator Tree

```text
Hash Join
    ┌──────┴──────┐
    ↑             ↑
Build side     Probe side
users          orders
```

---

# 26. Hash Join Dry Run

## users

```text
1 Mohamed
2 John
```

## orders

```text
user=1 amount=100
user=1 amount=200
user=2 amount=300
```

Build:

```text
1 → Mohamed
2 → John
```

Probe:

```text
order user=1 → lookup 1 → Mohamed → output Mohamed 100
order user=1 → lookup 1 → Mohamed → output Mohamed 200
order user=2 → lookup 2 → John    → output John 300
```

Final output:

```text
Mohamed 100
Mohamed 200
John    300
```

---

# 27. Hash Join Complexity

Average cost:

```text
O(N + M)
```

Because:

```text
build once
probe once
```

Much better than nested loop for large unsorted tables.

---

# 28. Why Hash Join Is Fast

Hash lookup is usually:

```text
O(1)
```

So each probe is fast.

Hash join avoids:

```text
repeated full scans
```

---

# 29. Hash Join Weakness

Hash join needs memory.

If hash table too large:

```text
spill to disk
```

Then performance drops.

---

# 30. Hash Join Spill ASCII

```text
Build hash table
      ↓
Memory full
      ↓
Partition hash table to disk
      ↓
Read partitions later
      ↓
Continue join
```

Disk spill makes joins slow.

---

# 31. Merge Join

Merge Join works when both inputs are sorted by join key.

Mental model:

```text
merge two sorted streams
```

Like merge step of merge sort.

---

# 32. Merge Join ASCII

## users sorted by id

```text
1 Mohamed
2 John
3 Alice
```

## orders sorted by user_id

```text
1 amount=100
1 amount=200
3 amount=500
```

Execution:

```text
users pointer at 1
orders pointer at 1
match

users pointer at 2
orders pointer still 3
2 < 3, advance users

users pointer at 3
orders pointer at 3
match
```

---

# 33. Merge Join Flow

```text
left pointer
right pointer
compare join keys
    ↓
if equal: emit match
if left smaller: advance left
if right smaller: advance right
```

---

# 34. Merge Join Cost

If already sorted:

```text
O(N + M)
```

If not sorted:

```text
sort cost + merge cost
```

---

# 35. When Merge Join Is Good

Good when:

```text
both inputs already sorted
indexes provide order
large datasets
range-like joins
```

---

# 36. Join Cardinality

Cardinality means:

```text
number of rows produced by join
```

Example:

```text
1 user can have 1000 orders
```

Join result may be much larger than users table.

---

# 37. Cardinality Explosion

Bad join condition can create huge output.

Example:

```text
1 million rows × 1 million rows
```

Possible output:

```text
trillions of rows
```

This is called:

```text
cardinality explosion
```

---

# 38. Join Order Matters

Query:

```sql
A JOIN B JOIN C
```

Database can execute:

```text
(A JOIN B) JOIN C
```

or:

```text
A JOIN (B JOIN C)
```

Costs can be massively different.

---

# 39. Join Order ASCII

Option 1:

```text
A JOIN B
    ↓
large intermediate
    ↓
JOIN C
```

Option 2:

```text
B JOIN C
    ↓
small intermediate
    ↓
JOIN A
```

Optimizer tries to choose the smaller intermediate path.

---

# 40. Why Joins Become Expensive

Joins may require:

```text
large scans
hash tables
sorting
memory
disk spill
network shuffle
large intermediate results
```

---

# 41. Distributed Joins

In distributed databases, rows may live on different nodes.

Join may require:

```text
network shuffle
```

Very expensive.

---

# 42. Distributed Join ASCII

```text
Node-1: users partition
Node-2: orders partition
Node-3: payments partition
        ↓
shuffle rows by join key
        ↓
join matching rows
```

---

# 43. Broadcast Join

If one table is small:

```text
copy small table to all nodes
```

Then each node can join locally.

---

# 44. Broadcast Join ASCII

```text
Small users table
      ↓
copy to Node-1
copy to Node-2
copy to Node-3
      ↓
each node joins with local orders
```

Good when:

```text
one table is small
```

---

# 45. Predicate Pushdown Before Join

Bad:

```text
join huge tables
then filter
```

Better:

```text
filter first
then join smaller datasets
```

---

# 46. Predicate Pushdown ASCII

Bad:

```text
users JOIN orders
      ↓
WHERE users.country = 'RO'
```

Better:

```text
Filter users.country = 'RO'
      ↓
JOIN filtered users with orders
```

This reduces join work.

---

# 47. Projection Before Join

Avoid carrying unnecessary columns.

Bad:

```text
join full wide rows
```

Better:

```text
join only needed columns
```

Reduces:

```text
memory
network
CPU
hash table size
```

---

# 48. Backend API Example

Spring Boot endpoint:

```text
GET /user-orders
```

SQL:

```sql
SELECT u.name, o.amount
FROM users u
JOIN orders o
ON u.id = o.user_id;
```

Execution:

```text
Repository sends SQL
        ↓
Optimizer chooses join plan
        ↓
Join operator combines rows
        ↓
Projection keeps name, amount
        ↓
DTO returned
```

---

# 49. Java Nested Loop Join

```java
for (User user : users) {

    for (Order order : orders) {

        if (user.id == order.userId) {

            System.out.println(
                    user.name + " " + order.amount
            );
        }
    }
}
```

This is simple but expensive.

Cost:

```text
O(N × M)
```

---

# 50. Java Hash Join

```java
Map<Integer, User> userMap =
        new HashMap<>();

for (User user : users) {

    userMap.put(user.id, user);
}

for (Order order : orders) {

    User user =
            userMap.get(order.userId);

    if (user != null) {

        System.out.println(
                user.name + " " + order.amount
        );
    }
}
```

Cost:

```text
O(N + M)
```

---

# 51. Java Hash Join Dry Run

Users:

```text
1 Mohamed
2 John
```

Build map:

```text
1 → Mohamed
2 → John
```

Orders:

```text
user=1 amount=100
user=2 amount=300
```

Probe:

```text
order.userId=1 → Mohamed → output Mohamed 100
order.userId=2 → John    → output John 300
```

---

# 52. Join Optimization Rules

## Rule 1

```text
Index join keys
```

Example:

```text
orders.user_id
users.id
```

## Rule 2

```text
Filter before join
```

## Rule 3

```text
Avoid SELECT *
```

## Rule 4

```text
Join smaller intermediate results first
```

---

# 53. Common Slow Join Patterns

## Pattern 1

```sql
JOIN without index
```

Problem:

```text
full scans
```

## Pattern 2

```sql
JOIN huge tables without filter
```

Problem:

```text
huge intermediate result
```

## Pattern 3

```sql
SELECT *
FROM many joined tables
```

Problem:

```text
wide rows
memory pressure
network cost
```

---

# 54. Join Algorithm Comparison

| Join Type | Best For | Weakness |
|---|---|---|
| Nested Loop | Small inputs | O(N × M) |
| Index Nested Loop | Small left + indexed right | Many index lookups |
| Hash Join | Large equality joins | Needs memory |
| Merge Join | Sorted inputs | Sorting cost if unsorted |

---

# 55. Interview Explanation

If interviewer asks:

```text
How do joins work internally?
```

Strong answer:

```text
Databases use join algorithms such as nested loop join,
hash join, and merge join. Nested loop compares rows repeatedly,
hash join builds a hash table from one side and probes with the other,
and merge join walks two sorted inputs together.
```

Senior addition:

```text
The optimizer chooses join order and join algorithm using statistics,
cardinality estimates, indexes, memory cost, and expected intermediate size.
```

---

# 56. Common Mistakes

## Mistake 1

```text
Thinking joins are cheap
```

Joins are often expensive.

---

## Mistake 2

```text
Ignoring indexes on join keys
```

Bad for performance.

---

## Mistake 3

```text
Filtering after join
```

Often should filter before join.

---

## Mistake 4

```text
Using SELECT * with joins
```

Creates wide result rows.

---

## Mistake 5

```text
Ignoring join order
```

Join order can massively change query cost.

---

# 57. Final Mental Model

```text
JOIN
    =
combine matching rows

Nested Loop
    =
compare repeatedly

Index Nested Loop
    =
loop + index lookup

Hash Join
    =
build hash table + probe

Merge Join
    =
merge sorted streams
```

---

# 58. What To Remember

```text
INNER JOIN returns matching rows.

LEFT JOIN preserves all left rows.

Nested Loop Join is simple but can be expensive.

Hash Join is powerful for equality joins.

Merge Join works well on sorted inputs.

Join order matters.

Indexes on join keys are critical.

Filter before joining whenever possible.
```

---

# 59. Next File

```text
013_BufferPool_PageCache.md
```

Next you learn:

```text
buffer pool
page cache
LRU
cache hit vs miss
dirty pages
why RAM matters in databases
```
