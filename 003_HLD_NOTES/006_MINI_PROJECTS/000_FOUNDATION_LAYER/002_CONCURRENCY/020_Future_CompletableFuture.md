# 019_Future_CompletableFuture.md

# MiniConcurrency — 019 Future & CompletableFuture

## 0. Why This File Exists

In previous files you learned:

```text
Thread pools
ThreadPoolExecutor
Worker threads
Task queues
submit()
```

But modern backend systems need:

```text
asynchronous programming
parallel execution
non-blocking pipelines
composable async workflows
```

Java provides:

```text
Future
CompletableFuture
```

These are heavily used in:

```text
Spring Boot async APIs
microservices
parallel DB/API calls
Kafka processing
backend orchestration
notification systems
high-performance APIs
```

This file teaches:

```text
What Future is
Future limitations
What CompletableFuture is
supplyAsync
runAsync
thenApply
thenCompose
thenCombine
exception handling
parallel execution
backend async patterns
production pitfalls
```

---

# 1. One-Line Definition

## Future

```text
Future represents the result of a task that may complete later.
```

---

## CompletableFuture

```text
CompletableFuture is an advanced async pipeline API for composing asynchronous tasks.
```

---

# 2. Real Mental Model

Restaurant example.

Without async:

```text
Wait for coffee
then order sandwich
then wait again
```

With async:

```text
Coffee and sandwich prepared in parallel
```

Faster overall.

---

# 3. Why Async Programming Matters

Synchronous flow:

```text
request waits
thread blocked
CPU underutilized
high latency
```

Async flow:

```text
task executes in background
thread reused
better throughput
```

---

# 4. What Is Future?

Future means:

```text
Result available in future.
```

Example:

```text
Start task now.
Collect result later.
```

---

# 5. Future Example

```java
import java.util.concurrent.*;

public class FutureDemo {

    public static void main(String[] args)
            throws Exception {

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        Future<Integer> future =
                executor.submit(() -> {

                    Thread.sleep(2000);

                    return 42;
                });

        System.out.println("Task submitted");

        Integer result = future.get();

        System.out.println(result);

        executor.shutdown();
    }
}
```

---

# 6. Future Dry Run

Step 1:

```text
Task submitted to thread pool
```

Step 2:

```text
Worker thread executes task
```

Step 3:

```text
Main thread continues
```

Step 4:

```text
future.get() waits
```

Step 5:

```text
Task completes
result returned
```

---

# 7. Future Problem

Main problem:

```java
future.get()
```

is blocking.

Meaning:

```text
thread waits until result arrives
```

This reduces async benefit.

---

# 8. Future Limitations

```text
Blocking get()
Hard to combine futures
Hard to chain operations
Manual coordination
Poor composition
```

To solve this:

```text
CompletableFuture
```

was introduced.

---

# 9. What Is CompletableFuture?

CompletableFuture allows:

```text
async pipelines
callbacks
parallel composition
non-blocking chains
```

Think:

```text
Promise pipeline
```

similar to JavaScript promises.

---

# 10. supplyAsync()

Used when task returns result.

Example:

```java
CompletableFuture<String> future =
        CompletableFuture.supplyAsync(() -> {

            return "Hello";
        });
```

---

# 11. runAsync()

Used when task returns nothing.

Example:

```java
CompletableFuture<Void> future =
        CompletableFuture.runAsync(() -> {

            System.out.println("Running");
        });
```

---

# 12. supplyAsync Example

```java
import java.util.concurrent.*;

public class SupplyAsyncDemo {

    public static void main(String[] args)
            throws Exception {

        CompletableFuture<String> future =
                CompletableFuture.supplyAsync(() -> {

                    sleep(2000);

                    return "Hello Async";
                });

        System.out.println("Doing other work");

        String result = future.get();

        System.out.println(result);
    }

    private static void sleep(long millis) {

        try {

            Thread.sleep(millis);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }
}
```

---

# 13. Async Flow

```text
Main thread starts async task
      ↓
Worker thread executes task
      ↓
Main thread continues
      ↓
Later result collected
```

---

# 14. thenApply()

Transforms result.

Example:

```java
future.thenApply(value -> value.toUpperCase())
```

Meaning:

```text
take previous result
transform it
```

---

# 15. thenApply Example

```java
import java.util.concurrent.*;

public class ThenApplyDemo {

    public static void main(String[] args)
            throws Exception {

        CompletableFuture<String> future =
                CompletableFuture
                        .supplyAsync(() -> "hello")
                        .thenApply(value ->
                                value.toUpperCase());

        System.out.println(
                future.get()
        );
    }
}
```

Output:

```text
HELLO
```

---

# 16. Pipeline Mental Model

```text
Task-1
   ↓
Transform result
   ↓
Transform again
   ↓
Final result
```

Async pipeline.

---

# 17. Multiple thenApply()

```java
CompletableFuture
    .supplyAsync(() -> "hello")
    .thenApply(s -> s + " world")
    .thenApply(String::toUpperCase);
```

Flow:

```text
"hello"
→ "hello world"
→ "HELLO WORLD"
```

---

# 18. thenAccept()

Consumes result without returning new one.

Example:

```java
future.thenAccept(System.out::println);
```

Similar to:

```text
final consumer
```

---

# 19. thenRun()

Runs task after completion.

No input.

No output.

Example:

```java
future.thenRun(() ->
        System.out.println("Done"));
```

---

# 20. thenCompose()

Very important backend concept.

Used for:

```text
async task depending on previous async result
```

---

# 21. thenCompose Mental Model

Example:

```text
Get user
    ↓
Use userId to fetch orders
```

Second async task depends on first.

---

# 22. thenCompose Example

```java
import java.util.concurrent.*;

public class ThenComposeDemo {

    public static void main(String[] args)
            throws Exception {

        CompletableFuture<String> future =
                getUser()
                .thenCompose(user ->
                        getOrders(user));

        System.out.println(
                future.get()
        );
    }

    static CompletableFuture<String> getUser() {

        return CompletableFuture
                .supplyAsync(() -> "user-101");
    }

    static CompletableFuture<String> getOrders(
            String user) {

        return CompletableFuture
                .supplyAsync(() ->
                        "orders for " + user);
    }
}
```

---

# 23. thenCompose Flow

```text
getUser()
    ↓
returns userId
    ↓
getOrders(userId)
    ↓
returns orders
```

Very common microservice flow.

---

# 24. thenApply vs thenCompose

## thenApply

Transforms normal value.

```text
A → B
```

---

## thenCompose

Chains async future.

```text
Future<A> → Future<B>
```

Very important interview question.

---

# 25. Parallel Async Execution

One huge CompletableFuture advantage:

```text
parallel execution
```

---

# 26. Example — Parallel APIs

Without parallelism:

```text
Call User API
wait
Call Order API
wait
Call Payment API
wait
```

Slow.

---

# 27. Parallel Flow

With CompletableFuture:

```text
Call all APIs together
wait for all results
combine results
```

Much faster.

---

# 28. allOf()

Waits for multiple futures.

Example:

```java
CompletableFuture.allOf(f1, f2, f3)
```

---

# 29. Parallel Example

```java
import java.util.concurrent.*;

public class AllOfDemo {

    public static void main(String[] args)
            throws Exception {

        CompletableFuture<String> userFuture =
                CompletableFuture
                        .supplyAsync(() -> {

                            sleep(2000);

                            return "User";
                        });

        CompletableFuture<String> orderFuture =
                CompletableFuture
                        .supplyAsync(() -> {

                            sleep(2000);

                            return "Orders";
                        });

        CompletableFuture<Void> combined =
                CompletableFuture.allOf(
                        userFuture,
                        orderFuture
                );

        combined.get();

        System.out.println(
                userFuture.get()
        );

        System.out.println(
                orderFuture.get()
        );
    }

    static void sleep(long millis) {

        try {

            Thread.sleep(millis);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }
}
```

---

# 30. Parallel Execution Dry Run

Both tasks start together.

```text
User API → 2s
Order API → 2s
```

Total time:

```text
≈ 2s
```

Instead of:

```text
≈ 4s
```

Huge latency improvement.

---

# 31. thenCombine()

Combines two independent futures.

Example:

```java
future1.thenCombine(
    future2,
    (a, b) -> a + b
)
```

---

# 32. thenCombine Example

```java
import java.util.concurrent.*;

public class ThenCombineDemo {

    public static void main(String[] args)
            throws Exception {

        CompletableFuture<String> first =
                CompletableFuture
                        .supplyAsync(() -> "Hello");

        CompletableFuture<String> second =
                CompletableFuture
                        .supplyAsync(() -> "World");

        CompletableFuture<String> combined =
                first.thenCombine(
                        second,
                        (a, b) -> a + " " + b
                );

        System.out.println(
                combined.get()
        );
    }
}
```

Output:

```text
Hello World
```

---

# 33. Exception Handling Problem

Async tasks may fail.

Without handling:

```text
future pipeline breaks
```

Need proper exception handling.

---

# 34. exceptionally()

Handles exception.

Example:

```java
future.exceptionally(ex -> "fallback");
```

---

# 35. Exception Example

```java
import java.util.concurrent.*;

public class ExceptionDemo {

    public static void main(String[] args)
            throws Exception {

        CompletableFuture<String> future =
                CompletableFuture
                        .supplyAsync(() -> {

                            int x = 1 / 0;

                            return "hello";
                        })
                        .exceptionally(ex -> {

                            return "fallback";
                        });

        System.out.println(
                future.get()
        );
    }
}
```

Output:

```text
fallback
```

---

# 36. handle()

Can process:

```text
success
or failure
```

Example:

```java
future.handle((result, ex) -> ...)
```

---

# 37. whenComplete()

Used for side effects:

```text
logging
metrics
cleanup
```

without changing result.

---

# 38. Custom Executor

By default:

```text
ForkJoinPool.commonPool()
```

is used.

Production systems usually use:

```text
custom thread pool
```

---

# 39. Custom Executor Example

```java
ExecutorService executor =
        Executors.newFixedThreadPool(10);

CompletableFuture<String> future =
        CompletableFuture
                .supplyAsync(() -> {

                    return "Hello";
                }, executor);
```

---

# 40. Why Custom Executor Matters

Avoids:

```text
shared common pool overload
```

Provides:

```text
better isolation
monitoring
resource control
```

---

# 41. Backend Example — Aggregator API

Common microservice pattern.

Example:

```text
Fetch User
Fetch Orders
Fetch Recommendations
```

all in parallel.

Then combine.

---

# 42. Aggregator Flow

```text
HTTP request
      ↓
parallel async calls
      ↓
wait all
      ↓
merge response
      ↓
return combined JSON
```

Very common backend architecture.

---

# 43. Backend Example — Notification System

```text
Email async
SMS async
Push notification async
```

Run in parallel.

Improves latency.

---

# 44. Backend Example — Payment Flow

```text
Validate payment
      ↓
Call fraud service
      ↓
Update DB
      ↓
Publish Kafka event
```

Some steps can execute asynchronously.

---

# 45. Common Production Problems

```text
Blocking get()
Too many async tasks
No timeout
Shared common pool overload
Deep callback chains
Memory pressure
Unhandled exceptions
```

---

# 46. Blocking get() Problem

Bad:

```java
future.get();
```

inside request thread.

This blocks thread.

Kills async scalability.

---

# 47. Timeout Handling

Better:

```java
future.get(2, TimeUnit.SECONDS);
```

or:

```java
orTimeout()
completeOnTimeout()
```

---

# 48. Async Does NOT Mean Faster CPU

Async mainly helps:

```text
IO-bound workloads
```

because waiting time overlaps.

Not magic CPU speedup.

---

# 49. CompletableFuture Mental Model

```text
Start async task
      ↓
Attach transformations
      ↓
Attach async dependencies
      ↓
Combine parallel tasks
      ↓
Handle failures
      ↓
Produce final result
```

---

# 50. Interview Explanation

If interviewer asks:

```text
Why CompletableFuture?
```

Good answer:

```text
CompletableFuture enables non-blocking asynchronous programming,
parallel execution, async composition, and pipeline-based processing.
It is commonly used for parallel API calls, backend orchestration,
and high-performance asynchronous workflows.
```

Strong backend addition:

```text
thenCompose is used for dependent async tasks,
while allOf and thenCombine help parallelize independent operations.
```

---

# 51. Common Mistakes

## Mistake 1

```text
Blocking immediately using get().
```

Kills async benefit.

---

## Mistake 2

```text
Using commonPool for everything.
```

Can overload shared pool.

---

## Mistake 3

```text
Ignoring exception handling.
```

Async failures disappear silently.

---

## Mistake 4

```text
Creating too many async tasks.
```

Overhead and thread exhaustion.

---

## Mistake 5

```text
No timeout.
```

Requests may hang forever.

---

# 52. Mini Dry Run Summary

```text
Start async task
      ↓
Worker thread executes task
      ↓
Main thread continues
      ↓
Async pipeline transforms result
      ↓
Parallel futures combine
      ↓
Final response generated
```

---

# 53. Visual Summary

```text
Async Tasks
     ↓
CompletableFuture Pipeline
     ↓
Transform
     ↓
Compose
     ↓
Combine
     ↓
Handle Errors
     ↓
Final Result
```

---

# 54. What To Remember

```text
Future = result later.

CompletableFuture = async pipeline framework.

thenApply → transform value.
thenCompose → chain async task.
thenCombine/allOf → parallel execution.

CompletableFuture is heavily used in backend microservices.
```

---

# 55. Next File

```text
020_ForkJoinPool_WorkStealing.md
```

Next you learn:

```text
ForkJoinPool
work stealing
recursive parallelism
divide and conquer
parallel streams
CPU-bound parallel execution
```
