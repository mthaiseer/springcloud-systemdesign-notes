# 013_RoundRobin_Weighted_LeastConnection_LB.md

# MiniServiceDiscovery — 013 Round Robin, Weighted, Least Connection Load Balancing

---

# 1. Why This File Exists

Service discovery answers:

```text
Which instances are available?
```

But service discovery alone does NOT answer:

```text
Which instance should receive THIS request?
```

Example registry result:

```text
payment-service
    → payment-1
    → payment-2
    → payment-3
```

Now the client or proxy must choose one.

That choice is called:

```text
load balancing
```

This file explains:

```text
why load balancing is needed
Round Robin load balancing
Weighted load balancing
Least Connection load balancing
health-aware filtering
thread-safe counters
Java implementation
dry runs
production tradeoffs
client-side vs server-side placement
Kubernetes / Envoy mapping
interview explanation
```

---

# 2. One-Line Definition

```text
Load balancing distributes requests across multiple healthy service instances.
```

---

# 3. Biggest Mental Model

```text
Service Discovery =
find healthy instances

Load Balancer =
choose one instance
```

---

# 4. Service Discovery vs Load Balancer

## Service Discovery

Returns:

```text
[payment1, payment2, payment3]
```

## Load Balancer

Selects:

```text
payment2
```

for the current request.

---

# 5. Full Request Flow

```text
Order Service
      ↓
lookup(payment-service)
      ↓
[payment1, payment2, payment3]
      ↓
Load Balancer
      ↓
payment2 selected
      ↓
HTTP call
```

---

# 6. Why Load Balancing Is Needed

Without load balancing:

```text
all traffic may go to one instance
```

Example:

```text
payment1 → overloaded
payment2 → idle
payment3 → idle
```

Result:

```text
high latency
timeouts
CPU exhaustion
bad utilization
partial outage
```

---

# 7. Goal Of Load Balancing

A good load balancer tries to distribute traffic:

```text
fairly
efficiently
safely
based on health
based on capacity
based on current load
```

---

# 8. Three Core Algorithms

This file focuses on:

```text
Round Robin
Weighted
Least Connection
```

These are the foundation behind many production routing systems.

---

# 9. Algorithm Comparison

| Algorithm | Main Idea | Best For |
|---|---|---|
| Round Robin | Take turns | equal instances |
| Weighted | more traffic to stronger instances | unequal capacity |
| Least Connection | choose least busy instance | long/variable requests |

---

# 10. Load Balancing Placement

Load balancing can happen:

```text
inside client
```

or:

```text
inside infrastructure/proxy
```

Client-side examples:

```text
Spring Cloud LoadBalancer
Ribbon
gRPC client LB
```

Server-side examples:

```text
Kubernetes Service
Envoy
NGINX
HAProxy
AWS ELB
```


---

# 11. Round Robin Load Balancing

Round Robin means:

```text
take turns in order
```

---

# 12. Round Robin Mental Model

```text
A → B → C → A → B → C
```

---

# 13. Round Robin Example

Instances:

```text
payment1
payment2
payment3
```

Requests:

```text
R1 → payment1
R2 → payment2
R3 → payment3
R4 → payment1
R5 → payment2
R6 → payment3
```

---

# 14. Round Robin ASCII

```text
Request Stream
     ↓
[payment1, payment2, payment3]

R1 → payment1
R2 → payment2
R3 → payment3
R4 → payment1
```

---

# 15. Why Round Robin Popular

Benefits:

```text
simple
fast
fair for equal servers
easy to reason about
minimal metadata needed
```

---

# 16. Round Robin Weakness

Round Robin assumes:

```text
all instances are equal
```

But production instances may differ by:

```text
CPU
memory
region
latency
active connections
current load
hardware size
```

---

# 17. Unequal Capacity Problem

Suppose:

```text
payment1 = 16 CPU
payment2 = 2 CPU
```

Round Robin sends equal traffic.

Problem:

```text
payment2 may overload
```

---

# 18. Round Robin Java Implementation

```java
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer {

    private final AtomicInteger counter =
            new AtomicInteger(0);

    public ServiceInstance choose(
            List<ServiceInstance> instances) {

        if (instances == null || instances.isEmpty()) {
            return null;
        }

        int index =
                Math.abs(counter.getAndIncrement());

        return instances.get(
                index % instances.size()
        );
    }
}
```

---

# 19. Why AtomicInteger Used

Many threads may call:

```java
choose()
```

at the same time.

Need safe counter increment.

---

# 20. Round Robin Dry Run

Instances:

```text
[payment1, payment2, payment3]
```

Counter starts:

```text
0
```

Request 1:

```text
0 % 3 = 0 → payment1
```

Request 2:

```text
1 % 3 = 1 → payment2
```

Request 3:

```text
2 % 3 = 2 → payment3
```

Request 4:

```text
3 % 3 = 0 → payment1
```

---

# 21. Round Robin With Health Filtering

Before Round Robin:

```text
remove unhealthy instances
```

Example:

```text
payment1 → UP
payment2 → DOWN
payment3 → UP
```

Healthy list:

```text
[payment1, payment3]
```

Then Round Robin runs only on healthy list.

---

# 22. Health-Aware Round Robin Code

```java
public ServiceInstance chooseHealthyRoundRobin(
        List<ServiceInstance> instances) {

    List<ServiceInstance> healthy =
            instances.stream()
                    .filter(instance ->
                            instance.getStatus()
                                    == InstanceStatus.UP
                    )
                    .toList();

    return choose(healthy);
}
```


---

# 23. Weighted Load Balancing

Weighted load balancing handles:

```text
unequal instance capacity
```

---

# 24. Weighted Mental Model

```text
Stronger instance gets more traffic.
```

---

# 25. Weight Example

```text
payment1 → weight=3
payment2 → weight=1
```

Traffic ratio:

```text
payment1 gets 3 parts
payment2 gets 1 part
```

So approximately:

```text
payment1 → 75%
payment2 → 25%
```

---

# 26. Why Weighted LB Useful

Useful when instances differ by:

```text
CPU size
memory size
machine type
region cost
network capacity
performance profile
```

---

# 27. Weighted Round Robin

Simple idea:

```text
repeat instance based on weight
```

Example:

```text
payment1 weight=3
payment2 weight=1
```

Expanded list:

```text
[payment1, payment1, payment1, payment2]
```

Requests:

```text
R1 → payment1
R2 → payment1
R3 → payment1
R4 → payment2
R5 → payment1
```

---

# 28. Weighted ASCII

```text
Weight Table

payment1: 3
payment2: 1

Expanded Rotation:
payment1 → payment1 → payment1 → payment2
```

---

# 29. Weighted Java Model

Assume ServiceInstance has:

```java
getWeight()
```

---

# 30. Simple Weighted Implementation

```java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WeightedRoundRobinLoadBalancer {

    private final AtomicInteger counter =
            new AtomicInteger(0);

    public ServiceInstance choose(
            List<ServiceInstance> instances) {

        List<ServiceInstance> expanded =
                new ArrayList<>();

        for (ServiceInstance instance : instances) {

            int weight = Math.max(
                    1,
                    instance.getWeight()
            );

            for (int i = 0; i < weight; i++) {
                expanded.add(instance);
            }
        }

        if (expanded.isEmpty()) {
            return null;
        }

        int index =
                Math.abs(counter.getAndIncrement());

        return expanded.get(
                index % expanded.size()
        );
    }
}
```

---

# 31. Weighted Dry Run

Instances:

```text
payment1 weight=3
payment2 weight=1
```

Expanded:

```text
[payment1, payment1, payment1, payment2]
```

Requests:

```text
R1 → payment1
R2 → payment1
R3 → payment1
R4 → payment2
```

---

# 32. Problem With Expanded Weighted List

If weights are large:

```text
weight=10000
```

expanded list becomes huge.

Memory inefficient.

---

# 33. Production Weighted Algorithms

Production systems often use:

```text
smooth weighted round robin
weighted random
least request with weights
EWMA latency-based balancing
```

---

# 34. Weighted Routing In Service Mesh

Envoy/Istio can route:

```text
v1 → 90%
v2 → 10%
```

This is weighted traffic splitting.

---

# 35. Canary Use Case

Canary deployment:

```text
old version receives 95%
new version receives 5%
```

Metadata + weighted routing enables this.

---

# 36. Weighted Routing Mistake

Wrong weights can overload weak instances.

Example:

```text
small instance weight=10
large instance weight=1
```

Bad configuration.

---

# 37. Weight Mental Model

```text
Weight should represent relative capacity or desired traffic share.
```


---

# 38. Least Connection Load Balancing

Least Connection chooses:

```text
instance with fewest active requests
```

---

# 39. Least Connection Mental Model

```text
Send next request to least busy instance.
```

---

# 40. Why Least Connection Needed

Round Robin does not know:

```text
which instance currently busy
```

Some requests may take:

```text
10 ms
```

Others:

```text
10 seconds
```

Round Robin may overload a slow instance.

---

# 41. Least Connection Example

Active requests:

```text
payment1 → 100 active
payment2 → 20 active
payment3 → 5 active
```

Next request goes to:

```text
payment3
```

---

# 42. Least Connection ASCII

```text
Current Load

payment1: ██████████ 100
payment2: ██ 20
payment3: ▌ 5

Choose:
payment3
```

---

# 43. Least Connection Java Model

Assume instance tracks active connections:

```java
getActiveConnections()
```

---

# 44. Least Connection Implementation

```java
import java.util.Comparator;
import java.util.List;

public class LeastConnectionLoadBalancer {

    public ServiceInstance choose(
            List<ServiceInstance> instances) {

        return instances.stream()
                .filter(instance ->
                        instance.getStatus()
                                == InstanceStatus.UP
                )
                .min(
                        Comparator.comparingInt(
                                ServiceInstance::getActiveConnections
                        )
                )
                .orElse(null);
    }
}
```

---

# 45. Least Connection Dry Run

Input:

```text
payment1 active=20
payment2 active=7
payment3 active=11
```

Minimum:

```text
payment2
```

Selected:

```text
payment2
```

---

# 46. Least Connection Benefits

Good for:

```text
long-running requests
uneven request duration
mixed heavy/light traffic
connection-heavy systems
```

---

# 47. Least Connection Weakness

Needs accurate active connection tracking.

If metrics stale:

```text
wrong routing decisions
```

---

# 48. Active Connection Counter

Each instance may track:

```text
activeConnections
```

When request starts:

```text
increment
```

When request ends:

```text
decrement
```

---

# 49. Active Connection Code

```java
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceInstance {

    private final AtomicInteger activeConnections =
            new AtomicInteger(0);

    public void incrementActiveConnections() {
        activeConnections.incrementAndGet();
    }

    public void decrementActiveConnections() {
        activeConnections.decrementAndGet();
    }

    public int getActiveConnections() {
        return activeConnections.get();
    }
}
```

---

# 50. Request Wrapper

```java
ServiceInstance instance =
        loadBalancer.choose(instances);

instance.incrementActiveConnections();

try {
    call(instance);
} finally {
    instance.decrementActiveConnections();
}
```

---

# 51. Why finally Important

If request fails:

```text
counter still must decrement
```

Otherwise active connection count leaks upward.

---

# 52. Counter Leak Problem

If decrement missing:

```text
activeConnections stays high forever
```

Load balancer avoids healthy instance incorrectly.

---

# 53. Least Connection Production Variant

Production proxies often use:

```text
least request
least outstanding requests
latency-aware load balancing
```

Envoy supports advanced versions.

---

# 54. Least Connection vs Round Robin

Round Robin:

```text
simple but load-unaware
```

Least Connection:

```text
adaptive but needs metrics
```


---

# 55. Health-Aware Load Balancing

Before choosing instance, always filter:

```text
UP instances only
```

Never route to:

```text
DOWN
STARTING
OUT_OF_SERVICE
UNKNOWN
```

---

# 56. Health Filtering Flow

```text
Registry Result
      ↓
Filter Healthy
      ↓
Apply Algorithm
      ↓
Select Instance
```

---

# 57. Health Filtering Example

Registry:

```text
payment1 → UP
payment2 → DOWN
payment3 → OUT_OF_SERVICE
payment4 → UP
```

Healthy list:

```text
[payment1, payment4]
```

---

# 58. Health-Aware Code

```java
public List<ServiceInstance> filterHealthy(
        List<ServiceInstance> instances) {

    return instances.stream()
            .filter(instance ->
                    instance.getStatus()
                            == InstanceStatus.UP
            )
            .toList();
}
```

---

# 59. Zone-Aware Load Balancing

Prefer same zone or region.

Example:

```text
client zone = eu-west
```

Prefer:

```text
instances with zone=eu-west
```

---

# 60. Zone-Aware Benefit

Benefits:

```text
lower latency
less cross-region traffic
better fault isolation
lower cloud cost
```

---

# 61. Zone-Aware Flow

```text
Filter healthy
      ↓
Filter same zone
      ↓
If same zone empty, fallback to other zones
      ↓
Apply load balancer
```

---

# 62. Metadata-Aware Routing

Load balancer can use metadata:

```text
version
zone
weight
environment
canary
protocol
```

---

# 63. Canary Routing Example

```text
v1 weight=95
v2 weight=5
```

Requests split:

```text
95% v1
5% v2
```

---

# 64. Circuit Breaker Integration

If selected instance fails repeatedly:

```text
circuit breaker opens
```

Then load balancer should avoid that instance temporarily.

---

# 65. Retry Integration

If selected instance fails:

```text
retry may choose another instance
```

But retries must be limited.

Too many retries cause:

```text
retry storm
```

---

# 66. Timeout Integration

Load balancing needs timeouts.

Otherwise a slow selected instance may block:

```text
client threads
```

---

# 67. Production Routing Stack

```text
Service Discovery
      ↓
Health Filtering
      ↓
Load Balancer
      ↓
Timeout
      ↓
Retry
      ↓
Circuit Breaker
      ↓
Remote Call
```

---

# 68. Client-Side Load Balancing

Client-side means:

```text
client chooses instance
```

Examples:

```text
Spring Cloud LoadBalancer
Ribbon
OpenFeign integration
```

---

# 69. Server-Side Load Balancing

Server-side means:

```text
proxy/load balancer chooses instance
```

Examples:

```text
Kubernetes Service
Envoy
NGINX
HAProxy
AWS ELB
```

---

# 70. Kubernetes Load Balancing

Kubernetes Service uses:

```text
Service
EndpointSlice
kube-proxy
iptables/ipvs
```

to distribute traffic to pods.

---

# 71. Envoy / Istio Load Balancing

Envoy supports:

```text
round robin
least request
random
ring hash
weighted clusters
locality-aware routing
```

---

# 72. Spring Cloud LoadBalancer

Spring Cloud LoadBalancer typically works with:

```text
service instance supplier
health check filtering
round robin selection
```

---

# 73. Common Production Mistakes

## Mistake 1

```text
routing without health filtering
```

Dead instances receive traffic.

---

## Mistake 2

```text
using Round Robin for unequal servers
```

Weak servers overload.

---

## Mistake 3

```text
bad weights
```

Traffic distribution becomes wrong.

---

## Mistake 4

```text
least connection with stale metrics
```

Wrong instance selected.

---

## Mistake 5

```text
no timeout
```

Slow instance blocks client.

---

## Mistake 6

```text
unbounded retries
```

Retry storm.

---

# 74. Debugging Questions

If traffic distribution looks wrong, ask:

```text
are all instances healthy?
are weights correct?
is active connection count accurate?
is client registry cache stale?
is algorithm working as expected?
is one instance slower than others?
are retries amplifying traffic?
```

---

# 75. Dry Run — Full Health + Round Robin

Registry:

```text
payment1 UP
payment2 DOWN
payment3 UP
```

Filter:

```text
[payment1, payment3]
```

Round Robin:

```text
R1 → payment1
R2 → payment3
R3 → payment1
```

---

# 76. Dry Run — Weighted Canary

Instances:

```text
payment-v1 weight=95
payment-v2 weight=5
```

Traffic:

```text
mostly v1
small portion v2
```

Used for canary rollout.

---

# 77. Dry Run — Least Connection

Active:

```text
payment1 = 30
payment2 = 4
payment3 = 18
```

Choose:

```text
payment2
```

---

# 78. Dry Run — Retry Another Instance

Selected:

```text
payment2
```

payment2 timeout.

Retry selects:

```text
payment3
```

Circuit breaker records payment2 failure.

---

# 79. Strong Interview Answer

Question:

```text
Explain Round Robin, Weighted, and Least Connection load balancing.
```

Strong answer:

```text
Round Robin sends requests sequentially across instances and works well
when instances are equal. Weighted load balancing sends more traffic to
instances with higher configured capacity. Least Connection chooses the
instance with the fewest active requests and works better when request
durations vary.
```

Senior addition:

```text
In production, these algorithms are combined with health-aware filtering,
timeouts, retries, circuit breakers, metadata-aware routing, zone awareness,
and stale registry protection.
```

---

# 80. Most Important Insight

```text
Load balancing is not just fairness.
It is traffic safety and capacity management.
```

---

# 81. Final Mental Model

```text
Round Robin
=
take turns

Weighted
=
stronger gets more

Least Connection
=
least busy gets next
```

---

# 82. What To Remember

```text
Service discovery finds instances.

Load balancing chooses one.

Round Robin is simple.

Weighted handles different capacities.

Least Connection handles dynamic load.

Health filtering must happen before selection.

Client-side LB gives local control.

Server-side LB centralizes routing.

Production LB works with retries, timeouts, and circuit breakers.
```

---

# 83. Next File

```text
014_Client_Side_vs_Server_Side_LoadBalancing.md
```

Next you learn:

```text
client-side load balancing
server-side load balancing
Ribbon/Spring Cloud LoadBalancer
Kubernetes/Envoy routing
tradeoffs
production architectures
```
