# 007_Mutex_Lock.md

# MiniConcurrency — 007 Mutex Lock

## 0. Why This File Exists

After learning race condition and critical section, the next important question is:

```text
How do we protect a critical section?
```

One of the most common answers:

```text
Mutex / Lock
```

This file teaches:

```text
What a mutex is
Why locks are needed
How Java synchronized works
How threads acquire/release locks
Why locks can hurt performance
How backend systems use locks safely
```

---

# 1. One-Line Definition

```text
Mutex = mutual exclusion lock.
```

Simple meaning:

```text
Only one thread can enter protected code at a time.
```

---

# 2. Real Mental Model

Imagine one bathroom key.

```text
Only the person holding the key can enter.
Others must wait outside.
```

In concurrency:

```text
Key = lock
Bathroom = critical section
People = threads
```

---

# 3. Why Mutex Is Needed

Because multiple threads may update shared data.

Unsafe:

```java
counter++;
```

Problem:

```text
Multiple threads can execute it together.
```

Fix:

```text
Allow only one thread at a time.
```

That is mutex.

---

# 4. Core Flow

```text
Thread wants to enter critical section
        ↓
tries to acquire lock
        ↓
if lock is free → enters
        ↓
if lock is busy → waits/blocks
        ↓
after work done → releases lock
```

---

# 5. Mutex Diagram

```text
Thread A
   |
   v
Acquire Lock ✅
   |
   v
Critical Section
   |
   v
Release Lock


Thread B
   |
   v
Try Lock ❌
   |
   v
BLOCKED until lock released
```

---

# 6. Java Uses Monitor Locks

In Java, every object can act like a lock.

Example:

```java
Object lock = new Object();
```

You can use:

```java
synchronized(lock) {
    // critical section
}
```

---

# 7. Java Example — Basic Mutex

```java
public class MutexDemo {

    private static int counter = 0;

    private static final Object lock = new Object();

    public static void increment() {

        synchronized (lock) {

            counter++;
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

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

Expected output:

```text
200000
```

---

# 8. Step-by-Step Dry Run

Initial:

```text
counter = 0
lock = free
```

Thread A:

```text
tries synchronized(lock)
lock is free
acquires lock
enters critical section
```

Thread B:

```text
tries synchronized(lock)
lock is already owned by Thread A
Thread B becomes BLOCKED
```

Thread A:

```text
counter++
releases lock
```

Thread B:

```text
acquires lock
counter++
releases lock
```

Final:

```text
counter = 2
```

No lost update.

---

# 9. What Happens If Lock Is Not Used?

Unsafe dry run:

```text
counter = 0

Thread A reads 0
Thread B reads 0

Thread A writes 1
Thread B writes 1
```

Expected:

```text
2
```

Actual:

```text
1
```

With lock:

```text
Thread A fully finishes before Thread B enters.
```

---

# 10. synchronized Method

Instead of block:

```java
public synchronized void increment() {

    counter++;
}
```

This locks on:

```text
this
```

for instance methods.

---

# 11. synchronized Static Method

```java
public static synchronized void increment() {

    counter++;
}
```

This locks on:

```text
Class object
```

Example:

```text
MutexDemo.class
```

---

# 12. synchronized Block vs Method

## Method Lock

```java
public synchronized void process() {

    validate();
    callDatabase();
    counter++;
}
```

Locks whole method.

---

## Block Lock

```java
public void process() {

    validate();

    callDatabase();

    synchronized (lock) {

        counter++;
    }
}
```

Locks only critical section.

Better for performance.

---

# 13. Lock Scope Matters

Bad:

```java
synchronized(lock) {

    callExternalAPI();
    callDatabase();
    counter++;
}
```

Problem:

```text
Thread holds lock during slow IO.
```

Other threads wait unnecessarily.

Better:

```java
callExternalAPI();
callDatabase();

synchronized(lock) {

    counter++;
}
```

Only shared update locked.

---

# 14. Thread State With Mutex

When thread waits for a synchronized lock:

```text
BLOCKED
```

Example:

```text
Thread A owns lock
Thread B wants same lock
Thread B state = BLOCKED
```

---

# 15. Java Example — BLOCKED With Lock

```java
public class LockBlockedDemo {

    static final Object lock = new Object();

    public static void main(String[] args)
            throws Exception {

        Thread t1 = new Thread(() -> {

            synchronized (lock) {

                try {

                    Thread.sleep(3000);

                } catch (Exception e) {

                }
            }
        });

        Thread t2 = new Thread(() -> {

            synchronized (lock) {

                System.out.println("Thread 2 entered");
            }
        });

        t1.start();

        Thread.sleep(100);

        t2.start();

        Thread.sleep(100);

        System.out.println(t2.getState());
    }
}
```

Expected output:

```text
BLOCKED
```

Why?

```text
t1 holds lock.
t2 waits for same lock.
```

---

# 16. Important: sleep() Does Not Release Lock

In this code:

```java
synchronized(lock) {

    Thread.sleep(3000);
}
```

Thread sleeps but still owns lock.

Other threads remain blocked.

---

# 17. Lock Release Rules

Lock is released when:

```text
synchronized block exits normally
```

or:

```text
exception happens and block exits
```

Example:

```java
synchronized(lock) {

    throw new RuntimeException();
}
```

Lock still gets released when block exits.

---

# 18. Mutex Guarantees

Mutex gives:

```text
Mutual exclusion
Visibility guarantees
Ordering guarantees
```

Simple meaning:

```text
One thread at a time
and memory changes become visible safely.
```

---

# 19. Visibility Benefit

Without synchronization:

```text
Thread A changes value
Thread B may not immediately see it
```

With synchronization on same lock:

```text
Thread B sees updates after acquiring lock.
```

This is part of Java memory model.

---

# 20. Lock Contention

Definition:

```text
Many threads competing for same lock.
```

Example:

```text
200 request threads waiting for one lock.
```

Problem:

```text
High latency
Low throughput
BLOCKED threads
```

---

# 21. Production Example — Bad Global Lock

```java
public class OrderService {

    private final Object lock = new Object();

    public void placeOrder() {

        synchronized(lock) {

            validateOrder();

            saveOrder();

            callPayment();

            updateInventory();
        }
    }
}
```

Problem:

```text
Only one order can be processed at a time.
```

Bad for scale.

---

# 22. Better Backend Design

Lock only inventory update:

```java
public class OrderService {

    private final Object inventoryLock = new Object();

    public void placeOrder() {

        validateOrder();

        saveOrder();

        callPayment();

        synchronized(inventoryLock) {

            updateInventory();
        }
    }
}
```

Better:

```text
More parallelism
Less lock wait
Higher throughput
```

---

# 23. Per-Key Lock Idea

Instead of one global lock:

```text
one lock for all users
```

Use:

```text
one lock per user/order/product
```

Example:

```text
product-101 lock
product-102 lock
product-103 lock
```

Now different products can be updated in parallel.

---

# 24. Simple Per-Key Lock Example

```java
import java.util.concurrent.ConcurrentHashMap;

public class ProductLockService {

    private final ConcurrentHashMap<String, Object> locks =
            new ConcurrentHashMap<>();

    public void updateProduct(String productId) {

        Object lock = locks.computeIfAbsent(
                productId,
                id -> new Object()
        );

        synchronized (lock) {

            // update only this product safely
        }
    }
}
```

Mental model:

```text
Same product → same lock
Different products → different locks
```

---

# 25. Mutex vs AtomicInteger

Use mutex when:

```text
Multiple related operations must be protected
Multiple variables must be updated together
Complex critical section exists
```

Use AtomicInteger when:

```text
Single counter update
Simple atomic operation
```

---

# 26. Example — Atomic Not Enough

```java
balance = balance - amount;
transactions.add(txn);
```

These two operations must happen together.

Need:

```text
lock
transaction
synchronization
```

AtomicInteger alone may not protect full business invariant.

---

# 27. Mutex vs Database Lock

In-memory Java mutex protects:

```text
inside one JVM process
```

Database lock protects:

```text
shared data across multiple application instances
```

Very important.

---

# 28. Production Warning

If you have:

```text
3 Spring Boot instances
```

Java synchronized lock protects only:

```text
one instance
```

It does NOT protect across all pods.

For distributed systems, need:

```text
DB lock
Redis distributed lock
Kafka partition ordering
Optimistic locking
```

---

# 29. Kubernetes Example

```text
order-service pod-1
order-service pod-2
order-service pod-3
```

Each pod has its own JVM.

Each JVM has its own lock.

So:

```java
synchronized(lock)
```

does not synchronize across pods.

---

# 30. Redis Distributed Lock Note

For multi-instance locking:

```text
Redis SET NX PX
```

can be used carefully.

But distributed locks have tradeoffs:

```text
expiry
clock issues
network partition
lock ownership
```

Learn later in distributed systems.

---

# 31. Deadlock Risk

Locks can cause deadlock.

Example:

```text
Thread A holds Lock 1 and waits for Lock 2
Thread B holds Lock 2 and waits for Lock 1
```

Result:

```text
Both wait forever.
```

Deadlock will be learned later.

---

# 32. Best Practices

```text
Keep lock scope small.
Avoid locking slow IO.
Use final lock objects.
Avoid exposing lock object publicly.
Prefer thread-safe libraries when possible.
Avoid nested locks unless necessary.
```

---

# 33. Interview Explanation

If interviewer asks:

```text
What is mutex?
```

Good answer:

```text
A mutex is a mutual exclusion lock that allows only one thread
to enter a critical section at a time. It protects shared mutable data
from race conditions by forcing other threads to wait until the lock
is released.
```

Strong backend addition:

```text
In Java, synchronized uses monitor locks. But synchronized only protects
threads inside the same JVM process, not across multiple Spring Boot pods.
```

---

# 34. Common Mistakes

## Mistake 1

```text
Locking entire method when only one line needs protection.
```

Hurts throughput.

---

## Mistake 2

```text
Sleeping while holding lock.
```

Blocks other threads unnecessarily.

---

## Mistake 3

```text
Thinking synchronized works across microservice instances.
```

Wrong.

It works only inside same JVM.

---

## Mistake 4

```text
Using public lock object.
```

Dangerous because external code can lock it.

---

# 35. Mini Dry Run Summary

```text
Thread wants shared data
    ↓
acquires lock
    ↓
enters critical section
    ↓
updates data safely
    ↓
releases lock
    ↓
next waiting thread enters
```

---

# 36. Visual Summary

```text
Shared Mutable Data
        ↓
Critical Section
        ↓
Mutex Lock
        ↓
One Thread At A Time
        ↓
Thread Safety
```

---

# 37. What To Remember

```text
Mutex = mutual exclusion.

Lock protects critical section.

Only one thread enters protected code.

synchronized is Java monitor locking.

Keep locks small.

Java locks do not protect across multiple JVMs/pods.
```

---

# 38. Next File

```text
008_ReentrantLock.md
```

Next you learn:

```text
Explicit lock/unlock
tryLock
fairness
interruptible locking
why ReentrantLock is more flexible than synchronized
```
