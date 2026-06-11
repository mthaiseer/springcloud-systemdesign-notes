# 005_CGroups_Resource_Control

# Control Groups (CGroups) Resource Control

## Why This Matters

Linux Control Groups (CGroups) are the resource management foundation of Docker and Kubernetes.

Namespaces answer:

```text
Who can see what?
```

CGroups answer:

```text
Who can consume how much?
```

Without CGroups, one container could:

- Consume all CPU
- Consume all memory
- Exhaust disk I/O
- Starve other applications

Every production container platform relies on CGroups for predictable resource allocation.

---

## Mental Model

Think of a company budget.

```text
Company
 |
 + Team A -> $1000
 + Team B -> $2000
 + Team C -> $500
```

Teams cannot exceed budgets.

CGroups work similarly:

```text
Server
 |
 + Container A -> 1 CPU, 1GB RAM
 + Container B -> 2 CPU, 4GB RAM
 + Container C -> 0.5 CPU, 512MB RAM
```

CGroups enforce budgets.

---

## Core Concepts

### What Is A CGroup?

A kernel mechanism for grouping processes and controlling resource consumption.

Resources:

- CPU
- Memory
- Disk I/O
- Network
- PIDs

Why Needed:

Multi-tenant workloads.

Advantages:

- Fairness
- Isolation
- Predictability

Disadvantages:

- Misconfigured limits cause throttling.

Interview Explanation:

CGroups provide resource accounting and enforcement.

---

### CPU Control

Limits CPU usage.

Example:

```text
Container A -> 1 CPU
Container B -> 2 CPU
```

Kernel scheduler enforces quotas.

Advantages:

Prevents CPU starvation.

Disadvantages:

Overly aggressive limits reduce throughput.

---

### Memory Control

Limits memory usage.

Example:

```text
Container -> 512 MB
```

If exceeded:

```text
OOM Kill
```

Advantages:

Protects host.

Disadvantages:

Application may terminate.

---

### PID Limits

Limits process count.

Example:

```text
Max Processes = 100
```

Protects host from fork bombs.

---

### I/O Control

Regulates disk access.

Benefits:

- Fairness
- Stable latency

---

## Internal Architecture

```text
Container Process
       |
     CGroup
       |
+------+------+------+------+
| CPU | MEM | IO | PID |
+------+------+------+------+
       |
Linux Scheduler
       |
Linux Kernel
```

---

## Step-by-Step Flow

```text
docker run
      |
Create CGroup
      |
Attach Process
      |
Apply Limits
      |
Monitor Usage
      |
Enforce Policies
```

---

## Data Structures Used

```java
class CGroupConfig {

    long memoryLimit;

    int cpuQuota;

    int pidLimit;
}
```

```java
class ResourceUsage {

    long memoryUsed;

    long cpuTime;

    int processCount;
}
```

Kernel maintains accounting structures per cgroup hierarchy.

---

## Algorithms Used

### CPU Scheduling

Uses Linux Completely Fair Scheduler.

```text
Process
   |
CGroup Weight
   |
CPU Allocation
```

### Memory Accounting

```text
Allocation
   |
Track Usage
   |
Check Limit
   |
Allow / Reject
```

---

## Production Implementation

Docker:

```bash
docker run --memory=1g --cpus=2 app
```

Kubernetes:

```yaml
resources:
  requests:
    cpu: "500m"
    memory: "512Mi"
  limits:
    cpu: "1"
    memory: "1Gi"
```

---

## Java Code Examples

### Memory Pressure Demo

```java
public class MemoryPressure {

    public static void main(String[] args) {

        byte[][] data = new byte[1000][];

        for(int i=0;i<1000;i++) {

            data[i] =
                new byte[1024*1024];

            System.out.println(
                "Allocated " + i + " MB"
            );
        }
    }
}
```

Dry Run:

```text
Allocate Memory
     |
Reach Limit
     |
OOM Kill
```

---

### CPU Stress Demo

```java
public class CpuStress {

    public static void main(String[] args) {

        while(true) {

            Math.sqrt(System.nanoTime());
        }
    }
}
```

Observe throttling when CPU quota applied.

---

## Spring Boot Example

```java
@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {

        return "UP";
    }
}
```

Container:

```text
CPU = 500m
RAM = 512MB
```

Spring Boot must operate within limits.

---

## Spring Cloud Example

```text
Gateway
User
Order
Payment
```

Assign limits:

```text
Gateway 1 CPU

User 2 CPU

Order 2 CPU

Payment 1 CPU
```

Avoid resource contention.

---

## Kubernetes Example

```yaml
resources:
  requests:
    cpu: "500m"
  limits:
    cpu: "1"
```

Scheduler uses requests.

Kernel enforces limits through CGroups.

---

## Sequence Diagram (ASCII)

```text
Container Start
      |
Create CGroup
      |
Attach Process
      |
Track Usage
      |
Limit Exceeded?
      |
Yes -> Throttle/Kill
```

---

## Request Lifecycle

```text
Client
   |
Container
   |
Spring Boot
   |
CPU Usage
Memory Usage
   |
CGroup Enforcement
```

---

## Failure Scenarios

### OOM Kill

```text
Memory > Limit
```

Kernel terminates process.

---

### CPU Throttling

```text
CPU > Quota
```

Performance degradation.

---

### PID Exhaustion

```text
Too Many Processes
```

New processes denied.

---

## Debugging Guide

View Stats:

```bash
docker stats
```

Inspect:

```bash
docker inspect container
```

Check Memory:

```bash
cat memory.current
```

CPU:

```bash
cat cpu.stat
```

Kubernetes:

```bash
kubectl top pod
```

---

## Performance Considerations

Good:

```text
Reasonable limits
Realistic requests
```

Bad:

```text
Tiny memory limit
Aggressive CPU quota
```

---

## Scalability Considerations

CGroups enable safe multi-tenancy.

```text
100 Containers
      |
Fair Resource Distribution
```

---

## CAP Tradeoffs

CGroups are local kernel mechanisms.

CAP theorem does not apply directly.

---

## Common Interview Questions

### Q1 What are CGroups?
A: Linux resource control mechanism.

### Q2 Why needed?
A: Prevent resource abuse.

### Q3 CPU control?
A: Quotas and shares.

### Q4 Memory control?
A: Memory accounting and limits.

### Q5 What happens when memory exceeded?
A: OOM Kill.

### Q6 What is CPU throttling?
A: Scheduler restricts CPU time.

### Q7 Docker uses CGroups?
A: Yes.

### Q8 Kubernetes uses CGroups?
A: Yes.

### Q9 Difference between namespaces and cgroups?
A: Isolation vs resource control.

### Q10 What is memory.current?
A: Current memory usage.

### Q11 What is cpu.stat?
A: CPU accounting metrics.

### Q12 What is PID limit?
A: Maximum process count.

### Q13 Why requests and limits?
A: Scheduling and enforcement.

### Q14 What causes OOMKilled pod?
A: Memory limit exceeded.

### Q15 What scheduler enforces CPU?
A: Linux CFS.

### Q16 Can CPU be oversubscribed?
A: Yes.

### Q17 Can memory be oversubscribed?
A: Risky.

### Q18 Why monitor cgroups?
A: Capacity planning.

### Q19 Production best practice?
A: Set requests and limits.

### Q20 Explain cgroups in one minute.
A: Kernel mechanism for resource accounting and enforcement.

---

## Strong Interview Answers

### What Are CGroups?

CGroups are Linux kernel primitives that group processes and enforce resource policies such as CPU, memory, disk I/O, and PID limits. Docker and Kubernetes rely on CGroups to prevent one workload from monopolizing host resources.

### Namespaces vs CGroups

Namespaces isolate visibility.

CGroups isolate consumption.

Containers require both.

---

## Real World Example

E-commerce platform:

```text
Gateway -> 1 CPU

User -> 2 CPU

Order -> 2 CPU

Payment -> 1 CPU
```

Traffic spike:

```text
User Service
Consumes More CPU
```

CGroups prevent starvation of other services.

---

## FAANG/System Design Discussion

Topics:

- Multi-tenancy
- Noisy neighbor problem
- Resource isolation
- OOM kills
- Kubernetes requests and limits
- Capacity planning

Senior-level explanation:

```text
Namespaces = Isolation

CGroups = Resource Governance
```

---

## Production Checklist

- Set CPU requests
- Set CPU limits
- Set memory requests
- Set memory limits
- Monitor OOM kills
- Track throttling
- Alert on saturation
- Capacity planning

---

## Key Takeaways

1. CGroups manage resource consumption.
2. Containers rely on CGroups.
3. CPU quotas prevent starvation.
4. Memory limits prevent host exhaustion.
5. OOM kill occurs when memory exceeded.
6. Kubernetes uses CGroups underneath.
7. Requests guide scheduling.
8. Limits guide enforcement.
9. Namespaces isolate visibility.
10. CGroups isolate resource usage.

---

# One-Page Cheat Sheet

```text
Namespaces
   -> Isolation

CGroups
   -> Resource Control

CPU
   -> Quotas/Shares

Memory
   -> Limits

PID
   -> Process Count

OOM Kill
   -> Memory Exceeded

Docker
   -> Uses CGroups

Kubernetes
   -> Uses CGroups
```

---

# Last-Minute Interview Revision

```text
CGroups = Resource Budgets

CPU -> Throttle

Memory -> OOM Kill

PID -> Process Limits

Namespaces -> Visibility

CGroups -> Consumption

Docker + Kubernetes rely on both
```

---

# Mental Models Table

| Concept | Mental Model |
|----------|-------------|
| CGroup | Budget |
| CPU Quota | Spending Limit |
| Memory Limit | Storage Capacity |
| PID Limit | Headcount Limit |
| OOM Kill | Budget Exhausted |
| Scheduler | Finance Manager |
| Container | Team |
| Host | Company |
