# 003_Service_Registry_Mental_Model.md

# MiniServiceDiscovery — Service Registry Mental Model

---

# 1. Why This File Exists

Service discovery works because of one central component:

```text
Service Registry
```

Without registry:

```text
services cannot dynamically find each other
```

Registry is the:

```text
source of truth for live service instances
```

This file explains:

```text
what service registry is
what registry stores
how registry internally modeled
how services register
how clients lookup services
why registry is critical
how production registries work
```

---

# 2. One-Line Definition

```text
Service Registry is a dynamic database of healthy service instances.
```

---

# 3. Biggest Mental Model

```text
Service Registry =
Dynamic Phonebook For Microservices
```

---

# 4. Real World Analogy

Phone contacts store:

```text
person name
phone number
```

Service registry stores:

```text
service name
service instances
```

Example:

```text
payment-service
    → 10.0.1.10:8080
    → 10.0.1.11:8080
```

---

# 5. Core Problem Registry Solves

Problem:

```text
service locations constantly change
```

Registry solves this by:

```text
tracking current healthy service instances dynamically
```

---

# 6. Registry Without Discovery

Without registry:

```text
caller must know exact service IP
```

If IP changes:

```text
communication fails
```

---

# 7. Registry With Discovery

With registry:

```text
caller only knows service name
```

Example:

```text
payment-service
```

Registry resolves current healthy instances dynamically.

---

# 8. Service Registry Flow

```text
Service Starts
      ↓
Registers Itself
      ↓
Registry Stores Metadata
      ↓
Client Looks Up Service
      ↓
Registry Returns Healthy Instances
      ↓
Load Balancer Chooses One
```

---

# 9. Registry ASCII Diagram

```text
             ┌─────────────────┐
             │ Service Registry │
             └─────────────────┘
                 ↑         ↑
                 │         │
      register   │         │ lookup
                 │         │
         Payment Service   Order Service
```

---

# 10. What Registry Stores

Registry stores:

```text
service name
instance ID
host
port
status
metadata
heartbeat timestamp
zone
region
version
```

---

# 11. Example Registry Data

```text
payment-service
    → instance-1
        host=10.0.1.10
        port=8080
        status=UP

    → instance-2
        host=10.0.1.11
        port=8080
        status=UP
```

---

# 12. Why Multiple Instances Exist

Production systems run multiple instances for:

```text
high availability
fault tolerance
load balancing
autoscaling
rolling deployment
```

Registry tracks all healthy instances.

---

# 13. Registry Mental Model

```text
Registry =
live map of service name → healthy instances
```

---

# 14. Core Internal Data Structure

Internally registry is often modeled as:

```java
ConcurrentHashMap<
    String,
    List<ServiceInstance>
>
```

Where:

```text
key = service name
value = service instances
```

---

# 15. Internal Registry Example

```text
payment-service
    → payment-instance-1
    → payment-instance-2

inventory-service
    → inventory-instance-1
```

---

# 16. ServiceInstance Mental Model

Every service instance is represented as an object.

Example:

```java
public class ServiceInstance {

    private String serviceName;

    private String instanceId;

    private String host;

    private int port;

    private String status;

    private long lastHeartbeatTime;
}
```

---

# 17. Why Instance ID Needed

Multiple instances may exist.

Need unique identity.

Example:

```text
payment-service-1
payment-service-2
payment-service-3
```

---

# 18. Why Status Needed

Registry must know:

```text
is instance healthy?
```

Possible states:

```text
UP
DOWN
STARTING
OUT_OF_SERVICE
UNKNOWN
```

---

# 19. Why Heartbeat Timestamp Needed

Registry must detect:

```text
dead instances
```

Heartbeat timestamp helps determine:

```text
last known alive time
```

---

# 20. Why Metadata Needed

Metadata enables:

```text
version routing
zone-aware routing
canary deployment
blue-green deployment
weighted balancing
protocol handling
```

---

# 21. Metadata Example

```text
version=v2
zone=eu-west
weight=5
protocol=https
```

---

# 22. Service Registration Flow

When service starts:

```text
service sends registration request
```

Registry stores:

```text
service metadata
```

---

# 23. Registration ASCII

```text
Payment Service Starts
         ↓
Register(payment-service)
         ↓
Registry Adds Instance
```

---

# 24. Registration Example

Input:

```text
serviceName = payment-service
host = 10.0.1.10
port = 8080
```

Registry after registration:

```text
payment-service
    → 10.0.1.10:8080
```

---

# 25. Service Lookup Flow

Client asks registry:

```text
lookup(payment-service)
```

Registry returns:

```text
healthy payment instances
```

---

# 26. Lookup ASCII

```text
Order Service
      ↓ lookup(payment-service)
Registry
      ↓
[payment1, payment2, payment3]
```

---

# 27. Deregistration Flow

When service shuts down gracefully:

```text
service deregisters itself
```

Registry removes instance.

---

# 28. Deregistration ASCII

```text
Payment Service Stops
         ↓
Deregister Instance
         ↓
Registry Removes Instance
```

---

# 29. Heartbeat Flow

Services periodically send:

```text
heartbeat
```

Meaning:

```text
I'm still alive
```

Registry updates:

```text
lastHeartbeatTime
```

---

# 30. Heartbeat ASCII

```text
Payment Service
      ↓ heartbeat
Registry
      ↓ update timestamp
```

---

# 31. Registry Cleanup Problem

What if service crashes without deregistering?

Need automatic cleanup.

Registry uses:

```text
TTL expiration
heartbeat timeout
cleanup scanner
```

---

# 32. TTL Mental Model

```text
No heartbeat within TTL?
Instance considered dead.
```

---

# 33. Registry as Source Of Truth

Registry becomes:

```text
central live knowledge of system topology
```

Meaning registry knows:

```text
which services exist
which instances healthy
which instances dead
```

---

# 34. Why Thread Safety Important

Many operations happen concurrently:

```text
registration
lookup
heartbeat
cleanup
deregistration
```

Need thread-safe data structures.

---

# 35. Concurrent Operations Example

```text
1000 clients doing lookup
50 services registering
50 services heartbeating
cleanup thread removing dead instances
```

All simultaneously.

---

# 36. Thread-Safe Registry Structure

Example:

```java
private final ConcurrentHashMap<
        String,
        CopyOnWriteArrayList<ServiceInstance>
> registry = new ConcurrentHashMap<>();
```

---

# 37. Why ConcurrentHashMap Used

Need:

```text
concurrent reads
concurrent writes
safe updates
high throughput
```

HashMap alone unsafe.

---

# 38. Why Lookup Must Be Fast

Every service call may need lookup.

Registry becomes hot path.

Lookup should be:

```text
very low latency
```

Usually:

```text
O(1) hashmap access
```

---

# 39. Registry Read vs Write Pattern

Registry workloads are mostly:

```text
many reads
few writes
```

Example:

```text
millions of lookups
few registrations per second
```

---

# 40. Why Registry Caching Exists

Clients often cache registry locally.

Benefits:

```text
lower latency
reduced registry load
better availability
```

---

# 41. Cached Registry Mental Model

```text
registry acts like source of truth

clients maintain local snapshot
```

---

# 42. Registry Replication Problem

Registry itself can fail.

Need multiple registry nodes.

---

# 43. Replication ASCII

```text
Registry-A ↔ Registry-B ↔ Registry-C
```

---

# 44. Distributed Systems Problem Begins Here

At scale registry becomes distributed systems problem.

Need handling for:

```text
network partitions
stale registry
replication lag
split brain
eventual consistency
```

---

# 45. Stale Registry Problem

Registry says:

```text
instance alive
```

But actual instance already dead.

Clients receive stale instance list.

---

# 46. Split Brain Problem

Two registry groups both believe:

```text
I am correct registry
```

Dangerous inconsistency.

---

# 47. CAP Tradeoff

Registry systems must choose tradeoffs between:

```text
Consistency
Availability
Partition tolerance
```

---

# 48. Eureka Mental Model

Eureka prefers:

```text
availability
```

Even if data becomes slightly stale.

---

# 49. Zookeeper Mental Model

Zookeeper prefers:

```text
strong consistency
```

Even if availability reduced during partition.

---

# 50. Kubernetes Registry Model

Kubernetes uses:

```text
API Server
Etcd
CoreDNS
EndpointSlices
```

instead of classic Eureka registry.

---

# 51. Service Discovery + Load Balancing

Registry answers:

```text
WHERE are instances?
```

Load balancer answers:

```text
WHICH instance should receive request?
```

---

# 52. Discovery + LB ASCII

```text
Client
   ↓
Registry Lookup
   ↓
Healthy Instances
   ↓
Load Balancer
   ↓
Selected Instance
```

---

# 53. Registry + Circuit Breaker

Registry provides:

```text
instance list
```

Circuit breaker protects against:

```text
slow
failing
overloaded
instances
```

---

# 54. Production Architecture Example

```text
Order Service
      ↓
Registry
      ↓
Load Balancer
      ↓
Circuit Breaker
      ↓
Payment Service
```

---

# 55. Dry Run — Registration

Input:

```text
payment-service
10.0.1.10
8080
```

Registry after registration:

```text
payment-service
    → 10.0.1.10:8080
```

---

# 56. Dry Run — Scaling

More instances added:

```text
10.0.1.11
10.0.1.12
```

Registry becomes:

```text
payment-service
    → 10.0.1.10
    → 10.0.1.11
    → 10.0.1.12
```

---

# 57. Dry Run — Lookup

Order service asks:

```text
lookup(payment-service)
```

Registry returns:

```text
[payment1, payment2, payment3]
```

---

# 58. Dry Run — Dead Instance

Payment2 stops sending heartbeat.

TTL expires.

Cleanup removes:

```text
payment2
```

Registry becomes:

```text
[payment1, payment3]
```

---

# 59. Strong Interview Answer

Question:

```text
What is service registry?
```

Strong answer:

```text
A service registry is a dynamic database of healthy service instances.
Services register themselves with metadata and periodically send heartbeats.
Clients query the registry to discover healthy instances dynamically instead
of using hardcoded IP addresses.
```

Senior addition:

```text
At scale, registry systems become distributed systems themselves and must
handle replication, stale data, network partitions, eventual consistency,
and CAP theorem tradeoffs.
```

---

# 60. Most Important Insight

```text
Registry transforms infrastructure knowledge into a dynamic queryable system.
```

---

# 61. Final Mental Model

```text
Registry =
Dynamic Service Database
+
Health Tracker
+
Instance Directory
+
Routing Source
for microservices
```

---

# 62. What To Remember

```text
Registry stores healthy instances.

Services dynamically register.

Clients dynamically lookup.

Heartbeats maintain liveness.

TTL removes dead instances.

Registry usually backed by hashmap-like structures.

Reads dominate writes.

Thread safety critical.

Registry becomes distributed systems problem at scale.
```

---

# 63. Next File

```text
004_Client_Side_vs_Server_Side_Discovery.md
```

Next you learn:

```text
how client-side discovery works
how server-side discovery works
Ribbon/OpenFeign routing
Kubernetes Service routing
tradeoffs between both approaches
```
