# 060_SLO_SLA_Error_Budget.md
# MiniURLShortener — SLO, SLA & Error Budget

> Core mental model: **SLO is the engineering promise, SLA is the business/legal promise, and error budget is the amount of unreliability you are allowed to spend without breaking user trust.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. SLA vs SLO vs SLI](#4-sla-vs-slo-vs-sli)
- [5. Error Budget Mental Model](#5-error-budget-mental-model)
- [6. User Journey Based Reliability](#6-user-journey-based-reliability)
- [7. SLIs For URL Shortener](#7-slis-for-url-shortener)
- [8. SLO Design For Create API](#8-slo-design-for-create-api)
- [9. SLO Design For Redirect API](#9-slo-design-for-redirect-api)
- [10. Availability SLO](#10-availability-slo)
- [11. Latency SLO](#11-latency-slo)
- [12. Correctness SLO](#12-correctness-slo)
- [13. Freshness SLO](#13-freshness-slo)
- [14. Error Budget Calculation](#14-error-budget-calculation)
- [15. Burn Rate Mental Model](#15-burn-rate-mental-model)
- [16. Multi-Window Burn Rate Alerts](#16-multi-window-burn-rate-alerts)
- [17. Prometheus Recording Rules](#17-prometheus-recording-rules)
- [18. Prometheus Alert Rules](#18-prometheus-alert-rules)
- [19. Grafana Dashboard Design](#19-grafana-dashboard-design)
- [20. Tracing And Logs For SLO Debugging](#20-tracing-and-logs-for-slo-debugging)
- [21. Step-by-Step Dry Runs](#21-step-by-step-dry-runs)
- [22. Production Failure Stories](#22-production-failure-stories)
- [23. Debugging Mindset](#23-debugging-mindset)
- [24. Release Governance With Error Budget](#24-release-governance-with-error-budget)
- [25. Common Mistakes](#25-common-mistakes)
- [26. Interview-Ready Explanation](#26-interview-ready-explanation)
- [27. Senior Engineer Checklist](#27-senior-engineer-checklist)
- [28. One-Page Cheat Sheet](#28-one-page-cheat-sheet)
- [29. One Picture To Remember](#29-one-picture-to-remember)

---

## 1. Why This Exists

By now MiniURLShortener has observability foundations:

```text
057_Prometheus_Metrics.md
058_Grafana_Dashboards.md
059_Distributed_Tracing.md
```

You can collect metrics, draw dashboards, and trace requests.

But production teams still need one deeper answer:

```text
Are users actually happy with reliability?
```

Raw metrics are not enough.

Bad dashboard thinking:

```text
CPU is 45%.
Memory is 62%.
DB connections are 35.
Redis latency is 3ms.
```

Better reliability thinking:

```text
Can users create short URLs successfully?
Can users redirect quickly?
How much failure can we tolerate this month?
Are we allowed to deploy today?
Should we stop feature work and fix reliability?
```

That is where SLI, SLO, SLA, and error budget come in.

Production mental model:

```text
Metrics tell you what happened.
SLO tells you whether it matters.
Error budget tells you what decision to make.
```

ASCII:

```text
Metrics
  |
  v
SLI: what we measure
  |
  v
SLO: target we promise internally
  |
  v
Error Budget: allowed failure
  |
  v
Engineering Decision:
    deploy faster?
    freeze release?
    fix reliability?
```

---

## 2. The One Core Mental Model

SLO is not a dashboard.

SLO is a reliability contract between engineering and user happiness.

```text
User Journey
     |
     v
SLI measurement
     |
     v
SLO target
     |
     v
Error budget
     |
     v
Decision
```

For MiniURLShortener:

```text
User journey 1:
Create a short URL.

User journey 2:
Click a short URL and get redirected.
```

The redirect path matters more than the create path because redirect traffic is usually much higher and directly affects end users.

ASCII:

```text
                 RELIABILITY PYRAMID

                       SLA
              business/legal promise
                         ^
                         |
                       SLO
              internal engineering target
                         ^
                         |
                       SLI
              measured user experience
                         ^
                         |
                    Raw Metrics
              counters, histograms, logs
```

One-line memory:

```text
SLI measures reality, SLO defines acceptable reality, SLA defines contractual consequences.
```

---

## 3. Problem Statement

Design production-grade SLO, SLA, and error budget thinking for MiniURLShortener.

It must answer:

```text
1. What should we measure?
2. What is a good request?
3. What is a bad request?
4. What target should we set?
5. How much failure is allowed?
6. When should alerts fire?
7. When should releases stop?
8. How do metrics, logs, and traces help debug SLO violations?
```

Out of scope:

```text
1. Legal SLA contract writing.
2. Full incident management process.
3. Multi-region failover implementation.
4. Complete SRE organization design.
```

This chapter gives the engineering model.

---

## 4. SLA vs SLO vs SLI

### SLI

SLI means Service Level Indicator.

It is a measured number.

Examples:

```text
redirect success rate
create success rate
p95 redirect latency
p99 create latency
Kafka analytics freshness
```

### SLO

SLO means Service Level Objective.

It is the target for an SLI.

Example:

```text
99.9% of redirect requests should complete successfully over 30 days.
```

### SLA

SLA means Service Level Agreement.

It is usually an external business/legal promise.

Example:

```text
If monthly availability drops below 99.5%, customer receives service credit.
```

ASCII:

```text
+------+-------------------------+-----------------------------+
| Term | Meaning                 | Example                     |
+------+-------------------------+-----------------------------+
| SLI  | measured reality        | 99.93% success rate         |
| SLO  | internal target         | target >= 99.9%             |
| SLA  | external/legal promise  | credit if below 99.5%       |
+------+-------------------------+-----------------------------+
```

Important rule:

```text
SLA should usually be looser than SLO.
```

Why?

Because SLO is your early warning line.
SLA is the legal/business damage line.

ASCII:

```text
Better reliability
      ^
      |
100%  | perfect but impossible
99.95 | strong internal stretch
99.90 | SLO target
99.50 | SLA promise
      |
      +--------------------> worse reliability
```

---

## 5. Error Budget Mental Model

Error budget is the allowed failure.

Formula:

```text
Error Budget = 100% - SLO
```

If SLO is 99.9%, then error budget is:

```text
0.1%
```

Meaning:

```text
Out of all valid user requests, 0.1% may fail or be too slow during the window.
```

ASCII:

```text
Total valid requests in 30 days
+--------------------------------------------------+
| GOOD REQUESTS                                    |
| 99.9%                                            |
+--------------------------------------------------+
| BAD REQUESTS allowed                             |
| 0.1% error budget                                |
+--------------------------------------------------+
```

Error budget is not only math.

It is a decision mechanism.

```text
Budget healthy:
    ship features normally

Budget burning fast:
    investigate and slow risky deploys

Budget exhausted:
    freeze risky launches and fix reliability
```

Engineering memory:

```text
Error budget converts reliability from emotion into policy.
```

---

## 6. User Journey Based Reliability

Do not create SLOs from random infrastructure metrics first.

Start from user journeys.

For MiniURLShortener:

```text
Journey A: Create short URL
    POST /api/v1/urls

Journey B: Redirect short URL
    GET /{shortCode}

Journey C: Analytics pipeline
    click event should appear in analytics later
```

ASCII:

```text
User
 |
 +--> Create Link Journey
 |       POST /api/v1/urls
 |       needs correctness + acceptable latency
 |
 +--> Redirect Journey
 |       GET /abc123
 |       needs very high availability + low latency
 |
 +--> Analytics Journey
         click counted later
         needs freshness, not instant strict correctness
```

Different journeys need different SLOs.

Create API:

```text
Lower traffic.
More DB writes.
Can tolerate slightly higher latency.
```

Redirect API:

```text
High traffic.
User-facing click path.
Needs very low latency.
Must remain highly available.
```

Analytics:

```text
Async.
Can be delayed.
Should not break redirect path.
```

Rule:

```text
SLOs should follow user pain, not server architecture.
```

---

## 7. SLIs For URL Shortener

Good SLIs for MiniURLShortener:

```text
1. Request success rate
2. Request latency
3. Correct redirect rate
4. Analytics freshness
5. Error ratio by endpoint
```

### Good request definition

A redirect request is good if:

```text
1. shortCode exists
2. request returns expected 302
3. latency is under target threshold
```

A redirect request is not considered bad if:

```text
shortCode does not exist and API returns clean 404
shortCode is expired and API returns clean 410
shortCode is blocked and API returns clean 403
```

Why?

Because these are valid business outcomes, not service reliability failures.

ASCII:

```text
GET /abc123
   |
   +-- 302 under 100ms -----------> GOOD
   +-- 302 but 900ms -------------> BAD for latency SLO
   +-- 500 -----------------------> BAD
   +-- timeout -------------------> BAD
   +-- 404 for unknown code ------> usually NOT counted as outage
   +-- 410 expired ---------------> usually NOT counted as outage
```

Important distinction:

```text
HTTP status alone does not define reliability.
Business meaning defines reliability.
```

---

## 8. SLO Design For Create API

Create API:

```text
POST /api/v1/urls
```

Suggested SLOs:

```text
Availability SLO:
99.5% of valid create requests succeed over 30 days.

Latency SLO:
95% of successful create requests complete under 300ms over 30 days.

Correctness SLO:
99.99% of successful creates produce a unique shortCode mapped to the requested longUrl.
```

Why not 99.99% availability for create API first?

Because create is write-heavy and may depend on DB, ID generation, uniqueness checks, rate limiting, and validation.

It is important, but redirect usually matters more.

Create flow:

```text
POST /api/v1/urls
     |
     v
validation
     |
     v
ID generation
     |
     v
DB insert
     |
     v
response
```

Create SLI table:

```text
+----------------------+------------------------------------------+
| SLI                  | Good Event                               |
+----------------------+------------------------------------------+
| availability          | valid request returns 201/200            |
| latency               | valid successful request under threshold |
| correctness           | shortCode resolves to correct longUrl    |
+----------------------+------------------------------------------+
```

Bad events:

```text
500
timeout
DB unavailable
ID generation failure
duplicate generated code not retried correctly
```

Not bad for availability SLO:

```text
400 invalid URL
409 custom alias already exists
429 rate limited request
```

Why not?

Because the service correctly rejected the request.

---

## 9. SLO Design For Redirect API

Redirect API:

```text
GET /{shortCode}
```

Suggested SLOs:

```text
Availability SLO:
99.9% of valid active redirect requests return correct redirect over 30 days.

Latency SLO:
99% of valid active redirect requests complete under 100ms over 30 days.

Correctness SLO:
99.999% of redirects send users to the correct longUrl.
```

Redirect path:

```text
Browser / App
     |
     v
GET /abc123
     |
     v
Gateway / Load Balancer
     |
     v
Spring Boot Pod
     |
     +--> Redis cache hit -> return 302
     |
     +--> DB lookup -> cache -> return 302
```

ASCII:

```text
               REDIRECT RELIABILITY PATH

User Click
   |
   v
+---------+     +--------------+     +---------+     +-----------+
| Browser | --> | Load Balancer| --> | API Pod | --> | Redis/DB  |
+---------+     +--------------+     +---------+     +-----------+
                                      |
                                      v
                                  302 Location
```

Why redirect deserves stronger SLO:

```text
1. It is usually the highest traffic path.
2. It directly affects end users.
3. Users expect link clicks to be instant.
4. Downtime damages trust quickly.
```

Bad redirect events:

```text
500
timeout
wrong destination
cache returns stale wrong mapping
DB unavailable and cache miss
latency above threshold
```

Usually not counted as service failure:

```text
unknown shortCode -> 404
expired shortCode -> 410
blocked shortCode -> 403
invalid shortCode format -> 400
```

---

## 10. Availability SLO

Availability SLO measures successful service behavior.

Basic formula:

```text
Availability = good_requests / total_valid_requests
```

Example:

```text
valid redirect requests = 10,000,000
successful redirects = 9,995,000
availability = 9,995,000 / 10,000,000
availability = 99.95%
```

For 99.9% SLO:

```text
Allowed failures = 0.1%
Allowed failures for 10,000,000 requests = 10,000
```

ASCII:

```text
10,000,000 valid redirect requests
+------------------------------------------------+
| 9,990,000 must be good for 99.9% SLO           |
+------------------------------------------------+
| 10,000 may fail                                |
+------------------------------------------------+
```

Important:

```text
Availability SLO should count valid user journeys, not every HTTP response.
```

Why?

Because bots may generate millions of bad short codes.
Those should not make your reliability look terrible if your API returns correct 404s.

---

## 11. Latency SLO

Latency SLO measures speed from user perspective.

Example:

```text
99% of valid active redirects complete under 100ms.
```

Why percentile, not average?

Average hides pain.

Example:

```text
99 requests = 20ms
1 request = 5000ms
average ≈ 69ms
```

Average looks fine, but one user had terrible experience.

ASCII:

```text
Latency distribution

fast users                          slow users
|------------------------------------|----------|
10ms 20ms 30ms 40ms 50ms             800ms 2s

Average hides tail.
p95/p99 reveals tail.
```

Recommended latency SLOs:

```text
Redirect:
    p99 < 100ms for cache-hit heavy path
    p95 < 50ms when mostly cached

Create:
    p95 < 300ms
    p99 < 800ms

Analytics:
    event freshness p95 < 5 minutes
```

Do not set unrealistic latency SLOs before measuring baseline.

Rule:

```text
Measure first, then set SLO slightly stricter than what users need and system can realistically sustain.
```

---

## 12. Correctness SLO

Correctness is more important than availability in some systems.

For URL shortener:

```text
Wrong redirect is worse than failed redirect.
```

Why?

A failed redirect annoys user.
A wrong redirect may cause security, privacy, or business damage.

Correctness SLO examples:

```text
99.999% of successful redirects must resolve to the intended longUrl.

100% of custom aliases must map to exactly one active URL at a time.

0 known cases of redirecting to wrong customer-owned destination.
```

ASCII:

```text
shortCode abc123
      |
      v
Expected: https://store.com/product/10
Actual:   https://evil.com/login

This is not latency issue.
This is correctness incident.
```

Correctness risks:

```text
cache stale after update
wrong DB row returned
bad sharding router
duplicate shortCode
corrupt migration
incorrect fallback logic
```

Correctness guardrails:

```text
DB UNIQUE(short_code)
immutable mapping for active short codes
careful cache invalidation
integration tests
migration validation
canary release
shadow verification
```

---

## 13. Freshness SLO

Freshness matters for async systems.

For MiniURLShortener analytics:

```text
User clicks link.
Redirect succeeds immediately.
Click event goes to Kafka.
Analytics worker processes later.
Dashboard updates later.
```

This is not strict request availability.

It is freshness.

Freshness SLO example:

```text
95% of click events should appear in analytics within 5 minutes.
99% within 30 minutes.
```

ASCII:

```text
Redirect path:
User click -> 302 immediately

Analytics path:
click event -> Kafka -> worker -> DB -> dashboard
                  delay is acceptable but bounded
```

Freshness bad events:

```text
Kafka consumer lag too high
analytics worker down
DLQ increasing
DB writes failing
backpressure causing long delay
```

Freshness SLI:

```text
now - event_created_at when event becomes visible in analytics
```

Rule:

```text
Do not block redirect success on analytics success.
Use freshness SLO for async pipeline instead.
```

---

## 14. Error Budget Calculation

Formula:

```text
error_budget_ratio = 1 - SLO
allowed_bad_events = total_events * error_budget_ratio
```

Example 1:

```text
Redirect SLO = 99.9%
Monthly valid redirects = 50,000,000
Error budget = 0.1%
Allowed bad redirects = 50,000,000 * 0.001
Allowed bad redirects = 50,000
```

Example 2:

```text
Create SLO = 99.5%
Monthly valid creates = 1,000,000
Error budget = 0.5%
Allowed bad creates = 5,000
```

ASCII:

```text
99.9% SLO

Every 1000 valid requests:
+-----------------------------------------------+
| 999 must be good                              |
+-----------------------------------------------+
| 1 may be bad                                  |
+-----------------------------------------------+
```

Downtime approximation:

```text
30 days = 43,200 minutes
99.9% allows 0.1% bad time
43,200 * 0.001 = 43.2 minutes/month
```

Common availability budgets:

```text
+------------+-----------------------+
| SLO        | Approx bad time/month |
+------------+-----------------------+
| 99%        | 7.2 hours             |
| 99.5%      | 3.6 hours             |
| 99.9%      | 43.2 minutes          |
| 99.95%     | 21.6 minutes          |
| 99.99%     | 4.32 minutes          |
+------------+-----------------------+
```

Warning:

```text
Request-based SLO is usually better than time-based SLO for APIs.
```

Why?

Because one minute at peak traffic hurts more than one minute at 3 AM.

---

## 15. Burn Rate Mental Model

Burn rate tells how fast you are consuming error budget.

Simple idea:

```text
burn_rate = current_error_rate / allowed_error_rate
```

For 99.9% SLO:

```text
allowed_error_rate = 0.1% = 0.001
```

If current error rate is 1%:

```text
burn_rate = 0.01 / 0.001 = 10x
```

Meaning:

```text
You are burning budget 10 times faster than allowed.
```

ASCII:

```text
Error Budget Tank

Normal burn:
+----------------------------+
| fuel lasts whole month     |
+----------------------------+

10x burn:
+----------------------------+
| fuel disappears quickly    |
+----------------------------+
```

Burn rate interpretation:

```text
1x:
    exactly on budget

2x:
    concerning if sustained

10x:
    serious issue

50x:
    page immediately
```

Why burn rate alerts are better than raw error alerts:

```text
Raw alert:
    5xx > 10 per minute

Problem:
    ignores traffic volume and SLO target

Burn rate alert:
    service is consuming reliability budget too fast
```

---

## 16. Multi-Window Burn Rate Alerts

One window is not enough.

Short window catches fast incidents.
Long window avoids noise.

Example windows:

```text
Fast burn:
    5m and 1h

Slow burn:
    30m and 6h
```

ASCII:

```text
Incident detection

5m window:
    catches sudden fire quickly

1h window:
    confirms it is not one tiny spike

6h window:
    catches slow budget leak
```

Common alert strategy:

```text
Page:
    burn rate > 14.4x over 5m and 1h

Ticket / warning:
    burn rate > 6x over 30m and 6h

Low priority:
    burn rate > 3x over 2h and 1d
```

For 99.9% SLO:

```text
allowed error ratio = 0.001
14.4x burn threshold = 0.0144 = 1.44% bad events
6x burn threshold = 0.006 = 0.6% bad events
```

Why use AND condition?

```text
5m high + 1h high = real sustained issue
Only 5m high = maybe small spike
Only 1h high = maybe old issue recovering
```

---

## 17. Prometheus Recording Rules

Assume metrics from previous Prometheus chapter:

```text
http_server_requests_seconds_count
http_server_requests_seconds_bucket
```

Labels:

```text
uri
method
status
outcome
```

### Redirect total valid requests

For redirect endpoint, we may exclude invalid business outcomes depending on design.

Basic example:

```yaml
groups:
  - name: miniurl-slo-recording-rules
    rules:
      - record: miniurl:redirect_requests:rate5m
        expr: |
          sum(rate(http_server_requests_seconds_count{uri="/{shortCode}"}[5m]))
```

### Redirect bad requests

Count server failures and timeouts.

```yaml
      - record: miniurl:redirect_bad_requests:rate5m
        expr: |
          sum(rate(http_server_requests_seconds_count{uri="/{shortCode}",status=~"5.."}[5m]))
```

### Redirect error ratio

```yaml
      - record: miniurl:redirect_error_ratio:rate5m
        expr: |
          miniurl:redirect_bad_requests:rate5m
          /
          miniurl:redirect_requests:rate5m
```

### Latency SLI with histogram

p99 redirect latency:

```promql
histogram_quantile(
  0.99,
  sum(rate(http_server_requests_seconds_bucket{uri="/{shortCode}"}[5m])) by (le)
)
```

ASCII:

```text
Raw request metrics
      |
      v
Recording rules
      |
      v
Error ratio / latency percentile
      |
      v
Alerts + dashboards
```

Important:

```text
Recording rules make dashboards faster and alert expressions easier to read.
```

---

## 18. Prometheus Alert Rules

Example redirect SLO:

```text
SLO = 99.9%
allowed_error_ratio = 0.001
```

Fast burn page:

```yaml
groups:
  - name: miniurl-slo-alerts
    rules:
      - alert: RedirectErrorBudgetFastBurn
        expr: |
          (
            sum(rate(http_server_requests_seconds_count{uri="/{shortCode}",status=~"5.."}[5m]))
            /
            sum(rate(http_server_requests_seconds_count{uri="/{shortCode}"}[5m]))
          ) > (14.4 * 0.001)
          and
          (
            sum(rate(http_server_requests_seconds_count{uri="/{shortCode}",status=~"5.."}[1h]))
            /
            sum(rate(http_server_requests_seconds_count{uri="/{shortCode}"}[1h]))
          ) > (14.4 * 0.001)
        for: 2m
        labels:
          severity: page
          service: miniurl-shortener
          slo: redirect-availability
        annotations:
          summary: "Redirect API is burning error budget fast"
          description: "Redirect error budget burn rate is above 14.4x over 5m and 1h."
```

Slow burn ticket:

```yaml
      - alert: RedirectErrorBudgetSlowBurn
        expr: |
          (
            sum(rate(http_server_requests_seconds_count{uri="/{shortCode}",status=~"5.."}[30m]))
            /
            sum(rate(http_server_requests_seconds_count{uri="/{shortCode}"}[30m]))
          ) > (6 * 0.001)
          and
          (
            sum(rate(http_server_requests_seconds_count{uri="/{shortCode}",status=~"5.."}[6h]))
            /
            sum(rate(http_server_requests_seconds_count{uri="/{shortCode}"}[6h]))
          ) > (6 * 0.001)
        for: 10m
        labels:
          severity: ticket
          service: miniurl-shortener
          slo: redirect-availability
        annotations:
          summary: "Redirect API is slowly burning error budget"
          description: "Redirect error budget burn rate is above 6x over 30m and 6h."
```

Latency alert example:

```yaml
      - alert: RedirectP99LatencyHigh
        expr: |
          histogram_quantile(
            0.99,
            sum(rate(http_server_requests_seconds_bucket{uri="/{shortCode}"}[5m])) by (le)
          ) > 0.100
        for: 10m
        labels:
          severity: page
          service: miniurl-shortener
          slo: redirect-latency
        annotations:
          summary: "Redirect p99 latency is above 100ms"
```

---

## 19. Grafana Dashboard Design

A good SLO dashboard starts with user health.

Dashboard sections:

```text
1. SLO summary
2. Error budget remaining
3. Burn rate
4. Availability by endpoint
5. Latency percentiles
6. Dependency health
7. Recent deploy markers
8. Top traces/log links
```

ASCII layout:

```text
+---------------------------------------------------------+
| MiniURLShortener SLO Dashboard                          |
+------------------------+--------------------------------+
| Redirect SLO           | Create SLO                      |
| 99.93% / target 99.9   | 99.71% / target 99.5            |
+------------------------+--------------------------------+
| Error Budget Remaining | Burn Rate                       |
| 72%                    | 0.8x                            |
+---------------------------------------------------------+
| Redirect p50 p95 p99 latency                            |
+---------------------------------------------------------+
| 5xx by endpoint | Redis errors | DB errors | Kafka lag    |
+---------------------------------------------------------+
| Deploy markers + incident annotations                   |
+---------------------------------------------------------+
```

Good dashboard questions:

```text
Are users affected?
Which SLO is violated?
How fast is budget burning?
Which endpoint is responsible?
Which dependency changed?
Was there a deploy?
```

Bad dashboard design:

```text
100 panels but no decision.
```

Good dashboard design:

```text
5 panels that tell whether to act.
```

---

## 20. Tracing And Logs For SLO Debugging

SLO alert tells you user pain exists.

Tracing and logs tell you why.

Flow:

```text
SLO alert fires
     |
     v
Check dashboard
     |
     v
Identify endpoint and dependency
     |
     v
Open traces for slow/error requests
     |
     v
Search logs by correlationId/traceId
     |
     v
Find root cause
```

ASCII:

```text
SLO Alert
   |
   +--> Metrics: what and how much?
   |
   +--> Traces: where in request path?
   |
   +--> Logs: exact exception and context?
```

Example:

```text
Alert:
Redirect p99 latency > 100ms

Metrics:
Redis latency increased.

Trace:
GET /abc123 spends 180ms in Redis GET.

Logs:
Redis timeout warnings from one pod zone.

Root cause:
Bad Redis node/network issue.
```

SLO debugging needs all three:

```text
Metrics show symptom.
Traces show path.
Logs show details.
```

---

## 21. Step-by-Step Dry Runs

### Dry Run 1: Healthy redirect day

Traffic:

```text
valid redirects = 10,000,000
bad redirects = 2,000
```

Calculation:

```text
error ratio = 2,000 / 10,000,000
error ratio = 0.0002 = 0.02%
availability = 99.98%
```

SLO:

```text
99.9%
```

Result:

```text
SLO passed.
Error budget consumed = 0.02 / 0.1 = 20% of monthly budget if this is whole month.
```

---

### Dry Run 2: Redirect outage spike

SLO:

```text
99.9%
allowed error ratio = 0.1% = 0.001
```

Current 5m error ratio:

```text
2%
```

Burn rate:

```text
0.02 / 0.001 = 20x
```

Interpretation:

```text
Severe fast burn.
Page if sustained in short and long window.
```

ASCII:

```text
Allowed: 0.1% errors
Actual:  2.0% errors

Actual is 20x allowed.
```

---

### Dry Run 3: Bot attack causes many 404s

Traffic:

```text
GET /random0001 -> 404
GET /random0002 -> 404
GET /random0003 -> 404
```

Question:

```text
Should this burn redirect availability budget?
```

Answer:

```text
Usually no, if the service correctly returns 404 quickly.
```

Why?

```text
Unknown short code is not service failure.
It is a valid business response.
```

But still monitor:

```text
404 rate
bot traffic
rate limiting
CPU cost
```

---

### Dry Run 4: Analytics consumer lag

Redirects:

```text
healthy
```

Kafka consumer:

```text
lag = 2,000,000 messages
click events visible after 2 hours
```

Which SLO is affected?

```text
Analytics freshness SLO, not redirect availability SLO.
```

Decision:

```text
Do not roll back redirect API if redirect path is healthy.
Scale analytics workers or fix consumer issue.
```

---

### Dry Run 5: Error budget exhausted

Monthly budget:

```text
allowed bad redirects = 50,000
```

Current bad redirects:

```text
52,000
```

Result:

```text
Budget exhausted.
```

Policy:

```text
Stop risky feature launches.
Prioritize reliability fixes.
Review incidents.
Add tests/alerts/capacity if needed.
```

---

## 22. Production Failure Stories

### Failure Story 1: Dashboard was green, users were angry

Infrastructure metrics:

```text
CPU normal
memory normal
DB normal
```

User issue:

```text
redirect p99 latency = 3 seconds
```

Root cause:

```text
Dashboard focused on servers, not user journey latency.
```

Fix:

```text
Create redirect latency SLO and p99 dashboard.
```

Lesson:

```text
Server health is not equal to user happiness.
```

---

### Failure Story 2: 404 storm looked like outage

Bot generated random short codes.

Dashboard showed:

```text
HTTP non-2xx rate = 80%
```

Team panicked.

Root cause:

```text
SLO counted all 4xx as failures.
```

Fix:

```text
Define valid active redirect requests and count expected 404/410 separately.
```

Lesson:

```text
Reliability SLI must understand business meaning.
```

---

### Failure Story 3: Average latency hid bad tail

Dashboard showed:

```text
average redirect latency = 45ms
```

But users complained.

p99 showed:

```text
p99 = 2.5s
```

Root cause:

```text
A small percentage of requests were cache misses hitting overloaded DB.
```

Fix:

```text
Use p95/p99 latency SLO and dependency breakdown.
```

Lesson:

```text
Average latency is dangerous for user-facing systems.
```

---

### Failure Story 4: Analytics delay was treated as API outage

Click analytics delayed for one hour.

Redirect worked normally.

Root cause:

```text
Team had no separate freshness SLO.
```

Fix:

```text
Separate redirect availability SLO from analytics freshness SLO.
```

Lesson:

```text
Different user journeys need different SLOs.
```

---

### Failure Story 5: Deploy continued after budget was gone

After multiple incidents, error budget was exhausted.

Team still shipped risky cache changes.

Result:

```text
Another outage.
SLA breach.
Customer trust damaged.
```

Root cause:

```text
Error budget was measured but not connected to release policy.
```

Fix:

```text
Create error-budget-based release governance.
```

Lesson:

```text
An SLO without decision policy is only decoration.
```

---

## 23. Debugging Mindset

When SLO alert fires, ask:

```text
1. Which user journey is affected?
2. Which SLI is bad: availability, latency, correctness, freshness?
3. Is the burn fast or slow?
4. Is it one endpoint or all endpoints?
5. Is it one pod, zone, DB, Redis node, or Kafka partition?
6. Did a deploy happen recently?
7. Are errors expected business outcomes or true failures?
8. Do traces show app time, Redis time, DB time, or network time?
9. Do logs show a new exception class?
10. Is the error budget still healthy?
```

Debug map:

```text
Availability SLO bad:
    check 5xx, timeouts, dependency failures

Latency SLO bad:
    check p95/p99, traces, cache hit ratio, DB slow queries

Correctness SLO bad:
    check cache invalidation, DB mapping, shard routing, deployments

Freshness SLO bad:
    check Kafka lag, worker errors, DLQ, DB writes
```

ASCII:

```text
SLO violation
   |
   +-- availability? -> 5xx/timeouts/dependencies
   |
   +-- latency? -----> p99/traces/cache/DB
   |
   +-- correctness? -> wrong mapping/cache/sharding
   |
   +-- freshness? --> Kafka lag/workers/DLQ
```

Golden rule:

```text
Do not debug from infrastructure upward first.
Debug from user pain downward.
```

---

## 24. Release Governance With Error Budget

Error budget should guide release decisions.

Example policy:

```text
Budget remaining > 50%:
    normal releases allowed

Budget remaining 20% to 50%:
    release with caution
    stronger canary required

Budget remaining 0% to 20%:
    only low-risk releases
    reliability fixes prioritized

Budget exhausted:
    freeze risky feature releases
    only reliability/security fixes
```

ASCII:

```text
Error Budget Remaining

100%  +--------------------+ normal shipping
 50%  +--------------------+ cautious shipping
 20%  +--------------------+ reliability focus
  0%  +--------------------+ freeze risky changes
```

This avoids two extremes:

```text
Extreme 1:
    Never ship because reliability fear.

Extreme 2:
    Ship always and burn users.
```

Balanced rule:

```text
If reliability is healthy, spend budget to move fast.
If reliability is unhealthy, earn budget back by fixing system.
```

---

## 25. Common Mistakes

### Mistake 1: Confusing SLA and SLO

Wrong:

```text
Our SLA is 99.9%, so engineering target is also 99.9%.
```

Better:

```text
SLO should usually be stricter than SLA.
```

---

### Mistake 2: Creating SLOs from CPU and memory

Wrong:

```text
CPU should be under 80%.
```

That is useful monitoring, but not a user SLO.

Better:

```text
99.9% of valid redirects should complete successfully.
```

---

### Mistake 3: Counting all 4xx as failures

Wrong:

```text
404 unknown shortCode burns availability budget.
```

Better:

```text
Expected business responses should be classified separately.
```

---

### Mistake 4: Using average latency

Wrong:

```text
Average latency is 50ms, so users are happy.
```

Better:

```text
Track p95/p99 latency.
```

---

### Mistake 5: Too many SLOs

Wrong:

```text
Create 50 SLOs for every tiny metric.
```

Better:

```text
Start with 3-5 user-journey SLOs.
```

---

### Mistake 6: No error budget policy

Wrong:

```text
We track error budget but never change behavior.
```

Better:

```text
Error budget controls release risk and reliability work.
```

---

### Mistake 7: Unrealistic SLO

Wrong:

```text
99.999% for everything from day one.
```

Better:

```text
Choose SLO based on user need, architecture maturity, and cost.
```

---

## 26. Interview-Ready Explanation

If interviewer asks:

```text
How would you define SLOs for a URL shortener?
```

Strong answer:

```text
I would start from user journeys, not infrastructure metrics. The two main journeys are creating a short URL and redirecting an active short code. Redirect is the most critical path because it is high traffic and directly user-facing, so I would define an availability SLO such as 99.9% of valid active redirect requests returning the correct redirect over 30 days, and a latency SLO such as 99% under 100ms. For create, I may start with 99.5% availability and p95 under 300ms because it is write-heavy and less latency-sensitive. I would define SLIs carefully so expected business responses like 404 for unknown codes or 410 for expired links do not burn the availability budget. I would calculate error budget as 100% minus SLO, track burn rate, and alert using multi-window burn rate alerts. If error budget is healthy, teams can ship faster. If it is exhausted, we freeze risky features and focus on reliability fixes. Metrics show the SLO violation, traces show where the request slowed or failed, and logs give the exception-level context.
```

Why this is strong:

```text
1. Starts from user journeys.
2. Separates redirect and create paths.
3. Defines valid request semantics.
4. Uses availability and latency SLOs.
5. Mentions correctness.
6. Explains error budget.
7. Uses burn-rate alerts.
8. Connects SLOs to release decisions.
9. Shows metrics/traces/logs debugging flow.
```

Senior one-liner:

```text
SLOs turn observability data into reliability decisions.
```

---

## 27. Senior Engineer Checklist

Before calling your observability production-ready, confirm:

```text
[ ] Main user journeys are identified
[ ] Redirect SLO exists
[ ] Create SLO exists
[ ] Analytics freshness SLO exists if analytics is user-visible
[ ] SLI clearly defines good and bad events
[ ] Expected 4xx business responses are classified correctly
[ ] Availability SLO uses valid user requests
[ ] Latency SLO uses p95/p99, not average
[ ] Correctness risks are monitored separately
[ ] Error budget is calculated over a clear window
[ ] Burn rate dashboard exists
[ ] Fast burn page alert exists
[ ] Slow burn ticket alert exists
[ ] Dashboard shows budget remaining
[ ] Dashboard links to traces and logs
[ ] Deploy markers are visible in Grafana
[ ] Release policy uses error budget state
[ ] Incident review checks SLO impact
[ ] SLOs are reviewed after architecture changes
```

---

## 28. One-Page Cheat Sheet

```text
Core mental model:
SLI measures reality.
SLO defines acceptable reality.
SLA defines contractual consequence.
Error budget is allowed unreliability.

Formula:
Error budget = 100% - SLO
Burn rate = current error rate / allowed error rate

URL shortener journeys:
1. Create short URL
2. Redirect short URL
3. Analytics freshness

Redirect SLO example:
99.9% of valid active redirects return correct 302 over 30 days.
99% of valid active redirects complete under 100ms.

Create SLO example:
99.5% of valid create requests succeed over 30 days.
p95 create latency under 300ms.

Do not count as outage:
400 invalid input
404 unknown code
410 expired code
403 blocked code
409 duplicate alias

Usually count as bad:
500
timeout
wrong redirect
latency above SLO threshold
DB/Redis dependency failure causing user failure

Burn rate:
1x = on budget
10x = serious
50x = emergency

Dashboard must answer:
Are users affected?
Which SLO?
How much budget left?
How fast burning?
Which endpoint/dependency?
Was there a deploy?

Release policy:
Healthy budget -> ship
Low budget -> caution
Exhausted budget -> freeze risky releases, fix reliability
```

---

## 29. One Picture To Remember

```text
                  SLO / SLA / ERROR BUDGET MENTAL MODEL

                         USER JOURNEY
                              |
                              v
              +-------------------------------+
              | What does user need?          |
              | create / redirect / analytics |
              +-------------------------------+
                              |
                              v
              +-------------------------------+
              | SLI                           |
              | measured good vs bad events   |
              +-------------------------------+
                              |
                              v
              +-------------------------------+
              | SLO                           |
              | internal reliability target   |
              +-------------------------------+
                              |
                              v
              +-------------------------------+
              | Error Budget                  |
              | allowed failure               |
              +-------------------------------+
                              |
              +---------------+---------------+
              |                               |
              v                               v
       Budget healthy                   Budget burning fast
       ship normally                    investigate / slow deploys
              |                               |
              v                               v
       Budget exhausted                 reliability mode
       freeze risky work                fix root cause


FINAL MEMORY:

Metrics observe the system.
SLOs judge user happiness.
Error budgets guide engineering behavior.
SLAs define external consequences.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. SLI is what you measure, SLO is what you target, SLA is what you promise externally.
2. Error budget is the amount of failure allowed by the SLO.
3. Good SLOs start from user journeys, not CPU or memory graphs.
4. Expected business responses like 404 unknown shortCode should not automatically count as outages.
5. Burn-rate alerts and error-budget policy turn observability into engineering decisions.
```

After this chapter, MiniObservability has a complete production reliability layer:

```text
057 Prometheus Metrics
058 Grafana Dashboards
059 Distributed Tracing
060 SLO SLA Error Budget
```
