# MiniRedis Phase 26 — Redlock Basic (Rich Version)

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Redlock Exists](#2-why-redlock-exists)
- [3. Single-Node Lock Problem](#3-single-node-lock-problem)
- [4. Redlock Mental Model](#4-redlock-mental-model)
- [5. Quorum Explained](#5-quorum-explained)
- [6. Validity Time Explained](#6-validity-time-explained)
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
Redlock Basic
```

Main objective:

```text
Acquire the same lock on multiple Redis nodes
and accept the lock only if majority nodes agree.
```

Mental model:

```text
distributed lock with quorum
```

Earlier phase:

```text
single Redis lock
```

This phase:

```text
multi Redis node lock
```

Real-world analogy:

```text
To enter a secure vault,
you need approval from 3 out of 5 managers.
```

You do not need all 5.

You need:

```text
majority approval
```

That majority is called:

```text
quorum
```

Production systems using this idea:

```text
distributed locks
leader election
critical section protection
high availability coordination
```

---

# 2. Why Redlock Exists

Single-node distributed lock has one major weakness:

```text
the Redis node itself can fail
```

Example:

```text
Client-A acquires lock on redis-1
redis-1 crashes
Client-B may acquire lock elsewhere
```

Now two clients may believe:

```text
I own the lock
```

This can cause:

```text
double payment
duplicate job execution
oversold inventory
split brain
```

Redlock tries to reduce this risk by using:

```text
multiple independent Redis nodes
```

Instead of trusting one Redis node:

```text
trust majority of nodes
```

---

# 3. Single-Node Lock Problem

Single-node lock flow:

```text
Client-A
   |
   v
Redis-1
```

Client-A acquires:

```text
LOCK order:1 token-A
```

Problem cases:

## Problem 1 — Redis Node Crash

Redis crashes after granting lock.

The lock state may disappear.

## Problem 2 — Network Partition

Client cannot reach Redis.

System cannot safely know:

```text
does lock still exist?
```

## Problem 3 — Failover Race

Redis master fails before replication reaches replica.

Replica promoted without lock.

Another client acquires same lock.

Result:

```text
two lock owners
```

Redlock attempts to reduce this issue.

---

# 4. Redlock Mental Model

Redlock uses:

```text
N independent Redis nodes
```

Usually:

```text
5 nodes
```

To acquire lock:

```text
client tries all nodes
```

Success condition:

```text
lock acquired on majority nodes
AND
time taken is less than TTL
```

For 5 nodes:

```text
majority = 3
```

Architecture:

```text
             Client
               |
   +-----------+-----------+
   |           |           |
   v           v           v
Redis-1     Redis-2     Redis-3
   |           |
   v           v
Redis-4     Redis-5
```

If lock succeeds on:

```text
3 out of 5
```

then client owns lock.

If only:

```text
2 out of 5
```

then lock acquisition fails.

---

# 5. Quorum Explained

Quorum means:

```text
majority agreement
```

Formula:

```text
quorum = floor(N / 2) + 1
```

Examples:

| Nodes | Quorum |
|---|---|
| 3 | 2 |
| 5 | 3 |
| 7 | 4 |

For 5 Redis nodes:

```text
quorum = 3
```

Why not require all 5?

Because one node may be:

```text
down
slow
network unreachable
```

Quorum gives:

```text
fault tolerance
```

---

# 6. Validity Time Explained

Redlock is not only about quorum.

It also checks:

```text
how long lock acquisition took
```

Example:

```text
TTL = 30000 ms
time spent acquiring = 200 ms
```

Remaining validity:

```text
29800 ms
```

Lock is useful.

But if:

```text
TTL = 30000 ms
time spent acquiring = 31000 ms
```

Then lock already expired.

Even if quorum succeeded:

```text
lock must fail
```

So Redlock success requires:

```text
successCount >= quorum
AND
elapsedTime < ttlMillis
```

---

# 7. Deep Internal Data Structure Explanation

MiniRedis uses:

```text
List<DistributedLockNode>
```

Each node simulates:

```text
independent Redis instance
```

Each Redis node stores:

```java
Map<String, LockEntry>
```

Meaning:

```text
lock key -> token + expiry
```

RedlockManager stores:

```text
multiple nodes
quorum rule
```

Acquire flow:

```text
1. generate token
2. try lock on each node
3. count successes
4. check elapsed time
5. if quorum success -> lock acquired
6. else rollback acquired locks
```

Rollback is important.

If acquisition fails after partially locking nodes:

```text
release those partial locks
```

Otherwise stale partial locks block future clients.

---

# 8. Complete Java Code

## 8.1 LockEntry.java

### Logic Before Code

One lock entry stores:

```text
owner token
expiry time
```

```java
package com.miniredis.lock;

/**
 * Stores lock owner token and expiry timestamp.
 */
public class LockEntry {

    public final String token;
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

## 8.2 DistributedLockNode.java

### Logic Before Code

This represents one independent Redis node.

It supports:

```text
try acquire
safe release
```

```java
package com.miniredis.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Simulates one Redis node used by Redlock.
 */
public class DistributedLockNode {

    private final String nodeName;

    private final Map<String, LockEntry> locks =
            new HashMap<>();

    public DistributedLockNode(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * Try to acquire lock on this node.
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

        if (existing == null
                || now >= existing.expireAtMillis) {

            locks.put(
                    lockKey,
                    new LockEntry(
                            token,
                            now + ttlMillis
                    )
            );

            return true;
        }

        return false;
    }

    /**
     * Release only if token matches.
     */
    public synchronized boolean release(
            String lockKey,
            String token
    ) {

        LockEntry existing =
                locks.get(lockKey);

        if (existing == null) {
            return false;
        }

        if (Objects.equals(
                existing.token,
                token
        )) {

            locks.remove(lockKey);
            return true;
        }

        return false;
    }

    public String getNodeName() {
        return nodeName;
    }

    public Map<String, LockEntry> snapshot() {
        return new HashMap<>(locks);
    }
}
```

---

## 8.3 RedlockManager.java

### Logic Before Code

This class coordinates locking across multiple Redis nodes.

It applies:

```text
quorum rule
validity time rule
rollback on failure
```

```java
package com.miniredis.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Simplified Redlock implementation.
 */
public class RedlockManager {

    private final List<DistributedLockNode> nodes;

    public RedlockManager(
            List<DistributedLockNode> nodes
    ) {
        this.nodes = nodes;
    }

    /**
     * Acquire distributed lock.
     */
    public String acquire(
            String lockKey,
            long ttlMillis
    ) {

        String token =
                UUID.randomUUID().toString();

        int quorum =
                nodes.size() / 2 + 1;

        int successCount =
                0;

        long start =
                System.currentTimeMillis();

        List<DistributedLockNode> lockedNodes =
                new ArrayList<>();

        // --------------------------------
        // TRY LOCK ON EACH NODE
        // --------------------------------

        for (DistributedLockNode node : nodes) {

            boolean success =
                    node.acquire(
                            lockKey,
                            token,
                            ttlMillis
                    );

            if (success) {

                successCount++;

                lockedNodes.add(node);
            }
        }

        long elapsed =
                System.currentTimeMillis() - start;

        // --------------------------------
        // REDLOCK SUCCESS CONDITION
        // --------------------------------

        if (successCount >= quorum
                && elapsed < ttlMillis) {

            return token;
        }

        // --------------------------------
        // ROLLBACK PARTIAL LOCKS
        // --------------------------------

        for (DistributedLockNode node : lockedNodes) {

            node.release(
                    lockKey,
                    token
            );
        }

        return null;
    }

    /**
     * Release distributed lock from all nodes.
     */
    public void release(
            String lockKey,
            String token
    ) {

        for (DistributedLockNode node : nodes) {

            node.release(
                    lockKey,
                    token
            );
        }
    }
}
```

---

## 8.4 Phase026Driver.java

### Logic Before Code

This driver demonstrates:

```text
5 Redis nodes
quorum = 3
lock success
second client blocked
safe release
```

```java
package com.miniredis.driver;

import com.miniredis.lock.DistributedLockNode;
import com.miniredis.lock.RedlockManager;

import java.util.List;

public class Phase026Driver {

    public static void main(String[] args) {

        List<DistributedLockNode> nodes =
                List.of(
                        new DistributedLockNode("redis-1"),
                        new DistributedLockNode("redis-2"),
                        new DistributedLockNode("redis-3"),
                        new DistributedLockNode("redis-4"),
                        new DistributedLockNode("redis-5")
                );

        RedlockManager manager =
                new RedlockManager(nodes);

        // --------------------------------
        // CLIENT-A ACQUIRES LOCK
        // --------------------------------

        String tokenA =
                manager.acquire(
                        "order:1",
                        30000
                );

        System.out.println(
                "client-A token = "
                        + tokenA
        );

        // --------------------------------
        // CLIENT-B TRIES SAME LOCK
        // --------------------------------

        String tokenB =
                manager.acquire(
                        "order:1",
                        30000
                );

        System.out.println(
                "client-B token = "
                        + tokenB
        );

        // --------------------------------
        // CLIENT-A RELEASES LOCK
        // --------------------------------

        manager.release(
                "order:1",
                tokenA
        );

        // --------------------------------
        // CLIENT-B TRIES AGAIN
        // --------------------------------

        tokenB =
                manager.acquire(
                        "order:1",
                        30000
                );

        System.out.println(
                "client-B token after release = "
                        + tokenB
        );
    }
}
```

---

# 9. Step-by-Step Dry Run

## Step 1 — Create 5 Redis Nodes

Memory:

```text
redis-1 -> {}
redis-2 -> {}
redis-3 -> {}
redis-4 -> {}
redis-5 -> {}
```

Quorum:

```text
5 / 2 + 1 = 3
```

---

## Step 2 — Client-A Acquires Lock

Code:

```java
manager.acquire("order:1", 30000);
```

Execution:

```text
1. generate token-A
2. try redis-1
3. try redis-2
4. try redis-3
5. try redis-4
6. try redis-5
7. count success
```

Suppose all succeed:

```text
successCount = 5
```

Check:

```text
5 >= 3 yes
elapsed < TTL yes
```

Result:

```text
lock acquired
```

---

## Step 3 — Client-B Tries Same Lock

Code:

```java
manager.acquire("order:1", 30000);
```

Each node already has:

```text
order:1 -> token-A
```

So Client-B fails on all nodes.

Result:

```text
null
```

Meaning:

```text
lock denied
```

---

## Step 4 — Client-A Releases Lock

Code:

```java
manager.release("order:1", tokenA);
```

Execution:

```text
1. send release to all nodes
2. each node checks token
3. token matches
4. remove lock
```

Memory:

```text
all redis nodes empty again
```

---

## Step 5 — Client-B Tries Again

Now locks are free.

Client-B gets quorum.

Result:

```text
new token returned
```

---

# 10. Internal Memory Visualization

After Client-A lock:

```text
redis-1
 └── order:1 -> token-A

redis-2
 └── order:1 -> token-A

redis-3
 └── order:1 -> token-A

redis-4
 └── order:1 -> token-A

redis-5
 └── order:1 -> token-A
```

After release:

```text
redis-1 -> {}
redis-2 -> {}
redis-3 -> {}
redis-4 -> {}
redis-5 -> {}
```

---

# 11. Complexity Analysis

| Operation | Complexity | Reason |
|---|---|---|
| Acquire | O(n) | try all nodes |
| Release | O(n) | release all nodes |
| Single node acquire | O(1) | HashMap lookup |

Where:

```text
n = number of Redis lock nodes
```

---

# 12. Real Production Use Cases

## High Availability Locks

Avoid trusting one Redis node.

## Payment Critical Section

Prevent duplicate payment capture.

## Inventory Reservation

Prevent overselling under failure.

## Leader Election

Only one worker becomes leader.

## Distributed Job Execution

Only one worker runs a scheduled job.

---

# 13. Redis Production Internals

Redlock concept:

```text
Acquire lock on multiple independent Redis masters.
Require majority success.
Check elapsed time.
Release using token.
```

Single Redis lock command:

```text
SET lockKey token NX PX ttl
```

Safe unlock:

```text
Lua check token and delete
```

Important note:

```text
Redlock is debated in distributed systems.
```

Why?

Because correctness depends on:

```text
clock drift
network delays
pause times
failure assumptions
```

For very critical systems:

```text
use fencing tokens
database constraints
idempotency keys
transactional storage
```

---

# 14. Failure Cases And Bottlenecks

## Problem 1 — Clock Drift

Nodes disagree about time.

Result:

```text
TTL correctness risk
```

## Problem 2 — Long GC Pause

Client acquires lock, pauses longer than TTL.

Another client acquires lock.

Old client resumes and still writes.

Fix:

```text
fencing tokens
```

## Problem 3 — Partial Lock Success

Client locks 2 nodes but not quorum.

Fix:

```text
rollback partial locks
```

## Problem 4 — Slow Redis Node

Lock acquisition takes too long.

Fix:

```text
short per-node timeout
```

## Problem 5 — Network Partition

Client may see subset of nodes.

Risk:

```text
incorrect lock ownership under bad assumptions
```

---

# 15. Interview Questions

## Q1

Why Redlock instead of single Redis lock?

Answer:

```text
single Redis node can fail
Redlock uses quorum across multiple nodes
```

## Q2

What is quorum?

Answer:

```text
majority agreement
```

## Q3

Why elapsed time check?

Answer:

```text
lock must still be valid after acquisition
```

## Q4

Why rollback partial locks?

Answer:

```text
avoid stale partial locks after failed acquisition
```

## Q5

Why is Redlock debated?

Answer:

```text
distributed timing assumptions are hard
```

## Q6

What is fencing token?

Answer:

```text
monotonically increasing token used by downstream system
to reject stale lock holders
```

---

# 16. Final Mental Model

```text
Single Redis lock
   -> one node authority

Redlock
   -> majority-based lock authority
```

Redlock teaches:

```text
quorum
leases
timeouts
partial failure
distributed coordination
```

For real financial or inventory systems:

```text
combine locks with idempotency and database constraints
```
