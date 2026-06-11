# 021_Service_Mesh_Envoy_Istio_Discovery.md

# Service Mesh, Envoy, Istio, and Discovery

> **MiniServiceDiscovery Chapter 021**
>
> Goal: understand how service discovery changes when your microservices move from
> application-level discovery such as Eureka/OpenFeign to **infrastructure-level
> discovery through a service mesh** using **Envoy sidecars** and **Istio control plane**.

---

## 1. Why This Chapter Exists

Earlier chapters covered:

- service registry mental model
- instance registration and lookup
- Eureka / Consul / Kubernetes discovery
- client-side vs server-side load balancing
- stale registry and split-brain problems
- Kubernetes DNS + CoreDNS + Service discovery

In Kubernetes, service discovery usually looks like this:

```text
order-service
    |
    | DNS lookup: payment-service.default.svc.cluster.local
    v
Kubernetes Service virtual IP
    |
    v
one of payment-service Pods
```

This works, but production microservices need more than plain DNS:

- retries
- timeouts
- circuit breaking
- mTLS
- traffic splitting
- canary release
- observability
- request tracing
- authorization
- outlier detection
- locality-aware routing
- fault injection
- zero-trust service-to-service communication

If every team implements these inside application code, every service becomes heavy,
inconsistent, and hard to operate.

A **service mesh** moves these network concerns out of application code and into the
platform layer.

---

## 2. Core Mental Model

A service mesh is a dedicated infrastructure layer for service-to-service communication.

Instead of this:

```text
Service A application code
    |
    | HTTP call + retry + timeout + TLS + tracing + LB logic
    v
Service B
```

You get this:

```text
Service A app
    |
    | localhost call
    v
Envoy sidecar near Service A
    |
    | mTLS + routing + retry + LB + tracing
    v
Envoy sidecar near Service B
    |
    v
Service B app
```

The application code becomes simpler:

```java
restTemplate.getForObject("http://payment-service/payments/" + id, Payment.class);
```

But actual production behavior is controlled by mesh configuration:

```yaml
retries:
  attempts: 3
  perTryTimeout: 500ms
timeout: 2s
trafficPolicy:
  loadBalancer:
    simple: LEAST_REQUEST
```

---

## 3. Service Mesh in One Sentence

A service mesh is a network control layer where each service call is intercepted by a
proxy, and the platform controls discovery, routing, security, telemetry, and resilience
without changing business code.

---

## 4. Envoy vs Istio

Many beginners confuse Envoy and Istio.

They are different layers.

| Component | What it is | Responsibility |
|---|---|---|
| Envoy | Data-plane proxy | Handles actual traffic |
| Istio | Control plane + config model | Tells Envoy what to do |
| Kubernetes DNS | Basic service naming | Resolves service names |
| Kubernetes API | Source of truth for pods/services/endpoints | Used by Istio to build discovery state |
| Sidecar injector | Kubernetes admission webhook | Adds Envoy container to pods |
| xDS APIs | Envoy dynamic config APIs | Push routes/clusters/listeners/endpoints to Envoy |

Simple mental model:

```text
Kubernetes knows what exists.
Istio watches Kubernetes.
Istio generates Envoy config.
Envoy enforces that config on real traffic.
```

---

## 5. Data Plane and Control Plane

### 5.1 Data Plane

The data plane is the path where real requests flow.

In Istio, the data plane is mostly Envoy proxies.

```text
Request path:
client app -> client Envoy -> network -> server Envoy -> server app
```

Envoy handles:

- TCP / HTTP / gRPC proxying
- TLS termination and origination
- load balancing
- retries
- timeouts
- circuit breaking
- health checking
- metrics
- tracing headers
- access logs

### 5.2 Control Plane

The control plane does not handle normal application requests.

It configures proxies.

In modern Istio, the main control-plane component is `istiod`.

`istiod` handles:

- service discovery
- configuration translation
- certificate authority
- xDS config serving
- sidecar config generation
- pushing updates to Envoy

```text
Kubernetes API
    |
    v
istiod
    |
    | xDS config
    v
Envoy sidecars
```

---

## 6. How Discovery Works in Istio

In Kubernetes-only discovery:

```text
app -> DNS -> Service ClusterIP -> kube-proxy/ipvs/iptables -> Pod
```

In Istio discovery:

```text
app -> local Envoy
        |
        | Envoy already knows endpoints from istiod
        v
      remote Envoy -> app
```

Envoy does not need to perform DNS lookup for every request.

Instead:

1. Kubernetes API stores Services, Pods, Endpoints, EndpointSlices.
2. Istio watches Kubernetes objects.
3. Istio builds an internal service registry.
4. Istio pushes endpoint data to Envoy using xDS.
5. Envoy keeps an in-memory dynamic cluster table.
6. Envoy load-balances requests to healthy endpoints.

---

## 7. xDS Mental Model

Envoy is configured through dynamic discovery APIs collectively called **xDS**.

Important xDS types:

| xDS API | Meaning | Example |
|---|---|---|
| LDS | Listener Discovery Service | What port should Envoy listen on? |
| RDS | Route Discovery Service | Which route should this request match? |
| CDS | Cluster Discovery Service | What upstream service clusters exist? |
| EDS | Endpoint Discovery Service | Which pod IPs belong to each cluster? |
| SDS | Secret Discovery Service | Which TLS certs/secrets should Envoy use? |

Request flow:

```text
Incoming app request
    |
    v
Envoy Listener       <- LDS
    |
    v
Envoy Route         <- RDS
    |
    v
Envoy Cluster       <- CDS
    |
    v
Envoy Endpoint      <- EDS
    |
    v
Remote Pod
```

Very important interview line:

> Kubernetes DNS gives you a name. Istio/Envoy xDS gives you a continuously updated
> traffic-control graph: listeners, routes, clusters, endpoints, and secrets.

---

## 8. Sidecar Injection

In Istio sidecar mode, each application pod gets an additional Envoy container.

Example pod without mesh:

```text
order-pod
  - order-service container
```

Example pod with mesh:

```text
order-pod
  - order-service container
  - istio-proxy container
```

Usually injection is enabled at namespace level:

```bash
kubectl label namespace default istio-injection=enabled
```

Then Kubernetes admission webhook mutates new pods and adds:

- Envoy proxy container
- init container or CNI traffic redirection
- volumes for certs/config
- environment variables
- readiness probes

---

## 9. Traffic Interception

The application thinks it is calling another service directly.

But traffic is transparently redirected through Envoy.

Typical outbound flow:

```text
order app
  |
  | calls http://payment-service:8080
  v
iptables / CNI redirection
  |
  v
local Envoy outbound listener
  |
  v
remote payment Envoy
  |
  v
payment app
```

Typical inbound flow:

```text
network packet to payment pod
  |
  v
payment Envoy inbound listener
  |
  v
payment app localhost:8080
```

This is why service mesh can enforce security and routing without application code changes.

---

## 10. Discovery Example Without Service Mesh

Spring Cloud style:

```java
@FeignClient(name = "payment-service")
public interface PaymentClient {
    @GetMapping("/payments/{id}")
    PaymentDto getPayment(@PathVariable String id);
}
```

With Eureka/OpenFeign:

```text
order-service -> Eureka lookup -> choose instance -> HTTP call
```

Client-side app library handles:

- lookup
- load balancing
- retry
- timeout
- circuit breaker if integrated

Problem:

- Java apps use one library.
- Go apps use another.
- Node.js apps use another.
- Every team must configure resilience consistently.
- TLS and tracing become scattered.

---

## 11. Discovery Example With Istio Mesh

Application code:

```java
@RestController
public class OrderController {

    private final WebClient webClient;

    public OrderController(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://payment-service").build();
    }

    @GetMapping("/orders/{id}/payment")
    public Mono<String> getPayment(@PathVariable String id) {
        return webClient.get()
                .uri("/payments/{id}", id)
                .retrieve()
                .bodyToMono(String.class);
    }
}
```

No Eureka client.
No Ribbon.
No load balancer library.
No manual TLS code.

Routing config is outside the app:

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: payment-service
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

---

## 12. Istio CRDs Used for Discovery and Routing

Istio uses Kubernetes custom resources.

Most important ones:

| Istio Resource | Purpose |
|---|---|
| Gateway | Defines edge ingress listener |
| VirtualService | Defines routing rules |
| DestinationRule | Defines subsets, LB policy, TLS, circuit breaking |
| ServiceEntry | Adds external/non-Kubernetes services to mesh registry |
| Sidecar | Limits proxy config scope |
| PeerAuthentication | mTLS mode for workloads |
| RequestAuthentication | JWT validation |
| AuthorizationPolicy | Service-to-service access control |
| EnvoyFilter | Low-level Envoy customization |

---

## 13. VirtualService

A `VirtualService` controls where traffic goes.

Example: route all payment traffic to v1.

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: payment-service
spec:
  hosts:
  - payment-service
  http:
  - route:
    - destination:
        host: payment-service
        subset: v1
```

Example: canary 95/5 split.

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: payment-service-canary
spec:
  hosts:
  - payment-service
  http:
  - route:
    - destination:
        host: payment-service
        subset: stable
      weight: 95
    - destination:
        host: payment-service
        subset: canary
      weight: 5
```

Mental model:

```text
VirtualService = route table
DestinationRule = upstream behavior
```

---

## 14. DestinationRule

A `DestinationRule` defines policies for traffic after routing decision.

Example:

```yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: payment-service
spec:
  host: payment-service
  trafficPolicy:
    loadBalancer:
      simple: LEAST_REQUEST
    connectionPool:
      tcp:
        maxConnections: 100
      http:
        http1MaxPendingRequests: 1000
        maxRequestsPerConnection: 10
    outlierDetection:
      consecutive5xxErrors: 5
      interval: 10s
      baseEjectionTime: 30s
  subsets:
  - name: stable
    labels:
      version: v1
  - name: canary
    labels:
      version: v2
```

This gives:

- least-request load balancing
- connection limits
- pending request limits
- outlier detection
- version-based subsets

---

## 15. Subsets and Version Discovery

Kubernetes Service selects pods by labels.

Example:

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

Both v1 and v2 pods may match:

```yaml
labels:
  app: payment
  version: v1
```

```yaml
labels:
  app: payment
  version: v2
```

Istio `DestinationRule` creates logical subsets:

```yaml
subsets:
- name: v1
  labels:
    version: v1
- name: v2
  labels:
    version: v2
```

Now traffic can be split by version without creating separate Kubernetes Services.

---

## 16. Istio Discovery Flow Step-by-Step

Assume `order-service` calls `payment-service`.

### Step 1: Kubernetes Objects Exist

```text
Deployment/payment-v1
Deployment/payment-v2
Service/payment-service
Pods:
  payment-v1-abc 10.1.1.10
  payment-v1-def 10.1.1.11
  payment-v2-xyz 10.1.1.12
```

### Step 2: Istiod Watches Kubernetes

`istiod` watches:

- Services
- Pods
- EndpointSlices
- Namespaces
- Istio CRDs

### Step 3: Istiod Builds Service Registry

Internal model:

```text
Host: payment-service.default.svc.cluster.local
Endpoints:
  - 10.1.1.10 version=v1
  - 10.1.1.11 version=v1
  - 10.1.1.12 version=v2
```

### Step 4: Istiod Generates xDS

It sends to Envoy:

```text
Cluster: outbound|80||payment-service.default.svc.cluster.local
Endpoints:
  10.1.1.10:8080
  10.1.1.11:8080
  10.1.1.12:8080
Routes:
  90% subset v1
  10% subset v2
```

### Step 5: Envoy Handles Calls

When app calls:

```text
http://payment-service/payments/123
```

Envoy:

1. intercepts request
2. matches outbound listener
3. matches HTTP route
4. chooses cluster
5. applies traffic split
6. chooses endpoint
7. opens mTLS connection to remote Envoy
8. forwards request

---

## 17. ServiceEntry: External Service Discovery

Service mesh can also model external services.

Example: allow calls to external payment gateway.

```yaml
apiVersion: networking.istio.io/v1beta1
kind: ServiceEntry
metadata:
  name: stripe-api
spec:
  hosts:
  - api.stripe.com
  ports:
  - number: 443
    name: https
    protocol: HTTPS
  resolution: DNS
  location: MESH_EXTERNAL
```

Now Istio can apply:

- egress policy
- TLS origination
- observability
- allow/block rules
- retries/timeouts

Mental model:

```text
ServiceEntry = add non-Kubernetes service into Istio service registry
```

---

## 18. mTLS in Discovery

In plain Kubernetes:

```text
order pod -> payment pod
```

Traffic may be unencrypted inside cluster unless configured.

In Istio mTLS:

```text
order app -> order Envoy
order Envoy == mTLS ==> payment Envoy
payment Envoy -> payment app
```

The app does not manage certificates.

Istio manages workload identity using SPIFFE-like identities:

```text
spiffe://cluster.local/ns/default/sa/order-service-account
spiffe://cluster.local/ns/default/sa/payment-service-account
```

Example strict mTLS:

```yaml
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default-strict-mtls
  namespace: default
spec:
  mtls:
    mode: STRICT
```

---

## 19. AuthorizationPolicy

Discovery tells you where services are.
Authorization decides whether they may talk.

Example: only `order-service` can call `payment-service`.

```yaml
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: payment-allow-order
  namespace: default
spec:
  selector:
    matchLabels:
      app: payment
  action: ALLOW
  rules:
  - from:
    - source:
        principals:
        - cluster.local/ns/default/sa/order-sa
```

This is powerful because access control is based on workload identity,
not only IP address.

---

## 20. Retry and Timeout Policy

Example:

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: payment-service
spec:
  hosts:
  - payment-service
  http:
  - timeout: 2s
    retries:
      attempts: 3
      perTryTimeout: 500ms
      retryOn: 5xx,connect-failure,refused-stream
    route:
    - destination:
        host: payment-service
```

Important warning:

Retries can amplify traffic.

If one request retries 3 times under failure:

```text
1000 RPS original
3 retries
possible upstream pressure = 3000 extra attempts
```

Bad retry config can turn a small outage into a cascading failure.

Production rule:

```text
Retry only idempotent operations.
Use bounded attempts.
Use per-try timeout.
Combine with circuit breaking and rate limiting.
```

---

## 21. Circuit Breaking / Outlier Detection

Envoy supports outlier detection.

Example:

```yaml
trafficPolicy:
  outlierDetection:
    consecutive5xxErrors: 5
    interval: 10s
    baseEjectionTime: 30s
    maxEjectionPercent: 50
```

Meaning:

- if endpoint returns repeated 5xx
- eject it from load balancing temporarily
- test again after ejection time
- avoid sending traffic to bad pods

This is similar to a circuit breaker, but at proxy/endpoint level.

Application-level circuit breakers still matter when:

- fallback logic is business-specific
- you need local degradation behavior
- you need custom error mapping
- you need to avoid expensive app-level computation

---

## 22. Load Balancing in Envoy/Istio

Common policies:

| Policy | Meaning | Good For |
|---|---|---|
| ROUND_ROBIN | rotate endpoints | simple uniform workloads |
| LEAST_REQUEST | choose endpoint with fewer active requests | variable latency |
| RANDOM | random endpoint | simple large clusters |
| PASSTHROUGH | do not proxy as normal service | special cases |
| CONSISTENT_HASH | same key maps to same endpoint | sticky/session/cache locality |

Example consistent hash by header:

```yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: profile-service
spec:
  host: profile-service
  trafficPolicy:
    loadBalancer:
      consistentHash:
        httpHeaderName: x-user-id
```

Use case:

```text
Same user-id -> same profile pod
```

Useful for:

- sticky sessions
- local cache affinity
- reducing cache misses

Risk:

- uneven distribution if keys are skewed
- endpoint changes can remap traffic
- not a replacement for distributed cache

---

## 23. Canary Deployment With Istio

Deploy v2 but send only 5% traffic.

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: checkout-service
spec:
  hosts:
  - checkout-service
  http:
  - route:
    - destination:
        host: checkout-service
        subset: v1
      weight: 95
    - destination:
        host: checkout-service
        subset: v2
      weight: 5
```

DestinationRule:

```yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: checkout-service
spec:
  host: checkout-service
  subsets:
  - name: v1
    labels:
      version: v1
  - name: v2
    labels:
      version: v2
```

Production rollout:

```text
1% -> observe p99/error rate
5% -> observe
10% -> observe
25% -> observe
50% -> observe
100% -> complete
```

Rollback is easy:

```yaml
weight: 100 for v1
weight: 0 for v2
```

No app redeploy required.

---

## 24. Header-Based Routing

Send beta users to v2.

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: search-service
spec:
  hosts:
  - search-service
  http:
  - match:
    - headers:
        x-user-segment:
          exact: beta
    route:
    - destination:
        host: search-service
        subset: v2
  - route:
    - destination:
        host: search-service
        subset: v1
```

Use cases:

- beta testing
- internal dogfooding
- region-specific routing
- premium-user routing
- A/B testing

---

## 25. Fault Injection

Istio can inject failures for testing resilience.

Delay example:

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: payment-fault-test
spec:
  hosts:
  - payment-service
  http:
  - fault:
      delay:
        percentage:
          value: 10
        fixedDelay: 2s
    route:
    - destination:
        host: payment-service
```

Abort example:

```yaml
fault:
  abort:
    percentage:
      value: 5
    httpStatus: 503
```

This helps test:

- timeout behavior
- retry behavior
- UI degradation
- fallback logic
- alerting
- SLO burn rate

---

## 26. Observability

Service mesh gives uniform telemetry because all traffic passes through Envoy.

You get:

- request rate
- error rate
- duration / latency
- p50 / p95 / p99
- source workload
- destination workload
- response code
- retry count
- mTLS status
- bytes in/out

Golden signals:

```text
Traffic = request rate
Errors = 4xx/5xx/grpc errors
Latency = p95/p99
Saturation = connections/pending requests/resource pressure
```

Common tools:

- Prometheus
- Grafana
- Jaeger / Zipkin / Tempo
- Kiali
- OpenTelemetry

---

## 27. Kiali Mental Model

Kiali visualizes the service mesh graph.

Example:

```text
frontend
   |
   v
order-service
   |
   +--> payment-service
   |
   +--> inventory-service
```

It can show:

- edges between services
- traffic percentage
- error rate
- latency
- mTLS status
- missing policies
- unhealthy routes

For interviews:

> Kiali is not the mesh itself. It is an observability UI over mesh telemetry and config.

---

## 28. Service Mesh vs API Gateway

They are not the same.

| Feature | API Gateway | Service Mesh |
|---|---|---|
| Main traffic | North-south | East-west |
| Position | Edge of cluster | Between services |
| Handles external clients | Yes | Not primarily |
| Handles service-to-service | Limited | Yes |
| Auth | User/client auth | Workload/service auth |
| Examples | Spring Cloud Gateway, Kong, NGINX | Istio, Linkerd, Consul Connect |
| Proxy | Centralized gateway | Sidecars/ambient proxies |

Mental model:

```text
API Gateway = front door
Service Mesh = internal road system
```

Usually both exist:

```text
Internet
   |
   v
API Gateway / Ingress Gateway
   |
   v
Service Mesh internal services
```

---

## 29. Service Mesh vs Eureka

| Topic | Eureka/OpenFeign | Istio/Envoy |
|---|---|---|
| Discovery location | Application library | Infrastructure proxy |
| Language dependency | Yes | Mostly no |
| Registry | Eureka server | Kubernetes/Istio registry |
| Load balancing | Client-side library | Envoy |
| Retry/timeouts | App/library config | Mesh config |
| mTLS | App/platform custom | Built-in mesh feature |
| Traffic splitting | Harder | Native |
| Observability | App instrumentation | Uniform proxy telemetry |
| Failure blast radius | App config mismatch | Central policy risk |
| Operational complexity | Lower | Higher |

Strong interview answer:

> Eureka solves service lookup for applications. Istio solves traffic management,
> security, and observability for service-to-service communication. In Kubernetes,
> Istio usually builds on top of Kubernetes service discovery rather than replacing
> it completely.

---

## 30. Service Mesh vs Kubernetes DNS

| Capability | Kubernetes DNS | Istio Mesh |
|---|---|---|
| Name resolution | Yes | Uses/watches it indirectly |
| Endpoint awareness | Basic through Service/EndpointSlice | Rich endpoint config via EDS |
| Per-request routing | No | Yes |
| Canary traffic split | No native per-request split | Yes |
| mTLS | No | Yes |
| Retries/timeouts | No | Yes |
| Circuit breaking | No | Yes |
| Telemetry | Basic cluster metrics | Rich L7 telemetry |
| Authorization policy | NetworkPolicy-level | L7/workload identity policy |

Kubernetes DNS answers:

```text
What IP should this service name map to?
```

Istio answers:

```text
For this request, from this workload, to this service, with these headers,
which version/endpoint should receive traffic, using which security and retry policy?
```

---

## 31. Ambient Mesh Note

Traditional Istio uses sidecars.

Newer Istio deployments may use **ambient mesh**, where not every pod needs its own
sidecar. Ambient mesh separates traffic handling into shared node-level and waypoint
components.

Mental model:

```text
Sidecar mode:
  each pod gets Envoy

Ambient mode:
  node-level ztunnel handles secure L4
  optional waypoint proxy handles L7 policy
```

Why it matters:

- lower sidecar overhead
- easier adoption
- fewer pod restarts for onboarding
- different operational model

For interviews, sidecar mode is still the most commonly discussed mental model.

---

## 32. Failure Modes

### 32.1 Istiod Down

If `istiod` is down, existing Envoys usually continue serving with last known config.

But:

- new config will not propagate
- new services/endpoints may not be discovered
- cert rotation may eventually fail
- new pods may not get correct config

Mental model:

```text
Control plane down does not instantly stop data plane.
But the mesh becomes stale.
```

### 32.2 Envoy Sidecar Crash

If Envoy crashes inside pod:

- app may be alive
- traffic may fail
- readiness may fail depending config
- pod may be restarted

This is why sidecar readiness matters.

### 32.3 Bad VirtualService

A wrong route can send traffic to:

- wrong subset
- zero endpoints
- wrong host
- unexpected version

Example dangerous config:

```yaml
route:
- destination:
    host: payment-service
    subset: v3
```

If no `v3` endpoints exist, calls can fail.

### 32.4 Retry Storm

Bad retries:

```text
service A retries to B
service B retries to C
service C is slow
```

Can create multiplicative pressure.

```text
A attempts 3
B attempts 3
Total downstream attempts = 9
```

### 32.5 mTLS Misconfiguration

STRICT mTLS with non-mesh workload can break traffic.

Example:

```text
mesh service requires mTLS
legacy service sends plain HTTP
connection rejected
```

Migration strategy:

```text
PERMISSIVE -> observe -> fix clients -> STRICT
```

### 32.6 Too Much Proxy Config

Large clusters with many services can produce huge Envoy configs.

Symptoms:

- high memory usage
- slow config push
- Envoy CPU pressure
- delayed convergence

Mitigation:

- use Sidecar resource to limit egress scope
- split namespaces
- avoid global wildcard configs
- use ambient mesh where appropriate
- reduce unused ServiceEntries

---

## 33. Production Debugging Commands

Check injected sidecar:

```bash
kubectl get pod order-abc -o jsonpath='{.spec.containers[*].name}'
```

Expected:

```text
order-service istio-proxy
```

Check proxy status:

```bash
istioctl proxy-status
```

Inspect routes:

```bash
istioctl proxy-config routes order-abc.default
```

Inspect clusters:

```bash
istioctl proxy-config clusters order-abc.default
```

Inspect endpoints:

```bash
istioctl proxy-config endpoints order-abc.default
```

Inspect listeners:

```bash
istioctl proxy-config listeners order-abc.default
```

Check mTLS:

```bash
istioctl authn tls-check order-abc.default payment-service.default.svc.cluster.local
```

Analyze config problems:

```bash
istioctl analyze
```

View Envoy admin config dump:

```bash
kubectl exec order-abc -c istio-proxy -- curl localhost:15000/config_dump
```

---

## 34. Java App Example: Mesh-Friendly Service Call

Do not hardcode pod IPs.

Good:

```java
@Service
public class PaymentClient {

    private final WebClient client;

    public PaymentClient(WebClient.Builder builder) {
        this.client = builder
                .baseUrl("http://payment-service")
                .build();
    }

    public Mono<PaymentResponse> getPayment(String paymentId) {
        return client.get()
                .uri("/payments/{id}", paymentId)
                .retrieve()
                .bodyToMono(PaymentResponse.class);
    }
}
```

Mesh handles:

- choosing pod endpoint
- mTLS
- retry
- timeout
- canary
- metrics

But application still owns:

- business fallback
- idempotency key
- transaction correctness
- domain error handling
- validation
- logging business context

---

## 35. Idempotency With Mesh Retries

If mesh retries POST requests blindly, duplicate side effects can happen.

Bad endpoint:

```java
@PostMapping("/charge")
public ChargeResponse charge(@RequestBody ChargeRequest request) {
    paymentGateway.charge(request.card(), request.amount());
    return new ChargeResponse("SUCCESS");
}
```

If timeout happens after charge succeeded, retry may charge again.

Better:

```java
@PostMapping("/charge")
public ChargeResponse charge(
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @RequestBody ChargeRequest request
) {
    return paymentService.chargeOnce(idempotencyKey, request);
}
```

Idempotent service logic:

```java
@Service
public class PaymentService {

    private final PaymentRepository repository;
    private final ExternalGateway gateway;

    @Transactional
    public ChargeResponse chargeOnce(String key, ChargeRequest request) {
        return repository.findByIdempotencyKey(key)
                .map(PaymentRecord::toResponse)
                .orElseGet(() -> {
                    GatewayResult result = gateway.charge(request);
                    PaymentRecord record = repository.save(
                            PaymentRecord.success(key, request.amount(), result.reference())
                    );
                    return record.toResponse();
                });
    }
}
```

Rule:

```text
Mesh retries improve availability only when application operations are safe to retry.
```

---

## 36. Kubernetes YAML Example

Payment deployment v1:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payment-v1
spec:
  replicas: 3
  selector:
    matchLabels:
      app: payment
      version: v1
  template:
    metadata:
      labels:
        app: payment
        version: v1
    spec:
      serviceAccountName: payment-sa
      containers:
      - name: payment
        image: demo/payment:v1
        ports:
        - containerPort: 8080
```

Payment deployment v2:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payment-v2
spec:
  replicas: 1
  selector:
    matchLabels:
      app: payment
      version: v2
  template:
    metadata:
      labels:
        app: payment
        version: v2
    spec:
      serviceAccountName: payment-sa
      containers:
      - name: payment
        image: demo/payment:v2
        ports:
        - containerPort: 8080
```

Kubernetes Service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: payment-service
spec:
  selector:
    app: payment
  ports:
  - name: http
    port: 80
    targetPort: 8080
```

Istio DestinationRule:

```yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: payment-service
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

Istio VirtualService:

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: payment-service
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

---

## 37. End-to-End Request Dry Run

Request:

```text
GET /orders/101/payment
```

### Step 1: User hits order-service

```text
client -> ingress -> order-service
```

### Step 2: order-service calls payment-service

```java
GET http://payment-service/payments/101
```

### Step 3: iptables redirects outbound traffic

```text
order app -> order Envoy
```

### Step 4: Envoy checks route

Envoy has route:

```text
host = payment-service
90% -> subset v1
10% -> subset v2
```

### Step 5: Envoy selects endpoint

Example selected endpoint:

```text
10.1.1.11:8080, version=v1
```

### Step 6: Envoy opens mTLS connection

```text
order Envoy == mTLS ==> payment Envoy
```

### Step 7: payment Envoy forwards to local app

```text
payment Envoy -> localhost:8080
```

### Step 8: Response returns through proxies

```text
payment app -> payment Envoy -> order Envoy -> order app
```

### Step 9: Metrics emitted

Envoy records:

```text
source=order-service
destination=payment-service
response_code=200
duration_ms=43
protocol=http
mtls=true
```

---

## 38. Performance Cost

Service mesh is powerful but not free.

Costs:

- extra proxy hop
- CPU for Envoy
- memory per sidecar
- TLS handshake/crypto cost
- config push complexity
- debugging complexity
- operational learning curve

Common production overhead questions:

```text
Does every request become slower?
```

Usually there is some overhead, but acceptable if configured well.

Mitigation:

- right-size sidecar CPU/memory
- avoid unnecessary filters
- avoid excessive retries
- use connection pooling
- use keep-alive
- limit config scope
- monitor proxy p99 separately
- consider ambient mesh for large clusters

---

## 39. When Not to Use Service Mesh

Avoid service mesh when:

- system is small
- team lacks platform maturity
- few services exist
- simple Kubernetes DNS is enough
- latency budget is extremely tight
- you do not need mTLS/canary/traffic policy
- debugging capacity is low

Use service mesh when:

- many microservices exist
- multiple languages exist
- zero-trust internal security is required
- canary/release safety matters
- uniform telemetry matters
- service-to-service policy matters
- platform team can operate it

Strong decision rule:

```text
Do not adopt mesh because it is trendy.
Adopt mesh when repeated cross-cutting network problems become more expensive
than operating the mesh itself.
```

---

## 40. Interview Answer: How Istio Discovery Works

Use this answer:

> In Kubernetes, services are discovered through DNS and Service/EndpointSlice objects.
> Istio builds on top of that. `istiod` watches Kubernetes services, pods, endpoints,
> and Istio routing resources. It converts this state into Envoy xDS configuration:
> listeners, routes, clusters, endpoints, and secrets. Each pod has an Envoy sidecar
> that receives this dynamic config. When an application calls `payment-service`,
> traffic is intercepted by the local Envoy, which uses its in-memory xDS config to
> choose the route, apply retries/timeouts/mTLS, load-balance to a healthy endpoint,
> and forward traffic to the remote Envoy and application.

---

## 41. Interview Answer: Service Mesh vs Eureka

> Eureka is mainly a service registry and discovery mechanism. The application or
> client library performs lookup and load balancing. Istio is a service mesh where
> Envoy proxies handle discovery-derived routing, load balancing, mTLS, telemetry,
> retries, timeouts, and canary traffic. In Kubernetes, Istio does not usually replace
> Kubernetes discovery; it watches Kubernetes service and endpoint state and pushes
> richer dynamic config to Envoy.

---

## 42. Interview Answer: Why Envoy Does Not Query DNS Every Time

> Envoy receives endpoint information from the control plane through EDS, the Endpoint
> Discovery Service. So it has a local in-memory view of upstream endpoints. This avoids
> relying on DNS lookup for every request and allows richer behavior such as health-aware
> load balancing, outlier detection, subsets, locality, and traffic splitting.

---

## 43. Interview Answer: What Happens During Pod Scale-Up

When `payment-service` scales from 3 to 5 pods:

```text
1. Kubernetes creates new pods.
2. EndpointSlice updates with new pod IPs.
3. istiod observes EndpointSlice change.
4. istiod recalculates endpoint config.
5. istiod pushes EDS update to Envoy proxies.
6. Envoys include new endpoints in LB pool.
7. Traffic gradually reaches new pods.
```

Important:

```text
There is propagation delay.
Discovery is eventually consistent.
Readiness probes prevent traffic before pods are ready.
```

---

## 44. Interview Answer: What Happens During Pod Failure

```text
1. Pod fails or readiness becomes false.
2. Kubernetes removes endpoint from ready endpoints.
3. istiod watches endpoint update.
4. istiod pushes EDS update.
5. Envoy removes endpoint from load-balancing pool.
6. Outlier detection may eject bad endpoint earlier if failures are observed.
```

This combines:

- Kubernetes readiness
- endpoint updates
- Istio xDS propagation
- Envoy outlier detection

---

## 45. Common Mistakes

### Mistake 1: Thinking mesh replaces Kubernetes Service

Istio usually uses Kubernetes Service objects as service identity and discovery source.

### Mistake 2: Putting all resilience in mesh

Mesh can retry/time out, but app still needs:

- idempotency
- fallbacks
- business error handling
- graceful degradation

### Mistake 3: Enabling strict mTLS instantly

Legacy/non-mesh services may break.

### Mistake 4: Infinite retries

Bad retry policy can overload dependencies.

### Mistake 5: No resource limits for sidecar

Envoy consumes CPU/memory. It must be monitored.

### Mistake 6: Too many wildcard ServiceEntries

Can create security holes and config bloat.

### Mistake 7: Not naming service ports

Istio protocol detection works better with named ports.

Good:

```yaml
ports:
- name: http
  port: 80
  targetPort: 8080
```

Bad:

```yaml
ports:
- port: 80
```

---

## 46. Strong Production Checklist

Before using mesh in production:

- [ ] Namespaces labeled correctly for injection
- [ ] Sidecars injected into expected pods
- [ ] Service ports named correctly
- [ ] Readiness/liveness probes configured
- [ ] VirtualService routes tested
- [ ] DestinationRule subsets match pod labels
- [ ] Retry policy only for safe operations
- [ ] Timeout policy defined
- [ ] Outlier detection configured for critical services
- [ ] mTLS migration planned
- [ ] AuthorizationPolicy tested
- [ ] Metrics dashboards created
- [ ] p95/p99 latency tracked
- [ ] Proxy CPU/memory tracked
- [ ] `istioctl analyze` clean
- [ ] rollback strategy exists
- [ ] canary weights automated
- [ ] config ownership defined

---

## 47. Mini Project: Build Mental Simulation

Implement a simplified service mesh discovery simulator.

### Data Structures

```java
class Service {
    String name;
    List<Endpoint> endpoints;
}

class Endpoint {
    String ip;
    int port;
    String version;
    boolean healthy;
}

class VirtualRoute {
    String serviceName;
    List<WeightedDestination> destinations;
}

class WeightedDestination {
    String subset;
    int weight;
}

class DestinationRule {
    String serviceName;
    Map<String, Map<String, String>> subsetLabels;
}
```

### Goal

Given:

```text
payment-service endpoints:
  10.0.0.1 version=v1 healthy=true
  10.0.0.2 version=v1 healthy=true
  10.0.0.3 version=v2 healthy=true

VirtualService:
  v1 weight 90
  v2 weight 10
```

Simulate:

- route matching
- subset selection
- endpoint filtering
- load balancing
- unhealthy endpoint removal

### Java Sketch

```java
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class Endpoint {
    final String ip;
    final int port;
    final String version;
    boolean healthy;

    Endpoint(String ip, int port, String version, boolean healthy) {
        this.ip = ip;
        this.port = port;
        this.version = version;
        this.healthy = healthy;
    }

    @Override
    public String toString() {
        return ip + ":" + port + " version=" + version + " healthy=" + healthy;
    }
}

class WeightedDestination {
    final String subset;
    final int weight;

    WeightedDestination(String subset, int weight) {
        this.subset = subset;
        this.weight = weight;
    }
}

class MeshRouter {
    private final List<Endpoint> endpoints;
    private final List<WeightedDestination> destinations;
    private int roundRobinIndex = 0;

    MeshRouter(List<Endpoint> endpoints, List<WeightedDestination> destinations) {
        this.endpoints = endpoints;
        this.destinations = destinations;
    }

    public Endpoint route() {
        String subset = chooseSubset();
        List<Endpoint> candidates = endpoints.stream()
                .filter(e -> e.healthy)
                .filter(e -> e.version.equals(subset))
                .toList();

        if (candidates.isEmpty()) {
            throw new IllegalStateException("No healthy endpoints for subset " + subset);
        }

        Endpoint selected = candidates.get(roundRobinIndex % candidates.size());
        roundRobinIndex++;
        return selected;
    }

    private String chooseSubset() {
        int total = destinations.stream().mapToInt(d -> d.weight).sum();
        int random = ThreadLocalRandom.current().nextInt(total);
        int cumulative = 0;

        for (WeightedDestination d : destinations) {
            cumulative += d.weight;
            if (random < cumulative) {
                return d.subset;
            }
        }

        return destinations.get(destinations.size() - 1).subset;
    }
}

public class MeshDiscoveryDemo {
    public static void main(String[] args) {
        List<Endpoint> endpoints = List.of(
                new Endpoint("10.0.0.1", 8080, "v1", true),
                new Endpoint("10.0.0.2", 8080, "v1", true),
                new Endpoint("10.0.0.3", 8080, "v2", true)
        );

        List<WeightedDestination> rules = List.of(
                new WeightedDestination("v1", 90),
                new WeightedDestination("v2", 10)
        );

        MeshRouter router = new MeshRouter(endpoints, rules);

        Map<String, Integer> counts = new HashMap<>();

        for (int i = 0; i < 1000; i++) {
            Endpoint endpoint = router.route();
            counts.merge(endpoint.version, 1, Integer::sum);
        }

        System.out.println(counts);
    }
}
```

Expected output approximately:

```text
{v1=900, v2=100}
```

Not exact because random weighted routing is probabilistic.

---

## 48. Real-World Design: Order Payment Mesh

```text
client
  |
  v
ingress-gateway
  |
  v
order-service
  |
  +--> payment-service
  |
  +--> inventory-service
  |
  +--> notification-service
```

Mesh policies:

### order -> payment

```text
timeout: 2s
retry: 2 attempts for connect-failure/5xx only
mTLS: strict
authorization: only order can call payment
outlierDetection: eject after 5 errors
```

### order -> inventory

```text
timeout: 500ms
retry: 1
fallback in app: reserve later / pending status
```

### order -> notification

```text
async via Kafka preferred
no synchronous hard dependency
```

Important design principle:

```text
Do not make every dependency synchronous just because mesh makes calls easier.
```

---

## 49. Strong System Design Explanation

When designing high-scale microservices:

```text
API Gateway handles external traffic.
Service mesh handles internal service-to-service traffic.
Kubernetes handles scheduling and basic service discovery.
Istio watches Kubernetes and configures Envoy.
Envoy handles real traffic enforcement.
Application code focuses on domain logic.
```

At 10k+ RPS:

- set strict timeouts
- avoid retry storms
- use idempotency
- monitor p99
- tune connection pools
- prefer async for non-critical dependencies
- use canary release
- apply mTLS gradually
- define ownership of routing config
- keep mesh config small

---

## 50. Final Cheat Sheet

### Key Terms

| Term | Meaning |
|---|---|
| Service Mesh | Infrastructure layer for service-to-service communication |
| Envoy | Proxy that handles traffic |
| Istio | Control plane and policy model |
| istiod | Main Istio control-plane component |
| Sidecar | Proxy container injected beside app |
| xDS | Dynamic Envoy configuration APIs |
| LDS | Listener config |
| RDS | Route config |
| CDS | Cluster config |
| EDS | Endpoint config |
| SDS | Secret/cert config |
| VirtualService | Routing rules |
| DestinationRule | Upstream policy/subsets |
| ServiceEntry | External service registry entry |
| PeerAuthentication | mTLS mode |
| AuthorizationPolicy | Access control |
| Outlier Detection | Eject unhealthy endpoints |
| Canary | Gradual weighted rollout |

### One-Line Memory Hooks

```text
Kubernetes DNS = name to service.
EndpointSlice = service to pod IPs.
Istio = watches service state and policies.
xDS = pushes dynamic proxy config.
Envoy = enforces routing/security/telemetry.
VirtualService = where traffic goes.
DestinationRule = how traffic behaves after destination is chosen.
ServiceEntry = add external service to mesh.
PeerAuthentication = mTLS mode.
AuthorizationPolicy = who can call whom.
```

### Best Interview Closing

> A service mesh changes service discovery from an application-library problem into
> an infrastructure-controlled traffic graph. Envoy sidecars receive dynamic endpoint
> and route configuration from Istio. This allows Kubernetes services to be discovered
> and consumed with richer behavior: load balancing, canary routing, retries, timeouts,
> mTLS, authorization, and uniform telemetry. The tradeoff is additional operational
> complexity, resource overhead, and the need to carefully design retry, timeout, and
> rollout policies.

---

## 51. What To Remember Forever

```text
Service discovery is not only "find IP".
At production scale, discovery becomes:
  find the right endpoint,
  for this caller,
  for this request,
  with correct security,
  with correct traffic policy,
  with observability,
  while surviving failures.
```

That is why service mesh exists.
