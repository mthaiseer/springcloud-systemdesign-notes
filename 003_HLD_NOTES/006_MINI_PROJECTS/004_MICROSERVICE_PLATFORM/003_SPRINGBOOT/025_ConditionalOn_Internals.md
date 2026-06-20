# 025_ConditionalOn_Internals — The Startup Decision Gate Model

## Core Mental Model

Do not imagine `@ConditionalOn...` annotations as decoration.

The better mental model is:

> **`@ConditionalOn...` annotations are startup decision gates. They decide whether a configuration class or bean definition is allowed to enter the Spring container.**

```text
Configuration / Bean Candidate
              |
              v
+-----------------------------+
| Conditional Gate            |
|-----------------------------|
| Is class present?           |
| Is property enabled?        |
| Is bean missing?            |
| Is app web/non-web?         |
| Is resource present?        |
+-----------------------------+
              |
       +------+------+
       |             |
       v             v
   Match          No match
       |             |
       v             v
 Register        Skip
 bean            bean
```

This chapter teaches exactly one idea:

> **Spring Boot does not create auto-configured beans blindly; it first runs condition checks during startup, and only matching candidates become bean definitions.**

If you remember only one sentence:

> **`@ConditionalOn...` is Spring Boot’s “should this bean exist?” decision gate.**

---

## Why This Exists

Spring Boot supports many application types:

```text
Web MVC app
Reactive WebFlux app
Batch app
CLI app
JPA app
Mongo app
Kafka app
Security app
Actuator app
Redis app
```

But one application does not need all possible beans.

A REST API may need:

```text
DispatcherServlet
Tomcat
Jackson
Controller mappings
```

A CLI app may not need:

```text
Tomcat
DispatcherServlet
HTTP filters
```

A JPA app may need:

```text
DataSource
EntityManagerFactory
TransactionManager
JpaRepository proxies
```

A Mongo-only app may not need:

```text
EntityManagerFactory
JpaTransactionManager
Hibernate
```

So Spring Boot must decide:

```text
Should this bean be created?
Should this configuration apply?
Should this feature activate?
Should Boot back off because user already configured it?
```

That is why conditions exist.

Without conditions, Boot would create too much.

```text
Too many beans
Wrong infrastructure
Bean conflicts
Unwanted security
Wrong web server
Startup failures
Unexpected behavior
```

Conditions make auto-configuration safe and flexible.

---

## Problem Statement

You add a dependency:

```xml
<artifactId>spring-boot-starter-web</artifactId>
```

Now Spring Boot creates web infrastructure.

You add another dependency:

```xml
<artifactId>spring-boot-starter-security</artifactId>
```

Now Spring Boot may create default security infrastructure.

You define your own bean:

```java
@Bean
public ObjectMapper objectMapper() {
    return new ObjectMapper();
}
```

Now Spring Boot may skip its default `ObjectMapper`.

The core problem:

> **How does Spring Boot decide what to activate and what to skip during startup?**

The answer:

```text
It evaluates Condition objects behind @ConditionalOn... annotations.
```

Every condition gives a yes/no answer.

```text
yes  -> include configuration/bean definition
no   -> exclude configuration/bean definition
```

---

## Real World Analogy

Imagine airport boarding gates.

A passenger does not board just because they are inside the airport.

They must pass checks:

```text
Has ticket?
Correct flight?
Passport valid?
Security cleared?
Boarding group called?
```

Only then:

```text
Passenger enters aircraft.
```

Spring Boot conditions work the same way.

```text
Passenger                  Bean candidate
Boarding gate              @ConditionalOn...
Ticket                     classpath/property/bean state
Allowed to board           bean definition registered
Denied boarding            bean skipped
```

Important:

> **A bean class existing in the codebase does not mean it boards the container. It must pass the condition gate.**

---

## The One Mental Model

Think of Spring Boot startup as a selection process.

```text
Many possible beans
        |
        v
Condition gates
        |
        v
Only matching beans enter ApplicationContext
```

ASCII picture:

```text
Candidate: DataSource bean
        |
        v
+----------------------------------+
| Conditions                       |
|----------------------------------|
| Is JDBC class present?           |
| Is DataSource class present?     |
| Is this not a reactive-only app? |
| Is no DataSource bean present?   |
| Are datasource props available?  |
+----------------------------------+
        |
        v
Match -> register DataSource bean definition
No    -> skip DataSource bean definition
```

Spring Boot is mostly asking:

```text
Does this application appear to need this thing?
Has the user already provided this thing?
Is this thing enabled by property?
```

---

## Core Concepts

## Condition

A condition is logic that returns match or no match.

Conceptually:

```java
public interface Condition {
    boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);
}
```

Spring Boot provides many ready-made condition annotations.

## `@Conditional`

Base Spring annotation.

```java
@Conditional(MyCondition.class)
@Bean
public MyService myService() {
    return new MyService();
}
```

It means:

```text
Only create this bean if MyCondition matches.
```

Spring Boot builds specialized annotations on top of this.

## `@ConditionalOnClass`

Applies when a class exists on the classpath.

```java
@ConditionalOnClass(name = "com.fasterxml.jackson.databind.ObjectMapper")
```

Mental model:

```text
If Jackson exists, JSON support can be configured.
```

## `@ConditionalOnMissingBean`

Applies when a bean is absent.

```java
@Bean
@ConditionalOnMissingBean
public ObjectMapper objectMapper() {
    return new ObjectMapper();
}
```

Mental model:

```text
Create default only if user did not provide one.
```

## `@ConditionalOnBean`

Applies when another bean already exists.

```java
@Bean
@ConditionalOnBean(DataSource.class)
public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
}
```

Mental model:

```text
Create helper only if required foundation exists.
```

## `@ConditionalOnProperty`

Applies based on property value.

```java
@ConditionalOnProperty(
    prefix = "audit",
    name = "enabled",
    havingValue = "true"
)
```

Mental model:

```text
Feature flag at startup.
```

## `@ConditionalOnWebApplication`

Applies only for web apps.

```java
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
```

Mental model:

```text
Only configure servlet web infrastructure if this is a servlet web application.
```

## `@ConditionalOnResource`

Applies if a resource exists.

```java
@ConditionalOnResource(resources = "classpath:schema.sql")
```

Mental model:

```text
Only configure this if a specific file/resource is present.
```

---

## Internal Architecture

```text
Auto-Configuration Candidate
        |
        v
ConfigurationClassParser
        |
        v
ConditionEvaluator
        |
        v
Condition objects
        |
        +-- OnClassCondition
        +-- OnBeanCondition
        +-- OnPropertyCondition
        +-- OnWebApplicationCondition
        +-- OnResourceCondition
        |
        v
ConditionOutcome
        |
        +-- match
        +-- no match
        |
        v
Register or skip bean definition
```

More concrete:

```text
@SpringBootApplication
        |
        v
@EnableAutoConfiguration
        |
        v
AutoConfigurationImportSelector
        |
        v
Candidate auto-config classes
        |
        v
Condition evaluation
        |
        v
Only matching classes imported
        |
        v
Bean methods inside them evaluated
        |
        v
Only matching beans registered
```

Important distinction:

```text
Condition on class
  can skip entire configuration class.

Condition on bean method
  can skip only that specific bean.
```

---

## Internal Working

Consider this simplified auto-configuration:

```java
@AutoConfiguration
@ConditionalOnClass(name = "com.example.AuditClient")
@ConditionalOnProperty(prefix = "audit", name = "enabled", havingValue = "true")
public class AuditAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuditService auditService(AuditClient auditClient) {
        return new DefaultAuditService(auditClient);
    }
}
```

Startup logic:

```text
1. Boot discovers AuditAutoConfiguration as candidate.
2. ConditionEvaluator checks @ConditionalOnClass.
3. Is AuditClient on classpath?
   - no -> skip entire configuration
   - yes -> continue
4. ConditionEvaluator checks @ConditionalOnProperty.
5. Is audit.enabled=true?
   - no -> skip entire configuration
   - yes -> continue
6. Configuration class is imported.
7. Bean method auditService() is inspected.
8. @ConditionalOnMissingBean checks if AuditService already exists.
9. If no AuditService exists, register bean definition.
10. During context refresh, create DefaultAuditService bean.
```

The key idea:

```text
Conditions usually run before actual bean creation.
They decide whether bean definitions enter the container.
```

---

## Rich ASCII Diagram — Gate Before Bean Definition

```text
Candidate bean method:

@Bean
@ConditionalOnMissingBean
AuditService auditService()

          |
          v
+---------------------------------------+
| ConditionEvaluator                    |
|---------------------------------------|
| Does AuditService bean already exist? |
+---------------------------------------+
          |
      +---+---+
      |       |
      v       v
    No bean   Bean exists
      |       |
      v       v
 Register    Skip
 definition  method
      |
      v
 Later during refresh:
 create AuditService object
```

Remember:

```text
Condition check happens before the bean object exists.
```

---

## Rich ASCII Diagram — Multiple Gates

```text
AuditAutoConfiguration
          |
          v
+----------------------------+
| Gate 1: classpath          |
| Is AuditClient present?    |
+----------------------------+
          |
      yes v
+----------------------------+
| Gate 2: property           |
| audit.enabled=true?        |
+----------------------------+
          |
      yes v
+----------------------------+
| Gate 3: missing bean       |
| AuditService absent?       |
+----------------------------+
          |
      yes v
+----------------------------+
| Register DefaultAuditService|
+----------------------------+
```

If any gate fails:

```text
bean/configuration does not enter the container.
```

---

## Step-by-Step Dry Run — `@ConditionalOnClass`

Code:

```java
@AutoConfiguration
@ConditionalOnClass(name = "redis.clients.jedis.Jedis")
public class RedisClientAutoConfiguration {

    @Bean
    public RedisHealthChecker redisHealthChecker() {
        return new RedisHealthChecker();
    }
}
```

Scenario A:

```text
Jedis dependency exists.
```

Flow:

```text
1. Boot loads RedisClientAutoConfiguration candidate.
2. OnClassCondition checks classpath.
3. Jedis class found.
4. Condition matches.
5. Configuration is imported.
6. redisHealthChecker bean definition is registered.
```

Scenario B:

```text
Jedis dependency missing.
```

Flow:

```text
1. Boot loads candidate.
2. OnClassCondition checks classpath.
3. Jedis class not found.
4. Condition fails.
5. Entire configuration skipped.
6. No RedisHealthChecker bean.
```

Mental model:

```text
Classpath is a feature signal.
```

---

## Step-by-Step Dry Run — `@ConditionalOnMissingBean`

Auto-config:

```java
@Bean
@ConditionalOnMissingBean
public PaymentClient paymentClient() {
    return new DefaultPaymentClient();
}
```

Scenario A: user provides no bean.

```text
1. Spring evaluates bean method condition.
2. It searches bean definitions for PaymentClient.
3. None found.
4. Condition matches.
5. DefaultPaymentClient bean definition registered.
```

Scenario B: user provides custom bean.

```java
@Bean
public PaymentClient paymentClient() {
    return new StripePaymentClient();
}
```

Flow:

```text
1. User PaymentClient bean definition exists.
2. Auto-config bean method checks @ConditionalOnMissingBean.
3. PaymentClient already exists.
4. Condition fails.
5. DefaultPaymentClient is skipped.
6. StripePaymentClient is used.
```

This is Boot's back-off mechanism.

---

## Step-by-Step Dry Run — `@ConditionalOnProperty`

Code:

```java
@Bean
@ConditionalOnProperty(
    prefix = "feature.recommendations",
    name = "enabled",
    havingValue = "true"
)
public RecommendationService recommendationService() {
    return new RecommendationService();
}
```

Properties:

```properties
feature.recommendations.enabled=true
```

Flow:

```text
1. Spring reads Environment.
2. OnPropertyCondition checks feature.recommendations.enabled.
3. Value equals true.
4. Condition matches.
5. Bean definition registered.
```

If property is:

```properties
feature.recommendations.enabled=false
```

Then:

```text
Condition fails.
Bean skipped.
```

Important:

```text
@ConditionalOnProperty is evaluated at startup.
Changing property after startup does not automatically create/remove bean.
```

---

## Step-by-Step Dry Run — Web Application Condition

Code:

```java
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletOnlyConfiguration {
}
```

Servlet app:

```text
spring-boot-starter-web present
Servlet web context selected
Condition matches
```

Reactive app:

```text
spring-boot-starter-webflux present
Reactive web context selected
Condition may not match for SERVLET
```

CLI app:

```text
spring.main.web-application-type=none
Condition fails
```

Mental model:

```text
App type is also a startup signal.
```

---

## Java Code Example — Custom Feature Toggle

Suppose you want an audit feature that should exist only when enabled.

### Property

```properties
audit.enabled=true
```

### Service

```java
public interface AuditService {
    void record(String event);
}
```

```java
public class DefaultAuditService implements AuditService {

    @Override
    public void record(String event) {
        System.out.println("AUDIT: " + event);
    }
}
```

### Configuration

```java
@Configuration
public class AuditConfiguration {

    @Bean
    @ConditionalOnProperty(
            prefix = "audit",
            name = "enabled",
            havingValue = "true"
    )
    public AuditService auditService() {
        return new DefaultAuditService();
    }
}
```

### Consumer

```java
@Service
public class OrderService {

    private final Optional<AuditService> auditService;

    public OrderService(Optional<AuditService> auditService) {
        this.auditService = auditService;
    }

    public void createOrder() {
        auditService.ifPresent(audit -> audit.record("order-created"));
    }
}
```

Internal execution:

```text
audit.enabled=true
  -> AuditService bean exists
  -> Optional contains service
  -> audit recorded

audit.enabled=false
  -> AuditService bean skipped
  -> Optional.empty
  -> order still works
```

This is safe optional infrastructure.

---

## Spring Boot Example — Default Bean With Override

### Library Default

```java
@Configuration
public class NotificationConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public NotificationSender notificationSender() {
        return new EmailNotificationSender();
    }
}
```

### User Override

```java
@Configuration
public class CustomNotificationConfig {

    @Bean
    public NotificationSender notificationSender() {
        return new SmsNotificationSender();
    }
}
```

Startup:

```text
1. User bean definition is registered.
2. Auto-config checks missing bean.
3. NotificationSender already exists.
4. Default EmailNotificationSender skipped.
5. SmsNotificationSender used.
```

This is how Boot allows custom behavior without forcing you to exclude configuration.

---

## Production Scale Example

Imagine your platform team builds an internal starter:

```text
company-spring-boot-starter-observability
```

It should auto-configure:

```text
Request tracing
Metrics tags
Audit logging
Correlation ID filter
Standard health indicators
```

But only when safe.

Conditions:

```java
@ConditionalOnClass(MeterRegistry.class)
@ConditionalOnProperty(prefix = "company.observability", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnMissingBean(CorrelationIdFilter.class)
```

Why conditions matter at scale:

```text
Different services have different dependencies.
Some services use servlet stack.
Some use reactive stack.
Some define custom tracing.
Some disable audit logging.
Some are command-line workers.
```

Without conditions, the internal starter would break services.

With conditions, it adapts.

```text
Service A has web MVC -> gets servlet correlation filter
Service B is worker   -> no servlet filter
Service C custom bean -> default backs off
Service D disabled    -> no observability beans
```

Conditions are what make shared starters production-safe.

---

## Production Failure Story

A team created an internal payment starter.

It auto-created a default `PaymentClient`:

```java
@Bean
public PaymentClient paymentClient() {
    return new RealPaymentClient();
}
```

There was no condition.

In tests, teams wanted a fake client:

```java
@Bean
public PaymentClient fakePaymentClient() {
    return new FakePaymentClient();
}
```

Startup failed:

```text
NoUniqueBeanDefinitionException:
expected single matching bean but found 2:
paymentClient, fakePaymentClient
```

Root cause:

```text
Starter did not use @ConditionalOnMissingBean.
Default bean did not back off when app provided custom bean.
```

Fix:

```java
@Bean
@ConditionalOnMissingBean
public PaymentClient paymentClient() {
    return new RealPaymentClient();
}
```

Another failure:

```text
Starter registered servlet filter in batch jobs.
Batch jobs had no servlet environment.
Startup failed.
```

Fix:

```java
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
```

Lesson:

> **Every reusable auto-configuration should be guarded by conditions that describe exactly when it is safe to activate.**

---

## Debugging Mindset

When a conditional bean surprises you, ask:

```text
1. Was the condition placed on the class or bean method?
2. Was the required class actually on the classpath?
3. Was the property name/value correct?
4. Was the bean already present?
5. Was the app type servlet/reactive/non-web?
6. Was the bean hidden behind a profile?
7. Did condition evaluation happen before the bean you expected existed?
```

### Symptom Map

```text
Expected bean missing
  -> condition failed
  -> property disabled
  -> class missing
  -> profile inactive
  -> wrong app type

Unexpected default bean exists
  -> missing @ConditionalOnMissingBean
  -> condition too broad
  -> property matchIfMissing enabled

Custom bean ignored
  -> wrong type
  -> wrong bean name
  -> auto-config condition evaluated differently
  -> multiple beans causing ambiguity

Feature enabled in production accidentally
  -> property matchIfMissing=true
  -> dependency activated classpath condition
  -> default condition too permissive
```

### Debug Tools

Run:

```bash
java -jar app.jar --debug
```

Look for:

```text
CONDITIONS EVALUATION REPORT
```

Actuator:

```properties
management.endpoints.web.exposure.include=conditions
```

Then inspect:

```text
/actuator/conditions
```

Use carefully because it can expose internal configuration details.

---

## Common Misconceptions

## Misconception 1 — “`@ConditionalOn...` runs at every request”

No.

Conditions are startup-time decisions.
They decide bean definition registration during application context creation.

## Misconception 2 — “`@ConditionalOnMissingBean` checks Java classes”

No.

It checks Spring beans in the application context/bean factory, not plain classes sitting on the classpath.

## Misconception 3 — “`@ConditionalOnClass` creates the class”

No.

It only checks whether a class is present.
It does not instantiate that class.

## Misconception 4 — “If property changes, bean appears automatically”

No.

Most conditions are evaluated at startup.
Changing external config later does not automatically rebuild the application context.

## Misconception 5 — “Conditions are only for Spring Boot internals”

No.

You can use them in your own configuration, especially for reusable modules, internal starters, optional integrations, and feature-specific beans.

## Misconception 6 — “Use conditions everywhere”

No.

Too many conditions can make the app hard to reason about.
Use them where bean existence should truly depend on environment, classpath, properties, or existing beans.

---

## Performance Considerations

Condition evaluation has startup cost, but usually it is small compared with:

```text
database initialization
entity scanning
web server startup
bean creation
migrations
external calls
```

However, in very large apps:

```text
Many auto-configurations
Large classpaths
Many conditional beans
Complex custom conditions
```

can contribute to startup time.

Better practices:

```text
Keep custom conditions simple.
Avoid network calls inside conditions.
Avoid heavy file scanning inside conditions.
Use standard Boot conditions when possible.
Make conditions deterministic.
```

Bad custom condition:

```text
Condition calls external service to decide whether bean should exist.
```

Good condition:

```text
Condition checks property/classpath/bean presence.
```

Conditions should be fast and local.

---

## Scalability Considerations

Conditions help scale codebases, not request throughput directly.

They allow one shared library to adapt to many services:

```text
same starter
different classpaths
different properties
different app types
different custom beans
```

This matters in platform engineering.

Example:

```text
100 microservices use company-observability-starter
```

Good conditions mean:

```text
web apps get filters
workers skip filters
apps with Micrometer get metrics
apps without Micrometer skip metrics
custom implementations override defaults
```

Bad conditions mean:

```text
shared starter breaks half the fleet
unexpected beans appear
startup becomes fragile
teams fork configuration
```

Senior rule:

> **Reusable infrastructure must be conditional, override-friendly, and observable through condition reports.**

---

## Failure Investigation Playbook

## Step 1 — Identify the bean/configuration

Ask:

```text
Which bean is missing or unexpected?
Which auto-configuration or config class owns it?
```

## Step 2 — Check the condition report

Run:

```bash
java -jar app.jar --debug
```

Find:

```text
Positive matches
Negative matches
```

## Step 3 — Read condition messages

Look for:

```text
did not find required class
found unwanted bean
did not find property
matched because property value true
did not match because not a web application
```

## Step 4 — Verify inputs

Check:

```text
mvn dependency:tree
active profiles
application.yml
environment variables
bean names/types
web application type
```

## Step 5 — Fix the smallest input

Prefer:

```text
correct property
add/remove dependency
define custom bean
fix profile
fix condition
```

Avoid immediately excluding large auto-configuration unless necessary.

---

## Interview Q&A

### Q1. What are `@ConditionalOn...` annotations?

Strong answer:

> They are Spring Boot condition annotations used during startup to decide whether a configuration class or bean method should contribute bean definitions to the application context. They check things like classpath, properties, existing beans, resources, and web application type.

### Q2. What is the difference between `@ConditionalOnClass` and `@ConditionalOnBean`?

Strong answer:

> `@ConditionalOnClass` checks whether a class exists on the classpath. `@ConditionalOnBean` checks whether a Spring bean exists in the application context. Classpath presence does not mean a bean exists.

### Q3. What does `@ConditionalOnMissingBean` do?

Strong answer:

> It creates the bean only if no matching bean already exists. It is commonly used by auto-configuration so Boot can provide defaults while backing off when the application defines a custom bean.

### Q4. When are conditions evaluated?

Strong answer:

> Conditions are evaluated during application context startup, mostly while configuration classes and bean methods are being processed. They usually decide whether bean definitions are registered before actual bean objects are created.

### Q5. How do you debug why a conditional bean was not created?

Strong answer:

> I run the app with `--debug` or inspect `/actuator/conditions`. The condition evaluation report shows positive and negative matches and explains which condition matched or failed.

### Q6. Can I use conditions in my own code?

Strong answer:

> Yes. They are useful for optional integrations, feature toggles, reusable internal starters, and environment-specific infrastructure. But they should be used carefully to avoid hidden behavior.

### Q7. Why did adding a dependency activate a feature?

Strong answer:

> Because many auto-configurations use `@ConditionalOnClass`. Adding a dependency puts new classes on the classpath, which can make those conditions match and activate default beans.

---

## Production Checklist

```text
Condition Design
[ ] Is the condition necessary?
[ ] Is it checking the right signal?
[ ] Is it on the class or method intentionally?
[ ] Is it deterministic and fast?

Override Safety
[ ] Does default bean use @ConditionalOnMissingBean?
[ ] Can application teams replace the default?
[ ] Are multiple beans avoided?

Property Safety
[ ] Are property names documented?
[ ] Is matchIfMissing intentional?
[ ] Are production defaults safe?

Classpath Safety
[ ] Could adding a dependency accidentally activate this?
[ ] Is @ConditionalOnClass specific enough?
[ ] Are optional dependencies handled safely?

Debugging
[ ] Is condition visible in condition report?
[ ] Can teams understand why it matched?
[ ] Are startup logs clear enough?
```

---

## One-Page Cheat Sheet

```text
ConditionalOn Internals
=======================

Core Idea
---------
Startup decision gates for bean/config registration.

Condition Input Signals
-----------------------
Classpath
Properties
Existing beans
Missing beans
Resources
Web app type
Profiles

Common Annotations
------------------
@ConditionalOnClass
  Class exists on classpath.

@ConditionalOnMissingClass
  Class does not exist.

@ConditionalOnBean
  Bean exists in context.

@ConditionalOnMissingBean
  Bean does not exist; create default.

@ConditionalOnProperty
  Property has expected value.

@ConditionalOnWebApplication
  App is web app.

@ConditionalOnResource
  Resource exists.

Key Distinction
---------------
Class exists != Bean exists.

Timing
------
Evaluated during startup,
before or while bean definitions are registered.

Debug
-----
java -jar app.jar --debug
/actuator/conditions

Best Sentence
-------------
@ConditionalOn... answers:
Should this configuration or bean definition enter the Spring container?
```

---

## Last-Minute Interview Revision

Do not say:

```text
`@ConditionalOn` magically enables beans.
```

Say:

```text
`@ConditionalOn...` annotations are startup-time condition gates. Spring evaluates them using the environment, classpath, bean factory, and application type. If they match, the configuration or bean definition is registered; if they do not match, it is skipped.
```

Senior version:

```text
I use conditions to make reusable configuration safe: activate only when required dependencies and properties exist, back off when users provide custom beans, and make the decision visible through the condition evaluation report.
```

---

## One Picture To Remember

```text
              CONDITIONALON STARTUP GATE

Candidate Configuration / Bean
              |
              v
+--------------------------------------+
| Condition Evaluator                  |
|--------------------------------------|
| @ConditionalOnClass       classpath? |
| @ConditionalOnBean        bean?      |
| @ConditionalOnMissingBean missing?   |
| @ConditionalOnProperty    property?  |
| @ConditionalOnWebApp      app type?  |
+--------------------------------------+
              |
       +------+------+
       |             |
       v             v
   MATCH           NO MATCH
       |             |
       v             v
Register bean      Skip
definition         candidate
       |
       v
Bean created later during context refresh
```

Final retention sentence:

> **`@ConditionalOn...` is the gatekeeper that decides whether Spring Boot should register a bean/configuration during startup.**
