# 099_ServiceDiscovery_Final_CheatSheet.md

# Service Discovery Final Cheat Sheet
## Last-Minute Revision for FAANG, Staff Engineer, System Design, Production Debugging

---

# 1. Service Discovery in One Line

```text
Service Discovery = Finding healthy service instances dynamically.
```

Instead of:

```java
http://10.10.1.12:8080
```

Use:

```java
http://payment-service
```

Discovery resolves:

```text
payment-service
      ↓
healthy instances
      ↓
selected endpoint
```

---

# 2. Why Service Discovery Exists

Without discovery:

```text
Hardcoded IPs
Manual updates
Downtime
Scaling pain
```

With discovery:

```text
Auto registration
Auto lookup
Auto failover
Auto scaling
```

---

# 3. Core Components

```text
Service Provider
Service Registry
Service Consumer
Health Checker
Load Balancer
```

Architecture:

```text
Provider
   |
Register
   |
Registry
   |
Lookup
   |
Consumer
```

---

# 4. Discovery Flow

```text
1. Service starts
2. Registers
3. Sends heartbeat
4. Consumer queries registry
5. Registry returns healthy instances
6. Consumer sends request
```

---

# 5. Registration

```text
Service → Registry
```

Metadata:

```text
serviceName
instanceId
IP
Port
Zone
Version
Health
```

Example:

```json
{
  "service":"payment-service",
  "ip":"10.1.2.3",
  "port":8080
}
```

---

# 6. Heartbeats

Purpose:

```text
Prove instance is alive
```

Flow:

```text
Every 30 sec
```

Missing heartbeat:

```text
Lease expires
Instance removed
```

---

# 7. Lease Model

```text
Renew lease
or
Get removed
```

Example:

```text
Renew every 30s
Expire after 90s
```

---

# 8. Health Checks

Two types:

```text
Active
Passive
```

Active:

```text
Ping instance
```

Passive:

```text
Observe failures
```

---

# 9. Client-Side Discovery

Examples:

```text
Ribbon
OpenFeign
gRPC
```

Flow:

```text
Client
  |
Registry lookup
  |
Choose instance
  |
Request
```

Pros:

```text
Smart routing
Less LB hop
```

Cons:

```text
Library dependency
```

---

# 10. Server-Side Discovery

Examples:

```text
Nginx
HAProxy
Gateway
ALB
```

Flow:

```text
Client
  |
LB
  |
Service
```

Pros:

```text
Simple client
```

Cons:

```text
Extra hop
```

---

# 11. Eureka Quick Revision

Components:

```text
Eureka Server
Eureka Client
```

Provider:

```text
Register
Heartbeat
Deregister
```

Consumer:

```text
Fetch registry
Cache registry
Lookup service
```

---

# 12. Eureka Self Preservation

Purpose:

```text
Avoid mass eviction
during network partition
```

Rule:

```text
Too many heartbeats missing
```

Then:

```text
Stop evictions
```

---

# 13. Eureka Replication

Multi-node Eureka:

```text
Node A
 ↔
Node B
 ↔
Node C
```

Replicates:

```text
Registrations
Heartbeats
Removals
```

---

# 14. Consul Quick Revision

Features:

```text
Registry
KV Store
Health Checks
DNS
Multi DC
```

Architecture:

```text
Servers
Agents
```

Consensus:

```text
Raft
```

---

# 15. ZooKeeper Discovery

Uses:

```text
Ephemeral Nodes
```

If service dies:

```text
Node removed automatically
```

---

# 16. Kubernetes Discovery

Core Objects:

```text
Pod
Deployment
Service
EndpointSlice
CoreDNS
```

Flow:

```text
Service Name
      ↓
CoreDNS
      ↓
Cluster IP
      ↓
Pod
```

---

# 17. CoreDNS

Purpose:

```text
Service Name Resolution
```

Example:

```text
payment-service.default.svc.cluster.local
```

---

# 18. EndpointSlice

Stores:

```text
Pod IPs
```

Example:

```text
10.1.1.10
10.1.1.11
10.1.1.12
```

---

# 19. Service Types

```text
ClusterIP
NodePort
LoadBalancer
Headless
```

---

# 20. Headless Service

```yaml
clusterIP: None
```

Returns:

```text
Individual Pod IPs
```

Useful for:

```text
Kafka
Cassandra
MongoDB
```

---

# 21. Load Balancing Algorithms

```text
Round Robin
Weighted RR
Least Connection
Least Request
Random
Consistent Hashing
```

---

# 22. Round Robin

```text
A
B
C
A
B
C
```

Simple.

---

# 23. Weighted Round Robin

Example:

```text
A = 80
B = 20
```

Traffic:

```text
80%
20%
```

---

# 24. Least Connection

Choose:

```text
Fewest active connections
```

Good for:

```text
Long lived requests
```

---

# 25. Consistent Hashing

Used in:

```text
Redis
Caches
CDN
Sharding
```

Benefits:

```text
Minimal remapping
```

---

# 26. Discovery Caching

Client cache reduces:

```text
Registry load
```

Tradeoff:

```text
Stale data
```

---

# 27. Registry Replication

Need for:

```text
HA
Multi region
```

Challenges:

```text
Consistency
Lag
Split brain
```

---

# 28. CAP Mapping

Registry is distributed system.

Tradeoffs:

```text
Consistency
Availability
Partition Tolerance
```

Examples:

```text
Eureka → AP
ZooKeeper → CP
Consul → CP
```

---

# 29. Split Brain

Problem:

```text
Two leaders
```

Results:

```text
Conflicting state
```

Fix:

```text
Quorum
Raft
ZooKeeper
```

---

# 30. Service Mesh

Purpose:

```text
Move networking concerns
out of application
```

---

# 31. Envoy

Data Plane

Responsibilities:

```text
Routing
Retries
Timeouts
mTLS
Metrics
Tracing
```

---

# 32. Istio

Control Plane

Responsibilities:

```text
Discovery
Policies
Certificates
Routing Rules
```

---

# 33. Data Plane vs Control Plane

```text
Envoy = Data Plane
Istiod = Control Plane
```

---

# 34. xDS APIs

```text
LDS
RDS
CDS
EDS
SDS
```

Remember:

```text
Listener
Route
Cluster
Endpoint
Secret
```

---

# 35. VirtualService

Controls:

```text
Routing
```

Examples:

```text
Canary
A/B Testing
Header Routing
```

---

# 36. DestinationRule

Controls:

```text
Subsets
Load balancing
Circuit breaking
```

---

# 37. ServiceEntry

Adds:

```text
External services
into mesh
```

---

# 38. mTLS

Flow:

```text
Envoy
  ↔
Envoy
```

Provides:

```text
Encryption
Identity
Authentication
```

---

# 39. AuthorizationPolicy

Controls:

```text
Who can call whom
```

---

# 40. Canary Deployment

Example:

```text
v1 = 95%
v2 = 5%
```

---

# 41. Retry Policy

Useful for:

```text
Transient failures
```

Danger:

```text
Retry storm
```

---

# 42. Timeout Policy

Never:

```text
Infinite wait
```

Always define:

```text
Connection timeout
Read timeout
```

---

# 43. Circuit Breaker

States:

```text
Closed
Open
Half Open
```

Purpose:

```text
Prevent cascading failures
```

---

# 44. Outlier Detection

Envoy feature.

Removes:

```text
Bad endpoints
```

---

# 45. Observability

Four pillars:

```text
Metrics
Logs
Tracing
Profiling
```

---

# 46. Golden Signals

```text
Latency
Traffic
Errors
Saturation
```

---

# 47. Metrics Tell

```text
WHAT happened?
```

---

# 48. Tracing Tells

```text
WHERE happened?
```

---

# 49. Logs Tell

```text
WHY happened?
```

---

# 50. Debugging Tells

```text
HOW to fix?
```

---

# 51. Prometheus

Flow:

```text
App
 ↓
/metrics
 ↓
Prometheus
 ↓
Grafana
```

---

# 52. Important Registry Metrics

```text
healthy_instances
unhealthy_instances
heartbeat_failures
lookup_latency
registry_size
```

---

# 53. Important Mesh Metrics

```text
RPS
Latency
Retries
Errors
Connections
```

---

# 54. Tracing

Components:

```text
Trace
Span
TraceID
```

---

# 55. OpenTelemetry

Industry standard.

Provides:

```text
Metrics
Tracing
Logs
```

---

# 56. Jaeger

Used for:

```text
Distributed tracing
```

---

# 57. Discovery Debugging Flow

```text
Registration
 ↓
Heartbeat
 ↓
Registry
 ↓
DNS
 ↓
Endpoint
 ↓
Network
 ↓
Trace
 ↓
Logs
```

---

# 58. Eureka Debugging

Verify:

```text
Instance registered?
```

Check:

```text
/actuator/health
/eureka/apps
```

---

# 59. Kubernetes Debugging

```bash
kubectl get svc
kubectl get endpoints
kubectl get pods
```

---

# 60. DNS Debugging

```bash
nslookup
dig
```

---

# 61. Connectivity Debugging

```bash
curl
wget
telnet
```

---

# 62. Istio Debugging

```bash
istioctl proxy-status
```

```bash
istioctl analyze
```

---

# 63. Common Production Failures

```text
DNS failure
Network partition
Heartbeat loss
Retry storm
Bad route
Bad cert
Split brain
```

---

# 64. Retry Storm Mental Model

```text
1 request
  ↓
3 retries
  ↓
4 total requests
```

Scale problem:

```text
1000 RPS
→ 4000 requests
```

---

# 65. Discovery Performance Metrics

Watch:

```text
Lookup latency
Cache hit rate
DNS latency
Registry sync delay
```

---

# 66. Multi Region Discovery

Problems:

```text
Replication lag
Stale data
Network latency
```

---

# 67. Production Checklist

✓ Registration

✓ Heartbeats

✓ Health Checks

✓ Registry Replication

✓ Load Balancing

✓ Metrics

✓ Tracing

✓ Logs

✓ Alerts

✓ Dashboards

✓ Runbooks

✓ HA

---

# 68. FAANG Interview Answers

What is service discovery?

```text
Mechanism that allows services to dynamically find healthy instances
without hardcoding addresses.
```

---

Client-side vs Server-side?

```text
Client-side:
client chooses instance.

Server-side:
load balancer chooses instance.
```

---

Why heartbeats?

```text
Detect dead instances.
```

---

Why self-preservation?

```text
Avoid mass eviction during partitions.
```

---

Why EndpointSlice?

```text
Scales better than large Endpoints objects.
```

---

Why service mesh?

```text
Move networking concerns from application to infrastructure.
```

---

Why tracing?

```text
Find slow/failing service in request path.
```

---

# 69. Production Mental Model

```text
Registry
    ↓
Discovery
    ↓
Load Balancing
    ↓
Routing
    ↓
Retries
    ↓
Security
    ↓
Observability
```

---

# 70. Remember Forever

```text
Discovery answers:

Where is the service?

Load Balancer answers:

Which instance?

Gateway answers:

Which route?

Service Mesh answers:

How should traffic behave?

Metrics answer:

What is wrong?

Tracing answers:

Where is wrong?

Logs answer:

Why is wrong?
```

---

# FINAL INTERVIEW ONE-LINER

```text
Modern service discovery is no longer just finding an IP address.

It combines registration, health checking, dynamic routing,
load balancing, failover, service mesh policies, observability,
security, and production debugging to ensure requests always reach
healthy service instances reliably at scale.
```
