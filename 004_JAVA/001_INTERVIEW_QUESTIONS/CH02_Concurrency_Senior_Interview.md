# CH 02 --- Concurrency & Multithreading

## Senior Java Distributed Backend Interview Series

**Target:** \~50 interview questions\
**Goal:** Master Java concurrency from threads → synchronization → JMM →
locks/CAS → executors → asynchronous programming → virtual threads, with
senior-level failure analysis and production reasoning.

------------------------------------------------------------------------

# Chapter Map

``` text
CH 02 — CONCURRENCY (~50 Q)
|
+-- 1. Threads / Synchronization (10)
|   +-- Process vs thread
|   +-- Thread lifecycle
|   +-- Runnable / Callable
|   +-- start() vs run()
|   +-- Race condition
|   +-- synchronized
|   +-- Monitor / intrinsic lock
|   +-- wait / notify / notifyAll
|   +-- Deadlock / livelock / starvation
|   +-- Thread safety strategies
|
+-- 2. volatile / JMM / Happens-Before (10)
|   +-- Java Memory Model
|   +-- Visibility / atomicity / ordering
|   +-- volatile
|   +-- volatile vs synchronized
|   +-- Happens-before
|   +-- Reordering
|   +-- Safe publication
|   +-- final-field semantics
|   +-- Double-checked locking
|   +-- Immutability and thread safety
|
+-- 3. Locks / Atomic / CAS (10)
|   +-- Lock API
|   +-- ReentrantLock
|   +-- tryLock
|   +-- ReadWriteLock
|   +-- StampedLock
|   +-- Atomic classes
|   +-- CAS
|   +-- ABA problem
|   +-- LongAdder
|   +-- Lock-free trade-offs
|
+-- 4. Executors / ThreadPool (10)
|   +-- Executor framework
|   +-- ExecutorService
|   +-- ThreadPoolExecutor
|   +-- corePoolSize / maxPoolSize
|   +-- Work queues
|   +-- Rejection policies
|   +-- CPU vs I/O pool sizing
|   +-- Thread pool exhaustion
|   +-- ScheduledExecutorService
|   +-- ForkJoinPool / work stealing
|
+-- 5. CompletableFuture / Virtual Threads (10)
    +-- Future limitations
    +-- CompletableFuture
    +-- thenApply / thenCompose
    +-- thenCombine
    +-- Exception handling
    +-- Async executor selection
    +-- allOf / fan-out fan-in
    +-- Virtual threads
    +-- Platform vs virtual threads
    +-- Production design trade-offs
```

------------------------------------------------------------------------

# Mental Model --- Why Concurrency Is Hard

``` text
                SHARED STATE
                     |
          +----------+----------+
          |                     |
       Thread A               Thread B
          |                     |
        read x                 read x
          |                     |
       modify                  modify
          |                     |
        write x                 write x
          \                     /
           +-------- RACE -----+
                     |
                     v
             WRONG / UNSTABLE RESULT
```

Concurrency correctness is mainly about controlling:

``` text
VISIBILITY   -> Can another thread see my write?
ATOMICITY    -> Can an operation be interrupted/interleaved?
ORDERING     -> Can operations be observed in another order?
```

------------------------------------------------------------------------

# 1. Threads / Synchronization --- 10 Questions

## Q1. What is the difference between a process and a thread?

### Short answer

A **process** is an independently executing program with its own address
space. Threads are execution units inside a process and normally share
the process heap/resources.

``` text
Operating System
|
+-- Process A
|   |
|   +-- Heap -------------------- shared
|   +-- Files / sockets --------- shared
|   |
|   +-- Thread T1
|   |    +-- Stack
|   |
|   +-- Thread T2
|        +-- Stack
|
+-- Process B
    +-- Separate memory space
```

### Senior point

Threads are cheaper to communicate between because they share memory,
but that same shared memory introduces race conditions and
synchronization requirements.

------------------------------------------------------------------------

## Q2. Explain the Java thread lifecycle.

``` text
                start()
NEW ----------------------------> RUNNABLE
                                    |
                +-------------------+-------------------+
                |                                       |
                v                                       v
             RUNNING                             BLOCKED / WAITING
                |                                       |
                | sleep/wait/lock/I/O                   |
                +--------------------<------------------+
                |
                | run() finishes / exception
                v
            TERMINATED
```

Java's official `Thread.State` values are:

``` text
NEW
RUNNABLE
BLOCKED
WAITING
TIMED_WAITING
TERMINATED
```

**Important:** Java's `RUNNABLE` state covers threads that may be
running or ready to run at the OS level.

------------------------------------------------------------------------

## Q3. Runnable vs Callable?

``` java
Runnable task = () -> {
    System.out.println("processing");
};

Callable<Integer> taskWithResult = () -> {
    return 42;
};
```

  -----------------------------------------------------------------------
  Runnable                            Callable
  ----------------------------------- -----------------------------------
  `run()`                             `call()`

  no result                           returns result

  cannot declare checked exception    can throw checked exception

  often used with Thread/Executor     commonly submitted to
                                      ExecutorService
  -----------------------------------------------------------------------

``` java
ExecutorService executor = Executors.newSingleThreadExecutor();

Future<Integer> future = executor.submit(() -> 42);

Integer value = future.get();
```

### Senior follow-up

`Future.get()` blocks the calling thread. This limitation is one reason
`CompletableFuture` is useful for composition.

------------------------------------------------------------------------

## Q4. What is the difference between `start()` and `run()`?

``` java
Thread t = new Thread(() ->
    System.out.println(Thread.currentThread().getName())
);

t.run();    // normal method invocation
t.start();  // starts a new thread
```

``` text
t.run()
   |
   +--> current thread
        executes run()

t.start()
   |
   +--> JVM/OS creates/schedules new thread
              |
              +--> new thread executes run()
```

Calling `start()` twice on the same `Thread` causes
`IllegalThreadStateException`.

------------------------------------------------------------------------

## Q5. What is a race condition?

``` java
class Counter {
    int count = 0;

    void increment() {
        count++;
    }
}
```

`count++` is conceptually:

``` text
READ count
   |
   v
ADD 1
   |
   v
WRITE count
```

Two threads can interleave:

``` text
Initial count = 10

Thread A                 Thread B
--------                 --------
read 10
                         read 10
add -> 11
                         add -> 11
write 11
                         write 11

Expected = 12
Actual   = 11
```

### Fix options

-   synchronization
-   atomic classes
-   locks
-   confinement
-   immutability
-   avoiding shared mutable state

------------------------------------------------------------------------

## Q6. How does `synchronized` work?

``` java
public synchronized void increment() {
    count++;
}
```

Equivalent conceptual model:

``` text
Thread A ----+
             |
Thread B ----+--> MONITOR / LOCK --> Critical Section
             |
Thread C ----+
```

Only one thread can own a given monitor at a time.

``` java
synchronized (lock) {
    // critical section
}
```

### Instance vs static synchronization

``` text
synchronized instance method
        |
        v
locks: this

static synchronized method
        |
        v
locks: Class object
```

### Senior trap

These two methods do **not** necessarily block each other if they lock
different monitor objects.

------------------------------------------------------------------------

## Q7. What is a monitor / intrinsic lock?

Every Java object can conceptually act as a monitor.

``` java
private final Object lock = new Object();

void update() {
    synchronized (lock) {
        // lock object's monitor
    }
}
```

``` text
Object Monitor
|
+-- Owner: Thread A
|
+-- Entry set
|   +-- Thread B
|   +-- Thread C
|
+-- Wait set
    +-- Thread D
```

Threads attempting to enter the synchronized block while another thread
owns the monitor become `BLOCKED`.

------------------------------------------------------------------------

## Q8. Explain `wait()`, `notify()` and `notifyAll()`.

``` java
synchronized (queue) {
    while (queue.isEmpty()) {
        queue.wait();
    }

    process(queue.remove());
}
```

Producer:

``` java
synchronized (queue) {
    queue.add(item);
    queue.notifyAll();
}
```

### Visual

``` text
Consumer
   |
check condition
   |
false
   |
 wait()
   |
releases monitor
   v
WAIT SET

Producer
   |
changes condition
   |
notifyAll()
   |
Consumer wakes
   |
must reacquire monitor
   |
check condition AGAIN
```

Always prefer checking conditions in a `while`, not an `if`, because
wakeups do not guarantee the condition is now true.

### Production preference

For most application code, higher-level constructs such as
`BlockingQueue`, executors, latches, semaphores, or concurrent
collections are preferable to hand-written wait/notify protocols.

------------------------------------------------------------------------

## Q9. Deadlock vs livelock vs starvation?

### Deadlock

``` text
Thread A owns Lock 1
     |
     +---- waits for Lock 2
                      ^
                      |
Thread B owns Lock 2--+
     |
     +---- waits for Lock 1
                      ^
                      |
                      +--- Thread A owns it
```

Common deadlock conditions:

``` text
Mutual exclusion
      +
Hold and wait
      +
No preemption
      +
Circular wait
      =
DEADLOCK
```

Prevention techniques:

-   consistent lock ordering
-   minimize nested locks
-   `tryLock()` + timeout
-   reduce shared state

### Livelock

Threads are active and responding to each other but make no useful
progress.

### Starvation

A thread continually fails to get CPU/lock/resource access.

------------------------------------------------------------------------

## Q10. What strategies can make code thread-safe?

``` text
THREAD SAFETY
|
+-- Avoid shared state
|   +-- local variables
|   +-- thread confinement
|
+-- Immutable state
|
+-- Synchronization
|
+-- Explicit locks
|
+-- Atomic variables
|
+-- Concurrent collections
|
+-- Message passing / queues
|
+-- Database-level concurrency control
```

### Senior principle

The best synchronization is often **not sharing mutable state at all**.

------------------------------------------------------------------------

# 2. volatile / JMM / Happens-Before --- 10 Questions

## Q11. What is the Java Memory Model (JMM)?

The JMM defines rules for how Java threads interact through memory,
including visibility and ordering guarantees.

A useful mental model:

``` text
                  SHARED HEAP
                 value = 100
                    ^   ^
                   /     \
                  /       \
        Thread A             Thread B
        working              working
        memory               memory
```

Without appropriate synchronization, you cannot reason as though every
write by A immediately becomes visible to B.

The JMM provides rules through mechanisms such as:

-   monitor lock/unlock
-   `volatile`
-   thread start/join
-   final-field initialization
-   concurrent utilities

------------------------------------------------------------------------

## Q12. Explain visibility, atomicity and ordering.

### Visibility

``` text
Thread A: flag = true
Thread B: while (!flag) { }
```

Will B necessarily observe A's update without synchronization? Do not
rely on it.

### Atomicity

An atomic operation appears indivisible with respect to competing
threads.

``` java
count++; // NOT atomic
```

### Ordering

Compiler/JIT/CPU optimizations may reorder operations when allowed, as
long as single-thread semantics are preserved.

Concurrency requires additional ordering guarantees.

------------------------------------------------------------------------

## Q13. What does `volatile` guarantee?

``` java
private volatile boolean running = true;

void stop() {
    running = false;
}

void execute() {
    while (running) {
        // work
    }
}
```

`volatile` primarily provides:

``` text
WRITE volatile variable
          |
          | visibility + ordering guarantee
          v
READ same volatile variable
```

A write to a volatile variable **happens-before** every subsequent read
of that variable.

### What volatile does NOT provide

``` java
volatile int count = 0;

count++; // still not atomic
```

------------------------------------------------------------------------

## Q14. `volatile` vs `synchronized`?

``` text
volatile
|
+-- visibility
+-- ordering constraints
+-- no mutual exclusion
+-- compound operations still unsafe

synchronized
|
+-- mutual exclusion
+-- visibility
+-- happens-before via unlock -> later lock
+-- protects compound invariants
```

Use `volatile` when a variable represents simple independently
readable/writable state and no compound invariant requires locking.

------------------------------------------------------------------------

## Q15. What is a happens-before relationship?

Happens-before is a JMM ordering relation that guarantees memory
visibility.

Important examples:

``` text
PROGRAM ORDER
statement A
   |
   v
statement B

MONITOR
unlock(lock)
   |
   v
later lock(lock)

VOLATILE
write volatile x
   |
   v
later read volatile x

THREAD START
actions before start()
   |
   v
actions in started thread

THREAD JOIN
actions in worker
   |
   v
successful return from join()
```

### Senior answer

Happens-before is stronger than saying one operation occurred earlier in
wall-clock time. It gives a defined memory-ordering/visibility
guarantee.

------------------------------------------------------------------------

## Q16. What is instruction reordering and why does it matter?

Source:

``` java
data = loadData();
ready = true;
```

Without a synchronization relationship, another thread cannot simply
assume it can use `ready` as a safe publication signal.

``` text
Writer                    Reader

data = object
ready = true        --->  if (ready)
                              use(data)
```

Synchronization creates the required ordering/visibility guarantees.

------------------------------------------------------------------------

## Q17. What is safe publication?

Safe publication means making an object available to other threads
through a mechanism that guarantees they see it in a properly
initialized state.

Examples:

``` text
Safe publication
|
+-- static initialization
+-- volatile reference
+-- synchronized block
+-- thread-safe collection
+-- final-field guarantees (with correct construction)
```

Example:

``` java
private volatile Config config;

public void reload() {
    config = new Config(...);
}
```

------------------------------------------------------------------------

## Q18. What are final-field semantics?

Correctly constructed objects with `final` fields receive special JMM
initialization guarantees.

``` java
final class UserConfig {
    private final int timeout;
    private final String region;

    UserConfig(int timeout, String region) {
        this.timeout = timeout;
        this.region = region;
    }
}
```

### Critical rule

Do not allow `this` to escape from the constructor before construction
is complete.

``` text
constructor
   |
initialize final state
   |
construction completes
   |
publish object
```

This is one reason immutable objects are easier to reason about
concurrently.

------------------------------------------------------------------------

## Q19. Why did classic double-checked locking require `volatile`?

``` java
class Singleton {
    private static volatile Singleton instance;

    static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

Conceptually, construction involves:

``` text
1. allocate memory
2. initialize object
3. publish reference
```

Without the required memory-ordering guarantee, unsafe publication
historically made double-checked locking broken.

### Better alternatives

Often prefer simpler initialization patterns:

``` java
private static final Singleton INSTANCE = new Singleton();
```

or initialization-on-demand holder when lazy initialization is actually
required.

------------------------------------------------------------------------

## Q20. Why is immutability useful in concurrency?

``` java
public record Price(String symbol, BigDecimal amount) {}
```

``` text
Mutable object
Thread A ----> modifies
Thread B ----> reads
      |
 synchronization required

Immutable object
Thread A ----+
             +----> read-only object
Thread B ----+
```

Immutable objects eliminate many races because their state cannot change
after construction.

------------------------------------------------------------------------

# 3. Locks / Atomic / CAS --- 10 Questions

## Q21. `synchronized` vs `Lock` API?

`Lock` provides capabilities beyond basic monitor syntax.

``` java
Lock lock = new ReentrantLock();

lock.lock();
try {
    update();
} finally {
    lock.unlock();
}
```

Advantages can include:

-   `tryLock()`
-   interruptible acquisition
-   timed acquisition
-   multiple `Condition`s
-   optional fairness policies

### Rule

Always unlock in `finally`.

------------------------------------------------------------------------

## Q22. What is `ReentrantLock` and what does reentrant mean?

Reentrant means the thread that already owns the lock can acquire it
again.

``` java
lock.lock();      // hold count = 1
try {
    lock.lock();  // hold count = 2
    try {
        // work
    } finally {
        lock.unlock(); // count = 1
    }
} finally {
    lock.unlock();     // count = 0
}
```

Java intrinsic locks are also reentrant.

------------------------------------------------------------------------

## Q23. Why is `tryLock()` useful?

``` java
if (lock.tryLock(200, TimeUnit.MILLISECONDS)) {
    try {
        update();
    } finally {
        lock.unlock();
    }
} else {
    // fallback / fail / retry
}
```

It can help avoid waiting indefinitely and can be part of
deadlock-avoidance strategies.

``` text
Acquire lock?
   |
 +-- YES -> critical section
 |
 +-- NO after timeout
       |
       +--> retry / fail fast / fallback
```

------------------------------------------------------------------------

## Q24. What is `ReadWriteLock`?

``` text
                ReadWriteLock
                     |
        +------------+------------+
        |                         |
     READ LOCK                 WRITE LOCK
        |                         |
multiple readers              exclusive
can coexist                   access
```

``` java
ReadWriteLock rw = new ReentrantReadWriteLock();

rw.readLock().lock();
try {
    return cache.get(key);
} finally {
    rw.readLock().unlock();
}
```

It may help for read-heavy workloads, but extra complexity/overhead
means it should be justified by measurement.

------------------------------------------------------------------------

## Q25. What is `StampedLock`?

`StampedLock` supports:

-   write locks
-   read locks
-   optimistic reads

``` java
long stamp = lock.tryOptimisticRead();
double currentX = x;
double currentY = y;

if (!lock.validate(stamp)) {
    stamp = lock.readLock();
    try {
        currentX = x;
        currentY = y;
    } finally {
        lock.unlockRead(stamp);
    }
}
```

### Senior caveat

`StampedLock` is not reentrant and is easier to misuse. Use it only when
its benefits are meaningful.

------------------------------------------------------------------------

## Q26. What are atomic classes?

Examples:

``` text
AtomicInteger
AtomicLong
AtomicBoolean
AtomicReference
```

``` java
AtomicInteger counter = new AtomicInteger();

counter.incrementAndGet();
```

They support thread-safe atomic operations without using a traditional
explicit lock for each operation.

------------------------------------------------------------------------

## Q27. What is CAS?

CAS = Compare-And-Set / Compare-And-Swap.

Conceptually:

``` text
memory contains 10

CAS(
 expected = 10,
 newValue = 11
)

        |
        +-- actual == expected?
                 |
          +------+------+
          |             |
         YES           NO
          |             |
      write 11       fail/retry
```

Typical retry loop:

``` text
LOOP
 |
 read old
 |
 calculate new
 |
 CAS(old, new)
 |
 +-- success -> done
 |
 +-- failure -> retry
```

CAS enables many non-blocking algorithms.

------------------------------------------------------------------------

## Q28. What is the ABA problem?

``` text
Thread A reads: A

Thread B:
A -> B -> A

Thread A CAS:
"Still A, so nothing changed?"
```

The value looks unchanged even though intermediate state changed.

Possible solutions in algorithms that need version awareness include
stamped/versioned references such as `AtomicStampedReference`.

------------------------------------------------------------------------

## Q29. `AtomicLong` vs `LongAdder`?

Under high contention:

``` text
AtomicLong
T1 --+
T2 --+--> same atomic value --> CAS contention
T3 --+

LongAdder
T1 --> cell 1
T2 --> cell 2
T3 --> cell 3
          |
          v
      sum cells
```

`LongAdder` is often useful for high-contention statistics/counters
where obtaining an exact instantaneous value during concurrent updates
is not the main requirement.

Typical example: metrics counters.

------------------------------------------------------------------------

## Q30. Are lock-free algorithms always faster?

No.

``` text
LOW CONTENTION
CAS -> cheap -> often excellent

HIGH CONTENTION
CAS fail
  |
retry
  |
CAS fail
  |
retry
  |
CPU work increases
```

Performance depends on:

-   contention
-   critical-section duration
-   fairness requirements
-   workload
-   CPU architecture
-   retry rate

**Senior rule:** benchmark rather than assuming lock-free means faster.

------------------------------------------------------------------------

# 4. Executors / ThreadPool --- 10 Questions

## Q31. Why use the Executor framework instead of creating Threads directly?

Bad scaling pattern:

``` java
new Thread(() -> process(request)).start();
```

Executor separates:

``` text
TASK SUBMISSION
      |
      v
  Executor
      |
      +--> thread management
      +--> queueing
      +--> lifecycle
      +--> rejection
      +--> metrics/tuning
```

``` java
ExecutorService executor =
        Executors.newFixedThreadPool(10);

executor.submit(this::process);
```

------------------------------------------------------------------------

## Q32. What is `ExecutorService`?

It extends executor functionality with:

-   task submission
-   `Future`
-   lifecycle management
-   shutdown
-   bulk execution

``` java
ExecutorService pool = Executors.newFixedThreadPool(4);

try {
    Future<String> result =
        pool.submit(() -> "done");
} finally {
    pool.shutdown();
}
```

------------------------------------------------------------------------

## Q33. Explain `ThreadPoolExecutor`.

Core constructor:

``` java
new ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAliveTime,
    TimeUnit.SECONDS,
    workQueue,
    threadFactory,
    rejectionHandler
);
```

Mental model:

``` text
Incoming Task
     |
     v
core threads available?
     |
 +---+---+
 |       |
YES      NO
 |       |
run     queue task
          |
      queue full?
          |
      +---+---+
      |       |
     NO      YES
      |       |
    wait   create thread
           up to max
               |
          max reached?
               |
              YES
               |
            REJECT
```

This flow is a critical senior interview concept.

------------------------------------------------------------------------

## Q34. `corePoolSize` vs `maximumPoolSize`?

With a bounded queue:

``` text
tasks
 |
 v
use/create up to core threads
 |
 v
queue
 |
queue FULL
 |
v
create extra threads up to maximumPoolSize
 |
max reached
 |
REJECT
```

### Senior trap

A very large/unbounded queue can mean `maximumPoolSize` has little
practical effect because tasks keep queueing instead of causing the pool
to expand.

------------------------------------------------------------------------

## Q35. What work queues can ThreadPoolExecutor use?

Common choices:

``` text
BlockingQueue
|
+-- ArrayBlockingQueue
|      bounded
|
+-- LinkedBlockingQueue
|      optionally bounded
|
+-- SynchronousQueue
|      direct handoff; no storage
|
+-- PriorityBlockingQueue
       priority ordering
```

### Production preference

Bounded queues are often useful because they make overload explicit and
enable backpressure/rejection rather than unlimited memory growth.

------------------------------------------------------------------------

## Q36. What are rejection policies?

When:

``` text
pool at max threads
        +
queue full
        =
REJECTION
```

Built-in strategies include:

``` text
AbortPolicy
    -> throw RejectedExecutionException

CallerRunsPolicy
    -> caller executes task

DiscardPolicy
    -> discard task

DiscardOldestPolicy
    -> remove oldest queued task and retry
```

### Senior point

Rejection policy is part of your overload/backpressure design, not
merely an implementation detail.

------------------------------------------------------------------------

## Q37. How do you size a thread pool?

For CPU-bound tasks:

``` text
threads ≈ number of available cores
```

For blocking/I/O-heavy work, more concurrency may be useful because
threads spend time waiting.

A common conceptual model:

``` text
N_threads ≈ N_cpu * (1 + wait_time / compute_time)
```

But this is a starting model, not a production constant.

Measure:

-   CPU utilization
-   task latency
-   queue depth
-   rejection rate
-   downstream capacity
-   memory
-   context switching

### Critical distributed-system point

Increasing thread count cannot fix a database/downstream service that
only supports limited concurrency.

------------------------------------------------------------------------

## Q38. What is thread-pool exhaustion?

``` text
HTTP Requests
     |
     v
Thread Pool
[busy][busy][busy][busy]
     |
new requests
     |
     v
   QUEUE
     |
queue grows
     |
latency grows
     |
timeouts
     |
retries
     |
MORE LOAD
     |
     +----------+
                |
                v
        RETRY / OVERLOAD STORM
```

Potential causes:

-   slow database
-   downstream timeout
-   blocking I/O
-   deadlock
-   pool too small
-   tasks waiting on tasks from the same pool
-   no timeout
-   excessive retries

### Senior debugging path

``` text
Latency spike
   |
   v
Pool active threads?
   |
Queue depth?
   |
Thread dump
   |
What are threads waiting on?
   |
DB pool / network / lock / downstream?
```

------------------------------------------------------------------------

## Q39. What is `ScheduledExecutorService`?

``` java
ScheduledExecutorService scheduler =
    Executors.newScheduledThreadPool(2);

scheduler.scheduleAtFixedRate(
    this::refresh,
    0,
    30,
    TimeUnit.SECONDS
);
```

Useful for delayed or periodic tasks.

Know the conceptual difference between:

``` text
scheduleAtFixedRate
target cadence based on start schedule

scheduleWithFixedDelay
wait fixed delay after previous execution completes
```

------------------------------------------------------------------------

## Q40. What is ForkJoinPool and work stealing?

Designed for recursively decomposable tasks.

``` text
                Big Task
                   |
          +--------+--------+
          |                 |
        Task A            Task B
       /     \            /    \
     A1      A2         B1      B2
```

Workers maintain queues.

``` text
Worker 1 queue: [A1 A2 A3]
Worker 2 queue: []

Worker 2
   |
   +--> STEALS work from Worker 1
```

Used by parallel streams and many `CompletableFuture` async operations
when no custom executor is provided.

------------------------------------------------------------------------

# 5. CompletableFuture / Virtual Threads --- 10 Questions

## Q41. What are the limitations of `Future`?

``` java
Future<User> future = executor.submit(this::loadUser);

User user = future.get(); // blocking
```

Classic `Future` is awkward for:

-   chaining
-   combining independent results
-   callbacks
-   exception pipelines
-   non-blocking composition

This motivated `CompletableFuture`.

------------------------------------------------------------------------

## Q42. What is `CompletableFuture`?

``` java
CompletableFuture<User> future =
    CompletableFuture.supplyAsync(() -> loadUser(id));

CompletableFuture<String> result =
    future.thenApply(User::name);
```

Pipeline:

``` text
load user
   |
   v
CompletableFuture<User>
   |
 thenApply
   |
   v
CompletableFuture<String>
```

It supports asynchronous pipelines and explicit completion.

------------------------------------------------------------------------

## Q43. `thenApply()` vs `thenCompose()`?

### thenApply

Use when the transformation returns a normal value.

``` java
CompletableFuture<String> name =
    userFuture.thenApply(User::name);
```

``` text
Future<User>
    |
 thenApply
    |
    v
Future<String>
```

### thenCompose

Use when the transformation itself returns another `CompletableFuture`.

``` java
CompletableFuture<Order> order =
    userFuture.thenCompose(
        user -> loadLatestOrderAsync(user.id())
    );
```

Without compose:

``` text
Future<Future<Order>>
```

With compose:

``` text
Future<Order>
```

Think:

``` text
thenApply   ~= map
thenCompose ~= flatMap
```

------------------------------------------------------------------------

## Q44. What is `thenCombine()`?

Use when two independent asynchronous operations can run in parallel and
their results are needed together.

``` java
CompletableFuture<User> user =
    loadUserAsync(id);

CompletableFuture<List<Order>> orders =
    loadOrdersAsync(id);

CompletableFuture<UserSummary> summary =
    user.thenCombine(
        orders,
        UserSummary::new
    );
```

``` text
           +--> User API ------+
Request ---|                   +--> combine --> response
           +--> Orders API ----+
```

This is common in backend aggregation services.

------------------------------------------------------------------------

## Q45. How do you handle CompletableFuture exceptions?

Important methods:

``` java
future.exceptionally(ex -> fallback());

future.handle((value, ex) -> {
    if (ex != null) {
        return fallback();
    }
    return value;
});

future.whenComplete((value, ex) -> {
    // observation / logging
});
```

Conceptually:

``` text
                 Future
                   |
             +-----+-----+
             |           |
           SUCCESS     FAILURE
             |           |
          transform    fallback/
                       propagate
```

### Senior point

Do not silently convert every failure into a default value. Decide
whether the correct behavior is:

-   recover
-   retry
-   fallback
-   partially respond
-   propagate

------------------------------------------------------------------------

## Q46. Which thread executes CompletableFuture stages?

This matters.

``` java
CompletableFuture.supplyAsync(task)
```

Without an executor, async methods generally use the common
`ForkJoinPool`.

``` java
ExecutorService ioPool =
    Executors.newFixedThreadPool(20);

CompletableFuture.supplyAsync(
    this::callRemoteService,
    ioPool
);
```

Also distinguish:

``` text
thenApply(...)
    -> may execute in thread completing previous stage

thenApplyAsync(...)
    -> schedules async execution
```

### Senior warning

Putting blocking database/network work into the common pool can
interfere with unrelated work that shares that pool.

------------------------------------------------------------------------

## Q47. How do you implement fan-out / fan-in?

``` text
                    Request
                       |
            +----------+----------+
            |          |          |
            v          v          v
          API A      API B      API C
            |          |          |
            +----------+----------+
                       |
                       v
                    combine
                       |
                       v
                    response
```

``` java
CompletableFuture<Void> all =
    CompletableFuture.allOf(a, b, c);

CompletableFuture<Result> result =
    all.thenApply(ignored ->
        combine(a.join(), b.join(), c.join())
    );
```

### Production concerns

Fan-out multiplies dependency traffic and failure opportunities.

Think about:

-   per-call timeout
-   global request deadline
-   partial failure
-   retries
-   bulkheads
-   downstream capacity

------------------------------------------------------------------------

## Q48. What are virtual threads?

Virtual threads are lightweight Java threads designed to make
thread-per-task style practical for large numbers of mostly blocking
tasks.

``` java
try (var executor =
         Executors.newVirtualThreadPerTaskExecutor()) {

    Future<String> result =
        executor.submit(() -> callRemoteService());
}
```

Mental model:

``` text
Traditional model

Request 1 -> Platform Thread 1
Request 2 -> Platform Thread 2
Request 3 -> Platform Thread 3
...

Platform threads are relatively expensive.


Virtual-thread model

Request 1 -> Virtual Thread \
Request 2 -> Virtual Thread  \
Request 3 -> Virtual Thread   +--> JVM schedules over
Request 4 -> Virtual Thread  /     carrier/platform threads
Request N -> Virtual Thread /
```

They simplify scalable blocking-style code.

------------------------------------------------------------------------

## Q49. Platform threads vs virtual threads?

``` text
PLATFORM THREAD
Java Thread
   |
roughly maps to
   |
OS Thread
```

``` text
VIRTUAL THREAD
Java Virtual Thread
   |
JVM scheduling
   |
Carrier / Platform Threads
   |
OS Threads
```

Virtual threads are especially attractive for:

-   request-per-thread servers
-   blocking network calls
-   JDBC
-   service orchestration

They do **not** make CPU-bound work faster.

``` text
CPU-bound task
1000 virtual threads
      |
      v
still limited by available CPU cores
```

### Senior point

Virtual threads change the cost model of blocking threads; they do not
remove downstream resource limits.

------------------------------------------------------------------------

## Q50. What can still go wrong after moving to virtual threads?

This is a critical senior-level question.

``` text
100,000 Virtual Threads
          |
          +--> DB
          |
          +--> Redis
          |
          +--> External API
```

Your database may still have:

``` text
HikariCP connections = 50
```

So:

``` text
Virtual Threads
|||||||||||||||||||||||||||||||||
                |
                v
          Hikari Pool
       [50 connections]
                |
                v
           PostgreSQL
```

Virtual threads do not eliminate:

-   DB connection limits
-   rate limits
-   memory limits
-   external-service capacity
-   lock contention
-   CPU saturation
-   need for timeouts
-   backpressure
-   concurrency control

### Senior design principle

``` text
Cheap concurrency
      !=
Infinite downstream capacity
```

------------------------------------------------------------------------

# Senior Scenario 1 --- `volatile` Counter Bug

``` java
class Metrics {
    private volatile long requests = 0;

    void recordRequest() {
        requests++;
    }
}
```

### Question

Is this thread-safe?

**No.**

``` text
volatile
   |
   +--> visibility ✓
   +--> ordering guarantees ✓
   +--> atomic compound increment ✗
```

Possible solutions include:

``` java
private final AtomicLong requests =
    new AtomicLong();

void recordRequest() {
    requests.incrementAndGet();
}
```

For a highly contended metrics counter:

``` java
private final LongAdder requests =
    new LongAdder();
```

------------------------------------------------------------------------

# Senior Scenario 2 --- Thread Pool + HikariCP

Suppose:

``` text
HTTP worker threads = 200
Application executor = 100
Hikari connections = 20
DB query time = 1 second
```

Traffic spikes.

``` text
Requests
   |
   v
Application Threads
|||||||||||||||||||||
   |
   v
Hikari Pool
[20 DB connections]
   |
   v
Database
```

Increasing the application executor from 100 → 500 may make things
**worse**, not better.

Why?

``` text
More threads
   |
   v
more requests waiting for DB connection
   |
   v
memory / scheduling / latency
   |
   v
timeouts
   |
   v
retries
   |
   v
even more load
```

The bottleneck is downstream capacity.

------------------------------------------------------------------------

# Senior Scenario 3 --- CompletableFuture Latency

``` java
CompletableFuture<User> user =
    CompletableFuture.supplyAsync(
        () -> userClient.get(id)
    );

CompletableFuture<List<Order>> orders =
    CompletableFuture.supplyAsync(
        () -> orderClient.get(id)
    );

return user.thenCombine(orders, Summary::new)
           .join();
```

Questions to ask:

``` text
Which executor?
      |
Are calls blocking?
      |
What are the timeouts?
      |
What if User API = 100ms
but Orders API = 30 seconds?
      |
Can common pool become unhealthy?
      |
How is cancellation handled?
      |
Should partial response be allowed?
```

A senior answer goes beyond syntax and discusses:

-   bounded deadlines
-   executor isolation where appropriate
-   failure semantics
-   retries
-   observability
-   downstream limits

------------------------------------------------------------------------

# Senior Scenario 4 --- Deadlock

``` java
void transfer(Account from, Account to) {
    synchronized (from) {
        synchronized (to) {
            // transfer
        }
    }
}
```

Two calls:

``` text
Thread A: transfer(A, B)
Thread B: transfer(B, A)
```

Potential:

``` text
Thread A                  Thread B

lock A                    lock B
  |                         |
wait B                    wait A
  |                         |
  +--------- DEADLOCK ------+
```

One approach is deterministic lock ordering.

``` text
always lock smaller account ID first
               |
               v
all threads acquire locks
in SAME ORDER
```

------------------------------------------------------------------------

# Senior Scenario 5 --- Virtual Threads and Database Overload

Suppose a service changes from:

``` text
200 platform threads
```

to:

``` text
50,000 virtual threads
```

but the database supports only 100 useful concurrent queries.

``` text
50,000 virtual threads
          |
          v
      DB concurrency
          |
        ~100
```

You still need:

-   connection-pool limits
-   request admission control
-   timeouts
-   rate limiting
-   bulkheads
-   capacity planning

Virtual threads improve the thread scalability model, not the physical
capacity of dependent systems.

------------------------------------------------------------------------

# Mini Coding Drill 1 --- Atomic Counter

Implement a thread-safe counter.

``` java
class RequestCounter {
    private final AtomicLong counter =
        new AtomicLong();

    public long increment() {
        return counter.incrementAndGet();
    }

    public long get() {
        return counter.get();
    }
}
```

Be prepared to explain when `LongAdder` may be preferable.

------------------------------------------------------------------------

# Mini Coding Drill 2 --- Bounded Thread Pool

``` java
ExecutorService executor =
    new ThreadPoolExecutor(
        10,
        20,
        60,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(100),
        new ThreadPoolExecutor.CallerRunsPolicy()
    );
```

Draw it:

``` text
                   Incoming tasks
                         |
                         v
                   Core threads 10
                         |
                    all busy?
                         |
                         v
                    Queue 100
                         |
                    queue full?
                         |
                         v
                Extra threads up to 20
                         |
                     all busy?
                         |
                         v
                  CallerRunsPolicy
                         |
                         v
            submitting thread does work
```

This can naturally slow producers and provide a form of backpressure.

------------------------------------------------------------------------

# Mini Coding Drill 3 --- Parallel Service Calls

``` java
CompletableFuture<User> user =
    CompletableFuture.supplyAsync(
        () -> userService.load(id),
        executor
    );

CompletableFuture<Account> account =
    CompletableFuture.supplyAsync(
        () -> accountService.load(id),
        executor
    );

CompletableFuture<Response> response =
    user.thenCombine(
        account,
        Response::new
    );
```

Sequential:

``` text
User API 200ms
      |
Account API 300ms
      |
Total ~500ms + overhead
```

Parallel:

``` text
       +--> User API ---- 200ms ---+
Start |                            +--> combine
       +--> Account ---- 300ms ----+

Total approximately slowest branch
~300ms + overhead
```

Only when the operations are independent and downstream concurrency is
acceptable.

------------------------------------------------------------------------

# Mini Coding Drill 4 --- Virtual Thread Executor

``` java
try (var executor =
         Executors.newVirtualThreadPerTaskExecutor()) {

    List<Future<String>> futures =
        urls.stream()
            .map(url ->
                executor.submit(
                    () -> fetch(url)
                )
            )
            .toList();

    for (Future<String> future : futures) {
        System.out.println(future.get());
    }
}
```

Interview discussion:

``` text
Virtual threads simplify blocking concurrency
                  |
                  v
But still ask:
  |
  +-- How many outbound calls?
  +-- Downstream rate limit?
  +-- DB connection pool?
  +-- Timeout?
  +-- Cancellation?
  +-- Memory?
  +-- CPU-bound work?
```

------------------------------------------------------------------------

# Production Debugging Flow --- High Latency

``` text
p99 latency increases
        |
        v
Check request rate / error rate
        |
        v
Check active threads
        |
        v
Thread pool queue growing?
        |
   +----+----+
   |         |
  YES       NO
   |         |
   v         v
Thread dump  Check DB / network /
   |         locks / GC / downstream
   v
What are threads doing?
   |
   +-- WAITING for DB connection
   |
   +-- BLOCKED on monitor
   |
   +-- WAITING on Future
   |
   +-- socket read
   |
   +-- CPU loop
```

This is the type of reasoning expected from a senior backend engineer.

------------------------------------------------------------------------

# Production Debugging Flow --- Deadlock

``` text
Service stops progressing
        |
        v
CPU may be low
        |
        v
Requests accumulate
        |
        v
Take thread dump
        |
        v
Look for BLOCKED threads
        |
        v
Find lock ownership
        |
        v
A waits for B
B waits for A
        |
        v
Identify circular dependency
        |
        v
Fix lock ordering / architecture
```

------------------------------------------------------------------------

# Production Debugging Flow --- Pool Exhaustion

``` text
Latency
  ↑
  |
Queue depth
  ↑
  |
Active threads = max
  |
  v
WHY?
  |
  +-- DB slow?
  +-- DB pool exhausted?
  +-- remote service slow?
  +-- lock contention?
  +-- Future.get/join waiting?
  +-- retry storm?
  +-- tasks recursively using same pool?
```

Never respond to pool exhaustion by blindly increasing the pool size.

------------------------------------------------------------------------

# Critical Comparison Sheet

## synchronized vs volatile vs Atomic

``` text
                 synchronized     volatile       Atomic
----------------------------------------------------------
Visibility            YES            YES           YES
Mutual exclusion      YES             NO            NO*
Atomic RMW             YES             NO           YES
Blocking              CAN             NO            NO*
Compound invariant    YES             NO        LIMITED
```

`*` Atomic operations typically use non-blocking primitives such as CAS,
though implementation details and contention behavior matter.

------------------------------------------------------------------------

## synchronized vs ReentrantLock

``` text
synchronized
|
+-- simple
+-- automatic unlock
+-- intrinsic monitor
+-- wait/notify

ReentrantLock
|
+-- explicit lock/unlock
+-- tryLock
+-- timeout
+-- interruptible acquisition
+-- Condition
+-- optional fairness
```

Prefer the simpler mechanism unless explicit-lock features are actually
needed.

------------------------------------------------------------------------

## AtomicLong vs LongAdder

``` text
AtomicLong
   |
single value
   |
CAS contention
   |
exact atomic operations

LongAdder
   |
striped cells
   |
lower contention
   |
excellent for high-update counters
```

------------------------------------------------------------------------

## CompletableFuture vs Virtual Threads

These are not direct replacements.

``` text
CompletableFuture
|
+-- async composition
+-- dependency graph
+-- callbacks
+-- fan-out / fan-in

Virtual Threads
|
+-- simple sequential blocking style
+-- cheap thread-per-task
+-- easier stack traces/control flow
```

A modern backend can use either or both depending on the problem.

------------------------------------------------------------------------

# Senior Rapid-Fire Follow-Ups

Be able to answer these without notes:

1.  Why is `count++` unsafe even if `count` is volatile?
2.  Does synchronized guarantee visibility?
3.  Can synchronized methods deadlock?
4.  Is `HashMap` safe if all reads happen concurrently?
5.  What establishes happens-before?
6.  Why does double-checked locking use volatile?
7.  CAS vs mutex --- what is the trade-off?
8.  Why can CAS perform poorly under contention?
9.  Why might LongAdder beat AtomicLong?
10. Why must `unlock()` be in `finally`?
11. What happens when ThreadPoolExecutor's queue is full?
12. Why can an unbounded executor queue be dangerous?
13. Why might increasing thread count increase latency?
14. How do thread pool size and HikariCP size interact?
15. What is CallerRunsPolicy useful for?
16. What pool does CompletableFuture use by default for async methods?
17. `thenApply` vs `thenCompose`?
18. How would you parallelize independent downstream calls?
19. What happens if one CompletableFuture never completes?
20. Are virtual threads useful for CPU-heavy workloads?
21. Do virtual threads remove the need for connection pools?
22. How do you diagnose a Java deadlock?
23. How do you diagnose thread-pool exhaustion?
24. BLOCKED vs WAITING?
25. Why is immutable state concurrency-friendly?

------------------------------------------------------------------------

# Interview Question Checklist

``` text
THREADS / SYNCHRONIZATION
[ ] 01 Process vs thread
[ ] 02 Thread lifecycle
[ ] 03 Runnable vs Callable
[ ] 04 start() vs run()
[ ] 05 Race condition
[ ] 06 synchronized
[ ] 07 Monitor / intrinsic lock
[ ] 08 wait / notify / notifyAll
[ ] 09 Deadlock / livelock / starvation
[ ] 10 Thread-safety strategies

JMM / VOLATILE
[ ] 11 Java Memory Model
[ ] 12 Visibility / atomicity / ordering
[ ] 13 volatile guarantees
[ ] 14 volatile vs synchronized
[ ] 15 happens-before
[ ] 16 Reordering
[ ] 17 Safe publication
[ ] 18 final-field semantics
[ ] 19 Double-checked locking
[ ] 20 Immutability

LOCKS / ATOMIC / CAS
[ ] 21 Lock API
[ ] 22 ReentrantLock
[ ] 23 tryLock
[ ] 24 ReadWriteLock
[ ] 25 StampedLock
[ ] 26 Atomic classes
[ ] 27 CAS
[ ] 28 ABA problem
[ ] 29 AtomicLong vs LongAdder
[ ] 30 Lock-free trade-offs

EXECUTORS / THREAD POOL
[ ] 31 Executor framework
[ ] 32 ExecutorService
[ ] 33 ThreadPoolExecutor
[ ] 34 corePoolSize / maximumPoolSize
[ ] 35 Work queues
[ ] 36 Rejection policies
[ ] 37 Pool sizing
[ ] 38 Thread-pool exhaustion
[ ] 39 ScheduledExecutorService
[ ] 40 ForkJoinPool / work stealing

COMPLETABLEFUTURE / VIRTUAL THREADS
[ ] 41 Future limitations
[ ] 42 CompletableFuture
[ ] 43 thenApply / thenCompose
[ ] 44 thenCombine
[ ] 45 Exception handling
[ ] 46 Executor selection
[ ] 47 Fan-out / fan-in
[ ] 48 Virtual threads
[ ] 49 Platform vs virtual threads
[ ] 50 Virtual-thread production trade-offs
```

------------------------------------------------------------------------

# Chapter 02 Visual Summary

``` text
                    JAVA CONCURRENCY
                           |
        +------------------+------------------+
        |                                     |
   CORRECTNESS                             EXECUTION
        |                                     |
        v                                     v
 Shared Mutable State                   Threads / Tasks
        |                                     |
        +--> Visibility                       +--> Executor
        |      +--> volatile                  |
        |      +--> JMM                       +--> ThreadPool
        |      +--> happens-before            |      |
        |                                     |      +--> queue
        +--> Atomicity                        |      +--> max threads
        |      +--> synchronized              |      +--> rejection
        |      +--> Lock                      |
        |      +--> Atomic/CAS                +--> CompletableFuture
        |                                            |
        +--> Ordering                                +--> composition
        |                                            +--> fan-out
        +--> Safe publication                       +--> failure handling
        |
        +--> Immutability                     +--> Virtual Threads
                                                    |
                                                    +--> cheap blocking
                                                    +--> thread-per-task
                                                    +--> NOT infinite capacity
```

------------------------------------------------------------------------

# Distributed Backend Connection

Concurrency does not live in isolation.

``` text
Client Requests
      |
      v
Spring Boot
      |
      v
Request Threads / Virtual Threads
      |
      +------------------+
      |                  |
      v                  v
   Executor          CompletableFuture
      |                  |
      +--------+---------+
               |
               v
           HikariCP
               |
               v
          PostgreSQL

               +
               |
               +--> Redis
               |
               +--> Kafka
               |
               +--> Remote APIs
```

A concurrency decision can therefore affect:

``` text
threads
   ↓
connection pool
   ↓
database
   ↓
latency
   ↓
timeouts
   ↓
retries
   ↓
system load
```

That cross-layer reasoning is what turns a Java concurrency answer into
a **senior distributed-backend answer**.

------------------------------------------------------------------------

# Chapter 02 Exit Criteria

You are ready for **CH 03 --- JVM & Performance** when you can:

1.  Draw a race condition and explain why `count++` is not atomic.
2.  Explain `synchronized` using object monitors.
3.  Explain visibility, atomicity and ordering separately.
4.  Explain happens-before without saying only "thread A happens first."
5.  Explain exactly what `volatile` does and does not guarantee.
6.  Explain safe publication and double-checked locking.
7.  Draw CAS and explain contention.
8.  Compare AtomicLong and LongAdder.
9.  Draw ThreadPoolExecutor's task-admission flow.
10. Explain why queue choice changes pool behavior.
11. Diagnose thread-pool exhaustion from metrics + thread dumps.
12. Explain `thenApply`, `thenCompose`, and `thenCombine`.
13. Design a fan-out/fan-in backend call with failure considerations.
14. Explain platform vs virtual threads.
15. Explain why virtual threads do not remove downstream concurrency
    limits.
16. Solve the deadlock, HikariCP, and latency scenarios in this chapter
    aloud.

``` text
CH 02 CONCURRENCY
       |
       v
Threads / Synchronization
       |
       v
JMM / volatile / happens-before ★★★
       |
       v
Locks / Atomic / CAS
       |
       v
Executors / ThreadPool ★★★
       |
       v
CompletableFuture
       |
       v
Virtual Threads ★★★
       |
       v
READY FOR CH 03
JVM + GC + JIT + Performance
```

------------------------------------------------------------------------

## Next Chapter

``` text
CH 03 — JVM & PERFORMANCE (~40 Q)
|
+-- Memory / Class Loading       10
+-- Garbage Collection           10
+-- JIT                           5
+-- OOM / Dumps / Performance    15
```
