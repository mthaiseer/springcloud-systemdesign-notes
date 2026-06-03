# 012_Thread_Communication.md

# MiniConcurrency — 012 Thread Communication

## 0. Why This File Exists

After learning:

```text
Locks
Mutex
Atomic variables
volatile
```

the next important question is:

```text
How do threads coordinate with each other?
```

Example:

```text
Producer creates task
Consumer waits for task

One thread finishes work
Another thread continues

Thread waits until condition becomes true
```

This is called:

```text
Thread communication
```

This file teaches:

```text
wait()
notify()
notifyAll()
producer-consumer pattern
condition signaling
monitor ownership
wait vs sleep
BlockingQueue mental model
backend examples
```

---

# 1. One-Line Definition

```text
Thread communication means threads coordinating execution and exchanging signals safely.
```

Simple meaning:

```text
One thread tells another:
"Now you can continue."
```

---

# 2. Real Mental Model

Imagine restaurant kitchen.

```text
Chef prepares food
Waiter waits
```

When food ready:

```text
Chef signals waiter
```

Then waiter delivers food.

This is similar to:

```text
Producer → Consumer
```

---

# 3. Why Communication Is Needed

Without coordination:

```text
Consumer may try to consume before producer creates data.
```

Problem:

```text
Empty queue
Null value
Busy waiting
Waste CPU
```

Need safe coordination.

---

# 4. Busy Waiting Problem

Bad approach:

```java
while(queue.isEmpty()) {

}
```

Problem:

```text
Infinite CPU usage
Wastes CPU cycles
Poor scalability
```

Better:

```text
Thread sleeps/waits efficiently until notified.
```

---

# 5. Java wait() and notify()

Java provides:

```java
wait()
notify()
notifyAll()
```

These belong to:

```java
Object class
```

Not Thread class.

---

# 6. Important Rule

Very important:

```text
wait()/notify() must be called inside synchronized block.
```

Otherwise:

```text
IllegalMonitorStateException
```

---

# 7. Why synchronized Is Required

Because wait/notify work with:

```text
Object monitor lock
```

Thread must own monitor before communication.

---

# 8. wait() Meaning

```java
lock.wait();
```

Means:

```text
Release lock
Pause thread
Wait for notification
```

Important:

```text
wait() releases monitor lock
```

---

# 9. notify() Meaning

```java
lock.notify();
```

Means:

```text
Wake one waiting thread.
```

But:

```text
Not immediately running.
Thread must reacquire lock first.
```

---

# 10. notifyAll() Meaning

```java
lock.notifyAll();
```

Means:

```text
Wake all waiting threads.
```

They compete for lock afterward.

---

# 11. wait/notify Flow

```text
Thread A:
wait()
    ↓
releases lock
    ↓
goes WAITING

Thread B:
notify()
    ↓
Thread A wakes up
    ↓
tries to reacquire lock
    ↓
continues execution
```

---

# 12. Basic wait/notify Example

```java
public class WaitNotifyDemo {

    private static final Object lock =
            new Object();

    public static void main(String[] args)
            throws Exception {

        Thread waitingThread = new Thread(() -> {

            synchronized (lock) {

                try {

                    System.out.println(
                            "Waiting thread waiting..."
                    );

                    lock.wait();

                    System.out.println(
                            "Waiting thread resumed"
                    );

                } catch (InterruptedException e) {

                }
            }
        });

        Thread notifierThread = new Thread(() -> {

            synchronized (lock) {

                System.out.println(
                        "Notifier thread notifying"
                );

                lock.notify();
            }
        });

        waitingThread.start();

        Thread.sleep(1000);

        notifierThread.start();
    }
}
```

Possible output:

```text
Waiting thread waiting...
Notifier thread notifying
Waiting thread resumed
```

---

# 13. Step-by-Step Dry Run

Initial:

```text
waitingThread starts
```

Step 1:

```text
waitingThread enters synchronized(lock)
```

Step 2:

```text
calls lock.wait()
```

Effects:

```text
releases lock
goes WAITING state
```

Step 3:

```text
notifierThread enters synchronized(lock)
```

Step 4:

```text
calls notify()
```

Step 5:

```text
waitingThread wakes up
```

But:

```text
must reacquire lock first
```

Step 6:

```text
waitingThread continues after wait()
```

---

# 14. Important: wait() Releases Lock

Very important.

```java
lock.wait();
```

releases monitor lock.

But:

```java
Thread.sleep()
```

does NOT release lock.

---

# 15. wait() vs sleep()

| Feature | wait() | sleep() |
|---|---|---|
| Class | Object | Thread |
| Releases lock | Yes | No |
| Needs synchronized | Yes | No |
| Purpose | Thread coordination | Delay/pause |
| Woken by | notify/notifyAll | timeout |

---

# 16. Producer-Consumer Problem

Classic thread communication problem.

Producer:

```text
Creates tasks/data
```

Consumer:

```text
Consumes tasks/data
```

Need coordination when:

```text
Queue empty
Queue full
```

---

# 17. Simple Producer-Consumer Example

```java
import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumerDemo {

    private static final Queue<Integer> queue =
            new LinkedList<>();

    private static final Object lock =
            new Object();

    public static void main(String[] args) {

        Thread producer = new Thread(() -> {

            int value = 0;

            while (true) {

                synchronized (lock) {

                    queue.add(value);

                    System.out.println(
                            "Produced: " + value
                    );

                    value++;

                    lock.notify();

                    try {

                        Thread.sleep(500);

                    } catch (Exception e) {

                    }
                }
            }
        });

        Thread consumer = new Thread(() -> {

            while (true) {

                synchronized (lock) {

                    while (queue.isEmpty()) {

                        try {

                            lock.wait();

                        } catch (Exception e) {

                        }
                    }

                    int item = queue.poll();

                    System.out.println(
                            "Consumed: " + item
                    );
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

Initial:

```text
queue empty
```

Consumer:

```text
enters synchronized
queue empty
calls wait()
releases lock
WAITING
```

Producer:

```text
acquires lock
adds item to queue
calls notify()
releases lock
```

Consumer:

```text
wakes up
reacquires lock
consumes item
```

Safe coordination.

---

# 19. Why while Instead of if?

Very important.

Correct:

```java
while(queue.isEmpty()) {

    lock.wait();
}
```

Not:

```java
if(queue.isEmpty()) {

    lock.wait();
}
```

Why?

Because:

```text
Spurious wakeups can happen.
Another thread may consume item first.
```

Need condition recheck.

---

# 20. Spurious Wakeup

Thread may wake up:

```text
without notify
or condition still false
```

Rare but possible.

Always recheck condition.

---

# 21. notify vs notifyAll

## notify()

```text
Wakes one waiting thread.
```

Good for:

```text
single consumer
simple coordination
```

---

## notifyAll()

```text
Wakes all waiting threads.
```

Good when:

```text
multiple waiting conditions
multiple producers/consumers
```

Safer but may wake unnecessary threads.

---

# 22. Thread States During Communication

wait():

```text
WAITING
```

sleep():

```text
TIMED_WAITING
```

waiting for synchronized lock:

```text
BLOCKED
```

---

# 23. Common Backend Example — Task Queue

Thread communication used in:

```text
thread pools
task schedulers
message queues
Kafka consumers
job workers
```

Example:

```text
Producer thread adds task
Worker thread waits for task
```

---

# 24. Why BlockingQueue Exists

Manual wait/notify is error-prone.

Java provides:

```java
BlockingQueue
```

which internally handles:

```text
locking
waiting
signaling
thread coordination
```

Automatically.

---

# 25. BlockingQueue Example

```java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueDemo {

    public static void main(String[] args) {

        BlockingQueue<Integer> queue =
                new LinkedBlockingQueue<>();

        Thread producer = new Thread(() -> {

            int value = 0;

            while (true) {

                try {

                    queue.put(value);

                    System.out.println(
                            "Produced: " + value
                    );

                    value++;

                    Thread.sleep(500);

                } catch (Exception e) {

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

                } catch (Exception e) {

                }
            }
        });

        producer.start();
        consumer.start();
    }
}
```

---

# 26. Why BlockingQueue Is Better

Instead of manual:

```text
wait()
notify()
synchronized
condition checks
```

BlockingQueue internally handles:

```text
thread-safe queue
waiting
notification
locking
```

Much safer.

---

# 27. Backend Example — ThreadPoolExecutor

ThreadPoolExecutor internally uses:

```text
BlockingQueue
```

Flow:

```text
HTTP request creates task
task added to queue
worker threads wait for tasks
workers consume tasks
```

Exactly producer-consumer.

---

# 28. Kafka Consumer Mental Model

Kafka broker:

```text
produces messages
```

Consumer threads:

```text
poll messages
process messages
```

Internally similar to producer-consumer coordination.

---

# 29. wait/notify Limitations

Manual wait/notify is difficult because of:

```text
missed signals
deadlock
wrong lock ownership
spurious wakeups
complex debugging
```

Modern systems often prefer:

```text
BlockingQueue
Executors
CompletableFuture
Reactive programming
```

---

# 30. IllegalMonitorStateException

This fails:

```java
lock.wait();
```

outside synchronized block.

Why?

```text
Thread does not own monitor lock.
```

Correct:

```java
synchronized(lock) {

    lock.wait();
}
```

---

# 31. Lost Notification Problem

Dangerous scenario:

```text
notify() happens before wait()
```

Then waiting thread may wait forever.

Need careful condition handling.

BlockingQueue avoids many such problems.

---

# 32. Condition Variables

Advanced lock communication later uses:

```java
Condition
await()
signal()
```

with ReentrantLock.

More flexible than wait/notify.

---

# 33. Real Production Mental Model

```text
Producer thread
    ↓
shared queue
    ↓
consumer thread waits
    ↓
producer notifies
    ↓
consumer wakes and processes
```

This pattern exists everywhere in backend systems.

---

# 34. Interview Explanation

If interviewer asks:

```text
What is wait/notify in Java?
```

Good answer:

```text
wait() allows a thread to release a monitor lock and suspend execution
until another thread signals it using notify() or notifyAll().
They are used for thread communication and coordination,
commonly in producer-consumer scenarios.
```

Strong backend addition:

```text
Modern Java applications often prefer BlockingQueue or higher-level
concurrency utilities instead of manual wait/notify.
```

---

# 35. Common Mistakes

## Mistake 1

```text
Calling wait() outside synchronized block.
```

Causes exception.

---

## Mistake 2

```text
Using if instead of while around wait().
```

Unsafe because of spurious wakeups.

---

## Mistake 3

```text
Forgetting that wait() releases lock.
```

Very important behavior.

---

## Mistake 4

```text
Using notify() when multiple conditions exist.
```

May wake wrong thread.

---

# 36. Mini Dry Run Summary

```text
Consumer checks queue
    ↓
queue empty
    ↓
consumer wait()
    ↓
producer adds item
    ↓
producer notify()
    ↓
consumer wakes
    ↓
consumer processes item
```

---

# 37. Visual Summary

```text
Producer Thread
      ↓
Shared Queue
      ↓
notify()
      ↓
Consumer Thread wakes
      ↓
Processes item
```

---

# 38. What To Remember

```text
wait() releases lock and waits.

notify() wakes one thread.

notifyAll() wakes all waiting threads.

wait/notify must be inside synchronized block.

Always use while around wait().

BlockingQueue is safer than manual wait/notify.
```

---

# 39. Next File

```text
013_Producer_Consumer_Pattern.md
```

Next you learn deeper:

```text
bounded buffer
backpressure
BlockingQueue internals
throughput balancing
real backend queue systems
```
