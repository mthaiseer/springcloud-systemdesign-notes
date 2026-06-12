# 004_Linux_Namespaces.md

# Linux Namespaces - Understanding First Edition

## Goal of This Chapter

This chapter is written for backend engineers, system design interviews, Docker, Kubernetes, Spring Boot, and cloud-native development.

The goal is NOT to become a Linux kernel developer.

The goal is to understand:

- Why namespaces exist
- What problem they solve
- How Docker uses them
- How Kubernetes uses them
- How they affect real production systems

---

# Mental Model

A container is not a virtual machine.

A container is simply:

Process + Isolation

Linux namespaces provide that isolation.

Think of a large office building.

Everyone shares:

- same building
- same electricity
- same water
- same security

But each team gets:

- its own room
- its own desks
- its own whiteboard

Namespaces work similarly.

All containers share:

- one Linux kernel

But each container gets its own view of resources.

---

# Why Namespaces Were Created

Before namespaces:

Application A could see Application B.

Problems:

- process visibility
- port conflicts
- hostname conflicts
- filesystem visibility
- security concerns

Namespaces solve these.

---

# The Six Namespaces That Matter Most

1. PID Namespace
2. Network Namespace
3. Mount Namespace
4. UTS Namespace
5. IPC Namespace
6. User Namespace

These are the ones Docker relies on heavily.

---

# PID Namespace

Problem:

Without isolation every process can see every other process.

Example:

Host:

- postgres
- redis
- nginx
- java app

Container:

Without PID namespace:

Container sees everything.

With PID namespace:

Container sees only its own processes.

Mental model:

Every container gets its own process world.

Why PID 1 Matters

Inside a container the first process becomes PID 1.

For example:

Spring Boot app

or

nginx

appears as PID 1.

Interview takeaway:

PID namespace isolates process visibility.

---

# Network Namespace

This is the most important namespace for backend engineers.

Without it:

Every application shares the same network stack.

Conflict:

App A uses 8080

App B uses 8080

Only one wins.

With network namespace:

Container A:

8080

Container B:

8080

No conflict.

Each container has its own network world.

---

# Docker Networking Mental Model

Application
    |
Container Network
    |
Docker Network
    |
Host Network
    |
Internet

Important:

localhost inside a container means:

the container itself

not the host machine.

This causes many beginner mistakes.

---

# Real Production Failure

Bad:

Spring Boot

jdbc:postgresql://localhost:5432/orders

Postgres runs in another container.

Result:

Connection refused.

Why?

localhost means the current container.

Correct:

jdbc:postgresql://postgres:5432/orders

This single concept explains thousands of Docker networking issues.

---

# Mount Namespace

Mount namespace controls filesystem visibility.

Host:

/home
/etc
/var

Container:

/
/app
/tmp

The container sees its own filesystem view.

This is why Docker images work.

---

# Docker Image Connection

A Docker image becomes the filesystem seen by the container.

Spring Boot example:

Base Linux
 +
JDK
 +
Application JAR

Container sees:

/app/app.jar

because of mount isolation.

---

# Real Production Failure

Developer:

File exists on host.

Application:

File not found.

Cause:

File never mounted into the container.

Understanding mount namespace immediately explains the problem.

---

# UTS Namespace

UTS namespace controls hostname.

Without it:

All containers see the host hostname.

With it:

Container A:

user-service

Container B:

payment-service

Host:

node-17

Useful for:

- logs
- metrics
- observability
- debugging

---

# IPC Namespace

IPC means Inter Process Communication.

Think:

shared memory
message queues
semaphores

Normally not a major topic for Spring Boot developers.

Just understand:

Container A should not interfere with Container B communication resources.

---

# User Namespace

Security-focused namespace.

Without user namespace:

Container root
=
Host root

Dangerous.

With user namespace:

Container thinks:

I am root.

Host sees:

Normal user.

Huge security improvement.

---

# How Docker Uses Namespaces

When you run:

docker run app

Docker creates:

PID namespace
Network namespace
Mount namespace
UTS namespace
IPC namespace
User namespace

Then starts the application process.

That process becomes the container.

Important:

Container is still just a process.

Not a VM.

---

# Kubernetes Connection

A Kubernetes Pod contains one or more containers.

Key idea:

Containers inside the same Pod share networking.

Therefore:

localhost works between containers inside a Pod.

This is why sidecars work.

Example:

Application Container

talks to

Envoy Sidecar

using localhost.

---

# Why Containers Are Lightweight

VM:

Hardware
-> Guest OS
-> Application

Container:

Linux Kernel
-> Application

No guest OS.

Less memory.

Faster startup.

Higher density.

---

# Performance Tradeoff

Container advantages:

Fast startup

Low memory

High density

Container limitations:

Shared kernel

Weaker isolation than VMs

Understanding this tradeoff is important in architecture interviews.

---

# Common Mistakes

Mistake 1

Container = VM

Wrong.

Container = isolated process.

Mistake 2

localhost means host machine

Wrong.

localhost means current network namespace.

Mistake 3

Namespace and Cgroup are same

Wrong.

Namespace:

What process can see.

Cgroup:

What process can use.

---

# System Design Connection

When discussing:

Microservices

Docker

Kubernetes

Service Mesh

Cloud Native

you are indirectly discussing namespaces.

Namespaces are one of the foundations of modern platform engineering.

---

# Strong Interview Answer

What are Linux namespaces?

Linux namespaces are kernel features that provide isolated views of system resources. They allow processes to have separate process trees, network stacks, filesystems, hostnames, IPC resources, and user mappings while still sharing the same Linux kernel. Docker and Kubernetes use namespaces to provide container isolation.

---

# Final Cheat Sheet

PID Namespace
-> Process isolation

Network Namespace
-> Network isolation

Mount Namespace
-> Filesystem isolation

UTS Namespace
-> Hostname isolation

IPC Namespace
-> Communication isolation

User Namespace
-> Identity isolation

Container

=

Process
+
Namespaces
+
Cgroups
+
Filesystem

Most important takeaway:

A container is not a small virtual machine.

A container is an isolated Linux process.
