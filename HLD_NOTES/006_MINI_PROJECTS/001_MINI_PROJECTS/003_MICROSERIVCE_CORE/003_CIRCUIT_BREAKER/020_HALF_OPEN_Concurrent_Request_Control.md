# 020_HALF_OPEN_Concurrent_Request_Control.md

# MiniCircuitBreaker — 020 HALF_OPEN Concurrent Request Control

---

# 1. Why This File Exists

Previous files explained:

```text
CLOSED state
OPEN state
HALF_OPEN state
State transition engine
Thread safety
AtomicReference
CAS
```

Now we focus on one of the most delicate circuit breaker problems:

```text
HALF_OPEN concurrent request control
```

HALF_OPEN means:

```text
the dependency may have recovered
```

But we are not sure yet.

So the circuit breaker must allow:

```text
only a small number of probe requests
```

If too many requests enter HALF_OPEN at once:

```text
recovering service gets overloaded again
```

This file explains:

```text
HALF_OPEN probe requests
concurrent request limits
recovery storm
permit control
Semaphore approach
AtomicInteger CAS approach
permit leakage
probe success/failure logic
HALF_OPEN timeout
Resilience4j config
Java implementation
production tuning
```

---

# 2. One-Line Definition

```text
HALF_OPEN concurrent request control limits how many test requests can hit a recovering dependency.
```

---

# 3. Biggest Mental Model

```text
OPEN blocked traffic
      ↓
Cooldown ended
      ↓
HALF_OPEN allows only a few probes
      ↓
Success → CLOSED
Failure → OPEN
```

---

# 4. Why HALF_OPEN Needs Control

When circuit is OPEN:

```text
requests are rejected
```

After wait duration:

```text
circuit moves to HALF_OPEN
```

If full traffic resumes immediately:

```text
dependency may crash again
```

HALF_OPEN prevents this by allowing limited probes.

---

# 5. Recovery Storm Problem

Suppose:

```text
circuit OPEN for 30 seconds
```

During that time:

```text
10,000 requests waiting/arriving
```

When circuit becomes HALF_OPEN, if all are allowed:

```text
10,000 requests hit recovering service
```

This is:

```text
recovery storm
```

---

# 6. Recovery Storm ASCII

```text
OPEN
 ↓ wait 30s
HALF_OPEN
 ↓
10,000 requests flood service
 ↓
service crashes again
```

---

# 7. Correct HALF_OPEN Behavior

Allow only:

```text
N probe requests
```

Example:

```text
permittedNumberOfCallsInHalfOpenState = 5
```

All extra requests are:

```text
rejected
or
fallback returned
```

---

# 8. Controlled Recovery ASCII

```text
HALF_OPEN
   ↓
Allow 5 Probe Requests
   ↓
Observe Results
   ↓
Healthy?
```

---

# 9. HALF_OPEN Is Not Normal State

HALF_OPEN is:

```text
testing state
```

NOT:

```text
fully recovered state
```

Full traffic resumes only after:

```text
probe success criteria met
```

---

# 10. State Comparison

```text
CLOSED:
all traffic allowed

OPEN:
no traffic allowed

HALF_OPEN:
limited probe traffic allowed
```

---

# 11. Probe Request

A probe request is:

```text
real request allowed through to test dependency health
```

It is not fake.

It hits the actual dependency.

---

# 12. Probe Request ASCII

```text
Request
   ↓
HALF_OPEN Permit?
   ↓
Allowed
   ↓
Remote Dependency
   ↓
Record Result
```

---

# 13. Permit Concept

HALF_OPEN uses permits.

Permit means:

```text
permission to execute one probe request
```

If no permit:

```text
request rejected
```

---

# 14. Permit ASCII

```text
Permits = 3

[P][P][P]

R1 uses permit
R2 uses permit
R3 uses permit
R4 rejected
```

---

# 15. Why Extra Requests Rejected

Extra requests are rejected to avoid:

```text
overloading recovering dependency
breaking recovery test
creating traffic spike
```

---

# 16. Success Path

If probe requests succeed:

```text
HALF_OPEN → CLOSED
```

Traffic resumes normally.

---

# 17. Success ASCII

```text
HALF_OPEN
   ↓
5 successful probes
   ↓
CLOSED
```

---

# 18. Failure Path

If probe request fails:

```text
HALF_OPEN → OPEN
```

Dependency still unhealthy.

---

# 19. Failure ASCII

```text
HALF_OPEN
   ↓
probe fails
   ↓
OPEN
```

---

# 20. Immediate Failure Strategy

Many systems reopen circuit on:

```text
first HALF_OPEN failure
```

because failure indicates dependency not recovered.

---

# 21. Threshold Strategy

Some systems require:

```text
failure rate among probes
```

Example:

```text
3 failures out of 5 probes
```

before reopening.

Simpler strategy:

```text
any failure → OPEN
```

---

# 22. Resilience4j Config

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        permittedNumberOfCallsInHalfOpenState: 5
        waitDurationInOpenState: 30s
```

Meaning:

```text
after 30s OPEN duration,
allow 5 HALF_OPEN probe calls
```

---

# 23. maxWaitDurationInHalfOpenState

HALF_OPEN should not stay forever.

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        maxWaitDurationInHalfOpenState: 10s
```

Meaning:

```text
HALF_OPEN must resolve within 10 seconds
```

---

# 24. Why Max Wait Needed

Probe calls may hang.

Without max wait:

```text
circuit stuck HALF_OPEN
```

Need:

```text
timeout + max wait
```

---

# 25. HALF_OPEN Internal Data

Circuit breaker needs:

```text
state = HALF_OPEN
permitted calls
in-flight probes
successful probes
failed probes
max wait duration
enteredHalfOpenAt timestamp
```

---

# 26. Internal Data ASCII

```text
HalfOpenState
 ├── maxPermits
 ├── inFlight
 ├── successCount
 ├── failureCount
 ├── enteredAt
 └── maxWaitDuration
```

---

# 27. Semaphore Approach

Simplest way:

```text
Semaphore permits
```

Each probe request must acquire permit.

---

# 28. Semaphore Java Example

```java
import java.util.concurrent.Semaphore;

public class HalfOpenSemaphoreControl {

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

# 29. Semaphore Dry Run

Config:

```text
permits = 5
```

Requests:

```text
R1 R2 R3 R4 R5
```

Allowed.

Request:

```text
R6
```

Rejected.

---

# 30. Permit Release Problem

If permit released after probe completes:

```text
more than 5 total probes may happen over time
```

Depending on design, this may be okay or not.

Some circuit breakers limit:

```text
total permitted calls
```

not just:

```text
concurrent permitted calls
```

---

# 31. Concurrent vs Total Probe Limit

## Concurrent Limit

```text
max 5 in-flight probes at once
```

## Total Probe Limit

```text
only 5 probes total in HALF_OPEN
```

Important difference.

---

# 32. Production HALF_OPEN Usually Needs Total Probe Limit

For circuit breaker recovery, usually you want:

```text
limited number of test calls total
```

Example:

```text
permit 5 test calls
then decide
```

---

# 33. AtomicInteger Total Permit Approach

Use:

```text
AtomicInteger remainingPermits
```

Each probe consumes one permanently for this HALF_OPEN cycle.

---

# 34. AtomicInteger Permit Code

```java
import java.util.concurrent.atomic.AtomicInteger;

public class HalfOpenPermitCounter {

    private final AtomicInteger remainingPermits =
            new AtomicInteger(5);

    public boolean tryAcquireProbe() {

        while (true) {

            int current =
                    remainingPermits.get();

            if (current <= 0) {

                return false;
            }

            if (remainingPermits.compareAndSet(
                    current,
                    current - 1)) {

                return true;
            }
        }
    }
}
```

---

# 35. Atomic Permit Dry Run

Initial:

```text
remainingPermits = 5
```

Five threads acquire:

```text
5 → 4 → 3 → 2 → 1 → 0
```

Sixth thread:

```text
rejected
```

---

# 36. Why CAS Needed

Without CAS:

```text
many threads can read remainingPermits = 1
```

and all enter.

CAS ensures:

```text
only one thread decrements from 1 to 0
```

---

# 37. Permit Race ASCII

Without CAS:

```text
remaining = 1

Thread A reads 1
Thread B reads 1

Both enter
```

With CAS:

```text
Thread A wins
Thread B fails
```

---

# 38. Success Counter

HALF_OPEN tracks:

```text
successful probe calls
```

If enough successes:

```text
transition to CLOSED
```

---

# 39. Failure Counter

HALF_OPEN tracks:

```text
failed probe calls
```

If failure occurs:

```text
transition to OPEN
```

---

# 40. Atomic Probe Metrics

```java
import java.util.concurrent.atomic.AtomicInteger;

public class HalfOpenMetrics {

    private final AtomicInteger successes =
            new AtomicInteger();

    private final AtomicInteger failures =
            new AtomicInteger();

    public int recordSuccess() {

        return successes.incrementAndGet();
    }

    public int recordFailure() {

        return failures.incrementAndGet();
    }
}
```

---

# 41. HALF_OPEN Decision Logic

```text
if any probe fails:
    HALF_OPEN → OPEN

if successCount == permittedCalls:
    HALF_OPEN → CLOSED
```

---

# 42. Java HALF_OPEN Controller

```java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class HalfOpenController {

    private final int permittedCalls = 5;

    private final AtomicInteger remainingPermits =
            new AtomicInteger(permittedCalls);

    private final AtomicInteger successes =
            new AtomicInteger();

    private final AtomicReference<State> state;

    public HalfOpenController(
            AtomicReference<State> state) {

        this.state = state;
    }

    public boolean tryAcquireProbe() {

        while (true) {

            int current =
                    remainingPermits.get();

            if (current <= 0) {
                return false;
            }

            if (remainingPermits.compareAndSet(
                    current,
                    current - 1)) {

                return true;
            }
        }
    }

    public void recordSuccess() {

        int successCount =
                successes.incrementAndGet();

        if (successCount >= permittedCalls) {

            state.compareAndSet(
                    State.HALF_OPEN,
                    State.CLOSED
            );
        }
    }

    public void recordFailure() {

        state.compareAndSet(
                State.HALF_OPEN,
                State.OPEN
        );
    }
}
```

---

# 43. State Enum

```java
public enum State {

    CLOSED,
    OPEN,
    HALF_OPEN
}
```

---

# 44. Dry Run — All Probes Success

Config:

```text
permittedCalls = 3
```

Flow:

```text
R1 success → successCount = 1
R2 success → successCount = 2
R3 success → successCount = 3
```

Transition:

```text
HALF_OPEN → CLOSED
```

---

# 45. Dry Run — One Probe Fails

Config:

```text
permittedCalls = 3
```

Flow:

```text
R1 success
R2 failure
```

Transition:

```text
HALF_OPEN → OPEN
```

R3 not needed.

---

# 46. Race: Success And Failure Same Time

One thread records success.

Another records failure.

Need CAS transitions.

If failure wins:

```text
HALF_OPEN → OPEN
```

If success closes first:

```text
HALF_OPEN → CLOSED
```

Design must define behavior.

Many systems prefer:

```text
failure should reopen
```

while still in HALF_OPEN.

---

# 47. CAS Transition Protection

```java
state.compareAndSet(
        State.HALF_OPEN,
        State.OPEN
);
```

ensures transition valid only if still HALF_OPEN.

---

# 48. Reset Permits On Enter HALF_OPEN

Every time circuit enters HALF_OPEN:

```text
reset remaining permits
reset success count
reset failure count
set enteredAt timestamp
```

---

# 49. Reset Code

```java
public void resetForHalfOpen() {

    remainingPermits.set(permittedCalls);
    successes.set(0);
    failures.set(0);
    enteredAtMillis =
            System.currentTimeMillis();
}
```

---

# 50. Why Reset Important

Without reset:

```text
old permits/successes leak into new recovery cycle
```

Circuit behaves incorrectly.

---

# 51. HALF_OPEN Timeout

If probes hang:

```text
HALF_OPEN may never resolve
```

Need timeout.

---

# 52. HALF_OPEN Max Wait Logic

```text
if now - enteredHalfOpenAt > maxWait:
    transition HALF_OPEN → OPEN
```

---

# 53. Max Wait Java Example

```java
public boolean halfOpenExpired(
        long now,
        long enteredAt,
        long maxWaitMs) {

    return now - enteredAt > maxWaitMs;
}
```

---

# 54. CallNotPermitted In HALF_OPEN

If no permit available:

```text
reject request
```

Usually similar to:

```text
CallNotPermittedException
```

---

# 55. Fallback In HALF_OPEN

Rejected HALF_OPEN requests can return:

```text
fallback
cached response
try later message
```

---

# 56. Do Not Queue HALF_OPEN Requests

Queuing many HALF_OPEN requests defeats the purpose.

Better:

```text
reject fast
```

---

# 57. Why No Queue

Queue creates:

```text
traffic burst after permits free
```

HALF_OPEN should test carefully.

---

# 58. Production Metrics

Monitor:

```text
HALF_OPEN entries
probe calls allowed
probe calls rejected
probe success count
probe failure count
HALF_OPEN duration
HALF_OPEN → CLOSED count
HALF_OPEN → OPEN count
```

---

# 59. Observability Value

If circuit keeps going:

```text
OPEN → HALF_OPEN → OPEN
```

Dependency still unhealthy.

This should trigger alert/investigation.

---

# 60. Common Mistakes

## Mistake 1

```text
Allowing unlimited HALF_OPEN requests
```

Recovery storm.

---

## Mistake 2

```text
Only limiting concurrent probes, not total probes
```

May allow too many test calls over time.

---

## Mistake 3

```text
No permit reset
```

Broken recovery cycles.

---

## Mistake 4

```text
No max wait duration
```

HALF_OPEN stuck forever.

---

## Mistake 5

```text
No CAS transition
```

Race between success/failure decisions.

---

# 61. Most Important Insight

```text
HALF_OPEN protects the recovery phase.
```

OPEN protects failure phase.

CLOSED protects normal phase through monitoring.

---

# 62. Distributed Systems Insight

Recovery is dangerous.

A system can fail again because:

```text
too much traffic returns too quickly
```

HALF_OPEN prevents recovery overload.

---

# 63. Interview Explanation

If interviewer asks:

```text
How does HALF_OPEN control concurrent requests?
```

Strong answer:

```text
HALF_OPEN allows only a limited number of probe requests to test whether
the dependency has recovered. Extra requests are rejected or fallback is
returned. The circuit closes after enough successful probes and reopens
on failures.
```

Senior addition:

```text
Production implementations use atomic permit counters, Semaphore, or CAS
to avoid recovery storms and ensure only valid HALF_OPEN transitions occur
under concurrency.
```

---

# 64. Final Mental Model

```text
OPEN:
stop traffic

HALF_OPEN:
test with few requests

CLOSED:
resume normal traffic
```

---

# 65. What To Remember

```text
HALF_OPEN is controlled recovery testing.

Do not allow full traffic immediately.

Use permits for probe requests.

AtomicInteger/CAS prevents permit races.

Semaphore controls concurrent probes.

Total probe limit controls recovery decision.

Failure usually reopens circuit.

Success threshold closes circuit.

Reset permits on every HALF_OPEN entry.

Use max wait duration to avoid stuck HALF_OPEN.

Monitor HALF_OPEN transitions carefully.
```

---

# 66. Next File

```text
021_CircuitBreaker_With_RestClient_WebClient.md
```

Next you learn:

```text
Spring RestClient integration
RestTemplate/WebClient patterns
blocking vs reactive calls
timeouts
fallback
Resilience4j annotations
production HTTP client protection
```
