# 010_Atomic_Variables.md

# MiniConcurrency — 010 Atomic Variables

## 0. Why This File Exists

After learning locks, mutex, and ReadWriteLock, the next question is:

```text
Do we always need locks for small shared updates?
```

Answer:

```text
No.
```

For simple shared values like counters, flags, and references, Java provides:

```text
Atomic variables
```

Atomic variables are heavily used in:

```text
rate limiters
metrics counters
thread pools
connection pools
circuit breakers
cache statistics
Kafka/Redis style internals
```

---

# 1. One-Line Definition

```text
Atomic variable = thread-safe variable that performs operations as one indivisible unit.
```

Simple meaning:

```text
No partial update.
No lost update.
Safe under multiple threads.
```

---

# 2. Why Atomic Variables Exist

This is unsafe:

```java
counter++;
```

Because internally:

```text
read counter
add 1
write counter
```

Multiple threads can interleave these steps.

Atomic variables fix this.

---

# 3. Common Java Atomic Classes

```java
AtomicInteger
AtomicLong
AtomicBoolean
AtomicReference
```

Package:

```java
import java.util.concurrent.atomic.*;
```

---

# 4. Basic AtomicInteger Example

```java
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounterDemo {

    private static final AtomicInteger counter =
            new AtomicInteger(0);

    public static void main(String[] args)
            throws Exception {

        Thread t1 = new Thread(() -> {

            for (int i = 0; i < 100000; i++) {

                counter.incrementAndGet();
            }
        });

        Thread t2 = new Thread(() -> {

            for (int i = 0; i < 100000; i++) {

                counter.incrementAndGet();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(counter.get());
    }
}
```

Expected output:

```text
200000
```

---

# 5. Why This Is Safe

This operation:

```java
counter.incrementAndGet();
```

is atomic.

Meaning:

```text
read + increment + write
happens safely as one operation from thread view.
```

No lost update.

---

# 6. Dry Run — Normal int Fails

Initial:

```text
counter = 0
```

Thread A:

```text
read 0
```

Thread B:

```text
read 0
```

Thread A:

```text
write 1
```

Thread B:

```text
write 1
```

Expected:

```text
2
```

Actual:

```text
1
```

---

# 7. Dry Run — AtomicInteger Works

Initial:

```text
counter = 0
```

Thread A:

```text
atomic increment
0 → 1
success
```

Thread B:

```text
atomic increment
1 → 2
success
```

Final:

```text
2
```

No lost update.

---

# 8. Important Internal Concept — CAS

Most atomic variables are built on:

```text
CAS = Compare-And-Swap
```

CAS means:

```text
Compare current value with expected value.
If same, update to new value.
If different, fail and retry.
```

---

# 9. CAS Mental Model

```text
I think value is 10.
If it is still 10, change it to 11.
If someone changed it, I retry.
```

---

# 10. CAS Formula

```text
CAS(memory, expectedValue, newValue)
```

If:

```text
memory == expectedValue
```

then:

```text
memory = newValue
return true
```

Else:

```text
return false
```

---

# 11. CAS Dry Run — Success

Initial:

```text
counter = 5
```

Thread A wants increment:

```text
expected = 5
newValue = 6
```

CAS checks:

```text
current value == expected?
5 == 5 → yes
```

Update:

```text
counter = 6
```

Result:

```text
success
```

---

# 12. CAS Dry Run — Failure + Retry

Initial:

```text
counter = 5
```

Thread A reads:

```text
expected = 5
newValue = 6
```

Before A updates, Thread B updates:

```text
counter = 6
```

Thread A CAS checks:

```text
current value == expected?
6 == 5 → no
```

CAS fails.

Thread A retries:

```text
expected = 6
newValue = 7
CAS success
```

Final:

```text
counter = 7
```

No lost update.

---

# 13. Lock-Free Meaning

Atomic variables often use lock-free algorithms.

Meaning:

```text
No thread blocks waiting for a mutex lock.
```

Instead:

```text
Try update
If fails, retry
```

This can be faster for simple operations.

---

# 14. AtomicInteger Common Methods

```java
get()
set(value)
incrementAndGet()
getAndIncrement()
decrementAndGet()
addAndGet(value)
compareAndSet(expected, update)
```

---

# 15. incrementAndGet vs getAndIncrement

```java
incrementAndGet()
```

means:

```text
increment first, then return new value
```

Example:

```text
0 → 1
returns 1
```

```java
getAndIncrement()
```

means:

```text
return old value, then increment
```

Example:

```text
0 → 1
returns 0
```

---

# 16. Java Example — Difference

```java
import java.util.concurrent.atomic.AtomicInteger;

public class IncrementDifferenceDemo {

    public static void main(String[] args) {

        AtomicInteger value = new AtomicInteger(0);

        System.out.println(value.incrementAndGet());

        System.out.println(value.getAndIncrement());

        System.out.println(value.get());
    }
}
```

Output:

```text
1
1
2
```

Dry run:

```text
Start value = 0

incrementAndGet:
0 → 1, returns 1

getAndIncrement:
returns 1, then 1 → 2

get:
returns 2
```

---

# 17. compareAndSet Example

```java
import java.util.concurrent.atomic.AtomicInteger;

public class CompareAndSetDemo {

    public static void main(String[] args) {

        AtomicInteger value = new AtomicInteger(10);

        boolean success =
                value.compareAndSet(10, 20);

        System.out.println(success);
        System.out.println(value.get());
    }
}
```

Output:

```text
true
20
```

---

# 18. compareAndSet Failure Example

```java
AtomicInteger value = new AtomicInteger(10);

boolean success =
        value.compareAndSet(5, 20);

System.out.println(success);
System.out.println(value.get());
```

Output:

```text
false
10
```

Why?

```text
Expected value was 5
Actual value was 10
So update failed
```

---

# 19. AtomicBoolean Example

Useful for flags:

```java
import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicFlagDemo {

    private final AtomicBoolean running =
            new AtomicBoolean(true);

    public void stop() {

        running.set(false);
    }

    public boolean isRunning() {

        return running.get();
    }
}
```

Used for:

```text
shutdown flags
background workers
circuit breaker open/closed state
```

---

# 20. AtomicReference Example

AtomicReference safely updates object reference.

```java
import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceDemo {

    private final AtomicReference<String> config =
            new AtomicReference<>("v1");

    public String getConfig() {

        return config.get();
    }

    public void updateConfig(String newConfig) {

        config.set(newConfig);
    }
}
```

Useful for:

```text
hot config reload
route table reference
feature flag snapshot
```

---

# 21. Spring Boot Example — Request Counter

Bad:

```java
private int requestCount = 0;

requestCount++;
```

Safe:

```java
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class MetricsController {

    private final AtomicInteger requestCount =
            new AtomicInteger(0);

    @GetMapping("/orders")
    public String orders() {

        int current =
                requestCount.incrementAndGet();

        return "count = " + current;
    }
}
```

---

# 22. Backend Use Case — Rate Limiter

MiniRateLimiter:

```text
request count per user
```

Atomic counter can help:

```java
AtomicInteger tokens =
        new AtomicInteger(100);
```

But be careful:

```text
distributed rate limiter needs Redis/database
because local AtomicInteger works only inside one JVM.
```

---

# 23. Backend Use Case — Circuit Breaker

MiniCircuitBreaker needs:

```text
failure count
success count
state flag
```

Atomic variables can help:

```java
AtomicInteger failureCount
AtomicBoolean open
AtomicLong lastFailureTime
```

---

# 24. Backend Use Case — Metrics

Metrics counters:

```text
total requests
failed requests
cache hits
cache misses
```

Use:

```text
AtomicLong
LongAdder
```

For very high contention, `LongAdder` often performs better.

---

# 25. AtomicInteger vs synchronized

| Feature | AtomicInteger | synchronized |
|---|---|---|
| Locking | Lock-free style | Lock-based |
| Good for | Single variable | Complex critical section |
| Blocking | No normal blocking | Threads may block |
| Performance | Good for simple counters | Good for grouped logic |
| Multiple variables | Hard | Easier |

---

# 26. When Atomic Variables Are Good

Use atomic variables for:

```text
simple counters
boolean flags
state references
single value updates
metrics
CAS-based state transition
```

---

# 27. When Atomic Variables Are NOT Enough

Not enough for multi-step business logic:

```java
if (stock > 0) {
    stock--;
    createOrder();
}
```

This is more than one atomic operation.

Need:

```text
lock
transaction
database isolation
optimistic locking
```

---

# 28. Example — Atomic Mistake

```java
if (stock.get() > 0) {

    stock.decrementAndGet();
}
```

Problem:

```text
get() and decrementAndGet() are separate operations.
```

Two threads can both see stock > 0.

Better:

```text
Use CAS loop
or lock
or DB transaction
```

---

# 29. CAS Loop Example — Safe Stock Decrement

```java
import java.util.concurrent.atomic.AtomicInteger;

public class StockService {

    private final AtomicInteger stock =
            new AtomicInteger(10);

    public boolean purchase() {

        while (true) {

            int current = stock.get();

            if (current <= 0) {

                return false;
            }

            int updated = current - 1;

            if (stock.compareAndSet(current, updated)) {

                return true;
            }
        }
    }
}
```

---

# 30. Dry Run — CAS Loop Purchase

Initial:

```text
stock = 1
```

Thread A:

```text
current = 1
updated = 0
```

Thread B:

```text
current = 1
updated = 0
```

Thread A CAS:

```text
stock is 1 → update to 0 → success
```

Thread B CAS:

```text
expected 1 but stock is 0 → fail
```

Thread B retries:

```text
current = 0
return false
```

Only one purchase succeeds.

---

# 31. LongAdder Note

For very high contention counters:

```java
LongAdder
```

can be better than:

```java
AtomicLong
```

Why?

```text
It spreads updates across internal cells
and combines values later.
```

Good for:

```text
metrics counters
high-throughput statistics
```

Not ideal when you need exact immediate value after every update.

---

# 32. Production Warning — JVM Local Only

Atomic variables protect data only inside:

```text
one JVM process
```

If you run:

```text
3 Kubernetes pods
```

each pod has its own AtomicInteger.

So:

```text
AtomicInteger is not distributed.
```

For distributed counters use:

```text
Redis INCR
database update
Kafka aggregation
distributed coordination
```

---

# 33. Real Backend Mental Model

```text
AtomicInteger
    ↓
safe local counter
    ↓
fast for one JVM
    ↓
not enough for multi-instance systems
```

---

# 34. Common Production Uses

```text
request counters
connection counters
thread pool active count
retry count
failure count
circuit breaker state
feature flag reference
config snapshot
```

---

# 35. Interview Explanation

If interviewer asks:

```text
What is AtomicInteger?
```

Good answer:

```text
AtomicInteger is a thread-safe integer that supports atomic operations
like increment and compare-and-set without using explicit synchronized blocks.
It usually relies on CAS, where the update succeeds only if the current value
matches the expected value; otherwise it retries.
```

Strong backend addition:

```text
Atomic variables are great for local counters and flags, but they do not
solve distributed concurrency across multiple JVM instances.
```

---

# 36. Common Mistakes

## Mistake 1

```text
Thinking AtomicInteger solves all concurrency problems.
```

Wrong.

Only simple value updates.

---

## Mistake 2

```text
Using get() then set() as if atomic together.
```

Wrong.

Combined operation may still race.

---

## Mistake 3

```text
Using AtomicInteger for distributed counters.
```

Wrong.

Only JVM-local.

---

## Mistake 4

```text
Ignoring CAS retry cost under heavy contention.
```

High contention can cause many retries.

---

# 37. Mini Dry Run Summary

```text
Thread reads expected value
        ↓
computes new value
        ↓
CAS checks current value
        ↓
if same → update success
        ↓
if changed → retry
```

---

# 38. Visual Summary

```text
Shared Counter
     ↓
AtomicInteger
     ↓
CAS
     ↓
No lost update
     ↓
Thread-safe local counter
```

---

# 39. What To Remember

```text
Atomic variables protect simple shared values.

CAS = compare current value with expected, then update.

AtomicInteger is good for counters.

AtomicBoolean is good for flags.

AtomicReference is good for shared object references.

Atomic variables are JVM-local, not distributed.
```

---

# 40. Next File

```text
011_volatile_Keyword.md
```

Next you learn:

```text
visibility problem
CPU cache
volatile variable
happens-before
why volatile is not atomic
```
