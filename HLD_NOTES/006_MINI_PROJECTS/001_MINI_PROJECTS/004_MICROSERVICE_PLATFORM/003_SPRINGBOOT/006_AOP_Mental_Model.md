# 006_AOP_Mental_Model.md

# AOP Mental Model
## One Core Mental Model: AOP Is A Smart Middleman Inserted Between Caller And Target

---

# Why AOP Exists

Modern applications contain hundreds of business methods.

```text
Create Order
Transfer Money
Book Ticket
Cancel Booking
Generate Invoice
```

Every method usually needs:

```text
Logging
Transaction
Security
Metrics
Tracing
Retry
Auditing
```

Without AOP:

Business logic becomes mixed with infrastructure logic.

---

# The Problem

Imagine 300 service methods.

Without AOP:

```java
public void transfer() {

    logStart();

    startTransaction();

    try {

        businessLogic();

        commit();

        logSuccess();

    } catch(Exception e) {

        rollback();

        logFailure();
    }
}
```

Now repeat this in:

```text
300 Services
500 Methods
```

Huge duplication.

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

Target Object
```

Everything in Spring AOP comes from this picture.

The middleman can:

```text
Run Code Before

Run Code After

Run Code On Error

Modify Results

Block Access
```

---

# Real World Analogy

Airport Security

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

Security sits between.

AOP works exactly the same.

---

# One Picture To Remember

```text
CLIENT

   |
   v

+----------------+
| SMART MIDDLEMAN|
+----------------+

   |
   v

REAL OBJECT
```

Never forget this.

---

# Spring Boot Example #1

Without AOP

```java
@Service
public class PaymentService {

    public void transfer() {

        System.out.println("START");

        businessLogic();

        System.out.println("END");
    }
}
```

Business code polluted.

With AOP:

```java
@Logged
public void transfer() {

    businessLogic();
}
```

Logging moved outside business code.

---

# Rich ASCII Diagram

```text
Client

   |
   v

Logging Advice

   |
   v

Transaction Advice

   |
   v

Security Advice

   |
   v

PaymentService
```

---

# Core Vocabulary

```text
Target
= Real Object

Proxy
= Smart Middleman

Advice
= Extra Behavior

Aspect
= Collection Of Advice

Join Point
= Method Execution

Pointcut
= Which Methods?
```

---

# Internal Working

Developer writes:

```java
@Transactional
@Service
public class PaymentService {
}
```

Spring sees:

```text
Transactional Annotation
```

Creates:

```text
Proxy
      |
      v
PaymentService
```

---

# Proxy Creation Pipeline

During startup:

```text
Bean Creation

      |
      v

BeanPostProcessor

      |
      v

InfrastructureAdvisorAutoProxyCreator

      |
      v

ProxyFactory

      |
      v

JDK/CGLIB Proxy

      |
      v

Stored In Context
```

This is where AOP enters Spring.

---

# What Gets Injected?

Code:

```java
@Autowired
private PaymentService service;
```

Reality:

```text
service

      |
      v

PaymentService$$SpringProxy

      |
      v

PaymentService
```

Most developers think:

```text
Injected Bean = PaymentService
```

Reality:

```text
Injected Bean = Proxy
```

Very important.

---

# Dry Run #1

Code:

```java
service.transfer();
```

Execution:

```text
Caller

   |
   v

Proxy

   |
   v

PaymentService
```

Not:

```text
Caller

   |
   v

PaymentService
```

---

# Around Advice Mental Model

Most important advice type.

```text
Before

      |
      v

Target Method

      |
      v

After
```

Internally:

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

# Spring Boot Example #2

```java
@Aspect
@Component
public class LoggingAspect {

    @Around(
      "execution(* com.demo..*(..))"
    )
    public Object log(
      ProceedingJoinPoint pjp
    ) throws Throwable {

        System.out.println("Before");

        Object result =
            pjp.proceed();

        System.out.println("After");

        return result;
    }
}
```

Execution:

```text
Before

Real Method

After
```

---

# Transaction Example

```java
@Transactional
public void transfer() {

    debit();

    credit();
}
```

Developer only sees:

```java
transfer()
```

Proxy performs everything else.

---

# Internal Execution

```text
Client

    |
    v

Transaction Proxy

    |
    v

Begin Transaction

    |
    v

transfer()

    |
    v

Commit
```

---

# Failure Flow

Exception:

```text
Client

    |
    v

Proxy

    |
    v

Begin Transaction

    |
    v

transfer()

    |
    v

Exception

    |
    v

Rollback
```

This is the real magic of Transactional.

---

# Spring Boot Example #3

```java
@Transactional
public void createOrder() {

    orderRepo.save(order);

    paymentRepo.save(payment);

    throw new RuntimeException();
}
```

Execution:

```text
Start Transaction

Save Order

Save Payment

Exception

Rollback Everything
```

Database remains consistent.

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

Logging Aspect

  |
  v

Metrics Aspect

  |
  v

Target Service
```

Many aspects can execute together.

---

# Production Scale Example

E-Commerce System

```text
300 Services

10000 Requests/sec
```

Requirements:

```text
Transaction

Tracing

Metrics

Security

Logging
```

Without AOP:

Modify hundreds of services.

With AOP:

Add behavior once.

Entire system gains capability.

---

# Famous Production Failure

Developer:

```java
@Transactional
private void save() {
}
```

Expected:

```text
Transaction Active
```

Reality:

```text
No Transaction
```

Why?

---

# Failure Explanation

```text
Caller

   |
   v

Target Directly

No Proxy

No Interception

No Transaction
```

Private methods cannot be intercepted.

---

# Self Invocation Problem

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

Expected:

```text
Transaction Active
```

Reality:

```text
No Transaction
```

---

# Internal Execution

Developer imagines:

```text
process()

   |
   v

Proxy

   |
   v

save()
```

Reality:

```text
process()

   |
   v

this.save()

   |
   v

Target Object
```

Proxy bypassed.

---

# Spring Boot Example #4

```java
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentService self;

    public void process() {

        self.save();
    }

    @Transactional
    public void save() {
    }
}
```

Now:

```text
Call Goes Through Proxy

Transaction Active
```

(Alternative fixes explained in later chapter.)

---

# Security Example

```java
@PreAuthorize(
 "hasRole('ADMIN')"
)
public void deleteUser() {
}
```

Execution:

```text
Client

      |
      v

Security Proxy

      |
      v

Check Role

      |
      +--- ADMIN -> Continue

      |
      +--- Not ADMIN -> 403
```

---

# Logging Example

```java
@LogExecutionTime
public Order getOrder() {
}
```

Execution:

```text
Before Advice

Start Timer

     |
     v

getOrder()

     |
     v

Stop Timer

     |
     v

Write Log
```

---

# Debugging Mindset

Never ask:

```text
Why Transaction Failed?
```

Ask:

```text
Did The Call Pass Through Proxy?
```

If proxy missing:

```text
AOP Missing
```

---

# Senior Debugging Checklist

```text
Spring Bean?

Public Method?

Proxy Created?

Self Invocation?

Final Method?

Private Method?
```

---

# Proxy Inspection

```java
AopUtils.isAopProxy(bean)

AopUtils.isJdkDynamicProxy(bean)

AopUtils.isCglibProxy(bean)
```

Useful in production debugging.

---

# How Seniors Debug

```java
System.out.println(
bean.getClass()
);
```

Output:

```text
PaymentService$$SpringProxy
```

Proxy exists.

If:

```text
PaymentService
```

No proxy.

---

# Common Misconceptions

## AOP Modifies My Code

Wrong.

It wraps code.

---

## Transaction Logic Lives In Service

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

## AOP Is Only For Transactions

Wrong.

Used for:

```text
Logging

Metrics

Security

Caching

Retry

Tracing
```

---

# Interview Questions

## What Is AOP?

A mechanism that inserts behavior around method execution without modifying business code.

---

## What Is The Mental Model?

Smart middleman between caller and target.

---

## Why Does Spring Need Proxy?

To intercept method calls.

---

## Why Does Self Invocation Fail?

Proxy bypassed.

---

## Why Does Transactional Need Public Methods?

Proxy interception.

---

## What Is Advice?

Behavior executed around method calls.

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
= Advice Collection

Pointcut
= Which Methods

Join Point
= Method Execution
```

Flow:

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

Security Aspect

      |
      v

Transaction Aspect

      |
      v

Logging Aspect

      |
      v

Metrics Aspect

      |
      v

PaymentService

      |
      v

Database
```

If you remember one sentence:

AOP is a smart middleman that intercepts calls and adds behavior before the real method executes.
