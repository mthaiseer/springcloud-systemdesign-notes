
# 022_Concurrency_CheatSheet.md

# MiniConcurrency — Final Revision CheatSheet

# 0. Core Mental Model

Concurrency =
multiple tasks making progress together

NOT always parallel.

---

# 1. Process vs Thread

## Process

Independent program
Own memory
Heavyweight

## Thread

Lightweight execution unit inside process
Shares memory

---

# 2. Concurrency vs Parallelism

## Concurrency

Tasks overlap in progress

## Parallelism

Tasks run literally at same time

Need multiple CPU cores.

---

# 3. Thread Lifecycle

NEW
↓
RUNNABLE
↓
RUNNING
↓
BLOCKED / WAITING / TIMED_WAITING
↓
TERMINATED

---

# 4. Context Switching

CPU switches between threads.

Too many threads:
high overhead.

---

# 5. Race Condition

Multiple threads modify shared data simultaneously.

Example:

counter++

NOT atomic.

---

# 6. Critical Section

Code accessing shared mutable state.

Protect using:
lock / atomic / synchronization

---

# 7. Mutex Lock

synchronized(lock)

Only one thread enters critical section.

---

# 8. ReentrantLock

Features:
- tryLock()
- fairness
- interruptible lock
- multiple conditions

---

# 9. ReadWriteLock

Read Lock:
multiple readers allowed.

Write Lock:
single writer only.

Good for:
cache / configuration / read-heavy systems

---

# 10. Atomic Variables

AtomicInteger
AtomicLong
AtomicReference

Internally uses CAS.

---

# 11. volatile Keyword

Guarantees:
visibility

Does NOT guarantee:
atomicity

---

# 12. Thread Communication

wait()
notify()
notifyAll()
BlockingQueue
Conditions

---

# 13. Wait Notify

wait():
release lock + sleep

notify():
wake one thread

notifyAll():
wake all waiting threads

---

# 14. Producer Consumer Pattern

Producer:
creates tasks/messages

Consumer:
processes tasks/messages

Used in:
Kafka
RabbitMQ
Thread pools

---

# 15. BlockingQueue

Thread-safe queue.

Methods:
put()
take()
offer()
poll()

---

# 16. Deadlock

Thread-A waits for Thread-B
Thread-B waits for Thread-A

Nobody progresses.

Prevention:
consistent lock ordering
timeouts
avoid nested locks

---

# 17. Livelock

Threads active
but no useful progress.

---

# 18. Starvation

One thread never gets fair chance.

---

# 19. Thread Pool Basics

Reusable worker threads.

Mental model:

Tasks
↓
Queue
↓
Workers

---

# 20. ExecutorService

Executors.newFixedThreadPool()

---

# 21. submit vs execute

execute():
fire-and-forget

submit():
returns Future

---

# 22. Future

Result available later.

future.get() is blocking.

---

# 23. CompletableFuture

Async pipeline API.

---

# 24. thenApply

Transform value.

A → B

---

# 25. thenCompose

Chain async task.

Future<A> → Future<B>

---

# 26. allOf

Run futures in parallel.

---

# 27. thenCombine

Combine independent futures.

---

# 28. CompletableFuture Mental Model

Start async task
↓
Transform
↓
Compose
↓
Combine
↓
Final result

---

# 29. ThreadPoolExecutor Internals

core threads
↓
queue
↓
extra threads
↓
rejection policy

---

# 30. Queue Types

ArrayBlockingQueue:
bounded

LinkedBlockingQueue:
often unbounded

SynchronousQueue:
direct handoff

---

# 31. Rejection Policies

AbortPolicy
CallerRunsPolicy
DiscardPolicy

CallerRunsPolicy gives natural backpressure.

---

# 32. CPU vs IO Bound

CPU-bound:
threads ≈ CPU cores

IO-bound:
can use more threads

---

# 33. Thread Starvation

All workers blocked.

Symptoms:
queue growth
timeouts
high latency

---

# 34. Backpressure

Slow producers when overloaded.

---

# 35. Bulkhead Pattern

Separate thread pools.

payment pool
notification pool
search pool

---

# 36. Retry Pattern

Danger:
retry storm

Use:
exponential backoff + jitter

---

# 37. Timeout Pattern

Never wait forever.

future.get(timeout)

---

# 38. Circuit Breaker

Stop calling failing dependency temporarily.

---

# 39. Redis Concurrency Model

Single-threaded event loop.

Avoids lock complexity.

---

# 40. Kafka Concurrency Model

Partition-based parallelism.

Ordering guaranteed inside partition only.

---

# 41. Database Connection Pool

Reuse DB connections.

Example:
HikariCP

---

# 42. Spring Boot Concurrency

HTTP Request
↓
Tomcat Thread Pool
↓
Controller
↓
Service
↓
DB/API/Kafka
↓
Response

---

# 43. Tomcat Mental Model

Worker thread pool handles requests.

NOT new thread per request.

---

# 44. Event Loop Model

Used by:
Redis
Netty
NodeJS
WebFlux

few threads
many connections
non-blocking IO

---

# 45. Lock Contention

Many threads competing same lock.

Fix:
reduce lock scope
sharding
lock-free structures

---

# 46. ThreadLocal

Danger in thread pools:
memory leak
request data leakage

---

# 47. Production Metrics

Monitor:
CPU
queue size
thread count
rejected tasks
latency
Kafka lag

---

# 48. Thread Dump Debugging

jstack <pid>
jcmd <pid> Thread.print

Look for:
BLOCKED threads
deadlocks
high RUNNABLE threads

---

# 49. High CPU Problems

Possible causes:
busy loop
retry storm
lock contention

---

# 50. Production Golden Rules

Prefer bounded queues
Use timeouts
Use retries carefully
Separate thread pools
Avoid unnecessary blocking
Monitor everything

---

# 51. Final Backend Mental Model

Requests
↓
Thread Pools
↓
Queues
↓
Workers
↓
DB/Kafka/Redis
↓
Async Processing
↓
Response

---

# 52. Final Summary

Concurrency is mostly:

threads
queues
locks
async pipelines
worker pools
coordination
backpressure
timeouts

Master these deeply to understand:

Spring Boot internals
Kafka internals
Redis internals
Tomcat internals
high-scale backend systems
