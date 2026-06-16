# 003_IoC_DI_Mental_Model.md

> MiniSpringBoot Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Production Debugging

---

# 1. Why This Chapter Exists

Most people learn IoC and Dependency Injection as definitions:

```text
IoC = Inversion of Control
DI  = Dependency Injection
```

That is technically correct, but not useful enough for real backend work.

The real question is:

```text
Why should my class NOT create its own dependencies?
Why should Spring create and connect objects for me?
```

This chapter gives the mental model behind Spring's most important idea:

```text
Your application is an object graph.
Spring is the graph builder.
```

One picture:

```text
Without Spring

main()
  |
  +-- new Controller()
  +-- new Service()
  +-- new Repository()
  +-- new Client()
  +-- manually pass objects everywhere

With Spring

Classes + metadata
       |
       v
Spring Container
       |
       v
Created + wired + managed beans
```

Do not memorize IoC and DI.

Understand the pain:

```text
Manual object creation couples business code to infrastructure.
Dependency Injection separates what a class needs from how dependencies are built.
```

That sentence explains a large part of Spring.

---

# 2. The Simple World: Manual Object Creation

In a tiny Java program, manual creation is fine.

```java
public class Main {
    public static void main(String[] args) {
        EmailService emailService = new EmailService();
        emailService.sendWelcomeEmail("user-1");
    }
}

class EmailService {
    void sendWelcomeEmail(String userId) {
        System.out.println("Welcome " + userId);
    }
}
```

Architecture:

```text
Main
 |
 v
EmailService
```

Here, there is no problem.

The object has no database, no HTTP client, no configuration, no retry policy, no metrics, no security token, no environment-specific behavior.

Manual creation feels natural.

But real backend classes rarely stay like this.

A service usually needs collaborators.

```text
OrderService
  |
  +-- OrderRepository
  +-- PaymentClient
  +-- InventoryClient
  +-- AuditPublisher
  +-- PricingService
  +-- Clock
```

At that point, `new` starts spreading everywhere.

The application becomes not only business logic, but construction logic.

That is the beginning of the problem.

---

# 3. The Object Graph Mental Model

A backend application is not a list of classes.

It is a graph of objects connected to other objects.

Example:

```text
OrderController
      |
      v
OrderService
  |        |          |
  v        v          v
Repo   PaymentClient  EventPublisher
  |        |          |
  v        v          v
DB     External API   Kafka
```

Every arrow means:

```text
This object needs that object to do its work.
```

In plain Java, you build this graph manually.

```java
DataSource dataSource = new HikariDataSource(...);
OrderRepository repository = new JdbcOrderRepository(dataSource);
PaymentClient paymentClient = new StripePaymentClient("https://stripe-api");
EventPublisher publisher = new KafkaEventPublisher(...);

OrderService service = new OrderService(repository, paymentClient, publisher);
OrderController controller = new OrderController(service);
```

This is not wrong for small apps.

But at production scale, there may be hundreds or thousands of beans.

Manual graph building creates several problems:

```text
Creation order becomes complex.
Configuration spreads across code.
Testing becomes painful.
Implementation swapping becomes hard.
Lifecycle management becomes inconsistent.
Cross-cutting behavior becomes scattered.
```

Spring's container exists to build this graph for you.

```text
You describe the graph.
Spring constructs the graph.
```

---

# 4. What Is Inversion Of Control?

Inversion of Control means control moves from your application code to the framework.

Before IoC:

```java
public class OrderService {
    private final PaymentClient paymentClient = new StripePaymentClient();
}
```

Your class controls dependency creation.

After IoC:

```java
@Service
public class OrderService {
    private final PaymentClient paymentClient;

    public OrderService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }
}
```

Now `OrderService` only says:

```text
I need a PaymentClient.
```

Spring decides:

```text
Which PaymentClient bean should be created and injected?
```

ASCII:

```text
Before IoC

OrderService
   |
   +-- creates StripePaymentClient itself
   |
   v
Tight coupling
```

```text
After IoC

OrderService
   |
   +-- declares need: PaymentClient
           |
           v
      Spring Container provides implementation
```

The control that is inverted is object creation and wiring.

Old control:

```text
My code creates dependencies.
```

New control:

```text
The container creates dependencies and gives them to my code.
```

---

# 5. What Is Dependency Injection?

Dependency Injection is the technique used to implement IoC.

Instead of this:

```java
class OrderService {
    private final PaymentClient paymentClient = new StripePaymentClient();
}
```

Use this:

```java
class OrderService {
    private final PaymentClient paymentClient;

    OrderService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }
}
```

The dependency enters from outside.

```text
Dependency Injection = dependencies are provided to an object instead of created inside it.
```

Real-world analogy:

```text
Bad restaurant design:
Chef buys vegetables, repairs oven, hires waiter, cooks food.

Good restaurant design:
Chef receives ingredients and tools from the kitchen system.
Chef focuses on cooking.
```

Mapping:

```text
Chef             = business service
Ingredients      = dependencies
Kitchen manager  = Spring container
Recipe           = business logic
```

ASCII:

```text
Without DI

Chef
 +-- buys ingredients
 +-- starts oven
 +-- cooks
 +-- bills customer
```

```text
With DI

Kitchen System ---> provides ingredients/tools ---> Chef cooks
```

DI is not magic.

It is disciplined object construction.

---

# 6. The Bad Design: Business Code Creates Infrastructure

This is a common beginner design:

```java
public class CheckoutService {
    private final OrderRepository orderRepository = new PostgresOrderRepository();
    private final PaymentClient paymentClient = new StripePaymentClient();

    public void checkout(String orderId) {
        paymentClient.charge(orderId);
        orderRepository.save(orderId);
    }
}
```

It looks simple.

But it hides infrastructure choices inside business code.

Problems:

```text
CheckoutService knows Postgres implementation.
CheckoutService knows Stripe implementation.
Test cannot easily replace real payment client.
Staging cannot easily swap endpoint.
Changing repository construction changes service code.
```

Diagram:

```text
CheckoutService
   |
   +-- business rule
   +-- infrastructure construction
   +-- vendor choice
   +-- test pain
```

The service has too many responsibilities.

A business service should know the workflow:

```text
charge payment
save order
publish event
```

It should not know:

```text
how Stripe client is constructed
which JDBC URL is used
how Kafka producer is configured
which fake object test should use
```

DI separates these responsibilities.

---

# 7. The Better Design: Depend On Capability

Instead of depending on a concrete class, depend on a capability.

```java
public interface PaymentClient {
    void charge(String orderId);
}
```

Production implementation:

```java
@Component
public class StripePaymentClient implements PaymentClient {
    @Override
    public void charge(String orderId) {
        System.out.println("Charging via Stripe: " + orderId);
    }
}
```

Service:

```java
@Service
public class CheckoutService {
    private final PaymentClient paymentClient;

    public CheckoutService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public void checkout(String orderId) {
        paymentClient.charge(orderId);
    }
}
```

Mental model:

```text
CheckoutService depends on the idea of payment, not Stripe construction.
```

ASCII:

```text
CheckoutService
      |
      v
PaymentClient interface
      ^
      |
StripePaymentClient
```

This gives flexibility:

```text
Production -> StripePaymentClient
Testing    -> FakePaymentClient
Local dev  -> LogOnlyPaymentClient
Future     -> AdyenPaymentClient
```

DI keeps the business workflow stable while implementations can change.

---

# 8. Constructor Injection: The Default You Should Prefer

Constructor injection means dependencies are passed through the constructor.

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

Why this is preferred:

```text
Dependencies are explicit.
Fields can be final.
Object cannot be half-created.
Tests can instantiate class without Spring.
Missing dependencies fail early.
```

ASCII:

```text
Constructor
   |
   +-- requires Repository
   +-- requires PaymentClient
   |
   v
Fully initialized OrderService
```

A good class tells the truth at construction time.

If the object cannot work without `PaymentClient`, the constructor should say so.

That is why constructor injection is clean design, not only Spring style.

Plain unit test:

```java
@Test
void checkoutChargesPayment() {
    PaymentClient fakePayment = new FakePaymentClient();
    OrderRepository fakeRepo = new InMemoryOrderRepository();

    OrderService service = new OrderService(fakeRepo, fakePayment);

    service.checkout("order-1");
}
```

No Spring context needed.

That is a powerful sign of good design.

---

# 9. Field Injection: Why It Is Usually Bad

Field injection looks short:

```java
@Service
public class OrderService {
    @Autowired
    private PaymentClient paymentClient;
}
```

But it hides dependencies.

Problems:

```text
Class looks constructible with default constructor.
Dependencies are invisible from constructor.
Fields cannot be final.
Plain unit testing becomes awkward.
Object can exist in invalid state before injection.
```

ASCII:

```text
new OrderService()
      |
      v
paymentClient = null
      |
      v
Spring later injects field
```

This creates a weaker object model.

Constructor injection says:

```text
You cannot create me unless you provide everything I need.
```

Field injection says:

```text
Create me first; maybe someone injects my dependencies later.
```

For production backend code, prefer explicit construction.

Use field injection rarely, mostly in old code or framework-specific edge cases.

---

# 10. Setter Injection: When It Makes Sense

Setter injection means dependencies are optional or changeable.

```java
@Component
public class ReportService {
    private AuditLogger auditLogger;

    @Autowired
    public void setAuditLogger(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }
}
```

It is less common for mandatory dependencies.

Mental model:

```text
Constructor injection = required dependency
Setter injection      = optional or configurable dependency
Field injection       = hidden dependency
```

ASCII:

```text
Required dependency
   |
   v
Constructor

Optional dependency
   |
   v
Setter
```

Most service/repository/client dependencies are required.

So constructor injection should be your default.

---

# 11. How Spring Builds The Graph

When a Spring Boot app starts, Spring scans classes and builds bean definitions.

```java
@SpringBootApplication
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
```

Spring performs a startup flow:

```text
1. Start application
2. Scan packages
3. Find components
4. Create bean definitions
5. Resolve dependencies
6. Instantiate beans
7. Inject dependencies
8. Apply post-processors
9. Create proxies if needed
10. Start web server
```

ASCII:

```text
@Component classes
       |
       v
Component Scan
       |
       v
Bean Definitions
       |
       v
Dependency Resolution
       |
       v
Bean Creation
       |
       v
Ready ApplicationContext
```

A bean definition is not the object itself.

It is metadata about how the object should be created.

```text
Class name
Scope
Constructor
Dependencies
Initialization rules
Proxy needs
Configuration metadata
```

Spring uses this metadata to build the object graph.

---

# 12. What Is ApplicationContext?

`ApplicationContext` is the Spring container.

It manages beans.

```text
ApplicationContext
  |
  +-- stores bean definitions
  +-- creates bean instances
  +-- resolves dependencies
  +-- manages lifecycle
  +-- publishes events
  +-- exposes environment/config
  +-- supports AOP/proxies
```

Mental model:

```text
ApplicationContext is the runtime registry of your application objects.
```

ASCII:

```text
+----------------------------------+
| ApplicationContext               |
|                                  |
|  orderController --------------+ |
|  orderService ----------------+ | |
|  orderRepository ------------+ | | |
|  paymentClient -------------+ | | | |
|  dataSource ----------------+ | | | |
+----------------------------------+
```

When Spring injects a dependency, it usually looks for a matching bean inside the context.

Example:

```text
OrderService needs PaymentClient
ApplicationContext searches for PaymentClient bean
Finds StripePaymentClient
Injects it into OrderService constructor
```

That is DI in action.

---

# 13. Bean Creation Dry Run

Suppose we have:

```java
@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
}

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

@Repository
public class OrderRepository {}

@Component
public class StripePaymentClient implements PaymentClient {}
```

Startup dry run:

```text
Spring finds OrderController
  needs OrderService

Spring finds OrderService
  needs OrderRepository and PaymentClient

Spring finds OrderRepository
  no dependency

Spring finds StripePaymentClient
  matches PaymentClient

Spring creates:
  OrderRepository
  StripePaymentClient
  OrderService(repository, paymentClient)
  OrderController(orderService)
```

ASCII:

```text
OrderRepository       StripePaymentClient
      |                     |
      +----------+----------+
                 v
           OrderService
                 |
                 v
          OrderController
```

Spring creates dependencies before dependents.

This is why dependency graphs matter.

---

# 14. DI And Interfaces

DI works best when business code depends on stable abstractions.

```java
public interface NotificationSender {
    void send(String userId, String message);
}
```

Implementations:

```java
@Component
public class EmailNotificationSender implements NotificationSender {
    public void send(String userId, String message) {
        System.out.println("Email: " + message);
    }
}

@Component
public class SmsNotificationSender implements NotificationSender {
    public void send(String userId, String message) {
        System.out.println("SMS: " + message);
    }
}
```

But now Spring has a question:

```text
NotificationSender has two beans.
Which one should I inject?
```

If there is ambiguity, Spring fails fast.

Typical error:

```text
No qualifying bean of type 'NotificationSender' available:
expected single matching bean but found 2
```

You can resolve using `@Primary`:

```java
@Primary
@Component
public class EmailNotificationSender implements NotificationSender {}
```

Or `@Qualifier`:

```java
@Service
public class AlertService {
    private final NotificationSender sender;

    public AlertService(@Qualifier("smsNotificationSender") NotificationSender sender) {
        this.sender = sender;
    }
}
```

Mental model:

```text
Interface gives flexibility.
Multiple implementations require selection rules.
```

---

# 15. @Component, @Service, @Repository: Same Bean Idea, Different Meaning

These annotations all register beans.

```java
@Component
class GeneralComponent {}

@Service
class OrderService {}

@Repository
class OrderRepository {}
```

At a high level:

```text
@Component  = generic Spring-managed object
@Service    = business/service layer object
@Repository = persistence layer object
```

ASCII:

```text
Annotation
    |
    v
Component scan finds class
    |
    v
Bean definition registered
    |
    v
Bean created and injectable
```

Why use different stereotypes?

Because they express intent.

```text
@Service tells humans this is business/use-case logic.
@Repository tells humans this is persistence boundary.
@Controller tells humans this is HTTP/web boundary.
```

Spring code should be readable by architecture role.

When you see:

```java
@Service
public class CheckoutService {}
```

You should think:

```text
This is business workflow code.
It may define transaction boundaries.
It should not parse raw HTTP.
It should not contain SQL details directly.
```

Annotations are metadata, but also communication.

---

# 16. @Bean Methods: When You Cannot Annotate The Class

Sometimes the class is not yours.

Example:

```text
RestTemplate
ObjectMapper
DataSource
KafkaProducer
Third-party SDK client
```

You cannot add `@Component` inside third-party library source code.

So you use `@Configuration` + `@Bean`.

```java
@Configuration
public class PaymentClientConfig {

    @Bean
    public PaymentGatewayClient paymentGatewayClient(PaymentProperties properties) {
        return new PaymentGatewayClient(properties.baseUrl(), properties.timeoutMs());
    }
}
```

Mental model:

```text
@Component = Spring creates object by scanning your class.
@Bean      = you write factory method; Spring stores returned object as bean.
```

ASCII:

```text
@Configuration class
       |
       v
@Bean method called by Spring
       |
       v
Returned object enters ApplicationContext
```

Use `@Bean` when:

```text
The class is from a library.
Construction needs special setup.
You want explicit factory logic.
You want multiple named beans of same type.
```

Example with named beans:

```java
@Bean
public Clock systemClock() {
    return Clock.systemUTC();
}
```

Now any service can inject `Clock`.

This also makes testing easier.

---

# 17. Real World Example: Payment Provider Switch

Bad design:

```java
@Service
public class CheckoutService {
    private final StripeClient stripeClient = new StripeClient("prod-key");
}
```

Switching payment provider touches business code.

Better design:

```java
public interface PaymentGateway {
    PaymentResult charge(Money amount, String orderId);
}
```

Stripe implementation:

```java
@Component
@Profile("stripe")
public class StripePaymentGateway implements PaymentGateway {
    public PaymentResult charge(Money amount, String orderId) {
        return PaymentResult.success(orderId);
    }
}
```

Adyen implementation:

```java
@Component
@Profile("adyen")
public class AdyenPaymentGateway implements PaymentGateway {
    public PaymentResult charge(Money amount, String orderId) {
        return PaymentResult.success(orderId);
    }
}
```

Service:

```java
@Service
public class CheckoutService {
    private final PaymentGateway paymentGateway;

    public CheckoutService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public void checkout(Money amount, String orderId) {
        paymentGateway.charge(amount, orderId);
    }
}
```

ASCII:

```text
CheckoutService
      |
      v
PaymentGateway
   ^        ^
   |        |
Stripe    Adyen
```

Runtime selection:

```yaml
spring:
  profiles:
    active: stripe
```

Business code stays stable.

Infrastructure changes through Spring configuration.

---

# 18. Configuration Injection

Dependencies are not only objects.

Configuration is also a dependency.

Bad:

```java
@Component
public class TaxClient {
    private final String baseUrl = "https://prod-tax.company.com";
}
```

Better:

```yaml
tax:
  base-url: https://tax.company.com
  timeout-ms: 1500
```

Typed config:

```java
@ConfigurationProperties(prefix = "tax")
public record TaxProperties(
        String baseUrl,
        int timeoutMs
) {}
```

Client:

```java
@Component
public class TaxClient {
    private final TaxProperties properties;

    public TaxClient(TaxProperties properties) {
        this.properties = properties;
    }
}
```

ASCII:

```text
application.yml
      |
      v
TaxProperties bean
      |
      v
TaxClient receives config
```

Mental model:

```text
Config should enter from outside like any dependency.
```

This makes environments safe:

```text
local   -> localhost services
stage   -> staging services
prod    -> production services
test    -> fake or embedded services
```

Do not hide environment inside Java code.

---

# 19. Bean Scope Mental Model

By default, Spring beans are singleton scoped.

This means:

```text
One bean instance per ApplicationContext.
```

Example:

```java
@Service
public class OrderService {}
```

There is usually one `OrderService` object for the application.

ASCII:

```text
ApplicationContext
   |
   +-- orderService instance #1
   +-- checkoutService instance #1
   +-- paymentClient instance #1
```

Important:

```text
Singleton does not mean JVM global singleton pattern.
It means one instance managed by this Spring container.
```

Production implication:

```text
Singleton beans should usually be stateless.
```

Bad:

```java
@Service
public class CheckoutService {
    private String currentOrderId; // dangerous shared mutable state
}
```

Why dangerous?

```text
Many HTTP requests use the same service instance concurrently.
Shared mutable fields can leak data between requests.
```

Good:

```java
public void checkout(String orderId) {
    String currentOrderId = orderId; // local variable, request-safe
}
```

Mental model:

```text
Spring services are shared workers.
Do not store per-request state in fields.
```

---

# 20. Request Scope And Prototype Scope

Most backend code uses singleton beans.

But Spring supports other scopes.

```text
singleton  = one instance per container
prototype  = new instance each time requested
request    = one instance per HTTP request
session    = one instance per HTTP session
```

ASCII:

```text
Singleton
Request 1 ---> same Service instance
Request 2 ---> same Service instance
Request 3 ---> same Service instance
```

```text
Request scope
Request 1 ---> RequestBean #1
Request 2 ---> RequestBean #2
Request 3 ---> RequestBean #3
```

Use request scope carefully.

Most use cases can pass data through method parameters instead.

Example good design:

```java
@Service
public class PricingService {
    public Price calculate(UserContext userContext, Cart cart) {
        return Price.of(100);
    }
}
```

Avoid turning Spring scope into a substitute for clean method design.

---

# 21. Circular Dependencies

A circular dependency happens when beans depend on each other.

```text
A needs B
B needs A
```

Example:

```java
@Service
public class OrderService {
    public OrderService(PaymentService paymentService) {}
}

@Service
public class PaymentService {
    public PaymentService(OrderService orderService) {}
}
```

ASCII:

```text
OrderService ---> PaymentService
     ^               |
     |               v
     +---------------+
```

Spring cannot cleanly decide which one to create first.

Circular dependencies often indicate design smell.

Possible causes:

```text
Two services know too much about each other.
Workflow is split incorrectly.
Shared logic should be extracted.
Domain event should decouple the direction.
A coordinator service is missing.
```

Better:

```text
CheckoutService orchestrates order + payment

CheckoutService
   |            |
   v            v
OrderService  PaymentService
```

Code:

```java
@Service
public class CheckoutService {
    private final OrderService orderService;
    private final PaymentService paymentService;

    public CheckoutService(OrderService orderService,
                           PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }
}
```

Do not fix circular dependency by blindly adding `@Lazy`.

First ask:

```text
Why do these services depend on each other?
```

---

# 22. Production Bug: NoSuchBeanDefinitionException

Common error:

```text
No qualifying bean of type 'PaymentClient' available
```

Meaning:

```text
Spring tried to inject PaymentClient, but no matching bean exists in ApplicationContext.
```

Debug ladder:

```text
Is implementation annotated with @Component / @Service?
Is it inside component scan package?
Is it excluded by profile?
Is bean created by @Bean method?
Is interface type correct?
Is condition preventing auto-configuration?
```

ASCII:

```text
OrderService needs PaymentClient
          |
          v
ApplicationContext searches beans
          |
          +-- none found
          |
          v
Startup fails
```

Example mistake:

```java
public class StripePaymentClient implements PaymentClient {}
```

No annotation.

Fix:

```java
@Component
public class StripePaymentClient implements PaymentClient {}
```

Or define:

```java
@Bean
PaymentClient paymentClient() {
    return new StripePaymentClient();
}
```

Mental model:

```text
If Spring does not know an object exists, it cannot inject it.
```

---

# 23. Production Bug: Multiple Beans Found

Common error:

```text
Parameter 0 of constructor required a single bean, but 2 were found
```

Example:

```java
@Component
class EmailSender implements NotificationSender {}

@Component
class SmsSender implements NotificationSender {}
```

Service:

```java
@Service
class AlertService {
    AlertService(NotificationSender sender) {}
}
```

Spring asks:

```text
Which NotificationSender should I use?
```

ASCII:

```text
AlertService -> NotificationSender
                   ^       ^
                   |       |
               Email     SMS
```

Fix options:

```java
@Primary
@Component
class EmailSender implements NotificationSender {}
```

Or:

```java
@Service
class AlertService {
    AlertService(@Qualifier("smsSender") NotificationSender sender) {}
}
```

Better architectural question:

```text
Should AlertService really depend on one sender?
Or should it depend on a strategy selector?
```

Example:

```java
@Component
public class NotificationRouter {
    private final Map<String, NotificationSender> senders;

    public NotificationRouter(Map<String, NotificationSender> senders) {
        this.senders = senders;
    }
}
```

Spring can inject all implementations as a list or map.

---

# 24. Injecting Lists And Maps

Spring can inject all beans of a type.

```java
public interface FraudRule {
    boolean suspicious(Order order);
}
```

Rules:

```java
@Component
class HighAmountRule implements FraudRule {
    public boolean suspicious(Order order) { return false; }
}

@Component
class CountryMismatchRule implements FraudRule {
    public boolean suspicious(Order order) { return false; }
}
```

Engine:

```java
@Service
public class FraudEngine {
    private final List<FraudRule> rules;

    public FraudEngine(List<FraudRule> rules) {
        this.rules = rules;
    }

    public boolean suspicious(Order order) {
        return rules.stream().anyMatch(rule -> rule.suspicious(order));
    }
}
```

ASCII:

```text
FraudEngine
   |
   +-- HighAmountRule
   +-- CountryMismatchRule
   +-- VelocityRule
```

This is powerful for extensibility.

Add a new rule:

```java
@Component
class VelocityRule implements FraudRule {}
```

No need to edit `FraudEngine`.

Mental model:

```text
DI can build plugin-style architecture.
```

Use this for:

```text
validators
rules
strategies
handlers
processors
message consumers
pricing policies
```

---

# 25. IoC In Web Request Flow

DI is not only startup wiring.

It affects request handling too.

```java
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final CheckoutService checkoutService;

    public OrderController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    public OrderResponse create(@RequestBody CreateOrderRequest request) {
        return checkoutService.checkout(request);
    }
}
```

Request flow:

```text
HTTP request
    |
    v
DispatcherServlet
    |
    v
OrderController bean
    |
    v
CheckoutService bean
    |
    v
Repository / Client beans
```

ASCII:

```text
POST /orders
    |
    v
Spring MVC infrastructure
    |
    v
Controller created by DI
    |
    v
Service created by DI
    |
    v
Repository created by DI
```

Because Spring created the controller, it could inject the service.

Because Spring created the service, it could inject repository and clients.

This is why manually creating Spring controllers/services inside methods is usually wrong.

Do not do:

```java
OrderService service = new OrderService(...);
```

inside controller code.

Let Spring own the graph.

---

# 26. IoC And Transactions

Transactions are one reason Spring-managed beans matter.

```java
@Service
public class CheckoutService {
    @Transactional
    public void checkout(String orderId) {
        // database writes
    }
}
```

Spring often applies transactions using a proxy.

ASCII:

```text
Caller
  |
  v
Transactional Proxy
  |
  +-- begin transaction
  |
  v
Real CheckoutService.checkout()
  |
  +-- commit or rollback
```

If you create the object manually:

```java
CheckoutService service = new CheckoutService(...);
service.checkout("order-1");
```

Then Spring proxy may not be involved.

Result:

```text
@Transactional may not apply.
```

Mental model:

```text
Spring features often attach to Spring-managed beans, not random objects created with new.
```

That is why IoC matters beyond dependency wiring.

It enables infrastructure wrapping.

---

# 27. Self-Invocation Bug

Classic production bug:

```java
@Service
public class UserService {

    public void updateAndSaveAll(List<User> users) {
        updateUsers(users);
        saveAll(users);
    }

    @Transactional
    public void saveAll(List<User> users) {
        // save users
    }
}
```

Problem:

```text
updateAndSaveAll calls saveAll inside the same object.
The call may not pass through Spring proxy.
@Transactional may be bypassed.
```

ASCII:

```text
External caller -> Proxy -> updateAndSaveAll()
                            |
                            v
                       this.saveAll()
                            |
                            v
                       Real method directly
                       proxy skipped
```

Better design:

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
        // save users
    }
}
```

Now call path:

```text
UserService -> UserSaveService proxy -> transactional method
```

Do not memorize this as a weird rule.

Understand the proxy path.

---

# 28. IoC And Testing

Good DI makes tests simple.

Business class:

```java
public class CheckoutService {
    private final PaymentGateway paymentGateway;
    private final OrderRepository orderRepository;

    public CheckoutService(PaymentGateway paymentGateway,
                           OrderRepository orderRepository) {
        this.paymentGateway = paymentGateway;
        this.orderRepository = orderRepository;
    }

    public void checkout(String orderId) {
        paymentGateway.charge(orderId);
        orderRepository.save(orderId);
    }
}
```

Unit test without Spring:

```java
class FakePaymentGateway implements PaymentGateway {
    boolean charged = false;

    public void charge(String orderId) {
        charged = true;
    }
}

@Test
void checkoutChargesAndSaves() {
    FakePaymentGateway payment = new FakePaymentGateway();
    InMemoryOrderRepository repo = new InMemoryOrderRepository();

    CheckoutService service = new CheckoutService(payment, repo);

    service.checkout("order-1");

    assertTrue(payment.charged);
    assertTrue(repo.exists("order-1"));
}
```

ASCII:

```text
Production graph
CheckoutService -> StripePaymentGateway -> External API

Test graph
CheckoutService -> FakePaymentGateway -> no network
```

This is the key benefit:

```text
Same business class, different dependency graph.
```

Good Spring code should still be good plain Java code.

---

# 29. IoC And Spring Boot Auto-Configuration

Spring Boot uses IoC heavily.

When you add a dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

Spring Boot sees web libraries on classpath and auto-configures web infrastructure.

```text
Classpath contains Spring MVC
      |
      v
Boot creates DispatcherServlet, converters, handler mappings
      |
      v
Your @RestController beans can serve HTTP
```

Similarly for database:

```text
DataSource properties + JDBC library
      |
      v
Boot creates DataSource bean
      |
      v
JdbcTemplate / JPA repositories can use it
```

ASCII:

```text
Dependencies + application.yml
          |
          v
Spring Boot AutoConfiguration
          |
          v
Infrastructure beans
          |
          v
Your beans inject and use them
```

IoC is not just your application classes.

Spring Boot also creates infrastructure beans.

This is why auto-configuration feels magical.

But it is still bean creation based on conditions.

---

# 30. Debugging Bean Wiring In Production

When the app fails to start, follow the object graph.

Common questions:

```text
Which bean failed?
Which dependency was missing?
Was there more than one candidate?
Was the bean excluded by profile?
Was component scan package wrong?
Did auto-configuration back off?
Did configuration binding fail?
Was there a circular dependency?
```

Useful tools:

```bash
java -jar app.jar --debug

curl localhost:8080/actuator/beans
curl localhost:8080/actuator/conditions
curl localhost:8080/actuator/env
curl localhost:8080/actuator/configprops
```

`--debug` helps show auto-configuration condition reports.

Actuator endpoints help inspect runtime state.

Mental model:

```text
Most Spring startup failures are graph-building failures.
```

ASCII:

```text
Startup failure
     |
     v
Find failing bean
     |
     v
Find missing/ambiguous dependency
     |
     v
Check scan/profile/config/conditions
     |
     v
Fix graph
```

Do not randomly add annotations.

Trace the graph.

---

# 31. Real Production Story: Hidden new Broke Observability

A team had this inside a service:

```java
@Service
public class InvoiceService {
    public InvoiceResponse createInvoice(Order order) {
        TaxClient taxClient = new TaxClient("https://tax-api");
        return taxClient.calculate(order);
    }
}
```

Problems appeared:

```text
No timeout configuration.
No metrics around TaxClient.
No retry policy.
No test replacement.
New client created per request.
Production URL hidden in code.
```

The fix:

```java
@Component
public class TaxClient {
    private final RestClient restClient;
    private final TaxProperties properties;

    public TaxClient(RestClient.Builder builder, TaxProperties properties) {
        this.properties = properties;
        this.restClient = builder
                .baseUrl(properties.baseUrl())
                .build();
    }
}
```

Service:

```java
@Service
public class InvoiceService {
    private final TaxClient taxClient;

    public InvoiceService(TaxClient taxClient) {
        this.taxClient = taxClient;
    }
}
```

ASCII:

```text
Bad:
InvoiceService -> new TaxClient per call -> hidden config

Good:
Spring creates TaxClient once -> configured, observable, testable
```

Lesson:

```text
Infrastructure clients should usually be Spring-managed beans.
```

---

# 32. Real Production Story: Wrong Bean Injected

A system had two implementations:

```text
RealEmailSender
MockEmailSender
```

Both were accidentally active in production.

The service used `@Primary` on the mock sender from an old test config.

Result:

```text
Order confirmations were logged but not actually emailed.
```

Root cause:

```text
Bean selection was not environment-safe.
```

Better design:

```java
@Component
@Profile("prod")
public class RealEmailSender implements EmailSender {}

@Component
@Profile({"local", "test"})
public class MockEmailSender implements EmailSender {}
```

Or explicit configuration:

```yaml
notification:
  mode: real
```

Factory:

```java
@Configuration
public class NotificationConfig {
    @Bean
    EmailSender emailSender(NotificationProperties props) {
        if (props.mode().equals("real")) return new RealEmailSender();
        return new MockEmailSender();
    }
}
```

Production lesson:

```text
When multiple beans exist, selection must be deliberate.
```

Do not rely on accidental bean names or old test annotations.

---

# 33. Real Production Story: Singleton State Leak

A service stored request data in a field:

```java
@Service
public class ReportService {
    private String currentUserId;

    public Report generate(String userId) {
        this.currentUserId = userId;
        return buildReport();
    }
}
```

Under load, reports were generated for the wrong user.

Why?

```text
ReportService is singleton.
Many requests use same object concurrently.
currentUserId is shared mutable state.
```

ASCII:

```text
Request A sets currentUserId = user-A
Request B sets currentUserId = user-B
Request A continues and reads user-B
```

Correct:

```java
@Service
public class ReportService {
    public Report generate(String userId) {
        return buildReport(userId);
    }

    private Report buildReport(String userId) {
        return new Report(userId);
    }
}
```

Mental model:

```text
Singleton Spring beans are shared.
Keep per-request data in method parameters/local variables.
```

DI does not remove concurrency responsibility.

---

# 34. Mini Build: Plain Java Container

To understand Spring, build a tiny mental container.

Manual map:

```java
class MiniContainer {
    private final Map<Class<?>, Object> beans = new HashMap<>();

    public <T> void register(Class<T> type, T instance) {
        beans.put(type, instance);
    }

    public <T> T getBean(Class<T> type) {
        return type.cast(beans.get(type));
    }
}
```

Usage:

```java
MiniContainer container = new MiniContainer();

PaymentClient paymentClient = new StripePaymentClient();
OrderRepository repository = new InMemoryOrderRepository();
OrderService orderService = new OrderService(repository, paymentClient);

container.register(PaymentClient.class, paymentClient);
container.register(OrderRepository.class, repository);
container.register(OrderService.class, orderService);

OrderService service = container.getBean(OrderService.class);
service.checkout("order-1");
```

This is very simplified.

But the mental model is useful:

```text
Container stores created objects and gives them to other objects.
```

Spring is much more powerful:

```text
scanning
reflection
constructor resolution
lifecycle callbacks
conditions
profiles
proxying
auto-configuration
scopes
post-processors
```

But the core idea remains:

```text
Managed object graph.
```

---

# 35. Mini Build: Constructor Resolution Mental Model

A real container needs to know constructor dependencies.

Pseudo-flow:

```text
createBean(OrderService)
  inspect constructor
  sees OrderRepository
  createBean(OrderRepository)
  sees PaymentClient
  createBean(PaymentClient)
  call new OrderService(repository, paymentClient)
```

ASCII:

```text
create OrderService
   |
   +-- need OrderRepository
   |      |
   |      +-- create OrderRepository
   |
   +-- need PaymentClient
          |
          +-- create StripePaymentClient
```

Pseudo-code:

```java
Object createBean(Class<?> clazz) {
    Constructor<?> constructor = chooseConstructor(clazz);
    Object[] args = Arrays.stream(constructor.getParameterTypes())
            .map(this::getOrCreateBean)
            .toArray();
    return constructor.newInstance(args);
}
```

This is not production-ready code.

It is only to understand the engine.

Spring does this with many rules and extension points.

But conceptually:

```text
Constructor parameters define edges in the object graph.
```

That is why constructor injection is so readable.

---

# 36. Common Beginner Mistakes

```text
Mistake 1:
Thinking IoC is only a definition.
Correct:
IoC means object creation/wiring control moves to the container.

Mistake 2:
Using new for services inside Spring beans.
Correct:
Inject Spring-managed beans.

Mistake 3:
Using field injection everywhere.
Correct:
Prefer constructor injection.

Mistake 4:
Injecting concrete classes unnecessarily.
Correct:
Depend on interfaces when implementation flexibility matters.

Mistake 5:
Creating circular service dependencies.
Correct:
Introduce coordinator service or domain event.

Mistake 6:
Putting request data in singleton bean fields.
Correct:
Use method parameters/local variables.

Mistake 7:
Using @Qualifier everywhere to hide messy design.
Correct:
Use clear strategy/factory/router patterns when many implementations exist.

Mistake 8:
Assuming @Transactional works on all calls.
Correct:
Understand proxy path and self-invocation.

Mistake 9:
Starting Spring for every unit test.
Correct:
Use constructor injection and plain unit tests for business logic.

Mistake 10:
Thinking annotations are the real design.
Correct:
Annotations describe metadata; object graph design is the real design.
```

---

# 37. Interview Answers

## What is IoC?

IoC means the framework controls object creation and wiring instead of application code doing it manually with `new`. In Spring, the container creates beans, resolves dependencies, manages lifecycle, and provides objects where needed.

## What is Dependency Injection?

Dependency Injection means an object receives its dependencies from outside, usually through a constructor, instead of creating them internally. This reduces coupling and improves testability.

## Why is constructor injection preferred?

Constructor injection makes dependencies explicit, supports `final` fields, prevents partially initialized objects, fails fast when dependencies are missing, and allows plain unit testing without starting Spring.

## What is a Spring bean?

A Spring bean is an object created, configured, wired, and managed by the Spring container.

## What is ApplicationContext?

`ApplicationContext` is the Spring container that stores bean definitions, creates beans, resolves dependencies, manages lifecycle, publishes events, and provides infrastructure integration.

## What happens if two beans implement the same interface?

Spring cannot choose automatically unless one is marked `@Primary`, one is selected using `@Qualifier`, profiles/conditions remove ambiguity, or the injection point accepts all beans as a list/map.

## Why can `new` be dangerous inside Spring services?

Objects created manually are not managed by Spring, so they do not receive dependency injection, configuration, lifecycle callbacks, proxies, transactions, metrics, or other framework behavior.

## What is a circular dependency?

A circular dependency happens when bean A needs bean B and bean B needs bean A. It often indicates poor responsibility boundaries and should usually be fixed by redesigning the service flow.

## Why should singleton beans be stateless?

Spring singleton beans are shared across requests and threads. Mutable per-request fields can leak data or cause race conditions.

## How does DI help testing?

DI allows tests to provide fake or in-memory dependencies to the same business class, so business logic can be tested without real databases, external APIs, Kafka, or Spring context startup.

---

# 38. Debugging Checklist

```text
[ ] Read the full startup error, not only the first line.
[ ] Identify the bean that failed to create.
[ ] Identify the missing or ambiguous dependency.
[ ] Check if the class has @Component/@Service/@Repository.
[ ] Check if it is inside component scan package.
[ ] Check active profiles.
[ ] Check @Conditional / auto-configuration conditions.
[ ] Check @Bean method exists for third-party classes.
[ ] Check if multiple implementations need @Primary or @Qualifier.
[ ] Check for circular dependencies.
[ ] Check configuration properties binding.
[ ] Check if a manually created object bypasses Spring.
[ ] Check if proxy-based behavior is bypassed by self-invocation.
[ ] Check actuator /beans and /conditions if app starts.
```

Command ladder:

```bash
java -jar app.jar --debug
curl localhost:8080/actuator/beans
curl localhost:8080/actuator/conditions
curl localhost:8080/actuator/configprops
curl localhost:8080/actuator/env
```

Mental model:

```text
Spring startup error = object graph construction problem until proven otherwise.
```

---

# 39. Cheat Sheet

```text
IoC
  Framework controls object creation/wiring.

DI
  Dependencies are provided from outside.

Bean
  Spring-managed object.

ApplicationContext
  Runtime container/registry of beans.

Constructor Injection
  Best default for required dependencies.

Field Injection
  Hidden dependency; avoid for application services.

Setter Injection
  Useful for optional/changeable dependencies.

@Component
  Generic Spring bean.

@Service
  Business/use-case layer bean.

@Repository
  Persistence boundary bean.

@Configuration + @Bean
  Explicit factory method for beans, often third-party classes.

@Primary
  Default bean when multiple candidates exist.

@Qualifier
  Explicit bean selection.

@Profile
  Environment-based bean activation.

Singleton Scope
  One bean instance per Spring container.

Prototype Scope
  New bean instance when requested.

Circular Dependency
  A needs B and B needs A; usually design smell.

Self Invocation
  Same-object method call may bypass proxy behavior.
```

Production mapping:

```text
Need testability             -> constructor injection
Need provider switch          -> interface + implementation beans
Need env-specific behavior    -> profiles/config properties
Need third-party object       -> @Bean factory method
Need all strategies           -> inject List<T> or Map<String,T>
Need transaction behavior     -> Spring-managed proxy path
Need safe singleton service   -> stateless fields
Need debug startup failure    -> trace bean graph
```

---

# 40. One Picture To Remember

```text
Your code should say WHAT it needs.
Spring decides HOW to provide it.
```

Full picture:

```text
Classes
  |
  | @Component / @Service / @Repository / @Bean
  v
Bean Definitions
  |
  v
ApplicationContext
  |
  +-- creates objects
  +-- resolves constructor parameters
  +-- injects dependencies
  +-- applies configuration
  +-- manages lifecycle
  +-- creates proxies
  v
Running Object Graph

HTTP Request
  |
  v
Controller Bean
  |
  v
Service Bean
  |
  +-- Repository Bean -> Database
  +-- Client Bean -----> External API
  +-- Publisher Bean --> Kafka
```

Final memory hook:

```text
IoC is the decision to stop building the object graph manually.
DI is the mechanism of giving each object what it needs from that graph.
Spring is the production-grade container that builds, manages, and enhances that graph.
```

---

# 41. Final Sentence

Do not memorize IoC and DI as interview definitions.

Remember the production reason:

```text
A backend is a graph of collaborating objects.
Spring builds and manages that graph so business code stays focused, testable, configurable, and production-ready.
```
