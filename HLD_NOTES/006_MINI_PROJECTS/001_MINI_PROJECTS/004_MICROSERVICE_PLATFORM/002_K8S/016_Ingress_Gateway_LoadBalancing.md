# 016_Ingress_Gateway_LoadBalancing.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Ingress Exists

Before Ingress, most beginners think Kubernetes networking ends at Service.

They learn:

```text
Pod
Service
ClusterIP
NodePort
LoadBalancer
```

Then they ask:

```text
How does real internet traffic enter the cluster?
How do users reach order.mycompany.com?
How do we route /api/orders to order-service?
How do we terminate HTTPS?
How do we avoid creating one cloud load balancer per service?
```

A Service solves stable access **inside** the cluster.

Ingress solves controlled HTTP/S access **from outside** the cluster.

One picture:

```text
Internet User
     |
     | https://shop.example.com/api/orders
     v
Ingress / Gateway
     |
     v
Kubernetes Service
     |
     v
Ready Pods
```

Do not memorize:

```text
Ingress = external access
```

Understand the production problem:

```text
Many apps live inside the cluster.
The outside world needs one controlled front door.
That front door must understand HTTP host, path, TLS, routing, and sometimes load balancing.
```

Mental model:

```text
Service  = stable internal address for Pods
Ingress  = HTTP front door into Services
Gateway  = newer, more expressive front door API
```

---

# 2. The Wrong Way To Think About Ingress

Wrong mental model:

```text
Ingress directly sends traffic to Pods.
```

Usually wrong.

Better model:

```text
Ingress routes traffic to Services.
Services select ready Pods.
kube-proxy / dataplane forwards to Pod IPs.
```

Diagram:

```text
Wrong:

Client
  |
  v
Ingress
  |
  v
Pod


Correct:

Client
  |
  v
Load Balancer / NodePort
  |
  v
Ingress Controller Pod
  |
  v
Service
  |
  v
EndpointSlice
  |
  v
Ready Pod
```

Ingress is not a magical traffic object by itself.

An Ingress YAML is mostly a **routing rule**.

You also need an **Ingress Controller** that watches those rules and configures a real proxy like NGINX, Envoy, HAProxy, Traefik, or a cloud load balancer integration.

Important distinction:

```text
Ingress resource    = desired HTTP routing rules
Ingress controller  = running software that implements those rules
```

This is like Kubernetes desired-state thinking again.

You write:

```text
Host shop.example.com path /api/orders should go to order-service
```

The controller makes proxy config real.

---

# 3. Real World Analogy: Mall Entrance

Imagine a shopping mall.

Inside the mall:

```text
Shop A = order-service
Shop B = payment-service
Shop C = inventory-service
```

Customers do not enter through random employee doors.

They enter through the main entrance.

At the entrance, signs say:

```text
Electronics -> Floor 2
Food Court  -> Floor 3
Parking     -> Basement
```

Kubernetes mapping:

```text
Mall entrance       = Ingress / Gateway
Signs               = routing rules
Shops               = Services
Employees           = Pods
Mall directory      = DNS
Security desk       = TLS/auth/WAF/rate limiting
```

Diagram:

```text
Customer
   |
   v
+----------------------+
| Mall Main Entrance   |
| shop.com             |
+----------+-----------+
           |
           +--> /orders    -> Order Shop
           +--> /payments  -> Payment Shop
           +--> /inventory -> Inventory Shop
```

Do not memorize Ingress as YAML.

Remember:

```text
Ingress is the mall entrance for HTTP traffic.
Services are shop counters inside the mall.
Pods are workers behind each counter.
```

---

# 4. Core Picture: Internet To Spring Boot Pod

```text
Browser / Mobile App
        |
        | HTTPS request
        v
+-----------------------------+
| DNS                         |
| shop.example.com -> LB IP   |
+-------------+---------------+
              |
              v
+-----------------------------+
| Cloud Load Balancer         |
| public IP                   |
+-------------+---------------+
              |
              v
+-----------------------------+
| Ingress Controller          |
| NGINX / Envoy / Traefik     |
+-------------+---------------+
              |
              v
+-----------------------------+
| Kubernetes Service          |
| order-service ClusterIP     |
+-------------+---------------+
              |
              v
+-----------------------------+
| Ready Endpoints             |
| Pod A / Pod B / Pod C       |
+-------------+---------------+
              |
              v
+-----------------------------+
| Spring Boot Container       |
| embedded Tomcat port 8080   |
+-----------------------------+
```

This is the real production path.

Notice the layers:

```text
DNS layer
Cloud load balancer layer
Ingress controller layer
Service layer
Pod readiness layer
Application layer
```

Debugging ingress problems means walking this chain slowly.

Do not jump directly to app logs unless you know traffic reached the app.

---

# 5. Service vs Ingress vs Gateway

A clean mental model:

```text
Service:
  Stable internal networking to Pods.

Ingress:
  HTTP/S routing from outside to Services.

Gateway API:
  Newer, more expressive traffic entry model.
```

ASCII comparison:

```text
Inside cluster:

payment-service -> http://order-service
        |
        v
      Service


Outside cluster:

User -> https://shop.example.com/orders
        |
        v
      Ingress / Gateway
```

Table-like mental model:

```text
Object        Main Question Answered

Service       "How do Pods find other Pods reliably?"
Ingress       "How does HTTP traffic enter the cluster?"
Gateway       "How do platform teams expose shared gateways cleanly?"
```

Do not confuse them.

A Service can exist without Ingress.

An Ingress usually points to a Service.

A Gateway usually has Routes that point to Services.

---

# 6. Ingress Resource Mental Model

An Ingress resource is a routing rule.

Example:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: shop-ingress
spec:
  rules:
    - host: shop.example.com
      http:
        paths:
          - path: /api/orders
            pathType: Prefix
            backend:
              service:
                name: order-service
                port:
                  number: 80
```

Meaning:

```text
When HTTP Host = shop.example.com
and path starts with /api/orders
send request to Kubernetes Service order-service on port 80.
```

Picture:

```text
shop.example.com/api/orders/123
          |
          v
Ingress rule match:
  host = shop.example.com
  path = /api/orders
          |
          v
order-service:80
```

The Ingress object does not automatically create the proxy.

The controller watches Ingress objects and builds runtime config.

---

# 7. Ingress Controller Mental Model

Controller pattern again:

```text
while true:
    watch Ingress objects
    watch Services
    watch EndpointSlices
    build proxy configuration
    reload/update proxy
```

ASCII:

```text
+----------------------+
| Ingress YAML         |
| desired route rules  |
+----------+-----------+
           |
           v
+----------------------+
| API Server / etcd    |
+----------+-----------+
           |
           v
+----------------------+
| Ingress Controller   |
| watch + configure    |
+----------+-----------+
           |
           v
+----------------------+
| Real Proxy Runtime   |
| NGINX / Envoy        |
+----------------------+
```

This is why changing an Ingress can update traffic without restarting your application.

Production mental model:

```text
Ingress controller is just another workload in the cluster,
but it sits on the traffic entry path.
```

Usually it runs as:

```text
Deployment or DaemonSet
Service type LoadBalancer or NodePort
ConfigMap for tuning
Pods with high availability
```

If the controller is down, your apps may still run, but external HTTP access can break.

---

# 8. Full External Request Dry Run

Request:

```text
GET https://shop.example.com/api/orders/42
```

Dry run:

```text
1. Browser asks DNS:
   "What is the IP for shop.example.com?"

2. DNS returns public load balancer IP.

3. Browser opens TCP connection to load balancer on port 443.

4. TLS handshake starts.

5. Load balancer forwards traffic to Ingress Controller.

6. Ingress Controller sees HTTP Host:
   shop.example.com

7. Ingress Controller sees path:
   /api/orders/42

8. It matches Ingress rule:
   host shop.example.com + prefix /api/orders

9. Backend is:
   order-service:80

10. Kubernetes Service resolves to ready endpoints:
    order-service pod A, B, C

11. Traffic is sent to one ready Pod.

12. Spring Boot receives request on port 8080.

13. Controller method runs.

14. Response travels back through same chain.
```

Picture:

```text
User
 |
 | DNS
 v
LB IP
 |
 | TLS + HTTP
 v
Ingress Controller
 |
 | rule match
 v
Service
 |
 | endpoint selected
 v
Pod
 |
 v
Spring Boot
```

This dry run is more important than memorizing YAML fields.

---

# 9. Host-Based Routing

Host-based routing uses domain names.

Example:

```text
orders.example.com    -> order-service
payments.example.com  -> payment-service
inventory.example.com -> inventory-service
```

YAML:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: host-routing
spec:
  rules:
    - host: orders.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: order-service
                port:
                  number: 80

    - host: payments.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: payment-service
                port:
                  number: 80
```

ASCII:

```text
orders.example.com
        |
        v
order-service


payments.example.com
        |
        v
payment-service
```

Use host routing when each service deserves its own clean domain or subdomain.

Real product example:

```text
api.company.com
admin.company.com
payments.company.com
```

---

# 10. Path-Based Routing

Path-based routing uses URL path.

Example:

```text
shop.example.com/api/orders     -> order-service
shop.example.com/api/payments   -> payment-service
shop.example.com/api/inventory  -> inventory-service
```

YAML:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: path-routing
spec:
  rules:
    - host: shop.example.com
      http:
        paths:
          - path: /api/orders
            pathType: Prefix
            backend:
              service:
                name: order-service
                port:
                  number: 80

          - path: /api/payments
            pathType: Prefix
            backend:
              service:
                name: payment-service
                port:
                  number: 80
```

ASCII:

```text
shop.example.com
      |
      +-- /api/orders    -> order-service
      |
      +-- /api/payments  -> payment-service
      |
      +-- /api/inventory -> inventory-service
```

Path routing is common for backend APIs.

Be careful with path rewriting.

Some applications expect:

```text
/order/42
```

But Ingress sends:

```text
/api/orders/order/42
```

or rewrites it incorrectly.

This is a common production bug.

---

# 11. TLS Termination Mental Model

TLS termination means HTTPS encryption ends at a layer.

Common pattern:

```text
Client --HTTPS--> Ingress --HTTP--> Service --HTTP--> Pod
```

Diagram:

```text
Encrypted Traffic
Client ==================> Ingress Controller
                              |
                              | decrypted HTTP inside cluster
                              v
                         order-service
```

Ingress TLS example:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: shop-tls
spec:
  tls:
    - hosts:
        - shop.example.com
      secretName: shop-tls-secret
  rules:
    - host: shop.example.com
      http:
        paths:
          - path: /api/orders
            pathType: Prefix
            backend:
              service:
                name: order-service
                port:
                  number: 80
```

The Secret contains certificate and private key.

Mental model:

```text
DNS points to load balancer.
Certificate proves identity of domain.
Ingress controller uses certificate.
Traffic is routed after TLS handshake.
```

Production options:

```text
TLS at cloud LB
TLS at Ingress Controller
TLS all the way to Pod
mTLS inside service mesh
```

Do not blindly say "Ingress handles TLS" without knowing where TLS actually terminates in your setup.

---

# 12. Load Balancing: Where It Happens

Load balancing can happen at multiple layers.

```text
Layer 1: Cloud Load Balancer
         balances across nodes or ingress pods

Layer 2: Ingress Controller
         balances HTTP requests to service endpoints

Layer 3: Kubernetes Service / kube-proxy / eBPF dataplane
         balances to Pod IPs

Layer 4: Application client library
         retries, circuit breaker, connection pools
```

Picture:

```text
Client
  |
  v
Cloud LB
  |
  v
Ingress Controller Pods
  |
  v
Service
  |
  v
Pod A / Pod B / Pod C
```

Important interview answer:

```text
Ingress is not always the only load balancer.
It is often part of a chain of load balancers.
```

For HTTP traffic, the Ingress Controller may choose upstream endpoints.

For some setups, the controller sends traffic to Service ClusterIP and kube-proxy chooses Pod.

For other setups, the controller directly watches EndpointSlices and sends traffic to Pod IPs.

The exact datapath depends on controller implementation and configuration.

---

# 13. Session Affinity And Sticky Sessions

Stateless services are easiest.

```text
Any request can go to any Pod.
```

But some legacy apps need sticky sessions:

```text
User A request 1 -> Pod 1
User A request 2 -> Pod 1
```

ASCII:

```text
Without sticky:

User A -> Pod 1
User A -> Pod 3
User A -> Pod 2


With sticky:

User A -> Pod 1
User A -> Pod 1
User A -> Pod 1
```

Better product-company design:

```text
Do not store login/session state inside Pod memory.
Store session/token state in Redis, database, or JWT.
Keep Pods stateless.
```

Spring Boot warning:

```text
HttpSession in memory + multiple Pods = random login issues
```

Better:

```text
JWT stateless auth
Redis-backed Spring Session
External cache
```

Ingress sticky sessions can help, but they are not a replacement for good stateless architecture.

---

# 14. Spring Boot Example: Order Service

Simple controller:

```java
package com.example.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @GetMapping("/api/orders/{id}")
    public String getOrder(@PathVariable String id) {
        return "order=" + id;
    }

    @GetMapping("/internal/ping")
    public String ping() {
        return "pong";
    }
}
```

Spring Boot application:

```java
package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
```

application.yml:

```yaml
server:
  port: 8080

management:
  endpoint:
    health:
      probes:
        enabled: true
  endpoints:
    web:
      exposure:
        include: health,info
```

Important:

```text
Container port = 8080
Service port   = 80
Target port    = 8080
Ingress backend points to Service port 80
```

This port chain is one of the most common beginner confusions.

---

# 15. Deployment + Service + Ingress Full YAML

Deployment:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 3
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
          image: example/order-service:1.0.0
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

Service:

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
    - name: http
      port: 80
      targetPort: 8080
```

Ingress:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: order-ingress
spec:
  ingressClassName: nginx
  rules:
    - host: shop.example.com
      http:
        paths:
          - path: /api/orders
            pathType: Prefix
            backend:
              service:
                name: order-service
                port:
                  number: 80
```

Flow:

```text
shop.example.com/api/orders/42
      |
      v
Ingress rule
      |
      v
order-service:80
      |
      v
targetPort 8080
      |
      v
Spring Boot /api/orders/42
```

---

# 16. Port Mapping Mental Model

```text
containerPort:
  Documentation for container runtime and humans.
  App listens here.

targetPort:
  Service forwards to this port on Pod.

port:
  Service exposes this port inside cluster.

Ingress backend port:
  Service port, not container port.
```

ASCII:

```text
Ingress
  backend: order-service:80
              |
              v
Service port: 80
              |
              v
Service targetPort: 8080
              |
              v
Pod containerPort: 8080
              |
              v
Spring Boot Tomcat: 8080
```

Common bug:

```text
Ingress backend says 8080
Service exposes only 80
Result: route fails or backend not found
```

Debug:

```bash
kubectl describe ingress order-ingress
kubectl describe service order-service
kubectl get endpointslice -l kubernetes.io/service-name=order-service
```

Mental model:

```text
Ingress talks to Service port.
Service talks to Pod targetPort.
App listens on container port.
```

---

# 17. Readiness And Ingress Traffic

Ingress should not send traffic to unready Pods.

The readiness path:

```text
Spring Boot starts
   |
   v
Readiness probe checks /actuator/health/readiness
   |
   v
Pod Ready = true
   |
   v
EndpointSlice includes ready endpoint
   |
   v
Service can route
   |
   v
Ingress traffic reaches Pod
```

If readiness fails:

```text
Pod Running but not Ready
Service has no ready endpoint
Ingress may return 502 / 503
```

ASCII:

```text
Pod Running
  |
  +-- Ready = false  -> no traffic
  |
  +-- Ready = true   -> traffic allowed
```

Production story:

```text
A Java app starts Tomcat quickly but DB connection pool initializes slowly.
Without readiness, traffic hits it too early and users see errors.
With readiness, Kubernetes waits before exposing it.
```

Ingress correctness depends on readiness correctness.

---

# 18. Production Story: 502 Bad Gateway

Symptoms:

```text
User sees 502 Bad Gateway
Ingress controller logs show upstream failure
Pods appear Running
```

Possible causes:

```text
Service selector matches no Pods
Service targetPort wrong
App not listening on expected port
Readiness passing incorrectly
Pod accepts connection but closes it
Ingress path rewrite sends wrong path
```

Layer-by-layer debug:

```bash
kubectl get ingress
kubectl describe ingress order-ingress

kubectl get svc order-service
kubectl describe svc order-service

kubectl get endpoints order-service
kubectl get endpointslice -l kubernetes.io/service-name=order-service

kubectl get pods -l app=order-service -o wide
kubectl logs deploy/order-service
```

Inside cluster test:

```bash
kubectl run curl --image=curlimages/curl -it --rm -- sh

curl -v http://order-service/api/orders/42
curl -v http://order-service.default.svc.cluster.local/api/orders/42
```

Mental model:

```text
502 often means the proxy could not successfully talk to backend.
Do not blame DNS first.
Check backend reachability.
```

---

# 19. Production Story: 404 From Ingress

Symptoms:

```text
Ingress reachable
TLS works
But route returns 404
```

Possible causes:

```text
Host does not match
Path does not match
Wrong pathType
Request reaches default backend
App receives rewritten path it does not understand
Ingress controller class mismatch
```

Example:

```text
Ingress expects:
Host: shop.example.com

User calls:
Host: www.shop.example.com
```

Result:

```text
No matching rule
Default backend 404
```

Debug:

```bash
curl -H "Host: shop.example.com" http://<lb-ip>/api/orders/42
kubectl describe ingress order-ingress
```

Mindset:

```text
404 may come from Ingress or from application.
Find who returned it.
```

Check headers and logs:

```text
Ingress access logs say route not matched -> Ingress 404
Spring Boot logs show request received -> App 404
```

---

# 20. Production Story: TLS Certificate Problem

Symptoms:

```text
Browser warning
Certificate mismatch
Expired certificate
TLS handshake failure
```

Possible causes:

```text
DNS points to wrong load balancer
Ingress uses wrong Secret
Secret missing tls.crt or tls.key
Certificate does not include host
Certificate expired
cert-manager challenge failed
```

Debug:

```bash
kubectl describe ingress shop-ingress
kubectl get secret shop-tls-secret
kubectl describe certificate
kubectl describe challenge
```

Mental model:

```text
Domain, DNS, certificate, Ingress rule, and controller must agree.
```

Picture:

```text
shop.example.com
   |
   | DNS
   v
LB IP
   |
   | TLS cert must contain shop.example.com
   v
Ingress Controller
```

Certificate bugs often look like networking bugs, but they are identity bugs.

---

# 21. NGINX Ingress Mental Model

NGINX Ingress Controller is common.

Mental model:

```text
Kubernetes Ingress objects become NGINX routing config.
NGINX receives traffic.
NGINX proxies to backend Services/Pods.
```

Simplified:

```text
Ingress YAML
    |
    v
Controller watches API
    |
    v
Generated nginx.conf
    |
    v
NGINX worker processes
    |
    v
Backend upstreams
```

Common features:

```text
Host/path routing
TLS termination
Path rewrite
Request size limits
Timeouts
Rate limiting
Sticky sessions
Custom headers
```

Example annotations:

```yaml
metadata:
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/proxy-read-timeout: "60"
    nginx.ingress.kubernetes.io/proxy-send-timeout: "60"
```

Warning:

```text
Annotations are controller-specific.
An annotation that works in NGINX may not work in Traefik or Envoy.
```

Do not memorize annotations first.

Understand the traffic behavior they change.

---

# 22. Gateway API Mental Model

Ingress is simple but limited.

Gateway API separates platform ownership from application routing.

Main objects:

```text
GatewayClass  = type of gateway implementation
Gateway       = actual shared entry point
HTTPRoute     = app/team routing rule
```

Analogy:

```text
GatewayClass = type of airport
Gateway      = airport terminal
HTTPRoute    = airline route assignment
```

ASCII:

```text
Platform Team
    |
    v
GatewayClass + Gateway
    |
    v
Shared public entry point


Application Team
    |
    v
HTTPRoute
    |
    v
Service
```

Gateway API picture:

```text
Client
  |
  v
Gateway
  |
  +-- HTTPRoute /api/orders   -> order-service
  |
  +-- HTTPRoute /api/payments -> payment-service
```

Why it matters:

```text
Ingress often mixes infrastructure concerns and app routing in one object.
Gateway API gives clearer separation for larger organizations.
```

For product companies and platform engineering, Gateway API is increasingly important because it models ownership better.

---

# 23. Gateway API Example

Gateway:

```yaml
apiVersion: gateway.networking.k8s.io/v1
kind: Gateway
metadata:
  name: public-gateway
spec:
  gatewayClassName: nginx
  listeners:
    - name: http
      protocol: HTTP
      port: 80
      hostname: shop.example.com
```

HTTPRoute:

```yaml
apiVersion: gateway.networking.k8s.io/v1
kind: HTTPRoute
metadata:
  name: order-route
spec:
  parentRefs:
    - name: public-gateway
  hostnames:
    - shop.example.com
  rules:
    - matches:
        - path:
            type: PathPrefix
            value: /api/orders
      backendRefs:
        - name: order-service
          port: 80
```

Mental model:

```text
Gateway owns listener.
HTTPRoute owns route.
Service owns Pod selection.
```

Diagram:

```text
Gateway: shop.example.com:80
       |
       v
HTTPRoute: /api/orders
       |
       v
order-service:80
       |
       v
Pods
```

In interviews, say:

```text
Ingress is older and simpler.
Gateway API is more expressive and role-oriented.
```

---

# 24. API Gateway vs Kubernetes Ingress

This is a common confusion.

Kubernetes Ingress:

```text
Cluster entry routing layer.
Mainly L7 routing, TLS, host/path rules.
```

API Gateway:

```text
Product/API management layer.
Often handles auth, API keys, quotas, request transformation, versioning, developer portals, monetization, analytics.
```

Diagram:

```text
Client
  |
  v
API Gateway
  |
  v
Ingress / Gateway
  |
  v
Service
  |
  v
Pod
```

Sometimes they are combined.

Example:

```text
Kong, Ambassador/Emissary, APISIX, Envoy Gateway
```

But the responsibilities are different.

Do not answer:

```text
Ingress and API Gateway are same.
```

Better answer:

```text
Ingress is Kubernetes-native traffic entry routing.
API Gateway is a broader API management pattern.
A product stack may use one tool for both, but the mental responsibilities differ.
```

---

# 25. Real World Architecture: E-Commerce

```text
                         Internet
                            |
                            v
                    +----------------+
                    | DNS            |
                    +--------+-------+
                             |
                             v
                    +----------------+
                    | Cloud LB       |
                    +--------+-------+
                             |
                             v
                    +----------------+
                    | Ingress/Gateway|
                    +--------+-------+
                             |
        +--------------------+--------------------+
        |                    |                    |
        v                    v                    v
+---------------+    +---------------+    +---------------+
| order-service |    | payment-svc   |    | catalog-svc   |
+-------+-------+    +-------+-------+    +-------+-------+
        |                    |                    |
        v                    v                    v
   Order Pods           Payment Pods          Catalog Pods
```

Routing:

```text
shop.example.com/api/orders   -> order-service
shop.example.com/api/payments -> payment-service
shop.example.com/api/catalog  -> catalog-service
```

Production additions:

```text
TLS certificates
Rate limits
Request body size limits
Timeouts
Retries
Circuit breakers
Observability
WAF
mTLS
Canary routing
```

Ingress is the door.

It is not the whole security or reliability architecture.

---

# 26. Canary Routing Mental Model

Canary means send a small percentage of traffic to new version.

```text
95% traffic -> v1
 5% traffic -> v2
```

Picture:

```text
Client Requests
      |
      v
Ingress / Gateway
      |
      +---- 95% ---> order-service-v1
      |
      +----  5% ---> order-service-v2
```

Why:

```text
Test new version with small real traffic.
Watch error rate, latency, logs.
Increase traffic if healthy.
Rollback quickly if bad.
```

Ingress-based canary depends on controller support.

Service mesh or progressive delivery tools may do this better:

```text
Argo Rollouts
Flagger
Istio
Linkerd
NGINX canary annotations
Gateway API traffic splitting support depending on implementation
```

Mental model:

```text
Rolling update changes Pods behind one Service.
Canary routing intentionally splits traffic between versions.
```

---

# 27. Timeouts, Retries, And Slow Spring Boot APIs

Ingress defaults can hurt.

Example:

```text
Spring Boot endpoint takes 90 seconds.
Ingress proxy timeout is 60 seconds.
User sees 504 Gateway Timeout.
App may still finish later.
```

ASCII:

```text
Client waits
   |
   v
Ingress waits 60s
   |
   +-- timeout -> 504 to client
   |
   v
Spring Boot still processing
```

Bad fix:

```text
Set timeout to 600 seconds everywhere.
```

Better engineering:

```text
Make APIs fast
Use async processing for long jobs
Return job id
Use queue/Kafka
Use backpressure
Set sensible timeouts
```

For Java systems:

```text
Tomcat thread pool
DB connection pool
HTTP client timeout
Ingress proxy timeout
Load balancer idle timeout
```

All must align.

One slow layer can exhaust another layer.

---

# 28. Request Size Limits

Uploading files through Ingress can fail.

Symptoms:

```text
413 Request Entity Too Large
```

Cause:

```text
Ingress controller request body limit is smaller than upload size.
```

Example NGINX annotation:

```yaml
metadata:
  annotations:
    nginx.ingress.kubernetes.io/proxy-body-size: "20m"
```

But production design should ask:

```text
Should large files go through Spring Boot?
Should clients upload directly to S3/GCS using signed URLs?
Should backend only receive metadata?
```

Better design:

```text
Client -> Object Storage signed URL
Backend -> validates metadata
Async worker -> processes file
```

Ingress can allow larger bodies, but it may not be the best architecture.

Do not make the gateway a dumping ground for huge uploads unless needed.

---

# 29. WebSocket And Long-Lived Connections

Ingress can support WebSockets if controller and timeouts are configured.

Flow:

```text
Client
  |
  | WebSocket upgrade
  v
Ingress Controller
  |
  v
chat-service
  |
  v
Chat Pod
```

Challenges:

```text
Long-lived connections
Connection draining during deploy
Sticky routing sometimes needed
Idle timeout
Load balancer timeout
Pod termination grace period
```

Spring Boot WebSocket issue:

```text
Pod receives many active connections.
Rolling update kills Pod too fast.
Users disconnect.
```

Better rollout:

```text
preStop hook
terminationGracePeriodSeconds
readiness turns false before shutdown
connection draining
external pub/sub for messages
```

Ingress is not only request/response.

For WebSockets, lifecycle and draining matter.

---

# 30. Security Boundary Mental Model

Ingress is exposed to the internet.

So it must be treated as a high-risk layer.

Security responsibilities may include:

```text
TLS
Allowed hosts
Request size limits
Rate limits
IP allow/deny lists
WAF
Auth integration
Header sanitation
mTLS with upstreams
NetworkPolicy
```

But do not assume:

```text
Ingress makes internal services secure automatically.
```

You still need:

```text
Authentication
Authorization
Input validation
Secrets management
Network policies
Least privilege
Audit logs
```

Picture:

```text
Internet
  |
  v
Ingress  <- exposed layer
  |
  v
Service
  |
  v
Pod      <- still must validate user
```

Common bug:

```text
Internal admin endpoint accidentally exposed through broad path rule.
```

Example risky rule:

```text
path: /
backend: admin-service
```

Always review exposure.

---

# 31. NetworkPolicy With Ingress

Kubernetes Ingress object is not the same as NetworkPolicy ingress rule.

Words collide.

```text
Ingress resource:
  HTTP routing from outside.

NetworkPolicy ingress:
  which inbound network traffic is allowed to Pods.
```

ASCII:

```text
External HTTP Ingress
Client -> Ingress Controller -> Service -> Pod


NetworkPolicy ingress
Which Pods/IPs may connect to this Pod?
```

A secure model:

```text
Only ingress-controller namespace can talk to backend service Pods.
Other namespaces cannot directly call them.
```

Example idea:

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-from-ingress-controller
spec:
  podSelector:
    matchLabels:
      app: order-service
  ingress:
    - from:
        - namespaceSelector:
            matchLabels:
              name: ingress-nginx
      ports:
        - protocol: TCP
          port: 8080
```

This requires a CNI that enforces NetworkPolicy.

Do not confuse the two meanings of "ingress".

---

# 32. Observability: What To Measure

For Ingress/Gateway, measure:

```text
Request rate
Status codes
Latency p50/p95/p99
Upstream latency
TLS handshake errors
4xx vs 5xx
Backend connection failures
Retries
Timeouts
Request body size
Ingress controller CPU/memory
Open connections
```

Picture:

```text
Client
  |
  | edge latency
  v
Ingress
  |
  | upstream latency
  v
Service/Pod
  |
  | app latency
  v
Spring Boot
```

Logs to correlate:

```text
Ingress access log
Ingress error log
Spring Boot application log
Application metrics
Distributed trace
```

Useful trace headers:

```text
X-Request-ID
traceparent
X-B3-TraceId
```

Production debugging mindset:

```text
A 500 with app logs is different from a 502 with ingress upstream errors.
A 404 from ingress is different from a 404 from Spring Boot.
A 504 means timeout path must be traced.
```

---

# 33. Debugging Checklist: External Traffic Broken

Use this order:

```text
1. DNS
   Does domain resolve to expected IP?

2. Cloud Load Balancer
   Is public LB healthy?

3. Ingress Controller
   Are controller Pods running and ready?

4. Ingress Object
   Does host/path match the request?

5. IngressClass
   Is the correct controller watching this Ingress?

6. TLS
   Is the certificate correct and valid?

7. Service
   Does backend Service exist and expose correct port?

8. Endpoints
   Does Service have ready endpoints?

9. Pods
   Are Pods Ready?

10. Application
   Does app answer inside cluster?

11. Logs
   Who returned the error?
```

Commands:

```bash
nslookup shop.example.com
curl -v https://shop.example.com/api/orders/42

kubectl get ingress -A
kubectl describe ingress order-ingress

kubectl get ingressclass

kubectl get pods -n ingress-nginx
kubectl logs -n ingress-nginx deploy/ingress-nginx-controller

kubectl get svc order-service
kubectl get endpoints order-service
kubectl get pods -l app=order-service
kubectl logs deploy/order-service
```

Golden rule:

```text
Debug from outside to inside.
Then debug from inside to outside.
```

---

# 34. Inside-Cluster Curl Debug

Create temporary curl Pod:

```bash
kubectl run curl \
  --image=curlimages/curl \
  -it --rm -- sh
```

Test Service:

```bash
curl -v http://order-service/api/orders/42
```

Test full DNS name:

```bash
curl -v http://order-service.default.svc.cluster.local/api/orders/42
```

Test Pod directly:

```bash
curl -v http://10.1.2.3:8080/api/orders/42
```

Interpretation:

```text
Pod direct works, Service fails:
  Service selector or port issue.

Service works, Ingress fails:
  Ingress host/path/TLS/controller issue.

Ingress works, app returns 500:
  Application bug or dependency issue.
```

ASCII:

```text
Pod direct test
   |
   v
Service test
   |
   v
Ingress test
   |
   v
External DNS test
```

This removes guesswork.

---

# 35. Common Beginner Mistakes

```text
Mistake 1:
Thinking Ingress works without Ingress Controller.
Correct:
Ingress resource needs a controller implementation.

Mistake 2:
Pointing Ingress backend to containerPort.
Correct:
Ingress backend points to Service port.

Mistake 3:
Ignoring host header.
Correct:
Host rule must match request Host.

Mistake 4:
Using path rewrite without understanding app routes.
Correct:
Know what path Spring Boot receives.

Mistake 5:
Thinking TLS Secret alone is enough.
Correct:
DNS, host, cert, Secret, and controller must align.

Mistake 6:
Making every Service type LoadBalancer.
Correct:
Usually use one/few ingress entry points and internal ClusterIP Services.

Mistake 7:
Using sticky sessions to hide stateful app design.
Correct:
Prefer stateless Pods or external session storage.

Mistake 8:
Not checking endpoints.
Correct:
No ready endpoints means no healthy backend.
```

---

# 36. Interview Questions

## What is Kubernetes Ingress?

Ingress is a Kubernetes API object that defines HTTP/S routing rules from outside the cluster to Services inside the cluster. It usually requires an Ingress Controller such as NGINX, Envoy, Traefik, HAProxy, or a cloud controller to implement those rules.

## Is Ingress the same as an Ingress Controller?

No. Ingress is the desired-state resource. The Ingress Controller is the running software that watches Ingress objects and configures a proxy or load balancer to route traffic.

## Does Ingress send traffic directly to Pods?

Usually Ingress routes to Services, and Services route to ready Pods. Some controllers optimize by watching EndpointSlices and routing directly to Pod IPs, but the conceptual backend in the Ingress spec is a Service.

## Why do we need Ingress if we already have Service?

Service gives stable internal access to Pods. Ingress gives external HTTP/S entry with host-based routing, path-based routing, and TLS termination.

## What is host-based routing?

Host-based routing sends traffic based on domain name. For example, orders.example.com goes to order-service while payments.example.com goes to payment-service.

## What is path-based routing?

Path-based routing sends traffic based on URL path. For example, shop.example.com/api/orders goes to order-service while /api/payments goes to payment-service.

## What is TLS termination?

TLS termination is where encrypted HTTPS traffic is decrypted. It can happen at a cloud load balancer, at the Ingress Controller, or sometimes continue all the way to the Pod.

## What is Gateway API?

Gateway API is a newer Kubernetes networking API that separates infrastructure gateway ownership from application routing. It uses objects like GatewayClass, Gateway, and HTTPRoute.

## Ingress vs API Gateway?

Ingress is Kubernetes-native HTTP/S entry routing. API Gateway is a broader API management layer that may include auth, quotas, API keys, transformations, analytics, developer portals, and versioning.

## How do you debug 502 from Ingress?

Check the Ingress rule, Service, Service port, EndpointSlices, Pod readiness, app listening port, and ingress controller logs. A 502 usually means the proxy could not successfully talk to its backend.

---

# 37. Cheat Sheet

```text
Ingress Resource       = desired HTTP/S routing rules
Ingress Controller     = software implementing those rules
Gateway API            = newer role-oriented traffic API
GatewayClass           = gateway implementation type
Gateway                = shared listener / entry point
HTTPRoute              = route from host/path to backend
Service                = stable internal access to Pods
EndpointSlice          = ready backend Pod IPs and ports
TLS Termination        = where HTTPS gets decrypted
Host Routing           = route by domain name
Path Routing           = route by URL path
Sticky Session         = same client goes to same backend
Canary Routing         = split traffic across versions
502                    = proxy/backend communication failure
503                    = no healthy backend / unavailable
504                    = timeout
404                    = no route match or app route missing
```

Request chain:

```text
DNS
 |
 v
Cloud Load Balancer
 |
 v
Ingress Controller / Gateway
 |
 v
Service
 |
 v
EndpointSlice
 |
 v
Ready Pod
 |
 v
Spring Boot
```

Port chain:

```text
Ingress backend service port
        |
        v
Service port
        |
        v
Service targetPort
        |
        v
Pod containerPort
        |
        v
Application listen port
```

---

# 38. One Picture To Remember

```text
                           INTERNET
                              |
                              | https://shop.example.com/api/orders/42
                              v
                      +------------------+
                      | DNS              |
                      | shop -> LB IP    |
                      +---------+--------+
                                |
                                v
                      +------------------+
                      | Cloud LB         |
                      | public entry     |
                      +---------+--------+
                                |
                                v
                      +------------------+
                      | Ingress/Gateway  |
                      | host/path/TLS    |
                      +---------+--------+
                                |
                   rule match: /api/orders
                                |
                                v
                      +------------------+
                      | Service          |
                      | order-service:80 |
                      +---------+--------+
                                |
                         ready endpoints
                                |
              +-----------------+-----------------+
              |                 |                 |
              v                 v                 v
        +-----------+     +-----------+     +-----------+
        | Pod A     |     | Pod B     |     | Pod C     |
        | :8080     |     | :8080     |     | :8080     |
        +-----------+     +-----------+     +-----------+
              |                 |                 |
              v                 v                 v
        Spring Boot       Spring Boot       Spring Boot

Rule:

Ingress/Gateway is the front door.
Service is the internal counter.
EndpointSlice is the list of ready workers.
Pods are the workers.
Spring Boot is the business logic.
```

---

# 39. Final Production Checklist

```text
[ ] I installed an Ingress Controller or Gateway implementation.
[ ] I know which IngressClass or GatewayClass is used.
[ ] DNS points to the correct public load balancer.
[ ] TLS certificate matches the host.
[ ] Ingress host exactly matches the request Host header.
[ ] Path rules match expected API paths.
[ ] Path rewrite behavior is understood and tested.
[ ] Backend Service exists.
[ ] Ingress backend port matches Service port.
[ ] Service targetPort matches application listen port.
[ ] Service selector matches Pod labels.
[ ] EndpointSlices contain ready endpoints.
[ ] Readiness probes protect traffic.
[ ] Ingress controller logs and metrics are enabled.
[ ] p95/p99 latency, 4xx, 5xx, 502, 503, 504 are monitored.
[ ] Timeouts are aligned across LB, Ingress, app, DB, and HTTP clients.
[ ] Large uploads and WebSockets have explicit architecture decisions.
[ ] Admin/internal routes are not accidentally exposed.
[ ] NetworkPolicy is considered for backend services.
```

---

# 40. Final Memory Hook

Do not memorize Ingress as YAML.

Remember the production machine:

```text
User traffic needs a safe HTTP front door.
Ingress/Gateway is that front door.
Service finds the right internal counter.
EndpointSlice lists ready workers.
Pods run the real Java process.
```

Final sentence:

```text
Ingress is not "external access magic".
Ingress is Kubernetes desired-state HTTP routing made real by a controller.
Gateway API is the cleaner next-generation model for the same front-door problem at platform scale.
```
