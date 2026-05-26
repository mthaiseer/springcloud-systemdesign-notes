# 010 — Redis Distributed Fixed Window

---

# 1. Goal

Use Redis as shared store across app instances.

---

# 2. Production Feature Added

```text
Use Redis as shared store across app instances.
```

---

# 3. Delta From Previous Phase

```text
Moved state from JVM memory to Redis.
```

---

# 4. Architecture

```mermaid
flowchart TD
    Request[Incoming Request]
    Stage[Redis Distributed Fixed Window]
    Limiter[Limiter]
    Store[State Store]
    Decision[Decision]
    Request --> Stage
    Stage --> Limiter
    Limiter --> Store
    Store --> Decision
```

---

# 5. Production-Level Explanation

Use INCR + EXPIRE. Beware race between INCR and EXPIRE unless Lua or transaction used.

---

# 6. Complete Java Skeleton

This phase is an integration/production phase. Use the algorithms from earlier phases.

## `RateLimitDecision.java`

```java
package com.miniratelimiter.core;

public class RateLimitDecision {
    private final boolean allowed;
    private final String reason;

    public RateLimitDecision(boolean allowed, String reason) {
        this.allowed = allowed;
        this.reason = reason;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "RateLimitDecision{allowed=" + allowed + ", reason='" + reason + "'}";
    }
}
```

## `RedisDistributedFixedWindow.java`

```java
package com.miniratelimiter.production;

import com.miniratelimiter.core.RateLimitDecision;

public class RedisDistributedFixedWindow {

    public RateLimitDecision handle(String userId, String api, String ip) {
        /*
         * Phase 010: Redis Distributed Fixed Window
         *
         * Production idea:
         * Use Redis as shared store across app instances.
         */
        return new RateLimitDecision(true, "allowed by Redis Distributed Fixed Window");
    }
}
```

## `Driver.java`

```java
package com.miniratelimiter.driver;

import com.miniratelimiter.production.RedisDistributedFixedWindow;

public class Driver {
    public static void main(String[] args) {
        RedisDistributedFixedWindow component = new RedisDistributedFixedWindow();
        System.out.println(component.handle("alice", "/payment", "10.0.0.1"));
    }
}
```

---

# 7. DSA/CP Mapping

```text
Shared counter and atomic increment. CP analogy: global shared frequency map.
```

---

# 8. Interview Notes

Explain:

```text
why this feature is needed
what state is stored
where bottleneck can happen
how to test it
how to make it distributed
```

---

# 9. Production Checklist

```text
correctness
latency
thread safety
distributed consistency
cleanup / TTL
metrics
fallback behavior
load testing
```

---

# How To Run

```bash
javac -d out $(find src -name "*.java")
java -cp out com.miniratelimiter.driver.Driver
```

Windows PowerShell:

```powershell
Get-ChildItem -Recurse -Filter *.java src | ForEach-Object FullName | javac -d out
java -cp out com.miniratelimiter.driver.Driver
```
