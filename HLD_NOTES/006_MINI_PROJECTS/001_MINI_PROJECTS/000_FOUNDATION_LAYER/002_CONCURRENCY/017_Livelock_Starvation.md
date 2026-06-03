# 017_Livelock_Starvation.md

# MiniConcurrency — 017 Livelock & Starvation

## 0. Why This File Exists

After learning:

- Locks
- Deadlocks
- BlockingQueue
- Producer-Consumer

the next important concurrency problems are:

- Livelock
- Starvation

These are tricky because:

- system may still be running
- threads may still be active
- CPU may still be busy
- but useful progress is poor or zero

This file teaches:

- What starvation is
- What livelock is
- Difference from deadlock
- Real backend examples
- Fairness
- Priority problems
- Retry loops
- Lock contention
- Thread scheduling
- ReentrantLock fairness
- Prevention techniques
- Production debugging

---

# 1. One-Line Definitions

## Starvation

A thread waits too long because other threads keep getting resources first.

## Livelock

Threads keep reacting to each other but no useful progress happens.

---

# 2. Simple Mental Model

## Deadlock

Nobody moves.

## Livelock

Everybody moves,
but nobody progresses.

## Starvation

One thread never gets fair chance.

---

# 3. Real-Life Deadlock Example

Two people block doorway.

Person A waits.
Person B waits.
Nobody moves.

Deadlock.

---

# 4. Real-Life Livelock Example

Two people try to avoid each other:

- A moves left
- B moves left
- A moves right
- B moves right

Both keep moving forever.

Nobody crosses.

Livelock.

---

# 5. Real-Life Starvation Example

Restaurant kitchen:

VIP orders always processed first.
Normal customer waits forever.

Starvation.

---

# 6. Deadlock vs Livelock vs Starvation

| Problem | Threads Active? | Progress? |
|---|---|---|
| Deadlock | No | No |
| Livelock | Yes | No |
| Starvation | Some | One thread suffers |

---

# 7. Starvation Definition Deeply

Starvation means:

A thread keeps waiting because other threads repeatedly get CPU/lock/resource first.

Example:

high-priority threads dominate,
low-priority thread rarely runs.

---

# 8. Common Causes Of Starvation

- Unfair locks
- Priority scheduling
- Infinite high-priority traffic
- Aggressive retry loops
- CPU starvation
- Thread pool overload
- Busy waiting

---

# 9. Starvation Example — Unfair Lock

Imagine:

10 fast threads repeatedly acquire lock.
1 slow thread keeps waiting.

Slow thread may starve.

---

# 10. Why synchronized Can Starve

synchronized does NOT guarantee fairness.

Meaning:

thread waiting longest
may not get lock next.

JVM scheduler decides.

---

# 11. Fairness

Fairness means:

threads get fair chance to acquire resource.

Example:

FIFO lock ordering.

---

# 12. ReentrantLock Fairness

ReentrantLock supports fairness mode.

new ReentrantLock(true)

Meaning:

longest waiting thread gets priority.

---

# 13. Tradeoff Of Fair Locks

Fair locks reduce starvation.

But:

- lower throughput
- more scheduling overhead
- less optimization opportunity

Production tradeoff:

fairness vs throughput.

---

# 14. Thread Priority Starvation

Java supports thread priority.

Problem:

high-priority threads may dominate CPU.

Low-priority threads may starve.

---

# 15. CPU Starvation

CPU starvation happens when:

too many CPU-heavy threads run.

Other tasks:

- GC
- network threads
- heartbeat threads

may struggle.

Symptoms:

- timeouts
- slow health checks
- lag

---

# 16. Thread Pool Starvation

Example:

Thread pool size = 10

All 10 threads blocked on slow API.

New tasks cannot execute.

Queue grows forever.

This is thread starvation.

---

# 17. Backend Example — API Thread Starvation

Request thread calls slow external API.

All worker threads blocked.

New requests wait forever.

Common production issue.

---

# 18. Prevention — Separate Thread Pools

Good design:

- HTTP pool
- DB pool
- Background worker pool
- Scheduler pool

Isolation prevents starvation spreading.

---

# 19. Prevention — Timeouts

Never wait forever.

Bad:

future.get()

Better:

future.get(2, TimeUnit.SECONDS)

Timeout prevents stuck resources.

---

# 20. Prevention — Bounded Queues

Bad:

unbounded queue.

Tasks pile forever.

Better:

bounded queue + rejection.

---

# 21. Prevention — Backpressure

When overloaded:

- reject work
- slow producers
- return 429

This protects worker threads.

---

# 22. What Is Livelock?

Livelock means:

threads keep changing state
but useful progress never happens.

Threads are active.

System is alive.

But work never completes.

---

# 23. Classic Livelock Example

Two polite people:

- Person A moves left
- Person B moves left
- Person A moves right
- Person B moves right

Forever.

No progress.

---

# 24. Livelock In Concurrency

Threads:

- detect contention
- release resource
- retry immediately
- repeat forever

No deadlock.

But no progress.

---

# 25. Why Livelock Happens

Overly polite retry behavior:

- detect contention
- release resource
- retry immediately

Without randomness or delay.

---

# 26. Livelock Symptoms

- CPU high
- threads active
- logs continuously printing
- requests never complete

Different from deadlock:

deadlock often has low CPU.

---

# 27. Livelock Prevention

Add:

- random delay
- backoff
- retry limit
- ownership rules

---

# 28. Backoff Strategy

Instead of immediate retry:

Thread.sleep(randomDelay)

Example delays:

- 10ms
- 50ms
- 100ms

Randomness breaks synchronization pattern.

---

# 29. Exponential Backoff

Retry delays grow:

- 100ms
- 200ms
- 400ms
- 800ms

Used heavily in:

- distributed systems
- Kafka retries
- network retries
- microservices

---

# 30. Retry Storm Problem

Without backoff:

all clients retry together.

Can overload system further.

This causes:

retry storm.

Very dangerous in microservices.

---

# 31. Backend Example — Retry Storm

Database becomes slow.

All services retry instantly.

DB gets even more overloaded.

More retries happen.

System collapse possible.

---

# 32. Use Jitter

Jitter means:

randomize retry delay.

Instead of:

all retry at exact same time.

---

# 33. Starvation vs Deadlock

Starvation:

one thread unlucky,
others still progress.

Deadlock:

all involved threads freeze.

---

# 34. Livelock vs Deadlock

Deadlock:

threads blocked.

Livelock:

threads active but useless.

---

# 35. Production Symptoms

## Starvation

- some requests very delayed
- certain tasks never complete
- queue lag
- thread pool exhaustion

## Livelock

- high retries
- high CPU
- continuous logging
- no successful completion

---

# 36. Monitoring Metrics

Useful metrics:

- queue size
- retry count
- thread blocked time
- thread wait time
- request latency
- CPU usage
- task completion rate

---

# 37. Prevention Summary

## Prevent Starvation

- fair locks
- bounded queues
- timeouts
- separate thread pools
- backpressure
- avoid CPU monopolization

## Prevent Livelock

- random delay
- exponential backoff
- jitter
- retry limits
- better coordination

---

# 38. Interview Explanation

## What is starvation?

Starvation occurs when a thread waits indefinitely because other
threads repeatedly get access to CPU or locks first.

## What is livelock?

Livelock occurs when threads keep reacting to each other and changing
state, but no useful work gets completed.

Strong backend addition:

Retry storms in distributed systems are a common livelock-like problem.
Exponential backoff with jitter is a common solution.

---

# 39. Common Mistakes

## Mistake 1

Infinite retry loops without backoff.

Can cause livelock.

## Mistake 2

Using unbounded queues.

Can cause starvation and overload.

## Mistake 3

One thread pool for everything.

Slow tasks block critical tasks.

## Mistake 4

Ignoring fairness under contention.

Some tasks may starve.

## Mistake 5

Retrying immediately under failures.

Can amplify outages.

---

# 40. Mini Dry Run Summary

## Starvation

Fast threads repeatedly acquire resource.
Slow thread keeps waiting.

## Livelock

Thread-1 retries.
Thread-2 retries.
Both active forever.
No useful completion.

---

# 41. What To Remember

Deadlock:
nothing moves.

Livelock:
everything moves but no progress.

Starvation:
one thread never gets fair chance.

Fairness helps starvation.
Backoff + jitter help livelock.

---

# 42. Next File

018_ThreadPoolExecutor.md

Next you learn:

- ThreadPoolExecutor internals
- core threads
- max threads
- work queue
- rejection policy
- Future
- submit vs execute
- real backend worker pools
