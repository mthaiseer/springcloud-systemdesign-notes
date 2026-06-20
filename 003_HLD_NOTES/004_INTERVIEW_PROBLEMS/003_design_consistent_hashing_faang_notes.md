# 06 — Design Consistent Hashing

## 1. Problem

When we horizontally scale caches, databases, or request routers, we need to decide which server owns a key.

A simple approach is:

```text
serverIndex = hash(key) % N
```

where `N` is the number of servers.

This works only when the server pool is fixed. If a server is added or removed, `N` changes, so most keys are remapped.

---

## 2. Rehashing Problem

### Before: 4 servers

```text
serverIndex = hash(key) % 4

+--------+--------+--------+--------+
| S0     | S1     | S2     | S3     |
+--------+--------+--------+--------+
| key1   | key0   | key2   | key5   |
| key3   | key4   | key6   | key7   |
+--------+--------+--------+--------+
```

### After: 1 server removed, now 3 servers

```text
serverIndex = hash(key) % 3

+--------+--------+--------+
| S0     | S1     | S2     |
+--------+--------+--------+
| key0   | key2   | key3   |
| key1   | key4   |        |
| key5   | key6   |        |
| key7   |        |        |
+--------+--------+--------+
```

### Issue

Most keys move, not only the keys from the failed server.

This causes:

- cache miss storm
- database pressure
- high latency
- uneven traffic during recovery

---

## 3. What Consistent Hashing Solves

Consistent hashing reduces remapping.

When a server is added or removed, only a small fraction of keys move.

```text
Traditional hashing:
Server change -> almost all keys move

Consistent hashing:
Server change -> only nearby keys move
```

Interview phrase:

> Consistent hashing maps both servers and keys onto the same hash ring. A key is assigned to the first server found while moving clockwise on the ring.

---

## 4. Hash Space and Hash Ring

A hash function maps values into a fixed range.

Example:

```text
0 ------------------------------ 2^160 - 1
```

If we connect both ends, we get a ring.

```text
                0
                |
        +---------------+
      /                   \
     |                     |
     |      HASH RING      |
     |                     |
      \                   /
        +---------------+
                |
             2^160 - 1
```

---

## 5. Mapping Servers to the Ring

Servers are hashed by server ID, IP, hostname, or stable identifier.

```text
              S0
              |
       S3 ----+---- S1
              |
              S2

S0 = hash("server-0")
S1 = hash("server-1")
S2 = hash("server-2")
S3 = hash("server-3")
```

---

## 6. Mapping Keys to Servers

Keys are also hashed onto the same ring.

Rule:

```text
For a key, move clockwise until the first server is found.
```

```text
                  S0
              k0  |
                  |
        S3 -------+------- S1
             k3   |   k1
                  |
                  S2
                  k2
```

Example mapping:

```text
k0 -> S0
k1 -> S1
k2 -> S2
k3 -> S3
```

---

## 7. Adding a Server

Suppose `S4` is added between `k0` and `S0`.

```text
Before:

k0 --------clockwise--------> S0

After:

k0 ----> S4 ----> S0
```

Only keys between previous server and new server move to the new server.

```text
Affected range:
previous server ----> new server
```

Visual:

```text
                  S0
              k0  |
                S4|
        S3 -------+------- S1
                  |
                  S2
```

Only `k0` moves from `S0` to `S4`.

---

## 8. Removing a Server

If `S1` is removed, keys owned by `S1` move to the next clockwise server.

```text
Before:

k1 ----> S1

After S1 removed:

k1 ----> S2
```

Visual:

```text
                  S0
                  |
        S3 -------+------- X S1 removed
                  |   k1 moves clockwise
                  v
                  S2
```

Only keys in `S1`'s partition move.

---

## 9. Problems with Basic Consistent Hashing

### Problem 1: Uneven partitions

Servers may not be evenly spaced on the ring.

```text
S0 ---- small ---- S1 -------- huge -------- S2 -- small -- S3
```

One server may own a much larger hash range than others.

### Problem 2: Uneven key distribution

Even if servers are balanced, keys may cluster in one region.

```text
S0 ---- k1 k2 k3 k4 k5 ---- S1 ---- S2 ---- S3
```

This creates hotspots.

---

## 10. Virtual Nodes

Virtual nodes solve uneven distribution.

Instead of placing each physical server once on the ring, place it many times.

```text
Server A -> A#0, A#1, A#2, A#3 ...
Server B -> B#0, B#1, B#2, B#3 ...
```

Visual:

```text
                 A#0
                  |
        B#2 ------+------ A#1
       /                   \
    A#2                     B#0
       \                   /
        B#1 ------+------ A#3
                  |
                 B#3
```

Each virtual node points back to a real server.

```text
A#0, A#1, A#2, A#3 -> Server A
B#0, B#1, B#2, B#3 -> Server B
```

Benefits:

- smoother key distribution
- easier server addition/removal
- supports weighted capacity
- reduces hotspot risk

---

## 11. Weighted Consistent Hashing

If servers have different capacity, give stronger servers more virtual nodes.

```text
Small server  -> 100 virtual nodes
Large server  -> 300 virtual nodes
```

This makes the large server own more partitions.

---

## 12. Affected Key Range

### Adding a server

When adding `S4`:

```text
Affected keys are between:
previous clockwise predecessor -> S4
```

```text
S3 ---- affected range ---- S4 ---- S0
```

Keys in that range move to `S4`.

### Removing a server

When removing `S1`:

```text
Affected keys are between:
previous server -> removed server
```

```text
S0 ---- affected range ---- S1 ---- S2
```

Those keys move to `S2`.

---

## 13. High-Level Architecture

Consistent hashing is commonly used in distributed caches and databases.

```text
             +---------+
Request ---> | Router  |
             +----+----+
                  |
                  v
          hash(key) on ring
                  |
       +----------+----------+
       |          |          |
       v          v          v
     Cache A    Cache B    Cache C
```

With virtual nodes:

```text
             +---------+
Request ---> | Router  |
             +----+----+
                  |
                  v
        Consistent Hash Ring
                  |
     +------------+------------+
     |            |            |
   A#0,A#1      B#0,B#1      C#0,C#1
     |            |            |
   Server A     Server B     Server C
```

---

## 14. Java Reference Code — Simple Consistent Hash Ring

This is a compact Java implementation using virtual nodes.

```java
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ConsistentHashRing {
    private final int virtualNodes;
    private final SortedMap<Long, String> ring = new TreeMap<>();

    public ConsistentHashRing(int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    public void addServer(String serverId) {
        for (int i = 0; i < virtualNodes; i++) {
            String virtualNodeKey = serverId + "#" + i;
            ring.put(hash(virtualNodeKey), serverId);
        }
    }

    public void removeServer(String serverId) {
        for (int i = 0; i < virtualNodes; i++) {
            String virtualNodeKey = serverId + "#" + i;
            ring.remove(hash(virtualNodeKey));
        }
    }

    public String getServer(String key) {
        if (ring.isEmpty()) {
            throw new IllegalStateException("No servers available");
        }

        long keyHash = hash(key);

        // Find first virtual node clockwise from keyHash.
        SortedMap<Long, String> tailMap = ring.tailMap(keyHash);
        Long nodeHash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();

        return ring.get(nodeHash);
    }

    private long hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));

            // Convert first 8 bytes to positive long.
            long hash = 0;
            for (int i = 0; i < 8; i++) {
                hash = (hash << 8) | (digest[i] & 0xff);
            }
            return hash & Long.MAX_VALUE;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm unavailable", e);
        }
    }

    public static void main(String[] args) {
        ConsistentHashRing hashRing = new ConsistentHashRing(100);

        hashRing.addServer("server-A");
        hashRing.addServer("server-B");
        hashRing.addServer("server-C");

        List<String> keys = List.of("user:1", "user:2", "photo:9", "order:99");

        System.out.println("Before adding server-D:");
        for (String key : keys) {
            System.out.println(key + " -> " + hashRing.getServer(key));
        }

        hashRing.addServer("server-D");

        System.out.println("\nAfter adding server-D:");
        for (String key : keys) {
            System.out.println(key + " -> " + hashRing.getServer(key));
        }
    }
}
```

---

## 15. Java Reference Code — Simpler Version Without Virtual Nodes

Use this version only to understand the base concept.

```java
import java.util.*;

public class SimpleHashRing {
    private final TreeMap<Integer, String> ring = new TreeMap<>();

    public void addServer(String server) {
        ring.put(hash(server), server);
    }

    public void removeServer(String server) {
        ring.remove(hash(server));
    }

    public String getServer(String key) {
        if (ring.isEmpty()) {
            throw new IllegalStateException("No servers available");
        }

        int keyHash = hash(key);
        Map.Entry<Integer, String> entry = ring.ceilingEntry(keyHash);

        if (entry == null) {
            entry = ring.firstEntry(); // wrap around
        }

        return entry.getValue();
    }

    private int hash(String value) {
        return Math.abs(value.hashCode());
    }
}
```

Note: This is easier to read, but not production quality. Java's default `hashCode()` is not ideal for a distributed hash ring.

---

## 16. Consistent Hashing vs Modulo Hashing

| Topic | Modulo Hashing | Consistent Hashing |
|---|---|---|
| Formula | `hash(key) % N` | first server clockwise on ring |
| Add server | most keys move | small fraction moves |
| Remove server | most keys move | small fraction moves |
| Cache stability | poor | good |
| Complexity | simple | moderate |
| Production use | limited | common |

---

## 17. Common Use Cases

Consistent hashing is useful in:

- distributed cache systems
- database sharding
- key-value stores
- CDN request routing
- load balancing
- distributed queues
- object storage
- chat/message partitioning

Real-world systems using similar ideas:

- Amazon Dynamo-style partitioning
- Apache Cassandra partitioning
- distributed caches like Memcached clients
- CDN routing systems
- load balancers

---

## 18. FAANG Interview Talking Points

### Start with the problem

> If we use `hash(key) % N`, changing `N` remaps most keys. This is bad for caches because it causes massive cache misses.

### Then propose consistent hashing

> I will place both keys and servers on a hash ring. For each key, I move clockwise and assign it to the first server.

### Then discuss server changes

> Adding or removing a server only affects keys in the neighboring partition, instead of remapping the whole keyspace.

### Then mention virtual nodes

> Basic consistent hashing can create uneven partitions, so I would use virtual nodes. Each physical server appears multiple times on the ring.

### Then discuss production concerns

Mention:

- virtual nodes
- weighted nodes
- replication factor
- failure detection
- data migration
- hot keys
- monitoring
- rebalance throttling
- stable hash function

---

## 19. Production Design Notes

### Stable hash function

Use a stable hash function such as:

- MurmurHash
- xxHash
- SHA-1
- MD5

Avoid language-specific unstable hashing if different services must agree on placement.

### Replication

For reliability, store each key on multiple clockwise servers.

```text
Primary   -> first server clockwise
Replica 1 -> next server clockwise
Replica 2 -> next server clockwise
```

Visual:

```text
keyX ---> S1 primary ---> S2 replica ---> S3 replica
```

### Hot key mitigation

If one key receives too much traffic:

- cache it locally
- split it into subkeys
- replicate hot keys
- use request coalescing
- apply rate limiting

### Rebalancing

When adding/removing servers:

- move only affected partitions
- throttle migration
- monitor latency and error rate
- avoid moving too much data at once

---

## 20. Example Interview Answer

> I would use consistent hashing to distribute keys across servers. Instead of using `hash(key) % N`, I map servers and keys to a hash ring. To find the owner of a key, I move clockwise until I find the first server. This minimizes data movement when servers are added or removed. To improve balance, I use virtual nodes so each physical server appears many times on the ring. In production, I would also support replication, weighted nodes, health checks, and controlled rebalancing.

---

## 21. Quick Revision

```text
Why not hash % N?
Because changing N remaps most keys.

What is consistent hashing?
Map keys and servers to a ring; key goes to first server clockwise.

What happens when adding a server?
Only keys in the new server's range move.

What happens when removing a server?
Only keys owned by removed server move to next clockwise server.

Why virtual nodes?
Better distribution and less skew.

How to handle bigger servers?
Give them more virtual nodes.

How to improve availability?
Store replicas on next clockwise servers.
```

---

## 22. Final Mental Model

```text
                 keyA
                  |
                  v
          +---------------+
       S3 |               | S0
          |   HASH RING   |
       S2 |               | S1
          +---------------+

Rule:
key -> walk clockwise -> first server owns it
```

Consistent hashing is mainly about minimizing movement while keeping distribution reasonably balanced.
