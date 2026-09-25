# CH 04 --- Spring Core

## Senior Java Distributed Backend Interview Series

**Target:** \~35 interview questions\
**Goal:** Understand Spring's core container deeply enough to explain
dependency injection, bean creation, lifecycle, scopes, proxies, AOP,
circular dependencies, configuration, and common production/interview
traps.

------------------------------------------------------------------------

# Chapter Map

``` text
CH 04 — SPRING CORE (~35 Q)
|
+-- 1. IoC / DI Fundamentals (8)
|   +-- Spring / IoC
|   +-- DI
|   +-- BeanFactory / ApplicationContext
|   +-- Constructor / setter / field injection
|   +-- Component scanning
|   +-- Stereotype annotations
|   +-- @Bean / @Configuration
|   +-- Dependency resolution
|
+-- 2. Bean Lifecycle / Scopes (8)
|   +-- Bean creation lifecycle
|   +-- BeanPostProcessor
|   +-- @PostConstruct / @PreDestroy
|   +-- InitializingBean / DisposableBean
|   +-- Singleton
|   +-- Prototype
|   +-- Web scopes
|   +-- Scope mismatch
|
+-- 3. Configuration / Container Internals (8)
|   +-- @Primary / @Qualifier
|   +-- Lazy initialization
|   +-- Profiles
|   +-- Properties / Environment
|   +-- Circular dependencies
|   +-- FactoryBean
|   +-- Application events
|   +-- Spring startup flow
|
+-- 4. AOP / Proxy Internals (11)
    +-- AOP terminology
    +-- Spring proxy model
    +-- JDK proxy vs class-based proxy
    +-- Advice types
    +-- Pointcuts
    +-- Proxy invocation flow
    +-- Self-invocation problem
    +-- final/private method limitations
    +-- AOP use cases
    +-- Ordering
    +-- Production proxy debugging
```

------------------------------------------------------------------------

# Big Picture --- What Spring Core Actually Does

``` text
                  APPLICATION START
                         |
                         v
                 Spring Container
                         |
          +--------------+--------------+
          |              |              |
          v              v              v
     Read Config     Discover Beans   Build Metadata
          |              |              |
          +--------------+--------------+
                         |
                         v
                Resolve Dependencies
                         |
                         v
                   Create Beans
                         |
                         v
               BeanPostProcessors
                         |
                         v
              Wrap Some Beans in Proxy
                         |
                         v
                Application Ready
```

The key mental model:

``` text
YOU write:
@Service
class OrderService { ... }

SPRING does:
discover
   ↓
instantiate
   ↓
inject dependencies
   ↓
initialize
   ↓
possibly create proxy
   ↓
manage lifecycle
```

------------------------------------------------------------------------

# 1. IoC / Dependency Injection --- 8 Questions

## Q1. What is Spring IoC?

IoC = **Inversion of Control**.

Without a container:

``` java
class OrderService {
    private final PaymentService paymentService =
        new PaymentService();
}
```

`OrderService` controls dependency creation.

With Spring:

``` java
@Service
class OrderService {
    private final PaymentService paymentService;

    OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

``` text
Traditional
OrderService
    |
    +--> creates PaymentService

IoC
Spring Container
    |
    +--> creates PaymentService
    |
    +--> creates OrderService
             |
             +--> injects PaymentService
```

**Senior answer:** IoC is broader than DI. Dependency Injection is the
primary mechanism Spring uses to implement IoC.

------------------------------------------------------------------------

## Q2. What is Dependency Injection?

A class receives its dependencies instead of constructing them itself.

``` text
OrderController
      |
      v
 OrderService
      |
      v
PaymentGateway
```

Spring constructs this object graph.

Benefits:

-   lower coupling
-   easier testing
-   replaceable implementations
-   centralized lifecycle/configuration
-   clearer dependencies

------------------------------------------------------------------------

## Q3. BeanFactory vs ApplicationContext?

``` text
BeanFactory
   |
   +-- fundamental IoC container
   +-- bean creation / lookup

ApplicationContext
   |
   +-- BeanFactory capabilities
   +-- events
   +-- internationalization
   +-- resource loading
   +-- environment/profiles
   +-- enterprise integration
```

Most Spring applications use `ApplicationContext`.

### Senior point

Do not describe `ApplicationContext` merely as "an advanced
BeanFactory." Explain that it is the normal application-level container
abstraction exposing additional framework services.

------------------------------------------------------------------------

## Q4. Constructor vs setter vs field injection?

### Constructor injection

``` java
@Service
class OrderService {
    private final PaymentService paymentService;

    OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

Preferred for required dependencies.

``` text
Advantages
|
+-- dependency is explicit
+-- object cannot exist without required dependency
+-- supports final fields
+-- easy unit testing
+-- exposes excessive dependencies via constructor size
```

### Setter injection

Useful for genuinely optional/configurable dependencies.

### Field injection

``` java
@Autowired
private PaymentService paymentService;
```

Usually discouraged because dependencies are hidden and plain unit
construction is harder.

------------------------------------------------------------------------

## Q5. How does component scanning work?

``` java
@SpringBootApplication
public class App { }
```

Conceptually:

``` text
Base package
    |
    v
Scan classes
    |
    +-- @Component
    +-- @Service
    +-- @Repository
    +-- @Controller
    +-- @Configuration
            |
            v
      Bean definitions
            |
            v
      Spring container
```

A common production issue is placing components outside the scan
hierarchy.

------------------------------------------------------------------------

## Q6. `@Component`, `@Service`, `@Repository`, `@Controller`?

They are stereotype annotations.

``` text
@Component
    |
    +-- @Service
    +-- @Repository
    +-- @Controller
```

Semantically:

``` text
@Service      -> service/business layer
@Repository   -> persistence layer
@Controller   -> MVC controller
@Component    -> generic component
```

`@Repository` also participates in persistence exception translation
infrastructure.

------------------------------------------------------------------------

## Q7. `@Bean` vs component scanning?

Component scanning:

``` java
@Service
class PaymentService {}
```

`@Bean`:

``` java
@Configuration
class PaymentConfig {

    @Bean
    PaymentClient paymentClient() {
        return new PaymentClient();
    }
}
```

Use `@Bean` especially when:

-   configuring third-party classes
-   construction requires explicit logic
-   you want configuration-centric bean definitions

``` text
@Component
class itself declares:
"I am a Spring component"

@Bean
configuration declares:
"Spring, manage this returned object"
```

------------------------------------------------------------------------

## Q8. How does Spring resolve a dependency?

Suppose:

``` java
interface PaymentGateway {}

@Component
class StripeGateway implements PaymentGateway {}

@Component
class BankGateway implements PaymentGateway {}
```

Injection:

``` java
OrderService(PaymentGateway gateway)
```

Spring sees multiple candidates.

``` text
Required type: PaymentGateway
          |
          v
Find candidates
          |
    +-----+-----+
    |           |
 Stripe       Bank
    |
AMBIGUOUS
```

Resolve using mechanisms such as:

``` java
@Primary
```

or

``` java
@Qualifier("stripeGateway")
```

------------------------------------------------------------------------

# 2. Bean Lifecycle / Scopes --- 8 Questions

## Q9. Explain the Spring bean lifecycle.

High-level lifecycle:

``` text
Bean Definition
      |
      v
Instantiate Bean
      |
      v
Populate Dependencies
      |
      v
Aware callbacks
      |
      v
BeanPostProcessor
before initialization
      |
      v
@PostConstruct /
initialization callbacks
      |
      v
BeanPostProcessor
after initialization
      |
      v
READY BEAN
      |
      v
Application shutdown
      |
      v
@PreDestroy /
destroy callbacks
```

### Critical senior point

Proxy creation commonly happens through bean post-processing.

------------------------------------------------------------------------

## Q10. What is BeanPostProcessor?

It allows custom logic around bean initialization.

``` java
public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(
        Object bean, String beanName);

    Object postProcessAfterInitialization(
        Object bean, String beanName);
}
```

Mental model:

``` text
Raw Bean
   |
   v
BPP before
   |
   v
Initialization
   |
   v
BPP after
   |
   +--> same bean
   |
   +--> wrapped/proxied bean
```

This extension point is fundamental to many Spring features.

------------------------------------------------------------------------

## Q11. `@PostConstruct` and `@PreDestroy`?

``` java
@Component
class CacheLoader {

    @PostConstruct
    void load() {
        // initialize
    }

    @PreDestroy
    void cleanup() {
        // release resources
    }
}
```

``` text
construct
   ↓
inject
   ↓
@PostConstruct
   ↓
serve requests
   ↓
shutdown
   ↓
@PreDestroy
```

Avoid heavy initialization that makes startup unpredictable when a
better lifecycle mechanism is available.

------------------------------------------------------------------------

## Q12. InitializingBean / DisposableBean vs annotations?

``` java
class Service implements InitializingBean {
    @Override
    public void afterPropertiesSet() {}
}
```

Direct Spring interfaces couple the class to Spring.

Annotation/callback options are generally cleaner for application
components.

`@Bean` can also specify lifecycle methods.

``` java
@Bean(initMethod = "start", destroyMethod = "stop")
Client client() { ... }
```

------------------------------------------------------------------------

## Q13. What is singleton scope in Spring?

Default scope:

``` java
@Service
class OrderService {}
```

One bean instance per Spring container/bean definition context.

``` text
ApplicationContext
      |
      +--> OrderService instance
             ^
             |
      +------+------+
      |             |
 Controller A   Controller B
```

### Critical trap

Spring singleton != JVM-wide singleton.

Multiple application contexts can have separate instances.

### Concurrency trap

Singleton beans are shared by many request threads.

Therefore:

``` java
@Service
class BadService {
    private String currentUser; // dangerous mutable request state
}
```

is unsafe.

Prefer stateless singleton services.

------------------------------------------------------------------------

## Q14. What is prototype scope?

``` java
@Scope("prototype")
@Component
class TaskContext {}
```

Spring creates a new instance each time the bean is requested from the
container.

``` text
request bean -> instance A
request bean -> instance B
request bean -> instance C
```

### Senior trap

Spring does not manage the complete destruction lifecycle of prototype
beans in the same way it manages singleton destruction.

------------------------------------------------------------------------

## Q15. What are request/session scopes?

In web applications:

``` text
request scope
    |
one bean instance per HTTP request

session scope
    |
one bean instance per HTTP session
```

Example:

``` java
@RequestScope
@Component
class RequestMetadata {}
```

These scopes require web-context support.

------------------------------------------------------------------------

## Q16. What is the scope mismatch problem?

Suppose:

``` text
Singleton Service
      |
      v
Request-scoped Bean
```

Singleton is created once, but request bean must change per request.

Spring can solve this through a scoped proxy/provider-style lookup.

``` text
Singleton
   |
   v
Scoped Proxy
   |
   +--> Request A instance
   |
   +--> Request B instance
```

This is another example of why understanding proxies matters.

------------------------------------------------------------------------

# 3. Configuration / Container Internals --- 8 Questions

## Q17. `@Primary` vs `@Qualifier`?

``` java
@Primary
@Component
class DefaultPaymentGateway
        implements PaymentGateway {}
```

`@Primary` gives a preferred candidate.

``` java
OrderService(
    @Qualifier("bankGateway")
    PaymentGateway gateway
)
```

`@Qualifier` narrows selection explicitly.

Mental model:

``` text
multiple candidates
       |
       +--> qualifier specified?
       |        |
       |       YES -> matching candidate
       |
       +--> otherwise primary?
                |
               YES -> primary
```

------------------------------------------------------------------------

## Q18. What is lazy initialization?

``` java
@Lazy
@Component
class ExpensiveClient {}
```

Instead of:

``` text
Application startup
      |
create bean immediately
```

lazy behavior:

``` text
Application startup
      |
do not create bean yet
      |
first use
      |
create bean
```

Trade-off:

``` text
faster startup
      vs
failure/initialization cost may move to runtime
```

------------------------------------------------------------------------

## Q19. What are Spring profiles?

``` java
@Profile("prod")
@Bean
DataSource prodDataSource() { ... }
```

``` text
Environment
|
+-- local
+-- test
+-- prod
```

Profiles conditionally activate beans/configuration.

### Senior warning

Do not let profile combinations become an uncontrolled configuration
matrix.

------------------------------------------------------------------------

## Q20. How does Spring externalized configuration work conceptually?

Sources can include:

``` text
application properties/YAML
environment variables
system properties
command-line values
external config systems
```

Typical binding:

``` java
@ConfigurationProperties(prefix = "payment")
public record PaymentProperties(
    URI endpoint,
    Duration timeout
) {}
```

Prefer type-safe configuration for nontrivial groups of settings.

------------------------------------------------------------------------

## Q21. What is a circular dependency?

``` text
Service A
   |
   v
Service B
   |
   v
Service A
```

Constructor example:

``` java
class A {
    A(B b) {}
}

class B {
    B(A a) {}
}
```

Spring cannot simply construct either first.

### Senior answer

Circular dependencies often indicate poor responsibility boundaries.

Better:

``` text
Before
A <----> B

Refactor
A ---> SharedCoordinator <--- B
```

Do not treat workarounds as the first architectural solution.

------------------------------------------------------------------------

## Q22. What is `FactoryBean`?

A `FactoryBean<T>` is itself a Spring-managed factory whose purpose is
to create another object.

``` text
Container
   |
   +--> FactoryBean
           |
           +--> creates Product Object
```

This is different from a regular bean factory method annotated with
`@Bean`.

Important conceptual distinction:

``` text
getBean("x")
   -> product created by FactoryBean

getBean("&x")
   -> FactoryBean itself
```

------------------------------------------------------------------------

## Q23. What are Spring application events?

Publisher:

``` java
publisher.publishEvent(
    new OrderCreatedEvent(orderId)
);
```

Listener:

``` java
@EventListener
void handle(OrderCreatedEvent event) {
    ...
}
```

``` text
Publisher
   |
   v
ApplicationEventPublisher
   |
   +--> Listener A
   +--> Listener B
```

### Senior warning

In-process Spring events are not a replacement for Kafka or another
durable distributed messaging system.

``` text
Spring Event
|
+-- same application/process
+-- typically no durable broker semantics

Kafka
|
+-- distributed
+-- durable log
+-- cross-service communication
```

------------------------------------------------------------------------

## Q24. What happens during Spring application startup?

Simplified:

``` text
main()
  |
  v
Create ApplicationContext
  |
  v
Load configuration
  |
  v
Register bean definitions
  |
  v
Invoke container post-processors
  |
  v
Register BeanPostProcessors
  |
  v
Instantiate non-lazy singletons
  |
  v
Inject dependencies
  |
  v
Initialize / proxy beans
  |
  v
Publish lifecycle events
  |
  v
Application ready
```

This flow helps diagnose startup failures and proxy/lifecycle issues.

------------------------------------------------------------------------

# 4. AOP / Proxy Internals --- 11 Questions

## Q25. What is AOP?

AOP = Aspect-Oriented Programming.

It separates cross-cutting concerns from core business logic.

``` text
                 Business Method
                       |
          +------------+------------+
          |            |            |
       Logging     Transaction    Security
          |            |            |
          +------------+------------+
                       |
                       v
                 actual method
```

Common cross-cutting concerns:

-   transactions
-   security
-   metrics
-   logging
-   tracing
-   caching

------------------------------------------------------------------------

## Q26. Explain AOP terminology.

``` text
Aspect
  |
  +-- cross-cutting concern

Advice
  |
  +-- code executed

Pointcut
  |
  +-- which join points/methods?

Join Point
  |
  +-- execution point

Proxy
  |
  +-- wrapper/interceptor object
```

Example:

``` text
Pointcut: all methods annotated @Audited
Advice: record execution time
```

------------------------------------------------------------------------

## Q27. How does Spring AOP generally work?

Suppose:

``` java
@Service
class PaymentService {
    @Transactional
    public void pay() {}
}
```

Conceptually:

``` text
Caller
  |
  v
Spring Proxy
  |
  +--> transaction interceptor
  |
  +--> other interceptors
  |
  v
Target PaymentService
  |
  v
pay()
```

The caller normally holds the proxy reference rather than directly
invoking the raw target.

------------------------------------------------------------------------

## Q28. JDK dynamic proxy vs class-based proxy?

Conceptually:

``` text
JDK Dynamic Proxy
      |
interfaces
      |
Proxy implements interface
```

``` text
Class-based Proxy
      |
subclasses target class
      |
intercepts overridable methods
```

### Interview answer

Proxy choice and exact defaults can depend on configuration/framework
version. The important architectural point is that proxy-based
interception has method/visibility/self-invocation constraints.

------------------------------------------------------------------------

## Q29. What types of advice exist conceptually?

``` text
Before
  |
Target Method
  |
After

Around
  |
  +--> before
  +--> invoke target
  +--> after
```

Common AspectJ-style annotations:

``` java
@Before(...)
@After(...)
@AfterReturning(...)
@AfterThrowing(...)
@Around(...)
```

`@Around` is powerful because it controls whether/when the target
invocation proceeds.

------------------------------------------------------------------------

## Q30. What is a pointcut?

A pointcut determines where advice applies.

Example:

``` java
@Around("execution(* com.example.service..*(..))")
```

Conceptually:

``` text
All methods
    |
    v
Pointcut expression
    |
    +--> matches OrderService.place()
    +--> matches PaymentService.pay()
    +--> excludes Controller
```

Keep pointcuts understandable. Overly broad aspects can create
surprising behavior.

------------------------------------------------------------------------

## Q31. Explain the proxy invocation chain.

``` text
HTTP Controller
      |
      v
OrderService Proxy
      |
      +--> Security?
      |
      +--> Transaction?
      |
      +--> Metrics?
      |
      +--> Cache?
      |
      v
OrderService Target
      |
      v
repository.save()
```

A method call can therefore involve much more than the visible Java
method body.

This is essential for debugging senior Spring applications.

------------------------------------------------------------------------

## Q32. What is the self-invocation problem?

``` java
@Service
class PaymentService {

    public void checkout() {
        savePayment(); // direct call on this
    }

    @Transactional
    public void savePayment() {
        ...
    }
}
```

External call:

``` text
Controller
   |
   v
Proxy
   |
   v
checkout()
```

Internal call:

``` text
checkout()
   |
   v
this.savePayment()
   |
   X does not re-enter external proxy
```

Therefore proxy-based advice on `savePayment()` may not execute as
expected.

### Better design

Move transactional responsibility to an appropriate separate
bean/boundary when it reflects the domain/use-case boundary.

This becomes especially important in CH 06 Transactions.

------------------------------------------------------------------------

## Q33. Why can final/private methods be problematic with proxy-based AOP?

Class-based interception relies on overriding/interceptable methods.

``` text
Proxy subclass
    |
    +--> override method()
              |
              +--> intercept
```

A `final` method cannot be overridden.

Private methods are not externally overridable entry points.

The exact behavior depends on proxy mechanism, but the senior takeaway
is:

**Do not design Spring AOP assuming every internal/private/final method
can be transparently intercepted.**

------------------------------------------------------------------------

## Q34. What are good and bad AOP use cases?

Good:

``` text
Transactions
Security
Metrics
Tracing
Auditing
Caching
```

Potentially bad:

``` text
Complex business workflow hidden in aspects
       |
       v
Control flow becomes invisible
       |
       v
Hard debugging / surprising behavior
```

Rule:

``` text
Cross-cutting infrastructure -> AOP can fit

Core domain behavior -> explicit code usually clearer
```

------------------------------------------------------------------------

## Q35. How would you debug "Spring annotation is present but does nothing"?

This is a high-value senior interview scenario.

``` text
Annotation not working
       |
       v
Is object Spring-managed?
       |
       +-- NO -> no Spring proxy/infrastructure
       |
       v
Is call going through proxy?
       |
       +-- self invocation?
       |
       v
Is method interceptable?
       |
       +-- private/final?
       |
       v
Correct annotation/configuration?
       |
       v
Correct bean selected?
       |
       v
Proxy type / advisor order?
       |
       v
Check logs / bean metadata / runtime class
```

Useful debugging:

``` java
System.out.println(bean.getClass());
```

You may discover a generated proxy rather than the original class.

------------------------------------------------------------------------

# Senior Scenario 1 --- Stateful Singleton Bug

``` java
@Service
class UserService {
    private String currentUser;

    String process(String user) {
        currentUser = user;
        doWork();
        return currentUser;
    }
}
```

Two requests:

``` text
Request A                   Request B
---------                   ---------
currentUser = Alice
                            currentUser = Bob
doWork()
return currentUser
      |
      v
could return Bob
```

Why?

``` text
Spring singleton
      |
one shared instance
      |
many request threads
      |
mutable field
      |
RACE CONDITION
```

Better:

``` java
String process(String user) {
    String currentUser = user;
    doWork();
    return currentUser;
}
```

Keep request state local where possible.

------------------------------------------------------------------------

# Senior Scenario 2 --- Circular Dependency

``` text
OrderService
    |
    v
PaymentService
    |
    v
OrderService
```

Bad response:

> "Use lazy injection and continue."

Senior response:

``` text
First ask WHY cycle exists
        |
        v
Are responsibilities mixed?
        |
        v
Extract orchestration/domain service
```

Possible redesign:

``` text
          CheckoutService
          /             \
         v               v
 OrderService       PaymentService
```

Use container workarounds only when the architecture genuinely justifies
the dependency relationship.

------------------------------------------------------------------------

# Senior Scenario 3 --- `@Transactional` Appears Broken

``` java
@Service
class OrderService {

    public void placeOrder() {
        saveOrder();
    }

    @Transactional
    public void saveOrder() {
        repository.save(...);
    }
}
```

Visual:

``` text
External Caller
      |
      v
OrderService Proxy
      |
      v
placeOrder()
      |
      v
this.saveOrder()
      |
      X
transaction interceptor bypassed
```

The lesson is not merely "`@Transactional` doesn't work."

The deeper lesson:

``` text
Spring feature
     |
implemented through proxy?
     |
YES
     |
call must cross proxy boundary
```

This same mental model helps with:

-   `@Async`
-   caching
-   security
-   custom aspects
-   transactions

------------------------------------------------------------------------

# Senior Scenario 4 --- Slow Startup

``` text
Deployment
   |
   v
Pod starts
   |
   v
Spring context creation
   |
   v
Bean initialization
   |
   +--> remote API call 20s
   +--> cache warm-up 30s
   +--> schema operation
   +--> huge component scan
   |
   v
readiness delayed
```

Investigate:

``` text
Startup timing
   |
   +--> bean initialization
   +--> external dependencies
   +--> DB migrations
   +--> classpath/component scanning
   +--> eager initialization
   +--> custom post-processors
```

Do not blindly make everything lazy; that can move failures into live
traffic.

------------------------------------------------------------------------

# Senior Scenario 5 --- Multiple Payment Implementations

``` java
interface PaymentGateway {}

@Component
class CardGateway implements PaymentGateway {}

@Component
class WalletGateway implements PaymentGateway {}
```

Then:

``` java
@Service
class CheckoutService {
    CheckoutService(PaymentGateway gateway) {}
}
```

Result:

``` text
PaymentGateway candidates
        |
    +---+---+
    |       |
  Card    Wallet
    |
AMBIGUOUS
```

Possible approaches:

``` text
One default?
   |
 @Primary

Explicit implementation?
   |
 @Qualifier

Runtime strategy?
   |
Map<String, PaymentGateway>
   |
Strategy selection
```

The third approach is often useful when business logic dynamically
chooses implementations.

------------------------------------------------------------------------

# Mini Coding Drill 1 --- Constructor Injection

``` java
@Service
public class OrderService {

    private final OrderRepository repository;
    private final PaymentGateway paymentGateway;

    public OrderService(
            OrderRepository repository,
            PaymentGateway paymentGateway) {
        this.repository = repository;
        this.paymentGateway = paymentGateway;
    }
}
```

Unit test without Spring:

``` java
OrderRepository repository = mock(OrderRepository.class);
PaymentGateway gateway = mock(PaymentGateway.class);

OrderService service =
    new OrderService(repository, gateway);
```

This is one practical advantage of constructor injection.

------------------------------------------------------------------------

# Mini Coding Drill 2 --- Strategy Injection

``` java
public interface NotificationSender {
    String type();
    void send(String message);
}
```

``` java
@Component
class EmailSender implements NotificationSender {
    public String type() {
        return "email";
    }

    public void send(String message) {}
}
```

``` java
@Service
class NotificationService {

    private final Map<String, NotificationSender> senders;

    NotificationService(List<NotificationSender> senderList) {
        this.senders = senderList.stream()
            .collect(Collectors.toMap(
                NotificationSender::type,
                Function.identity()
            ));
    }

    void send(String type, String message) {
        senders.get(type).send(message);
    }
}
```

Visual:

``` text
NotificationService
       |
       v
Map<String, Sender>
       |
  +----+-----+
  |          |
email       sms
  |          |
  v          v
EmailSender SmsSender
```

This combines Spring DI with the Strategy pattern.

------------------------------------------------------------------------

# Mini Coding Drill 3 --- Bean Lifecycle

``` java
@Component
class ExternalClient {

    @PostConstruct
    void init() {
        System.out.println("initialized");
    }

    @PreDestroy
    void close() {
        System.out.println("closed");
    }
}
```

Know where these callbacks fit:

``` text
constructor
   |
dependency injection
   |
post-process before
   |
@PostConstruct
   |
post-process after / proxy
   |
READY
```

------------------------------------------------------------------------

# Mini Coding Drill 4 --- Simple Aspect

``` java
@Aspect
@Component
class TimingAspect {

    @Around("@annotation(Timed)")
    Object measure(ProceedingJoinPoint pjp)
            throws Throwable {

        long start = System.nanoTime();

        try {
            return pjp.proceed();
        } finally {
            long elapsed =
                System.nanoTime() - start;

            System.out.println(
                "elapsed=" + elapsed
            );
        }
    }
}
```

Visual:

``` text
Caller
   |
   v
Proxy
   |
TimingAspect BEFORE
   |
Target Method
   |
TimingAspect AFTER
   |
   v
Caller
```

------------------------------------------------------------------------

# Spring Core + Concurrency Connection

From CH 02:

``` text
Spring Singleton Bean
        |
        v
Shared across request threads
        |
        v
Does bean contain mutable fields?
        |
    +---+---+
    |       |
   NO      YES
    |       |
 usually   concurrency
 safe      risk
```

Example:

``` java
@Service
class CounterService {
    private int count;

    void increment() {
        count++;
    }
}
```

Spring does **not** automatically make singleton beans thread-safe.

This is a frequent interview misconception.

------------------------------------------------------------------------

# Spring Core + Distributed Backend Connection

``` text
HTTP Request
     |
     v
Controller Bean
     |
     v
Service Proxy
     |
     +--> Security
     +--> Transaction
     +--> Metrics
     +--> Cache
     |
     v
Service Target
     |
     v
Repository
     |
     v
Database
```

In a real distributed backend, understanding the Spring proxy/container
layer helps explain:

-   where transactions begin
-   where security executes
-   where metrics are recorded
-   why annotations may be bypassed
-   why startup fails
-   why bean state may race
-   why a dependency is ambiguous
-   why a circular dependency appears

------------------------------------------------------------------------

# Production Debugging Flow --- Bean Creation Failure

``` text
Application fails startup
        |
        v
Find root BeanCreationException
        |
        v
Which bean failed?
        |
        +--> missing dependency?
        |
        +--> multiple candidates?
        |
        +--> circular dependency?
        |
        +--> property binding failure?
        |
        +--> @PostConstruct failure?
        |
        +--> external dependency call?
        |
        v
Trace dependency chain
```

Do not stop at the top-level exception. Spring startup exceptions often
contain a nested dependency chain.

------------------------------------------------------------------------

# Production Debugging Flow --- Wrong Bean Injected

``` text
Unexpected implementation
        |
        v
Required interface
        |
        v
List candidate beans
        |
        v
@Primary?
        |
        v
@Qualifier?
        |
        v
Profile / conditional configuration?
        |
        v
Bean overridden / auto-configured?
```

------------------------------------------------------------------------

# Production Debugging Flow --- Proxy Problem

``` text
Annotation has no effect
        |
        v
Spring-managed object?
        |
        v
Runtime object is proxy?
        |
        v
External call crosses proxy?
        |
        v
Method interceptable?
        |
        v
Advisor/aspect enabled?
        |
        v
Correct ordering?
```

------------------------------------------------------------------------

# Critical Comparison Sheet

## IoC vs DI

``` text
IoC
|
+-- broad design principle:
    framework/container controls lifecycle

DI
|
+-- mechanism:
    dependencies supplied to object
```

------------------------------------------------------------------------

## Constructor vs Field Injection

``` text
CONSTRUCTOR
+ explicit dependencies
+ final fields
+ easy plain unit tests
+ required dependency guaranteed

FIELD
- hidden dependency
- harder plain construction
- mutable injection point
- encourages excessive dependencies
```

------------------------------------------------------------------------

## Singleton vs Prototype

``` text
Singleton
one managed instance
      |
shared by many callers

Prototype
new instance per retrieval
```

------------------------------------------------------------------------

## `@Component` vs `@Bean`

``` text
@Component
class discovered automatically

@Bean
object explicitly produced
by configuration method
```

------------------------------------------------------------------------

## Spring Event vs Kafka

``` text
Spring Event
     |
same application
     |
in-process communication

Kafka
     |
distributed broker
     |
durable cross-service events
```

------------------------------------------------------------------------

## JDK Proxy vs Class-Based Proxy

``` text
JDK Proxy
    |
interface-based

Class Proxy
    |
subclass-based
```

Interview focus: know **why proxy mechanics affect annotations and
method calls**.

------------------------------------------------------------------------

# Senior Rapid-Fire Follow-Ups

1.  IoC vs DI?
2.  Why constructor injection?
3.  Is Spring singleton JVM singleton?
4.  Are Spring singleton beans thread-safe?
5.  How does Spring discover components?
6.  `@Component` vs `@Bean`?
7.  `@Service` vs `@Repository`?
8.  What happens when two beans implement the same interface?
9.  `@Primary` vs `@Qualifier`?
10. Explain the bean lifecycle.
11. What is BeanPostProcessor?
12. Where are proxies created?
13. `@PostConstruct` vs constructor?
14. Singleton vs prototype?
15. Who destroys prototype beans?
16. How can singleton depend on request scope?
17. What is lazy initialization?
18. What are profiles?
19. Why prefer `@ConfigurationProperties` for grouped config?
20. Why are constructor circular dependencies problematic?
21. How would you redesign a circular dependency?
22. What is FactoryBean?
23. Spring events vs Kafka?
24. What is AOP?
25. What is advice?
26. What is a pointcut?
27. How does a Spring proxy work?
28. JDK proxy vs class-based proxy?
29. What is self-invocation?
30. Why can `@Transactional` fail on an internal call?
31. Why are private/final methods problematic for some proxy
    interception?
32. What are good AOP use cases?
33. Why can excessive AOP be harmful?
34. How would you debug an annotation that does nothing?
35. How would you debug a BeanCreationException?

------------------------------------------------------------------------

# Interview Checklist

``` text
IOC / DI
[ ] 01 Spring IoC
[ ] 02 Dependency Injection
[ ] 03 BeanFactory vs ApplicationContext
[ ] 04 Constructor / setter / field injection
[ ] 05 Component scanning
[ ] 06 Stereotype annotations
[ ] 07 @Bean vs component
[ ] 08 Dependency resolution

LIFECYCLE / SCOPES
[ ] 09 Bean lifecycle
[ ] 10 BeanPostProcessor
[ ] 11 @PostConstruct / @PreDestroy
[ ] 12 Lifecycle interfaces
[ ] 13 Singleton
[ ] 14 Prototype
[ ] 15 Web scopes
[ ] 16 Scope mismatch

CONFIGURATION / INTERNALS
[ ] 17 @Primary / @Qualifier
[ ] 18 Lazy initialization
[ ] 19 Profiles
[ ] 20 Externalized configuration
[ ] 21 Circular dependencies
[ ] 22 FactoryBean
[ ] 23 Application events
[ ] 24 Startup flow

AOP / PROXIES
[ ] 25 AOP
[ ] 26 Terminology
[ ] 27 Proxy model
[ ] 28 JDK vs class proxy
[ ] 29 Advice
[ ] 30 Pointcuts
[ ] 31 Invocation chain
[ ] 32 Self-invocation
[ ] 33 final/private limitations
[ ] 34 AOP use cases
[ ] 35 Proxy debugging
```

------------------------------------------------------------------------

# Chapter 04 Visual Summary

``` text
                         SPRING CORE
                              |
            +-----------------+-----------------+
            |                                   |
         CONTAINER                            PROXY
            |                                   |
            v                                   v
      Bean Definitions                       Caller
            |                                   |
            v                                   v
      Instantiate Bean                    Spring Proxy
            |                                   |
            v                          +--------+--------+
      Inject Dependencies               |        |       |
            |                        Security   Tx      Metrics
            v                          |        |       |
       Lifecycle                          +-----+-------+
            |                                   |
            v                                   v
    BeanPostProcessors                     Target Bean
            |
            +--> may wrap bean in proxy
            |
            v
       Managed Bean
```

------------------------------------------------------------------------

# Chapter 04 Exit Criteria

You are ready for **CH 05 --- Spring Boot / MVC** when you can explain
without notes:

1.  IoC vs Dependency Injection.
2.  Why constructor injection is normally preferred.
3.  BeanFactory vs ApplicationContext.
4.  How component scanning becomes bean definitions.
5.  `@Component` vs `@Bean`.
6.  How Spring resolves multiple implementations.
7.  The complete high-level bean lifecycle.
8.  What BeanPostProcessor does.
9.  Why singleton beans can create concurrency bugs.
10. Singleton vs prototype vs request scope.
11. Scope mismatch and scoped proxies.
12. Why circular dependencies are often a design smell.
13. Spring events vs Kafka.
14. How Spring proxy-based AOP works.
15. JDK vs class-based proxy conceptually.
16. Why self-invocation matters.
17. Why annotations such as transactions can appear to do nothing.
18. How to debug BeanCreationException.
19. How to debug the wrong implementation being injected.
20. How Spring Core concepts connect to transactions, security, caching
    and observability.

``` text
CH 04 SPRING CORE
       |
       v
IoC / DI
       |
       v
Bean Lifecycle
       |
       v
Scopes
       |
       v
Configuration
       |
       v
BeanPostProcessor
       |
       v
AOP / PROXY ★★★
       |
       v
READY FOR CH 05
SPRING BOOT + MVC
```

------------------------------------------------------------------------

## Next Chapter

``` text
CH 05 — SPRING BOOT / MVC (~40 Q)
|
+-- Spring Boot startup / auto-configuration
+-- Starters / configuration / profiles
+-- DispatcherServlet
+-- Request lifecycle
+-- Controllers / binding / validation
+-- Filters / interceptors
+-- Exception handling
+-- Actuator
+-- Production API scenarios
```
