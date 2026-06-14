# 012_Kubernetes_Networking_Model.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Kubernetes Networking Exists

Docker taught us that a container can run an application process.

Kubernetes teaches us that hundreds or thousands of containers must communicate safely across many machines.

The real problem is not:

```text
How do I start one container?
```

The real production problem is:

```text
How does payment-service Pod on node-1 call inventory-service Pod on node-7
without caring where inventory-service is currently running?
```

A Kubernetes cluster is not one machine. It is many machines acting like one application platform.

```text
Cluster
  |
  +-- Node-1
  |     +-- Pod A
  |     +-- Pod B
  |
  +-- Node-2
  |     +-- Pod C
  |     +-- Pod D
  |
  +-- Node-3
        +-- Pod E
```

Without a networking model, every application would need to know:

```text
Which node has which pod?
What is the pod IP?
Did the pod restart?
Did the IP change?
Which pod is ready?
Which port should I call?
```

That would be impossible to operate.

Kubernetes networking exists to hide unstable infrastructure behind stable communication rules.

The mental model:

```text
Pods are temporary.
Nodes are physical placement.
Services give stable access.
DNS gives stable names.
CNI gives Pod networking.
kube-proxy / dataplane routes Service traffic.
```

One sentence:

```text
Kubernetes networking makes changing Pods look like stable services.
```

---

# 2. The Wrong Way To Think About Kubernetes Networking

Wrong model:

```text
A Pod is like a VM with a fixed IP forever.
```

Wrong.

Pods are replaceable.

```text
Pod order-abc -> IP 10.244.1.10
Pod crashes
New Pod order-def -> IP 10.244.2.21
```

If another service directly calls `10.244.1.10`, it breaks when the Pod dies.

Another wrong model:

```text
Service is a real process that forwards requests.
```

Usually wrong.

A Kubernetes Service is mostly a virtual abstraction. It gives a stable virtual IP and name. The real routing is handled by cluster networking rules, kube-proxy, iptables/IPVS/eBPF depending on implementation.

Wrong mental picture:

```text
Client -> Service Pod -> Backend Pod
```

Better mental picture:

```text
Client -> Service virtual IP/name -> networking rules -> one ready backend Pod
```

ASCII:

```text
Client Pod
   |
   | http://order-service
   v
Service abstraction
   |
   | selects ready Pods using labels
   v
Pod A / Pod B / Pod C
```

Do not memorize objects.

Ask:

```text
What stability problem does this object solve?
```

---

# 3. The Four Kubernetes Networking Promises

Kubernetes networking is built around a few important promises.

## Promise 1: Every Pod gets its own IP

```text
Pod A = 10.244.1.5
Pod B = 10.244.2.7
Pod C = 10.244.3.9
```

Containers inside the same Pod share the same network namespace.

## Promise 2: Pods can communicate with Pods without NAT

A Pod on node-1 should be able to reach a Pod on node-2 using the Pod IP.

```text
Pod A on Node-1 -> Pod B on Node-2
```

## Promise 3: Nodes can communicate with Pods

Kubelet, probes, and node-level components must be able to reach Pod IPs.

## Promise 4: Services provide stable access

Applications should call Services, not Pod IPs.

```text
http://inventory-service:8080
```

Not:

```text
http://10.244.2.7:8080
```

Picture:

```text
Unstable Layer:
  Pods come and go
  IPs change
  readiness changes

Stable Layer:
  Service name
  Service virtual IP
  selector
```

This is the heart of Kubernetes networking.

---

# 4. Real World Analogy: Hotel Reception

Imagine a hotel.

Guests should not know the private phone number of every staff member.

They call reception.

```text
Guest
  |
  | dial reception
  v
Reception Desk
  |
  +--> available cleaner
  +--> available room service
  +--> available security
```

Staff members change shifts.

```text
Cleaner A leaves
Cleaner B joins
Cleaner C is busy
```

But guests still call the same reception number.

Kubernetes Service is reception.

Pods are workers.

DNS name is the hotel phone extension.

```text
Client Pod
  |
  | call inventory-service
  v
Service
  |
  +--> Ready Pod 1
  +--> Ready Pod 2
  +--> Ready Pod 3
```

Mental model:

```text
Do not call workers directly.
Call the stable desk.
```

---

# 5. The Core Networking Picture

```text
                       Kubernetes Cluster

+---------------------------------------------------------------+
|                                                               |
|  Node-1                         Node-2                        |
|  +-----------------------+      +-----------------------+      |
|  | Pod: order-service    |      | Pod: inventory-1      |      |
|  | IP: 10.244.1.10      |      | IP: 10.244.2.20      |      |
|  +-----------+-----------+      +-----------+-----------+      |
|              |                              |                  |
|              |                              |                  |
|        CNI Pod Network connects all Pod IPs                   |
|              |                              |                  |
|              +--------------+---------------+                  |
|                             |                                  |
|                             v                                  |
|                  Service: inventory-service                   |
|                  ClusterIP: 10.96.10.50                       |
|                  DNS: inventory-service.default.svc            |
|                                                               |
+---------------------------------------------------------------+
```

When order-service calls inventory-service:

```text
1. order-service resolves DNS name.
2. DNS returns Service ClusterIP.
3. Request goes to Service virtual IP.
4. kube-proxy/dataplane chooses backend Pod.
5. Packet reaches inventory Pod IP.
6. Response returns to order-service.
```

The application sees:

```text
http://inventory-service:8080
```

The cluster handles:

```text
DNS
Service selection
endpoint tracking
packet routing
node crossing
```

---

# 6. Pod Network Mental Model

A Pod is the smallest deployable unit in Kubernetes.

Each Pod gets one network namespace.

All containers inside the Pod share:

```text
same IP
same port space
same localhost
same network interfaces
```

Diagram:

```text
Pod
+------------------------------------------------+
| Network Namespace                              |
| IP: 10.244.1.10                                |
|                                                |
|  Container A: Spring Boot app :8080            |
|  Container B: sidecar proxy :15001             |
|                                                |
|  localhost is shared                           |
+------------------------------------------------+
```

This means a sidecar can talk to the main container using localhost.

```text
Main app -> localhost:15001
Sidecar  -> localhost:8080
```

But two containers in the same Pod cannot both bind the same port.

Wrong:

```text
Container A binds 8080
Container B also binds 8080
```

They share a port space, so this conflicts.

Production lesson:

```text
Pod networking is shared inside Pod.
Pod networking is isolated across Pods.
```

---

# 7. Pod-To-Pod Communication

In Kubernetes, every Pod can reach every other Pod IP, assuming no NetworkPolicy blocks it.

Example:

```text
Pod A: 10.244.1.10 on Node-1
Pod B: 10.244.2.20 on Node-2
```

Flow:

```text
Pod A
  |
  | packet to 10.244.2.20
  v
Node-1 networking
  |
  v
CNI routing / overlay / native routing
  |
  v
Node-2 networking
  |
  v
Pod B
```

ASCII:

```text
+-------------------+                      +-------------------+
| Node-1            |                      | Node-2            |
|                   |                      |                   |
|  Pod A            |                      |  Pod B            |
|  10.244.1.10      |---- Pod Network ---->|  10.244.2.20      |
|                   |                      |                   |
+-------------------+                      +-------------------+
```

The exact mechanism depends on CNI plugin:

```text
Flannel      -> often overlay VXLAN
Calico       -> routing / BGP / policy
Cilium       -> eBPF dataplane
Weave        -> overlay networking
Cloud CNI    -> cloud VPC integration
```

Do not memorize plugin names first.

Remember:

```text
CNI is responsible for giving Pods network connectivity.
```

---

# 8. CNI Mental Model

CNI means Container Network Interface.

It is the plugin system Kubernetes uses to connect Pods to the cluster network.

When a Pod is created:

```text
Kubelet
  |
  | asks container runtime to create Pod sandbox
  v
CNI plugin
  |
  | assigns IP
  | creates interfaces
  | sets routes
  | applies network rules
  v
Pod network works
```

Picture:

```text
Pod Created
   |
   v
Network namespace created
   |
   v
CNI called
   |
   +--> assign Pod IP
   +--> attach veth pair
   +--> configure routes
   +--> apply policy
   v
Pod can communicate
```

CNI is like the road construction department.

Kubernetes says:

```text
I need this Pod connected.
```

CNI builds the roads.

Common production symptoms of CNI issues:

```text
Pods stuck in ContainerCreating
Pods have no IP
Cross-node Pod traffic fails
DNS fails because CoreDNS cannot reach API or Pods
NetworkPolicy behaves unexpectedly
```

Debug commands:

```bash
kubectl get pods -A -o wide
kubectl describe pod <pod>
kubectl get nodes -o wide
kubectl logs -n kube-system <cni-pod-name>
```

---

# 9. Service Mental Model

A Service gives stable access to a changing set of Pods.

Deployment creates Pods with labels:

```yaml
labels:
  app: inventory-service
```

Service selects them:

```yaml
selector:
  app: inventory-service
```

Diagram:

```text
Service: inventory-service
selector: app=inventory-service

        |
        v
+-------------------+
| Endpoints         |
| 10.244.1.11:8080 |
| 10.244.2.21:8080 |
| 10.244.3.31:8080 |
+-------------------+
```

The Service does not care about Pod names.

It cares about labels and readiness.

If a Pod is not ready, it should not receive normal Service traffic.

```text
Ready Pod     -> endpoint included
NotReady Pod  -> endpoint excluded for normal traffic
Deleted Pod   -> endpoint removed
New Ready Pod -> endpoint added
```

Mental model:

```text
Service = stable front door + dynamic backend list
```

---

# 10. Service YAML Example

Spring Boot inventory-service Deployment:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: inventory-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: inventory-service
  template:
    metadata:
      labels:
        app: inventory-service
    spec:
      containers:
        - name: inventory-service
          image: inventory-service:1.0.0
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
```

Service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: inventory-service
spec:
  type: ClusterIP
  selector:
    app: inventory-service
  ports:
    - port: 8080
      targetPort: 8080
```

Meaning:

```text
Service port 8080 receives traffic.
Traffic is sent to targetPort 8080 on matching ready Pods.
```

Picture:

```text
Client
  |
  | http://inventory-service:8080
  v
Service port: 8080
  |
  v
Pod targetPort: 8080
```

Common mistake:

```text
port and targetPort are not understood.
```

Simple rule:

```text
port       = Service-facing port
targetPort = Pod container port
```

---

# 11. ClusterIP Mental Model

ClusterIP is the default Service type.

It gives an internal-only stable virtual IP.

```text
Service inventory-service
ClusterIP 10.96.10.50
```

Pods inside the cluster can call it.

```text
order-service -> 10.96.10.50:8080
```

Usually applications do not call the IP directly.

They call DNS:

```text
http://inventory-service:8080
```

Diagram:

```text
Pod A
  |
  | DNS: inventory-service
  v
ClusterIP: 10.96.10.50
  |
  v
Backend Pod selected
```

ClusterIP is not normally reachable from outside the cluster.

Wrong expectation:

```text
Browser on laptop -> ClusterIP
```

Usually fails.

Correct:

```text
Inside cluster -> ClusterIP
Outside cluster -> NodePort / LoadBalancer / Ingress
```

Mental model:

```text
ClusterIP = internal stable service address
```

---

# 12. DNS Mental Model

Kubernetes runs cluster DNS, usually CoreDNS.

When a Service is created:

```text
Service name: inventory-service
Namespace: default
```

DNS name becomes:

```text
inventory-service.default.svc.cluster.local
```

Inside the same namespace, a Pod can usually call:

```text
inventory-service
```

Across namespaces:

```text
inventory-service.default
inventory-service.default.svc.cluster.local
```

Diagram:

```text
order-service Pod
  |
  | DNS query: inventory-service
  v
CoreDNS
  |
  | returns ClusterIP
  v
10.96.10.50
```

Debug DNS:

```bash
kubectl run dns-test --image=busybox:1.36 -it --rm --restart=Never -- sh
nslookup inventory-service
wget -qO- http://inventory-service:8080/actuator/health
```

Production lesson:

```text
If DNS fails, Services may look broken even when Pods are healthy.
```

---

# 13. kube-proxy Mental Model

kube-proxy watches Services and Endpoints.

It programs node-level networking rules so Service IP traffic reaches backend Pods.

Simple view:

```text
API Server
  |
  | Service + EndpointSlice updates
  v
kube-proxy on every node
  |
  | programs rules
  v
Packets to ClusterIP are redirected to Pods
```

Picture:

```text
Node
+------------------------------------------------+
| kube-proxy watches API Server                  |
|                                                |
| Service IP: 10.96.10.50                        |
| Backends:                                      |
|   10.244.1.11:8080                             |
|   10.244.2.21:8080                             |
|                                                |
| Node networking rules choose backend           |
+------------------------------------------------+
```

kube-proxy modes may include:

```text
iptables
IPVS
```

Some modern CNIs can replace kube-proxy with eBPF dataplane.

Do not memorize implementation first.

Remember:

```text
Some dataplane component turns Service abstraction into packet routing.
```

---

# 14. EndpointSlice Mental Model

A Service selector finds Pods.

Kubernetes stores the backend list in EndpointSlices.

Old mental model:

```text
Service directly knows all Pods
```

Better model:

```text
Service selector
   |
   v
EndpointSlice Controller
   |
   v
EndpointSlices containing ready Pod IPs
   |
   v
kube-proxy / dataplane consumes them
```

ASCII:

```text
Pods with label app=inventory
  |
  v
Service selector
  |
  v
EndpointSlice
+---------------------------+
| 10.244.1.11:8080 Ready   |
| 10.244.2.21:8080 Ready   |
| 10.244.3.31:8080 Ready   |
+---------------------------+
```

Debug:

```bash
kubectl get endpoints inventory-service
kubectl get endpointslice -l kubernetes.io/service-name=inventory-service
```

If Service exists but endpoints are empty:

```text
Likely causes:
- selector mismatch
- Pods not Ready
- wrong namespace
- no Pods exist
```

---

# 15. NodePort Mental Model

NodePort exposes a Service on every node at a high port.

Example:

```text
NodePort: 30080
```

Then this works from outside if firewall allows:

```text
http://node-ip:30080
```

Diagram:

```text
External Client
   |
   | node-1-ip:30080
   v
NodePort rule on Node-1
   |
   v
Service backend selection
   |
   v
Pod on Node-2 maybe
```

Important:

```text
You may enter through node-1 but reach a Pod on node-2.
```

YAML:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: inventory-nodeport
spec:
  type: NodePort
  selector:
    app: inventory-service
  ports:
    - port: 8080
      targetPort: 8080
      nodePort: 30080
```

Mental model:

```text
NodePort = every node opens same external port for that Service
```

Production note:

```text
NodePort is often used behind a LoadBalancer or for simple testing,
but not usually the final clean HTTP routing model for many services.
```

---

# 16. LoadBalancer Mental Model

LoadBalancer Service asks the cloud provider or infrastructure to create an external load balancer.

```text
External Client
   |
   v
Cloud Load Balancer
   |
   v
Kubernetes Service
   |
   v
Ready Pods
```

YAML:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: inventory-lb
spec:
  type: LoadBalancer
  selector:
    app: inventory-service
  ports:
    - port: 80
      targetPort: 8080
```

Meaning:

```text
Expose service externally on port 80.
Send traffic to Pods on container port 8080.
```

ASCII:

```text
Internet
   |
   v
External LB IP
   |
   v
Service port 80
   |
   v
Pod targetPort 8080
```

Production lesson:

```text
LoadBalancer per service can become expensive and messy.
Ingress or Gateway API is often better for many HTTP services.
```

---

# 17. Ingress Mental Model

Ingress handles HTTP routing into the cluster.

It is not the same as Service.

Service gives stable access to Pods.

Ingress routes external HTTP host/path to Services.

```text
Client
  |
  | https://api.example.com/orders
  v
Ingress Controller
  |
  | route /orders
  v
order-service Service
  |
  v
order-service Pods
```

Picture:

```text
Internet
  |
  v
Ingress Controller
  |
  +-- /orders     -> order-service
  +-- /payments   -> payment-service
  +-- /inventory  -> inventory-service
```

Ingress YAML:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: api-ingress
spec:
  rules:
    - host: api.example.com
      http:
        paths:
          - path: /inventory
            pathType: Prefix
            backend:
              service:
                name: inventory-service
                port:
                  number: 8080
```

Mental model:

```text
Ingress = HTTP routing layer
Service = stable backend abstraction
Pod = actual application process
```

---

# 18. Full Request Dry Run: Browser To Spring Boot Pod

User calls:

```text
https://api.example.com/inventory/items/123
```

Dry run:

```text
1. Browser resolves api.example.com to external load balancer IP.

2. Request reaches cloud load balancer.

3. Load balancer forwards to Ingress Controller.

4. Ingress Controller checks host and path.

5. Rule matches /inventory.

6. Ingress forwards request to inventory-service Service.

7. Service virtual IP is mapped to ready backend Pods.

8. Dataplane chooses one Pod IP.

9. Packet reaches inventory-service Pod.

10. Spring Boot Tomcat receives request on port 8080.

11. Controller method runs.

12. Response travels back through the same chain.
```

ASCII:

```text
Browser
  |
  v
DNS public
  |
  v
Cloud Load Balancer
  |
  v
Ingress Controller
  |
  v
Service: inventory-service
  |
  v
Pod: inventory-service-abc
  |
  v
Spring Boot Controller
```

Java example:

```java
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @GetMapping("/items/{id}")
    public Map<String, Object> find(@PathVariable String id) {
        return Map.of(
            "id", id,
            "available", true,
            "servedBy", System.getenv().getOrDefault("HOSTNAME", "local")
        );
    }
}
```

This `HOSTNAME` helps see which Pod served the request.

---

# 19. Service Discovery From Spring Boot

Inside Kubernetes, a Spring Boot service can call another service by Kubernetes DNS name.

Example using WebClient:

```java
@Configuration
public class ClientConfig {

    @Bean
    WebClient inventoryClient(WebClient.Builder builder) {
        return builder
            .baseUrl("http://inventory-service:8080")
            .build();
    }
}
```

Usage:

```java
@Service
public class OrderService {

    private final WebClient inventoryClient;

    public OrderService(WebClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    public Mono<String> checkInventory(String itemId) {
        return inventoryClient
            .get()
            .uri("/inventory/items/{id}", itemId)
            .retrieve()
            .bodyToMono(String.class);
    }
}
```

No Eureka needed for this basic case.

Kubernetes Service + DNS already gives service discovery.

Mental model:

```text
Spring Cloud Discovery world:
  App asks registry for instances.

Kubernetes world:
  App calls stable Service DNS.
  Cluster networking chooses backend Pod.
```

This is why many Kubernetes-native systems use:

```text
http://service-name.namespace.svc.cluster.local
```

---

# 20. Readiness And Networking

Readiness controls whether a Pod should receive Service traffic.

Spring Boot may be running, but not ready.

```text
Java process alive
Tomcat started
DB connection still failing
Cache not warm
Kafka not connected
```

Readiness probe:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
```

Spring Boot config:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
  health:
    readinessstate:
      enabled: true
    livenessstate:
      enabled: true
```

Flow:

```text
Pod starts
  |
  v
Readiness fails
  |
  v
Pod not added to Service endpoints
  |
  v
No user traffic
  |
  v
Readiness passes
  |
  v
Pod added to endpoints
  |
  v
Traffic allowed
```

Production lesson:

```text
Readiness is networking safety.
It protects users from half-started apps.
```

---

# 21. NetworkPolicy Mental Model

By default, many Kubernetes clusters allow broad Pod-to-Pod traffic.

NetworkPolicy lets you restrict traffic.

Mental model:

```text
Without NetworkPolicy:
  Everyone can talk to everyone.

With NetworkPolicy:
  Only allowed conversations pass.
```

Analogy:

```text
Office without badge doors:
  Anyone can enter any room.

Office with badge doors:
  Only allowed teams enter certain rooms.
```

Example: only order-service can call payment-service.

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-order-to-payment
spec:
  podSelector:
    matchLabels:
      app: payment-service
  policyTypes:
    - Ingress
  ingress:
    - from:
        - podSelector:
            matchLabels:
              app: order-service
      ports:
        - protocol: TCP
          port: 8080
```

Picture:

```text
order-service   ---- allowed ----> payment-service
inventory       ---- blocked ----> payment-service
random-pod      ---- blocked ----> payment-service
```

Important:

```text
NetworkPolicy enforcement depends on CNI support.
```

---

# 22. Same Node vs Cross Node Traffic

Same node Pod traffic:

```text
Pod A -> node bridge/veth -> Pod B
```

Cross node Pod traffic:

```text
Pod A -> Node-1 network -> CNI routing/overlay -> Node-2 network -> Pod B
```

ASCII:

```text
Same Node

Node-1
+--------------------------------+
| Pod A ---- local network ---- Pod B |
+--------------------------------+

Cross Node

Node-1                              Node-2
+-----------+                      +-----------+
| Pod A     |---- cluster net ---->| Pod B     |
+-----------+                      +-----------+
```

Why this matters:

```text
Same-node traffic may be fast.
Cross-node traffic depends on CNI, overlay, routing, MTU, cloud network.
```

Production symptoms:

```text
Same-node calls work.
Cross-node calls timeout.
```

Possible causes:

```text
CNI routing issue
MTU mismatch
firewall/security group problem
node route table issue
NetworkPolicy issue
```

Debug by checking Pod placement:

```bash
kubectl get pods -o wide
```

---

# 23. MTU Mental Model

MTU is Maximum Transmission Unit.

It is the maximum packet size on a network path.

Overlay networks add extra headers.

```text
Original packet
  + overlay header
  = bigger packet
```

If packet becomes too large:

```text
fragmentation or drop
```

Symptoms:

```text
small requests work
large responses hang
TLS handshakes behave strangely
some HTTP calls timeout
file upload fails
```

Picture:

```text
Packet size: 1500
Overlay adds: 50
Total: 1550
Network supports: 1500
Result: trouble
```

This is why Kubernetes networking bugs can look weird.

It is not always your Java code.

Debug thinking:

```text
If tiny curl works but large payload fails,
think MTU, proxy, timeout, or packet fragmentation.
```

---

# 24. Hairpin Traffic Mental Model

Hairpin happens when a Pod calls a Service and the Service routes back to the same Pod.

```text
Pod A
  |
  | calls service-name
  v
Service
  |
  v
Pod A again
```

This can happen when a Service has only one backend or load balancing selects the caller Pod.

Why it matters:

```text
Some network setups need correct hairpin mode.
Otherwise the Pod cannot reach itself through Service IP.
```

Example:

```text
inventory-service Pod calls http://inventory-service:8080/internal
Service sends traffic back to same Pod
```

Usually you should avoid unnecessary self-calls.

Better:

```text
Call local method if inside same app.
Call Service only for inter-service communication.
```

Mental model:

```text
Service call may return to any ready backend, including yourself.
```

---

# 25. Production Story: Service Exists But No Traffic

Symptoms:

```text
kubectl get svc
inventory-service ClusterIP 10.96.10.50

kubectl get pods
inventory-service-abc Running Ready
```

But:

```text
curl http://inventory-service:8080 fails
```

Debug:

```bash
kubectl describe svc inventory-service
kubectl get endpoints inventory-service
kubectl get pods --show-labels
```

Result:

```text
Endpoints: <none>
```

Root cause:

Deployment label:

```yaml
labels:
  app: inventory
```

Service selector:

```yaml
selector:
  app: inventory-service
```

Mismatch.

Diagram:

```text
Service selector app=inventory-service
      |
      v
No matching Pods
      |
      v
No endpoints
      |
      v
No traffic
```

Fix:

```text
Make Service selector match Pod labels.
```

Lesson:

```text
In Kubernetes networking, labels are wiring.
Wrong label = broken wire.
```

---

# 26. Production Story: DNS Failure

Symptoms:

```text
curl http://inventory-service:8080
Could not resolve host
```

But Service exists.

Possible causes:

```text
CoreDNS down
Pod DNS config broken
wrong namespace
NetworkPolicy blocks DNS
CNI issue
node local DNS cache issue
```

Debug:

```bash
kubectl get pods -n kube-system -l k8s-app=kube-dns
kubectl logs -n kube-system deploy/coredns
kubectl exec -it <pod> -- cat /etc/resolv.conf
kubectl exec -it <pod> -- nslookup kubernetes.default
kubectl exec -it <pod> -- nslookup inventory-service
```

Important namespace lesson:

```text
From same namespace:
  inventory-service

From another namespace:
  inventory-service.default
```

ASCII:

```text
Pod in orders namespace
  |
  | inventory-service
  v
Looks for inventory-service.orders.svc

Correct:
  inventory-service.default.svc
```

---

# 27. Production Story: Works From One Pod, Fails From Another

Symptoms:

```text
order-service can call payment-service
inventory-service cannot call payment-service
```

Possible causes:

```text
NetworkPolicy allows only order-service
wrong namespace DNS
service mesh policy
mTLS identity issue
source Pod label mismatch
```

Debug:

```bash
kubectl get networkpolicy -A
kubectl get pods --show-labels
kubectl exec -it order-pod -- curl payment-service:8080/actuator/health
kubectl exec -it inventory-pod -- curl payment-service:8080/actuator/health
```

NetworkPolicy mental model:

```text
Traffic permission may depend on source label and destination label.
```

Diagram:

```text
[app=order]      ---> allowed ---> [app=payment]
[app=inventory]  ---> blocked ---> [app=payment]
```

Lesson:

```text
When one Pod works and another fails, compare labels, namespace, policy, sidecars, and DNS config.
```

---

# 28. Production Story: Pod IP Works, Service Name Fails

Case:

```text
curl http://10.244.2.20:8080 works
curl http://inventory-service:8080 fails
```

This means Pod networking works, but Service/DNS path is broken.

Possible layers:

```text
DNS resolution failed
Service selector wrong
Endpoints empty
kube-proxy/dataplane issue
Service port wrong
targetPort wrong
```

Debug chain:

```bash
nslookup inventory-service
kubectl get svc inventory-service -o yaml
kubectl get endpoints inventory-service
kubectl get endpointslice -l kubernetes.io/service-name=inventory-service
```

Mental model:

```text
Pod IP path and Service path are different layers.
```

ASCII:

```text
Direct Pod IP:
Client -> Pod IP

Service path:
Client -> DNS -> ClusterIP -> dataplane -> Endpoint Pod
```

If direct Pod IP works, do not blame the Spring Boot app first.

Blame service discovery or service routing path.

---

# 29. Production Story: Service Works, Ingress Fails

Case:

```text
Inside cluster:
curl http://inventory-service:8080 works

Outside cluster:
https://api.example.com/inventory fails
```

This means backend Service is healthy, but external routing is broken.

Possible causes:

```text
Ingress rule wrong
Ingress class missing
TLS secret issue
DNS public record wrong
Load balancer unhealthy
path rewrite issue
backend service port mismatch
```

Debug:

```bash
kubectl get ingress
kubectl describe ingress api-ingress
kubectl logs -n ingress-nginx deploy/ingress-nginx-controller
kubectl get svc -n ingress-nginx
```

Layer model:

```text
Browser
  |
  v
Public DNS
  |
  v
Load Balancer
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

Rule:

```text
If Service works internally, debug from Ingress outward.
```

---

# 30. Debugging Mindset: Layer By Layer

Never randomly change YAML.

Follow the chain.

```text
1. Is the Pod running?
2. Is the Pod ready?
3. Does the Pod listen on correct port?
4. Does Service selector match labels?
5. Are endpoints populated?
6. Does DNS resolve?
7. Does ClusterIP work?
8. Does NodePort/LoadBalancer work?
9. Does Ingress route correctly?
10. Is NetworkPolicy blocking traffic?
11. Is CNI healthy?
12. Is cloud firewall/security group blocking traffic?
```

Commands:

```bash
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>

kubectl get svc
kubectl describe svc <svc>
kubectl get endpoints <svc>
kubectl get endpointslice -l kubernetes.io/service-name=<svc>

kubectl exec -it <pod> -- nslookup <svc>
kubectl exec -it <pod> -- curl -v http://<svc>:<port>/actuator/health

kubectl get networkpolicy -A
kubectl get ingress
kubectl describe ingress <ingress>
```

Mental model:

```text
Networking bugs are solved by proving each layer.
```

---

# 31. Kubernetes Networking With Spring Boot Profiles

A common production bug is environment-specific URL configuration.

Bad application.yml:

```yaml
inventory:
  base-url: http://localhost:8081
```

This works locally but fails in Kubernetes.

Inside a Pod:

```text
localhost means same Pod
```

It does not mean another service.

Correct Kubernetes config:

```yaml
inventory:
  base-url: http://inventory-service:8080
```

Spring config:

```java
@ConfigurationProperties(prefix = "inventory")
public class InventoryClientProperties {
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
```

Client:

```java
@Bean
WebClient inventoryWebClient(WebClient.Builder builder,
                             InventoryClientProperties props) {
    return builder.baseUrl(props.getBaseUrl()).build();
}
```

ConfigMap:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: order-config
data:
  application-k8s.yml: |
    inventory:
      base-url: http://inventory-service:8080
```

Lesson:

```text
In Kubernetes, localhost is inside the same Pod.
Use Service DNS for other applications.
```

---

# 32. Common Beginner Mistakes

```text
Mistake 1:
Calling Pod IPs directly.
Correct:
Call Service DNS.

Mistake 2:
Thinking localhost means another Pod.
Correct:
localhost means same Pod.

Mistake 3:
Creating Service with wrong selector.
Correct:
Verify labels and endpoints.

Mistake 4:
Confusing port and targetPort.
Correct:
port is Service port, targetPort is Pod port.

Mistake 5:
Thinking ClusterIP is externally reachable.
Correct:
ClusterIP is internal.

Mistake 6:
Debugging Ingress before checking Service.
Correct:
Debug from Pod -> Service -> Ingress.

Mistake 7:
Ignoring readiness.
Correct:
Only ready Pods receive Service traffic.

Mistake 8:
Assuming NetworkPolicy always works.
Correct:
It depends on CNI support.
```

---

# 33. Interview Answers

## What is the Kubernetes networking model?

Kubernetes gives every Pod its own IP and expects Pods to communicate across nodes through a flat Pod network provided by a CNI plugin. Because Pods are temporary and IPs change, applications usually communicate through Services, which provide stable virtual IPs and DNS names backed by ready Pod endpoints.

## Why should applications call Services instead of Pod IPs?

Pod IPs are unstable because Pods can be recreated, rescheduled, or replaced during rollouts. A Service gives a stable name and virtual IP, while Kubernetes dynamically updates the backend endpoints based on matching labels and readiness.

## What does CNI do?

CNI plugins configure Pod networking. They assign Pod IPs, attach network interfaces, configure routes, and often enforce NetworkPolicy. Examples include Calico, Cilium, Flannel, and cloud-provider CNIs.

## What is kube-proxy?

kube-proxy watches Services and EndpointSlices and programs node-level networking rules so traffic to Service virtual IPs is routed to backend Pods. Some clusters replace kube-proxy with eBPF-based dataplanes.

## What is the difference between Service port and targetPort?

Service port is the port exposed by the Service. targetPort is the port on the backend Pod/container that receives traffic.

## What is ClusterIP?

ClusterIP is the default Service type. It exposes a stable internal virtual IP reachable inside the cluster.

## What is NodePort?

NodePort exposes a Service on a fixed port on every node, allowing external clients to access the Service through any node IP and that port if network/firewall rules allow.

## What is LoadBalancer?

LoadBalancer asks the underlying cloud or infrastructure provider to provision an external load balancer and route traffic to the Kubernetes Service.

## What is Ingress?

Ingress is an HTTP routing abstraction. It routes external host/path-based HTTP traffic to Services, usually through an Ingress Controller.

## Why can a Service have no endpoints?

Common reasons are selector-label mismatch, Pods not ready, Pods in another namespace, or no matching Pods existing.

---

# 34. Cheat Sheet

```text
Pod IP                 = IP assigned to one Pod
Pod network            = flat network between Pods
CNI                    = plugin that creates Pod networking
Service                = stable access to dynamic Pods
ClusterIP              = internal Service virtual IP
NodePort               = exposes Service on every node port
LoadBalancer           = external load balancer for a Service
Ingress                = HTTP routing to Services
CoreDNS                = cluster DNS
kube-proxy             = programs Service routing rules
EndpointSlice          = backend Pod IP list for Service
NetworkPolicy          = traffic allow/block rules
port                   = Service-facing port
targetPort             = Pod-facing port
localhost in Pod        = same Pod only
readiness              = controls Service endpoint inclusion
```

Core flow:

```text
Client Pod
  |
  | DNS lookup service-name
  v
CoreDNS
  |
  | returns ClusterIP
  v
Service virtual IP
  |
  v
kube-proxy / dataplane
  |
  v
Endpoint Pod IP
  |
  v
Spring Boot container
```

Debug flow:

```text
Pod -> Ready -> Service selector -> Endpoints -> DNS -> ClusterIP -> Ingress -> External LB
```

---

# 35. One Picture To Remember

```text
                         INTERNET
                            |
                            v
                    +---------------+
                    | Load Balancer |
                    +-------+-------+
                            |
                            v
                    +---------------+
                    | Ingress       |
                    | host/path     |
                    +-------+-------+
                            |
                            v
                    +---------------+
                    | Service       |
                    | stable name   |
                    | ClusterIP     |
                    +-------+-------+
                            |
              selector + endpoints + readiness
                            |
          +-----------------+-----------------+
          |                 |                 |
          v                 v                 v
   +-------------+   +-------------+   +-------------+
   | Pod A       |   | Pod B       |   | Pod C       |
   | 10.244.1.5  |   | 10.244.2.8  |   | 10.244.3.9  |
   | Spring Boot |   | Spring Boot |   | Spring Boot |
   +-------------+   +-------------+   +-------------+

CNI connects Pod IPs across nodes.
CoreDNS turns service names into Service IPs.
Dataplane turns Service IPs into Pod traffic.
Readiness decides which Pods receive traffic.
```

Final memory hook:

```text
Pods are cattle.
Pod IPs are temporary.
Services are stable phone numbers.
DNS is the phone book.
CNI builds the roads.
kube-proxy/dataplane directs the packets.
Readiness decides who can receive customers.
```

---

# 36. Final Production Checklist

```text
[ ] I know that every Pod gets its own IP.
[ ] I know Pods can communicate across nodes through CNI.
[ ] I know applications should call Service DNS, not Pod IPs.
[ ] I understand Service selector -> EndpointSlice -> backend Pods.
[ ] I understand ClusterIP, NodePort, LoadBalancer, and Ingress.
[ ] I understand port vs targetPort.
[ ] I understand localhost means same Pod.
[ ] I know readiness controls Service traffic.
[ ] I can debug empty endpoints.
[ ] I can test DNS from inside a Pod.
[ ] I can separate Pod networking issues from Service issues.
[ ] I can separate Service issues from Ingress issues.
[ ] I know NetworkPolicy may block traffic.
[ ] I know CNI implementation affects routing, policy, and performance.
```

