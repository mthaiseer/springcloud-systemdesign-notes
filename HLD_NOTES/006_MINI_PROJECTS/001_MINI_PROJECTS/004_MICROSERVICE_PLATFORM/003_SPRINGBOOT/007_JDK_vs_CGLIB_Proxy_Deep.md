
# 007_JDK_vs_CGLIB_Proxy.md

# MiniSpringBoot Deep Production Mode
# JDK Proxy vs CGLIB Proxy

## One Core Mental Model

> Spring cannot inject transactions, security, caching, retries, async execution, or AOP behavior into your class directly.
>
> Spring creates a PROXY object that stands between the caller and the real object.

Everything in this chapter comes from that single idea.

---

# Why This Chapter Exists

Many developers memorize:

- @Transactional
- @Cacheable
- @Async
- @Secured
- AOP

But fail to answer:

Why does Spring need a proxy?

Why does self invocation fail?

Why do final methods break transactions?

Why do some beans use JDK Proxy while others use CGLIB?

The answer is the proxy model.

---

# Problem Before Proxies Exist

Suppose:

```java
@Service
public class PaymentService {

    public void pay() {
        System.out.println("processing payment");
    }
}
```

Business requirement:

```text
Before pay():
    Start Transaction

After pay():
    Commit Transaction

If Exception:
    Rollback
```

Spring cannot rewrite your source code.

So how can it add behavior?

Answer:

Create another object.

---

# Core Mental Model

Without Proxy:

Client -> Real Object

With Proxy:

Client -> Proxy -> Real Object

The proxy becomes a programmable gateway.

```text
Client
   |
   v
Proxy
   |
   +--> Start Tx
   +--> Security Check
   +--> Cache Lookup
   +--> Metrics
   |
   v
Real Object
```

---

# Real World Analogy

Airport Security

Without security:

Passenger -> Aircraft

With security:

Passenger
    |
Security Gate
    |
Aircraft

The gate adds behavior.

Spring Proxy = Security Gate

---

# Rich ASCII Diagram

```text
                 CLIENT

                    |
                    v

        +-------------------------+
        |         PROXY           |
        +-------------------------+

          |      |       |

          v      v       v

      Security  Tx   Cache

          |      |       |

          +------+-------+

                 |
                 v

        +-------------------------+
        |      REAL OBJECT        |
        +-------------------------+
```

---

# Two Proxy Technologies

Spring has two ways.

1. JDK Dynamic Proxy
2. CGLIB Proxy

Both solve the same problem.

Only implementation differs.

---

# JDK Proxy Mental Model

JDK Proxy works using interfaces.

```java
public interface PaymentService {
    void pay();
}
```

```java
@Service
public class PaymentServiceImpl
        implements PaymentService {

    public void pay() {
    }
}
```

Generated:

```text
GeneratedProxy
      |
implements
      |
PaymentService
      |
      v
PaymentServiceImpl
```

---

# JDK Proxy Internal Working

Spring asks JVM:

```text
Generate class at runtime
```

Generated structure:

```java
class Proxy
implements PaymentService {

   public void pay() {

      beginTransaction();

      target.pay();

      commitTransaction();
   }
}
```

You never see this class.

But JVM creates it.

---

# JDK Proxy Dry Run

Request:

```java
paymentService.pay();
```

Flow:

```text
Client
  |
  v
JDK Proxy
  |
Begin Tx
  |
target.pay()
  |
Commit
```

---

# Why JDK Proxy Exists

Advantages:

```text
No bytecode inheritance
Simple
Stable
Uses JVM API
```

Limitation:

```text
Needs Interface
```

---

# CGLIB Mental Model

Suppose:

```java
@Service
public class PaymentService {

    public void pay() {
    }
}
```

No interface.

JDK Proxy cannot help.

Spring generates:

```text
PaymentServiceProxy
      extends
PaymentService
```

---

# CGLIB Internal Model

Original:

```java
public class PaymentService {

   public void pay() {
   }
}
```

Generated:

```java
public class PaymentServiceProxy
extends PaymentService {

   @Override
   public void pay() {

      beginTransaction();

      super.pay();

      commitTransaction();
   }
}
```

---

# CGLIB Dry Run

```java
paymentService.pay();
```

Flow:

```text
Client
   |
   v
Generated Subclass
   |
Begin Tx
   |
super.pay()
   |
Commit
```

---

# Side By Side Comparison

```text
JDK PROXY

Interface
    |
    v
Generated Proxy
    |
    v
Implementation
```

```text
CGLIB

Generated Child Class
          |
          v
Original Class
```

---

# How Spring Chooses

Rule:

```text
Interface Exists
      |
      v
Prefer JDK Proxy
```

Otherwise:

```text
No Interface
      |
      v
CGLIB
```

Modern Spring Boot frequently uses CGLIB.

---

# Production Scale Example

Imagine:

```text
OrderService
PaymentService
InventoryService
FraudService
NotificationService
```

Every service has:

```text
Transactions
Security
Metrics
Tracing
Caching
```

Spring cannot inject this logic manually into 500 services.

Instead:

```text
500 Services
      |
      v
500 Proxies
```

Each proxy adds behavior automatically.

---

# Transaction Internal Flow

@Transactional

```text
Client
   |
   v
Proxy
   |
Begin Transaction
   |
Target Method
   |
Commit
```

Exception:

```text
Client
   |
Proxy
   |
Begin Tx
   |
Target Method
   |
Exception
   |
Rollback
```

Transaction logic lives in proxy.

Not target object.

---

# Security Internal Flow

```text
Client
   |
Proxy
   |
Check User
   |
Authorized?
   |
Target Method
```

Again:

Security lives in proxy.

---

# Caching Internal Flow

```text
Client
   |
Proxy
   |
Cache Lookup
   |
Hit?
 |     |
Yes   No
 |     |
Return Execute
       |
   Save Cache
```

Target object knows nothing about cache.

---

# Production Failure Story 1

Developer:

```java
@Service
public final class PaymentService {
}
```

Spring uses CGLIB.

Problem:

```text
final class
cannot be extended
```

Generated subclass impossible.

Proxy creation fails.

---

# Production Failure Story 2

```java
public final void pay() {
}
```

CGLIB requires:

```text
Override Method
```

Cannot override final method.

Result:

```text
@Transactional ignored
```

---

# Production Failure Story 3

Self Invocation

```java
public void process() {
   pay();
}

@Transactional
public void pay() {
}
```

Flow:

```text
process()
   |
this.pay()
```

Proxy bypassed.

No transaction.

---

# Deep Debugging Mindset

Whenever:

```text
@Transactional fails

@Cacheable fails

@Async fails

@Retryable fails
```

Ask:

Question 1

```text
Did call go through proxy?
```

Question 2

```text
JDK or CGLIB?
```

Question 3

```text
Any final class?
```

Question 4

```text
Any final method?
```

Question 5

```text
Any self invocation?
```

This solves most production AOP bugs.

---

# Java Example - JDK Proxy

```java
public interface UserService {

    void save();
}
```

```java
@Service
public class UserServiceImpl
implements UserService {

    public void save() {
        System.out.println("save");
    }
}
```

Runtime:

```text
UserService Proxy
       |
       v
UserServiceImpl
```

---

# Java Example - CGLIB

```java
@Service
public class UserService {

    public void save() {
    }
}
```

Runtime:

```text
UserServiceProxy
      extends
UserService
```

---

# Internal Execution Walkthrough

Request enters.

```text
Controller
    |
    v
Proxy
```

Proxy chain:

```text
Transaction Interceptor
        |
Security Interceptor
        |
Metrics Interceptor
        |
Cache Interceptor
        |
Target Method
```

Return path:

```text
Target
   |
Cache
   |
Metrics
   |
Security
   |
Transaction
   |
Client
```

---

# Common Misconceptions

Wrong:

```text
@Transactional code
exists inside method
```

Correct:

```text
@Transactional logic
exists inside proxy
```

Wrong:

```text
Proxy is optional
```

Correct:

```text
Proxy is required
for Spring AOP features
```

Wrong:

```text
Self invocation should work
```

Correct:

```text
Self invocation bypasses proxy
```

---

# Interview Questions

Q. Why does Spring use proxies?

A:

To add behavior without modifying application code.

Q. When does Spring use JDK Proxy?

A:

When an interface is available.

Q. When does Spring use CGLIB?

A:

When class-based proxying is needed.

Q. Why do final methods break transactions?

A:

CGLIB cannot override final methods.

Q. Why does self invocation fail?

A:

Proxy is bypassed.

Q. Where does transaction logic live?

A:

Inside transaction interceptor attached to proxy.

---

# Cheat Sheet

```text
JDK Proxy

Requires Interface

Implements Interface

Cannot proxy concrete class directly
```

```text
CGLIB

No Interface Needed

Creates Child Class

Cannot extend Final Class

Cannot override Final Method
```

```text
Proxy Hit
      =
AOP Works

Proxy Bypass
      =
AOP Fails
```

---

# One Picture To Remember

```text
                 SPRING MAGIC

                       |

                       v

                    PROXY

              /                \

             /                  \

            v                    v

      JDK Proxy            CGLIB Proxy

     Interface              Subclass

            \               /

             \             /

              \           /

                Real Object



No Proxy
   =
No Transaction

No Proxy
   =
No Security

No Proxy
   =
No Cache

Proxy
   =
Spring AOP
```

# Final Memory Hook

Remember only:

Spring never puts transaction logic inside your class.

Spring puts transaction logic inside a proxy.

JDK Proxy = Interface based.

CGLIB = Subclass based.

Everything else is detail.
