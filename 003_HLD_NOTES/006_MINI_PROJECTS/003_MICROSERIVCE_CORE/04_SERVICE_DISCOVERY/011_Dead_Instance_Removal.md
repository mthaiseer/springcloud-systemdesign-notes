# 011_Dead_Instance_Removal.md

# MiniServiceDiscovery — Dead Instance Removal

---

# 1. Why This File Exists

Service discovery systems continuously face one dangerous problem:

```text
dead instances remaining inside registry
```

Example:

```text
payment-service
    → payment-1
    → payment-2
    → payment-3
```

Suppose:

```text
payment-2 crashes
```

But registry still contains:

```text
payment-2
```

Clients continue routing traffic there.

This causes:

```text
timeouts
retry storms
slow requests
cascading failures
partial outages
```

So service discovery systems must automatically:

```text
detect dead instances
remove stale instances
prevent stale routing
```

This file explains:

```text
dead instance detection
cleanup scanners
TTL expiration cleanup
eviction algorithms
grace periods
false positive removals
Eureka eviction
self-preservation
Kubernetes endpoint removal
distributed cleanup problems
Java implementation
dry runs
production tuning
```

---

# 2. One-Line Definition

```text
Dead instance removal automatically evicts expired or unreachable service instances from registry.
```

---

# 3. Biggest Mental Model

```text
Heartbeats prove liveness.

Dead instance removal handles silence.
```

---

# 4. Core Failure Problem

Registry contains:

```text
payment1
payment2
payment3
```

Now:

```text
payment2 crashes
```

Registry still returns:

```text
payment2
```

Clients continue sending requests.

Requests fail.

---

# 5. Failure Flow ASCII

```text
Registry
   ↓
[payment1, payment2, payment3]

payment2 crashes

Registry still returns payment2
      ↓
Clients call dead service
      ↓
Timeouts/errors
```

---

# 6. Why Deregistration Alone Is Not Enough

Graceful shutdown works only when:

```text
service exits normally
```

But real systems fail unexpectedly:

```text
process crash
OOM kill
kernel panic
network partition
container crash
node failure
```

No graceful deregistration possible.

Need automatic cleanup.

---

# 7. Cleanup Flow

```text
Heartbeat Stops
      ↓
Lease Expires
      ↓
Cleanup Scanner Detects Expiration
      ↓
Instance Removed
```

---

# 8. Cleanup Scanner Mental Model

```text
Garbage collector for dead services.
```

---

# 9. What Cleanup Scanner Does

Background task periodically:

```text
scan registry
check lease expiration
remove expired instances
remove empty services
```

---

# 10. Cleanup Scanner ASCII

```text
Background Cleanup Thread
        ↓
Scan Registry
        ↓
Find Expired Instances
        ↓
Remove Dead Instances
```

---

# 11. Why Cleanup Runs In Background

Cleanup should NOT block:

```text
lookup
registration
heartbeats
```

So it runs asynchronously.

---

# 12. Cleanup Frequency

Typical cleanup intervals:

```text
5 sec
10 sec
30 sec
```

---

# 13. Cleanup Tradeoff

Fast cleanup:

```text
faster dead removal
higher CPU usage
higher false positive risk
```

Slow cleanup:

```text
stale instances remain longer
```

---

# 14. Expiration Logic

Registry checks:

```text
currentTime > leaseExpirationTime
```

If true:

```text
instance expired
```

---

# 15. Example Expiration

Current time:

```text
12:00:40
```

Lease expiration:

```text
12:00:30
```

Result:

```text
instance expired
```

---

# 16. ServiceInstance Model

```java
import java.util.concurrent.atomic.AtomicLong;

public class ServiceInstance {

    private final String instanceId;

    private final AtomicLong leaseExpirationTime;

    public ServiceInstance(
            String instanceId,
            long expirationTime) {

        this.instanceId = instanceId;

        this.leaseExpirationTime =
                new AtomicLong(expirationTime);
    }

    public String getInstanceId() {
        return instanceId;
    }

    public long getLeaseExpirationTime() {
        return leaseExpirationTime.get();
    }

    public boolean isExpired(long now) {

        return now >
                leaseExpirationTime.get();
    }
}
```

---

# 17. Why AtomicLong Used

Heartbeat thread updates expiration.

Cleanup thread reads expiration.

Need:

```text
safe visibility between threads
```

---

# 18. Basic Cleanup Code

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
    }
}
```

---

# 19. Empty Service Cleanup

After removing all instances:

```text
payment-service → []
```

Registry should remove service key too.

---

# 20. Full Cleanup Code

```java
public void cleanupExpiredInstances(
        long now) {

    for (String serviceName : registry.keySet()) {

        CopyOnWriteArrayList<ServiceInstance> instances =
                registry.get(serviceName);

        if (instances == null) {
            continue;
        }

        instances.removeIf(instance ->
                instance.isExpired(now)
        );

        if (instances.isEmpty()) {

            registry.remove(
                    serviceName,
                    instances
            );
        }
    }
}
```

---

# 21. Why remove(key, value)

Safer than:

```java
registry.remove(serviceName)
```

because another thread may already replace list.

Conditional remove prevents:

```text
accidental deletion
```

---

# 22. Cleanup Thread

Typical scheduler:

```java
ScheduledExecutorService cleanupExecutor =
        Executors.newSingleThreadScheduledExecutor();
```

---

# 23. Periodic Cleanup Task

```java
cleanupExecutor.scheduleAtFixedRate(
        () -> cleanupExpiredInstances(
                System.currentTimeMillis()
        ),
        10,
        10,
        TimeUnit.SECONDS
);
```

---

# 24. Detection Delay Formula

Worst-case dead detection:

```text
TTL + cleanup interval
```

Example:

```text
TTL = 30 sec
cleanup every 10 sec
```

Worst-case removal:

```text
~40 sec
```

---

# 25. Why Removal Not Instant

Instance may die immediately after cleanup scan.

Registry waits until:

```text
next cleanup cycle
```

---

# 26. Timeline ASCII

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

# 27. Grace Period

Production systems often allow:

```text
extra safety window
```

before eviction.

---

# 28. Why Grace Period Needed

Temporary issues may delay heartbeat:

```text
GC pause
network jitter
CPU spike
packet loss
```

Without grace:

```text
healthy service removed accidentally
```

---

# 29. False Positive Failure Detection

Healthy instance removed incorrectly.

Called:

```text
false positive failure detection
```

---

# 30. Bad Configuration Example

Heartbeat:

```text
10 sec
```

TTL:

```text
11 sec
```

One network delay:

```text
healthy instance evicted
```

---

# 31. Better Configuration

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

# 32. Conservative Cleanup

Conservative cleanup:

```text
wait longer before removal
```

Benefits:

```text
fewer false positives
```

Tradeoff:

```text
dead instances remain longer
```

---

# 33. Aggressive Cleanup

Aggressive cleanup:

```text
remove instances quickly
```

Benefits:

```text
fast dead removal
```

Tradeoff:

```text
higher false positive risk
```

---

# 34. Cleanup Tradeoff Mental Model

```text
Fast cleanup
=
risk false removals

Slow cleanup
=
risk stale routing
```

---

# 35. Eureka Eviction Model

Eureka periodically runs:

```text
eviction task
```

It removes:

```text
expired leases
```

---

# 36. Eureka Mental Model

```text
Expired lease
=
dead instance
```

---

# 37. Eureka Self-Preservation

Suppose:

```text
many heartbeats disappear suddenly
```

Possible reasons:

```text
network partition
registry overload
mass crash
```

Eureka may enter:

```text
self-preservation mode
```

---

# 38. Self-Preservation Goal

Avoid:

```text
mass accidental eviction
```

during network problems.

---

# 39. Self-Preservation Mental Model

```text
Better stale registry
than deleting half cluster accidentally.
```

---

# 40. Self-Preservation ASCII

```text
Heartbeat Drop Spike
      ↓
Eureka Suspects Network Problem
      ↓
Stops Aggressive Eviction
```

---

# 41. Distributed Cleanup Problem

In distributed registries:

```text
multiple registry nodes exist
```

Possible problems:

```text
split brain
stale replication
duplicate eviction
inconsistent cleanup
```

---

# 42. Split Brain Example

Registry A sees:

```text
payment1 alive
```

Registry B sees:

```text
payment1 dead
```

Clients get inconsistent routing.

---

# 43. Kubernetes Cleanup Model

Kubernetes removes endpoints when:

```text
readiness fails
node unreachable
pod deleted
```

---

# 44. Kubernetes Endpoint Removal

```text
Readiness Fails
      ↓
EndpointSlice Updated
      ↓
Traffic Stops
```

---

# 45. Why Kubernetes Faster

Kubernetes continuously watches cluster state.

Endpoint updates propagate quickly.

---

# 46. Cleanup Race Condition

Heartbeat may arrive while cleanup runs.

Need thread safety.

---

# 47. Race Example

Thread A:

```text
heartbeat updates expiration
```

Thread B:

```text
cleanup checks expiration
```

Without safe visibility:

```text
healthy instance removed
```

---

# 48. Safe Visibility

Use:

```text
AtomicLong
volatile
thread-safe collections
```

---

# 49. Cleanup During Lookup

Lookup thread may iterate while cleanup removes instance.

Need safe collections like:

```text
CopyOnWriteArrayList
```

---

# 50. CopyOnWrite Benefit

Readers see:

```text
stable snapshot
```

while cleanup safely modifies copy.

---

# 51. Cleanup Logging

Production systems log evictions.

Example:

```text
Removed expired payment-2
```

Useful for debugging.

---

# 52. Important Cleanup Metrics

Track:

```text
eviction count
expired instances
heartbeat failures
cleanup latency
false positive removals
```

---

# 53. Monitoring Insight

Sudden eviction spike may indicate:

```text
network partition
GC issue
registry overload
heartbeat failure
```

---

# 54. Dry Run — Normal Removal

Heartbeat:

```text
every 10 sec
```

TTL:

```text
30 sec
```

payment2 crashes at:

```text
12:00:05
```

No heartbeat received.

Lease expires:

```text
12:00:30
```

Cleanup runs:

```text
12:00:35
```

payment2 removed.

---

# 55. Dry Run — Graceful Shutdown

payment1 deregisters before shutdown.

Registry removes immediately.

No TTL wait needed.

---

# 56. Dry Run — False Positive

Heartbeat delayed due to:

```text
20 sec GC pause
```

TTL too small:

```text
15 sec
```

Cleanup removes healthy instance accidentally.

---

# 57. Dry Run — Self Preservation

Heartbeat traffic suddenly drops 80%.

Eureka suspects:

```text
network partition
```

Eviction paused temporarily.

Registry becomes stale but safer.

---

# 58. Common Production Mistakes

## Mistake 1

```text
TTL too aggressive
```

Healthy services removed.

---

## Mistake 2

```text
cleanup interval too slow
```

Dead instances remain too long.

---

## Mistake 3

```text
no self-preservation
```

Mass accidental eviction possible.

---

## Mistake 4

```text
unsafe cleanup thread
```

Race conditions.

---

## Mistake 5

```text
heartbeat delays ignored
```

GC pauses cause instability.

---

## Mistake 6

```text
no eviction monitoring
```

Hard to debug outages.

---

# 59. Production Debugging Questions

If services disappear unexpectedly, ask:

```text
did heartbeat stop?
was TTL too small?
was there GC pause?
did cleanup run aggressively?
did network partition happen?
did self-preservation activate?
```

---

# 60. Strong Interview Answer

Question:

```text
How are dead instances removed from service registry?
```

Strong answer:

```text
Discovery systems periodically run cleanup scanners that check whether
service leases expired due to missed heartbeats. Expired instances are
evicted from registry to prevent routing traffic to dead services.
```

Senior addition:

```text
Production systems carefully tune TTL and cleanup intervals to balance
fast dead detection against false positive removals. Systems like Eureka
also implement self-preservation to avoid mass accidental eviction during
network partitions.
```

---

# 61. Most Important Insight

```text
Dead instance removal keeps registry trustworthy.
```

---

# 62. Final Mental Model

```text
Heartbeat
=
proof of life

TTL expiration
=
silence timeout

Cleanup scanner
=
dead service sweeper

Eviction
=
remove stale routing target
```

---

# 63. What To Remember

```text
Dead services must be removed automatically.

Cleanup scanners periodically evict expired instances.

TTL expiration determines death.

Grace periods reduce false positives.

Aggressive cleanup risks instability.

Conservative cleanup risks stale routing.

Eureka uses eviction + self-preservation.

Kubernetes removes unhealthy endpoints automatically.

Cleanup must be thread-safe.
```

---

# 64. Next File

```text
012_Eureka_Self_Preservation_Mode.md
```

Next you learn:

```text
network partition handling
mass heartbeat loss
split brain protection
availability vs consistency
why Eureka prefers availability
```
