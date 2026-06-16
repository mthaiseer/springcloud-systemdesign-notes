# 003_IoC_DI_Mental_Model.md

# IoC & DI Mental Model
## One Core Mental Model: "Don't Build Your Dependencies. Ask The Container For Them."

---

# Why It Exists

Before Spring, applications were built like this:

```java
public class UserService {

    private UserRepository repository =
        new UserRepository();

    private EmailService emailService =
        new EmailService();
}
```

The service creates everything itself.

This works for:

```text
10 Classes
```

But fails for:

```text
500 Classes

Database

Kafka

Redis

REST Clients

Security

Caching
```

Managing dependencies becomes a nightmare.

---

# The Problem

Imagine:

```text
Controller
      |
      v
Service
      |
      v
Repository
      |
      v
DataSource
```

Who creates:

```text
Repository ?

Datasource ?

Service ?

Controller ?
```

Without a container:

Every class creates its own dependencies.

---

# Traditional Programming

```java
UserRepository repository =
        new UserRepository();

UserService service =
        new UserService(repository);
```

Developer responsible for wiring.

Developer responsible for creation.

Developer responsible for lifecycle.

---

# Core Mental Model

Traditional Programming:

```text
Object

     |
     v

Build Dependencies
```

IoC:

```text
Object

     |
     v

Receive Dependencies
```

The responsibility flips.

This is Inversion of Control.

---

# Real World Analogy

Restaurant

Bad Design:

```text
Customer

     |
     v

Walk Into Kitchen

Cook Food

Wash Dishes

Serve Himself
```

Good Design:

```text
Customer

     |
     v

Orders Food

     |
     v

Restaurant Handles Everything
```

Customer focuses only on eating.

Service focuses only on business logic.

---

# One Picture To Remember

```text
Without Spring

Service

   |
   v

Creates Repository

   |
   v

Creates DataSource
```

```text
With Spring

ApplicationContext

      |
      v

Creates Repository

      |
      v

Creates DataSource

      |
      v

Injects Into Service
```

---

# What Is IoC?

IoC:

```text
Inversion Of Control
```

Meaning:

```text
Object Creation

Dependency Wiring

Lifecycle

Configuration
```

moves from:

```text
Application Code
```

to:

```text
Spring Container
```

---

# What Is DI?

Dependency Injection is how IoC is implemented.

Spring:

```text
Creates Object

Finds Dependencies

Injects Dependencies
```

into your bean.

---

# Rich Diagram

```text
ApplicationContext

      |
      |
      +----------------+
      |                |
      v                v

Repository      EmailService

      \           /

       \         /

        \       /

         v     v

         UserService
```

ApplicationContext owns the graph.

---

# Spring Boot Example #1

Without DI

```java
public class UserService {

    private UserRepository repository =
         new UserRepository();
}
```

Problems:

```text
Hard To Test

Hard To Replace

Hard To Mock
```

---

# With DI

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
}
```

Developer does not create repository.

Spring injects it.

---

# Internal Working

Developer writes:

```java
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(
        UserRepository repository
    ){
        this.repository = repository;
    }
}
```

Spring performs:

```text
Create Repository

Create Service

Inject Repository

Store Bean
```

---

# Dry Run #1

Startup:

```text
Need UserService

Need Repository

Create Repository

Create Service

Inject Repository

Ready
```

Simple dependency graph.

---

# Constructor Injection

Recommended approach.

```java
@Service
@RequiredArgsConstructor
class UserService {

    private final UserRepository repo;
}
```

Benefits:

```text
Immutable

Testable

Required Dependencies Visible
```

---

# Why Constructor Injection Wins

Bad:

```java
@Autowired
private UserRepository repo;
```

Object can exist in invalid state.

Good:

```java
UserService(UserRepository repo)
```

Object cannot exist without dependency.

---

# Spring Boot Example #2

```java
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepo;

    private final EmailService emailService;

    private final AuditService auditService;
}
```

Execution:

```text
Create Repository

Create EmailService

Create AuditService

Inject All

Create PaymentService
```

---

# Dependency Graph Resolution

```text
PaymentService

   |
   +------ UserRepository

   |
   +------ EmailService

   |
   +------ AuditService
```

Spring resolves graph automatically.

---

# Production Scale Example

Large E-Commerce

```text
500 Beans

Controllers

Services

Repositories

Kafka Producers

Redis Clients

Security Components
```

Without IoC:

```text
Manual Wiring Hell
```

With IoC:

```text
Container Builds Graph
```

Startup handles everything.

---

# Circular Dependency Failure

Example:

```java
class A {

   A(B b){}
}

class B {

   B(A a){}
}
```

Graph:

```text
A -> B
^    |
|    v
+----+
```

Failure.

---

# Failure Flow

```text
Create A

Need B

Create B

Need A

Create A

Need B
```

Loop forever.

Spring stops with:

```text
BeanCurrentlyInCreationException
```

---

# Production Failure Story

Team built:

```java
@Service
class PaymentService {

   private final OrderService orderService;
}

@Service
class OrderService {

   private final PaymentService paymentService;
}
```

Startup failed.

Production outage.

Root cause:

```text
Circular Dependency
```

Fix:

```text
Extract Third Service
```

---

# Spring Boot Example #3

Bad:

```java
@Service
class PaymentService {

   private final OrderService orderService;
}

@Service
class OrderService {

   private final PaymentService paymentService;
}
```

Good:

```java
@Service
class OrderCoordinator {
}
```

Graph:

```text
PaymentService

      |
      v

OrderCoordinator

      |
      v

OrderService
```

Cycle removed.

---

# Debugging Mindset

Never ask:

```text
Which Bean Failed?
```

Ask:

```text
Which Dependency Failed?
```

Huge difference.

---

# Debugging Flow

```text
Controller

      |
      v

Service

      |
      v

Repository

      |
      v

Datasource

      |
      X

Failure
```

Root cause usually lives at bottom.

---

# Senior Debugging Checklist

```text
Missing Bean?

Wrong Package Scan?

Circular Dependency?

Multiple Candidates?

Bean Creation Failure?
```

---

# Multiple Candidates Problem

Example:

```java
interface PaymentProcessor {}
```

Implementations:

```java
StripeProcessor

PaypalProcessor
```

Injection:

```java
PaymentProcessor processor;
```

Spring asks:

```text
Which One?
```

Failure.

---

# Solution

```java
@Qualifier("stripeProcessor")
```

or

```java
@Primary
```

---

# Spring Boot Example #4

```java
@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final PaymentProcessor processor;
}
```

Startup:

```text
Found Stripe

Found Paypal

Ambiguous

Startup Fails
```

Error:

```text
NoUniqueBeanDefinitionException
```

---

# Internal Execution Explanation

Spring internally:

```text
Scan Components

      |
      v

Build Bean Definitions

      |
      v

Resolve Dependencies

      |
      v

Instantiate Beans

      |
      v

Inject Dependencies

      |
      v

Store Singleton Beans
```

IoC + DI in action.

---

# Common Misconceptions

## IoC Equals DI

Wrong.

```text
IoC
```

is the principle.

```text
DI
```

is one implementation.

---

## Spring Creates Beans Randomly

Wrong.

Dependency graph determines order.

---

## Autowired Creates Objects

Wrong.

Spring creates objects.

Autowired injects references.

---

## Constructor Injection Is Verbose

Wrong.

Lombok removes boilerplate.

---

# Interview Questions

## What Is IoC?

Transfer of object creation and wiring responsibility from application code to container.

---

## What Is DI?

Process of supplying dependencies to an object.

---

## What Is The Mental Model?

Don't build dependencies.

Receive dependencies.

---

## Why Constructor Injection?

Immutability, testability, explicit dependencies.

---

## Difference Between IoC And DI?

IoC = Principle

DI = Implementation

---

## What Causes Circular Dependency?

Mutual dependency loop.

---

# Cheat Sheet

```text
IoC

Container Owns:

Object Creation

Dependency Wiring

Lifecycle

Configuration
```

```text
DI

Container:

Creates Bean

Finds Dependencies

Injects Dependencies
```

Flow:

```text
ApplicationContext

      |
      v

Create Beans

      |
      v

Inject Dependencies

      |
      v

Ready
```

---

# One Giant Picture To Remember

```text
Without IoC

Service

   |
   v

Creates Repository

   |
   v

Creates Database Client
```

```text
With IoC

ApplicationContext

      |
      +------ Repository

      |
      +------ DatabaseClient

      |
      +------ EmailService

      |
      v

Inject Into Service
```

If you remember one sentence:

"Don't build your dependencies. Ask the container for them."
