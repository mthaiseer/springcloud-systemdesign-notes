# 07 — Design a Key-Value Store

> FAANG/system-design interview notes with simple diagrams and small Java reference code.

---

## 1. Problem Statement

Design a distributed **key-value store** that supports:

```java
put(key, value)
get(key)
```

A key-value store stores unique keys mapped to opaque values.

Examples:

```text
145 -> john
147 -> bob
160 -> julia
```

### Requirements

| Requirement | Meaning |
|---|---|
| Small object size | Key-value pair usually < 10 KB |
| Big data support | Data cannot fit on one machine |
| High availability | System responds even during failures |
| High scalability | Add/remove nodes automatically |
| Tunable consistency | Choose stronger or weaker consistency per use case |
| Low latency | Fast reads and writes |
| Fault tolerance | No single point of failure |

---

## 2. Single Server Design

Simplest version:

```text
Client
  |
  v
+--------------------+
| Single Server      |
|                    |
| HashMap<Key,Value> |
+--------------------+
```

### Problem

Everything in memory is fast, but memory is limited.

Possible optimizations:

```text
1. Compress data
2. Keep hot data in memory
3. Store cold data on disk
```

But a single server eventually hits limits.

---

## 3. Distributed Key-Value Store

A distributed key-value store spreads data across many servers.

```text
                 +---------+
Client --------> | Router  |
                 +---------+
                     |
       +-------------+-------------+
       |             |             |
       v             v             v
    Node A        Node B        Node C
   key range     key range     key range
```

This is also called a **Distributed Hash Table**, or DHT.

---

## 4. CAP Theorem

In a distributed system, we usually choose 2 of 3:

```text
             CAP

      Consistency
          /\
         /  \
        /    \
       /      \
Availability--Partition Tolerance
```

### Definitions

| Term | Meaning |
|---|---|
| Consistency | Every client sees the latest data |
| Availability | Every request gets a response |
| Partition tolerance | System works despite network splits |

### Practical Choices

| Type | Meaning | Example Use Case |
|---|---|---|
| CP | Consistency + Partition tolerance | Banking, inventory correctness |
| AP | Availability + Partition tolerance | Shopping cart, feeds, metrics |
| CA | Consistency + Availability without partitions | Not realistic in distributed systems |

### Interview Tip

For a highly available key-value store like Dynamo/Cassandra, choose:

```text
AP + eventual consistency
```

because network partitions are unavoidable.

---

## 5. High-Level Architecture

```text
                 +----------+
                 |  Client  |
                 +----------+
                      |
                      | get/put
                      v
              +---------------+
              | Coordinator   |
              +---------------+
                      |
          consistent hashing ring
                      |
      +-------+-------+-------+-------+
      |       |       |       |       |
      v       v       v       v       v
    Node0   Node1   Node2   Node3   Node4
```

### Coordinator

The coordinator is the node that receives the client request and routes it to replica nodes.

```text
Client -> Coordinator -> Replica Nodes
```

Every node can act as a coordinator.

---

## 6. Data Partitioning with Consistent Hashing

Problem with normal hashing:

```text
serverIndex = hash(key) % N
```

When `N` changes, most keys move.

### Consistent Hashing Ring

```text
             s0
          /      \
       k3          k0
      /              \
    s3                s1
      \              /
       k2          k1
          \      /
             s2
```

Rule:

```text
Hash key onto ring.
Move clockwise.
First server found owns the key.
```

Example:

```text
key0 -> clockwise -> s1
key1 -> clockwise -> s2
key2 -> clockwise -> s3
```

### Benefits

| Benefit | Why it matters |
|---|---|
| Less data movement | Only nearby keys move when node changes |
| Horizontal scaling | Add/remove nodes easily |
| Better distribution | Especially with virtual nodes |
| Heterogeneity | Bigger machines can get more virtual nodes |

---

## 7. Java: Simple Consistent Hash Ring

```java
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

public class ConsistentHashRing {
    private final TreeMap<Long, String> ring = new TreeMap<>();
    private final int virtualNodes;

    public ConsistentHashRing(int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    public void addNode(String nodeId) {
        for (int i = 0; i < virtualNodes; i++) {
            long hash = hash(nodeId + "#" + i);
            ring.put(hash, nodeId);
        }
    }

    public void removeNode(String nodeId) {
        for (int i = 0; i < virtualNodes; i++) {
            long hash = hash(nodeId + "#" + i);
            ring.remove(hash);
        }
    }

    public String getNode(String key) {
        if (ring.isEmpty()) {
            throw new IllegalStateException("No nodes available");
        }

        long keyHash = hash(key);
        Map.Entry<Long, String> entry = ring.ceilingEntry(keyHash);

        // Wrap around the ring.
        if (entry == null) {
            entry = ring.firstEntry();
        }

        return entry.getValue();
    }

    private long hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

            long value = 0;
            for (int i = 0; i < 8; i++) {
                value = (value << 8) | (bytes[i] & 0xff);
            }
            return value & Long.MAX_VALUE;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ConsistentHashRing ring = new ConsistentHashRing(100);
        ring.addNode("node-a");
        ring.addNode("node-b");
        ring.addNode("node-c");

        System.out.println("user:1 -> " + ring.getNode("user:1"));
        System.out.println("user:2 -> " + ring.getNode("user:2"));
    }
}
```

---

## 8. Data Replication

To achieve high availability, store each key on multiple nodes.

If replication factor is `N = 3`:

```text
             s0
          /      \
       key0 ---> s1   replica 1
              -> s2   replica 2
              -> s3   replica 3
```

Rule:

```text
Hash key onto ring.
Walk clockwise.
Pick first N unique physical servers.
```

### Why unique physical servers?

With virtual nodes, several virtual nodes may belong to the same physical server. Replicas must be on different physical servers for fault tolerance.

---

## 9. Quorum Consensus

Define:

```text
N = total replicas
W = write acknowledgements required
R = read responses required
```

Example with `N = 3`:

```text
             Coordinator
              /   |   \
             /    |    \
          Node1 Node2 Node3
```

### Common Configurations

| Config | Meaning |
|---|---|
| W = 1, R = 1 | Fast but weak consistency |
| W = 1, R = N | Fast writes, stronger reads |
| W = N, R = 1 | Strong writes, fast reads |
| W + R > N | Strong consistency possible |
| N = 3, W = 2, R = 2 | Common balanced setup |

### Rule of Thumb

```text
If W + R > N, reads and writes overlap on at least one latest replica.
```

---

## 10. Java: Quorum Write Simulation

```java
import java.util.*;

class ReplicaNode {
    private final String name;
    private final Map<String, String> store = new HashMap<>();

    ReplicaNode(String name) {
        this.name = name;
    }

    boolean put(String key, String value) {
        store.put(key, value);
        System.out.println(name + " stored " + key + "=" + value);
        return true; // ACK
    }

    String get(String key) {
        return store.get(key);
    }
}

public class QuorumExample {
    public static boolean quorumPut(
            List<ReplicaNode> replicas,
            String key,
            String value,
            int writeQuorum
    ) {
        int ack = 0;

        for (ReplicaNode node : replicas) {
            if (node.put(key, value)) {
                ack++;
            }
            if (ack >= writeQuorum) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        List<ReplicaNode> replicas = List.of(
                new ReplicaNode("node-1"),
                new ReplicaNode("node-2"),
                new ReplicaNode("node-3")
        );

        boolean success = quorumPut(replicas, "name", "john", 2);
        System.out.println("Write success: " + success);
    }
}
```

---

## 11. Consistency Models

| Model | Meaning | Tradeoff |
|---|---|---|
| Strong consistency | Always read latest value | Higher latency, lower availability |
| Weak consistency | Latest value not guaranteed | Fast, but stale reads possible |
| Eventual consistency | Replicas converge over time | Good for availability |

For this design:

```text
Use eventual consistency with tunable quorum.
```

---

## 12. Inconsistency Resolution with Vector Clocks

Concurrent writes may create conflicts.

Example:

```text
Initial:
name = john

Server A writes:
name = johnSanFrancisco

Server B writes:
name = johnNewYork

Conflict:
Two valid versions exist.
```

### Vector Clock Format

```text
value + { serverId -> version }

D1: name=john              {S1:1}
D2: name=johnSF            {S1:2}
D3: name=johnNY            {S1:1, S2:1}
```

### Conflict Detection

```text
A is ancestor of B if every version in A <= version in B.
Otherwise, they conflict.
```

---

## 13. Java: Vector Clock Conflict Check

```java
import java.util.*;

public class VectorClock {
    private final Map<String, Integer> versions = new HashMap<>();

    public void increment(String serverId) {
        versions.put(serverId, versions.getOrDefault(serverId, 0) + 1);
    }

    public boolean descendsFrom(VectorClock other) {
        for (Map.Entry<String, Integer> entry : other.versions.entrySet()) {
            String server = entry.getKey();
            int otherVersion = entry.getValue();
            int thisVersion = versions.getOrDefault(server, 0);

            if (thisVersion < otherVersion) {
                return false;
            }
        }
        return true;
    }

    public boolean conflictsWith(VectorClock other) {
        return !this.descendsFrom(other) && !other.descendsFrom(this);
    }

    public String toString() {
        return versions.toString();
    }

    public static void main(String[] args) {
        VectorClock a = new VectorClock();
        a.increment("S1");
        a.increment("S1"); // {S1=2}

        VectorClock b = new VectorClock();
        b.increment("S1");
        b.increment("S2"); // {S1=1, S2=1}

        System.out.println("A: " + a);
        System.out.println("B: " + b);
        System.out.println("Conflict? " + a.conflictsWith(b));
    }
}
```

---

## 14. Failure Detection with Gossip Protocol

All-to-all health checks do not scale well.

Bad approach:

```text
Every node talks to every node.
Too expensive when cluster grows.
```

Better approach: **gossip protocol**.

```text
s0 ---> random nodes ---> more random nodes
 |                           |
 | heartbeat info            | membership info spreads
 v                           v
Cluster eventually learns which nodes are alive/down
```

Each node maintains:

```text
memberId -> heartbeatCounter -> lastUpdatedTime
```

If heartbeat does not increase for a while:

```text
node is suspected down
```

---

## 15. Temporary Failure: Sloppy Quorum + Hinted Handoff

Strict quorum may block reads/writes if replica nodes are down.

Sloppy quorum chooses first healthy nodes instead.

```text
Normal replicas for key: s1, s2, s3

s2 is down.

Write goes to: s1, s3, s4
                    |
                    v
              s4 stores hint for s2

When s2 returns:
s4 sends missing data back to s2
```

This is called **hinted handoff**.

---

## 16. Permanent Failure: Merkle Tree + Anti-Entropy

If replicas diverge permanently, compare data efficiently.

Merkle tree idea:

```text
                 rootHash
                /        \
          hash(1-6)      hash(7-12)
          /    \          /     \
     h(1-3)  h(4-6)  h(7-9)  h(10-12)
```

Compare replica trees:

```text
If root hash same -> replicas match
If root hash different -> compare children
Only sync mismatched buckets
```

Benefit:

```text
Transfer data proportional to differences, not full dataset size.
```

---

## 17. Write Path

Inspired by Cassandra/LSM-tree style storage.

```text
Client
  |
  | write(key,value)
  v
+-------- Server --------+
|                        |
| 1. Append Commit Log   | durable on disk
| 2. Update Memtable     | fast memory write
| 3. Flush to SSTable    | when memtable full
|                        |
+------------------------+
```

### Why commit log first?

If the server crashes before flushing memory to disk, data can be replayed from the commit log.

---

## 18. Java: Simple Write Path Simulation

```java
import java.util.*;

class MiniStorageEngine {
    private final List<String> commitLog = new ArrayList<>();
    private final TreeMap<String, String> memtable = new TreeMap<>();
    private final List<Map<String, String>> ssTables = new ArrayList<>();
    private final int flushThreshold;

    MiniStorageEngine(int flushThreshold) {
        this.flushThreshold = flushThreshold;
    }

    public void put(String key, String value) {
        // 1. Durable append. Real systems write this to disk.
        commitLog.add(key + "=" + value);

        // 2. Fast in-memory write.
        memtable.put(key, value);

        // 3. Flush when memory threshold is reached.
        if (memtable.size() >= flushThreshold) {
            flush();
        }
    }

    private void flush() {
        ssTables.add(new TreeMap<>(memtable));
        memtable.clear();
        System.out.println("Flushed memtable to SSTable");
    }

    public String get(String key) {
        // Check latest in-memory data first.
        if (memtable.containsKey(key)) {
            return memtable.get(key);
        }

        // Check newest SSTable first.
        for (int i = ssTables.size() - 1; i >= 0; i--) {
            String value = ssTables.get(i).get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        MiniStorageEngine db = new MiniStorageEngine(3);
        db.put("a", "1");
        db.put("b", "2");
        db.put("c", "3"); // flush
        db.put("a", "4");

        System.out.println(db.get("a")); // 4
        System.out.println(db.get("b")); // 2
    }
}
```

---

## 19. Read Path

```text
Client
  |
  | get(key)
  v
+-------- Server --------+
|                        |
| 1. Check Memtable      |
| 2. Check Bloom Filter  |
| 3. Search SSTables     |
| 4. Return value        |
|                        |
+------------------------+
```

Visual:

```text
Read Request
    |
    v
Memtable hit? ---- yes ----> return value
    |
    no
    v
Bloom filter says possible? ---- no ----> not found
    |
    yes
    v
Search SSTables ----> return value / not found
```

### Bloom Filter

A Bloom filter quickly answers:

```text
Key definitely does not exist
or
Key might exist
```

It can have false positives, but no false negatives.

---

## 20. Java: Tiny Bloom Filter Example

```java
import java.util.*;

public class SimpleBloomFilter {
    private final BitSet bits;
    private final int size;

    public SimpleBloomFilter(int size) {
        this.size = size;
        this.bits = new BitSet(size);
    }

    public void add(String key) {
        bits.set(hash1(key));
        bits.set(hash2(key));
    }

    public boolean mightContain(String key) {
        return bits.get(hash1(key)) && bits.get(hash2(key));
    }

    private int hash1(String key) {
        return Math.abs(key.hashCode()) % size;
    }

    private int hash2(String key) {
        return Math.abs(Objects.hash(key, 31)) % size;
    }

    public static void main(String[] args) {
        SimpleBloomFilter bf = new SimpleBloomFilter(1000);
        bf.add("user:1");

        System.out.println(bf.mightContain("user:1")); // true
        System.out.println(bf.mightContain("user:2")); // probably false
    }
}
```

---

## 21. Full Request Flow

### Write Flow

```text
Client
  |
  v
Coordinator
  |
  | choose replicas using consistent hashing
  v
Replica nodes
  |
  | append commit log
  | update memtable
  | return ACK
  v
Coordinator waits for W ACKs
  |
  v
Client gets success
```

### Read Flow

```text
Client
  |
  v
Coordinator
  |
  | query R replicas
  v
Replica responses
  |
  | compare versions/vector clocks
  v
Return latest value or conflicting versions
```

---

## 22. System Components Inside Each Node

```text
+-----------------------------+
| Node                        |
|                             |
| +-------------------------+ |
| | Client API              | |
| +-------------------------+ |
| | Consistent Hash Router  | |
| +-------------------------+ |
| | Replication             | |
| +-------------------------+ |
| | Conflict Resolution     | |
| +-------------------------+ |
| | Failure Detection       | |
| +-------------------------+ |
| | Storage Engine          | |
| +-------------------------+ |
+-----------------------------+
```

---

## 23. Tradeoffs

| Area | Option A | Option B | Interview Notes |
|---|---|---|---|
| Consistency | Strong | Eventual | Strong is safer but slower |
| Availability | High | Lower | AP systems keep serving during partitions |
| Latency | Low quorum | High quorum | Lower quorum is faster |
| Storage | More replicas | Fewer replicas | More replicas improve reliability |
| Conflict handling | Server-side | Client-side | Dynamo often exposes conflicts to clients |
| Failure repair | Hinted handoff | Merkle tree repair | Temporary vs permanent failure |

---

## 24. FAANG Interview Talking Points

### Start with Requirements

Mention:

```text
- get(key), put(key,value)
- small values, large dataset
- low latency
- high availability
- tunable consistency
- no single point of failure
```

### Then Build the Design

```text
1. Single-node HashMap is simple but not scalable.
2. Use consistent hashing for partitioning.
3. Replicate each key to N nodes.
4. Use quorum reads/writes with R, W, N.
5. Use vector clocks for conflicts.
6. Use gossip for failure detection.
7. Use hinted handoff for temporary failures.
8. Use Merkle trees for anti-entropy repair.
9. Use commit log + memtable + SSTables for storage.
10. Use Bloom filters to speed up reads.
```

### Good Default Values

```text
N = 3
W = 2
R = 2
Consistency = eventual, tunable per request
Partitioning = consistent hashing with virtual nodes
Replication = cross-rack / cross-data-center
```

---

## 25. Common Follow-Up Questions

### Q1. Why not use a relational database?

Because this system prioritizes:

```text
- massive scale
- simple key-based access
- high availability
- horizontal scaling
```

Relational joins and transactions are not the main requirement.

### Q2. How do you handle hot keys?

Options:

```text
- cache hot keys
- replicate hot keys more widely
- split hot key by suffix, for example celebrity:123:shard:1
- use request coalescing
```

### Q3. How do you avoid data loss?

```text
- commit log before memory write
- replication factor N
- cross-data-center replication
- hinted handoff
- anti-entropy repair
```

### Q4. How do you reduce read latency?

```text
- memtable cache
- Bloom filters
- compact SSTables
- read from nearest replica
- cache hot keys
```

### Q5. What happens during network partition?

AP design:

```text
System continues serving reads/writes.
Conflicts may happen.
Resolve later using vector clocks and reconciliation.
```

CP design:

```text
System blocks unsafe operations to preserve consistency.
Availability is reduced.
```

---

## 26. Final Summary Table

| Goal | Technique |
|---|---|
| Store big data | Consistent hashing |
| Scale horizontally | Add nodes to hash ring |
| Even distribution | Virtual nodes |
| High availability | Replication |
| Tunable consistency | Quorum consensus |
| Resolve conflicts | Vector clocks |
| Detect failures | Gossip protocol |
| Temporary failure recovery | Sloppy quorum + hinted handoff |
| Permanent failure repair | Merkle tree + anti-entropy |
| Fast writes | Commit log + memtable |
| Fast reads | Memtable + Bloom filter + SSTables |
| Data center outage | Cross-data-center replication |

---

## 27. One-Minute Interview Pitch

A scalable key-value store partitions data using consistent hashing with virtual nodes. Each key is replicated to `N` unique nodes for availability. Clients talk to any coordinator node, which routes reads and writes to replicas. Consistency is tunable using quorum parameters `R`, `W`, and `N`; if `R + W > N`, stronger consistency is achieved. The system favors high availability and eventual consistency, with vector clocks for conflict detection, gossip for failure detection, hinted handoff for temporary failures, and Merkle trees for anti-entropy repair. Each node uses a log-structured storage engine with commit log, memtable, SSTables, and Bloom filters for low-latency reads and writes.
