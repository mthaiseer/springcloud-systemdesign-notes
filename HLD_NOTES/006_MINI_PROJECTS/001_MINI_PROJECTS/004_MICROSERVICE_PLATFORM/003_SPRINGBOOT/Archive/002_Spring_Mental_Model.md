# 002_Spring_Mental_Model.md

> MiniSpringBoot Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Production Debugging

---

# 1. Why This Chapter Exists

In the previous chapter, the main idea was:

```text
Spring exists because humans should not manually create, wire, configure,
wrap, and operate hundreds of backend objects by hand.
```

This chapter answers the next question:

```text
What is the mental model of Spring?
```

Do not start with annotations.

Do not memorize:

```java
@Component
@Service
@Repository
@Autowired
@Bean
@Configuration
@Transactional
```

These are labels.

The real Spring model is:

```text
Your code describes an application.
Spring turns that description into a running object graph.
Then Spring wraps that graph with infrastructure behavior.
```

One picture:

```text
Java Classes + Metadata + Config
            |
            v
+---------------------------+
| Spring ApplicationContext |
| creates beans             |
| wires dependencies        |
| manages lifecycle         |
| applies post processors   |
| creates proxies           |
+-------------+-------------+
              |
              v
Running Backend Application
```

If you remember only one thing:

```text
Spring is not annotation magic.
Spring is an object graph factory + lifecycle manager + infrastructure wrapper.
```

---

# 2. The One-Line Mental Model

Spring mental model:

```text
Declare what exists.
Declare what each object needs.
Let the container build and operate the system.
```

Plain Java model:

```java
OrderRepository repo = new OrderRepository(dataSource);
PaymentClient payment = new PaymentClient(httpClient);
OrderService service = new OrderService(repo, payment);
```

Spring model:

```java
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;

    public OrderService(OrderRepository orderRepository,
                        PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
    }
}
```

Spring reads this as:

```text
OrderService is a managed object.
OrderService needs OrderRepository and PaymentClient.
I must create those first.
Then I can create OrderService.
```

ASCII:

```text
Developer writes classes
       |
       v
Spring discovers candidates
       |
       v
Spring builds dependency graph
       |
       v
Spring creates objects in correct order
       |
       v
Spring applies infrastructure
       |
       v
Application is ready
```

This is the foundation for IoC, DI, beans, proxies, transactions, MVC, configuration, and Boot auto-configuration.

---

# 3. Real World Analogy: Factory Assembly Line

Imagine a factory that builds cars.

A worker does not manually create every part:

```text
engine
wheels
battery
brake system
dashboard
software unit
fuel system
```

Instead, the factory has:

```text
blueprints
parts catalog
assembly order
quality checks
special stations
final inspection
```

Spring is similar.

```text
Java class                 = blueprint
Bean definition            = factory instruction
ApplicationContext         = factory manager
Dependency injection       = assembly step
BeanPostProcessor          = quality/customization station
Proxy                      = protective wrapper
Actuator                   = factory dashboard
Spring Boot                = preconfigured factory setup
```

Diagram:

```text
Blueprints
  |
  v
Factory instructions
  |
  v
Assembly line
  |
  +-- create object
  +-- inject dependencies
  +-- configure properties
  +-- run lifecycle hooks
  +-- wrap with proxy if needed
  |
  v
Ready vehicle
```

Spring is the factory for your backend application objects.

The important shift:

```text
You do not build every object manually.
You design the blueprints and let the factory assemble the system.
```

---

# 4. The Application Is An Object Graph

A backend application is not one class.

It is a graph.

Example order system:

```text
OrderController
      |
      v
CheckoutService
      |
      +--> OrderRepository
      |        |
      |        v
      |     DataSource
      |
      +--> PaymentClient
      |        |
      |        v
      |     RestClient
      |
      +--> InventoryClient
      |
      +--> ApplicationEventPublisher
```

In plain Java, you manually build this graph.

In Spring, the container builds it.

Spring is basically answering:

```text
What objects exist?
What does each object need?
In what order should they be created?
Which ones need wrapping?
Which ones need lifecycle callbacks?
Which ones are singletons?
Which ones are request-scoped?
Which config should they receive?
```

Spring startup is graph construction.

Common startup errors are graph construction errors.

```text
NoSuchBeanDefinitionException      -> graph node missing
UnsatisfiedDependencyException     -> edge cannot be resolved
BeanCurrentlyInCreationException   -> circular graph
ConfigurationProperties error      -> config node cannot be bound
```

Mental model:

```text
Spring does not "run annotations".
Spring builds a graph from metadata.
```

---

# 5. What Is A Bean?

A bean is not a special Java type.

A bean is:

```text
A normal Java object whose creation, configuration, wiring, lifecycle,
and destruction are managed by Spring.
```

Normal object:

```java
OrderService service = new OrderService(...);
```

Spring bean:

```text
Spring creates OrderService.
Spring injects dependencies.
Spring may apply post processors.
Spring may wrap it with a proxy.
Spring stores it in the container.
Spring destroys it on shutdown.
```

ASCII:

```text
Class
 |
 v
BeanDefinition
 |
 v
Object instance
 |
 v
Dependencies injected
 |
 v
Post processors
 |
 v
Proxy maybe created
 |
 v
Bean available for use
```

Important:

```text
A bean is a managed object, not an annotation.
```

You can create beans through:

```text
@Component / @Service / @Repository / @Controller
@Bean methods
Auto-configuration
FactoryBean
Programmatic registration
```

But the result is the same:

```text
The object becomes part of the Spring-managed graph.
```

---

# 6. What Is ApplicationContext?

`ApplicationContext` is the main Spring container.

It stores and manages beans.

It knows:

```text
bean names
bean types
bean definitions
dependencies
environment properties
profiles
events
resources
message sources
lifecycle callbacks
post processors
```

Mental model:

```text
ApplicationContext is the runtime registry + factory + lifecycle manager.
```

ASCII:

```text
+------------------------------------------------+
| ApplicationContext                              |
|                                                |
|  Bean Definitions                              |
|  Environment / Profiles                        |
|  BeanFactory                                   |
|  BeanPostProcessors                            |
|  Event Publisher                               |
|  Resource Loader                               |
|  Lifecycle Manager                             |
|                                                |
+----------------------+-------------------------+
                       |
                       v
             Managed Bean Graph
```

When you run:

```java
SpringApplication.run(OrderApplication.class, args);
```

Spring Boot creates an `ApplicationContext`.

Then the context builds your application.

Important mindset:

```text
When a Spring Boot app starts, the real work is not just "main method runs".
The real work is "ApplicationContext is created and refreshed".
```

---

# 7. Spring Boot Startup: Big Picture

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
Load Bean Definitions
  |
  v
Apply Auto-Configuration
  |
  v
Instantiate Beans
  |
  v
Run BeanPostProcessors
  |
  v
Start Embedded Server
  |
  v
Publish Ready Event
```

ASCII:

```text
+--------------------------+
| main()                   |
+------------+-------------+
             |
             v
+--------------------------+
| SpringApplication.run    |
+------------+-------------+
             |
             v
+--------------------------+
| Environment prepared     |
+------------+-------------+
             |
             v
+--------------------------+
| Bean definitions loaded  |
+------------+-------------+
             |
             v
+--------------------------+
| Context refresh          |
| create beans             |
+------------+-------------+
             |
             v
+--------------------------+
| Web server starts        |
+------------+-------------+
             |
             v
+--------------------------+
| ApplicationReadyEvent    |
+--------------------------+
```

Do not memorize every internal class first.

Remember the direction:

```text
Configuration -> definitions -> objects -> infrastructure -> running app
```

---

# 8. Component Scan Mental Model

Component scan is how Spring finds many of your classes.

Example:

```java
@SpringBootApplication
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
```

`@SpringBootApplication` includes component scanning from its package downward.

Package example:

```text
com.company.order
  |
  +-- OrderApplication
  |
  +-- controller
  |    +-- OrderController
  |
  +-- service
  |    +-- CheckoutService
  |
  +-- repository
       +-- OrderRepository
```

Spring scans:

```text
com.company.order.*
```

It finds:

```java
@RestController
@Service
@Repository
@Component
@Configuration
```

ASCII:

```text
Base package
    |
    v
Scan classes
    |
    +-- class has stereotype annotation? yes -> candidate bean
    +-- class has no annotation? no -> ignored
    |
    v
Create BeanDefinition
```

Common production bug:

```text
Application class is placed in wrong package.
Spring does not scan services in sibling package.
App starts with NoSuchBeanDefinitionException.
```

Bad structure:

```text
com.company.app.OrderApplication

com.company.service.CheckoutService   <-- not under com.company.app
```

Better:

```text
com.company.OrderApplication

com.company.order.service.CheckoutService
com.company.order.controller.OrderController
```

Memory hook:

```text
Component scan is Spring's "find my classes" phase.
```

---

# 9. Bean Definition vs Bean Instance

This distinction removes a lot of Spring confusion.

A bean definition is metadata.

A bean instance is the actual object.

```text
BeanDefinition:
    "I know how to create OrderService"

Bean Instance:
    new OrderService(orderRepository, paymentClient)
```

ASCII:

```text
Class + annotation
       |
       v
BeanDefinition metadata
       |
       v
Actual object created later
```

Example:

```java
@Service
public class CheckoutService {
    public CheckoutService(OrderRepository repository) {}
}
```

Spring stores a definition like:

```text
bean name: checkoutService
bean class: CheckoutService
constructor args: OrderRepository
scope: singleton
lazy: false
```

Then later, during context refresh:

```text
Create OrderRepository first.
Create CheckoutService using OrderRepository.
```

Why this matters:

```text
Spring can know about a bean before creating it.
Auto-configuration can conditionally register definitions.
Post-processors can modify definitions before objects exist.
Lazy beans may have definitions but no instance yet.
```

Mental model:

```text
Definition is plan.
Instance is built object.
```

---

# 10. Dependency Injection: Constructor First

Dependency Injection means:

```text
An object receives what it needs from outside.
It does not create its collaborators internally.
```

Best default:

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

Why constructor injection is best:

```text
Dependencies are explicit.
Object cannot exist without required dependencies.
Fields can be final.
Unit testing is easy.
No hidden magic in private fields.
```

Bad field injection:

```java
@Service
public class CheckoutService {
    @Autowired
    private OrderRepository orderRepository;
}
```

Problems:

```text
Harder to test without Spring.
Object can be partially initialized.
Dependencies are hidden.
Reflection magic is required.
```

ASCII:

```text
Constructor injection

Spring
  |
  +-- creates dependency A
  +-- creates dependency B
  |
  v
new Service(A, B)
```

Memory hook:

```text
Constructor injection makes the dependency graph visible in Java code.
```

---

# 11. IoC: Who Controls Object Creation?

IoC means Inversion of Control.

Do not memorize the definition.

Understand the direction change.

Before Spring:

```text
Your main method controls object creation.
```

```java
OrderRepository repo = new OrderRepository();
CheckoutService service = new CheckoutService(repo);
```

With Spring:

```text
You declare classes.
Spring controls object creation.
```

```java
@Service
class CheckoutService {
    CheckoutService(OrderRepository repo) {}
}
```

ASCII:

```text
Without IoC:

Application code
   |
   +-- new Repository
   +-- new Service
   +-- new Controller

With IoC:

Application code declares needs
   |
   v
Spring container creates and wires objects
```

IoC is not only for convenience.

It enables:

```text
testability
replaceable implementations
configuration injection
AOP wrapping
transaction management
lifecycle management
auto-configuration
```

Memory hook:

```text
IoC means the framework builds the system around your classes.
```

---

# 12. The Container Is A Graph Solver

Spring must solve a dependency graph.

Example:

```text
OrderController -> CheckoutService -> OrderRepository -> DataSource
                                  -> PaymentClient -> RestClient
```

Creation order:

```text
DataSource
RestClient
OrderRepository
PaymentClient
CheckoutService
OrderController
```

ASCII:

```text
Need OrderController
        |
        v
Need CheckoutService
        |
        +--> Need OrderRepository
        |          |
        |          v
        |       Need DataSource
        |
        +--> Need PaymentClient
                   |
                   v
                Need RestClient
```

Spring works backward from required dependencies.

If anything is missing:

```text
Graph cannot be built.
Application fails fast.
```

This fail-fast behavior is good.

It is better for an app to fail at startup than to fail after receiving real production traffic.

Common error reading:

```text
Parameter 0 of constructor in CheckoutService required a bean of type
OrderRepository that could not be found.
```

Translation:

```text
Spring tried to create CheckoutService.
Constructor needs OrderRepository.
No bean exists for OrderRepository.
Graph edge unresolved.
```

---

# 13. Interfaces And Multiple Implementations

Suppose you have:

```java
public interface PaymentClient {
    void charge(String orderId);
}

@Component
public class StripePaymentClient implements PaymentClient {}

@Component
public class RazorpayPaymentClient implements PaymentClient {}
```

Then:

```java
@Service
public class CheckoutService {
    public CheckoutService(PaymentClient paymentClient) {}
}
```

Problem:

```text
Spring sees two candidates for PaymentClient.
Which one should it inject?
```

ASCII:

```text
CheckoutService needs PaymentClient
              |
              v
      +-------+--------+
      |                |
StripePaymentClient  RazorpayPaymentClient
```

Solutions:

```java
@Primary
@Component
public class StripePaymentClient implements PaymentClient {}
```

or:

```java
@Service
public class CheckoutService {
    public CheckoutService(@Qualifier("razorpayPaymentClient")
                           PaymentClient paymentClient) {}
}
```

or configuration-driven:

```java
@Configuration
public class PaymentConfig {
    @Bean
    PaymentClient paymentClient(PaymentProperties props) {
        if (props.provider().equals("stripe")) {
            return new StripePaymentClient();
        }
        return new RazorpayPaymentClient();
    }
}
```

Mental model:

```text
Spring injects by type first.
If multiple beans match, you must disambiguate.
```

---

# 14. @Component, @Service, @Repository, @Controller

These annotations are stereotype labels.

They all create Spring beans.

But they communicate role.

```text
@Component      generic Spring-managed object
@Service        business/use-case layer
@Repository     persistence boundary
@Controller     MVC web controller
@RestController controller + response body
```

ASCII:

```text
HTTP Request
    |
    v
@RestController
    |
    v
@Service
    |
    v
@Repository
    |
    v
Database
```

Do not memorize them as magic.

Read them as:

```text
This class belongs to this responsibility layer.
Spring should manage it.
```

`@Repository` can also participate in exception translation for persistence exceptions.

`@Service` does not make code business-safe automatically.

It is a role marker.

Bad:

```java
@RestController
public class OrderController {
    @PostMapping("/orders")
    public void create() {
        // 150 lines business logic + SQL + payment call
    }
}
```

Better:

```text
Controller handles HTTP.
Service handles use case.
Repository handles persistence.
Client handles external systems.
```

Memory hook:

```text
Stereotype annotations are role labels plus bean registration.
```

---

# 15. @Configuration And @Bean

Not every object is your class.

Some objects come from libraries.

Example:

```text
ObjectMapper
RestClient
ThreadPoolTaskExecutor
DataSource
KafkaTemplate
RedisConnectionFactory
```

You may need to create them explicitly.

Use `@Configuration` + `@Bean`.

```java
@Configuration
public class HttpClientConfig {

    @Bean
    public RestClient paymentRestClient(RestClient.Builder builder,
                                        PaymentProperties props) {
        return builder
                .baseUrl(props.baseUrl())
                .build();
    }
}
```

Mental model:

```text
@Component scan finds classes.
@Bean methods are factory methods.
```

ASCII:

```text
@Configuration class
       |
       v
@Bean method
       |
       v
Spring calls method
       |
       v
Returned object becomes bean
```

Important:

```text
Use @Component when Spring can create your class directly.
Use @Bean when you need custom construction logic or third-party objects.
```

Example:

```java
@Bean
Clock clock() {
    return Clock.systemUTC();
}
```

This makes time testable.

---

# 16. Singleton Scope Is Default

By default, Spring beans are singletons.

That means:

```text
One bean instance per ApplicationContext.
```

Example:

```text
One CheckoutService
One OrderRepository
One PaymentClient
One ObjectMapper
One DataSource bean
```

ASCII:

```text
ApplicationContext
  |
  +-- checkoutService instance #1
  +-- orderRepository instance #1
  +-- paymentClient instance #1
```

Important:

```text
Singleton does not mean global JVM singleton.
It means one instance managed by that Spring context.
```

Why singleton is default:

```text
Most service/repository/controller objects are stateless.
They can be safely reused across requests.
```

Bad singleton design:

```java
@Service
public class CheckoutService {
    private String currentOrderId; // dangerous shared mutable request state
}
```

Problem:

```text
Multiple threads handle multiple requests using same singleton bean.
Shared mutable fields can leak data across requests.
```

Better:

```java
public OrderResponse checkout(CreateOrderRequest request) {
    String currentOrderId = request.orderId(); // local variable
}
```

Memory hook:

```text
Spring service beans should usually be stateless.
```

---

# 17. Request Scope And Why You Rarely Need It

Spring supports scopes:

```text
singleton
prototype
request
session
application
websocket
```

Request scope means:

```text
One bean instance per HTTP request.
```

Example:

```java
@Component
@RequestScope
public class RequestContext {
    private String correlationId;
}
```

ASCII:

```text
Request A -> RequestContext A
Request B -> RequestContext B
Request C -> RequestContext C
```

But most backend code should avoid overusing request-scoped beans.

Prefer passing explicit values:

```java
checkoutService.checkout(command, correlationId);
```

or use logging MDC for correlation IDs.

Why?

```text
Request-scoped beans can hide dependencies.
They complicate async execution.
They can break outside web request context.
They make testing harder.
```

Mental model:

```text
Singleton stateless services are the normal Spring backend model.
Request state belongs in method parameters, DTOs, security context, or logging context.
```

---

# 18. Bean Lifecycle

A Spring bean has a lifecycle.

Simple version:

```text
register definition
create instance
inject dependencies
set properties
run aware callbacks
run post-processors before init
run init callbacks
run post-processors after init
bean ready
destroy on shutdown
```

ASCII:

```text
BeanDefinition
      |
      v
Constructor
      |
      v
Dependency Injection
      |
      v
@PostConstruct
      |
      v
BeanPostProcessor after init
      |
      v
Ready Bean
      |
      v
@PreDestroy on shutdown
```

Example:

```java
@Component
public class CacheWarmup {
    @PostConstruct
    void init() {
        System.out.println("Warm cache after dependencies are ready");
    }

    @PreDestroy
    void shutdown() {
        System.out.println("Clean resources before exit");
    }
}
```

Be careful:

```text
Do not put slow or risky network calls in @PostConstruct unless you want startup to fail.
Do not start unmanaged threads casually.
Do not assume all application traffic is safe before readiness is UP.
```

Production mindset:

```text
Startup hooks affect deployment reliability.
```

---

# 19. BeanPostProcessor: The Hidden Extension Point

A `BeanPostProcessor` can modify beans after creation.

You do not use it daily, but Spring uses this idea heavily.

Mental model:

```text
BeanPostProcessor is an inspection/customization station in the bean factory.
```

ASCII:

```text
Create bean
   |
   v
Before initialization processors
   |
   v
Initialization
   |
   v
After initialization processors
   |
   v
Maybe original bean, maybe proxy
```

Many Spring features rely on post-processing:

```text
@Autowired processing
@ConfigurationProperties binding
@Transactional proxy creation
@Async proxy creation
@PostConstruct handling
AOP advisors
```

This explains why annotations are not magic.

Example mental flow:

```text
@Transactional annotation found
       |
       v
Infrastructure processor recognizes it
       |
       v
Proxy is created around target bean
       |
       v
Calls through proxy can start/commit/rollback transaction
```

Memory hook:

```text
Spring features are often implemented by processors that inspect metadata and enhance beans.
```

---

# 20. Proxy Mental Model

A proxy is an object that stands in front of the real object.

Caller thinks it is calling service.

Actually:

```text
Caller -> Proxy -> Real Service
```

ASCII:

```text
Controller
   |
   v
CheckoutService Proxy
   |
   +-- begin transaction
   +-- security check
   +-- metrics timing
   |
   v
Real CheckoutService
   |
   v
Proxy commits/rollbacks/records
```

Example:

```java
@Service
public class CheckoutService {

    @Transactional
    public void checkout() {
        // business logic
    }
}
```

Spring may expose a proxy bean instead of the raw object.

So other beans receive:

```text
CheckoutService proxy
```

not necessarily:

```text
raw CheckoutService instance
```

This is how cross-cutting behavior is added without polluting business code.

Memory hook:

```text
Proxy means "same interface, extra behavior around the real method".
```

---

# 21. Why @Transactional Sometimes Does Not Work

`@Transactional` usually works through proxies.

So this works:

```text
OrderController -> CheckoutService proxy -> checkout()
```

But this can fail:

```java
@Service
public class UserService {

    public void updateAndSaveAll(List<User> users) {
        saveAll(users); // self-invocation
    }

    @Transactional
    public void saveAll(List<User> users) {
        // save
    }
}
```

Why?

```text
The call happens inside the same object.
It does not go through the Spring proxy.
```

ASCII:

```text
External call:
Controller -> Proxy -> RealService.saveAll()
             transaction works

Self call:
RealService.updateAndSaveAll()
      |
      v
RealService.saveAll()
      no proxy crossing
```

Private method is worse:

```java
@Transactional
private void saveAll(...) {}
```

Private methods are not a good proxy target.

Correct design:

```java
@Service
public class UserService {
    private final UserSaveService userSaveService;

    public UserService(UserSaveService userSaveService) {
        this.userSaveService = userSaveService;
    }

    public void updateAndSaveAll(List<User> users) {
        users.forEach(u -> u.setStatus("updated"));
        userSaveService.saveAll(users);
    }
}

@Service
public class UserSaveService {
    @Transactional
    public void saveAll(List<User> users) {
        // repository.saveAll(users)
    }
}
```

Memory hook:

```text
For proxy behavior, the call must cross the proxy boundary.
```

---

# 22. AOP Is Wrapping, Not Business Logic

AOP means Aspect-Oriented Programming.

Do not memorize terminology first.

Think:

```text
Some behavior must run around many methods.
```

Examples:

```text
transactions
logging
metrics
security
retry
caching
async execution
```

Without AOP:

```java
public void checkout() {
    startTimer();
    beginTransaction();
    try {
        // business logic
        commit();
    } catch (Exception e) {
        rollback();
        throw e;
    } finally {
        recordTimer();
    }
}
```

With AOP/proxy:

```java
@Transactional
@Timed
public void checkout() {
    // business logic only
}
```

ASCII:

```text
Method Call
   |
   v
Aspect 1: security
   |
   v
Aspect 2: transaction
   |
   v
Aspect 3: metrics
   |
   v
Business Method
```

Important:

```text
AOP does not remove the need to understand boundaries.
Wrong transaction boundary still causes wrong behavior.
Wrong retry boundary can duplicate side effects.
Wrong cache boundary can serve stale data.
```

Memory hook:

```text
AOP is infrastructure wrapping around business methods.
```

---

# 23. MVC Request Lifecycle

For web apps, Spring MVC handles HTTP flow.

Example:

```java
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CheckoutService checkoutService;

    public OrderController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return checkoutService.checkout(request);
    }
}
```

Request flow:

```text
HTTP POST /orders
        |
        v
Embedded Tomcat
        |
        v
DispatcherServlet
        |
        v
HandlerMapping finds controller method
        |
        v
MessageConverter JSON -> Java DTO
        |
        v
Validation
        |
        v
Controller method
        |
        v
Service
        |
        v
MessageConverter Java -> JSON
        |
        v
HTTP Response
```

ASCII:

```text
Client
  |
  v
Tomcat
  |
  v
DispatcherServlet
  |
  +-- route
  +-- bind
  +-- validate
  +-- call controller
  +-- serialize response
  |
  v
Client receives JSON
```

Memory hook:

```text
Controller method is only the visible middle of a larger request pipeline.
```

---

# 24. DTO Boundary Mental Model

Do not expose entities directly from controllers.

Bad:

```java
@GetMapping("/{id}")
public OrderEntity get(@PathVariable Long id) {
    return repository.findById(id).orElseThrow();
}
```

Problems:

```text
Entity fields leak into API.
Lazy loading can trigger surprise queries.
Internal DB model becomes public contract.
Sensitive fields may leak.
API changes become tied to schema changes.
```

Better:

```java
public record OrderResponse(
        Long id,
        String status,
        BigDecimal total
) {}

@GetMapping("/{id}")
public OrderResponse get(@PathVariable Long id) {
    Order order = orderService.get(id);
    return new OrderResponse(order.id(), order.status(), order.total());
}
```

ASCII:

```text
Database Entity
      |
      v
Domain / Service model
      |
      v
API DTO
      |
      v
JSON Response
```

Mental model:

```text
Controller boundary should speak API language.
Repository boundary should speak persistence language.
Service should protect business language.
```

Spring makes it easy to build APIs, but design is still your responsibility.

---

# 25. Validation Flow

Spring can validate request DTOs.

```java
public record CreateOrderRequest(
        @NotBlank String userId,
        @NotBlank String itemId,
        @Positive int quantity
) {}
```

Controller:

```java
@PostMapping("/orders")
public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
    return checkoutService.checkout(request);
}
```

Flow:

```text
JSON body
   |
   v
CreateOrderRequest object
   |
   v
Bean Validation checks annotations
   |
   +-- valid -> controller method runs
   +-- invalid -> MethodArgumentNotValidException
```

ASCII:

```text
Request JSON
   |
   v
DTO binding
   |
   v
Validation gate
   |
   +-- pass -> service
   +-- fail -> error response
```

Production mindset:

```text
Validate at system boundaries.
Do not trust client input.
Keep validation errors consistent.
```

Use global exception handler:

```java
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        return Map.of(
                "error", "VALIDATION_FAILED",
                "message", "Request body is invalid"
        );
    }
}
```

Memory hook:

```text
Validation is the gate between untrusted HTTP input and trusted service logic.
```

---

# 26. Configuration Mental Model

Configuration is external reality entering your app.

Examples:

```text
database URL
Redis host
Kafka bootstrap servers
JWT issuer
timeouts
pool size
feature flags
payment provider
third-party base URLs
```

Do not hardcode this.

Bad:

```java
private final String paymentUrl = "https://prod-payment.company.com";
```

Better:

```yaml
payment:
  base-url: https://payment.company.com
  timeout-ms: 2000
```

Typed config:

```java
@ConfigurationProperties(prefix = "payment")
public record PaymentProperties(
        String baseUrl,
        int timeoutMs
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
@Component
public class PaymentClient {
    private final PaymentProperties props;

    public PaymentClient(PaymentProperties props) {
        this.props = props;
    }
}
```

ASCII:

```text
application.yml / env vars
          |
          v
Spring Environment
          |
          v
ConfigurationProperties binding
          |
          v
Typed config object
          |
          v
Bean uses config
```

Memory hook:

```text
Configuration should be external, typed, validated, and injectable.
```

---

# 27. Profiles Mental Model

Profiles select environment-specific beans or properties.

Example:

```text
dev
test
stage
prod
```

`application-dev.yml`:

```yaml
payment:
  base-url: http://localhost:9090
```

`application-prod.yml`:

```yaml
payment:
  base-url: https://payment.company.com
```

Run:

```bash
java -jar app.jar --spring.profiles.active=prod
```

ASCII:

```text
Base config
   |
   +-- dev overrides
   +-- stage overrides
   +-- prod overrides
```

Use profiles carefully.

Good:

```text
Different URLs
Different credentials from env
Different feature flags
Different logging levels
```

Dangerous:

```text
Completely different business behavior per profile.
Too many @Profile beans.
Prod behavior not tested.
```

Example:

```java
@Bean
@Profile("dev")
PaymentClient fakePaymentClient() {
    return new FakePaymentClient();
}
```

Memory hook:

```text
Profiles should select environment details, not create multiple applications hidden in one codebase.
```

---

# 28. Auto-Configuration Mental Model

Spring Boot auto-configuration means:

```text
Spring Boot looks at your classpath, properties, and existing beans,
then configures sensible defaults automatically.
```

Example:

If you add:

```xml
<artifactId>spring-boot-starter-web</artifactId>
```

Boot can configure:

```text
embedded Tomcat
DispatcherServlet
JSON converters
error handling
web MVC infrastructure
```

If you add:

```xml
<artifactId>spring-boot-starter-data-jpa</artifactId>
```

Boot can configure:

```text
EntityManagerFactory
TransactionManager
DataSource integration
Spring Data repositories
```

ASCII:

```text
Classpath + Properties + Existing Beans
              |
              v
Auto-Configuration Conditions
              |
              v
Register useful infrastructure beans
```

Important:

```text
Auto-configuration backs off if you define your own bean.
```

Example:

```text
If no ObjectMapper bean exists, Boot creates one.
If you provide ObjectMapper bean, Boot uses yours.
```

Memory hook:

```text
Spring Boot auto-config is conditional default bean registration.
```

---

# 29. Starter Dependencies Mental Model

A starter is not magic.

It is a curated dependency bundle.

Example:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

This brings common web dependencies together.

Without starter:

```text
You manually choose Spring MVC version.
You manually choose Jackson version.
You manually choose Tomcat version.
You manually align compatible libraries.
```

With starter:

```text
Boot dependency management chooses compatible versions.
```

ASCII:

```text
starter-web
   |
   +-- spring-webmvc
   +-- jackson
   +-- validation support
   +-- embedded tomcat
   +-- logging integration
```

Mental model:

```text
Starter = common stack shortcut.
Auto-configuration = creates beans for that stack.
```

They work together:

```text
Starter puts libraries on classpath.
Auto-config sees libraries and configures infrastructure.
```

Memory hook:

```text
Starter provides ingredients; auto-configuration cooks default meal.
```

---

# 30. Data Access Mental Model

Spring data access is about reducing repetitive plumbing.

Plain JDBC concerns:

```text
get connection
prepare statement
bind params
execute query
map result
handle exceptions
close resources
manage transaction
```

Spring provides:

```text
JdbcTemplate
Spring Data JPA
transaction abstraction
exception translation
repository interfaces
```

Repository responsibility:

```text
Persistence boundary, not business workflow.
```

Example:

```java
@Repository
public class OrderJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public OrderJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Order order) {
        jdbcTemplate.update(
                "insert into orders(id, status) values (?, ?)",
                order.id(),
                order.status()
        );
    }
}
```

Spring Data JPA:

```java
public interface OrderJpaRepository
        extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByExternalId(String externalId);
}
```

ASCII:

```text
Service
  |
  v
Repository
  |
  v
Spring data infrastructure
  |
  v
DataSource / EntityManager
  |
  v
Database
```

Memory hook:

```text
Repository hides persistence plumbing, not business rules.
```

---

# 31. Transaction Mental Model

A transaction is a unit of work.

Example checkout:

```text
insert order
reserve stock
insert payment record
```

All should succeed or all should rollback.

Spring transaction flow:

```text
Caller
  |
  v
Transactional Proxy
  |
  +-- get connection/entity manager
  +-- begin transaction
  |
  v
Service method
  |
  +-- repository operation 1
  +-- repository operation 2
  +-- repository operation 3
  |
  v
Proxy commits or rollbacks
```

Example:

```java
@Service
public class CheckoutService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;

    public CheckoutService(OrderRepository orderRepository,
                           InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public void checkout(CreateOrderCommand command) {
        orderRepository.insert(command.orderId());
        inventoryRepository.reserve(command.itemId(), command.quantity());
    }
}
```

Important rules:

```text
Put @Transactional on service-layer public methods.
Keep transaction boundary around one business use case.
Avoid long external API calls inside DB transaction.
Understand rollback behavior.
Avoid private/self-invoked transactional methods.
```

Memory hook:

```text
Transaction boundary belongs around a business unit of work, not random repository calls.
```

---

# 32. External API Client Mental Model

External calls are production danger points.

Examples:

```text
payment service
inventory service
tax service
shipping service
identity provider
third-party APIs
```

Never treat external calls like local method calls.

Bad:

```java
public void checkout() {
    paymentClient.charge(); // no timeout, no retry strategy, inside transaction
}
```

Better mental model:

```text
External service can be slow, down, partially failing, or duplicate-processing requests.
```

Spring-friendly client design:

```java
@Component
public class PaymentClient {
    private final RestClient restClient;

    public PaymentClient(RestClient paymentRestClient) {
        this.restClient = paymentRestClient;
    }

    public PaymentResponse authorize(String orderId) {
        return restClient.post()
                .uri("/payments/authorize")
                .body(Map.of("orderId", orderId))
                .retrieve()
                .body(PaymentResponse.class);
    }
}
```

Config:

```java
@Configuration
public class PaymentHttpConfig {
    @Bean
    RestClient paymentRestClient(RestClient.Builder builder,
                                 PaymentProperties props) {
        return builder.baseUrl(props.baseUrl()).build();
    }
}
```

ASCII:

```text
Service
  |
  v
PaymentClient bean
  |
  v
RestClient / timeout / base URL
  |
  v
Remote payment service
```

Production checklist:

```text
timeout
retry only when safe
idempotency key for side effects
circuit breaker if needed
logging correlation ID
metrics
fallback only when business-safe
```

Memory hook:

```text
External clients are infrastructure boundaries; configure and observe them explicitly.
```

---

# 33. Events Mental Model

Spring has application events.

Do not confuse them with Kafka.

Spring application events are in-process.

Example:

```java
public record OrderCreatedEvent(String orderId) {}
```

Publish:

```java
@Service
public class CheckoutService {
    private final ApplicationEventPublisher publisher;

    public CheckoutService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void checkout(String orderId) {
        // create order
        publisher.publishEvent(new OrderCreatedEvent(orderId));
    }
}
```

Listen:

```java
@Component
public class EmailOnOrderCreatedListener {

    @EventListener
    public void on(OrderCreatedEvent event) {
        System.out.println("Send email for " + event.orderId());
    }
}
```

ASCII:

```text
CheckoutService
      |
      v
ApplicationEventPublisher
      |
      +-- Email listener
      +-- Audit listener
      +-- Metrics listener
```

Use carefully:

```text
Good for decoupling in-process side effects.
Not a replacement for durable messaging.
If app crashes, in-memory event is gone.
For reliable integration, use outbox + Kafka/RabbitMQ.
```

Memory hook:

```text
Spring events decouple inside one JVM; Kafka decouples across systems durably.
```

---

# 34. Actuator Mental Model

A production app needs visibility.

Spring Boot Actuator exposes operational endpoints.

Examples:

```text
/actuator/health
/actuator/health/liveness
/actuator/health/readiness
/actuator/metrics
/actuator/prometheus
/actuator/mappings
/actuator/beans
/actuator/env
```

Config:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

ASCII:

```text
Kubernetes
   |
   +-- liveness probe  -> is process alive?
   +-- readiness probe -> should app receive traffic?
   |
   v
Spring Boot Actuator
```

Important distinction:

```text
Liveness: should container be restarted?
Readiness: should traffic be sent to this instance?
```

Debugging endpoints:

```bash
curl localhost:8080/actuator/health
curl localhost:8080/actuator/mappings
curl localhost:8080/actuator/beans
curl localhost:8080/actuator/env
```

Memory hook:

```text
Actuator is the production dashboard of a Spring Boot app.
```

---

# 35. Testing Mental Model

Do not start full Spring for every test.

Testing layers:

```text
Plain unit test
  |
  +-- fastest
  +-- no Spring context
  +-- test business logic

Slice test
  |
  +-- web layer / repository layer
  +-- partial Spring context

Full integration test
  |
  +-- @SpringBootTest
  +-- real context
  +-- slower but realistic
```

ASCII:

```text
Unit tests
   |
   v
Many, fast, business logic

Slice tests
   |
   v
Some, framework boundary

Integration tests
   |
   v
Few, full wiring
```

Plain unit test:

```java
class CheckoutServiceTest {

    @Test
    void checkoutCreatesOrder() {
        OrderRepository repo = new InMemoryOrderRepository();
        PaymentClient payment = new FakePaymentClient();

        CheckoutService service = new CheckoutService(repo, payment);

        service.checkout(new CreateOrderCommand("order-1", "item-1", 1));

        // assert result
    }
}
```

Spring MVC test:

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    // test HTTP mapping, validation, JSON
}
```

Memory hook:

```text
Good Spring code is testable without Spring.
Use Spring tests when testing Spring integration.
```

---

# 36. Common Startup Failure Mental Models

When Spring fails at startup, translate the error into graph language.

## NoSuchBeanDefinitionException

```text
A dependency requires a bean, but no matching bean exists.
```

Possible causes:

```text
Class not annotated
Package not scanned
Wrong profile
Conditional bean not created
Interface has no implementation
```

## NoUniqueBeanDefinitionException

```text
More than one bean matches the required type.
```

Fix:

```text
@Primary
@Qualifier
specific bean name
configuration choice
```

## UnsatisfiedDependencyException

```text
Spring cannot create a bean because one of its constructor dependencies fails.
```

Read nested cause.

## BeanCurrentlyInCreationException

```text
Circular dependency.
```

Example:

```text
A needs B
B needs A
```

ASCII:

```text
ServiceA --> ServiceB
   ^          |
   |          v
   +----------+
```

Fix by redesigning responsibility boundaries.

Memory hook:

```text
Most startup errors are broken object graph errors.
```

---

# 37. Circular Dependency Mental Model

Circular dependencies usually mean design smell.

Example:

```java
@Service
class OrderService {
    OrderService(PaymentService paymentService) {}
}

@Service
class PaymentService {
    PaymentService(OrderService orderService) {}
}
```

Graph:

```text
OrderService -> PaymentService -> OrderService
```

Spring cannot decide clean creation order.

Bad mental model:

```text
Just use @Lazy to silence it.
```

Better mental model:

```text
Why do these two services know too much about each other?
```

Possible fixes:

```text
Extract shared domain service.
Use domain event.
Move orchestration to higher-level use case service.
Split query vs command responsibility.
Introduce interface at correct boundary.
```

Better design:

```text
CheckoutService
   |
   +-- OrderService
   +-- PaymentService
```

ASCII:

```text
Bad:
OrderService <--> PaymentService

Better:
CheckoutService
   +--> OrderService
   +--> PaymentService
```

Memory hook:

```text
Circular dependency is often architecture asking for a coordinator.
```

---

# 38. Production Story: Bean Exists But Wrong Bean Injected

A team has two payment clients:

```java
@Component
class StripePaymentClient implements PaymentClient {}

@Component
class MockPaymentClient implements PaymentClient {}
```

In staging, they expect mock.

In production, they expect Stripe.

But production accidentally injects mock due to `@Primary`.

Symptom:

```text
Orders are created but no real charges happen.
```

Root cause:

```text
Bean selection was implicit.
Environment behavior was hidden in annotations.
```

Better:

```java
@ConfigurationProperties(prefix = "payment")
public record PaymentProperties(String provider) {}

@Configuration
public class PaymentConfig {

    @Bean
    PaymentClient paymentClient(PaymentProperties props) {
        return switch (props.provider()) {
            case "stripe" -> new StripePaymentClient();
            case "mock" -> new MockPaymentClient();
            default -> throw new IllegalArgumentException("Unknown provider");
        };
    }
}
```

Config:

```yaml
payment:
  provider: stripe
```

Lesson:

```text
For business-critical strategy selection, prefer explicit configuration over annotation accidents.
```

Memory hook:

```text
Implicit bean selection is convenient until production needs certainty.
```

---

# 39. Production Story: App Started But Requests Failed

A Spring Boot app deployed successfully.

Kubernetes showed pod as running.

But requests returned 500.

Investigation:

```text
JVM alive: yes
Spring context started: yes
Tomcat listening: yes
Database reachable: no
Readiness probe: not configured
Traffic still sent to pod: yes
```

ASCII:

```text
Pod Running
    |
    v
Traffic sent
    |
    v
App tries DB
    |
    v
DB connection fails
    |
    v
500 errors
```

Fix:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
```

Kubernetes:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
```

Lesson:

```text
Running is not ready.
A process can be alive but not capable of serving traffic.
```

Memory hook:

```text
Production readiness is a contract between Spring Boot and the platform.
```

---

# 40. Production Story: Slow Startup Due To @PostConstruct

A team added:

```java
@PostConstruct
void warmCache() {
    remoteCatalogClient.loadAllProducts();
}
```

It worked locally.

In production:

```text
Remote catalog service was slow.
Spring startup blocked.
Kubernetes rollout stalled.
Pods failed readiness.
Deployment timed out.
```

Mental model:

```text
@PostConstruct runs during bean initialization.
If it blocks, application startup blocks.
If it fails, context creation can fail.
```

Better:

```text
Keep startup hooks lightweight.
Use ApplicationReadyEvent for post-start tasks if appropriate.
Make warmup bounded by timeout.
Expose readiness accurately.
Use background warmup only if app can safely serve without cache.
```

Example:

```java
@Component
public class CacheWarmupRunner {

    @EventListener(ApplicationReadyEvent.class)
    public void warmAfterStartup() {
        // bounded warmup, metrics, timeout, safe failure behavior
    }
}
```

Memory hook:

```text
Startup code is deployment code. Treat it like production infrastructure.
```

---

# 41. Debugging Ladder: Spring App Will Not Start

Use this ladder instead of random annotation changes.

```text
1. Read the first meaningful "Caused by"
2. Identify which bean failed
3. Identify which dependency is missing or ambiguous
4. Check package scanning
5. Check profile-specific beans/config
6. Check @ConfigurationProperties binding
7. Check circular dependency
8. Check auto-configuration report with --debug
9. Check conditional beans
10. Reduce to smallest failing graph
```

Useful commands:

```bash
java -jar app.jar --debug

./mvnw spring-boot:run -Dspring-boot.run.arguments=--debug
```

Useful actuator endpoints when app starts:

```bash
curl localhost:8080/actuator/beans
curl localhost:8080/actuator/env
curl localhost:8080/actuator/mappings
curl localhost:8080/actuator/conditions
```

ASCII:

```text
Startup failure
   |
   v
Find failed bean
   |
   v
Find missing/ambiguous dependency
   |
   v
Fix graph/config
```

Memory hook:

```text
Spring startup debugging is graph debugging.
```

---

# 42. Debugging Ladder: Endpoint Returns 404

If a Spring endpoint returns 404:

```text
1. Is application running?
2. Is controller package scanned?
3. Is class annotated with @RestController?
4. Is method annotated with @GetMapping/@PostMapping?
5. Is class-level @RequestMapping prefix correct?
6. Is HTTP method correct?
7. Is context-path configured?
8. Is actuator mappings endpoint showing the route?
9. Is gateway/load balancer rewriting path?
10. Is security blocking and hiding response?
```

Example:

```java
@RestController
@RequestMapping("/orders")
class OrderController {
    @GetMapping("/{id}")
    OrderResponse get(@PathVariable Long id) {}
}
```

Expected:

```text
GET /orders/123
```

Not:

```text
POST /orders/123
GET /api/orders/123 unless context path or gateway adds /api
```

ASCII:

```text
Client URL
   |
   v
Gateway path rewrite?
   |
   v
Tomcat context path?
   |
   v
DispatcherServlet mappings?
   |
   v
Controller method?
```

Memory hook:

```text
404 is usually route discovery mismatch, not business logic failure.
```

---

# 43. Debugging Ladder: @Transactional Not Working

Checklist:

```text
1. Is method public?
2. Is it called from another Spring bean?
3. Does call cross proxy boundary?
4. Is class managed by Spring?
5. Is transaction manager configured?
6. Is exception type causing rollback?
7. Are you catching exception inside method?
8. Are DB operations using same transaction manager?
9. Are you doing external calls inside transaction?
10. Is async/thread switch losing transaction context?
```

Common bug:

```java
@Transactional
private void save() {}
```

Fix:

```text
Move transactional method to public method on Spring-managed service.
Call it from another bean.
```

ASCII:

```text
Works:
Controller -> Proxy -> Service.transactionalMethod()

Fails:
Service.methodA() -> this.transactionalMethod()
```

Rollback detail:

```text
By default, unchecked RuntimeException triggers rollback.
Checked exceptions may need rollbackFor.
```

Example:

```java
@Transactional(rollbackFor = IOException.class)
public void importFile() throws IOException {}
```

Memory hook:

```text
Transaction is proxy + boundary + exception rule.
```

---

# 44. Debugging Ladder: Configuration Not Loaded

Symptoms:

```text
null property
wrong URL
wrong profile
ConfigurationProperties not bound
prod uses dev config
```

Checklist:

```text
1. Is property key correct?
2. Is active profile correct?
3. Is config file name correct?
4. Is @ConfigurationPropertiesScan enabled?
5. Is the properties class a bean?
6. Are environment variables overriding config?
7. Is value relaxed-binding compatible?
8. Is secret mounted correctly in Kubernetes?
9. Is config map updated but pod not restarted?
10. Check /actuator/env carefully
```

Example:

```yaml
payment:
  timeout-ms: 2000
```

Binds to:

```java
@ConfigurationProperties(prefix = "payment")
public record PaymentProperties(int timeoutMs) {}
```

Environment variable:

```text
PAYMENT_TIMEOUT_MS=2000
```

ASCII:

```text
application.yml
env var
command line arg
Kubernetes secret/configmap
        |
        v
Spring Environment
        |
        v
Typed properties
```

Memory hook:

```text
Configuration debugging is source-order + binding-name debugging.
```

---

# 45. One Complete Dry Run: Order API Startup And Request

## Startup

```text
1. main() calls SpringApplication.run()
2. Spring creates ApplicationContext
3. Environment loads application.yml
4. Component scan finds OrderController, CheckoutService, OrderRepository
5. Auto-config creates DataSource, JdbcTemplate, MVC infrastructure
6. Spring creates OrderRepository with JdbcTemplate
7. Spring creates CheckoutService with OrderRepository and PaymentClient
8. Spring sees @Transactional and creates proxy if needed
9. Spring creates OrderController with CheckoutService proxy
10. Tomcat starts on port 8080
11. App becomes ready
```

ASCII:

```text
OrderController
      |
      v
CheckoutService Proxy
      |
      v
Real CheckoutService
      |
      v
OrderRepository
      |
      v
JdbcTemplate
      |
      v
DataSource
```

## Request

```text
POST /orders
  |
  v
Tomcat
  |
  v
DispatcherServlet
  |
  v
JSON -> CreateOrderRequest
  |
  v
Validation
  |
  v
OrderController.create()
  |
  v
CheckoutService proxy opens transaction
  |
  v
Repository writes DB
  |
  v
Proxy commits transaction
  |
  v
OrderResponse -> JSON
```

Memory hook:

```text
Startup builds the graph.
Request walks the graph.
Proxies wrap important edges.
```

---

# 46. What Spring Is Responsible For vs What You Are Responsible For

Spring responsibility:

```text
create beans
wire dependencies
bind configuration
start web server
map HTTP requests
convert JSON
manage transactions
apply AOP proxies
integrate data access
expose actuator endpoints
manage lifecycle
```

Your responsibility:

```text
correct domain model
clean service boundaries
safe transaction boundaries
idempotent external side effects
good SQL and indexes
DTO design
exception strategy
timeouts and resilience
observability design
security rules
testing strategy
production readiness
```

ASCII:

```text
Spring gives infrastructure rails
        |
        v
You still drive the train
```

Bad belief:

```text
If it compiles and Spring starts, design is good.
```

Correct belief:

```text
Spring can perfectly wire bad architecture.
```

Memory hook:

```text
Spring manages objects. Engineers manage correctness.
```

---

# 47. Interview Answers

## What is the mental model of Spring?

Spring is an IoC container that builds and manages an application object graph. Developers declare components, dependencies, and configuration. Spring creates beans, wires dependencies, manages lifecycle, and applies infrastructure behavior such as transactions, MVC, validation, and AOP proxies.

## What is a Spring bean?

A Spring bean is a normal Java object managed by the Spring container. Spring creates it, injects dependencies, applies post-processors, may wrap it with a proxy, and manages its lifecycle.

## What is ApplicationContext?

ApplicationContext is Spring's main container. It stores bean definitions, creates bean instances, resolves dependencies, manages environment and profiles, publishes events, and coordinates lifecycle.

## How does Dependency Injection work?

A class declares its dependencies, usually through a constructor. Spring resolves matching beans from the container and passes them into the constructor when creating the object.

## Why is constructor injection preferred?

It makes dependencies explicit, supports immutable fields, prevents partially initialized objects, and allows plain unit testing without starting Spring.

## What is auto-configuration?

Auto-configuration is Spring Boot's conditional default bean registration. Boot looks at the classpath, existing beans, and properties, then creates useful infrastructure beans when appropriate.

## What is the difference between starter and auto-configuration?

A starter brings a curated dependency set. Auto-configuration sees those dependencies and creates default beans and infrastructure. Starter provides ingredients; auto-configuration cooks the defaults.

## Why can @Transactional fail?

Because transactions are commonly applied through Spring proxies. If a method is private, not managed by Spring, or called through self-invocation, the call may not cross the proxy boundary, so transaction behavior may not apply.

## What is a proxy in Spring?

A proxy is an object placed in front of the real bean. It intercepts method calls and adds behavior such as transactions, security, metrics, caching, or async execution before or after calling the real method.

## What are common Spring startup errors?

Missing beans, ambiguous beans, circular dependencies, configuration binding failures, package scanning issues, profile mismatches, and failed auto-configuration.

---

# 48. Cheat Sheet

```text
Spring mental model:
Classes + metadata + config -> ApplicationContext -> bean graph -> proxies -> running app

Bean:
Spring-managed Java object

ApplicationContext:
container + registry + factory + lifecycle manager

BeanDefinition:
metadata/recipe for creating a bean

Dependency Injection:
object receives dependencies from outside

IoC:
Spring controls object creation and wiring

Component Scan:
Spring finds annotated classes and registers bean definitions

@Configuration + @Bean:
manual factory method for beans, useful for third-party/custom objects

Singleton:
one bean instance per ApplicationContext by default

BeanPostProcessor:
extension point that can customize/wrap beans

Proxy:
object in front of real bean to add cross-cutting behavior

AOP:
infrastructure wrapping around methods

@Transactional:
transaction boundary usually implemented by proxy

Auto-configuration:
conditional default infrastructure bean registration

Starter:
curated dependency bundle

Actuator:
production visibility endpoints

Profile:
environment-specific config/beans
```

Layer responsibility:

```text
Controller  -> HTTP boundary
Service     -> business use case
Repository  -> persistence boundary
Client      -> external system boundary
Config      -> infrastructure object creation
DTO         -> API contract
Entity      -> persistence model
```

Debugging memory:

```text
Startup failure     -> graph construction problem
404                 -> route mapping problem
@Transactional fail -> proxy boundary problem
Wrong config        -> environment/binding problem
Slow startup        -> lifecycle hook problem
Circular dependency -> design boundary problem
```

---

# 49. One Picture To Remember

```text
                Source Code
                   |
                   |
                   v
        +----------------------+
        | Classes + Metadata   |
        | @Service, @Bean, yml |
        +----------+-----------+
                   |
                   v
        +----------------------+
        | Bean Definitions     |
        | recipes, not objects |
        +----------+-----------+
                   |
                   v
        +----------------------+
        | ApplicationContext   |
        | graph builder        |
        +----------+-----------+
                   |
                   v
        +----------------------+
        | Bean Instances       |
        | real Java objects    |
        +----------+-----------+
                   |
                   v
        +----------------------+
        | Post Processors      |
        | inject, bind, wrap   |
        +----------+-----------+
                   |
                   v
        +----------------------+
        | Proxies              |
        | tx, security, AOP    |
        +----------+-----------+
                   |
                   v
        +----------------------+
        | Running Backend App  |
        | HTTP, DB, metrics    |
        +----------------------+
```

Final memory hook:

```text
Spring startup builds the graph.
Spring runtime walks the graph.
Spring proxies wrap the graph.
Spring Boot configures the graph.
Your job is to design the graph correctly.
```

---

# 50. Final Production Checklist

```text
[ ] I can explain Spring without saying "annotation magic".
[ ] I know a bean is a managed Java object.
[ ] I know ApplicationContext is the container.
[ ] I understand bean definition vs bean instance.
[ ] I know component scan finds candidate classes.
[ ] I can explain constructor injection.
[ ] I understand singleton beans should usually be stateless.
[ ] I know when to use @Bean instead of @Component.
[ ] I can explain proxy behavior.
[ ] I know why @Transactional fails with self-invocation/private methods.
[ ] I know starter vs auto-configuration.
[ ] I know configuration should be typed and external.
[ ] I know profiles should not hide different applications.
[ ] I can debug missing bean and ambiguous bean errors.
[ ] I can explain MVC request flow.
[ ] I know Actuator readiness vs liveness.
[ ] I can test business logic without Spring.
[ ] I understand Spring manages infrastructure, not architecture correctness.
```

---

# 51. Final Sentence

Do not memorize Spring as annotations.

Remember the system:

```text
Spring is a container that reads your Java classes, metadata, and configuration,
builds a managed object graph, injects dependencies, applies lifecycle processing,
wraps beans with infrastructure proxies, and runs a production Java backend.
```

Once this mental model is clear, annotations become easy because every annotation maps to a job inside this system.
