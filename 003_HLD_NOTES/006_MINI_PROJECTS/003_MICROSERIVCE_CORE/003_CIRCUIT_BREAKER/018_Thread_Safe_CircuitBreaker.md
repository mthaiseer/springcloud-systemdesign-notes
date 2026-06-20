# 018_Thread_Safe_CircuitBreaker.md

# MiniCircuitBreaker — 018 Thread Safe CircuitBreaker

---

# 1. Why This File Exists

Previous files explained:

```text
Circuit Breaker states
Sliding windows
Failure rate
Slow call rate
Timeout
Retry
Bulkhead
RateLimiter
```

Now we must understand a very important production problem:

```text
many threads call circuit breaker at the same time
```

A circuit breaker is used on:

```text
every protected request
```

So in production:

```text
hundreds/thousands of threads
```

may simultaneously:

```text
check state
record success
record failure
update sliding window
transition CLOSED → OPEN
transition OPEN → HALF_OPEN
```

If this is not thread-safe:

```text
metrics become wrong
state transitions become corrupted
HALF_OPEN permits leak
circuit opens incorrectly
circuit never opens
```

This file explains:

```text
thread safety
race conditions
shared state
atomic counters
synchronized vs lock-free
volatile
AtomicReference
LongAdder
thread-safe sliding windows
safe state transitions
concurrent metrics update
Java implementation
production bugs
```

---

# 2. One-Line Definition

```text
Thread-safe circuit breaker means multiple threads can safely read and update circuit breaker state without corrupting metrics or transitions.
```

---

# 3. Biggest Mental Model

```text
Many Threads
     ↓
Shared CircuitBreaker State
     ↓
Need Safe Updates
```

---

# 4. Why Thread Safety Critical

Circuit breaker stores shared mutable data:

```text
current state
failure count
success count
slow call count
sliding window
OPEN timestamp
HALF_OPEN permits
```

Multiple threads update these concurrently.

---

# 5. Shared State ASCII

```text
Thread A ─┐
Thread B ─┼──> CircuitBreaker State
Thread C ─┘
```

If not protected:

```text
race condition
```

---

# 6. Race Condition

Race condition happens when:

```text
multiple threads access shared data
and final result depends on timing
```

---

# 7. Race Condition Example

Suppose:

```text
failureCount = 10
```

Two threads increment at same time.

Expected:

```text
12
```

Actual may become:

```text
11
```

because both read old value.

---

# 8. Lost Update ASCII

```text
failureCount = 10

Thread A reads 10
Thread B reads 10

Thread A writes 11
Thread B writes 11

Expected 12
Actual 11
```

---

# 9. Why Lost Updates Dangerous

Circuit breaker may calculate:

```text
wrong failure rate
```

Then:

```text
OPEN too late
or
never OPEN
```

---

# 10. State Transition Race

Suppose multiple threads detect:

```text
failure threshold exceeded
```

All try:

```text
CLOSED → OPEN
```

Without safe transition:

```text
duplicate events
wrong timestamps
broken metrics reset
```

---

# 11. State Race ASCII

```text
Thread A: CLOSED → OPEN
Thread B: CLOSED → OPEN
Thread C: CLOSED → OPEN
```

Only one should win.

---

# 12. HALF_OPEN Race

HALF_OPEN allows limited probe requests.

Example:

```text
permitted calls = 5
```

Without thread safety:

```text
50 threads may enter
```

This destroys controlled recovery.

---

# 13. HALF_OPEN Race ASCII

```text
HALF_OPEN permits = 5

50 threads see permit available
        ↓
50 probe calls hit recovering service
```

Dangerous.

---

# 14. Thread Safety Goals

Circuit breaker must ensure:

```text
correct counters
correct state visibility
single valid transition
safe sliding window update
safe HALF_OPEN permit control
low latency
```

---

# 15. Naive Non-Thread-Safe Code

```java
public class UnsafeCircuitBreaker {

    private int failureCount = 0;

    public void recordFailure() {

        failureCount++;
    }
}
```

Problem:

```text
failureCount++ is not atomic
```

---

# 16. Why failureCount++ Not Atomic

This operation has steps:

```text
read value
add 1
write value
```

Multiple threads can interleave.

---

# 17. AtomicInteger Solution

```java
import java.util.concurrent.atomic.AtomicInteger;

public class SafeCounter {

    private final AtomicInteger failureCount =
            new AtomicInteger(0);

    public void recordFailure() {

        failureCount.incrementAndGet();
    }

    public int failures() {

        return failureCount.get();
    }
}
```

---

# 18. AtomicInteger Mental Model

```text
increment happens as one atomic operation
```

No lost update.

---

# 19. AtomicInteger Dry Run

Initial:

```text
failureCount = 10
```

Two threads increment.

Atomic result:

```text
12
```

Correct.

---

# 20. LongAdder

For very high contention counters, Java provides:

```text
LongAdder
```

It spreads updates internally.

Good for:

```text
high-throughput metrics
```

---

# 21. LongAdder Example

```java
import java.util.concurrent.atomic.LongAdder;

public class MetricsCounter {

    private final LongAdder failures =
            new LongAdder();

    public void recordFailure() {

        failures.increment();
    }

    public long failureCount() {

        return failures.sum();
    }
}
```

---

# 22. AtomicInteger vs LongAdder

## AtomicInteger

```text
exact atomic value
good for control logic
```

## LongAdder

```text
better under high contention
good for metrics
```

---

# 23. volatile

`volatile` ensures:

```text
visibility
```

not atomicity.

---

# 24. volatile Example

```java
private volatile boolean open;
```

If one thread sets:

```text
open = true
```

other threads see latest value.

But:

```text
volatile does not make count++ atomic
```

---

# 25. volatile Mental Model

```text
visibility yes
compound atomicity no
```

---

# 26. synchronized

`synchronized` protects critical sections.

```java
public synchronized void recordFailure() {

    failureCount++;
}
```

Guarantees:

```text
mutual exclusion
visibility
```

---

# 27. synchronized Pros

```text
simple
correct
easy to reason
```

---

# 28. synchronized Cons

```text
thread blocking
lock contention
latency spikes
lower throughput
```

---

# 29. Lock-Free Approach

Production circuit breakers prefer:

```text
AtomicReference
AtomicInteger
LongAdder
CAS
```

to reduce blocking.

---

# 30. AtomicReference For State

Circuit breaker state should be stored safely.

```java
import java.util.concurrent.atomic.AtomicReference;

public class StateHolder {

    private final AtomicReference<State> state =
            new AtomicReference<>(State.CLOSED);
}
```

---

# 31. State Enum

```java
public enum State {

    CLOSED,
    OPEN,
    HALF_OPEN
}
```

---

# 32. CAS Transition

CAS means:

```text
Compare And Set
```

Only update if current value equals expected value.

---

# 33. CAS Mental Model

```text
if current == expected:
    update
else:
    fail
```

---

# 34. CLOSED → OPEN CAS

```java
public boolean transitionToOpen() {

    return state.compareAndSet(
            State.CLOSED,
            State.OPEN
    );
}
```

---

# 35. CAS Dry Run

Current state:

```text
CLOSED
```

Thread A:

```text
compareAndSet(CLOSED, OPEN)
```

Success.

Thread B:

```text
compareAndSet(CLOSED, OPEN)
```

Fails because state already OPEN.

Only one transition wins.

---

# 36. Why CAS Important

CAS prevents:

```text
duplicate transitions
wrong event publishing
timestamp corruption
metrics reset races
```

---

# 37. State Visibility

Using AtomicReference ensures all threads see:

```text
latest state
```

---

# 38. Thread-Safe State Machine

```java
import java.util.concurrent.atomic.AtomicReference;

public class ThreadSafeStateMachine {

    private final AtomicReference<State> state =
            new AtomicReference<>(State.CLOSED);

    public State currentState() {

        return state.get();
    }

    public boolean toOpen() {

        return state.compareAndSet(
                State.CLOSED,
                State.OPEN
        );
    }

    public boolean toHalfOpen() {

        return state.compareAndSet(
                State.OPEN,
                State.HALF_OPEN
        );
    }

    public boolean toClosed() {

        return state.compareAndSet(
                State.HALF_OPEN,
                State.CLOSED
        );
    }
}
```

---

# 39. Thread-Safe Metrics

Circuit breaker metrics need safe updates:

```text
total calls
failed calls
slow calls
successful calls
```

---

# 40. Metrics With LongAdder

```java
import java.util.concurrent.atomic.LongAdder;

public class CircuitMetrics {

    private final LongAdder total =
            new LongAdder();

    private final LongAdder failures =
            new LongAdder();

    private final LongAdder slowCalls =
            new LongAdder();

    public void recordSuccess() {

        total.increment();
    }

    public void recordFailure() {

        total.increment();
        failures.increment();
    }

    public void recordSlowCall() {

        total.increment();
        slowCalls.increment();
    }

    public double failureRate() {

        long totalCalls = total.sum();

        if (totalCalls == 0) {
            return 0.0;
        }

        return failures.sum() * 100.0
                / totalCalls;
    }
}
```

---

# 41. Important LongAdder Warning

LongAdder is great for metrics.

But for exact state decisions:

```text
AtomicInteger / CAS often better
```

because LongAdder sum may not be perfectly instant under concurrent updates.

---

# 42. Sliding Window Thread Safety

Sliding window has shared data:

```text
bucket array
current index
counters
timestamps
```

Need safe update.

---

# 43. Sliding Window Race

Thread A updates bucket.

Thread B resets same bucket.

Metrics become inconsistent.

---

# 44. Safe Sliding Window Options

Use:

```text
synchronized window update
bucket-level locks
atomic bucket counters
CAS-based ring buffer
```

---

# 45. Simple Safe Window

```java
public class SafeWindow {

    private int total;
    private int failures;

    public synchronized void recordFailure() {

        total++;
        failures++;
    }

    public synchronized double failureRate() {

        if (total == 0) {
            return 0.0;
        }

        return failures * 100.0 / total;
    }
}
```

Good for learning.

Production may optimize.

---

# 46. Performance Tradeoff

`synchronized` is simple but may bottleneck.

Atomic structures are faster but harder.

---

# 47. HALF_OPEN Permit Control

HALF_OPEN must allow limited probe calls.

Use:

```text
AtomicInteger permits
Semaphore
CAS counter
```

---

# 48. HALF_OPEN Permit Example

```java
import java.util.concurrent.Semaphore;

public class HalfOpenPermits {

    private final Semaphore permits =
            new Semaphore(5);

    public boolean tryAcquireProbe() {

        return permits.tryAcquire();
    }

    public void releaseProbe() {

        permits.release();
    }
}
```

---

# 49. HALF_OPEN Dry Run

Permits:

```text
5
```

Threads:

```text
T1 T2 T3 T4 T5
```

Allowed.

Thread:

```text
T6
```

Rejected.

---

# 50. Permit Leak Problem

If probe request throws exception and permit not released:

```text
permit leak
```

Eventually:

```text
HALF_OPEN stuck
```

Always release in:

```java
finally
```

---

# 51. Full Thread-Safe Mini CB

```java
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

public class MiniCircuitBreaker {

    private final AtomicReference<State> state =
            new AtomicReference<>(State.CLOSED);

    private final LongAdder total =
            new LongAdder();

    private final LongAdder failures =
            new LongAdder();

    private final double threshold = 50.0;

    public boolean allowRequest() {

        return state.get() != State.OPEN;
    }

    public void recordSuccess() {

        total.increment();
    }

    public void recordFailure() {

        total.increment();
        failures.increment();

        if (failureRate() >= threshold) {

            state.compareAndSet(
                    State.CLOSED,
                    State.OPEN
            );
        }
    }

    public double failureRate() {

        long totalCalls = total.sum();

        if (totalCalls == 0) {
            return 0.0;
        }

        return failures.sum() * 100.0
                / totalCalls;
    }
}
```

---

# 52. Mini CB Dry Run

Initial:

```text
state = CLOSED
```

Many failures recorded concurrently.

When failure rate crosses threshold:

```text
multiple threads try CLOSED → OPEN
```

CAS ensures:

```text
only one wins
```

---

# 53. Event Publishing Race

If every thread publishes:

```text
CLOSED_TO_OPEN event
```

dashboard gets duplicate alerts.

CAS solves this:

```text
publish event only if compareAndSet succeeds
```

---

# 54. Correct Event Publishing

```java
if (state.compareAndSet(
        State.CLOSED,
        State.OPEN)) {

    publishStateTransitionEvent();
}
```

---

# 55. OPEN Timestamp Race

When opening circuit, set:

```text
openedAt timestamp
```

Only winning transition thread should set it.

---

# 56. Correct Timestamp Logic

```java
if (state.compareAndSet(
        State.CLOSED,
        State.OPEN)) {

    openedAt =
            System.currentTimeMillis();
}
```

---

# 57. Thread Safety Bugs In Production

Common bugs:

```text
wrong failure rate
duplicate transition events
HALF_OPEN too many probes
OPEN timestamp overwritten
sliding window corrupted
permit leaks
deadlocks
```

---

# 58. Deadlock Risk

If synchronized blocks call external code:

```text
deadlock risk
```

Never hold lock while making remote calls.

---

# 59. Bad Lock Example

```java
public synchronized String call() {

    return remoteService.call();
}
```

Problem:

```text
lock held during network call
```

Very dangerous.

---

# 60. Correct Lock Scope

Only lock:

```text
state update
metrics update
```

Do NOT lock:

```text
remote call execution
```

---

# 61. Correct Flow

```text
Check state quickly
      ↓
Release any lock
      ↓
Call remote service
      ↓
Update metrics safely
```

---

# 62. Observability Metrics

Monitor concurrency safety signals:

```text
state transition count
failure rate
OPEN count
HALF_OPEN rejected count
permit leakage symptoms
metrics inconsistency
```

---

# 63. Common Mistakes

## Mistake 1

```text
Using int counters without atomicity
```

Lost updates.

---

## Mistake 2

```text
Using volatile for counters
```

Visibility only, not atomicity.

---

## Mistake 3

```text
Holding synchronized lock during remote call
```

Deadlock/latency risk.

---

## Mistake 4

```text
No CAS for state transitions
```

Duplicate transitions.

---

## Mistake 5

```text
No HALF_OPEN permit control
```

Recovery traffic spike.

---

# 64. Most Important Insight

```text
Circuit breaker is a concurrent state machine.
```

NOT just:

```text
if failure then open
```

---

# 65. Distributed Systems Insight

At high traffic, correctness bugs appear only under:

```text
concurrency
load
failure
```

Exactly when you need circuit breaker most.

---

# 66. Interview Explanation

If interviewer asks:

```text
How do you make circuit breaker thread-safe?
```

Strong answer:

```text
Use atomic counters or LongAdder for metrics, AtomicReference with CAS for
state transitions, and semaphore/atomic permit control for HALF_OPEN. Avoid
holding locks during remote calls and publish transition events only when
CAS succeeds.
```

Senior addition:

```text
The circuit breaker is a concurrent finite state machine, so state
visibility, lost updates, duplicate transitions, and HALF_OPEN probe races
must all be handled carefully.
```

---

# 67. Final Mental Model

```text
Shared State
    +
Many Threads
    ↓
Atomic Updates
    +
CAS Transitions
    ↓
Correct CircuitBreaker
```

---

# 68. What To Remember

```text
Circuit breaker is used by many threads.

Shared metrics must be thread-safe.

failureCount++ is not atomic.

volatile gives visibility, not atomicity.

AtomicInteger is good for exact counters.

LongAdder is good for high-throughput metrics.

AtomicReference + CAS protects state transitions.

HALF_OPEN needs permit control.

Do not hold locks during remote calls.

Publish events only on successful transition.
```

---

# 69. Next File

```text
019_Atomic_State_Management_CAS.md
```

Next you learn:

```text
CAS deeper
AtomicReference internals
compareAndSet loops
ABA problem basics
lock-free state machine
safe transition events
production transition design
```
