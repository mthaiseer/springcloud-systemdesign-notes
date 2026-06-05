# 015_Transactions_ACID_MVCC.md

# MiniDatabase — 015 Transactions ACID MVCC

## 0. Why This File Exists

Databases must keep data correct when:

```text
many users read/write at same time
server crashes
transactions partially fail
money moves between accounts
orders and payments update together
```

This file teaches:

```text
transactions
ACID
isolation levels
dirty read
non-repeatable read
phantom read
lost update
MVCC
snapshot visibility
row versions
vacuum / cleanup
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
A transaction is a group of database operations that must behave like one safe unit.
```

Example:

```text
Transfer money from A to B.
```

Both debit and credit must succeed together.

---

# 2. Biggest Mental Model

Without transaction:

```text
operation-1 succeeds
operation-2 fails
database becomes inconsistent
```

With transaction:

```text
all operations succeed
or
all operations rollback
```

---

# 3. Money Transfer Example

Transfer 100 from account A to account B:

```sql
UPDATE accounts SET balance = balance - 100 WHERE id = 'A';
UPDATE accounts SET balance = balance + 100 WHERE id = 'B';
```

If first succeeds and second fails:

```text
money disappears
```

Transaction prevents this.

---

# 4. Transaction Flow

```text
BEGIN
  ↓
operation-1
  ↓
operation-2
  ↓
COMMIT
```

Failure:

```text
BEGIN
  ↓
operation-1
  ↓
operation-2 fails
  ↓
ROLLBACK
```

---

# 5. Transaction ASCII

```text
BEGIN TRANSACTION
        ↓
Deduct 100 from A
        ↓
Add 100 to B
        ↓
COMMIT
```

Failure case:

```text
BEGIN TRANSACTION
        ↓
Deduct 100 from A
        ↓
Add 100 to B fails
        ↓
ROLLBACK
        ↓
A remains unchanged
B remains unchanged
```

---

# 6. ACID

Transactions provide:

```text
A = Atomicity
C = Consistency
I = Isolation
D = Durability
```

---

# 7. ACID Mental Model

```text
Atomicity   = all or nothing
Consistency = valid rules preserved
Isolation  = concurrent transactions don't corrupt each other
Durability = committed data survives crash
```

---

# 8. Atomicity

Atomicity means:

```text
all operations succeed
or none take effect
```

Example:

```text
debit A
credit B
```

Both or none.

---

# 9. Atomicity ASCII

```text
Transaction
│
├── Step-1 debit A
├── Step-2 credit B
└── Commit
```

If Step-2 fails:

```text
rollback Step-1
```

Final state remains safe.

---

# 10. Consistency

Consistency means:

```text
database moves from one valid state to another valid state
```

Rules remain true.

Examples:

```text
balance cannot be negative
foreign key must point to valid row
unique email must stay unique
order must have valid user
```

---

# 11. Consistency Example

Constraint:

```text
balance >= 0
```

Transaction tries:

```text
balance = -100
```

Database rejects it.

---

# 12. Isolation

Isolation means:

```text
concurrent transactions should not interfere incorrectly
```

Example:

```text
two users update same balance at same time
```

Without isolation:

```text
lost update
dirty read
phantom read
```

---

# 13. Durability

Durability means:

```text
once committed, data survives crash
```

Implemented using:

```text
WAL
redo log
fsync
replication sometimes
```

---

# 14. Durability ASCII

```text
COMMIT
  ↓
WAL flushed to disk
  ↓
crash happens
  ↓
recovery replays WAL
  ↓
committed data restored
```

---

# 15. Why Isolation Is Hard

Multiple transactions run at the same time.

Example:

```text
T1 reads account
T2 updates account
T1 reads again
```

What should T1 see?

This depends on:

```text
isolation level
```

---

# 16. Common Concurrency Problems

```text
dirty read
non-repeatable read
phantom read
lost update
write skew
```

---

# 17. Dirty Read

Dirty read means:

```text
transaction reads uncommitted data from another transaction
```

Dangerous.

---

# 18. Dirty Read ASCII

```text
T1: UPDATE balance = 500
    not committed yet

T2: READ balance = 500

T1: ROLLBACK

T2 saw data that never really committed
```

---

# 19. Non-Repeatable Read

Non-repeatable read means:

```text
same row read twice gives different result
```

because another transaction committed update between reads.

---

# 20. Non-Repeatable Read ASCII

```text
T1: SELECT balance → 100

T2: UPDATE balance = 200
T2: COMMIT

T1: SELECT balance → 200
```

T1 read same row twice but got different values.

---

# 21. Phantom Read

Phantom read means:

```text
same query returns extra/missing rows
```

because another transaction inserted/deleted matching rows.

---

# 22. Phantom Read ASCII

```text
T1: SELECT * FROM orders WHERE amount > 100
    returns 5 rows

T2: INSERT order amount=200
T2: COMMIT

T1: SELECT * FROM orders WHERE amount > 100
    returns 6 rows
```

New row is a phantom.

---

# 23. Lost Update

Lost update means:

```text
two transactions overwrite each other
```

---

# 24. Lost Update ASCII

Initial:

```text
balance = 100
```

```text
T1 reads 100
T2 reads 100

T1 adds 50 → writes 150
T2 subtracts 20 → writes 80
```

Expected:

```text
130
```

Actual:

```text
80
```

T1 update lost.

---

# 25. Isolation Levels

Common SQL isolation levels:

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
```

---

# 26. Isolation Level Table

| Isolation Level | Dirty Read | Non-Repeatable Read | Phantom Read |
|---|---|---|---|
| Read Uncommitted | Possible | Possible | Possible |
| Read Committed | Prevented | Possible | Possible |
| Repeatable Read | Prevented | Prevented | DB-dependent |
| Serializable | Prevented | Prevented | Prevented |

---

# 27. Read Committed

Read Committed means:

```text
only committed data is visible
```

But repeated reads may see different committed versions.

---

# 28. Read Committed ASCII

```text
T1: SELECT balance → 100

T2: UPDATE balance=200
T2: COMMIT

T1: SELECT balance → 200
```

Allowed in Read Committed.

---

# 29. Repeatable Read

Repeatable Read means:

```text
same row read multiple times remains same within transaction
```

T1 sees stable snapshot.

---

# 30. Repeatable Read ASCII

```text
T1 starts
T1 reads balance → 100

T2 updates balance → 200
T2 commits

T1 reads balance again → 100
```

T1 sees old snapshot.

---

# 31. Serializable

Serializable means:

```text
transactions behave as if executed one after another
```

Strongest isolation.

But:

```text
more locking/conflict detection
lower concurrency
```

---

# 32. MVCC

MVCC means:

```text
Multi-Version Concurrency Control
```

Core idea:

```text
database keeps multiple versions of rows
```

Instead of overwriting immediately.

---

# 33. MVCC Biggest Mental Model

UPDATE in MVCC:

```text
does not simply overwrite old row
```

It creates:

```text
new row version
```

Old version may remain for old transactions.

---

# 34. MVCC Update ASCII

Before:

```text
Row Version V1
balance = 100
```

Update:

```text
balance = 150
```

After:

```text
V1: balance = 100  old version
V2: balance = 150  new version
```

Different transactions may see different versions.

---

# 35. Why MVCC Exists

Without MVCC:

```text
readers block writers
writers block readers
```

With MVCC:

```text
readers can read old version
writers create new version
```

Better concurrency.

---

# 36. Reader Writer MVCC ASCII

```text
T1 reading balance
        ↓
sees V1 balance=100

T2 updates balance
        ↓
creates V2 balance=150

T1 continues reading V1
New transactions see V2
```

No heavy reader/writer blocking.

---

# 37. Snapshot

Snapshot means:

```text
view of database at a point in time
```

Transaction reads data visible in its snapshot.

---

# 38. Snapshot ASCII

```text
Transaction T1 starts at time=10

Visible:
rows committed before time=10

Not visible:
rows committed after time=10
```

---

# 39. MVCC Row Metadata

MVCC rows often store metadata like:

```text
created_by_transaction
deleted_by_transaction
version timestamps
visibility flags
```

Postgres-style mental model:

```text
xmin = transaction that created row
xmax = transaction that deleted/replaced row
```

---

# 40. xmin / xmax ASCII

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

# 41. MVCC Visibility Rule

Transaction asks:

```text
is this row version visible to me?
```

Database checks:

```text
xmin committed before snapshot?
xmax absent or after snapshot?
```

If yes:

```text
row version visible
```

---

# 42. MVCC Visibility ASCII

```text
T1 snapshot = before T20

V1:
balance=100
xmin=T10
xmax=T20

T1 sees V1 because T20 is after T1 snapshot
```

New transaction after T20 sees newer version.

---

# 43. MVCC Delete

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

# 44. MVCC Delete ASCII

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

# 45. Vacuum / Cleanup

Old row versions cannot stay forever.

Database later removes:

```text
dead tuples
old versions
deleted rows
```

This cleanup is often called:

```text
vacuum
compaction
garbage collection
```

---

# 46. Vacuum ASCII

```text
Old versions:
V1 balance=100 dead
V2 balance=150 live

Vacuum:
remove V1
keep V2
```

---

# 47. MVCC Tradeoff

Benefits:

```text
non-blocking reads
better concurrency
snapshot isolation
```

Costs:

```text
old versions consume space
vacuum required
visibility checks cost CPU
long transactions delay cleanup
```

---

# 48. Long Transaction Problem

If a transaction stays open too long:

```text
old row versions cannot be removed
```

Because transaction may still need them.

This causes:

```text
table bloat
vacuum delay
storage growth
```

---

# 49. Long Transaction ASCII

```text
T1 starts and stays open
        ↓
many updates create old versions
        ↓
vacuum cannot remove old versions
        ↓
table grows
```

---

# 50. Transaction + WAL + MVCC Together

```text
Transaction
    ↓
creates row versions
    ↓
writes WAL records
    ↓
commits
    ↓
MVCC controls visibility
    ↓
WAL provides durability
```

---

# 51. Full System ASCII

```text
BEGIN
  ↓
UPDATE row
  ↓
Create new row version
  ↓
Write WAL
  ↓
COMMIT
  ↓
Readers choose visible version
  ↓
Vacuum cleans old versions later
```

---

# 52. Java Transaction Mental Model

```java
try {
    begin();

    debit(accountA, 100);
    credit(accountB, 100);

    commit();

} catch (Exception e) {
    rollback();
}
```

Core idea:

```text
commit all or rollback all
```

---

# 53. Java Mini Transaction Log

```java
import java.util.ArrayList;
import java.util.List;

public class MiniTransaction {

    private final List<Runnable> undoLog =
            new ArrayList<>();

    public void addUndo(Runnable undoAction) {
        undoLog.add(undoAction);
    }

    public void rollback() {

        for (int i = undoLog.size() - 1; i >= 0; i--) {
            undoLog.get(i).run();
        }
    }

    public void commit() {
        undoLog.clear();
    }
}
```

This is simplified.

Real databases use WAL, undo/redo logs, MVCC, locks.

---

# 54. Java Transfer Example

```java
public void transfer(Account a,
                     Account b,
                     int amount) {

    MiniTransaction tx =
            new MiniTransaction();

    try {
        int oldA = a.balance;
        int oldB = b.balance;

        tx.addUndo(() -> a.balance = oldA);
        tx.addUndo(() -> b.balance = oldB);

        a.balance -= amount;
        b.balance += amount;

        tx.commit();

    } catch (Exception e) {
        tx.rollback();
    }
}
```

---

# 55. Full Dry Run — Transaction

Initial:

```text
A = 1000
B = 500
```

Transfer:

```text
100 from A to B
```

Steps:

```text
BEGIN
A = 900
B = 600
COMMIT
```

Final:

```text
A = 900
B = 600
```

Failure case:

```text
BEGIN
A = 900
B update fails
ROLLBACK
```

Final:

```text
A = 1000
B = 500
```

---

# 56. Full Dry Run — MVCC

Initial:

```text
V1: balance=100
```

T1 starts:

```text
snapshot sees V1
```

T2 updates:

```text
V2: balance=150
T2 commits
```

T1 reads again:

```text
still sees V1 balance=100
```

New transaction T3:

```text
sees V2 balance=150
```

---

# 57. Production Notes

## Use transactions for:

```text
payments
orders
inventory
account updates
multi-row consistency
```

## Avoid long transactions because:

```text
lock holding
MVCC bloat
vacuum delay
replication lag
```

## Choose isolation carefully:

```text
higher isolation = safer but slower
lower isolation = faster but riskier
```

---

# 58. Interview Explanation

If interviewer asks:

```text
What is ACID?
```

Strong answer:

```text
ACID describes transaction guarantees: Atomicity means all-or-nothing,
Consistency means valid rules are preserved, Isolation means concurrent
transactions do not corrupt each other, and Durability means committed data
survives crashes.
```

If interviewer asks:

```text
What is MVCC?
```

Strong answer:

```text
MVCC is a concurrency control technique where databases keep multiple row
versions so readers can see a consistent snapshot while writers create new
versions, reducing reader-writer blocking.
```

---

# 59. Common Mistakes

## Mistake 1

```text
Thinking transaction only means commit
```

Transaction also includes rollback, isolation, durability.

---

## Mistake 2

```text
Ignoring isolation level
```

Different isolation levels allow different anomalies.

---

## Mistake 3

```text
Thinking UPDATE always overwrites row
```

In MVCC it often creates a new version.

---

## Mistake 4

```text
Keeping transactions open too long
```

Can cause locks and MVCC bloat.

---

## Mistake 5

```text
Thinking Serializable is always best
```

It is safest but may reduce concurrency.

---

# 60. Final Mental Model

```text
Transaction
    =
safe unit of work

ACID
    =
correctness guarantees

MVCC
    =
multiple row versions for concurrency

WAL
    =
durability and recovery
```

Full flow:

```text
BEGIN
  ↓
read/write rows
  ↓
create versions
  ↓
write WAL
  ↓
COMMIT / ROLLBACK
  ↓
visibility rules decide what each transaction sees
```

---

# 61. What To Remember

```text
Transactions protect correctness.

ACID = Atomicity, Consistency, Isolation, Durability.

Isolation prevents concurrency anomalies.

MVCC keeps multiple versions of rows.

Readers see snapshots.

Writers create new versions.

Old versions are cleaned later.

WAL makes committed changes durable.
```

---

# 62. Next File

```text
016_Isolation_Levels_Anomalies.md
```

Next you learn:

```text
Read Uncommitted
Read Committed
Repeatable Read
Serializable
Dirty Read
Non-repeatable Read
Phantom Read
Write Skew
Lost Update
```
