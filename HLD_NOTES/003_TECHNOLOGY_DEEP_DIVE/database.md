# Database Design for System Design Interviews — Ultimate Reference

> A practical, end-to-end guide to database design from **first principles** to **high-scale production architecture**.  
> Covers **SQL vs NoSQL**, **schema design**, **normalization**, **indexing**, **partitioning**, **sharding**, **replication**, **consistency**, **transactions**, **query optimization**, and **Spring Boot examples** for both RDBMS and NoSQL.

---

## Table of Contents

1. [How to think about database design](#1-how-to-think-about-database-design)
2. [SQL vs NoSQL](#2-sql-vs-nosql)
3. [Schema design principles](#3-schema-design-principles)
4. [Normalization vs denormalization](#4-normalization-vs-denormalization)
5. [Indexing strategies](#5-indexing-strategies)
6. [Partitioning in RDBMS](#6-partitioning-in-rdbms)
7. [Sharding in RDBMS and NoSQL](#7-sharding-in-rdbms-and-nosql)
8. [Replication patterns](#8-replication-patterns)
9. [Consistency models](#9-consistency-models)
10. [Transactions and distributed transactions](#10-transactions-and-distributed-transactions)
11. [Query optimization](#11-query-optimization)
12. [Real design examples](#12-real-design-examples)
13. [Spring Boot + PostgreSQL step by step](#13-spring-boot--postgresql-step-by-step)
14. [Spring Boot + MongoDB step by step](#14-spring-boot--mongodb-step-by-step)
15. [Spring Boot patterns for partitioning and sharding](#15-spring-boot-patterns-for-partitioning-and-sharding)
16. [Interview answer templates](#16-interview-answer-templates)
17. [Final cheat sheets](#17-final-cheat-sheets)

---

# 1. How to think about database design

Database design is not about picking a trendy database. It is about matching your **data model**, **access patterns**, and **scale requirements**.

When an interviewer asks, _“How would you store this data?”_, they are really asking:

- What does the data look like?
- What queries matter most?
- What consistency guarantees are required?
- What is the expected scale?
- What can be eventually consistent, and what cannot?

## 1.1 Start with these questions

Before choosing any database, ask:

### Workload
- Read-heavy or write-heavy?
- Random lookups or range scans?
- OLTP or analytics?
- Hot key risk?

### Data shape
- Strong relationships between entities?
- Nested JSON documents?
- Time-series?
- Graph-like relationships?

### Consistency
- Is stale data acceptable?
- Are transactions required across multiple rows/tables?
- Do we need strict inventory or money correctness?

### Scale
- Number of users?
- QPS / TPS?
- Total data size?
- Growth rate?

### Operations
- Backups?
- Multi-region?
- Failover?
- Team familiarity?

---

## 1.2 The practical workflow

A good design process:

```text
Requirements
   ↓
Entities + relationships
   ↓
Choose SQL / NoSQL / hybrid
   ↓
Design schema
   ↓
Define indexes for main queries
   ↓
Plan partitioning / sharding
   ↓
Plan replication + failover
   ↓
Think about consistency + transactions
   ↓
Validate with example queries
```

---

# 2. SQL vs NoSQL

This is usually the first major decision.

## 2.1 SQL (Relational databases)

Examples:
- PostgreSQL
- MySQL
- MariaDB
- Oracle
- SQL Server

Relational databases store data in tables and model relationships explicitly.

### Example

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

### Strengths
- Strong ACID guarantees
- Complex joins and queries
- Referential integrity
- Mature indexing
- Easy ad-hoc analytics with SQL
- Great for transactional systems

### Weaknesses
- Scaling writes horizontally is harder
- Schema migrations need care
- Join-heavy workloads can get expensive at scale
- Cross-region multi-primary is complex

---

## 2.2 NoSQL

NoSQL is not one thing. It is a family of data models.

### Main categories

| Type | Examples | Best for |
|---|---|---|
| Document | MongoDB, Couchbase | flexible JSON-like records |
| Key-Value | Redis, DynamoDB | simple fast lookups |
| Wide-column | Cassandra, HBase | huge write throughput, time-series |
| Graph | Neo4j, Neptune | relationships, traversals |

---

## 2.3 Document database example

```json
{
  "_id": "user_123",
  "name": "Alice",
  "email": "alice@example.com",
  "addresses": [
    {
      "city": "New York",
      "zip": "10001"
    }
  ]
}
```

### Good for
- flexible schemas
- nested data
- entity-centric reads
- evolving product catalogs
- content management

### Weakness
- joins are limited or absent
- cross-document transactions may be weaker or more expensive
- ad-hoc relational querying is weaker than SQL

---

## 2.4 Key-value example

```text
key   = session:abc123
value = {"userId": 42, "expiresAt": 1710000000}
```

### Good for
- cache
- sessions
- feature flags
- counters
- super fast lookups

### Weakness
- query options are limited
- not suitable for rich relational data

---

## 2.5 Wide-column example

```text
row key: user_123
columns:
  profile:name = Alice
  profile:email = alice@example.com
  order:1001 = {...}
  order:1002 = {...}
```

### Good for
- append-heavy workloads
- large scale event/time-series
- write-heavy analytics
- large distributed clusters

### Weakness
- harder modeling
- joins are absent
- query flexibility is reduced

---

## 2.6 Graph database example

```text
(Alice)-[:FOLLOWS]->(Bob)
(Bob)-[:LIKES]->(Post1)
```

### Good for
- recommendations
- fraud relationships
- social networks
- shortest path / traversal queries

### Weakness
- not ideal for generic OLTP
- specialized operational model

---

## 2.7 Decision framework

| Requirement | Prefer SQL | Prefer NoSQL |
|---|---|---|
| Strong transactions | ✅ | maybe |
| Rich joins | ✅ | ❌ |
| Flexible schema | limited | ✅ |
| Massive write scale | harder | often easier |
| Known access patterns only | maybe | ✅ |
| Ad-hoc analytics | ✅ | weaker |
| Time-series/event ingestion | possible | wide-column often better |

---

## 2.8 Hybrid is normal

Real systems often use multiple stores:

| Use case | Database |
|---|---|
| orders, payments | PostgreSQL |
| session cache | Redis |
| product catalog | MongoDB |
| feed events | Cassandra |
| search | Elasticsearch |
| graph recommendations | Neo4j |

### Interview rule
Do not force one database to solve every problem.

---

# 3. Schema design principles

Good schema design makes queries efficient, updates safe, and scaling easier later.

## 3.1 Start with entities

Example e-commerce system:

```text
User (1) ----< (N) Order
Order (1) ----< (N) OrderItem
Product (1) ----< (N) OrderItem
```

This gives:

- `users`
- `products`
- `orders`
- `order_items`

---

## 3.2 Choose primary keys carefully

### Option A: auto-increment

```sql
id BIGSERIAL PRIMARY KEY
```

Pros:
- compact
- sequential
- index-friendly

Cons:
- hard in distributed systems
- predictable IDs

### Option B: UUID

```sql
id UUID PRIMARY KEY DEFAULT gen_random_uuid()
```

Pros:
- globally unique
- easy in distributed systems

Cons:
- larger index
- random inserts fragment B-tree more

### Option C: ULID / Snowflake

Time-sortable, globally unique.

Pros:
- scalable
- better insertion locality than random UUID
- good for distributed systems

### Recommendation
For single-node/simple systems: `BIGSERIAL` is fine.  
For distributed/high-scale systems: prefer **ULID**, **UUIDv7**, or **Snowflake-style IDs**.

---

## 3.3 Add foreign keys where appropriate

```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    total_amount DECIMAL(10,2) NOT NULL
);
```

Pros:
- prevents invalid references
- better data integrity

Cons:
- can complicate extreme-scale sharding
- some high-scale systems enforce integrity in app layer instead

### Rule
Use foreign keys by default in relational systems unless you have a clear reason not to.

---

## 3.4 Use correct data types

| Data | Recommended Type | Why |
|---|---|---|
| money | DECIMAL(10,2) | exact precision |
| timestamps | TIMESTAMPTZ | timezone-safe |
| ids | BIGINT / UUID / ULID | scalable identity |
| JSON payload | JSONB | flexible + indexable |
| flags | BOOLEAN | clear semantics |

### Example

```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    metadata JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

---

## 3.5 Constraints matter

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    age INT CHECK (age >= 0 AND age < 150),
    status VARCHAR(20) NOT NULL CHECK (status IN ('active', 'inactive', 'banned'))
);
```

Use:
- `NOT NULL`
- `UNIQUE`
- `CHECK`
- `FOREIGN KEY`

These stop bad data early.

---

# 4. Normalization vs denormalization

## 4.1 Normalization

Normalization means storing each fact once.

### Bad unnormalized table

| order_id | user_name | user_email | product_name | product_price |
|---|---|---|---|---|
| 1 | Alice | alice@example.com | Laptop | 999 |
| 2 | Alice | alice@example.com | Mouse | 49 |

Problems:
- duplicated data
- hard updates
- inconsistency risk

### Normalized version

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(255) UNIQUE
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    current_price DECIMAL(10,2)
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    product_id BIGINT REFERENCES products(id),
    quantity INT NOT NULL
);
```

### Pros
- less duplication
- easier updates
- strong consistency

### Cons
- more joins
- slower reads for some hot paths

---

## 4.2 Denormalization

Denormalization duplicates some data to make reads cheaper.

### Example

Store product name and price in `order_items`:

```sql
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL
);
```

Why?
- order history should show product price at purchase time
- avoids join to `products` for every read
- product name changes later should not rewrite history

### Pros
- faster reads
- fewer joins
- more self-contained rows/documents

### Cons
- duplicated data
- more complex writes
- possible inconsistency if not handled carefully

---

## 4.3 Practical rule

| Situation | Recommendation |
|---|---|
| transactional core tables | normalize first |
| read-heavy hot paths | selectively denormalize |
| historical snapshots | denormalize intentionally |
| analytics views | denormalize aggressively |

---

# 5. Indexing strategies

Indexes are the #1 performance lever.

## 5.1 Why indexes matter

Without index:

```sql
SELECT * FROM users WHERE email = 'alice@example.com';
```

Database does full table scan.

With index:

```sql
CREATE INDEX idx_users_email ON users(email);
```

Database can directly find the row.

---

## 5.2 Basic indexes

### Single-column index

```sql
CREATE INDEX idx_orders_user_id ON orders(user_id);
```

### Unique index

```sql
CREATE UNIQUE INDEX idx_users_email_unique ON users(email);
```

### Composite index

```sql
CREATE INDEX idx_orders_user_status_created
ON orders(user_id, status, created_at DESC);
```

### Partial index

```sql
CREATE INDEX idx_orders_pending
ON orders(created_at)
WHERE status = 'pending';
```

---

## 5.3 Composite index order matters

Index on:

```sql
(user_id, status, created_at)
```

Helps queries like:

```sql
SELECT * FROM orders
WHERE user_id = 123;

SELECT * FROM orders
WHERE user_id = 123 AND status = 'pending';

SELECT * FROM orders
WHERE user_id = 123 AND status = 'pending'
ORDER BY created_at DESC;
```

Does **not** help much for:

```sql
SELECT * FROM orders WHERE status = 'pending';
```

because `user_id` is the leftmost column.

### Rule
Put:
1. equality filters first
2. then range filters
3. then sort columns

---

## 5.4 Covering index

If query needs only indexed columns, DB may avoid table access.

```sql
CREATE INDEX idx_orders_covering
ON orders(user_id, status, created_at, total_amount);
```

Query:

```sql
SELECT user_id, status, created_at, total_amount
FROM orders
WHERE user_id = 123;
```

---

## 5.5 JSONB indexes in PostgreSQL

```sql
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    payload JSONB
);

CREATE INDEX idx_events_payload_gin
ON events USING GIN (payload);
```

Query:

```sql
SELECT * FROM events
WHERE payload @> '{"eventType": "click"}';
```

---

## 5.6 Step-by-step indexing example

### Step 1: create table

```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

### Step 2: slow query

```sql
SELECT * FROM orders
WHERE user_id = 42 AND status = 'pending'
ORDER BY created_at DESC
LIMIT 20;
```

### Step 3: create matching index

```sql
CREATE INDEX idx_orders_user_status_created
ON orders(user_id, status, created_at DESC);
```

### Step 4: verify plan

```sql
EXPLAIN ANALYZE
SELECT * FROM orders
WHERE user_id = 42 AND status = 'pending'
ORDER BY created_at DESC
LIMIT 20;
```

---

# 6. Partitioning in RDBMS

Partitioning splits a large table into smaller physical pieces inside one database.

## 6.1 Why partition

Use partitioning when:
- table is huge
- queries naturally filter by partition key
- you need easier archival/deletion
- maintenance on one chunk should be isolated

Good partition keys:
- time
- region
- tenant
- status (sometimes)

Bad partition keys:
- very low cardinality values
- values not used in query filters

---

## 6.2 Range partitioning step by step

### Parent table

```sql
CREATE TABLE orders (
    id BIGSERIAL,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
) PARTITION BY RANGE (created_at);
```

### Child partitions

```sql
CREATE TABLE orders_2024_01 PARTITION OF orders
FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE orders_2024_02 PARTITION OF orders
FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');

CREATE TABLE orders_2024_03 PARTITION OF orders
FOR VALUES FROM ('2024-03-01') TO ('2024-04-01');
```

### Insert data

```sql
INSERT INTO orders (user_id, total_amount, created_at)
VALUES
(1, 99.99, '2024-01-15'),
(2, 49.99, '2024-02-10');
```

Rows go to correct child partition automatically.

### Query with partition pruning

```sql
EXPLAIN ANALYZE
SELECT * FROM orders
WHERE created_at >= '2024-02-01'
  AND created_at < '2024-03-01';
```

Only the February partition is scanned.

---

## 6.3 List partitioning

```sql
CREATE TABLE customers (
    id BIGSERIAL,
    region VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL
) PARTITION BY LIST (region);

CREATE TABLE customers_us PARTITION OF customers
FOR VALUES IN ('US');

CREATE TABLE customers_eu PARTITION OF customers
FOR VALUES IN ('EU');

CREATE TABLE customers_asia PARTITION OF customers
FOR VALUES IN ('ASIA');
```

Useful when region-based separation matters.

---

## 6.4 Hash partitioning

```sql
CREATE TABLE sessions (
    id BIGSERIAL,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL
) PARTITION BY HASH (user_id);

CREATE TABLE sessions_p0 PARTITION OF sessions
FOR VALUES WITH (MODULUS 4, REMAINDER 0);

CREATE TABLE sessions_p1 PARTITION OF sessions
FOR VALUES WITH (MODULUS 4, REMAINDER 1);

CREATE TABLE sessions_p2 PARTITION OF sessions
FOR VALUES WITH (MODULUS 4, REMAINDER 2);

CREATE TABLE sessions_p3 PARTITION OF sessions
FOR VALUES WITH (MODULUS 4, REMAINDER 3);
```

Hash partitioning is good for even distribution.

---

## 6.5 Partition maintenance

### Drop old partitions fast

```sql
DROP TABLE orders_2023_01;
```

Much faster than deleting billions of old rows.

### Add new partition

```sql
CREATE TABLE orders_2024_04 PARTITION OF orders
FOR VALUES FROM ('2024-04-01') TO ('2024-05-01');
```

### Practical tip
Automate partition creation ahead of time.

---

## 6.6 Partitioning pitfalls

- Queries not filtering on partition key may still hit many partitions
- Too many partitions can hurt planning time
- Unique constraints can be harder across partitions
- Global indexes may behave differently by database

---

# 7. Sharding in RDBMS and NoSQL

Partitioning is inside one DB server.  
Sharding spreads data across multiple database servers.

## 7.1 Why shard

Use sharding when:
- one DB server cannot handle write throughput
- data size exceeds single-node limits
- tenant or geo isolation is needed

---

## 7.2 Sharding strategies

### A. Range-based sharding

```text
Shard 1: user_id 1 - 1,000,000
Shard 2: user_id 1,000,001 - 2,000,000
Shard 3: user_id 2,000,001 - 3,000,000
```

Pros:
- range queries easy
- intuitive

Cons:
- hot shards if traffic skewed
- hard rebalancing

---

### B. Hash-based sharding

```text
shard = hash(user_id) % N
```

Pros:
- even distribution
- simple routing

Cons:
- range queries hard
- re-sharding is painful unless consistent hashing is used

---

### C. Directory-based sharding

Maintain mapping table:

```text
user_id 123 -> shard_2
user_id 456 -> shard_1
```

Pros:
- flexible rebalancing
- custom placement

Cons:
- mapping service can become bottleneck
- more moving parts

---

## 7.3 RDBMS sharding example

Suppose we shard `orders` by `user_id`.

### Shard databases
- `orders_db_0`
- `orders_db_1`
- `orders_db_2`
- `orders_db_3`

### Routing rule

```java
int shard = Math.abs(Long.hashCode(userId)) % 4;
```

### Query
If `user_id=42`, compute shard and query only that DB.

---

## 7.4 NoSQL sharding

Many NoSQL systems do it for you.

### MongoDB sharding

Shard key example:
```javascript
sh.shardCollection("shop.orders", { "userId": "hashed" })
```

This automatically distributes documents across shards.

### Cassandra sharding concept
Partition key determines node placement.

```sql
CREATE TABLE user_events (
    user_id UUID,
    event_time TIMESTAMP,
    event_type TEXT,
    PRIMARY KEY ((user_id), event_time)
) WITH CLUSTERING ORDER BY (event_time DESC);
```

`user_id` is the partition key.

---

## 7.5 Cross-shard query problem

Query not using shard key:

```sql
SELECT * FROM orders WHERE status = 'pending';
```

This requires:
- scatter query to all shards
- merge results

This is expensive.

### Solutions
- design queries around shard key
- maintain secondary read models
- use search/analytics store
- denormalize frequently accessed views

---

## 7.6 Sharding best practices

- choose shard key carefully
- avoid hot keys
- keep related data together
- plan re-sharding early
- monitor shard imbalance
- minimize cross-shard joins/transactions

---

# 8. Replication patterns

Replication copies data for scalability and fault tolerance.

## 8.1 Single leader replication

```text
          Writes
            |
         Primary
        /   |   \
   Replica Replica Replica
```

### Pros
- simple mental model
- strong consistency on primary
- good read scaling

### Cons
- primary is write bottleneck
- replica lag possible

---

## 8.2 Sync vs async replication

### Synchronous
Write acknowledged only after replica confirms.

Pros:
- stronger durability
- no data loss on primary crash if synced

Cons:
- higher latency
- lower availability

### Asynchronous
Write acknowledged immediately, replicated later.

Pros:
- fast writes
- higher availability

Cons:
- replica lag
- possible data loss on crash

---

## 8.3 Multi-leader replication

```text
Region US <----> Region EU
   leader           leader
```

Useful for:
- multi-region low-latency writes

Problems:
- conflict resolution
- write ordering complexity

---

## 8.4 Leaderless replication

Used by Cassandra/DynamoDB style systems.

Write to `W` replicas, read from `R` replicas.

If:

```text
W + R > N
```

then reads overlap writes.

Example:
- `N=3`
- `W=2`
- `R=2`

Strong-ish quorum behavior.

---

# 9. Consistency models

## 9.1 Strong consistency

Every read sees latest committed write.

Best for:
- banking
- inventory
- critical state

Trade-off:
- slower
- less available under partition

---

## 9.2 Eventual consistency

Reads may be stale briefly, but replicas converge.

Best for:
- feeds
- analytics
- recommendations

Trade-off:
- simpler scale
- stale reads possible

---

## 9.3 Read-your-writes

User sees their own updates immediately.

Useful for:
- shopping cart
- profile update
- drafts

Implementation:
- sticky session
- primary read after write
- version/session-aware reads

---

## 9.4 Causal consistency

If B depends on A, everyone sees A before B.

Useful for:
- comments/replies
- collaborative systems

---

# 10. Transactions and distributed transactions

## 10.1 Local transaction

```sql
BEGIN;

UPDATE accounts SET balance = balance - 100 WHERE id = 1;
UPDATE accounts SET balance = balance + 100 WHERE id = 2;

COMMIT;
```

Good when all data is in one DB.

---

## 10.2 Isolation levels

| Level | Dirty Read | Non-repeatable Read | Phantom Read |
|---|---|---|---|
| Read Uncommitted | yes | yes | yes |
| Read Committed | no | yes | yes |
| Repeatable Read | no | no | yes |
| Serializable | no | no | no |

Most systems use:
- `READ COMMITTED`
- `REPEATABLE READ`

---

## 10.3 Distributed transactions

If data spans services/DBs:
- 2PC
- Saga
- Outbox
- TCC

### Practical guidance
For microservices, prefer:
- local transaction per service
- outbox for reliable event publishing
- saga for distributed workflow

---

# 11. Query optimization

## 11.1 Use EXPLAIN ANALYZE

```sql
EXPLAIN ANALYZE
SELECT * FROM orders
WHERE user_id = 42 AND status = 'pending';
```

Look for:
- Seq Scan on large tables
- huge rows removed by filter
- nested loops on large sets

---

## 11.2 Common fixes

### Add index
```sql
CREATE INDEX idx_orders_user_status
ON orders(user_id, status);
```

### Select fewer columns
```sql
SELECT id, total_amount, status
FROM orders
WHERE user_id = 42;
```

### Add LIMIT
```sql
SELECT * FROM orders
WHERE user_id = 42
ORDER BY created_at DESC
LIMIT 20;
```

### Avoid N+1
Use joins or batch loading.

---

## 11.3 Connection pooling

Use HikariCP in Spring Boot.

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

---

## 11.4 Caching

### Read-through pattern
```text
request -> cache -> DB if miss -> cache fill
```

### Redis example logic
- key: `user:42`
- TTL: 1 hour

Use for:
- profiles
- product details
- feed pages
- hot counts

---

# 12. Real design examples

## 12.1 Twitter/X style feed

### Tables

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE tweets (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_tweets_user_created
ON tweets(user_id, created_at DESC);

CREATE TABLE follows (
    follower_id BIGINT NOT NULL REFERENCES users(id),
    followee_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (follower_id, followee_id)
);
```

### Pull model query

```sql
SELECT t.*
FROM tweets t
JOIN follows f ON t.user_id = f.followee_id
WHERE f.follower_id = 123
ORDER BY t.created_at DESC
LIMIT 100;
```

### Push model
Precompute timelines.

```sql
CREATE TABLE timelines (
    user_id BIGINT NOT NULL,
    tweet_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (user_id, created_at, tweet_id)
);
```

### Design note
Use hybrid:
- push for normal users
- pull for celebrity users

---

## 12.2 E-commerce order system

### Schema

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    inventory_count INT NOT NULL CHECK (inventory_count >= 0)
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0)
);
```

### Inventory-safe update

```sql
UPDATE products
SET inventory_count = inventory_count - 1
WHERE id = 123 AND inventory_count > 0;
```

Check affected rows = 1.

---

## 12.3 Analytics/time-series system

### Partitioned table

```sql
CREATE TABLE events (
    id BIGSERIAL,
    event_type VARCHAR(50) NOT NULL,
    user_id BIGINT,
    properties JSONB,
    created_at TIMESTAMPTZ NOT NULL
) PARTITION BY RANGE (created_at);
```

### Monthly partition

```sql
CREATE TABLE events_2024_01 PARTITION OF events
FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');
```

### Aggregate table

```sql
CREATE TABLE event_counts_hourly (
    event_type VARCHAR(50) NOT NULL,
    hour_bucket TIMESTAMPTZ NOT NULL,
    count BIGINT NOT NULL,
    PRIMARY KEY (event_type, hour_bucket)
);
```

---

# 13. Spring Boot + PostgreSQL step by step

This is a simple but production-friendly starter.

## 13.1 Dependencies (Maven)

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

---

## 13.2 application.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/shopdb
    username: postgres
    password: postgres
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true

server:
  port: 8080
```

---

## 13.3 SQL schema

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    inventory_count INT NOT NULL CHECK (inventory_count >= 0),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0)
);

CREATE INDEX idx_orders_user_created
ON orders(user_id, created_at DESC);
```

---

## 13.4 JPA entities

### UserEntity.java

```java
package com.example.demo.user;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
}
```

### ProductEntity.java

```java
package com.example.demo.product;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "inventory_count", nullable = false)
    private Integer inventoryCount;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public Integer getInventoryCount() { return inventoryCount; }

    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setInventoryCount(Integer inventoryCount) { this.inventoryCount = inventoryCount; }
}
```

---

## 13.5 Repository

### ProductRepository.java

```java
package com.example.demo.product;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Modifying
    @Query("""
        update ProductEntity p
        set p.inventoryCount = p.inventoryCount - :quantity
        where p.id = :productId and p.inventoryCount >= :quantity
    """)
    int reserveInventory(@Param("productId") Long productId, @Param("quantity") int quantity);
}
```

---

## 13.6 Service with transaction

### OrderService.java

```java
package com.example.demo.order;

import com.example.demo.product.ProductEntity;
import com.example.demo.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderEntity createSimpleOrder(Long userId, Long productId, int quantity) {
        int updated = productRepository.reserveInventory(productId, quantity);
        if (updated == 0) {
            throw new IllegalStateException("Out of stock");
        }

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setStatus("CONFIRMED");
        order.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

        return orderRepository.save(order);
    }
}
```

This demonstrates:
- transaction boundary
- atomic inventory reservation
- order write

---

## 13.7 Controller

```java
package com.example.demo.order;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderEntity createOrder(@RequestParam Long userId,
                                   @RequestParam Long productId,
                                   @RequestParam int quantity) {
        return orderService.createSimpleOrder(userId, productId, quantity);
    }
}
```

---

# 14. Spring Boot + MongoDB step by step

Use MongoDB when document-oriented storage fits better.

## 14.1 Dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
</dependencies>
```

---

## 14.2 application.yml

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/catalogdb
```

---

## 14.3 Document model

### ProductDocument.java

```java
package com.example.demo.catalog;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Map;

@Document(collection = "products")
public class ProductDocument {

    @Id
    private String id;

    private String name;
    private BigDecimal price;
    private String category;
    private Map<String, Object> attributes;

    public String getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; }
    public Map<String, Object> getAttributes() { return attributes; }

    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
}
```

---

## 14.4 Repository

```java
package com.example.demo.catalog;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductMongoRepository extends MongoRepository<ProductDocument, String> {
    List<ProductDocument> findByCategory(String category);
}
```

---

## 14.5 Service + controller

```java
package com.example.demo.catalog;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {

    private final ProductMongoRepository repository;

    public CatalogService(ProductMongoRepository repository) {
        this.repository = repository;
    }

    public List<ProductDocument> listByCategory(String category) {
        return repository.findByCategory(category);
    }

    public ProductDocument create(ProductDocument doc) {
        return repository.save(doc);
    }
}
```

```java
package com.example.demo.catalog;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalog/products")
public class CatalogController {

    private final CatalogService service;

    public CatalogController(CatalogService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductDocument> byCategory(@RequestParam String category) {
        return service.listByCategory(category);
    }

    @PostMapping
    public ProductDocument create(@RequestBody ProductDocument doc) {
        return service.create(doc);
    }
}
```

---

# 15. Spring Boot patterns for partitioning and sharding

## 15.1 Partitioning with PostgreSQL

Partitioning is transparent to the app if done in DB.

Your Spring Boot app still uses the parent table.

Example entity maps to `orders`, while PostgreSQL routes to child partitions automatically.

No code change needed beyond:
- choosing partition key
- ensuring queries filter by it

### Practical app rule
If you partition by `created_at`, make sure app queries include date range.

Bad:

```java
orderRepository.findAll();
```

Good:

```java
orderRepository.findByCreatedAtBetween(start, end);
```

---

## 15.2 Manual sharding in Spring Boot

Here is a simple routing pattern for RDBMS sharding.

### Step 1: define shard routing rule

```java
package com.example.demo.sharding;

public final class ShardUtil {
    private ShardUtil() {}

    public static int shardForUserId(Long userId, int numberOfShards) {
        return Math.abs(Long.hashCode(userId)) % numberOfShards;
    }
}
```

---

### Step 2: configure multiple data sources

```java
package com.example.demo.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class ShardDataSourceConfig {

    @Bean
    public Map<Integer, DataSource> shardDataSources() {
        return Map.of(
            0, DataSourceBuilder.create()
                    .url("jdbc:postgresql://localhost:5432/orders_db_0")
                    .username("postgres").password("postgres").build(),
            1, DataSourceBuilder.create()
                    .url("jdbc:postgresql://localhost:5432/orders_db_1")
                    .username("postgres").password("postgres").build(),
            2, DataSourceBuilder.create()
                    .url("jdbc:postgresql://localhost:5432/orders_db_2")
                    .username("postgres").password("postgres").build(),
            3, DataSourceBuilder.create()
                    .url("jdbc:postgresql://localhost:5432/orders_db_3")
                    .username("postgres").password("postgres").build()
        );
    }
}
```

---

### Step 3: route query

```java
package com.example.demo.sharding;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;

@Service
public class ShardedOrderReader {

    private final Map<Integer, DataSource> shardDataSources;

    public ShardedOrderReader(Map<Integer, DataSource> shardDataSources) {
        this.shardDataSources = shardDataSources;
    }

    public int countOrdersByUser(Long userId) {
        int shard = ShardUtil.shardForUserId(userId, 4);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(shardDataSources.get(shard));

        Integer count = jdbcTemplate.queryForObject(
            "select count(*) from orders where user_id = ?",
            Integer.class,
            userId
        );

        return count == null ? 0 : count;
    }
}
```

This is the basic pattern:
- compute shard
- select datasource
- execute query only there

---

## 15.3 MongoDB sharding note

When MongoDB is sharded, the application usually talks to `mongos`, not individual shards.  
Routing is handled by MongoDB if your queries include the shard key.

Example:
- shard key: `userId`
- query:

```java
mongoTemplate.find(
    org.springframework.data.mongodb.core.query.Query.query(
        org.springframework.data.mongodb.core.query.Criteria.where("userId").is(userId)
    ),
    ProductDocument.class
);
```

If `userId` is the shard key, routing is efficient.

---

# 16. Interview answer templates

## 16.1 “SQL or NoSQL?”

```text
I’d choose based on the data model and access patterns.
If I need strong ACID transactions, joins, and relational integrity, I’d start with PostgreSQL.
If the schema is flexible and reads are mostly by whole document, MongoDB is a good fit.
In many real systems I’d use both: relational DB for transactions, Redis for caching, and possibly a document or search store for specific read patterns.
```

---

## 16.2 “How would you scale the database?”

```text
I’d first optimize schema and indexing.
Then I’d add read replicas for read-heavy traffic.
If the table becomes huge, I’d partition by time or tenant.
If one node still can’t handle writes, I’d shard by a high-cardinality key like user_id.
I’d be careful to choose a shard key that avoids hot shards and keeps the main queries shard-local.
```

---

## 16.3 “How do you design schema?”

```text
I’d start from entities and relationships, define primary keys, foreign keys, and constraints, then add indexes to support the main queries.
I’d normalize core transactional data first, and selectively denormalize read-heavy paths where joins become expensive.
```

---

# 17. Final cheat sheets

## 17.1 SQL vs NoSQL

| Need | Use |
|---|---|
| ACID transactions | SQL |
| joins and ad-hoc queries | SQL |
| flexible schema | document DB |
| massive write scale | wide-column / distributed DB |
| cache / sessions / counters | key-value |

---

## 17.2 Partitioning vs sharding

| Concept | Meaning |
|---|---|
| Partitioning | split large table inside one DB server |
| Sharding | split data across multiple DB servers |

---

## 17.3 Read scaling path

```text
Indexes
→ query optimization
→ connection pooling
→ cache
→ read replicas
→ denormalized read models
```

---

## 17.4 Write scaling path

```text
batching
→ async processing
→ partitioning
→ shard by high-cardinality key
→ specialized stores for heavy ingestion
```

---

## 17.5 What to say in interviews

```text
I’d start with the access patterns, not the technology.
Then I’d design the schema, add indexes for the critical queries, and choose the simplest database that meets current requirements.
After that I’d discuss how the design evolves under scale: partitioning, replication, sharding, consistency trade-offs, and operational concerns like failover and migrations.
```

---

# Closing note

The goal is not to memorize every database.  
The goal is to understand:

- what the workload needs
- what each database is good at
- which trade-offs you are making
- how the design evolves from simple to large scale

That is what interviewers are really testing.
