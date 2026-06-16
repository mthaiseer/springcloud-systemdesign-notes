# 005_Bean_Lifecycle.md

# Bean Lifecycle
## The Mental Model: Spring Raises Every Bean From Birth To Death

---

# Why It Exists

Most developers think:

```java
@Service
class UserService {}
```

and Spring magically injects it.

Reality:

A bean goes through an entire lifecycle.

```text
Birth
 ↓
Dependency Injection
 ↓
Initialization
 ↓
Ready For Use
 ↓
Destruction
```

Without lifecycle management:

- Resources leak
- Connections remain open
- Configuration incomplete
- Startup becomes unpredictable
- Shutdown becomes dangerous

Spring solves this using Bean Lifecycle Management.

---

# Problem

Imagine a bean:

```java
class DatabaseClient {
}
```

Questions:

```text
When should connection open?

Who injects dependencies?

Who validates config?

Who starts background threads?

Who closes resources?
```

Without a lifecycle:

Chaos.

---

# Mental Model

A Bean is not an object.

A Bean is a managed object.

Huge difference.

Normal Object:

```text
new Object()

Created

Used

Garbage Collected
```

Spring Bean:

```text
Create
 ↓
Inject
 ↓
Initialize
 ↓
Manage
 ↓
Destroy
```

ApplicationContext supervises entire journey.

---

# Real World Analogy

Employee Lifecycle

```text
Candidate
    ↓
Hiring
    ↓
Training
    ↓
Assigned Desk
    ↓
Working
    ↓
Retirement
```

Bean Lifecycle:

```text
Class
   ↓
Instantiation
   ↓
Dependency Injection
   ↓
Initialization
   ↓
Serving Requests
   ↓
Destruction
```

Spring behaves like HR.

---

# One Picture To Remember

```text
             SPRING CONTAINER

                     |
                     |
                     v

             Instantiate Bean

                     |
                     v

            Inject Dependencies

                     |
                     v

           Post Processing Phase

                     |
                     v

              Initialization

                     |
                     v

                Ready State

                     |
                     v

                Application

                     |
                     v

                Destruction
```

Everything passes through Spring.

---

# Complete Lifecycle Diagram

```text
Class Definition

        |
        v

Constructor Called

        |
        v

Dependency Injection

        |
        v

Aware Interfaces

        |
        v

BeanPostProcessor Before Init

        |
        v

@PostConstruct

        |
        v

afterPropertiesSet()

        |
        v

Custom Init Method

        |
        v

BeanPostProcessor After Init

        |
        v

READY

        |
        v

Application Running

        |
        v

@PreDestroy

        |
        v

destroy()

        |
        v

Custom Destroy Method
```

This is the full lifecycle.

---

# Internal Working

When Spring starts:

```text
ApplicationContext
        |
        v
Find Bean Definition
        |
        v
Create Bean
        |
        v
Manage Lifecycle
```

Lifecycle is orchestrated by:

```text
BeanFactory
+
BeanPostProcessor
+
Lifecycle Callbacks
```

---

# Phase 1: Instantiation

Spring executes:

```java
new UserService()
```

or

```java
new UserService(repo)
```

depending on constructor.

At this point:

```text
Object Exists

Dependencies Not Injected Yet
```

---

# Dry Run #1

```java
@Service
class UserService {

    public UserService() {
        System.out.println("Constructor");
    }
}
```

Output:

```text
Constructor
```

Only object creation happened.

Nothing else.

---

# Phase 2: Dependency Injection

Example:

```java
@Service
class UserService {

    private final UserRepository repo;

    UserService(UserRepository repo) {
        this.repo = repo;
    }
}
```

Flow:

```text
Need UserService

Need UserRepository

Create Repository

Inject Repository

Create Service
```

Dependency graph resolved first.

---

# Dependency Resolution Diagram

```text
UserController
      |
      v
UserService
      |
      v
UserRepository
      |
      v
DataSource
```

Spring walks graph from bottom upward.

---

# Phase 3: Aware Interfaces

Spring can inject container information.

Example:

```java
BeanNameAware
ApplicationContextAware
EnvironmentAware
```

Flow:

```text
Bean Created

Spring Says:

Here is your Bean Name

Here is ApplicationContext

Here is Environment
```

Most projects rarely need this.

---

# Phase 4: BeanPostProcessor Before Initialization

Critical concept.

Spring executes:

```text
postProcessBeforeInitialization()
```

for every bean.

Think:

```text
Inspection Phase
```

---

# Why BeanPostProcessor Exists

Spring wants extension points.

Example:

```text
Logging

Validation

AOP

Transactions

Security
```

Need hook before bean becomes active.

---

# Phase 5: @PostConstruct

Example:

```java
@PostConstruct
public void init() {
    System.out.println("Init");
}
```

Runs after:

```text
Constructor

Dependency Injection
```

Perfect place for:

```text
Validation

Cache Warmup

Resource Initialization
```

---

# Dry Run #2

```java
@Service
class UserService {

    UserService() {
        System.out.println("Constructor");
    }

    @PostConstruct
    void init() {
        System.out.println("PostConstruct");
    }
}
```

Output:

```text
Constructor

PostConstruct
```

Order matters.

---

# Phase 6: InitializingBean

```java
class UserService
implements InitializingBean {

    public void afterPropertiesSet() {
    }
}
```

Spring invokes:

```text
afterPropertiesSet()
```

after injection.

Less common today.

@PostConstruct preferred.

---

# Phase 7: Custom Init Method

Configuration:

```java
@Bean(initMethod="start")
```

Flow:

```text
PostConstruct

↓

afterPropertiesSet()

↓

start()
```

---

# Phase 8: BeanPostProcessor After Initialization

Very important.

Spring now executes:

```text
postProcessAfterInitialization()
```

This phase creates proxies.

AOP lives here.

Transactions live here.

Security often lives here.

Remember:

Proxy creation happens AFTER initialization.

---

# Lifecycle Timeline

```text
Constructor
      ↓
Injection
      ↓
BeforeInit BPP
      ↓
@PostConstruct
      ↓
afterPropertiesSet
      ↓
Custom Init
      ↓
AfterInit BPP
      ↓
Proxy Creation
      ↓
Ready
```

Memorize this flow.

---

# Production Example

Payment Service

```java
@Service
class PaymentService {

    @PostConstruct
    void init() {

        LoadExchangeRates();

        WarmCache();

        ValidateConfiguration();
    }
}
```

Startup:

```text
Create Bean

Inject Dependencies

Run Init

Ready
```

System safe before serving traffic.

---

# Production Failure Story

Team created:

```java
@PostConstruct
void init() {

   Thread.sleep(30000);
}
```

500 beans.

Startup:

```text
30 seconds × many services
```

Application needed minutes to boot.

Production outage occurred after restart.

Lesson:

Keep initialization lightweight.

---

# Failure Flow

Example:

```java
@PostConstruct
void init() {

   throw new RuntimeException();
}
```

Flow:

```text
Bean Creation

Injection

@PostConstruct

Exception
```

Result:

```text
Bean Creation Failed

Context Startup Failed
```

Application may not start.

---

# Real Production Failure

Database unavailable.

```java
@PostConstruct
void init() {

   datasource.getConnection();
}
```

Startup:

```text
DB Down

Init Fails

Bean Fails

Context Fails

Application Down
```

Seen frequently in production.

---

# Debugging Mindset

When startup fails ask:

```text
Which lifecycle phase failed?
```

Not:

```text
Which bean failed?
```

Better question.

---

# Debugging Flow

```text
Constructor?
      |
      v

Injection?
      |
      v

PostConstruct?
      |
      v

Proxy Creation?
      |
      v

Ready?
```

Locate phase.

Locate issue.

---

# Reading Logs Like Seniors

Logs:

```text
Creating shared instance

Finished creating instance

Invoking init methods
```

Interpretation:

```text
Instantiation Success

Injection Success

Initialization Running
```

Now you know exact lifecycle stage.

---

# Destruction Phase

Shutdown:

```text
SIGTERM

Spring Shutdown

Destroy Beans
```

Order:

```text
Ready Bean

↓

@PreDestroy

↓

destroy()

↓

Custom Destroy
```

---

# Example

```java
@Service
class ConnectionManager {

    @PreDestroy
    void shutdown() {
        System.out.println("Closing");
    }
}
```

Application stops:

```text
Closing
```

Resources released.

---

# Why Destruction Matters

Imagine:

```text
Database Connections

Kafka Consumers

Threads

File Handles
```

Not closed.

Result:

```text
Resource Leaks
```

Production instability.

---

# Dry Run #3

Startup:

```text
Constructor

Inject

PostConstruct

Ready
```

Shutdown:

```text
PreDestroy

Destroy
```

Full lifecycle complete.

---

# Common Misconceptions

## Misconception 1

Constructor = Ready Bean

Wrong.

Dependencies may not be injected yet.

---

## Misconception 2

@PostConstruct runs before injection

Wrong.

Runs after dependency injection.

---

## Misconception 3

Destroy methods always run

Wrong.

Force kill:

```text
kill -9
```

No graceful shutdown.

---

## Misconception 4

BeanPostProcessor optional concept

Wrong.

AOP depends heavily on it.

---

# Java Example

```java
@Service
public class UserService
implements InitializingBean {

    public UserService() {
        System.out.println("Constructor");
    }

    @PostConstruct
    public void init() {
        System.out.println("PostConstruct");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("AfterPropertiesSet");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("PreDestroy");
    }
}
```

Startup:

```text
Constructor

PostConstruct

AfterPropertiesSet
```

Shutdown:

```text
PreDestroy
```

---

# Internal Execution Explanation

Spring internally performs:

```text
instantiateBean()

populateBean()

applyBeanPostProcessorsBeforeInitialization()

invokeInitMethods()

applyBeanPostProcessorsAfterInitialization()
```

Core lifecycle pipeline.

---

# Advanced Mental Model

A Bean behaves like:

```text
Managed Process
```

not

```text
Passive Object
```

Spring continuously supervises it.

Exactly like an operating system supervises a process.

---

# Interview Questions

## What is Bean Lifecycle?

The sequence of phases a bean passes through from creation to destruction under Spring management.

---

## Constructor vs PostConstruct?

Constructor:

Object creation.

PostConstruct:

Runs after dependency injection.

---

## Purpose of BeanPostProcessor?

Provides lifecycle extension points and enables features like AOP and transaction proxies.

---

## What runs first?

```text
Constructor

Dependency Injection

PostConstruct
```

---

## What happens if PostConstruct fails?

Bean creation fails and application startup may fail.

---

## When is PreDestroy called?

During graceful container shutdown.

---

# Cheat Sheet

```text
Bean Lifecycle

Instantiate
    ↓
Inject Dependencies
    ↓
Aware Interfaces
    ↓
BeforeInit BPP
    ↓
@PostConstruct
    ↓
afterPropertiesSet
    ↓
Custom Init
    ↓
AfterInit BPP
    ↓
Ready
    ↓
@PreDestroy
    ↓
Destroy
```

Important:

Constructor ≠ Ready

@PostConstruct = Initialization

@PreDestroy = Cleanup

BeanPostProcessor =
Lifecycle Extension Point

---

# One Picture To Remember

```text
                 SPRING HR DEPARTMENT

                        |
                        v

                   HIRE EMPLOYEE

                        |
                        v

                 GIVE DEPENDENCIES

                        |
                        v

                    TRAINING

                (@PostConstruct)

                        |
                        v

                  ACTIVE EMPLOYEE

                      (READY)

                        |
                        v

                     RETIRES

                  (@PreDestroy)
```

If you remember one sentence:

"Spring raises every bean from birth, prepares it for work, supervises it while running, and cleans it up before death."
