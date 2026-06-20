# 023_SpringBoot_Startup — The Boot Assembly Line Model

## Core Mental Model

Do not imagine Spring Boot startup as:

```text
main() runs
application magically starts
```

That is too vague.

The better mental model is:

> **Spring Boot startup is an assembly line that turns configuration + classpath + code into a running application context and web server.**

```text
main()
  |
  v
SpringApplication.run()
  |
  v
+---------------------------------------------------+
| 1. Prepare environment                            |
| 2. Create ApplicationContext                      |
| 3. Load bean definitions                          |
| 4. Apply auto-configuration                       |
| 5. Instantiate beans                              |
| 6. Run lifecycle callbacks                        |
| 7. Start embedded web server                      |
| 8. Mark application ready                         |
+---------------------------------------------------+
  |
  v
Application ready to receive requests
```

This chapter teaches exactly one idea:

> **Spring Boot startup is not magic; it is a staged bootstrapping pipeline that builds the Spring container before your application can serve traffic.**

If you remember only one sentence:

> **Spring Boot starts by building the environment, then the ApplicationContext, then beans, then the web server, then readiness.**

---

## Why This Exists

A normal Java application starts with `main()`.

```java
public static void main(String[] args) {
    OrderService service = new OrderService();
    service.createOrder();
}
```

You manually create objects.

But a Spring Boot application is different:

```java
@SpringBootApplication
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
```

You usually do not write:

```java
new OrderController(...)
new OrderService(...)
new ProductRepository(...)
new DataSource(...)
new EntityManagerFactory(...)
new Tomcat(...)
```

Spring Boot does that assembly for you.

But in production, startup failures are common:

```text
Application failed to start
BeanCreationException
No qualifying bean
Port already in use
Database connection failed
Circular dependency
Missing property
Auto-configuration did not apply
Migration failed
Profile mismatch
```

If startup feels magical, these errors feel random.

If you understand the assembly line, startup errors become diagnosable.

---

## Problem Statement

A Spring Boot application must answer many startup questions before it can serve traffic:

```text
Which profile is active?
Which properties should be loaded?
Is this a web application?
Which embedded server should run?
Which classes are components?
Which beans should be created?
Which auto-configurations apply?
Which bean depends on which other bean?
When should database connections be initialized?
When should migrations run?
When is the application ready?
```

The core problem:

> **How does Spring Boot convert your source code and configuration into a running application without you manually wiring everything?**

Spring Boot solves this using a startup pipeline:

```text
1. Bootstrap SpringApplication
2. Prepare Environment
3. Create ApplicationContext
4. Load bean definitions
5. Process configuration and auto-configuration
6. Refresh context
7. Instantiate singleton beans
8. Run lifecycle callbacks
9. Start web server
10. Publish ready events
```

This is the one mental model.

---

## Real World Analogy

Imagine opening a restaurant in the morning.

```text
Owner unlocks building
Manager reads today's schedule
Kitchen stations are prepared
Staff are assigned roles
Ingredients are checked
Machines are started
Health checks pass
Doors open to customers
```

Spring Boot startup is similar:

```text
Restaurant opening             Spring Boot startup
------------------             -------------------
Unlock building                main()
Read schedule                  load properties/profiles
Prepare stations               create ApplicationContext
Assign staff                   register bean definitions
Start machines                 instantiate beans/resources
Start front door               start embedded server
Open to customers              application ready
```

A restaurant cannot serve customers before kitchen, staff, and payment terminal are ready.

A Spring Boot app should not serve requests before context, beans, DB, and web server are ready.

---

## The One Mental Model

Think of startup as a conveyor belt.

```text
Source Code + Properties + Dependencies
                 |
                 v
+--------------------------------------+
| SpringApplication                    |
| decides how to bootstrap             |
+--------------------------------------+
                 |
                 v
+--------------------------------------+
| Environment                          |
| profiles + properties + args         |
+--------------------------------------+
                 |
                 v
+--------------------------------------+
| ApplicationContext                   |
| Spring container                     |
+--------------------------------------+
                 |
                 v
+--------------------------------------+
| Bean Definitions                     |
| recipe cards for beans               |
+--------------------------------------+
                 |
                 v
+--------------------------------------+
| Bean Instances                       |
| actual objects wired together        |
+--------------------------------------+
                 |
                 v
+--------------------------------------+
| Embedded Web Server                  |
| Tomcat/Jetty/Netty starts            |
+--------------------------------------+
                 |
                 v
+--------------------------------------+
| Ready Application                    |
| can receive traffic                  |
+--------------------------------------+
```

Important distinction:

```text
Bean definition = recipe
Bean instance   = actual cooked object
```

Spring Boot first discovers recipes.
Then it creates objects.

---

## Core Concepts

## SpringApplication

`SpringApplication.run()` is the startup orchestrator.

```java
SpringApplication.run(OrderApplication.class, args);
```

It does not just call your controllers.
It coordinates the boot sequence.

It decides:

```text
- application type: servlet, reactive, or non-web
- environment setup
- listeners
- initializers
- application context type
- startup events
```

## Environment

The `Environment` contains configuration.

```text
application.properties
application.yml
environment variables
command-line arguments
active profiles
system properties
```

Example:

```properties
spring.profiles.active=prod
server.port=8080
spring.datasource.url=jdbc:postgresql://db:5432/orders
```

During startup, many decisions depend on the environment:

```text
Which profile beans are active?
Which database URL is used?
Which port should the server bind?
Which auto-configurations should apply?
```

## ApplicationContext

The `ApplicationContext` is the Spring container.

It owns:

```text
- bean definitions
- bean instances
- dependency injection
- lifecycle callbacks
- events
- environment access
```

Mental model:

```text
ApplicationContext = factory + registry + lifecycle manager
```

## Bean Definition

A bean definition is not the object yet.
It is metadata describing how to create the object.

```text
Bean name: orderService
Class: OrderService
Scope: singleton
Dependencies: ProductRepository, OrderRepository
Lifecycle callbacks: yes/no
```

## Bean Instance

A bean instance is the actual Java object created from a bean definition.

```text
OrderService@7a21
ProductRepository proxy@92fa
OrderController@11bc
```

Spring creates and wires these during context refresh.

## Auto-Configuration

Auto-configuration means:

> **Spring Boot looks at your classpath, properties, and existing beans, then registers useful default beans if conditions match.**

Example:

```text
If spring-webmvc is on classpath
and servlet web app is detected
then configure DispatcherServlet and embedded Tomcat.

If spring-data-jpa is on classpath
and DataSource exists
then configure EntityManagerFactory and transaction manager.
```

Auto-configuration is conditional assembly.

It is not random magic.

## Context Refresh

`refresh()` is the critical phase.

During refresh, Spring:

```text
- processes bean factory post-processors
- resolves configuration classes
- registers bean definitions
- creates singleton beans
- applies BeanPostProcessors
- runs initialization callbacks
- starts lifecycle beans
- starts embedded web server in web apps
```

If a bean cannot be created, startup usually fails here.

---

## Internal Architecture

```text
main()
  |
  v
SpringApplication.run()
  |
  +--> Prepare Bootstrap Registry
  |
  +--> Create Environment
  |
  +--> Load application.properties/yml
  |
  +--> Create ApplicationContext
  |
  +--> Apply Initializers
  |
  +--> Load Bean Definitions
  |
  +--> Apply Auto-Configurations
  |
  +--> Refresh ApplicationContext
  |       |
  |       +--> BeanFactoryPostProcessors
  |       +--> BeanPostProcessors
  |       +--> Instantiate singleton beans
  |       +--> Dependency injection
  |       +--> init callbacks
  |       +--> start lifecycle beans
  |
  +--> Start Web Server
  |
  +--> Run ApplicationRunner / CommandLineRunner
  |
  +--> Publish ApplicationReadyEvent
```

Simple picture:

```text
SpringApplication
      |
      v
Environment
      |
      v
ApplicationContext
      |
      v
BeanFactory
      |
      v
Bean Definitions
      |
      v
Bean Instances
      |
      v
Ready App
```

---

## Internal Working

Given this app:

```java
@SpringBootApplication
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
```

This annotation matters:

```java
@SpringBootApplication
```

It combines:

```text
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```

Meaning:

```text
@SpringBootConfiguration
  This class is a configuration source.

@EnableAutoConfiguration
  Import Boot auto-configurations based on classpath/properties.

@ComponentScan
  Scan current package and subpackages for components.
```

If your main class is here:

```text
com.example.order.OrderApplication
```

Spring scans:

```text
com.example.order.*
```

But not automatically:

```text
com.example.payment.*
```

unless configured.

This is why package placement matters.

---

## Step-by-Step Startup Flow

## Step 1 — JVM Calls main()

```java
public static void main(String[] args) {
    SpringApplication.run(OrderApplication.class, args);
}
```

At this moment:

```text
No controller bean exists.
No service bean exists.
No repository bean exists.
No web server is ready.
```

Only Java has started.

## Step 2 — SpringApplication Is Created

Spring Boot prepares startup orchestration.

```text
Primary source: OrderApplication.class
Arguments: command-line args
Application type: servlet web app if Spring MVC/Tomcat found
Listeners: registered
Initializers: registered
```

## Step 3 — Environment Is Prepared

Spring loads configuration.

Sources may include:

```text
application.yml
application-prod.yml
environment variables
JVM system properties
command-line arguments
```

Example:

```bash
java -jar app.jar --server.port=9090 --spring.profiles.active=prod
```

Environment now knows:

```text
active profile = prod
server port = 9090
datasource URL = from prod config
```

## Step 4 — ApplicationContext Is Created

For a normal Spring MVC app:

```text
ServletWebServerApplicationContext
```

This context knows how to start an embedded servlet web server.

For a non-web app:

```text
AnnotationConfigApplicationContext
```

For reactive app:

```text
ReactiveWebServerApplicationContext
```

## Step 5 — Bean Definitions Are Registered

Spring scans components:

```java
@RestController
@Service
@Repository
@Component
@Configuration
@Bean
```

It creates bean definitions.

```text
orderController -> OrderController class
orderService    -> OrderService class
orderRepository -> Spring Data repository proxy definition
```

Still, many beans may not be actual objects yet.

They are mostly recipes.

## Step 6 — Auto-Configuration Adds More Bean Definitions

Spring Boot checks conditions.

```text
Is DataSource class available?
Is datasource URL configured?
Has user already defined DataSource bean?

If needed:
  create DataSource bean definition
```

Example auto-configured beans:

```text
DispatcherServlet
TomcatServletWebServerFactory
ObjectMapper
DataSource
EntityManagerFactory
TransactionManager
HandlerMapping
MessageConverters
```

Boot does this because dependencies are present.

```text
spring-boot-starter-web
  -> MVC + Jackson + embedded Tomcat

spring-boot-starter-data-jpa
  -> JPA + Hibernate + transaction support
```

## Step 7 — Context Refresh Creates Beans

Now the main container refresh begins.

Spring creates singleton beans in dependency order.

Example:

```text
OrderController needs OrderService
OrderService needs ProductRepository
ProductRepository needs EntityManager
EntityManager needs EntityManagerFactory
EntityManagerFactory needs DataSource
DataSource needs datasource properties
```

Creation chain:

```text
DataSource
  -> EntityManagerFactory
      -> TransactionManager
          -> Repository proxies
              -> Service
                  -> Controller
```

If something is missing, startup fails.

Example:

```text
OrderService required a bean of type PaymentClient that could not be found
```

That means:

```text
Spring found a dependency edge but no bean definition/instance to satisfy it.
```

## Step 8 — BeanPostProcessors Apply Enhancements

Some beans are wrapped or modified.

Examples:

```text
@Transactional service -> proxy wrapping service
@Async method          -> proxy wrapping service
@Repository           -> exception translation proxy
@ConfigurationProperties -> properties binding
```

Your service may look like a normal object, but the object injected into controller may be a proxy.

```text
Controller -> OrderService proxy -> TransactionInterceptor -> real OrderService
```

## Step 9 — Embedded Web Server Starts

For Spring MVC apps, Boot starts embedded Tomcat.

```text
Tomcat binds to port 8080/9090
DispatcherServlet registered
Filters registered
Servlet context ready
```

Now HTTP requests can be accepted.

## Step 10 — Runners Execute

After context is ready, Boot runs:

```java
@Component
public class StartupRunner implements CommandLineRunner {
    @Override
    public void run(String... args) {
        System.out.println("Application started");
    }
}
```

Also:

```java
ApplicationRunner
```

These are useful for startup tasks, but dangerous for heavy blocking work.

## Step 11 — ApplicationReadyEvent Published

Spring publishes events such as:

```text
ApplicationStartedEvent
ApplicationReadyEvent
```

Readiness means:

```text
The application context is refreshed and the app is ready to service requests.
```

In Kubernetes, readiness probes should align with this idea.

---

## Rich ASCII Diagram — Startup Assembly Line

```text
+-----------------------------+
| JVM starts main()           |
+-------------+---------------+
              |
              v
+-----------------------------+
| SpringApplication.run()     |
| boot orchestrator           |
+-------------+---------------+
              |
              v
+-----------------------------+
| Prepare Environment         |
| properties, profiles, args  |
+-------------+---------------+
              |
              v
+-----------------------------+
| Create ApplicationContext   |
| container type selected     |
+-------------+---------------+
              |
              v
+-----------------------------+
| Register Bean Definitions   |
| scan + config + @Bean       |
+-------------+---------------+
              |
              v
+-----------------------------+
| Apply Auto-Configuration    |
| conditional default beans   |
+-------------+---------------+
              |
              v
+-----------------------------+
| Refresh Context             |
| instantiate + wire beans    |
+-------------+---------------+
              |
              v
+-----------------------------+
| Start Embedded Server       |
| Tomcat/Jetty/Netty          |
+-------------+---------------+
              |
              v
+-----------------------------+
| Run Runners + Ready Event   |
| app ready for traffic       |
+-----------------------------+
```

---

## Java Code Example — Minimal Boot App

```java
@SpringBootApplication
public class ShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }
}
```

Controller:

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return productService.getProduct(id);
    }
}
```

Service:

```java
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow();

        return new ProductResponse(product.getId(), product.getName());
    }
}
```

Repository:

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

Startup object graph:

```text
ProductController
        |
        v
ProductService
        |
        v
ProductRepository proxy
        |
        v
EntityManager
        |
        v
DataSource
```

Spring must create lower-level infrastructure first.

---

## Internal Execution Explanation

When app starts:

```text
1. @SpringBootApplication triggers component scan.
2. ProductController bean definition found.
3. ProductService bean definition found.
4. ProductRepository interface found by Spring Data JPA.
5. Boot auto-configures DataSource from properties.
6. Boot auto-configures EntityManagerFactory.
7. Spring Data creates repository proxy.
8. Spring creates ProductService with repository dependency.
9. Spring creates ProductController with service dependency.
10. MVC registers controller mapping:
    GET /api/products/{id}
11. Tomcat starts.
12. App becomes ready.
```

Mapping table:

```text
Annotation              Startup effect
----------              --------------
@SpringBootApplication  scan + auto-config + boot config
@RestController         create controller bean + request mapping
@Service                create service bean
@Repository             create persistence bean / exception translation
@Bean                   create bean from method
@Configuration          source of bean definitions
@Profile                active only for matching profile
@ConditionalOn...       auto-config condition
```

---

## Spring Boot Example With Startup Logs

Typical log shape:

```text
Starting ShopApplication using Java ...
No active profile set, falling back to default profile
Tomcat initialized with port 8080
Starting service [Tomcat]
Initializing Spring embedded WebApplicationContext
Bootstrapping Spring Data JPA repositories
Initialized JPA EntityManagerFactory
Tomcat started on port 8080
Started ShopApplication in 4.231 seconds
```

How to read it:

```text
Starting application
  -> SpringApplication started

No active profile
  -> Environment prepared

Tomcat initialized
  -> web application detected

Bootstrapping repositories
  -> Spring Data scanning repository interfaces

EntityManagerFactory initialized
  -> JPA infrastructure ready

Tomcat started
  -> web server bound to port

Started application
  -> context refresh complete
```

Startup logs are not noise.
They are the assembly line speaking.

---

## Sequence Diagram

```text
JVM
 |
 | calls main()
 v
ShopApplication
 |
 | SpringApplication.run()
 v
SpringApplication
 |
 | prepare Environment
 v
Environment
 |
 | create context
 v
ApplicationContext
 |
 | scan components
 v
BeanDefinitionRegistry
 |
 | register auto-config beans
 v
AutoConfiguration
 |
 | refresh()
 v
BeanFactory
 |
 | instantiate singletons
 v
Beans
 |
 | start embedded server
 v
Tomcat
 |
 | publish ready event
 v
ApplicationReadyEvent
```

---

## Multiple Dry Runs

## Dry Run 1 — Normal Successful Startup

Configuration:

```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/shop
```

Flow:

```text
1. main() calls SpringApplication.run().
2. Environment loads properties.
3. Servlet web application detected.
4. ServletWebServerApplicationContext created.
5. Component scan finds controller, service, repository.
6. Auto-config creates DataSource and JPA infrastructure.
7. Beans are instantiated in dependency order.
8. Tomcat starts on 8080.
9. ApplicationReadyEvent published.
10. App can receive HTTP requests.
```

Result:

```text
Started ShopApplication
```

## Dry Run 2 — Missing Bean Failure

Code:

```java
@Service
public class OrderService {
    private final PaymentClient paymentClient;

    public OrderService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }
}
```

But no bean exists:

```text
No @Component PaymentClient
No @Bean PaymentClient
No auto-configuration creates it
```

Startup:

```text
1. Spring creates OrderService.
2. Constructor requires PaymentClient.
3. BeanFactory searches for PaymentClient bean.
4. None found.
5. Startup fails.
```

Error:

```text
Parameter 0 of constructor in OrderService required a bean of type PaymentClient that could not be found.
```

Mental model:

```text
Bean recipe required dependency.
Container could not satisfy dependency edge.
```

## Dry Run 3 — Port Already In Use

Configuration:

```properties
server.port=8080
```

But another app already uses 8080.

Startup:

```text
1. Context mostly builds successfully.
2. Tomcat tries to bind port 8080.
3. OS says port already in use.
4. Web server startup fails.
5. Spring shuts down context.
```

Fix:

```text
Change server.port
Stop conflicting process
Use random port for tests
```

## Dry Run 4 — Wrong Profile

Code:

```java
@Profile("prod")
@Bean
public PaymentClient prodPaymentClient() {
    return new RealPaymentClient();
}
```

Active profile:

```text
dev
```

Result:

```text
prodPaymentClient bean definition not active.
Dependent service may fail if no dev replacement exists.
```

Mental model:

```text
Profiles filter bean definitions before object creation.
```

## Dry Run 5 — Slow Startup

Symptoms:

```text
Application takes 90 seconds to start.
```

Possible causes:

```text
Database connection attempts timeout.
Flyway/Liquibase migrations run slowly.
Too many classpath scans.
External call in @PostConstruct.
Heavy CommandLineRunner.
Large EntityManagerFactory initialization.
```

Debug using startup assembly line:

```text
Which stage is slow?
Environment?
Bean creation?
DB migration?
Web server?
Runners?
```

---

## Production Scale Example

In production, Spring Boot startup is part of deployment safety.

```text
Kubernetes Pod starts
        |
        v
JVM starts
        |
        v
Spring Boot startup
        |
        v
ApplicationReadyEvent
        |
        v
Readiness probe passes
        |
        v
Service sends traffic
```

Bad deployment:

```text
Container starts
Kubernetes sends traffic immediately
Spring context still initializing
Requests fail
```

Good deployment:

```text
Container starts
Liveness says process alive
Readiness stays false while app initializes
DB migrations complete
Application ready
Readiness passes
Traffic begins
```

Startup also matters for autoscaling.

```text
If startup takes 2 minutes,
new pods cannot absorb traffic quickly.
```

Production startup concerns:

```text
- startup time
- readiness correctness
- database availability
- migration safety
- config correctness
- memory during classpath scanning
- graceful failure
```

---

## Production Failure Story

A team deployed a Spring Boot order service to Kubernetes.

Everything looked fine locally.

In production, pods restarted repeatedly.

Logs:

```text
Started OrderApplication
ApplicationReadyEvent published
Running startup cache warmup...
Calling pricing-service...
Timeout after 60 seconds
Pod killed by liveness probe
```

Root cause:

```text
Heavy external network call was placed in CommandLineRunner.
The app looked started but was not operational.
Liveness probe killed it during slow warmup.
```

Better design:

```text
Do not block startup on optional warmup.
Use readiness to control traffic.
Move warmup to background after readiness only if safe.
Set realistic startup probes.
Avoid external calls in @PostConstruct and bean constructors.
```

Another failure:

```text
PaymentClient bean existed only under @Profile("prod").
Production used active profile "production".
Bean missing.
Startup failed.
```

Lesson:

> **Startup failures are rarely random. They are usually assembly-line failures: missing recipe, bad config, failed dependency, broken lifecycle step, or web server bind issue.**

---

## Debugging Mindset

When Spring Boot fails to start, ask:

```text
Which startup stage failed?
```

Use this map:

```text
Property/profile error
  -> Environment stage

No qualifying bean
  -> Bean definition/dependency stage

BeanCreationException
  -> Bean instantiation/init stage

Circular dependency
  -> Object graph design stage

Port already in use
  -> Web server start stage

Database connection failed
  -> Infrastructure bean stage

Migration failed
  -> Startup runner/migration stage

App starts but traffic fails
  -> Readiness/lifecycle stage
```

### Debugging Commands and Settings

Show auto-configuration report:

```bash
java -jar app.jar --debug
```

Useful properties:

```properties
logging.level.org.springframework.boot.autoconfigure=DEBUG
logging.level.org.springframework.beans.factory=DEBUG
```

Actuator startup endpoint if enabled:

```properties
management.endpoints.web.exposure.include=startup,health,info
management.endpoint.startup.enabled=true
```

Use with care in production.

### How to Read `ConditionEvaluationReport`

If auto-config did not happen, ask:

```text
Was the required class on classpath?
Was the required property present?
Did user-defined bean disable auto-config?
Did a condition not match?
```

Auto-configuration is condition-based.

When a condition fails, Boot usually tells you why.

---

## Common Misconceptions

## Misconception 1 — “Spring Boot starts Tomcat first”

Not exactly.

Boot creates and refreshes the application context.
The embedded web server starts as part of web context refresh.
Many bean definitions and infrastructure beans are prepared before requests can be served.

## Misconception 2 — “@SpringBootApplication only marks main class”

No.

It combines configuration, component scanning, and auto-configuration.

```text
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```

## Misconception 3 — “Auto-configuration always wins”

No.

Auto-configuration is conditional.
Your own bean often backs off auto-config.

Example:

```text
If you define your own DataSource,
Boot usually does not create another default DataSource.
```

## Misconception 4 — “A bean exists because the class exists”

No.

A class becomes a bean only if:

```text
- component scanned with stereotype annotation
- declared with @Bean
- imported
- created by auto-configuration
- registered programmatically
```

A plain class on the classpath is not automatically a bean.

## Misconception 5 — “Startup success means app is healthy”

Not always.

The app may start but fail actual dependencies later.

Use health/readiness checks.

```text
Started != ready for real traffic
```

## Misconception 6 — “Put initialization anywhere”

No.

Avoid heavy work in:

```text
constructors
@PostConstruct
BeanPostProcessor
CommandLineRunner
```

unless you intentionally want startup to wait/fail.

---

## Java/Spring Boot Code Examples With Internal Execution

## Example 1 — Custom Bean

```java
@Configuration
public class PaymentConfig {

    @Bean
    public PaymentClient paymentClient() {
        return new StripePaymentClient();
    }
}
```

Startup:

```text
1. Component scan finds PaymentConfig.
2. Spring sees @Bean method.
3. Registers bean definition for paymentClient.
4. During refresh, invokes paymentClient().
5. Stores StripePaymentClient as singleton bean.
6. Injects it where PaymentClient is required.
```

## Example 2 — Profile-Specific Bean

```java
@Configuration
public class PaymentConfig {

    @Bean
    @Profile("dev")
    public PaymentClient fakePaymentClient() {
        return new FakePaymentClient();
    }

    @Bean
    @Profile("prod")
    public PaymentClient realPaymentClient() {
        return new RealPaymentClient();
    }
}
```

Startup with:

```properties
spring.profiles.active=dev
```

Result:

```text
fakePaymentClient active.
realPaymentClient ignored.
```

Startup with:

```properties
spring.profiles.active=prod
```

Result:

```text
realPaymentClient active.
fakePaymentClient ignored.
```

Startup with no active profile:

```text
Neither dev nor prod bean active.
Dependency may fail.
```

## Example 3 — ApplicationRunner

```java
@Component
public class StartupCheckRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("Startup check completed");
    }
}
```

Internal timing:

```text
Runs after ApplicationContext refresh.
Runs before SpringApplication.run() fully returns.
Can delay application readiness.
```

Use for lightweight startup checks only.

---

## Performance Considerations

Startup performance matters when:

```text
- autoscaling
- blue/green deployment
- pod restarts
- serverless cold starts
- CI integration tests
```

Slow startup causes:

```text
long deployment time
slow recovery after crash
poor autoscaling response
test suite slowdown
increased memory during boot
```

Common startup slow points:

```text
Database migrations
Entity scanning
Classpath scanning
Large dependency graph
External network calls
Heavy @PostConstruct logic
CommandLineRunner jobs
```

Better practices:

```text
Keep constructors lightweight.
Avoid external calls during bean creation.
Use lazy initialization only deliberately.
Measure startup stages.
Split heavy warmup from critical readiness.
Use Testcontainers carefully in tests.
```

Lazy initialization:

```properties
spring.main.lazy-initialization=true
```

This may reduce startup time but can move failures to first request.

Use carefully.

---

## Scalability Considerations

Startup is part of system scalability.

```text
If each pod starts in 10 seconds:
  autoscaling responds quickly.

If each pod starts in 3 minutes:
  traffic spike may hurt before new pods are ready.
```

For Kubernetes:

```text
startupProbe:
  protects slow-starting app from premature liveness kill

readinessProbe:
  prevents traffic before app is ready

livenessProbe:
  restarts stuck app after it should be healthy
```

Use separate mental model:

```text
Started process != ready app != healthy dependency graph
```

Production-grade startup:

```text
- deterministic profiles
- fail fast on required config
- avoid optional dependency blocking
- readiness reflects serving ability
- migration strategy is safe
- startup time is measured
```

---

## Failure Investigation Playbook

## Step 1 — Find the first real exception

Spring logs can be long.

Look for:

```text
Caused by:
```

The last exception is not always the root.
Find the first meaningful cause.

## Step 2 — Classify the failure stage

```text
Environment?
Bean definition?
Bean creation?
Auto-configuration?
Web server?
Database?
Migration?
Runner?
```

## Step 3 — Inspect active profiles and properties

Check logs:

```text
The following profiles are active: prod
```

Verify:

```text
server.port
datasource URL
feature flags
profile names
secrets
```

## Step 4 — Inspect bean graph

For missing beans:

```text
Who needs the bean?
What type is required?
Was implementation scanned?
Is it behind @Profile?
Is it behind @Conditional?
```

## Step 5 — Inspect auto-configuration report

Run:

```bash
java -jar app.jar --debug
```

Look for:

```text
Positive matches
Negative matches
Unconditional classes
```

## Step 6 — Check infrastructure

```text
Is DB reachable?
Is port available?
Are migrations safe?
Are credentials valid?
Is config mounted?
```

---

## Interview Q&A

### Q1. What happens when a Spring Boot application starts?

Strong answer:

> `SpringApplication.run()` prepares the environment, creates the appropriate `ApplicationContext`, loads bean definitions through component scan and configuration classes, applies auto-configuration, refreshes the context by creating and wiring beans, starts the embedded web server for web apps, runs startup runners, and publishes readiness events.

### Q2. What does `@SpringBootApplication` do?

Strong answer:

> It combines `@SpringBootConfiguration`, `@EnableAutoConfiguration`, and `@ComponentScan`. It marks the main configuration class, enables conditional Boot auto-configuration, and scans the package and subpackages for components.

### Q3. What is auto-configuration?

Strong answer:

> Auto-configuration is Spring Boot’s conditional registration of default beans based on classpath, properties, and existing beans. For example, if Spring MVC is on the classpath, Boot configures MVC infrastructure and an embedded server unless the user provides alternatives.

### Q4. What is ApplicationContext refresh?

Strong answer:

> Refresh is the phase where the Spring container processes bean definitions, applies post-processors, instantiates singleton beans, injects dependencies, runs initialization callbacks, starts lifecycle beans, and for web apps starts the embedded server.

### Q5. Why does a “No qualifying bean” error happen?

Strong answer:

> It means Spring found a dependency requirement but could not find a matching bean in the application context. The implementation may not be component-scanned, may not be declared with `@Bean`, may be inactive due to profile/condition, or may be missing from the classpath.

### Q6. Does startup success mean the application is production ready?

Strong answer:

> Not always. Startup success means the context was created, but production readiness should include whether the app can actually serve traffic and required dependencies are healthy. That is why readiness probes and health checks matter.

### Q7. Why should heavy work not be placed in constructors or `@PostConstruct`?

Strong answer:

> Because it blocks bean creation and can fail or delay the entire application startup. Heavy external calls or warmups should be carefully controlled, observable, and often moved outside critical startup or reflected in readiness.

---

## Production Checklist

```text
Startup Configuration
[ ] Are active profiles explicit?
[ ] Are required properties validated?
[ ] Are secrets/config mounted before startup?
[ ] Are defaults safe?

Bean Graph
[ ] Are components under scan path?
[ ] Are custom beans declared clearly?
[ ] Are profile-specific beans covered for each environment?
[ ] Are circular dependencies avoided?

Auto-Configuration
[ ] Do we understand which starters are active?
[ ] Have we checked condition report for surprises?
[ ] Are user-defined beans intentionally overriding defaults?

Infrastructure
[ ] Is DB reachable?
[ ] Are migrations safe and bounded?
[ ] Is server.port correct?
[ ] Are external calls avoided during bean creation?

Production Runtime
[ ] Is readiness probe accurate?
[ ] Is liveness probe not too aggressive?
[ ] Is startup probe configured for slow apps?
[ ] Is startup time measured?
[ ] Are startup failures logged clearly?
```

---

## One-Page Cheat Sheet

```text
Spring Boot Startup
===================

main()
  JVM entry point.

SpringApplication.run()
  Startup orchestrator.

Environment
  Properties, profiles, args, env vars.

ApplicationContext
  Spring container.

Bean Definition
  Recipe for creating a bean.

Bean Instance
  Actual object created by Spring.

Auto-Configuration
  Conditional default bean registration.

Context Refresh
  Main phase where beans are created and wired.

Embedded Server
  Tomcat/Jetty/Netty starts for web apps.

Runners
  ApplicationRunner / CommandLineRunner after context refresh.

Ready Event
  App is ready to serve traffic.

Best Sentence
-------------
Spring Boot startup is an assembly line:
environment -> context -> bean definitions -> beans -> web server -> ready.

Debugging Rules
---------------
Missing property?       Environment stage.
No qualifying bean?     Bean graph stage.
BeanCreationException?  Bean creation/init stage.
Port in use?            Web server stage.
DB failure?             Infrastructure bean stage.
Slow startup?           Measure assembly line stage.
```

---

## Last-Minute Interview Revision

Do not say:

```text
Spring Boot starts automatically because of annotations.
```

Say:

```text
Spring Boot uses `SpringApplication.run()` to prepare the environment, create the application context, register bean definitions through scanning and auto-configuration, refresh the context by creating beans, start the embedded server, and publish readiness events.
```

Senior version:

```text
I debug startup by locating the failed assembly-line stage: environment, bean definition, dependency graph, auto-configuration, bean initialization, infrastructure startup, web server binding, or readiness. That makes Boot startup predictable instead of magical.
```

---

## One Picture To Remember

```text
                 SPRING BOOT STARTUP ASSEMBLY LINE

main()
  |
  v
SpringApplication.run()
  |
  v
+------------------+
| Environment      |  profiles, properties, args
+------------------+
  |
  v
+------------------+
| ApplicationContext| container selected
+------------------+
  |
  v
+------------------+
| Bean Definitions | recipes from scan/config/auto-config
+------------------+
  |
  v
+------------------+
| Bean Instances   | objects created, injected, proxied
+------------------+
  |
  v
+------------------+
| Web Server       | Tomcat/Jetty/Netty binds port
+------------------+
  |
  v
+------------------+
| Ready App        | can receive traffic
+------------------+
```

Final retention sentence:

> **Spring Boot startup is the assembly line that turns configuration, classpath, and code into a ready ApplicationContext plus web server.**
