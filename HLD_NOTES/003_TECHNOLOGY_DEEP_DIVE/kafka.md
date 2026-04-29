# Kafka + Spring Boot Mastery: From Newbie to Production-Ready Pro

A hands-on, chapter-wise guide for learning Apache Kafka with Spring Boot, from fundamentals to production deployment.

---

## Table of Contents

1. [Kafka Basics](#chapter-1-kafka-basics)
2. [Local Setup](#chapter-2-local-setup)
3. [Spring Boot Producer](#chapter-3-spring-boot-producer)
4. [Spring Boot Consumer](#chapter-4-spring-boot-consumer)
5. [Partitions and Consumer Groups](#chapter-5-partitions-and-consumer-groups)
6. [Retry and Dead Letter Topic](#chapter-6-retry-and-dead-letter-topic)
7. [Manual Acknowledgment](#chapter-7-manual-acknowledgment)
8. [Testing](#chapter-8-testing)
9. [Transactions](#chapter-9-transactions)
10. [Schema Registry](#chapter-10-schema-registry)
11. [Security](#chapter-11-security)
12. [Production Deployment](#chapter-12-production-deployment)
13. [Final Hands-On Project](#final-hands-on-project)

---

# Chapter 1: Kafka Basics

## 1.1 What is Kafka?

Apache Kafka is a distributed event streaming platform. In simple words, it allows applications to send, store, and read streams of events.

Instead of one service directly calling another service every time something happens, a service can publish an event to Kafka. Other services can consume that event independently.

Example:

```text
Order Service publishes: OrderCreated
Payment Service consumes: OrderCreated
Email Service consumes: OrderCreated
Inventory Service consumes: OrderCreated
```

This makes systems more scalable, loosely coupled, and resilient.

## 1.2 Kafka Mental Model

Kafka is best understood as a distributed, append-only log.

Messages are appended to topics. Consumers read messages using offsets. Kafka keeps messages for a configured retention time, even after they are consumed.

This is different from traditional queues where messages usually disappear after consumption.

## 1.3 Core Kafka Terms

### Broker

A broker is a Kafka server. A Kafka cluster has one or more brokers.

### Topic

A topic is a named stream of records.

Examples:

```text
orders
payments
notifications
user-events
```

### Partition

A topic is split into partitions. Partitions allow Kafka to scale horizontally.

Example:

```text
orders topic
 ├── partition 0
 ├── partition 1
 └── partition 2
```

### Offset

An offset is the position of a message inside a partition.

Example:

```text
partition 0:
offset 0 -> first message
offset 1 -> second message
offset 2 -> third message
```

### Producer

A producer writes messages to Kafka.

### Consumer

A consumer reads messages from Kafka.

### Consumer Group

A consumer group is a group of consumers sharing work. Kafka assigns partitions to consumers inside a group.

## 1.4 Message Structure

A Kafka message has:

```text
Key
Value
Headers
Timestamp
```

Example:

```json
{
  "key": "order-123",
  "value": {
    "orderId": "order-123",
    "amount": 150.00
  }
}
```

The key is important because Kafka uses it to choose the partition.

## 1.5 Ordering Rule

Kafka guarantees ordering only within a partition.

If you want all events for the same order to stay ordered, use the same key.

```text
Same key -> same partition -> ordered events for that key
```

Good key examples:

```text
orderId
customerId
paymentId
```

Bad key examples:

```text
random UUID for every event
null key when ordering matters
```

## 1.6 Delivery Guarantees

### At Most Once

The message may be lost, but it is not processed twice.

### At Least Once

The message is not lost, but it may be processed more than once.

This is common in Kafka systems.

### Exactly Once

Kafka supports exactly-once semantics in specific scenarios using idempotent producers and transactions. This is advanced and requires careful design.

## 1.7 Common Beginner Mistakes

1. Thinking Kafka is just a queue.
2. Ignoring message keys.
3. Creating too few partitions.
4. Assuming global ordering across a topic.
5. Writing consumers that are not idempotent.
6. Ignoring dead-letter topics.
7. Not monitoring consumer lag.

## 1.8 Exercise

Design topics for an e-commerce system.

Suggested topics:

```text
orders
payments
shipments
notifications
inventory-events
```

Suggested key:

```text
orderId
```

---

# Chapter 2: Local Setup

## 2.1 Goal

In this chapter, you will run Kafka locally using Docker and test it using command-line tools.

## 2.2 Docker Compose Setup

Create a file named `docker-compose.yml`:

```yaml
services:
  kafka:
    image: apache/kafka:latest
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_LISTENERS: PLAINTEXT://:9092,CONTROLLER://:9093
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@localhost:9093
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

Start Kafka:

```bash
docker compose up -d
```

Check container:

```bash
docker ps
```

## 2.3 Create a Topic

```bash
docker exec -it kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create \
  --topic orders \
  --partitions 3 \
  --replication-factor 1
```

## 2.4 List Topics

```bash
docker exec -it kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --list
```

## 2.5 Describe Topic

```bash
docker exec -it kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --describe \
  --topic orders
```

## 2.6 Test Console Producer

```bash
docker exec -it kafka /opt/kafka/bin/kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \
  --topic orders
```

Type:

```text
hello kafka
order created
payment received
```

## 2.7 Test Console Consumer

Open another terminal:

```bash
docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic orders \
  --from-beginning
```

You should see the messages.

## 2.8 Exercise

Create these topics:

```text
payments
notifications
orders.DLT
```

---

# Chapter 3: Spring Boot Producer

## 3.1 Goal

Create a Spring Boot REST API that publishes order events to Kafka.

## 3.2 Create Project

Use Spring Initializr with:

```text
Java 21
Maven
Spring Boot
Dependencies:
- Spring Web
- Spring for Apache Kafka
- Validation
- Actuator
```

## 3.3 Maven Dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

## 3.4 Application Configuration

Create `application.yml`:

```yaml
spring:
  application:
    name: order-service

  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

server:
  port: 8080
```

## 3.5 Event Model

```java
package com.example.orderservice.order;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderCreatedEvent(
        String eventId,
        String orderId,
        String customerId,
        BigDecimal amount,
        Instant createdAt
) {}
```

## 3.6 Request Model

```java
package com.example.orderservice.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank String customerId,
        @DecimalMin("0.01") BigDecimal amount
) {}
```

## 3.7 Producer Service

```java
package com.example.orderservice.order;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private static final String TOPIC = "orders";

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(OrderCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.orderId(), event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        System.err.println("Failed to send event: " + exception.getMessage());
                    } else {
                        var metadata = result.getRecordMetadata();
                        System.out.printf(
                                "Sent event to topic=%s partition=%d offset=%d%n",
                                metadata.topic(),
                                metadata.partition(),
                                metadata.offset()
                        );
                    }
                });
    }
}
```

## 3.8 REST Controller

```java
package com.example.orderservice.order;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderProducer producer;

    public OrderController(OrderProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public String createOrder(@Valid @RequestBody CreateOrderRequest request) {
        var event = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                request.customerId(),
                request.amount(),
                Instant.now()
        );

        producer.send(event);

        return event.orderId();
    }
}
```

## 3.9 Test Producer

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"customer-1","amount":99.50}'
```

## 3.10 What Happens Internally

```text
HTTP request
 -> Controller
 -> Event object created
 -> KafkaTemplate sends event
 -> JsonSerializer converts object to bytes
 -> Kafka broker stores message in orders topic
```

## 3.11 Common Mistakes

1. Forgetting serializer configuration.
2. Using a random key when ordering matters.
3. Ignoring send failures.
4. Sending huge messages.
5. Putting business logic inside the controller.

---

# Chapter 4: Spring Boot Consumer

## 4.1 Goal

Create a Kafka consumer that reads order events.

## 4.2 Consumer Configuration

Add this to `application.yml`:

```yaml
spring:
  kafka:
    consumer:
      group-id: payment-service
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.example.orderservice.order"
```

## 4.3 Consumer Service

```java
package com.example.orderservice.order;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    @KafkaListener(topics = "orders", groupId = "payment-service")
    public void consume(OrderCreatedEvent event) {
        System.out.println("Received event: " + event);
    }
}
```

## 4.4 What Happens Internally

```text
Kafka broker
 -> Consumer polls records
 -> JsonDeserializer converts bytes to Java object
 -> Spring calls @KafkaListener method
 -> Method processes the event
 -> Offset is committed
```

## 4.5 Consumer Group Explanation

If multiple consumers use the same group id, Kafka shares partitions among them.

If multiple consumers use different group ids, each group receives its own copy of the messages.

Example:

```text
payment-service group receives OrderCreated
email-service group also receives OrderCreated
analytics-service group also receives OrderCreated
```

## 4.6 Exercise

Create another listener with group id `email-service` and verify both consumers receive the same event.

---

# Chapter 5: Partitions and Consumer Groups

## 5.1 Why Partitions Matter

Partitions provide parallelism.

If a topic has 3 partitions, up to 3 consumers in the same group can actively process messages.

```text
orders topic with 3 partitions
Consumer A -> partition 0
Consumer B -> partition 1
Consumer C -> partition 2
```

## 5.2 Rule

```text
Maximum active consumers in a group = number of partitions
```

If you have 3 partitions and 5 consumers, 2 consumers will be idle.

## 5.3 Configure Listener Concurrency

```yaml
spring:
  kafka:
    listener:
      concurrency: 3
```

## 5.4 Read Metadata in Consumer

```java
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

@KafkaListener(topics = "orders", groupId = "payment-service")
public void consume(
        OrderCreatedEvent event,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) long offset
) {
    System.out.printf("Event=%s partition=%d offset=%d%n", event, partition, offset);
}
```

## 5.5 Partitioning Strategy

Kafka uses the key to select the partition.

```text
partition = hash(key) % number_of_partitions
```

## 5.6 Production Advice

Choose partition count carefully. Increasing partitions later is possible, but it can change key distribution and ordering behavior.

---

# Chapter 6: Retry and Dead Letter Topic

## 6.1 Why Retry Exists

Consumers fail for many reasons:

```text
Database unavailable
External API timeout
Bad data
Temporary network issue
Bug in code
```

Temporary failures should be retried. Permanent failures should be sent to a dead-letter topic.

## 6.2 Create DLT Topic

```java
package com.example.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic ordersTopic() {
        return TopicBuilder.name("orders")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ordersDltTopic() {
        return TopicBuilder.name("orders.DLT")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
```

## 6.3 Error Handler Configuration

```java
package com.example.orderservice.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorConfig {

    @Bean
    public DefaultErrorHandler defaultErrorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        var recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> new TopicPartition(record.topic() + ".DLT", record.partition())
        );

        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
    }
}
```

## 6.4 DLT Listener

```java
@KafkaListener(topics = "orders.DLT", groupId = "order-dlt-service")
public void consumeDlt(OrderCreatedEvent event) {
    System.err.println("Received failed event in DLT: " + event);
}
```

## 6.5 Simulate Failure

```java
@KafkaListener(topics = "orders", groupId = "payment-service")
public void consume(OrderCreatedEvent event) {
    if (event.amount().intValue() == 999) {
        throw new RuntimeException("Simulated failure");
    }
    System.out.println("Processed event: " + event);
}
```

Send:

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"customer-1","amount":999}'
```

## 6.6 Common Mistakes

1. Infinite retries blocking partitions.
2. No DLT.
3. Not alerting on DLT messages.
4. Treating all exceptions the same.
5. Retrying non-retryable validation errors.

---

# Chapter 7: Manual Acknowledgment

## 7.1 What is Acknowledgment?

Acknowledgment controls when Kafka considers a message processed.

By default, Spring Kafka can commit offsets automatically depending on configuration.

Manual acknowledgment gives you more control.

## 7.2 Configuration

```yaml
spring:
  kafka:
    listener:
      ack-mode: manual
```

## 7.3 Consumer with Manual Ack

```java
import org.springframework.kafka.support.Acknowledgment;

@KafkaListener(topics = "orders", groupId = "payment-service")
public void consume(OrderCreatedEvent event, Acknowledgment acknowledgment) {
    try {
        System.out.println("Processing event: " + event);
        acknowledgment.acknowledge();
    } catch (Exception exception) {
        throw exception;
    }
}
```

## 7.4 Why Manual Ack Matters

Only acknowledge after successful processing.

Bad:

```text
ack first -> process later -> crash -> message lost logically
```

Good:

```text
process first -> ack after success
```

## 7.5 Idempotent Consumer

Because Kafka commonly uses at-least-once delivery, the same message may be processed more than once.

Use an idempotency table:

```sql
CREATE TABLE processed_events (
    event_id VARCHAR(100) PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL
);
```

Pseudo-code:

```java
if (alreadyProcessed(event.eventId())) {
    acknowledgment.acknowledge();
    return;
}

processBusinessLogic(event);
markProcessed(event.eventId());
acknowledgment.acknowledge();
```

---

# Chapter 8: Testing

## 8.1 Why Testing Kafka Code Matters

Kafka bugs often appear only during integration:

```text
wrong serializer
wrong topic name
wrong group id
deserialization failure
retry not working
DLT not configured
```

## 8.2 Unit Test Producer

```java
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.Instant;

class OrderProducerTest {

    @Test
    void shouldSendOrderEvent() {
        KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        OrderProducer producer = new OrderProducer(kafkaTemplate);

        var event = new OrderCreatedEvent(
                "event-1",
                "order-1",
                "customer-1",
                BigDecimal.TEN,
                Instant.now()
        );

        producer.send(event);

        Mockito.verify(kafkaTemplate).send("orders", "order-1", event);
    }
}
```

## 8.3 Add Test Dependency

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka-test</artifactId>
    <scope>test</scope>
</dependency>
```

## 8.4 Embedded Kafka Test

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"orders"})
class KafkaIntegrationTest {

    @Autowired
    private OrderProducer producer;

    @Test
    void shouldPublishMessage() {
        // Build event and publish it.
        // In a real test, consume it using a test consumer and assert payload.
    }
}
```

## 8.5 Better Production-Like Testing

Use Testcontainers with a real Kafka container when possible.

Recommended tests:

1. Producer publishes valid event.
2. Consumer processes valid event.
3. Invalid event goes to DLT.
4. Consumer does not process duplicate event twice.
5. Manual ack works correctly.

---

# Chapter 9: Transactions

## 9.1 Why Transactions?

Transactions are useful when you need atomic Kafka writes.

Example:

```text
send event to payments
send event to audit-events
both succeed or both fail
```

## 9.2 Enable Transactions

```yaml
spring:
  kafka:
    producer:
      transaction-id-prefix: order-tx-
```

## 9.3 Transactional Producer

```java
package com.example.orderservice.order;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionalPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TransactionalPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String key, Object event) {
        kafkaTemplate.executeInTransaction(operations -> {
            operations.send("orders", key, event);
            operations.send("audit-events", key, event);
            return true;
        });
    }
}
```

## 9.4 Important Warning

Kafka transactions do not automatically make your database transaction and Kafka transaction atomic together.

For database + Kafka consistency, use the Outbox Pattern.

---

# Chapter 10: Schema Registry

## 10.1 Why Schema Management Matters

If producers and consumers exchange JSON without rules, changes can break consumers.

Example breaking change:

```json
{
  "amount": 99.50
}
```

Changed to:

```json
{
  "totalAmount": 99.50
}
```

Old consumers expecting `amount` may fail.

## 10.2 Event Versioning

Use explicit versioning:

```java
public record EventEnvelope<T>(
        String eventId,
        String eventType,
        int eventVersion,
        String occurredAt,
        T payload
) {}
```

Example:

```json
{
  "eventId": "event-1",
  "eventType": "OrderCreated",
  "eventVersion": 1,
  "occurredAt": "2026-04-29T10:00:00Z",
  "payload": {
    "orderId": "order-1",
    "amount": 99.50
  }
}
```

## 10.3 Schema Technologies

Common options:

```text
Avro
Protobuf
JSON Schema
```

## 10.4 Compatibility Rules

Prefer backward-compatible changes:

Good:

```text
Add optional field
Add field with default value
```

Bad:

```text
Remove required field
Rename field
Change field type
```

---

# Chapter 11: Security

## 11.1 Why Kafka Security Matters

Kafka often carries business-critical data.

Protect:

```text
customer data
payment events
order information
audit logs
```

## 11.2 Security Layers

1. Encryption in transit using SSL/TLS.
2. Authentication using SASL or mTLS.
3. Authorization using ACLs.
4. Secret management.
5. Network restrictions.

## 11.3 SASL SSL Example

```yaml
spring:
  kafka:
    bootstrap-servers: your-kafka-broker:9093
    properties:
      security.protocol: SASL_SSL
      sasl.mechanism: PLAIN
      sasl.jaas.config: >
        org.apache.kafka.common.security.plain.PlainLoginModule required
        username="${KAFKA_USERNAME}"
        password="${KAFKA_PASSWORD}";
```

## 11.4 Best Practices

1. Never hardcode secrets.
2. Use environment variables or secret managers.
3. Use TLS in production.
4. Use ACLs per service.
5. Separate dev, staging, and production clusters.
6. Rotate credentials.

---

# Chapter 12: Production Deployment

## 12.1 Producer Production Config

```yaml
spring:
  kafka:
    producer:
      properties:
        acks: all
        enable.idempotence: true
        retries: 10
        compression.type: snappy
        linger.ms: 5
```

Explanation:

```text
acks=all -> wait for all in-sync replicas
enable.idempotence=true -> reduce duplicates from producer retries
retries=10 -> retry transient failures
compression.type=snappy -> reduce network usage
linger.ms=5 -> small batching delay for throughput
```

## 12.2 Consumer Production Config

```yaml
spring:
  kafka:
    consumer:
      enable-auto-commit: false
    listener:
      ack-mode: manual
      concurrency: 3
```

## 12.3 Topic Production Rules

```text
replication factor >= 3
min.insync.replicas >= 2
partitions based on throughput needs
retention based on replay requirements
```

## 12.4 Monitoring Checklist

Monitor:

```text
consumer lag
producer error rate
consumer processing failures
DLT message count
broker disk usage
under-replicated partitions
request latency
rebalance frequency
```

## 12.5 Logging Checklist

Log:

```text
eventId
eventType
key
topic
partition
offset
consumer group
correlationId
```

## 12.6 Deployment Checklist

Before production:

```text
Topics created with correct partitions and replication
Retry and DLT configured
Consumers are idempotent
Schema versioning strategy exists
Monitoring dashboards exist
Alerts configured
Security configured
Load testing completed
Runbook written
```

## 12.7 Common Production Incidents

### Consumer Lag Increasing

Possible causes:

```text
consumer is slow
external dependency is slow
not enough partitions
not enough consumer instances
large messages
```

### DLT Growing

Possible causes:

```text
bad producer data
consumer bug
schema mismatch
non-retryable business error
```

### Rebalances Happening Often

Possible causes:

```text
consumer crashes
processing takes too long
max.poll.interval.ms too low
unstable network
```

---

# Final Hands-On Project

## Project Goal

Build a mini event-driven e-commerce system.

```text
Order Service
  -> publishes OrderCreated

Payment Service
  -> consumes OrderCreated
  -> publishes PaymentCompleted

Notification Service
  -> consumes OrderCreated and PaymentCompleted
  -> sends fake notification
```

## Services

```text
order-service
payment-service
notification-service
```

## Topics

```text
orders
payments
notifications
orders.DLT
payments.DLT
```

## Event Flow

```text
POST /orders
  -> order-service publishes OrderCreated
  -> payment-service consumes OrderCreated
  -> payment-service publishes PaymentCompleted
  -> notification-service consumes both events
```

## Suggested Folder Structure

```text
kafka-springboot-project/
 ├── docker-compose.yml
 ├── order-service/
 ├── payment-service/
 └── notification-service/
```

## Step 1: Start Kafka

Use the Docker Compose file from Chapter 2.

## Step 2: Build Order Service

Responsibilities:

```text
Expose POST /orders
Create OrderCreatedEvent
Publish to orders topic
```

## Step 3: Build Payment Service

Responsibilities:

```text
Consume orders topic
Process fake payment
Publish PaymentCompletedEvent
Send failures to orders.DLT
```

## Step 4: Build Notification Service

Responsibilities:

```text
Consume orders topic as notification-order-group
Consume payments topic as notification-payment-group
Print notification logs
```

## Step 5: Add Idempotency

Each service should store processed event IDs.

For a simple local exercise, use an in-memory set.

For production, use a database table.

## Step 6: Add Tests

Test:

```text
OrderCreated is published
Payment service consumes OrderCreated
PaymentCompleted is published
Invalid event goes to DLT
Duplicate event is ignored
```

## Step 7: Add Observability

Enable actuator:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

## Step 8: Production Readiness

Add:

```text
manual acknowledgment
retry + DLT
idempotency
schema versioning
secure credentials
monitoring
alerts
runbook
```

---

# Final Learning Path

## Beginner Level

You should understand:

```text
topics
partitions
offsets
producers
consumers
consumer groups
```

## Intermediate Level

You should be able to build:

```text
Spring Boot producer
Spring Boot consumer
retry and DLT
manual acknowledgment
integration tests
```

## Advanced Level

You should understand:

```text
idempotency
transactions
schema evolution
security
monitoring
consumer lag
production deployment
```

## Production-Ready Level

You should be able to design:

```text
resilient event-driven systems
multi-service Kafka architecture
failure recovery strategy
schema governance
secure Kafka deployment
operational dashboards and alerts
```

---

# End of Book

