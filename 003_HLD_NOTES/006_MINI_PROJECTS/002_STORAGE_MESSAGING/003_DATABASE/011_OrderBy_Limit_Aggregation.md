# 011_OrderBy_Limit_Aggregation.md

# MiniDatabase — 011 OrderBy Limit Aggregation

## 0. Why This File Exists

After learning:

```text
Scan
Filter
Projection
```

the next important database execution operators are:

```text
ORDER BY
LIMIT
COUNT
SUM
GROUP BY
Aggregation
```

These operators power analytics, dashboards, APIs, reports, leaderboards, rankings, pagination, and statistics.

This file teaches:

```text
how sorting works internally
how LIMIT optimization works
how aggregation works
how GROUP BY works
how top-K queries work
how databases implement COUNT/SUM
```

Only ASCII diagrams are used.

---

# 1. Biggest Mental Model

Query:

```sql
SELECT city, COUNT(*)
FROM users
GROUP BY city
ORDER BY COUNT(*) DESC
LIMIT 3;
```

Execution:

```text
Scan rows
    ↓
Group rows by city
    ↓
Count rows
    ↓
Sort by count descending
    ↓
Keep top 3 rows
    ↓
Return result
```

---

# 2. Full Operator Pipeline

```text
+----------------------+
| LIMIT 3              |
+----------------------+
           ↑
+----------------------+
| ORDER BY count DESC  |
+----------------------+
           ↑
+----------------------+
| GROUP BY city        |
| COUNT(*)             |
+----------------------+
           ↑
+----------------------+
| Scan users           |
+----------------------+
```

Rows move upward.

---

# 3. ORDER BY

`ORDER BY` sorts rows.

Example:

```sql
SELECT * FROM users
ORDER BY age;
```

Goal:

```text
return rows in sorted order
```

---

# 4. ORDER BY ASCII

Before sorting:

```text
30
20
35
25
```

After sorting:

```text
20
25
30
35
```

---

# 5. Sort Operator

Database uses a:

```text
Sort Operator
```

Execution:

```text
read rows
    ↓
store rows
    ↓
sort rows
    ↓
return sorted output
```

---

# 6. In-Memory Sort

If rows fit in RAM:

```text
load rows into memory
use sorting algorithm
return sorted rows
```

Complexity:

```text
O(N log N)
```

---

# 7. External Sort

If data too large for RAM:

```text
split into chunks
sort each chunk
write temp files
merge sorted files
```

---

# 8. External Sort ASCII

```text
Large Input
    ↓
Chunk-1 → sort → temp-file-1
Chunk-2 → sort → temp-file-2
Chunk-3 → sort → temp-file-3
    ↓
Merge temp files
    ↓
Final sorted output
```

---

# 9. Why ORDER BY Can Be Expensive

Sorting large datasets requires:

```text
memory
disk temporary files
many comparisons
CPU work
```

Especially expensive for:

```sql
ORDER BY huge_column
```

on millions of rows.

---

# 10. ORDER BY Using Index

Sometimes sorting can be avoided.

Example:

```sql
SELECT * FROM users
ORDER BY id;
```

If BTree index exists on:

```text
id
```

Database can scan index in sorted order.

No explicit sort required.

---

# 11. Index Ordered Scan ASCII

```text
BTree Leaf Pages
│
├── 1
├── 2
├── 3
├── 4
└── 5
```

Already sorted.

Database walks leaves sequentially.

---

# 12. LIMIT

LIMIT restricts number of rows returned.

Example:

```sql
SELECT * FROM users LIMIT 10;
```

Execution:

```text
return first 10 rows
stop execution
```

---

# 13. LIMIT ASCII

```text
Rows:
1
2
3
4
5
...
1000000

LIMIT 3
    ↓
1
2
3
STOP
```

---

# 14. Why LIMIT Is Important

LIMIT reduces:

```text
CPU
memory
network transfer
response time
```

Very important for APIs and pagination.

---

# 15. LIMIT Optimization

Without LIMIT:

```text
scan entire dataset
```

With LIMIT:

```text
stop early
```

Huge optimization.

---

# 16. ORDER BY + LIMIT

Very common query:

```sql
SELECT *
FROM orders
ORDER BY created_at DESC
LIMIT 10;
```

Meaning:

```text
latest 10 orders
```

---

# 17. Top-K Query

ORDER BY + LIMIT is called:

```text
Top-K Query
```

Example:

```sql
ORDER BY score DESC LIMIT 10
```

Meaning:

```text
top 10 scores
```

---

# 18. Top-K Optimization

Instead of sorting everything:

```text
database may keep only best K rows
```

Using:

```text
heap / priority queue
```

---

# 19. Top-K ASCII

Goal:

```text
Top 3 largest numbers
```

Input:

```text
5 1 8 3 10 7
```

Keep min-heap size 3:

```text
5
5 1
8 5 1
8 5 3
10 8 5
10 8 7
```

Final:

```text
10 8 7
```

No full sort needed.

---

# 20. Aggregation

Aggregation combines many rows into summary values.

Examples:

```sql
COUNT(*)
SUM(price)
AVG(score)
MIN(age)
MAX(age)
```

---

# 21. COUNT Example

```sql
SELECT COUNT(*)
FROM users;
```

Execution:

```text
scan rows
increment counter
return final count
```

---

# 22. COUNT ASCII

```text
counter = 0

Row-1 → counter = 1
Row-2 → counter = 2
Row-3 → counter = 3
```

Final result:

```text
COUNT = 3
```

---

# 23. SUM Example

```sql
SELECT SUM(price)
FROM orders;
```

Execution:

```text
sum = 0
sum += each value
```

---

# 24. SUM ASCII

```text
prices:
100
200
300

sum = 0
sum = 100
sum = 300
sum = 600
```

Final:

```text
600
```

---

# 25. AVG Example

```sql
SELECT AVG(score)
FROM exams;
```

Internally:

```text
SUM(score) / COUNT(score)
```

Database maintains:

```text
running sum
running count
```

---

# 26. GROUP BY

GROUP BY divides rows into groups.

Example:

```sql
SELECT city, COUNT(*)
FROM users
GROUP BY city;
```

Goal:

```text
count users per city
```

---

# 27. GROUP BY ASCII

Rows:

```text
Bucharest
London
Bucharest
Paris
London
```

Groups:

```text
Bucharest → 2
London → 2
Paris → 1
```

---

# 28. Hash Aggregate

Most databases use:

```text
Hash Aggregate
```

Mental model:

```text
HashMap<group_key, aggregate_state>
```

Example:

```text
HashMap<city, count>
```

---

# 29. Hash Aggregate ASCII

```text
Rows arrive:
Bucharest
London
Bucharest
Paris

HashMap:

Bucharest → 2
London → 1
Paris → 1
```

---

# 30. Hash Aggregate Flow

```text
read row
    ↓
compute group key
    ↓
find hash entry
    ↓
update aggregate value
```

---

# 31. Sort Aggregate

Alternative approach:

```text
sort rows by group key
aggregate sequentially
```

Useful when:

```text
data already sorted
```

---

# 32. Sort Aggregate ASCII

Before sort:

```text
London
Paris
London
Bucharest
```

After sort:

```text
Bucharest
London
London
Paris
```

Now aggregation becomes easy.

---

# 33. Hash Aggregate vs Sort Aggregate

| Feature | Hash Aggregate | Sort Aggregate |
|---|---|---|
| Needs sorting | No | Yes |
| Memory usage | Higher | Moderate |
| Good for unsorted data | Yes | No |
| Good for sorted data | Sometimes | Excellent |

---

# 34. HAVING Clause

HAVING filters groups after aggregation.

Example:

```sql
SELECT city, COUNT(*)
FROM users
GROUP BY city
HAVING COUNT(*) > 10;
```

Execution:

```text
group rows
compute counts
filter groups
```

---

# 35. HAVING ASCII

```text
Groups:

Bucharest → 20
London → 5
Paris → 12

HAVING count > 10
        ↓
Bucharest
Paris
```

---

# 36. Full Aggregation Pipeline

```text
Scan users
    ↓
Hash Aggregate by city
    ↓
Sort by count DESC
    ↓
LIMIT 3
    ↓
Return top cities
```

---

# 37. Full Pipeline ASCII

```text
+----------------------+
| LIMIT 3              |
+----------------------+
           ↑
+----------------------+
| ORDER BY count DESC  |
+----------------------+
           ↑
+----------------------+
| Hash Aggregate       |
| city → COUNT(*)      |
+----------------------+
           ↑
+----------------------+
| Scan users           |
+----------------------+
```

---

# 38. Blocking Operators

Some operators must consume all rows first.

Examples:

```text
SORT
HASH AGGREGATE
```

These are:

```text
blocking operators
```

because output comes later.

---

# 39. Blocking Operator ASCII

```text
Scan rows
    ↓
Store rows
    ↓
Sort / Aggregate
    ↓
Return output
```

---

# 40. Streaming Operators

Some operators stream rows immediately.

Examples:

```text
Scan
Filter
Projection
```

These operators do not need all rows first.

---

# 41. Streaming vs Blocking

| Operator | Streaming | Blocking |
|---|---|---|
| Scan | Yes | No |
| Filter | Yes | No |
| Projection | Yes | No |
| Sort | No | Yes |
| Hash Aggregate | No | Yes |

---

# 42. Backend API Example

Spring Boot endpoint:

```text
GET /top-cities
```

SQL:

```sql
SELECT city, COUNT(*)
FROM users
GROUP BY city
ORDER BY COUNT(*) DESC
LIMIT 10;
```

Execution:

```text
Scan users
    ↓
Aggregate counts
    ↓
Sort groups
    ↓
Keep top 10
    ↓
Return JSON
```

---

# 43. Java COUNT Example

```java
int count = 0;

for (Row row : rows) {
    count++;
}

System.out.println(count);
```

---

# 44. Java SUM Example

```java
int sum = 0;

for (Row row : rows) {

    sum += (int) row.get("price");
}

System.out.println(sum);
```

---

# 45. Java GROUP BY COUNT Example

```java
Map<String, Integer> counts =
        new HashMap<>();

for (Row row : rows) {

    String city =
            (String) row.get("city");

    counts.put(
            city,
            counts.getOrDefault(city, 0) + 1
    );
}

System.out.println(counts);
```

---

# 46. Java Top-K Example

```java
PriorityQueue<Integer> heap =
        new PriorityQueue<>();

int k = 3;

for (int value : values) {

    heap.offer(value);

    if (heap.size() > k) {
        heap.poll();
    }
}

System.out.println(heap);
```

Keeps only best K values.

---

# 47. Full Dry Run

Query:

```sql
SELECT city, COUNT(*)
FROM users
GROUP BY city
ORDER BY COUNT(*) DESC
LIMIT 2;
```

Rows:

```text
Bucharest
London
Bucharest
Paris
London
Bucharest
```

Execution:

```text
Scan rows
    ↓
Hash aggregate

Bucharest → 3
London → 2
Paris → 1

Sort descending

Bucharest → 3
London → 2
Paris → 1

LIMIT 2

Bucharest → 3
London → 2
```

Final result:

```text
Bucharest → 3
London → 2
```

---

# 48. Production Performance Notes

## ORDER BY expensive because:

```text
sorting
memory
disk temporary files
CPU comparisons
```

## GROUP BY expensive because:

```text
large hash tables
many distinct groups
memory pressure
```

## LIMIT useful because:

```text
reduces work early
```

---

# 49. Common Slow Query Patterns

```sql
ORDER BY huge_column
```

Problem:

```text
large sort
```

```sql
GROUP BY high_cardinality_column
```

Problem:

```text
huge hash table
```

```sql
SELECT * ORDER BY created_at
```

without index:

```text
full sort required
```

---

# 50. Interview Explanation

If interviewer asks:

```text
How do ORDER BY and GROUP BY work internally?
```

Strong answer:

```text
ORDER BY uses sort operators or index ordering to produce sorted rows.
GROUP BY uses aggregation operators such as hash aggregate or sort aggregate
to combine rows into grouped summary values.
```

Senior addition:

```text
ORDER BY + LIMIT queries are optimized using top-K algorithms and heaps.
```

---

# 51. Common Mistakes

## Mistake 1

```text
Thinking ORDER BY is free
```

Sorting large datasets is expensive.

---

## Mistake 2

```text
Thinking LIMIT always fast
```

Without index, database may still scan/sort huge data.

---

## Mistake 3

```text
Ignoring cardinality in GROUP BY
```

Too many distinct groups can explode memory usage.

---

# 52. Final Mental Model

```text
ORDER BY
    =
sorting rows

LIMIT
    =
stop after K rows

GROUP BY
    =
group rows

COUNT/SUM
    =
aggregate values
```

Execution:

```text
Scan
   ↓
Aggregate
   ↓
Sort
   ↓
Limit
   ↓
Result
```

---

# 53. What To Remember

```text
ORDER BY may require sorting.

LIMIT reduces work.

GROUP BY commonly uses hash aggregation.

ORDER BY + LIMIT is top-K query.

Sorting huge data may spill to disk.

COUNT/SUM maintain running state.
```

---

# 54. Next File

```text
012_Joins_NestedLoop_HashJoin.md
```

Next you learn:

```text
INNER JOIN
LEFT JOIN
Nested Loop Join
Hash Join
Merge Join
join execution plans
why joins become expensive
```
