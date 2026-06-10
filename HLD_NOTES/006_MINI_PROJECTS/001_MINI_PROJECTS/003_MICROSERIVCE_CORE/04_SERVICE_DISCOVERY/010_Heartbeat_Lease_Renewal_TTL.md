# 010_Heartbeat_Lease_Renewal_TTL.md

# MiniServiceDiscovery — 010 Heartbeat, Lease Renewal, TTL

---

# 1. Why This File Exists

Service registration alone is not enough.

After registration:

```text
registry must continuously know:
is instance still alive?
```

Because services may:

```text
crash
freeze
disconnect
restart
lose network
become unhealthy
```

If dead instances remain inside registry:

```text
clients continue routing traffic to dead services
```

This causes:

```text
timeouts
retry storms
cascading failures
slow requests
partial outages
```

To solve this, service discovery systems use:

```text
heartbeats
leases
TTL expiration
cleanup scanners
```

This file explains:

```text
heartbeat mechanism
lease renewal
TTL expiration
cleanup scanning
dead instance detection
false positive removals
Eureka lease model
Kubernetes readiness/liveness
timing calculations
production tuning
Java implementation
dry runs
```

---

# 2. One-Line Definition

```text
Heartbeats periodically prove instance liveness, leases track temporary validity, and TTL expiration removes dead instances automatically.
```

---

# 3. Biggest Mental Model

```text
Registration says:
"I exist"

Heartbeat says:
"I still exist"

TTL expiration says:
"You disappeared"
```

---

# 4. Core Discovery Problem

Suppose registry contains:

```text
payment-1
payment-2
payment-3
```

Now:

```text
payment-2 crashes
```

Registry still thinks:

```text
payment-2 alive
```

Clients continue sending traffic there.

Requests fail.

---

# 5. Dead Instance Problem ASCII

```text
Registry
   ↓
[payment1, payment2, payment3]

payment2 crashes

Registry still returns payment2
      ↓
Clients call dead instance
      ↓
Timeouts/errors
```

---

# 6. Why Static Registration Fails

If service registers only once:

```text
registry state becomes stale over time
```

Need continuous liveness verification.

---

# 7. Solution

Registry continuously verifies:

```text
is instance still alive?
```

using:

```text
heartbeats
```

---

# 8. What Is Heartbeat?

Heartbeat means:

```text
periodic signal proving instance still alive
```

---

# 9. Heartbeat Mental Model

Service periodically says:

```text
"I'm alive."
```

---

# 10. Heartbeat Flow

```text
Service Registers
      ↓
Every N Seconds
      ↓
Service Sends Heartbeat
      ↓
Registry Updates Timestamp
      ↓
Lease Extended
```

---

# 11. Heartbeat ASCII

```text
payment-service
      ↓ heartbeat
Registry
      ↓ update timestamp
```

---

# 12. Heartbeat Interval

Heartbeats commonly sent every:

```text
5 sec
10 sec
30 sec
```

depending on system scale.

---

# 13. Heartbeat Tradeoff

Small heartbeat interval:

```text
fast dead detection
more network traffic
more registry load
```

Large heartbeat interval:

```text
less overhead
slower dead detection
```

---

# 14. Example Timing

Heartbeat interval:

```text
10 sec
```

TTL:

```text
30 sec
```

Meaning:

```text
instance may miss ~3 heartbeats before removal
```

---

# 15. What Is Lease?

Lease means:

```text
temporary validity granted to instance
```

---

# 16. Lease Mental Model

Registry says:

```text
You are considered alive
for next 30 seconds.
Renew before expiration.
```

---

# 17. Lease Renewal

Each heartbeat:

```text
extends lease expiration
```

---

# 18. Lease ASCII

```text
Lease Expiration = NOW + TTL

Heartbeat arrives
      ↓
Lease expiration extended
```

---

# 19. What Is TTL?

TTL means:

```text
Time To Live
```

---

# 20. TTL Mental Model

```text
If heartbeat not received before TTL expires,
instance considered dead.
```

---

# 21. TTL ASCII

```text
Heartbeat Missing
      ↓
TTL Expires
      ↓
Cleanup Scanner Removes Instance
```

---

# 22. Why TTL Important

Without TTL:

```text
dead instances remain forever
```

Registry becomes stale.

---

# 23. Full Lifecycle

```text
Register
    ↓
Lease Created
    ↓
Heartbeat Renews Lease
    ↓
Heartbeat Stops
    ↓
TTL Expires
    ↓
Cleanup Removes Instance
```

---

# 24. Internal Timing Model

Registry stores:

```java
lastHeartbeatTime
leaseExpirationTime
```

---

# 25. Thread-Safe ServiceInstance

```java
import java.util.concurrent.atomic.AtomicLong;

public class ServiceInstance {

    private final String instanceId;

    private final AtomicLong lastHeartbeatTime;

    private final AtomicLong leaseExpirationTime;

    public ServiceInstance(
            String instanceId,
            long now,
            long ttlMillis) {

        this.instanceId = instanceId;

        this.lastHeartbeatTime =
                new AtomicLong(now);

        this.leaseExpirationTime =
                new AtomicLong(now + ttlMillis);
    }

    public void renewLease(
            long now,
            long ttlMillis) {

        lastHeartbeatTime.set(now);

        leaseExpirationTime.set(
                now + ttlMillis
        );
    }

    public boolean isExpired(
            long now) {

        return now >
                leaseExpirationTime.get();
    }

    public long getLastHeartbeatTime() {
        return lastHeartbeatTime.get();
    }

    public long getLeaseExpirationTime() {
        return leaseExpirationTime.get();
    }
}
```

---

# 26. Why AtomicLong Used

Heartbeat thread updates timestamps.

Cleanup thread reads timestamps.

Need:

```text
safe visibility between threads
```

---

# 27. Heartbeat Request Example

Typical request:

```json
{
  "serviceName": "payment-service",
  "instanceId": "payment-1"
}
```

---

# 28. Heartbeat Internal Flow

```text
receive heartbeat
      ↓
find instance
      ↓
update heartbeat timestamp
      ↓
extend lease expiration
```

---

# 29. Heartbeat Renewal Code

```java
public void heartbeat(
        String serviceName,
        String instanceId,
        long now,
        long ttlMillis) {

    List<ServiceInstance> instances =
            registry.get(serviceName);

    if (instances == null) {
        return;
    }

    for (ServiceInstance instance : instances) {

        if (instance.getInstanceId()
                .equals(instanceId)) {

            instance.renewLease(
                    now,
                    ttlMillis
            );

            return;
        }
    }
}
```

---

# 30. Cleanup Scanner

Registry runs background cleanup task.

Periodically checks:

```text
which leases expired?
```

---

# 31. Cleanup Scanner Mental Model

```text
Garbage collector for dead instances.
```

---

# 32. Cleanup Flow

```text
Every N Seconds
      ↓
Scan Registry
      ↓
Check Lease Expiration
      ↓
Remove Expired Instances
```

---

# 33. Cleanup Scanner Code

```java
public void cleanupExpiredInstances(
        long now) {

    for (String serviceName : registry.keySet()) {

        List<ServiceInstance> instances =
                registry.get(serviceName);

        if (instances == null) {
            continue;
        }

        instances.removeIf(instance ->
                instance.isExpired(now)
        );

        if (instances.isEmpty()) {
            registry.remove(serviceName);
        }
    }
}
```

---

# 34. Cleanup Interval

Cleanup commonly runs every:

```text
5 sec
10 sec
30 sec
```

---

# 35. Cleanup Tradeoff

Fast cleanup:

```text
dead instances removed quickly
higher CPU usage
```

Slow cleanup:

```text
stale instances remain longer
```

---

# 36. Missed Heartbeats

Missing one heartbeat does NOT always mean:

```text
instance dead
```

Possible causes:

```text
network delay
packet loss
GC pause
CPU spike
thread starvation
temporary partition
```

---

# 37. False Positive Removal

Healthy instance accidentally removed.

Called:

```text
false positive failure detection
```

---

# 38. Bad Configuration Example

Heartbeat:

```text
10 sec
```

TTL:

```text
11 sec
```

One small network delay:

```text
healthy instance removed
```

---

# 39. Better Configuration

Heartbeat:

```text
10 sec
```

TTL:

```text
30 sec
```

Allows:

```text
multiple missed heartbeats
```

---

# 40. Eureka Lease Model

Eureka uses:

```text
lease renewal
```

instead of directly exposing heartbeat timestamps.

Same core idea.

---

# 41. Eureka Mental Model

```text
Each instance owns renewable lease.
```

No renewal:

```text
lease expires
instance evicted
```

---

# 42. Eureka Default Timing

Typical defaults:

```text
heartbeat every 30 sec
lease expiration 90 sec
```

Meaning:

```text
~3 missed heartbeats tolerated
```

---

# 43. Eureka Self-Preservation

Suppose heartbeats suddenly drop massively.

Possible reasons:

```text
all services crashed
OR
network partition
```

Eureka may enter:

```text
self-preservation mode
```

and stop aggressive eviction.

---

# 44. Self-Preservation Mental Model

```text
Better stale registry
than accidental mass eviction.
```

---

# 45. Self-Preservation ASCII

```text
Mass Heartbeat Drop
      ↓
Eureka Suspects Network Issue
      ↓
Temporarily Stops Cleanup
```

---

# 46. Kubernetes Health Model

Kubernetes uses:

```text
liveness probe
readiness probe
startup probe
```

instead of classic Eureka lease model.

---

# 47. Kubernetes Readiness

If readiness probe fails:

```text
pod removed from endpoints
```

Traffic stops routing there.

---

# 48. Kubernetes Liveness

If liveness fails:

```text
container restarted
```

---

# 49. Readiness vs Liveness

## Readiness

```text
Should traffic route here?
```

## Liveness

```text
Should container restart?
```

---

# 50. Heartbeat Limitation

Heartbeat only proves:

```text
process still sending signal
```

Does NOT guarantee:

```text
DB healthy
dependencies healthy
thread pool healthy
application healthy
```

---

# 51. Deep Health Checks

Production systems combine:

```text
heartbeat
+
health endpoints
+
dependency checks
```

---

# 52. Example Health Endpoint

```text
/actuator/health
```

Response:

```json
{
  "status": "UP"
}
```

---

# 53. Heartbeat Scaling Problem

Large systems may generate:

```text
millions of heartbeats/sec
```

Registry must scale carefully.

---

# 54. Scaling Techniques

Production systems use:

```text
regional registries
distributed registries
heartbeat batching
client-side caching
hierarchical discovery
```

---

# 55. Detection Time Formula

Worst-case detection time:

```text
TTL + cleanup interval
```

Example:

```text
TTL = 30 sec
cleanup every 10 sec
```

Worst-case:

```text
~40 sec removal delay
```

---

# 56. Why Detection Not Instant

Instance may die immediately after cleanup scan.

Registry waits for:

```text
next cleanup cycle
```

---

# 57. Detection Timeline ASCII

```text
Cleanup Scan
      ↓
Instance Dies
      ↓
TTL Expires
      ↓
Next Cleanup Scan
      ↓
Instance Removed
```

---

# 58. Dry Run — Healthy Renewal

Time:

```text
12:00:00
```

Register payment-1.

Lease expires:

```text
12:00:30
```

Heartbeat at:

```text
12:00:10
```

Lease extended:

```text
12:00:40
```

Heartbeat at:

```text
12:00:20
```

Lease extended:

```text
12:00:50
```

Instance remains healthy.

---

# 59. Dry Run — Crash

payment-1 crashes at:

```text
12:00:05
```

No more heartbeats.

Lease expires:

```text
12:00:30
```

Cleanup runs at:

```text
12:00:35
```

Instance removed.

---

# 60. Dry Run — False Positive

Heartbeat:

```text
every 10 sec
```

TTL:

```text
11 sec
```

Network delay:

```text
2 sec
```

Heartbeat arrives late.

Healthy instance removed accidentally.

---

# 61. Production Tuning Guidelines

Typical safe formula:

```text
TTL ≈ 3 × heartbeat interval
```

Example:

```text
heartbeat = 10 sec
TTL = 30 sec
```

---

# 62. Common Production Mistakes

## Mistake 1

```text
TTL too low
```

False removals.

---

## Mistake 2

```text
cleanup too aggressive
```

Registry instability.

---

## Mistake 3

```text
heartbeat interval too small
```

Massive registry overhead.

---

## Mistake 4

```text
heartbeat alone trusted
```

App may be alive but unusable.

---

## Mistake 5

```text
cleanup thread unsafe
```

Race conditions.

---

## Mistake 6

```text
clock synchronization ignored
```

Distributed timing issues.

---

# 63. Production Debugging Questions

If instance disappears unexpectedly, ask:

```text
did heartbeat stop?
was TTL too low?
was there GC pause?
was cleanup too aggressive?
did readiness fail?
did Eureka enter self-preservation?
```

---

# 64. Strong Interview Answer

Question:

```text
How do heartbeat and TTL work in service discovery?
```

Strong answer:

```text
After registration, service instances periodically send heartbeats to renew
their lease in the registry. Each heartbeat updates the last heartbeat
timestamp and extends lease expiration time. If heartbeats stop and TTL
expires, cleanup scanners remove the instance to prevent traffic routing
to dead services.
```

Senior addition:

```text
Production systems carefully tune heartbeat interval and TTL to avoid false
positive removals caused by GC pauses or temporary network issues. Systems
like Eureka also implement self-preservation to avoid mass accidental
evictions during partitions.
```

---

# 65. Most Important Insight

```text
Heartbeats maintain liveness.
TTL removes silence.
```

---

# 66. Final Mental Model

```text
Heartbeat
=
renew lease

Lease
=
temporary permission to stay alive

TTL expiration
=
instance disappeared

Cleanup scanner
=
dead instance garbage collector
```

---

# 67. What To Remember

```text
Heartbeats prove liveness.

Leases expire without renewal.

TTL removes dead instances.

Cleanup scanners evict expired entries.

TTL should tolerate missed heartbeats.

Heartbeat alone does not guarantee full health.

Eureka uses lease renewal.

Kubernetes uses readiness/liveness probes.

False positive removals are dangerous.
```

---

# 68. Next File

```text
011_Dead_Instance_Removal.md
```

Next you learn:

```text
cleanup strategies
eviction algorithms
grace periods
self-preservation
distributed cleanup challenges
aggressive vs conservative eviction
```
