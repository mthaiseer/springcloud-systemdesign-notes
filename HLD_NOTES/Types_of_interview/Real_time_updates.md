# Real-Time Updates in System Design – Quick Notes

> Short, practical notes for interviews: when to use polling, SSE, or WebSockets.

---

## Why This Matters
Modern apps must show changes **immediately**:
- Chat messages
- Stock price updates
- Notifications
- Live sports scores
- Collaboration changes

Traditional HTTP is **request → response → close**.  
If data changes right after the response, the client will not know unless it asks again.

So the core problem is:

```text
How does the server push new data to the client instantly?
```

---

## Where This Pattern Shows Up

- **Chat** → messages, typing indicators
- **Uber / Lyft** → driver location updates
- **Trading platforms** → millisecond price updates
- **Collaborative editors** → live edits
- **Notifications** → instant alerts
- **Live sports** → score and play-by-play updates

---

# 4 Approaches

1. **Short Polling**
2. **Long Polling**
3. **Server-Sent Events (SSE)**
4. **WebSockets**

Think of them as a spectrum:

```text
Simple ------------------------------------------ Complex
Short Polling -> Long Polling -> SSE -> WebSockets
```

---

# 1) Short Polling

## Idea
Client keeps asking:

```text
"Any updates?"
```

every few seconds.

## Flow
```text
Client -> HTTP request
Server -> response (data or empty)
Wait
Repeat
```

## Example
Poll every **5 sec**

```text
1M users / 5 sec = 200K requests/sec
```

Most requests return **nothing**.

## Pros
- Very simple
- Works everywhere
- Easy to debug
- Stateless server

## Cons
- Wasteful: many empty requests
- Higher latency (depends on poll interval)
- Heavy server load at scale
- Battery drain on mobile

## Best For
- Email inbox refresh
- Dashboard refresh
- Background sync
- Status checks

## Avoid For
- Chat
- Live tracking
- Trading
- Multiplayer games

---

# 2) Long Polling

## Idea
Client sends request once.  
If no data exists, **server waits** until data arrives or timeout happens.

## Flow
```text
Client -> request
Server -> hold connection
New data arrives
Server -> respond immediately
Client -> opens new request
```

## Why Better than Short Polling
Instead of repeated empty requests, server answers **only when useful**.

## Pros
- Near real-time
- Fewer wasted requests
- Still standard HTTP
- Works through proxies/firewalls

## Cons
- Many open connections on server
- Needs async/event-driven backend
- New request needed after every response
- Timeout/retry logic required
- Ordering can get tricky

## Best For
- Notification systems
- Activity feeds
- Moderate chat
- Progress updates

## Example
Short polling in 30 sec:
```text
6 requests, mostly empty
```

Long polling in 30 sec:
```text
2 requests, both useful
```

---

# 3) Server-Sent Events (SSE)

## Idea
Client opens **one long-lived HTTP connection**.  
Server pushes updates whenever data changes.

## Flow
```text
Client -> open EventSource connection
Server -> keep connection open
Server -> send events continuously
```

## Event Format
```text
event: price-update
id: 12345
data: {"symbol":"AAPL","price":150.25}
```

## Best Part
Browser automatically handles:
- reconnect
- retry
- Last-Event-ID resume

## Pros
- Very simple client API
- Built-in reconnection
- Great for server-to-client streaming
- Works with HTTP infra
- Easier than WebSockets

## Cons
- One-way only (server -> client)
- Text only
- Browser connection limits may matter
- Not ideal for true bidirectional apps

## Best For
- Stock tickers
- Live sports scores
- Notification streams
- Build / job progress
- Social/activity feeds

## Not Best For
- Real-time chat
- Multiplayer games
- Collaborative editing

## Key Difference vs Long Polling
Long polling:
```text
1 response per request
```

SSE:
```text
Many events over one connection
```

---

# 4) WebSockets

## Idea
Persistent **bidirectional** connection.  
Client and server can both send messages anytime.

## Flow
```text
HTTP Upgrade -> 101 Switching Protocols
Then full-duplex WebSocket connection
```

## Why Powerful
- No request/response model
- Either side can send anytime
- Very low overhead per message
- Great for interactive systems

## Pros
- True bidirectional communication
- Lowest latency
- Efficient for high-frequency updates
- Supports binary messages

## Cons
- Harder to implement and operate
- Stateful connections
- Needs connection tracking
- Usually needs pub/sub layer for scale
- Reconnection must be built manually
- Some proxies/firewalls may block it

## Best For
- Real-time chat
- Multiplayer games
- Collaborative editors
- Live trading

## Example Scaling Challenge
If Alice is connected to **Server 1** and Bob to **Server 2**:

```text
Alice -> Server 1 -> Redis/Kafka -> Server 2 -> Bob
```

So you usually need:
- message bus
- connection registry
- pub/sub routing

---

# Quick Comparison Table

| Aspect | Short Polling | Long Polling | SSE | WebSockets |
|---|---|---|---|---|
| Direction | Client -> Server | Client -> Server | Server -> Client | Bidirectional |
| Latency | High | Low | Low | Lowest |
| Connection | New request every poll | New request after each response | One persistent HTTP connection | One persistent socket |
| Complexity | Lowest | Low-Medium | Medium | Highest |
| Scaling | Easy | Medium | Medium | Hard |
| Reconnection | Not needed | Manual | Automatic | Manual |
| Binary Support | No | No | No | Yes |
| Firewall Friendly | Yes | Yes | Yes | Sometimes blocked |

---

# Decision Guide

## Use Short Polling when:
- slight delay is okay
- updates are infrequent
- you want the simplest solution

## Use Long Polling when:
- you need near real-time
- you want to stay in HTTP
- message frequency is moderate

## Use SSE when:
- updates mostly flow **server -> client**
- you want simpler real-time push
- automatic reconnection is useful

## Use WebSockets when:
- both client and server send often
- low latency is critical
- interaction is high-frequency

---

# Recommendations by Use Case

| Use Case | Recommended |
|---|---|
| Email inbox | Short Polling |
| Dashboard metrics | Short Polling / SSE |
| Notification system | SSE |
| Activity feed | SSE |
| Stock ticker | SSE |
| Live sports | SSE |
| Build progress | SSE |
| Basic chat | Long Polling or SSE + POST |
| Real-time chat | WebSockets |
| Multiplayer game | WebSockets |
| Collaborative editor | WebSockets |
| Live trading | WebSockets |

---

# Common Production Concerns

## 1. Reconnection
Connections drop. Use:

```text
Exponential backoff + jitter
1s -> 2s -> 4s -> 8s -> ... cap at 30s
```

## 2. Heartbeats
A silent connection may be dead.

Use:
```text
PING / PONG every ~30 sec
```

## 3. Message Ordering
Messages can arrive out of order.

Use:
- sequence numbers
- event IDs
- deduplication
- buffering gaps

## 4. Backpressure
Slow clients cannot keep up.

Options:
- drop low-priority updates
- batch updates
- disconnect slow clients

## 5. Compression
Useful for large JSON/text traffic.

- WebSocket: `permessage-deflate`
- Good for text
- Not useful for already-compressed media

---

# Short Examples

## Example 1: Notification System
Need:
- instant alerts
- mostly server -> client
- reconnect support

**Choice:** SSE

Why:
- easy push model
- built-in reconnect
- simpler than WebSockets

---

## Example 2: Chat App
Need:
- instant messages
- typing indicators
- read receipts
- both sides send often

**Choice:** WebSockets

Why:
- true bidirectional communication
- low-latency interactive messaging

---

## Example 3: Stock Ticker
Need:
- constant server -> client updates
- high frequency
- little client sending

**Choice:** SSE

Why:
- perfect for streaming updates one way

---

## Example 4: Driver Tracking
Need:
- server pushes driver position
- updates every few seconds
- moderate real-time needs

**Choice:** SSE or Long Polling

Why:
- if one-way updates dominate, SSE is simpler
- long polling is okay if infra is HTTP-only

---

## Example 5: Multiplayer Game
Need:
- bidirectional updates
- very low latency
- frequent messages

**Choice:** WebSockets

Why:
- full-duplex low-latency communication

---

# Interview Answer Template

If asked “How would you support real-time updates?”:

```text
I’d first clarify whether the communication is one-way or bidirectional, 
how often updates happen, and how strict the latency requirement is.

If slight delay is acceptable, short polling is simplest.
If we want near real-time while staying in HTTP, long polling works.
If updates are mostly server-to-client, I’d use SSE because it is simpler and has built-in reconnection.
If both client and server need frequent low-latency communication, I’d use WebSockets.
```

---

# Key Takeaways

- Start with the **simplest solution**
- **SSE is often enough** and easier than WebSockets
- Use **WebSockets only when bidirectional low-latency is truly needed**
- Always plan for:
    - reconnects
    - heartbeats
    - ordering
    - backpressure

---

## Final Shortcut

```text
Short Polling = simple but wasteful
Long Polling = better HTTP real-time
SSE = best for one-way push
WebSockets = best for full duplex
```
