# MySQL System Design — Last-Minute Notes + Spring Boot Examples

A short, practical guide for interviews and implementation. Focus on **InnoDB**, **indexes**, **locking**, **replication**, **sharding**, and **Spring Boot patterns**.

---

## 1) When to choose MySQL

Choose MySQL when you have:

**SQL example**
```sql
-- Typical OLTP schema
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_orders_user_created (user_id, created_at),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Common relational query
SELECT o.id, o.total, o.created_at
FROM orders o
WHERE o.user_id = 42
ORDER BY o.created_at DESC
LIMIT 20;
```

**Example**
An e-commerce app fits MySQL well: relational schema, ACID order placement, and lots of catalog/profile reads that can move to replicas.


- **Read-heavy web apps**: easy read scaling with replicas
- **Relational data**: users → orders → items → products
- **ACID needs**: orders, payments, inventory, auth
- **Operational simplicity**: mature ecosystem, tooling, observability
- **Existing team expertise**: often more important than feature lists

Do **not** choose MySQL first when you need:

- very advanced SQL / analytics / JSON-heavy querying → **PostgreSQL**
- extreme write throughput → **Cassandra / ScyllaDB**
- built-in horizontal write scaling → **TiDB / CockroachDB / Vitess on MySQL**
- rich geospatial support → **PostGIS**

**Interview line:**  
> MySQL is strongest for structured, read-heavy systems where I want simple operations, ACID via InnoDB, and straightforward read scaling with replicas.

---

## 2) MySQL architecture you should say out loud

Typical production path:

**SQL example**
```sql
-- Write goes to primary
INSERT INTO orders (user_id, total) VALUES (42, 99.99);

-- Read may go to replica
SELECT id, total, created_at
FROM orders
WHERE user_id = 42
ORDER BY created_at DESC
LIMIT 10;
```

**Example**
App servers connect through ProxySQL. Writes are routed to primary, read-only traffic goes to replicas, and ProxySQL protects MySQL from connection storms.


`App → ProxySQL → MySQL Primary / Read Replicas`

Core pieces:

- **ProxySQL**: connection pooling, routing, read/write split
- **SQL Parser / Optimizer / Execution Engine**: plan + execute queries
- **InnoDB**: storage engine handling transactions, locks, MVCC
- **Buffer Pool**: RAM cache for data/index pages
- **Redo Log**: crash recovery + durable commits
- **Binlog**: replication stream to replicas

**Mental model**
- Writes go to **primary**
- Reads can go to **replicas**
- InnoDB uses **buffer pool + redo log + undo log**
- Replication uses the **binary log**

---

## 3) InnoDB internals that matter

### 3.1 Buffer Pool
Most important read-performance component.

**SQL example**
```sql
-- Frequently-read hot query that benefits from buffer pool hits
SELECT id, status, total
FROM orders
WHERE user_id = 123
ORDER BY created_at DESC
LIMIT 20;
```

**Example**
If recent orders for active users fit in memory, this query becomes mostly RAM hits instead of random disk reads.


```ini
# my.cnf
innodb_buffer_pool_size = 12G
innodb_buffer_pool_instances = 8
```

Rule of thumb:
- dedicated DB server: **60–80% of RAM**
- shared server: **~50%**

Check hit ratio:

```sql
SHOW STATUS LIKE 'Innodb_buffer_pool_read%';
```

You want **very high hit ratio** on production workloads.

### 3.2 Redo Log
Enables fast durable commits.

**SQL example**
```sql
START TRANSACTION;
UPDATE account SET balance = balance - 100 WHERE id = 1;
UPDATE account SET balance = balance + 100 WHERE id = 2;
COMMIT;
```

**Example**
On commit, InnoDB first makes redo durable. Even if data pages are flushed later, MySQL can recover the committed transfer after a crash.


```ini
innodb_flush_log_at_trx_commit = 1
innodb_log_file_size = 1G
```

`innodb_flush_log_at_trx_commit`:
- `1` = safest, flush every commit
- `2` = faster, small durability risk on OS crash
- `0` = fastest, can lose up to ~1 second

### 3.3 Undo Log + MVCC
Readers see a consistent snapshot while writers keep updating rows.

**SQL example**
```sql
-- Transaction A
START TRANSACTION;
SELECT balance FROM account WHERE id = 1;

-- Transaction B concurrently updates same row
UPDATE account SET balance = 500 WHERE id = 1;
COMMIT;

-- Transaction A still sees its original snapshot under REPEATABLE READ
SELECT balance FROM account WHERE id = 1;
COMMIT;
```

**Example**
A reporting transaction can keep reading a stable version of data while checkout transactions continue updating rows.


Why this matters:
- readers usually do **not** block writers
- writers usually do **not** block readers
- old versions are kept in **undo logs**

### 3.4 Clustered Index
In InnoDB, the **primary key index stores the row data**.

**SQL example**
```sql
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

-- Fast PK lookup
SELECT * FROM product WHERE id = 1001;

-- Secondary index lookup needs extra hop through PK
SELECT * FROM product WHERE sku = 'SKU-123';
```

**Example**
If `id` is compact and sequential, inserts stay localized and secondary indexes stay smaller because they store the PK value.


That means:
- PK lookups are fastest
- secondary indexes store **primary key values**
- bad PK choice hurts all secondary indexes too

Best PK choices:
- **BIGINT AUTO_INCREMENT** for most systems
- **ordered UUID / ULID** if distributed ID generation is required
- avoid random UUIDs if you care about insert locality and index fragmentation

---

## 4) Indexing strategy

### 4.1 Default index type: B+Tree
Good for:

**Example**
Use a B+Tree for email lookups, date ranges, and `ORDER BY created_at LIMIT 20` style queries.

- equality
- range
- prefix search
- sorting

```sql
CREATE INDEX idx_users_email ON users(email);

SELECT * FROM users WHERE email = 'a@b.com';
SELECT * FROM users WHERE email LIKE 'john%';
SELECT * FROM users WHERE email > 'a' AND email < 'm';
SELECT * FROM users ORDER BY email LIMIT 20;
```

### 4.2 Composite indexes
Order matters.

**Example**
If your main query is “all orders for one user sorted by date,” then `(user_id, created_at)` is the right shape. The reverse order usually hurts.


```sql
CREATE INDEX idx_orders_user_date ON orders(user_id, created_at);

-- good
SELECT * FROM orders WHERE user_id = 10;
SELECT * FROM orders WHERE user_id = 10 AND created_at >= '2026-01-01';
SELECT * FROM orders WHERE user_id = 10 ORDER BY created_at DESC;

-- not efficient with this index
SELECT * FROM orders WHERE created_at >= '2026-01-01';
```

**Rule:** put **equality columns first**, then **range / sort columns**.

### 4.3 Covering indexes
Avoid the extra table lookup.

**Example**
A dashboard query that only needs `user_id`, `status`, and `total` can be served directly from the index without touching the base table.


```sql
CREATE INDEX idx_orders_covering ON orders(user_id, status, total);

EXPLAIN SELECT user_id, status, total
FROM orders
WHERE user_id = 123;
```

Look for `Using index` in `EXPLAIN`.

### 4.4 Full-text index

**Example**
For blog/article search, `MATCH ... AGAINST` is better than trying to use `LIKE '%word%'` on large text columns.


```sql
CREATE FULLTEXT INDEX idx_articles_ft ON articles(title, body);

SELECT *
FROM articles
WHERE MATCH(title, body)
AGAINST('mysql replication' IN NATURAL LANGUAGE MODE);
```

### 4.5 Index anti-patterns

**Example**
Applying a function like `YEAR(created_at)` hides the raw indexed value from the optimizer, so MySQL often falls back to a scan.

Bad:
```sql
SELECT * FROM users WHERE YEAR(created_at) = 2026;
SELECT * FROM products WHERE name LIKE '%phone%';
SELECT * FROM users WHERE phone = 1234567890; -- phone is VARCHAR
```

Better:
```sql
SELECT * FROM users
WHERE created_at >= '2026-01-01' AND created_at < '2027-01-01';

SELECT * FROM products
WHERE name LIKE 'phone%';

SELECT * FROM users
WHERE phone = '1234567890';
```

---

## 5) Transactions and locking

### 5.1 Isolation levels
InnoDB default is **REPEATABLE READ**.

**SQL example**
```sql
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;

START TRANSACTION;
SELECT quantity FROM inventory WHERE product_id = 123;
-- another transaction commits an update here
SELECT quantity FROM inventory WHERE product_id = 123;
COMMIT;
```

**Example**
Use REPEATABLE READ when you want a stable snapshot during a transaction. Use READ COMMITTED when simpler read behavior and less locking matter more.


| Level | Dirty Read | Non-Repeatable Read | Phantom Read |
|---|---:|---:|---:|
| READ UNCOMMITTED | Yes | Yes | Yes |
| READ COMMITTED | No | Yes | Yes |
| REPEATABLE READ | No | No | Mostly prevented in InnoDB |
| SERIALIZABLE | No | No | No |

For interviews:
- **READ COMMITTED**: common for simpler app behavior
- **REPEATABLE READ**: MySQL default, strong enough for many OLTP systems
- **SERIALIZABLE**: strongest, but more blocking / aborts

### 5.2 Pessimistic locking
Best when contention is high.

**Example**
In a flash sale, many users race for the same inventory row. `FOR UPDATE` serializes access and prevents overselling.


```sql
START TRANSACTION;

SELECT quantity
FROM inventory
WHERE product_id = 123
FOR UPDATE;

UPDATE inventory
SET quantity = quantity - 1
WHERE product_id = 123;

COMMIT;
```

Use for:
- flash sale inventory
- wallet transfer
- seat booking
- job queue claims

### 5.3 Optimistic locking
Best when conflicts are rare.

**Example**
Profile edits or low-contention inventory updates usually work well with a version column because most requests do not collide.


```sql
ALTER TABLE inventory ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
```

```sql
UPDATE inventory
SET quantity = quantity - 1,
    version = version + 1
WHERE product_id = 123
  AND version = 5
  AND quantity > 0;
```

If `rows affected = 0`, retry or fail.

### 5.4 Locking reads

**Example**
`FOR UPDATE SKIP LOCKED` is great for worker queues: multiple workers can safely claim different pending jobs without stepping on each other.

```sql
SELECT * FROM accounts WHERE id = 1 FOR SHARE;
SELECT * FROM accounts WHERE id = 1 FOR UPDATE;
SELECT * FROM jobs WHERE status = 'PENDING' FOR UPDATE SKIP LOCKED LIMIT 1;
```

Useful for worker queues and high-contention updates.

### 5.5 Deadlocks
They happen under concurrency.

**SQL example**
```sql
-- Transaction 1
START TRANSACTION;
SELECT * FROM account WHERE id = 1 FOR UPDATE;
-- waits later on account 2

-- Transaction 2
START TRANSACTION;
SELECT * FROM account WHERE id = 2 FOR UPDATE;
-- waits later on account 1
```

**Example**
Two transfers that lock accounts in opposite order can deadlock. Fix it by always locking smaller account ID first.


Reduce them by:
- always locking rows in the **same order**
- keeping transactions **short**
- indexing properly so fewer rows are locked
- using the weakest isolation that still preserves correctness

Inspect:

```sql
SHOW ENGINE INNODB STATUS;
```

---

## 6) Replication

### 6.1 Basic flow
Primary writes changes to **binlog**. Replicas pull and apply them.

**SQL example**
```sql
SHOW BINARY LOG STATUS;
SHOW REPLICA STATUS\G
```

**Example**
A checkout insert is committed on primary, written to binlog, then streamed to replicas so product/order reads can be offloaded.


Good for:
- read scaling
- failover standby
- backups / analytics offload

### 6.2 Binlog format
Use **ROW**.

**SQL example**
```ini
binlog_format = ROW
```

**Example**
With row-based replication, `UPDATE accounts SET balance = balance - 100 WHERE id = 1` replicates as exact row changes, avoiding ambiguity from non-deterministic SQL.


```ini
binlog_format = ROW
```

Why:
- deterministic
- safer with triggers / functions / non-deterministic SQL

### 6.3 Async vs Semi-sync

**SQL / config example**
```ini
# semi-sync example
plugin-load-add = semisync_source.so
rpl_semi_sync_source_enabled = 1
plugin-load-add = semisync_replica.so
rpl_semi_sync_replica_enabled = 1
```

#### Asynchronous
- primary commits immediately
- lowest write latency
- can lose latest commits if primary dies before replica receives them

#### Semi-synchronous
- primary waits until at least one replica receives the binlog event
- lower data-loss risk
- higher write latency

### 6.4 Read scaling pattern
- writes → primary
- reads → replicas
- read-after-write sensitive flows → read from primary briefly after write

**SQL example**
```sql
-- write
UPDATE users SET last_login_at = NOW() WHERE id = 42;

-- immediate consistency read should go to primary
SELECT last_login_at FROM users WHERE id = 42;
```

**Example**
After a user updates their profile, keep that user’s next few reads on primary so they see their own write immediately.

- reads → replicas
- read-after-write sensitive flows → read from primary briefly after write

Check lag:

```sql
SHOW REPLICA STATUS\G
```

Look at:
- `Seconds_Behind_Source`

### 6.5 Group Replication
Use when you want:

**Example**
For an internal business system that needs automatic failover without custom orchestration, Group Replication can be simpler than building your own promotion flow.

- automatic failover
- built-in HA
- consensus-based coordination

Trade-offs:
- higher write latency
- more complexity
- needs quorum

**Interview line:**  
> Default choice is primary + replicas with async row-based replication. If I need stronger durability and automatic failover, I’d consider semi-sync or Group Replication.

---

## 7) Sharding

Sharding is for when:

**SQL example**
```sql
-- single-shard query if sharded by user_id
SELECT * FROM orders WHERE user_id = 123;

-- cross-shard query if not routed by shard key
SELECT COUNT(*) FROM orders WHERE created_at >= '2026-01-01';
```

**Example**
If orders are sharded by `user_id`, “show my orders” hits one shard, but “all orders this month” may require scatter-gather across all shards.

- dataset no longer fits comfortably on one primary
- write throughput exceeds one primary
- replicas + caching + tuning are no longer enough

### 7.1 Shard approaches

#### Range-based
```text
1–1M -> shard1
1M–2M -> shard2
```

**Example**
This works well when tenants are naturally grouped by ID ranges, but new IDs often create hotspots on the latest shard.

1M–2M -> shard2
```

Pros:
- simple
- range queries stay local

Cons:
- hotspots
- rebalancing pain

#### Hash-based
```text
shard = hash(user_id) % N

user_id 123 -> shard 3
```

**Example**
A social app often hashes `user_id` so read and write load spread more evenly.

```

Pros:
- even distribution

Cons:
- range queries scatter
- resharding is painful without an indirection layer

#### Directory-based
Lookup service maps key → shard.

**Example**
Large SaaS systems use this to move a single tenant to a dedicated shard without moving everyone else.


Pros:
- flexible movement
- tenant-by-tenant migration

Cons:
- more infra
- lookup layer becomes critical

### 7.2 Good shard key
Choose a key that is:

**Example**
For orders, shard by `user_id` instead of `order_id` so “get all orders for one user” stays on one shard.

- high cardinality
- evenly distributed
- used in most queries
- stable

Common choices:
- `user_id`
- `tenant_id`
- `conversation_id`

### 7.3 Before you shard
Always say you would try first:
1. query optimization
2. indexing
3. bigger primary
4. read replicas
5. Redis / caching
6. archival / partitioning
7. **then sharding**

### 7.4 Vitess
Best-known MySQL sharding platform.

**Example**
Vitess is a good interview answer when you want MySQL compatibility but do not want shard routing logic spread across application code.


Why it matters:
- query routing
- pooling
- resharding support
- less sharding logic in app code

---

## 8) Query optimization

### 8.1 EXPLAIN and EXPLAIN ANALYZE

**Example**
If `EXPLAIN` shows `type = ALL` on a million-row table, that usually means a full table scan and a likely indexing problem.

```sql
EXPLAIN SELECT * FROM orders WHERE user_id = 123;
EXPLAIN ANALYZE SELECT * FROM orders WHERE user_id = 123;
```

Key things to inspect:
- `type`
- `key`
- `rows`
- `Extra`

Join/access type quality:
- `const`, `eq_ref` → great
- `ref`, `range` → good
- `index` → okay-ish
- `ALL` → full table scan, usually bad on large tables

### 8.2 Common fixes
- add / adjust indexes
- avoid `SELECT *`
- make `ORDER BY ... LIMIT` index-friendly
- replace large `OFFSET` pagination with keyset pagination
- batch inserts

**SQL example**
```sql
-- Batch insert is far cheaper than many round trips
INSERT INTO event_log (user_id, event_type)
VALUES (1, 'click'), (2, 'view'), (3, 'purchase');
```

- avoid `SELECT *`
- make `ORDER BY ... LIMIT` index-friendly
- replace large `OFFSET` pagination with keyset pagination
- batch inserts

Bad:
```sql
SELECT * FROM products ORDER BY id LIMIT 20 OFFSET 10000;
```

Good:
```sql
SELECT * FROM products
WHERE id > 10000
ORDER BY id
LIMIT 20;
```

### 8.3 Monitoring
- slow query log
- Performance Schema
- process list

**Example**
Turn on the slow query log first, find the few query patterns eating most of the time, then run `EXPLAIN ANALYZE` on those.

- Performance Schema
- process list

```sql
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;

SHOW PROCESSLIST;
```

---

## 9) MySQL vs others

### MySQL vs PostgreSQL
Choose **MySQL** for:

**Example**
For a classic CRUD-heavy ecommerce backend with moderate analytics and strong team familiarity, MySQL is often the simpler operational choice.

- simpler operations
- read-heavy web systems
- mature replica-based scaling
- team familiarity

Choose **PostgreSQL** for:
- more advanced SQL
- stronger JSON querying/indexing
- richer extensions / geospatial

### MySQL vs MongoDB
Choose **MySQL** when relationships + transactions matter.  

**Example**
Orders, payments, inventory, and users are easier to keep consistent in a relational schema than across duplicated documents.

Choose **MongoDB** when schema flexibility and document modeling dominate.

### MySQL vs Cassandra
Choose **MySQL** for ACID + flexible queries.  

**Example**
Use Cassandra for massive event ingestion; use MySQL for transactional order management.

Choose **Cassandra** for extreme write throughput and huge distributed scale.

### MySQL vs TiDB
Choose **MySQL** if a single-node primary architecture is still enough.  

**Example**
TiDB becomes attractive when one primary can no longer handle writes and you need distributed SQL with MySQL compatibility.

Choose **TiDB** when you want MySQL compatibility with distributed SQL scaling.

---

# Spring Boot implementation examples

Below is a simple, practical stack:
- Spring Boot
- Spring Data JPA
- MySQL
- Flyway
- HikariCP

---

## 10) Dependencies

```xml
<!-- pom.xml -->
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
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>

    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

---

## 11) Configuration

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/appdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: app
    password: secret
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000

  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    properties:
      hibernate:
        format_sql: true
        jdbc:
          batch_size: 50

  flyway:
    enabled: true
    locations: classpath:db/migration

logging:
  level:
    org.hibernate.SQL: info
```

---

## 12) Flyway schema

```sql
-- src/main/resources/db/migration/V1__init.sql
CREATE TABLE account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_name VARCHAR(255) NOT NULL,
    balance DECIMAL(19,2) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inventory (
    product_id BIGINT PRIMARY KEY,
    quantity INT NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE payment_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    idempotency_key VARCHAR(100) NOT NULL,
    account_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_payment_idempotency UNIQUE (idempotency_key)
);

CREATE INDEX idx_payment_account_created ON payment_request(account_id, created_at);
```

---

## 13) JPA entities

```java
// Account.java
package com.example.demo.account;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    public Long getId() { return id; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public Long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
}
```

```java
// Inventory.java
package com.example.demo.inventory;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Version
    @Column(nullable = false)
    private Long version;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Long getVersion() { return version; }
}
```

```java
// PaymentRequest.java
package com.example.demo.payment;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment_request", uniqueConstraints = {
        @UniqueConstraint(name = "uk_payment_idempotency", columnNames = "idempotency_key")
})
public class PaymentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    public Long getId() { return id; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

---

## 14) Repositories

```java
// AccountRepository.java
package com.example.demo.account;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") Long id);
}
```

```java
// InventoryRepository.java
package com.example.demo.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
```

```java
// PaymentRequestRepository.java
package com.example.demo.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {
    Optional<PaymentRequest> findByIdempotencyKey(String idempotencyKey);
}
```

---

## 15) Pessimistic locking example: transfer money safely

```java
// TransferService.java
package com.example.demo.account;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Long first = Math.min(fromId, toId);
        Long second = Math.max(fromId, toId);

        Account a = accountRepository.findByIdForUpdate(first)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + first));
        Account b = accountRepository.findByIdForUpdate(second)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + second));

        Account from = fromId.equals(first) ? a : b;
        Account to = toId.equals(first) ? a : b;

        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
    }
}
```

Why good:
- `@Transactional`
- `PESSIMISTIC_WRITE`
- lock ordering reduces deadlocks

---

## 16) Optimistic locking example: low-contention inventory update

```java
// InventoryService.java
package com.example.demo.inventory;

import jakarta.transaction.Transactional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public void decrementStock(Long productId, int amount) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found"));

        if (inventory.getQuantity() < amount) {
            throw new IllegalStateException("Out of stock");
        }

        inventory.setQuantity(inventory.getQuantity() - amount);
    }

    public void decrementWithRetry(Long productId, int amount) {
        int retries = 3;
        for (int i = 0; i < retries; i++) {
            try {
                decrementStock(productId, amount);
                return;
            } catch (ObjectOptimisticLockingFailureException ex) {
                if (i == retries - 1) throw ex;
            }
        }
    }
}
```

Why good:
- best when collisions are rare
- avoids blocking
- retries on version conflict

---

## 17) Idempotency with unique key

```java
// PaymentService.java
package com.example.demo.payment;

import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final PaymentRequestRepository paymentRequestRepository;

    public PaymentService(PaymentRequestRepository paymentRequestRepository) {
        this.paymentRequestRepository = paymentRequestRepository;
    }

    @Transactional
    public PaymentRequest createPayment(String idempotencyKey, Long accountId, BigDecimal amount) {
        return paymentRequestRepository.findByIdempotencyKey(idempotencyKey)
                .orElseGet(() -> {
                    PaymentRequest request = new PaymentRequest();
                    request.setIdempotencyKey(idempotencyKey);
                    request.setAccountId(accountId);
                    request.setAmount(amount);
                    request.setStatus("CREATED");
                    try {
                        return paymentRequestRepository.save(request);
                    } catch (DataIntegrityViolationException e) {
                        return paymentRequestRepository.findByIdempotencyKey(idempotencyKey)
                                .orElseThrow(() -> e);
                    }
                });
    }
}
```

Why good:
- retries become safe
- unique constraint enforces idempotency at DB layer

---

## 18) Native SQL UPSERT in MySQL

```java
// Example native query
@Modifying
@Query(value = """
    INSERT INTO payment_request (idempotency_key, account_id, amount, status)
    VALUES (:key, :accountId, :amount, 'CREATED')
    ON DUPLICATE KEY UPDATE
        status = status
    """, nativeQuery = true)
void insertIgnoreDuplicate(
        @Param("key") String key,
        @Param("accountId") Long accountId,
        @Param("amount") BigDecimal amount);
```

For MySQL, interview-safe phrasing:
- PostgreSQL → `ON CONFLICT`
- MySQL → `ON DUPLICATE KEY UPDATE`

---

## 19) Read/write split pattern in Spring Boot

In many apps:
- **writes** use primary datasource
- **read-only queries** use replica datasource

Simplest interview answer:
- start with **single datasource**
- introduce **replica datasource** only when read load justifies complexity
- route with `@Transactional(readOnly = true)` + routing datasource or at proxy layer (ProxySQL)

Example architecture answer:
> I’d prefer read/write split at ProxySQL first because it keeps routing logic out of the application. For stricter control, I can add a routing datasource in Spring Boot.

---

## 20) Pagination example

```java
// Bad: offset gets slower with large page numbers
Page<Order> findByUserId(Long userId, Pageable pageable);
```

Prefer keyset/cursor style for large feeds:

```sql
SELECT id, user_id, status, created_at
FROM orders
WHERE user_id = 123
  AND id > 5000
ORDER BY id
LIMIT 20;
```

Spring Data example:

```java
@Query("select o from OrderEntity o where o.userId = :userId and o.id > :cursor order by o.id asc")
List<OrderEntity> findNextPage(@Param("userId") Long userId,
                               @Param("cursor") Long cursor,
                               Pageable pageable);
```

---

## 21) What to say in interviews

Use these lines directly.

### Why MySQL?
> I’d choose MySQL when the system is relational, read-heavy, and I want mature operations plus easy read scaling through replicas.

### Why InnoDB?
> InnoDB gives me ACID transactions, row-level locking, MVCC, crash recovery, and clustered indexes, so it’s the default production choice.

### How do you prevent overselling?
> For high-contention inventory I’d use a short transaction with `SELECT ... FOR UPDATE`, or optimistic locking with a version column if contention is low.

### How do you scale reads?
> Primary handles writes, replicas handle reads, and I keep read-after-write sensitive flows on the primary for a short window.

### When would you shard?
> Only after exhausting query tuning, indexes, vertical scaling, replicas, and caching. Then I’d shard by a stable high-cardinality key like `user_id` or `tenant_id`.

### Biggest MySQL trade-off?
> It scales reads very well, but write scaling usually requires sharding or a system like Vitess, and its advanced SQL features are generally less rich than PostgreSQL.

---

## 22) Final study checklist

Know these cold:

- what **Buffer Pool** does
- what **Redo Log** does
- what **MVCC** means
- why **clustered PK** matters
- how **secondary indexes** work
- **leftmost prefix** rule
- **covering index**
- `FOR UPDATE` vs optimistic locking
- async vs semi-sync replication
- replication lag handling
- when to shard, and good shard keys
- `EXPLAIN` basics
- why keyset pagination beats offset at scale

---

## 23) One-page memory version

- **MySQL sweet spot**: read-heavy relational apps
- **InnoDB**: transactions, MVCC, row locks, crash recovery
- **Buffer Pool** = read speed
- **Redo Log** = durability + fast commit
- **Clustered PK** = table stored by PK
- **Secondary index** = points to PK, not row pointer
- **Composite index**: equality first, range next
- **Covering index** avoids table lookup
- **FOR UPDATE** for high contention
- **Optimistic locking** for low contention
- **ROW binlog** for safe replication
- **Replicas** scale reads, not writes
- **Sharding** only after simpler fixes fail
- **Keyset pagination** over offset
- **EXPLAIN** before guessing

---

Keep this as your private repo README and practice explaining each section aloud in 30–60 seconds.
