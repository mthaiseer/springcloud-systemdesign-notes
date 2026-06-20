# 014_KubeProxy_IPTables_IPVS.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why This Chapter Exists

In the previous networking chapters, you learned that Kubernetes gives Pods unstable IPs and Services give stable access.

But one question remains:

```text
Client Pod calls http://order-service
        |
        v
Service ClusterIP
        |
        v
Which real Pod receives the packet?
```

That is where `kube-proxy` enters.

Most beginners memorize:

```text
kube-proxy uses iptables or IPVS
```

But they do not understand what problem it solves.

The real problem is simple:

```text
Service IP is virtual.
No Pod is actually listening on the Service IP.
Some node-level mechanism must translate Service traffic to real Pod IPs.
```

One picture:

```text
Client
  |
  | call 10.96.10.20:8080
  v
Service ClusterIP
  |
  | kube-proxy programmed rules
  v
Real Pod IP 10.244.2.15:8080
```

Kubernetes Service is the stable phone number.

Pod IPs are temporary employee extensions.

`kube-proxy` is the phone switchboard that forwards calls from the stable number to available employees.

Do not memorize iptables and IPVS first.

Understand this:

```text
kube-proxy = node-level Service traffic translator
```

---

# 2. The Service Illusion

A Kubernetes Service looks like a real load balancer.

Example:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: order-service
spec:
  selector:
    app: order-service
  ports:
    - port: 80
      targetPort: 8080
```

Kubernetes gives it a ClusterIP:

```text
order-service.default.svc.cluster.local -> 10.96.10.20
```

A client calls:

```bash
curl http://order-service
```

DNS resolves:

```text
order-service -> 10.96.10.20
```

But here is the important part:

```text
There is no container running at 10.96.10.20.
```

The ClusterIP is virtual.

It exists because Linux networking rules on each node know how to intercept traffic to that IP.

ASCII:

```text
Client Pod
   |
   | DNS lookup
   v
10.96.10.20:80       <-- virtual Service IP
   |
   | node rules rewrite packet
   v
10.244.2.15:8080     <-- real Pod IP
```

Mental model:

```text
Service IP is not a machine.
Service IP is a routing promise.
```

`kube-proxy` keeps that promise by programming packet forwarding rules.

---

# 3. Real World Analogy: Reception Desk

Imagine an office building.

Customers know only one number:

```text
Order Department: +40-100-ORDER
```

Inside the office, workers change desks every day.

```text
Alice desk 3
Bob desk 7
Carol desk 12
```

Customer does not know this.

They call the stable department number.

Reception desk chooses an available worker.

```text
Customer
   |
   v
Reception Number
   |
   +--> Alice
   +--> Bob
   +--> Carol
```

In Kubernetes:

```text
Customer number = Service ClusterIP
Workers         = Pods
Reception       = kube-proxy rules
```

If Bob leaves and Dave joins, the customer number does not change.

Reception updates its internal routing list.

```text
Old endpoints:
10.244.1.10
10.244.2.15

New endpoints:
10.244.1.10
10.244.3.21
```

This is the Service abstraction.

You do not teach clients every Pod IP.

You teach the cluster how to route from a stable virtual IP to changing Pod IPs.

---

# 4. kube-proxy In One Sentence

`kube-proxy` watches Services and EndpointSlices, then programs the node network so packets sent to Service IPs are forwarded to matching Pod IPs.

Expanded:

```text
kube-proxy watches:
  - Services
  - EndpointSlices

kube-proxy writes:
  - iptables rules
  - or IPVS virtual server rules
  - or userspace proxy rules in older mode

Result:
  Service traffic reaches real Pods
```

ASCII:

```text
API Server
   |
   | watch Services + EndpointSlices
   v
kube-proxy on every node
   |
   | program Linux packet rules
   v
Node networking
   |
   | DNAT / load balance
   v
Backend Pod
```

Important:

```text
kube-proxy does not usually proxy bytes like an application reverse proxy.
```

In modern modes, traffic is handled by the Linux kernel.

`kube-proxy` is more like a rule writer than a traffic handler.

Mental shortcut:

```text
kube-proxy writes the map.
Linux kernel drives the car.
```

---

# 5. Where kube-proxy Runs

`kube-proxy` usually runs as a DaemonSet.

That means one instance on every node.

```text
Cluster

Node-1
  kube-proxy
  Pod A

Node-2
  kube-proxy
  Pod B

Node-3
  kube-proxy
  Pod C
```

Why every node?

Because a Pod on any node can call any Service.

So every node must know how to translate:

```text
Service IP -> backend Pod IP
```

Diagram:

```text
Client Pod on Node-1
   |
   | calls Service IP
   v
Node-1 kube-proxy rules
   |
   +--> Pod on Node-1
   +--> Pod on Node-2
   +--> Pod on Node-3
```

Even if the backend Pod is remote, the local node must make the first Service routing decision.

This is why kube-proxy is node-local.

It prepares each node to understand Kubernetes Services.

---

# 6. The Objects kube-proxy Watches

A Service selector finds matching Pods.

But kube-proxy does not directly scan Pods in the hot path.

Kubernetes creates endpoint data.

Modern Kubernetes uses EndpointSlice.

Flow:

```text
Pods
  |
  | labels match Service selector
  v
EndpointSlice controller
  |
  | creates EndpointSlice objects
  v
kube-proxy watches EndpointSlices
  |
  | programs node rules
  v
Service traffic works
```

ASCII:

```text
Service selector:
app = order-service

Matching Pods:
10.244.1.10:8080
10.244.2.15:8080
10.244.3.21:8080

EndpointSlice:
order-service endpoints =
  10.244.1.10:8080
  10.244.2.15:8080
  10.244.3.21:8080

kube-proxy:
write rules for 10.96.10.20:80
```

Important mental model:

```text
Service is stable identity.
EndpointSlice is current backend list.
kube-proxy is node rule programmer.
```

If a Pod is not Ready, it normally should not appear as a ready endpoint for normal Service traffic.

That is why readiness probes matter.

---

# 7. End-To-End Service Packet Flow

Assume:

```text
Client Pod IP:    10.244.1.5
Service ClusterIP:10.96.10.20
Service port:     80
Backend Pod IP:   10.244.2.15
Target port:      8080
```

Application request:

```java
restTemplate.getForObject("http://order-service/api/orders/1", Order.class);
```

DNS:

```text
order-service -> 10.96.10.20
```

Packet:

```text
source      = 10.244.1.5:45678
destination = 10.96.10.20:80
```

Node rules rewrite destination:

```text
destination = 10.244.2.15:8080
```

Then packet is routed through the CNI network to the backend Pod.

ASCII:

```text
Client Pod
10.244.1.5
   |
   | dst=10.96.10.20:80
   v
Node networking rules
   |
   | DNAT
   v
dst=10.244.2.15:8080
   |
   v
Backend Pod
```

Return traffic is tracked by connection tracking.

So the client still feels like it talked to the Service.

```text
Application thinks:
I called order-service.

Kernel handled:
Service IP -> Pod IP translation.
```

---

# 8. kube-proxy Modes

Common modes:

```text
userspace  - old, slow, rarely used
iptables   - common default in many clusters
ipvs       - kernel load balancing using IPVS
```

Simple comparison:

```text
Mode        Main idea
----------  ----------------------------------------
userspace   kube-proxy process handles connections
iptables    Linux netfilter rules choose backends
ipvs        Linux IPVS virtual server chooses backends
```

Diagram:

```text
Service traffic
   |
   +--> userspace mode: packet goes to kube-proxy process
   |
   +--> iptables mode: packet handled by netfilter rules
   |
   +--> IPVS mode: packet handled by kernel IPVS load balancer
```

For learning:

```text
iptables = rule-chain based NAT
IPVS     = kernel load balancer table
```

Do not memorize commands first.

Understand the shape:

```text
iptables mode creates many rules.
IPVS mode creates virtual services and real servers.
```

---

# 9. Userspace Mode: Historical Model

In userspace mode, kube-proxy actually accepted connections and proxied them to backend Pods.

Flow:

```text
Client Pod
   |
   | Service IP traffic
   v
iptables redirects to kube-proxy local port
   |
   v
kube-proxy process
   |
   v
Backend Pod
```

ASCII:

```text
Client
  |
  v
Service IP
  |
  v
kube-proxy process
  |
  v
Pod
```

Problem:

```text
Traffic enters user space.
Extra context switches.
Less efficient.
kube-proxy process becomes more directly involved.
```

This mode is mostly useful for history.

Modern clusters typically use iptables or IPVS.

Memory hook:

```text
userspace mode = kube-proxy as an active traffic middleman
iptables/IPVS = kube-proxy as rule programmer
```

For production engineering, focus on iptables and IPVS.

---

# 10. iptables Mode Mental Model

iptables is Linux packet rule machinery.

In iptables mode, kube-proxy creates NAT rules.

When packet destination matches a Service IP and port, rules choose one backend and rewrite the destination.

High-level:

```text
dst = ServiceIP:port
        |
        v
iptables rule match
        |
        v
choose backend
        |
        v
DNAT to PodIP:targetPort
```

ASCII:

```text
Packet:
dst=10.96.10.20:80

iptables NAT table
  |
  +-- KUBE-SERVICES
        |
        +-- match 10.96.10.20:80
              |
              v
            KUBE-SVC-XYZ
              |
              +-- random probability -> KUBE-SEP-A -> 10.244.1.10:8080
              +-- random probability -> KUBE-SEP-B -> 10.244.2.15:8080
              +-- default            -> KUBE-SEP-C -> 10.244.3.21:8080
```

Important terms:

```text
DNAT = Destination Network Address Translation
```

Before:

```text
destination = Service IP
```

After:

```text
destination = Pod IP
```

The application does not do this.

The Linux kernel does it while processing packets.

---

# 11. iptables Rule Chain Picture

The actual names can be long, but the mental picture matters.

```text
PREROUTING / OUTPUT
        |
        v
KUBE-SERVICES
        |
        v
KUBE-SVC-<hash>
        |
        v
KUBE-SEP-<endpoint-hash>
        |
        v
DNAT to Pod IP
```

ASCII:

```text
+------------------+
| Packet enters    |
+--------+---------+
         |
         v
+------------------+
| NAT table        |
+--------+---------+
         |
         v
+------------------+
| KUBE-SERVICES    |
+--------+---------+
         |
         v
+------------------+
| KUBE-SVC-ABC     |
| service chain    |
+--------+---------+
         |
         v
+------------------+
| KUBE-SEP-POD1    |
| endpoint chain   |
+--------+---------+
         |
         v
+------------------+
| DNAT to Pod IP   |
+------------------+
```

Think of it like a call routing menu:

```text
Main switchboard
  -> service extension
      -> endpoint extension
          -> actual worker
```

`iptables` mode works well but can become heavy when the number of Services and endpoints becomes very large.

Because the matching model is rule-chain based.

---

# 12. iptables Dry Run

Initial cluster state:

```text
Service:
order-service ClusterIP 10.96.10.20 port 80

Endpoints:
10.244.1.10:8080
10.244.2.15:8080
10.244.3.21:8080
```

kube-proxy creates rules:

```text
If packet dst=10.96.10.20:80
  choose endpoint
  DNAT to endpoint PodIP:8080
```

Request arrives:

```text
Client -> 10.96.10.20:80
```

Kernel processing:

```text
1. Packet enters node network stack.

2. NAT table checks Service rules.

3. Packet matches order-service ClusterIP rule.

4. Rule jumps to order-service chain.

5. Probability rule selects endpoint B.

6. Endpoint rule changes destination:
   10.96.10.20:80 -> 10.244.2.15:8080

7. Packet routes to backend Pod.

8. conntrack remembers mapping for this connection.

9. Return traffic is translated correctly.
```

Picture:

```text
10.244.1.5:45678 -> 10.96.10.20:80
                     |
                     v
              iptables DNAT
                     |
                     v
10.244.1.5:45678 -> 10.244.2.15:8080
```

The app does not know this happened.

---

# 13. IPVS Mode Mental Model

IPVS stands for IP Virtual Server.

It is a Linux kernel load balancing feature.

In IPVS mode, kube-proxy programs virtual services and real servers.

Mental model:

```text
Virtual Service = Kubernetes Service IP:port
Real Servers    = backend Pod IPs:ports
```

ASCII:

```text
IPVS table

Virtual Service:
10.96.10.20:80
   |
   +--> Real Server 10.244.1.10:8080
   +--> Real Server 10.244.2.15:8080
   +--> Real Server 10.244.3.21:8080
```

This looks more like a real load balancer table.

Instead of walking many iptables chains, the kernel can use IPVS load-balancing structures.

High-level packet flow:

```text
Packet dst=10.96.10.20:80
        |
        v
IPVS virtual service lookup
        |
        v
scheduler chooses real server
        |
        v
packet forwarded to Pod
```

IPVS supports different scheduling algorithms.

Common ones:

```text
rr  = round robin
lc  = least connection
sh  = source hashing
```

Memory hook:

```text
iptables = if-this-then-that chains
IPVS     = kernel load balancer table
```

---

# 14. IPVS Dry Run

Same Service:

```text
ClusterIP: 10.96.10.20
Port:      80
Backends:
  10.244.1.10:8080
  10.244.2.15:8080
  10.244.3.21:8080
```

kube-proxy programs IPVS:

```text
Virtual service:
10.96.10.20:80

Real servers:
10.244.1.10:8080
10.244.2.15:8080
10.244.3.21:8080
```

Request:

```text
Client -> 10.96.10.20:80
```

Kernel:

```text
1. Packet hits node networking.

2. IPVS sees virtual service 10.96.10.20:80.

3. IPVS scheduler chooses real server.

4. Destination becomes selected backend Pod.

5. Packet is routed to Pod.

6. Connection state keeps future packets consistent.
```

ASCII:

```text
Client Packet
dst=10.96.10.20:80
      |
      v
+--------------------------+
| IPVS Virtual Service     |
| 10.96.10.20:80           |
+------------+-------------+
             |
             | choose backend
             v
+--------------------------+
| Real Server              |
| 10.244.2.15:8080         |
+--------------------------+
```

Think of IPVS as a built-in kernel load balancer that kube-proxy configures.

---

# 15. iptables vs IPVS

Both modes solve the same Service problem.

```text
Service IP -> backend Pod IP
```

But internal implementation differs.

```text
iptables:
  rules and chains
  NAT-based random selection
  can become large with many Services/endpoints

IPVS:
  virtual services and real servers
  kernel load-balancing engine
  supports scheduling algorithms
  usually better fit for large Service tables
```

ASCII comparison:

```text
iptables

Packet
  |
  v
Rule chain
  |
  v
Rule chain
  |
  v
Endpoint rule
  |
  v
Pod


IPVS

Packet
  |
  v
Virtual service lookup
  |
  v
Scheduler chooses real server
  |
  v
Pod
```

Simple interview answer:

```text
iptables mode uses netfilter NAT rules generated by kube-proxy.
IPVS mode uses Linux IPVS kernel load balancing tables generated by kube-proxy.
Both are programmed by kube-proxy and both forward Service traffic to Pod endpoints.
```

Do not claim IPVS is always faster in every cluster.

The real answer depends on scale, kernel, CNI, conntrack, traffic pattern, and configuration.

---

# 16. ClusterIP Service Flow

ClusterIP is internal Service access.

Example:

```text
payment-service calls order-service
```

Flow:

```text
payment Pod
   |
   | DNS: order-service -> 10.96.10.20
   v
Service ClusterIP
   |
   | kube-proxy rules
   v
order Pod endpoint
```

ASCII:

```text
Namespace: shop

payment-service Pod
10.244.1.5
   |
   | http://order-service
   v
CoreDNS
   |
   | returns 10.96.10.20
   v
kube-proxy programmed rules
   |
   v
order-service Pod
10.244.2.15:8080
```

Spring Boot code:

```java
@Service
public class PaymentClient {
    private final RestTemplate restTemplate;

    public PaymentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OrderDto getOrder(String orderId) {
        return restTemplate.getForObject(
            "http://order-service/api/orders/" + orderId,
            OrderDto.class
        );
    }
}
```

The Java code uses a stable Service name.

Kubernetes handles the unstable Pod IPs.

This is the big developer value.

---

# 17. NodePort Service Flow

NodePort exposes a Service on every node IP at a fixed port.

Example:

```text
NodeIP:30080 -> Service -> Pod
```

YAML:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: order-service-nodeport
spec:
  type: NodePort
  selector:
    app: order-service
  ports:
    - port: 80
      targetPort: 8080
      nodePort: 30080
```

Traffic:

```text
External Client
   |
   | http://node-2-ip:30080
   v
NodePort rule on node-2
   |
   v
Service backend selection
   |
   v
Pod maybe on node-1/node-2/node-3
```

ASCII:

```text
Internet / office network
        |
        v
Node-2 IP:30080
        |
        v
kube-proxy rule
        |
        +--> Pod on Node-1
        +--> Pod on Node-3
```

Important:

```text
NodePort exists on every node.
Backend Pod may be on any node.
```

This is why NodePort can create an extra network hop if the chosen backend is on a different node.

---

# 18. LoadBalancer Service Flow

In cloud Kubernetes, a LoadBalancer Service usually creates an external cloud load balancer.

Flow:

```text
External client
   |
   v
Cloud Load Balancer
   |
   v
NodePort on Kubernetes nodes
   |
   v
kube-proxy Service rules
   |
   v
Backend Pod
```

ASCII:

```text
User Browser
    |
    v
Cloud LB
    |
    +--> Node-1:nodePort
    +--> Node-2:nodePort
    +--> Node-3:nodePort
             |
             v
       kube-proxy rules
             |
             v
       Pod endpoint
```

Mental model:

```text
LoadBalancer Service = cloud entry + Kubernetes Service routing
```

The cloud load balancer usually does not know every Pod directly in basic setups.

It sends traffic to nodes.

The node then uses kube-proxy rules to select a backend.

Some CNIs and cloud integrations can optimize this and route more directly, but the classic mental model is:

```text
Cloud LB -> NodePort -> Service -> Pod
```

---

# 19. externalTrafficPolicy Mental Model

For NodePort and LoadBalancer Services, `externalTrafficPolicy` matters.

Two common values:

```text
Cluster
Local
```

`Cluster`:

```text
Traffic can be forwarded to any ready backend Pod in the cluster.
May lose original client source IP due to SNAT.
Can balance across all endpoints.
```

`Local`:

```text
Traffic only goes to local node endpoints.
Preserves client source IP.
Nodes without local endpoints should not receive traffic.
```

ASCII:

```text
externalTrafficPolicy: Cluster

Client
  |
  v
Node-1
  |
  +--> Pod on Node-1
  +--> Pod on Node-2
  +--> Pod on Node-3


externalTrafficPolicy: Local

Client
  |
  v
Node-1
  |
  +--> only Pod on Node-1
```

Production choice:

```text
Need source IP? Consider Local.
Need easier cluster-wide balancing? Cluster is common.
```

Wrong setting can cause confusing traffic behavior.

---

# 20. Session Affinity

By default, Service load balancing is not sticky.

Requests from the same client may reach different Pods.

Kubernetes Service supports:

```yaml
sessionAffinity: ClientIP
```

Mental model:

```text
Same client IP -> same backend for some time
```

ASCII:

```text
Client A
  |
  +--> Pod 1
  +--> Pod 1
  +--> Pod 1

Client B
  |
  +--> Pod 2
  +--> Pod 2
```

Use carefully.

For Spring Boot microservices, prefer stateless application design:

```text
Session data in Redis/database/token
not in local JVM memory
```

Bad pattern:

```java
private final Map<String, Cart> localSessionCart = new HashMap<>();
```

If Service sends next request to another Pod, cart disappears.

Better:

```text
JWT for identity
Redis for shared session/cart state
Database for source of truth
```

Kubernetes can provide stickiness, but application architecture should not depend blindly on one Pod.

---

# 21. Connection Tracking

Service NAT depends heavily on Linux connection tracking.

`conntrack` remembers flows.

Example first packet:

```text
10.244.1.5:45678 -> 10.96.10.20:80
```

Translated:

```text
10.244.1.5:45678 -> 10.244.2.15:8080
```

conntrack remembers:

```text
For this connection, Service IP maps to Pod 10.244.2.15.
```

So later packets in the same TCP connection keep going to the same backend.

ASCII:

```text
New connection
   |
   v
Choose backend
   |
   v
Store mapping in conntrack
   |
   v
Future packets reuse mapping
```

Production issue:

```text
High connection churn can fill conntrack table.
```

Symptoms may include:

```text
Random connection failures
DNS failures
Service timeouts
Packets dropped
```

Debug direction:

```bash
conntrack -S
sysctl net.netfilter.nf_conntrack_max
dmesg | grep conntrack
```

Do not treat Service routing as magic.

It uses real kernel tables with real limits.

---

# 22. Hairpin Traffic

Hairpin traffic happens when a Pod calls a Service and the selected backend is itself or another Pod on the same node in a way that traffic loops through Service NAT.

Example:

```text
Pod A calls order-service
Service selects Pod A itself
```

ASCII:

```text
Pod A
  |
  | call Service IP
  v
kube-proxy rules
  |
  | choose backend
  v
Pod A
```

This can be valid.

But the network path may need hairpin handling depending on CNI and bridge setup.

Production symptom if broken:

```text
Pod can call other Pods through Service
but cannot call Service when backend resolves to itself
```

Debug mindset:

```text
Direct Pod IP works.
Service IP sometimes fails.
Failure depends on selected endpoint.
```

This is not a Java bug.

It is a Service NAT / CNI / hairpin behavior issue.

---

# 23. Service Without Endpoints

This is one of the most common Kubernetes networking bugs.

Service exists:

```bash
kubectl get svc order-service
```

Pods exist:

```bash
kubectl get pods
```

But traffic fails.

Check endpoints:

```bash
kubectl get endpoints order-service
kubectl get endpointslice -l kubernetes.io/service-name=order-service
```

Broken:

```text
ENDPOINTS: <none>
```

Why?

```text
Service selector does not match Pod labels
Pods not Ready
Wrong targetPort
Pods in wrong namespace
```

ASCII:

```text
Service selector:
app = order-service

Pods:
app = order

Result:
No match
No endpoints
No traffic
```

Mental model:

```text
Service without endpoints is a signboard pointing to nobody.
```

Before blaming kube-proxy, check selectors and readiness.

---

# 24. targetPort Mistake

Service port and targetPort are different.

```yaml
ports:
  - port: 80
    targetPort: 8080
```

Means:

```text
Clients call Service on port 80.
Backend Pod receives traffic on port 8080.
```

Spring Boot default:

```text
server.port=8080
```

Wrong YAML:

```yaml
ports:
  - port: 80
    targetPort: 9090
```

Result:

```text
Service routes to Pod IP:9090
But Spring Boot listens on 8080
Connection refused or timeout
```

ASCII:

```text
Client
  |
  v
Service 10.96.10.20:80
  |
  v
Pod 10.244.2.15:9090
  |
  v
Nothing listening
```

Debug:

```bash
kubectl describe svc order-service
kubectl get endpoints order-service
kubectl exec -it <pod> -- netstat -tulpn
kubectl logs <pod>
```

Mental model:

```text
Service port is the front door.
targetPort is the room where app actually listens.
```

---

# 25. Readiness And kube-proxy

kube-proxy routes to endpoints created from ready Pods.

If Spring Boot starts but readiness fails, Service should not send normal traffic to it.

Flow:

```text
Pod starts
  |
  v
Spring Boot starts
  |
  v
Readiness probe fails
  |
  v
Endpoint not ready
  |
  v
kube-proxy rules exclude it from normal traffic
```

ASCII:

```text
Pods:
Pod A Ready     -> endpoint included
Pod B NotReady  -> endpoint excluded
Pod C Ready     -> endpoint included

Service routes:
A, C
not B
```

Spring Boot Actuator:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
```

Probe:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
```

Production lesson:

```text
Bad readiness = bad load balancing.
```

If readiness always passes too early, traffic hits unprepared Pods.

If readiness always fails, Service has no endpoints.

---

# 26. kube-proxy Is Not CoreDNS

CoreDNS and kube-proxy solve different parts.

```text
CoreDNS:
  service name -> ClusterIP

kube-proxy:
  ClusterIP -> Pod IP
```

ASCII:

```text
Client wants:
http://order-service

Step 1: DNS
order-service -> 10.96.10.20

Step 2: Service routing
10.96.10.20 -> 10.244.2.15
```

If DNS fails:

```text
curl: could not resolve host
```

If kube-proxy/Service routing fails:

```text
DNS resolves, but connection times out/refused
```

Debug separation:

```bash
nslookup order-service
curl http://10.96.10.20
kubectl get endpoints order-service
```

Mental model:

```text
DNS gives address.
kube-proxy makes address useful.
```

Never mix these two in interviews.

---

# 27. kube-proxy Is Not CNI

CNI and kube-proxy also solve different problems.

```text
CNI:
  Pod-to-Pod networking across nodes

kube-proxy:
  Service virtual IP to endpoint routing
```

ASCII:

```text
Pod A -> Pod B direct IP
        |
        v
CNI network handles routing


Pod A -> Service IP
        |
        v
kube-proxy chooses Pod B
        |
        v
CNI network carries packet to Pod B
```

Both are needed in classic clusters.

If direct Pod IP communication fails, suspect CNI.

If Pod IP works but Service IP fails, suspect Service/kube-proxy/endpoints/rules.

Debug logic:

```text
1. Can Pod A reach Pod B by Pod IP?
   No -> CNI/routing/network policy issue.

2. Can Pod A resolve Service DNS?
   No -> CoreDNS issue.

3. Can Pod A reach Service IP?
   No -> endpoints/kube-proxy/iptables/IPVS/NetworkPolicy.
```

This layered thinking prevents random debugging.

---

# 28. NetworkPolicy Interaction

NetworkPolicy can block traffic even if kube-proxy routes correctly.

Flow:

```text
Service selects backend.
kube-proxy DNATs packet to Pod IP.
NetworkPolicy/CNI enforcement may allow or deny.
```

ASCII:

```text
Client Pod
  |
  v
Service IP
  |
  v
kube-proxy DNAT
  |
  v
Backend Pod IP
  |
  v
NetworkPolicy says DENY
```

Symptoms:

```text
Service exists
Endpoints exist
kube-proxy rules exist
But traffic times out
```

Debug:

```bash
kubectl get networkpolicy -A
kubectl describe networkpolicy <name>
```

Mental model:

```text
kube-proxy chooses where traffic should go.
NetworkPolicy may decide whether traffic is allowed.
```

Do not blame kube-proxy before checking policies.

---

# 29. Production Story: Wrong Selector

Incident:

```text
order-service deployment is running.
Service DNS resolves.
Clients get timeout.
```

Objects:

```yaml
Deployment labels:
  app: order

Service selector:
  app: order-service
```

Debug:

```bash
kubectl get pods --show-labels
kubectl describe svc order-service
kubectl get endpoints order-service
```

Output:

```text
Endpoints: <none>
```

Root cause:

```text
Service has no matching Pods.
kube-proxy has no backend list to program.
```

Fix:

```yaml
selector:
  app: order
```

Lesson:

```text
A Service is not connected by name.
A Service is connected by labels.
```

ASCII:

```text
Service name: order-service
Pod name: order-service-abc

Names look related
but selector labels decide reality.
```

---

# 30. Production Story: kube-proxy Not Running

Symptoms:

```text
Pods can run.
DNS may resolve.
Services fail from one node or many nodes.
```

Check:

```bash
kubectl -n kube-system get ds kube-proxy
kubectl -n kube-system get pods -l k8s-app=kube-proxy -o wide
kubectl -n kube-system logs <kube-proxy-pod>
```

Possible causes:

```text
kube-proxy DaemonSet missing
kube-proxy crashlooping
wrong config mode
missing kernel modules for IPVS
iptables command compatibility issue
node-level permissions problem
```

ASCII:

```text
Node-1 kube-proxy OK
  Service works from Node-1

Node-2 kube-proxy DOWN
  Service rules stale/missing
  Service fails from Node-2
```

Mental model:

```text
Because kube-proxy is per-node, Service failures can be node-specific.
```

Debug from multiple nodes.

Do not test from only one Pod and assume cluster-wide truth.

---

# 31. Production Story: Stale Rules

Sometimes endpoints change fast during rollout.

Expected:

```text
Old Pods removed.
New Pods added.
kube-proxy updates node rules.
```

If rules are stale or update is delayed, traffic may go to dead endpoints temporarily.

Symptoms:

```text
Intermittent connection refused
Only during rollout
Some nodes affected
```

Debug:

```bash
kubectl get endpointslice -l kubernetes.io/service-name=order-service -o wide
kubectl -n kube-system logs <kube-proxy-pod>
iptables-save | grep KUBE-SVC
ipvsadm -Ln
```

Dry run:

```text
1. Pod old-A terminating.
2. EndpointSlice removes old-A.
3. kube-proxy watches update.
4. Node rules update.
5. Traffic stops going to old-A.
```

If step 3 or 4 is unhealthy, stale routing appears.

Production lesson:

```text
Kubernetes networking is eventually updated by watchers.
Watch health matters.
```

---

# 32. Production Story: Conntrack Exhaustion

High-scale Java service creates many short-lived TCP connections.

Example bad pattern:

```text
New HTTP client per request
No connection pooling
Huge outbound fanout
```

Result:

```text
Conntrack table fills.
New connections fail randomly.
```

Symptoms:

```text
Random timeout to Services
DNS timeouts
Node dmesg shows conntrack table full
Problem worse under load
```

Java anti-pattern:

```java
public String call() {
    RestTemplate rt = new RestTemplate(); // bad if repeatedly created without pooling
    return rt.getForObject("http://inventory-service/items", String.class);
}
```

Better mindset:

```text
Use connection pooling.
Reuse HTTP clients.
Tune timeouts.
Reduce unnecessary connection churn.
```

Spring Boot with WebClient/HTTP client pooling is safer than uncontrolled per-request clients.

ASCII:

```text
Too many new connections
        |
        v
conntrack entries explode
        |
        v
kernel drops/denies new flows
        |
        v
Service appears flaky
```

Lesson:

```text
Some Kubernetes networking incidents are caused by application connection behavior.
```

---

# 33. Debugging Service Traffic: Layer By Layer

Use this order:

```text
1. Does Service exist?
2. Does DNS resolve?
3. Does Service selector match Pods?
4. Are endpoints present?
5. Are Pods Ready?
6. Is targetPort correct?
7. Can client reach Pod IP directly?
8. Does NetworkPolicy allow traffic?
9. Is kube-proxy running on client node?
10. Are iptables/IPVS rules present?
11. Is conntrack healthy?
12. Is CNI routing healthy?
```

Commands:

```bash
kubectl get svc order-service
kubectl describe svc order-service

kubectl get pods -l app=order-service -o wide
kubectl get endpoints order-service
kubectl get endpointslice -l kubernetes.io/service-name=order-service

kubectl exec -it <client-pod> -- nslookup order-service
kubectl exec -it <client-pod> -- curl -v http://order-service
kubectl exec -it <client-pod> -- curl -v http://10.96.10.20
kubectl exec -it <client-pod> -- curl -v http://<pod-ip>:8080

kubectl -n kube-system get pods -l k8s-app=kube-proxy -o wide
kubectl -n kube-system logs <kube-proxy-pod>
```

Node-level:

```bash
iptables-save | grep KUBE-SERVICES
ipvsadm -Ln
conntrack -S
```

Mindset:

```text
Do not guess.
Follow name -> Service IP -> endpoints -> node rules -> Pod.
```

---

# 34. Spring Boot Example: Correct Service Design

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

Client code:

```java
@RestController
public class CheckoutController {
    private final WebClient webClient;

    public CheckoutController(WebClient.Builder builder) {
        this.webClient = builder
            .baseUrl("http://order-service")
            .build();
    }

    @GetMapping("/checkout/{orderId}")
    public Mono<String> checkout(@PathVariable String orderId) {
        return webClient.get()
            .uri("/api/orders/{id}", orderId)
            .retrieve()
            .bodyToMono(String.class);
    }
}
```

Mental model:

```text
Java uses Service name.
CoreDNS resolves name.
kube-proxy maps ClusterIP to ready Pods.
CNI carries packet.
```

---

# 35. Spring Boot Timeout Mindset

Kubernetes Service routing does not replace application timeouts.

Bad:

```java
RestTemplate restTemplate = new RestTemplate();
```

Without proper timeouts, calls can hang longer than expected.

Production mindset:

```text
Service discovery gives address.
kube-proxy routes packet.
Your app still needs:
  - connection timeout
  - read timeout
  - retry policy
  - circuit breaker
  - bulkhead
```

ASCII:

```text
Checkout Service
   |
   | call order-service
   v
Kubernetes Service routing works
   |
   v
Order Pod slow due DB
   |
   v
Checkout threads block
```

Kubernetes networking cannot fix slow downstream logic.

Use Resilience4j style thinking:

```text
Timeout       -> avoid infinite wait
Retry         -> handle transient failure carefully
CircuitBreaker-> stop hammering broken dependency
Bulkhead      -> protect thread pools
```

Memory hook:

```text
kube-proxy connects services.
It does not make dependencies healthy.
```

---

# 36. Scaling Impact

As Services and endpoints grow, kube-proxy has more work.

Events that trigger updates:

```text
Pod created
Pod deleted
Pod readiness changes
Service created
Service changed
EndpointSlice updated
Node changes
```

iptables mode:

```text
More Services/endpoints can mean larger rule sets.
Rule synchronization can become heavier.
```

IPVS mode:

```text
Maintains virtual service tables.
Often chosen for large clusters.
Still depends on sync health and kernel behavior.
```

ASCII:

```text
Many Services
Many Pods
Frequent rollouts
      |
      v
EndpointSlice churn
      |
      v
kube-proxy sync pressure
      |
      v
possible latency/stale update symptoms
```

Production checks:

```bash
kubectl -n kube-system logs <kube-proxy-pod> | grep -i sync
kubectl get endpointslice -A | wc -l
```

Do not over-optimize early.

But know the shape of the scaling problem.

---

# 37. kube-proxy Replacement: eBPF Note

Some modern CNIs can replace kube-proxy behavior using eBPF.

Examples include advanced dataplanes where Service load balancing is handled by eBPF programs instead of iptables/IPVS kube-proxy rules.

Mental model:

```text
Classic:
kube-proxy -> iptables/IPVS -> Service routing

eBPF dataplane:
CNI/eBPF programs -> Service routing
```

ASCII:

```text
Classic Node
  kube-proxy
    |
    v
  iptables/IPVS


eBPF Node
  CNI agent
    |
    v
  eBPF programs in kernel
```

For this chapter, learn kube-proxy first.

Why?

```text
It teaches the core Service routing problem.
```

Once you understand:

```text
Service IP is virtual
Node dataplane translates to Pod IP
```

Then eBPF replacement becomes easy to understand.

It changes implementation, not the fundamental problem.

---

# 38. Common Misconceptions

```text
Misconception 1:
Service IP belongs to a Pod.
Correct:
Service IP is virtual and implemented through node dataplane rules.

Misconception 2:
kube-proxy is always in the traffic path as a process.
Correct:
In iptables/IPVS modes, kernel handles traffic after kube-proxy programs rules.

Misconception 3:
CoreDNS load balances traffic.
Correct:
CoreDNS resolves Service name to ClusterIP. kube-proxy/dataplane routes to endpoints.

Misconception 4:
Service name must match Deployment name.
Correct:
Service selector labels decide endpoints.

Misconception 5:
IPVS removes all networking problems.
Correct:
IPVS changes load balancing implementation but conntrack, CNI, policies, readiness, and app behavior still matter.

Misconception 6:
If Pod is Running, Service sends traffic to it.
Correct:
Normal Service traffic should use ready endpoints.
```

---

# 39. Interview Questions

## What is kube-proxy?

kube-proxy is a node-level Kubernetes component that watches Services and EndpointSlices and programs the node networking dataplane so traffic sent to Service IPs is forwarded to backend Pod IPs.

## Does kube-proxy proxy every request?

Not usually in modern modes. In iptables and IPVS modes, kube-proxy programs kernel rules or tables. The Linux kernel handles the packet forwarding. The old userspace mode involved kube-proxy more directly in traffic forwarding.

## What is the difference between Service and EndpointSlice?

A Service provides stable identity such as ClusterIP and DNS name. EndpointSlices contain the current backend Pod IPs and ports selected by the Service. kube-proxy uses this endpoint information to program routing.

## How does ClusterIP work?

ClusterIP is a virtual IP. Pods send traffic to that IP. Node-level dataplane rules programmed by kube-proxy translate the destination from Service IP and port to a selected backend Pod IP and targetPort.

## What is iptables mode?

In iptables mode, kube-proxy writes Linux netfilter NAT rules. Packets matching a Service IP and port are routed through Kubernetes chains and DNATed to backend Pod IPs.

## What is IPVS mode?

In IPVS mode, kube-proxy programs Linux IPVS virtual services and real servers. IPVS acts as a kernel load balancer that chooses backend endpoints for Service traffic.

## iptables vs IPVS?

iptables uses rule chains and NAT rules. IPVS uses kernel virtual server tables and load-balancing algorithms. Both are programmed by kube-proxy and both map Service traffic to Pod endpoints.

## Why can a Service have no endpoints?

Usually because the Service selector does not match Pod labels, Pods are not Ready, Pods are in a different namespace, or target configuration is wrong.

## What is targetPort?

targetPort is the port on the backend Pod where the application actually listens. Service port is what clients call; targetPort is where traffic is sent on the Pod.

## How do you debug Service traffic failure?

Check Service, DNS, selectors, endpoints, Pod readiness, targetPort, direct Pod IP connectivity, NetworkPolicy, kube-proxy health, iptables/IPVS rules, conntrack, and CNI routing.

---

# 40. Cheat Sheet

```text
kube-proxy              = node component that programs Service routing
Service                 = stable virtual identity for Pods
ClusterIP               = virtual internal Service IP
EndpointSlice           = current backend Pod IPs and ports
iptables mode           = Service routing via netfilter NAT rules
IPVS mode               = Service routing via Linux kernel virtual server
DNAT                    = change destination IP/port
conntrack               = kernel connection tracking table
CoreDNS                 = Service name -> ClusterIP
CNI                     = Pod-to-Pod network routing
NetworkPolicy           = traffic allow/deny rules
targetPort              = backend Pod application port
NodePort                = Service exposed on every node at fixed port
LoadBalancer            = external LB + NodePort/Service routing
externalTrafficPolicy   = controls cluster-wide vs local endpoint routing
sessionAffinity         = optional client-IP stickiness
```

Debug flow:

```text
Name resolves?
  |
  v
Service exists?
  |
  v
Endpoints exist?
  |
  v
Pods ready?
  |
  v
targetPort correct?
  |
  v
NetworkPolicy allows?
  |
  v
kube-proxy healthy?
  |
  v
iptables/IPVS rules present?
  |
  v
conntrack/CNI healthy?
```

---

# 41. One Picture To Remember

```text
                       CLIENT POD
                           |
                           | http://order-service
                           v
                    +---------------+
                    | CoreDNS       |
                    | name -> IP    |
                    +-------+-------+
                            |
                            v
                    Service ClusterIP
                     10.96.10.20:80
                            |
                            | virtual IP
                            v
              +-------------------------------+
              | Node Dataplane                |
              | programmed by kube-proxy      |
              |                               |
              | iptables mode: NAT chains     |
              | IPVS mode: virtual servers    |
              +---------------+---------------+
                              |
                              | DNAT / select backend
                              v
                    +----------------+
                    | Endpoint Pod   |
                    | 10.244.2.15    |
                    | port 8080      |
                    +----------------+

Rule:

CoreDNS gives the Service IP.
kube-proxy makes the Service IP reach real Pods.
CNI carries traffic between Pods and nodes.
Readiness decides which Pods should receive traffic.
```

---

# 42. Final Production Checklist

```text
[ ] I understand Service IP is virtual.
[ ] I understand kube-proxy runs on every node.
[ ] I understand kube-proxy watches Services and EndpointSlices.
[ ] I understand CoreDNS resolves name to ClusterIP.
[ ] I understand kube-proxy/dataplane maps ClusterIP to Pod IP.
[ ] I understand iptables mode uses NAT rule chains.
[ ] I understand IPVS mode uses kernel virtual server tables.
[ ] I understand targetPort must match application listening port.
[ ] I understand Service selector labels decide endpoints.
[ ] I understand NotReady Pods should not receive normal Service traffic.
[ ] I understand NodePort exposes a port on every node.
[ ] I understand LoadBalancer often routes through NodePort to nodes.
[ ] I understand externalTrafficPolicy Cluster vs Local.
[ ] I understand conntrack can become a bottleneck.
[ ] I can debug Service traffic layer by layer.
```

---

# 43. Final Memory Hook

Do not memorize kube-proxy as:

```text
iptables vs IPVS
```

Remember it as:

```text
Service IP is fake.
Pod IPs are real.
kube-proxy teaches every node how to translate fake stable IPs into real changing Pod IPs.
```

Final sentence:

```text
Kubernetes Service networking is not magic load balancing; it is node-level packet translation continuously updated from Service and EndpointSlice state.
```
