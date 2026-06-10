# 006_Service_Instance_Model.md

# MiniServiceDiscovery — Service Instance Model

---

# 1. Why This File Exists

Service discovery fundamentally operates on:

```text
Service Instances
```

NOT just:

```text
service names
```

A registry does not simply store:

```text
payment-service
inventory-service
notification-service
```

Instead, it stores:

```text
actual live running instances
```

Example:

```text
payment-service
    → payment-instance-1
    → payment-instance-2
    → payment-instance-3
```

Each running instance has:

```text
network identity
health status
heartbeat information
routing metadata
availability state
deployment metadata
zone information
```

This file explains:

```text
what ServiceInstance means
how service instances internally modeled
why metadata critical
how instances tracked
how instance lifecycle works
how Eureka/Kubernetes model instances
how registries internally store instances
```

---

# 2. One-Line Definition

```text
ServiceInstance represents one live running endpoint of a service registered in discovery system.
```

---

# 3. Biggest Mental Model

```text
Service
=
logical application

Service Instance
=
one running copy of that application
```

---

# 4. Real World Analogy

Example:

```text
Uber
```

is the:

```text
service
```

But actual drivers are like:

```text
service instances
```

Registry tracks:

```text
which drivers currently active
where they located
whether healthy
```

Exactly same idea.

---

# 5. Another Mental Model

```text
Service =
class blueprint

ServiceInstance =
actual running object
```

---

# 6. Service vs Instance

## Service

Logical identity:

```text
payment-service
```

---

## Service Instance

Actual running process/container/pod:

```text
payment-instance-1
payment-instance-2
payment-instance-3
```

---

# 7. Why Multiple Instances Exist

Production systems never run:

```text
single instance only
```

because single instance creates:

```text
single point of failure
```

Instead systems use multiple instances for:

```text
high availability
fault tolerance
horizontal scaling
load balancing
rolling deployment
autoscaling
zero downtime deployments
```

---

# 8. Multiple Instances ASCII

```text
payment-service
    ├── payment-instance-1
    ├── payment-instance-2
    └── payment-instance-3
```

---

# 9. Why Registry Tracks Instances

Clients need answers for:

```text
which instances alive?
which instances healthy?
which version running?
which zone nearest?
which instance overloaded?
```

Registry answers these questions using:

```text
ServiceInstance objects
```

---

# 10. Example Registry State

```text
payment-service
    → 10.0.1.10:8080
    → 10.0.1.11:8080
    → 10.0.1.12:8080

inventory-service
    → 10.0.2.10:8080
```

Each endpoint is one:

```text
ServiceInstance
```

---

# 11. Core Internal Java Model

Typical implementation:

```java
public class ServiceInstance {

    private String serviceName;

    private String instanceId;

    private String host;

    private int port;

    private InstanceStatus status;

    private long registrationTime;

    private long lastHeartbeatTime;

    private Map<String, String> metadata;
}
```

---

# 12. Why serviceName Needed

Registry groups instances using:

```text
service name
```

Example:

```text
payment-service
inventory-service
user-service
```

Without serviceName:

```text
registry cannot group related instances
```

---

# 13. Why instanceId Needed

Many instances may run simultaneously.

Need unique identity.

Example:

```text
payment-1
payment-2
payment-3
```

---

# 14. Instance ID Mental Model

```text
serviceName
=
logical application identity

instanceId
=
individual runtime identity
```

---

# 15. Why host Needed

Registry must know:

```text
where instance actually running
```

Example:

```text
10.0.1.10
```

May represent:

```text
VM
container
pod
physical machine
```

---

# 16. Why port Needed

Many applications may run on same machine.

Need exact endpoint.

Example:

```text
10.0.1.10:8080
10.0.1.10:9090
10.0.1.10:7070
```

---

# 17. Why status Needed

Registry must know:

```text
can traffic safely route here?
```

---

# 18. Common Instance Status Values

Typical statuses:

```text
UP
DOWN
STARTING
OUT_OF_SERVICE
UNKNOWN
```

---

# 19. Status Mental Model

## UP

```text
safe to receive traffic
```

## DOWN

```text
do not route traffic
```

## STARTING

```text
booting but not ready yet
```

## OUT_OF_SERVICE

```text
temporarily disabled
```

---

# 20. Why registrationTime Needed

Useful for:

```text
monitoring
uptime calculation
debugging
deployment analysis
instance aging
```

---

# 21. Why lastHeartbeatTime Needed

Registry must detect:

```text
dead instances
```

Heartbeat timestamp proves liveness.

---

# 22. Heartbeat Mental Model

```text
Service periodically says:
"I'm still alive"
```

Registry updates:

```text
lastHeartbeatTime
```

---

# 23. TTL Mental Model

If heartbeat missing beyond TTL:

```text
instance considered dead
```

---

# 24. TTL Example

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
```

---

# 25. Why Metadata Important

Metadata transforms registry into:

```text
intelligent routing engine
```

instead of:

```text
simple IP list
```

---

# 26. Metadata Examples

```text
version=v2
zone=eu-west
region=eu
weight=5
protocol=https
canary=true
environment=prod
```

---

# 27. Version Routing Example

Suppose:

```text
v1 and v2 deployed together
```

Registry stores:

```text
payment1 → version=v1
payment2 → version=v2
```

Router can selectively route traffic.

---

# 28. Canary Deployment Example

Goal:

```text
send only 5% traffic to v2
```

Metadata:

```text
canary=true
```

Routing engine filters instances.

---

# 29. Zone-Aware Routing Example

Metadata:

```text
zone=us-east
zone=eu-west
```

Client prefers nearest zone.

Benefits:

```text
lower latency
reduced cross-region traffic
better resilience
```

---

# 30. Weighted Routing Example

Metadata:

```text
weight=10
weight=2
```

More powerful instance receives more traffic.

---

# 31. Protocol Metadata Example

Metadata:

```text
protocol=https
```

Useful when services support:

```text
HTTP
HTTPS
gRPC
WebSocket
```

---

# 32. Environment Metadata Example

Metadata:

```text
environment=staging
environment=prod
```

Avoids accidental cross-environment routing.

---

# 33. Full Rich ServiceInstance Model

```java
public class ServiceInstance {

    private String serviceName;

    private String instanceId;

    private String host;

    private int port;

    private InstanceStatus status;

    private long registrationTime;

    private long lastHeartbeatTime;

    private Map<String, String> metadata;

    private int weight;

    private String zone;

    private String region;

    private String version;
}
```

---

# 34. ServiceInstance Lifecycle

Lifecycle:

```text
instance created
    ↓
instance registers
    ↓
instance becomes healthy
    ↓
heartbeat updates
    ↓
serves traffic
    ↓
instance unhealthy
    ↓
instance removed
```

---

# 35. Lifecycle ASCII

```text
CREATED
   ↓
REGISTERED
   ↓
UP
   ↓
DOWN
   ↓
REMOVED
```

---

# 36. Registration Flow

When service starts:

```text
instance registers itself
```

Registry stores ServiceInstance object.

---

# 37. Registration ASCII

```text
Payment Instance
      ↓
Register()
      ↓
Registry Stores Instance
```

---

# 38. Lookup Flow

Client asks:

```text
lookup(payment-service)
```

Registry returns:

```text
List<ServiceInstance>
```

---

# 39. Lookup ASCII

```text
Order Service
      ↓
lookup(payment-service)
      ↓
[payment1, payment2, payment3]
```

---

# 40. Why Registry Uses List<ServiceInstance>

One service may have:

```text
many healthy instances
```

Need collection.

Usually:

```java
List<ServiceInstance>
```

---

# 41. Registry Internal Structure

Typical implementation:

```java
ConcurrentHashMap<
    String,
    CopyOnWriteArrayList<ServiceInstance>
>
```

---

# 42. Internal Structure Mental Model

```text
key
=
service name

value
=
list of live instances
```

---

# 43. Example Internal Registry

```text
payment-service
    → [payment1, payment2, payment3]

inventory-service
    → [inventory1]
```

---

# 44. Why ConcurrentHashMap Used

Need:

```text
concurrent lookups
concurrent registration
concurrent heartbeats
concurrent removals
```

Regular HashMap unsafe.

---

# 45. Why CopyOnWriteArrayList Used

Registry workloads mostly:

```text
many reads
few writes
```

Ideal for:

```text
CopyOnWriteArrayList
```

---

# 46. Read-Heavy Mental Model

Discovery systems commonly handle:

```text
millions of lookups
few registrations
```

Optimized heavily for fast reads.

---

# 47. Thread Safety Problem

Simultaneously:

```text
1000 clients lookup
50 services heartbeat
10 services register
cleanup thread removes dead instances
```

Need thread-safe design.

---

# 48. Client Cache Mental Model

Clients often cache:

```text
List<ServiceInstance>
```

locally.

Benefits:

```text
lower latency
reduced registry load
better resilience
```

---

# 49. Stale Instance Problem

Client cache may contain:

```text
dead instance
```

Need:

```text
heartbeat filtering
TTL expiration
retry logic
health checks
```

---

# 50. Example JSON Representation

```json
{
  "serviceName": "payment-service",
  "instanceId": "payment-1",
  "host": "10.0.1.10",
  "port": 8080,
  "status": "UP",
  "metadata": {
    "version": "v2",
    "zone": "eu-west"
  }
}
```

---

# 51. Eureka Instance Model

Eureka internally stores:

```text
InstanceInfo
```

Contains:

```text
host
port
VIP address
metadata
lease info
status
heartbeat timestamps
```

---

# 52. Kubernetes Instance Model

Kubernetes uses:

```text
Pods
Endpoints
EndpointSlices
```

Each pod effectively acts as:

```text
ServiceInstance
```

---

# 53. Kubernetes Example

```text
payment-pod-1 → 10.0.1.10
payment-pod-2 → 10.0.1.11
payment-pod-3 → 10.0.1.12
```

---

# 54. Service Mesh Instance Model

Service mesh tracks:

```text
service endpoints
health state
routing metadata
traffic policies
sidecar endpoints
```

---

# 55. Why ServiceInstance Central To Discovery

Everything depends on instance model:

```text
routing
load balancing
heartbeat
health tracking
traffic splitting
zone awareness
canary deployment
weighted routing
```

---

# 56. Dry Run — Registration

Input:

```text
serviceName = payment-service
instanceId = payment-1
host = 10.0.1.10
port = 8080
status = UP
```

Registry after registration:

```text
payment-service
    → payment-1
```

---

# 57. Dry Run — Scaling

New instances added:

```text
payment-2
payment-3
```

Registry becomes:

```text
payment-service
    → payment-1
    → payment-2
    → payment-3
```

---

# 58. Dry Run — Heartbeat Update

Heartbeat received from:

```text
payment-2
```

Registry updates:

```text
lastHeartbeatTime
```

for payment-2.

---

# 59. Dry Run — TTL Expiration

Input:

```text
payment-3 no heartbeat for 40 sec
TTL = 30 sec
```

Result:

```text
payment-3 removed
```

---

# 60. Dry Run — Canary Routing

Instances:

```text
payment1 → version=v1
payment2 → version=v2
```

Router sends:

```text
5% traffic → payment2
95% traffic → payment1
```

using metadata.

---

# 61. Dry Run — Zone-Aware Routing

Instances:

```text
payment1 → zone=eu-west
payment2 → zone=us-east
```

European client prefers:

```text
payment1
```

---

# 62. Strong Interview Answer

Question:

```text
What is ServiceInstance?
```

Strong answer:

```text
A ServiceInstance represents one live running endpoint of a service inside
a discovery system. It contains network identity, health status,
heartbeat information, and metadata used for routing, load balancing,
and traffic management.
```

Senior addition:

```text
Modern discovery systems use ServiceInstance metadata for advanced routing
patterns such as canary deployment, weighted balancing, zone-aware routing,
and version-aware traffic control.
```

---

# 63. Most Important Insight

```text
Service discovery fundamentally tracks live runtime instances, not just service names.
```

---

# 64. Final Mental Model

```text
Service
=
logical application

ServiceInstance
=
live running endpoint
+
health state
+
heartbeat
+
routing metadata
```

---

# 65. What To Remember

```text
Registry stores instances.

Each instance has host + port.

Status controls routing eligibility.

Heartbeats prove liveness.

TTL removes dead instances.

Metadata enables advanced routing.

Discovery systems are read-heavy.

Thread safety critical.

ServiceInstance is core unit of discovery.
```

---

# 66. Next File

```text
007_Service_Registration_Lookup_Deregistration.md
```

Next you learn:

```text
how services register themselves
how lookup internally works
how deregistration happens
how registry lifecycle managed
```
