# 050_Kubernetes_Service.md
# MiniURLShortener — Kubernetes Service

> Core mental model: **A Kubernetes Service is the stable network front door for a changing set of Pods. Pods are temporary, but the Service name and virtual IP stay stable.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Why Pods Need Services](#4-why-pods-need-services)
- [5. Pod IP vs Service IP](#5-pod-ip-vs-service-ip)
- [6. Label Selector Mental Model](#6-label-selector-mental-model)
- [7. Endpoints Mental Model](#7-endpoints-mental-model)
- [8. ClusterIP Service](#8-clusterip-service)
- [9. NodePort Service](#9-nodeport-service)
- [10. LoadBalancer Service](#10-loadbalancer-service)
- [11. ExternalName Service](#11-externalname-service)
- [12. Service DNS Mental Model](#12-service-dns-mental-model)
- [13. Port, TargetPort, NodePort](#13-port-targetport-nodeport)
- [14. MiniURLShortener Service Architecture](#14-miniurlshortener-service-architecture)
- [15. Deployment Labels](#15-deployment-labels)
- [16. ClusterIP Service YAML](#16-clusterip-service-yaml)
- [17. NodePort Service YAML](#17-nodeport-service-yaml)
- [18. LoadBalancer Service YAML](#18-loadbalancer-service-yaml)
- [19. Service Discovery Between Apps](#19-service-discovery-between-apps)
- [20. Readiness And Service Traffic](#20-readiness-and-service-traffic)
- [21. kube-proxy Mental Model](#21-kube-proxy-mental-model)
- [22. iptables / IPVS Simple View](#22-iptables--ipvs-simple-view)
- [23. Service Traffic Flow](#23-service-traffic-flow)
- [24. Session Affinity](#24-session-affinity)
- [25. Headless Service](#25-headless-service)
- [26. Service For Postgres, Redis, Kafka](#26-service-for-postgres-redis-kafka)
- [27. Applying And Testing](#27-applying-and-testing)
- [28. Common kubectl Commands](#28-common-kubectl-commands)
- [29. Step-by-Step Dry Runs](#29-step-by-step-dry-runs)
- [30. Internal Execution Walkthrough](#30-internal-execution-walkthrough)
- [31. Debugging Mindset](#31-debugging-mindset)
- [32. Production Failure Stories](#32-production-failure-stories)
- [33. Common Mistakes](#33-common-mistakes)
- [34. Interview-Ready Explanation](#34-interview-ready-explanation)
- [35. Senior Engineer Checklist](#35-senior-engineer-checklist)
- [36. One-Page Cheat Sheet](#36-one-page-cheat-sheet)
- [37. One Picture To Remember](#37-one-picture-to-remember)

---

## 1. Why This Exists

In the previous Kubernetes Deployment chapter, MiniURLShortener was deployed as multiple Pods.

Example:

```text
Deployment desired replicas = 3
```

Kubernetes creates:

```text
mini-url-shortener-pod-a
mini-url-shortener-pod-b
mini-url-shortener-pod-c
```

But Pods are temporary.

A Pod can die.

A rollout can replace Pods.

Scaling can add more Pods.

Each new Pod gets a different IP.

Bad idea:

```text
Gateway calls Pod IP directly.
```

Why bad?

```text
Pod IP changes.
Pod may die.
New Pod has new IP.
Gateway config becomes stale.
```

Kubernetes Service solves this.

Service gives:

```text
stable DNS name
stable virtual IP
load balancing to ready Pods
selector-based Pod discovery
```

ASCII:

```text
Gateway
   |
   v
Service: mini-url-shortener-service
   |
   +-- Pod A
   +-- Pod B
   +-- Pod C
```

Production memory:

```text
Pods are cattle. Service is the stable address.
```

---

## 2. The One Core Mental Model

A Kubernetes Service is a stable front door.

Behind that front door, Pods can change.

ASCII:

```text
Stable Front Door
+--------------------------------+
| Service                        |
| mini-url-shortener-service     |
| ClusterIP: 10.96.10.20         |
+---------------+----------------+
                |
                v
Changing Backends
+--------+  +--------+  +--------+
| Pod A  |  | Pod B  |  | Pod C  |
+--------+  +--------+  +--------+
```

One-line memory:

```text
Service gives a stable network identity to unstable Pods.
```

The Service does not usually run your app.

It is a networking object.

It selects Pods using labels.

```text
Service selector:
    app = mini-url-shortener

Pods labels:
    app = mini-url-shortener
```

If labels match:

```text
Service sends traffic to those Pods.
```

If labels do not match:

```text
Service has no endpoints.
Traffic fails.
```

---

## 3. Problem Statement

Create Kubernetes Services for MiniURLShortener.

We need to understand:

```text
1. Why Service is needed.
2. How Service selects Pods.
3. What ClusterIP means.
4. What NodePort means.
5. What LoadBalancer means.
6. What ExternalName means.
7. What Service DNS name looks like.
8. What port, targetPort, and nodePort mean.
9. How readiness affects Service traffic.
10. How to debug Service traffic failures.
```

For MiniURLShortener we need:

```text
mini-url-shortener-service
gateway-service later
config-server-service
eureka-service
postgres-service
redis-service
kafka-service
```

This chapter focuses on the app Service.

---

## 4. Why Pods Need Services

Pods are not stable network targets.

Example:

```text
Pod A IP = 10.244.1.10
Pod B IP = 10.244.2.15
Pod C IP = 10.244.3.20
```

After rollout:

```text
Pod A deleted
Pod D created with IP = 10.244.1.44
```

If a client used Pod A IP:

```text
10.244.1.10
```

It breaks.

Service hides Pod churn.

ASCII:

```text
Before rollout:
Service -> Pod A, Pod B, Pod C

During rollout:
Service -> Pod B, Pod C, Pod D

After rollout:
Service -> Pod D, Pod E, Pod F
```

Client still calls:

```text
mini-url-shortener-service:8080
```

This is the key value.

Client does not care which Pod handles the request.

---

## 5. Pod IP vs Service IP

Pod IP:

```text
Belongs to one Pod.
Changes when Pod is recreated.
Not safe as stable dependency address.
```

Service IP:

```text
Virtual IP.
Stable for lifetime of Service.
Load balances to Pods.
```

ASCII:

```text
Service IP: 10.96.10.20
        |
        +-- Pod IP 10.244.1.10
        +-- Pod IP 10.244.2.15
        +-- Pod IP 10.244.3.20
```

If Pod dies:

```text
Pod IP disappears.
Service IP remains.
```

This is similar to:

```text
Domain name points to changing servers.
```

But Kubernetes Service works inside the cluster.

---

## 6. Label Selector Mental Model

Labels connect Services to Pods.

Deployment Pod template:

```yaml
metadata:
  labels:
    app: mini-url-shortener
```

Service selector:

```yaml
selector:
  app: mini-url-shortener
```

ASCII:

```text
Service selector:
    app=mini-url-shortener
             |
             v
Find Pods:
    Pod A label app=mini-url-shortener
    Pod B label app=mini-url-shortener
    Pod C label app=mini-url-shortener
```

If selector is wrong:

```yaml
selector:
  app: miniurl
```

But Pods have:

```yaml
app: mini-url-shortener
```

Then:

```text
Service finds zero Pods.
```

ASCII:

```text
Service selector app=miniurl
        |
        v
No matching Pods
        |
        v
No endpoints
        |
        v
Traffic fails
```

Golden rule:

```text
Service selector must match Pod labels exactly.
```

---

## 7. Endpoints Mental Model

Endpoints are the actual backend Pod IPs selected by a Service.

Service:

```text
mini-url-shortener-service
```

Endpoints:

```text
10.244.1.10:8080
10.244.2.15:8080
10.244.3.20:8080
```

ASCII:

```text
Service
  |
  v
Endpoints object
  |
  +-- 10.244.1.10:8080
  +-- 10.244.2.15:8080
  +-- 10.244.3.20:8080
```

Command:

```bash
kubectl get endpoints -n miniurl
```

or newer:

```bash
kubectl get endpointslices -n miniurl
```

If Service has no endpoints:

```text
selector mismatch
Pods not ready
Pods not running
wrong namespace
wrong target labels
```

Debug memory:

```text
Service problem? Check endpoints first.
```

---

## 8. ClusterIP Service

ClusterIP is the default Service type.

It exposes app inside the cluster.

YAML:

```yaml
spec:
  type: ClusterIP
```

Use ClusterIP when:

```text
one app calls another app inside cluster
gateway calls backend app
workers call internal services
```

ASCII:

```text
Inside Cluster

Gateway Pod
   |
   v
ClusterIP Service
   |
   v
Backend Pods
```

MiniURLShortener backend Service:

```text
mini-url-shortener-service:8080
```

Accessible inside same namespace:

```text
http://mini-url-shortener-service:8080
```

Accessible from another namespace:

```text
http://mini-url-shortener-service.miniurl.svc.cluster.local:8080
```

ClusterIP is usually not directly accessible from your laptop.

For local test:

```bash
kubectl port-forward svc/mini-url-shortener-service 8080:8080 -n miniurl
```

---

## 9. NodePort Service

NodePort exposes the Service on each Kubernetes Node.

YAML:

```yaml
spec:
  type: NodePort
```

Example:

```yaml
ports:
  - port: 8080
    targetPort: 8080
    nodePort: 30080
```

Traffic:

```text
NodeIP:30080 -> Service -> Pods
```

ASCII:

```text
External Client
      |
      v
Node IP:30080
      |
      v
NodePort Service
      |
      v
Pods
```

Use NodePort for:

```text
local testing
bare metal simple exposure
debugging
```

Usually not preferred as final cloud production entry.

In production, prefer:

```text
Ingress
LoadBalancer
API Gateway
cloud load balancer
```

NodePort range is commonly:

```text
30000-32767
```

---

## 10. LoadBalancer Service

LoadBalancer Service asks cloud provider to create external load balancer.

YAML:

```yaml
spec:
  type: LoadBalancer
```

Traffic:

```text
Internet
   |
   v
Cloud Load Balancer
   |
   v
Kubernetes Service
   |
   v
Pods
```

ASCII:

```text
External User
    |
    v
Cloud LB IP/DNS
    |
    v
LoadBalancer Service
    |
    v
Pod A / Pod B / Pod C
```

Use LoadBalancer for:

```text
simple external exposure in cloud
public APIs
gateway service
```

In production microservices:

```text
Usually expose only Gateway/Ingress externally.
Backend services stay ClusterIP.
```

For MiniURLShortener:

```text
gateway-service -> LoadBalancer or Ingress
mini-url-shortener-service -> ClusterIP
```

Why?

```text
Backend should not be directly public unless intentionally designed.
```

---

## 11. ExternalName Service

ExternalName maps a Kubernetes Service name to external DNS.

Example:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: external-postgres
  namespace: miniurl
spec:
  type: ExternalName
  externalName: mydb.example.com
```

Now app can call:

```text
external-postgres.miniurl.svc.cluster.local
```

Kubernetes DNS returns:

```text
mydb.example.com
```

ASCII:

```text
App
 |
 v
external-postgres
 |
 v
DNS alias
 |
 v
mydb.example.com
```

Use for:

```text
managed database DNS alias
external API alias
migration compatibility
```

Limitations:

```text
No load balancing by Kubernetes.
DNS alias only.
No port mapping in same way as ClusterIP.
```

---

## 12. Service DNS Mental Model

Kubernetes creates DNS records for Services.

Format:

```text
service-name.namespace.svc.cluster.local
```

Example:

```text
mini-url-shortener-service.miniurl.svc.cluster.local
```

Inside same namespace:

```text
mini-url-shortener-service
```

From another namespace:

```text
mini-url-shortener-service.miniurl
```

Full:

```text
mini-url-shortener-service.miniurl.svc.cluster.local
```

ASCII:

```text
App Pod
  |
  v
DNS query:
mini-url-shortener-service
  |
  v
CoreDNS
  |
  v
ClusterIP
```

Debug DNS:

```bash
kubectl exec -it <pod> -n miniurl -- nslookup mini-url-shortener-service
```

If DNS fails:

```text
CoreDNS issue
wrong service name
wrong namespace
network policy
```

---

## 13. Port, TargetPort, NodePort

This is very important.

Service YAML:

```yaml
ports:
  - port: 80
    targetPort: 8080
    nodePort: 30080
```

Meaning:

```text
port:
    Service port

targetPort:
    Pod/container port

nodePort:
    Node external port for NodePort service
```

ASCII:

```text
Client
  |
  v
Service port: 80
  |
  v
Pod targetPort: 8080
```

For NodePort:

```text
External Client
  |
  v
Node port: 30080
  |
  v
Service port: 80
  |
  v
Pod targetPort: 8080
```

MiniURLShortener usually:

```yaml
port: 8080
targetPort: 8080
```

Because Spring Boot listens on 8080.

But common production pattern:

```yaml
port: 80
targetPort: 8080
```

So internal clients call:

```text
http://service:80
```

while app still listens:

```text
8080
```

---

## 14. MiniURLShortener Service Architecture

Recommended Kubernetes exposure:

```text
External User
   |
   v
Ingress / Gateway LoadBalancer
   |
   v
gateway-service
   |
   v
mini-url-shortener-service
   |
   v
mini-url-shortener Pods
```

ASCII:

```text
Internet
   |
   v
Ingress / Cloud LB
   |
   v
Gateway Service
   |
   v
MiniURLShortener Service
   |
   +-- Pod 1
   +-- Pod 2
   +-- Pod 3
```

For this chapter:

```text
mini-url-shortener-service = ClusterIP
```

Because backend should be internal.

For local learning:

```text
Use kubectl port-forward
or NodePort
```

Production:

```text
Ingress/Gateway handles TLS, routing, public exposure.
Backend Services stay private.
```

---

## 15. Deployment Labels

Deployment must label Pods correctly.

Example:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mini-url-shortener
  namespace: miniurl
spec:
  replicas: 3
  selector:
    matchLabels:
      app: mini-url-shortener
  template:
    metadata:
      labels:
        app: mini-url-shortener
    spec:
      containers:
        - name: mini-url-shortener
          image: mini-url-shortener:050
          ports:
            - containerPort: 8080
```

Important:

```text
spec.selector.matchLabels must match template.metadata.labels.
Service selector should match same labels.
```

ASCII:

```text
Deployment selector:
    app=mini-url-shortener

Pod labels:
    app=mini-url-shortener

Service selector:
    app=mini-url-shortener
```

If these three do not align:

```text
Deployment may not manage Pods correctly.
Service may not route traffic correctly.
```

---

## 16. ClusterIP Service YAML

Create:

```text
k8s/miniurl-service-clusterip.yml
```

YAML:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: mini-url-shortener-service
  namespace: miniurl
  labels:
    app: mini-url-shortener
spec:
  type: ClusterIP
  selector:
    app: mini-url-shortener
  ports:
    - name: http
      port: 8080
      targetPort: 8080
      protocol: TCP
```

Apply:

```bash
kubectl apply -f k8s/miniurl-service-clusterip.yml
```

Test inside cluster:

```bash
kubectl run curl-test \
  --image=curlimages/curl \
  --rm -it \
  -n miniurl \
  -- curl http://mini-url-shortener-service:8080/actuator/health
```

Local test:

```bash
kubectl port-forward svc/mini-url-shortener-service 8080:8080 -n miniurl
```

Then:

```bash
curl http://localhost:8080/actuator/health
```

---

## 17. NodePort Service YAML

Create:

```text
k8s/miniurl-service-nodeport.yml
```

YAML:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: mini-url-shortener-nodeport
  namespace: miniurl
spec:
  type: NodePort
  selector:
    app: mini-url-shortener
  ports:
    - name: http
      port: 8080
      targetPort: 8080
      nodePort: 30080
      protocol: TCP
```

Access:

```text
http://<node-ip>:30080
```

For Minikube:

```bash
minikube service mini-url-shortener-nodeport -n miniurl
```

ASCII:

```text
Host
 |
 v
NodeIP:30080
 |
 v
NodePort Service
 |
 v
Pod:8080
```

Use NodePort for learning/debugging.

For production cloud exposure, use LoadBalancer/Ingress.

---

## 18. LoadBalancer Service YAML

Create:

```text
k8s/miniurl-service-loadbalancer.yml
```

YAML:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: mini-url-shortener-lb
  namespace: miniurl
spec:
  type: LoadBalancer
  selector:
    app: mini-url-shortener
  ports:
    - name: http
      port: 80
      targetPort: 8080
      protocol: TCP
```

Traffic:

```text
External LoadBalancer port 80
       |
       v
Service
       |
       v
Pod targetPort 8080
```

Check:

```bash
kubectl get svc -n miniurl
```

In cloud:

```text
EXTERNAL-IP appears after cloud provider provisions LB.
```

In local Kind/Minikube:

```text
EXTERNAL-IP may stay pending unless addon/tunnel is configured.
```

For Minikube:

```bash
minikube tunnel
```

or use NodePort.

---

## 19. Service Discovery Between Apps

Suppose Gateway calls MiniURLShortener.

Gateway config:

```yaml
routes:
  - id: miniurl
    uri: http://mini-url-shortener-service:8080
    predicates:
      - Path=/api/v1/urls/**
```

Inside Kubernetes same namespace:

```text
http://mini-url-shortener-service:8080
```

Different namespace:

```text
http://mini-url-shortener-service.miniurl:8080
```

Full DNS:

```text
http://mini-url-shortener-service.miniurl.svc.cluster.local:8080
```

ASCII:

```text
Gateway Pod
    |
    v
DNS: mini-url-shortener-service
    |
    v
Service ClusterIP
    |
    v
MiniURLShortener Pods
```

This replaces many local Compose names.

Compose:

```text
mini-url-shortener:8080
```

Kubernetes:

```text
mini-url-shortener-service:8080
```

---

## 20. Readiness And Service Traffic

Service sends traffic only to ready Pods.

If Pod is running but not ready:

```text
Service does not include it as endpoint.
```

ASCII:

```text
Pods:
Pod A ready       -> endpoint included
Pod B not ready   -> endpoint excluded
Pod C ready       -> endpoint included

Service routes only to:
Pod A, Pod C
```

This is why readiness is critical during rollout.

New Pod starts:

```text
container running
Spring Boot booting
readiness false
no traffic yet
```

After app is ready:

```text
readiness true
Service adds endpoint
traffic begins
```

Command:

```bash
kubectl get endpoints mini-url-shortener-service -n miniurl
```

If no endpoints but Pods are running:

```text
readiness may be failing
selector may be wrong
```

---

## 21. kube-proxy Mental Model

kube-proxy runs on each Node.

It programs networking rules so Service IP can route to Pod IPs.

ASCII:

```text
Client Pod
   |
   v
Service ClusterIP
   |
   v
Node networking rules
managed by kube-proxy
   |
   v
One backend Pod IP
```

kube-proxy watches:

```text
Services
EndpointSlices
```

When endpoints change:

```text
kube-proxy updates routing rules
```

Simple mental model:

```text
Service is the API object.
EndpointSlice lists backends.
kube-proxy makes traffic routing work.
```

You usually do not call kube-proxy directly.

But knowing it helps debugging.

---

## 22. iptables / IPVS Simple View

kube-proxy can use modes like:

```text
iptables
IPVS
```

High-level only:

```text
iptables mode:
    Linux rules redirect Service IP traffic to Pod IPs.

IPVS mode:
    Linux virtual server load balancing for Service traffic.
```

ASCII:

```text
Packet to Service IP
       |
       v
iptables/IPVS rules
       |
       v
selected Pod IP
```

Do not overcomplicate for now.

For interview:

```text
Kubernetes Service gets a virtual IP. kube-proxy programs node networking rules using iptables or IPVS so traffic to that virtual IP is forwarded to one of the ready Pod endpoints.
```

That is enough for most backend interviews.

---

## 23. Service Traffic Flow

Request from Gateway to MiniURLShortener:

```text
1. Gateway calls http://mini-url-shortener-service:8080.
2. CoreDNS resolves service name to ClusterIP.
3. Packet goes to ClusterIP.
4. kube-proxy rules select backend endpoint.
5. Traffic reaches one ready Pod.
6. Spring Boot handles request.
7. Response returns.
```

ASCII:

```text
Gateway Pod
   |
   v
DNS lookup
   |
   v
ClusterIP
   |
   v
kube-proxy rules
   |
   v
Ready Pod
   |
   v
Spring Boot
```

If Pod dies:

```text
Endpoint removed.
Service routes to remaining Pods.
```

If new Pod becomes ready:

```text
Endpoint added.
Service can route to it.
```

This is dynamic service discovery.

---

## 24. Session Affinity

By default, Service load balances across Pods.

Sometimes you want same client IP to go to same Pod.

YAML:

```yaml
spec:
  sessionAffinity: ClientIP
```

Meaning:

```text
Requests from same client IP prefer same backend Pod.
```

Use cases:

```text
legacy sticky session apps
stateful in-memory session apps
```

For MiniURLShortener:

```text
Usually do not need session affinity.
```

Why?

```text
App should be stateless.
Session/token data should be external or encoded in JWT.
Cache should be Redis.
Database should be shared.
```

Production rule:

```text
Avoid sticky sessions unless necessary.
Stateless services scale better.
```

---

## 25. Headless Service

A headless Service has no ClusterIP.

YAML:

```yaml
spec:
  clusterIP: None
```

Instead of returning one virtual IP, DNS returns Pod IPs directly.

Use cases:

```text
StatefulSets
databases
Kafka brokers
direct Pod discovery
```

ASCII:

```text
Normal Service:
DNS -> one ClusterIP -> load balances

Headless Service:
DNS -> Pod IP 1, Pod IP 2, Pod IP 3
```

For MiniURLShortener app:

```text
Use normal ClusterIP.
```

For systems like Kafka/Postgres StatefulSet:

```text
Headless Service may be useful.
```

Do not use headless service unless you know why.

---

## 26. Service For Postgres, Redis, Kafka

If dependencies run inside Kubernetes, they also need Services.

Postgres Service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: postgres-service
  namespace: miniurl
spec:
  type: ClusterIP
  selector:
    app: postgres
  ports:
    - port: 5432
      targetPort: 5432
```

Redis Service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: redis-service
  namespace: miniurl
spec:
  type: ClusterIP
  selector:
    app: redis
  ports:
    - port: 6379
      targetPort: 6379
```

Kafka Service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: kafka-service
  namespace: miniurl
spec:
  type: ClusterIP
  selector:
    app: kafka
  ports:
    - port: 9092
      targetPort: 9092
```

App config:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-service:5432/miniurl
SPRING_DATA_REDIS_HOST=redis-service
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka-service:9092
```

ASCII:

```text
MiniURLShortener Pod
   |
   +--> postgres-service:5432
   +--> redis-service:6379
   +--> kafka-service:9092
```

---

## 27. Applying And Testing

Apply ClusterIP Service:

```bash
kubectl apply -f k8s/miniurl-service-clusterip.yml
```

Check Service:

```bash
kubectl get svc -n miniurl
```

Check details:

```bash
kubectl describe svc mini-url-shortener-service -n miniurl
```

Check endpoints:

```bash
kubectl get endpoints mini-url-shortener-service -n miniurl
```

Port-forward:

```bash
kubectl port-forward svc/mini-url-shortener-service 8080:8080 -n miniurl
```

Test:

```bash
curl http://localhost:8080/actuator/health
```

Test inside cluster:

```bash
kubectl run curl-test \
  --image=curlimages/curl \
  --rm -it \
  -n miniurl \
  -- curl http://mini-url-shortener-service:8080/actuator/health
```

Delete:

```bash
kubectl delete -f k8s/miniurl-service-clusterip.yml
```

---

## 28. Common kubectl Commands

List Services:

```bash
kubectl get svc -n miniurl
```

Describe Service:

```bash
kubectl describe svc mini-url-shortener-service -n miniurl
```

Get Endpoints:

```bash
kubectl get endpoints -n miniurl
```

Get EndpointSlices:

```bash
kubectl get endpointslices -n miniurl
```

Check Pods and labels:

```bash
kubectl get pods -n miniurl --show-labels
```

Port forward:

```bash
kubectl port-forward svc/mini-url-shortener-service 8080:8080 -n miniurl
```

Test DNS from temporary Pod:

```bash
kubectl run dns-test \
  --image=busybox \
  --rm -it \
  -n miniurl \
  -- nslookup mini-url-shortener-service
```

Test HTTP from temporary Pod:

```bash
kubectl run curl-test \
  --image=curlimages/curl \
  --rm -it \
  -n miniurl \
  -- curl http://mini-url-shortener-service:8080/actuator/health
```

---

## 29. Step-by-Step Dry Runs

### Dry Run 1: Service Finds Pods

Pods:

```text
Pod A label app=mini-url-shortener
Pod B label app=mini-url-shortener
Pod C label app=mini-url-shortener
```

Service selector:

```text
app=mini-url-shortener
```

Flow:

```text
1. Service selector matches Pods.
2. EndpointSlice stores Pod IPs.
3. kube-proxy programs rules.
4. Requests to Service route to Pods.
```

ASCII:

```text
Service selector
   |
   v
Pod labels match
   |
   v
Endpoints created
   |
   v
Traffic works
```

---

### Dry Run 2: Selector Mismatch

Pods:

```text
app=mini-url-shortener
```

Service:

```text
selector app=miniurl
```

Flow:

```text
1. Service searches for app=miniurl.
2. No Pods match.
3. Endpoints empty.
4. Request fails.
```

Command:

```bash
kubectl get endpoints mini-url-shortener-service -n miniurl
```

Result:

```text
<none>
```

Fix:

```text
Change selector to app=mini-url-shortener.
```

---

### Dry Run 3: Pod Not Ready

Pods:

```text
Pod A ready
Pod B not ready
Pod C ready
```

Service endpoints:

```text
Pod A
Pod C
```

Flow:

```text
1. Pod B readiness fails.
2. Endpoint controller excludes Pod B.
3. Service routes only to Pod A and Pod C.
4. Pod B receives no traffic.
```

ASCII:

```text
Service
  |
  +--> Pod A ready
  +--> Pod C ready
  X--> Pod B not ready
```

---

### Dry Run 4: Port vs TargetPort

Service:

```yaml
port: 80
targetPort: 8080
```

Client calls:

```text
http://mini-url-shortener-service
```

Flow:

```text
1. Client connects to Service port 80.
2. Service forwards to Pod port 8080.
3. Spring Boot receives request on 8080.
```

ASCII:

```text
Client -> Service:80 -> Pod:8080
```

---

### Dry Run 5: Pod Replaced During Rollout

Before:

```text
Service endpoints:
Pod A, Pod B, Pod C
```

Rollout:

```text
Pod A deleted
Pod D created
Pod D readiness passes
```

After:

```text
Service endpoints:
Pod B, Pod C, Pod D
```

Client still calls:

```text
mini-url-shortener-service
```

No client config change needed.

---

## 30. Internal Execution Walkthrough

When you create Service:

```text
1. kubectl sends Service YAML to API Server.
2. API Server stores Service in etcd.
3. EndpointSlice controller watches Services and Pods.
4. It finds Pods matching selector.
5. It creates EndpointSlices with Pod IPs and ports.
6. CoreDNS creates DNS record for Service.
7. kube-proxy watches Service and EndpointSlices.
8. kube-proxy programs node networking rules.
9. Client calls Service DNS name.
10. DNS returns ClusterIP.
11. Traffic to ClusterIP is routed to a ready Pod endpoint.
```

ASCII:

```text
kubectl apply service
      |
      v
API Server / etcd
      |
      v
EndpointSlice Controller
      |
      v
EndpointSlices: Pod IPs
      |
      v
CoreDNS + kube-proxy
      |
      v
Service traffic works
```

This is the Service control-plane/data-plane split:

```text
Control plane:
    stores Service, watches Pods, creates endpoints

Data plane:
    kube-proxy/network rules route packets
```

---

## 31. Debugging Mindset

When Service does not work, ask:

```text
Does Service exist?
Is it in the correct namespace?
Is selector correct?
Do Pods have matching labels?
Are Pods ready?
Are endpoints created?
Is targetPort correct?
Is app listening on targetPort?
Is DNS working?
Is NetworkPolicy blocking traffic?
Is kube-proxy healthy?
```

Debug map:

```text
Service exists but no endpoints:
    selector mismatch
    Pods not ready
    wrong namespace

Endpoints exist but request fails:
    targetPort wrong
    app not listening
    app returns error
    network policy
    kube-proxy issue

DNS fails:
    wrong service name
    wrong namespace
    CoreDNS issue

Port-forward works but service call fails inside cluster:
    DNS/network policy/client config issue

Service works inside cluster but not outside:
    wrong Service type
    no Ingress/LoadBalancer/NodePort
```

Commands:

```bash
kubectl get svc -n miniurl
kubectl describe svc mini-url-shortener-service -n miniurl
kubectl get pods -n miniurl --show-labels
kubectl get endpoints -n miniurl
kubectl get endpointslices -n miniurl
kubectl logs deployment/mini-url-shortener -n miniurl
```

Golden question:

```text
Is this a selector problem, readiness problem, port problem, DNS problem, or external exposure problem?
```

---

## 32. Production Failure Stories

### Failure Story 1: Empty Endpoints

Service selector:

```yaml
app: miniurl
```

Pod label:

```yaml
app: mini-url-shortener
```

Result:

```text
Service has no endpoints.
Gateway gets connection failures.
```

Fix:

```text
Align selector and Pod labels.
```

Lesson:

```text
Labels are the wiring of Kubernetes Services.
```

---

### Failure Story 2: Wrong targetPort

Spring Boot listens:

```text
8080
```

Service:

```yaml
targetPort: 80
```

Result:

```text
Traffic reaches wrong port.
Connection refused.
```

Fix:

```yaml
targetPort: 8080
```

Lesson:

```text
port is Service port; targetPort is Pod port.
```

---

### Failure Story 3: Pod Running But Not Ready

Pod status:

```text
Running
```

But readiness probe fails.

Service endpoints:

```text
empty
```

Developer thinks Service is broken.

Actually:

```text
Kubernetes is protecting traffic from unready Pod.
```

Fix:

```text
Check readiness endpoint and app dependency readiness.
```

Lesson:

```text
Running does not mean ready.
```

---

### Failure Story 4: Backend Exposed Publicly

Backend Service type:

```yaml
LoadBalancer
```

Result:

```text
MiniURLShortener backend directly public.
Bypasses Gateway security/rate limiting.
```

Fix:

```text
Use ClusterIP for backend.
Expose only Gateway/Ingress publicly.
```

Lesson:

```text
Not every Service should be public.
```

---

### Failure Story 5: latest Tag During Rollout

Service is fine, but different Pods run different images due to `latest`.

Result:

```text
Same Service routes to inconsistent app versions.
Hard-to-debug behavior.
```

Fix:

```text
Use immutable image tags and controlled rollout.
```

Lesson:

```text
Stable Service does not fix unstable deployment discipline.
```

---

## 33. Common Mistakes

### Mistake 1: Thinking Service creates Pods

Wrong:

```text
Service runs my app.
```

Correct:

```text
Deployment runs Pods. Service routes to Pods.
```

### Mistake 2: Selector mismatch

Wrong:

```text
Service selector does not match Pod labels.
```

Correct:

```text
Keep labels consistent.
```

### Mistake 3: Confusing port and targetPort

Wrong:

```text
targetPort set to Service port accidentally.
```

Correct:

```text
targetPort must match container listening port.
```

### Mistake 4: Exposing backend with LoadBalancer

Wrong:

```text
Every microservice gets public LB.
```

Correct:

```text
Usually only Gateway/Ingress is public.
```

### Mistake 5: Ignoring readiness

Wrong:

```text
Pod is Running, so Service should work.
```

Correct:

```text
Service routes only to ready endpoints.
```

### Mistake 6: Using Pod IP directly

Wrong:

```text
Gateway calls Pod IP.
```

Correct:

```text
Gateway calls Service DNS.
```

### Mistake 7: Wrong namespace DNS

Wrong:

```text
Calling service name from another namespace without namespace suffix.
```

Correct:

```text
service.namespace
```

---

## 34. Interview-Ready Explanation

If interviewer asks:

```text
What is a Kubernetes Service and why do we need it?
```

Strong answer:

```text
A Kubernetes Service provides a stable network identity and load balancing for a
dynamic set of Pods. Pods are temporary and their IPs change during crashes, scaling,
and rolling updates, so clients should not call Pod IPs directly. A Service uses a
label selector to find matching ready Pods and exposes them through a stable DNS name
and virtual IP. For internal microservice communication I would usually use a
ClusterIP Service. For local or bare-metal exposure NodePort can be used, and in
cloud environments LoadBalancer can provision an external load balancer. The Service
port is the port clients call, targetPort is the Pod/container port, and nodePort is
the external node port for NodePort services. If a Service has no traffic, I would
first check selectors, Pod labels, readiness, endpoints, and targetPort.
```

Why this is strong:

```text
1. Explains stable identity.
2. Explains Pod IP instability.
3. Explains selectors.
4. Explains ready endpoints.
5. Explains ClusterIP/NodePort/LoadBalancer.
6. Explains port/targetPort/nodePort.
7. Explains debugging path.
```

Senior one-liner:

```text
A Service is Kubernetes service discovery plus load balancing for ready Pods selected by labels.
```

---

## 35. Senior Engineer Checklist

Before production:

```text
[ ] Backend Services use ClusterIP
[ ] Public exposure goes through Gateway/Ingress/LoadBalancer intentionally
[ ] Service selector matches Pod labels
[ ] Deployment selector matches Pod template labels
[ ] Service targetPort matches container port
[ ] Readiness probe configured
[ ] Endpoints exist after deployment
[ ] DNS name tested from another Pod
[ ] Port-forward tested for local debugging
[ ] No client uses Pod IP directly
[ ] Service names documented
[ ] Namespace DNS understood
[ ] LoadBalancer used only where intended
[ ] NetworkPolicy impact understood if enabled
```

If these are checked, your Kubernetes Service setup is production-shaped.

---

## 36. One-Page Cheat Sheet

```text
Core mental model:
Service = stable front door for changing Pods.

Pod IP:
temporary

Service IP/DNS:
stable

Service selects Pods by labels:
selector app=x
Pod label app=x

Endpoints:
actual ready Pod IPs behind Service

Service types:
ClusterIP    -> internal cluster access
NodePort     -> expose on each node
LoadBalancer -> cloud external LB
ExternalName -> DNS alias

Ports:
port       -> Service port
targetPort -> Pod/container port
nodePort   -> Node external port

DNS:
same namespace:
    service-name

different namespace:
    service-name.namespace

full:
    service-name.namespace.svc.cluster.local

Debug:
kubectl get svc -n miniurl
kubectl describe svc name -n miniurl
kubectl get endpoints -n miniurl
kubectl get pods --show-labels -n miniurl
kubectl port-forward svc/name 8080:8080 -n miniurl

Golden rule:
Deployment runs Pods.
Service routes to ready Pods.
Labels connect them.
```

---

## 37. One Picture To Remember

```text
                 KUBERNETES SERVICE MENTAL MODEL

                   "Stable front door, changing Pods"

Client / Gateway
       |
       v
+--------------------------------------+
| Service                              |
| name: mini-url-shortener-service     |
| ClusterIP: stable virtual IP         |
| selector: app=mini-url-shortener     |
+-------------------+------------------+
                    |
                    v
          Ready Endpoints Only
                    |
       +------------+------------+
       |            |            |
       v            v            v
+-----------+  +-----------+  +-----------+
| Pod A     |  | Pod B     |  | Pod C     |
| 10.244.x  |  | 10.244.y  |  | 10.244.z  |
| ready     |  | ready     |  | ready     |
+-----------+  +-----------+  +-----------+

If Pod dies:
    endpoint removed

If new Pod ready:
    endpoint added

Client still calls:
    mini-url-shortener-service:8080


FINAL MEMORY:

Pods are temporary.
Service is stable.
Labels connect Service to Pods.
Readiness controls endpoints.
port is Service port.
targetPort is Pod port.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. A Kubernetes Service gives stable DNS and virtual IP for changing Pods.
2. Services find Pods using label selectors.
3. Service traffic goes only to ready Pod endpoints.
4. ClusterIP is for internal access, NodePort for node-level exposure, and LoadBalancer for cloud external exposure.
5. Most Service bugs are selector mismatch, readiness failure, wrong targetPort, or wrong namespace DNS.
```

Next possible chapters:

```text
051_Kubernetes_Ingress.md
052_Kubernetes_ConfigMap_Secret.md
053_Kubernetes_HPA_Resource_Tuning.md
054_Kubernetes_Debugging_Playbook.md
