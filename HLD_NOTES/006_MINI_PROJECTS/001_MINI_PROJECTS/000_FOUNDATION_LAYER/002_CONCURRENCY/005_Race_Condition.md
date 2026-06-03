# 005_Race_Condition.md

# MiniConcurrency — 005 Race Condition

## 0. Why This File Exists

Race condition is one of the most important backend concurrency problems.

Without understanding race conditions, you cannot properly understand:

```text
Locks
synchronized
AtomicInteger
ConcurrentHashMap
ThreadPool safety
Spring Boot shared state
Kafka consumer safety
Redis internals
```

This topic is the foundation of thread safety.

---

# 1. One-Line Definition

```text
Race condition happens when multiple threads access shared data concurrently
and final result depends on execution timing/order.
```

Simple meaning:

```text
Multiple threads modify same data unsafely.
```

Result:

```text
Wrong values
Corrupted data
Inconsistent state
Production bugs
```

---

# 2. Real Mental Model

Imagine:

```text
Bank balance = 1000
```

Two ATM machines withdraw simultaneously:

```text
ATM A withdraws 100
ATM B withdraws 200
```

Expected:

```text
700
```

But due to unsafe updates:

```text
900
or
800
```

This is race condition.

---

# 3. Why It Happens

Because operations that LOOK atomic are often multiple steps internally.

Example:

```java
counter++;
```

Looks simple.

Internally:

```text
1. read counter
2. add 1
3. write result
```

Multiple threads can interleave these steps.

---

# 4. Shared Memory Problem

Threads inside same process share heap memory.

Example:

```text
Process Heap:
counter = 0

Thread A accesses counter
Thread B accesses counter
```

Danger:

```text
Both threads modify same memory simultaneously.
```

---

# 5. Basic Java Race Condition Example

```java
public class RaceConditionDemo {

    static int counter = 0;

    public static void main(String[] args)
            throws InterruptedException {

        Thread t1 = new Thread(() -> {

            for (int i = 0; i < 100000; i++) {

                counter++;
            }
        });

        Thread t2 = new Thread(() -> {

            for (int i = 0; i < 100000; i++) {

                counter++;
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

Actual may be:

```text
173421
189990
195234
```

Different each run.

---

# 6. Step-by-Step Dry Run

Initial:

```text
counter = 0
```

Thread A:

```text
read counter = 0
```

Thread B:

```text
read counter = 0
```

Thread A:

```text
add 1 → 1
write 1
```

Thread B:

```text
add 1 → 1
write 1
```

Expected:

```text
2
```

Actual:

```text
1
```

One update is lost.

Called:

```text
Lost update
Race condition
```

---

# 7. Visual Timeline

```text
Thread A              Thread B

read 0
                       read 0

compute 1
                       compute 1

write 1
                       write 1
```

Problem:

```text
Both threads used stale value.
```

---

# 8. Why Result Changes Every Run

Thread scheduling is unpredictable.

OS scheduler decides:

```text
which thread runs
when it pauses
when it resumes
```

So race conditions are:

```text
non-deterministic
```

Hard to debug.

---

# 9. Very Important Reality

Race conditions may:

```text
Work perfectly in testing
Fail randomly in production
```

Why?

Because production has:

```text
More users
More CPU contention
More threads
Different scheduling
```

---

# 10. Spring Boot Real Example

Dangerous code:

```java
@RestController
public class OrderController {

    private int requestCount = 0;

    @GetMapping("/orders")
    public String orders() {

        requestCount++;

        return "count = " + requestCount;
    }
}
```

Why dangerous?

Because:

```text
Spring singleton bean shared by all request threads.
```

Many users call endpoint simultaneously.

Multiple Tomcat worker threads execute:

```text
requestCount++
```

Unsafely.

---

# 11. Backend Dry Run

```text
User A request
    ↓
Thread-1

User B request
    ↓
Thread-2
```

Both execute:

```java
requestCount++;
```

Possible corruption:

```text
Expected:
1002

Actual:
1001
```

---

# 12. Fix 1 — synchronized

```java
public class SyncDemo {

    static int counter = 0;

    public synchronized static void increment() {

        counter++;
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

Now result:

```text
200000
```

---

# 13. How synchronized Fixes Race Condition

`synchronized` provides:

```text
Mutual exclusion
```

Meaning:

```text
Only one thread enters critical section at a time.
```

Flow:

```text
Thread A enters increment()
Thread B waits
Thread A exits
Thread B enters
```

No overlap.

---

# 14. Important Term — Critical Section

Critical section means:

```text
Code accessing shared mutable data.
```

Example:

```java
counter++;
```

Critical sections require protection.

---

# 15. Fix 2 — AtomicInteger

Better modern approach:

```java
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicDemo {

    static AtomicInteger counter =
            new AtomicInteger(0);

    public static void main(String[] args)
            throws Exception {

        Thread t1 = new Thread(() -> {

            for (int i = 0; i < 100000; i++) {

                counter.incrementAndGet();
            }
        });

        Thread t2 = new Thread(() -> {

            for (int i = 0; i < 100000; i++) {

                counter.incrementAndGet();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(counter.get());
    }
}
```

Expected:

```text
200000
```

Safely.

---

# 16. Why AtomicInteger Works

Internally uses:

```text
CPU CAS operations
Compare-And-Swap
Hardware atomic instructions
```

Much faster than heavy locking in many cases.

---

# 17. synchronized vs AtomicInteger

| Feature | synchronized | AtomicInteger |
|---|---|---|
| Lock-based | Yes | No |
| Blocking | Yes | Usually no |
| Simpler | Yes | Moderate |
| Performance | Slower under contention | Faster |
| Multiple variables | Easier | Harder |

---

# 18. Race Condition in Collections

Dangerous:

```java
HashMap
ArrayList
LinkedList
```

Not thread-safe.

Example:

```java
Map<Integer, String> map =
        new HashMap<>();
```

Multiple threads modifying it may corrupt structure.

---

# 19. Safe Alternatives

Use:

```java
ConcurrentHashMap
CopyOnWriteArrayList
BlockingQueue
ConcurrentLinkedQueue
```

Example:

```java
ConcurrentHashMap<Integer, String> map =
        new ConcurrentHashMap<>();
```

---

# 20. ConcurrentHashMap Mental Model

Instead of locking entire map:

```text
Fine-grained locking
CAS
Bucket-level synchronization
```

Allows better concurrency.

---

# 21. Real Production Race Condition Examples

---

## Example 1 — Duplicate Payment

Two payment threads:

```text
Both see order unpaid
Both process payment
```

Result:

```text
Customer charged twice
```

---

## Example 2 — Overselling Tickets

Two threads:

```text
Seat count = 1
```

Both sell simultaneously.

Result:

```text
Two customers get same seat
```

---

## Example 3 — Inventory Corruption

```text
Stock = 5
```

Many threads reduce stock concurrently.

Final inventory becomes wrong.

---

# 22. Database Race Conditions

Even DB operations can race.

Example:

```text
SELECT balance
UPDATE balance
```

Two transactions overlap.

Need:

```text
Transaction isolation
Locks
Optimistic locking
Pessimistic locking
```

---

# 23. Kafka Consumer Race Conditions

Dangerous:

```text
Multiple consumer threads update shared cache
```

Without synchronization:

```text
Corrupted cache state
```

---

# 24. Redis Race Condition Example

Redis mostly avoids races by:

```text
Single-threaded command execution
```

Very important design choice.

Commands execute sequentially.

---

# 25. Important Backend Rule

```text
Shared mutable state is dangerous.
```

Best backend designs minimize shared mutable data.

---

# 26. Immutable Object Idea

Immutable objects are safer.

Example:

```java
String
Integer
LocalDate
```

Cannot change after creation.

Less race-condition risk.

---

# 27. Local Variable Safety

Local variables are thread-safe.

Example:

```java
public void test() {

    int x = 10;
}
```

Why?

Because:

```text
Each thread gets separate stack.
```

Problem mostly happens with:

```text
Shared heap objects
```

---

# 28. Race Condition Detection Is Hard

Why difficult?

Because:

```text
May fail only sometimes
Depends on timing
Depends on CPU scheduling
Depends on load
```

These bugs are often intermittent.

---

# 29. Production Symptoms

Race conditions may cause:

```text
Wrong counters
Duplicate records
Negative inventory
Corrupted cache
Random failures
Inconsistent state
```

Very difficult production issues.

---

# 30. Real Backend Mental Model

```text
Spring Boot Process
    |
    ├── Thread-1 updates counter
    ├── Thread-2 updates counter
    ├── Thread-3 updates counter
    └── Shared heap memory
```

Without synchronization:

```text
Unsafe shared access
```

---

# 31. Interview Explanation

If interviewer asks:

```text
What is race condition?
```

Good answer:

```text
Race condition happens when multiple threads access shared mutable data
concurrently and final result depends on timing/interleaving of execution.
It usually occurs because operations that appear atomic internally involve
multiple steps like read-modify-write.
```

Strong backend addition:

```text
In Spring Boot, singleton beans are shared across request threads,
so mutable shared state must be synchronized or use thread-safe structures.
```

---

# 32. Common Mistakes

## Mistake 1

```text
Thinking counter++ is atomic.
```

Wrong.

---

## Mistake 2

```text
Using HashMap concurrently.
```

Dangerous.

---

## Mistake 3

```text
Thinking bugs appear every run.
```

Race conditions are timing dependent.

---

## Mistake 4

```text
Synchronizing everything blindly.
```

Can hurt performance badly.

---

# 33. Mini Dry Run Summary

```text
Two threads share same variable
    ↓
Both read old value
    ↓
Both compute new value
    ↓
One update overwritten
    ↓
Incorrect result
```

---

# 34. Visual Summary

```text
Shared Data
     ↓
Multiple Threads
     ↓
Unsafe Concurrent Access
     ↓
Race Condition
     ↓
Wrong Result
```

Fix using:

```text
synchronized
AtomicInteger
ConcurrentHashMap
Locks
Immutable design
```

---

# 35. What To Remember

```text
Race condition = unsafe concurrent shared-data access.

counter++ is NOT thread-safe.

Shared mutable state is dangerous.

Use synchronization or atomic/thread-safe structures.
```

---

# 36. Next File

```text
006_Critical_Section.md
```

Next you learn:

```text
What exactly must be protected
Granularity of synchronization
Lock scope
Performance tradeoffs
```
