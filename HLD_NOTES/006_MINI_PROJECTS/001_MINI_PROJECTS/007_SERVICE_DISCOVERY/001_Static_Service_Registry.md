# 001_Static_Service_Registry.md

# MiniServiceDiscovery Phase 001 — Static Service Registry

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Build](#3-what-we-build)
- [4. Current Architecture](#4-current-architecture)
- [5. Service Discovery Mental Model](#5-service-discovery-mental-model)
- [6. Folder Structure](#6-folder-structure)
- [7. Step-by-Step Flow](#7-step-by-step-flow)
- [8. Complete Java Code](#8-complete-java-code)
  - [8.1 ServiceInstance.java](#81-serviceinstancejava)
  - [8.2 StaticServiceRegistry.java](#82-staticserviceregistryjava)
  - [8.3 DiscoveryClient.java](#83-discoveryclientjava)
  - [8.4 Phase001StaticServiceRegistryDriver.java](#84-phase001staticserviceregistrydriverjava)
- [9. How To Run](#9-how-to-run)
- [10. Dry Run](#10-dry-run)
- [11. DSA / CP Concepts Used](#11-dsa--cp-concepts-used)
- [12. System Design Relevance](#12-system-design-relevance)
- [13. Real-World Mapping](#13-real-world-mapping)
- [14. Production-Grade Concepts](#14-production-grade-concepts)
- [15. Scalability Discussion](#15-scalability-discussion)
- [16. Interview Notes](#16-interview-notes)
- [17. Common Bugs](#17-common-bugs)
- [18. Current Limitations](#18-current-limitations)
- [19. Next Step](#19-next-step)

---

# 1. Goal

In this phase, we build the foundation of:

```text
MiniServiceDiscovery
```

The first component is:

```text
Static Service Registry
```

A service registry stores:

```text
service-name
    ->
list of backend instances
```

Example:

```text
user-service
    ->
localhost:9001
localhost:9002
```

This is the foundation behind:

```text
Eureka
Consul
ZooKeeper
etcd
Kubernetes service discovery
```

---

# 2. Why This Phase Matters

In distributed systems:

```text
services move
instances scale up/down
containers restart
IPs change
```

Hardcoding backend addresses inside applications is dangerous.

Instead, systems use:

```text
service discovery
```

Flow:

```text
gateway/client
    ->
ask registry for service instances
    ->
pick one backend
    ->
send request
```

Without service discovery:

```text
gateway must hardcode every IP
```

With service discovery:

```text
services become dynamic
```

---

# 3. What We Build

We build:

```text
1. ServiceInstance
2. StaticServiceRegistry
3. DiscoveryClient
```

## ServiceInstance

Represents one backend service instance.

Example:

```text
user-service -> localhost:9001
```

---

## StaticServiceRegistry

Stores:

```text
service-name -> instances
```

Example:

```text
user-service -> [9001, 9002]
order-service -> [9010]
```

---

## DiscoveryClient

Asks registry:

```text
Give me instances for user-service
```

Registry returns:

```text
localhost:9001
localhost:9002
```

---

# 4. Current Architecture

```text
+-------------------+
| Gateway / Client  |
+---------+---------+
          |
          | discover(user-service)
          v
+-------------------+
| Service Registry  |
+---------+---------+
          |
          | returns instances
          v
+-------------------+
| user-service      |
| localhost:9001    |
+-------------------+
```

---

# 5. Service Discovery Mental Model

Core idea:

```text
service-name
    ->
list of instances
```

Example:

```text
payment-service
    ->
10.0.0.1:8080
10.0.0.2:8080
10.0.0.3:8080
```

Gateway flow:

```text
request comes
    ->
discover payment-service
    ->
pick instance
    ->
forward request
```

This is the foundation of:

```text
microservices
Kubernetes
cloud-native systems
dynamic scaling
```

---

# 6. Folder Structure

```text
MiniServiceDiscovery/
└── src/
    └── main/
        └── java/
            └── com/
                └── miniservicediscovery/
                    ├── model/
                    │   └── ServiceInstance.java
                    ├── registry/
                    │   └── StaticServiceRegistry.java
                    ├── client/
                    │   └── DiscoveryClient.java
                    └── driver/
                        └── Phase001StaticServiceRegistryDriver.java
```

---

# 7. Step-by-Step Flow

## Step 1 — Registry Starts

Registry is created in memory.

---

## Step 2 — Register Services

Example:

```text
user-service -> localhost:9001
user-service -> localhost:9002
order-service -> localhost:9010
```

---

## Step 3 — Client Requests Service Discovery

Example:

```text
discover("user-service")
```

---

## Step 4 — Registry Returns Instances

Result:

```text
localhost:9001
localhost:9002
```

---

## Step 5 — Client Chooses One Instance

Later phases add:

```text
round robin
least connections
zone aware routing
```

For now, client simply prints instances.

---

# 8. Complete Java Code

---

## 8.1 ServiceInstance.java

### Logic before this class

This class represents one backend service instance.

Example:

```text
user-service
    ->
localhost:9001
```

Each instance stores:

```text
service name
host
port
```

Later phases add:

```text
health status
heartbeat timestamp
zone
metadata
weight
version
```

```java
package com.miniservicediscovery.model;

public class ServiceInstance {

    private final String serviceName;
    private final String host;
    private final int port;

    public ServiceInstance(String serviceName, String host, int port) {
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "ServiceInstance{" +
                "serviceName='" + serviceName + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
```

---

## 8.2 StaticServiceRegistry.java

### Logic before this class

This is the actual registry.

Core structure:

```text
Map<
    service-name,
    list of instances
>
```

Example:

```text
"user-service"
    ->
[
  localhost:9001,
  localhost:9002
]
```

Operations:

```text
register service
discover service
list all services
```

This is intentionally static.

Later phases add:

```text
heartbeats
TTL expiry
health checks
replication
watch mechanism
persistence
leader election
```

```java
package com.miniservicediscovery.registry;

import com.miniservicediscovery.model.ServiceInstance;

import java.util.*;

public class StaticServiceRegistry {

    private final Map<String, List<ServiceInstance>> registry;

    public StaticServiceRegistry() {
        this.registry = new HashMap<>();
    }

    public void register(ServiceInstance instance) {
        registry
                .computeIfAbsent(
                        instance.getServiceName(),
                        k -> new ArrayList<>()
                )
                .add(instance);

        System.out.println("Registered: " + instance);
    }

    public List<ServiceInstance> discover(String serviceName) {
        return registry.getOrDefault(serviceName, Collections.emptyList());
    }

    public void printRegistry() {
        System.out.println("\n===== SERVICE REGISTRY =====");

        for (Map.Entry<String, List<ServiceInstance>> entry : registry.entrySet()) {

            System.out.println(entry.getKey());

            for (ServiceInstance instance : entry.getValue()) {
                System.out.println("   -> " +
                        instance.getHost() +
                        ":" +
                        instance.getPort());
            }
        }

        System.out.println("============================\n");
    }
}
```

---

## 8.3 DiscoveryClient.java

### Logic before this class

This class simulates:

```text
gateway
microservice
load balancer
```

asking the registry:

```text
Where is user-service?
```

Registry returns service instances.

Later phases add:

```text
client-side load balancing
cache
watch updates
zone-aware routing
retry
circuit breaker integration
```

```java
package com.miniservicediscovery.client;

import com.miniservicediscovery.model.ServiceInstance;
import com.miniservicediscovery.registry.StaticServiceRegistry;

import java.util.List;

public class DiscoveryClient {

    private final StaticServiceRegistry registry;

    public DiscoveryClient(StaticServiceRegistry registry) {
        this.registry = registry;
    }

    public void discoverAndPrint(String serviceName) {

        System.out.println("\nDiscovering service: " + serviceName);

        List<ServiceInstance> instances =
                registry.discover(serviceName);

        if (instances.isEmpty()) {
            System.out.println("No instances found.");
            return;
        }

        for (ServiceInstance instance : instances) {
            System.out.println(
                    "Found instance -> " +
                            instance.getHost() +
                            ":" +
                            instance.getPort()
            );
        }
    }
}
```

---

## 8.4 Phase001StaticServiceRegistryDriver.java

### Logic before this class

This driver demonstrates:

```text
register services
discover services
print registry
```

Flow:

```text
create registry
    ->
register instances
    ->
client discovers service
```

```java
package com.miniservicediscovery.driver;

import com.miniservicediscovery.client.DiscoveryClient;
import com.miniservicediscovery.model.ServiceInstance;
import com.miniservicediscovery.registry.StaticServiceRegistry;

public class Phase001StaticServiceRegistryDriver {

    public static void main(String[] args) {

        StaticServiceRegistry registry =
                new StaticServiceRegistry();

        registry.register(
                new ServiceInstance(
                        "user-service",
                        "localhost",
                        9001
                )
        );

        registry.register(
                new ServiceInstance(
                        "user-service",
                        "localhost",
                        9002
                )
        );

        registry.register(
                new ServiceInstance(
                        "order-service",
                        "localhost",
                        9010
                )
        );

        registry.printRegistry();

        DiscoveryClient client =
                new DiscoveryClient(registry);

        client.discoverAndPrint("user-service");

        client.discoverAndPrint("order-service");

        client.discoverAndPrint("payment-service");
    }
}
```

---

# 9. How To Run

## IntelliJ

1. Create Java project:

```text
MiniServiceDiscovery
```

2. Create packages:

```text
com.miniservicediscovery.model
com.miniservicediscovery.registry
com.miniservicediscovery.client
com.miniservicediscovery.driver
```

3. Add classes.

4. Run:

```text
Phase001StaticServiceRegistryDriver
```

---

## Command Line

Compile:

```bash
javac -d out src/main/java/com/miniservicediscovery/model/ServiceInstance.java \
             src/main/java/com/miniservicediscovery/registry/StaticServiceRegistry.java \
             src/main/java/com/miniservicediscovery/client/DiscoveryClient.java \
             src/main/java/com/miniservicediscovery/driver/Phase001StaticServiceRegistryDriver.java
```

Run:

```bash
java -cp out com.miniservicediscovery.driver.Phase001StaticServiceRegistryDriver
```

---

# 10. Dry Run

## Step 1 — Registry Created

```text
registry = {}
```

---

## Step 2 — Register user-service

```text
user-service -> localhost:9001
```

Registry:

```text
{
  user-service:
    [localhost:9001]
}
```

---

## Step 3 — Register another instance

```text
user-service -> localhost:9002
```

Registry:

```text
{
  user-service:
    [
      localhost:9001,
      localhost:9002
    ]
}
```

---

## Step 4 — Discover Service

Client asks:

```text
discover(user-service)
```

Registry returns:

```text
localhost:9001
localhost:9002
```

---

## Visual Flow

```text
Gateway
   |
   | discover("user-service")
   v
Registry
   |
   | returns instances
   v
localhost:9001
localhost:9002
```

---

# 11. DSA / CP Concepts Used

This phase mainly uses:

| Concept | Usage |
|---|---|
| HashMap | service-name -> instances |
| ArrayList | store service instances |
| Object modeling | ServiceInstance |
| Lookup | discover services |

Core structure:

```text
Map<String, List<ServiceInstance>>
```

This pattern appears everywhere in distributed systems.

---

# 12. System Design Relevance

This phase maps to:

```text
service discovery
```

In HLD:

```text
Client
   ->
Gateway
   ->
Service Discovery
   ->
Backend Service
```

Without discovery:

```text
hardcoded backend IPs
```

With discovery:

```text
dynamic infrastructure
```

---

# 13. Real-World Mapping

This phase teaches concepts used by:

```text
Netflix Eureka
HashiCorp Consul
ZooKeeper
etcd
Kubernetes Services
DNS-based discovery
```

Example:

```text
user-service.default.svc.cluster.local
```

in Kubernetes is basically service discovery.

---

# 14. Production-Grade Concepts

Real service discovery systems add:

```text
heartbeats
health checks
TTL expiry
watch mechanism
push updates
zone awareness
replication
leader election
persistence
distributed consistency
```

This phase only introduces the core registry concept.

---

# 15. Scalability Discussion

Current phase:

```text
single JVM
in-memory registry
static instances
```

Production systems:

```text
distributed registry cluster
replicated state
eventual consistency
health monitoring
dynamic scaling
```

Potential bottlenecks:

```text
single point of failure
memory growth
stale instance list
no health verification
```

---

# 16. Interview Notes

## Q1. What is service discovery?

Service discovery allows clients/gateways to dynamically locate backend services.

Instead of:

```text
hardcoded IPs
```

clients ask registry:

```text
Where is user-service?
```

---

## Q2. Why is service discovery needed?

Because in distributed systems:

```text
instances scale dynamically
containers restart
IPs change
```

Service discovery keeps systems dynamic.

---

## Q3. What is client-side discovery?

Flow:

```text
client
    ->
registry
    ->
choose backend
```

Example:

```text
Ribbon
Kubernetes client-side routing
```

---

## Q4. What is server-side discovery?

Flow:

```text
client
    ->
load balancer/gateway
    ->
registry
    ->
backend
```

Example:

```text
Kubernetes Service
AWS ELB
NGINX
```

---

# 17. Common Bugs

## Bug 1 — Duplicate registration

Reason:

```text
same instance registered multiple times
```

Fix:

```text
use instance ID
```

---

## Bug 2 — Stale instances

Reason:

```text
instance crashed but registry still contains it
```

Fix:

```text
heartbeat + TTL expiry
```

---

## Bug 3 — Registry memory leak

Reason:

```text
dead services never removed
```

Fix:

```text
cleanup task
```

---

## Bug 4 — Service not found

Reason:

```text
wrong service name
```

Fix:

```text
consistent naming convention
```

---

# 18. Current Limitations

Current phase does NOT support:

```text
heartbeats
health checks
TTL expiry
replication
load balancing
watch updates
distributed registry
persistence
leader election
```

Those are added later.

---

# 19. Next Step

Next file:

```text
002_Service_Instance_Model.md
```

In the next phase, we improve:

```text
ServiceInstance
```

by adding:

```text
instanceId
health status
last heartbeat
metadata
zone
version
```

This moves MiniServiceDiscovery closer to a real production registry.
