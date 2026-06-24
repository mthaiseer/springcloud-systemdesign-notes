# 051_Ingress_Controller.md
# MiniURLShortener — Ingress Controller

> Core mental model: **Ingress is the routing rule; Ingress Controller is the real reverse proxy/load balancer that watches those rules and sends external HTTP/HTTPS traffic to internal Kubernetes Services.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Service vs Ingress](#4-service-vs-ingress)
- [5. Ingress vs Ingress Controller](#5-ingress-vs-ingress-controller)
- [6. External Traffic Flow](#6-external-traffic-flow)
- [7. Host-Based Routing](#7-host-based-routing)
- [8. Path-Based Routing](#8-path-based-routing)
- [9. TLS Termination](#9-tls-termination)
- [10. NGINX Ingress Mental Model](#10-nginx-ingress-mental-model)
- [11. MiniURLShortener Architecture](#11-miniurlshortener-architecture)
- [12. Required Backend Service](#12-required-backend-service)
- [13. Basic Ingress YAML](#13-basic-ingress-yaml)
- [14. Path Routing YAML](#14-path-routing-yaml)
- [15. Host Routing YAML](#15-host-routing-yaml)
- [16. TLS Ingress YAML](#16-tls-ingress-yaml)
- [17. IngressClass](#17-ingressclass)
- [18. Local Testing With Minikube](#18-local-testing-with-minikube)
- [19. DNS And /etc/hosts](#19-dns-and-etchosts)
- [20. Spring Cloud Gateway vs Ingress](#20-spring-cloud-gateway-vs-ingress)
- [21. Common NGINX Annotations](#21-common-nginx-annotations)
- [22. Timeouts And Body Size](#22-timeouts-and-body-size)
- [23. Rate Limiting At Ingress](#23-rate-limiting-at-ingress)
- [24. Redirect API Routing](#24-redirect-api-routing)
- [25. Step-by-Step Dry Runs](#25-step-by-step-dry-runs)
- [26. Internal Execution Walkthrough](#26-internal-execution-walkthrough)
- [27. Common kubectl Commands](#27-common-kubectl-commands)
- [28. Debugging Mindset](#28-debugging-mindset)
- [29. Production Failure Stories](#29-production-failure-stories)
- [30. Common Mistakes](#30-common-mistakes)
- [31. Interview-Ready Explanation](#31-interview-ready-explanation)
- [32. Senior Engineer Checklist](#32-senior-engineer-checklist)
- [33. One-Page Cheat Sheet](#33-one-page-cheat-sheet)
- [34. One Picture To Remember](#34-one-picture-to-remember)

---

## 1. Why This Exists

In the Kubernetes Service chapter, MiniURLShortener learned how to expose Pods inside the cluster.

Example internal Service:

```text
mini-url-shortener-service:8080
```

That works for traffic inside Kubernetes.

But real users do not usually call internal ClusterIP Services.

Users call public URLs:

```text
https://sho.rt/api/v1/urls
https://sho.rt/abc123
```

You could expose every backend with a `LoadBalancer` Service, but that becomes messy and expensive.

Bad design:

```text
mini-url-shortener-service -> public LoadBalancer
user-service               -> public LoadBalancer
admin-service              -> public LoadBalancer
analytics-service          -> public LoadBalancer
```

Problems:

```text
1. Too many public entry points.
2. TLS certificates become scattered.
3. Security rules become inconsistent.
4. Cloud load balancer cost increases.
5. Backend services can be bypassed directly.
6. Gateway/rate-limit/auth logic may be skipped.
```

Ingress gives one HTTP/HTTPS entry layer.

ASCII:

```text
Internet
   |
   v
+----------------------+         +-------------------------------+
| Ingress Controller   |  --->   | internal Kubernetes Services  |
| NGINX / Traefik / LB |         +-------------------------------+
+----------------------+
```

Production memory:

```text
Service gives stable internal access.
Ingress gives controlled external HTTP access.
```

---

## 2. The One Core Mental Model

Ingress has two parts.

```text
Ingress Resource:
    YAML routing rules.

Ingress Controller:
    Real running proxy/load balancer that implements those rules.
```

ASCII:

```text
+------------------------------+
| Ingress YAML                 |
| host: sho.rt                 |
| path: /api                   |
| backend: miniurl-service     |
+--------------+---------------+
               |
               | watched by
               v
+------------------------------+
| Ingress Controller           |
| NGINX / Traefik / HAProxy    |
| receives real HTTP traffic   |
+--------------+---------------+
               |
               v
+------------------------------+
| Kubernetes Service           |
+--------------+---------------+
               |
               v
+------------------------------+
| Ready Pods                   |
+------------------------------+
```

One-line memory:

```text
Ingress is the rule; Ingress Controller is the engine.
```

If you create Ingress YAML without an Ingress Controller:

```text
The rule exists, but nobody enforces it.
```

Analogy:

```text
Ingress YAML = road sign
Ingress Controller = traffic police + road system
```

---

## 3. Problem Statement

Create a production-shaped Ingress Controller model for MiniURLShortener.

We need to understand:

```text
1. Service vs Ingress.
2. Ingress vs Ingress Controller.
3. Host-based routing.
4. Path-based routing.
5. TLS termination.
6. IngressClass.
7. NGINX Ingress examples.
8. Local testing.
9. Spring Cloud Gateway relationship.
10. Debugging 404, 502, 503, TLS, and DNS issues.
```

For MiniURLShortener, Ingress should support:

```text
POST /api/v1/urls       -> create short URL
GET /{shortCode}        -> redirect
GET /actuator/health    -> maybe internal only, not public production
```

Out of scope:

```text
1. Full Gateway API deep dive.
2. Full cert-manager deep dive.
3. Service mesh ingress gateway.
4. Cloud-specific ALB/GCLB annotations.
5. WAF deep dive.
```

---

## 4. Service vs Ingress

Service and Ingress solve different layers of networking.

Service:

```text
Stable internal network endpoint for Pods.
Routes traffic to ready Pod endpoints.
```

Ingress:

```text
External HTTP/HTTPS routing rules into Services.
Routes by host/path.
```

Table:

```text
+----------------+---------------------------------------------+
| Object         | Main Job                                    |
+----------------+---------------------------------------------+
| Service        | Stable internal access to Pods              |
| Ingress        | HTTP/HTTPS routing rules to Services        |
| Controller     | Real proxy/load balancer enforcing Ingress  |
+----------------+---------------------------------------------+
```

ASCII:

```text
External User
     |
     v
Ingress Controller
     |
     v
Service
     |
     v
Pods
```

Important:

```text
Ingress normally routes to a Service, not directly to Pods.
```

Why?

```text
Pods are temporary.
Services provide stable backend identity.
```

---

## 5. Ingress vs Ingress Controller

Ingress Resource:

```yaml
kind: Ingress
```

It stores routing rules.

Ingress Controller examples:

```text
NGINX Ingress Controller
Traefik
HAProxy
Contour
AWS ALB Controller
GCE Ingress Controller
Azure Application Gateway Ingress Controller
```

ASCII:

```text
Kubernetes API
      |
      v
Ingress object stored
      |
      v
Controller watches Ingress objects
      |
      v
Controller configures proxy
      |
      v
Proxy receives traffic
```

If you apply Ingress but controller is missing:

```text
kubectl get ingress shows it.
But external traffic does not work.
```

Check controller:

```bash
kubectl get pods -n ingress-nginx
kubectl get ingressclass
```

Senior memory:

```text
Ingress is declarative config. Controller is the runtime implementation.
```

---

## 6. External Traffic Flow

Full request flow:

```text
User Browser
   |
   v
DNS
   |
   v
External IP / LoadBalancer
   |
   v
Ingress Controller
   |
   v
Kubernetes Service
   |
   v
Ready Pod
```

ASCII:

```text
https://sho.rt/api/v1/urls
          |
          v
+--------------------------+
| DNS                      |
| sho.rt -> ingress IP     |
+------------+-------------+
             |
             v
+--------------------------+
| Ingress Controller       |
| host/path routing        |
+------------+-------------+
             |
             v
+--------------------------+
| mini-url-shortener-svc   |
+------------+-------------+
             |
             v
+--------------------------+
| Spring Boot Pod          |
+--------------------------+
```

The Ingress Controller is usually exposed by:

```text
LoadBalancer Service
NodePort Service
hostNetwork in some setups
cloud load balancer integration
```

The Ingress resource itself is not the external IP.

---

## 7. Host-Based Routing

Host-based routing uses domain names.

Example:

```text
api.sho.rt     -> API service
admin.sho.rt   -> admin service
redirect.sho.rt -> redirect service
```

ASCII:

```text
Ingress Controller
      |
      +-- Host api.sho.rt      -> api-service
      |
      +-- Host admin.sho.rt    -> admin-service
      |
      +-- Host redirect.sho.rt -> redirect-service
```

YAML shape:

```yaml
rules:
  - host: api.sho.rt
    http:
      paths:
        - path: /
          pathType: Prefix
          backend:
            service:
              name: mini-url-shortener-service
              port:
                number: 8080
```

Use host routing when:

```text
1. Different subdomains map to different apps.
2. You want clean separation between public API/admin/app.
3. TLS certificate can cover those hosts.
```

---

## 8. Path-Based Routing

Path-based routing uses URL paths.

Example:

```text
sho.rt/api/*     -> URL API
sho.rt/admin/*   -> admin service
sho.rt/u/*       -> user service
sho.rt/{code}    -> redirect handler
```

ASCII:

```text
Host: sho.rt
      |
      +-- /api      -> mini-url-shortener-service
      +-- /admin    -> admin-service
      +-- /u        -> user-service
      +-- /abc123   -> redirect handler
```

YAML shape:

```yaml
paths:
  - path: /api
    pathType: Prefix
    backend:
      service:
        name: mini-url-shortener-service
        port:
          number: 8080
```

`pathType: Prefix` matches:

```text
/api
/api/
/api/v1/urls
```

URL shortener warning:

```text
Root redirect path /{shortCode} can conflict with /api, /admin, /actuator.
```

So reserve system paths:

```text
/api
/admin
/actuator
/health
/metrics
```

---

## 9. TLS Termination

TLS termination means HTTPS is decrypted at the Ingress Controller.

Flow:

```text
Browser --HTTPS--> Ingress Controller --HTTP--> Service --HTTP--> Pod
```

ASCII:

```text
Client
  |
  | HTTPS
  v
+--------------------+
| Ingress Controller |
| TLS certificate    |
| decrypts request   |
+---------+----------+
          |
          | HTTP inside cluster
          v
+--------------------+
| Service -> Pod     |
+--------------------+
```

Ingress TLS config:

```yaml
tls:
  - hosts:
      - miniurl.local
    secretName: miniurl-tls-secret
```

TLS Secret contains:

```text
tls.crt
tls.key
```

Create manually for local testing:

```bash
kubectl create secret tls miniurl-tls-secret \
  --cert=tls.crt \
  --key=tls.key \
  -n miniurl
```

Production usually uses:

```text
cert-manager
Let's Encrypt
cloud certificate manager
```

---

## 10. NGINX Ingress Mental Model

NGINX Ingress Controller works like this:

```text
1. NGINX controller Pod runs in cluster.
2. It watches Ingress resources.
3. It reads Services, Endpoints, and Secrets.
4. It generates NGINX configuration.
5. It receives external requests.
6. It proxies requests to backend Services/Pods.
```

ASCII:

```text
Kubernetes API
      |
      v
NGINX Ingress Controller watches
      |
      v
Generate NGINX config
      |
      v
NGINX reverse proxy
      |
      v
Backend Service
```

Equivalent mental NGINX config:

```nginx
server {
    server_name miniurl.local;

    location /api {
        proxy_pass http://mini-url-shortener-service;
    }
}
```

Kubernetes generates this behavior dynamically from YAML.

---

## 11. MiniURLShortener Architecture

Simple learning architecture:

```text
Internet -> Ingress Controller -> mini-url-shortener-service -> Pods
```

ASCII:

```text
User
 |
 v
Ingress Controller
 |
 v
mini-url-shortener-service
 |
 +-- Pod 1
 +-- Pod 2
 +-- Pod 3
```

Production microservice architecture:

```text
Internet -> Ingress Controller -> Spring Cloud Gateway -> backend services
```

ASCII:

```text
Internet
   |
   v
+----------------------+
| Ingress Controller   |
| TLS + host/path      |
+----------+-----------+
           |
           v
+----------------------+
| Spring Cloud Gateway |
| auth/rate/filter     |
+----------+-----------+
           |
           +--> mini-url-shortener-service
           +--> user-service
           +--> analytics-service
```

Recommended evolution:

```text
Step 1: Ingress -> MiniURLShortener directly.
Step 2: Ingress -> Gateway -> MiniURLShortener.
```

---

## 12. Required Backend Service

Ingress routes to a Kubernetes Service.

Service example:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: mini-url-shortener-service
  namespace: miniurl
spec:
  type: ClusterIP
  selector:
    app: mini-url-shortener
  ports:
    - name: http
      port: 8080
      targetPort: 8080
```

Check:

```bash
kubectl get svc -n miniurl
kubectl get endpoints mini-url-shortener-service -n miniurl
```

If endpoints are empty:

```text
Ingress cannot send traffic successfully.
```

Common causes:

```text
selector mismatch
Pods not ready
wrong namespace
Deployment not running
```

---

## 13. Basic Ingress YAML

Create:

```text
k8s/miniurl-ingress-basic.yml
```

YAML:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: mini-url-shortener-ingress
  namespace: miniurl
spec:
  ingressClassName: nginx
  rules:
    - host: miniurl.local
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: mini-url-shortener-service
                port:
                  number: 8080
```

Meaning:

```text
Host miniurl.local
Any path /
Route to mini-url-shortener-service:8080
```

Apply:

```bash
kubectl apply -f k8s/miniurl-ingress-basic.yml
```

Check:

```bash
kubectl get ingress -n miniurl
kubectl describe ingress mini-url-shortener-ingress -n miniurl
```

---

## 14. Path Routing YAML

Path routing example:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: miniurl-path-ingress
  namespace: miniurl
spec:
  ingressClassName: nginx
  rules:
    - host: miniurl.local
      http:
        paths:
          - path: /api
            pathType: Prefix
            backend:
              service:
                name: mini-url-shortener-service
                port:
                  number: 8080
          - path: /
            pathType: Prefix
            backend:
              service:
                name: mini-url-shortener-service
                port:
                  number: 8080
```

Flow:

```text
/api/v1/urls -> create API
/abc123      -> redirect API
```

ASCII:

```text
miniurl.local/api/v1/urls
        |
        v
/api rule -> service

miniurl.local/abc123
        |
        v
/ rule -> service
```

Warning:

```text
/ catches everything.
Protect reserved paths intentionally.
```

---

## 15. Host Routing YAML

Host routing example:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: miniurl-host-ingress
  namespace: miniurl
spec:
  ingressClassName: nginx
  rules:
    - host: api.miniurl.local
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: mini-url-shortener-service
                port:
                  number: 8080
    - host: redirect.miniurl.local
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: mini-url-shortener-service
                port:
                  number: 8080
```

ASCII:

```text
api.miniurl.local/*
      |
      v
API route

redirect.miniurl.local/*
      |
      v
Redirect route
```

Host routing is cleaner when service boundaries are separate.

---

## 16. TLS Ingress YAML

TLS Ingress:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: miniurl-tls-ingress
  namespace: miniurl
spec:
  ingressClassName: nginx
  tls:
    - hosts:
        - miniurl.local
      secretName: miniurl-tls-secret
  rules:
    - host: miniurl.local
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: mini-url-shortener-service
                port:
                  number: 8080
```

Generate local self-signed cert:

```bash
openssl req -x509 -nodes -days 365 \
  -newkey rsa:2048 \
  -keyout tls.key \
  -out tls.crt \
  -subj "/CN=miniurl.local/O=miniurl"
```

Create Secret:

```bash
kubectl create secret tls miniurl-tls-secret \
  --cert=tls.crt \
  --key=tls.key \
  -n miniurl
```

Test:

```bash
curl -k https://miniurl.local/actuator/health
```

`-k` is only for local self-signed testing.

---

## 17. IngressClass

IngressClass tells which controller handles the Ingress.

Example:

```yaml
spec:
  ingressClassName: nginx
```

A cluster may have:

```text
nginx
traefik
alb
internal-nginx
```

ASCII:

```text
Ingress ingressClassName=nginx
        |
        v
NGINX Controller handles it

Ingress ingressClassName=alb
        |
        v
AWS ALB Controller handles it
```

Check:

```bash
kubectl get ingressclass
```

If wrong:

```text
Controller may ignore the Ingress.
```

---

## 18. Local Testing With Minikube

Enable NGINX Ingress:

```bash
minikube addons enable ingress
```

Check:

```bash
kubectl get pods -n ingress-nginx
```

Get Minikube IP:

```bash
minikube ip
```

Example:

```text
192.168.49.2
```

Add hosts entry:

```text
192.168.49.2 miniurl.local
```

Apply Ingress:

```bash
kubectl apply -f k8s/miniurl-ingress-basic.yml
```

Test:

```bash
curl http://miniurl.local/actuator/health
```

ASCII:

```text
Laptop curl miniurl.local
       |
       v
/etc/hosts -> Minikube IP
       |
       v
Ingress Controller
       |
       v
Service -> Pod
```

---

## 19. DNS And /etc/hosts

For local testing, you can fake DNS with `/etc/hosts`.

Linux/Mac:

```text
/etc/hosts
```

Windows:

```text
C:\Windows\System32\drivers\etc\hosts
```

Example:

```text
192.168.49.2 miniurl.local
```

Flow:

```text
curl miniurl.local
    |
    v
OS checks hosts file
    |
    v
Ingress IP
```

Production uses real DNS:

```text
Route53
Cloud DNS
Azure DNS
Cloudflare
company DNS
```

DNS points domain to the load balancer created for the Ingress Controller.

---

## 20. Spring Cloud Gateway vs Ingress

Ingress and Spring Cloud Gateway are different.

Ingress:

```text
Cluster edge HTTP routing.
TLS termination.
Host/path routing.
Cloud load balancer integration.
```

Spring Cloud Gateway:

```text
Application-level gateway.
Authentication filters.
Authorization.
Rate limiting by user/API key.
Request/response transformation.
Service discovery and resilience integration.
```

Recommended production flow:

```text
Internet
  |
  v
Ingress Controller
  |
  v
Spring Cloud Gateway Service
  |
  +--> mini-url-shortener-service
  +--> user-service
  +--> analytics-service
```

Mental split:

```text
Ingress gets traffic into the cluster.
Gateway applies API platform logic.
```

---

## 21. Common NGINX Annotations

NGINX Ingress supports annotations.

Example:

```yaml
metadata:
  annotations:
    nginx.ingress.kubernetes.io/proxy-read-timeout: "30"
    nginx.ingress.kubernetes.io/proxy-send-timeout: "30"
    nginx.ingress.kubernetes.io/proxy-body-size: "1m"
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
```

Meaning:

```text
proxy-read-timeout:
    how long NGINX waits for backend response.

proxy-send-timeout:
    how long NGINX waits while sending to backend.

proxy-body-size:
    maximum request body size.

ssl-redirect:
    redirect HTTP to HTTPS.
```

Warning:

```text
Annotations are controller-specific.
NGINX annotations may not work on Traefik or ALB.
```

Rule:

```text
Know your Ingress Controller before copying annotations.
```

---

## 22. Timeouts And Body Size

Ingress can reject requests before they reach Spring Boot.

Example:

```text
Request body too large -> 413 Request Entity Too Large
Backend too slow       -> 504 Gateway Timeout
Backend unavailable    -> 502/503
```

For URL shortener create API:

```text
Request body should be small.
1MB is enough.
```

Good annotation:

```yaml
nginx.ingress.kubernetes.io/proxy-body-size: "1m"
```

Timeout example:

```yaml
nginx.ingress.kubernetes.io/proxy-read-timeout: "10"
nginx.ingress.kubernetes.io/proxy-send-timeout: "10"
```

Align timeouts across layers:

```text
Client timeout
Ingress timeout
Gateway timeout
Resilience4j timeout
Spring Boot server timeout
Database timeout
```

ASCII:

```text
Client -> Ingress -> Gateway -> Service -> Pod -> DB
  T1        T2          T3        T4       T5
```

If outer timeout is much longer than inner timeout, users may wait unnecessarily.

---

## 23. Rate Limiting At Ingress

Ingress can rate limit before traffic reaches app.

NGINX example:

```yaml
metadata:
  annotations:
    nginx.ingress.kubernetes.io/limit-rps: "10"
    nginx.ingress.kubernetes.io/limit-burst-multiplier: "5"
```

Use for:

```text
basic IP-level abuse protection
coarse traffic throttling
protecting backend from obvious floods
```

But app-level limits are still needed.

Why?

```text
Ingress usually sees IP-level identity.
Business limits need user ID/API key/tenant.
```

Layered design:

```text
Ingress:
    coarse IP limit

Gateway:
    API key / user limit

Application:
    business-specific quota
```

ASCII:

```text
Internet
  |
  v
Ingress IP rate limit
  |
  v
Gateway user/API-key rate limit
  |
  v
Application business validation
```

---

## 24. Redirect API Routing

URL shortener routing is tricky because short codes often live at root.

Examples:

```text
POST /api/v1/urls
GET /abc123
```

If Ingress routes `/` to your service, it catches everything:

```text
/api/v1/urls
/abc123
/actuator
/admin
/favicon.ico
```

So design reserved paths carefully.

Recommended:

```text
/api/**      -> API handlers
/{code}      -> redirect handler
/actuator/** -> not public in production
/admin/**    -> separate protected admin route
```

ASCII:

```text
miniurl.local/api/v1/urls
        |
        v
Create API

miniurl.local/abc123
        |
        v
Redirect API

miniurl.local/actuator
        |
        v
Should be blocked/protected
```

Production rule:

```text
Do not expose sensitive actuator endpoints publicly.
```

---

## 25. Step-by-Step Dry Runs

### Dry Run 1: Basic HTTP Request

Request:

```text
GET http://miniurl.local/api/v1/urls
```

Flow:

```text
1. DNS resolves miniurl.local to Ingress Controller IP.
2. Request reaches Ingress Controller.
3. Controller checks host rule.
4. Host miniurl.local matches.
5. Path /api matches.
6. Request proxies to mini-url-shortener-service:8080.
7. Service routes to ready Pod.
8. Spring Boot handles request.
```

ASCII:

```text
Browser
  |
  v
DNS
  |
  v
Ingress Controller
  |
  v
Service
  |
  v
Pod
```

### Dry Run 2: Wrong Host

Ingress expects:

```text
miniurl.local
```

Request host:

```text
wrong.local
```

Flow:

```text
1. Request reaches controller.
2. No matching host rule.
3. Controller returns default 404.
```

Fix:

```text
Use correct DNS or Host header.
```

Test:

```bash
curl -H "Host: miniurl.local" http://<ingress-ip>/actuator/health
```

### Dry Run 3: Service Has No Endpoints

Ingress rule is correct.

But Service endpoints:

```text
<none>
```

Flow:

```text
1. Ingress matches request.
2. Controller tries backend Service.
3. No ready endpoints exist.
4. Request fails, often 503.
```

Fix:

```bash
kubectl get endpoints -n miniurl
kubectl get pods -n miniurl --show-labels
kubectl describe svc mini-url-shortener-service -n miniurl
```

### Dry Run 4: TLS Request

Request:

```text
https://miniurl.local/api/v1/urls
```

Flow:

```text
1. Browser connects with TLS.
2. Ingress Controller presents certificate from TLS Secret.
3. TLS handshake succeeds.
4. Controller decrypts request.
5. Controller proxies to Service.
6. Service routes to Pod.
```

ASCII:

```text
Browser --HTTPS--> Ingress --HTTP--> Service --HTTP--> Pod
```

### Dry Run 5: New Pod During Rollout

New Pod starts but readiness is false.

Flow:

```text
1. Service does not include new Pod endpoint.
2. Ingress continues routing to old ready Pods.
3. New Pod becomes ready.
4. Service endpoint list updates.
5. Ingress can route to new Pod.
```

This protects rolling update traffic.

---

## 26. Internal Execution Walkthrough

When you apply Ingress:

```text
1. kubectl sends Ingress YAML to API Server.
2. API Server stores Ingress object in etcd.
3. Ingress Controller watches API Server.
4. Controller sees new/changed Ingress.
5. Controller checks ingressClassName.
6. Controller reads backend Services.
7. Controller reads EndpointSlices.
8. Controller reads TLS Secrets if configured.
9. Controller generates proxy config.
10. Controller reloads/applies config.
11. External request arrives.
12. Proxy matches host/path.
13. Proxy forwards to backend Service endpoints.
```

ASCII:

```text
kubectl apply ingress
        |
        v
API Server
        |
        v
Ingress object stored
        |
        v
Controller watches
        |
        v
Proxy config generated
        |
        v
Traffic routed
```

Control plane:

```text
Ingress object, Services, EndpointSlices, Secrets.
```

Data plane:

```text
Ingress Controller proxy handling real packets.
```

---

## 27. Common kubectl Commands

List Ingress:

```bash
kubectl get ingress -n miniurl
```

Describe Ingress:

```bash
kubectl describe ingress mini-url-shortener-ingress -n miniurl
```

Check IngressClass:

```bash
kubectl get ingressclass
```

Check controller Pods:

```bash
kubectl get pods -n ingress-nginx
```

Controller logs:

```bash
kubectl logs -n ingress-nginx deployment/ingress-nginx-controller
```

Check backend Service:

```bash
kubectl get svc -n miniurl
```

Check endpoints:

```bash
kubectl get endpoints -n miniurl
```

Test Host header:

```bash
curl -H "Host: miniurl.local" http://<ingress-ip>/actuator/health
```

Check TLS Secret:

```bash
kubectl get secret miniurl-tls-secret -n miniurl
```

---

## 28. Debugging Mindset

When Ingress fails, ask:

```text
Did request reach the Ingress Controller?
Is DNS pointing to controller IP?
Is Ingress Controller installed and running?
Is ingressClassName correct?
Does host rule match request Host header?
Does path rule match request path?
Does backend Service exist?
Does backend Service have endpoints?
Are Pods ready?
Is targetPort correct?
Is TLS Secret present and valid?
Are annotations valid for this controller?
```

Debug map:

```text
404:
    host/path mismatch
    wrong ingress class
    default backend response

502:
    backend connection problem
    wrong targetPort
    Pod not accepting connection

503:
    Service has no ready endpoints
    selector mismatch
    readiness failing

TLS error:
    missing TLS Secret
    wrong certificate CN/SAN
    self-signed cert not trusted

Timeout:
    DNS wrong
    LoadBalancer not ready
    firewall/security group
    controller not exposed
```

Golden question:

```text
Did the request reach controller, match rule, find Service, and find ready endpoints?
```

---

## 29. Production Failure Stories

### Failure Story 1: Ingress Exists But No Controller

Team applies Ingress YAML.

```bash
kubectl get ingress
```

shows object.

But traffic does not work.

Root cause:

```text
No Ingress Controller installed.
```

Fix:

```text
Install NGINX/Traefik/cloud Ingress Controller.
```

Lesson:

```text
Ingress rules need a controller to become real traffic behavior.
```

### Failure Story 2: Wrong IngressClass

Ingress:

```yaml
ingressClassName: nginx
```

Cluster only has:

```text
alb
```

Result:

```text
Ingress ignored.
```

Fix:

```text
Use correct ingressClassName.
```

Lesson:

```text
IngressClass connects Ingress to the right controller.
```

### Failure Story 3: Backend Service Has No Endpoints

Ingress routes to Service.

Service selector is wrong.

Endpoints are empty.

Result:

```text
503 Service Unavailable
```

Fix:

```text
Fix Service selector and Pod labels.
```

Lesson:

```text
Ingress depends on Service endpoints.
```

### Failure Story 4: Actuator Exposed Publicly

Ingress routes `/` to app.

Public users can access:

```text
/actuator/env
/actuator/metrics
```

Fix:

```text
Limit actuator exposure.
Protect management endpoints.
Do not route sensitive paths publicly.
```

Lesson:

```text
Root path catches everything.
```

### Failure Story 5: Body Size Too Small

Client sends request larger than Ingress limit.

Result:

```text
413 Request Entity Too Large
```

Fix:

```text
Set correct proxy-body-size.
Keep URL shortener payloads small.
```

Lesson:

```text
Ingress can reject requests before Spring Boot sees them.
```

---

## 30. Common Mistakes

### Mistake 1: Thinking Ingress Works Without Controller

Wrong:

```text
Apply Ingress YAML and expect traffic to work.
```

Correct:

```text
Install and expose an Ingress Controller.
```

### Mistake 2: Confusing Service And Ingress

Wrong:

```text
Ingress replaces Service.
```

Correct:

```text
Ingress routes to Service. Service routes to Pods.
```

### Mistake 3: Wrong Host Header

Wrong:

```text
curl IP directly without Host header while Ingress expects miniurl.local.
```

Correct:

```bash
curl -H "Host: miniurl.local" http://<ingress-ip>/...
```

### Mistake 4: Debugging Ingress Before Service

Wrong:

```text
Ingress broken, but Service has no endpoints.
```

Correct:

```text
Check Service endpoints early.
```

### Mistake 5: Public Backend Services

Wrong:

```text
Every microservice gets public Ingress.
```

Correct:

```text
Expose Gateway/edge intentionally. Keep backend Services private.
```

### Mistake 6: Blind Annotation Copy

Wrong:

```text
Use NGINX annotation on ALB controller.
```

Correct:

```text
Annotations are controller-specific.
```

---

## 31. Interview-Ready Explanation

If interviewer asks:

```text
What is Kubernetes Ingress and how is it different from Service?
```

Strong answer:

```text
A Kubernetes Service gives stable internal networking and load balancing to Pods.
Ingress is a layer-7 HTTP/HTTPS routing resource that defines how external traffic
should enter the cluster and route to internal Services based on host and path. The
Ingress object itself is only the rule; an Ingress Controller such as NGINX, Traefik,
HAProxy, or a cloud load balancer controller is the actual component that watches
Ingress resources and configures a reverse proxy or load balancer. A typical request
flow is DNS to the Ingress Controller, host/path match, forwarding to a Kubernetes
Service, then Service routing to ready Pods. TLS is commonly terminated at the
Ingress Controller using a Kubernetes TLS Secret. In production, I would usually
expose only the API Gateway or edge service through Ingress and keep backend services
as ClusterIP.
```

Senior one-liner:

```text
Ingress defines external HTTP routing; the Ingress Controller implements it and forwards traffic to internal Services.
```

---

## 32. Senior Engineer Checklist

Before production:

```text
[ ] Ingress Controller installed
[ ] Controller externally reachable
[ ] ingressClassName correct
[ ] DNS points to controller/load balancer
[ ] Host rules match real domains
[ ] Path rules are intentional
[ ] Backend Service exists
[ ] Service has ready endpoints
[ ] TLS Secret configured
[ ] HTTPS redirect configured if needed
[ ] Actuator/sensitive paths not public
[ ] Body size limit appropriate
[ ] Timeouts appropriate
[ ] Rate limiting/WAF considered
[ ] Only edge/gateway exposed publicly
[ ] Backend services remain ClusterIP
[ ] Controller logs monitored
[ ] 404/502/503 debug playbook known
```

---

## 33. One-Page Cheat Sheet

```text
Core mental model:
Ingress = routing rule
Ingress Controller = actual proxy/load balancer

Service:
stable internal endpoint to Pods

Ingress:
external HTTP/HTTPS routing to Services

Flow:
Client -> DNS -> Ingress Controller -> Service -> Ready Pod

Routing:
Host-based:
    api.example.com -> api-service

Path-based:
    /api -> api-service
    /admin -> admin-service

TLS:
Ingress terminates HTTPS using TLS Secret.

IngressClass:
tells which controller handles Ingress.

Debug:
404 -> host/path/class mismatch
502 -> backend connection/port issue
503 -> no ready endpoints
TLS -> secret/cert/DNS issue

Commands:
kubectl get ingress -n miniurl
kubectl describe ingress name -n miniurl
kubectl get ingressclass
kubectl get svc -n miniurl
kubectl get endpoints -n miniurl
kubectl logs -n ingress-nginx deployment/ingress-nginx-controller

Golden rule:
Ingress routes to Service.
Service routes to ready Pods.
Controller makes Ingress real.
```

---

## 34. One Picture To Remember

```text
                 INGRESS CONTROLLER MENTAL MODEL

                       "Rule + Traffic Cop"

User Browser
https://miniurl.local/api/v1/urls
        |
        v
DNS resolves domain
        |
        v
+--------------------------------------+
| Ingress Controller                   |
| NGINX / Traefik / ALB                |
| receives real external traffic       |
+------------------+-------------------+
                   |
                   | watches
                   v
+--------------------------------------+
| Ingress Resource                     |
| host: miniurl.local                  |
| path: /api                           |
| backend: mini-url-shortener-service  |
+------------------+-------------------+
                   |
                   v
+--------------------------------------+
| Kubernetes Service                   |
| stable internal endpoint             |
+------------------+-------------------+
                   |
                   v
+-----------+   +-----------+   +-----------+
| Ready Pod |   | Ready Pod |   | Ready Pod |
+-----------+   +-----------+   +-----------+

FINAL MEMORY:

Ingress is the rule.
Controller is the proxy.
Service is the stable internal target.
Pods are the changing backends.
TLS usually terminates at Ingress.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Ingress defines external HTTP/HTTPS routing rules into the cluster.
2. Ingress Controller is the real proxy/load balancer that implements those rules.
3. Ingress routes to Services, and Services route to ready Pods.
4. Host and path rules decide which backend Service receives the request.
5. Most Ingress bugs are missing controller, wrong class, host/path mismatch, empty endpoints, or TLS/DNS issues.
```

Next possible chapters:

```text
052_Kubernetes_ConfigMap_Secret.md
053_Kubernetes_HPA_Resource_Tuning.md
054_Kubernetes_Debugging_Playbook.md
055_CI_CD_Docker_Kubernetes.md
```
