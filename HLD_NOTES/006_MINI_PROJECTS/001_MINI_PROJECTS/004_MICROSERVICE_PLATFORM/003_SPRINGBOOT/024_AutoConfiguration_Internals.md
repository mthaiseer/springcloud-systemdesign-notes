# 024_AutoConfiguration_Internals — The Conditional Assembly Model

## Core Mental Model

Do not imagine Spring Boot auto-configuration as:

```text
Spring Boot magically knows what I want.
```

That model is dangerous.

The better mental model is:

> **Auto-configuration is a conditional assembly engine. It looks at your classpath, properties, and existing beans, then conditionally registers default bean definitions.**

```text
Classpath + Properties + Existing Beans
                  |
                  v
        +----------------------+
        | Auto-Configuration   |
        | condition engine     |
        +----------------------+
                  |
                  v
        Register default beans
        only if conditions match
```

This chapter teaches exactly one idea:

> **Spring Boot auto-configuration is not magic; it is a set of conditional configuration classes that add infrastructure beans only when the application looks like it needs them.**

If you remember only one sentence:

> **Auto-configuration asks: “Given what is on the classpath, what properties exist, and what beans are missing, what default beans should I register?”**

---

## Why This Exists

Without Spring Boot auto-configuration, every Spring application would need lots of manual setup.

For a web app, you would configure:

```text
DispatcherServlet
HandlerMapping
HandlerAdapter
MessageConverters
Jackson ObjectMapper
Embedded Tomcat
Error handling
Static resources
Validation
```

For a JPA app, you would configure:

```text
DataSource
EntityManagerFactory
JpaTransactionManager
Hibernate properties
Repository scanning
Transaction infrastructure
```

For a security app, you would configure:

```text
SecurityFilterChain
Authentication providers
Password encoder
CSRF rules
OAuth2 resource server
JWT decoder
```

This is a lot of boilerplate.

Spring Boot exists to reduce this setup.

But it cannot blindly create everything.

It must ask questions:

```text
Is this a web app?
Is Tomcat on the classpath?
Did the user already define a web server factory?
Is spring.datasource.url configured?
Is Hibernate available?
Did the user define a DataSource manually?
Is security on the classpath?
Did the user define a custom SecurityFilterChain?
```

Auto-configuration exists to answer these questions and assemble sensible defaults.

---

## Problem Statement

You add this dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

Then this endpoint works:

```java
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
```

But you never manually configured:

```text
Tomcat
DispatcherServlet
JSON converters
request mapping infrastructure
error controller
```

The core problem:

> **How does Spring Boot decide which infrastructure beans to create without you explicitly writing configuration for each one?**

Answer:

```text
Auto-configuration classes are imported.
Each class has conditions.
If conditions match, bean definitions are registered.
If user already provided a bean, Boot usually backs off.
```

Auto-configuration is controlled defaulting.

---

## Real World Analogy

Imagine a smart hotel room setup team.

Before a guest arrives, they inspect the booking:

```text
Guest booked business room?
  Add desk and office chair.

Guest selected baby crib?
  Add crib.

Guest selected sea view?
  Prepare sea-view room.

Guest already brought own pillow?
  Do not add default pillow.

Guest has no breakfast package?
  Do not prepare breakfast tray.
```

Auto-configuration works similarly.

```text
Hotel setup team                  Spring Boot auto-configuration
----------------                  ------------------------------
Booking details                   application.properties
Guest needs                       classpath dependencies
Already provided items            existing user beans
Default hotel items               auto-configured beans
Skip if already provided          @ConditionalOnMissingBean
```

Spring Boot does not randomly add beans.

It inspects conditions and assembles the room.

---

## The One Mental Model

Think of auto-configuration as a chain of conditional recipes.

```text
AutoConfiguration Class
        |
        v
Condition checks
        |
        +-- Is class present?
        +-- Is property enabled?
        +-- Is this a web app?
        +-- Is bean missing?
        +-- Is resource present?
        |
        v
If conditions match:
        register bean definitions
Else:
        skip
```

Picture:

```text
+--------------------------------------------------+
| WebMvcAutoConfiguration                          |
|--------------------------------------------------|
| Condition: is Servlet web app?                   |
| Condition: is Spring MVC on classpath?           |
| Condition: no custom WebMvcConfigurationSupport? |
|                                                  |
| If yes:                                          |
|   register MVC infrastructure beans              |
| If no:                                           |
|   do nothing                                     |
+--------------------------------------------------+
```

The most important rule:

> **Auto-configuration provides defaults, but backs off when you provide your own bean.**

That is how Spring Boot is both convenient and customizable.

---

## Core Concepts

## Auto-Configuration Class

An auto-configuration class is a normal configuration class designed to be imported automatically by Spring Boot.

Conceptually:

```java
@AutoConfiguration
@ConditionalOnClass(SomeLibrary.class)
public class SomeLibraryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SomeClient someClient() {
        return new SomeClient();
    }
}
```

Meaning:

```text
If SomeLibrary is on the classpath
and no SomeClient bean already exists
then create a default SomeClient bean.
```

## Conditional Annotations

Auto-configuration relies heavily on conditions.

Common conditions:

```text
@ConditionalOnClass
  applies if a class exists on the classpath

@ConditionalOnMissingClass
  applies if a class does not exist

@ConditionalOnBean
  applies if a bean already exists

@ConditionalOnMissingBean
  applies if a bean does not already exist

@ConditionalOnProperty
  applies if a property has a certain value

@ConditionalOnWebApplication
  applies only for web apps

@ConditionalOnNotWebApplication
  applies only for non-web apps

@ConditionalOnResource
  applies if a resource exists
```

These conditions are the decision gates.

## Starter

A starter is a dependency bundle.

Example:

```text
spring-boot-starter-web
```

It brings:

```text
Spring MVC
Jackson
Validation support
Embedded Tomcat
Logging dependencies
```

A starter does not itself configure everything.

It puts libraries on the classpath.

Then auto-configuration sees those libraries and activates matching configuration.

```text
Starter adds classpath ingredients.
Auto-configuration cooks the default setup.
```

## Backing Off

Backing off means:

> **If the user defines their own bean, Spring Boot does not create the default one.**

Example:

```java
@Bean
public ObjectMapper objectMapper() {
    return new ObjectMapper()
            .findAndRegisterModules();
}
```

If Boot sees your `ObjectMapper`, it may skip creating its default `ObjectMapper`.

This is the customization model.

You do not usually disable Boot.
You provide a more specific bean.

## Condition Evaluation Report

Spring Boot can explain which auto-configurations matched or did not match.

Run:

```bash
java -jar app.jar --debug
```

You will see:

```text
Positive matches
Negative matches
Exclusions
Unconditional classes
```

This report is the X-ray of auto-configuration.

---

## Internal Architecture

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
Load auto-configuration class names
        |
        v
Evaluate conditions
        |
        v
Register matching bean definitions
        |
        v
ApplicationContext refresh
        |
        v
Create actual beans
```

Important:

```text
Auto-configuration mostly registers bean definitions.
Actual bean creation happens later during context refresh.
```

Detailed pipeline:

```text
1. Spring sees @SpringBootApplication.
2. @EnableAutoConfiguration becomes active.
3. AutoConfigurationImportSelector loads auto-config class list.
4. Spring evaluates conditions on each auto-config class.
5. Matching auto-configurations are imported.
6. Imported configs contribute bean definitions.
7. BeanFactory later creates bean instances.
```

---

## Internal Working

## Step 1 — `@SpringBootApplication`

```java
@SpringBootApplication
public class ShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }
}
```

`@SpringBootApplication` includes:

```text
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```

The auto-configuration part comes from:

```text
@EnableAutoConfiguration
```

## Step 2 — Import Selector Loads Candidates

Spring Boot has a list of auto-configuration candidates.

Modern Boot versions use metadata like:

```text
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

This file contains auto-configuration class names.

Conceptually:

```text
DataSourceAutoConfiguration
HibernateJpaAutoConfiguration
WebMvcAutoConfiguration
JacksonAutoConfiguration
SecurityAutoConfiguration
...
```

These are candidates.

Candidate does not mean active.

## Step 3 — Conditions Are Evaluated

For each candidate, Boot checks conditions.

Example:

```text
DataSourceAutoConfiguration
  Is DataSource class available?
  Is this not a reactive-only setup?
  Is there no user-defined DataSource?
  Are datasource properties present or embedded DB available?
```

If conditions match, it registers definitions.

If conditions fail, it skips.

## Step 4 — Bean Definitions Are Registered

Example:

```text
DataSource bean definition
JdbcTemplate bean definition
TransactionManager bean definition
```

Again:

```text
Definition first.
Object later.
```

## Step 5 — Context Refresh Creates Beans

During refresh:

```text
Bean definitions become actual objects.
Dependencies are injected.
BeanPostProcessors apply proxies/customization.
```

If something fails, startup fails.

---

## Rich ASCII Diagram — Conditional Assembly Engine

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
        +----------------------------------------+
        | Load candidate auto-config classes     |
        +----------------------------------------+
                            |
                            v
        +----------------------------------------+
        | Evaluate conditions                    |
        |----------------------------------------|
        | Classpath?                             |
        | Property?                              |
        | Web app?                               |
        | Existing bean?                         |
        | Missing bean?                          |
        +----------------------------------------+
                            |
             +--------------+--------------+
             |                             |
             v                             v
       Conditions match               Conditions fail
             |                             |
             v                             v
 Register bean definitions             Skip config
             |
             v
    Context refresh creates beans
```

---

## Example — Web MVC Auto-Configuration

You add:

```xml
<artifactId>spring-boot-starter-web</artifactId>
```

Classpath now contains:

```text
DispatcherServlet
Spring MVC
Jackson
Tomcat
```

Boot sees:

```text
Servlet web application = yes
Spring MVC classes present = yes
User has not fully taken over MVC = yes
```

Then it configures:

```text
DispatcherServlet
RequestMappingHandlerMapping
RequestMappingHandlerAdapter
HttpMessageConverters
Jackson JSON support
Error handling
Static resources
Embedded Tomcat
```

Your controller works:

```java
@RestController
public class ProductController {

    @GetMapping("/products")
    public List<String> products() {
        return List.of("keyboard", "mouse");
    }
}
```

Without MVC auto-configuration, your `@GetMapping` would not be enough.
The runtime infrastructure must exist.

---

## Example — DataSource Auto-Configuration

Properties:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shop
spring.datasource.username=shop
spring.datasource.password=secret
```

Dependency:

```text
spring-boot-starter-data-jpa
```

Boot sees:

```text
JDBC classes present
DataSource implementation available
Datasource properties exist
No user-defined DataSource bean
```

Then it creates:

```text
DataSource
EntityManagerFactory
TransactionManager
JPA repository infrastructure
```

Your repository works:

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

The repository is not working alone.
Auto-configuration assembled the infrastructure below it.

```text
Repository proxy
    |
    v
EntityManager
    |
    v
EntityManagerFactory
    |
    v
DataSource
    |
    v
Database
```

---

## Step-by-Step Dry Run — Web App

Input setup:

```text
Dependency: spring-boot-starter-web
Code: @RestController
No custom MVC config
```

Startup dry run:

```text
1. main() calls SpringApplication.run().
2. @EnableAutoConfiguration activates import selector.
3. Boot loads WebMvcAutoConfiguration candidate.
4. Condition checks:
   - Servlet web app? yes
   - Spring MVC on classpath? yes
   - Missing full custom MVC override? yes
5. Web MVC bean definitions are registered.
6. Boot loads ServletWebServerFactoryAutoConfiguration.
7. Condition checks:
   - Servlet web app? yes
   - Tomcat on classpath? yes
8. Tomcat factory bean definition is registered.
9. Context refresh creates MVC beans.
10. Tomcat starts.
11. Controller mappings become active.
```

Result:

```text
GET /products works
```

---

## Step-by-Step Dry Run — JPA App

Input setup:

```text
Dependency: spring-boot-starter-data-jpa
Property: spring.datasource.url exists
Entity: @Entity Product
Repository: ProductRepository extends JpaRepository
```

Startup dry run:

```text
1. Boot loads DataSourceAutoConfiguration.
2. Checks JDBC/DataSource classes.
3. Checks no user DataSource bean.
4. Checks datasource properties.
5. Registers DataSource bean definition.

6. Boot loads HibernateJpaAutoConfiguration.
7. Checks Hibernate/JPA classes.
8. Checks DataSource bean exists.
9. Registers EntityManagerFactory definition.
10. Registers JpaTransactionManager definition.

11. Spring Data JPA repository auto-config activates.
12. ProductRepository proxy bean definition registered.
13. Context refresh creates DataSource.
14. Creates EntityManagerFactory.
15. Creates transaction manager.
16. Creates repository proxy.
17. Services can inject repository.
```

Result:

```text
productRepository.findById() works
```

---

## Step-by-Step Dry Run — User Bean Overrides Default

User defines:

```java
@Configuration
public class JsonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules();
    }
}
```

Boot dry run:

```text
1. JacksonAutoConfiguration candidate loads.
2. Jackson classes are present.
3. Boot checks ObjectMapper condition.
4. User ObjectMapper bean already exists.
5. @ConditionalOnMissingBean fails.
6. Boot backs off from default ObjectMapper.
7. User ObjectMapper is used.
```

Important:

```text
Customizing Boot often means adding a bean,
not rewriting the whole framework.
```

---

## Step-by-Step Dry Run — Property Controls Auto-Configuration

Example:

```properties
spring.sql.init.mode=never
```

Boot dry run:

```text
1. SQL initialization auto-configuration candidate loads.
2. Property is checked.
3. Property says initialization disabled.
4. SQL init beans are not registered.
```

Another example:

```properties
spring.main.web-application-type=none
```

Result:

```text
Web auto-configurations may not apply as servlet web app.
Embedded server does not start.
```

Properties are switches in the auto-configuration decision engine.

---

## Java Code Example — Custom Auto-Configuration Style

Imagine you are building a small internal library.

You want:

```text
If AuditClient is on classpath
and app property audit.enabled=true
and user has no AuditService bean
then create default AuditService.
```

Conceptual auto-config:

```java
@AutoConfiguration
@ConditionalOnClass(AuditClient.class)
@ConditionalOnProperty(prefix = "audit", name = "enabled", havingValue = "true")
public class AuditAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuditService auditService(AuditClient auditClient) {
        return new DefaultAuditService(auditClient);
    }
}
```

Service:

```java
public class DefaultAuditService implements AuditService {

    private final AuditClient auditClient;

    public DefaultAuditService(AuditClient auditClient) {
        this.auditClient = auditClient;
    }

    @Override
    public void record(String event) {
        auditClient.send(event);
    }
}
```

Internal execution:

```text
AuditClient class exists? yes
audit.enabled=true? yes
AuditService bean missing? yes
Register DefaultAuditService bean definition
Create bean during refresh
Inject wherever AuditService is needed
```

If user writes:

```java
@Bean
public AuditService customAuditService() {
    return new CustomAuditService();
}
```

Then:

```text
@ConditionalOnMissingBean fails.
DefaultAuditService is not created.
CustomAuditService wins.
```

That is Boot’s design philosophy.

---

## Spring Boot Example With Condition Report

Run:

```bash
java -jar shop.jar --debug
```

Example style of output:

```text
============================
CONDITIONS EVALUATION REPORT
============================

Positive matches:
-----------------
WebMvcAutoConfiguration matched:
  - found Servlet web application
  - found DispatcherServlet class

DataSourceAutoConfiguration matched:
  - found DataSource class
  - found spring.datasource.url

Negative matches:
-----------------
SecurityAutoConfiguration did not match:
  - Spring Security classes not found

JacksonAutoConfiguration.ObjectMapperConfiguration:
  - did not match because ObjectMapper bean already exists
```

How to interpret:

```text
Positive match
  Auto-config condition passed.

Negative match
  Auto-config skipped and tells why.

MissingBean negative
  You probably provided your own bean.

Class not found
  Dependency/starter missing.

Property mismatch
  Configuration disabled or wrong property value.
```

---

## Production Scale Example

In production, auto-configuration decisions can change behavior dramatically.

Example:

```text
Developer adds spring-boot-starter-security
```

Suddenly:

```text
SecurityAutoConfiguration activates.
Default security filter chain appears.
Endpoints require authentication.
Health checks may fail.
```

Nothing “mystical” happened.

Classpath changed.
Conditions matched.
Default security beans were registered.

Another example:

```text
A team adds actuator dependency.
Management endpoints appear.
Health indicators are auto-configured.
Readiness/liveness behavior changes.
```

Another example:

```text
A team adds data-jpa starter but forgets datasource URL.
DataSource auto-config tries to create DataSource.
Startup fails because no suitable driver/config.
```

At scale, dependency changes are behavior changes.

Senior rule:

> **Every new starter should be treated as a production behavior change because it can activate new auto-configurations.**

---

## Production Failure Story

A team had a public catalog service.

They added this dependency for password hashing utility:

```text
spring-boot-starter-security
```

After deployment, all endpoints started returning 401 Unauthorized.

The team thought:

```text
We did not write any security config.
Why did security turn on?
```

Root cause:

```text
Spring Security classes appeared on the classpath.
SecurityAutoConfiguration conditions matched.
Boot registered a default SecurityFilterChain.
Default security required authentication.
```

Fix:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/catalog/**").permitAll()
                    .anyRequest().authenticated())
            .build();
}
```

Or remove the starter if not needed.

Lesson:

> **Adding a starter changes the classpath; changing the classpath can activate auto-configuration; activated auto-configuration can change runtime behavior.**

---

## Debugging Mindset

When auto-configuration surprises you, ask:

```text
1. Which starter/dependency added the triggering class?
2. Which auto-configuration matched?
3. Which condition caused it to match?
4. Did my custom bean make Boot back off?
5. Did a property enable or disable it?
6. Is this a servlet, reactive, or non-web app?
7. Is my bean created too late or under the wrong profile?
```

### Symptom Map

```text
Unexpected bean exists
  -> Auto-configuration matched due to classpath/property.

Expected bean missing
  -> Condition did not match, profile disabled, property missing, or user bean changed behavior.

Default config not used
  -> @ConditionalOnMissingBean backed off because custom bean exists.

Endpoint suddenly secured
  -> Security starter activated SecurityAutoConfiguration.

DataSource startup failure
  -> JDBC/JPA auto-config matched but properties/driver invalid.

Controller not working
  -> Web MVC auto-config missing or app not servlet web app.

JSON behavior changed
  -> ObjectMapper customizer/bean altered Jackson auto-config.
```

### Debug Tools

```bash
java -jar app.jar --debug
```

Properties:

```properties
logging.level.org.springframework.boot.autoconfigure=DEBUG
```

Actuator condition endpoint if exposed:

```properties
management.endpoints.web.exposure.include=conditions
```

Then inspect:

```text
/actuator/conditions
```

Use carefully in production due to information exposure.

---

## Common Misconceptions

## Misconception 1 — “Auto-configuration creates beans randomly”

No.

It creates beans using conditions.

```text
Classpath + properties + existing beans + app type
```

## Misconception 2 — “Starter equals auto-configuration”

Not exactly.

A starter mainly adds dependencies.
Those dependencies can trigger auto-configurations.

```text
starter -> classpath changes -> conditions match -> auto-config applies
```

## Misconception 3 — “Boot ignores my custom config”

Usually no.

Many Boot defaults use `@ConditionalOnMissingBean`.
If you provide the right bean type, Boot backs off.

## Misconception 4 — “Auto-configuration happens after my app starts”

No.

Auto-configuration contributes bean definitions during startup before the context is fully refreshed.

## Misconception 5 — “Excluding auto-config is always the best customization”

Usually no.

Prefer:

```text
1. Set property
2. Provide custom bean
3. Use customizer bean
4. Exclude auto-configuration only when needed
```

## Misconception 6 — “If dependency compiles, runtime behavior is unchanged”

Wrong.

In Spring Boot, classpath is a configuration signal.
Adding dependencies can activate behavior.

---

## Performance Considerations

Auto-configuration affects startup performance.

Cost sources:

```text
condition evaluation
classpath scanning
configuration class processing
bean definition registration
infrastructure bean creation
```

Usually this is acceptable.

But large applications may suffer from:

```text
too many starters
unused auto-configurations
large classpath
heavy infrastructure beans
slow DataSource/JPA initialization
many entity packages
```

Optimization mindset:

```text
Do not remove auto-config blindly.
First identify slow startup stage.
Then reduce unnecessary starters or heavy beans.
```

Useful approaches:

```text
Use actuator startup endpoint.
Use application startup metrics.
Avoid unused starters.
Avoid heavy work in bean creation.
Use lazy initialization only deliberately.
Exclude specific auto-config only when proven unnecessary.
```

---

## Scalability Considerations

Auto-configuration affects production scalability because it decides infrastructure defaults.

Examples:

```text
Connection pool default size
Jackson configuration
Tomcat thread defaults
Multipart upload limits
JPA open-in-view setting
Actuator endpoints
Security filters
Cache manager
Task executor
```

Defaults are good starting points.
They are not always production-final.

Senior approach:

```text
Use auto-configuration to bootstrap.
Then explicitly tune production-critical defaults.
```

Examples:

```properties
server.tomcat.threads.max=200
spring.datasource.hikari.maximum-pool-size=30
spring.jpa.open-in-view=false
spring.servlet.multipart.max-file-size=10MB
management.endpoint.health.probes.enabled=true
```

Auto-configuration gives the machinery.
Production tuning sets its limits.

---

## Failure Investigation Playbook

## Step 1 — Identify the unexpected behavior

Examples:

```text
Security enabled
DataSource created unexpectedly
Custom ObjectMapper ignored
Repository not created
Tomcat started when not expected
App did not start as web app
```

## Step 2 — Check dependencies

Ask:

```text
Which starter was added?
Which transitive dependency introduced the class?
```

Use:

```bash
mvn dependency:tree
```

or:

```bash
gradle dependencies
```

## Step 3 — Run condition report

```bash
java -jar app.jar --debug
```

Look for the relevant auto-configuration:

```text
SecurityAutoConfiguration
DataSourceAutoConfiguration
WebMvcAutoConfiguration
JacksonAutoConfiguration
HibernateJpaAutoConfiguration
```

## Step 4 — Read why it matched or failed

Condition report tells:

```text
matched because class found
failed because bean missing
failed because property not enabled
backed off because custom bean exists
```

## Step 5 — Choose the smallest fix

Order of preference:

```text
1. Correct property
2. Add/remove starter
3. Provide custom bean
4. Add customizer
5. Exclude auto-configuration
```

Example exclude:

```java
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class CatalogApplication {
}
```

Use exclusion only when you truly do not want that auto-config.

---

## Interview Q&A

### Q1. What is Spring Boot auto-configuration?

Strong answer:

> Auto-configuration is Spring Boot’s mechanism for conditionally registering default bean definitions based on classpath, properties, application type, and existing beans. It reduces manual configuration while allowing customization because many defaults back off when the user provides their own bean.

### Q2. How does `@SpringBootApplication` enable auto-configuration?

Strong answer:

> `@SpringBootApplication` includes `@EnableAutoConfiguration`. That imports auto-configuration candidates through `AutoConfigurationImportSelector`. Spring Boot then evaluates conditions on those candidates and imports the ones that match.

### Q3. What is `@ConditionalOnMissingBean`?

Strong answer:

> It means the auto-configured bean should only be created if the application context does not already contain a bean of that type or name. This lets user-defined beans override Boot defaults.

### Q4. What happens when you add a starter?

Strong answer:

> A starter adds dependencies to the classpath. Those classes can satisfy `@ConditionalOnClass` checks, causing related auto-configurations to activate and register default infrastructure beans.

### Q5. How do you debug auto-configuration?

Strong answer:

> I run the app with `--debug` or inspect `/actuator/conditions` if enabled. Then I check positive and negative matches to see which auto-configuration matched, failed, or backed off and why.

### Q6. How do you customize auto-configuration?

Strong answer:

> Prefer properties, custom beans, or customizer beans. Boot defaults often use `@ConditionalOnMissingBean`, so defining a bean of the right type makes Boot back off. Excluding auto-configuration is a last resort.

### Q7. Why did security turn on after adding a dependency?

Strong answer:

> Adding `spring-boot-starter-security` puts Spring Security classes on the classpath. That satisfies security auto-configuration conditions, so Boot registers a default security filter chain unless the application provides its own security configuration.

---

## Production Checklist

```text
Dependency Changes
[ ] Did we add any new starter?
[ ] Did transitive dependencies activate new auto-config?
[ ] Did runtime behavior change after classpath change?

Condition Debugging
[ ] Have we checked condition evaluation report?
[ ] Do we know why key auto-config matched?
[ ] Do we know why expected auto-config did not match?

Customization
[ ] Can a property solve this?
[ ] Should we provide a custom bean?
[ ] Should we provide a customizer?
[ ] Is exclusion truly necessary?

Production Defaults
[ ] Are connection pool defaults tuned?
[ ] Are web server thread defaults tuned?
[ ] Is security explicit?
[ ] Are actuator endpoints controlled?
[ ] Is open-in-view intentionally configured?
[ ] Are JSON settings intentional?

Release Safety
[ ] Review starter changes in PRs.
[ ] Check startup logs after dependency changes.
[ ] Verify health/readiness after auto-config changes.
```

---

## One-Page Cheat Sheet

```text
Auto-Configuration Internals
============================

Core Idea
---------
Conditional default bean registration.

Inputs
------
Classpath
Properties
Existing beans
Application type
Profiles/resources

Engine
------
@EnableAutoConfiguration
  -> AutoConfigurationImportSelector
  -> load candidate auto-config classes
  -> evaluate conditions
  -> register matching bean definitions

Common Conditions
-----------------
@ConditionalOnClass
@ConditionalOnMissingClass
@ConditionalOnBean
@ConditionalOnMissingBean
@ConditionalOnProperty
@ConditionalOnWebApplication
@ConditionalOnResource

Starter
-------
Adds dependencies to classpath.
Classpath can activate auto-config.

Back Off
--------
Boot default bean is skipped when user bean exists.

Debug
-----
java -jar app.jar --debug
/actuator/conditions

Best Sentence
-------------
Auto-configuration asks:
Given classpath, properties, and existing beans,
which default beans should Spring Boot register?
```

---

## Last-Minute Interview Revision

Do not say:

```text
Spring Boot magically configures everything.
```

Say:

```text
Spring Boot auto-configuration imports candidate configuration classes and evaluates conditions based on classpath, properties, application type, and existing beans. Matching configurations register default bean definitions, often backing off when the user defines their own bean.
```

Senior version:

```text
I treat every starter as a classpath signal that may activate production behavior. When something unexpected happens, I inspect the condition evaluation report to see which auto-configuration matched, failed, or backed off, then customize with properties or beans before considering exclusion.
```

---

## One Picture To Remember

```text
              SPRING BOOT AUTO-CONFIGURATION

        Dependencies / Properties / User Beans
                        |
                        v
        +------------------------------------+
        | AutoConfigurationImportSelector    |
        +------------------------------------+
                        |
                        v
        +------------------------------------+
        | Candidate Auto-Configuration       |
        | Classes                            |
        +------------------------------------+
                        |
                        v
        +------------------------------------+
        | Condition Checks                   |
        | - class present?                   |
        | - property enabled?                |
        | - web app?                         |
        | - bean missing?                    |
        +------------------------------------+
                        |
             +----------+----------+
             |                     |
             v                     v
       Match conditions        Fail conditions
             |                     |
             v                     v
   Register default beans        Skip
             |
             v
   ApplicationContext creates beans
```

Final retention sentence:

> **Auto-configuration is Spring Boot’s conditional assembly engine: it reads classpath, properties, and existing beans, then registers only the default infrastructure your app appears to need.**
