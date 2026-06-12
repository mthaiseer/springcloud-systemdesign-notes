
# 005_CGroups_Resource_Control.md

# Cgroups Resource Control – Understanding First Edition With ASCII Diagrams

## What You Will Learn

After this chapter you will understand:

- Why namespaces are not enough
- Why one bad service can crash a server
- How Linux prevents resource starvation
- How Docker resource limits work
- How Kubernetes requests and limits work
- Why OOMKilled happens
- How Spring Boot behaves under memory pressure
- How cgroups appear in production incidents

---

# Mental Model

Namespaces answer:

```text
What can a process SEE?
```

Cgroups answer:

```text
What can a process USE?
```

```text
Container
+--------------------------------+
| Namespaces                     |
| -> Process View                |
| -> Network View                |
| -> Filesystem View             |
+--------------------------------+

+--------------------------------+
| Cgroups                        |
| -> CPU Limit                   |
| -> Memory Limit                |
| -> IO Limit                    |
| -> Process Limit               |
+--------------------------------+
```

Simple rule:

```text
Namespaces = Isolation

Cgroups = Resource Control
```

---

# Why This Problem Exists

Imagine a server:

```text
64 GB RAM
16 CPU Cores
```

Running:

```text
Gateway
Order Service
Payment Service
Redis
Kafka Consumer
```

Without cgroups:

```text
Server
+------------------------------------+
| Gateway             1 GB           |
| Redis               4 GB           |
| Kafka               2 GB           |
| Order Service       2 GB           |
| Payment Service    55 GB !!!       |
+------------------------------------+
```

One service can consume everything.

Result:

```text
Memory Exhausted
      |
Kernel Under Pressure
      |
Entire Node Unstable
```

This is the exact problem cgroups solve.

---

# Real World Analogy

Apartment Building

```text
Apartment Building
+--------------------------------+
| Family A -> 2 Parking Slots    |
| Family B -> 2 Parking Slots    |
| Family C -> 2 Parking Slots    |
+--------------------------------+
```

Without limits:

```text
Family A occupies all slots
```

With limits:

```text
Everyone gets fair usage
```

Cgroups are resource reservations for processes.

---

# Core Concept 1: CPU Control

Problem:

```text
One service consumes all CPU.
```

Without limits:

```text
CPU Usage

Payment Service
██████████████████████ 95%

Gateway
█

Order
█
```

With limits:

```text
CPU Allocation

Payment -> Max 40%
Gateway -> Max 30%
Order   -> Max 30%
```

Mental model:

```text
Cgroup CPU Limit
        |
Maximum CPU Share
```

Spring Boot relevance:

Infinite loops, bad algorithms, large batch jobs and Kafka consumers can monopolize CPU.

---

# Core Concept 2: Memory Control

Most important cgroup.

```text
Container Memory Limit
       =
Maximum RAM
```

Example:

```text
Container Limit = 2 GB
```

Memory usage:

```text
0 MB
 |
500 MB
 |
1000 MB
 |
1500 MB
 |
1900 MB
 |
2000 MB  <-- LIMIT
 |
OOMKilled
```

Visual:

```text
Container
+--------------------------+
| Heap        1200 MB      |
| Cache        300 MB      |
| Threads      200 MB      |
| Native       300 MB      |
+--------------------------+

Total = 2 GB
```

---

# Spring Boot Memory Anatomy

Many developers think:

```text
Container Memory = JVM Heap
```

Wrong.

Real picture:

```text
Container Limit
2 GB
 |
 +-- Heap
 |     1200 MB
 |
 +-- Metaspace
 |      150 MB
 |
 +-- Thread Stacks
 |      100 MB
 |
 +-- Direct Buffers
 |      250 MB
 |
 +-- Native Memory
 |      300 MB
```

This explains many OOMKilled incidents.

---

# Core Concept 3: Process Limits

Imagine:

```text
Process
   |
Creates 10000 Children
```

Without limits:

```text
Server becomes unusable
```

With cgroups:

```text
Process Count Limit
```

Protection against runaway applications.

---

# Core Concept 4: IO Control

Problem:

```text
Backup Job
     |
Consumes All Disk Bandwidth
```

Result:

```text
Database becomes slow
```

With cgroups:

```text
Container A -> 100 MB/s
Container B -> 100 MB/s
```

Fair usage.

---

# Internal Flow

```text
docker run
     |
Container Runtime
     |
Linux Kernel
     |
Create Cgroup
     |
Attach Process
     |
Enforce Limits
```

Memory request:

```text
Spring Boot
      |
Needs Memory
      |
Kernel Checks Limit
      |
Enough ?
  |        |
 Yes      No
  |        |
Allocate  OOM Kill
```

---

# Docker Connection

Docker relies heavily on cgroups.

Without cgroups:

```text
Container
      =
Isolation Only
```

With cgroups:

```text
Container
      =
Isolation
      +
Resource Control
```

Example:

```text
Host
+--------------------------------+
| Container A -> 2 GB            |
| Container B -> 4 GB            |
| Container C -> 1 CPU           |
+--------------------------------+
```

---

# Kubernetes Connection

Kubernetes scheduling depends on resources.

```yaml
requests:
  memory: 1Gi
  cpu: 500m

limits:
  memory: 2Gi
  cpu: 1
```

Mental model:

```text
Request
   =
Guaranteed

Limit
   =
Maximum
```

Visual:

```text
Memory

0------1GB------2GB

Request   Limit
```

Scheduler uses request.

Kernel enforces limit.

---

# Production Failure Story #1

Black Friday Traffic

```text
100 RPS
1000 RPS
10000 RPS
```

Payment service memory leak:

```text
1 GB
2 GB
4 GB
8 GB
```

Without cgroups:

```text
Entire Node Crashes
```

With cgroups:

```text
Payment Service Killed

Gateway Survives
Order Survives
Redis Survives
```

---

# Production Failure Story #2

CPU Throttling

```text
Container CPU Limit = 1 Core
```

Traffic spike:

```text
Needs 4 cores
Allowed 1 core
```

Result:

```text
Latency increases
```

This is called throttling.

---

# Production Failure Story #3

OOMKilled

```text
Container Limit = 512 MB
```

JVM:

```text
Heap = 450 MB
```

Traffic spike:

```text
More Threads
More Buffers
```

Result:

```text
OOMKilled
```

---

# Debugging Mindset

When service becomes slow:

```text
Slow Service
     |
CPU High?
     |
Memory High?
     |
OOMKilled?
     |
CPU Throttled?
```

Always investigate resources first.

---

# Performance Tradeoffs

Benefits:

```text
Predictability
Isolation
Multi-tenancy
Safety
```

Costs:

```text
Throttling
OOMKills
Artificial ceilings
```

| Area | No Cgroups | With Cgroups |
|-------|------------|--------------|
| Stability | Low | High |
| Predictability | Low | High |
| Multi-tenancy | Poor | Good |
| Safety | Low | High |

---

# Security Considerations

Without limits:

```text
Malicious App
      |
Consumes CPU
Consumes Memory
      |
Denial Of Service
```

With cgroups:

```text
Blast Radius Reduced
```

---

# Common Mistakes

1. Namespace = Cgroup
2. Heap = Container Memory
3. Requests = Limits
4. More CPU fixes everything
5. Ignore native memory
6. Ignore thread stacks
7. No memory limits
8. No CPU limits
9. Ignore throttling
10. Ignore OOMKilled events

---

# System Design Connection

```text
Microservice
      |
Container
      |
Namespaces
      |
Cgroups
      |
Node
      |
Cluster
```

Resource isolation is one reason microservices can safely share infrastructure.

---

# Strong Interview Answers

Q: Why are namespaces not enough?

A:

```text
Namespaces isolate visibility.
Cgroups control resource usage.
```

Q: Why does OOMKilled happen?

A:

```text
Application exceeded memory cgroup limit.
Kernel killed it to protect the host.
```

Q: Requests vs Limits?

A:

```text
Requests -> Scheduling

Limits -> Maximum consumption
```

---

# One Picture To Remember

```text
Container
+------------------------------------+
| Namespaces                         |
|   -> What I Can See                |
|                                    |
| Cgroups                            |
|   -> CPU                           |
|   -> Memory                        |
|   -> IO                            |
|   -> Process Count                 |
+------------------------------------+
                 |
                 v
        Stable Production System
```
