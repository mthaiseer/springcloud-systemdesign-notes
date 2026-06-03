# 014_BlockingQueue.md

# MiniConcurrency — 014 BlockingQueue

## 0. Why This File Exists

After learning producer-consumer, the next question is:

```text
Do we really need to manually write wait/notify logic every time?
```

Answer:

```text
No.
```

Java provides:

```text
BlockingQueue
```

BlockingQueue is the production-ready abstraction for:

```text
safe producer-consumer coordination
thread-safe task queues
bounded queues
worker queues
thread pool internals
backpressure
```

This file teaches:

```text
What BlockingQueue is
Why it exists
put/take behavior
offer/poll behavior
ArrayBlockingQueue
LinkedBlockingQueue
PriorityBlockingQueue
DelayQueue
ThreadPoolExecutor mapping
Backend production use cases
```

---

# 1. One-Line Definition

```text
BlockingQueue is a thread-safe queue where producers wait when queue is full
and consumers wait when queue is empty.
```

Simple meaning:

```text
Producer blocks if no space.
Consumer blocks if no data.
```

---

# 2. Real Mental Model

Imagine restaurant order counter.

```text
Chef places dishes on counter.
Waiter takes dishes from counter.
```

If counter is full:

```text
Chef waits.
```

If counter is empty:

```text
Waiter waits.
```

That is BlockingQueue.

---

# 3. Why BlockingQueue Exists

Manual producer-consumer using:

```text
synchronized
wait()
notify()
notifyAll()
```

is error-prone.

BlockingQueue handles internally:

```text
locking
waiting
signaling
thread safety
capacity control
```

---

# 4. Core Flow

```text
Producer Thread
      ↓ put()
BlockingQueue
      ↓ take()
Consumer Thread
```

---

# 5. Key Behavior

```text
put(item)
    ↓
waits if queue is full

take()
    ↓
waits if queue is empty
```

This gives natural backpressure.

---

# 6. Java Interface

```java
import java.util.concurrent.BlockingQueue;
```

Common implementations:

```java
ArrayBlockingQueue
LinkedBlockingQueue
PriorityBlockingQueue
DelayQueue
SynchronousQueue
LinkedTransferQueue
```

---

# 7. Basic Example

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueBasicDemo {

    public static void main(String[] args)
            throws Exception {

        BlockingQueue<Integer> queue =
                new ArrayBlockingQueue<>(2);

        queue.put(1);
        queue.put(2);

        System.out.println(queue.take());
        System.out.println(queue.take());
    }
}
```

Output:

```text
1
2
```

---

# 8. put() Dry Run

Queue capacity:

```text
2
```

Initial:

```text
queue = []
```

Operation:

```java
queue.put(1);
```

Result:

```text
queue = [1]
```

Operation:

```java
queue.put(2);
```

Result:

```text
queue = [1, 2]
```

Queue is full.

Now:

```java
queue.put(3);
```

This blocks until consumer removes an item.

---

# 9. take() Dry Run

Initial:

```text
queue = []
```

Consumer calls:

```java
queue.take();
```

Since queue is empty:

```text
consumer waits
```

Producer later calls:

```java
queue.put(10);
```

Consumer wakes and receives:

```text
10
```

---

# 10. Method Comparison

| Method | Queue Full | Queue Empty |
|---|---|---|
| put() | waits | — |
| take() | — | waits |
| offer() | returns false | — |
| poll() | — | returns null |
| offer(timeout) | waits limited time | — |
| poll(timeout) | — | waits limited time |
| add() | throws exception | — |
| remove() | — | throws exception |

---

# 11. put() vs offer()

## put()

```java
queue.put(item);
```

Meaning:

```text
Wait forever until space available.
```

---

## offer()

```java
boolean accepted = queue.offer(item);
```

Meaning:

```text
Try immediately.
If full, return false.
```

---

# 12. offer() Example

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class OfferDemo {

    public static void main(String[] args) {

        BlockingQueue<Integer> queue =
                new ArrayBlockingQueue<>(2);

        System.out.println(queue.offer(1));
        System.out.println(queue.offer(2));
        System.out.println(queue.offer(3));
    }
}
```

Output:

```text
true
true
false
```

---

# 13. take() vs poll()

## take()

```java
queue.take();
```

Meaning:

```text
Wait forever until item available.
```

---

## poll()

```java
queue.poll();
```

Meaning:

```text
Try immediately.
If empty, return null.
```

---

# 14. poll() Example

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PollDemo {

    public static void main(String[] args) {

        BlockingQueue<Integer> queue =
                new ArrayBlockingQueue<>(2);

        System.out.println(queue.poll());
    }
}
```

Output:

```text
null
```

---

# 15. Timeout Methods

Useful in production:

```java
queue.offer(item, 2, TimeUnit.SECONDS);
queue.poll(2, TimeUnit.SECONDS);
```

Meaning:

```text
wait only limited time
then fail gracefully
```

---

# 16. Timeout Offer Example

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TimeoutOfferDemo {

    public static void main(String[] args)
            throws Exception {

        BlockingQueue<Integer> queue =
                new ArrayBlockingQueue<>(1);

        queue.put(1);

        boolean accepted =
                queue.offer(2, 1, TimeUnit.SECONDS);

        System.out.println(accepted);
    }
}
```

Output:

```text
false
```

After 1 second, no space was available.

---

# 17. Producer-Consumer With BlockingQueue

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerConsumerBlockingQueue {

    public static void main(String[] args) {

        BlockingQueue<Integer> queue =
                new ArrayBlockingQueue<>(5);

        Thread producer = new Thread(() -> {

            int value = 1;

            while (true) {

                try {

                    queue.put(value);

                    System.out.println(
                            "Produced: " + value
                    );

                    value++;

                    Thread.sleep(300);

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

                    Thread.sleep(700);

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

# 18. Producer-Consumer Dry Run

Capacity:

```text
5
```

Producer is faster:

```text
produces every 300ms
```

Consumer is slower:

```text
consumes every 700ms
```

Queue grows:

```text
[1]
[1,2]
[1,2,3]
```

When queue full:

```text
producer blocks on put()
```

Consumer removes item:

```text
space available
producer continues
```

This is backpressure.

---

# 19. ArrayBlockingQueue

```java
new ArrayBlockingQueue<>(capacity)
```

Properties:

```text
fixed capacity
array based
bounded
good for backpressure
single lock or internal locking depending implementation
```

Use when:

```text
you want strict capacity limit
stable memory usage
predictable queue size
```

---

# 20. LinkedBlockingQueue

```java
new LinkedBlockingQueue<>(capacity)
```

Properties:

```text
linked nodes
optionally bounded
can be unbounded if no capacity passed
```

Danger:

```java
new LinkedBlockingQueue<>()
```

This can grow very large.

Production advice:

```text
Always prefer bounded capacity unless strong reason.
```

---

# 21. ArrayBlockingQueue vs LinkedBlockingQueue

| Feature | ArrayBlockingQueue | LinkedBlockingQueue |
|---|---|---|
| Backing structure | Array | Linked nodes |
| Capacity | Always bounded | Bounded or unbounded |
| Memory | Fixed | Grows with nodes |
| Backpressure | Strong | Only if bounded |
| Common use | Worker queues | Flexible producer-consumer |

---

# 22. PriorityBlockingQueue

Priority-based queue.

```java
PriorityBlockingQueue<Task>
```

Higher priority tasks processed first.

Important:

```text
It is unbounded by default.
```

Use carefully.

---

# 23. PriorityBlockingQueue Example

```java
import java.util.concurrent.PriorityBlockingQueue;

class Task implements Comparable<Task> {

    int priority;
    String name;

    Task(int priority, String name) {
        this.priority = priority;
        this.name = name;
    }

    public int compareTo(Task other) {
        return Integer.compare(this.priority, other.priority);
    }
}

public class PriorityQueueDemo {

    public static void main(String[] args)
            throws Exception {

        PriorityBlockingQueue<Task> queue =
                new PriorityBlockingQueue<>();

        queue.put(new Task(2, "low"));
        queue.put(new Task(1, "high"));

        System.out.println(queue.take().name);
    }
}
```

Output:

```text
high
```

---

# 24. DelayQueue

DelayQueue releases item only after delay expires.

Useful for:

```text
scheduled tasks
retry delay
timeout handling
delayed messages
```

---

# 25. SynchronousQueue

SynchronousQueue has:

```text
no internal capacity
```

Producer must hand item directly to consumer.

Mental model:

```text
direct handoff
```

Used in some thread pool designs.

---

# 26. BlockingQueue Inside ThreadPoolExecutor

ThreadPoolExecutor uses BlockingQueue as work queue.

Flow:

```text
submit task
    ↓
worker available? run
    ↓
else put task into queue
    ↓
worker take task later
```

---

# 27. ThreadPoolExecutor Mapping

```text
Caller thread = Producer
BlockingQueue = Work queue
Worker threads = Consumers
```

This is exactly producer-consumer.

---

# 28. Backend Example — Async Email

```text
Order service receives order
    ↓
adds email task to BlockingQueue
    ↓
email worker takes task
    ↓
sends email
```

Benefit:

```text
API response is fast
email sent in background
```

---

# 29. Backend Example — Log Writer

```text
Request thread creates log event
    ↓
BlockingQueue
    ↓
background log writer
```

Benefit:

```text
request thread does not block on slow IO
```

---

# 30. Backend Example — MiniScheduler

```text
scheduled jobs
    ↓
queue
    ↓
worker threads
```

BlockingQueue helps manage job execution safely.

---

# 31. Backpressure With offer()

For API systems, sometimes better:

```java
if (!queue.offer(task)) {
    return "System busy";
}
```

Instead of blocking request thread forever.

---

# 32. Example — Reject When Queue Full

```java
public boolean submitTask(Runnable task) {

    return queue.offer(task);
}
```

If false:

```text
return 429 Too Many Requests
or 503 Service Unavailable
```

This protects system under overload.

---

# 33. Graceful Shutdown With Interrupt

BlockingQueue methods throw:

```java
InterruptedException
```

Correct handling:

```java
catch (InterruptedException e) {
    Thread.currentThread().interrupt();
    break;
}
```

This preserves interrupt signal.

---

# 34. Worker Shutdown Example

```java
while (!Thread.currentThread().isInterrupted()) {

    try {

        Runnable task = queue.take();

        task.run();

    } catch (InterruptedException e) {

        Thread.currentThread().interrupt();
    }
}
```

---

# 35. Poison Pill Alternative

Special task means:

```text
stop worker
```

Example:

```java
Runnable poisonPill = () -> {};
```

Consumer checks identity and exits.

Used when you want controlled shutdown without interrupt.

---

# 36. Production Metrics

Always monitor:

```text
queue size
remaining capacity
producer rate
consumer rate
oldest task age
processing latency
rejection count
failure count
```

Queue size alone is not enough.

---

# 37. Queue Size Warning

Large queue may hide overload.

Example:

```text
queue size = 100000
```

System may look alive, but:

```text
new tasks wait too long
latency explodes
timeouts happen
```

---

# 38. Bounded Queue Production Rule

Good design:

```text
bounded queue + rejection policy + metrics
```

Bad design:

```text
unbounded queue + no monitoring
```

---

# 39. Common Rejection Strategies

When queue full:

```text
reject request
drop low priority task
block producer
retry later
send to DLQ
scale workers
return 429/503
```

Choose based on business criticality.

---

# 40. Ordering Tradeoff

BlockingQueue preserves queue order for simple FIFO queues.

But with multiple consumers:

```text
start order may be FIFO
finish order may differ
```

Why?

```text
different tasks take different processing time
```

---

# 41. Idempotency Reminder

If task processing can retry:

```text
same task may run twice
```

Need:

```text
idempotency key
deduplication
safe retry logic
```

Critical for:

```text
payments
orders
notifications
inventory
```

---

# 42. Real Production Symptoms

BlockingQueue issues show as:

```text
queue full
producer blocked
consumer lag
high task latency
rejections increasing
memory pressure
worker starvation
slow downstream dependency
```

---

# 43. Interview Explanation

If interviewer asks:

```text
What is BlockingQueue?
```

Good answer:

```text
BlockingQueue is a thread-safe queue designed for producer-consumer systems.
Producers block when the queue is full and consumers block when the queue is empty.
It removes the need to manually use wait/notify and is used internally by thread pools.
```

Strong backend addition:

```text
Bounded BlockingQueue provides backpressure, which protects backend systems
from accepting unlimited work under overload.
```

---

# 44. Common Mistakes

## Mistake 1

```text
Using unbounded LinkedBlockingQueue in production.
```

Can cause memory growth.

---

## Mistake 2

```text
Using put() in request path without timeout.
```

Request thread may block too long.

---

## Mistake 3

```text
Ignoring InterruptedException.
```

Breaks graceful shutdown.

---

## Mistake 4

```text
Only monitoring thread count, not queue size.
```

Queue metrics are critical.

---

## Mistake 5

```text
Assuming queue solves slow consumers.
```

Queue only buffers. It does not fix slow processing.

---

# 45. Mini Dry Run Summary

```text
Producer calls put()
    ↓
if queue has space, insert
    ↓
if full, wait

Consumer calls take()
    ↓
if item exists, remove
    ↓
if empty, wait
```

---

# 46. Visual Summary

```text
Producer
   ↓ put()
BlockingQueue
   ↓ take()
Consumer
```

With backpressure:

```text
Queue Full
   ↓
Producer blocks / rejects
   ↓
System protected
```

---

# 47. What To Remember

```text
BlockingQueue = thread-safe producer-consumer queue.

put() waits when full.

take() waits when empty.

offer() is useful for fail-fast overload control.

Prefer bounded queues in production.

ThreadPoolExecutor uses BlockingQueue internally.

Queue needs monitoring, backpressure, and rejection strategy.
```

---

# 48. Next File

```text
015_Producer_Consumer.md
```

Next you can either:

```text
deepen producer-consumer implementation with multiple producers/consumers
```

or move to:

```text
015_Deadlock.md
```

depending on your MiniConcurrency index.
