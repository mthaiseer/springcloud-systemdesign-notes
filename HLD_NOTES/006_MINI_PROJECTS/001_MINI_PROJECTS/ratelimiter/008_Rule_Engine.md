# 008 — Rule Engine

---

# 1. Goal

Resolve different limits per endpoint, tenant, or user tier.

---

# 2. Production Feature Added

```text
Resolve different limits per endpoint, tenant, or user tier.
```

---

# 3. Delta From Previous Phase

```text
Added config lookup before applying limiter.
```

---

# 4. Architecture

```mermaid
flowchart TD
    Request[Incoming Request]
    Stage[Rule Engine]
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

Use maps like `/payment -> strict`, `/search -> relaxed`, `default -> standard`.

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

## `RuleEngine.java`

```java
package com.miniratelimiter.production;

import com.miniratelimiter.core.RateLimitDecision;

public class RuleEngine {

    public RateLimitDecision handle(String userId, String api, String ip) {
        /*
         * Phase 008: Rule Engine
         *
         * Production idea:
         * Resolve different limits per endpoint, tenant, or user tier.
         */
        return new RateLimitDecision(true, "allowed by Rule Engine");
    }
}
```

## `Driver.java`

```java
package com.miniratelimiter.driver;

import com.miniratelimiter.production.RuleEngine;

public class Driver {
    public static void main(String[] args) {
        RuleEngine component = new RuleEngine();
        System.out.println(component.handle("alice", "/payment", "10.0.0.1"));
    }
}
```

---

# 7. DSA/CP Mapping

```text
Map lookup, pattern matching, default rule fallback. CP analogy: choose rule based on interval/category/key.
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
