# 003_Thread_Lifecycle.md

# MiniConcurrency — 003 Thread Lifecycle

## 0. Why This File Exists

Before learning:

```text
Locks
wait/notify
BlockingQueue
ThreadPool
CompletableFuture
Deadlock debugging
```

you must understand:

```text
How a thread lives
How it moves between states
Why threads become blocked/waiting
How JVM schedules threads
```

This file builds the mental model for production concurrency debugging.

---

# 1. One-Line Definition

```text
Thread lifecycle = different states a thread goes through from creation to termination.
```

Java thread states:

```text
NEW
RUNNABLE
BLOCKED
WAITING
TIMED_WAITING
TERMINATED
```

---

# 2. Big Picture Lifecycle Diagram

```text
NEW
  |
  | start()
  v
RUNNABLE
  |
  | trying to acquire lock
  v
BLOCKED
  |
  | lock acquired
  v
RUNNABLE
  |
  | wait()
  v
WAITING
  |
  | notify()
  v
RUNNABLE
  |
  | sleep()
  v
TIMED_WAITING
  |
  | timeout over
  v
RUNNABLE
  |
  | run() completed
  v
TERMINATED
```

---

# 3. Important Mental Model

Very important:

```text
RUNNABLE does NOT mean currently running on CPU.
```

It means:

```text
Ready to run
or
currently running
```

OS scheduler decides actual execution.

---

# 4. State 1 — NEW

Definition:

```text
Thread object created but start() not called yet.
```

Example:

```java
Thread t = new Thread(() -> {
    System.out.println("Worker thread");
});
```

Current state:

```text
NEW
```

Why?

Because:

```text
Thread exists as object
but JVM has not scheduled it.
```

---

# 5. Java Example — NEW State

```java
public class NewStateDemo {

    public static void main(String[] args) {

        Thread t = new Thread(() -> {
            System.out.println("Running");
        });

        System.out.println(t.getState());
    }
}
```

Expected output:

```text
NEW
```

---

# 6. State 2 — RUNNABLE

Definition:

```text
Thread is ready to run or currently running.
```

Example:

```java
t.start();
```

After this:

```text
NEW → RUNNABLE
```

Important:

```text
start() does NOT guarantee immediate execution.
```

JVM gives thread to OS scheduler.

---

# 7. Java Example — RUNNABLE

```java
public class RunnableStateDemo {

    public static void main(String[] args) throws Exception {

        Thread t = new Thread(() -> {

            while (true) {

            }
        });

        t.start();

        Thread.sleep(100);

        System.out.println(t.getState());
    }
}
```

Likely output:

```text
RUNNABLE
```

---

# 8. Dry Run — RUNNABLE

```text
Step 1:
main thread creates worker thread.

State:
NEW

Step 2:
main calls start()

State:
RUNNABLE

Step 3:
OS scheduler may run thread immediately
or later.

Step 4:
Thread continuously executes loop.
```

---

# 9. State 3 — BLOCKED

Definition:

```text
Thread waiting to acquire monitor lock.
```

Usually happens with:

```java
synchronized
```

Important:

```text
Only one thread can own monitor lock.
```

Other threads become:

```text
BLOCKED
```

---

# 10. Java Example — BLOCKED

```java
public class BlockedDemo {

    static final Object lock = new Object();

    public static void main(String[] args) throws Exception {

        Thread t1 = new Thread(() -> {

            synchronized (lock) {

                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
            }
        });

        Thread t2 = new Thread(() -> {

            synchronized (lock) {

                System.out.println("Thread 2 acquired lock");
            }
        });

        t1.start();

        Thread.sleep(100);

        t2.start();

        Thread.sleep(100);

        System.out.println(t2.getState());
    }
}
```

Expected output:

```text
BLOCKED
```

---

# 11. Dry Run — BLOCKED

```text
Step 1:
t1 acquires lock.

Step 2:
t1 sleeps for 5 seconds
while still holding lock.

Step 3:
t2 tries synchronized(lock)

Step 4:
lock already owned by t1.

Step 5:
t2 becomes BLOCKED.

Step 6:
after t1 exits synchronized block,
t2 acquires lock.
```

---

# 12. State 4 — WAITING

Definition:

```text
Thread waits indefinitely until another thread wakes it.
```

Common causes:

```java
wait()
join()
LockSupport.park()
```

---

# 13. wait() Mental Model

```text
Thread voluntarily pauses
and releases lock.
```

Another thread later calls:

```java
notify()
or
notifyAll()
```

Then waiting thread wakes up.

---

# 14. Java Example — WAITING

```java
public class WaitingDemo {

    static final Object lock = new Object();

    public static void main(String[] args) throws Exception {

        Thread t = new Thread(() -> {

            synchronized (lock) {

                try {

                    lock.wait();

                } catch (Exception e) {

                }
            }
        });

        t.start();

        Thread.sleep(100);

        System.out.println(t.getState());
    }
}
```

Expected output:

```text
WAITING
```

---

# 15. Dry Run — WAITING

```text
Step 1:
thread acquires lock.

Step 2:
thread calls wait()

Step 3:
thread releases monitor lock.

Step 4:
thread enters WAITING state.

Step 5:
thread stays waiting forever
until notify() happens.
```

---

# 16. State 5 — TIMED_WAITING

Definition:

```text
Thread waits for specific time duration.
```

Common causes:

```java
sleep()
wait(timeout)
join(timeout)
```

---

# 17. Java Example — TIMED_WAITING

```java
public class TimedWaitingDemo {

    public static void main(String[] args) throws Exception {

        Thread t = new Thread(() -> {

            try {

                Thread.sleep(5000);

            } catch (Exception e) {

            }
        });

        t.start();

        Thread.sleep(100);

        System.out.println(t.getState());
    }
}
```

Expected output:

```text
TIMED_WAITING
```

---

# 18. Dry Run — TIMED_WAITING

```text
Step 1:
thread starts.

Step 2:
thread calls sleep(5000)

Step 3:
thread pauses for 5 seconds.

State:
TIMED_WAITING

Step 4:
after timeout,
thread returns to RUNNABLE.
```

---

# 19. State 6 — TERMINATED

Definition:

```text
Thread execution completed.
```

Example:

```java
run() method finished
```

or

```text
uncaught exception occurred
```

---

# 20. Java Example — TERMINATED

```java
public class TerminatedDemo {

    public static void main(String[] args) throws Exception {

        Thread t = new Thread(() -> {

            System.out.println("Done");
        });

        t.start();

        t.join();

        System.out.println(t.getState());
    }
}
```

Expected output:

```text
TERMINATED
```

---

# 21. Complete Lifecycle Example

```java
public class FullLifecycleDemo {

    public static void main(String[] args) throws Exception {

        Thread t = new Thread(() -> {

            try {

                Thread.sleep(1000);

            } catch (Exception e) {

            }
        });

        System.out.println("1. " + t.getState());

        t.start();

        System.out.println("2. " + t.getState());

        Thread.sleep(100);

        System.out.println("3. " + t.getState());

        t.join();

        System.out.println("4. " + t.getState());
    }
}
```

Possible output:

```text
1. NEW
2. RUNNABLE
3. TIMED_WAITING
4. TERMINATED
```

---

# 22. Thread State Transition Summary

| Action | State Change |
|---|---|
| new Thread() | NEW |
| start() | NEW → RUNNABLE |
| waiting for lock | RUNNABLE → BLOCKED |
| wait() | RUNNABLE → WAITING |
| sleep() | RUNNABLE → TIMED_WAITING |
| notify() | WAITING → RUNNABLE |
| timeout over | TIMED_WAITING → RUNNABLE |
| run() finished | RUNNABLE → TERMINATED |

---

# 23. Important Difference — BLOCKED vs WAITING

This confuses many developers.

---

## BLOCKED

```text
Waiting to acquire lock.
```

Thread cannot continue because:

```text
another thread owns monitor lock.
```

---

## WAITING

```text
Thread voluntarily waits.
```

Usually after:

```java
wait()
join()
```

Thread waits for another event.

---

# 24. sleep() vs wait()

Very important interview topic.

| Feature | sleep() | wait() |
|---|---|---|
| Class | Thread | Object |
| Releases lock | No | Yes |
| Needs synchronized | No | Yes |
| State | TIMED_WAITING | WAITING/TIMED_WAITING |
| Wakeup | timeout | notify/notifyAll |

---

# 25. Backend Example — Spring Boot

Imagine:

```text
100 HTTP requests arrive.
```

Tomcat worker threads handle them.

Some threads:

```text
RUNNABLE
→ processing request

BLOCKED
→ waiting for synchronized resource

WAITING
→ waiting on DB connection pool

TIMED_WAITING
→ sleep/retry timeout

TERMINATED
→ request finished
```

Understanding these states is critical for:

```text
Thread dump analysis
Performance debugging
Deadlock debugging
Latency troubleshooting
```

---

# 26. Backend Example — Kafka Consumer

Kafka consumer threads may become:

```text
RUNNABLE
→ processing messages

WAITING
→ polling broker

TIMED_WAITING
→ retry backoff

BLOCKED
→ lock contention
```

---

# 27. Real Production Debugging

Production issues often show:

```text
500 BLOCKED threads
```

Meaning:

```text
Some lock contention exists.
```

Or:

```text
Many WAITING threads
```

Meaning:

```text
Maybe DB pool exhausted.
```

Or:

```text
Many TIMED_WAITING threads
```

Meaning:

```text
Heavy retries or sleeps.
```

---

# 28. jstack Thread Dump Example

Production engineers use:

```bash
jstack <pid>
```

to inspect thread states.

Example:

```text
"worker-1" RUNNABLE
"worker-2" BLOCKED
"worker-3" WAITING
```

This helps diagnose:

```text
Deadlock
CPU spike
Slow requests
Lock contention
```

---

# 29. Common Mistakes

## Mistake 1

```text
Thinking RUNNABLE means actively running.
```

Wrong.

It means:

```text
Ready to run or running.
```

---

## Mistake 2

```text
Calling wait() without synchronized.
```

Wrong.

Leads to:

```text
IllegalMonitorStateException
```

---

## Mistake 3

```text
Thinking sleep() releases lock.
```

Wrong.

Thread still owns lock.

---

## Mistake 4

```text
Confusing BLOCKED with WAITING.
```

BLOCKED:

```text
waiting for lock
```

WAITING:

```text
waiting for event/notification
```

---

# 30. Visual Mental Model

```text
NEW
 ↓
RUNNABLE
 ↓
+----------------------------+
|                            |
| BLOCKED                    |
| WAITING                    |
| TIMED_WAITING              |
|                            |
+----------------------------+
 ↓
RUNNABLE
 ↓
TERMINATED
```

---

# 31. What To Remember

```text
NEW
→ created but not started

RUNNABLE
→ ready/running

BLOCKED
→ waiting for monitor lock

WAITING
→ waiting indefinitely

TIMED_WAITING
→ waiting for timeout

TERMINATED
→ execution finished
```

---

# 32. Real Backend Mental Model

```text
Spring Boot Process
    |
    ├── HTTP thread → RUNNABLE
    ├── DB thread → WAITING
    ├── Scheduler → TIMED_WAITING
    ├── Kafka consumer → RUNNABLE
    └── Lock contention thread → BLOCKED
```

This is exactly what happens in production systems.

---

# 33. Next File

```text
004_Context_Switching.md
```

Next you learn:

```text
How CPU switches between threads
Why too many threads hurt performance
Context switch overhead
CPU scheduling basics
Thread starvation
```
