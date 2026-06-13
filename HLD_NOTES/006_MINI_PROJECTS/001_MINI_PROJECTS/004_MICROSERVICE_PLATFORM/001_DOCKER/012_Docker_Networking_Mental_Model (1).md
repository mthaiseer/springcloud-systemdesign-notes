# 012_Docker_Networking_Mental_Model.md

# Docker Networking Mental Model - Understanding First Edition With ASCII Diagrams

## Goal of This Chapter

This chapter is written for senior Java backend engineers, Spring Boot developers, system design interviews, Docker, Kubernetes, and production engineering.

The goal is to build a strong **mental model** of how networking works in Docker — not to memorize commands.

You will understand:
- Why containers need their own network stack
- How containers talk to each other and the outside world
- The role of different network drivers
- Real Spring Boot microservice communication patterns
- How this connects to Kubernetes

```text
Learning Goal
     |
     +--> Container network isolation
     +--> Virtual networking plumbing
     +--> Communication patterns
     +--> Production troubleshooting mindset
```

---

# Mental Model

Docker networking gives each container its own **isolated network namespace** while still allowing controlled communication.

```text
Host Machine
+-------------------------------+
| Host Network Namespace        |
|  eth0 (real network card)     |
+-------------------------------+
          ^
          | veth pair
          |
Container A                  Container B
+-------------+          +-------------+
| Namespace   |          | Namespace   |
| eth0 (172.x)|          | eth0 (172.x)|
| Spring Boot |          | Spring Boot |
+-------------+          +-------------+
```

Each container thinks it has its own full network stack.

---

# Why Networking Matters in Docker

Without proper networking:

- Containers can't talk to each other
- Apps can't be reached from outside
- Microservices become impossible to coordinate

```text
Isolated Container          Connected Containers
+---------------+           +---------------+
| Can't reach   |    <-->   | Service A     |
| anything      |           | Service B     |
+---------------+           +---------------+
```

---

# Core Building Blocks

## 1. Network Namespace

Every container gets its own:

```text
Container Network Namespace
+--------------------------+
| Interfaces (eth0)        |
| IP Address               |
| Routing Table            |
| iptables / Firewall      |
| Port Space (0-65535)     |
+--------------------------+
```

## 2. veth Pairs (Virtual Ethernet)

Docker connects the container to the host using a virtual cable.

```text
Host Namespace          Container Namespace
     veth-host  <====>     eth0
     (one end)             (other end)
```

Visual:

```text
          veth pair (virtual cable)
Host <=====================> Container
```

## 3. Docker Bridge (docker0)

Default network acts like a virtual switch.

```text
Docker Bridge Network
       +-----------------+
       |   docker0       |
       |  (Virtual Switch)|
       +-----------------+
         /         \
   Container A   Container B
   172.17.0.2    172.17.0.3
```

All containers on the same bridge can reach each other by IP.

---

# Port Publishing

To make a container accessible from outside:

```text
docker run -p 8080:8080 myapp
```

Flow:

```text
External World
      |
Host Port 8080 (iptables DNAT)
      |
   veth pair
      |
Container Port 8080
```

---

# User-Defined Networks & Service Discovery

Better than default bridge:

```text
docker network create backend
```

Then:

```text
Container A (order-service)     Container B (payment-service)
         |                                |
      backend network (with built-in DNS)
```

Inside payment-service:

```java
String url = "http://order-service:8080/orders";  // name resolution works!
```

---

# Spring Boot in Practice

### 1. Order Service

```java
// OrderController.java
@RestController
@RequestMapping("/orders")
public class OrderController {

    @GetMapping
    public String getOrders() {
        return "Orders from " + System.getenv("HOSTNAME");
    }
}
```

### 2. Payment Service calling Order Service

```java
// PaymentService.java
@Service
public class PaymentService {

    private final RestTemplate restTemplate;

    public PaymentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String processPayment() {
        // Uses Docker service name
        String response = restTemplate.getForObject(
            "http://order-service:8080/orders", 
            String.class
        );
        return "Payment processed with: " + response;
    }
}
```

### docker-compose.yml Example

```yaml
version: '3.8'
services:
  order-service:
    build: ./order-service
    networks:
      - backend

  payment-service:
    build: ./payment-service
    networks:
      - backend
    depends_on:
      - order-service

networks:
  backend:
    driver: bridge
```

With this setup, services discover each other using container names.

---

# Docker Network Drivers Comparison

```text
Driver     | Isolation | Multi-Host | Use Case                     | Performance
-----------|-----------|------------|------------------------------|------------
bridge     | Good      | No         | Local development            | Medium
host       | None      | No         | High throughput              | Highest
overlay    | Good      | Yes        | Swarm / multi-node           | Good
macvlan    | Good      | Yes        | Direct network access        | High
none       | Max       | No         | Complete isolation           | -
```

---

# Production Failure Stories

**Story 1: Hardcoded IPs**

Developer used `172.17.0.5` → broke when containers restarted.

**Fix**: Always use service names on user-defined networks.

**Story 2: Port Conflicts**

Multiple services trying to bind to 8080 on host.

**Fix**: Use `-p` selectively or internal networks only.

---

# Connection to Kubernetes

Docker networking is single-host focused.

Kubernetes uses **CNI** (Container Network Interface) for cluster-wide flat networking.

But the mental model is similar: Pods get IPs, Services provide discovery.

---

# Debugging Mindset

Ask these questions:

1. Which network is the container attached to?
2. What is its IP? (`ip addr` inside container)
3. Can it ping other containers by name?
4. Are ports published correctly?
5. Is DNS working? (`cat /etc/hosts`)

---

# Strong Interview Answers

**Q: How do two containers communicate?**

A: On the same user-defined bridge network, they use Docker's built-in DNS to resolve names to IPs. Traffic goes through veth pairs and the bridge.

**Q: What is port publishing?**

A: It creates NAT rules on the host so external traffic on a host port is forwarded to the container's port.

---

# Final Cheat Sheet

```text
Key Mental Model:
Container = Own network namespace
Communication = veth + bridge + DNS
External access = Port publishing (-p)
Best practice = User-defined networks + service names
```

```text
One Big Picture
Docker Host
+-------------------------------------------+
| Host Namespace (eth0)                     |
+-------------------------------------------+
         |               |
      veth pair      veth pair
         |               |
   +-----+-----+   +-----+-----+
   | Container |   | Container |
   | order     |   | payment   |
   +-----------+   +-----------+
         \             /
        backend bridge network
```

This mental model will help you understand Kubernetes networking much faster.
