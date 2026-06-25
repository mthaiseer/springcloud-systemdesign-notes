# 056_Logging_Correlation_Id.md
# MiniURLShortener — Logging & Correlation ID

> Core mental model: **A correlation ID is the thread that stitches one user request across logs, services, database calls, cache calls, Kafka messages, and Kubernetes pods. Logging tells you what happened; correlation ID tells you which request it happened to.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Logging vs Tracing vs Metrics](#4-logging-vs-tracing-vs-metrics)
- [5. What Is a Correlation ID?](#5-what-is-a-correlation-id)
- [6. Request Flow Without Correlation ID](#6-request-flow-without-correlation-id)
- [7. Request Flow With Correlation ID](#7-request-flow-with-correlation-id)
- [8. Production Log Contract](#8-production-log-contract)
- [9. Spring Boot MDC Mental Model](#9-spring-boot-mdc-mental-model)
- [10. Correlation ID Filter](#10-correlation-id-filter)
- [11. Logback JSON / Pattern Setup](#11-logback-json--pattern-setup)
- [12. Controller Logging](#12-controller-logging)
- [13. Service Logging](#13-service-logging)
- [14. Repository / Database Logging Mindset](#14-repository--database-logging-mindset)
- [15. Feign / RestTemplate Propagation](#15-feign--resttemplate-propagation)
- [16. Kafka Propagation](#16-kafka-propagation)
- [17. API Gateway / Ingress / EKS Flow](#17-api-gateway--ingress--eks-flow)
- [18. Logging Levels](#18-logging-levels)
- [19. What Not To Log](#19-what-not-to-log)
- [20. Step-by-Step Dry Runs](#20-step-by-step-dry-runs)
- [21. Internal Execution Walkthrough](#21-internal-execution-walkthrough)
- [22. Local Testing With curl](#22-local-testing-with-curl)
- [23. Unit and Integration Testing](#23-unit-and-integration-testing)
- [24. Production Failure Stories](#24-production-failure-stories)
- [25. Debugging Mindset](#25-debugging-mindset)
- [26. Common Mistakes](#26-common-mistakes)
- [27. Interview-Ready Explanation](#27-interview-ready-explanation)
- [28. Senior Engineer Checklist](#28-senior-engineer-checklist)
- [29. One-Page Cheat Sheet](#29-one-page-cheat-sheet)
- [30. One Picture To Remember](#30-one-picture-to-remember)

---

## 1. Why This Exists

By now MiniURLShortener has production-like pieces:

```text
Client
  -> Spring Cloud Gateway
  -> URL Shortener service
  -> PostgreSQL
  -> Redis
  -> Kafka click analytics
  -> async worker
  -> EKS deployment
```

When something fails, a beginner asks:

```text
Why did it fail?
```

A production engineer asks:

```text
Which request failed?
Which user/request path caused it?
Which pod handled it?
Which database query was slow?
Was Redis missed?
Was Kafka publish successful?
Did the async worker process the same event?
Can I find all logs for this one request in 10 seconds?
```

Without correlation ID, logs become scattered noise:

```text
pod-1: Create short URL started
pod-3: Redis timeout
pod-2: Kafka publish failed
pod-1: Internal error
```

You cannot know whether these logs belong to the same request.

With correlation ID:

```text
correlationId=7f9a-abc request started
correlationId=7f9a-abc cache lookup miss
correlationId=7f9a-abc database insert ok
correlationId=7f9a-abc kafka publish ok
correlationId=7f9a-abc response 201
```

Now debugging becomes a straight line.

Production memory:

```text
Logs without correlation ID are like CCTV footage without timestamps.
```

---

## 2. The One Core Mental Model

Logging and correlation ID are the:

```text
REQUEST STORY RECORDER
```

Every request becomes a story.

```text
start -> validate -> business logic -> DB/Redis/Kafka -> response
```

Correlation ID is the story ID.

ASCII:

```text
              ONE USER REQUEST STORY

Client
  |
  | X-Correlation-Id: req-123
  v
+-------------------+
| Gateway           | logs req-123
+-------------------+
  |
  v
+-------------------+
| URL Service       | logs req-123
+-------------------+
  |        |       |
  |        |       +-----------> Kafka logs req-123
  |        +-------------------> Redis logs req-123
  +----------------------------> Postgres logs req-123

Final search in logs:

correlationId = req-123
```

One-line memory:

```text
Logging records events; correlation ID connects events.
```

---

## 3. Problem Statement

Build a clean logging and correlation ID model for MiniURLShortener.

It must support:

```text
1. Accept correlation ID from incoming HTTP header.
2. Generate a new correlation ID when missing.
3. Put correlation ID into MDC.
4. Add correlation ID to every log line.
5. Return correlation ID in response header.
6. Propagate correlation ID to downstream HTTP calls.
7. Propagate correlation ID into Kafka message headers.
8. Restore correlation ID in async Kafka consumers.
9. Avoid logging sensitive data.
10. Make debugging easy in local, Docker, and EKS.
```

Out of scope for this chapter:

```text
1. Full OpenTelemetry tracing.
2. Grafana/Loki setup.
3. ELK stack deployment.
4. CloudWatch dashboard setup.
5. Advanced PII redaction engine.
```

This chapter creates the production-shaped logging foundation.

---

## 4. Logging vs Tracing vs Metrics

These three are related but different.

### Logs

Logs answer:

```text
What happened?
```

Example:

```text
correlationId=req-123 event=SHORT_URL_CREATED shortCode=abc123 latencyMs=42
```

### Metrics

Metrics answer:

```text
How often? How fast? How many?
```

Example:

```text
http_requests_total = 10,000
p99_latency = 180ms
error_rate = 0.3%
```

### Traces

Traces answer:

```text
Where did time go across services?
```

Example:

```text
Gateway span: 10ms
URL service span: 70ms
Postgres span: 45ms
Kafka span: 8ms
```

ASCII:

```text
+-----------+----------------------+-----------------------------+
| Tool      | Main Question        | Example                     |
+-----------+----------------------+-----------------------------+
| Logs      | What happened?       | exception, event, message   |
| Metrics   | How much/how often?  | p99, RPS, error rate        |
| Traces    | Where did time go?   | service-to-service timeline |
+-----------+----------------------+-----------------------------+
```

Correlation ID is useful even before full tracing.

```text
Without tracing, correlation ID still connects logs.
With tracing, correlation ID complements traceId/spanId.
```

---

## 5. What Is a Correlation ID?

A correlation ID is a unique identifier attached to one request or workflow.

Common header names:

```text
X-Correlation-Id
X-Request-Id
traceparent
```

For this project, use:

```text
X-Correlation-Id
```

Example HTTP request:

```http
POST /api/v1/urls HTTP/1.1
Host: api.miniurl.com
Content-Type: application/json
X-Correlation-Id: req-20260625-abc123
```

If client does not send it, backend generates it:

```text
UUID.randomUUID().toString()
```

Response should include it:

```http
HTTP/1.1 201 Created
X-Correlation-Id: req-20260625-abc123
```

Why return it?

```text
Client can report: "My request failed. Correlation ID is req-20260625-abc123."
Engineer searches logs using that ID.
```

---

## 6. Request Flow Without Correlation ID

Problem scenario:

```text
POST /api/v1/urls returns 500
```

Logs:

```text
INFO  Create URL request received
INFO  Validating long URL
ERROR DataIntegrityViolationException
INFO  Kafka publish started
ERROR Unexpected server error
```

In one pod, this may be understandable.

In production with many pods:

```text
url-service-7f9d8c-abcde
url-service-7f9d8c-fghij
url-service-7f9d8c-klmno
analytics-worker-12345
spring-cloud-gateway-88888
```

Logs mix together.

ASCII:

```text
Request A ---- log ---- log ---- error
Request B -------- log ---- log
Request C -- log -------- error

Combined log stream:

log
log
error
log
log
error

Which error belongs to which request?
```

This is why correlation ID exists.

---

## 7. Request Flow With Correlation ID

With correlation ID:

```text
Request A = corr-A
Request B = corr-B
Request C = corr-C
```

Combined logs:

```text
corr-A request started
corr-B request started
corr-A validation ok
corr-C request started
corr-B redis miss
corr-A db insert failed
corr-C response 302
```

Search:

```text
correlationId=corr-A
```

You get:

```text
corr-A request started
corr-A validation ok
corr-A db insert failed
```

ASCII:

```text
ALL LOGS
  |
  v
+----------------------------+
| Filter correlationId=corr-A|
+----------------------------+
  |
  v
Only one request story
```

Production debugging becomes fast.

---

## 8. Production Log Contract

A good log line should be structured.

Minimum fields:

```text
timestamp
level
service
environment
correlationId
method
path
status
latencyMs
event
message
exceptionClass
podName
```

Example JSON log:

```json
{
  "timestamp": "2026-06-25T10:15:30.123Z",
  "level": "INFO",
  "service": "miniurl-shortener",
  "environment": "prod",
  "correlationId": "req-123",
  "method": "POST",
  "path": "/api/v1/urls",
  "status": 201,
  "latencyMs": 43,
  "event": "SHORT_URL_CREATED",
  "shortCode": "aB91xZ"
}
```

For local learning, pattern logs are enough:

```text
2026-06-25 10:15:30 INFO [miniurl-shortener] [req-123] POST /api/v1/urls 201 43ms SHORT_URL_CREATED shortCode=aB91xZ
```

Important distinction:

```text
Message is for human reading.
Fields are for searching/filtering.
```

---

## 9. Spring Boot MDC Mental Model

MDC means:

```text
Mapped Diagnostic Context
```

Think of MDC as a small map attached to the current thread.

```text
MDC["correlationId"] = "req-123"
```

Every log line from that thread can include it.

ASCII:

```text
HTTP Request Thread
  |
  v
+-------------------------+
| MDC Map                 |
| correlationId=req-123   |
| method=POST             |
| path=/api/v1/urls       |
+-------------------------+
  |
  v
Logger prints fields automatically
```

Important warning:

```text
MDC is thread-local.
```

That means:

```text
Same thread -> MDC available.
Different async thread -> MDC may be missing unless copied.
Kafka consumer thread -> must set MDC again from message header.
```

Rule:

```text
Set MDC at request entry.
Clear MDC at request exit.
Restore MDC at async boundaries.
```

---

## 10. Correlation ID Filter

Create file:

```text
src/main/java/com/miniurl/shortener/common/logging/CorrelationIdFilter.java
```

Code:

```java
package com.miniurl.shortener.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String correlationId = request.getHeader(CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        try {
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            MDC.put("method", request.getMethod());
            MDC.put("path", request.getRequestURI());

            response.setHeader(CORRELATION_ID_HEADER, correlationId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
```

Why `finally`?

```text
Tomcat reuses threads.
If MDC is not cleared, the next request may accidentally reuse old correlation ID.
```

Thread reuse bug:

```text
Request A puts corr-A into MDC.
Request A finishes but MDC not cleared.
Same thread handles Request B.
Request B logs corr-A by mistake.
```

This is a serious debugging trap.

---

## 11. Logback JSON / Pattern Setup

For local learning, create:

```text
src/main/resources/logback-spring.xml
```

Simple pattern version:

```xml
<configuration>

    <property name="APP_NAME" value="miniurl-shortener" />

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>
                %d{yyyy-MM-dd HH:mm:ss.SSS} %-5level
                [${APP_NAME}]
                [corr=%X{correlationId}]
                [method=%X{method}]
                [path=%X{path}]
                [%thread]
                %logger{36} - %msg%n
            </pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>

</configuration>
```

Example output:

```text
2026-06-25 10:15:30.123 INFO [miniurl-shortener] [corr=req-123] [method=POST] [path=/api/v1/urls] [http-nio-8080-exec-1] UrlController - Creating short URL
```

For real production, prefer JSON logs because log platforms search fields better.

Conceptual JSON format:

```json
{
  "timestamp": "2026-06-25T10:15:30.123Z",
  "level": "INFO",
  "service": "miniurl-shortener",
  "correlationId": "req-123",
  "method": "POST",
  "path": "/api/v1/urls",
  "logger": "UrlController",
  "message": "Creating short URL"
}
```

---

## 12. Controller Logging

Controller should log request boundary, not every small internal detail.

Example:

```java
package com.miniurl.shortener.url.controller;

import com.miniurl.shortener.url.dto.CreateShortUrlRequest;
import com.miniurl.shortener.url.dto.CreateShortUrlResponse;
import com.miniurl.shortener.url.service.UrlService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private static final Logger log = LoggerFactory.getLogger(UrlController.class);

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateShortUrlResponse createShortUrl(
            @Valid @RequestBody CreateShortUrlRequest request
    ) {
        log.info("event=CREATE_SHORT_URL_REQUEST_RECEIVED hasCustomAlias={}",
                request.getCustomAlias() != null && !request.getCustomAlias().isBlank());

        CreateShortUrlResponse response = urlService.createShortUrl(request);

        log.info("event=CREATE_SHORT_URL_REQUEST_COMPLETED shortCode={}",
                response.getShortCode());

        return response;
    }
}
```

Notice:

```text
We do not log full longUrl.
```

Why?

Long URLs may contain:

```text
tokens
session IDs
email addresses
reset-password links
tracking identifiers
```

Better log:

```text
host=example.com
urlLength=140
urlHash=abc123
```

---

## 13. Service Logging

Service logs important business decisions.

Example:

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.url.dto.CreateShortUrlRequest;
import com.miniurl.shortener.url.dto.CreateShortUrlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private static final Logger log = LoggerFactory.getLogger(UrlService.class);

    private final UrlValidatorService validatorService;
    private final ShortCodeGenerator shortCodeGenerator;
    private final ShortUrlRepository repository;

    public UrlService(
            UrlValidatorService validatorService,
            ShortCodeGenerator shortCodeGenerator,
            ShortUrlRepository repository
    ) {
        this.validatorService = validatorService;
        this.shortCodeGenerator = shortCodeGenerator;
        this.repository = repository;
    }

    public CreateShortUrlResponse createShortUrl(CreateShortUrlRequest request) {
        log.debug("event=CREATE_SHORT_URL_VALIDATION_STARTED");

        validatorService.validateLongUrl(request.getLongUrl());

        if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {
            validatorService.validateCustomAlias(request.getCustomAlias());
            log.info("event=CUSTOM_ALIAS_REQUESTED");
        }

        String shortCode = request.getCustomAlias();
        if (shortCode == null || shortCode.isBlank()) {
            shortCode = shortCodeGenerator.generate();
            log.debug("event=SHORT_CODE_GENERATED shortCode={}", shortCode);
        }

        // Persist entity here.
        // Publish Kafka event later.

        log.info("event=SHORT_URL_CREATED shortCode={}", shortCode);

        return new CreateShortUrlResponse(shortCode, "https://miniurl.com/" + shortCode);
    }
}
```

Logging rule:

```text
INFO = important business lifecycle
DEBUG = detailed developer diagnostics
ERROR = something failed and needs attention
```

Do not turn service into log spam.

---

## 14. Repository / Database Logging Mindset

Database logging should help answer:

```text
Was DB called?
Was it slow?
Did it fail?
Was the failure expected or unexpected?
```

But do not log every SQL query at INFO in production.

Bad:

```text
INFO select * from short_urls where long_url='https://secret-reset-link...'
```

Better:

```text
INFO event=DB_INSERT_SHORT_URL_SUCCESS shortCode=abc123 latencyMs=12
WARN event=DB_INSERT_SHORT_URL_SLOW shortCode=abc123 latencyMs=850
ERROR event=DB_INSERT_SHORT_URL_FAILED exceptionClass=DataIntegrityViolationException
```

Production mindset:

```text
For normal traffic, log business events.
For deep SQL, use DEBUG locally or database monitoring.
```

ASCII:

```text
Application logs:
    DB operation outcome + latency

Database logs/APM:
    exact SQL + plan + wait events
```

---

## 15. Feign / RestTemplate Propagation

When service A calls service B, correlation ID must move with the request.

ASCII:

```text
Client
  |
  | X-Correlation-Id=req-123
  v
Service A
  |
  | X-Correlation-Id=req-123
  v
Service B
```

Feign interceptor:

```java
package com.miniurl.shortener.common.logging;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignCorrelationIdConfig {

    @Bean
    public RequestInterceptor correlationIdRequestInterceptor() {
        return template -> {
            String correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY);
            if (correlationId != null && !correlationId.isBlank()) {
                template.header(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationId);
            }
        };
    }
}
```

Why this matters:

```text
Without propagation, Service B generates a new ID.
Then one user request becomes two disconnected log stories.
```

Correct:

```text
Gateway logs req-123
URL service logs req-123
Analytics service logs req-123
```

Wrong:

```text
Gateway logs req-123
URL service logs req-123
Analytics service logs random-999
```

---

## 16. Kafka Propagation

HTTP request may publish a Kafka event:

```text
URL created
Click tracked
Analytics event
```

Correlation ID must go into Kafka headers.

Producer ASCII:

```text
HTTP Thread MDC
 correlationId=req-123
        |
        v
Kafka ProducerRecord headers
 X-Correlation-Id=req-123
        |
        v
Kafka topic
```

Producer example:

```java
package com.miniurl.shortener.analytics;

import com.miniurl.shortener.common.logging.CorrelationIdFilter;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class ClickEventPublisher {

    private final KafkaTemplate<String, ClickEvent> kafkaTemplate;

    public ClickEventPublisher(KafkaTemplate<String, ClickEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ClickEvent event) {
        ProducerRecord<String, ClickEvent> record =
                new ProducerRecord<>("url-click-events", event.shortCode(), event);

        String correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY);
        if (correlationId != null) {
            record.headers().add(
                    CorrelationIdFilter.CORRELATION_ID_HEADER,
                    correlationId.getBytes(StandardCharsets.UTF_8)
            );
        }

        kafkaTemplate.send(record);
    }
}
```

Consumer must restore MDC:

```java
package com.miniurl.shortener.analytics;

import com.miniurl.shortener.common.logging.CorrelationIdFilter;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class ClickEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ClickEventConsumer.class);

    @KafkaListener(topics = "url-click-events", groupId = "analytics-worker")
    public void consume(
            ClickEvent event,
            @Header(name = "X-Correlation-Id", required = false) byte[] correlationIdBytes
    ) {
        String correlationId = correlationIdBytes == null
                ? "kafka-" + java.util.UUID.randomUUID()
                : new String(correlationIdBytes, StandardCharsets.UTF_8);

        try {
            MDC.put(CorrelationIdFilter.CORRELATION_ID_MDC_KEY, correlationId);
            log.info("event=CLICK_EVENT_CONSUMED shortCode={}", event.shortCode());

            // process analytics

            log.info("event=CLICK_EVENT_PROCESSED shortCode={}", event.shortCode());
        } finally {
            MDC.clear();
        }
    }
}
```

Important compile note:

```text
Avoid importing two different Header classes with the same name.
Use Spring Messaging @Header for listener method parameters.
```

Simpler listener signature:

```java
import org.springframework.messaging.handler.annotation.Header;
```

Then:

```java
@Header(name = "X-Correlation-Id", required = false) byte[] correlationIdBytes
```

---

## 17. API Gateway / Ingress / EKS Flow

In production, request may pass through:

```text
Client
  -> AWS Load Balancer
  -> Kubernetes Ingress
  -> Spring Cloud Gateway
  -> URL service pod
  -> Redis/Postgres/Kafka
  -> worker pod
```

ASCII:

```text
+--------+
| Client |
+--------+
    |
    | X-Correlation-Id=req-123
    v
+-------------------+
| AWS ALB           |
+-------------------+
    |
    v
+-------------------+
| K8s Ingress       |
+-------------------+
    |
    v
+-------------------+
| Spring Gateway    | log req-123
+-------------------+
    |
    v
+-------------------+
| URL Service Pod   | log req-123
+-------------------+
    |      |      |
    |      |      +----> Kafka topic header req-123
    |      +-----------> Redis
    +------------------> PostgreSQL
                         
Kafka Worker Pod logs req-123
```

In EKS, also include infrastructure fields:

```text
podName
namespace
nodeName
containerName
serviceName
version
```

These fields help answer:

```text
Did only one pod fail?
Did a new deployment version fail?
Is one node causing failures?
Is one namespace affected?
```

Kubernetes log query examples:

```bash
kubectl logs deploy/miniurl-shortener -n prod | grep req-123
kubectl logs pod/miniurl-shortener-abc123 -n prod | grep req-123
```

In real production, use centralized logs:

```text
CloudWatch Logs
Loki
ELK / OpenSearch
Datadog
New Relic
```

---

## 18. Logging Levels

Use levels consistently.

```text
TRACE: extremely detailed; almost never in production
DEBUG: developer diagnostics
INFO : normal important lifecycle event
WARN : unusual but handled condition
ERROR: failure requiring investigation
```

MiniURLShortener examples:

```text
INFO  CREATE_SHORT_URL_REQUEST_RECEIVED
INFO  SHORT_URL_CREATED
INFO  REDIRECT_SUCCESS
WARN  REDIRECT_SHORT_CODE_NOT_FOUND
WARN  CUSTOM_ALIAS_ALREADY_EXISTS
ERROR DATABASE_UNAVAILABLE
ERROR KAFKA_PUBLISH_FAILED_AFTER_RETRIES
```

Avoid:

```text
ERROR for every 404
ERROR for validation failure
INFO for every internal loop step
DEBUG enabled in production by default
```

Why 404 is usually WARN, not ERROR:

```text
A user entering a wrong short code is not a server bug.
```

Why DB down is ERROR:

```text
The service cannot fulfill valid requests.
```

---

## 19. What Not To Log

Never blindly log request bodies.

Risky data:

```text
passwords
access tokens
refresh tokens
JWTs
API keys
reset-password URLs
email verification URLs
full longUrl with query parameters
credit card data
personal identifiers
cookies
authorization headers
```

Bad:

```java
log.info("request={}", request);
```

If request contains longUrl:

```text
https://example.com/reset-password?token=secret-token
```

You leaked a secret.

Better:

```java
log.info("event=CREATE_SHORT_URL_REQUEST_RECEIVED urlHost={} urlLength={} hasCustomAlias={}",
        safeHost,
        request.getLongUrl().length(),
        hasCustomAlias);
```

Safe logging strategy:

```text
1. Log identifiers that are safe.
2. Log counts and lengths.
3. Log domains, not full URLs.
4. Log hashes when you need matching without exposing values.
5. Redact known sensitive fields.
```

Example:

```text
longUrlHash=sha256(first 12 chars)
urlHost=example.com
urlLength=128
```

---

## 20. Step-by-Step Dry Runs

### Dry Run 1: Client sends correlation ID

Request:

```bash
curl -H "X-Correlation-Id: req-abc" \
  -H "Content-Type: application/json" \
  -d '{"longUrl":"https://example.com"}' \
  http://localhost:8080/api/v1/urls
```

Flow:

```text
1. CorrelationIdFilter reads X-Correlation-Id=req-abc.
2. Filter puts correlationId=req-abc into MDC.
3. Controller logs request received with req-abc.
4. Service logs short URL created with req-abc.
5. Response includes X-Correlation-Id=req-abc.
6. Filter clears MDC.
```

Log sample:

```text
INFO [corr=req-abc] event=CREATE_SHORT_URL_REQUEST_RECEIVED
INFO [corr=req-abc] event=SHORT_URL_CREATED shortCode=aB91xZ
```

---

### Dry Run 2: Client does not send correlation ID

Request:

```bash
curl -H "Content-Type: application/json" \
  -d '{"longUrl":"https://example.com"}' \
  http://localhost:8080/api/v1/urls
```

Flow:

```text
1. Header is missing.
2. Filter generates UUID.
3. MDC gets generated ID.
4. Logs include generated ID.
5. Response returns same ID in X-Correlation-Id.
```

Response header:

```text
X-Correlation-Id: 9e86e2b1-850e-4f1b-99e0-0ef65e11b777
```

---

### Dry Run 3: Validation failure

Request:

```json
{
  "longUrl": ""
}
```

Flow:

```text
1. Filter sets correlation ID.
2. Bean Validation fails.
3. GlobalExceptionHandler returns 400 VALIDATION_FAILED.
4. Error log/response includes correlation ID header.
5. Client can report correlation ID.
```

Response:

```http
HTTP/1.1 400 Bad Request
X-Correlation-Id: req-bad-1
```

JSON:

```json
{
  "code": "VALIDATION_FAILED",
  "message": "Request validation failed"
}
```

---

### Dry Run 4: Kafka async event

Create URL request:

```text
correlationId=req-create-777
```

Flow:

```text
1. HTTP request enters service with req-create-777.
2. URL service logs creation with req-create-777.
3. Kafka producer adds X-Correlation-Id=req-create-777 to message headers.
4. Analytics worker consumes event later.
5. Consumer restores MDC from Kafka header.
6. Worker logs processing with req-create-777.
```

ASCII:

```text
URL Service Log:
  req-create-777 SHORT_URL_CREATED

Kafka Header:
  X-Correlation-Id=req-create-777

Worker Log:
  req-create-777 CLICK_EVENT_PROCESSED
```

---

### Dry Run 5: MDC leak bug

Bug:

```text
Filter sets MDC but does not clear it.
```

Flow:

```text
1. Request A handled by thread-1, MDC=corr-A.
2. Request A completes.
3. Thread-1 reused for Request B.
4. Request B missing correlation header.
5. Old corr-A remains.
6. Request B logs incorrectly show corr-A.
```

This creates false debugging evidence.

Fix:

```java
finally {
    MDC.clear();
}
```

---

## 21. Internal Execution Walkthrough

Full HTTP create request:

```text
1. Client sends POST /api/v1/urls.
2. Request reaches Tomcat.
3. CorrelationIdFilter runs once.
4. Filter reads or creates correlation ID.
5. Filter stores ID in MDC.
6. Filter writes ID to response header.
7. DispatcherServlet routes to controller.
8. Controller logs request received.
9. Bean Validation runs.
10. Service validates business rules.
11. Service calls DB/Redis/Kafka.
12. Each log line includes MDC correlationId.
13. Response returns to client.
14. Filter finally block clears MDC.
```

ASCII:

```text
HTTP Request
    |
    v
+-----------------------+
| CorrelationIdFilter   |
| MDC.put(req-123)      |
+-----------------------+
    |
    v
+-----------------------+
| Controller            |
| log req-123           |
+-----------------------+
    |
    v
+-----------------------+
| Service               |
| log req-123           |
+-----------------------+
    |
    +------> DB log req-123
    +------> Redis log req-123
    +------> Kafka header req-123
    |
    v
+-----------------------+
| Response              |
| X-Correlation-Id      |
+-----------------------+
    |
    v
finally MDC.clear()
```

---

## 22. Local Testing With curl

Start app:

```bash
mvn spring-boot:run
```

Test with custom correlation ID:

```bash
curl -i \
  -H "X-Correlation-Id: local-test-001" \
  -H "Content-Type: application/json" \
  -d '{"longUrl":"https://example.com"}' \
  http://localhost:8080/api/v1/urls
```

Expected response header:

```text
X-Correlation-Id: local-test-001
```

Expected logs:

```text
[corr=local-test-001] event=CREATE_SHORT_URL_REQUEST_RECEIVED
[corr=local-test-001] event=SHORT_URL_CREATED
```

Test generated ID:

```bash
curl -i \
  -H "Content-Type: application/json" \
  -d '{"longUrl":"https://example.com"}' \
  http://localhost:8080/api/v1/urls
```

Expected:

```text
Response contains generated X-Correlation-Id
Logs contain same ID
```

Test validation failure:

```bash
curl -i \
  -H "X-Correlation-Id: bad-url-test" \
  -H "Content-Type: application/json" \
  -d '{"longUrl":""}' \
  http://localhost:8080/api/v1/urls
```

Expected:

```text
HTTP 400
X-Correlation-Id: bad-url-test
Logs contain bad-url-test
```

---

## 23. Unit and Integration Testing

### Filter unit test idea

Test cases:

```text
1. Existing header is preserved.
2. Missing header generates new ID.
3. Response header is set.
4. MDC is cleared after request.
```

Pseudo-test:

```java
@Test
void shouldUseExistingCorrelationId() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-Correlation-Id", "test-corr-1");

    MockHttpServletResponse response = new MockHttpServletResponse();

    FilterChain chain = (req, res) -> {
        assertThat(MDC.get("correlationId")).isEqualTo("test-corr-1");
    };

    filter.doFilter(request, response, chain);

    assertThat(response.getHeader("X-Correlation-Id")).isEqualTo("test-corr-1");
    assertThat(MDC.get("correlationId")).isNull();
}
```

### MockMvc integration test

```java
mockMvc.perform(post("/api/v1/urls")
        .header("X-Correlation-Id", "mvc-corr-1")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"longUrl\":\"https://example.com\"}"))
        .andExpect(header().string("X-Correlation-Id", "mvc-corr-1"));
```

### Kafka propagation test idea

```text
1. Put correlation ID in MDC.
2. Publish Kafka event.
3. Assert ProducerRecord contains X-Correlation-Id header.
4. Consumer receives header.
5. Consumer logs/processes with MDC set.
```

Testing rule:

```text
Do not only test business output.
Test observability contract too.
```

---

## 24. Production Failure Stories

### Failure Story 1: 500 error with no searchable ID

Incident:

```text
Customer reports: Create URL failed around 10:03.
```

Logs contain thousands of lines around 10:03.

Root cause:

```text
No correlation ID returned to client.
```

Fix:

```text
Return X-Correlation-Id in every response.
Ask customer/support to include it in tickets.
```

Lesson:

```text
A customer-visible correlation ID turns vague reports into searchable evidence.
```

---

### Failure Story 2: Old correlation ID leaks into new request

Incident:

```text
Logs show two unrelated users with same correlation ID.
```

Root cause:

```text
MDC was not cleared in finally block.
Tomcat thread was reused.
```

Fix:

```text
Always MDC.clear() in finally.
```

Lesson:

```text
Thread-local data must be cleaned after request completion.
```

---

### Failure Story 3: Async worker loses request identity

Incident:

```text
URL creation logs show req-777.
Kafka worker logs show no correlation ID.
```

Root cause:

```text
Kafka producer did not propagate correlation ID in headers.
Consumer did not restore MDC.
```

Fix:

```text
Write correlation ID to Kafka headers.
Read header in consumer and put into MDC.
```

Lesson:

```text
Async boundaries break context unless you explicitly carry it.
```

---

### Failure Story 4: Sensitive URLs leaked in logs

Incident:

```text
Password reset link appears in centralized logs.
```

Root cause:

```text
Controller logged full request object.
```

Fix:

```text
Log host, length, hash, and safe metadata only.
Redact tokens and query parameters.
```

Lesson:

```text
Logs are production data. Treat them as sensitive.
```

---

### Failure Story 5: Too much logging increases cost

Incident:

```text
Logging bill spikes after enabling DEBUG in production.
```

Root cause:

```text
Detailed debug logs emitted for every request and loop.
```

Fix:

```text
Keep production default at INFO.
Use sampling for noisy logs.
Enable DEBUG temporarily for specific packages only.
```

Lesson:

```text
Logging has performance and money cost.
```

---

## 25. Debugging Mindset

When production issue happens, ask:

```text
Do I have the correlation ID?
Can I search all logs by this ID?
Which service first saw the request?
Which service returned the error?
Did the same ID reach downstream services?
Did Kafka event carry the same ID?
Which pod handled the request?
Which version was deployed?
What was the latency at each step?
Was this a known business error or unexpected exception?
Did logs expose any sensitive data?
```

Debug map:

```text
Missing correlation ID in all logs:
    filter not registered or log pattern missing MDC

Correlation ID in controller but not service:
    unlikely in same thread; check async boundary

Correlation ID in service but not Feign target:
    missing Feign interceptor

Correlation ID in producer but not consumer:
    missing Kafka header or consumer MDC restore

Wrong correlation ID reused:
    MDC.clear missing

Logs too noisy:
    wrong level, excessive INFO, DEBUG enabled
```

Useful commands:

```bash
kubectl logs deploy/miniurl-shortener -n prod | grep req-123
kubectl logs deploy/analytics-worker -n prod | grep req-123
kubectl get pods -n prod
```

With centralized logs:

```text
service="miniurl-shortener" AND correlationId="req-123"
correlationId="req-123"
correlationId="req-123" AND level="ERROR"
```

Golden rule:

```text
A production log should reduce time-to-debug, not increase noise.
```

---

## 26. Common Mistakes

### Mistake 1: Generate ID but do not return it

Wrong:

```text
Backend logs correlation ID internally only.
```

Correct:

```text
Return X-Correlation-Id response header.
```

### Mistake 2: No MDC clear

Wrong:

```java
MDC.put("correlationId", id);
filterChain.doFilter(request, response);
```

Correct:

```java
try {
    MDC.put("correlationId", id);
    filterChain.doFilter(request, response);
} finally {
    MDC.clear();
}
```

### Mistake 3: Log full request object

Wrong:

```java
log.info("request={}", request);
```

Correct:

```java
log.info("event=CREATE_SHORT_URL hasCustomAlias={} urlLength={}", hasCustomAlias, urlLength);
```

### Mistake 4: Losing ID across service calls

Wrong:

```text
Service A logs corr-A.
Service B logs corr-random.
```

Correct:

```text
Feign/Gateway forwards X-Correlation-Id.
```

### Mistake 5: Losing ID in Kafka

Wrong:

```text
HTTP logs have ID, worker logs do not.
```

Correct:

```text
Kafka headers carry X-Correlation-Id.
Consumer restores MDC.
```

### Mistake 6: ERROR for client mistakes

Wrong:

```text
ERROR invalid alias
ERROR short code not found
```

Correct:

```text
WARN or INFO for expected client/domain errors.
ERROR for system failures.
```

### Mistake 7: Logs without event names

Weak:

```text
created
failed
started
```

Strong:

```text
event=SHORT_URL_CREATED
event=KAFKA_PUBLISH_FAILED
event=REDIRECT_SHORT_CODE_NOT_FOUND
```

---

## 27. Interview-Ready Explanation

If interviewer asks:

```text
How would you implement correlation ID and logging in a Spring Boot microservice?
```

Strong answer:

```text
I would add a OncePerRequestFilter at the HTTP boundary. It reads X-Correlation-Id
from the incoming request or generates a UUID if missing. Then it puts the ID into
SLF4J MDC, adds the same ID to the response header, and clears MDC in a finally
block to avoid thread-local leakage because servlet threads are reused. The logback
pattern or JSON encoder includes MDC fields, so every log line contains the
correlation ID. For downstream calls, I propagate the same header using a Feign
RequestInterceptor or equivalent HTTP client interceptor. For Kafka, I write the
correlation ID into message headers on publish and restore it into MDC inside the
consumer before processing. I avoid logging sensitive request bodies or full URLs,
and I use consistent event names and log levels so production incidents can be
searched quickly by correlationId.
```

Why this is strong:

```text
1. Mentions HTTP boundary filter.
2. Handles missing incoming ID.
3. Uses MDC correctly.
4. Clears MDC to avoid thread reuse bugs.
5. Returns ID to client.
6. Propagates across HTTP calls.
7. Propagates across Kafka async boundary.
8. Shows security awareness about logs.
9. Shows production debugging mindset.
```

Senior one-liner:

```text
A correlation ID converts distributed logs from scattered events into one searchable request story.
```

---

## 28. Senior Engineer Checklist

Before moving forward, confirm:

```text
[ ] CorrelationIdFilter exists
[ ] Filter reads X-Correlation-Id
[ ] Filter generates UUID when missing
[ ] Filter puts correlationId into MDC
[ ] Filter adds X-Correlation-Id to response header
[ ] Filter clears MDC in finally
[ ] logback pattern includes correlationId
[ ] controller logs lifecycle events
[ ] service logs important business events
[ ] full longUrl is not logged
[ ] tokens/passwords/authorization headers are not logged
[ ] Feign interceptor propagates X-Correlation-Id
[ ] Kafka producer writes X-Correlation-Id header
[ ] Kafka consumer restores MDC from header
[ ] expected domain errors are not logged as ERROR
[ ] infrastructure failures are logged as ERROR
[ ] logs include event names
[ ] EKS logs can be searched by correlation ID
[ ] tests verify response header behavior
[ ] tests verify MDC clear behavior
```

If these are checked, your logging model is production-shaped.

---

## 29. One-Page Cheat Sheet

```text
Core mental model:
Logging records events.
Correlation ID connects events.

Header:
X-Correlation-Id

Spring Boot implementation:
OncePerRequestFilter
  read header or generate UUID
  MDC.put("correlationId", id)
  response.setHeader("X-Correlation-Id", id)
  filterChain.doFilter(...)
  finally MDC.clear()

MDC:
Thread-local map used by logger.
Must clear because servlet threads are reused.
Must restore across async boundaries.

Log fields:
timestamp
level
service
correlationId
method
path
event
status
latencyMs
exceptionClass
podName

HTTP propagation:
Feign RequestInterceptor copies MDC correlationId to X-Correlation-Id.

Kafka propagation:
Producer writes correlation ID to Kafka headers.
Consumer reads header and puts it into MDC.

Do not log:
passwords
tokens
JWTs
API keys
Authorization headers
full longUrl with query params
cookies
PII

Logging levels:
INFO  normal lifecycle
WARN  unusual but handled
ERROR system failure
DEBUG detailed local diagnostics

Debug query:
correlationId=req-123

Golden rule:
Return correlation ID to client so support can search exact logs.
```

---

## 30. One Picture To Remember

```text
             LOGGING & CORRELATION ID MENTAL MODEL

                    "One request = one story ID"

Client
  |
  | X-Correlation-Id: req-123
  v
+------------------------------------------------+
| CorrelationIdFilter                            |
| 1. read/generate ID                            |
| 2. MDC.put(correlationId=req-123)              |
| 3. response header X-Correlation-Id=req-123    |
+------------------------------------------------+
  |
  v
+-------------------------+
| Controller              |
| log req-123             |
+-------------------------+
  |
  v
+-------------------------+
| Service                 |
| log req-123             |
+-------------------------+
  |        |        |
  |        |        +----------------------+
  |        |                               v
  |        |                       +---------------+
  |        |                       | Kafka Header  |
  |        |                       | req-123       |
  |        |                       +---------------+
  |        |                               |
  |        |                               v
  |        |                       +---------------+
  |        |                       | Worker MDC    |
  |        |                       | log req-123   |
  |        |                       +---------------+
  |        v
  |   +---------+
  |   | Redis   |
  |   +---------+
  v
+------------+
| PostgreSQL |
+------------+

finally:
  MDC.clear()

Search logs:
  correlationId=req-123

Result:
  complete request story
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Logging tells what happened; correlation ID tells which request it happened to.
2. A Spring Boot filter should read or generate X-Correlation-Id and put it into MDC.
3. Always clear MDC in finally because servlet threads are reused.
4. Propagate correlation ID across HTTP calls and Kafka headers.
5. Never log secrets, tokens, or full sensitive URLs just to make debugging easier.
```

Next chapter:

```text
057_Metrics_Prometheus_Grafana.md
```
