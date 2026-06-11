# 017_Eventual_Consistency_CAP_Tradeoff.md

# Eventual Consistency & CAP Tradeoff

## Complete Production Grade Chapter

This chapter explains:

- Strong Consistency
- Eventual Consistency
- CAP Theorem
- AP vs CP
- Eureka vs ZooKeeper
- Quorum
- Read Repair
- Anti Entropy
- Gossip Protocol
- Vector Clocks
- Conflict Resolution
- Dynamo Model
- Cassandra Consistency Levels
- Service Discovery Tradeoffs
- Strong Interview Answers

---

# 1. The Fundamental Problem

Distributed systems run on multiple machines.

Example:

```text
Registry-A
Registry-B
Registry-C
```

Question:

When data changes:

```text
Payment-1 = DOWN
```

How fast should everyone know?

Immediately?

Eventually?

This creates the consistency problem.

---

# 2. Single Machine Consistency

Single machine:

```text
Write
↓
Read
```

Always sees latest value.

Simple.

No network.

No replication.

No disagreement.

---

# 3. Distributed Consistency Problem

Three registry nodes:

```text
A
B
C
```

Update arrives:

```text
Payment-1 DOWN
```

A knows immediately.

B and C learn later.

Temporary disagreement appears.

---

# 4. What Is Strong Consistency

Strong consistency guarantees:

```text
After write completes,
every future read sees latest value.
```

Example:

```text
A -> DOWN
```

Immediately:

```text
B -> DOWN
C -> DOWN
Clients -> DOWN
```

Single truth.

---

# 5. Advantages Of Strong Consistency

Benefits:

- No stale reads
- No disagreement
- Easier reasoning
- Easier debugging

---

# 6. Disadvantages Of Strong Consistency

Requires:

- Coordination
- Quorum
- Consensus

Costs:

- Latency
- Reduced availability
- Complexity

---

# 7. What Is Eventual Consistency

Definition:

```text
Replicas may temporarily disagree,
but eventually converge.
```

Example:

```text
T0

A = DOWN
B = UP
C = UP
```

Later:

```text
A = DOWN
B = DOWN
C = DOWN
```

All replicas agree.

---

# 8. Real World Analogy

WhatsApp message.

You send message.

Phone-A shows delivered.

Phone-B still syncing.

Eventually:

Both agree.

That is eventual consistency.

---

# 9. Why Eventual Consistency Exists

Networks are slow.

Machines fail.

Partitions happen.

Replication takes time.

Perfect coordination is expensive.

---

# 10. Eventual Consistency Window

Period where replicas disagree.

```text
Write
↓
Replication
↓
Convergence
```

This period is called:

Consistency Window.

---

# 11. Service Discovery Example

Registry Cluster:

```text
A
B
C
```

Payment-1 dies.

A detects immediately.

B learns 2 seconds later.

C learns 4 seconds later.

During those 4 seconds:

Registry answers differ.

---

# 12. Why Service Discovery Uses Eventual Consistency

Question:

Would you prefer:

A)

```text
Slightly stale service list
```

or

B)

```text
No service list
```

Most discovery systems choose A.

Availability matters.

---

# 13. CAP Theorem

CAP:

```text
C = Consistency
A = Availability
P = Partition Tolerance
```

Introduced by:

Eric Brewer.

---

# 14. What Is Partition

Partition:

```text
Nodes cannot communicate.
```

Example:

```text
Registry-A X Registry-B
```

Network broken.

---

# 15. Why Partition Tolerance Is Mandatory

Networks fail.

Switches fail.

Routers fail.

Regions fail.

Therefore:

```text
P is mandatory.
```

---

# 16. CAP Mental Model

When partition occurs:

Choose:

```text
CP
or
AP
```

Cannot fully achieve both.

---

# 17. AP Systems

Availability + Partition Tolerance

Behavior:

```text
Always answer requests
```

Even if stale.

Examples:

- Eureka
- DNS
- Cassandra (tunable)

---

# 18. CP Systems

Consistency + Partition Tolerance

Behavior:

```text
Refuse unsafe requests
```

Examples:

- ZooKeeper
- etcd
- Consul Leader

---

# 19. Eureka AP Tradeoff

Eureka prefers:

```text
Availability
```

Reason:

Temporary stale registry
is better than complete outage.

---

# 20. ZooKeeper CP Tradeoff

ZooKeeper prefers:

```text
Consistency
```

Reason:

Split brain must be avoided.

---

# 21. Quorum Concept

N=5

Majority:

```text
3
```

Need 3 nodes to agree.

---

# 22. Why Majority Works

Any two majorities overlap.

Example:

```text
5 nodes
majority=3
```

Impossible to have:

Two independent truths.

---

# 23. Read Your Writes

Guarantee:

```text
Client writes
↓
Client reads
↓
Sees own write
```

Common consistency model.

---

# 24. Monotonic Reads

Guarantee:

Reads never go backwards.

Example:

```text
Version 10
Version 11
```

Never:

```text
Version 10
Version 11
Version 9
```

---

# 25. Monotonic Writes

Writes preserve order.

Example:

```text
Update1
Update2
```

Never applied as:

```text
Update2
Update1
```

---

# 26. Session Consistency

Within session:

Client sees consistent view.

Across sessions:

May differ.

---

# 27. Eventual Consistency Spectrum

Strong
↓
Session
↓
Monotonic
↓
Eventual

Increasing availability.

---

# 28. Read Repair

Replica mismatch:

```text
A = DOWN
B = UP
```

Read detects mismatch.

Repair happens.

Consistency improves.

---

# 29. Anti Entropy

Background synchronization.

Nodes compare states.

Differences repaired.

---

# 30. Gossip Protocol

Rumor style propagation.

```text
A -> B
B -> C
C -> D
```

Eventually everyone knows.

---

# 31. Gossip Advantages

Benefits:

- Scalable
- Decentralized
- Fault tolerant

---

# 32. Gossip Disadvantages

Drawbacks:

- Delayed convergence
- Temporary stale state

---

# 33. Versioning

Every update gets:

```java
long version;
```

Higher version wins.

---

# 34. Conflict Example

A:

```text
Version 10
```

B:

```text
Version 11
```

Winner:

```text
Version 11
```

---

# 35. Last Write Wins

Highest timestamp wins.

Simple.

But clock skew dangerous.

---

# 36. Vector Clocks

Track update history.

Useful:

```text
Multiple writers
```

Detect concurrent updates.

---

# 37. Dynamo Mental Model

Amazon Dynamo introduced:

- Eventual consistency
- Vector clocks
- Gossip
- Quorum

Foundation for many NoSQL systems.

---

# 38. N R W Formula

Dynamo:

```text
N = replicas
R = read quorum
W = write quorum
```

Consistency rule:

```text
R + W > N
```

---

# 39. Example

```text
N=3
R=2
W=2
```

2 + 2 > 3

Stronger consistency.

---

# 40. Cassandra Consistency Levels

Examples:

```text
ONE
QUORUM
ALL
LOCAL_QUORUM
```

Tradeoff:

Consistency vs latency.

---

# 41. ONE

Fastest.

May be stale.

---

# 42. QUORUM

Balanced.

Common production choice.

---

# 43. ALL

Strongest.

Slowest.

---

# 44. Service Discovery Timeline

```text
T0 Instance dies

T1 Registry notices

T2 Replication

T3 Cache refresh

T4 Convergence
```

Eventually consistent.

---

# 45. DNS Example

DNS update.

Some clients:

Old IP.

Some clients:

New IP.

Eventually all agree.

---

# 46. Kubernetes Example

Pod removed.

Endpoints update later.

Temporary stale routing.

---

# 47. Eureka Example

Peer replication delay.

Temporary stale registry.

Eventually converges.

---

# 48. Production Metrics

Track:

- Replication Lag
- Cache Age
- Heartbeat Delay
- Consistency Lag
- Quorum Health

---

# 49. Debugging Playbook

Check:

1. Heartbeats
2. Replication Lag
3. Cache Age
4. Network Partition
5. Gossip Delay

---

# 50. Strong Interview Answers

## What is Eventual Consistency?

Replicas may temporarily disagree but eventually converge to same value.

---

## What is CAP?

During partition, system chooses consistency or availability.

---

## Why Eureka AP?

Availability more important than perfect freshness.

---

## Why ZooKeeper CP?

Consistency more important than availability.

---

## Why P mandatory?

Networks fail.

---

## AP vs CP?

AP:

May be stale.

CP:

May be unavailable.

---

# 51. FAANG Style Answer

Question:

Why use eventual consistency?

Answer:

Eventual consistency allows systems to remain highly available and scalable by avoiding synchronous coordination across all replicas. Temporary inconsistency is accepted, and replicas converge later through replication, gossip, and repair mechanisms.

---

# 52. One-Line Interview Answer

Eventual consistency allows replicas to temporarily disagree after updates but guarantees convergence over time, trading strict consistency for higher availability and scalability.
