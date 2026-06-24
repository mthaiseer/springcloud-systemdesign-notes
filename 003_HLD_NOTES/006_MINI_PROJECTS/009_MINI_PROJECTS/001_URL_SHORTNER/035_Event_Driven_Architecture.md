# 035_Event_Driven_Architecture.md
# MiniURLShortener — Event Driven Architecture

> Core mental model: **Event-driven architecture turns important business facts into durable messages so other parts of the system can react asynchronously without blocking the main user request.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Request-Driven vs Event-Driven Thinking](#4-request-driven-vs-event-driven-thinking)
- [5. Events Are Facts, Not Commands](#5-events-are-facts-not-commands)
- [6. MiniURLShortener Event Map](#6-miniurlshortener-event-map)
- [7. Event Flow Big Picture](#7-event-flow-big-picture)
- [8. Why Kafka Fits This Chapter](#8-why-kafka-fits-this-chapter)
- [9. Topic Design](#9-topic-design)
- [10. Event Schema Design](#10-event-schema-design)
- [11. Producer Design](#11-producer-design)
- [12. Consumer Design](#12-consumer-design)
- [13. Outbox Pattern](#13-outbox-pattern)
- [14. Idempotency In Event Consumers](#14-idempotency-in-event-consumers)
- [15. Ordering And Partitioning](#15-ordering-and-partitioning)
- [16. Retry And Dead Letter Topics](#16-retry-and-dead-letter-topics)
- [17. Backpressure In Event-Driven Systems](#17-backpressure-in-event-driven-systems)
- [18. Eventual Consistency](#18-eventual-consistency)
- [19. Observability And Debugging](#19-observability-and-debugging)
- [20. Spring Boot Implementation](#20-spring-boot-implementation)
- [21. Step-by-Step Dry Runs](#21-step-by-step-dry-runs)
- [22. Internal Execution Walkthrough](#22-internal-execution-walkthrough)
- [23. Testing Strategy](#23-testing-strategy)
- [24. Production Failure Stories](#24-production-failure-stories)
- [25. Debugging Mindset](#25-debugging-mindset)
- [26. Common Mistakes](#26-common-mistakes)
- [27. Interview-Ready Explanation](#27-interview-ready-explanation)
- [28. Senior Engineer Checklist](#28-senior-engineer-checklist)
- [29. One-Page Cheat Sheet](#29-one-page-cheat-sheet)
- [30. One Picture To Remember](#30-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener started as a simple synchronous backend:

```text
POST /api/v1/urls     -> create short URL
GET /{shortCode}      -> redirect user
```

At small scale, the service can do many things inside the request thread:

```text
validate URL
save URL
write analytics
send notification
update dashboard
call abuse scanner
write audit log
```

But at production scale, this becomes dangerous.

A redirect request must be fast. If every redirect waits for analytics, database writes, notification calls, and dashboard updates, then user-facing latency becomes tied to slow background work.

Bad synchronous design:

```text
User clicks short URL
  |
  v
Redirect service
  |
  +--> DB lookup
  +--> Analytics write
  +--> Audit write
  +--> Notification call
  +--> Fraud check
  |
  v
Redirect response delayed
```

If analytics is slow, redirect becomes slow.
If notification service is down, redirect may fail.
If dashboard database is overloaded, user experience suffers.

Event-driven architecture fixes this by separating the main business action from side effects.

Better design:

```text
User clicks short URL
  |
  v
Redirect service does only redirect-critical work
  |
  +--> publishes UrlRedirected event
  |
  v
302 redirect quickly returned

Background consumers later process:
  analytics
  audit
  dashboards
  abuse detection
```

Production mental model:

```text
The request path should do what the user needs now.
Events let the system do everything else later, safely and independently.
```

---

## 2. The One Core Mental Model

Event-driven architecture is:

```text
BUSINESS FACT BROADCASTING
```

A service records that something important happened, then publishes that fact so other services can react.

```text
Something happened:
    ShortUrlCreated
    UrlRedirected
    ShortUrlBlocked
    AliasConflictDetected
    AnalyticsAggregated

Other components react:
    analytics worker
    audit worker
    dashboard updater
    abuse scanner
    notification worker
```

ASCII:

```text
                 Business Action
                       |
                       v
              +------------------+
              | Domain Event     |
              | "URL redirected"|
              +------------------+
                       |
                       v
              +------------------+
              | Kafka Topic      |
              | durable stream   |
              +------------------+
                 /       |        \
                v        v         v
        Analytics    Audit     Dashboard
        Consumer     Consumer  Consumer
```

One-line memory:

```text
An event is a durable fact that lets other parts of the system react without slowing down the original request.
```

For MiniURLShortener:

```text
Redirect should redirect.
Analytics should happen because redirect happened.
The redirect request should not become an analytics transaction.
```

---

## 3. Problem Statement

Design event-driven architecture for MiniURLShortener.

It must support:

```text
1. Publishing events when short URLs are created.
2. Publishing events when short URLs are redirected.
3. Processing click analytics asynchronously.
4. Processing audit logs asynchronously.
5. Supporting retry for temporary failures.
6. Sending poison messages to DLT.
7. Avoiding duplicate analytics writes.
8. Preserving useful ordering per shortCode.
9. Handling consumer lag and backpressure.
10. Keeping user-facing APIs fast.
```

It should avoid:

```text
blocking redirect on analytics
losing events silently
creating duplicate analytics rows
publishing event before DB commit incorrectly
one giant topic for everything
unclear event schema
breaking old consumers when schema changes
turning Kafka into remote method calls
```

Out of scope:

```text
1. Full Kafka cluster operations.
2. Schema Registry deep dive.
3. Exactly-once Kafka internals.
4. Flink/Spark stream processing.
5. Full distributed tracing implementation.
```

This chapter focuses on production-shaped event design for the URL shortener.

---

## 4. Request-Driven vs Event-Driven Thinking

Request-driven systems ask:

```text
Who do I call now to finish this operation?
```

Event-driven systems ask:

```text
What fact happened, and who may care about it later?
```

Synchronous request-driven flow:

```text
Redirect API
   |
   +--> Analytics API
   |
   +--> Audit API
   |
   +--> Dashboard API
   |
   v
Return response only after calls finish
```

Event-driven flow:

```text
Redirect API
   |
   +--> publish UrlRedirected event
   |
   v
Return response quickly

Consumers:
   Analytics consumes event
   Audit consumes event
   Dashboard consumes event
```

Comparison:

```text
+----------------------+--------------------------+---------------------------+
| Dimension            | Request-Driven           | Event-Driven              |
+----------------------+--------------------------+---------------------------+
| Coupling             | Caller knows receiver    | Producers know topic      |
| Latency              | Waits for downstream     | Background processing     |
| Failure impact       | Downstream can break API | Downstream can retry      |
| Scaling              | Scale call chain         | Scale consumers separately|
| Debugging            | Direct call stack        | Needs event tracing       |
| Consistency          | Often immediate          | Usually eventual          |
+----------------------+--------------------------+---------------------------+
```

Important:

```text
Event-driven is not automatically better.
It is better when side effects can happen asynchronously.
```

Do not use events for everything.

Use synchronous calls when:

```text
client needs answer now
operation must complete before response
strong consistency is required immediately
```

Use events when:

```text
work is a side effect
multiple consumers need the same fact
work can retry independently
latency should not block user request
```

---

## 5. Events Are Facts, Not Commands

This is the most important design rule.

A command says:

```text
Do this.
```

An event says:

```text
This happened.
```

Bad event name:

```text
UpdateAnalyticsCommand
SendAuditLogCommand
ProcessClickCommand
```

Better event name:

```text
UrlRedirectedEvent
ShortUrlCreatedEvent
ShortUrlBlockedEvent
```

Why?

Because producer should not control every consumer.

Bad mental model:

```text
Redirect service tells analytics what to do.
```

Better mental model:

```text
Redirect service announces that redirect happened.
Analytics decides how to react.
Audit decides how to react.
Dashboard decides how to react.
```

ASCII:

```text
Wrong:
Redirect Service ---> "Analytics, increment count"

Right:
Redirect Service ---> "UrlRedirected happened"
                         |
                         +--> Analytics increments count
                         +--> Audit writes record
                         +--> Dashboard updates view
```

Event naming rule:

```text
Use past tense:
ShortUrlCreated
UrlRedirected
ShortUrlExpired
ShortUrlBlocked
```

Past tense reminds you it is a fact.

---

## 6. MiniURLShortener Event Map

Useful events in MiniURLShortener:

```text
ShortUrlCreated
UrlRedirected
ShortUrlExpired
ShortUrlBlocked
ShortUrlDeleted
AnalyticsAggregated
```

Event map:

```text
+-------------------+----------------------+-----------------------------+
| Event             | Producer             | Consumers                   |
+-------------------+----------------------+-----------------------------+
| ShortUrlCreated   | URL service          | audit, dashboard, abuse     |
| UrlRedirected     | Redirect service     | analytics, audit, dashboard |
| ShortUrlBlocked   | Admin service        | cache invalidator, audit    |
| ShortUrlDeleted   | Admin service        | cache invalidator, audit    |
| AnalyticsAggregated| Analytics worker    | dashboard, reports          |
+-------------------+----------------------+-----------------------------+
```

Main chapter focus:

```text
UrlRedirected event
```

Because redirect traffic is high volume.

A single popular short URL can produce:

```text
1,000 clicks/minute
100,000 clicks/minute
millions/day
```

Without events:

```text
redirect endpoint becomes analytics writer
```

With events:

```text
redirect endpoint becomes event producer
analytics worker becomes event consumer
```

---

## 7. Event Flow Big Picture

Full MiniURLShortener event-driven flow:

```text
          User Click
              |
              v
      +----------------+
      | Redirect API   |
      +----------------+
              |
              | DB/cache lookup
              v
      +----------------+
      | Return 302     |
      +----------------+
              |
              | publish event
              v
      +----------------+
      | Kafka Topic    |
      | url.redirected |
      +----------------+
          /      |       \
         v       v        v
+------------+ +------+ +------------+
| Analytics  | |Audit | | Dashboard  |
| Consumer   | |Worker| | Consumer   |
+------------+ +------+ +------------+
      |
      v
+----------------+
| Analytics DB   |
+----------------+
```

Important design decision:

```text
Should event be published before or after redirect response?
```

Usually:

```text
Do redirect-critical lookup.
Publish event asynchronously.
Return redirect quickly.
```

But avoid silent event loss.

For create events, outbox is safer because create involves DB transaction.

For redirect events, tradeoff depends on analytics criticality:

```text
If analytics can lose tiny percentage -> async producer may be acceptable.
If analytics must be complete -> use durable event write/outbox/log-first approach.
```

Senior answer:

```text
For user-facing redirect, correctness of redirect is more important than click analytics.
But I still monitor producer errors and consumer lag, and for revenue-critical analytics I use stronger durability.
```

---

## 8. Why Kafka Fits This Chapter

Kafka is useful because it acts like a durable event log.

Mental model:

```text
Kafka is not just a queue.
Kafka is an append-only commit log split into partitions.
```

ASCII:

```text
Topic: url.redirected.v1

Partition 0:
  offset 0 -> event A
  offset 1 -> event B
  offset 2 -> event C

Partition 1:
  offset 0 -> event D
  offset 1 -> event E

Consumer group analytics-worker:
  remembers committed offsets
```

Kafka helps with:

```text
durability
consumer groups
parallel processing
replay
backpressure buffering
multiple independent consumers
ordering per partition
```

Why not only REST calls?

```text
REST call is point-to-point and immediate.
Kafka event is broadcast-like and durable.
```

If analytics worker is down:

```text
REST call fails now.
Kafka event waits in topic.
Worker catches up later.
```

ASCII:

```text
Analytics worker down
       |
       v
Kafka stores events
       |
       v
Worker comes back
       |
       v
Consumes old events from committed offset
```

---

## 9. Topic Design

Do not dump everything into one topic.

Bad topic:

```text
events
```

Better topics:

```text
url.created.v1
url.redirected.v1
url.lifecycle.v1
url.analytics.retry.v1
url.analytics.dlt.v1
```

Topic naming convention:

```text
<domain>.<event-name>.<version>
```

Examples:

```text
url.redirected.v1
url.created.v1
url.blocked.v1
```

Why include version?

Because event schema changes over time.

```text
v1: shortCode, timestamp, userAgent
v2: adds country, deviceType, referrerDomain
```

Topic design table:

```text
+----------------------+-----------------------------+-------------------------+
| Topic                | Purpose                     | Volume                  |
+----------------------+-----------------------------+-------------------------+
| url.created.v1       | URL creation events         | medium                  |
| url.redirected.v1    | click/redirect events       | very high               |
| url.analytics.retry.v1| temporary analytics retry  | variable                |
| url.analytics.dlt.v1 | poison messages             | low but critical        |
+----------------------+-----------------------------+-------------------------+
```

Partitioning rule:

```text
Use shortCode as key for redirect events.
```

Why?

```text
All clicks for same shortCode go to same partition.
This preserves order per shortCode.
It also helps aggregate per shortCode.
```

But beware hot keys.

A viral short URL can overload one partition.

Advanced solution:

```text
key = shortCode + bucket
```

Example:

```text
sale2026#0
sale2026#1
sale2026#2
```

This spreads one hot URL across partitions but weakens strict ordering.

---

## 10. Event Schema Design

Event schema should contain enough information for consumers to work without calling producer service for every event.

Bad event:

```json
{
  "shortCode": "abc123"
}
```

Better event:

```json
{
  "eventId": "evt_01HYZABC123",
  "eventType": "UrlRedirected",
  "eventVersion": 1,
  "occurredAt": "2026-06-24T10:15:30Z",
  "shortCode": "abc123",
  "urlId": 1001,
  "ownerId": 501,
  "requestId": "req_789",
  "ipHash": "a8f14c",
  "userAgent": "Mozilla/5.0",
  "referer": "https://google.com",
  "country": "RO"
}
```

Core fields:

```text
eventId        unique id for idempotency
eventType      UrlRedirected
eventVersion   schema version
occurredAt      when fact happened
requestId       trace/correlation id
shortCode       business key
urlId           stable DB identifier
```

Privacy note:

```text
Do not blindly store raw IP addresses forever.
Use IP hash or privacy-aware retention.
```

Schema design ASCII:

```text
+------------------------------+
| Event Envelope               |
| eventId                      |
| eventType                    |
| eventVersion                 |
| occurredAt                   |
| requestId                    |
+------------------------------+
| Event Payload                |
| shortCode                    |
| urlId                        |
| ownerId                      |
| click metadata               |
+------------------------------+
```

Envelope fields are common.
Payload fields are event-specific.

---

## 11. Producer Design

Producer is the service that publishes the event.

For redirect event:

```text
RedirectService produces UrlRedirected event.
```

Producer responsibilities:

```text
1. Build correct event.
2. Use stable eventId.
3. Use useful partition key.
4. Publish to correct topic.
5. Log publish success/failure.
6. Avoid blocking user path unnecessarily.
```

Simple producer flow:

```text
redirect request
  |
  v
lookup shortCode
  |
  v
build UrlRedirectedEvent
  |
  v
send to Kafka
  |
  v
return redirect
```

Better high-scale redirect flow:

```text
redirect request
  |
  v
lookup shortCode
  |
  +--> return redirect as fast as possible
  |
  +--> publish analytics event asynchronously
```

Producer failure question:

```text
What if Kafka publish fails?
```

Options:

```text
1. Fail redirect request.
2. Ignore failure and lose analytics.
3. Retry briefly.
4. Write to local/outbox fallback.
```

For MiniURLShortener:

```text
Redirect correctness > analytics completeness.
```

But for paid analytics product:

```text
Analytics completeness may become business-critical.
Then use stronger durability.
```

---

## 12. Consumer Design

Consumer receives events and reacts.

Analytics consumer responsibilities:

```text
1. Read UrlRedirected events.
2. Validate event shape.
3. Deduplicate using eventId.
4. Aggregate click count.
5. Commit offset only after successful processing.
6. Retry temporary failures.
7. Send poison messages to DLT.
```

Consumer flow:

```text
Kafka event
  |
  v
Deserialize
  |
  v
Validate schema
  |
  v
Check idempotency table
  |
  +-- already processed -> commit offset
  |
  v
Write analytics
  |
  v
Record eventId processed
  |
  v
Commit offset
```

ASCII:

```text
+-------------+     +--------------+     +----------------+
| Kafka Topic | --> | Consumer     | --> | Analytics DB   |
+-------------+     +--------------+     +----------------+
                         |
                         v
                  +--------------+
                  | processed_events
                  | event_id PK  |
                  +--------------+
```

Golden rule:

```text
A consumer must be safe to run the same event twice.
```

Kafka may deliver at least once.
Your consumer must be idempotent.

---

## 13. Outbox Pattern

The outbox pattern solves a classic problem:

```text
How do I update the database and publish an event atomically?
```

Bad create flow:

```text
1. Insert short_url row.
2. Publish ShortUrlCreated event.
```

Failure case:

```text
DB insert succeeds.
Kafka publish fails.
Now short URL exists but no event exists.
```

Opposite failure:

```text
Kafka publish succeeds.
DB transaction rolls back.
Now event says URL exists but DB says it does not.
```

Outbox solution:

```text
In the same DB transaction:
1. Insert business row.
2. Insert outbox event row.

Later:
3. Outbox publisher reads unsent events.
4. Publishes to Kafka.
5. Marks outbox row as published.
```

ASCII:

```text
CreateShortUrl Transaction

+----------------------------+
| BEGIN TX                   |
| insert short_urls          |
| insert outbox_events       |
| COMMIT                     |
+----------------------------+
              |
              v
     Outbox Publisher Worker
              |
              v
          Kafka Topic
```

Outbox table:

```sql
CREATE TABLE outbox_events (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(64) NOT NULL UNIQUE,
    aggregate_type VARCHAR(64) NOT NULL,
    aggregate_id VARCHAR(64) NOT NULL,
    event_type VARCHAR(128) NOT NULL,
    topic VARCHAR(128) NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    published_at TIMESTAMPTZ
);
```

When to use outbox:

```text
Use outbox when event must correspond exactly to a DB state change.
```

Great for:

```text
ShortUrlCreated
ShortUrlBlocked
ShortUrlDeleted
```

Maybe not always needed for:

```text
high-volume click events where tiny analytics loss is acceptable
```

Senior nuance:

```text
Outbox increases reliability but adds latency and operational complexity.
Use it where event correctness matters.
```

---

## 14. Idempotency In Event Consumers

Idempotency means:

```text
Processing the same event multiple times has the same final effect as processing it once.
```

Why needed?

Because Kafka delivery is usually at-least-once.

Failure example:

```text
1. Consumer writes analytics.
2. Consumer crashes before committing offset.
3. Consumer restarts.
4. Same event is read again.
5. Without idempotency, analytics increments twice.
```

Bad result:

```text
Actual clicks: 1
Recorded clicks: 2
```

Idempotent design:

```text
processed_events table has event_id unique.
Consumer inserts event_id inside same transaction as analytics write.
If event_id already exists, skip.
```

SQL:

```sql
CREATE TABLE processed_events (
    event_id VARCHAR(64) PRIMARY KEY,
    consumer_name VARCHAR(128) NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
```

Consumer transaction:

```text
BEGIN
  insert into processed_events(event_id, consumer_name)
  if duplicate -> already processed -> rollback/commit skip
  update analytics count
COMMIT
commit Kafka offset
```

ASCII:

```text
Same event delivered twice
        |
        v
+----------------------+
| event_id exists?     |
+----------------------+
   | yes        | no
   v            v
 skip       write analytics
```

Golden rule:

```text
Commit Kafka offset after side effect is safely stored.
```

---

## 15. Ordering And Partitioning

Kafka ordering is guaranteed only within a partition.

If all events for a shortCode use key `shortCode`, Kafka sends them to the same partition.

```text
key = abc123
```

ASCII:

```text
url.redirected.v1

Partition 0:
  abc123 click 1
  abc123 click 2
  abc123 click 3

Partition 1:
  xyz789 click 1
  xyz789 click 2
```

This gives per-shortCode ordering.

Why ordering matters:

```text
For simple count aggregation, strict order may not matter.
For lifecycle events, order matters more.
```

Example lifecycle:

```text
ShortUrlCreated
ShortUrlBlocked
ShortUrlDeleted
```

If these are processed out of order, cache may become wrong.

Partition key choices:

```text
+------------------+-----------------------------+--------------------------+
| Key              | Benefit                     | Risk                     |
+------------------+-----------------------------+--------------------------+
| shortCode        | per URL ordering            | hot key for viral URL    |
| ownerId          | per owner ordering          | hot owner risk           |
| random           | best spread                 | no ordering              |
| shortCode+bucket | spread hot URL              | weaker ordering          |
+------------------+-----------------------------+--------------------------+
```

For MiniURLShortener redirect analytics:

```text
Start with key = shortCode.
If hot keys appear, introduce bucketed aggregation.
```

---

## 16. Retry And Dead Letter Topics

Not all failures are equal.

Temporary failure:

```text
analytics DB timeout
network issue
connection pool exhausted
```

Permanent failure:

```text
invalid JSON
missing required field
unsupported schema version
corrupt payload
```

Retry strategy:

```text
temporary failure -> retry with backoff
permanent failure -> DLT
```

ASCII:

```text
url.redirected.v1
      |
      v
Analytics Consumer
      |
      +-- success -> commit
      |
      +-- temporary fail -> retry topic
      |
      +-- poison message -> DLT
```

Topic design:

```text
url.redirected.v1
url.redirected.retry.1m.v1
url.redirected.retry.5m.v1
url.redirected.dlt.v1
```

Why DLT matters:

```text
One poison event should not block an entire partition forever.
```

DLT message should include:

```text
original topic
original partition
original offset
event payload
error class
error message
failedAt
consumer name
```

Important:

```text
DLT is not a trash bin.
DLT is a production investigation queue.
```

---

## 17. Backpressure In Event-Driven Systems

Backpressure means:

```text
Producers create work faster than consumers can process it.
```

In event-driven systems, backpressure appears as consumer lag.

```text
incoming events: 100,000/min
consumer capacity: 50,000/min
lag grows: 50,000/min
```

ASCII:

```text
Producer speed:  >>>>>>>>>>>>>>>>
Consumer speed:  >>>>>>>
Kafka lag:       [ grows grows grows ]
```

Kafka helps by buffering, but it does not magically remove work.

Backpressure controls:

```text
1. Increase consumer instances up to partition count.
2. Increase partitions if needed.
3. Batch DB writes.
4. Reduce per-event DB writes using aggregation windows.
5. Pause consumers when DB is overloaded.
6. Shed non-critical events if business allows.
7. Use retry topics instead of tight retry loops.
```

MiniURLShortener analytics example:

Bad:

```text
one DB update per click
```

Better:

```text
aggregate in memory for 5 seconds
then write count delta per shortCode
```

ASCII:

```text
1000 click events for abc123
        |
        v
Analytics worker groups them
        |
        v
one DB update:
    click_count = click_count + 1000
```

Golden rule:

```text
Kafka absorbs bursts, but consumers and databases must still be designed for the total work.
```

---

## 18. Eventual Consistency

Event-driven systems are usually eventually consistent.

Meaning:

```text
The main action is complete now.
The side effects become visible later.
```

Example:

```text
User clicks short URL at 10:00:00.
Redirect happens immediately.
Dashboard count updates at 10:00:05.
```

This is okay for analytics.

It is not okay for everything.

Good eventual consistency use cases:

```text
analytics dashboards
audit indexing
email notifications
search index updates
recommendations
reports
```

Bad eventual consistency use cases:

```text
payment confirmation before charging
seat booking without lock
security decision needed before access
redirect target lookup itself
```

MiniURLShortener consistency model:

```text
Redirect correctness: strong enough now.
Analytics visibility: eventual.
Dashboard freshness: eventual.
Audit: durable eventually.
```

Interview sentence:

```text
I use events for side effects where eventual consistency is acceptable, not for decisions that must be correct before responding to the user.
```

---

## 19. Observability And Debugging

Event-driven systems need stronger observability because there is no single call stack.

Track:

```text
producer send rate
producer error rate
topic lag
consumer processing rate
consumer failure rate
retry topic size
DLT count
event processing latency
oldest unprocessed event age
```

Important IDs:

```text
requestId
eventId
shortCode
consumerGroup
topic
partition
offset
```

ASCII trace:

```text
Request req_123
   |
   v
UrlRedirected event evt_999
   |
   v
Kafka url.redirected.v1 partition 3 offset 881
   |
   v
analytics-worker processed evt_999
   |
   v
analytics DB updated
```

Debugging without IDs is painful.

Bad log:

```text
processed event
```

Better log:

```text
processed eventId=evt_999 topic=url.redirected.v1 partition=3 offset=881 shortCode=abc123 latencyMs=42
```

Golden rule:

```text
Every event should be traceable from producer to consumer to side effect.
```

---

## 20. Spring Boot Implementation

### 20.1 Event DTO

```java
package com.miniurl.shortener.events;

import java.time.Instant;

public class UrlRedirectedEvent {

    private String eventId;
    private String eventType;
    private int eventVersion;
    private Instant occurredAt;
    private String requestId;
    private String shortCode;
    private Long urlId;
    private Long ownerId;
    private String ipHash;
    private String userAgent;
    private String referer;
    private String country;

    public UrlRedirectedEvent() {
    }

    public UrlRedirectedEvent(
            String eventId,
            String eventType,
            int eventVersion,
            Instant occurredAt,
            String requestId,
            String shortCode,
            Long urlId,
            Long ownerId,
            String ipHash,
            String userAgent,
            String referer,
            String country
    ) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventVersion = eventVersion;
        this.occurredAt = occurredAt;
        this.requestId = requestId;
        this.shortCode = shortCode;
        this.urlId = urlId;
        this.ownerId = ownerId;
        this.ipHash = ipHash;
        this.userAgent = userAgent;
        this.referer = referer;
        this.country = country;
    }

    public String getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public int getEventVersion() { return eventVersion; }
    public Instant getOccurredAt() { return occurredAt; }
    public String getRequestId() { return requestId; }
    public String getShortCode() { return shortCode; }
    public Long getUrlId() { return urlId; }
    public Long getOwnerId() { return ownerId; }
    public String getIpHash() { return ipHash; }
    public String getUserAgent() { return userAgent; }
    public String getReferer() { return referer; }
    public String getCountry() { return country; }
}
```

### 20.2 Kafka Producer

```java
package com.miniurl.shortener.events;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UrlEventProducer {

    private static final String URL_REDIRECTED_TOPIC = "url.redirected.v1";

    private final KafkaTemplate<String, UrlRedirectedEvent> kafkaTemplate;

    public UrlEventProducer(KafkaTemplate<String, UrlRedirectedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUrlRedirected(UrlRedirectedEvent event) {
        // Key = shortCode gives ordering per short URL.
        kafkaTemplate.send(URL_REDIRECTED_TOPIC, event.getShortCode(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        // In production: log eventId, shortCode, requestId, exception.
                        // Do not throw blindly if redirect correctness is more important than analytics.
                        System.err.println("Failed to publish UrlRedirected event: " + event.getEventId());
                    }
                });
    }
}
```

### 20.3 Redirect Service Publishing Event

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.events.UrlEventProducer;
import com.miniurl.shortener.events.UrlRedirectedEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RedirectService {

    private final ShortUrlRepository repository;
    private final UrlEventProducer eventProducer;

    public RedirectService(
            ShortUrlRepository repository,
            UrlEventProducer eventProducer
    ) {
        this.repository = repository;
        this.eventProducer = eventProducer;
    }

    public String resolveRedirect(String shortCode, RedirectRequestMetadata metadata) {
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));

        if (shortUrl.isBlocked()) {
            throw new ShortCodeBlockedException(shortCode);
        }

        if (shortUrl.isExpired(Instant.now())) {
            throw new ShortCodeExpiredException(shortCode);
        }

        UrlRedirectedEvent event = new UrlRedirectedEvent(
                "evt_" + UUID.randomUUID(),
                "UrlRedirected",
                1,
                Instant.now(),
                metadata.requestId(),
                shortUrl.getShortCode(),
                shortUrl.getId(),
                shortUrl.getOwnerId(),
                metadata.ipHash(),
                metadata.userAgent(),
                metadata.referer(),
                metadata.country()
        );

        eventProducer.publishUrlRedirected(event);

        return shortUrl.getLongUrl();
    }
}
```

### 20.4 Consumer With Idempotency

```java
package com.miniurl.shortener.analytics;

import com.miniurl.shortener.events.UrlRedirectedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UrlRedirectedAnalyticsConsumer {

    private final ProcessedEventRepository processedEventRepository;
    private final AnalyticsRepository analyticsRepository;

    public UrlRedirectedAnalyticsConsumer(
            ProcessedEventRepository processedEventRepository,
            AnalyticsRepository analyticsRepository
    ) {
        this.processedEventRepository = processedEventRepository;
        this.analyticsRepository = analyticsRepository;
    }

    @Transactional
    @KafkaListener(
            topics = "url.redirected.v1",
            groupId = "analytics-worker"
    )
    public void consume(UrlRedirectedEvent event) {
        if (processedEventRepository.existsByEventIdAndConsumerName(
                event.getEventId(),
                "analytics-worker"
        )) {
            return;
        }

        analyticsRepository.incrementClickCount(event.getShortCode(), event.getOccurredAt());

        processedEventRepository.save(new ProcessedEvent(
                event.getEventId(),
                "analytics-worker"
        ));
    }
}
```

### 20.5 Analytics Increment Query

```java
package com.miniurl.shortener.analytics;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Repository
public class AnalyticsRepository {

    private final JdbcTemplate jdbcTemplate;

    public AnalyticsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void incrementClickCount(String shortCode, Instant occurredAt) {
        LocalDate day = occurredAt.atZone(ZoneOffset.UTC).toLocalDate();

        jdbcTemplate.update("""
            INSERT INTO url_daily_analytics(short_code, day, click_count)
            VALUES (?, ?, 1)
            ON CONFLICT (short_code, day)
            DO UPDATE SET click_count = url_daily_analytics.click_count + 1
            """, shortCode, day);
    }
}
```

### 20.6 Processed Event Table

```sql
CREATE TABLE processed_events (
    event_id VARCHAR(80) NOT NULL,
    consumer_name VARCHAR(128) NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (event_id, consumer_name)
);
```

### 20.7 Analytics Table

```sql
CREATE TABLE url_daily_analytics (
    short_code VARCHAR(32) NOT NULL,
    day DATE NOT NULL,
    click_count BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (short_code, day)
);
```

---

## 21. Step-by-Step Dry Runs

### Dry Run 1: Normal Redirect Event

Input:

```text
GET /abc123
```

Flow:

```text
1. Redirect controller receives request.
2. RedirectService looks up abc123.
3. URL is active and not expired.
4. Service builds UrlRedirectedEvent.
5. Producer sends event to url.redirected.v1 with key abc123.
6. API returns 302 redirect.
7. Analytics consumer reads event.
8. Consumer checks processed_events.
9. eventId is new.
10. Consumer increments analytics count.
11. Consumer stores processed eventId.
12. Kafka offset is committed.
```

Result:

```text
User gets fast redirect.
Analytics updates asynchronously.
```

---

### Dry Run 2: Analytics Consumer Down

Situation:

```text
Redirect traffic continues.
Analytics worker is down for 10 minutes.
```

Flow:

```text
1. Redirect service keeps publishing UrlRedirected events.
2. Kafka stores events in topic partitions.
3. Consumer group offset does not move.
4. Consumer lag grows.
5. Worker restarts.
6. Worker resumes from last committed offset.
7. Worker catches up.
```

ASCII:

```text
Producer ---> Kafka topic ---> X analytics worker down
                 |
                 v
              lag grows
                 |
                 v
          worker restarts
                 |
                 v
            lag decreases
```

Lesson:

```text
Kafka decouples producer availability from consumer availability.
```

---

### Dry Run 3: Consumer Crash After DB Write

Flow:

```text
1. Consumer reads event evt_1.
2. Consumer increments click count.
3. Consumer saves processed eventId.
4. Consumer crashes before offset commit.
5. Kafka redelivers evt_1.
6. Consumer checks processed_events.
7. evt_1 already exists.
8. Consumer skips analytics update.
9. Offset commits.
```

Result:

```text
No duplicate click count.
```

Lesson:

```text
Idempotency protects analytics from duplicate delivery.
```

---

### Dry Run 4: Poison Message

Event:

```json
{
  "eventType": "UrlRedirected",
  "eventVersion": 99,
  "shortCode": null
}
```

Flow:

```text
1. Consumer receives event.
2. Schema validation fails.
3. Retrying will not fix it.
4. Consumer sends event to DLT.
5. Offset is committed to avoid blocking partition.
6. Engineer investigates DLT.
```

Lesson:

```text
Permanent bad messages should go to DLT, not infinite retry.
```

---

### Dry Run 5: Hot Short URL

Situation:

```text
shortCode = sale2026
traffic = 100,000 clicks/min
```

Flow:

```text
1. All events use key sale2026.
2. Kafka puts all on one partition.
3. One consumer thread handles that partition.
4. Lag grows for that partition.
5. Other partitions may be fine.
```

Fix options:

```text
1. Batch aggregation.
2. Bucket key: sale2026#0..N.
3. Increase partitions for future topics.
4. Dedicated hot-key handling.
```

Lesson:

```text
Ordering keys can create hot partitions.
```

---

## 22. Internal Execution Walkthrough

Detailed redirect event path:

```text
1. HTTP request enters application.
2. Controller extracts shortCode.
3. Service validates shortCode format.
4. Service checks Redis cache or DB.
5. Service confirms URL is active.
6. Service constructs event object.
7. Producer serializes event to JSON.
8. Kafka producer chooses partition using key shortCode.
9. Broker appends event to partition log.
10. Producer receives acknowledgement.
11. Consumer polls event from assigned partition.
12. Consumer deserializes event.
13. Consumer checks idempotency table.
14. Consumer writes analytics update.
15. Consumer stores processed eventId.
16. Consumer commits Kafka offset.
```

ASCII:

```text
+--------+    +----------+    +---------+    +-------+    +----------+
| Client | -> | Redirect | -> | Kafka   | -> | Worker| -> | Analytics|
|        |    | Service  |    | Topic   |    |       |    | DB       |
+--------+    +----------+    +---------+    +-------+    +----------+
     |              |              |             |             |
     | GET /abc123  |              |             |             |
     |              | UrlRedirected|             |             |
     | <- 302       |              |             |             |
     |              |              | poll event  |             |
     |              |              |             | increment   |
```

Key insight:

```text
The user's redirect response does not wait for analytics DB write.
```

---

## 23. Testing Strategy

Test event-driven code at multiple levels.

### Unit tests

```text
RedirectService publishes UrlRedirectedEvent after valid redirect.
Event contains correct shortCode, urlId, ownerId, requestId.
Blocked URL does not publish redirect event.
Expired URL does not publish redirect event.
```

### Producer tests

```text
Topic name is url.redirected.v1.
Kafka key is shortCode.
Event has eventId.
Event has eventVersion.
```

### Consumer tests

```text
New event increments analytics.
Duplicate event does not increment analytics twice.
Invalid event goes to DLT.
Temporary DB failure triggers retry.
```

### Integration tests

Use Testcontainers Kafka and Postgres:

```text
1. Produce UrlRedirectedEvent.
2. Consumer receives event.
3. Analytics DB row is updated.
4. processed_events row is created.
5. Duplicate event does not double count.
```

### Lag/backpressure tests

```text
Produce many events quickly.
Slow analytics DB intentionally.
Verify consumer lag increases.
Scale consumers.
Verify lag decreases.
```

Testing rule:

```text
Do not only test event publishing.
Test event side effects and duplicate delivery.
```

---

## 24. Production Failure Stories

### Failure Story 1: Analytics call inside redirect path

A team writes analytics directly during redirect.

```text
GET /abc123 -> lookup -> insert analytics -> redirect
```

Analytics DB slows down.

Result:

```text
Redirect p99 jumps from 40ms to 900ms.
Users feel delay.
```

Fix:

```text
Publish UrlRedirected event.
Process analytics asynchronously.
```

Lesson:

```text
Do not put non-critical side effects in critical request path.
```

---

### Failure Story 2: Duplicate click counts

Consumer processes event and writes analytics.
Then it crashes before committing offset.
Kafka redelivers event.
Analytics increments again.

Result:

```text
Dashboard overcounts clicks.
```

Fix:

```text
Use eventId and processed_events idempotency table.
```

Lesson:

```text
At-least-once delivery requires idempotent consumers.
```

---

### Failure Story 3: Event published but DB rolled back

Create URL flow publishes `ShortUrlCreated` before DB transaction commits.
Then DB transaction fails.

Result:

```text
Dashboard shows URL that does not exist.
```

Fix:

```text
Use outbox pattern for DB state change events.
```

Lesson:

```text
Publishing events around transactions requires atomicity thinking.
```

---

### Failure Story 4: Poison message blocks partition

Consumer fails on one malformed event.
It retries forever without committing offset.

Result:

```text
All later events in that partition are blocked.
Lag grows massively.
```

Fix:

```text
Classify permanent failure and send to DLT.
```

Lesson:

```text
Retry is for temporary failure, not corrupt data.
```

---

### Failure Story 5: Hot key overload

A celebrity shares one short link.
All events use same shortCode key.
All clicks go to one Kafka partition.

Result:

```text
One partition lag explodes while others are idle.
```

Fix:

```text
Use aggregation, bucketed keys, or special hot-key path.
```

Lesson:

```text
Good partition keys balance ordering and distribution.
```

---

## 25. Debugging Mindset

When event-driven flow breaks, ask:

```text
Was the event produced?
Which topic?
Which partition and offset?
Was event schema valid?
Which consumer group should process it?
Is consumer lag growing?
Did consumer fail or skip?
Was eventId already processed?
Did DB side effect commit?
Was offset committed?
Did event go to retry or DLT?
```

Debug map:

```text
No analytics update:
    check producer logs
    check Kafka topic
    check consumer lag
    check consumer errors
    check processed_events
    check analytics DB

Duplicate analytics:
    check eventId uniqueness
    check processed_events transaction
    check offset commit timing

Lag growing:
    check DB latency
    check consumer concurrency
    check partition count
    check hot keys

DLT increasing:
    check schema changes
    check bad producer deployment
    check unsupported eventVersion
```

Useful queries:

```sql
SELECT *
FROM processed_events
WHERE event_id = 'evt_123';
```

```sql
SELECT short_code, day, click_count
FROM url_daily_analytics
WHERE short_code = 'abc123';
```

Useful logs:

```text
eventId
topic
partition
offset
shortCode
consumerGroup
processingLatencyMs
errorClass
```

Golden rule:

```text
Debug event-driven systems by following the event, not the call stack.
```

---

## 26. Common Mistakes

### Mistake 1: Treating events as commands

Wrong:

```text
SendAnalyticsUpdateCommand
```

Correct:

```text
UrlRedirectedEvent
```

### Mistake 2: Blocking user path on side effects

Wrong:

```text
Redirect waits for analytics DB.
```

Correct:

```text
Redirect publishes event and returns fast.
```

### Mistake 3: No idempotency

Wrong:

```text
Every consumed event increments count blindly.
```

Correct:

```text
Use eventId deduplication.
```

### Mistake 4: Publishing event before DB commit

Wrong:

```text
publish ShortUrlCreated, then commit DB
```

Correct:

```text
use outbox for DB state change events
```

### Mistake 5: One giant topic

Wrong:

```text
events
```

Correct:

```text
url.redirected.v1
url.created.v1
url.lifecycle.v1
```

### Mistake 6: No DLT

Wrong:

```text
poison message retries forever
```

Correct:

```text
send permanent failures to DLT
```

### Mistake 7: Ignoring consumer lag

Wrong:

```text
Kafka accepted messages, so system is healthy.
```

Correct:

```text
Monitor lag and oldest unprocessed event age.
```

### Mistake 8: No schema version

Wrong:

```json
{"shortCode":"abc123"}
```

Correct:

```json
{"eventVersion":1,"eventType":"UrlRedirected","shortCode":"abc123"}
```

---

## 27. Interview-Ready Explanation

If interviewer asks:

```text
How would you use event-driven architecture in a URL shortener?
```

Strong answer:

```text
I would keep the redirect path focused on redirect-critical work: validate the short
code, resolve the target URL from cache or DB, check status and expiry, then return
302 quickly. Side effects such as click analytics, audit logging, dashboard updates,
and abuse detection should be event-driven. After a successful redirect, the service
publishes a UrlRedirected event to Kafka with a stable eventId, eventVersion,
occurredAt, shortCode, urlId, requestId, and privacy-safe metadata. Analytics,
audit, and dashboard consumers process the event independently.

For DB state changes like ShortUrlCreated or ShortUrlBlocked, I would use the outbox
pattern so the database write and event record are committed atomically. Consumers
must be idempotent because Kafka can redeliver messages; I would use eventId plus a
processed_events table to avoid duplicate side effects. I would key redirect events
by shortCode for per-link ordering, while watching for hot keys. Temporary failures
should retry with backoff, permanent poison messages should go to DLT, and the system
must monitor consumer lag, retry counts, DLT count, and event processing latency.
```

Why this is strong:

```text
1. Separates critical path from side effects.
2. Uses events as facts.
3. Mentions Kafka topic design.
4. Handles DB/event atomicity with outbox.
5. Handles duplicate delivery with idempotency.
6. Understands ordering and hot partitions.
7. Includes retry and DLT.
8. Includes observability and lag.
9. Shows senior production tradeoff thinking.
```

Senior one-liner:

```text
I use events to broadcast business facts and let independent consumers process side effects asynchronously, reliably, and idempotently.
```

---

## 28. Senior Engineer Checklist

Before calling your URL shortener event-driven design production-shaped, confirm:

```text
[ ] Event names are past-tense facts
[ ] Topic names are domain-specific and versioned
[ ] UrlRedirected event has eventId
[ ] Event has eventVersion
[ ] Event has occurredAt
[ ] Event has requestId/correlationId
[ ] Kafka key choice is intentional
[ ] Redirect path does not wait for analytics DB
[ ] Consumers are idempotent
[ ] processed_events table exists
[ ] Offset commits happen after safe processing
[ ] Retry strategy exists for temporary failures
[ ] DLT exists for poison messages
[ ] Consumer lag is monitored
[ ] Oldest unprocessed event age is monitored
[ ] Producer failures are logged/alerted
[ ] Outbox is used for critical DB state change events
[ ] Schema evolution is planned
[ ] Privacy-sensitive metadata is handled carefully
[ ] Hot key strategy exists for viral short URLs
[ ] Integration tests verify duplicate delivery behavior
```

---

## 29. One-Page Cheat Sheet

```text
Core mental model:
Event-driven architecture = business fact broadcasting.

Event:
Something that already happened.
Use past tense:
UrlRedirected, ShortUrlCreated, ShortUrlBlocked.

Use events when:
side effect can happen later
multiple consumers need same fact
consumer failure should not break API
work needs retry/replay

Avoid events when:
client needs immediate answer
strong consistency is required before response
operation is simple and local

MiniURLShortener events:
ShortUrlCreated
UrlRedirected
ShortUrlBlocked
ShortUrlDeleted
AnalyticsAggregated

Topic naming:
url.redirected.v1
url.created.v1
url.redirected.retry.v1
url.redirected.dlt.v1

Event fields:
eventId
eventType
eventVersion
occurredAt
requestId
shortCode
urlId
metadata

Producer rule:
Publish facts, not instructions.

Consumer rule:
Be idempotent.
At-least-once delivery means duplicates can happen.

Outbox:
Use when DB write and event must be atomic.

Ordering:
Kafka orders within partition only.
Key by shortCode for per-link order.
Watch hot keys.

Retry:
Temporary failure -> retry/backoff.
Permanent bad message -> DLT.

Backpressure:
Lag means consumers are slower than producers.
Kafka buffers work but does not remove work.

Observability:
Monitor producer errors, consumer lag, DLT count,
retry count, processing latency, oldest event age.
```

---

## 30. One Picture To Remember

```text
                 EVENT-DRIVEN ARCHITECTURE MENTAL MODEL

                        "Something happened"

User Request
    |
    v
+-------------------------+
| Redirect Service        |
| critical path only      |
+-------------------------+
    |
    +------------------------------+
    | return 302 fast              |
    v                              v
Client                       +----------------------+
                             | UrlRedirected Event  |
                             | eventId, shortCode   |
                             +----------------------+
                                       |
                                       v
                              +----------------+
                              | Kafka Topic    |
                              | durable log    |
                              +----------------+
                                 /       |       \
                                v        v        v
                         +----------+ +------+ +-----------+
                         |Analytics | |Audit | |Dashboard  |
                         |Consumer  | |Worker| |Consumer   |
                         +----------+ +------+ +-----------+
                              |
                              v
                    +----------------------+
                    | Idempotent write     |
                    | processed_events     |
                    | analytics tables     |
                    +----------------------+

FINAL MEMORY:

The request path creates the fact.
Kafka stores the fact.
Consumers react to the fact.
Idempotency protects against duplicate facts.
Outbox protects DB/event atomicity.
Lag tells you consumers are falling behind.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Event-driven architecture broadcasts business facts so side effects do not block the main request.
2. Events should be past-tense facts like UrlRedirected, not commands like UpdateAnalytics.
3. Kafka gives durable buffering, replay, consumer groups, and partitioned ordering.
4. Consumers must be idempotent because events can be delivered more than once.
5. Use outbox when a database state change and event publication must succeed together.
```

Next chapter:

```text
036_Analytics_Aggregation.md
```
