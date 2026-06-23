# 016_Actuator_Health_Checks.md
# MiniURLShortener — Actuator & Health Checks

> Core mental model: **Actuator health checks are the application’s production heartbeat. They let infrastructure decide whether the app is alive, ready to receive traffic, overloaded, misconfigured, or dependent on a broken downstream system.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What Spring Boot Actuator Is](#4-what-spring-boot-actuator-is)
- [5. Health Check Mental Model](#5-health-check-mental-model)
- [6. Liveness vs Readiness](#6-liveness-vs-readiness)
- [7. MiniURLShortener Health Requirements](#7-miniurlshortener-health-requirements)
- [8. Maven Dependency](#8-maven-dependency)
- [9. Basic Actuator Configuration](#9-basic-actuator-configuration)
- [10. Health Endpoint Output](#10-health-endpoint-output)
- [11. Database Health Check](#11-database-health-check)
- [12. Redis Health Check Later](#12-redis-health-check-later)
- [13. Custom Shortener Health Indicator](#13-custom-shortener-health-indicator)
- [14. Liveness Probe Configuration](#14-liveness-probe-configuration)
- [15. Readiness Probe Configuration](#15-readiness-probe-configuration)
- [16. Kubernetes Probe Mental Model](#16-kubernetes-probe-mental-model)
- [17. Docker Compose Health Check](#17-docker-compose-health-check)
- [18. Health Groups](#18-health-groups)
- [19. Endpoint Exposure Security](#19-endpoint-exposure-security)
- [20. Metrics vs Health Checks](#20-metrics-vs-health-checks)
- [21. Create API Failure Flow With Health](#21-create-api-failure-flow-with-health)
- [22. Redirect API Failure Flow With Health](#22-redirect-api-failure-flow-with-health)
- [23. Step-by-Step Dry Runs](#23-step-by-step-dry-runs)
- [24. Internal Execution Walkthrough](#24-internal-execution-walkthrough)
- [25. Testing Strategy](#25-testing-strategy)
- [26. Production Failure Stories](#26-production-failure-stories)
- [27. Debugging Mindset](#27-debugging-mindset)
- [28. Common Mistakes](#28-common-mistakes)
- [29. Interview-Ready Explanation](#29-interview-ready-explanation)
- [30. Senior Engineer Checklist](#30-senior-engineer-checklist)
- [31. One-Page Cheat Sheet](#31-one-page-cheat-sheet)
- [32. One Picture To Remember](#32-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener is no longer only a local Spring Boot API.

It is becoming a production-shaped backend.

You already have:

```text
POST /api/v1/urls
GET /{shortCode}
Controller -> Service -> Repository
JPA / JDBC choices
Transaction boundaries
Global exception handling
```

But production infrastructure needs one more question answered continuously:

```text
Can this app instance safely receive traffic right now?
```

A load balancer, Kubernetes, Docker, monitoring system, or deployment pipeline cannot understand your business code directly.

It needs simple signals:

```text
UP
DOWN
OUT_OF_SERVICE
UNKNOWN
```

Without health checks, infrastructure behaves blindly.

Bad production examples:

```text
App process is running, but DB connection is broken.
Load balancer still sends traffic.
Users receive 500.

App starts slowly because schema migration is running.
Kubernetes sends traffic too early.
First requests fail.

App deadlocks internally.
Process still exists.
No restart happens.

DB is temporarily down.
Kubernetes restarts every pod repeatedly.
System enters crash loop.
```

Good production behavior:

```text
If app is dead -> restart it.
If app is alive but not ready -> keep it running, but remove traffic.
If DB is down -> readiness should fail, liveness should usually stay alive.
If deployment is warming up -> wait before routing requests.
```

One-line production rule:

```text
Health checks are not for humans clicking endpoints; they are contracts between your app and infrastructure.
```

---

## 2. The One Core Mental Model

Actuator health checks are the:

```text
APPLICATION HEARTBEAT + TRAFFIC GATE
```

Heartbeat asks:

```text
Is the process alive?
```

Traffic gate asks:

```text
Should traffic be sent here?
```

ASCII:

```text
                    Infrastructure
         Load Balancer / Kubernetes / Docker / Monitor
                              |
                              v
                    +-------------------+
                    |  Health Endpoint  |
                    |  /actuator/health |
                    +-------------------+
                              |
            +-----------------+-----------------+
            |                                   |
            v                                   v
   Is app process alive?              Is app ready for traffic?
       LIVENESS                              READINESS
            |                                   |
            v                                   v
   restart if broken                  remove from load balancer
```

The most important distinction:

```text
Liveness protects the container from being stuck.
Readiness protects users from being routed to a bad instance.
```

For MiniURLShortener:

```text
Liveness:
    Is Spring Boot running?
    Is the JVM responsive?

Readiness:
    Can the app serve create and redirect requests?
    Is DB reachable?
    Are required dependencies usable?
```

Memory hook:

```text
Liveness = should I restart this app?
Readiness = should I send traffic to this app?
```

---

## 3. Problem Statement

Build production-style health checks for MiniURLShortener.

The chapter must answer:

```text
1. What is Spring Boot Actuator?
2. Why do health checks exist?
3. What is liveness?
4. What is readiness?
5. What should be checked for URL shortener?
6. How do DB checks affect readiness?
7. How do Kubernetes probes use health endpoints?
8. What should not be exposed publicly?
9. How do health checks help debugging?
10. How do we avoid wrong restarts and bad traffic routing?
```

Out of scope:

```text
Full Prometheus setup
Grafana dashboard
Distributed tracing
Alertmanager rules
Advanced SLO design
```

Those belong to observability phase.

This chapter focuses on health checks as a production safety boundary.

---

## 4. What Spring Boot Actuator Is

Spring Boot Actuator adds production-ready endpoints to a Spring Boot app.

Examples:

```text
/actuator/health
/actuator/info
/actuator/metrics
/actuator/prometheus
/actuator/env
/actuator/beans
/actuator/loggers
```

For this chapter, focus mainly on:

```text
/actuator/health
/actuator/health/liveness
/actuator/health/readiness
```

Actuator is not your business API.

Business API:

```text
POST /api/v1/urls
GET /{shortCode}
```

Operations API:

```text
GET /actuator/health
GET /actuator/health/readiness
GET /actuator/health/liveness
```

ASCII:

```text
                  MiniURLShortener App

       +---------------------------------------+
       | Business Endpoints                    |
       |                                       |
       | POST /api/v1/urls                     |
       | GET  /{shortCode}                     |
       +---------------------------------------+

       +---------------------------------------+
       | Operational Endpoints                 |
       |                                       |
       | GET /actuator/health                  |
       | GET /actuator/health/liveness         |
       | GET /actuator/health/readiness        |
       +---------------------------------------+
```

Mental model:

```text
Business endpoints serve users.
Actuator endpoints serve operators and infrastructure.
```

---

## 5. Health Check Mental Model

A health check is a small question with a strict answer.

Question:

```text
Are you okay?
```

Answer:

```json
{
  "status": "UP"
}
```

But production needs more nuance.

Example:

```text
App process is alive.
Database is down.
```

Should the app be restarted?

Usually:

```text
No.
```

Should the app receive traffic?

Usually:

```text
No.
```

That is why liveness and readiness are separate.

ASCII:

```text
+-----------------------------+
| App Process Running         |
| JVM responsive              |
+-----------------------------+
             |
             v
       Liveness = UP

+-----------------------------+
| App can serve requests      |
| DB reachable                |
| required dependencies ready |
+-----------------------------+
             |
             v
       Readiness = UP or DOWN
```

Health status should be simple.

Do not make health endpoint perform heavy business work.

Bad health check:

```text
Insert row into DB
Call external payment API
Call third-party analytics
Run expensive query
Scan table
```

Good health check:

```text
Can app respond?
Can DB connection be acquired?
Can required dependency answer a lightweight ping?
```

Rule:

```text
Health checks must be cheap, fast, safe, and predictable.
```

---

## 6. Liveness vs Readiness

This is the most important section.

### Liveness

Liveness answers:

```text
Is the app alive enough to keep running?
```

If liveness fails, Kubernetes restarts the container.

Use liveness for:

```text
JVM deadlock
app event loop stuck
process not responding
fatal internal broken state
```

Do not fail liveness only because database is down.

Why?

Because restarting every pod will not fix the database.

ASCII:

```text
DB DOWN
  |
  v
If liveness depends on DB:

Pod 1 fails liveness -> restart
Pod 2 fails liveness -> restart
Pod 3 fails liveness -> restart

Result:
CrashLoopBackOff storm
System becomes worse
```

### Readiness

Readiness answers:

```text
Can this app instance receive user traffic now?
```

If readiness fails, Kubernetes removes the pod from service endpoints.

The pod is not restarted just because readiness fails.

Use readiness for:

```text
DB unavailable
Redis unavailable later
migration still running
cache warming
app not fully initialized
external required dependency down
```

ASCII:

```text
                   Kubernetes Service
                           |
             +-------------+-------------+
             |                           |
             v                           v
        Pod A ready                  Pod B not ready
             |                           |
             v                           x
        receives traffic           no traffic routed
```

One-line memory:

```text
Liveness failure kills the pod; readiness failure removes the pod from traffic.
```

---

## 7. MiniURLShortener Health Requirements

MiniURLShortener has two core user paths.

Create path:

```text
POST /api/v1/urls
needs application + database
```

Redirect path:

```text
GET /{shortCode}
needs application + database
later needs Redis cache
```

For current phase:

```text
Required dependency:
    PostgreSQL

Optional/future dependency:
    Redis
    Kafka
    CDN
```

Health requirement table:

```text
+----------------------+----------------------+-------------------------+
| Component            | Liveness Impact      | Readiness Impact        |
+----------------------+----------------------+-------------------------+
| JVM running          | yes                  | yes                     |
| Spring context       | yes                  | yes                     |
| PostgreSQL           | usually no           | yes                     |
| Redis later          | usually no           | depends on design       |
| Kafka async logs     | no                   | usually no for redirect |
| Metrics endpoint     | no                   | no                      |
+----------------------+----------------------+-------------------------+
```

Important design decision:

```text
If PostgreSQL is down, MiniURLShortener cannot create or redirect from DB.
So readiness should be DOWN.
```

But liveness should remain UP because the app process itself is alive.

Correct behavior:

```text
DB down:
    liveness  = UP
    readiness = DOWN
```

---

## 8. Maven Dependency

Add Actuator dependency.

File:

```text
pom.xml
```

Code:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

If using Spring Web and Data JPA, you may already have:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

After adding actuator, start app and open:

```text
GET http://localhost:8080/actuator/health
```

Expected simple response:

```json
{
  "status": "UP"
}
```

---

## 9. Basic Actuator Configuration

File:

```text
src/main/resources/application.yml
```

Basic production-shaped config:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when_authorized
      probes:
        enabled: true
  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true
```

For local learning, you can temporarily use:

```yaml
management:
  endpoint:
    health:
      show-details: always
```

But in production, avoid exposing detailed dependency information publicly.

Safer production principle:

```text
Public health endpoint should say UP/DOWN.
Internal authenticated health can show details.
```

ASCII:

```text
Public Internet
      |
      v
/actuator/health  -> minimal status only

Internal Network / Authenticated Operator
      |
      v
/actuator/health  -> detailed components
```

---

## 10. Health Endpoint Output

Simple output:

```json
{
  "status": "UP"
}
```

Detailed output may look like:

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

When database is down:

```json
{
  "status": "DOWN",
  "components": {
    "db": {
      "status": "DOWN"
    }
  }
}
```

Important:

```text
Health response is not an error response like ApiErrorResponse.
It is an operational status response.
```

Do not force actuator health to use your API error contract.

Business API errors:

```json
{
  "code": "SHORT_CODE_NOT_FOUND",
  "message": "shortCode not found"
}
```

Actuator health:

```json
{
  "status": "UP"
}
```

They serve different users.

---

## 11. Database Health Check

Spring Boot Actuator automatically provides a DB health indicator when DataSource exists.

It checks whether a database connection can be validated.

ASCII:

```text
/actuator/health/readiness
          |
          v
  HealthEndpoint
          |
          v
  DataSourceHealthIndicator
          |
          v
  HikariCP connection
          |
          v
  PostgreSQL reachable?
          |
   +------+------+
   |             |
   v             v
  UP            DOWN
```

Why DB health matters for MiniURLShortener:

```text
Create URL requires insert into short_urls.
Redirect requires lookup by short_code.
If DB is down, app cannot reliably serve core business flows.
```

But be careful:

```text
DB health check should be lightweight.
```

Do not run:

```sql
SELECT COUNT(*) FROM short_urls;
```

That can become expensive.

Good DB health behavior:

```text
Acquire connection from pool.
Validate connection.
Return UP/DOWN.
```

Production caution:

```text
If health checks are too frequent and expensive, they themselves can overload the system.
```

---

## 12. Redis Health Check Later

In the next phases, redirect becomes read-heavy.

You may add Redis:

```text
GET /{shortCode}
    Redis cache lookup
    DB fallback
```

Should Redis failure make readiness DOWN?

It depends.

Case 1: Redis is optimization only.

```text
If Redis down, fallback to DB works.
Readiness may remain UP.
```

Case 2: Redis is required for rate limiting or redirect latency SLA.

```text
If Redis down, system may be considered degraded or not ready.
```

Decision table:

```text
+------------------------------+-----------------------------+
| Redis Role                   | Readiness When Redis Down   |
+------------------------------+-----------------------------+
| cache only with DB fallback  | maybe UP / degraded         |
| required session store       | DOWN                        |
| required rate limiter        | depends on fail-open/closed |
| required redirect store      | DOWN                        |
+------------------------------+-----------------------------+
```

Senior mindset:

```text
Do not blindly include every dependency in readiness.
Include dependencies required for serving traffic safely.
```

---

## 13. Custom Shortener Health Indicator

Sometimes you need a custom health check.

Example:

```text
Check whether shortener service can access required configuration.
Check whether base URL is configured.
Check whether short code length is valid.
```

Create:

```text
src/main/java/com/miniurl/shortener/common/health/ShortenerConfigHealthIndicator.java
```

Code:

```java
package com.miniurl.shortener.common.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ShortenerConfigHealthIndicator implements HealthIndicator {

    private final String publicBaseUrl;
    private final int shortCodeLength;

    public ShortenerConfigHealthIndicator(
            @Value("${app.shortener.public-base-url:}") String publicBaseUrl,
            @Value("${app.shortener.short-code-length:7}") int shortCodeLength
    ) {
        this.publicBaseUrl = publicBaseUrl;
        this.shortCodeLength = shortCodeLength;
    }

    @Override
    public Health health() {
        if (publicBaseUrl == null || publicBaseUrl.isBlank()) {
            return Health.down()
                    .withDetail("reason", "public base URL is missing")
                    .build();
        }

        if (shortCodeLength < 4 || shortCodeLength > 16) {
            return Health.down()
                    .withDetail("reason", "short code length must be between 4 and 16")
                    .build();
        }

        return Health.up()
                .withDetail("publicBaseUrlConfigured", true)
                .withDetail("shortCodeLength", shortCodeLength)
                .build();
    }
}
```

Example config:

```yaml
app:
  shortener:
    public-base-url: https://sho.rt
    short-code-length: 7
```

When healthy:

```json
{
  "status": "UP",
  "components": {
    "shortenerConfig": {
      "status": "UP"
    }
  }
}
```

Naming note:

```text
Class name ShortenerConfigHealthIndicator becomes component name shortenerConfig.
```

Use custom health indicators carefully.

Good custom checks:

```text
fast configuration validation
local state check
lightweight dependency ping
```

Bad custom checks:

```text
expensive DB scan
third-party slow API call
business write operation
```

---

## 14. Liveness Probe Configuration

Spring Boot can expose liveness endpoint:

```text
/actuator/health/liveness
```

Example response:

```json
{
  "status": "UP"
}
```

Kubernetes liveness probe:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
  timeoutSeconds: 2
  failureThreshold: 3
```

Meaning:

```text
initialDelaySeconds: wait 30 seconds before first probe
periodSeconds: check every 10 seconds
timeoutSeconds: fail if no response in 2 seconds
failureThreshold: restart after 3 consecutive failures
```

ASCII:

```text
Kubernetes kubelet
       |
       v
GET /actuator/health/liveness
       |
       +-- 200 UP ------> keep pod running
       |
       +-- fail x3 -----> restart container
```

Liveness should be conservative.

Wrong liveness:

```text
DB down -> liveness DOWN -> restart pod
```

Correct liveness:

```text
App process stuck -> liveness DOWN -> restart pod
```

---

## 15. Readiness Probe Configuration

Spring Boot can expose readiness endpoint:

```text
/actuator/health/readiness
```

Kubernetes readiness probe:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 20
  periodSeconds: 5
  timeoutSeconds: 2
  failureThreshold: 3
```

ASCII:

```text
Kubernetes Service
        |
        v
Endpoint list contains only ready pods
        |
        +-- Pod A readiness UP   -> included
        +-- Pod B readiness DOWN -> excluded
        +-- Pod C readiness UP   -> included
```

When readiness fails:

```text
Pod remains running.
Traffic is not routed to it.
```

This is perfect for:

```text
startup delay
DB temporarily down
migration running
warming cache later
```

Important:

```text
Readiness failure is not always a bug.
Sometimes it is the correct safety response.
```

---

## 16. Kubernetes Probe Mental Model

Kubernetes uses probes to decide pod lifecycle behavior.

ASCII:

```text
                +---------------------+
                | Kubernetes Kubelet  |
                +---------------------+
                          |
          +---------------+---------------+
          |                               |
          v                               v
   Liveness Probe                  Readiness Probe
          |                               |
          v                               v
 Should restart pod?           Should route traffic?
          |                               |
    yes/no decision               yes/no decision
```

Traffic flow:

```text
Client
  |
  v
Load Balancer
  |
  v
Kubernetes Service
  |
  v
Only READY pods
  |
  v
MiniURLShortener Pod
```

If pod is alive but not ready:

```text
Process exists.
No traffic.
No restart.
```

If pod is not alive:

```text
Process exists or hangs.
Restart happens.
```

One picture:

```text
Liveness = self-healing
Readiness = traffic safety
```

---

## 17. Docker Compose Health Check

For local development, Docker Compose can also use health checks.

Example app service:

```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 10s
      timeout: 3s
      retries: 5
      start_period: 30s
```

Postgres service:

```yaml
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: miniurl
      POSTGRES_USER: miniurl
      POSTGRES_PASSWORD: miniurl
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U miniurl -d miniurl"]
      interval: 10s
      timeout: 5s
      retries: 5
```

ASCII startup:

```text
Docker starts postgres
        |
        v
pg_isready passes
        |
        v
Docker starts app
        |
        v
app /actuator/health passes
        |
        v
local environment ready
```

Important nuance:

```text
depends_on controls startup ordering, not full production resilience.
```

Your app should still handle DB connection failures gracefully.

---

## 18. Health Groups

Spring Boot supports health groups.

You can define different health endpoints with different components.

Example:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
      group:
        liveness:
          include: livenessState
        readiness:
          include: readinessState,db,shortenerConfig
```

Meaning:

```text
Liveness checks only app liveness state.
Readiness checks app readiness, DB, and shortener config.
```

ASCII:

```text
/actuator/health/liveness
        |
        +-- livenessState

/actuator/health/readiness
        |
        +-- readinessState
        +-- db
        +-- shortenerConfig
```

This is cleaner than one giant health endpoint.

Why?

Because different consumers need different answers.

```text
Kubelet liveness wants restart signal.
Load balancer readiness wants traffic signal.
Monitoring wants dependency visibility.
```

---

## 19. Endpoint Exposure Security

Actuator can expose sensitive information if configured carelessly.

Dangerous exposure:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

This may expose:

```text
environment variables
configuration properties
beans
loggers
mappings
metrics
```

Bad public exposure:

```text
/actuator/env
/actuator/configprops
/actuator/beans
```

These can leak:

```text
internal hostnames
property names
feature flags
configuration structure
sometimes secrets if misconfigured
```

Safer config:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when_authorized
```

Even better production architecture:

```text
Expose health publicly only if needed by load balancer.
Expose detailed actuator endpoints only internally.
Protect with Spring Security.
```

ASCII:

```text
Internet
   |
   v
Public LB
   |
   +--> /actuator/health only

Internal Admin Network
   |
   +--> /actuator/metrics
   +--> /actuator/loggers
   +--> /actuator/prometheus
```

Rule:

```text
Actuator is powerful. Expose minimum endpoints needed.
```

---

## 20. Metrics vs Health Checks

Health checks and metrics are different.

Health check:

```text
Binary-ish current state.
UP or DOWN.
Used for traffic and restart decisions.
```

Metrics:

```text
Time-series measurements.
Used for dashboards, alerts, capacity planning.
```

Examples:

```text
Health:
    DB reachable? UP/DOWN

Metrics:
    request count
    p95 latency
    p99 latency
    DB connection pool usage
    JVM memory
    HTTP 500 count
```

ASCII:

```text
Health Check
    |
    v
Can I send traffic now?

Metrics
    |
    v
How is the system behaving over time?
```

Do not overload health checks with metrics logic.

Bad:

```text
If p99 latency > 500ms, readiness DOWN immediately.
```

Why bad?

```text
Temporary latency spike can remove all pods from traffic.
```

Better:

```text
Use metrics + alerting for latency.
Use readiness for hard dependency availability.
```

---

## 21. Create API Failure Flow With Health

Create API:

```text
POST /api/v1/urls
```

Needs DB insert.

When DB is healthy:

```text
Readiness UP
Traffic routed
Create works
```

When DB is down:

```text
Readiness DOWN
Traffic should stop reaching this pod
Existing in-flight requests may fail cleanly
```

ASCII:

```text
Client
  |
  v
Load Balancer
  |
  v
Kubernetes Service
  |
  +-- readiness UP pod ----> POST /api/v1/urls
  |
  +-- readiness DOWN pod --X no traffic
```

If a request reaches app during DB outage:

```text
Controller
  |
  v
Service
  |
  v
Repository
  |
  v
DB connection failure
  |
  v
GlobalExceptionHandler
  |
  v
safe 500 or mapped infrastructure response
```

Health checks reduce failures.

They do not eliminate all failures.

Why?

```text
A dependency can fail between readiness check and actual request.
```

Production rule:

```text
Health checks are traffic guidance, not a replacement for error handling.
```

---

## 22. Redirect API Failure Flow With Health

Redirect API:

```text
GET /{shortCode}
```

Current phase:

```text
Needs DB lookup.
```

Future phase:

```text
Try Redis cache.
Fallback DB.
```

Current DB-down flow:

```text
DB down -> readiness DOWN -> pod removed from traffic.
```

But if request still arrives:

```text
GET /abc123
  |
  v
Service lookup
  |
  v
DB unavailable
  |
  v
Exception
  |
  v
Global handler returns safe error
```

Future Redis-down but DB-up flow:

```text
GET /abc123
  |
  v
Redis miss/failure
  |
  v
DB lookup succeeds
  |
  v
302 redirect
```

Then Redis may not need to make readiness DOWN.

ASCII:

```text
Redirect with Redis later

Client
  |
  v
App
  |
  +--> Redis UP ----> return cached URL
  |
  +--> Redis DOWN
           |
           v
        DB fallback
           |
           v
        redirect still works
```

Design lesson:

```text
Readiness depends on whether the dependency is mandatory or optional for safe serving.
```

---

## 23. Step-by-Step Dry Runs

### Dry Run 1: App healthy, DB healthy

State:

```text
Spring app running
DB reachable
shortener config valid
```

Request:

```http
GET /actuator/health/readiness
```

Flow:

```text
1. Kubelet calls readiness endpoint.
2. Actuator invokes readiness health group.
3. readinessState is UP.
4. DB health indicator validates connection.
5. shortenerConfig health indicator checks config.
6. All checks pass.
7. Response is UP.
8. Kubernetes keeps pod in service endpoints.
```

Result:

```json
{
  "status": "UP"
}
```

---

### Dry Run 2: App healthy, DB down

State:

```text
Spring app running
PostgreSQL unavailable
```

Readiness flow:

```text
1. Kubelet calls /actuator/health/readiness.
2. App responds.
3. DB health indicator cannot validate connection.
4. Readiness becomes DOWN.
5. Kubernetes removes pod from traffic.
6. Pod is not restarted just because readiness failed.
```

Correct result:

```text
liveness  = UP
readiness = DOWN
```

Why this is correct:

```text
Restarting app will not fix PostgreSQL.
Removing traffic protects users.
```

---

### Dry Run 3: App stuck

State:

```text
JVM process exists
HTTP server not responding
```

Flow:

```text
1. Kubelet calls liveness endpoint.
2. Request times out.
3. This happens 3 consecutive times.
4. Liveness probe fails.
5. Kubernetes restarts container.
```

Correct result:

```text
restart pod
```

---

### Dry Run 4: Missing base URL config

Config:

```yaml
app:
  shortener:
    public-base-url: ""
```

Flow:

```text
1. Actuator invokes ShortenerConfigHealthIndicator.
2. It sees publicBaseUrl is blank.
3. It returns DOWN.
4. Readiness becomes DOWN if included in readiness group.
5. Pod does not receive traffic.
```

Why good:

```text
Without public base URL, create API may generate broken short links.
```

---

### Dry Run 5: Redis down in future cache-aside design

State:

```text
Redis down
PostgreSQL up
App supports DB fallback
```

Decision:

```text
Readiness may remain UP.
```

Why:

```text
Redirect still works, but slower.
This is degraded mode, not total unready mode.
```

Better observability:

```text
Metric/alert for Redis down.
Readiness only DOWN if Redis is mandatory.
```

---

## 24. Internal Execution Walkthrough

When `/actuator/health/readiness` is called:

```text
1. HTTP request enters embedded Tomcat.
2. Spring maps request to Actuator HealthEndpoint.
3. HealthEndpoint asks HealthContributorRegistry for relevant indicators.
4. Readiness group selects configured indicators.
5. Each indicator returns Health.up() or Health.down().
6. Actuator aggregates statuses.
7. HTTP response is returned.
```

ASCII:

```text
GET /actuator/health/readiness
        |
        v
DispatcherServlet
        |
        v
Actuator HealthEndpoint
        |
        v
Health Group: readiness
        |
        +--> readinessState
        +--> db
        +--> shortenerConfig
        |
        v
Aggregate result
        |
        v
HTTP 200 with UP
or HTTP 503 with DOWN
```

Status aggregation:

```text
If all required components UP -> overall UP
If one required component DOWN -> overall DOWN
```

Important:

```text
Actuator health is outside normal controller/service/repository business flow.
```

It should not call your `UrlShortenerService.createShortUrl()`.

Why?

```text
Health checks must not create data or mutate business state.
```

---

## 25. Testing Strategy

Test actuator configuration and custom health indicators.

### Test 1: Health endpoint exposed

```text
GET /actuator/health -> 200
```

MockMvc example:

```java
mockMvc.perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
```

### Test 2: Custom config health UP

```java
ShortenerConfigHealthIndicator indicator =
        new ShortenerConfigHealthIndicator("https://sho.rt", 7);

Health health = indicator.health();

assertThat(health.getStatus()).isEqualTo(Status.UP);
```

### Test 3: Missing base URL health DOWN

```java
ShortenerConfigHealthIndicator indicator =
        new ShortenerConfigHealthIndicator("", 7);

Health health = indicator.health();

assertThat(health.getStatus()).isEqualTo(Status.DOWN);
```

### Test 4: Invalid short code length health DOWN

```java
ShortenerConfigHealthIndicator indicator =
        new ShortenerConfigHealthIndicator("https://sho.rt", 2);

Health health = indicator.health();

assertThat(health.getStatus()).isEqualTo(Status.DOWN);
```

### Test 5: DB readiness with Testcontainers

Use Testcontainers later:

```text
Postgres container running -> readiness UP
Postgres container stopped -> readiness DOWN
```

Testing rule:

```text
Do not only test happy health.
Test misconfiguration and dependency failure.
```

---

## 26. Production Failure Stories

### Failure Story 1: DB outage causes pod restart storm

A team configured liveness to include DB health.

When PostgreSQL went down:

```text
Every pod liveness failed.
Kubernetes restarted all pods.
Pods started again.
DB was still down.
Liveness failed again.
CrashLoopBackOff everywhere.
```

Root cause:

```text
Liveness was checking external dependency.
```

Fix:

```text
Liveness checks only app responsiveness.
Readiness checks DB.
```

Lesson:

```text
Do not use liveness to detect dependency outages.
```

---

### Failure Story 2: Traffic sent before app is ready

During deployment, app started but migration was still running.

Kubernetes sent traffic immediately.

Users saw:

```text
500 errors
missing table errors
connection errors
```

Root cause:

```text
No readiness probe or initial delay too small.
```

Fix:

```text
Use readiness probe.
Tune startup timing.
Ensure readiness only UP after app is truly ready.
```

Lesson:

```text
Process started is not equal to traffic ready.
```

---

### Failure Story 3: Exposed actuator leaked internals

A service exposed:

```text
/actuator/env
/actuator/beans
/actuator/configprops
```

Public users could inspect internal configuration structure.

Root cause:

```text
management.endpoints.web.exposure.include=*
```

Fix:

```text
Expose only health/info publicly.
Protect sensitive endpoints behind internal network and auth.
```

Lesson:

```text
Actuator is powerful and must be secured.
```

---

### Failure Story 4: Expensive health check overloaded DB

A custom health check ran:

```sql
SELECT COUNT(*) FROM short_urls;
```

Every pod ran this every few seconds.

At scale:

```text
health checks created load
DB CPU increased
real user traffic slowed
readiness started flapping
```

Fix:

```text
Use lightweight connection validation.
Never scan business tables in health check.
```

Lesson:

```text
Health checks must not become the disease.
```

---

### Failure Story 5: Readiness flapped during temporary latency

A team made readiness fail when p99 latency crossed threshold.

During a traffic spike:

```text
Pods became unready.
Remaining pods got more traffic.
Their latency increased.
They also became unready.
Entire service lost capacity.
```

Fix:

```text
Use metrics and autoscaling for latency.
Use readiness for hard inability to serve.
```

Lesson:

```text
Do not turn every performance symptom into readiness failure.
```

---

## 27. Debugging Mindset

When health check fails, ask:

```text
Which endpoint failed?
/actuator/health
/actuator/health/liveness
/actuator/health/readiness
```

Then ask:

```text
Is app dead or only not ready?
Is DB down?
Is config missing?
Is startup still running?
Is probe timeout too aggressive?
Is health check too expensive?
Is endpoint exposed on correct port/path?
Is security blocking the probe?
```

Debug map:

```text
liveness DOWN:
    app may be stuck
    HTTP server may not respond
    probe path wrong
    timeout too low

readiness DOWN:
    DB unavailable
    config invalid
    startup incomplete
    dependency required but down

health endpoint 404:
    actuator not added
    endpoint not exposed
    wrong management base path

health endpoint 401/403:
    Spring Security blocking actuator
    kubelet/load balancer lacks permission
```

Useful commands:

```bash
curl -i http://localhost:8080/actuator/health
curl -i http://localhost:8080/actuator/health/liveness
curl -i http://localhost:8080/actuator/health/readiness
```

Kubernetes commands:

```bash
kubectl describe pod miniurl-xxx
kubectl logs miniurl-xxx
kubectl get endpoints
kubectl get pods -w
```

Golden rule:

```text
Before changing code, identify whether the failure is liveness, readiness, dependency, config, security, or probe tuning.
```

---

## 28. Common Mistakes

### Mistake 1: Confusing liveness and readiness

Wrong:

```text
DB down -> liveness DOWN
```

Correct:

```text
DB down -> readiness DOWN, liveness usually UP
```

### Mistake 2: Exposing all actuator endpoints

Wrong:

```yaml
include: "*"
```

Correct:

```yaml
include: health,info,metrics
```

And protect sensitive endpoints.

### Mistake 3: Heavy health checks

Wrong:

```text
Health check runs expensive query.
```

Correct:

```text
Health check uses lightweight dependency validation.
```

### Mistake 4: No readiness probe

Wrong:

```text
Traffic sent as soon as container starts.
```

Correct:

```text
Traffic sent only after readiness UP.
```

### Mistake 5: Making optional dependencies mandatory

Wrong:

```text
Redis cache down -> readiness DOWN even though DB fallback works.
```

Correct:

```text
Treat Redis as degraded if fallback works.
```

### Mistake 6: Hiding all health details during debugging

Wrong:

```text
Only {status: DOWN}, no logs, no internal details.
```

Correct:

```text
Public minimal response, internal detailed response/logs.
```

### Mistake 7: Depending only on health checks

Wrong:

```text
If readiness is UP, requests cannot fail.
```

Correct:

```text
Dependencies can fail between checks. Error handling still needed.
```

---

## 29. Interview-Ready Explanation

If interviewer asks:

```text
How do you use health checks in your Spring Boot URL shortener?
```

Strong answer:

```text
I use Spring Boot Actuator to expose operational health endpoints separately from business APIs. The key design is separating liveness and readiness. Liveness tells Kubernetes whether the app process is alive and should be restarted if stuck. Readiness tells the load balancer whether this instance can safely receive traffic. For the URL shortener, PostgreSQL is required for create and redirect in the current phase, so DB health belongs in readiness. But DB failure should usually not fail liveness, because restarting all pods will not fix a database outage and can create a restart storm. I expose minimal public health information, keep sensitive actuator endpoints internal or protected, and avoid expensive checks. Health checks guide traffic and restart decisions, while global exception handling still protects individual requests because dependencies can fail between checks.
```

Why this is strong:

```text
1. Separates business API from operational API.
2. Explains liveness vs readiness correctly.
3. Understands DB dependency behavior.
4. Avoids restart storm.
5. Mentions security exposure.
6. Mentions cheap health checks.
7. Connects health checks with error handling.
8. Shows Kubernetes/load balancer thinking.
```

Senior one-liner:

```text
Liveness is for self-healing; readiness is for safe traffic routing.
```

---

## 30. Senior Engineer Checklist

Before moving forward, confirm:

```text
[ ] spring-boot-starter-actuator added
[ ] /actuator/health works locally
[ ] health endpoint exposure is minimal
[ ] show-details is safe for environment
[ ] probes.enabled=true configured
[ ] liveness endpoint works
[ ] readiness endpoint works
[ ] DB health included in readiness
[ ] DB health not blindly included in liveness
[ ] custom config health indicator added if useful
[ ] health checks are lightweight
[ ] actuator sensitive endpoints not public
[ ] Docker Compose healthcheck exists for local setup
[ ] Kubernetes livenessProbe planned
[ ] Kubernetes readinessProbe planned
[ ] probe delays/timeouts are realistic
[ ] health failure debugging commands documented
[ ] team understands readiness DOWN does not mean pod restart
[ ] global exception handler still handles request-level failures
```

If these are true, MiniURLShortener has production-shaped health behavior.

---

## 31. One-Page Cheat Sheet

```text
Core mental model:
Actuator health checks are the app heartbeat and traffic gate.

Business endpoints:
POST /api/v1/urls
GET /{shortCode}

Operational endpoints:
GET /actuator/health
GET /actuator/health/liveness
GET /actuator/health/readiness

Liveness:
Question: should this pod be restarted?
Failure action: restart container
Should not usually depend on DB

Readiness:
Question: should this pod receive traffic?
Failure action: remove from service endpoints
Should include mandatory dependencies like DB

For MiniURLShortener current phase:
DB down -> readiness DOWN
DB down -> liveness usually UP

Good health check:
cheap
fast
safe
non-mutating
predictable

Bad health check:
expensive query
business write
slow third-party call
sensitive public detail

Actuator security:
Do not expose include: "*" publicly.
Expose minimum endpoints.
Protect internal details.

Kubernetes:
livenessProbe -> /actuator/health/liveness
readinessProbe -> /actuator/health/readiness

Health vs metrics:
Health = can I send traffic now?
Metrics = how system behaves over time?
```

---

## 32. One Picture To Remember

```text
             ACTUATOR & HEALTH CHECKS MENTAL MODEL

                         Infrastructure
              Kubernetes / Docker / Load Balancer
                               |
                               v
                    +------------------------+
                    | Spring Boot Actuator   |
                    +------------------------+
                               |
              +----------------+----------------+
              |                                 |
              v                                 v
   /actuator/health/liveness        /actuator/health/readiness
              |                                 |
              v                                 v
      Is JVM/app alive?              Can app receive traffic?
              |                                 |
       fail -> restart              fail -> remove traffic
              |                                 |
              v                                 v
       protects pod                  protects users


MiniURLShortener rule:

DB down:
    liveness  = UP
    readiness = DOWN

App stuck:
    liveness  = DOWN
    readiness = DOWN or unreachable

Final memory:

Liveness asks: should I kill and restart this instance?
Readiness asks: should I send user traffic to this instance?
Health checks guide infrastructure.
Error handling still protects each request.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Actuator exposes operational endpoints for infrastructure and engineers.
2. Liveness decides restart; readiness decides traffic routing.
3. Database failure should usually fail readiness, not liveness.
4. Health checks must be cheap, safe, non-mutating, and minimally exposed.
5. Health checks reduce bad routing, but request-level error handling is still required.
```

After this chapter, your MiniURLShortener production coding phase has a health-check foundation:

```text
010 Clean Architecture
011 Controller Service Repository
012 JPA vs JDBC
013 Transaction Boundaries
014 Global Exception Handler
015 Configuration Properties
016 Actuator Health Checks
```
