# 006_AOP_Mental_Model.md

# AOP Mental Model
## One Core Mental Model: AOP Is A Smart Middleman Inserted Between Caller And Target

---

# Why AOP Exists

Imagine 500 service methods.

Every method needs:

```text
Logging
Transactions
Security
Metrics
Retry
Auditing
```

Without AOP:

```java
public void transfer() {

    log();

    startTransaction();

    try {
        businessLogic();

        commit();
    }
    catch(Exception e) {
        rollback();
    }
}
```

Business code becomes polluted.

AOP solves this.

---

# The Problem

Suppose:

```java
transferMoney()

createOrder()

cancelOrder()

refundPayment()
```

Every method needs:

```text
Transaction
Logging
Security
```

Duplicated code everywhere.

This violates:

```text
Single Responsibility Principle
```

---

# Mental Model

AOP = Smart Middleman

```text
Caller
   |
   v

Smart Middleman
   |
   v

Real Object
```

The middleman can:

```text
Execute Code Before

Execute Code After

Execute Code On Error

Modify Return Value
```

---

# Real World Analogy

Airport Security

Passenger wants airplane.

Actual flow:

```text
Passenger
     |
     v

Security Checkpoint
     |
     v

Boarding Gate
     |
     v

Aircraft
```

Passenger never directly enters aircraft.

Security sits in between.

AOP works exactly the same.

---

# Java Example

Without AOP

```java
public void transfer() {

    System.out.println("Start");

    businessLogic();

    System.out.println("End");
}
```

With AOP

```java
@Logged
public void transfer() {

    businessLogic();
}
```

Logging moved outside business code.

---

# One Picture To Remember

```text
CALLER

   |
   v

+----------------+
| SMART MIDDLEMAN|
+----------------+

   |
   v

TARGET OBJECT
```

Everything in AOP comes from this picture.

---

# Rich ASCII Diagram

```text
Client

   |
   v

+----------------------+
| Logging Advice       |
+----------------------+

   |
   v

+----------------------+
| Transaction Advice   |
+----------------------+

   |
   v

+----------------------+
| Security Advice      |
+----------------------+

   |
   v

PaymentService
```

---

# Core AOP Vocabulary

```text
Target
= Real Object

Advice
= Extra Behavior

Aspect
= Collection Of Advice

Join Point
= Method Execution

Proxy
= Smart Middleman

Pointcut
= Which Methods?
```

---

# Internal Working

Developer writes:

```java
@Transactional
@Service
class PaymentService {}
```

Spring sees:

```text
Transactional Annotation
```

Spring creates:

```text
Proxy
      |
      v
PaymentService
```

---

# Internal Flow

```text
Bean Creation

      |
      v

BeanPostProcessor

      |
      v

Detect AOP Annotation

      |
      v

Create Proxy

      |
      v

Store Proxy In Context
```

ApplicationContext usually stores proxy.

---

# Dry Run #1

Code:

```java
paymentService.transfer();
```

Actual Flow:

```text
Caller

   |
   v

Proxy

   |
   v

Real Method
```

Not:

```text
Caller
   |
   v
Real Method
```

---

# Around Advice Mental Model

Most important advice.

```text
Before

      |
      v

Target Method

      |
      v

After
```

Spring internally:

```java
before();

try {

    method();

    after();

}
catch(Exception e){

    error();

}
```

---

# Dry Run #2

Transaction Example

```java
@Transactional
public void transfer() {
}
```

Flow:

```text
Proxy Receives Call

Start Transaction

Call Target

Commit

Return
```

Exception:

```text
Proxy Receives Call

Start Transaction

Call Target

Exception

Rollback
```

---

# Why Proxy Exists

Without proxy:

```java
transfer()
```

cannot magically gain:

```text
Transaction

Security

Logging
```

Proxy provides interception point.

---

# Production Scale Example

E-Commerce System

```text
300 Services

10,000 Requests/sec
```

Requirements:

```text
Transactions

Metrics

Tracing

Security
```

Instead of modifying 300 services:

```text
Add Aspects Once
```

All services gain behavior.

---

# Multi Layer Interception

```text
Client

  |
  v

Security Aspect

  |
  v

Transaction Aspect

  |
  v

Metrics Aspect

  |
  v

Target Service
```

---

# Production Failure Story

Team created:

```java
@Transactional
private void save() {
}
```

Expected:

```text
Transaction
```

Reality:

```text
No Transaction
```

Why?

Proxy cannot intercept private methods.

Outage followed.

---

# Failure Flow

Developer:

```java
@Transactional
private void save() {
}
```

Flow:

```text
Caller

   |
   v

Target Directly

No Proxy

No Transaction
```

---

# Famous Self Invocation Problem

```java
@Service
public class PaymentService {

    public void process() {

        save();
    }

    @Transactional
    public void save() {
    }
}
```

Developer expects:

```text
Transaction Active
```

Reality:

```text
No Transaction
```

---

# Internal Execution

Expected:

```text
process()

  |
  v

Proxy

  |
  v

save()
```

Actual:

```text
process()

   |
   v

save()

Inside Same Object
```

Proxy bypassed.

This is one of Spring's most famous bugs.

---

# Debugging Mindset

Always ask:

```text
Did Call Pass Through Proxy?
```

Not:

```text
Why Transaction Failed?
```

If proxy not involved:

```text
AOP Not Applied
```

---

# Debugging Checklist

```text
Public Method?

Spring Bean?

Proxy Created?

Called Through Bean?

Self Invocation?
```

---

# How Seniors Debug

Check actual class.

```java
System.out.println(
service.getClass()
);
```

Output:

```text
PaymentService$$SpringProxy
```

Proxy exists.

If output:

```text
PaymentService
```

No proxy.

---

# Common Misconceptions

## AOP Changes My Code

Wrong.

AOP wraps code.

---

## Transactional Lives In Service

Wrong.

Transaction logic lives in proxy.

---

## Self Invocation Uses Proxy

Wrong.

It bypasses proxy.

---

## Private Methods Work

Wrong.

Proxy cannot intercept them.

---

# Spring Boot Example

```java
@Service
public class PaymentService {

    @Transactional
    public void transfer() {

        System.out.println("Business Logic");
    }
}
```

Execution:

```text
Client

  |
  v

Transaction Proxy

  |
  v

PaymentService.transfer()

  |
  v

Commit
```

---

# Internal Execution Explanation

Spring proxy performs:

```java
startTransaction();

try {

    target.transfer();

    commit();

}
catch(Exception e){

    rollback();
}
```

Your method never contains this code.

Proxy injects it.

---

# Dry Run #3

Security Example

```java
@Secured("ADMIN")
public void deleteUser() {
}
```

Flow:

```text
Proxy Receives Call

Check Role

ADMIN ?

   |
  Yes
   |
   v

Call Method
```

Otherwise:

```text
Access Denied
```

---

# Interview Questions

## What Is AOP?

A mechanism that inserts behavior around method execution without modifying business code.

---

## What Is The Mental Model?

A smart middleman between caller and target.

---

## Why Does Spring Need Proxies?

To intercept method calls and add behavior.

---

## Why Does Self Invocation Fail?

Call never goes through proxy.

---

## Why Does Transactional Need Public Methods?

Proxy interception works on externally invoked methods.

---

## What Is Advice?

Code executed before, after, around, or on exception.

---

# Cheat Sheet

```text
AOP

Target
= Real Object

Proxy
= Smart Middleman

Advice
= Extra Behavior

Aspect
= Collection Of Advice

Pointcut
= Which Methods

Join Point
= Method Execution
```

Core Flow:

```text
Caller

  |
  v

Proxy

  |
  v

Target
```

---

# One Giant Picture To Remember

```text
User Request

      |
      v

+-------------------+
| Security Aspect   |
+-------------------+

      |
      v

+-------------------+
| Transaction Aspect|
+-------------------+

      |
      v

+-------------------+
| Logging Aspect    |
+-------------------+

      |
      v

+-------------------+
| Metrics Aspect    |
+-------------------+

      |
      v

PaymentService

      |
      v

Database
```

If you remember one sentence:

"AOP is a smart middleman that intercepts method calls and adds behavior before the real method executes."
