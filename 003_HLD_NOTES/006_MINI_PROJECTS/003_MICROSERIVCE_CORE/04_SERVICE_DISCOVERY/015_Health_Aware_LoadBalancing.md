# 015_Health_Aware_LoadBalancing.md

# Health-Aware Load Balancing

## Learning Goals

By the end of this chapter you will understand:

- Why traditional load balancing fails
- How healthy instances are selected
- Active vs Passive health checks
- Health score based routing
- Eureka health-aware routing
- Kubernetes readiness/liveness probes
- Service Mesh health routing
- Production-grade health-aware algorithms
- Real-world Netflix/Uber patterns

---

# 1. The Core Problem

Imagine:

User-Service

Instance A
Instance B
Instance C

Round Robin:

Req1 -> A
Req2 -> B
Req3 -> C

Everything works.

Suddenly:

Instance B loses DB connectivity.

Application still running.

Traditional LB still sends traffic.

Result:

500 Errors
Timeouts
Retries

Customers suffer.

---

# 2. Why Traditional Load Balancing Fails

Traditional LB assumes:

All registered instances are healthy.

Reality:

Registered != Healthy

Examples:

- Database unavailable
- Kafka unavailable
- Memory exhausted
- CPU 100%
- Long GC pause
- Dependency failure

The instance exists.

But cannot serve traffic.

---

# 3. Mental Model

Traditional LB:

Request
 ↓
Choose Server
 ↓
Forward

Health-Aware LB:

Request
 ↓
Filter Healthy Servers
 ↓
Apply Algorithm
 ↓
Forward

Health filtering happens BEFORE load balancing.

---

# 4. Cascading Failure Scenario

Suppose:

10 instances.

2 become unhealthy.

Load Balancer continues routing.

More failures occur.

Clients retry.

Retry traffic increases.

Healthy nodes overloaded.

More failures.

System collapse.

This is called:

Cascading Failure.

---

# 5. Zombie Instance Problem

Zombie Instance:

Still registered
But not functional

Examples:

- Deadlock
- DB disconnected
- Dependency unavailable

Registry thinks:

UP

Reality:

DOWN

Health-aware routing removes zombies.

---

# 6. Service Instance Model

```java
public class ServiceInstance {

    private String instanceId;

    private String host;

    private int port;

    private boolean healthy;

    private long lastHeartbeat;

    private int healthScore;
}
```

---

# 7. Health State Machine

HEALTHY
 ↓
SUSPECT
 ↓
UNHEALTHY
 ↓
RECOVERING
 ↓
HEALTHY

This prevents frequent state oscillation.

---

# 8. Active Health Checks

Load balancer actively checks service.

Example:

Every 10 seconds

LB -> /health

Response:

200 OK

Healthy

Advantages:

- Fast detection
- Accurate

Disadvantages:

- Extra network calls

---

# 9. Passive Health Checks

Observe real traffic.

If:

- Error rate high
- Timeout rate high

Mark unhealthy.

Advantages:

No extra traffic

Disadvantages:

Slow detection

---

# 10. Hybrid Health Detection

Production systems use:

Active + Passive

Reason:

Best accuracy.

Netflix and Envoy follow similar models.

---

# 11. Failure Threshold

Bad Design:

1 failure
→ unhealthy

Problem:

Network glitches happen.

Good Design:

5 failures

FAIL
FAIL
FAIL
FAIL
FAIL

Then remove.

---

# 12. Recovery Threshold

Bad:

1 success
→ healthy

Good:

3 successes

SUCCESS
SUCCESS
SUCCESS

Then restore.

---

# 13. Health-Aware Round Robin

Instances:

A Healthy
B Healthy
C Unhealthy

Filter:

A
B

Round Robin:

A
B
A
B

C receives zero traffic.

---

# 14. Health-Aware Weighted Round Robin

Weights:

A = 10
B = 5
C = 20

C unhealthy.

Remaining:

A = 10
B = 5

Traffic redistributed.

---

# 15. Health-Aware Least Connection

Connections:

A = 80
B = 20
C = 5

C unhealthy.

Ignore C.

Choose B.

Health filtering first.

---

# 16. Health-Aware Consistent Hashing

Normally:

User123 → Node3

If Node3 unhealthy:

Hash Ring Rebalances

Traffic moves to next healthy node.

Useful in:

- Redis
- Cassandra
- DynamoDB

---

# 17. Thread Safe Registry

```java
private final ConcurrentHashMap<
    String,
    CopyOnWriteArrayList<ServiceInstance>
> registry = new ConcurrentHashMap<>();
```

Benefits:

- Concurrent reads
- Safe updates
- Lock-free lookups

---

# 18. Health Checker Scheduler

```java
ScheduledExecutorService executor =
    Executors.newScheduledThreadPool(4);

executor.scheduleAtFixedRate(
    this::checkHealth,
    0,
    10,
    TimeUnit.SECONDS
);
```

---

# 19. Health Endpoint

```java
@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "UP";
    }
}
```

---

# 20. Spring Boot Actuator

Dependency:

spring-boot-starter-actuator

Endpoint:

/actuator/health

Response:

{
 "status":"UP"
}

---

# 21. Readiness Probe

Question:

Can traffic be routed?

Example:

Application started.

DB not connected.

Ready?

NO

Do not route.

---

# 22. Liveness Probe

Question:

Is process alive?

If failed:

Restart container.

---

# 23. Startup Probe

Question:

Has startup completed?

Useful for:

- Slow JVM startup
- Cache warmup

---

# 24. Kubernetes Example

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
```

When readiness fails:

Pod removed from service endpoints.

Traffic stops.

---

# 25. CoreDNS Flow

Pod Healthy

↓

Endpoint Added

↓

DNS Updated

↓

Traffic Allowed

Unhealthy

↓

Endpoint Removed

↓

Traffic Blocked

---

# 26. Eureka Health Checks

Without health checks:

Instance Registered
→ Traffic Routed

With health checks:

Unhealthy
→ Removed

Property:

eureka.client.healthcheck.enabled=true

---

# 27. Spring Cloud LoadBalancer

Flow:

Feign Client
 ↓
Service Registry
 ↓
Healthy Instances
 ↓
Load Balancer
 ↓
Chosen Instance

---

# 28. Service Mesh Health Routing

Envoy Sidecar:

Collects:

- Errors
- Latency
- Timeouts

Bad endpoints removed.

---

# 29. Istio Outlier Detection

If instance:

- High errors
- High latency

Istio ejects instance.

Temporary removal.

Automatic recovery later.

---

# 30. Health Score Model

Binary Health:

Healthy / Unhealthy

Modern systems:

0-100 Score

Example:

Latency Score = 30
CPU Score = 20
Error Score = 40

Total = 90

Higher score gets more traffic.

---

# 31. Production Metrics

Track:

- Healthy Instances
- Unhealthy Instances
- Success Rate
- Error Rate
- Timeout Rate
- p95
- p99
- Health Check Latency

---

# 32. Real Netflix Example

100 instances.

5 nodes:

- GC pause
- Slow DB

Health system detects.

Traffic diverted.

Users unaffected.

---

# 33. Real Uber Example

Driver Service:

One AZ degraded.

Health checks fail.

Traffic routed to healthy AZ.

System survives.

---

# 34. Real Amazon Example

Checkout Service:

Dependency unhealthy.

Readiness probe fails.

Pod removed.

Traffic shifted.

Purchases continue.

---

# 35. Common Failure Modes

Mistakes:

1. Health endpoint only checks JVM
2. DB not checked
3. Kafka not checked
4. No recovery threshold
5. Very frequent checks
6. Very slow checks

---

# 36. Interview Questions

Q: Why not route to unhealthy instances?

Because they increase failures and retries.

Q: Active vs Passive?

Active:
Proactive checks

Passive:
Observe production traffic

Q: Why readiness probe?

Prevent traffic before application is ready.

---

# 37. Production Checklist

✓ Health endpoint

✓ Active checks

✓ Passive checks

✓ Readiness probe

✓ Liveness probe

✓ Startup probe

✓ Failure threshold

✓ Recovery threshold

✓ Metrics

✓ Alerting

✓ Health-aware LB

✓ Circuit breaker integration

✓ Service mesh integration

---

# 38. Complete Mental Model

Registry
 ↓
Health Detection
 ↓
Healthy Instance Set
 ↓
Load Balancer
 ↓
Request Routing

Rule:

Health Filtering
ALWAYS happens before
Load Balancing.

---

# Final Cheat Sheet

Health-Aware Load Balancing

Goal:
Never send traffic to unhealthy nodes.

Health Sources:

- Heartbeat
- Active Checks
- Passive Checks
- Readiness Probe
- Liveness Probe

Algorithms:

- Round Robin
- Weighted Round Robin
- Least Connection
- Consistent Hashing

Production Rule:

Healthy Instance Filter
→ Load Balancing
→ Route Request

Not

Load Balancing
→ Discover Failure Later
