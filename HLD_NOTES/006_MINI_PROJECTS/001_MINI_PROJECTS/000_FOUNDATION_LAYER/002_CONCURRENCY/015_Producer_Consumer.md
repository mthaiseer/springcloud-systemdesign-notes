# 015_Producer_Consumer.md

# MiniConcurrency — 015 Producer Consumer

## 0. Why This File Exists

You already learned the producer-consumer pattern and BlockingQueue basics.

This file goes one level deeper into a practical backend-style implementation:

```text
multiple producers
multiple consumers
bounded queue
worker shutdown
poison pill
error handling
retry logic
metrics
backpressure
```

This is the pattern behind:

```text
ThreadPoolExecutor
Kafka consumers
notification workers
email workers
payment event processors
log writers
background job systems
```

---

# 1. One-Line Definition

```text
Producer-consumer is a concurrency model where producers create work
and consumers process work through a shared queue.
```

Simple version:

```text
Producer → Queue → Consumer
```

---

# 2. Why This Pattern Is Important

In backend systems, you often do not want the request thread to do heavy work.

Example:

```text
User places order
    ↓
API saves order
    ↓
email task added to queue
    ↓
background worker sends email
```

Benefit:

```text
API response is fast.
Heavy work happens asynchronously.
```

---

# 3. Core Components

```text
Task
Producer
BlockingQueue
Consumer Worker
Error Handler
Retry Strategy
Shutdown Strategy
Metrics
```

---

# 4. Basic Architecture

```text
Producer-1 \
Producer-2  ---> BlockingQueue ---> Consumer-1
Producer-3 /                      Consumer-2
                                  Consumer-3
```

---

# 5. Backend Mental Model

```text
HTTP Request Thread
      ↓
creates background task
      ↓
puts task into bounded queue
      ↓
worker thread takes task
      ↓
processes task
```

---

# 6. Task Model

```java
public class Task {

    private final String id;
    private final String payload;
    private final int retryCount;

    public Task(String id, String payload, int retryCount) {
        this.id = id;
        this.payload = payload;
        this.retryCount = retryCount;
    }

    public String getId() {
        return id;
    }

    public String getPayload() {
        return payload;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public Task incrementRetry() {
        return new Task(id, payload, retryCount + 1);
    }
}
```

Why immutable?

```text
Safer across threads.
No shared mutable modification.
```

---

# 7. Simple Worker Service

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class WorkerService {

    private final BlockingQueue<Task> queue =
            new ArrayBlockingQueue<>(100);

    public boolean submit(Task task) {

        return queue.offer(task);
    }

    public void startWorker() {

        Thread worker = new Thread(() -> {

            while (true) {

                try {

                    Task task = queue.take();

                    process(task);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        worker.start();
    }

    private void process(Task task) {

        System.out.println(
                "Processing task: " + task.getId()
        );
    }
}
```

---

# 8. Dry Run — Simple Worker

Initial:

```text
queue = []
worker waiting on take()
```

Producer:

```text
submit(Task-1)
```

Queue:

```text
[Task-1]
```

Worker:

```text
wakes up
takes Task-1
processes Task-1
waits again
```

---

# 9. Why offer() Instead Of put() For API Path

```java
queue.offer(task);
```

returns false if full.

This is useful because:

```text
request thread should not block forever.
```

If queue is full:

```text
return 429 Too Many Requests
or 503 Service Unavailable
```

---

# 10. Backpressure Example

```java
public boolean submit(Task task) {

    boolean accepted = queue.offer(task);

    if (!accepted) {

        System.out.println("Queue full. Rejecting task.");
    }

    return accepted;
}
```

Flow:

```text
Queue full
    ↓
submit returns false
    ↓
caller rejects request
    ↓
system protected
```

---

# 11. Multiple Consumers

```java
public void startWorkers(int workerCount) {

    for (int i = 0; i < workerCount; i++) {

        Thread worker = new Thread(() -> {

            while (true) {

                try {

                    Task task = queue.take();

                    process(task);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        worker.start();
    }
}
```

Now:

```text
multiple workers consume from same queue
```

---

# 12. Dry Run — Multiple Consumers

Queue:

```text
[Task-1, Task-2, Task-3]
```

Consumers:

```text
Worker-1 takes Task-1
Worker-2 takes Task-2
Worker-3 takes Task-3
```

Benefit:

```text
higher throughput
```

Tradeoff:

```text
finish order may differ
```

---

# 13. Ordering Tradeoff

Single consumer:

```text
Task-1 finishes before Task-2
```

Multiple consumers:

```text
Task-2 may finish before Task-1
```

Why?

```text
Task processing time differs.
```

This is very important for:

```text
payments
inventory
order state transitions
```

---

# 14. When You Need Ordering

If tasks for same order/user must be ordered:

```text
same key should go to same worker/partition
```

Kafka solves this with:

```text
partition key
```

Example:

```text
orderId = 101
all events for order-101 go to same partition
```

---

# 15. Error Handling

Bad:

```java
process(task);
```

If exception happens:

```text
worker may die
queue stops draining
```

Better:

```java
try {
    process(task);
} catch (Exception e) {
    handleFailure(task, e);
}
```

---

# 16. Worker With Error Handling

```java
private void runWorker() {

    while (true) {

        try {

            Task task = queue.take();

            try {

                process(task);

            } catch (Exception e) {

                handleFailure(task, e);
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            break;
        }
    }
}
```

---

# 17. Retry Logic

Retry is needed when failures are temporary:

```text
network timeout
temporary DB failure
email provider down
external API 500
```

Example:

```java
private void handleFailure(Task task, Exception e) {

    if (task.getRetryCount() < 3) {

        queue.offer(task.incrementRetry());

    } else {

        moveToDeadLetterQueue(task, e);
    }
}
```

---

# 18. Retry Dry Run

Task:

```text
Task-1 retryCount = 0
```

Processing fails:

```text
retryCount < 3
```

Requeue:

```text
Task-1 retryCount = 1
```

Fails again:

```text
retryCount = 2
```

Fails again:

```text
retryCount = 3
```

Next failure:

```text
move to DLQ
```

---

# 19. Dead Letter Queue

DLQ means:

```text
Dead Letter Queue
```

Used for tasks that failed repeatedly.

Purpose:

```text
do not lose task
do not retry forever
allow manual investigation
```

---

# 20. Simple DLQ Example

```java
private final BlockingQueue<Task> deadLetterQueue =
        new ArrayBlockingQueue<>(1000);

private void moveToDeadLetterQueue(Task task, Exception e) {

    deadLetterQueue.offer(task);

    System.out.println(
            "Moved task to DLQ: " + task.getId()
    );
}
```

---

# 21. Retry Backoff

Immediate retry can overload downstream.

Better:

```text
retry after delay
```

Example strategies:

```text
fixed delay
exponential backoff
jitter
```

Example:

```text
1s
2s
4s
8s
```

---

# 22. Poison Pill Shutdown

Poison pill is a special task that tells worker to stop.

```java
public static final Task POISON =
        new Task("POISON", "", 0);
```

Worker:

```java
if (task == POISON) {
    break;
}
```

---

# 23. Poison Pill Worker Example

```java
private void runWorker() {

    while (true) {

        try {

            Task task = queue.take();

            if (task == POISON) {

                break;
            }

            process(task);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            break;
        }
    }
}
```

---

# 24. Shutdown With Multiple Workers

If there are 3 workers:

```text
send 3 poison pills
```

Why?

```text
Each worker needs one stop signal.
```

---

# 25. Graceful Shutdown Flow

```text
stop accepting new tasks
    ↓
let queue drain
    ↓
send poison pills
    ↓
workers exit
    ↓
application shuts down
```

---

# 26. Full Producer-Consumer Service

```java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerConsumerService {

    private static final Task POISON =
            new Task("POISON", "", 0);

    private final BlockingQueue<Task> queue =
            new ArrayBlockingQueue<>(100);

    private final BlockingQueue<Task> dlq =
            new ArrayBlockingQueue<>(1000);

    private final List<Thread> workers =
            new ArrayList<>();

    private volatile boolean acceptingTasks = true;

    public boolean submit(Task task) {

        if (!acceptingTasks) {

            return false;
        }

        return queue.offer(task);
    }

    public void start(int workerCount) {

        for (int i = 0; i < workerCount; i++) {

            Thread worker = new Thread(this::runWorker);

            worker.setName("worker-" + i);

            workers.add(worker);

            worker.start();
        }
    }

    private void runWorker() {

        while (true) {

            try {

                Task task = queue.take();

                if (task == POISON) {

                    break;
                }

                try {

                    process(task);

                } catch (Exception e) {

                    handleFailure(task, e);
                }

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void process(Task task) {

        System.out.println(
                Thread.currentThread().getName()
                + " processing "
                + task.getId()
        );
    }

    private void handleFailure(Task task, Exception e) {

        if (task.getRetryCount() < 3) {

            queue.offer(task.incrementRetry());

        } else {

            dlq.offer(task);
        }
    }

    public void shutdown() {

        acceptingTasks = false;

        for (int i = 0; i < workers.size(); i++) {

            queue.offer(POISON);
        }
    }
}
```

---

# 27. Full Service Dry Run

Start:

```text
3 workers started
all waiting on queue.take()
```

Producer submits:

```text
Task-1
Task-2
Task-3
```

Workers:

```text
worker-0 takes Task-1
worker-1 takes Task-2
worker-2 takes Task-3
```

Task fails:

```text
handleFailure()
retryCount < 3
requeue task
```

Shutdown:

```text
acceptingTasks = false
3 poison pills added
each worker receives one and exits
```

---

# 28. Why acceptingTasks Is volatile

```java
private volatile boolean acceptingTasks = true;
```

Because:

```text
one thread changes it during shutdown
other producer threads read it
```

volatile ensures visibility.

---

# 29. Metrics To Add

Production service should track:

```text
submitted count
accepted count
rejected count
processed count
failed count
retried count
DLQ count
queue size
worker count
processing latency
oldest task age
```

---

# 30. Simple Metrics With AtomicLong

```java
import java.util.concurrent.atomic.AtomicLong;

private final AtomicLong submitted =
        new AtomicLong();

private final AtomicLong processed =
        new AtomicLong();

private final AtomicLong failed =
        new AtomicLong();
```

Usage:

```java
submitted.incrementAndGet();
processed.incrementAndGet();
failed.incrementAndGet();
```

---

# 31. Backend Use Case — Notification Service

```text
Order created
    ↓
submit email task
    ↓
queue
    ↓
email workers
    ↓
send email/SMS/push
```

Needs:

```text
retry
DLQ
idempotency
metrics
backpressure
```

---

# 32. Backend Use Case — Payment Worker

```text
payment event
    ↓
queue
    ↓
settlement worker
```

Must be careful:

```text
idempotency is mandatory
duplicate processing is possible
ordering may matter
```

---

# 33. Idempotency

Idempotency means:

```text
same task can run multiple times without wrong effect.
```

Example:

```text
send payment settlement only once
```

Use:

```text
idempotency key
dedup table
unique constraint
processed event store
```

---

# 34. Duplicate Task Problem

Task may be processed twice if:

```text
worker crashes after processing
retry happens
producer resends
ack failed
```

So consumers must be:

```text
idempotent
```

---

# 35. Backpressure Strategy

When queue is full:

```text
queue.offer(task) returns false
```

Options:

```text
reject request
return 429
return 503
drop low-priority task
scale workers
send to external queue
```

---

# 36. Queue Capacity Tradeoff

Small queue:

```text
fast overload signal
low memory
less waiting latency
```

Large queue:

```text
absorbs bursts
but increases latency
can hide downstream problems
```

---

# 37. Production Rule

```text
Do not use unbounded queue for critical backend systems.
```

Prefer:

```text
bounded queue
metrics
rejection policy
retry/DLQ
backpressure
```

---

# 38. Thread Count Tuning

If task is CPU-bound:

```text
workers ≈ CPU cores
```

If task is IO-bound:

```text
workers can be higher
```

Example IO-bound:

```text
email API
HTTP call
DB call
file upload
```

---

# 39. Real Production Symptoms

Bad producer-consumer design shows:

```text
queue growing forever
consumer lag
high memory
slow API
task timeout
worker death
DLQ growth
duplicate processing
out-of-order processing
```

---

# 40. Interview Explanation

If interviewer asks:

```text
How would you implement producer-consumer in Java?
```

Good answer:

```text
I would use a BlockingQueue with producer threads submitting tasks
and consumer worker threads taking tasks from the queue. I would prefer
a bounded queue to provide backpressure. For production readiness,
I would add error handling, retries, DLQ, graceful shutdown, metrics,
and idempotency.
```

Strong backend addition:

```text
This is similar to how ThreadPoolExecutor works internally and how Kafka
acts as a distributed producer-consumer system.
```

---

# 41. Common Mistakes

## Mistake 1

```text
Using unbounded queue.
```

Can cause memory explosion.

---

## Mistake 2

```text
No shutdown strategy.
```

Workers may hang forever.

---

## Mistake 3

```text
No error handling in consumer.
```

Worker dies silently.

---

## Mistake 4

```text
No DLQ.
```

Bad tasks retry forever.

---

## Mistake 5

```text
No idempotency.
```

Duplicate processing causes data corruption.

---

# 42. Mini Dry Run Summary

```text
Producer submits task
    ↓
queue accepts task if capacity exists
    ↓
consumer takes task
    ↓
process succeeds
    ↓
metrics updated
```

Failure path:

```text
process fails
    ↓
retry if retryCount < max
    ↓
else move to DLQ
```

Shutdown path:

```text
stop accepting tasks
    ↓
send poison pills
    ↓
workers exit
```

---

# 43. Visual Summary

```text
Producer
   ↓
Bounded BlockingQueue
   ↓
Worker Pool
   ↓
Process Task
   ↓
Success / Retry / DLQ
```

---

# 44. What To Remember

```text
Producer-consumer is the base of async backend processing.

BlockingQueue is the safest Java implementation.

Bounded queue gives backpressure.

Multiple consumers improve throughput but can break ordering.

Production version needs retries, DLQ, metrics, graceful shutdown, and idempotency.
```

---

# 45. Next File

```text
016_Deadlock.md
```

Next you learn:

```text
how locks can freeze the system
deadlock conditions
two-lock deadlock dry run
prevention techniques
production debugging
```
