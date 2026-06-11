# 022_Registry_Metrics_Tracing_Debugging.md

# Registry Metrics, Tracing, and Debugging

> MiniServiceDiscovery Chapter 022
>
> Goal: Learn how to monitor, trace, troubleshoot, and debug service discovery systems
> in production environments including Eureka, Consul, Kubernetes Service Discovery,
> Envoy, Istio, API Gateways, Load Balancers, and Microservices.

---

# 1. Why This Chapter Matters

Building service discovery is only half the job.

The harder problem is:

```text
Why is service A unable to reach service B?
```

Production incidents are rarely caused by code alone.

Most outages involve:

- wrong service registration
- stale discovery cache
- unhealthy instances
- DNS failures
- network partitions
- bad routing rules
- certificate issues
- timeout misconfiguration
- load-balancer failures
- cascading failures
- slow databases
- retry storms

A senior engineer must answer:

```text
What is broken?
Where is it broken?
Why is it broken?
How do we prove it?
```

That is where metrics, tracing, logs, and debugging become critical.

---

# 2. Production Mental Model

Every request leaves evidence.

Think of production observability as:

```text
Metrics
   ↓
Something looks wrong

Tracing
   ↓
Find where request slowed

Logs
   ↓
Find exact failure

Debugging
   ↓
Fix root cause
```

Golden rule:

```text
Metrics tell WHAT.
Tracing tells WHERE.
Logs tell WHY.
Debugging tells HOW TO FIX.
```

---

# 3. Four Pillars of Observability

Modern microservices use:

```text
Metrics
Logs
Tracing
Profiling
```

Visualization:

```text
            Observability
                  |
    --------------------------------
    |              |             |
 Metrics        Logs         Traces
    |
 Profiling
```

---

# 4. Metrics Fundamentals

Metrics are numerical measurements over time.

Examples:

```text
CPU usage
Memory usage
RPS
Latency
Error rate
Connection count
Registry size
```

Metrics answer:

```text
Is system healthy?
```

Not:

```text
Why did request fail?
```

---

# 5. Service Discovery Metrics

Registry systems expose critical metrics.

Example:

```text
registered_instances_total
healthy_instances_total
heartbeat_failures_total
registry_updates_total
registry_size
```

Visualization:

```text
Registry
   |
   +-- instances = 500
   +-- healthy = 498
   +-- unhealthy = 2
```

If healthy instances suddenly drop:

```text
500 -> 300
```

You immediately know discovery is unhealthy.

---

# 6. Registry Health Metrics

Most important discovery metrics:

## Registration Rate

```text
registrations/sec
```

Measures:

```text
How many services join cluster?
```

Example:

```text
20 new registrations/minute
```

---

## Deregistration Rate

```text
deregistrations/sec
```

Measures:

```text
How many services leave cluster?
```

Large spikes may indicate:

- crash loops
- node failures
- network partition

---

## Heartbeat Success Rate

```text
successful_heartbeats / total_heartbeats
```

Example:

```text
99.99%
```

If it falls:

```text
99.99 → 70%
```

Something is seriously wrong.

---

## Registry Propagation Delay

Measures:

```text
How long before all nodes see updates?
```

Example:

```text
50ms
```

Bad:

```text
10 seconds
```

Leads to stale routing.

---

# 7. Golden Signals

Google SRE popularized:

```text
Latency
Traffic
Errors
Saturation
```

Remember:

```text
L T E S
```

---

## Latency

```text
Request duration
```

Examples:

```text
p50 = 10ms
p95 = 40ms
p99 = 120ms
```

Never rely only on average latency.

Example:

```text
Average = 20ms
p99 = 3 seconds
```

Users still suffer.

---

## Traffic

Examples:

```text
RPS
QPS
connections
bytes/sec
```

Questions:

```text
Are we overloaded?
```

---

## Errors

Examples:

```text
HTTP 500
HTTP 503
gRPC errors
Timeouts
```

Formula:

```text
error_rate =
failed_requests / total_requests
```

---

## Saturation

Measures resource exhaustion.

Examples:

```text
CPU
memory
thread pool
DB pool
queue depth
```

---

# 8. Prometheus Mental Model

Prometheus is the most common metrics system.

Flow:

```text
Application
     |
     v
/metrics endpoint
     |
     v
Prometheus scrape
     |
     v
Time Series DB
     |
     v
Grafana
```

---

# 9. Example Metrics

Spring Boot:

```java
Counter requests =
Counter.builder("orders_created")
       .register(meterRegistry);
```

Increment:

```java
requests.increment();
```

Produces:

```text
orders_created_total 10234
```

---

# 10. Important Discovery Metrics Dashboard

Always monitor:

```text
Registry size
Healthy instances
Unhealthy instances
Heartbeat failures
Registration failures
Lookup latency
DNS failures
Service resolution latency
```

---

# 11. Discovery Latency

Critical metric:

```text
How fast can service be found?
```

Example:

```text
lookup = 5ms
```

Bad:

```text
lookup = 500ms
```

Every request becomes slower.

---

# 12. Cache Metrics

Client-side discovery often uses cache.

Monitor:

```text
cache_hit_rate
cache_miss_rate
cache_evictions
```

Example:

```text
Hit Rate = 99%
```

Good.

Example:

```text
Hit Rate = 50%
```

Bad.

---

# 13. DNS Metrics

For Kubernetes discovery:

Monitor:

```text
CoreDNS latency
DNS errors
NXDOMAIN responses
Cache hit ratio
```

Example:

```text
coredns_dns_request_duration
```

Spike means:

```text
service discovery slowdown
```

---

# 14. Tracing Fundamentals

Metrics show:

```text
Something is slow.
```

Tracing shows:

```text
Which service is slow.
```

Example request:

```text
Client
  |
  v
Order Service
  |
  v
Payment Service
  |
  v
Database
```

Metrics show:

```text
Request = 3 sec
```

Tracing shows:

```text
Order = 20ms
Payment = 50ms
DB = 2930ms
```

Root cause found.

---

# 15. Distributed Tracing Mental Model

A request receives:

```text
Trace ID
```

Every service call propagates it.

Example:

```text
TraceID = abc123
```

Flow:

```text
Client
  |
  v
Order
  |
  v
Payment
  |
  v
Inventory
```

All logs contain:

```text
abc123
```

Now entire request can be reconstructed.

---

# 16. Span

A span is one operation.

Example:

```text
Trace
 |
 +-- Span 1 Order API
 |
 +-- Span 2 Payment API
 |
 +-- Span 3 SQL Query
```

Visualization:

```text
Trace
 ├── Order
 ├── Payment
 └── SQL
```

---

# 17. OpenTelemetry

Industry standard.

Provides:

```text
Metrics
Tracing
Logs
```

Flow:

```text
App
 |
 v
OpenTelemetry SDK
 |
 v
Collector
 |
 v
Jaeger
Tempo
Zipkin
Datadog
New Relic
```

---

# 18. Trace Context Propagation

HTTP example:

```http
traceparent:
00-abcd1234-xyz987-01
```

Every downstream service receives it.

Without propagation:

```text
Broken traces
```

---

# 19. Jaeger

Popular tracing platform.

Provides:

```text
Trace search
Latency analysis
Dependency graph
Timeline
```

Typical workflow:

```text
Find slow request
Open trace
Locate slow span
```

---

# 20. Example Trace Analysis

Request:

```text
GET /orders/100
```

Timeline:

```text
Order API      20ms
Payment API    35ms
Inventory API  25ms
Database       3000ms
```

Root cause:

```text
Database
```

Not service discovery.

---

# 21. Discovery Trace Example

Example:

```text
Order Service
  |
  + DNS Lookup 500ms
  |
  + Payment Call 50ms
```

Trace immediately reveals:

```text
DNS resolution issue
```

---

# 22. Registry Logs

Metrics show symptoms.

Logs show details.

Example:

```text
Instance payment-12 registered
```

Example:

```text
Heartbeat missed
```

Example:

```text
Lease expired
```

Example:

```text
Service removed
```

---

# 23. Useful Registry Log Events

Monitor:

```text
Registration
Deregistration
Heartbeat
Lease expiration
Registry sync
Replication failure
Network partition
```

---

# 24. Eureka Debugging

Common commands:

Check registered apps:

```text
http://eureka-server:8761/eureka/apps
```

Verify:

```text
payment-service exists?
```

---

Check instance:

```text
UP
DOWN
STARTING
OUT_OF_SERVICE
```

---

# 25. Kubernetes Discovery Debugging

Verify service:

```bash
kubectl get svc
```

Verify endpoints:

```bash
kubectl get endpoints
```

Verify EndpointSlices:

```bash
kubectl get endpointslices
```

---

Example:

```bash
kubectl describe svc payment-service
```

Shows:

```text
selector
ports
endpoints
```

---

# 26. DNS Debugging

Launch debug pod:

```bash
kubectl run dns-test --rm -it \
--image=busybox
```

Run:

```bash
nslookup payment-service
```

Expected:

```text
10.96.1.10
```

If failure:

```text
DNS issue
```

---

# 27. Service Connectivity Debugging

Inside pod:

```bash
curl payment-service
```

or

```bash
wget
```

or

```bash
telnet
```

Verifies connectivity.

---

# 28. Network Policy Debugging

Problem:

```text
Service exists
DNS works
Still cannot connect
```

Check:

```bash
kubectl get networkpolicy
```

Often traffic blocked.

---

# 29. Istio Debugging

Very common interview topic.

Check proxy status:

```bash
istioctl proxy-status
```

---

Inspect listeners:

```bash
istioctl proxy-config listeners POD
```

---

Inspect routes:

```bash
istioctl proxy-config routes POD
```

---

Inspect clusters:

```bash
istioctl proxy-config clusters POD
```

---

Inspect endpoints:

```bash
istioctl proxy-config endpoints POD
```

---

# 30. Sidecar Verification

Verify sidecar injected.

```bash
kubectl get pod POD \
-o jsonpath='{.spec.containers[*].name}'
```

Expected:

```text
app
istio-proxy
```

---

# 31. mTLS Debugging

Verify:

```bash
istioctl authn tls-check
```

Common issue:

```text
STRICT mTLS
```

but

```text
client not using mesh
```

Connection rejected.

---

# 32. Gateway Debugging

Check ingress gateway:

```bash
kubectl get gateway
```

Check virtual services:

```bash
kubectl get virtualservice
```

Common issue:

```text
host mismatch
```

---

# 33. Load Balancer Debugging

Symptoms:

```text
Some users fail
Others succeed
```

Possible causes:

```text
One unhealthy backend
```

Check:

```text
backend health
backend weights
connection count
```

---

# 34. Retry Storm Detection

Metrics:

```text
Requests = 1000
Retries = 5000
```

Danger.

Root causes:

```text
Slow dependency
Aggressive retry policy
```

Can create cascading failure.

---

# 35. Circuit Breaker Debugging

Metrics:

```text
open_circuit_count
```

Logs:

```text
Circuit OPEN
```

Indicates dependency instability.

---

# 36. Thread Pool Saturation

Common hidden issue.

Metrics:

```text
active_threads
queue_size
rejected_tasks
```

Example:

```text
pool=200
active=200
queue=5000
```

System overloaded.

---

# 37. Connection Pool Metrics

Database pools:

```text
active connections
idle connections
wait time
```

HikariCP:

```text
hikaricp_connections_active
```

High wait time:

```text
DB bottleneck
```

---

# 38. Registry Replication Debugging

Multi-region registries replicate data.

Monitor:

```text
replication lag
replication failures
sync duration
```

Example:

```text
US registry updated
EU registry delayed 20 sec
```

Stale routing possible.

---

# 39. Network Partition Debugging

Symptoms:

```text
Instances disappear
Heartbeats fail
```

Metrics:

```text
heartbeat timeout
lease expiration
```

Logs:

```text
cannot reach registry
```

---

# 40. Debugging Workflow

Step 1

```text
Observe alert
```

Step 2

```text
Check dashboard
```

Step 3

```text
Identify spike
```

Step 4

```text
Open trace
```

Step 5

```text
Locate slow service
```

Step 6

```text
Inspect logs
```

Step 7

```text
Fix root cause
```

---

# 41. Real Incident Example

Users complain:

```text
Checkout slow
```

Metrics:

```text
Latency spike
```

Trace:

```text
Order -> Payment
```

Payment span:

```text
2.8 sec
```

Logs:

```text
Connection timeout DB
```

Root cause:

```text
Database saturation
```

---

# 42. Real Discovery Incident

Symptoms:

```text
Payment unreachable
```

Metrics:

```text
Healthy instances = 0
```

Logs:

```text
Lease expired
```

Root cause:

```text
Heartbeat network failure
```

---

# 43. Registry Dashboard Design

Must include:

```text
Healthy instances
Unhealthy instances
Registration rate
Heartbeat rate
Lookup latency
Replication lag
DNS latency
```

---

# 44. Tracing Dashboard Design

Include:

```text
Top slow services
Top failing services
Slow endpoints
Trace search
Dependency graph
```

---

# 45. Alerting Rules

Examples:

```text
Healthy instances < 90%
```

```text
Heartbeat failures > threshold
```

```text
p99 latency > 500ms
```

```text
Error rate > 5%
```

```text
Registry unavailable
```

---

# 46. Production Commands Cheat Sheet

Eureka:

```text
/actuator/health
/eureka/apps
```

Kubernetes:

```bash
kubectl get svc
kubectl get endpoints
kubectl get pods
kubectl logs
```

DNS:

```bash
nslookup
dig
```

Istio:

```bash
istioctl proxy-status
istioctl analyze
```

Linux:

```bash
netstat
ss
curl
telnet
tcpdump
```

---

# 47. Interview Answer

How would you debug service discovery failure?

Answer:

```text
First verify service registration.
Then verify health checks.
Then verify registry state.
Then verify DNS resolution.
Then verify endpoint availability.
Then verify network connectivity.
Then inspect traces.
Then inspect logs.
Finally isolate root cause and validate fix.
```

---

# 48. Real World Mental Model

Think like a doctor.

Metrics:

```text
Patient has fever.
```

Tracing:

```text
Which organ affected?
```

Logs:

```text
Exact disease.
```

Debugging:

```text
Treatment plan.
```

Same in production systems.

---

# 49. Final Summary

Metrics answer:

```text
WHAT is wrong?
```

Tracing answers:

```text
WHERE is wrong?
```

Logs answer:

```text
WHY is wrong?
```

Debugging answers:

```text
HOW to fix?
```

A production-grade service discovery platform is not complete until:

```text
Metrics exist
Tracing exists
Logs exist
Alerts exist
Runbooks exist
Debug workflow exists
```

That is what separates a toy registry from a production registry.
