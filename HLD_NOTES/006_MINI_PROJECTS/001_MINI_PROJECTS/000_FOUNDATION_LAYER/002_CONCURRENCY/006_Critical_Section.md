# 006_Critical_Section.md

# MiniConcurrency — 006 Critical Section

## 0. Why This File Exists

After understanding race conditions, the next question is:

```text
Exactly WHICH code must be protected?
```

Answer:

```text
Critical section
```

This file teaches:

```text
What critical sections are
Why they are dangerous
How synchronization protects them
How lock scope affects performance
How backend systems reduce contention
```

This is one of the most important concurrency design concepts.

---

# 1. One-Line Definition

```text
Critical section = code section accessing shared mutable data
that must not be executed by multiple threads simultaneously.
```

Simple meaning:

```text
Shared data update area.
```

---

# 2. Real Mental Model

Imagine:

```text
One ATM cash machine drawer
```

Only one employee should access it at a time.

Why?

Because:

```text
Money count may become corrupted.
```

That protected area is similar to:

```text
Critical section
```

---

# 3. Why Critical Sections Exist

Threads share heap memory.

Example:

```java
counter++;
```

Multiple threads executing simultaneously may corrupt value.

So:

```text
Critical section must be protected.
```

---

# 4. Basic Example

Unsafe code:

```java
counter++;
```

Internally:

```text
1. read counter
2. add 1
3. write result
```

This entire sequence is the critical section.

---

# 5. Visual Flow

```text
Shared Variable
      ↓
Multiple Threads
      ↓
Critical Section
      ↓
Race Condition Risk
```

---

# 6. Java Example — Unsafe Critical Section

```java
public class UnsafeCounter {

    static int counter = 0;

    public static void increment() {

        counter++;
    }
}
```

Problem:

```text
Multiple threads can enter increment() together.
```

Danger:

```text
Race condition
Lost updates
```

---

# 7. Step-by-Step Dry Run

Initial:

```text
counter = 5
```

Thread A:

```text
read 5
```

Thread B:

```text
read 5
```

Thread A:

```text
write 6
```

Thread B:

```text
write 6
```

Expected:

```text
7
```

Actual:

```text
6
```

Critical section executed concurrently.

---

# 8. Protecting Critical Section

Use synchronization.

Example:

```java
public synchronized void increment() {

    counter++;
}
```

Now:

```text
Only one thread enters at a time.
```

---

# 9. synchronized Mental Model

```text
Thread acquires lock
    ↓
Executes critical section
    ↓
Releases lock
```

Other threads must wait.

---

# 10. Java Example — Safe Critical Section

```java
public class SafeCounter {

    static int counter = 0;

    public synchronized static void increment() {

        counter++;
    }
}
```

Now:

```text
increment() becomes protected critical section.
```

---

# 11. Dry Run — synchronized

```text
Thread A enters increment()
    ↓
Acquires lock
    ↓
Updates counter
    ↓
Releases lock

Thread B waiting
    ↓
Gets lock after A finishes
```

No overlap.

No corruption.

---

# 12. Important Idea — Mutual Exclusion

Critical section protection provides:

```text
Mutual exclusion
```

Meaning:

```text
Only one thread allowed inside protected code.
```

---

# 13. Critical Section Scope Matters

Very important backend concept.

Bad:

```java
public synchronized void process() {

    validate();
    callDatabase();
    callExternalAPI();
    counter++;
    generatePdf();
}
```

Problem:

```text
Huge critical section.
```

Other threads blocked too long.

---

# 14. Better Design

Protect only shared update.

```java
public void process() {

    validate();

    callDatabase();

    callExternalAPI();

    synchronized(this) {

        counter++;
    }

    generatePdf();
}
```

Now:

```text
Small critical section.
Better concurrency.
```

---

# 15. Lock Granularity

Very important term.

---

## Coarse-Grained Lock

Large protected area.

Example:

```text
Whole method synchronized.
```

Advantages:

```text
Simple
Safer
```

Disadvantages:

```text
Poor concurrency
More blocking
Lower throughput
```

---

## Fine-Grained Lock

Small protected area.

Advantages:

```text
Better performance
Higher concurrency
```

Disadvantages:

```text
More complex
Harder debugging
```

---

# 16. Real Backend Example

Imagine:

```text
1000 requests
```

Only this line needs protection:

```java
inventory--;
```

Do NOT lock entire request processing flow.

Only protect:

```text
Shared mutable state update.
```

---

# 17. Spring Boot Example

Bad:

```java
@RestController
public class OrderController {

    private int count = 0;

    @GetMapping("/orders")
    public synchronized String orders() {

        callDatabase();

        callExternalAPI();

        count++;

        return "ok";
    }
}
```

Problem:

```text
Only one request handled at a time.
```

Terrible scalability.

---

# 18. Better Spring Boot Design

```java
@RestController
public class OrderController {

    private int count = 0;

    @GetMapping("/orders")
    public String orders() {

        callDatabase();

        callExternalAPI();

        synchronized(this) {

            count++;
        }

        return "ok";
    }
}
```

Now:

```text
Only count update protected.
```

Much better throughput.

---

# 19. Important Performance Tradeoff

Critical sections provide safety.

But too much locking causes:

```text
Thread contention
Blocking
Poor CPU utilization
Lower throughput
High latency
```

Concurrency design is balance between:

```text
Safety
vs
Performance
```

---

# 20. Lock Contention

Definition:

```text
Many threads competing for same lock.
```

Example:

```text
200 threads waiting for one synchronized block.
```

Symptoms:

```text
BLOCKED threads
Slow APIs
High latency
Low throughput
```

---

# 21. Production Mental Model

```text
Many request threads
       ↓
Same critical section
       ↓
Heavy lock contention
       ↓
Performance degradation
```

---

# 22. Fine-Grained Design Example

Instead of:

```java
synchronized entireMap
```

Use:

```text
Bucket-level locking
Segment locking
Partitioned locking
```

This is how:

```text
ConcurrentHashMap
Kafka
Databases
```

improve scalability.

---

# 23. ConcurrentHashMap Idea

Instead of one giant lock:

```text
Multiple smaller locks
```

Benefits:

```text
Different threads work independently.
```

Higher throughput.

---

# 24. Critical Section in Database

Example:

```text
Deduct inventory
```

Must be protected.

Otherwise:

```text
Overselling products
Negative stock
Duplicate seat booking
```

Real systems use:

```text
DB locks
Transactions
Isolation levels
Optimistic locking
```

---

# 25. Redis Design Insight

Redis avoids many race conditions by:

```text
Single-threaded command execution
```

Only one command modifies data at a time.

Very important design choice.

---

# 26. Kafka Design Insight

Kafka reduces contention using:

```text
Partitioning
Sequential append logs
```

Instead of many threads modifying same structure heavily.

---

# 27. Read-Only Access

Important:

```text
Reading immutable/shared read-only data
usually safe.
```

Danger mostly happens with:

```text
Shared mutable writes.
```

---

# 28. Local Variables Usually Safe

Example:

```java
public void test() {

    int x = 10;
}
```

Safe because:

```text
Each thread has separate stack memory.
```

Not shared.

---

# 29. Multiple Critical Sections

Example:

```java
balance update
inventory update
cache update
```

Each may need separate synchronization strategy.

---

# 30. Deadlock Risk

Too many locks may cause:

```text
Deadlock
```

Example:

```text
Thread A waiting lock B
Thread B waiting lock A
```

Will learn later.

---

# 31. Real Production Symptoms

Bad critical section design may cause:

```text
High BLOCKED threads
Slow APIs
CPU underutilized
Poor throughput
Latency spikes
Thread starvation
```

---

# 32. Interview Explanation

If interviewer asks:

```text
What is critical section?
```

Good answer:

```text
Critical section is the portion of code that accesses shared mutable data
and must be protected from concurrent execution to avoid race conditions.
Synchronization mechanisms like synchronized blocks, locks, or atomics
are used to protect critical sections.
```

Strong backend addition:

```text
Good backend systems minimize critical section size to reduce contention
and improve scalability.
```

---

# 33. Common Mistakes

## Mistake 1

```text
Synchronizing huge methods unnecessarily.
```

Hurts scalability.

---

## Mistake 2

```text
Protecting too little code.
```

Still unsafe.

---

## Mistake 3

```text
Ignoring lock contention.
```

Very important production issue.

---

## Mistake 4

```text
Thinking all shared reads are dangerous.
```

Mostly shared writes are dangerous.

---

# 34. Mini Dry Run Summary

```text
Shared mutable variable
      ↓
Critical section identified
      ↓
Protected with synchronization
      ↓
Only one thread enters
      ↓
Race condition prevented
```

---

# 35. Visual Summary

```text
Shared Data
    ↓
Critical Section
    ↓
Need Protection
    ↓
Lock / synchronized / atomic
    ↓
Thread Safety
```

---

# 36. What To Remember

```text
Critical section = shared mutable data access area.

Protect only necessary code.

Large locks reduce performance.

Small critical sections improve scalability.
```

---

# 37. Next File

```text
007_Mutex_Lock.md
```

Next you learn:

```text
What locks actually are
Monitor locking
Mutex concept
Lock acquisition/release
Thread blocking behavior
```
