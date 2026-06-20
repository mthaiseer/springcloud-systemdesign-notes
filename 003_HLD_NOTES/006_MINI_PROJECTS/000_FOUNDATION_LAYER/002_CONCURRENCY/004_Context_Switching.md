# 004_Context_Switching.md

# MiniConcurrency — 004 Context Switching

## 0. Why This File Exists

Before learning:

```text
ThreadPool
ExecutorService
Async programming
Reactive systems
Performance tuning
```

you must understand:

```text
How CPU switches between threads
Why too many threads reduce performance
What happens internally during switching
Why thread pools exist
```

This file explains one of the most important backend performance concepts.

---

# 1. One-Line Definition

```text
Context switching = CPU pauses one thread/process and starts another.
```

The CPU saves old execution state and restores new execution state.

---

# 2. Why Context Switching Exists

CPU cores are limited.

Example:

```text
Machine has:
8 CPU cores

Application has:
500 threads
```

CPU cannot run 500 threads simultaneously.

So OS scheduler continuously switches between them.

---

# 3. Real Mental Model

Imagine:

```text
One chef
Many food orders
```

Chef works:

```text
Order A for few seconds
→ pause

Order B
→ pause

Order C
→ pause

Back to Order A
```

This is similar to CPU scheduling.

---

# 4. Important Reality

Very important:

```text
Concurrency != parallelism
```

---

## Concurrency

```text
Tasks make progress together.
```

Can happen even on:

```text
single CPU core
```

via switching.

---

## Parallelism

```text
Tasks truly run simultaneously.
```

Requires:

```text
multiple CPU cores
```

---

# 5. Big Picture Flow

```text
Thread A running
      ↓
Time slice expires
      ↓
OS pauses Thread A
      ↓
CPU state saved
      ↓
OS picks Thread B
      ↓
CPU state restored
      ↓
Thread B runs
```

This is:

```text
Context switching
```

---

# 6. What Is “Context”?

Context means execution state.

Includes:

```text
CPU registers
Program counter
Stack pointer
Thread state
Memory mapping
Scheduling metadata
```

---

# 7. Very Important Internal Idea

CPU must remember:

```text
Where Thread A stopped.
```

Otherwise execution cannot continue later.

So OS saves:

```text
Instruction position
Variables
Stack state
CPU register values
```

Then restores later.

---

# 8. Context Switching Diagram

```text
CPU
 |
 +----------------------+
 | Running Thread A     |
 +----------------------+
            |
            | switch
            v
Save A context
Load B context
            |
            v
 +----------------------+
 | Running Thread B     |
 +----------------------+
```

---

# 9. Time Slice / Quantum

OS gives small execution window:

```text
2ms
5ms
10ms
```

called:

```text
Time slice
CPU quantum
```

After that:

```text
scheduler may switch thread
```

---

# 10. CPU Scheduler

OS scheduler decides:

```text
Which thread runs next.
```

Scheduler considers:

```text
Priority
Fairness
CPU usage
Waiting time
IO wait
```

---

# 11. Example — Single Core CPU

Imagine:

```text
1 CPU core
3 threads
```

Execution:

```text
Time 0-5ms   → Thread A
Time 5-10ms  → Thread B
Time 10-15ms → Thread C
Time 15-20ms → Thread A
```

User feels:

```text
All threads running simultaneously
```

Actually:

```text
CPU rapidly switching
```

---

# 12. Java Example — Many Threads

```java
public class ContextSwitchDemo {

    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {

            Thread t = new Thread(() -> {

                for (int j = 0; j < 5; j++) {

                    System.out.println(
                        Thread.currentThread().getName()
                    );

                    try {

                        Thread.sleep(100);

                    } catch (Exception e) {

                    }
                }
            });

            t.start();
        }
    }
}
```

Possible output:

```text
Thread-0
Thread-1
Thread-2
Thread-0
Thread-3
Thread-1
```

Why mixed?

Because scheduler keeps switching threads.

---

# 13. Dry Run — Context Switching

```text
Step 1:
Thread-0 starts running.

Step 2:
CPU time slice ends.

Step 3:
OS saves Thread-0 context.

Step 4:
OS loads Thread-1 context.

Step 5:
Thread-1 runs.

Step 6:
Later CPU switches again.
```

This happens continuously.

---

# 14. Context Switch Cost

Important:

```text
Context switching is NOT free.
```

Switching requires:

```text
Save registers
Restore registers
Kernel scheduling work
Cache invalidation
Memory synchronization
```

Too many switches hurt performance.

---

# 15. Why Too Many Threads Hurt Performance

Many beginners think:

```text
More threads = faster
```

Wrong.

Too many threads cause:

```text
Heavy context switching
Memory overhead
CPU cache misses
Scheduler overhead
Thread contention
```

Eventually performance decreases.

---

# 16. Important Backend Reality

Imagine:

```text
5000 threads
8 CPU cores
```

CPU constantly switches threads.

Result:

```text
High CPU usage
Low throughput
Poor latency
Slow system
```

This is why:

```text
Thread pools exist
```

---

# 17. Thread Pool Motivation

Instead of:

```text
Create new thread per request
```

Backend systems use:

```text
Fixed reusable worker threads
```

Example:

```text
200 worker threads
handle
10000 requests over time
```

Benefits:

```text
Less context switching
Less memory usage
Better CPU utilization
```

---

# 18. Spring Boot Example

Tomcat default worker thread pool:

```text
~200 request threads
```

Flow:

```text
HTTP request arrives
    ↓
Tomcat worker thread handles request
    ↓
Response sent
    ↓
Thread returned to pool
```

Without pool:

```text
new thread per request
```

would destroy performance.

---

# 19. Context Switching vs Blocking IO

Very important backend concept.

Imagine thread:

```text
waiting for database response
```

CPU should not waste time.

So scheduler switches to another thread.

Flow:

```text
Thread A waiting DB
    ↓
OS pauses A
    ↓
CPU runs Thread B
```

This improves utilization.

---

# 20. Backend Example — Kafka

Kafka broker may have:

```text
network threads
IO threads
replication threads
```

OS continuously schedules them.

Too many broker threads:

```text
→ high context switching
→ poor throughput
```

---

# 21. CPU-Bound vs IO-Bound

Very important.

---

## CPU-Bound

Threads mostly use CPU.

Example:

```text
Image processing
Encryption
Compression
Heavy calculations
```

Too many threads are bad.

---

## IO-Bound

Threads mostly wait.

Example:

```text
DB calls
HTTP calls
Redis calls
Kafka polling
File reading
```

More threads may help.

---

# 22. Rule of Thumb

For CPU-bound work:

```text
Threads ≈ CPU cores
```

For IO-bound work:

```text
Can use more threads
because many wait frequently.
```

---

# 23. Cache Problem During Context Switching

CPU has fast cache memory.

When switching threads:

```text
CPU cache may become invalid.
```

Then CPU reloads data from RAM.

This slows performance.

Called:

```text
Cache miss
```

---

# 24. User Mode vs Kernel Mode

Context switching usually involves:

```text
User mode
→ kernel mode
→ scheduler work
→ back to user mode
```

Kernel transition itself has overhead.

---

# 25. Real Production Symptoms

Too many context switches may show:

```text
High CPU
Low throughput
Slow APIs
Scheduler overhead
Latency spikes
Thread pool exhaustion
```

Linux tools:

```bash
top
htop
vmstat
pidstat
```

help diagnose.

---

# 26. Java Thread Dump Example

```bash
jstack <pid>
```

May show:

```text
RUNNABLE threads
WAITING threads
BLOCKED threads
```

If thousands exist:

```text
possible thread explosion
```

---

# 27. Bad Backend Design Example

```java
@GetMapping("/orders")
public String orders() {

    new Thread(() -> {
        process();
    }).start();

    return "ok";
}
```

Danger:

```text
Every request creates new thread.
```

Under load:

```text
10000 requests
→ 10000 threads
→ massive context switching
→ memory crash
```

Very bad design.

---

# 28. Better Design

Use thread pool:

```java
ExecutorService executor =
    Executors.newFixedThreadPool(20);
```

Benefits:

```text
Controlled thread count
Lower switching overhead
Stable system
```

---

# 29. Context Switching vs Async

Modern high-scale systems try to reduce thread blocking.

Example:

```text
Reactive programming
Netty
WebFlux
Event loops
Async IO
```

Why?

Because:

```text
Less blocking
Less threads
Less context switching
Higher scalability
```

---

# 30. Netty Mental Model

Traditional:

```text
1 request = 1 thread
```

Netty:

```text
few event-loop threads
handle many connections asynchronously
```

This reduces context switching massively.

---

# 31. Real Backend Mental Model

```text
Spring Boot Process
    |
    ├── Request Thread
    ├── Kafka Thread
    ├── Scheduler Thread
    ├── DB Pool Thread
    └── Async Thread
```

OS continuously switches between them.

---

# 32. Important Interview Explanation

If interviewer asks:

```text
What is context switching?
```

Good answer:

```text
Context switching is the process where CPU pauses one thread/process
and resumes another by saving/restoring execution state.
It enables concurrency on limited CPU cores but introduces overhead.
Too many threads can hurt performance due to excessive context switching.
```

Strong backend addition:

```text
Thread pools reduce thread creation and excessive context switching
in backend systems like Spring Boot.
```

---

# 33. Common Mistakes

## Mistake 1

```text
Thinking more threads always improve performance.
```

Wrong.

---

## Mistake 2

```text
Creating new thread per request.
```

Dangerous.

---

## Mistake 3

```text
Ignoring CPU core count.
```

Important for tuning thread pools.

---

## Mistake 4

```text
Confusing concurrency with parallelism.
```

Different concepts.

---

# 34. Mini Dry Run Summary

```text
CPU runs Thread A
    ↓
time slice ends
    ↓
save Thread A context
    ↓
load Thread B context
    ↓
Thread B runs
```

This happens millions of times.

---

# 35. Visual Summary

```text
Few CPU cores
      ↓
Many threads compete
      ↓
OS scheduler switches continuously
      ↓
Too many switches
      ↓
Performance degradation
```

---

# 36. What To Remember

```text
Context switching = switching execution between threads/processes.

It has overhead.

Too many threads hurt performance.

Thread pools reduce switching overhead.

Modern async systems try to reduce blocking + switching.
```

---

# 37. Next File

```text
005_Race_Condition.md
```

Next you learn:

```text
Shared memory corruption
Lost updates
Unsafe concurrency
Why synchronization is needed
```
