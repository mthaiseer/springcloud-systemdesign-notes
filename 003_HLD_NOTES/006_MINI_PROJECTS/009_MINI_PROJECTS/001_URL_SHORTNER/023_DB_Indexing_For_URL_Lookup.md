# 023_DB_Indexing_For_URL_Lookup.md
# MiniURLShortener — DB Indexing For URL Lookup

> Core mental model: **A database index is a lookup map that lets PostgreSQL jump directly to the row instead of scanning the whole table. For a URL shortener, `short_code` is the most important read key, so the redirect lookup must be protected by a unique B-Tree index.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Without Index vs With Index](#4-without-index-vs-with-index)
- [5. URL Lookup Query](#5-url-lookup-query)
- [6. B-Tree Index Mental Model](#6-b-tree-index-mental-model)
- [7. Why `short_code` Needs Unique Index](#7-why-short_code-needs-unique-index)
- [8. Index As Performance + Correctness](#8-index-as-performance--correctness)
- [9. Table Design Reminder](#9-table-design-reminder)
- [10. Main Index DDL](#10-main-index-ddl)
- [11. Lookup Execution Flow](#11-lookup-execution-flow)
- [12. EXPLAIN Mental Model](#12-explain-mental-model)
- [13. Sequential Scan vs Index Scan](#13-sequential-scan-vs-index-scan)
- [14. Covering Index And INCLUDE](#14-covering-index-and-include)
- [15. Partial Index For Active URLs](#15-partial-index-for-active-urls)
- [16. Index Selectivity](#16-index-selectivity)
- [17. Indexes And Redis Cache](#17-indexes-and-redis-cache)
- [18. Index Write Cost](#18-index-write-cost)
- [19. Index Bloat And Maintenance](#19-index-bloat-and-maintenance)
- [20. Common Query Patterns](#20-common-query-patterns)
- [21. Java/Spring Data Repository](#21-javaspring-data-repository)
- [22. Step-by-Step Dry Runs](#22-step-by-step-dry-runs)
- [23. Internal Execution Walkthrough](#23-internal-execution-walkthrough)
- [24. Testing Strategy](#24-testing-strategy)
- [25. Observability And Slow Query Debugging](#25-observability-and-slow-query-debugging)
- [26. Production Failure Stories](#26-production-failure-stories)
- [27. Debugging Mindset](#27-debugging-mindset)
- [28. Common Mistakes](#28-common-mistakes)
- [29. Interview-Ready Explanation](#29-interview-ready-explanation)
- [30. Senior Engineer Checklist](#30-senior-engineer-checklist)
- [31. One-Page Cheat Sheet](#31-one-page-cheat-sheet)
- [32. One Picture To Remember](#32-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener redirect API receives:

```http
GET /abc123
```

The service needs to find:

```text
short_code = abc123
```

Then return:

```http
302 Location: https://example.com/article
```

The core database query is:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = 'abc123';
```

If `short_urls` has only 100 rows, this is easy.

If it has 100 million rows, this can become dangerous without an index.

Without index, PostgreSQL may need to scan many rows:

```text
row 1
row 2
row 3
...
row 99,999,999
```

With index, PostgreSQL can jump near the target:

```text
short_code index -> pointer to matching row
```

ASCII:

```text
WITHOUT INDEX

short_urls table
+--------+-----------+-----------------------+
| row id | shortCode | longUrl               |
+--------+-----------+-----------------------+
| 1      | aa11      | ...                   |
| 2      | bb22      | ...                   |
| 3      | cc33      | ...                   |
| ...    | ...       | ...                   |
| N      | abc123    | https://example.com   |
+--------+-----------+-----------------------+

Must search many rows.


WITH INDEX

short_code index
+-----------+----------------+
| shortCode | row pointer    |
+-----------+----------------+
| abc123    | row location   |
+-----------+----------------+

Jump directly.
```

Production memory:

```text
Redis reduces how often DB is queried.
Index makes every DB fallback query fast.
```

Both are needed.

---

## 2. The One Core Mental Model

The core mental model:

```text
INDEX = SORTED LOOKUP MAP
```

An index is like a book index.

If you want to find the topic "transactions" in a book, you do not read every page.

You go to the index:

```text
transactions -> pages 43, 88, 120
```

For URL lookup:

```text
abc123 -> row location
```

ASCII:

```text
Book:

Without index:
    read page 1
    read page 2
    read page 3
    ...
    find topic

With index:
    topic -> page number -> jump


Database:

Without index:
    scan rows

With index:
    short_code -> row pointer -> jump
```

One-line memory:

```text
An index trades extra write/storage cost for much faster reads.
```

For MiniURLShortener:

```text
short_code is the lookup key.
Therefore short_code needs an index.
Because short_code must be unique, it needs a unique index.
```

---

## 3. Problem Statement

Design the database indexing strategy for MiniURLShortener URL lookup.

The design must support:

```text
1. Fast redirect lookup by short_code.
2. Uniqueness guarantee for short_code.
3. Efficient fallback when Redis misses.
4. Correct duplicate alias detection.
5. Predictable p99 latency under high read load.
6. Safe query shape for JPA/Spring Data.
7. Debuggability using EXPLAIN.
8. Awareness of write and storage tradeoffs.
9. Future support for active/deleted/blocked filtering.
10. Production monitoring for slow queries and index usage.
```

Core query:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?;
```

Main index:

```sql
CREATE UNIQUE INDEX uk_short_urls_short_code
ON short_urls(short_code);
```

Non-goals:

```text
full PostgreSQL internals
all index types
sharding
partitioning
full query optimizer course
```

This chapter teaches the exact indexing knowledge needed for URL lookup.

---

## 4. Without Index vs With Index

Assume table has:

```text
100,000,000 rows
```

Without index:

```text
PostgreSQL may scan table until it finds matching short_code.
```

This is called:

```text
Sequential Scan
```

ASCII:

```text
Sequential scan:

[ row1 ] -> not match
[ row2 ] -> not match
[ row3 ] -> not match
...
[ row99999999 ] -> not match
[ row100000000 ] -> match
```

With index:

```text
PostgreSQL searches index tree.
Then jumps to row.
```

This is called:

```text
Index Scan
```

ASCII:

```text
B-Tree index:

                [ mmmm ]
              /          \
        [ ffff ]          [ tttt ]
        /    \            /    \
   [abc123] [gggg]   [sale]   [zzzz]
      |
      v
 row pointer
```

Performance difference:

```text
Sequential scan:
    work grows with table size

Index lookup:
    work grows slowly as table size grows
```

Simple intuition:

```text
No index = search every shelf in a library.
Index = use catalog number.
```

---

## 5. URL Lookup Query

Redirect lookup must be simple.

Best query:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?;
```

Why only these columns?

Because redirect path only needs:

```text
longUrl
status
expiresAt
```

Avoid:

```sql
SELECT *
FROM short_urls
WHERE short_code = ?;
```

Why?

```text
loads unnecessary columns
more network bytes
more object mapping
more memory
less clear intent
```

If table later has many columns:

```text
description
owner_id
metadata
analytics flags
created_by
updated_by
large JSON
```

`SELECT *` becomes wasteful.

ASCII:

```text
Redirect path needs:

+------------+----------+------------+
| long_url   | status   | expires_at |
+------------+----------+------------+

It does not need:

+---------+-------------+--------------+----------+
| owner   | audit fields| metadata json | comments |
+---------+-------------+--------------+----------+
```

Repository method should reflect this.

Basic JPA entity lookup is acceptable early.

Later optimization can use projection.

---

## 6. B-Tree Index Mental Model

PostgreSQL default index type is usually B-Tree.

B-Tree keeps keys sorted.

For short codes:

```text
abc123
bcd222
mohamed
sale2026
xyz999
```

B-Tree structure:

```text
                    [mohamed]
                  /           \
          [bcd222]             [sale2026]
          /      \             /       \
     [abc123]  [ccccc]    [qqqqq]    [xyz999]
```

Lookup `abc123`:

```text
1. Start at root.
2. Compare abc123 with mohamed.
3. Go left.
4. Compare with bcd222.
5. Go left.
6. Find abc123.
7. Follow row pointer.
```

ASCII:

```text
search abc123

            mohamed
              |
        abc123 < mohamed
              v
            bcd222
              |
        abc123 < bcd222
              v
            abc123
              |
              v
          row location
```

Why B-Tree is good here:

```text
equality lookup
range lookup
sorted keys
unique constraint support
```

For URL shortener:

```text
WHERE short_code = ?
```

is a perfect B-Tree use case.

---

## 7. Why `short_code` Needs Unique Index

The short code is the public identifier.

Two rows must never share the same short code.

Bad state:

```text
short_code = abc123 -> https://first.com
short_code = abc123 -> https://second.com
```

Then redirect becomes ambiguous.

ASCII:

```text
GET /abc123
   |
   v
Which one?

+----------+---------------------+
| abc123   | https://first.com   |
| abc123   | https://second.com  |
+----------+---------------------+
```

Unique index prevents this.

DDL:

```sql
CREATE UNIQUE INDEX uk_short_urls_short_code
ON short_urls(short_code);
```

Or table constraint:

```sql
ALTER TABLE short_urls
ADD CONSTRAINT uk_short_urls_short_code UNIQUE (short_code);
```

Both create a unique index internally.

Benefits:

```text
1. Fast lookup.
2. Duplicate prevention.
3. Safe custom alias handling.
4. Safe generated code collision handling.
5. Strong concurrency correctness.
```

The unique index is both:

```text
performance tool
correctness guard
```

---

## 8. Index As Performance + Correctness

Many engineers think index only improves speed.

But unique index also enforces business rule.

Business rule:

```text
shortCode must be globally unique.
```

Application check alone is not enough.

Race condition:

```text
Pod A checks abc123 available.
Pod B checks abc123 available.
Pod A inserts abc123.
Pod B inserts abc123.
```

Without unique index:

```text
both succeed
data corruption
```

With unique index:

```text
one succeeds
one fails
```

ASCII:

```text
Pod A              Pod B
 |                  |
 check free         check free
 |                  |
 insert abc123      insert abc123
 |                  |
 success            UNIQUE VIOLATION
```

The database is the final authority.

Rule:

```text
Application validation improves user experience.
Database constraint protects truth.
```

For MiniURLShortener, unique index is mandatory.

---

## 9. Table Design Reminder

Example table:

```sql
CREATE TABLE short_urls (
    id BIGSERIAL PRIMARY KEY,
    short_code VARCHAR(32) NOT NULL,
    long_url TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    expires_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NULL
);
```

Important columns:

```text
id:
    internal primary key

short_code:
    public lookup key

long_url:
    redirect target

status:
    ACTIVE / BLOCKED / DELETED

expires_at:
    optional expiry time
```

Why not use `short_code` as primary key?

Possible, but using surrogate `id` gives flexibility.

Surrogate id benefits:

```text
stable internal FK
smaller joins if bigint
future click tables can reference id
short_code can change only if product allows
clean internal/external separation
```

Public lookup still uses:

```text
short_code unique index
```

ASCII:

```text
Internal world:
    id

External world:
    short_code
```

---

## 10. Main Index DDL

Primary required index:

```sql
CREATE UNIQUE INDEX uk_short_urls_short_code
ON short_urls(short_code);
```

If using Flyway:

```text
src/main/resources/db/migration/V2__add_short_code_index.sql
```

Migration:

```sql
CREATE UNIQUE INDEX IF NOT EXISTS uk_short_urls_short_code
ON short_urls(short_code);
```

If table constraint preferred:

```sql
ALTER TABLE short_urls
ADD CONSTRAINT uk_short_urls_short_code UNIQUE (short_code);
```

Naming convention:

```text
uk_ = unique key
idx_ = non-unique index
```

Examples:

```text
uk_short_urls_short_code
idx_short_urls_created_at
idx_short_urls_status
```

For PostgreSQL, index name matters in debugging.

Error message may include:

```text
duplicate key value violates unique constraint "uk_short_urls_short_code"
```

Your service can map this to:

```text
ALIAS_ALREADY_EXISTS
```

---

## 11. Lookup Execution Flow

Redirect lookup with index:

```text
1. Query arrives.
2. PostgreSQL planner sees WHERE short_code = ?.
3. Planner knows index exists.
4. Planner chooses index scan.
5. Index finds matching key.
6. PostgreSQL fetches row.
7. Returns long_url, status, expires_at.
```

ASCII:

```text
SQL:
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = 'abc123';

Execution:

Query Planner
     |
     v
Use uk_short_urls_short_code
     |
     v
Find index entry abc123
     |
     v
Fetch table row
     |
     v
Return columns
```

If no index:

```text
Query Planner
     |
     v
Sequential scan
     |
     v
Check every row's short_code
```

Index makes DB fallback fast when Redis misses.

---

## 12. EXPLAIN Mental Model

`EXPLAIN` shows the query plan.

Use:

```sql
EXPLAIN
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = 'abc123';
```

Better:

```sql
EXPLAIN ANALYZE
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = 'abc123';
```

Difference:

```text
EXPLAIN:
    estimated plan

EXPLAIN ANALYZE:
    actually runs query and shows real timing
```

Expected good sign:

```text
Index Scan using uk_short_urls_short_code
```

Bad sign for large table:

```text
Seq Scan on short_urls
```

ASCII:

```text
Good plan:
    Index Scan
        |
        v
    fast lookup


Bad plan:
    Seq Scan
        |
        v
    scan table
```

Important:

```text
EXPLAIN ANALYZE runs the query.
Be careful with UPDATE/DELETE in production.
```

For SELECT lookup, it is generally safe but still use carefully.

---

## 13. Sequential Scan vs Index Scan

Sequential scan:

```text
read table rows one by one
```

Index scan:

```text
search index then fetch matching row
```

Table:

```text
+----------------+-----------------------------+
| Plan           | Meaning                     |
+----------------+-----------------------------+
| Seq Scan       | scans table                 |
| Index Scan     | uses index, fetches row     |
| Index Only Scan| uses index without heap row |
+----------------+-----------------------------+
```

For small tables, PostgreSQL may choose sequential scan even if index exists.

Why?

```text
If table is tiny, scanning it may be cheaper than using index.
```

This is normal.

For large table:

```text
short_code lookup should use index
```

ASCII:

```text
Small table:
    scan 10 rows is cheap

Large table:
    scan 100M rows is deadly
```

Do not panic if local dev with 5 rows shows Seq Scan.

Test with realistic row count when learning performance.

---

## 14. Covering Index And INCLUDE

A covering index can include extra columns needed by query.

PostgreSQL syntax:

```sql
CREATE INDEX idx_short_urls_redirect_lookup
ON short_urls(short_code)
INCLUDE (long_url, status, expires_at);
```

Idea:

```text
Index key:
    short_code

Included columns:
    long_url, status, expires_at
```

This may allow:

```text
Index Only Scan
```

ASCII:

```text
Normal index:

short_code -> row pointer -> table row -> long_url/status/expires_at


Covering index:

short_code -> long_url/status/expires_at directly from index
```

Pros:

```text
can reduce heap/table access
good for read-heavy lookup
```

Cons:

```text
larger index
more write cost
more storage
index bloat risk
long_url TEXT can make index large
```

Important caution:

```text
Do not blindly INCLUDE large long_url if URLs are huge.
```

For MiniURLShortener early version:

```text
Unique index on short_code is enough.
```

Covering index is later optimization after measuring.

---

## 15. Partial Index For Active URLs

Partial index indexes only rows matching condition.

Example:

```sql
CREATE INDEX idx_short_urls_active_short_code
ON short_urls(short_code)
WHERE status = 'ACTIVE';
```

This helps queries like:

```sql
SELECT long_url
FROM short_urls
WHERE short_code = ?
  AND status = 'ACTIVE';
```

But for redirect, service may need to distinguish:

```text
missing -> 404
blocked -> 403
expired -> 410
deleted -> 404
```

If query only active rows:

```text
blocked row may look missing
```

That may be acceptable or not depending on API contract.

ASCII:

```text
Full index:
    ACTIVE
    BLOCKED
    DELETED

Partial active index:
    only ACTIVE
```

Use partial index when:

```text
query always filters status
table has many inactive rows
you do not need to distinguish inactive states from missing
```

For MiniURLShortener:

```text
main unique index on short_code first
partial index later only if needed
```

Senior rule:

```text
Index must match query semantics, not only speed.
```

---

## 16. Index Selectivity

Selectivity means how unique or filtering a column is.

High selectivity:

```text
short_code
email
id
```

Low selectivity:

```text
status
boolean active
country if few countries
```

Index on high-selectivity column is very useful.

Index on low-selectivity column may be less useful.

Example:

```text
status = ACTIVE
```

If 95% rows are ACTIVE, index on status alone is not very helpful.

ASCII:

```text
short_code:
    abc123 matches 1 row out of millions
    very selective

status:
    ACTIVE matches millions of rows
    low selectivity
```

For redirect lookup:

```text
short_code = ?
```

is highly selective.

That is why index is powerful.

Combined index example:

```sql
CREATE INDEX idx_status_short_code
ON short_urls(status, short_code);
```

But if query is only:

```sql
WHERE short_code = ?
```

then this index is not ideal compared to:

```sql
ON short_urls(short_code)
```

Index order matters.

---

## 17. Indexes And Redis Cache

Redis does not remove need for DB index.

Why?

Because cache miss still hits DB.

Cache miss happens when:

```text
first request
TTL expired
Redis restarted
cache evicted
cache key invalidated
Redis failed
negative cache disabled
```

If DB fallback is slow, p99 suffers.

ASCII:

```text
Redirect request
   |
   v
Redis
   |
   +-- hit -> fast
   |
   +-- miss -> PostgreSQL must still be fast
```

Cache hit rate example:

```text
99% hit rate
1% DB fallback
```

At 50,000 RPS:

```text
1% fallback = 500 DB reads/sec
```

Those 500 reads/sec must use index.

Memory:

```text
Cache reduces frequency.
Index reduces cost per DB fallback.
```

Both are complementary.

---

## 18. Index Write Cost

Indexes are not free.

Every insert into `short_urls` must update:

```text
table
primary key index
short_code unique index
other indexes
```

ASCII:

```text
INSERT row
   |
   +-- write table data
   |
   +-- update primary key index
   |
   +-- update short_code index
   |
   +-- update any other indexes
```

More indexes:

```text
faster reads for some queries
slower writes
more storage
more maintenance
```

For URL shortener:

```text
short_code unique index is worth it
```

But avoid adding unnecessary indexes.

Bad:

```sql
CREATE INDEX idx_long_url ON short_urls(long_url);
CREATE INDEX idx_status ON short_urls(status);
CREATE INDEX idx_created_at_status_long_url ON short_urls(created_at, status, long_url);
```

Unless queries need them.

Rule:

```text
Add indexes for real query patterns, not imagination.
```

---

## 19. Index Bloat And Maintenance

Over time, indexes can grow inefficient due to updates/deletes.

This is called:

```text
index bloat
```

Causes:

```text
many updates
many deletes
dead tuples
vacuum lag
high churn
```

Symptoms:

```text
index size grows
queries slow down
storage increases
vacuum pressure
```

PostgreSQL maintenance:

```text
VACUUM
ANALYZE
REINDEX when needed
autovacuum tuning
```

For MiniURLShortener:

```text
short_urls mostly insert-heavy
status may update to BLOCKED/DELETED
click events should be in separate table later
```

Important:

```text
Do not update click_count synchronously on short_urls for every redirect.
```

Why?

```text
hot row updates
index/table bloat
write contention
slow redirect path
```

Click analytics should be async.

---

## 20. Common Query Patterns

### Redirect lookup

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?;
```

Index:

```sql
UNIQUE(short_code)
```

### Duplicate alias check

```sql
SELECT 1
FROM short_urls
WHERE short_code = ?;
```

Index:

```sql
UNIQUE(short_code)
```

### Admin list by owner

Future:

```sql
SELECT *
FROM short_urls
WHERE owner_id = ?
ORDER BY created_at DESC
LIMIT 50;
```

Possible index:

```sql
CREATE INDEX idx_short_urls_owner_created
ON short_urls(owner_id, created_at DESC);
```

### Cleanup expired URLs

Future:

```sql
SELECT id
FROM short_urls
WHERE expires_at < now()
  AND status = 'ACTIVE'
LIMIT 1000;
```

Possible index:

```sql
CREATE INDEX idx_short_urls_expiry_active
ON short_urls(expires_at)
WHERE status = 'ACTIVE';
```

Important:

```text
Each index should match an actual query.
```

For this chapter, redirect lookup is the main query.

---

## 21. Java/Spring Data Repository

Simple repository:

```java
package com.miniurl.shortener.url.repository;

import com.miniurl.shortener.url.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);
}
```

Spring Data generates query like:

```sql
SELECT *
FROM short_urls
WHERE short_code = ?
```

For early implementation, this is okay.

Later projection:

```java
public interface RedirectLookupView {
    String getLongUrl();
    String getStatus();
    Instant getExpiresAt();
}
```

Repository:

```java
@Query("select s.longUrl as longUrl, s.status as status, s.expiresAt as expiresAt from ShortUrl s where s.shortCode = :shortCode")
Optional<RedirectLookupView> findRedirectByShortCode(String shortCode);
```

Why projection?

```text
loads only needed columns
smaller object mapping
clear redirect intent
```

But do not optimize too early.

First:

```text
correct unique index
simple query
measure
```

---

## 22. Step-by-Step Dry Runs

### Dry Run 1: Lookup Without Index

Table rows:

```text
1 million
```

Query:

```sql
SELECT long_url
FROM short_urls
WHERE short_code = 'abc123';
```

Execution:

```text
1. PostgreSQL checks table.
2. No useful index exists.
3. It scans rows one by one.
4. It compares short_code for each row.
5. It eventually finds abc123.
6. Query latency grows with table size.
```

ASCII:

```text
row1 -> no
row2 -> no
row3 -> no
...
row800000 -> yes
```

Result:

```text
slow at scale
bad p99
high DB CPU
```

---

### Dry Run 2: Lookup With Unique Index

Index:

```sql
CREATE UNIQUE INDEX uk_short_urls_short_code
ON short_urls(short_code);
```

Execution:

```text
1. Query planner sees WHERE short_code = ?.
2. Planner chooses index scan.
3. B-Tree quickly finds abc123.
4. Index points to table row.
5. DB returns long_url, status, expires_at.
```

ASCII:

```text
B-Tree search
   |
   v
abc123 found
   |
   v
row pointer
   |
   v
return row
```

Result:

```text
fast DB fallback
stable redirect latency
```

---

### Dry Run 3: Duplicate Alias Race

Two requests:

```text
customAlias = sale2026
```

Timeline:

```text
1. Request A checks existsByShortCode -> false.
2. Request B checks existsByShortCode -> false.
3. Request A inserts.
4. Request B inserts.
```

Without unique index:

```text
both rows inserted
```

With unique index:

```text
Request A succeeds
Request B fails with unique violation
```

Service maps failure:

```text
409 ALIAS_ALREADY_EXISTS
```

---

### Dry Run 4: Redis Miss Fallback

Redis:

```text
miss
```

DB:

```text
indexed short_code lookup
```

Execution:

```text
1. Redis misses.
2. Service calls repository.
3. DB uses index.
4. Row returns quickly.
5. Service fills Redis.
6. Redirect succeeds.
```

Memory:

```text
Cache miss should be slower than hit but still controlled.
```

---

### Dry Run 5: Query Plan Debug

You run:

```sql
EXPLAIN ANALYZE
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = 'abc123';
```

Good output contains:

```text
Index Scan using uk_short_urls_short_code
```

Bad output for large table contains:

```text
Seq Scan on short_urls
```

Debug next:

```text
Does index exist?
Is query using same column?
Is data type matching?
Are statistics updated?
Is table tiny in local test?
```

---

## 23. Internal Execution Walkthrough

Full redirect DB fallback:

```text
1. Request enters RedirectController.
2. Service checks Redis.
3. Redis miss.
4. Service calls repository.findByShortCode(shortCode).
5. Hibernate creates SQL.
6. PostgreSQL receives SQL.
7. Planner checks available indexes.
8. Planner chooses unique short_code index.
9. Executor scans B-Tree.
10. Matching row pointer found.
11. Table row fetched.
12. long_url/status/expires_at returned.
13. Hibernate maps row to entity/projection.
14. Service validates status and expiry.
15. Service fills Redis.
16. Controller returns 302.
```

ASCII:

```text
Spring Boot
   |
   v
Repository
   |
   v
SQL WHERE short_code = ?
   |
   v
PostgreSQL planner
   |
   v
B-Tree index
   |
   v
row fetch
   |
   v
redirect metadata
```

Important:

```text
Indexing is invisible in Java code but critical in production behavior.
```

---

## 24. Testing Strategy

### Migration test

Confirm index exists.

Example SQL:

```sql
SELECT indexname
FROM pg_indexes
WHERE tablename = 'short_urls';
```

Expected:

```text
uk_short_urls_short_code
```

### Duplicate constraint test

Integration test:

```text
1. Insert row with short_code abc123.
2. Insert another row with same short_code.
3. Assert unique constraint violation.
```

### Repository test

```text
findByShortCode returns correct row
existsByShortCode returns true/false
```

### Query plan learning test

For local performance learning:

```text
1. Insert many rows.
2. Run EXPLAIN ANALYZE.
3. Confirm index scan.
```

Do not make fragile EXPLAIN tests part of normal CI unless controlled.

### Redirect integration test

```text
Redis disabled or miss
GET /abc123
DB lookup succeeds
302 returned
```

Testing goal:

```text
prove database constraint and lookup behavior
```

Not every index behavior must be unit tested.

---

## 25. Observability And Slow Query Debugging

Important DB metrics:

```text
query latency
slow query count
rows scanned
index usage
DB CPU
buffer cache hit ratio
locks
connections
Hikari pending connections
```

PostgreSQL tools:

```text
EXPLAIN ANALYZE
pg_stat_statements
pg_indexes
pg_stat_user_indexes
auto_explain
slow query logs
```

Useful query:

```sql
SELECT schemaname, relname, indexrelname, idx_scan
FROM pg_stat_user_indexes
WHERE relname = 'short_urls';
```

This shows whether indexes are used.

Slow query signs:

```text
Seq Scan on huge table
high rows removed by filter
high execution time
high shared buffer reads
```

ASCII:

```text
Slow redirect
   |
   v
Check app p99
   |
   v
Check DB fallback count
   |
   v
Check slow query logs
   |
   v
EXPLAIN query
   |
   v
Index scan or seq scan?
```

Senior debugging rule:

```text
Always connect app latency to DB query plan.
```

---

## 26. Production Failure Stories

### Failure Story 1: Missing Index Took Down Redirects

The system worked in staging with 1,000 rows.

Production had 50 million rows.

Redirect query did sequential scan.

DB CPU hit 100%.

Root cause:

```text
No index on short_code.
```

Fix:

```text
Add unique index on short_code.
Verify with EXPLAIN.
```

Lesson:

```text
Small test data hides missing indexes.
```

---

### Failure Story 2: Duplicate Short Codes Corrupted Redirects

Application checked alias availability before insert.

Two concurrent requests passed the check.

Both inserted same code.

Root cause:

```text
No DB unique constraint.
```

Fix:

```text
Unique index on short_code.
Map unique violation to 409.
```

Lesson:

```text
Only the database can reliably enforce uniqueness under concurrency.
```

---

### Failure Story 3: Too Many Indexes Slowed Writes

Team added indexes for every column.

Create API became slower.

Disk usage grew.

Root cause:

```text
Indexing without query-driven reasoning.
```

Fix:

```text
Keep required indexes.
Remove unused indexes.
Track pg_stat_user_indexes.
```

Lesson:

```text
Indexes speed reads but tax writes.
```

---

### Failure Story 4: Cache Miss Storm Exposed Bad Index

Redis restarted.

All redirect requests fell back to DB.

DB query was not indexed well.

Outage happened.

Root cause:

```text
Cache hid the database weakness until cache failed.
```

Fix:

```text
Proper DB index.
Load test cache-miss path.
```

Lesson:

```text
Never let cache be the only thing making DB query survivable.
```

---

### Failure Story 5: Index Not Used Due To Query Mismatch

Index existed on `short_code`.

Query used function:

```sql
WHERE lower(short_code) = lower(?)
```

Normal index was not used.

Root cause:

```text
Query expression did not match index.
```

Fix:

```text
Normalize short_code before storing/searching.
Or create expression index if needed.
```

Lesson:

```text
Index must match query shape.
```

---

## 27. Debugging Mindset

When redirect DB lookup is slow, ask:

```text
Does short_code unique index exist?
Is query filtering by short_code exactly?
Is data type same as column type?
Is query applying functions to indexed column?
Is table small, causing local Seq Scan?
Is production table using Index Scan?
Are DB statistics updated?
Is Redis miss rate high?
Is Hikari pool waiting?
Is DB CPU high?
Are there too many indexes hurting writes?
```

Debug commands:

```sql
\d short_urls

EXPLAIN ANALYZE
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = 'abc123';

SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'short_urls';

SELECT relname, indexrelname, idx_scan
FROM pg_stat_user_indexes
WHERE relname = 'short_urls';
```

Debug map:

```text
Seq Scan on huge table:
    missing index
    query mismatch
    stale stats

Unique violation:
    duplicate alias/generation collision
    map to domain error

High write latency:
    too many indexes
    index bloat
    slow disk

High p99 only on cache miss:
    DB lookup/index issue
```

Golden rule:

```text
For URL lookup, first inspect the exact SQL and exact index.
```

---

## 28. Common Mistakes

### Mistake 1: No Index On short_code

Wrong:

```text
Redirect lookup scans table.
```

Correct:

```sql
CREATE UNIQUE INDEX uk_short_urls_short_code
ON short_urls(short_code);
```

### Mistake 2: Only Application Uniqueness Check

Wrong:

```text
existsByShortCode before insert is enough.
```

Correct:

```text
application check + DB unique constraint
```

### Mistake 3: SELECT *

Wrong:

```sql
SELECT *
FROM short_urls
WHERE short_code = ?;
```

Correct:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?;
```

### Mistake 4: Too Many Indexes

Wrong:

```text
index every column
```

Correct:

```text
index real query patterns
```

### Mistake 5: Ignoring Cache Miss Path

Wrong:

```text
Redis hit rate is high, DB index does not matter.
```

Correct:

```text
cache can miss or fail; DB fallback must be fast
```

### Mistake 6: Function On Indexed Column

Wrong:

```sql
WHERE lower(short_code) = lower(?)
```

Correct:

```text
normalize before storing
query exact indexed column
```

### Mistake 7: Not Using EXPLAIN

Wrong:

```text
guess why query is slow
```

Correct:

```text
inspect actual query plan
```

### Mistake 8: Confusing Status Index With Lookup Index

Wrong:

```sql
CREATE INDEX idx_status ON short_urls(status);
```

and assume redirect lookup is optimized.

Correct:

```sql
CREATE UNIQUE INDEX uk_short_urls_short_code
ON short_urls(short_code);
```

---

## 29. Interview-Ready Explanation

If interviewer asks:

```text
How would you index the URL shortener table for redirect lookup?
```

Strong answer:

```text
The redirect query is `WHERE short_code = ?`, so the most important index is a unique B-Tree
index on `short_code`. It gives two benefits: fast lookup and correctness. Fast lookup matters
because Redis can miss or fail, and the database fallback must still be quick. Correctness
matters because two rows must never share the same short code, especially under concurrent
custom alias or generated-code insert races. I would start with `CREATE UNIQUE INDEX
uk_short_urls_short_code ON short_urls(short_code)`. Then I would verify the query plan with
`EXPLAIN ANALYZE` and look for an index scan. I would avoid unnecessary indexes because every
index adds write and storage cost. Later, if measurement shows benefit, I might consider a
covering index or partial indexes for specific admin/cleanup queries, but the required index
for redirect is the unique short_code index.
```

Senior version:

```text
Redis reduces the number of database reads, but indexing reduces the cost of each fallback read.
A production URL shortener needs both: cache for read amplification and a unique index for fast,
correct source-of-truth lookup.
```

Why this is strong:

```text
1. Starts from actual query.
2. Chooses correct B-Tree unique index.
3. Explains performance and correctness.
4. Mentions concurrency race.
5. Mentions Redis miss fallback.
6. Mentions EXPLAIN.
7. Mentions index write tradeoff.
8. Avoids premature over-indexing.
```

---

## 30. Senior Engineer Checklist

Before calling DB indexing production-shaped, confirm:

```text
[ ] short_code has unique index
[ ] duplicate short_code is impossible at DB level
[ ] redirect query filters by short_code
[ ] query does not apply function to short_code
[ ] repository method is clear
[ ] Redis miss path uses indexed lookup
[ ] EXPLAIN shows index scan on realistic data
[ ] duplicate alias maps to 409
[ ] generated collision is retried or handled
[ ] unnecessary indexes are avoided
[ ] write cost is understood
[ ] index usage is observable
[ ] slow query logs are enabled in production
[ ] pg_stat_statements or equivalent is available
[ ] cache miss path is load tested
[ ] index migration is included in Flyway
```

---

## 31. One-Page Cheat Sheet

```text
Core mental model:
Index = sorted lookup map.

URL lookup query:
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?;

Required index:
CREATE UNIQUE INDEX uk_short_urls_short_code
ON short_urls(short_code);

Why unique:
1. fast lookup
2. prevents duplicate short codes
3. safe under concurrency

Without index:
Seq Scan
work grows with table size

With index:
Index Scan
jump to row

Redis relation:
Redis reduces how often DB is called.
Index makes DB fallback fast.

EXPLAIN:
Good for large table:
    Index Scan using uk_short_urls_short_code

Bad:
    Seq Scan on short_urls

Tradeoff:
indexes speed reads
indexes slow writes
indexes use storage

Do not:
SELECT * on hot path
index every column
trust app-only uniqueness
ignore cache miss path
apply lower(short_code) unless indexed
```

---

## 32. One Picture To Remember

```text
              DB INDEXING FOR URL LOOKUP MENTAL MODEL

                      "Jump, do not scan"

GET /abc123
    |
    v
Redis cache
    |
    +-- HIT ----------------------------+
    |                                   |
    v                                   |
302 redirect                            |
                                        |
    +-- MISS                            |
           |                            |
           v                            |
+------------------------------+        |
| PostgreSQL query             |        |
| WHERE short_code = 'abc123'  |        |
+---------------+--------------+        |
                |                       |
                v                       |
+------------------------------+        |
| B-Tree Unique Index          |        |
| uk_short_urls_short_code     |        |
+---------------+--------------+        |
                |                       |
                v                       |
+------------------------------+        |
| Row found                    |        |
| long_url/status/expires_at   |        |
+---------------+--------------+        |
                |                       |
                v                       |
        Redis fill + 302 ---------------+


WITHOUT INDEX:
    scan many rows

WITH INDEX:
    short_code -> row pointer

FINAL MEMORY:

Redis protects frequency.
Index protects fallback cost.
Unique index protects correctness.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. The redirect lookup query is `WHERE short_code = ?`, so `short_code` needs a unique B-Tree index.
2. The index improves performance by avoiding table scans and improves correctness by preventing duplicate short codes.
3. Redis does not remove the need for an index because cache misses and failures still fall back to PostgreSQL.
4. Use EXPLAIN ANALYZE to confirm the query plan uses an index scan on realistic data.
5. Indexes are not free, so add them based on real query patterns and avoid over-indexing.
```

Next chapter:

```text
024_Connection_Pooling_Hikari.md
```
