# 001 MiniThreadPool — Java 21 From Scratch

> Goal: Build a production-style mini thread pool incrementally to understand Java `ExecutorService`, workers, queues, futures, shutdown, rejection policies, metrics, and scheduled execution internals.

---

## Clickable Index

1. [Why Build MiniThreadPool?](#1-why-build-minithreadpool)
2. [Real-World Use Cases](#2-real-world-use-cases)
3. [Core Mental Model](#3-core-mental-model)
4. [Final Feature Set](#4-final-feature-set)
5. [Final Project Structure](#5-final-project-structure)
6. [Maven Setup](#6-maven-setup)
7. [Phase 1 — Fixed Thread Pool With Runnable](#7-phase-1--fixed-thread-pool-with-runnable)
8. [Phase 2 — Bounded Queue + Backpressure](#8-phase-2--bounded-queue--backpressure)
9. [Phase 3 — Graceful Shutdown](#9-phase-3--graceful-shutdown)
10. [Phase 4 — Rejection Policies](#10-phase-4--rejection-policies)
11. [Phase 5 — Callable + MiniFuture](#11-phase-5--callable--minifuture)
12. [Phase 6 — Metrics](#12-phase-6--metrics)
13. [Phase 7 — Scheduled Tasks](#13-phase-7--scheduled-tasks)
14. [Phase 8 — Final Integrated Working Solution](#14-phase-8--final-integrated-working-solution)
15. [Driver Classes](#15-driver-classes)
16. [JUnit Tests](#16-junit-tests)
17. [Load / Overload Demo](#17-load--overload-demo)
18. [Interview Explanation](#18-interview-explanation)
19. [What To Add Later](#19-what-to-add-later)

---

# 1. Why Build MiniThreadPool?

Without a thread pool, a server may create one new thread for every task:

```java
new Thread(task).start();
```

This is dangerous because:

```text
10,000 requests => 10,000 threads
Too much memory
Too much context switching
High latency
Possible server crash
```

A thread pool solves this by:

```text
1. Creating fixed worker threads once.
2. Reusing those workers.
3. Putting incoming tasks into a queue.
4. Applying backpressure when overloaded.
```

---

# 2. Real-World Use Cases

| System | Thread pool usage |
|---|---|
| Web server | Handle requests using worker threads |
| Kafka consumer | Process messages in parallel |
| API Gateway | Proxy upstream requests |
| Redis-like system | Background expiry, persistence, replication |
| Log aggregator | Parse and ship logs in parallel |
| Scheduler | Run delayed / periodic tasks |
| Notification system | Send email/SMS/push asynchronously |
| File service | Process uploads, scans, compression |

---

# 3. Core Mental Model

```text
Client submits task
        |
        v
+----------------+
|  Task Queue    |
+----------------+
   |     |     |
   v     v     v
Worker Worker Worker
   |     |     |
   v     v     v
execute execute execute
```

Thread pool has three core components:

```text
1. Task Queue
2. Worker Threads
3. Pool State
```

Worker loop:

```text
while pool is running OR queue still has tasks:
    take one task
    execute task
```

---

# 4. Final Feature Set

By the end, your MiniThreadPool supports:

```text
1. Fixed-size worker threads
2. Bounded blocking queue
3. submit(Runnable)
4. submit(Callable<T>) returning MiniFuture<T>
5. Graceful shutdown
6. Force shutdown
7. Rejection policies
8. Metrics
9. Scheduled delayed task execution
10. Driver/demo classes
11. JUnit tests
```

---

# 5. Final Project Structure

```text
mini-thread-pool/
├── pom.xml
├── README.md
├── src/main/java/com/mini/threadpool/
│   ├── MiniThreadPool.java
│   ├── MiniFuture.java
│   ├── RejectionPolicy.java
│   ├── AbortPolicy.java
│   ├── CallerRunsPolicy.java
│   ├── DiscardPolicy.java
│   ├── BlockPolicy.java
│   ├── ThreadPoolMetrics.java
│   ├── MiniScheduledThreadPool.java
│   ├── DemoPhase1.java
│   ├── DemoFuture.java
│   ├── DemoRejection.java
│   ├── DemoScheduled.java
│   └── DemoOverload.java
└── src/test/java/com/mini/threadpool/
    ├── MiniThreadPoolTest.java
    ├── MiniFutureTest.java
    ├── ShutdownTest.java
    └── RejectionPolicyTest.java
```

---

# 6. Maven Setup

Create `pom.xml`:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.mini</groupId>
    <artifactId>mini-thread-pool</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
            </plugin>
        </plugins>
    </build>
</project>
```

---

# 7. Phase 1 — Fixed Thread Pool With Runnable

## 7.1 Goal

Build the first working version:

```text
- N fixed worker threads
- submit Runnable task
- workers execute tasks
```

## 7.2 Theory

A task is submitted into a queue. Workers continuously consume from the queue.

```text
submit(task)
    |
    v
queue.offer(task)
    |
    v
worker.take()
    |
    v
task.run()
```

## 7.3 Phase 1 Code

Create `MiniThreadPool.java`:

```java
package com.mini.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MiniThreadPool {
    private final BlockingQueue<Runnable> taskQueue;
    private final List<Thread> workers;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public MiniThreadPool(int numberOfThreads) {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.workers = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            Thread worker = new Thread(this::workerLoop, "mini-worker-" + i);
            workers.add(worker);
            worker.start();
        }
    }

    public void submit(Runnable task) {
        if (!running.get()) {
            throw new IllegalStateException("Thread pool is shutting down");
        }
        taskQueue.offer(task);
    }

    private void workerLoop() {
        while (running.get() || !taskQueue.isEmpty()) {
            try {
                Runnable task = taskQueue.poll(100, TimeUnit.MILLISECONDS);
                if (task != null) {
                    task.run();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void shutdown() {
        running.set(false);
    }
}
```

## 7.4 Driver

Create `DemoPhase1.java`:

```java
package com.mini.threadpool;

public class DemoPhase1 {
    public static void main(String[] args) throws InterruptedException {
        MiniThreadPool pool = new MiniThreadPool(3);

        for (int i = 1; i <= 10; i++) {
            int taskId = i;
            pool.submit(() -> {
                System.out.println(Thread.currentThread().getName() + " executing task " + taskId);
                sleep(500);
            });
        }

        Thread.sleep(3000);
        pool.shutdown();
        System.out.println("Shutdown requested");
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

## 7.5 Expected Output

```text
mini-worker-0 executing task 1
mini-worker-1 executing task 2
mini-worker-2 executing task 3
mini-worker-0 executing task 4
...
Shutdown requested
```

---

# 8. Phase 2 — Bounded Queue + Backpressure

## 8.1 Why Bounded Queue?

Unbounded queue can cause memory explosion.

```text
Producer submits 1,000,000 tasks
Workers process slowly
Queue grows forever
Heap memory increases
Application may crash
```

Bounded queue controls memory:

```java
new ArrayBlockingQueue<>(queueCapacity);
```

## 8.2 Update Constructor

Change queue from `LinkedBlockingQueue` to `ArrayBlockingQueue`.

```java
public MiniThreadPool(int numberOfThreads, int queueCapacity) {
    this.taskQueue = new ArrayBlockingQueue<>(queueCapacity);
    this.workers = new ArrayList<>();

    for (int i = 0; i < numberOfThreads; i++) {
        Thread worker = new Thread(this::workerLoop, "mini-worker-" + i);
        workers.add(worker);
        worker.start();
    }
}
```

Add import:

```java
import java.util.concurrent.ArrayBlockingQueue;
```

## 8.3 Backpressure Meaning

Backpressure means:

```text
System says: I cannot accept unlimited work.
Either slow down producer, reject task, or make caller execute task.
```

---

# 9. Phase 3 — Graceful Shutdown

## 9.1 Requirement

When `shutdown()` is called:

```text
1. Reject new tasks.
2. Continue processing already queued tasks.
3. Exit workers when queue becomes empty.
```

## 9.2 Add Await Termination

```java
public void awaitTermination() throws InterruptedException {
    for (Thread worker : workers) {
        worker.join();
    }
}
```

Usage:

```java
pool.shutdown();
pool.awaitTermination();
```

## 9.3 Force Shutdown

```java
public void shutdownNow() {
    running.set(false);
    taskQueue.clear();
    for (Thread worker : workers) {
        worker.interrupt();
    }
}
```

Difference:

| Method | Behavior |
|---|---|
| `shutdown()` | Finish queued tasks |
| `shutdownNow()` | Interrupt workers and clear queue |

---

# 10. Phase 4 — Rejection Policies

## 10.1 Problem

What happens if queue is full?

```text
2 workers
queue capacity 5
100 tasks submitted quickly
```

Need rejection policy.

## 10.2 RejectionPolicy Interface

Create `RejectionPolicy.java`:

```java
package com.mini.threadpool;

public interface RejectionPolicy {
    void reject(Runnable task, MiniThreadPool pool);
}
```

## 10.3 AbortPolicy

Create `AbortPolicy.java`:

```java
package com.mini.threadpool;

public class AbortPolicy implements RejectionPolicy {
    @Override
    public void reject(Runnable task, MiniThreadPool pool) {
        throw new IllegalStateException("Task rejected: queue is full");
    }
}
```

## 10.4 CallerRunsPolicy

Create `CallerRunsPolicy.java`:

```java
package com.mini.threadpool;

public class CallerRunsPolicy implements RejectionPolicy {
    @Override
    public void reject(Runnable task, MiniThreadPool pool) {
        task.run();
    }
}
```

## 10.5 DiscardPolicy

Create `DiscardPolicy.java`:

```java
package com.mini.threadpool;

public class DiscardPolicy implements RejectionPolicy {
    @Override
    public void reject(Runnable task, MiniThreadPool pool) {
        // intentionally discard
    }
}
```

## 10.6 BlockPolicy

Create `BlockPolicy.java`:

```java
package com.mini.threadpool;

public class BlockPolicy implements RejectionPolicy {
    @Override
    public void reject(Runnable task, MiniThreadPool pool) {
        try {
            pool.putTask(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while blocking for queue space", e);
        }
    }
}
```

## 10.7 Updated Submit Logic

```java
public void submit(Runnable task) {
    if (!running.get()) {
        throw new IllegalStateException("Thread pool is shutting down");
    }

    boolean accepted = taskQueue.offer(task);
    if (!accepted) {
        rejectedTaskCount.incrementAndGet();
        rejectionPolicy.reject(task, this);
    }
}
```

Support method for `BlockPolicy`:

```java
void putTask(Runnable task) throws InterruptedException {
    taskQueue.put(task);
}
```

---

# 11. Phase 5 — Callable + MiniFuture

## 11.1 Theory

`Runnable` does not return a result.

```java
pool.submit(() -> System.out.println("done"));
```

`Callable<T>` returns result:

```java
MiniFuture<Integer> future = pool.submit(() -> 10 + 20);
Integer result = future.get();
```

Flow:

```text
Callable<T>
   |
   v
wrap inside Runnable
   |
   v
worker executes Callable
   |
   v
store result in MiniFuture
   |
   v
future.get() returns result
```

## 11.2 MiniFuture Code

Create `MiniFuture.java`:

```java
package com.mini.threadpool;

public class MiniFuture<T> {
    private T result;
    private Throwable error;
    private boolean done;

    public synchronized T get() throws Exception {
        while (!done) {
            wait();
        }

        if (error != null) {
            throw new Exception(error);
        }

        return result;
    }

    public synchronized boolean isDone() {
        return done;
    }

    synchronized void complete(T result) {
        if (done) return;
        this.result = result;
        this.done = true;
        notifyAll();
    }

    synchronized void completeExceptionally(Throwable error) {
        if (done) return;
        this.error = error;
        this.done = true;
        notifyAll();
    }
}
```

## 11.3 Add Callable Submit

Inside `MiniThreadPool.java`:

```java
import java.util.concurrent.Callable;
```

Add method:

```java
public <T> MiniFuture<T> submit(Callable<T> callable) {
    MiniFuture<T> future = new MiniFuture<>();

    submit(() -> {
        try {
            T result = callable.call();
            future.complete(result);
        } catch (Throwable e) {
            future.completeExceptionally(e);
        }
    });

    return future;
}
```

## 11.4 Future Driver

Create `DemoFuture.java`:

```java
package com.mini.threadpool;

public class DemoFuture {
    public static void main(String[] args) throws Exception {
        MiniThreadPool pool = new MiniThreadPool(2, 10, new AbortPolicy());

        MiniFuture<Integer> future = pool.submit(() -> {
            Thread.sleep(500);
            return 100 + 200;
        });

        System.out.println("Doing other work...");
        System.out.println("Result = " + future.get());

        pool.shutdown();
        pool.awaitTermination();
    }
}
```

---

# 12. Phase 6 — Metrics

## 12.1 Why Metrics?

In production, you need to know:

```text
How many tasks submitted?
How many completed?
How many failed?
How many rejected?
How large is the queue?
How many workers are active?
```

## 12.2 ThreadPoolMetrics Code

Create `ThreadPoolMetrics.java`:

```java
package com.mini.threadpool;

public record ThreadPoolMetrics(
        long submittedTasks,
        long completedTasks,
        long failedTasks,
        long rejectedTasks,
        int queueSize,
        int activeWorkers
) {}
```

Use counters inside `MiniThreadPool`:

```java
private final AtomicLong submittedTaskCount = new AtomicLong();
private final AtomicLong completedTaskCount = new AtomicLong();
private final AtomicLong failedTaskCount = new AtomicLong();
private final AtomicLong rejectedTaskCount = new AtomicLong();
private final AtomicInteger activeWorkerCount = new AtomicInteger();
```

In `submit()`:

```java
submittedTaskCount.incrementAndGet();
```

In worker execution:

```java
activeWorkerCount.incrementAndGet();
try {
    task.run();
    completedTaskCount.incrementAndGet();
} catch (Throwable e) {
    failedTaskCount.incrementAndGet();
} finally {
    activeWorkerCount.decrementAndGet();
}
```

Metrics method:

```java
public ThreadPoolMetrics getMetrics() {
    return new ThreadPoolMetrics(
            submittedTaskCount.get(),
            completedTaskCount.get(),
            failedTaskCount.get(),
            rejectedTaskCount.get(),
            taskQueue.size(),
            activeWorkerCount.get()
    );
}
```

---

# 13. Phase 7 — Scheduled Tasks

## 13.1 Goal

Support:

```java
scheduler.schedule(task, 1000);
```

Meaning:

```text
Run task after 1000 milliseconds.
```

## 13.2 Theory

Use a priority queue ordered by execution time.

```text
Delayed Task A => runAt = now + 5000
Delayed Task B => runAt = now + 1000
Delayed Task C => runAt = now + 3000
```

Queue order:

```text
B, C, A
```

## 13.3 MiniScheduledThreadPool Code

Create `MiniScheduledThreadPool.java`:

```java
package com.mini.threadpool;

import java.util.PriorityQueue;

public class MiniScheduledThreadPool {
    private final MiniThreadPool workerPool;
    private final PriorityQueue<ScheduledTask> scheduledTasks = new PriorityQueue<>();
    private final Thread schedulerThread;
    private volatile boolean running = true;

    public MiniScheduledThreadPool(int workers, int queueCapacity) {
        this.workerPool = new MiniThreadPool(workers, queueCapacity, new AbortPolicy());
        this.schedulerThread = new Thread(this::schedulerLoop, "mini-scheduler");
        this.schedulerThread.start();
    }

    public void schedule(Runnable task, long delayMillis) {
        long runAt = System.currentTimeMillis() + delayMillis;
        synchronized (scheduledTasks) {
            scheduledTasks.offer(new ScheduledTask(task, runAt));
            scheduledTasks.notifyAll();
        }
    }

    private void schedulerLoop() {
        while (running) {
            try {
                Runnable taskToRun = null;

                synchronized (scheduledTasks) {
                    while (scheduledTasks.isEmpty() && running) {
                        scheduledTasks.wait();
                    }

                    if (!running) break;

                    ScheduledTask next = scheduledTasks.peek();
                    long now = System.currentTimeMillis();
                    long waitTime = next.runAtMillis - now;

                    if (waitTime > 0) {
                        scheduledTasks.wait(waitTime);
                    } else {
                        taskToRun = scheduledTasks.poll().task;
                    }
                }

                if (taskToRun != null) {
                    workerPool.submit(taskToRun);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void shutdown() {
        running = false;
        synchronized (scheduledTasks) {
            scheduledTasks.notifyAll();
        }
        schedulerThread.interrupt();
        workerPool.shutdown();
    }

    private record ScheduledTask(Runnable task, long runAtMillis) implements Comparable<ScheduledTask> {
        @Override
        public int compareTo(ScheduledTask other) {
            return Long.compare(this.runAtMillis, other.runAtMillis);
        }
    }
}
```

---

# 14. Phase 8 — Final Integrated Working Solution

Replace `MiniThreadPool.java` with this final version.

```java
package com.mini.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MiniThreadPool {
    private final BlockingQueue<Runnable> taskQueue;
    private final List<Thread> workers;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final RejectionPolicy rejectionPolicy;

    private final AtomicLong submittedTaskCount = new AtomicLong();
    private final AtomicLong completedTaskCount = new AtomicLong();
    private final AtomicLong failedTaskCount = new AtomicLong();
    private final AtomicLong rejectedTaskCount = new AtomicLong();
    private final AtomicInteger activeWorkerCount = new AtomicInteger();

    public MiniThreadPool(int numberOfThreads) {
        this(numberOfThreads, 1000, new AbortPolicy());
    }

    public MiniThreadPool(int numberOfThreads, int queueCapacity, RejectionPolicy rejectionPolicy) {
        if (numberOfThreads <= 0) {
            throw new IllegalArgumentException("numberOfThreads must be > 0");
        }
        if (queueCapacity <= 0) {
            throw new IllegalArgumentException("queueCapacity must be > 0");
        }

        this.taskQueue = new ArrayBlockingQueue<>(queueCapacity);
        this.workers = new ArrayList<>();
        this.rejectionPolicy = rejectionPolicy;

        for (int i = 0; i < numberOfThreads; i++) {
            Thread worker = new Thread(this::workerLoop, "mini-worker-" + i);
            workers.add(worker);
            worker.start();
        }
    }

    public void submit(Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException("task cannot be null");
        }
        if (!running.get()) {
            throw new IllegalStateException("Thread pool is shutting down");
        }

        submittedTaskCount.incrementAndGet();
        boolean accepted = taskQueue.offer(task);

        if (!accepted) {
            rejectedTaskCount.incrementAndGet();
            rejectionPolicy.reject(task, this);
        }
    }

    public <T> MiniFuture<T> submit(Callable<T> callable) {
        if (callable == null) {
            throw new IllegalArgumentException("callable cannot be null");
        }

        MiniFuture<T> future = new MiniFuture<>();

        submit(() -> {
            try {
                T result = callable.call();
                future.complete(result);
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    void putTask(Runnable task) throws InterruptedException {
        taskQueue.put(task);
    }

    private void workerLoop() {
        while (running.get() || !taskQueue.isEmpty()) {
            try {
                Runnable task = taskQueue.poll(100, TimeUnit.MILLISECONDS);
                if (task != null) {
                    executeTask(task);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void executeTask(Runnable task) {
        activeWorkerCount.incrementAndGet();
        try {
            task.run();
            completedTaskCount.incrementAndGet();
        } catch (Throwable e) {
            failedTaskCount.incrementAndGet();
            System.err.println("Task failed: " + e.getMessage());
        } finally {
            activeWorkerCount.decrementAndGet();
        }
    }

    public void shutdown() {
        running.set(false);
    }

    public void shutdownNow() {
        running.set(false);
        taskQueue.clear();
        for (Thread worker : workers) {
            worker.interrupt();
        }
    }

    public void awaitTermination() throws InterruptedException {
        for (Thread worker : workers) {
            worker.join();
        }
    }

    public ThreadPoolMetrics getMetrics() {
        return new ThreadPoolMetrics(
                submittedTaskCount.get(),
                completedTaskCount.get(),
                failedTaskCount.get(),
                rejectedTaskCount.get(),
                taskQueue.size(),
                activeWorkerCount.get()
        );
    }
}
```

---

# 15. Driver Classes

## 15.1 DemoRejection.java

```java
package com.mini.threadpool;

public class DemoRejection {
    public static void main(String[] args) throws InterruptedException {
        MiniThreadPool pool = new MiniThreadPool(1, 2, new CallerRunsPolicy());

        for (int i = 1; i <= 10; i++) {
            int taskId = i;
            pool.submit(() -> {
                System.out.println(Thread.currentThread().getName() + " running task " + taskId);
                sleep(300);
            });
        }

        pool.shutdown();
        pool.awaitTermination();
        System.out.println(pool.getMetrics());
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

## 15.2 DemoScheduled.java

```java
package com.mini.threadpool;

public class DemoScheduled {
    public static void main(String[] args) throws InterruptedException {
        MiniScheduledThreadPool scheduler = new MiniScheduledThreadPool(2, 10);

        scheduler.schedule(() -> System.out.println("Runs after 1 second"), 1000);
        scheduler.schedule(() -> System.out.println("Runs after 2 seconds"), 2000);
        scheduler.schedule(() -> System.out.println("Runs after 500 ms"), 500);

        Thread.sleep(3000);
        scheduler.shutdown();
    }
}
```

## 15.3 DemoOverload.java

```java
package com.mini.threadpool;

public class DemoOverload {
    public static void main(String[] args) throws InterruptedException {
        MiniThreadPool pool = new MiniThreadPool(2, 5, new DiscardPolicy());

        for (int i = 1; i <= 100; i++) {
            int taskId = i;
            pool.submit(() -> {
                sleep(1000);
                System.out.println("Completed task " + taskId);
            });
        }

        Thread.sleep(3000);
        pool.shutdown();
        pool.awaitTermination();

        System.out.println("Final metrics: " + pool.getMetrics());
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

---

# 16. JUnit Tests

## 16.1 MiniThreadPoolTest.java

```java
package com.mini.threadpool;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MiniThreadPoolTest {

    @Test
    void shouldExecuteSubmittedTasks() throws InterruptedException {
        MiniThreadPool pool = new MiniThreadPool(3, 10, new AbortPolicy());
        AtomicInteger counter = new AtomicInteger();

        for (int i = 0; i < 5; i++) {
            pool.submit(counter::incrementAndGet);
        }

        pool.shutdown();
        pool.awaitTermination();

        assertEquals(5, counter.get());
        assertEquals(5, pool.getMetrics().completedTasks());
    }
}
```

## 16.2 MiniFutureTest.java

```java
package com.mini.threadpool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MiniFutureTest {

    @Test
    void shouldReturnCallableResult() throws Exception {
        MiniThreadPool pool = new MiniThreadPool(2, 10, new AbortPolicy());

        MiniFuture<Integer> future = pool.submit(() -> 40 + 2);

        assertEquals(42, future.get());

        pool.shutdown();
        pool.awaitTermination();
    }
}
```

## 16.3 ShutdownTest.java

```java
package com.mini.threadpool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ShutdownTest {

    @Test
    void shouldRejectTasksAfterShutdown() {
        MiniThreadPool pool = new MiniThreadPool(1, 10, new AbortPolicy());
        pool.shutdown();

        assertThrows(IllegalStateException.class, () -> pool.submit(() -> {}));
    }
}
```

## 16.4 RejectionPolicyTest.java

```java
package com.mini.threadpool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RejectionPolicyTest {

    @Test
    void abortPolicyShouldThrowWhenQueueFull() {
        MiniThreadPool pool = new MiniThreadPool(1, 1, new AbortPolicy());

        pool.submit(() -> sleep(500));
        pool.submit(() -> sleep(500));

        assertThrows(IllegalStateException.class, () -> pool.submit(() -> {}));

        pool.shutdownNow();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

---

# 17. Load / Overload Demo

## 17.1 Scenario

```text
Workers = 2
Queue capacity = 5
Tasks = 100
Each task sleeps 1 second
```

Expected behavior:

```text
Some tasks execute.
Some tasks are rejected/discarded/caller-runs depending on policy.
Metrics show overload clearly.
```

## 17.2 What To Observe

| Metric | Meaning |
|---|---|
| submittedTasks | how many tasks user tried to submit |
| completedTasks | how many actually completed |
| rejectedTasks | how much overload happened |
| queueSize | current backlog |
| activeWorkers | workers currently executing |

---

# 18. Interview Explanation

If interviewer asks:

> How does a thread pool work internally?

Answer:

```text
A thread pool pre-creates a fixed number of worker threads.
Incoming tasks are submitted into a blocking queue.
Each worker continuously takes one task from the queue and executes it.
This avoids creating one thread per request and gives control over concurrency.
A bounded queue prevents memory explosion.
When the queue is full, rejection policies provide backpressure.
Graceful shutdown stops accepting new tasks but lets queued tasks finish.
Callable and Future allow async result handling.
```

If interviewer asks:

> Why bounded queue?

Answer:

```text
An unbounded queue hides overload and can cause heap memory explosion.
A bounded queue makes overload visible and forces a backpressure decision:
reject, block, discard, or caller-runs.
```

If interviewer asks:

> What is CallerRunsPolicy?

Answer:

```text
If queue is full, the submitting thread executes the task itself.
This slows down the producer naturally, creating backpressure.
```

---

# 19. What To Add Later

Advanced extensions:

```text
1. Core pool size vs max pool size
2. Keep-alive timeout for extra workers
3. Priority task queue
4. Task cancellation
5. Future timeout get()
6. Periodic scheduled tasks
7. Virtual thread mode
8. Prometheus metrics endpoint
9. JMH benchmark
10. Work-stealing pool
```

This prepares you for:

```text
MiniRedis background expiry
MiniKafka network/request workers
MiniMQ consumer workers
MiniGateway proxy workers
MiniELK ingestion workers
MiniScheduler delayed jobs
MiniRaft replication workers
```

