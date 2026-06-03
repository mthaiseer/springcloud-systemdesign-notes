# 013_Producer_Consumer_Pattern.md

# MiniConcurrency — 013 Producer Consumer Pattern

## 0. Why This File Exists

Producer-Consumer is one of the most important concurrency patterns.

It appears inside:

```text
Thread pools
Kafka consumers
Message queues
Schedulers
Logging systems
Payment processing
Notification systems
Background workers
Async job systems
```

This file teaches:

```text
Producer-consumer mental model
Unbounded queue
Bounded queue
Backpressure
wait/notify implementation
BlockingQueue implementation
ThreadPoolExecutor mapping
Kafka-style mapping
Production tradeoffs
```

---

# 1. One-Line Definition

```text
Producer-Consumer pattern separates task creation from task processing using a shared queue.
```

Simple meaning:

```text
Producer adds work.
Consumer processes work.
Queue connects them.
```

---

# 2. Real Mental Model

Restaurant example:

```text
Chef prepares food
    ↓
places food on counter
    ↓
waiter picks food
    ↓
serves customer
```

Mapping:

```text
Chef    = Producer
Counter = Queue
Waiter  = Consumer
```

---

# 3. Backend Mental Model

```text
HTTP request thread
    ↓
adds task to queue
    ↓
worker thread processes task later
```

This avoids doing heavy work directly inside request thread.

---

# 4. Why Producer-Consumer Exists

Without queue:

```text
Producer and consumer tightly coupled.
```

Problem:

```text
If consumer is slow, producer gets stuck.
If producer is fast, system overloads.
```

With queue:

```text
Producer and consumer are decoupled.
```

---

# 5. Basic Flow

```text
Producer Thread
      ↓
Shared Queue
      ↓
Consumer Thread
```

Detailed:

```text
Producer creates item
Producer puts item into queue
Consumer waits for item
Consumer takes item
Consumer processes item
```

---

# 6. Core Components

```text
Producer
Queue
Consumer
Locking / synchronization
Wait strategy
Backpressure
```

---

# 7. Simple Producer-Consumer Diagram

```text
+-----------+       +---------+       +-----------+
| Producer  | --->  | Queue   | --->  | Consumer  |
+-----------+       +---------+       +-----------+
```

Multiple producers/consumers:

```text
Producer-1  \
Producer-2   ---> Queue ---> Consumer-1
Producer-3  /              Consumer-2
```

---

# 8. Why Queue Is Important

Queue acts as buffer.

If producer is faster:

```text
queue grows
```

If consumer is faster:

```text
consumer waits
```

Queue smooths speed differences.

---

# 9. Unbounded Queue

Unbounded queue means:

```text
queue can grow without fixed limit
```

Danger:

```text
memory can explode
OutOfMemoryError
high latency
```

Example:

```text
Producer creates 1M tasks/sec
Consumer processes 10K tasks/sec
Queue grows forever
```

---

# 10. Bounded Queue

Bounded queue means:

```text
queue has max capacity
```

Example:

```text
capacity = 100
```

If queue full:

```text
producer must wait
or fail
or drop task
or apply backpressure
```

This protects system.

---

# 11. Backpressure

Backpressure means:

```text
When consumers are slow, producers are slowed down.
```

Simple meaning:

```text
Do not accept unlimited work.
```

Backend example:

```text
Queue full
    ↓
API returns 429 / 503
or producer waits
```

---

# 12. Why Backpressure Matters

Without backpressure:

```text
requests pile up
memory grows
latency explodes
system crashes
```

With backpressure:

```text
system protects itself
```

This is production-grade design.

---

# 13. Manual wait/notify Implementation — Bounded Queue

```java
import java.util.LinkedList;
import java.util.Queue;

public class BoundedBuffer<T> {

    private final Queue<T> queue =
            new LinkedList<>();

    private final int capacity;

    private final Object lock =
            new Object();

    public BoundedBuffer(int capacity) {

        this.capacity = capacity;
    }

    public void put(T item)
            throws InterruptedException {

        synchronized (lock) {

            while (queue.size() == capacity) {

                lock.wait();
            }

            queue.add(item);

            lock.notifyAll();
        }
    }

    public T take()
            throws InterruptedException {

        synchronized (lock) {

            while (queue.isEmpty()) {

                lock.wait();
            }

            T item = queue.poll();

            lock.notifyAll();

            return item;
        }
    }
}
```

---

# 14. Why while Is Used

Correct:

```java
while (queue.isEmpty()) {
    lock.wait();
}
```

Not:

```java
if (queue.isEmpty()) {
    lock.wait();
}
```

Because:

```text
spurious wakeups can happen
another consumer may take item first
condition must be rechecked
```

---

# 15. Dry Run — Empty Queue

Initial:

```text
queue = []
capacity = 3
```

Consumer calls:

```java
take()
```

Flow:

```text
consumer enters synchronized(lock)
queue is empty
consumer calls wait()
consumer releases lock
consumer goes WAITING
```

Producer calls:

```java
put(10)
```

Flow:

```text
producer enters lock
adds 10
queue = [10]
notifyAll()
producer releases lock
```

Consumer wakes:

```text
reacquires lock
checks queue not empty
polls 10
returns 10
```

---

# 16. Dry Run — Full Queue

Initial:

```text
queue = [1, 2, 3]
capacity = 3
```

Producer calls:

```java
put(4)
```

Flow:

```text
producer enters lock
queue full
producer calls wait()
producer releases lock
producer waits
```

Consumer calls:

```java
take()
```

Flow:

```text
consumer removes 1
queue = [2, 3]
notifyAll()
```

Producer wakes:

```text
rechecks queue size
space available
adds 4
queue = [2, 3, 4]
```

---

# 17. Why notifyAll() Here?

Because there may be:

```text
many producers waiting for space
many consumers waiting for data
```

`notify()` may wake the wrong kind of thread.

`notifyAll()` is safer.

Tradeoff:

```text
more wakeups
more overhead
but safer correctness
```

---

# 18. Problem With Manual wait/notify

Manual implementation is error-prone:

```text
forgot while
forgot notifyAll
called wait outside synchronized
used wrong lock
missed notification
deadlock
```

So in production, prefer:

```text
BlockingQueue
```

---

# 19. BlockingQueue Version

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueProducerConsumer {

    public static void main(String[] args) {

        BlockingQueue<Integer> queue =
                new ArrayBlockingQueue<>(3);

        Thread producer = new Thread(() -> {

            int value = 1;

            while (true) {

                try {

                    queue.put(value);

                    System.out.println(
                            "Produced: " + value
                    );

                    value++;

                    Thread.sleep(500);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        Thread consumer = new Thread(() -> {

            while (true) {

                try {

                    int item = queue.take();

                    System.out.println(
                            "Consumed: " + item
                    );

                    Thread.sleep(1000);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        producer.start();
        consumer.start();
    }
}
```

---

# 20. BlockingQueue Dry Run

Capacity:

```text
3
```

Producer:

```text
put(1)
put(2)
put(3)
```

Queue full:

```text
[1, 2, 3]
```

Producer tries:

```text
put(4)
```

It blocks.

Consumer:

```text
take() → removes 1
```

Now space exists.

Producer continues:

```text
put(4)
```

---

# 21. BlockingQueue Methods

| Method | If Full | If Empty |
|---|---|---|
| put() | waits | — |
| take() | — | waits |
| offer() | returns false | — |
| poll() | — | returns null |
| offer(timeout) | waits limited time | — |
| poll(timeout) | — | waits limited time |

---

# 22. ArrayBlockingQueue vs LinkedBlockingQueue

## ArrayBlockingQueue

```text
fixed capacity
array based
good for backpressure
```

## LinkedBlockingQueue

```text
linked nodes
can be bounded or unbounded
may grow large if unbounded
```

Production rule:

```text
Prefer bounded queues.
```

---

# 23. ThreadPoolExecutor Uses Producer-Consumer

ThreadPoolExecutor mental model:

```text
submit task
    ↓
task queue
    ↓
worker threads consume task
```

Mapping:

```text
Caller thread = producer
Work queue = queue
Worker threads = consumers
```

---

# 24. ThreadPoolExecutor Flow

```text
Client submits task
      ↓
Executor checks worker availability
      ↓
If worker free, task runs
      ↓
Else task enters queue
      ↓
Worker takes task from queue
      ↓
Executes task
```

---

# 25. Backend Example — Email Notification Service

```text
Order placed
    ↓
producer adds email task
    ↓
queue stores task
    ↓
email worker sends email
```

Benefit:

```text
Order API returns fast.
Email sent asynchronously.
```

---

# 26. Backend Example — Logging System

```text
Application thread produces log event
    ↓
log queue
    ↓
background log writer consumes
```

Benefit:

```text
request thread not blocked by disk/network logging
```

---

# 27. Backend Example — Payment Events

```text
Payment completed
    ↓
producer creates settlement event
    ↓
queue/Kafka
    ↓
consumer processes settlement
```

This is producer-consumer at distributed scale.

---

# 28. Kafka Is Distributed Producer-Consumer

Kafka model:

```text
Producer writes message
    ↓
Kafka topic partition
    ↓
Consumer reads message
```

Mapping:

```text
Producer = app writing events
Queue = Kafka topic partition
Consumer = service processing events
```

---

# 29. Redis Queue Example

Redis can be used as queue:

```text
LPUSH task
BRPOP task
```

Producer:

```text
LPUSH
```

Consumer:

```text
BRPOP waits until task exists
```

---

# 30. Backpressure Strategies

When queue is full:

```text
Block producer
Reject request
Drop low-priority task
Retry later
Return 429
Return 503
Scale consumers
Increase capacity carefully
```

---

# 31. Production Tradeoff — Queue Size

Small queue:

```text
fast backpressure
less memory
may reject too early
```

Large queue:

```text
absorbs bursts
higher memory
higher latency
can hide overload
```

Good systems choose queue size carefully.

---

# 32. Latency Problem With Large Queues

If queue has:

```text
100000 tasks
```

New task waits behind many old tasks.

Result:

```text
high latency
timeout
poor user experience
```

Queue length is not free.

---

# 33. Throughput Balancing

If producer rate is:

```text
1000 tasks/sec
```

and each consumer processes:

```text
100 tasks/sec
```

Need:

```text
10 consumers
```

Formula:

```text
required consumers = producer rate / consumer rate
```

Example:

```text
1000 / 100 = 10
```

---

# 34. Poison Pill Shutdown Pattern

Special item tells consumer to stop.

Example:

```java
queue.put(-1);
```

Consumer:

```java
if (item == -1) {
    break;
}
```

This is called:

```text
poison pill
```

---

# 35. Graceful Shutdown Example

```java
while (true) {

    int item = queue.take();

    if (item == -1) {

        break;
    }

    process(item);
}
```

Used for:

```text
worker shutdown
background jobs
controlled stop
```

---

# 36. Error Handling In Consumer

Consumer should handle exceptions.

Bad:

```java
process(item);
```

If exception kills consumer:

```text
queue stops processing
```

Better:

```java
try {
    process(item);
} catch (Exception e) {
    // log and continue / retry / dead-letter
}
```

---

# 37. Retry and Dead Letter Queue

Production systems often need:

```text
retry queue
dead letter queue
```

If task fails repeatedly:

```text
move to DLQ
investigate later
```

Kafka/RabbitMQ systems use this heavily.

---

# 38. Ordering

Single consumer:

```text
preserves queue order
```

Multiple consumers:

```text
higher throughput
but ordering may change
```

Tradeoff:

```text
ordering vs parallelism
```

Kafka solves this with:

```text
partition-level ordering
```

---

# 39. Duplicate Processing

Consumers may process same task twice if:

```text
retry happens
crash after processing before ack
network failure
```

Need:

```text
idempotency
deduplication
transactional processing
```

Important in payment systems.

---

# 40. Real Production Symptoms

Producer-consumer issues show as:

```text
queue size growing
consumer lag
high latency
memory pressure
task timeout
thread starvation
failed retries
DLQ growth
```

---

# 41. Monitoring Metrics

Track:

```text
queue size
producer rate
consumer rate
processing latency
failure count
retry count
DLQ count
worker utilization
```

These are core production metrics.

---

# 42. Interview Explanation

If interviewer asks:

```text
What is producer-consumer pattern?
```

Good answer:

```text
Producer-consumer is a concurrency pattern where producers create tasks
and place them into a shared queue, while consumers take tasks from the queue
and process them. It decouples task creation from processing and helps
control load using bounded queues and backpressure.
```

Strong backend addition:

```text
ThreadPoolExecutor, Kafka, logging systems, schedulers, and async job workers
are all producer-consumer systems.
```

---

# 43. Common Mistakes

## Mistake 1

```text
Using unbounded queue in production.
```

Can cause memory explosion.

---

## Mistake 2

```text
Ignoring backpressure.
```

System overloads silently.

---

## Mistake 3

```text
Consumer dies on exception.
```

Queue stops draining.

---

## Mistake 4

```text
Assuming multiple consumers preserve global ordering.
```

They usually do not.

---

## Mistake 5

```text
No idempotency.
```

Dangerous for retries/payments.

---

# 44. Mini Dry Run Summary

```text
Producer creates item
    ↓
puts item into bounded queue
    ↓
if queue full, producer waits/rejects
    ↓
consumer takes item
    ↓
processes item
    ↓
queue capacity frees
```

---

# 45. Visual Summary

```text
Producer
   ↓
Bounded Queue
   ↓
Consumer Workers
   ↓
Processing
```

With backpressure:

```text
Queue Full
   ↓
Slow producer / reject request
   ↓
Protect system
```

---

# 46. What To Remember

```text
Producer-consumer decouples task creation and task processing.

Queue is the buffer.

Bounded queue gives backpressure.

BlockingQueue is safer than manual wait/notify.

ThreadPoolExecutor is producer-consumer internally.

Kafka is distributed producer-consumer.

Production needs metrics, retries, DLQ, idempotency, and backpressure.
```

---

# 47. Next File

```text
014_BlockingQueue.md
```

Next you learn:

```text
BlockingQueue internals
put/take behavior
ArrayBlockingQueue
LinkedBlockingQueue
DelayQueue
PriorityBlockingQueue
real worker queue design
```
