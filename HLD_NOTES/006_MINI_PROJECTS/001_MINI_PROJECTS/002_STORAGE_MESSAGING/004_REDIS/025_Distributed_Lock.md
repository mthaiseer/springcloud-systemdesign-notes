# MiniRedis Phase 25 — Distributed Lock (Rich Version)

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Distributed Lock Exists](#2-why-distributed-lock-exists)
- [3. Race Condition Problem](#3-race-condition-problem)
- [4. Lock Mental Model](#4-lock-mental-model)
- [5. Why TTL Is Critical](#5-why-ttl-is-critical)
- [6. Why Owner Token Is Critical](#6-why-owner-token-is-critical)
- [7. Deep Internal Data Structure Explanation](#7-deep-internal-data-structure-explanation)
- [8. Complete Java Code](#8-complete-java-code)
- [9. Step-by-Step Dry Run](#9-step-by-step-dry-run)
- [10. Internal Memory Visualization](#10-internal-memory-visualization)
- [11. Complexity Analysis](#11-complexity-analysis)
- [12. Real Production Use Cases](#12-real-production-use-cases)
- [13. Redis Production Internals](#13-redis-production-internals)
- [14. Failure Cases And Bottlenecks](#14-failure-cases-and-bottlenecks)
- [15. Interview Questions](#15-interview-questions)
- [16. Final Mental Model](#16-final-mental-model)

---

# 1. Goal

In this phase, we build:

```text
Distributed Lock
```

Main objective:

```text
Allow only ONE client at a time
to execute a critical operation.
```

Mental model:

```text
global mutex across distributed systems
```

Real-world analogy:

```text
One hotel room key.

Only one person can own the key at a time.
```

If another person tries:

```text
access denied
```

Distributed lock works exactly like this.

Commands conceptually involved:

```text
SETNX
EXPIRE
LOCK
UNLOCK
```

Example:

```text
LOCK order:1 token-abc EX 30
```

Meaning:

```text
Acquire lock on order:1
for 30 seconds
```

Production systems using distributed locks:

```text
payment processing
inventory reservation
cron jobs
leader election
job scheduling
distributed transactions
```

---

# 2. Why Distributed Lock Exists

Without distributed locking:

```text
multiple servers may update same resource simultaneously
```

Result:

```text
race conditions
duplicate processing
data corruption
```

Example:

```text
Only 1 product left in inventory.
```

Two users buy simultaneously.

Without lock:

```text
both requests succeed
inventory becomes negative
```

This becomes catastrophic in:

```text
payments
banking
ticket booking
stock trading
```

Distributed lock guarantees:

```text
exclusive ownership
```

Only one client can hold the lock.

---

# 3. Race Condition Problem

Suppose:

```text
inventory = 1
```

Two servers process payment simultaneously.

# Without Lock

Server-A reads:

```text
inventory = 1
```

Server-B reads:

```text
inventory = 1
```

Both decrement:

```text
inventory = -1
```

Incorrect state.

---

# With Lock

Server-A acquires lock:

```text
LOCK inventory:item-1
```

Server-B tries same lock:

```text
FAIL
```

Server-B waits/retries.

Result:

```text
safe sequential execution
```

This is the core purpose of distributed locking.

---

# 4. Lock Mental Model

Architecture:

```text
Client-A
   |
   | acquire lock
   v
+----------------+
| DistributedLock|
+----------------+
   ^
   |
Client-B blocked
```

Flow:

```text
1. client requests lock
2. system checks ownership
3. if free -> grant lock
4. if occupied -> reject
```

Very important:

```text
lock must eventually expire
```

Otherwise:

```text
deadlock forever
```

---

# 5. Why TTL Is Critical

Suppose:

```text
client acquires lock
```

Then crashes.

Without TTL:

```text
lock never released
```

Entire system stuck forever.

TTL solves this.

Example:

```text
LOCK order:1 token EX 30
```

Meaning:

```text
auto-release after 30 seconds
```

Even if owner crashes:

```text
system recovers automatically
```

This is why Redis locks always use:

```text
expiration time
```

---

# 6. Why Owner Token Is Critical

Very important distributed systems concept.

Suppose:

```text
Client-A acquires lock
```

Then:

```text
Client-B accidentally releases it
```

Catastrophic.

Solution:

```text
owner token
```

Lock internally stores:

```text
token-abc
```

Unlock succeeds ONLY if:

```text
provided token matches owner token
```

This guarantees:

```text
safe release
```

Exactly how Redis distributed locking works.

---

# 7. Deep Internal Data Structure Explanation

MiniRedis implementation:

```java
Map<String, LockEntry>
```

Meaning:

```text
lock key
   ->
owner token + expiration time
```

Example:

```text
locks
 └── order:1
      ├── token = client-A
      └── expireAt = 1720000
```

---

# Why HashMap?

Because lock operations require:

```text
fast lookup
fast insert
fast delete
```

Complexity:

| Operation | Complexity |
|---|---|
| acquire lock | O(1) |
| release lock | O(1) |
| expiration check | O(1) |

---

# Lock Acquisition Logic

Acquire succeeds if:

```text
1. lock missing
OR
2. lock expired
```

Otherwise:

```text
lock denied
```

---

# Lock Release Logic

Release succeeds ONLY if:

```text
owner token matches
```

This prevents:

```text
accidental unlock by another client
```

---

# Production Redis Internals

Real Redis distributed lock commonly uses:

```text
SET key value NX PX 30000
```

Meaning:

```text
NX -> set only if absent
PX -> expiration
```

Unlock uses Lua script:

```text
check token + delete atomically
```

Very important.

Because:

```text
check + delete separately
is NOT safe
```

---

# 8. Complete Java Code

## 8.1 LockEntry.java

### Logic Before Code

LockEntry stores:

```text
owner token
expiration timestamp
```

```java
package com.miniredis.lock;

/**
 * Represents one distributed lock entry.
 */
public class LockEntry {

    /**
     * Owner token.
     */
    public final String token;

    /**
     * Expiration timestamp.
     */
    public final long expireAtMillis;

    public LockEntry(
            String token,
            long expireAtMillis
    ) {

        this.token = token;
        this.expireAtMillis = expireAtMillis;
    }
}
```

---

## 8.2 DistributedLockManager.java

### Logic Before Code

This class simulates:

```text
distributed lock acquisition
TTL expiration
safe unlock
```

Core responsibilities:

```text
1. acquire lock
2. validate owner
3. release safely
4. expire stale locks
```

```java
package com.miniredis.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * DistributedLockManager simulates
 * Redis distributed locking.
 */
public class DistributedLockManager {

    /**
     * lock key -> lock entry
     */
    private final Map<String, LockEntry> locks =
            new HashMap<>();

    /**
     * Acquire distributed lock.
     */
    public synchronized boolean acquire(
            String lockKey,
            String token,
            long ttlMillis
    ) {

        long now =
                System.currentTimeMillis();

        LockEntry existing =
                locks.get(lockKey);

        // --------------------------------
        // LOCK FREE OR EXPIRED
        // --------------------------------

        if (existing == null
                || now >= existing.expireAtMillis) {

            LockEntry newLock =
                    new LockEntry(
                            token,
                            now + ttlMillis
                    );

            locks.put(
                    lockKey,
                    newLock
            );

            return true;
        }

        // --------------------------------
        // LOCK CURRENTLY OWNED
        // --------------------------------

        return false;
    }

    /**
     * Release lock safely.
     */
    public synchronized boolean release(
            String lockKey,
            String token
    ) {

        LockEntry existing =
                locks.get(lockKey);

        // --------------------------------
        // LOCK MISSING
        // --------------------------------

        if (existing == null) {

            return false;
        }

        // --------------------------------
        // ONLY OWNER CAN RELEASE
        // --------------------------------

        if (Objects.equals(
                existing.token,
                token
        )) {

            locks.remove(lockKey);

            return true;
        }

        return false;
    }

    /**
     * Debug helper.
     */
    public Map<String, LockEntry> snapshot() {

        return new HashMap<>(locks);
    }
}
```

---

## 8.3 Phase025Driver.java

### Logic Before Code

This driver demonstrates:

```text
lock ownership
lock contention
safe unlock
TTL-based locking
```

```java
package com.miniredis.driver;

import com.miniredis.lock.DistributedLockManager;

public class Phase025Driver {

    public static void main(String[] args) {

        DistributedLockManager manager =
                new DistributedLockManager();

        // --------------------------------
        // CLIENT-A ACQUIRES LOCK
        // --------------------------------

        boolean ok1 =
                manager.acquire(
                        "order:1",
                        "token-A",
                        30000
                );

        System.out.println(
                "client-A lock = "
                        + ok1
        );

        // --------------------------------
        // CLIENT-B FAILS
        // --------------------------------

        boolean ok2 =
                manager.acquire(
                        "order:1",
                        "token-B",
                        30000
                );

        System.out.println(
                "client-B lock = "
                        + ok2
        );

        // --------------------------------
        // WRONG OWNER RELEASE
        // --------------------------------

        boolean badUnlock =
                manager.release(
                        "order:1",
                        "token-B"
                );

        System.out.println(
                "client-B unlock = "
                        + badUnlock
        );

        // --------------------------------
        // CORRECT OWNER RELEASE
        // --------------------------------

        boolean okUnlock =
                manager.release(
                        "order:1",
                        "token-A"
                );

        System.out.println(
                "client-A unlock = "
                        + okUnlock
        );
    }
}
```

---

# 9. Step-by-Step Dry Run

# Step 1 — Client-A Acquires Lock

Code:

```java
manager.acquire(
    "order:1",
    "token-A",
    30000
);
```

Execution:

```text
1. lock missing
2. create LockEntry
3. store token + expiration
4. return true
```

Memory:

```text
locks
 └── order:1
      ├── token = token-A
      └── expireAt = future time
```

---

# Step 2 — Client-B Tries Same Lock

Code:

```java
manager.acquire(
    "order:1",
    "token-B",
    30000
);
```

Execution:

```text
1. lock exists
2. lock not expired
3. reject request
```

Result:

```text
false
```

Meaning:

```text
lock contention
```

---

# Step 3 — Wrong Unlock Attempt

Code:

```java
manager.release(
    "order:1",
    "token-B"
);
```

Execution:

```text
1. locate lock
2. compare tokens
3. token mismatch
4. reject unlock
```

Result:

```text
false
```

Critical safety mechanism.

---

# Step 4 — Correct Unlock

Code:

```java
manager.release(
    "order:1",
    "token-A"
);
```

Execution:

```text
1. locate lock
2. token matches
3. remove lock
4. return true
```

Memory after unlock:

```text
locks = {}
```

---

# 10. Internal Memory Visualization

```text
locks
 └── order:1
      ├── token = token-A
      └── expireAt = 1720000
```

After release:

```text
locks = {}
```

---

# 11. Complexity Analysis

| Operation | Complexity | Reason |
|---|---|---|
| acquire | O(1) | HashMap lookup |
| release | O(1) | HashMap remove |
| expiration check | O(1) | timestamp compare |

---

# 12. Real Production Use Cases

# Payment Idempotency

Prevent duplicate payment execution.

---

# Inventory Reservation

Prevent overselling.

---

# Distributed Cron Jobs

Ensure only one worker executes task.

---

# Leader Election

Only one active leader node.

---

# Job Scheduling

Prevent duplicate background jobs.

---

# 13. Redis Production Internals

Real Redis lock:

```text
SET key token NX PX 30000
```

Safe unlock uses:

```text
Lua script
```

Why Lua?

Because:

```text
check + delete must be atomic
```

Advanced production systems use:

```text
RedLock
fencing tokens
lease renewal
watchdog timers
```

---

# 14. Failure Cases And Bottlenecks

# Problem 1 — Client Crash

Lock never released.

Fix:

```text
TTL expiration
```

---

# Problem 2 — Clock Drift

Machines disagree on time.

Result:

```text
incorrect expiration
```

---

# Problem 3 — Network Partition

Client thinks lock acquired.

Master disagrees.

Can cause:

```text
split brain
```

---

# Problem 4 — Long GC Pause

Application paused longer than TTL.

Result:

```text
lock stolen by another client
```

Very important production issue.

---

# 15. Interview Questions

# Q1

Why distributed lock needed?

Answer:

```text
prevent concurrent modification
```

---

# Q2

Why TTL mandatory?

Answer:

```text
prevent deadlock after crash
```

---

# Q3

Why token needed?

Answer:

```text
safe ownership validation
```

---

# Q4

Why Lua script used in Redis unlock?

Answer:

```text
atomic check-and-delete
```

---

# Q5

What is RedLock?

Answer:

```text
multi-node distributed lock algorithm
```

---

# 16. Final Mental Model

```text
Distributed lock
   -> distributed mutex
```

Locks become foundation for:

```text
safe concurrency
distributed coordination
leader election
job scheduling
payments
inventory consistency
```
