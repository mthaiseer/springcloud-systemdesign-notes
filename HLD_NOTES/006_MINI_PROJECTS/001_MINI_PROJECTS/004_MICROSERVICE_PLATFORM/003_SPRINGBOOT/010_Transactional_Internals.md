# 010_Transactional_Internals.md

# MiniSpringBoot Deep Production Mode
# 010 - Transactional Internals

## One Core Mental Model

> A transaction is not magic.
>
> Spring creates a transaction context, binds it to the current thread,
> and every repository operation participates in that context until commit or rollback.

Mental Model:

```text
BEGIN
  |
Transaction Context Created
  |
Connection Bound To Thread
  |
Business Logic Executes
  |
Repositories Reuse Same Connection
  |
COMMIT / ROLLBACK
  |
Context Destroyed
```

---

## Why This Exists

Databases must protect consistency.

Without transactions:

```text
Update Account Balance   SUCCESS
Insert Ledger Entry      FAILED
```

System becomes inconsistent.

Transactions provide:

```text
Atomicity
Consistency
Isolation
Durability
```

---

## Problem Statement

Developers often think:

```text
@Transactional
= database magic
```

Reality:

```text
@Transactional
= transaction context lifecycle
```

Understanding the internals explains:

- Rollbacks
- Propagation
- Isolation
- Thread boundaries
- Self invocation issues
- Connection reuse

---

## Real World Analogy

Imagine a warehouse worker.

```text
Open Work Folder
Put all changes inside
Validate everything
Publish folder
```

If something fails:

```text
Destroy folder
Pretend work never happened
```

Transaction context = work folder.

---

## Internal Architecture

```text
Controller
    |
    v
Transactional Proxy
    |
    v
TransactionInterceptor
    |
    v
PlatformTransactionManager
    |
    v
TransactionSynchronizationManager
    |
    v
ThreadLocal Context
    |
    v
JDBC Connection
    |
    v
Database
```

---

## Internal Working

### Step 1

Proxy intercepts call.

### Step 2

TransactionManager begins transaction.

### Step 3

Connection acquired from HikariCP.

### Step 4

autoCommit=false

### Step 5

Connection bound to current thread.

```text
Thread-101
   |
   +---- Connection-17
```

### Step 6

Repositories reuse Connection-17.

### Step 7

Commit or rollback.

### Step 8

Connection returned to pool.

---

## Rich ASCII Diagram

```text
Request
   |
   v
Proxy
   |
   v
+---------------------------+
| Transaction Context       |
|                           |
| Connection = Conn-17      |
| Active = true             |
| RollbackOnly = false      |
+---------------------------+
   |
   v
Repositories
   |
   v
Database
```

---

## Java Example

```java
@Transactional
public void transfer() {

    accountRepository.debit();

    accountRepository.credit();

    ledgerRepository.insert();
}
```

Internal flow:

```text
Begin TX
Acquire Connection
Execute SQL
Execute SQL
Execute SQL
Commit
Release Connection
```

---

## Spring Boot Example

```java
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public void processPayment() {

        paymentRepository.save(...);

    }
}
```

Execution:

```text
Controller
   |
Proxy
   |
TransactionInterceptor
   |
TransactionManager
   |
PaymentService
   |
Repository
```

---

## Dry Run 1 (Success)

```text
BEGIN

Insert Order
Insert Payment
Insert Ledger

COMMIT
```

Result:

```text
All rows visible
```

---

## Dry Run 2 (Failure)

```text
BEGIN

Insert Order
Insert Payment

Exception

ROLLBACK
```

Result:

```text
Nothing persisted
```

---

## Production Scale Example

1000 RPS

Transaction Duration:

```text
50 ms
```

Active Transactions:

```text
1000 * 0.05

= 50 active transactions
```

Key lesson:

```text
Transaction duration directly affects
connection pool pressure.
```

---

## Production Failure Story

Checkout service:

```java
@Transactional
public void checkout() {

    orderRepository.save();

    paymentGateway.call();

    inventoryRepository.reserve();
}
```

Problem:

```text
Remote API = 5 seconds
Connection held for 5 seconds
Pool exhausted
```

Fix:

```text
Short transaction
External call outside transaction
```

---

## Debugging Mindset

Always ask:

```text
Did proxy run?
Was transaction active?
Was connection bound?
Did exception escape?
Was rollback executed?
```

Useful check:

```java
TransactionSynchronizationManager
    .isActualTransactionActive();
```

---

## Common Misconceptions

### Wrong

```text
@Transactional creates transaction inside method
```

### Correct

```text
Transaction exists around method
```

### Wrong

```text
Each repository has own connection
```

### Correct

```text
Repositories reuse same transaction connection
```

---

## Interview Questions

### How does Spring keep repositories in same transaction?

Using ThreadLocal transaction context.

### Why does rollback happen?

Proxy catches exception and instructs TransactionManager.

### Why are transactions expensive?

Connections, locks, WAL, isolation, commit costs.

---

## Cheat Sheet

```text
@Transactional
      |
      v
Proxy
      |
      v
TransactionInterceptor
      |
      v
TransactionManager
      |
      v
ThreadLocal Context
      |
      v
Connection
      |
      v
Database
      |
      v
Commit / Rollback
```

---

## One Picture To Remember

```text
BEGIN
  |
ThreadLocal Transaction Context
  |
Connection Bound
  |
Repositories Execute
  |
Commit / Rollback
  |
Connection Released
  |
END
```

Final Memory Hook:

```text
@Transactional
     !=
Database Magic

@Transactional
     =
Managed Transaction Context Lifecycle
```
