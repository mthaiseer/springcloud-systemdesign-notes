# 016_Deadlock.md

# MiniConcurrency — 016 Deadlock

## 0. Why This File Exists

After learning mutex, ReentrantLock, BlockingQueue, and producer-consumer, the next critical concurrency problem is:

```text
What if threads wait forever for each other?
```

That problem is called:

```text
Deadlock
```

Deadlock is dangerous because:

```text
threads stop progressing
requests hang
workers freeze
queues grow
system becomes partially or fully stuck
```

This file teaches:

```text
What deadlock is
Why deadlock happens
Coffman conditions
Java synchronized deadlock
ReentrantLock deadlock
Step-by-step dry run
Deadlock prevention
Lock ordering
tryLock timeout
Database deadlock
Microservice deadlock
Production debugging with thread dumps
Interview explanation
```

---

# 1. One-Line Definition

```text
Deadlock happens when two or more threads wait forever for resources held by each other.
```

Simple meaning:

```text
Thread A waits for Thread B.
Thread B waits for Thread A.
Nobody moves.
```

---

# 2. Real Mental Model

Imagine two cars on a narrow road.

```text
Car A wants to move forward.
Car B wants to move forward.
Both block each other.
Neither reverses.
```

Result:

```text
Both stuck forever.
```

That is deadlock.

---

# 3. Basic Deadlock Situation

```text
Thread-1 holds Lock-A
Thread-1 waits for Lock-B

Thread-2 holds Lock-B
Thread-2 waits for Lock-A
```

Result:

```text
Thread-1 cannot continue.
Thread-2 cannot continue.
```

---

# 4. Visual Diagram

```text
Thread-1
   |
   v
holds Lock-A
   |
   v
waiting for Lock-B


Thread-2
   |
   v
holds Lock-B
   |
   v
waiting for Lock-A
```

Circular wait:

```text
Thread-1 → Thread-2 → Thread-1
```

This is deadlock.

---

# 5. Why Deadlock Is Hard

Deadlock may happen only under:

```text
specific timing
specific load
specific thread scheduling
specific request order
```

So it may:

```text
work in local testing
fail randomly in production
```

---

# 6. Coffman Conditions

Deadlock usually needs 4 conditions:

```text
1. Mutual exclusion
2. Hold and wait
3. No preemption
4. Circular wait
```

If all 4 exist:

```text
deadlock is possible
```

If you break one:

```text
deadlock can be prevented
```

---

# 7. Condition 1 — Mutual Exclusion

```text
Only one thread can hold a resource at a time.
```

Example:

```text
Only one thread can own a lock.
```

In Java:

```java
synchronized(lock) {
    // only one thread here
}
```

---

# 8. Condition 2 — Hold And Wait

```text
Thread holds one resource while waiting for another.
```

Example:

```text
Thread-1 holds Lock-A
and waits for Lock-B
```

This is dangerous.

---

# 9. Condition 3 — No Preemption

```text
A lock cannot be forcibly taken from a thread.
```

The thread must release it voluntarily.

---

# 10. Condition 4 — Circular Wait

```text
A cycle of waiting exists.
```

Example:

```text
Thread-1 waits for Thread-2
Thread-2 waits for Thread-1
```

---

# 11. Java synchronized Deadlock Example

```java
public class DeadlockDemo {

    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void main(String[] args) {

        Thread thread1 = new Thread(() -> {

            synchronized (lockA) {

                System.out.println("Thread-1 acquired Lock-A");

                sleep(100);

                synchronized (lockB) {

                    System.out.println("Thread-1 acquired Lock-B");
                }
            }
        });

        Thread thread2 = new Thread(() -> {

            synchronized (lockB) {

                System.out.println("Thread-2 acquired Lock-B");

                sleep(100);

                synchronized (lockA) {

                    System.out.println("Thread-2 acquired Lock-A");
                }
            }
        });

        thread1.start();
        thread2.start();
    }

    private static void sleep(long millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

---

# 12. Step-by-Step Dry Run

Initial:

```text
Lock-A = free
Lock-B = free
```

Step 1:

```text
Thread-1 starts
Thread-1 acquires Lock-A
```

Step 2:

```text
Thread-2 starts
Thread-2 acquires Lock-B
```

Step 3:

```text
Thread-1 tries to acquire Lock-B
```

But:

```text
Lock-B is held by Thread-2
```

So:

```text
Thread-1 becomes BLOCKED
```

Step 4:

```text
Thread-2 tries to acquire Lock-A
```

But:

```text
Lock-A is held by Thread-1
```

So:

```text
Thread-2 becomes BLOCKED
```

Final state:

```text
Thread-1 waits for Lock-B
Thread-2 waits for Lock-A
```

Nobody releases anything.

Deadlock.

---

# 13. Why sleep() In Example?

```java
sleep(100);
```

is used to increase the chance that:

```text
Thread-1 gets Lock-A
Thread-2 gets Lock-B
```

before either tries the second lock.

In real production, this timing may happen naturally under load.

---

# 14. Thread State During Deadlock

Usually threads appear as:

```text
BLOCKED
```

because they are waiting to acquire monitor locks.

In thread dumps, you may see:

```text
waiting to lock <object>
locked <object>
```

---

# 15. Deadlock With ReentrantLock

Deadlock is not limited to `synchronized`.

It can also happen with:

```java
ReentrantLock
```

Example:

```java
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDeadlockDemo {

    private static final ReentrantLock lockA = new ReentrantLock();
    private static final ReentrantLock lockB = new ReentrantLock();

    public static void main(String[] args) {

        Thread thread1 = new Thread(() -> {

            lockA.lock();

            try {

                System.out.println("Thread-1 acquired Lock-A");

                sleep(100);

                lockB.lock();

                try {
                    System.out.println("Thread-1 acquired Lock-B");
                } finally {
                    lockB.unlock();
                }

            } finally {
                lockA.unlock();
            }
        });

        Thread thread2 = new Thread(() -> {

            lockB.lock();

            try {

                System.out.println("Thread-2 acquired Lock-B");

                sleep(100);

                lockA.lock();

                try {
                    System.out.println("Thread-2 acquired Lock-A");
                } finally {
                    lockA.unlock();
                }

            } finally {
                lockB.unlock();
            }
        });

        thread1.start();
        thread2.start();
    }

    private static void sleep(long millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

Problem is same:

```text
inconsistent lock order
```

---

# 16. Main Cause — Inconsistent Lock Ordering

Deadlock usually happens because different threads acquire locks in different orders.

Bad:

```text
Thread-1: Lock-A → Lock-B
Thread-2: Lock-B → Lock-A
```

Good:

```text
Thread-1: Lock-A → Lock-B
Thread-2: Lock-A → Lock-B
```

---

# 17. Fix 1 — Consistent Lock Ordering

Rule:

```text
Always acquire locks in the same global order.
```

Example:

```text
always Lock-A first
then Lock-B
```

---

# 18. Fixed synchronized Version

```java
public class DeadlockFixedByOrdering {

    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void main(String[] args) {

        Thread thread1 = new Thread(() -> {

            synchronized (lockA) {

                synchronized (lockB) {

                    System.out.println("Thread-1 completed");
                }
            }
        });

        Thread thread2 = new Thread(() -> {

            synchronized (lockA) {

                synchronized (lockB) {

                    System.out.println("Thread-2 completed");
                }
            }
        });

        thread1.start();
        thread2.start();
    }
}
```

Now both use:

```text
Lock-A → Lock-B
```

No circular wait.

---

# 19. Dry Run — Fixed Version

Thread-1:

```text
acquires Lock-A
acquires Lock-B
does work
releases Lock-B
releases Lock-A
```

Thread-2:

```text
waits for Lock-A
then acquires Lock-A
then acquires Lock-B
does work
```

No cycle.

Only waiting.

Waiting is okay.

Deadlock is not okay.

---

# 20. Important Difference — Waiting vs Deadlock

Waiting:

```text
Thread waits, but eventually continues.
```

Deadlock:

```text
Thread waits forever because cycle exists.
```

---

# 21. Fix 2 — Use tryLock()

`ReentrantLock` provides:

```java
tryLock()
```

Instead of waiting forever:

```text
try to acquire
if fail, release and retry later
```

---

# 22. tryLock Deadlock Avoidance Example

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TryLockAvoidDeadlock {

    private static final ReentrantLock lockA = new ReentrantLock();
    private static final ReentrantLock lockB = new ReentrantLock();

    public static void main(String[] args) {

        Runnable task = () -> {

            while (true) {

                boolean gotA = false;
                boolean gotB = false;

                try {

                    gotA = lockA.tryLock(500, TimeUnit.MILLISECONDS);
                    gotB = lockB.tryLock(500, TimeUnit.MILLISECONDS);

                    if (gotA && gotB) {

                        System.out.println(
                                Thread.currentThread().getName()
                                + " acquired both locks"
                        );

                        break;
                    }

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    break;

                } finally {

                    if (gotB) {
                        lockB.unlock();
                    }

                    if (gotA) {
                        lockA.unlock();
                    }
                }

                sleep(100);
            }
        };

        new Thread(task, "worker-1").start();
        new Thread(task, "worker-2").start();
    }

    private static void sleep(long millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

---

# 23. Why tryLock Helps

Without tryLock:

```text
Thread waits forever
```

With tryLock timeout:

```text
Thread waits limited time
if cannot get both locks
release what it has
retry later
```

This breaks:

```text
hold and wait forever
```

---

# 24. Fix 3 — Reduce Lock Scope

Bad:

```java
synchronized (lock) {

    callDatabase();
    callExternalAPI();
    updateCounter();
}
```

Problem:

```text
lock held during slow IO
```

Better:

```java
callDatabase();
callExternalAPI();

synchronized (lock) {

    updateCounter();
}
```

Keep critical section small.

---

# 25. Fix 4 — Avoid Nested Locks

Nested locks increase deadlock risk.

Bad:

```java
synchronized(lockA) {

    synchronized(lockB) {

        synchronized(lockC) {

        }
    }
}
```

Better:

```text
redesign data ownership
use one lock if acceptable
use immutable data
use queue/message passing
```

---

# 26. Fix 5 — Immutable Design

Immutable objects reduce need for locks.

Less locks:

```text
less deadlock risk
```

Example:

```java
public final class Config {

    private final int timeoutMs;

    public Config(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }
}
```

Instead of modifying shared object:

```text
create new object and swap reference
```

---

# 27. Fix 6 — Use Message Passing

Instead of many threads locking same data:

```text
send task to one owner thread
```

Example:

```text
Producer → Queue → Single Consumer owns state
```

This avoids shared mutable state.

Redis uses a similar idea:

```text
single-threaded command execution
```

to avoid many locking problems.

---

# 28. Classic Bank Transfer Deadlock

Two accounts:

```text
Account A
Account B
```

Thread-1:

```text
transfer A → B
locks A
then locks B
```

Thread-2:

```text
transfer B → A
locks B
then locks A
```

Deadlock possible.

---

# 29. Safe Bank Transfer Using Lock Ordering

Use account ID order:

```text
always lock smaller account ID first
```

Example:

```java
public void transfer(Account from, Account to, int amount) {

    Account first =
            from.getId() < to.getId()
            ? from
            : to;

    Account second =
            from.getId() < to.getId()
            ? to
            : from;

    synchronized (first) {

        synchronized (second) {

            from.withdraw(amount);
            to.deposit(amount);
        }
    }
}
```

Now every transfer follows same order.

---

# 30. Backend Example — Cache + DB Lock

Dangerous:

```text
Thread-1:
cache lock → DB row lock

Thread-2:
DB row lock → cache lock
```

Possible deadlock.

Fix:

```text
consistent order
or avoid holding one lock while acquiring another
```

---

# 31. Database Deadlock

Deadlocks also happen in databases.

Example:

```text
Transaction-1 locks row A
Transaction-2 locks row B
Transaction-1 waits row B
Transaction-2 waits row A
```

Databases usually detect this and abort one transaction.

Application must handle:

```text
retry transaction
```

---

# 32. Microservice Deadlock

Distributed deadlock can happen when services wait on each other.

Example:

```text
Service A calls Service B
Service B calls Service C
Service C calls Service A
```

Or with distributed locks:

```text
Pod-1 holds Redis lock A, waits lock B
Pod-2 holds Redis lock B, waits lock A
```

Harder than JVM deadlock.

---

# 33. Production Symptoms

Deadlock symptoms:

```text
API requests hang
thread pool stops processing
queue size grows
blocked thread count increases
CPU may be low
application looks alive but stuck
timeouts increase
no logs after certain point
```

Important clue:

```text
low CPU + stuck requests can indicate blocked/deadlocked threads
```

---

# 34. Debugging With jstack

Java command:

```bash
jstack <pid>
```

or:

```bash
jcmd <pid> Thread.print
```

Thread dump can show:

```text
Found one Java-level deadlock
```

and list involved threads.

---

# 35. Thread Dump Mental Model

Look for:

```text
Thread A:
- locked object X
- waiting to lock object Y

Thread B:
- locked object Y
- waiting to lock object X
```

This cycle confirms deadlock.

---

# 36. Thread Dump Example Shape

```text
Thread-1:
  waiting to lock Lock-B
  locked Lock-A

Thread-2:
  waiting to lock Lock-A
  locked Lock-B
```

This means:

```text
Thread-1 waits for Thread-2
Thread-2 waits for Thread-1
```

Deadlock.

---

# 37. Deadlock vs Starvation

Deadlock:

```text
threads wait forever in a cycle
```

Starvation:

```text
one thread waits too long because others keep getting resources
```

Example starvation:

```text
unfair lock keeps favoring other threads
```

---

# 38. Deadlock vs Livelock

Deadlock:

```text
threads do nothing
```

Livelock:

```text
threads keep reacting/retrying
but no useful progress
```

Example:

```text
two people keep stepping aside for each other forever
```

---

# 39. Deadlock vs Slow System

Slow system:

```text
eventually progresses
```

Deadlock:

```text
never progresses without intervention
```

---

# 40. Prevention Checklist

```text
Use consistent lock ordering.
Avoid nested locks.
Keep lock scope small.
Do not call external systems while holding lock.
Use tryLock timeout where useful.
Use immutable objects.
Use message passing / queues.
Monitor blocked threads.
Analyze thread dumps.
```

---

# 41. Production Design Rule

If lock is needed:

```text
document lock order clearly
```

Example:

```text
Always acquire:
1. account lock
2. inventory lock
3. cache lock
```

Never reverse it.

---

# 42. Real Backend Mental Model

```text
Request Thread-1
    ↓
holds resource A
    ↓
waits resource B

Request Thread-2
    ↓
holds resource B
    ↓
waits resource A
```

No response returns.

Thread pool capacity reduces.

System slowly freezes.

---

# 43. Interview Explanation

If interviewer asks:

```text
What is deadlock?
```

Good answer:

```text
Deadlock is a situation where two or more threads wait forever
because each holds a resource the other needs. A common example is
Thread-1 holding Lock-A and waiting for Lock-B, while Thread-2 holds
Lock-B and waits for Lock-A.
```

Strong backend addition:

```text
Deadlock can be prevented using consistent lock ordering, reducing lock scope,
avoiding nested locks, and using tryLock with timeout where appropriate.
In production, thread dumps using jstack help identify deadlocked threads.
```

---

# 44. Common Mistakes

## Mistake 1

```text
Acquiring locks in different orders.
```

Most common cause.

---

## Mistake 2

```text
Holding lock while calling DB/API/network.
```

Dangerous.

---

## Mistake 3

```text
Too many nested locks.
```

Hard to reason.

---

## Mistake 4

```text
Ignoring thread dumps.
```

Thread dump is key debugging tool.

---

## Mistake 5

```text
Thinking ReentrantLock prevents deadlock automatically.
```

Wrong.

It only gives extra tools like tryLock.

---

# 45. Mini Dry Run Summary

```text
Thread-1 gets Lock-A
Thread-2 gets Lock-B
Thread-1 waits for Lock-B
Thread-2 waits for Lock-A
cycle formed
both wait forever
```

---

# 46. Visual Summary

```text
Thread-1 --holds--> Lock-A
Thread-1 --waits--> Lock-B

Thread-2 --holds--> Lock-B
Thread-2 --waits--> Lock-A
```

Cycle:

```text
Thread-1 → Lock-B → Thread-2 → Lock-A → Thread-1
```

---

# 47. What To Remember

```text
Deadlock = circular waiting forever.

Four conditions:
mutual exclusion
hold and wait
no preemption
circular wait

Most practical fix:
consistent lock ordering.

Production debugging:
thread dump / jstack.

Avoid nested locks and slow calls inside locks.
```

---

# 48. Next File

```text
017_Livelock_Starvation.md
```

Next you learn:

```text
Starvation
Livelock
Fairness
Priority problems
Retry loops with no progress
Production scheduling issues
```
