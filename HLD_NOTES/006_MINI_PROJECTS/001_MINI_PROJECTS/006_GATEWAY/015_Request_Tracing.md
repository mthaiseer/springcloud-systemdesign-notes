# 015_Request_Tracing.md

# MiniGateway Phase 15 — Request Tracing

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Built Previously](#3-what-we-built-previously)
- [4. Previous Limitation](#4-previous-limitation)
- [5. What We Build](#5-what-we-build)
- [6. Current Architecture](#6-current-architecture)
- [7. Folder Structure](#7-folder-structure)
- [8. Complete Java Code](#8-complete-java-code)
- [9. How To Run](#9-how-to-run)
- [10. Dry Run](#10-dry-run)
- [11. DSA / CP Concepts Used](#11-dsa--cp-concepts-used)
- [12. System Design Relevance](#12-system-design-relevance)
- [13. Gateway Connection With This Phase](#13-gateway-connection-with-this-phase)
- [14. Production-Grade Concepts](#14-production-grade-concepts)
- [15. Scalability Discussion](#15-scalability-discussion)
- [16. Interview Notes](#16-interview-notes)
- [17. Common Bugs](#17-common-bugs)
- [18. Current Limitations](#18-current-limitations)
- [19. Next Step](#19-next-step)

---

# 1. Goal

In this phase, we build:

```text
Request Tracing
```

Purpose:

```text
Add trace ID propagation for distributed tracing.
```

This continues MiniGateway from a simple reverse proxy into a real production-style API Gateway.

---

# 2. Why This Phase Matters

A gateway is the hot path of almost every request.

Production systems rely on gateway capabilities for:

```text
routing
security
traffic protection
observability
resilience
deployment control
edge behavior
```

This phase adds one of those capabilities.

---

# 3. What We Built Previously

Earlier phases gave us the base flow:

```text
Client
  -> MiniGateway reverse proxy
  -> Backend service
  -> Response
```

Current mental model:

```text
receive request
  -> inspect request
  -> apply gateway rule
  -> select backend or reject
  -> forward request
  -> return response
```

---

# 4. Previous Limitation

```text
Distributed requests cannot be followed.
```

This limitation matters because gateways centralize cross-cutting behavior.

Without this feature, every backend service would need to solve the same problem independently.

---

# 5. What We Build

We build:

```text
TraceFilter
```

Core flow:

```text
traceId propagation
```

Behavior:

```text
request enters gateway
  -> gateway checks rule
  -> gateway updates route/filter/state
  -> gateway forwards or rejects
  -> response goes back to client
```

---

# 6. Current Architecture

```text
+------------------+
| Client           |
+--------+---------+
         |
         v
+------------------+
| MiniGateway      |
| Request Tracing  |
+--------+---------+
         |
         v
+------------------+
| Backend Service  |
+------------------+
```

Detailed pipeline:

```text
HTTP Request
  -> parser
  -> filter/routing layer
  -> load balancer/protection layer
  -> backend client
  -> response writer
```

---

# 7. Folder Structure

Recommended structure:

```text
MiniGateway/
└── src/main/java/com/minigateway/
    ├── backend/
    ├── client/
    ├── config/
    ├── http/
    ├── route/
    ├── lb/
    ├── filter/
    ├── protection/
    ├── telemetry/
    ├── edge/
    ├── server/
    └── driver/
```

Create only the packages used in this phase.

---

# 8. Complete Java Code


## 8.1 `GatewayTelemetry.java`

### Logic before this class

Telemetry records what happened at the gateway.

Production gateways emit:

```text
logs
metrics
traces
audit events
```

```java
package com.minigateway.telemetry;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class GatewayTelemetry {
    private final AtomicLong totalRequests = new AtomicLong();
    private final AtomicLong totalErrors = new AtomicLong();

    public String correlationId(String incoming) {
        if (incoming != null && !incoming.isBlank()) {
            return incoming;
        }
        return UUID.randomUUID().toString();
    }

    public void logRequest(String method, String path, int status, long durationMs, String correlationId) {
        totalRequests.incrementAndGet();

        if (status >= 500) {
            totalErrors.incrementAndGet();
        }

        System.out.println(Instant.now() +
                " method=" + method +
                " path=" + path +
                " status=" + status +
                " durationMs=" + durationMs +
                " correlationId=" + correlationId);
    }

    public String metrics() {
        return "gateway_requests_total=" + totalRequests.get() +
                "\ngateway_errors_total=" + totalErrors.get();
    }
}
```

---

## 8.2 `Phase015Driver.java`

### Logic before this class

The driver simulates request logging, metrics, and correlation IDs.

```java
package com.minigateway.driver;

import com.minigateway.telemetry.GatewayTelemetry;

public class Phase015Driver {
    public static void main(String[] args) {
        GatewayTelemetry telemetry = new GatewayTelemetry();

        String correlationId = telemetry.correlationId(null);

        telemetry.logRequest("GET", "/users/1", 200, 12, correlationId);
        telemetry.logRequest("GET", "/orders/9", 502, 3000, correlationId);

        System.out.println();
        System.out.println(telemetry.metrics());
    }
}
```


---

# 9. How To Run

Compile:

```bash
javac -d out src/main/java/com/minigateway/**/*.java
```

Run:

```bash
java -cp out com.minigateway.driver.Phase015Driver
```

If your shell does not expand `**`, compile specific files manually or use IntelliJ.

---

# 10. Dry Run

Example:

```text
Client sends request related to: Request Tracing
```

Flow:

```text
1. Gateway receives request.
2. Gateway reads method/path/headers.
3. Gateway applies TraceFilter.
4. Gateway decides allow/deny/route/modify.
5. Gateway forwards or returns response.
```

State transition:

```text
Before:
Distributed requests cannot be followed.

After:
traceId propagation
```

---

# 11. DSA / CP Concepts Used

```text
context propagation
```

Gateway DSA mental model:

```text
routing       -> maps, tries, prefix matching
load balance  -> circular array, heap/counters
rate limiting -> token bucket, sliding window
circuit breaker -> state machine
metrics       -> counters, histograms
cache         -> hash map + TTL + LRU
```

---

# 12. System Design Relevance

This phase maps to:

```text
distributed tracing
```

In HLD interviews:

```text
Client
  -> API Gateway
  -> service discovery / load balancer
  -> backend service
```

Gateway owns many cross-cutting concerns.

---

# 13. Gateway Connection With This Phase

Real systems implementing this idea:

```text
NGINX
Envoy
Kong
Spring Cloud Gateway
AWS API Gateway
Kubernetes Ingress
Cloudflare
```

MiniGateway version:

```text
simple Java implementation
```

Production version:

```text
non-blocking IO
connection pools
dynamic config
health checks
service discovery
JWT/OAuth validation
Redis-backed rate limiting
metrics/tracing
TLS
multi-region routing
```

---

# 14. Production-Grade Concepts

Production gateway questions:

```text
What if backend is down?
What if backend is slow?
What if traffic spikes?
What if token is invalid?
What if config changes?
What if one service is overloaded?
What if we deploy a bad version?
What if logs need tracing?
```

This phase contributes one answer to those questions.

---

# 15. Scalability Discussion

Current learning implementation:

```text
simple Java classes
in-memory state
driver-based tests
```

Production implementation:

```text
Netty/event loop
async HTTP client
connection pool
timeouts
bulkheads
Redis
Kafka audit events
Prometheus metrics
distributed tracing
Kubernetes ingress
```

Bottlenecks:

```text
CPU
network sockets
backend latency
connection pool exhaustion
hot routes
large payloads
slow clients
metrics/log volume
```

---

# 16. Interview Notes

Explain gateway features using this structure:

```text
Problem
  -> Gateway feature
  -> Data structure
  -> Failure mode
  -> Production improvement
```

Common follow-ups:

```text
How do you route requests?
How do you balance traffic?
How do you handle backend failure?
How do you verify JWT?
How do you rate-limit across gateway nodes?
How do you do canary deployment?
How do you observe latency?
```

---

# 17. Common Bugs

## Bug 1 — Wrong route matched

Cause:

```text
short prefix matched before long prefix
```

Fix:

```text
use longest prefix matching
```

## Bug 2 — Dead backend selected

Cause:

```text
health status ignored
```

Fix:

```text
health checks and outlier detection
```

## Bug 3 — Gateway hangs

Cause:

```text
no timeout
```

Fix:

```text
connect timeout and read timeout
```

## Bug 4 — Duplicate writes

Cause:

```text
unsafe retry on POST
```

Fix:

```text
idempotency key
```

## Bug 5 — Missing trace context

Cause:

```text
gateway did not propagate trace/correlation headers
```

Fix:

```text
always generate and forward trace IDs
```

---

# 18. Current Limitations

Current phase is intentionally simplified.

It does not yet fully cover:

```text
HTTP/2
TLS internals
streaming body
chunked encoding
full Netty event-loop model
real OAuth2 introspection
distributed config watcher
production-grade retry policy
```

These are introduced progressively.

---

# 19. Next Step

Next file:

```text
016_Correlation_ID.md
```

Continue the MiniGateway roadmap step by step.
