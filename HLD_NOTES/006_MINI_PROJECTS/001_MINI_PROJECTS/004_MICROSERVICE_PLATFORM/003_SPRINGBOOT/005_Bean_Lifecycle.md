# 005_Bean_Lifecycle.md

> MiniSpringBoot Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Production Debugging

---

# 1. Why This Chapter Exists

Most Spring learners hear this sentence:

```text
Spring creates beans and manages their lifecycle.
```

But this sentence is too abstract.

A bean is not just an object.

A Spring bean is an object that goes through a controlled journey:

```text
definition -> construction -> dependency injection -> initialization -> post-processing -> ready -> destruction
```

If you understand this journey, many Spring production problems become obvious:

```text
Why is my dependency null?
Why did @PostConstruct run before my app was ready?
Why is @Transactional not active inside initialization?
Why did my bean become a proxy?
Why does a circular dependency fail?
Why is shutdown not closing resources?
Why did a Kafka listener start too early?
Why is my cache warmup blocking startup?
```

The bean lifecycle is the hidden startup pipeline behind every Spring Boot application.

One picture:

```text
Class metadata
   |
   v
BeanDefinition
   |
   v
Create object
   |
   v
Inject dependencies
   |
   v
Initialize bean
   |
   v
Apply post-processors / proxy
   |
   v
Ready bean in ApplicationContext
   |
   v
Destroy on shutdown
```

Do not memorize lifecycle callback names first.

Understand the story:

```text
Spring is a factory.
A bean is a product.
The lifecycle is the assembly line.
```

---

# 2. The Simple World: Plain Java Object Lifecycle

In plain Java, object lifecycle is simple.

```java
OrderService service = new OrderService();
service.placeOrder("order-1");
```

Lifecycle:

```text
new object
   |
   v
constructor runs
   |
   v
object is usable
   |
   v
garbage collector eventually removes it
```

ASCII:

```text
Developer Code
     |
     v
new OrderService()
     |
     v
constructor
     |
     v
use object
```

You control object creation.

You control when it is used.

You control constructor arguments.

You control cleanup if cleanup exists.

Example:

```java
public class FileReportWriter {
    private final BufferedWriter writer;

    public FileReportWriter(String path) throws IOException {
        this.writer = Files.newBufferedWriter(Path.of(path));
    }

    public void write(String line) throws IOException {
        writer.write(line);
    }

    public void close() throws IOException {
        writer.close();
    }
}
```

In plain Java, the caller must remember:

```text
create object
use object
close object
handle exception
```

This is fine for small code.

But backend applications have hundreds of such objects.

---

# 3. The Spring World: Object Creation Is Managed

In Spring, you usually do not write this:

```java
OrderRepository repository = new OrderRepository(dataSource);
OrderService service = new OrderService(repository);
OrderController controller = new OrderController(service);
```

You write:

```java
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}
```

Spring does the rest.

Mental model:

```text
You declare what the class needs.
Spring decides when and how to create it.
```

ASCII:

```text
Your Classes
  @Service
  @Repository
  @Controller
      |
      v
Spring scans metadata
      |
      v
Spring creates BeanDefinitions
      |
      v
Spring creates object instances
      |
      v
Spring injects dependencies
      |
      v
Spring initializes and manages beans
```

A bean is not created randomly.

It follows a pipeline.

That pipeline is the bean lifecycle.

---

# 4. Bean vs Object

A normal Java object:

```java
new PaymentClient()
```

A Spring bean:

```text
PaymentClient object created, wired, initialized, post-processed, possibly proxied, tracked, and destroyed by Spring.
```

Difference:

```text
Object:
- Created manually
- No framework lifecycle
- No automatic dependency injection
- No post-processing
- No automatic destroy callback

Bean:
- Created by Spring
- Stored in ApplicationContext
- Dependencies injected
- Lifecycle callbacks invoked
- BeanPostProcessors may wrap it
- Destroy callbacks invoked on shutdown
```

ASCII:

```text
Plain Object

Developer
   |
   v
new PaymentClient()
   |
   v
use directly
```

```text
Spring Bean

Class
  |
  v
BeanDefinition
  |
  v
Spring Factory
  |
  v
Object Instance
  |
  v
Dependencies + Init + Proxy
  |
  v
ApplicationContext
```

Important memory hook:

```text
Every Spring bean is a Java object.
Not every Java object is a Spring bean.
```

---

# 5. The Bean Lifecycle Assembly Line

Think of a car factory.

```text
Blueprint       = BeanDefinition
Raw car body    = object instance
Wiring          = dependency injection
Quality check   = initialization callbacks
Custom wrapping = proxy/post-processing
Showroom        = ready bean in ApplicationContext
Scrap/recycle   = destroy callback
```

Spring bean lifecycle:

```text
1. Read metadata
2. Create BeanDefinition
3. Instantiate object
4. Populate properties / inject dependencies
5. Aware callbacks
6. BeanPostProcessor before initialization
7. Initialization callbacks
8. BeanPostProcessor after initialization
9. Bean ready for use
10. Destruction callback on shutdown
```

ASCII:

```text
+-------------------------+
| 1. BeanDefinition       |
+------------+------------+
             |
             v
+-------------------------+
| 2. Instantiate Object   |
+------------+------------+
             |
             v
+-------------------------+
| 3. Inject Dependencies  |
+------------+------------+
             |
             v
+-------------------------+
| 4. Aware Callbacks      |
+------------+------------+
             |
             v
+-------------------------+
| 5. Before Init BPP      |
+------------+------------+
             |
             v
+-------------------------+
| 6. Init Callbacks       |
+------------+------------+
             |
             v
+-------------------------+
| 7. After Init BPP       |
|    proxy may be created |
+------------+------------+
             |
             v
+-------------------------+
| 8. Ready Bean           |
+------------+------------+
             |
             v
+-------------------------+
| 9. Destroy on Shutdown  |
+-------------------------+
```

BPP means BeanPostProcessor.

Do not memorize this as a list.

Remember it as:

```text
metadata -> object -> dependencies -> initialization -> wrapping -> ready -> cleanup
```

---

# 6. Phase 1: BeanDefinition Is The Blueprint

Before Spring creates an object, it creates metadata.

This metadata is called a `BeanDefinition`.

It describes:

```text
bean class
bean name
scope
constructor arguments
factory method
lazy/eager behavior
init method
destroy method
dependency information
primary/qualifier metadata
```

Example:

```java
@Service
public class OrderService {
}
```

Spring sees this and internally understands:

```text
Bean name: orderService
Bean class: com.example.OrderService
Scope: singleton
Create method: constructor
Candidate for dependency injection: yes
```

ASCII:

```text
@Service class
      |
      v
Component Scan
      |
      v
BeanDefinition Registry
      |
      v
"orderService" -> OrderService blueprint
```

Important:

```text
BeanDefinition is not the object.
It is the recipe for creating the object.
```

Like:

```text
Recipe card != cooked food
Class file != bean instance
BeanDefinition != bean object
```

This distinction matters in debugging startup.

Sometimes Spring has the definition but cannot create the bean.

---

# 7. Phase 2: Instantiation

Instantiation means Spring creates the raw object.

For constructor injection:

```java
@Service
public class OrderService {
    private final PaymentClient paymentClient;

    public OrderService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }
}
```

Spring must first create or find `PaymentClient`.

Then it calls:

```text
new OrderService(paymentClient)
```

ASCII:

```text
BeanDefinition: OrderService
      |
      v
Resolve constructor dependencies
      |
      v
PaymentClient bean
      |
      v
new OrderService(paymentClient)
```

If dependencies are missing, startup fails.

Common error:

```text
UnsatisfiedDependencyException
```

Meaning:

```text
Spring tried to create a bean but could not find or create something required by its constructor.
```

Production mindset:

```text
Constructor injection makes missing dependencies fail early at startup.
That is good.
A broken graph should fail before receiving traffic.
```

---

# 8. Phase 3: Dependency Injection

Dependency injection means Spring fills the bean with required collaborators.

There are three common styles:

```text
Constructor injection
Setter injection
Field injection
```

Recommended style:

```java
@Service
public class CheckoutService {
    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;

    public CheckoutService(OrderRepository orderRepository,
                           PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
    }
}
```

ASCII:

```text
OrderRepository bean ----+
                         |
                         v
PaymentClient bean ------> CheckoutService constructor
```

Field injection:

```java
@Service
public class BadCheckoutService {
    @Autowired
    private PaymentClient paymentClient;
}
```

Why avoid field injection?

```text
Dependency is hidden.
Object cannot be created easily in unit tests.
Field may be null outside Spring.
Class can be partially initialized.
```

Constructor injection mental model:

```text
A service should be born complete.
```

ASCII:

```text
Good:
constructor requires all dependencies
object cannot exist without them

Bad:
object created first
dependencies injected later into fields
```

---

# 9. Phase 4: Aware Callbacks

Sometimes a bean wants to know about the Spring container.

Spring offers `Aware` interfaces.

Examples:

```text
BeanNameAware
ApplicationContextAware
EnvironmentAware
ResourceLoaderAware
```

Example:

```java
@Component
public class MyBean implements BeanNameAware {
    @Override
    public void setBeanName(String name) {
        System.out.println("Bean name is " + name);
    }
}
```

Lifecycle position:

```text
instantiate
   |
   v
inject dependencies
   |
   v
Aware callbacks
   |
   v
initialization callbacks
```

ASCII:

```text
Spring creates bean
      |
      v
Spring injects dependencies
      |
      v
Spring says:
"By the way, your bean name is myBean"
```

Use carefully.

Most business beans should not need `ApplicationContextAware`.

Bad smell:

```java
@Component
public class OrderService implements ApplicationContextAware {
    private ApplicationContext context;

    public void process() {
        PaymentClient client = context.getBean(PaymentClient.class);
    }
}
```

This turns Spring into a service locator and hides dependencies.

Better:

```java
@Service
public class OrderService {
    private final PaymentClient paymentClient;

    public OrderService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }
}
```

Rule:

```text
Use Aware interfaces for infrastructure components.
Avoid them in business services.
```

---

# 10. Phase 5: BeanPostProcessor Before Initialization

A `BeanPostProcessor` can intercept beans before and after initialization.

Concept:

```text
Spring creates bean.
Then processors get a chance to inspect or modify it.
```

Interface:

```java
public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object bean, String beanName);
    Object postProcessAfterInitialization(Object bean, String beanName);
}
```

Example:

```java
@Component
public class LoggingBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (beanName.contains("Service")) {
            System.out.println("Before init: " + beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (beanName.contains("Service")) {
            System.out.println("After init: " + beanName);
        }
        return bean;
    }
}
```

ASCII:

```text
Raw bean
  |
  v
BeanPostProcessor beforeInit
  |
  v
@PostConstruct / init methods
  |
  v
BeanPostProcessor afterInit
```

Important:

```text
Many Spring features are implemented using post-processors.
```

Examples:

```text
@Autowired processing
@ConfigurationProperties binding
@PostConstruct detection
AOP proxy creation
@Transactional proxy creation
```

So when you use annotations, remember:

```text
Annotation is metadata.
Post-processor is one of the workers that acts on metadata.
```

---

# 11. Phase 6: Initialization Callbacks

Initialization is where the bean can run setup logic after dependencies are injected.

Common options:

```text
@PostConstruct
InitializingBean.afterPropertiesSet()
custom initMethod
```

Recommended for most apps:

```java
@Component
public class CacheWarmup {
    private final ProductRepository productRepository;

    public CacheWarmup(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void load() {
        System.out.println("Dependencies are injected. Can run setup.");
    }
}
```

Lifecycle:

```text
constructor
   |
   v
dependencies injected
   |
   v
@PostConstruct
```

ASCII:

```text
new CacheWarmup(repository)
      |
      v
repository field available
      |
      v
@PostConstruct runs
```

But be careful.

`@PostConstruct` runs during application startup.

Bad example:

```java
@PostConstruct
public void warmAllCaches() {
    // Loads millions of rows and blocks startup
}
```

Problem:

```text
Application startup becomes slow.
Kubernetes readiness may be delayed.
Deployment may timeout.
DB may be overloaded during rolling deploy.
```

Better options:

```text
Warm small critical cache only.
Move heavy warmup to ApplicationReadyEvent.
Make warmup async and observable.
Use readiness state if traffic must wait.
```

---

# 12. Phase 7: After Initialization And Proxy Creation

After initialization, BeanPostProcessors run again.

This is where proxies are commonly created.

Example:

```java
@Service
public class PaymentService {
    @Transactional
    public void charge() {
        // business logic
    }
}
```

Spring may expose not the raw object, but a proxy object.

ASCII:

```text
Raw PaymentService object
        |
        v
AOP BeanPostProcessor
        |
        v
Proxy object
        |
        v
ApplicationContext stores proxy as bean
```

Runtime call:

```text
Controller
   |
   v
PaymentService proxy
   |
   +-- open transaction
   +-- call real PaymentService.charge()
   +-- commit/rollback
```

Important:

```text
The bean you inject may be a proxy, not the original class instance.
```

This explains:

```text
@Transactional
@Cacheable
@Async
@Retryable
method security
```

These usually work through proxy/wrapper behavior.

The lifecycle gives birth to the proxy after the object exists.

---

# 13. Phase 8: Ready Bean

After initialization and post-processing, the bean is ready.

It is stored in the `ApplicationContext`.

Other beans can inject it.

Controllers can receive requests.

Scheduled jobs may run.

Listeners may consume messages.

ASCII:

```text
ApplicationContext
   |
   +-- orderController
   +-- checkoutService
   +-- orderRepository
   +-- dataSource
   +-- transactionManager
   +-- kafkaListenerContainer
```

But understand the exact meaning of ready:

```text
Bean ready != whole application ready
```

A bean may be initialized before:

```text
web server is accepting traffic
all runners have completed
ApplicationReadyEvent is published
readiness probe is UP
external systems are healthy
```

Startup has multiple layers:

```text
Bean created
   |
   v
ApplicationContext refreshed
   |
   v
ApplicationRunner / CommandLineRunner
   |
   v
ApplicationReadyEvent
   |
   v
Readiness UP
   |
   v
Traffic allowed
```

Do not confuse bean lifecycle with full application readiness.

---

# 14. Phase 9: Destruction Callback

When the Spring context shuts down, singleton beans can receive destroy callbacks.

Common options:

```text
@PreDestroy
DisposableBean.destroy()
custom destroyMethod
AutoCloseable.close()
```

Example:

```java
@Component
public class ReportFileWriter {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
        System.out.println("Executor shutting down");
    }
}
```

ASCII:

```text
SIGTERM / app shutdown
        |
        v
Spring context closes
        |
        v
destroy callbacks
        |
        +-- close pools
        +-- stop schedulers
        +-- stop listeners
        +-- release resources
```

Production importance:

```text
If you create resources manually, you must close them.
```

Examples:

```text
custom thread pools
file handles
Netty clients
HTTP clients
database-like connections
local caches with background threads
message consumers
```

If you use Spring-managed infrastructure, Spring usually handles cleanup.

If you create it yourself, you own the cleanup.

---

# 15. Full Lifecycle With Java Example

Bean:

```java
@Component
public class LifecycleDemoBean implements BeanNameAware, InitializingBean, DisposableBean {

    private final PaymentClient paymentClient;

    public LifecycleDemoBean(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
        System.out.println("1. Constructor: object created");
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("2. BeanNameAware: " + name);
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("3. @PostConstruct: dependencies available");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("4. InitializingBean.afterPropertiesSet");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("5. @PreDestroy: cleanup");
    }

    @Override
    public void destroy() {
        System.out.println("6. DisposableBean.destroy");
    }
}
```

Expected startup/shutdown idea:

```text
Startup:
constructor
aware callbacks
post-process before init
@PostConstruct
afterPropertiesSet
custom init
post-process after init
ready

Shutdown:
@PreDestroy
DisposableBean.destroy
custom destroy
```

ASCII:

```text
STARTUP
  |
  v
Constructor
  |
  v
Dependency injection
  |
  v
Aware callbacks
  |
  v
Before-init post processors
  |
  v
@PostConstruct
  |
  v
afterPropertiesSet
  |
  v
After-init post processors
  |
  v
Bean ready
```

```text
SHUTDOWN
  |
  v
@PreDestroy
  |
  v
DisposableBean.destroy
  |
  v
custom destroy method
  |
  v
resource released
```

In production code, do not use every callback.

Use the simplest mechanism needed.

---

# 16. The Most Useful Lifecycle Hooks

For normal backend developers, the highest ROI hooks are:

```text
Constructor
@PostConstruct
@PreDestroy
ApplicationRunner
CommandLineRunner
ApplicationReadyEvent
SmartLifecycle for advanced start/stop components
```

Mental model:

```text
Constructor:
dependencies are being assigned; avoid heavy logic.

@PostConstruct:
dependencies are available; small setup is okay.

ApplicationRunner:
context is started; run startup tasks.

ApplicationReadyEvent:
application is ready; do work after startup completes.

@PreDestroy:
release resources during shutdown.

SmartLifecycle:
control start/stop order for infrastructure beans.
```

ASCII:

```text
Constructor
   |
   v
@PostConstruct
   |
   v
Context refresh complete
   |
   v
ApplicationRunner / CommandLineRunner
   |
   v
ApplicationReadyEvent
   |
   v
Traffic / runtime
   |
   v
@PreDestroy
```

Rule of thumb:

```text
Do small dependency-based setup in @PostConstruct.
Do external heavy startup work after context startup.
Do cleanup in @PreDestroy.
```

---

# 17. @PostConstruct: Good And Bad Uses

Good use:

```java
@Component
public class CurrencyCodeValidator {
    private Set<String> supported;

    @PostConstruct
    void init() {
        supported = Set.of("USD", "EUR", "INR");
    }

    public boolean isSupported(String code) {
        return supported.contains(code);
    }
}
```

This is fine:

```text
small
local
fast
deterministic
no network
no huge DB scan
```

Bad use:

```java
@PostConstruct
void init() {
    externalPaymentApi.callHealthEndpoint();
    loadTenMillionRowsIntoCache();
    publishKafkaEvent();
}
```

Problems:

```text
startup depends on external system
deployments become fragile
slow startup causes rollout failures
side effects may happen before readiness
no retry/observability strategy
```

Better:

```java
@Component
public class CacheWarmupOnReady {
    private final ProductCacheWarmer warmer;

    public CacheWarmupOnReady(ProductCacheWarmer warmer) {
        this.warmer = warmer;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warm() {
        warmer.warmSmallCriticalCache();
    }
}
```

Even better for heavy warmup:

```text
warm asynchronously
record metrics
limit concurrency
have fallback behavior
do not block all startup unless required
```

---

# 18. @PreDestroy: Real Production Importance

Imagine a bean creates a custom executor.

```java
@Component
public class InvoiceAsyncProcessor {
    private final ExecutorService executor = Executors.newFixedThreadPool(8);

    public void process(Runnable task) {
        executor.submit(task);
    }
}
```

Problem:

```text
If executor is not shut down, app may hang or lose tasks badly during shutdown.
```

Fix:

```java
@Component
public class InvoiceAsyncProcessor {
    private final ExecutorService executor = Executors.newFixedThreadPool(8);

    public void process(Runnable task) {
        executor.submit(task);
    }

    @PreDestroy
    public void stop() {
        executor.shutdown();
    }
}
```

Better Spring-native approach:

```java
@Configuration
public class ExecutorConfig {
    @Bean(destroyMethod = "shutdown")
    public ExecutorService invoiceExecutor() {
        return Executors.newFixedThreadPool(8);
    }
}
```

ASCII:

```text
Kubernetes rolling update
       |
       v
SIGTERM sent to pod
       |
       v
Spring context closes
       |
       v
@PreDestroy / destroyMethod
       |
       v
executor shutdown
       |
       v
pod exits cleanly
```

In production, shutdown is part of correctness.

---

# 19. Bean Scopes And Lifecycle

The default scope is singleton.

```text
singleton = one bean instance per ApplicationContext
```

Other common scopes:

```text
prototype = new instance each time requested
request   = one per HTTP request
session   = one per HTTP session
application = one per ServletContext
```

ASCII:

```text
Singleton

ApplicationContext
   |
   +-- one CheckoutService shared
```

```text
Prototype

getBean()
   |
   +-- new object
getBean()
   |
   +-- another new object
```

Important lifecycle difference:

```text
Spring fully manages singleton lifecycle.
Spring creates prototype beans but does not manage full destruction automatically.
```

Example:

```java
@Component
@Scope("prototype")
public class ExportJob {
    @PreDestroy
    public void cleanup() {
        System.out.println("May not be called automatically for prototype");
    }
}
```

Production lesson:

```text
Avoid prototype beans with resources unless you clearly own cleanup.
```

Most backend services should be singleton.

Use request scope only when request-specific state is truly needed.

Avoid mutable state in singleton services.

---

# 20. Singleton Beans And Thread Safety

Spring singleton does not mean one per JVM globally.

It means:

```text
one instance per Spring ApplicationContext
```

Since web apps handle many requests concurrently, singleton beans must be thread-safe.

Bad:

```java
@Service
public class BadOrderService {
    private String currentOrderId;

    public void process(String orderId) {
        this.currentOrderId = orderId;
        // another request can overwrite this
    }
}
```

ASCII:

```text
Request A ----+
              v
        Same singleton service
              ^
Request B ----+
```

Problem:

```text
Mutable per-request state stored inside singleton bean.
Requests corrupt each other.
```

Good:

```java
@Service
public class OrderService {
    public void process(String orderId) {
        String currentOrderId = orderId;
        // local variable is request-safe
    }
}
```

Rule:

```text
Singleton service fields should usually be dependencies or immutable config.
Per-request data should be method-local or request-scoped.
```

This is one of the most important lifecycle-related production lessons.

---

# 21. Circular Dependency Lifecycle Problem

Circular dependency:

```java
@Service
class A {
    A(B b) {}
}

@Service
class B {
    B(A a) {}
}
```

Graph:

```text
A needs B
B needs A
```

ASCII:

```text
Create A
  |
  v
Need B
  |
  v
Create B
  |
  v
Need A
  |
  v
A is not ready yet
```

This commonly fails with constructor injection.

Error:

```text
BeanCurrentlyInCreationException
```

Do not solve circular dependencies by randomly using lazy injection.

Usually it means your design is tangled.

Better design:

```text
Extract shared responsibility into C
```

Before:

```text
A <----> B
```

After:

```text
A ---> C
B ---> C
```

Example:

```java
@Service
class OrderService {
    private final PricingService pricingService;
}

@Service
class PricingService {
    private final DiscountPolicy discountPolicy;
}
```

If `PricingService` also needs `OrderService`, ask:

```text
Is there a domain service or repository abstraction missing?
Are responsibilities mixed?
Should event publishing decouple them?
```

Circular dependency is not only a Spring error.

It is a design smell.

---

# 22. Lifecycle And @Transactional

A common trap:

```java
@Service
public class StartupLoader {
    @PostConstruct
    public void init() {
        loadData();
    }

    @Transactional
    public void loadData() {
        // expected transaction
    }
}
```

This may not behave as expected.

Why?

```text
@PostConstruct runs during bean initialization.
Self-invocation calls this.loadData().
The call may not go through the transactional proxy.
```

ASCII:

```text
Spring initializing raw bean
      |
      v
@PostConstruct
      |
      v
this.loadData()
      |
      v
raw object method
      |
      x
proxy not involved
```

Better:

```java
@Component
public class StartupLoader {
    private final DataLoaderService dataLoaderService;

    public StartupLoader(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        dataLoaderService.loadData();
    }
}

@Service
class DataLoaderService {
    @Transactional
    public void loadData() {
        // transaction works when called through proxy
    }
}
```

Mental model:

```text
Proxy-based features need calls to enter through the proxy.
Initialization code often runs on the raw bean.
```

This explains many real bugs.

---

# 23. Lifecycle And @Async

Another trap:

```java
@Component
public class ReportStarter {
    @PostConstruct
    public void init() {
        generateAsync();
    }

    @Async
    public void generateAsync() {
        // expected async
    }
}
```

Problem:

```text
Self-invocation bypasses the @Async proxy.
Method may run synchronously.
```

Bad flow:

```text
@PostConstruct
   |
   v
this.generateAsync()
   |
   v
same object
   |
   x
no async proxy
```

Better:

```java
@Component
public class ReportStarter {
    private final ReportJobService reportJobService;

    public ReportStarter(ReportJobService reportJobService) {
        this.reportJobService = reportJobService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        reportJobService.generateAsync();
    }
}

@Service
public class ReportJobService {
    @Async
    public void generateAsync() {
        // runs on async executor
    }
}
```

Rule:

```text
@Transactional, @Async, @Cacheable, @Retryable usually require proxy entry.
Do not call them through this.method().
```

---

# 24. Lifecycle And External Systems

Backend beans often depend on external systems:

```text
PostgreSQL
Redis
Kafka
RabbitMQ
Elasticsearch
S3
Payment API
Service discovery
Config server
```

Bad startup pattern:

```java
@PostConstruct
void init() {
    paymentClient.ping();
    redisTemplate.opsForValue().get("startup-key");
    kafkaTemplate.send("app-started", "started");
}
```

Problems:

```text
external dependency outage can prevent app startup
startup side effects can be duplicated during retries
rolling deploy can overload dependency
partial startup becomes hard to reason about
```

Better decision model:

```text
Is the dependency required for serving traffic?
    yes -> health/readiness should reflect it
    no  -> app can start, degrade gracefully

Is startup work idempotent?
    yes -> safe to retry
    no  -> avoid doing it automatically

Is work heavy?
    yes -> background job, controlled concurrency, metrics
    no  -> maybe okay
```

ASCII:

```text
Startup task
   |
   +-- small local setup -> @PostConstruct okay
   |
   +-- required dependency check -> readiness health
   |
   +-- heavy warmup -> ApplicationReadyEvent / async worker
   |
   +-- business side effect -> avoid or make idempotent
```

Lifecycle is not only a coding detail.

It is an operational design decision.

---

# 25. BeanFactoryPostProcessor vs BeanPostProcessor

Two names sound similar.

They work at different levels.

```text
BeanFactoryPostProcessor:
acts on bean definitions before beans are created.

BeanPostProcessor:
acts on bean instances after objects are created.
```

ASCII:

```text
BeanDefinition stage
      |
      v
BeanFactoryPostProcessor
      |
      v
Object creation
      |
      v
BeanPostProcessor
      |
      v
Ready bean
```

Analogy:

```text
BeanFactoryPostProcessor = modifies recipe before cooking
BeanPostProcessor        = modifies dish after cooking
```

Example idea:

```text
PropertySourcesPlaceholderConfigurer resolves ${...} placeholders.
ConfigurationClassPostProcessor processes @Configuration classes.
AutowiredAnnotationBeanPostProcessor injects @Autowired dependencies.
AnnotationAwareAspectJAutoProxyCreator creates AOP proxies.
```

You do not need to implement these often.

But knowing they exist helps explain how Spring does so much work from annotations.

---

# 26. FactoryBean: Bean That Creates Another Bean

A `FactoryBean` is a special Spring contract.

Normal bean:

```text
getBean("x") returns object x
```

FactoryBean:

```text
getBean("x") returns product created by factory
getBean("&x") returns the factory itself
```

ASCII:

```text
FactoryBean
    |
    v
creates product object
    |
    v
ApplicationContext exposes product as bean
```

Real examples:

```text
JPA EntityManagerFactory
MyBatis mapper factories
proxy factories
client SDK factories
```

Simple example:

```java
@Component("paymentClient")
public class PaymentClientFactoryBean implements FactoryBean<PaymentClient> {

    @Override
    public PaymentClient getObject() {
        return new PaymentClient("https://payment-api");
    }

    @Override
    public Class<?> getObjectType() {
        return PaymentClient.class;
    }
}
```

Most application developers rarely write FactoryBeans.

But many frameworks use them.

Mental model:

```text
Sometimes the bean definition does not directly map to a simple new object.
Spring can ask a factory to produce the bean.
```

---

# 27. @Bean Methods And Lifecycle

Not all beans come from `@Component`.

You can define beans with `@Bean`.

```java
@Configuration
public class HttpClientConfig {

    @Bean(destroyMethod = "close")
    public CloseableHttpClient httpClient() {
        return HttpClients.createDefault();
    }
}
```

Lifecycle:

```text
Spring calls @Bean method
   |
   v
receives object
   |
   v
injects dependencies if needed
   |
   v
initialization callbacks
   |
   v
bean is ready
   |
   v
destroyMethod on shutdown
```

ASCII:

```text
@Configuration class
      |
      v
@Bean method
      |
      v
object returned
      |
      v
Spring-managed bean
```

Important:

```text
Objects returned from @Bean methods are Spring beans.
```

Good use cases:

```text
third-party clients
custom ObjectMapper
ExecutorService
RestClient/WebClient
Clock
configuration-heavy infrastructure objects
```

Example:

```java
@Bean
public Clock clock() {
    return Clock.systemUTC();
}
```

Now services can inject `Clock`, making time testable.

---

# 28. Lazy Beans

By default, singleton beans are usually created eagerly during startup.

Lazy bean:

```java
@Component
@Lazy
public class HeavyReportGenerator {
}
```

Meaning:

```text
Do not create this bean during startup.
Create it when first requested.
```

ASCII:

```text
Startup
   |
   +-- normal beans created
   |
   +-- lazy bean skipped
              |
              v
       first use creates bean
```

Pros:

```text
faster startup
avoid creating rarely used components
can break some startup dependency pressure
```

Cons:

```text
first request may be slow
errors appear at runtime instead of startup
can hide broken configuration until traffic hits
```

Production rule:

```text
Use lazy loading intentionally.
Do not use it to hide broken dependencies.
```

For core services, fail-fast startup is usually better.

---

# 29. SmartLifecycle For Advanced Start/Stop

`SmartLifecycle` is used when a component must start and stop with the application context.

Example use cases:

```text
message listener containers
background workers
custom schedulers
network servers
connection managers
```

Simplified example:

```java
@Component
public class CustomWorker implements SmartLifecycle {
    private volatile boolean running = false;

    @Override
    public void start() {
        running = true;
        System.out.println("Worker started");
    }

    @Override
    public void stop() {
        running = false;
        System.out.println("Worker stopped");
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
```

Lifecycle position:

```text
context refresh
   |
   v
start lifecycle beans
   |
   v
application running
   |
   v
stop lifecycle beans during shutdown
```

ASCII:

```text
Spring context
   |
   +-- start phase 0 beans
   +-- start phase 100 beans
   +-- start phase 1000 beans
```

Phases allow ordering:

```text
lower phase starts earlier
higher phase stops earlier
```

Most developers do not need `SmartLifecycle` daily.

But it explains how Spring controls infrastructure startup and shutdown.

---

# 30. ApplicationRunner vs CommandLineRunner

These run after the application context is loaded.

`CommandLineRunner`:

```java
@Component
public class StartupCommandLineRunner implements CommandLineRunner {
    @Override
    public void run(String... args) {
        System.out.println("CommandLineRunner runs after context startup");
    }
}
```

`ApplicationRunner`:

```java
@Component
public class StartupApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        System.out.println(args.getOptionNames());
    }
}
```

Difference:

```text
CommandLineRunner gets raw String[] args.
ApplicationRunner gets parsed ApplicationArguments.
```

ASCII:

```text
Beans initialized
   |
   v
Context refreshed
   |
   v
CommandLineRunner / ApplicationRunner
   |
   v
ApplicationReadyEvent
```

Use cases:

```text
validate startup assumptions
run small bootstrap tasks
log important startup info
seed dev-only data
```

Be careful:

```text
Long runner blocks application readiness.
Failure in runner can fail startup.
```

---

# 31. ApplicationReadyEvent

`ApplicationReadyEvent` means Spring Boot considers the app ready.

Example:

```java
@Component
public class ReadyLogger {
    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        System.out.println("Application is ready");
    }
}
```

Mental model:

```text
ApplicationReadyEvent happens later than @PostConstruct.
```

ASCII:

```text
@PostConstruct
   |
   v
Context refresh
   |
   v
Runners
   |
   v
ApplicationReadyEvent
```

Good use:

```text
start non-critical warmup
log final startup status
trigger background jobs
notify monitoring
```

Avoid:

```text
dangerous business side effects
non-idempotent writes
huge blocking tasks
```

In Kubernetes, readiness/liveness must still be configured correctly.

`ApplicationReadyEvent` is an app event.

It is not a full replacement for operational health probes.

---

# 32. Real-World Production Story: Slow Startup From @PostConstruct

A team adds this:

```java
@PostConstruct
void warmCache() {
    productRepository.findAll().forEach(cache::put);
}
```

In dev, product table has 500 rows.

In production, product table has 20 million rows.

Deployment result:

```text
pods take 12 minutes to start
rollout exceeds deadline
database CPU spikes
old pods terminate before new pods are ready
users see errors
```

ASCII:

```text
New Pod
  |
  v
@PostConstruct warmup
  |
  +-- huge DB scan
  +-- cache memory spike
  +-- startup blocked
  |
  v
readiness delayed / rollout failed
```

Better design:

```text
warm only top N hot items
load lazily on miss
use background warmup with rate limiting
monitor warmup progress
keep readiness independent if service can degrade
avoid findAll for large tables
```

Production lesson:

```text
@PostConstruct is not a place for uncontrolled production data scans.
```

---

# 33. Real-World Production Story: Bean Created But Proxy Expected

A developer logs the class:

```java
@Service
public class PaymentService {
    @Transactional
    public void pay() {}
}
```

In runtime:

```java
System.out.println(paymentService.getClass());
```

Output may be:

```text
class com.example.PaymentService$$SpringCGLIB$$0
```

Developer thinks:

```text
Why is this not my class?
```

Answer:

```text
Spring exposed a proxy object.
The proxy wraps the real object to apply transaction behavior.
```

ASCII:

```text
Injected dependency
      |
      v
Proxy class
      |
      v
Real PaymentService
```

Production importance:

```text
Debugging stack traces may show proxy classes.
Method visibility/finality can affect proxy behavior.
Self-invocation bypasses proxy behavior.
```

Interview memory:

```text
Spring bean lifecycle can end with a different object being exposed than the raw object created by constructor.
```

---

# 34. Real-World Production Story: Shutdown Lost Messages

A service consumes Kafka messages and processes invoices.

During rolling update:

```text
pod receives SIGTERM
application exits quickly
in-flight processing stops
some messages are retried
some external calls duplicate
users see duplicate invoice emails
```

Why?

```text
Shutdown did not stop listeners gracefully.
Custom executor was not drained.
Idempotency was missing.
```

Lifecycle-aware design:

```text
stop receiving new messages
finish in-flight messages within timeout
commit offsets only after successful processing
shutdown executors
make external side effects idempotent
```

ASCII:

```text
SIGTERM
  |
  v
Stop accepting new work
  |
  v
Finish in-flight work
  |
  v
Commit/rollback correctly
  |
  v
Close resources
  |
  v
Exit
```

Spring helps with lifecycle, but your design must cooperate.

---

# 35. Debugging Bean Lifecycle Problems

When startup fails, follow the lifecycle stage.

```text
1. Was the class discovered?
2. Was a BeanDefinition created?
3. Did constructor dependency resolution fail?
4. Did property binding fail?
5. Did @PostConstruct throw?
6. Did a BeanPostProcessor fail?
7. Did proxy creation fail?
8. Did a runner fail?
9. Did readiness stay DOWN?
10. Did destroy callback fail during shutdown?
```

Common errors:

```text
NoSuchBeanDefinitionException
UnsatisfiedDependencyException
BeanCurrentlyInCreationException
BeanCreationException
BeanInitializationException
ConfigurationPropertiesBindException
NoUniqueBeanDefinitionException
IllegalStateException from @PostConstruct
```

Commands:

```bash
java -jar app.jar --debug

curl localhost:8080/actuator/beans
curl localhost:8080/actuator/health
curl localhost:8080/actuator/conditions
curl localhost:8080/actuator/configprops
curl localhost:8080/actuator/env
```

Spring Boot config for actuator:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,beans,conditions,configprops,env,metrics
```

Use carefully in production.

Some endpoints expose sensitive details.

Restrict them with security.

---

# 36. Startup Timeline: Spring Boot Mental Model

A simplified Spring Boot startup:

```text
main()
  |
  v
SpringApplication.run()
  |
  v
create ApplicationContext
  |
  v
load BeanDefinitions
  |
  v
run BeanFactoryPostProcessors
  |
  v
register BeanPostProcessors
  |
  v
create singleton beans
  |
  v
inject dependencies
  |
  v
initialize beans
  |
  v
create proxies
  |
  v
refresh context complete
  |
  v
start embedded server / lifecycle beans
  |
  v
run ApplicationRunner / CommandLineRunner
  |
  v
publish ApplicationReadyEvent
```

ASCII:

```text
main
 |
 v
SpringApplication
 |
 v
ApplicationContext
 |
 v
BeanFactory
 |
 v
BeanDefinitions
 |
 v
Singleton Beans
 |
 v
PostProcessors / Proxies
 |
 v
Runners
 |
 v
Ready
```

This timeline is enough for most interviews and production debugging.

You do not need to memorize every internal method.

Know the flow.

---

# 37. Practical Example: Payment Client Bean

Configuration:

```yaml
payment:
  base-url: https://payment.company.com
  timeout-ms: 2000
```

Properties:

```java
@ConfigurationProperties(prefix = "payment")
public record PaymentProperties(
        String baseUrl,
        int timeoutMs
) {}
```

Client:

```java
public class PaymentClient implements AutoCloseable {
    private final String baseUrl;
    private final int timeoutMs;

    public PaymentClient(String baseUrl, int timeoutMs) {
        this.baseUrl = baseUrl;
        this.timeoutMs = timeoutMs;
    }

    public void charge(String orderId) {
        System.out.println("Charging " + orderId + " via " + baseUrl);
    }

    @Override
    public void close() {
        System.out.println("Closing payment client");
    }
}
```

Bean config:

```java
@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {

    @Bean(destroyMethod = "close")
    public PaymentClient paymentClient(PaymentProperties properties) {
        return new PaymentClient(
                properties.baseUrl(),
                properties.timeoutMs()
        );
    }
}
```

Lifecycle:

```text
bind payment properties
   |
   v
call @Bean paymentClient()
   |
   v
register PaymentClient as bean
   |
   v
inject into services
   |
   v
close on shutdown
```

ASCII:

```text
application.yml
      |
      v
PaymentProperties
      |
      v
PaymentConfig.paymentClient()
      |
      v
PaymentClient bean
      |
      v
CheckoutService
```

This is a clean lifecycle-aware design.

---

# 38. Practical Example: Avoid Heavy Work In Constructor

Bad:

```java
@Service
public class ProductService {
    private final Map<Long, Product> cache = new HashMap<>();

    public ProductService(ProductRepository repository) {
        repository.findAll().forEach(product -> cache.put(product.id(), product));
    }
}
```

Problems:

```text
constructor does heavy DB work
object creation becomes slow
testing becomes hard
startup failure source is confusing
cache load has no metrics
```

Better:

```java
@Service
public class ProductService {
    private final ProductRepository repository;
    private final Map<Long, Product> cache = new ConcurrentHashMap<>();

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    void initSmallCache() {
        repository.findTop100HotProducts()
                .forEach(product -> cache.put(product.id(), product));
    }
}
```

Even better for large warmup:

```java
@Component
public class ProductCacheWarmup {
    private final ProductService productService;

    public ProductCacheWarmup(ProductService productService) {
        this.productService = productService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warm() {
        productService.warmAsync();
    }
}
```

Rule:

```text
Constructor should assign dependencies.
Initialization should be explicit and controlled.
```

---

# 39. Practical Example: Request Scope

Most services are singleton.

But sometimes request scope helps.

Example:

```java
@Component
@RequestScope
public class RequestContext {
    private final String correlationId;

    public RequestContext(HttpServletRequest request) {
        this.correlationId = request.getHeader("X-Correlation-Id");
    }

    public String correlationId() {
        return correlationId;
    }
}
```

Usage:

```java
@Service
public class AuditService {
    private final RequestContext requestContext;

    public AuditService(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    public void audit(String action) {
        System.out.println(requestContext.correlationId() + " " + action);
    }
}
```

Mental model:

```text
RequestContext is created per HTTP request.
AuditService is singleton.
Spring injects a proxy that resolves the current request's RequestContext.
```

ASCII:

```text
Singleton AuditService
        |
        v
RequestContext proxy
        |
        v
actual RequestContext for current HTTP request
```

Production caution:

```text
Request scope outside a web request can fail.
Prefer explicit parameters for simple context passing.
Use MDC for logging correlation IDs.
```

---

# 40. Common Beginner Mistakes

```text
Mistake 1:
Putting heavy DB scans in constructors.
Correct:
Constructors should assign dependencies only.

Mistake 2:
Putting huge warmups in @PostConstruct.
Correct:
Use controlled warmup after readiness or background workers.

Mistake 3:
Assuming @PostConstruct means app is ready.
Correct:
It only means this bean's dependencies are injected.

Mistake 4:
Calling @Transactional method from @PostConstruct in same class.
Correct:
Call transactional method through another proxied bean.

Mistake 5:
Keeping request-specific data in singleton fields.
Correct:
Use method-local variables, request scope, or explicit context.

Mistake 6:
Ignoring @PreDestroy for manually created resources.
Correct:
Close custom executors, clients, and file handles.

Mistake 7:
Using ApplicationContext.getBean inside business code.
Correct:
Prefer constructor injection.

Mistake 8:
Using lazy beans to hide broken startup.
Correct:
Fail fast for core dependencies.

Mistake 9:
Thinking bean ready means app ready.
Correct:
Application readiness is broader than bean initialization.

Mistake 10:
Treating circular dependency as a Spring problem only.
Correct:
Fix the design graph.
```

---

# 41. Interview Questions

## What is the Spring bean lifecycle?

The Spring bean lifecycle is the process by which Spring reads bean metadata, creates the object, injects dependencies, invokes lifecycle callbacks, applies post-processors and proxies, exposes the bean for use, and destroys it during shutdown.

## What is a BeanDefinition?

A BeanDefinition is metadata describing how Spring should create and manage a bean. It includes class, scope, constructor information, factory method, init/destroy methods, and other configuration.

## What is the difference between object and bean?

An object is any Java instance. A bean is an object created, configured, wired, post-processed, tracked, and managed by Spring.

## When does @PostConstruct run?

`@PostConstruct` runs after the bean has been constructed and dependencies have been injected, but before the bean is fully considered ready for normal application use.

## Is @PostConstruct the same as application ready?

No. `@PostConstruct` is per-bean initialization. Application readiness happens later after the application context is refreshed, runners have executed, and the application is ready to serve traffic.

## Why can @Transactional fail inside @PostConstruct?

Because `@PostConstruct` runs during bean initialization and self-invocation may call the raw object directly instead of going through the transactional proxy.

## What is BeanPostProcessor?

A BeanPostProcessor intercepts bean instances before and after initialization. It is used by Spring infrastructure to process annotations and create proxies.

## What is BeanFactoryPostProcessor?

A BeanFactoryPostProcessor modifies bean definitions before bean instances are created.

## Why can the injected bean be a proxy?

Spring may wrap a bean with a proxy to apply cross-cutting behavior such as transactions, caching, async execution, retries, or security.

## What is the default bean scope?

The default scope is singleton, meaning one bean instance per Spring ApplicationContext.

## Are singleton beans thread-safe automatically?

No. Singleton only means one instance per context. Developers must avoid mutable request-specific state inside singleton beans.

## What happens to prototype bean destruction?

Spring creates prototype beans but does not automatically manage their full destruction lifecycle like singleton beans. The caller must handle cleanup if needed.

## When should @PreDestroy be used?

Use `@PreDestroy` to release resources created by the bean, such as custom executors, file handles, clients, or background workers.

## What is SmartLifecycle?

SmartLifecycle is an advanced interface for beans that need controlled start and stop behavior with phase ordering during context startup and shutdown.

## Constructor injection or field injection?

Prefer constructor injection because dependencies are explicit, immutable, testable, and required at object creation.

---

# 42. Cheat Sheet

```text
Bean lifecycle:
metadata -> instantiate -> inject -> aware -> before init -> init -> after init/proxy -> ready -> destroy
```

```text
BeanDefinition:
blueprint/recipe for creating bean
```

```text
Instantiation:
Spring creates raw object
```

```text
Dependency injection:
Spring provides required collaborators
```

```text
Aware callbacks:
Spring gives container metadata to bean
```

```text
BeanPostProcessor:
acts on bean instance before/after initialization
```

```text
BeanFactoryPostProcessor:
acts on bean definitions before object creation
```

```text
@PostConstruct:
small setup after dependencies are injected
```

```text
@PreDestroy:
cleanup before bean is destroyed
```

```text
ApplicationRunner / CommandLineRunner:
run after context startup
```

```text
ApplicationReadyEvent:
app is considered ready by Spring Boot
```

```text
Singleton:
one bean per ApplicationContext
```

```text
Prototype:
new bean per request to container, destruction not fully managed
```

```text
Proxy:
wrapper object used for transactions, async, caching, retries, security
```

Production rules:

```text
[ ] Keep constructors lightweight.
[ ] Prefer constructor injection.
[ ] Avoid mutable request state in singleton beans.
[ ] Use @PostConstruct only for small deterministic setup.
[ ] Do not put huge DB scans in startup hooks.
[ ] Do not rely on @Transactional self-invocation.
[ ] Use @PreDestroy for manual resources.
[ ] Treat circular dependencies as design smell.
[ ] Understand that injected beans may be proxies.
[ ] Use readiness probes for production traffic safety.
```

---

# 43. One Picture To Remember

```text
Spring Bean Lifecycle

Class + Annotation
        |
        v
+-----------------------+
| BeanDefinition        |
| blueprint / metadata  |
+-----------+-----------+
            |
            v
+-----------------------+
| Instantiate           |
| constructor called    |
+-----------+-----------+
            |
            v
+-----------------------+
| Dependency Injection  |
| collaborators wired   |
+-----------+-----------+
            |
            v
+-----------------------+
| Aware Callbacks       |
| bean name/context etc |
+-----------+-----------+
            |
            v
+-----------------------+
| Before Init BPP       |
| annotation workers    |
+-----------+-----------+
            |
            v
+-----------------------+
| Initialization        |
| @PostConstruct etc    |
+-----------+-----------+
            |
            v
+-----------------------+
| After Init BPP        |
| proxy may be created  |
+-----------+-----------+
            |
            v
+-----------------------+
| Ready Bean            |
| injectable/useable    |
+-----------+-----------+
            |
            v
+-----------------------+
| Destruction           |
| @PreDestroy/close     |
+-----------------------+
```

Memory hook:

```text
Spring does not simply call new.

Spring manufactures beans through a lifecycle assembly line:
blueprint -> build -> wire -> initialize -> wrap -> use -> cleanup.
```

---

# 44. Final Production Checklist

```text
[ ] I can explain the difference between object and bean.
[ ] I can draw the bean lifecycle from metadata to destruction.
[ ] I know BeanDefinition is the blueprint, not the object.
[ ] I understand constructor injection creates complete objects.
[ ] I know when @PostConstruct runs.
[ ] I know why @PostConstruct is not application readiness.
[ ] I know why @Transactional can fail during self-invocation.
[ ] I understand BeanPostProcessor creates/changes bean instances.
[ ] I understand BeanFactoryPostProcessor changes definitions before creation.
[ ] I know why injected beans may be proxies.
[ ] I know singleton beans must be thread-safe.
[ ] I know prototype destruction is not fully managed.
[ ] I know when to use @PreDestroy.
[ ] I can debug startup errors by lifecycle phase.
[ ] I can explain circular dependency as a graph/design problem.
```

---

# 45. Final Sentence

Do not memorize bean lifecycle callback names.

Remember the factory:

```text
Spring turns class metadata into production-ready managed objects by constructing, wiring, initializing, wrapping, exposing, and safely destroying beans.
```

Once you see this lifecycle, Spring stops feeling like annotation magic and starts looking like a predictable object graph assembly line.
