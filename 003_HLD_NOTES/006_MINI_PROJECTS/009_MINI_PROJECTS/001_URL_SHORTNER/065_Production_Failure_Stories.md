# 065_Production_Failure_Stories.md
# MiniURLShortener — Production Failure Stories

> Core mental model: **Production failure stories are compressed engineering experience. A failure story teaches you what broke, why it broke, how users felt it, how engineers detected it, how they fixed it, and what guardrail prevents the same class of failure from returning.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. What Is A Production Failure Story?](#3-what-is-a-production-failure-story)
- [4. Failure Story Template](#4-failure-story-template)
- [5. MiniURLShortener Failure Surface](#5-miniurlshortener-failure-surface)
- [6. Severity And Blast Radius](#6-severity-and-blast-radius)
- [7. Story 1: Redirect Latency Spike Due To DB Connection Exhaustion](#7-story-1-redirect-latency-spike-due-to-db-connection-exhaustion)
- [8. Story 2: Redis Cache Outage Overloads PostgreSQL](#8-story-2-redis-cache-outage-overloads-postgresql)
- [9. Story 3: Hot Short Code Creates One-Key Meltdown](#9-story-3-hot-short-code-creates-one-key-meltdown)
- [10. Story 4: Kafka Consumer Lag Hides Analytics Loss](#10-story-4-kafka-consumer-lag-hides-analytics-loss)
- [11. Story 5: Duplicate Click Events Inflate Metrics](#11-story-5-duplicate-click-events-inflate-metrics)
- [12. Story 6: Bad Deployment Breaks Redirects](#12-story-6-bad-deployment-breaks-redirects)
- [13. Story 7: Database Migration Locks The Main Table](#13-story-7-database-migration-locks-the-main-table)
- [14. Story 8: Expired Links Return 500 Instead Of 410](#14-story-8-expired-links-return-500-instead-of-410)
- [15. Story 9: Missing Correlation ID Makes Debugging Slow](#15-story-9-missing-correlation-id-makes-debugging-slow)
- [16. Story 10: Rate Limiter Misconfiguration Blocks Real Users](#16-story-10-rate-limiter-misconfiguration-blocks-real-users)
- [17. Story 11: Thread Pool Saturation Freezes The API](#17-story-11-thread-pool-saturation-freezes-the-api)
- [18. Story 12: Read Replica Lag Serves Stale Redirect Data](#18-story-12-read-replica-lag-serves-stale-redirect-data)
- [19. Story 13: DNS Or Gateway Issue Looks Like App Failure](#19-story-13-dns-or-gateway-issue-looks-like-app-failure)
- [20. Story 14: S3/Log Pipeline Backpressure Fills Disk](#20-story-14-s3log-pipeline-backpressure-fills-disk)
- [21. Story 15: Security Blocklist Push Blocks Valid Campaign](#21-story-15-security-blocklist-push-blocks-valid-campaign)
- [22. Cross-Story Patterns](#22-cross-story-patterns)
- [23. Debugging Playbook](#23-debugging-playbook)
- [24. Incident Commander Mental Model](#24-incident-commander-mental-model)
- [25. Metrics To Check First](#25-metrics-to-check-first)
- [26. Logs To Check First](#26-logs-to-check-first)
- [27. Safe Rollback And Mitigation Patterns](#27-safe-rollback-and-mitigation-patterns)
- [28. Java/Spring Boot Guardrail Examples](#28-javaspring-boot-guardrail-examples)
- [29. Testing Failure Stories Before Production](#29-testing-failure-stories-before-production)
- [30. Common Mistakes](#30-common-mistakes)
- [31. Interview-Ready Explanation](#31-interview-ready-explanation)
- [32. Senior Engineer Checklist](#32-senior-engineer-checklist)
- [33. One-Page Cheat Sheet](#33-one-page-cheat-sheet)
- [34. One Picture To Remember](#34-one-picture-to-remember)
- [35. Final Retention Summary](#35-final-retention-summary)

---

## 1. Why This Exists

By now MiniURLShortener has many production-shaped pieces:

```text
Spring Boot API
PostgreSQL
Redis cache
Kafka analytics
Docker / Kubernetes
Ingress / Gateway
HikariCP
rate limiting
observability
SLO / SLA / error budget
load testing
performance tuning
```

But knowing components is not enough.

Production systems fail because components interact under pressure.

Example:

```text
Redis is down.
Redirect API falls back to PostgreSQL.
PostgreSQL connection pool fills.
Tomcat request threads wait.
Latency rises.
Clients retry.
Traffic doubles.
Database gets worse.
Entire API becomes slow.
```

This is not a Redis-only problem.

It is a cascade.

Production failure stories teach:

```text
1. What breaks first.
2. What breaks next.
3. What signal reveals the truth.
4. What immediate mitigation saves users.
5. What permanent guardrail prevents repeat failure.
```

One strong senior engineer skill:

```text
You can see the failure chain before it fully happens.
```

---

## 2. The One Core Mental Model

Production failures are rarely isolated bugs.

They are usually:

```text
small trigger + missing guardrail + high traffic = user-visible incident
```

ASCII:

```text
Small Trigger
    |
    v
+-------------------+
| Missing Guardrail |
+-------------------+
    |
    v
+-------------------+
| Load Amplification|
+-------------------+
    |
    v
+-------------------+
| Cascading Failure |
+-------------------+
    |
    v
User Pain + Pager + Incident
```

Example:

```text
Trigger:
    Redis timeout increases.

Missing guardrail:
    No short timeout, no circuit breaker, no stale cache fallback.

Amplification:
    Every redirect request hits DB.

Cascade:
    DB pool exhausted, API threads blocked, p99 explodes.
```

Memory sentence:

```text
A production failure story is a movie: trigger, cascade, detection, mitigation, prevention.
```

---

## 3. What Is A Production Failure Story?

A good production failure story is not gossip.

It is a structured engineering lesson.

Bad story:

```text
Redis went down and everything failed.
```

Good story:

```text
At 09:10 UTC Redis latency increased from 2ms to 400ms. The redirect service had a 1s Redis timeout and no circuit breaker, so Tomcat threads waited. Cache misses and Redis timeouts caused DB fallback. PostgreSQL active connections reached max pool size, p99 moved from 80ms to 5s, and clients retried. We mitigated by disabling DB fallback for known hot keys, lowering Redis timeout, and scaling API pods. Permanent fixes were cache circuit breaker, stale cache serving, bulkhead isolation, and dashboards for Redis timeout rate and DB pool saturation.
```

A good story contains:

```text
symptom
impact
trigger
root cause
blast radius
detection signal
mitigation
permanent fix
lesson
```

---

## 4. Failure Story Template

Use this template for every incident.

```text
Failure story template:

1. What users saw
2. What dashboards showed
3. What changed recently
4. What actually broke
5. Why existing guardrails failed
6. Immediate mitigation
7. Permanent fix
8. Tests/alerts added
9. Lesson learned
```

ASCII:

```text
User Symptom
    |
    v
Dashboard Signal
    |
    v
Recent Change?
    |
    v
Root Cause
    |
    v
Mitigation
    |
    v
Permanent Guardrail
    |
    v
Learning
```

This is the same style you should use in interviews.

Interviewers like candidates who can say:

```text
I would first reduce blast radius, then debug root cause.
```

Not:

```text
I will immediately SSH into the server and guess.
```

---

## 5. MiniURLShortener Failure Surface

MiniURLShortener looks small, but production surface is large.

```text
Client
  |
  v
CDN / DNS / Load Balancer / Ingress
  |
  v
Spring Boot API pods
  |
  +--> Redis cache
  |
  +--> PostgreSQL primary
  |
  +--> PostgreSQL read replica
  |
  +--> Kafka click topic
  |
  +--> analytics worker
  |
  +--> logs / metrics / traces
```

ASCII:

```text
                         +------------------+
                         |  Observability   |
                         | logs metrics     |
                         | traces alerts    |
                         +---------^--------+
                                   |
Client -> CDN -> Ingress -> API Pods --------> Kafka -> Workers
                             |   |
                             |   +----------> Redis
                             |
                             +--------------> PostgreSQL
                                             / Primary
                                             / Replica
```

Failure can happen in:

```text
1. CDN/DNS/Ingress routing
2. API thread pools
3. Hikari connection pool
4. Redis cache
5. PostgreSQL locks / slow queries / failover
6. Kafka broker or consumer lag
7. deployment config
8. rate limiter
9. security blocklist
10. logging/metrics/tracing pipeline
```

Senior mindset:

```text
Do not debug only code. Debug the whole request path.
```

---

## 6. Severity And Blast Radius

Severity means user/business impact.

Blast radius means how much of the system is affected.

```text
SEV1:
    Redirect API mostly down.
    Revenue/campaign links broken.

SEV2:
    Create API down, redirects still work.
    New links cannot be created.

SEV3:
    Analytics delayed, user path still works.

SEV4:
    Internal dashboard wrong, no user impact.
```

ASCII:

```text
+----------------------+------------------------+----------------------+
| Failure              | User Impact            | Typical Severity     |
+----------------------+------------------------+----------------------+
| Redirect down         | Links do not open      | SEV1                 |
| Redirect slow         | Users abandon          | SEV1/SEV2            |
| Create API down       | Cannot create links    | SEV2                 |
| Analytics delayed     | Reports stale          | SEV3                 |
| Admin UI broken       | Internal inconvenience | SEV3/SEV4            |
+----------------------+------------------------+----------------------+
```

Important:

```text
For URL shortener, redirect path is more critical than create path.
```

Why?

```text
Redirect is read-heavy and user-facing for every click.
Create is lower volume and less frequent.
```

Production prioritization:

```text
Protect redirect first.
Protect database second.
Protect analytics after user path.
```

---

## 7. Story 1: Redirect Latency Spike Due To DB Connection Exhaustion

### What users saw

Users clicked short links and waited 3-8 seconds.

Some requests timed out.

```text
GET /abc123 -> slow
GET /sale99 -> timeout
```

### Dashboard signal

```text
p50 latency: 40ms -> 90ms
p95 latency: 120ms -> 2s
p99 latency: 250ms -> 8s
HTTP 5xx: rising
Hikari active connections: maxed
Hikari pending threads: rising
PostgreSQL CPU: high
```

ASCII:

```text
Traffic spike
    |
    v
Redirect API
    |
    v
Hikari pool full
    |
    v
Tomcat threads waiting
    |
    v
p99 latency explosion
    |
    v
client retries
    |
    v
more traffic
```

### Root cause

A new deployment changed redirect lookup from cached path to DB-first path.

Before:

```text
API -> Redis -> DB only on cache miss
```

After bug:

```text
API -> DB -> Redis update
```

At high traffic, every redirect hit PostgreSQL.

### Why it became severe

```text
1. Redirect is high QPS.
2. PostgreSQL was sized assuming high cache hit ratio.
3. Hikari max pool was reached.
4. Requests waited for DB connections.
5. Clients retried slow requests.
```

### Immediate mitigation

```text
1. Roll back deployment.
2. Temporarily increase API pods only if DB can handle it.
3. Reduce client retry pressure if controlled clients exist.
4. Enable cache-first logic.
5. Protect DB with shorter query timeout.
```

### Permanent fix

```text
1. Add integration test: redirect must check Redis before DB.
2. Add dashboard for Redis hit ratio.
3. Alert on Hikari pending threads > 0 for sustained period.
4. Add load test gate before production.
5. Add feature flag for redirect lookup strategy.
```

### Lesson

```text
A small change in read path order can multiply DB load by 100x.
```

---

## 8. Story 2: Redis Cache Outage Overloads PostgreSQL

### What users saw

Redirects became slow during Redis incident.

Some links still worked, but latency became unstable.

### Normal flow

```text
GET /abc123
   |
   v
Redis hit -> return longUrl fast
```

### Failure flow

```text
GET /abc123
   |
   v
Redis timeout
   |
   v
DB fallback
   |
   v
DB overload
```

ASCII:

```text
                 Redis Healthy
                     |
                     v
Client -> API -> Redis HIT -> 302 fast

                 Redis Unhealthy
                     |
                     v
Client -> API -> Redis timeout -> DB fallback -> DB overload
```

### Root cause

Redis latency increased due to cluster node issue.

The app had:

```text
Redis timeout = 1 second
no circuit breaker
no fallback limit
no stale cache layer
```

So every request waited too long before falling back.

### Detection signal

```text
redis_command_latency_p99 high
redis_timeout_count high
cache_hit_ratio dropping
postgres_connections rising
redirect_p99 rising
```

### Immediate mitigation

```text
1. Lower Redis timeout.
2. Temporarily disable Redis calls if timeout rate is high.
3. Serve only DB for low traffic while protecting DB with rate limits.
4. Increase Redis capacity or fail over cluster node.
5. Protect hot links with local in-memory cache.
```

### Permanent fix

```text
1. Redis circuit breaker.
2. Cache bulkhead separate from DB bulkhead.
3. Local Caffeine cache for hottest short codes.
4. Stale cache serving where acceptable.
5. Alert on Redis timeout rate, not only Redis availability.
```

### Java guardrail idea

```java
// Mental model only: keep Redis timeout small for redirect path.
// A slow cache is often worse than a missed cache.
```

### Lesson

```text
Cache is not only an optimization. Cache failure mode must be designed.
```

---

## 9. Story 3: Hot Short Code Creates One-Key Meltdown

### What users saw

One viral marketing link became slow.

Other links were mostly fine at first, then the system degraded.

### Trigger

A celebrity shared:

```text
https://sho.rt/bigdeal
```

Traffic jumped from:

```text
500 RPS total
```

to:

```text
35,000 RPS for one short code
```

ASCII:

```text
             Normal Traffic
abc111  ---> small load
xyz222  ---> small load
sale99  ---> small load

             Viral Traffic
bigdeal ---> huge load huge load huge load huge load
```

### Root cause

The system cached `bigdeal`, but analytics was written synchronously before redirect response.

Flow:

```text
GET /bigdeal
  -> Redis lookup
  -> Kafka publish click event
  -> wait for publish ack
  -> 302 redirect
```

Kafka publish latency increased under load.

Redirect latency increased.

### Detection signal

```text
one shortCode dominates traffic
Kafka publish latency high
redirect p99 high only for one route/key
API CPU high
```

### Immediate mitigation

```text
1. Make analytics fire-and-forget or buffer safely.
2. Temporarily sample click events for hot key.
3. Bypass expensive per-click processing.
4. Add local cache for hot short code.
5. Keep redirect path minimal.
```

### Permanent fix

```text
1. Never block redirect on analytics success.
2. Add async queue with bounded buffer.
3. Add hot-key detection.
4. Add per-key metrics.
5. Add click event sampling for extreme campaigns.
6. Use batch analytics aggregation.
```

### Lesson

```text
The redirect path must be brutally small. Anything non-essential must be async.
```

---

## 10. Story 4: Kafka Consumer Lag Hides Analytics Loss

### What users saw

Redirects worked.

Dashboard showed stale click counts.

Marketing team complained:

```text
Campaign clicks are not updating.
```

### Flow

```text
API publishes click events -> Kafka -> analytics worker -> DB/reporting table
```

ASCII:

```text
Redirect API
    |
    v
Kafka topic: click-events
    |
    v
Consumer group: analytics-workers
    |
    v
Click summary table
```

### Root cause

Analytics worker processed messages slower than producer rate.

Consumer lag grew.

```text
producer rate: 20,000 events/sec
consumer rate: 7,000 events/sec
lag: grows forever
```

### Why it was missed

The API was healthy.

Kafka broker was healthy.

Only consumer lag was unhealthy.

### Detection signal

```text
consumer_group_lag increasing
oldest_unprocessed_event_age increasing
worker CPU high
worker DB writes slow
analytics freshness SLA breached
```

### Immediate mitigation

```text
1. Scale analytics worker replicas if partitions allow.
2. Increase Kafka partitions if needed with careful planning.
3. Batch DB writes.
4. Temporarily reduce analytics detail.
5. Prioritize recent events if dashboard freshness matters.
```

### Permanent fix

```text
1. Alert on lag age, not only lag count.
2. Batch aggregation.
3. Idempotent click processing.
4. Partition by shortCode or campaign carefully.
5. Separate real-time counters from deep analytics.
```

### Lesson

```text
A queue makes failure delayed, not impossible.
```

---

## 11. Story 5: Duplicate Click Events Inflate Metrics

### What users saw

Users did not see failure.

Business saw impossible analytics:

```text
Clicks doubled after retry storm.
```

### Trigger

Kafka producer retried after timeout.

The broker had actually received the first event, but producer did not receive ack in time.

Producer sent again.

### Failure shape

```text
Click happened once.
Event stored twice.
Dashboard counted two clicks.
```

ASCII:

```text
API sends event #123
    |
    v
Kafka stores event
    |
    x ack timeout
    |
    v
API retries event #123
    |
    v
Kafka stores duplicate
```

### Root cause

No event idempotency.

Click event had no stable `eventId`.

Analytics worker used blind insert/increment.

### Immediate mitigation

```text
1. Stop retry storm if active.
2. Recompute analytics for affected window.
3. Deduplicate using approximate fields if possible.
4. Communicate dashboard correction.
```

### Permanent fix

```text
1. Generate clickEventId.
2. Use idempotent producer settings.
3. Store processed event IDs for dedup window.
4. Use unique constraint for exact-once effect where possible.
5. Design analytics as at-least-once input, idempotent output.
```

### Example event

```json
{
  "eventId": "01JABCCLICK123",
  "shortCode": "abc123",
  "occurredAt": "2026-06-25T08:00:00Z",
  "userAgentHash": "...",
  "ipHash": "..."
}
```

### Lesson

```text
Kafka gives durable delivery. Your consumer must still handle duplicates.
```

---

## 12. Story 6: Bad Deployment Breaks Redirects

### What users saw

After deployment, many redirects returned 500.

Create API still worked.

### Recent change

A new release changed URL status enum:

```text
ACTIVE
BLOCKED
DELETED
```

to:

```text
ENABLED
BLOCKED
DELETED
```

But old database rows still had `ACTIVE`.

### Failure

Application failed to deserialize status.

```text
DB row status = ACTIVE
Java enum expects ENABLED
exception -> 500
```

ASCII:

```text
Old DB Data             New App Code
-----------             ------------
ACTIVE       ----x----> ENABLED expected
```

### Root cause

Backward-incompatible enum change.

No migration compatibility window.

### Immediate mitigation

```text
1. Roll back application.
2. Restore old enum compatibility.
3. Avoid DB mass update during incident unless necessary.
4. Verify redirect p99 and error rate after rollback.
```

### Permanent fix

```text
1. Expand-contract migration pattern.
2. Backward-compatible enum mapping.
3. Canary deployment.
4. Contract tests against production-like data.
5. Deployment checklist for schema/data compatibility.
```

### Expand-contract model

```text
Step 1: new app accepts ACTIVE and ENABLED
Step 2: migrate old data gradually
Step 3: verify no ACTIVE remains
Step 4: remove ACTIVE support later
```

### Lesson

```text
Code deploy and data migration must be compatible across time.
```

---

## 13. Story 7: Database Migration Locks The Main Table

### What users saw

Create API timed out.

Redirects that missed cache also timed out.

### Migration

```sql
ALTER TABLE short_urls ADD COLUMN campaign_id VARCHAR(64) NOT NULL DEFAULT 'default';
```

On a large table, migration caused heavy locking/rewrite.

### Failure chain

```text
Migration starts
  |
  v
table lock or heavy rewrite
  |
  v
API inserts/reads wait
  |
  v
Hikari pool fills
  |
  v
requests timeout
```

ASCII:

```text
Migration session
      |
      v
 short_urls locked/heavy rewrite
      |
      +--> create requests wait
      +--> cache miss redirects wait
      +--> DB pool fills
```

### Root cause

Unsafe migration on hot table.

### Immediate mitigation

```text
1. Stop migration if safe.
2. Kill blocking session if needed.
3. Restore DB availability.
4. Keep app stable.
5. Re-run migration using safe pattern later.
```

### Permanent fix

Safe migration pattern:

```text
1. Add nullable column without default.
2. Backfill in small batches.
3. Add default for new rows.
4. Add NOT NULL constraint later using safe validation if supported.
5. Monitor locks and query latency during migration.
```

### Lesson

```text
Schema migration is production code. Treat it like a risky deploy.
```

---

## 14. Story 8: Expired Links Return 500 Instead Of 410

### What users saw

Expired campaign links returned:

```http
500 Internal Server Error
```

Instead of:

```http
410 Gone
```

### Root cause

Service returned `null` when link expired.

Controller expected a valid response object.

```java
RedirectResult result = service.resolve(shortCode);
return ResponseEntity.status(302)
        .location(URI.create(result.longUrl()))
        .build();
```

When `result` was null:

```text
NullPointerException -> 500
```

### Correct model

Expired is a known business state.

```text
expired link -> ShortCodeExpiredException -> 410 Gone
```

ASCII:

```text
DB row exists
   |
   v
expiresAt < now
   |
   +-- wrong -> return null -> NPE -> 500
   |
   +-- right -> throw domain exception -> 410
```

### Immediate mitigation

```text
1. Patch service to throw ShortCodeExpiredException.
2. Add global handler mapping.
3. Add regression test.
```

### Permanent fix

```text
1. Avoid null for domain states.
2. Use explicit domain exceptions or result types.
3. Test all lifecycle states: active, blocked, deleted, expired, missing.
```

### Lesson

```text
Known business states should never accidentally become 500s.
```

---

## 15. Story 9: Missing Correlation ID Makes Debugging Slow

### What users saw

Intermittent 500s.

Support had only this:

```text
User says link abc123 failed around morning.
```

### Engineer problem

Logs existed, but there was no common request identifier.

```text
Ingress log has one timestamp.
App log has another.
DB slow query has another.
Kafka publish log has another.
```

No easy way to connect them.

ASCII:

```text
Request
  |
  +--> Ingress log: no shared id
  +--> App log: no shared id
  +--> DB log: no shared id
  +--> Kafka log: no shared id

Debugging = guessing by timestamp
```

### Root cause

No correlation ID propagation.

### Immediate mitigation

```text
1. Search logs by shortCode and timestamp.
2. Narrow by pod, path, status code.
3. Add temporary structured logs if safe.
```

### Permanent fix

```text
1. Generate or accept X-Correlation-Id.
2. Store in MDC.
3. Add to logs.
4. Return in response header.
5. Propagate to downstream calls and Kafka events.
```

### Spring Boot filter sketch

```java
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Correlation-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String correlationId = request.getHeader(HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);
        response.setHeader(HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("correlationId");
        }
    }
}
```

### Lesson

```text
Without correlation ID, every incident becomes slower and more expensive.
```

---

## 16. Story 10: Rate Limiter Misconfiguration Blocks Real Users

### What users saw

Many valid users received:

```http
429 Too Many Requests
```

during a marketing campaign.

### Root cause

Rate limiter key used only source IP.

Many real users came through the same corporate NAT or mobile carrier IP.

```text
1000 users -> same public IP -> rate limiter thinks one user is abusive
```

ASCII:

```text
User A --\
User B ---\
User C ----> Carrier NAT IP -> API -> rate limiter key = IP
User D ---/                          |
User E --/                           v
                                 false 429
```

### Immediate mitigation

```text
1. Increase temporary limit for affected campaign.
2. Whitelist trusted campaign path carefully.
3. Switch key to user/API key where available.
4. Monitor abuse separately.
```

### Permanent fix

```text
1. Use layered limits: IP + account + API key + shortCode.
2. Different limits for create and redirect path.
3. Separate bot protection from normal user throttling.
4. Add dashboards for 429 by country, ASN, endpoint, shortCode.
5. Add dry-run mode before enforcing new limiter.
```

### Lesson

```text
Rate limiting protects systems, but bad keys punish good users.
```

---

## 17. Story 11: Thread Pool Saturation Freezes The API

### What users saw

API seemed down even though CPU was not 100%.

Requests waited and timed out.

### Root cause

Tomcat worker threads were blocked on slow downstream calls.

```text
max threads = 200
200 requests waiting on Redis/DB
new requests cannot be served
```

ASCII:

```text
Tomcat Thread Pool
+------------------------------------------------+
| T1 waiting DB                                  |
| T2 waiting Redis                               |
| T3 waiting DB                                  |
| ...                                            |
| T200 waiting Redis                             |
+------------------------------------------------+

New request -> waits outside -> timeout
```

### Detection signal

```text
Tomcat busy threads near max
request queue rising
CPU moderate
Redis/DB latency high
p99 high
```

### Immediate mitigation

```text
1. Reduce downstream timeout.
2. Enable circuit breaker.
3. Shed load for non-critical endpoints.
4. Scale pods only if downstream can handle it.
5. Protect redirect path from analytics path.
```

### Permanent fix

```text
1. Bulkhead isolation.
2. Strict timeouts.
3. Circuit breakers.
4. Separate executor pools for async work.
5. Backpressure instead of unlimited waiting.
```

### Lesson

```text
A server can be dead because threads are waiting, not because CPU is high.
```

---

## 18. Story 12: Read Replica Lag Serves Stale Redirect Data

### What users saw

A newly created short link returned 404 for some seconds.

Create API returned success:

```json
{
  "shortCode": "new99"
}
```

But redirect returned:

```http
404 Not Found
```

### Root cause

Redirect lookup used read replica.

Replica lag was 8 seconds.

```text
Create writes to primary.
Redirect reads from replica.
Replica has not received row yet.
```

ASCII:

```text
POST /urls
   |
   v
Primary DB: row exists
   |
   | replication lag
   v
Replica DB: row not yet visible
   |
GET /new99 -> reads replica -> 404
```

### Immediate mitigation

```text
1. Read newly created shortCode from primary for short consistency window.
2. Route redirect misses to primary as second check.
3. Temporarily reduce replica usage if lag is high.
```

### Permanent fix

```text
1. Monitor replica lag.
2. Use read-your-write strategy for newly created links.
3. On replica miss, fallback to primary before returning 404.
4. Cache created shortCode immediately.
5. Alert when lag exceeds redirect freshness SLO.
```

### Lesson

```text
Read replicas improve scale but introduce consistency delay.
```

---

## 19. Story 13: DNS Or Gateway Issue Looks Like App Failure

### What users saw

Some regions could not access short links.

Other regions worked.

### App metrics

API pods looked healthy.

```text
CPU normal
DB normal
Redis normal
5xx normal
```

But external checks failed.

### Root cause

DNS or edge routing issue.

Traffic from one region was routed to unhealthy ingress/LB path.

ASCII:

```text
Region A users -> healthy edge -> API works
Region B users -> bad edge     -> timeout
Region C users -> healthy edge -> API works
```

### Detection signal

```text
synthetic checks fail from one region
app internal metrics normal
LB 5xx or connection errors high
DNS changes recent
```

### Immediate mitigation

```text
1. Route traffic away from bad edge/region.
2. Roll back DNS/LB config if changed.
3. Communicate regional impact.
4. Verify from external probes, not only internal metrics.
```

### Permanent fix

```text
1. Multi-region synthetic monitoring.
2. DNS/LB config change review.
3. Health checks from outside cluster.
4. Runbook for edge routing incidents.
```

### Lesson

```text
If users cannot reach you, your app metrics may still look perfect.
```

---

## 20. Story 14: S3/Log Pipeline Backpressure Fills Disk

### What users saw

API pods restarted repeatedly.

Some requests failed during restarts.

### Root cause

Log exporter could not ship logs to external storage.

Local log files accumulated.

Disk filled.

Kubernetes evicted pods.

ASCII:

```text
App writes logs
    |
    v
Log agent cannot ship
    |
    v
local disk fills
    |
    v
pod eviction/restart
    |
    v
request failures
```

### Why it happened

```text
1. Error storm generated huge logs.
2. Log shipping was slow/unavailable.
3. No log rate limiting.
4. Disk usage alert was missing.
```

### Immediate mitigation

```text
1. Reduce noisy logging.
2. Restore log shipping.
3. Increase disk temporarily if needed.
4. Restart healthy pods carefully.
5. Avoid debug-level logs in production.
```

### Permanent fix

```text
1. Log sampling for repeated errors.
2. Disk usage alerts.
3. Log volume dashboards.
4. Separate app health from logging backend health.
5. Avoid logging full payloads repeatedly.
```

### Lesson

```text
Observability tools can become part of the failure if not bounded.
```

---

## 21. Story 15: Security Blocklist Push Blocks Valid Campaign

### What users saw

A valid marketing campaign link returned:

```http
403 Forbidden
```

### Root cause

Security blocklist update had an overly broad pattern.

```text
blocked pattern: promo
```

It matched:

```text
/summer-promo
/promo2026
```

### Failure chain

```text
blocklist update
  |
  v
valid shortCode marked blocked
  |
  v
redirect returns 403
  |
  v
campaign loses traffic
```

ASCII:

```text
Blocklist Rule
    |
    +--> bad.com/promo-malware     should block
    +--> sho.rt/summer-promo       should not block but blocked
```

### Immediate mitigation

```text
1. Roll back blocklist version.
2. Unblock affected short codes.
3. Verify campaign links.
4. Review audit log of changed rules.
```

### Permanent fix

```text
1. Blocklist dry-run mode.
2. Rule impact preview before activation.
3. Approval workflow for broad rules.
4. Canary blocklist rollout.
5. Audit trail for all security changes.
```

### Lesson

```text
Security controls need safety controls too.
```

---

## 22. Cross-Story Patterns

After many incidents, patterns repeat.

```text
Pattern 1: Slow dependency causes thread exhaustion.
Pattern 2: Cache failure causes DB overload.
Pattern 3: Queue hides failure until lag becomes huge.
Pattern 4: Duplicate delivery breaks non-idempotent consumers.
Pattern 5: Bad deploy breaks data compatibility.
Pattern 6: Migration locks hot table.
Pattern 7: Missing correlation ID slows debugging.
Pattern 8: Wrong rate limiter key blocks real users.
Pattern 9: Replica lag causes stale reads.
Pattern 10: Observability pipeline becomes failure source.
```

ASCII:

```text
Most production failures belong to these families:

Dependency Slow/Down
        |
        v
Timeout / Threads / Pool / Retry
        |
        v
Cascading Failure

Data Change
        |
        v
Compatibility / Migration / Staleness
        |
        v
Wrong Response

Async Pipeline
        |
        v
Lag / Duplicate / Loss
        |
        v
Incorrect Analytics
```

Senior memory:

```text
Every incident should produce one reusable pattern, not only one local fix.
```

---

## 23. Debugging Playbook

When production is burning, do not randomly inspect code.

Use this order.

### Step 1: Confirm user impact

```text
Which endpoint?
Which region?
Which users?
Which status codes?
Since when?
```

### Step 2: Check golden signals

```text
Latency
Traffic
Errors
Saturation
```

### Step 3: Ask what changed

```text
deployment
config
DB migration
feature flag
traffic spike
dependency issue
certificate/DNS/LB change
```

### Step 4: Reduce blast radius

```text
rollback
disable feature flag
shed non-critical load
route traffic away
scale safe layer
```

### Step 5: Find root cause

```text
logs
metrics
traces
DB locks
pool saturation
Kafka lag
Redis timeout
```

### Step 6: Add guardrail

```text
alert
test
circuit breaker
limit
idempotency
migration rule
runbook
```

ASCII:

```text
Impact -> Signals -> Recent Change -> Mitigate -> Root Cause -> Guardrail
```

Rule:

```text
Mitigation first during SEV1. Perfect explanation can wait.
```

---

## 24. Incident Commander Mental Model

During serious incidents, one person should coordinate.

Incident commander does not need to fix every line of code.

They manage:

```text
1. impact understanding
2. roles
3. timeline
4. decisions
5. communication
6. mitigation priority
7. postmortem follow-up
```

ASCII:

```text
                 Incident Commander
                         |
      +------------------+------------------+
      |                  |                  |
      v                  v                  v
  Debug Lead       Comms Lead        Ops/Deploy Lead
      |                  |                  |
      v                  v                  v
find cause       update stakeholders   rollback/mitigate
```

Good incident behavior:

```text
clear owner
short updates
no blame
facts over guesses
one change at a time when possible
record timeline
```

Bad incident behavior:

```text
everyone changing things
no timeline
no customer impact estimate
no rollback owner
argument during outage
```

---

## 25. Metrics To Check First

For MiniURLShortener, check these first.

### API metrics

```text
http_server_requests_seconds p50/p95/p99
request rate by endpoint
5xx rate
4xx rate
Tomcat busy threads
JVM CPU
JVM memory
GC pause
```

### DB metrics

```text
Hikari active connections
Hikari pending threads
DB CPU
slow queries
locks
replica lag
connection count
```

### Redis metrics

```text
cache hit ratio
Redis command latency
Redis timeout count
evictions
memory usage
connection errors
```

### Kafka metrics

```text
producer error rate
producer latency
consumer lag
oldest event age
DLQ count
consumer processing rate
```

### Kubernetes metrics

```text
pod restarts
OOMKilled
evictions
CPU throttling
readiness probe failures
HPA scaling events
```

ASCII dashboard map:

```text
User pain?
   |
   +--> API latency/errors
   |
   +--> API saturation
   |
   +--> DB pool/locks
   |
   +--> Redis latency/hit ratio
   |
   +--> Kafka lag
   |
   +--> Pod restarts
```

---

## 26. Logs To Check First

Useful log fields:

```text
correlationId
method
path
status
latencyMs
shortCode
errorCode
exceptionClass
podName
traceId
```

For redirect failures:

```text
shortCode
cacheHit true/false
lookupSource REDIS/DB/REPLICA
redirectDecision ACTIVE/BLOCKED/EXPIRED/MISSING
```

For create failures:

```text
customAliasPresent
validationError
DB constraint name
generationRetryCount
```

For Kafka analytics:

```text
eventId
shortCode
partition
offset
publishLatencyMs
consumerLag
processingResult
```

Bad logs:

```text
Something failed
Exception occurred
NullPointerException
```

Good logs:

```text
correlationId=abc method=GET path=/sale99 status=410 errorCode=SHORT_CODE_EXPIRED latencyMs=18 shortCode=sale99
```

Important privacy rule:

```text
Do not log full long URLs with tokens or secrets.
```

---

## 27. Safe Rollback And Mitigation Patterns

Production mitigation options:

```text
1. rollback deployment
2. disable feature flag
3. reduce timeout
4. open circuit breaker
5. shed non-critical load
6. route to primary DB
7. bypass analytics
8. scale workers
9. pause unsafe migration
10. revert configuration
```

ASCII:

```text
Incident
   |
   v
Can rollback fix it?
   | yes
   v
Rollback

If not:
   |
   v
Can feature flag disable it?
   | yes
   v
Disable flag

If dependency issue:
   |
   v
Timeout / circuit breaker / fallback / load shed
```

Rollback rules:

```text
1. Rollback should be practiced before incident.
2. Rollback should not depend on tribal knowledge.
3. Rollback should be compatible with DB schema.
4. Rollback should be faster than patching during SEV1.
```

Mitigation rule:

```text
During outage, prefer the safest reversible action.
```

---

## 28. Java/Spring Boot Guardrail Examples

### 28.1 Hikari timeout guardrail

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 30
      connection-timeout: 300
      validation-timeout: 200
      leak-detection-threshold: 2000
```

Mental model:

```text
Do not let requests wait forever for DB connections.
Fast failure is often safer than thread starvation.
```

### 28.2 Rest/Redis timeout mindset

```text
Redirect path timeout should be short.
A slow cache should not freeze request threads.
```

Pseudo-config:

```yaml
app:
  cache:
    connect-timeout-ms: 100
    command-timeout-ms: 150
```

### 28.3 Circuit breaker for dependency

```java
// Conceptual service boundary
public LongUrlResult resolve(String shortCode) {
    Optional<LongUrlResult> cached = cacheClient.get(shortCode);
    if (cached.isPresent()) {
        return cached.get();
    }
    return databaseLookup(shortCode);
}
```

Guardrail idea:

```text
If Redis timeout rate is high, stop waiting on Redis for every request.
Open circuit and protect API threads.
```

### 28.4 Idempotent Kafka consumer

```java
@Transactional
public void processClick(ClickEvent event) {
    if (processedEventRepository.existsByEventId(event.eventId())) {
        return;
    }

    clickSummaryRepository.increment(event.shortCode(), event.occurredAt());
    processedEventRepository.save(new ProcessedEvent(event.eventId()));
}
```

Mental model:

```text
At-least-once delivery requires idempotent processing.
```

### 28.5 Safe error response

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
    log.error("Unexpected error path={} correlationId={}",
            request.getRequestURI(), MDC.get("correlationId"), ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiErrorResponse.internalError(request.getRequestURI()));
}
```

Mental model:

```text
Client gets safe message. Logs get full stack trace.
```

---

## 29. Testing Failure Stories Before Production

You can test many failures before real users suffer.

### Load test scenarios

```text
1. Redis unavailable during redirect load.
2. PostgreSQL slow query during redirect load.
3. Kafka broker slow during hot shortCode traffic.
4. Consumer lag under analytics burst.
5. API deployment while traffic continues.
6. DB migration on large table clone.
7. Rate limiter under shared IP traffic.
```

### Chaos-style tests

```text
kill Redis pod
restart Kafka broker
add DB latency
limit DB connections
kill API pod during traffic
simulate replica lag
fill disk in staging log volume
```

### Acceptance criteria

```text
Redirect p99 remains within SLO where possible.
DB does not collapse.
API fails fast instead of hanging.
Analytics can lag without breaking redirect.
Errors are correct and safe.
Alerts fire before users complain.
```

ASCII:

```text
Failure Injection
      |
      v
Observe Metrics
      |
      v
Check SLO
      |
      v
Check Alerts
      |
      v
Improve Guardrails
```

Rule:

```text
A failure story becomes mature when you can reproduce it safely in staging.
```

---

## 30. Common Mistakes

### Mistake 1: Debugging root cause before reducing impact

Wrong:

```text
Let's fully understand everything before rollback.
```

Correct:

```text
If users are heavily impacted, mitigate first.
```

### Mistake 2: Scaling the wrong layer

Wrong:

```text
DB is overloaded, so add more API pods.
```

This can make DB worse.

Correct:

```text
Identify bottleneck before scaling.
```

### Mistake 3: No alert on saturation

Wrong:

```text
Only alert on 5xx.
```

Correct:

```text
Alert on pool saturation, lag, timeout rate, disk usage before 5xx explodes.
```

### Mistake 4: Blocking user path on analytics

Wrong:

```text
Redirect waits for click analytics write.
```

Correct:

```text
Redirect should succeed even if analytics is delayed.
```

### Mistake 5: Treating queue as magic

Wrong:

```text
We use Kafka, so analytics is safe.
```

Correct:

```text
Kafka needs lag alerts, idempotency, DLQ, replay strategy.
```

### Mistake 6: Unsafe migrations

Wrong:

```text
Run ALTER TABLE on huge table during peak hours.
```

Correct:

```text
Use expand-contract, batches, lock monitoring, rollback plan.
```

### Mistake 7: Missing rollback plan

Wrong:

```text
We will figure out rollback during incident.
```

Correct:

```text
Every risky deploy has rollback steps before deploy.
```

### Mistake 8: No postmortem action owner

Wrong:

```text
We learned a lot.
```

Correct:

```text
Add alert, test, owner, deadline, and verification.
```

---

## 31. Interview-Ready Explanation

If interviewer asks:

```text
Tell me about production failure handling in your URL shortener design.
```

Strong answer:

```text
For a URL shortener, I treat the redirect path as the highest priority path because every click depends on it. The main production failures I design for are cache outage causing DB overload, hot short-code traffic, DB connection pool exhaustion, Kafka analytics lag, duplicate event processing, unsafe migrations, and bad deploys. I protect the redirect path with cache-first lookup, short dependency timeouts, circuit breakers, DB pool monitoring, local hot-key cache, and by keeping analytics asynchronous. For Kafka, I assume at-least-once delivery and make consumers idempotent. For DB changes, I use expand-contract migrations and avoid locking hot tables. During incidents, I first confirm impact using golden signals, check what changed, reduce blast radius through rollback or feature flags, then find root cause using logs, metrics, traces, pool metrics, Redis latency, DB locks, and Kafka lag. Every incident should end with a guardrail: alert, test, runbook, circuit breaker, or migration rule.
```

Why this is strong:

```text
1. Prioritizes redirect path.
2. Mentions realistic failure modes.
3. Explains cascade prevention.
4. Shows async/Kafka correctness.
5. Shows DB migration maturity.
6. Separates mitigation from root cause.
7. Ends with guardrails.
```

Senior one-liner:

```text
I do not only design the happy path; I design how the system fails without taking users down.
```

---

## 32. Senior Engineer Checklist

Before calling MiniURLShortener production-ready, check:

```text
[ ] Redirect path does not block on analytics
[ ] Redis timeout is short
[ ] Redis failure does not collapse DB
[ ] Hikari active/pending metrics are monitored
[ ] DB slow queries and locks are visible
[ ] Cache hit ratio is visible
[ ] Hot shortCode traffic can be detected
[ ] Kafka producer errors are monitored
[ ] Kafka consumer lag and lag age are monitored
[ ] Click consumers are idempotent
[ ] DLQ exists for poison events
[ ] Duplicate event strategy exists
[ ] Read replica lag is monitored
[ ] Replica miss can fallback to primary when needed
[ ] Rate limiter has safe keys and dry-run mode
[ ] Correlation ID exists in logs and response headers
[ ] Generic errors do not leak stack traces
[ ] Deployments are canaried or safely rolled out
[ ] Rollback plan exists
[ ] DB migrations follow expand-contract
[ ] Disk usage and log volume are monitored
[ ] Synthetic checks exist from outside cluster
[ ] Postmortems produce owners and guardrails
```

If these are checked, failure stories become prevention stories.

---

## 33. One-Page Cheat Sheet

```text
Core mental model:
Small trigger + missing guardrail + high load = production incident.

Failure story structure:
1. user symptom
2. dashboard signal
3. recent change
4. root cause
5. mitigation
6. permanent guardrail
7. lesson

Most common MiniURLShortener failures:
- Redis outage -> DB overload
- DB pool exhaustion -> thread starvation
- hot shortCode -> key-level overload
- Kafka lag -> stale analytics
- duplicate Kafka events -> inflated metrics
- bad deploy -> 500s
- unsafe migration -> table locks
- replica lag -> new links return 404
- rate limiter bad key -> real users blocked
- missing correlation ID -> slow debugging

First metrics:
latency, traffic, errors, saturation
Hikari active/pending
Redis latency/hit ratio
DB locks/slow queries
Kafka lag/oldest age
pod restarts/OOM/evictions

Mitigation options:
rollback
feature flag off
shorter timeout
circuit breaker
load shedding
bypass analytics
pause migration
route away from bad region
scale safe bottleneck

Permanent fixes:
alerts
tests
runbooks
idempotency
bulkheads
safe migrations
correlation IDs
hot-key protection
stale/fallback strategy

Golden rule:
Protect redirect path first.
```

---

## 34. One Picture To Remember

```text
                 PRODUCTION FAILURE STORY MENTAL MODEL

                              Trigger
                                 |
                                 v
                    +-------------------------+
                    | Missing Guardrail       |
                    | timeout? breaker? test? |
                    +-----------+-------------+
                                |
                                v
                    +-------------------------+
                    | Load Amplification      |
                    | retries, hot key, pool  |
                    +-----------+-------------+
                                |
                                v
                    +-------------------------+
                    | Cascading Failure       |
                    | DB, Redis, Kafka, API   |
                    +-----------+-------------+
                                |
                                v
                    +-------------------------+
                    | User Impact             |
                    | slow, 500, 404, stale   |
                    +-----------+-------------+
                                |
                                v
                    +-------------------------+
                    | Mitigation              |
                    | rollback, flag, shed    |
                    +-----------+-------------+
                                |
                                v
                    +-------------------------+
                    | Permanent Guardrail     |
                    | alert, test, runbook    |
                    +-------------------------+

FINAL MEMORY:

A senior engineer does not just ask, "Why did it fail?"
A senior engineer asks:

1. How did it reach users?
2. Why did guardrails not stop it?
3. How do we make this class of failure harder next time?
```

---

## 35. Final Retention Summary

Remember these five sentences:

```text
1. Production failures are usually cascades, not isolated bugs.
2. The redirect path is the most important MiniURLShortener path to protect.
3. Queues, caches, replicas, and rate limiters all introduce their own failure modes.
4. During incidents, reduce blast radius first, then investigate root cause.
5. Every incident must end with a guardrail: alert, test, runbook, limit, or design change.
```

Next chapter suggestion:

```text
066_Production_Readiness_Checklist.md
```
