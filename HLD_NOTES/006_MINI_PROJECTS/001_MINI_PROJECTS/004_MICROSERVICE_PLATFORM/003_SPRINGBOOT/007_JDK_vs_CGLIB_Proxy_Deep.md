
# 007_JDK_vs_CGLIB_Proxy.md

# MiniSpringBoot Deep Production Mode
# JDK Proxy vs CGLIB Proxy

## One Core Mental Model

> Spring cannot inject transactions, security, caching, retries, async execution, or AOP behavior into your class directly.
>
> Spring creates a PROXY object that stands between the caller and the real object.

Everything in this chapter comes from that single idea.

---

# Why This Chapter Exists

Many developers memorize:

- @Transactional
- @Cacheable
- @Async
- @Secured
- AOP

But fail to answer:

Why does Spring need a proxy?

Why does self invocation fail?

Why do final methods break transactions?

Why do some beans use JDK Proxy while others use CGLIB?

The answer is the proxy model.

---

# Problem Before Proxies Exist

Suppose:

```java
@Service
public class PaymentService {

    public void pay() {
        System.out.println("processing payment");
    }
}
```

Business requirement:

```text
Before pay():
    Start Transaction

After pay():
    Commit Transaction

If Exception:
    Rollback
```

Spring cannot rewrite your source code.

So how can it add behavior?

Answer:

Create another object.

---

# Core Mental Model

Without Proxy:

Client -> Real Object

With Proxy:

Client -> Proxy -> Real Object

The proxy becomes a programmable gateway.

```text
Client
   |
   v
Proxy
   |
   +--> Start Tx
   +--> Security Check
   +--> Cache Lookup
   +--> Metrics
   |
   v
Real Object
```

---

# Real World Analogy

Airport Security

Without security:

Passenger -> Aircraft

With security:

Passenger
    |
Security Gate
    |
Aircraft

The gate adds behavior.

Spring Proxy = Security Gate

---

# Rich ASCII Diagram

```text
                 CLIENT

                    |
                    v

        +-------------------------+
        |         PROXY           |
        +-------------------------+

          |      |       |

          v      v       v

      Security  Tx   Cache

          |      |       |

          +------+-------+

                 |
                 v

        +-------------------------+
        |      REAL OBJECT        |
        +-------------------------+
```

---

# Two Proxy Technologies

Spring has two ways.

1. JDK Dynamic Proxy
2. CGLIB Proxy

Both solve the same problem.

Only implementation differs.

---

# JDK Proxy Mental Model

JDK Proxy works using interfaces.

```java
public interface PaymentService {
    void pay();
}
```

```java
@Service
public class PaymentServiceImpl
        implements PaymentService {

    public void pay() {
    }
}
```

Generated:

```text
GeneratedProxy
      |
implements
      |
PaymentService
      |
      v
PaymentServiceImpl
```

---

# JDK Proxy Internal Working

Spring asks JVM:

```text
Generate class at runtime
```

Generated structure:

```java
class Proxy
implements PaymentService {

   public void pay() {

      beginTransaction();

      target.pay();

      commitTransaction();
   }
}
```

You never see this class.

But JVM creates it.

---

# JDK Proxy Dry Run

Request:

```java
paymentService.pay();
```

Flow:

```text
Client
  |
  v
JDK Proxy
  |
Begin Tx
  |
target.pay()
  |
Commit
```

---

# Why JDK Proxy Exists

Advantages:

```text
No bytecode inheritance
Simple
Stable
Uses JVM API
```

Limitation:

```text
Needs Interface
```

---

# CGLIB Mental Model

Suppose:

```java
@Service
public class PaymentService {

    public void pay() {
    }
}
```

No interface.

JDK Proxy cannot help.

Spring generates:

```text
PaymentServiceProxy
      extends
PaymentService
```

---

# CGLIB Internal Model

Original:

```java
public class PaymentService {

   public void pay() {
   }
}
```

Generated:

```java
public class PaymentServiceProxy
extends PaymentService {

   @Override
   public void pay() {

      beginTransaction();

      super.pay();

      commitTransaction();
   }
}
```

---

# CGLIB Dry Run

```java
paymentService.pay();
```

Flow:

```text
Client
   |
   v
Generated Subclass
   |
Begin Tx
   |
super.pay()
   |
Commit
```

---

# Side By Side Comparison

```text
JDK PROXY

Interface
    |
    v
Generated Proxy
    |
    v
Implementation
```

```text
CGLIB

Generated Child Class
          |
          v
Original Class
```

---

# How Spring Chooses

Rule:

```text
Interface Exists
      |
      v
Prefer JDK Proxy
```

Otherwise:

```text
No Interface
      |
      v
CGLIB
```

Modern Spring Boot frequently uses CGLIB.

---

# Production Scale Example

Imagine:

```text
OrderService
PaymentService
InventoryService
FraudService
NotificationService
```

Every service has:

```text
Transactions
Security
Metrics
Tracing
Caching
```

Spring cannot inject this logic manually into 500 services.

Instead:

```text
500 Services
      |
      v
500 Proxies
```

Each proxy adds behavior automatically.

---

# Transaction Internal Flow

@Transactional

```text
Client
   |
   v
Proxy
   |
Begin Transaction
   |
Target Method
   |
Commit
```

Exception:

```text
Client
   |
Proxy
   |
Begin Tx
   |
Target Method
   |
Exception
   |
Rollback
```

Transaction logic lives in proxy.

Not target object.

---

# Security Internal Flow

```text
Client
   |
Proxy
   |
Check User
   |
Authorized?
   |
Target Method
```

Again:

Security lives in proxy.

---

# Caching Internal Flow

```text
Client
   |
Proxy
   |
Cache Lookup
   |
Hit?
 |     |
Yes   No
 |     |
Return Execute
       |
   Save Cache
```

Target object knows nothing about cache.

---

# Production Failure Story 1

Developer:

```java
@Service
public final class PaymentService {
}
```

Spring uses CGLIB.

Problem:

```text
final class
cannot be extended
```

Generated subclass impossible.

Proxy creation fails.

---

# Production Failure Story 2

```java
public final void pay() {
}
```

CGLIB requires:

```text
Override Method
```

Cannot override final method.

Result:

```text
@Transactional ignored
```

---

# Production Failure Story 3

Self Invocation

```java
public void process() {
   pay();
}

@Transactional
public void pay() {
}
```

Flow:

```text
process()
   |
this.pay()
```

Proxy bypassed.

No transaction.

---

# Deep Debugging Mindset

Whenever:

```text
@Transactional fails

@Cacheable fails

@Async fails

@Retryable fails
```

Ask:

Question 1

```text
Did call go through proxy?
```

Question 2

```text
JDK or CGLIB?
```

Question 3

```text
Any final class?
```

Question 4

```text
Any final method?
```

Question 5

```text
Any self invocation?
```

This solves most production AOP bugs.

---

# Java Example - JDK Proxy

```java
public interface UserService {

    void save();
}
```

```java
@Service
public class UserServiceImpl
implements UserService {

    public void save() {
        System.out.println("save");
    }
}
```

Runtime:

```text
UserService Proxy
       |
       v
UserServiceImpl
```

---

# Java Example - CGLIB

```java
@Service
public class UserService {

    public void save() {
    }
}
```

Runtime:

```text
UserServiceProxy
      extends
UserService
```

---

# Internal Execution Walkthrough

Request enters.

```text
Controller
    |
    v
Proxy
```

Proxy chain:

```text
Transaction Interceptor
        |
Security Interceptor
        |
Metrics Interceptor
        |
Cache Interceptor
        |
Target Method
```

Return path:

```text
Target
   |
Cache
   |
Metrics
   |
Security
   |
Transaction
   |
Client
```

---

# Common Misconceptions

Wrong:

```text
@Transactional code
exists inside method
```

Correct:

```text
@Transactional logic
exists inside proxy
```

Wrong:

```text
Proxy is optional
```

Correct:

```text
Proxy is required
for Spring AOP features
```

Wrong:

```text
Self invocation should work
```

Correct:

```text
Self invocation bypasses proxy
```


---

# Redis Code Snippet Example - Why Proxy Matters For @Cacheable

Redis caching in Spring is also proxy-based.

Many developers think:

```text
@Cacheable talks to Redis directly from my method
```

Wrong mental model.

Correct mental model:

```text
Client
   |
   v
Spring Proxy
   |
   +--> Check Redis Cache
   |
   +--> If Hit: return cached value
   |
   +--> If Miss: call real method
   |
   +--> Store result in Redis
   |
   v
Real Service Method
```

The Redis logic is not inside your method.

The Redis cache logic is executed by the proxy before and after your method call.

---

# Redis Cache Flow Diagram

```text
                 CLIENT REQUEST

                       |
                       v

              +------------------+
              |  SPRING PROXY    |
              +------------------+

                       |
                       v

              Check Redis Cache

                 /             \

                /               \

             HIT                 MISS

              |                    |

              v                    v

        Return Value         Call Real Method

                                   |
                                   v

                              Save To Redis

                                   |
                                   v

                              Return Value
```

This is the same proxy mental model as transactions.

```text
@Transactional -> Transaction Proxy
@Cacheable     -> Cache Proxy
@Async         -> Async Proxy
@Retryable     -> Retry Proxy
```

---

# Redis Setup Example

## Maven Dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

---

## application.yml

```yaml
spring:
  cache:
    type: redis

  data:
    redis:
      host: localhost
      port: 6379
```

---

## Enable Caching

```java
@SpringBootApplication
@EnableCaching
public class MiniSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                MiniSpringBootApplication.class,
                args
        );
    }
}
```

Important:

```text
@EnableCaching
     |
     v
Spring creates cache infrastructure
     |
     v
Spring creates proxy around @Cacheable beans
```

Without `@EnableCaching`, the cache proxy behavior will not be activated.

---

# Working Redis Cache Example - Proxy Hit

```java
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Cacheable(value = "products", key = "#id")
    public ProductDto getProduct(Long id) {

        System.out.println("DB HIT for product id = " + id);

        Product product = productRepository.findById(id)
                .orElseThrow();

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getPrice()
        );
    }
}
```

Controller:

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ProductDto getProduct(@PathVariable Long id) {

        return productService.getProduct(id);
    }
}
```

Flow:

```text
HTTP Request
     |
     v
ProductController
     |
     v
ProductService Proxy
     |
     v
Check Redis key: products::1
     |
     +--> Miss first time
     |
     v
Call real ProductService.getProduct(1)
     |
     v
DB Query
     |
     v
Store result in Redis
     |
     v
Return ProductDto
```

Second request:

```text
HTTP Request
     |
     v
ProductController
     |
     v
ProductService Proxy
     |
     v
Check Redis key: products::1
     |
     +--> Hit
     |
     v
Return cached ProductDto

Real method is NOT called.
DB is NOT hit.
```

If you see this log only once:

```text
DB HIT for product id = 1
```

then Redis caching is working.

---

# Internal Execution Walkthrough - @Cacheable

When this method is called through the proxy:

```java
productService.getProduct(1L);
```

Spring mentally executes something like:

```java
Object cacheKey = "products::1";

Object cachedValue = redis.get(cacheKey);

if (cachedValue != null) {
    return cachedValue;
}

Object result = target.getProduct(1L);

redis.set(cacheKey, result);

return result;
```

But this code is not inside your method.

It lives in Spring's caching interceptor attached to the proxy.

---

# Failing Redis Cache Example - Self Invocation

```java
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductDto getProductForOrder(Long id) {

        // Internal call
        // This is this.getProduct(id)
        return getProduct(id);
    }

    @Cacheable(value = "products", key = "#id")
    public ProductDto getProduct(Long id) {

        System.out.println("DB HIT for product id = " + id);

        Product product = productRepository.findById(id)
                .orElseThrow();

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getPrice()
        );
    }
}
```

Controller:

```java
@GetMapping("/order-view/{id}")
public ProductDto getProductForOrder(@PathVariable Long id) {

    return productService.getProductForOrder(id);
}
```

Expected by junior developer:

```text
getProductForOrder()
      |
      v
@Cacheable getProduct()
      |
      v
Redis cache used
```

Actual flow:

```text
Controller
    |
    v
ProductService Proxy
    |
    v
getProductForOrder()
    |
    v
this.getProduct(id)
    |
    v
Real Method Directly
```

Proxy is bypassed.

Redis cache interceptor does not run.

Result:

```text
Every call hits DB.
Redis is ignored.
```

This is the exact same reason `@Transactional` fails during self-invocation.

```text
No Proxy
   =
No Transaction

No Proxy
   =
No Redis Cache
```

---

# Fix 1 - Move Cache Method To Another Bean

This is the clean production solution.

```java
@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private final ProductRepository productRepository;

    @Cacheable(value = "products", key = "#id")
    public ProductDto getProduct(Long id) {

        System.out.println("DB HIT for product id = " + id);

        Product product = productRepository.findById(id)
                .orElseThrow();

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getPrice()
        );
    }
}
```

```java
@Service
@RequiredArgsConstructor
public class ProductOrderService {

    private final ProductCacheService productCacheService;

    public ProductDto getProductForOrder(Long id) {

        return productCacheService.getProduct(id);
    }
}
```

Now flow:

```text
ProductOrderService
        |
        v
ProductCacheService Proxy
        |
        v
Redis Cache Interceptor
        |
        v
Real ProductCacheService.getProduct()
```

Proxy is hit.

Redis works.

---

# Fix 2 - Inject Self Proxy

This works, but is less clean.

```java
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductService self;

    public ProductDto getProductForOrder(Long id) {

        return self.getProduct(id);
    }

    @Cacheable(value = "products", key = "#id")
    public ProductDto getProduct(Long id) {

        System.out.println("DB HIT for product id = " + id);

        Product product = productRepository.findById(id)
                .orElseThrow();

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getPrice()
        );
    }
}
```

Mental model:

```text
this.getProduct()
      |
      v
Bypasses proxy


self.getProduct()
      |
      v
Hits proxy
```

Usually prefer separate bean over self-injection.

---

# Redis + Proxy Production Failure Story

A production team added Redis caching to reduce database pressure.

```java
@Cacheable(value = "products", key = "#id")
public ProductDto getProduct(Long id) {
    return repository.findById(id);
}
```

They tested `/products/{id}` and cache worked.

Then a high-traffic checkout flow used:

```java
public OrderSummary createOrder(Long productId) {
    ProductDto product = getProduct(productId);
    ...
}
```

During sale traffic:

```text
DB CPU 95%
Redis QPS low
Cache hit ratio near 0%
Checkout latency high
```

Why?

The checkout flow called the cached method internally.

```text
createOrder()
    |
    v
this.getProduct()
```

Proxy was bypassed.

Redis was not used.

The fix:

```text
Move cached method to ProductCacheService
```

After fix:

```text
Controller / OrderService
        |
        v
ProductCacheService Proxy
        |
        v
Redis Cache
```

Result:

```text
DB load dropped
Redis hit ratio increased
p99 latency improved
```

Senior debugging lesson:

When cache does not work, do not start with Redis.

First ask:

```text
Did the method call pass through Spring proxy?
```

---

# Redis Debugging Checklist

When `@Cacheable` does not work:

```text
1. Is @EnableCaching present?

2. Is Redis configured?

3. Is the method public?

4. Is the method called from another Spring bean?

5. Is it self-invocation?

6. Is the bean proxied?

7. Is the key correct?

8. Is the result serializable?

9. Are cache names consistent?

10. Is TTL configured as expected?
```

Proxy-related debugging:

```java
System.out.println(productService.getClass());
```

Possible output:

```text
class com.example.ProductService$$SpringCGLIB$$0
```

or:

```text
class jdk.proxy2.$Proxy89
```

If you see the real class only, proxy may not be active.

---

# Redis Example - Cache Eviction

Updating data must evict stale cache.

```java
@Service
@RequiredArgsConstructor
public class ProductWriteService {

    private final ProductRepository productRepository;

    @CacheEvict(value = "products", key = "#id")
    public void updatePrice(Long id, BigDecimal newPrice) {

        Product product = productRepository.findById(id)
                .orElseThrow();

        product.setPrice(newPrice);

        productRepository.save(product);
    }
}
```

Flow:

```text
Client
   |
   v
ProductWriteService Proxy
   |
   v
Evict Redis key products::1
   |
   v
Call updatePrice()
   |
   v
DB updated
```

If `updatePrice()` is called internally through `this.updatePrice()`, eviction can also be skipped.

Same rule:

```text
No Proxy
   =
No Cache Eviction
```

---

# Redis Example - Cache Put

```java
@CachePut(value = "products", key = "#result.id")
public ProductDto refreshProduct(Long id) {

    Product product = productRepository.findById(id)
            .orElseThrow();

    return new ProductDto(
            product.getId(),
            product.getName(),
            product.getPrice()
    );
}
```

Mental model:

```text
Always execute method
        |
        v
Put returned value into Redis
```

Difference:

```text
@Cacheable
   =
Maybe skip method if cache hit

@CachePut
   =
Always run method and update cache

@CacheEvict
   =
Remove value from cache
```

All three depend on proxy interception.

---

# Final Redis Memory Hook

```text
Redis caching in Spring is not magic inside Redis.

Redis caching in Spring is proxy interception.

@Cacheable
@CachePut
@CacheEvict

all require:

Client
   |
   v
Spring Proxy
   |
   v
Real Method
```

If the call does not pass through the proxy, Redis annotations do not execute.


---

# Interview Questions

Q. Why does Spring use proxies?

A:

To add behavior without modifying application code.

Q. When does Spring use JDK Proxy?

A:

When an interface is available.

Q. When does Spring use CGLIB?

A:

When class-based proxying is needed.

Q. Why do final methods break transactions?

A:

CGLIB cannot override final methods.

Q. Why does self invocation fail?

A:

Proxy is bypassed.

Q. Where does transaction logic live?

A:

Inside transaction interceptor attached to proxy.

---

# Cheat Sheet

```text
JDK Proxy

Requires Interface

Implements Interface

Cannot proxy concrete class directly
```

```text
CGLIB

No Interface Needed

Creates Child Class

Cannot extend Final Class

Cannot override Final Method
```

```text
Proxy Hit
      =
AOP Works

Proxy Bypass
      =
AOP Fails
```

---

# One Picture To Remember

```text
                 SPRING MAGIC

                       |

                       v

                    PROXY

              /                \

             /                  \

            v                    v

      JDK Proxy            CGLIB Proxy

     Interface              Subclass

            \               /

             \             /

              \           /

                Real Object



No Proxy
   =
No Transaction

No Proxy
   =
No Security

No Proxy
   =
No Cache

Proxy
   =
Spring AOP
```

# Final Memory Hook

Remember only:

Spring never puts transaction logic inside your class.

Spring puts transaction logic inside a proxy.

JDK Proxy = Interface based.

CGLIB = Subclass based.

Everything else is detail.
