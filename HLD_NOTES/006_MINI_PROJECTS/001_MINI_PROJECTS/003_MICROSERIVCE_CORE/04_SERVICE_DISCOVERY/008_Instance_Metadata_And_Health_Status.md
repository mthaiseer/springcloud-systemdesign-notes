# 008_Instance_Metadata_And_Health_Status.md

# MiniServiceDiscovery — Instance Metadata And Health Status

---

# 1. Why This File Exists

Basic service discovery can work with:

```text
service name
host
port
```

But production systems need much more intelligence.

Modern discovery systems must answer:

```text
which instance is healthy?
which version should receive traffic?
which zone is nearest?
which instance overloaded?
which canary version should receive 5% traffic?
```

This intelligence comes from:

```text
metadata
+
health status
```

This file explains:

```text
what instance metadata means
why metadata important
what health status means
how health-aware routing works
how canary deployment works
how zone-aware routing works
weighted routing
metadata-driven traffic control
Eureka metadata
Kubernetes readiness/liveness
production routing patterns
```

---

# 2. Biggest Mental Model

```text
Metadata
=
routing intelligence

Health status
=
traffic safety decision
```

---

# 3. Service Discovery Evolution

## Phase 1

Simple discovery:

```text
serviceName → IP
```

---

## Phase 2

Health-aware discovery:

```text
serviceName → healthy IPs
```

---

## Phase 3

Intelligent discovery:

```text
serviceName
    ↓
filter by:
version
zone
health
weight
environment
protocol
```

---

# 4. What Is Metadata?

Metadata means:

```text
extra information attached to service instance
```

---

# 5. Metadata Mental Model

```text
Metadata =
labels/tags describing instance behavior
```

---

# 6. Example Metadata

```text
version=v2
zone=eu-west
region=eu
weight=5
environment=prod
protocol=https
canary=true
```

---

# 7. Full ServiceInstance Example

```java
public class ServiceInstance {

    private String serviceName;

    private String instanceId;

    private String host;

    private int port;

    private InstanceStatus status;

    private Map<String, String> metadata;
}
```

---

# 8. Why Metadata Important

Metadata enables:

```text
canary deployment
blue-green deployment
weighted routing
zone-aware routing
environment isolation
protocol routing
traffic shaping
feature rollout
```

---

# 9. Metadata Without Routing Intelligence

Without metadata:

```text
all instances treated equally
```

Cannot do:

```text
smart traffic control
```

---

# 10. Version Metadata

Example:

```text
payment1 → version=v1
payment2 → version=v2
```

Allows:

```text
version-aware routing
```

---

# 11. Canary Deployment Mental Model

Goal:

```text
send small traffic percentage to new version
```

Example:

```text
95% → v1
5% → v2
```

---

# 12. Canary ASCII

```text
Clients
   ↓
Router
   ↓
95% → v1
5%  → v2
```

---

# 13. Canary Metadata Example

```text
payment1 → version=v1
payment2 → version=v1
payment3 → version=v2
```

Traffic router selectively chooses.

---

# 14. Blue-Green Deployment Mental Model

Two environments:

```text
BLUE = old version
GREEN = new version
```

Metadata:

```text
environment=blue
environment=green
```

Traffic switches gradually.

---

# 15. Blue-Green ASCII

```text
Clients
   ↓
Router
   ↓
BLUE or GREEN
```

---

# 16. Zone Metadata

Example:

```text
zone=eu-west
zone=us-east
```

Allows:

```text
zone-aware routing
```

---

# 17. Zone-Aware Routing Mental Model

Prefer nearby instances.

Benefits:

```text
lower latency
reduced cross-region traffic
better resilience
```

---

# 18. Zone-Aware Routing Example

European client prefers:

```text
zone=eu-west
```

instead of:

```text
zone=us-east
```

---

# 19. Region Metadata

Example:

```text
region=eu
region=us
region=apac
```

Useful for:

```text
multi-region failover
geo-routing
compliance routing
```

---

# 20. Weight Metadata

Example:

```text
weight=10
weight=2
```

Allows:

```text
weighted load balancing
```

---

# 21. Weighted Routing Mental Model

More powerful instances receive more traffic.

---

# 22. Weighted Routing Example

```text
payment1 → weight=10
payment2 → weight=2
```

payment1 receives:

```text
much larger traffic share
```

---

# 23. Protocol Metadata

Example:

```text
protocol=http
protocol=https
protocol=grpc
```

Allows protocol-aware routing.

---

# 24. Environment Metadata

Example:

```text
environment=prod
environment=staging
environment=dev
```

Prevents accidental cross-environment calls.

---

# 25. Metadata Java Example

```java
Map<String, String> metadata =
        Map.of(
                "version", "v2",
                "zone", "eu-west",
                "environment", "prod"
        );
```

---

# 26. Metadata Registration Example

```json
{
  "serviceName": "payment-service",
  "instanceId": "payment-1",
  "metadata": {
    "version": "v2",
    "zone": "eu-west",
    "weight": "5"
  }
}
```

---

# 27. Health Status Definition

Health status tells registry:

```text
whether instance should receive traffic
```

---

# 28. Biggest Health Mental Model

```text
Healthy instance
=
safe traffic target
```

---

# 29. Common Health States

Typical statuses:

```text
UP
DOWN
STARTING
OUT_OF_SERVICE
UNKNOWN
```

---

# 30. Status Meanings

## UP

```text
safe to route traffic
```

---

## DOWN

```text
do not route traffic
```

---

## STARTING

```text
booting but not ready
```

---

## OUT_OF_SERVICE

```text
temporarily disabled intentionally
```

---

## UNKNOWN

```text
registry uncertain about health
```

---

# 31. Why Health Status Critical

Without health tracking:

```text
traffic may route to broken instances
```

Causing:

```text
timeouts
errors
cascading failures
```

---

# 32. Health-Aware Routing

Registry should return only:

```text
healthy instances
```

---

# 33. Health Filtering Example

Registry:

```text
payment1 → UP
payment2 → DOWN
payment3 → UP
```

Lookup should return:

```text
payment1
payment3
```

---

# 34. Health Filtering Code

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

# 35. Health Check Types

Production systems use:

```text
heartbeat checks
HTTP health checks
TCP checks
readiness checks
liveness checks
dependency checks
```

---

# 36. Heartbeat Health Check

Service periodically says:

```text
I'm alive
```

Simple but limited.

---

# 37. Heartbeat Problem

Instance may still send heartbeat while:

```text
DB disconnected
thread pool exhausted
dependency failing
```

Heartbeat alone insufficient.

---

# 38. HTTP Health Check

Registry/proxy calls:

```text
/health
```

Example response:

```json
{
  "status": "UP"
}
```

---

# 39. Deep Health Checks

Health check may verify:

```text
database connectivity
Kafka connectivity
Redis availability
disk space
memory pressure
thread pool health
```

---

# 40. Health Check Tradeoff

Very deep health checks may become:

```text
slow
expensive
unstable
```

Need balance.

---

# 41. Kubernetes Health Model

Kubernetes uses:

```text
liveness probe
readiness probe
startup probe
```

---

# 42. Liveness Probe Mental Model

Question:

```text
Should container restart?
```

---

# 43. Readiness Probe Mental Model

Question:

```text
Should traffic route here?
```

---

# 44. Startup Probe Mental Model

Question:

```text
Has application finished booting?
```

---

# 45. Kubernetes Routing Logic

If readiness probe fails:

```text
pod removed from service endpoints
```

Traffic stops routing.

---

# 46. Kubernetes ASCII

```text
Readiness Probe Fails
       ↓
Pod Removed From EndpointSlice
       ↓
No Traffic Routed
```

---

# 47. Eureka Health Model

Eureka primarily relies on:

```text
lease renewal heartbeat
```

Optionally integrates with:

```text
Spring Boot Actuator health
```

---

# 48. Spring Boot Actuator Example

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

# 49. Health Status Lifecycle

```text
STARTING
    ↓
UP
    ↓
DOWN
```

or:

```text
UP
    ↓
OUT_OF_SERVICE
```

during maintenance.

---

# 50. Maintenance Window Example

Admin marks instance:

```text
OUT_OF_SERVICE
```

Traffic stops.

Instance still running.

Useful for:

```text
maintenance
debugging
controlled draining
```

---

# 51. Connection Draining Mental Model

Before shutdown:

```text
stop new traffic
finish old requests
then exit
```

---

# 52. Draining Flow

```text
UP
   ↓
OUT_OF_SERVICE
   ↓
No New Traffic
   ↓
In-flight Requests Finish
   ↓
Shutdown
```

---

# 53. Metadata + Health Combined

Registry may filter:

```text
only UP instances
AND
only version=v2
AND
only zone=eu-west
```

---

# 54. Combined Filtering Example

Instances:

```text
payment1 → version=v1 → UP
payment2 → version=v2 → UP
payment3 → version=v2 → DOWN
```

Query:

```text
lookupHealthy(version=v2)
```

Returns:

```text
payment2
```

---

# 55. Metadata-Based Routing Engine

Production routing engines use metadata for:

```text
traffic splitting
feature rollout
tenant routing
A/B testing
geo routing
service mesh policies
```

---

# 56. Service Mesh Metadata Routing

Envoy/Istio can route based on:

```text
headers
metadata
version
labels
weights
```

---

# 57. Istio Canary Example

```yaml
v1 weight: 90
v2 weight: 10
```

Traffic split automatically.

---

# 58. Dry Run — Canary Routing

Registry:

```text
payment1 → version=v1
payment2 → version=v1
payment3 → version=v2
```

Router sends:

```text
95% traffic → v1
5% traffic → v2
```

---

# 59. Dry Run — Zone Routing

Instances:

```text
payment1 → zone=eu-west
payment2 → zone=us-east
```

European client receives:

```text
payment1
```

---

# 60. Dry Run — Health Filtering

Registry:

```text
payment1 → UP
payment2 → DOWN
payment3 → OUT_OF_SERVICE
```

lookupHealthy() returns:

```text
payment1
```

only.

---

# 61. Dry Run — Readiness Failure

Kubernetes readiness fails.

Result:

```text
pod removed from EndpointSlice
```

Traffic stops immediately.

---

# 62. Common Production Mistakes

## Mistake 1

```text
routing traffic to STARTING instances
```

App may not be ready.

---

## Mistake 2

```text
using only heartbeat checks
```

App may be alive but unusable.

---

## Mistake 3

```text
deep health checks too expensive
```

Health endpoint itself becomes bottleneck.

---

## Mistake 4

```text
forgetting connection draining
```

Requests fail during deployment.

---

## Mistake 5

```text
incorrect metadata labels
```

Traffic routed incorrectly.

---

# 63. Production Debugging Questions

If traffic behaving incorrectly, ask:

```text
is instance UP?
is readiness failing?
is metadata wrong?
is zone routing correct?
is canary weight correct?
is instance OUT_OF_SERVICE?
```

---

# 64. Strong Interview Answer

Question:

```text
Why is metadata important in service discovery?
```

Strong answer:

```text
Metadata enables intelligent routing decisions beyond simple service lookup.
Modern systems use metadata for canary deployment, weighted routing,
zone-aware routing, version-aware traffic control, environment isolation,
and service mesh policies.
```

---

# 65. Interview Answer — Health Status

Question:

```text
Why is health status important?
```

Strong answer:

```text
Health status ensures traffic only routes to safe instances. Discovery
systems filter unhealthy instances to prevent routing traffic to failing,
starting, overloaded, or intentionally disabled services.
```

---

# 66. Most Important Insight

```text
Metadata makes discovery intelligent.

Health status makes discovery safe.
```

---

# 67. Final Mental Model

```text
Metadata
=
how traffic SHOULD route

Health Status
=
whether traffic CAN route
```

---

# 68. What To Remember

```text
Metadata enables intelligent routing.

Health status enables safe routing.

Version metadata supports canary deployment.

Zone metadata supports low-latency routing.

Weight metadata supports weighted balancing.

Readiness controls traffic eligibility.

Liveness controls restart decisions.

Production systems combine metadata + health filtering.
```

---

# 69. Next File

```text
009_Thread_Safe_InMemory_Registry.md
```

Next you learn:

```text
how to build thread-safe registry
ConcurrentHashMap internals
CopyOnWriteArrayList
race conditions
concurrent registration
concurrent lookup
cleanup synchronization
```
