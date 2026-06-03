# 002_Process_vs_Thread.md

# MiniConcurrency — 002 Process vs Thread

## 0. Why This Topic Matters

Before learning locks, race conditions, thread pools, Kafka consumers, Redis workers, or Spring Boot request handling, you must understand this foundation:

```text
Process = running application
Thread  = worker inside that application
```

In backend systems, almost everything runs using processes and threads.

Examples:

```text
Java Spring Boot app     -> one JVM process
HTTP request handler     -> thread from thread pool
Kafka consumer service   -> process with consumer threads
Redis server             -> process handling commands
Database server          -> process with many worker threads
Docker container         -> usually one main process
Kubernetes pod           -> runs one or more containers/processes
```

If you understand process vs thread clearly, you will understand concurrency, parallelism, performance, scaling, and production debugging much better.

---

# 1. Simple Definition

## Process

A process is a running program with its own memory.

Example:

```text
You run:
java -jar order-service.jar

Operating system creates:
Order Service Process
```

That process has:

```text
own memory
own heap
own stack area
own file descriptors
own network sockets
own process id
```

---

## Thread

A thread is a smaller execution unit inside a process.

One process can have many threads.

Example:

```text
Order Service Process
├── main thread
├── HTTP worker thread 1
├── HTTP worker thread 2
├── Kafka consumer thread
├── scheduler thread
└── GC thread
```

Each thread executes code independently, but threads inside the same process share memory.

---

# 2. Best Mental Model

```text
Process = house
Thread  = person working inside the house
```

Each house has its own rooms and resources.

```text
House A cannot directly use House B's private rooms.
```

But people inside the same house share the same kitchen, table, and tools.

That is why threads can easily share data, but that also creates race conditions.

---

# 3. Process vs Thread Diagram

```text
Operating System
│
├── Process A: order-service
│   ├── Thread 1: handles request /orders/1
│   ├── Thread 2: handles request /orders/2
│   └── Thread 3: Kafka consumer
│
├── Process B: payment-service
│   ├── Thread 1: handles payment request
│   └── Thread 2: background retry worker
│
└── Process C: redis-server
    └── Thread / event loop handles commands
```

Important:

```text
Different processes do not share heap memory directly.
Threads in the same process share heap memory.
```

---

# 4. Key Difference Table

| Feature | Process | Thread |
|---|---|---|
| Meaning | Running program | Execution unit inside process |
| Memory | Own memory | Shares process memory |
| Creation cost | Expensive | Cheaper |
| Communication | Harder, needs IPC/network/files | Easier, shared objects |
| Failure impact | Process crash may not kill others | Thread crash can affect same process |
| Isolation | Strong | Weak |
| Example | Spring Boot service JVM | Request worker thread |

---

# 5. Java View

When you run a Java application:

```bash
java Main
```

The operating system creates one JVM process.

Inside that JVM process, Java creates threads.

```text
JVM Process
├── main thread
├── garbage collector threads
├── JIT compiler threads
├── HTTP server threads
├── async executor threads
└── application-created threads
```

---

# 6. Java Example — One Process, Multiple Threads

```java
public class ProcessVsThreadDemo {

    public static void main(String[] args) {

        System.out.println("Main method started");
        System.out.println("Current thread: " + Thread.currentThread().getName());

        Thread worker1 = new Thread(() -> {
            System.out.println("Worker 1 running on: " + Thread.currentThread().getName());
        });

        Thread worker2 = new Thread(() -> {
            System.out.println("Worker 2 running on: " + Thread.currentThread().getName());
        });

        worker1.start();
        worker2.start();

        System.out.println("Main method finished");
    }
}
```

---

# 7. Step-by-Step Dry Run

## Step 1

Program starts.

```text
OS creates JVM process
JVM creates main thread
main() starts running
```

Output:

```text
Main method started
Current thread: main
```

---

## Step 2

Java creates two thread objects.

```java
Thread worker1 = new Thread(...);
Thread worker2 = new Thread(...);
```

Important:

```text
Thread object created, but actual thread has not started yet.
```

---

## Step 3

`worker1.start()` tells JVM/OS:

```text
Create a new execution thread.
Run the lambda code inside that thread.
```

Possible output:

```text
Worker 1 running on: Thread-0
```

---

## Step 4

`worker2.start()` creates another execution thread.

Possible output:

```text
Worker 2 running on: Thread-1
```

---

## Step 5

Main thread continues independently.

Possible output:

```text
Main method finished
```

---

# 8. Important Observation — Output Order Is Not Guaranteed

Possible output 1:

```text
Main method started
Current thread: main
Worker 1 running on: Thread-0
Worker 2 running on: Thread-1
Main method finished
```

Possible output 2:

```text
Main method started
Current thread: main
Main method finished
Worker 1 running on: Thread-0
Worker 2 running on: Thread-1
```

Why?

Because thread scheduling is controlled by the OS/JVM.

```text
You start threads.
But you do not control exact execution order.
```

This is the first major concurrency lesson.

---

# 9. Java Example — Shared Memory Between Threads

```java
public class SharedMemoryDemo {

    static int counter = 0;

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            counter++;
        });

        Thread t2 = new Thread(() -> {
            counter++;
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Counter = " + counter);
    }
}
```

Expected:

```text
Counter = 2
```

But in more repeated cases, shared memory can create wrong results due to race conditions.

---

# 10. Why Shared Memory Is Powerful

Threads inside the same process can access the same variable:

```java
static int counter = 0;
```

This is useful because communication is easy.

```text
Thread 1 updates counter
Thread 2 reads counter
```

But this is also dangerous.

```text
Two threads update same data at same time
        ↓
Race condition
        ↓
Wrong result
```

---

# 11. Process Isolation Example

Suppose you run two Java programs separately:

```bash
java OrderService
java PaymentService
```

Operating system creates:

```text
Process 1: OrderService JVM
Process 2: PaymentService JVM
```

Each process has separate memory.

```text
OrderService counter != PaymentService counter
```

They cannot directly access each other's Java objects.

To communicate, they need:

```text
HTTP
Kafka
Redis
Database
gRPC
files
sockets
```

---

# 12. Backend Real-World Mapping

## Spring Boot

```text
Spring Boot application = one JVM process
Each request = handled by a worker thread
```

Example:

```text
100 concurrent HTTP requests
        ↓
Tomcat thread pool may use 100 worker threads
```

---

## Redis

```text
Redis server = process
Commands handled by event loop / worker threads depending on version/features
```

MiniRedis helps you understand:

```text
server process
client connection handling
command execution
shared in-memory map
thread safety
```

---

## Kafka

```text
Kafka broker = process
Kafka consumer app = process
Consumer threads = process records concurrently
```

---

## Docker

```text
Docker container usually runs one main process
```

Example:

```text
Container 1 -> order-service process
Container 2 -> payment-service process
Container 3 -> redis process
```

---

## Kubernetes

```text
Pod runs container
Container runs process
Process has threads
```

Layer:

```text
Kubernetes Cluster
    ↓
Node
    ↓
Pod
    ↓
Container
    ↓
Process
    ↓
Threads
```

---

# 13. Why Process Is Safer Than Thread

If one process crashes, other processes may continue.

Example:

```text
payment-service crashes
order-service can still run
```

But if one thread corrupts shared memory inside the same process, the whole process may become unstable.

Example:

```text
one bad thread modifies shared cache incorrectly
all request threads may see wrong data
```

---

# 14. Why Thread Is Faster Than Process

Threads are lighter because they share memory and resources.

Creating a process is heavier because OS must allocate separate memory and resources.

```text
Process creation -> expensive
Thread creation  -> cheaper
Thread pool      -> even better
```

That is why backend servers use thread pools.

---

# 15. Why Thread Pool Exists

Creating a new thread for every request is expensive.

Bad model:

```text
Request 1 -> create thread
Request 2 -> create thread
Request 3 -> create thread
...
```

Better model:

```text
Create fixed worker threads once
Reuse them for many requests
```

This is thread pool.

```text
ThreadPool
├── worker-1
├── worker-2
├── worker-3
└── worker-4
```

Requests wait in a queue.

```text
Request Queue -> Worker Threads -> Execute Task
```

---

# 16. Mini System Design Connection

Process vs Thread connects to many minis:

```text
MiniConcurrency
    ↓
MiniThreadPool
    ↓
MiniRedis
    ↓
MiniKafka
    ↓
MiniGateway
    ↓
MiniSpringBootCloud
    ↓
MiniDocker
    ↓
MiniKubernetes
```

Why?

Because every production service is:

```text
process + threads + network + memory + queues
```

---

# 17. Common Interview Question

## Question

What is the difference between process and thread?

## Strong Answer

A process is an independent running program with its own memory space. A thread is a lightweight execution unit inside a process. Threads inside the same process share heap memory, file descriptors, and other process resources, but each thread has its own call stack. Processes provide stronger isolation but are heavier. Threads are faster to create and communicate more easily, but shared memory makes concurrency bugs like race conditions and deadlocks possible.

---

# 18. One-Line Memory Hook

```text
Process = isolated application
Thread  = shared-memory worker inside application
```

---

# 19. What Breaks Next?

Once multiple threads share memory, these problems appear:

```text
race condition
lost update
visibility issue
deadlock
starvation
thread contention
context switching overhead
```

That is why next topics are important:

```text
Thread lifecycle
Context switching
Race condition
Locks
Atomic variables
BlockingQueue
ThreadPool
```

---

# 20. Final Summary

```text
Process:
- independent running program
- own memory
- strong isolation
- expensive creation
- communicates using IPC/network

Thread:
- execution unit inside process
- shares memory with other threads
- cheaper creation
- fast communication
- dangerous without synchronization
```

Production mental model:

```text
Kubernetes Pod
    ↓
Docker Container
    ↓
Java Process
    ↓
Thread Pool
    ↓
Request Handler Thread
    ↓
Business Logic
```

If you understand this, concurrency becomes much easier.

