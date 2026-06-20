# 099_ServiceDiscovery_Final_CheatSheet.md

# MiniServiceDiscovery — Final Cheat Sheet

> Last-minute revision file for **Service Discovery**, **Service Registry**, **Load Balancing**, **Health Checks**, **Eureka**, **Kubernetes DNS/CoreDNS**, **Spring Cloud**, **Envoy/Istio**, **Tracing/Metrics**, and **Production Debugging**.
>
> Goal: after reading this file, you should be able to answer system design, Java backend, cloud-native, and production-debugging questions confidently.

---

## 0. One-Line Mental Model

**Service Discovery = dynamic phone book for services.**

Instead of hardcoding:

```text
payment-service = 10.0.1.23:8080
```

we ask a discovery system:

```text
Where are healthy instances of payment-service right now?
```

The answer can change because instances are constantly:

- starting
- stopping
- scaling up
- scaling down
- moving between nodes
- becoming unhealthy
- recovering
- being redeployed

---

## 1. Why Hardcoded IPs Fail

Hardcoded IPs work only in small static systems.

They fail in cloud-native systems because:

| Problem | What Happens |
|---|---|
| Instance restarts | New IP is assigned |
| Autoscaling | More instances appear dynamically |
| Deployment | Old pods die, new pods start |
| Node failure | Instances disappear suddenly |
| Region failover | Traffic must move elsewhere |
| Blue-green deployment | Two versions coexist |
| Canary deployment | Small traffic percentage goes to new version |

### Interview Answer

> Hardcoded IPs couple clients to infrastructure. In cloud-native systems, service instances are ephemeral, so clients should depend on a logical service name, while a discovery mechanism resolves that name to healthy endpoints dynamically.

---

## 2. Core Components

```text
+------------------+        register        +------------------+
| Service Instance | ---------------------> | Service Registry |
+------------------+                        +------------------+
        |                                             |
        | heartbeat                                   | lookup
        v                                             v
+------------------+                        +------------------+
| Health Checker   |                        | Service Client   |
+------------------+                        +------------------+
```

### Main Parts

| Component | Responsibility |
|---|---|
| Service Instance | Real running app process/pod |
| Service Registry | Stores service-name → instances |
| Heartbeat | Instance says “I am alive” |
| Health Check | System verifies liveness/readiness |
| Discovery Client | Finds service instances |
| Load Balancer | Selects one instance |

---

## 3. Service Registry Mental Model

A service registry is usually a map:

```java
Map<String, List<ServiceInstance>> registry;
```

Example:

```text
payment-service -> [
  10.0.1.10:8080 HEALTHY zone-a v1,
  10.0.1.11:8080 HEALTHY zone-a v1,
  10.0.2.20:8080 DEGRADED zone-b v2
]
```

### ServiceInstance Model

```java
class ServiceInstance {
    String serviceName;
    String instanceId;
    String host;
    int port;
    String zone;
    String version;
    Map<String, String> metadata;
    HealthStatus status;
    long lastHeartbeatTime;
}
```

### Metadata Examples

| Metadata | Usage |
|---|---|
| version=v1 | Canary / blue-green |
| zone=eu-west-1a | Zone-aware routing |
| weight=5 | Weighted load balancing |
| protocol=http/grpc | Protocol selection |
| secure=true | HTTPS endpoint |
| build=abc123 | Debugging deployment |

---

## 4. Registration Flow

```text
1. Instance starts
2. Instance creates metadata
3. Instance registers with registry
4. Registry stores instance
5. Instance periodically sends heartbeat
6. Clients discover instance
```

### Java-Style Registration Example

```java
public class ServiceRegistry {
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<ServiceInstance>> registry =
            new ConcurrentHashMap<>();

    public void register(ServiceInstance instance) {
        registry.computeIfAbsent(instance.serviceName(), k -> new CopyOnWriteArrayList<>())
                .add(instance);
    }

    public List<ServiceInstance> lookup(String serviceName) {
        return registry.getOrDefault(serviceName, new CopyOnWriteArrayList<>())
                .stream()
                .filter(i -> i.status() == HealthStatus.HEALTHY)
                .toList();
    }
}
```

### Important Point

Registration alone is not enough. Without heartbeat or health checks, dead instances remain in the registry.

---

## 5. Deregistration Flow

Graceful shutdown:

```text
1. App receives SIGTERM
2. App stops accepting new traffic
3. App deregisters from registry
4. App waits for in-flight requests
5. App exits
```

Failure shutdown:

```text
1. Instance crashes
2. No deregistration happens
3. Heartbeats stop
4. Registry expires lease after TTL
5. Instance removed or marked DOWN
```

### Kubernetes Equivalent

| Concept | Kubernetes Mechanism |
|---|---|
| Deregister | Pod removed from Endpoints/EndpointSlice |
| Stop traffic | readinessProbe fails |
| Graceful shutdown | terminationGracePeriodSeconds |
| In-flight drain | preStop hook / service mesh drain |

---

## 6. Heartbeat / Lease / TTL

Heartbeat prevents the registry from keeping dead instances forever.

```text
Instance sends heartbeat every N seconds.
Registry expires instance if no heartbeat for TTL seconds.
```

Example:

```text
heartbeat interval = 30 sec
lease expiration = 90 sec
```

If an instance misses 3 heartbeats, it may be removed.

### Java Lease Logic

```java
boolean isExpired(ServiceInstance instance, long nowMillis, long ttlMillis) {
    return nowMillis - instance.lastHeartbeatTime() > ttlMillis;
}
```

### Tradeoff

| Short TTL | Long TTL |
|---|---|
| Faster failure detection | Slower failure detection |
| More false positives | Fewer false positives |
| More registry churn | More stale instances |
| Good for fast failover | Good for unstable networks |

---

## 7. Health Status Types

| Status | Meaning |
|---|---|
| UP / HEALTHY | Can receive traffic |
| DOWN | Should not receive traffic |
| STARTING | Booting, not ready |
| OUT_OF_SERVICE | Intentionally removed |
| DEGRADED | Alive but limited capacity |
| UNKNOWN | Health not confirmed |

### Liveness vs Readiness

| Probe | Question | Action |
|---|---|---|
| Liveness | Is process alive? | Restart if failed |
| Readiness | Can it receive traffic? | Remove from traffic if failed |

### Interview Answer

> Liveness should be conservative and only fail when the process is truly broken. Readiness should fail when the service cannot safely serve requests, for example when DB connections are exhausted or warm-up is incomplete.

---

## 8. Client-Side Discovery

In client-side discovery, the client asks the registry directly.

```text
Client -> Registry -> instance list
Client -> Load Balancer -> chosen instance
Client -> Service Instance
```

### Example

```text
order-service calls payment-service
order-service queries Eureka
Eureka returns payment instances
order-service chooses one using load balancer
```

### Pros

- Fast after local cache is warm
- Client controls load balancing
- Can do retries, zone preference, weighted routing
- Fewer network hops

### Cons

- Client needs discovery logic
- Every language needs library support
- Registry details leak into applications
- Harder polyglot support

---

## 9. Server-Side Discovery

In server-side discovery, client calls a stable endpoint/load balancer.

```text
Client -> Load Balancer / Proxy -> Service Instance
```

Examples:

- Kubernetes Service
- AWS ALB/NLB
- NGINX
- Envoy
- Istio sidecar

### Pros

- Client is simple
- Works with any language
- Centralized routing policies
- Easier platform control

### Cons

- Extra network hop
- Proxy/load balancer can become bottleneck
- Requires infrastructure layer
- Debugging may need proxy-level visibility

---

## 10. Client-Side vs Server-Side Discovery

| Area | Client-Side | Server-Side |
|---|---|---|
| Example | Eureka + Ribbon/Spring Cloud LB | Kubernetes Service / Envoy |
| Client complexity | Higher | Lower |
| Platform complexity | Lower | Higher |
| Polyglot support | Harder | Easier |
| Control | App/client | Platform/proxy |
| Common today | Spring Cloud legacy/internal | Kubernetes/service mesh |

### Strong Interview Answer

> Client-side discovery is powerful when the application stack is homogeneous and clients can embed discovery logic. Server-side discovery is better in cloud-native environments because clients depend only on stable service names while the platform handles endpoint updates, load balancing, and policy enforcement.

---

## 11. Load Balancing Algorithms

### Round Robin

```text
A, B, C, A, B, C
```

Simple and fair when all instances have similar capacity.

```java
class RoundRobinLoadBalancer {
    private final AtomicInteger index = new AtomicInteger(0);

    public ServiceInstance choose(List<ServiceInstance> instances) {
        int i = Math.abs(index.getAndIncrement());
        return instances.get(i % instances.size());
    }
}
```

### Weighted Round Robin

```text
A weight 3
B weight 1
Traffic: A, A, A, B
```

Useful when instances have different capacity.

### Least Connections

Chooses instance with fewest active requests.

Good for long-running requests.

### Random

Simple and surprisingly effective at scale.

### Power of Two Choices

Pick two random instances and choose the less loaded one.

```java
ServiceInstance choose(List<ServiceInstance> instances) {
    ServiceInstance a = random(instances);
    ServiceInstance b = random(instances);
    return a.activeRequests() <= b.activeRequests() ? a : b;
}
```

Very good balance with low overhead.

---

## 12. Health-Aware Load Balancing

A load balancer should not blindly send traffic to every registered instance.

It should consider:

- health status
- readiness
- error rate
- latency
- active connections
- zone
- circuit breaker state
- recent failures

### Basic Filter

```java
List<ServiceInstance> candidates = instances.stream()
        .filter(i -> i.status() == HealthStatus.HEALTHY)
        .filter(i -> i.isReady())
        .filter(i -> !i.circuitOpen())
        .toList();
```

### Production Rule

Never load balance before filtering unhealthy instances.

```text
filter healthy -> rank candidates -> choose one -> send request
```

---

## 13. Eureka Cheat Sheet

Eureka is a Netflix/Spring Cloud service registry.

### Key Concepts

| Concept | Meaning |
|---|---|
| Eureka Server | Registry server |
| Eureka Client | App that registers/discovers |
| Heartbeat | Lease renewal |
| Lease | Time-bound registration |
| Self-preservation | Avoid deleting too many instances during network problems |
| Registry cache | Client-side local copy |

### Eureka Flow

```text
1. Service starts
2. Registers with Eureka
3. Sends heartbeat every 30s
4. Client fetches registry every 30s
5. Client load balances locally
6. Expired leases removed unless self-preservation protects them
```

### Common Config

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
  instance:
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90
```

### Eureka Self-Preservation

Self-preservation prevents mass eviction when many heartbeats are missed.

```text
If Eureka receives fewer renewals than expected,
it assumes network partition instead of mass service failure.
```

### Why It Exists

Without self-preservation:

```text
temporary network issue -> missed heartbeats -> mass eviction -> clients lose all endpoints
```

With self-preservation:

```text
temporary network issue -> stale registry retained -> clients may still call old endpoints
```

### Tradeoff

| Benefit | Cost |
|---|---|
| Avoids registry meltdown | May keep dead instances longer |
| Better availability | More stale endpoint risk |
| Good during partitions | Requires client retries/circuit breakers |

---

## 14. Consul / Zookeeper / Eureka / Kubernetes Comparison

| System | Model | Strong Point | Weak Point |
|---|---|---|---|
| Eureka | AP-style registry | Availability, Spring Cloud | Stale data possible |
| Consul | Service discovery + KV | Health checks, DNS/API | Operational complexity |
| Zookeeper | CP coordination | Strong consistency | Not ideal as simple registry for high churn |
| Kubernetes | Native service discovery | Built into platform | Mostly inside cluster |
| Istio/Envoy | Service mesh discovery/routing | Traffic control, mTLS, observability | Complexity |

### CAP Mental Model

| Preference | Example | Behavior |
|---|---|---|
| AP | Eureka | Prefer availability, tolerate stale reads |
| CP | Zookeeper | Prefer consistency, may reject requests during partition |
| Platform-native | Kubernetes | Uses API server + endpoints + DNS/proxying |

---

## 15. Kubernetes Service Discovery

Kubernetes gives stable service names even though pods are ephemeral.

```text
Client calls:
payment-service.default.svc.cluster.local
```

Kubernetes resolves this to service endpoints.

### Important Objects

| Object | Role |
|---|---|
| Pod | Running instance |
| Service | Stable virtual name/IP |
| Endpoints | Backend pod IPs |
| EndpointSlice | Scalable endpoint representation |
| CoreDNS | DNS resolution |
| kube-proxy | Service traffic routing |
| readinessProbe | Controls endpoint inclusion |

---

## 16. Kubernetes DNS Resolution Flow

```text
order-service pod
  -> payment-service DNS name
  -> CoreDNS
  -> ClusterIP
  -> kube-proxy/IPVS/iptables
  -> backend pod IP
```

### Service DNS Names

```text
payment-service
payment-service.default
payment-service.default.svc
payment-service.default.svc.cluster.local
```

### Example Service

```yaml
apiVersion: v1
kind: Service
metadata:
  name: payment-service
spec:
  selector:
    app: payment
  ports:
    - port: 80
      targetPort: 8080
```

### Example Deployment Readiness

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
```

If readiness fails, pod is removed from service endpoints.

---

## 17. Kubernetes Service Types

| Type | Use Case |
|---|---|
| ClusterIP | Internal service discovery |
| NodePort | Expose on node port |
| LoadBalancer | Cloud external LB |
| ExternalName | DNS alias |
| Headless Service | Direct pod discovery |

### Headless Service

```yaml
spec:
  clusterIP: None
```

Used when clients need direct pod IPs, for example:

- StatefulSet
- Cassandra
- Kafka
- custom client-side load balancing

---

## 18. EndpointSlice Mental Model

Old Kubernetes used one big Endpoints object.

Problem:

```text
large service -> thousands of pods -> huge endpoints object -> update overhead
```

EndpointSlice splits endpoints into smaller chunks.

```text
payment-service
  EndpointSlice-1: 100 endpoints
  EndpointSlice-2: 100 endpoints
  EndpointSlice-3: 100 endpoints
```

This improves scalability.

---

## 19. Spring Cloud Service Discovery

Spring Cloud commonly uses:

- Eureka Client
- Spring Cloud LoadBalancer
- OpenFeign
- Gateway
- Resilience4j

### Feign Example

```java
@FeignClient(name = "payment-service")
public interface PaymentClient {
    @PostMapping("/payments")
    PaymentResponse pay(@RequestBody PaymentRequest request);
}
```

Here `payment-service` is logical name, not host/IP.

### RestTemplate Example

```java
@Bean
@LoadBalanced
RestTemplate restTemplate() {
    return new RestTemplate();
}
```

```java
restTemplate.getForObject(
    "http://payment-service/api/payments/123",
    PaymentResponse.class
);
```

### WebClient Example

```java
@Bean
@LoadBalanced
WebClient.Builder webClientBuilder() {
    return WebClient.builder();
}
```

---

## 20. OpenFeign Internal Call Flow

```text
Java interface method
  -> Feign proxy
  -> encode request
  -> resolve service name
  -> choose instance
  -> execute HTTP call
  -> decode response
  -> handle error/retry/fallback
```

### Important Layers

| Layer | Responsibility |
|---|---|
| Feign Proxy | Converts interface call to HTTP |
| Encoder | Java object -> JSON |
| Decoder | JSON -> Java object |
| Contract | Reads annotations |
| Client | Executes request |
| LoadBalancer | Chooses instance |
| ErrorDecoder | Maps errors |
| Retryer | Retry behavior |

---

## 21. Envoy / Istio Service Discovery

Envoy uses xDS APIs.

| xDS | Meaning |
|---|---|
| LDS | Listener Discovery Service |
| RDS | Route Discovery Service |
| CDS | Cluster Discovery Service |
| EDS | Endpoint Discovery Service |
| SDS | Secret Discovery Service |

### Envoy Mental Model

```text
Listener: where Envoy receives traffic
Route: how request path/host maps
Cluster: logical upstream service
Endpoint: actual backend IP:port
```

### Request Flow

```text
App -> localhost sidecar Envoy -> upstream sidecar Envoy -> target app
```

### Istio Control Plane

```text
Kubernetes API -> Istiod -> Envoy xDS config -> sidecars route traffic
```

---

## 22. Service Mesh vs Normal Discovery

| Feature | Normal Discovery | Service Mesh |
|---|---|---|
| Basic discovery | Yes | Yes |
| mTLS | App-managed | Mesh-managed |
| Retries | App code | Proxy config |
| Traffic split | Harder | Built-in |
| Canary | App/LB logic | VirtualService rules |
| Observability | App instrumentation | Proxy metrics/traces |
| Complexity | Lower | Higher |

### Interview Answer

> Service mesh moves discovery, routing, retries, mTLS, traffic splitting, and telemetry out of application code into sidecar proxies controlled by a central control plane.

---

## 23. Canary Routing Example in Istio

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: payment
spec:
  hosts:
    - payment-service
  http:
    - route:
        - destination:
            host: payment-service
            subset: v1
          weight: 90
        - destination:
            host: payment-service
            subset: v2
          weight: 10
```

```yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: payment
spec:
  host: payment-service
  subsets:
    - name: v1
      labels:
        version: v1
    - name: v2
      labels:
        version: v2
```

---

## 24. Network Partition / Stale Registry / Split Brain

### Network Partition

```text
Some nodes cannot talk to other nodes.
```

Example:

```text
Zone A cannot reach Zone B
```

### Stale Registry

Registry says instance is alive, but it is actually dead or unreachable.

### Split Brain

Two registry groups believe different truths.

```text
Registry A view: payment-1 alive, payment-2 dead
Registry B view: payment-1 dead, payment-2 alive
```

### How to Survive

- client retries
- timeouts
- circuit breakers
- health-aware load balancing
- zone-aware routing
- outlier detection
- active health checks
- short DNS TTL where appropriate
- graceful degradation

---

## 25. Eventual Consistency in Discovery

Discovery data is often eventually consistent.

```text
Instance dies at T0
Registry notices at T0 + TTL
Client refreshes at T0 + TTL + cache interval
```

During this window, client may call stale instance.

### Solution

Do not expect discovery to be perfect.

Use:

```text
timeout + retry + circuit breaker + health check + load balancing
```

### Strong Interview Answer

> Service discovery reduces but does not eliminate failure. Since registry data can be stale, clients must still use timeouts, retries with backoff, circuit breakers, and idempotency where required.

---

## 26. Registry Replication

A registry should not be single-node in production.

```text
Registry Node A <-> Registry Node B <-> Registry Node C
```

### Replication Models

| Model | Description |
|---|---|
| Leader-follower | Writes to leader, replicated to followers |
| Peer-to-peer | Each node shares updates with peers |
| Gossip | Nodes spread updates gradually |
| CP quorum | Majority required for writes |
| AP replication | Accept writes locally, sync later |

### Eureka Style

Eureka servers can replicate registrations among peers.

Clients can talk to any available Eureka node.

---

## 27. Debugging Service Discovery Issues

### Symptom: Service Cannot Resolve Name

Check:

```bash
nslookup payment-service
kubectl exec -it pod -- nslookup payment-service
kubectl get svc
kubectl get endpoints payment-service
kubectl get endpointslice
```

Possible causes:

- service name typo
- wrong namespace
- CoreDNS issue
- service has no endpoints
- NetworkPolicy blocking DNS

### Symptom: Service Resolves But Calls Fail

Check:

```bash
kubectl get pods -l app=payment
kubectl describe pod payment-xxx
kubectl logs payment-xxx
kubectl get endpoints payment-service -o yaml
```

Possible causes:

- pod not ready
- wrong targetPort
- app not listening on expected port
- readiness probe failing
- NetworkPolicy blocking traffic
- TLS/mTLS mismatch

### Symptom: Random 5xx

Possible causes:

- stale endpoints
- overloaded instance
- bad deployment version
- partial canary failure
- missing retry/circuit breaker
- uneven load balancing

---

## 28. Production Metrics to Watch

### Registry Metrics

| Metric | Why It Matters |
|---|---|
| registered_instances | Sudden drop indicates issue |
| heartbeat_success_rate | Detect network/instance problems |
| registry_fetch_latency | Client discovery slowness |
| lease_expirations | Too many means failures or timeout too low |
| self_preservation_triggered | Network partition or mass heartbeat loss |

### Client Metrics

| Metric | Meaning |
|---|---|
| request latency p95/p99 | User impact |
| 5xx rate | Downstream failures |
| timeout count | Unreachable or slow services |
| retry count | Hidden instability |
| circuit breaker open count | Dependency failure |
| load balancer chosen instance distribution | Hotspot detection |

### Kubernetes Metrics

| Metric | Meaning |
|---|---|
| pod readiness | Whether pods receive traffic |
| endpoint count | Backends available |
| CoreDNS latency | DNS resolution health |
| CoreDNS errors | DNS failures |
| kube-proxy sync latency | Service routing health |

---

## 29. Distributed Tracing Cheat Sheet

Trace a request across services:

```text
API Gateway -> Order -> Payment -> Inventory -> Notification
```

Each service creates spans.

### Trace Helps Answer

- Which service is slow?
- Which dependency failed?
- Was retry happening?
- Did traffic go to wrong version?
- Did canary version cause errors?

### Useful Tags

```text
service.name
service.version
instance.id
zone
http.status_code
error
retry_count
peer.service
k8s.pod.name
```

---

## 30. Timeout / Retry / Circuit Breaker Rules

### Timeout

Never use infinite timeout.

```text
connect timeout: small
read timeout: based on SLA
```

### Retry

Retry only when safe.

Safe examples:

- GET request
- idempotent PUT
- POST with idempotency key
- transient 503/504

Dangerous examples:

- payment charge without idempotency key
- order creation without duplicate protection

### Circuit Breaker

Stops sending traffic to failing dependency.

```text
CLOSED -> OPEN -> HALF_OPEN -> CLOSED
```

### Golden Rule

Discovery alone cannot protect the system. Discovery must be combined with resilience patterns.

---

## 31. Common Interview Questions

### Q1. What is service discovery?

**Answer:**

Service discovery is the mechanism by which services dynamically find network locations of other services. Instead of hardcoding IPs, clients use logical service names that map to currently healthy instances.

---

### Q2. Why is service discovery needed in microservices?

**Answer:**

Microservice instances are dynamic due to autoscaling, container restarts, deployments, and failures. Service discovery allows clients to find healthy instances without knowing their physical IPs.

---

### Q3. What is the difference between client-side and server-side discovery?

**Answer:**

In client-side discovery, the client queries the registry and chooses an instance. In server-side discovery, the client calls a stable load balancer or service name, and the infrastructure routes the request.

---

### Q4. How does Eureka work?

**Answer:**

Services register with Eureka and periodically renew leases through heartbeats. Clients fetch the registry and cache it locally. If heartbeats stop, Eureka expires instances, unless self-preservation is active.

---

### Q5. What is Eureka self-preservation?

**Answer:**

Self-preservation prevents Eureka from evicting many instances when heartbeat renewals suddenly drop, assuming a network partition rather than mass instance failure. It improves availability but can keep stale instances.

---

### Q6. How does Kubernetes service discovery work?

**Answer:**

Kubernetes creates stable Service objects. Pods matching the Service selector are added to Endpoints or EndpointSlices. CoreDNS resolves service names, and kube-proxy or CNI routes traffic to backend pods.

---

### Q7. What happens when a Kubernetes pod is not ready?

**Answer:**

If readinessProbe fails, the pod is removed from the Service endpoints, so it should not receive normal service traffic.

---

### Q8. Why can service discovery still send traffic to dead instances?

**Answer:**

Because discovery data can be stale. There is a delay between instance failure, registry update, client cache refresh, and load balancer selection.

---

### Q9. How do you handle stale discovery data?

**Answer:**

Use short timeouts, retries with backoff, health-aware load balancing, circuit breakers, outlier detection, and idempotency for retried operations.

---

### Q10. What is service mesh discovery?

**Answer:**

In service mesh, proxies such as Envoy receive endpoint and routing configuration from a control plane like Istio. Applications call local proxies, and the mesh handles routing, retries, mTLS, telemetry, and traffic policy.

---

## 32. Design: Build an In-Memory Service Registry

### Requirements

Functional:

- register service instance
- deregister instance
- heartbeat update
- lookup healthy instances
- expire dead instances

Non-functional:

- thread-safe
- low-latency lookup
- handles concurrent registration
- supports TTL cleanup

### Data Structures

```java
ConcurrentHashMap<String, ConcurrentHashMap<String, ServiceInstance>> registry;
```

Why nested map?

```text
serviceName -> instanceId -> instance
```

This makes deregistration fast.

### Basic Implementation

```java
public class InMemoryRegistry {
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, ServiceInstance>> registry =
            new ConcurrentHashMap<>();

    public void register(ServiceInstance instance) {
        registry.computeIfAbsent(instance.serviceName(), k -> new ConcurrentHashMap<>())
                .put(instance.instanceId(), instance);
    }

    public void deregister(String serviceName, String instanceId) {
        var instances = registry.get(serviceName);
        if (instances != null) {
            instances.remove(instanceId);
            if (instances.isEmpty()) {
                registry.remove(serviceName, instances);
            }
        }
    }

    public void heartbeat(String serviceName, String instanceId, long now) {
        var instances = registry.get(serviceName);
        if (instances == null) return;

        ServiceInstance instance = instances.get(instanceId);
        if (instance != null) {
            instance.updateHeartbeat(now);
        }
    }

    public List<ServiceInstance> lookup(String serviceName) {
        var instances = registry.get(serviceName);
        if (instances == null) return List.of();

        return instances.values().stream()
                .filter(ServiceInstance::isHealthy)
                .toList();
    }
}
```

---

## 33. Scaling the Registry

### Problems at Scale

| Problem | Explanation |
|---|---|
| Too many heartbeats | Registry write pressure |
| Too many clients fetching | Registry read pressure |
| Large registry payload | Network cost |
| Frequent churn | Constant updates |
| Multi-region latency | Cross-region sync delay |

### Solutions

- client-side cache
- delta updates
- heartbeat batching
- registry replication
- sharding by service name
- local zone registry
- eventual consistency
- gossip propagation
- DNS-based discovery for simple services

---

## 34. Caching Discovery Data

Clients often cache registry data.

```text
Client cache refresh every 30 seconds
```

### Benefits

- lower registry load
- faster lookups
- registry outage tolerance

### Risk

- stale instance list

### Rule

Cache discovery data, but design calls as if cache can be wrong.

---

## 35. DNS-Based Discovery

DNS can be used for service discovery.

Example:

```text
payment-service.default.svc.cluster.local -> ClusterIP
```

Or DNS can return multiple A records:

```text
payment-service -> 10.0.1.10, 10.0.1.11, 10.0.1.12
```

### DNS Pros

- language independent
- simple
- widely supported

### DNS Cons

- caching issues
- TTL delays
- limited metadata
- weaker health awareness
- client behavior varies

---

## 36. Zone-Aware Discovery

Prefer local-zone instances first.

```text
client zone = eu-west-1a
prefer instances in eu-west-1a
fallback to eu-west-1b
```

### Why?

- lower latency
- lower cross-zone cost
- better fault isolation

### Risk

If local zone is overloaded, strict zone preference can hurt performance.

Better:

```text
prefer same zone, but spill over when needed
```

---

## 37. Version-Aware Discovery

Useful for:

- canary deployment
- blue-green deployment
- A/B testing
- backward compatibility testing

Example:

```text
payment-service v1 -> 90% traffic
payment-service v2 -> 10% traffic
```

Metadata:

```text
version=v1
version=v2
```

---

## 38. Security in Service Discovery

Important security concerns:

| Risk | Mitigation |
|---|---|
| Fake service registration | Authenticated registration |
| Registry data leak | Access control |
| MITM between services | mTLS |
| Unauthorized service calls | Service identity / policy |
| Poisoned endpoints | Validation and signed config |

### Service Mesh Security

Istio/Envoy can provide:

- workload identity
- mTLS
- authorization policy
- certificate rotation

---

## 39. Anti-Patterns

| Anti-Pattern | Why Bad |
|---|---|
| Hardcoded IPs | Breaks with dynamic infra |
| No readiness probe | Traffic goes to unready pods |
| Infinite timeout | Thread exhaustion |
| Retry without backoff | Retry storm |
| Retry non-idempotent payment | Duplicate charge |
| No circuit breaker | Cascading failure |
| Registry as single point of failure | Discovery outage |
| Too-short TTL | False evictions |
| Too-long TTL | Stale endpoints |
| Ignoring zone | Higher latency/cost |

---

## 40. System Design Answer Template

When asked to design service discovery:

```text
1. Instances register with registry using service name, host, port, metadata.
2. Instances renew lease using heartbeat.
3. Registry expires instances after TTL.
4. Clients discover service by logical name.
5. Client or proxy load balances across healthy endpoints.
6. Readiness/health checks prevent bad instances from receiving traffic.
7. Registry is replicated to avoid single point of failure.
8. Clients cache registry data to reduce load.
9. Because data can be stale, clients use timeout, retry, circuit breaker.
10. Metrics, tracing, and logs are used to debug discovery and routing issues.
```

---

## 41. Production Incident Playbook

### Case 1: All Calls to Payment Failing

Check:

```bash
kubectl get svc payment-service
kubectl get endpoints payment-service
kubectl get pods -l app=payment
kubectl describe pod <payment-pod>
kubectl logs <payment-pod>
```

Likely causes:

- no ready pods
- wrong service selector
- wrong targetPort
- app crash loop
- readiness failing

---

### Case 2: Only Some Calls Failing

Likely causes:

- one bad pod
- canary version broken
- stale endpoint
- uneven load balancing
- zone-specific problem

Check:

```bash
kubectl get pods -l app=payment -o wide
kubectl get endpointslice -l kubernetes.io/service-name=payment-service
```

Also inspect traces grouped by:

```text
pod name
version
zone
status code
```

---

### Case 3: DNS Failure

Check:

```bash
kubectl get pods -n kube-system -l k8s-app=kube-dns
kubectl logs -n kube-system deploy/coredns
kubectl exec -it <pod> -- nslookup kubernetes.default
kubectl exec -it <pod> -- cat /etc/resolv.conf
```

Possible causes:

- CoreDNS down
- NetworkPolicy blocking DNS
- node DNS issue
- wrong namespace
- search domain confusion

---

### Case 4: Eureka Shows Old Instances

Likely causes:

- lease expiration too long
- self-preservation active
- clients not deregistering on shutdown
- heartbeat path still alive but app not ready

Fix:

- implement graceful deregistration
- tune lease settings carefully
- add readiness/health endpoint
- use client-side retries and circuit breakers

---

## 42. Last-Minute Comparison Table

| Topic | Key Sentence |
|---|---|
| Service Discovery | Finds dynamic service instances by logical name |
| Registry | Stores service → instances |
| Heartbeat | Keeps lease alive |
| TTL | Removes stale instances after timeout |
| Health Check | Prevents bad instances from serving traffic |
| Client-Side Discovery | Client chooses endpoint |
| Server-Side Discovery | Proxy/platform chooses endpoint |
| Eureka | AP-style Spring Cloud registry |
| Kubernetes Service | Stable virtual service over pods |
| CoreDNS | Resolves service DNS names |
| EndpointSlice | Scalable backend endpoint list |
| Envoy | Data-plane proxy |
| Istio | Control plane + policy + telemetry |
| Stale Registry | Registry data is outdated |
| Self-Preservation | Avoids mass eviction during partition |
| Circuit Breaker | Stops calls to failing dependency |

---

## 43. Strong Interview Sound Bites

Use these directly in interviews.

### Sound Bite 1

> Service discovery gives a stable logical name over dynamic instances.

### Sound Bite 2

> Discovery data is not a source of absolute truth; it is a best-effort routing hint that can be stale.

### Sound Bite 3

> Health checks decide whether an instance should receive traffic, not merely whether the process exists.

### Sound Bite 4

> Kubernetes discovery is mostly server-side from the application perspective: apps call a Service DNS name, and Kubernetes handles endpoint routing.

### Sound Bite 5

> Eureka favors availability, so clients must tolerate stale instances using timeouts, retries, and circuit breakers.

### Sound Bite 6

> Service mesh moves discovery, routing, retries, mTLS, and telemetry from application code to sidecar proxies.

### Sound Bite 7

> Readiness failure removes a pod from traffic; liveness failure restarts the pod.

### Sound Bite 8

> A registry outage should not instantly break all traffic if clients cache discovery data.

### Sound Bite 9

> Load balancing should be health-aware, version-aware, and zone-aware in production.

### Sound Bite 10

> The safest discovery design assumes endpoints can be stale and every remote call can fail.

---

## 44. Final Revision Diagram

```text
                 +----------------------+
                 |   Service Registry   |
                 | service -> instances |
                 +----------+-----------+
                            ^
                            |
             register / heartbeat / deregister
                            |
+------------------+        |        +------------------+
| payment-service  | -------+------> | order-service    |
| instance-1       |                 | discovery client |
+------------------+                 +--------+---------+
                                              |
                                              | lookup payment-service
                                              v
                                      +------------------+
                                      | Load Balancer    |
                                      +--------+---------+
                                               |
                                               v
                                      +------------------+
                                      | Chosen Instance  |
                                      +------------------+
```

Kubernetes version:

```text
order pod
  -> payment-service.default.svc.cluster.local
  -> CoreDNS
  -> ClusterIP
  -> EndpointSlice
  -> ready payment pod
```

Service mesh version:

```text
order app
  -> local Envoy sidecar
  -> Istio routing policy
  -> payment Envoy sidecar
  -> payment app
```

---

## 45. Final Checklist Before Interview

You should be able to explain:

- [ ] Why hardcoded IPs fail
- [ ] How registry stores instances
- [ ] Registration / heartbeat / deregistration flow
- [ ] Lease expiration and TTL tradeoffs
- [ ] Client-side vs server-side discovery
- [ ] Eureka flow and self-preservation
- [ ] Kubernetes Service, CoreDNS, Endpoints, EndpointSlice
- [ ] Liveness vs readiness
- [ ] Round robin, weighted, least connections, power of two choices
- [ ] Stale registry and eventual consistency
- [ ] Network partition and split brain
- [ ] Registry replication
- [ ] Service mesh and Envoy xDS
- [ ] Istio VirtualService and DestinationRule
- [ ] Metrics, tracing, and debugging commands
- [ ] Why timeout/retry/circuit breaker are mandatory

---

## 46. Ultra-Compressed Final Answer

If interviewer asks:

> Design service discovery for microservices.

Say:

> I would maintain a replicated service registry where each instance registers using service name, host, port, zone, version, and health metadata. Instances send periodic heartbeats, and the registry expires leases using TTL. Clients either query the registry directly and load balance locally, or call a stable platform endpoint such as a Kubernetes Service or Envoy proxy. Health checks and readiness probes ensure only safe instances receive traffic. Since registry data can be stale due to caching, partitions, or delayed heartbeats, every client call must use timeouts, retries with backoff, circuit breakers, and idempotency where needed. In production I would monitor heartbeat rate, endpoint count, DNS latency, 5xx rate, retries, circuit breaker openings, and trace calls by service version, zone, and pod.

---

## 47. What To Remember Forever

```text
Service discovery is not just lookup.
It is dynamic routing under failure.
```

The real production design is:

```text
Discovery
+ Health
+ Load Balancing
+ Caching
+ Replication
+ Timeouts
+ Retries
+ Circuit Breakers
+ Observability
```

That is the complete mental model.

---

# End of MiniServiceDiscovery
