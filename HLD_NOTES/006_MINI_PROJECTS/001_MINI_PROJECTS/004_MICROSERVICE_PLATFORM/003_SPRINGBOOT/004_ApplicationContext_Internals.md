# 004_ApplicationContext_Internals.md

# ApplicationContext Internals
## The Mental Model: Spring's Bean Factory Is a Smart Container That Builds and Manages an Entire Object Graph

---

# Why It Exists

Before Spring, developers manually created every object:

```java
UserRepository repo = new UserRepository();
EmailService email = new EmailService();
UserService service = new UserService(repo, email);
```

As systems grew:

- Hundreds of objects
- Complex dependencies
- Different environments
- Lifecycle management
- Cross-cutting features

Manual wiring became impossible.

Spring created ApplicationContext.

ApplicationContext is NOT a bag of objects.

ApplicationContext is a factory + registry + dependency resolver + lifecycle manager.

---

# Problem

Imagine 500 classes.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Datasource
```

Who creates them?

Who creates them in correct order?

Who injects dependencies?

Who initializes them?

Who destroys them?

Without a container:

Chaos.

---

# Mental Model

ApplicationContext behaves like a city construction authority.

You don't build houses yourself.

You submit blueprints.

Authority:

1. Reads blueprint
2. Determines dependencies
3. Builds houses
4. Connects utilities
5. Keeps registry
6. Maintains lifecycle

Spring does exactly this.

---

# Real World Analogy

Airport Control Tower

Passengers = Requests

Planes = Beans

Runways = Dependencies

Tower = ApplicationContext

```text
               CONTROL TOWER
                      |
                      |
       +--------------+-------------+
       |                            |
     Plane A                    Plane B
       |                            |
     Fuel                        Fuel
       |                            |
   Ground Crew                 Ground Crew
```

Tower knows everything.

Planes know nothing.

ApplicationContext knows everything.

Beans know nothing.

---

# One Core Diagram

```text
                    ApplicationContext

                          |
         ----------------------------------------
         |                |                    |
         |                |                    |
     Bean Registry   Dependency Graph   Lifecycle Engine
         |                |                    |
         |                |                    |
         ----------------------------------------
                          |
                     Creates Beans
                          |
                          v

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

Remember:

ApplicationContext owns the graph.

---

# Internal Working

Startup sequence:

```text
SpringApplication.run()

        |
        v

Create ApplicationContext

        |
        v

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

Initialize Beans

        |
        v

Ready
```

Most developers only see:

```java
@SpringBootApplication
public class App {}
```

Internally thousands of operations occur.

---

# Bean Definition Phase

Spring does NOT create beans immediately.

First:

It creates metadata.

```java
@Service
public class UserService {}
```

becomes:

```text
BeanDefinition

Name: userService
Type: UserService
Scope: Singleton
Lazy: false
Dependencies: []
```

Think:

Blueprint.

Not actual building.

---

# Phase 1: Component Scan

Spring scans packages.

```text
com.company

   |
   +-- Controller
   +-- Service
   +-- Repository
```

Finds:

```java
@Component
@Service
@Repository
@Controller
```

Creates BeanDefinitions.

Result:

```text
BeanDefinitionMap

userController
userService
userRepository
```

Still no objects.

Only plans.

---

# Phase 2: Dependency Graph Creation

Example:

```java
UserController
     |
     v
UserService
     |
     v
UserRepository
```

Spring creates graph.

```text
Controller
    |
Service
    |
Repository
```

Now Spring knows:

Repository first.

Then Service.

Then Controller.

---

# Phase 3: Bean Creation

Repository:

```java
new UserRepository()
```

Service:

```java
new UserService(repository)
```

Controller:

```java
new UserController(service)
```

Dependency graph drives creation order.

---

# Dry Run #1

Classes:

```java
@Repository
class UserRepository {}

@Service
class UserService {
    UserService(UserRepository repo){}
}

@RestController
class UserController {
    UserController(UserService service){}
}
```

Execution:

```text
Step 1

Need Controller

Controller requires Service

Pause Controller

Step 2

Need Service

Service requires Repository

Pause Service

Step 3

Create Repository

Repository Ready

Step 4

Create Service

Service Ready

Step 5

Create Controller

Controller Ready
```

This is recursive dependency resolution.

---

# Internal Cache

Singleton beans stored here:

```text
Singleton Cache

--------------------------------
userRepository
userService
userController
--------------------------------
```

Future requests:

No new objects.

Spring returns cached instance.

---

# Why Singleton By Default

Creating objects repeatedly is expensive.

Imagine:

```text
1000 requests/sec
```

Without singleton:

```text
1000 UserService objects/sec
```

Huge waste.

Instead:

```text
One UserService

Shared by all requests
```

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

Spring tries:

```text
Create A
Need B

Create B
Need A

Create A
Need B
```

Loop.

Failure.

```text
BeanCurrentlyInCreationException
```

---

# Production Scale Example

E-commerce Platform

```text
500 Beans

Controllers      40
Services        150
Repositories     80
Clients          50
Configs          60
Utilities       120
```

Startup:

```text
Scan
Build Definitions
Resolve Graph
Instantiate
Initialize
Ready
```

ApplicationContext manages all.

---

# Internal Data Structures

Simplified:

```text
ApplicationContext

    |
    +-- BeanDefinitionMap
    |
    +-- SingletonObjects
    |
    +-- BeanFactory
    |
    +-- Environment
    |
    +-- EventPublisher
```

Most important:

```text
BeanDefinitionMap
SingletonObjects
```

Blueprints + Actual Objects

---

# Bean Retrieval Flow

When code executes:

```java
context.getBean(UserService.class);
```

Flow:

```text
Check Singleton Cache

      |
      |
 Exists?
  /   \
Yes    No
 |      |
Return  Create Bean
```

---

# Dry Run #2

```java
UserService s1 =
 context.getBean(UserService.class);

UserService s2 =
 context.getBean(UserService.class);
```

Flow:

```text
First Call

Cache Miss
Create Bean
Store Cache

Second Call

Cache Hit
Return Existing Bean
```

Result:

```java
s1 == s2
```

true

---

# Lifecycle Integration

Creation:

```text
Constructor
     |
Dependency Injection
     |
@PostConstruct
     |
Ready
```

Destruction:

```text
@PreDestroy
     |
Shutdown
```

ApplicationContext orchestrates everything.

---

# Production Failure Story

A team had:

```java
@Service
public class PricingService {

    public PricingService(
       CurrencyClient client,
       TaxClient taxClient,
       DiscountClient discountClient,
       ...
       25 dependencies
    )
}
```

Startup time:

30+ seconds.

Problem:

Huge dependency graph.

Result:

Slow boot.

Solution:

Split service.

Reduce graph complexity.

Lesson:

ApplicationContext startup cost grows with graph complexity.

---

# Failure Flow

Missing Bean

```java
@Service
class UserService {

    UserService(PaymentClient client){}
}
```

No PaymentClient bean.

Startup:

```text
Create UserService

Need PaymentClient

Not Found

Fail Startup
```

Error:

```text
NoSuchBeanDefinitionException
```

---

# Debugging Mindset

Never ask:

```text
Why bean failed?
```

Ask:

```text
What dependency chain failed?
```

Example:

```text
Controller

 -> Service

 -> Repository

 -> DataSource

 -> Driver

 FAILED
```

Root cause often lives at bottom.

---

# How Seniors Debug

Enable:

```properties
logging.level.org.springframework=DEBUG
```

Watch:

```text
Creating shared instance of bean

Creating bean userService

Creating bean userRepository
```

Follow chain.

---

# Common Misconceptions

## Misconception 1

ApplicationContext = Bean Factory

Wrong.

BeanFactory is only one part.

ApplicationContext adds:

- Events
- Environment
- Resource loading
- Aware interfaces
- Internationalization

---

## Misconception 2

Beans created when scanned

Wrong.

Scanning creates definitions.

Instantiation happens later.

---

## Misconception 3

Spring magically injects objects

Wrong.

Dependency graph resolution drives injection.

---

# Java Example

```java
@Repository
class UserRepository {

    public UserRepository() {
        System.out.println("Repository");
    }
}

@Service
class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
        System.out.println("Service");
    }
}

@RestController
class UserController {

    public UserController(UserService service) {
        System.out.println("Controller");
    }
}
```

Startup:

```text
Repository
Service
Controller
```

Order determined by graph.

---

# Internal Execution Explanation

Spring sees:

```text
Controller

requires

Service

requires

Repository
```

Algorithm:

```text
Build Repository

Build Service

Build Controller
```

Topological dependency resolution.

---

# Advanced Mental Model

ApplicationContext behaves like:

```text
Compiler
+
Dependency Graph Engine
+
Object Cache
+
Lifecycle Manager
```

Most developers only see DI.

Senior engineers see graph management.

---

# Interview Questions

## What is ApplicationContext?

A container that manages bean definitions, dependency resolution, bean lifecycle, configuration, events, and object retrieval.

---

## Difference Between BeanFactory and ApplicationContext?

BeanFactory:

Basic DI container.

ApplicationContext:

Advanced container built on top of BeanFactory.

---

## Why singleton scope?

Memory efficiency and startup performance.

---

## How does dependency injection work?

Spring builds dependency graph and recursively resolves required beans.

---

## Why circular dependency happens?

Graph contains cycle.

```text
A -> B -> A
```

Cannot resolve clean creation order.

---

# Cheat Sheet

```text
ApplicationContext

Purpose:
Manage complete object graph

Stores:
Bean Definitions
Singleton Objects

Startup:

Scan
↓
Definitions
↓
Graph
↓
Instantiate
↓
Initialize

Most Important Structures:

BeanDefinitionMap
SingletonCache

Common Errors:

NoSuchBeanDefinitionException
BeanCurrentlyInCreationException

Mental Model:

Smart Factory + Registry +
Lifecycle Manager
```

---

# One Picture To Remember

```text
                APPLICATION CONTEXT

                      BRAIN

                         |
     -------------------------------------------------
     |                 |                |            |
     v                 v                v            v

 Bean          Dependency       Lifecycle       Cache
Definitions       Graph          Manager

     \               |               |          /
      \              |               |         /
       \             |               |        /
        -------------------------------------
                        |
                        v

               Complete Object Graph

 Controller
      |
 Service
      |
 Repository
      |
 DataSource
```

If you remember only one sentence:

"ApplicationContext owns, builds, wires, caches, and manages the entire application object graph."
