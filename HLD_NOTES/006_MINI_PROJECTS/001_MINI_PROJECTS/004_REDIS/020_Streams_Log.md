# MiniRedis Phase 20 — Streams Log (Rich Version)

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Redis Streams Exist](#2-why-redis-streams-exist)
- [3. Pub/Sub vs Streams](#3-pubsub-vs-streams)
- [4. Internal Data Structure Deep Dive](#4-internal-data-structure-deep-dive)
- [5. Architecture Mental Model](#5-architecture-mental-model)
- [6. Complete Java Code](#6-complete-java-code)
- [7. Step-by-Step Dry Run](#7-step-by-step-dry-run)
- [8. Internal Memory Visualization](#8-internal-memory-visualization)
- [9. Complexity Analysis](#9-complexity-analysis)
- [10. Real Production Use Cases](#10-real-production-use-cases)
- [11. Redis Production Internals](#11-redis-production-internals)
- [12. Failure Cases And Bottlenecks](#12-failure-cases-and-bottlenecks)
- [13. Interview Questions](#13-interview-questions)
- [14. Final Mental Model](#14-final-mental-model)

---

# 1. Goal

In this phase, we build:

```text
Redis Streams Log
```

Main objective:

```text
Store durable append-only events
that consumers can replay later.
```

Mental model:

```text
Mini Kafka inside Redis
```

Streams are NOT simple Pub/Sub.

Pub/Sub:

```text
live delivery only
```

Streams:

```text
durable event log
```

Real-world analogy:

```text
Bank transaction history.
```

Even if a user disconnects:

```text
transactions still exist
```

and can be replayed later.

Commands introduced:

```text
XADD
XREAD
```

Example:

```text
XADD orders order-created
XADD orders payment-success

XREAD orders
```

Result:

```text
all stored events returned
```

Production systems using this idea:

```text
Kafka
Pulsar
Redis Streams
Event sourcing
Audit logs
Payment events
Order pipelines
```

---

# 2. Why Redis Streams Exist

Earlier we built:

```text
Pub/Sub
```

Problem with Pub/Sub:

```text
messages disappear
```

Example:

```text
Publisher sends message
Subscriber offline
Message lost forever
```

That is acceptable for:

```text
live chat typing indicator
temporary notifications
real-time dashboards
```

But NOT acceptable for:

```text
payments
orders
banking
analytics
event processing
```

Example:

```text
payment-completed
```

If this message disappears:

```text
system inconsistency happens
```

Redis Streams solve this problem.

Instead of:

```text
deliver and forget
```

Streams do:

```text
append and retain
```

Mental model:

```text
append-only log
```

Every event gets:

```text
unique ordered ID
```

Example:

```text
1720001-0 order-created
1720002-0 payment-success
1720003-0 shipped
```

Consumers can later:

```text
resume reading from last ID
```

This becomes the foundation of:

```text
event-driven architecture
```

---

# 3. Pub/Sub vs Streams

# Pub/Sub

```text
fire and forget
```

Flow:

```text
publisher
   -> subscriber
```

If subscriber offline:

```text
message lost
```

Good for:

```text
live notifications
chat typing
cache invalidation
```

---

# Streams

```text
append and replay
```

Flow:

```text
producer
   -> stream log
       -> consumers read later
```

If consumer offline:

```text
message still exists
```

Good for:

```text
payments
orders
audit logs
analytics
microservice events
```

---

# 4. Internal Data Structure Deep Dive

MiniRedis implementation:

```java
Map<String, List<String>>
```

Meaning:

```text
stream name
   -> ordered list of events
```

Example:

```text
streams
 └── order-stream
      ├── 1720001-0 order-created
      ├── 1720002-0 payment-success
      └── 1720003-0 shipped
```

---

# Why List?

Because Streams require:

```text
append-only ordering
```

New events always go:

```text
to the end
```

Mental model:

```text
timeline
```

---

# Stream ID Structure

Each event has:

```text
timestamp-sequence
```

Example:

```text
1720001-0
1720001-1
1720002-0
```

Purpose:

```text
global ordering
```

Even if multiple events occur in same millisecond:

```text
sequence differentiates them
```

---

# Complexity

| Operation | Complexity |
|---|---|
| XADD | O(1) append |
| XREAD | O(n) scan |

---

# Why Streams Are Powerful

Because consumers can:

```text
resume from previous offset
```

Example:

```text
consumer crashed after reading:

1720002-0
```

Next read:

```text
resume from:
1720002-0
```

Exactly like Kafka offsets.

---

# Production Redis Internals

Real Redis Streams are MUCH more advanced.

Internally Redis uses:

```text
Radix Tree
ListPack
Consumer Groups
Pending Entry List
```

NOT simple ArrayList.

Why?

Because production systems need:

```text
millions of events
low memory
fast append
fast replay
consumer tracking
```

---

# 5. Architecture Mental Model

```text
Producer
   |
   v
XADD
   |
   v
Stream Log
   |
   +------------------+
   |                  |
   v                  v
Consumer A       Consumer B
```

Very important:

```text
Consumers are decoupled.
```

Producer does NOT care:

```text
who reads later
```

This is core event-driven architecture.

---

# 6. Complete Java Code

## 6.1 MessageBus.java

### Logic Before Code

This class simulates:

```text
Redis Streams append-only log
```

Core responsibilities:

```text
1. append events
2. generate IDs
3. replay events
```

Internal storage:

```java
Map<String, List<String>>
```

Meaning:

```text
stream name
   -> ordered events
```

```java
package com.miniredis.messaging;

import java.util.*;

/**
 * MessageBus simulates Redis Streams.
 *
 * Stream behavior:
 *
 * producer
 *    -> append event
 *    -> event stored permanently
 *    -> consumers can replay later
 */
public class MessageBus {

    /**
     * streams:
     *
     * key   -> stream name
     * value -> ordered list of events
     *
     * Example:
     *
     * order-stream
     *   -> [
     *        1720001-0 order-created,
     *        1720002-0 payment-success
     *      ]
     */
    private final Map<String, List<String>> streams =
            new HashMap<>();

    /**
     * XADD stream message
     *
     * Appends a new event into stream.
     */
    public String xadd(String stream, String message) {

        /**
         * Generate stream ID.
         *
         * Real Redis uses:
         * timestamp-sequence
         */
        String id =
                System.currentTimeMillis()
                        + "-"
                        + streams
                        .getOrDefault(stream, List.of())
                        .size();

        /**
         * Create stream if missing.
         */
        List<String> events =
                streams.computeIfAbsent(
                        stream,
                        s -> new ArrayList<>()
                );

        /**
         * Append event.
         */
        events.add(id + " " + message);

        /**
         * Return generated ID.
         */
        return id;
    }

    /**
     * XREAD stream
     *
     * Returns all stream events.
     */
    public List<String> xread(String stream) {

        /**
         * Return COPY to avoid exposing
         * internal mutable structure.
         */
        return new ArrayList<>(
                streams.getOrDefault(
                        stream,
                        List.of()
                )
        );
    }
}
```

---

## 6.2 Phase020Driver.java

### Logic Before Code

This driver simulates:

```text
producer appending events
consumer replaying events
```

```java
package com.miniredis.driver;

import com.miniredis.messaging.MessageBus;

public class Phase020Driver {

    public static void main(String[] args) {

        /**
         * Create stream engine.
         */
        MessageBus bus = new MessageBus();

        // --------------------------------
        // PRODUCER APPENDS EVENTS
        // --------------------------------

        String id1 =
                bus.xadd(
                        "order-stream",
                        "order-created"
                );

        String id2 =
                bus.xadd(
                        "order-stream",
                        "payment-success"
                );

        String id3 =
                bus.xadd(
                        "order-stream",
                        "order-shipped"
                );

        System.out.println(id1);
        System.out.println(id2);
        System.out.println(id3);

        // --------------------------------
        // CONSUMER REPLAYS EVENTS
        // --------------------------------

        System.out.println(
                bus.xread("order-stream")
        );
    }
}
```

---

# 7. Step-by-Step Dry Run

# Step 1

Code:

```java
bus.xadd(
    "order-stream",
    "order-created"
);
```

Meaning:

```text
append first event
```

Before memory:

```text
streams = {}
```

Execution:

```text
1. stream missing
2. create ArrayList
3. generate ID
4. append event
```

After memory:

```text
streams
 └── order-stream
      └── 1720001-0 order-created
```

---

# Step 2

Code:

```java
bus.xadd(
    "order-stream",
    "payment-success"
);
```

Execution:

```text
1. stream exists
2. generate next ID
3. append event
```

After memory:

```text
streams
 └── order-stream
      ├── 1720001-0 order-created
      └── 1720002-1 payment-success
```

---

# Step 3

Code:

```java
bus.xread("order-stream");
```

Execution:

```text
1. locate stream
2. copy all events
3. return ordered list
```

Result:

```text
[
  1720001-0 order-created,
  1720002-1 payment-success
]
```

---

# 8. Internal Memory Visualization

```text
MessageBus
 └── streams
      └── order-stream
           ├── 1720001-0 order-created
           ├── 1720002-1 payment-success
           └── 1720003-2 order-shipped
```

---

# 9. Complexity Analysis

| Operation | Complexity | Reason |
|---|---|---|
| XADD | O(1) | append to end |
| XREAD | O(n) | scan stream |
| Stream creation | O(1) | HashMap insert |

---

# 10. Real Production Use Cases

# Payments

```text
payment-created
payment-success
payment-failed
```

---

# Ecommerce

```text
order-created
inventory-updated
shipment-created
```

---

# Analytics

```text
page-view
button-click
purchase-event
```

---

# Audit Logging

```text
user-login
password-change
role-update
```

---

# 11. Redis Production Internals

Real Redis Streams support:

```text
Consumer Groups
Acknowledgements
Pending entries
Replay
Blocking reads
Distributed consumption
```

MiniRedis version:

```text
single JVM
single process
simple append-only list
```

Production Redis:

```text
highly optimized stream engine
```

---

# 12. Failure Cases And Bottlenecks

# Problem 1 — Infinite Stream Growth

Streams continuously grow.

Result:

```text
memory explosion
```

Production fix:

```text
stream trimming
TTL
compaction
```

---

# Problem 2 — Slow Consumers

Producer faster than consumers.

Result:

```text
backlog growth
```

Production fix:

```text
consumer groups
parallel processing
```

---

# Problem 3 — Consumer Crash

Consumer dies after partial processing.

Need:

```text
resume from offset
```

Exactly why stream IDs matter.

---

# 13. Interview Questions

# Q1

Why Streams instead of Pub/Sub?

Answer:

```text
Streams are durable and replayable.
Pub/Sub loses offline messages.
```

---

# Q2

Why append-only log architecture is powerful?

Answer:

```text
events become immutable history
```

Used in:

```text
Kafka
Event sourcing
CQRS
```

---

# Q3

Why IDs are ordered?

Answer:

```text
consumers need replay ordering
```

---

# Q4

How does Kafka relate to Redis Streams?

Answer:

```text
Both use append-only event logs.
```

Kafka:

```text
distributed durable log
```

Redis Streams:

```text
lighter in-memory event stream
```

---

# 14. Final Mental Model

```text
Pub/Sub
   -> live broadcast

Streams
   -> durable replayable history
```

Streams are the foundation of:

```text
event-driven systems
microservices
distributed workflows
async pipelines
real-time analytics
```
