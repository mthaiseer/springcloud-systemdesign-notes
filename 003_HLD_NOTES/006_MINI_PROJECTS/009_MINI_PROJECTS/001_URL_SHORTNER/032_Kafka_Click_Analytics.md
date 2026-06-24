# 032_Kafka_Click_Analytics.md
# MiniURLShortener — Kafka Click Analytics

> Core mental model: **A redirect request must stay fast, while click analytics can be processed later. Kafka is the buffer between the hot redirect path and the slower analytics pipeline.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Redirect Path vs Analytics Path](#4-redirect-path-vs-analytics-path)
- [5. Click Event Mental Model](#5-click-event-mental-model)
- [6. Kafka Topic Design](#6-kafka-topic-design)
- [7. Partition Key Design](#7-partition-key-design)
- [8. Event Schema Design](#8-event-schema-design)
- [9. Producer Flow In Redirect API](#9-producer-flow-in-redirect-api)
- [10. Fire-And-Forget vs Reliable Send](#10-fire-and-forget-vs-reliable-send)
- [11. Consumer Group Mental Model](#11-consumer-group-mental-model)
- [12. Click Aggregation Models](#12-click-aggregation-models)
- [13. Database Tables For Analytics](#13-database-tables-for-analytics)
- [14. Idempotency And Duplicate Events](#14-idempotency-and-duplicate-events)
- [15. Offset Commit Strategy](#15-offset-commit-strategy)
- [16. Retry Topic And Dead Letter Topic](#16-retry-topic-and-dead-letter-topic)
- [17. Backpressure And Lag](#17-backpressure-and-lag)
- [18. Ordering Guarantees](#18-ordering-guarantees)
- [19. Spring Boot Producer Code](#19-spring-boot-producer-code)
- [20. Spring Boot Consumer Code](#20-spring-boot-consumer-code)
- [21. Step-by-Step Dry Runs](#21-step-by-step-dry-runs)
- [22. Internal Execution Walkthrough](#22-internal-execution-walkthrough)
- [23. Production Failure Stories](#23-production-failure-stories)
- [24. Debugging Mindset](#24-debugging-mindset)
- [25. Testing Strategy](#25-testing-strategy)
- [26. Common Mistakes](#26-common-mistakes)
- [27. Interview-Ready Explanation](#27-interview-ready-explanation)
- [28. Senior Engineer Checklist](#28-senior-engineer-checklist)
- [29. One-Page Cheat Sheet](#29-one-page-cheat-sheet)
- [30. One Picture To Remember](#30-one-picture-to-remember)

---

## 1. Why This Exists

In MiniURLShortener, the redirect endpoint is the hottest path:

```text
GET /abc123
```

The user expects this path to be extremely fast:

```text
browser clicks short URL
server finds long URL
server returns 302 redirect
browser moves to destination
```

But product teams also want analytics:

```text
how many clicks did this link get?
clicks by hour
clicks by country
clicks by device
clicks by referrer
unique visitors approximately
spam or bot patterns
campaign performance
```

The mistake is to update analytics synchronously inside the redirect request.

Bad design:

```text
GET /abc123
  -> lookup URL
  -> insert click row
  -> update daily counter
  -> update country counter
  -> update device counter
  -> wait for DB
  -> return redirect
```

This makes every redirect slower and more fragile. If the analytics database is slow, users feel it. If analytics fails, redirect may fail. That is the wrong dependency direction.

Better design:

```text
GET /abc123
  -> lookup URL
  -> publish ClickEvent to Kafka
  -> return redirect quickly

Kafka consumer later:
  -> read ClickEvent
  -> aggregate clicks
  -> write analytics tables
```

Production memory:

```text
Redirect is user-facing and latency-sensitive.
Analytics is business-facing and eventually consistent.
Kafka separates them.
```

---

## 2. The One Core Mental Model

Kafka click analytics is an:

```text
ASYNC EVENT PIPELINE
```

The redirect service emits facts. Analytics consumers process those facts later.

ASCII:

```text
                 HOT PATH                         COLD / ASYNC PATH

Browser              Redirect Service                 Kafka                  Analytics Worker
  |                         |                            |                            |
  | GET /abc123             |                            |                            |
  |------------------------>|                            |                            |
  |                         | lookup longUrl             |                            |
  |                         |--------------------------->| click-events topic         |
  |                         |                            |--------------------------->|
  | 302 Location            |                            |                            | aggregate/write DB
  |<------------------------|                            |                            |
```

Core idea:

```text
Do not make the redirect request wait for analytics.
Emit a click event and let another worker handle it.
```

One-line memory:

```text
Kafka turns click tracking from blocking work into buffered background work.
```

For MiniURLShortener:

```text
Redirect service owns redirect correctness.
Kafka owns durable event buffering.
Analytics worker owns counters and reporting tables.
```

---

## 3. Problem Statement

Build a production-shaped click analytics pipeline for MiniURLShortener.

It must support:

```text
1. Emit one ClickEvent for each successful redirect.
2. Keep redirect latency low.
3. Avoid breaking redirect if analytics is temporarily down.
4. Process click events in consumers.
5. Aggregate total clicks and time-bucketed clicks.
6. Handle duplicate events safely.
7. Retry transient failures.
8. Send poisoned events to a dead-letter topic.
9. Monitor consumer lag.
10. Explain ordering and partitioning tradeoffs.
```

It should avoid:

```text
synchronous analytics writes in redirect path
one Kafka partition bottleneck
random partition keys with no locality
non-idempotent counters
committing offsets before DB write
infinite poison-message retries
logging sensitive data carelessly
```

Out of scope for this chapter:

```text
1. Full Kafka cluster operations.
2. Kafka Streams windowing implementation.
3. Real GeoIP database integration.
4. Exact unique user analytics at massive scale.
5. Click fraud detection ML.
```

This chapter focuses on the backend engineering model needed for interviews and production design.

---

## 4. Redirect Path vs Analytics Path

A URL shortener has two different paths.

Redirect path:

```text
latency sensitive
user-facing
must be fast
must be highly available
should do minimum work
```

Analytics path:

```text
eventually consistent
business-facing
can be delayed by seconds/minutes
can retry
can batch
can tolerate temporary lag
```

ASCII:

```text
                 REDIRECT PATH

GET /abc123
   |
   v
validate shortCode
   |
   v
cache/DB lookup
   |
   v
return 302 quickly


                 ANALYTICS PATH

ClickEvent
   |
   v
Kafka topic
   |
   v
consumer group
   |
   v
aggregate tables
   |
   v
dashboard/API
```

The redirect endpoint should not directly update multiple analytics tables because each extra write increases p99 latency.

At 100 requests per second, synchronous analytics may look fine.

At 50,000 requests per second, it becomes dangerous:

```text
50k redirects/sec
x 3 analytics writes
= 150k extra DB writes/sec
```

If the analytics DB slows down, the redirect API slows down. That creates a user-facing incident from a non-critical feature.

Correct senior mindset:

```text
Separate critical user path from non-critical business processing.
```

---

## 5. Click Event Mental Model

A click event is a fact that already happened.

Example:

```text
User clicked shortCode abc123 at 2026-06-24T10:00:00Z.
```

It should be immutable.

Do not publish commands like:

```text
IncrementClickCounter
```

Prefer publishing facts like:

```text
ClickRecorded
```

Why?

Because a fact can support many future consumers:

```text
analytics counter consumer
fraud detection consumer
billing consumer
campaign reporting consumer
real-time dashboard consumer
```

ASCII:

```text
                    ClickEvent fact
                          |
          +---------------+---------------+
          |               |               |
          v               v               v
   Counter Worker   Fraud Worker   Dashboard Worker
```

Event thinking:

```text
Producer says what happened.
Consumers decide what to do with it.
```

Minimum click event fields:

```text
eventId
shortCode
urlId
clickedAt
ipHash
userAgent
referer
country/device later
```

Privacy mindset:

```text
Do not store raw IP by default if hash is enough.
Do not log full referrers if they may contain tokens.
```

---

## 6. Kafka Topic Design

Start with one main topic:

```text
url-click-events-v1
```

Retry topics:

```text
url-click-events-retry-v1
url-click-events-dlt-v1
```

Topic model:

```text
+----------------------------+
| url-click-events-v1        |
| partitions: 12 or more     |
| key: shortCode or urlId    |
| value: ClickEvent JSON     |
+----------------------------+
```

ASCII:

```text
Topic: url-click-events-v1

Partition 0:  [event][event][event]
Partition 1:  [event][event][event]
Partition 2:  [event][event][event]
Partition 3:  [event][event][event]
...
Partition 11: [event][event][event]
```

Why multiple partitions?

```text
Kafka parallelism comes from partitions.
More partitions allow more consumers in same group to process in parallel.
```

Consumer group example:

```text
analytics-consumer-group
```

If topic has 12 partitions and 4 consumers:

```text
each consumer gets about 3 partitions
```

If topic has 12 partitions and 20 consumers:

```text
only 12 consumers are active for that topic
8 consumers sit idle
```

Rule:

```text
Maximum parallelism of one consumer group for one topic = number of partitions.
```

---

## 7. Partition Key Design

Kafka chooses partition based on message key.

Common choices:

```text
key = shortCode
key = urlId
key = random eventId
```

For click analytics, prefer:

```text
key = urlId or shortCode
```

Why?

Events for the same short URL go to the same partition. This preserves per-link ordering and makes reasoning easier.

ASCII:

```text
key = abc123
     |
     v
hash(abc123) % partitions = 2
     |
     v
Partition 2

All abc123 click events go to Partition 2.
```

Tradeoff:

```text
Good: per-link ordering
Bad: hot links can create hot partitions
```

Hot key example:

```text
normal link: 10 clicks/sec
viral link: 100,000 clicks/sec
```

If all viral-link events go to one partition, one consumer may become overloaded.

Alternative for very high scale:

```text
key = shortCode + shardBucket
```

Example:

```text
abc123:0
abc123:1
abc123:2
abc123:3
```

This spreads one hot shortCode across multiple partitions, but loses strict ordering for that shortCode.

Senior tradeoff:

```text
For normal scale, key by urlId/shortCode.
For viral hot links, shard the key and accept weaker ordering.
```

---

## 8. Event Schema Design

ClickEvent should be explicit and versioned.

Example JSON:

```json
{
  "eventId": "01J0CLICK9J7P4X8Y2H6Z",
  "eventType": "URL_CLICKED",
  "schemaVersion": 1,
  "urlId": 12345,
  "shortCode": "abc123",
  "clickedAt": "2026-06-24T10:00:00Z",
  "ipHash": "d6f4a8...",
  "userAgent": "Mozilla/5.0",
  "referer": "https://example.com/page",
  "requestId": "req-789",
  "producer": "miniurl-redirect-service"
}
```

Important fields:

```text
eventId       -> idempotency and debugging
schemaVersion -> safe event evolution
urlId         -> stable internal identifier
shortCode     -> human/debug identifier
clickedAt     -> event time
requestId     -> trace correlation
```

Schema evolution rule:

```text
Add fields safely.
Do not remove or rename fields without a new schema version.
```

Bad schema:

```json
{
  "code": "abc123",
  "time": "now"
}
```

Why bad?

```text
ambiguous fields
no event ID
no schema version
hard to deduplicate
hard to evolve
hard to debug
```

Good event design makes future analytics easier.

---

## 9. Producer Flow In Redirect API

Redirect service flow:

```text
GET /{shortCode}
```

ASCII:

```text
Client
  |
  v
RedirectController
  |
  v
RedirectService
  |
  +--> lookup longUrl from Redis/Postgres
  |
  +--> build ClickEvent
  |
  +--> publish to Kafka
  |
  v
302 Redirect
```

Important: publishing should not dominate redirect latency.

Two common patterns:

### Pattern A: Best-effort async send

```text
send event asynchronously
return redirect without waiting for broker ack
```

Good for low latency.

Risk:

```text
some click events may be lost if producer fails before Kafka accepts them
```

### Pattern B: Wait for Kafka ack briefly

```text
send event
wait for Kafka ack within small timeout
then return redirect
```

Good for stronger analytics durability.

Risk:

```text
Kafka latency affects redirect p99
```

Practical v1 choice:

```text
Best-effort async send with error logging.
Do not fail redirect because analytics event failed.
```

Why?

```text
Redirect correctness is more important than perfect click counting.
```

---

## 10. Fire-And-Forget vs Reliable Send

This is the central tradeoff.

```text
Fire-and-forget:
    fastest redirect
    possible analytics loss

Reliable send:
    stronger analytics
    higher redirect latency
```

Decision table:

```text
+---------------------------+----------------------+----------------------+
| Choice                    | Pros                 | Cons                 |
+---------------------------+----------------------+----------------------+
| Async no wait             | lowest latency       | possible event loss  |
| Async callback logging    | low latency          | still possible loss  |
| Wait for Kafka ack        | stronger durability  | higher p99 latency   |
| Outbox pattern            | strongest durability | more complexity      |
+---------------------------+----------------------+----------------------+
```

For MiniURLShortener phase 032:

```text
Use async Kafka send.
Log failures.
Do not block redirect.
Mention outbox as future upgrade.
```

Outbox future model:

```text
Redirect transaction writes click_outbox row.
Background publisher sends row to Kafka.
```

But for a high-QPS redirect endpoint, writing every click to outbox in the primary DB may also become expensive.

Senior answer:

```text
For business-critical events like payment, use outbox.
For best-effort click analytics, async Kafka publish is often acceptable.
```

---

## 11. Consumer Group Mental Model

A Kafka consumer group lets multiple workers share topic partitions.

ASCII:

```text
Topic: url-click-events-v1

P0 ----> Consumer A
P1 ----> Consumer A
P2 ----> Consumer B
P3 ----> Consumer B
P4 ----> Consumer C
P5 ----> Consumer C
```

Within one consumer group:

```text
one partition is consumed by only one consumer at a time
```

Across different groups:

```text
each group gets its own copy of the stream
```

Example:

```text
analytics-counter-group reads all click events
fraud-detection-group reads all click events
billing-group reads all click events
```

ASCII:

```text
                   url-click-events-v1
                          |
          +---------------+----------------+
          |               |                |
          v               v                v
 analytics group    fraud group      dashboard group
```

Consumer group is not just scaling. It is also ownership.

```text
Each group represents one independent purpose.
```

---

## 12. Click Aggregation Models

There are two main analytics models.

### Model 1: Raw click storage

Store every click event.

```text
click_events table
```

Pros:

```text
flexible reprocessing
debugging
new analytics later
```

Cons:

```text
huge storage
expensive queries
privacy concerns
```

### Model 2: Aggregated counters

Update counters by URL and time bucket.

```text
url_click_stats_daily
url_click_stats_hourly
```

Pros:

```text
fast dashboard queries
less storage
simpler reporting
```

Cons:

```text
less detailed history
harder to recompute if logic changes
```

Practical v1:

```text
Store aggregated counters.
Optionally store raw events for limited retention.
```

ASCII:

```text
ClickEvent stream
      |
      v
+---------------------+
| Consumer            |
+---------------------+
      |
      +--> raw_click_events        optional / short retention
      |
      +--> url_click_stats_hourly  dashboard
      |
      +--> url_click_stats_daily   dashboard
```

At high scale, counters may need batching or Redis pre-aggregation before DB flush.

---

## 13. Database Tables For Analytics

A simple hourly table:

```sql
CREATE TABLE url_click_stats_hourly (
    url_id BIGINT NOT NULL,
    bucket_hour TIMESTAMPTZ NOT NULL,
    total_clicks BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (url_id, bucket_hour)
);
```

Upsert increment:

```sql
INSERT INTO url_click_stats_hourly (url_id, bucket_hour, total_clicks, updated_at)
VALUES (:urlId, :bucketHour, 1, now())
ON CONFLICT (url_id, bucket_hour)
DO UPDATE SET
    total_clicks = url_click_stats_hourly.total_clicks + 1,
    updated_at = now();
```

Daily table:

```sql
CREATE TABLE url_click_stats_daily (
    url_id BIGINT NOT NULL,
    bucket_date DATE NOT NULL,
    total_clicks BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (url_id, bucket_date)
);
```

Event dedup table:

```sql
CREATE TABLE processed_click_events (
    event_id VARCHAR(64) PRIMARY KEY,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
```

Flow:

```text
consumer receives event
  |
  v
insert eventId into processed_click_events
  |
  +-- duplicate -> skip
  |
  v
increment hourly/daily counters
  |
  v
commit Kafka offset
```

For very high write volume, a dedup table for every click can be expensive. Use it when duplicates are costly. For approximate analytics, duplicates may be acceptable within small error bounds.

---

## 14. Idempotency And Duplicate Events

Kafka consumers can process a message more than once.

Why duplicates happen:

```text
consumer processes DB write
consumer crashes before offset commit
consumer restarts
same Kafka message is read again
```

ASCII:

```text
Read event E1
   |
   v
DB counter +1 succeeds
   |
   v
CRASH before offset commit
   |
   v
Restart
   |
   v
Read event E1 again
   |
   v
DB counter +1 again  <-- duplicate count
```

Solution options:

```text
1. Accept small overcount for analytics.
2. Deduplicate using eventId table.
3. Use idempotent aggregation design.
4. Use exactly-once stream processing in advanced systems.
```

For MiniURLShortener v1:

```text
Use eventId and processed_click_events for clear interview-quality design.
```

Transaction idea:

```text
BEGIN
  INSERT eventId into processed_click_events
  IF inserted:
      increment counters
COMMIT
commit Kafka offset
```

If duplicate event arrives:

```text
INSERT eventId fails due to primary key
skip counter update
commit offset
```

Golden rule:

```text
Make the side effect idempotent before committing the offset.
```

---

## 15. Offset Commit Strategy

Kafka offset means:

```text
consumer's progress position in a partition
```

Bad strategy:

```text
commit offset before database write
```

Failure:

```text
offset committed
DB write fails
message will not be retried
click is lost
```

Better strategy:

```text
process DB write first
then commit offset
```

ASCII:

```text
Correct order:

1. Poll event
2. Validate event
3. Write analytics DB
4. Commit offset
```

Spring Kafka can do manual ack:

```text
@KafkaListener
process event
ack.acknowledge()
```

Only acknowledge after successful processing.

If processing fails:

```text
do not ack
retry or send to retry/DLT depending on error handler
```

Senior mental model:

```text
Offset commit is the consumer saying: I am done with this message.
Do not say it before the side effect is safe.
```

---

## 16. Retry Topic And Dead Letter Topic

Some failures are temporary:

```text
DB timeout
network issue
connection pool exhausted
temporary lock timeout
```

Some failures are permanent:

```text
invalid JSON
missing urlId
bad schema version
poison event
```

Retry model:

```text
main topic -> retry topic -> DLT if still failing
```

ASCII:

```text
url-click-events-v1
        |
        v
Analytics Consumer
        |
        +-- success --> commit
        |
        +-- transient failure --> url-click-events-retry-v1
        |
        +-- permanent failure --> url-click-events-dlt-v1

url-click-events-retry-v1
        |
        v
Retry Consumer
        |
        +-- success --> commit
        +-- fail too many times --> url-click-events-dlt-v1
```

Why not retry forever?

```text
A poison message can block a partition forever.
```

DLT purpose:

```text
Preserve failed events for inspection without blocking healthy traffic.
```

DLT should include metadata:

```text
original topic
original partition
original offset
exception class
failure reason
failedAt
event payload
```

Production rule:

```text
Retry transient errors. Quarantine poison events.
```

---

## 17. Backpressure And Lag

Consumer lag means:

```text
Kafka has messages that consumer has not processed yet.
```

Example:

```text
producer writes 50,000 clicks/sec
consumer processes 30,000 clicks/sec
lag grows by 20,000/sec
```

ASCII:

```text
Kafka partition:

[processed][processed][processed][waiting][waiting][waiting][waiting]
                              ^
                              consumer offset

Lag = messages after consumer offset
```

Causes of lag:

```text
consumer too slow
DB writes too slow
too few partitions
too few consumers
hot partition
large message payloads
frequent retries
```

Fixes:

```text
batch DB writes
increase consumers up to partition count
increase partitions carefully
optimize DB indexes/upserts
use Redis pre-aggregation
split hot keys
move raw event storage to cheaper store
```

Important:

```text
Adding consumers beyond partition count does not help one consumer group.
```

Lag is acceptable if bounded.

Lag is dangerous if always increasing.

---

## 18. Ordering Guarantees

Kafka ordering is guaranteed only within a partition.

```text
Same partition -> ordered
Different partitions -> no global order
```

ASCII:

```text
Partition 0: E1 -> E3 -> E5
Partition 1: E2 -> E4 -> E6

No global guarantee that E3 is processed before E2.
```

If key = shortCode:

```text
all events for same shortCode go to same partition
per-shortCode ordering exists
```

If key = random eventId:

```text
events for same shortCode spread across partitions
more parallelism
no per-shortCode ordering
```

For click counters, exact order usually does not matter because increment is commutative:

```text
+1 then +1 = +2
+1 after +1 = +2
```

For time-bucket analytics, use event timestamp:

```text
clickedAt determines bucket
processing time should not decide user click time
```

Senior distinction:

```text
Event time = when click happened.
Processing time = when worker processed it.
```

Use event time for analytics buckets.

---

## 19. Spring Boot Producer Code

Event DTO:

```java
package com.miniurl.shortener.analytics.event;

import java.time.Instant;

public class ClickEvent {

    private String eventId;
    private String eventType;
    private int schemaVersion;
    private Long urlId;
    private String shortCode;
    private Instant clickedAt;
    private String ipHash;
    private String userAgent;
    private String referer;
    private String requestId;
    private String producer;

    // getters and setters omitted for brevity in real code generate them
}
```

Producer service:

```java
package com.miniurl.shortener.analytics.producer;

import com.miniurl.shortener.analytics.event.ClickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClickEventProducer {

    private static final Logger log = LoggerFactory.getLogger(ClickEventProducer.class);
    private static final String TOPIC = "url-click-events-v1";

    private final KafkaTemplate<String, ClickEvent> kafkaTemplate;

    public ClickEventProducer(KafkaTemplate<String, ClickEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ClickEvent event) {
        String key = event.getShortCode();

        kafkaTemplate.send(TOPIC, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.warn(
                                "click_event_publish_failed shortCode={} eventId={} error={}",
                                event.getShortCode(),
                                event.getEventId(),
                                ex.toString()
                        );
                        return;
                    }

                    log.debug(
                            "click_event_published shortCode={} eventId={} partition={} offset={}",
                            event.getShortCode(),
                            event.getEventId(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset()
                    );
                });
    }
}
```

Redirect service usage:

```java
public RedirectResult redirect(String shortCode, HttpServletRequest request) {
    ShortUrl url = shortUrlLookupService.findActive(shortCode);

    ClickEvent event = clickEventFactory.from(url, request);

    // Best-effort. Do not fail redirect if analytics publishing fails.
    clickEventProducer.publish(event);

    return RedirectResult.to(url.getLongUrl());
}
```

Important:

```text
The producer logs publish failure but does not throw into redirect path.
```

---

## 20. Spring Boot Consumer Code

Consumer:

```java
package com.miniurl.shortener.analytics.consumer;

import com.miniurl.shortener.analytics.event.ClickEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class ClickAnalyticsConsumer {

    private final ClickAnalyticsService clickAnalyticsService;

    public ClickAnalyticsConsumer(ClickAnalyticsService clickAnalyticsService) {
        this.clickAnalyticsService = clickAnalyticsService;
    }

    @KafkaListener(
            topics = "url-click-events-v1",
            groupId = "click-analytics-consumer-group"
    )
    public void consume(ClickEvent event, Acknowledgment acknowledgment) {
        clickAnalyticsService.process(event);

        // acknowledge only after DB side effects are complete
        acknowledgment.acknowledge();
    }
}
```

Service:

```java
package com.miniurl.shortener.analytics.consumer;

import com.miniurl.shortener.analytics.event.ClickEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class ClickAnalyticsService {

    private final ProcessedClickEventRepository processedRepo;
    private final ClickStatsRepository statsRepo;

    public ClickAnalyticsService(
            ProcessedClickEventRepository processedRepo,
            ClickStatsRepository statsRepo
    ) {
        this.processedRepo = processedRepo;
        this.statsRepo = statsRepo;
    }

    @Transactional
    public void process(ClickEvent event) {
        validate(event);

        boolean firstTime = processedRepo.tryInsert(event.getEventId());

        if (!firstTime) {
            return;
        }

        Instant bucketHour = event.getClickedAt().truncatedTo(ChronoUnit.HOURS);

        statsRepo.incrementHourly(event.getUrlId(), bucketHour);
        statsRepo.incrementDaily(event.getUrlId(), bucketHour.atZone(java.time.ZoneOffset.UTC).toLocalDate());
    }

    private void validate(ClickEvent event) {
        if (event.getEventId() == null || event.getEventId().isBlank()) {
            throw new IllegalArgumentException("eventId is required");
        }
        if (event.getUrlId() == null) {
            throw new IllegalArgumentException("urlId is required");
        }
        if (event.getClickedAt() == null) {
            throw new IllegalArgumentException("clickedAt is required");
        }
    }
}
```

Repository idea with JDBC:

```java
public boolean tryInsert(String eventId) {
    int rows = jdbcTemplate.update("""
        INSERT INTO processed_click_events(event_id, processed_at)
        VALUES (?, now())
        ON CONFLICT (event_id) DO NOTHING
        """, eventId);

    return rows == 1;
}
```

Counter increment:

```java
public void incrementHourly(Long urlId, Instant bucketHour) {
    jdbcTemplate.update("""
        INSERT INTO url_click_stats_hourly(url_id, bucket_hour, total_clicks, updated_at)
        VALUES (?, ?, 1, now())
        ON CONFLICT (url_id, bucket_hour)
        DO UPDATE SET
            total_clicks = url_click_stats_hourly.total_clicks + 1,
            updated_at = now()
        """, urlId, bucketHour);
}
```

---

## 21. Step-by-Step Dry Runs

### Dry Run 1: Successful Click Event

Request:

```http
GET /abc123
```

Flow:

```text
1. Redirect service finds urlId=101 and longUrl=https://example.com.
2. Service builds ClickEvent with eventId=E1.
3. Producer sends event to Kafka with key=abc123.
4. Kafka stores event in partition 4.
5. Redirect service returns 302 immediately.
6. Analytics consumer reads E1.
7. Consumer inserts E1 into processed_click_events.
8. Consumer increments hourly and daily counters.
9. Consumer commits offset.
```

Result:

```text
User experiences fast redirect.
Dashboard eventually shows +1 click.
```

---

### Dry Run 2: Kafka Publish Fails

Failure:

```text
Kafka broker temporarily unavailable
```

Flow:

```text
1. Redirect service finds long URL successfully.
2. Producer attempts async send.
3. Send callback receives exception.
4. Application logs click_event_publish_failed.
5. Redirect response still returns 302.
```

Result:

```text
User is not affected.
One analytics event may be lost.
```

Why acceptable in v1:

```text
Click analytics is useful but not as critical as redirect correctness.
```

---

### Dry Run 3: Consumer DB Failure

Failure:

```text
analytics DB timeout
```

Flow:

```text
1. Consumer reads event E2.
2. Consumer tries to write DB counters.
3. DB timeout occurs.
4. Consumer does not acknowledge offset.
5. Error handler retries or publishes to retry topic.
6. Event is processed later.
```

Result:

```text
Temporary DB failure does not lose event if offset is not committed early.
```

---

### Dry Run 4: Duplicate Event After Crash

Flow:

```text
1. Consumer reads E3.
2. Consumer inserts E3 into processed_click_events.
3. Consumer increments counters.
4. Application crashes before offset commit.
5. Consumer restarts and reads E3 again.
6. processed_click_events already contains E3.
7. Consumer skips counter increment.
8. Consumer commits offset.
```

Result:

```text
Counter is incremented once, not twice.
```

---

### Dry Run 5: Poison Event

Bad event:

```json
{
  "eventId": "E4",
  "shortCode": "abc123"
}
```

Missing:

```text
urlId
clickedAt
```

Flow:

```text
1. Consumer reads E4.
2. Validation fails.
3. This is not a transient DB issue.
4. Event is sent to DLT.
5. Consumer continues with next events.
```

Result:

```text
Bad event does not block the partition forever.
```

---

## 22. Internal Execution Walkthrough

End-to-end:

```text
1. Browser sends GET /abc123.
2. Tomcat receives request.
3. DispatcherServlet routes to RedirectController.
4. RedirectService validates shortCode.
5. Service reads Redis cache or Postgres.
6. Service confirms URL is active and not expired.
7. Service creates ClickEvent.
8. KafkaTemplate serializes event.
9. Producer partitioner hashes shortCode.
10. Event is appended to Kafka partition log.
11. Redirect response is returned as HTTP 302.
12. Consumer group polls Kafka.
13. One consumer receives event from assigned partition.
14. Consumer validates schema and required fields.
15. Consumer performs idempotent DB transaction.
16. Consumer acknowledges offset.
17. Dashboard reads aggregate table later.
```

ASCII:

```text
+---------+      +------------------+      +--------------+
| Browser | ---> | Redirect Service | ---> | Kafka Topic  |
+---------+      +------------------+      +--------------+
     ^                    |                       |
     |                    v                       v
     |              302 Redirect           Analytics Consumer
     |                                            |
     |                                            v
     |                                    Analytics Tables
     |                                            |
     +--------------------------------------------+
                  dashboard later
```

Important separation:

```text
The redirect request does not wait for analytics table updates.
```

---

## 23. Production Failure Stories

### Failure Story 1: Analytics DB slows down redirects

Bad design:

```text
Redirect endpoint directly updates click_stats table.
```

Incident:

```text
Marketing campaign creates 30k redirects/sec.
click_stats table row for one viral URL becomes hot.
DB update latency rises.
Redirect p99 jumps from 40ms to 2s.
Users complain short links are slow.
```

Fix:

```text
Move analytics writes behind Kafka.
Redirect emits event and returns quickly.
Consumers aggregate asynchronously.
```

Lesson:

```text
Never let non-critical analytics writes sit on the critical redirect path.
```

---

### Failure Story 2: Poison message blocks partition

Bad consumer:

```text
Retries invalid event forever.
```

Incident:

```text
One malformed event keeps failing.
Partition cannot progress.
Lag grows for all later events in that partition.
```

Fix:

```text
Classify permanent errors and send them to DLT.
```

Lesson:

```text
Retry is useful only for recoverable errors.
Poison events need quarantine.
```

---

### Failure Story 3: Duplicate counts after restart

Bad consumer:

```text
Counter update is not idempotent.
```

Incident:

```text
Consumer writes +1, crashes before offset commit, then reprocesses event after restart.
Dashboard overcounts clicks.
```

Fix:

```text
Use eventId and processed_click_events table before incrementing counters.
```

Lesson:

```text
At-least-once delivery means your consumer must handle duplicates.
```

---

### Failure Story 4: Hot partition from viral link

Design:

```text
key = shortCode
```

Incident:

```text
One celebrity posts a short link.
All events for that shortCode go to one partition.
One consumer is overloaded while others are idle.
Lag grows only on one partition.
```

Fix options:

```text
shard hot key: shortCode + bucket
increase partitions for future traffic
pre-aggregate at edge/Redis
special handling for viral links
```

Lesson:

```text
Partition key gives ordering, but can create hot spots.
```

---

### Failure Story 5: Dashboard uses processing time

Bad aggregation:

```text
bucket = now() when consumer processes event
```

Incident:

```text
Kafka lag reaches 2 hours.
Old clicks are counted in the wrong current hour.
Dashboard shows false spike.
```

Fix:

```text
bucket using event.clickedAt, not processing time.
```

Lesson:

```text
Analytics should usually use event time.
```

---

## 24. Debugging Mindset

When click analytics is wrong, ask:

```text
Was the event produced?
Was it accepted by Kafka?
Which topic/partition/offset?
What key was used?
Is consumer group lag increasing?
Did consumer process successfully?
Was offset committed before or after DB write?
Did idempotency skip a duplicate?
Did event go to retry or DLT?
Is dashboard reading event time buckets or processing time buckets?
```

Debug map:

```text
Redirect fast but no analytics:
    producer failure, topic issue, consumer down, DLT, or dashboard query issue

Kafka lag growing:
    consumer slower than producer, DB bottleneck, too few partitions, hot key

Counts too high:
    duplicate processing, missing idempotency, replay without reset

Counts too low:
    producer loss, DLT events, committed offset before DB write, filtering bug

Wrong hourly chart:
    processing time used instead of clickedAt
```

Useful commands/concepts:

```text
consumer group lag
partition assignment
DLT event count
producer error logs
DB upsert latency
slow query logs
```

Golden rule:

```text
Always trace one eventId from redirect log -> Kafka metadata -> consumer log -> analytics DB row.
```

---

## 25. Testing Strategy

Test this as a pipeline, not only as methods.

### Producer tests

```text
redirect emits ClickEvent for successful redirect
blocked/expired/not-found redirect does not emit success click event
event has eventId, urlId, shortCode, clickedAt
Kafka key is shortCode/urlId
```

### Consumer unit tests

```text
valid event increments hourly and daily counters
duplicate event does not increment twice
missing urlId fails validation
old clickedAt goes to correct old bucket
```

### Integration tests

Use Testcontainers for Kafka and Postgres.

```text
start Kafka container
start Postgres container
send ClickEvent
consumer processes it
assert stats table total_clicks = 1
send same event again
assert total_clicks still = 1
```

### Failure tests

```text
DB failure -> offset not acknowledged
invalid event -> DLT
retry exhausted -> DLT
producer failure does not fail redirect
```

Important assertion:

```text
Do not only test that Kafka send was called.
Test that analytics result is correct after consumption.
```

---

## 26. Common Mistakes

### Mistake 1: Updating analytics synchronously

Wrong:

```text
Redirect endpoint updates stats table before returning 302.
```

Correct:

```text
Redirect endpoint publishes ClickEvent and returns quickly.
```

### Mistake 2: Committing offset too early

Wrong:

```text
commit offset, then write DB
```

Correct:

```text
write DB, then commit offset
```

### Mistake 3: No idempotency

Wrong:

```text
every consumed message increments counter blindly
```

Correct:

```text
use eventId to handle duplicate processing
```

### Mistake 4: One partition only

Wrong:

```text
url-click-events topic has 1 partition
```

Correct:

```text
use enough partitions for expected parallelism
```

### Mistake 5: Random key without thinking

Wrong:

```text
key = random eventId for all cases
```

Correct:

```text
choose key based on ordering and hot-key tradeoff
```

### Mistake 6: Infinite retry

Wrong:

```text
bad event retried forever
```

Correct:

```text
retry transient failures, DLT permanent failures
```

### Mistake 7: Using processing time for event buckets

Wrong:

```text
bucket = now()
```

Correct:

```text
bucket = clickedAt truncated to hour/day
```

### Mistake 8: Logging sensitive click data

Wrong:

```text
log raw IP and full referrer with tokens
```

Correct:

```text
hash IP, sanitize referrer, use eventId/requestId for tracing
```

---

## 27. Interview-Ready Explanation

If interviewer asks:

```text
How would you build click analytics for a URL shortener?
```

Strong answer:

```text
I would keep the redirect path fast and separate it from analytics. On each successful redirect, the service creates a versioned ClickEvent containing eventId, urlId, shortCode, clickedAt, requestId, and safe metadata. It publishes the event to a Kafka topic such as url-click-events-v1 using urlId or shortCode as the key, so events for the same link usually land in the same partition. The redirect response should not wait for analytics table updates.

A separate analytics consumer group reads the events, validates them, deduplicates using eventId if exact counting is required, and updates hourly/daily aggregate tables using idempotent upserts. The consumer commits offsets only after the database side effect succeeds. Transient failures go to retry, while malformed poison events go to a dead-letter topic so one bad event does not block a partition. I would monitor consumer lag, DLT count, producer failures, DB upsert latency, and hot partitions. For viral links, I may shard the key or pre-aggregate because keying only by shortCode can create a hot partition.
```

Why this is strong:

```text
1. Separates hot path from async path.
2. Uses Kafka correctly as a durable buffer.
3. Mentions partition key tradeoff.
4. Handles duplicate events.
5. Commits offsets after side effects.
6. Uses retry and DLT.
7. Talks about lag and hot partitions.
8. Uses event time for analytics correctness.
```

Senior one-liner:

```text
A click is an immutable event; Kafka buffers it, consumers aggregate it, and redirect latency stays protected.
```

---

## 28. Senior Engineer Checklist

Before calling Kafka click analytics production-shaped, confirm:

```text
[ ] Redirect emits ClickEvent only after successful active redirect
[ ] Redirect does not synchronously update analytics tables
[ ] ClickEvent has eventId
[ ] ClickEvent has schemaVersion
[ ] ClickEvent has clickedAt event time
[ ] Topic name is versioned
[ ] Topic has enough partitions
[ ] Partition key is intentionally chosen
[ ] Producer failures are logged with eventId/requestId
[ ] Producer failure does not break redirect in v1 design
[ ] Consumer group name is stable
[ ] Consumer validates event schema
[ ] Consumer writes DB before committing offset
[ ] Duplicate event handling exists if exact count needed
[ ] Retry topic exists for transient failures
[ ] DLT exists for poison events
[ ] DLT is monitored
[ ] Consumer lag is monitored
[ ] DB upsert latency is monitored
[ ] Hot partition risk is understood
[ ] Analytics buckets use clickedAt, not processing time
[ ] Raw IP/referrer logging is avoided or sanitized
```

If these are checked, your click analytics design is interview-ready and production-aware.

---

## 29. One-Page Cheat Sheet

```text
Core mental model:
Kafka click analytics = async event pipeline.
Redirect emits facts. Consumers process later.

Hot path:
GET /shortCode
lookup URL
publish ClickEvent
return 302

Async path:
Kafka topic
consumer group
idempotent aggregation
analytics DB/dashboard

Topic:
url-click-events-v1
retry: url-click-events-retry-v1
DLT: url-click-events-dlt-v1

Event:
eventId
schemaVersion
urlId
shortCode
clickedAt
requestId
safe metadata

Partition key:
shortCode/urlId -> ordering per link, possible hot partition
random eventId -> better spread, weaker per-link ordering
shortCode+bucket -> handles viral hot key, weaker ordering

Consumer correctness:
process DB write first
commit offset after success
handle duplicates with eventId
retry transient failures
DLT poison events

Analytics tables:
url_click_stats_hourly(url_id, bucket_hour, total_clicks)
url_click_stats_daily(url_id, bucket_date, total_clicks)
processed_click_events(event_id)

Monitor:
producer failures
consumer lag
DLT count
retry count
DB upsert latency
hot partitions

Most common bugs:
synchronous analytics in redirect path
offset committed too early
no idempotency
infinite retry
wrong partition key
using processing time instead of clickedAt
```

---

## 30. One Picture To Remember

```text
                 KAFKA CLICK ANALYTICS MENTAL MODEL

                       "Redirect fast, analyze later"

                         HOT USER PATH

Browser
  |
  | GET /abc123
  v
+--------------------------+
| Redirect Service         |
| - validate shortCode     |
| - lookup longUrl         |
| - build ClickEvent       |
+--------------------------+
  |                 |
  | 302 Redirect    | async event
  v                 v
User goes       +-----------------------------+
to long URL     | Kafka: url-click-events-v1  |
                | key = shortCode/urlId       |
                +-----------------------------+
                              |
                              v
                    +---------------------+
                    | Analytics Consumer  |
                    | - validate event    |
                    | - dedupe eventId    |
                    | - update counters   |
                    | - commit offset     |
                    +---------------------+
                              |
                 +------------+------------+
                 |                         |
                 v                         v
       url_click_stats_hourly    url_click_stats_daily

Failure paths:

transient DB issue ---> retry topic
bad poison event ----> DLT
consumer slow -------> lag alert
viral hot key -------> hot partition alert

FINAL MEMORY:

A click is a fact.
Kafka buffers the facts.
Consumers aggregate the facts.
Redirect latency stays protected.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Redirect must stay fast; analytics can be eventually consistent.
2. Publish ClickEvent facts to Kafka instead of synchronously updating counters.
3. Choose partition keys intentionally because they control ordering and hot spots.
4. Consumers must write side effects before committing offsets.
5. Idempotency, retry topics, DLT, and lag monitoring make the pipeline production-shaped.
```

After this chapter, MiniURLShortener has a stronger analytics backbone:

```text
031_Read_Replica_Design.md
032_Kafka_Click_Analytics.md
```

Next possible phase:

```text
033_Kafka_Retry_DLT_Idempotency_Deep_Dive.md
034_Redis_PreAggregation_For_Hot_Links.md
035_Analytics_Dashboard_API.md
```
