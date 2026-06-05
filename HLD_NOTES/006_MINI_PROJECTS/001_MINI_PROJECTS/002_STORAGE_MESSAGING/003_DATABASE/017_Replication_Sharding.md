# 017_Replication_Sharding.md

# MiniDatabase — 017 Replication Sharding

## 0. Why This File Exists

A single database server eventually becomes insufficient.

Problems:

```text
too many users
too many reads
too many writes
too much storage
too much traffic
high availability requirements
global users
```

Real production systems solve this using:

```text
Replication
Sharding
Partitioning
Distributed databases
```

This file teaches:

```text
leader-follower replication
read replicas
sync vs async replication
replication lag
failover
horizontal scaling
sharding
partitioning
consistent hashing
hot shard problem
rebalancing
distributed queries
```

Only ASCII diagrams are used.

---

# 1. Biggest Mental Model

Single database:

```text
one machine
one disk
one CPU
one RAM
```

Eventually becomes bottleneck.

Scaling approaches:

```text
Replication
    =
copy database to multiple servers

Sharding
    =
split database across servers
```

---

# 2. Replication vs Sharding

Replication:

```text
same data copied to many nodes
```

Goal:

```text
high availability
read scaling
backup
failover
```

Sharding:

```text
different data stored on different nodes
```

Goal:

```text
write scaling
storage scaling
horizontal scaling
```

---

# 3. Replication ASCII

```text
           Primary DB
                │
      ┌─────────┼─────────┐
      ↓         ↓         ↓
 Replica-1  Replica-2  Replica-3
```

All replicas contain same data.

---

# 4. Sharding ASCII

```text
Users 1-1M   → Shard-1
Users 1M-2M  → Shard-2
Users 2M-3M  → Shard-3
```

Each shard stores only part of total data.

---

# 5. Why Replication Exists

Replication solves:

```text
database crashes
high read traffic
disaster recovery
backup
regional availability
```

---

# 6. Why Sharding Exists

Sharding solves:

```text
single machine write bottleneck
disk size limit
memory limit
CPU limit
massive dataset scaling
```

---

# 7. Leader Follower Replication

Most common replication model.

One node:

```text
Leader / Primary
```

Other nodes:

```text
Followers / Replicas
```

Leader handles writes.

Followers replicate data.

---

# 8. Leader Follower ASCII

```text
          Clients
              │
      ┌───────┴───────┐
      ↓               ↓
    Writes          Reads
      ↓               ↓
   Primary DB    Read Replicas
```

---

# 9. Write Flow In Replication

```text
Client writes to primary
       ↓
Primary updates data
       ↓
Primary generates WAL/binlog
       ↓
Replicas replay log
       ↓
Replicas become consistent
```

---

# 10. Replication WAL Flow

```text
Primary:
UPDATE row
      ↓
Write WAL/binlog
      ↓
Send log to replicas
      ↓
Replicas replay changes
```

---

# 11. PostgreSQL Replication Mental Model

entity["software","PostgreSQL","database"] replication uses:

```text
WAL shipping
streaming replication
logical replication
```

Replica continuously reads WAL stream.

---

# 12. MySQL Replication Mental Model

entity["software","MySQL","database"] replication uses:

```text
binlog
replica IO thread
replica SQL thread
```

Replicas replay binlog events.

---

# 13. Read Replicas

Replicas often serve:

```text
read queries
analytics
search
reporting
```

This reduces load on primary.

---

# 14. Read Replica ASCII

```text
              App
               │
       ┌───────┴────────┐
       ↓                ↓
   Write Query      Read Query
       ↓                ↓
    Primary         Replica
```

---

# 15. Read Scaling

Suppose:

```text
100k reads/sec
5k writes/sec
```

Single primary struggles with reads.

Add replicas:

```text
Primary
Replica-1
Replica-2
Replica-3
```

Read traffic distributed.

---

# 16. Replication Lag

Replicas are often slightly behind primary.

Reason:

```text
replication is asynchronous
```

This delay is:

```text
replication lag
```

---

# 17. Replication Lag ASCII

```text
Primary updated balance=500
        ↓
Replica still has balance=400
        ↓
Replica catches up later
```

Temporary inconsistency.

---

# 18. Eventual Consistency

Many distributed systems provide:

```text
eventual consistency
```

Meaning:

```text
all replicas become consistent eventually
```

Not necessarily immediately.

---

# 19. Strong Consistency

Strong consistency means:

```text
after commit
all readers immediately see latest value
```

More expensive.

---

# 20. Sync Replication

Synchronous replication:

```text
primary waits for replicas before commit success
```

Safer.

But slower.

---

# 21. Sync Replication ASCII

```text
Primary receives write
      ↓
Write locally
      ↓
Wait for replica ACK
      ↓
Commit success
```

---

# 22. Async Replication

Asynchronous replication:

```text
primary commits immediately
replicas catch up later
```

Faster.

But possible lag/data loss during crash.

---

# 23. Async Replication ASCII

```text
Primary commits
      ↓
Client gets success
      ↓
Replica updated later
```

---

# 24. Failover

If primary crashes:

```text
promote replica to new primary
```

This is failover.

---

# 25. Failover ASCII

Before crash:

```text
Primary
Replica-1
Replica-2
```

After crash:

```text
Replica-1 promoted to primary
Replica-2 follows new primary
```

---

# 26. Split Brain Problem

Dangerous scenario:

```text
two nodes both think they are primary
```

Leads to conflicting writes.

---

# 27. Split Brain ASCII

```text
Node-A thinks primary
Node-B thinks primary

Both accept writes
```

Data divergence occurs.

---

# 28. Consensus Systems

Distributed systems use:

```text
Raft
Paxos
ZooKeeper
etcd
```

to avoid split brain.

---

# 29. CAP Theorem

Distributed systems tradeoff:

```text
Consistency
Availability
Partition Tolerance
```

Cannot fully maximize all three simultaneously.

---

# 30. CAP ASCII

```text
       Consistency
          /\
         /  \
        /    \
       /______\
Availability   Partition Tolerance
```

---

# 31. Sharding

Sharding means:

```text
split dataset across multiple servers
```

Each server stores subset of data.

---

# 32. Why Sharding Needed

Single machine eventually limited by:

```text
CPU
RAM
disk
network
IOPS
```

Sharding scales horizontally.

---

# 33. Horizontal Scaling

Vertical scaling:

```text
bigger machine
```

Horizontal scaling:

```text
more machines
```

Sharding enables horizontal scaling.

---

# 34. Sharding ASCII

```text
Shard-1 → Users A-F
Shard-2 → Users G-M
Shard-3 → Users N-Z
```

---

# 35. Range-Based Sharding

Partition by ranges.

Example:

```text
User IDs 1-1M → Shard-1
User IDs 1M-2M → Shard-2
```

Simple.

But may create hot ranges.

---

# 36. Hash-Based Sharding

Hash key determines shard.

Example:

```text
hash(userId) % N
```

Better distribution.

---

# 37. Hash Sharding ASCII

```text
hash(101) % 3 → Shard-2
hash(555) % 3 → Shard-1
hash(777) % 3 → Shard-0
```

---

# 38. Hot Shard Problem

One shard receives too much traffic.

Example:

```text
celebrity user
viral post
popular tenant
```

One shard overloaded.

---

# 39. Hot Shard ASCII

```text
Shard-1 → 5% traffic
Shard-2 → 90% traffic
Shard-3 → 5% traffic
```

System bottleneck becomes Shard-2.

---

# 40. Rebalancing

Rebalancing means:

```text
move data between shards
```

to improve distribution.

---

# 41. Rebalancing ASCII

Before:

```text
Shard-1 huge
Shard-2 small
Shard-3 small
```

Move some data:

```text
Shard-1 medium
Shard-2 medium
Shard-3 medium
```

---

# 42. Consistent Hashing

Consistent hashing reduces data movement when adding/removing shards.

Very important distributed systems concept.

Used in:

- entity["software","Cassandra","database"]
- entity["software","Redis","database"] cluster
- CDN systems
- distributed caches

---

# 43. Consistent Hash Ring

```text
          [Shard-1]
        /           \
   key=10         key=50
      |             |
[Shard-3]         [Shard-2]
```

Keys mapped around ring.

---

# 44. Why Consistent Hashing Useful

Without consistent hashing:

```text
adding 1 shard remaps almost all keys
```

With consistent hashing:

```text
only small subset moves
```

---

# 45. Cross-Shard Query Problem

Querying many shards is difficult.

Example:

```sql
SELECT COUNT(*)
FROM all_users;
```

Need query across all shards.

---

# 46. Cross-Shard Query ASCII

```text
Coordinator
    ↓
Query Shard-1
Query Shard-2
Query Shard-3
    ↓
Merge results
```

---

# 47. Distributed Transactions

Transactions across shards are difficult.

Need protocols like:

```text
2PC
Saga
distributed commit
```

Complex and expensive.

---

# 48. Two Phase Commit 2PC

Coordinator asks all nodes:

```text
Can you commit?
```

If all yes:

```text
commit everywhere
```

Else:

```text
rollback everywhere
```

---

# 49. 2PC ASCII

```text
Coordinator
      ↓
PREPARE Shard-1
PREPARE Shard-2
      ↓
All OK
      ↓
COMMIT everywhere
```

---

# 50. Distributed System Tradeoffs

Replication gives:

```text
availability
read scaling
```

Sharding gives:

```text
write scaling
storage scaling
```

But distributed systems become more complex.

---

# 51. Cassandra Mental Model

entity["software","Cassandra","database"] uses:

```text
partitioning
replication
consistent hashing
eventual consistency
LSM trees
```

Designed for massive scale.

---

# 52. MongoDB Mental Model

entity["software","MongoDB","database"] uses:

```text
replica sets
sharding
primary-secondary replication
```

---

# 53. Redis Cluster Mental Model

entity["software","Redis","database"] cluster uses:

```text
hash slots
partitioning
replication
```

Keys distributed across slots.

---

# 54. Replication vs Backup

Replication:

```text
real-time copies
```

Backup:

```text
point-in-time restore
```

Replication is not backup.

Mistaken deletes may replicate too.

---

# 55. Backup Problem ASCII

```text
DELETE users table
      ↓
replicated to all replicas
```

All replicas lose data too.

Need backups separately.

---

# 56. Read-After-Write Problem

User writes data:

```text
Primary updated
```

Then reads from replica:

```text
Replica still stale
```

User sees old data.

---

# 57. Read-After-Write ASCII

```text
Write profile update
      ↓
Primary updated

Immediately read from replica
      ↓
Old profile returned
```

Common distributed systems issue.

---

# 58. Geo Replication

Global systems replicate across regions.

Example:

```text
US-East
Europe
Asia
```

Improves:

```text
availability
latency
disaster recovery
```

---

# 59. Geo Replication ASCII

```text
US Primary
     ↓
EU Replica
     ↓
Asia Replica
```

---

# 60. Java Shard Router Example

```java
public class ShardRouter {

    private final int shardCount;

    public ShardRouter(int shardCount) {
        this.shardCount = shardCount;
    }

    public int getShard(long userId) {

        return (int)(userId % shardCount);
    }
}
```

---

# 61. Java Replication Mental Model

```java
public void write(Data data) {

    primary.write(data);

    for (Replica replica : replicas) {
        replica.replicate(data);
    }
}
```

Simplified conceptual model.

---

# 62. Spring Read Replica Example

```java
@Transactional(readOnly = true)
public User getUser(Long id) {

    return replicaRepository.findById(id);
}
```

Reads from replica.

---

# 63. Full Replication Dry Run

Client updates balance:

```text
UPDATE balance=500
```

Execution:

```text
Primary updates row
      ↓
Primary writes WAL/binlog
      ↓
Replica receives log
      ↓
Replica replays change
      ↓
Replica now balance=500
```

---

# 64. Full Sharding Dry Run

Suppose:

```text
4 shards
```

Query:

```text
userId=12345
```

Shard routing:

```text
12345 % 4 = 1
```

Route to:

```text
Shard-1
```

---

# 65. Production Notes

Use replication for:

```text
high availability
read scaling
regional redundancy
```

Use sharding for:

```text
massive write scaling
massive datasets
multi-tenant scaling
```

Avoid distributed transactions when possible.

Prefer:

```text
single-shard transactions
event-driven workflows
Sagas
```

---

# 66. Common Problems

```text
replication lag
hot shards
split brain
cross-shard joins
distributed transaction failures
rebalance cost
network partitions
```

---

# 67. Interview Explanation — Replication

If interviewer asks:

```text
What is replication?
```

Strong answer:

```text
Replication copies database changes from a primary node to replicas.
It improves availability, failover capability, and read scalability.
Replication can be synchronous or asynchronous depending on consistency needs.
```

---

# 68. Interview Explanation — Sharding

If interviewer asks:

```text
What is sharding?
```

Strong answer:

```text
Sharding horizontally partitions data across multiple database servers.
Each shard stores only part of the total dataset, enabling horizontal
scaling for storage and write throughput.
```

---

# 69. Interview Explanation — Consistent Hashing

If interviewer asks:

```text
Why use consistent hashing?
```

Strong answer:

```text
Consistent hashing minimizes key redistribution when shards are added or removed.
Instead of remapping almost all keys, only a smaller subset of keys move.
```

---

# 70. Common Mistakes

## Mistake 1

```text
Thinking replication increases write scalability
```

Replication mainly helps reads and availability.

---

## Mistake 2

```text
Ignoring replication lag
```

Leads to stale reads.

---

## Mistake 3

```text
Thinking replication is backup
```

Replication copies mistakes too.

---

## Mistake 4

```text
Poor shard key selection
```

Creates hot shards.

---

## Mistake 5

```text
Using distributed transactions everywhere
```

Very expensive and complex.

---

# 71. Final Mental Model

```text
Replication
    =
same data copied to many nodes

Sharding
    =
different data distributed across nodes
```

Flow:

```text
Primary handles writes
      ↓
Replicas copy data
      ↓
Shard router distributes traffic
      ↓
Distributed system scales horizontally
```

---

# 72. What To Remember

```text
Replication improves availability and read scaling.

Sharding improves write scaling and storage scaling.

Leader-follower is common replication model.

Replication lag causes stale reads.

Consistent hashing reduces key movement.

Hot shards become bottlenecks.

Distributed transactions are difficult.

Replication is not backup.
```

---

# 73. Next File

```text
018_Postgres_MySQL_Cassandra_Internals.md
```

Next you learn:

```text
PostgreSQL internals
MySQL InnoDB internals
Cassandra internals
heap storage
clustered indexes
SSTables
compaction
real-world database engine architecture
```
