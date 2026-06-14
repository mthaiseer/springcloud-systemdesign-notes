# 001_Why_Kubernetes_Exists.md

# Why Kubernetes Exists

## Goal

Do not memorize Kubernetes.

Understand the production problems that forced companies to create Kubernetes.

If you understand the problems, Kubernetes becomes an obvious solution.

---

# Before Kubernetes

Imagine a Java Spring Boot application.

```text
Customer Service
```

Runs on:

```text
Server-1
```

Everything works.

Then traffic grows.

```text
100 Users
   ↓
1000 Users
   ↓
10000 Users
```

Now one server is not enough.

---

# First Solution: Bigger Server

```text
+------------------+
|   Spring Boot    |
|                  |
|   4 CPU          |
|   8 GB RAM       |
+------------------+
```

Upgrade to:

```text
+------------------+
|   Spring Boot    |
|                  |
|   32 CPU         |
|   128 GB RAM     |
+------------------+
```

Problem:

- Expensive
- Has limits
- Single point of failure

This is called Vertical Scaling.

---

# Second Solution: Multiple Servers

```text
             Users
               |
               v

      +----------------+
      | Load Balancer  |
      +----------------+

        /     |      \
       v      v       v

   Server1 Server2 Server3
```

Better.

But new problems appear.

---

# Real Production Problems

## Problem 1: Deployment

You have:

```text
Server-1
Server-2
Server-3
Server-4
Server-5
```

Deploying manually:

```bash
ssh server1
scp app.jar

ssh server2
scp app.jar

...
```

Painful.

---

## Problem 2: Server Crash

```text
Server-3
   X
 CRASH
```

Who restarts the application?

Human?

Not scalable.

---

## Problem 3: Traffic Spike

Morning:

```text
100 Requests/sec
```

Black Friday:

```text
10000 Requests/sec
```

Need more instances.

How?

Who creates them?

Who removes them later?

---

## Problem 4: Resource Waste

```text
Server
 ├─ App-A uses 10%
 ├─ App-B uses 15%
 └─ 75% unused
```

Money wasted.

---

## Problem 5: Service Discovery

Application A calls Application B.

```text
Order Service
      |
      v
Payment Service
```

Payment Service moves.

IP changes.

Everything breaks.

---

## Problem 6: Load Balancing

```text
User
 |
 v

Which instance?
```

```text
Payment-1
Payment-2
Payment-3
Payment-4
```

Need automatic routing.

---

# Docker Helped

Docker solved packaging.

```text
Code
 +
Dependencies
 +
Runtime
 =
Docker Image
```

Run anywhere.

But Docker did not solve:

- Scheduling
- Auto healing
- Scaling
- Service discovery
- Rolling deployment

---

# Enter Kubernetes

Kubernetes is a giant automation platform.

Think:

```text
Developer
    |
    v

Desired State

"I need 5 instances"
"I need auto healing"
"I need scaling"

    |
    v

Kubernetes

    |
    v

Actual State
```

Kubernetes continuously matches:

```text
Desired State
      =
Actual State
```

---

# Kubernetes Mental Model

You never say:

```text
Start container now
```

Instead:

```text
Keep 5 containers alive
```

Kubernetes ensures it.

---

# Example

You declare:

```yaml
replicas: 3
```

Reality:

```text
Pod-1
Pod-2
Pod-3
```

If Pod-2 dies:

```text
Pod-1
Pod-2  X
Pod-3
```

Kubernetes sees mismatch.

Creates:

```text
Pod-1
Pod-3
Pod-4
```

Automatically.

---

# Real World Analogy

Hotel Manager

You own a hotel.

Need:

```text
100 Rooms Ready
```

Guests leave.

Guests arrive.

Employees clean rooms.

Manager only checks:

```text
Required = 100
Current  = 97
```

Creates 3 more available rooms.

Kubernetes works similarly.

---

# Java Spring Boot Example

Application:

```java
@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(
            OrderApplication.class,
            args
        );
    }
}
```

Docker:

```dockerfile
FROM eclipse-temurin:21

COPY target/order.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
```

Kubernetes:

```yaml
apiVersion: apps/v1
kind: Deployment

metadata:
  name: order-service

spec:
  replicas: 3
```

Instead of managing servers, you describe desired state.

---

# What Kubernetes Actually Automates

## Deployment

```text
Version-1
    |
    v
Version-2
```

Without downtime.

---

## Healing

```text
Pod Crash
    |
    v
Auto Restart
```

---

## Scaling

```text
100 RPS
   ↓
3 Pods

10000 RPS
   ↓
30 Pods
```

---

## Networking

```text
Order Service
      |
      v
Payment Service
```

Without hardcoded IPs.

---

## Resource Allocation

```text
CPU
RAM
Storage
Network
```

Shared efficiently.

---

# Why Google Built Kubernetes

Google ran:

- Search
- Gmail
- Maps
- YouTube

Millions of containers.

Humans cannot manage that scale.

Google created Borg.

Kubernetes is inspired by Borg.

---

# One Picture To Remember

```text
                   PROBLEMS

Deployment
Scaling
Healing
Networking
Discovery
Resource Usage

                       |
                       v

                KUBERNETES

                       |
                       v

Desired State
      |
      v
Continuous Reconciliation
      |
      v
Actual State

Always Matching
```

---

# Interview Answer

Question:

Why Kubernetes exists?

Answer:

"Kubernetes exists to automate deployment, scaling, recovery, networking, and lifecycle management of containerized applications. Instead of manually managing servers and containers, developers declare the desired state and Kubernetes continuously works to keep the actual state matching the desired state."
