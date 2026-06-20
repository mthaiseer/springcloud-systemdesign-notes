# 013_Service_ClusterIP_NodePort_LoadBalancer.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Kubernetes Services Exist

Pods are temporary workers.

A Pod can be created, killed, rescheduled, replaced, or moved to another node.

That means Pod IPs are not stable.

```text
Morning:
order-pod-a = 10.244.1.12
order-pod-b = 10.244.2.18

After rollout:
order-pod-a gone
order-pod-c = 10.244.3.31
```

If another service calls Pod IP directly, the system becomes fragile.

```text
payment-service
      |
      | http://10.244.1.12:8080
      v
order-pod-a
```

This breaks when the Pod is replaced.

Kubernetes Service solves this.

A Service gives:

```text
Stable name
Stable virtual IP
Load balancing to matching ready Pods
Loose coupling from Pod IP changes
```

Mental model:

```text
Pods are hotel guests.
Service is hotel reception.

Guests may change rooms.
Reception number stays stable.
```

ASCII:

```text
Client Pod
   |
   | http://order-service:8080
   v
+-------------------------+
| Service                 |
| stable identity         |
+-----------+-------------+
            |
            +--> Pod A 10.244.1.12
            +--> Pod B 10.244.2.18
            +--> Pod C 10.244.3.31
```

Do not memorize Service types first.

Understand the production problem:

```text
How do I reach changing Pods through one stable address?
```

---

# 2. The Core Service Mental Model

A Service is not a process running inside a container.

It is an API object plus network rules created by Kubernetes components.

```text
Service YAML
    |
    v
API Server stores Service object
    |
    v
EndpointSlice controller finds matching Pods
    |
    v
kube-proxy / CNI programs node networking rules
    |
    v
Traffic reaches Pods
```

One picture:

```text
Service = stable front door for dynamic backend Pods
```

Objects involved:

```text
Deployment creates Pods
Service selects Pods using labels
EndpointSlice stores selected Pod IPs
kube-proxy routes traffic to those Pod IPs
CoreDNS resolves service name
```

Diagram:

```text
Deployment
   |
   v
Pods with label app=order
   |
   | selected by
   v
Service selector app=order
   |
   v
EndpointSlice: 10.244.1.10, 10.244.2.20
   |
   v
kube-proxy rules on every node
```

Important:

```text
Service does not magically know Pods.
Service uses labels.
```

Wrong label means Service has no backends.

```text
Service exists
DNS resolves
ClusterIP exists
But endpoints are empty
```

This is one of the most common Kubernetes networking bugs.

---

# 3. Real World Analogy: Restaurant Reception

Imagine a restaurant with many chefs.

Customers should not know which chef is working today.

Wrong model:

```text
Customer calls Chef-1 directly.
Chef-1 is sick.
Customer fails.
```

Correct model:

```text
Customer calls reception.
Reception sends request to any available chef.
```

Kubernetes:

```text
Customer       = client Pod
Reception      = Service
Chefs          = backend Pods
Chef availability = readiness
Chef names     = Pod IPs
```

Diagram:

```text
Customer
   |
   | call restaurant number
   v
Reception
   |
   +--> Chef A available
   +--> Chef B available
   +--> Chef C busy/unavailable
```

Kubernetes equivalent:

```text
payment-service
   |
   | http://order-service
   v
Service order-service
   |
   +--> order-pod-a Ready
   +--> order-pod-b Ready
   +--> order-pod-c NotReady X
```

The key lesson:

```text
Clients should depend on stable service identity, not unstable worker identity.
```

---

# 4. Service Type Overview Without Memorization

There are several Service types because traffic can come from different places.

```text
Inside cluster only        -> ClusterIP
Outside through node port  -> NodePort
Outside through cloud LB   -> LoadBalancer
DNS name to external host  -> ExternalName
No virtual IP              -> Headless Service
```

Do not memorize this table blindly.

Ask this question:

```text
Who is calling this app?
```

```text
Another Pod inside cluster?
Use ClusterIP.

A developer/testing client hitting node IP?
Use NodePort.

Real internet users in cloud production?
Use LoadBalancer or Ingress.

Stateful pods needing direct identity?
Use Headless Service.
```

ASCII decision model:

```text
Need access?
   |
   +-- only inside cluster ------------> ClusterIP
   |
   +-- outside through every node ------> NodePort
   |
   +-- public/private cloud endpoint ---> LoadBalancer
   |
   +-- direct Pod DNS identities -------> Headless
```

This chapter focuses on:

```text
ClusterIP
NodePort
LoadBalancer
```

Because these are the core traffic exposure layers.

---

# 5. ClusterIP Mental Model

ClusterIP is the default Service type.

It exposes Pods using a stable virtual IP reachable only inside the cluster.

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
    - port: 80
      targetPort: 8080
```

Meaning:

```text
Inside the cluster, clients can call:
http://order-service:80

Kubernetes forwards that to matching Pods on port 8080.
```

Diagram:

```text
payment-pod
   |
   | http://order-service:80
   v
ClusterIP Service 10.96.10.20:80
   |
   +--> order-pod-a 10.244.1.11:8080
   +--> order-pod-b 10.244.2.22:8080
```

Mental model:

```text
ClusterIP = internal stable phone number
```

It is not normally reachable from your laptop directly.

```text
Laptop outside cluster
   |
   | cannot directly reach 10.96.x.x
   v
ClusterIP blocked/not routable externally
```

ClusterIP is perfect for microservice-to-microservice communication.

Example:

```text
payment-service -> order-service
order-service   -> inventory-service
inventory       -> pricing-service
```

Each talks using stable service DNS names.

---

# 6. ClusterIP Dry Run

You deploy three Order Service Pods:

```text
order-pod-a app=order-service 10.244.1.11
order-pod-b app=order-service 10.244.2.22
order-pod-c app=order-service 10.244.3.33
```

Then create Service:

```yaml
selector:
  app: order-service
ports:
  - port: 80
    targetPort: 8080
```

Dry run:

```text
1. API Server stores Service object.

2. EndpointSlice controller watches Service selector.

3. It finds Pods with label app=order-service.

4. It checks ready addresses.

5. It creates/updates EndpointSlice objects.

6. CoreDNS can resolve order-service to ClusterIP.

7. kube-proxy programs rules on nodes.

8. Client calls order-service:80.

9. DNS resolves to ClusterIP.

10. Node networking rule redirects traffic to one ready Pod IP:8080.
```

ASCII:

```text
Client
  |
  | DNS: order-service -> 10.96.10.20
  v
10.96.10.20:80
  |
  | kube-proxy rule
  v
10.244.2.22:8080
```

Notice:

```text
The client never needs to know Pod IPs.
```

---

# 7. Port, TargetPort, NodePort: The Confusing Trio

Many beginners confuse these three.

```text
port       = Service port
 targetPort = container/Pod port
nodePort   = port opened on every Node
```

ClusterIP example:

```yaml
ports:
  - port: 80
    targetPort: 8080
```

Meaning:

```text
Client calls Service on 80.
Pod receives traffic on 8080.
```

Diagram:

```text
Client
  |
  | http://order-service:80
  v
Service port 80
  |
  v
Pod targetPort 8080
```

NodePort example:

```yaml
ports:
  - port: 80
    targetPort: 8080
    nodePort: 30080
```

Meaning:

```text
External client calls NodeIP:30080.
Service receives on port 80 internally.
Pod receives on 8080.
```

Diagram:

```text
External Client
  |
  | http://node-ip:30080
  v
NodePort 30080
  |
  v
Service port 80
  |
  v
Pod targetPort 8080
```

Memory hook:

```text
nodePort   = outside node door
port       = service door
targetPort = pod door
```

---

# 8. NodePort Mental Model

NodePort exposes a Service on a port across all cluster nodes.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: order-nodeport
spec:
  type: NodePort
  selector:
    app: order-service
  ports:
    - port: 80
      targetPort: 8080
      nodePort: 30080
```

Now traffic can enter through:

```text
http://node-1-ip:30080
http://node-2-ip:30080
http://node-3-ip:30080
```

Even if the Pod is not on that node, Kubernetes can forward to the right Pod.

Diagram:

```text
External Client
   |
   | node-2-ip:30080
   v
Node-2
   |
   | kube-proxy rule
   v
Service
   |
   +--> Pod on Node-1
   +--> Pod on Node-3
```

Mental model:

```text
NodePort = open the same door number on every worker node
```

But NodePort is usually not the final production internet exposure model.

Why?

```text
You need to know node IPs.
Node IPs may change.
Firewall rules are harder.
TLS/routing is not elegant.
Port range is limited.
```

NodePort is useful for:

```text
Testing
Bare-metal clusters
Building block for LoadBalancer
Simple internal exposure
```

---

# 9. NodePort Dry Run

Initial state:

```text
Nodes:
node-1 = 192.168.1.11
node-2 = 192.168.1.12
node-3 = 192.168.1.13

Pods:
order-pod-a on node-1
order-pod-b on node-3
```

Service:

```text
type: NodePort
nodePort: 30080
port: 80
targetPort: 8080
```

Dry run:

```text
1. User calls http://192.168.1.12:30080.

2. Request reaches node-2.

3. node-2 may not have an order Pod.

4. kube-proxy rule on node-2 sees NodePort 30080.

5. Rule maps traffic to Service backends.

6. Traffic is forwarded to order-pod-a or order-pod-b.

7. Response returns to client.
```

ASCII:

```text
Client
  |
  v
node-2:30080
  |
  | no local order Pod required
  v
kube-proxy rule
  |
  +--> node-1/order-pod-a:8080
  +--> node-3/order-pod-b:8080
```

This is why NodePort works on every node.

The node is not necessarily hosting the selected Pod.

It is acting as an entry point into the Service routing rules.

---

# 10. LoadBalancer Mental Model

LoadBalancer exposes a Service through an external load balancer provided by cloud infrastructure.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: order-lb
spec:
  type: LoadBalancer
  selector:
    app: order-service
  ports:
    - port: 80
      targetPort: 8080
```

In cloud environments, Kubernetes asks the cloud provider to create a load balancer.

```text
AWS ELB/NLB
GCP Load Balancer
Azure Load Balancer
```

Mental model:

```text
LoadBalancer = cloud-managed public/private entrance in front of NodePort/Service
```

Diagram:

```text
Internet Client
   |
   v
Cloud Load Balancer
   |
   +--> node-1:nodePort
   +--> node-2:nodePort
   +--> node-3:nodePort
          |
          v
      Service routing
          |
          v
      Ready Pods
```

Important:

```text
LoadBalancer Service usually creates NodePort internally.
```

So the layered view is:

```text
LoadBalancer
    -> NodePort
        -> ClusterIP Service routing
            -> Pod targetPort
```

Production use:

```text
Expose one TCP/UDP service directly.
```

For many HTTP routes/domains, Ingress or Gateway API is often better.

---

# 11. LoadBalancer Dry Run

You apply:

```yaml
kind: Service
spec:
  type: LoadBalancer
```

Dry run:

```text
1. API Server stores Service object.

2. Cloud Controller Manager sees LoadBalancer Service.

3. It calls cloud provider API.

4. Cloud provider creates external load balancer.

5. Kubernetes updates Service status with external IP/hostname.

6. Load balancer sends traffic to cluster nodes.

7. NodePort receives traffic.

8. kube-proxy forwards to ready backend Pod.
```

ASCII:

```text
kubectl apply service.yaml
        |
        v
API Server
        |
        v
Cloud Controller Manager
        |
        v
Cloud Provider API
        |
        v
External LB created
        |
        v
Service status gets EXTERNAL-IP
```

Traffic path:

```text
User Browser
   |
   v
external-lb.example.com:80
   |
   v
node-2:31xxx
   |
   v
order-pod-b:8080
```

Debug point:

```text
If EXTERNAL-IP stays <pending>, Kubernetes may not have a working cloud load balancer integration.
```

Common in local clusters like kind/minikube unless special support is added.

---

# 12. Service And EndpointSlice

Old Kubernetes used Endpoints objects heavily.

Modern Kubernetes uses EndpointSlice for scalable endpoint tracking.

Service:

```text
Stable frontend
```

EndpointSlice:

```text
Current backend Pod addresses
```

Diagram:

```text
Service order-service
selector app=order-service
        |
        v
EndpointSlice
  - 10.244.1.11:8080 Ready
  - 10.244.2.22:8080 Ready
  - 10.244.3.33:8080 NotReady
```

When Pods change:

```text
Pod added     -> EndpointSlice updated
Pod deleted   -> EndpointSlice updated
Pod NotReady  -> removed from ready endpoints
```

Traffic should go only to ready endpoints.

This is why readiness probes matter.

```text
Bad readiness = bad traffic routing
```

Debug commands:

```bash
kubectl get endpoints order-service
kubectl get endpointslice -l kubernetes.io/service-name=order-service
kubectl describe svc order-service
```

Mental model:

```text
Service is the name.
EndpointSlice is the live address book.
```

---

# 13. kube-proxy Mental Model

kube-proxy runs on nodes and programs networking rules so Service traffic reaches Pods.

It does not usually proxy every packet in user-space like a normal reverse proxy.

It programs kernel/network rules.

Modes include:

```text
iptables mode
IPVS mode
```

Simplified view:

```text
Service IP:Port
     |
     v
kube-proxy rule
     |
     +--> Pod A
     +--> Pod B
     +--> Pod C
```

Node view:

```text
Node
+--------------------------------+
| kube-proxy                     |
|  watches Services              |
|  watches EndpointSlices        |
|  writes iptables/IPVS rules    |
+--------------------------------+
```

Traffic path:

```text
packet to ClusterIP
       |
       v
kernel rule catches packet
       |
       v
DNAT to backend Pod IP
```

Mental model:

```text
kube-proxy = rule programmer, not always packet middleman
```

If kube-proxy is broken:

```text
DNS may work
Service object may exist
Pods may be ready
But traffic to ClusterIP fails
```

---

# 14. DNS And Service Names

Normally clients do not call ClusterIP directly.

They call DNS names.

```text
http://order-service
http://order-service.default.svc.cluster.local
```

CoreDNS resolves Service names.

```text
order-service.default.svc.cluster.local
        |
        v
ClusterIP 10.96.10.20
```

Inside same namespace:

```text
http://order-service
```

From another namespace:

```text
http://order-service.default
```

Fully qualified:

```text
http://order-service.default.svc.cluster.local
```

Diagram:

```text
payment-pod
  |
  | DNS query: order-service
  v
CoreDNS
  |
  | returns 10.96.10.20
  v
payment-pod sends HTTP to ClusterIP
```

Debug:

```bash
kubectl run dns-test --image=busybox:1.36 -it --rm -- sh
nslookup order-service
wget -qO- http://order-service
```

Mental model:

```text
DNS finds the Service.
Service routing finds the Pod.
```

---

# 15. Spring Boot Example: Order Service

Simple Spring Boot controller:

```java
package com.example.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @GetMapping("/orders/health")
    public String health() {
        return "order-service OK";
    }

    @GetMapping("/orders/demo")
    public String demo() {
        return "Order created by pod";
    }
}
```

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
          image: order-service:1.0.0
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /orders/health
              port: 8080
            initialDelaySeconds: 5
            periodSeconds: 10
```

ClusterIP Service:

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
    - port: 80
      targetPort: 8080
```

Now internal clients call:

```text
http://order-service/orders/demo
```

They do not care which Pod handles the request.

---

# 16. Spring Boot Client Calling ClusterIP

Payment Service can call Order Service by DNS name.

```java
package com.example.payment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class PaymentController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/pay/demo")
    public String pay() {
        String response = restTemplate.getForObject(
            "http://order-service/orders/demo",
            String.class
        );
        return "Payment completed after calling: " + response;
    }
}
```

Mental model:

```text
Payment Service does not know Pod IP.
Payment Service knows business name: order-service.
```

Flow:

```text
payment-pod
   |
   | http://order-service/orders/demo
   v
CoreDNS resolves order-service
   |
   v
ClusterIP
   |
   v
One ready order Pod
```

In production, use timeouts.

```java
// In real systems prefer WebClient/RestClient with timeout, retry, circuit breaker.
```

Kubernetes Service solves discovery.

It does not solve:

```text
Timeouts
Retries
Circuit breaking
Idempotency
Slow downstreams
```

Those remain application responsibilities.

---

# 17. NodePort Spring Boot Test YAML

For local testing, expose Order Service using NodePort.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: order-nodeport
spec:
  type: NodePort
  selector:
    app: order-service
  ports:
    - port: 80
      targetPort: 8080
      nodePort: 30080
```

Call:

```bash
curl http://<node-ip>:30080/orders/demo
```

Minikube example:

```bash
minikube service order-nodeport --url
```

Mental model:

```text
Your laptop -> NodeIP:30080 -> Service -> Pod:8080
```

ASCII:

```text
Laptop
  |
  | curl node-ip:30080
  v
NodePort on node
  |
  v
Service selection
  |
  v
Ready order Pod
```

Production warning:

```text
Do not expose many services using random NodePorts as your main architecture.
Use Ingress/Gateway/API Gateway/LoadBalancer depending on need.
```

NodePort is a useful learning and infrastructure building block.

---

# 18. LoadBalancer Spring Boot YAML

For cloud production direct exposure:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: order-public
spec:
  type: LoadBalancer
  selector:
    app: order-service
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
```

Check:

```bash
kubectl get svc order-public
```

Example output:

```text
NAME           TYPE           CLUSTER-IP      EXTERNAL-IP       PORT(S)
order-public   LoadBalancer   10.96.10.20     35.201.10.50      80:31234/TCP
```

Call:

```bash
curl http://35.201.10.50/orders/demo
```

Flow:

```text
Internet
  |
  v
Cloud Load Balancer :80
  |
  v
NodePort :31234
  |
  v
Service
  |
  v
Pod :8080
```

Production considerations:

```text
Who terminates TLS?
Is the LB public or private?
Do you need HTTP routing?
Do you need WAF?
Do you need sticky sessions?
Do you need source IP preservation?
```

For multiple HTTP services, direct LoadBalancer per service can become expensive and messy.

Ingress or Gateway API is often better.

---

# 19. ExternalTrafficPolicy Mental Model

For NodePort and LoadBalancer, external traffic can be handled in two common ways.

```text
externalTrafficPolicy: Cluster
externalTrafficPolicy: Local
```

Cluster mode:

```text
Traffic can enter any node and be forwarded to any backend Pod.
```

```text
Client
  |
  v
Node-1
  |
  +--> Pod on Node-2
```

Local mode:

```text
Traffic entering a node is sent only to Pods local to that node.
```

```text
Client
  |
  v
Node-1
  |
  +--> Pod on Node-1 only
```

Why use Local?

```text
Preserve real client source IP in many setups.
Avoid extra cross-node hop.
```

Risk:

```text
If a node has no local backend Pod, traffic to that node may fail or be dropped.
```

YAML:

```yaml
spec:
  type: LoadBalancer
  externalTrafficPolicy: Local
```

Mental model:

```text
Cluster = flexible routing
Local   = local-only routing with better source IP behavior
```

Use Local carefully with proper Pod spreading.

---

# 20. Session Affinity

By default, Service load balancing does not guarantee the same client always goes to the same Pod.

```text
Request 1 -> Pod A
Request 2 -> Pod B
Request 3 -> Pod C
```

For stateless services, this is good.

But some legacy apps expect sticky behavior.

Kubernetes supports basic client IP affinity:

```yaml
spec:
  sessionAffinity: ClientIP
```

Mental model:

```text
Same client IP should keep going to same backend Pod for a period.
```

Warning:

```text
Do not use stickiness to hide bad stateful application design.
```

Better architecture:

```text
App Pods are stateless
Session stored in Redis/database/token
Any Pod can serve any request
```

Diagram:

```text
Good cloud-native model:

Client
  |
  +--> Pod A
  +--> Pod B
  +--> Pod C
        |
        v
     Redis/DB shared state
```

Sticky sessions are sometimes necessary, but they should not be your default design.

---

# 21. Headless Service Quick Contrast

This chapter is about ClusterIP, NodePort, LoadBalancer.

But you should know the contrast.

Normal Service:

```text
One stable virtual IP
Load balances to Pods
```

Headless Service:

```text
No ClusterIP
DNS can return individual Pod IPs
```

YAML:

```yaml
spec:
  clusterIP: None
```

Used for:

```text
StatefulSet
Databases
Kafka-like systems
Direct Pod identity
```

Normal Service:

```text
order-service -> one virtual IP -> any order Pod
```

Headless:

```text
mysql-0.mysql -> specific Pod
mysql-1.mysql -> specific Pod
```

Mental model:

```text
ClusterIP Service = reception desk
Headless Service  = directory of exact room numbers
```

Do not use Headless Service for normal stateless microservice traffic unless you need direct backend discovery.

---

# 22. Production Story: Service Has No Endpoints

Symptoms:

```text
curl http://order-service fails
DNS resolves
Service exists
Pods are Running
```

Check:

```bash
kubectl get svc order-service
kubectl get endpoints order-service
```

Output:

```text
ENDPOINTS: <none>
```

Most common cause:

```text
Service selector does not match Pod labels.
```

Example bug:

```yaml
# Pod label
app: order

# Service selector
app: order-service
```

Debug:

```bash
kubectl get pods --show-labels
kubectl describe svc order-service
kubectl get endpointslice -l kubernetes.io/service-name=order-service
```

Fix:

```yaml
selector:
  app: order
```

Mental model:

```text
Service does not point to Pods manually.
It selects them by label.
Wrong label = invisible backends.
```

---

# 23. Production Story: Pod Running But Not Receiving Traffic

Symptoms:

```text
Pod status: Running
Service endpoints do not include Pod
```

Possible cause:

```text
Readiness probe failing
```

Kubernetes only routes Service traffic to ready Pods.

Debug:

```bash
kubectl get pods
kubectl describe pod order-service-abc
kubectl logs order-service-abc
kubectl get endpoints order-service
```

Events may show:

```text
Readiness probe failed: HTTP probe failed with statuscode: 503
```

Spring Boot causes:

```text
DB connection failing
Actuator readiness disabled
Wrong health endpoint
App startup slow
External dependency required during readiness
```

Mental model:

```text
Running = process exists
Ready   = safe to receive traffic
```

Fix options:

```text
Correct readiness path
Increase initialDelaySeconds
Separate liveness and readiness
Do not make readiness depend on optional dependencies
```

---

# 24. Production Story: NodePort Works On One Node But Not Another

Symptoms:

```text
node-1:30080 works
node-2:30080 fails
```

Possible causes:

```text
Firewall blocks node-2 port
Node security group mismatch
kube-proxy broken on node-2
externalTrafficPolicy: Local and no local Pod on node-2
Node not healthy
CNI issue
```

Debug:

```bash
kubectl get nodes -o wide
kubectl get pods -o wide
kubectl get svc order-nodeport -o yaml
kubectl -n kube-system get pods -l k8s-app=kube-proxy -o wide
```

Check externalTrafficPolicy:

```bash
kubectl get svc order-nodeport -o jsonpath='{.spec.externalTrafficPolicy}'
```

Mental model:

```text
NodePort opens the same door on every node,
but firewall, node health, and local policy still matter.
```

If using Local mode:

```text
Only nodes with local ready Pods should receive traffic.
```

Use topology spread or DaemonSet-like placement if every node must serve local traffic.

---

# 25. Production Story: LoadBalancer External IP Pending

Symptoms:

```text
kubectl get svc
order-public LoadBalancer 10.96.10.20 <pending>
```

Meaning:

```text
Kubernetes Service exists,
but no external cloud load balancer has been provisioned.
```

Common causes:

```text
Running on local cluster without LB support
Cloud controller manager missing
Cloud permissions missing
Quota exceeded
Invalid subnet/security group annotations
Unsupported Service configuration
```

Debug:

```bash
kubectl describe svc order-public
kubectl get events --sort-by=.metadata.creationTimestamp
kubectl -n kube-system get pods
```

Local cluster solutions:

```text
minikube tunnel
MetalLB for bare metal
kind extra port mappings
Ingress controller with port mapping
```

Mental model:

```text
LoadBalancer Service is a request to infrastructure.
If no infrastructure controller answers, EXTERNAL-IP stays pending.
```

---

# 26. Production Story: Traffic Reaches Wrong App

Symptoms:

```text
Calling order-service returns inventory response
```

Likely cause:

```text
Selector too broad or labels reused incorrectly.
```

Bad labels:

```yaml
# order pod
app: backend

# inventory pod
app: backend

# order service selector
app: backend
```

The Service selects both order and inventory Pods.

Diagram:

```text
order-service selector app=backend
        |
        +--> order-pod
        +--> inventory-pod X
```

Better labels:

```yaml
app.kubernetes.io/name: order-service
app.kubernetes.io/component: backend
app.kubernetes.io/part-of: ecommerce
```

Service selector:

```yaml
selector:
  app.kubernetes.io/name: order-service
```

Mental model:

```text
Labels are routing identity.
Bad identity causes bad traffic.
```

---

# 27. Debugging Mindset: Service Traffic Chain

When Service traffic fails, do not guess randomly.

Follow the chain:

```text
1. Is client using correct DNS/name/port?
2. Does DNS resolve?
3. Does Service exist?
4. Is Service selector correct?
5. Are EndpointSlices populated?
6. Are Pods Ready?
7. Is targetPort correct?
8. Is app listening on that port?
9. Is kube-proxy healthy?
10. Is CNI routing working?
11. Are NetworkPolicies blocking traffic?
12. Are cloud firewall/security groups blocking external traffic?
```

Commands:

```bash
kubectl get svc
kubectl describe svc order-service
kubectl get endpoints order-service
kubectl get endpointslice -l kubernetes.io/service-name=order-service
kubectl get pods --show-labels -o wide
kubectl describe pod <pod>
kubectl logs <pod>
```

Inside-cluster test:

```bash
kubectl run net-debug --image=busybox:1.36 -it --rm -- sh
nslookup order-service
wget -qO- http://order-service/orders/demo
```

Port-forward fallback:

```bash
kubectl port-forward svc/order-service 8080:80
curl http://localhost:8080/orders/demo
```

Mental model:

```text
Debug from name -> Service -> endpoints -> Pod -> app.
```

---

# 28. targetPort Production Bug

A very common bug:

```yaml
ports:
  - port: 80
    targetPort: 80
```

But Spring Boot listens on 8080.

Result:

```text
Service forwards to Pod IP:80
Nothing is listening there
Connection refused / timeout
```

Correct:

```yaml
ports:
  - port: 80
    targetPort: 8080
```

Debug:

```bash
kubectl describe svc order-service
kubectl get pod order-pod -o yaml | grep containerPort -A2
kubectl exec -it order-pod -- sh
netstat -tulnp
```

Container check:

```bash
curl localhost:8080/orders/health
curl localhost:80/orders/health
```

Mental model:

```text
Service port is what clients call.
targetPort is where the container actually listens.
```

If these do not match reality, Service routing fails even though Kubernetes objects look healthy.

---

# 29. Service vs Ingress vs Gateway

Service exposes Pods at L4 style: TCP/UDP service abstraction.

Ingress/Gateway handles HTTP routing at L7.

Service:

```text
order-service:80 -> Pods
```

Ingress:

```text
api.example.com/orders -> order-service
api.example.com/payments -> payment-service
```

Diagram:

```text
Internet
  |
  v
Ingress / Gateway
  |
  +--> /orders   -> order-service ClusterIP
  +--> /payments -> payment-service ClusterIP
  +--> /users    -> user-service ClusterIP
```

Mental model:

```text
Service = stable backend identity
Ingress/Gateway = HTTP front-door routing
```

In production:

```text
Most internal services use ClusterIP.
One or few edge components use LoadBalancer.
HTTP routing is done by Ingress/Gateway/API Gateway.
```

Avoid:

```text
Creating public LoadBalancer for every microservice without reason.
```

That increases cost, attack surface, and operational complexity.

---

# 30. Real Production Architecture

Typical microservice setup:

```text
Internet
   |
   v
Cloud Load Balancer
   |
   v
Ingress Controller / API Gateway
   |
   +--> order-service ClusterIP
   +--> payment-service ClusterIP
   +--> inventory-service ClusterIP
              |
              v
            Pods
```

Internal calls:

```text
payment-service -> order-service
order-service   -> inventory-service
```

All via ClusterIP DNS.

Production pattern:

```text
Public edge = LoadBalancer
HTTP routing = Ingress/Gateway
Internal discovery = ClusterIP Service
Pod replacement = Deployment/ReplicaSet
Readiness = endpoint safety
```

One picture:

```text
Users
  |
  v
External LB
  |
  v
Ingress Controller
  |
  v
ClusterIP Services
  |
  v
Ready Pods
```

This is the clean mental model.

---

# 31. Interview Questions

## Why do we need Kubernetes Service?

Pods are temporary and their IPs change. A Service provides a stable virtual IP and DNS name, and load balances traffic to matching ready Pods using labels and EndpointSlices.

## What is ClusterIP?

ClusterIP is the default Service type. It exposes the Service on an internal virtual IP reachable inside the cluster. It is commonly used for microservice-to-microservice communication.

## What is NodePort?

NodePort exposes a Service on the same port across all nodes. External clients can call NodeIP:nodePort, and Kubernetes forwards traffic to ready backend Pods.

## What is LoadBalancer?

LoadBalancer asks the cloud provider to create an external load balancer for the Service. Traffic enters through the cloud load balancer, reaches node ports, and then gets routed to backend Pods.

## Difference between port, targetPort, and nodePort?

port is the Service port clients use. targetPort is the Pod/container port where traffic is forwarded. nodePort is the external port opened on every node for NodePort or LoadBalancer Services.

## How does Service know which Pods to send traffic to?

It uses label selectors. Matching ready Pods are tracked through EndpointSlices. kube-proxy or the CNI dataplane uses those endpoints to route traffic.

## What happens if Service selector is wrong?

The Service may have no endpoints or wrong endpoints. DNS and ClusterIP may still exist, but traffic will fail or route to the wrong Pods.

## Why is readiness important for Services?

Services should route only to ready Pods. A Pod can be Running but not Ready. Readiness protects users from traffic reaching apps that are not prepared to serve requests.

## Why might LoadBalancer external IP stay pending?

The cluster may not have cloud load balancer integration, the cloud controller may be missing, permissions may be wrong, quota may be exceeded, or the cluster may be local/bare-metal without a load balancer implementation.

## When should we use Ingress instead of LoadBalancer Service?

Use Ingress or Gateway when you need HTTP routing, many domains, path-based routing, TLS termination, or centralized edge control. Use LoadBalancer for direct L4 exposure or as the front door for an ingress controller.

---

# 32. Cheat Sheet

```text
Service                 = stable access to dynamic Pods
ClusterIP               = internal-only Service IP
NodePort                = exposes Service on every Node IP and fixed port
LoadBalancer            = cloud external LB in front of Service
port                    = Service port
targetPort              = Pod/container port
nodePort                = Node external port
selector                = labels used to find Pods
EndpointSlice           = live backend Pod address list
kube-proxy              = programs Service routing rules
CoreDNS                 = resolves Service names
readiness               = decides if Pod receives Service traffic
externalTrafficPolicy   = external routing behavior: Cluster or Local
sessionAffinity         = sticky-ish client IP routing
```

Debug commands:

```bash
kubectl get svc
kubectl describe svc <service>
kubectl get endpoints <service>
kubectl get endpointslice -l kubernetes.io/service-name=<service>
kubectl get pods --show-labels -o wide
kubectl describe pod <pod>
kubectl logs <pod>
```

Port meaning:

```text
External client -> nodePort -> service port -> targetPort -> container
Internal client -> service port -> targetPort -> container
```

---

# 33. One Picture To Remember

```text
                      CLIENT
                        |
                        | DNS: order-service
                        v
                 +--------------+
                 | CoreDNS      |
                 | name -> VIP  |
                 +------+-------+
                        |
                        v
              +-------------------+
              | Service           |
              | ClusterIP:80      |
              | selector app=order|
              +---------+---------+
                        |
                        v
              +-------------------+
              | EndpointSlice     |
              | ready Pod IPs     |
              +---------+---------+
                        |
                        v
              +-------------------+
              | kube-proxy / CNI  |
              | routing rules     |
              +---------+---------+
                        |
          +-------------+-------------+
          |                           |
          v                           v
   order-pod-a:8080            order-pod-b:8080

External path:

Internet
   |
   v
LoadBalancer
   |
   v
NodePort
   |
   v
ClusterIP Service
   |
   v
Ready Pod targetPort
```

Final rule:

```text
Do not memorize Service types as YAML keywords.
Remember the traffic question:

Inside cluster?        ClusterIP.
Outside through node?  NodePort.
Outside through cloud? LoadBalancer.
Many HTTP routes?      Ingress/Gateway in front of ClusterIP Services.
```

---

# 34. Final Production Checklist

```text
[ ] I know why Pods need stable Service identity.
[ ] I understand ClusterIP is internal cluster access.
[ ] I understand NodePort opens a port on every node.
[ ] I understand LoadBalancer asks cloud infrastructure for an external LB.
[ ] I can explain port vs targetPort vs nodePort.
[ ] I know Service selects Pods using labels.
[ ] I know EndpointSlice stores live ready backend addresses.
[ ] I know DNS resolves service name to ClusterIP.
[ ] I know readiness controls whether Pod receives traffic.
[ ] I can debug empty endpoints.
[ ] I can debug wrong targetPort.
[ ] I can debug LoadBalancer EXTERNAL-IP pending.
[ ] I understand why most internal microservices should use ClusterIP.
[ ] I understand why Ingress/Gateway is better for many HTTP routes.
```

---

# 35. Final Memory Hook

Kubernetes Service is not just a YAML object.

It is the stable contract between clients and replaceable Pods.

```text
Pods are replaceable.
Services are stable.
Labels connect them.
EndpointSlices track them.
kube-proxy/CNI routes to them.
Readiness protects traffic.
```

Final sentence:

```text
A Service is Kubernetes saying: clients should call the business capability, not chase the current worker IP.
```
