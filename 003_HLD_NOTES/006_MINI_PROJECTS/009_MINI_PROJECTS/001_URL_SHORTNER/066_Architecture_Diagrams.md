# 066_Architecture_Diagrams.md
# MiniURLShortener — Architecture Diagrams

> Core mental model: **Architecture diagrams are not decoration. They are the visual contract of the system. A good diagram explains boundaries, request flow, data flow, ownership, scaling points, failure points, and tradeoffs faster than paragraphs of text.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. What Architecture Diagrams Must Answer](#3-what-architecture-diagrams-must-answer)
- [4. Diagram Levels Mental Model](#4-diagram-levels-mental-model)
- [5. Context Diagram](#5-context-diagram)
- [6. Container Diagram](#6-container-diagram)
- [7. Component Diagram](#7-component-diagram)
- [8. Request Flow Diagram](#8-request-flow-diagram)
- [9. Data Flow Diagram](#9-data-flow-diagram)
- [10. Deployment Diagram](#10-deployment-diagram)
- [11. Runtime Sequence Diagram](#11-runtime-sequence-diagram)
- [12. Failure Flow Diagram](#12-failure-flow-diagram)
- [13. Scaling Diagram](#13-scaling-diagram)
- [14. Security Boundary Diagram](#14-security-boundary-diagram)
- [15. Observability Diagram](#15-observability-diagram)
- [16. Complete URL Shortener Architecture](#16-complete-url-shortener-architecture)
- [17. Diagramming Rules For Senior Engineers](#17-diagramming-rules-for-senior-engineers)
- [18. Step-by-Step Dry Runs](#18-step-by-step-dry-runs)
- [19. Production Failure Stories](#19-production-failure-stories)
- [20. Debugging Mindset](#20-debugging-mindset)
- [21. Common Mistakes](#21-common-mistakes)
- [22. Interview-Ready Explanation](#22-interview-ready-explanation)
- [23. Senior Engineer Checklist](#23-senior-engineer-checklist)
- [24. One-Page Cheat Sheet](#24-one-page-cheat-sheet)
- [25. One Picture To Remember](#25-one-picture-to-remember)

---

## 1. Why This Exists

By now MiniURLShortener has many production pieces:

```text
Spring Boot API
PostgreSQL
Redis cache
Kafka click analytics
async worker
Docker
Kubernetes
Ingress
HPA
RDS / ElastiCache / MSK
observability
failure handling
```

Without architecture diagrams, the system becomes hard to explain.

You may know every class and configuration, but in interviews and production reviews, people ask:

```text
Where does the request enter?
Which service owns writes?
Where is cache used?
What happens if Redis is down?
What is synchronous and asynchronous?
Where does Kafka fit?
How does traffic scale?
What is inside Kubernetes?
Where are logs, metrics, and traces collected?
What is the failure boundary?
```

A strong architecture diagram answers these visually.

Bad diagram:

```text
Client -> App -> DB
```

Better diagram:

```text
Browser / Mobile
      |
      v
DNS / CDN / WAF
      |
      v
Ingress / Load Balancer
      |
      v
Spring Boot URL API Pods
      |              |
      |              +--> Redis cache
      |
      +--> PostgreSQL primary / read replicas
      |
      +--> Kafka click-events topic
                         |
                         v
                 Analytics Worker
```

Production memory:

```text
A diagram should show where traffic flows, where state lives, where failures happen, and where scaling happens.
```

---

## 2. The One Core Mental Model

Architecture diagrams are:

```text
MAPS OF SYSTEM BOUNDARIES AND FLOWS
```

They are not just boxes.

A useful diagram shows:

```text
1. Boundaries
2. Direction of flow
3. Ownership of data
4. Runtime behavior
5. Failure points
6. Scaling points
7. External dependencies
```

ASCII:

```text
                 SYSTEM MAP MENTAL MODEL

     Who uses it?          Where request enters?
          |                         |
          v                         v
+----------------+         +-------------------+
| External Users | ----->  | Edge / Ingress    |
+----------------+         +-------------------+
                                  |
                                  v
                         +-------------------+
                         | Application Core  |
                         +-------------------+
                            |        |       |
                            v        v       v
                         Cache      DB     Queue
                            |        |       |
                            v        v       v
                       Speed     Truth   Async Work
```

One-line memory:

```text
Architecture diagrams convert invisible runtime behavior into visible engineering decisions.
```

---

## 3. What Architecture Diagrams Must Answer

A 10/10 architecture diagram should answer these questions:

```text
1. Who are the external actors?
2. What are the system boundaries?
3. What are the main runtime components?
4. Which path is synchronous?
5. Which path is asynchronous?
6. Where does data persist?
7. Where is cache used?
8. Where can the system scale horizontally?
9. Where can the system fail?
10. How do engineers observe it?
```

For MiniURLShortener:

```text
External actors:
    browser, mobile app, API client, admin, analytics consumer

Main components:
    Spring Boot URL API, Redis, PostgreSQL, Kafka, Worker

Synchronous paths:
    create short URL
    redirect short URL

Asynchronous paths:
    click event analytics
    aggregation
    abuse detection later

Persistent state:
    PostgreSQL

Fast lookup state:
    Redis

Event stream:
    Kafka

Deployment surface:
    Kubernetes / EKS
```

If your diagram does not answer these, it is probably too shallow.

---

## 4. Diagram Levels Mental Model

Do not draw one giant diagram for everything.

Use levels.

```text
Level 1: Context       -> Who uses the system?
Level 2: Container     -> What deployable units exist?
Level 3: Component     -> What is inside one service?
Level 4: Runtime Flow  -> What happens during a request?
Level 5: Deployment    -> Where does it run?
Level 6: Failure       -> What breaks and how do we degrade?
```

ASCII:

```text
+--------------------------------------------------+
| Level 1: System Context                          |
| User -> URL Shortener -> external systems        |
+--------------------------------------------------+
                  |
                  v
+--------------------------------------------------+
| Level 2: Containers                              |
| API, DB, Redis, Kafka, Worker                    |
+--------------------------------------------------+
                  |
                  v
+--------------------------------------------------+
| Level 3: Components                              |
| Controller, Service, Repository, Cache, Producer |
+--------------------------------------------------+
                  |
                  v
+--------------------------------------------------+
| Level 4: Runtime Flow                            |
| Create, Redirect, Analytics                      |
+--------------------------------------------------+
                  |
                  v
+--------------------------------------------------+
| Level 5: Deployment                              |
| Ingress, Pods, HPA, RDS, ElastiCache, MSK        |
+--------------------------------------------------+
```

Interview rule:

```text
Start high-level, then zoom in only when needed.
```

---

## 5. Context Diagram

A context diagram shows the system as one box and its external actors.

It answers:

```text
Who uses the system?
What external systems does it touch?
What is inside vs outside?
```

MiniURLShortener context:

```text
+----------------+        create / manage links       +----------------------+
| API Client     | ----------------------------------> |                      |
+----------------+                                     |                      |
                                                       |                      |
+----------------+        redirect short link          | MiniURLShortener     |
| Browser User   | ----------------------------------> | System               |
+----------------+                                     |                      |
                                                       |                      |
+----------------+        admin block / inspect        |                      |
| Admin User     | ----------------------------------> |                      |
+----------------+                                     +----------+-----------+
                                                                  |
                                                                  | events / metrics
                                                                  v
                                                       +----------------------+
                                                       | Observability Stack  |
                                                       +----------------------+
```

What this diagram deliberately hides:

```text
Redis
PostgreSQL
Kafka
Pods
classes
repositories
```

Why?

Because context diagrams are for boundary clarity, not internals.

Good context diagram sentence:

```text
MiniURLShortener is a public-facing service used by clients to create short URLs and by browsers to resolve short codes, while emitting logs, metrics, traces, and analytics events for operations.
```

---

## 6. Container Diagram

A container diagram shows deployable/runtime pieces.

In C4 terminology, a container can mean:

```text
web app
backend service
database
cache
message broker
worker
```

MiniURLShortener container diagram:

```text
                         +-------------------+
                         | Browser / Client  |
                         +---------+---------+
                                   |
                                   v
                         +-------------------+
                         | Load Balancer /   |
                         | Ingress           |
                         +---------+---------+
                                   |
                                   v
                         +-------------------+
                         | Spring Boot       |
                         | URL API           |
                         +----+---------+----+
                              |         |
                 cache lookup |         | durable write/read
                              v         v
                       +------+--+   +--+----------------+
                       | Redis   |   | PostgreSQL        |
                       | Cache   |   | Primary + Replica |
                       +---------+   +-------------------+
                              |
                              | optional warm cache
                              v
                         fast redirects

                         Async click path:

                         +-------------------+
                         | Spring Boot API   |
                         +---------+---------+
                                   |
                                   | publish click event
                                   v
                         +-------------------+
                         | Kafka Topic       |
                         | click-events      |
                         +---------+---------+
                                   |
                                   v
                         +-------------------+
                         | Analytics Worker  |
                         +---------+---------+
                                   |
                                   v
                         +-------------------+
                         | Analytics Store   |
                         +-------------------+
```

Container responsibility table:

```text
+-------------------+-------------------------------------------+
| Container         | Responsibility                            |
+-------------------+-------------------------------------------+
| Ingress/LB        | Route external HTTP traffic               |
| URL API           | Create links, redirect links, validate    |
| Redis             | Fast shortCode -> longUrl lookup          |
| PostgreSQL        | Source of truth for URLs and metadata     |
| Kafka             | Durable click event buffer                |
| Worker            | Process clicks asynchronously             |
| Observability     | Logs, metrics, traces, alerts             |
+-------------------+-------------------------------------------+
```

Senior memory:

```text
Container diagrams show deployable things, not Java classes.
```

---

## 7. Component Diagram

A component diagram zooms inside the Spring Boot URL API.

It answers:

```text
What are the main modules inside the service?
How do they depend on each other?
Where do validation, cache, DB, and Kafka calls happen?
```

ASCII:

```text
+-------------------------------------------------------------+
| Spring Boot URL API                                         |
|                                                             |
|  +--------------------+                                     |
|  | UrlController      |                                     |
|  | POST /urls         |                                     |
|  | GET /{shortCode}   |                                     |
|  +---------+----------+                                     |
|            |                                                |
|            v                                                |
|  +--------------------+        +-------------------------+   |
|  | UrlService         | -----> | UrlValidatorService     |   |
|  | orchestration      |        | URL + alias rules       |   |
|  +----+----------+----+        +-------------------------+   |
|       |          |                                           |
|       |          |                                           |
|       v          v                                           |
| +-----------+ +----------------+                             |
| | Cache     | | UrlRepository  |                             |
| | Service   | | JPA/JDBC       |                             |
| +-----+-----+ +--------+-------+                             |
|       |                |                                     |
|       v                v                                     |
|    Redis          PostgreSQL                                 |
|                                                             |
|       +----------------------+                              |
|       | ClickEventProducer   | -----> Kafka                  |
|       +----------------------+                              |
|                                                             |
|  +-------------------------+                                |
|  | GlobalExceptionHandler  |                                |
|  +-------------------------+                                |
|                                                             |
+-------------------------------------------------------------+
```

Dependency direction:

```text
Controller -> Service -> Repository / Cache / Producer
Controller should not talk directly to DB.
Repository should not call Controller.
Exception handler should not contain business logic.
```

Clean dependency model:

```text
HTTP layer knows service.
Service knows domain and ports.
Infrastructure implements persistence, cache, and messaging.
```

Bad component diagram smell:

```text
Controller -> Repository
Controller -> Redis
Controller -> Kafka
```

That means orchestration is leaking into the HTTP layer.

---

## 8. Request Flow Diagram

A request flow diagram shows one user action end-to-end.

### Create Short URL Flow

```text
POST /api/v1/urls

Client
  |
  v
Ingress / LB
  |
  v
UrlController
  |
  v
DTO Validation
  |
  +-- invalid --> 400 VALIDATION_FAILED
  |
  v
UrlService
  |
  v
Business Validation
  |
  +-- invalid URL/alias --> 400 INVALID_URL / INVALID_ALIAS
  |
  v
Generate or accept shortCode
  |
  v
PostgreSQL INSERT
  |
  +-- duplicate alias --> 409 ALIAS_ALREADY_EXISTS
  |
  v
Redis SET optional warm cache
  |
  v
201 Created
```

### Redirect Flow

```text
GET /abc123

Browser
  |
  v
Ingress / LB
  |
  v
UrlController
  |
  v
Validate shortCode format
  |
  +-- invalid --> 400 INVALID_SHORT_CODE
  |
  v
Redis GET abc123
  |
  +-- hit --> return 302 Location
  |
  +-- miss
        |
        v
   PostgreSQL SELECT
        |
        +-- not found --> 404 SHORT_CODE_NOT_FOUND
        +-- blocked   --> 403 SHORT_CODE_BLOCKED
        +-- expired   --> 410 SHORT_CODE_EXPIRED
        |
        v
   Redis SET abc123 -> longUrl
        |
        v
   Kafka publish click event
        |
        v
   302 Location
```

Important production nuance:

```text
Redirect path must be very fast.
Analytics must not block redirect latency.
```

So click analytics should be asynchronous.

---

## 9. Data Flow Diagram

Data flow diagrams focus on data movement, not deployment.

For MiniURLShortener, there are two main data flows.

### URL Metadata Flow

```text
Create request
    |
    v
Validate longUrl + alias
    |
    v
short_urls table
    |
    +--> short_code
    +--> long_url
    +--> status
    +--> expires_at
    +--> created_by
    +--> created_at
```

ASCII:

```text
+----------------------+        +----------------------+
| CreateShortUrlRequest| -----> | URL API              |
| longUrl              |        | validation + service |
| customAlias          |        +----------+-----------+
| expiresAt            |                   |
+----------------------+                   v
                                  +----------------------+
                                  | PostgreSQL           |
                                  | short_urls table     |
                                  +----------+-----------+
                                             |
                                             v
                                  +----------------------+
                                  | Redis cache          |
                                  | shortCode -> longUrl |
                                  +----------------------+
```

### Click Event Flow

```text
Redirect request
    |
    v
Resolve URL
    |
    v
Emit ClickEvent
    |
    v
Kafka
    |
    v
Worker
    |
    v
Analytics aggregate
```

ASCII:

```text
Browser GET /abc123
      |
      v
+-------------------+
| URL API           |
| resolve redirect  |
+--------+----------+
         |
         | ClickEvent {shortCode, ts, ipHash, userAgentHash}
         v
+-------------------+
| Kafka             |
| click-events      |
+--------+----------+
         |
         v
+-------------------+
| Analytics Worker  |
+--------+----------+
         |
         v
+-------------------+
| Aggregates        |
| daily clicks      |
+-------------------+
```

Data ownership rule:

```text
PostgreSQL owns URL truth.
Redis owns temporary speed copy.
Kafka owns event movement.
Analytics store owns derived aggregates.
```

Never confuse cache data with source of truth.

---

## 10. Deployment Diagram

Deployment diagrams show where things run.

For EKS-style deployment:

```text
Internet
   |
   v
+-----------------------------+
| AWS Route53 / DNS           |
+-------------+---------------+
              |
              v
+-----------------------------+
| ALB / Ingress Controller    |
+-------------+---------------+
              |
              v
+-----------------------------------------------------+
| EKS Cluster                                         |
|                                                     |
|  +------------------+    +------------------+       |
|  | URL API Pod 1    |    | URL API Pod 2    |       |
|  | Spring Boot      |    | Spring Boot      |       |
|  +--------+---------+    +---------+--------+       |
|           |                        |                |
|           +-----------+------------+                |
|                       |                             |
|  +--------------------v--------------------+        |
|  | Kubernetes Service: url-api-service     |        |
|  +-----------------------------------------+        |
|                                                     |
|  +------------------+                              |
|  | Worker Pod(s)    |                              |
|  +------------------+                              |
|                                                     |
+----------------------+------------------------------+
                       |
        +--------------+---------------+
        |              |               |
        v              v               v
+-------------+  +-------------+  +-------------+
| RDS         |  | ElastiCache |  | MSK Kafka   |
| PostgreSQL  |  | Redis       |  |             |
+-------------+  +-------------+  +-------------+
```

Deployment diagram should show:

```text
1. Public entry point
2. Cluster boundary
3. App pods
4. Kubernetes service
5. Managed dependencies
6. External network calls
```

What not to include here:

```text
Java DTOs
private methods
small helper classes
SQL details
```

Deployment diagrams answer infrastructure questions, not code questions.

---

## 11. Runtime Sequence Diagram

Sequence diagrams show time order.

### Redirect Sequence

```text
Browser        Ingress        URL API        Redis        PostgreSQL        Kafka
   |              |              |             |              |              |
   | GET /abc123  |              |             |              |              |
   |------------->|              |             |              |              |
   |              | forward      |             |              |              |
   |              |------------->|             |              |              |
   |              |              | GET abc123  |              |              |
   |              |              |------------>|              |              |
   |              |              | cache miss  |              |              |
   |              |              |<------------|              |              |
   |              |              | SELECT      |              |              |
   |              |              |--------------------------->|              |
   |              |              | row found   |              |              |
   |              |              |<---------------------------|              |
   |              |              | SET cache   |              |              |
   |              |              |------------>|              |              |
   |              |              | publish click event         |              |
   |              |              |------------------------------------------>|
   |              | 302 Location |             |              |              |
   |<-------------|<-------------|             |              |              |
```

Important sequence lesson:

```text
Client should not wait for analytics processing.
```

Publishing to Kafka may happen before returning 302, but actual analytics aggregation must happen later.

Even better for extreme latency:

```text
Redirect response first, then non-blocking event publishing with local buffer or best-effort producer.
```

But for many systems:

```text
Resolve -> publish lightweight event -> return redirect
```

is acceptable if Kafka latency is low and producer timeout is controlled.

---

## 12. Failure Flow Diagram

Architecture diagrams must show failures.

### Redis Down During Redirect

```text
GET /abc123
   |
   v
URL API
   |
   v
Redis GET
   |
   +-- Redis down
          |
          v
   fallback to PostgreSQL
          |
          +-- row found -> return 302
          +-- not found -> 404
```

ASCII:

```text
Browser
  |
  v
URL API
  |
  v
+------------------+
| Try Redis        |
+--------+---------+
         |
         +-- success hit --> 302
         |
         +-- miss/down
                |
                v
        +------------------+
        | PostgreSQL       |
        | source of truth  |
        +--------+---------+
                 |
                 +-- found --> 302
                 +-- empty --> 404
```

Principle:

```text
Cache failure should degrade latency, not correctness.
```

### Kafka Down During Redirect

```text
Redirect request
   |
   v
Resolve URL
   |
   v
Kafka publish fails
   |
   +-- Option A: fail redirect?        BAD for UX
   +-- Option B: return redirect       Better
   +-- Option C: local retry/outbox    Best for reliability
```

ASCII:

```text
URL resolved successfully
        |
        v
Try publish click event
        |
        +-- success -> 302
        |
        +-- failure -> log + metric + fallback
                         |
                         v
                       302 still returned
```

Principle:

```text
Analytics failure should not break core redirect path.
```

### PostgreSQL Down During Create

```text
POST /urls
   |
   v
Validation passes
   |
   v
DB insert fails
   |
   v
503 Service Unavailable or safe 500 depending on mapping
```

Create depends on DB truth.

Redirect may partially survive with warm Redis cache.

```text
Create path availability depends on PostgreSQL.
Redirect path can survive some DB failures if cache hit.
```

---

## 13. Scaling Diagram

Scaling diagrams show where horizontal scaling helps and where bottlenecks remain.

```text
                 +-------------------+
                 | Load Balancer     |
                 +---------+---------+
                           |
        +------------------+------------------+
        |                  |                  |
        v                  v                  v
+---------------+  +---------------+  +---------------+
| API Pod 1     |  | API Pod 2     |  | API Pod N     |
+-------+-------+  +-------+-------+  +-------+-------+
        |                  |                  |
        +------------------+------------------+
                           |
            +--------------+--------------+
            |                             |
            v                             v
     +-------------+               +-------------+
     | Redis       |               | PostgreSQL  |
     | Cluster     |               | Primary     |
     +------+------+               +------+------+
            |                             |
            v                             v
   many fast reads                 write bottleneck
```

Scaling rules:

```text
API pods scale horizontally.
Redis scales reads and hot lookups.
Kafka scales by partitions and consumers.
PostgreSQL writes require careful design.
Read replicas help reads, not writes.
```

Create path scaling:

```text
API pods -> DB primary insert
```

Redirect path scaling:

```text
API pods -> Redis cache hit -> 302
```

Click analytics scaling:

```text
Kafka partitions -> consumer group workers
```

ASCII for Kafka scaling:

```text
click-events topic

+-------------+   +-------------+   +-------------+
| Partition 0 |   | Partition 1 |   | Partition 2 |
+------+------+   +------+------+   +------+------+
       |                 |                 |
       v                 v                 v
+-------------+   +-------------+   +-------------+
| Worker 1    |   | Worker 2    |   | Worker 3    |
+-------------+   +-------------+   +-------------+
```

Bottleneck checklist:

```text
[ ] DB connection pool exhausted?
[ ] Redis hot key pressure?
[ ] Kafka partition count too low?
[ ] Worker consumer lag increasing?
[ ] API CPU saturated?
[ ] Load balancer target health failing?
[ ] p99 latency rising?
```

---

## 14. Security Boundary Diagram

Security diagrams show trust zones.

```text
UNTRUSTED INTERNET
       |
       v
+-------------------+
| WAF / Rate Limit  |
+---------+---------+
          |
          v
+-------------------+
| Ingress / TLS     |
+---------+---------+
          |
          v
TRUSTED CLUSTER NETWORK
          |
          v
+-------------------+
| URL API Pods      |
+----+---------+----+
     |         |
     v         v
+--------+  +--------+
| Redis  |  | DB     |
| private|  | private|
+--------+  +--------+
```

Security boundaries:

```text
Public internet:
    untrusted users, bots, attackers

Edge:
    TLS termination, WAF, rate limit, request size limit

Application:
    validation, authentication, authorization, audit logs

Data layer:
    private subnet, credentials, least privilege

Observability:
    logs without secrets
```

For URL shortener specifically:

```text
Do not allow unrestricted admin APIs.
Do not expose database publicly.
Do not log sensitive long URLs fully.
Do not fetch long URLs server-side without SSRF protection.
Do not let custom aliases override reserved routes.
```

ASCII reserved route risk:

```text
User creates alias: /admin
        |
        v
Could conflict with real admin route
        |
        v
Block reserved aliases early
```

---

## 15. Observability Diagram

Observability diagram shows how engineers see the system.

```text
                          +------------------+
                          | User Request     |
                          +--------+---------+
                                   |
                                   v
                          +------------------+
                          | URL API Pod      |
                          +---+----------+---+
                              |          |
               logs ---------+          +--------- metrics
                              |          |
                              v          v
                       +----------+  +----------+
                       | Loki /   |  | Prometheus|
                       | ELK      |  +-----+----+
                       +----+-----+        |
                            |              v
                            |        +----------+
                            |        | Grafana  |
                            |        +----------+
                            |
 traces --------------------+
                            v
                       +----------+
                       | Jaeger / |
                       | Tempo    |
                       +----------+
```

What to observe:

```text
API:
    request rate
    error rate
    p50/p95/p99 latency
    CPU/memory
    thread pool
    DB pool

Redis:
    hit ratio
    latency
    evictions
    memory

PostgreSQL:
    connections
    slow queries
    locks
    replication lag

Kafka:
    produce latency
    consumer lag
    failed messages
    retry topic growth

Business:
    creates per minute
    redirects per minute
    not found rate
    expired rate
    blocked rate
```

Observability should connect to architecture.

Bad:

```text
We have Grafana.
```

Good:

```text
For every critical arrow in the architecture diagram, we track latency, error rate, and saturation.
```

---

## 16. Complete URL Shortener Architecture

This is the complete production mental model diagram.

```text
                                  PUBLIC INTERNET
                                        |
                                        v
                              +-------------------+
                              | DNS / CDN / WAF   |
                              +---------+---------+
                                        |
                                        v
                              +-------------------+
                              | ALB / Ingress     |
                              +---------+---------+
                                        |
                                        v
+--------------------------------------------------------------------------------+
| Kubernetes / EKS Cluster                                                       |
|                                                                                |
|    +---------------------+       +---------------------+                       |
|    | URL API Pod 1       |       | URL API Pod 2       |       ... Pod N        |
|    | Spring Boot         |       | Spring Boot         |                       |
|    +----------+----------+       +----------+----------+                       |
|               |                             |                                  |
|               +-------------+---------------+                                  |
|                             |                                                  |
|                    +--------v---------+                                        |
|                    | K8s Service      |                                        |
|                    | url-api-service  |                                        |
|                    +--------+---------+                                        |
|                             |                                                  |
|              +--------------+--------------+                                   |
|              |                             |                                   |
|              v                             v                                   |
|    +-------------------+          +--------------------+                       |
|    | Worker Pods       |          | Observability      |                       |
|    | analytics         |          | logs/metrics/traces|                       |
|    +---------+---------+          +--------------------+                       |
|              |                                                                 |
+--------------+-----------------------------------------------------------------+
               |
               | managed/private dependencies
               v
+-------------------+       +-------------------+       +-------------------+
| ElastiCache Redis |       | RDS PostgreSQL    |       | MSK Kafka         |
| cache lookup      |       | source of truth   |       | click-events      |
+-------------------+       +-------------------+       +-------------------+
```

Core request paths:

```text
Create:
Client -> Ingress -> URL API -> PostgreSQL -> Redis warm -> 201

Redirect cache hit:
Browser -> Ingress -> URL API -> Redis -> Kafka event -> 302

Redirect cache miss:
Browser -> Ingress -> URL API -> Redis miss -> PostgreSQL -> Redis set -> Kafka -> 302

Analytics:
URL API -> Kafka -> Worker -> Analytics Store
```

Core failure behavior:

```text
Redis down:
    fallback to PostgreSQL, latency increases

Kafka down:
    redirect should still work, analytics may be delayed/lost depending on outbox

PostgreSQL down:
    create fails, redirect only works on cache hits

Worker down:
    Kafka lag grows, redirect unaffected

API pods overloaded:
    HPA scales pods, but DB/Redis/Kafka must handle increased load
```

---

## 17. Diagramming Rules For Senior Engineers

### Rule 1: Label every arrow

Bad:

```text
API ----> DB
```

Good:

```text
API ---- SELECT short_code / INSERT url ----> PostgreSQL
```

### Rule 2: Show sync vs async

```text
Synchronous:
Client waits for result.

Asynchronous:
Client does not wait for worker processing.
```

ASCII:

```text
Sync:   Client ---> API ---> DB ---> API ---> Client

Async:  API ---> Kafka ---> Worker
        Client already got response
```

### Rule 3: Show source of truth

```text
PostgreSQL = truth
Redis = cache
Kafka = event stream
Analytics DB = derived view
```

### Rule 4: Show failure boundaries

For every dependency, ask:

```text
If this is down, what happens?
```

### Rule 5: Do not mix too many levels

Bad single diagram:

```text
User -> Controller -> Service -> Repository -> Pod -> Node -> RDS -> SQL index
```

This mixes:

```text
context
components
deployment
implementation details
```

Better:

```text
Use separate diagrams.
```

### Rule 6: Use diagrams to explain decisions

Every diagram should support a decision:

```text
Why Redis?
Why Kafka?
Why read replica?
Why HPA?
Why outbox?
Why rate limiter?
```

### Rule 7: Keep diagrams readable

A good diagram is not the most complete.

It is the easiest accurate diagram to understand.

---

## 18. Step-by-Step Dry Runs

### Dry Run 1: Explain architecture in interview

Interviewer asks:

```text
Can you explain your URL shortener architecture?
```

Answer flow:

```text
1. Start with context.
2. Show main containers.
3. Explain create path.
4. Explain redirect path.
5. Explain async analytics.
6. Explain scaling.
7. Explain failure behavior.
```

ASCII speaking map:

```text
Client
  |
  v
Ingress
  |
  v
URL API
  |        |        |
  v        v        v
Redis     DB      Kafka
(speed)  truth   async events
```

Strong explanation:

```text
The URL API is stateless and horizontally scalable. PostgreSQL is the source of
truth for URL mappings. Redis caches hot shortCode-to-longUrl mappings to keep
redirect latency low. Kafka decouples click analytics from the redirect path so
user redirects are not blocked by aggregation work. Workers consume click events
and update analytics asynchronously. In Kubernetes, API pods scale with HPA, while
RDS, Redis, and Kafka are managed dependencies. If Redis fails, we fall back to
PostgreSQL. If Kafka fails, redirect should still succeed and analytics degrades.
```

---

### Dry Run 2: Draw create flow

Request:

```http
POST /api/v1/urls
```

Flow:

```text
1. Client sends longUrl.
2. Ingress routes to API pod.
3. Controller validates DTO.
4. Service validates business rules.
5. Service generates shortCode.
6. Repository inserts into PostgreSQL.
7. Optional Redis warm cache.
8. API returns 201.
```

Diagram:

```text
Client -> Ingress -> API -> Validation -> PostgreSQL INSERT -> Redis SET -> 201
```

Tradeoff:

```text
Redis warm cache improves first redirect latency but create path has one extra dependency.
If Redis set fails, create should still succeed because PostgreSQL owns truth.
```

---

### Dry Run 3: Draw redirect flow

Request:

```http
GET /abc123
```

Flow:

```text
1. Browser requests short code.
2. API checks Redis.
3. If cache hit, return redirect quickly.
4. If cache miss, read PostgreSQL.
5. Validate status and expiry.
6. Populate Redis.
7. Publish click event.
8. Return 302.
```

Diagram:

```text
Browser -> Ingress -> API -> Redis GET
                              |
              +---------------+---------------+
              |                               |
             hit                             miss
              |                               |
              v                               v
           302                         PostgreSQL SELECT
                                              |
                                              v
                                        Redis SET + Kafka
                                              |
                                              v
                                             302
```

Tradeoff:

```text
Cache hit path is optimized because redirect traffic is read-heavy and latency-sensitive.
```

---

### Dry Run 4: Explain failure behavior

Question:

```text
What happens if Redis goes down?
```

Answer:

```text
Redis is not the source of truth. If Redis is down, redirect requests fall back to
PostgreSQL. The system remains correct but latency and DB load increase. We need
metrics and alerts for Redis failures and DB pressure because prolonged Redis
outage may overload PostgreSQL.
```

Diagram:

```text
Redis down
    |
    v
Fallback to DB
    |
    v
Correct but slower
    |
    v
Watch DB load and p99 latency
```

---

## 19. Production Failure Stories

### Failure Story 1: Diagram hid synchronous dependency

A team drew:

```text
API -> Kafka -> Worker
```

But the real code did:

```text
API waits for worker result before returning response.
```

Problem:

```text
The diagram claimed async, but runtime was sync.
```

Impact:

```text
p99 latency exploded when worker slowed down.
```

Fix:

```text
Draw sync arrows and async arrows differently.
Make client response independent of worker processing.
```

Lesson:

```text
A wrong diagram is worse than no diagram because it creates false confidence.
```

---

### Failure Story 2: Cache shown as truth

Diagram showed:

```text
API -> Redis
```

No DB was shown.

New engineer assumed Redis was authoritative and changed TTL behavior.

Impact:

```text
Some links disappeared from cache and were treated as missing.
```

Fix:

```text
Diagram source of truth explicitly:
PostgreSQL = truth, Redis = cache.
```

Lesson:

```text
Always label state ownership.
```

---

### Failure Story 3: Missing failure arrows

Architecture review only showed happy path:

```text
Client -> API -> Redis -> 302
```

During Redis outage, API returned 500 instead of falling back to DB.

Root cause:

```text
Failure behavior was never designed.
```

Fix:

```text
Add failure diagrams for Redis down, Kafka down, DB down, worker down.
```

Lesson:

```text
Production architecture includes unhappy paths.
```

---

### Failure Story 4: Deployment diagram missed DB connection pool

API pods scaled from 5 to 40.

Each pod had:

```text
Hikari maximumPoolSize = 30
```

Total possible DB connections:

```text
40 pods * 30 = 1200 connections
```

PostgreSQL limit:

```text
300 connections
```

Impact:

```text
DB connection exhaustion.
```

Fix:

```text
Deployment diagram added pod count, Hikari pool size, PgBouncer, and DB max connections.
```

Lesson:

```text
Scaling app pods without dependency capacity planning creates outages.
```

---

### Failure Story 5: Observability diagram was missing business metrics

System had CPU and memory dashboards.

But nobody tracked:

```text
redirect not found rate
expired link rate
Kafka consumer lag
Redis hit ratio
```

Impact:

```text
A cache bug caused DB overload, but team saw it late.
```

Fix:

```text
Add metrics for every important architecture arrow and business outcome.
```

Lesson:

```text
Infrastructure metrics are not enough. Architecture-aware metrics are required.
```

---

## 20. Debugging Mindset

When debugging with architecture diagrams, ask:

```text
1. Which request path is failing?
2. Which component owns this behavior?
3. Which dependency is on the critical path?
4. Is the failure synchronous or asynchronous?
5. Is the source of truth reachable?
6. Is cache hiding or amplifying the problem?
7. Is queue lag increasing?
8. Which arrow has increased latency?
9. Which boundary changed recently?
10. Is the diagram still true?
```

Debug map:

```text
Create failing:
    check validation, DB insert, unique constraints, Hikari, DB health

Redirect slow:
    check Redis hit ratio, Redis latency, DB fallback rate, p99 latency

Redirect 404 spike:
    check cache invalidation, DB rows, status/deleted logic, routing rules

Analytics delayed:
    check Kafka lag, worker health, retry/DLQ growth

API 5xx spike:
    check dependency timeouts, thread pools, connection pools, exception mapping
```

Architecture debugging diagram:

```text
Symptom
  |
  v
Identify request path
  |
  v
Find critical arrows
  |
  v
Check metrics per arrow
  |
  v
Check logs/traces for failing component
  |
  v
Confirm source of truth
  |
  v
Fix or degrade gracefully
```

Golden rule:

```text
A diagram is useful only if it helps you localize failure faster.
```

---

## 21. Common Mistakes

### Mistake 1: Drawing only boxes, no arrows

Wrong:

```text
API   DB   Redis   Kafka
```

Correct:

```text
API --cache get/set--> Redis
API --insert/select--> PostgreSQL
API --publish event--> Kafka
```

### Mistake 2: No labels on arrows

Without labels, nobody knows whether an arrow means:

```text
HTTP call
SQL query
cache lookup
event publish
metrics scrape
```

### Mistake 3: Mixing all levels

Wrong:

```text
User -> Controller -> Service -> Pod -> Node -> DB index
```

Correct:

```text
Separate context, component, deployment, and data diagrams.
```

### Mistake 4: Not showing failure behavior

Happy path diagrams are incomplete.

Production needs:

```text
Redis down
Kafka down
DB down
worker down
pod overload
```

### Mistake 5: Not showing source of truth

Always label:

```text
PostgreSQL = source of truth
Redis = cache
Kafka = event stream
Analytics = derived data
```

### Mistake 6: Too much detail

If a diagram has 50 boxes, nobody uses it.

Better:

```text
One diagram per question.
```

### Mistake 7: Diagram not updated after code changes

Outdated diagrams cause wrong decisions.

Rule:

```text
When architecture changes, diagrams change.
```

---

## 22. Interview-Ready Explanation

If interviewer asks:

```text
How do you design architecture diagrams for your URL shortener?
```

Strong answer:

```text
I use multiple diagram levels instead of one overloaded picture. I start with a
context diagram to show external users and system boundaries. Then I use a
container diagram to show deployable pieces: URL API, PostgreSQL, Redis, Kafka,
workers, and observability. Then I zoom into the Spring Boot API with a component
diagram showing controller, service, validation, repository, cache service, and
Kafka producer. For runtime behavior, I draw separate request flows for create
and redirect. Redirect is optimized around Redis cache hits, with PostgreSQL as
the source of truth on misses. Click analytics is asynchronous through Kafka so
redirect latency is not coupled to aggregation. I also draw failure diagrams for
Redis down, Kafka down, DB down, and worker lag, because production architecture
is not only happy path. Finally, I connect the diagram to metrics: every critical
arrow should have latency, error rate, and saturation visibility.
```

Why this is strong:

```text
1. Shows diagram levels.
2. Separates system boundary from internals.
3. Explains source of truth.
4. Explains sync vs async.
5. Explains scaling and failure behavior.
6. Connects architecture to observability.
7. Sounds like production experience, not textbook drawing.
```

Senior one-liner:

```text
I draw architecture diagrams to make boundaries, flows, ownership, scaling points, and failure behavior visible.
```

---

## 23. Senior Engineer Checklist

Before calling an architecture diagram production-ready, confirm:

```text
[ ] It has a clear title and purpose.
[ ] It shows system boundary.
[ ] It shows external actors.
[ ] It labels every major arrow.
[ ] It separates sync and async flows.
[ ] It identifies source of truth.
[ ] It distinguishes cache from database.
[ ] It shows queue/event flow clearly.
[ ] It shows deployment boundary if infrastructure-focused.
[ ] It shows scaling points.
[ ] It shows bottleneck dependencies.
[ ] It shows failure fallback for critical dependencies.
[ ] It connects to observability.
[ ] It avoids unnecessary class-level noise.
[ ] It is updated when architecture changes.
```

URL shortener-specific checklist:

```text
[ ] Create flow shown.
[ ] Redirect cache-hit flow shown.
[ ] Redirect cache-miss flow shown.
[ ] Kafka click analytics flow shown.
[ ] Redis failure fallback shown.
[ ] Kafka failure degradation shown.
[ ] PostgreSQL source-of-truth marked.
[ ] API pods shown as stateless.
[ ] HPA / scaling shown.
[ ] DB connection pressure considered.
[ ] Logs/metrics/traces shown.
```

---

## 24. One-Page Cheat Sheet

```text
Core mental model:
Architecture diagrams are maps of boundaries and flows.

Diagram levels:
1. Context      -> who uses system?
2. Container    -> deployable/runtime units
3. Component    -> inside one service
4. Runtime flow -> request sequence
5. Deployment   -> where it runs
6. Failure      -> what breaks and fallback

MiniURLShortener containers:
Client
Ingress / Load Balancer
Spring Boot URL API
Redis cache
PostgreSQL source of truth
Kafka click-events
Analytics worker
Observability stack

Create path:
Client -> Ingress -> API -> Validation -> PostgreSQL -> optional Redis -> 201

Redirect cache hit:
Browser -> Ingress -> API -> Redis -> Kafka -> 302

Redirect cache miss:
Browser -> Ingress -> API -> Redis miss -> PostgreSQL -> Redis set -> Kafka -> 302

State ownership:
PostgreSQL = truth
Redis = speed copy
Kafka = event transport
Analytics store = derived data

Failure rules:
Redis down -> fallback DB, slower
Kafka down -> redirect still works, analytics degraded
DB down -> create fails, redirect cache hits may work
Worker down -> Kafka lag grows, redirect unaffected

Diagram quality rules:
Label arrows.
Show boundaries.
Separate sync/async.
Show source of truth.
Show failure paths.
Do not mix all levels.
Connect diagrams to metrics.
```

---

## 25. One Picture To Remember

```text
                  MINIURLSHORTENER ARCHITECTURE MAP

                              Users / Browsers / API Clients
                                           |
                                           v
                                  +----------------+
                                  | DNS / CDN / WAF|
                                  +--------+-------+
                                           |
                                           v
                                  +----------------+
                                  | Ingress / LB   |
                                  +--------+-------+
                                           |
                                           v
                              +-----------------------+
                              | Stateless URL API Pods|
                              | create + redirect     |
                              +----+----------+---+---+
                                   |          |   |
                         cache     |          |   | publish event
                                   v          v   v
                              +-------+  +-------+  +-------+
                              | Redis |  |  DB   |  | Kafka |
                              | speed |  | truth |  | async |
                              +-------+  +---+---+  +---+---+
                                             |          |
                                             |          v
                                             |    +------------+
                                             |    | Worker     |
                                             |    | analytics  |
                                             |    +------------+
                                             |
                                             v
                                      durable URL data

                          Observability wraps every arrow:
                       logs + metrics + traces + alerts

FINAL MEMORY:

Context shows who uses it.
Containers show what runs.
Components show what is inside.
Flows show what happens.
Deployment shows where it runs.
Failure diagrams show how it survives.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Architecture diagrams are visual contracts of system boundaries and flows.
2. Use different diagram levels instead of one overloaded mega-diagram.
3. Always label arrows, source of truth, sync/async paths, and failure behavior.
4. For URL shortener, PostgreSQL is truth, Redis is speed, Kafka is async decoupling.
5. A senior diagram helps explain, scale, debug, and operate the system.
```

After this chapter, you can explain MiniURLShortener visually in interviews and production reviews:

```text
064_LLD_Interview_Answer.md
065_Production_Failure_Stories.md
066_Architecture_Diagrams.md
```
