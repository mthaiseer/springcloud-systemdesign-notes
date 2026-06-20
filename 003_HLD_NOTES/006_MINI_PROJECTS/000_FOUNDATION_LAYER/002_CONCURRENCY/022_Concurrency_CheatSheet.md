# 022_Concurrency_CheatSheet.md

# MiniConcurrency — Detailed Last-Minute Revision CheatSheet

## 0. How To Use This CheatSheet

Use this file for:

```text
last-minute interview revision
production debugging mental model
Spring Boot/Tomcat/Kafka/Redis concurrency recall
quick comparison before system design interviews
```

Main goal:

```text
Remember concepts by mental model + flow, not by definition only.
```

---

# 1. Full MiniConcurrency Map

```text
001 What Is Concurrency
        ↓
002 Process vs Thread
        ↓
003 Thread Lifecycle
        ↓
004 Context Switching
        ↓
005 Race Condition
        ↓
006 Critical Section
        ↓
007 Mutex Lock
        ↓
008 ReentrantLock
        ↓
009 Read Write Lock
        ↓
010 Atomic Variables
        ↓
011 volatile
        ↓
012 Thread Communication
        ↓
013 Producer Consumer Pattern
        ↓
014 BlockingQueue
        ↓
015 Producer Consumer
        ↓
016 Deadlock
        ↓
017 Livelock Starvation
        ↓
018 ThreadPool Basics
        ↓
019 ThreadPoolExecutor Internals
        ↓
020 Future CompletableFuture
        ↓
021 Production Grade Concurrency
```

---

# 2. One Big Mental Model

```text
Backend Concurrency =
many requests
+ limited CPU cores
+ limited threads
+ shared memory
+ queues
+ locks
+ async tasks
+ downstream dependencies
```

Everything becomes:

```text
Who owns the thread?
Who owns the data?
Who owns the lock?
Who owns the queue?
Who owns the connection?
Who waits?
Who blocks?
Who retries?
Who times out?
```

---

# 3. Real Backend Flow

```text
Client Request
      ↓
Load Balancer
      ↓
Spring Boot / Tomcat
      ↓
Tomcat Worker Thread
      ↓
Controller
      ↓
Service
      ↓
DB / Redis / Kafka / External API
      ↓
Response
```

Concurrency problems can happen at every arrow.

---

# 4. Process vs Thread

## Process

```text
Running program with its own memory.
```

Examples:

```text
java -jar app.jar
redis-server
kafka broker
postgres process
```

## Thread

```text
Execution unit inside process.
Shares process heap memory.
```

Examples:

```text
Tomcat request thread
Kafka consumer worker
Scheduler thread
GC thread
Async executor thread
```

## Diagram

```text
JVM Process
│
├── main thread
├── Tomcat worker-1
├── Tomcat worker-2
├── Kafka listener thread
├── @Async executor thread
├── scheduler thread
└── GC thread
```

---

# 5. Process vs Thread Quick Table

| Concept | Process | Thread |
|---|---|---|
| Memory | Own memory | Shared heap |
| Cost | Heavy | Lighter |
| Communication | Harder | Easier |
| Failure isolation | Better | Worse |
| Backend example | order-service JVM | request worker thread |

---

# 6. Concurrency vs Parallelism

## Concurrency

```text
Multiple tasks make progress together.
```

Can happen on one CPU core by switching.

## Parallelism

```text
Multiple tasks run literally at same time.
```

Needs multiple CPU cores.

## Mental Model

```text
Concurrency = juggling many balls
Parallelism = many hands working at same time
```

---

# 7. Thread Lifecycle

```text
NEW
 |
 | start()
 v
RUNNABLE
 |
 | waiting for lock
 v
BLOCKED
 |
 | wait()/join()
 v
WAITING
 |
 | sleep()/timed wait
 v
TIMED_WAITING
 |
 | run() ends
 v
TERMINATED
```

## Important

```text
RUNNABLE does not always mean running on CPU.
It means ready or running.
```

---

# 8. Context Switching

```text
CPU pauses Thread-A
saves its state
loads Thread-B state
runs Thread-B
```

## Diagram

```text
Thread-A running
      ↓
save registers / stack pointer / program counter
      ↓
scheduler picks Thread-B
      ↓
restore Thread-B state
      ↓
Thread-B running
```

## Why It Matters

Too many threads cause:

```text
context switching overhead
CPU cache misses
scheduler overhead
poor latency
```

---

# 9. Race Condition

## Definition

```text
Race condition happens when multiple threads access shared mutable data
and final result depends on timing/order.
```

## Example

```java
counter++;
```

Looks one operation, but internally:

```text
1. read counter
2. add 1
3. write counter
```

## Race Dry Run

```text
counter = 0

Thread-A reads 0
Thread-B reads 0

Thread-A writes 1
Thread-B writes 1

Expected = 2
Actual   = 1
```

---

# 10. Critical Section

```text
Code that accesses shared mutable state.
```

Example:

```java
counter++;
```

Critical section must be protected.

## Mental Model

```text
Shared Data
    ↓
Critical Section
    ↓
Need Protection
    ↓
Lock / Atomic / Thread-safe structure
```

---

# 11. Mutex Lock

## Meaning

```text
Mutual exclusion.
Only one thread enters protected section at a time.
```

## Java

```java
synchronized (lock) {
    counter++;
}
```

## Flow

```text
Thread wants lock
    ↓
lock free?
    ├── yes → enter critical section
    └── no  → BLOCKED
```

---

# 12. ReentrantLock

More powerful explicit lock.

```java
lock.lock();
try {
    // critical section
} finally {
    lock.unlock();
}
```

## Features

```text
tryLock()
timeout lock
fairness
interruptible lock
manual unlock
```

## Must Remember

```text
Always unlock in finally.
```

---

# 13. ReadWriteLock

Good for read-heavy data.

```text
Many readers together
OR
one writer alone
```

## Diagram

```text
Reader-1 ✅
Reader-2 ✅
Reader-3 ✅

Writer ❌ waits
```

When writer active:

```text
Writer ✅
Reader ❌
Writer-2 ❌
```

## Good For

```text
config cache
route table
service discovery registry
feature flags
```

---

# 14. Atomic Variables

## Main Idea

```text
Thread-safe single-value updates without explicit locks.
```

Examples:

```java
AtomicInteger
AtomicLong
AtomicBoolean
AtomicReference
```

## CAS Mental Model

```text
I think value is 5.
If value is still 5, change to 6.
If not, retry.
```

## CAS Flow

```text
read expected
    ↓
compute updated
    ↓
compare current with expected
    ├── same → update success
    └── changed → retry
```

---

# 15. AtomicInteger vs synchronized

| Use Case | Best Choice |
|---|---|
| simple counter | AtomicInteger |
| flag | AtomicBoolean / volatile |
| multiple variables together | synchronized / Lock |
| complex business invariant | lock / DB transaction |

---

# 16. volatile

## Meaning

```text
Visibility guarantee.
```

If Thread-A writes volatile value:

```text
Thread-B sees latest value.
```

## Good Use

```java
private volatile boolean running = true;
```

## Bad Use

```java
volatile int counter;
counter++;
```

Why bad?

```text
volatile is visibility, not atomicity.
```

---

# 17. volatile Mental Model

```text
Without volatile:
Thread cache may hold stale value.

With volatile:
threads see latest written value.
```

Use for:

```text
shutdown flag
running flag
simple state flag
immutable config reference
```

---

# 18. Thread Communication

Threads coordinate using:

```text
wait()
notify()
notifyAll()
BlockingQueue
Condition
CompletableFuture
```

## Goal

```text
One thread waits until another thread signals work is ready.
```

---

# 19. wait / notify

## wait()

```text
release lock + pause thread
```

## notify()

```text
wake one waiting thread
```

## notifyAll()

```text
wake all waiting threads
```

## Rule

```text
wait/notify must be inside synchronized block.
```

---

# 20. wait vs sleep

| Feature | wait | sleep |
|---|---|---|
| Releases lock | Yes | No |
| Needs synchronized | Yes | No |
| Woken by | notify/notifyAll | timeout |
| Purpose | coordination | delay |

---

# 21. Producer Consumer Pattern

```text
Producer creates work
      ↓
Queue buffers work
      ↓
Consumer processes work
```

## Backend Examples

```text
HTTP request → background job queue
Kafka producer → topic → consumer
Log event → log queue → writer
Email task → queue → email worker
```

---

# 22. Producer Consumer Flow

```text
Producer
   |
   | put task
   v
+--------+
| Queue  |
+--------+
   |
   | take task
   v
Consumer
```

---

# 23. BlockingQueue

Thread-safe producer-consumer queue.

## Important Methods

| Method | Behavior |
|---|---|
| put() | wait if full |
| take() | wait if empty |
| offer() | return false if full |
| poll() | return null if empty |

## Production Rule

```text
Prefer bounded queue.
```

---

# 24. Bounded Queue Backpressure

```text
Queue full
    ↓
producer blocks / rejects
    ↓
system protected
```

Better:

```text
reject work
```

than:

```text
accept unlimited work and crash
```

---

# 25. Producer Consumer Production Design

Must include:

```text
bounded queue
worker threads
error handling
retry
DLQ
metrics
graceful shutdown
idempotency
```

---

# 26. Deadlock

## Definition

```text
Two or more threads wait forever for locks held by each other.
```

## Classic Flow

```text
Thread-1 holds Lock-A, waits Lock-B
Thread-2 holds Lock-B, waits Lock-A
```

## Diagram

```text
Thread-1 → waits for Lock-B → held by Thread-2
Thread-2 → waits for Lock-A → held by Thread-1
```

Cycle = deadlock.

---

# 27. Deadlock Prevention

```text
consistent lock ordering
avoid nested locks
small lock scope
tryLock timeout
avoid slow IO inside lock
```

## Best Practical Rule

```text
Always acquire locks in same order.
```

---

# 28. Livelock

```text
Threads are active but no useful progress.
```

Example:

```text
Thread-A retries
Thread-B retries
Thread-A retries
Thread-B retries
```

Fix:

```text
backoff + jitter + retry limit
```

---

# 29. Starvation

```text
One thread never gets fair chance.
```

Causes:

```text
unfair lock
priority scheduling
thread pool exhaustion
busy high-priority tasks
```

Fix:

```text
fair locks
separate pools
bounded queues
timeouts
```

---

# 30. Thread Pool Basics

## Definition

```text
Reusable worker threads execute tasks from a queue.
```

## Flow

```text
Task submitted
      ↓
Queue
      ↓
Worker Thread
      ↓
Task executed
      ↓
Worker reused
```

## Why

Avoid:

```text
new Thread() per request
```

because it causes:

```text
memory overhead
context switching
CPU pressure
```

---

# 31. ExecutorService

```java
ExecutorService executor =
    Executors.newFixedThreadPool(10);
```

## execute vs submit

```text
execute() = fire and forget
submit()  = returns Future
```

---

# 32. Future

```text
Future = result available later.
```

## Problem

```java
future.get();
```

blocks current thread.

Use timeout:

```java
future.get(2, TimeUnit.SECONDS);
```

---

# 33. ThreadPoolExecutor Internals

## Main Flow

```text
Task arrives
    ↓
active threads < corePoolSize?
    ├── yes → create core worker
    └── no
          ↓
       queue has space?
          ├── yes → enqueue
          └── no
                ↓
             active threads < maxPoolSize?
                ├── yes → create extra worker
                └── no → reject
```

---

# 34. ThreadPoolExecutor Parameters

```text
corePoolSize     = base workers
maximumPoolSize  = max workers
keepAliveTime    = idle timeout for extra workers
workQueue        = waiting task queue
threadFactory    = creates named threads
rejectionHandler = overload behavior
```

---

# 35. Rejection Policies

## AbortPolicy

```text
throw exception
```

## CallerRunsPolicy

```text
caller thread runs task
natural backpressure
```

## DiscardPolicy

```text
drop silently
```

## DiscardOldestPolicy

```text
drop oldest queued task
```

---

# 36. CompletableFuture

Async pipeline API.

## Basic Flow

```text
supplyAsync
    ↓
thenApply
    ↓
thenCompose
    ↓
thenCombine / allOf
    ↓
exceptionally / handle
```

---

# 37. CompletableFuture Methods

| Method | Meaning |
|---|---|
| supplyAsync | async task with result |
| runAsync | async task without result |
| thenApply | transform result |
| thenCompose | chain dependent async task |
| thenCombine | combine two futures |
| allOf | wait for many futures |
| exceptionally | fallback on error |
| handle | success/failure handling |

---

# 38. thenApply vs thenCompose

## thenApply

```text
value → transformed value
```

## thenCompose

```text
value → another async future
```

Example:

```text
getUser()
    ↓ thenCompose
getOrders(userId)
```

---

# 39. Parallel API Aggregation

```text
Request
  ↓
start userFuture
start orderFuture
start recommendationFuture
  ↓
allOf()
  ↓
combine response
  ↓
return JSON
```

Improves latency when tasks are independent.

---

# 40. Production Grade Concurrency Mental Model

```text
Requests arrive
      ↓
Tomcat thread pool
      ↓
service logic
      ↓
DB connection pool / Redis / Kafka
      ↓
async worker queues
      ↓
timeouts + retries + circuit breaker
      ↓
response
```

---

# 41. Spring Boot Request Thread Flow

```text
Client
  ↓
Tomcat acceptor
  ↓
Tomcat worker thread
  ↓
Controller
  ↓
Service
  ↓
Repository / Redis / Kafka / API
  ↓
Response
  ↓
worker returned to pool
```

---

# 42. Tomcat Thread Pool

```text
Tomcat does not create new thread per request.
It reuses worker threads.
```

If all workers blocked:

```text
new requests wait
latency increases
timeouts happen
```

---

# 43. Kafka Concurrency

```text
Producer → Topic → Partition → Consumer
```

Ordering:

```text
guaranteed only within same partition
```

Parallelism:

```text
more partitions → more consumers can process in parallel
```

---

# 44. Redis Concurrency

Redis core command execution is mostly:

```text
single-threaded event loop
```

Benefit:

```text
no complex locks for data structures
predictable command ordering
```

---

# 45. Database Connection Pool

```text
Request thread borrows DB connection
      ↓
runs query
      ↓
returns connection
```

If pool exhausted:

```text
request waits
latency increases
timeouts happen
```

---

# 46. Backpressure

```text
When system overloaded, slow down or reject producers.
```

Examples:

```text
bounded queues
CallerRunsPolicy
429 Too Many Requests
503 Service Unavailable
Kafka lag control
rate limiter
```

---

# 47. Bulkhead Pattern

```text
Separate resource pools for different work.
```

Example:

```text
payment-pool
email-pool
search-pool
analytics-pool
```

Benefit:

```text
email overload does not kill payments
```

---

# 48. Retry + Timeout + Circuit Breaker

## Timeout

```text
do not wait forever
```

## Retry

```text
retry temporary failure
```

## Circuit Breaker

```text
stop calling failing dependency temporarily
```

## Golden Rule

```text
retry without timeout/backoff is dangerous
```

---

# 49. Retry Storm

```text
Service slows
    ↓
many clients retry instantly
    ↓
service becomes slower
    ↓
more retries
    ↓
system collapse
```

Fix:

```text
exponential backoff + jitter + retry limit
```

---

# 50. Production Failure Cascade

```text
Slow database
      ↓
DB connections blocked
      ↓
Tomcat threads wait
      ↓
request queue grows
      ↓
timeouts increase
      ↓
retries increase
      ↓
system overload
```

---

# 51. Thread Dump Debugging

Commands:

```bash
jstack <pid>
jcmd <pid> Thread.print
```

Look for:

```text
BLOCKED       → lock contention / deadlock
WAITING       → waiting for queue/condition
TIMED_WAITING → sleep/timeout/wait
RUNNABLE      → running or ready
```

---

# 52. Common Production Symptoms

## Deadlock

```text
low CPU
requests stuck
BLOCKED threads
```

## Starvation

```text
queue grows
workers unavailable
some tasks never execute
```

## Retry Storm

```text
high CPU
high logs
high outbound calls
low success
```

## Pool Exhaustion

```text
all threads/connections busy
new work waits
```

---

# 53. ThreadLocal Warning

ThreadLocal is dangerous in thread pools.

Why?

```text
worker thread reused
old request data may remain
```

Always clean:

```java
try {
    // use ThreadLocal
} finally {
    threadLocal.remove();
}
```

---

# 54. CPU-Bound vs IO-Bound Pool Sizing

## CPU-Bound

```text
threads ≈ CPU cores
```

## IO-Bound

```text
threads can be more than CPU cores
```

because many threads wait on network/DB.

---

# 55. Golden Rules For Production Concurrency

```text
1. Prefer bounded queues.
2. Use timeouts everywhere.
3. Use retries carefully.
4. Add backoff + jitter.
5. Separate thread pools by workload.
6. Monitor queue size.
7. Monitor active threads.
8. Avoid unbounded async tasks.
9. Avoid shared mutable state.
10. Use thread dumps for debugging.
```

---

# 56. Interview Quick Answers

## What is race condition?

```text
Multiple threads access shared mutable data and result depends on timing.
```

## What is deadlock?

```text
Threads wait forever for resources held by each other.
```

## What is volatile?

```text
Visibility guarantee, not atomicity.
```

## What is AtomicInteger?

```text
Thread-safe counter using CAS.
```

## What is BlockingQueue?

```text
Thread-safe queue where producers/consumers block when full/empty.
```

## Why thread pool?

```text
Reuse worker threads and control concurrency.
```

## Why CompletableFuture?

```text
Compose async tasks and run independent work in parallel.
```

## What is backpressure?

```text
Slow or reject producers when consumers/system are overloaded.
```

---

# 57. Final One-Page Mental Model

```text
Client Requests
      ↓
Thread Pool
      ↓
Queue
      ↓
Worker Threads
      ↓
Shared Data
      ↓
Locks / Atomics / Concurrent Collections
      ↓
DB / Redis / Kafka / External APIs
      ↓
Timeouts / Retries / Circuit Breaker
      ↓
Backpressure / Rejection
      ↓
Stable Production System
```

---

# 58. Final Summary

Concurrency mastery means understanding:

```text
threads
memory sharing
race conditions
locks
atomics
visibility
queues
thread pools
async pipelines
deadlocks
backpressure
production debugging
```

If you understand this cheat sheet, you can better understand:

```text
Spring Boot internals
Tomcat worker threads
Kafka consumer concurrency
Redis event loop
Database connection pooling
High-scale backend systems
System design production tradeoffs
```
