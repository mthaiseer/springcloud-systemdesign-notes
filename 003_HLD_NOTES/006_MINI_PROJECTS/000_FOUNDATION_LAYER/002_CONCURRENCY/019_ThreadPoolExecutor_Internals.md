# 019_ThreadPoolExecutor_Internals.md

# MiniConcurrency — 019 ThreadPoolExecutor Internals

## 0. Why This File Exists

In previous file you learned:

```text
Thread pools basics
Fixed thread pool
Cached thread pool
submit vs execute
Future
```

But real backend systems do NOT usually use:

```java
Executors.newFixedThreadPool()
```

directly in production.

Why?

Because production systems need control over:

```text
thread count
queue size
timeouts
rejection policy
backpressure
memory usage
worker lifecycle
```

The real engine behind Java thread pools is:

```text
ThreadPoolExecutor
```

This file teaches:

```text
ThreadPoolExecutor internals
corePoolSize
maximumPoolSize
keepAliveTime
work queue
worker lifecycle
task flow
rejection policies
queue strategies
production tuning
Spring Boot mapping
backend mental models
```

---

# 1. One-Line Definition

```text
ThreadPoolExecutor is the core Java thread pool implementation that manages worker threads and task queues.
```

Simple meaning:

```text
Task manager for reusable worker threads.
```

---

# 2. Real Mental Model

Imagine restaurant manager.

Manager controls:

```text
how many waiters exist
how many customers can wait
when to hire temporary workers
when to reject customers
```

ThreadPoolExecutor does same for tasks.

---

# 3. High-Level Flow

```text
Task submitted
      ↓
Worker available?
      ↓ yes
Run task immediately

      ↓ no
Put into queue

Queue full?
      ↓ yes
Create extra thread?

Max threads reached?
      ↓ yes
Reject task
```

---

# 4. Internal Components

ThreadPoolExecutor has:

```text
Worker threads
Task queue
Thread factory
Rejection handler
Pool state
```

---

# 5. Core Constructor

```java
ThreadPoolExecutor executor =
    new ThreadPoolExecutor(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        timeUnit,
        workQueue,
        threadFactory,
        rejectionHandler
    );
```

This is the real production constructor.

---

# 6. Parameters Overview

| Parameter | Meaning |
|---|---|
| corePoolSize | Minimum worker threads kept alive |
| maximumPoolSize | Maximum allowed threads |
| keepAliveTime | Idle timeout for extra threads |
| workQueue | Queue for waiting tasks |
| threadFactory | Creates worker threads |
| rejectionHandler | Handles rejected tasks |

---

# 7. corePoolSize

```text
Minimum number of worker threads kept alive.
```

Example:

```java
corePoolSize = 4
```

Meaning:

```text
Pool keeps at least 4 workers ready.
```

Even if idle.

---

# 8. maximumPoolSize

```text
Maximum number of worker threads allowed.
```

Example:

```java
maximumPoolSize = 10
```

Meaning:

```text
Pool may grow from 4 to 10 threads under heavy load.
```

---

# 9. keepAliveTime

```text
How long extra threads stay alive when idle.
```

Example:

```java
keepAliveTime = 60 seconds
```

Meaning:

```text
threads above core size die after 60s idle
```

---

# 10. workQueue

```text
Stores waiting tasks.
```

Example:

```java
new ArrayBlockingQueue<>(100)
```

Meaning:

```text
maximum 100 waiting tasks
```

---

# 11. Rejection Handler

When:

```text
queue full
AND max threads reached
```

new task cannot be accepted.

Then:

```text
rejection policy executes
```

---

# 12. Full Constructor Example

```java
import java.util.concurrent.*;

public class ThreadPoolExecutorDemo {

    public static void main(String[] args) {

        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(
                        2,
                        4,
                        60,
                        TimeUnit.SECONDS,
                        new ArrayBlockingQueue<>(2),
                        Executors.defaultThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy()
                );

        for (int i = 1; i <= 10; i++) {

            int taskId = i;

            executor.submit(() -> {

                System.out.println(
                        Thread.currentThread().getName()
                        + " processing task "
                        + taskId
                );

                try {

                    Thread.sleep(5000);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
    }
}
```

---

# 13. Important Pool Configuration

Pool:

```text
corePoolSize = 2
maxPoolSize = 4
queue size = 2
```

---

# 14. ThreadPoolExecutor Task Flow

When task arrives:

## Step 1

If:

```text
active threads < corePoolSize
```

Create new worker immediately.

---

## Step 2

Else:

```text
put task into queue
```

---

## Step 3

If queue full AND:

```text
active threads < maxPoolSize
```

Create extra worker.

---

## Step 4

If queue full AND:

```text
active threads == maxPoolSize
```

Reject task.

---

# 15. Dry Run — First Tasks

Pool:

```text
core = 2
max = 4
queue = 2
```

Task-1:

```text
workers < core
create Worker-1
```

Task-2:

```text
workers < core
create Worker-2
```

Now:

```text
2 workers active
```

---

# 16. Dry Run — Queue Filling

Task-3:

```text
core workers busy
queue has space
enqueue Task-3
```

Task-4:

```text
queue has space
enqueue Task-4
```

Queue now full.

---

# 17. Dry Run — Extra Threads

Task-5:

```text
queue full
workers < max
create Worker-3
```

Task-6:

```text
queue full
workers < max
create Worker-4
```

Now:

```text
4 workers active
queue full
```

---

# 18. Dry Run — Rejection

Task-7:

```text
queue full
workers == max
```

Cannot accept task.

Rejection handler executes.

---

# 19. Important Insight

Pool growth order:

```text
core threads first
then queue
then extra threads
then rejection
```

Very important interview concept.

---

# 20. Why Queue Before Max Threads?

Java design assumes:

```text
queueing cheaper than creating many threads
```

Too many threads cause:

```text
context switching
memory pressure
scheduler overhead
```

---

# 21. Worker Thread Lifecycle

Worker thread:

```text
created
takes tasks
executes tasks
waits for more tasks
dies if idle beyond keepAliveTime
```

---

# 22. Core Threads vs Extra Threads

Core threads:

```text
stay alive normally
```

Extra threads:

```text
temporary burst workers
```

Die after idle timeout.

---

# 23. allowCoreThreadTimeOut()

Normally:

```text
core threads never die
```

But:

```java
executor.allowCoreThreadTimeOut(true);
```

allows even core workers to expire.

Useful for low-memory systems.

---

# 24. Queue Types

Most important production topic.

Different queues change pool behavior heavily.

---

# 25. ArrayBlockingQueue

```java
new ArrayBlockingQueue<>(100)
```

Characteristics:

```text
bounded
fixed size
strong backpressure
predictable memory
```

Good production choice.

---

# 26. LinkedBlockingQueue

```java
new LinkedBlockingQueue<>()
```

Danger:

```text
unbounded by default
```

Can grow forever.

Possible:

```text
memory explosion
high latency
```

---

# 27. SynchronousQueue

```java
new SynchronousQueue<>()
```

Meaning:

```text
no queue storage
direct handoff to worker
```

Task must immediately find worker.

Used by:

```text
CachedThreadPool
```

---

# 28. CachedThreadPool Internals

Internally similar to:

```java
corePoolSize = 0
maxPoolSize = Integer.MAX_VALUE
SynchronousQueue
```

Meaning:

```text
create threads aggressively
```

Dangerous under high load.

---

# 29. PriorityBlockingQueue

Allows:

```text
priority-based task execution
```

Danger:

```text
unbounded by default
```

Need caution.

---

# 30. Rejection Policies

Built-in policies:

```text
AbortPolicy
CallerRunsPolicy
DiscardPolicy
DiscardOldestPolicy
```

---

# 31. AbortPolicy

Default.

Behavior:

```text
throw RejectedExecutionException
```

Good when overload should fail fast.

---

# 32. CallerRunsPolicy

Behavior:

```text
calling thread executes task
```

Meaning:

```text
producer slows down naturally
```

Provides implicit backpressure.

---

# 33. CallerRunsPolicy Mental Model

Instead of:

```text
reject task
```

caller must do work itself.

This slows producer.

Very clever overload control.

---

# 34. DiscardPolicy

Behavior:

```text
silently drop task
```

Dangerous.

Only safe for:

```text
non-critical telemetry/logging
```

---

# 35. DiscardOldestPolicy

Behavior:

```text
remove oldest queued task
accept new task
```

Useful when:

```text
latest data more important
```

Example:

```text
live monitoring updates
```

---

# 36. Rejection Policy Production Choice

Critical systems:

```text
AbortPolicy
CallerRunsPolicy
```

Non-critical systems:

```text
DiscardPolicy sometimes acceptable
```

---

# 37. ThreadFactory

Controls thread creation.

Can customize:

```text
thread names
priority
daemon mode
uncaught exception handling
```

---

# 38. Custom ThreadFactory Example

```java
ThreadFactory factory = runnable -> {

    Thread thread = new Thread(runnable);

    thread.setName("payment-worker");

    return thread;
};
```

Useful for:

```text
monitoring
debugging
thread dumps
```

---

# 39. Pool States

ThreadPoolExecutor states:

```text
RUNNING
SHUTDOWN
STOP
TIDYING
TERMINATED
```

---

# 40. shutdown()

```java
executor.shutdown();
```

Meaning:

```text
stop accepting new tasks
finish existing tasks
```

Graceful shutdown.

---

# 41. shutdownNow()

```java
executor.shutdownNow();
```

Meaning:

```text
interrupt workers immediately
attempt force stop
```

Tasks may remain incomplete.

---

# 42. awaitTermination()

```java
executor.awaitTermination(
    60,
    TimeUnit.SECONDS
);
```

Wait for pool shutdown completion.

---

# 43. Thread Pool Metrics

Important production metrics:

```text
active thread count
pool size
queue size
task wait time
task execution time
rejected tasks
completed task count
```

---

# 44. Queue Wait Time Problem

Large queue may hide overload.

Example:

```text
queue size = 100000
```

Task may wait:

```text
minutes before execution
```

System "alive" but unusable.

---

# 45. CPU-Bound Pool Sizing

CPU-heavy tasks:

```text
encryption
compression
image processing
math
```

Rule:

```text
threads ≈ CPU cores
```

---

# 46. IO-Bound Pool Sizing

IO tasks:

```text
DB
HTTP
Kafka
network
disk
```

Threads spend time waiting.

Can use:

```text
more threads than CPU cores
```

---

# 47. Thread Starvation Problem

Small pool:

```text
all workers blocked
```

New tasks wait forever.

Example:

```text
pool size = 10
all 10 waiting on slow API
```

---

# 48. Backend Example — Tomcat

Tomcat uses worker thread pools.

Flow:

```text
HTTP request
    ↓
Tomcat worker thread
    ↓
Controller
    ↓
Service
    ↓
Response
```

No new thread per request.

---

# 49. Backend Example — Kafka Consumers

Kafka consumers often use pools:

```text
poll messages
submit processing tasks
worker pool processes messages
```

Improves throughput.

---

# 50. Backend Example — Notification Service

```text
Email task submitted
    ↓
Task queue
    ↓
Worker pool sends emails
```

Typical async architecture.

---

# 51. Production Design Rules

```text
Prefer bounded queues.
Avoid unlimited thread growth.
Monitor queue size.
Tune for workload type.
Use timeouts.
Separate CPU and IO pools.
Choose rejection policy carefully.
```

---

# 52. Common Production Mistakes

## Mistake 1

```text
Using unbounded LinkedBlockingQueue.
```

Memory danger.

---

## Mistake 2

```text
Too many threads.
```

Context switching explosion.

---

## Mistake 3

```text
Very small pool for IO-heavy tasks.
```

Starvation.

---

## Mistake 4

```text
Ignoring rejected tasks.
```

Critical failures hidden.

---

## Mistake 5

```text
Blocking inside worker pool endlessly.
```

Workers exhausted.

---

# 53. Internal Mental Model

```text
Tasks arrive
    ↓
Core workers created
    ↓
Queue buffers tasks
    ↓
Extra workers created under pressure
    ↓
Rejection when overloaded
```

---

# 54. Interview Explanation

If interviewer asks:

```text
How does ThreadPoolExecutor work internally?
```

Good answer:

```text
ThreadPoolExecutor first creates core worker threads.
If core workers are busy, tasks are queued.
If the queue becomes full and the current thread count is below maximumPoolSize,
extra worker threads are created.
If both queue and max threads are exhausted, tasks are rejected using the configured rejection policy.
```

Strong backend addition:

```text
The choice of queue type and rejection policy strongly affects system behavior,
backpressure, memory usage, and overload handling in production systems.
```

---

# 55. Mini Dry Run Summary

Pool:

```text
core = 2
max = 4
queue = 2
```

Tasks:

```text
Task-1 → Worker-1
Task-2 → Worker-2
Task-3 → Queue
Task-4 → Queue
Task-5 → Worker-3
Task-6 → Worker-4
Task-7 → Rejected
```

---

# 56. Visual Summary

```text
Incoming Tasks
        ↓
+-------------------+
| ThreadPoolExecutor|
+-------------------+
        ↓
Core Workers
        ↓
Task Queue
        ↓
Extra Workers
        ↓
Rejection Policy
```

---

# 57. What To Remember

```text
ThreadPoolExecutor is the real production thread pool engine.

Flow:
core threads → queue → extra threads → rejection.

Bounded queues are critical.

Queue choice changes behavior heavily.

CallerRunsPolicy provides natural backpressure.

Thread pool tuning depends on CPU-bound vs IO-bound workload.
```

---

# 58. Next File

```text
020_CompletableFuture_Basics.md
```

Next you learn:

```text
asynchronous pipelines
non-blocking composition
thenApply
thenCompose
parallel async execution
backend async APIs
```
