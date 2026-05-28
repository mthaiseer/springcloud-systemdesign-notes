# 018_Distributed_Scheduler_Node.md

# MiniScheduler Phase 18 — Distributed Scheduler Node

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
- [10. System Design Relevance](#10-system-design-relevance)
- [11. Production Concepts](#11-production-concepts)
- [12. Scalability Discussion](#12-scalability-discussion)
- [13. Interview Notes](#13-interview-notes)
- [14. Common Bugs](#14-common-bugs)
- [15. Current Limitations](#15-current-limitations)
- [16. Next Step](#16-next-step)

---

# 1. Goal

In this phase, we build:

```text
Distributed Scheduler Node
```

Purpose:

```text
Run many scheduler nodes safely.
```

---

# 2. Why This Phase Matters

A scheduler is a production system that decides:

```text
what should run
when it should run
where it should run
what happens if it fails
```

This phase adds one important capability to that lifecycle.

---

# 3. Previous Limitation

Before this phase, MiniScheduler had limited support for:

```text
timing
execution
reliability
persistence
distribution
workflow behavior
```

This phase improves one of those areas.

---

# 4. What We Build

We add:

```text
Distributed Scheduler Node
```

Mental model:

```text
Task
  -> scheduledAt / dependency / retry state
  -> scheduler decision
  -> execution or wait
```

---

# 5. Architecture

```text
Task Store
   |
   v
Scheduler Loop / Timer
   |
   v
Due Task Selector
   |
   v
Worker / Executor
   |
   v
Success / Retry / DLQ
```

---

# 6. Step-by-Step Flow

```text
1. Task is created or loaded.
2. Scheduler checks if it is ready.
3. Scheduler decides whether to execute.
4. Worker executes task.
5. Result updates task status.
6. Failure may trigger retry/backoff/DLQ.
```

---

# 7. Complete Java Code


## 8.1 `PersistentTaskStore.java`

### Logic before this class

Production schedulers cannot lose tasks on restart.

This class stores tasks as simple lines in a file.

```java
package com.minischeduler.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersistentTaskStore {

    private final File file;

    public PersistentTaskStore(String path) {
        this.file = new File(path);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
    }

    public void append(String taskLine) {
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(taskLine);
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> loadAll() {
        List<String> result = new ArrayList<>();

        if (!file.exists()) {
            return result;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
```

---

## 8.2 `DistributedTaskLock.java`

### Logic before this class

A distributed scheduler needs locks.

Only one node should execute a task.

```java
package com.minischeduler.distributed;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class DistributedTaskLock {

    private final Map<String, Instant> locks = new HashMap<>();

    public synchronized boolean tryLock(String taskId, long ttlSeconds) {
        Instant now = Instant.now();
        Instant expiry = locks.get(taskId);

        if (expiry == null || now.isAfter(expiry)) {
            locks.put(taskId, now.plusSeconds(ttlSeconds));
            return true;
        }

        return false;
    }

    public synchronized void unlock(String taskId) {
        locks.remove(taskId);
    }
}
```

---

## 8.3 `PhaseDriver.java`

### Logic before this class

The driver demonstrates persistence and task locking.

```java
package com.minischeduler.driver;

import com.minischeduler.persistence.PersistentTaskStore;
import com.minischeduler.distributed.DistributedTaskLock;

public class PhaseDriver {

    public static void main(String[] args) {
        PersistentTaskStore store =
                new PersistentTaskStore("data/tasks.log");

        store.append("task-1|EMAIL|PENDING");
        store.append("task-2|PAYMENT_RETRY|PENDING");

        System.out.println("Recovered tasks:");
        for (String line : store.loadAll()) {
            System.out.println(line);
        }

        DistributedTaskLock lock =
                new DistributedTaskLock();

        System.out.println("node-A lock = " + lock.tryLock("task-1", 30));
        System.out.println("node-B lock = " + lock.tryLock("task-1", 30));

        lock.unlock("task-1");
    }
}
```


---

# 8. Dry Run

Example flow:

```text
Task scheduled
  -> scheduler tick
  -> task not due yet
  -> wait
  -> scheduler tick
  -> task due
  -> execute
  -> mark SUCCESS
```

For failure phases:

```text
execute
  -> fail
  -> retry count increment
  -> backoff delay
  -> retry later
  -> DLQ if max retries exceeded
```

---

# 9. DSA / CP Concepts Used

| Concept | Usage |
|---|---|
| PriorityQueue / Min-Heap | earliest task first |
| HashMap | task lookup |
| Queue | pending execution |
| State machine | task lifecycle |
| DAG | dependency scheduling |
| Set | completed dependency tracking |
| ThreadPool | worker execution |

---

# 10. System Design Relevance

This phase maps to:

```text
Quartz
Airflow
Temporal
Cadence
AWS EventBridge Scheduler
Kubernetes CronJob
Celery
Sidekiq
payment retry schedulers
email schedulers
report generators
```

---

# 11. Production Concepts

Production scheduler must handle:

```text
at-least-once execution
idempotency
task locking
leader election
persistent storage
retry policy
backoff
DLQ
misfire handling
clock skew
worker crash
metrics
```

---

# 12. Scalability Discussion

Scaling path:

```text
single JVM scheduler
  -> worker pool
  -> persistent task store
  -> distributed lock
  -> leader election
  -> sharded task partitions
  -> multi-node scheduler cluster
```

Bottlenecks:

```text
large number of pending tasks
many tasks due at same second
database polling pressure
worker pool saturation
retry storms
hot shard
```

---

# 13. Interview Notes

Good explanation structure:

```text
Task model
  -> due task selection
  -> execution model
  -> failure handling
  -> persistence
  -> distribution
```

Common follow-ups:

```text
How do you avoid duplicate execution?
How do you recover after crash?
How do you retry failed jobs?
How do you handle millions of scheduled jobs?
How do you shard tasks?
```

---

# 14. Common Bugs

## Bug 1 — Duplicate execution

Fix:

```text
distributed lock or task ownership
```

## Bug 2 — Lost task after restart

Fix:

```text
persistent store
```

## Bug 3 — Retry storm

Fix:

```text
exponential backoff + jitter
```

## Bug 4 — Long task blocks scheduler

Fix:

```text
worker thread pool
```

## Bug 5 — Missed execution after downtime

Fix:

```text
misfire policy
```

---

# 15. Current Limitations

This phase is still simplified.

Full production needs:

```text
database locking
distributed nodes
leader election
metrics
DLQ
idempotent handlers
recovery
alerting
```

---

# 16. Next Step

```text
019_Task_Sharding.md
```
