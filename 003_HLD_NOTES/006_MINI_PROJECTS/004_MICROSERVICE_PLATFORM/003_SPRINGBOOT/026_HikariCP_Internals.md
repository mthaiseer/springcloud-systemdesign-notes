# 026_HikariCP_Internals — The Database Connection Checkout Desk Model

## Core Mental Model

Do not imagine HikariCP as:

```text
HikariCP makes SQL faster.
```

That is not the real mental model.

The better mental model is:

> **HikariCP is a small, strict checkout desk for reusable database connections.**

```text
Application Threads
        |
        | borrow connection
        v
+-----------------------------+
| HikariCP Pool               |
|-----------------------------|
| idle connections            |
| active connections          |
| wait queue                  |
| timeout rules               |
| health checks               |
+-----------------------------+
        |
        | physical TCP/JDBC connections
        v
Database
```

This chapter teaches exactly one idea:

> **HikariCP protects your application and database by limiting, reusing, validating, and timing out database connections.**

If you remember only one sentence:

> **A request does not “open a DB connection”; it borrows one from HikariCP, uses it briefly, and returns it.**

---

## Why This Exists

Creating a real database connection is expensive.

A JDBC connection usually means:

```text
TCP connection
database authentication
session setup
network round trips
server-side memory
client-side objects
```

If every request opened a new connection:

```text
Request 1 -> open DB connection -> query -> close
Request 2 -> open DB connection -> query -> close
Request 3 -> open DB connection -> query -> close
```

Production would suffer:

```text
high latency
database connection storms
too much authentication overhead
too many TCP sockets
database max connection exhaustion
slow startup spikes
unpredictable p99 latency
```

So applications use a connection pool.

A pool keeps a limited number of reusable connections ready.

```text
Open once
Reuse many times
Limit total number
Block or timeout when exhausted
```

That is HikariCP.

---

## Problem Statement

Imagine an order service handling 500 requests per second.

Each request needs the database:

```text
Controller -> Service -> Repository -> Database
```

Without a pool:

```text
500 requests/sec
500 new connections/sec
database collapses
```

With unlimited connections:

```text
500 app threads
500 DB connections
database memory spikes
context switching increases
queries slow down
```

The core problem:

> **How can many application threads safely share a small number of expensive database connections without overwhelming the database?**

HikariCP solves this by acting as a controlled checkout desk:

```text
1. Keep a small pool of physical connections.
2. Let threads borrow connections.
3. Return connections after use.
4. Make waiting threads timeout if pool is exhausted.
5. Retire old/broken connections.
6. Expose metrics so production issues are visible.
```

---

## Real World Analogy

Imagine a hotel with 10 rental cars.

```text
Guests need cars.
Hotel owns only 10 cars.
Guests borrow a car.
Guests return the car.
If all cars are taken, new guests wait.
If waiting too long, they leave.
Old/broken cars are replaced.
```

Mapping:

```text
Hotel rental cars            HikariCP
-----------------            --------
Rental car                   JDBC connection
Guest                        application thread
Checkout desk                connection pool
Borrow car                   getConnection()
Return car                   close() returns to pool
All cars busy                pool exhausted
Wait limit                   connectionTimeout
Old car replaced             maxLifetime
Broken car check             validation/keepalive
```

Important:

> **Calling `connection.close()` usually does not close the physical DB connection. It returns the connection to the pool.**

That is the key hidden behavior.

---

## The One Mental Model

Think of HikariCP as a bounded resource manager.

```text
Database can safely handle N app connections.
So app pool size must be controlled.

Too small:
  threads wait, latency rises

Too large:
  database overloaded, all queries slow

Just right:
  database saturated but stable
```

Picture:

```text
Requests
  |
  v
App Threads
  |
  | need DB work
  v
+------------------------------------------------+
| HikariCP                                       |
|------------------------------------------------|
| maximumPoolSize = 20                           |
| active = connections currently borrowed        |
| idle = connections ready to borrow             |
| waiting = threads blocked for connection       |
| timeout = how long a thread waits              |
+------------------------------------------------+
  |
  v
Database
```

The pool is not a performance magic box.
It is a pressure valve.

---

## Core Concepts

## Physical Connection

A physical connection is the real JDBC connection to the database.

```text
App JVM socket <---- TCP ----> Database backend/session
```

It consumes resources on both sides.

## Pooled Connection

Hikari wraps physical connections.

When your code receives a connection, it receives a proxy/wrapper.

```text
Application sees: java.sql.Connection
Actually: Hikari proxy connection
```

When you call:

```java
connection.close();
```

Hikari intercepts it.

Instead of closing the physical connection:

```text
mark as idle
return to pool
make available for next thread
```

## Active Connection

A connection currently borrowed by application code.

```text
Thread has it.
Repository/JPA is using it.
Database query may be running.
```

## Idle Connection

A connection currently sitting in the pool ready to be borrowed.

```text
No thread owns it now.
It is ready for next request.
```

## maximumPoolSize

The maximum number of physical connections Hikari will keep for that pool.

```properties
spring.datasource.hikari.maximum-pool-size=20
```

This is one of the most important production settings.

## connectionTimeout

How long a thread waits for a connection before failing.

```properties
spring.datasource.hikari.connection-timeout=30000
```

If pool is exhausted:

```text
wait up to connectionTimeout
then throw SQLTransientConnectionException
```

## maxLifetime

How long a physical connection can live before Hikari retires it.

```properties
spring.datasource.hikari.max-lifetime=1800000
```

This prevents stale connections from living forever.

## idleTimeout

How long an idle connection may sit before being retired, if pool has more than minimum idle.

```properties
spring.datasource.hikari.idle-timeout=600000
```

## leakDetectionThreshold

A debugging feature.

```properties
spring.datasource.hikari.leak-detection-threshold=20000
```

If a connection is borrowed too long, Hikari logs a warning.

This does not automatically fix the leak.
It tells you where a connection was borrowed and not returned quickly.

---

## Internal Architecture

```text
Spring Boot
   |
   v
DataSource bean
   |
   v
HikariDataSource
   |
   v
HikariPool
   |
   +--> ConcurrentBag of pool entries
   |
   +--> HouseKeeper background task
   |
   +--> connection creation
   |
   +--> validation/retirement
   |
   +--> metrics
   |
   v
Database connections
```

In a Spring Boot JPA app:

```text
Repository
   |
   v
EntityManager
   |
   v
Hibernate Session
   |
   v
DataSource.getConnection()
   |
   v
HikariCP borrow connection
   |
   v
JDBC query
```

Your repository does not talk directly to Hikari in normal code.
Hibernate/JDBC asks the DataSource for a connection.

---

## Internal Working

When a request executes a repository method:

```java
@Transactional
public ProductResponse getProduct(Long id) {
    Product product = productRepository.findById(id).orElseThrow();
    return new ProductResponse(product.getId(), product.getName());
}
```

The hidden connection flow:

```text
1. Request enters controller.
2. Service method starts transaction.
3. Hibernate needs a JDBC connection.
4. Hibernate calls DataSource.getConnection().
5. Hikari checks if idle connection exists.
6. If yes, Hikari marks it active and returns proxy connection.
7. Hibernate executes SQL.
8. Transaction commits/rolls back.
9. Connection close is called.
10. Hikari intercepts close.
11. Connection returns to idle pool.
```

If no idle connection exists:

```text
1. Hikari checks active count.
2. If active < maximumPoolSize, create new connection.
3. If active == maximumPoolSize, thread waits.
4. If wait exceeds connectionTimeout, exception is thrown.
```

---

## Rich ASCII Diagram — Borrow and Return

```text
Before request:

+-----------------------------+
| Hikari Pool                 |
|-----------------------------|
| idle:  C1 C2 C3             |
| active: none                |
+-----------------------------+

Request thread needs DB
        |
        v
DataSource.getConnection()
        |
        v
Borrow C1

During query:

+-----------------------------+
| Hikari Pool                 |
|-----------------------------|
| idle:  C2 C3                |
| active: C1 -> Thread-17     |
+-----------------------------+

Query done, transaction ends
        |
        v
connection.close()
        |
        v
Hikari returns C1 to pool

After request:

+-----------------------------+
| Hikari Pool                 |
|-----------------------------|
| idle:  C1 C2 C3             |
| active: none                |
+-----------------------------+
```

---

## Rich ASCII Diagram — Pool Exhaustion

```text
maximumPoolSize = 3

Thread-1 borrows C1
Thread-2 borrows C2
Thread-3 borrows C3

+-----------------------------+
| Hikari Pool                 |
|-----------------------------|
| active: C1 C2 C3            |
| idle: none                  |
| waiting: none               |
+-----------------------------+

Thread-4 asks for connection
        |
        v
No idle connection
active == max
        |
        v
Thread-4 waits

+-----------------------------+
| Hikari Pool                 |
|-----------------------------|
| active: C1 C2 C3            |
| idle: none                  |
| waiting: Thread-4           |
+-----------------------------+

If C2 returns before timeout:
  Thread-4 gets C2

If not:
  Thread-4 gets connection timeout exception
```

---

## Step-by-Step Dry Run — Normal Flow

Configuration:

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.connection-timeout=30000
```

Request:

```text
GET /api/products/1001
```

Flow:

```text
1. Controller receives request.
2. Service method begins.
3. Repository method needs SQL.
4. Hibernate requests a connection.
5. Hikari sees 5 idle connections.
6. Hikari gives one connection to the thread.
7. SQL executes in 12 ms.
8. Transaction ends.
9. Hibernate closes connection.
10. Hikari returns it to idle.
11. Response is sent.
```

Pool metrics during request:

```text
active: 1
idle: 9
waiting: 0
```

Healthy.

---

## Step-by-Step Dry Run — Pool Exhaustion

Configuration:

```text
maximumPoolSize = 10
connectionTimeout = 30s
```

Production spike:

```text
100 request threads need DB at same time.
Each DB operation takes 2 seconds.
```

Flow:

```text
1. First 10 threads borrow all 10 connections.
2. Remaining 90 threads wait.
3. If a connection returns within 30 seconds, waiting thread proceeds.
4. If not, request fails with timeout.
```

Symptoms:

```text
HikariPool-1 - Connection is not available, request timed out after 30000ms
```

Important:

```text
This does not always mean Hikari is broken.
It means connections are held longer or demand exceeds pool capacity.
```

Possible causes:

```text
slow SQL
long transactions
connection leak
database locks
pool too small
too many app threads
database overloaded
```

---

## Step-by-Step Dry Run — Connection Leak

Bad code:

```java
public void badJdbcCode(DataSource dataSource) throws SQLException {
    Connection connection = dataSource.getConnection();
    PreparedStatement ps = connection.prepareStatement("select * from products");
    ps.executeQuery();

    // forgot connection.close()
}
```

Flow:

```text
1. Thread borrows connection C1.
2. Query executes.
3. Code forgets to close connection.
4. Hikari still thinks C1 is active.
5. Over time, more leaked connections accumulate.
6. Pool reaches maximumPoolSize.
7. New requests wait and timeout.
```

With Spring/JPA, this is less common if you let Spring manage transactions.
But leaks can still happen with manual JDBC misuse.

Correct style:

```java
try (Connection connection = dataSource.getConnection();
     PreparedStatement ps = connection.prepareStatement("select * from products")) {
    ps.executeQuery();
}
```

In Spring, prefer:

```java
JdbcTemplate
JpaRepository
@Transactional
```

They manage connection lifecycle for you.

---

## Java/Spring Boot Configuration Example

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shop
spring.datasource.username=shop
spring.datasource.password=secret

spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.leak-detection-threshold=20000
```

Meaning:

```text
maximum-pool-size=20
  At most 20 physical DB connections from this app instance.

minimum-idle=5
  Try to keep 5 idle connections ready.

connection-timeout=30000
  Wait up to 30 seconds for a connection.

idle-timeout=600000
  Retire idle connections after 10 minutes if above minimum.

max-lifetime=1800000
  Retire physical connection after 30 minutes.

leak-detection-threshold=20000
  Log warning if connection borrowed over 20 seconds.
```

Production caution:

```text
Do not copy numbers blindly.
Pool size depends on DB capacity, number of app instances, query time, and traffic.
```

---

## Spring Boot Example With Repository

Entity:

```java
@Entity
public class Product {

    @Id
    private Long id;

    private String name;

    private int stock;

    protected Product() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
```

Repository:

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

Service:

```java
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));

        return new ProductResponse(product.getId(), product.getName());
    }
}
```

Controller:

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return productService.getProduct(id);
    }
}
```

Internal execution:

```text
HTTP thread enters controller.
Service transaction begins.
Hibernate needs connection for SELECT.
Hikari gives connection.
SQL executes.
Transaction completes.
Connection returns to pool.
```

---

## Sequence Diagram

```text
Client
  |
  | GET /api/products/1001
  v
Controller
  |
  v
Service
  |
  | @Transactional begins
  v
Hibernate
  |
  | DataSource.getConnection()
  v
HikariCP
  |
  | borrow idle connection
  v
JDBC Connection
  |
  | SELECT ...
  v
Database
  |
  | rows returned
  v
Hibernate
  |
  | transaction ends
  | connection.close()
  v
HikariCP
  |
  | return connection to idle pool
  v
Client receives response
```

---

## Production Scale Example

Imagine Kubernetes deployment:

```text
Order service replicas: 10
Hikari maximumPoolSize per pod: 30
Postgres max_connections: 200
```

Potential connections:

```text
10 pods * 30 connections = 300 possible app connections
```

But database allows:

```text
200 total connections
```

Problem:

```text
If all pods scale up and fill pools, DB max_connections can be exhausted.
```

Senior calculation:

```text
Total app connection budget <= DB safe connection capacity

safe DB connections for app = 150
number of app pods = 10

max pool per pod <= 150 / 10 = 15
```

So:

```properties
spring.datasource.hikari.maximum-pool-size=15
```

This is why pool sizing must consider total replicas, not one JVM.

---

## Production Failure Story

A team had an order service.

Configuration:

```properties
spring.datasource.hikari.maximum-pool-size=50
```

Kubernetes replicas:

```text
20 pods
```

Database max connections:

```text
500
```

Potential app connections:

```text
20 * 50 = 1000
```

During traffic spike:

```text
Pods warmed up.
Hikari pools grew.
Postgres hit max_connections.
New connections failed.
Existing queries slowed.
Services started timing out.
Kubernetes scaled more pods.
More pods created more pools.
Database pressure got worse.
```

Root cause:

```text
Pool size was chosen per app instance, not per whole fleet.
Autoscaling multiplied connection demand.
```

Fix:

```text
Reduce maximumPoolSize per pod.
Use PgBouncer if appropriate.
Tune DB max_connections carefully.
Add metrics for active/idle/pending connections.
Limit app thread pools.
Optimize slow queries.
```

Lesson:

> **A connection pool is not only an application setting; it is a distributed database capacity contract.**

---

## Debugging Mindset

When Hikari problems happen, ask:

```text
1. Are connections waiting or active?
2. Is the pool exhausted?
3. Are queries slow?
4. Are transactions holding connections too long?
5. Is there a connection leak?
6. Is max pool too small for workload?
7. Is max pool too large for database?
8. How many app replicas exist?
9. Are database locks causing connection hold time?
10. Are external calls happening inside transactions?
```

### Symptom Map

```text
Connection timeout from Hikari
  -> pool exhausted
  -> slow SQL, leaks, long transactions, too small pool, DB locks

Database max connections reached
  -> too many pods * pool size
  -> missing global connection budget

High active connections
  -> DB work is slow or too much concurrent DB demand

High idle connections
  -> pool may be too large or traffic low

High pending/waiting threads
  -> connection bottleneck

Leak detection warning
  -> connection borrowed too long
  -> possible leak or long transaction
```

---

## Common Misconceptions

## Misconception 1 — “Increasing pool size always improves performance”

No.

Too many connections can make the database slower.

```text
More connections -> more DB concurrency
Too much concurrency -> more CPU switching, locks, memory, IO pressure
```

Sometimes reducing pool size improves stability.

## Misconception 2 — “Hikari timeout means database is down”

Not always.

It may mean all pool connections are busy.

Database may be up but overloaded, locked, or slow.

## Misconception 3 — “connection.close() closes the database socket”

In a pool, close usually returns the connection to the pool.

Physical close happens when Hikari retires or evicts the connection.

## Misconception 4 — “One request equals one connection forever”

Usually no.

A connection is borrowed only when needed and returned after transaction/session work.

But long transactions can hold it for a long time.

## Misconception 5 — “Pool size should equal request thread count”

No.

Most request time may be CPU, network, cache, or waiting on other services.
Pool size should match DB capacity and query workload, not blindly match web threads.

## Misconception 6 — “Connection leak only happens when using JDBC manually”

Manual JDBC is common cause.
But long transactions, streaming results, blocked threads, or incorrect transaction boundaries can also make connections appear stuck.

---

## Performance Considerations

HikariCP performance depends on:

```text
borrow time
connection hold time
query duration
transaction duration
pool size
database capacity
thread contention
```

Important formula:

```text
needed connections roughly = throughput * DB connection hold time
```

Example:

```text
200 requests/sec
average DB connection hold time = 50ms = 0.05s

needed active connections ≈ 200 * 0.05 = 10
```

If hold time becomes 500ms:

```text
200 * 0.5 = 100 active connections needed
```

This shows why slow queries explode pool demand.

The best way to reduce pool pressure:

```text
make DB work faster
shorten transactions
avoid unnecessary DB calls
remove N+1 queries
do not call external APIs inside transactions
```

Not simply:

```text
increase maximumPoolSize
```

---

## Scalability Considerations

At scale, pool sizing is fleet sizing.

```text
total possible DB connections =
number of app instances * maximumPoolSize
```

If autoscaling can create 30 pods:

```text
30 pods * 20 pool = 600 possible DB connections
```

You must compare with:

```text
database max_connections
reserved admin connections
other services
migration jobs
read replicas
pooler limits
```

Production pattern:

```text
DB safe app connections: 300
max app pods: 20
pool per pod: 300 / 20 = 15
```

If you need more throughput:

```text
optimize queries
use caching
use read replicas
use PgBouncer
split workloads
reduce transaction time
batch carefully
scale database
```

HikariCP is not a substitute for database design.

---

## Failure Investigation Playbook

## Step 1 — Read the exact error

Common:

```text
HikariPool-1 - Connection is not available, request timed out after 30000ms
```

This means:

```text
A thread waited connectionTimeout and did not get a connection.
```

## Step 2 — Check pool metrics

Look at:

```text
hikaricp.connections.active
hikaricp.connections.idle
hikaricp.connections.pending
hikaricp.connections.max
hikaricp.connections.timeout
```

Interpret:

```text
active high + pending high
  pool exhausted

idle high + timeout still happening
  possible different pool, config mismatch, or thread issue

active high + DB slow queries
  DB bottleneck

active high + leak warnings
  connection not returned quickly
```

## Step 3 — Check database

Inspect:

```text
slow queries
lock waits
active sessions
max_connections
CPU/IO
deadlocks
connection count by app
```

## Step 4 — Check transaction boundaries

Look for:

```java
@Transactional
public void processOrder() {
    orderRepository.save(order);
    paymentClient.charge(); // external call inside transaction: dangerous
    emailClient.send();     // holds DB connection longer
}
```

Better:

```text
Keep DB transaction short.
Use outbox/events for external side effects.
```

## Step 5 — Check fleet math

```text
replica count * maximumPoolSize <= DB safe app connection budget
```

If not, fix pool size or architecture.

---

## Interview Q&A

### Q1. What is HikariCP?

Strong answer:

> HikariCP is a JDBC connection pool. It keeps a limited set of reusable database connections and lets application threads borrow and return them instead of opening a new physical connection for every database operation.

### Q2. What happens when `DataSource.getConnection()` is called?

Strong answer:

> Hikari tries to provide an idle pooled connection. If none is idle and the pool is below `maximumPoolSize`, it may create a new physical connection. If the pool is already full, the thread waits up to `connectionTimeout`; then it fails if no connection becomes available.

### Q3. What does `connection.close()` do with HikariCP?

Strong answer:

> It usually does not close the physical database connection. Hikari intercepts the call and returns the connection to the pool for reuse.

### Q4. Why is increasing `maximumPoolSize` not always good?

Strong answer:

> Because the database has limited capacity. Too many concurrent connections can increase CPU context switching, memory use, lock contention, and query latency. Pool size must be aligned with database capacity and total number of app instances.

### Q5. How do you size a connection pool?

Strong answer:

> I consider DB capacity, number of application replicas, expected throughput, average connection hold time, query latency, and other services using the database. Fleet-wide possible connections must stay within the database’s safe connection budget.

### Q6. What causes Hikari connection timeout?

Strong answer:

> It means a thread waited for a connection and none became available before `connectionTimeout`. Common causes are slow queries, long transactions, connection leaks, lock waits, pool too small, or database overload.

### Q7. How do you debug pool exhaustion?

Strong answer:

> I check Hikari metrics: active, idle, pending, max, and timeout count. Then I correlate with database slow queries, locks, active sessions, transaction duration, leak detection logs, and replica-count times pool-size math.

---

## Production Checklist

```text
Configuration
[ ] Is maximumPoolSize explicitly set?
[ ] Is connectionTimeout reasonable?
[ ] Is maxLifetime lower than database/network connection kill time?
[ ] Is leakDetectionThreshold used only for debugging?

Fleet Math
[ ] replicas * maximumPoolSize <= DB safe app connection budget
[ ] autoscaling max replicas considered
[ ] other services sharing DB considered

Transaction Design
[ ] No external API calls inside DB transactions
[ ] No long-running work inside @Transactional
[ ] Read-only transactions used where appropriate
[ ] N+1 queries fixed

Observability
[ ] Active connections monitored
[ ] Idle connections monitored
[ ] Pending threads monitored
[ ] Timeout count monitored
[ ] DB locks and slow queries monitored

Failure Readiness
[ ] Hikari timeout alerts exist
[ ] DB max connection alerts exist
[ ] Runbook explains pool exhaustion
[ ] Load tests include realistic DB latency
```

---

## One-Page Cheat Sheet

```text
HikariCP Internals
==================

Core Idea
---------
Bounded checkout desk for reusable DB connections.

Borrow
------
Application calls DataSource.getConnection().
Hikari returns idle connection or creates one if below max.

Use
---
Hibernate/JDBC executes SQL.

Return
------
connection.close() returns connection to pool.

Pool States
-----------
Idle:
  ready to borrow

Active:
  currently borrowed

Pending:
  thread waiting for connection

Key Settings
------------
maximumPoolSize:
  max physical connections per app instance

connectionTimeout:
  max wait time for a connection

maxLifetime:
  retire old physical connections

idleTimeout:
  retire excess idle connections

leakDetectionThreshold:
  warn if connection held too long

Best Sentence
-------------
A request borrows a DB connection,
uses it briefly,
and returns it to HikariCP.

Debug Rules
-----------
Timeout?       Pool exhausted.
Active high?   Connections held too long.
Pending high?  Threads waiting.
Idle high?     Pool over-sized or low demand.
DB max hit?    replicas * pool too high.
```

---

## Last-Minute Interview Revision

Do not say:

```text
HikariCP improves database speed.
```

Say:

```text
HikariCP is a JDBC connection pool. It avoids opening a new physical database connection per request by maintaining a bounded pool of reusable connections. Threads borrow connections, use them briefly, and return them. Pool sizing protects both application latency and database capacity.
```

Senior version:

```text
I treat Hikari pool size as a database capacity contract across the whole fleet. I tune it using active/idle/pending metrics, query latency, transaction duration, and replica count rather than blindly increasing it during timeouts.
```

---

## One Picture To Remember

```text
                 APPLICATION REQUESTS
                         |
                         v
                 Need database work
                         |
                         v
        +----------------------------------+
        | HIKARICP CHECKOUT DESK          |
        |----------------------------------|
        | idle connections: ready cars     |
        | active connections: borrowed     |
        | waiting threads: queue           |
        | max pool size: car limit         |
        | timeout: max wait time           |
        +----------------------------------+
                         |
                         v
              PHYSICAL DATABASE CONNECTIONS
                         |
                         v
                      DATABASE
```

Final retention sentence:

> **HikariCP is the checkout desk that lets many app threads safely share a small, bounded set of reusable database connections.**
