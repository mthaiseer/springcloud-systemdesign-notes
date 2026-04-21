# Distributed Transactions in Distributed Systems — Complete Reference

> A detailed, practical guide from fundamentals to implementation.  
> Covers **why distributed transactions are hard**, **2PC**, **3PC**, **Saga**, **TCC**, **Outbox**, **idempotency**, **pitfalls**, **best practices**, and a **step-by-step Spring Boot + DB example** you can build from scratch.

---

## Table of Contents

1. [The motivating problem](#the-motivating-problem)
2. [What is a distributed transaction?](#what-is-a-distributed-transaction)
3. [Why distributed transactions are hard](#why-distributed-transactions-are-hard)
4. [Approach overview](#approach-overview)
5. [Two-Phase Commit (2PC)](#two-phase-commit-2pc)
6. [Three-Phase Commit (3PC)](#three-phase-commit-3pc)
7. [Saga Pattern](#saga-pattern)
8. [TCC Pattern](#tcc-pattern)
9. [Outbox Pattern](#outbox-pattern)
10. [How to choose the right approach](#how-to-choose-the-right-approach)
11. [Best practices](#best-practices)
12. [Common pitfalls](#common-pitfalls)
13. [Quick comparison table](#quick-comparison-table)
14. [Step-by-step Spring Boot + DB implementation](#step-by-step-spring-boot--db-implementation)
15. [Interview answer template](#interview-answer-template)
16. [Final takeaways](#final-takeaways)

---

# The motivating problem

Imagine a user clicks **Place Order** on an e-commerce site. Behind the scenes:

1. **Inventory Service** must reserve the items
2. **Payment Service** must charge the card
3. **Order Service** must create the order record

All three must succeed together, or none should happen.

### Failure examples

- **Payment succeeds, inventory fails**  
  You charged a customer for items you cannot deliver.

- **Order gets created, payment fails**  
  You created an order that has not been paid for.

- **Inventory reserved, order creation crashes**  
  Stock is blocked and other customers may see less availability.

In a **single database**, this is easy:

```sql
BEGIN;
UPDATE inventory SET reserved = reserved + 1 WHERE sku = 'A123';
INSERT INTO orders(...);
INSERT INTO payments(...);
COMMIT;
```

The database guarantees **all-or-nothing**.

But in microservices, each service often owns its own database. Then you no longer have one database transaction boundary. That is where distributed transactions become hard.

---

# What is a distributed transaction?

A **distributed transaction** is a transaction that spans **multiple services, databases, or nodes**, while still trying to preserve a transaction-like guarantee.

## Local transaction

A local transaction stays within one database:

```text
BEGIN
  UPDATE inventory
  INSERT order
  INSERT payment
COMMIT
```

The database enforces ACID.

## Distributed transaction

A distributed transaction spans multiple independent systems:

```text
Client
  -> Order Service / DB
  -> Inventory Service / DB
  -> Payment Service / DB
```

No single system controls all updates. That means no single component can easily guarantee global ACID across all participants.

## ACID recap

- **Atomicity** — all steps succeed or all are rolled back
- **Consistency** — state remains valid before and after
- **Isolation** — concurrent operations do not interfere
- **Durability** — once committed, data survives failure

The challenge is that in a distributed setup, these properties are much harder to enforce across service boundaries.

---

# Why distributed transactions are hard

Distributed transactions are not just “a bigger transaction.” They are fundamentally harder because the environment is unreliable.

## 1. The Two Generals Problem

This classic thought experiment captures the problem of distributed agreement.

Two generals must attack together. They can only communicate through messengers, and messengers may be captured. Even with acknowledgments, neither general can be perfectly sure the final confirmation arrived.

This maps directly to distributed transactions.

### Example mapping

- Coordinator sends “commit”
- Participant may receive it or not
- Participant sends ack
- Ack may get lost
- Coordinator does not know whether the participant committed
- Participant does not know whether coordinator knows

This uncertainty is fundamental.

---

## 2. Network failures

Networks can:

- drop messages
- duplicate messages
- delay messages
- reorder messages

That means “no response” is ambiguous.

If Service A does not hear from Service B, B may be:

- dead
- slow
- partitioned
- healthy, but response got lost

That ambiguity is one of the core reasons distributed transaction protocols are complex.

---

## 3. Node failures

A node can crash:

- before receiving a command
- after receiving but before processing
- after processing but before replying
- after replying but before persisting
- during recovery

Each crash point creates different recovery behavior.

---

## 4. CAP theorem

In practice, distributed systems must tolerate partitions. Under partition, you generally choose between:

- **Consistency**
- **Availability**

Strong distributed transaction protocols usually choose **consistency** over availability.

That means if a participant is unreachable, the transaction may block or fail rather than risk corruption.

---

# Approach overview

There are three broad strategies.

## A. Strong consistency approaches

Try to approximate traditional transactions across nodes.

- **2PC**
- **3PC**
- consensus-based commit variants

Best when:
- consistency is critical
- latency is acceptable
- availability can be sacrificed

---

## B. Eventual consistency approaches

Accept temporary inconsistency but guarantee convergence later.

- **Saga**
- **TCC**
- **Outbox**
- Event sourcing variations

Best when:
- microservices are autonomous
- you can tolerate temporary inconsistency
- availability matters more

---

## C. Avoid distributed transactions

Often the best answer is architectural.

- co-locate data
- reduce service boundaries
- use one database transaction where possible
- redesign workflow to avoid cross-service atomicity

Best when:
- you control service boundaries
- consistency requirements are strict
- you can simplify design

---

# Two-Phase Commit (2PC)

Two-Phase Commit is the classic strong-consistency protocol.

## Idea

A **coordinator** asks all participants whether they are ready to commit.  
If all agree, coordinator tells everyone to commit.  
If any say no, coordinator tells everyone to abort.

## Phase 1: Prepare / Vote

Coordinator asks:

```text
Can you commit?
```

Each participant:
- executes local work
- writes intent to durable log
- locks resources if needed
- replies YES or NO

## Phase 2: Commit / Abort

- If **all YES** -> coordinator sends `COMMIT`
- If **any NO** -> coordinator sends `ABORT`

Participants then finalize.

---

## 2PC flow diagram

```text
Coordinator -> A: PREPARE
Coordinator -> B: PREPARE
Coordinator -> C: PREPARE

A -> Coordinator: YES
B -> Coordinator: YES
C -> Coordinator: YES

Coordinator -> A: COMMIT
Coordinator -> B: COMMIT
Coordinator -> C: COMMIT
```

Abort case:

```text
Coordinator -> A: PREPARE
Coordinator -> B: PREPARE
Coordinator -> C: PREPARE

A -> Coordinator: YES
B -> Coordinator: NO
C -> Coordinator: YES

Coordinator -> A: ABORT
Coordinator -> B: ABORT
Coordinator -> C: ABORT
```

---

## Why 2PC works

It gives all participants a consistent final decision, assuming they can contact the coordinator and recover logs correctly.

A participant that votes YES has promised it is able to commit after recovery.

---

## The main problem: blocking

Once a participant votes YES, it may be stuck waiting for the coordinator.

If the coordinator crashes after collecting YES votes but before sending COMMIT/ABORT:

- participants cannot safely commit
- participants cannot safely abort
- they remain blocked
- locks stay held
- system throughput suffers badly

### Blocking scenario

```text
Coordinator crashes
A = PREPARED, waiting
B = PREPARED, waiting
C = PREPARED, waiting
```

This can cause:

- stuck transactions
- lock contention
- cascading timeouts
- broader outages

---

## Pros of 2PC

- strong consistency
- conceptually simple
- supported by some databases / XA environments
- works for short transactions in controlled environments

## Cons of 2PC

- blocking under coordinator failure
- reduced availability
- lock-heavy
- not ideal across unreliable networks
- painful in microservices

---

## When to use 2PC

Use 2PC only when:

- strong consistency is absolutely required
- participants support XA / 2PC semantics
- transactions are short
- environment is controlled (same DC, reliable infra)
- you accept blocking risk

Avoid it for most microservices.

---

# Three-Phase Commit (3PC)

3PC tries to improve 2PC by reducing blocking.

## Phases

1. **CanCommit**
2. **PreCommit**
3. **DoCommit**

The extra phase gives participants more information before final commit.

---

## 3PC flow

```text
Coordinator -> participants: CanCommit?
participants -> coordinator: YES

Coordinator -> participants: PreCommit
participants prepare and ack

Coordinator -> participants: DoCommit
participants commit
```

---

## Why 3PC was created

In 2PC, if the coordinator dies, prepared participants do not know what others voted.

In 3PC, participants in `PreCommit` state know the transaction was likely going to commit, so they can decide more safely after timeout.

---

## Limitation of 3PC

3PC is not safe under arbitrary network partitions. Different participants may time out and make inconsistent decisions.

So while it reduces blocking in theory, it introduces other correctness issues in realistic partitioned environments.

---

## Practical takeaway

3PC is mostly of theoretical interest.

In real systems:
- it is rarely used
- it is usually replaced by consensus-based protocols or eventual consistency patterns

---

# Saga Pattern

The Saga pattern is the most common answer in microservices.

## Idea

Break one distributed transaction into a sequence of **local transactions**.

Each step:
- commits locally
- then triggers the next step

If a later step fails:
- execute **compensating transactions** to undo prior successful steps

---

## Example order saga

Forward path:

1. Create order
2. Reserve inventory
3. Charge payment
4. Mark order confirmed

Failure path:

- If payment fails after inventory reserve:
  - release inventory
  - cancel order

---

## Saga flow

```text
T1: Create Order
T2: Reserve Inventory
T3: Process Payment
T4: Confirm Order
```

Compensation:

```text
If T3 fails:
C2: Release Inventory
C1: Cancel Order
```

---

## Important point

Saga is **not atomic in the database sense**.

At intermediate points, the system may be inconsistent:

- order exists but payment not processed yet
- inventory reserved but order not finalized yet

That is acceptable because the system is designed to converge.

---

## Orchestration vs choreography

## Orchestration

A central orchestrator manages the saga.

### Flow

```text
Orchestrator -> Order Service
Orchestrator -> Inventory Service
Orchestrator -> Payment Service
```

The orchestrator:
- knows the sequence
- stores state
- handles retries
- triggers compensation

### Pros
- easier to reason about
- easy to monitor
- easier debugging
- explicit workflow

### Cons
- orchestrator becomes central coordinator
- tighter coupling to steps

---

## Choreography

No central coordinator. Each service emits events and others react.

### Flow

```text
OrderCreated -> Inventory reserves -> emits InventoryReserved
InventoryReserved -> Payment charges -> emits PaymentProcessed
PaymentProcessed -> Order confirms
```

### Pros
- looser coupling
- more event-driven
- easier to add listeners

### Cons
- harder to see end-to-end workflow
- debugging is harder
- logic becomes scattered

---

## Saga examples

### E-commerce order
- create order
- reserve inventory
- process payment
- confirm order
- compensate if payment fails

### Travel booking
- reserve flight
- reserve hotel
- reserve car
- if hotel fails, cancel flight reservation

### Subscription signup
- create user
- create billing customer
- provision plan
- if provisioning fails, deactivate billing

---

## Designing compensating transactions

This is the hardest part.

Compensation is not always true rollback.

### Example: refund is not undo
Charging a card and refunding it are not identical to “never charged.”

Why?
- card statement may show both operations
- fees may apply
- customer may receive notifications

So compensation is usually **business undo**, not perfect technical undo.

---

## When to use Saga

Use Saga when:

- building microservices
- availability matters
- temporary inconsistency is acceptable
- compensating actions are possible
- workflows may be long-running

Saga is usually the default practical answer for business workflows.

---

# TCC Pattern

TCC stands for:

- **Try**
- **Confirm**
- **Cancel**

It is a stricter variation of saga with explicit reservation.

## Idea

Instead of committing work immediately, each service first reserves resources.

If all Try steps succeed:
- Confirm them all

If any Try fails:
- Cancel all reservations

---

## TCC example: payment + inventory + order

### Try phase
- order service reserves order id / pending record
- inventory service reserves stock
- payment service reserves funds / authorization hold

### Confirm phase
- create final order
- deduct stock permanently
- capture payment

### Cancel phase
- release order reservation
- release stock
- release payment authorization

---

## TCC flow diagram

```text
TRY Order
TRY Inventory
TRY Payment

if all succeed:
  CONFIRM Order
  CONFIRM Inventory
  CONFIRM Payment

if any fail:
  CANCEL Order
  CANCEL Inventory
  CANCEL Payment
```

---

## Why TCC is stronger than plain saga

Because resources are reserved but not fully committed, you get better isolation semantics than a saga that immediately commits each step.

However, TCC requires every service to implement:
- Try
- Confirm
- Cancel

That is a high implementation cost.

---

## Pros of TCC

- more controlled than basic saga
- better handling of reservations
- good for payments, inventory, booking-like workflows

## Cons of TCC

- much more complex than saga
- services must be designed explicitly for TCC
- Confirm should be designed to almost never fail
- reservations can create resource contention

---

## When to use TCC

Use TCC when:
- you can reserve resources explicitly
- intermediate visibility is a problem
- you need stronger business consistency than basic saga
- domains are booking/payment/inventory style

---

# Outbox Pattern

The Outbox pattern solves a different but related distributed transaction problem:

> How do I update my database and publish an event reliably without losing one of them?

---

## The dual-write problem

Suppose Order Service does:

1. write order row to database
2. publish `OrderCreated` event to Kafka

If database succeeds but Kafka publish fails:
- order exists
- other services never hear about it

If Kafka publish succeeds but database write fails:
- other services think order exists
- but it does not

This is called the **dual-write problem**.

---

## Outbox solution

Write both:
- business data
- event record

in the **same local transaction**.

Then a background relay publishes the outbox rows to the message broker.

---

## Outbox flow

```text
Transaction:
  INSERT order
  INSERT outbox_event

Commit DB transaction

Background relay:
  read unprocessed outbox rows
  publish to Kafka
  mark row processed
```

Because the DB transaction is local, it is atomic.

Either:
- both order + outbox row are stored
- or neither is

That removes the dual-write inconsistency.

---

## Outbox table example

```sql
CREATE TABLE outbox (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL
);
```

---

## Outbox pros

- simple and reliable
- avoids distributed transaction with broker
- works well with Kafka/RabbitMQ
- ideal companion to Saga

## Outbox cons

- eventual consistency
- relay process required
- consumers must be idempotent because duplicates can happen

---

## When to use Outbox

Use Outbox when:
- your service writes DB state and emits events
- you need reliable event delivery
- you are using event-driven architecture
- you want to avoid 2PC with message broker

This is one of the most valuable production patterns.

---

# How to choose the right approach

## Key question 1: Do you really need distributed transactions?

Often the correct answer is:
```text
No — redesign to avoid them
```

Examples:
- put order + payment in same database if boundary allows
- use one service as source of truth
- use asynchronous workflows

---

## Key question 2: Do you need strong consistency?

If yes:
- prefer local transaction if possible
- else maybe 2PC or TCC
- but expect lower availability and more complexity

If no:
- use Saga / Outbox
- embrace eventual consistency

---

## Decision guide

| Requirement | Best fit |
|---|---|
| single DB possible | local transaction |
| strong cross-service consistency | 2PC or TCC |
| microservices business workflow | Saga |
| DB + event publish reliability | Outbox |
| reservations needed | TCC |
| high availability, async flow | Saga + Outbox |

---

# Best practices

## 1. Prefer local transactions whenever possible

This is the single best practice.

Before reaching for a distributed transaction, ask:
- can I merge services?
- can I co-locate data?
- can I move coordination into one bounded context?

Avoiding the problem is often the best engineering move.

---

## 2. Make operations idempotent

Retries happen. Messages may be delivered more than once.

So every command should ideally be safe to run twice.

### Idempotency pattern

- client sends `requestId`
- service checks if request already processed
- if yes, returns previous result
- if no, processes and records requestId

This is essential for:
- payment processing
- saga steps
- outbox consumers
- REST command APIs

---

## 3. Use timeouts everywhere

Never wait forever on a remote call.

Every step in a distributed workflow should have:
- timeout
- retry policy
- failure path

---

## 4. Design compensations up front

Do not design the happy path first and add compensation later.

For every forward action, ask:
```text
If this step succeeds and the workflow later fails, how do I undo it?
```

If you cannot answer that, your saga design is incomplete.

---

## 5. Store workflow state explicitly

For orchestration, persist saga state:
- current step
- retry count
- failure reason
- compensations done or pending
- timestamps

This helps with:
- recovery after crash
- manual debugging
- stuck workflow detection

---

## 6. Monitor stuck transactions

Track:
- workflow age
- failed compensations
- retry counts
- outbox backlog
- event publish latency
- dead-letter queue growth

Alert when:
- saga stuck in one step too long
- outbox events not processed
- compensation repeatedly fails

---

## 7. Expect duplicates and reordering

In distributed systems:
- a message can be redelivered
- a response can be retried
- events can arrive out of order

Design for it.

---

## 8. Minimize lock duration

If using TCC or 2PC, avoid holding locks for long business workflows.

Short, bounded locking windows are safer.

---

# Common pitfalls

## Pitfall 1: Assuming partial failure won’t happen

Wrong approach:

```python
service_a.update()
service_b.update()
service_c.update()
```

If B fails after A succeeded, the system is already inconsistent.

Always track:
- what completed
- what failed
- what must be compensated

---

## Pitfall 2: Treating retries as harmless

Retries can cause:
- duplicate charges
- duplicate orders
- repeated event side effects

Without idempotency, retries are dangerous.

---

## Pitfall 3: Using 2PC in microservices casually

2PC across independently deployed services:
- is operationally painful
- reduces availability
- can block badly
- does not fit loosely coupled systems well

---

## Pitfall 4: Forgetting that compensation can fail

Example:
- refund API is down
- inventory release call times out
- order cancel endpoint fails validation

You need:
- retry policies
- dead-letter handling
- operator dashboard
- manual remediation path

---

## Pitfall 5: Sending emails or notifications too early

If you send confirmation email before the saga truly completes, compensation cannot “unsend” the email.

Delay irreversible side effects until the workflow is safely complete.

---

# Quick comparison table

| Approach | Consistency | Availability | Latency | Complexity | Good for |
|---|---|---:|---:|---:|---|
| Local transaction | Strong | High | Low | Low | same DB |
| 2PC | Strong | Low | Medium | Medium | controlled XA systems |
| 3PC | Theoretical stronger liveness | Medium | High | High | rarely used |
| Saga | Eventual | High | Medium | High | microservices workflows |
| TCC | Stronger business consistency | Medium | Medium | High | payments, reservations |
| Outbox | Eventual | High | Low | Medium | DB + event publishing |

---

# Step-by-step Spring Boot + DB implementation

This section gives a practical implementation using:

- Spring Boot
- Spring Data JPA
- H2 or MySQL/PostgreSQL
- local DB transaction
- Saga orchestration style
- Outbox pattern
- idempotency support

## What this sample demonstrates

We will model a simplified order flow:

1. Create order in `PENDING`
2. Reserve inventory
3. Charge payment
4. Mark order `CONFIRMED`
5. If anything fails -> compensate
6. Also write an outbox event in the same DB transaction

This is the most practical “from scratch” implementation pattern.

---

## 1. Project structure

```text
src/main/java/com/example/tx
  ├── DistributedTxApplication.java
  ├── controller/OrderController.java
  ├── domain/OrderEntity.java
  ├── domain/OutboxEvent.java
  ├── domain/ProcessedRequest.java
  ├── repo/OrderRepository.java
  ├── repo/OutboxRepository.java
  ├── repo/ProcessedRequestRepository.java
  ├── service/InventoryClient.java
  ├── service/PaymentClient.java
  ├── service/OrderSagaService.java
  ├── service/OutboxRelayService.java
  └── web/CreateOrderRequest.java
```

---

## 2. Maven dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

---

## 3. application.properties

```properties
spring.datasource.url=jdbc:h2:mem:txdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
spring.jpa.show-sql=true
server.port=8080
```

---

## 4. Main application

```java
package com.example.tx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DistributedTxApplication {
    public static void main(String[] args) {
        SpringApplication.run(DistributedTxApplication.class, args);
    }
}
```

---

## 5. Order entity

```java
package com.example.tx.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerId;
    private String sku;
    private Integer quantity;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Instant createdAt;

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        CANCELLED
    }

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
}
```

---

## 6. Outbox event entity

```java
package com.example.tx.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;
    private String aggregateId;
    private String eventType;

    @Lob
    private String payload;

    private Instant createdAt;
    private Instant processedAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getAggregateType() { return aggregateType; }
    public void setAggregateType(String aggregateType) { this.aggregateType = aggregateType; }
    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getProcessedAt() { return processedAt; }
    public void setProcessedAt(Instant processedAt) { this.processedAt = processedAt; }
}
```

---

## 7. Idempotency table

```java
package com.example.tx.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "processed_requests")
public class ProcessedRequest {

    @Id
    private String requestId;

    private Instant createdAt = Instant.now();

    public ProcessedRequest() {}

    public ProcessedRequest(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() { return requestId; }
    public Instant getCreatedAt() { return createdAt; }
}
```

---

## 8. Repositories

```java
package com.example.tx.repo;

import com.example.tx.domain.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
```

```java
package com.example.tx.repo;

import com.example.tx.domain.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByProcessedAtIsNullOrderByCreatedAtAsc();
    List<OutboxEvent> findByProcessedAtBefore(Instant before);
}
```

```java
package com.example.tx.repo;

import com.example.tx.domain.ProcessedRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedRequestRepository extends JpaRepository<ProcessedRequest, String> {
}
```

---

## 9. Request DTO

```java
package com.example.tx.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateOrderRequest {

    @NotBlank
    private String requestId;

    @NotBlank
    private String customerId;

    @NotBlank
    private String sku;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    private BigDecimal amount;

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
```

---

## 10. Fake Inventory client

In a real system this would call another service.  
Here we simulate it with in-memory state so the flow is understandable.

```java
package com.example.tx.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InventoryClient {

    private final Map<String, Integer> available = new ConcurrentHashMap<>();
    private final Map<String, Integer> reserved = new ConcurrentHashMap<>();

    public InventoryClient() {
        available.put("SKU-1", 10);
        available.put("SKU-2", 5);
    }

    public void reserve(String sku, int qty) {
        int stock = available.getOrDefault(sku, 0);
        if (stock < qty) {
            throw new IllegalStateException("Not enough stock for " + sku);
        }
        available.put(sku, stock - qty);
        reserved.put(sku, reserved.getOrDefault(sku, 0) + qty);
    }

    public void release(String sku, int qty) {
        reserved.put(sku, Math.max(0, reserved.getOrDefault(sku, 0) - qty));
        available.put(sku, available.getOrDefault(sku, 0) + qty);
    }

    public Map<String, Integer> snapshot() {
        return Map.copyOf(available);
    }
}
```

---

## 11. Fake Payment client

```java
package com.example.tx.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PaymentClient {

    private final Set<String> chargedOrderKeys = ConcurrentHashMap.newKeySet();

    public void charge(String paymentKey, BigDecimal amount) {
        if (!chargedOrderKeys.add(paymentKey)) {
            return; // idempotent
        }

        // Example failure hook:
        if (amount.signum() < 0) {
            throw new IllegalStateException("Invalid amount");
        }
    }

    public void refund(String paymentKey) {
        chargedOrderKeys.remove(paymentKey);
    }
}
```

---

## 12. Order saga service

This is the core orchestration example.

```java
package com.example.tx.service;

import com.example.tx.domain.OrderEntity;
import com.example.tx.domain.OutboxEvent;
import com.example.tx.domain.ProcessedRequest;
import com.example.tx.repo.OrderRepository;
import com.example.tx.repo.OutboxRepository;
import com.example.tx.repo.ProcessedRequestRepository;
import com.example.tx.web.CreateOrderRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderSagaService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ProcessedRequestRepository processedRequestRepository;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;

    public OrderSagaService(OrderRepository orderRepository,
                            OutboxRepository outboxRepository,
                            ProcessedRequestRepository processedRequestRepository,
                            InventoryClient inventoryClient,
                            PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.processedRequestRepository = processedRequestRepository;
        this.inventoryClient = inventoryClient;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public OrderEntity createOrder(CreateOrderRequest request) {
        // Idempotency check
        if (processedRequestRepository.existsById(request.getRequestId())) {
            throw new IllegalStateException("Duplicate requestId: " + request.getRequestId());
        }

        processedRequestRepository.save(new ProcessedRequest(request.getRequestId()));

        OrderEntity order = new OrderEntity();
        order.setCustomerId(request.getCustomerId());
        order.setSku(request.getSku());
        order.setQuantity(request.getQuantity());
        order.setAmount(request.getAmount());
        order.setStatus(OrderEntity.OrderStatus.PENDING);
        orderRepository.save(order);

        boolean inventoryReserved = false;
        boolean paymentCharged = false;
        String paymentKey = "order-" + order.getId();

        try {
            inventoryClient.reserve(order.getSku(), order.getQuantity());
            inventoryReserved = true;

            paymentClient.charge(paymentKey, order.getAmount());
            paymentCharged = true;

            order.setStatus(OrderEntity.OrderStatus.CONFIRMED);
            orderRepository.save(order);

            OutboxEvent event = new OutboxEvent();
            event.setAggregateType("Order");
            event.setAggregateId(order.getId().toString());
            event.setEventType("OrderConfirmed");
            event.setPayload("{\"orderId\":" + order.getId() + ",\"status\":\"CONFIRMED\"}");
            outboxRepository.save(event);

            return order;
        } catch (Exception ex) {
            // Compensation
            if (paymentCharged) {
                paymentClient.refund(paymentKey);
            }

            if (inventoryReserved) {
                inventoryClient.release(order.getSku(), order.getQuantity());
            }

            order.setStatus(OrderEntity.OrderStatus.CANCELLED);
            orderRepository.save(order);

            OutboxEvent event = new OutboxEvent();
            event.setAggregateType("Order");
            event.setAggregateId(order.getId().toString());
            event.setEventType("OrderCancelled");
            event.setPayload("{\"orderId\":" + order.getId() + ",\"status\":\"CANCELLED\",\"reason\":\"" +
                    ex.getMessage().replace("\"", "'") + "\"}");
            outboxRepository.save(event);

            throw ex;
        }
    }
}
```

---

## 13. REST controller

```java
package com.example.tx.controller;

import com.example.tx.domain.OrderEntity;
import com.example.tx.service.OrderSagaService;
import com.example.tx.web.CreateOrderRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderSagaService orderSagaService;

    public OrderController(OrderSagaService orderSagaService) {
        this.orderSagaService = orderSagaService;
    }

    @PostMapping
    public OrderEntity create(@Valid @RequestBody CreateOrderRequest request) {
        return orderSagaService.createOrder(request);
    }
}
```

---

## 14. Outbox relay service

In production, this would publish to Kafka/RabbitMQ.

For now, we just print and mark processed.

```java
package com.example.tx.service;

import com.example.tx.domain.OutboxEvent;
import com.example.tx.repo.OutboxRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class OutboxRelayService {

    private final OutboxRepository outboxRepository;

    public OutboxRelayService(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxRepository.findByProcessedAtIsNullOrderByCreatedAtAsc();
        for (OutboxEvent event : events) {
            // Replace with Kafka publish in real systems
            System.out.println("Publishing event: " + event.getEventType() + " payload=" + event.getPayload());
            event.setProcessedAt(Instant.now());
            outboxRepository.save(event);
        }
    }
}
```

---

## 15. Example request

### Success case

```http
POST /orders
Content-Type: application/json

{
  "requestId": "req-1001",
  "customerId": "cust-1",
  "sku": "SKU-1",
  "quantity": 2,
  "amount": 100.00
}
```

### What happens

1. requestId stored
2. order inserted as `PENDING`
3. inventory reserved
4. payment charged
5. order updated to `CONFIRMED`
6. outbox row inserted
7. relay publishes event later

---

## 16. Example failure case

If inventory reserve or payment charge throws:

1. order inserted as `PENDING`
2. maybe inventory reserved
3. maybe payment charged
4. exception happens
5. service compensates:
   - refund payment if charged
   - release inventory if reserved
6. order marked `CANCELLED`
7. cancellation outbox event written

This is exactly the Saga pattern in action.

---

## 17. SQL schema summary

If you prefer raw SQL reference:

```sql
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(255),
    sku VARCHAR(255),
    quantity INT,
    amount DECIMAL(19,2),
    status VARCHAR(32),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE outbox_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aggregate_type VARCHAR(255),
    aggregate_id VARCHAR(255),
    event_type VARCHAR(255),
    payload TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL
);

CREATE TABLE processed_requests (
    request_id VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 18. How to extend this to production

### Add Kafka
Replace relay print with:
- publish to Kafka topic
- mark processed only after broker ack

### Add dead-letter queue
If publish repeatedly fails:
- mark event failed
- send to DLQ / ops dashboard

### Add retries
For inventory/payment remote calls:
- retry transient failures
- keep idempotency

### Add saga state table
Track:
- step name
- retry count
- status
- timestamps

### Add metrics
Monitor:
- confirmed orders
- cancelled orders
- compensation count
- outbox lag
- publish failures

---

# Interview answer template

If asked:

> “How would you handle a transaction across Order, Payment, and Inventory services?”

A strong concise answer:

```text
If I can avoid a distributed transaction, I would co-locate the write path into one bounded context. 
If not, in a microservices architecture I would use a Saga pattern instead of 2PC because 2PC reduces availability and can block under coordinator failure. 
I would make every step idempotent, define explicit compensating actions such as refund payment and release inventory, and persist workflow progress. 
If the services communicate by events, I would use the Outbox pattern to make database updates and event publication reliable.
```

If asked when 2PC is appropriate:

```text
I would only use 2PC when I absolutely need strong consistency across participants, the participants support XA semantics, transactions are short-lived, and I can tolerate blocking and reduced availability.
```

---

# Final takeaways

- Distributed transactions are hard because networks fail, nodes crash, and there is no single authority across services.
- 2PC gives strong consistency but can block and hurts availability.
- 3PC is rarely used in practice.
- Saga is the most practical pattern for microservices.
- TCC is useful when resources can be reserved explicitly.
- Outbox is the best way to solve the DB write + event publish reliability problem.
- Idempotency is mandatory.
- The best distributed transaction is often the one you avoid by good service and data design.

## One-line cheat sheet

```text
Strong consistency in controlled environment -> 2PC/TCC
Microservices business workflow -> Saga
DB update + event publish -> Outbox
Best answer if possible -> avoid distributed transaction entirely
```
