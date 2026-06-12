# 004_Linux_Namespaces.md

# Linux Namespaces - Understanding First Edition With ASCII Diagrams

## Goal of This Chapter

This chapter is written for backend engineers, system design interviews, Docker, Kubernetes, Spring Boot, and cloud-native development.

The goal is NOT to become a Linux kernel developer.

The goal is to understand:

- Why namespaces exist
- What problem they solve
- How Docker uses them
- How Kubernetes uses them
- How they affect real production systems

```text
Learning Goal
     |
     +--> Understand Docker isolation
     +--> Understand Kubernetes Pods
     +--> Avoid localhost/container mistakes
     +--> Explain containers in interviews
```

---

# Mental Model

A container is not a virtual machine.

A container is simply:

```text
Container = Process + Isolation
```

Linux namespaces provide the isolation.

Think of a large office building.

```text
One Building
+--------------------------------------------------+
|                                                  |
|  +----------------+  +----------------+          |
|  | Team A Room    |  | Team B Room    |          |
|  | own desks      |  | own desks      |          |
|  | own whiteboard |  | own whiteboard |          |
|  +----------------+  +----------------+          |
|                                                  |
|  Shared: electricity, water, security, building  |
+--------------------------------------------------+
```

Namespaces work similarly.

```text
One Linux Host
+--------------------------------------------------+
|                  Linux Kernel                    |
|                                                  |
|  +----------------+  +----------------+          |
|  | Container A    |  | Container B    |          |
|  | private view   |  | private view   |          |
|  +----------------+  +----------------+          |
|                                                  |
|  Shared: same kernel                             |
+--------------------------------------------------+
```

All containers share:

- one Linux kernel

But each container gets its own view of resources.

```text
Same Kernel
    |
    +--> Container A sees its own processes
    +--> Container B sees its own network
    +--> Container C sees its own filesystem
```

---

# Why Namespaces Were Created

Before namespaces:

Application A could see Application B.

```text
Without Namespaces

Host
+--------------------------------------------------+
| Process Table                                    |
|                                                  |
| postgres                                         |
| redis                                            |
| nginx                                            |
| java-app-A                                       |
| java-app-B                                       |
+--------------------------------------------------+

Every app can potentially observe the same global world.
```

Problems:

- process visibility
- port conflicts
- hostname conflicts
- filesystem visibility
- security concerns

Namespaces solve these by creating private views.

```text
With Namespaces

Host Kernel
+--------------------------------------------------+
|                                                  |
|  Container A View        Container B View        |
|  +---------------+       +---------------+       |
|  | java-app-A    |       | java-app-B    |       |
|  | localhost     |       | localhost     |       |
|  | /app          |       | /app          |       |
|  +---------------+       +---------------+       |
|                                                  |
+--------------------------------------------------+
```

---

# The Six Namespaces That Matter Most

```text
Linux Namespaces Used By Containers

+-------------------+--------------------------------+
| Namespace         | What It Isolates               |
+-------------------+--------------------------------+
| PID               | Process list / process IDs      |
| Network           | IP, ports, routes, localhost    |
| Mount             | Filesystem view                 |
| UTS               | Hostname                        |
| IPC               | Shared memory, queues           |
| User              | UID/GID identity mapping        |
+-------------------+--------------------------------+
```

These are the ones Docker relies on heavily.

---

# PID Namespace

Problem:

Without isolation every process can see every other process.

Example host:

```text
Host Process World
+--------------------------------------------------+
| PID 1      systemd                               |
| PID 523    postgres                              |
| PID 711    redis                                 |
| PID 900    nginx                                 |
| PID 1200   java app                              |
+--------------------------------------------------+
```

Without PID namespace:

```text
Container sees:
systemd
postgres
redis
nginx
java app
```

With PID namespace:

```text
Container PID Namespace
+----------------------------+
| PID 1   java app           |
| PID 7   helper process     |
+----------------------------+
```

Mental model:

Every container gets its own process world.

```text
Host View                         Container View

PID 1200 java app  ----------->   PID 1 java app

Same process.
Different view.
```

Why PID 1 Matters

Inside a container the first process becomes PID 1.

For example:

- Spring Boot app
- nginx
- Redis
- Kafka broker

appears as PID 1.

```text
Container
+-------------------------------+
| PID 1                         |
|  |                            |
|  +--> Spring Boot Application |
+-------------------------------+
```

Interview takeaway:

PID namespace isolates process visibility.

---

# Network Namespace

This is the most important namespace for backend engineers.

Without it:

Every application shares the same network stack.

```text
Without Network Namespace

Host Network Stack
+--------------------------------------------------+
| 0.0.0.0:8080  -> App A                           |
| 0.0.0.0:8080  -> App B  CONFLICT                 |
+--------------------------------------------------+
```

With network namespace:

```text
Container A Network Namespace
+-----------------------------+
| localhost                   |
| eth0                        |
| port 8080                   |
+-----------------------------+

Container B Network Namespace
+-----------------------------+
| localhost                   |
| eth0                        |
| port 8080                   |
+-----------------------------+
```

No conflict.

Each container has its own network world.

```text
Container A localhost != Container B localhost != Host localhost
```

---

# Docker Networking Mental Model

```text
Request From Browser
       |
       v
Host Network
       |
       v
Docker Bridge / Docker Network
       |
       v
Container Network Namespace
       |
       v
Spring Boot App
```

A slightly more detailed picture:

```text
Host
+------------------------------------------------------+
|                                                      |
|  Browser/curl                                        |
|      |                                               |
|      v                                               |
|  localhost:8080                                      |
|      |                                               |
|      v                                               |
|  Docker Port Mapping                                 |
|      |                                               |
|      v                                               |
|  docker0 bridge                                      |
|      |                                               |
|      v                                               |
|  veth pair                                           |
|      |                                               |
|      v                                               |
|  Container eth0                                      |
|      |                                               |
|      v                                               |
|  Spring Boot :8080                                   |
|                                                      |
+------------------------------------------------------+
```

Important:

localhost inside a container means:

```text
the container itself
```

not the host machine.

This causes many beginner mistakes.

---

# Real Production Failure

Bad Spring Boot config:

```text
jdbc:postgresql://localhost:5432/orders
```

Postgres runs in another container.

Result:

```text
Connection refused
```

Why?

```text
App Container
+----------------------------------+
| Spring Boot                      |
| tries localhost:5432             |
|                                  |
| localhost points here            |
| not to PostgreSQL container      |
+----------------------------------+

Postgres Container
+----------------------------------+
| PostgreSQL :5432                 |
+----------------------------------+
```

Correct:

```text
jdbc:postgresql://postgres:5432/orders
```

Docker Compose mental model:

```text
Docker Network
+--------------------------------------------------+
|                                                  |
|  app container  ------DNS name------> postgres   |
|                                                  |
+--------------------------------------------------+
```

This single concept explains thousands of Docker networking issues.

---

# Mount Namespace

Mount namespace controls filesystem visibility.

Host:

```text
Host Filesystem
/
├── home
├── etc
├── var
├── usr
└── opt
```

Container:

```text
Container Filesystem
/
├── app
├── bin
├── etc
├── tmp
└── lib
```

The container sees its own filesystem view.

This is why Docker images work.

```text
Host Disk
    |
    +--> Docker image layers
            |
            +--> Container mount namespace
                    |
                    +--> container sees /
```

---

# Docker Image Connection

A Docker image becomes the filesystem seen by the container.

Spring Boot example:

```text
Docker Image Layers

+-----------------------------+
| Application JAR layer       |
+-----------------------------+
| JDK layer                   |
+-----------------------------+
| Base Linux layer            |
+-----------------------------+
```

Container sees:

```text
Container /
+-----------------------------+
| /app/app.jar                |
| /usr/bin/java               |
| /etc                        |
| /tmp                        |
+-----------------------------+
```

because of mount isolation.

---

# Real Production Failure: File Not Found

Developer says:

```text
File exists on host.
```

Application says:

```text
File not found.
```

Diagram:

```text
Host
+------------------------------+
| /home/user/config.yml        |
+------------------------------+

Container
+------------------------------+
| /app                         |
| /tmp                         |
| config.yml missing           |
+------------------------------+
```

Cause:

```text
File never mounted into the container.
```

Correct mental model:

```text
Host file is not automatically visible inside container.
You must mount it.
```

Understanding mount namespace immediately explains the problem.

---

# UTS Namespace

UTS namespace controls hostname.

Without it:

```text
All containers see same host hostname:

node-17
node-17
node-17
```

With it:

```text
Host hostname:
node-17

Container A hostname:
user-service

Container B hostname:
payment-service
```

Diagram:

```text
Linux Host
+--------------------------------------------------+
| Hostname: node-17                                |
|                                                  |
|  Container A              Container B            |
|  hostname=user-service    hostname=payment       |
+--------------------------------------------------+
```

Useful for:

- logs
- metrics
- observability
- debugging

---

# IPC Namespace

IPC means Inter Process Communication.

Think:

- shared memory
- message queues
- semaphores

Normally not a major topic for Spring Boot developers.

Just understand:

Container A should not interfere with Container B communication resources.

```text
IPC Namespace A
+------------------------------+
| Shared memory A              |
| Queue A                      |
+------------------------------+

IPC Namespace B
+------------------------------+
| Shared memory B              |
| Queue B                      |
+------------------------------+
```

---

# User Namespace

Security-focused namespace.

Without user namespace:

```text
Container root
      |
      v
Host root
```

Dangerous.

With user namespace:

```text
Container
+----------------------+
| root = UID 0         |
+----------------------+
          |
          | mapped to
          v
Host
+----------------------+
| normal user UID      |
| example: 100000      |
+----------------------+
```

The container thinks:

```text
I am root.
```

Host sees:

```text
Normal user.
```

Huge security improvement.

---

# How Docker Uses Namespaces

When you run:

```text
docker run app
```

Docker creates:

```text
+---------------------------+
| PID namespace             |
| Network namespace         |
| Mount namespace           |
| UTS namespace             |
| IPC namespace             |
| User namespace            |
+---------------------------+
```

Then starts the application process.

```text
docker run app
      |
      v
Docker / container runtime
      |
      v
Create namespace views
      |
      v
Start application process
      |
      v
Container is running
```

Important:

```text
Container is still just a process.
Not a VM.
```

---

# Kubernetes Connection

A Kubernetes Pod contains one or more containers.

Key idea:

Containers inside the same Pod share networking.

```text
Kubernetes Pod
+--------------------------------------------------+
| Shared Network Namespace                         |
|                                                  |
|  +------------------+    +------------------+    |
|  | App Container    |    | Envoy Sidecar    |    |
|  | localhost:8080   |    | localhost:15000  |    |
|  +------------------+    +------------------+    |
|                                                  |
+--------------------------------------------------+
```

Therefore:

```text
localhost works between containers inside a Pod.
```

This is why sidecars work.

Example:

```text
Application Container
        |
        | localhost
        v
Envoy Sidecar
```

Important consequence:

```text
Two containers in same Pod cannot use the same port.
```

Because they share one network namespace.

---

# Why Containers Are Lightweight

VM model:

```text
Physical Machine
      |
Hypervisor
      |
Virtual Hardware
      |
Guest OS Kernel
      |
Application
```

Container model:

```text
Physical Machine
      |
Linux Kernel
      |
Namespaced Process
      |
Application
```

No guest OS.

Result:

- less memory
- faster startup
- higher density

```text
VMs:
1 machine -> fewer workloads

Containers:
1 machine -> many isolated processes
```

---

# Performance Tradeoff

Container advantages:

```text
+ Fast startup
+ Low memory usage
+ High density
+ Good for microservices
```

Container limitations:

```text
- Shared kernel
- Weaker isolation than VMs
- Kernel bug can affect all containers
- Misconfiguration can break isolation
```

Architecture takeaway:

```text
Use containers for speed and density.
Use VMs when stronger isolation boundary is required.
```

---

# Common Mistakes

## Mistake 1: Container = VM

Wrong:

```text
Container = small virtual machine
```

Correct:

```text
Container = isolated Linux process
```

## Mistake 2: localhost means host machine

Wrong inside container:

```text
localhost = my laptop / host
```

Correct:

```text
localhost = current network namespace
```

## Mistake 3: Namespace and Cgroup are same

Wrong:

```text
Namespace = Cgroup
```

Correct:

```text
Namespace -> what process can see
Cgroup    -> what process can use
```

Diagram:

```text
Container
+------------------------------+
| Namespace: private view      |
| Cgroup: resource limits      |
+------------------------------+
```

---

# System Design Connection

When discussing:

- Microservices
- Docker
- Kubernetes
- Service Mesh
- Cloud Native
- Sidecars
- Multi-tenancy
- Container security

you are indirectly discussing namespaces.

```text
System Design
     |
     +--> Microservices
              |
              +--> Containers
                       |
                       +--> Namespaces
```

Namespaces are one of the foundations of modern platform engineering.

---

# Strong Interview Answer

What are Linux namespaces?

Linux namespaces are kernel features that provide isolated views of system resources. They allow processes to have separate process trees, network stacks, filesystems, hostnames, IPC resources, and user mappings while still sharing the same Linux kernel. Docker and Kubernetes use namespaces to provide container isolation.

Stronger backend-focused version:

Linux namespaces are the reason a container can behave as if it has its own machine while actually sharing the host kernel. PID namespaces isolate process visibility, network namespaces isolate localhost, ports, routes and interfaces, mount namespaces isolate filesystem views, UTS namespaces isolate hostnames, IPC namespaces isolate shared communication resources, and user namespaces isolate identity mapping. Docker creates these namespace views and then starts the application process inside them.

---

# Final Cheat Sheet

```text
PID Namespace
-> Process isolation
```

```text
Network Namespace
-> Network isolation
-> localhost is namespace-local
```

```text
Mount Namespace
-> Filesystem isolation
-> container sees its own /
```

```text
UTS Namespace
-> Hostname isolation
```

```text
IPC Namespace
-> Communication isolation
```

```text
User Namespace
-> Identity isolation
-> container root can map to non-root host user
```

```text
Container
=
Process
+
Namespaces
+
Cgroups
+
Filesystem
```

Most important takeaway:

```text
A container is not a small virtual machine.

A container is an isolated Linux process.
```

---

# One Picture To Remember

```text
Host Machine
+------------------------------------------------------+
|                    Linux Kernel                      |
|                                                      |
|  +-------------------+     +-------------------+     |
|  | Container A       |     | Container B       |     |
|  |                   |     |                   |     |
|  | PID view          |     | PID view          |     |
|  | Network view      |     | Network view      |     |
|  | Mount view        |     | Mount view        |     |
|  | Hostname view     |     | Hostname view     |     |
|  | User view         |     | User view         |     |
|  +-------------------+     +-------------------+     |
|                                                      |
+------------------------------------------------------+
```

That picture is the core of Linux namespaces.
