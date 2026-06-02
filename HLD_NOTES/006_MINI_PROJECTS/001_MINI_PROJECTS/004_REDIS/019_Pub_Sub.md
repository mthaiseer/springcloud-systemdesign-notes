# MiniRedis Phase 19 — Pub/Sub + Stream Replay

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why Pub/Sub Exists](#2-why-pubsub-exists)
- [3. Previous Limitation](#3-previous-limitation)
- [4. What We Build In This Phase](#4-what-we-build-in-this-phase)
- [5. Pub/Sub vs Stream Mental Model](#5-pubsub-vs-stream-mental-model)
- [6. Architecture Diagram](#6-architecture-diagram)
- [7. Internal Data Structures](#7-internal-data-structures)
- [8. Complete Java Code With Deep Comments](#8-complete-java-code-with-deep-comments)
- [9. How To Run](#9-how-to-run)
- [10. Code-Level Dry Run](#10-code-level-dry-run)
- [11. Memory State Dry Run](#11-memory-state-dry-run)
- [12. Output Dry Run](#12-output-dry-run)
- [13. Test Commands](#13-test-commands)
- [14. DSA / CP Concepts Used](#14-dsa--cp-concepts-used)
- [15. System Design Relevance](#15-system-design-relevance)
- [16. Production Upgrade Path](#16-production-upgrade-path)
- [17. Failure Cases](#17-failure-cases)
- [18. Interview Notes](#18-interview-notes)
- [19. Next Step](#19-next-step)

---

# 1. Goal

In this phase, we move MiniRedis from:

```text
request-response storage
```

to:

```text
event delivery + message replay
```

Earlier phases answered questions like:

```text
GET user:1
HGET user:1 name
ZRANGE leaderboard
```

But real backend systems also need this pattern:

```text
Something happened.
Notify all interested clients immediately.
```

That is the job of Pub/Sub.

In this phase, we build two related ideas:

```text
1. Pub/Sub
   - volatile real-time fanout
   - messages are delivered only to currently subscribed clients

2. Stream
   - durable append-only message log
   - messages can be read later
```

Main commands simulated:

```text
SUBSCRIBE orders client-1
SUBSCRIBE orders client-2
PUBLISH orders order-created

XADD order-stream order-paid
XREAD order-stream
```

Real-world analogy:

```text
Pub/Sub is like a live announcement in a room.
Only people currently inside the room hear it.

Stream is like writing the announcement into a notebook.
Anyone can read it later.
```

Production examples:

```text
Pub/Sub:
- chat live delivery
- cache invalidation
- live notification fanout
- WebSocket event push

Streams:
- order event log
- payment event replay
- audit trail
- consumer group processing
- reliable background jobs
```

---

# 2. Why Pub/Sub Exists

A normal Redis key-value command is synchronous:

```text
Client asks
Server replies
```

Example:

```text
Client -> GET session:123
Server -> session data
```

But many systems are event-driven.

Example:

```text
Order service creates an order.
Notification service should know.
Analytics service should know.
Email service should know.
WebSocket gateway should know.
```

Without Pub/Sub, every service must keep asking:

```text
Any new orders?
Any new orders?
Any new orders?
```

This is polling.

Polling problems:

```text
wasted CPU
extra network calls
higher latency
delayed updates
poor scalability
```

Pub/Sub changes the model:

```text
Subscribers register interest once.
Publisher sends once.
Server fans out to all subscribers.
```

Mental model:

```text
channel -> list of interested clients
```

Example:

```text
orders -> [client-1, client-2, client-3]
```

When publisher sends:

```text
PUBLISH orders order-created
```

MiniRedis does:

```text
for every client subscribed to orders:
    deliver order-created
```

This is the Observer Pattern in system design.

---

# 3. Previous Limitation

Before this phase, MiniRedis could store and retrieve data.

It could do:

```text
SET key value
GET key
LPUSH jobs job-1
LPOP jobs
HSET user:1 name Mohamed
```

But it could not do this:

```text
Client A waits for messages.
Client B publishes a message.
Client A receives it automatically.
```

That means MiniRedis had no event fanout.

Previous model:

```text
Client pulls data from Redis.
```

New model:

```text
Redis pushes data to subscribed clients.
```

Why this matters:

```text
Modern backend systems are not only CRUD.
They are event-driven.
```

Examples:

```text
Order placed        -> publish order.created
Payment completed   -> publish payment.completed
User came online    -> publish user.online
Cache changed       -> publish cache.invalidate
Message sent        -> publish chat.message
```

---

# 4. What We Build In This Phase

We build one class:

```text
MessageBus
```

It has two responsibilities:

```text
1. Pub/Sub registry
2. Stream log storage
```

## 4.1 Pub/Sub Methods

```java
subscribe(channel, clientId)
publish(channel, message)
```

Meaning:

```text
subscribe:
add client to channel subscriber list

publish:
deliver message to every client subscribed to that channel
```

## 4.2 Stream Methods

```java
xadd(stream, message)
xread(stream)
```

Meaning:

```text
xadd:
append message to stream and return generated id

xread:
read all messages from stream
```

## 4.3 Important Learning Point

Pub/Sub and Stream are intentionally different.

```text
Pub/Sub does not store history.
Stream stores history.
```

So:

```text
If client is offline during Pub/Sub message:
message is lost.

If client is offline during Stream append:
message can be read later.
```

---

# 5. Pub/Sub vs Stream Mental Model

| Feature | Pub/Sub | Stream |
|---|---|---|
| Storage | No history | Stores history |
| Delivery | Live only | Replayable |
| Offline consumer | Misses message | Can read later |
| Use case | live notification | durable event log |
| Example | chat online push | order event history |
| MiniRedis DS | `Map<String, List<String>>` | `Map<String, List<String>>` |

Simple difference:

```text
Pub/Sub = shout now
Stream  = write and read later
```

Example:

```text
client-1 subscribes to orders
client-2 subscribes to orders
publisher publishes order-created
```

Pub/Sub result:

```text
client-1 receives order-created
client-2 receives order-created
```

But if client-3 subscribes later:

```text
client-3 does NOT receive old order-created message
```

Stream result:

```text
XADD order-stream order-paid
```

Later:

```text
XREAD order-stream
```

returns:

```text
order-paid
```

---

# 6. Architecture Diagram

```text
                 +-------------------+
                 | Publisher Client  |
                 +---------+---------+
                           |
                           | PUBLISH orders order-created
                           v
+-------------------+   +-----------------------+
| Subscriber Client |   | MessageBus            |
| client-1          |<--| channel -> clients    |
+-------------------+   +-----------+-----------+
                                   |
+-------------------+              |
| Subscriber Client |<-------------+
| client-2          |
+-------------------+
```

Stream side:

```text
Publisher
   |
   | XADD order-stream order-paid
   v
+-----------------------------+
| streams                     |
| order-stream                |
|   1000-0 order-paid         |
+-----------------------------+
   ^
   |
   | XREAD order-stream
Reader
```

Combined class:

```text
MessageBus
├── subscribers
│   └── channel -> list of clients
│
└── streams
    └── stream -> list of entries
```

---

# 7. Internal Data Structures

## 7.1 Subscriber Registry

```java
private final Map<String, List<String>> subscribers = new HashMap<>();
```

Meaning:

```text
channel name -> subscribers of that channel
```

Example:

```text
orders -> [client-1, client-2]
chat   -> [client-9]
```

Why `HashMap`?

```text
We need fast channel lookup.
```

Why `List`?

```text
A channel can have many subscribers.
We iterate over them during publish.
```

Complexity:

| Operation | Complexity | Why |
|---|---:|---|
| subscribe | O(1) average | HashMap lookup + append |
| publish | O(k) | k = subscribers on channel |

Important:

```text
Publish cost depends on subscriber count.
```

If one channel has 1 million subscribers:

```text
PUBLISH becomes expensive.
```

---

## 7.2 Stream Storage

```java
private final Map<String, List<String>> streams = new HashMap<>();
```

Meaning:

```text
stream name -> ordered event list
```

Example:

```text
order-stream
 ├── 1710000000000-0 order-created
 ├── 1710000000001-1 order-paid
 └── 1710000000002-2 order-shipped
```

Why `List`?

```text
Stream is append-only in this mini version.
New events go to the end.
Reads return entries in insertion order.
```

Complexity:

| Operation | Complexity | Why |
|---|---:|---|
| xadd | O(1) amortized | append to ArrayList |
| xread | O(n) | copy all entries |

---

# 8. Complete Java Code With Deep Comments

## 8.1 `MessageBus.java`

```java
package com.miniredis.messaging;

import java.util.*;

/**
 * MessageBus is the messaging layer of MiniRedis Phase 19.
 *
 * It demonstrates two important Redis concepts:
 *
 * 1. Pub/Sub
 *    - live fanout
 *    - no message history
 *    - if subscriber is offline, message is missed
 *
 * 2. Streams
 *    - append-only event log
 *    - messages are stored
 *    - readers can read later
 *
 * This class is intentionally simple.
 * It is not thread-safe yet.
 * Later phases can upgrade it with ConcurrentHashMap,
 * client sockets, blocking reads, acknowledgements, and consumer groups.
 */
public class MessageBus {

    /**
     * Pub/Sub subscriber registry.
     *
     * Key   = channel name
     * Value = list of client IDs subscribed to that channel
     *
     * Example memory:
     *
     * subscribers = {
     *   "orders" -> ["client-1", "client-2"],
     *   "chat"   -> ["client-9"]
     * }
     *
     * When PUBLISH orders order-created happens,
     * we find subscribers.get("orders") and deliver the message
     * to every client in that list.
     */
    private final Map<String, List<String>> subscribers = new HashMap<>();

    /**
     * Stream storage.
     *
     * Key   = stream name
     * Value = ordered list of stream entries
     *
     * Example memory:
     *
     * streams = {
     *   "order-stream" -> [
     *      "1710000000000-0 order-created",
     *      "1710000000001-1 order-paid"
     *   ]
     * }
     *
     * Unlike Pub/Sub, stream messages are stored.
     * That means they can be replayed later.
     */
    private final Map<String, List<String>> streams = new HashMap<>();

    /**
     * SUBSCRIBE channel clientId
     *
     * Adds a client to a channel subscriber list.
     *
     * Example:
     * subscribe("orders", "client-1")
     *
     * Before:
     * subscribers = {}
     *
     * After:
     * subscribers = {
     *   "orders" -> ["client-1"]
     * }
     */
    public void subscribe(String channel, String clientId) {

        // Step 1:
        // Find the list of subscribers for this channel.
        // If this channel does not exist yet, create a new ArrayList.
        List<String> clients = subscribers.computeIfAbsent(
                channel,
                c -> new ArrayList<>()
        );

        // Step 2:
        // Add this client to the subscriber list.
        // Now this client will receive future messages published to this channel.
        clients.add(clientId);
    }

    /**
     * PUBLISH channel message
     *
     * Sends message to every currently subscribed client.
     *
     * Example:
     * publish("orders", "order-created")
     *
     * If memory is:
     * subscribers = {
     *   "orders" -> ["client-1", "client-2"]
     * }
     *
     * Then output:
     * deliver to client-1: order-created
     * deliver to client-2: order-created
     */
    public void publish(String channel, String message) {

        // Step 1:
        // Get all clients currently subscribed to this channel.
        // If nobody subscribed, use an empty list.
        List<String> clients = subscribers.getOrDefault(channel, List.of());

        // Step 2:
        // Fanout message to every subscriber.
        // In real Redis, this would write to each client's socket.
        // In MiniRedis, we print delivery to console.
        for (String client : clients) {
            System.out.println("deliver to " + client + ": " + message);
        }
    }

    /**
     * XADD stream message
     *
     * Appends a message to a stream and returns a generated stream ID.
     *
     * Example:
     * xadd("order-stream", "order-paid")
     *
     * Generated ID format in this mini version:
     * currentTimeMillis + "-" + currentStreamSize
     *
     * Example ID:
     * 1710000000000-0
     *
     * Real Redis stream IDs also use a time-sequence format:
     * millisecondsTime-sequenceNumber
     */
    public String xadd(String stream, String message) {

        // Step 1:
        // Read current stream size.
        // If stream does not exist, current size is 0.
        int currentSize = streams.getOrDefault(stream, List.of()).size();

        // Step 2:
        // Generate a simple Redis-like stream ID.
        // Example: 1710000000000-0
        String id = System.currentTimeMillis() + "-" + currentSize;

        // Step 3:
        // Create stream list if missing.
        List<String> entries = streams.computeIfAbsent(
                stream,
                s -> new ArrayList<>()
        );

        // Step 4:
        // Store ID and message together.
        // This creates an append-only event log.
        entries.add(id + " " + message);

        // Step 5:
        // Return generated ID to caller.
        return id;
    }

    /**
     * XREAD stream
     *
     * Reads all messages stored in a stream.
     *
     * Example:
     * xread("order-stream")
     *
     * If memory is:
     * order-stream -> [
     *   "1710000000000-0 order-paid"
     * ]
     *
     * Return:
     * ["1710000000000-0 order-paid"]
     */
    public List<String> xread(String stream) {

        // Step 1:
        // Get stream entries.
        // If stream does not exist, use empty list.
        List<String> entries = streams.getOrDefault(stream, List.of());

        // Step 2:
        // Return a copy, not the original internal list.
        // This protects internal memory from accidental external modification.
        return new ArrayList<>(entries);
    }
}
```

---

## 8.2 `Phase019Driver.java`

```java
package com.miniredis.driver;

import com.miniredis.messaging.MessageBus;

/**
 * Phase019Driver demonstrates both:
 *
 * 1. Pub/Sub live fanout
 * 2. Stream durable replay
 */
public class Phase019Driver {

    public static void main(String[] args) {

        // Create one message bus instance.
        // This represents the Redis messaging engine in memory.
        MessageBus bus = new MessageBus();

        // -------------------------------
        // PUB/SUB DEMO
        // -------------------------------

        // client-1 subscribes to the "orders" channel.
        // Memory:
        // orders -> [client-1]
        bus.subscribe("orders", "client-1");

        // client-2 also subscribes to the same channel.
        // Memory:
        // orders -> [client-1, client-2]
        bus.subscribe("orders", "client-2");

        // Publish one message to orders.
        // Both client-1 and client-2 receive it.
        bus.publish("orders", "order-created");

        // -------------------------------
        // STREAM DEMO
        // -------------------------------

        // Add durable event to stream.
        // This event is stored in memory and can be read later.
        String id = bus.xadd("order-stream", "order-paid");

        // Print generated stream ID.
        System.out.println("stream id = " + id);

        // Read all stream entries.
        System.out.println("stream entries = " + bus.xread("order-stream"));
    }
}
```

---

# 9. How To Run

Compile:

```bash
javac -d out src/main/java/com/miniredis/**/*.java
```

Run:

```bash
java -cp out com.miniredis.driver.Phase019Driver
```

Expected style of output:

```text
deliver to client-1: order-created
deliver to client-2: order-created
stream id = 1710000000000-0
stream entries = [1710000000000-0 order-paid]
```

The exact timestamp changes every run.

---

# 10. Code-Level Dry Run

## Step 1

Code:

```java
MessageBus bus = new MessageBus();
```

Memory:

```text
subscribers = {}
streams     = {}
```

Meaning:

```text
No channel exists.
No stream exists.
```

---

## Step 2

Code:

```java
bus.subscribe("orders", "client-1");
```

Inside method:

```java
List<String> clients = subscribers.computeIfAbsent(
        channel,
        c -> new ArrayList<>()
);
```

Here:

```text
channel = orders
clientId = client-1
```

`orders` does not exist yet.

So Java creates:

```text
new ArrayList<>()
```

Then:

```java
clients.add(clientId);
```

Memory becomes:

```text
subscribers
└── orders -> [client-1]
```

---

## Step 3

Code:

```java
bus.subscribe("orders", "client-2");
```

This time `orders` already exists.

So `computeIfAbsent` does NOT create a new list.

It returns existing list:

```text
[client-1]
```

Then we append:

```text
client-2
```

Memory becomes:

```text
subscribers
└── orders -> [client-1, client-2]
```

---

## Step 4

Code:

```java
bus.publish("orders", "order-created");
```

Inside method:

```java
List<String> clients = subscribers.getOrDefault(channel, List.of());
```

Lookup:

```text
subscribers.get("orders")
```

Result:

```text
[client-1, client-2]
```

Then loop:

```java
for (String client : clients) {
    System.out.println("deliver to " + client + ": " + message);
}
```

Iteration 1:

```text
client = client-1
message = order-created
print deliver to client-1: order-created
```

Iteration 2:

```text
client = client-2
message = order-created
print deliver to client-2: order-created
```

Important:

```text
message is NOT stored in Pub/Sub.
```

---

## Step 5

Code:

```java
String id = bus.xadd("order-stream", "order-paid");
```

Inside method:

```java
int currentSize = streams.getOrDefault(stream, List.of()).size();
```

Before this call:

```text
streams = {}
```

So:

```text
currentSize = 0
```

Then:

```java
String id = System.currentTimeMillis() + "-" + currentSize;
```

Example:

```text
id = 1710000000000-0
```

Then:

```java
List<String> entries = streams.computeIfAbsent(
        stream,
        s -> new ArrayList<>()
);
```

`order-stream` does not exist.

So Java creates:

```text
new ArrayList<>()
```

Then:

```java
entries.add(id + " " + message);
```

Memory becomes:

```text
streams
└── order-stream
    └── 1710000000000-0 order-paid
```

Return:

```text
1710000000000-0
```

---

## Step 6

Code:

```java
bus.xread("order-stream")
```

Inside method:

```java
List<String> entries = streams.getOrDefault(stream, List.of());
```

Lookup:

```text
streams.get("order-stream")
```

Result:

```text
[1710000000000-0 order-paid]
```

Then:

```java
return new ArrayList<>(entries);
```

Why return copy?

Because if we returned original list:

```java
List<String> result = bus.xread("order-stream");
result.clear();
```

Then external code could accidentally delete internal stream data.

Returning copy protects internal memory.

---

# 11. Memory State Dry Run

## Initial

```text
MessageBus
├── subscribers = {}
└── streams     = {}
```

---

## After `subscribe("orders", "client-1")`

```text
MessageBus
├── subscribers
│   └── orders -> [client-1]
└── streams = {}
```

---

## After `subscribe("orders", "client-2")`

```text
MessageBus
├── subscribers
│   └── orders -> [client-1, client-2]
└── streams = {}
```

---

## After `publish("orders", "order-created")`

Memory is unchanged:

```text
MessageBus
├── subscribers
│   └── orders -> [client-1, client-2]
└── streams = {}
```

Why unchanged?

```text
Pub/Sub delivery is volatile.
It does not store message history.
```

---

## After `xadd("order-stream", "order-paid")`

```text
MessageBus
├── subscribers
│   └── orders -> [client-1, client-2]
│
└── streams
    └── order-stream
        └── 1710000000000-0 order-paid
```

---

## After `xread("order-stream")`

Memory is unchanged:

```text
MessageBus
├── subscribers
│   └── orders -> [client-1, client-2]
│
└── streams
    └── order-stream
        └── 1710000000000-0 order-paid
```

Why unchanged?

```text
Read does not delete stream entries.
```

---

# 12. Output Dry Run

Program output:

```text
deliver to client-1: order-created
deliver to client-2: order-created
stream id = 1710000000000-0
stream entries = [1710000000000-0 order-paid]
```

Timestamp changes every run.

Stable output pattern:

```text
deliver to client-1: order-created
deliver to client-2: order-created
stream id = <timestamp>-0
stream entries = [<timestamp>-0 order-paid]
```

---

# 13. Test Commands

Mental Redis commands:

```text
SUBSCRIBE orders client-1
SUBSCRIBE orders client-2
PUBLISH orders order-created
```

Expected Pub/Sub delivery:

```text
client-1 receives order-created
client-2 receives order-created
```

Stream commands:

```text
XADD order-stream order-paid
XREAD order-stream
```

Expected stream result:

```text
<id> order-paid
```

Important test:

```text
PUBLISH payments payment-created
```

If no subscriber exists:

```text
nothing is printed
message is lost
```

---

# 14. DSA / CP Concepts Used

| Concept | Where Used | Why |
|---|---|---|
| HashMap | channel lookup | O(1) average access |
| ArrayList | subscriber list | append and iterate |
| Observer Pattern | Pub/Sub | publisher does not know subscribers directly |
| Append-only list | Stream | preserve event order |
| Defensive copy | xread | protect internal state |

Complexity:

| Method | Complexity | Explanation |
|---|---:|---|
| subscribe | O(1) average | HashMap lookup + ArrayList append |
| publish | O(k) | k subscribers receive message |
| xadd | O(1) amortized | append to stream list |
| xread | O(n) | copies n stream entries |

Most important system design point:

```text
PUBLISH is not O(1).
It is O(number of subscribers on that channel).
```

---

# 15. System Design Relevance

## 15.1 Cache Invalidation

Example:

```text
User profile updated.
Publish cache.invalidate.user:1
```

Subscribers:

```text
API servers
edge cache
profile service
```

They clear stale cache.

---

## 15.2 Chat Delivery

Example:

```text
PUBLISH room:123 hello
```

Subscribers:

```text
all online users in room 123
```

Limitation:

```text
Offline users miss Pub/Sub message.
```

So real chat also stores messages in DB or stream.

---

## 15.3 Order Event Pipeline

Pub/Sub:

```text
notify live dashboards immediately
```

Stream:

```text
store order event for replay and retry
```

Good production design:

```text
Pub/Sub for live push
Stream/Kafka for durability
```

---

# 16. Production Upgrade Path

Current MiniRedis:

```text
HashMap<String, List<String>> subscribers
HashMap<String, List<String>> streams
console print delivery
single JVM
not thread-safe
```

Production Redis Pub/Sub:

```text
client socket registry
channel dictionary
pattern subscriptions
network writes
single-threaded event loop
```

Production Redis Streams:

```text
stream IDs
consumer groups
pending entries list
acknowledgement
range reads
blocking reads
memory trimming
```

Upgrade path for MiniRedis:

```text
Step 1: replace console print with client socket write
Step 2: add unsubscribe
Step 3: avoid duplicate subscriptions
Step 4: add thread-safe maps
Step 5: add stream offset reads
Step 6: add consumer groups
Step 7: add ACK / pending message tracking
Step 8: add persistence using AOF
```

---

# 17. Failure Cases

## 17.1 Duplicate Subscribe

Problem:

```java
subscribe("orders", "client-1");
subscribe("orders", "client-1");
```

Current memory:

```text
orders -> [client-1, client-1]
```

Then publish delivers twice.

Fix:

```text
Use Set instead of List for subscribers.
```

Better DS:

```java
Map<String, Set<String>> subscribers
```

---

## 17.2 Slow Subscriber

Problem:

```text
One client is slow.
Publish loop blocks delivery.
```

Production fix:

```text
non-blocking socket
client output buffer
backpressure
slow client disconnect policy
```

---

## 17.3 Message Loss

Problem:

```text
Subscriber offline during publish.
Message is lost.
```

Fix:

```text
Use Streams / Kafka / durable queue.
```

---

## 17.4 Stream Memory Growth

Problem:

```text
xadd forever
stream grows forever
memory full
```

Fix:

```text
stream trimming
TTL
max length policy
persistence + compaction
```

---

## 17.5 Concurrent Modification

Problem:

```text
One thread publishes while another subscribes.
ArrayList may change during iteration.
```

Fix options:

```text
ConcurrentHashMap
CopyOnWriteArrayList
synchronized block
single-threaded event loop like Redis
```

---

# 18. Interview Notes

## Core Explanation

Say:

```text
Pub/Sub keeps a channel-to-subscribers mapping.
When a publisher sends a message to a channel,
Redis looks up all subscribers and fans out the message.
It is live and volatile, so offline clients miss messages.
For durable replay, Redis Streams or Kafka is better.
```

## Common Questions

### Why is Pub/Sub not durable?

Because Pub/Sub is optimized for live delivery.
It does not write messages into a persistent log.

---

### What is publish complexity?

```text
O(k)
```

where:

```text
k = number of subscribers on that channel
```

---

### Pub/Sub vs Queue?

```text
Pub/Sub:
one message goes to all subscribers

Queue:
one message usually goes to one worker
```

---

### Pub/Sub vs Stream?

```text
Pub/Sub:
live, volatile, no replay

Stream:
durable, replayable, supports consumer tracking
```

---

### Why use Streams for payments/orders?

Because payments/orders need:

```text
replay
retry
audit
at-least-once processing
failure recovery
```

Pub/Sub alone is unsafe for this.

---

# 19. Next Step

Next phase:

```text
020
```

Recommended next topic:

```text
Redis Streams deeper implementation
```

Add:

```text
XADD
XRANGE
XREAD from offset
consumer group
ACK
pending entries list
```

This will connect MiniRedis to Kafka-style durable event processing.
