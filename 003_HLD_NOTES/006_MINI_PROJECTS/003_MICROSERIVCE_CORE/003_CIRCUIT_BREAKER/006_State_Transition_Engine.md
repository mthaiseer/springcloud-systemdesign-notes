# 006_State_Transition_Engine.md

# MiniCircuitBreaker — 006 State Transition Engine

---

# 1. Why This File Exists

Previous files explained:

```text
CLOSED
OPEN
HALF_OPEN
```

But now important question:

```text
Who controls transitions between states?
```

Need:

```text
state transition engine
```

This engine is the brain of circuit breaker.

It decides:

```text
when CLOSED → OPEN
when OPEN → HALF_OPEN
when HALF_OPEN → CLOSED
when HALF_OPEN → OPEN
```

This file explains:

```text
state machine internals
transition rules
event-driven transitions
lock-free transitions
AtomicReference
CAS operations
thread safety
production state management
Resilience4j internals
```

---

# 2. One-Line Definition

```text
State transition engine controls how a circuit breaker moves between states.
```

---

# 3. Biggest Mental Model

```text
Requests + Metrics
        ↓
Transition Engine
        ↓
State Decision
        ↓
CLOSED / OPEN / HALF_OPEN
```

---

# 4. Why Transition Engine Important

Without transition engine:

```text
state changes become inconsistent
```

In distributed systems:

```text
multiple threads may update state simultaneously
```

Need:

```text
safe centralized transition logic
```

---

# 5. State Machine Mental Model

Circuit breaker internally behaves like:

```text
finite state machine (FSM)
```

Only valid transitions allowed.

---

# 6. State Machine ASCII

```text
           failures threshold reached
CLOSED  ----------------------------> OPEN
   ^                                    |
   |                                    |
   |                                    | wait duration elapsed
   |                                    v
   +----------- HALF_OPEN <-------------+
                 |
                 |
                 +---- success → CLOSED
                 |
                 +---- failure → OPEN
```

---

# 7. Core Transition Rules

```text
CLOSED → OPEN
OPEN → HALF_OPEN
HALF_OPEN → CLOSED
HALF_OPEN → OPEN
```

These are valid transitions.

---

# 8. Invalid Transitions

Examples:

```text
OPEN → CLOSED directly
CLOSED → HALF_OPEN directly
```

Usually invalid.

Need controlled transition flow.

---

# 9. Why Controlled Transitions Matter

Without rules:

```text
race conditions
invalid states
broken recovery logic
```

may occur.

---

# 10. Event-Driven State Machine

Transitions triggered by:

```text
failure rate
slow call rate
timeout
cooldown expiration
probe success
probe failure
```

---

# 11. Transition Events ASCII

```text
Failures
   ↓
Transition Engine
   ↓
OPEN
```

---

# 12. CLOSED → OPEN Transition

Triggered when:

```text
failure threshold exceeded
```

or:

```text
slow call threshold exceeded
```

---

# 13. CLOSED → OPEN Example

Config:

```text
minimum calls = 10
failure threshold = 50%
```

Calls:

```text
6 failures out of 10
```

Result:

```text
OPEN
```

---

# 14. OPEN → HALF_OPEN Transition

Triggered when:

```text
wait duration elapsed
```

Example:

```text
OPEN for 30 seconds
```

Then:

```text
HALF_OPEN
```

---

# 15. HALF_OPEN → CLOSED Transition

Triggered when:

```text
probe requests successful
```

Example:

```text
5 successful probe calls
```

Result:

```text
CLOSED
```

---

# 16. HALF_OPEN → OPEN Transition

Triggered when:

```text
probe request fails
```

Service still unhealthy.

Result:

```text
OPEN again
```

---

# 17. Transition Engine Responsibilities

Transition engine handles:

```text
state validation
state changes
timestamps
metrics reset
probe counters
thread safety
```

---

# 18. Internal Transition Flow

```text
Event Occurs
      ↓
Validate Transition
      ↓
Update State
      ↓
Update Metrics
      ↓
Publish Events
```

---

# 19. Why Thread Safety Critical

Multiple requests may:

```text
fail simultaneously
```

Without thread safety:

```text
multiple inconsistent transitions
```

may happen.

---

# 20. Concurrency Problem Example

Two threads detect threshold exceeded simultaneously.

Without protection:

```text
duplicate transitions
race conditions
incorrect metrics
```

---

# 21. Race Condition ASCII

```text
Thread A → OPEN
Thread B → OPEN
Thread C → HALF_OPEN
```

Potential inconsistency.

---

# 22. Production Goal

Need:

```text
single authoritative state transition
```

even under high concurrency.

---

# 23. AtomicReference Mental Model

Modern circuit breakers use:

```text
AtomicReference<State>
```

to store current state safely.

---

# 24. AtomicReference ASCII

```text
AtomicReference
        ↓
Current State
```

Updates happen atomically.

---

# 25. CAS Operation

CAS means:

```text
Compare And Set
```

Core lock-free concurrency primitive.

---

# 26. CAS Mental Model

```text
if current state == expected:
    update state
else:
    retry/fail
```

---

# 27. CAS ASCII

```text
Expected: CLOSED
Current:  CLOSED
      ↓
Update To OPEN
```

Atomic operation.

---

# 28. Why CAS Powerful

Avoids heavy locks.

Provides:

```text
high concurrency
low latency
thread safety
```

---

# 29. Lock-Free Transition Engine

Modern Resilience4j-style engines use:

```text
lock-free state transitions
```

using:

```text
AtomicReference
CAS
```

instead of synchronized everywhere.

---

# 30. Why Lock-Free Better

Locks can cause:

```text
contention
thread blocking
latency spikes
```

CAS much faster.

---

# 31. Transition Timestamp

Engine tracks:

```text
when state changed
```

Needed for:

```text
OPEN cooldown timing
```

---

# 32. Timestamp ASCII

```text
state = OPEN
openedAt = 10:00:00
```

After 30 seconds:

```text
HALF_OPEN allowed
```

---

# 33. Transition Metadata

State transition often stores:

```text
current state
previous state
timestamp
failure rate
event reason
```

---

# 34. Transition Metadata ASCII

```text
Transition
 ├── fromState
 ├── toState
 ├── timestamp
 ├── reason
 └── metricsSnapshot
```

---

# 35. Transition Events

Production systems publish:

```text
state transition events
```

Example:

```text
CLOSED_TO_OPEN
OPEN_TO_HALF_OPEN
HALF_OPEN_TO_CLOSED
```

---

# 36. Why Events Important

Useful for:

```text
monitoring
alerting
tracing
debugging
metrics
```

---

# 37. Event Stream ASCII

```text
State Change
      ↓
Event Published
      ↓
Metrics/Logs/Alerts
```

---

# 38. Resilience4j Transition Events

Resilience4j internally publishes:

```text
CircuitBreakerOnStateTransitionEvent
```

Very important observability feature.

---

# 39. Java State Enum

```java
public enum CircuitBreakerState {

    CLOSED,
    OPEN,
    HALF_OPEN
}
```

---

# 40. Java AtomicReference Example

```java
import java.util.concurrent.atomic.AtomicReference;

public class CircuitBreakerStateMachine {

    private final AtomicReference<CircuitBreakerState>
            state =
            new AtomicReference<>(
                    CircuitBreakerState.CLOSED
            );
}
```

---

# 41. Java CLOSED → OPEN CAS

```java
public boolean transitionToOpen() {

    return state.compareAndSet(
            CircuitBreakerState.CLOSED,
            CircuitBreakerState.OPEN
    );
}
```

---

# 42. CAS Dry Run

Suppose:

```text
Current State = CLOSED
```

Thread A:

```text
compareAndSet(CLOSED, OPEN)
```

Success:

```text
State becomes OPEN
```

Thread B simultaneously:

```text
compareAndSet(CLOSED, OPEN)
```

Fails because state already OPEN.

This prevents duplicate transitions.

---

# 43. Why CAS Prevents Races

Only one thread wins transition.

Other threads:

```text
see updated state
```

Very important concurrency guarantee.

---

# 44. Java OPEN → HALF_OPEN

```java
public boolean transitionToHalfOpen() {

    return state.compareAndSet(
            CircuitBreakerState.OPEN,
            CircuitBreakerState.HALF_OPEN
    );
}
```

---

# 45. Java HALF_OPEN → CLOSED

```java
public boolean transitionToClosed() {

    return state.compareAndSet(
            CircuitBreakerState.HALF_OPEN,
            CircuitBreakerState.CLOSED
    );
}
```

---

# 46. Java HALF_OPEN → OPEN

```java
public boolean transitionBackToOpen() {

    return state.compareAndSet(
            CircuitBreakerState.HALF_OPEN,
            CircuitBreakerState.OPEN
    );
}
```

---

# 47. Full Transition Engine Example

```java
import java.util.concurrent.atomic.AtomicReference;

public class StateTransitionEngine {

    private final AtomicReference<CircuitBreakerState>
            state =
            new AtomicReference<>(
                    CircuitBreakerState.CLOSED
            );

    public boolean toOpen() {

        return state.compareAndSet(
                CircuitBreakerState.CLOSED,
                CircuitBreakerState.OPEN
        );
    }

    public boolean toHalfOpen() {

        return state.compareAndSet(
                CircuitBreakerState.OPEN,
                CircuitBreakerState.HALF_OPEN
        );
    }

    public boolean toClosed() {

        return state.compareAndSet(
                CircuitBreakerState.HALF_OPEN,
                CircuitBreakerState.CLOSED
        );
    }

    public CircuitBreakerState getState() {

        return state.get();
    }
}
```

---

# 48. Dry Run — Failure Path

Initial:

```text
CLOSED
```

Failures exceed threshold.

Thread A:

```text
compareAndSet(CLOSED, OPEN)
```

Success.

Now:

```text
OPEN
```

Other threads cannot reopen again.

---

# 49. Dry Run — Recovery Path

OPEN cooldown expires.

Transition:

```text
OPEN → HALF_OPEN
```

Probe requests succeed.

Transition:

```text
HALF_OPEN → CLOSED
```

Normal traffic resumes.

---

# 50. Transition Engine Performance Goal

State transitions must be:

```text
fast
lock-free
low contention
thread-safe
```

because circuit breakers run on:

```text
every request
```

---

# 51. Production Metrics

Transition engine tracks:

```text
transition count
OPEN frequency
HALF_OPEN failures
recovery success rate
```

---

# 52. Production Observability

Important dashboards:

```text
CLOSED → OPEN spikes
OPEN duration
recovery time
HALF_OPEN failures
```

using:

```text
Micrometer
Prometheus
Grafana
```

---

# 53. Common Mistakes

## Mistake 1

```text
Using normal variable for state
```

Not thread-safe.

---

## Mistake 2

```text
Using heavy synchronized everywhere
```

High contention.

---

## Mistake 3

```text
Allowing invalid transitions
```

Breaks state machine.

---

## Mistake 4

```text
No transition events
```

Hard to debug outages.

---

## Mistake 5

```text
No timestamp tracking
```

OPEN cooldown impossible.

---

# 54. Most Important Insight

```text
State transition engine is the brain of the circuit breaker.
```

Everything revolves around:

```text
safe state transitions
```

---

# 55. Distributed Systems Insight

At scale:

```text
concurrency correctness
is more important than syntax
```

Lock-free transitions are production critical.

---

# 56. Interview Explanation

If interviewer asks:

```text
How are circuit breaker state transitions implemented?
```

Strong answer:

```text
Circuit breakers are implemented as finite state machines with controlled
transitions between CLOSED, OPEN, and HALF_OPEN states. Modern
implementations use AtomicReference and CAS operations to ensure
thread-safe lock-free state transitions under concurrency.
```

Senior addition:

```text
Transition engines also publish state-change events for observability,
metrics, and alerting.
```

---

# 57. Final Mental Model

```text
Metrics + Events
        ↓
Transition Engine
        ↓
Safe State Change
```

---

# 58. What To Remember

```text
Circuit breaker is a finite state machine.

Transition engine controls all state changes.

Valid transitions only.

AtomicReference stores current state.

CAS provides lock-free thread-safe transitions.

Transition events useful for observability.

State transitions must be lightweight.

Transition engine is the brain of circuit breaker.
```

---

# 59. Complete State Machine Flow

```text
Healthy Service
      ↓
CLOSED
      ↓
Failures exceed threshold
      ↓
OPEN
      ↓
Cooldown expires
      ↓
HALF_OPEN
      ↓
Probe requests
      ↓
Healthy?
   yes/no
```

---

# 60. Next File

```text
007_Failure_Threshold.md
```

Next you learn:

```text
failure percentage calculation
minimum calls
threshold tuning
slow call thresholds
production threshold strategy
false positive prevention
```
