# 017_Eventual_Consistency_CAP_Tradeoff.md

# Eventual Consistency & CAP Tradeoff

## MiniServiceDiscovery Series

Previous:
- 016_Network_Partition_Stale_Registry_SplitBrain.md

Next:
- 018_Registry_Replication_Model.md

---

# 1. Why This Chapter Matters

A common beginner assumption:

```text
Registry updated
↓
Everyone instantly sees update
```

Reality:

```text
Registry updated
↓
Replication delay
↓
Cache refresh delay
↓
Network delay
↓
Clients eventually see update
```

Distributed systems are not instantly consistent.

This chapter explains:

- Eventual Consistency
- CAP Theorem
- AP vs CP tradeoffs
- Why Eureka behaves differently from ZooKeeper
- How service discovery remains available during failures

---

# 2. Consistency Mental Model

Suppose:

```text
Payment-Service
```

has instances:

```text
A
B
C
```

Registry-A receives update:

```text
C = DOWN
```

Question:

Will every registry node know immediately?

Answer:

No.

There is always propagation delay.

---

# 3. Strong Consistency

Strong consistency means:

```text
After a write,
all future reads see that write.
```

Example:

```text
Registry-A marks C DOWN
```

Immediately:

```text
Registry-B sees DOWN
Registry-C sees DOWN
Clients see DOWN
```

Single truth.

Advantages:

- No stale reads
- No disagreement

Disadvantages:

- Slower
- More coordination

---

# 4. Eventual Consistency

Eventual consistency means:

```text
All nodes eventually converge.
```

Immediately after update:

```text
Registry-A : C DOWN
Registry-B : C UP
Registry-C : C UP
```

After replication:

```text
Registry-A : C DOWN
Registry-B : C DOWN
Registry-C : C DOWN
```

Convergence occurs later.

---

# 5. Real World Analogy

Think about WhatsApp.

Message sent.

Phone-A shows:

```text
Delivered
```

Phone-B may not yet show it.

Eventually:

```text
Both agree.
```

That is eventual consistency.

---

# 6. Why Eventual Consistency Exists

Because:

```text
Network latency
Replication latency
Node failures
Partitions
```

make instant global agreement expensive.

---

# 7. Service Discovery Example

Registry Cluster:

```text
Registry-A
Registry-B
Registry-C
```

Service dies:

```text
Payment-1
```

Registry-A detects failure.

Replication takes 3 seconds.

During those 3 seconds:

```text
A = DOWN
B = UP
C = UP
```

Temporary inconsistency.

---

# 8. Eventual Consistency Window

The period where nodes disagree.

```text
Write Time
↓
Replication
↓
Convergence
```

Window length depends on:

- Replication speed
- Network latency
- Node load
- Queue backlog

---

# 9. Why Service Discovery Accepts Eventual Consistency

Perfect consistency costs availability.

Imagine:

```text
Registry unavailable
```

during network issue.

Would you prefer:

A)

```text
Slightly stale service list
```

or

B)

```text
No service list at all
```

Many systems choose A.

---

# 10. CAP Theorem

CAP:

```text
C = Consistency
A = Availability
P = Partition Tolerance
```

During network partition:

You can choose only:

```text
CP
or
AP
```

Not both.

---

# 11. Partition Tolerance

Partition means:

```text
Nodes cannot communicate.
```

Example:

```text
Registry-A X Registry-B
```

P is mandatory.

Networks fail.

---

# 12. Consistency Definition

Consistency means:

```text
Every read sees latest write.
```

No stale reads.

No disagreement.

---

# 13. Availability Definition

Availability means:

```text
Every request gets a response.
```

Maybe stale.

But response exists.

---

# 14. CAP Triangle Mental Model

```text
       C
      / \
     /   \
    /     \
   A-------P
```

When partition happens:

Must choose:

```text
CP
or
AP
```

---

# 15. AP System

Availability + Partition Tolerance

Behavior:

```text
Keep serving requests
Even with stale data
```

Examples:

- Eureka
- DNS
- Cassandra (configurable)

---

# 16. CP System

Consistency + Partition Tolerance

Behavior:

```text
Refuse unsafe operations
Until quorum exists
```

Examples:

- ZooKeeper
- etcd
- Consul control plane

---

# 17. Eureka Tradeoff

Eureka prefers:

```text
AP
```

Reason:

Service discovery should stay available.

Even if data is slightly stale.

---

# 18. ZooKeeper Tradeoff

ZooKeeper prefers:

```text
CP
```

Reason:

Consistency is critical.

Split brain must be prevented.

---

# 19. Quorum Concept

5 nodes.

Majority:

```text
3
```

Partition:

```text
3 nodes
2 nodes
```

Only majority continues.

---

# 20. Why Majority Works

Two majorities overlap.

Example:

```text
N=5
Majority=3
```

Impossible:

```text
3 nodes
3 nodes
```

without sharing node.

Therefore:

Single truth remains.

---

# 21. Read Repair

Suppose:

```text
Registry-B stale
```

Client reads:

```text
A says DOWN
B says UP
```

Read repair updates B.

Consistency improves.

---

# 22. Anti Entropy Repair

Background synchronization.

```text
Node-A compares Node-B
```

Differences fixed.

Used periodically.

---

# 23. Gossip Protocol

Information spreads like rumor.

```text
A -> B
B -> C
C -> D
```

Eventually everyone knows.

Advantages:

- Scalable
- Decentralized

Disadvantages:

- Not immediate

---

# 24. Versioning

Every update carries:

```java
long version;
```

Example:

```text
Version 10
Version 12
```

12 wins.

---

# 25. Vector Clock Concept

Tracks update history.

Used when:

```text
Multiple writers
```

Need conflict detection.

---

# 26. Conflict Example

Registry-A:

```text
Service UP
Version 10
```

Registry-B:

```text
Service DOWN
Version 11
```

Winner:

```text
Version 11
```

---

# 27. Last Write Wins

Simple strategy.

Highest timestamp wins.

Easy.

But clock skew can be dangerous.

---

# 28. Strong Consistency Cost

To guarantee consistency:

Need:

```text
Coordination
Acknowledgements
Quorum
```

Costs:

- Latency
- Complexity
- Reduced availability

---

# 29. Eventual Consistency Benefit

Benefits:

```text
Fast
Scalable
Available
Fault tolerant
```

Tradeoff:

Temporary stale data.

---

# 30. Service Discovery Timeline

```text
T0 Service dies

T1 Registry-A notices

T2 Replication starts

T3 Registry-B updated

T4 Client refreshes cache

T5 Everyone agrees
```

Consistency achieved eventually.

---

# 31. DNS Example

DNS is eventually consistent.

Update record.

Some clients still see old IP.

Eventually:

All caches expire.

Everyone sees new IP.

---

# 32. Kubernetes Example

Pod dies.

Endpoint removed.

Some clients still:

```text
Use cached endpoint
```

Eventually:

Updated endpoint list.

---

# 33. Eureka Example

Instance removed.

Peer replication delayed.

Some clients:

```text
Still see instance
```

Eventually:

All peers converge.

---

# 34. Consul Example

Leader updates state.

Followers replicate.

Eventually:

Cluster agrees.

---

# 35. Metrics To Monitor

Replication Lag

```text
CurrentVersion
ReplicaVersion
```

Difference should stay small.

---

# 36. Consistency Lag

Measure:

```text
Write Time
Read Visibility Time
```

Lag:

```text
Visibility - Write
```

---

# 37. Production Problems

Too much lag:

- Stale routing
- Wrong endpoints
- Increased retries

---

# 38. CAP Interview Question

Q:

Can system be CA?

Answer:

Only if no partitions occur.

Real distributed systems:

Need P.

---

# 39. CAP Interview Question

Q:

Why not CP everywhere?

Answer:

Availability matters.

Users prefer degraded service over outage.

---

# 40. CAP Interview Question

Q:

Why not AP everywhere?

Answer:

Some systems require strict consistency.

Examples:

- Leader election
- Distributed lock
- Metadata store

---

# 41. Java Example: Versioned Record

```java
class RegistryRecord {

    String service;

    String status;

    long version;
}
```

---

# 42. Java Example: Conflict Resolution

```java
RegistryRecord choose(
        RegistryRecord a,
        RegistryRecord b) {

    return a.version > b.version
            ? a
            : b;
}
```

---

# 43. Java Example: Gossip Update

```java
void gossip(Node peer) {

    peer.merge(localState);
}
```

---

# 44. AP vs CP Summary

AP:

```text
Always respond
May be stale
```

CP:

```text
Always correct
May refuse request
```

---

# 45. Service Discovery Design Rule

Discovery systems often favor:

```text
Availability
```

because:

```text
Stale endpoint
>
No endpoint
```

for short periods.

---

# 46. Debugging Playbook

Check:

- Replication lag
- Cache age
- Heartbeats
- Partition events
- Leader election logs

---

# 47. Production Checklist

✓ Replication monitoring

✓ Cache refresh tuning

✓ Version tracking

✓ Conflict resolution

✓ Gossip monitoring

✓ Quorum monitoring

✓ Partition alerts

---

# 48. Mental Model

```text
Write
 ↓
Replication
 ↓
Temporary disagreement
 ↓
Convergence
```

Eventual consistency means:

```text
Not consistent now
But consistent later
```

---

# 49. Final Cheat Sheet

Strong Consistency:

```text
All reads see latest write
```

Eventual Consistency:

```text
Nodes eventually agree
```

AP:

```text
Availability first
```

CP:

```text
Consistency first
```

Eureka:

```text
AP
```

ZooKeeper:

```text
CP
```

Rule:

```text
Every distributed system
must choose tradeoffs.
```
