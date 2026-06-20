# 020_Kubernetes_DNS_CoreDNS_ServiceDiscovery.md

# Kubernetes DNS, CoreDNS & Service Discovery Internals

> MiniServiceDiscovery deep note  
> Goal: understand how Kubernetes service discovery works internally using DNS, CoreDNS, Services, Endpoints, EndpointSlices, kube-proxy, and Spring Boot microservices.

---

## 0. Why This Chapter Matters

In traditional microservices, service discovery often means:

```text
Service A -> asks Registry -> gets IPs of Service B -> calls one IP
```

Examples:

- Eureka
- Consul
- Zookeeper
- Nacos
- custom registry

In Kubernetes, the mental model changes.

Most applications do **not** call a registry directly.

Instead, they call a stable DNS name:

```text
http://payment-service:8080/pay
```

Kubernetes makes that name resolve to a stable virtual service identity.

Internally, Kubernetes connects:

```text
DNS name
  -> Kubernetes Service
  -> EndpointSlice / Endpoints
  -> Pod IPs
  -> kube-proxy / iptables / IPVS / eBPF routing
```

So Kubernetes service discovery is mostly DNS-based plus service abstraction.

---

## 1. The Core Mental Model

### Old world: hardcoded IP

```text
order-service -> 10.0.1.23:8080
```

Problem:

- pod dies
- IP changes
- scale up/down changes backend list
- deployment rollout changes instances
- network partition creates stale IPs

### Registry world

```text
order-service -> Eureka -> payment-service instances
```

Application is aware of discovery.

### Kubernetes world

```text
order-service -> DNS name -> payment-service
```

Application does not usually know pod IPs.

It only knows:

```text
payment-service.default.svc.cluster.local
```

or shorter:

```text
payment-service
```

Kubernetes and CoreDNS handle resolution.

---

## 2. Kubernetes Service Discovery Objects

The main objects are:

```text
Pod
Service
EndpointSlice
CoreDNS
kube-proxy
Node networking
```

### Pod

A Pod gets an IP.

Example:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: payment-abc123
  labels:
    app: payment
spec:
  containers:
    - name: payment
      image: payment:1.0
      ports:
        - containerPort: 8080
```

Pod IP may be:

```text
10.244.2.17
```

But this IP is unstable.

When pod restarts, it may become:

```text
10.244.3.42
```

So clients should not call Pod IP directly.

---

## 3. Service Object

A Kubernetes Service gives stable identity to a dynamic set of Pods.

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
    - port: 8080
      targetPort: 8080
  type: ClusterIP
```

This creates:

```text
payment-service.default.svc.cluster.local
```

And a stable virtual IP:

```text
ClusterIP = 10.96.15.20
```

Clients call:

```text
http://payment-service:8080
```

The Service sends traffic to pods matching:

```yaml
app: payment
```

---

## 4. DNS Name Format

Kubernetes DNS names follow this pattern:

```text
<service-name>.<namespace>.svc.<cluster-domain>
```

Default cluster domain:

```text
cluster.local
```

Example:

```text
payment-service.default.svc.cluster.local
```

Inside the same namespace, this works:

```text
payment-service
```

Across namespace:

```text
payment-service.payments
```

Fully qualified:

```text
payment-service.payments.svc.cluster.local
```

---

## 5. Short Name Resolution

Inside a pod, `/etc/resolv.conf` usually contains search domains.

Example:

```text
search default.svc.cluster.local svc.cluster.local cluster.local
nameserver 10.96.0.10
options ndots:5
```

When app calls:

```text
payment-service
```

DNS resolver may try:

```text
payment-service.default.svc.cluster.local
payment-service.svc.cluster.local
payment-service.cluster.local
payment-service
```

This is why short names work inside the same namespace.

---

## 6. CoreDNS Role

CoreDNS is the DNS server running inside Kubernetes.

Usually deployed in `kube-system` namespace:

```bash
kubectl get pods -n kube-system -l k8s-app=kube-dns
```

Example output:

```text
NAME                       READY   STATUS    RESTARTS   AGE
coredns-668d6bf9bc-abcde   1/1     Running   0          10d
coredns-668d6bf9bc-fghij   1/1     Running   0          10d
```

CoreDNS answers questions like:

```text
What is the IP of payment-service.default.svc.cluster.local?
```

Answer:

```text
10.96.15.20
```

That IP is the Service ClusterIP.

---

## 7. CoreDNS ConfigMap

CoreDNS behavior is controlled by a ConfigMap.

```bash
kubectl get configmap coredns -n kube-system -o yaml
```

Typical Corefile:

```text
.:53 {
    errors
    health {
       lameduck 5s
    }
    ready
    kubernetes cluster.local in-addr.arpa ip6.arpa {
       pods insecure
       fallthrough in-addr.arpa ip6.arpa
       ttl 30
    }
    prometheus :9153
    forward . /etc/resolv.conf {
       max_concurrent 1000
    }
    cache 30
    loop
    reload
    loadbalance
}
```

### Important plugins

| Plugin | Purpose |
|---|---|
| `kubernetes` | Answers DNS queries for Services and Pods |
| `cache` | Caches DNS answers |
| `forward` | Forwards external DNS queries |
| `prometheus` | Exposes CoreDNS metrics |
| `ready` | Readiness endpoint |
| `health` | Health endpoint |
| `loadbalance` | Randomizes DNS answer order |
| `loop` | Detects forwarding loops |
| `reload` | Reloads config when ConfigMap changes |

---

## 8. Service Discovery Flow

When `order-service` calls:

```text
http://payment-service:8080/pay
```

The flow is:

```text
1. Java app asks OS resolver for payment-service
2. Pod resolver checks /etc/resolv.conf
3. Query goes to CoreDNS service IP, usually 10.96.0.10
4. CoreDNS checks Kubernetes API/service cache
5. CoreDNS returns ClusterIP of payment-service
6. Java app connects to ClusterIP:8080
7. kube-proxy/eBPF forwards traffic to one healthy payment pod
```

Important point:

```text
DNS discovery gives Service IP.
Load balancing usually happens after that at Service routing layer.
```

---

## 9. Service vs EndpointSlice

A Service is stable.

Pods are dynamic.

Kubernetes needs a mapping:

```text
Service -> Pod IPs
```

This mapping is stored using EndpointSlices.

Example:

```bash
kubectl get endpointslice
```

Output:

```text
NAME                    ADDRESSTYPE   PORTS   ENDPOINTS
payment-service-abc12   IPv4          8080    10.244.1.5,10.244.2.8
```

EndpointSlice contains backend pod IPs.

Example:

```yaml
apiVersion: discovery.k8s.io/v1
kind: EndpointSlice
metadata:
  name: payment-service-abc12
  labels:
    kubernetes.io/service-name: payment-service
addressType: IPv4
ports:
  - name: http
    port: 8080
endpoints:
  - addresses:
      - 10.244.1.5
    conditions:
      ready: true
  - addresses:
      - 10.244.2.8
    conditions:
      ready: true
```

---

## 10. Why EndpointSlice Replaced Endpoints

Older Kubernetes used `Endpoints`.

Problem:

```text
One large Endpoints object can become huge.
```

If a Service has thousands of pods, every update modifies a big object.

EndpointSlice solves this by sharding endpoints.

Example:

```text
payment-service-slice-1 -> 100 endpoints
payment-service-slice-2 -> 100 endpoints
payment-service-slice-3 -> 100 endpoints
```

Benefits:

- better scalability
- smaller updates
- less API server load
- better watch performance
- supports topology hints
- supports dual-stack IPv4/IPv6 better

---

## 11. ClusterIP Service Discovery

Most internal service-to-service traffic uses `ClusterIP`.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: user-service
spec:
  type: ClusterIP
  selector:
    app: user
  ports:
    - port: 8080
      targetPort: 8080
```

DNS:

```text
user-service.default.svc.cluster.local -> ClusterIP
```

Clients:

```text
http://user-service:8080
```

Use this for:

- internal microservices
- backend to backend communication
- API to database proxy
- worker to internal services

---

## 12. NodePort Service Discovery

NodePort exposes service on every node IP.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: user-service-nodeport
spec:
  type: NodePort
  selector:
    app: user
  ports:
    - port: 8080
      targetPort: 8080
      nodePort: 30080
```

Access:

```text
http://<node-ip>:30080
```

Internally, Service still gets DNS:

```text
user-service-nodeport.default.svc.cluster.local
```

But NodePort is mostly for external access or development.

---

## 13. LoadBalancer Service Discovery

Cloud Kubernetes can provision external load balancer.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: user-service-lb
spec:
  type: LoadBalancer
  selector:
    app: user
  ports:
    - port: 80
      targetPort: 8080
```

Flow:

```text
Internet
  -> Cloud Load Balancer
  -> NodePort
  -> ClusterIP
  -> Pod
```

For internal calls, other services can still call:

```text
user-service-lb.default.svc.cluster.local
```

---

## 14. Headless Service

A headless Service has no ClusterIP.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: cassandra
spec:
  clusterIP: None
  selector:
    app: cassandra
  ports:
    - port: 9042
      targetPort: 9042
```

DNS does not return one virtual IP.

Instead, DNS returns Pod IPs directly.

Example:

```text
cassandra.default.svc.cluster.local -> 10.244.1.5, 10.244.2.8, 10.244.3.9
```

Use headless services for:

- StatefulSet
- Cassandra
- Kafka
- ZooKeeper
- Redis Cluster
- databases needing stable member identity
- client-side load balancing
- peer discovery

---

## 15. StatefulSet DNS

StatefulSet pods get stable names.

Example StatefulSet:

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: kafka
spec:
  serviceName: kafka-headless
  replicas: 3
  selector:
    matchLabels:
      app: kafka
  template:
    metadata:
      labels:
        app: kafka
    spec:
      containers:
        - name: kafka
          image: bitnami/kafka
```

With headless Service:

```text
kafka-0.kafka-headless.default.svc.cluster.local
kafka-1.kafka-headless.default.svc.cluster.local
kafka-2.kafka-headless.default.svc.cluster.local
```

This is important because Kafka brokers need stable advertised identity.

---

## 16. DNS Records in Kubernetes

### A / AAAA records

For normal Service:

```text
payment-service.default.svc.cluster.local A 10.96.15.20
```

For headless Service:

```text
cassandra.default.svc.cluster.local A 10.244.1.5
cassandra.default.svc.cluster.local A 10.244.2.8
```

### SRV records

For named ports:

```yaml
ports:
  - name: http
    port: 8080
```

SRV query:

```text
_http._tcp.payment-service.default.svc.cluster.local
```

Returns service port information.

---

## 17. kube-proxy Role

CoreDNS only resolves names.

It does not forward application traffic.

After DNS returns ClusterIP, traffic routing is handled by kube-proxy or replacement dataplane.

Modes:

```text
iptables
IPVS
eBPF/Cilium
```

Flow:

```text
Client Pod -> ClusterIP -> node network rules -> selected backend Pod
```

kube-proxy watches:

- Services
- EndpointSlices

And programs network rules.

---

## 18. iptables Mode Mental Model

In iptables mode, kube-proxy creates NAT rules.

Example:

```text
10.96.15.20:8080 -> randomly DNAT to 10.244.1.5:8080
10.96.15.20:8080 -> randomly DNAT to 10.244.2.8:8080
```

Conceptually:

```text
if destination == payment ClusterIP:
    choose backend pod
    rewrite destination to pod IP
```

This is kernel-level routing, not application-level load balancing.

---

## 19. IPVS Mode Mental Model

IPVS mode uses Linux IP Virtual Server.

It is more optimized for many Services/backends.

It supports algorithms like:

- round robin
- least connection
- source hashing
- destination hashing

Conceptual flow:

```text
ClusterIP is virtual server
Pod IPs are real servers
IPVS selects backend
```

---

## 20. eBPF Service Routing

Modern clusters may use Cilium or similar eBPF dataplane.

Instead of iptables chains, eBPF programs run inside kernel hooks.

Benefits:

- lower overhead at scale
- better observability
- faster service translation
- advanced network policies
- topology-aware routing

Mental model:

```text
ClusterIP packet enters node
eBPF program checks service map
eBPF picks backend endpoint
packet forwarded efficiently
```

---

## 21. Service Discovery vs Load Balancing

Important interview distinction:

```text
Service discovery answers: where is service identity?
Load balancing answers: which backend instance gets this request?
```

In Kubernetes:

```text
DNS -> Service ClusterIP
Service dataplane -> Pod load balancing
```

For normal ClusterIP service:

```text
Client only sees one stable IP.
```

For headless service:

```text
Client sees multiple pod IPs and can do client-side load balancing.
```

---

## 22. Java Spring Boot Example

### Service A calls Service B

`order-service` wants to call `payment-service`.

Inside Kubernetes:

```java
@Service
public class PaymentClient {
    private final RestTemplate restTemplate;

    public PaymentClient(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(500))
                .setReadTimeout(Duration.ofSeconds(2))
                .build();
    }

    public PaymentResponse pay(PaymentRequest request) {
        String url = "http://payment-service:8080/api/payments";

        return restTemplate.postForObject(
                url,
                request,
                PaymentResponse.class
        );
    }
}
```

No Eureka required.

No hardcoded Pod IP required.

The name `payment-service` is resolved by Kubernetes DNS.

---

## 23. Spring WebClient Example

```java
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient paymentWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://payment-service:8080")
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(2))
                ))
                .build();
    }
}
```

Client:

```java
@Service
public class PaymentGatewayClient {

    private final WebClient paymentWebClient;

    public PaymentGatewayClient(WebClient paymentWebClient) {
        this.paymentWebClient = paymentWebClient;
    }

    public Mono<PaymentResult> createPayment(PaymentRequest request) {
        return paymentWebClient.post()
                .uri("/api/payments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResult.class)
                .timeout(Duration.ofSeconds(3));
    }
}
```

Kubernetes DNS makes this work:

```text
payment-service -> ClusterIP -> payment pods
```

---

## 24. Spring Cloud Kubernetes

Spring Cloud Kubernetes can integrate Kubernetes discovery with Spring Cloud abstractions.

But for many applications, simple DNS is enough.

### Simple DNS style

```text
http://inventory-service:8080
```

### Spring Cloud DiscoveryClient style

Application can query Kubernetes API and discover service instances.

Dependency example:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-kubernetes-client-discovery</artifactId>
</dependency>
```

Usage:

```java
@RestController
public class DiscoveryDebugController {

    private final DiscoveryClient discoveryClient;

    public DiscoveryDebugController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @GetMapping("/debug/services")
    public List<String> services() {
        return discoveryClient.getServices();
    }

    @GetMapping("/debug/services/{name}")
    public List<ServiceInstance> instances(@PathVariable String name) {
        return discoveryClient.getInstances(name);
    }
}
```

This is useful when:

- application wants instance metadata
- custom routing needed
- Spring Cloud LoadBalancer used
- migrating from Eureka model

But the most Kubernetes-native approach remains:

```text
DNS + Service
```

---

## 25. Kubernetes Deployment + Service Example

### payment-service deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payment-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: payment-service
  template:
    metadata:
      labels:
        app: payment-service
    spec:
      containers:
        - name: payment-service
          image: payment-service:1.0
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 5
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
```

### Service

```yaml
apiVersion: v1
kind: Service
metadata:
  name: payment-service
spec:
  type: ClusterIP
  selector:
    app: payment-service
  ports:
    - name: http
      port: 8080
      targetPort: 8080
```

Now any pod in same namespace can call:

```text
http://payment-service:8080
```

---

## 26. Readiness Probe and Discovery

Readiness matters for service discovery.

If pod is not ready:

```text
Pod should not receive traffic.
```

Kubernetes removes unready pod from Service endpoints.

Flow:

```text
Pod readiness false
  -> EndpointSlice marks endpoint ready=false
  -> kube-proxy removes/avoids backend
  -> traffic stops
```

This prevents traffic to warming or broken pods.

Example readiness endpoint in Spring Boot:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

Kubernetes:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
```

---

## 27. Liveness vs Readiness vs Startup Probe

| Probe | Meaning | If fails |
|---|---|---|
| Liveness | Is process alive? | Restart container |
| Readiness | Can receive traffic? | Remove from endpoints |
| Startup | Has slow app started? | Protect app from premature liveness kill |

For service discovery, readiness is the most directly important.

If readiness fails, Kubernetes discovery should stop routing traffic to that pod.

---

## 28. DNS Caching Problem

DNS caching can create surprising behavior.

Example:

```text
Java app resolves payment-service once and caches forever.
```

Then service IP or backend changes.

For normal ClusterIP Service, this is usually okay because ClusterIP is stable.

But for headless service, DNS returns Pod IPs.

If Java caches old Pod IPs too long:

```text
client may call dead pod IP
```

### Java DNS cache TTL

Java has DNS cache settings:

```text
networkaddress.cache.ttl
networkaddress.cache.negative.ttl
```

Can be configured:

```bash
-Dsun.net.inetaddr.ttl=30
-Dsun.net.inetaddr.negative.ttl=10
```

For headless services, always think about client DNS cache behavior.

---

## 29. CoreDNS Cache TTL

CoreDNS `cache 30` means DNS answers may be cached for around 30 seconds.

Corefile:

```text
cache 30
```

The Kubernetes plugin can also set TTL:

```text
kubernetes cluster.local {
    ttl 30
}
```

Tradeoff:

| Low TTL | High TTL |
|---|---|
| Faster updates | Less DNS load |
| More DNS QPS | Slower failover |
| Better for dynamic pods | Better for stable services |

For normal ClusterIP, high-ish caching is usually fine.

For headless services, be more careful.

---

## 30. DNS Query Amplification Due to ndots

Pod resolv.conf often has:

```text
options ndots:5
```

If the app queries external name:

```text
api.stripe.com
```

Because it has fewer than 5 dots, resolver may first try search domains:

```text
api.stripe.com.default.svc.cluster.local
api.stripe.com.svc.cluster.local
api.stripe.com.cluster.local
api.stripe.com
```

This can increase DNS queries.

Solution:

Use trailing dot for external FQDN in some cases:

```text
api.stripe.com.
```

Or tune ndots where appropriate.

---

## 31. Debugging DNS From Inside Pod

Run temporary debug pod:

```bash
kubectl run dns-debug \
  --image=busybox:1.36 \
  --restart=Never \
  -- sleep 3600
```

Exec:

```bash
kubectl exec -it dns-debug -- sh
```

Test:

```bash
nslookup payment-service
```

Expected:

```text
Server:    10.96.0.10
Address:   10.96.0.10:53

Name:      payment-service.default.svc.cluster.local
Address:   10.96.15.20
```

Using `dig` image:

```bash
kubectl run dig-debug \
  --image=infoblox/dnstools \
  --restart=Never \
  -- sleep 3600
```

```bash
kubectl exec -it dig-debug -- dig payment-service.default.svc.cluster.local
```

---

## 32. Debugging Service Endpoints

Check Service:

```bash
kubectl get svc payment-service
```

Check endpoints:

```bash
kubectl get endpoints payment-service
```

Check EndpointSlices:

```bash
kubectl get endpointslice -l kubernetes.io/service-name=payment-service
```

Describe Service:

```bash
kubectl describe svc payment-service
```

Common issue:

```text
Service has no endpoints.
```

That usually means selector does not match pod labels or pods are not ready.

---

## 33. Common Failure: Service Selector Mismatch

Deployment:

```yaml
metadata:
  labels:
    app: payment
```

Service:

```yaml
selector:
  app: payment-service
```

Mismatch.

Result:

```text
Service exists
DNS resolves
But no backend endpoints
Traffic fails
```

Fix:

```yaml
selector:
  app: payment
```

Debug:

```bash
kubectl get pods --show-labels
kubectl describe svc payment-service
kubectl get endpoints payment-service
```

---

## 34. Common Failure: DNS Works but HTTP Fails

Example:

```bash
nslookup payment-service
```

works.

But:

```bash
curl http://payment-service:8080
```

fails.

Possible reasons:

- wrong targetPort
- pod not listening on expected port
- readiness probe failing
- NetworkPolicy blocking traffic
- app bound to localhost only
- container crashed
- service selector wrong
- pod firewall/security sidecar issue

Check:

```bash
kubectl get svc payment-service -o yaml
kubectl get pods -l app=payment-service
kubectl logs deploy/payment-service
kubectl describe pod <pod>
kubectl get endpointslice -l kubernetes.io/service-name=payment-service -o yaml
```

---

## 35. Common Failure: CoreDNS Down

Symptoms:

```text
service names cannot resolve
external domains cannot resolve
pods show UnknownHostException
```

Check:

```bash
kubectl get pods -n kube-system -l k8s-app=kube-dns
kubectl logs -n kube-system deploy/coredns
kubectl get svc -n kube-system kube-dns
```

Restart CoreDNS carefully:

```bash
kubectl rollout restart deployment/coredns -n kube-system
```

---

## 36. Common Failure: External DNS Slow

App calls:

```text
https://api.stripe.com
```

Slow due to search domain expansion.

Check DNS query count with CoreDNS metrics.

Possible mitigations:

- use connection pooling
- avoid resolving per request
- tune JVM DNS TTL
- use trailing dot for FQDN if supported
- tune `ndots`
- improve CoreDNS replica count
- use NodeLocal DNSCache

---

## 37. NodeLocal DNSCache

NodeLocal DNSCache runs a DNS cache on each node.

Without it:

```text
Pod -> CoreDNS Service -> CoreDNS Pod
```

With NodeLocal DNSCache:

```text
Pod -> local node DNS cache -> CoreDNS if miss
```

Benefits:

- lower latency
- reduced CoreDNS load
- better resilience
- fewer conntrack issues
- improves large-cluster DNS performance

Useful at scale when many pods create heavy DNS traffic.

---

## 38. NetworkPolicy and DNS

If NetworkPolicy is strict, pods may be unable to query DNS.

DNS usually uses:

```text
UDP 53
TCP 53
```

CoreDNS is in `kube-system`.

Example allow DNS egress:

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-dns
spec:
  podSelector: {}
  policyTypes:
    - Egress
  egress:
    - to:
        - namespaceSelector:
            matchLabels:
              kubernetes.io/metadata.name: kube-system
      ports:
        - protocol: UDP
          port: 53
        - protocol: TCP
          port: 53
```

If DNS is blocked, service discovery fails before HTTP call even starts.

---

## 39. Multi-Namespace Service Calls

Same namespace:

```text
http://payment-service:8080
```

Different namespace:

```text
http://payment-service.payments:8080
```

Full:

```text
http://payment-service.payments.svc.cluster.local:8080
```

Best practice:

- use short name inside same namespace
- use namespace-qualified name across namespaces
- avoid ambiguous names

Example:

```java
String url = "http://payment-service.payments:8080/api/payments";
```

---

## 40. ExternalName Service

ExternalName maps Kubernetes service name to external DNS name.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: external-payment-gateway
spec:
  type: ExternalName
  externalName: api.payment-provider.com
```

App calls:

```text
http://external-payment-gateway.default.svc.cluster.local
```

DNS returns CNAME:

```text
api.payment-provider.com
```

Use cases:

- hide external dependency behind internal name
- migrate external provider
- environment-specific mapping

Caution:

- no Kubernetes proxying
- no endpoint readiness
- DNS only
- port handling still application responsibility

---

## 41. Service Discovery With Ingress

Ingress is for external HTTP routing.

Flow:

```text
Internet
  -> Ingress Controller
  -> Kubernetes Service
  -> Pods
```

Ingress does not replace internal service discovery.

Internal services still usually call:

```text
http://payment-service:8080
```

External clients call:

```text
https://api.company.com/payments
```

---

## 42. Service Discovery With Gateway API

Gateway API is newer and more expressive than Ingress.

But same internal principle remains:

```text
Gateway -> Service -> EndpointSlice -> Pods
```

Application service discovery inside cluster still often uses DNS names.

---

## 43. Service Mesh Interaction

With Istio/Linkerd/Consul service mesh:

```text
Application -> sidecar proxy -> service
```

DNS may still resolve service name.

But actual traffic routing may be controlled by sidecar.

Example with Istio:

```text
order pod
  -> Envoy sidecar
  -> payment-service
  -> Envoy sidecar on payment pod
  -> payment app
```

Mesh can add:

- retries
- mTLS
- circuit breaking
- traffic splitting
- canary routing
- observability
- authorization policy

Kubernetes DNS still provides base identity.

---

## 44. Comparison: Eureka vs Kubernetes DNS

| Feature | Eureka | Kubernetes DNS |
|---|---|---|
| Discovery style | Application queries registry | Application uses DNS |
| Registry object | Eureka server | Kubernetes API + CoreDNS |
| Instance identity | app instance | Pod endpoint |
| Load balancing | client-side Ribbon/SCLB | Service dataplane |
| Health removal | Eureka heartbeat/lease | readiness + endpoints |
| Failure mode | stale registry cache | stale DNS/cache/endpoints |
| Good for | non-K8s microservices | K8s-native services |

Important:

```text
In Kubernetes, Eureka is often unnecessary for internal services.
```

But Eureka may still be used when:

- hybrid K8s + VM environment
- legacy Spring Cloud apps
- cross-datacenter registry pattern
- non-K8s clients need discovery

---

## 45. Kubernetes DNS vs Consul

| Feature | Kubernetes DNS | Consul |
|---|---|---|
| Native environment | Kubernetes | VM, hybrid, multi-DC |
| Discovery API | DNS + K8s API | DNS + HTTP API |
| Health checks | K8s probes | Consul checks |
| Multi-DC | not primary feature | strong feature |
| Service mesh | external/mesh add-on | Consul Connect |
| Best for | cluster-local discovery | hybrid infra discovery |

---

## 46. Production Best Practices

### 1. Use stable Service names

Good:

```text
payment-service
inventory-service
user-service
```

Bad:

```text
payment-v1-pod-abc123
10.244.1.5
```

### 2. Always configure readiness probes

Without readiness, traffic can hit unready pods.

### 3. Use namespace-qualified names across namespaces

Good:

```text
payment-service.payments
```

### 4. Avoid DNS lookup per request

Use connection pooling.

### 5. Be careful with headless services

Client must handle multiple IPs and stale DNS.

### 6. Monitor CoreDNS

Track:

- DNS QPS
- error rate
- latency
- cache hit ratio
- SERVFAIL/NXDOMAIN
- CoreDNS CPU/memory

### 7. Scale CoreDNS

Large clusters need enough CoreDNS replicas.

### 8. Use NodeLocal DNSCache for high QPS clusters

Especially if many pods resolve frequently.

### 9. Keep Service selectors precise

Wrong selector is a common production issue.

### 10. Use NetworkPolicy carefully

Allow DNS egress.

---

## 47. Observability Metrics

CoreDNS exposes Prometheus metrics.

Common metrics:

```text
coredns_dns_requests_total
coredns_dns_responses_total
coredns_dns_request_duration_seconds
coredns_cache_hits_total
coredns_cache_misses_total
coredns_forward_requests_total
```

Useful alerts:

```text
High DNS latency
High SERVFAIL rate
CoreDNS pods down
CoreDNS CPU throttling
Low cache hit ratio
Sudden NXDOMAIN spike
```

Example PromQL:

```promql
rate(coredns_dns_requests_total[5m])
```

DNS latency:

```promql
histogram_quantile(
  0.99,
  sum(rate(coredns_dns_request_duration_seconds_bucket[5m])) by (le)
)
```

---

## 48. Scaling CoreDNS

Check current deployment:

```bash
kubectl get deployment coredns -n kube-system
```

Scale:

```bash
kubectl scale deployment coredns -n kube-system --replicas=4
```

But do not blindly scale.

First check:

- DNS QPS
- CPU throttling
- memory usage
- node distribution
- autoscaler
- cache hit/miss
- query patterns

CoreDNS autoscaler may already exist in managed clusters.

---

## 49. DNS Lookup From Java: Production Notes

Java HTTP clients should reuse connections.

Bad:

```java
for each request:
    new RestTemplate()
    resolve DNS
    create TCP connection
```

Good:

```java
shared WebClient / RestTemplate
connection pool
timeouts
retries with backoff
circuit breaker
bulkhead
```

Example Apache HttpClient pool:

```java
@Bean
public RestTemplate restTemplate() {
    PoolingHttpClientConnectionManager cm =
            new PoolingHttpClientConnectionManager();

    cm.setMaxTotal(200);
    cm.setDefaultMaxPerRoute(50);

    CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(cm)
            .evictIdleConnections(TimeValue.ofSeconds(30))
            .build();

    HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory(httpClient);

    factory.setConnectTimeout(500);
    factory.setConnectionRequestTimeout(500);
    factory.setReadTimeout(2000);

    return new RestTemplate(factory);
}
```

This prevents excessive DNS/TCP overhead.

---

## 50. Resilience4j With Kubernetes DNS

Kubernetes service discovery does not replace resilience.

You still need:

- timeout
- retry
- circuit breaker
- bulkhead
- rate limiter

Example:

```java
@Service
public class SafePaymentClient {

    private final WebClient paymentWebClient;

    public SafePaymentClient(WebClient paymentWebClient) {
        this.paymentWebClient = paymentWebClient;
    }

    @CircuitBreaker(name = "payment", fallbackMethod = "fallback")
    @TimeLimiter(name = "payment")
    public CompletableFuture<PaymentResult> pay(PaymentRequest request) {
        return paymentWebClient.post()
                .uri("/api/payments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResult.class)
                .timeout(Duration.ofSeconds(2))
                .toFuture();
    }

    public CompletableFuture<PaymentResult> fallback(
            PaymentRequest request,
            Throwable ex
    ) {
        return CompletableFuture.completedFuture(
                PaymentResult.failed("PAYMENT_TEMPORARILY_UNAVAILABLE")
        );
    }
}
```

Why still needed?

Because service discovery only finds service.

It does not guarantee:

- downstream is fast
- downstream is healthy after accepting connection
- database behind downstream is healthy
- downstream is not overloaded

---

## 51. Rollout Behavior

During Deployment rollout:

```text
old pods running
new pods starting
new pods become ready
EndpointSlices update
traffic shifts gradually
old pods terminate
```

If readiness is correct, service discovery avoids unready new pods.

Bad readiness causes:

```text
traffic sent to app before warmup
startup errors
5xx during rollout
```

Good readiness checks:

- app started
- DB connection ready if required
- cache warm enough if required
- essential dependencies available
- migrations done if app depends on them

---

## 52. Graceful Shutdown and Discovery

When pod is terminated:

```text
SIGTERM sent
readiness should fail
endpoint removed
app drains requests
container exits
```

Spring Boot graceful shutdown:

```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

Kubernetes:

```yaml
terminationGracePeriodSeconds: 45
```

PreStop hook example:

```yaml
lifecycle:
  preStop:
    exec:
      command: ["sh", "-c", "sleep 10"]
```

Why sleep?

To allow EndpointSlice/kube-proxy updates to propagate before process exits.

---

## 53. Mini Implementation: Simple DNS-Based Discovery in Java

This mini code shows how DNS returns addresses.

```java
import java.net.InetAddress;
import java.util.Arrays;

public class DnsDiscoveryDemo {

    public static void main(String[] args) throws Exception {
        String serviceName = args.length > 0
                ? args[0]
                : "payment-service.default.svc.cluster.local";

        InetAddress[] addresses = InetAddress.getAllByName(serviceName);

        System.out.println("Service: " + serviceName);
        System.out.println("Resolved addresses:");

        Arrays.stream(addresses)
                .forEach(addr -> System.out.println(" - " + addr.getHostAddress()));
    }
}
```

Run inside pod:

```bash
java DnsDiscoveryDemo payment-service
```

For ClusterIP service:

```text
Resolved addresses:
 - 10.96.15.20
```

For headless service:

```text
Resolved addresses:
 - 10.244.1.5
 - 10.244.2.8
 - 10.244.3.9
```

---

## 54. Mini Implementation: Client-Side Load Balancing for Headless Service

For headless Service, app may receive multiple pod IPs.

Simple round robin:

```java
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;

public class HeadlessRoundRobinClient {

    private final String serviceDnsName;
    private final int port;
    private final HttpClient httpClient;
    private final AtomicInteger index = new AtomicInteger();

    public HeadlessRoundRobinClient(String serviceDnsName, int port) {
        this.serviceDnsName = serviceDnsName;
        this.port = port;
        this.httpClient = HttpClient.newHttpClient();
    }

    public String get(String path) throws Exception {
        InetAddress[] addresses = InetAddress.getAllByName(serviceDnsName);

        if (addresses.length == 0) {
            throw new IllegalStateException("No endpoints found for " + serviceDnsName);
        }

        int selected = Math.floorMod(index.getAndIncrement(), addresses.length);
        String ip = addresses[selected].getHostAddress();

        URI uri = URI.create("http://" + ip + ":" + port + path);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .timeout(java.time.Duration.ofSeconds(2))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}
```

Important limitation:

```text
This simple version ignores stale IPs, health, retries, TLS hostname, and connection pooling details.
```

Production systems need more robust load balancers.

---

## 55. Why Normal Service Is Usually Better Than Headless

For typical stateless Spring Boot services:

Use:

```text
ClusterIP Service
```

Because:

- stable DNS name
- stable ClusterIP
- simple client
- Kubernetes handles pod selection
- readiness integration
- less DNS cache risk

Use headless only when application needs direct pod identity.

---

## 56. Strong Interview Answer

### Question

How does service discovery work in Kubernetes?

### Answer

Kubernetes provides service discovery mainly through DNS and Service objects. Each Service gets a stable DNS name like `payment-service.default.svc.cluster.local`. CoreDNS watches Kubernetes service and endpoint data through the Kubernetes API and answers DNS queries for those names. For a normal ClusterIP Service, DNS resolves the service name to a stable virtual IP. When a pod sends traffic to that ClusterIP, kube-proxy or an eBPF dataplane forwards the traffic to one of the ready backend pods using EndpointSlice data. EndpointSlices maintain the dynamic mapping between Services and Pod IPs. Readiness probes control whether a pod is included in endpoints, so unready pods are removed from traffic. For headless Services, DNS returns the pod IPs directly instead of a ClusterIP, which is useful for StatefulSets like Kafka or Cassandra.

---

## 57. FAANG-Style Deep-Dive Questions

### Q1. Why does Kubernetes use Services instead of direct Pod IPs?

Because Pod IPs are ephemeral. Pods can die, restart, move nodes, or be replaced during deployments. A Service gives stable identity and routing over dynamic pods.

### Q2. What happens when a pod becomes unready?

Its readiness condition becomes false. Kubernetes updates EndpointSlice information. kube-proxy/eBPF stops routing Service traffic to that pod.

### Q3. Does CoreDNS load balance traffic?

For normal ClusterIP Services, CoreDNS returns the Service IP. Traffic load balancing happens in the service dataplane. For headless Services, CoreDNS can return multiple pod IPs, and clients may load balance.

### Q4. Why might DNS resolve but requests still fail?

DNS only confirms the Service name exists. HTTP can fail because endpoints are empty, targetPort is wrong, pod is not listening, readiness failed, NetworkPolicy blocks traffic, or app crashed.

### Q5. What is the difference between Endpoints and EndpointSlice?

Endpoints is the older single object containing all backend addresses. EndpointSlice shards endpoints into smaller objects, improving scalability and watch efficiency for large services.

### Q6. Why is headless Service useful?

It allows DNS to return individual Pod IPs. This is useful for StatefulSets and systems requiring stable member identity, such as Kafka, Cassandra, ZooKeeper, and Redis Cluster.

### Q7. How does Kubernetes DNS compare with Eureka?

Eureka is an application-level registry where clients query instances. Kubernetes DNS is infrastructure-level discovery where clients use DNS names and Kubernetes Service routing handles backend selection.

### Q8. What is NodeLocal DNSCache?

It runs a DNS cache on each node to reduce CoreDNS load and DNS latency. Pods query local DNS cache first, and cache misses go to CoreDNS.

### Q9. What is the role of kube-proxy in service discovery?

kube-proxy does not resolve DNS. It watches Services and EndpointSlices and programs node-level networking rules so ClusterIP traffic is routed to backend pods.

### Q10. Why can `ndots:5` cause extra DNS traffic?

Names with fewer than five dots are expanded using search domains first. External names may generate several failed cluster-local lookups before the real external lookup.

---

## 58. Debugging Checklist

When service call fails:

```text
1. Is Service present?
   kubectl get svc

2. Does DNS resolve?
   nslookup payment-service

3. Does Service have endpoints?
   kubectl get endpoints payment-service
   kubectl get endpointslice -l kubernetes.io/service-name=payment-service

4. Do Service selectors match Pod labels?
   kubectl get pods --show-labels
   kubectl describe svc payment-service

5. Are pods ready?
   kubectl get pods
   kubectl describe pod <pod>

6. Is targetPort correct?
   kubectl get svc payment-service -o yaml

7. Is app listening on 0.0.0.0, not only localhost?
   kubectl logs <pod>

8. Is NetworkPolicy blocking DNS or HTTP?
   kubectl get networkpolicy

9. Is CoreDNS healthy?
   kubectl get pods -n kube-system -l k8s-app=kube-dns

10. Are there DNS latency/errors?
   Check CoreDNS metrics
```

---

## 59. Production Incident Example

### Symptom

Order service gets:

```text
java.net.UnknownHostException: payment-service
```

### Likely causes

- CoreDNS down
- wrong namespace
- DNS blocked by NetworkPolicy
- service name typo
- pod resolv.conf broken
- kube-dns Service unavailable

### Debug

```bash
kubectl get svc payment-service
kubectl get svc -n kube-system kube-dns
kubectl get pods -n kube-system -l k8s-app=kube-dns
kubectl exec -it order-pod -- cat /etc/resolv.conf
kubectl exec -it order-pod -- nslookup payment-service
```

### Fix

Depends on root cause:

- fix service name
- use namespace-qualified DNS
- allow DNS egress
- restore CoreDNS
- fix cluster DNS configuration

---

## 60. Another Incident Example: No Endpoints

### Symptom

DNS resolves:

```text
payment-service -> 10.96.15.20
```

But HTTP request times out.

Check:

```bash
kubectl get endpoints payment-service
```

Output:

```text
NAME              ENDPOINTS   AGE
payment-service   <none>      2h
```

Root cause:

```text
Service selector does not match pod labels
```

Fix selector.

---

## 61. Another Incident Example: Bad Readiness

### Symptom

During rollout, 5xx spikes.

Reason:

```text
New pods marked ready before app is truly ready.
```

Fix readiness:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 15
  periodSeconds: 5
  failureThreshold: 3
```

Also ensure Spring readiness checks real app readiness.

---

## 62. Important Commands Cheat Sheet

```bash
# Services
kubectl get svc
kubectl describe svc payment-service
kubectl get svc payment-service -o yaml

# Endpoints
kubectl get endpoints payment-service
kubectl get endpointslice -l kubernetes.io/service-name=payment-service
kubectl get endpointslice -o wide

# DNS
kubectl run dns-debug --image=busybox:1.36 --restart=Never -- sleep 3600
kubectl exec -it dns-debug -- nslookup payment-service

# CoreDNS
kubectl get pods -n kube-system -l k8s-app=kube-dns
kubectl logs -n kube-system deploy/coredns
kubectl get configmap coredns -n kube-system -o yaml

# Pods and labels
kubectl get pods --show-labels
kubectl get pods -l app=payment-service
kubectl describe pod <pod-name>

# Rollout
kubectl rollout status deployment/payment-service
kubectl rollout restart deployment/payment-service

# NetworkPolicy
kubectl get networkpolicy
kubectl describe networkpolicy <name>
```

---

## 63. System Design Mental Model

For an internal microservice call:

```text
OrderService Java code
  -> http://payment-service:8080
  -> Pod DNS resolver
  -> CoreDNS
  -> Service ClusterIP
  -> kube-proxy/eBPF
  -> ready payment pod
  -> payment container
```

For a StatefulSet database:

```text
App
  -> cassandra.default.svc.cluster.local
  -> CoreDNS
  -> multiple Cassandra pod IPs
  -> client driver chooses node
```

For external traffic:

```text
User
  -> Load Balancer / Ingress / Gateway
  -> Kubernetes Service
  -> EndpointSlice
  -> Pod
```

---

## 64. Design Tradeoffs

### ClusterIP Service

Pros:

- simple
- stable IP
- Kubernetes load balancing
- good for stateless apps

Cons:

- less direct endpoint control
- connection-level balancing may not be perfect for long-lived connections

### Headless Service

Pros:

- direct pod identity
- useful for stateful systems
- client-side load balancing possible

Cons:

- DNS caching issues
- client must handle endpoint changes
- stale IP risk
- more complex client behavior

### Service Mesh

Pros:

- advanced traffic control
- mTLS
- retries
- canary
- observability

Cons:

- operational complexity
- sidecar/resource overhead
- debugging complexity

---

## 65. Common Misconceptions

### Misconception 1

CoreDNS sends traffic to pods.

Correction:

```text
CoreDNS resolves names. kube-proxy/eBPF routes traffic.
```

### Misconception 2

DNS returns pod IPs for every Service.

Correction:

```text
Normal ClusterIP Service returns ClusterIP.
Headless Service returns pod IPs.
```

### Misconception 3

Service discovery means service is healthy.

Correction:

```text
DNS resolution only means name exists.
Health depends on endpoints/readiness/application state.
```

### Misconception 4

Kubernetes removes need for timeouts/retries.

Correction:

```text
Discovery only finds services. Resilience is still app responsibility.
```

### Misconception 5

Liveness probe controls traffic.

Correction:

```text
Readiness controls traffic.
Liveness controls restart.
```

---

## 66. Mini Project Exercise

Build a mini cluster with:

```text
order-service
payment-service
inventory-service
```

### Step 1: Deploy payment-service

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payment-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: payment-service
  template:
    metadata:
      labels:
        app: payment-service
    spec:
      containers:
        - name: payment-service
          image: hashicorp/http-echo
          args:
            - "-text=payment ok"
          ports:
            - containerPort: 5678
```

### Step 2: Service

```yaml
apiVersion: v1
kind: Service
metadata:
  name: payment-service
spec:
  selector:
    app: payment-service
  ports:
    - port: 8080
      targetPort: 5678
```

### Step 3: Test DNS

```bash
kubectl run test-client --image=curlimages/curl --restart=Never -- sleep 3600
kubectl exec -it test-client -- curl http://payment-service:8080
```

Expected:

```text
payment ok
```

### Step 4: Scale payment

```bash
kubectl scale deployment payment-service --replicas=5
kubectl get endpointslice -l kubernetes.io/service-name=payment-service
```

Observe endpoint changes.

---

## 67. Real-World Architecture Example

E-commerce system:

```text
frontend-gateway
order-service
payment-service
inventory-service
shipping-service
notification-service
```

Internal calls:

```text
order-service -> payment-service.payments:8080
order-service -> inventory-service.inventory:8080
order-service -> shipping-service.shipping:8080
```

Each service has:

- Deployment
- ClusterIP Service
- readiness probe
- liveness probe
- HPA
- metrics
- logs
- tracing

Service discovery uses DNS.

Traffic safety uses:

- timeout
- retry
- circuit breaker
- idempotency
- bulkhead
- rate limiting

Observability uses:

- Prometheus
- Grafana
- Loki/ELK
- Jaeger/Tempo
- CoreDNS metrics

---

## 68. How This Connects to MiniServiceDiscovery

Previous chapters covered:

```text
Registry
Registration
Lookup
Heartbeat
Lease
Stale instances
Self-preservation
Client-side/server-side load balancing
Replication
```

Kubernetes maps these ideas differently:

| MiniServiceDiscovery Concept | Kubernetes Equivalent |
|---|---|
| Service registry | Kubernetes API server |
| Registered instance | Pod endpoint |
| Heartbeat | kubelet pod status + readiness |
| Lease expiry | pod deletion/not ready |
| Lookup | DNS query to CoreDNS |
| Instance list | EndpointSlice |
| Load balancer | kube-proxy/IPVS/eBPF |
| Registry replication | API server/etcd control plane |
| Stale registry | stale EndpointSlice/DNS cache |
| Health-aware routing | readiness probes |
| Split brain | control plane/network partition behavior |

This is why understanding Eureka/registry internals helps Kubernetes.

The same core problem exists:

```text
How do clients find healthy service instances in a dynamic distributed system?
```

Kubernetes answer:

```text
DNS + Service + EndpointSlice + readiness + dataplane routing.
```

---

## 69. Last-Minute Revision

Remember this chain:

```text
Service name
  -> CoreDNS
  -> ClusterIP
  -> kube-proxy/eBPF
  -> EndpointSlice
  -> Ready Pod IP
```

For headless:

```text
Service name
  -> CoreDNS
  -> Pod IPs directly
  -> client chooses endpoint
```

Most common bugs:

```text
wrong namespace
wrong selector
no endpoints
bad readiness
wrong targetPort
CoreDNS down
NetworkPolicy blocks DNS
Java DNS cache issue
ndots query amplification
```

Best answer:

```text
Kubernetes service discovery is DNS-based. CoreDNS resolves Service names. Services provide stable virtual identity. EndpointSlices track ready pod backends. kube-proxy or eBPF routes traffic from ClusterIP to pods. Readiness controls endpoint membership.
```

---

## 70. One-Page Cheat Sheet

```text
Normal service:
payment-service.default.svc.cluster.local -> ClusterIP

Headless service:
cassandra.default.svc.cluster.local -> Pod IPs

Same namespace:
http://payment-service:8080

Different namespace:
http://payment-service.payments:8080

CoreDNS:
resolves DNS

kube-proxy/eBPF:
routes ClusterIP traffic

EndpointSlice:
stores backend pod IPs

Readiness:
controls if pod receives traffic

Liveness:
controls restart

Startup:
protects slow startup

NodeLocal DNSCache:
reduces DNS latency/load

ExternalName:
CNAME to external DNS

Ingress/Gateway:
external HTTP routing to Service

Service Mesh:
advanced traffic policy on top of service discovery
```

---

## 71. Final Interview Summary

Kubernetes hides dynamic pod IPs behind stable Service names. Applications call DNS names such as `payment-service`. CoreDNS resolves those names using Kubernetes service data. For normal ClusterIP services, DNS returns a stable virtual IP, and kube-proxy/IPVS/eBPF routes the connection to a ready backend pod using EndpointSlice data. For headless services, DNS returns pod IPs directly, which is useful for StatefulSets. Readiness probes decide whether a pod is included in endpoints. Production issues usually come from selector mismatch, no endpoints, bad readiness probes, CoreDNS failures, DNS caching, NetworkPolicy, or wrong namespace names.

---
