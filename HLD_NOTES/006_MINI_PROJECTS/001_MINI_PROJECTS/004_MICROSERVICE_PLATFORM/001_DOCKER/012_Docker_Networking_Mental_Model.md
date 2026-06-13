# 012_Docker_Networking_Mental_Model.md

# Docker Networking Mental Model - Understanding First Edition With ASCII Diagrams

## Goal of This Chapter

This chapter is written for senior Java backend engineers, Spring Boot developers, system design interviews, Docker, Kubernetes, and production engineering.

The goal is to build a strong **mental model** of how Docker networking works under the hood — not to memorize commands.

You will understand:
- How containers get isolated yet connected
- Why different network drivers exist and when to use them
- How port publishing, DNS, and service discovery work
- Real Spring Boot microservices communication patterns
- Common production pitfalls and how to avoid them
- How Docker networking connects to Kubernetes CNI

```text
Learning Goal
     |
     +--> Deep mental model of network namespaces
     +--> Understand virtual networking plumbing
     +--> Master bridge, host, overlay drivers
     +--> Apply to Spring Boot microservices
     +--> Prepare for Kubernetes networking
     +--> Explain confidently in interviews
```

---

# Mental Model: The Big Picture

Docker Networking = **Network Namespace Isolation** + **Virtual Ethernet Plumbing** + **Controlled Connectivity**.

Think of it like this:

```text
Physical Datacenter                  Docker Host (Your Machine / Node)
+-------------------+               +-----------------------------------+
| Multiple Buildings|               | Multiple Containers               |
|   (Servers)       |               |   (Isolated Apps)                 |
+-------------------+               +-----------------------------------+
         |                                       |
   Physical Routers/Switches               Docker Networks + veth Pairs
```

Each container lives in its own "apartment" (network namespace) with its own doors (interfaces), address (IP), and rules.

---

# Core Building Block: Linux Network Namespace

This is the foundation.

```text
Host Network Namespace
+---------------------------------------------+
| eth0 (physical NIC)                         |
| IP: 192.168.1.45                            |
| iptables rules                              |
| Routing table                               |
+---------------------------------------------+

Container Network Namespace (isolated)
+---------------------------------------------+
| eth0 (virtual interface)                    |
| IP: 172.17.0.3                              |
| Its own iptables                            |
| Its own routing table                       |
+---------------------------------------------+
```

**Key Insight**: The container thinks it has the entire network stack to itself.

---

# How Containers Connect to the Host: veth Pairs

Docker uses **virtual Ethernet (veth)** pairs to bridge namespaces.

```text
Host Namespace                  Container Namespace
+----------------+             +-------------------+
| veth0 (host)   |  <------->  | eth0 (container)  |
| IP (bridge)    |             | IP 172.17.0.x     |
+----------------+             +-------------------+
```

Visual flow when container starts:

```text
1. Create veth pair
2. Move one end into container namespace
3. Attach other end to docker0 bridge
4. Assign IP to container eth0
5. Setup NAT rules for outbound traffic
```

---

# Default Bridge Network Deep Dive

When you do `docker run` without `--network`, the container joins the **bridge** network.

```text
Docker Host
+-------------------------------------------------+
| docker0 Bridge (virtual switch)                 |
| IP: 172.17.0.1                                  |
+-------------------------------------------------+
          |                |               |
       vethA             vethB           vethC
          |                |               |
   Container A       Container B     Container C
   172.17.0.2        172.17.0.3      172.17.0.4
```

All containers on the same bridge can talk to each other directly by IP.

**Spring Boot Example**:

Container A (Order Service) can call:

```bash
curl http://172.17.0.3:8080/payment
```

But hardcoding IPs is bad — use service names instead.

---

# Port Publishing Explained

```text
External World
      |
   Host Port 8080 (published)
      |
   iptables DNAT rule
      |
   Container eth0:8080 (internal)
```

Detailed flow:

```text
docker run -p 8080:8080 myapp
          |
   Creates iptables rule:
   DNAT tcp --dport 8080 -> 172.17.0.2:8080
```

ASCII representation:

```text
Internet / Browser
     |
   Host (port 8080) ← published
     |
   docker0 Bridge
     |
   Container (port 8080)
```

---

# User-Defined Bridge Networks (Best Practice)

Instead of default bridge, create your own:

```bash
docker network create backend-network
```

```text
Custom Bridge Network: backend-network
+-------------------------------------------+
| docker0-like bridge                       |
+-------------------------------------------+
          |               |
     Order Service    Payment Service
     (order-svc)      (payment-svc)
```

Now service discovery works beautifully:

```text
Inside Payment Service:
curl http://order-svc:8080/api/orders
```

---

# Spring Boot in Practice

### 1. Simple Spring Boot Application

```java
// OrderServiceApplication.java
@SpringBootApplication
@RestController
@RequestMapping("/api/orders")
public class OrderServiceApplication {

    @GetMapping
    public ResponseEntity<String> getOrders() {
        return ResponseEntity.ok("Orders from " + 
            InetAddress.getLocalHost().getHostName() + 
            " on network: " + getContainerIP());
    }

    private String getContainerIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
```

### 2. Inter-Service Communication

```java
// PaymentService.java
@Service
public class PaymentService {

    private final RestTemplate restTemplate;

    public PaymentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String processPayment() {
        // Using Docker service name - no hardcoded IP!
        String url = "http://order-svc:8080/api/orders";
        return restTemplate.getForObject(url, String.class);
    }
}
```

### 3. docker-compose.yml (Production-like)

```yaml
version: '3.8'

services:
  order-svc:
    build: ./order-service
    container_name: order-svc
    networks:
      - backend
    ports:
      - "8080:8080"   # Only expose what is needed externally

  payment-svc:
    build: ./payment-service
    container_name: payment-svc
    networks:
      - backend
    depends_on:
      - order-svc

networks:
  backend:
    driver: bridge
    driver_opts:
      com.docker.network.bridge.name: "backend-br"
```

---

# Docker Network Drivers Comparison (Deep)

| Driver      | Isolation | Performance | Multi-Host | Use Case for Spring Boot                     | Diagram Insight |
|-------------|-----------|-------------|------------|---------------------------------------------|-----------------|
| bridge      | High      | Good        | No         | Local development, simple microservices     | Virtual switch |
| host        | None      | Excellent   | No         | High throughput services, low latency       | Same namespace |
| overlay     | High      | Good        | Yes        | Docker Swarm multi-node                     | VXLAN overlay  |
| macvlan     | High      | Excellent   | Yes        | Legacy systems needing real IP/MAC          | Direct NIC     |
| none        | Maximum   | N/A         | No         | Security critical isolated workloads        | No network     |

---

# Host Network Mode

```text
Container using --network host
+-----------------------------------+
| Uses Host's eth0 directly         |
| Same IP as host                   |
| No port publishing needed         |
| No namespace isolation            |
+-----------------------------------+
```

**When to use**:
- Performance critical Spring Boot APIs
- When you need to bind to specific host ports without mapping

---

# Overlay Network (Preview for Swarm/K8s)

```text
Multi-Host Overlay
+---------------+     +---------------+
| Node 1        |     | Node 2        |
| Order Service |<--->| Payment Service|
+---------------+     +---------------+
         \_______________ VXLAN Tunnel _____________/
```

---

# Production Failure Stories

### Story 1: Hardcoded IPs Break Everything
**Symptom**: Works in local docker-compose, fails in Kubernetes.
**Root Cause**: Used `172.17.0.x` IPs.
**Fix**: Always use service names + DNS.

### Story 2: Port Exhaustion on Single Host
**Symptom**: Can't run multiple instances of same Spring Boot app.
**Fix**: Use user-defined networks + internal ports only. Publish only one entrypoint.

### Story 3: Slow Inter-Service Calls
**Symptom**: High latency between services.
**Cause**: Default bridge + many iptables rules.
**Fix**: Optimize with host network or tune MTU.

### Story 4: DNS Resolution Failures
**Symptom**: `curl order-svc` works sometimes, not always.
**Fix**: Use user-defined networks. Restart Docker if DNS cache issues.

---

# Connection to Kubernetes

```text
Docker Bridge (Single Host)
     |
     v
Kubernetes CNI (Calico / Flannel / Cilium)
     |
     +--> Pod Network (flat, cluster-wide)
     +--> Services (ClusterIP, NodePort, LoadBalancer)
     +--> DNS (CoreDNS)
```

**Key Difference**:
Docker networking is host-centric. Kubernetes is cluster-centric.

---

# Advanced Topics

- **iptables & NAT Deep Dive**
- **MTU Issues in Overlay**
- **Network Policies** (Kubernetes equivalent)
- **Service Mesh** (Istio, Linkerd) for advanced traffic management

---

# Debugging Docker Networking

Essential commands:

```bash
docker network ls
docker network inspect backend
docker exec order-svc ip addr show
docker exec order-svc cat /etc/hosts
docker exec order-svc ping payment-svc
iptables -t nat -L -v -n
```

---

# Strong Interview Answers

**Q1: How does a container access the internet?**  
A: Outbound traffic is masqueraded (SNAT) by iptables on the host. The container's traffic appears to come from the host's IP.

**Q2: Why use user-defined networks instead of default bridge?**  
A: Better isolation, built-in DNS service discovery, and cleaner architecture for microservices.

**Q3: Difference between bridge and host network?**  
A: Bridge provides isolation with virtual interfaces. Host shares the host's network stack for maximum performance but zero isolation.

**Q4: How would you design networking for 10 Spring Boot microservices?**  
A: Use docker-compose with user-defined bridge networks for local dev. In production, rely on Kubernetes Services + CNI.

---

# Final Cheat Sheet

```text
Docker Networking = 
  Network Namespace + veth + Bridge + DNS + NAT
```

**Best Practices**:
- Always use user-defined bridge networks
- Communicate via service names
- Publish only necessary ports
- Avoid hardcoded IPs
- Use host network only when performance is critical
- Test inter-service calls early

---

# One Big Picture To Remember

```text
Docker Host Machine
+===================================================================+
| Host Network Namespace                                            |
|   eth0 (Real NIC - 192.168.1.100)                                 |
+===================================================================+
           |
     docker0 / Custom Bridge
           |
   +-------+-------+-------+
   |       |       |       |
 vethA   vethB   vethC   vethD
   |       |       |       |
OrderSvc PaymentSvc UserSvc  DB
(8080)   (8081)   (8082)   (5432)
   |       |       |
Service Discovery via Docker DNS
   |
External Access via -p / Port Publishing
```

**Networking quality directly determines**:
- Microservices communication reliability
- Debugging experience
- Production scalability
- Ease of migration to Kubernetes
- Security posture

Master this mental model and container networking becomes intuitive.