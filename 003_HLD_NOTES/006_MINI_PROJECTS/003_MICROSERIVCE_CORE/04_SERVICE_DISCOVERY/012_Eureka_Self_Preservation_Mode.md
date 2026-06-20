# 012_Eureka_Self_Preservation_Mode.md

# MiniServiceDiscovery — Eureka Self Preservation Mode

---

# 1. Why This File Exists

One of the hardest problems in distributed systems is:

```text
network partition
```

Suppose suddenly:

```text
thousands of heartbeats disappear
```

Registry now faces dangerous question:

```text
Did all services actually die?
OR
Did network connectivity break temporarily?
```

If registry aggressively removes all instances:

```text
healthy services disappear from registry
```

Entire platform may collapse.

To prevent this, Eureka introduced:

```text
Self Preservation Mode
```

This is one of the most important concepts in:

```text
service discovery
distributed systems
availability engineering
microservice resilience
```

This file explains:

```text
why self-preservation needed
network partition problem
mass heartbeat loss
false eviction problem
availability vs consistency tradeoff
Eureka self-preservation internals
renewal threshold calculation
eviction suppression
split brain scenarios
production tuning
Java mental model
dry runs
interview explanation
```

---

# 2. One-Line Definition

```text
Eureka self-preservation mode temporarily stops aggressive instance eviction during abnormal heartbeat drops to avoid mass accidental removal.
```

---

# 3. Biggest Mental Model

```text
Better stale registry
than deleting half the cluster accidentally.
```

---

# 4. Core Distributed Systems Problem

Suppose system has:

```text
1000 service instances
```

All send heartbeats normally.

Suddenly registry receives only:

```text
200 heartbeats
```

Question:

```text
Did 800 services die?
OR
Did network break?
```

Registry cannot know immediately.

---

# 5. Dangerous Situation

If registry aggressively removes all missing services:

```text
healthy services removed from registry
```

Clients cannot discover them anymore.

Entire system becomes unavailable.

---

# 6. Network Partition Mental Model

```text
Services alive
BUT
registry temporarily cannot hear them
```

---

# 7. Network Partition ASCII

```text
Services Alive
      ↓ X network broken X
Eureka Server

Heartbeats blocked
```

---

# 8. Without Self Preservation

Registry behavior:

```text
heartbeat missing
      ↓
TTL expires
      ↓
mass eviction
      ↓
registry becomes mostly empty
```

Clients fail to discover services.

---

# 9. Catastrophic Eviction ASCII

```text
1000 instances registered

network issue happens

registry evicts 800 instances
      ↓
clients see empty registry
      ↓
system outage
```

---

# 10. Eureka Solution

Instead of aggressively evicting:

```text
pause eviction temporarily
```

This is:

```text
self-preservation mode
```

---

# 11. Self Preservation Mental Model

```text
Registry protects itself
from making catastrophic cleanup decisions.
```

---

# 12. Eureka Philosophy

Eureka prefers:

```text
availability
```

over:

```text
strict consistency
```

---

# 13. CAP Tradeoff

During partition:

```text
Eureka chooses Availability
```

Meaning:

```text
possibly stale registry
BUT
service discovery still works
```

---

# 14. Why Stale Registry Better

Stale registry may contain:

```text
some dead instances
```

But clients still discover many healthy services.

Better than:

```text
empty registry
```

---

# 15. Biggest Insight

```text
A partially stale registry is often safer than an aggressively cleaned registry.
```

---

# 16. Eureka Renewal Concept

Eureka tracks:

```text
expected heartbeat renewals
```

and:

```text
actual heartbeat renewals
```

---

# 17. Renewal Mental Model

Registry estimates:

```text
How many heartbeats SHOULD arrive?
```

Then compares:

```text
How many actually arrived?
```

---

# 18. Example

Suppose:

```text
100 instances
```

Heartbeat interval:

```text
30 sec
```

Expected renewals/min:

```text
200 heartbeats/min
```

---

# 19. Renewal Threshold

Eureka calculates:

```text
renewal threshold
```

Usually around:

```text
85% of expected renewals
```

---

# 20. Threshold Example

Expected renewals:

```text
200/min
```

Threshold:

```text
170/min
```

If actual renewals fall below:

```text
170
```

Eureka suspects abnormal event.

---

# 21. Self Preservation Trigger

Condition:

```text
actual renewals < threshold
```

Then:

```text
self-preservation activates
```

---

# 22. Trigger ASCII

```text
Expected Renewals = 200/min
Threshold = 170/min

Actual = 80/min
      ↓
Self Preservation Enabled
```

---

# 23. What Happens During Self Preservation

Eureka:

```text
reduces/stops aggressive eviction
```

Meaning:

```text
expired instances may temporarily remain
```

---

# 24. Why This Helps

Even if registry stale:

```text
many healthy services still discoverable
```

System continues operating.

---

# 25. Tradeoff

Benefit:

```text
prevents catastrophic mass eviction
```

Cost:

```text
registry may contain stale entries
```

---

# 26. Self Preservation Tradeoff Mental Model

```text
Risk stale routing
instead of
risk total discovery collapse
```

---

# 27. Internal Eureka Variables

Eureka internally tracks:

```text
expectedNumberOfClientsSendingRenews
numberOfRenewsPerMinThreshold
actualRenewalsPerMin
```

---

# 28. Simplified Threshold Formula

```text
threshold =
expected renewals × 0.85
```

---

# 29. Example Calculation

Instances:

```text
100
```

Heartbeat every:

```text
30 sec
```

Expected renewals/min:

```text
200
```

Threshold:

```text
200 × 0.85 = 170
```

---

# 30. Normal Operation

Actual renewals:

```text
195/min
```

Above threshold.

Eviction works normally.

---

# 31. Partition Scenario

Actual renewals suddenly:

```text
70/min
```

Below threshold.

Eureka suspects:

```text
network partition
```

Self-preservation activates.

---

# 32. Self Preservation ASCII

```text
Heartbeat Drop
      ↓
Threshold Violated
      ↓
Eviction Suspended
      ↓
Registry Preserved
```

---

# 33. Why This Is Important

Without self-preservation:

```text
temporary network issue
```

could wipe entire registry.

---

# 34. Split Brain Scenario

Suppose:

```text
registry cluster partitions
```

Different Eureka nodes see different heartbeats.

Without protection:

```text
mass inconsistent eviction possible
```

---

# 35. Split Brain ASCII

```text
Region A sees service alive
Region B sees service missing
```

Self-preservation reduces aggressive cleanup.

---

# 36. Availability vs Consistency

## Strict Consistency

Immediately remove missing instances.

Risk:

```text
mass false eviction
```

---

## High Availability

Keep stale entries temporarily.

Risk:

```text
some stale routing
```

---

# 37. Eureka Choice

Eureka intentionally chooses:

```text
Availability
```

because discovery outage worse than stale routing.

---

# 38. Why Discovery Outage Dangerous

If registry empty:

```text
all service lookups fail
```

Entire platform may stop functioning.

---

# 39. Stale Routing Easier To Handle

Clients can still recover using:

```text
retry
timeout
circuit breaker
load balancing
health checks
```

Much safer.

---

# 40. Relationship With Circuit Breaker

Even if stale instance returned:

```text
Circuit breaker prevents cascading failures.
```

---

# 41. Combined Resilience Model

```text
Eureka self-preservation
+
timeouts
+
retries
+
circuit breakers
+
load balancing
```

Together improve resilience.

---

# 42. Eureka Eviction Task

Normally Eureka periodically:

```text
evicts expired leases
```

During self-preservation:

```text
eviction slowed or paused
```

---

# 43. Simplified Internal Logic

```java
if (actualRenewals < renewalThreshold) {

    selfPreservationEnabled = true;

} else {

    selfPreservationEnabled = false;
}
```

---

# 44. Eviction Logic

```java
if (!selfPreservationEnabled) {

    evictExpiredInstances();
}
```

---

# 45. Why Self Preservation Not Perfect

Registry may keep:

```text
dead instances longer
```

This may cause:

```text
some failed requests
```

But overall availability remains higher.

---

# 46. Production Tuning

Teams may configure:

```text
renewal threshold
eviction interval
lease duration
self-preservation enable/disable
```

---

# 47. Dangerous Misconfiguration

If threshold too sensitive:

```text
self-preservation triggers too often
```

Registry becomes very stale.

---

# 48. Another Dangerous Misconfiguration

If self-preservation disabled entirely:

```text
network partition may wipe registry
```

Very dangerous in distributed environments.

---

# 49. Why Some Teams Disable It

In small/local systems:

```text
stale registry may be more annoying
```

But in large distributed systems:

```text
self-preservation usually safer
```

---

# 50. Kubernetes Difference

Kubernetes typically uses:

```text
stronger centralized control plane
continuous state reconciliation
```

instead of Eureka-style self-preservation.

---

# 51. Kubernetes Philosophy

Kubernetes prefers:

```text
more consistent cluster state
```

using controllers and reconciliation loops.

---

# 52. Eureka vs Kubernetes Mental Model

## Eureka

```text
AP-oriented
availability focused
```

---

## Kubernetes

```text
more consistency/reconciliation focused
```

---

# 53. Real Production Example

Suppose AWS network issue causes:

```text
heartbeat delays
```

Without self-preservation:

```text
registry may evict thousands of healthy services
```

With self-preservation:

```text
registry remains mostly stable
```

---

# 54. Dry Run — Normal Operation

Instances:

```text
100
```

Expected renewals:

```text
200/min
```

Threshold:

```text
170/min
```

Actual renewals:

```text
190/min
```

Result:

```text
normal eviction mode
```

---

# 55. Dry Run — Partition

Network issue occurs.

Actual renewals:

```text
60/min
```

Below threshold.

Result:

```text
self-preservation enabled
```

Eviction paused.

---

# 56. Dry Run — Recovery

Network restored.

Actual renewals return:

```text
195/min
```

Above threshold.

Result:

```text
self-preservation disabled
normal eviction resumes
```

---

# 57. Production Monitoring Metrics

Important metrics:

```text
renewal threshold
actual renewals/min
eviction count
self-preservation active flag
heartbeat latency
lease expiration count
```

---

# 58. Debugging Questions

If registry becomes stale:

```text
is self-preservation enabled?
are heartbeats delayed?
is network partition happening?
is threshold configured correctly?
```

---

# 59. Common Production Mistakes

## Mistake 1

```text
disabling self-preservation blindly
```

Dangerous.

---

## Mistake 2

```text
TTL too aggressive
```

False eviction spikes.

---

## Mistake 3

```text
not monitoring renewal metrics
```

Hard to debug partitions.

---

## Mistake 4

```text
confusing stale registry with broken registry
```

Stale registry often safer.

---

## Mistake 5

```text
heartbeat interval too short
```

Excessive sensitivity.

---

# 60. Strong Interview Answer

Question:

```text
What is Eureka self-preservation mode?
```

Strong answer:

```text
Eureka self-preservation mode temporarily suspends aggressive instance
eviction when heartbeat renewals drop below a configured threshold.
This protects against mass accidental eviction during network partitions
or transient infrastructure failures.
```

Senior addition:

```text
Eureka intentionally prefers availability over strict consistency during
partitions. A stale registry containing some dead instances is usually
safer than an empty registry where service discovery completely fails.
```

---

# 61. Most Important Insight

```text
Self-preservation protects the registry from making catastrophic cleanup decisions during uncertainty.
```

---

# 62. Final Mental Model

```text
Heartbeat loss spike
=
possible network partition

Self-preservation
=
pause aggressive eviction

Goal
=
keep discovery available
```

---

# 63. What To Remember

```text
Network partitions can look like mass service failure.

Eureka tracks expected vs actual renewals.

Low renewals trigger self-preservation.

Self-preservation pauses aggressive eviction.

Eureka prefers availability over consistency.

Stale registry often safer than empty registry.

Circuit breakers help tolerate stale routing.

Monitoring renewal metrics is critical.
```

---

# 64. Next File

```text
013_RoundRobin_Weighted_LeastConnection_LB.md
```

Next you learn:

```text
how clients choose instances
round robin balancing
weighted balancing
least connections
health-aware routing
load balancing strategies
```
