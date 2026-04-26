# 08 — Design a Unique ID Generator in Distributed Systems

## 1. Problem Statement

Design a distributed system that generates unique IDs for records such as users, orders, posts, payments, logs, or events.

### Required APIs

```java
long nextId();
```

### Requirements

| Requirement | Meaning |
|---|---|
| Unique | No two generated IDs should be the same. |
| Numeric only | ID should be a number, not a UUID string. |
| 64-bit | ID should fit inside signed `long`. |
| Time ordered | Newer IDs should usually be larger than older IDs. |
| High scale | Generate more than 10,000 IDs/second. |
| Distributed | Multiple machines should generate IDs independently. |
| Low latency | ID generation should not call a central database every time. |

---

## 2. Why Database `AUTO_INCREMENT` Is Not Enough

A single database auto-increment key is simple, but it does not work well at distributed scale.

```text
Client -> App Server -> Single DB AUTO_INCREMENT
```

Problems:

- Single database becomes bottleneck.
- Single point of failure.
- Multi-region ID generation becomes slow.
- Hard to keep IDs globally unique across many databases.

---

## 3. High-Level Options

### Option A: Multi-master Auto Increment

Each database generates IDs with a step size equal to number of databases.

```text
DB1: 1, 3, 5, 7, ...
DB2: 2, 4, 6, 8, ...
```

#### Diagram

```text
          +------------+
          | Web Servers|
          +------------+
             /      \
            /        \
+----------------+  +----------------+
| MySQL Master 1 |  | MySQL Master 2 |
| 1, 3, 5, ...   |  | 2, 4, 6, ...   |
+----------------+  +----------------+
```

#### Pros

- Numeric IDs.
- Easy for small systems.
- Uses database built-in feature.

#### Cons

- Hard to scale when adding/removing DBs.
- IDs are not globally time ordered.
- Multi-data-center setup is difficult.
- Database dependency adds latency.

---

### Option B: UUID

Example:

```text
09c93e62-50b4-468d-bf8a-c07e1040bfb2
```

#### Diagram

```text
+-------------+     +-------------+     +-------------+
| Web Server  |     | Web Server  |     | Web Server  |
| UUID Gen    |     | UUID Gen    |     | UUID Gen    |
+-------------+     +-------------+     +-------------+
       |                   |                   |
       v                   v                   v
 Independent ID generation on every machine
```

#### Pros

- No coordination needed.
- Very low collision probability.
- Easy to scale horizontally.

#### Cons

- Usually 128-bit, not 64-bit.
- Not numeric-only in common representation.
- Not naturally time sortable.
- Large index size in databases.

---

### Option C: Ticket Server

A centralized service generates IDs, often backed by a database sequence.

#### Diagram

```text
+------------+      +---------------+
| Web Server | ---> | Ticket Server |
+------------+      | AUTO_INCREMENT|
+------------+ ---> +---------------+
| Web Server |
+------------+
```

#### Pros

- Simple.
- Numeric IDs.
- Easy to reason about uniqueness.

#### Cons

- Ticket server can become bottleneck.
- Single point of failure unless replicated.
- Replication introduces synchronization complexity.

---

### Option D: Snowflake-style ID Generator

Use different bit sections inside a 64-bit integer.

```text
| sign | timestamp | datacenter | machine | sequence |
|  1   |    41     |     5      |    5    |    12    |
```

This is the best fit for the given requirements.

---

## 4. Snowflake ID Layout

A 64-bit ID is divided into sections:

```text
  0 | 41-bit timestamp | 5-bit datacenter | 5-bit machine | 12-bit sequence
----+------------------+------------------+---------------+----------------
sign| milliseconds     | data center id   | machine id    | per-ms counter
```

### Meaning of Each Field

| Field | Bits | Capacity |
|---|---:|---:|
| Sign bit | 1 | Always `0`, keeps ID positive. |
| Timestamp | 41 | About 69 years in milliseconds. |
| Datacenter ID | 5 | `2^5 = 32` datacenters. |
| Machine ID | 5 | `2^5 = 32` machines per datacenter. |
| Sequence | 12 | `2^12 = 4096` IDs per millisecond per machine. |

### Capacity

```text
32 datacenters * 32 machines * 4096 IDs/ms
= 4,194,304 IDs/ms globally
```

For the requirement of 10,000 IDs/second, this is more than enough.

---

## 5. Snowflake Visual Flow

```text
Request ID
   |
   v
+--------------------+
| Get current millis |
+--------------------+
   |
   v
+------------------------------+
| Same millis as last request? |
+------------------------------+
      | yes                         | no
      v                             v
+-------------------+       +----------------+
| sequence++        |       | sequence = 0   |
+-------------------+       +----------------+
      |                             |
      v                             v
+------------------------------+
| sequence overflow?           |
+------------------------------+
      | yes                         | no
      v                             v
+------------------------+    +----------------------+
| wait until next millis |    | compose 64-bit ID    |
+------------------------+    +----------------------+
                                      |
                                      v
                                Return ID
```

---

## 6. Bit Shift Formula

```text
ID = ((timestamp - customEpoch) << 22)
     | (datacenterId << 17)
     | (machineId << 12)
     | sequence
```

Why shift by 22?

```text
5 datacenter bits + 5 machine bits + 12 sequence bits = 22 bits
```

Detailed layout:

```text
timestamp part:   41 bits shifted left by 22
Datacenter part:   5 bits shifted left by 17
Machine part:      5 bits shifted left by 12
Sequence part:    12 bits stays at the end
```

---

## 7. Example ID Generation

Assume:

```text
customEpoch   = 1288834974657
currentMillis = 1586451091225
datacenterId  = 10
machineId     = 12
sequence      = 0
```

Then:

```text
relativeTimestamp = currentMillis - customEpoch
                  = 297616116568
```

Final ID is built by combining timestamp, datacenter, machine, and sequence bits.

---

## 8. Java Code — Simple Snowflake Generator

```java
public class SnowflakeIdGenerator {
    // Twitter Snowflake custom epoch: Nov 04 2010 01:42:54 UTC
    private static final long EPOCH = 1288834974657L;

    private static final long DATACENTER_BITS = 5L;
    private static final long MACHINE_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_DATACENTER_ID = (1L << DATACENTER_BITS) - 1; // 31
    private static final long MAX_MACHINE_ID = (1L << MACHINE_BITS) - 1;       // 31
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;        // 4095

    private static final long MACHINE_SHIFT = SEQUENCE_BITS;                  // 12
    private static final long DATACENTER_SHIFT = SEQUENCE_BITS + MACHINE_BITS; // 17
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_BITS + DATACENTER_BITS; // 22

    private final long datacenterId;
    private final long machineId;

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIdGenerator(long datacenterId, long machineId) {
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID) {
            throw new IllegalArgumentException("Invalid datacenterId: " + datacenterId);
        }
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException("Invalid machineId: " + machineId);
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long currentTimestamp = currentMillis();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException(
                "Clock moved backwards. Refusing to generate ID for "
                + (lastTimestamp - currentTimestamp) + " ms"
            );
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;

            // Sequence overflow in same millisecond.
            // Wait until next millisecond.
            if (sequence == 0) {
                currentTimestamp = waitUntilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_SHIFT)
                | (machineId << MACHINE_SHIFT)
                | sequence;
    }

    private long waitUntilNextMillis(long lastTimestamp) {
        long timestamp = currentMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = currentMillis();
        }
        return timestamp;
    }

    private long currentMillis() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(10, 12);

        for (int i = 0; i < 5; i++) {
            System.out.println(generator.nextId());
        }
    }
}
```

---

## 9. Java Code — Decode Snowflake ID

Useful for debugging and interviews.

```java
public class SnowflakeIdDecoder {
    private static final long EPOCH = 1288834974657L;

    private static final long SEQUENCE_MASK = (1L << 12) - 1;
    private static final long MACHINE_MASK = (1L << 5) - 1;
    private static final long DATACENTER_MASK = (1L << 5) - 1;

    public static void decode(long id) {
        long sequence = id & SEQUENCE_MASK;
        long machineId = (id >> 12) & MACHINE_MASK;
        long datacenterId = (id >> 17) & DATACENTER_MASK;
        long timestamp = (id >> 22) + EPOCH;

        System.out.println("timestamp    = " + timestamp);
        System.out.println("datacenterId = " + datacenterId);
        System.out.println("machineId    = " + machineId);
        System.out.println("sequence     = " + sequence);
    }

    public static void main(String[] args) {
        long id = 1234567890123456789L;
        decode(id);
    }
}
```

---

## 10. Java Code — Thread Test

```java
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class SnowflakeIdTest {
    public static void main(String[] args) throws InterruptedException {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
        Set<Long> ids = ConcurrentHashMap.newKeySet();

        int threads = 10;
        int idsPerThread = 10_000;
        CountDownLatch latch = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            new Thread(() -> {
                for (int i = 0; i < idsPerThread; i++) {
                    ids.add(generator.nextId());
                }
                latch.countDown();
            }).start();
        }

        latch.await();

        int expected = threads * idsPerThread;
        System.out.println("Expected: " + expected);
        System.out.println("Actual:   " + ids.size());
    }
}
```

---

## 11. How to Assign Datacenter and Machine IDs

Each generator must have a unique pair:

```text
(datacenterId, machineId)
```

Example:

```text
DC 0: machine 0, machine 1, machine 2, ... machine 31
DC 1: machine 0, machine 1, machine 2, ... machine 31
```

### Common Assignment Methods

| Method | Description |
|---|---|
| Config file | Each machine starts with fixed config. Simple but manual. |
| Environment variables | Useful in containers and Kubernetes. |
| ZooKeeper/etcd | Dynamically assign machine IDs with leases. |
| Kubernetes StatefulSet ordinal | Pod ordinal can become machine ID. |
| Cloud metadata | Use region/zone/instance metadata. |

### Important

Never allow two live generators to use the same `(datacenterId, machineId)` pair, or they can create duplicate IDs in the same millisecond with the same sequence.

---

## 12. Clock Rollback Problem

Snowflake depends on system time. If the machine clock moves backward, duplicate or out-of-order IDs can happen.

```text
Time: 1000 ms -> generate ID A
Time: 1001 ms -> generate ID B
Clock rollback to 1000 ms -> may generate duplicate-like timestamp range
```

### Ways to Handle Clock Rollback

| Strategy | Explanation |
|---|---|
| Throw error | Refuse to generate IDs until clock catches up. Simple and safe. |
| Wait | Sleep until current time is greater than last timestamp. |
| Use NTP | Keep clocks synchronized. |
| Use logical clock | If clock moves backward slightly, keep using last timestamp. |
| Dedicated ID service | Centralize ID generation per region and monitor it carefully. |

Interview-safe answer:

> If the clock moves backward, the generator should either wait until the clock catches up or fail fast. For mission-critical systems, use NTP, monitoring, and unique machine IDs with leases.

---

## 13. Deployment Architecture

### Embedded ID Generator

Each app server has its own ID generator.

```text
+------------------+    +------------------+    +------------------+
| App Server       |    | App Server       |    | App Server       |
| Snowflake Gen    |    | Snowflake Gen    |    | Snowflake Gen    |
| DC=1, Machine=1  |    | DC=1, Machine=2  |    | DC=1, Machine=3  |
+------------------+    +------------------+    +------------------+
```

Pros:

- Very low latency.
- No network call.
- Easy to scale.

Cons:

- Machine ID assignment must be carefully managed.
- Clock rollback must be handled on every server.

---

### Dedicated ID Generator Service

```text
+------------+       +---------------------+
| App Server | ----> | ID Generator Service|
+------------+       | Snowflake Cluster   |
+------------+       +---------------------+
| App Server | ---->       |     |     |
+------------+             v     v     v
                       Gen-1 Gen-2 Gen-3
```

Pros:

- Centralized monitoring.
- Easier to control machine IDs.
- Easier to upgrade algorithm.

Cons:

- Adds network latency.
- Service must be highly available.
- Can become dependency bottleneck.

---

## 14. FAANG Interview Talking Points

### Why Snowflake?

Snowflake satisfies the requirements because:

- IDs are unique.
- IDs are numeric.
- IDs fit into 64-bit.
- IDs are roughly time sortable.
- IDs can be generated independently on many machines.
- No central database call is required.

### What Makes ID Unique?

```text
timestamp + datacenterId + machineId + sequence
```

Two machines can generate IDs at the same timestamp, but their `datacenterId` or `machineId` differs.

One machine can generate multiple IDs in the same millisecond, but `sequence` differs.

### What Happens If Sequence Overflows?

If one machine generates more than 4096 IDs in the same millisecond:

```text
Wait until next millisecond, then reset sequence to 0.
```

### Are IDs Strictly Increasing Globally?

Not always. They are roughly increasing by time, but across machines, small ordering differences can happen due to clock skew.

Better wording:

> Snowflake IDs are k-sortable: mostly ordered by time, but not guaranteed to be perfectly globally sequential.

---

## 15. Comparison Table

| Approach | Unique | 64-bit | Numeric | Time ordered | Distributed | Main issue |
|---|---|---|---|---|---|---|
| DB auto increment | Yes | Yes | Yes | Yes on one DB | Poor | Bottleneck/SPOF |
| Multi-master increment | Yes if configured | Yes | Yes | No | Medium | Hard to scale/change DB count |
| UUID | Practically yes | No | Not always | No | Excellent | 128-bit and large indexes |
| Ticket server | Yes | Yes | Yes | Usually | Medium | SPOF/bottleneck |
| Snowflake | Yes | Yes | Yes | Mostly yes | Excellent | Clock and machine ID management |

---

## 16. Common Edge Cases

### Duplicate Machine ID

Problem:

```text
Machine A: DC=1, Machine=5
Machine B: DC=1, Machine=5
```

Both can generate duplicate IDs.

Solution:

- Use unique machine ID assignment.
- Use leases with ZooKeeper/etcd.
- Monitor duplicate assignment.

---

### Clock Moves Backward

Problem:

```text
lastTimestamp = 1000
currentTimestamp = 990
```

Solution:

- Wait until clock catches up.
- Fail fast for large rollback.
- Use NTP and monitoring.

---

### Timestamp Overflow

41 bits gives about 69 years.

Solution:

- Choose a recent custom epoch.
- Migrate before overflow.
- Tune bit allocation if needed.

---

### Hot Machine

One machine may generate too many IDs and hit sequence overflow often.

Solution:

- Add more machines.
- Load balance better.
- Increase sequence bits if timestamp lifetime can be reduced.

---

## 17. Bit Allocation Tuning

Default layout:

```text
41 timestamp | 5 datacenter | 5 machine | 12 sequence
```

Can tune based on business need:

| Use case | Better allocation |
|---|---|
| More machines | Increase datacenter/machine bits. |
| More IDs per machine | Increase sequence bits. |
| Longer lifetime | Increase timestamp bits. |
| Fewer regions | Reduce datacenter bits. |

Example high-throughput per machine layout:

```text
40 timestamp | 4 datacenter | 6 machine | 13 sequence
```

This gives:

```text
8192 IDs/ms/machine
```

But timestamp lifetime becomes shorter.

---

## 18. Final Interview Summary

A distributed unique ID generator can be implemented using a Snowflake-style 64-bit ID.

The ID contains:

```text
timestamp + datacenter ID + machine ID + sequence number
```

This design avoids a central database bottleneck, supports high throughput, generates numeric 64-bit IDs, and provides approximate time ordering.

Main production concerns are:

- clock synchronization,
- unique machine ID assignment,
- sequence overflow handling,
- high availability,
- observability and alerting.

---

## 19. Quick Revision

```text
Need 64-bit numeric ordered IDs?
Use Snowflake.

Why unique?
Timestamp + datacenter + machine + sequence.

Why scalable?
Every machine generates IDs independently.

Biggest risk?
Clock rollback and duplicate machine IDs.

What if 4096 IDs/ms exceeded?
Wait for next millisecond.
```
