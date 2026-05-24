# 002_JVM_Performance_Tuning_From_Scratch_Java21

# Clickable Index

- [Part 0 — Goal Of This Handbook](#part-0--goal-of-this-handbook)
- [Part 1 — How To Learn JVM Performance Correctly](#part-1--how-to-learn-jvm-performance-correctly)
- [Part 2 — Final Project We Are Building](#part-2--final-project-we-are-building)
- [Part 3 — JVM Mental Model From First Principles](#part-3--jvm-mental-model-from-first-principles)
- [Part 4 — Maven Project Setup](#part-4--maven-project-setup)
- [Part 5 — Phase 1: Baseline Performance Lab](#part-5--phase-1-baseline-performance-lab)
- [Part 6 — Phase 2: Allocation Pressure Lab](#part-6--phase-2-allocation-pressure-lab)
- [Part 7 — Phase 3: GC Pause Lab](#part-7--phase-3-gc-pause-lab)
- [Part 8 — Phase 4: Memory Leak Lab](#part-8--phase-4-memory-leak-lab)
- [Part 9 — Phase 5: Thread Explosion Lab](#part-9--phase-5-thread-explosion-lab)
- [Part 10 — Phase 6: Lock Contention Lab](#part-10--phase-6-lock-contention-lab)
- [Part 11 — Phase 7: JIT Warmup Lab](#part-11--phase-7-jit-warmup-lab)
- [Part 12 — Phase 8: Queue Backpressure Lab](#part-12--phase-8-queue-backpressure-lab)
- [Part 13 — Phase 9: IO and Buffer Lab](#part-13--phase-9-io-and-buffer-lab)
- [Part 14 — Phase 10: Mini Production Service](#part-14--phase-10-mini-production-service)
- [Part 15 — Observability Commands](#part-15--observability-commands)
- [Part 16 — JVM Tuning Playbook](#part-16--jvm-tuning-playbook)
- [Part 17 — Driver Classes](#part-17--driver-classes)
- [Part 18 — JUnit Tests](#part-18--junit-tests)
- [Part 19 — Production Checklist](#part-19--production-checklist)
- [Part 20 — Interview Explanation](#part-20--interview-explanation)
- [Part 21 — What This Teaches For Distributed Systems](#part-21--what-this-teaches-for-distributed-systems)
- [Part 22 — Next Advanced Extensions](#part-22--next-advanced-extensions)

---

# Part 0 — Goal Of This Handbook

We are building a JVM performance engineering lab from scratch using Java 21.

The goal is not to memorize JVM terms. The goal is to create bottlenecks, observe them, explain them using JVM internals, optimize them, and measure again.

Learning model:

```text
BUILD SYSTEM
    ↓
LOAD TEST
    ↓
OBSERVE BOTTLENECK
    ↓
LEARN JVM INTERNALS EXPLAINING BOTTLENECK
    ↓
OPTIMIZE
    ↓
MEASURE AGAIN
```

By the end, you should understand:

```text
Why latency spikes happen
Why GC pauses happen
Why object allocation matters
Why too many threads hurt performance
Why locks reduce throughput
Why queues create hidden latency
Why batching improves throughput
Why JIT needs warmup
Why heap size affects performance
Why native memory matters
Why p99 latency matters more than average latency
```

---

# Part 1 — How To Learn JVM Performance Correctly

Bad way:

```text
Read JVM theory
Read GC theory
Read random tuning flags
Try to memorize everything
```

Good way:

```text
Build a small system
Create one bottleneck intentionally
Observe it with JVM tools
Learn the internal reason
Optimize the bottleneck
Measure the result
```

Example:

```text
Build allocation-heavy service
    ↓
Observe frequent young GC
    ↓
Learn Eden, Survivor, TLAB, allocation rate
    ↓
Reduce temporary objects
    ↓
Measure lower GC frequency
```

Golden rule:

```text
Never tune blindly.
Measure first.
```

---

# Part 2 — Final Project We Are Building

Project name:

```text
jvm-performance-lab
```

Final folder structure:

```text
jvm-performance-lab/
├── pom.xml
├── README.md
├── scripts/
│   ├── run-gc-log.sh
│   ├── run-small-heap.sh
│   ├── run-jfr.sh
│   └── run-thread-dump.sh
├── src/main/java/com/jvmlab/
│   ├── Main.java
│   ├── common/
│   │   ├── StopWatch.java
│   │   └── MemoryReporter.java
│   ├── phase1/BaselineThroughputDemo.java
│   ├── phase2/AllocationPressureDemo.java
│   ├── phase3/GcPauseDemo.java
│   ├── phase4/MemoryLeakDemo.java
│   ├── phase5/ThreadExplosionDemo.java
│   ├── phase6/SynchronizedCounterDemo.java
│   ├── phase6/LongAdderCounterDemo.java
│   ├── phase7/JitWarmupDemo.java
│   ├── phase8/QueueBackpressureDemo.java
│   ├── phase9/BufferIoDemo.java
│   └── phase10/
│       ├── MiniHttpLikeServer.java
│       ├── Request.java
│       ├── Response.java
│       └── RequestProcessor.java
└── src/test/java/com/jvmlab/
    ├── MetricsTest.java
    ├── AllocationPressureTest.java
    ├── CounterTest.java
    └── QueueBackpressureTest.java
```


---

# Part 3 — JVM Mental Model From First Principles

## 3.1 Java Source To Machine Code

```text
Java source
    ↓ javac
Bytecode
    ↓ JVM interpreter
Executed slowly first
    ↓ JVM detects hot methods
JIT compiles hot bytecode
    ↓
Native machine code
```

Important:

```text
Java performance changes over time.
Cold code may be slower.
Hot code becomes faster after JIT compilation.
```

This is why benchmarking without warmup is misleading.

## 3.2 JVM Runtime Areas

```text
JVM Process
├── Heap
│   ├── Young Generation
│   │   ├── Eden
│   │   └── Survivor
│   └── Old Generation
├── Thread Stacks
├── Metaspace
├── Code Cache
├── Direct / Native Memory
└── GC / Compiler / VM Threads
```

| Area | Stores |
|---|---|
| Heap | objects and arrays |
| Stack | method calls and local variables |
| Metaspace | class metadata |
| Code Cache | JIT compiled native code |
| Direct Memory | off-heap buffers |
| Native Memory | JVM/internal/native allocations |

## 3.3 Stack vs Heap

```java
User user = new User("Alice");
```

Mental model:

```text
Thread Stack
└── local variable user
        |
        v
Heap
└── User object
    └── name -> "Alice"
```

Stack:

```text
Per thread
Fast
Method frames
Local variables
References
```

Heap:

```text
Shared by all threads
Objects live here
Garbage collected
Can cause GC pressure
```

## 3.4 Object Allocation Path

Most Java objects are allocated in Eden.

Fast path:

```text
Thread wants new object
    ↓
Uses TLAB
    ↓
Bump pointer allocation
    ↓
Very fast
```

TLAB means Thread Local Allocation Buffer.

Why TLAB exists:

```text
Multiple threads allocating objects from shared heap would contend.
TLAB gives each thread a small private allocation region.
```

Object lifecycle:

```text
new object
    ↓
Eden
    ↓ survives young GC
Survivor
    ↓ survives multiple GCs
Old generation
```

## 3.5 Garbage Collection Mental Model

GC finds objects that are no longer reachable.

GC roots:

```text
Thread stacks
Static fields
JNI references
Class metadata
```

Reachability:

```text
GC Roots
   |
   v
Live objects
```

If object cannot be reached:

```text
Garbage
```

Young GC:

```text
Collects young generation
Usually frequent and short
```

Old GC:

```text
Collects old generation
Usually less frequent but more expensive
```

Stop-the-world:

```text
Application threads pause
GC runs
Application resumes
```

Latency spike example:

```text
Request arrives
    ↓
GC pause starts
    ↓
Request waits
    ↓
p99 latency spikes
```

## 3.6 JIT Compiler Mental Model

JIT optimizes hot methods.

Hot method:

```text
A method called many times
or loop executed many times
```

JIT optimizations:

```text
Inlining
Dead code elimination
Loop optimization
Escape analysis
Lock elimination
```

## 3.7 Threads, OS Scheduler, and CPU

Java platform thread maps to OS thread.

If you create too many platform threads:

```text
More runnable threads than CPU cores
    ↓
OS context switching
    ↓
CPU overhead
    ↓
latency increases
```

Diagram:

```text
1000 Java threads
       |
       v
OS scheduler
       |
       v
8 CPU cores
```

Only a few threads run at a time.

## 3.8 Locks, CAS, and Memory Visibility

`synchronized`:

```text
Mutual exclusion
Visibility guarantee
Can block threads
```

Atomic variables:

```text
Use CAS
Avoid blocking in many cases
```

CAS:

```text
Compare current value with expected
If same, update
Else retry
```

`volatile` provides visibility and ordering for a variable.

## 3.9 IO, Page Cache, and Native Memory

Not all memory is Java heap.

Example:

```java
ByteBuffer.allocateDirect(1024)
```

uses off-heap/native memory.

File IO often uses OS page cache.

Kafka-like systems are fast because:

```text
Sequential writes
Batching
OS page cache
Zero-copy
```


---

# Part 4 — Maven Project Setup

## pom.xml

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.jvmlab</groupId>
    <artifactId>jvm-performance-lab</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.release>21</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>5.10.2</junit.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
            </plugin>
        </plugins>
    </build>
</project>
```

## Common Utility: StopWatch.java

```java
package com.jvmlab.common;

public class StopWatch {
    private final long startNanos;

    private StopWatch() {
        this.startNanos = System.nanoTime();
    }

    public static StopWatch start() {
        return new StopWatch();
    }

    public long elapsedMillis() {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }

    public long elapsedNanos() {
        return System.nanoTime() - startNanos;
    }
}
```

## Common Utility: MemoryReporter.java

```java
package com.jvmlab.common;

public class MemoryReporter {
    public static void printMemory(String label) {
        Runtime runtime = Runtime.getRuntime();

        long max = runtime.maxMemory();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = total - free;

        System.out.println("[" + label + "]");
        System.out.println("Used MB  = " + toMb(used));
        System.out.println("Free MB  = " + toMb(free));
        System.out.println("Total MB = " + toMb(total));
        System.out.println("Max MB   = " + toMb(max));
    }

    private static long toMb(long bytes) {
        return bytes / 1024 / 1024;
    }
}
```


---

# Part 5 — Phase 1: Baseline Performance Lab

## What are we building?

A simple benchmark runner that executes a CPU task many times and reports:

```text
total time
throughput
memory usage
```

## Why this phase?

Before tuning, you need baseline measurement.

Without baseline:

```text
Optimization is guessing
```

## What we care about

```text
Measure before changing
Repeat runs
Warmup matters
Average is not enough
```

## BaselineThroughputDemo.java

```java
package com.jvmlab.phase1;

import com.jvmlab.common.MemoryReporter;
import com.jvmlab.common.StopWatch;

public class BaselineThroughputDemo {
    public static void main(String[] args) {
        int iterations = 100_000_000;

        MemoryReporter.printMemory("before");

        StopWatch sw = StopWatch.start();

        long sum = 0;
        for (int i = 0; i < iterations; i++) {
            sum += compute(i);
        }

        long elapsedMs = sw.elapsedMillis();

        MemoryReporter.printMemory("after");

        System.out.println("sum = " + sum);
        System.out.println("elapsedMs = " + elapsedMs);
        System.out.println("ops/sec = " + (iterations * 1000L / Math.max(1, elapsedMs)));
    }

    private static int compute(int x) {
        return (x * 31) ^ (x >>> 3);
    }
}
```

## Dry run

```text
Start program
    ↓
Print memory before
    ↓
Run compute loop
    ↓
Print memory after
    ↓
Print throughput
```

## Bottleneck questions

Ask:

```text
Is CPU high?
Is memory growing?
Is result stable across runs?
Is first run slower than later runs?
```

If first run is slower, possible reason:

```text
JIT warmup
class loading
initial compilation
```


---

# Part 6 — Phase 2: Allocation Pressure Lab

## What are we building?

A program that creates many temporary objects.

## Why this phase?

To understand allocation rate and GC pressure.

## What problem does this expose?

```text
Too many short-lived objects
    ↓
Eden fills quickly
    ↓
Frequent young GC
    ↓
CPU spent on GC
    ↓
Latency spikes
```

## AllocationPressureDemo.java

```java
package com.jvmlab.phase2;

import com.jvmlab.common.MemoryReporter;
import com.jvmlab.common.StopWatch;

import java.util.ArrayList;
import java.util.List;

public class AllocationPressureDemo {
    public static void main(String[] args) {
        int batches = 100;
        int objectsPerBatch = 100_000;

        StopWatch sw = StopWatch.start();

        for (int batch = 1; batch <= batches; batch++) {
            List<byte[]> temp = new ArrayList<>();

            for (int i = 0; i < objectsPerBatch; i++) {
                temp.add(new byte[1024]);
            }

            if (batch % 10 == 0) {
                MemoryReporter.printMemory("batch " + batch);
            }
        }

        System.out.println("elapsedMs = " + sw.elapsedMillis());
    }
}
```

## Run with GC logs

```bash
java -Xms256m -Xmx256m -Xlog:gc* -cp target/classes com.jvmlab.phase2.AllocationPressureDemo
```

## Observe

You should see:

```text
Frequent GC logs
Heap usage rising and falling
Application still completes
```

## Learn internals

This is mostly young generation churn.

```text
Object allocated in Eden
Eden fills
Young GC runs
Most temp objects dead
Memory reclaimed
```

## Optimization idea

Bad:

```text
Create many temporary arrays
```

Better:

```text
Reuse buffers
Batch work
Avoid unnecessary object creation
Use primitive arrays where possible
```


---

# Part 7 — Phase 3: GC Pause Lab

## What are we building?

A program that keeps many objects alive.

## Why?

Short-lived objects are easier for GC.

Long-lived objects are harder because they survive and move to old generation.

## GcPauseDemo.java

```java
package com.jvmlab.phase3;

import com.jvmlab.common.MemoryReporter;

import java.util.ArrayList;
import java.util.List;

public class GcPauseDemo {
    private static final List<byte[]> retained = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        int rounds = 200;

        for (int round = 1; round <= rounds; round++) {
            retained.add(new byte[1024 * 1024]);

            for (int i = 0; i < 10_000; i++) {
                byte[] temp = new byte[1024];
            }

            if (round % 10 == 0) {
                MemoryReporter.printMemory("round " + round);
                Thread.sleep(100);
            }
        }

        System.out.println("retained objects = " + retained.size());
    }
}
```

## Run

```bash
java -Xms256m -Xmx256m -Xlog:gc* -cp target/classes com.jvmlab.phase3.GcPauseDemo
```

## Observe

Possible results:

```text
GC more frequent
Old generation grows
Eventually OutOfMemoryError possible
```

## Learn

```text
Live set size matters
GC must keep reachable objects
Old generation pressure increases pause risk
```

## Distributed system connection

In a Kafka consumer:

```text
Retaining too many messages
    ↓
Heap grows
    ↓
GC pause
    ↓
Consumer stops polling
    ↓
Consumer lag increases
```


---

# Part 8 — Phase 4: Memory Leak Lab

## What are we building?

A controlled memory leak.

## Why?

Memory leaks in Java are not usually lost pointers.

They are usually:

```text
unwanted retained references
```

## MemoryLeakDemo.java

```java
package com.jvmlab.phase4;

import com.jvmlab.common.MemoryReporter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryLeakDemo {
    private static final Map<String, byte[]> cache = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        int i = 0;

        while (true) {
            cache.put(UUID.randomUUID().toString(), new byte[1024 * 100]);

            if (++i % 100 == 0) {
                MemoryReporter.printMemory("entries " + cache.size());
                Thread.sleep(100);
            }
        }
    }
}
```

## Run

```bash
java -Xms256m -Xmx256m -Xlog:gc* -cp target/classes com.jvmlab.phase4.MemoryLeakDemo
```

## Observe

```text
Heap keeps growing
GC cannot reclaim
Eventually OOM
```

## Learn

Object is not garbage if reachable:

```text
static map
   ↓
key
   ↓
value byte[]
```

## Fix strategies

```text
TTL
max size
LRU eviction
weak references
remove unused entries
cache metrics
```

## Distributed system connection

MiniRedis teaches:

```text
cache must have eviction
TTL prevents unbounded growth
memory limit is mandatory
```


---

# Part 9 — Phase 5: Thread Explosion Lab

## What are we building?

A program that creates too many platform threads.

## Why?

To understand why thread pools and virtual threads matter.

## ThreadExplosionDemo.java

```java
package com.jvmlab.phase5;

import com.jvmlab.common.MemoryReporter;

public class ThreadExplosionDemo {
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 10_000;

        for (int i = 0; i < threadCount; i++) {
            int id = i;

            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(60_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "platform-thread-" + id);

            t.start();

            if (i % 1000 == 0) {
                System.out.println("created threads = " + i);
                MemoryReporter.printMemory("threads");
            }
        }

        Thread.sleep(60_000);
    }
}
```

## Observe

Possible results:

```text
High memory usage
Slow startup
Native thread creation failure
OS overhead
```

## Learn

Each platform thread needs:

```text
native stack
OS scheduling
kernel resources
```

## Try virtual threads

```java
Thread.startVirtualThread(() -> {
    try {
        Thread.sleep(60_000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});
```

Virtual threads are much lighter for blocking workloads.

## Distributed system connection

API Gateway:

```text
Many blocking calls
    ↓
Platform threads expensive
    ↓
Virtual threads can help
```

But CPU-bound work still needs limited parallelism.


---

# Part 10 — Phase 6: Lock Contention Lab

## What are we building?

Two counters:

```text
synchronized counter
LongAdder counter
```

## Why?

To see lock contention.

## SynchronizedCounterDemo.java

```java
package com.jvmlab.phase6;

import com.jvmlab.common.StopWatch;

import java.util.ArrayList;
import java.util.List;

public class SynchronizedCounterDemo {
    private long counter = 0;

    public synchronized void increment() {
        counter++;
    }

    public long value() {
        return counter;
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizedCounterDemo demo = new SynchronizedCounterDemo();

        int threads = 8;
        int incrementsPerThread = 2_000_000;

        List<Thread> workers = new ArrayList<>();
        StopWatch sw = StopWatch.start();

        for (int t = 0; t < threads; t++) {
            Thread worker = new Thread(() -> {
                for (int i = 0; i < incrementsPerThread; i++) {
                    demo.increment();
                }
            });
            workers.add(worker);
            worker.start();
        }

        for (Thread worker : workers) {
            worker.join();
        }

        System.out.println("value = " + demo.value());
        System.out.println("elapsedMs = " + sw.elapsedMillis());
    }
}
```

## LongAdderCounterDemo.java

```java
package com.jvmlab.phase6;

import com.jvmlab.common.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

public class LongAdderCounterDemo {
    private final LongAdder counter = new LongAdder();

    public void increment() {
        counter.increment();
    }

    public long value() {
        return counter.sum();
    }

    public static void main(String[] args) throws InterruptedException {
        LongAdderCounterDemo demo = new LongAdderCounterDemo();

        int threads = 8;
        int incrementsPerThread = 2_000_000;

        List<Thread> workers = new ArrayList<>();
        StopWatch sw = StopWatch.start();

        for (int t = 0; t < threads; t++) {
            Thread worker = new Thread(() -> {
                for (int i = 0; i < incrementsPerThread; i++) {
                    demo.increment();
                }
            });
            workers.add(worker);
            worker.start();
        }

        for (Thread worker : workers) {
            worker.join();
        }

        System.out.println("value = " + demo.value());
        System.out.println("elapsedMs = " + sw.elapsedMillis());
    }
}
```

## Observe

Usually:

```text
LongAdder faster under high contention
```

## Learn

`synchronized` forces one thread at a time.

LongAdder spreads updates across cells, reducing contention.

## Distributed system connection

Metrics counters in high-scale services should avoid hot locks.


---

# Part 11 — Phase 7: JIT Warmup Lab

## What are we building?

A demo showing warmup effect.

## Why?

JIT needs time to optimize hot methods.

## JitWarmupDemo.java

```java
package com.jvmlab.phase7;

public class JitWarmupDemo {
    public static void main(String[] args) {
        for (int round = 1; round <= 20; round++) {
            long start = System.nanoTime();

            long result = 0;
            for (int i = 0; i < 50_000_000; i++) {
                result += hotMethod(i);
            }

            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            System.out.println("round=" + round + ", elapsedMs=" + elapsedMs + ", result=" + result);
        }
    }

    private static int hotMethod(int x) {
        return (x * 17) ^ (x >>> 2);
    }
}
```

## Observe

Early rounds may be slower.

Later rounds may become faster.

## Learn

```text
Interpreter first
Profiling
JIT compilation
Optimized native code
```

## Run with compilation logs

```bash
java -XX:+UnlockDiagnosticVMOptions -XX:+PrintCompilation -cp target/classes com.jvmlab.phase7.JitWarmupDemo
```


---

# Part 12 — Phase 8: Queue Backpressure Lab

## What are we building?

Producer-consumer queue simulation.

## Why?

Queues hide overload until latency explodes.

## QueueBackpressureDemo.java

```java
package com.jvmlab.phase8;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class QueueBackpressureDemo {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(100);

        Thread consumer = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Integer item = queue.take();
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "slow-consumer");

        consumer.start();

        for (int i = 1; i <= 1000; i++) {
            boolean accepted = queue.offer(i);

            if (!accepted) {
                System.out.println("Rejected item " + i + ", queue full");
            }

            if (i % 50 == 0) {
                System.out.println("submitted=" + i + ", queueSize=" + queue.size());
            }

            Thread.sleep(5);
        }

        consumer.interrupt();
        consumer.join();
    }
}
```

## Observe

```text
Producer faster than consumer
Queue fills
Rejections happen
Latency would increase
```

## Learn

Backpressure is mandatory.

Options:

```text
reject
block
drop
scale consumers
batch
slow producer
```


---

# Part 13 — Phase 9: IO and Buffer Lab

## What are we building?

File write benchmark.

## Why?

Kafka and log systems depend on IO behavior.

## BufferIoDemo.java

```java
package com.jvmlab.phase9;

import com.jvmlab.common.StopWatch;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BufferIoDemo {
    public static void main(String[] args) throws IOException {
        int records = 1_000_000;
        byte[] data = "hello-jvm-performance\n".getBytes(StandardCharsets.UTF_8);

        StopWatch sw = StopWatch.start();

        try (BufferedOutputStream out =
                     new BufferedOutputStream(new FileOutputStream("target/io-demo.log"))) {

            for (int i = 0; i < records; i++) {
                out.write(data);
            }
        }

        System.out.println("elapsedMs = " + sw.elapsedMillis());
        System.out.println("records/sec = " + (records * 1000L / Math.max(1, sw.elapsedMillis())));
    }
}
```

## Learn

Batching helps.

Buffered IO reduces syscall overhead.

Distributed system connection:

```text
Kafka batches records before writing
Log aggregation batches events
Metrics systems batch samples
```


---

# Part 14 — Phase 10: Mini Production Service

## What are we building?

A simple request processing service.

No Spring Boot.

Just simulate:

```text
request arrives
    ↓
queue
    ↓
worker threads
    ↓
processing
    ↓
metrics
```

## Why?

This combines JVM performance concepts:

```text
threads
queue
allocation
CPU
latency
metrics
backpressure
```

## Request.java

```java
package com.jvmlab.phase10;

public record Request(long id, long createdAtNanos, String payload) {
}
```

## Response.java

```java
package com.jvmlab.phase10;

public record Response(long requestId, long latencyMicros, boolean success) {
}
```

## RequestProcessor.java

```java
package com.jvmlab.phase10;

public class RequestProcessor {
    public Response process(Request request) {
        long checksum = 0;

        for (int i = 0; i < request.payload().length(); i++) {
            checksum += request.payload().charAt(i) * 31L;
        }

        boolean success = checksum >= 0;
        long latencyMicros = (System.nanoTime() - request.createdAtNanos()) / 1000;

        return new Response(request.id(), latencyMicros, success);
    }
}
```

## MiniHttpLikeServer.java

```java
package com.jvmlab.phase10;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class MiniHttpLikeServer {
    private final BlockingQueue<Request> queue;
    private final List<Thread> workers = new ArrayList<>();
    private final RequestProcessor processor = new RequestProcessor();
    private final AtomicLong accepted = new AtomicLong();
    private final AtomicLong rejected = new AtomicLong();
    private final AtomicLong completed = new AtomicLong();
    private final List<Long> latenciesMicros = Collections.synchronizedList(new ArrayList<>());
    private volatile boolean running = true;

    public MiniHttpLikeServer(int workerCount, int queueCapacity) {
        this.queue = new ArrayBlockingQueue<>(queueCapacity);

        for (int i = 0; i < workerCount; i++) {
            Thread worker = new Thread(this::workerLoop, "service-worker-" + i);
            workers.add(worker);
            worker.start();
        }
    }

    public boolean submit(Request request) {
        boolean ok = queue.offer(request);

        if (ok) {
            accepted.incrementAndGet();
        } else {
            rejected.incrementAndGet();
        }

        return ok;
    }

    public void shutdown() throws InterruptedException {
        running = false;

        for (Thread worker : workers) {
            worker.interrupt();
        }

        for (Thread worker : workers) {
            worker.join();
        }
    }

    public void printMetrics() {
        List<Long> snapshot;

        synchronized (latenciesMicros) {
            snapshot = new ArrayList<>(latenciesMicros);
        }

        Collections.sort(snapshot);

        long p50 = percentile(snapshot, 50);
        long p95 = percentile(snapshot, 95);
        long p99 = percentile(snapshot, 99);

        System.out.println("accepted=" + accepted.get());
        System.out.println("completed=" + completed.get());
        System.out.println("rejected=" + rejected.get());
        System.out.println("queueSize=" + queue.size());
        System.out.println("p50Micros=" + p50);
        System.out.println("p95Micros=" + p95);
        System.out.println("p99Micros=" + p99);
    }

    private void workerLoop() {
        while (running || !queue.isEmpty()) {
            try {
                Request request = queue.poll(100, TimeUnit.MILLISECONDS);

                if (request == null) {
                    continue;
                }

                Response response = processor.process(request);
                latenciesMicros.add(response.latencyMicros());
                completed.incrementAndGet();

            } catch (InterruptedException e) {
                if (!running) {
                    break;
                }
            }
        }
    }

    private static long percentile(List<Long> values, int percentile) {
        if (values.isEmpty()) {
            return 0;
        }

        int index = (int) Math.ceil(percentile / 100.0 * values.size()) - 1;
        return values.get(Math.max(0, Math.min(index, values.size() - 1)));
    }

    public static void main(String[] args) throws InterruptedException {
        MiniHttpLikeServer server = new MiniHttpLikeServer(4, 1000);

        int totalRequests = 100_000;

        for (int i = 1; i <= totalRequests; i++) {
            Request request = new Request(
                    i,
                    System.nanoTime(),
                    "payload-" + i
            );

            server.submit(request);
        }

        Thread.sleep(3000);
        server.printMetrics();
        server.shutdown();
    }
}
```

## What to observe

```text
accepted
rejected
queue size
p50
p95
p99
```

Why p99 matters:

```text
Most users may be fast
But some users experience bad latency
High-scale systems care about tail latency
```


---

# Part 15 — Observability Commands

## Show Java process

```bash
jps -l
```

## Thread dump

```bash
jstack <pid>
```

Use when:

```text
threads stuck
deadlock suspected
blocked threads
```

## Heap histogram

```bash
jcmd <pid> GC.class_histogram
```

Use when:

```text
memory usage high
want to know object counts
```

## GC logs

```bash
java -Xlog:gc* -jar app.jar
```

## JFR recording

```bash
java -XX:StartFlightRecording=filename=recording.jfr,duration=60s -jar app.jar
```

## Heap dump on OOM

```bash
java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof -jar app.jar
```

---

# Part 16 — JVM Tuning Playbook

## Rule 1: Do not tune before measuring

Bad:

```text
Let me change GC flags randomly
```

Good:

```text
Measure p99 latency
Check GC logs
Check allocation rate
Check thread dump
Then tune
```

## Rule 2: Heap sizing

Common baseline:

```bash
-Xms2g -Xmx2g
```

Why same value?

```text
Avoid heap resizing cost
More predictable behavior
```

## Rule 3: Choose GC based on goal

| Goal | Possible GC |
|---|---|
| simple small app | default GC |
| balanced service | G1GC |
| low latency | ZGC |
| throughput batch job | ParallelGC |

## Rule 4: Reduce allocation before tuning GC

Best GC optimization:

```text
Create less garbage
```

## Rule 5: Watch tail latency

Average latency can hide problems.

Always watch:

```text
p95
p99
p999
```


---

# Part 17 — Driver Classes

## Main.java

```java
package com.jvmlab;

import com.jvmlab.phase1.BaselineThroughputDemo;
import com.jvmlab.phase2.AllocationPressureDemo;
import com.jvmlab.phase6.SynchronizedCounterDemo;
import com.jvmlab.phase6.LongAdderCounterDemo;
import com.jvmlab.phase10.MiniHttpLikeServer;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return;
        }

        switch (args[0]) {
            case "baseline" -> BaselineThroughputDemo.main(new String[]{});
            case "allocation" -> AllocationPressureDemo.main(new String[]{});
            case "sync-counter" -> SynchronizedCounterDemo.main(new String[]{});
            case "longadder-counter" -> LongAdderCounterDemo.main(new String[]{});
            case "service" -> MiniHttpLikeServer.main(new String[]{});
            default -> printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  baseline");
        System.out.println("  allocation");
        System.out.println("  sync-counter");
        System.out.println("  longadder-counter");
        System.out.println("  service");
    }
}
```

---

# Part 18 — JUnit Tests

## MetricsTest.java

```java
package com.jvmlab;

import com.jvmlab.common.StopWatch;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetricsTest {
    @Test
    void stopwatchShouldMeasureElapsedTime() throws InterruptedException {
        StopWatch sw = StopWatch.start();
        Thread.sleep(10);
        assertTrue(sw.elapsedMillis() >= 10);
    }
}
```

## CounterTest.java

```java
package com.jvmlab;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.LongAdder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CounterTest {
    @Test
    void longAdderShouldCountCorrectly() {
        LongAdder adder = new LongAdder();

        for (int i = 0; i < 1000; i++) {
            adder.increment();
        }

        assertEquals(1000, adder.sum());
    }
}
```

## QueueBackpressureTest.java

```java
package com.jvmlab;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueueBackpressureTest {
    @Test
    void boundedQueueShouldRejectWhenFull() {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1);

        assertTrue(queue.offer(1));
        assertFalse(queue.offer(2));
    }
}
```

## AllocationPressureTest.java

```java
package com.jvmlab;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AllocationPressureTest {
    @Test
    void shouldAllocateArray() {
        byte[] data = new byte[1024];
        assertEquals(1024, data.length);
    }
}
```


---

# Part 19 — Production Checklist

Before deploying Java service:

```text
Set heap size intentionally
Enable GC logs
Expose metrics
Track p95/p99 latency
Track allocation rate
Track queue depth
Track active threads
Track rejected requests
Track GC pause time
Take thread dumps during incidents
Use JFR for profiling
```

Important production metrics:

| Metric | Why |
|---|---|
| CPU usage | saturation |
| heap used | memory pressure |
| GC pause | latency spike |
| allocation rate | garbage pressure |
| thread count | scheduling overhead |
| blocked threads | lock contention |
| queue depth | overload |
| p99 latency | tail user pain |
| error rate | failure |
| throughput | capacity |

---

# Part 20 — Interview Explanation

## How do you approach JVM performance tuning?

Answer:

```text
I first define the performance symptom:
high latency, low throughput, high CPU, memory growth, or GC pauses.

Then I measure using metrics, GC logs, thread dumps, heap histograms, and profiling tools like JFR.

I avoid changing JVM flags blindly.

If the issue is GC, I check allocation rate, live set size, heap sizing, and object retention.
If the issue is CPU, I profile hot methods and check lock contention.
If the issue is latency, I check queue depth, GC pauses, blocking IO, and p99 latency.
Then I optimize the application first, tune JVM second, and measure again.
```

## Why is reducing allocation important?

```text
Every allocated object eventually creates GC work.
High allocation rate fills Eden quickly.
This causes frequent young GC.
If objects survive, they promote to old generation.
Large live sets increase GC cost.
```

## Why can too many threads reduce performance?

```text
Platform threads consume native memory and require OS scheduling.
If runnable threads far exceed CPU cores, context switching increases.
CPU spends time switching instead of doing useful work.
```

## Why does p99 matter?

```text
Average latency hides tail problems.
In distributed systems, one slow dependency can slow the whole request path.
Tail latency affects user experience and system reliability.
```

---

# Part 21 — What This Teaches For Distributed Systems

| JVM concept | Distributed system connection |
|---|---|
| GC pause | Kafka consumer lag, API latency |
| allocation rate | log parsing, serialization |
| heap leak | cache and session stores |
| thread explosion | gateways, blocking RPC |
| lock contention | metrics, counters, queues |
| p99 latency | user-facing systems |
| backpressure | Kafka, MQ, API Gateway |
| batching | Kafka, ELK, metrics |
| direct memory | Netty, Kafka, file IO |
| JIT warmup | service startup performance |

After this lab, you understand why production systems care about:

```text
bounded queues
backpressure
low allocation
efficient serialization
thread pool sizing
GC tuning
tail latency
observability
```

---

# Part 22 — Next Advanced Extensions

Add these later:

```text
1. JMH benchmark module
2. async-profiler flame graph setup
3. JFR automated recordings
4. Prometheus metrics endpoint
5. Netty server performance lab
6. Direct ByteBuffer lab
7. mmap FileChannel lab
8. False sharing benchmark
9. Virtual thread server lab
10. Docker memory limit lab
11. Kubernetes JVM container tuning lab
12. G1 vs ZGC comparison lab
```

---

# Final Mental Model

When a Java system is slow, ask:

```text
Is CPU saturated?
Is GC pausing?
Is allocation rate high?
Is heap too small?
Is live set too large?
Are threads blocked?
Is queue depth growing?
Is IO slow?
Is p99 latency high?
Is JIT warmed up?
Is native memory growing?
```

Then:

```text
Measure
Explain
Optimize
Measure again
```

That is JVM performance engineering.
