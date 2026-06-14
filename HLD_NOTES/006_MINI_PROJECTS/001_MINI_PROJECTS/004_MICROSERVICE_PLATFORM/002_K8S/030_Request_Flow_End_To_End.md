# 030_Request_Flow_End_To_End.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Request Flow Matters

Most Kubernetes learners understand how to create a Pod.

But in production, users do not care whether a Pod exists.

Users care about this:

```text
Can my request reach the application and get a correct response?
```

That means request flow is the real production path.

A Pod can be Running.

A Deployment can be Available.

A Service can exist.

Ingress can exist.

DNS can resolve.

Still the user may see:

```text
502 Bad Gateway
503 Service Unavailable
Connection timeout
TLS error
Slow response
Random failures
```

So the correct mental model is not:

```text
I deployed my app, so traffic works.
```

The correct model is:

```text
Traffic passes through many doors.
Every door must know where to send the request.
Every door can fail differently.
```

One picture:

```text
Client
  |
  v
DNS
  |
  v
Load Balancer
  |
  v
Ingress Controller
  |
  v
Kubernetes Service
  |
  v
Endpoint / EndpointSlice
  |
  v
kube-proxy / CNI routing
  |
  v
Pod IP
  |
  v
Container port
  |
  v
Spring Boot handler
  |
  v
Response goes back
```

If you remember only one thing from this chapter, remember this:

```text
Kubernetes request flow is a chain of routing decisions.
Debug the chain from outside to inside, then inside to outside.
```

---

# 2. The Wrong Way To Think About Request Flow

Bad mental model:

```text
Service sends traffic to Pod.
```

This is too short.

It hides the real path.

A Service is not a process forwarding packets like Nginx.

A ClusterIP Service is usually a virtual IP implemented by node networking rules.

An Ingress object is not itself a reverse proxy.

An Ingress Controller is the real proxy process that reads Ingress rules and configures itself.

A Pod is not reached by container name.

It is reached by Pod IP and container port.

Wrong memorized model:

```text
Ingress -> Service -> Pod
```

Better production model:

```text
Ingress Controller process receives traffic
       |
       v
Ingress rule tells which Service backend
       |
       v
Service selector points to ready endpoints
       |
       v
Node networking chooses one Pod IP
       |
       v
CNI delivers packet to the Pod network namespace
       |
       v
Container process accepts connection on target port
```

ASCII:

```text
Simple diagram people memorize:

Client -> Ingress -> Service -> Pod

Production diagram you debug:

Client
  |
  | DNS + TLS + HTTP
  v
External Load Balancer
  |
  | nodePort / proxy port
  v
Ingress Controller Pod
  |
  | routing rule host/path
  v
Service virtual IP
  |
  | selector -> EndpointSlice
  v
Ready Pod IP
  |
  | CNI network path
  v
Container port
  |
  v
Spring Boot controller
```

Do not memorize the names.

Understand each handoff.

Every handoff asks:

```text
Who receives this request now?
How does it know the next destination?
What happens if the next destination is missing?
```

---

# 3. Real World Analogy: Parcel Delivery

Imagine a customer sends a parcel to an apartment.

Address:

```text
City: Bucharest
Building: Cloud Tower
Floor: 7
Apartment: 703
Person: Order Service
```

Delivery path:

```text
Customer
  |
  v
National courier hub
  |
  v
City warehouse
  |
  v
Building reception
  |
  v
Floor corridor
  |
  v
Apartment door
  |
  v
Person receives parcel
```

Kubernetes request flow is similar.

```text
Customer browser      = external client
DNS                   = address lookup
Load Balancer         = city/building entrance
Ingress Controller    = reception desk
Service               = department name
EndpointSlice         = current employee list
Pod IP                = apartment door
Container port        = person listening
Spring Boot handler   = worker processing request
```

If the parcel fails, you do not say:

```text
The apartment is broken.
```

You ask:

```text
Did the address resolve?
Did the courier reach the city?
Did reception know the department?
Was the employee present today?
Was the apartment door open?
Did the person accept the parcel?
```

That is exactly how you debug Kubernetes traffic.

```text
Did DNS resolve?
Did load balancer reach ingress?
Did ingress match host/path?
Did Service have endpoints?
Did network route to Pod?
Was the container listening?
Did the app return success?
```

---

# 4. The Full Request Flow Picture

```text
                   OUTSIDE CLUSTER

+---------+       +------+       +-------------------+
| Client  | ----> | DNS  | ----> | External LB / IP  |
+---------+       +------+       +---------+---------+
                                           |
                                           v
                 INSIDE / EDGE OF CLUSTER

                              +----------------------+
                              | Ingress Controller   |
                              | nginx/traefik/envoy  |
                              +----------+-----------+
                                         |
                                         | host/path rule
                                         v
                              +----------------------+
                              | Service              |
                              | order-service        |
                              | ClusterIP virtual IP |
                              +----------+-----------+
                                         |
                                         | selector
                                         v
                              +----------------------+
                              | EndpointSlice        |
                              | ready Pod IPs        |
                              +----------+-----------+
                                         |
                                         | kube-proxy / CNI
                                         v
                              +----------------------+
                              | Pod                  |
                              | 10.244.2.15:8080     |
                              +----------+-----------+
                                         |
                                         v
                              +----------------------+
                              | Spring Boot          |
                              | @RestController      |
                              +----------------------+
```

Request flow is not one jump.

It is a chain.

Each layer transforms the destination:

```text
Domain name       -> IP address
External IP       -> ingress controller
Host/path         -> service backend
Service selector  -> endpoint list
Virtual service   -> real Pod IP
Pod IP:port       -> Java process
HTTP path         -> controller method
```

---

# 5. Example Application: Order Service

Spring Boot endpoint:

```java
package com.example.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @GetMapping("/orders/{id}")
    public OrderResponse getOrder(@PathVariable String id) {
        return new OrderResponse(id, "PAID", "Request reached order-service");
    }

    public record OrderResponse(String id, String status, String message) {}
}
```

The application listens on port `8080`:

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
        include: health,info,prometheus
```

The request we want:

```text
GET https://api.example.com/orders/42
```

Expected final result:

```json
{
  "id": "42",
  "status": "PAID",
  "message": "Request reached order-service"
}
```

But production does not care only about Java code.

For this response to happen, the following must all be true:

```text
DNS resolves api.example.com
TLS certificate is valid
External LB reaches ingress controller
Ingress rule matches host api.example.com
Ingress rule matches path /orders
Service exists
Service has matching ready endpoints
Pod network is healthy
Container listens on 8080
Spring Boot route exists
Dependencies are healthy enough
Response can travel back
```

---

# 6. Kubernetes YAML For The Flow

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
  name: order-service
spec:
  rules:
    - host: api.example.com
      http:
        paths:
          - path: /orders
            pathType: Prefix
            backend:
              service:
                name: order-service
                port:
                  number: 80
```

Important mental model:

```text
Ingress sends to Service port 80.
Service sends to Pod targetPort 8080.
Container listens on 8080.
```

Diagram:

```text
Ingress backend:
  service: order-service:80
             |
             v
Service port:
  port: 80
  targetPort: 8080
             |
             v
Pod container:
  containerPort: 8080
             |
             v
Spring Boot:
  server.port=8080
```

---

# 7. DNS Stage

The client starts with a name:

```text
api.example.com
```

It needs an IP address.

DNS answers:

```text
api.example.com -> 35.200.10.25
```

That IP usually belongs to:

```text
Cloud Load Balancer
Ingress Gateway
Reverse proxy entry point
```

ASCII:

```text
Client
  |
  | resolve api.example.com
  v
DNS
  |
  | 35.200.10.25
  v
Client connects to 35.200.10.25
```

Common failures:

```text
Wrong DNS record
Old IP still cached
DNS points to wrong environment
DNS record missing
Split-horizon DNS confusion
```

Debug:

```bash
nslookup api.example.com
dig api.example.com
curl -v https://api.example.com/orders/42
```

Mental model:

```text
Before Kubernetes can receive traffic, the outside world must know where the cluster entrance is.
```

If DNS is wrong, Kubernetes may be perfectly healthy but unreachable.

---

# 8. TLS Stage

For HTTPS, TLS happens before HTTP routing is useful.

Client asks:

```text
Can I securely talk to api.example.com?
```

The ingress controller or load balancer presents a certificate.

The certificate must match:

```text
api.example.com
```

Flow:

```text
Client
  |
  | TLS ClientHello SNI=api.example.com
  v
Load Balancer / Ingress Controller
  |
  | returns certificate
  v
Client verifies certificate
```

Common failures:

```text
Certificate expired
Certificate does not include host
Wrong secret mounted
TLS terminated at wrong layer
Client uses HTTP against HTTPS port
Ingress class not processing TLS rule
```

Debug:

```bash
curl -vk https://api.example.com/orders/42
openssl s_client -connect api.example.com:443 -servername api.example.com
kubectl get secret
kubectl describe ingress order-service
```

Mental model:

```text
TLS failure means request may never reach your Spring Boot controller.
```

Do not debug Java logs first when the error is certificate-related.

---

# 9. External Load Balancer Stage

In cloud Kubernetes, external traffic often reaches a cloud load balancer first.

```text
Internet
  |
  v
Cloud Load Balancer
  |
  v
Ingress Controller Service
```

The load balancer may send traffic to nodes or directly to ingress controller pods depending on setup.

Common Kubernetes object:

```yaml
kind: Service
metadata:
  name: ingress-nginx-controller
spec:
  type: LoadBalancer
```

This creates an external entry:

```text
EXTERNAL-IP: 35.200.10.25
```

ASCII:

```text
Client
  |
  v
35.200.10.25
  |
  v
Cloud Load Balancer
  |
  v
Node / Ingress Controller Pod
```

Common failures:

```text
Load balancer health check fails
Security group blocks traffic
Firewall blocks 443/80
Wrong backend port
No healthy ingress controller pods
Cloud quota issue
```

Debug:

```bash
kubectl get svc -n ingress-nginx
kubectl get pods -n ingress-nginx -o wide
kubectl describe svc -n ingress-nginx ingress-nginx-controller
```

Mental model:

```text
The external load balancer is the cluster doorbell.
If the doorbell is disconnected, nobody inside hears the request.
```

---

# 10. Ingress Controller Stage

An Ingress object is only a rule.

The Ingress Controller is the running proxy that applies the rule.

```text
Ingress object      = routing instruction
Ingress controller  = real traffic-handling process
```

Examples:

```text
NGINX Ingress Controller
Traefik
HAProxy Ingress
Envoy Gateway
Cloud-specific controllers
```

Flow:

```text
Ingress Controller receives HTTP request
      |
      v
Reads Host header: api.example.com
      |
      v
Reads path: /orders/42
      |
      v
Finds matching Ingress rule
      |
      v
Sends request to backend Service
```

ASCII:

```text
HTTP Request
Host: api.example.com
Path: /orders/42
        |
        v
+---------------------+
| Ingress Controller  |
| rule table          |
+----------+----------+
           |
           v
backend: order-service:80
```

Common failures:

```text
Ingress controller not installed
Wrong ingressClassName
Host mismatch
Path mismatch
Backend service name wrong
Backend service port wrong
Controller cannot watch namespace
```

Debug:

```bash
kubectl get ingress
kubectl describe ingress order-service
kubectl logs -n ingress-nginx deploy/ingress-nginx-controller
kubectl get ingressclass
```

Mental model:

```text
Ingress matching is like an HTTP routing table.
Wrong host/path means the request reaches the proxy but not your service.
```

---

# 11. Service Stage

The Service gives stable access to unstable Pods.

Pods are temporary:

```text
order-service-abc -> 10.244.1.10
order-service-def -> 10.244.2.15
order-service-ghi -> 10.244.3.21
```

After restart:

```text
old Pod gone
new Pod -> new IP
```

The Service hides this instability.

```text
order-service.default.svc.cluster.local
ClusterIP: 10.96.20.50
```

Flow:

```text
Ingress Controller
  |
  | http://order-service:80
  v
Service order-service
  |
  | targetPort 8080
  v
Ready Pod IP:8080
```

ASCII:

```text
+-------------------+
| Service           |
| name: order       |
| port: 80          |
| targetPort: 8080  |
+---------+---------+
          |
          +--> 10.244.1.10:8080
          +--> 10.244.2.15:8080
          +--> 10.244.3.21:8080
```

Important:

```text
Service does not select every Pod.
Service selects Pods whose labels match its selector.
```

Wrong selector means:

```text
Service exists but has no endpoints.
```

Debug:

```bash
kubectl get svc order-service
kubectl describe svc order-service
kubectl get endpoints order-service
kubectl get endpointslice -l kubernetes.io/service-name=order-service
kubectl get pods --show-labels
```

---

# 12. EndpointSlice Stage

EndpointSlice contains the real backend Pod IPs for a Service.

Service selector:

```yaml
selector:
  app: order-service
```

Pod labels:

```yaml
labels:
  app: order-service
```

Kubernetes creates EndpointSlices like:

```text
order-service endpoints:
  10.244.1.10:8080 Ready
  10.244.2.15:8080 Ready
  10.244.3.21:8080 Ready
```

ASCII:

```text
Service selector
app=order-service
      |
      v
Matching Ready Pods
      |
      v
EndpointSlice
+---------------------+
| 10.244.1.10:8080    |
| 10.244.2.15:8080    |
| 10.244.3.21:8080    |
+---------------------+
```

This is where readiness matters.

A Pod can be Running but not Ready.

If not Ready, it should not be added as a serving endpoint.

```text
Running != endpoint-ready
```

Common failures:

```text
Service selector mismatch
Readiness probe failing
Pod labels changed
Wrong targetPort
Pods in different namespace than Service
```

Debug:

```bash
kubectl get endpointslice
kubectl describe endpointslice <name>
kubectl get pods -l app=order-service -o wide
kubectl describe pod <pod-name>
```

Mental model:

```text
EndpointSlice is the current phonebook of real Pod destinations.
No endpoint means the Service has nobody to call.
```

---

# 13. kube-proxy Stage

kube-proxy programs node-level networking rules so Service IPs can route to Pod IPs.

Depending on cluster mode, it may use:

```text
iptables
IPVS
eBPF in some CNIs
```

Mental model:

```text
Service ClusterIP is virtual.
kube-proxy teaches nodes how to translate virtual Service traffic into real Pod traffic.
```

Flow:

```text
Packet destination: 10.96.20.50:80
      |
      v
Node networking rule
      |
      v
Choose endpoint 10.244.2.15:8080
      |
      v
Packet destination rewritten/routed
```

ASCII:

```text
Service VIP
10.96.20.50:80
      |
      v
kube-proxy rules
      |
      +--> 10.244.1.10:8080
      +--> 10.244.2.15:8080
      +--> 10.244.3.21:8080
```

Common failures:

```text
kube-proxy not running
iptables rules corrupted
IPVS sync issue
EndpointSlice not reflected in rules
Node networking problem
```

Debug:

```bash
kubectl get pods -n kube-system -l k8s-app=kube-proxy
kubectl logs -n kube-system -l k8s-app=kube-proxy
kubectl get endpoints order-service
```

On node, advanced debugging may involve:

```bash
iptables-save | grep order-service
ipvsadm -Ln
```

Mental model:

```text
Service is the name and virtual address.
kube-proxy or CNI dataplane makes packets actually move.
```

---

# 14. CNI Stage

CNI provides Pod networking.

It makes this promise possible:

```text
Every Pod gets an IP.
Pods can communicate across nodes.
```

Examples:

```text
Calico
Cilium
Flannel
Weave
Cloud CNI plugins
```

If selected endpoint is on another node, the packet must cross the Pod network.

```text
Node A ingress controller
      |
      | packet to 10.244.2.15
      v
Node B Pod network
      |
      v
order-service Pod
```

ASCII:

```text
Node A                             Node B
+------------------+               +------------------+
| Ingress Pod      |               | Order Pod        |
| 10.244.1.5       |               | 10.244.2.15      |
+--------+---------+               +---------+--------+
         |                                   ^
         | CNI overlay / routing             |
         +-----------------------------------+
```

Common failures:

```text
CNI pod crash
NetworkPolicy blocks traffic
Overlay route broken
Node-to-node firewall issue
MTU mismatch
DNS works but packet cannot reach Pod
```

Debug:

```bash
kubectl get pods -n kube-system
kubectl get networkpolicy
kubectl exec -it <debug-pod> -- curl http://order-service/orders/42
kubectl exec -it <debug-pod> -- nc -vz order-service 80
```

Mental model:

```text
Service decides which Pod should receive traffic.
CNI makes it physically reachable across the cluster network.
```

---

# 15. Pod Network Namespace Stage

A Pod has its own network namespace.

Containers inside the same Pod share:

```text
IP address
localhost
network interfaces
port space
```

For a normal single-container Spring Boot Pod:

```text
Pod IP: 10.244.2.15
Container listens: 0.0.0.0:8080
```

ASCII:

```text
Pod network namespace
+--------------------------------+
| IP: 10.244.2.15                |
|                                |
| Container: order-service       |
|   listens on 0.0.0.0:8080      |
|                                |
| localhost inside Pod           |
+--------------------------------+
```

Important mistake:

If Spring Boot listens only on `127.0.0.1`, external traffic to Pod IP may fail depending on binding behavior.

Correct:

```text
server.address=0.0.0.0
server.port=8080
```

Usually Spring Boot binds to all interfaces by default unless configured otherwise.

Common failures:

```text
App listening on wrong port
App bound to localhost only
Container port mismatch
Sidecar intercept issue
Process not started
Readiness passes wrong endpoint
```

Debug:

```bash
kubectl exec -it <pod> -- printenv
kubectl exec -it <pod> -- sh
kubectl exec -it <pod> -- wget -qO- localhost:8080/actuator/health
kubectl logs <pod>
```

Mental model:

```text
The Pod IP reaches the Pod network namespace.
The Java process must actually listen there.
```

---

# 16. Spring Boot Handler Stage

Now the request finally reaches the Java application.

```text
GET /orders/42
```

Spring MVC maps:

```text
HTTP method + path -> controller method
```

Code:

```java
@GetMapping("/orders/{id}")
public OrderResponse getOrder(@PathVariable String id) {
    return new OrderResponse(id, "PAID", "Request reached order-service");
}
```

Flow:

```text
Tomcat accepts socket
      |
      v
Spring Security filters
      |
      v
Request mapping
      |
      v
Controller method
      |
      v
Service layer
      |
      v
Repository / external dependency
      |
      v
Response serialization
```

ASCII:

```text
Container port 8080
      |
      v
Embedded Tomcat
      |
      v
Filter Chain
      |
      v
DispatcherServlet
      |
      v
@RestController
      |
      v
JSON Response
```

Common failures:

```text
404 path not mapped
401 authentication missing
403 authorization failed
500 application exception
DB connection timeout
Thread pool exhausted
Slow downstream dependency
Serialization error
```

Debug:

```bash
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl exec -it <pod> -- curl localhost:8080/orders/42
kubectl port-forward pod/<pod> 8080:8080
curl localhost:8080/orders/42
```

Mental model:

```text
Once traffic reaches Java, Kubernetes routing is no longer the main suspect.
Now debug application behavior.
```

---

# 17. Response Path

The response usually follows the reverse network path.

```text
Spring Boot
  |
  v
Pod network namespace
  |
  v
CNI / node networking
  |
  v
Ingress controller
  |
  v
Load balancer
  |
  v
Client
```

ASCII:

```text
Request path:
Client -> LB -> Ingress -> Service -> Pod -> App

Response path:
App -> Pod -> Ingress -> LB -> Client
```

But response failures can still happen:

```text
Application takes too long
Ingress timeout reached
Connection reset
Response too large
Client disconnects
mTLS sidecar issue
NetworkPolicy asymmetric behavior
```

Production examples:

```text
App finishes in 90 seconds
Ingress timeout is 60 seconds
Client sees 504 Gateway Timeout
Java logs show success later
```

Mental model:

```text
The request reaching the app is only half the story.
The response must return before timeouts expire.
```

Debug timeout chain:

```text
Client timeout
Load balancer timeout
Ingress proxy timeout
Service mesh timeout
Spring Boot server timeout
Downstream DB/API timeout
```

---

# 18. Full Dry Run: Successful Request

Request:

```bash
curl https://api.example.com/orders/42
```

Dry run:

```text
1. Client resolves api.example.com using DNS.

2. DNS returns external load balancer IP.

3. Client opens TCP connection to port 443.

4. TLS handshake validates certificate for api.example.com.

5. HTTP request is sent:
   Host: api.example.com
   Path: /orders/42

6. Load balancer forwards request to ingress controller.

7. Ingress controller checks routing rules.

8. Rule matches:
   host = api.example.com
   path prefix = /orders

9. Backend selected:
   service = order-service
   port = 80

10. Service maps port 80 to targetPort 8080.

11. EndpointSlice contains ready Pod IPs.

12. kube-proxy/CNI dataplane chooses one endpoint:
   10.244.2.15:8080

13. Packet reaches Pod network namespace.

14. Spring Boot embedded Tomcat receives request.

15. Spring Security and filters run.

16. DispatcherServlet finds OrderController.

17. Controller returns JSON.

18. Response travels back through ingress and load balancer.

19. Client receives HTTP 200.
```

One-line flow:

```text
Domain -> LB -> Ingress Controller -> Service -> EndpointSlice -> Pod IP -> Container Port -> Spring Boot Controller
```

---

# 19. Production Story: 503 From Ingress

Symptom:

```text
HTTP/1.1 503 Service Temporarily Unavailable
```

Common meaning:

```text
Ingress received the request but could not find a healthy backend.
```

Possible causes:

```text
Service has no endpoints
Readiness probe failing
Wrong Service name in Ingress
Wrong Service port in Ingress
Pods not Ready
Pods in CrashLoopBackOff
```

Debug chain:

```bash
kubectl describe ingress order-service
kubectl get svc order-service
kubectl get endpoints order-service
kubectl get pods -l app=order-service
kubectl describe pod <pod>
```

ASCII:

```text
Client
  |
  v
Ingress Controller OK
  |
  v
Backend Service selected
  |
  v
Endpoint list empty
  |
  v
503
```

Mental model:

```text
503 usually means proxy is alive, backend is unavailable.
```

Do not start by changing DNS.

Start by checking Service endpoints.

---

# 20. Production Story: 404 From Ingress

Symptom:

```text
HTTP/1.1 404 Not Found
```

There are two different 404s.

```text
Ingress 404:
  Ingress controller could not match host/path.

Application 404:
  Request reached Spring Boot, but no controller route matched.
```

How to distinguish:

```text
Check ingress logs.
Check app logs.
Port-forward directly to app.
```

Flow:

```bash
curl -v https://api.example.com/orders/42
kubectl logs -n ingress-nginx deploy/ingress-nginx-controller
kubectl logs deploy/order-service
kubectl port-forward svc/order-service 8080:80
curl localhost:8080/orders/42
```

ASCII:

```text
Case A: Ingress 404
Client -> Ingress -> no matching rule -> 404

Case B: App 404
Client -> Ingress -> Service -> Pod -> Spring Boot no mapping -> 404
```

Mental model:

```text
Same HTTP status can come from different layers.
Always identify which layer generated it.
```

---

# 21. Production Story: Timeout

Symptom:

```text
curl: Operation timed out
504 Gateway Timeout
```

Timeout means some layer waited too long.

Possible locations:

```text
Client to load balancer blocked
Load balancer to ingress blocked
Ingress to Service has no route
NetworkPolicy blocks Pod traffic
App accepts but does not respond
DB query is slow
Thread pool exhausted
```

Debug approach:

```text
1. Can you reach the external IP?
2. Does ingress receive logs?
3. Does Service have endpoints?
4. Can a debug Pod curl the Service?
5. Can you curl the Pod IP directly?
6. Can you curl localhost from inside the Pod?
7. Do Java logs show request received?
8. Are DB/downstream calls slow?
```

Commands:

```bash
kubectl run tmp-curl --rm -it --image=curlimages/curl -- sh
curl -v http://order-service.default.svc.cluster.local/orders/42
curl -v http://10.244.2.15:8080/orders/42
```

Mental model:

```text
Timeout means the chain did not break loudly.
It waited silently somewhere.
```

---

# 22. Production Story: Wrong targetPort

Service:

```yaml
ports:
  - port: 80
    targetPort: 8081
```

But Spring Boot listens on:

```text
8080
```

Result:

```text
Service exists
Endpoints exist
Pods Ready maybe true
But traffic fails
```

ASCII:

```text
Ingress -> Service:80
              |
              v
        targetPort:8081
              |
              v
        Pod IP:8081
              |
              v
        nobody listening
```

Debug:

```bash
kubectl describe svc order-service
kubectl get pod <pod> -o yaml | grep containerPort
kubectl exec -it <pod> -- wget -qO- localhost:8080/actuator/health
kubectl exec -it <pod> -- wget -qO- localhost:8081/actuator/health
```

Correct:

```yaml
ports:
  - port: 80
    targetPort: 8080
```

Mental model:

```text
port is the Service-facing port.
targetPort is the container-facing port.
Wrong targetPort sends traffic to the wrong door.
```

---

# 23. Production Story: Readiness Failing

Pod status:

```text
NAME                             READY   STATUS
order-service-7d9c8f8d5b-abc     0/1     Running
```

App is running, but not ready.

Ingress returns:

```text
503
```

Why?

Because Service has no ready endpoints.

Flow:

```text
Spring Boot process alive
      |
      v
Readiness endpoint returns DOWN
      |
      v
Pod not Ready
      |
      v
EndpointSlice excludes Pod
      |
      v
Service has no usable backend
      |
      v
Ingress returns 503
```

Common Spring Boot causes:

```text
DB connection unavailable
Redis unavailable
Kafka health indicator down
Wrong profile
Missing secret
Startup migration still running
```

Debug:

```bash
kubectl describe pod <pod>
kubectl logs <pod>
kubectl exec -it <pod> -- wget -qO- localhost:8080/actuator/health/readiness
```

Mental model:

```text
Readiness is the traffic gate.
Running means process exists.
Ready means safe to receive traffic.
```

---

# 24. Production Story: NetworkPolicy Blocks Traffic

A NetworkPolicy can block traffic even when Service and endpoints are correct.

Symptom:

```text
Service has endpoints
Pod is Ready
Ingress route is correct
But request times out
```

Possible reason:

```text
NetworkPolicy denies ingress from ingress namespace to app namespace.
```

ASCII:

```text
Ingress Controller Pod
      |
      | blocked by NetworkPolicy
      X
Order Service Pod
```

Debug:

```bash
kubectl get networkpolicy -A
kubectl describe networkpolicy -n prod
kubectl run tmp-curl --rm -it --image=curlimages/curl -n prod -- sh
curl http://order-service.prod.svc.cluster.local/orders/42
```

Mental model:

```text
Service discovery can be correct while traffic permission is denied.
```

Service answers:

```text
Where is the backend?
```

NetworkPolicy answers:

```text
Who is allowed to talk to it?
```

---

# 25. Debugging Mindset: Outside To Inside

Use this when users cannot reach your service.

```text
1. DNS
   Does domain resolve to expected IP?

2. TLS
   Is certificate valid for host?

3. External Load Balancer
   Is external IP active and forwarding?

4. Ingress Controller
   Is controller running?
   Does it receive the request?

5. Ingress Rule
   Does host/path match?

6. Service
   Does service exist?
   Is port correct?

7. EndpointSlice
   Are ready Pod IPs present?

8. Pod
   Is Pod Running and Ready?

9. Container
   Is process listening on expected port?

10. Application
   Does Spring Boot route exist?
   Are dependencies healthy?
```

ASCII checklist:

```text
DNS -> LB -> Ingress -> Rule -> Service -> Endpoints -> Pod -> Port -> App
```

Commands:

```bash
dig api.example.com
curl -vk https://api.example.com/orders/42
kubectl get ingress
kubectl describe ingress order-service
kubectl get svc order-service
kubectl get endpoints order-service
kubectl get pods -l app=order-service -o wide
kubectl describe pod <pod>
kubectl logs <pod>
```

Rule:

```text
Do not jump randomly.
Find the first broken link in the chain.
```

---

# 26. Debugging Mindset: Inside To Outside

Use this when you are inside the cluster or want to isolate external routing.

Start from the app and move outward.

```text
1. Inside Pod:
   curl localhost:8080/orders/42

2. Pod IP:
   curl http://10.244.2.15:8080/orders/42

3. Service DNS:
   curl http://order-service/orders/42

4. Service FQDN:
   curl http://order-service.default.svc.cluster.local/orders/42

5. Ingress internal/external:
   curl -H 'Host: api.example.com' http://<ingress-ip>/orders/42

6. Public domain:
   curl https://api.example.com/orders/42
```

ASCII:

```text
localhost -> Pod IP -> Service DNS -> Ingress IP + Host -> Public Domain
```

This isolates the failing layer.

Example:

```text
localhost works
Pod IP works
Service fails
```

Likely suspects:

```text
Service selector
Service targetPort
kube-proxy/dataplane
```

Example:

```text
Service works
Ingress fails
```

Likely suspects:

```text
Ingress host/path
Ingress backend service port
Ingress class
Ingress controller
```

---

# 27. Request Flow With Service Mesh

If a service mesh is installed, request flow gains sidecars or node proxies.

Example with sidecar:

```text
Ingress Gateway
  |
  v
Service
  |
  v
Pod IP
  |
  v
Envoy sidecar
  |
  v
Spring Boot container
```

Inside Pod:

```text
+----------------------------------+
| Pod                              |
|                                  |
| +-------------+  +-------------+ |
| | Envoy Proxy |  | Spring Boot | |
| +------+------+  +------+------+ |
|        |                ^        |
|        +----------------+        |
+----------------------------------+
```

Service mesh may add:

```text
mTLS
Retries
Timeouts
Circuit breaking
Traffic splitting
Authorization policies
Observability
```

New failure types:

```text
mTLS certificate issue
Sidecar not injected
AuthorizationPolicy denies traffic
DestinationRule timeout too low
VirtualService route mismatch
Retry storm
```

Mental model:

```text
Service mesh inserts another intelligent traffic layer between network routing and application code.
```

Debug:

```bash
kubectl get pods -o jsonpath='{.items[*].spec.containers[*].name}'
kubectl logs <pod> -c istio-proxy
kubectl describe authorizationpolicy
kubectl describe virtualservice
```

---

# 28. Observability For Request Flow

To debug request flow quickly, collect signals at every layer.

```text
Layer                 Signal
--------------------------------------------------
DNS                   resolution result
Load Balancer          health checks, 4xx/5xx
Ingress Controller     access logs, upstream status
Service                endpoint count
Pod                    readiness, restart count
Application            logs, traces, metrics
Database               latency, errors, pool usage
```

Spring Boot correlation ID filter:

```java
package com.example.order;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Correlation-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String correlationId = request.getHeader(HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);
        response.setHeader(HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("correlationId");
        }
    }
}
```

Mental model:

```text
Without correlation ID, each layer tells a separate story.
With correlation ID, all layers describe the same request.
```

---

# 29. Golden Signals For Request Flow

For every service, watch:

```text
Traffic
Errors
Latency
Saturation
```

Request-flow version:

```text
Traffic:
  How many requests reach ingress and app?

Errors:
  Are failures generated by ingress, service mesh, or app?

Latency:
  Where is time spent?

Saturation:
  Which resource is overloaded?
```

ASCII:

```text
Client
  |
  | latency A
  v
Ingress
  |
  | latency B
  v
Service / Network
  |
  | latency C
  v
App
  |
  | latency D
  v
DB / External API
```

Useful metrics:

```text
ingress request count by status
ingress upstream latency
pod CPU/memory
container restarts
Spring Boot HTTP server request duration
HikariCP active connections
JVM thread count
DB query latency
```

Mental model:

```text
End-user latency is the sum of many smaller latencies.
Find the layer that spends the time.
```

---

# 30. Common Status Codes And Likely Layers

```text
Status / Error              Likely Layer
----------------------------------------------------------
DNS lookup failed            DNS
TLS certificate error         TLS / Ingress / LB
Connection refused            Port not listening / wrong backend
Connection timeout            Network / firewall / policy / app hang
404                           Ingress route or app route
401                           App auth / gateway auth
403                           AuthZ / Network policy / gateway policy
502                           Proxy could not talk to backend
503                           No healthy backend / no endpoints
504                           Upstream timeout
500                           Application exception
```

Important:

```text
These are clues, not final answers.
```

Always confirm with logs and direct tests.

Example:

```text
503 from ingress:
  Check endpoints.

503 from app:
  Check application logs.
```

Layer ownership matters.

```text
Same status code, different owner.
```

---

# 31. Mini Incident Drill

Incident:

```text
Users report api.example.com/orders/42 returns 503.
```

Step 1: Confirm externally.

```bash
curl -vk https://api.example.com/orders/42
```

Step 2: Check ingress.

```bash
kubectl describe ingress order-service
kubectl logs -n ingress-nginx deploy/ingress-nginx-controller | tail
```

Step 3: Check service.

```bash
kubectl get svc order-service
kubectl describe svc order-service
```

Step 4: Check endpoints.

```bash
kubectl get endpoints order-service
kubectl get endpointslice -l kubernetes.io/service-name=order-service
```

Output:

```text
ENDPOINTS: <none>
```

Step 5: Check Pods.

```bash
kubectl get pods -l app=order-service
```

Output:

```text
order-service-abc 0/1 Running
order-service-def 0/1 Running
order-service-ghi 0/1 Running
```

Step 6: Check readiness.

```bash
kubectl describe pod order-service-abc
kubectl logs order-service-abc
```

Root cause:

```text
Readiness probe fails because DB password Secret was rotated incorrectly.
```

Fix:

```text
Restore correct Secret
Restart rollout
Verify readiness
Verify endpoints
Verify external request
```

Lesson:

```text
503 was not an ingress problem.
503 was caused by no ready application endpoints.
```

---

# 32. Java Production Example: Slow DB Causes 504

Controller:

```java
@GetMapping("/orders/{id}")
public OrderResponse getOrder(@PathVariable String id) {
    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));

    return new OrderResponse(order.id(), order.status(), "OK");
}
```

Problem:

```text
DB query sometimes takes 75 seconds.
Ingress timeout is 60 seconds.
```

User sees:

```text
504 Gateway Timeout
```

Java logs later show:

```text
Request completed successfully in 75000 ms
```

This confuses beginners.

The app eventually returned, but too late for the proxy.

ASCII:

```text
Client waits
  |
  v
Ingress waits max 60s
  |
  v
Spring Boot waits DB 75s
  |
  v
Ingress gives up at 60s -> 504
  |
  v
App finishes at 75s -> nobody listening
```

Fix direction:

```text
Optimize query
Add index
Reduce DB timeout
Add circuit breaker
Return async accepted response if needed
Increase proxy timeout only when justified
```

Mental model:

```text
A timeout is usually a budget mismatch.
Every layer has a patience limit.
```

---

# 33. Java Production Example: Thread Pool Exhaustion

Spring Boot uses request threads to process HTTP requests.

If all request threads are busy, new requests wait.

Symptoms:

```text
Ingress 504
High latency
CPU maybe not high
Thread count high
DB pool exhausted
```

Possible cause:

```java
@GetMapping("/reports/{id}")
public ReportResponse getReport(@PathVariable String id) {
    // Slow blocking call
    String data = externalReportClient.fetchReport(id);
    return new ReportResponse(id, data);
}
```

If external service is slow, Tomcat threads pile up.

ASCII:

```text
Tomcat Threads
+--------+----------------+
| T1     | waiting API    |
| T2     | waiting API    |
| T3     | waiting API    |
| ...    | ...            |
| T200   | waiting API    |
+--------+----------------+

New request -> waits -> timeout
```

Fix direction:

```text
Set downstream timeouts
Use bulkhead
Use circuit breaker
Tune thread pool carefully
Use async/message queue for long work
Expose metrics
```

Mental model:

```text
Request flow can fail inside the app even when Kubernetes routing is perfect.
```

---

# 34. What Kubernetes Does Not Guarantee

Kubernetes does not guarantee:

```text
Correct DNS records
Correct TLS certificates
Correct ingress rules
Correct service selectors
Correct targetPort
Correct application route mappings
Fast database queries
Enough Java request threads
Correct timeout budgets
Correct retry strategy
Zero downtime with bad readiness probes
```

Kubernetes gives primitives.

You design the flow.

Bad request flow on Kubernetes is still bad request flow.

ASCII:

```text
Bad routing config
      |
      v
Kubernetes
      |
      v
Automated bad routing config
```

Important lesson:

```text
Kubernetes can route to healthy endpoints only if you define health, labels, ports, and rules correctly.
```

---

# 35. Beginner Mistakes

```text
Mistake 1:
Thinking Ingress object itself handles traffic.
Correct:
Ingress Controller handles traffic. Ingress object is config.

Mistake 2:
Thinking Service forwards packets as a process.
Correct:
ClusterIP Service is virtual; dataplane rules route traffic.

Mistake 3:
Confusing port and targetPort.
Correct:
port is Service-facing, targetPort is Pod-facing.

Mistake 4:
Ignoring readiness.
Correct:
Only Ready Pods should receive Service traffic.

Mistake 5:
Debugging Java first for TLS errors.
Correct:
TLS fails before request reaches Java.

Mistake 6:
Debugging DNS for 503.
Correct:
503 often means proxy reached but backend unavailable.

Mistake 7:
Assuming 404 always means app route missing.
Correct:
Ingress can also generate 404.

Mistake 8:
Ignoring NetworkPolicy.
Correct:
Service discovery and network permission are different.
```

---

# 36. Interview Questions

## Explain Kubernetes request flow end to end.

A client resolves the domain using DNS, connects to the external load balancer, and the request reaches the ingress controller. The ingress controller matches the HTTP host and path against Ingress rules, selects a backend Service, and sends traffic to the Service port. The Service maps to ready Pod endpoints through EndpointSlices. kube-proxy or the CNI dataplane routes traffic from the virtual Service address to a real Pod IP and target port. The packet reaches the Pod network namespace, the container process receives it, and Spring Boot maps the HTTP path to a controller method. The response travels back through the same chain.

## What is the difference between Ingress and Ingress Controller?

Ingress is a Kubernetes API object containing HTTP routing rules. Ingress Controller is the actual running proxy that watches those rules and handles traffic. Without an ingress controller, Ingress objects do not route traffic by themselves.

## What is the difference between Service port and targetPort?

Service port is the port exposed by the Service inside the cluster. targetPort is the port on the selected Pod/container where traffic is sent. For example, a Service may expose port 80 and forward to targetPort 8080 where Spring Boot listens.

## Why can a Service have no endpoints?

A Service can have no endpoints if its selector does not match any Pods, if matching Pods are not Ready, if Pods are in another namespace, or if labels changed. In that case, traffic to the Service has no backend.

## Why can a Pod be Running but not receive traffic?

Running only means the container process exists. Traffic should be sent only when the Pod is Ready. If the readiness probe fails, Kubernetes removes the Pod from Service endpoints, so it will not receive normal Service traffic.

## How do you debug 503 from ingress?

First confirm the ingress rule and backend service. Then check whether the Service exists and has endpoints. If endpoints are empty, check Pod labels and readiness. Check ingress controller logs to confirm whether the 503 comes from lack of upstream backends.

## How do you distinguish ingress 404 from app 404?

Check ingress logs and application logs. Then port-forward directly to the Service or Pod and call the same path. If direct app call returns 200 but ingress returns 404, the ingress host/path rule is wrong. If direct app call also returns 404, the application route is likely missing or different.

## What role does kube-proxy play?

kube-proxy watches Services and EndpointSlices and programs node networking rules, such as iptables or IPVS, so traffic to a Service virtual IP can be routed to real Pod IPs.

## What role does CNI play?

CNI provides Pod networking. It assigns Pod IPs and enables Pod-to-Pod communication across nodes. After a backend Pod IP is selected, the CNI dataplane makes that Pod reachable.

## What causes 504 Gateway Timeout?

A 504 usually means a proxy waited for an upstream response and timed out. Causes include slow application code, slow database queries, blocked network path, overloaded thread pools, or timeout budget mismatch between ingress, service mesh, application, and downstream services.

---

# 37. Cheat Sheet

```text
Request Flow Core:
Client -> DNS -> Load Balancer -> Ingress Controller -> Service -> EndpointSlice -> Pod IP -> Container Port -> Spring Boot

Ingress:
HTTP host/path routing rule

Ingress Controller:
Actual proxy that handles traffic

Service:
Stable name/IP for unstable Pods

Service port:
Port exposed by Service

targetPort:
Port on Pod/container

EndpointSlice:
Current list of ready backend Pod IPs

Readiness:
Traffic eligibility gate

kube-proxy:
Programs Service routing rules

CNI:
Provides Pod networking

NetworkPolicy:
Controls allowed traffic

Pod IP:
Real backend destination

Container port:
Where application listens
```

Debug commands:

```bash
# External checks
dig api.example.com
curl -vk https://api.example.com/orders/42

# Ingress
kubectl get ingress
kubectl describe ingress order-service
kubectl logs -n ingress-nginx deploy/ingress-nginx-controller

# Service and endpoints
kubectl get svc order-service
kubectl describe svc order-service
kubectl get endpoints order-service
kubectl get endpointslice -l kubernetes.io/service-name=order-service

# Pods
kubectl get pods -l app=order-service -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl logs <pod> --previous

# Internal testing
kubectl run tmp-curl --rm -it --image=curlimages/curl -- sh
curl http://order-service.default.svc.cluster.local/orders/42

# Direct app testing
kubectl port-forward svc/order-service 8080:80
curl localhost:8080/orders/42
```

Status clues:

```text
DNS fail    -> DNS
TLS fail    -> certificate / ingress / LB
404         -> ingress rule or app route
502         -> proxy backend communication problem
503         -> no healthy backend / no endpoints
504         -> upstream timeout
500         -> app error
```

---

# 38. One Picture To Remember

```text
                         USER REQUEST
                              |
                              v
                     api.example.com/orders/42
                              |
                              v
                       +-------------+
                       | DNS         |
                       | name -> IP  |
                       +------+------+ 
                              |
                              v
                       +-------------+
                       | External LB |
                       | cluster door|
                       +------+------+ 
                              |
                              v
                  +-----------------------+
                  | Ingress Controller    |
                  | host/path -> service  |
                  +-----------+-----------+
                              |
                              v
                       +-------------+
                       | Service     |
                       | stable VIP  |
                       +------+------+ 
                              |
                              v
                       +-------------+
                       | Endpoints   |
                       | ready pods  |
                       +------+------+ 
                              |
                              v
                  +-----------------------+
                  | kube-proxy / CNI      |
                  | VIP -> Pod IP route   |
                  +-----------+-----------+
                              |
                              v
                       +-------------+
                       | Pod         |
                       | 10.x.x.x    |
                       +------+------+ 
                              |
                              v
                       +-------------+
                       | Container   |
                       | port 8080   |
                       +------+------+ 
                              |
                              v
                       +-------------+
                       | Spring Boot |
                       | Controller  |
                       +------+------+ 
                              |
                              v
                         HTTP RESPONSE
```

Rule:

```text
A request does not magically reach a Pod.
It is handed from layer to layer.
Debug the handoffs.
```

---

# 39. Final Production Checklist

```text
[ ] DNS points to the correct external entry.
[ ] TLS certificate matches the host.
[ ] External load balancer is healthy.
[ ] Ingress controller pods are Running and Ready.
[ ] Ingress class is correct.
[ ] Ingress host and path match the request.
[ ] Ingress backend service name is correct.
[ ] Ingress backend service port is correct.
[ ] Service selector matches Pod labels.
[ ] Service port and targetPort are correct.
[ ] EndpointSlice contains ready Pod IPs.
[ ] Pods are Running and Ready.
[ ] Container listens on the targetPort.
[ ] Spring Boot route exists.
[ ] App dependencies are healthy enough for readiness.
[ ] NetworkPolicy allows ingress-to-app traffic.
[ ] Timeouts are aligned across LB, ingress, mesh, app, and downstreams.
[ ] Logs include correlation ID.
[ ] Metrics separate ingress errors from app errors.
```

---

# 40. Final Memory Hook

Do not memorize request flow as objects.

Remember it as a delivery chain.

```text
Address -> Entrance -> Reception -> Department -> Employee -> Work -> Reply
```

Kubernetes version:

```text
DNS -> Load Balancer -> Ingress -> Service -> EndpointSlice -> Pod -> Container -> App -> Response
```

Final sentence:

```text
Kubernetes request flow is not one route. It is a chain of decisions, and production debugging means finding the first wrong decision in that chain.
```
