
# 018_Registry_Replication_Model.md

# Registry Replication Model

## Learning Objectives

After completing this chapter you should understand:

- Why service registries must be replicated
- How Eureka peer replication works
- How Consul and etcd replicate data
- Active-Active vs Active-Passive registries
- Delta replication vs full synchronization
- Eventual consistency in service discovery
- Split brain problems
- Quorum and consensus concepts
- Production-grade registry replication

---

# Why Registry Replication Exists

A service registry is the central directory of a microservice platform.

Without a registry:

```text
Order Service cannot find Payment Service
Gateway cannot find User Service
Load Balancer cannot find instances
```

If the registry becomes unavailable, service discovery fails.

This creates a Single Point Of Failure.

```text
                Registry

                    X

      Entire Discovery Layer Down
```

Therefore production systems replicate registry information across multiple nodes.

---

# Mental Model

Think about Google Contacts.

Your contacts exist on:

- Mobile phone
- Tablet
- Laptop

When you add a contact:

```text
Add Contact
      ↓
Google Cloud
      ↓
All Devices Updated
```

Registry replication works similarly.

Service registration performed on one node eventually appears on all registry nodes.

---

# High Level Architecture

```text
            Registry-A
           /          \
          /            \
         /              \
Registry-B ---------- Registry-C
```

Each node contains:

```text
Services
Instances
Metadata
Health Information
Lease Information
```

All nodes exchange updates.

---

# Registry Data Example

```java
class ServiceInstance {

    String serviceName;

    String instanceId;

    String host;

    int port;

    String status;

    long lastHeartbeat;
}
```

Example:

```text
Order Service

Instance-1
10.0.0.1:8080

Instance-2
10.0.0.2:8080
```

This data must be present on all registry servers.

---

# What Gets Replicated

## Registration

```text
REGISTER Order-Service
```

## Heartbeat

```text
HEARTBEAT Instance-1
```

## Metadata Updates

```text
Status UP → DOWN
```

## Deregistration

```text
DELETE Instance-1
```

---

# Registration Replication Flow

Step 1

```text
Order-Service
      |
      v
 Registry-A
```

Step 2

Registry-A stores locally.

```text
Registry-A

Order-Service
```

Step 3

Replication starts.

```text
Registry-A
   |
   +----> Registry-B
   |
   +----> Registry-C
```

Step 4

Cluster converges.

```text
A = Order
B = Order
C = Order
```

---

# Heartbeat Replication

Service sends:

```text
Heartbeat
```

Registry updates:

```java
lastHeartbeat = currentTimeMillis();
```

Replication occurs.

```text
Registry-A
     |
     +--> Registry-B
     |
     +--> Registry-C
```

All heartbeat timestamps become consistent.

---

# Deregistration Replication

Instance shutdown.

```text
DELETE Instance
```

Registry-A removes instance.

Replication event:

```text
DELETE
```

sent to peers.

Final state:

```text
A = Removed
B = Removed
C = Removed
```

---

# Peer To Peer Replication

Used by Eureka.

Architecture:

```text
A <----> B

A <----> C

B <----> C
```

Every node communicates with every other node.

Advantages:

- No leader
- Simple
- Highly available

Disadvantages:

- More network traffic
- Eventual consistency

---

# Leader Follower Replication

Used by:

- etcd
- Consul
- ZooKeeper style systems

Architecture:

```text
          Leader
         /  |  \
        /   |   \

      F1   F2   F3
```

Writes go to leader.

Leader replicates to followers.

Advantages:

- Strong consistency
- Easier conflict handling

Disadvantages:

- Leader election complexity

---

# Replication Event Pipeline

Production systems rarely replicate immediately.

Instead:

```text
Write Event
      ↓
Replication Queue
      ↓
Replication Worker
      ↓
Peer Registry
```

This improves throughput.

---

# Replication Queue

Example:

```java
class ReplicationEvent {

    EventType type;

    ServiceInstance instance;

    long timestamp;
}
```

Queue:

```java
BlockingQueue<ReplicationEvent>
```

Stores:

```text
REGISTER
HEARTBEAT
DELETE
STATUS_CHANGE
```

---

# Replication Worker

```java
while(true){

    ReplicationEvent event =
        queue.take();

    replicate(event);
}
```

Background workers continuously process replication events.

---

# Delta Replication

Do not replicate the entire registry.

Replicate only changes.

Example:

```text
Status Change

UP → DOWN
```

Only this change is transmitted.

Benefits:

- Lower bandwidth
- Faster replication
- Better scalability

---

# Full Synchronization

Sometimes nodes fall behind.

Example:

```text
Registry-C disconnected
```

When it reconnects:

```text
Registry-A
      |
      +----> Full Sync
      |
      +----> Registry-C
```

Entire registry copied.

---

# Eventual Consistency

Most service discovery systems are eventually consistent.

Example:

Time T0

```text
A = Order
B = Empty
C = Empty
```

Time T1

```text
A = Order
B = Order
C = Order
```

Temporary inconsistency is accepted.

---

# Strong Consistency

Strong consistency means:

```text
Write succeeds

Immediately visible everywhere
```

Achieved using:

```text
Quorum
Consensus
Leader Election
```

Examples:

- etcd
- Consul
- ZooKeeper

---

# Network Partition Scenario

Cluster:

```text
A ----- B

C isolated
```

New registration:

```text
Order-Service
```

Visible on:

```text
A
B
```

Not visible on:

```text
C
```

C becomes stale.

---

# Stale Registry Problem

Registry-C still believes:

```text
Order-Service missing
```

or

```text
Dead instance alive
```

Requests may fail.

Full synchronization fixes the issue.

---

# Split Brain

Partition:

```text
A + B

C
```

If both sides accept writes:

```text
A says Order UP

C says Order DOWN
```

Conflicting truths exist.

This is Split Brain.

---

# Quorum Based Replication

For N nodes:

```text
Quorum = N/2 + 1
```

Example:

```text
5 nodes

Need 3
```

Write succeeds only after majority acknowledgment.

Benefits:

- Prevents split brain
- Improves consistency

---

# Eureka Replication Internals

Eureka uses:

```text
Peer Aware Registry
```

Flow:

```text
Register Instance
      ↓
Store Locally
      ↓
Replicate To Peers
```

Operations replicated:

```text
Register
Heartbeat
Cancel
Status Update
```

---

# Consul Replication

Consul uses:

```text
Raft
```

Components:

```text
Leader
Followers
```

Majority vote required.

---

# etcd Replication

Kubernetes stores service discovery information in etcd.

etcd uses:

```text
Raft Consensus
```

Properties:

- Strong consistency
- Leader election
- Quorum writes

---

# ZooKeeper Replication

ZooKeeper uses:

```text
Leader
Follower
Observer
```

Writes:

```text
Leader
   ↓
Majority Ack
   ↓
Commit
```

---

# Failure Scenario 1

Registry-A crashes.

```text
A Down
```

Remaining:

```text
B
C
```

Discovery continues.

---

# Failure Scenario 2

Replication delay.

```text
Heartbeat delayed
```

Temporary stale state appears.

TTL mechanisms help avoid problems.

---

# Failure Scenario 3

Network partition.

```text
A+B

C
```

Potential stale entries.

Requires synchronization.

---

# Production Optimizations

## Compression

```text
GZIP
Snappy
```

## Batching

```text
1000 updates

→

1 batch
```

## Retry

```text
Exponential Backoff
```

## Checksum

```text
CRC
Hash
```

Integrity validation.

---

# Java Design

```java
class RegistryNode {}

class PeerRegistry {}

class ReplicationQueue {}

class ReplicationWorker {}

class FullSyncManager {}

class ServiceInstance {}
```

---

# CP / FAANG Problem Forms

## Problem 1

Propagate update to all registries.

Solution:

```text
BFS
Graph Traversal
```

---

## Problem 2

Find minimum nodes needed for survival.

Solution:

```text
Connectivity
Graph Theory
```

---

## Problem 3

Detect stale instances.

Solution:

```text
Heap
Timestamp
TTL
```

---

## Problem 4

Determine cluster leader.

Solution:

```text
Voting
Majority Selection
```

---

# Strong Interview Answer

Registry replication eliminates the registry as a single point of failure. Service metadata is replicated across multiple registry nodes so that service discovery continues even when individual registry servers fail. Systems such as Eureka use peer-to-peer replication and eventual consistency, while systems such as etcd, Consul and ZooKeeper use leader-based replication with quorum consensus to provide stronger consistency guarantees.

---

# Cheat Sheet

```text
Eureka
→ Peer Replication

Consul
→ Raft

etcd
→ Raft

ZooKeeper
→ Leader/Follower

Replication Types
→ Full Sync
→ Delta Sync

Consistency
→ Eventual
→ Strong

Failure Protection
→ Replication
→ Quorum
→ Consensus
```
