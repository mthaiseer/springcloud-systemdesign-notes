# 018_ThreadPool_Basics.md

# MiniConcurrency — 018 ThreadPool Basics

## 0. Why This File Exists

Creating a new thread for every task is expensive.

Example:

```text
10000 HTTP requests
→ 10000 new threads
```

Problems:

```text
high memory usage
slow thread creation
context switching overhead
CPU pressure
system instability
```

Modern backend systems solve this using:

```text
Thread Pools
```

Thread pools are one of the most important backend concurrency concepts.

Used everywhere:

```text
Spring Boot
Tomcat
Kafka consumers
Executors
Schedulers
Async workers
Database workers
Web servers
```

This file teaches:

```text
What thread pool is
Why thread pools exist
Task queue
Worker threads
Thread reuse
Fixed thread pool
Cached thread pool
submit vs execute
Future basics
Thread pool sizing
Backend production mapping
```

---

# 1. One-Line Definition

```text
A thread pool is a group of reusable worker threads that execute tasks from a queue.
```

Simple meaning:

```text
Instead of creating new thread every time,
reuse existing worker threads.
```

---

# 2. Real Mental Model

Restaurant example:

Without thread pool:

```text
Hire new waiter for every customer.
```

Very expensive.

With thread pool:

```text
Keep fixed waiters ready.
Assign customers to available waiter.
```

Efficient.

---

# 3. Core Thread Pool Flow

```text
Task submitted
    ↓
Task queue
    ↓
Worker thread picks task
    ↓
Executes task
    ↓
Worker reused for next task
```

---

# 4. Why Thread Creation Is Expensive

Creating thread involves:

```text
OS thread creation
stack allocation
scheduler registration
context setup
```

Threads are not free.

---

# 5. Problem Without Thread Pool

Bad design:

```java
for each request:
    new Thread(...)
```

Problems:

```text
too many threads
memory explosion
context switching
slow performance
thread starvation
```

---

# 6. Thread Pool Benefits

```text
Thread reuse
Controlled concurrency
Lower memory usage
Better throughput
Task queueing
Backpressure
Thread lifecycle management
```

---

# 7. Basic Thread Pool Diagram

```text
Tasks
  ↓
Task Queue
  ↓
Worker-1
Worker-2
Worker-3
```

Workers continuously pull tasks.

---

# 8. Java Executor Framework

Java provides:

```java
ExecutorService
```

Most common way:

```java
Executors.newFixedThreadPool()
```

---

# 9. Fixed Thread Pool Example

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FixedThreadPoolDemo {

    public static void main(String[] args) {

        ExecutorService executor =
                Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 10; i++) {

            int taskId = i;

            executor.submit(() -> {

                System.out.println(
                        Thread.currentThread().getName()
                        + " processing task "
                        + taskId
                );

                try {

                    Thread.sleep(1000);

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

# 10. Dry Run — Fixed Thread Pool

Pool size:

```text
3 workers
```

Tasks:

```text
10 tasks submitted
```

Execution:

```text
Worker-1 takes Task-1
Worker-2 takes Task-2
Worker-3 takes Task-3
```

Remaining:

```text
Task-4 to Task-10 wait in queue
```

When Worker-1 finishes:

```text
Worker-1 takes Task-4
```

Workers reused.

No new thread creation.

---

# 11. Important Observation

Even though:

```text
10 tasks submitted
```

only:

```text
3 threads exist
```

Tasks wait in queue.

---

# 12. Thread Pool Internals

Internally:

```text
Task Queue
+ Worker Threads
+ Scheduler Logic
```

Flow:

```text
submit task
→ queue task
→ worker takes task
→ execute
→ worker returns to pool
```

---

# 13. Worker Thread Loop Mental Model

Internally workers behave like:

```java
while (true) {

    task = queue.take();

    task.run();
}
```

Workers continuously consume tasks.

Exactly producer-consumer pattern.

---

# 14. Producer-Consumer Mapping

```text
Caller thread = Producer
Task queue = Shared queue
Worker threads = Consumers
```

Thread pool is producer-consumer system internally.

---

# 15. execute() vs submit()

Two common methods:

```java
execute()
submit()
```

---

# 16. execute()

```java
executor.execute(task);
```

Characteristics:

```text
fire-and-forget
no return value
```

Used when:

```text
result not needed
```

---

# 17. submit()

```java
Future<?> future =
        executor.submit(task);
```

Characteristics:

```text
returns Future
can get result
can check completion
can cancel task
```

---

# 18. submit() Example

```java
import java.util.concurrent.*;

public class SubmitDemo {

    public static void main(String[] args)
            throws Exception {

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        Future<Integer> future =
                executor.submit(() -> {

                    Thread.sleep(1000);

                    return 42;
                });

        System.out.println(
                "Waiting for result..."
        );

        int result = future.get();

        System.out.println(result);

        executor.shutdown();
    }
}
```

Output:

```text
Waiting for result...
42
```

---

# 19. What Is Future?

Future represents:

```text
result of asynchronous computation
```

Meaning:

```text
task running now
result available later
```

---

# 20. Future Methods

Common methods:

```java
future.get()
future.isDone()
future.cancel()
```

---

# 21. future.get()

```java
future.get();
```

Meaning:

```text
wait until task completes
then return result
```

Danger:

```text
can block forever
```

Better:

```java
future.get(2, TimeUnit.SECONDS);
```

---

# 22. Thread Pool Queue

If all workers busy:

```text
new tasks go into queue
```

Queue smooths bursts.

Example:

```text
3 workers
100 tasks
97 tasks wait in queue
```

---

# 23. Queue Growth Problem

If tasks arrive faster than processing:

```text
queue grows forever
```

Problems:

```text
memory usage
high latency
timeouts
OutOfMemoryError
```

---

# 24. Why Bounded Queue Matters

Good production systems use:

```text
bounded queue
```

When full:

```text
reject tasks
slow producers
apply backpressure
```

Protects system.

---

# 25. Cached Thread Pool

Java provides:

```java
Executors.newCachedThreadPool()
```

Behavior:

```text
creates threads as needed
reuses idle threads
```

Danger:

```text
can create too many threads
```

---

# 26. Cached Thread Pool Problem

Example:

```text
100000 slow tasks
```

Cached pool may create:

```text
thousands of threads
```

Possible:

```text
memory pressure
CPU thrashing
context switching
```

Production caution required.

---

# 27. Single Thread Executor

```java
Executors.newSingleThreadExecutor()
```

Behavior:

```text
only one worker thread
tasks execute sequentially
```

Useful when:

```text
ordering required
single ownership needed
```

Similar mental model to Redis command execution.

---

# 28. Scheduled Thread Pool

```java
Executors.newScheduledThreadPool()
```

Used for:

```text
delayed tasks
periodic jobs
schedulers
heartbeat tasks
cleanup tasks
```

---

# 29. Scheduled Task Example

```java
import java.util.concurrent.*;

public class ScheduledDemo {

    public static void main(String[] args) {

        ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(
                () -> System.out.println("Heartbeat"),
                0,
                2,
                TimeUnit.SECONDS
        );
    }
}
```

---

# 30. Thread Pool Sizing

Very important production topic.

Too few threads:

```text
low throughput
queue growth
high latency
```

Too many threads:

```text
context switching
memory pressure
CPU thrashing
```

Need balance.

---

# 31. CPU-Bound Thread Count

CPU-bound tasks:

```text
math
compression
encryption
image processing
```

Rule of thumb:

```text
threads ≈ CPU cores
```

---

# 32. IO-Bound Thread Count

IO-bound tasks:

```text
DB calls
HTTP calls
file upload
Kafka/network operations
```

Threads often wait.

Can use:

```text
more threads than CPU cores
```

---

# 33. Example Thread Sizing

Machine:

```text
8 CPU cores
```

CPU-bound:

```text
8-10 threads
```

IO-bound:

```text
50-200 threads
```

depending on wait time.

---

# 34. Thread Pool Starvation

If all worker threads blocked:

```text
new tasks never execute
```

Example:

```text
all workers waiting on slow API
```

Queue grows forever.

Very common production issue.

---

# 35. Backend Example — API Worker Pool

```text
HTTP request arrives
    ↓
task submitted to pool
    ↓
worker executes business logic
```

Tomcat works similarly.

---

# 36. Backend Example — Kafka Consumers

Kafka consumers often use worker pools:

```text
consumer polls messages
    ↓
submit processing tasks to worker pool
```

Allows parallel processing.

---

# 37. Backend Example — Notification System

```text
email tasks
SMS tasks
push notification tasks
```

submitted to worker pool.

Workers process asynchronously.

---

# 38. Backend Example — Spring Boot

Spring Boot uses thread pools in:

```text
Tomcat request handling
@Async
Schedulers
WebFlux internals
Kafka listeners
TaskExecutor
```

---

# 39. Spring Boot Mental Model

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

Request handled by pool thread.

Not new thread per request.

---

# 40. shutdown()

Important:

```java
executor.shutdown();
```

Meaning:

```text
stop accepting new tasks
finish existing tasks
shutdown gracefully
```

---

# 41. shutdownNow()

```java
executor.shutdownNow();
```

Meaning:

```text
attempt immediate shutdown
interrupt workers
```

Dangerous if tasks not interruption-safe.

---

# 42. Graceful Shutdown Flow

```text
stop accepting new tasks
    ↓
finish queued/running tasks
    ↓
workers exit
```

Important in production deployment.

---

# 43. Common Production Problems

```text
Too many threads
Unbounded queue
Blocking tasks
Thread starvation
Slow downstream dependency
Memory pressure
Queue explosion
Deadlock
Long GC pauses
```

---

# 44. Monitoring Metrics

Important metrics:

```text
active thread count
queue size
task wait time
task execution time
rejected task count
pool utilization
CPU usage
```

---

# 45. Thread Pool Mental Model

```text
Tasks arrive
    ↓
Queue buffers tasks
    ↓
Workers execute tasks
    ↓
Workers reused
```

Core idea:

```text
reuse threads instead of creating new ones
```

---

# 46. Interview Explanation

If interviewer asks:

```text
Why use thread pool?
```

Good answer:

```text
Thread pools improve performance by reusing worker threads instead of
creating new threads for every task. They reduce thread creation cost,
control concurrency, and provide task queueing and backpressure.
```

Strong backend addition:

```text
Modern backend frameworks like Tomcat, Spring Boot, Kafka consumers,
and async worker systems heavily rely on thread pools internally.
```

---

# 47. Common Mistakes

## Mistake 1

```text
Creating new thread per request.
```

Does not scale.

---

## Mistake 2

```text
Using unbounded queues.
```

Can cause memory explosion.

---

## Mistake 3

```text
Too many worker threads.
```

Causes context switching overhead.

---

## Mistake 4

```text
Blocking tasks inside small pool.
```

Causes starvation.

---

## Mistake 5

```text
Forgetting shutdown().
```

Application may not terminate properly.

---

# 48. Mini Dry Run Summary

```text
10 tasks submitted
    ↓
3 worker threads exist
    ↓
3 tasks execute immediately
    ↓
remaining tasks wait in queue
    ↓
workers finish and reuse for next tasks
```

---

# 49. Visual Summary

```text
Producer Threads
        ↓
     Task Queue
        ↓
+----------------+
| Worker Threads |
+----------------+
        ↓
   Execute Tasks
```

---

# 50. What To Remember

```text
Thread pool = reusable worker threads.

Tasks wait in queue.

Workers continuously execute queued tasks.

Thread pools improve scalability and efficiency.

Bounded queues + proper sizing are critical in production.

Thread pools internally use producer-consumer pattern.
```

---

# 51. Next File

```text
019_ThreadPoolExecutor_Internals.md
```

Next you learn:

```text
corePoolSize
maximumPoolSize
keepAliveTime
work queues
rejection policies
CallerRunsPolicy
AbortPolicy
custom thread pools
real production tuning
```
