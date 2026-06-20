# 005_HALF_OPEN_State.md

# MiniCircuitBreaker — 005 HALF_OPEN State

---

# 1. Why This File Exists

OPEN state blocks requests to unhealthy services.

But eventually service may recover.

Question:

```text
How does circuit breaker know service healthy again?
```

It cannot stay OPEN forever.

Need:

```text
controlled recovery testing
```

This is the purpose of:

```text
HALF_OPEN state
```

HALF_OPEN is one of the MOST important distributed resiliency concepts.

This file explains:

```text
why HALF_OPEN exists
probe requests
controlled recovery
success/failure testing
concurrency limits
HALF_OPEN → CLOSED
HALF_OPEN → OPEN
Resilience4j internals
```

---

# 2. One-Line Definition

```text
HALF_OPEN state allows limited test requests to check whether a service has recovered.
```

---

# 3. Biggest Mental Model

```text
Service Failed
      ↓
OPEN blocks traffic
      ↓
Wait period ends
      ↓
HALF_OPEN allows few test requests
      ↓
Recovered?
   yes/no
```

---

# 4. Why HALF_OPEN Needed

Suppose service outage temporary.

If circuit stays OPEN forever:

```text
service may recover
but traffic never resumes
```

Need safe recovery testing.

---

# 5. HALF_OPEN State Meaning

HALF_OPEN means:

```text
service health uncertain
```

So:

```text
allow SMALL amount of traffic
```

NOT full traffic.

---

# 6. HALF_OPEN ASCII

```text
OPEN
  ↓
Cooldown Ends
  ↓
HALF_OPEN
  ↓
Few Probe Requests
```

---

# 7. Why Limited Requests Important

Suppose service JUST recovered.

Sending full traffic immediately may:

```text
overload service again
```

HALF_OPEN prevents traffic spikes.

---

# 8. Traffic Spike Problem

Without HALF_OPEN:

```text
OPEN
 ↓
Service recovers
 ↓
10,000 requests instantly hit service
 ↓
Service crashes again
```

---

# 9. HALF_OPEN Protection

HALF_OPEN acts like:

```text
careful recovery ramp-up
```

instead of:

```text
full traffic flood
```

---

# 10. Probe Request Concept

HALF_OPEN allows:

```text
few test/probe requests
```

These requests determine:

```text
is service healthy now?
```

---

# 11. Probe Request ASCII

```text
HALF_OPEN
    ↓
Allow 3 Requests
    ↓
Observe Results
```

---

# 12. HALF_OPEN Success Case

If probe requests succeed:

```text
service considered healthy again
```

Transition:

```text
HALF_OPEN → CLOSED
```

Traffic resumes normally.

---

# 13. HALF_OPEN Success ASCII

```text
HALF_OPEN
    ↓
Probe Requests Success
    ↓
CLOSED
```

---

# 14. HALF_OPEN Failure Case

If probe requests fail:

```text
service still unhealthy
```

Transition:

```text
HALF_OPEN → OPEN
```

Traffic blocked again.

---

# 15. HALF_OPEN Failure ASCII

```text
HALF_OPEN
    ↓
Probe Request Fails
    ↓
OPEN
```

---

# 16. Full State Machine

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

# 17. Why HALF_OPEN Is Delicate

HALF_OPEN is dangerous because:

```text
too many probe requests
can overload recovering service
```

Need strict limits.

---

# 18. HALF_OPEN Concurrency Limit

Usually HALF_OPEN permits:

```text
very small number of requests
```

Example:

```text
3
5
10
```

NOT thousands.

---

# 19. HALF_OPEN Concurrency ASCII

```text
HALF_OPEN
   ↓
Permit Only 5 Calls
```

Additional requests:

```text
rejected
```

---

# 20. Why Concurrent Limits Important

Without limits:

```text
many threads may simultaneously probe
```

Service recovery testing becomes unreliable.

---

# 21. HALF_OPEN Request Flow

```text
Request Arrives
      ↓
Circuit = HALF_OPEN
      ↓
Probe Slot Available?
       ↓
    yes/no
```

---

# 22. HALF_OPEN Success Logic

Suppose:

```text
allowed probe calls = 5
```

If:

```text
all 5 succeed
```

Transition:

```text
CLOSED
```

---

# 23. HALF_OPEN Failure Logic

Suppose:

```text
1 probe request fails
```

Often circuit immediately:

```text
returns OPEN
```

---

# 24. Why Failure Quickly Reopens

Failure during HALF_OPEN means:

```text
service still unstable
```

Better to protect system again.

---

# 25. HALF_OPEN Timing

Typical flow:

```text
OPEN
  ↓
wait 30s
  ↓
HALF_OPEN
  ↓
allow 5 calls
  ↓
success/failure decision
```

---

# 26. HALF_OPEN Internal Data

HALF_OPEN tracks:

```text
probe request count
probe successes
probe failures
concurrency limit
```

---

# 27. Internal Data ASCII

```text
CircuitBreaker
 ├── state = HALF_OPEN
 ├── permittedCalls = 5
 ├── successfulCalls
 ├── failedCalls
 └── currentProbeCount
```

---

# 28. HALF_OPEN Is Recovery Gate

Mental model:

```text
CLOSED = healthy
OPEN = unhealthy
HALF_OPEN = recovery testing
```

---

# 29. Why HALF_OPEN Better Than Immediate CLOSED

Immediate reopening dangerous.

HALF_OPEN allows:

```text
controlled confidence building
```

before full traffic resumes.

---

# 30. Real Production Example

Example:

```text
Payment service outage
```

OPEN blocks requests.

After 30 seconds:

```text
HALF_OPEN allows 3 payment attempts
```

If successful:

```text
payment traffic restored
```

---

# 31. Kafka Consumer Example

External API recovered.

HALF_OPEN allows:

```text
few Kafka messages
```

instead of entire backlog flood.

Very important.

---

# 32. Database Recovery Example

Database overloaded.

OPEN protects DB.

HALF_OPEN carefully tests DB recovery.

Avoids:

```text
immediate traffic spikes
```

---

# 33. HALF_OPEN vs OPEN

OPEN:

```text
zero traffic
```

HALF_OPEN:

```text
limited traffic
```

---

# 34. HALF_OPEN vs CLOSED

CLOSED:

```text
all requests allowed
```

HALF_OPEN:

```text
few requests allowed
```

---

# 35. HALF_OPEN Resource Protection

HALF_OPEN protects:

```text
recovering services
thread pools
DB pools
network resources
```

during recovery phase.

---

# 36. Resilience4j HALF_OPEN Config

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        permittedNumberOfCallsInHalfOpenState: 5
```

Meaning:

```text
allow 5 probe requests
```

---

# 37. Important HALF_OPEN Configs

Common configs:

```text
waitDurationInOpenState
permittedNumberOfCallsInHalfOpenState
maxWaitDurationInHalfOpenState
```

---

# 38. maxWaitDurationInHalfOpenState

Limits how long HALF_OPEN can remain active.

Prevents:

```text
stuck HALF_OPEN state
```

---

# 39. HALF_OPEN Timeout Problem

Suppose probe requests hang forever.

HALF_OPEN never resolves.

Need:

```text
timeouts
max wait duration
```

---

# 40. HALF_OPEN Decision Algorithm

Pseudo logic:

```text
if all probes succeed:
    CLOSED

if any probe fails:
    OPEN
```

---

# 41. Java Enum

```java
public enum CircuitBreakerState {

    CLOSED,
    OPEN,
    HALF_OPEN
}
```

---

# 42. Java HALF_OPEN Example

```java
public class HalfOpenStateHandler {

    private int permittedCalls = 5;

    private int successCount = 0;

    private int failureCount = 0;

    public CircuitBreakerState recordSuccess() {

        successCount++;

        if (successCount >= permittedCalls) {

            return CircuitBreakerState.CLOSED;
        }

        return CircuitBreakerState.HALF_OPEN;
    }

    public CircuitBreakerState recordFailure() {

        failureCount++;

        return CircuitBreakerState.OPEN;
    }
}
```

---

# 43. Java Dry Run — Success Path

Config:

```text
permittedCalls = 3
```

Calls:

```text
success
success
success
```

Execution:

```text
successCount=1
successCount=2
successCount=3
```

Transition:

```text
HALF_OPEN → CLOSED
```

---

# 44. Java Dry Run — Failure Path

Calls:

```text
success
failure
```

Execution:

```text
successCount=1
failureCount=1
```

Transition:

```text
HALF_OPEN → OPEN
```

---

# 45. HALF_OPEN Concurrency Problem

Multiple threads may simultaneously enter HALF_OPEN.

Need:

```text
atomic counters
CAS
thread-safe permits
```

---

# 46. Production Concurrency Control

Real systems use:

```text
Semaphore
AtomicInteger
LongAdder
CAS operations
```

to control HALF_OPEN requests safely.

---

# 47. Semaphore Mental Model

HALF_OPEN often behaves like:

```text
small semaphore gate
```

Example:

```text
5 permits only
```

---

# 48. Semaphore ASCII

```text
HALF_OPEN
    ↓
[ Permit Permit Permit ]
```

No permits:

```text
reject request
```

---

# 49. Why HALF_OPEN Important In Distributed Systems

Without HALF_OPEN:

```text
recovery traffic spikes
```

can cause repeated outages.

HALF_OPEN enables:

```text
safe gradual recovery
```

---

# 50. Production Metrics

Important HALF_OPEN metrics:

```text
probe request count
probe success rate
probe failure rate
HALF_OPEN duration
transition count
```

---

# 51. Observability

Production monitoring:

```text
OPEN → HALF_OPEN transitions
HALF_OPEN failures
recovery success rate
probe latency
```

using:

```text
Prometheus
Grafana
Micrometer
alerts
```

---

# 52. Common Mistakes

## Mistake 1

```text
Allowing too many HALF_OPEN requests
```

Can overload recovering service.

---

## Mistake 2

```text
No timeout during HALF_OPEN
```

May hang forever.

---

## Mistake 3

```text
Instantly restoring full traffic
```

Dangerous recovery spike.

---

## Mistake 4

```text
Ignoring concurrency control
```

Multiple threads may bypass limits.

---

## Mistake 5

```text
Long HALF_OPEN duration
```

Recovery decision delayed.

---

# 53. Most Important Insight

```text
HALF_OPEN is a controlled recovery checkpoint.
```

It prevents:

```text
recovery overload
```

which is common in distributed systems.

---

# 54. Distributed Systems Mental Model

```text
OPEN protects failure phase.

HALF_OPEN protects recovery phase.
```

Very important distinction.

---

# 55. Interview Explanation

If interviewer asks:

```text
What happens in HALF_OPEN state?
```

Strong answer:

```text
HALF_OPEN allows a limited number of probe requests after the OPEN wait
duration expires. If probe requests succeed, the circuit transitions back
to CLOSED. If failures continue, it transitions back to OPEN.
```

Senior addition:

```text
HALF_OPEN prevents traffic spikes during recovery and typically uses
thread-safe concurrency limits for probe requests.
```

---

# 56. Final Mental Model

```text
HALF_OPEN =
careful recovery testing
```

NOT:

```text
full recovery
```

yet.

---

# 57. State Comparison

```text
CLOSED:
all traffic allowed

OPEN:
no traffic allowed

HALF_OPEN:
limited test traffic allowed
```

---

# 58. What To Remember

```text
HALF_OPEN tests recovery safely.

HALF_OPEN allows limited probe requests.

Successful probes → CLOSED.

Failed probes → OPEN.

Concurrency limits are critical.

HALF_OPEN prevents recovery traffic spikes.

HALF_OPEN usually uses semaphores/CAS.

HALF_OPEN is recovery checkpoint state.
```

---

# 59. Final Recovery Flow

```text
Service Fails
      ↓
OPEN
      ↓
Cooldown
      ↓
HALF_OPEN
      ↓
Probe Requests
      ↓
Healthy?
   yes/no
```

---

# 60. Next File

```text
006_State_Transition_Engine.md
```

Next you learn:

```text
full circuit breaker state machine
transition engine internals
state transition rules
AtomicReference state updates
CAS transitions
lock-free state management
production transition algorithms
```
