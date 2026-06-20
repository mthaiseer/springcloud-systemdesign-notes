# 016_Isolation_MVCC_Locking.md

# MiniDatabase — 016 Isolation MVCC Locking

## 0. Why This File Exists

Transactions give us:

```text
BEGIN
COMMIT
ROLLBACK
ACID
MVCC basics
```

But real production databases must handle:

```text
many transactions running at the same time
```

Example systems:

```text
payments
bank transfers
ticket booking
stock trading
inventory updates
food delivery orders
hotel booking
wallet balance updates
```

If concurrency is not controlled:

```text
money disappears
inventory becomes negative
same seat is sold twice
same coupon is used multiple times
orders become inconsistent
```

This file teaches:

```text
isolation levels
dirty read
non-repeatable read
phantom read
lost update
write skew
MVCC visibility
shared locks
exclusive locks
row locks
table locks
gap locks
2PL
deadlocks
optimistic locking
pessimistic locking
SELECT FOR UPDATE
PostgreSQL MVCC mental model
MySQL InnoDB locking mental model
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
Isolation and locking decide what each transaction can see and modify
while many transactions run concurrently.
```

Simple meaning:

```text
many users can use database at same time
without corrupting data
```

---

# 2. Biggest Mental Model

Concurrent transactions need two things:

```text
Correctness
+
Performance
```

Databases achieve this using:

```text
MVCC
Locks
Isolation Levels
Deadlock Detection
```

---

# 3. Core Flow

```text
Transaction starts
      ↓
Database gives snapshot / visibility rules
      ↓
Reads use MVCC
      ↓
Writes acquire locks
      ↓
Conflicts handled
      ↓
Commit or rollback
```

---

# 4. Why Isolation Is Needed

Suppose:

```text
1 item left in stock
```

Two users buy at same time:

```text
T1 reads stock = 1
T2 reads stock = 1
T1 buys
T2 buys
```

Without isolation:

```text
stock becomes wrong
same item sold twice
```

---

# 5. Concurrency Problem Overview

Common problems:

```text
Dirty Read
Non-Repeatable Read
Phantom Read
Lost Update
Write Skew
Deadlock
```

You must know these for:

```text
backend interviews
database debugging
payments
orders
high-scale systems
```

---

# 6. Dirty Read

Dirty read means:

```text
one transaction reads uncommitted data from another transaction
```

This is dangerous because uncommitted data may rollback.

---

# 7. Dirty Read ASCII

```text
T1:
UPDATE accounts SET balance = 500
not committed yet

T2:
SELECT balance
gets 500

T1:
ROLLBACK
```

Problem:

```text
T2 saw value that never really existed
```

---

# 8. Dirty Read Mental Model

```text
Reading temporary data
that may disappear later
```

Most real databases prevent dirty reads.

---

# 9. Non-Repeatable Read

Non-repeatable read means:

```text
same row read twice inside same transaction returns different values
```

because another transaction committed update between reads.

---

# 10. Non-Repeatable Read ASCII

```text
T1:
SELECT balance WHERE id=1
→ 100

T2:
UPDATE balance = 200
COMMIT

T1:
SELECT balance WHERE id=1
→ 200
```

Problem:

```text
T1 read same row twice but got different value
```

---

# 11. Phantom Read

Phantom read means:

```text
same query returns different set of rows
```

because another transaction inserted/deleted rows matching condition.

---

# 12. Phantom Read ASCII

```text
T1:
SELECT * FROM orders WHERE amount > 100
→ 5 rows

T2:
INSERT order amount=200
COMMIT

T1:
SELECT * FROM orders WHERE amount > 100
→ 6 rows
```

The new row is a:

```text
phantom row
```

---

# 13. Lost Update

Lost update means:

```text
two transactions overwrite each other's changes
```

---

# 14. Lost Update ASCII

Initial:

```text
balance = 100
```

Execution:

```text
T1 reads 100
T2 reads 100

T1 adds 50
T1 writes 150

T2 subtracts 20
T2 writes 80
```

Expected:

```text
100 + 50 - 20 = 130
```

Actual:

```text
80
```

T1's update was lost.

---

# 15. Write Skew

Write skew is more advanced.

It happens when:

```text
two transactions read overlapping data
then update different rows
together they violate a business rule
```

---

# 16. Write Skew ASCII

Rule:

```text
At least one doctor must be on duty.
```

Initial:

```text
Doctor-A on duty = true
Doctor-B on duty = true
```

Concurrent transactions:

```text
T1 checks Doctor-B is on duty
T2 checks Doctor-A is on duty

T1 sets Doctor-A off duty
T2 sets Doctor-B off duty
```

Final:

```text
Doctor-A off
Doctor-B off
```

Business rule broken.

Serializable isolation prevents this.

---

# 17. Isolation Levels

SQL isolation levels:

```text
Read Uncommitted
Read Committed
Repeatable Read
Serializable
```

Higher isolation:

```text
more correctness
less concurrency
more overhead
```

---

# 18. Isolation Level Table

| Isolation Level | Dirty Read | Non-Repeatable Read | Phantom Read |
|---|---|---|---|
| Read Uncommitted | Possible | Possible | Possible |
| Read Committed | Prevented | Possible | Possible |
| Repeatable Read | Prevented | Prevented | DB-dependent |
| Serializable | Prevented | Prevented | Prevented |

---

# 19. Read Uncommitted

Weakest isolation.

Allows:

```text
dirty reads
```

Rarely used in serious production systems.

---

# 20. Read Committed

Read Committed means:

```text
each statement sees only committed data
```

But different statements inside same transaction may see different committed values.

---

# 21. Read Committed ASCII

```text
T1:
SELECT balance → 100

T2:
UPDATE balance=200
COMMIT

T1:
SELECT balance → 200
```

This is allowed in Read Committed.

---

# 22. Repeatable Read

Repeatable Read means:

```text
same row read multiple times stays stable inside transaction
```

Usually implemented with snapshot.

---

# 23. Repeatable Read ASCII

```text
T1 starts
T1 reads balance → 100

T2 updates balance → 200
T2 commits

T1 reads balance again → 100
```

T1 sees old snapshot.

---

# 24. Serializable

Serializable means:

```text
transactions behave as if executed one by one
```

Strongest isolation.

Best correctness.

But may reduce concurrency.

---

# 25. Serializable ASCII

Instead of true overlap:

```text
T1 and T2 concurrent
```

Database guarantees result is equivalent to:

```text
T1 then T2
```

or:

```text
T2 then T1
```

---

# 26. MVCC

MVCC means:

```text
Multi-Version Concurrency Control
```

Core idea:

```text
database keeps multiple row versions
```

instead of overwriting immediately.

---

# 27. MVCC Biggest Mental Model

Readers:

```text
read old versions
```

Writers:

```text
create new versions
```

This gives high concurrency.

---

# 28. Why MVCC Exists

Without MVCC:

```text
readers block writers
writers block readers
```

With MVCC:

```text
readers can continue using snapshot
writers create new version
```

---

# 29. MVCC Update ASCII

Before update:

```text
V1:
balance = 100
```

Update balance to 150:

```text
V1:
balance = 100   old version

V2:
balance = 150   new version
```

Different transactions may see different versions.

---

# 30. MVCC Reader Writer Example

```text
T1 starts and reads balance
        ↓
sees V1 balance=100

T2 updates balance
        ↓
creates V2 balance=150
COMMIT

T1 reads again
        ↓
still sees V1 balance=100

T3 starts later
        ↓
sees V2 balance=150
```

---

# 31. Snapshot

Snapshot means:

```text
view of database at a point in time
```

Transaction sees:

```text
rows committed before snapshot
```

and ignores:

```text
rows committed after snapshot
```

depending on isolation.

---

# 32. Snapshot ASCII

```text
T1 starts at time=10

Visible:
T1 can see rows committed before time=10

Invisible:
T1 cannot see rows committed after time=10
```

---

# 33. MVCC Metadata

MVCC row versions store metadata.

Postgres-style mental model:

```text
xmin = transaction that created row
xmax = transaction that deleted/replaced row
```

---

# 34. xmin xmax ASCII

```text
Row Version
+----------------------+
| balance = 100        |
| xmin = T10           |
| xmax = T20           |
+----------------------+
```

Meaning:

```text
created by T10
ended/replaced by T20
```

---

# 35. Visibility Rule

When transaction reads row version:

```text
Is xmin visible?
Is xmax invisible or empty?
```

If yes:

```text
row version visible
```

Otherwise:

```text
skip row version
```

---

# 36. MVCC Visibility ASCII

```text
T1 snapshot before T20

V1:
balance=100
xmin=T10
xmax=T20

T1 sees V1 because:
T10 committed before snapshot
T20 happened after snapshot
```

---

# 37. MVCC Delete

DELETE does not always remove row immediately.

It marks row version as deleted.

```text
DELETE row
    ↓
set xmax
    ↓
old snapshots may still see row
    ↓
new snapshots do not see row
```

---

# 38. MVCC Delete ASCII

Before:

```text
V1: user=Alice, xmax=null
```

Delete by T30:

```text
V1: user=Alice, xmax=T30
```

Old transaction:

```text
may still see Alice
```

New transaction:

```text
does not see Alice
```

---

# 39. Vacuum / Cleanup

Old versions cannot stay forever.

Database later removes:

```text
dead tuples
old versions
deleted rows
```

This cleanup is called:

```text
vacuum
garbage collection
compaction
```

depending on database.

---

# 40. Vacuum ASCII

```text
Before cleanup:

V1 balance=100 dead
V2 balance=150 live

Vacuum:

remove V1
keep V2
```

---

# 41. MVCC Tradeoff

Benefits:

```text
non-blocking reads
better concurrency
snapshot isolation
```

Costs:

```text
extra storage
visibility check overhead
vacuum needed
long transactions delay cleanup
```

---

# 42. Long Transaction Problem

Long transactions keep old snapshots alive.

This means:

```text
old row versions cannot be removed
```

---

# 43. Long Transaction ASCII

```text
T1 starts and stays open
        ↓
many updates happen
        ↓
old row versions accumulate
        ↓
vacuum cannot remove them
        ↓
table bloat
```

---

# 44. Locks

Locks prevent conflicting operations.

Main types:

```text
Shared Lock
Exclusive Lock
```

---

# 45. Shared Lock

Shared lock means:

```text
multiple readers allowed
writers blocked
```

---

# 46. Shared Lock ASCII

```text
T1 reads row → shared lock
T2 reads row → shared lock

Both allowed.

T3 wants update → waits
```

---

# 47. Exclusive Lock

Exclusive lock means:

```text
one writer only
other conflicting operations wait
```

---

# 48. Exclusive Lock ASCII

```text
T1 updates row
        ↓
exclusive lock acquired

T2 update same row
        ↓
waits
```

---

# 49. Lock Compatibility

| Lock A | Lock B | Compatible? |
|---|---|---|
| Shared | Shared | Yes |
| Shared | Exclusive | No |
| Exclusive | Shared | No |
| Exclusive | Exclusive | No |

---

# 50. Row Lock

Row lock locks:

```text
specific row only
```

Good concurrency.

---

# 51. Row Lock ASCII

```text
users table

Row-1 locked by T1
Row-2 free
Row-3 free
```

Other rows can still be updated.

---

# 52. Table Lock

Table lock locks:

```text
entire table
```

Lower concurrency.

---

# 53. Table Lock ASCII

```text
users table locked

Row-1 blocked
Row-2 blocked
Row-3 blocked
```

---

# 54. SELECT FOR UPDATE

`SELECT FOR UPDATE` locks selected rows for future update.

Example:

```sql
SELECT *
FROM accounts
WHERE id = 1
FOR UPDATE;
```

Meaning:

```text
I plan to update this row.
Nobody else should modify it now.
```

---

# 55. SELECT FOR UPDATE ASCII

```text
T1:
SELECT account 1 FOR UPDATE
        ↓
row locked

T2:
UPDATE account 1
        ↓
waits until T1 commits/rollbacks
```

---

# 56. Pessimistic Locking

Pessimistic locking assumes:

```text
conflicts likely
```

So it locks early.

Example:

```sql
SELECT ... FOR UPDATE
```

Good for:

```text
payments
inventory
ticket booking
wallet balance
```

---

# 57. Optimistic Locking

Optimistic locking assumes:

```text
conflicts are rare
```

Instead of locking early:

```text
check version at update time
```

---

# 58. Optimistic Locking ASCII

Initial:

```text
row version = 5
```

Transaction reads:

```text
version=5
```

Before update:

```text
WHERE id=1 AND version=5
```

If row still version 5:

```text
update succeeds
version becomes 6
```

If changed:

```text
update affects 0 rows
conflict detected
```

---

# 59. Optimistic vs Pessimistic

| Type | Idea | Good For |
|---|---|---|
| Optimistic | check conflict later | read-heavy systems |
| Pessimistic | lock early | high contention systems |

---

# 60. Two Phase Locking 2PL

2PL means:

```text
Growing phase:
acquire locks

Shrinking phase:
release locks
```

Once locks start releasing:

```text
cannot acquire new locks
```

---

# 61. 2PL ASCII

```text
Acquire Lock-A
Acquire Lock-B
Do work
Release Lock-A
Release Lock-B
```

This helps ensure serializable behavior.

---

# 62. Deadlock

Deadlock means:

```text
transactions wait forever on each other
```

---

# 63. Deadlock ASCII

```text
T1 holds Lock-A
T1 waits for Lock-B

T2 holds Lock-B
T2 waits for Lock-A
```

Cycle:

```text
T1 → T2 → T1
```

No progress.

---

# 64. Wait-For Graph

Database models waits:

```text
T1 waits for T2
T2 waits for T1
```

Graph:

```text
T1 ───▶ T2
▲       │
└───────┘
```

Cycle means:

```text
deadlock
```

---

# 65. Deadlock Detection

Database detects cycle and aborts one transaction.

```text
deadlock detected
        ↓
choose victim
        ↓
rollback victim
        ↓
release locks
        ↓
other transaction continues
```

---

# 66. PostgreSQL MVCC Mental Model

PostgreSQL uses:

```text
MVCC
xmin/xmax
snapshots
VACUUM
row versions
```

Important behavior:

```text
readers usually do not block writers
writers do not block readers
```

But writers can block other writers.

---

# 67. PostgreSQL MVCC ASCII

```text
UPDATE row
    ↓
new tuple version created
    ↓
old tuple remains temporarily
    ↓
snapshots choose visible version
    ↓
VACUUM removes dead tuple later
```

---

# 68. MySQL InnoDB Locking Mental Model

InnoDB uses:

```text
MVCC
record locks
gap locks
next-key locks
undo logs
```

Gap locks help prevent phantom rows in some isolation levels.

---

# 69. Gap Lock

Gap lock locks:

```text
space between index values
```

Example index:

```text
10 20 30
```

Gap lock:

```text
lock between 10 and 20
```

Insert 15 waits.

---

# 70. Gap Lock ASCII

```text
Index:
10 ---- gap ---- 20 ---- gap ---- 30

Transaction locks gap 10-20

INSERT 15
    ↓
waits
```

---

# 71. Java Optimistic Lock Example

```java
public class Account {

    private int balance;
    private int version;

    public synchronized boolean update(
            int expectedVersion,
            int newBalance) {

        if (version != expectedVersion) {
            return false;
        }

        balance = newBalance;
        version++;

        return true;
    }
}
```

Mental model:

```text
update only if version did not change
```

---

# 72. Java Pessimistic Lock Example

```java
import java.util.concurrent.locks.ReentrantLock;

public class AccountService {

    private final ReentrantLock lock =
            new ReentrantLock();

    public void transfer() {

        lock.lock();

        try {
            updateBalance();
        } finally {
            lock.unlock();
        }
    }

    private void updateBalance() {
        // critical section
    }
}
```

---

# 73. Spring Transaction Example

```java
@Transactional
public void transfer(Long fromId,
                     Long toId,
                     int amount) {

    Account from =
            accountRepository.findById(fromId).orElseThrow();

    Account to =
            accountRepository.findById(toId).orElseThrow();

    from.debit(amount);
    to.credit(amount);
}
```

Database internally uses:

```text
transaction
MVCC
locks
WAL
commit/rollback
```

---

# 74. Spring SELECT FOR UPDATE Example

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select a from Account a where a.id = :id")
Account findByIdForUpdate(Long id);
```

Meaning:

```text
lock account row before updating
```

Useful for:

```text
wallet balance
inventory
payment settlement
```

---

# 75. Full Dry Run — Lost Update Prevention

Initial:

```text
stock = 1
```

Without locking:

```text
T1 reads 1
T2 reads 1
T1 buys → stock=0
T2 buys → stock=0
```

Two buyers succeeded.

With lock:

```text
T1 SELECT FOR UPDATE stock row
T2 waits

T1 stock=1, buys, stock=0, commit

T2 resumes
T2 sees stock=0
T2 cannot buy
```

Correct.

---

# 76. Full Dry Run — MVCC Snapshot

Initial:

```text
V1 balance=100
```

T1 starts:

```text
snapshot sees V1
```

T2 updates:

```text
V2 balance=150
T2 commits
```

T1 reads again:

```text
still sees V1 balance=100
```

T3 starts later:

```text
sees V2 balance=150
```

---

# 77. Production Notes

Use Read Committed for:

```text
general OLTP systems
```

Use Repeatable Read for:

```text
stable reads inside transaction
```

Use Serializable for:

```text
strict correctness
rare high-value critical workflows
```

Use SELECT FOR UPDATE for:

```text
wallets
inventory
seat booking
payment state transitions
```

Avoid long transactions because they cause:

```text
lock contention
deadlocks
MVCC bloat
replication lag
vacuum delay
```

---

# 78. Common Slow/Failure Patterns

```text
long transaction holding locks
missing index causing many row locks
deadlocks from inconsistent lock order
high contention on same row
serializable retries
gap locks blocking inserts
```

---

# 79. Lock Ordering Rule

To reduce deadlocks:

```text
always lock resources in same order
```

Bad:

```text
T1 locks A then B
T2 locks B then A
```

Good:

```text
T1 locks A then B
T2 locks A then B
```

---

# 80. Interview Explanation — Isolation

If interviewer asks:

```text
What is isolation?
```

Strong answer:

```text
Isolation controls how concurrent transactions see each other's changes.
Different isolation levels allow or prevent anomalies such as dirty reads,
non-repeatable reads, phantom reads, lost updates, and write skew.
```

---

# 81. Interview Explanation — MVCC

If interviewer asks:

```text
What is MVCC?
```

Strong answer:

```text
MVCC is a concurrency control mechanism where the database keeps multiple
row versions. Readers use snapshots to see a consistent version of data,
while writers create new versions, reducing reader-writer blocking.
```

---

# 82. Interview Explanation — Deadlock

If interviewer asks:

```text
What is deadlock?
```

Strong answer:

```text
Deadlock occurs when transactions wait on each other in a cycle.
Databases detect cycles in a wait-for graph and usually abort one
transaction to release locks.
```

---

# 83. Common Mistakes

## Mistake 1

```text
Thinking MVCC means no locking
```

Wrong.

MVCC reduces read/write blocking but writes still need locks.

---

## Mistake 2

```text
Using Serializable everywhere
```

Safer but may reduce throughput.

---

## Mistake 3

```text
Ignoring deadlocks
```

Deadlocks are normal in high-concurrency systems and must be retried.

---

## Mistake 4

```text
Keeping transactions open too long
```

This causes lock contention and MVCC bloat.

---

## Mistake 5

```text
Thinking Read Committed gives stable reads
```

It does not guarantee repeatable reads.

---

# 84. Final Mental Model

```text
Isolation Levels
    =
what each transaction can see

MVCC
    =
multiple row versions for snapshots

Locks
    =
protect conflicting writes

Deadlock Detection
    =
break circular waits
```

Full flow:

```text
Transaction starts
      ↓
snapshot / isolation rules apply
      ↓
reads use MVCC visibility
      ↓
writes acquire locks
      ↓
conflicts wait or retry
      ↓
commit / rollback
```

---

# 85. What To Remember

```text
Isolation prevents concurrency anomalies.

MVCC keeps multiple row versions.

Readers often do not block writers.

Writers still need locks.

Shared locks allow multiple readers.

Exclusive locks allow one writer.

Deadlocks are circular waits.

SELECT FOR UPDATE locks rows for updates.

Long transactions are dangerous.

Serializable is strongest but costlier.
```

---

# 86. Next File

```text
017_Replication_Sharding.md
```

Next you learn:

```text
leader-follower replication
read replicas
sync vs async replication
replication lag
sharding
partitioning
consistent hashing
hot shard problem
```
