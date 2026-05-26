# 013 — Observability And Load Testing

---

# 1. Goal

Expose allowed/rejected/latency metrics and test with k6.

---

# 2. Production Feature Added

```text
Expose allowed/rejected/latency metrics and test with k6.
```

---

# 3. Delta From Previous Phase

```text
Added feedback loop.
```

---

# 4. Architecture

```mermaid
flowchart TD
    Request[Incoming Request]
    Stage[Observability And Load Testing]
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

Track rejection rate, p95 latency, Redis latency, hot keys.

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

## `ObservabilityAndLoadTesting.java`

```java
package com.miniratelimiter.production;

import com.miniratelimiter.core.RateLimitDecision;

public class ObservabilityAndLoadTesting {

    public RateLimitDecision handle(String userId, String api, String ip) {
        /*
         * Phase 013: Observability And Load Testing
         *
         * Production idea:
         * Expose allowed/rejected/latency metrics and test with k6.
         */
        return new RateLimitDecision(true, "allowed by Observability And Load Testing");
    }
}
```

## `Driver.java`

```java
package com.miniratelimiter.driver;

import com.miniratelimiter.production.ObservabilityAndLoadTesting;

public class Driver {
    public static void main(String[] args) {
        ObservabilityAndLoadTesting component = new ObservabilityAndLoadTesting();
        System.out.println(component.handle("alice", "/payment", "10.0.0.1"));
    }
}
```

---

# 7. DSA/CP Mapping

```text
Counting, aggregation, percentile thinking. CP analogy: maintain stats over events.
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
