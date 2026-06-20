# 019_Atomic_State_Management_CAS.md

# MiniCircuitBreaker — 019 Atomic State Management CAS

---

# 1. Why This File Exists

Previous file explained:

```text
thread-safe circuit breaker
atomic counters
LongAdder
AtomicReference
basic CAS
```

Now we go deeper into the most important concurrency mechanism inside production-grade circuit breakers:

```text
CAS
```

CAS means:

```text
Compare And Set
```

Circuit breaker state transitions must be:

```text
atomic
valid
single-winner
lock-free or low-lock
observable
```

Without CAS, multiple threads may corrupt:

```text
CLOSED → OPEN transition
OPEN → HALF_OPEN transition
HALF_OPEN → CLOSED transition
HALF_OPEN → OPEN transition
openedAt timestamp
event publishing
metrics reset
HALF_OPEN permits
```

This file explains:

```text
AtomicReference
compareAndSet
CAS loops
single-winner transition
lock-free state machine
state transition metadata
event publishing correctness
ABA problem basics
AtomicInteger vs AtomicReference
safe timestamp updates
production transition design
Java implementation
dry runs
```

---

# 2. One-Line Definition

```text
CAS updates state only if the current value is still the expected value.
```

---

# 3. Biggest Mental Model

```text
I will update state ONLY if nobody changed it before me.
```

---

# 4. CAS Formula

```text
compareAndSet(expectedValue, newValue)
```

Meaning:

```text
if current == expectedValue:
    current = newValue
    return true
else:
    return false
```

---

# 5. Why CAS Needed In Circuit Breaker

Many threads may detect:

```text
failure threshold crossed
```

at the same time.

All try:

```text
CLOSED → OPEN
```

But only one transition should happen.

CAS ensures:

```text
single winner
```

---

# 6. Race Without CAS

```text
Thread A sees CLOSED
Thread B sees CLOSED
Thread C sees CLOSED

All set state = OPEN
All publish event
All reset metrics
All set openedAt
```

Bad.

---

# 7. Race Without CAS ASCII

```text
CLOSED
  ↓
Thread A → OPEN
Thread B → OPEN
Thread C → OPEN

Duplicate transition side effects
```

---

# 8. With CAS

```text
Thread A compareAndSet(CLOSED, OPEN) → true
Thread B compareAndSet(CLOSED, OPEN) → false
Thread C compareAndSet(CLOSED, OPEN) → false
```

Only Thread A performs transition side effects.

---

# 9. With CAS ASCII

```text
CLOSED
  ↓
Thread A wins CAS
  ↓
OPEN

Thread B/C see already OPEN
```

---

# 10. AtomicReference

Circuit breaker state is commonly stored in:

```java
AtomicReference<State>
```

because state is not just a number.

It is an enum/object:

```text
CLOSED
OPEN
HALF_OPEN
```

---

# 11. State Enum

```java
public enum State {

    CLOSED,
    OPEN,
    HALF_OPEN
}
```

---

# 12. AtomicReference Example

```java
import java.util.concurrent.atomic.AtomicReference;

public class StateHolder {

    private final AtomicReference<State> state =
            new AtomicReference<>(State.CLOSED);

    public State getState() {

        return state.get();
    }
}
```

---

# 13. Basic CAS Transition

```java
public boolean transitionToOpen() {

    return state.compareAndSet(
            State.CLOSED,
            State.OPEN
    );
}
```

---

# 14. CAS Dry Run

Current:

```text
state = CLOSED
```

Call:

```text
compareAndSet(CLOSED, OPEN)
```

Result:

```text
true
state = OPEN
```

---

# 15. CAS Failed Dry Run

Current:

```text
state = OPEN
```

Call:

```text
compareAndSet(CLOSED, OPEN)
```

Result:

```text
false
state remains OPEN
```

---

# 16. Why Boolean Return Important

CAS returns:

```text
true
```

only when current thread performed transition.

Use this to control side effects.

---

# 17. Side Effects Must Only Happen On CAS Success

Side effects include:

```text
publish state transition event
set openedAt timestamp
reset metrics
reset HALF_OPEN permits
log transition
notify listeners
```

---

# 18. Correct Side Effect Pattern

```java
if (state.compareAndSet(
        State.CLOSED,
        State.OPEN)) {

    openedAt = System.currentTimeMillis();

    publishEvent("CLOSED_TO_OPEN");
}
```

---

# 19. Wrong Side Effect Pattern

```java
state.set(State.OPEN);

openedAt = System.currentTimeMillis();

publishEvent("CLOSED_TO_OPEN");
```

Problem:

```text
multiple threads can publish duplicate events
```

---

# 20. Circuit Breaker Valid Transitions

Valid transitions:

```text
CLOSED → OPEN
OPEN → HALF_OPEN
HALF_OPEN → CLOSED
HALF_OPEN → OPEN
```

Invalid transitions:

```text
CLOSED → HALF_OPEN
OPEN → CLOSED
```

usually not allowed directly.

---

# 21. Transition ASCII

```text
CLOSED ----failures----> OPEN
OPEN ----wait elapsed--> HALF_OPEN
HALF_OPEN ----success--> CLOSED
HALF_OPEN ----failure--> OPEN
```

---

# 22. Transition Method Design

Good design:

```text
one method per valid transition
```

Example:

```text
transitionClosedToOpen()
transitionOpenToHalfOpen()
transitionHalfOpenToClosed()
transitionHalfOpenToOpen()
```

---

# 23. Java Transition Engine

```java
import java.util.concurrent.atomic.AtomicReference;

public class AtomicTransitionEngine {

    private final AtomicReference<State> state =
            new AtomicReference<>(State.CLOSED);

    private volatile long openedAtMillis;

    public boolean closedToOpen() {

        boolean changed =
                state.compareAndSet(
                        State.CLOSED,
                        State.OPEN
                );

        if (changed) {

            openedAtMillis =
                    System.currentTimeMillis();

            publishEvent(
                    "CLOSED_TO_OPEN"
            );
        }

        return changed;
    }

    public boolean openToHalfOpen() {

        boolean changed =
                state.compareAndSet(
                        State.OPEN,
                        State.HALF_OPEN
                );

        if (changed) {

            publishEvent(
                    "OPEN_TO_HALF_OPEN"
            );
        }

        return changed;
    }

    public boolean halfOpenToClosed() {

        boolean changed =
                state.compareAndSet(
                        State.HALF_OPEN,
                        State.CLOSED
                );

        if (changed) {

            publishEvent(
                    "HALF_OPEN_TO_CLOSED"
            );
        }

        return changed;
    }

    public boolean halfOpenToOpen() {

        boolean changed =
                state.compareAndSet(
                        State.HALF_OPEN,
                        State.OPEN
                );

        if (changed) {

            openedAtMillis =
                    System.currentTimeMillis();

            publishEvent(
                    "HALF_OPEN_TO_OPEN"
            );
        }

        return changed;
    }

    private void publishEvent(String event) {

        System.out.println(event);
    }
}
```

---

# 24. Why openedAt Is volatile

`openedAtMillis` is read by multiple threads.

`volatile` ensures:

```text
latest value visible across threads
```

But transition itself is protected by CAS.

---

# 25. CAS vs volatile

## volatile

```text
visibility
```

## CAS

```text
atomic conditional update
```

Need both in different places.

---

# 26. CAS Loop

Sometimes update needs retry.

Pattern:

```text
while true:
    old = current
    new = transform(old)
    if CAS(old, new):
        break
```

---

# 27. CAS Loop Example

```java
public void forceOpenFromAnyState() {

    while (true) {

        State current =
                state.get();

        if (current == State.OPEN) {

            return;
        }

        if (state.compareAndSet(
                current,
                State.OPEN)) {

            publishEvent(
                    current + "_TO_OPEN"
            );

            return;
        }
    }
}
```

---

# 28. CAS Loop Dry Run

Thread A reads:

```text
CLOSED
```

Thread B changes:

```text
CLOSED → OPEN
```

Thread A CAS fails.

Thread A rereads:

```text
OPEN
```

Thread A stops.

Correct.

---

# 29. Why CAS Loop Useful

CAS loop handles:

```text
state changed between read and write
```

without locks.

---

# 30. CAS Loop Danger

Bad CAS loops can spin too much under high contention.

Need:

```text
small critical logic
fast retry
sometimes backoff
```

---

# 31. AtomicInteger For Counters

Use AtomicInteger for exact permit counters.

Example:

```java
AtomicInteger halfOpenInFlight =
        new AtomicInteger(0);
```

---

# 32. HALF_OPEN Permit With CAS

```java
public boolean tryAcquireHalfOpenPermit() {

    while (true) {

        int current =
                halfOpenInFlight.get();

        if (current >= maxHalfOpenCalls) {

            return false;
        }

        if (halfOpenInFlight.compareAndSet(
                current,
                current + 1)) {

            return true;
        }
    }
}
```

---

# 33. HALF_OPEN Permit Dry Run

Config:

```text
maxHalfOpenCalls = 5
```

If current:

```text
4
```

CAS:

```text
4 → 5
```

success.

If current:

```text
5
```

reject.

---

# 34. Why Atomic Permit Needed

Without atomic permit:

```text
too many probe requests enter HALF_OPEN
```

Recovering service may crash again.

---

# 35. Release HALF_OPEN Permit

```java
public void releaseHalfOpenPermit() {

    halfOpenInFlight.decrementAndGet();
}
```

Must happen in:

```java
finally
```

---

# 36. Permit Leak Problem

If exception occurs before release:

```text
permit never returned
```

Circuit may get stuck.

---

# 37. Safe Permit Usage

```java
if (!tryAcquireHalfOpenPermit()) {

    throw new RuntimeException(
            "No HALF_OPEN permit"
    );
}

try {

    callRemote();

} finally {

    releaseHalfOpenPermit();
}
```

---

# 38. Atomic State Object

Production systems may store more than enum.

Example state object:

```text
state type
timestamp
metrics snapshot
half-open permits
```

---

# 39. State Object Example

```java
public final class CircuitState {

    private final State state;

    private final long createdAt;

    public CircuitState(
            State state,
            long createdAt) {

        this.state = state;
        this.createdAt = createdAt;
    }

    public State state() {
        return state;
    }

    public long createdAt() {
        return createdAt;
    }
}
```

---

# 40. AtomicReference To State Object

```java
private final AtomicReference<CircuitState>
        current =
        new AtomicReference<>(
                new CircuitState(
                        State.CLOSED,
                        System.currentTimeMillis()
                )
        );
```

---

# 41. Why Immutable State Object

Immutable state object prevents:

```text
partial updates
visibility bugs
inconsistent state
```

Transition replaces whole object atomically.

---

# 42. Immutable State Transition

```java
public boolean toOpen() {

    CircuitState oldState =
            current.get();

    CircuitState newState =
            new CircuitState(
                    State.OPEN,
                    System.currentTimeMillis()
            );

    return current.compareAndSet(
            oldState,
            newState
    );
}
```

---

# 43. Immutable State Mental Model

```text
do not mutate existing state
replace entire state atomically
```

This is safer.

---

# 44. ABA Problem Basics

ABA means:

```text
state was A
changed to B
changed back to A
```

CAS sees:

```text
A
```

and thinks nothing changed.

---

# 45. ABA Example

```text
Thread A reads CLOSED

Thread B:
CLOSED → OPEN → CLOSED

Thread A sees CLOSED and CAS succeeds
```

But state changed in between.

---

# 46. Is ABA Serious Here?

For simple circuit breaker enum:

```text
usually manageable
```

But for advanced lock-free structures:

```text
important problem
```

Can use:

```text
versioned state
AtomicStampedReference
immutable state with timestamp/version
```

---

# 47. Versioned State

Add:

```text
version number
```

to state.

Even if state returns to CLOSED:

```text
version changed
```

---

# 48. Versioned State Example

```java
public final class VersionedState {

    final State state;

    final long version;

    VersionedState(
            State state,
            long version) {

        this.state = state;
        this.version = version;
    }
}
```

---

# 49. Why Version Helps

State:

```text
CLOSED v1
```

Changes:

```text
OPEN v2
CLOSED v3
```

Now old read:

```text
CLOSED v1
```

does not equal current:

```text
CLOSED v3
```

ABA avoided.

---

# 50. Event Publishing Correctness

Only publish transition event when:

```text
CAS succeeds
```

Otherwise duplicate alerts.

---

# 51. Correct Event Rule

```text
CAS success = this thread owns transition
CAS failure = another thread already transitioned
```

---

# 52. Metrics Reset Correctness

When CLOSED → OPEN:

```text
may freeze metrics snapshot
```

When HALF_OPEN → CLOSED:

```text
reset metrics
```

Only winning transition should do reset.

---

# 53. Reset Race Problem

If many threads reset metrics:

```text
some recorded calls lost
```

Need CAS-protected reset.

---

# 54. Production Transition Event

Transition event may contain:

```text
from state
to state
timestamp
failure rate
slow call rate
reason
```

---

# 55. Event Object Example

```java
public class TransitionEvent {

    private final State from;

    private final State to;

    private final long timestamp;

    private final String reason;

    public TransitionEvent(
            State from,
            State to,
            long timestamp,
            String reason) {

        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
        this.reason = reason;
    }
}
```

---

# 56. Lock-Free vs synchronized

## synchronized

```text
simple
safe
blocking
```

## CAS

```text
non-blocking
fast under low contention
harder to reason
```

---

# 57. When synchronized Is Fine

For learning or low traffic:

```text
synchronized is acceptable
```

For high-QPS libraries:

```text
CAS preferred
```

---

# 58. Do Not Hold Lock During Remote Call

Bad:

```java
public synchronized String call() {

    return remoteCall();
}
```

This holds lock during network call.

Very dangerous.

---

# 59. Correct Circuit Flow

```text
read state atomically
      ↓
release any lock
      ↓
perform remote call
      ↓
record result atomically
      ↓
maybe CAS transition
```

---

# 60. Complete CAS-Based Mini Flow

```text
Request arrives
      ↓
AtomicReference reads state
      ↓
If OPEN reject
      ↓
If allowed call remote
      ↓
Atomic metrics update
      ↓
If threshold crossed CAS CLOSED→OPEN
      ↓
Only CAS winner publishes event
```

---

# 61. Observability

Track:

```text
successful transitions
failed CAS attempts
OPEN count
HALF_OPEN permit rejection
duplicate event absence
```

Failed CAS attempts are usually normal under concurrency.

---

# 62. Common Mistakes

## Mistake 1

```text
state.set() instead of compareAndSet()
```

Can overwrite valid transition.

---

## Mistake 2

```text
side effects outside CAS success block
```

Duplicate events.

---

## Mistake 3

```text
mutable state object
```

Visibility/inconsistency bugs.

---

## Mistake 4

```text
no HALF_OPEN atomic permit control
```

Recovery storm.

---

## Mistake 5

```text
holding lock during remote call
```

Latency/deadlock risk.

---

# 63. Most Important Insight

```text
Circuit breaker state transition must be single-winner.
```

CAS gives that.

---

# 64. Distributed Systems Insight

In production, failure and concurrency happen together.

The circuit breaker must be most correct:

```text
exactly when the system is under stress
```

---

# 65. Interview Explanation

If interviewer asks:

```text
Why use CAS in circuit breaker?
```

Strong answer:

```text
CAS ensures only one thread performs a state transition such as CLOSED to
OPEN when multiple threads detect failures concurrently. This prevents
duplicate events, timestamp races, and corrupted transition logic.
```

Senior addition:

```text
A production circuit breaker is a lock-free or low-lock finite state
machine. AtomicReference and compareAndSet are used to ensure valid,
single-winner transitions under high concurrency.
```

---

# 66. Final Mental Model

```text
Expected State?
      ↓
Yes → Atomic Transition
No  → Another Thread Won
```

---

# 67. What To Remember

```text
CAS = compare current with expected, then update.

AtomicReference stores state safely.

compareAndSet prevents duplicate transitions.

Only CAS winner should publish events.

Only CAS winner should update transition timestamps.

Immutable state objects are safer.

ABA can be handled with versioned state.

HALF_OPEN permits need atomic control.

Never hold locks during remote calls.

Circuit breaker is a concurrent state machine.
```

---

# 68. Next File

```text
020_HALF_OPEN_Concurrent_Request_Control.md
```

Next you learn:

```text
HALF_OPEN permit control
probe request limiting
recovery storm prevention
Semaphore vs AtomicInteger
permit leak prevention
safe recovery testing
production HALF_OPEN design
```
