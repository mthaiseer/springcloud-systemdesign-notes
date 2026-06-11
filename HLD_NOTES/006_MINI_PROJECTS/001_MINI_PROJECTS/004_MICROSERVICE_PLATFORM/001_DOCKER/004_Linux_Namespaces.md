# 004_Linux_Namespaces

# Linux Namespaces

## Why This Matters

Linux namespaces are the core isolation mechanism behind containers. Docker did not invent container isolation; Docker orchestrates existing Linux kernel primitives. When an interviewer asks "How do containers isolate processes?", the correct answer begins with namespaces.

A container is fundamentally:

Process + Namespaces + Cgroups + Filesystem Layers

Without namespaces, every process would see the same:

- Process table
- Network interfaces
- Hostname
- Filesystems
- IPC resources
- User IDs

Namespaces create the illusion that a process owns its own machine.

---

## Mental Model

Imagine a large office building.

```text
Building
 |
 +-- Team A Room
 +-- Team B Room
 +-- Team C Room
```

All teams share the building.

But each team sees only its own room.

Namespaces create these "rooms" for processes.

Container Mental Model:

```text
Java Process
      |
PID Namespace
Network Namespace
Mount Namespace
UTS Namespace
IPC Namespace
User Namespace
      |
Linux Kernel
```

The kernel is shared.

The view is isolated.

---

## Core Concepts

### What Is A Namespace?

A namespace is a Linux kernel feature that provides isolation for specific resources.

Why Needed:

- Security
- Multi-tenancy
- Containers
- Cloud-native workloads

Advantages:

- Lightweight
- Fast
- Kernel-level

Disadvantages:

- Shared kernel
- Not hardware isolation

Interview Answer:

"A namespace virtualizes a kernel resource so a process sees an isolated view."

---

### PID Namespace

Controls process visibility.

Without namespace:

```text
PID 1
PID 2
PID 3
...
```

Container sees:

```text
PID 1
PID 2
PID 3
```

even though actual host PIDs differ.

Why Needed:

Processes should not see host processes.

Advantages:

Strong process isolation.

Disadvantages:

Debugging can be harder.

---

### Network Namespace

Creates isolated network stacks.

Each namespace gets:

```text
IP Address
Routing Table
Interfaces
Firewall Rules
```

Why Needed:

Independent networking.

Interview Answer:

Each container can have its own virtual network.

---

### Mount Namespace

Controls filesystem visibility.

Container:

```text
/
/app
/data
```

Host:

```text
/var
/home
/etc
```

Container cannot automatically see host filesystem.

---

### UTS Namespace

Controls:

```text
Hostname
Domain Name
```

Each container can have unique hostname.

---

### IPC Namespace

Controls:

```text
Shared Memory
Message Queues
Semaphores
```

Needed for application isolation.

---

### User Namespace

Maps users.

Example:

```text
Container Root
      |
Mapped
      |
Non-root Host User
```

Improves security.

---

## Internal Architecture

```text
Container Process
       |
+------+------+------+------+------+
| PID  | NET  | MNT  | IPC  | UTS  |
+------+------+------+------+------+
       |
   Linux Kernel
       |
   Hardware
```

Kernel maintains namespace descriptors and associates processes with namespace IDs.

---

## Step-by-Step Flow

Container Creation:

```text
docker run
      |
Create PID Namespace
      |
Create Network Namespace
      |
Create Mount Namespace
      |
Create IPC Namespace
      |
Create UTS Namespace
      |
Attach Process
      |
Start Container
```

---

## Data Structures Used

Conceptually:

```java
class Namespace {

    String id;

    String type;
}
```

Process metadata:

```java
class ProcessInfo {

    long pid;

    String namespaceId;
}
```

Kernel internally maintains namespace references per task structure.

---

## Algorithms Used

### Namespace Lookup

```text
Process
   |
Namespace Pointer
   |
Kernel Resource View
```

### Resource Resolution

```text
Open File
   |
Namespace Check
   |
Resolve Path
```

Time complexity generally remains near O(1) because kernel stores references directly.

---

## Production Implementation

Docker:

```text
Docker
   |
Namespaces
   |
Container Isolation
```

Kubernetes:

```text
Pod
  |
Containers
  |
Namespaces
```

Every modern container runtime relies on namespaces.

---

## Java Code Examples

### Example 1: Process Visibility

```java
public class ProcessDemo {

    public static void main(String[] args) {

        ProcessHandle.allProcesses()
                .limit(10)
                .forEach(System.out::println);
    }
}
```

Inside a container, visible processes differ from host visibility because of PID namespaces.

Dry Run:

1. JVM starts.
2. Queries process table.
3. Kernel returns namespace-scoped view.
4. Application sees isolated processes.

---

### Example 2: Hostname Isolation

```java
import java.net.InetAddress;

public class HostDemo {

    public static void main(String[] args)
            throws Exception {

        System.out.println(
            InetAddress.getLocalHost().getHostName()
        );
    }
}
```

UTS namespace determines hostname returned.

---

## Spring Boot Example

```java
@RestController
public class InfoController {

    @GetMapping("/info")
    public String info() throws Exception {

        return java.net.InetAddress
            .getLocalHost()
            .getHostName();
    }
}
```

Each container can return different hostname.

---

## Spring Cloud Example

```text
Gateway
User Service
Order Service
Payment Service
```

Each service runs in separate containers.

Each container receives:

- Separate PID view
- Separate network stack
- Separate hostname

---

## Kubernetes Example

```text
Pod
 |
 + Container A
 + Container B
```

Containers inside a pod share some namespaces such as network namespace.

Result:

```text
localhost communication works
```

between containers in same pod.

---

## Sequence Diagram (ASCII)

```text
Docker Engine
      |
Create Namespaces
      |
Attach Process
      |
Kernel
      |
Isolated View
      |
Application Starts
```

---

## Request Lifecycle

```text
Client
   |
Container IP
   |
Network Namespace
   |
Spring Boot
   |
Business Logic
   |
Response
```

---

## Failure Scenarios

### Broken Network Namespace

Symptoms:

```text
Cannot reach service
```

Cause:

- Routing issue
- Virtual interface issue

---

### Mount Namespace Issue

Symptoms:

```text
File not found
```

Cause:

Volume not mounted.

---

### PID Namespace Confusion

Symptoms:

```text
Process visible on host
Not visible in container
```

Expected behavior.

---

## Debugging Guide

View processes:

```bash
ps aux
```

Inspect namespaces:

```bash
lsns
```

View container:

```bash
docker inspect
```

Enter container:

```bash
docker exec -it container bash
```

Network:

```bash
ip addr
```

Hostname:

```bash
hostname
```

---

## Performance Considerations

Namespaces are lightweight.

Advantages:

- Minimal overhead
- Fast startup
- Kernel-native

Compared to VMs:

```text
VM -> OS overhead
Namespace -> tiny overhead
```

---

## Scalability Considerations

Namespaces scale extremely well.

```text
1 Host
 |
100 Containers
 |
100 Namespace Sets
```

No guest OS required.

High density workloads become possible.

---

## CAP Tradeoffs

Namespaces are local kernel constructs.

CAP theorem does not directly apply.

CAP discussions occur in distributed systems built on top of containers.

---

## Common Interview Questions

### Q1 What is a Linux namespace?
A: Kernel isolation mechanism for resources.

### Q2 Why are namespaces important?
A: They provide container isolation.

### Q3 What is PID namespace?
A: Process visibility isolation.

### Q4 What is network namespace?
A: Isolated networking stack.

### Q5 What is mount namespace?
A: Filesystem isolation.

### Q6 What is UTS namespace?
A: Hostname isolation.

### Q7 What is IPC namespace?
A: Shared memory isolation.

### Q8 What is user namespace?
A: User ID mapping isolation.

### Q9 Are namespaces Docker-specific?
A: No, Linux kernel feature.

### Q10 Do containers use namespaces?
A: Yes.

### Q11 Why are containers lightweight?
A: Shared kernel plus namespaces.

### Q12 Can containers see host processes?
A: Normally no due to PID namespace.

### Q13 Can containers have separate IPs?
A: Yes via network namespace.

### Q14 Do namespaces improve security?
A: Yes.

### Q15 Are namespaces enough?
A: Usually combined with cgroups and security controls.

### Q16 What happens when namespace is removed?
A: Resources are released.

### Q17 Kubernetes uses namespaces?
A: Linux namespaces internally for containers.

### Q18 Pod networking relationship?
A: Containers may share network namespace.

### Q19 Namespace vs VM?
A: Namespace isolates resources; VM virtualizes hardware.

### Q20 Explain namespaces in one minute.
A: Namespaces provide isolated views of kernel resources, making a process believe it owns its own machine.

---

## Strong Interview Answers

### How Do Containers Isolate Processes?

Containers rely primarily on Linux namespaces. PID namespaces isolate process visibility, network namespaces isolate networking, mount namespaces isolate filesystems, UTS namespaces isolate hostnames, IPC namespaces isolate inter-process communication, and user namespaces isolate user mappings.

### Why Are Containers Faster Than VMs?

Containers reuse the host kernel. Instead of booting a new OS, they create namespaces and start a process.

---

## Real World Example

E-commerce Platform:

```text
Gateway Container
User Container
Order Container
Payment Container
```

Every container has:

- Own hostname
- Own process view
- Own network interfaces

Yet all share one Linux kernel.

---

## FAANG/System Design Discussion

Topics commonly discussed:

- Container internals
- Process isolation
- Kubernetes pods
- Multi-tenancy
- Security boundaries
- Resource isolation

Expected senior-level explanation:

```text
Namespaces -> Isolation
Cgroups -> Limits
Layers -> Filesystem Efficiency
```

---

## Production Checklist

- Verify namespace isolation
- Use non-root containers
- Apply resource limits
- Monitor networking
- Validate volume mounts
- Use security scanning
- Enable observability

---

## Key Takeaways

1. Namespaces are kernel isolation primitives.
2. Docker builds on namespaces.
3. PID namespace isolates processes.
4. Network namespace isolates networking.
5. Mount namespace isolates filesystems.
6. UTS namespace isolates hostnames.
7. IPC namespace isolates communication.
8. User namespace improves security.
9. Containers are isolated processes.
10. Namespaces are foundational to Kubernetes.

---

# One-Page Cheat Sheet

```text
PID Namespace
  -> Process Isolation

Network Namespace
  -> Network Isolation

Mount Namespace
  -> Filesystem Isolation

UTS Namespace
  -> Hostname Isolation

IPC Namespace
  -> IPC Isolation

User Namespace
  -> User Mapping

Container
 =
Process
 +
Namespaces
 +
Cgroups
```

---

# Last-Minute Interview Revision

```text
Namespaces = Isolation

PID = Processes

NET = Networking

MNT = Filesystem

UTS = Hostname

IPC = Shared Memory

USER = Identity

Docker uses namespaces.
Kubernetes relies on containers.
```

---

# Mental Models Table

| Concept | Mental Model |
|----------|-------------|
| Namespace | Private Room |
| PID Namespace | Private Process List |
| Network Namespace | Private Network |
| Mount Namespace | Private Disk View |
| UTS Namespace | Private Computer Name |
| IPC Namespace | Private Conversation Channel |
| User Namespace | Identity Translator |
| Container | Isolated Apartment |
