# 015_CoreDNS_Service_Discovery.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why CoreDNS And Service Discovery Exist

In Kubernetes, Pods are temporary.

A Pod can be created, deleted, rescheduled, replaced, or restarted at any time.

That means Pod IPs are not a stable contract.

Example:

```text
order-service Pod today:

order-abc -> 10.244.1.12

After restart:

order-xyz -> 10.244.2.44
```

If another application stores `10.244.1.12`, it will eventually break.

This is the same problem we solved with Service.

But Service gives a stable virtual IP and stable name.

CoreDNS makes that stable name resolvable.

Mental model:

```text
Service = stable identity for changing Pods
CoreDNS = phone book that resolves Service name to Service IP
```

One picture:

```text
Payment Pod
   |
   | calls http://order-service:8080
   v
CoreDNS resolves order-service
   |
   v
Service ClusterIP
   |
   v
Ready Order Pods
```

Do not memorize CoreDNS as “DNS server inside Kubernetes.”

Understand it as:

```text
CoreDNS = dynamic DNS system backed by Kubernetes API objects
```

Traditional DNS mostly maps names to IPs from static records.

Kubernetes DNS maps service names to changing cluster objects.

---

# 2. The Wrong Way To Think About Service Discovery

Bad mental model:

```text
Pod A knows Pod B IP.
```

This is fragile.

Another bad mental model:

```text
Application should discover all Pod IPs manually.
```

That makes every application responsible for infrastructure logic.

Correct model:

```text
Application calls a stable service name.
Kubernetes resolves it.
Kubernetes routes it.
```

Wrong:

```text
payment-service -> 10.244.1.12
```

Correct:

```text
payment-service -> http://order-service.default.svc.cluster.local
```

Simplified:

```text
payment-service -> http://order-service
```

Inside same namespace, short name usually works.

ASCII:

```text
Wrong World

Payment Pod
   |
   | hardcoded 10.244.1.12
   v
Order Pod

Pod restarted -> IP changed -> failure


Kubernetes World

Payment Pod
   |
   | order-service
   v
CoreDNS
   |
   v
Service
   |
   v
Current Ready Pods
```

Do not memorize DNS suffixes first.

Understand why stable naming exists.

---

# 3. Real World Analogy: Company Reception Desk

Imagine a company office.

You want to meet the billing team.

Bad approach:

```text
Go directly to chair number 47.
```

But people move.

Correct approach:

```text
Ask reception: where is Billing Team?
```

Reception checks current office map and sends you to the correct place.

Kubernetes equivalent:

```text
Chair number       = Pod IP
Billing Team name  = Service name
Reception          = CoreDNS
Office routing     = kube-proxy / CNI
```

Diagram:

```text
Visitor
  |
  | "Where is Billing?"
  v
Reception
  |
  | "Go to Room B"
  v
Billing Team
```

Kubernetes:

```text
Client Pod
  |
  | "Where is order-service?"
  v
CoreDNS
  |
  | "ClusterIP is 10.96.20.15"
  v
Service Routing
  |
  v
Ready Pods
```

The important lesson:

```text
Clients should know business names, not machine addresses.
```

---

# 4. Kubernetes DNS Big Picture

CoreDNS normally runs as Pods in the `kube-system` namespace.

It is usually exposed by a Service named `kube-dns`.

```text
kubectl get pods -n kube-system -l k8s-app=kube-dns
kubectl get svc -n kube-system kube-dns
```

Big picture:

```text
Application Pod
      |
      | DNS query: order-service.default.svc.cluster.local
      v
Pod DNS config
      |
      v
kube-dns Service IP
      |
      v
CoreDNS Pod
      |
      v
Kubernetes API knowledge
      |
      v
DNS answer: ClusterIP
```

ASCII:

```text
+----------------------+
| payment Pod          |
| /etc/resolv.conf     |
+----------+-----------+
           |
           | DNS query
           v
+----------------------+
| kube-dns Service     |
| ClusterIP            |
+----------+-----------+
           |
           v
+----------------------+
| CoreDNS Pods         |
+----------+-----------+
           |
           | watches / queries Kubernetes API
           v
+----------------------+
| Services / Endpoints |
+----------------------+
```

CoreDNS is not guessing.

It knows Kubernetes objects.

---

# 5. Service Discovery Core Flow

Assume:

```text
Namespace: default
Service: order-service
ClusterIP: 10.96.20.15
Pods:
  order-a -> 10.244.1.11
  order-b -> 10.244.2.18
```

Client call:

```text
http://order-service:8080/api/orders
```

Dry run:

```text
1. Application asks OS to resolve "order-service".
2. Pod DNS config sends query to kube-dns Service IP.
3. kube-dns Service sends query to CoreDNS Pod.
4. CoreDNS checks Kubernetes plugin records.
5. CoreDNS returns Service ClusterIP: 10.96.20.15.
6. Client opens TCP connection to 10.96.20.15:8080.
7. kube-proxy / dataplane translates ClusterIP to a ready Pod IP.
8. Request reaches one order-service Pod.
```

Diagram:

```text
Java code
  |
  | WebClient -> order-service
  v
DNS Lookup
  |
  v
CoreDNS
  |
  | returns ClusterIP
  v
Service IP
  |
  v
kube-proxy/IPVS/iptables
  |
  v
Pod IP
```

Separate the two jobs:

```text
DNS resolves name -> Service IP
kube-proxy routes Service IP -> Pod IP
```

This separation is key.

---

# 6. CoreDNS vs kube-proxy

Many beginners confuse CoreDNS and kube-proxy.

They solve different layers.

```text
CoreDNS:
  name -> IP

kube-proxy:
  Service IP -> Pod IP
```

Example:

```text
order-service.default.svc.cluster.local
        |
        | CoreDNS
        v
10.96.20.15
        |
        | kube-proxy / IPVS / iptables
        v
10.244.1.11
```

ASCII:

```text
Layer 1: Naming

order-service
    |
    v
CoreDNS
    |
    v
10.96.20.15


Layer 2: Traffic Routing

10.96.20.15:8080
    |
    v
kube-proxy rules
    |
    v
10.244.x.y:8080
```

Interview sentence:

```text
CoreDNS handles service name resolution. kube-proxy handles dataplane routing from Service virtual IPs to backend Pods.
```

If DNS works but Service routing is broken, CoreDNS may be innocent.

If DNS fails, kube-proxy may still be fine.

Debug layer by layer.

---

# 7. DNS Names In Kubernetes

Kubernetes creates DNS names for Services.

Full service name:

```text
service-name.namespace.svc.cluster.local
```

Example:

```text
order-service.default.svc.cluster.local
```

Parts:

```text
order-service  = Service name
default        = Namespace
svc            = Service scope
cluster.local  = Cluster domain
```

Diagram:

```text
order-service.default.svc.cluster.local
     |          |       |        |
     |          |       |        +-- cluster DNS domain
     |          |       +----------- service zone
     |          +------------------- namespace
     +------------------------------ service name
```

Short forms work depending on namespace and search domains.

From a Pod in `default` namespace:

```text
order-service
order-service.default
order-service.default.svc
order-service.default.svc.cluster.local
```

From another namespace, use namespace-qualified name:

```text
order-service.default
```

or full name:

```text
order-service.default.svc.cluster.local
```

Do not rely blindly on short names across namespaces.

---

# 8. Pod /etc/resolv.conf Mental Model

Inside a Pod:

```bash
cat /etc/resolv.conf
```

You may see something like:

```text
nameserver 10.96.0.10
search default.svc.cluster.local svc.cluster.local cluster.local
options ndots:5
```

Meaning:

```text
nameserver = kube-dns/CoreDNS Service IP
search     = suffixes automatically tried
ndots      = controls when search suffixes are attempted
```

Mental model:

```text
Application asks Linux resolver.
Linux resolver uses /etc/resolv.conf.
Resolver sends DNS query to CoreDNS.
```

ASCII:

```text
Java App
  |
  v
Linux Resolver
  |
  v
/etc/resolv.conf
  |
  v
nameserver 10.96.0.10
  |
  v
CoreDNS
```

Important:

```text
Your Java app does not directly talk to CoreDNS.
It asks the OS/JVM resolver.
```

This matters when debugging caching, timeouts, or `UnknownHostException`.

---

# 9. CoreDNS Kubernetes Plugin

CoreDNS is modular.

Its Kubernetes behavior comes from the `kubernetes` plugin.

A simplified Corefile may look like:

```text
.:53 {
    errors
    health
    ready
    kubernetes cluster.local in-addr.arpa ip6.arpa {
       pods insecure
       fallthrough in-addr.arpa ip6.arpa
       ttl 30
    }
    prometheus :9153
    forward . /etc/resolv.conf
    cache 30
    loop
    reload
    loadbalance
}
```

Mental model:

```text
Corefile = CoreDNS routing table and behavior config
kubernetes plugin = answer cluster-local service queries
forward plugin = send external DNS queries upstream
cache plugin = reduce repeated lookups
```

ASCII:

```text
DNS Query
   |
   v
CoreDNS
   |
   +-- Is it cluster.local?
   |       |
   |       v
   |   Kubernetes plugin
   |
   +-- Is it external?
           |
           v
       Forward plugin
```

Example:

```text
order-service.default.svc.cluster.local -> Kubernetes plugin
google.com                               -> forward upstream
```

---

# 10. Cluster-Internal vs External DNS

CoreDNS handles both internal and external names.

Internal:

```text
order-service.default.svc.cluster.local
```

External:

```text
postgres.company.internal
google.com
api.stripe.com
```

Flow:

```text
Internal Service Name
    |
    v
CoreDNS Kubernetes plugin
    |
    v
ClusterIP


External Name
    |
    v
CoreDNS forward plugin
    |
    v
Upstream DNS
```

ASCII:

```text
Payment Pod
   |
   +--> order-service.default.svc.cluster.local
   |        |
   |        v
   |     CoreDNS -> Kubernetes API record
   |
   +--> api.external.com
            |
            v
         CoreDNS -> upstream DNS
```

Production issue:

```text
Internal DNS works but external DNS fails
```

Possible cause:

```text
CoreDNS forward upstream broken
node DNS broken
network policy blocks egress
corporate DNS unreachable
```

Another issue:

```text
External DNS works but service DNS fails
```

Possible cause:

```text
CoreDNS Kubernetes plugin issue
wrong namespace
Service does not exist
CoreDNS cannot watch API
```

---

# 11. Service Types And DNS

DNS exists for Services regardless of Service type, but the answer and routing behavior depend on the type.

## ClusterIP

Most common internal service.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: order-service
spec:
  type: ClusterIP
  selector:
    app: order-service
  ports:
    - port: 8080
      targetPort: 8080
```

DNS:

```text
order-service.default.svc.cluster.local -> ClusterIP
```

## Headless Service

```yaml
spec:
  clusterIP: None
```

DNS returns Pod IPs directly, usually used by StatefulSets.

```text
mysql.default.svc.cluster.local -> 10.244.1.10, 10.244.2.11
```

## ExternalName

Maps service name to external DNS name.

```yaml
spec:
  type: ExternalName
  externalName: database.company.com
```

DNS returns CNAME-like behavior.

---

# 12. ClusterIP Service Discovery Dry Run

Service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: inventory-service
spec:
  selector:
    app: inventory
  ports:
    - port: 8080
      targetPort: 8080
```

Pods:

```text
inventory-a app=inventory
inventory-b app=inventory
```

Client:

```text
order-service calls inventory-service
```

Dry run:

```text
1. order-service calls http://inventory-service:8080.
2. Resolver expands using search domain.
3. CoreDNS receives query for inventory-service.default.svc.cluster.local.
4. CoreDNS returns ClusterIP.
5. TCP connection opens to ClusterIP:8080.
6. kube-proxy chooses backend endpoint.
7. Request reaches one ready inventory Pod.
```

ASCII:

```text
order Pod
   |
   | inventory-service:8080
   v
CoreDNS
   |
   | 10.96.55.21
   v
Service VIP
   |
   +--> inventory-a
   +--> inventory-b
```

Important:

```text
DNS answer is usually Service IP, not Pod IP.
```

For normal ClusterIP Services, CoreDNS does not load balance Pods directly.

kube-proxy handles backend selection.

---

# 13. Headless Service Mental Model

Headless Service means:

```text
No virtual ClusterIP.
Return backend Pod IPs directly.
```

YAML:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: postgres
spec:
  clusterIP: None
  selector:
    app: postgres
  ports:
    - port: 5432
      targetPort: 5432
```

DNS behavior:

```text
postgres.default.svc.cluster.local -> Pod IPs
```

ASCII:

```text
Client
  |
  | DNS query: postgres
  v
CoreDNS
  |
  | returns:
  | 10.244.1.10
  | 10.244.2.11
  | 10.244.3.12
  v
Client chooses connection target
```

Why useful?

```text
Stateful databases
Kafka brokers
ZooKeeper
Elasticsearch
Applications needing identity-aware peers
```

Normal Service hides individual Pods.

Headless Service exposes them.

Use headless when the client or protocol needs to know individual members.

---

# 14. StatefulSet DNS Pattern

StatefulSets usually combine:

```text
Stable Pod names
Stable network identities
Headless Service
```

Example:

```text
postgres-0.postgres.default.svc.cluster.local
postgres-1.postgres.default.svc.cluster.local
postgres-2.postgres.default.svc.cluster.local
```

ASCII:

```text
Headless Service: postgres

postgres-0 ---> 10.244.1.10
postgres-1 ---> 10.244.2.11
postgres-2 ---> 10.244.3.12
```

StatefulSet identity:

```text
Pod name stays stable.
PVC stays stable.
DNS name stays stable.
```

This is different from Deployment Pods.

Deployment Pods are cattle:

```text
order-7d9f9d8c-px2aa
order-7d9f9d8c-k9z2b
```

StatefulSet Pods are ordered identities:

```text
mysql-0
mysql-1
mysql-2
```

Mental model:

```text
Deployment + ClusterIP Service = stateless app discovery
StatefulSet + Headless Service = identity-aware discovery
```

---

# 15. Spring Boot Service-to-Service Example

Payment service calls order service.

Bad version:

```java
String url = "http://10.244.1.12:8080/api/orders/" + orderId;
```

Good Kubernetes version:

```java
String url = "http://order-service:8080/api/orders/" + orderId;
```

Using `WebClient`:

```java
package com.example.payment;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OrderClient {
    private final WebClient webClient;

    public OrderClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://order-service:8080")
                .build();
    }

    public String fetchOrder(String orderId) {
        return webClient.get()
                .uri("/api/orders/{id}", orderId)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
```

Kubernetes does the discovery.

Application code only knows logical service name.

ASCII:

```text
Payment Java Code
   |
   | http://order-service:8080
   v
CoreDNS
   |
   v
Service ClusterIP
   |
   v
Order Pod
```

This is simple and powerful.

But remember: DNS discovery is not retry, timeout, circuit breaker, or observability.

You still configure those in Java.

---

# 16. Spring Boot Configuration Example

Do not hardcode hostnames inside Java code.

Use configuration.

`application.yml`:

```yaml
clients:
  order-service:
    base-url: http://order-service:8080
```

Configuration properties:

```java
package com.example.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clients.order-service")
public record OrderServiceProperties(String baseUrl) {
}
```

Client:

```java
package com.example.payment;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OrderClient {
    private final WebClient webClient;

    public OrderClient(WebClient.Builder builder, OrderServiceProperties props) {
        this.webClient = builder.baseUrl(props.baseUrl()).build();
    }
}
```

Deployment environment can override:

```yaml
env:
  - name: CLIENTS_ORDER_SERVICE_BASE_URL
    value: "http://order-service:8080"
```

For another namespace:

```yaml
value: "http://order-service.orders.svc.cluster.local:8080"
```

Mental model:

```text
Code should depend on logical service contract.
Environment provides service address.
```

---

# 17. Namespace Discovery Example

Suppose:

```text
payment-service namespace: payments
order-service namespace: orders
```

From `payments`, this may fail:

```text
http://order-service:8080
```

Because resolver first searches:

```text
payments.svc.cluster.local
```

Correct:

```text
http://order-service.orders:8080
```

or:

```text
http://order-service.orders.svc.cluster.local:8080
```

ASCII:

```text
payments namespace

payment Pod
   |
   | order-service
   v
Looks for:
order-service.payments.svc.cluster.local
   |
   v
Not found


Correct:

payment Pod
   |
   | order-service.orders
   v
order-service.orders.svc.cluster.local
```

Production bug:

```text
Works in dev when both services are in same namespace.
Fails in staging when services are split across namespaces.
```

Fix:

```text
Use namespace-qualified service name.
```

---

# 18. CoreDNS Scaling Mental Model

CoreDNS is in the request path for name resolution, not for every packet after resolution.

If DNS cache works:

```text
Many application requests
Few DNS lookups
```

If DNS caching is poor:

```text
Many application requests
Many DNS lookups
CoreDNS load increases
```

ASCII:

```text
Healthy

App -> DNS once -> cache -> many HTTP calls


Unhealthy

App -> DNS
App -> DNS
App -> DNS
App -> DNS
CoreDNS overwhelmed
```

CoreDNS runs as multiple replicas for availability.

```text
CoreDNS Pod A
CoreDNS Pod B
```

Usually exposed by kube-dns Service:

```text
Pod DNS query -> kube-dns ClusterIP -> one CoreDNS Pod
```

Scaling considerations:

```text
number of Pods
DNS QPS
TTL
application resolver caching
NodeLocal DNSCache
CoreDNS CPU/memory
```

Production symptom of overloaded DNS:

```text
Random UnknownHostException
Temporary failure in name resolution
High CoreDNS CPU
DNS timeout spikes
```

---

# 19. JVM DNS Caching Gotcha

Java can cache DNS results.

This can be good or bad.

Good:

```text
Less DNS QPS
Faster repeated calls
```

Bad:

```text
Stale IPs if DNS result changes
```

For normal ClusterIP Services, the Service IP is stable, so Java DNS caching is usually less risky.

For headless Services, DNS returns Pod IPs directly, so stale DNS can matter.

Mental model:

```text
ClusterIP Service:
  DNS gives stable virtual IP.
  kube-proxy handles changing Pods.

Headless Service:
  DNS gives changing Pod IPs.
  client caching matters more.
```

In modern Java, DNS cache behavior depends on security properties and JVM configuration.

Operational advice:

```text
Use ClusterIP for normal stateless microservices.
Use headless only when needed.
Be careful with long DNS cache TTLs for headless services.
```

ASCII:

```text
ClusterIP path:

Java cache -> Service IP stable -> backend changes hidden


Headless path:

Java cache -> Pod IP stale -> connection may fail
```

---

# 20. NodeLocal DNSCache

In some clusters, NodeLocal DNSCache is enabled.

It runs a DNS cache on each node.

Without NodeLocal:

```text
Pod -> kube-dns Service -> CoreDNS Pod
```

With NodeLocal:

```text
Pod -> local node DNS cache -> CoreDNS if cache miss
```

ASCII:

```text
Without NodeLocal

Pod
 |
 v
kube-dns Service
 |
 v
CoreDNS Pod


With NodeLocal DNSCache

Pod
 |
 v
NodeLocal DNSCache on same node
 |
 | cache miss
 v
CoreDNS Pod
```

Benefits:

```text
Lower DNS latency
Lower CoreDNS load
Less cross-node DNS traffic
Better resilience during bursts
```

Debug note:

```text
If NodeLocal is enabled, /etc/resolv.conf may point to a local link-local IP instead of kube-dns ClusterIP.
```

Do not panic.

That local IP forwards or caches DNS.

---

# 21. Production Story: Service Exists But DNS Fails

Symptoms:

```text
curl http://order-service:8080
Could not resolve host: order-service
```

Check from inside client Pod:

```bash
kubectl exec -it payment-pod -- cat /etc/resolv.conf
kubectl exec -it payment-pod -- nslookup order-service
kubectl exec -it payment-pod -- nslookup order-service.default.svc.cluster.local
```

Check Service:

```bash
kubectl get svc order-service
```

Possible causes:

```text
Wrong namespace
Service name typo
CoreDNS down
Pod DNS policy changed
NetworkPolicy blocking DNS
CoreDNS cannot access API
```

Layered debug:

```text
1. Does Service exist?
2. Are you querying from correct namespace?
3. Does full FQDN resolve?
4. Is CoreDNS running?
5. Can Pod reach DNS server IP?
6. Are DNS egress rules allowed?
```

ASCII:

```text
Name fails
   |
   +-- Service missing?
   +-- Namespace wrong?
   +-- CoreDNS down?
   +-- NetworkPolicy blocks UDP/TCP 53?
   +-- Pod DNS config broken?
```

---

# 22. Production Story: DNS Works But Traffic Fails

Symptoms:

```text
nslookup order-service works
curl http://order-service:8080 fails
```

This means DNS may not be the issue.

DNS only got you to Service IP.

Now check routing and endpoints.

Commands:

```bash
kubectl get svc order-service
kubectl get endpoints order-service
kubectl get endpointslice -l kubernetes.io/service-name=order-service
kubectl describe svc order-service
kubectl get pods --show-labels
```

Possible causes:

```text
Service selector mismatch
No ready Pods
Wrong targetPort
App not listening on expected port
NetworkPolicy blocks traffic
kube-proxy/dataplane issue
```

ASCII:

```text
DNS OK
  |
  v
Service IP reached?
  |
  +-- no endpoints
  |      |
  |      v
  |   label/readiness issue
  |
  +-- endpoints exist
         |
         v
      check targetPort/app/network
```

Interview sentence:

```text
Successful DNS resolution does not prove Service routing works.
It only proves the name resolved.
```

---

# 23. Production Story: Wrong Namespace

Services:

```text
orders/order-service
payments/payment-service
```

Payment code:

```text
http://order-service:8080
```

Failure:

```text
UnknownHostException: order-service
```

Why?

From payments namespace, short name expands to:

```text
order-service.payments.svc.cluster.local
```

But actual service is:

```text
order-service.orders.svc.cluster.local
```

Fix:

```text
http://order-service.orders:8080
```

or full:

```text
http://order-service.orders.svc.cluster.local:8080
```

ASCII:

```text
Wrong search:

payment Pod in payments
   |
   | order-service
   v
order-service.payments.svc.cluster.local
   |
   v
NXDOMAIN


Correct:

payment Pod in payments
   |
   | order-service.orders
   v
order-service.orders.svc.cluster.local
   |
   v
ClusterIP
```

---

# 24. Production Story: NetworkPolicy Blocks DNS

A common mistake:

```text
Apply default deny egress NetworkPolicy
Forget to allow DNS
```

Symptoms:

```text
All external calls fail
Service names fail
UnknownHostException
DNS timeout
```

NetworkPolicy example allowing DNS to kube-system:

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-dns-egress
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

Mental model:

```text
DNS is network traffic.
If you block egress, DNS can be blocked too.
```

ASCII:

```text
App Pod
  |
  | UDP 53
  v
CoreDNS

Default deny egress:
App Pod X DNS
```

Always test DNS after applying NetworkPolicy.

---

# 25. Production Story: ndots Creates Extra Queries

Pod `/etc/resolv.conf` often contains:

```text
options ndots:5
```

Meaning names with fewer than 5 dots may be tried with search suffixes first.

For external names like:

```text
api.stripe.com
```

Resolver may try cluster search variants before the absolute external name.

Conceptual pattern:

```text
api.stripe.com.default.svc.cluster.local
api.stripe.com.svc.cluster.local
api.stripe.com.cluster.local
api.stripe.com
```

This can increase DNS queries.

ASCII:

```text
App asks: api.stripe.com

Resolver tries:
  1. api.stripe.com.default.svc.cluster.local
  2. api.stripe.com.svc.cluster.local
  3. api.stripe.com.cluster.local
  4. api.stripe.com
```

Optimization:

```text
Use trailing dot for absolute DNS in some low-level clients:
api.stripe.com.
```

But in normal Spring Boot apps, do not randomly change this without measuring.

Mindset:

```text
DNS performance issues can come from resolver behavior, not only CoreDNS.
```

---

# 26. Debugging Toolkit

Use a temporary debug Pod:

```bash
kubectl run dns-debug --rm -it --image=busybox:1.36 -- sh
```

Inside:

```bash
nslookup kubernetes.default
nslookup order-service
nslookup order-service.default.svc.cluster.local
wget -qO- http://order-service:8080/actuator/health
cat /etc/resolv.conf
```

Useful commands:

```bash
kubectl get pods -n kube-system -l k8s-app=kube-dns
kubectl logs -n kube-system -l k8s-app=kube-dns
kubectl describe configmap coredns -n kube-system
kubectl get svc -n kube-system kube-dns
kubectl get endpoints -n kube-system kube-dns
```

Check app-side errors:

```text
java.net.UnknownHostException
java.net.SocketTimeoutException
Connection refused
Connection reset
HTTP 503
```

Interpretation:

```text
UnknownHostException       -> DNS/name resolution
Connection timeout         -> routing/network/app not reachable
Connection refused         -> target reached but port not accepting
HTTP 503                   -> app/proxy/service-level failure
```

---

# 27. Layered Debugging Mindset

Never debug Kubernetes DNS randomly.

Use layers.

```text
1. Is the service name correct?
2. Is namespace correct?
3. Does full FQDN resolve?
4. Does short name resolve?
5. Is CoreDNS running?
6. Can client Pod reach DNS server?
7. Does Service have endpoints?
8. Are backend Pods Ready?
9. Is targetPort correct?
10. Is app listening?
11. Is NetworkPolicy blocking DNS or app traffic?
```

ASCII:

```text
App call fails
   |
   v
Name resolution?
   |
   +-- fail -> DNS path
   |
   +-- success
          |
          v
       Service routing?
          |
          +-- fail -> endpoints/kube-proxy/network
          |
          +-- success
                 |
                 v
              App behavior
```

Golden rule:

```text
DNS success means only name -> IP worked.
It does not guarantee request success.
```

---

# 28. Complete Example: Order And Payment

Order Deployment:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
        - name: order-service
          image: order-service:1.0.0
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
```

Order Service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: order-service
spec:
  selector:
    app: order-service
  ports:
    - port: 8080
      targetPort: 8080
```

Payment config:

```yaml
clients:
  order-service:
    base-url: http://order-service:8080
```

Flow:

```text
payment-service -> order-service DNS -> Service IP -> ready order Pod
```

ASCII:

```text
Payment Pod
   |
   | http://order-service:8080
   v
CoreDNS
   |
   | ClusterIP
   v
order-service Service
   |
   +--> order Pod A Ready
   +--> order Pod B Ready
```

If readiness fails, endpoint may be removed.

So DNS still resolves, but Service has no ready backend.

---

# 29. What CoreDNS Does Not Do

CoreDNS does not:

```text
Restart failed Pods
Choose backend Pod for ClusterIP Service traffic
Make unhealthy Pods healthy
Fix Service selector mistakes
Fix wrong targetPort
Perform application retries
Provide circuit breaking
Guarantee external DNS is reachable
Replace service mesh discovery features
```

CoreDNS does:

```text
Resolve Kubernetes Service names
Resolve some Pod names depending on config
Forward external DNS queries
Cache DNS responses
Expose metrics
Integrate with Kubernetes API through plugin
```

ASCII:

```text
CoreDNS boundary

Name -> IP       YES
IP -> Pod route  NO
Retry failed app NO
Health check     NO
```

This boundary prevents wrong debugging.

---

# 30. CoreDNS Metrics And Observability

CoreDNS can expose Prometheus metrics, commonly on port `9153`.

Important metrics conceptually:

```text
DNS request count
DNS response codes
DNS latency
Cache hits/misses
Forward plugin errors
Kubernetes plugin behavior
```

Symptoms to monitor:

```text
High NXDOMAIN
High SERVFAIL
High DNS latency
CoreDNS CPU throttling
CoreDNS restarts
CoreDNS memory pressure
```

ASCII:

```text
Apps
 |
 | DNS queries
 v
CoreDNS
 |
 +--> metrics :9153
 |
 v
Prometheus
 |
 v
Grafana / Alerts
```

Useful alert ideas:

```text
CoreDNS unavailable
DNS latency p99 high
SERVFAIL rate high
CoreDNS CPU throttled
CoreDNS pod restarting
```

For production, DNS is critical infrastructure.

A DNS outage can look like full microservice outage.

---

# 31. Real Production Failure: DNS Outage Looks Like App Bug

Incident:

```text
Multiple services started throwing UnknownHostException.
Developers checked application logs and restarted Pods.
Nothing improved.
```

Root cause:

```text
CoreDNS Pods were CPU throttled.
DNS queries timed out during traffic spike.
```

Symptoms:

```text
Random service calls failed
External API calls failed
Some requests succeeded due to cache
CoreDNS CPU near limit
Latency spikes
```

Fixes:

```text
Increase CoreDNS CPU limits/requests
Scale CoreDNS replicas
Enable NodeLocal DNSCache
Tune noisy clients
Check ndots/search behavior
Add DNS metrics alerts
```

Lesson:

```text
When many unrelated services fail name resolution together, suspect cluster DNS.
```

ASCII:

```text
Many apps fail DNS
      |
      v
Common shared dependency?
      |
      v
CoreDNS
```

---

# 32. Real Production Failure: Headless Service And Stale Client Cache

Scenario:

```text
A Java service connects to a headless database service.
DNS returns Pod IPs directly.
Database Pod is rescheduled.
Java client keeps stale Pod IP too long.
```

Symptoms:

```text
Connection timeout to old Pod IP
Some clients recover
Some clients stuck longer
DNS lookup manually shows new IP
Application still tries old IP
```

Root cause:

```text
Client-side DNS cache / connection pool retained stale address.
```

Fix:

```text
Review JVM DNS TTL
Review database driver behavior
Use proper StatefulSet DNS names
Ensure connection pool refreshes bad connections
Prefer ClusterIP if identity is not required
```

Mental model:

```text
Headless Service moves discovery responsibility closer to the client.
```

ASCII:

```text
ClusterIP:
client -> stable VIP -> changing Pods hidden

Headless:
client -> Pod IP list -> client must handle change
```

---

# 33. CoreDNS And Service Mesh

Service mesh does not remove DNS.

Even with Istio/Envoy, initial service name resolution still matters.

Typical flow:

```text
App calls http://order-service
   |
   v
DNS resolves service name
   |
   v
Traffic intercepted by sidecar
   |
   v
Envoy applies routing, mTLS, retries, telemetry
```

ASCII:

```text
App Container
   |
   | order-service
   v
DNS
   |
   v
Envoy Sidecar
   |
   v
Service / Endpoint / Mesh routing
```

Service mesh adds:

```text
mTLS
retry policies
traffic splitting
observability
circuit breaking
advanced routing
```

CoreDNS still provides base name resolution.

Do not think:

```text
Istio replaces DNS.
```

Better:

```text
Istio builds advanced traffic control on top of Kubernetes service identity.
```

---

# 34. Interview Questions

## What is CoreDNS?

CoreDNS is the DNS server used by Kubernetes to provide cluster DNS. It resolves Kubernetes Service names such as `order-service.default.svc.cluster.local` to Service IPs and can forward external DNS queries to upstream resolvers.

## What is service discovery in Kubernetes?

Service discovery is the mechanism by which applications find other services using stable names instead of Pod IPs. In Kubernetes, a Service gives a stable identity and CoreDNS resolves that identity to a reachable Service IP.

## Does CoreDNS send traffic to Pods?

No. CoreDNS resolves names to IPs. For a normal ClusterIP Service, it returns the Service IP. kube-proxy or the cluster dataplane routes traffic from the Service IP to backend Pods.

## Why should applications call Service names instead of Pod IPs?

Pod IPs are temporary and change when Pods restart or reschedule. Service names are stable and are backed by Kubernetes DNS and Service routing.

## What is the full DNS name of a Service?

The common full form is:

```text
service-name.namespace.svc.cluster.local
```

Example:

```text
order-service.default.svc.cluster.local
```

## What is the difference between ClusterIP and headless Service DNS?

For a normal ClusterIP Service, DNS returns the Service virtual IP. For a headless Service with `clusterIP: None`, DNS returns backend Pod IPs directly.

## When do we use headless Service?

Use headless Service when clients need to discover individual Pods, commonly with StatefulSets, databases, Kafka, ZooKeeper, Elasticsearch, and identity-aware protocols.

## Why can DNS resolve but HTTP still fail?

DNS only resolves name to IP. HTTP can still fail due to no endpoints, wrong labels, failing readiness probes, wrong targetPort, app not listening, NetworkPolicy, or dataplane problems.

## What is `/etc/resolv.conf` inside a Pod?

It tells the Pod resolver which DNS server to use and which search domains to try. It usually points to the kube-dns/CoreDNS Service IP or NodeLocal DNSCache.

## How do you debug DNS inside Kubernetes?

Start from inside a Pod using `nslookup`, check `/etc/resolv.conf`, verify the Service exists, test full FQDN, inspect CoreDNS Pods/logs, check kube-dns Service/endpoints, and verify NetworkPolicy allows DNS traffic on UDP/TCP 53.

---

# 35. Cheat Sheet

```text
CoreDNS                         = Kubernetes DNS server
Service Discovery               = finding services by stable names
Service name                    = stable logical identity
Pod IP                          = temporary runtime address
ClusterIP                       = stable virtual Service IP
Headless Service                = Service without ClusterIP
FQDN                            = service.namespace.svc.cluster.local
kube-dns Service                = stable Service fronting CoreDNS Pods
/etc/resolv.conf                = Pod DNS resolver config
search domains                  = suffixes resolver tries automatically
ndots                           = controls search behavior
Kubernetes plugin               = CoreDNS plugin for cluster service records
forward plugin                  = sends external DNS queries upstream
cache plugin                    = caches DNS responses
NodeLocal DNSCache              = node-level DNS cache
UnknownHostException            = DNS/name resolution failure
No endpoints                    = Service has no ready backend Pods
```

Core flow:

```text
App calls order-service
   |
   v
Linux/JVM resolver
   |
   v
/etc/resolv.conf
   |
   v
CoreDNS
   |
   v
Service ClusterIP
   |
   v
kube-proxy/dataplane
   |
   v
Ready Pod
```

Debug order:

```text
[ ] Service name correct?
[ ] Namespace correct?
[ ] Full FQDN resolves?
[ ] Short name resolves?
[ ] CoreDNS Pods running?
[ ] kube-dns Service has endpoints?
[ ] Pod can reach DNS server?
[ ] NetworkPolicy allows UDP/TCP 53?
[ ] Service has endpoints?
[ ] Pods are Ready?
[ ] targetPort correct?
[ ] App listening?
```

---

# 36. One Picture To Remember

```text
                         APPLICATION CODE
                               |
                               | http://order-service:8080
                               v
                        +--------------+
                        | OS / JVM DNS |
                        | Resolver     |
                        +------+-------+
                               |
                               | /etc/resolv.conf
                               v
                        +--------------+
                        | kube-dns SVC |
                        | CoreDNS      |
                        +------+-------+
                               |
                               | name -> ClusterIP
                               v
                        +--------------+
                        | Service VIP  |
                        | 10.96.x.y    |
                        +------+-------+
                               |
                               | kube-proxy / dataplane
                               v
                  +------------+-------------+
                  |                          |
                  v                          v
          +---------------+          +---------------+
          | Ready Pod A   |          | Ready Pod B   |
          | Spring Boot   |          | Spring Boot   |
          +---------------+          +---------------+

Rule:

CoreDNS answers "where is the service name?"
kube-proxy/dataplane answers "which Pod receives this packet?"
Readiness answers "is this Pod safe for traffic?"
```

---

# 37. Final Memory Hook

Do not memorize CoreDNS as a random kube-system component.

Remember:

```text
Pods are temporary.
Services are stable.
CoreDNS turns stable names into reachable Service IPs.
```

The most important production distinction:

```text
DNS problem:
  name does not resolve

Service routing problem:
  name resolves but traffic does not reach a healthy backend

Application problem:
  traffic reaches backend but app returns error
```

Final sentence:

```text
CoreDNS is Kubernetes' dynamic phone book: applications ask for service names, CoreDNS returns stable cluster addresses, and the Kubernetes dataplane carries the request to the current healthy Pods.
```
