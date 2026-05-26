# 012 — Multi-Level Rate Limiter

---

# 1. Goal

Apply global + IP + user + endpoint limits together.

---

# 2. Production Feature Added

```text
Apply global + IP + user + endpoint limits together.
```

---

# 3. Delta From Previous Phase

```text
Composed multiple limiters.
```

---

# 4. Architecture

```mermaid
flowchart TD
    Request[Incoming Request]
    Stage[Multi-Level Rate Limiter]
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

Order matters: cheap checks first, expensive checks later.

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

## `Multi-LevelRateLimiter.java`

```java
package com.miniratelimiter.production;

import com.miniratelimiter.core.RateLimitDecision;

public class Multi-LevelRateLimiter {

    public RateLimitDecision handle(String userId, String api, String ip) {
        /*
         * Phase 012: Multi-Level Rate Limiter
         *
         * Production idea:
         * Apply global + IP + user + endpoint limits together.
         */
        return new RateLimitDecision(true, "allowed by Multi-Level Rate Limiter");
    }
}
```

## `Driver.java`

```java
package com.miniratelimiter.driver;

import com.miniratelimiter.production.Multi-LevelRateLimiter;

public class Driver {
    public static void main(String[] args) {
        Multi-LevelRateLimiter component = new Multi-LevelRateLimiter();
        System.out.println(component.handle("alice", "/payment", "10.0.0.1"));
    }
}
```

---

# 7. DSA/CP Mapping

```text
AND composition. CP analogy: action allowed only if all constraints pass.
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
