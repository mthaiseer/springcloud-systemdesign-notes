# 016_Network_Partition_Stale_Registry_SplitBrain.md

# Network Partition, Stale Registry, and Split Brain

## MiniServiceDiscovery Series

This chapter explains one of the most important distributed-systems failures in service discovery:

> What happens when registry nodes, service instances, and clients cannot fully talk to each other, but the system is still partially alive?

This is where service discovery stops being only a lookup table and becomes a real distributed systems problem.

---

# 1. Why This Topic Matters

A service registry answers:

```text
Where is service X running right now?
```

Example:

```text
Payment-Service =
[
  10.0.0.11:8080,
  10.0.0.12:8080,
  10.0.0.13:8080
]
```

But in real systems, the answer may be stale.

An instance may be:

- dead but still registered
- alive but unreachable
- healthy in one zone but invisible from another zone
- removed in one registry node but still present in another
- cached by a client even after registry correction

This creates:

- stale registry
- wrong routing
- zombie instances
- split brain
- inconsistent discovery
- retry storms
- cascading failure

Core lesson:

> Registry data is a recent hint, not an absolute truth.

---

# 2. Basic Mental Model

Normal condition:

```text
Client
  ↓
Registry
  ↓
Healthy Instance List
  ↓
Payment-Service
```

Partition condition:

```text
Client-A sees Registry-A
Client-B sees Registry-B

Registry-A and Registry-B disagree.
```

Now two clients can receive different answers for the same service.

That is the beginning of stale registry and split-brain behavior.

---

# 3. What Is Network Partition?

A network partition happens when two parts of a distributed system cannot communicate.

Important:

The machines may still be alive.

Only communication is broken.

Before partition:

```text
Registry-A  <---->  Registry-B
     |                  |
 Service-1          Service-2
```

After partition:

```text
Registry-A   X   Registry-B
     |             |
 Service-1     Service-2
```

Both registry nodes are alive.

But they cannot exchange updates.

---

# 4. Real-World Analogy

Imagine two hotel reception desks.

Desk-A says:

```text
Room 101 occupied
Room 102 free
```

Desk-B says:

```text
Room 101 free
Room 102 occupied
```

Because the communication system between desks broke.

Now both desks may assign rooms incorrectly.

In service discovery:

```text
Registry-A says Instance-X is UP.
Registry-B says Instance-X is DOWN.
```

Both may believe they are correct.

---

# 5. Stale Registry Problem

A stale registry contains old instance information.

Example:

```text
Registry says:

Payment-Service:
- 10.0.0.5:8080
- 10.0.0.6:8080
```

Actual state:

```text
10.0.0.5 is dead
10.0.0.6 is healthy
```

Client result:

```text
Client -> 10.0.0.5 -> timeout
```

Impact:

- failed requests
- retries
- latency spike
- thread pool exhaustion
- user-visible errors
- cascading failure

---

# 6. Why Stale Registry Happens

Main causes:

1. Missed heartbeat
2. Delayed heartbeat processing
3. Registry replication lag
4. Network partition
5. Client-side discovery cache not refreshed
6. DNS cache TTL too high
7. Self-preservation mode keeps old entries
8. Slow failure detection
9. Bad shutdown without deregistration
10. Endpoint update delay in Kubernetes

Important production truth:

```text
Instance death is immediate.
Registry knowledge is delayed.
Client knowledge is even more delayed.
```

---

# 7. Registry Data Is Always Slightly Old

There is always a delay between:

```text
Instance dies
```

and:

```text
All clients stop calling it
```

Timeline:

```text
T0: Instance dies
T1: Heartbeat missed
T2: Registry marks instance DOWN
T3: Registry replicates update
T4: Client refreshes cache
T5: Traffic finally stops
```

Between T0 and T5, stale traffic can happen.

This time period is called the stale window or inconsistency window.

---

# 8. Stale Window Formula

Mental model:

```text
Stale Window =
heartbeat interval
+ failure threshold delay
+ registry replication delay
+ client cache refresh delay
+ DNS / LB update delay
```

Example:

```text
Heartbeat interval       = 30 sec
Failure threshold        = 3 missed heartbeats
Registry replication     = 5 sec
Client cache refresh     = 30 sec

Stale Window ≈ 30*3 + 5 + 30
             ≈ 125 sec
```

For almost 2 minutes, some clients may still call a dead instance.

This is why client-side timeout, retry, circuit breaker, and local failure cache are mandatory.

---

# 9. Zombie Instance

A zombie instance is:

```text
Still registered,
but not actually usable.
```

Examples:

- process is stuck
- deadlock
- DB connection pool exhausted
- Kafka producer stuck
- pod terminating
- application alive but not ready
- instance unreachable from client zone

Registry may say:

```text
UP
```

Reality:

```text
Cannot serve traffic.
```

Health-aware load balancing from chapter 015 reduces this, but stale registry can still expose zombie instances temporarily.

---

# 10. Split Brain

Split brain happens when two partitions both believe they are authoritative.

Registry cluster:

```text
Registry-A  Registry-B  Registry-C
```

Network partition:

```text
Partition-1              Partition-2

Registry-A               Registry-B
Registry-C
```

If both sides accept updates:

```text
Partition-1:
Order-Service = A1, A2

Partition-2:
Order-Service = B1, B2
```

Now the system has two different truths.

That is split brain.

---

# 11. Why Split Brain Is Dangerous

Split brain can cause:

- duplicate leaders
- conflicting registry data
- stale service lists
- traffic routed to wrong region
- inconsistent health status
- failed failover
- inconsistent ownership
- data corruption in stateful systems

For service discovery, the common failure is:

```text
Different clients see different instance lists.
```

Example:

```text
Client-1 sees:
Payment = [A, B]

Client-2 sees:
Payment = [C, D]
```

Both clients think they are correct.

---

# 12. CAP Theorem Quick Mental Model

CAP:

```text
C = Consistency
A = Availability
P = Partition Tolerance
```

In distributed systems:

```text
Partitions can happen.
```

So P is mandatory.

During a partition, a system usually chooses:

```text
CP: Consistency over Availability
AP: Availability over Consistency
```

Service discovery often chooses availability because returning a slightly stale service list may be better than returning no service list.

But this depends on the system.

---

# 13. AP Registry Design

AP means:

```text
Availability + Partition Tolerance
```

During partition:

```text
Keep answering discovery requests,
even if data may be stale.
```

Typical behavior:

```text
Client asks for Payment-Service.
Registry returns best-known instance list.
```

Benefits:

- clients keep receiving answers
- partial system remains usable
- better during transient network issues

Risks:

- stale data
- inconsistent views
- zombie instances may remain visible

Eureka is commonly discussed as AP-oriented.

---

# 14. CP Registry Design

CP means:

```text
Consistency + Partition Tolerance
```

During partition:

```text
Only quorum side continues safely.
Minority side refuses writes or strong reads.
```

Benefits:

- avoids split brain
- preserves consistency
- one source of truth

Risks:

- minority partition becomes unavailable
- clients may fail discovery calls

ZooKeeper and etcd are commonly used as CP-style coordination systems.

---

# 15. Quorum Mental Model

Quorum means majority agreement.

```text
N = 3  -> majority = 2
N = 5  -> majority = 3
N = 7  -> majority = 4
```

Why majority prevents split brain:

For N = 5:

```text
Any majority has 3 nodes.
Two different groups of 3 must overlap.
```

That overlap prevents two independent leaders from safely existing.

---

# 16. Leader Election Split Brain

Suppose:

```text
Leader = Registry-A
Followers = Registry-B, Registry-C
```

Partition:

```text
Registry-A separated from B and C
```

Bad behavior:

```text
Registry-A still thinks it is leader.
Registry-B and Registry-C elect Registry-B as new leader.
```

Now:

```text
Two leaders exist.
```

This is split brain.

CP systems avoid this by requiring quorum.

Old leader without quorum must step down.

---

# 17. Eureka Self-Preservation Connection

Without self-preservation:

```text
Network issue
↓
Many heartbeats missed
↓
Registry evicts many instances
↓
Clients receive empty registry
↓
System outage
```

With self-preservation:

```text
Network issue
↓
Many heartbeats missed
↓
Registry suspects network issue
↓
Avoids mass eviction
↓
Clients still get previous registry
```

Tradeoff:

```text
Availability increases.
Freshness decreases.
```

This means Eureka may intentionally serve stale data during network trouble.

---

# 18. Registry Replication Delay

Example:

```text
Registry-A receives update:
Inventory-1 DOWN
```

But Registry-B has not received it yet.

Client-1 asks Registry-A:

```text
Inventory-1 removed
```

Client-2 asks Registry-B:

```text
Inventory-1 still present
```

Different clients get different answers.

This is eventual consistency.

---

# 19. Client-Side Cache Staleness

Even if registry becomes correct, clients can still be stale.

Example:

```text
Client cache refresh interval = 30 seconds
```

At T0:

```text
Registry removes dead instance.
```

But client refreshes at T30.

Between T0 and T30:

```text
Client may still call dead instance.
```

Therefore clients need:

- request timeout
- connection timeout
- retry different instance
- circuit breaker
- local failure cache
- health-aware load balancing

Never assume:

```text
Registry says UP == definitely healthy now.
```

Correct assumption:

```text
Registry says UP == probably healthy recently.
```

---

# 20. DNS Staleness

DNS-based service discovery can also become stale.

Example in Kubernetes:

```text
payment.default.svc.cluster.local
```

If pod IP changes, DNS clients may still cache the old IP.

High TTL:

```text
less DNS traffic,
more stale routing.
```

Low TTL:

```text
fresher routing,
more DNS load.
```

This is a tradeoff.

---

# 21. Kubernetes Endpoint Staleness

Kubernetes flow:

```text
Pod readiness changes
↓
EndpointSlice updated
↓
kube-proxy updates routing
↓
CoreDNS sees endpoints
↓
Clients route traffic
```

Delay can occur at every step.

So stale routing is still possible.

Mitigation:

- readiness probes
- graceful shutdown
- preStop hooks
- connection draining
- retries
- timeouts
- load balancer draining

---

# 22. Graceful Shutdown

Bad shutdown:

```text
Pod receives SIGTERM
↓
Pod exits immediately
↓
Registry / endpoint still has pod briefly
↓
Traffic fails
```

Good shutdown:

```text
Pod receives SIGTERM
↓
Readiness becomes false
↓
Endpoint removed
↓
Wait for drain
↓
Finish in-flight requests
↓
Exit
```

This reduces stale registry failures.

---

# 23. Correct Shutdown Flow

```text
1. Receive termination signal
2. Stop accepting new traffic
3. Mark readiness = false
4. Deregister from registry
5. Wait for LB/client cache drain
6. Finish in-flight requests
7. Exit process
```

This applies to:

- Kubernetes
- Eureka
- Consul
- custom service registries
- service mesh sidecars

---

# 24. Failure Scenario: Instance Dies Suddenly

```text
Payment-1 crashes
↓
No deregistration call
↓
Registry waits for heartbeat timeout
↓
Clients still call Payment-1
↓
Failures occur
```

Mitigation:

- heartbeat TTL
- active health checks
- passive failure detection
- circuit breaker
- retry another instance
- local failure cache

---

# 25. Failure Scenario: Instance Cannot Reach Registry

```text
Payment-1 is healthy.
But Payment-1 cannot reach Registry.
```

Registry sees:

```text
No heartbeat.
```

Possible interpretations:

1. instance died
2. network broke
3. registry overloaded
4. instance GC pause
5. packet loss

If evicted too quickly:

```text
healthy capacity removed.
```

If not evicted:

```text
possibly dead instance remains.
```

This is the tradeoff.

---

# 26. Failure Scenario: Client Cannot Reach Registry

```text
Client cannot reach registry.
But client has old cache.
```

Options:

1. fail immediately
2. use stale cache
3. use fallback endpoint
4. retry registry later

Most client-side discovery systems use stale cache for some time.

Reason:

```text
stale data is often better than no data.
```

But stale cache must have max age.

---

# 27. Failure Scenario: Cross-AZ Partition

```text
AZ-1 cannot reach AZ-2
```

Registry nodes in each AZ may see different instance health.

Clients in AZ-1 may route locally.

Clients in AZ-2 may route locally.

When partition heals:

```text
registry states must reconcile.
```

This is why zone-aware routing matters.

---

# 28. Zone-Aware Discovery

Instead of:

```text
pick any global instance
```

Prefer:

```text
1. same zone healthy instance
2. same region healthy instance
3. cross-region fallback
```

This reduces blast radius.

During partition:

```text
clients keep using local healthy instances.
```

---

# 29. Anti-Entropy Repair

Anti-entropy means background reconciliation.

Registry nodes periodically compare state.

Example:

```text
Registry-A state
Registry-B state

Compare versions
Repair differences
```

Common techniques:

- version numbers
- timestamps
- vector clocks
- gossip
- full sync
- delta sync

Anti-entropy is how eventual consistency heals.

---

# 30. Gossip Protocol Mental Model

Instead of one master broadcasting everything:

```text
Node-A tells Node-B
Node-B tells Node-C
Node-C tells Node-D
```

Information spreads like a rumor.

Advantages:

- scalable
- decentralized
- resilient

Disadvantages:

- eventually consistent
- not instant
- temporary stale data

Consul uses gossip-style membership detection.

---

# 31. Versioned Registry Records

Each instance record should carry version metadata.

```java
class InstanceRecord {
    String serviceName;
    String instanceId;
    String host;
    int port;

    InstanceStatus status;

    long version;
    long lastUpdatedAt;
}
```

If two registries disagree:

```text
higher version can win
```

But be careful:

Wall-clock timestamps are unsafe under clock skew.

---

# 32. Instance Status State Machine

```text
STARTING
   ↓
UP
   ↓
SUSPECT
   ↓
DOWN
   ↓
REMOVED
```

During partition:

```text
UP -> SUSPECT
```

Avoid immediate removal.

Reason:

The instance may be healthy but unreachable from one registry node.

---

# 33. Split Brain State Machine

```text
NORMAL
  ↓
PARTITION_DETECTED
  ↓
DEGRADED_MODE
  ↓
RECOVERY_SYNC
  ↓
NORMAL
```

AP degraded mode:

```text
continue serving best-known data
```

CP degraded mode:

```text
only quorum side accepts decisions
```

---

# 34. Java Simulation: Service Instance

```java
enum InstanceStatus {
    STARTING,
    UP,
    SUSPECT,
    DOWN,
    REMOVED
}

class ServiceInstance {
    final String serviceName;
    final String instanceId;
    final String host;
    final int port;

    volatile InstanceStatus status;
    volatile long lastHeartbeatTime;
    volatile long version;

    ServiceInstance(String serviceName, String instanceId, String host, int port) {
        this.serviceName = serviceName;
        this.instanceId = instanceId;
        this.host = host;
        this.port = port;
        this.status = InstanceStatus.STARTING;
        this.lastHeartbeatTime = System.currentTimeMillis();
        this.version = 1;
    }

    boolean isRoutable() {
        return status == InstanceStatus.UP;
    }
}
```

---

# 35. Java Simulation: Registry Node

```java
import java.util.*;
import java.util.concurrent.*;

class RegistryNode {

    private final String nodeId;

    private final ConcurrentHashMap<String, ServiceInstance> instances =
            new ConcurrentHashMap<>();

    private volatile boolean partitioned = false;

    RegistryNode(String nodeId) {
        this.nodeId = nodeId;
    }

    public void register(ServiceInstance instance) {
        instance.status = InstanceStatus.UP;
        instance.lastHeartbeatTime = System.currentTimeMillis();
        instance.version++;

        instances.put(instance.instanceId, instance);

        System.out.println(nodeId + " registered " + instance.instanceId);
    }

    public void heartbeat(String instanceId) {
        ServiceInstance instance = instances.get(instanceId);

        if (instance == null) {
            return;
        }

        instance.lastHeartbeatTime = System.currentTimeMillis();

        if (instance.status == InstanceStatus.SUSPECT) {
            instance.status = InstanceStatus.UP;
        }

        instance.version++;
    }

    public List<ServiceInstance> discover(String serviceName) {
        List<ServiceInstance> result = new ArrayList<>();

        for (ServiceInstance instance : instances.values()) {
            if (instance.serviceName.equals(serviceName) && instance.isRoutable()) {
                result.add(instance);
            }
        }

        return result;
    }

    public void markPartitioned(boolean value) {
        this.partitioned = value;
    }

    public boolean isPartitioned() {
        return partitioned;
    }

    public Collection<ServiceInstance> allInstances() {
        return instances.values();
    }
}
```

---

# 36. Java Simulation: Heartbeat Expiry

```java
class HeartbeatMonitor {

    private final RegistryNode registryNode;
    private final long ttlMillis;

    HeartbeatMonitor(RegistryNode registryNode, long ttlMillis) {
        this.registryNode = registryNode;
        this.ttlMillis = ttlMillis;
    }

    public void scan() {
        long now = System.currentTimeMillis();

        for (ServiceInstance instance : registryNode.allInstances()) {
            long age = now - instance.lastHeartbeatTime;

            if (age > ttlMillis && instance.status == InstanceStatus.UP) {
                instance.status = InstanceStatus.SUSPECT;
                instance.version++;

                System.out.println(
                    "Instance " + instance.instanceId + " marked SUSPECT"
                );
            }

            if (age > ttlMillis * 2 && instance.status == InstanceStatus.SUSPECT) {
                instance.status = InstanceStatus.DOWN;
                instance.version++;

                System.out.println(
                    "Instance " + instance.instanceId + " marked DOWN"
                );
            }
        }
    }
}
```

---

# 37. Java Simulation: AP Behavior

```java
class APDiscoveryPolicy {

    public List<ServiceInstance> discoverEvenDuringPartition(
            RegistryNode registry,
            String serviceName
    ) {
        return registry.discover(serviceName);
    }
}
```

Meaning:

```text
Even if partitioned, return best-known data.
```

Pros:

- available
- clients keep working

Cons:

- may be stale

---

# 38. Java Simulation: CP Behavior

```java
class CPDiscoveryPolicy {

    private final int totalNodes;
    private final int reachableNodes;

    CPDiscoveryPolicy(int totalNodes, int reachableNodes) {
        this.totalNodes = totalNodes;
        this.reachableNodes = reachableNodes;
    }

    public boolean hasQuorum() {
        return reachableNodes > totalNodes / 2;
    }

    public List<ServiceInstance> discover(
            RegistryNode registry,
            String serviceName
    ) {
        if (!hasQuorum()) {
            throw new IllegalStateException("No quorum. Refusing discovery.");
        }

        return registry.discover(serviceName);
    }
}
```

Meaning:

```text
If no quorum, stop serving possibly unsafe data.
```

---

# 39. Full Dry Run: AP Partition

Setup:

```text
Registry-A
Registry-B

Payment-1 registered in both
```

Partition:

```text
Registry-A X Registry-B
```

Payment-1 dies near Registry-A.

Registry-A marks:

```text
Payment-1 DOWN
```

Registry-B does not receive update.

Registry-B still says:

```text
Payment-1 UP
```

Client connected to Registry-B calls Payment-1.

Result:

```text
timeout
```

AP system stayed available but returned stale data.

---

# 40. Full Dry Run: CP Partition

Setup:

```text
5 registry nodes
```

Partition:

```text
Side A = 3 nodes
Side B = 2 nodes
```

Side A:

```text
has quorum
continues
```

Side B:

```text
no quorum
refuses writes
```

Result:

- no split brain
- minority unavailable
- consistency preserved

---

# 41. How Clients Should Handle Stale Registry

Client must assume registry can be stale.

Client-side protections:

1. connection timeout
2. request timeout
3. retry different instance
4. circuit breaker
5. local failure cache
6. health-aware LB
7. exponential backoff
8. bulkhead isolation

Never assume:

```text
Registry says UP == definitely healthy
```

Correct assumption:

```text
Registry says UP == probably healthy recently
```

---

# 42. Retry Different Instance

Bad retry:

```text
Try dead instance A
Retry dead instance A
Retry dead instance A
```

Good retry:

```text
Try instance A
If timeout, mark locally bad
Retry instance B
```

Java style:

```java
for (ServiceInstance instance : healthyInstances) {
    try {
        return call(instance);
    } catch (Exception ex) {
        localFailureCache.markBad(instance.instanceId);
    }
}

throw new RuntimeException("No instance succeeded");
```

---

# 43. Local Failure Cache

```java
import java.util.concurrent.*;

class LocalFailureCache {

    private final ConcurrentHashMap<String, Long> badUntil =
            new ConcurrentHashMap<>();

    private final long coolDownMillis = 10_000;

    public void markBad(String instanceId) {
        badUntil.put(instanceId, System.currentTimeMillis() + coolDownMillis);
    }

    public boolean isAllowed(String instanceId) {
        Long until = badUntil.get(instanceId);

        if (until == null) {
            return true;
        }

        if (System.currentTimeMillis() > until) {
            badUntil.remove(instanceId);
            return true;
        }

        return false;
    }
}
```

This protects the client from stale registry entries.

---

# 44. Circuit Breaker Connection

Circuit breaker handles repeated failures.

```text
Registry says instance is UP
Client calls instance
Failures increase
Circuit breaker opens
Client stops calling temporarily
```

Important:

```text
Registry health may lag behind reality.
```

So client-side resilience is mandatory.

---

# 45. Metrics to Monitor

Registry metrics:

- total registered instances
- UP instances
- SUSPECT instances
- DOWN instances
- heartbeat success rate
- heartbeat delay
- replication lag
- eviction count
- self-preservation status
- registry response latency

Client metrics:

- stale instance failures
- retry count
- retry success rate
- circuit breaker open count
- discovery cache age
- no instance available count

Network metrics:

- packet loss
- DNS failures
- TCP timeout rate
- cross-AZ latency
- cross-region latency

---

# 46. Debugging Playbook

When clients hit dead instances:

## Step 1: Check target instance

```text
Is the process alive?
Is readiness true?
Is liveness true?
Was it terminating?
```

## Step 2: Check registry

```text
Does registry show UP or DOWN?
What is last heartbeat time?
Which registry node was queried?
```

## Step 3: Check client cache

```text
When did client refresh discovery data?
Does client still have old IP?
```

## Step 4: Check network

```text
Can client reach instance?
Can instance reach registry?
Can registry nodes reach each other?
```

## Step 5: Check replication

```text
Do all registry nodes agree?
Is one registry node stale?
What is replication lag?
```

## Step 6: Check deployment lifecycle

```text
Was readiness set false before exit?
Was preStop hook configured?
Was drain time enough?
```

---

# 47. Production Checklist

## Registry Side

- heartbeat TTL configured
- eviction delay tuned
- self-preservation behavior understood
- replication monitored
- quorum rules clear
- partition behavior documented
- stale cache max age configured

## Instance Side

- readiness probe accurate
- liveness probe safe
- graceful shutdown enabled
- deregistration on shutdown
- dependencies included in health
- startup delay handled

## Client Side

- short connection timeout
- request timeout
- retry different instance
- circuit breaker enabled
- local failure cache enabled
- discovery cache refresh tuned
- fallback behavior defined

## Platform Side

- DNS TTL understood
- kube-proxy update latency monitored
- EndpointSlice updates monitored
- cross-AZ network monitored
- packet loss monitored

---

# 48. Common Interview Questions

## Q1. What is stale registry?

A stale registry has old service instance data.

Example:

```text
Registry says instance is UP,
but instance is actually dead.
```

---

## Q2. Why does stale registry happen?

Because failure detection and propagation take time.

Sources:

- heartbeat delay
- TTL
- replication lag
- client cache refresh delay
- DNS caching
- network partition

---

## Q3. What is split brain?

Two partitions of a distributed system both believe they are authoritative.

Example:

```text
Registry-A thinks it is leader.
Registry-B also thinks it is leader.
```

---

## Q4. How does quorum prevent split brain?

Only a majority partition can make decisions.

Minority partition cannot safely elect a leader.

---

## Q5. Why does Eureka prefer availability?

Because transient network failures are common in cloud systems.

Returning stale registry data may be better than returning no data.

---

## Q6. Why do ZooKeeper and etcd use quorum?

To preserve consistency and avoid split brain.

---

## Q7. How should clients handle stale discovery data?

Use:

- timeout
- retry different instance
- circuit breaker
- local bad-instance cache
- health-aware load balancing

---

# 49. Real-World Example: Deployment Stale Endpoint

Deployment starts terminating old pod:

```text
Old Pod still in client cache
New Pod starting
```

If shutdown is not graceful:

```text
Old Pod exits
Client still calls old pod
Requests fail
```

Correct flow:

```text
Readiness false
Endpoint removed
Wait
Drain traffic
Exit
```

---

# 50. Real-World Example: Cross-AZ Partition

```text
AZ-1 cannot reach AZ-2
```

Registry nodes in each AZ may see different instance health.

Clients in AZ-1 route locally.

Clients in AZ-2 route locally.

When partition heals:

```text
Registry states reconcile.
```

This is why zone-aware routing matters.

---

# 51. Blast Radius Control

Network partition should not destroy the full system.

Goal:

```text
Failure stays local.
```

Techniques:

- zone-aware routing
- regional isolation
- bulkheads
- circuit breakers
- local fallback
- partition-aware registry behavior
- client-side stale cache max age

---

# 52. Design Rule: Discovery Is Advisory

Very important:

> Service registry is not the final truth. It is a recent hint.

A registry entry means:

```text
This instance was healthy recently.
```

Not:

```text
This instance is guaranteed healthy now.
```

Therefore every client must be defensive.

---

# 53. Connection to Previous Chapters

From 010:

```text
Heartbeat + TTL detect dead instances.
```

From 011:

```text
Dead instances are removed after expiry.
```

From 012:

```text
Self-preservation avoids mass eviction during network issues.
```

From 015:

```text
Health-aware LB avoids bad instances.
```

This chapter:

```text
Explains why stale registry and split brain still happen.
```

---

# 54. Connection to Next Chapters

Next:

## 017_Eventual_Consistency_CAP_Tradeoff.md

You will learn:

- why service discovery data becomes eventually consistent
- why CAP theorem matters
- why AP vs CP choice changes behavior

## 018_Registry_Replication_Model.md

You will learn:

- full sync vs delta sync
- peer replication
- anti-entropy repair
- version conflict handling

---

# 55. Final Mental Model

```text
Network Partition
      ↓
Registry Nodes Disagree
      ↓
Some Data Becomes Stale
      ↓
Clients May Route Incorrectly
      ↓
Retries / Circuit Breakers / Health Checks Reduce Damage
      ↓
Replication / Gossip / Quorum Heal or Prevent Split Brain
```

---

# 56. Final Cheat Sheet

## Network Partition

```text
Nodes are alive, but cannot communicate.
```

## Stale Registry

```text
Registry contains old instance data.
```

## Split Brain

```text
Two sides believe they are authoritative.
```

## AP Discovery

```text
Available, but may be stale.
```

## CP Discovery

```text
Consistent, but may become unavailable.
```

## Eureka

```text
Availability-focused.
Can return stale data during failures.
```

## ZooKeeper / etcd

```text
Quorum-focused.
Avoid split brain.
```

## Client Rule

```text
Never blindly trust registry.
Always use timeout + retry + circuit breaker.
```

## Production Rule

```text
Design for stale discovery.
Assume network partitions will happen.
```

---

# 57. One-Line Interview Answer

Network partition in service discovery can cause stale registry and split brain because registry nodes or clients see different views of service health. AP systems like Eureka may continue serving stale data for availability, while CP systems like ZooKeeper or etcd use quorum to avoid split brain. Clients must handle stale discovery using timeouts, retries, circuit breakers, local failure cache, and health-aware load balancing.
