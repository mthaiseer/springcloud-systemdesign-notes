# 000_Index.md

# MiniServiceDiscovery — Complete Learning Roadmap

---

# 1. What You Are Building

You are NOT building:

```text
just another Spring Cloud Eureka tutorial
```

You ARE building:

```text
a mini internal model of Eureka / Consul / Kubernetes Service Discovery
```

Same style as:

```text
MiniRedis
MiniKafka
MiniCircuitBreaker
MiniSearchEngine
MiniGateway
```

Goal:

```text
understand how services dynamically find each other in distributed systems
```

---

# 2. Biggest Mental Model

```text
Service Discovery =
Dynamic Phonebook For Microservices
```

Instead of hardcoding:

```text
http://10.0.1.25:8080
```

a service asks:

```text
Where is payment-service?
```

Registry answers:

```text
payment-service instance-1
payment-service instance-2
payment-service instance-3
```

---

# 3. Why Service Discovery Exists

In production, service instances constantly:

```text
start
stop
crash
restart
scale up
scale down
move to another node
get new IP address
```

So fixed IP addresses fail.

---

# 4. Without Service Discovery

```text
order-service
      ↓
http://10.0.1.25:8080/payment
```

Problem:

```text
payment-service restarts
IP changes
order-service still calls old IP
request fails
```

---

# 5. With Service Discovery

```text
order-service
      ↓
service registry
      ↓
payment-service instances
      ↓
choose healthy instance
      ↓
make request
```

---

# 6. Real World Analogy

```text
Service Discovery = Google Maps for microservices
```

You do not memorize every shop address.

You search:

```text
nearest coffee shop
```

Similarly, services search:

```text
nearest healthy payment-service instance
```

---

# 7. What This Mini Teaches

This mini teaches:

```text
service registry internals
service instance model
registration
deregistration
heartbeat
TTL
lease renewal
dead instance removal
client-side discovery
server-side discovery
load balancing
health checks
network partitions
stale registry
split brain
eventual consistency
CAP tradeoffs
registry replication
Eureka internals
Consul/Zookeeper comparison
Kubernetes DNS discovery
service mesh discovery
observability
production architecture
```

---

# 8. Learning Philosophy

Every file follows this pattern:

```text
WHY?
WHAT PROBLEM?
MENTAL MODEL
INTERNAL DESIGN
JAVA CODE
DRY RUN
WHAT BREAKS?
PRODUCTION HANDLING
INTERVIEW EXPLANATION
```

---

# 9. Complete Learning Flow

```text
Foundation
   ↓
Registry Engine
   ↓
Heartbeat + TTL
   ↓
Load Balancing
   ↓
Distributed Systems Failure Modes
   ↓
Registry Replication
   ↓
Spring Cloud + Kubernetes
   ↓
Observability
   ↓
Production Architecture
   ↓
Final CheatSheet
```

---

# 10. Complete MiniServiceDiscovery Structure

```text
MiniServiceDiscovery/
├── 000_Index.md
│
├── FOUNDATION
│   ├── 001_What_Is_Service_Discovery.md
│   ├── 002_Why_Hardcoded_IPs_Fail.md
│   ├── 003_Service_Registry_Mental_Model.md
│   ├── 004_Client_Side_vs_Server_Side_Discovery.md
│   └── 005_Eureka_Consul_Zookeeper_K8s_Comparison.md
│
├── CORE REGISTRY ENGINE
│   ├── 006_Service_Instance_Model.md
│   ├── 007_Service_Registration.md
│   ├── 008_Instance_Metadata.md
│   ├── 009_InMemory_Service_Registry.md
│   ├── 010_Thread_Safe_Registry_ConcurrentHashMap.md
│   ├── 011_Service_Lookup.md
│   └── 012_Service_Deregistration.md
│
├── HEARTBEAT + TTL
│   ├── 013_Heartbeat_Mechanism.md
│   ├── 014_Lease_Renewal_Model.md
│   ├── 015_TTL_Expiration.md
│   ├── 016_Dead_Instance_Removal.md
│   └── 017_Eureka_Self_Preservation_Mode.md
│
├── LOAD BALANCING
│   ├── 018_RoundRobin_LoadBalancing.md
│   ├── 019_Weighted_LoadBalancing.md
│   ├── 020_LeastConnections_LoadBalancing.md
│   ├── 021_Health_Aware_LoadBalancing.md
│   └── 022_Client_Side_LoadBalancing.md
│
├── DISTRIBUTED SYSTEMS PROBLEMS
│   ├── 023_Network_Partition_Problem.md
│   ├── 024_Stale_Registry_Problem.md
│   ├── 025_Split_Brain_Problem.md
│   ├── 026_Eventual_Consistency_Registry.md
│   └── 027_CAP_Tradeoff_Service_Discovery.md
│
├── REGISTRY REPLICATION
│   ├── 028_Registry_Replication.md
│   ├── 029_Peer_To_Peer_Replication.md
│   ├── 030_Leader_Follower_Registry.md
│   └── 031_Consistency_vs_Availability.md
│
├── SPRING CLOUD + KUBERNETES
│   ├── 032_SpringCloud_Eureka_Internals.md
│   ├── 033_SpringCloud_LoadBalancer_OpenFeign.md
│   ├── 034_Kubernetes_Service_Discovery.md
│   ├── 035_DNS_Based_Discovery.md
│   └── 036_Service_Mesh_Discovery_Envoy_Istio.md
│
├── OBSERVABILITY
│   ├── 037_Health_Checks_And_Readiness.md
│   ├── 038_Registry_Metrics_Monitoring.md
│   └── 039_Tracing_Service_To_Service.md
│
├── PRODUCTION
│   └── 040_Production_Grade_ServiceDiscovery.md
│
└── 099_ServiceDiscovery_Final_CheatSheet.md
```

---

# 11. Layer 1 — Foundation

Purpose:

```text
understand why service discovery exists
```

You learn:

```text
hardcoded IP problem
dynamic instance problem
registry concept
client-side discovery
server-side discovery
Eureka vs Consul vs Zookeeper vs Kubernetes
```

---

# 12. Layer 2 — Core Registry Engine

Purpose:

```text
build mini in-memory registry
```

Core data structure:

```text
ConcurrentHashMap<
    serviceName,
    List<ServiceInstance>
>
```

Example:

```text
payment-service
    → 10.0.1.10:8080
    → 10.0.1.11:8080
    → 10.0.1.12:8080
```

---

# 13. Core Registry Operations

Registry supports:

```text
register service
lookup service
remove service
update metadata
list instances
```

---

# 14. ServiceInstance Mental Model

A service instance contains:

```text
serviceName
instanceId
host
port
status
metadata
lastHeartbeatTime
```

---

# 15. Layer 3 — Heartbeat + TTL

Purpose:

```text
detect dead instances
```

---

# 16. Heartbeat Mental Model

```text
Service says:
"I am alive"
```

every few seconds.

---

# 17. TTL Mental Model

```text
No heartbeat within TTL?
Instance considered dead.
```

Example:

```text
heartbeat every 10 sec
TTL = 30 sec
miss 3 heartbeats
remove instance
```

---

# 18. Lease Renewal Mental Model

Eureka uses leases.

Service does not register once forever.

It keeps renewing:

```text
lease
```

through heartbeats.

---

# 19. Dead Instance Removal

Registry periodically scans:

```text
lastHeartbeatTime
```

If expired:

```text
remove instance
```

---

# 20. Self Preservation Mental Model

If many heartbeats suddenly disappear:

```text
maybe network issue
not actual service death
```

So Eureka avoids deleting many instances immediately.

---

# 21. Layer 4 — Load Balancing

Purpose:

```text
choose one instance among many
```

Registry may return:

```text
payment-service has 5 healthy instances
```

Client must choose one.

---

# 22. Load Balancing Algorithms

You learn:

```text
Round Robin
Weighted Round Robin
Least Connections
Health-Aware Load Balancing
Client-Side Load Balancing
```

---

# 23. Round Robin Mental Model

```text
instance1
instance2
instance3
instance1
instance2
instance3
```

Simple fair rotation.

---

# 24. Weighted Load Balancing Mental Model

Powerful instance gets more traffic.

Example:

```text
instance A weight 3
instance B weight 1

A gets 3x traffic
```

---

# 25. Least Connections Mental Model

Send traffic to instance with:

```text
fewest active requests
```

---

# 26. Health-Aware Load Balancing

Avoid instances with:

```text
high latency
high error rate
unhealthy status
```

---

# 27. Layer 5 — Distributed Systems Problems

Purpose:

```text
understand why service discovery is hard
```

Problems:

```text
network partition
stale registry
split brain
eventual consistency
CAP tradeoff
```

---

# 28. Network Partition Mental Model

```text
Registry nodes cannot talk to each other.
```

Now each node may have different view of services.

---

# 29. Stale Registry Problem

Registry says instance alive.

But actually:

```text
instance already dead
```

Client gets stale data.

---

# 30. Split Brain Mental Model

Two registry groups both believe:

```text
I am correct registry
```

Dangerous in distributed systems.

---

# 31. Eventual Consistency Mental Model

```text
Registry copies may differ briefly
but converge later.
```

---

# 32. CAP Tradeoff

Service discovery must choose between:

```text
Consistency
Availability
Partition tolerance
```

During network partitions:

```text
cannot have perfect consistency and perfect availability together
```

---

# 33. Eureka CAP Mental Model

Eureka is closer to:

```text
AP
```

It prefers:

```text
availability
```

even if data can become slightly stale.

---

# 34. Zookeeper CAP Mental Model

Zookeeper is closer to:

```text
CP
```

It prefers:

```text
consistency
```

even if availability reduces during partition.

---

# 35. Consul Mental Model

Consul focuses strongly on:

```text
health checks
KV store
service discovery
more coordinated consistency model
```

---

# 36. Kubernetes Mental Model

Kubernetes service discovery mostly uses:

```text
DNS + Service abstraction
```

Service name resolves to cluster IP / endpoints.

---

# 37. Layer 6 — Registry Replication

Purpose:

```text
make registry highly available
```

Single registry server is a SPOF.

Need:

```text
multiple registry nodes
```

---

# 38. Replication Mental Model

```text
Registry node A
      ↔
Registry node B
      ↔
Registry node C
```

Each node shares instance data.

---

# 39. Peer-To-Peer Replication

Eureka-like model:

```text
registry nodes replicate to peers
```

No strict leader required.

---

# 40. Leader-Follower Registry

Some systems use:

```text
leader handles writes
followers replicate
```

Better consistency but lower availability during leader failure.

---

# 41. Layer 7 — Spring Cloud + Kubernetes

Purpose:

```text
connect internals to real frameworks
```

You learn:

```text
Eureka server
Eureka client
OpenFeign
Spring Cloud LoadBalancer
Kubernetes Service
CoreDNS
Envoy/Istio discovery
```

---

# 42. Spring Cloud Eureka Flow

```text
service starts
   ↓
registers with Eureka
   ↓
sends heartbeat
   ↓
client fetches registry
   ↓
client-side load balancing
```

---

# 43. OpenFeign Mental Model

Feign hides HTTP calls behind interface.

Service discovery resolves:

```text
service name → actual instance
```

---

# 44. Kubernetes DNS Mental Model

Inside cluster:

```text
http://payment-service
```

resolves using Kubernetes DNS.

---

# 45. Service Mesh Mental Model

Service mesh adds:

```text
sidecar proxy
traffic routing
mTLS
observability
retries
circuit breaking
```

Examples:

```text
Istio
Envoy
Linkerd
```

---

# 46. Layer 8 — Observability

Purpose:

```text
see registry health in production
```

Monitor:

```text
registered instance count
heartbeat failures
stale instances
lookup latency
registry replication lag
unhealthy instances
```

---

# 47. Health Checks

Health checks verify:

```text
is instance really usable?
```

Not only:

```text
process alive
```

---

# 48. Readiness vs Liveness

## Liveness

```text
is process alive?
```

## Readiness

```text
can it receive traffic?
```

Readiness is more important for discovery.

---

# 49. Layer 9 — Production Grade

Purpose:

```text
operate service discovery at scale
```

Covers:

```text
multi-zone deployment
multi-region discovery
registry failover
self-preservation
stale data handling
security
mTLS
observability
disaster recovery
```

---

# 50. Production Service Discovery Stack

```text
Service Registry
    +
Health Checks
    +
Load Balancer
    +
DNS
    +
Service Mesh
    +
Observability
```

---

# 51. What You Should Be Able To Explain

After this mini, you should explain:

```text
why hardcoded IPs fail
how service registration works
how heartbeats detect failures
how TTL removes dead instances
how client-side load balancing works
why stale registry happens
why Eureka prefers availability
how Kubernetes DNS discovery works
how service mesh improves discovery
how to monitor registry health
```

---

# 52. Core Java Implementation Direction

You will build small Java models for:

```text
ServiceInstance
ServiceRegistry
ConcurrentHashMapRegistry
HeartbeatManager
TTLScanner
RoundRobinLoadBalancer
HealthAwareLoadBalancer
RegistryReplicator
```

---

# 53. Mini Implementation Skeleton

```java
public class ServiceInstance {

    private String serviceName;

    private String instanceId;

    private String host;

    private int port;

    private long lastHeartbeatTime;

    private Map<String, String> metadata;
}
```

---

# 54. Registry Skeleton

```java
public class ServiceRegistry {

    private final Map<String, List<ServiceInstance>>
            registry =
            new ConcurrentHashMap<>();

    public void register(
            ServiceInstance instance) {

        registry
            .computeIfAbsent(
                    instance.getServiceName(),
                    key -> new CopyOnWriteArrayList<>()
            )
            .add(instance);
    }

    public List<ServiceInstance> lookup(
            String serviceName) {

        return registry.getOrDefault(
                serviceName,
                List.of()
        );
    }
}
```

---

# 55. Dry Run — Registration

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

# 56. Dry Run — Scaling

New instances register:

```text
10.0.1.11:8080
10.0.1.12:8080
```

Registry becomes:

```text
payment-service
    → 10.0.1.10:8080
    → 10.0.1.11:8080
    → 10.0.1.12:8080
```

---

# 57. Dry Run — Lookup

Order service asks:

```text
lookup("payment-service")
```

Registry returns:

```text
3 payment instances
```

Load balancer chooses one.

---

# 58. Dry Run — Heartbeat Expiry

Instance:

```text
lastHeartbeat = 12:00:00
TTL = 30 sec
currentTime = 12:00:40
```

Difference:

```text
40 sec
```

Since:

```text
40 > 30
```

Instance expired.

---

# 59. Why This Mini Helps System Design Interviews

System design interviews often ask:

```text
how services communicate
how scaling works
how failures handled
how load balancer finds instances
how Kubernetes routing works
how service mesh helps
```

Service discovery is foundation for all of these.

---

# 60. Senior Interview Answer Template

If interviewer asks:

```text
How does service discovery work?
```

Strong answer:

```text
Services register their instance metadata with a registry, continuously
renew their lease using heartbeats, and clients query the registry to get
healthy instances. A load balancer then selects one instance. Production
systems handle stale data, network partitions, registry replication, and
health-check based routing.
```

Senior addition:

```text
Different systems make different CAP tradeoffs. Eureka prefers availability
and tolerates stale data, while Zookeeper-style systems prefer consistency.
Kubernetes commonly uses DNS and endpoint updates, while service mesh adds
sidecar-based routing, observability, and policy enforcement.
```

---

# 61. Most Important Insight

```text
Service Discovery looks like a hashmap at first,
but becomes distributed systems at scale.
```

---

# 62. Final Mental Model

```text
Service Discovery =
Dynamic DNS
+
Health Tracking
+
Instance Registry
+
Load Balancing Source
+
Distributed Failure Handling
```

---

# 63. Recommended File Study Order

Follow numeric order:

```text
001 → 005  = understand why
006 → 012  = build registry engine
013 → 017  = detect dead instances
018 → 022  = route traffic
023 → 031  = distributed systems depth
032 → 036  = real framework mapping
037 → 040  = production readiness
099       = final revision
```

---

# 64. Completion Outcome

After completing this mini, you should be able to:

```text
build a mini registry
explain Eureka internals
explain Kubernetes discovery
explain client-side load balancing
handle stale registry problems
reason about CAP tradeoffs
debug registry failures
design production service discovery
```

---

# 65. Next File

```text
001_What_Is_Service_Discovery.md
```

Next you learn:

```text
what service discovery is
why microservices need it
how it enables dynamic scaling
why it is a foundation of distributed systems
```
