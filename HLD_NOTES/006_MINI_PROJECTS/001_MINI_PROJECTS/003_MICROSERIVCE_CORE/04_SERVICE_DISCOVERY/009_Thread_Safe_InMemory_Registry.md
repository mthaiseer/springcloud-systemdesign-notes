# 009_Thread_Safe_InMemory_Registry.md

# MiniServiceDiscovery — 009 Thread Safe InMemory Registry

---

# 1. Why This File Exists

A service registry is accessed by many threads at the same time.

Examples:

```text
service instances register
clients lookup instances
services send heartbeats
cleanup thread removes expired instances
admin marks instances OUT_OF_SERVICE
```

All of this can happen concurrently.

So an in-memory registry cannot safely use:

```java
HashMap<String, List<ServiceInstance>>
```

without protection.

This file explains:

```text
why registry must be thread-safe
what race conditions happen
why HashMap + ArrayList unsafe
how ConcurrentHashMap helps
how CopyOnWriteArrayList helps
safe registration
safe lookup
safe deregistration
safe heartbeat updates
safe TTL cleanup
snapshot reads
production tradeoffs
Java implementation
dry runs
interview explanation
```

---

# 2. One-Line Definition

```text
A thread-safe in-memory registry safely handles concurrent registration, lookup, heartbeat, cleanup, and deregistration.
```

---

# 3. Biggest Mental Model

```text
Registry is shared mutable state.
Shared mutable state needs thread safety.
```

---

# 4. Why Registry Is Shared State

Registry is shared by:

```text
registration threads
lookup threads
heartbeat threads
cleanup thread
admin update threads
```

All access same data:

```text
serviceName → instances
```

---

# 5. Core Registry Data

Simple model:

```java
Map<String, List<ServiceInstance>> registry;
```

Example:

```text
payment-service
    → payment-1
    → payment-2
```

---

# 6. Why HashMap Unsafe

`HashMap` is not safe for concurrent modification.

Problems:

```text
lost updates
corrupted internal structure
ConcurrentModificationException
visibility issues
incorrect lookup results
```

---

# 7. Why ArrayList Unsafe

`ArrayList` is also not safe for concurrent modification.

Problems:

```text
one thread iterates while another removes
one thread adds while another reads
size changes unexpectedly
ConcurrentModificationException
```

---

# 8. Unsafe Registry Example

```java
private final Map<String, List<ServiceInstance>> registry =
        new HashMap<>();
```

This looks simple.

But under concurrency:

```text
dangerous
```

---

# 9. Race Condition Example — Registration

Two instances register at same time:

```text
Thread A registers payment-1
Thread B registers payment-2
```

Both see:

```text
payment-service key missing
```

Both create new list.

One write overwrites the other.

Result:

```text
only one instance stored
```

---

# 10. Lost Update ASCII

```text
Thread A: get(payment-service) = null
Thread B: get(payment-service) = null

Thread A: put(payment-service, [payment-1])
Thread B: put(payment-service, [payment-2])

Final:
payment-service → [payment-2]

payment-1 lost
```

---

# 11. Race Condition Example — Lookup During Remove

Lookup thread iterates over list.

Cleanup thread removes expired instance.

Result may be:

```text
ConcurrentModificationException
stale result
inconsistent list
```

---

# 12. Lookup/Remove Race ASCII

```text
Thread A: iterate [payment1, payment2, payment3]
Thread B: remove payment2
Thread A: list changed during iteration
```

Unsafe.

---

# 13. Race Condition Example — Heartbeat Visibility

Heartbeat thread updates:

```text
lastHeartbeatTime
```

Cleanup thread may not see latest value if field visibility not safe.

Result:

```text
healthy instance removed incorrectly
```

---

# 14. Visibility Mental Model

Thread A writes.

Thread B must see latest write.

Without safe publication/volatile/synchronization:

```text
not guaranteed
```

---

# 15. Thread-Safe Registry Goal

Need safe support for:

```text
concurrent register
concurrent lookup
concurrent deregister
concurrent heartbeat
concurrent cleanup
```

---

# 16. Recommended Mini Data Structure

```java
ConcurrentHashMap<
    String,
    CopyOnWriteArrayList<ServiceInstance>
>
```

---

# 17. Why ConcurrentHashMap

`ConcurrentHashMap` supports:

```text
safe concurrent reads
safe concurrent writes
better performance than synchronized map
atomic computeIfAbsent
```

---

# 18. Why CopyOnWriteArrayList

Registry pattern is usually:

```text
many reads
few writes
```

`CopyOnWriteArrayList` is good when:

```text
lookup/iteration frequent
registration/removal less frequent
```

---

# 19. CopyOnWrite Mental Model

On write:

```text
copy old array
modify copy
replace reference
```

Readers see stable snapshot.

---

# 20. CopyOnWrite ASCII

```text
Old List: [payment1, payment2]

Write add payment3:
copy → [payment1, payment2, payment3]
replace reference
```

Existing readers continue safely.

---

# 21. Tradeoff Of CopyOnWriteArrayList

Good:

```text
safe iteration
fast reads
no ConcurrentModificationException
```

Bad:

```text
writes expensive
memory copy on each write
not good for write-heavy workload
```

---

# 22. Why It Fits Service Discovery

Discovery workload:

```text
lookups happen constantly
registrations happen occasionally
```

So:

```text
read-heavy structure fits well
```

---

# 23. ServiceInstance Thread Safety

Instance fields updated concurrently.

Example:

```text
status
lastHeartbeatTime
```

These should use:

```text
volatile
AtomicLong
AtomicReference
synchronization
```

---

# 24. ServiceInstance Safe Model

```java
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class ServiceInstance {

    private final String serviceName;

    private final String instanceId;

    private final String host;

    private final int port;

    private final AtomicReference<InstanceStatus> status;

    private final AtomicLong lastHeartbeatTime;

    private final Map<String, String> metadata;

    public ServiceInstance(
            String serviceName,
            String instanceId,
            String host,
            int port,
            InstanceStatus status,
            long now,
            Map<String, String> metadata) {

        this.serviceName = serviceName;
        this.instanceId = instanceId;
        this.host = host;
        this.port = port;
        this.status = new AtomicReference<>(status);
        this.lastHeartbeatTime = new AtomicLong(now);
        this.metadata = metadata;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public InstanceStatus getStatus() {
        return status.get();
    }

    public void setStatus(
            InstanceStatus newStatus) {

        status.set(newStatus);
    }

    public long getLastHeartbeatTime() {
        return lastHeartbeatTime.get();
    }

    public void updateHeartbeat(
            long now) {

        lastHeartbeatTime.set(now);
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
```

---

# 25. InstanceStatus Enum

```java
public enum InstanceStatus {

    STARTING,
    UP,
    DOWN,
    OUT_OF_SERVICE,
    UNKNOWN
}
```

---

# 26. Thread-Safe Registry Class

```java
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryServiceRegistry {

    private final ConcurrentHashMap<
            String,
            CopyOnWriteArrayList<ServiceInstance>
    > registry = new ConcurrentHashMap<>();

}
```

---

# 27. Safe Registration Code

```java
public void register(
        ServiceInstance instance) {

    CopyOnWriteArrayList<ServiceInstance> instances =
            registry.computeIfAbsent(
                    instance.getServiceName(),
                    key -> new CopyOnWriteArrayList<>()
            );

    boolean exists =
            instances.stream()
                    .anyMatch(existing ->
                            existing.getInstanceId()
                                    .equals(instance.getInstanceId())
                    );

    if (!exists) {
        instances.add(instance);
    }
}
```

---

# 28. Why computeIfAbsent Important

`computeIfAbsent` is atomic.

It prevents:

```text
two threads creating two different lists
```

for same service.

---

# 29. Registration Race Fixed

Before:

```text
Thread A and B both create list
one overwrites other
```

After:

```text
computeIfAbsent ensures one list
both add to same list safely
```

---

# 30. Safe Lookup Code

```java
public List<ServiceInstance> lookupHealthy(
        String serviceName) {

    return registry
            .getOrDefault(
                    serviceName,
                    new CopyOnWriteArrayList<>()
            )
            .stream()
            .filter(instance ->
                    instance.getStatus()
                            == InstanceStatus.UP
            )
            .toList();
}
```

---

# 31. Why Return Snapshot

`.toList()` returns a snapshot-like result.

Caller cannot mutate internal registry list directly.

---

# 32. Unsafe Return Problem

Bad:

```java
return registry.get(serviceName);
```

Caller may do:

```java
list.clear();
```

and accidentally wipe registry.

---

# 33. Safe Snapshot Mental Model

```text
Registry owns internal data.
Caller receives copy/view of result.
```

---

# 34. Safe Deregistration Code

```java
public void deregister(
        String serviceName,
        String instanceId) {

    CopyOnWriteArrayList<ServiceInstance> instances =
            registry.get(serviceName);

    if (instances == null) {
        return;
    }

    instances.removeIf(instance ->
            instance.getInstanceId()
                    .equals(instanceId)
    );

    if (instances.isEmpty()) {
        registry.remove(serviceName, instances);
    }
}
```

---

# 35. Why registry.remove(key, value)

This conditional remove ensures:

```text
remove only if current value is same list
```

Prevents accidentally removing newly updated mapping.

---

# 36. Safe Heartbeat Code

```java
public void heartbeat(
        String serviceName,
        String instanceId,
        long now) {

    List<ServiceInstance> instances =
            registry.get(serviceName);

    if (instances == null) {
        return;
    }

    for (ServiceInstance instance : instances) {

        if (instance.getInstanceId()
                .equals(instanceId)) {

            instance.updateHeartbeat(now);
            instance.setStatus(InstanceStatus.UP);
            return;
        }
    }
}
```

---

# 37. Heartbeat Race Safety

`AtomicLong` ensures:

```text
latest heartbeat timestamp visible to cleanup thread
```

`AtomicReference` ensures:

```text
status updates visible safely
```

---

# 38. Safe Cleanup Code

```java
public void removeExpiredInstances(
        long now,
        long ttlMillis) {

    for (String serviceName : registry.keySet()) {

        CopyOnWriteArrayList<ServiceInstance> instances =
                registry.get(serviceName);

        if (instances == null) {
            continue;
        }

        instances.removeIf(instance ->
                now - instance.getLastHeartbeatTime()
                        > ttlMillis
        );

        if (instances.isEmpty()) {
            registry.remove(serviceName, instances);
        }
    }
}
```

---

# 39. Cleanup Race Scenario

Cleanup may run while heartbeat updates.

If heartbeat updates before cleanup reads:

```text
instance survives
```

If cleanup reads old timestamp first:

```text
instance may be removed
```

Production systems reduce false removals with:

```text
reasonable TTL
multiple missed heartbeat policy
self-preservation
grace periods
```

---

# 40. Why TTL Should Not Be Too Low

If TTL too low:

```text
small network delay
```

causes:

```text
healthy instance removed
```

---

# 41. TTL Mental Model

```text
TTL should tolerate normal network jitter.
```

Example:

```text
heartbeat every 10 sec
TTL 30 sec
```

Allows:

```text
3 missed heartbeats
```

---

# 42. Full Registry Implementation

```java
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryServiceRegistry {

    private final ConcurrentHashMap<
            String,
            CopyOnWriteArrayList<ServiceInstance>
    > registry = new ConcurrentHashMap<>();

    public void register(
            ServiceInstance instance) {

        CopyOnWriteArrayList<ServiceInstance> instances =
                registry.computeIfAbsent(
                        instance.getServiceName(),
                        key -> new CopyOnWriteArrayList<>()
                );

        boolean exists =
                instances.stream()
                        .anyMatch(existing ->
                                existing.getInstanceId()
                                        .equals(instance.getInstanceId())
                        );

        if (!exists) {
            instances.add(instance);
        }
    }

    public List<ServiceInstance> lookupHealthy(
            String serviceName) {

        return registry
                .getOrDefault(
                        serviceName,
                        new CopyOnWriteArrayList<>()
                )
                .stream()
                .filter(instance ->
                        instance.getStatus()
                                == InstanceStatus.UP
                )
                .toList();
    }

    public void deregister(
            String serviceName,
            String instanceId) {

        CopyOnWriteArrayList<ServiceInstance> instances =
                registry.get(serviceName);

        if (instances == null) {
            return;
        }

        instances.removeIf(instance ->
                instance.getInstanceId()
                        .equals(instanceId)
        );

        if (instances.isEmpty()) {
            registry.remove(serviceName, instances);
        }
    }

    public void heartbeat(
            String serviceName,
            String instanceId,
            long now) {

        List<ServiceInstance> instances =
                registry.get(serviceName);

        if (instances == null) {
            return;
        }

        for (ServiceInstance instance : instances) {

            if (instance.getInstanceId()
                    .equals(instanceId)) {

                instance.updateHeartbeat(now);
                instance.setStatus(InstanceStatus.UP);
                return;
            }
        }
    }

    public void removeExpiredInstances(
            long now,
            long ttlMillis) {

        for (String serviceName : registry.keySet()) {

            CopyOnWriteArrayList<ServiceInstance> instances =
                    registry.get(serviceName);

            if (instances == null) {
                continue;
            }

            instances.removeIf(instance ->
                    now - instance.getLastHeartbeatTime()
                            > ttlMillis
            );

            if (instances.isEmpty()) {
                registry.remove(serviceName, instances);
            }
        }
    }

    public Map<String, List<ServiceInstance>> snapshot() {

        Map<String, List<ServiceInstance>> copy =
                new ConcurrentHashMap<>();

        for (Map.Entry<
                String,
                CopyOnWriteArrayList<ServiceInstance>
        > entry : registry.entrySet()) {

            copy.put(
                    entry.getKey(),
                    List.copyOf(entry.getValue())
            );
        }

        return copy;
    }
}
```

---

# 43. Dry Run — Concurrent Registration

Two threads:

```text
Thread A registers payment-1
Thread B registers payment-2
```

With `ConcurrentHashMap.computeIfAbsent`:

```text
one shared list created
both instances added safely
```

Final:

```text
payment-service
    → payment-1
    → payment-2
```

---

# 44. Dry Run — Lookup During Registration

Thread A:

```text
lookup(payment-service)
```

Thread B:

```text
register(payment-3)
```

With `CopyOnWriteArrayList`:

```text
lookup sees stable snapshot
registration creates new copy internally
no ConcurrentModificationException
```

---

# 45. Dry Run — Cleanup During Lookup

Thread A:

```text
lookup healthy instances
```

Thread B:

```text
remove expired payment-2
```

With CopyOnWriteArrayList:

```text
Thread A iteration safe
Thread B removal safe
```

---

# 46. Dry Run — Heartbeat During Cleanup

Thread A:

```text
heartbeat updates payment-1 timestamp
```

Thread B:

```text
cleanup checks TTL
```

`AtomicLong` ensures:

```text
timestamp visibility safer
```

TTL grace reduces false removal.

---

# 47. Why Not Synchronize Everything?

Could use:

```java
synchronized
```

But every lookup would block.

Bad because:

```text
lookup is very frequent
```

This reduces throughput.

---

# 48. Synchronized Registry Problem

```text
lookup waits for register
register waits for cleanup
cleanup waits for heartbeat
```

Registry becomes bottleneck.

---

# 49. Better Approach

Use:

```text
ConcurrentHashMap
CopyOnWriteArrayList
AtomicLong
AtomicReference
immutable snapshots
```

This supports high read throughput.

---

# 50. Alternative Design — ReadWriteLock

Could use:

```text
ReadWriteLock
```

Good when:

```text
many reads
some writes
```

But more complex.

For mini registry:

```text
ConcurrentHashMap + CopyOnWriteArrayList is simpler
```

---

# 51. Alternative Design — Immutable Registry Snapshot

Some production systems maintain:

```text
immutable registry snapshot
```

On update:

```text
build new snapshot
swap reference atomically
```

Great for read-heavy systems.

---

# 52. Snapshot Swap Mental Model

```text
old snapshot used by readers
new snapshot built by writer
AtomicReference swaps snapshot
```

---

# 53. Snapshot Design ASCII

```text
AtomicReference<RegistrySnapshot>
        ↓
Readers read immutable snapshot
        ↓
Writers build new snapshot and swap
```

---

# 54. Eureka-Like Client Cache

Eureka clients often keep:

```text
local registry cache
```

Cache is refreshed periodically.

This reduces:

```text
direct registry calls
```

---

# 55. Kubernetes EndpointSlice Model

Kubernetes stores endpoint state in:

```text
EndpointSlices
```

Controllers update endpoint state.

Consumers/proxies watch updates.

This is another form of:

```text
thread-safe distributed registry state
```

---

# 56. Production Race Conditions

Common production issues:

```text
stale client cache
duplicate registration
heartbeat delay
cleanup too aggressive
read while remove
metadata update race
```

---

# 57. How Production Systems Reduce Issues

Use:

```text
longer TTL than heartbeat interval
multiple missed heartbeat tolerance
self-preservation
health checks
retry on failed instance
circuit breaker
local cache refresh
```

---

# 58. Common Mistakes

## Mistake 1

```text
using HashMap for concurrent registry
```

Unsafe.

---

## Mistake 2

```text
returning internal mutable list
```

Caller may corrupt registry.

---

## Mistake 3

```text
TTL too low
```

False removals.

---

## Mistake 4

```text
no duplicate registration check
```

Same instance added twice.

---

## Mistake 5

```text
heartbeat field not thread-safe
```

Cleanup may read stale timestamp.

---

## Mistake 6

```text
synchronizing entire registry
```

Lookup throughput collapses.

---

# 59. Production Debugging Questions

If discovery behaves incorrectly, ask:

```text
was instance registered twice?
did lookup return DOWN instance?
did cleanup remove healthy instance?
was heartbeat timestamp visible?
is client cache stale?
is registry snapshot immutable?
```

---

# 60. Strong Interview Answer

Question:

```text
How do you make an in-memory service registry thread-safe?
```

Strong answer:

```text
I would use ConcurrentHashMap keyed by service name and a thread-safe list
such as CopyOnWriteArrayList for instances because registry workloads are
read-heavy. I would avoid exposing mutable internal lists, return snapshots,
use atomic fields for heartbeat/status updates, and handle cleanup with
safe remove operations.
```

Senior addition:

```text
For high-scale systems, immutable registry snapshots with atomic swaps can
provide very fast reads, and production systems also handle stale caches,
TTL grace periods, duplicate registrations, and self-preservation during
network partitions.
```

---

# 61. Most Important Insight

```text
Registry correctness depends on safe concurrent access.
```

---

# 62. Final Mental Model

```text
Registry =
shared mutable service map

Thread-safe registry =
safe concurrent lifecycle manager
```

---

# 63. What To Remember

```text
HashMap is unsafe under concurrency.

ArrayList is unsafe under concurrent iteration/update.

ConcurrentHashMap protects serviceName map.

CopyOnWriteArrayList protects read-heavy instance lists.

AtomicLong protects heartbeat timestamp visibility.

Return snapshots, not internal lists.

TTL cleanup must be safe.

Registry is read-heavy.

Thread safety is mandatory for discovery.
```

---

# 64. Next File

```text
010_Heartbeat_Lease_Renewal_TTL.md
```

Next you learn:

```text
heartbeat mechanism
lease renewal
TTL expiration
missed heartbeat handling
dead instance detection
Eureka lease model
production tuning
```
