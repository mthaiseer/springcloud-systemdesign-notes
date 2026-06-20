# 018_Sharding_Replication_Search.md

# MiniSearchEngine — 018 Sharding & Replication Search

## 0. Why This File Exists

A single search server cannot handle:

```text
billions of documents
millions of queries
high availability
global traffic
```

Modern search systems solve this using:

```text
sharding
replication
distributed search
```

This architecture powers:

```text
Elasticsearch
OpenSearch
SolrCloud
Google Search
Amazon Search
YouTube Search
Netflix Search
```

This file teaches:

```text
search sharding
search replication
distributed indexing
scatter-gather search
query coordination
distributed ranking
primary/replica shards
fault tolerance
high availability
Elasticsearch cluster architecture
Lucene shard internals
Java mini simulation
production tradeoffs
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
Distributed search splits indexes across shards and replicates them for scalability and fault tolerance.
```

---

# 2. Biggest Mental Model

Instead of:

```text
1 huge search server
```

we use:

```text
many smaller search nodes
```

working together.

---

# 3. Distributed Search ASCII

```text
Huge Index
     ↓
Split Into Shards
     ↓
Shard1
Shard2
Shard3
```

---

# 4. Why Single Server Fails

Single search server limitations:

```text
RAM limits
CPU limits
disk limits
network limits
failure risk
```

Eventually impossible to scale vertically.

---

# 5. Horizontal Scaling

Distributed search uses:

```text
horizontal scaling
```

Meaning:

```text
add more machines
```

instead of:

```text
one giant machine
```

---

# 6. Sharding

Core idea:

```text
split index into smaller pieces
```

Each piece called:

```text
Shard
```

---

# 7. Shard Mental Model

Shard =

```text
independent mini search index
```

In Elasticsearch:

```text
each shard = Lucene index
```

---

# 8. Sharding ASCII

```text
Global Index
 ├── Shard1
 ├── Shard2
 ├── Shard3
 └── Shard4
```

---

# 9. Why Sharding Important

Sharding enables:

```text
parallel indexing
parallel search
scalability
distributed storage
```

---

# 10. Example — Billion Documents

Suppose:

```text
1 billion documents
```

Split into:

```text
10 shards
```

Each shard stores:

```text
100 million documents
```

Much easier to manage.

---

# 11. Document Routing

When indexing:

```text
which shard stores document?
```

Need routing logic.

---

# 12. Hash-Based Routing

Common approach:

```text
hash(documentID) % shardCount
```

Example:

```text
docId=15
shards=4

15 % 4 = 3
```

Document goes to:

```text
Shard3
```

---

# 13. Routing ASCII

```text
Document
    ↓
Hash Function
    ↓
Target Shard
```

---

# 14. Search Query Flow

Search query:

```text
redis caching
```

must search ALL shards.

---

# 15. Scatter-Gather Model

Distributed search uses:

```text
scatter-gather
```

Meaning:

```text
scatter query to shards
gather shard results
merge rankings
```

---

# 16. Scatter-Gather ASCII

```text
Query
   ↓
Coordinator Node
   ↓
Shard1 Search
Shard2 Search
Shard3 Search
   ↓
Merge Results
```

---

# 17. Coordinator Node

Coordinator responsible for:

```text
sending queries
collecting shard responses
merging rankings
returning final top-k
```

---

# 18. Parallel Search

Shards search simultaneously.

Benefits:

```text
lower latency
parallel CPU usage
faster retrieval
```

---

# 19. Parallel Search ASCII

```text
Shard1 → Search
Shard2 → Search
Shard3 → Search
         ↓
Parallel Execution
```

---

# 20. Distributed Ranking Problem

Each shard computes:

```text
local top-k results
```

Coordinator must merge into:

```text
global top-k
```

---

# 21. Distributed Ranking ASCII

```text
Shard1 Top Results
Shard2 Top Results
Shard3 Top Results
        ↓
Merge By Score
        ↓
Global Top Results
```

---

# 22. Why Replication Needed

Sharding improves scalability.

But what if shard machine crashes?

Need:

```text
replication
```

---

# 23. Replication

Replication means:

```text
multiple copies of shard
```

---

# 24. Replication ASCII

```text
Primary Shard
      ↓
Replica Shard
Replica Shard
```

---

# 25. Primary Shard

Primary shard handles:

```text
writes/indexing
```

Replicas copy data from primary.

---

# 26. Replica Shard

Replica shards handle:

```text
fault tolerance
high availability
read scaling
```

---

# 27. Fault Tolerance Example

Suppose:

```text
Shard1 machine crashes
```

Replica promoted to:

```text
new primary
```

Search cluster survives.

---

# 28. Failover ASCII

```text
Primary Down
      ↓
Replica Promotion
      ↓
Service Continues
```

---

# 29. Read Scaling

Replicas also help:

```text
serve search queries
```

Multiple replicas allow:

```text
higher query throughput
```

---

# 30. Search Load Balancing

Coordinator distributes searches across:

```text
primary + replicas
```

Improves performance.

---

# 31. Search Load ASCII

```text
Query
   ↓
Replica1
Replica2
Replica3
```

---

# 32. Write Replication Flow

Indexing flow:

```text
Client
   ↓
Primary Shard
   ↓
Replicate To Replicas
   ↓
Acknowledge Success
```

---

# 33. Write Replication ASCII

```text
Document Write
      ↓
Primary
      ↓
Replica1
Replica2
```

---

# 34. Refresh vs Commit

Lucene indexing concepts:

```text
refresh
commit
```

Refresh:

```text
makes documents searchable
```

Commit:

```text
durable persistence
```

---

# 35. Near Real-Time Search

Elasticsearch supports:

```text
near real-time search
```

Documents searchable quickly after refresh.

---

# 36. Refresh ASCII

```text
Index Document
      ↓
Refresh
      ↓
Searchable
```

---

# 37. Shard Rebalancing

When new nodes added:

```text
shards redistributed
```

to balance cluster.

---

# 38. Rebalancing ASCII

```text
Old Node Heavy
      ↓
Move Shards
      ↓
Balanced Cluster
```

---

# 39. Hot Shard Problem

Some shards may receive:

```text
much more traffic
```

than others.

Called:

```text
hot shards
```

---

# 40. Hot Shard Example

Suppose:

```text
celebrity index
```

All queries routed to one shard.

That shard overloaded.

---

# 41. Hot Shard ASCII

```text
Shard1 → 95% traffic
Shard2 → idle
Shard3 → idle
```

Bad distribution.

---

# 42. Search Latency Sources

Distributed search latency includes:

```text
network communication
shard execution
result merging
GC pauses
disk IO
```

---

# 43. Query Fan-Out Problem

More shards means:

```text
more parallelism
```

But also:

```text
more network overhead
```

Tradeoff exists.

---

# 44. Too Many Shards Problem

Too many shards cause:

```text
memory overhead
cluster instability
slow coordination
higher metadata cost
```

---

# 45. Shard Sizing Tradeoff

Small shards:

```text
fast recovery
more overhead
```

Large shards:

```text
less overhead
slower recovery
```

Need balance.

---

# 46. Elasticsearch Cluster Architecture

```text
Cluster
 ├── Node1
 │    ├── Primary Shard
 │    └── Replica Shard
 │
 ├── Node2
 │    ├── Primary Shard
 │    └── Replica Shard
 │
 └── Node3
      ├── Primary Shard
      └── Replica Shard
```

---

# 47. Split Brain Problem

Distributed systems risk:

```text
multiple leaders
```

Need cluster coordination.

Elasticsearch uses:

```text
master election
cluster coordination
```

---

# 48. Consistency Tradeoffs

Distributed search balances:

```text
consistency
availability
latency
```

Classic distributed systems tradeoff.

---

# 49. Search Consistency Example

Document indexed:

```text
may not immediately appear on all replicas
```

Eventual consistency possible.

---

# 50. Replica Synchronization

Replicas synchronize using:

```text
operation logs
segment replication
translog/WAL
```

---

# 51. Search During Failure

Even during failures:

```text
remaining replicas serve queries
```

Cluster stays available.

---

# 52. Java SearchShard

```java
import java.util.ArrayList;
import java.util.List;

public class SearchShard {

    private final List<String> documents =
            new ArrayList<>();

    public void addDocument(
            String doc) {

        documents.add(doc);
    }

    public List<String> search(
            String term) {

        List<String> result =
                new ArrayList<>();

        for (String doc : documents) {

            if (doc.contains(term)) {
                result.add(doc);
            }
        }

        return result;
    }
}
```

---

# 53. Java Coordinator

```java
import java.util.*;

public class Coordinator {

    private final List<SearchShard> shards =
            new ArrayList<>();

    public void addShard(
            SearchShard shard) {

        shards.add(shard);
    }

    public List<String> search(
            String term) {

        List<String> result =
                new ArrayList<>();

        for (SearchShard shard : shards) {

            result.addAll(
                    shard.search(term)
            );
        }

        return result;
    }
}
```

---

# 54. Java Distributed Search Dry Run

Shard1:

```text
redis tutorial
```

Shard2:

```text
kafka guide
redis cluster
```

Query:

```text
redis
```

Results:

```text
redis tutorial
redis cluster
```

---

# 55. Java Replication Example

```java
public class ReplicaShard
        extends SearchShard {

}
```

Primary replicates writes to replicas.

---

# 56. Distributed Search Pipeline

```text
User Query
     ↓
Coordinator
     ↓
Scatter To Shards
     ↓
Local BM25 Ranking
     ↓
Merge Results
     ↓
Return Top-K
```

---

# 57. Production Challenges

Real distributed search systems handle:

```text
billions of documents
node failures
cluster rebalancing
hot shards
distributed ranking
cross-region replication
network partitions
```

---

# 58. Performance Tradeoffs

More shards:

```text
better parallelism
more overhead
```

More replicas:

```text
better availability
higher storage cost
```

---

# 59. Common Mistakes

## Mistake 1

```text
Creating too many shards
```

Huge cluster overhead.

---

## Mistake 2

```text
Ignoring hot shard problems
```

Leads to imbalance.

---

## Mistake 3

```text
Treating replicas as free
```

Replicas consume CPU, RAM, storage.

---

## Mistake 4

```text
Assuming distributed ranking trivial
```

Coordinator merge expensive.

---

# 60. Interview Explanation

If interviewer asks:

```text
How does distributed search work?
```

Strong answer:

```text
Distributed search splits indexes into shards distributed across nodes.
Queries are scattered to shards in parallel, local results are ranked,
and a coordinator merges results into final top-k responses.
```

Senior addition:

```text
Replication provides fault tolerance and read scaling while shard routing,
distributed ranking, and cluster coordination handle scalability and
availability challenges.
```

---

# 61. Final Mental Model

```text
Global Search Index
        ↓
Shards
        ↓
Distributed Query Execution
        ↓
Merged Ranked Results
```

---

# 62. What To Remember

```text
Sharding splits indexes horizontally.

Each shard is a mini Lucene index.

Replication provides fault tolerance.

Distributed search uses scatter-gather.

Coordinator merges shard rankings.

Replicas improve read scaling.

Too many shards hurt performance.

Hot shards create imbalance problems.
```

---

# 63. Next File

```text
019_Elasticsearch_Lucene_Internals.md
```

Next you learn:

```text
Lucene internals
segments
FST
posting compression
doc values
translog
refresh/flush
merge policy
Elasticsearch node internals
cluster state
real production architecture
```
