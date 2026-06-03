# 002_Process_vs_Thread.md

# MiniConcurrency — 002 Process vs Thread

## 0. Why This File Exists

Before learning locks, race conditions, thread pools, async programming, Kafka consumers, Redis server threads, or Spring Boot request handling, you must clearly understand:

```text
What is a process?
What is a thread?
Why do backend systems use many threads?
Why is sharing memory dangerous?
```

This file builds the foundation for all later MiniConcurrency topics.

---

## 1. One-Line Definition

```text
Process = running program with its own memory.

Thread = smaller execution unit inside a process that shares the process memory.
```

Example:

```text
Chrome browser = process
Each tab / renderer / worker = separate process or thread depending on browser design

Java Spring Boot app = one process
Each HTTP request may be handled by a thread from a thread pool
```

---

## 2. Process Mental Model

A process is an independent running program.

```text
Program on disk
    ↓
Operating system starts it
    ↓
Process in memory
```

Example:

```text
java -jar app.jar
```

This starts one JVM process.

That process has:

```text
Code
Heap memory
Stack memory
Open files
Network sockets
Environment variables
Process ID
```

Diagram:

```text
Process: SpringBootApp

+-----------------------------------+
| Code                              |
| Heap                              |
| Threads                           |
| Open DB connections               |
| Open Redis connections            |
| HTTP server socket                |
+-----------------------------------+
```

---

## 3. Thread Mental Model

A thread is a path of execution inside a process.

One process can contain many threads.

```text
Process
 ├── Thread 1
 ├── Thread 2
 ├── Thread 3
 └── Thread 4
```

In Java backend:

```text
Spring Boot JVM Process
 ├── HTTP request thread 1
 ├── HTTP request thread 2
 ├── Kafka consumer thread
 ├── Scheduler thread
 ├── GC thread
 └── Background cleanup thread
```

---

## 4. Process vs Thread Table

| Concept | Process | Thread |
|---|---|---|
| Meaning | Running program | Execution path inside process |
| Memory | Own memory | Shares process memory |
| Creation cost | Heavy | Lighter |
| Communication | Harder | Easier |
| Failure isolation | Better | Worse |
| Example | JVM app, Redis server, Kafka broker | HTTP worker thread, cleanup thread |
| Shared heap | No | Yes |
| Crash impact | Usually one process | Can crash/corrupt process state |

---

## 5. Very Important Memory Difference

### Process Memory

Two processes do not normally share memory.

```text
Process A memory
Process B memory
```

They are isolated.

If Process A changes a variable, Process B does not see it.

```text
Process A:
count = 10

Process B:
count = 10
```

These are separate copies.

---

### Thread Memory

Threads inside the same process share heap memory.

```text
Process Heap:
count = 10

Thread A reads/writes count
Thread B reads/writes count
```

This is powerful but dangerous.

Why?

Because multiple threads can update the same data at the same time.

That creates:

```text
Race condition
Data corruption
Lost update
Deadlock
Visibility problem
```

---

## 6. Java Example: One Process, Multiple Threads

```java
public class ProcessThreadDemo {

    public static void main(String[] args) {

        System.out.println("Main method started");

        Thread t1 = new Thread(() -> {
            System.out.println("Thread 1 running");
        });

        Thread t2 = new Thread(() -> {
            System.out.println("Thread 2 running");
        });

        t1.start();
        t2.start();

        System.out.println("Main method finished");
    }
}
```

---

## 7. Step-by-Step Dry Run

Code:

```java
Thread t1 = new Thread(() -> {
    System.out.println("Thread 1 running");
});

Thread t2 = new Thread(() -> {
    System.out.println("Thread 2 running");
});

t1.start();
t2.start();
```

Dry run:

```text
Step 1:
JVM process starts.

Step 2:
main thread begins execution.

Step 3:
main thread creates Thread object t1.
Important: t1 is created but not running yet.

Step 4:
main thread creates Thread object t2.
Important: t2 is also not running yet.

Step 5:
main thread calls t1.start().
Now OS/JVM schedules t1 to run.

Step 6:
main thread calls t2.start().
Now OS/JVM schedules t2 to run.

Step 7:
Thread execution order is not guaranteed.
Possible output order can change.
```

Possible output 1:

```text
Main method started
Thread 1 running
Thread 2 running
Main method finished
```

Possible output 2:

```text
Main method started
Main method finished
Thread 2 running
Thread 1 running
```

Why?

Because thread scheduling is controlled by JVM + OS.

---

## 8. Important: start() vs run()

### Wrong way

```java
t1.run();
```

This does not start a new thread.

It only calls the method like a normal function.

### Correct way

```java
t1.start();
```

This starts a real new thread.

---

## 9. Java Example: start() vs run()

```java
public class StartVsRunDemo {

    public static void main(String[] args) {

        Thread t = new Thread(() -> {
            System.out.println("Running inside: " 
                    + Thread.currentThread().getName());
        });

        t.run();

        t.start();
    }
}
```

Expected idea:

```text
t.run()
→ runs on main thread

t.start()
→ runs on new thread
```

Possible output:

```text
Running inside: main
Running inside: Thread-0
```

---

## 10. Shared Memory Example

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

        System.out.println(counter);
    }
}
```

You may expect:

```text
2
```

But for bigger loops, this can break.

---

## 11. Why counter++ Is Not Safe

This line:

```java
counter++;
```

Looks like one operation.

But internally it is:

```text
1. read counter
2. add 1
3. write counter back
```

Dry run with two threads:

```text
counter = 0

Thread A reads counter = 0
Thread B reads counter = 0

Thread A adds 1 → 1
Thread B adds 1 → 1

Thread A writes 1
Thread B writes 1

Expected counter = 2
Actual counter   = 1
```

This is called:

```text
Lost update
Race condition
```

---

## 12. Backend Example: Spring Boot Request Threads

Imagine this endpoint:

```java
@RestController
class OrderController {

    private int requestCount = 0;

    @GetMapping("/orders")
    public String getOrders() {
        requestCount++;
        return "orders";
    }
}
```

Problem:

```text
Many users call /orders at the same time.
Each request runs on a different thread.
All threads share the same controller object.
requestCount++ is not thread-safe.
```

Flow:

```text
User A request
    ↓
Thread 1
    ↓
requestCount++

User B request
    ↓
Thread 2
    ↓
requestCount++
```

If both happen together, count may become wrong.

---

## 13. Safer Version Using AtomicInteger

```java
import java.util.concurrent.atomic.AtomicInteger;

@RestController
class OrderController {

    private final AtomicInteger requestCount = new AtomicInteger(0);

    @GetMapping("/orders")
    public String getOrders() {
        requestCount.incrementAndGet();
        return "orders";
    }
}
```

Why safer?

```text
AtomicInteger makes increment operation thread-safe.
```

---

## 14. Process Communication vs Thread Communication

### Process Communication

Processes need external mechanisms:

```text
HTTP
TCP socket
File
Pipe
Message queue
Shared memory
Kafka
Redis
Database
```

Example:

```text
order-service process
    ↓ HTTP/Kafka
payment-service process
```

---

### Thread Communication

Threads can communicate through shared memory:

```text
Shared variable
BlockingQueue
ConcurrentHashMap
AtomicInteger
wait/notify
Future
CompletableFuture
```

Example:

```text
Producer thread
    ↓ BlockingQueue
Consumer thread
```

---

## 15. Real-World Mini Mapping

### MiniRedis

```text
Redis server = process

Inside it:
- network event loop
- background persistence thread
- cleanup logic
- client handling
```

---

### MiniKafka

```text
Kafka broker = process

Inside it:
- network threads
- IO threads
- log flush threads
- replication threads
```

---

### MiniGateway

```text
Gateway = process

Inside it:
- request worker threads
- filter execution
- load balancer logic
- timeout/retry logic
```

---

### MiniSpringBootCloud

```text
Each microservice = separate JVM process

Inside each service:
- request threads
- scheduler threads
- async executor threads
- Kafka listener threads
```

---

## 16. Why Backend Engineers Must Know This

Because production bugs often look like:

```text
High CPU
Too many threads
Thread pool exhausted
Deadlock
Slow response
Race condition
Memory leak
Connection pool starvation
Request timeout
Kafka consumer lag
```

All of these require process/thread understanding.

---

## 17. Interview Explanation

If interviewer asks:

```text
What is the difference between process and thread?
```

Good answer:

```text
A process is an independent running program with its own memory space.
A thread is a lightweight execution unit inside a process.
Threads inside the same process share heap memory, so communication is easier,
but synchronization is needed to avoid race conditions.
Processes are more isolated but heavier to create and communicate between.
```

Strong backend addition:

```text
In Spring Boot, one deployed service usually runs as one JVM process.
HTTP requests are handled by worker threads.
So shared mutable state in singleton beans must be thread-safe.
```

---

## 18. Common Mistakes

### Mistake 1

```text
Thinking every request creates a new process.
```

Usually wrong.

Most Java backend servers use worker threads.

---

### Mistake 2

```text
Thinking counter++ is thread-safe.
```

Wrong.

It is read + modify + write.

---

### Mistake 3

```text
Calling thread.run() to start thread.
```

Wrong.

Use:

```java
thread.start();
```

---

### Mistake 4

```text
Putting mutable variables inside Spring singleton service without synchronization.
```

Dangerous.

---

## 19. Mini Dry Run Summary

```text
Java app starts
    ↓
JVM process created
    ↓
main thread starts
    ↓
main creates worker threads
    ↓
threads share heap memory
    ↓
if shared data is modified unsafely
    ↓
race condition happens
```

---

## 20. Real Production Mental Model

```text
Machine
 ├── Process: gateway-service
 │    ├── Thread: request-1
 │    ├── Thread: request-2
 │    └── Thread: metrics
 │
 ├── Process: order-service
 │    ├── Thread: request-1
 │    ├── Thread: kafka-listener
 │    └── Thread: scheduler
 │
 ├── Process: redis
 │    └── event loop / background work
 │
 └── Process: kafka
      ├── network threads
      ├── IO threads
      └── replication threads
```

This is the real backend world.

---

## 21. What To Remember

```text
Process = isolated running program.
Thread = execution path inside a process.
Processes do not easily share memory.
Threads share memory.
Shared memory needs safety.
Java backend apps are usually one JVM process with many threads.
```

---

## 22. Next File

```text
003_Thread_Lifecycle.md
```

Next you learn:

```text
NEW
RUNNABLE
BLOCKED
WAITING
TIMED_WAITING
TERMINATED
```

This is needed before locks, wait/notify, thread pools, and production debugging.
