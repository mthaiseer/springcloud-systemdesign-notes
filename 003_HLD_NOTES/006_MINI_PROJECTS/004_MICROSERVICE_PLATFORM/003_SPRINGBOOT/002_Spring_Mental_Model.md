# 002_Spring_Mental_Model.md

# Spring Mental Model
## One Core Idea: Spring is an Object Factory + Dependency Graph Manager

---

# Why This Chapter Exists

Most developers learn Spring as:

- @Component
- @Service
- @Repository
- @Autowired
- @Bean
- @Configuration

and memorize annotations.

That works until production fails.

This chapter teaches only ONE mental model:

> Spring is a giant factory that creates, wires, manages and supervises application objects.

Everything else is built on top of that idea.

---

# Mental Model

Imagine building an e-commerce system.

Without Spring:

You create every object manually.

```java
Database db = new Database();
UserRepository repo = new UserRepository(db);
UserService service = new UserService(repo);
```

Now imagine 500 classes.

Impossible to maintain.

Spring says:

"Tell me WHAT objects exist.
I will decide HOW to build them."

---

# The Factory Model

```text
                SPRING CONTAINER

      +--------------------------------+
      |                                |
      |  UserController                |
      |  UserService                   |
      |  UserRepository                |
      |  DataSource                    |
      |  KafkaProducer                 |
      |  RedisClient                   |
      |                                |
      +--------------------------------+

          Creates
          Wires
          Stores
          Manages
```

This container is called:

ApplicationContext

---

# The Real Internal Model

Spring does NOT manage code.

Spring manages OBJECTS.

```text
Source Code
    |
    v
Class Definitions
    |
    v
Spring Scanning
    |
    v
Bean Definitions
    |
    v
Object Creation
    |
    v
Dependency Injection
    |
    v
Ready Application
```

The most important thing:

Spring works with Bean Definitions first.

Actual objects come later.

---

# Bean Definition Mental Model

A bean definition is a blueprint.

```text
Bean Definition

Name:
userService

Type:
UserService

Dependencies:
userRepository
```

Think:

Architect drawing.

Not actual building.

---

# Startup Dry Run

Application starts.

```text
java -jar app.jar
        |
        v
Spring Boot Starts
        |
        v
Creates ApplicationContext
        |
        v
Scans Packages
        |
        v
Finds Components
        |
        v
Builds Bean Definitions
        |
        v
Creates Objects
        |
        v
Injects Dependencies
        |
        v
Application Ready
```

This single diagram explains most startup behavior.

---

# Dependency Graph

Suppose:

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
Database
```

Spring builds a graph.

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

Spring resolves from bottom to top.

Create DataSource first.

Then Repository.

Then Service.

Then Controller.

---

# Warehouse Analogy

Think Amazon warehouse.

```text
Request Item
      |
      v
Warehouse Finds Item
      |
      v
Returns Ready Product
```

Spring Container:

```text
Request Bean
      |
      v
Container Finds Bean
      |
      v
Returns Ready Object
```

Exactly same idea.

---

# Internal Creation Flow

```text
ApplicationContext
        |
        v
BeanFactory
        |
        v
Bean Definition
        |
        v
Constructor
        |
        v
Dependency Injection
        |
        v
Initialization
        |
        v
Bean Ready
```

This flow repeats thousands of times.

---

# Constructor Injection Mental Model

Example:

```java
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```

Spring sees:

```text
Need UserService

UserService requires:

UserRepository
```

Container searches.

```text
UserRepository Found
       |
       v
Create UserService
```

Dependency solved.

---

# What Spring Really Stores

Not classes.

Objects.

```text
Bean Name
      |
      +----> Object Reference
```

Like:

```text
userService
      |
      +----> 0xABC123

userRepository
      |
      +----> 0xDEF456
```

Container acts like a giant map.

---

# Production Story

A fintech service had:

```text
300+ Beans
40+ Services
12 Databases
```

Startup suddenly failed.

Error:

```text
NoSuchBeanDefinitionException
```

Why?

Developer removed:

```java
@Repository
```

Spring could no longer create repository bean.

Entire dependency chain collapsed.

```text
Repository Missing
      |
      v
Service Missing
      |
      v
Controller Missing
      |
      v
Startup Failed
```

Understanding dependency graph immediately reveals the issue.

---

# Circular Dependency Failure

Example:

```text
ServiceA -> ServiceB
ServiceB -> ServiceA
```

Graph:

```text
A
|
v
B
^
|
+----
```

Spring cannot determine creation order.

Result:

```text
BeanCurrentlyInCreationException
```

Debugging mindset:

Always draw dependency graph.

---

# Debugging Startup Failures

Question:

Which bean failed?

Never start from top error.

Start from root cause.

```text
Controller Failed

Ignore

Service Failed

Ignore

Repository Failed

Ignore

DataSource Failed

FOUND ROOT CAUSE
```

Work bottom-up.

---

# Request Flow After Startup

Startup complete.

```text
Client Request
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
Database
```

Notice:

Spring does NOT create new objects every request.

Objects already exist.

Container reuses them.

---

# Singleton Mental Model

Default scope:

```text
Singleton
```

Meaning:

```text
One Container
      |
      +---- One UserService
      |
      +---- One Repository
      |
      +---- One Controller
```

10,000 requests.

Same object.

Huge memory savings.

---

# Production Failure

Bad code:

```java
@Service
public class UserService {

    private List<String> cache = new ArrayList<>();
}
```

Singleton means:

All requests share same cache.

Can create race conditions.

Lesson:

Understand bean scope.

---

# Java Example

```java
@Repository
public class UserRepository {
}

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}

@RestController
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }
}
```

Spring internally creates:

```text
UserRepository
      |
      v
UserService
      |
      v
UserController
```

Dependency graph complete.

---

# Interview Answer

Question:

What is Spring?

Strong answer:

"Spring is primarily an IoC container that manages object creation, dependency resolution, lifecycle management and configuration. Internally it builds bean definitions, creates an object graph, injects dependencies and stores managed objects inside the ApplicationContext."

---

# Interview Answer

Question:

What is ApplicationContext?

Answer:

"ApplicationContext is Spring's container. It stores bean definitions, creates beans, injects dependencies, manages lifecycle events and provides access to managed objects."

---

# Interview Answer

Question:

What is a Bean?

Answer:

"A bean is simply an object whose lifecycle is managed by the Spring container."

---

# One Picture To Remember

```text
                    SPRING

            APPLICATION CONTEXT

    +----------------------------------+
    |                                  |
    |  Bean Definitions                |
    |          |                       |
    |          v                       |
    |  Object Creation                 |
    |          |                       |
    |          v                       |
    |  Dependency Injection            |
    |          |                       |
    |          v                       |
    |  Managed Object Graph            |
    |                                  |
    +----------------------------------+

 Controller
      |
      v
 Service
      |
      v
 Repository
      |
      v
 Database
```

Memorize nothing.

Remember only:

> Spring is a factory that builds and manages a dependency graph of application objects.

Everything else in Spring Boot grows from this single idea.
