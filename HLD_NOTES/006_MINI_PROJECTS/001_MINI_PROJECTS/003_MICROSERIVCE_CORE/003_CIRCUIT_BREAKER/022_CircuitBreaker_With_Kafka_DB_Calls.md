# 022_CircuitBreaker_With_Kafka_DB_Calls.md

# MiniCircuitBreaker — 022 CircuitBreaker With Kafka DB Calls

---

# 1. Why This File Exists

Previous file explained circuit breaker with:

```text
RestTemplate
RestClient
WebClient
HTTP calls
```

But production systems also fail during:

```text
Kafka message processing
database calls
external API calls inside consumers
transactional writes
batch jobs
async workflows
```

Kafka and DB failures are different from normal HTTP request failures because they involve:

```text
message retry
consumer lag
offset commits
DLT / DLQ
idempotency
transactions
duplicate processing
poison messages
DB locks
deadlocks
connection pool exhaustion
```

This file explains:

```text
CircuitBreaker with Kafka consumers
CircuitBreaker with DB calls
when to use CB around Kafka processing
when NOT to use CB
consumer retries
DLT / DLQ
DB timeout
JPA transaction safety
idempotency
outbox pattern
consumer lag protection
production async resiliency
```

---

# 2. One-Line Definition

```text
CircuitBreaker protects Kafka consumers and DB-dependent workflows from repeatedly hitting unhealthy dependencies.
```

---

# 3. Biggest Mental Model

```text
Message / Request
       ↓
Processing Logic
       ↓
DB or External Dependency
       ↓
CircuitBreaker protects repeated failure
```

---

# 4. Why Kafka + DB Needs Different Thinking

HTTP request:

```text
client waits for response
```

Kafka message:

```text
message can be retried later
```

DB call:

```text
may be inside transaction
```

So failure handling must consider:

```text
offset commits
duplicates
transaction rollback
idempotency
DLT
```

---

# 5. Kafka Consumer Failure Problem

Kafka consumer receives message.

Then calls:

```text
external payment API
```

If payment API is down:

```text
consumer keeps retrying
same dependency keeps failing
consumer lag increases
thread pool gets blocked
```

Circuit breaker prevents repeated calls.

---

# 6. Kafka Failure ASCII

```text
Kafka Topic
    ↓
Consumer
    ↓
External API Down
    ↓
Retry Same Message
    ↓
Consumer Lag Grows
```

---

# 7. CircuitBreaker In Kafka Consumer

Circuit breaker wraps:

```text
dependency call inside message processing
```

Example:

```text
consumer → payment API
consumer → DB
consumer → notification provider
```

---

# 8. Kafka Consumer With CB ASCII

```text
Kafka Message
      ↓
Consumer
      ↓
CircuitBreaker
      ↓
External Dependency
```

---

# 9. Important Kafka Difference

If circuit is OPEN:

```text
do not call dependency
```

But now question:

```text
what to do with message?
```

Options:

```text
retry later
pause consumer
send to DLT
store for replay
mark as pending
```

---

# 10. Kafka Message Handling Options

When dependency unavailable:

```text
retry same message
backoff
pause partition
send to retry topic
send to DLT
store pending state
```

---

# 11. Do Not Infinite Retry

Bad:

```text
while true:
    retry message
```

This causes:

```text
consumer stuck
lag explosion
resource waste
```

---

# 12. Poison Message

Poison message means:

```text
message will always fail
```

Example:

```text
invalid schema
missing required field
bad business data
```

Circuit breaker should not solve poison messages.

DLT should.

---

# 13. Poison Message ASCII

```text
Bad Message
   ↓
Retry
   ↓
Fails Again
   ↓
Retry Forever
```

Correct:

```text
send to DLT
```

---

# 14. Dependency Failure vs Poison Message

## Dependency Failure

```text
external service down
DB unavailable
network timeout
```

Circuit breaker useful.

## Poison Message

```text
invalid payload
business validation failure
bad schema
```

DLT useful.

---

# 15. Kafka Retry Topics

Production Kafka often uses:

```text
main topic
retry topic
DLT topic
```

---

# 16. Retry Topic Flow

```text
main-topic
    ↓ failure
retry-topic-1
    ↓ failure
retry-topic-2
    ↓ failure
DLT
```

---

# 17. Why Retry Topics Better

Retry topics avoid:

```text
blocking main consumer forever
```

Message retried later.

Main topic continues processing.

---

# 18. Kafka CircuitBreaker Flow

```text
Message Received
      ↓
Validate Message
      ↓
CircuitBreaker Allows?
      ↓
Call Dependency
      ↓
Success?
```

If circuit OPEN:

```text
send to retry topic
or pause consumer
or fallback
```

---

# 19. Kafka Listener Example

```java
@KafkaListener(
        topics = "payments",
        groupId = "payment-consumer"
)
public void consume(String message) {

    processPayment(message);
}
```

---

# 20. Kafka Listener With CircuitBreaker

```java
@KafkaListener(
        topics = "payments",
        groupId = "payment-consumer"
)
@CircuitBreaker(
        name = "bankApi",
        fallbackMethod = "bankFallback"
)
public void consume(String message) {

    callBankApi(message);
}

public void bankFallback(
        String message,
        Exception ex) {

    sendToRetryTopic(message);
}
```

---

# 21. Fallback For Kafka Is Different

HTTP fallback returns response.

Kafka fallback should usually:

```text
republish to retry topic
store pending state
send to DLT
pause processing
```

---

# 22. Kafka Fallback Mental Model

```text
Do not fake success.
Preserve message for later.
```

---

# 23. Offset Commit Problem

If consumer commits offset after failure:

```text
message lost
```

If consumer does not commit:

```text
message retried
```

Need careful policy.

---

# 24. Offset Commit ASCII

```text
Process Message
   ↓
Success?
   ↓
Commit Offset

Failure?
   ↓
Do Not Commit / Send Retry / DLT
```

---

# 25. Manual Ack Pattern

With manual acknowledgment:

```java
@KafkaListener(topics = "orders")
public void consume(
        String message,
        Acknowledgment ack) {

    try {

        process(message);

        ack.acknowledge();

    } catch (Exception ex) {

        sendToRetryTopic(message);
    }
}
```

---

# 26. Why Manual Ack Useful

Manual ack gives control:

```text
commit only after successful processing
```

Very important for reliability.

---

# 27. Consumer Lag Problem

If dependency down:

```text
messages accumulate
```

This creates:

```text
consumer lag
```

Circuit breaker helps avoid wasting calls.

---

# 28. Consumer Lag ASCII

```text
Topic messages increase
      ↓
Consumer processing stuck
      ↓
Lag grows
```

---

# 29. Pause Consumer Strategy

If dependency down, sometimes:

```text
pause Kafka consumer
```

This prevents:

```text
hot loop retry
```

---

# 30. Retry Backoff In Kafka

Kafka retries should use:

```text
backoff
jitter
retry topic delay
```

Avoid immediate retry storm.

---

# 31. DLT / DLQ

Dead Letter Topic / Queue stores:

```text
messages that cannot be processed
```

for later inspection.

---

# 32. DLT Contents

DLT message should include:

```text
original payload
error reason
timestamp
attempt count
correlation id
source topic
```

---

# 33. DB Call Failure Problem

Database calls can fail due to:

```text
connection timeout
lock wait timeout
deadlock
slow query
connection pool exhaustion
transaction rollback
constraint violation
```

Not all DB errors should be treated equally.

---

# 34. DB Failure Classification

Retryable DB errors:

```text
deadlock
lock wait timeout
temporary connection issue
transient network failure
```

Non-retryable DB errors:

```text
syntax error
constraint violation
invalid data
missing required field
```

---

# 35. DB CircuitBreaker Use Case

Circuit breaker useful when DB is:

```text
unavailable
overloaded
slow
connection pool exhausted
```

But careful:

```text
do not hide data consistency errors
```

---

# 36. DB Call With CircuitBreaker

```java
@CircuitBreaker(
        name = "database",
        fallbackMethod = "dbFallback"
)
public Account loadAccount(Long id) {

    return accountRepository
            .findById(id)
            .orElseThrow();
}

public Account dbFallback(
        Long id,
        Exception ex) {

    throw new ServiceUnavailableException(
            "Database unavailable"
    );
}
```

---

# 37. DB Fallback Warning

For DB writes, fallback is dangerous.

Never fake:

```text
write success
```

Correct fallback:

```text
fail fast
store command for later
return pending
```

---

# 38. Read Fallback vs Write Fallback

## Read Fallback

May return:

```text
cache
stale data
empty result
```

## Write Fallback

Should preserve correctness:

```text
pending state
outbox
retry later
error
```

---

# 39. DB Read Fallback Example

```java
public Account dbFallback(
        Long id,
        Exception ex) {

    return cache.getAccount(id);
}
```

Acceptable if stale data allowed.

---

# 40. DB Write Fallback Example

```text
Order creation DB down
      ↓
Do NOT return ORDER_CREATED
      ↓
Return ORDER_PENDING or fail safely
```

---

# 41. Transaction Boundary Problem

Circuit breaker should not wrap too broadly around:

```text
large transaction with many operations
```

because fallback behavior becomes unclear.

---

# 42. Correct DB Protection Boundary

Prefer wrapping:

```text
specific external dependency/DB call
```

with clear semantics.

---

# 43. JPA Transaction Example

```java
@Transactional
public void transfer(
        Long from,
        Long to,
        BigDecimal amount) {

    debit(from, amount);
    credit(to, amount);
}
```

If failure occurs:

```text
transaction rolls back
```

Do not fallback to partial success.

---

# 44. Pessimistic Locking Failure

Example:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select a from Account a where a.id = :id")
Account findByIdForUpdate(Long id);
```

Possible failures:

```text
lock wait timeout
deadlock
transaction timeout
```

Some may be retryable.

---

# 45. Deadlock Retry

Deadlocks are often transient.

Safe retry only if operation is:

```text
idempotent
transactionally safe
bounded attempts
```

---

# 46. Idempotency In Kafka + DB

Kafka may deliver message more than once.

Therefore processing must be:

```text
idempotent
```

---

# 47. Idempotency Key

Use message id / event id:

```text
eventId
orderId
paymentId
```

Store processed ids.

---

# 48. Idempotent Consumer Pattern

```text
Receive message
      ↓
Check processed_event table
      ↓
Already processed?
   yes/no
      ↓
Skip or process
```

---

# 49. Idempotent Consumer Java Sketch

```java
@Transactional
public void processPayment(Event event) {

    if (processedEventRepository
            .existsById(event.id())) {

        return;
    }

    paymentRepository.save(
            event.toPayment()
    );

    processedEventRepository.save(
            new ProcessedEvent(event.id())
    );
}
```

---

# 50. Why Idempotency Critical

Without idempotency:

```text
Kafka retry
      ↓
duplicate DB write
      ↓
duplicate payment/order
```

---

# 51. Outbox Pattern

Outbox solves:

```text
DB update + Kafka publish consistency
```

Write event into DB outbox table in same transaction.

Separate publisher sends to Kafka.

---

# 52. Outbox ASCII

```text
Business DB Update
      +
Outbox Event Insert
      ↓ same transaction
Commit
      ↓
Publisher sends event to Kafka
```

---

# 53. CircuitBreaker With Outbox

If Kafka down:

```text
outbox retains event
publisher retries later
```

No need to fake success.

---

# 54. DB Connection Pool Exhaustion

If DB slow:

```text
connections stay busy
```

Then:

```text
new DB requests wait
```

Eventually application stalls.

---

# 55. DB Pool ASCII

```text
Hikari Pool

[C1][C2][C3][C4][C5]

All busy on slow queries
```

---

# 56. DB Protection Stack

Use:

```text
query timeout
transaction timeout
connection pool timeout
bulkhead
circuit breaker
read cache fallback
```

---

# 57. Hikari Timeout Concepts

Important configs:

```text
connectionTimeout
validationTimeout
idleTimeout
maxLifetime
maximumPoolSize
```

---

# 58. Query Timeout

Query timeout prevents:

```text
SQL running forever
```

---

# 59. Transaction Timeout

Transaction timeout prevents:

```text
long transactions holding locks forever
```

---

# 60. DB Slow Call Rate

Circuit breaker can open if:

```text
many DB calls are slow
```

This protects app from DB overload.

---

# 61. Kafka + CircuitBreaker Anti-Pattern

Bad:

```text
Circuit OPEN
      ↓
commit Kafka offset as success
```

This loses message.

---

# 62. Correct Pattern

If circuit OPEN:

```text
do not lose message
```

Use:

```text
retry topic
pause
DLT
pending store
```

---

# 63. Programmatic CB For Kafka

```java
CircuitBreaker cb =
        registry.circuitBreaker("bankApi");

Supplier<Void> decorated =
        CircuitBreaker.decorateSupplier(
                cb,
                () -> {
                    callBankApi();
                    return null;
                }
        );

Try.ofSupplier(decorated)
        .recover(ex -> {
            sendToRetryTopic();
            return null;
        });
```

---

# 64. Programmatic CB For DB

```java
Supplier<Account> decorated =
        CircuitBreaker.decorateSupplier(
                dbCircuitBreaker,
                () -> accountRepository
                        .findById(id)
                        .orElseThrow()
        );

Account account =
        Try.ofSupplier(decorated)
                .recover(ex -> cache.get(id))
                .get();
```

---

# 65. Kafka Observability

Monitor:

```text
consumer lag
retry topic size
DLT size
processing latency
failure rate
circuit OPEN count
```

---

# 66. DB Observability

Monitor:

```text
DB latency p95/p99
connection pool usage
lock wait time
deadlocks
query timeout count
circuit OPEN count
```

---

# 67. Production Scenario — Bank API Down

Payment events arrive in Kafka.

Bank API down.

Bad system:

```text
consumer retries hot loop
lag grows
threads blocked
bank API hammered
```

Good system:

```text
circuit opens
events go retry topic
consumer continues
bank API recovers
messages replay safely
```

---

# 68. Production Scenario — DB Slow

DB queries slow.

Bad system:

```text
all Hikari connections busy
request threads block
service down
```

Good system:

```text
query timeout
bulkhead
circuit breaker
read cache fallback
alerts fire
```

---

# 69. Common Mistakes

## Mistake 1

```text
Committing Kafka offset after failed processing
```

Message loss.

---

## Mistake 2

```text
Infinite retry on poison message
```

Consumer stuck.

---

## Mistake 3

```text
Fake success for DB write fallback
```

Data corruption.

---

## Mistake 4

```text
No idempotency
```

Duplicate processing.

---

## Mistake 5

```text
No DLT
```

Bad messages block pipeline.

---

## Mistake 6

```text
Circuit breaker around business validation
```

False OPEN.

---

# 70. Most Important Insight

```text
Kafka/DB circuit breaker is not only about failing fast.
It is about preserving correctness while controlling failure.
```

---

# 71. Distributed Systems Insight

Async systems need:

```text
replayability
idempotency
DLT
backoff
offset safety
```

Circuit breaker is only one part.

---

# 72. Interview Explanation

If interviewer asks:

```text
How do you use CircuitBreaker with Kafka and DB calls?
```

Strong answer:

```text
For Kafka consumers, I wrap unstable downstream calls with circuit breaker
and use retry topics or DLT instead of losing messages. For DB calls, I
combine query timeout, transaction timeout, connection pool limits,
idempotency, and safe fallback depending on read or write semantics.
```

Senior addition:

```text
Kafka and DB resiliency must preserve correctness. Circuit breaker should
not commit failed messages as success or fake successful writes. Idempotent
consumers, DLT, outbox pattern, and observability are critical.
```

---

# 73. Final Mental Model

```text
HTTP Failure:
return fallback to user

Kafka Failure:
preserve message for retry/DLT

DB Failure:
preserve transaction correctness
```

---

# 74. What To Remember

```text
Kafka failures need offset safety.

Poison messages go to DLT.

Dependency failures can use circuit breaker.

DB reads can use cache fallback.

DB writes must not fake success.

Kafka consumers must be idempotent.

DB transactions must rollback safely.

Outbox helps DB + Kafka consistency.

Monitor lag, DLT, DB pool, p99 latency.

Circuit breaker is part of a larger async resiliency strategy.
```

---

# 75. Next File

```text
023_Resilience4j_Internal_Model.md
```

Next you learn:

```text
Resilience4j internal architecture
CircuitBreakerRegistry
state machine implementation
sliding window metrics
event publisher
decorators
annotations
Spring Boot integration internals
```
