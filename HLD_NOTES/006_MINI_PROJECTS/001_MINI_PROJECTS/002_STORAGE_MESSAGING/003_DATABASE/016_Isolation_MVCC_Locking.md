# 016_Isolation_MVCC_Locking.md

# MiniDatabase — 016 Isolation MVCC Locking

This file covers:

- Isolation Levels
- Dirty Read
- Non-Repeatable Read
- Phantom Read
- Lost Update
- MVCC
- Snapshot Isolation
- Shared/Exclusive Locks
- Row vs Table Locks
- Deadlocks
- 2PL
- Optimistic vs Pessimistic Locking
- SELECT FOR UPDATE
- PostgreSQL MVCC
- MySQL InnoDB Locking
- Java Examples
- Production Notes

Core Mental Model:

Transactions run concurrently.
Database uses:

Isolation Levels
MVCC
Locks
Deadlock Detection

to maintain correctness and concurrency.
