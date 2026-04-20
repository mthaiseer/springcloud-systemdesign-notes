# Failure Detection & Heartbeats in Distributed Systems – One-Stop Reference

> Concise but practical notes on **failure detection**, **heartbeats**, **timeouts**, **phi accrual**, **gossip/SWIM**, and **working Spring Boot / SQL examples** you can implement from scratch.

---

## Why Failure Detection Matters

In distributed systems, nodes fail constantly:
- servers crash
- processes hang
- GC pauses happen
- networks partition
- packets get delayed or lost

The problem is not **if** failures happen.  
The problem is **how quickly and safely you detect them**.

### Why it matters
Failure detection is the trigger for:
- failover
- leader election
- removing bad nodes from load balancers
- reassigning work
- alerting operators
- restarting containers/pods

```text
Fast detection -> fast recovery
Wrong detection -> false failovers, split-brain, instability
```

---

# 1) What Is Failure Detection?

Failure detection is the mechanism by which one node (or monitor) decides whether another node is:

- alive
- unhealthy
- slow
- suspected dead
- failed

## Important truth
```text
No response does NOT mean dead.
It means unknown.
```

If Node A doesn’t hear back from Node B, B might be:
- crashed
- overloaded
- paused by GC
- partitioned
- alive but network-delayed

That is why practical systems often work with **suspicion**, not certainty.

---

# 2) Why Failure Detection Is Hard

## 2.1 The uncertainty problem
In distributed systems, you cannot perfectly distinguish:
- slow node
- dead node
- dropped packet
- network partition

This is a fundamental limitation.

---

## 2.2 Asynchronous networks
Real networks do not give you a hard upper bound on delay.

A message may arrive in:
- 1 ms
- 100 ms
- 2 seconds
- or never

So a timeout is always a **guess** based on acceptable risk.

---

## 2.3 The main trade-off

### Fast detection
- lower timeout
- faster failover
- but more false positives

### Accurate detection
- higher timeout
- fewer false positives
- but slower recovery

```text
You cannot maximize both speed and certainty.
```

---

# 3) How Heartbeats Work

A heartbeat is a periodic signal that says:

```text
I am still alive
```

---

## 3.1 Push model
The node pushes heartbeats to a monitor or peers.

### Example
```text
Node -> Monitor: heartbeat every 1 second
```

### Pros
- simple
- monitor sees liveness continuously
- quick detection

### Cons
- more network traffic
- large clusters can create chatter

---

## 3.2 Pull model
The monitor asks nodes periodically:

```text
Are you alive?
```

### Example
```text
Monitor -> Node: ping
Node -> Monitor: pong
```

### Pros
- centralized control
- easy to manage

### Cons
- monitor can become bottleneck
- less scalable
- single point of pressure

---

## 3.3 Hybrid model
Nodes normally push heartbeats.  
If the monitor misses one, it actively probes.

### Why useful
- reduces false positives
- gives faster recovery confidence
- common in production thinking

---

# 4) What a Heartbeat Contains

A heartbeat can be minimal or rich.

## Minimal heartbeat
```json
{
  "nodeId": "node-a",
  "timestamp": 1703001234567
}
```

## Rich heartbeat
```json
{
  "nodeId": "node-a",
  "timestamp": 1703001234567,
  "status": "healthy",
  "cpu": 0.73,
  "memoryUsedMb": 1200,
  "activeConnections": 1500,
  "version": "2.1.0"
}
```

## Why rich heartbeats help
You can distinguish:
- alive but overloaded
- alive but not ready
- alive and healthy

That enables:
- better load balancing
- graceful degradation
- readiness vs liveness separation

---

# 5) Heartbeat Interval and Timeout

Two key parameters define heartbeat behavior:

## Heartbeat interval
How often a heartbeat is sent.

Example:
```text
every 1 second
```

## Failure timeout
How long to wait without hearing from a node before suspecting failure.

Example:
```text
3 seconds or 5 seconds
```

---

## Practical relationship

| Interval | Timeout | Behavior |
|---|---:|---|
| 1s | 1s | very fast, many false positives |
| 1s | 3s | balanced default |
| 1s | 10s | slower, safer |

### Rule of thumb
```text
Timeout is usually 3x to 10x the heartbeat interval
```

---

# 6) Failure Detection Strategies

# 6.1 Fixed Timeout

## Idea
If heartbeat not seen within fixed time:
```text
mark node failed
```

### Example
- heartbeat every 1s
- timeout after 5s

If no heartbeat for 5 seconds -> failed

## Pros
- simple
- predictable
- easy to implement

## Cons
- does not adapt to network conditions
- bad under jitter/spikes
- can create false positives

## Best for
- simple internal systems
- low-latency stable environments
- first implementation

---

# 6.2 Adaptive Timeout

## Idea
Use observed heartbeat delays to compute timeout dynamically.

### Example logic
If heartbeat RTTs are usually:
- 45ms
- 50ms
- 55ms

Then timeout might be:
```text
mean + 4 * stddev
```

## Pros
- adapts to real conditions
- fewer false positives than fixed timeout
- works better across environments

## Cons
- more complex
- needs warm-up data
- can still react poorly to sudden network changes

## Best for
- production systems with variable latency
- environments spanning zones / regions

---

# 6.3 Phi Accrual Failure Detector

Used by systems like:
- Cassandra
- Akka

## Idea
Instead of alive/dead, compute a **suspicion level** `phi`.

Higher phi = more likely failed.

### Formula concept
```text
phi = -log10(probability node is still alive)
```

### Interpretation examples
| Phi | Meaning |
|---:|---|
| 1 | likely alive |
| 2 | maybe delayed |
| 4 | suspicious |
| 8 | likely failed |

Common threshold:
```text
phi >= 8 -> mark DOWN
```

## Pros
- not just binary
- adapts automatically
- gives quantitative suspicion level

## Cons
- more complex math
- threshold tuning needed
- assumes statistical heartbeat patterns

## Best for
- sophisticated distributed clusters
- large systems with variable latency

---

# 6.4 Gossip-Based Detection

Used in:
- Cassandra (with gossip)
- Consul / SWIM-like family
- membership systems

## Idea
Nodes share health information with each other instead of relying on one monitor.

### Flow
1. Each node keeps a local view of cluster health
2. Periodically gossips with random peers
3. Exchanges heartbeat/version info
4. Merges latest health state
5. Suspicion spreads through the cluster

## Pros
- decentralized
- scalable
- no single monitor bottleneck
- robust for large clusters

## Cons
- eventually consistent
- harder to reason about
- not instant detection

## Best for
- large distributed clusters
- membership systems
- decentralized architectures

---

## 6.5 SWIM-Style Detection

Used by:
- Consul (SWIM-based ideas)
- many membership protocols

## Idea
1. Node A pings Node D
2. If no response, A asks B and C to ping D indirectly
3. If indirect probes also fail -> D is suspected

This reduces false positives caused by:
- one bad network path
- temporary pairwise connectivity issue

## Pros
- fewer false positives
- scalable
- decentralized
- good for large clusters

## Cons
- more complex than simple ping/pong
- still probabilistic

---

# 7) Real-World Systems

## 7.1 Cassandra
Uses:
- **gossip**
- **phi accrual detector**

### Behavior
- nodes gossip heartbeats
- each node computes phi for peers
- if phi crosses threshold, peer marked DOWN

### Why it works
Good fit for:
- large clusters
- dynamic latency
- decentralized operation

---

## 7.2 ZooKeeper
Uses:
- session-based detection
- heartbeats tied to client session timeout

### Behavior
- clients maintain session
- if session heartbeats stop
- session expires
- ephemeral nodes are deleted

This is the basis for:
- leader election
- lock cleanup
- membership tracking

---

## 7.3 Kubernetes
Kubernetes uses multiple layers:

### Pod/container level
- liveness probe
- readiness probe
- startup probe

### Node level
- kubelet heartbeats to control plane

### Why layered detection matters
A container can be:
- alive but not ready
- unhealthy but still running
- failed completely

This separation is very important in interviews.

---

## 7.4 Consul
Consul uses SWIM-style membership and indirect probing.

### Why good
- decentralized
- low overhead
- good at avoiding false positives from pairwise network issues

---

# 8) The Main Trade-Offs

## 8.1 Detection speed vs false positives

| Lower Timeout | Higher Timeout |
|---|---|
| faster failover | slower failover |
| more false positives | fewer false positives |
| more instability risk | more downtime risk |

---

## 8.2 Cost of false positives
False positives can trigger:
- unnecessary failovers
- leader re-elections
- cache/data rebalancing
- operator alerts
- service churn

For expensive systems, false positives are very costly.

---

## 8.3 Cost of slow detection
Slow detection means:
- longer downtime
- longer stale routing
- worse SLA
- slower recovery

User-facing systems usually care a lot about this.

---

## 8.4 How to choose timeout

Use something like:

```text
timeout > p99 network latency + max pause + heartbeat interval
```

Where pause may include:
- GC pause
- event loop stall
- CPU starvation
- disk stalls

Then add safety margin.

---

# 9) Best Practices

## 1. Use multiple health signals
Do not depend only on heartbeat.

Combine:
- heartbeat/liveness
- readiness
- error rate
- CPU/memory pressure
- request success rate
- replication lag
- queue depth

---

## 2. Separate liveness from readiness

### Liveness
```text
Is the process alive?
```

### Readiness
```text
Can it safely serve traffic?
```

This is one of the most useful patterns in practice.

---

## 3. Degrade before declaring dead
Instead of immediately failing a node:
1. reduce traffic
2. stop sending new requests
3. remove from rotation
4. mark failed if problem persists

This avoids unnecessary hard failovers.

---

## 4. Handle network partitions explicitly
In partition-prone systems:
- use quorum decisions
- avoid unilateral failover if that risks split-brain
- combine failure detection with leadership/consensus rules

---

## 5. Log every state transition
Track:
- HEALTHY
- SUSPECTED
- UNHEALTHY
- FAILED
- RECOVERED

This is essential for debugging false positives.

---

## 6. Test failure detection
You should test:
- crash
- pause
- CPU starvation
- packet loss
- latency injection
- partition
- slow network

Tools:
- Toxiproxy
- Chaos Monkey
- Pumba
- tc/netem on Linux

---

# 10) Quick Reference

## Which strategy to choose?

| Situation | Good Choice |
|---|---|
| simple service monitor | fixed timeout heartbeat |
| cloud service with variable latency | adaptive timeout |
| large cluster, decentralized | gossip or SWIM |
| advanced production cluster | phi accrual |
| Kubernetes workload | probes + kubelet heartbeat |

---

## Interview shortcut

```text
Small/simple -> fixed timeout
Variable network -> adaptive timeout
Large cluster -> gossip/SWIM
Advanced cluster -> phi accrual
```

---

# 11) Spring Boot Implementation from Scratch

Below are practical examples you can run or adapt.

---

## 11.1 Basic Push Heartbeat Monitor (Spring Boot)

This example has:
- nodes sending heartbeats
- monitor storing last-seen timestamps
- scheduled checker marking nodes unhealthy

### Maven dependencies
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

### Heartbeat DTO
```java
public class HeartbeatRequest {
    private String nodeId;
    private long timestamp;
    private String status;

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

### Node health model
```java
import java.time.Instant;

public class NodeStatus {
    private final String nodeId;
    private volatile Instant lastSeen;
    private volatile String state;
    private volatile String reportedStatus;

    public NodeStatus(String nodeId) {
        this.nodeId = nodeId;
        this.lastSeen = Instant.now();
        this.state = "HEALTHY";
        this.reportedStatus = "healthy";
    }

    public String getNodeId() { return nodeId; }
    public Instant getLastSeen() { return lastSeen; }
    public void setLastSeen(Instant lastSeen) { this.lastSeen = lastSeen; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getReportedStatus() { return reportedStatus; }
    public void setReportedStatus(String reportedStatus) { this.reportedStatus = reportedStatus; }
}
```

### In-memory heartbeat registry
```java
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HeartbeatRegistry {

    private final Map<String, NodeStatus> nodes = new ConcurrentHashMap<>();

    public void recordHeartbeat(String nodeId, String reportedStatus) {
        NodeStatus status = nodes.computeIfAbsent(nodeId, NodeStatus::new);
        status.setLastSeen(Instant.now());
        status.setReportedStatus(reportedStatus);
        status.setState("HEALTHY");
    }

    public Map<String, NodeStatus> all() {
        return nodes;
    }
}
```

### Heartbeat controller
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/heartbeats")
public class HeartbeatController {

    private final HeartbeatRegistry registry;

    public HeartbeatController(HeartbeatRegistry registry) {
        this.registry = registry;
    }

    @PostMapping
    public String heartbeat(@RequestBody HeartbeatRequest request) {
        registry.recordHeartbeat(request.getNodeId(), request.getStatus());
        return "OK";
    }

    @GetMapping
    public Object all() {
        return registry.all();
    }
}
```

### Scheduled failure detector
```java
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class FailureDetectorTask {

    private final HeartbeatRegistry registry;
    private static final long SUSPECT_TIMEOUT_MS = 3000;
    private static final long FAIL_TIMEOUT_MS = 8000;

    public FailureDetectorTask(HeartbeatRegistry registry) {
        this.registry = registry;
    }

    @Scheduled(fixedRate = 1000)
    public void checkNodes() {
        Instant now = Instant.now();

        for (NodeStatus status : registry.all().values()) {
            long age = Duration.between(status.getLastSeen(), now).toMillis();

            if (age > FAIL_TIMEOUT_MS) {
                status.setState("FAILED");
            } else if (age > SUSPECT_TIMEOUT_MS) {
                status.setState("SUSPECTED");
            } else {
                status.setState("HEALTHY");
            }
        }
    }
}
```

### Main application
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FailureDetectionApplication {
    public static void main(String[] args) {
        SpringApplication.run(FailureDetectionApplication.class, args);
    }
}
```

---

## 11.2 Heartbeat Sender Example (Spring Boot node side)

A node can periodically send heartbeats to the monitor.

### Sender service
```java
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HeartbeatSender {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String nodeId = "node-a";
    private final String monitorUrl = "http://localhost:8080/heartbeats";

    @Scheduled(fixedRate = 1000)
    public void sendHeartbeat() {
        HeartbeatRequest request = new HeartbeatRequest();
        request.setNodeId(nodeId);
        request.setTimestamp(System.currentTimeMillis());
        request.setStatus("healthy");

        try {
            restTemplate.postForEntity(monitorUrl, request, String.class);
        } catch (Exception e) {
            System.err.println("Failed to send heartbeat: " + e.getMessage());
        }
    }
}
```

### RestTemplate bean
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

---

## 11.3 Pull-Based Health Check Example

Instead of nodes pushing, monitor pulls health.

### Node-side health endpoint
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public String health() {
        return "UP";
    }
}
```

### Monitor polling service
```java
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PullHealthMonitor {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, String> nodeStates = new ConcurrentHashMap<>();

    private final Map<String, String> monitoredNodes = Map.of(
            "node-a", "http://localhost:8081/health",
            "node-b", "http://localhost:8082/health"
    );

    @Scheduled(fixedRate = 2000)
    public void pollNodes() {
        monitoredNodes.forEach((nodeId, url) -> {
            try {
                String result = restTemplate.getForObject(url, String.class);
                nodeStates.put(nodeId, "UP".equals(result) ? "HEALTHY" : "UNHEALTHY");
            } catch (Exception e) {
                nodeStates.put(nodeId, "FAILED");
            }
        });
    }

    public Map<String, String> getNodeStates() {
        return nodeStates;
    }
}
```

---

## 11.4 Adaptive Timeout Example

Track recent heartbeat delays and compute dynamic threshold.

### Adaptive detector logic
```java
import java.util.ArrayList;
import java.util.List;

public class AdaptiveTimeoutCalculator {

    private final List<Long> samples = new ArrayList<>();

    public synchronized void recordSample(long rttMs) {
        samples.add(rttMs);
        if (samples.size() > 100) {
            samples.remove(0);
        }
    }

    public synchronized long currentTimeoutMs() {
        if (samples.isEmpty()) {
            return 3000;
        }

        double mean = samples.stream().mapToLong(Long::longValue).average().orElse(1000);
        double variance = samples.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0);
        double stddev = Math.sqrt(variance);

        return (long) Math.max(1000, mean + 4 * stddev);
    }
}
```

### Usage idea
- measure heartbeat RTT or inter-arrival delay
- call `recordSample(rtt)`
- use `currentTimeoutMs()` when checking node health

---

## 11.5 SQL-Backed Failure Tracking

If you want durable monitor state in SQL:

### SQL table
```sql
CREATE TABLE node_heartbeat (
    node_id VARCHAR(100) PRIMARY KEY,
    last_seen TIMESTAMP NOT NULL,
    state VARCHAR(20) NOT NULL,
    reported_status VARCHAR(50)
);
```

### Upsert heartbeat
PostgreSQL:
```sql
INSERT INTO node_heartbeat(node_id, last_seen, state, reported_status)
VALUES ('node-a', NOW(), 'HEALTHY', 'healthy')
ON CONFLICT (node_id)
DO UPDATE SET
    last_seen = EXCLUDED.last_seen,
    state = 'HEALTHY',
    reported_status = EXCLUDED.reported_status;
```

### Mark suspected
```sql
UPDATE node_heartbeat
SET state = 'SUSPECTED'
WHERE last_seen < NOW() - INTERVAL '3 seconds'
  AND state = 'HEALTHY';
```

### Mark failed
```sql
UPDATE node_heartbeat
SET state = 'FAILED'
WHERE last_seen < NOW() - INTERVAL '8 seconds';
```

This is simple and practical for internal dashboards or scheduler clusters.

---

## 11.6 Kubernetes Probe Quick Reference

A common interview expectation is knowing the difference.

### Liveness probe
Restarts the container if dead/hung.

Example:
```yaml
livenessProbe:
  httpGet:
    path: /health/live
    port: 8080
  periodSeconds: 10
  failureThreshold: 3
```

### Readiness probe
Stops routing traffic to the pod if not ready.

```yaml
readinessProbe:
  httpGet:
    path: /health/ready
    port: 8080
  periodSeconds: 5
```

### Startup probe
Used when app takes long to start.

```yaml
startupProbe:
  httpGet:
    path: /health/startup
    port: 8080
  failureThreshold: 30
  periodSeconds: 10
```

---

# 12) Practical Recommendation

## For simple internal systems
Use:
- push heartbeats
- fixed timeout
- optional DB state table

## For cloud microservices
Use:
- liveness + readiness
- adaptive timeout if needed
- monitor + load balancer integration

## For large distributed clusters
Use:
- gossip / SWIM
- phi accrual if advanced latency adaptation needed

## For Kubernetes
Use:
- probes
- kubelet/node heartbeat
- readiness before failover where possible

---

# 13) Final Summary

- **Failure detection is fundamental to fault tolerance**  
  Without it, no failover, rebalancing, or recovery can happen.

- **Failure detection is never certain**  
  No response means unknown, not definitely dead.

- **Heartbeats are the core mechanism**  
  Push, pull, or hybrid models all build on them.

- **The main trade-off is speed vs false positives**  
  Faster detection causes more mistakes; slower detection causes more downtime.

- **Different systems choose different detectors**  
  Fixed timeout, adaptive timeout, phi accrual, gossip, SWIM.

- **Real systems use layered signals**  
  Liveness, readiness, load, errors, and replication state together give better decisions.

- **Test under real failure conditions**  
  Crashes, latency, partitions, and pauses expose bugs that normal testing misses.

---

## Final 1-Line Shortcut
```text
Simple system -> heartbeats + fixed timeout | variable network -> adaptive timeout | large decentralized cluster -> gossip/SWIM | advanced production cluster -> phi accrual
```
