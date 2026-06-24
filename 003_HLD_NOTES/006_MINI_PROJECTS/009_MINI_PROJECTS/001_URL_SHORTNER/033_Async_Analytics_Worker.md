# 033_Async_Analytics_Worker.md
# MiniURLShortener — Async Analytics Worker

> Core mental model: **The redirect API must stay fast; analytics should happen later in a separate worker. The API records an event quickly, Kafka buffers it, and the worker processes it safely, idempotently, and retryably.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Synchronous vs Asynchronous Analytics](#4-synchronous-vs-asynchronous-analytics)
- [5. End-to-End Flow](#5-end-to-end-flow)
- [6. Analytics Event Contract](#6-analytics-event-contract)
- [7. Kafka Topic Design](#7-kafka-topic-design)
- [8. Worker Responsibility](#8-worker-responsibility)
- [9. Database Model For Analytics](#9-database-model-for-analytics)
- [10. Spring Boot Dependencies](#10-spring-boot-dependencies)
- [11. Kafka Consumer Configuration](#11-kafka-consumer-configuration)
- [12. Analytics Event DTO](#12-analytics-event-dto)
- [13. Analytics Repository](#13-analytics-repository)
- [14. Idempotency Design](#14-idempotency-design)
- [15. Worker Service Implementation](#15-worker-service-implementation)
- [16. Kafka Listener Implementation](#16-kafka-listener-implementation)
- [17. Retry And Dead Letter Topic](#17-retry-and-dead-letter-topic)
- [18. Manual Ack Mental Model](#18-manual-ack-mental-model)
- [19. Batching Strategy](#19-batching-strategy)
- [20. Aggregation Strategy](#20-aggregation-strategy)
- [21. Backpressure And Lag](#21-backpressure-and-lag)
- [22. Ordering And Partitioning](#22-ordering-and-partitioning)
- [23. Step-by-Step Dry Runs](#23-step-by-step-dry-runs)
- [24. Internal Execution Walkthrough](#24-internal-execution-walkthrough)
- [25. Production Failure Stories](#25-production-failure-stories)
- [26. Debugging Mindset](#26-debugging-mindset)
- [27. Testing Strategy](#27-testing-strategy)
- [28. Common Mistakes](#28-common-mistakes)
- [29. Interview-Ready Explanation](#29-interview-ready-explanation)
- [30. Senior Engineer Checklist](#30-senior-engineer-checklist)
- [31. One-Page Cheat Sheet](#31-one-page-cheat-sheet)
- [32. One Picture To Remember](#32-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener redirect is the hottest endpoint:

```text
GET /{shortCode}
```

At high traffic, redirect must be extremely fast:

```text
client clicks short URL
    -> lookup target URL
    -> return 302 redirect
```

But product teams also want analytics:

```text
how many clicks per short code?
which country?
which browser?
which referrer?
which hour?
which campaign?
unique visitors?
mobile vs desktop?
```

A beginner implementation may do this inside redirect request:

```text
1. lookup shortCode
2. insert click row
3. update counter
4. parse user-agent
5. geo lookup IP
6. update dashboard tables
7. return redirect
```

This is dangerous.

If analytics DB is slow, redirect becomes slow.
If analytics insert fails, user redirect may fail.
If geo lookup blocks, p99 latency explodes.
If dashboard aggregation locks rows, every click waits.

Production rule:

```text
Redirect path should do the minimum required to redirect.
Analytics should be asynchronous.
```

Async analytics worker exists to move heavy click processing away from user-facing latency.

---

## 2. The One Core Mental Model

Async analytics worker is a:

```text
DELAYED PROCESSING PIPELINE
```

The redirect API does not fully process analytics. It only emits a click event.
Kafka stores the event durably.
A background worker consumes the event and writes analytics.

ASCII:

```text
                 USER-FACING FAST PATH

Client
  |
  v
GET /abc123
  |
  v
+------------------+
| Redirect API     |
| lookup URL       |
| publish event    |
| return 302       |
+------------------+
       |
       | click event
       v
+------------------+
| Kafka Topic      |
| click-events     |
+------------------+
       |
       | consumed later
       v
+--------------------------+
| Async Analytics Worker   |
| validate + dedupe        |
| insert raw click         |
| update counters          |
+--------------------------+
       |
       v
+------------------+
| Analytics DB     |
+------------------+
```

One-line memory:

```text
Redirect is real-time; analytics is near-real-time.
```

Near-real-time means analytics may be delayed by seconds, but users still redirect fast.

---

## 3. Problem Statement

Build a production-shaped async worker for MiniURLShortener click analytics.

It must support:

```text
1. Consume click events from Kafka.
2. Validate event shape.
3. Avoid duplicate processing.
4. Insert raw click event safely.
5. Update aggregate counters.
6. Retry transient failures.
7. Send poison messages to dead-letter topic.
8. Track consumer lag.
9. Keep redirect API independent from analytics failures.
```

It should avoid:

```text
slow redirect response
analytics loss during brief DB downtime
duplicate counts due to Kafka redelivery
infinite retry loops
unbounded memory growth
one bad message blocking the partition forever
```

Out of scope for this chapter:

```text
full OLAP warehouse
real-time dashboard UI
Flink/Spark streaming
exact unique visitor analytics at global scale
privacy/legal deep dive
```

This chapter creates the worker foundation.

---

## 4. Synchronous vs Asynchronous Analytics

Synchronous analytics:

```text
Redirect request waits for analytics write.
```

Async analytics:

```text
Redirect request emits event and returns.
Worker processes event later.
```

ASCII comparison:

```text
SYNCHRONOUS

Client -> Redirect API -> DB lookup -> Analytics insert -> Counter update -> 302
                              slow part is inside user request

ASYNC

Client -> Redirect API -> DB lookup -> Kafka publish -> 302
                                      |
                                      v
                               Worker processes later
```

Synchronous is simpler but fragile.
Async is more complex but scalable.

Use synchronous only when analytics must be strongly consistent before response.
For URL click analytics, exact immediate count is usually not required.

Product expectation:

```text
User wants instant redirect.
Dashboard can be a few seconds late.
```

That tradeoff is why Kafka is useful.

---

## 5. End-to-End Flow

Final target flow:

```text
1. User clicks short URL.
2. Redirect service resolves long URL from Redis/Postgres.
3. Redirect service publishes ClickEvent to Kafka.
4. Redirect service returns HTTP 302.
5. Kafka stores event in click-events topic.
6. Worker consumes event.
7. Worker validates event.
8. Worker inserts raw click row using eventId idempotency.
9. Worker updates aggregate counter.
10. Worker commits Kafka offset after successful DB transaction.
```

ASCII:

```text
+--------+        +--------------+        +-------------+
| Client | -----> | Redirect API | -----> | Kafka       |
+--------+        +--------------+        | click-events|
    ^                    |                +-------------+
    |                    |                       |
    |                    v                       v
    |              302 Location          +----------------+
    |                                    | Worker Group   |
    |                                    +----------------+
    |                                           |
    |                                           v
    |                                    +---------------+
    +------------------------------------| Analytics DB  |
                                         +---------------+
```

Important separation:

```text
Redirect API owns user experience.
Worker owns analytics correctness.
```

---

## 6. Analytics Event Contract

A Kafka event is a contract between producer and consumer.

Example JSON event:

```json
{
  "eventId": "01JZCLICK8ZY4Y9E0VV9F7",
  "shortCode": "abc123",
  "clickedAt": "2026-06-24T10:15:30Z",
  "ipHash": "9e107d9d",
  "userAgent": "Mozilla/5.0...",
  "referrer": "https://google.com",
  "country": "RO",
  "deviceType": "MOBILE",
  "traceId": "74f86f9c1d"
}
```

Fields:

```text
eventId      unique event identifier for idempotency
shortCode    shortened URL code
clickedAt    event time, not processing time
ipHash       hashed IP, avoid storing raw IP by default
userAgent    optional raw user-agent or parsed fields
referrer     optional source page
country      optional country code
deviceType   optional MOBILE/DESKTOP/TABLET/BOT
traceId      correlation between redirect log and worker log
```

Contract rules:

```text
1. eventId must be unique.
2. shortCode must not be blank.
3. clickedAt must be present.
4. Event schema should be backward compatible.
5. Worker should tolerate unknown fields.
6. Producer should not send secrets.
```

ASCII:

```text
Producer and consumer agree on this shape:

Redirect API                      Worker
    |                               |
    |  ClickEvent JSON              |
    +------------------------------>|
            event contract
```

Bad contract design causes silent analytics corruption.

---

## 7. Kafka Topic Design

Recommended topics:

```text
url-click-events
url-click-events-retry
url-click-events-dlt
```

Main topic:

```text
url-click-events
```

Used for normal click events.

Retry topic:

```text
url-click-events-retry
```

Used for transient failures like database timeout.

Dead-letter topic:

```text
url-click-events-dlt
```

Used when message cannot be processed after retries, or event is permanently invalid.

ASCII:

```text
                    success
url-click-events  -----------> Analytics DB
      |
      | transient failure
      v
url-click-events-retry
      |
      | retries exhausted / poison message
      v
url-click-events-dlt
```

Partition key:

```text
shortCode
```

Why partition by shortCode?

```text
Events for the same shortCode go to the same partition.
This helps preserve per-link ordering.
```

But it can create hot partitions if one shortCode is extremely popular.

Tradeoff:

```text
Key by shortCode:
    better per-link ordering
    possible hot partition

Key by eventId:
    better distribution
    no per-link ordering
```

For beginner production version:

```text
Use shortCode as key.
Monitor hot partitions later.
```

---

## 8. Worker Responsibility

The worker should do only background processing.

Worker responsibilities:

```text
consume Kafka event
validate required fields
convert DTO to domain model
perform idempotency check
insert raw click
update aggregates
commit offset after success
retry transient failures
route poison events to DLT
emit logs/metrics
```

Worker should not:

```text
redirect users
call frontend
perform slow external calls inside listener without timeout
store raw secrets
assume exactly-once delivery from Kafka alone
```

Worker mental model:

```text
A worker is a deterministic machine:
input event -> safe DB transaction -> ack offset
```

ASCII:

```text
Kafka record
   |
   v
+----------------+
| Validate       |
+----------------+
   |
   v
+----------------+
| Idempotency    |
+----------------+
   |
   v
+----------------+
| DB transaction |
+----------------+
   |
   v
+----------------+
| Ack offset     |
+----------------+
```

Ack should happen only after the database write succeeds.

---

## 9. Database Model For Analytics

Use two tables:

```text
1. click_events_raw
2. url_click_aggregates_hourly
```

Raw table stores individual click events.
Aggregate table stores summarized counters.

SQL:

```sql
CREATE TABLE click_events_raw (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(64) NOT NULL,
    short_code VARCHAR(32) NOT NULL,
    clicked_at TIMESTAMPTZ NOT NULL,
    ip_hash VARCHAR(128),
    user_agent TEXT,
    referrer TEXT,
    country VARCHAR(2),
    device_type VARCHAR(32),
    trace_id VARCHAR(128),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_click_events_event_id UNIQUE (event_id)
);

CREATE INDEX idx_click_events_short_code_clicked_at
ON click_events_raw (short_code, clicked_at DESC);
```

Hourly aggregate:

```sql
CREATE TABLE url_click_aggregates_hourly (
    short_code VARCHAR(32) NOT NULL,
    bucket_hour TIMESTAMPTZ NOT NULL,
    click_count BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (short_code, bucket_hour)
);
```

Why raw + aggregate?

```text
Raw table supports detailed analysis and reprocessing.
Aggregate table supports fast dashboards.
```

ASCII:

```text
click_events_raw
    one row per click

url_click_aggregates_hourly
    one row per shortCode per hour

100,000 raw clicks ---------------> 1 aggregate row maybe
```

Without aggregate table, dashboard query becomes expensive:

```sql
SELECT count(*) FROM click_events_raw WHERE short_code = 'abc123';
```

At scale, this can become slow.

---

## 10. Spring Boot Dependencies

Maven dependencies:

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

For worker code, JDBC is often enough and predictable.

Why JDBC instead of JPA here?

```text
1. Worker writes simple rows.
2. Batch insert/upsert is easier to control.
3. Less ORM overhead.
4. SQL upsert is explicit.
```

JPA is fine for domain-heavy business flows.
Analytics ingestion is write-heavy and SQL-shaped.

---

## 11. Kafka Consumer Configuration

Example `application.yml`:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: analytics-worker-v1
      auto-offset-reset: earliest
      enable-auto-commit: false
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.miniurl.shortener.analytics"
    listener:
      ack-mode: manual

app:
  kafka:
    topics:
      click-events: url-click-events
      click-events-dlt: url-click-events-dlt
```

Important settings:

```text
group-id:
    all worker replicas with same group share partitions

enable-auto-commit=false:
    do not commit offsets before DB success

ack-mode=manual:
    code explicitly decides when offset is committed

auto-offset-reset=earliest:
    new group starts from beginning if no offset exists
```

ASCII:

```text
Kafka partition offset

0 1 2 3 4 5 6
        ^
        last committed offset

Worker reads 4.
If DB success -> commit 4.
If DB failure -> do not commit 4.
```

---

## 12. Analytics Event DTO

Java DTO:

```java
package com.miniurl.shortener.analytics.event;

import java.time.Instant;

public class ClickEvent {

    private String eventId;
    private String shortCode;
    private Instant clickedAt;
    private String ipHash;
    private String userAgent;
    private String referrer;
    private String country;
    private String deviceType;
    private String traceId;

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public Instant getClickedAt() { return clickedAt; }
    public void setClickedAt(Instant clickedAt) { this.clickedAt = clickedAt; }

    public String getIpHash() { return ipHash; }
    public void setIpHash(String ipHash) { this.ipHash = ipHash; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getReferrer() { return referrer; }
    public void setReferrer(String referrer) { this.referrer = referrer; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
}
```

Validation helper:

```java
package com.miniurl.shortener.analytics.service;

import com.miniurl.shortener.analytics.event.ClickEvent;
import org.springframework.stereotype.Component;

@Component
public class ClickEventValidator {

    public void validate(ClickEvent event) {
        if (event == null) {
            throw new InvalidClickEventException("event is null");
        }
        if (isBlank(event.getEventId())) {
            throw new InvalidClickEventException("eventId is required");
        }
        if (isBlank(event.getShortCode())) {
            throw new InvalidClickEventException("shortCode is required");
        }
        if (event.getClickedAt() == null) {
            throw new InvalidClickEventException("clickedAt is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
```

Exception:

```java
package com.miniurl.shortener.analytics.service;

public class InvalidClickEventException extends RuntimeException {
    public InvalidClickEventException(String message) {
        super(message);
    }
}
```

Invalid events are usually poison messages.
They should go to DLT, not retry forever.

---

## 13. Analytics Repository

Repository using JDBC:

```java
package com.miniurl.shortener.analytics.repository;

import com.miniurl.shortener.analytics.event.ClickEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Repository
public class ClickAnalyticsRepository {

    private final JdbcTemplate jdbcTemplate;

    public ClickAnalyticsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean insertRawClickIfAbsent(ClickEvent event) {
        String sql = """
                INSERT INTO click_events_raw
                (event_id, short_code, clicked_at, ip_hash, user_agent, referrer,
                 country, device_type, trace_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (event_id) DO NOTHING
                """;

        int rows = jdbcTemplate.update(
                sql,
                event.getEventId(),
                event.getShortCode(),
                Timestamp.from(event.getClickedAt()),
                event.getIpHash(),
                event.getUserAgent(),
                event.getReferrer(),
                event.getCountry(),
                event.getDeviceType(),
                event.getTraceId()
        );

        return rows == 1;
    }

    public void incrementHourlyCounter(String shortCode, Instant clickedAt) {
        Instant bucketHour = clickedAt.truncatedTo(ChronoUnit.HOURS);

        String sql = """
                INSERT INTO url_click_aggregates_hourly
                (short_code, bucket_hour, click_count, updated_at)
                VALUES (?, ?, 1, now())
                ON CONFLICT (short_code, bucket_hour)
                DO UPDATE SET
                    click_count = url_click_aggregates_hourly.click_count + 1,
                    updated_at = now()
                """;

        jdbcTemplate.update(sql, shortCode, Timestamp.from(bucketHour));
    }
}
```

Key idea:

```text
insertRawClickIfAbsent returns true only for first processing.
If duplicate event arrives, raw insert does nothing.
Then worker should skip counter update.
```

This prevents double counting.

---

## 14. Idempotency Design

Kafka consumers can receive the same message more than once.

Reasons:

```text
worker writes DB but crashes before ack
rebalance happens during processing
network issue during offset commit
manual retry sends same event again
producer retries without perfect dedupe
```

Therefore worker must be idempotent.

Idempotent means:

```text
Processing the same event multiple times has the same final effect as processing once.
```

ASCII:

```text
Same eventId = E123 arrives twice

First attempt:
    INSERT raw E123 -> success
    increment counter +1

Second attempt:
    INSERT raw E123 -> conflict do nothing
    skip counter

Final count: +1, not +2
```

Without idempotency:

```text
Kafka redelivery = duplicate analytics count
```

With idempotency:

```text
Kafka redelivery = harmless duplicate
```

The database unique constraint is the idempotency guard:

```sql
CONSTRAINT uk_click_events_event_id UNIQUE (event_id)
```

Rule:

```text
Do not rely on Kafka exactly-once alone for business correctness.
Use application-level idempotency.
```

---

## 15. Worker Service Implementation

Service:

```java
package com.miniurl.shortener.analytics.service;

import com.miniurl.shortener.analytics.event.ClickEvent;
import com.miniurl.shortener.analytics.repository.ClickAnalyticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClickAnalyticsWorkerService {

    private static final Logger log = LoggerFactory.getLogger(ClickAnalyticsWorkerService.class);

    private final ClickEventValidator validator;
    private final ClickAnalyticsRepository repository;

    public ClickAnalyticsWorkerService(
            ClickEventValidator validator,
            ClickAnalyticsRepository repository
    ) {
        this.validator = validator;
        this.repository = repository;
    }

    @Transactional
    public void process(ClickEvent event) {
        validator.validate(event);

        boolean inserted = repository.insertRawClickIfAbsent(event);

        if (!inserted) {
            log.info("duplicate click event ignored eventId={} shortCode={}",
                    event.getEventId(), event.getShortCode());
            return;
        }

        repository.incrementHourlyCounter(event.getShortCode(), event.getClickedAt());

        log.info("click event processed eventId={} shortCode={} clickedAt={}",
                event.getEventId(), event.getShortCode(), event.getClickedAt());
    }
}
```

Why transaction?

```text
Raw insert and aggregate increment should succeed together.
```

If raw insert succeeds but counter update fails, transaction rolls back.
Then Kafka message is retried.
On retry, raw insert can happen again because rollback removed it.
Final state remains correct.

ASCII transaction:

```text
BEGIN
  insert raw click
  increment hourly count
COMMIT

If failure:
ROLLBACK
Kafka offset not acked
message retried
```

---

## 16. Kafka Listener Implementation

Manual ack listener:

```java
package com.miniurl.shortener.analytics.consumer;

import com.miniurl.shortener.analytics.event.ClickEvent;
import com.miniurl.shortener.analytics.service.ClickAnalyticsWorkerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class ClickAnalyticsConsumer {

    private static final Logger log = LoggerFactory.getLogger(ClickAnalyticsConsumer.class);

    private final ClickAnalyticsWorkerService workerService;

    public ClickAnalyticsConsumer(ClickAnalyticsWorkerService workerService) {
        this.workerService = workerService;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.click-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(
            ClickEvent event,
            ConsumerRecord<String, ClickEvent> record,
            Acknowledgment acknowledgment
    ) {
        log.debug("received click event topic={} partition={} offset={} key={}",
                record.topic(), record.partition(), record.offset(), record.key());

        workerService.process(event);

        acknowledgment.acknowledge();

        log.debug("acknowledged click event partition={} offset={}",
                record.partition(), record.offset());
    }
}
```

Important:

```text
acknowledgment.acknowledge() happens after process(event).
```

If `process` throws exception:

```text
ack is not called
Spring Kafka error handler decides retry/DLT behavior
```

ASCII:

```text
consume event
   |
   v
process DB transaction
   |
   +-- success -> ack offset
   |
   +-- failure -> no ack -> retry/error handler
```

---

## 17. Retry And Dead Letter Topic

Not all failures are equal.

Transient failures:

```text
Postgres connection timeout
temporary network issue
deadlock
Kafka rebalance interruption
```

Permanent failures:

```text
missing eventId
invalid JSON
unknown incompatible schema
shortCode blank
clickedAt null
```

Transient should be retried.
Permanent should go to DLT.

Spring Kafka error handler example:

```java
package com.miniurl.shortener.analytics.config;

import com.miniurl.shortener.analytics.service.InvalidClickEventException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler defaultErrorHandler(
            KafkaTemplate<Object, Object> kafkaTemplate
    ) {
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(kafkaTemplate,
                        (ConsumerRecord<?, ?> record, Exception ex) ->
                                new org.apache.kafka.common.TopicPartition(
                                        record.topic() + "-dlt",
                                        record.partition()
                                ));

        FixedBackOff backOff = new FixedBackOff(1_000L, 3L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        errorHandler.addNotRetryableExceptions(InvalidClickEventException.class);

        return errorHandler;
    }
}
```

This means:

```text
Retry most exceptions 3 times with 1 second delay.
InvalidClickEventException goes directly to DLT.
After retries are exhausted, publish to DLT.
```

ASCII:

```text
event E1
  |
  v
worker fails DB timeout
  |
  +--> retry 1
  +--> retry 2
  +--> retry 3
  |
  v
DLT if still failing
```

DLT is not a trash can.
It is an investigation queue.

---

## 18. Manual Ack Mental Model

Offset commit tells Kafka:

```text
This message is done for this consumer group.
```

Auto commit can be risky because Kafka may commit before DB write is complete.

Bad timeline:

```text
1. Worker receives event offset 100.
2. Kafka auto-commits offset 100.
3. Worker tries DB insert.
4. Worker crashes.
5. Kafka thinks offset 100 is done.
6. Event is lost from processing.
```

Manual ack timeline:

```text
1. Worker receives event offset 100.
2. Worker writes DB successfully.
3. Worker acknowledges offset 100.
4. Kafka marks offset 100 done.
```

ASCII:

```text
Wrong:
    receive -> commit -> DB write -> crash = possible loss

Correct:
    receive -> DB write -> commit = safer
```

Manual ack does not guarantee exactly once.
It gives at-least-once with controlled commit.

Therefore:

```text
manual ack + idempotent DB write = production-safe pattern
```

---

## 19. Batching Strategy

At low volume, process one event at a time.
At high volume, batching improves throughput.

Single event mode:

```text
simple
lower latency
more DB round trips
```

Batch mode:

```text
better throughput
fewer DB round trips
more complex error handling
```

ASCII:

```text
Single:
Kafka -> event -> insert -> ack
Kafka -> event -> insert -> ack
Kafka -> event -> insert -> ack

Batch:
Kafka -> [event,event,event,event]
          |
          v
       batch insert
       batch aggregate
       ack batch
```

When to batch:

```text
consumer lag increasing
DB CPU okay but too many round trips
click volume high
analytics can tolerate slightly more delay
```

Batch danger:

```text
one poison event can fail whole batch
partial success must be handled carefully
```

Start with single-message processing.
Upgrade to batch when metrics prove the need.

---

## 20. Aggregation Strategy

Raw events are detailed but expensive to query.
Aggregates make dashboards fast.

Hourly bucket example:

```text
clickedAt = 2026-06-24T10:15:30Z
bucketHour = 2026-06-24T10:00:00Z
```

Aggregate key:

```text
(shortCode, bucketHour)
```

Every click increments that bucket.

ASCII:

```text
Raw clicks:
10:01 abc123
10:07 abc123
10:42 abc123
11:05 abc123

Aggregates:
abc123 10:00 -> 3
abc123 11:00 -> 1
```

Query for dashboard:

```sql
SELECT bucket_hour, click_count
FROM url_click_aggregates_hourly
WHERE short_code = 'abc123'
ORDER BY bucket_hour;
```

This is much faster than scanning all raw clicks.

Production evolution:

```text
v1: hourly counts
v2: country/device/referrer aggregates
v3: approximate unique visitors using HyperLogLog
v4: OLAP store such as ClickHouse/BigQuery/Druid
```

Do not start with complex OLAP unless needed.

---

## 21. Backpressure And Lag

Consumer lag means:

```text
Kafka has messages waiting that worker has not processed yet.
```

ASCII:

```text
Partition offsets:

0 1 2 3 4 5 6 7 8 9 10
                    ^ latest produced
        ^ committed by worker

Lag = latest - committed
```

If lag grows, worker is slower than producer.

Common causes:

```text
DB slow
worker replicas too few
partitions too few
large messages
external calls inside worker
batch size too small
locks/contention on aggregate rows
```

Fix options:

```text
scale worker replicas
increase topic partitions
optimize DB indexes
batch writes
separate hot shortCodes
move analytics to OLAP store
reduce per-event processing
```

Important:

```text
More workers help only up to partition count.
```

If topic has 3 partitions and consumer group has 10 workers:

```text
only 3 workers actively consume
7 are idle
```

ASCII:

```text
Partitions: P0 P1 P2
Workers:    W1 W2 W3 W4 W5
Assigned:   W1 W2 W3 idle idle
```

---

## 22. Ordering And Partitioning

Kafka preserves order inside a partition.
Kafka does not preserve global order across partitions.

If key is `shortCode`:

```text
All abc123 events go to same partition.
Order for abc123 is preserved.
```

ASCII:

```text
key abc123 -> partition 0: e1 e2 e3
key xyz999 -> partition 2: e4 e5 e6
```

But one viral link can overload one partition:

```text
abc123 gets 80% traffic
partition 0 becomes hot
```

Options later:

```text
1. key by eventId for better distribution
2. key by shortCode + shard suffix
3. aggregate in distributed counters
4. use OLAP/event stream processing
```

For MiniURLShortener learning version:

```text
Start with key = shortCode.
Understand ordering and hot partition tradeoff.
```

Interview sentence:

```text
Partition key is a tradeoff between ordering and load distribution.
```

---

## 23. Step-by-Step Dry Runs

### Dry Run 1: Normal click event

Event:

```json
{
  "eventId": "E100",
  "shortCode": "abc123",
  "clickedAt": "2026-06-24T10:15:30Z"
}
```

Flow:

```text
1. Worker receives event from url-click-events.
2. Validator checks eventId, shortCode, clickedAt.
3. DB inserts raw row E100.
4. Insert returns rows=1.
5. Worker increments aggregate abc123 / 10:00.
6. Transaction commits.
7. Listener acknowledges Kafka offset.
8. Dashboard eventually shows +1 click.
```

State:

```text
click_events_raw:
E100 abc123 10:15

url_click_aggregates_hourly:
abc123 10:00 count=1
```

---

### Dry Run 2: Duplicate event redelivered

Same event arrives again:

```text
eventId = E100
```

Flow:

```text
1. Validator passes.
2. INSERT raw click ON CONFLICT DO NOTHING.
3. Database returns rows=0.
4. Worker logs duplicate.
5. Worker skips aggregate increment.
6. Transaction commits.
7. Offset is acknowledged.
```

Final count:

```text
Still 1, not 2.
```

---

### Dry Run 3: DB timeout before commit

Flow:

```text
1. Worker receives event E200.
2. Validation passes.
3. DB insert starts.
4. DB timeout occurs.
5. Transaction rolls back.
6. Listener does not ack.
7. Error handler retries.
8. Retry later succeeds.
9. Offset is acknowledged after success.
```

Result:

```text
No event loss.
Possible duplicate attempt, but idempotency handles it.
```

---

### Dry Run 4: Invalid event

Event:

```json
{
  "eventId": "E300",
  "clickedAt": "2026-06-24T10:15:30Z"
}
```

Missing:

```text
shortCode
```

Flow:

```text
1. Worker receives event.
2. Validator throws InvalidClickEventException.
3. Error handler marks this as not retryable.
4. Message goes to DLT.
5. Offset is handled according to error handler.
6. Partition is not blocked forever.
```

Why no retry?

```text
Retrying missing shortCode will not magically fix the event.
```

---

### Dry Run 5: Worker crash after DB commit before ack

Timeline:

```text
1. Worker receives E400.
2. DB transaction commits successfully.
3. Worker crashes before Kafka ack.
4. Kafka redelivers E400 to another worker.
5. New worker attempts insert.
6. Unique event_id prevents duplicate.
7. Counter is not incremented again.
8. New worker acknowledges offset.
```

This is the most important production dry run.

Memory:

```text
Crash after DB commit before ack creates duplicate delivery.
Idempotency makes duplicate delivery safe.
```

---

## 24. Internal Execution Walkthrough

Full internal path:

```text
1. Kafka listener container polls records.
2. Spring deserializes JSON into ClickEvent.
3. Listener method receives ClickEvent and ConsumerRecord metadata.
4. Listener calls workerService.process(event).
5. Spring transaction proxy opens DB transaction.
6. Validator checks required fields.
7. Repository inserts raw click with ON CONFLICT DO NOTHING.
8. If inserted, repository upserts hourly aggregate.
9. Transaction commits.
10. Listener manually acknowledges Kafka offset.
11. Kafka stores committed offset for consumer group.
```

ASCII:

```text
KafkaConsumer.poll()
       |
       v
Spring Kafka Listener Container
       |
       v
Deserialize JSON -> ClickEvent
       |
       v
ClickAnalyticsConsumer.consume()
       |
       v
@Transactional Worker Service
       |
       +--> validate
       +--> insert raw if absent
       +--> increment aggregate
       |
       v
DB COMMIT
       |
       v
acknowledgment.acknowledge()
       |
       v
Kafka committed offset
```

Failure path:

```text
If exception before DB commit:
    transaction rollback
    no ack
    retry/DLT

If exception after DB commit before ack:
    possible duplicate delivery
    idempotency protects count
```

This is why transaction + manual ack + idempotency must work together.

---

## 25. Production Failure Stories

### Failure Story 1: Redirect p99 explodes because analytics is synchronous

Initial design:

```text
GET /abc123 writes analytics directly before redirect.
```

Incident:

```text
Analytics table becomes slow.
Redirect p99 jumps from 30ms to 2s.
Users feel short links are broken.
```

Fix:

```text
Publish click event to Kafka and process asynchronously.
```

Lesson:

```text
Do not put non-critical analytics work in the user-facing hot path.
```

---

### Failure Story 2: Duplicate counts after worker restart

Incident:

```text
Worker processed events and crashed before committing offsets.
Kafka redelivered messages.
Dashboard counts doubled.
```

Root cause:

```text
No eventId unique constraint.
Counter increment was not idempotent.
```

Fix:

```text
Add eventId and UNIQUE(event_id).
Only increment aggregate when raw insert succeeds.
```

Lesson:

```text
At-least-once delivery requires idempotent consumers.
```

---

### Failure Story 3: One bad event blocks partition

Incident:

```text
A malformed event keeps failing.
Worker retries forever.
All later messages in that partition are stuck.
```

Root cause:

```text
No DLT and no classification of retryable vs non-retryable exceptions.
```

Fix:

```text
Invalid events go to DLT. Transient failures retry with backoff.
```

Lesson:

```text
Poison messages must not block healthy traffic forever.
```

---

### Failure Story 4: Consumer lag grows silently

Incident:

```text
Dashboard is 2 hours behind.
No alert fires.
```

Root cause:

```text
No consumer lag monitoring.
Workers were slower than producers.
```

Fix:

```text
Track Kafka consumer lag, processing latency, DB write latency, DLT count.
```

Lesson:

```text
Async systems fail by delay before they fail by crash.
```

---

### Failure Story 5: Hot short code overloads one partition

Incident:

```text
One celebrity link receives 80% of traffic.
One Kafka partition and one aggregate row become hot.
```

Root cause:

```text
Partition key = shortCode and aggregate row updated per click.
```

Fix options:

```text
sharded counters
batch aggregation
partition key salting
OLAP/event processing system
```

Lesson:

```text
Partitioning strategy must consider hot keys.
```

---

## 26. Debugging Mindset

When analytics looks wrong, ask:

```text
Is redirect API producing events?
Are events reaching Kafka?
Is consumer group active?
Is lag increasing?
Are workers throwing exceptions?
Are messages going to DLT?
Are duplicate eventIds being ignored?
Is DB insert slow?
Is aggregate table locked/hot?
Is clickedAt timezone correct?
Is partition assignment balanced?
```

Debug map:

```text
Dashboard count too low:
    producer publish failures
    consumer lag
    DLT messages
    DB failures
    wrong time bucket

Dashboard count too high:
    missing idempotency
    duplicate eventId generation bug
    aggregate increment before raw insert check

Worker stopped:
    consumer group rebalance issue
    deserialization error
    database connection pool exhausted

One link delayed:
    hot partition
    hot aggregate row
    slow processing for that shortCode
```

Useful SQL:

```sql
SELECT event_id, short_code, clicked_at
FROM click_events_raw
WHERE event_id = 'E100';
```

```sql
SELECT short_code, bucket_hour, click_count
FROM url_click_aggregates_hourly
WHERE short_code = 'abc123'
ORDER BY bucket_hour DESC;
```

Useful Kafka checks:

```text
consumer group lag
partition assignment
DLT message count
retry topic message count
oldest unprocessed offset age
```

Golden rule:

```text
In async systems, always debug the pipeline stage by stage.
```

---

## 27. Testing Strategy

Test the worker like a production pipeline.

### Unit tests

Validate worker logic:

```text
valid event inserts raw and increments aggregate
same event twice increments only once
invalid event throws InvalidClickEventException
```

### Repository tests

Use Testcontainers Postgres:

```text
ON CONFLICT DO NOTHING works
hourly upsert increments count
transaction rollback prevents partial update
```

### Kafka integration tests

Use embedded Kafka or Testcontainers Kafka:

```text
event consumed from topic
successful processing commits offset
invalid event goes to DLT
transient failure retries
```

### Failure tests

```text
DB unavailable -> retry
malformed event -> DLT
duplicate event -> no double count
worker crash simulation -> redelivery safe
```

Example unit test idea:

```java
@Test
void duplicateEventShouldNotIncrementCounterTwice() {
    ClickEvent event = new ClickEvent();
    event.setEventId("E100");
    event.setShortCode("abc123");
    event.setClickedAt(Instant.parse("2026-06-24T10:15:30Z"));

    workerService.process(event);
    workerService.process(event);

    long count = testRepository.countFor("abc123", Instant.parse("2026-06-24T10:00:00Z"));

    assertEquals(1L, count);
}
```

Testing rule:

```text
Do not only test happy consumption.
Test duplicate, retry, DLT, and rollback behavior.
```

---

## 28. Common Mistakes

### Mistake 1: Doing analytics inside redirect request

Wrong:

```text
Redirect waits for analytics DB.
```

Correct:

```text
Redirect publishes event and returns 302.
```

### Mistake 2: Auto-committing offsets

Wrong:

```text
Kafka commits before DB success.
```

Correct:

```text
Manual ack after DB transaction succeeds.
```

### Mistake 3: No idempotency

Wrong:

```text
Every redelivery increments count.
```

Correct:

```text
UNIQUE(event_id) + increment only after raw insert succeeds.
```

### Mistake 4: Retrying poison messages forever

Wrong:

```text
Invalid message blocks partition.
```

Correct:

```text
Invalid message goes to DLT.
```

### Mistake 5: Treating Kafka as exactly-once magic

Wrong:

```text
Kafka will prevent all duplicates for me.
```

Correct:

```text
Consumer must be idempotent at business level.
```

### Mistake 6: No lag monitoring

Wrong:

```text
Async means safe, no need to monitor.
```

Correct:

```text
Monitor lag, processing latency, DLT, retry count, DB latency.
```

### Mistake 7: Updating aggregate before dedupe

Wrong:

```text
increment counter first, then insert raw event
```

Correct:

```text
insert raw event first; if inserted, increment counter
```

### Mistake 8: One huge transaction for too many messages

Wrong:

```text
consume 10,000 events in one transaction
```

Correct:

```text
start simple; batch carefully with bounded size and clear retry behavior
```

---

## 29. Interview-Ready Explanation

If interviewer asks:

```text
How would you design click analytics for a URL shortener?
```

Strong answer:

```text
I would keep redirect latency independent from analytics. The redirect service
resolves the long URL, publishes a click event to Kafka, and returns 302 quickly.
A separate analytics worker consumes click events from Kafka, validates the event,
and writes to analytics storage. Since Kafka consumers are usually at-least-once,
the worker must be idempotent. I would include a unique eventId in every click event
and enforce UNIQUE(event_id) in the raw click table. The worker inserts the raw click
using ON CONFLICT DO NOTHING and increments aggregate counters only when the insert
actually happens. Offset commit should happen after the DB transaction succeeds,
using manual acknowledgement. Transient failures are retried with backoff, while
permanently invalid messages go to a dead-letter topic. I would monitor consumer
lag, processing latency, DB latency, retry count, and DLT count. This design keeps
redirects fast while making analytics eventually consistent and reliable.
```

Why this is strong:

```text
1. Separates hot path from background work.
2. Mentions Kafka buffering.
3. Understands at-least-once delivery.
4. Adds idempotency using eventId.
5. Commits offset after DB success.
6. Handles retry and DLT.
7. Mentions monitoring and lag.
8. Understands eventual consistency.
```

Senior one-liner:

```text
For click analytics, I optimize redirect for latency and analytics for durable, idempotent, eventually consistent processing.
```

---

## 30. Senior Engineer Checklist

Before calling the worker production-shaped, confirm:

```text
[ ] Redirect API does not wait for analytics DB
[ ] ClickEvent has eventId
[ ] ClickEvent has shortCode and clickedAt
[ ] Kafka topic exists for click events
[ ] Worker consumer group is configured
[ ] auto commit is disabled
[ ] manual ack is enabled
[ ] DB has click_events_raw table
[ ] DB has UNIQUE(event_id)
[ ] Aggregate table exists
[ ] Worker validates required event fields
[ ] Worker inserts raw click before aggregate increment
[ ] Duplicate raw insert skips aggregate increment
[ ] Raw insert + aggregate update are in one transaction
[ ] Offset is acknowledged after successful process
[ ] Retry handler exists for transient failures
[ ] DLT exists for poison messages
[ ] Invalid event is not retried forever
[ ] Consumer lag is monitored
[ ] DLT count is monitored
[ ] DB write latency is monitored
[ ] Logs include eventId, shortCode, partition, offset, traceId
[ ] No raw sensitive IP/token is logged carelessly
```

If all are checked, your async analytics worker has a solid production foundation.

---

## 31. One-Page Cheat Sheet

```text
Core mental model:
Redirect is real-time.
Analytics is near-real-time.
Kafka is the buffer between them.

Why async:
Keep redirect fast.
Protect user path from analytics DB failures.
Absorb traffic spikes.
Retry safely.

Main flow:
Redirect API -> Kafka click-events -> Worker -> Analytics DB

Event fields:
eventId
shortCode
clickedAt
ipHash
userAgent
referrer
country
deviceType
traceId

Tables:
click_events_raw
    UNIQUE(event_id)

url_click_aggregates_hourly
    PRIMARY KEY(short_code, bucket_hour)

Reliability pattern:
manual ack after DB success
transaction around raw insert + aggregate update
idempotency using eventId
retry transient failures
DLT permanent failures

Most important bug:
Duplicate Kafka delivery can double count.
Fix:
UNIQUE(event_id) and increment only if raw insert succeeds.

Monitoring:
consumer lag
processing latency
DB latency
retry count
DLT count
partition hot spots

Interview sentence:
At-least-once Kafka delivery plus idempotent DB writes gives reliable analytics without slowing redirects.
```

---

## 32. One Picture To Remember

```text
                 ASYNC ANALYTICS WORKER MENTAL MODEL

                         FAST USER PATH

Client Click
    |
    v
+-----------------------------+
| Redirect API                |
| 1. resolve shortCode        |
| 2. publish ClickEvent       |
| 3. return 302 fast          |
+-----------------------------+
    |
    | eventId + shortCode + clickedAt
    v
+-----------------------------+
| Kafka: url-click-events     |
| durable buffer              |
| absorbs spikes              |
+-----------------------------+
    |
    v
+-----------------------------+
| Async Analytics Worker      |
| validate event              |
| insert raw if absent        |
| increment aggregate         |
| ack after DB success        |
+-----------------------------+
    |
    v
+-----------------------------+
| Analytics DB                |
| raw clicks                  |
| hourly counters             |
+-----------------------------+

Failure safety:

DB fail       -> no ack -> retry
bad event     -> DLT
redelivery    -> UNIQUE(event_id) -> no double count
lag grows     -> scale workers / partitions / DB
hot shortCode -> sharded counters later

FINAL MEMORY:

Do not make the user wait for analytics.
Make analytics durable, retryable, idempotent, and observable.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Redirect API should stay fast and only emit analytics events.
2. Kafka decouples user-facing latency from background processing.
3. Kafka consumers must be idempotent because redelivery can happen.
4. Manual ack should happen only after the analytics DB transaction succeeds.
5. Retry handles temporary failures; DLT handles poison messages.
```

After this chapter, the MiniURLShortener analytics path becomes production-shaped:

```text
032_Kafka_Click_Analytics.md
033_Async_Analytics_Worker.md
```

Next possible chapter:

```text
034_Analytics_Aggregation_Dashboard.md
```
