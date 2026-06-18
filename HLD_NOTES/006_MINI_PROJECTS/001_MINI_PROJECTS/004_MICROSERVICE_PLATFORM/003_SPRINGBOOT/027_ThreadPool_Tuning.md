# 027_ThreadPool_Tuning — The Concurrency Waiting Room Model

## Core Mental Model

Do not imagine thread pool tuning as:

```text
More threads = faster application.
```

That is one of the most dangerous production misunderstandings.

The better mental model is:

> **A thread pool is a bounded waiting room for work. It controls how much work runs now, how much waits, and what gets rejected when the system is full.**

```text
Incoming Tasks
      |
      v
+----------------------------+
| Thread Pool                |
|----------------------------|
| Running workers            |
| Waiting queue              |
| Rejection policy           |
| Timeout/monitoring         |
+----------------------------+
      |
      v
CPU / DB / Network / External Services
```

This chapter teaches exactly one idea:

> **Thread pool tuning is not about maximizing thread count; it is about matching concurrency to the real bottleneck and applying backpressure before overload becomes collapse.**

If you remember only one sentence:

> **Threads are not free speed; they are workers competing for limited CPU, memory, database connections, and downstream capacity.**

---

## Why This Exists

A backend service receives many requests.

Each request may do work:

```text
parse JSON
validate input
call service
query database
call remote API
publish Kafka event
return response
```

If every task creates a new thread:

```text
Task 1 -> new thread
Task 2 -> new thread
Task 3 -> new thread
...
Task 10000 -> new thread
```

The JVM and operating system suffer:

```text
too many stacks in memory
too much context switching
CPU wasted scheduling threads
GC pressure
database pool exhaustion
remote service overload
timeouts
cascading failures
```

A thread pool exists to put a controlled gate in front of execution.

```text
Only N tasks run now.
Some tasks wait.
Some tasks are rejected if the system is already full.
```

That sounds harsh, but it protects the system.

A service that rejects early can recover.

A service that accepts infinite work usually dies slowly.

---

## Problem Statement

Imagine this Spring Boot async code:

```java
@Async
public void sendEmail(Order order) {
    emailClient.send(order);
}
```

During normal traffic:

```text
100 emails/minute
```

During a flash sale:

```text
100,000 emails/minute
```

If the executor has an unbounded queue:

```text
Tasks keep piling up.
Memory grows.
Email sends become hours late.
Application eventually crashes.
```

If the executor has too many threads:

```text
Thousands of threads run.
CPU context switching increases.
Downstream email provider throttles.
Timeouts multiply.
```

If the executor is too small:

```text
Queue grows.
Latency increases.
Tasks miss deadlines.
```

The core problem:

> **How do we choose thread count, queue size, timeout, and rejection behavior so work completes predictably without overwhelming the JVM or downstream systems?**

Thread pool tuning solves this through:

```text
1. Bound running concurrency.
2. Bound waiting queue.
3. Choose rejection/backpressure behavior.
4. Align pool size with bottleneck.
5. Monitor active threads, queue depth, latency, and rejection.
```

---

## Real World Analogy

Imagine a bank branch.

```text
Customers enter.
Only 5 counters are open.
A waiting line holds 20 people.
If the line is full, new customers are told to come later.
```

Mapping:

```text
Bank branch                  Thread pool
-----------                  -----------
Customer                     Task/request
Counter staff                Worker threads
Waiting line                 Queue
Line capacity                Queue size
Come later                   Rejection policy
Slow cashier                 Slow task/downstream
More counters                More threads
Branch capacity              CPU/DB/downstream capacity
```

Adding more counters helps only until:

```text
cash vault is bottleneck
computer system is slow
manager approvals are slow
building is full
```

In software:

```text
more threads help only until CPU, DB, memory, or downstream service becomes the bottleneck.
```

---

## The One Mental Model

Every thread pool answers three questions:

```text
1. How many tasks can run now?
2. How many tasks can wait?
3. What happens when both are full?
```

ASCII:

```text
                Incoming Tasks
                      |
                      v
        +-----------------------------+
        | Thread Pool                 |
        |-----------------------------|
        | core threads: run work      |
        | max threads: burst capacity |
        | queue: waiting room         |
        | rejection: overload policy  |
        +-----------------------------+
                      |
                      v
        Limited real resources:
        CPU, DB pool, network, locks, downstream APIs
```

Tuning means:

```text
running work <= real capacity
waiting work <= memory/deadline capacity
overflow handled deliberately
```

---

## Core Concepts

## Worker Thread

A worker thread executes tasks.

```text
Thread-1 runs task A
Thread-2 runs task B
Thread-3 runs task C
```

Threads consume memory and scheduling overhead.

Each Java platform thread has a stack.
Thousands of blocked threads can consume significant memory and scheduler time.

## Task

A task is a unit of work submitted to the pool.

Examples:

```text
send email
process uploaded file
call external API
run scheduled job
publish notification
handle async event
```

## Queue

The queue stores tasks waiting for a worker.

```text
workers busy
new task arrives
task waits in queue
```

Queues are useful, but dangerous if unbounded.

```text
Unbounded queue = hidden overload storage
```

A queue should be sized according to:

```text
how long tasks can wait
how much memory they consume
how quickly the system should reject overload
```

## Core Pool Size

The normal number of worker threads kept ready.

```java
executor.setCorePoolSize(10);
```

Mental model:

```text
normal counter staff
```

## Max Pool Size

The maximum number of worker threads allowed during pressure.

```java
executor.setMaxPoolSize(30);
```

Mental model:

```text
extra counters opened during rush
```

Important:

> With many queue types, max pool size is only used after the queue fills.

## Rejection Policy

What happens when:

```text
workers are maxed
queue is full
new task arrives
```

Common policies:

```text
AbortPolicy
  throw exception

CallerRunsPolicy
  caller thread executes task, slowing producer

DiscardPolicy
  silently drop task

DiscardOldestPolicy
  drop oldest queued task and enqueue new one
```

In production, silent discard is dangerous unless explicitly intended.

## Backpressure

Backpressure means telling upstream:

```text
I cannot accept more work at this speed.
```

In a thread pool, backpressure can appear as:

```text
rejection
caller runs
HTTP 429/503
queue full
rate limiting
bounded Kafka consumer concurrency
```

Backpressure is good.
It prevents collapse.

---

## Internal Architecture

Java `ThreadPoolExecutor` roughly works like this:

```text
submit(task)
   |
   v
Are running threads < corePoolSize?
   |
   +-- yes -> create/use worker thread
   |
   +-- no
        |
        v
Can queue accept task?
   |
   +-- yes -> enqueue task
   |
   +-- no
        |
        v
Are running threads < maxPoolSize?
   |
   +-- yes -> create extra worker
   |
   +-- no  -> reject task
```

ASCII:

```text
                  submit task
                      |
                      v
        +-----------------------------+
        | running < core?             |
        +-------------+---------------+
              yes     | no
              v       v
        run in worker +----------------------+
                      | queue has space?      |
                      +----------+-----------+
                           yes   | no
                           v     v
                         enqueue +---------------------+
                                 | running < max?       |
                                 +----------+----------+
                                      yes   | no
                                      v     v
                              create worker reject
```

This order matters.

If queue is huge, the pool may never grow beyond core size.

---

## Internal Working

Suppose:

```text
corePoolSize = 5
maxPoolSize = 10
queueCapacity = 20
```

Tasks arrive.

```text
Task 1-5:
  running threads below core
  execute immediately

Task 6-25:
  core threads busy
  queue has space
  tasks wait in queue

Task 26-30:
  queue full
  running below max
  create extra threads

Task 31:
  running == max
  queue full
  reject
```

So the actual order is:

```text
core first
then queue
then max
then reject
```

Many developers expect:

```text
core first
then max
then queue
```

That is not how a typical `ThreadPoolExecutor` with bounded queue behaves.

This misunderstanding causes bad tuning.

---

## Rich ASCII Diagram — Thread Pool as Waiting Room

```text
Incoming tasks
 T1 T2 T3 T4 T5 T6 T7 T8 T9
        |
        v
+------------------------------------------------------+
| ThreadPoolExecutor                                   |
|------------------------------------------------------|
| Running workers                                      |
| +---------+ +---------+ +---------+                  |
| | Thread1 | | Thread2 | | Thread3 |                  |
| |  T1     | |  T2     | |  T3     |                  |
| +---------+ +---------+ +---------+                  |
|                                                      |
| Waiting queue                                        |
| +----+----+----+----+                                |
| | T4 | T5 | T6 | T7 |                                |
| +----+----+----+----+                                |
|                                                      |
| Rejection gate                                       |
| If workers full and queue full -> reject/backpressure|
+------------------------------------------------------+
```

---

## Rich ASCII Diagram — Hidden Bottleneck

```text
Thread Pool
  max threads = 100
        |
        v
Database Pool
  max connections = 20
        |
        v
Database
  safe active queries = 20
```

If 100 worker threads all need DB:

```text
20 get DB connections
80 wait for DB connections
```

Adding more app threads does not create more database capacity.

It creates more waiting.

Senior mental model:

```text
Thread pool size must respect downstream pool size.
```

---

## Step-by-Step Dry Run — Healthy Flow

Configuration:

```text
corePoolSize = 10
maxPoolSize = 20
queueCapacity = 100
```

Traffic:

```text
15 tasks arrive slowly
each task completes in 100 ms
```

Flow:

```text
1. First 10 tasks run immediately.
2. Task 11 waits briefly in queue.
3. Worker finishes task 1.
4. Worker takes task 11 from queue.
5. Queue stays small.
6. No rejection.
7. Latency stable.
```

Metrics:

```text
active threads: 8-10
queue size: 0-5
rejections: 0
task latency: stable
```

Healthy.

---

## Step-by-Step Dry Run — Queue Explosion

Configuration:

```text
corePoolSize = 10
maxPoolSize = 20
queueCapacity = 100000
```

Traffic spike:

```text
5000 tasks arrive quickly
downstream API slows from 100 ms to 5 seconds
```

Flow:

```text
1. 10 core threads run tasks.
2. Remaining tasks enter huge queue.
3. Queue grows to thousands.
4. Tasks wait minutes before execution.
5. Memory usage rises.
6. User-facing operations time out.
7. App still accepts more work.
8. Collapse becomes delayed and confusing.
```

Problem:

```text
The queue hid overload instead of applying backpressure.
```

Fix:

```text
Use bounded queue.
Reject or slow callers.
Add rate limiting.
Fix downstream slowness.
Separate pools by workload.
```

---

## Step-by-Step Dry Run — Too Many Threads

Configuration:

```text
corePoolSize = 100
maxPoolSize = 500
queueCapacity = 100
```

Machine:

```text
4 CPU cores
DB pool size = 20
```

Flow:

```text
1. Hundreds of threads run.
2. Many block waiting for DB connections.
3. CPU spends time context-switching.
4. Memory usage increases due to thread stacks.
5. DB receives too much concurrent work.
6. Latency gets worse, not better.
```

Problem:

```text
Thread count was tuned without considering bottlenecks.
```

Lesson:

> **If the bottleneck is DB or network, more threads can increase waiting and failure.**

---

## Step-by-Step Dry Run — Rejection Saves the System

Configuration:

```text
corePoolSize = 20
maxPoolSize = 20
queueCapacity = 100
rejectionPolicy = AbortPolicy
```

Traffic:

```text
1000 tasks arrive suddenly
```

Flow:

```text
1. 20 tasks run.
2. 100 tasks wait.
3. Task 121 arrives.
4. Pool rejects it.
5. Application returns 429/503 or records failure.
6. JVM memory stays stable.
7. Pool continues serving accepted work.
```

This is not failure of the pool.
This is the pool protecting the system.

---

## Java Code Example — Raw ThreadPoolExecutor

```java
ExecutorService executor = new ThreadPoolExecutor(
        10,                         // corePoolSize
        20,                         // maximumPoolSize
        60, TimeUnit.SECONDS,       // keepAliveTime
        new ArrayBlockingQueue<>(100),
        new ThreadPoolExecutor.AbortPolicy()
);
```

Execution model:

```text
up to 10 tasks run immediately
next 100 wait in queue
if queue full, grow up to 20 threads
if still full, reject
```

Task:

```java
executor.submit(() -> {
    processOrder();
});
```

If rejected:

```java
try {
    executor.submit(() -> processOrder());
} catch (RejectedExecutionException ex) {
    // return 429/503, retry later, or send to durable queue
}
```

---

## Spring Boot Code Example — Async Executor

Enable async:

```java
@Configuration
@EnableAsync
public class AsyncConfig {
}
```

Configure executor:

```java
@Configuration
public class ExecutorConfig {

    @Bean(name = "emailExecutor")
    public ThreadPoolTaskExecutor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("email-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}
```

Use executor:

```java
@Service
public class EmailService {

    @Async("emailExecutor")
    public void sendOrderEmail(Long orderId) {
        // call email provider
    }
}
```

Internal execution:

```text
OrderService calls emailService.sendOrderEmail()
Spring proxy intercepts @Async
Task submitted to emailExecutor
Caller returns quickly
Worker thread later sends email
```

Important:

```text
@Async works through Spring proxy.
Self-invocation can bypass it.
```

Bad:

```java
@Service
public class EmailService {

    public void createAndSend() {
        sendOrderEmail(1L); // self-invocation: @Async may be bypassed
    }

    @Async("emailExecutor")
    public void sendOrderEmail(Long orderId) {
    }
}
```

---

## Spring Boot Example — Web Server Thread Pool

For embedded Tomcat:

```properties
server.tomcat.threads.max=200
server.tomcat.threads.min-spare=20
server.tomcat.accept-count=100
```

Mental model:

```text
Tomcat threads handle HTTP requests.
If all request threads are busy, requests wait in accept queue.
If accept queue fills, clients may be refused/time out.
```

But Tomcat threads are different from async executor threads.

```text
HTTP request thread pool:
  handles incoming requests

@Async executor:
  handles background tasks

HikariCP pool:
  handles DB connections
```

These pools interact.

Bad configuration:

```text
Tomcat max threads = 500
Hikari max pool = 20
```

If all requests need DB:

```text
500 request threads compete for 20 DB connections.
480 threads wait.
Latency explodes.
```

Better:

```text
Limit request concurrency.
Optimize DB queries.
Align thread pool and DB pool capacity.
Use backpressure.
```

---

## Sequence Diagram — Request + DB Bottleneck

```text
Client
  |
  v
Tomcat Thread Pool
  |
  | request thread starts
  v
Controller
  |
  v
Service
  |
  | needs DB
  v
HikariCP
  |
  | only 20 connections
  v
Database
```

If Tomcat has 200 threads and Hikari has 20 connections:

```text
20 requests use DB
180 requests may wait for DB
```

This is why tuning one pool alone is not enough.

---

## Production Scale Example

Imagine an order service:

```text
Tomcat max threads: 200
Hikari max pool: 30
Kafka consumer concurrency: 20
Email async executor: 50
CPU cores: 4
```

Each order request:

```text
1. uses Tomcat thread
2. opens transaction
3. borrows DB connection
4. publishes Kafka event
5. submits email task
```

Under heavy traffic:

```text
Tomcat threads increase
DB pool saturates
transactions wait
email executor queue grows
Kafka publish latency increases
request latency rises
```

Senior approach:

```text
Map all pools.
Find bottleneck.
Tune concurrency by downstream capacity.
Add bounded queues.
Reject early.
Monitor each pool.
```

Pool map:

```text
HTTP Pool
  |
  v
Service Logic
  |
  +--> Hikari DB Pool
  |
  +--> Kafka Producer
  |
  +--> Async Email Pool
```

Every pool is a gate.
Every queue is a latency buffer.
Every unbounded queue is a potential memory bomb.

---

## Production Failure Story

A team had slow checkout during sale traffic.

They increased Tomcat threads:

```properties
server.tomcat.threads.max=800
```

For a short time, throughput increased.
Then everything got worse.

Symptoms:

```text
p99 latency > 20s
Hikari connection timeouts
Postgres CPU 100%
Kafka publish timeout
JVM memory high
GC pauses
```

Root cause:

```text
More request threads allowed more concurrent work into the service.
But Hikari pool was 40.
Database could safely handle around 50 active queries.
800 request threads mostly waited or overloaded downstream systems.
```

The fix was not more threads.

Fix:

```text
Tomcat max threads reduced.
Hikari pool tuned by DB capacity.
Slow queries optimized.
N+1 query fixed.
Async email moved to durable queue.
Rate limiting added for checkout.
Timeouts configured.
Backpressure returned 429/503 during overload.
```

Lesson:

> **Increasing thread count can turn a controlled bottleneck into a system-wide failure.**

---

## Debugging Mindset

When thread pool issues happen, ask:

```text
1. Which pool is saturated?
2. Are threads running CPU work or blocked waiting?
3. Is queue growing?
4. Are tasks being rejected?
5. What downstream resource are tasks waiting for?
6. Are there unbounded queues?
7. Are timeouts configured?
8. Are thread dumps showing blocked states?
9. Is CPU actually saturated?
10. Is DB/API/Kafka the real bottleneck?
```

### Symptom Map

```text
High CPU, low blocking
  -> CPU-bound workload
  -> too many threads can hurt

Low CPU, many blocked threads
  -> waiting on DB/network/locks
  -> find downstream bottleneck

Queue size growing
  -> arrival rate > completion rate
  -> increase capacity or reject/backpressure

Rejections increasing
  -> system is protecting itself
  -> tune capacity or upstream rate

Hikari timeout with many Tomcat threads
  -> request threads waiting for DB pool

Long p99 but low average
  -> queueing delay or lock contention

OutOfMemoryError
  -> unbounded queue or too many threads/tasks
```

### Thread Dump Clues

Look for states:

```text
RUNNABLE
  doing CPU work or native IO

WAITING/TIMED_WAITING
  waiting for queue, sleep, lock, future

BLOCKED
  waiting for monitor lock

parking to get DB connection
  Hikari pool pressure

socketRead
  waiting on remote service/database
```

Thread dump tells where threads are stuck.

---

## Common Misconceptions

## Misconception 1 — “More threads always means more throughput”

No.

Throughput improves only if the bottleneck can handle more concurrency.

If CPU or DB is saturated, more threads add overhead.

## Misconception 2 — “Large queue is safe”

A large queue hides overload.

It increases latency and memory usage.
A bounded queue with rejection is safer.

## Misconception 3 — “Rejected tasks mean bad configuration”

Not always.

Rejection can be correct backpressure.
The question is whether rejection is handled intentionally.

## Misconception 4 — “Thread pool tuning is independent”

No.

Tomcat pool, async pool, Hikari pool, Kafka consumer concurrency, and downstream services interact.

## Misconception 5 — “CPU-bound and IO-bound workloads use same sizing”

No.

CPU-bound work usually needs threads near CPU cores.

IO-bound work can use more threads because threads wait on IO, but still must respect downstream capacity.

## Misconception 6 — “Unbounded executors are convenient”

They are convenient until production traffic spike.

Unbounded thread creation or unbounded queues can crash the JVM.

---

## Performance Considerations

Use this basic thinking:

```text
Concurrency needed ≈ throughput × latency
```

Little's Law style:

```text
L = λ × W

L = average concurrency
λ = arrival rate
W = average time in system
```

Example:

```text
100 requests/sec
each DB task holds worker for 200ms = 0.2s

needed active workers ≈ 100 × 0.2 = 20
```

If latency becomes 1 second:

```text
100 × 1 = 100 workers
```

So when downstream slows, required concurrency explodes.

You cannot tune thread pools without latency.

Important distinction:

```text
CPU-bound:
  threads ≈ cores or cores + small number

IO-bound:
  threads can be higher
  but limited by DB/API/connection pools
```

---

## Scalability Considerations

At scale, thread pool tuning becomes admission control.

```text
Can this service safely accept more work?
If yes, run or queue.
If no, reject/backpressure.
```

For microservices:

```text
Service A thread pool overload
  -> calls Service B slower
  -> B queues grow
  -> C waits on B
  -> cascading failure
```

Prevention:

```text
bounded pools
bounded queues
timeouts
bulkheads
rate limits
circuit breakers
separate pools per workload
observability
```

Separate pools matter.

Bad:

```text
one executor for emails, payments, reports, notifications
```

A report spike can block payments.

Better:

```text
paymentExecutor
emailExecutor
reportExecutor
notificationExecutor
```

That is bulkheading.

---

## Failure Investigation Playbook

## Step 1 — Identify the saturated pool

Check metrics:

```text
active threads
pool size
queue size
completed task count
rejection count
task duration
```

## Step 2 — Check whether tasks are CPU-bound or blocked

Use:

```text
CPU metrics
thread dumps
async profiler
application logs
DB metrics
remote API latency
```

## Step 3 — Check queue behavior

Ask:

```text
Is queue bounded?
How large is it?
How long do tasks wait?
Are tasks still useful after waiting?
```

Some tasks expire.

Example:

```text
send OTP after 10 minutes = useless
```

## Step 4 — Check downstream pools

```text
Hikari active/pending
Kafka producer latency
Redis latency
HTTP client connection pool
external API errors
```

## Step 5 — Apply the smallest safe fix

Possible fixes:

```text
reduce thread count
increase thread count carefully
reduce queue size
add rejection handling
optimize slow task
split executor
add rate limit
move work to durable queue
increase downstream capacity
```

Do not blindly increase max threads.

---

## Interview Q&A

### Q1. What is thread pool tuning?

Strong answer:

> Thread pool tuning is choosing worker count, queue size, timeout, and rejection behavior so the service runs the right amount of concurrent work without overloading CPU, memory, database connections, or downstream services.

### Q2. Why is a bounded queue important?

Strong answer:

> A bounded queue prevents unlimited memory growth and hidden latency during overload. Once the queue is full, the system can apply backpressure through rejection or caller slowing instead of accepting work it cannot process in time.

### Q3. Why can increasing threads reduce performance?

Strong answer:

> More threads increase memory usage and context switching. If the bottleneck is CPU, database, locks, or a remote service, extra threads mostly wait and add overhead rather than increasing throughput.

### Q4. How do you size a pool?

Strong answer:

> I start from workload type and bottleneck. For CPU-bound work, keep threads near CPU cores. For IO-bound work, estimate concurrency using throughput × latency, then cap it by downstream capacity such as DB pool size, remote API limits, and memory. Then validate with metrics and load tests.

### Q5. What happens when a ThreadPoolExecutor queue is full?

Strong answer:

> If running threads are below maxPoolSize, it may create more workers. If already at maxPoolSize, it applies the rejection policy, such as throwing `RejectedExecutionException` or running the task in the caller thread.

### Q6. Why should Tomcat threads and Hikari pool be tuned together?

Strong answer:

> If Tomcat allows hundreds of concurrent request threads but Hikari only has a small DB pool, many request threads may block waiting for database connections. This increases latency and memory pressure. The request concurrency must respect database capacity.

### Q7. What metrics do you monitor?

Strong answer:

> Active threads, pool size, queue depth, task completion rate, task duration, rejection count, CPU usage, thread states, and downstream metrics like DB active connections, Hikari pending threads, and remote API latency.

---

## Production Checklist

```text
Pool Design
[ ] Is the pool bounded?
[ ] Is queue capacity bounded?
[ ] Is rejection policy intentional?
[ ] Are thread names meaningful?
[ ] Are separate workloads separated into different pools?

Sizing
[ ] Is workload CPU-bound or IO-bound?
[ ] Is pool size aligned with CPU cores?
[ ] Is pool size aligned with DB/API capacity?
[ ] Is queue size aligned with task deadline?
[ ] Is Little's Law estimate considered?

Spring Boot
[ ] Are @Async executors explicitly configured?
[ ] Is self-invocation avoided?
[ ] Is Tomcat max threads intentional?
[ ] Is Hikari pool aligned with request concurrency?

Observability
[ ] Active threads monitored
[ ] Queue depth monitored
[ ] Rejections monitored
[ ] Task duration monitored
[ ] Thread dumps available
[ ] Downstream latency monitored

Failure Safety
[ ] Timeouts configured
[ ] Rate limiting considered
[ ] Circuit breaker considered
[ ] Bulkhead/separate pools used
[ ] Overload returns controlled error
```

---

## One-Page Cheat Sheet

```text
Thread Pool Tuning
==================

Core Idea
---------
A thread pool is a bounded waiting room for work.

It controls:
  running tasks
  queued tasks
  rejected tasks

Thread
  worker that executes task

Queue
  waiting room for tasks

Core Pool Size
  normal worker count

Max Pool Size
  burst worker limit

Queue Capacity
  how much work can wait

Rejection Policy
  what happens when full

Backpressure
  refusing/slowing work before collapse

Golden Rule
-----------
More threads are useful only if the bottleneck can handle more concurrency.

Sizing Clue
-----------
concurrency ≈ throughput × latency

CPU-bound
  threads near CPU cores

IO-bound
  more threads possible,
  but capped by downstream capacity

Debug Rules
-----------
Queue growing?       arrival > completion
Rejections rising?   overload/backpressure
CPU high?            CPU-bound or too many threads
CPU low, threads wait? downstream bottleneck
Hikari timeout?      DB pool pressure
```

---

## Last-Minute Interview Revision

Do not say:

```text
I increase thread count to improve performance.
```

Say:

```text
I tune thread pools by matching concurrency to the bottleneck. I bound the queue, set intentional rejection/backpressure, monitor active threads and queue depth, and align request/async concurrency with downstream resources like DB connection pools and remote service limits.
```

Senior version:

```text
Thread pools are admission-control boundaries. I use them to prevent overload from spreading: bounded workers, bounded queues, timeouts, bulkheads, and observable rejection instead of unlimited waiting and hidden latency.
```

---

## One Picture To Remember

```text
                  INCOMING WORK
                       |
                       v
        +--------------------------------+
        | THREAD POOL WAITING ROOM      |
        |--------------------------------|
        | Running workers                |
        | [T1] [T2] [T3]                 |
        |                                |
        | Waiting queue                  |
        | [job] [job] [job]              |
        |                                |
        | Full?                          |
        | -> reject / caller runs / 503  |
        +--------------------------------+
                       |
                       v
          REAL BOTTLENECK RESOURCES
          CPU / DB / API / locks / IO
```

Final retention sentence:

> **Thread pool tuning is controlled admission: run only what the system can handle, queue only what can still be useful, and reject before overload becomes collapse.**
