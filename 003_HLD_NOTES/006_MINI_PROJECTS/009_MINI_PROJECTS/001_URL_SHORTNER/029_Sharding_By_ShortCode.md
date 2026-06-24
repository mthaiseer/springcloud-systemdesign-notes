# 029_Sharding_By_ShortCode.md
# MiniURLShortener — Sharding By ShortCode

> Core mental model: **Sharding splits one huge database table into multiple smaller database partitions/shards. For MiniURLShortener, sharding by `shortCode` means the same public lookup key used in `GET /{shortCode}` also decides which database shard stores the URL row.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What Is Sharding?](#4-what-is-sharding)
- [5. Partitioning vs Sharding](#5-partitioning-vs-sharding)
- [6. Why ShortCode Is A Natural Shard Key](#6-why-shortcode-is-a-natural-shard-key)
- [7. Single DB Baseline](#7-single-db-baseline)
- [8. Sharded Target Architecture](#8-sharded-target-architecture)
- [9. Hash-Based Sharding](#9-hash-based-sharding)
- [10. Range-Based Sharding](#10-range-based-sharding)
- [11. Consistent Hashing Intro](#11-consistent-hashing-intro)
- [12. Shard Router Mental Model](#12-shard-router-mental-model)
- [13. Create Flow With Sharding](#13-create-flow-with-sharding)
- [14. Redirect Flow With Sharding](#14-redirect-flow-with-sharding)
- [15. Redis Cache And Sharding](#15-redis-cache-and-sharding)
- [16. Unique Constraint In Sharded World](#16-unique-constraint-in-sharded-world)
- [17. Generated ID / ShortCode And Sharding](#17-generated-id--shortcode-and-sharding)
- [18. Hot Shard Problem](#18-hot-shard-problem)
- [19. Cross-Shard Query Problem](#19-cross-shard-query-problem)
- [20. Resharding Problem](#20-resharding-problem)
- [21. Shard Metadata](#21-shard-metadata)
- [22. Java/Spring Boot Implementation Sketch](#22-javaspring-boot-implementation-sketch)
- [23. Step-by-Step Dry Runs](#23-step-by-step-dry-runs)
- [24. Internal Execution Walkthrough](#24-internal-execution-walkthrough)
- [25. Testing Strategy](#25-testing-strategy)
- [26. Metrics And Observability](#26-metrics-and-observability)
- [27. Production Failure Stories](#27-production-failure-stories)
- [28. Debugging Mindset](#28-debugging-mindset)
- [29. Common Mistakes](#29-common-mistakes)
- [30. Interview-Ready Explanation](#30-interview-ready-explanation)
- [31. Senior Engineer Checklist](#31-senior-engineer-checklist)
- [32. One-Page Cheat Sheet](#32-one-page-cheat-sheet)
- [33. One Picture To Remember](#33-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener starts with one PostgreSQL database:

```text
short_urls table
```

At small and medium scale, one database is enough if you have:

```text
good schema
unique index on short_code
Redis cache
HikariCP tuning
read replicas
observability
```

But at very large scale, one database may become too large or too busy.

Problems:

```text
table has billions of rows
indexes become huge
backup/restore takes too long
vacuum pressure grows
write throughput reaches limit
storage reaches limit
one primary becomes too important
regional scaling becomes harder
```

Sharding solves this by splitting data.

Instead of one huge table:

```text
short_urls on DB_0
short_urls on DB_1
short_urls on DB_2
short_urls on DB_3
```

ASCII:

```text
Before sharding:

+-----------------------------+
| PostgreSQL Primary          |
| short_urls: 5 billion rows  |
+-----------------------------+


After sharding:

+-------------------+   +-------------------+
| Shard 0           |   | Shard 1           |
| short_urls subset |   | short_urls subset |
+-------------------+   +-------------------+

+-------------------+   +-------------------+
| Shard 2           |   | Shard 3           |
| short_urls subset |   | short_urls subset |
+-------------------+   +-------------------+
```

Production memory:

```text
Sharding is used when one database is no longer the right unit of scale.
```

---

## 2. The One Core Mental Model

The core mental model:

```text
SHARD KEY DECIDES WHERE THE ROW LIVES
```

For MiniURLShortener:

```text
shortCode -> shard number -> database
```

Example:

```text
shortCode = abc123
hash(abc123) % 4 = 2

Therefore:
    store/read from shard 2
```

ASCII:

```text
GET /abc123
    |
    v
hash("abc123") % 4
    |
    v
Shard 2
    |
    v
SELECT long_url FROM short_urls WHERE short_code = 'abc123'
```

One-line memory:

```text
Sharding adds a routing decision before database access.
```

Without sharding:

```text
Repository queries one DB.
```

With sharding:

```text
Router chooses DB.
Repository queries chosen DB.
```

The hard part is not splitting data once.

The hard part is:

```text
routing correctly
keeping uniqueness
handling resharding
debugging failures
avoiding hot shards
```

---

## 3. Problem Statement

Design sharding by `shortCode` for MiniURLShortener.

The design must support:

```text
1. Route redirect lookup to correct shard.
2. Route create insert to correct shard.
3. Keep `shortCode` unique.
4. Preserve fast `GET /{shortCode}` lookup.
5. Work with Redis cache-aside.
6. Avoid cross-shard query on redirect path.
7. Handle shard failures clearly.
8. Support future resharding.
9. Expose metrics per shard.
10. Explain tradeoffs in interviews.
```

Main query:

```sql
SELECT long_url, status, expires_at
FROM short_urls
WHERE short_code = ?;
```

Sharded version:

```text
1. Pick shard using shortCode.
2. Run same indexed query only on that shard.
```

Non-goals:

```text
full distributed transaction engine
multi-region active-active
global secondary indexes
complete resharding implementation
```

This chapter teaches first-principles sharding for URL lookup.

---

## 4. What Is Sharding?

Sharding means horizontal splitting of data across multiple databases/nodes.

Horizontal split:

```text
same table structure
different rows on different shards
```

ASCII:

```text
short_urls logical table

Rows:
abc123
sale2026
mohamed
x9z8
hello1
world2


Shard 0:
abc123
hello1

Shard 1:
sale2026
x9z8

Shard 2:
mohamed
world2
```

Each shard has same schema:

```sql
CREATE TABLE short_urls (
    id BIGSERIAL PRIMARY KEY,
    short_code VARCHAR(32) NOT NULL,
    long_url TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    expires_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL
);
```

But each shard has only some rows.

Sharding improves:

```text
storage capacity
write scalability
index size
maintenance isolation
failure isolation if designed well
```

Sharding adds complexity:

```text
routing
resharding
cross-shard queries
distributed uniqueness
operational overhead
```

Rule:

```text
Do not shard too early.
But know how to shard when needed.
```

---

## 5. Partitioning vs Sharding

These terms are often confused.

### Partitioning

Usually inside one database system.

Example:

```text
PostgreSQL partitioned table by created_at
```

The database knows partitions.

Application may still query one logical table.

ASCII:

```text
One PostgreSQL
  |
  +-- partition 2026_01
  +-- partition 2026_02
  +-- partition 2026_03
```

### Sharding

Across separate database instances or clusters.

Application/router decides target shard.

ASCII:

```text
Application Router
  |
  +-- PostgreSQL shard 0
  +-- PostgreSQL shard 1
  +-- PostgreSQL shard 2
```

MiniURLShortener sharding by shortCode means:

```text
application-level routing to different DBs
```

Partitioning can help before sharding.

But sharding is bigger operational step.

---

## 6. Why ShortCode Is A Natural Shard Key

Redirect API input is:

```text
GET /{shortCode}
```

So every read already has `shortCode`.

That makes it a good shard key.

Benefits:

```text
1. No extra lookup needed to route.
2. Redirect reads hit exactly one shard.
3. short_code index remains local per shard.
4. Cache key and shard key align.
5. Data distribution can be good if shortCode is random.
```

ASCII:

```text
Client gives:
    shortCode

Router uses:
    shortCode

Database query uses:
    shortCode

Everything aligns.
```

Bad shard key example:

```text
ownerId
```

For redirect:

```text
GET /abc123
```

You do not know ownerId unless you query somewhere first.

That would require:

```text
lookup shortCode -> ownerId -> shard
```

which defeats fast redirect.

Therefore:

```text
shortCode is the best redirect-path shard key.
```

---

## 7. Single DB Baseline

Before sharding:

```text
Client -> Spring Boot -> Redis -> PostgreSQL
```

ASCII:

```text
+--------+      +-------------+      +-------+      +------------+
| Client | ---> | Spring Boot | ---> | Redis | ---> | PostgreSQL |
+--------+      +-------------+      +-------+      +------------+
```

DB table:

```text
short_urls
```

Index:

```sql
CREATE UNIQUE INDEX uk_short_urls_short_code
ON short_urls(short_code);
```

Redirect miss:

```text
Redis miss -> query single PostgreSQL
```

This is simple.

Keep this as long as possible.

Only shard when signs appear:

```text
one DB storage too large
write throughput limit
index size too large
maintenance too slow
replication lag high
single primary bottleneck
```

Sharding should be a scaling step, not first design step.

---

## 8. Sharded Target Architecture

Sharded architecture:

```text
Client
  |
  v
Spring Boot
  |
  v
Redis cache
  |
  v
Shard Router
  |
  +-- Shard 0 PostgreSQL
  +-- Shard 1 PostgreSQL
  +-- Shard 2 PostgreSQL
  +-- Shard 3 PostgreSQL
```

ASCII:

```text
+--------+
| Client |
+---+----+
    |
    v
+-------------------+
| Spring Boot       |
| Redirect Service  |
+---+---------------+
    |
    v
+-------------------+
| Redis Cache       |
+---+---------------+
    |
 miss
    v
+-------------------+
| Shard Router      |
| hash(shortCode)   |
+---+---+---+---+---+
    |   |   |   |
    v   v   v   v
  DB0 DB1 DB2 DB3
```

Important:

```text
Redis cache can hide many DB reads.
But on cache miss, router must choose correct shard.
```

Each shard still has:

```text
local short_code unique index
```

---

## 9. Hash-Based Sharding

Hash-based sharding uses hash of shard key.

Formula:

```text
shard = hash(shortCode) % numberOfShards
```

Example:

```text
numberOfShards = 4

hash("abc123") % 4 = 2
hash("sale2026") % 4 = 1
hash("mohamed") % 4 = 3
```

ASCII:

```text
shortCode
   |
   v
hash(shortCode)
   |
   v
mod 4
   |
   +-- 0 -> shard 0
   +-- 1 -> shard 1
   +-- 2 -> shard 2
   +-- 3 -> shard 3
```

Pros:

```text
simple
usually good distribution if key random
redirect hits one shard
easy to implement
```

Cons:

```text
changing shard count changes mapping
resharding is hard
range scans by key are not meaningful
```

For MiniURLShortener generated short codes are usually random enough.

Therefore hash-based sharding is a strong starting mental model.

---

## 10. Range-Based Sharding

Range-based sharding assigns ranges to shards.

Example:

```text
shortCode starting a-f -> shard 0
shortCode starting g-l -> shard 1
shortCode starting m-r -> shard 2
shortCode starting s-z -> shard 3
```

ASCII:

```text
a-f  -> DB0
g-l  -> DB1
m-r  -> DB2
s-z  -> DB3
```

Pros:

```text
simple to reason
range movement possible
human-readable routing
```

Cons:

```text
hot ranges possible
bad distribution if codes not uniform
celebrity/custom aliases may cluster
manual rebalancing
```

Example hot range:

```text
many marketing links start with sale...
```

Then shard for `s-z` gets hot.

For URL shortener:

```text
hash-based is usually safer than prefix range
```

because random short codes distribute better.

---

## 11. Consistent Hashing Intro

Modulo sharding problem:

```text
shard = hash(key) % N
```

If N changes from 4 to 5:

```text
many keys move
```

Example:

```text
hash(abc123) % 4 = 2
hash(abc123) % 5 = 4
```

That means adding one shard can remap most keys.

Consistent hashing reduces key movement.

Mental model:

```text
place shards on a ring
place keys on same ring
key goes to next shard clockwise
```

ASCII:

```text
          [Shard A]
              ^
              |
   key1       |        key2
      \       |       /
       \      |      /
        +-----+-----+
       /             \
 [Shard D]         [Shard B]
       \             /
        +-----+-----+
              |
          [Shard C]
```

When adding a shard:

```text
only nearby keys move
```

For MiniURLShortener early sharding:

```text
simple modulo is okay for learning
```

For production large-scale:

```text
consistent hashing or shard metadata mapping is better
```

---

## 12. Shard Router Mental Model

Shard router is the component that maps shortCode to DataSource.

```text
shortCode -> shardId -> DataSource
```

ASCII:

```text
RedirectService
    |
    v
ShardRouter.route(shortCode)
    |
    +-- shardId 0 -> DataSource0
    +-- shardId 1 -> DataSource1
    +-- shardId 2 -> DataSource2
```

Router must be:

```text
deterministic
fast
well-tested
observable
versioned if mapping changes
```

Deterministic means:

```text
same shortCode always maps to same shard
```

If router changes accidentally:

```text
valid shortCode may be searched in wrong shard
returns 404
```

That is dangerous.

Rule:

```text
Shard routing logic is critical infrastructure code.
```

---

## 13. Create Flow With Sharding

Create API:

```http
POST /api/v1/urls
```

Flow:

```text
1. Validate request.
2. Generate or accept shortCode.
3. Route by shortCode.
4. Insert row into selected shard.
5. Add to Bloom filter.
6. Write Redis cache optional.
7. Return response.
```

ASCII:

```text
POST create
   |
   v
Generate shortCode
   |
   v
hash(shortCode) -> shardId
   |
   v
Insert into shard
   |
   v
Redis/Bloom update
   |
   v
201 Created
```

Custom alias:

```text
customAlias = mohamed
```

Shard:

```text
hash("mohamed") % N
```

Uniqueness:

```text
because routing is deterministic,
all attempts for same alias go to same shard.
```

Therefore local unique index on that shard catches duplicates.

---

## 14. Redirect Flow With Sharding

Redirect API:

```http
GET /abc123
```

Flow:

```text
1. Validate shortCode.
2. Bloom filter check optional.
3. Check Redis cache.
4. If Redis hit, return redirect.
5. If Redis miss, route by shortCode.
6. Query selected shard.
7. Fill Redis.
8. Return redirect or error.
```

ASCII:

```text
GET /abc123
   |
   v
Redis?
   |
   +-- hit -> 302
   |
   +-- miss
          |
          v
    hash(abc123) -> shard 2
          |
          v
    SELECT on shard 2
          |
          v
    cache fill + 302
```

The redirect path still queries only one shard.

That is the whole point.

Bad sharded redirect:

```text
query all shards until found
```

This is scatter-gather and should be avoided for hot path.

---

## 15. Redis Cache And Sharding

Redis cache key remains:

```text
redirect:v1:{shortCode}
```

Cache value may include:

```text
longUrl
status
expiresAt
optional shardId
```

Do you need shardId in cache?

Usually no for redirect hit.

But useful for debugging:

```json
{
  "longUrl": "https://example.com",
  "status": "ACTIVE",
  "expiresAt": null,
  "shardId": 2
}
```

ASCII:

```text
Redis hit:
    no DB shard needed

Redis miss:
    route by shortCode
```

Cache still works above shards:

```text
Redis hides many sharded DB reads.
```

If shard routing changes during resharding:

```text
cached values may still redirect
but DB fallback may need migration-aware routing
```

This is why resharding is hard.

---

## 16. Unique Constraint In Sharded World

In single DB:

```text
UNIQUE(short_code)
```

is global.

In sharded DB:

```text
UNIQUE(short_code)
```

is local per shard.

Is that enough?

If shard routing is deterministic by shortCode:

```text
same shortCode always maps to same shard
```

Then local unique is enough.

ASCII:

```text
shortCode = abc123
hash(abc123) -> shard 2

All abc123 inserts go to shard 2.
Shard 2 unique index prevents duplicates.
```

If routing is inconsistent:

```text
abc123 could go to shard 1 sometimes and shard 2 sometimes
```

Then duplicates can happen.

Therefore:

```text
correct routing guarantees global uniqueness with local unique indexes
```

This is a crucial interview point.

---

## 17. Generated ID / ShortCode And Sharding

Some URL shorteners generate numeric IDs and convert to Base62.

Example:

```text
id = 125000
shortCode = base62(id)
```

But in sharded systems, generating global sequential IDs is tricky.

Options:

```text
1. Snowflake-style distributed ID.
2. Dedicated ID generator service.
3. Database sequence per shard with shard prefix.
4. Random short code with collision retry.
5. NanoID/random Base62.
```

For sharding by shortCode:

```text
generate shortCode first
then route by shortCode
```

Random code approach:

```text
shortCode = random 7 chars
shard = hash(shortCode) % N
insert into shard
if unique conflict, retry
```

ASCII:

```text
generate random code
      |
      v
route by code
      |
      v
insert with unique index
      |
      +-- success
      |
      +-- conflict -> generate another
```

This is simple and shard-friendly.

---

## 18. Hot Shard Problem

A hot shard receives too much traffic.

Causes:

```text
bad shard key
non-uniform distribution
custom aliases clustering
hot shortCodes mapping to same shard
too few shards
one celebrity link
```

ASCII:

```text
Shard traffic:

Shard 0: ####
Shard 1: #####
Shard 2: ############################
Shard 3: ###
```

If using hash(shortCode), distribution of many keys is usually good.

But one viral key still maps to one shard on DB fallback.

Redis/CDN should absorb reads.

Create traffic also can skew if many custom aliases hash to same shard randomly, but over many aliases it usually balances.

Metrics needed:

```text
requests per shard
DB CPU per shard
query latency per shard
connection pool per shard
cache miss per shard
```

Hot shard mitigation:

```text
more shards
consistent hashing
move ranges/virtual nodes
cache hot keys
read replicas for hot shard
```

---

## 19. Cross-Shard Query Problem

Redirect is easy because it has shortCode.

But admin queries may not.

Example:

```text
show all URLs created by user 42
```

If data is sharded by shortCode:

```text
user 42's URLs may live on many shards
```

Query requires:

```text
scan all shards
merge results
```

ASCII:

```text
Admin query user 42

App
 |
 +-- query shard 0
 +-- query shard 1
 +-- query shard 2
 +-- query shard 3
 |
 v
merge/sort/page
```

This is called scatter-gather.

For hot redirect path:

```text
avoid scatter-gather
```

For admin/reporting:

```text
maybe acceptable
or use separate index table/search store
```

Possible solutions:

```text
user_url_index table by userId
Elasticsearch/OpenSearch
analytics store
CQRS read model
```

Senior rule:

```text
Choose shard key for hottest query path.
```

For URL shortener, hottest path is redirect by shortCode.

---

## 20. Resharding Problem

Resharding means changing shard layout.

Example:

```text
from 4 shards to 8 shards
```

With simple modulo:

```text
hash(key) % 4
```

changes to:

```text
hash(key) % 8
```

Many keys move.

ASCII:

```text
Before:
abc123 -> shard 2

After:
abc123 -> shard 6
```

But row is still on shard 2 unless migrated.

If router changes before data moves:

```text
GET /abc123 searches shard 6
row is on shard 2
returns 404
```

This is dangerous.

Resharding needs:

```text
migration plan
dual routing
metadata
backfill
cutover
verification
rollback
```

This is why production sharding often uses:

```text
consistent hashing
virtual shards
shard metadata table
```

Learning rule:

```text
Modulo sharding is easy to explain but painful to reshard.
```

---

## 21. Shard Metadata

Shard metadata describes where data ranges/buckets live.

Example:

```text
virtual_bucket 0 -> physical shard A
virtual_bucket 1 -> physical shard A
virtual_bucket 2 -> physical shard B
...
```

Flow:

```text
shortCode -> hash -> virtual bucket -> physical shard
```

ASCII:

```text
shortCode
   |
   v
hash
   |
   v
virtual bucket 57
   |
   v
metadata says bucket 57 -> shard 3
```

Why use virtual buckets?

```text
easier movement
move bucket 57 from shard 3 to shard 5
do not remap everything
```

This is more production-shaped than direct `% physicalShardCount`.

Simple learning:

```text
hash % N
```

Production:

```text
hash -> virtual bucket -> metadata -> shard
```

---

## 22. Java/Spring Boot Implementation Sketch

### Shard Router

```java
package com.miniurl.shortener.sharding;

import org.springframework.stereotype.Component;

@Component
public class ShortCodeShardRouter {

    private final int shardCount = 4;

    public int shardFor(String shortCode) {
        int hash = stableHash(shortCode);
        return Math.floorMod(hash, shardCount);
    }

    private int stableHash(String value) {
        int hash = 0;

        for (int i = 0; i < value.length(); i++) {
            hash = 31 * hash + value.charAt(i);
        }

        return hash;
    }
}
```

Important:

```text
Use stable hash.
Do not use logic that changes across deployments.
```

### Shard DataSource Registry

```java
package com.miniurl.shortener.sharding;

import javax.sql.DataSource;
import java.util.Map;

public class ShardDataSourceRegistry {

    private final Map<Integer, DataSource> dataSources;

    public ShardDataSourceRegistry(Map<Integer, DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    public DataSource getDataSource(int shardId) {
        DataSource dataSource = dataSources.get(shardId);

        if (dataSource == null) {
            throw new IllegalStateException("No datasource for shard " + shardId);
        }

        return dataSource;
    }
}
```

### Repository Pattern Idea

For learning, show concept:

```java
public Optional<RedirectLookup> findByShortCode(String shortCode) {
    int shardId = shardRouter.shardFor(shortCode);
    JdbcTemplate jdbcTemplate = jdbcTemplateFor(shardId);

    return jdbcTemplate.query(
            """
            SELECT long_url, status, expires_at
            FROM short_urls
            WHERE short_code = ?
            """,
            rs -> {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new RedirectLookup(
                        rs.getString("long_url"),
                        rs.getString("status"),
                        rs.getTimestamp("expires_at") == null
                                ? null
                                : rs.getTimestamp("expires_at").toInstant(),
                        shardId
                ));
            },
            shortCode
    );
}
```

Production note:

```text
Spring Data JPA multi-shard routing is possible but more complex.
For sharding demos, JdbcTemplate often makes routing clearer.
```

---

## 23. Step-by-Step Dry Runs

### Dry Run 1: Create Random ShortCode

Generated:

```text
shortCode = abc123
```

Shard count:

```text
4
```

Router:

```text
hash(abc123) % 4 = 2
```

Flow:

```text
1. Create request validated.
2. Service generates abc123.
3. Router chooses shard 2.
4. Insert into shard 2.
5. Unique index on shard 2 passes.
6. Add Redis cache/Bloom.
7. Return 201.
```

ASCII:

```text
abc123 -> shard 2 -> INSERT
```

---

### Dry Run 2: Redirect Cache Miss

Request:

```http
GET /abc123
```

Redis:

```text
miss
```

Router:

```text
abc123 -> shard 2
```

Flow:

```text
1. Service checks Redis.
2. Redis miss.
3. Router chooses shard 2.
4. Query shard 2 only.
5. Row found.
6. Fill Redis.
7. Return 302.
```

ASCII:

```text
GET /abc123
   |
   v
Redis miss
   |
   v
shard 2
   |
   v
302
```

---

### Dry Run 3: Duplicate Custom Alias

Two requests:

```text
customAlias = sale2026
```

Router:

```text
hash(sale2026) % 4 = 1
```

Both route to shard 1.

Flow:

```text
Request A inserts into shard 1 -> success.
Request B inserts into shard 1 -> unique violation.
```

Result:

```text
409 ALIAS_ALREADY_EXISTS
```

---

### Dry Run 4: Wrong Shard Routing Bug

Original router:

```text
hash(abc123) % 4 = 2
```

New buggy router:

```text
different hash -> shard 0
```

Flow:

```text
GET /abc123
   |
   v
router chooses shard 0
   |
   v
row not found
   |
   v
404
```

But row exists on shard 2.

Lesson:

```text
Shard routing logic must be stable and tested.
```

---

### Dry Run 5: Resharding From 4 To 8

Before:

```text
abc123 -> hash % 4 -> shard 2
```

After:

```text
abc123 -> hash % 8 -> shard 6
```

If data not moved:

```text
router searches shard 6
row lives shard 2
404
```

Correct migration needs:

```text
metadata
dual read
backfill
cutover
verification
```

---

## 24. Internal Execution Walkthrough

Redirect DB fallback with sharding:

```text
1. Client requests /abc123.
2. Controller extracts shortCode.
3. Service validates format.
4. Bloom filter may check existence.
5. Redis cache checked.
6. Redis miss.
7. ShardRouter computes shardId.
8. Repository gets DataSource/JdbcTemplate for shardId.
9. SQL runs on selected PostgreSQL.
10. Row returned or not found.
11. Service validates status/expiry.
12. Service fills Redis.
13. Controller returns response.
```

ASCII:

```text
Controller
   |
   v
Service
   |
   v
Redis miss
   |
   v
ShardRouter(shortCode)
   |
   v
DataSource for shard
   |
   v
SQL on one shard
```

Important:

```text
Sharding should be invisible to controller.
Service/repository infrastructure handles it.
```

---

## 25. Testing Strategy

### Unit Tests For Router

```text
same shortCode always same shard
different shortCodes distribute reasonably
shard ID is in valid range
hash handles negative values
```

Example:

```java
assertThat(router.shardFor("abc123"))
        .isEqualTo(router.shardFor("abc123"));
```

### Integration Tests

Use multiple test PostgreSQL containers or schemas:

```text
1. Insert abc123 using router.
2. Verify row exists only in expected shard.
3. Redirect lookup finds it.
4. Wrong shard does not contain it.
```

### Duplicate Test

```text
custom alias routes to same shard
duplicate insert fails with unique violation
```

### Resharding Safety Test

If router version changes:

```text
test known shortCode -> expected shard mapping
```

Golden test:

```text
routing snapshot test
```

Example table:

```text
abc123 -> shard 2
sale2026 -> shard 1
mohamed -> shard 3
```

If these change unexpectedly:

```text
test fails
```

---

## 26. Metrics And Observability

Metrics per shard:

```text
shard.request.count
shard.db.lookup.count
shard.db.query.latency
shard.db.error.count
shard.hikari.active
shard.hikari.pending
shard.cache.miss.count
shard.redirect.not_found
```

Distribution metrics:

```text
rows per shard
writes per shard
reads per shard
cache misses per shard
DB CPU per shard
storage per shard
```

ASCII:

```text
Shard dashboard:

Shard 0: reads #### writes ## storage ####
Shard 1: reads ######## writes ### storage #####
Shard 2: reads ### writes ## storage ####
Shard 3: reads #### writes ## storage ####
```

Alert signs:

```text
one shard has much higher CPU
one shard has much higher p99
one shard has high storage
one shard has high Hikari pending
cache miss spike isolated to one shard
```

Golden rule:

```text
After sharding, every DB metric needs shardId dimension.
```

---

## 27. Production Failure Stories

### Failure Story 1: Router Changed And Valid Links Returned 404

Developer changed hash function.

Existing rows stayed on old shards.

New router searched different shards.

Root cause:

```text
unstable shard routing
```

Fix:

```text
stable hash
routing snapshot tests
router versioning
migration plan
```

Lesson:

```text
Shard routing is data placement contract.
```

---

### Failure Story 2: Added Shard With Modulo And Broke Lookups

System changed:

```text
hash % 4
```

to:

```text
hash % 5
```

Most keys remapped.

Rows were not migrated.

Root cause:

```text
naive modulo resharding
```

Fix:

```text
consistent hashing or virtual bucket metadata
planned migration
dual-read during migration
```

Lesson:

```text
Adding shards is not just changing a number.
```

---

### Failure Story 3: Admin Query Became Scatter-Gather

Admin wanted all links for user 42.

Data was sharded by shortCode.

The query had to hit all shards.

Root cause:

```text
shard key optimized redirect path, not user listing
```

Fix:

```text
separate user index/read model
async projection
search store
```

Lesson:

```text
Shard key optimizes one access pattern and complicates others.
```

---

### Failure Story 4: Hot Shard From Bad Key Distribution

Short codes were sequential and range-sharded.

New writes all went to latest range.

One shard overloaded.

Root cause:

```text
range sharding with monotonically increasing key
```

Fix:

```text
hash-based sharding
randomized codes
virtual shards
```

Lesson:

```text
Shard key distribution matters.
```

---

### Failure Story 5: Local Unique Constraint Misunderstood

Team thought local unique index on each shard gives global uniqueness automatically.

Router bug sent same shortCode to two shards.

Duplicates happened.

Root cause:

```text
global uniqueness depends on deterministic routing
```

Fix:

```text
stable routing and tests
or global reservation service if needed
```

Lesson:

```text
Local unique is global only if same shortCode always routes to same shard.
```

---

## 28. Debugging Mindset

When sharded lookup fails, ask:

```text
What shortCode?
What shard should router choose?
Which shard actually has the row?
Did router version change?
Did shard count change?
Is Redis returning stale value?
Did create and redirect use same routing?
Is there a migration in progress?
Is one shard down?
Is the issue cache hit or DB fallback?
```

Debug commands:

```sql
-- run on expected shard
SELECT id, short_code, long_url, status
FROM short_urls
WHERE short_code = 'abc123';
```

Debug map:

```text
404 but row exists:
    wrong shard routing
    stale metadata
    resharding issue
    Bloom false negative

Duplicate code:
    routing inconsistency
    missing unique index on shard

One shard overloaded:
    hot shard
    cache miss spike
    bad distribution

Admin query slow:
    scatter-gather
    missing read model
```

Golden rule:

```text
Every sharding bug starts with: which shard did this key route to?
```

---

## 29. Common Mistakes

### Mistake 1: Sharding Too Early

Wrong:

```text
start with sharding before one DB is proven insufficient
```

Correct:

```text
use single DB + cache + replicas first
```

### Mistake 2: Bad Shard Key

Wrong:

```text
shard redirect data by ownerId when redirect only has shortCode
```

Correct:

```text
shard by shortCode for redirect hot path
```

### Mistake 3: Querying All Shards For Redirect

Wrong:

```text
search every shard for shortCode
```

Correct:

```text
route directly to one shard
```

### Mistake 4: Changing Modulo Count Casually

Wrong:

```text
hash % 4 -> hash % 8 without migration
```

Correct:

```text
use metadata/consistent hashing and migrate safely
```

### Mistake 5: Ignoring Cross-Shard Queries

Wrong:

```text
all queries remain easy after sharding
```

Correct:

```text
admin/reporting may need read models
```

### Mistake 6: No Per-Shard Metrics

Wrong:

```text
only total DB latency
```

Correct:

```text
measure by shardId
```

### Mistake 7: Unstable Hash Function

Wrong:

```text
routing changes across language/runtime/version
```

Correct:

```text
stable tested hash
```

### Mistake 8: Assuming Local Unique Means Global Without Routing

Wrong:

```text
unique per shard always equals global unique
```

Correct:

```text
only true if same shortCode always routes to same shard
```

---

## 30. Interview-Ready Explanation

If interviewer asks:

```text
How would you shard a URL shortener?
```

Strong answer:

```text
The hottest lookup in a URL shortener is redirect by shortCode, so I would shard by shortCode.
That way the request already contains the shard key, and a redirect cache miss can route to
exactly one database shard instead of scatter-gathering across all shards. A simple approach is
hash(shortCode) modulo shard count, with a local UNIQUE(short_code) index on each shard. Because
the same shortCode always routes to the same shard, local uniqueness effectively protects global
uniqueness. Redis still sits above the shards, so most redirects do not hit the database. The
main tradeoffs are resharding complexity, hot shard detection, and cross-shard queries like
listing all URLs by user. For production, I would avoid naive modulo changes during resharding
and prefer consistent hashing or virtual bucket metadata. I would track metrics per shard:
reads, writes, cache misses, DB latency, storage, Hikari pending, and errors.
```

Senior version:

```text
Shard by the key used by the hottest query. For a URL shortener, that is shortCode, because the
redirect path must route to one shard deterministically and never scan all shards.
```

Why this is strong:

```text
1. Chooses shard key from access pattern.
2. Explains one-shard redirect lookup.
3. Mentions local unique index.
4. Explains global uniqueness condition.
5. Keeps Redis above shards.
6. Mentions resharding tradeoff.
7. Mentions cross-shard query problem.
8. Mentions per-shard observability.
```

---

## 31. Senior Engineer Checklist

Before calling sharding production-shaped, confirm:

```text
[ ] Single DB limits justify sharding
[ ] shortCode is chosen as shard key
[ ] redirect lookup routes to one shard
[ ] create and redirect use same router
[ ] hash function is stable
[ ] routing snapshot tests exist
[ ] each shard has UNIQUE(short_code)
[ ] Redis cache sits above shards
[ ] Bloom/cache logic does not bypass routing correctness
[ ] per-shard metrics exist
[ ] shard failure behavior is defined
[ ] resharding strategy exists
[ ] modulo shard-count change is not done casually
[ ] cross-shard admin queries have a plan
[ ] hot shard detection exists
[ ] shard metadata/virtual buckets considered for production
```

---

## 32. One-Page Cheat Sheet

```text
Core mental model:
shard key decides where row lives

URL shortener shard key:
shortCode

Why:
redirect request already has shortCode
cache miss can hit exactly one shard
no scatter-gather for hot path

Simple routing:
shard = hash(shortCode) % shardCount

Better production:
hash -> virtual bucket -> shard metadata
or consistent hashing

Each shard:
same short_urls schema
local UNIQUE(short_code)
local index on short_code

Global uniqueness:
local unique is enough only if routing is deterministic

Redis:
sits above shards
hit -> no shard access
miss -> route to shard

Problems:
resharding
hot shard
cross-shard admin queries
router version changes
per-shard observability

Do not:
query all shards for redirect
change shard count without migration
use unstable hash
shard by ownerId for redirect path
```

---

## 33. One Picture To Remember

```text
                SHARDING BY SHORTCODE MENTAL MODEL

                    "shortCode chooses the database"

GET /abc123
    |
    v
+----------------------+
| Redis Cache          |
+------+---------------+
       |
       +-- hit -> 302
       |
       +-- miss
              |
              v
+----------------------+
| Shard Router         |
| hash(abc123) % N     |
+----------+-----------+
           |
           v
        shard 2
           |
           v
+----------------------+
| PostgreSQL Shard 2   |
| short_urls subset    |
| UNIQUE(short_code)   |
+----------+-----------+
           |
           v
      row found / not found
           |
           v
      Redis fill + response


CREATE /api/v1/urls
    |
    v
generate shortCode
    |
    v
same router
    |
    v
insert into selected shard


FINAL MEMORY:

The redirect request gives the shard key.
The router picks one shard.
The shard index finds the row.
Redis hides most reads.
Resharding is the hard part.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Sharding splits one huge URL table across multiple database shards.
2. `shortCode` is the natural shard key because redirect requests already contain it.
3. On Redis miss, the router computes the shard from `shortCode` and queries exactly one shard.
4. Local `UNIQUE(short_code)` gives global uniqueness only if routing is deterministic.
5. The hardest parts are resharding, hot shards, cross-shard queries, stable routing, and per-shard observability.
```

Next chapter:

```text
030_Read_Replicas_For_Redirect.md
```
