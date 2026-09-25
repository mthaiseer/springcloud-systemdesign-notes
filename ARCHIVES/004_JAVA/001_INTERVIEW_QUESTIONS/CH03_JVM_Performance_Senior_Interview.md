# CH 03 --- JVM & Performance

## Senior Java Distributed Backend Interview Series

**Target:** \~40 interview questions\
**Goal:** Understand what happens below Java code: class loading,
runtime memory, object allocation, garbage collection, JIT compilation,
OOMs, dumps, profiling, and production performance diagnosis.

------------------------------------------------------------------------

# Chapter Map

``` text
CH 03 — JVM & PERFORMANCE (~40 Q)
|
+-- 1. Memory / Class Loading (10)
|   +-- JDK / JVM execution pipeline
|   +-- JVM architecture
|   +-- Class loading lifecycle
|   +-- ClassLoader hierarchy
|   +-- Parent delegation
|   +-- Heap / Stack / Metaspace
|   +-- Object allocation
|   +-- TLAB
|   +-- Escape analysis
|   +-- Strong / Soft / Weak / Phantom references
|
+-- 2. Garbage Collection (10)
|   +-- Reachability / GC roots
|   +-- Generational hypothesis
|   +-- Young / Old generations
|   +-- Minor / Major / Full GC
|   +-- Stop-the-world
|   +-- Mark / Sweep / Compact / Copy
|   +-- G1 GC
|   +-- ZGC
|   +-- GC logs / metrics
|   +-- GC tuning strategy
|
+-- 3. JIT (5)
|   +-- Interpreter vs JIT
|   +-- Hot methods
|   +-- C1 / C2 / tiered compilation
|   +-- Inlining / devirtualization
|   +-- Deoptimization / warm-up
|
+-- 4. OOM / Dumps / Performance (15)
    +-- OutOfMemoryError variants
    +-- Java heap space
    +-- Metaspace
    +-- Direct buffer memory
    +-- Unable to create native thread
    +-- StackOverflowError
    +-- Memory leak
    +-- Heap dump
    +-- Thread dump
    +-- High CPU debugging
    +-- High memory debugging
    +-- GC pause debugging
    +-- Latency debugging
    +-- JVM/container memory
    +-- Performance methodology
```

------------------------------------------------------------------------

# JVM Big Picture

``` text
.java source
    |
    | javac
    v
.class bytecode
    |
    v
+--------------------------------------------------+
|                       JVM                        |
|                                                  |
|  Class Loader                                    |
|      |                                           |
|      v                                           |
|  Runtime Data Areas                              |
|  +----------+  +---------+  +----------------+   |
|  |   Heap   |  | Stacks  |  | Metaspace      |  |
|  +----------+  +---------+  +----------------+   |
|      |                                           |
|      +--------> Execution Engine                 |
|                   |                              |
|             +-----+------+                       |
|             |            |                       |
|        Interpreter      JIT                      |
|             |            |                       |
|             +-----+------+                       |
|                   |                              |
|                   v                              |
|              Native Code                         |
|                                                  |
|  Garbage Collector <-------- Heap                |
+--------------------------------------------------+
                    |
                    v
              OS / CPU / Memory
```

Senior interviews usually test the connection:

``` text
Java code
   -> allocation
   -> heap pressure
   -> GC
   -> CPU / pause
   -> request latency
   -> p99 / timeout
   -> distributed-system failure
```

------------------------------------------------------------------------

# 1. Memory / Class Loading --- 10 Questions

## Q1. How does Java source code become executable?

``` text
Hello.java
    |
    | javac
    v
Hello.class
(bytecode)
    |
    v
Class Loader
    |
    v
Bytecode Verification
    |
    v
Interpreter / JIT
    |
    v
Machine Code
    |
    v
CPU
```

Java's bytecode provides portability while the JVM performs
platform-specific execution.

### Senior follow-up

Why is Java called both compiled and interpreted?

Because source is compiled to bytecode, while the JVM can interpret
bytecode and dynamically compile hot code to native machine code.

------------------------------------------------------------------------

## Q2. What are the major JVM runtime data areas?

``` text
JVM PROCESS
|
+-- HEAP --------------------------- shared
|   +-- Java objects
|   +-- arrays
|
+-- METASPACE ---------------------- shared
|   +-- class metadata
|
+-- THREAD 1
|   +-- Java Stack
|       +-- Frame
|       +-- Frame
|
+-- THREAD 2
|   +-- Java Stack
|
+-- PC register -------------------- per thread
|
+-- Native method stack ------------ per thread
```

### Key distinction

``` text
Heap        -> shared objects
Stack       -> per-thread call frames
Metaspace   -> class metadata
```

------------------------------------------------------------------------

## Q3. Explain the class loading lifecycle.

``` text
LOAD
 |
 v
LINK
 |
 +-- Verify
 +-- Prepare
 +-- Resolve
 |
 v
INITIALIZE
```

### Loading

Find bytecode and create the runtime `Class` representation.

### Verification

Validate bytecode safety/structure.

### Preparation

Allocate class-level storage and assign default values.

### Resolution

Convert symbolic references when required.

### Initialization

Execute static initializers and assign explicit static values.

``` java
class Demo {
    static int x = 10;
    static {
        System.out.println("initialized");
    }
}
```

------------------------------------------------------------------------

## Q4. Explain the ClassLoader hierarchy.

``` text
Bootstrap ClassLoader
        |
        v
Platform ClassLoader
        |
        v
Application ClassLoader
        |
        v
Custom ClassLoader
```

Conceptually:

``` text
Bootstrap   -> core Java classes
Platform    -> platform modules/classes
Application -> application classpath/module path
Custom      -> plugins, containers, special loading
```

Class loaders are also important for class identity:

``` text
Class identity = class name + defining ClassLoader
```

The same bytecode loaded by different defining class loaders can
represent different runtime types.

------------------------------------------------------------------------

## Q5. What is parent delegation and why is it useful?

Typical conceptual lookup:

``` text
Application loader receives request
           |
           v
      ask parent
           |
           v
    Platform loader
           |
           v
      ask parent
           |
           v
      Bootstrap
           |
      found? yes -> use it
           |
          no
           v
 parent attempts loading
           |
          ...
```

Benefits include:

-   avoiding duplicate loading of core classes
-   consistency of foundational Java types
-   reducing accidental replacement of platform classes

### Senior note

Some frameworks/containers use specialized loading strategies, so do not
assume every environment follows one simplistic delegation pattern for
every class.

------------------------------------------------------------------------

## Q6. Heap vs Stack vs Metaspace?

``` text
void process() {
    User user = new User("A");
}
```

Conceptually:

``` text
Thread Stack                 Heap
+----------------+          +----------------+
| process frame  |          | User object    |
| user reference |--------->| name -> String |
+----------------+          +----------------+

Metaspace
+-------------------------+
| User class metadata     |
| methods / runtime info  |
+-------------------------+
```

### Important

Do not reduce this to "primitives are stack, objects are heap." JVM
optimization can make physical placement more nuanced. The useful
interview model is that stack frames hold method execution
state/references while objects are generally heap-managed.

------------------------------------------------------------------------

## Q7. How is a Java object allocated?

Simplified fast path:

``` text
new User()
    |
    v
Class loaded?
    |
    v
Find allocation space
    |
    v
Reserve memory
    |
    v
Zero initialize
    |
    v
Set object header
    |
    v
Run constructor
    |
    v
Return reference
```

For many allocations, the JVM can use pointer-bump allocation in
thread-local allocation regions, making allocation surprisingly cheap.

### Senior point

Object allocation can be cheap; **retaining too many objects** and
forcing expensive GC is often the bigger issue.

------------------------------------------------------------------------

## Q8. What is TLAB?

TLAB = Thread-Local Allocation Buffer.

``` text
Heap Young Generation
|
+-- TLAB Thread A
|   +-- obj
|   +-- obj
|   +-- obj
|
+-- TLAB Thread B
|   +-- obj
|   +-- obj
|
+-- remaining shared allocation space
```

Each thread can often allocate from its own region without contending on
a single global allocation pointer.

``` text
Thread A -> its TLAB -> fast allocation
Thread B -> its TLAB -> fast allocation
```

This is one reason short-lived object allocation can perform well in
modern JVMs.

------------------------------------------------------------------------

## Q9. What is escape analysis?

The JIT analyzes whether an object escapes a method/thread context.

``` java
int calculate() {
    Point p = new Point(10, 20);
    return p.x() + p.y();
}
```

Conceptual possibilities when optimization proves safety:

``` text
Object does not escape
       |
       +--> scalar replacement
       +--> lock elimination
       +--> allocation may be optimized away
```

### Senior warning

Do not claim "escape analysis always puts objects on the stack." The
optimization is implementation-dependent and may
eliminate/scalar-replace allocations instead.

------------------------------------------------------------------------

## Q10. Strong vs Soft vs Weak vs Phantom references?

``` text
Strong
  |
  +--> ordinary reference
  +--> object retained while strongly reachable

Soft
  |
  +--> GC-sensitive reference historically used for memory-sensitive caches

Weak
  |
  +--> does not keep object strongly reachable
  +--> useful in specialized mappings/listener patterns

Phantom
  |
  +--> post-mortem cleanup/notification patterns
  +--> used with ReferenceQueue
```

Example:

``` java
WeakReference<User> ref =
    new WeakReference<>(new User());

User user = ref.get();
```

### Senior recommendation

Do not build ordinary application caches around assumptions about
soft-reference behavior. Explicit bounded caches with clear eviction
policies are usually easier to reason about.

------------------------------------------------------------------------

# 2. Garbage Collection --- 10 Questions

## Q11. How does GC determine whether an object is alive?

Modern JVM collectors fundamentally use **reachability**, not simple
reference counting.

``` text
GC ROOTS
|
+-- live thread stacks
+-- static references
+-- JNI references
+-- JVM internal roots
        |
        v
      Object A
        |
        v
      Object B

Object X ---- Object Y
(no path from GC roots)
       |
       v
   reclaimable
```

### Why not basic reference counting?

Cycles:

``` text
A ---> B
^      |
|      v
+------+

No external root
```

A and B reference each other but are still unreachable and collectible.

------------------------------------------------------------------------

## Q12. What is the generational hypothesis?

Most objects die young.

``` text
Allocated objects
||||||||||||||||||||||||
        |
        v
Most die quickly  X X X X X X X
        |
        +--> survivors --> live longer
```

This motivates generational collection strategies:

``` text
Young generation -> collect frequently
Old generation   -> objects that survive longer
```

This model has evolved across collectors, but the core insight remains
useful.

------------------------------------------------------------------------

## Q13. Explain Eden, Survivor, and Old generation conceptually.

Classic generational model:

``` text
YOUNG GENERATION
+----------------------------------+
| Eden       | Survivor | Survivor |
+----------------------------------+
       |
       | surviving objects age
       v
+----------------------------------+
|          OLD GENERATION          |
+----------------------------------+
```

Typical lifecycle:

``` text
allocate
   |
   v
Eden
   |
Young GC
   |
Survivor
   |
Survive more collections
   |
Promotion
   v
Old
```

Exact layout/behavior depends on the collector.

------------------------------------------------------------------------

## Q14. Minor GC vs Major GC vs Full GC?

Terminology is collector-dependent, so answer carefully.

Conceptually:

``` text
Young / Minor GC
   -> focuses on young objects

Major / Old collection
   -> old-generation work

Full GC
   -> broad whole-heap / global collection behavior
```

### Senior answer

Do not rely only on these labels when debugging. Read the
collector-specific GC logs and identify:

-   cause
-   regions/generations involved
-   pause duration
-   reclaimed memory
-   allocation/promotion behavior

------------------------------------------------------------------------

## Q15. What is Stop-The-World (STW)?

A stop-the-world phase pauses application threads at safe points so the
JVM can perform certain GC/runtime operations safely.

``` text
Time -------------------------------------------------->

Application: ========| PAUSED |========================
GC:                  |  GC   |
```

Modern collectors try to perform more work concurrently, but some pause
phases remain.

### Distributed impact

``` text
GC pause 500 ms
    |
    v
request latency +500 ms
    |
    v
p99 spike
    |
    v
client timeout
    |
    v
retry
    |
    v
extra load
```

------------------------------------------------------------------------

## Q16. Explain mark, sweep, compact and copying.

### Mark

``` text
GC roots -> traverse reachable objects -> mark live
```

### Sweep

``` text
[Live][Dead][Live][Dead][Dead]
          |
          v
[Live][free][Live][free][free]
```

Can leave fragmentation.

### Compact

``` text
Before:
[L][free][L][free][L][free]

After:
[L][L][L][      free       ]
```

### Copy

``` text
From-space                 To-space
[L][D][L][D][L]  ----->   [L][L][L]
```

Collectors combine/adapt these ideas in different ways.

------------------------------------------------------------------------

## Q17. How does G1 GC work conceptually?

G1 divides the heap into regions rather than requiring one fixed
contiguous young/old layout.

``` text
HEAP REGIONS
+----+----+----+----+----+----+----+
| E  | O  | S  | E  | O  | H  | E  |
+----+----+----+----+----+----+----+

E = Eden
S = Survivor
O = Old
H = Humongous-related region usage
```

G1 aims for predictable pause-time behavior by choosing collections of
regions.

Conceptually:

``` text
Concurrent marking
       |
       v
identify reclaimable regions
       |
       v
collect selected region sets
       |
       v
evacuate live objects
```

### Senior areas to know

-   region-based heap
-   evacuation
-   remembered sets/card tracking concepts
-   concurrent marking
-   mixed collections
-   humongous objects
-   pause target is a goal, not a guarantee

------------------------------------------------------------------------

## Q18. What is ZGC and when is it attractive?

ZGC is designed for very low pause times, including on large heaps, by
performing much of its work concurrently with application threads.

``` text
Traditional concern
Large heap -> potentially disruptive pauses

ZGC goal
Large heap
   |
mostly concurrent GC work
   |
very short pause phases
```

Useful when latency consistency matters strongly.

### Trade-off thinking

Do not choose a collector from fashion. Consider:

``` text
throughput
latency target
heap size
CPU headroom
allocation rate
JDK version
operational evidence
```

------------------------------------------------------------------------

## Q19. What should you inspect in GC logs/metrics?

``` text
GC OBSERVABILITY
|
+-- allocation rate
+-- heap before / after GC
+-- pause duration
+-- pause frequency
+-- promotion rate
+-- old-gen occupancy
+-- concurrent-cycle behavior
+-- Full GC occurrence
+-- GC CPU consumption
+-- humongous allocations (collector-specific)
```

Useful service-level correlation:

``` text
GC pause timeline
        +
request latency timeline
        +
CPU timeline
        +
heap timeline
        =
root-cause evidence
```

Never tune GC using one isolated metric.

------------------------------------------------------------------------

## Q20. How should you approach GC tuning?

Bad approach:

``` text
"Latency is high -> increase heap"
```

Better:

``` text
1. Define symptom
      |
2. Measure allocation / heap / pauses
      |
3. Identify cause
      |
4. Fix application retention/allocation first if needed
      |
5. Verify heap/container sizing
      |
6. Tune collector only with evidence
      |
7. Load test
      |
8. Compare p50/p95/p99 + throughput + CPU
```

### Senior principle

GC tuning is not a substitute for fixing a memory leak or pathological
allocation pattern.

------------------------------------------------------------------------

# 3. JIT Compilation --- 5 Questions

## Q21. Interpreter vs JIT?

``` text
Bytecode
   |
   +--> Interpreter
   |      |
   |      +--> execute immediately
   |
   +--> Hot code detected
          |
          v
         JIT
          |
          v
     optimized native code
```

Interpreter advantages:

-   quick startup
-   no up-front compilation of everything

JIT advantages:

-   runtime profiling
-   optimized hot paths
-   speculative optimizations

------------------------------------------------------------------------

## Q22. What is a hot method / hot spot?

The JVM profiles execution.

``` text
method A called 5 times
method B called 10,000,000 times
                     |
                     v
                    HOT
                     |
                     v
               optimize more
```

The JVM spends optimization effort where it expects the most benefit.

This adaptive runtime optimization is central to JVM performance.

------------------------------------------------------------------------

## Q23. What are C1, C2 and tiered compilation conceptually?

A simplified HotSpot model:

``` text
Interpreter
    |
    v
C1 compilation
(fast / profiling-oriented)
    |
    v
more profiling
    |
    v
C2 compilation
(aggressive optimization)
```

Tiered compilation balances startup speed with peak performance.

Do not memorize implementation thresholds; explain the optimization
progression.

------------------------------------------------------------------------

## Q24. What are inlining and devirtualization?

### Inlining

Instead of:

``` text
caller
  |
  +--> method call
          |
          v
        body
```

JIT may conceptually transform to:

``` text
caller + method body together
```

This can expose more optimization opportunities.

### Devirtualization

For polymorphic calls, runtime profiling may let the JIT infer likely
concrete targets and optimize accordingly, while retaining the ability
to deoptimize if assumptions become invalid.

------------------------------------------------------------------------

## Q25. What are warm-up and deoptimization?

### Warm-up

``` text
Application starts
      |
interpreted / lightly compiled
      |
profiling accumulates
      |
hot code optimized
      |
steady-state performance
```

This is why microbenchmarks that measure only the first few executions
can be misleading.

### Deoptimization

The JIT may optimize based on runtime assumptions.

``` text
assumption valid
      |
optimized code
      |
assumption becomes invalid
      |
deoptimize
      |
return to safer execution / recompile
```

### Senior benchmark point

Use JMH for Java microbenchmarks rather than hand-written
`System.nanoTime()` loops that ignore warm-up, dead-code elimination,
constant folding, etc.

------------------------------------------------------------------------

# 4. OOM / Dumps / Performance --- 15 Questions

## Q26. What is `OutOfMemoryError`?

It means the JVM cannot satisfy a memory/resource allocation request
under the relevant memory area/condition.

Do not treat OOM as one single problem.

``` text
OutOfMemoryError
|
+-- Java heap space
+-- Metaspace
+-- Direct buffer memory / native allocation issues
+-- Unable to create native thread
+-- GC overhead-related situations
+-- other JVM/native resource failures
```

Diagnosis depends on the exact error message and runtime evidence.

------------------------------------------------------------------------

## Q27. What causes `Java heap space` OOM?

Possible causes:

``` text
Heap OOM
|
+-- genuine memory leak
+-- heap too small for workload
+-- huge request / object graph
+-- unbounded cache
+-- unbounded queue
+-- reading entire large file into memory
+-- traffic/concurrency explosion
+-- retention by static/thread-local/listener references
```

Example dangerous cache:

``` java
private static final Map<String, Object> CACHE =
    new ConcurrentHashMap<>();

void put(String key, Object value) {
    CACHE.put(key, value); // no bound / no eviction
}
```

------------------------------------------------------------------------

## Q28. What causes Metaspace OOM?

Metaspace stores class metadata in native memory.

``` text
Metaspace growth
|
+-- too many loaded classes
+-- dynamic class generation
+-- classloader leak
+-- repeated redeployment/plugin loading
```

ClassLoader leak pattern:

``` text
Old ClassLoader
     |
     +--> classes
     +--> static fields
     +--> objects

Something still references Old ClassLoader
     |
     v
cannot unload its classes
```

This is especially relevant in containers, plugin systems,
hot-reload/redeployment environments, and heavy proxy/code-generation
systems.

------------------------------------------------------------------------

## Q29. What is direct/native memory and how can it fail?

Not all JVM process memory is Java heap.

``` text
JVM PROCESS MEMORY
|
+-- Java Heap
+-- Metaspace
+-- Thread Stacks
+-- Code Cache
+-- Direct Buffers
+-- GC/native structures
+-- JVM/native libraries
```

Example:

``` java
ByteBuffer buffer =
    ByteBuffer.allocateDirect(1024 * 1024);
```

Direct buffers are outside the Java heap, though Java objects/reference
machinery still track them.

### Senior container trap

A pod can be OOMKilled even when heap usage is below `-Xmx` because the
**whole process** exceeds the container memory limit.

------------------------------------------------------------------------

## Q30. What causes `unable to create new native thread`?

Each platform thread consumes native resources, including stack memory
and OS scheduling structures.

``` text
new Thread
   |
   v
Need native thread resource
   |
   +-- process/user thread limit?
   +-- native memory available?
   +-- container limit?
   +-- too many existing threads?
```

Common application cause:

``` java
while (true) {
    new Thread(task).start();
}
```

or uncontrolled thread pools.

Virtual threads greatly change thread scalability for suitable
workloads, but native/container resources still matter.

------------------------------------------------------------------------

## Q31. `StackOverflowError` vs heap OOM?

StackOverflow usually means a thread's stack exhausted, often because of
excessive recursion.

``` java
void recurse() {
    recurse();
}
```

``` text
Thread Stack
+-------------+
| frame       |
+-------------+
| frame       |
+-------------+
| frame       |
+-------------+
| ...         |
+-------------+
| NO SPACE    | -> StackOverflowError
```

Heap OOM concerns object/native allocation rather than a single thread's
call-stack depth.

------------------------------------------------------------------------

## Q32. What is a Java memory leak if GC exists?

GC only collects **unreachable** objects.

``` text
Object no longer useful
        |
        BUT
        |
still reachable from GC root
        |
        v
GC must keep it
        |
        v
MEMORY LEAK
```

Common patterns:

-   unbounded caches/maps
-   listeners never removed
-   ThreadLocal values retained by long-lived threads
-   static collections
-   queues growing without consumption
-   classloader leaks
-   accidental object graph retention

### Key definition

``` text
Leak != "GC failed"
Leak = "application retains objects it no longer needs"
```

------------------------------------------------------------------------

## Q33. What is a heap dump and how do you use it?

A heap dump captures heap objects and reference relationships at a point
in time.

Use it to investigate:

``` text
Heap Dump
|
+-- which classes consume memory?
+-- how many instances?
+-- retained size?
+-- dominator tree?
+-- path to GC root?
+-- suspicious collections/caches?
```

Typical analysis flow:

``` text
OOM / high heap
    |
    v
Capture heap dump
    |
    v
Largest retained objects
    |
    v
Dominator tree
    |
    v
Path to GC roots
    |
    v
Why is this object retained?
```

Common tools include Eclipse MAT, VisualVM and commercial profilers.

------------------------------------------------------------------------

## Q34. What is a thread dump and how do you use it?

A thread dump shows thread stacks/states.

``` text
Thread Dump
|
+-- RUNNABLE
+-- BLOCKED
+-- WAITING
+-- TIMED_WAITING
+-- lock ownership
+-- stack traces
```

Useful for:

``` text
high CPU
   -> find busy threads / stack patterns

deadlock
   -> circular lock ownership

pool exhaustion
   -> what are workers waiting on?

slow service
   -> DB/socket/lock/future waits?
```

### Important

One dump is a snapshot. Multiple dumps separated by a few seconds can
reveal whether the same threads remain stuck in the same code.

------------------------------------------------------------------------

## Q35. How do you debug 100% CPU in a Java service?

``` text
CPU 100%
   |
   v
Is it JVM process?
   |
   v
Which thread(s)?
   |
   v
Capture thread dumps / profiler
   |
   v
Map hot thread -> Java stack
   |
   +-- infinite loop?
   +-- expensive serialization?
   +-- regex?
   +-- lock spinning/contention?
   +-- GC CPU?
   +-- excessive retries?
   +-- hot business algorithm?
```

### Production correlation

``` text
CPU ↑
 + GC CPU ↑ ?
 + allocation rate ↑ ?
 + request rate ↑ ?
 + latency ↑ ?
```

Use evidence before changing JVM flags.

------------------------------------------------------------------------

## Q36. How do you debug continuously increasing memory?

``` text
Memory rising
    |
    v
Heap or whole process RSS?
    |
+---+-------------------------+
|                             |
Heap                         Native/process
|                             |
GC after-used trend?          direct buffers?
|                             thread count?
heap dump                     metaspace?
|                             native libs?
retained objects              container metrics
```

For heap:

``` text
After-GC heap
100 MB
150 MB
220 MB
350 MB
500 MB
   |
   v
suspicious retention trend
```

If memory drops significantly after GC and tracks load, it may be normal
allocation pressure rather than a leak.

------------------------------------------------------------------------

## Q37. How do you debug long GC pauses?

``` text
p99 latency spike
      |
      v
GC pause correlated?
      |
     YES
      |
      v
Which collector/event?
      |
      +-- heap nearly full?
      +-- promotion pressure?
      +-- humongous allocation?
      +-- Full GC?
      +-- allocation burst?
      +-- CPU starvation?
      +-- container memory pressure?
```

Then determine whether the solution is:

-   application allocation reduction
-   retention fix
-   heap sizing
-   collector configuration
-   workload/concurrency control
-   collector change

------------------------------------------------------------------------

## Q38. How do you debug application latency from a JVM perspective?

``` text
Slow Request
    |
    +--> CPU saturation?
    |
    +--> GC pause?
    |
    +--> lock contention?
    |
    +--> thread pool queue?
    |
    +--> Hikari wait?
    |
    +--> downstream socket wait?
    |
    +--> JIT/warm-up/startup?
    |
    +--> allocation/serialization overhead?
```

Correlate JVM telemetry with distributed tracing:

``` text
Trace span: DB = 20 ms
Trace span: remote API = 50 ms
Total request = 2 sec
               |
               v
Where is remaining ~1.93 sec?
               |
               +--> queue?
               +--> lock?
               +--> GC?
               +--> executor scheduling?
```

This is a strong senior-level debugging pattern.

------------------------------------------------------------------------

## Q39. How should JVM memory be sized inside Kubernetes/container limits?

Critical mental model:

``` text
Container memory limit = 2 GB
|
+-- Java heap       e.g. 1.2 GB
+-- Metaspace
+-- Thread stacks
+-- Direct buffers
+-- Code cache
+-- GC/native memory
+-- JVM/native libs
+-- safety headroom
```

Dangerous assumption:

``` text
Container = 2 GB
-Xmx = 2 GB   <-- leaves no headroom
```

Possible result:

``` text
Heap < Xmx
but
Process RSS > container limit
        |
        v
Kubernetes OOMKilled
```

### Senior answer

Size the **whole JVM process**, not only heap. Observe actual
native/heap usage under realistic load.

------------------------------------------------------------------------

## Q40. What is a good JVM performance optimization methodology?

``` text
                 PERFORMANCE PROBLEM
                         |
                         v
                  Define symptom
             latency / throughput / CPU
                         |
                         v
                    Establish SLO
                         |
                         v
                    Measure baseline
                         |
                         v
              Profile / collect evidence
                         |
          +--------------+--------------+
          |              |              |
         CPU            Memory         Waiting
          |              |              |
      profiler       heap/GC       threads/traces
          |              |              |
          +--------------+--------------+
                         |
                         v
                  Find bottleneck
                         |
                         v
                 Change ONE thing
                         |
                         v
                    Load test
                         |
                         v
               Compare before/after
```

### Golden rules

1.  Measure before tuning.
2.  Optimize the bottleneck, not what looks interesting.
3.  Prefer algorithm/architecture fixes before JVM flag folklore.
4.  Test under representative load.
5.  Compare throughput **and** tail latency.
6.  Watch CPU, allocation, GC, threads, DB and downstreams together.
7.  Keep changes reversible.

------------------------------------------------------------------------

# Production Scenario 1 --- Memory Leak in a Spring Boot Service

Symptoms:

``` text
Pod starts:       400 MB
After 2 hours:    900 MB
After 6 hours:   1.5 GB
After 10 hours:  OOMKilled
```

Investigation:

``` text
Memory increasing
      |
      v
Check heap after GC
      |
      +-- increasing?
              |
             YES
              |
              v
        capture heap dump
              |
              v
        dominator tree
              |
              v
       giant Map found
              |
              v
      path to GC root
              |
              v
static cache retains entries
```

Fix:

``` text
Unbounded Map
     |
     v
Bounded cache
 + max size
 + TTL
 + eviction
 + metrics
```

Senior follow-up: why did GC not solve it? Because the objects remained
strongly reachable.

------------------------------------------------------------------------

# Production Scenario 2 --- GC Causing p99 Spikes

``` text
Normal p99 = 180 ms
Every few minutes = 2.5 sec
```

Dashboard:

``` text
Time ------------------------------------------------>
Latency  ____/^^^^^\________/^^^^^\________
GC Pause ____/^^^^^\________/^^^^^\________
Heap     /^^^^\_____/^^^^\_____/^^^^\______
```

Reasoning:

``` text
Latency and GC correlate
        |
        v
Check GC event type
        |
        v
Why is heap pressure high?
        |
   +----+----+
   |         |
allocation  retention
burst       problem
```

Do not immediately change collectors. First identify why the collector
is under pressure.

------------------------------------------------------------------------

# Production Scenario 3 --- Kubernetes OOMKilled but No Java Heap OOM

Configuration:

``` text
Pod memory limit = 1 GiB
-Xmx             = 800 MiB
```

Runtime:

``` text
Heap          700 MB
Metaspace      80 MB
Direct         90 MB
Thread stacks  70 MB
Other native   80 MB
--------------------
Total        1020 MB+
```

Result:

``` text
Linux cgroup limit exceeded
          |
          v
      OOMKilled
```

There may be no Java `OutOfMemoryError` because the OS/container killed
the process first.

------------------------------------------------------------------------

# Production Scenario 4 --- CPU 100% After Deployment

``` text
New deployment
     |
CPU 30% -> 100%
     |
p99 rises
```

Investigation:

``` text
CPU profile / thread samples
        |
        v
JSON serialization dominates
        |
        v
new response includes huge nested graph
        |
        v
more allocation + more CPU + more GC
```

This demonstrates why JVM performance debugging must connect:

``` text
CODE
 -> CPU
 -> allocation
 -> GC
 -> latency
```

------------------------------------------------------------------------

# Production Scenario 5 --- Thread Count Keeps Growing

``` text
Thread count
100 -> 500 -> 1500 -> 4000
```

Potential pattern:

``` java
ExecutorService executor =
    Executors.newCachedThreadPool();
```

Combined with slow blocking tasks:

``` text
slow downstream
      |
threads stay blocked longer
      |
new tasks arrive
      |
new threads created
      |
native memory increases
      |
context switching increases
      |
possible native-thread OOM
```

Fix is not automatically "use more memory." Control concurrency and
downstream behavior.

------------------------------------------------------------------------

# Mini Coding Drill 1 --- Memory Leak Pattern

``` java
class UserRegistry {
    private static final Map<String, User> USERS =
        new ConcurrentHashMap<>();

    void register(User user) {
        USERS.put(user.id(), user);
    }
}
```

Question: when can this leak?

``` text
Entries added forever
      +
No removal / TTL / bound
      =
retained object graph grows forever
```

------------------------------------------------------------------------

# Mini Coding Drill 2 --- ThreadLocal Leak Risk

``` java
private static final ThreadLocal<byte[]> BUFFER =
    new ThreadLocal<>();

void process() {
    BUFFER.set(new byte[10 * 1024 * 1024]);
    // work
}
```

Safer lifecycle:

``` java
void process() {
    try {
        BUFFER.set(new byte[10 * 1024 * 1024]);
        // work
    } finally {
        BUFFER.remove();
    }
}
```

Why it matters with pools:

``` text
ThreadPool worker lives for hours
          |
ThreadLocal value remains associated
          |
large object stays reachable
```

------------------------------------------------------------------------

# Mini Coding Drill 3 --- Avoid Accidental Giant Allocation

Risky:

``` java
byte[] data = inputStream.readAllBytes();
```

For a very large/untrusted input:

``` text
500 MB file
    |
read entire content
    |
large heap allocation
    |
GC pressure / OOM
```

Prefer streaming/bounded processing when the use case allows it.

------------------------------------------------------------------------

# Mini Coding Drill 4 --- Benchmarking

Bad benchmark:

``` java
long start = System.nanoTime();
for (int i = 0; i < 100; i++) {
    calculate();
}
System.out.println(System.nanoTime() - start);
```

Problems can include:

``` text
no proper warm-up
JIT compilation during measurement
constant folding
dead-code elimination
OS noise
GC noise
```

Use JMH for serious Java microbenchmarking.

------------------------------------------------------------------------

# JVM Production Observability Map

``` text
                        JVM SERVICE
                            |
       +--------------------+--------------------+
       |                    |                    |
      CPU                  MEMORY              THREADS
       |                    |                    |
       +-- process CPU      +-- heap used        +-- count
       +-- GC CPU           +-- after-GC         +-- states
       +-- hot methods      +-- metaspace        +-- pool active
                            +-- direct/native     +-- queue depth
                            +-- allocation rate   +-- blocked/waiting
                            |
                            v
                           GC
                            |
                       +----+----+
                       |         |
                    pauses    frequency
                       |         |
                       +----+----+
                            |
                            v
                         LATENCY
                            |
                    p50 / p95 / p99
```

For distributed systems, add:

``` text
JVM metrics
    +
Kubernetes metrics
    +
DB metrics
    +
Kafka/Redis metrics
    +
Distributed traces
    +
Logs
    =
Production diagnosis
```

------------------------------------------------------------------------

# Tool Mental Map

You do not need to memorize every command, but know which evidence you
need.

``` text
Need                       Typical tool/category
------------------------------------------------------------
Thread stacks              jstack / jcmd / profiler
Heap dump                  jcmd / JVM OOM dump configuration
Heap analysis              Eclipse MAT / profiler
GC behavior                GC logs / JFR / metrics
CPU profile                JFR / async-profiler / profiler
Class histogram            jcmd
Native memory              Native Memory Tracking / OS metrics
Live JVM metrics           JMX / Micrometer / observability stack
```

### Java Flight Recorder (JFR)

Think of JFR as low-overhead JVM/runtime event recording useful for
production-oriented diagnosis:

``` text
JFR
|
+-- CPU samples
+-- allocations
+-- locks
+-- GC events
+-- threads
+-- I/O/runtime events
```

------------------------------------------------------------------------

# Senior Rapid-Fire Follow-Ups

1.  Why can object allocation be cheap in Java?
2.  What is a TLAB?
3.  Does every local object live on the stack?
4.  What is escape analysis?
5.  Why can two classes with the same name be different types?
6.  What is parent delegation?
7.  Heap vs Metaspace?
8.  What are GC roots?
9.  Why can GC collect cyclic references?
10. Why are most collectors generational?
11. What is an STW pause?
12. Why can a GC pause cause retry storms?
13. G1 vs ZGC at a high level?
14. What is a humongous allocation in the G1 context?
15. What metrics tell you GC is unhealthy?
16. Why is increasing `-Xmx` not always the answer?
17. Interpreter vs JIT?
18. What is tiered compilation?
19. What is method inlining?
20. Why do Java benchmarks need warm-up?
21. What is deoptimization?
22. What is a Java memory leak?
23. How do you find why an object is retained?
24. Heap dump vs thread dump?
25. Why can a pod be OOMKilled without Java heap OOM?
26. What memory exists outside the heap?
27. What causes native-thread OOM?
28. How can ThreadLocal cause retention?
29. How do you debug 100% CPU?
30. How do you prove GC is causing p99 latency?

------------------------------------------------------------------------

# Interview Question Checklist

``` text
MEMORY / CLASS LOADING
[ ] 01 Java execution pipeline
[ ] 02 JVM runtime data areas
[ ] 03 Class loading lifecycle
[ ] 04 ClassLoader hierarchy
[ ] 05 Parent delegation
[ ] 06 Heap / Stack / Metaspace
[ ] 07 Object allocation
[ ] 08 TLAB
[ ] 09 Escape analysis
[ ] 10 Reference strengths

GARBAGE COLLECTION
[ ] 11 Reachability / GC roots
[ ] 12 Generational hypothesis
[ ] 13 Young / Survivor / Old
[ ] 14 Minor / Major / Full GC terminology
[ ] 15 Stop-the-world
[ ] 16 Mark / Sweep / Compact / Copy
[ ] 17 G1 GC
[ ] 18 ZGC
[ ] 19 GC logs / metrics
[ ] 20 GC tuning methodology

JIT
[ ] 21 Interpreter vs JIT
[ ] 22 Hot methods
[ ] 23 C1 / C2 / tiered compilation
[ ] 24 Inlining / devirtualization
[ ] 25 Warm-up / deoptimization

OOM / DUMPS / PERFORMANCE
[ ] 26 OutOfMemoryError overview
[ ] 27 Java heap space
[ ] 28 Metaspace OOM
[ ] 29 Direct/native memory
[ ] 30 Native thread OOM
[ ] 31 StackOverflowError
[ ] 32 Java memory leaks
[ ] 33 Heap dumps
[ ] 34 Thread dumps
[ ] 35 High CPU debugging
[ ] 36 High memory debugging
[ ] 37 Long GC pause debugging
[ ] 38 JVM latency debugging
[ ] 39 JVM memory in containers
[ ] 40 Performance methodology
```

------------------------------------------------------------------------

# Chapter 03 Visual Summary

``` text
                       JVM PERFORMANCE
                              |
          +-------------------+-------------------+
          |                   |                   |
      CLASS LOADING         MEMORY              EXECUTION
          |                   |                   |
     load/link/init       Heap / Stack        Interpreter
          |              / Metaspace              |
     ClassLoader              |                   v
          |                   v                  JIT
     class identity       Allocation               |
                              |                    +-- C1/C2
                              v                    +-- inline
                            TLAB                   +-- optimize
                              |
                              v
                             GC
                              |
                 +------------+------------+
                 |                         |
               G1/ZGC                   STW pauses
                 |                         |
                 +------------+------------+
                              |
                              v
                      SERVICE LATENCY
                              |
                     p50 / p95 / p99
```

------------------------------------------------------------------------

# Senior Cross-Layer Model

``` text
Incoming Traffic
      |
      v
Spring Boot
      |
      +--> object allocations
      |
      +--> thread activity
      |
      +--> serialization
      |
      v
JVM Heap / Native Memory
      |
      v
GC / JIT / CPU
      |
      v
Application latency
      |
      v
Timeouts
      |
      v
Retries
      |
      v
Kafka / Redis / DB / downstream load
```

This is why JVM performance is a **distributed backend topic**, not
merely a JVM trivia topic.

------------------------------------------------------------------------

# Chapter 03 Exit Criteria

You are ready for **CH 04 --- Spring Core** when you can explain aloud:

1.  `.java -> bytecode -> ClassLoader -> interpreter/JIT -> native execution`.
2.  Heap, stacks, Metaspace and native memory with an ASCII diagram.
3.  Loading, linking and initialization.
4.  ClassLoader hierarchy and parent delegation.
5.  Object allocation and why TLAB makes many allocations cheap.
6.  Escape analysis without claiming all non-escaping objects go to the
    stack.
7.  GC reachability and GC roots.
8.  Generational collection and object promotion conceptually.
9.  STW and how it affects p99 latency.
10. G1 architecture at a useful senior level.
11. When low-pause collectors such as ZGC are attractive.
12. Interpreter, JIT, tiered compilation, inlining and warm-up.
13. Heap OOM vs Metaspace vs native-thread/native-memory problems.
14. Why a Kubernetes pod can be OOMKilled below `-Xmx`.
15. Heap dump vs thread dump.
16. How to diagnose high CPU.
17. How to diagnose continuously growing memory.
18. How to correlate GC pauses with request latency.
19. Why unbounded caches, queues and ThreadLocals can leak memory.
20. The measure -\> profile -\> fix -\> load-test performance workflow.

``` text
CH 03 JVM & PERFORMANCE
       |
       v
Class Loading / Memory
       |
       v
Allocation / TLAB
       |
       v
GC / G1 / ZGC ★★★
       |
       v
JIT / Optimization
       |
       v
OOM / Heap Dump / Thread Dump ★★★
       |
       v
Production Performance Debugging ★★★
       |
       v
READY FOR PHASE 2
CH 04 — SPRING CORE
```

------------------------------------------------------------------------

# Next Chapter

``` text
PHASE 2 — SPRING + DATA

CH 04 — SPRING CORE (~35 Q)
|
+-- IoC / Dependency Injection
+-- Bean lifecycle / scopes
+-- Component scanning / configuration
+-- AOP / proxies
+-- JDK vs CGLIB proxies
+-- Spring internals / common traps
+-- Senior production scenarios
```
