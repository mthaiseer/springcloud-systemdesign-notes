
# 019_SpringCloud_Eureka_OpenFeign_Internals

# Deep Dive: Spring Cloud Eureka + OpenFeign Internals

## Table of Contents

1. Why Service Discovery Exists
2. Hardcoded IP Problem
3. Eureka Architecture
4. Eureka Registry Data Structures
5. Registration Flow Internals
6. Lease & Heartbeat Model
7. Self Preservation Mode
8. Registry Replication
9. Eureka Client Cache
10. Delta Fetch Mechanism
11. OpenFeign Architecture
12. Dynamic Proxy Internals
13. Feign Invocation Pipeline
14. RequestTemplate Internals
15. Encoder / Decoder Internals
16. Spring Cloud LoadBalancer Internals
17. Round Robin Implementation
18. Retry Flow
19. Timeout Flow
20. Circuit Breaker Integration
21. Complete Request Lifecycle
22. Failure Scenarios
23. Performance Analysis
24. Scaling Analysis
25. Memory Analysis
26. Production Troubleshooting
27. Source Code Mental Models
28. Interview Questions
29. Senior Engineer Summary

---

# 1. Why Service Discovery Exists

Imagine:

Order Service

10.0.0.5:8080

Payment Service

10.0.0.10:8080

User Service

10.0.0.15:8080

Order Service calls:

http://10.0.0.10:8080/payments

Problem:

What if payment service restarts?

New IP:

10.0.0.25

Order Service breaks.

This is why service discovery exists.

---

# 2. Hardcoded IP Failure Model

Without Eureka:

Order ---> 10.0.0.10

Container restart

Payment ---> 10.0.0.25

Order still calls old address.

Result:

Connection Refused

Production outage.

Service discovery removes this dependency.

---

# 3. Eureka Architecture

Main Components

Eureka Server
    |
    +-- Registry
    +-- Lease Manager
    +-- Replication Engine
    +-- Heartbeat Tracker
    +-- REST API

Clients
    |
    +-- Registration
    +-- Discovery
    +-- Cache

Think:

Eureka = DNS for Microservices

---

# 4. Internal Registry Structure

Core Structure:

ConcurrentHashMap<
    String,
    Map<String, Lease<InstanceInfo>>
>

Level 1

SERVICE_NAME

Level 2

INSTANCE_ID

Level 3

Lease Object

Example:

PAYMENT-SERVICE
   payment-1
   payment-2
   payment-3

ORDER-SERVICE
   order-1

Lookup:

O(1)

Very fast.

---

# 5. Registration Flow

Spring Boot Startup

Step 1

Create InstanceInfo

Contains:

- app name
- hostname
- ip
- port
- metadata

Step 2

DiscoveryClient starts

Step 3

POST registration

POST /eureka/apps/PAYMENT-SERVICE

Step 4

Registry updated

Step 5

Replication triggered

---

# 6. Lease Internals

Every instance gets lease.

Lease contains:

registrationTimestamp

lastRenewalTimestamp

duration

cancelled

evictionTimestamp

Default:

Renewal = 30 sec

Expiration = 90 sec

Meaning:

3 missed heartbeats => remove.

---

# 7. Heartbeat Internals

Every 30 sec:

PUT /eureka/apps/PAYMENT-SERVICE/payment-1

Server updates:

lastRenewalTimestamp

Instance remains UP.

Without heartbeat:

Lease expires.

---

# 8. Eviction Process

Background thread:

EvictionTask

Runs periodically.

Pseudo:

for lease:
   if expired:
      remove

Complexity:

O(N)

---

# 9. Self Preservation Mode

Most important Eureka feature.

Scenario:

Network issue.

1000 services registered.

Suddenly 700 heartbeats lost.

Normal logic:

Remove 700 services.

Disaster.

Instead:

Eureka enters:

SELF PRESERVATION MODE

Meaning:

Stop aggressive removals.

Protect registry.

---

# 10. Registry Replication

Cluster:

Eureka-1
Eureka-2
Eureka-3

Registration arrives at Eureka-1.

Replication:

Eureka-1 -> Eureka-2
Eureka-1 -> Eureka-3

Eventually all registries converge.

This is eventual consistency.

---

# 11. Client Cache

Every service keeps cache.

Order Service

Local Cache

PAYMENT-SERVICE

10.0.0.5
10.0.0.6
10.0.0.7

Requests never hit Eureka directly.

Huge performance gain.

---

# 12. Delta Fetch

Instead of downloading entire registry.

Client asks:

What changed?

Server returns:

Added
Removed
Updated

Much smaller payload.

---

# 13. OpenFeign Architecture

Developer writes:

@FeignClient("payment-service")

Interface only.

No implementation.

Spring generates implementation.

How?

Dynamic Proxy.

---

# 14. Feign Startup Internals

Spring Scan

FeignClientsRegistrar

Registers bean definitions.

Then:

FeignClientFactoryBean

Creates client.

Then:

Feign.Builder

Creates proxy.

Result:

PaymentClient proxy object.

---

# 15. Dynamic Proxy Model

Real implementation doesn't exist.

Proxy intercepts:

paymentClient.get(100)

Proxy converts:

HTTP Request

This is classic Proxy Pattern.

---

# 16. Invocation Pipeline

Method Call

-> InvocationHandler

-> RequestTemplate

-> LoadBalancer

-> HTTP Client

-> Response Decoder

-> Java Object

---

# 17. RequestTemplate Internals

Stores:

HTTP Method

URL

Headers

Body

Path Variables

Query Parameters

Example:

GET

/payments/{id}

id=100

Final URL:

/payments/100

---

# 18. Encoder Internals

Java Object

PaymentRequest

Encoder converts:

JSON

Example:

{
  "amount":100
}

Supported:

Jackson

Gson

Custom Encoder

---

# 19. Decoder Internals

Response:

JSON

Decoder converts:

PaymentResponse Java Object

Automatic serialization.

---

# 20. LoadBalancer Architecture

Old:

Ribbon

New:

Spring Cloud LoadBalancer

Responsibilities:

Instance Selection

Retries

Load Distribution

Health Awareness

---

# 21. Round Robin Algorithm

Instances

A
B
C

Requests

1 -> A
2 -> B
3 -> C
4 -> A

Implementation:

AtomicInteger counter

index = counter % size

Complexity O(1)

---

# 22. Random Load Balancing

Random index

Good distribution.

Simple.

O(1)

---

# 23. Weighted Load Balancing

Server A

Weight 5

Server B

Weight 1

Traffic:

AAAAAB

Useful when machines differ.

---

# 24. Retry Internals

Instance A

Timeout

Retry

Instance B

Timeout

Retry

Instance C

Success

Improves resiliency.

---

# 25. Timeout Internals

Connect Timeout

TCP connection time.

Read Timeout

Waiting for response.

Example:

2s connect

5s read

---

# 26. Circuit Breaker Integration

Flow:

Feign

-> Circuit Breaker

-> Remote Service

Failures exceed threshold.

Circuit opens.

Requests fail fast.

---

# 27. Complete Request Lifecycle

OrderController

calls

PaymentClient

Proxy intercepts

Build RequestTemplate

LoadBalancer chooses instance

HTTP client executes

Payment service responds

Decoder converts JSON

Return Java Object

---

# 28. Deep Sequence Diagram

Order Service

    |
    v

Feign Proxy

    |
    v

Invocation Handler

    |
    v

Request Template

    |
    v

Discovery Client

    |
    v

Local Eureka Cache

    |
    v

LoadBalancer

    |
    v

Chosen Instance

    |
    v

HTTP Client

    |
    v

Payment Service

    |
    v

Response

    |
    v

Decoder

    |
    v

Java Object

---

# 29. Failure Scenario 1

Instance crashes.

Heartbeat stops.

Registry still contains entry.

Temporary stale data.

Requests may fail.

After lease expiration:

Entry removed.

System recovers.

---

# 30. Failure Scenario 2

Eureka Server Down

Client cache exists.

Services continue working.

Major advantage.

---

# 31. Failure Scenario 3

Network Partition

Client cannot reach Eureka.

Local cache still used.

System partially survives.

---

# 32. Eventual Consistency

Registration happens.

Replication delayed.

Different Eureka nodes may differ briefly.

Eventually same state.

CAP tradeoff:

Availability favored.

---

# 33. Threading Model

Eureka Client Threads

Heartbeat Thread

Cache Refresh Thread

Registration Thread

Background Scheduler

Important interview topic.

---

# 34. Performance Analysis

Without Cache

Every request

-> Eureka

Huge bottleneck.

With Cache

Request

-> Local memory

Latency microseconds.

---

# 35. Memory Analysis

One Instance:

~1-3 KB

1000 Instances

~3 MB

10000 Instances

~30 MB

Cheap.

---

# 36. Scaling Analysis

100 Services

Heartbeat every 30 sec

100/30 = 3.3 heartbeats/sec

1000 Services

33 heartbeats/sec

10000 Services

333 heartbeats/sec

Still manageable.

---

# 37. Production Debugging

Problem:

503 errors.

Check:

1. Eureka UI
2. Instance UP status
3. Heartbeats
4. Cache refresh
5. LoadBalancer logs
6. Feign timeout settings

---

# 38. Common Mistakes

Using localhost registration

Wrong hostname

Short lease duration

No retries

No circuit breaker

Huge timeout values

---

# 39. Source Code Mental Model

FeignClient
    |
    v
FeignClientFactoryBean
    |
    v
Feign.Builder
    |
    v
Dynamic Proxy

EurekaClient
    |
    v
DiscoveryClient
    |
    v
Local Registry Cache

---

# 40. Interview Question

How does Feign work internally?

Answer:

Feign creates dynamic proxy implementations during startup. Method invocations are intercepted and transformed into RequestTemplate objects, routed through Spring Cloud LoadBalancer, resolved using Eureka service discovery, executed via HTTP clients, decoded, and returned as Java objects.

---

# 41. Interview Question

How does Eureka detect dead instances?

Answer:

Using lease renewal heartbeats. If heartbeats are not received within lease expiration duration, background eviction removes the instance.

---

# 42. Interview Question

Why local cache?

Answer:

To avoid Eureka becoming bottleneck and provide low latency service discovery.

---

# 43. Interview Question

Why Self Preservation Mode?

Answer:

To prevent mass eviction during network failures and improve availability.

---

# 44. Senior Engineer Mental Model

OpenFeign is:

Proxy
+ Serialization
+ Discovery
+ Load Balancing
+ Retry
+ HTTP Client Abstraction

Eureka is:

Registry
+ Lease Manager
+ Heartbeat Tracker
+ Discovery Engine
+ Replication System

---

# 45. Production Architecture

Gateway
   |
Order Service
   |
Feign
   |
LoadBalancer
   |
Eureka Cache
   |
Payment Service Cluster

This is the architecture running in many enterprise Spring Cloud systems.

---

# Final Summary

Eureka maintains a distributed in-memory registry of service instances using lease-based registration, heartbeats, eviction policies, replication, local caching, and eventual consistency. OpenFeign creates dynamic proxy implementations that transform Java method calls into HTTP requests. Spring Cloud LoadBalancer resolves service instances from Eureka caches, distributes traffic, performs retries, and integrates with circuit breakers. Together they form the backbone of service discovery and inter-service communication in Spring Cloud microservice ecosystems.
