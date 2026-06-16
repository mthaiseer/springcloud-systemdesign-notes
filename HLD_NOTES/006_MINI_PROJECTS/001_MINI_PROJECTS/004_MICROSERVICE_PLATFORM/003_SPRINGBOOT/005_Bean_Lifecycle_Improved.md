# 005_Bean_Lifecycle.md

# Bean Lifecycle
## Mental Model: Spring's Assembly Line That Turns a Raw Object Into a Production-Ready Bean

---

# Why This Exists

A Java object can be created with:

```java
new UserService();
```

But a production application needs much more:

- Dependencies injected
- Configuration validated
- Resources initialized
- Transactions enabled
- Proxies created
- Resources cleaned up

A raw object is not enough.

Spring therefore manages a complete lifecycle.

---

# The Problem

Imagine:

```java
@Service
class PaymentService {
}
```

Questions:

```text
Who creates it?

Who injects dependencies?

Who validates configuration?

Who creates transaction proxies?

Who cleans resources during shutdown?
```

Without a lifecycle manager:

```text
Chaos
```

---

# Core Mental Model

A Bean Lifecycle is an Assembly Line.

Raw Material:

```text
Java Class
```

Final Product:

```text
Production Ready Bean
```

Spring acts like a factory.

---

# Real World Analogy

Car Manufacturing Plant

```text
Raw Metal
    |
    v
Chassis
    |
    v
Engine Installed
    |
    v
Quality Check
    |
    v
Paint
    |
    v
Electronics
    |
    v
Ready Car
```

Spring Bean:

```text
Class
    |
    v
Instantiation
    |
    v
Dependency Injection
    |
    v
Validation
    |
    v
Enhancement
    |
    v
Proxy Wrapping
    |
    v
Ready Bean
```

---

# One Picture To Remember

```text
RAW OBJECT

      |
      v

+------------------+
| Instantiation    |
+------------------+

      |
      v

+------------------+
| Dependency       |
| Injection        |
+------------------+

      |
      v

+------------------+
| Initialization   |
+------------------+

      |
      v

+------------------+
| Enhancement      |
| Proxy Creation   |
+------------------+

      |
      v

PRODUCTION READY BEAN
```

---

# Complete Lifecycle Flow

```text
Bean Definition

      |
      v

Instantiation

      |
      v

Dependency Injection

      |
      v

Aware Interfaces

      |
      v

BeanPostProcessor
Before Initialization

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

BeanPostProcessor
After Initialization

      |
      v

Proxy Creation

      |
      v

READY

      |
      v

@PreDestroy

      |
      v

Destroy Method
```

---

# Internal Spring Source Flow

Most developers see:

```java
@Service
class UserService {}
```

Internally Spring executes:

```text
createBean()

      |
      v

doCreateBean()

      |
      v

createBeanInstance()

      |
      v

populateBean()

      |
      v

initializeBean()

      |
      v

applyBeanPostProcessors()

      |
      v

Return Bean
```

Understanding this explains half of Spring.

---

# Phase 1: Instantiation

Spring creates object.

```java
new UserService()
```

State:

```text
Object Exists

Dependencies Not Injected
```

---

# Dry Run #1

```java
@Service
class UserService {

    UserService() {
        System.out.println("Constructor");
    }
}
```

Output:

```text
Constructor
```

Only object creation happened.

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

Need Repository

Create Repository

Inject Repository

Create Service
```

---

# Dependency Graph Example

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
```

Spring resolves bottom-up.

---

# Phase 3: Aware Interfaces

Spring can inject container information.

```java
ApplicationContextAware
BeanNameAware
EnvironmentAware
```

Flow:

```text
Bean Created

Container Metadata Injected
```

---

# Phase 4: Before Initialization

Spring executes:

```text
BeanPostProcessor
Before Initialization
```

Think:

```text
Inspection Stage
```

Factory checks product before shipping.

---

# Phase 5: PostConstruct

```java
@PostConstruct
public void init() {
}
```

Runs after:

```text
Constructor

Dependency Injection
```

Typical uses:

```text
Cache Warmup

Validation

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
        System.out.println("Init");
    }
}
```

Output:

```text
Constructor

Init
```

---

# Phase 6: afterPropertiesSet()

```java
implements InitializingBean
```

Spring invokes:

```java
afterPropertiesSet()
```

after dependency injection.

---

# Phase 7: Custom Init

```java
@Bean(initMethod="start")
```

Order:

```text
@PostConstruct

↓

afterPropertiesSet()

↓

Custom Init
```

---

# Phase 8: BeanPostProcessor After Initialization

Most important phase.

```text
postProcessAfterInitialization()
```

This is where Spring can enhance beans.

---

# Proxy Creation Happens Here

Remember this forever:

```text
Proxy Creation

Happens AFTER Initialization
```

Diagram:

```text
Constructor
      |
Injection
      |
@PostConstruct
      |
AfterInit BPP
      |
Proxy Creation
      |
READY
```

This explains many Spring bugs.

---

# MOST IMPORTANT DRY RUN

```java
@Service
@Transactional
public class PaymentService {

    @PostConstruct
    public void init() {
        transfer();
    }

    public void transfer() {
        System.out.println("Transfer");
    }
}
```

Developers expect:

```text
Transaction Active
```

Reality:

```text
No Transaction
```

Why?

---

# Internal Execution

```text
Create Bean

Inject Dependencies

@PostConstruct Runs

transfer()

NO PROXY

NO TRANSACTION

AfterInit BPP

Create Transaction Proxy

READY
```

Key lesson:

```text
@PostConstruct executes
before transactional proxy exists.
```

This is a favorite senior interview question.

---

# Production Scale Example

Payment System Startup

```text
400 Beans

Repositories
Services
Controllers
Clients
Schedulers
```

Each bean goes through:

```text
Instantiate
Inject
Initialize
Enhance
Proxy
Ready
```

Application becomes healthy only after all beans finish lifecycle.

---

# Production Failure Story

Real incident:

```java
@PostConstruct
void init() {

    Thread.sleep(60000);
}
```

Startup:

```text
Bean Waiting

Context Waiting

Application Waiting
```

Kubernetes:

```text
Readiness Probe Failed

Pod Restarted
```

Root cause:

Heavy initialization.

---

# Failure Tree

```text
Bean Creation Failed

      |
      +--- Constructor Failure

      |
      +--- Dependency Failure

      |
      +--- PostConstruct Failure

      |
      +--- Proxy Creation Failure

      |
      +--- Circular Dependency
```

---

# Production Failure Example

```java
@PostConstruct
void init() {

    datasource.getConnection();
}
```

Database Down.

Flow:

```text
Create Bean

PostConstruct

Database Connection

Exception

Startup Failed
```

Application never becomes ready.

---

# Senior Debugging Mindset

Don't ask:

```text
Which bean failed?
```

Ask:

```text
Which lifecycle stage failed?
```

```text
Instantiation?

Injection?

Initialization?

Proxy Creation?

Shutdown?
```

Much faster debugging.

---

# Reading Startup Logs

```text
Creating shared instance

Populating bean

Invoking init method

Finished bean creation
```

Translation:

```text
Instantiation

Injection

Initialization

Ready
```

---

# Shutdown Lifecycle

Startup gets attention.

Shutdown is equally important.

Flow:

```text
Running

      |
      v

@PreDestroy

      |
      v

destroy()

      |
      v

Resource Cleanup
```

---

# Example

```java
@Service
class KafkaConsumerManager {

    @PreDestroy
    void shutdown() {

        consumer.close();
    }
}
```

Without cleanup:

```text
Resource Leaks
```

---

# Dry Run #3

Startup:

```text
Constructor

Injection

PostConstruct

Proxy Creation

Ready
```

Shutdown:

```text
PreDestroy

Destroy
```

Complete lifecycle.

---

# Common Misconceptions

## Constructor Means Ready

Wrong.

Dependencies may not be fully prepared.

---

## PostConstruct Runs Before Injection

Wrong.

It runs after injection.

---

## Transactional Works Everywhere

Wrong.

Not during PostConstruct.

Proxy does not exist yet.

---

## Destroy Always Executes

Wrong.

```text
kill -9
```

prevents graceful shutdown.

---

# Full Java Example

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
        System.out.println("afterPropertiesSet");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("PreDestroy");
    }
}
```

Output:

```text
Constructor

PostConstruct

afterPropertiesSet

Application Running

PreDestroy
```

---

# Internal Execution Explanation

Spring internally performs:

```text
instantiateBean()

      ↓

populateBean()

      ↓

initializeBean()

      ↓

applyBeanPostProcessorsBeforeInitialization()

      ↓

@PostConstruct

      ↓

afterPropertiesSet()

      ↓

applyBeanPostProcessorsAfterInitialization()

      ↓

Create Proxy

      ↓

Return Bean
```

---

# Interview Questions

## What Is Bean Lifecycle?

Complete journey of a bean from creation to destruction.

---

## Constructor vs PostConstruct?

Constructor:

Object creation.

PostConstruct:

Runs after dependency injection.

---

## Why Does Transactional Fail Inside PostConstruct?

Proxy has not been created yet.

---

## What Does BeanPostProcessor Do?

Provides extension points before and after initialization.

Used heavily by AOP.

---

## When Is Proxy Created?

After initialization phase.

---

## Why Is PreDestroy Important?

Releases resources safely.

---

# Cheat Sheet

```text
Bean Lifecycle

Instantiate
    ↓
Inject
    ↓
Aware
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
    ↓
@PreDestroy
    ↓
Destroy
```

Remember:

```text
@PostConstruct

BEFORE Proxy

@Transactional

AFTER Proxy
```

---

# Final Picture To Remember

```text
SPRING FACTORY

Raw Java Object

       |
       v

Instantiation

       |
       v

Dependency Installation

       |
       v

Inspection

       |
       v

@PostConstruct

       |
       v

Enhancement

       |
       v

Proxy Wrapping

       |
       v

Production Ready Bean

       |
       v

@PreDestroy

       |
       v

Destroyed
```

If you remember one sentence:

Spring transforms a raw Java object into a production-ready managed component through a carefully controlled lifecycle.
