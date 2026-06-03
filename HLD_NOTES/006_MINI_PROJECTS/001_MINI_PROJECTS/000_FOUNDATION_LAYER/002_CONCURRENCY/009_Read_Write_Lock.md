# 009_Read_Write_Lock.md

# MiniConcurrency — 009 Read Write Lock

## 0. Why This File Exists

After learning `synchronized`, mutex, and `ReentrantLock`, the next question is:

```text
Do we really need to block readers from reading together?
```

In many backend systems:

```text
Reads are frequent
Writes are rare
```

Example:

```text
Config cache
User profile cache
Product catalog cache
Routing table
Service discovery registry
Feature flags
```

If many threads only read, allowing them to read together improves performance.

This is why we use:

```text
ReadWriteLock
```

---

# 1. One-Line Definition

```text
ReadWriteLock allows multiple readers at the same time,
but only one writer at a time.
```

Simple rule:

```text
Many readers OR one writer.
```

---

# 2. Real Mental Model

Imagine a library book.

```text
Many people can read same book together.
```

But if someone is editing the book:

```text
Nobody should read while editing.
Only one editor can edit at a time.
```

So:

```text
Readers can share.
Writer needs exclusive access.
```

---

# 3. Why Normal Mutex Is Not Enough

With normal mutex:

```text
Reader 1 enters
Reader 2 waits
Reader 3 waits
Writer waits
```

Even if all readers are only reading.

This reduces throughput.

With read-write lock:

```text
Reader 1 enters
Reader 2 enters
Reader 3 enters
Writer waits
```

Better for read-heavy systems.

---

# 4. Core Rule

```text
Multiple readers allowed together.
Only one writer allowed.
Writer blocks readers.
Readers block writer.
```

---

# 5. Visual Model

```text
Read Lock:
Reader A ✅
Reader B ✅
Reader C ✅

Write Lock:
Writer A ✅
Reader A ❌
Writer B ❌
```

---

# 6. Java Class

Java provides:

```java
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
```

Common implementation:

```java
ReentrantReadWriteLock
```

---

# 7. Basic Structure

```java
ReadWriteLock rwLock = new ReentrantReadWriteLock();

Lock readLock = rwLock.readLock();
Lock writeLock = rwLock.writeLock();
```

Use:

```java
readLock.lock();
readLock.unlock();

writeLock.lock();
writeLock.unlock();
```

---

# 8. Java Example — Simple Cache

```java
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConfigCache {

    private final Map<String, String> cache = new HashMap<>();

    private final ReadWriteLock rwLock =
            new ReentrantReadWriteLock();

    private final Lock readLock =
            rwLock.readLock();

    private final Lock writeLock =
            rwLock.writeLock();

    public String get(String key) {

        readLock.lock();

        try {

            return cache.get(key);

        } finally {

            readLock.unlock();
        }
    }

    public void put(String key, String value) {

        writeLock.lock();

        try {

            cache.put(key, value);

        } finally {

            writeLock.unlock();
        }
    }
}
```

---

# 9. Step-by-Step Dry Run — Multiple Readers

Initial:

```text
cache = { "timeout" : "500ms" }
```

Thread A:

```text
calls get("timeout")
acquires read lock
```

Thread B:

```text
calls get("timeout")
also acquires read lock
```

Thread C:

```text
calls get("timeout")
also acquires read lock
```

All three read together.

Result:

```text
High read throughput.
```

---

# 10. Step-by-Step Dry Run — Writer Arrives

Thread A and B are reading:

```text
Reader A holds read lock
Reader B holds read lock
```

Thread W tries:

```text
put("timeout", "1s")
```

Writer needs write lock.

But read locks are active.

So:

```text
Writer waits.
```

When all readers release:

```text
Writer gets write lock.
```

During write:

```text
new readers wait.
```

---

# 11. Step-by-Step Dry Run — Writer Active

Writer W:

```text
acquires write lock
updates cache
```

Reader A tries:

```text
get("timeout")
```

Reader A waits because:

```text
Writer has exclusive access.
```

Writer finishes:

```text
releases write lock
```

Reader A continues.

---

# 12. Read Lock vs Write Lock

| Lock Type | Allows Multiple Threads? | Blocks What? |
|---|---:|---|
| Read lock | Yes, many readers | Writers |
| Write lock | No, only one writer | Readers and writers |

---

# 13. Why ReadWriteLock Improves Performance

If workload is:

```text
95% reads
5% writes
```

Normal mutex serializes everything:

```text
read
read
read
write
read
```

ReadWriteLock allows:

```text
read + read + read together
write alone
```

Better throughput.

---

# 14. Backend Example — Feature Flag Cache

Feature flags are read on every request:

```text
isNewCheckoutEnabled()
```

But updated rarely.

ReadWriteLock is useful:

```text
many request threads read flags
admin/config thread updates flags
```

---

# 15. Backend Example — Service Discovery Registry

MiniServiceDiscovery:

```text
Gateway reads service instances frequently.
Services register/deregister rarely.
```

Good fit:

```text
ReadWriteLock
```

Flow:

```text
Many clients discover instances → read lock
Service register/remove → write lock
```

---

# 16. Backend Example — API Gateway Route Table

MiniGateway:

```text
Every request reads route config.
Config reload writes route config rarely.
```

Good fit:

```text
ReadWriteLock
```

---

# 17. Important: HashMap Still Needs Protection

This is unsafe:

```java
HashMap<String, String> map = new HashMap<>();
```

If multiple threads read/write without protection:

```text
unsafe
```

With ReadWriteLock:

```text
safe if every read uses read lock
and every write uses write lock.
```

---

# 18. Rule: All Access Must Follow Same Lock

Bad:

```java
public String get(String key) {
    return cache.get(key);
}
```

Good:

```java
public String get(String key) {
    readLock.lock();
    try {
        return cache.get(key);
    } finally {
        readLock.unlock();
    }
}
```

If some code bypasses lock:

```text
thread safety is broken.
```

---

# 19. try-finally Is Mandatory

Bad:

```java
readLock.lock();
return cache.get(key);
readLock.unlock();
```

If exception happens:

```text
lock may never release.
```

Correct:

```java
readLock.lock();

try {
    return cache.get(key);
} finally {
    readLock.unlock();
}
```

Same for write lock.

---

# 20. ReadWriteLock vs ReentrantLock

| Feature | ReentrantLock | ReadWriteLock |
|---|---|---|
| Multiple readers | No | Yes |
| Writer exclusivity | Yes | Yes |
| Good for read-heavy | Moderate | Excellent |
| Simpler | Yes | Slightly complex |
| Risk | contention | starvation/complexity |

---

# 21. When To Use ReadWriteLock

Use when:

```text
Reads are much more frequent than writes.
Read operation takes meaningful time.
Shared data must be consistent.
```

Examples:

```text
config cache
routing table
metadata registry
feature flag map
in-memory catalog
```

---

# 22. When NOT To Use ReadWriteLock

Avoid when:

```text
Writes are frequent.
Critical section is very small.
ConcurrentHashMap is enough.
Complexity is not worth it.
```

Example:

```text
simple counter
```

Use:

```text
AtomicInteger
```

---

# 23. Important Starvation Problem

If readers keep coming continuously:

```text
writer may wait too long.
```

This is called:

```text
writer starvation
```

---

# 24. Fair ReadWriteLock

You can create fair lock:

```java
new ReentrantReadWriteLock(true);
```

Fair mode:

```text
threads served roughly in waiting order
```

Tradeoff:

```text
less starvation
but lower throughput
```

---

# 25. Non-Fair vs Fair

Default:

```java
new ReentrantReadWriteLock()
```

means:

```text
non-fair
```

Usually better throughput.

Fair:

```java
new ReentrantReadWriteLock(true)
```

more predictable, but slower.

---

# 26. Lock Downgrading

Advanced concept:

```text
Write lock → read lock
```

Allowed pattern:

```text
Acquire write lock
Update data
Acquire read lock
Release write lock
Continue reading safely
Release read lock
```

This is called:

```text
lock downgrading
```

---

# 27. Lock Upgrading Warning

Dangerous:

```text
Read lock → write lock
```

Can deadlock if multiple readers try upgrade.

Avoid unless very carefully designed.

---

# 28. Simple Production Cache Example

```java
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RouteConfigStore {

    private final Map<String, String> routes =
            new HashMap<>();

    private final ReentrantReadWriteLock lock =
            new ReentrantReadWriteLock();

    public String findRoute(String path) {

        lock.readLock().lock();

        try {

            return routes.get(path);

        } finally {

            lock.readLock().unlock();
        }
    }

    public void reloadRoutes(Map<String, String> newRoutes) {

        lock.writeLock().lock();

        try {

            routes.clear();
            routes.putAll(newRoutes);

        } finally {

            lock.writeLock().unlock();
        }
    }
}
```

---

# 29. Dry Run — Gateway Route Config

Initial routes:

```text
/api/users  -> user-service
/api/orders -> order-service
```

100 requests arrive:

```text
all call findRoute()
```

They can read together.

Admin reloads config:

```text
reloadRoutes()
```

Writer waits until readers finish.

Then writer updates routes safely.

During update:

```text
new readers wait.
```

After update:

```text
readers continue with new route table.
```

---

# 30. Alternative: ConcurrentHashMap

For simple map reads/writes, often use:

```java
ConcurrentHashMap
```

It may be simpler and faster.

ReadWriteLock is useful when:

```text
multiple operations must be protected together
```

Example:

```java
routes.clear();
routes.putAll(newRoutes);
```

This must be atomic as a group.

---

# 31. Important Production Warning

ReadWriteLock protects only:

```text
inside one JVM process
```

Not across:

```text
multiple pods
multiple JVMs
multiple microservice instances
```

For distributed data:

```text
database transaction
Redis lock
ZooKeeper/etcd
Kafka partition ordering
```

---

# 32. Real Production Symptoms of Bad Lock Design

Bad ReadWriteLock usage may cause:

```text
writer starvation
blocked readers
deadlock during upgrade
stale reads
low throughput
latency spikes
```

---

# 33. Thread States

When waiting for lock:

```text
threads may be WAITING / BLOCKED depending on implementation details
```

From a debugging view, you may see:

```text
parking
waiting on condition
blocked by lock
```

in thread dumps.

---

# 34. Interview Explanation

If interviewer asks:

```text
What is ReadWriteLock?
```

Good answer:

```text
ReadWriteLock separates locking into read lock and write lock.
Multiple readers can hold the read lock simultaneously, but write lock is exclusive.
It improves performance for read-heavy workloads while still protecting shared mutable state.
```

Strong backend addition:

```text
It is useful for config caches, routing tables, and service discovery registries,
where reads are frequent and updates are rare.
```

---

# 35. Common Mistakes

## Mistake 1

```text
Using ReadWriteLock for write-heavy workloads.
```

Can hurt performance.

---

## Mistake 2

```text
Forgetting unlock in finally.
```

Dangerous.

---

## Mistake 3

```text
Bypassing locks in some methods.
```

Breaks thread safety.

---

## Mistake 4

```text
Trying to upgrade read lock to write lock.
```

Can deadlock.

---

# 36. Mini Dry Run Summary

```text
Many readers
    ↓
read lock shared
    ↓
writer arrives
    ↓
waits for readers to finish
    ↓
writer gets exclusive lock
    ↓
updates data
    ↓
readers continue
```

---

# 37. Visual Summary

```text
Read-heavy shared data
        ↓
ReadWriteLock
        ↓
Many readers together
        ↓
One writer alone
        ↓
Better throughput than mutex
```

---

# 38. What To Remember

```text
ReadWriteLock = many readers OR one writer.

Best for read-heavy workloads.

Every read must use read lock.

Every write must use write lock.

Use finally to unlock.

Avoid read-to-write upgrade.
```

---

# 39. Next File

```text
010_Atomic_Variables.md
```

Next you learn:

```text
AtomicInteger
CAS
lock-free updates
compare-and-swap
when atomics beat locks
```
