# PostgreSQL for System Design Interviews — Last-Minute Notes

A compact, implementation-focused guide you can keep in a private GitHub repo.

---

## 1) When PostgreSQL is the right choice

Choose PostgreSQL when the system needs:

- **Complex relational queries**: joins, aggregations, CTEs, window functions.
- **Strong consistency**: money movement, inventory, bookings, ledgers.
- **Reliable transactions**: ACID by default.
- **Flexible querying**: schema evolves, query patterns change.
- **JSON + relational mix**: structured core data with semi-structured fields via `JSONB`.
- **Full-text search**: good enough for many apps without Elasticsearch.
- **Geospatial support**: via **PostGIS**.

Typical interview systems:

- Payment system
- E-commerce orders + inventory
- Booking/reservations
- User/account management
- Financial ledger
- CMS / content platform
- Multi-tenant SaaS

### Good interview line

> “I’d start with PostgreSQL when I need correctness, transactional guarantees, and flexible SQL over related data. I’d add read replicas, pooling, indexing, and partitioning as scale grows.”

---

## 2) When PostgreSQL is **not** the right choice

Avoid PostgreSQL as the main store when the system is primarily:

- **Extreme write-heavy append-only**: telemetry, clickstream, event firehose at massive scale.
  - Better fit: Cassandra, ClickHouse.
- **Massive horizontal scale by default**:
  - Better fit: DynamoDB, Cassandra, CockroachDB.
- **Simple key-value only**:
  - Better fit: Redis, DynamoDB.
- **Time-series first**:
  - Better fit: TimescaleDB, InfluxDB, ClickHouse.
- **Cache layer**:
  - Better fit: Redis or Memcached in front of PostgreSQL.

### Good interview line

> “PostgreSQL is a great default, but not for every workload. If the access pattern is mostly key-value or append-only at huge scale, I’d reach for a more specialized database.”

---

## 3) Architecture you should describe in interviews

```text
Clients/App Servers
        |
        v
    PgBouncer
   /         \
Writes       Reads
  |             |
  v             v
Primary ----> Replicas
   |            ^
   |            |
   +-- WAL ---- +  (streaming replication)
```

### Request flow

1. App connects to **PgBouncer**, not directly to PostgreSQL.
2. **Writes** go to the **primary**.
3. **Reads** can go to **replicas**.
4. Inside PostgreSQL:
   - **Parser** validates SQL
   - **Optimizer** chooses a plan
   - **Executor** runs it
   - **Buffer Manager** uses memory pages when possible
   - **WAL** ensures durability before commit

### Key points to mention

- **Primary** is source of truth for writes.
- **Replicas** help with read scaling and failover.
- **Replication lag** means replicas may be slightly stale.
- **Read-your-writes** sensitive flows should read from primary briefly after a write.

---

## 4) ACID in practical terms

### Atomicity
All-or-nothing.

Example: transfer money. Either debit and credit both happen, or neither happens.

### Consistency
Constraints keep data valid.

Examples:
- foreign keys
- unique constraints
- check constraints

### Isolation
Concurrent transactions should not corrupt data.

### Durability
Committed data survives crashes through **WAL**.

### Example: money transfer in SQL

```sql
BEGIN;

UPDATE accounts
SET balance = balance - 100
WHERE id = 1;

UPDATE accounts
SET balance = balance + 100
WHERE id = 2;

COMMIT;
```

---

## 5) Isolation levels you must know

### Read Committed (default)
- Each statement sees only committed data.
- Different statements in one transaction can see different values.
- Good default for most workloads.

```sql
BEGIN;
SELECT balance FROM accounts WHERE id = 1; -- 100
-- another tx commits update to 150
SELECT balance FROM accounts WHERE id = 1; -- 150
COMMIT;
```

### Repeatable Read
- Transaction sees a stable snapshot from the beginning.
- Great for consistent reports and many business transactions.

```sql
BEGIN ISOLATION LEVEL REPEATABLE READ;
SELECT balance FROM accounts WHERE id = 1; -- 100
-- another tx commits update to 150
SELECT balance FROM accounts WHERE id = 1; -- still 100
COMMIT;
```

### Serializable
- Strongest isolation.
- PostgreSQL aborts one transaction if concurrent execution could violate serial order.
- Use for high-correctness flows like inventory oversell prevention.
- **Must retry on serialization failure**.

```sql
BEGIN ISOLATION LEVEL SERIALIZABLE;
SELECT SUM(balance) FROM accounts;
-- do writes based on that read
COMMIT; -- may fail with serialization error
```

### Interview rule of thumb

- **Most requests** → `Read Committed`
- **Consistent reporting / snapshot reads** → `Repeatable Read`
- **Critical correctness (inventory, balances, booking conflicts)** → `Serializable` or locks/constraints

---

## 6) Preventing double-spending / overselling

### Option A: row lock with `SELECT ... FOR UPDATE`

Lock the row before updating.

```sql
BEGIN;

SELECT balance
FROM accounts
WHERE id = 1
FOR UPDATE;

UPDATE accounts
SET balance = balance - 100
WHERE id = 1;

UPDATE accounts
SET balance = balance + 100
WHERE id = 2;

COMMIT;
```

Use when:
- updating a known row
- contention is acceptable
- you want simple correctness

### Option B: serializable transaction

Good when correctness depends on multiple reads/writes and you want DB-level protection.

### Option C: constraint-based correctness

For inventory:

```sql
ALTER TABLE inventory
ADD CONSTRAINT inventory_non_negative CHECK (quantity >= 0);
```

Then update atomically:

```sql
UPDATE inventory
SET quantity = quantity - 1
WHERE product_id = 123 AND quantity > 0;
```

Check affected rows. If `0`, item is sold out.

### Best interview line

> “For payments or inventory, I prevent race conditions at the database layer, not just in application code, using row locks, serializable isolation, or constraints depending on the flow.”

---

## 7) Indexing cheat sheet

Indexes speed reads but make writes slower and use storage.

### 7.1 B-tree (default)
Best general-purpose index.

Use for:
- equality
- range queries
- prefix matches
- sorting

```sql
CREATE INDEX idx_users_email ON users(email);

SELECT * FROM users WHERE email = 'john@example.com';
SELECT * FROM users WHERE email LIKE 'john%';
SELECT * FROM users WHERE email > 'a' AND email < 'b';
SELECT * FROM users ORDER BY email LIMIT 10;
```

### 7.2 Hash
Equality-only.
Usually not worth choosing over B-tree.

```sql
CREATE INDEX idx_users_uuid_hash ON users USING hash(uuid);
```

### 7.3 GIN
Use for:
- `JSONB`
- arrays
- full-text search

```sql
CREATE INDEX idx_products_attrs ON products USING gin(attributes);
SELECT * FROM products WHERE attributes @> '{"color":"red"}';

CREATE INDEX idx_posts_tags ON posts USING gin(tags);
SELECT * FROM posts WHERE tags @> ARRAY['postgresql'];

CREATE INDEX idx_articles_search
ON articles USING gin(to_tsvector('english', content));
```

### 7.4 GiST
Use for:
- ranges
- overlap queries
- geospatial

```sql
CREATE INDEX idx_reservations_during ON reservations USING gist(time_range);
SELECT * FROM reservations
WHERE time_range && '[2026-04-20 10:00, 2026-04-20 11:00)'::tstzrange;
```

### 7.5 BRIN
Use for huge, naturally ordered tables such as logs/events.

```sql
CREATE INDEX idx_events_created_at ON events USING brin(created_at);

SELECT * FROM events
WHERE created_at >= '2026-04-01' AND created_at < '2026-04-02';
```

Best when:
- table is very large
- data is stored in time order
- queries are mostly time ranges

### 7.6 Composite index
Column order matters.

```sql
CREATE INDEX idx_orders_user_date ON orders(user_id, created_at);

SELECT * FROM orders WHERE user_id = 42;
SELECT * FROM orders WHERE user_id = 42 AND created_at > '2026-01-01';
```

This index is **not** ideal for:

```sql
SELECT * FROM orders WHERE created_at > '2026-01-01';
```

Rule:
- put **equality filters first**
- then **range filters**

### 7.7 Partial index
Index only what you query often.

```sql
CREATE INDEX idx_active_users_email
ON users(email)
WHERE status = 'active';
```

### 7.8 Covering index
Avoids extra table reads.

```sql
CREATE INDEX idx_orders_user_include
ON orders(user_id)
INCLUDE (total, status);
```

---

## 8) Partitioning for very large tables

Partition when a table becomes operationally painful.

Benefits:
- faster time-bounded queries
- easier vacuum/index maintenance
- cheap old-data deletion by dropping partitions
- better manageability

### 8.1 Range partitioning by time

```sql
CREATE TABLE orders (
    id BIGSERIAL,
    user_id BIGINT,
    total NUMERIC(12,2),
    created_at TIMESTAMP NOT NULL
) PARTITION BY RANGE (created_at);

CREATE TABLE orders_2026_q1 PARTITION OF orders
FOR VALUES FROM ('2026-01-01') TO ('2026-04-01');

CREATE TABLE orders_2026_q2 PARTITION OF orders
FOR VALUES FROM ('2026-04-01') TO ('2026-07-01');
```

### 8.2 List partitioning

```sql
CREATE TABLE customers (
    id BIGSERIAL,
    name TEXT,
    region TEXT NOT NULL
) PARTITION BY LIST (region);

CREATE TABLE customers_us PARTITION OF customers FOR VALUES IN ('us-east', 'us-west');
CREATE TABLE customers_eu PARTITION OF customers FOR VALUES IN ('eu-west', 'eu-central');
```

### 8.3 Hash partitioning

```sql
CREATE TABLE events (
    id BIGSERIAL,
    user_id BIGINT NOT NULL,
    event_type TEXT
) PARTITION BY HASH (user_id);

CREATE TABLE events_0 PARTITION OF events FOR VALUES WITH (MODULUS 4, REMAINDER 0);
CREATE TABLE events_1 PARTITION OF events FOR VALUES WITH (MODULUS 4, REMAINDER 1);
CREATE TABLE events_2 PARTITION OF events FOR VALUES WITH (MODULUS 4, REMAINDER 2);
CREATE TABLE events_3 PARTITION OF events FOR VALUES WITH (MODULUS 4, REMAINDER 3);
```

### Interview guidance

Partition by the same field used in common filters.

Examples:
- logs/events/orders → `created_at`
- multi-tenant → `tenant_id`
- region → `region`

### Common limitations

- too many partitions add overhead
- global uniqueness is tricky
- foreign keys get more complex
- partition key updates are awkward

---

## 9) Replication and HA

### Streaming replication
Replicas replay WAL from primary.

### Asynchronous replication
- fastest writes
- possible data loss on primary failure

### Synchronous replication
- primary waits for replica ack
- safer, higher latency
- good for financial-grade durability

### Example config

```conf
# postgresql.conf
wal_level = replica
max_wal_senders = 5
wal_keep_size = 1GB
```

```conf
# pg_hba.conf
host replication replica_user 10.0.0.10/32 md5
```

### Create replica

```bash
pg_basebackup -h primary_host -D /var/lib/postgresql/data -U replica_user -P
```

### Promote replica on failover

```sql
SELECT pg_promote();
```

### Measure replication lag on replica

```sql
SELECT EXTRACT(EPOCH FROM (now() - pg_last_xact_replay_timestamp())) AS lag_seconds;
```

### Interview-ready HA options

- **Manual failover**: simplest, slower
- **Patroni + etcd/Consul**: automatic leader election/failover
- **Managed cloud**: RDS Multi-AZ / Aurora PostgreSQL

### Good interview line

> “For HA, I’d use one primary plus replicas. If failover speed matters, I’d use Patroni or a managed HA offering. If zero data loss matters, I’d consider synchronous replication for critical writes.”

---

## 10) Connection pooling

PostgreSQL uses **process-per-connection**, so too many connections hurt memory and throughput.

### Why PgBouncer
Without pooling:
- every connection costs memory
- connection setup is expensive
- many services can overwhelm PostgreSQL quickly

With PgBouncer:
- many app clients share fewer DB connections
- more stable connection counts
- better concurrency

### Recommended mode
Use **transaction pooling** unless you truly need session state.

### Example PgBouncer config

```ini
[databases]
appdb = host=127.0.0.1 port=5432 dbname=appdb

[pgbouncer]
listen_addr = 0.0.0.0
listen_port = 6432
pool_mode = transaction
max_client_conn = 10000
default_pool_size = 100
min_pool_size = 10
reserve_pool_size = 5
server_idle_timeout = 600
```

### What to monitor

- `cl_waiting > 0` → requests are waiting for DB connections
- `sv_active` close to pool size → pool pressure
- average query time increasing → DB is slow, not just pool-starved

### Best practice
Use both:
- **application-side pool**
- **PgBouncer**

---

## 11) Patterns that show up in interviews

### 11.1 Optimistic locking

```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    version INT NOT NULL DEFAULT 1
);

UPDATE products
SET price = 12.00,
    version = version + 1
WHERE id = 123 AND version = 5;
```

If affected rows = `0`, another update won first.

### 11.2 Advisory locks

```sql
SELECT pg_try_advisory_lock(hashtext('payout_user_123'));
-- true = lock acquired

SELECT pg_advisory_unlock(hashtext('payout_user_123'));
```

Use for:
- one worker per job
- per-user workflow exclusivity
- cross-process coordination

### 11.3 UPSERT

```sql
INSERT INTO users (email, name, updated_at)
VALUES ('john@example.com', 'John Doe', NOW())
ON CONFLICT (email)
DO UPDATE SET
    name = EXCLUDED.name,
    updated_at = EXCLUDED.updated_at;
```

### 11.4 `RETURNING`

```sql
INSERT INTO orders (user_id, total)
VALUES (123, 99.99)
RETURNING id, created_at;
```

### 11.5 CTEs

```sql
WITH user_orders AS (
    SELECT user_id, COUNT(*) AS order_count, SUM(total) AS total_spent
    FROM orders
    WHERE created_at > NOW() - INTERVAL '1 year'
    GROUP BY user_id
)
SELECT *
FROM user_orders
WHERE total_spent > 1000;
```

### 11.6 Prevent double-booking with exclusion constraint

```sql
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    resource_id BIGINT NOT NULL,
    time_range TSTZRANGE NOT NULL,
    user_id BIGINT NOT NULL,
    EXCLUDE USING gist (
        resource_id WITH =,
        time_range WITH &&
    )
);
```

Insert will fail automatically if time ranges overlap for the same resource.

### 11.7 Audit logging with triggers

```sql
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    table_name TEXT NOT NULL,
    operation TEXT NOT NULL,
    old_data JSONB,
    new_data JSONB,
    changed_by TEXT,
    changed_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE OR REPLACE FUNCTION audit_trigger()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO audit_log (table_name, operation, old_data, new_data, changed_by)
    VALUES (
        TG_TABLE_NAME,
        TG_OP,
        CASE WHEN TG_OP IN ('UPDATE', 'DELETE') THEN row_to_json(OLD)::jsonb ELSE NULL END,
        CASE WHEN TG_OP IN ('INSERT', 'UPDATE') THEN row_to_json(NEW)::jsonb ELSE NULL END,
        current_user
    );
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;
```

---

## 12) Performance optimization workflow

### 12.1 Start with `EXPLAIN ANALYZE`

```sql
EXPLAIN (ANALYZE, BUFFERS)
SELECT *
FROM orders
WHERE user_id = 42 AND created_at > '2026-01-01';
```

Look for:
- `Seq Scan` on big tables → probably missing/unused index
- bad join strategy
- large sorts
- actual rows vs estimated rows mismatch

### 12.2 Refresh stats

```sql
ANALYZE orders;
ANALYZE;
```

### 12.3 Important settings

```conf
shared_buffers = 4GB
work_mem = 64MB
maintenance_work_mem = 1GB
effective_cache_size = 12GB
checkpoint_timeout = 15min
max_wal_size = 4GB
random_page_cost = 1.1
max_connections = 200
```

### 12.4 Vacuum / autovacuum
PostgreSQL uses MVCC, so updates create dead tuples.

```sql
VACUUM ANALYZE orders;
```

Avoid in production unless you truly need it:

```sql
VACUUM FULL orders;
```

### 12.5 Common anti-patterns

- `SELECT *`
- N+1 queries
- over-indexing
- very long transactions
- large `OFFSET` pagination

### Better pagination

```sql
-- Bad
SELECT * FROM products ORDER BY id LIMIT 20 OFFSET 10000;

-- Good
SELECT * FROM products
WHERE id > 12345
ORDER BY id
LIMIT 20;
```

### Useful monitoring queries

```sql
SELECT query, calls, mean_exec_time, total_exec_time
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 20;
```

```sql
SELECT pid, now() - query_start AS duration, query
FROM pg_stat_activity
WHERE state = 'active'
ORDER BY duration DESC;
```

```sql
SELECT relname, n_dead_tup, n_live_tup
FROM pg_stat_user_tables
ORDER BY n_dead_tup DESC;
```

---

## 13) PostgreSQL vs alternatives

### PostgreSQL vs MySQL
Choose PostgreSQL for:
- richer SQL
- stronger JSONB story
- more advanced indexing/extensions
- stricter data integrity by default

### PostgreSQL vs MongoDB
Choose PostgreSQL for:
- joins and relational data
- stronger transactional patterns
- SQL flexibility

Choose MongoDB for:
- document-first modeling
- looser schema needs
- easier native sharding story

### PostgreSQL vs DynamoDB
Choose PostgreSQL for:
- complex queries
- transactions
- flexible read patterns

Choose DynamoDB for:
- massive managed horizontal scale
- simple key-based access
- serverless operational model

### Good interview line

> “Database choice is driven by access patterns, consistency requirements, and scale shape. PostgreSQL is my default for transactional relational systems, not for every workload.”

---

# 14) Spring Boot implementation notes

Below is a clean starting point for building a transactional PostgreSQL-backed service from scratch.

## 14.1 Maven dependencies

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

    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
</dependencies>
```

## 14.2 `application.yml`

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/appdb
    username: appuser
    password: secret
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          time_zone: UTC
    open-in-view: false

  flyway:
    enabled: true
    locations: classpath:db/migration
```

## 14.3 Flyway migration: `V1__init.sql`

```sql
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    owner_name TEXT NOT NULL,
    balance NUMERIC(19,2) NOT NULL CHECK (balance >= 0),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE idempotency_keys (
    key TEXT PRIMARY KEY,
    response_json JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_accounts_owner_name ON accounts(owner_name);
```

## 14.4 Entity

```java
package com.example.demo.account;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "accounts")
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

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public Long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
```

## 14.5 Repository

```java
package com.example.demo.account;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> findByIdForUpdate(Long id);
}
```

## 14.6 DTOs

```java
package com.example.demo.account;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
        @NotNull Long fromAccountId,
        @NotNull Long toAccountId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull String idempotencyKey
) {}
```

```java
package com.example.demo.account;

import java.math.BigDecimal;

public record TransferResponse(
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount,
        String status
) {}
```

## 14.7 Service: safe transfer with row locking

```java
package com.example.demo.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TransferResponse transfer(TransferRequest request) {
        TransferResponse cached = tryLoadIdempotentResponse(request.idempotencyKey());
        if (cached != null) {
            return cached;
        }

        Account from = accountRepository.findByIdForUpdate(request.fromAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        Account to = accountRepository.findByIdForUpdate(request.toAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        if (from.getId().equals(to.getId())) {
            throw new IllegalArgumentException("Source and destination must differ");
        }

        BigDecimal amount = request.amount();
        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);
        entityManager.flush();

        TransferResponse response = new TransferResponse(
                from.getId(), to.getId(), amount, "SUCCESS"
        );

        storeIdempotentResponse(request.idempotencyKey(), response);
        return response;
    }

    private TransferResponse tryLoadIdempotentResponse(String key) {
        return jdbcTemplate.query(
                "SELECT response_json FROM idempotency_keys WHERE key = ?",
                rs -> {
                    if (!rs.next()) return null;
                    try {
                        return objectMapper.readValue(rs.getString("response_json"), TransferResponse.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                key
        );
    }

    private void storeIdempotentResponse(String key, TransferResponse response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            jdbcTemplate.update(
                    "INSERT INTO idempotency_keys(key, response_json) VALUES (?, ?::jsonb) ON CONFLICT DO NOTHING",
                    key, json
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
```

## 14.8 Controller

```java
package com.example.demo.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transferService.transfer(request));
    }
}
```

## 14.9 Serializable transaction with retry

For higher correctness flows, retry on serialization failure.

```java
package com.example.demo.account;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TransferRetryFacade {

    private final TransferServiceSerializable transferServiceSerializable;

    public TransferResponse transferWithRetry(TransferRequest request) {
        int maxRetries = 3;
        RuntimeException last = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return transferServiceSerializable.transferSerializable(request);
            } catch (RuntimeException ex) {
                last = ex;
                String message = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
                boolean retryable = message.contains("could not serialize") ||
                        message.contains("serialization") ||
                        ex instanceof CannotAcquireLockException;

                if (!retryable || attempt == maxRetries) {
                    throw ex;
                }

                try {
                    Thread.sleep(25L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw ex;
                }
            }
        }

        throw last;
    }
}
```

```java
package com.example.demo.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferServiceSerializable {

    private final AccountRepository accountRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransferResponse transferSerializable(TransferRequest request) {
        Account from = accountRepository.findById(request.fromAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        Account to = accountRepository.findById(request.toAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        BigDecimal amount = request.amount();
        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);

        return new TransferResponse(from.getId(), to.getId(), amount, "SUCCESS");
    }
}
```

## 14.10 Optimistic locking example in Spring Boot

Because `Account` has `@Version`, concurrent updates can fail with optimistic locking.

```java
@Transactional
public void renameAccount(Long id, String ownerName) {
    Account account = accountRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));

    account.setOwnerName(ownerName);
    accountRepository.save(account);
}
```

If another transaction updates the same row first, Spring throws an optimistic locking exception.

## 14.11 Native PostgreSQL UPSERT from Spring Boot

```java
package com.example.demo.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserWriteRepository {

    private final JdbcTemplate jdbcTemplate;

    public void upsertUser(String email, String name) {
        jdbcTemplate.update("""
            INSERT INTO users(email, name, updated_at)
            VALUES (?, ?, NOW())
            ON CONFLICT (email)
            DO UPDATE SET
                name = EXCLUDED.name,
                updated_at = NOW()
            """, email, name);
    }
}
```

## 14.12 Recommended production setup

- Flyway for schema migrations
- HikariCP in app
- PgBouncer in front of DB
- read/write split only when justified
- explicit indexes for real query paths
- idempotency keys for retried writes
- short transactions
- `EXPLAIN ANALYZE` before tuning

---

## 15) Strong interview answers by use case

### Payment system
- PostgreSQL primary store
- `Serializable` or row locks for transfers
- idempotency keys for retries
- audit log for money movement
- replicas for reads, primary for write-after-read consistency

### Inventory / e-commerce checkout
- `UPDATE ... WHERE quantity > 0`
- non-negative constraint
- maybe `SELECT ... FOR UPDATE` for hot rows
- indexes on `(product_id)` and order tables

### Booking system
- exclusion constraint on `(resource_id, time_range)`
- transaction to insert reservation + payment hold
- replicas for search, primary for booking commit

### Multi-tenant SaaS
- PostgreSQL with tenant-aware schema or `tenant_id`
- row-level security if needed
- indexes prefixed by `tenant_id`
- partitioning when tenants or time-series tables get large

---

## 16) Final interview summary

If asked “Why PostgreSQL?” say:

> “Because this system needs relational modeling, transactional correctness, and flexible querying. PostgreSQL gives me ACID guarantees, strong SQL support, JSONB when needed, and a mature operational model. I’d handle scale with proper indexing, PgBouncer, read replicas, partitioning, and strict transaction design for critical flows.”

If asked “What are the trade-offs?” say:

> “It’s not the easiest database for huge horizontal scale or extreme append-only workloads. Connections are expensive, so pooling matters. Replicas can lag. Stronger isolation can reduce throughput. But for transactional systems, those trade-offs are often worth it.”

---

## 17) Memorize these one-liners

- **PostgreSQL is the default choice for transactional relational systems.**
- **Use PgBouncer because PostgreSQL connections are expensive.**
- **Use B-tree by default, GIN for JSONB/arrays/full-text, GiST for overlap/geospatial, BRIN for huge time-ordered tables.**
- **Use row locks, serializable transactions, or constraints to prevent race conditions.**
- **Use replicas for scaling reads, but primary for correctness-sensitive reads.**
- **Partition by the field most queries filter on.**
- **Always verify performance with `EXPLAIN ANALYZE`, not guesses.**

