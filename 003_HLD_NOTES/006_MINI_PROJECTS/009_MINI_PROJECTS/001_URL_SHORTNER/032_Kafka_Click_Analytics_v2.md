# MiniURLShortener — Kafka Click Analytics v2

> **Mental model:** Keep redirects fast. Publish an immutable click fact to Kafka and aggregate it asynchronously.

## Index
1. Goal and architecture
2. Requirements and decisions
3. Event contract
4. Topic and partitioning
5. Producer
6. Consumer, idempotency and offsets
7. Database
8. Retry and DLT
9. Spring Boot implementation
10. Scaling and capacity
11. Observability and failures
12. Testing
13. Interview answers
14. Cheat sheet

---

# 1. Goal and Architecture

The hottest endpoint is:

```http
GET /{shortCode}
```

It must do minimal synchronous work:

```text
shortCode -> Redis/DB lookup -> 302 redirect
```

Analytics such as total clicks, hourly clicks, country, device and referrer must not add database writes to this critical path.

## Bad design

```text
GET /abc123
   |
   +--> resolve URL
   +--> insert raw click
   +--> update hourly count
   +--> update daily count
   +--> update dimensions
   |
   v
302
```

Consequences:

```text
analytics latency increases redirect p99
analytics failure can break redirects
traffic spikes amplify database writes
reporting logic becomes coupled to redirect logic
```

## Production-shaped design

```text
                         HOT PATH
+--------+      +------------------+      +-------------+
| Client | ---> | Redirect Service | ---> | Redis / DB  |
+--------+      +------------------+      +-------------+
                       |
                       | async URL_CLICKED
                       v
             +----------------------+
             | Kafka click topic    |
             +----------------------+
                       |
                       v
             +----------------------+
             | Analytics consumers  |
             | validate             |
             | deduplicate          |
             | aggregate            |
             +----------------------+
                       |
                       v
             +----------------------+
             | Analytics database   |
             +----------------------+
```

Sequence:

```text
Client        Redirect Service        Kafka        Consumer        DB
  |                  |                  |              |             |
  | GET /abc123      |                  |              |             |
  |----------------->|                  |              |             |
  |                  | lookup URL       |              |             |
  |                  |---- Redis/DB --->|              |             |
  |                  | async event      |              |             |
  |                  |----------------->|              |             |
  | 302 Location     |                  |              |             |
  |<-----------------|                  |              |             |
  |                  |                  | poll         |             |
  |                  |                  |------------->|             |
  |                  |                  |              | DB tx       |
  |                  |                  |              |------------>|
  |                  |                  |              | commit, ACK |
```

Core separation:

```text
redirect path  = synchronous, latency-sensitive, highly available
analytics path = asynchronous, buffered, eventually consistent
```

---

# 2. Requirements and Decisions

## Functional requirements

```text
emit one click event after a successful redirect lookup
aggregate by URL and event-time bucket
retry transient failures
send poison records to a dead-letter topic
support replay and future independent consumers
expose throughput, error and lag metrics
```

## Non-functional requirements

```text
analytics outage must not break redirect
duplicate delivery must not inflate counters
consumer crash must not lose completed work
pipeline must scale horizontally
event contract must evolve safely
sensitive metadata must be minimized
```

## High-ROI decisions

| Concern | Decision |
|---|---|
| Event | Immutable `URL_CLICKED` fact |
| Main topic | `url-click-events-v1` |
| Partition key | `urlId` |
| Delivery | At least once |
| Offset | Acknowledge after DB transaction |
| Duplicate protection | Unique `eventId` |
| Bucketing | Use `clickedAt` |
| Poison event | Bounded retry, then DLT |
| Redirect on publish failure | Redirect still succeeds |
| Storage | Aggregates; raw retention optional |

---

# 3. Event Contract

An event says what happened; it does not command a consumer.

Prefer:

```text
URL_CLICKED
```

Avoid:

```text
INCREMENT_CLICK_COUNTER
```

One fact can serve multiple consumers:

```text
                      URL_CLICKED
                           |
          +----------------+----------------+
          |                |                |
          v                v                v
      Analytics        Fraud Check     Live Dashboard
```

## JSON

```json
{
  "eventId": "01J0CLICK9J7P4X8Y2H6Z",
  "eventType": "URL_CLICKED",
  "schemaVersion": 1,
  "urlId": 12345,
  "shortCode": "abc123",
  "clickedAt": "2026-07-13T09:30:12.481Z",
  "requestId": "req-789",
  "ipHash": "sha256:9af6...",
  "deviceType": "MOBILE",
  "refererHost": "example.com",
  "producer": "miniurl-redirect-service"
}
```

## Required fields

```text
eventId       -> idempotency and tracing
schemaVersion -> contract evolution
urlId         -> partition and aggregation key
shortCode     -> debugging
clickedAt     -> event-time bucket
requestId     -> cross-service correlation
```

## Privacy

```text
prefer hashed IP over raw IP
store referrer host instead of full tokenized URL
avoid logging complete event payloads
define retention for raw metadata
```

## Evolution

```text
add optional fields safely
never silently rename or change field meaning
consumers ignore unknown fields
use a new version/topic for incompatible changes
consider Avro/Protobuf plus Schema Registry later
```

---

# 4. Topic and Partitioning

Topics:

```text
url-click-events-v1
url-click-events-dlt-v1
```

Optional explicit retry topic:

```text
url-click-events-retry-v1
```

## Consumer parallelism

```text
P0 ---> Consumer A
P1 ---> Consumer A
P2 ---> Consumer B
P3 ---> Consumer B
P4 ---> Consumer C
P5 ---> Consumer C
```

Rules:

```text
one partition has one active consumer per group
maximum active consumers in a group <= partition count
different groups independently receive the full stream
```

Example groups:

```text
url-click-events-v1
        |
        +--> analytics-counter-group
        +--> fraud-detection-group
        +--> realtime-dashboard-group
```

## Partition key

Default:

```text
key = urlId
```

```text
hash(urlId) % partitionCount -> partition
```

Benefits:

```text
stable distribution
per-URL ordering
simple debugging
```

Risk:

```text
viral URL -> one hot partition
```

Hot-key mitigation:

```text
key = urlId + ":" + shardBucket
```

```text
viral urlId=12345
      |
      +--> 12345:0 -> P2
      +--> 12345:1 -> P7
      +--> 12345:2 -> P11
```

Trade-off:

```text
more parallelism
less strict per-URL ordering
```

Click counting normally does not require strict ordering, so salting is acceptable for exceptional hot URLs.

---

# 5. Producer

## Redirect flow

```text
GET /{shortCode}
       |
       v
validate
       |
       v
Redis/Postgres lookup
       |
       v
build ClickEvent
       |
       +--> async Kafka send
       |
       v
return 302
```

Emit only after successful URL resolution. Do not emit for invalid, expired, disabled or missing links.

## Reliability choices

| Pattern | Latency | Durability | Complexity |
|---|---:|---:|---:|
| Async send | Lowest | Best effort | Low |
| Async callback | Low | Best effort + visibility | Low |
| Wait for broker ACK | Higher | Stronger | Medium |
| Transactional outbox | DB-dependent | Strongest | High |

Recommended for click analytics:

```text
async send with callback
record publish failure
do not fail redirect
```

For payments or business-critical events, an outbox is more appropriate.

## Producer properties

```properties
spring.kafka.producer.acks=all
spring.kafka.producer.properties.enable.idempotence=true
spring.kafka.producer.retries=2147483647
spring.kafka.producer.properties.max.in.flight.requests.per.connection=5
spring.kafka.producer.properties.linger.ms=5
spring.kafka.producer.properties.batch.size=65536
spring.kafka.producer.compression-type=lz4
spring.kafka.producer.properties.delivery.timeout.ms=30000
```

Why:

```text
acks=all          -> stronger broker acknowledgement
idempotence       -> protects against retry duplicates in producer session
linger + batch    -> better throughput with small latency trade-off
compression       -> lower network/storage cost
delivery timeout  -> bounded unresolved send
```

Producer idempotence does not replace consumer idempotency.

---

# 6. Consumer, Idempotency and Offsets

## Correct order

```text
poll event
   |
   v
begin DB transaction
   |
   +--> INSERT eventId
   |
   +--> if first processing:
   |       increment hourly
   |       increment daily
   |
   v
commit DB
   |
   v
acknowledge Kafka offset
```

Never acknowledge before the DB transaction.

Bad:

```text
ACK -> DB write -> DB fails
```

Result:

```text
Kafka considers event complete
analytics update is lost
```

## Delivery semantics

At-most-once:

```text
ACK before processing
possible loss
```

At-least-once:

```text
process then ACK
possible duplicate delivery
```

Recommended:

```text
Kafka delivery = at least once
business effect = effectively once through eventId deduplication
```

## Why aggregate upsert alone is not idempotent

```text
counter = 10

event E1 first delivery:
10 -> 11

event E1 redelivery:
11 -> 12
```

Atomic does not mean idempotent.

Correct transaction:

```text
INSERT E1 into processed_click_event

if inserted:
    increment
else:
    skip duplicate
```

## Crash windows

Before DB commit:

```text
transaction rolls back
offset is redelivered
safe
```

After DB commit but before ACK:

```text
event is redelivered
eventId already exists
counter increment is skipped
safe
```

## Event-time bucketing

Use:

```text
bucket = truncate(clickedAt)
```

Not:

```text
bucket = consumer processing time
```

Example:

```text
clickedAt  = 10:58
processed  = 11:10
bucket     = 10:00
```

Replay must produce the same historical bucket.

---

# 7. Database

## Deduplication

```sql
CREATE TABLE processed_click_event (
    event_id      VARCHAR(64) PRIMARY KEY,
    url_id        BIGINT NOT NULL,
    processed_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
```

## Hourly aggregate

```sql
CREATE TABLE url_click_stats_hourly (
    url_id       BIGINT NOT NULL,
    bucket_hour  TIMESTAMPTZ NOT NULL,
    total_clicks BIGINT NOT NULL DEFAULT 0,
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (url_id, bucket_hour)
);
```

```sql
INSERT INTO url_click_stats_hourly (
    url_id, bucket_hour, total_clicks
)
VALUES (:urlId, :bucketHour, :increment)
ON CONFLICT (url_id, bucket_hour)
DO UPDATE SET
    total_clicks =
        url_click_stats_hourly.total_clicks + EXCLUDED.total_clicks,
    updated_at = now();
```

## Daily aggregate

```sql
CREATE TABLE url_click_stats_daily (
    url_id       BIGINT NOT NULL,
    bucket_date  DATE NOT NULL,
    total_clicks BIGINT NOT NULL DEFAULT 0,
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (url_id, bucket_date)
);
```

Retention rule:

```text
dedup rows must live at least as long as replay/redelivery horizon
```

At larger scale:

```text
partition tables by time
batch increments
store raw events in object storage or OLAP system
```

---

# 8. Retry and DLT

Classify errors.

Transient:

```text
DB timeout
temporary network failure
deadlock
connection pool exhaustion
```

Permanent:

```text
invalid JSON
unsupported schema version
missing urlId
invalid clickedAt
```

Flow:

```text
main topic
    |
    v
consumer
    |
    +--> success -> DB -> ACK
    |
    +--> transient -> retry with backoff
    |
    +--> permanent / exhausted
                    |
                    v
                  DLT
```

Use bounded retries. Infinite retries can block one partition forever.

DLT metadata:

```text
original topic
partition
offset
key
safe payload/reference
exception type
failed timestamp
consumer version
requestId/eventId
```

A DLT needs:

```text
alert
dashboard
root-cause investigation
controlled replay
replay audit
```

---

# 9. Spring Boot Implementation

## Event

```java
public record ClickEvent(
        String eventId,
        String eventType,
        int schemaVersion,
        long urlId,
        String shortCode,
        Instant clickedAt,
        String requestId,
        String ipHash,
        String deviceType,
        String refererHost,
        String producer
) {}
```

## Producer

```java
@Component
public class ClickEventPublisher {

    private static final String TOPIC = "url-click-events-v1";

    private final KafkaTemplate<Long, ClickEvent> kafkaTemplate;
    private final Counter success;
    private final Counter failure;

    public ClickEventPublisher(
            KafkaTemplate<Long, ClickEvent> kafkaTemplate,
            MeterRegistry registry
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.success = registry.counter(
                "url_click_publish_total", "result", "success"
        );
        this.failure = registry.counter(
                "url_click_publish_total", "result", "failure"
        );
    }

    public void publish(ClickEvent event) {
        kafkaTemplate.send(TOPIC, event.urlId(), event)
                .whenComplete((result, error) -> {
                    if (error == null) {
                        success.increment();
                    } else {
                        failure.increment();
                        log.error(
                                "Click publish failed eventId={} urlId={} requestId={}",
                                event.eventId(),
                                event.urlId(),
                                event.requestId(),
                                error
                        );
                    }
                });
    }
}
```

## Transactional processor

```java
@Service
public class ClickAnalyticsProcessor {

    private final ClickAnalyticsRepository repository;

    public ClickAnalyticsProcessor(
            ClickAnalyticsRepository repository
    ) {
        this.repository = repository;
    }

    @Transactional
    public Result process(ClickEvent event) {
        validate(event);

        boolean first = repository.markProcessed(
                event.eventId(),
                event.urlId()
        );

        if (!first) {
            return Result.DUPLICATE;
        }

        Instant hour = event.clickedAt()
                .truncatedTo(ChronoUnit.HOURS);

        LocalDate day = event.clickedAt()
                .atZone(ZoneOffset.UTC)
                .toLocalDate();

        repository.incrementHourly(event.urlId(), hour, 1);
        repository.incrementDaily(event.urlId(), day, 1);

        return Result.PROCESSED;
    }

    private void validate(ClickEvent event) {
        if (event.schemaVersion() != 1) {
            throw new UnsupportedSchemaVersionException();
        }
        if (!"URL_CLICKED".equals(event.eventType())) {
            throw new InvalidClickEventException();
        }
    }

    public enum Result {
        PROCESSED,
        DUPLICATE
    }
}
```

## Dedup repository method

```java
public boolean markProcessed(String eventId, long urlId) {
    String sql = """
        INSERT INTO processed_click_event (
            event_id, url_id, processed_at
        )
        VALUES (:eventId, :urlId, now())
        ON CONFLICT (event_id) DO NOTHING
        """;

    return jdbc.update(
            sql,
            Map.of("eventId", eventId, "urlId", urlId)
    ) == 1;
}
```

## Consumer

```java
@Component
public class ClickAnalyticsConsumer {

    private final ClickAnalyticsProcessor processor;

    public ClickAnalyticsConsumer(
            ClickAnalyticsProcessor processor
    ) {
        this.processor = processor;
    }

    @KafkaListener(
            topics = "url-click-events-v1",
            groupId = "analytics-counter-group",
            containerFactory =
                    "clickEventKafkaListenerContainerFactory"
    )
    public void consume(
            ClickEvent event,
            Acknowledgment acknowledgment
    ) {
        processor.process(event);
        acknowledgment.acknowledge();
    }
}
```

If processing throws, ACK is not called.

## Error handler

```java
@Bean
DefaultErrorHandler clickErrorHandler(
        KafkaTemplate<Object, Object> template
) {
    DeadLetterPublishingRecoverer recoverer =
            new DeadLetterPublishingRecoverer(
                    template,
                    (record, error) -> new TopicPartition(
                            "url-click-events-dlt-v1",
                            record.partition()
                    )
            );

    ExponentialBackOff backOff = new ExponentialBackOff();
    backOff.setInitialInterval(1_000);
    backOff.setMultiplier(2);
    backOff.setMaxInterval(10_000);
    backOff.setMaxElapsedTime(30_000);

    DefaultErrorHandler handler =
            new DefaultErrorHandler(recoverer, backOff);

    handler.addNotRetryableExceptions(
            InvalidClickEventException.class,
            UnsupportedSchemaVersionException.class
    );

    return handler;
}
```

## Listener factory

```java
@Bean
ConcurrentKafkaListenerContainerFactory<Long, ClickEvent>
clickEventKafkaListenerContainerFactory(
        ConsumerFactory<Long, ClickEvent> consumerFactory,
        DefaultErrorHandler errorHandler
) {
    var factory =
            new ConcurrentKafkaListenerContainerFactory<Long, ClickEvent>();

    factory.setConsumerFactory(consumerFactory);
    factory.setCommonErrorHandler(errorHandler);
    factory.setConcurrency(4);
    factory.getContainerProperties().setAckMode(
            ContainerProperties.AckMode.MANUAL_IMMEDIATE
    );

    return factory;
}
```

## Consumer properties

```properties
spring.kafka.consumer.enable-auto-commit=false
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.max-poll-records=500
spring.kafka.consumer.properties.max.poll.interval.ms=300000
spring.kafka.listener.ack-mode=manual_immediate
```

Processing must remain safely below `max.poll.interval.ms`, otherwise Kafka may rebalance the consumer.

---

# 10. Scaling and Capacity

Assume:

```text
peak redirects = 100,000/sec
event size     = 500 bytes
```

Ingress:

```text
100,000 * 500 bytes
= 50 MB/sec before replication and overhead
```

Events/day at continuous peak:

```text
100,000 * 86,400
= 8.64 billion/day
```

Raw payload/day:

```text
8.64B * 500 bytes
~ 4.32 TB/day before replication and compression
```

This shows why unlimited raw retention is expensive.

## Partition estimate

Approximation:

```text
partitions =
max(
  peak producer throughput / safe producer throughput per partition,
  peak consumer throughput / safe consumer throughput per partition
)
```

Add headroom and load test.

Example consumer capacity:

```text
one consumer = 8,000 events/sec
required     = 100,000 / 8,000
             = 12.5
```

Use at least 13 active consumers; choose more partitions for headroom, such as 24 or 32 after benchmarking.

## Database pressure

Naive:

```text
100k events/sec * 2 aggregate updates
= 200k updates/sec
```

Evolution:

```text
Stage 1: record consumer -> PostgreSQL
Stage 2: batch/group increments -> PostgreSQL
Stage 3: stream pre-aggregation -> Redis/OLAP/snapshots
```

Batch concept:

```text
500 events
   |
   v
group by (urlId, hour)
   |
   v
few aggregate upserts
```

Scale based on metrics, not imagination.

---

# 11. Observability and Failures

## Metrics

Producer:

```text
publish TPS
publish failures
send latency
retry rate
batch size
compression ratio
```

Consumer:

```text
consume TPS
lag per partition
processing p95/p99
retry count
DLT count
duplicate rate
rebalance count
```

Database:

```text
transaction latency
upsert latency
deadlocks
pool usage
connection acquisition time
```

## Alerts

```text
lag grows continuously
publish failures exceed threshold
DLT receives sustained traffic
consumer TPS < producer TPS
DB p99 breaches budget
one partition lag is much higher than others
no consumption while publishing continues
```

## Failure 1: crash after DB commit

```text
DB committed
consumer crashes before ACK
event redelivered
eventId conflict
skip increment
ACK
```

Result: no double counting.

## Failure 2: DB outage

```text
DB timeout
retry with backoff
DB recovers -> success
or retries exhausted -> DLT + alert
```

## Failure 3: poison event

```text
bad record at offset 100
retry forever -> partition blocked
```

Correct:

```text
bounded retries -> DLT -> continue partition
```

## Failure 4: hot partition

Symptoms:

```text
one partition lag high
one consumer CPU high
others underused
```

Mitigation:

```text
salt hot urlId
batch/pre-aggregate
review partition count
```

## Debugging tree

```text
Analytics behind?
     |
     +--> producer failures?
     |       -> broker/network/serialization/auth
     |
     +--> consumer lag?
             |
             +--> all partitions
             |       -> consumer or DB capacity
             |
             +--> one partition
                     -> hot key or poison event

Lag low but counts wrong?
     |
     +--> duplicate handling
     +--> event-time/time-zone bug
     +--> reporting query bug
     +--> producer event loss
```

---

# 12. Testing

Unit tests:

```text
event validation
hour/day bucket calculation
unsupported schema
duplicate result
metadata sanitization
```

PostgreSQL Testcontainers:

```text
first event increments once
same eventId twice increments once
different eventIds increment twice
rollback leaves no dedup row or counter
```

Kafka integration:

```text
key=urlId
successful consume and ACK
retry on transient error
DLT on permanent error
redelivery does not double-count
```

End-to-end:

```text
HTTP redirect -> 302
             -> Kafka event
             -> eventual analytics row
```

Load tests:

```text
redirect p50/p95/p99
publish success/failure
consumer throughput
lag growth and recovery
DB saturation
hot-key behavior
rebalance during deployment
```

Critical resilience test:

```text
disable analytics DB
verify redirect remains healthy
observe retries/lag/DLT
restore DB
verify recovery
```

---

# 13. Interview Answers

## Why Kafka?

Kafka decouples the latency-sensitive redirect from slower analytics. It buffers spikes, supports replay, scales through partitions and lets multiple consumer groups process the same stream.

## Why not synchronous analytics?

A non-critical analytics database must not increase redirect p99 or cause redirect failures.

## Why not RabbitMQ?

RabbitMQ can work for task queues. Kafka fits a high-throughput replayable event stream with retention and multiple independent consumers. The final choice depends on requirements and operations.

## Delivery guarantee?

At least once. The DB transaction completes before ACK, and unique `eventId` prevents duplicate business effects.

## Why `urlId` as key?

Stable distribution and per-URL ordering. A viral URL may create a hot partition; salt exceptional keys when necessary.

## How many partitions?

Estimate from peak producer and consumer throughput, add headroom, and verify with load tests. Consumer parallelism cannot exceed partition count.

## How avoid double counting?

Insert `eventId` into a unique dedup table in the same transaction as counter updates. Duplicate delivery cannot insert and therefore does not increment.

## When commit offset?

After the DB transaction succeeds. Commit-before-write risks data loss.

## How handle lag?

Monitor per partition. All partitions lagging suggests overall consumer/DB capacity; one partition suggests a hot key or poison record.

## Why event time?

Lag and replay must not move historical clicks into the consumer's current hour/day.

## Is producer idempotence enough?

No. It reduces producer retry duplicates but does not solve consumer redelivery or replay.

## Kafka unavailable?

For best-effort click analytics, record publish failure but preserve redirect. If losslessness becomes mandatory, adopt a durable outbox/fallback and accept its cost.

---

# 14. One-Page Cheat Sheet

```text
GOAL
----
redirect fast
analytics async
analytics failure must not break redirect

FLOW
----
Client -> Redirect Service -> Redis/DB -> 302
                    |
                    v
             URL_CLICKED Kafka
                    |
                    v
      validate -> dedup -> aggregate -> DB -> ACK

EVENT
-----
eventId
schemaVersion
urlId
shortCode
clickedAt
requestId
privacy-safe metadata

TOPIC
-----
url-click-events-v1
url-click-events-dlt-v1

KEY
---
default: urlId
hot URL: urlId:shardBucket

PRODUCER
--------
acks=all
enable.idempotence=true
retries
small linger
batching
lz4
bounded delivery timeout
async callback
redirect survives publish failure

CONSUMER
--------
at-least-once
manual ACK
DB commit before ACK
bounded retry
DLT for poison event
event-time buckets

IDEMPOTENCY
-----------
INSERT unique eventId
if inserted -> increment
if duplicate -> skip

CRASH WINDOW
------------
DB commit, crash before ACK
-> redelivery
-> eventId conflict
-> no double count

SCALING
-------
parallel consumers <= partitions
batch/group DB updates
monitor lag per partition
salt hot keys

METRICS
-------
publish TPS/failure
consume TPS
lag
processing p95/p99
retry
DLT
duplicate rate
DB latency/pool
rebalance count

SENIOR ANSWER
-------------
"We publish an immutable URL_CLICKED event so redirect latency does not
depend on analytics. The topic is partitioned by urlId. Consumers process
at least once, insert eventId and update aggregates in one DB transaction,
then ACK. Redelivery is therefore safe. We bucket by event time, retry
transient failures, send poison records to a DLT, and monitor lag,
throughput, hot partitions and database latency."
```

## Final picture

```text
+--------+    +------------------+    +-------------+
| Client | -> | Redirect Service | -> | Redis / DB  |
+--------+    +------------------+    +-------------+
                       |
                       | URL_CLICKED, key=urlId
                       v
             +----------------------+
             | Kafka                |
             | partitions/retention |
             +----------------------+
                       |
                       v
             +----------------------+
             | Analytics Consumer   |
             | at-least-once        |
             +----------------------+
                       |
             +---------+----------+
             |                    |
             v                    v
+----------------------+  +----------------------+
| processed_event      |  | hourly/daily stats   |
| eventId PRIMARY KEY  |  | atomic UPSERT        |
+----------------------+  +----------------------+
             |
             v
       DB COMMIT -> ACK

transient failure -> bounded retry
poison/exhausted  -> DLT + alert
hot URL           -> salted key
```

```text
FAST REDIRECT
+ DURABLE BUFFER
+ IDEMPOTENT CONSUMER
+ EVENT-TIME AGGREGATION
+ LAG / RETRY / DLT OBSERVABILITY
= PRODUCTION-SHAPED CLICK ANALYTICS
```
