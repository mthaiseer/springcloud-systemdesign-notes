# 009_SelfInvocation_Problem.md

# Self Invocation Problem – The Proxy Boundary Mental Model

## Why This Exists

Spring AOP was designed to add behavior around business methods without modifying the business code itself.

The key design choice:

```text
Business Logic
      +
Proxy-Based Interception
```

Instead of injecting transaction code, retry code, caching code, metrics code, and security code into every method, Spring creates a proxy around a bean.

This gives a clean separation:

```text
Business Logic
      |
      v
Target Object

Infrastructure Logic
      |
      v
Proxy
```

The Self Invocation Problem exists because developers often think:

```text
@Transactional
means
Transaction inside method
```

But reality is:

```text
@Transactional
means
Transaction around method call
```

That distinction explains everything.

---

## Problem Statement

Consider:

```java
@Service
public class OrderService {

    public void checkout() {
        saveOrder();
    }

    @Transactional
    public void saveOrder() {
        // DB work
    }
}
```

Most developers expect:

```text
checkout()
      |
saveOrder()
      |
transaction active
```

Actual behavior:

```text
checkout()
      |
this.saveOrder()
      |
direct JVM call
      |
proxy skipped
      |
transaction missing
```

---

## Mental Model

### Proxy Boundary Mental Model

Remember only one picture:

```text
WORKS

Caller
   |
   v
+--------+
| Proxy  |
+--------+
   |
   v
Target


FAILS

Target
  |
  v
Target
```

Rule:

```text
Cross Proxy Boundary
      |
      v
AOP Works

Stay Inside Object
      |
      v
AOP Disappears
```

---

## Real World Analogy

Imagine airport security.

```text
Passenger
    |
Security Gate
    |
Airport
```

Every passenger entering from outside passes security.

But movement inside airport terminals:

```text
Terminal A
    |
Terminal B
```

does not pass security again.

The proxy is airport security.

Self invocation is moving between terminals.

---

## Core Concepts

### Proxy

Wrapper object created by Spring.

### Target Object

Real business bean.

### Interceptor

Logic executed before and after method invocation.

### Advisor

Combines pointcut and advice.

### TransactionInterceptor

Interceptor responsible for transaction lifecycle.

### Self Invocation

Method A calling Method B inside same object.

---

## Internal Architecture

```text
Controller
     |
     v
+----------------+
| Spring Proxy   |
+----------------+
        |
        v
TransactionInterceptor
        |
        v
SecurityInterceptor
        |
        v
MetricsInterceptor
        |
        v
Target Bean
```

---

## Internal Working

Normal flow:

```text
Controller
      |
      v
Proxy
      |
      v
Interceptor Chain
      |
      v
Target Method
```

Self Invocation:

```text
Target.methodA()
      |
      v
Target.methodB()
```

No proxy involved.

---

## Step-by-Step Flow

### Normal

```text
1. Controller calls service

2. Proxy receives call

3. TransactionInterceptor runs

4. Transaction begins

5. Target method executes

6. Commit

7. Response returned
```

### Self Invocation

```text
1. Controller calls service

2. Proxy receives call

3. methodA executes

4. methodA calls methodB

5. JVM directly invokes methodB

6. Interceptors skipped
```

---

## Deep Walkthrough Example

```java
@Service
public class PaymentService {

    public void processPayment() {
        validate();
        savePayment();
    }

    @Transactional
    public void savePayment() {
    }

    private void validate() {
    }
}
```

Runtime:

```text
Controller
   |
Proxy
   |
processPayment()
   |
this.savePayment()
   |
savePayment()
```

No transaction created.

Why?

Because:

```text
processPayment()
and
savePayment()
are executing
inside same object instance.
```

---

## Rich ASCII Diagrams

### Diagram 1

```text
Caller
  |
Proxy
  |
Target
```

### Diagram 2

```text
Target
  |
Target
```

### Diagram 3

```text
External Bean
      |
      v
Proxy
      |
      v
Transactional Method
```

### Diagram 4

```text
Bean A
   |
   v
Bean B Proxy
   |
   v
Bean B Target
```

### Diagram 5

```text
Bean A
   |
   v
Bean A
```

Proxy bypass.

---

## Data Structures & Algorithms Used

Spring AOP mainly relies on:

### Interceptor Chain

Conceptually:

```text
List<Interceptor>
```

Execution:

```text
Interceptor1
     |
Interceptor2
     |
Interceptor3
     |
Target
```

### ThreadLocal

Transactions are stored in:

```text
ThreadLocal<TransactionContext>
```

Lookup complexity:

```text
O(1)
```

---

## Java Code Example (with execution explanation)

```java
public class UserService {

    public void process() {
        save();
    }

    @Transactional
    public void save() {
    }
}
```

Execution:

```text
process()
   |
save()
```

Equivalent JVM behavior:

```java
this.save();
```

Proxy not involved.

---

## Spring Boot Example (with internal flow)

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;

    public void checkout() {
        saveOrder();
    }

    @Transactional
    public void saveOrder() {
        repository.save(new Order());
    }
}
```

Internal flow:

```text
Controller
     |
Proxy
     |
checkout()
     |
this.saveOrder()
     |
repository.save()
```

Transaction interceptor skipped.

---

## Production Architecture Example

```text
Gateway
   |
Order Service
   |
PostgreSQL
```

Developer assumes:

```text
@Transactional active
```

Reality:

```text
self invocation
      |
transaction missing
```

Partial writes become possible.

---

## Sequence Diagram (ASCII)

```text
Controller      Proxy      Service

    |             |           |
    |-----------> |           |
    |             |---------->|
    |             |           |
    |             | process() |
    |             |           |
    |             |           | save()
    |             |           |
```

No interceptor around save().

---

## Request Lifecycle

```text
HTTP Request
      |
DispatcherServlet
      |
Controller
      |
Proxy
      |
Service
      |
Repository
      |
Database
```

Broken path:

```text
Service
   |
this.call()
   |
No Proxy
```

---

## Multiple Dry Runs (Normal, Edge, Failure)

### Normal Flow

External bean call.

```text
Transaction Active
```

### Edge Case

Public transactional method called externally.

```text
Works
```

### Failure Case

Internal method call.

```text
Fails
```

### Cacheable Case

```text
Cache skipped
```

### Retryable Case

```text
Retry skipped
```

### Async Case

```text
Async skipped
```

### Security Case

```text
Security skipped
```

---

## Failure Scenarios

1. Missing rollback
2. Missing cache hit
3. Missing retry
4. Missing async execution
5. Missing authorization checks

---

## Failure Investigation Playbook

Step 1

```java
System.out.println(bean.getClass());
```

Step 2

Check:

```text
$$SpringCGLIB$$
```

Step 3

Enable transaction logs.

Step 4

Search for:

```java
this.method()
```

Step 5

Check integration tests.

---

## Debugging Guide

Ask:

```text
Did call hit proxy?
```

Not:

```text
Does annotation exist?
```

Annotation without proxy is useless.

---

## Performance Considerations

Self invocation is not slow.

The danger is:

```text
Missing cache
Missing retry
Missing async
```

Performance degradation appears indirectly.

---

## Scalability Considerations

Without cache:

```text
More DB load
```

Without async:

```text
Lower throughput
```

Without retry:

```text
Higher failure rate
```

---

## Production Failure Story

Large ecommerce platform.

Original:

```text
Controller
   |
@Transactional
```

Refactor:

```text
processOrder()
      |
saveOrder()
```

same class.

Outcome:

```text
Transactions disappeared.
```

Result:

```text
Orders persisted
Payments failed
Inventory inconsistent
```

Root cause:

```text
Self Invocation
```

---

## Common Misconceptions

Wrong:

```text
@Transactional modifies method
```

Correct:

```text
Proxy surrounds method call
```

Wrong:

```text
Annotation guarantees behavior
```

Correct:

```text
Proxy guarantees behavior
```

---

## FAANG/System Design Discussion

Strong engineers understand:

```text
AOP is infrastructure.

Infrastructure lives in proxy.

Proxy boundary determines behavior.
```

This understanding applies to:

- Spring
- CDI
- Guice interceptors
- ASP.NET filters

---

## Common Interview Questions

1. Why does self invocation fail?
2. Why does @Transactional stop working?
3. Does @Cacheable fail too?
4. JDK Proxy vs CGLIB?
5. Best fix?

---

## Strong Interview Answers

Why does self invocation break transactions?

```text
Spring AOP is proxy based.

Internal calls bypass proxy.

TransactionInterceptor never executes.

Therefore transaction boundary is never created.
```

---

## Production Checklist

```text
[ ] Spring bean
[ ] Proxy exists
[ ] Public method
[ ] No self invocation
[ ] Integration tests
[ ] Logs verified
```

---

## One-Page Cheat Sheet

```text
External Call
      |
      v
Proxy
      |
      v
Advice Executes
```

```text
Internal Call
      |
      v
Target
      |
      v
Target
```

```text
Advice Missing
```

---

## Last-Minute Interview Revision

Remember:

```text
AOP lives in proxy.
```

Remember:

```text
No proxy
      =
No transaction
```

Remember:

```text
No proxy
      =
No cache
No retry
No async
No security
```

---

## Mental Models Table

| Concept | Mental Model |
|----------|-------------|
| Proxy | Security Gate |
| Target Bean | Office |
| Transaction | Protected Workspace |
| Self Invocation | Side Door |
| Cache | Reception Desk |
| Retry | Automatic Redial |

---

## Key Takeaways

1. Spring AOP is proxy based.
2. Advice lives in proxy.
3. Self invocation bypasses proxy.
4. TransactionInterceptor never executes.
5. @Transactional fails.
6. @Cacheable fails.
7. @Retryable fails.
8. @Async fails.
9. Security advice fails.
10. The Proxy Boundary Mental Model explains everything.

Final memory hook:

```text
Cross Proxy Boundary
       |
       v
AOP Exists

Stay Inside Object
       |
       v
AOP Disappears
```
