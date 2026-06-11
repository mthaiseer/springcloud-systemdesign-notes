# 014_Client_Side_vs_Server_Side_LoadBalancing.md

# Client-Side vs Server-Side Load Balancing

## MiniServiceDiscovery Context

In a microservice system, **service discovery** answers one core question:

> “For service name `payment-service`, which live instance should I call now?”

But discovery alone is not enough.

If the registry returns 5 healthy instances:

```text
payment-service
 ├── 10.0.1.11:8080
 ├── 10.0.1.12:8080
 ├── 10.0.1.13:8080
 ├── 10.0.1.14:8080
 └── 10.0.1.15:8080
```

The caller still needs to decide:

```text
Which instance should receive this request?
```

That decision is **load balancing**.

This note explains two major models:

1. **Client-side load balancing**
2. **Server-side load balancing**

Both are heavily used in real production systems.

---

# 1. Core Mental Model

## Service Discovery vs Load Balancing

### Service Discovery

Service discovery tells us:

```text
Where are the instances?
```

Example:

```text
GET /registry/payment-service

Response:
[
  "10.0.1.11:8080",
  "10.0.1.12:8080",
  "10.0.1.13:8080"
]
```

### Load Balancing

Load balancing tells us:

```text
Which instance should I call for this request?
```

Example:

```text
Request 1 -> 10.0.1.11
Request 2 -> 10.0.1.12
Request 3 -> 10.0.1.13
Request 4 -> 10.0.1.11
```

---

# 2. Why Load Balancing Is Needed

Without load balancing:

```text
order-service -> payment-service instance A only
```

Problems:

```text
1. One instance overloaded
2. Other instances idle
3. Higher latency
4. More failures
5. Poor resource usage
6. No fault tolerance
```

With load balancing:

```text
order-service
   ├── payment-service A
   ├── payment-service B
   └── payment-service C
```

Requests are spread across healthy instances.

---

# 3. Two Load Balancing Models

## Model 1: Client-Side Load Balancing

The **caller/client** chooses the target service instance.

```text
order-service
   |
   | 1. Ask registry: where is payment-service?
   v
service-registry
   |
   | 2. Return instances
   v
order-service
   |
   | 3. Choose instance locally
   v
payment-service instance B
```

The load balancing logic lives inside the client application.

Common examples:

```text
Netflix Ribbon      legacy
Spring Cloud LoadBalancer
Eureka client
OpenFeign + LoadBalancer
gRPC client-side LB
```

---

## Model 2: Server-Side Load Balancing

The **client sends request to a load balancer**, and the load balancer chooses the target instance.

```text
order-service
   |
   | Call payment-service virtual address
   v
load-balancer
   |
   | Choose backend instance
   v
payment-service instance B
```

The load balancing logic lives outside the client.

Common examples:

```text
AWS ALB / NLB
NGINX
HAProxy
Kubernetes Service kube-proxy
Envoy proxy
API Gateway
Cloud Load Balancer
```

---

# 4. Client-Side Load Balancing Flow

## Step-by-Step Flow

```text
1. order-service wants to call payment-service
2. order-service asks registry for payment-service instances
3. registry returns healthy instances
4. order-service caches the instance list locally
5. local load balancer chooses one instance
6. order-service sends request directly to chosen instance
7. failed instances are avoided after health/status update
```

---

## Diagram

```text
                     +------------------+
                     | Service Registry |
                     | Eureka / Consul  |
                     +---------+--------+
                               ^
                               |
                 fetch instances of payment-service
                               |
+---------------+              |
| order-service |--------------+
|               |
| Local LB      |
| Round Robin   |
+-------+-------+
        |
        | direct call
        v
+-------------------+
| payment-service-2 |
+-------------------+
```

---

# 5. Server-Side Load Balancing Flow

## Step-by-Step Flow

```text
1. order-service calls payment-service DNS or virtual endpoint
2. request reaches external/internal load balancer
3. load balancer has backend pool of payment-service instances
4. load balancer checks health of backends
5. load balancer chooses healthy backend
6. request is forwarded to selected instance
7. response flows back through load balancer
```

---

## Diagram

```text
+---------------+
| order-service |
+-------+-------+
        |
        | call payment-service.internal
        v
+-------------------+
| Load Balancer     |
| NGINX / ALB / K8s |
+----+---------+----+
     |         |
     v         v
+---------+ +---------+
| pay-1   | | pay-2   |
+---------+ +---------+
```

---

# 6. Key Difference

The main difference is:

```text
Who chooses the target instance?
```

| Model | Who chooses instance? | Client knows backend IPs? | Common usage |
|---|---|---|---|
| Client-side LB | Caller application | Yes | Eureka, Feign, gRPC |
| Server-side LB | Load balancer/proxy | No | NGINX, ALB, Kubernetes Service |

---

# 7. Client-Side Load Balancing Example

Imagine this registry:

```text
payment-service:
  - 10.0.1.11:8080
  - 10.0.1.12:8080
  - 10.0.1.13:8080
```

`order-service` receives this list and keeps it locally:

```java
List<ServiceInstance> instances = registry.lookup("payment-service");
```

Then local round robin chooses:

```text
Request 1 -> 10.0.1.11
Request 2 -> 10.0.1.12
Request 3 -> 10.0.1.13
Request 4 -> 10.0.1.11
```

---

# 8. Simple Java Client-Side Load Balancer

## Service Instance Model

```java
public class ServiceInstance {
    private final String serviceName;
    private final String host;
    private final int port;
    private final boolean healthy;

    public ServiceInstance(String serviceName, String host, int port, boolean healthy) {
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
        this.healthy = healthy;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public String url() {
        return "http://" + host + ":" + port;
    }
}
```

---

## Registry Interface

```java
import java.util.List;

public interface ServiceRegistry {
    List<ServiceInstance> lookup(String serviceName);
}
```

---

## In-Memory Registry

```java
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryServiceRegistry implements ServiceRegistry {

    private final Map<String, List<ServiceInstance>> registry = new ConcurrentHashMap<>();

    public void register(ServiceInstance instance) {
        registry.compute(instance.getServiceName(), (serviceName, oldList) -> {
            List<ServiceInstance> newList = new ArrayList<>();
            if (oldList != null) {
                newList.addAll(oldList);
            }
            newList.add(instance);
            return newList;
        });
    }

    @Override
    public List<ServiceInstance> lookup(String serviceName) {
        return registry.getOrDefault(serviceName, List.of());
    }
}
```

---

## Round Robin Client-Side Load Balancer

```java
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientSideRoundRobinLoadBalancer {

    private final ServiceRegistry registry;
    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public ClientSideRoundRobinLoadBalancer(ServiceRegistry registry) {
        this.registry = registry;
    }

    public ServiceInstance choose(String serviceName) {
        List<ServiceInstance> healthyInstances = registry.lookup(serviceName)
                .stream()
                .filter(ServiceInstance::isHealthy)
                .toList();

        if (healthyInstances.isEmpty()) {
            throw new IllegalStateException("No healthy instances for service: " + serviceName);
        }

        AtomicInteger counter = counters.computeIfAbsent(serviceName, key -> new AtomicInteger(0));

        int index = Math.abs(counter.getAndIncrement() % healthyInstances.size());
        return healthyInstances.get(index);
    }
}
```

---

## Dry Run

Instances:

```text
payment-service:
0 -> 10.0.1.11:8080
1 -> 10.0.1.12:8080
2 -> 10.0.1.13:8080
```

Counter starts at `0`.

```text
Request 1:
counter = 0
index = 0 % 3 = 0
chosen = 10.0.1.11
counter becomes 1

Request 2:
counter = 1
index = 1 % 3 = 1
chosen = 10.0.1.12
counter becomes 2

Request 3:
counter = 2
index = 2 % 3 = 2
chosen = 10.0.1.13
counter becomes 3

Request 4:
counter = 3
index = 3 % 3 = 0
chosen = 10.0.1.11
```

---

# 9. Server-Side Load Balancing Example

Client calls only one stable address:

```text
http://payment-service.internal/pay
```

The client does **not** know actual backend IPs.

The load balancer knows:

```text
Backend pool:
  - 10.0.1.11:8080
  - 10.0.1.12:8080
  - 10.0.1.13:8080
```

The LB forwards requests:

```text
Request 1 -> LB -> 10.0.1.11
Request 2 -> LB -> 10.0.1.12
Request 3 -> LB -> 10.0.1.13
```

---

# 10. NGINX Server-Side Load Balancing Example

```nginx
upstream payment_service {
    server 10.0.1.11:8080;
    server 10.0.1.12:8080;
    server 10.0.1.13:8080;
}

server {
    listen 80;

    location /payment/ {
        proxy_pass http://payment_service/;
    }
}
```

Client calls:

```text
http://payment.company.internal/payment/charge
```

NGINX forwards to one backend.

---

# 11. Kubernetes Server-Side Load Balancing

In Kubernetes, a `Service` gives a stable virtual IP/DNS name.

Pods are dynamic:

```text
payment-service pod A -> 10.244.1.10
payment-service pod B -> 10.244.2.14
payment-service pod C -> 10.244.3.19
```

But client calls:

```text
http://payment-service.default.svc.cluster.local
```

Kubernetes routes traffic to matching pods.

---

## Kubernetes Service Example

```yaml
apiVersion: v1
kind: Service
metadata:
  name: payment-service
spec:
  selector:
    app: payment-service
  ports:
    - port: 80
      targetPort: 8080
```

Pods selected by:

```yaml
labels:
  app: payment-service
```

Client does not need to know pod IPs.

---

# 12. Spring Cloud Client-Side Load Balancing

In Spring Cloud, service calls often look like this:

```java
@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/payments")
    PaymentResponse createPayment(@RequestBody PaymentRequest request);
}
```

Notice:

```java
name = "payment-service"
```

There is no hardcoded IP.

Internally:

```text
Feign client
   -> asks LoadBalancerClient
   -> gets instances from DiscoveryClient
   -> picks one instance
   -> sends HTTP request
```

---

## Mental Flow

```text
OrderController
   -> PaymentClient.createPayment()
   -> Feign builds request for payment-service
   -> Spring Cloud LoadBalancer resolves payment-service
   -> DiscoveryClient returns instances from Eureka
   -> LoadBalancer chooses instance
   -> HTTP call goes to selected instance
```

---

# 13. Spring RestTemplate Example

```java
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

Usage:

```java
@Service
public class OrderService {

    private final RestTemplate restTemplate;

    public OrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PaymentResponse callPayment(PaymentRequest request) {
        return restTemplate.postForObject(
                "http://payment-service/payments",
                request,
                PaymentResponse.class
        );
    }
}
```

Here:

```text
http://payment-service/payments
```

is not DNS in the normal sense. Spring Cloud intercepts it and resolves `payment-service` using service discovery.

---

# 14. Comparison Table

| Area | Client-Side LB | Server-Side LB |
|---|---|---|
| Instance selection | Done by client app | Done by LB/proxy |
| Client knows instances | Yes | No |
| Registry dependency | Client talks to registry | LB/proxy may talk to registry |
| Latency | One less network hop | Extra hop through LB |
| Simplicity for clients | More complex | Simpler |
| Central control | Harder | Easier |
| Failure isolation | Each client handles logic | LB handles logic centrally |
| Language dependency | Needs client library per language | Language agnostic |
| Common examples | Eureka + Feign | NGINX, ALB, K8s Service |

---

# 15. Advantages of Client-Side Load Balancing

## 1. Fewer Network Hops

Client directly calls the target instance.

```text
client -> service instance
```

Instead of:

```text
client -> load balancer -> service instance
```

This can reduce latency.

---

## 2. Smart Per-Client Decisions

The client can make decisions based on:

```text
1. Local latency history
2. Previous failures
3. Retry budget
4. Zone preference
5. Request type
6. Instance metadata
```

Example:

```text
Client in zone-a prefers service instances in zone-a.
```

---

## 3. Registry Awareness

The client has direct visibility into registry metadata:

```text
version=v2
zone=eu-west-1a
weight=5
status=UP
canary=true
```

This allows advanced routing.

---

## 4. Useful for Internal Microservices

Client-side LB works well when:

```text
1. Services are internal
2. All services use same platform/framework
3. Registry integration is mature
4. Teams accept client library dependency
```

---

# 16. Disadvantages of Client-Side Load Balancing

## 1. Client Complexity

Every client needs load balancing logic.

```text
Java client needs Java library
Go client needs Go library
Node client needs Node library
Python client needs Python library
```

This becomes painful in polyglot systems.

---

## 2. Harder to Change Globally

If load balancing policy changes from:

```text
round robin -> least latency
```

You may need to update many applications.

---

## 3. Stale Registry Cache

Clients usually cache registry data.

Problem:

```text
Registry knows instance is dead,
but client cache still contains dead instance.
```

Result:

```text
client may call dead instance until cache refresh
```

---

## 4. Inconsistent Decisions

Each client has its own view of the world.

```text
Client A sees 3 instances
Client B sees 4 instances
Client C still sees dead instance
```

This is normal in eventually consistent service discovery.

---

# 17. Advantages of Server-Side Load Balancing

## 1. Simple Clients

Client only knows one stable endpoint:

```text
http://payment-service.internal
```

No service registry logic is needed inside app code.

---

## 2. Language Agnostic

Any client can call the load balancer:

```text
Java
Go
Python
Node.js
curl
mobile app
legacy system
```

No special service discovery SDK required.

---

## 3. Centralized Control

Policies can be changed in one place:

```text
round robin
least connections
weighted routing
blue-green deployment
canary traffic
rate limiting
TLS termination
WAF rules
```

---

## 4. Strong Operational Visibility

Load balancers usually expose:

```text
request count
latency
5xx errors
backend health
active connections
TLS metrics
upstream response time
```

This helps production debugging.

---

# 18. Disadvantages of Server-Side Load Balancing

## 1. Extra Network Hop

Request path:

```text
client -> LB -> service instance
```

This adds some latency.

---

## 2. Load Balancer Can Become Bottleneck

If not scaled properly:

```text
all traffic goes through LB
```

The LB must be highly available and scalable.

---

## 3. Less Client-Specific Intelligence

The LB may not know detailed client-side context:

```text
caller retry history
business operation type
local circuit breaker state
custom client latency data
```

---

## 4. Operational Dependency

If LB config is wrong, many services can break at once.

Example:

```text
wrong upstream config
wrong health check path
wrong timeout
wrong TLS certificate
```

---

# 19. Important Production Pattern: Hybrid Model

Real production systems often use both.

Example:

```text
External user
   -> Cloud Load Balancer
   -> API Gateway
   -> internal service
   -> client-side LB / service mesh
   -> downstream service
```

---

## Hybrid Diagram

```text
Internet
   |
   v
AWS ALB / Cloud LB
   |
   v
API Gateway
   |
   v
order-service
   |
   | client-side LB or Envoy sidecar
   v
payment-service instances
```

This is common because:

```text
1. External traffic needs server-side LB
2. Internal traffic may use service discovery
3. Mesh/proxy can handle retries and mTLS
4. Gateway handles auth/routing/rate limits
```

---

# 20. Relation to Eureka

Eureka is commonly used with **client-side discovery and load balancing**.

Flow:

```text
1. payment-service registers with Eureka
2. order-service fetches registry from Eureka
3. order-service caches instance list
4. Spring Cloud LoadBalancer chooses payment instance
5. order-service calls selected payment instance directly
```

Eureka server usually does not sit in the request path.

That means:

```text
Eureka is not a request load balancer.
Eureka is a registry.
Client uses registry data to load balance.
```

---

# 21. Relation to Kubernetes

Kubernetes commonly gives server-side style discovery through Service DNS.

Client calls:

```text
http://payment-service
```

Kubernetes Service routes traffic to pods.

But with service mesh, traffic may go through Envoy sidecars:

```text
order pod
   -> local Envoy sidecar
   -> payment pod Envoy sidecar
   -> payment container
```

This creates a more advanced proxy-based model.

---

# 22. Health-Aware Load Balancing

Both models must avoid unhealthy instances.

## Client-Side Health Awareness

Client may use:

```text
1. Registry health status
2. Local failure count
3. Circuit breaker state
4. Timeout history
5. Retry result
```

Example:

```text
If instance fails 5 times, temporarily remove from local rotation.
```

---

## Server-Side Health Awareness

LB performs active health checks:

```text
GET /actuator/health
```

If response is not healthy:

```text
remove backend from pool
```

---

# 23. Failure Scenario: Dead Instance

## Client-Side LB Failure

```text
1. payment-service-2 dies
2. Eureka eventually marks it DOWN
3. order-service still has stale cached registry
4. order-service may call dead instance
5. request fails
6. retry/circuit breaker handles failure
7. cache refresh removes dead instance
```

Important concept:

```text
Client-side LB needs timeout + retry + circuit breaker.
```

---

## Server-Side LB Failure

```text
1. payment-service-2 dies
2. LB health check fails
3. LB removes backend
4. new requests avoid dead backend
```

But there is still a detection delay.

---

# 24. Failure Scenario: Load Balancer Down

In server-side LB, if the load balancer fails:

```text
client -> LB X -> service
```

Traffic can fail unless LB is highly available.

Production solution:

```text
1. Multiple LB nodes
2. DNS failover
3. Cloud-managed LB
4. Anycast/VIP
5. Active-active proxies
```

Client-side LB avoids a central request-path LB, but still depends on registry availability and cached data.

---

# 25. Failure Scenario: Registry Down

## Client-Side LB

If registry is down:

```text
existing clients can continue using cached registry
new clients may struggle to discover services
```

This is why Eureka clients cache registry data.

## Server-Side LB

If LB has static or previously known backends:

```text
traffic can continue
```

If LB dynamically depends on registry updates:

```text
new backend changes may not be reflected
```

---

# 26. CAP and Consistency Angle

Service discovery is often eventually consistent.

This means:

```text
Not every client sees the same instance list at the same time.
```

Client-side LB accepts this reality.

Example:

```text
Client A registry cache:
[payment-1, payment-2]

Client B registry cache:
[payment-1, payment-2, payment-3]
```

For a short period, both are valid views.

Production systems handle this using:

```text
timeouts
retries
circuit breakers
health checks
short cache TTLs
heartbeats
connection draining
```

---

# 27. Load Balancing Algorithms

Both client-side and server-side models can use similar algorithms.

## Round Robin

```text
A -> B -> C -> A -> B -> C
```

Good when instances are equal.

## Weighted Round Robin

```text
A weight 5
B weight 2
C weight 1
```

A receives more traffic.

## Least Connections

Choose instance with fewest active connections.

Good when requests are long-lived.

## Random

Pick random healthy instance.

Simple and often surprisingly effective.

## Least Latency

Choose instance with best recent latency.

Requires metrics.

## Zone-Aware

Prefer same availability zone.

```text
caller in zone-a -> prefer service in zone-a
```

Reduces cross-zone latency and cost.

---

# 28. Interview Explanation

If interviewer asks:

> Difference between client-side and server-side load balancing?

Strong answer:

```text
In client-side load balancing, the caller fetches service instances from a registry and chooses the target instance locally. This is common with Eureka, Spring Cloud LoadBalancer, OpenFeign, and older Netflix Ribbon. The client knows backend addresses and directly calls the selected instance.

In server-side load balancing, the caller sends traffic to a stable endpoint like NGINX, AWS ALB, Kubernetes Service, or Envoy. The load balancer chooses a healthy backend. The client does not know individual backend instances.

Client-side LB can reduce hops and allow smart per-client routing, but adds complexity to every client and can suffer from stale registry caches. Server-side LB centralizes policy and is language agnostic, but adds another hop and must be highly available.
```

---

# 29. FAANG/System Design Usage

## When to Use Client-Side LB

Use it when:

```text
1. Internal microservices talk to each other
2. You already have service registry
3. You want direct service-to-service calls
4. You want zone-aware routing
5. You use Spring Cloud / gRPC / service mesh aware clients
```

Example systems:

```text
order -> payment
feed -> user-service
notification -> template-service
search -> ranking-service
```

---

## When to Use Server-Side LB

Use it when:

```text
1. External traffic enters system
2. Clients should not know service instances
3. You need centralized TLS/routing/rate limiting
4. You support many languages/clients
5. You use Kubernetes Service or cloud LB
```

Example systems:

```text
mobile app -> API Gateway
browser -> Cloud LB
partner system -> public API endpoint
legacy service -> NGINX endpoint
```

---

# 30. Real Production Architecture Example

## Payment System

```text
Mobile App
   |
   v
Cloud Load Balancer
   |
   v
API Gateway
   |
   v
Order Service
   |
   | client-side LB / service mesh
   v
Payment Service
   |
   v
Bank Connector Service
```

### Explanation

```text
Cloud LB:
Handles external traffic, TLS, public endpoint.

API Gateway:
Handles auth, rate limiting, routing.

Order Service -> Payment Service:
Can use client-side LB, Kubernetes Service, or service mesh.

Payment -> Bank Connector:
Needs retries, circuit breaker, timeout, idempotency.
```

---

# 31. Connection with Circuit Breaker

Load balancing decides:

```text
Which instance should I call?
```

Circuit breaker decides:

```text
Should I call this dependency at all?
```

They work together.

Example:

```text
payment-service-2 has high failure rate

LB: may still select it if marked healthy
Circuit breaker: blocks calls after repeated failures
```

Good client-side load balancers integrate with circuit breakers.

---

# 32. Timeout and Retry Design

Bad design:

```text
client timeout = 30 seconds
retry count = 5
```

This can amplify failures.

Better design:

```text
connect timeout = 200ms
read timeout = 500ms
retry only safe/idempotent requests
use jittered backoff
respect retry budget
avoid retry storm
```

For payment APIs:

```text
Never blindly retry non-idempotent charge requests.
Use idempotency key.
```

---

# 33. Sticky Sessions

Sometimes requests from same user need same backend.

Example:

```text
user-123 -> instance A
user-123 -> instance A
```

This is called sticky session or session affinity.

## Why It Can Be Dangerous

If backend stores session locally:

```text
instance A dies -> session lost
```

Better production design:

```text
store session in Redis/database
make services stateless
avoid sticky sessions unless required
```

---

# 34. Blue-Green and Canary Routing

Server-side LB is often better for global traffic shifting.

Example:

```text
v1 receives 95% traffic
v2 receives 5% traffic
```

```text
payment-v1 weight 95
payment-v2 weight 5
```

Client-side LB can also do this if metadata supports it, but server-side/proxy-based routing is usually easier to control centrally.

---

# 35. Debugging Checklist

When service call fails, check:

```text
1. Is target service registered?
2. Are instances marked healthy?
3. Is client cache stale?
4. Is load balancer backend pool correct?
5. Is health check path correct?
6. Are ports correct?
7. Is DNS resolving?
8. Are firewall/security group rules open?
9. Are timeouts too high or too low?
10. Are retries causing duplicate traffic?
11. Is circuit breaker open?
12. Is service overloaded?
13. Are all instances receiving traffic evenly?
```

---

# 36. Metrics to Monitor

For load balancing, monitor:

```text
request_count_by_instance
error_rate_by_instance
p50_latency_by_instance
p95_latency_by_instance
p99_latency_by_instance
active_connections
connection_errors
timeout_count
retry_count
circuit_breaker_open_count
backend_health_status
registry_instance_count
```

Important interview line:

```text
Average latency hides bad instances. Always check latency and error rate per instance.
```

---

# 37. Common Mistakes

## Mistake 1: Thinking Eureka Is a Load Balancer

Wrong:

```text
Eureka balances traffic.
```

Correct:

```text
Eureka stores service instance information.
The client-side load balancer uses Eureka data to choose an instance.
```

---

## Mistake 2: No Timeout

Without timeout:

```text
client threads block forever
thread pool gets exhausted
service becomes slow/unavailable
```

---

## Mistake 3: Retrying Everything

Blind retries can cause:

```text
duplicate payments
database overload
retry storm
cascading failure
```

---

## Mistake 4: Ignoring Health Checks

If health check only checks process alive:

```text
app is running but DB is down
LB still sends traffic
requests fail
```

Better health check should include readiness:

```text
Can this instance actually serve traffic now?
```

---

## Mistake 5: Not Draining Connections

During deployment:

```text
instance removed immediately
active requests fail
```

Better:

```text
1. mark instance not ready
2. stop receiving new traffic
3. wait for in-flight requests
4. shutdown gracefully
```

---

# 38. Production Grade Design

A production-grade service call should usually include:

```text
1. Service discovery
2. Load balancing
3. Timeout
4. Retry with backoff
5. Circuit breaker
6. Bulkhead/thread isolation
7. Metrics
8. Tracing
9. Health-aware routing
10. Graceful shutdown
11. Connection pooling
12. Idempotency where needed
```

For example:

```text
order-service -> payment-service
```

Should not be just:

```java
restTemplate.postForObject(url, request, Response.class);
```

It should be protected with:

```text
load balancer + timeout + retry + circuit breaker + tracing + metrics
```

---

# 39. Client-Side LB Mini Implementation Idea

## Files

```text
ServiceInstance.java
ServiceRegistry.java
InMemoryServiceRegistry.java
LoadBalancer.java
RoundRobinLoadBalancer.java
HealthAwareLoadBalancer.java
Demo.java
```

---

## LoadBalancer Interface

```java
public interface LoadBalancer {
    ServiceInstance choose(String serviceName);
}
```

---

## Demo

```java
public class Demo {
    public static void main(String[] args) {
        InMemoryServiceRegistry registry = new InMemoryServiceRegistry();

        registry.register(new ServiceInstance("payment-service", "10.0.1.11", 8080, true));
        registry.register(new ServiceInstance("payment-service", "10.0.1.12", 8080, true));
        registry.register(new ServiceInstance("payment-service", "10.0.1.13", 8080, true));

        ClientSideRoundRobinLoadBalancer lb = new ClientSideRoundRobinLoadBalancer(registry);

        for (int i = 1; i <= 10; i++) {
            ServiceInstance selected = lb.choose("payment-service");
            System.out.println("Request " + i + " -> " + selected.url());
        }
    }
}
```

Expected output:

```text
Request 1 -> http://10.0.1.11:8080
Request 2 -> http://10.0.1.12:8080
Request 3 -> http://10.0.1.13:8080
Request 4 -> http://10.0.1.11:8080
Request 5 -> http://10.0.1.12:8080
Request 6 -> http://10.0.1.13:8080
```

---

# 40. HLD Interview Deep Answer

For a high-scale system, say:

```text
At the edge, I would use server-side load balancing with a managed cloud load balancer or NGINX/Envoy layer. This gives a stable public endpoint, TLS termination, centralized routing, rate limiting, and health checks.

Inside the cluster, service-to-service traffic can use Kubernetes Service, service mesh, or client-side load balancing depending on the platform. If using Spring Cloud Eureka, each service fetches registry data and Spring Cloud LoadBalancer chooses a healthy instance. If using Kubernetes-native architecture, services call stable DNS names and kube-proxy/IPVS or Envoy handles routing.

For reliability, I will add timeouts, retries with jitter, circuit breakers, per-instance metrics, health checks, and graceful shutdown. I will also avoid sticky sessions unless required and keep services stateless.
```

---

# 41. Quick Decision Matrix

| Requirement | Better Choice |
|---|---|
| Public internet traffic | Server-side LB |
| Mobile/browser clients | Server-side LB |
| Internal Java Spring services with Eureka | Client-side LB |
| Polyglot services | Server-side LB or service mesh |
| Central traffic policy | Server-side LB |
| Lowest hop count | Client-side LB |
| Canary/blue-green routing | Server-side LB / mesh |
| Zone-aware internal routing | Client-side LB / mesh |
| Kubernetes-native app | K8s Service / mesh |
| Legacy clients | Server-side LB |

---

# 42. Memory Hook

Remember:

```text
Client-side LB:
Client asks registry and chooses instance.

Server-side LB:
Client calls one endpoint; LB chooses instance.
```

Short form:

```text
Client-side = smart client
Server-side = smart proxy
```

---

# 43. One-Minute Revision

```text
Service discovery gives instance list.
Load balancing chooses one instance.

Client-side LB:
- client fetches instance list from registry
- client chooses target
- direct call to instance
- examples: Eureka + Feign, Spring Cloud LoadBalancer, gRPC
- pros: fewer hops, smarter client decisions
- cons: client complexity, stale cache, SDK per language

Server-side LB:
- client calls stable endpoint
- LB/proxy chooses backend
- examples: NGINX, HAProxy, AWS ALB, Kubernetes Service, Envoy
- pros: simple clients, centralized control, language agnostic
- cons: extra hop, LB must be highly available

Production usually uses hybrid:
external traffic -> cloud LB/API gateway
internal traffic -> service discovery / K8s service / service mesh
```

---

# 44. Final Interview Cheat Answer

```text
Client-side load balancing means the caller is responsible for choosing the target instance. It gets instances from a registry like Eureka or Consul and uses a local algorithm such as round robin, weighted round robin, or health-aware routing. This reduces an extra network hop and supports smart per-client decisions, but it increases client complexity and can suffer from stale registry caches.

Server-side load balancing means the caller sends requests to a stable endpoint like NGINX, AWS ALB, Kubernetes Service, or Envoy. The load balancer chooses a healthy backend. This makes clients simple and centralizes traffic policies, but adds another hop and requires the load balancer layer to be highly available.

In real production systems, both are often combined: external traffic enters through a server-side load balancer and API gateway, while internal service-to-service calls use Kubernetes Service, service mesh, or client-side discovery depending on the platform.
```

---

# 45. Next File Connection

This file prepares you for:

```text
015_Health_Aware_LoadBalancing.md
```

Because after understanding where load balancing happens, the next question is:

```text
How do we avoid unhealthy, slow, overloaded, or failing instances?
```

That is health-aware load balancing.
