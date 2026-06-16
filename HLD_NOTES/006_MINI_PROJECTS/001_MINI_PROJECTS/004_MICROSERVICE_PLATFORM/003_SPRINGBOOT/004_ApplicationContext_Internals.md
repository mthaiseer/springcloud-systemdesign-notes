# 004_ApplicationContext_Internals.md

> MiniSpringBoot Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Production Debugging

---

# 1. Why This Chapter Exists

Most developers use Spring Boot like this:

```java
@SpringBootApplication
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
```

Then they add:

```java
@Service
@Repository
@RestController
@Configuration
@Bean
@Transactional
```

The application starts, APIs work, database calls happen, and everything feels automatic.

But production bugs usually begin when the “automatic” part is not understood.

You see errors like:

```text
NoSuchBeanDefinitionException
UnsatisfiedDependencyException
BeanCurrentlyInCreationException
NoUniqueBeanDefinitionException
BeanCreationException
BeanNotOfRequiredTypeException
@Transactional not working
Application started but wrong bean used
Profile-specific bean missing
Circular dependency detected
```

These are not random Spring errors.

They are signals from the `ApplicationContext`.

The real question is not:

```text
What is ApplicationContext definition?
```

The real question is:

```text
How does Spring turn classes + annotations + configuration into a running object graph?
```

One picture:

```text
Your Classes
    |
    | annotations, @Bean methods, config, profiles
    v
Bean Definitions
    |
    | metadata: class, scope, dependencies, conditions
    v
ApplicationContext
    |
    | instantiate, inject, initialize, proxy, publish events
    v
Running Spring Application
```

This chapter teaches the container from the inside.

Do not memorize Spring internals as names.

Remember the factory mental model:

```text
ApplicationContext is the factory + registry + lifecycle manager + event hub
for your Spring application objects.
```

---

# 2. The Big Mental Model

A Spring application is not mainly a collection of annotations.

It is a managed object graph.

```text
Controller
   |
   v
Service
   |
   +--> Repository
   |
   +--> PaymentClient
   |
   +--> EventPublisher
```

Plain Java object graph:

```java
PaymentClient paymentClient = new StripePaymentClient();
OrderRepository repository = new JdbcOrderRepository(dataSource);
OrderService service = new OrderService(repository, paymentClient);
OrderController controller = new OrderController(service);
```

Spring object graph:

```text
Declare:
  OrderController needs OrderService
  OrderService needs OrderRepository and PaymentClient

Container:
  creates PaymentClient
  creates OrderRepository
  creates OrderService
  creates OrderController
  stores them as beans
```

ASCII:

```text
+-----------------------+
| Source Code           |
| @Service, @Bean, etc. |
+-----------+-----------+
            |
            v
+-----------------------+
| BeanDefinition        |
| "recipe" for bean     |
+-----------+-----------+
            |
            v
+-----------------------+
| BeanFactory           |
| creates beans         |
+-----------+-----------+
            |
            v
+-----------------------+
| ApplicationContext    |
| BeanFactory + extras  |
+-----------+-----------+
            |
            v
+-----------------------+
| Ready Beans           |
| singleton registry    |
+-----------------------+
```

Important distinction:

```text
BeanDefinition = recipe
Bean instance   = actual object
ApplicationContext = machine that reads recipes and produces managed objects
```

If you understand this difference, many Spring bugs become easy.

---

# 3. Real World Analogy: Factory + Warehouse + Control Room

Imagine a company that manufactures and ships products.

```text
Blueprints       = BeanDefinitions
Raw materials    = classes, configuration, constructor arguments
Assembly line    = BeanFactory
Quality checks   = BeanPostProcessors
Packaging        = AOP proxies
Warehouse        = singleton bean registry
Control room     = ApplicationContext
Shipping events  = ApplicationEvents
Shutdown process = destroy callbacks
```

Diagram:

```text
Blueprints
    |
    v
Factory builds product
    |
    v
Quality inspection
    |
    v
Optional wrapping/package
    |
    v
Warehouse stores final product
    |
    v
Customer receives product
```

Spring equivalent:

```text
BeanDefinition
    |
    v
Instantiate bean
    |
    v
Dependency injection
    |
    v
Aware callbacks / init methods
    |
    v
BeanPostProcessors
    |
    v
Proxy maybe created
    |
    v
Singleton cache
    |
    v
Application uses bean
```

Do not memorize each interface first.

Remember the story:

```text
Spring does not just call new.
Spring builds, configures, enhances, stores, and shuts down objects.
```

---

# 4. What ApplicationContext Really Is

`ApplicationContext` is the central Spring container interface.

It extends the basic bean container idea with production application features.

Simple mental model:

```text
BeanFactory        = object creation and dependency injection engine
ApplicationContext = BeanFactory + application-level services
```

ApplicationContext provides:

```text
Bean lookup
Bean creation and wiring
Bean lifecycle management
Resource loading
Environment and profiles
Message resolution
Application event publishing
Internationalization support
Integration with post-processors
```

ASCII:

```text
+------------------------------------------------+
| ApplicationContext                             |
|                                                |
|  +----------------------+                      |
|  | BeanFactory           | create/wire beans   |
|  +----------------------+                      |
|                                                |
|  Environment             profiles/properties   |
|  ResourceLoader          load classpath/files  |
|  EventPublisher          publish events        |
|  MessageSource           i18n messages         |
|  LifecycleProcessor      start/stop beans      |
+------------------------------------------------+
```

In most Spring Boot apps, you do not manually create it.

Spring Boot creates it when you call:

```java
SpringApplication.run(OrderApplication.class, args);
```

That call returns an `ApplicationContext`:

```java
ConfigurableApplicationContext context =
        SpringApplication.run(OrderApplication.class, args);

OrderService service = context.getBean(OrderService.class);
```

Usually you should not call `getBean()` inside business code.

But it helps for understanding:

```text
getBean(OrderService.class)
means:
  "ApplicationContext, give me the managed OrderService object."
```

---

# 5. ApplicationContext vs BeanFactory

A common interview question:

```text
What is the difference between BeanFactory and ApplicationContext?
```

Do not answer as memorized theory.

Answer through responsibility.

```text
BeanFactory:
  basic container
  knows bean definitions
  creates beans
  injects dependencies
  manages scope

ApplicationContext:
  full application container
  includes BeanFactory
  adds environment, events, resources, messages, lifecycle integration
  eagerly creates singleton beans by default in normal Spring apps
```

Diagram:

```text
BeanFactory
   |
   | basic object graph engine
   v
ApplicationContext
   |
   | object graph + application services
   v
Production Spring app container
```

Code mental model:

```java
ApplicationContext context =
        SpringApplication.run(App.class, args);

OrderService orderService = context.getBean(OrderService.class);
```

Internally:

```text
ApplicationContext delegates bean creation to an internal BeanFactory.
```

Production meaning:

```text
When a bean fails during startup, the low-level failure often happens inside BeanFactory,
but ApplicationContext reports startup failure because the application cannot be built.
```

---

# 6. The Startup Chain: From main() To Ready App

Spring Boot startup is a pipeline.

```text
main()
  |
  v
SpringApplication.run()
  |
  v
Create ApplicationContext
  |
  v
Prepare Environment
  |
  v
Load bean definitions
  |
  v
Refresh ApplicationContext
  |
  v
Instantiate singleton beans
  |
  v
Start embedded server
  |
  v
ApplicationReadyEvent
```

ASCII:

```text
+------------------------------+
| main()                       |
+--------------+---------------+
               |
               v
+------------------------------+
| SpringApplication.run()      |
+--------------+---------------+
               |
               v
+------------------------------+
| Create ApplicationContext    |
+--------------+---------------+
               |
               v
+------------------------------+
| Read config/environment      |
+--------------+---------------+
               |
               v
+------------------------------+
| Register BeanDefinitions     |
+--------------+---------------+
               |
               v
+------------------------------+
| refresh()                    |
| the big container build step |
+--------------+---------------+
               |
               v
+------------------------------+
| Beans created and wired      |
+--------------+---------------+
               |
               v
+------------------------------+
| Web server ready             |
+------------------------------+
```

The most important word:

```text
refresh()
```

`refresh()` is the big lifecycle method where the container moves from “prepared metadata” to “running context”.

You do not need to memorize every internal method.

Remember:

```text
Before refresh:
  Spring knows many recipes.

During refresh:
  Spring validates, enhances, and creates beans.

After refresh:
  the application object graph is ready.
```

---

# 7. BeanDefinition: The Recipe, Not The Object

When Spring scans this class:

```java
@Service
public class OrderService {
    private final PaymentClient paymentClient;

    public OrderService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }
}
```

Spring does not immediately think only:

```text
I have an OrderService object.
```

First, it thinks:

```text
I have a recipe for creating an OrderService bean.
```

A simplified bean definition contains:

```text
bean name
bean class
scope
constructor arguments
factory method if @Bean
lazy/eager flag
primary flag
qualifiers
init method
destroy method
conditions
role
```

ASCII:

```text
@Service class
      |
      v
+-----------------------------+
| BeanDefinition              |
| name: orderService          |
| class: OrderService         |
| scope: singleton            |
| dependencies: PaymentClient |
| lazy: false                 |
+-----------------------------+
      |
      v
Actual object created later
```

Why this matters:

```text
Spring can modify bean definitions before objects are created.
```

This is how auto-configuration, configuration properties, component scanning, and many framework extensions work.

Container thinking:

```text
First collect all recipes.
Then let processors adjust recipes.
Then create objects from final recipes.
```

---

# 8. How Component Scan Creates BeanDefinitions

When you use:

```java
@SpringBootApplication
public class OrderApplication {}
```

It includes component scanning from the package of `OrderApplication` downward.

Simplified:

```text
com.company.order
 ├── OrderApplication
 ├── controller
 │    └── OrderController
 ├── service
 │    └── OrderService
 └── repository
      └── OrderRepository
```

If `OrderApplication` is in `com.company.order`, Spring scans:

```text
com.company.order.*
```

It finds stereotype annotations:

```java
@Component
@Service
@Repository
@Controller
@RestController
@Configuration
```

ASCII:

```text
Base package
    |
    v
ClassPath scanner
    |
    +-- finds @Service OrderService
    +-- finds @Repository OrderRepository
    +-- finds @RestController OrderController
    |
    v
BeanDefinition registry
```

Production bug:

```text
Application class placed in wrong package.
Component scan does not reach service package.
Bean missing at runtime.
```

Bad structure:

```text
com.company.app.OrderApplication

com.company.service.OrderService
```

If scan starts at `com.company.app`, it may not scan `com.company.service`.

Better:

```text
com.company.OrderApplication

com.company.app.*
com.company.service.*
com.company.repository.*
```

Mental model:

```text
If Spring did not scan it, Spring cannot manage it.
```

---

# 9. @Bean Methods: Manual Recipes Inside Spring

Not every bean comes from component scanning.

Sometimes you create a bean explicitly:

```java
@Configuration
public class PaymentConfig {

    @Bean
    public PaymentClient paymentClient(PaymentProperties properties) {
        return new StripePaymentClient(properties.baseUrl(), properties.timeout());
    }
}
```

This means:

```text
The method is a factory recipe.
Spring should call this method and store the returned object as a bean.
```

ASCII:

```text
@Configuration class
        |
        v
@Bean method
        |
        v
BeanDefinition:
  name = paymentClient
  factory method = paymentClient()
        |
        v
Spring calls method when bean needed
```

Why use `@Bean`?

```text
Third-party classes
Complex construction
Library clients
Objects needing custom setup
Infrastructure beans
```

Example:

```java
@Bean
public RestClient paymentRestClient(RestClient.Builder builder,
                                    PaymentProperties properties) {
    return builder
            .baseUrl(properties.baseUrl())
            .build();
}
```

Rule:

```text
@Component is for your own class.
@Bean is often for objects you construct/configure explicitly.
```

Both become bean definitions.

That is the unifying model.

---

# 10. Bean Naming Mental Model

Spring gives each bean a name.

Default component name:

```java
@Service
public class OrderService {}
```

Bean name:

```text
orderService
```

Class name with first letter lowercased.

Explicit name:

```java
@Service("checkoutService")
public class OrderService {}
```

`@Bean` method name:

```java
@Bean
public PaymentClient paymentClient() {
    return new StripePaymentClient();
}
```

Bean name:

```text
paymentClient
```

ASCII:

```text
Bean Registry
+----------------------+--------------------------+
| bean name            | bean instance            |
+----------------------+--------------------------+
| orderService         | OrderService object      |
| paymentClient        | StripePaymentClient obj  |
| orderRepository      | OrderRepository object   |
+----------------------+--------------------------+
```

Why names matter:

```text
Qualifiers use names.
Actuator /beans displays names.
Duplicate names can override/fail depending on settings.
```

Example:

```java
@Service("stripePaymentClient")
public class StripePaymentClient implements PaymentClient {}

@Service("mockPaymentClient")
public class MockPaymentClient implements PaymentClient {}
```

Injection with qualifier:

```java
@Service
public class OrderService {
    public OrderService(@Qualifier("stripePaymentClient")
                        PaymentClient paymentClient) {
    }
}
```

Remember:

```text
Types decide most injections.
Names help disambiguate.
```

---

# 11. Dependency Resolution: How Constructor Injection Works

Given:

```java
@Service
public class OrderService {
    private final PaymentClient paymentClient;
    private final OrderRepository orderRepository;

    public OrderService(PaymentClient paymentClient,
                        OrderRepository orderRepository) {
        this.paymentClient = paymentClient;
        this.orderRepository = orderRepository;
    }
}
```

Spring reads constructor parameters:

```text
OrderService needs:
  PaymentClient
  OrderRepository
```

Container process:

```text
create OrderService?
  need PaymentClient
    create/find PaymentClient
  need OrderRepository
    create/find OrderRepository
  call constructor
```

ASCII:

```text
getBean(OrderService)
       |
       v
Need PaymentClient? ----> getBean(PaymentClient)
       |
       v
Need OrderRepository? --> getBean(OrderRepository)
       |
       v
new OrderService(paymentClient, orderRepository)
```

If exactly one bean matches each type, injection succeeds.

If none exists:

```text
NoSuchBeanDefinitionException
```

If multiple exist:

```text
NoUniqueBeanDefinitionException
```

Example problem:

```java
@Component
class StripePaymentClient implements PaymentClient {}

@Component
class RazorpayPaymentClient implements PaymentClient {}

@Service
class OrderService {
    OrderService(PaymentClient paymentClient) {}
}
```

Spring asks:

```text
Which PaymentClient?
```

Fix options:

```java
@Primary
@Component
class StripePaymentClient implements PaymentClient {}
```

or:

```java
OrderService(@Qualifier("razorpayPaymentClient")
             PaymentClient paymentClient) {}
```

Mental model:

```text
Autowiring is type matching with rules.
It is not guessing business intent.
```

---

# 12. Singleton Scope Does Not Mean Java Singleton Pattern

Default Spring scope is singleton.

But it does not mean the class uses the Gang of Four Singleton pattern.

Spring singleton means:

```text
One bean instance per ApplicationContext.
```

ASCII:

```text
ApplicationContext A
  |
  +-- orderService instance #1

ApplicationContext B
  |
  +-- orderService instance #2
```

Inside one context:

```text
getBean(OrderService.class) -> same object every time
```

Example:

```java
OrderService a = context.getBean(OrderService.class);
OrderService b = context.getBean(OrderService.class);

System.out.println(a == b); // true
```

Why singleton default?

```text
Most services are stateless.
Repositories are thread-safe proxies.
Controllers are stateless request handlers.
Creating them once is efficient.
```

Danger:

```java
@Service
public class BadCartService {
    private final List<String> cartItems = new ArrayList<>();

    public void add(String item) {
        cartItems.add(item);
    }
}
```

This is dangerous because:

```text
Singleton bean shared by all users and all threads.
Mutable per-user state leaks between requests.
```

Correct:

```text
Keep singleton beans stateless.
Store request/user state in method parameters, database, session, or request-scoped objects only when truly needed.
```

Production memory hook:

```text
Spring singleton is a shared worker, not a personal notebook.
```

---

# 13. Bean Lifecycle: Full Story

A bean is not just constructed.

It passes through lifecycle stages.

Simplified lifecycle:

```text
1. BeanDefinition exists
2. Instantiate object
3. Populate dependencies
4. Aware callbacks
5. BeanPostProcessor before init
6. Init callback
7. BeanPostProcessor after init
8. Proxy may be returned
9. Bean ready
10. Destroy callback on shutdown
```

ASCII:

```text
BeanDefinition
      |
      v
Constructor called
      |
      v
Dependencies injected
      |
      v
Aware callbacks
      |
      v
@PostConstruct
      |
      v
BeanPostProcessors
      |
      v
Proxy wrapping maybe
      |
      v
Ready bean in context
      |
      v
@PreDestroy on shutdown
```

Example:

```java
@Component
public class CacheWarmupService {

    public CacheWarmupService() {
        System.out.println("constructor");
    }

    @PostConstruct
    public void init() {
        System.out.println("init after dependencies are ready");
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("cleanup before app stops");
    }
}
```

Important:

```text
Constructor:
  dependencies are being set.
  avoid heavy external calls.

@PostConstruct:
  dependencies are injected.
  useful for lightweight validation/init.

ApplicationReadyEvent:
  whole app is ready.
  better for startup tasks that need full context.
```

---

# 14. BeanPostProcessor: The Hidden Extension Point

Many Spring features work because of post-processors.

Mental model:

```text
A BeanPostProcessor gets a chance to inspect or wrap beans during creation.
```

Simplified interface:

```java
public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object bean, String beanName);
    Object postProcessAfterInitialization(Object bean, String beanName);
}
```

ASCII:

```text
Raw bean
   |
   v
PostProcessor before init
   |
   v
init method
   |
   v
PostProcessor after init
   |
   v
final bean returned to app
```

Examples of things powered by processors:

```text
@Autowired processing
@PostConstruct handling
@ConfigurationProperties binding support
AOP proxy creation
@Transactional proxy creation
@Async proxy creation
```

You normally do not write BeanPostProcessors daily.

But knowing they exist explains Spring magic.

Example custom debug processor:

```java
@Component
public class BeanLoggingPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (beanName.contains("order")) {
            System.out.println("Initialized bean: " + beanName
                    + " -> " + bean.getClass().getName());
        }
        return bean;
    }
}
```

Production caution:

```text
Custom post-processors are powerful and dangerous.
They run for many beans.
A slow post-processor slows application startup.
A buggy one can break the whole context.
```

---

# 15. BeanFactoryPostProcessor: Modify Recipes Before Objects Exist

There are two different moments:

```text
Before objects exist:
  modify BeanDefinitions

After objects exist:
  modify/wrap bean instances
```

`BeanFactoryPostProcessor` works before bean creation.

ASCII:

```text
BeanDefinitions loaded
        |
        v
BeanFactoryPostProcessors run
        |
        v
Definitions may be changed
        |
        v
Beans are instantiated
```

This is important because:

```text
Some framework features need to change metadata before objects are created.
```

Example conceptual use:

```java
@Component
public class BeanDefinitionDebugPostProcessor
        implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) {

        int count = beanFactory.getBeanDefinitionCount();
        System.out.println("Bean definitions count = " + count);
    }
}
```

Real Spring Boot uses many internal processors.

Mental model:

```text
BeanFactoryPostProcessor edits blueprints.
BeanPostProcessor edits/wraps products.
```

Factory analogy:

```text
Blueprint editor before manufacturing.
Quality inspector after manufacturing.
```

---

# 16. refresh(): The Big Container Build Method

`ApplicationContext.refresh()` is the heart of startup.

You rarely call it manually in Spring Boot, but you must understand its meaning.

Simplified refresh flow:

```text
prepare context
prepare bean factory
load bean definitions
invoke bean factory post-processors
register bean post-processors
initialize message source
initialize event multicaster
create web server if web app
instantiate singleton beans
publish context refreshed event
```

ASCII:

```text
refresh()
  |
  +--> prepare environment
  +--> prepare bean factory
  +--> run BeanFactoryPostProcessors
  +--> register BeanPostProcessors
  +--> setup events/messages
  +--> create non-lazy singletons
  +--> finish refresh
```

Production meaning:

```text
If refresh fails, application startup fails.
```

Common failure points:

```text
Cannot bind configuration
Cannot resolve dependency
Cannot connect required datasource
Circular dependency
Bean init method throws exception
Port already used
Invalid proxy creation
```

Good debugging question:

```text
Did failure happen while creating bean definitions,
while resolving dependencies,
while initializing a bean,
or while starting web server?
```

This narrows the problem quickly.

---

# 17. Lazy vs Eager Bean Creation

By default, singleton beans are created eagerly during context startup.

Meaning:

```text
Application fails fast if object graph is broken.
```

Example:

```java
@Service
public class OrderService {
    public OrderService(MissingDependency missingDependency) {}
}
```

Startup fails immediately.

This is usually good.

Lazy bean:

```java
@Lazy
@Service
public class HeavyReportService {
}
```

Meaning:

```text
Do not create this bean during startup.
Create it when first requested.
```

ASCII:

```text
Eager singleton:
startup -> create bean now -> fail fast if broken

Lazy singleton:
startup -> skip bean now
first use -> create bean -> may fail later
```

Global lazy initialization:

```yaml
spring:
  main:
    lazy-initialization: true
```

Production caution:

```text
Lazy startup can hide broken dependencies until real traffic hits the endpoint.
```

Use lazy only when you know why.

Mental model:

```text
Eager startup pays cost early and catches errors early.
Lazy startup defers cost and may defer failure.
```

---

# 18. Circular Dependencies

Circular dependency means:

```text
A needs B
B needs A
```

Example:

```java
@Service
public class OrderService {
    public OrderService(InvoiceService invoiceService) {}
}

@Service
public class InvoiceService {
    public InvoiceService(OrderService orderService) {}
}
```

ASCII:

```text
OrderService
     |
     v
InvoiceService
     |
     v
OrderService
```

Spring tries:

```text
create OrderService
  needs InvoiceService
    create InvoiceService
      needs OrderService
        but OrderService is not ready
```

Result:

```text
BeanCurrentlyInCreationException
```

Old Spring versions sometimes allowed some circular references with setter injection.

Modern Spring Boot encourages failing fast.

Do not fix by randomly adding `@Lazy`.

Better design:

```text
Extract shared responsibility.
Use domain events.
Split orchestration from low-level services.
Remove two-way service dependency.
```

Refactor:

```java
@Service
public class CheckoutService {
    private final OrderService orderService;
    private final InvoiceService invoiceService;

    public CheckoutService(OrderService orderService,
                           InvoiceService invoiceService) {
        this.orderService = orderService;
        this.invoiceService = invoiceService;
    }

    public void checkout(String orderId) {
        orderService.create(orderId);
        invoiceService.createForOrder(orderId);
    }
}
```

Diagram:

```text
CheckoutService
   |
   +--> OrderService
   |
   +--> InvoiceService
```

Memory hook:

```text
Circular dependency is usually a design smell, not an annotation problem.
```

---

# 19. Profiles And Conditional Beans

Spring does not always create every bean.

It uses conditions and profiles.

Example:

```java
@Component
@Profile("dev")
public class FakePaymentClient implements PaymentClient {
    public void charge(String orderId) {
        System.out.println("fake charge");
    }
}
```

```java
@Component
@Profile("prod")
public class StripePaymentClient implements PaymentClient {
    public void charge(String orderId) {
        System.out.println("real charge");
    }
}
```

ASCII:

```text
Active profile = dev
   |
   +--> create FakePaymentClient
   +--> skip StripePaymentClient

Active profile = prod
   |
   +--> create StripePaymentClient
   +--> skip FakePaymentClient
```

Activate profile:

```bash
java -jar app.jar --spring.profiles.active=prod
```

or:

```yaml
spring:
  profiles:
    active: prod
```

Conditional bean example:

```java
@Bean
@ConditionalOnProperty(
        name = "payment.provider",
        havingValue = "stripe"
)
PaymentClient stripePaymentClient() {
    return new StripePaymentClient();
}
```

Production bug:

```text
Expected bean does not exist because profile/condition did not match.
```

Debug:

```bash
java -jar app.jar --debug
```

Actuator:

```bash
curl localhost:8080/actuator/conditions
curl localhost:8080/actuator/beans
```

Mental model:

```text
Not every recipe becomes active.
Profiles and conditions decide which recipes enter the factory.
```

---

# 20. Environment: Where Configuration Comes From

ApplicationContext has access to `Environment`.

Configuration can come from:

```text
application.yml
application.properties
profile-specific files
environment variables
command-line arguments
system properties
config server
Kubernetes ConfigMaps/Secrets
```

Simplified priority idea:

```text
Command line can override application.yml.
Environment variables often override packaged defaults.
Profile files override base files for active profile.
```

Example:

```yaml
payment:
  timeout-ms: 2000
  provider: stripe
```

Typed binding:

```java
@ConfigurationProperties(prefix = "payment")
public record PaymentProperties(
        int timeoutMs,
        String provider
) {}
```

Enable scanning:

```java
@SpringBootApplication
@ConfigurationPropertiesScan
public class OrderApplication {}
```

Use:

```java
@Service
public class PaymentClient {
    private final PaymentProperties properties;

    public PaymentClient(PaymentProperties properties) {
        this.properties = properties;
    }
}
```

ASCII:

```text
application.yml / env vars
          |
          v
Environment
          |
          v
ConfigurationProperties binder
          |
          v
PaymentProperties bean
          |
          v
PaymentClient
```

Production mindset:

```text
Do not hide configuration inside constructors.
Let environment feed the context.
Let typed properties feed beans.
```

---

# 21. Application Events

ApplicationContext can publish events.

Spring itself publishes lifecycle events:

```text
ApplicationStartingEvent
ApplicationEnvironmentPreparedEvent
ApplicationPreparedEvent
ContextRefreshedEvent
ApplicationStartedEvent
ApplicationReadyEvent
ContextClosedEvent
```

Most useful in business apps:

```text
ApplicationReadyEvent
ContextClosedEvent
custom domain/application events
```

Example:

```java
@Component
public class StartupLogger {

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        System.out.println("Application is ready to receive traffic");
    }
}
```

Custom event:

```java
public record OrderCreatedEvent(String orderId) {}
```

Publisher:

```java
@Service
public class OrderService {
    private final ApplicationEventPublisher events;

    public OrderService(ApplicationEventPublisher events) {
        this.events = events;
    }

    public void createOrder(String orderId) {
        // save order
        events.publishEvent(new OrderCreatedEvent(orderId));
    }
}
```

Listener:

```java
@Component
public class EmailListener {

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        System.out.println("Send email for " + event.orderId());
    }
}
```

ASCII:

```text
OrderService
    |
    | publish OrderCreatedEvent
    v
ApplicationContext event multicaster
    |
    +--> EmailListener
    +--> AuditListener
    +--> MetricsListener
```

Caution:

```text
Default event listeners are usually synchronous.
If listener fails, it may affect publisher flow.
For reliable distributed events, use Kafka/outbox, not only in-memory Spring events.
```

---

# 22. Resource Loading

ApplicationContext can load resources.

Examples:

```text
classpath:templates/email.html
file:/opt/app/config/rules.json
https://example.com/config.json
```

Code:

```java
@Component
public class EmailTemplateLoader {
    private final ResourceLoader resourceLoader;

    public EmailTemplateLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String loadTemplate() throws IOException {
        Resource resource = resourceLoader.getResource(
                "classpath:templates/order-created.html"
        );
        return resource.getContentAsString(StandardCharsets.UTF_8);
    }
}
```

ASCII:

```text
ResourceLoader
   |
   +--> classpath resource
   +--> file system resource
   +--> URL resource
```

Why it matters:

```text
Spring abstracts where resources come from.
```

Production caution:

```text
Classpath resources inside a jar may not behave like normal files.
Use Resource streams instead of assuming File paths.
```

Bad:

```java
new File("src/main/resources/templates/email.html")
```

This works in IDE but fails in packaged jar.

Better:

```java
resourceLoader.getResource("classpath:templates/email.html")
```

Mental model:

```text
ApplicationContext is also your app's resource access gateway.
```

---

# 23. AOP Proxy Creation Inside Context

When you use:

```java
@Transactional
public void checkout() {}
```

Spring often creates a proxy.

The bean registered for injection may not be the raw object.

It may be:

```text
Proxy object -> real target object
```

ASCII:

```text
Caller
  |
  v
CheckoutService proxy
  |
  +-- begin transaction
  |
  v
Real CheckoutService.checkout()
  |
  +-- business logic
  |
  v
Proxy commits/rolls back
```

Bean lifecycle with proxy:

```text
Raw CheckoutService object
       |
       v
BeanPostProcessor sees @Transactional
       |
       v
Create proxy wrapping target
       |
       v
Context exposes proxy as bean
```

This explains:

```text
@Transactional private methods do not work in normal proxy model.
Self-invocation bypasses proxy.
Final classes/methods can cause proxy limitations depending on proxy type.
```

Example failure:

```java
@Service
public class UserService {

    public void outer() {
        inner(); // call stays inside same object
    }

    @Transactional
    public void inner() {
        // transaction may not start when called by outer()
    }
}
```

Why:

```text
External call:
  OtherBean -> proxy -> inner()

Self call:
  this.inner() -> real object directly
```

Fix:

```text
Move transactional method to another bean.
Call through injected bean.
Put transaction boundary on public external method.
```

---

# 24. @Configuration Class Proxy Mental Model

This code looks simple:

```java
@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public JsonService jsonService() {
        return new JsonService(objectMapper());
    }
}
```

Naive thinking:

```text
jsonService() calls objectMapper(), so new ObjectMapper each time?
```

Spring full `@Configuration` classes are enhanced so `@Bean` method calls return managed singleton beans.

ASCII:

```text
AppConfig class
    |
    v
Spring creates enhanced proxy subclass
    |
    v
objectMapper() call intercepted
    |
    v
return singleton objectMapper bean
```

This protects singleton semantics.

However, modern Spring also supports lighter configuration style with `proxyBeanMethods = false`:

```java
@Configuration(proxyBeanMethods = false)
public class AppConfig {
}
```

Meaning:

```text
Do not proxy @Bean method calls.
Use when @Bean methods do not call each other directly.
```

Production rule:

```text
Prefer method parameter injection in @Bean methods.
```

Better:

```java
@Bean
public JsonService jsonService(ObjectMapper objectMapper) {
    return new JsonService(objectMapper);
}
```

This avoids relying on direct method call interception.

Mental model:

```text
@Configuration is not just a class with methods.
It can be a factory configuration class enhanced by Spring.
```

---

# 25. Spring Boot Auto-Configuration And ApplicationContext

Spring Boot auto-configuration means:

```text
Spring Boot registers bean definitions automatically based on classpath, properties, and missing beans.
```

Example:

If you include:

```xml
spring-boot-starter-web
```

Spring Boot can configure:

```text
DispatcherServlet
HandlerMapping
Jackson ObjectMapper
Tomcat/Jetty/Undertow embedded server
MVC infrastructure
Error handling
```

If you include:

```xml
spring-boot-starter-data-jpa
```

Spring Boot can configure:

```text
DataSource
EntityManagerFactory
TransactionManager
JpaRepositories
Hibernate integration
```

ASCII:

```text
Classpath + Properties + Existing Beans
              |
              v
AutoConfiguration conditions
              |
              v
Register additional BeanDefinitions
              |
              v
ApplicationContext creates infrastructure beans
```

Example condition logic:

```text
If DataSource class exists
and datasource properties exist
and user did not define DataSource bean
then create DataSource bean
```

This is why adding a dependency can change startup behavior.

Debug auto-configuration:

```bash
java -jar app.jar --debug
```

Actuator:

```bash
curl localhost:8080/actuator/conditions
```

Memory hook:

```text
Boot does not magically run libraries.
Boot contributes bean recipes to ApplicationContext.
```

---

# 26. Parent And Child ApplicationContexts

Most simple apps have one context.

But Spring can have parent-child contexts.

Mental model:

```text
Child can see parent beans.
Parent cannot see child beans.
```

ASCII:

```text
Parent Context
  |
  +-- dataSource
  +-- commonConfig
  |
  v
Child Web Context
  |
  +-- controllers
  +-- webMvcConfig
```

Older Spring MVC apps often had:

```text
Root context:
  services, repositories, infrastructure

Web context:
  controllers, MVC beans
```

Spring Boot usually hides this complexity.

Why know it?

```text
Some advanced apps, tests, or frameworks create multiple contexts.
A bean may exist in one context but not another.
```

Debug question:

```text
Which ApplicationContext owns this bean?
```

For most Boot backend developers:

```text
One context is the normal mental model.
Parent-child matters in advanced debugging.
```

---

# 27. WebApplicationContext And Embedded Server

A Spring Boot web app uses a web-specific ApplicationContext.

For servlet apps, commonly:

```text
AnnotationConfigServletWebServerApplicationContext
```

It creates both:

```text
Spring beans
embedded servlet web server
```

Startup flow:

```text
ApplicationContext refresh
    |
    v
create MVC infrastructure
    |
    v
create DispatcherServlet
    |
    v
start embedded Tomcat
    |
    v
register servlet mappings
```

ASCII:

```text
HTTP request
    |
    v
Embedded Tomcat
    |
    v
DispatcherServlet bean
    |
    v
HandlerMapping
    |
    v
Controller bean
    |
    v
Service bean
```

Important:

```text
Tomcat is not outside Spring Boot in the usual jar deployment.
Spring Boot starts embedded Tomcat as part of application context startup.
```

Production failure:

```text
Port 8080 already in use.
Context may create beans successfully,
but web server startup fails.
```

Error category:

```text
Object graph is okay.
Network/server binding failed.
```

Debug:

```bash
lsof -i :8080
java -jar app.jar --server.port=8081
```

---

# 28. The Singleton Registry And Three-Level Cache Idea

During bean creation, Spring tracks singleton beans.

Simplified caches:

```text
fully initialized singletons
early singleton references
singleton factories
```

You do not need to memorize implementation details deeply.

But the mental model helps with circular dependency discussions.

ASCII:

```text
Creating Bean A
   |
   +-- expose early reference maybe
   |
   v
Bean A needs Bean B
   |
   v
Creating Bean B
   |
   +-- Bean B needs Bean A
   |
   v
Can Spring provide early A?
```

Constructor circular dependencies are hard/impossible because:

```text
A cannot be constructed without B.
B cannot be constructed without A.
No partially constructed object can satisfy constructor requirement safely.
```

Setter/field cycles historically could sometimes be resolved because:

```text
Object can be constructed first,
then dependencies injected later.
```

But good design avoids cycles.

Production mindset:

```text
Do not rely on early references to make bad graphs work.
Fix the graph.
```

---

# 29. Why Field Injection Is Weak

Field injection:

```java
@Service
public class OrderService {

    @Autowired
    private PaymentClient paymentClient;
}
```

Problems:

```text
Dependencies hidden
Object can be created in invalid state
Harder to test with plain Java
Encourages circular dependencies
No final fields
```

Constructor injection:

```java
@Service
public class OrderService {
    private final PaymentClient paymentClient;

    public OrderService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }
}
```

Benefits:

```text
Dependencies explicit
Immutable fields
Easy unit testing
Fail fast
Clear object graph
```

ASCII:

```text
Constructor injection:
OrderService needs PaymentClient before it can exist

Field injection:
OrderService exists first, dependency patched later
```

ApplicationContext mental model:

```text
Constructor injection gives the container a clean graph.
Field injection lets objects exist half-built.
```

Production rule:

```text
Use constructor injection for application services.
```

---

# 30. Debugging Missing Bean Errors

Error:

```text
No qualifying bean of type 'PaymentClient' available
```

Think like ApplicationContext.

Ask:

```text
Was the class scanned?
Is it annotated or registered with @Bean?
Is the active profile correct?
Did @ConditionalOnProperty match?
Is the bean type correct?
Is it in another ApplicationContext?
Did bean creation fail earlier?
```

ASCII debugging ladder:

```text
Class exists?
   |
   v
Scanned package?
   |
   v
BeanDefinition registered?
   |
   v
Condition/profile matched?
   |
   v
Bean created successfully?
   |
   v
Injectable by requested type?
```

Commands:

```bash
java -jar app.jar --debug

curl localhost:8080/actuator/beans
curl localhost:8080/actuator/conditions
curl localhost:8080/actuator/env
```

Code check:

```java
@SpringBootApplication(scanBasePackages = "com.company")
public class OrderApplication {}
```

Better package design usually removes need for explicit scanBasePackages.

Mental model:

```text
No bean means the recipe never existed,
was skipped,
failed to build,
or does not match the requested type.
```

---

# 31. Debugging Multiple Bean Errors

Error:

```text
NoUniqueBeanDefinitionException:
expected single matching bean but found 2
```

Example:

```java
@Component
class StripePaymentClient implements PaymentClient {}

@Component
class PaypalPaymentClient implements PaymentClient {}
```

Injection:

```java
OrderService(PaymentClient paymentClient) {}
```

Spring asks:

```text
Which implementation should I inject?
```

Fix with `@Primary`:

```java
@Primary
@Component
class StripePaymentClient implements PaymentClient {}
```

Fix with `@Qualifier`:

```java
OrderService(@Qualifier("paypalPaymentClient")
             PaymentClient paymentClient) {}
```

Fix with collection injection:

```java
@Service
public class PaymentRouter {
    private final Map<String, PaymentClient> clients;

    public PaymentRouter(Map<String, PaymentClient> clients) {
        this.clients = clients;
    }
}
```

ASCII:

```text
PaymentClient type
    |
    +-- stripePaymentClient
    +-- paypalPaymentClient

Single injection needs one winner.
Collection injection can receive all.
```

Production design:

```text
If multiple implementations are valid, model selection explicitly.
Do not depend on accidental bean names.
```

---

# 32. Debugging BeanCreationException

`BeanCreationException` means:

```text
Spring found the bean recipe but failed while creating the object.
```

Possible causes:

```text
Constructor threw exception
@PostConstruct threw exception
Configuration binding failed
External dependency check failed
Invalid @Bean method
Proxy creation failed
Factory method threw exception
```

Example:

```java
@Bean
public PaymentClient paymentClient(PaymentProperties properties) {
    if (properties.baseUrl() == null) {
        throw new IllegalStateException("payment.base-url missing");
    }
    return new PaymentClient(properties.baseUrl());
}
```

Startup:

```text
BeanCreationException: Error creating bean with name 'paymentClient'
Caused by: IllegalStateException: payment.base-url missing
```

Debugging rule:

```text
Always read the deepest caused-by exception.
```

ASCII:

```text
Top-level:
Application failed to start

Middle:
BeanCreationException paymentClient

Root cause:
payment.base-url missing
```

Production mindset:

```text
Spring's top error is often wrapper context.
The useful error is near the bottom.
```

---

# 33. Production Story: Bean Missing Due To Package Layout

A team creates:

```text
com.company.app.OrderApplication
com.company.payment.StripePaymentClient
```

`OrderApplication`:

```java
@SpringBootApplication
public class OrderApplication {}
```

`OrderService` needs:

```java
public OrderService(PaymentClient paymentClient) {}
```

Startup fails:

```text
No qualifying bean of type PaymentClient
```

Why?

```text
Component scan starts from com.company.app.
com.company.payment is outside the scan tree.
```

Bad tree:

```text
com.company
 ├── app
 │    └── OrderApplication
 └── payment
      └── StripePaymentClient
```

Scan from:

```text
com.company.app
```

Does not include:

```text
com.company.payment
```

Fix:

```text
Move OrderApplication to com.company
```

Better tree:

```text
com.company
 ├── OrderApplication
 ├── payment
 ├── order
 └── user
```

Lesson:

```text
ApplicationContext can only manage classes it discovers or is explicitly told about.
```

---

# 34. Production Story: Wrong Bean Chosen In Payment System

There are two clients:

```java
@Component
@Profile("prod")
class StripePaymentClient implements PaymentClient {}

@Component
@Profile("dev")
class FakePaymentClient implements PaymentClient {}
```

Staging starts with:

```bash
--spring.profiles.active=dev
```

Orders are accepted but no real payment authorization happens.

Why?

```text
ApplicationContext created the dev bean because active profile was dev.
Business code was correct.
Container configuration was wrong.
```

Debug:

```bash
curl localhost:8080/actuator/env | grep profiles
curl localhost:8080/actuator/beans | grep PaymentClient
```

Better safety:

```java
@Component
@Profile("local")
class FakePaymentClient implements PaymentClient {}
```

Avoid using `dev` loosely if staging can accidentally use it.

Add startup log:

```java
@Component
public class PaymentClientLogger {

    public PaymentClientLogger(PaymentClient client) {
        System.out.println("Active PaymentClient = "
                + client.getClass().getName());
    }
}
```

Lesson:

```text
In production bugs, ask not only "what does code say?"
Ask "which bean did the context actually create?"
```

---

# 35. Production Story: Startup Slow Because Bean Does Network Call

Bad:

```java
@Component
public class ExchangeRateClient {

    public ExchangeRateClient() {
        // calls external API during construction
    }
}
```

Problem:

```text
ApplicationContext startup waits for network.
If API is slow, deployment is slow.
If API is down, app cannot start.
```

Better:

```java
@Component
public class ExchangeRateClient {

    private final RestClient restClient;

    public ExchangeRateClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public BigDecimal getRate(String currency) {
        return restClient.get()
                .uri("/rates/{currency}", currency)
                .retrieve()
                .body(BigDecimal.class);
    }
}
```

If warmup is needed:

```java
@Component
public class ExchangeRateWarmup {

    @EventListener(ApplicationReadyEvent.class)
    public void warmup() {
        // optional async warmup with timeout and fallback
    }
}
```

Production rule:

```text
Constructors should build objects, not perform unreliable remote operations.
```

ApplicationContext should not be blocked by unnecessary network calls.

---

# 36. Production Story: @PostConstruct Failure Takes Down App

Example:

```java
@Component
public class CacheLoader {

    @PostConstruct
    public void load() {
        throw new RuntimeException("Redis unavailable");
    }
}
```

Result:

```text
BeanCreationException
ApplicationContext refresh fails
Application does not start
```

Sometimes this is correct.

For example:

```text
Missing required encryption key
Invalid database schema
Required config absent
```

Sometimes it is too strict.

For optional cache warmup, better:

```text
Start app
Mark readiness based on critical dependencies
Warm cache with retries
Expose degraded metrics if cache unavailable
```

Use health indicators and readiness probes.

ASCII:

```text
Critical startup validation
  |
  +-- fail fast if app cannot safely run

Optional warmup
  |
  +-- do after ready or asynchronously
  +-- retry/backoff
  +-- do not kill app unnecessarily
```

Mental model:

```text
Anything thrown during bean creation can kill context startup.
Be intentional.
```

---

# 37. Testing With ApplicationContext

Not every test needs Spring.

Plain unit test:

```java
@Test
void checkoutCalculatesTotal() {
    PaymentClient fake = new FakePaymentClient();
    OrderRepository repo = new InMemoryOrderRepository();

    OrderService service = new OrderService(repo, fake);

    service.checkout("order-1");
}
```

No ApplicationContext needed.

Spring integration test:

```java
@SpringBootTest
class OrderApplicationTest {

    @Autowired
    ApplicationContext context;

    @Test
    void contextLoads() {
        assertNotNull(context);
    }
}
```

Slice test:

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {
}
```

Data test:

```java
@DataJpaTest
class OrderRepositoryTest {
}
```

ASCII:

```text
Unit test:
  new Service(fake deps)
  fast, no context

Slice test:
  small part of context
  MVC or JPA only

@SpringBootTest:
  full context
  slower, realistic integration
```

Production mindset:

```text
Use ApplicationContext when testing Spring wiring/integration.
Use plain Java tests when testing business logic.
```

Do not start the full Spring context for every small calculation.

---

# 38. Actuator: Seeing The Context From Outside

Add actuator:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Expose useful endpoints carefully:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,beans,conditions,env,mappings,metrics
```

Useful endpoints:

```text
/actuator/health
/actuator/beans
/actuator/conditions
/actuator/env
/actuator/mappings
/actuator/metrics
```

ASCII:

```text
Running ApplicationContext
      |
      v
Actuator endpoints
      |
      +-- beans: what exists?
      +-- conditions: why auto-config matched/skipped?
      +-- env: what config values are active?
      +-- mappings: what URLs exist?
```

Security warning:

```text
Do not expose sensitive actuator endpoints publicly.
```

Production setup:

```text
Expose health/prometheus publicly only if needed.
Protect beans/env/conditions behind internal network or security.
```

Mental model:

```text
Actuator is the window into the running ApplicationContext.
```

---

# 39. Common ApplicationContext Exceptions

## NoSuchBeanDefinitionException

Meaning:

```text
No bean matched the requested type/name.
```

Likely causes:

```text
Not scanned
Condition/profile skipped
Missing @Bean
Wrong package
Bean creation failed earlier
```

## NoUniqueBeanDefinitionException

Meaning:

```text
Multiple beans matched but one was expected.
```

Fix:

```text
@Primary
@Qualifier
inject collection/map
refactor explicit router
```

## UnsatisfiedDependencyException

Meaning:

```text
Spring cannot satisfy constructor/field/method dependency.
```

Usually wraps missing/multiple/failed dependency.

## BeanCurrentlyInCreationException

Meaning:

```text
Circular dependency or bean requested while still being built.
```

## BeanCreationException

Meaning:

```text
Recipe found, creation failed.
```

Read root cause.

## BeanNotOfRequiredTypeException

Meaning:

```text
Bean name exists, but actual object type differs from expected.
```

Can happen with proxies or wrong bean names.

ASCII failure ladder:

```text
Startup failed
  |
  v
Which bean?
  |
  v
Which dependency?
  |
  v
Which root cause?
```

---

# 40. Debugging Checklist: Think Like The Container

When something fails, walk this path:

```text
1. Is the class in the component scan path?
2. Does it have a stereotype annotation or @Bean method?
3. Is the active profile correct?
4. Did conditions match?
5. Is there exactly one bean for the injected type?
6. Is the bean failing during constructor or init?
7. Is there a circular dependency?
8. Is the bean proxied?
9. Is the method call going through proxy?
10. Is config bound correctly?
11. Is the expected ApplicationContext the one being used?
12. What does actuator /beans show?
13. What does actuator /conditions show?
14. What is the deepest root cause in logs?
```

Command ladder:

```bash
java -jar app.jar --debug

curl localhost:8080/actuator/health
curl localhost:8080/actuator/beans
curl localhost:8080/actuator/conditions
curl localhost:8080/actuator/env
curl localhost:8080/actuator/mappings
```

Log config:

```yaml
logging:
  level:
    org.springframework.beans.factory: DEBUG
    org.springframework.context.annotation: DEBUG
```

Use DEBUG carefully because logs can be huge.

Mental hook:

```text
Spring startup errors are graph-building errors.
Find where the graph breaks.
```

---

# 41. Mini Dry Run: Creating Three Beans

Code:

```java
@Repository
public class OrderRepository {
}

@Component
public class StripePaymentClient implements PaymentClient {
}

@Service
public class OrderService {
    private final OrderRepository repository;
    private final PaymentClient paymentClient;

    public OrderService(OrderRepository repository,
                        PaymentClient paymentClient) {
        this.repository = repository;
        this.paymentClient = paymentClient;
    }
}
```

Container dry run:

```text
1. Component scan finds OrderRepository.
2. Register BeanDefinition: orderRepository.
3. Component scan finds StripePaymentClient.
4. Register BeanDefinition: stripePaymentClient.
5. Component scan finds OrderService.
6. Register BeanDefinition: orderService.
7. Create singleton orderRepository.
8. Create singleton stripePaymentClient.
9. Create singleton orderService.
10. Resolve constructor dependencies:
    - OrderRepository -> orderRepository
    - PaymentClient -> stripePaymentClient
11. Call OrderService constructor.
12. Store final bean in singleton registry.
```

ASCII:

```text
BeanDefinitions:
  orderRepository
  stripePaymentClient
  orderService
        |
        v
Instantiate dependencies first
        |
        v
OrderService(repository, paymentClient)
        |
        v
Ready singleton registry
```

This is the essence of ApplicationContext.

---

# 42. Mini Dry Run: Transactional Service

Code:

```java
@Service
public class CheckoutService {

    @Transactional
    public void checkout(String orderId) {
        // write order
        // write payment
    }
}
```

Container dry run:

```text
1. BeanDefinition registered for CheckoutService.
2. Raw CheckoutService object is created.
3. Dependencies are injected.
4. BeanPostProcessor checks for transactional metadata.
5. Spring creates proxy object.
6. Context stores/exposes proxy as checkoutService bean.
7. Other beans receive proxy, not raw object.
```

Runtime dry run:

```text
Controller calls checkoutService.checkout()
    |
    v
Proxy intercepts call
    |
    v
TransactionManager begins transaction
    |
    v
Raw CheckoutService.checkout() executes
    |
    v
Proxy commits or rolls back
```

ASCII:

```text
Controller
   |
   v
CheckoutService Proxy
   |
   +-- begin tx
   |
   v
Real CheckoutService
   |
   +-- business method
   |
   v
Proxy completes tx
```

This explains many transactional bugs.

---

# 43. Interview Answers

## What is ApplicationContext?

ApplicationContext is Spring's main application container. It stores bean definitions, creates and wires beans, manages bean lifecycle, resolves configuration and profiles, publishes events, loads resources, and exposes application-level services around the underlying BeanFactory.

## What is a BeanDefinition?

A BeanDefinition is metadata or a recipe describing how Spring should create a bean. It includes class, scope, dependencies, factory method, lazy flag, qualifiers, lifecycle methods, and other metadata.

## ApplicationContext vs BeanFactory?

BeanFactory is the basic object creation and dependency injection engine. ApplicationContext builds on it and adds environment, events, resource loading, message resolution, lifecycle integration, and application-level features.

## What happens during Spring Boot startup?

SpringApplication creates and prepares an ApplicationContext, loads environment and bean definitions, runs post-processors, refreshes the context, creates singleton beans, starts the embedded web server if needed, and publishes lifecycle events such as ApplicationReadyEvent.

## What is refresh()?

`refresh()` is the major ApplicationContext lifecycle operation that initializes the container: prepares the bean factory, runs post-processors, registers infrastructure, creates singleton beans, starts lifecycle components, and marks the context as ready.

## What is a BeanPostProcessor?

A BeanPostProcessor can inspect, modify, or wrap bean instances during creation. Many Spring features such as lifecycle annotations, AOP proxies, and transactional proxies rely on post-processors.

## Why can @Transactional fail?

In common Spring proxy-based transaction management, transactional behavior is applied by a proxy. Private methods, self-invocation, and calls that do not pass through the proxy may not trigger transaction logic.

## Why are circular dependencies bad?

Circular dependencies mean beans depend on each other in a loop. They complicate construction, often indicate mixed responsibilities, and can fail during context creation. The better fix is usually refactoring the design.

## Why prefer constructor injection?

Constructor injection makes dependencies explicit, supports final fields, allows immutable valid objects, improves testability, and gives the ApplicationContext a clean dependency graph.

## How do you debug missing bean errors?

Check component scan path, annotations or @Bean registration, active profiles, conditions, bean creation errors, type matching, qualifiers, and actuator `/beans` or `/conditions`.

---

# 44. Cheat Sheet

```text
ApplicationContext
  Full Spring application container.

BeanFactory
  Low-level bean creation and dependency injection engine.

BeanDefinition
  Recipe/metadata for creating a bean.

Bean
  Object created, wired, and managed by Spring.

Component Scan
  Finds annotated classes and registers BeanDefinitions.

@Bean
  Factory method that contributes a BeanDefinition.

Singleton Scope
  One bean instance per ApplicationContext.

refresh()
  Main lifecycle method that builds the running context.

BeanFactoryPostProcessor
  Modifies bean definitions before beans are created.

BeanPostProcessor
  Modifies/wraps bean instances during creation.

Environment
  Holds properties, profiles, and configuration sources.

Profile
  Activates or skips beans/config based on environment.

Condition
  Decides whether auto-configuration or a bean should apply.

ApplicationEventPublisher
  Publishes events to listeners inside the context.

ResourceLoader
  Loads classpath/file/URL resources.

AOP Proxy
  Wrapper around bean that applies cross-cutting behavior.

Actuator /beans
  Shows beans in running context.

Actuator /conditions
  Shows why auto-config matched or did not match.
```

Production mapping:

```text
Missing bean          -> scan/profile/condition/registration issue
Multiple beans        -> type ambiguity
Bean creation failed  -> constructor/init/factory method/root cause issue
Circular dependency   -> design graph issue
Tx not working        -> proxy boundary issue
Wrong config          -> environment/profile/property source issue
Slow startup          -> heavy bean creation/init issue
Wrong bean in prod    -> profile/condition/primary/qualifier issue
```

---

# 45. One Picture To Remember

```text
Source Code
  |
  | @Component, @Service, @Repository, @Controller
  | @Configuration, @Bean
  | @Profile, @Conditional, @ConfigurationProperties
  v
+--------------------------------------------------+
| BeanDefinition Registry                          |
| recipes for all possible beans                   |
+----------------------+---------------------------+
                       |
                       v
+--------------------------------------------------+
| BeanFactoryPostProcessors                        |
| edit recipes before objects exist                |
+----------------------+---------------------------+
                       |
                       v
+--------------------------------------------------+
| BeanFactory                                      |
| instantiate + resolve dependencies               |
+----------------------+---------------------------+
                       |
                       v
+--------------------------------------------------+
| BeanPostProcessors                               |
| init callbacks, inject support, create proxies   |
+----------------------+---------------------------+
                       |
                       v
+--------------------------------------------------+
| ApplicationContext                               |
| singleton registry + env + events + resources    |
+----------------------+---------------------------+
                       |
                       v
+--------------------------------------------------+
| Running Spring Boot Application                  |
| controllers, services, repositories, infra beans |
+--------------------------------------------------+
```

Final memory hook:

```text
ApplicationContext is not magic.

It is the runtime machine that turns bean recipes into a wired, initialized,
possibly proxied, observable, and shutdown-aware Java application.
```

---

# 46. Final Production Checklist

```text
[ ] I can explain ApplicationContext without saying only "container".
[ ] I know BeanDefinition is a recipe, not the object.
[ ] I know component scanning registers bean definitions.
[ ] I know @Bean methods also register bean definitions.
[ ] I know constructor injection is dependency graph construction.
[ ] I understand singleton means one bean per ApplicationContext.
[ ] I know bean lifecycle from constructor to init to post-processing.
[ ] I understand why BeanPostProcessors explain Spring magic.
[ ] I know refresh() is the big context build step.
[ ] I can debug missing bean errors using scan/profile/condition/type.
[ ] I can debug multiple bean errors using Primary/Qualifier/collection.
[ ] I know circular dependencies are graph design smells.
[ ] I understand @Transactional proxy boundaries.
[ ] I know actuator /beans and /conditions help inspect the context.
[ ] I avoid heavy network calls in constructors and init methods.
[ ] I can explain why wrong active profile can create wrong production beans.
```

---

# 47. Final Sentence

Do not memorize ApplicationContext as a definition.

Remember it as the Spring runtime factory:

```text
Classes become bean recipes.
Recipes become objects.
Objects get dependencies.
Objects get lifecycle callbacks.
Objects may become proxies.
Final beans live inside ApplicationContext and form the running backend.
```
