# 018_Push_Based_Service_Updates.md

# MiniServiceDiscovery Phase 18 — Push Based Service Updates

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. Previous Limitation](#3-previous-limitation)
- [4. What We Build](#4-what-we-build)
- [5. Architecture](#5-architecture)
- [6. Step-by-Step Flow](#6-step-by-step-flow)
- [7. Complete Java Code](#7-complete-java-code)
- [8. Dry Run](#8-dry-run)
- [9. DSA / CP Concepts Used](#9-dsa--cp-concepts-used)
- [10. Production Concepts](#10-production-concepts)
- [11. Scalability Discussion](#11-scalability-discussion)
- [12. Interview Notes](#12-interview-notes)
- [13. Common Bugs](#13-common-bugs)
- [14. Current Limitations](#14-current-limitations)
- [15. Next Step](#15-next-step)

---

# 1. Goal

In this phase we build:

```text
Push Based Service Updates
```

Purpose:

```text
Push updates to subscribers.
```

---

# 2. Why This Phase Matters

Service discovery is the backbone of distributed systems.

This phase improves:

```text
dynamic service location
routing
health awareness
service scaling
gateway integration
```

---

# 3. Previous Limitation

Earlier phases lacked:

```text
dynamic infrastructure capability
```

This phase solves part of that problem.

---

# 4. What We Build

Core components:

```text
registry
service instance
discovery client
health/metadata logic
```

---

# 5. Architecture

```text
Client/Gateway
    |
    | discover(service)
    v
Service Registry
    |
    v
Healthy Service Instances
```

---

# 6. Step-by-Step Flow

```text
1. Service registers itself
2. Registry stores service info
3. Client discovers service
4. Registry returns matching instances
5. Client/gateway selects backend
```

---

# 7. Complete Java Code

## 7.1 `ServiceInstance.java`

### Logic before this class

Represents one backend service instance.

```java
package com.miniservicediscovery.model;

public class ServiceInstance {

    private final String serviceName;
    private final String host;
    private final int port;
    private boolean healthy;

    public ServiceInstance(
            String serviceName,
            String host,
            int port
    ) {
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
        this.healthy = true;
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

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    @Override
    public String toString() {
        return serviceName + " -> " + host + ":" + port +
                " healthy=" + healthy;
    }
}
```

---

## 7.2 `ServiceRegistry.java`

### Logic before this class

Registry stores:

```text
service-name -> instances
```

```java
package com.miniservicediscovery.registry;

import com.miniservicediscovery.model.ServiceInstance;

import java.util.*;

public class ServiceRegistry {

    private final Map<String, List<ServiceInstance>> registry =
            new HashMap<>();

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
        return registry.getOrDefault(
                serviceName,
                Collections.emptyList()
        );
    }

    public void printRegistry() {
        System.out.println("\n===== REGISTRY =====");

        for (Map.Entry<String, List<ServiceInstance>> entry :
                registry.entrySet()) {

            System.out.println(entry.getKey());

            for (ServiceInstance instance :
                    entry.getValue()) {

                System.out.println("   -> " + instance);
            }
        }

        System.out.println("====================\n");
    }
}
```

---

## 7.3 `Phase18Driver.java`

### Logic before this class

Driver demonstrates registry operations.

```java
package com.miniservicediscovery.driver;

import com.miniservicediscovery.model.ServiceInstance;
import com.miniservicediscovery.registry.ServiceRegistry;

public class Phase18Driver {

    public static void main(String[] args) {

        ServiceRegistry registry =
                new ServiceRegistry();

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

        System.out.println(
                registry.discover("user-service")
        );
    }
}
```

---

# 8. Dry Run

```text
register(user-service, localhost:9001)
register(user-service, localhost:9002)

discover(user-service)

Result:
localhost:9001
localhost:9002
```

Visual:

```text
Gateway
   |
   | discover(user-service)
   v
Registry
   |
   v
localhost:9001
localhost:9002
```

---

# 9. DSA / CP Concepts Used

| Concept | Usage |
|---|---|
| HashMap | service-name -> instances |
| ArrayList | store instances |
| Lookup | discovery |
| Filtering | health checks |
| Round robin | later phases |

Core structure:

```text
Map<String, List<ServiceInstance>>
```

---

# 10. Production Concepts

Real systems add:

```text
heartbeats
TTL expiry
health checks
watch updates
replication
leader election
persistence
distributed consistency
```

Used by:

```text
Eureka
Consul
ZooKeeper
etcd
Kubernetes discovery
```

---

# 11. Scalability Discussion

Current phase:

```text
single JVM
in-memory state
no replication
```

Production systems:

```text
multi-node registry cluster
eventual consistency
heartbeat cleanup
distributed replication
```

---

# 12. Interview Notes

Common questions:

```text
Why service discovery?
Client-side vs server-side discovery?
How does Kubernetes discovery work?
How do you avoid stale instances?
How do gateways use service discovery?
```

---

# 13. Common Bugs

## Bug 1 — Duplicate registrations

Fix:

```text
use unique instance ID
```

## Bug 2 — Stale instances

Fix:

```text
heartbeat + cleanup
```

## Bug 3 — Wrong service name

Fix:

```text
consistent naming convention
```

---

# 14. Current Limitations

Current phase does not fully support:

```text
distributed replication
real networking
watch mechanism
leader election
persistent storage
```

---

# 15. Next Step

```text
019_Multi_Registry_Replication.md
```
