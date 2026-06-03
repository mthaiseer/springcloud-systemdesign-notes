# 008_ReentrantLock.md

# MiniConcurrency — 008 ReentrantLock

## 0. Why This File Exists

After learning `synchronized` and mutex locking, the next important question is:

```text
Why does Java provide ReentrantLock?
```

Because:

```text
synchronized is simple
but limited.
```

`ReentrantLock` provides:

```text
More control
More flexibility
Advanced locking features
```

This file teaches:

```text
What ReentrantLock is
Why it exists
How lock()/unlock() works
Reentrancy concept
tryLock()
Fair locks
Interruptible locks
Deadlock avoidance
Backend production use cases
```

---

# 1. One-Line Definition

```text
ReentrantLock is an explicit advanced mutex lock implementation in Java.
```

Simple meaning:

```text
Manual lock/unlock with more features than synchronized.
```

---

# 2. Package

```java
import java.util.concurrent.locks.ReentrantLock;
```

---

# 3. Why "Reentrant"?

Very important.

Reentrant means:

```text
Same thread can acquire same lock multiple times safely.
```

Without deadlocking itself.

---

# 4. Reentrant Mental Model

Example:

```text
Thread A acquires lock
    ↓
Thread A calls another method
    ↓
same thread acquires same lock again
```

Allowed.

Lock internally tracks:

```text
Owner thread
Hold count
```

---

# 5. Basic ReentrantLock Flow

```text
Thread tries lock()
      ↓
if free → acquires lock
      ↓
enters critical section
      ↓
unlock()
      ↓
lock released
```

---

# 6. Basic Java Example

```java
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo {

    private static int counter = 0;

    private static final ReentrantLock lock =
            new ReentrantLock();

    public static void increment() {

        lock.lock();

        try {

            counter++;

        } finally {

            lock.unlock();
        }
    }

    public static void main(String[] args)
            throws Exception {

        Thread t1 = new Thread(() -> {

            for (int i = 0; i < 100000; i++) {

                increment();
            }
        });

        Thread t2 = new Thread(() -> {

            for (int i = 0; i < 100000; i++) {

                increment();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(counter);
    }
}
```

Expected:

```text
200000
```

---

# 7. Why try-finally Is Critical

Very important.

Bad:

```java
lock.lock();

counter++;

lock.unlock();
```

If exception occurs before unlock:

```text
Lock never released.
Other threads wait forever.
```

Dangerous.

Correct:

```java
lock.lock();

try {

    counter++;

} finally {

    lock.unlock();
}
```

Always releases lock safely.

---

# 8. Step-by-Step Dry Run

Initial:

```text
counter = 0
lock = free
```

Thread A:

```text
lock.lock()
acquires lock
```

Thread B:

```text
lock.lock()
lock busy
Thread B waits
```

Thread A:

```text
counter++
unlock()
```

Thread B:

```text
acquires lock
counter++
unlock()
```

Final:

```text
counter = 2
```

---

# 9. Reentrant Example

```java
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantExample {

    private final ReentrantLock lock =
            new ReentrantLock();

    public void outer() {

        lock.lock();

        try {

            inner();

        } finally {

            lock.unlock();
        }
    }

    public void inner() {

        lock.lock();

        try {

            System.out.println("Inner method");

        } finally {

            lock.unlock();
        }
    }
}
```

Works safely.

Why?

Because same thread can reacquire lock.

---

# 10. Hold Count Mental Model

Internally:

```text
Thread A acquires lock → count = 1
Thread A acquires again → count = 2
unlock() → count = 1
unlock() → count = 0
lock released
```

---

# 11. synchronized Is Also Reentrant

Important:

```text
Java synchronized is also reentrant.
```

Example:

```java
synchronized method A
    ↓
calls synchronized method B
```

Same thread can enter.

---

# 12. Why ReentrantLock Exists

Because synchronized lacks features like:

```text
tryLock()
timeout locking
interruptible lock
fairness policy
multiple conditions
manual lock control
```

---

# 13. tryLock()

Very powerful feature.

Instead of waiting forever:

```java
lock.lock();
```

you can try:

```java
if (lock.tryLock()) {

    try {

        // critical section

    } finally {

        lock.unlock();
    }
}
```

Meaning:

```text
Acquire lock only if immediately available.
```

---

# 14. tryLock() Mental Model

```text
Lock free?
    YES → enter critical section
    NO  → skip/fallback/retry later
```

Useful for avoiding long waits.

---

# 15. Java Example — tryLock()

```java
import java.util.concurrent.locks.ReentrantLock;

public class TryLockDemo {

    static ReentrantLock lock =
            new ReentrantLock();

    public static void main(String[] args) {

        if (lock.tryLock()) {

            try {

                System.out.println("Lock acquired");

            } finally {

                lock.unlock();
            }

        } else {

            System.out.println("Could not acquire lock");
        }
    }
}
```

---

# 16. Timeout Locking

Can wait only limited time.

Example:

```java
lock.tryLock(2, TimeUnit.SECONDS);
```

Meaning:

```text
Wait maximum 2 seconds.
```

Very useful in production systems.

---

# 17. Interruptible Lock

Feature:

```java
lock.lockInterruptibly();
```

Allows thread waiting for lock to be interrupted.

Useful for:

```text
Graceful shutdown
Cancellation
Timeout handling
```

`synchronized` cannot do this easily.

---

# 18. Fair Lock

Default ReentrantLock:

```text
Non-fair
```

Meaning:

```text
Some threads may get lock repeatedly.
```

Fair lock:

```java
new ReentrantLock(true);
```

Meaning:

```text
First waiting thread gets lock first.
```

FIFO style.

---

# 19. Fairness Tradeoff

Fair lock advantages:

```text
Less starvation
More predictable
```

Disadvantages:

```text
Lower throughput
More scheduler overhead
```

Most systems use:

```text
non-fair locks
```

for performance.

---

# 20. ReentrantLock vs synchronized

| Feature | synchronized | ReentrantLock |
|---|---|---|
| Simple | Yes | Moderate |
| Manual unlock | No | Yes |
| tryLock | No | Yes |
| Timeout | No | Yes |
| Interruptible | No | Yes |
| Fairness | No | Yes |
| Multiple conditions | No | Yes |
| Automatic unlock | Yes | No |

---

# 21. Important Risk

With synchronized:

```text
JVM automatically releases lock.
```

With ReentrantLock:

```text
YOU must unlock manually.
```

Forgetting unlock causes:

```text
Deadlock
Thread starvation
Permanent blocking
```

---

# 22. Production Example — Inventory Update

```java
import java.util.concurrent.locks.ReentrantLock;

public class InventoryService {

    private final ReentrantLock lock =
            new ReentrantLock();

    private int stock = 10;

    public void purchase() {

        lock.lock();

        try {

            if (stock > 0) {

                stock--;

                System.out.println(
                        "Remaining stock: " + stock
                );
            }

        } finally {

            lock.unlock();
        }
    }
}
```

Protects inventory safely.

---

# 23. Backend Example — tryLock()

Suppose:

```text
High-traffic payment system
```

Instead of waiting forever:

```text
If lock unavailable
→ fail fast
→ retry later
```

Useful for:

```text
Low latency systems
```

---

# 24. Deadlock Avoidance With tryLock

Without tryLock:

```text
Thread A waits forever
Thread B waits forever
```

With tryLock timeout:

```text
Thread can back off safely.
```

Very important in production systems.

---

# 25. Example — Deadlock Prevention

```java
if (lock.tryLock()) {

    try {

        // work

    } finally {

        lock.unlock();
    }

} else {

    // retry later
}
```

Instead of:

```text
Wait forever
```

---

# 26. Lock Contention Still Exists

Even ReentrantLock can suffer:

```text
High contention
BLOCKED threads
Poor scalability
```

Locking still has cost.

---

# 27. Real Backend Mental Model

```text
100 request threads
       ↓
Same lock
       ↓
Threads queue up
       ↓
Only one executes critical section
```

Still serialized access.

---

# 28. Where ReentrantLock Is Common

Used in:

```text
Thread pools
Connection pools
Kafka internals
Concurrent frameworks
Schedulers
Custom concurrency libraries
```

---

# 29. Readability Tradeoff

`synchronized`:

```java
synchronized(lock) {

}
```

Simpler.

ReentrantLock:

```java
lock.lock();

try {

} finally {

    lock.unlock();
}
```

More verbose but more powerful.

---

# 30. Performance Notes

Modern JVM optimized synchronized heavily.

Important reality:

```text
ReentrantLock is not always faster.
```

Choose based on features needed.

---

# 31. Common Backend Rule

Use:

```text
synchronized
```

for:

```text
simple locking
small projects
basic critical sections
```

Use:

```text
ReentrantLock
```

for:

```text
advanced concurrency control
timeouts
interruptible waiting
fairness
custom lock behavior
```

---

# 32. Important Production Warning

Like synchronized:

```text
ReentrantLock works only inside same JVM process.
```

It does NOT synchronize across:

```text
multiple Spring Boot pods
multiple Kubernetes instances
multiple JVMs
```

Distributed coordination needs:

```text
DB locks
Redis locks
ZooKeeper
etcd
Kafka partition ordering
```

---

# 33. Interview Explanation

If interviewer asks:

```text
Why use ReentrantLock instead of synchronized?
```

Good answer:

```text
ReentrantLock provides advanced features like tryLock(),
timeout locking, interruptible locking, fairness policy,
and explicit lock control. synchronized is simpler and
automatically releases locks, but less flexible.
```

Strong backend addition:

```text
tryLock() is useful in high-throughput systems to avoid
threads waiting forever under contention.
```

---

# 34. Common Mistakes

## Mistake 1

```text
Forgetting unlock().
```

Very dangerous.

---

## Mistake 2

```text
Not using finally block.
```

Lock may never release on exception.

---

## Mistake 3

```text
Using fair lock unnecessarily.
```

Can reduce performance.

---

## Mistake 4

```text
Thinking ReentrantLock solves distributed locking.
```

Wrong.

Only JVM-local.

---

# 35. Mini Dry Run Summary

```text
Thread tries lock()
      ↓
if lock free → enters
      ↓
critical section
      ↓
unlock()
      ↓
next waiting thread continues
```

---

# 36. Visual Summary

```text
Shared Mutable Data
        ↓
ReentrantLock
        ↓
One Thread At A Time
        ↓
Safe Critical Section
```

Advanced features:

```text
tryLock
fairness
interruptible waiting
timeout locking
```

---

# 37. What To Remember

```text
ReentrantLock = advanced explicit mutex.

Always unlock in finally block.

Provides tryLock(), fairness, timeout, interruptible locking.

Useful for advanced concurrency control.
```

---

# 38. Next File

```text
009_Read_Write_Lock.md
```

Next you learn:

```text
Multiple readers
Single writer
Read-heavy optimization
How databases/cache systems improve concurrency
```
