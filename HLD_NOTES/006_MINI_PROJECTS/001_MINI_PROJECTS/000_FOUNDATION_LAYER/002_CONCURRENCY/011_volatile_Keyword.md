# 011_volatile_Keyword.md

# MiniConcurrency — 011 volatile Keyword

## 0. Why This File Exists

After learning locks and atomic variables, the next important question is:

```text
What if one thread updates a value,
but another thread does not see the latest value?
```

This is called:

```text
visibility problem
```

Java provides:

```java
volatile
```

to solve visibility problems for simple shared variables.

This file teaches:

```text
What volatile means
Why visibility matters
How CPU cache causes stale reads
What volatile guarantees
What volatile does NOT guarantee
Why volatile is not atomic
Backend examples
Interview explanation
```

---

# 1. One-Line Definition

```text
volatile ensures that changes to a variable are visible to all threads.
```

Simple meaning:

```text
Thread A updates value.
Thread B sees latest value.
```

---

# 2. The Core Problem

Threads may cache values locally.

Example:

```java
boolean running = true;
```

Thread A:

```text
while (running) {
    // work
}
```

Thread B:

```text
running = false;
```

Problem:

```text
Thread A may not see updated false immediately.
```

It may continue forever.

---

# 3. Why This Happens

Modern CPUs and JVM optimize memory access.

A thread may read variable from:

```text
CPU cache
register
local working memory
```

Instead of always reading from main memory.

So one thread's update may not be visible to another thread immediately.

---

# 4. Memory Visibility Mental Model

Without volatile:

```text
Main Memory:
running = false

Thread A cache:
running = true
```

Thread A keeps reading old cached value.

With volatile:

```text
Thread must read latest value from main memory visibility path.
```

---

# 5. Visual Diagram

```text
Without volatile:

Thread A cache: running = true
Main memory:    running = false

Thread A may keep using old value.
```

```text
With volatile:

Thread A reads latest visible value.
Thread B writes latest visible value.
```

---

# 6. Java Example — Without volatile

```java
public class WithoutVolatileDemo {

    private static boolean running = true;

    public static void main(String[] args)
            throws Exception {

        Thread worker = new Thread(() -> {

            while (running) {

                // keep working
            }

            System.out.println("Worker stopped");
        });

        worker.start();

        Thread.sleep(1000);

        running = false;

        System.out.println("Main changed running to false");
    }
}
```

Problem:

```text
Worker may not stop reliably.
```

Why?

```text
Worker thread may not see latest running=false.
```

---

# 7. Java Example — With volatile

```java
public class VolatileDemo {

    private static volatile boolean running = true;

    public static void main(String[] args)
            throws Exception {

        Thread worker = new Thread(() -> {

            while (running) {

                // keep working
            }

            System.out.println("Worker stopped");
        });

        worker.start();

        Thread.sleep(1000);

        running = false;

        System.out.println("Main changed running to false");
    }
}
```

Now:

```text
Worker sees running=false and stops.
```

---

# 8. Step-by-Step Dry Run — With volatile

Initial:

```text
running = true
```

Worker thread:

```text
checks running
sees true
continues loop
```

Main thread:

```text
sets running = false
```

Because running is volatile:

```text
write becomes visible to other threads
```

Worker thread:

```text
reads running again
sees false
exits loop
prints "Worker stopped"
```

---

# 9. What volatile Guarantees

volatile provides:

```text
Visibility
Ordering
```

Meaning:

```text
Latest value is visible across threads.
Some instruction reordering is prevented around volatile access.
```

---

# 10. What volatile Does NOT Guarantee

Very important:

```text
volatile does NOT make compound operations atomic.
```

Example:

```java
volatile int counter = 0;

counter++;
```

Still unsafe.

Why?

Because:

```text
counter++ = read + add + write
```

Volatile only ensures visibility of each read/write.

It does not protect the whole operation.

---

# 11. Dry Run — volatile counter++ Still Fails

Initial:

```text
counter = 0
```

Thread A:

```text
reads volatile counter = 0
```

Thread B:

```text
reads volatile counter = 0
```

Thread A:

```text
writes 1
```

Thread B:

```text
writes 1
```

Expected:

```text
2
```

Actual:

```text
1
```

Even with volatile.

---

# 12. Bad Example — volatile Counter

```java
public class BadVolatileCounter {

    private static volatile int counter = 0;

    public static void increment() {

        counter++;
    }
}
```

Problem:

```text
counter++ is not atomic.
```

Use:

```text
AtomicInteger
or
synchronized
```

---

# 13. Correct Counter Using AtomicInteger

```java
import java.util.concurrent.atomic.AtomicInteger;

public class GoodAtomicCounter {

    private static final AtomicInteger counter =
            new AtomicInteger(0);

    public static void increment() {

        counter.incrementAndGet();
    }
}
```

---

# 14. Correct Counter Using synchronized

```java
public class GoodSyncCounter {

    private static int counter = 0;

    public static synchronized void increment() {

        counter++;
    }
}
```

---

# 15. volatile vs AtomicInteger

| Feature | volatile | AtomicInteger |
|---|---|---|
| Visibility | Yes | Yes |
| Atomic increment | No | Yes |
| Good for flags | Yes | Yes |
| Good for counters | No | Yes |
| Lock-free CAS | No | Yes |
| Compound operation safety | No | Limited/simple |

---

# 16. volatile vs synchronized

| Feature | volatile | synchronized |
|---|---|---|
| Visibility | Yes | Yes |
| Mutual exclusion | No | Yes |
| Atomic compound operations | No | Yes |
| Blocking | No | Yes |
| Use case | flags/state visibility | critical sections |

---

# 17. Best Use Case — Stop Flag

Good volatile use:

```java
private volatile boolean running = true;
```

Because operation is simple:

```text
read flag
write flag
```

No compound update.

---

# 18. Backend Example — Background Worker Stop

```java
public class BackgroundWorker {

    private volatile boolean running = true;

    public void start() {

        Thread worker = new Thread(() -> {

            while (running) {

                doWork();
            }
        });

        worker.start();
    }

    public void stop() {

        running = false;
    }

    private void doWork() {

        // background work
    }
}
```

This is common in:

```text
cleanup threads
polling loops
scheduler workers
custom consumers
```

---

# 19. Backend Example — Config Version

```java
private volatile String configVersion = "v1";
```

One thread updates:

```java
configVersion = "v2";
```

Other threads read:

```java
String version = configVersion;
```

Good because:

```text
single reference read/write is enough
```

---

# 20. AtomicReference Alternative

For shared object references:

```java
AtomicReference<Config> configRef;
```

is often better when you need:

```text
compareAndSet
safe swap
CAS update
```

---

# 21. Important: volatile Reference vs Object Mutability

Example:

```java
private volatile Config config;
```

Volatile guarantees visibility of:

```text
config reference
```

But not automatically safe mutation of fields inside Config.

Danger:

```java
config.timeout = 1000;
```

If Config is mutable, still risk.

Better:

```text
use immutable Config object
replace whole reference
```

---

# 22. Safe Config Swap Pattern

```java
private volatile Config config =
        new Config(500, true);

public Config getConfig() {

    return config;
}

public void reload() {

    config = new Config(1000, false);
}
```

Where:

```text
Config is immutable
```

This is safe and clean.

---

# 23. Immutable Config Example

```java
public final class Config {

    private final int timeoutMs;
    private final boolean enabled;

    public Config(int timeoutMs, boolean enabled) {
        this.timeoutMs = timeoutMs;
        this.enabled = enabled;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
```

---

# 24. volatile and Happens-Before

Important Java memory model term:

```text
A write to a volatile variable happens-before
every subsequent read of that same volatile variable.
```

Simple meaning:

```text
Thread B reading volatile sees Thread A's latest write.
```

---

# 25. Instruction Reordering Problem

JVM/CPU may reorder instructions for optimization.

Volatile creates memory barriers that restrict reordering.

Simple idea:

```text
Operations before volatile write cannot be freely moved after it.
Operations after volatile read cannot be freely moved before it.
```

This helps safe visibility.

---

# 26. Common Pattern — Ready Flag

```java
class ReadyDemo {

    private int data = 0;

    private volatile boolean ready = false;

    public void writer() {

        data = 42;

        ready = true;
    }

    public void reader() {

        if (ready) {

            System.out.println(data);
        }
    }
}
```

Because `ready` is volatile:

```text
when reader sees ready=true,
it should also see data=42 written before it.
```

---

# 27. Dry Run — Ready Flag

Writer thread:

```text
data = 42
ready = true
```

Reader thread:

```text
reads ready
sees true
reads data
sees 42
```

Volatile creates visibility ordering.

---

# 28. Common Backend Use Cases

Use volatile for:

```text
shutdown flag
running flag
initialized flag
config reference
feature flag snapshot reference
circuit breaker state visibility
simple status flag
```

---

# 29. Circuit Breaker Example

```java
private volatile boolean open = false;

public boolean allowRequest() {

    return !open;
}

public void openCircuit() {

    open = true;
}
```

This is okay if:

```text
only visibility of state is needed
```

But if you need:

```text
state transitions with counters
```

use:

```text
AtomicReference
AtomicBoolean compareAndSet
lock
```

---

# 30. volatile In Singleton Double-Checked Locking

Classic use:

```java
private static volatile MyService instance;
```

Volatile prevents visibility/reordering issues.

Example:

```java
public class MyService {

    private static volatile MyService instance;

    private MyService() {

    }

    public static MyService getInstance() {

        if (instance == null) {

            synchronized (MyService.class) {

                if (instance == null) {

                    instance = new MyService();
                }
            }
        }

        return instance;
    }
}
```

Modern Spring apps rarely need this manually, but it is an important interview pattern.

---

# 31. Production Warning — Not Distributed

volatile works only inside:

```text
one JVM process
```

It does not synchronize across:

```text
multiple pods
multiple JVMs
multiple microservices
```

For distributed visibility use:

```text
database
Redis
Kafka
Config Server
ZooKeeper/etcd
```

---

# 32. volatile vs Database State

This:

```java
private volatile boolean maintenanceMode;
```

works only in current instance.

If 5 pods exist:

```text
pod-1 may have true
pod-2 may have false
```

Need centralized config for distributed systems.

---

# 33. Real Production Mental Model

```text
volatile
    ↓
fast JVM-local visibility
    ↓
good for flags/references
    ↓
not good for counters
    ↓
not distributed
```

---

# 34. Interview Explanation

If interviewer asks:

```text
What is volatile in Java?
```

Good answer:

```text
volatile ensures visibility of variable updates across threads.
A write to a volatile variable is visible to subsequent reads by other threads.
It also prevents some instruction reordering around that variable.
However, volatile does not provide mutual exclusion and does not make compound
operations like counter++ atomic.
```

Strong backend addition:

```text
volatile is good for shutdown flags or immutable config reference swaps,
but counters should use AtomicInteger or locks.
```

---

# 35. Common Mistakes

## Mistake 1

```text
Using volatile int counter for increments.
```

Wrong.

Use AtomicInteger.

---

## Mistake 2

```text
Thinking volatile means thread-safe for all operations.
```

Wrong.

Only visibility, not mutual exclusion.

---

## Mistake 3

```text
Using volatile mutable object and mutating internals.
```

Dangerous.

Prefer immutable object reference swap.

---

## Mistake 4

```text
Thinking volatile works across Kubernetes pods.
```

Wrong.

Only JVM-local.

---

# 36. Mini Dry Run Summary

```text
Thread A writes volatile flag = false
        ↓
write becomes visible
        ↓
Thread B reads same volatile flag
        ↓
sees latest false
        ↓
stops safely
```

---

# 37. Visual Summary

```text
Shared Flag
    ↓
volatile
    ↓
Visibility guarantee
    ↓
Threads see latest value
```

But:

```text
volatile counter++ ❌
AtomicInteger counter++ ✅
```

---

# 38. What To Remember

```text
volatile = visibility, not atomicity.

Good for flags.

Good for immutable reference swaps.

Bad for counters.

Does not provide mutual exclusion.

Works only inside one JVM.
```

---

# 39. Next File

```text
012_Thread_Communication.md
```

Next you learn:

```text
How threads coordinate
wait/notify
producer-consumer
condition signaling
safe handoff between threads
```
