# 000_Index.md

# MiniServiceDiscovery — Complete Learning Roadmap

---

# 1. Why This Mini Exists

Modern backend systems are no longer deployed as:

```text
one monolith
one server
one fixed IP address
```

They are deployed as:

```text
microservices
containers
Kubernetes pods
autoscaled instances
multi-node systems
multi-zone systems
service mesh networks
```

In this world:

```text
service locations keep changing
```

So services need a way to dynamically find each other.

That is the purpose of:

```text
Service Discovery
```

This mini teaches Service Discovery like MiniRedis / MiniKafka / MiniCircuitBreaker:

```text
not as a framework tutorial
but as an internal system model
```

---

# 2. One-Line Definition

```text
Service Discovery lets services dynamically find healthy instances of other services.
```

---

# 3. Biggest Mental Model

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

# 4. Real World Analogy

```text
Service Discovery = Google Maps for services
```

You do not memorize every shop’s current GPS coordinate.

You search dynamically:

```text
nearest coffee shop
```

Similarly, services ask dynamically:

```text
healthy payment-service instances
```

---

# 5. The Core Problem

In cloud-native systems, service instances constantly:

```text
start
stop
crash
restart
scale up
scale down
move to another node
get new IP address
become unhealthy
recover later
```

So this breaks:

```text
hardcoded IP communication
```

---

# 6. Without Service Discovery

```text
Order Service
      ↓
http://10.0.1.25:8080
      ↓
Payment Service
```

Now payment-service restarts.

New IP:

```text
10.0.1.88
```

But order-service still calls:

```text
10.0.1.25
```

Result:

```text
requests fail
```

---

# 7. With Service Discovery

```text
Order Service
      ↓
Service Registry
      ↓
Healthy Payment Instances
      ↓
Load Balancer chooses one
      ↓
Payment Service
```

Now when payment-service restarts, it re-registers with a new address.

Clients discover updated information.

---

# 8. Final Optimized MiniServiceDiscovery Structure

This is the approved minimal-but-complete structure:

```text
MiniServiceDiscovery/
├── 000_Index.md
│
├── 001_What_Is_Service_Discovery.md
├── 002_Why_Hardcoded_IPs_Fail.md
├── 003_Service_Registry_Mental_Model.md
├── 004_Client_Side_vs_Server_Side_Discovery.md
├── 005_Eureka_Consul_Zookeeper_K8s_Comparison.md
│
├── 006_Service_Instance_Model.md
├── 007_Service_Registration_Lookup_Deregistration.md
├── 008_Instance_Metadata_And_Health_Status.md
├── 009_Thread_Safe_InMemory_Registry.md
│
├── 010_Heartbeat_Lease_Renewal_TTL.md
├── 011_Dead_Instance_Removal.md
├── 012_Eureka_Self_Preservation_Mode.md
│
├── 013_RoundRobin_Weighted_LeastConnection_LB.md
├── 014_Client_Side_vs_Server_Side_LoadBalancing.md
├── 015_Health_Aware_LoadBalancing.md
│
├── 016_Network_Partition_Stale_Registry_SplitBrain.md
├── 017_Eventual_Consistency_CAP_Tradeoff.md
├── 018_Registry_Replication_Model.md
│
├── 019_SpringCloud_Eureka_OpenFeign_Internals.md
├── 020_Kubernetes_DNS_CoreDNS_ServiceDiscovery.md
├── 021_Service_Mesh_Envoy_Istio_Discovery.md
│
├── 022_Registry_Metrics_Tracing_Debugging.md
├── 023_Production_Grade_ServiceDiscovery.md
│
└── 099_ServiceDiscovery_Final_CheatSheet.md
```

---

# 9. Why This Structure Is Optimized

This structure is:

```text
minimal
complete
production-relevant
interview-ready
not bloated
```

It covers all must-have layers:

```text
foundation
registry internals
heartbeat and TTL
load balancing
distributed systems problems
replication
Spring Cloud mapping
Kubernetes mapping
service mesh basics
observability
production design
```

---

# 10. Learning Flow

```text
Foundation
   ↓
Registry Engine
   ↓
Heartbeat + TTL
   ↓
Load Balancing
   ↓
Distributed Systems Problems
   ↓
Replication
   ↓
Spring Cloud + Kubernetes
   ↓
Observability
   ↓
Production Architecture
   ↓
Final CheatSheet
```

This order is important because it moves from:

```text
WHY
  ↓
HOW
  ↓
INTERNALS
  ↓
FAILURES
  ↓
PRODUCTION
```

---

# 11. Layer 1 — Foundation

Files:

```text
001_What_Is_Service_Discovery.md
002_Why_Hardcoded_IPs_Fail.md
003_Service_Registry_Mental_Model.md
004_Client_Side_vs_Server_Side_Discovery.md
005_Eureka_Consul_Zookeeper_K8s_Comparison.md
```

Purpose:

```text
understand why service discovery exists
```

You learn:

```text
what service discovery is
why static IPs fail
what registry means
client-side discovery
server-side discovery
Eureka vs Consul vs Zookeeper vs Kubernetes
```

---

# 12. Foundation Mental Model

```text
Microservices are dynamic.
Static addresses fail.
Registry solves dynamic lookup.
```

---

# 13. 001_What_Is_Service_Discovery.md

This file answers:

```text
what service discovery means
why microservices need it
how services dynamically find each other
```

Core mental model:

```text
Dynamic phonebook for microservices
```

---

# 14. 002_Why_Hardcoded_IPs_Fail.md

This file explains:

```text
why static IP-based service communication breaks
```

Reasons:

```text
containers restart
pods move
autoscaling creates new instances
cloud networking changes IPs
node failures happen
```

---

# 15. 003_Service_Registry_Mental_Model.md

This file introduces:

```text
service registry
```

Registry stores:

```text
service name
instance id
host
port
status
metadata
heartbeat time
```

Mental model:

```text
registry = dynamic map of serviceName → live instances
```

---

# 16. 004_Client_Side_vs_Server_Side_Discovery.md

This file compares two patterns:

```text
client-side discovery
server-side discovery
```

Client-side:

```text
client queries registry and chooses instance
```

Server-side:

```text
client calls load balancer and load balancer chooses instance
```

---

# 17. 005_Eureka_Consul_Zookeeper_K8s_Comparison.md

This file compares real tools:

```text
Eureka
Consul
Zookeeper
Kubernetes DNS
```

Mental model:

```text
Different discovery tools make different consistency/availability tradeoffs.
```

---

# 18. Layer 2 — Core Registry Engine

Files:

```text
006_Service_Instance_Model.md
007_Service_Registration_Lookup_Deregistration.md
008_Instance_Metadata_And_Health_Status.md
009_Thread_Safe_InMemory_Registry.md
```

Purpose:

```text
build the mini registry internally
```

---

# 19. Core Registry Data Structure

The mini registry is mainly:

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

Example:

```text
payment-service
    → 10.0.1.10:8080
    → 10.0.1.11:8080
    → 10.0.1.12:8080
```

---

# 20. 006_Service_Instance_Model.md

This file defines the core object:

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

This object is the basic unit of discovery.

---

# 21. 007_Service_Registration_Lookup_Deregistration.md

This file explains three main registry operations:

```text
register
lookup
deregister
```

Flow:

```text
service starts
    ↓
registers
    ↓
client looks up
    ↓
service stops
    ↓
deregisters
```

---

# 22. 008_Instance_Metadata_And_Health_Status.md

This file explains metadata:

```text
version
zone
region
weight
protocol
health status
tags
```

Metadata helps with:

```text
routing
health-aware load balancing
blue-green deployment
canary routing
zone-aware routing
```

---

# 23. 009_Thread_Safe_InMemory_Registry.md

This file explains why registry must be thread-safe.

Many services may simultaneously:

```text
register
deregister
send heartbeat
lookup instances
```

Need:

```text
ConcurrentHashMap
CopyOnWriteArrayList
locks when needed
safe iteration
```

---

# 24. Layer 3 — Heartbeat + TTL

Files:

```text
010_Heartbeat_Lease_Renewal_TTL.md
011_Dead_Instance_Removal.md
012_Eureka_Self_Preservation_Mode.md
```

Purpose:

```text
detect whether registered service instances are still alive
```

---

# 25. Heartbeat Mental Model

```text
Service says:
"I'm alive"
```

every few seconds.

Example:

```text
payment-service sends heartbeat every 10 seconds
```

---

# 26. Lease Renewal Mental Model

Service registration is not permanent.

It is like a rental lease:

```text
service must renew lease
```

If lease is not renewed:

```text
registry assumes instance dead
```

---

# 27. TTL Mental Model

TTL means:

```text
time to live
```

Example:

```text
heartbeat interval = 10 seconds
TTL = 30 seconds
```

If no heartbeat for 30 seconds:

```text
instance expired
```

---

# 28. 010_Heartbeat_Lease_Renewal_TTL.md

This file explains:

```text
heartbeat interval
lease renewal
lastHeartbeatTime
TTL calculation
```

Dry run:

```text
last heartbeat = 12:00:00
current time = 12:00:40
TTL = 30 sec

40 > 30
instance expired
```

---

# 29. 011_Dead_Instance_Removal.md

This file explains the cleanup scanner.

Flow:

```text
periodic scanner runs
    ↓
checks lastHeartbeatTime
    ↓
expired?
    ↓
remove instance
```

---

# 30. 012_Eureka_Self_Preservation_Mode.md

This file explains a very important distributed systems concept.

If many heartbeats disappear suddenly:

```text
maybe network partition happened
```

not necessarily:

```text
all services died
```

So Eureka avoids mass deletion.

Mental model:

```text
Do not delete half the world during a network glitch.
```

---

# 31. Layer 4 — Load Balancing

Files:

```text
013_RoundRobin_Weighted_LeastConnection_LB.md
014_Client_Side_vs_Server_Side_LoadBalancing.md
015_Health_Aware_LoadBalancing.md
```

Purpose:

```text
choose one healthy instance among many
```

---

# 32. Discovery vs Load Balancing

Service discovery answers:

```text
WHERE are the instances?
```

Load balancing answers:

```text
WHICH instance should receive this request?
```

---

# 33. 013_RoundRobin_Weighted_LeastConnection_LB.md

This file covers:

```text
Round Robin
Weighted Round Robin
Least Connections
```

Round Robin:

```text
A → B → C → A → B → C
```

Weighted:

```text
stronger instance gets more traffic
```

Least Connection:

```text
send request to least busy instance
```

---

# 34. 014_Client_Side_vs_Server_Side_LoadBalancing.md

Client-side LB:

```text
client chooses instance
```

Examples:

```text
Eureka + Ribbon
Spring Cloud LoadBalancer
OpenFeign
```

Server-side LB:

```text
load balancer chooses instance
```

Examples:

```text
Kubernetes Service
NGINX
Envoy
AWS ELB
```

---

# 35. 015_Health_Aware_LoadBalancing.md

This file explains smarter routing.

Avoid instances with:

```text
high latency
high error rate
unhealthy status
overload
```

Mental model:

```text
Do not route traffic to a sick instance.
```

---

# 36. Layer 5 — Distributed Systems Problems

Files:

```text
016_Network_Partition_Stale_Registry_SplitBrain.md
017_Eventual_Consistency_CAP_Tradeoff.md
018_Registry_Replication_Model.md
```

Purpose:

```text
understand why service discovery becomes hard at scale
```

---

# 37. 016_Network_Partition_Stale_Registry_SplitBrain.md

This file explains:

```text
network partition
stale registry
split brain
```

Network partition:

```text
registry nodes cannot communicate
```

Stale registry:

```text
registry says instance alive but instance is dead
```

Split brain:

```text
two groups both think they are correct registry
```

---

# 38. 017_Eventual_Consistency_CAP_Tradeoff.md

This file explains:

```text
eventual consistency
CAP theorem
availability vs consistency tradeoff
```

Key insight:

```text
during network partition,
you cannot have perfect consistency and perfect availability together
```

---

# 39. 018_Registry_Replication_Model.md

This file explains how registry nodes replicate data.

Replication patterns:

```text
peer-to-peer
leader-follower
multi-zone registry
```

Purpose:

```text
registry should not be single point of failure
```

---

# 40. Layer 6 — Spring Cloud + Kubernetes

Files:

```text
019_SpringCloud_Eureka_OpenFeign_Internals.md
020_Kubernetes_DNS_CoreDNS_ServiceDiscovery.md
021_Service_Mesh_Envoy_Istio_Discovery.md
```

Purpose:

```text
map your internal model to real production tools
```

---

# 41. 019_SpringCloud_Eureka_OpenFeign_Internals.md

This file explains:

```text
Eureka Server
Eureka Client
OpenFeign
Spring Cloud LoadBalancer
registry fetch
heartbeat
client-side load balancing
```

Flow:

```text
service registers with Eureka
    ↓
client fetches registry
    ↓
OpenFeign resolves service name
    ↓
LoadBalancer chooses instance
```

---

# 42. 020_Kubernetes_DNS_CoreDNS_ServiceDiscovery.md

This file explains Kubernetes discovery.

Kubernetes uses:

```text
Service object
Endpoints / EndpointSlices
CoreDNS
ClusterIP
```

DNS example:

```text
payment-service.default.svc.cluster.local
```

---

# 43. 021_Service_Mesh_Envoy_Istio_Discovery.md

This file explains service mesh discovery.

Service mesh adds:

```text
sidecar proxy
traffic routing
mTLS
retries
circuit breaking
observability
policy enforcement
```

Examples:

```text
Envoy
Istio
Linkerd
```

---

# 44. Layer 7 — Observability

File:

```text
022_Registry_Metrics_Tracing_Debugging.md
```

Purpose:

```text
debug service discovery in production
```

Monitor:

```text
registered instance count
heartbeat failures
lookup latency
stale instances
registry replication lag
DNS failures
service-to-service trace
```

---

# 45. Observability Mental Model

```text
Metrics show WHAT happened.
Logs show WHY it happened.
Tracing shows WHERE request went.
```

---

# 46. Layer 8 — Production

File:

```text
023_Production_Grade_ServiceDiscovery.md
```

Purpose:

```text
combine all concepts into production architecture
```

Covers:

```text
multi-zone registry
high availability
registry failover
stale data handling
security
rate limiting
health-aware routing
service mesh integration
production debugging
```

---

# 47. Final CheatSheet

File:

```text
099_ServiceDiscovery_Final_CheatSheet.md
```

Purpose:

```text
quick revision
last-minute interview recall
mental models
diagrams
production patterns
```

---

# 48. Core Java Implementation Plan

You will implement small Java models:

```text
ServiceInstance
ServiceRegistry
HeartbeatManager
TTLScanner
RoundRobinLoadBalancer
HealthAwareLoadBalancer
RegistryReplicator
```

---

# 49. ServiceInstance Skeleton

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

# 50. Registry Skeleton

```java
public class ServiceRegistry {

    private final ConcurrentHashMap<
            String,
            List<ServiceInstance>
    > registry = new ConcurrentHashMap<>();

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

# 51. Dry Run — Registration

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

# 52. Dry Run — Scaling

More instances register:

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

# 53. Dry Run — Lookup

Order service asks:

```text
lookup("payment-service")
```

Registry returns:

```text
3 payment instances
```

Then load balancer chooses one.

---

# 54. Dry Run — Heartbeat Expiry

Input:

```text
lastHeartbeat = 12:00:00
currentTime = 12:00:40
TTL = 30 sec
```

Calculation:

```text
40 sec > 30 sec
```

Result:

```text
instance expired
remove from registry
```

---

# 55. Why This Mini Helps Interviews

System design interviews often ask:

```text
how services communicate
how microservices scale
how load balancer finds instances
how Kubernetes service routing works
how service mesh helps
how stale service data handled
```

This mini gives you the internal answer.

---

# 56. Strong Interview Answer

Question:

```text
How does service discovery work?
```

Answer:

```text
Services register their instance metadata with a registry and keep renewing
their lease using heartbeats. Clients query the registry to get healthy
instances and use load balancing to choose one. Production systems must
handle stale registries, network partitions, replication, health checks,
and CAP tradeoffs.
```

Senior addition:

```text
Eureka prefers availability and may tolerate stale data, while Zookeeper-like
systems prefer consistency. Kubernetes commonly uses DNS/CoreDNS and
EndpointSlices, while service mesh systems use sidecar proxies like Envoy
for advanced routing, observability, and security.
```

---

# 57. High ROI Files

Most important files:

```text
003_Service_Registry_Mental_Model.md
009_Thread_Safe_InMemory_Registry.md
010_Heartbeat_Lease_Renewal_TTL.md
012_Eureka_Self_Preservation_Mode.md
016_Network_Partition_Stale_Registry_SplitBrain.md
017_Eventual_Consistency_CAP_Tradeoff.md
019_SpringCloud_Eureka_OpenFeign_Internals.md
020_Kubernetes_DNS_CoreDNS_ServiceDiscovery.md
023_Production_Grade_ServiceDiscovery.md
099_ServiceDiscovery_Final_CheatSheet.md
```

---

# 58. Most Important Insight

```text
Service Discovery looks like a hashmap at first,
but becomes a distributed systems problem at scale.
```

---

# 59. Final Mental Model

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

# 60. Completion Outcome

After this mini, you can explain:

```text
Eureka internals
Consul / Zookeeper tradeoffs
Kubernetes DNS discovery
CoreDNS
OpenFeign service resolution
client-side load balancing
service mesh discovery
network partition handling
stale registry handling
CAP tradeoffs
production service discovery
```

---

# 61. Next File

```text
001_What_Is_Service_Discovery.md
```
