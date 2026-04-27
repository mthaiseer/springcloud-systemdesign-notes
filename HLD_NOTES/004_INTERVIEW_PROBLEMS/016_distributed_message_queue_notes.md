# Distributed Message Queue - Visual Notes (Java + Mermaid)

## 1. High-Level Architecture

```mermaid
flowchart LR
    P[Producer] -->|produce| B[Broker Cluster]
    B -->|consume| C[Consumer Group]
    B --> M[Metadata Store]
    B --> S[State Store]
    B --> Z[Coordination Service]
```

---

## 2. Topic, Partition, Broker

```mermaid
flowchart TB
    T[Topic A]
    T --> P1[Partition 1]
    T --> P2[Partition 2]

    P1 --> B1[Broker 1]
    P2 --> B2[Broker 2]
```

### Java Model

```java
class Message {
    String topic;
    int partition;
    long offset;
    byte[] key;
    byte[] value;
    long timestamp;
}
```

---

## 3. Producer Flow (Batching + Routing)

```mermaid
sequenceDiagram
    participant Producer
    participant Broker

    Producer->>Producer: buffer messages
    Producer->>Broker: send batch
    Broker-->>Producer: ACK
```

### Java Producer Example

```java
class Producer {
    List<Message> buffer = new ArrayList<>();

    void send(Message msg) {
        buffer.add(msg);
        if (buffer.size() >= 10) {
            flush();
        }
    }

    void flush() {
        // simulate sending batch
        System.out.println("Sending batch of size: " + buffer.size());
        buffer.clear();
    }
}
```

---

## 4. Consumer Flow (Pull Model)

```mermaid
sequenceDiagram
    participant Consumer
    participant Broker

    Consumer->>Broker: fetch(offset)
    Broker-->>Consumer: batch of messages
    Consumer->>Broker: commit(offset)
```

### Java Consumer Example

```java
class Consumer {
    long offset = 0;

    void poll(List<Message> partition) {
        for (Message m : partition) {
            if (m.offset >= offset) {
                process(m);
                offset = m.offset + 1;
            }
        }
    }

    void process(Message m) {
        System.out.println("Processing: " + m.offset);
    }
}
```

---

## 5. Consumer Groups

```mermaid
flowchart LR
    P1[Partition 1] --> C1[Consumer 1]
    P2[Partition 2] --> C2[Consumer 2]
```

Rule:
- One partition → one consumer per group

---

## 6. Storage (WAL + Segments)

```mermaid
flowchart TB
    Log --> Segment1
    Log --> Segment2
    Segment2 --> Active[Active Segment]
```

### Java Append Log

```java
class LogSegment {
    List<Message> messages = new ArrayList<>();

    void append(Message msg) {
        messages.add(msg);
    }
}
```

---

## 7. Replication

```mermaid
flowchart LR
    Leader --> F1[Follower 1]
    Leader --> F2[Follower 2]
```

### Concept
- Leader handles writes
- Followers replicate

---

## 8. Delivery Semantics

### At-least-once (most common)

```mermaid
flowchart LR
    Producer --> Broker --> Consumer
```

- Possible duplicates
- No data loss

---

## 9. ACK Modes

| Mode | Behavior |
|------|--------|
| ack=0 | no guarantee |
| ack=1 | leader only |
| ack=all | strongest guarantee |

---

## 10. Scaling

```mermaid
flowchart LR
    AddPartition --> MoreParallelism
    AddConsumer --> FasterProcessing
    AddBroker --> MoreStorage
```

---

## 11. Delayed Messages

```mermaid
flowchart LR
    Producer --> TempStorage
    TempStorage -->|after delay| Broker
```

### Java Idea

```java
class DelayedMessage {
    Message message;
    long deliverAt;
}
```

---

## 12. Key Takeaways

- Use partitions for scalability
- Use WAL for performance
- Use batching for throughput
- Use consumer groups for parallelism
- Trade latency vs durability

---

## 13. Mental Model

```mermaid
flowchart LR
    Producer --> Partition --> Log --> Consumer
```

---

End of Notes
