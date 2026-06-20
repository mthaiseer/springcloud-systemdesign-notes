# 007_Service_Registration_Lookup_Deregistration.md

# MiniServiceDiscovery — 007 Service Registration, Lookup, Deregistration

---

# 1. Why This File Exists

Service discovery is not just:

```text
store service name and IP
```

It is a lifecycle system for service instances.

Every service instance goes through:

```text
register
heartbeat
lookup
serve traffic
deregister
expire if dead
```

The three most important registry operations are:

```text
REGISTER
LOOKUP
DEREGISTER
```

Without these operations:

```text
new services cannot join
clients cannot discover services
dead instances stay forever
traffic routes to wrong targets
```

This file explains:

```text
service registration
service lookup
service deregistration
heartbeat connection
TTL cleanup connection
thread-safe registry update
duplicate registration handling
Eureka lifecycle mapping
Kubernetes lifecycle mapping
Java implementation
dry runs
production failure scenarios
interview explanation
```

---

# 2. One-Line Definition

```text
Registration adds an instance, lookup discovers healthy instances, and deregistration removes an instance from routing.
```

---

# 3. Biggest Mental Model

```text
Registration
=
join the system

Lookup
=
find who is available

Deregistration
=
leave the system safely
```

---

# 4. Full Service Instance Lifecycle

```text
Service Starts
      ↓
Registers Itself
      ↓
Registry Stores Instance
      ↓
Heartbeat Keeps Lease Alive
      ↓
Clients Lookup Service
      ↓
Load Balancer Selects Instance
      ↓
Instance Receives Traffic
      ↓
Service Shuts Down
      ↓
Deregistration Removes Instance
```

If service crashes:

```text
no deregistration
      ↓
heartbeat stops
      ↓
TTL expires
      ↓
cleanup removes instance
```

---

# 5. Lifecycle ASCII

```text
STARTING
   ↓ register
REGISTERED
   ↓ heartbeat
UP
   ↓ lookup + traffic
SERVING
   ↓ graceful shutdown
DEREGISTERED

OR

UP
   ↓ crash
NO HEARTBEAT
   ↓ TTL expires
REMOVED
```

---

# 6. Core Registry Data Structure

At mini level:

```java
ConcurrentHashMap<
    String,
    CopyOnWriteArrayList<ServiceInstance>
>
```

Meaning:

```text
serviceName → list of running instances
```

Example:

```text
payment-service
    → payment-1
    → payment-2

inventory-service
    → inventory-1
```

---

# 7. Why Registry Is Read-Heavy

Service registration happens:

```text
occasionally
```

Service lookup happens:

```text
very frequently
```

Example:

```text
100 registrations/min
1,000,000 lookups/min
```

So registry must optimize for:

```text
fast lookup
safe concurrent reads
low latency
```

---

# 8. Operation 1 — Registration

Registration means:

```text
a service instance announces itself to registry
```

The service says:

```text
I am payment-service.
My instance ID is payment-1.
My address is 10.0.1.10:8080.
My status is UP.
```

---

# 9. Registration Request

Example JSON:

```json
{
  "serviceName": "payment-service",
  "instanceId": "payment-1",
  "host": "10.0.1.10",
  "port": 8080,
  "status": "UP",
  "metadata": {
    "version": "v1",
    "zone": "eu-west",
    "protocol": "http"
  }
}
```

---

# 10. Registration Flow

```text
Service Boots
      ↓
Server Port Opens
      ↓
Service Builds Instance Metadata
      ↓
Service Sends Register Request
      ↓
Registry Validates Request
      ↓
Registry Stores Instance
      ↓
Heartbeat Starts
```

---

# 11. Registration ASCII

```text
Payment Service
      ↓ register(payment-1)
Registry
      ↓
payment-service → [payment-1]
```

---

# 12. Why Registration Must Happen After Server Is Ready

Bad order:

```text
register first
then start server
```

Problem:

```text
clients may call instance before it can serve traffic
```

Better order:

```text
start server
pass readiness check
register
```

---

# 13. Registration Readiness Mental Model

```text
Do not enter phonebook before you can answer calls.
```

---

# 14. ServiceInstance Java Model

```java
import java.util.Map;

public class ServiceInstance {

    private final String serviceName;

    private final String instanceId;

    private final String host;

    private final int port;

    private volatile InstanceStatus status;

    private volatile long lastHeartbeatTime;

    private final Map<String, String> metadata;

    public ServiceInstance(
            String serviceName,
            String instanceId,
            String host,
            int port,
            InstanceStatus status,
            long lastHeartbeatTime,
            Map<String, String> metadata) {

        this.serviceName = serviceName;
        this.instanceId = instanceId;
        this.host = host;
        this.port = port;
        this.status = status;
        this.lastHeartbeatTime = lastHeartbeatTime;
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
        return status;
    }

    public void setStatus(
            InstanceStatus status) {
        this.status = status;
    }

    public long getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public void updateHeartbeat(
            long now) {
        this.lastHeartbeatTime = now;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
```

---

# 15. InstanceStatus Enum

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

# 16. Registry Class Skeleton

```java
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceRegistry {

    private final ConcurrentHashMap<
            String,
            CopyOnWriteArrayList<ServiceInstance>
    > registry = new ConcurrentHashMap<>();

}
```

---

# 17. Registration Code

```java
public void register(
        ServiceInstance instance) {

    registry
        .computeIfAbsent(
                instance.getServiceName(),
                serviceName ->
                        new CopyOnWriteArrayList<>()
        )
        .add(instance);
}
```

---

# 18. computeIfAbsent Mental Model

```text
If serviceName does not exist,
create empty list.

Then add instance to list.
```

Example:

```text
payment-service missing
      ↓
create list
      ↓
add payment-1
```

---

# 19. Registration Dry Run — First Instance

Input:

```text
payment-service
payment-1
10.0.1.10:8080
```

Before:

```text
registry = {}
```

After:

```text
payment-service
    → payment-1
```

---

# 20. Registration Dry Run — Scaling

New instance:

```text
payment-2
10.0.1.11:8080
```

Before:

```text
payment-service
    → payment-1
```

After:

```text
payment-service
    → payment-1
    → payment-2
```

---

# 21. Duplicate Registration Problem

What if same instance registers twice?

Bad result:

```text
payment-service
    → payment-1
    → payment-1
```

This causes:

```text
double traffic
incorrect load balancing
wrong metrics
```

---

# 22. Safer Registration Code

```java
public void registerSafely(
        ServiceInstance instance) {

    CopyOnWriteArrayList<ServiceInstance> instances =
            registry.computeIfAbsent(
                    instance.getServiceName(),
                    key -> new CopyOnWriteArrayList<>()
            );

    boolean alreadyExists =
            instances.stream()
                    .anyMatch(existing ->
                            existing.getInstanceId()
                                    .equals(instance.getInstanceId())
                    );

    if (!alreadyExists) {
        instances.add(instance);
    }
}
```

---

# 23. Registration Validation

Production registry may validate:

```text
serviceName not empty
instanceId unique
host valid
port valid
metadata allowed
service identity trusted
```

---

# 24. Registration Security

Without security, malicious service could register as:

```text
payment-service
```

and receive traffic.

Production systems use:

```text
mTLS
tokens
service identity
Kubernetes service accounts
ACL policies
```

---

# 25. Registration Metadata

Registration often includes:

```text
version
zone
region
protocol
weight
environment
build number
```

This enables intelligent routing.

---

# 26. Operation 2 — Lookup

Lookup means:

```text
client asks registry for instances of a service
```

Client asks:

```text
Where is payment-service?
```

Registry responds:

```text
[payment-1, payment-2, payment-3]
```

---

# 27. Lookup Flow

```text
Client Sends Lookup(serviceName)
      ↓
Registry Finds Instance List
      ↓
Registry Filters Healthy Instances
      ↓
Registry Returns Result
      ↓
Client/Proxy Load Balances
```

---

# 28. Lookup ASCII

```text
Order Service
      ↓ lookup(payment-service)
Registry
      ↓
[payment-1, payment-2, payment-3]
```

---

# 29. Basic Lookup Code

```java
public List<ServiceInstance> lookup(
        String serviceName) {

    return registry.getOrDefault(
            serviceName,
            new CopyOnWriteArrayList<>()
    );
}
```

---

# 30. Problem With Basic Lookup

Basic lookup may return:

```text
DOWN instances
expired instances
OUT_OF_SERVICE instances
```

Need filtering.

---

# 31. Healthy Lookup Code

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

# 32. Lookup Dry Run — Healthy Filtering

Registry:

```text
payment-1 → UP
payment-2 → DOWN
payment-3 → UP
```

Lookup:

```text
lookupHealthy(payment-service)
```

Returns:

```text
payment-1
payment-3
```

---

# 33. Lookup + Load Balancer

Registry returns:

```text
healthy instance list
```

Load balancer chooses:

```text
one instance
```

Example:

```text
Round Robin selects payment-2
```

---

# 34. Lookup + Load Balancer ASCII

```text
Registry
   ↓
[payment1, payment2, payment3]
   ↓
Round Robin
   ↓
payment2
```

---

# 35. Why Lookup Must Be Fast

Lookup may happen on every service call.

If lookup slow:

```text
all microservice calls become slow
```

So registry lookup must be:

```text
fast
cached
low-latency
thread-safe
```

---

# 36. Lookup Complexity

With hashmap:

```text
serviceName lookup ≈ O(1)
```

Filtering:

```text
O(number of instances for that service)
```

Usually small.

---

# 37. Client-Side Discovery Lookup

In Eureka-like systems:

```text
client fetches registry snapshot
client stores local cache
client performs lookup locally
```

This reduces registry load.

---

# 38. Server-Side Discovery Lookup

In Kubernetes-like systems:

```text
client calls service DNS name
infrastructure resolves/routs
```

Client does not manually fetch registry.

---

# 39. Cached Lookup Mental Model

```text
Registry =
source of truth

Client cache =
local snapshot
```

---

# 40. Cache Benefit

Client cache gives:

```text
lower latency
lower registry traffic
better resilience if registry temporarily down
```

---

# 41. Cache Problem

Cache may be stale.

Client may call:

```text
dead instance
```

Need:

```text
refresh interval
health checks
retry
circuit breaker
TTL
```

---

# 42. Operation 3 — Deregistration

Deregistration means:

```text
service voluntarily removes itself from registry
```

Service says:

```text
I am shutting down.
Remove me from routing.
```

---

# 43. Why Deregistration Important

Without deregistration:

```text
clients may keep routing traffic to shutting-down instance
```

This causes:

```text
connection refused
timeouts
partial failures
```

---

# 44. Graceful Shutdown Flow

```text
receive shutdown signal
      ↓
stop accepting new traffic
      ↓
deregister from registry
      ↓
finish in-flight requests
      ↓
exit process
```

---

# 45. Deregistration ASCII

```text
Payment Service
      ↓ deregister(payment-1)
Registry
      ↓
remove payment-1
```

---

# 46. Deregistration Code

```java
public void deregister(
        String serviceName,
        String instanceId) {

    List<ServiceInstance> instances =
            registry.get(serviceName);

    if (instances == null) {
        return;
    }

    instances.removeIf(instance ->
            instance.getInstanceId()
                    .equals(instanceId)
    );
}
```

---

# 47. Deregistration Dry Run

Before:

```text
payment-service
    → payment-1
    → payment-2
```

Deregister:

```text
payment-1
```

After:

```text
payment-service
    → payment-2
```

---

# 48. Empty Service Cleanup

After removing last instance:

```text
payment-service → []
```

Registry may remove service key entirely.

---

# 49. Empty Cleanup Code

```java
public void deregisterAndCleanup(
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
        registry.remove(serviceName);
    }
}
```

---

# 50. Crash Without Deregistration

If instance crashes:

```text
no deregistration request sent
```

Registry still contains stale entry.

Need:

```text
heartbeat + TTL cleanup
```

---

# 51. TTL Cleanup Flow

```text
instance crashes
      ↓
heartbeat stops
      ↓
TTL expires
      ↓
cleanup scanner removes instance
```

---

# 52. Cleanup Scanner Code

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
            registry.remove(serviceName);
        }
    }
}
```

---

# 53. Crash Dry Run

Before:

```text
payment-service
    → payment-1
```

payment-1 crashes.

No deregistration.

After TTL expires:

```text
cleanup removes payment-1
```

Registry:

```text
empty
```

---

# 54. Registration + Heartbeat Relationship

Registration says:

```text
I exist
```

Heartbeat says:

```text
I still exist
```

Deregistration says:

```text
I no longer exist
```

---

# 55. Eureka Lifecycle Mapping

Eureka clients:

```text
register on startup
renew lease using heartbeat
fetch registry for lookup
deregister on shutdown
```

Eureka server:

```text
stores InstanceInfo
tracks lease
evicts expired instances
```

---

# 56. Eureka ASCII

```text
Eureka Client
    ↓ register
Eureka Server
    ↓ stores InstanceInfo

Eureka Client
    ↓ heartbeat / renew
Eureka Server
    ↓ update lease timestamp
```

---

# 57. Kubernetes Lifecycle Mapping

Kubernetes does not usually require app-level registration.

Instead:

```text
Pod created
      ↓
Kubernetes API updates EndpointSlice
      ↓
CoreDNS resolves service
      ↓
traffic routed to pod
```

---

# 58. Kubernetes ASCII

```text
Pod Created
   ↓
EndpointSlice Updated
   ↓
Service DNS Works
   ↓
Traffic Routed
```

---

# 59. Client-Side vs Server-Side Lifecycle

## Client-Side

```text
client fetches registry and chooses instance
```

## Server-Side

```text
infrastructure discovers and routes
```

---

# 60. Thread Safety Challenge

These operations happen concurrently:

```text
register
lookup
heartbeat
deregister
cleanup
```

Race conditions possible.

---

# 61. Race Example

Cleanup removes instance while lookup is reading.

Need:

```text
thread-safe collections
immutable snapshots
safe iteration
```

---

# 62. Safe Snapshot Lookup

```java
public List<ServiceInstance> lookupSnapshot(
        String serviceName) {

    List<ServiceInstance> instances =
            registry.getOrDefault(
                    serviceName,
                    new CopyOnWriteArrayList<>()
            );

    return List.copyOf(instances);
}
```

---

# 63. Why Return Copy

If caller receives internal list directly:

```text
caller may accidentally modify registry
```

Return copy protects registry state.

---

# 64. Full Mini Registry Code

```java
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MiniServiceRegistry {

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
            registry.remove(serviceName);
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
}
```

---

# 65. Full Lifecycle Dry Run

## Step 1 — Register payment-1

```text
register(payment-1)
```

Registry:

```text
payment-service
    → payment-1
```

---

# 66. Step 2 — Lookup payment-service

```text
lookupHealthy(payment-service)
```

Returns:

```text
[payment-1]
```

---

# 67. Step 3 — Autoscaling Adds payment-2

```text
register(payment-2)
```

Registry:

```text
payment-service
    → payment-1
    → payment-2
```

---

# 68. Step 4 — payment-1 Graceful Shutdown

```text
deregister(payment-1)
```

Registry:

```text
payment-service
    → payment-2
```

---

# 69. Step 5 — payment-2 Crashes

No deregistration.

Heartbeat stops.

TTL cleanup eventually removes:

```text
payment-2
```

Registry becomes:

```text
empty
```

---

# 70. Common Mistakes

## Mistake 1

```text
registering before app is ready
```

Traffic may hit unready instance.

---

## Mistake 2

```text
not handling duplicate registration
```

Same instance may receive extra traffic.

---

## Mistake 3

```text
returning DOWN instances in lookup
```

Causes avoidable failures.

---

## Mistake 4

```text
not deregistering during graceful shutdown
```

Traffic may route to shutting-down service.

---

## Mistake 5

```text
no TTL cleanup
```

Crashed instances stay forever.

---

## Mistake 6

```text
exposing internal mutable registry list
```

External caller may corrupt registry.

---

# 71. Production Debugging Questions

If service call fails, ask:

```text
is instance registered?
is instance UP?
is registry stale?
is client cache stale?
did heartbeat stop?
did deregistration happen?
did TTL cleanup remove instance?
```

---

# 72. Strong Interview Answer

Question:

```text
How does service registration and lookup work?
```

Strong answer:

```text
When a service starts, it registers its instance metadata such as service
name, instance ID, host, port, health status, and metadata with a registry.
Clients then lookup the service name to retrieve healthy instances and use
a load balancer to select one. During shutdown the service deregisters, and
if it crashes unexpectedly, heartbeat TTL cleanup removes the stale entry.
```

Senior addition:

```text
Production systems must handle duplicate registration, stale client cache,
thread-safe concurrent updates, health-aware filtering, secure registration,
and different lifecycle models such as Eureka lease renewal or Kubernetes
EndpointSlice updates.
```

---

# 73. Most Important Insight

```text
Service discovery is lifecycle management for service instances.
```

---

# 74. Final Mental Model

```text
REGISTER
=
I joined

HEARTBEAT
=
I am alive

LOOKUP
=
Who is available?

DEREGISTER
=
I am leaving

TTL CLEANUP
=
You disappeared
```

---

# 75. What To Remember

```text
Registration adds instance.

Lookup returns healthy instances.

Deregistration removes graceful shutdowns.

Heartbeat keeps instance alive.

TTL cleanup removes crashed instances.

Lookup must be fast.

Registry is read-heavy.

Thread safety is mandatory.

Production registries must prevent stale routing.
```

---

# 76. Next File

```text
008_Instance_Metadata_And_Health_Status.md
```

Next you learn:

```text
how metadata enables intelligent routing
how health status controls traffic eligibility
zone-aware routing
canary routing
weighted routing
health-aware discovery
```
