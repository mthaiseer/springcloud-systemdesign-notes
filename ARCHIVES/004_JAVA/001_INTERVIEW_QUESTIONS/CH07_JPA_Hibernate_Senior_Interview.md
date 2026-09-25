# CH 07 --- JPA & Hibernate

## Senior Java Distributed Backend Interview Series

**Target:** \~45 interview questions\
**Goal:** Master ORM/JPA/Hibernate internals, persistence context,
entity lifecycle, dirty checking, fetching, relationships, N+1, locking,
batching, and production performance/debugging.

------------------------------------------------------------------------

# Chapter Map

``` text
CH 07 — JPA / HIBERNATE (~45 Q)
|
+-- 1. ORM / JPA / Hibernate Fundamentals (8)
+-- 2. Persistence / Dirty Checking / Flush (9)
+-- 3. Relationships / Fetching (11)
+-- 4. Cascade / Locking / Concurrency (8)
+-- 5. Performance / Production (9)
```

# Big Picture

``` text
HTTP Request
     |
Controller
     |
Service Proxy
     |
@Transactional
     |
EntityManager
     |
Persistence Context
     |
Hibernate
     |
JDBC / HikariCP
     |
PostgreSQL
```

``` text
JPA       = specification/API contract
Hibernate = common JPA implementation
Database  = actual persistence engine
```

------------------------------------------------------------------------

# 1. ORM / JPA / Hibernate Fundamentals

## Q1. What is ORM?

ORM means Object-Relational Mapping.

``` text
JAVA OBJECT                     RELATIONAL DB

Order                           orders
+ id                            + id
+ status          <------>      + status
+ total                         + total
```

It maps classes to tables, objects to rows, fields to columns, and
object relationships to relational relationships.

Benefits include reduced repetitive JDBC code, change tracking,
relationship mapping and query abstractions. Costs include hidden SQL,
N+1 problems, memory overhead and abstraction leaks.

**Senior rule:** JPA does not remove the need to understand SQL.

## Q2. JPA vs Hibernate?

``` text
JPA
+ specification
+ annotations/interfaces/contracts

Hibernate
+ implementation/provider
+ ORM engine
+ provider-specific capabilities
```

JPA defines APIs such as `EntityManager`; Hibernate commonly implements
them underneath.

## Q3. What is EntityManager?

It is JPA's primary interface for interacting with a persistence
context.

Common operations:

``` text
persist
find
merge
remove
flush
clear
detach
createQuery
```

``` text
EntityManager
     |
Persistence Context
     |
+-- manages entities
+-- tracks changes
+-- identity map
+-- coordinates flush
```

In Spring applications, Spring normally manages EntityManager lifecycle
and transaction association.

## Q4. What are entity lifecycle states?

``` text
             persist()
TRANSIENT ----------------> MANAGED
                              |
                              | detach()/clear()
                              v
                           DETACHED
                              |
                              | merge()
                              v
                           MANAGED

MANAGED
   |
   | remove()
   v
REMOVED
```

Transient means not managed. Managed means tracked by the current
persistence context. Detached means previously managed but no longer
tracked. Removed means scheduled for deletion.

## Q5. What is the persistence context?

``` text
Persistence Context
|
+-- User#1  -> Java object A
+-- User#2  -> Java object B
+-- Order#7 -> Java object C
```

It provides identity management, first-level caching, dirty checking,
write-behind behavior and coordination with transactional work.

This is one of the most important JPA concepts.

## Q6. What is first-level cache?

Within one persistence context:

``` java
User a = entityManager.find(User.class, 1L);
User b = entityManager.find(User.class, 1L);
```

Conceptually:

``` text
first find User#1
       |
       v
     SQL
       |
       v
Persistence Context stores User#1

second find User#1
       |
       v
Persistence Context lookup
       |
       v
same managed representation
```

L1 cache is persistence-context local and fundamental to JPA behavior.

## Q7. What does entity identity mean?

``` text
Database identity: User ID 42

Persistence Context
       |
       +--> one managed representation
            for User#42
```

Entity `equals/hashCode` design requires care, especially with generated
identifiers. Avoid blindly including every mutable field.

## Q8. What makes a good entity design?

Typical goals:

``` text
Entity
|
+-- stable identity
+-- controlled mutability
+-- sensible relationship ownership
+-- deliberate fetching
+-- protected invariants
+-- careful equals/hashCode
```

Example:

``` java
@Entity
class Order {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    protected Order() {}

    public void cancel() {
        if (status == OrderStatus.SHIPPED) {
            throw new IllegalStateException();
        }
        status = OrderStatus.CANCELLED;
    }
}
```

------------------------------------------------------------------------

# 2. Persistence / Dirty Checking / Flush

## Q9. persist() vs merge()?

`persist()` makes a new/transient instance managed.

``` text
Transient
   |
 persist
   |
   v
Managed
```

`merge()` copies state into a managed instance and returns that managed
instance.

``` text
Detached object
      |
    merge
      |
      v
Managed instance returned
```

Do not assume the original detached instance becomes managed.

## Q10. What is dirty checking?

``` java
@Transactional
public void rename(long id, String name) {
    User user = entityManager.find(User.class, id);
    user.setName(name);
}
```

``` text
Load managed entity
      |
modify field
      |
Persistence Context tracks change
      |
FLUSH
      |
UPDATE SQL
```

No explicit update call is necessarily needed for a managed entity.

## Q11. What is flush?

Flush synchronizes pending persistence-context changes to the database.

``` text
Persistence Context
+ INSERT pending
+ UPDATE pending
+ DELETE pending
       |
       v
     FLUSH
       |
       v
 SQL executed
```

**FLUSH != COMMIT.**

## Q12. Flush vs commit?

``` text
BEGIN
  |
modify entity
  |
FLUSH
  |
SQL sent
  |
transaction remains active
  |
more work
  |
COMMIT
```

A later rollback can still undo flushed but uncommitted changes.

## Q13. When can Hibernate flush?

Typical situations include transaction commit, explicit `flush()`, and
before certain queries when synchronization is required. Exact behavior
depends on flush mode/provider behavior.

Do not assume SQL executes at the exact Java line where a field changes.

## Q14. Does Spring Data save() always execute SQL immediately?

No.

``` text
save()
  |
persistence operation
  |
managed/pending state
  |
flush
  |
SQL
  |
commit
```

`saveAndFlush()` requests a flush but still does not equal transaction
commit.

## Q15. Why are bulk updates dangerous to persistence-context consistency?

Bulk JPQL/SQL can bypass normal managed-entity dirty checking.

``` text
Persistence Context:
User#1 active=true

Bulk UPDATE
      |
      v

Database:
User#1 active=false

Persistence Context:
may still say active=true
```

Flush before and clear/refresh afterward when required by the use case.

## Q16. clear() vs detach()?

`detach(entity)` detaches one managed entity.

`clear()` detaches all entities in the persistence context.

``` text
Before clear
PC: User1, User2, Order1

After clear
PC: empty
```

This is useful in large batch processing.

## Q17. What does @Transactional(readOnly=true) mean?

It communicates read-only intent and may enable
framework/provider/database optimizations depending on configuration.

``` text
readOnly=true
    |
semantic hint / optimization opportunity
    |
NOT a universal security mechanism
```

------------------------------------------------------------------------

# 3. Relationships / Fetching

## Q18. How do you map one-to-one?

``` java
@OneToOne
@JoinColumn(name = "profile_id")
private Profile profile;
```

``` text
users.profile_id ------> profiles.id
```

Ask whether the domain truly requires one-to-one; do not create it
automatically.

## Q19. ManyToOne and OneToMany?

``` text
Customer 1 -------- N Orders
```

Many side:

``` java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "customer_id")
private Customer customer;
```

Inverse side:

``` java
@OneToMany(mappedBy = "customer")
private List<Order> orders = new ArrayList<>();
```

The FK commonly lives on the many side.

## Q20. Why often avoid direct ManyToMany?

Real join relationships often gain attributes.

``` text
UserRole
+ user_id
+ role_id
+ assigned_at
+ assigned_by
+ status
```

Then model:

``` text
User 1--N UserRole N--1 Role
```

This gives better lifecycle, querying and domain control.

## Q21. What is the owning side?

The owning side controls relationship persistence, typically the side
containing the FK mapping.

``` java
@ManyToOne
@JoinColumn(name = "customer_id")
private Customer customer;
```

Inverse:

``` java
@OneToMany(mappedBy = "customer")
private List<Order> orders;
```

Updating only the inverse collection may not update the DB relationship
as expected.

## Q22. What does mappedBy mean?

``` text
Customer.orders
    |
 inverse
    |
Order.customer
    |
 owning side
    |
orders.customer_id
```

`mappedBy = "customer"` says the relationship is mapped by the
`customer` field on `Order`.

## Q23. Lazy vs eager loading?

``` text
LAZY
load association when needed

EAGER
association expected to be loaded eagerly
```

But neither annotation is a complete performance strategy.

``` text
Use case
   |
What data is needed?
   |
Fetch exactly that
```

## Q24. What causes LazyInitializationException?

``` text
TX
 |
load Order
 |
customer remains lazy
 |
TX/context closes
 |
serializer accesses customer
 |
LazyInitializationException
```

Bad fix:

``` text
make everything EAGER
```

Better:

``` text
fetch needed graph in service boundary
       |
map DTO
       |
return DTO
```

## Q25. What is N+1?

``` text
1 query: load 100 orders

then:
100 queries to load customers

Total = 101 queries
```

``` text
Orders Query
    |
    +--> Order1 -> Customer query
    +--> Order2 -> Customer query
    +--> Order3 -> Customer query
    ...
```

This can destroy DB capacity and API p99.

## Q26. What is JOIN FETCH?

Example JPQL:

``` text
select o
from Order o
join fetch o.customer
where o.id = :id
```

``` text
Without fetch join:
Order query -> later Customer query

With fetch join:
Order JOIN Customer -> required graph together
```

Use it intentionally per use case.

## Q27. What is EntityGraph?

An EntityGraph declares associations to fetch for a particular query/use
case.

``` java
@EntityGraph(attributePaths = {"customer"})
Optional<Order> findById(Long id);
```

``` text
Order query
   |
EntityGraph
   +-- customer
   |
fetch selected graph
```

## Q28. Why use DTO projections?

If an endpoint only needs:

``` text
orderId
customerName
total
```

do not necessarily load a large managed entity graph.

``` text
DB
 |
select required columns
 |
DTO
 |
API
```

Benefits can include less transferred data, lower persistence-context
memory and fewer lazy-loading surprises.

------------------------------------------------------------------------

# 4. Cascade / Locking / Concurrency

## Q29. What is cascade?

Cascade propagates selected entity lifecycle operations.

``` text
PERSIST
MERGE
REMOVE
REFRESH
DETACH
ALL
```

Example:

``` java
@OneToMany(
    mappedBy = "order",
    cascade = CascadeType.ALL
)
private List<OrderItem> items;
```

Do not use `CascadeType.ALL` blindly across shared aggregate boundaries.

## Q30. What is orphanRemoval?

``` text
Order.items = [A,B,C]
remove B
      |
B becomes orphan
      |
DELETE B
```

It differs from deleting children because the parent itself was removed.

## Q31. How does optimistic locking work?

``` java
@Version
private long version;
```

``` text
Initial version = 5

TX A                    TX B
read v5                 read v5
update                   update

UPDATE WHERE v=5        UPDATE WHERE v=5
success                 0 rows -> conflict

new version=6
```

## Q32. What is @Version for?

Conceptual SQL:

``` sql
UPDATE product
SET stock = ?,
    version = ?
WHERE id = ?
  AND version = ?;
```

It detects concurrent modifications instead of silently overwriting
them.

## Q33. What is pessimistic locking?

``` java
entityManager.find(
    Product.class,
    id,
    LockModeType.PESSIMISTIC_WRITE
);
```

``` text
TX A locks row
      |
      +--> modifies
      |
      +--> commits
      |
TX B waits
```

It is useful when conflict likelihood/cost justifies blocking.

## Q34. How can lost updates be prevented?

Options include:

``` text
@Version
  -> detect conflict

Pessimistic lock
  -> serialize writers

Atomic SQL
  -> update with condition
```

Example:

``` sql
UPDATE product
SET stock = stock - 1
WHERE id = ?
  AND stock > 0;
```

Check affected-row count.

## Q35. Can JPA transactions deadlock?

Yes.

``` text
TX A locks Product1 -> waits Product2
TX B locks Product2 -> waits Product1
```

Mitigate with consistent access order, short transactions, good indexes
and safe deadlock retry where appropriate.

## Q36. Optimistic vs pessimistic: how choose?

``` text
Conflicts rare?
    -> optimistic often attractive

Conflicts frequent/costly?
    -> pessimistic may help
```

Also consider transaction duration, retry cost, latency, DB capacity and
business semantics.

------------------------------------------------------------------------

# 5. Performance / Production

## Q37. What is JDBC batching?

Without batching:

``` text
INSERT1 -> network
INSERT2 -> network
INSERT3 -> network
```

With batching:

``` text
INSERT1 INSERT2  +--> JDBC batch --> DB
INSERT3 /
```

This can reduce round trips. Hibernate batching also depends on
configuration and identifier strategy.

## Q38. How do you safely process large inserts?

Bad:

``` text
one transaction
 |
persist 1,000,000 entities
 |
Persistence Context keeps growing
```

Better conceptual pattern:

``` java
for (int i = 0; i < items.size(); i++) {
    entityManager.persist(items.get(i));

    if ((i + 1) % batchSize == 0) {
        entityManager.flush();
        entityManager.clear();
    }
}
```

``` text
batch -> flush -> clear -> next batch
```

For very large workloads, JDBC/bulk-loading may be more appropriate than
ORM.

## Q39. Offset vs keyset pagination?

Offset:

``` sql
SELECT ...
ORDER BY id
LIMIT 50 OFFSET 100000;
```

Keyset:

``` sql
SELECT ...
FROM orders
WHERE id > :lastId
ORDER BY id
LIMIT 50;
```

``` text
last seen key
     |
seek forward
```

Keyset is often more scalable for large ordered datasets, assuming a
suitable stable ordering.

## Q40. Why are collection fetch joins + pagination tricky?

A join duplicates parent rows:

``` text
Order1 Item1
Order1 Item2
Order1 Item3
Order2 Item1
Order2 Item2
```

SQL row pagination does not cleanly equal parent-entity pagination.

A common strategy:

``` text
1. page parent IDs
2. fetch required graph for those IDs
```

## Q41. What is Open Session in View?

Conceptually:

``` text
HTTP request
    |
Persistence Context remains available
    |
controller/serialization
    |
request ends
```

It can allow lazy queries during serialization, which may hide poor
fetch planning and N+1.

For many service APIs:

``` text
Service TX
  |
fetch required data
  |
map DTO
  |
return DTO
```

is easier to reason about.

## Q42. What is second-level cache?

``` text
PC A --+
PC B --+--> shared L2 cache
PC C --+
```

It is optional/provider-specific and shared beyond one persistence
context.

Challenges include invalidation, stale-data expectations, memory and
distributed topology.

## Q43. What is query cache?

Conceptually:

``` text
Query parameters
      |
cached result identifiers
      |
reuse if valid
```

It differs from entity L2 cache.

High-churn query results may have poor hit rate and high invalidation
cost. Measure before enabling.

## Q44. How do you see what Hibernate actually does?

Observe:

``` text
generated SQL
query count
query duration
bind values when safe
batch behavior
Hikari metrics
```

Useful sources include controlled SQL logging, Hibernate
metrics/statistics, APM/tracing, DB slow-query logs and `EXPLAIN`.

**Senior rule:** Diagnose actual SQL, not just annotations.

## Q45. How do you debug a slow JPA endpoint?

``` text
Endpoint p99 = 4s
      |
Trace request
      |
Repository = 3.5s?
      |
How many SQL queries?
      |
 +----+----+
 |         |
1 slow    500 small
 |         |
EXPLAIN    N+1
index      lazy loading
locks      poor fetch plan
```

Also inspect Hikari active/pending, transaction duration, DB CPU, locks,
rows scanned, result-set size, persistence-context size and
serialization.

------------------------------------------------------------------------

# Senior Scenario 1 --- N+1 in Production

``` java
List<Order> orders = orderRepository.findAll();

for (Order order : orders) {
    log.info(order.getCustomer().getName());
}
```

Potential:

``` text
1 Orders query
+
1000 Customer queries
=
1001 queries
```

Impact:

``` text
DB round trips ↑
connection occupancy ↑
DB CPU ↑
API p99 ↑
```

Possible fixes: fetch join, EntityGraph, DTO projection, batch fetching,
or endpoint query redesign.

------------------------------------------------------------------------

# Senior Scenario 2 --- EAGER "Fix" Makes It Worse

``` text
Order
 |
 + Customer
 |    + Address
 |
 + Items
      + Product
           + Category
```

Making every relationship eager can create huge object graphs, complex
joins, additional queries and memory pressure.

``` text
LazyInitializationException
      |
      X make all EAGER
      |
      v
Define use-case fetch plan
      |
      v
DTO
```

------------------------------------------------------------------------

# Senior Scenario 3 --- Persistence Context Growth

``` text
500,000 persisted entities
       |
       v
500,000 managed entities
       |
       v
heap ↑
GC ↑
dirty-check cost ↑
OOM risk ↑
```

Use appropriate batch boundaries with flush/clear or a lower-level bulk
persistence mechanism.

------------------------------------------------------------------------

# Senior Scenario 4 --- Optimistic Lock Conflict

``` text
Order version=8

User A                   User B
load v8                  load v8
change address           change status
save                     save

success                  conflict
version=9
```

Now decide business behavior: retry, reload/merge, or report a conflict.
Locking technology alone does not define product semantics.

------------------------------------------------------------------------

# Senior Scenario 5 --- Serialization Triggers SQL

``` text
Controller returns Entity
       |
JSON serializer
       |
getItems()
       |
lazy query
       |
getProduct()
       |
more queries
```

Better:

``` text
Service TX
  |
fetch/query
  |
map DTO
  |
return DTO
```

------------------------------------------------------------------------

# Senior Scenario 6 --- Bulk Update Leaves Stale State

``` text
Managed User: active=true
       |
Bulk DB update
       |
DB: active=false
       |
Managed User may remain active=true
```

Understand bulk DML semantics and clear/refresh when necessary.

------------------------------------------------------------------------

# Mini Coding Drill 1 --- Bidirectional Helper

``` java
@Entity
class Order {
    @OneToMany(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }
}
```

Keep both sides of the in-memory object graph logically consistent.

------------------------------------------------------------------------

# Mini Coding Drill 2 --- Fetch Join

``` java
@Query("""
    select o
    from Order o
    join fetch o.customer
    where o.id = :id
""")
Optional<Order> findWithCustomer(@Param("id") Long id);
```

Use case needs Order + Customer, so fetch them intentionally.

------------------------------------------------------------------------

# Mini Coding Drill 3 --- DTO Projection

``` java
public record OrderSummary(
    Long id,
    String customerName,
    BigDecimal total
) {}
```

Conceptual query:

``` text
select new OrderSummary(
    order.id,
    customer.name,
    order.total
)
```

Ideal for many read-model endpoints.

------------------------------------------------------------------------

# Mini Coding Drill 4 --- Optimistic Inventory

``` java
@Entity
class Inventory {
    @Id
    private Long productId;

    private int stock;

    @Version
    private long version;

    public void reserve() {
        if (stock <= 0) {
            throw new OutOfStockException();
        }
        stock--;
    }
}
```

For extremely hot inventory, repeated optimistic retries may be
inefficient; consider atomic SQL or another contention strategy.

------------------------------------------------------------------------

# Mini Coding Drill 5 --- Batch Processing

``` java
@Transactional
public void importRows(List<Row> rows) {
    int batchSize = 50;

    for (int i = 0; i < rows.size(); i++) {
        entityManager.persist(map(rows.get(i)));

        if ((i + 1) % batchSize == 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }
}
```

`flush/clear` controls persistence-context size; JDBC batching also
requires appropriate configuration.

------------------------------------------------------------------------

# Fetch Strategy Decision Tree

``` text
Need association?
     |
 +---+---+
 |       |
NO      YES
 |       |
lazy     |
         v
Need managed graph?
    +----+----+
    |         |
   NO        YES
    |         |
DTO/proj   fetch join /
           EntityGraph
```

------------------------------------------------------------------------

# N+1 Diagnostic Flow

``` text
API slow
  |
Count SQL
  |
expected 3, actual 503?
  |
repeated similar SQL?
  |
  v
 N+1
  |
  +--> fetch join
  +--> EntityGraph
  +--> DTO projection
  +--> query redesign
```

------------------------------------------------------------------------

# Persistence Context Visual

``` text
             TRANSACTION
                  |
          Persistence Context
        +----------------------+
        | User#1    MANAGED    |
        | User#2    MANAGED    |
        | Order#8   MANAGED    |
        +----------------------+
                  |
            dirty checking
                  |
                FLUSH
                  |
          SQL INSERT/UPDATE
                  |
               COMMIT
```

------------------------------------------------------------------------

# Entity Lifecycle Visual

``` text
new Entity
   |
TRANSIENT
   |
 persist
   |
MANAGED
   |
   +--> dirty checking -> flush
   |
   +--> detach/clear -> DETACHED
   |                      |
   |                    merge
   |                      |
   |                 MANAGED COPY
   |
   +--> remove -> REMOVED
```

------------------------------------------------------------------------

# JPA + Transaction Connection

``` text
@Transactional
      |
BEGIN
      |
Persistence Context
      |
Managed Entities
      |
Dirty Checking
      |
Flush
      |
SQL
      |
COMMIT
```

This directly builds on CH06.

------------------------------------------------------------------------

# JPA + Hikari Connection

``` text
Request
  |
Service Transaction
  |
EntityManager
  |
Hibernate
  |
JDBC
  |
HikariCP
  |
PostgreSQL
```

``` text
500 ORM queries
   |
DB round trips
   |
connection held longer
   |
Hikari occupancy
   |
API p99
```

------------------------------------------------------------------------

# JPA + JVM Connection

``` text
100,000 managed entities
        |
Heap usage ↑
        |
GC pressure ↑
        |
CPU / pauses ↑
        |
p99 latency ↑
```

------------------------------------------------------------------------

# Production Debugging --- Slow Repository

``` text
Repository slow
      |
How many SQL?
   +-- ONE  -> EXPLAIN / index / locks / rows
   |
   +-- MANY -> N+1 / lazy loading / repeated lookup
```

# Production Debugging --- LazyInitializationException

``` text
LazyInitializationException
        |
Where accessed?
        |
Outside persistence context?
        |
Why does caller need entity?
        |
Define DTO/fetch plan
```

# Production Debugging --- Memory Growth

``` text
Heap grows during batch
       |
many managed entities?
       |
long persistence context?
       |
flush / clear missing?
```

# Production Debugging --- Locking

``` text
API latency
   |
DB wait
   |
lock wait?
   |
who owns lock?
   |
why transaction long?
   +-- remote call
   +-- huge batch
   +-- pessimistic lock
   +-- poor access order
```

------------------------------------------------------------------------

# Critical Comparison Sheet

``` text
JPA                 Hibernate
specification       implementation/provider
```

``` text
persist             merge
new -> managed      state copied -> managed returned instance
```

``` text
FLUSH               COMMIT
send pending SQL    complete transaction
```

``` text
L1 cache            L2 cache
context-local       shared/optional
```

``` text
LAZY                EAGER
deferred load       eager association expectation

Neither replaces use-case fetch planning.
```

``` text
FETCH JOIN          DTO PROJECTION
managed graph       read-model fields
```

``` text
OPTIMISTIC          PESSIMISTIC
detect conflict     block competitor
```

------------------------------------------------------------------------

# Senior Rapid-Fire Follow-Ups

1.  ORM?
2.  JPA vs Hibernate?
3.  EntityManager?
4.  Entity lifecycle states?
5.  Persistence context?
6.  First-level cache?
7.  Entity identity?
8.  Entity equals/hashCode risks?
9.  persist vs merge?
10. Dirty checking?
11. Flush?
12. Flush vs commit?
13. When can Hibernate flush?
14. Does save() immediately execute SQL?
15. Bulk update stale state?
16. clear vs detach?
17. readOnly transaction?
18. Owning side?
19. mappedBy?
20. Lazy vs eager?
21. LazyInitializationException?
22. Why not make everything eager?
23. N+1?
24. How detect N+1?
25. Fetch join?
26. EntityGraph?
27. DTO projection?
28. Why avoid direct many-to-many?
29. Cascade?
30. orphanRemoval?
31. Optimistic locking?
32. @Version?
33. Pessimistic locking?
34. Lost update?
35. Deadlock?
36. Lock strategy?
37. JDBC batching?
38. Why flush/clear batches?
39. Offset vs keyset?
40. Fetch join + pagination issue?
41. OSIV?
42. L2 cache?
43. Query cache risks?
44. How inspect Hibernate SQL?
45. Debug a 4-second JPA endpoint.

------------------------------------------------------------------------

# Interview Checklist

``` text
FUNDAMENTALS
[ ] 01 ORM
[ ] 02 JPA vs Hibernate
[ ] 03 EntityManager
[ ] 04 Entity lifecycle
[ ] 05 Persistence Context
[ ] 06 First-level cache
[ ] 07 Entity identity
[ ] 08 Entity design

PERSISTENCE
[ ] 09 persist vs merge
[ ] 10 Dirty checking
[ ] 11 Flush
[ ] 12 Flush vs commit
[ ] 13 Flush timing
[ ] 14 save behavior
[ ] 15 Bulk DML
[ ] 16 clear / detach
[ ] 17 readOnly

RELATIONSHIPS / FETCHING
[ ] 18 OneToOne
[ ] 19 ManyToOne / OneToMany
[ ] 20 ManyToMany
[ ] 21 Owning side
[ ] 22 mappedBy
[ ] 23 Lazy / eager
[ ] 24 LazyInitializationException
[ ] 25 N+1
[ ] 26 JOIN FETCH
[ ] 27 EntityGraph
[ ] 28 DTO projection

CASCADE / LOCKING
[ ] 29 Cascade
[ ] 30 orphanRemoval
[ ] 31 Optimistic locking
[ ] 32 @Version
[ ] 33 Pessimistic locking
[ ] 34 Lost update
[ ] 35 Deadlocks
[ ] 36 Lock strategy

PERFORMANCE / PRODUCTION
[ ] 37 JDBC batching
[ ] 38 Batch inserts
[ ] 39 Pagination
[ ] 40 Fetch join + pagination
[ ] 41 OSIV
[ ] 42 Second-level cache
[ ] 43 Query cache
[ ] 44 SQL visibility
[ ] 45 Production methodology
```

------------------------------------------------------------------------

# Chapter 07 Visual Summary

``` text
                     JPA / HIBERNATE
                           |
          +----------------+----------------+
          |                                 |
     OBJECT MODEL                       DATABASE
          |                                 |
        Entity                           Table/Row
          |
    EntityManager
          |
 Persistence Context ★★★
          |
    +-----+-----+
    |           |
 Identity   Dirty Checking
    |           |
    +-----+-----+
          |
        Flush
          |
      Hibernate
          |
        JDBC
          |
     PostgreSQL
```

Performance chain:

``` text
Fetch strategy
      |
SQL count / quality
      |
DB latency
      |
Connection hold time
      |
Hikari occupancy
      |
API p99
```

------------------------------------------------------------------------

# Chapter 07 Exit Criteria

You are ready for **CH 08 --- SQL & Database** when you can:

1.  Explain JPA vs Hibernate.
2.  Draw entity lifecycle states.
3.  Explain persistence context and L1 cache.
4.  Explain dirty checking.
5.  Explain flush vs commit.
6.  Explain persist vs merge.
7.  Explain bulk-DML stale state.
8.  Explain owning side and mappedBy.
9.  Explain why LAZY/EAGER is not a full fetch strategy.
10. Diagnose LazyInitializationException.
11. Draw N+1.
12. Choose fetch join vs EntityGraph vs DTO projection.
13. Explain cascade vs orphanRemoval.
14. Explain optimistic locking with versioned SQL.
15. Explain pessimistic locking and deadlock risk.
16. Explain batching and flush/clear.
17. Explain offset vs keyset pagination.
18. Explain OSIV trade-offs.
19. Explain L1 vs L2/query cache.
20. Debug JPA -\> SQL -\> Hikari -\> DB -\> p99.

``` text
CH 07 JPA / HIBERNATE
        |
Entity Lifecycle
        |
Persistence Context ★★★
        |
Dirty Checking / Flush ★★★
        |
Relationships
        |
Fetch Strategy / N+1 ★★★
        |
Locking
        |
Batching / Performance
        |
READY FOR CH 08
SQL & DATABASE
```

------------------------------------------------------------------------

## Next Chapter

``` text
CH 08 — SQL & DATABASE (~50 Q)
|
+-- JOIN / GROUP BY / HAVING
+-- Subqueries / CTE
+-- Window functions
+-- B-tree / composite / covering indexes
+-- Selectivity
+-- EXPLAIN / query plans
+-- Transactions / isolation / deadlocks
+-- HikariCP
+-- Replication
+-- Partitioning
+-- Sharding
+-- Production DB troubleshooting
```
