# MiniRedis Phase 30 — Load Testing With k6 (Rich Version)

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Load Testing Exists](#2-why-load-testing-exists)
- [3. Blind Performance Problem](#3-blind-performance-problem)
- [4. Load Testing Mental Model](#4-load-testing-mental-model)
- [5. Core Performance Metrics Explained](#5-core-performance-metrics-explained)
- [6. Throughput vs Latency Explained](#6-throughput-vs-latency-explained)
- [7. Deep Internal Data Structure Explanation](#7-deep-internal-data-structure-explanation)
- [8. Complete Java Code](#8-complete-java-code)
- [9. Step-by-Step Dry Run](#9-step-by-step-dry-run)
- [10. Internal Memory Visualization](#10-internal-memory-visualization)
- [11. Complexity Analysis](#11-complexity-analysis)
- [12. Real Production Use Cases](#12-real-production-use-cases)
- [13. Redis Production Internals](#13-redis-production-internals)
- [14. Failure Cases And Bottlenecks](#14-failure-cases-and-bottlenecks)
- [15. Interview Questions](#15-interview-questions)
- [16. Final Mental Model](#16-final-mental-model)

---

# 1. Goal

In this phase, we build:

```text
Load Testing With k6
```

Main objective:

```text
Benchmark MiniRedis under concurrent traffic.
```

Mental model:

```text
simulate thousands of users
measure how Redis behaves under pressure
```

This phase introduces:

```text
throughput testing
latency analysis
stress testing
bottleneck detection
performance benchmarking
```

Core tool:

```text
k6
```

Production systems heavily use:

```text
k6
JMeter
wrk
Locust
Gatling
JMH
```

Real-world analogy:

```text
Before opening a shopping mall,
simulate thousands of visitors
to ensure elevators, billing, and parking survive peak traffic.
```

Load testing does the same for backend systems.

---

# 2. Why Load Testing Exists

A system working for:

```text
1 user
```

does NOT guarantee it works for:

```text
100,000 concurrent users
```

Without load testing:

```text
production failures happen unexpectedly
```

Common production problems:

```text
CPU spikes
high latency
GC pauses
connection exhaustion
memory leaks
thread contention
network saturation
```

Load testing reveals these problems BEFORE production.

This is critical for:

```text
scalability
capacity planning
SRE
performance engineering
```

---

# 3. Blind Performance Problem

Suppose MiniRedis handles:

```text
GET user:1
```

fast for one client.

But what happens under:

```text
50,000 requests/second?
```

Without benchmarking:

```text
we do not know:
maximum throughput
latency under pressure
breaking point
resource bottlenecks
```

This is dangerous.

Production systems must answer:

```text
How much traffic can system handle?
What happens at peak load?
What fails first?
How does latency degrade?
```

Load testing gives these answers.

---

# 4. Load Testing Mental Model

Architecture:

```text
k6 Virtual Users
        |
        v
MiniRedis Server
        |
        +-------------------+
        |                   |
        v                   v
Command Execution      Metrics Collection
        |
        v
Latency / Throughput Results
```

Load testing flow:

```text
1. generate concurrent traffic
2. execute Redis commands
3. measure latency
4. measure throughput
5. identify bottlenecks
6. analyze scaling behavior
```

Important:

```text
performance must be measured
not guessed
```

---

# 5. Core Performance Metrics Explained

# Throughput (QPS)

Meaning:

```text
Queries Per Second
```

Example:

```text
100,000 GET requests per second
```

Higher throughput means:

```text
system handles more traffic
```

---

# Average Latency

Meaning:

```text
average response time
```

Example:

```text
2 ms average
```

But averages can hide problems.

---

# p95 Latency

Meaning:

```text
95% requests finished below this latency
```

Example:

```text
p95 = 15ms
```

Meaning:

```text
95% requests faster than 15ms
```

---

# p99 Latency

Most important metric.

Meaning:

```text
99% requests below this latency
```

Why important?

Because users feel:

```text
tail latency spikes
```

not averages.

---

# Error Rate

Meaning:

```text
failed requests percentage
```

Example:

```text
5% errors under high load
```

Means:

```text
system unstable
```

---

# Virtual Users (VUs)

In k6:

```text
VUs = concurrent simulated users
```

Example:

```text
1000 VUs
```

means:

```text
1000 concurrent workers generating traffic
```

---

# 6. Throughput vs Latency Explained

Very important backend concept.

# Throughput

Measures:

```text
how much work system handles
```

# Latency

Measures:

```text
how long one request takes
```

Tradeoff:

```text
higher throughput often increases latency
```

Example:

```text
10 users -> 2ms latency
1000 users -> 20ms latency
10000 users -> 500ms latency
```

Eventually system collapses.

Load testing helps find:

```text
safe operating range
```

---

# 7. Deep Internal Data Structure Explanation

MiniRedis load test uses:

```text
k6 virtual users
```

Each VU repeatedly sends requests.

Server internally uses:

```java
ThreadPoolExecutor
ConcurrentHashMap
AtomicLong metrics
```

Load testing stresses:

```text
network
CPU
memory
GC
thread scheduling
lock contention
```

MetricsRegistry tracks:

```text
QPS
latency
errors
```

k6 internally collects:

```text
request duration
checks
iterations
failure rate
throughput
```

---

# Why ConcurrentHashMap Matters

Under high concurrency:

```text
many threads access Redis store simultaneously
```

Without thread-safe structures:

```text
race conditions
corrupted data
crashes
```

ConcurrentHashMap provides:

```text
high concurrent read/write performance
```

---

# Thread Pool Under Load

Suppose:

```text
5000 concurrent requests
```

Thread pool decides:

```text
how requests are scheduled
```

Bad thread pool settings cause:

```text
context switching
queue buildup
OOM
latency spikes
```

Load testing reveals this quickly.

---

# 8. Complete Java Code

## 8.1 MetricsRegistry.java

### Logic Before Code

MetricsRegistry tracks runtime performance metrics.

```java
package com.miniredis.metrics;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe metrics registry.
 */
public class MetricsRegistry {

    private final AtomicLong commands =
            new AtomicLong();

    private final AtomicLong totalLatencyNanos =
            new AtomicLong();

    private final AtomicLong errors =
            new AtomicLong();

    /**
     * Record successful command.
     */
    public void recordCommand(
            long latencyNanos
    ) {

        commands.incrementAndGet();

        totalLatencyNanos.addAndGet(
                latencyNanos
        );
    }

    /**
     * Record failed command.
     */
    public void recordError() {

        errors.incrementAndGet();
    }

    /**
     * Average latency in ms.
     */
    public double averageLatencyMillis() {

        long totalCommands =
                commands.get();

        if (totalCommands == 0) {
            return 0;
        }

        return (totalLatencyNanos.get()
                / 1_000_000.0)
                / totalCommands;
    }

    public String report() {

        return "\n===== LOAD TEST METRICS =====\n"
                + "commands      = "
                + commands.get() + "\n"
                + "errors        = "
                + errors.get() + "\n"
                + "avg latency   = "
                + averageLatencyMillis()
                + " ms\n"
                + "=============================\n";
    }
}
```

---

## 8.2 MiniRedisHttpServer.java

### Logic Before Code

This is a tiny HTTP wrapper around MiniRedis.

Purpose:

```text
allow k6 HTTP benchmarking
```

Real Redis uses TCP RESP protocol.

MiniRedis phase uses HTTP for easier k6 integration.

```java
package com.miniredis.server;

import com.miniredis.metrics.MetricsRegistry;

import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Minimal HTTP wrapper for load testing.
 */
public class MiniRedisHttpServer {

    public static void main(String[] args)
            throws Exception {

        MetricsRegistry metrics =
                new MetricsRegistry();

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(8080),
                        0
                );

        // --------------------------------
        // SIMPLE PING ENDPOINT
        // --------------------------------

        server.createContext(
                "/ping",
                exchange -> {

                    long start =
                            System.nanoTime();

                    try {

                        String response = "PONG";

                        exchange.sendResponseHeaders(
                                200,
                                response.length()
                        );

                        OutputStream os =
                                exchange.getResponseBody();

                        os.write(
                                response.getBytes()
                        );

                        os.close();

                    } catch (Exception e) {

                        metrics.recordError();

                    } finally {

                        long latency =
                                System.nanoTime() - start;

                        metrics.recordCommand(
                                latency
                        );
                    }
                }
        );

        server.setExecutor(
                java.util.concurrent.Executors
                        .newFixedThreadPool(32)
        );

        server.start();

        System.out.println(
                "MiniRedis HTTP server running on port 8080"
        );

        // --------------------------------
        // PRINT METRICS EVERY 5 SECONDS
        // --------------------------------

        while (true) {

            Thread.sleep(5000);

            System.out.println(
                    metrics.report()
            );
        }
    }
}
```

---

## 8.3 miniredis.js (k6 Script)

### Logic Before Code

This script generates concurrent traffic.

Purpose:

```text
measure throughput and latency
```

```javascript
import http from 'k6/http';

import { sleep, check } from 'k6';

/**
 * k6 load test configuration
 */
export const options = {

  vus: 100,

  duration: '30s',
};

/**
 * Virtual user execution loop
 */
export default function () {

  // --------------------------------
  // SEND REQUEST
  // --------------------------------

  const res =
      http.get(
          'http://localhost:8080/ping'
      );

  // --------------------------------
  // VALIDATE RESPONSE
  // --------------------------------

  check(res, {

    'status is 200': r => r.status === 200,

    'body is PONG': r => r.body === 'PONG',
  });

  // --------------------------------
  // THINK TIME
  // --------------------------------

  sleep(0.1);
}
```

---

## 8.4 Phase030Driver.java

### Logic Before Code

This driver only explains execution order.

```java
package com.miniredis.driver;

/**
 * Execution order:
 *
 * 1. Start MiniRedisHttpServer
 * 2. Run k6 script
 * 3. Observe metrics
 */
public class Phase030Driver {

    public static void main(String[] args) {

        System.out.println(
                "Start MiniRedisHttpServer first."
        );

        System.out.println(
                "Then run:"
        );

        System.out.println(
                "k6 run miniredis.js"
        );
    }
}
```

---

# 9. Step-by-Step Dry Run

## Step 1 — Start HTTP Server

Code:

```bash
java MiniRedisHttpServer
```

Execution:

```text
1. open port 8080
2. create /ping endpoint
3. initialize thread pool
4. wait for requests
```

---

## Step 2 — Start k6 Load Test

Command:

```bash
k6 run miniredis.js
```

Execution:

```text
1. create 100 virtual users
2. each user repeatedly sends GET /ping
3. requests hit MiniRedis server
4. metrics update continuously
```

---

## Step 3 — Request Execution

Example request:

```text
GET /ping
```

Server execution:

```text
1. start latency timer
2. generate PONG response
3. record metrics
4. send response
```

---

## Step 4 — Metrics Output

Example:

```text
===== LOAD TEST METRICS =====
commands      = 120000
errors        = 0
avg latency   = 1.5 ms
=============================
```

Meaning:

```text
MiniRedis handled 120k requests
with low latency and no failures
```

---

## Step 5 — Increase Load

Change:

```javascript
vus: 100
```

to:

```javascript
vus: 5000
```

Possible results:

```text
higher latency
thread saturation
CPU spike
GC pause
errors
```

This reveals scaling bottlenecks.

---

# 10. Internal Memory Visualization

```text
k6 Virtual Users
 ├── VU-1
 ├── VU-2
 ├── VU-3
 └── ...

HTTP Thread Pool
 ├── worker-1
 ├── worker-2
 ├── worker-3
 └── ...

MetricsRegistry
 ├── commands
 ├── errors
 └── latency
```

---

# 11. Complexity Analysis

| Operation | Complexity | Reason |
|---|---|---|
| HTTP request | O(1) | simple response |
| Metrics update | O(1) | AtomicLong |
| Thread scheduling | depends | executor behavior |
| Throughput scaling | limited by CPU/network | system bottlenecks |

Load testing complexity focuses on:

```text
resource contention
```

not algorithmic Big-O alone.

---

# 12. Real Production Use Cases

## Redis Benchmarking

Measure Redis throughput under load.

## Capacity Planning

Determine maximum safe QPS.

## Stress Testing

Find system breaking point.

## Regression Testing

Verify performance after code changes.

## Infrastructure Validation

Test autoscaling and failover behavior.

---

# 13. Redis Production Internals

Real Redis benchmarking tools:

```text
redis-benchmark
memtier_benchmark
wrk
k6
```

Production Redis performance depends on:

```text
event loop efficiency
memory layout
network stack
pipelining
CPU cache locality
```

MiniRedis version:

```text
Java HTTP wrapper
```

Real Redis version:

```text
highly optimized TCP event-driven architecture
```

Production load testing also measures:

```text
p95
p99
tail latency
throughput saturation
```

---

# 14. Failure Cases And Bottlenecks

## Problem 1 — Thread Pool Saturation

Too many concurrent requests.

Result:

```text
queue buildup
latency spike
```

Fix:

```text
better async model
event loop
backpressure
```

---

## Problem 2 — GC Pause

Too many allocations under load.

Result:

```text
latency spikes
```

Fix:

```text
reduce allocations
optimize heap
```

---

## Problem 3 — Network Saturation

NIC bandwidth exhausted.

Fix:

```text
load balancing
horizontal scaling
```

---

## Problem 4 — Lock Contention

Too many threads compete for shared resource.

Fix:

```text
lock-free structures
partitioning
sharding
```

---

## Problem 5 — Coordinated Omission

Benchmark tool hides worst latency.

Fix:

```text
careful load generation
tail-aware benchmarking
```

---

# 15. Interview Questions

## Q1

Why load testing important?

Answer:

```text
validate scalability and identify bottlenecks before production
```

## Q2

Difference between throughput and latency?

Answer:

```text
throughput = amount of work
latency = time per request
```

## Q3

Why p99 latency important?

Answer:

```text
tail latency impacts real users
```

## Q4

What causes latency spikes under load?

Answer:

```text
GC
CPU saturation
thread contention
network bottlenecks
```

## Q5

Why use virtual users in k6?

Answer:

```text
simulate concurrent traffic realistically
```

---

# 16. Final Mental Model

```text
Load testing
   -> scalability validation

Metrics
   -> performance visibility

Benchmarking
   -> production readiness
```

Load testing teaches:

```text
throughput
latency
tail latency
resource contention
capacity planning
performance engineering
SRE thinking
```
