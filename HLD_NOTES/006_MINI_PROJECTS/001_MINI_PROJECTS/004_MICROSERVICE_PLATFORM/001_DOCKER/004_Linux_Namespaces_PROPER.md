# 004_Linux_Namespaces.md

# Linux Namespaces - MiniDocker Deep Production Mode

## 0. What You Should Know After This Chapter

After this chapter, you should be able to explain how Docker isolates a process without booting a full virtual machine. You should be able to answer senior-level questions such as:

- Why does a process inside a container think it is PID 1?
- Why can two containers both use port 8080 internally?
- Why can a container have its own hostname?
- Why can a root user inside a container be mapped to a non-root user on the host?
- Why can containers inside the same Kubernetes Pod talk using `localhost`?
- Why are namespaces not enough for complete container security?
- How do Docker, containerd, runc, and the Linux kernel work together?

The core mental model is:

```text
Container
  =
Linux Process
  +
Namespaces  -> what the process can see
  +
Cgroups     -> how much resource it can use
  +
Filesystem  -> what root filesystem it sees
```

This chapter focuses only on namespaces.

---

## 1. Beginner Mental Model

Imagine one large apartment building.

```text
Building
 |
 +-- Apartment A
 +-- Apartment B
 +-- Apartment C
```

All apartments share:

```text
Same land
Same building structure
Same electricity system
Same water supply
```

But each apartment has its own:

```text
Door
Room view
Kitchen
Address label
Private space
```

Linux namespaces are like apartments for processes.

The machine has one Linux kernel, but each containerized process sees a private view of selected kernel resources.

```text
Host Machine
 |
 +-- Container A Process
 |      sees its own process list
 |      sees its own network interfaces
 |      sees its own hostname
 |      sees its own root filesystem
 |
 +-- Container B Process
        sees its own process list
        sees its own network interfaces
        sees its own hostname
        sees its own root filesystem
```

The important point:

```text
Namespaces do not create a new kernel.
Namespaces create isolated views inside the same kernel.
```

That is why containers are lighter than virtual machines.

---

## 2. Why This Problem Exists

Before containers, applications often ran directly on the host.

```text
Host OS
 |
 +-- Java App A
 +-- Java App B
 +-- Redis
 +-- Nginx
```

This caused many problems.

### Problem 1: Process Visibility

Without PID isolation, every process can potentially see the host process table.

```text
ps aux
 |
 +-- systemd
 +-- sshd
 +-- postgres
 +-- java app A
 +-- java app B
```

For multi-tenant systems, this is dangerous.

### Problem 2: Network Conflicts

If two apps both want port 8080:

```text
App A -> 0.0.0.0:8080
App B -> 0.0.0.0:8080
```

Only one can bind to that port on the same network stack.

### Problem 3: Filesystem Confusion

If all applications see the same `/etc`, `/var`, `/tmp`, and `/home`, one app may accidentally read or overwrite files belonging to another app.

### Problem 4: Hostname and Identity

Distributed systems often depend on hostnames. If every service sees the host machine name, it becomes difficult to identify instances.

### Problem 5: Security Boundaries

Cloud platforms need to run workloads from different teams or customers on the same machine. Without isolation, one workload can inspect or interfere with another.

Namespaces solve these problems by providing resource-specific isolation.

---

## 3. What Is A Linux Namespace?

A Linux namespace is a kernel feature that wraps a global system resource and gives a process an isolated view of that resource.

Formal interview answer:

> A Linux namespace virtualizes a kernel resource so that processes inside the namespace see their own isolated instance of that resource, while still sharing the same underlying Linux kernel.

Simple version:

```text
Namespace = private view of one operating system resource
```

Examples:

```text
PID namespace     -> private process list
Network namespace -> private network stack
Mount namespace   -> private filesystem mount view
UTS namespace     -> private hostname
IPC namespace     -> private shared-memory/message-queue view
User namespace    -> private UID/GID mapping
Cgroup namespace  -> private cgroup path view
Time namespace    -> private clock offsets for some clocks
```

Docker primarily depends on:

```text
PID
NET
MNT
UTS
IPC
USER
```

---

## 4. Kernel Internal Flow

At runtime, a container is still just a Linux process.

```text
java -jar app.jar
```

When the same Java process is started in Docker, it is still a process, but its kernel metadata points to namespace objects.

Conceptually:

```text
task_struct
     |
     +-- pid
     +-- memory info
     +-- file descriptors
     +-- credentials
     +-- nsproxy
              |
              +-- pid namespace
              +-- network namespace
              +-- mount namespace
              +-- uts namespace
              +-- ipc namespace
              +-- cgroup namespace
```

`task_struct` is the kernel's main process descriptor. Every Linux process has one. It contains scheduling state, credentials, memory references, file descriptor references, and namespace references.

The namespace references are grouped through a structure commonly understood as `nsproxy`.

Mental model:

```text
Process does not carry the whole namespace data directly.
Process points to namespace objects.
Multiple processes can point to the same namespace objects.
```

That is how containers in the same Kubernetes Pod can share a network namespace.

---

## 5. clone(), unshare(), and setns()

Linux provides system calls that create or join namespaces.

### clone()

`clone()` can create a new process and place it into new namespaces.

Conceptual call:

```c
clone(
    child_function,
    child_stack,
    CLONE_NEWPID |
    CLONE_NEWNET |
    CLONE_NEWNS  |
    CLONE_NEWUTS |
    CLONE_NEWIPC,
    NULL
);
```

The flags mean:

```text
CLONE_NEWPID -> create new PID namespace
CLONE_NEWNET -> create new network namespace
CLONE_NEWNS  -> create new mount namespace
CLONE_NEWUTS -> create new hostname namespace
CLONE_NEWIPC -> create new IPC namespace
```

Docker does not directly expose this to you, but the lower runtime layer uses these kernel capabilities.

### unshare()

`unshare()` allows an existing process to detach from some namespaces and create new ones.

Example command:

```bash
sudo unshare --uts bash
hostname mini-container
hostname
```

This changes the hostname inside the new UTS namespace without changing the host hostname.

### setns()

`setns()` allows a process to join an existing namespace.

This is the concept behind tools like:

```bash
nsenter
```

Example:

```bash
sudo nsenter -t <pid> -n ip addr
```

This means:

```text
Enter the network namespace of process <pid>
Then run ip addr there
```

Production debugging heavily depends on this idea.

---

## 6. Docker Runtime Flow

When you run:

```bash
docker run nginx
```

The flow is roughly:

```text
User
 |
docker CLI
 |
dockerd
 |
containerd
 |
containerd-shim
 |
runc
 |
Linux kernel
 |
container process starts
```

Detailed flow:

```text
1. Docker CLI sends request to Docker daemon.
2. Docker daemon resolves image and container configuration.
3. containerd manages container lifecycle.
4. runc receives OCI runtime specification.
5. runc asks the Linux kernel to create namespaces.
6. runc sets up filesystem, network, hostname, mounts, and credentials.
7. runc starts the container's first process.
8. That process becomes PID 1 inside the container PID namespace.
```

Important interview point:

```text
Docker is not the isolation primitive.
Docker is a manager.
Linux kernel namespaces provide isolation.
runc is the low-level runtime that applies the OCI spec using kernel features.
```

---

## 7. PID Namespace

PID namespace isolates process IDs and process visibility.

On the host:

```text
Host PID 24931 -> nginx master process
```

Inside the container:

```text
Container PID 1 -> nginx master process
```

Same process, different view.

ASCII view:

```text
Host PID Namespace
 |
 +-- PID 1      systemd
 +-- PID 800    dockerd
 +-- PID 1200   containerd
 +-- PID 24931  nginx

Container PID Namespace
 |
 +-- PID 1      nginx
```

The container process is not magically separate from the host. The host can still see it because the host PID namespace is the parent/global view.

### Why PID 1 Matters

Inside a container, the first process becomes PID 1.

PID 1 has special responsibilities:

```text
1. Receive signals
2. Reap zombie child processes
3. Act like init inside that namespace
```

A common production problem:

```text
Java app starts child process
Child process exits
PID 1 does not reap it
Zombie process remains
```

This is why containers sometimes use init helpers such as `tini`.

Docker option:

```bash
docker run --init my-app
```

### Dry Run

Command:

```bash
docker run --rm alpine sh -c "ps -ef"
```

Inside container:

```text
PID   USER   COMMAND
1     root   sh -c ps -ef
7     root   ps -ef
```

On host, the same process may have PID 42188.

Interview answer:

> PID namespace makes a process see a private process tree. The container's first process appears as PID 1 inside the namespace, while the host still sees the real host PID.

---

## 8. Network Namespace

Network namespace isolates the network stack.

Each network namespace can have its own:

```text
Interfaces
IP addresses
Routing tables
iptables rules
ARP table
Loopback device
Listening ports
```

This is why two containers can both listen on port 8080 internally.

```text
Container A netns
 |
 +-- 0.0.0.0:8080

Container B netns
 |
 +-- 0.0.0.0:8080
```

No conflict because they are different network namespaces.

### Docker Bridge Flow

Typical Docker bridge networking:

```text
Container
   |
 eth0
   |
 veth-container-end
   |
 veth-host-end
   |
 docker0 bridge
   |
 host network namespace
   |
 host NIC
   |
 internet
```

Packet flow from container to internet:

```text
1. App sends packet.
2. Packet enters container eth0.
3. eth0 is one side of a veth pair.
4. Packet exits through host-side veth.
5. Packet reaches docker0 bridge.
6. Host applies routing/NAT.
7. Packet exits through host NIC.
```

### localhost Important Point

Inside a container:

```text
localhost = container network namespace
```

Not the host.

So if a Spring Boot app inside container calls:

```text
http://localhost:5432
```

it is looking for PostgreSQL inside the same container, not the host PostgreSQL.

### Debug Commands

```bash
docker inspect <container>
docker exec -it <container> ip addr
docker exec -it <container> ip route
sudo nsenter -t <pid> -n ip addr
```

### Failure Case

Symptom:

```text
Container cannot reach another service
```

Possible causes:

```text
Wrong Docker network
Wrong DNS name
iptables/NAT issue
Bridge issue
Service listening on 127.0.0.1 instead of 0.0.0.0
Kubernetes NetworkPolicy blocking traffic
```

---

## 9. Mount Namespace

Mount namespace isolates filesystem mount points.

It controls what the process sees as:

```text
/
 /proc
 /sys
 /app
 /data
 /tmp
```

A container may see:

```text
/
├── app
├── bin
├── etc
├── lib
└── tmp
```

But the host has a very different filesystem.

```text
/
├── boot
├── home
├── var
├── usr
├── opt
└── run
```

The container does not automatically see the full host filesystem.

### Mount Namespace + chroot + pivot_root

Container startup usually involves preparing a root filesystem and changing the process's root view.

Conceptual flow:

```text
1. Prepare image root filesystem.
2. Create mount namespace.
3. Mount required filesystems.
4. Mount /proc, /sys, /dev as needed.
5. Switch process root to container root filesystem.
6. Start application.
```

### OverlayFS Relationship

Docker images are layered.

```text
Base image layer
   +
JDK layer
   +
Application jar layer
   +
Writable container layer
```

OverlayFS creates a merged view.

```text
lowerdir = read-only image layers
upperdir = writable container layer
merged   = what container sees
```

ASCII:

```text
Container sees /app/app.jar
        |
Merged Overlay View
        |
+-----------------------+
| writable upper layer  |
+-----------------------+
| image lower layers    |
+-----------------------+
```

Mount namespace decides where this merged filesystem is mounted for the container process.

### Failure Case

Symptom:

```text
File exists on host but not inside container
```

Possible causes:

```text
Volume not mounted
Mounted to wrong path
Mount namespace hides host path
Read-only filesystem
Wrong Kubernetes volumeMount
```

---

## 10. UTS Namespace

UTS namespace isolates hostname and domain name.

Without UTS isolation, all containers would see the host hostname.

With UTS namespace:

```text
Container A hostname -> user-service-1
Container B hostname -> payment-service-1
Host hostname        -> prod-node-17
```

Command:

```bash
docker run --rm alpine hostname
```

Docker usually sets the hostname to the container ID or configured name.

Spring Boot example:

```java
import java.net.InetAddress;

public class HostnameDemo {
    public static void main(String[] args) throws Exception {
        System.out.println(InetAddress.getLocalHost().getHostName());
    }
}
```

Inside different containers, this can return different hostnames because of UTS namespace.

Production relevance:

```text
Logs
Metrics
Tracing
Service identity
Debugging pod/container instance
```

---

## 11. IPC Namespace

IPC namespace isolates inter-process communication resources.

It covers:

```text
System V shared memory
Message queues
Semaphores
POSIX message queues
```

Why this matters:

```text
Container A should not read shared memory segments created by Container B.
Container B should not interfere with semaphores used by Container A.
```

For most Spring Boot microservices, you may not directly use System V IPC. But databases, native programs, and some high-performance systems can use shared memory and semaphores.

Kubernetes note:

Containers in the same Pod may share IPC namespace if configured, but by default this is not always shared in the same way as network.

---

## 12. User Namespace

User namespace isolates user and group IDs.

This is critical for security.

Without user namespace:

```text
Container root UID 0
        =
Host root UID 0
```

Dangerous if container escapes or accesses mounted host files.

With user namespace mapping:

```text
Container UID 0
        |
mapped to
        |
Host UID 100000
```

So the process thinks it is root inside the container, but the host sees it as an unprivileged user.

ASCII:

```text
Inside container:
root = UID 0

Host view:
same process = UID 100000
```

### Rootless Docker

Rootless Docker relies heavily on user namespaces.

Benefits:

```text
Less privilege on host
Lower blast radius
Better multi-tenant security
```

Limitations:

```text
Some networking features are harder
Some filesystem operations are limited
Some privileged workloads may not work
```

Strong interview point:

> User namespaces reduce the risk of container root being equivalent to host root by mapping container UIDs to different host UIDs.

---

## 13. Cgroup Namespace

Cgroup namespace isolates the view of cgroup paths.

Cgroups are mainly about resource control:

```text
CPU
Memory
IO
PIDs
```

Cgroup namespace is not the same as cgroups themselves.

Mental model:

```text
Cgroups          -> enforce resource limits
Cgroup namespace -> hide/virtualize cgroup path view
```

This helps processes inside containers see a cleaner cgroup hierarchy.

---

## 14. Time Namespace

Time namespace allows processes to have different offsets for certain clocks.

It is less commonly discussed in beginner Docker learning, but useful to know for completeness.

Useful areas:

```text
Testing time-dependent systems
Checkpoint/restore
Specialized container workloads
```

Most normal Spring Boot containers do not depend on custom time namespaces.

---

## 15. Docker Example

Create a simple Spring Boot app and containerize it.

### Java Controller

```java
package com.example.namespace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;

@RestController
public class InfoController {

    @GetMapping("/info")
    public String info() throws Exception {
        String hostname = InetAddress.getLocalHost().getHostName();
        String pid = ManagementFactory.getRuntimeMXBean().getName();

        return "hostname=" + hostname + "\n" +
               "jvmPidInfo=" + pid + "\n";
    }
}
```

### Dockerfile

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/namespace-demo.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build and Run

```bash
mvn clean package
docker build -t namespace-demo .
docker run --rm -p 8080:8080 --name ns-demo namespace-demo
```

Open:

```bash
curl localhost:8080/info
```

You may see a hostname based on the container ID/name.

### What Namespaces Are Involved?

```text
PID namespace     -> Java process sees container process tree
Network namespace -> app has container network stack
Mount namespace   -> app sees image filesystem
UTS namespace     -> app sees container hostname
IPC namespace     -> app has isolated IPC resources
```

---

## 16. Kubernetes Pod Namespace Sharing

A Kubernetes Pod is not just "a container." It is a group of one or more containers that share some namespaces.

```text
Pod
 |
 +-- app container
 +-- sidecar container
```

The most important shared namespace:

```text
Network namespace
```

That means both containers share:

```text
same IP address
same port space
same localhost
```

ASCII:

```text
Pod Network Namespace
 |
 +-- app container      localhost:8080
 +-- sidecar container  localhost:15000
```

This is why service mesh sidecars work.

Example:

```text
App sends request
 |
localhost / iptables redirection
 |
Envoy sidecar
 |
Remote service
```

### Important Kubernetes Rule

Containers in the same Pod cannot both bind the same port.

```text
Container A binds 8080
Container B also tries 8080
Result: conflict
```

Because they share network namespace.

This is different from separate Docker containers on separate network namespaces.

---

## 17. Full Dry Run: docker run nginx

Command:

```bash
docker run --rm -p 8080:80 nginx
```

Step-by-step:

```text
1. Docker CLI sends create request to dockerd.
2. dockerd checks if nginx image exists locally.
3. If missing, image is pulled from registry.
4. containerd receives runtime request.
5. runc prepares OCI container.
6. Kernel creates PID namespace.
7. Kernel creates network namespace.
8. Kernel creates mount namespace.
9. Kernel creates UTS namespace.
10. Kernel creates IPC namespace.
11. Docker sets up veth pair.
12. Host-side veth connects to docker bridge.
13. Container-side veth appears as eth0.
14. OverlayFS merged root is prepared.
15. Container process starts.
16. nginx becomes PID 1 inside container.
17. Port mapping forwards host 8080 to container 80.
18. Request to localhost:8080 reaches nginx.
```

Request flow:

```text
Browser
 |
localhost:8080 on host
 |
Docker port forwarding / NAT
 |
docker0 bridge
 |
veth pair
 |
container eth0
 |
nginx:80
```

---

## 18. Production Debugging Guide

### Find Container PID On Host

```bash
docker inspect --format '{{.State.Pid}}' ns-demo
```

Assume output:

```text
42188
```

### Enter Network Namespace

```bash
sudo nsenter -t 42188 -n ip addr
```

### Enter Mount Namespace

```bash
sudo nsenter -t 42188 -m sh
```

### List Namespaces

```bash
lsns
```

### Inspect Process Namespace Links

```bash
ls -l /proc/42188/ns
```

Example output:

```text
pid -> pid:[4026532761]
net -> net:[4026532763]
mnt -> mnt:[4026532759]
uts -> uts:[4026532760]
ipc -> ipc:[4026532762]
```

Two processes sharing the same namespace will have the same namespace inode number.

### Debug Network

```bash
docker exec -it ns-demo ip addr
docker exec -it ns-demo ip route
docker exec -it ns-demo ss -lntp
```

### Debug Filesystem

```bash
docker exec -it ns-demo mount
docker exec -it ns-demo df -h
docker exec -it ns-demo ls -lah /app
```

### Debug PID

```bash
docker exec -it ns-demo ps -ef
ps -ef | grep java
```

Inside container PID may be 1. On host it will be different.

---

## 19. Failure Cases

### Failure 1: Application Uses localhost Incorrectly

Inside container:

```text
localhost = container itself
```

Bad config:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/app
```

If PostgreSQL is in another container, this fails.

Correct Docker Compose style:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/app
```

### Failure 2: PID 1 Does Not Reap Zombies

Symptom:

```bash
ps -ef
```

shows many defunct processes.

Cause:

```text
Container main process acts as PID 1 but does not reap children.
```

Fix:

```bash
docker run --init my-app
```

or use a proper init process.

### Failure 3: File Not Found After Mount

Symptom:

```text
/app/config.yml not found
```

Cause:

```text
Mount namespace sees different filesystem.
Volume mounted to wrong path.
Kubernetes volumeMount path wrong.
```

### Failure 4: Permission Denied With User Namespace

Symptom:

```text
Permission denied writing mounted volume
```

Cause:

```text
Container UID mapped to host UID that lacks write permissions.
```

Fix:

```text
Align UID/GID ownership
Use proper fsGroup in Kubernetes
Avoid running as root blindly
```

### Failure 5: Port Conflict In Same Pod

Two containers in the same Pod try to bind port 8080.

Cause:

```text
They share network namespace.
```

Fix:

```text
Use different ports inside the Pod.
```

---

## 20. Performance And Scaling Tradeoffs

Namespaces are lightweight because they do not emulate hardware and do not boot a guest OS.

Container startup:

```text
Create namespaces
Set cgroups
Prepare filesystem
Start process
```

VM startup:

```text
Start virtual hardware
Boot guest kernel
Start guest OS services
Start application
```

Comparison:

| Area | Container Namespaces | Virtual Machine |
|---|---|---|
| Kernel | Shared host kernel | Separate guest kernel |
| Startup | Milliseconds/seconds | Seconds/minutes |
| Memory overhead | Low | Higher |
| Isolation strength | Process/kernel isolation | Hardware-level virtualization |
| Density | High | Lower |
| Security boundary | Weaker than VM | Stronger than container |

Namespaces scale well, but they are not free. Network namespace with bridge and NAT can add overhead. Heavy iptables rules can slow packet processing. Large numbers of containers can increase pressure on kernel data structures, conntrack tables, file descriptors, and network rules.

Production scaling considerations:

```text
Avoid unnecessary privileged containers
Watch conntrack table usage
Monitor container network latency
Use host networking only when justified
Use CNI plugins carefully in Kubernetes
Keep images small to reduce startup time
Use proper PID limits
```

---

## 21. Security Discussion

Namespaces are isolation, not a complete security solution.

A secure container setup usually combines:

```text
Namespaces
Cgroups
Seccomp
AppArmor / SELinux
Linux capabilities
Read-only root filesystem
Non-root user
Image scanning
Kubernetes securityContext
NetworkPolicy
```

Dangerous Docker options:

```bash
--privileged
--pid=host
--net=host
-v /:/host
```

These weaken namespace isolation.

Example:

```bash
docker run --pid=host alpine ps -ef
```

Now the container can see host processes.

Strong production rule:

```text
Do not disable namespaces unless you have a clear operational reason.
```

---

## 22. Common Mistakes

### Mistake 1: Thinking Container Is A Mini VM

Wrong:

```text
Container = tiny VM
```

Better:

```text
Container = isolated Linux process
```

### Mistake 2: Confusing Namespace With Cgroup

Wrong:

```text
Namespace limits CPU and memory
```

Correct:

```text
Namespace controls what process can see.
Cgroup controls how much resource process can use.
```

### Mistake 3: Misunderstanding localhost

Wrong:

```text
localhost means my laptop/host
```

Inside container:

```text
localhost means container namespace
```

Inside Kubernetes Pod:

```text
localhost means shared Pod network namespace
```

### Mistake 4: Running Everything As Root

Root inside container can still be risky, especially with mounted host paths or weak runtime settings.

### Mistake 5: Using --net=host Without Understanding

Host networking removes network namespace isolation.

---

## 23. Strong Interview Answer

### Question: How do containers isolate processes?

Strong answer:

> Containers isolate processes using Linux namespaces. A container is not a virtual machine; it is a normal Linux process attached to isolated namespace objects. PID namespaces isolate process visibility, network namespaces isolate interfaces and routing tables, mount namespaces isolate filesystem views, UTS namespaces isolate hostnames, IPC namespaces isolate shared memory and message queues, and user namespaces isolate UID/GID mappings. Docker and Kubernetes rely on runtimes such as containerd and runc to configure these namespaces through Linux kernel system calls like clone, unshare, and setns. Namespaces control what a process can see, while cgroups control how much CPU, memory, and IO it can use.

### Question: Why are containers lighter than VMs?

Strong answer:

> Containers share the host kernel and isolate only selected resource views using namespaces. A VM boots a separate guest OS with its own kernel and virtual hardware. Since containers avoid guest OS boot and hardware emulation, they start faster and use less memory. The tradeoff is that the isolation boundary is weaker than a VM because the kernel is shared.

### Question: Why can containers in a Kubernetes Pod communicate through localhost?

Strong answer:

> Containers in the same Kubernetes Pod share the Pod's network namespace. Therefore they share the same IP address, loopback interface, and port space. When one container calls localhost, it reaches the shared Pod network namespace, so it can communicate with another container in the same Pod if that container listens on a different port.

---

## 24. Mini Production Case Study

You deploy a Spring Boot app and PostgreSQL using Docker Compose.

Bad configuration:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/orders
```

Compose:

```yaml
services:
  app:
    image: order-service
  postgres:
    image: postgres:16
```

The app fails:

```text
Connection refused localhost:5432
```

Why?

Because inside the app container:

```text
localhost = app container network namespace
```

PostgreSQL is in another container with another network namespace.

Correct:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/orders
```

Now Docker DNS resolves `postgres` to the PostgreSQL container IP on the shared Docker network.

This one bug teaches:

```text
Network namespace
Container DNS
Service discovery
Docker bridge networking
Microservice communication
```

---

## 25. Final Cheat Sheet

```text
Linux Namespace
  =
Kernel feature that gives a process an isolated view of a resource.
```

```text
PID Namespace
  -> process list isolation
  -> container process can be PID 1
```

```text
Network Namespace
  -> interfaces, IPs, routes, ports, firewall rules
  -> localhost inside container is not host localhost
```

```text
Mount Namespace
  -> filesystem mount view
  -> works with OverlayFS and volumes
```

```text
UTS Namespace
  -> hostname isolation
```

```text
IPC Namespace
  -> shared memory, semaphores, message queues
```

```text
User Namespace
  -> UID/GID mapping
  -> container root can map to non-root host user
```

```text
Cgroup Namespace
  -> virtual cgroup path view
```

```text
Docker Runtime Flow
  docker CLI
      |
  dockerd
      |
  containerd
      |
  runc
      |
  Linux kernel namespaces
      |
  container process
```

```text
Namespace vs Cgroup
  Namespace -> what you can see
  Cgroup    -> what you can use
```

```text
Container vs VM
  Container -> shared kernel + namespaces
  VM        -> separate guest kernel + virtual hardware
```

```text
Kubernetes Pod
  -> containers share network namespace
  -> localhost works between containers in same Pod
  -> same port cannot be used twice in same Pod
```

---

## 26. Last-Minute Revision

If you remember only one thing:

```text
A container is not a small VM.
A container is a Linux process with isolated namespace views.
```

If interviewer asks "how Docker isolation works":

```text
Namespaces isolate visibility.
Cgroups limit resources.
OverlayFS provides layered filesystem.
runc applies these kernel features.
Kubernetes orchestrates containers across nodes.
```

This is the foundation for the next chapters:

```text
004_Linux_Namespaces
005_CGroups_Resource_Control
006_UnionFS_And_Layered_Filesystems
007_Copy_On_Write
```

Together:

```text
Namespaces -> isolation
Cgroups -> limits
UnionFS/OverlayFS -> image layers
Copy-on-write -> efficient container writes
```
