# 029_Production_Failure_Playbook — The Incident Loop Model

## Core Mental Model

Do not imagine production debugging as:

```text
Open logs.
Search random errors.
Guess fix.
Deploy quickly.
Hope.
```

That is how incidents become worse.

The better mental model is:

> **A production failure is handled through a loop: detect, contain, locate, explain, fix, verify, and prevent.**

```text
Incident starts
      |
      v
+------------------+
| Detect           |
+------------------+
      |
      v
+------------------+
| Contain          |
+------------------+
      |
      v
+------------------+
| Locate           |
+------------------+
      |
      v
+------------------+
| Explain          |
+------------------+
      |
      v
+------------------+
| Fix              |
+------------------+
      |
      v
+------------------+
| Verify           |
+------------------+
      |
      v
+------------------+
| Prevent          |
+------------------+
```

This chapter teaches exactly one idea:

> **Production failure handling is not random debugging; it is a disciplined incident loop that protects users first, then finds root cause, then prevents recurrence.**

If you remember only one sentence:

> **During production failure, first reduce blast radius, then diagnose with signals, then fix safely, then prove recovery.**

---

## Why This Exists

In local development, failure is cheap.

```text
Test fails.
Read stack trace.
Fix code.
Run again.
```

In production, failure is expensive.

```text
Users cannot checkout.
Payments duplicate.
Database locks pile up.
Kafka lag grows.
Pods restart.
Error rate spikes.
Revenue drops.
Customer trust falls.
```

Production debugging has pressure:

```text
time pressure
incomplete information
many moving parts
multiple teams
real users affected
risk of making it worse
```

So you need a playbook.

Not because every incident is the same.

Because your brain under pressure needs a stable path.

A senior engineer does not start by asking:

```text
Which line of code is wrong?
```

A senior engineer first asks:

```text
How bad is it?
Who is affected?
Can we stop the bleeding?
Where is the failure stage?
What evidence proves the cause?
What is the safest fix?
How do we know recovery is real?
How do we prevent recurrence?
```

That is the production failure mindset.

---

## Problem Statement

A Spring Boot order service starts failing.

Symptoms:

```text
p99 latency increased from 200 ms to 8 seconds
HTTP 500 rate increased
Hikari connection timeout errors
Kubernetes restarted some pods
Postgres CPU high
Kafka consumer lag increasing
```

Many possible causes:

```text
bad deploy
slow query
N+1 query
database lock
connection pool exhaustion
downstream timeout
thread pool saturation
Redis outage
Kafka backlog
memory leak
GC pause
wrong configuration
traffic spike
```

The core problem:

> **How do you debug production failure safely without guessing and without worsening the outage?**

The answer is the incident loop:

```text
1. Detect: confirm symptom and impact.
2. Contain: stop user damage and reduce blast radius.
3. Locate: find failed stage in the system path.
4. Explain: build evidence-based causal chain.
5. Fix: apply safest short-term correction.
6. Verify: prove metrics recovered.
7. Prevent: add permanent fix, tests, alerts, and runbook.
```

---

## Real World Analogy

Imagine a hospital emergency room.

A patient arrives in critical condition.

A good doctor does not start with a long academic diagnosis.

First:

```text
Check breathing.
Stop bleeding.
Stabilize patient.
Measure vitals.
Find injury source.
Treat.
Monitor recovery.
Plan prevention.
```

Production incidents are similar.

```text
Hospital emergency              Production incident
------------------              -------------------
Patient unstable                service failing
Vitals                          metrics
Stop bleeding                   rollback/rate limit/disable feature
Diagnosis                       root cause analysis
Treatment                       fix/config/deploy
Monitoring                      verify recovery
Prevention                      postmortem/action items
```

The first goal is not intellectual completeness.

The first goal is stability.

---

## The One Mental Model

Production failure is a loop, not a straight line.

```text
Detect
  What is broken?

Contain
  How do we reduce damage now?

Locate
  Which system stage is failing?

Explain
  What causal chain fits the evidence?

Fix
  What is the safest change?

Verify
  Did the system actually recover?

Prevent
  What stops recurrence?
```

ASCII:

```text
                   +----------------+
                   |    DETECT      |
                   +--------+-------+
                            |
                            v
                   +----------------+
                   |   CONTAIN      |
                   +--------+-------+
                            |
                            v
                   +----------------+
                   |    LOCATE      |
                   +--------+-------+
                            |
                            v
                   +----------------+
                   |   EXPLAIN      |
                   +--------+-------+
                            |
                            v
                   +----------------+
                   |      FIX       |
                   +--------+-------+
                            |
                            v
                   +----------------+
                   |    VERIFY      |
                   +--------+-------+
                            |
                            v
                   +----------------+
                   |    PREVENT     |
                   +----------------+
```

If verification fails:

```text
go back to Locate or Contain.
```

---

## Core Concepts

## Incident

An incident is a production condition that harms users, reliability, correctness, security, or business operations.

Examples:

```text
high error rate
slow checkout
payment duplicate
data corruption
Kafka lag
DB outage
CPU saturation
memory leak
security misconfiguration
```

## Blast Radius

Blast radius means how much of the system or user base is affected.

```text
one endpoint
one tenant
one region
one database shard
all users
payment only
read-only traffic
write traffic
```

Reducing blast radius is often the first win.

## Mitigation

Mitigation is a fast action to reduce impact.

Examples:

```text
rollback
disable feature flag
scale pods
rate limit traffic
pause consumer
block bad tenant
increase timeout carefully
fail open/fail closed based on risk
switch to read-only mode
```

Mitigation may not solve root cause.
It buys time.

## Root Cause

Root cause is the deepest useful explanation that lets you prevent recurrence.

Bad root cause:

```text
Service crashed.
```

Better:

```text
New release added N+1 query on order list.
This increased DB query count per request from 3 to 503.
Hikari pool saturated.
Tomcat threads waited for DB connections.
Requests timed out.
```

## Signal

A signal is evidence.

Common signals:

```text
metrics
logs
traces
thread dumps
heap dumps
database statistics
query plans
deployment history
error rates
queue lag
connection pool metrics
```

Senior rule:

> **Never trust one signal alone during an incident. Correlate at least two or three.**

## Timeline

A timeline connects change and symptom.

```text
10:05 deploy started
10:08 new pods ready
10:10 p99 latency increased
10:12 Hikari pending threads increased
10:14 error rate increased
10:16 rollback started
10:20 latency recovered
```

Timeline is one of the strongest debugging tools.

---

## Internal Architecture of Failure Thinking

A request path has stages:

```text
Client
  |
  v
Load Balancer
  |
  v
Spring Boot Server
  |
  v
Filter Chain
  |
  v
Controller
  |
  v
Service
  |
  v
Thread Pool / Transaction
  |
  v
Repository
  |
  v
HikariCP
  |
  v
Database
  |
  v
External Services / Kafka / Redis
```

Production failure is usually a stage failure or a queue between stages.

```text
Tomcat queue
Hikari queue
Kafka lag
DB lock queue
thread pool queue
HTTP client connection pool
Redis latency
```

Debugging is locating the queue or stage where time/errors accumulate.

---

## The Production Failure Flow

## Step 1 — Detect

Ask:

```text
What exactly is wrong?
How many users?
Which endpoint?
Which region?
Since when?
What changed?
```

Do not say:

```text
The app is slow.
```

Say:

```text
POST /api/orders p99 increased from 250 ms to 8 s since 10:10 UTC.
HTTP 500 rate increased from 0.1% to 12%.
Only checkout endpoint affected.
```

Detection needs precision.

## Step 2 — Contain

Ask:

```text
Can we reduce impact before root cause is fully known?
```

Options:

```text
rollback latest deploy
disable feature flag
reduce traffic to faulty path
rate limit abusive clients
pause background job
stop consumer causing DB pressure
switch to fallback
increase replicas if CPU-bound
protect database
```

Containment goal:

```text
stop bleeding
preserve data correctness
buy investigation time
```

## Step 3 — Locate

Ask:

```text
Where is the failure happening?
```

Use stage map:

```text
Before app?
  load balancer/network

At server?
  Tomcat threads/accept queue

At filters?
  auth/CORS/rate limit

At controller?
  validation/body parsing

At service?
  business logic/downstream call

At repository?
  slow query/N+1

At pool?
  Hikari/thread pool exhaustion

At database?
  locks/CPU/index/migration

At async?
  Kafka lag/executor queue
```

## Step 4 — Explain

Build causal chain.

Example:

```text
New deploy added order.getProduct().getName() in a loop.
This triggered N+1 queries.
DB query count increased.
DB latency increased.
Hikari connections stayed active longer.
Pending connection requests rose.
Tomcat threads waited.
HTTP latency and 500s increased.
```

A causal chain is stronger than a guess.

## Step 5 — Fix

Choose safest fix.

Options:

```text
rollback
hotfix query
disable feature
reduce concurrency
add index
kill blocking query
pause job
increase pool temporarily
scale database
```

Safety rule:

```text
Prefer reversible fixes during incident.
Prefer simple changes over complex changes.
```

## Step 6 — Verify

After fix, check:

```text
error rate
p95/p99 latency
throughput
DB CPU
Hikari active/pending
thread pool queue
Kafka lag
business success rate
user reports
```

Do not declare victory because deploy finished.

Declare recovery when metrics prove recovery.

## Step 7 — Prevent

After incident:

```text
write timeline
identify root cause
add alert
add dashboard
add test
add guardrail
improve rollout
document runbook
remove sharp edge
```

Prevention is what turns pain into engineering maturity.

---

## Rich ASCII Diagram — Incident Loop

```text
Incident: checkout failing
        |
        v
+------------------+
| DETECT           |
| p99 8s, 500 12%  |
+--------+---------+
         |
         v
+------------------+
| CONTAIN          |
| rollback/flag    |
+--------+---------+
         |
         v
+------------------+
| LOCATE           |
| Hikari pending   |
| DB slow queries  |
+--------+---------+
         |
         v
+------------------+
| EXPLAIN          |
| N+1 caused DB    |
| pressure         |
+--------+---------+
         |
         v
+------------------+
| FIX              |
| join fetch/query |
+--------+---------+
         |
         v
+------------------+
| VERIFY           |
| p99 normal       |
| errors normal    |
+--------+---------+
         |
         v
+------------------+
| PREVENT          |
| test + alert     |
+------------------+
```

---

## Rich ASCII Diagram — Request Failure Localization

```text
Client
  |
  v
Load Balancer
  |
  v
Tomcat Thread Pool  <--- saturation?
  |
  v
Filter Chain       <--- auth/rate limit failure?
  |
  v
DispatcherServlet  <--- mapping/binding failure?
  |
  v
Controller         <--- wrong response?
  |
  v
Service            <--- business/downstream timeout?
  |
  v
Repository         <--- N+1/slow query?
  |
  v
HikariCP           <--- pending connections?
  |
  v
Database           <--- locks/CPU/index?
```

Debugging means putting the red X at the correct stage.

---

## Java/Spring Boot Example — Observable Request

Controller:

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final MeterRegistry meterRegistry;

    public OrderController(OrderService orderService,
                           MeterRegistry meterRegistry) {
        this.orderService = orderService;
        this.meterRegistry = meterRegistry;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            OrderResponse response = orderService.createOrder(request);
            meterRegistry.counter("orders.create.success").increment();
            return ResponseEntity.status(201).body(response);
        } catch (RuntimeException ex) {
            meterRegistry.counter("orders.create.failure").increment();
            throw ex;
        } finally {
            sample.stop(meterRegistry.timer("orders.create.latency"));
        }
    }
}
```

This is simplified.
In real systems, you often use filters, interceptors, AOP, or Micrometer instrumentation instead of manual metrics everywhere.

The goal:

```text
Make request success/failure/latency visible.
```

---

## Java/Spring Boot Example — Request ID Filter

```java
@Component
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = Optional.ofNullable(request.getHeader(HEADER))
                .orElse(UUID.randomUUID().toString());

        MDC.put("requestId", requestId);
        response.setHeader(HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("requestId");
        }
    }
}
```

Log pattern should include request ID.

Why this matters:

```text
During incident, one request can be traced across controller, service, repository logs.
```

Without correlation ID:

```text
logs become noise
```

---

## Java/Spring Boot Example — Safe Exception Mapping

```java
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("BUSINESS_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccess(DataAccessException ex) {
        return ResponseEntity.status(503)
                .body(new ErrorResponse("DATABASE_UNAVAILABLE", "Temporary database problem"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception ex) {
        return ResponseEntity.status(500)
                .body(new ErrorResponse("INTERNAL_ERROR", "Unexpected error"));
    }
}
```

```java
public record ErrorResponse(String code, String message) {}
```

Production principle:

```text
Expose useful error code.
Do not leak stack traces or secrets to clients.
Log full details internally.
```

---

## Step-by-Step Dry Run — Hikari Pool Exhaustion

Symptom:

```text
POST /api/orders timing out
```

Signals:

```text
Hikari pending connections high
Hikari active connections = max
DB CPU moderate
Thread dump shows many threads waiting for connection
```

Incident loop:

```text
Detect:
  checkout p99 8 seconds, 500 rate 10%

Contain:
  temporarily rate limit checkout
  pause non-critical background DB job

Locate:
  Hikari pool saturated

Explain:
  background report job started at same time
  report job opened long transactions
  connections held for minutes
  checkout requests could not borrow DB connections

Fix:
  stop report job
  move report job to read replica
  reduce report batch size
  add separate pool/job schedule

Verify:
  Hikari pending returns to 0
  checkout p99 returns to baseline
  error rate normal

Prevent:
  alert on Hikari pending
  report job timeout
  separate worker pool
  runbook update
```

---

## Step-by-Step Dry Run — Bad Deploy

Symptom:

```text
Error rate increased after deployment.
```

Timeline:

```text
10:05 deploy v42 started
10:08 v42 ready
10:10 500s increased
10:11 logs show NullPointerException in CheckoutMapper
```

Incident loop:

```text
Detect:
  500s on checkout after v42

Contain:
  rollback to v41

Locate:
  only pods running v42 show error

Explain:
  mapper expects non-null coupon field
  new request path passes null

Fix:
  rollback immediate
  code hotfix with null handling

Verify:
  v41 restored error rate
  v43 tested and canary clean

Prevent:
  add unit test for null coupon
  add canary alert
  improve mapper validation
```

Senior lesson:

```text
Recent change is not always root cause, but it is always a strong suspect.
```

---

## Step-by-Step Dry Run — Database Lock Contention

Symptom:

```text
Order update endpoint slow.
```

Signals:

```text
DB lock waits high
specific UPDATE query blocked
Hikari active high
CPU not high
```

Incident loop:

```text
Detect:
  order update p99 15s

Contain:
  stop bulk admin job updating same table
  reduce write traffic if needed

Locate:
  DB lock wait on orders table

Explain:
  admin job updates orders without batching
  long transaction locks many rows
  user updates wait behind it

Fix:
  kill blocking transaction
  rewrite job with small batches
  add index if missing
  run during low traffic

Verify:
  lock waits drop
  Hikari active drops
  latency normal

Prevent:
  lock wait alert
  batch job guidelines
  transaction timeout
```

---

## Step-by-Step Dry Run — Redis Cache Failure

Symptom:

```text
Catalog latency increased.
```

Signals:

```text
Redis timeout errors
DB read QPS increased 10x
cache hit ratio dropped
```

Incident loop:

```text
Detect:
  catalog p99 high, Redis errors

Contain:
  enable degraded response for catalog
  rate limit expensive endpoints
  protect DB

Locate:
  Redis unavailable, DB fallback overloaded

Explain:
  Redis cluster failover caused cache misses
  all traffic fell back to DB
  DB could not absorb full read load

Fix:
  restore Redis
  warm hot keys gradually
  add fallback protection

Verify:
  hit ratio recovers
  DB QPS normal
  catalog p99 normal

Prevent:
  cache outage runbook
  stampede protection
  DB fallback rate limit
  Redis alerting
```

---

## Production Scale Example

Large service dependencies:

```text
Spring Boot app
  |
  +-- PostgreSQL via HikariCP
  +-- Redis cache
  +-- Kafka
  +-- external payment API
  +-- S3
  +-- Kubernetes
```

Failure can propagate:

```text
Payment API slows
  -> service threads wait
  -> Tomcat thread pool fills
  -> users retry
  -> traffic increases
  -> DB transactions stay open
  -> Hikari pool saturates
  -> unrelated endpoints fail
```

Containment options:

```text
payment circuit breaker
timeout shorter than request timeout
bulkhead thread pool for payment calls
idempotency keys
outbox pattern
queue-based processing
```

Production failure thinking is about chains.

```text
Cause A creates pressure at B.
B creates queue at C.
C causes user-visible failure at D.
```

---

## Production Failure Story

An ecommerce company had intermittent checkout outages every Friday evening.

Symptoms:

```text
checkout p99 > 10s
Hikari pool exhausted
Postgres CPU high
Kafka lag high
```

Initial guess:

```text
Traffic spike.
Need more pods.
```

They scaled pods from 10 to 30.

Outage got worse.

Why?

```text
Each pod had Hikari maximumPoolSize=30.
10 pods = 300 possible DB connections.
30 pods = 900 possible DB connections.
Postgres safe capacity was around 350.
Scaling pods overloaded DB harder.
```

Actual root cause:

```text
Friday promotion created hot product reads.
One endpoint had N+1 query.
More pods amplified DB connection pressure.
Kafka consumers also wrote order events aggressively.
```

Final fix:

```text
Fix N+1 with projection query.
Reduce Hikari pool per pod.
Add Redis cache for hot catalog reads.
Add rate limit for promotion endpoint.
Tune Kafka consumer concurrency.
Add dashboard for:
  - Hikari active/pending
  - DB connections
  - query count per endpoint
  - p99 latency
```

Lesson:

> **Scaling the app can worsen incidents when the database is the bottleneck. Always identify the bottleneck before adding capacity.**

---

## Debugging Mindset

## The Five Questions

During incident, keep asking:

```text
1. What changed?
2. What is the user impact?
3. Where is the bottleneck?
4. What is the safest mitigation?
5. What evidence proves recovery?
```

## The Three Golden Signals for Backend APIs

```text
Latency:
  How slow?

Traffic:
  How much?

Errors:
  How many failed?
```

Add saturation:

```text
How full are critical resources?
  CPU
  memory
  thread pools
  Hikari pool
  DB connections
  queues
```

## Do Not Debug From Emotion

Bad:

```text
I think it is Redis.
Let's restart Redis.
```

Better:

```text
Redis p99 latency increased at 10:11.
Cache hit ratio dropped.
DB reads increased.
Catalog p99 increased.
Redis is likely part of causal chain.
```

Evidence first.

---

## Common Misconceptions

## Misconception 1 — “Root cause first, mitigation later”

During active user impact, mitigation often comes first.

You can do root cause after stabilizing.

## Misconception 2 — “Scaling pods fixes production incidents”

Only if app CPU/request capacity is bottleneck.

If DB, Redis, Kafka, or external API is bottleneck, scaling pods can worsen load.

## Misconception 3 — “Logs are enough”

Logs are not enough.

Use:

```text
metrics
traces
logs
thread dumps
DB views
deployment timeline
```

## Misconception 4 — “One error explains everything”

One error may be symptom, not cause.

Example:

```text
Hikari timeout is often symptom of slow DB query, lock, leak, or downstream-held transaction.
```

## Misconception 5 — “Restarting is a fix”

Restarting may mitigate.
It is not root cause unless the problem is known to be transient and understood.

## Misconception 6 — “Postmortem is blame”

Postmortem is learning.

Good postmortem asks:

```text
Why did system allow this?
Why did detection take time?
Why did blast radius grow?
What guardrail is missing?
```

Not:

```text
Who broke it?
```

---

## Performance Considerations

During performance incidents, separate:

```text
latency
throughput
saturation
errors
```

Example:

```text
Latency high but CPU low:
  likely waiting on DB/network/locks

CPU high:
  CPU-bound work, JSON, encryption, loops, GC

Memory high:
  leak, queue growth, large payloads, cache explosion

DB CPU high:
  slow queries, missing indexes, too many connections

DB lock wait high:
  transaction contention

Queue lag high:
  consumers too slow or downstream blocked
```

Use stage-based thinking:

```text
Where is time spent?
Where is queue growing?
Where is resource saturated?
```

---

## Scalability Considerations

Production failure playbooks must include scale behavior.

Questions:

```text
If traffic doubles, what breaks first?
If one dependency slows, what queues grow?
If Redis fails, can DB handle fallback?
If Kafka lags, how long to recover?
If pods scale up, can DB handle pool multiplication?
If one tenant abuses API, can we isolate?
```

Patterns:

```text
bulkheads
rate limits
timeouts
circuit breakers
idempotency
backpressure
graceful degradation
read replicas
cache protection
queue isolation
tenant isolation
```

Reliability is designed before incident.

---

## Failure Investigation Playbook

## Phase 1 — Triage

```text
[ ] What is the alert?
[ ] What endpoint/job/component?
[ ] Since when?
[ ] What changed?
[ ] What is user impact?
[ ] Is data correctness at risk?
```

## Phase 2 — Containment

```text
[ ] Rollback possible?
[ ] Feature flag off?
[ ] Rate limit?
[ ] Pause job/consumer?
[ ] Protect database?
[ ] Disable non-critical path?
[ ] Communicate status?
```

## Phase 3 — Localization

```text
[ ] Load balancer healthy?
[ ] App pods healthy?
[ ] Tomcat threads saturated?
[ ] Hikari pending?
[ ] DB slow queries?
[ ] DB locks?
[ ] Redis latency?
[ ] Kafka lag?
[ ] External API latency?
[ ] Recent deploy/config?
```

## Phase 4 — Causal Chain

Write:

```text
Because A happened,
B increased,
which caused C queue,
which caused D user impact.
```

Example:

```text
Because new endpoint introduced N+1,
DB query count increased,
which held Hikari connections longer,
which caused request threads to wait,
which increased checkout latency and 500s.
```

## Phase 5 — Fix

```text
[ ] Is fix reversible?
[ ] Is rollback safer?
[ ] Can we canary?
[ ] Can we reduce blast radius?
[ ] Do we need data repair?
[ ] Do we need customer communication?
```

## Phase 6 — Verify

```text
[ ] p95/p99 recovered
[ ] error rate recovered
[ ] saturation recovered
[ ] queues draining
[ ] business metrics recovered
[ ] no new errors introduced
```

## Phase 7 — Prevention

```text
[ ] Alert added
[ ] Dashboard added
[ ] Test added
[ ] Runbook updated
[ ] Limit/timeout added
[ ] Ownership clear
[ ] Postmortem completed
```

---

## Interview Q&A

### Q1. How do you approach a production incident?

Strong answer:

> I follow an incident loop: detect the exact symptom and impact, contain the blast radius, locate the failing stage using metrics/logs/traces, build an evidence-based causal chain, apply the safest fix or rollback, verify recovery through metrics, and then prevent recurrence with tests, alerts, dashboards, or design changes.

### Q2. What do you check first during high latency?

Strong answer:

> I check scope and timeline first: which endpoints, since when, and what changed. Then I inspect saturation signals like CPU, memory, Tomcat threads, Hikari active/pending, DB slow queries/locks, external API latency, and queue depth to locate where time is accumulating.

### Q3. When would you rollback?

Strong answer:

> If the incident started after a deploy and rollback is safe, I prefer rollback as a fast containment step. It reduces user impact while we continue root cause analysis. I avoid risky hotfixes during active incidents unless rollback is impossible.

### Q4. How do you debug Hikari connection timeouts?

Strong answer:

> I treat Hikari timeout as a symptom of pool exhaustion. I check active, idle, pending, and timeout metrics, then correlate with slow queries, DB locks, long transactions, connection leaks, recent traffic changes, and replica-count times pool-size capacity.

### Q5. How do you know an incident is resolved?

Strong answer:

> Not when the fix is deployed, but when metrics recover: latency, error rate, saturation, queue depth, business success rate, and dependency health return to normal and stay stable for a reasonable window.

### Q6. What makes a good postmortem?

Strong answer:

> A good postmortem is blameless, timeline-based, evidence-driven, and action-oriented. It explains impact, root cause, contributing factors, detection gaps, response gaps, and concrete prevention items with owners.

### Q7. What is blast radius?

Strong answer:

> Blast radius is the scope of impact: how many users, tenants, regions, endpoints, or systems are affected. Reducing blast radius through feature flags, rate limits, isolation, or rollback is one of the first goals during an incident.

---

## Production Checklist

```text
Before Incident
[ ] Dashboards exist for latency/errors/traffic/saturation
[ ] Hikari metrics monitored
[ ] DB slow query and lock monitoring exists
[ ] Kafka lag monitored
[ ] Redis latency/hit ratio monitored
[ ] External API latency monitored
[ ] Deploy timeline visible
[ ] Feature flags available
[ ] Rollback is tested

During Incident
[ ] Impact identified
[ ] Incident owner assigned
[ ] Timeline started
[ ] Mitigation considered early
[ ] Evidence collected
[ ] Changes communicated
[ ] Risky actions avoided
[ ] Recovery verified

After Incident
[ ] Postmortem written
[ ] Root cause explained
[ ] Action items assigned
[ ] Alerts improved
[ ] Tests added
[ ] Runbook updated
[ ] Guardrails added
```

---

## One-Page Cheat Sheet

```text
Production Failure Playbook
===========================

Core Loop
---------
Detect
  What is broken and who is affected?

Contain
  Reduce blast radius and user damage.

Locate
  Find failed stage or saturated queue.

Explain
  Build evidence-based causal chain.

Fix
  Apply safest reversible correction.

Verify
  Prove recovery with metrics.

Prevent
  Add guardrails to stop recurrence.

Golden Signals
--------------
Latency
Traffic
Errors
Saturation

Common Saturation Points
------------------------
CPU
Memory
Tomcat threads
Hikari pool
DB connections
DB locks
Thread pool queues
Kafka lag
Redis latency
External API latency

Best Incident Questions
-----------------------
What changed?
What is impact?
Where is bottleneck?
What is safest mitigation?
What proves recovery?

Best Sentence
-------------
First stop the bleeding,
then find root cause,
then prevent recurrence.
```

---

## Last-Minute Interview Revision

Do not say:

```text
I check logs and fix the bug.
```

Say:

```text
I handle production failures using a structured incident loop: detect impact, contain blast radius, locate the failing stage using metrics/logs/traces, explain the causal chain, apply a safe fix or rollback, verify recovery through metrics, and then prevent recurrence with tests, alerts, dashboards, and runbook updates.
```

Senior version:

```text
In production, correctness and containment come before curiosity. I stabilize the system first, then use evidence across metrics, logs, traces, thread dumps, and database signals to find the causal chain. I avoid guessing, prefer reversible fixes, and verify recovery before closing the incident.
```

---

## One Picture To Remember

```text
                 PRODUCTION INCIDENT LOOP

                       ALERT
                         |
                         v
             +-----------------------+
             | DETECT                |
             | exact symptom/impact  |
             +-----------+-----------+
                         |
                         v
             +-----------------------+
             | CONTAIN               |
             | reduce blast radius   |
             +-----------+-----------+
                         |
                         v
             +-----------------------+
             | LOCATE                |
             | failed stage/queue    |
             +-----------+-----------+
                         |
                         v
             +-----------------------+
             | EXPLAIN               |
             | causal chain          |
             +-----------+-----------+
                         |
                         v
             +-----------------------+
             | FIX                   |
             | safe/reversible       |
             +-----------+-----------+
                         |
                         v
             +-----------------------+
             | VERIFY                |
             | metrics recovered     |
             +-----------+-----------+
                         |
                         v
             +-----------------------+
             | PREVENT               |
             | guardrails/runbook    |
             +-----------------------+
```

Final retention sentence:

> **Production debugging is not guessing under pressure; it is a disciplined loop that stabilizes first, explains with evidence, fixes safely, and prevents recurrence.**
