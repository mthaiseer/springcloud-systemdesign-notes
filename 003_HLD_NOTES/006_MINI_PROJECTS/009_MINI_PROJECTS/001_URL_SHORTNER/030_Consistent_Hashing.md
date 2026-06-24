# 030_Consistent_Hashing.md
# MiniURLShortener — Consistent Hashing

> Core mental model: **Consistent hashing is a stable routing ring. It decides which shard/cache node owns a key while minimizing movement when nodes are added or removed.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. The Naive Modulo Problem](#4-the-naive-modulo-problem)
- [5. Consistent Hashing Ring Mental Model](#5-consistent-hashing-ring-mental-model)
- [6. Key Ownership Rule](#6-key-ownership-rule)
- [7. Add Node Flow](#7-add-node-flow)
- [8. Remove Node Flow](#8-remove-node-flow)
- [9. Virtual Nodes](#9-virtual-nodes)
- [10. Data Distribution Intuition](#10-data-distribution-intuition)
- [11. Where It Fits In MiniURLShortener](#11-where-it-fits-in-miniurlshortener)
- [12. Cache Routing Use Case](#12-cache-routing-use-case)
- [13. Database Sharding Use Case](#13-database-sharding-use-case)
- [14. Read Path With Consistent Hashing](#14-read-path-with-consistent-hashing)
- [15. Write Path With Consistent Hashing](#15-write-path-with-consistent-hashing)
- [16. Java Implementation](#16-java-implementation)
- [17. Spring Boot Router Service](#17-spring-boot-router-service)
- [18. Step-by-Step Dry Runs](#18-step-by-step-dry-runs)
- [19. Internal Execution Walkthrough](#19-internal-execution-walkthrough)
- [20. Production Scale Example](#20-production-scale-example)
- [21. Failure Stories](#21-failure-stories)
- [22. Debugging Mindset](#22-debugging-mindset)
- [23. Common Mistakes](#23-common-mistakes)
- [24. Testing Strategy](#24-testing-strategy)
- [25. Interview-Ready Explanation](#25-interview-ready-explanation)
- [26. Senior Engineer Checklist](#26-senior-engineer-checklist)
- [27. One-Page Cheat Sheet](#27-one-page-cheat-sheet)
- [28. One Picture To Remember](#28-one-picture-to-remember)

---

## 1. Why This Exists

In MiniURLShortener, the redirect endpoint is read-heavy:

```text
GET /{shortCode}
```

At small scale, one database and one Redis instance are enough.

```text
Client -> App -> Redis -> Postgres
```

At high scale, one cache or one database becomes a bottleneck:

```text
100k RPS redirect traffic
millions of short codes
hot links
cache memory pressure
DB read pressure
node failures
new nodes added during traffic growth
```

You need to split keys across multiple nodes.

Example:

```text
shortCode abc123 -> cache-node-1
shortCode xyz999 -> cache-node-2
shortCode sale77 -> cache-node-3
```

The routing question becomes:

```text
Given a key, which node should own it?
```

The beginner answer is modulo:

```text
nodeIndex = hash(shortCode) % numberOfNodes
```

This works until the number of nodes changes.

If you add one node:

```text
3 nodes -> 4 nodes
```

Most keys move to new owners. That means cache misses explode or database shards need massive migration.

Consistent hashing exists to solve this:

```text
When the cluster changes, move only a small fraction of keys.
```

Production memory:

```text
Modulo routing is simple but unstable.
Consistent hashing is slightly more complex but stable during scaling.
```

---

## 2. The One Core Mental Model

Consistent hashing is a:

```text
STABLE ROUTING RING
```

Imagine all hash values arranged on a circle.

```text
0 ---------------------------------------------------- MAX_HASH
|                                                        |
+------------------------ circle ------------------------+
```

Nodes are placed on the circle.

Keys are also placed on the circle.

A key belongs to the first node found when walking clockwise.

ASCII:

```text
                 hash ring

                       [Node B]
                          ^
                          |
              key1 -----> |
                    .-----+-----.
                 .'             '.
        [Node A]                  [Node C]
                 '.             .'
                    '-----------'

Rule:
key -> hash(key) -> walk clockwise -> first node
```

One-line memory:

```text
Put nodes and keys on the same circle; each key belongs to the next node clockwise.
```

Why this helps:

```text
If one node is added, only keys in one nearby arc move.
If one node is removed, only its keys move to the next node.
Other keys keep the same owner.
```

This is the whole idea.

Everything else is implementation detail:

```text
hash function
ring data structure
virtual nodes
node discovery
migration
replication
monitoring
```

---

## 3. Problem Statement

Build a consistent hashing router for MiniURLShortener.

It must answer:

```text
Given shortCode, return target cache shard or DB shard.
```

It should support:

```text
1. Multiple physical nodes.
2. Node lookup by key.
3. Adding a node with minimal key movement.
4. Removing a node with minimal key movement.
5. Virtual nodes for better balance.
6. Deterministic routing across app pods.
7. Simple Java implementation.
8. Spring Boot service shape.
9. Debugging and testing strategy.
```

It should avoid:

```text
full cluster remapping on scale-out
hotspot caused by poor distribution
non-deterministic node order between app pods
hashCode instability mistakes
routing reads and writes differently
using consistent hashing when a managed DB already handles sharding internally
```

Out of scope for this chapter:

```text
1. Full Redis Cluster protocol.
2. Cassandra token ring internals.
3. Automatic data migration engine.
4. Raft/gossip membership.
5. Cross-region replication.
```

This chapter gives the mental model and production-shaped router.

---

## 4. The Naive Modulo Problem

The simplest routing formula is:

```text
shard = hash(key) % N
```

For 3 nodes:

```text
N = 3

hash(abc123) = 10
10 % 3 = 1 -> node-1

hash(sale77) = 11
11 % 3 = 2 -> node-2

hash(xyz999) = 12
12 % 3 = 0 -> node-0
```

Looks good.

But now add one node.

```text
N = 4
```

Same keys:

```text
10 % 4 = 2 -> node-2   moved
11 % 4 = 3 -> node-3   moved
12 % 4 = 0 -> node-0   same
```

Most keys move.

ASCII:

```text
Before: 3 nodes

key -> hash % 3

abc123 -> node-1
sale77 -> node-2
xyz999 -> node-0

After: 4 nodes

key -> hash % 4

abc123 -> node-2   changed
sale77 -> node-3   changed
xyz999 -> node-0   same
```

At cache layer, this causes:

```text
massive cache miss storm
more DB reads
higher p99 latency
possible DB overload
```

At database sharding layer, this causes:

```text
large data migration
routing inconsistency
complex dual-write/backfill
risk of lost reads
```

Mental model:

```text
Modulo binds every key to total node count.
When node count changes, the formula changes for almost every key.
```

Consistent hashing changes the question.

Instead of:

```text
Which bucket number from 0..N-1?
```

It asks:

```text
Where is the key on the ring, and what node is next clockwise?
```

That makes routing stable.

---

## 5. Consistent Hashing Ring Mental Model

Think of hash space as a clock.

```text
0 to 359 degrees
```

Nodes live at positions:

```text
Node A -> 40
Node B -> 160
Node C -> 280
```

Keys live at positions:

```text
key1 -> 20
key2 -> 90
key3 -> 200
key4 -> 330
```

Ownership rule:

```text
key belongs to first node clockwise
```

ASCII clock:

```text
                         0/360
                           |
                 key4=330  | key1=20
                       \   |   /
                        \  |  /
                         \ | /
Node C=280 --------------- + --------------- Node A=40
                           |
                           |
                     Node B=160

key1=20  -> Node A=40
key2=90  -> Node B=160
key3=200 -> Node C=280
key4=330 -> Node A=40 because ring wraps around
```

The wrap-around is important.

If key hash is greater than all node hashes:

```text
key position = 330
nodes = 40,160,280
no node clockwise before MAX
wrap to first node = 40
```

This is why ring implementation usually uses a sorted map.

Java data structure:

```text
TreeMap<Long, Node>
```

Lookup:

```text
tailMap(hash).firstKey()
if none, use firstKey()
```

ASCII:

```text
Sorted ring positions:

40  -> Node A
160 -> Node B
280 -> Node C

key hash = 90
next position >= 90 is 160
owner = Node B
```

One rule, all cases.

---

## 6. Key Ownership Rule

The key ownership rule is the heart of consistent hashing.

```text
owner(key) = first node clockwise from hash(key)
```

Example ring:

```text
10  -> node-A
40  -> node-B
80  -> node-C
```

Keys:

```text
hash(k1) = 5
hash(k2) = 20
hash(k3) = 50
hash(k4) = 90
```

Lookup table:

```text
+------+-----------+-------------------------+--------+
| Key  | Key Hash  | First Node Clockwise    | Owner  |
+------+-----------+-------------------------+--------+
| k1   | 5         | 10                      | A      |
| k2   | 20        | 40                      | B      |
| k3   | 50        | 80                      | C      |
| k4   | 90        | wrap to 10              | A      |
+------+-----------+-------------------------+--------+
```

Diagram:

```text
0      10         40              80        100
|------A----------B---------------C---------|
   k1      k2            k3            k4

k1 -> A
k2 -> B
k3 -> C
k4 -> A after wrap
```

Notice that nodes own ranges.

```text
Node A owns: (80, MAX] + [0,10]
Node B owns: (10,40]
Node C owns: (40,80]
```

This range view is useful for debugging.

If node B is hot, check:

```text
1. Is its ring range too large?
2. Are many hot keys inside its range?
3. Are virtual nodes insufficient?
4. Is hash function poor?
```

---

## 7. Add Node Flow

Suppose current ring:

```text
10 -> A
40 -> B
80 -> C
```

Ranges:

```text
A owns (80,10]
B owns (10,40]
C owns (40,80]
```

Add node D at 60.

New ring:

```text
10 -> A
40 -> B
60 -> D
80 -> C
```

New ranges:

```text
A owns (80,10]
B owns (10,40]
D owns (40,60]
C owns (60,80]
```

Only part of C's old range moved to D.

ASCII:

```text
Before:

0----10(A)----40(B)----------------80(C)----100
              |<------ C owns ----->|

After adding D at 60:

0----10(A)----40(B)-----60(D)------80(C)----100
              |<- D owns ->|<- C ->|
```

Moved keys:

```text
Only keys with hash in (40,60]
```

Unaffected:

```text
A range unchanged
B range unchanged
C keeps (60,80]
```

This is the main win.

Production meaning:

```text
Adding a cache node warms only a fraction of keyspace.
Adding a DB shard migrates only one range, not everything.
```

With many nodes, adding one node roughly moves:

```text
1 / (number of nodes + 1)
```

of keys, assuming balanced distribution.

---

## 8. Remove Node Flow

Current ring:

```text
10 -> A
40 -> B
60 -> D
80 -> C
```

Remove D.

Before removal:

```text
D owns (40,60]
```

After removal:

```text
10 -> A
40 -> B
80 -> C
```

D's keys move to the next clockwise node.

```text
D range (40,60] moves to C
```

ASCII:

```text
Before:

0----10(A)----40(B)-----60(D)------80(C)----100
              |<- D owns ->|<- C owns ----->|

After removing D:

0----10(A)----40(B)----------------80(C)----100
              |<------ C owns ----->|
```

Only removed node's keys move.

This matters during failure:

```text
cache-node-2 dies
only its keys miss and refill elsewhere
other cache nodes keep serving their keys
```

But note:

```text
If a very large node range disappears, the next node can become overloaded.
```

That is why virtual nodes and replication matter.

---

## 9. Virtual Nodes

Physical nodes placed once on the ring can be uneven.

Example:

```text
A at 10
B at 20
C at 90
```

Ranges:

```text
A owns (90,10]  -> 20% if ring 0..100
B owns (10,20]  -> 10%
C owns (20,90]  -> 70%
```

C becomes hot.

ASCII:

```text
0----10(A)--20(B)-----------------------------90(C)----100
           B tiny       C huge range             A wrap
```

Virtual nodes solve this by placing each physical node many times on the ring.

```text
Node A -> A#0, A#1, A#2, A#3...
Node B -> B#0, B#1, B#2, B#3...
Node C -> C#0, C#1, C#2, C#3...
```

ASCII:

```text
0----A0----C1----B0----A1----C0----B1----A2----C2----B2----100
```

Each virtual node owns a small range.

A physical node owns the union of its virtual-node ranges.

Why this helps:

```text
1. Better load balance.
2. Smaller movement chunks.
3. Easier weighted capacity.
```

Weighted capacity example:

```text
small node  -> 100 virtual nodes
large node  -> 300 virtual nodes
```

The large node appears more often and owns more ranges.

Production rule:

```text
Without virtual nodes, consistent hashing can be badly imbalanced.
With virtual nodes, distribution becomes smoother.
```

---

## 10. Data Distribution Intuition

Consistent hashing is not magic.

It depends on:

```text
hash function quality
number of physical nodes
number of virtual nodes
key popularity distribution
```

Balanced hash positions do not guarantee balanced traffic.

Example:

```text
shortCode = worldcup2026
```

If this one short code gets 30k RPS, its owner node is hot even if ring distribution is perfect.

Two different balances exist:

```text
Key count balance:
    each node owns similar number of keys

Traffic balance:
    each node receives similar RPS
```

Consistent hashing mostly helps key count distribution.

Hot-key mitigation needs extra strategies:

```text
1. local in-memory cache for top hot keys
2. replicated cache entries
3. CDN edge caching for redirects
4. request coalescing
5. hot-key detection
6. special routing for celebrity links
```

ASCII:

```text
Balanced key ranges:

Node A: 1M keys
Node B: 1M keys
Node C: 1M keys

But traffic:

Node A: 5k RPS
Node B: 60k RPS  <- hot key lives here
Node C: 5k RPS
```

Senior distinction:

```text
Consistent hashing balances ownership, not necessarily popularity.
```

---

## 11. Where It Fits In MiniURLShortener

MiniURLShortener high-scale path:

```text
Client
  |
  v
Load Balancer
  |
  v
Spring Boot App Pods
  |
  +--> Redis cache cluster/shards
  |
  +--> Postgres/Cassandra shards
```

Consistent hashing can be used in two places:

```text
1. Cache shard routing
2. Database shard routing
```

Cache shard routing:

```text
shortCode -> ConsistentHashRouter -> Redis node
```

Database shard routing:

```text
shortCode -> ConsistentHashRouter -> DB shard
```

ASCII:

```text
GET /abc123
    |
    v
+----------------------+
| Spring Boot App Pod  |
+----------------------+
    |
    v
+----------------------+
| Consistent Hash Ring |
+----------------------+
    |
    +---- abc123 -> Redis-2
    |
    +---- abc123 -> DB-Shard-2
```

Important:

```text
The same key must route consistently for reads and writes.
```

If create writes `abc123` to shard-2 but redirect reads shard-1:

```text
404 false not found
```

Routing must be deterministic across all app pods.

That means:

```text
same node list
same virtual node count
same hash function
same sorting
same key normalization
same deployment configuration
```

---

## 12. Cache Routing Use Case

For cache routing, consistent hashing is easier because cache data can be rebuilt.

Redirect flow:

```text
GET /abc123
```

Cache lookup:

```text
redisNode = ring.getNode("abc123")
value = redisNode.get("url:abc123")
```

If cache miss:

```text
lookup DB
write to same cache node
return redirect
```

ASCII:

```text
GET /abc123
    |
    v
hash(abc123)
    |
    v
Ring owner = Redis-2
    |
    v
GET url:abc123 from Redis-2
    |
    +-- hit  -> 302 redirect
    |
    +-- miss -> DB lookup -> SET Redis-2 -> 302 redirect
```

When adding Redis-4:

```text
some keys now route to Redis-4
those keys miss at first
DB fills cache gradually
```

This is acceptable if DB can handle warm-up.

Production caution:

```text
Do not add many cache nodes at peak traffic without warm-up.
```

Better:

```text
1. Add node gradually.
2. Prewarm popular keys.
3. Monitor DB QPS and p99.
4. Use TTL jitter.
```

---

## 13. Database Sharding Use Case

Database sharding is more serious than cache sharding.

If consistent hashing routes DB writes:

```text
shortCode -> DB shard
```

Then the row physically lives on that shard.

Create API:

```text
shortCode abc123 -> DB-Shard-2 -> INSERT
```

Redirect API:

```text
shortCode abc123 -> DB-Shard-2 -> SELECT
```

If ring changes, ownership changes.

Problem:

```text
The row may still be on old shard while router sends reads to new shard.
```

Therefore, DB sharding needs migration strategy.

Possible approaches:

```text
1. Freeze ring for DB shards.
2. Add new shard only with controlled range migration.
3. Keep routing metadata/version.
4. Use dual-read during migration.
5. Use managed distributed database or Cassandra-like token ring.
```

ASCII migration:

```text
Before adding Shard-D:

Range (40,80] belongs to Shard-C
Rows physically on Shard-C

After adding Shard-D at 60:

Range (40,60] should belong to Shard-D
Rows must move C -> D

Until migration completes:

Router must know where old rows are.
```

Senior rule:

```text
Consistent hashing tells you target ownership; it does not automatically move persistent data safely.
```

For MiniURLShortener learning project:

```text
Use consistent hashing first for Redis/cache routing.
For DB sharding, explain migration and implement only after core project is stable.
```

---

## 14. Read Path With Consistent Hashing

Redirect read path:

```text
GET /{shortCode}
```

ASCII:

```text
Client
  |
  v
LB
  |
  v
App Pod
  |
  v
Validate shortCode
  |
  v
ConsistentHashRouter.getNode(shortCode)
  |
  v
Redis shard lookup
  |
  +-- HIT ---------------------> return 302
  |
  +-- MISS
        |
        v
   DB shard lookup
        |
        +-- not found -> 404
        +-- expired   -> 410
        +-- blocked   -> 403
        +-- active    -> cache set -> 302
```

Key principle:

```text
The router must run before cache lookup.
```

Bad path:

```text
try all Redis nodes until found
```

Why bad:

```text
1. More network calls.
2. Higher p99.
3. Hides routing bugs.
4. Does not scale.
```

Correct path:

```text
one key -> one owner node
```

With replication, it can be:

```text
one key -> primary owner + replica owners
```

But the ownership rule must still be deterministic.

---

## 15. Write Path With Consistent Hashing

Create short URL path:

```text
POST /api/v1/urls
```

ASCII:

```text
Client
  |
  v
App Pod
  |
  v
Generate / accept shortCode
  |
  v
Validate shortCode
  |
  v
ConsistentHashRouter.getNode(shortCode)
  |
  v
Write DB shard
  |
  v
Write cache shard or invalidate
  |
  v
Return short URL
```

Important:

```text
The shortCode must exist before routing.
```

For custom alias:

```text
alias = mohamed
router.getNode("mohamed")
insert into that shard
```

For generated ID:

```text
shortCode = base62(id)
router.getNode(shortCode)
insert into that shard
```

Uniqueness issue:

```text
If each shortCode maps to exactly one shard, uniqueness check is local to that shard.
```

But if custom aliases need global uniqueness and routing changes:

```text
Need stable routing or global alias registry.
```

For MiniURLShortener:

```text
Use shortCode as the sharding key.
All operations for one shortCode go to same shard.
```

---

## 16. Java Implementation

A simple consistent hash ring can use:

```text
TreeMap<Long, String>
```

Where:

```text
key   = hash position
value = physical node id
```

Implementation:

```java
package com.miniurl.shortener.routing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ConsistentHashRing {

    private final TreeMap<Long, String> ring = new TreeMap<>();
    private final int virtualNodes;

    public ConsistentHashRing(List<String> physicalNodes, int virtualNodes) {
        if (physicalNodes == null || physicalNodes.isEmpty()) {
            throw new IllegalArgumentException("At least one node is required");
        }
        if (virtualNodes <= 0) {
            throw new IllegalArgumentException("virtualNodes must be positive");
        }

        this.virtualNodes = virtualNodes;

        for (String node : physicalNodes) {
            addNode(node);
        }
    }

    public void addNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            String virtualNodeKey = node + "#" + i;
            long hash = hash64(virtualNodeKey);
            ring.put(hash, node);
        }
    }

    public void removeNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            String virtualNodeKey = node + "#" + i;
            long hash = hash64(virtualNodeKey);
            ring.remove(hash);
        }
    }

    public String getNode(String key) {
        if (ring.isEmpty()) {
            throw new IllegalStateException("No nodes available in hash ring");
        }

        long keyHash = hash64(key);

        Map.Entry<Long, String> entry = ring.ceilingEntry(keyHash);

        if (entry == null) {
            entry = ring.firstEntry();
        }

        return entry.getValue();
    }

    public int ringSize() {
        return ring.size();
    }

    private static long hash64(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            long result = 0;
            for (int i = 0; i < 8; i++) {
                result = (result << 8) | (bytes[i] & 0xffL);
            }
            return result & Long.MAX_VALUE;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
```

Why not Java `String.hashCode()`?

```text
1. It is only 32-bit.
2. It is not ideal for ring distribution.
3. It can cause more collisions.
4. It is easy to misuse across languages.
```

For production, you may use:

```text
MurmurHash3
xxHash
SHA-256 truncated to 64-bit
```

For learning, SHA-256 truncated to 64-bit is clear and deterministic.

---

## 17. Spring Boot Router Service

In MiniURLShortener, wrap routing behind a service.

```java
package com.miniurl.shortener.routing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShardRouterService {

    private final ConsistentHashRing cacheRing;
    private final ConsistentHashRing databaseRing;

    public ShardRouterService(
            @Value("${miniurl.cache.nodes}") List<String> cacheNodes,
            @Value("${miniurl.database.shards}") List<String> databaseShards,
            @Value("${miniurl.routing.virtual-nodes:128}") int virtualNodes
    ) {
        this.cacheRing = new ConsistentHashRing(cacheNodes, virtualNodes);
        this.databaseRing = new ConsistentHashRing(databaseShards, virtualNodes);
    }

    public String cacheNodeFor(String shortCode) {
        return cacheRing.getNode(normalize(shortCode));
    }

    public String databaseShardFor(String shortCode) {
        return databaseRing.getNode(normalize(shortCode));
    }

    private String normalize(String shortCode) {
        if (shortCode == null || shortCode.isBlank()) {
            throw new IllegalArgumentException("shortCode is required");
        }
        return shortCode.trim();
    }
}
```

Example configuration:

```yaml
miniurl:
  routing:
    virtual-nodes: 128
  cache:
    nodes:
      - redis-1:6379
      - redis-2:6379
      - redis-3:6379
  database:
    shards:
      - shard-1
      - shard-2
      - shard-3
```

Usage in redirect service:

```java
public RedirectTarget findRedirectTarget(String shortCode) {
    String cacheNode = shardRouterService.cacheNodeFor(shortCode);

    Optional<RedirectTarget> cached = cacheClient.get(cacheNode, shortCode);
    if (cached.isPresent()) {
        return cached.get();
    }

    String dbShard = shardRouterService.databaseShardFor(shortCode);
    RedirectTarget target = repository.findByShortCode(dbShard, shortCode)
            .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));

    cacheClient.set(cacheNode, shortCode, target);
    return target;
}
```

Production note:

```text
Keep router logic separate from business logic.
Service asks router where to go; service should not know ring internals.
```

---

## 18. Step-by-Step Dry Runs

### Dry Run 1: Basic lookup

Ring:

```text
10 -> A
40 -> B
80 -> C
```

Key:

```text
shortCode = abc123
hash = 35
```

Flow:

```text
1. Hash abc123 to 35.
2. Find first node hash >= 35.
3. 40 is the first clockwise node.
4. 40 maps to B.
5. Owner is B.
```

Diagram:

```text
0----10(A)----------35(key)----40(B)-----------80(C)----100
                              clockwise -----> owner B
```

---

### Dry Run 2: Wrap-around lookup

Ring:

```text
10 -> A
40 -> B
80 -> C
```

Key hash:

```text
95
```

Flow:

```text
1. Hash key to 95.
2. No node hash >= 95.
3. Wrap to first node in ring.
4. First node is 10.
5. Owner is A.
```

Diagram:

```text
0----10(A)----40(B)----80(C)----------95(key)----100
|<---------------------- wrap clockwise ----------|
owner A
```

---

### Dry Run 3: Add node

Before:

```text
10 -> A
40 -> B
80 -> C
```

Add:

```text
60 -> D
```

Key hashes:

```text
50 -> before owner C, after owner D
70 -> before owner C, after owner C
20 -> before owner B, after owner B
```

Table:

```text
+----------+--------------+-------------+--------+
| Key Hash | Before Owner | After Owner | Moved? |
+----------+--------------+-------------+--------+
| 20       | B            | B           | No     |
| 50       | C            | D           | Yes    |
| 70       | C            | C           | No     |
| 90       | A            | A           | No     |
+----------+--------------+-------------+--------+
```

Only keys in:

```text
(40,60]
```

move to D.

---

### Dry Run 4: Remove node

Before:

```text
10 -> A
40 -> B
60 -> D
80 -> C
```

Remove:

```text
D
```

D owned:

```text
(40,60]
```

After removal, those keys go to C.

```text
50 -> D before, C after
70 -> C before, C after
20 -> B before, B after
```

Only D's old range moves.

---

### Dry Run 5: Virtual nodes

Physical nodes:

```text
A, B, C
```

Virtual nodes:

```text
3 per physical node
```

Ring entries:

```text
A#0 -> 12
A#1 -> 58
A#2 -> 91
B#0 -> 20
B#1 -> 63
B#2 -> 77
C#0 -> 5
C#1 -> 44
C#2 -> 86
```

Sorted ring:

```text
5(C) 12(A) 20(B) 44(C) 58(A) 63(B) 77(B) 86(C) 91(A)
```

Now each physical node owns multiple smaller ranges.

This is smoother than one giant range per physical node.

---

## 19. Internal Execution Walkthrough

When `getNode("abc123")` runs:

```text
1. Normalize key.
2. Hash key to 64-bit integer.
3. Search TreeMap for ceiling entry.
4. If found, return that node.
5. If not found, wrap to first entry.
```

ASCII:

```text
shortCode
   |
   v
hash64(shortCode)
   |
   v
TreeMap.ceilingEntry(hash)
   |
   +-- exists -> return node
   |
   +-- null   -> ring.firstEntry()
```

For add node:

```text
1. For i = 0..virtualNodes-1
2. virtualKey = node + "#" + i
3. hash virtualKey
4. put hash -> physicalNode into TreeMap
```

For remove node:

```text
1. For i = 0..virtualNodes-1
2. virtualKey = node + "#" + i
3. hash virtualKey
4. remove hash from TreeMap
```

Complexity:

```text
Let V = total virtual nodes.
Lookup: O(log V)
Add physical node: O(K log V), K = virtual nodes per physical node
Remove physical node: O(K log V)
Memory: O(V)
```

For 10 nodes with 128 virtual nodes:

```text
V = 1280
lookup cost is tiny
```

---

## 20. Production Scale Example

Assume MiniURLShortener has:

```text
100k redirect RPS
3 Redis cache shards
3 DB read shards
shortCode key routing
```

Without consistent hashing:

```text
hash(shortCode) % 3
```

Add Redis-4:

```text
hash(shortCode) % 4
```

Effect:

```text
large percentage of keys route to different Redis node
cache hit ratio drops sharply
DB QPS spikes
p99 latency increases
```

With consistent hashing:

```text
add Redis-4 into ring
only keys in Redis-4's new ranges move
most keys stay on same Redis node
cache hit ratio drops only partially
```

ASCII:

```text
Scale-out event:

Before:
App -> Redis-1 / Redis-2 / Redis-3

After:
App -> Redis-1 / Redis-2 / Redis-3 / Redis-4

Modulo:
    many keys remapped

Consistent hashing:
    only nearby ring ranges remapped
```

Operational rollout:

```text
1. Deploy all app pods with same new ring config.
2. Add Redis-4.
3. Monitor cache hit ratio.
4. Monitor DB QPS.
5. Monitor p95/p99 redirect latency.
6. Prewarm top keys if needed.
7. Roll back ring config if DB starts melting.
```

Production lesson:

```text
Consistent hashing reduces blast radius during scaling.
It does not remove the need for rollout discipline.
```

---

## 21. Failure Stories

### Failure Story 1: Cache miss storm after adding node

Team uses:

```text
hash(key) % N
```

They add one Redis node during peak traffic.

Suddenly:

```text
cache hit ratio drops from 95% to 40%
DB QPS triples
p99 latency jumps
redirects timeout
```

Root cause:

```text
Modulo remapped most keys.
```

Fix:

```text
Use consistent hashing with virtual nodes.
Add nodes gradually.
Prewarm hot keys.
```

Lesson:

```text
Routing formula can become a production incident.
```

---

### Failure Story 2: App pods disagree on ring

Pod A config:

```text
redis-1, redis-2, redis-3
```

Pod B config:

```text
redis-1, redis-2, redis-3, redis-4
```

Same key:

```text
Pod A routes abc123 -> redis-2
Pod B routes abc123 -> redis-4
```

Symptoms:

```text
random cache misses
inconsistent metrics
hard-to-debug p99 spikes
```

Root cause:

```text
rolling deployment caused mixed ring versions.
```

Fix:

```text
Version ring config.
Roll out carefully.
For DB sharding, never allow mixed ownership without migration plan.
```

Lesson:

```text
Consistent hashing must be consistent across clients.
```

---

### Failure Story 3: No virtual nodes causes hotspot

Ring:

```text
A at 10
B at 20
C at 90
```

C owns huge range.

Symptoms:

```text
C memory high
C CPU high
C latency high
A and B mostly idle
```

Root cause:

```text
Physical nodes placed once; ring distribution uneven.
```

Fix:

```text
Use 128 or 256 virtual nodes per physical node.
Measure distribution.
```

Lesson:

```text
Consistent hashing without virtual nodes is often incomplete.
```

---

### Failure Story 4: DB shard added without migration

Team adds DB-Shard-4 to ring.

Router sends some existing short codes to DB-Shard-4.

But rows still exist on old DB-Shard-2.

Symptoms:

```text
valid short links return 404
customer complaints
support tickets explode
```

Root cause:

```text
Ring ownership changed before persistent data migration.
```

Fix:

```text
Use migration plan, ring versioning, dual-read, or routing metadata.
```

Lesson:

```text
Cache movement is cheap. Database movement is dangerous.
```

---

## 22. Debugging Mindset

When routing looks wrong, ask:

```text
What key was routed?
What hash value was produced?
Which ring version was used?
Which virtual node was selected?
Which physical node was returned?
Do all app pods have same node list?
Do all app pods have same virtual node count?
Was the node recently added or removed?
Is this cache routing or DB routing?
Is the issue key-count imbalance or traffic-hot-key imbalance?
```

Debug log fields:

```text
shortCode
keyHash
ringVersion
selectedVirtualNodeHash
selectedPhysicalNode
operationType
cacheHit
latencyMs
```

Example debug line:

```text
shortCode=abc123 keyHash=721992 ringVersion=v12 selectedNode=redis-2 cacheHit=false latencyMs=18
```

Useful test command idea:

```text
route 10,000 sample shortCodes and count owners
```

Expected:

```text
roughly balanced distribution
```

If not balanced:

```text
increase virtual nodes
check hash function
check node weights
check ring construction
```

Golden debugging rule:

```text
Before blaming Redis or DB, prove the key routed to the expected owner.
```

---

## 23. Common Mistakes

### Mistake 1: Using modulo for dynamic clusters

Wrong:

```text
hash(key) % nodeCount
```

Correct:

```text
consistent hashing ring
```

Modulo is okay only when node count is fixed or remapping is acceptable.

---

### Mistake 2: No virtual nodes

Wrong:

```text
one ring point per physical node
```

Correct:

```text
many virtual nodes per physical node
```

---

### Mistake 3: Different rings in different app pods

Wrong:

```text
pod A and pod B have different node lists
```

Correct:

```text
centralized config, versioned rollout, deterministic ring
```

---

### Mistake 4: Changing DB ring without migration

Wrong:

```text
add DB shard and immediately route existing keys there
```

Correct:

```text
planned migration or stable routing metadata
```

---

### Mistake 5: Confusing cache shard and DB shard safety

Wrong:

```text
Cache remapping is fine, so DB remapping is fine too.
```

Correct:

```text
Cache can refill. DB rows must be moved safely.
```

---

### Mistake 6: Using non-deterministic hash or ordering

Wrong:

```text
ring depends on random order or environment-specific behavior
```

Correct:

```text
same input always builds same ring
```

---

### Mistake 7: Thinking consistent hashing solves hot keys

Wrong:

```text
consistent hashing balances all traffic automatically
```

Correct:

```text
it balances ownership; hot keys need separate mitigation
```

---

## 24. Testing Strategy

Test consistent hashing like infrastructure code.

### Test 1: Same key returns same node

```java
String n1 = ring.getNode("abc123");
String n2 = ring.getNode("abc123");
assertEquals(n1, n2);
```

### Test 2: All keys return valid nodes

```java
for (String key : sampleKeys) {
    assertTrue(nodes.contains(ring.getNode(key)));
}
```

### Test 3: Distribution is reasonable

```text
Generate 100,000 keys.
Route them.
Count per physical node.
Verify no node has extreme imbalance.
```

Example expected:

```text
Node A: 33.1%
Node B: 32.8%
Node C: 34.1%
```

### Test 4: Add node moves limited keys

```text
Create ring with A,B,C.
Route 100,000 keys.
Create ring with A,B,C,D.
Route same keys.
Count moved keys.
Expected around 25%, not 75%.
```

### Test 5: Remove node moves only that node's keys

```text
Before removal, record owner for every key.
Remove node B.
Keys not owned by B should mostly stay where they were.
```

### Test 6: App pod determinism

```text
Build ring twice from same config.
Route same keys.
Every key must return same node.
```

Testing mindset:

```text
Routing bugs are production bugs.
Test stability, distribution, and remapping behavior.
```

---

## 25. Interview-Ready Explanation

If interviewer asks:

```text
Why use consistent hashing in a URL shortener?
```

Strong answer:

```text
A URL shortener is read-heavy, and at scale we often split cache or storage by shortCode. The naive approach is hash(shortCode) modulo numberOfNodes, but when we add or remove a node, the node count changes and most keys remap. That can cause a cache miss storm or a dangerous database migration. Consistent hashing places both nodes and keys on a hash ring. A key belongs to the first node clockwise from its hash. When a node is added, only keys in the adjacent range move to the new node. When a node is removed, only that node's range moves to the next node. In production, I would use virtual nodes to improve balance, a deterministic hash function, versioned ring configuration, and careful migration strategy for persistent database shards. For cache shards, remapping is usually acceptable with warm-up; for DB shards, data movement must be explicitly planned.
```

If interviewer asks:

```text
Does consistent hashing solve hot keys?
```

Strong answer:

```text
Not completely. Consistent hashing balances key ownership, assuming hash distribution is good. But traffic can still be skewed if one shortCode becomes extremely popular. For hot keys, I would use CDN caching, local in-memory cache, replicated cache entries, request coalescing, or special hot-key routing.
```

Senior one-liner:

```text
Consistent hashing reduces key movement during cluster changes; virtual nodes improve balance; migration strategy is still required for persistent data.
```

---

## 26. Senior Engineer Checklist

Before using consistent hashing in MiniURLShortener, confirm:

```text
[ ] Routing key is clearly chosen, usually shortCode
[ ] Same key is used for read and write routing
[ ] Hash function is deterministic and well-distributed
[ ] Ring uses sorted positions
[ ] Wrap-around case is handled
[ ] Virtual nodes are enabled
[ ] Virtual node count is configurable
[ ] All app pods use same ring config
[ ] Ring config has versioning for production
[ ] Cache routing and DB routing are treated differently
[ ] Cache warm-up plan exists for node addition
[ ] DB migration plan exists before DB ring changes
[ ] Distribution tests exist
[ ] Remapping tests exist
[ ] Debug logs can show selected node
[ ] Hot-key mitigation is considered separately
```

If these are checked, your routing model is production-shaped.

---

## 27. One-Page Cheat Sheet

```text
Core mental model:
Consistent hashing is a stable routing ring.

Why:
Modulo remaps too many keys when node count changes.

Ring rule:
hash(key) -> walk clockwise -> first node owns key.

Data structure:
TreeMap<hashPosition, physicalNode>

Lookup:
ceilingEntry(keyHash)
if null -> firstEntry()

Add node:
Only keys in new node's range move.

Remove node:
Removed node's keys move to next clockwise node.

Virtual nodes:
Place each physical node many times on ring.
Improves balance and smaller movement chunks.

Good for:
cache shard routing
load distribution
minimizing cache misses during scale-out

Careful for:
DB sharding because rows must migrate safely.

Does not solve:
hot-key traffic imbalance by itself.

Production must-haves:
same config across pods
deterministic hash
virtual nodes
ring versioning
metrics and debug logs
migration plan for persistent data
```

---

## 28. One Picture To Remember

```text
                 CONSISTENT HASHING MENTAL MODEL

                         hash ring

                              0
                              |
                              v
                    .-------------------.
                 .'                       '.
              .'       key=abc123           '.
             /           hash=35              \
            |                                  |
            |   Node A=10       Node B=40     |
            |        ^              ^         |
            |        |              |         |
            |        |       abc123 walks     |
            |        |       clockwise ------>|
            |                                  |
             \                                /
              '.          Node C=80          .'
                '.                         .'
                  '-----------------------'

Rule:
    key -> hash -> next clockwise node

When adding Node D:
    only keys near D move

When removing Node B:
    only B's keys move to next node

Virtual nodes:
    A#1 A#2 A#3 B#1 B#2 B#3 C#1 C#2 C#3
    smoother balance

Final memory:
    Modulo changes the whole map.
    Consistent hashing changes only nearby ranges.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Consistent hashing maps keys and nodes onto the same ring.
2. A key belongs to the first node clockwise from its hash.
3. Adding or removing a node moves only nearby ranges, not almost all keys.
4. Virtual nodes improve balance and reduce hotspot risk from uneven ring placement.
5. Cache remapping is recoverable, but DB shard remapping requires careful migration.
```

After this chapter, you understand how MiniURLShortener can route short codes across cache or storage shards without destroying locality during scale-out.

Next related chapters:

```text
031_Redis_Cluster_Routing.md
032_DB_Shard_Router_Implementation.md
033_Hot_Key_Detection.md
034_Shard_Migration_Playbook.md
```
