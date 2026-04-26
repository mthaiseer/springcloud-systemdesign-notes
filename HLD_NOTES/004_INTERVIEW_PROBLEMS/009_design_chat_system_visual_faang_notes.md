# 13 — Design a Chat System

> Goal: design a scalable chat system like WhatsApp/Facebook Messenger that supports 1-on-1 chat, small group chat, online presence, multiple devices, and push notifications.

---

# Step 0 — What is a Chat System?

A chat system lets users exchange messages in real time.

```text
Sender -> Chat Service -> Receiver
```

Basic responsibilities:

```text
1. Receive messages from clients.
2. Store messages durably.
3. Deliver messages to online recipients.
4. Hold messages for offline recipients.
5. Sync messages across multiple devices.
6. Track online/offline presence.
```

---

# Step 1 — Clarify Requirements

## Functional Requirements

- Support 1-on-1 chat.
- Support small group chat.
- Group size limit: 100 users.
- Support text messages only.
- Message length limit: 100,000 characters.
- Support online/offline indicator.
- Support multiple devices per user.
- Support push notifications for offline users.
- Store chat history forever.

## Non-functional Requirements

- Low delivery latency.
- High availability.
- Scalable to 50M DAU.
- Durable message storage.
- Ordered messages within a conversation.
- Eventually consistent across devices.
- Fault tolerant.

## Out of Scope

- End-to-end encryption.
- Voice/video calls.
- Large public channels.
- Media attachments.

Interview line:

> I will optimize for low-latency delivery while ensuring messages are durably stored.

---

# Step 2 — Communication Protocol Choice

## HTTP

Good for:

```text
login
signup
profile
group management
message history fetch
```

Not ideal for receiving real-time messages.

---

## Polling

Client repeatedly asks server:

```text
Client -> Server: Any new messages?
Server -> Client: No
Client -> Server: Any new messages?
Server -> Client: No
```

Problem:

```text
Wastes server resources.
Most responses may be empty.
```

---

## Long Polling

Client keeps request open until:

```text
1. New message arrives
2. Timeout happens
```

Problems:

- server may not know if client disconnected,
- clients may connect to different servers,
- still inefficient for low-activity users.

---

## WebSocket

Best choice for real-time chat.

```text
Client ---- HTTP handshake ----> Server
Client <--- upgrade accepted --- Server
Client <==== bidirectional ====> Server
```

Benefits:

- persistent connection,
- bidirectional,
- low latency,
- works over port 80/443,
- good for real-time messaging.

Interview answer:

> I would use WebSocket for real-time messaging and HTTP for normal request/response APIs.

---

# Step 3 — High-Level Architecture

```text
                           +----------------+
                           |    Clients     |
                           | mobile / web   |
                           +--------+-------+
                                    |
                  +-----------------+-----------------+
                  |                                   |
                  v                                   v
          +---------------+                   +---------------+
          | API Servers   |                   | Chat Servers  |
          | HTTP          |                   | WebSocket     |
          +-------+-------+                   +-------+-------+
                  |                                   |
                  v                                   v
       +-------------------+              +----------------------+
       | User/Profile DB   |              | Message Sync Queue   |
       +-------------------+              +----------+-----------+
                                                     |
                  +----------------------------------+----------------+
                  |                                                   |
                  v                                                   v
          +---------------+                                  +----------------+
          | KV Store      |                                  | Presence       |
          | Chat History  |                                  | Servers        |
          +---------------+                                  +-------+--------+
                                                                     |
                                                                     v
                                                            +----------------+
                                                            | Push Notif     |
                                                            | Servers        |
                                                            +----------------+
```

Main components:

```text
API Servers        -> login, signup, profile, group management
Chat Servers       -> WebSocket connections, send/receive messages
Presence Servers   -> online/offline status
Notification Server-> push notifications
KV Store           -> chat history
Message Queue      -> async fanout and storage pipeline
Service Discovery  -> choose best chat server
```

---

# Step 4 — Stateless vs Stateful Services

## Stateless Services

Use HTTP.

Examples:

```text
Authentication
User profile
Group management
Friend list
Message history fetch
```

These can sit behind a normal load balancer.

```text
Client -> Load Balancer -> API Server
```

---

## Stateful Services

Chat servers are stateful because they maintain WebSocket connections.

```text
User A <---- WebSocket ----> Chat Server 1
User B <---- WebSocket ----> Chat Server 2
```

A user usually stays connected to the same chat server until disconnect.

---

# Step 5 — Service Discovery

Clients need to connect to the best chat server.

Criteria:

```text
geographic location
server capacity
connection count
server health
latency
```

Flow:

```text
1. User logs in through API server.
2. API server authenticates user.
3. Service discovery chooses best chat server.
4. Client opens WebSocket to that chat server.
```

Visual:

```text
User
 |
 v
Load Balancer
 |
 v
API Server
 |
 v
Service Discovery
 |
 v
Best Chat Server Info
 |
 v
User opens WebSocket
```

Example:

```text
Service discovery returns:
wss://chat-server-12.example.com/ws
```

---

# Step 6 — Data Storage Choice

Chat systems store two categories of data.

## Generic Data

```text
user profile
settings
friend list
group membership
```

Good fit:

```text
Relational DB
```

---

## Message Data

Characteristics:

```text
huge write volume
recent messages read often
old messages rarely read
need low latency
need horizontal scaling
```

Good fit:

```text
Key-value store / wide-column store
Examples: Cassandra, HBase, DynamoDB
```

Interview line:

> I would use a key-value or wide-column store for chat history because it scales horizontally and supports fast reads/writes.

---

# Step 7 — Message Data Model

## 1-on-1 Message Table

```text
message

+------------+--------------+------------+---------+------------+
| message_id | message_from | message_to | content | created_at |
+------------+--------------+------------+---------+------------+
```

Suggested fields:

```sql
message_id BIGINT PRIMARY KEY
conversation_id BIGINT
message_from BIGINT
message_to BIGINT
content TEXT
created_at TIMESTAMP
```

---

## Conversation ID

For 1-on-1 chat:

```text
conversation_id = hash(min(userA, userB), max(userA, userB))
```

This ensures both users map to the same conversation.

---

## Group Message Table

```text
group_message

+------------+------------+---------+---------+------------+
| channel_id | message_id | user_id | content | created_at |
+------------+------------+---------+---------+------------+
```

Primary key:

```text
(channel_id, message_id)
```

Why?

```text
All messages in one group are queried by channel_id.
message_id gives ordering.
```

---

# Step 8 — Message ID Generation

Message ID must be:

```text
unique
sortable by time
monotonically increasing inside a conversation/channel
```

Options:

| Approach | Pros | Cons |
|---|---|---|
| DB auto-increment | Simple | Not good for distributed NoSQL |
| Snowflake ID | Global, sortable | Clock sync issue |
| Local sequence per conversation | Simple ordering per chat | Need per-channel counter |

Recommended:

```text
Use Snowflake-style 64-bit ID or local per-channel sequence.
```

Interview line:

> Ordering is only required within a conversation, so local sequence per conversation can be enough.

---

# Step 9 — 1-on-1 Message Flow

```text
User A
  |
  | WebSocket message
  v
Chat Server 1
  |
  | generate message_id
  v
Message Sync Queue
  |
  +--> Store in KV Store
  |
  +--> If User B online: deliver to User B's chat server
  |
  +--> If User B offline: send push notification
```

Visual:

```text
+--------+       +---------------+       +-------------------+
| User A | ----> | Chat Server 1 | ----> | Message Sync Queue|
+--------+       +---------------+       +---------+---------+
                                                   |
                         +-------------------------+----------------------+
                         |                                                |
                         v                                                v
                 +---------------+                              +----------------+
                 | KV Store      |                              | Chat Server 2  |
                 | Chat History  |                              | User B online  |
                 +---------------+                              +--------+-------+
                                                                          |
                                                                          v
                                                                      +--------+
                                                                      | User B |
                                                                      +--------+
```

If offline:

```text
Message Sync Queue -> Push Notification Server -> APNs/FCM -> User B
```

---

# Step 10 — Message Acknowledgement

For reliable UX, use acknowledgements.

```text
Client sends message
        |
        v
Server stores message
        |
        v
Server sends ACK to sender
        |
        v
Server delivers to receiver
        |
        v
Receiver sends delivery ACK
```

Status:

```text
SENDING
SENT
DELIVERED
READ
FAILED
```

Visual:

```text
User A -> Chat Server: message
User A <- Chat Server: sent ack
User B <- Chat Server: message
User B -> Chat Server: delivered ack
User A <- Chat Server: delivered/read receipt
```

---

# Step 11 — Multiple Device Sync

A user may be logged in on:

```text
phone
laptop
tablet
```

Each device tracks:

```text
cur_max_message_id
```

To sync:

```text
Fetch messages where:
recipient_id = current_user
message_id > cur_max_message_id
```

Visual:

```text
User A Phone  cur_max_message_id = 653
User A Laptop cur_max_message_id = 842

Both connect to Chat Server
        |
        v
KV Store returns newer messages for each device
```

Flow:

```text
Device reconnects
   |
   v
Send cur_max_message_id
   |
   v
Server fetches missing messages
   |
   v
Device updates local max id
```

---

# Step 12 — Small Group Chat Flow

Group size max: 100 users.

For small groups, fanout-on-write is acceptable.

```text
User A sends group message
        |
        v
Chat Server
        |
        v
Copy message to each member inbox/sync queue
        |
        v
Deliver to online users
        |
        v
Push notify offline users
```

Visual:

```text
                 +-------------+
User A --------> | Chat Server |
                 +------+------+
                        |
         +--------------+--------------+
         |                             |
         v                             v
  User B inbox                  User C inbox
         |                             |
         v                             v
      User B                        User C
```

Why this works:

```text
Group size is small.
Each recipient can simply read from their own inbox.
Message sync is easy.
```

For huge groups:

```text
Use fanout-on-read instead.
```

---

# Step 13 — Online Presence

Presence servers manage online/offline status.

## Login

```text
Client opens WebSocket
        |
        v
Presence Server
        |
        v
KV Store:
user_id -> {status: online, last_active_at}
```

Visual:

```text
User A -> Presence Server -> KV Store
                         userA: online
```

---

## Logout

```text
User logs out
        |
        v
Presence Server updates status
        |
        v
KV Store:
user_id -> {status: offline}
```

---

## Network Disconnect

Do not immediately mark offline.

Use heartbeat.

```text
Client sends heartbeat every 5 seconds.
If no heartbeat for 30 seconds, mark offline.
```

Visual:

```text
Client -> Server: heartbeat
5s
Client -> Server: heartbeat
5s
Client -> Server: heartbeat

No heartbeat for 30s
        |
        v
Server marks user offline
```

---

# Step 14 — Presence Fanout

When User A status changes:

```text
User A online/offline event
        |
        v
Presence Server
        |
        v
Publish to friend channels
        |
        v
Friends receive update through WebSocket
```

Visual:

```text
              +------------------+
User A -----> | Presence Servers |
              +---+----------+---+
                  |          |
                  v          v
            Channel A-B   Channel A-C
                  |          |
                  v          v
                User B     User C
```

For small friend lists:

```text
fanout status updates directly
```

For very large groups:

```text
fetch status only when user opens group/member list
```

---

# Step 15 — Push Notifications

If recipient is offline:

```text
Chat Server -> Notification Server -> APNs/FCM -> Device
```

Push notification content:

```text
"Alex sent you a message"
```

Important:

```text
Message content may be hidden for privacy.
```

Visual:

```text
Message Queue
     |
     v
Offline recipient?
     |
     v
Push Notification Server
     |
     v
APNs / FCM
     |
     v
User Device
```

---

# Step 16 — Message Storage and Retrieval

## Store Message

```text
partition key: conversation_id or channel_id
sort key: message_id
```

Example Cassandra-like schema:

```sql
CREATE TABLE messages_by_conversation (
    conversation_id BIGINT,
    message_id BIGINT,
    sender_id BIGINT,
    content TEXT,
    created_at TIMESTAMP,
    PRIMARY KEY (conversation_id, message_id)
);
```

Query recent messages:

```sql
SELECT *
FROM messages_by_conversation
WHERE conversation_id = ?
ORDER BY message_id DESC
LIMIT 50;
```

---

# Step 17 — Reliability

## Message Should Not Be Lost

Use:

```text
durable queue
persistent KV store
ACKs
retry
idempotency key
```

## Flow

```text
Client sends message
        |
        v
Chat server validates
        |
        v
Persist message / enqueue durable event
        |
        v
ACK sender
        |
        v
Deliver to recipient
```

If delivery fails:

```text
retry later
```

If user offline:

```text
store message and push notification
```

---

# Step 18 — Scaling Chat Servers

Chat servers maintain persistent WebSocket connections.

Scale by:

```text
1. Add more chat servers.
2. Use service discovery to assign users.
3. Track server connection count.
4. Reconnect users when server fails.
```

Visual:

```text
              +-------------------+
              | Service Discovery |
              +---------+---------+
                        |
        +---------------+---------------+
        |               |               |
        v               v               v
+-------------+ +-------------+ +-------------+
| Chat Srv 1  | | Chat Srv 2  | | Chat Srv N  |
+-------------+ +-------------+ +-------------+
```

---

# Step 19 — Handling Chat Server Failure

If chat server dies:

```text
1. WebSocket disconnects.
2. Client retries login/service discovery.
3. Service discovery returns new chat server.
4. Client reconnects.
5. Client sends cur_max_message_id.
6. Server fetches missed messages from KV store.
```

Visual:

```text
Chat Server 1 fails
        |
        v
Client reconnects
        |
        v
Service Discovery
        |
        v
Chat Server 2
        |
        v
Sync missed messages
```

---

# Step 20 — Final Architecture

```text
                                  +----------------+
                                  |    Clients     |
                                  | mobile / web   |
                                  +--------+-------+
                                           |
                      +--------------------+--------------------+
                      |                                         |
                      v                                         v
              +---------------+                         +---------------+
              | HTTP LB       |                         | Chat Gateway  |
              +-------+-------+                         | WebSocket LB  |
                      |                                 +-------+-------+
                      v                                         |
              +---------------+                                 v
              | API Servers   |                         +---------------+
              | auth/profile  |                         | Chat Servers  |
              | group mgmt    |                         +-------+-------+
              +-------+-------+                                 |
                      |                                         v
                      v                              +----------------------+
              +---------------+                      | Message Sync Queue   |
              | Service       |                      +----------+-----------+
              | Discovery     |                                 |
              +---------------+                 +---------------+--------------+
                                                |                              |
                                                v                              v
                                      +----------------+              +----------------+
                                      | KV Store       |              | Presence       |
                                      | Chat History   |              | Servers        |
                                      +----------------+              +--------+-------+
                                                                                |
                                                                                v
                                                                       +----------------+
                                                                       | Presence KV    |
                                                                       +----------------+

Offline users:
Message Queue -> Notification Servers -> APNs/FCM -> Devices
```

---

# Step 21 — Java Code: Message Model

```java
import java.time.Instant;

public record ChatMessage(
        long messageId,
        long conversationId,
        long senderId,
        Long receiverId,
        Long groupId,
        String content,
        Instant createdAt
) {}
```

---

# Step 22 — Java Code: Snowflake-like Message ID

Simplified version for learning.

```java
public class SimpleMessageIdGenerator {
    private static final long CUSTOM_EPOCH = 1704067200000L; // Jan 1 2024
    private static final int SEQUENCE_BITS = 12;
    private static final int MACHINE_BITS = 10;

    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private final long machineId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SimpleMessageIdGenerator(long machineId) {
        if (machineId < 0 || machineId >= (1L << MACHINE_BITS)) {
            throw new IllegalArgumentException("Invalid machine id");
        }
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long now = System.currentTimeMillis();

        if (now < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards");
        }

        if (now == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                while (now <= lastTimestamp) {
                    now = System.currentTimeMillis();
                }
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = now;

        return ((now - CUSTOM_EPOCH) << (MACHINE_BITS + SEQUENCE_BITS))
                | (machineId << SEQUENCE_BITS)
                | sequence;
    }
}
```

---

# Step 23 — Java Code: In-memory Message Store

```java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageStore {
    private final Map<Long, List<ChatMessage>> messagesByConversation = new ConcurrentHashMap<>();

    public void save(ChatMessage message) {
        messagesByConversation
                .computeIfAbsent(message.conversationId(), id -> new ArrayList<>())
                .add(message);
    }

    public List<ChatMessage> getMessagesAfter(long conversationId, long lastMessageId) {
        return messagesByConversation
                .getOrDefault(conversationId, List.of())
                .stream()
                .filter(m -> m.messageId() > lastMessageId)
                .sorted(Comparator.comparingLong(ChatMessage::messageId))
                .toList();
    }

    public List<ChatMessage> getRecentMessages(long conversationId, int limit) {
        return messagesByConversation
                .getOrDefault(conversationId, List.of())
                .stream()
                .sorted(Comparator.comparingLong(ChatMessage::messageId).reversed())
                .limit(limit)
                .toList();
    }
}
```

---

# Step 24 — Java Code: Conversation ID

```java
public class ConversationUtil {
    public static long oneOnOneConversationId(long userA, long userB) {
        long min = Math.min(userA, userB);
        long max = Math.max(userA, userB);

        return Math.abs((min + ":" + max).hashCode());
    }
}
```

---

# Step 25 — Java Code: Simple Chat Service

```java
import java.time.Instant;
import java.util.List;

public class ChatService {
    private final SimpleMessageIdGenerator idGenerator = new SimpleMessageIdGenerator(1);
    private final MessageStore messageStore = new MessageStore();

    public ChatMessage sendOneOnOneMessage(long fromUser, long toUser, String content) {
        validateMessage(content);

        long conversationId = ConversationUtil.oneOnOneConversationId(fromUser, toUser);
        long messageId = idGenerator.nextId();

        ChatMessage message = new ChatMessage(
                messageId,
                conversationId,
                fromUser,
                toUser,
                null,
                content,
                Instant.now()
        );

        messageStore.save(message);

        // Production:
        // 1. publish to message sync queue
        // 2. deliver through WebSocket if recipient online
        // 3. send push notification if offline

        return message;
    }

    public List<ChatMessage> syncMessages(long conversationId, long lastMessageId) {
        return messageStore.getMessagesAfter(conversationId, lastMessageId);
    }

    private void validateMessage(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        if (content.length() > 100_000) {
            throw new IllegalArgumentException("Message too long");
        }
    }

    public static void main(String[] args) {
        ChatService service = new ChatService();

        ChatMessage message = service.sendOneOnOneMessage(
                101,
                202,
                "Hello!"
        );

        System.out.println(message);

        List<ChatMessage> synced = service.syncMessages(
                message.conversationId(),
                0
        );

        System.out.println(synced);
    }
}
```

---

# Step 26 — Java Code: Presence Service

```java
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PresenceService {
    private static class Presence {
        boolean online;
        Instant lastActiveAt;
    }

    private final Map<Long, Presence> presenceByUser = new ConcurrentHashMap<>();

    public void markOnline(long userId) {
        Presence presence = new Presence();
        presence.online = true;
        presence.lastActiveAt = Instant.now();
        presenceByUser.put(userId, presence);
    }

    public void heartbeat(long userId) {
        Presence presence = presenceByUser.computeIfAbsent(userId, id -> new Presence());
        presence.online = true;
        presence.lastActiveAt = Instant.now();
    }

    public void markOffline(long userId) {
        Presence presence = presenceByUser.computeIfAbsent(userId, id -> new Presence());
        presence.online = false;
        presence.lastActiveAt = Instant.now();
    }

    public boolean isOnline(long userId) {
        Presence presence = presenceByUser.get(userId);
        return presence != null && presence.online;
    }

    public void expireInactiveUsers(long timeoutSeconds) {
        Instant cutoff = Instant.now().minusSeconds(timeoutSeconds);

        for (Map.Entry<Long, Presence> entry : presenceByUser.entrySet()) {
            Presence presence = entry.getValue();

            if (presence.online && presence.lastActiveAt.isBefore(cutoff)) {
                presence.online = false;
            }
        }
    }
}
```

---

# Step 27 — Java Code: Group Fanout

```java
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroupService {
    private final Map<Long, List<Long>> groupMembers = new ConcurrentHashMap<>();

    public void createGroup(long groupId, List<Long> members) {
        if (members.size() > 100) {
            throw new IllegalArgumentException("Group size cannot exceed 100");
        }
        groupMembers.put(groupId, members);
    }

    public List<Long> getRecipients(long groupId, long senderId) {
        return groupMembers
                .getOrDefault(groupId, List.of())
                .stream()
                .filter(userId -> userId != senderId)
                .toList();
    }
}
```

---

# Step 28 — Java Code: Message Queue Event

```java
public record MessageEvent(
        long messageId,
        long conversationId,
        long senderId,
        long recipientId,
        String content
) {}
```

---

# Step 29 — Java Code: Delivery Worker

```java
public class DeliveryWorker {
    private final PresenceService presenceService;

    public DeliveryWorker(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    public void deliver(MessageEvent event) {
        if (presenceService.isOnline(event.recipientId())) {
            sendViaWebSocket(event);
        } else {
            sendPushNotification(event);
        }
    }

    private void sendViaWebSocket(MessageEvent event) {
        System.out.println("Delivered via WebSocket to user " + event.recipientId());
    }

    private void sendPushNotification(MessageEvent event) {
        System.out.println("Push notification sent to user " + event.recipientId());
    }
}
```

---

# Step 30 — Scaling Strategy

## Chat Servers

```text
Scale horizontally.
Use service discovery.
Track active WebSocket connection count.
```

## Message Storage

```text
Partition by conversation_id or channel_id.
Sort by message_id.
Use replication for availability.
```

## Presence

```text
Store presence in fast KV store.
Use TTL/heartbeat.
Use pub-sub for friend status updates.
```

## Queues

```text
Use durable queues for message sync.
Partition queues by user_id or conversation_id.
```

---

# Step 31 — Failure Scenarios

## Chat Server Failure

```text
WebSocket disconnects
Client reconnects
Service discovery assigns new server
Client sends cur_max_message_id
Server syncs missed messages
```

## Message Delivery Failure

```text
Store message first
Retry delivery
Send push if receiver offline
```

## Duplicate Message

Use client-generated idempotency key:

```text
client_message_id
```

If same key received again:

```text
return existing message ACK
```

## Recipient Offline

```text
Store message
Send push notification
Deliver when recipient reconnects
```

---

# Step 32 — FAANG Talking Points

1. Use WebSocket for real-time bidirectional messaging.
2. Use HTTP for login/profile/group management.
3. Chat servers are stateful because they hold WebSocket connections.
4. API servers are stateless and horizontally scalable.
5. Use service discovery to assign best chat server.
6. Store messages in KV/wide-column store like Cassandra/HBase.
7. Use sortable message IDs for ordering.
8. Use durable message sync queue.
9. Use fanout-on-write for small groups.
10. Use fanout-on-read for very large groups.
11. Use heartbeat for presence.
12. Use pub-sub for status fanout.
13. Support multi-device sync with cur_max_message_id.
14. Send push notification for offline users.
15. On reconnect, fetch missed messages from KV store.
16. Use idempotency key to avoid duplicate sends.

---

# Step 33 — One-Minute Interview Summary

> I would use WebSocket for real-time chat because it provides persistent bidirectional communication. The system has stateless API servers for login, profiles, and group management, and stateful chat servers for WebSocket connections. Service discovery assigns users to healthy chat servers based on location and capacity. When a user sends a message, the chat server generates a sortable message ID, stores the message in a distributed key-value store, publishes it to a message sync queue, and delivers it to the recipient’s chat server if online. If the recipient is offline, the system sends a push notification. For small group chats, I would use fanout-on-write to each member inbox. Presence is handled by presence servers using WebSocket heartbeat and a fast KV store. Multi-device sync is done by tracking each device’s latest message ID and fetching missed messages on reconnect.

---

# Quick Revision

```text
Protocol:
WebSocket for chat, HTTP for normal APIs.

Core flow:
Sender -> Chat Server -> Message ID -> Queue -> KV Store -> Recipient Chat Server -> Receiver

Offline:
Store message -> Push notification -> Sync on reconnect

Presence:
Login -> online
Heartbeat -> keep online
No heartbeat -> offline
Presence fanout -> pub/sub

Storage:
KV / wide-column store
Partition by conversation_id or channel_id
Sort by message_id

Best phrase:
Chat servers are stateful because they maintain persistent WebSocket connections.
```
