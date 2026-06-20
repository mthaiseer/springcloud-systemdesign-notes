# 001_What_Is_Concurrency

## 0. Where This File Fits

```text
MiniConcurrency/
├── 001_What_Is_Concurrency.md   ✅ You are here
├── 002_Process_vs_Thread.md
├── 003_Thread_Lifecycle.md
├── 004_Context_Switching.md
├── 005_Race_Condition.md
├── 006_Critical_Section.md
├── 007_Mutex_Lock.md
├── 008_ReentrantLock.md
├── 009_Read_Write_Lock.md
├── 010_Atomic_Variables.md
├── 011_volatile_Keyword.md
├── 012_Thread_Communication.md
├── 013_Wait_Notify.md
├── 014_BlockingQueue.md
├── 015_Producer_Consumer.md
├── 016_Deadlock.md
├── 017_Livelock_Starvation.md
├── 018_ThreadPool_Basics.md
├── 019_Future_CompletableFuture.md
└── 020_Production_Grade_Concurrency.md
```

---

# 1. What Is Concurrency?

## Simple Definition

Concurrency means:

```text
Multiple tasks are in progress during the same time period.
```

It does **not always mean** tasks are running at the exact same nanosecond.

It means the system is handling multiple tasks by switching between them or running them on multiple CPU cores.

---

## Simple Mental Model

Imagine one chef cooking multiple dishes:

```text
Boil rice
    ↓
While rice is boiling, cut vegetables
    ↓
While vegetables cook, prepare sauce
    ↓
Serve all together
```

The chef is not doing every action at the exact same time.

But multiple tasks are **in progress together**.

That is concurrency.

---

# 2. Concurrency vs Parallelism

## Concurrency

```text
One person handling many tasks by switching between them.
```

Example:

```text
Single CPU core
    ↓
Thread A runs for some time
Thread B runs for some time
Thread C runs for some time
```

Looks like many things are happening together.

Actually, the CPU is switching fast.

---

## Parallelism

```text
Many people doing many tasks at the same exact time.
```

Example:

```text
Multiple CPU cores
    ↓
Core 1 runs Thread A
Core 2 runs Thread B
Core 3 runs Thread C
```

This is true simultaneous execution.

---

## Difference Table

| Concept | Meaning | Example |
|---|---|---|
| Concurrency | Multiple tasks in progress | One CPU switching between threads |
| Parallelism | Multiple tasks running at same time | Multiple CPU cores running multiple threads |

---

# 3. Why Concurrency Exists

Modern backend systems do not handle one request at a time.

They handle many users together.

Example:

```text
User 1 → login
User 2 → payment
User 3 → search
User 4 → upload file
User 5 → checkout
```

If the server processes only one request at a time:

```text
Request 1 finishes
    ↓
Request 2 starts
    ↓
Request 3 starts
```

The system becomes very slow.

Concurrency allows:

```text
Many requests to be handled together.
```

---

# 4. Real Backend Example

Suppose your Spring Boot service receives 3 requests:

```text
Request A → get order
Request B → make payment
Request C → send notification
```

Without concurrency:

```text
A runs
A finishes
B runs
B finishes
C runs
C finishes
```

With concurrency:

```text
A starts
B starts
C starts

All are in progress together
```

This improves throughput.

---

# 5. First Java Example: Sequential Execution

## Code

```java
public class SequentialExample {

    public static void main(String[] args) throws InterruptedException {

        processTask("Task-A");
        processTask("Task-B");
        processTask("Task-C");
    }

    static void processTask(String taskName) throws InterruptedException {

        System.out.println(taskName + " started by " + Thread.currentThread().getName());

        // Simulate slow work: DB call, API call, file read, etc.
        Thread.sleep(1000);

        System.out.println(taskName + " finished by " + Thread.currentThread().getName());
    }
}
```

---

## Output Shape

```text
Task-A started by main
Task-A finished by main
Task-B started by main
Task-B finished by main
Task-C started by main
Task-C finished by main
```

---

## Step-by-Step Dry Run

```text
main thread starts
    ↓
processTask("Task-A")
    ↓
Task-A sleeps for 1 second
    ↓
Task-A finishes
    ↓
processTask("Task-B")
    ↓
Task-B sleeps for 1 second
    ↓
Task-B finishes
    ↓
processTask("Task-C")
    ↓
Task-C sleeps for 1 second
    ↓
Task-C finishes
```

Total approximate time:

```text
1 second + 1 second + 1 second = 3 seconds
```

---

# 6. Java Example: Concurrent Execution Using Threads

## Code

```java
public class ConcurrentExample {

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> processTask("Task-A"));
        Thread t2 = new Thread(() -> processTask("Task-B"));
        Thread t3 = new Thread(() -> processTask("Task-C"));

        t1.start();
        t2.start();
        t3.start();
    }

    static void processTask(String taskName) {

        try {
            System.out.println(taskName + " started by " + Thread.currentThread().getName());

            // Simulate slow work: DB call, API call, file read, etc.
            Thread.sleep(1000);

            System.out.println(taskName + " finished by " + Thread.currentThread().getName());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(taskName + " interrupted");
        }
    }
}
```

---

## Output Shape

Output order is not guaranteed.

Example:

```text
Task-A started by Thread-0
Task-B started by Thread-1
Task-C started by Thread-2
Task-B finished by Thread-1
Task-A finished by Thread-0
Task-C finished by Thread-2
```

---

## Step-by-Step Dry Run

```text
main thread starts
    ↓
creates Thread-0 for Task-A
    ↓
creates Thread-1 for Task-B
    ↓
creates Thread-2 for Task-C
    ↓
t1.start()
    ↓
Task-A begins in separate thread
    ↓
t2.start()
    ↓
Task-B begins in separate thread
    ↓
t3.start()
    ↓
Task-C begins in separate thread
```

Now all tasks are sleeping/working during the same time period.

Approximate total time:

```text
Around 1 second, not 3 seconds
```

Because all three tasks are in progress together.

---

# 7. Important Point: start() vs run()

## Correct: start()

```java
Thread t = new Thread(() -> System.out.println("Running"));
t.start();
```

`start()` creates a new thread and executes the task in that new thread.

---

## Wrong for concurrency: run()

```java
Thread t = new Thread(() -> System.out.println("Running"));
t.run();
```

`run()` does **not** create a new thread.

It runs like a normal method call inside the current thread.

---

## Mental Model

```text
start() → new worker starts
run()   → current worker does the job directly
```

---

# 8. Java Example: start() vs run()

## Code

```java
public class StartVsRunExample {

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            System.out.println("Using start(): " + Thread.currentThread().getName());
        });

        Thread t2 = new Thread(() -> {
            System.out.println("Using run(): " + Thread.currentThread().getName());
        });

        t1.start(); // runs in new thread
        t2.run();   // runs in main thread
    }
}
```

---

## Output Shape

```text
Using run(): main
Using start(): Thread-0
```

Order may change, but the important point is:

```text
run()   → main
start() → Thread-0
```

---

# 9. Why Backend Engineers Need Concurrency

Concurrency appears everywhere in backend systems.

## Spring Boot

```text
Multiple HTTP requests handled by multiple threads.
```

## Redis

```text
Single-threaded event loop for command execution,
but still handles many clients using network multiplexing.
```

## Kafka

```text
Consumers process messages concurrently using consumer groups and worker threads.
```

## API Gateway

```text
Many incoming requests are routed to backend services concurrently.
```

## Database

```text
Many clients read/write together.
Locks, transactions, and isolation become important.
```

---

# 10. Core Problems Created by Concurrency

Concurrency is powerful but dangerous.

It introduces problems like:

```text
Race condition
Deadlock
Thread starvation
Data inconsistency
Visibility issues
Lock contention
Hard-to-reproduce bugs
```

This is why concurrency is not only about creating threads.

It is about creating **safe concurrent execution**.

---

# 11. Small Race Condition Preview

You will study this deeply in:

```text
005_Race_Condition.md
```

But here is a small preview.

---

## Code With Bug

```java
public class CounterRaceExample {

    static int counter = 0;

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> incrementManyTimes());
        Thread t2 = new Thread(() -> incrementManyTimes());

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final counter = " + counter);
    }

    static void incrementManyTimes() {
        for (int i = 0; i < 100_000; i++) {
            counter++;
        }
    }
}
```

---

## Expected Output

```text
Final counter = 200000
```

---

## Possible Actual Output

```text
Final counter = 173421
```

or

```text
Final counter = 189002
```

or some other incorrect value.

---

## Why?

Because:

```java
counter++;
```

is not one atomic operation.

It is actually:

```text
1. read counter
2. add 1
3. write counter
```

Two threads can overlap.

---

## Dry Run of Bug

Suppose counter is 5.

```text
Thread A reads counter = 5
Thread B reads counter = 5
Thread A adds 1 → 6
Thread B adds 1 → 6
Thread A writes 6
Thread B writes 6
```

Expected:

```text
5 + 2 = 7
```

Actual:

```text
6
```

One update is lost.

This is called:

```text
Race Condition
```

---

# 12. Safe Version Preview Using AtomicInteger

You will study this deeply in:

```text
010_Atomic_Variables.md
```

---

## Code

```java
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounterExample {

    static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> incrementManyTimes());
        Thread t2 = new Thread(() -> incrementManyTimes());

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final counter = " + counter.get());
    }

    static void incrementManyTimes() {
        for (int i = 0; i < 100_000; i++) {
            counter.incrementAndGet();
        }
    }
}
```

---

## Output

```text
Final counter = 200000
```

---

## Why This Works

`AtomicInteger` makes increment safe for multiple threads.

Mental model:

```text
Only one successful update happens at a time.
No lost update.
```

---

# 13. Layer-by-Layer Understanding

```text
Single thread
    ↓
Multiple threads
    ↓
Shared memory
    ↓
Race condition risk
    ↓
Need synchronization
    ↓
Locks / atomic variables
    ↓
Safe communication
    ↓
Thread pools
    ↓
Async backend systems
```

---

# 14. Where Concurrency Appears in Your Mini Systems

## MiniRedis

```text
Multiple clients connect to Redis.
Redis must handle many network connections safely.
```

## MiniKafka

```text
Many producers publish messages.
Many consumers read messages.
Partitions allow parallel consumption.
```

## MiniGateway

```text
Many client requests enter at the same time.
Gateway routes them concurrently.
```

## MiniScheduler

```text
Multiple jobs may run in parallel.
Need safe job locking and execution.
```

## MiniDatabase

```text
Many transactions read/write shared data.
Need locking, MVCC, isolation levels.
```

## MiniSpringBootCloud

```text
Thread pools handle HTTP requests, async tasks, Kafka listeners, DB calls.
```

---

# 15. Real-World Backend Flow

```text
Client Request
    ↓
Spring Boot Tomcat thread accepts request
    ↓
Controller runs
    ↓
Service calls DB / Redis / Kafka
    ↓
Thread waits or continues async
    ↓
Response returned
```

If 100 users call the API:

```text
100 requests
    ↓
handled by worker threads
    ↓
thread pool controls concurrency
```

Without concurrency:

```text
Only one request at a time
```

With concurrency:

```text
Many requests in progress together
```

---

# 16. Interview Explanation

If interviewer asks:

```text
What is concurrency?
```

Answer:

```text
Concurrency is the ability of a system to handle multiple tasks during the same time period.
In Java backend systems, concurrency usually means multiple threads handling multiple requests, jobs, or messages together.
It improves throughput, but it introduces problems like race conditions, deadlocks, visibility issues, and lock contention.
That is why we use synchronization, locks, atomic variables, blocking queues, thread pools, and async patterns.
```

---

# 17. Common Mistakes

## Mistake 1: Thinking concurrency always means parallelism

Wrong:

```text
Concurrency = always same-time execution
```

Correct:

```text
Concurrency = multiple tasks in progress
Parallelism = multiple tasks executing at the exact same time
```

---

## Mistake 2: Creating unlimited threads

Wrong:

```text
One request = one new thread forever
```

This can crash the system.

Correct:

```text
Use thread pools.
```

---

## Mistake 3: Sharing mutable data without protection

Wrong:

```java
counter++;
```

inside multiple threads without synchronization.

Correct options:

```java
synchronized
Lock
AtomicInteger
ConcurrentHashMap
BlockingQueue
```

---

# 18. Production Checklist

When using concurrency in production, ask:

```text
1. What data is shared?
2. Can multiple threads modify it?
3. Is the operation atomic?
4. Do I need a lock?
5. Can this deadlock?
6. Can this block too long?
7. Is thread pool size controlled?
8. What happens under high load?
9. What metrics should I monitor?
10. What is the fallback if threads are exhausted?
```

---

# 19. Mini Summary

```text
Concurrency = multiple tasks in progress together.
```

It helps backend systems handle:

```text
many users
many requests
many DB calls
many Kafka messages
many background jobs
```

But it introduces:

```text
race conditions
deadlocks
visibility issues
thread contention
```

So production concurrency needs:

```text
locks
atomic variables
thread pools
safe queues
timeouts
monitoring
```

---

# 20. Final Mental Model

```text
Concurrency is not about starting many threads randomly.

Concurrency is about safely coordinating many tasks
so the system becomes faster, scalable, and correct.
```

---

# 21. Next File

```text
002_Process_vs_Thread.md
```

Next you should understand:

```text
Process = independent running program
Thread  = lightweight execution path inside a process
```

This is the foundation for understanding Java backend execution.
