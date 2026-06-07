# 008_Sliding_Window_Count_Based.md

# MiniCircuitBreaker — 008 Sliding Window Count Based

---

# 1. Why This File Exists

Previous file explained:

```text
failure threshold
slow call threshold
minimum calls
```

But important question:

```text
How are recent calls tracked efficiently?
```

Need:

```text
Sliding Window
```

This file explains:

```text
count-based sliding window
ring buffer
circular arrays
bucket rotation
O(1) metrics updates
efficient failure calculation
production performance optimization
Resilience4j internals
```

Sliding window is one of the MOST important internal circuit breaker algorithms.

---

# 2. One-Line Definition

```text
Count-based sliding window tracks metrics for the most recent N calls.
```

---

# 3. Biggest Mental Model

```text
Recent Requests
       ↓
Store In Circular Window
       ↓
Continuously Evict Old Calls
       ↓
Calculate Current Health
```

---

# 4. Why Sliding Window Needed

Circuit breaker must evaluate:

```text
recent service health
```

NOT:

```text
all-time history
```

Old failures should eventually disappear.

---

# 5. Bad Approach

Wrong approach:

```text
track all requests forever
```

Problems:

```text
memory growth
old failures never disappear
incorrect health evaluation
```

---

# 6. Sliding Window Solution

Track only:

```text
last N requests
```

Example:

```text
last 100 calls
```

Older entries automatically removed.

---

# 7. Sliding Window ASCII

```text
Window Size = 5

[S][F][S][S][F]
```

New request enters:

```text
[F][S][S][F][S]
```

Oldest entry removed.

---

# 8. Why "Sliding"

Window continuously:

```text
moves forward
```

as new requests arrive.

Old requests:

```text
slide out
```

New requests:

```text
slide in
```

---

# 9. Sliding Mental Model

```text
fixed-size moving history
```

---

# 10. Count-Based Meaning

Count-based means:

```text
fixed number of requests
```

NOT:

```text
time duration
```

Example:

```text
track last 100 calls
```

---

# 11. Count-Based vs Time-Based

## Count-Based

```text
last N requests
```

---

## Time-Based

```text
last N seconds/minutes
```

---

# 12. Why Count-Based Popular

Advantages:

```text
simple
fast
predictable memory
O(1) updates
```

Very efficient.

---

# 13. Core Operations

Sliding window performs:

```text
insert new result
remove oldest result
update metrics
calculate rates
```

All must be:

```text
very fast
```

---

# 14. Why Performance Critical

Circuit breaker executes on:

```text
every protected request
```

Window updates must be:

```text
microseconds
```

---

# 15. Naive Implementation Problem

Bad approach:

```text
List + full recalculation every request
```

Example:

```text
iterate all 100 calls repeatedly
```

Too expensive.

---

# 16. Better Approach

Need:

```text
incremental O(1) updates
```

using:

```text
ring buffer
circular array
```

---

# 17. Ring Buffer Mental Model

```text
fixed-size circular array
```

When full:

```text
overwrite oldest element
```

---

# 18. Ring Buffer ASCII

```text
Index:
0 1 2 3 4

[S][F][S][S][F]
```

Next insertion overwrites oldest:

```text
[F][F][S][S][F]
 ^
```

Pointer rotates circularly.

---

# 19. Circular Pointer

Window uses:

```text
current index pointer
```

Example:

```text
0 → 1 → 2 → 3 → 4 → 0 → 1
```

Circular movement.

---

# 20. Why Circular Structure Powerful

Provides:

```text
constant memory
constant update time
automatic eviction
```

Perfect for high-scale systems.

---

# 21. Window Entry Types

Each entry may store:

```text
SUCCESS
FAILURE
SLOW_SUCCESS
SLOW_FAILURE
```

---

# 22. Window Entry ASCII

```text
[S][F][SS][SF][S]
```

---

# 23. Incremental Metrics Update

Instead of recalculating entire window:

```text
adjust counters incrementally
```

Very important optimization.

---

# 24. Incremental Update Example

Suppose oldest entry removed:

```text
FAILURE
```

Failure counter decreases:

```text
failureCount--
```

New entry added:

```text
SUCCESS
```

Success counter increases:

```text
successCount++
```

No full scan needed.

---

# 25. O(1) Complexity

Sliding window operations should be:

```text
O(1)
```

NOT:

```text
O(N)
```

for production performance.

---

# 26. O(1) Mental Model

```text
constant work per request
```

No matter window size.

---

# 27. Window Rotation Flow

```text
New Request
      ↓
Overwrite Oldest Entry
      ↓
Update Counters
      ↓
Move Pointer
```

---

# 28. Window Rotation ASCII

```text
Current Index = 2

[S][F][S][F][S]
       ^

Insert FAILURE

[S][F][F][F][S]
          ^
```

---

# 29. Failure Rate Calculation

Failure rate:

genui{"math_block_widget_always_prefetch_v2":{"content":"Failure\\ Rate = \\frac{Failures}{Total\\ Calls} \\times 100"}}

---

# 30. Example Window

```text
[S][F][F][S][F]
```

Failures:

```text
3
```

Total:

```text
5
```

Failure rate:

genui{"math_block_widget_always_prefetch_v2":{"content":"Failure\\ Rate = \\frac{3}{5} \\times 100 = 60\\%"}}

---

# 31. Threshold Decision

Threshold:

```text
50%
```

Current:

```text
60%
```

Result:

```text
OPEN
```

---

# 32. Slow Call Tracking

Window also tracks:

```text
slow calls
```

Example:

```text
response > 2 seconds
```

Counts as slow.

---

# 33. Slow Call Window Example

```text
[SLOW][SLOW][SUCCESS][SLOW][SUCCESS]
```

Slow calls:

```text
3 / 5 = 60%
```

Threshold exceeded.

---

# 34. Why Sliding Window Better Than Counters

Simple counters:

```text
all-time failures
```

never recover naturally.

Sliding window enables:

```text
automatic recovery over time
```

---

# 35. Recovery Mental Model

As old failures slide out:

```text
failure rate improves
```

Healthy service eventually:

```text
returns CLOSED
```

---

# 36. Recovery ASCII

Old unhealthy window:

```text
[F][F][F][F][S]
```

New healthy calls arrive:

```text
[S][S][F][S][S]
```

Failure rate decreases automatically.

---

# 37. Memory Efficiency

Window size fixed.

Example:

```text
100 entries always
```

Memory usage constant.

---

# 38. Why Fixed Memory Important

High-scale systems may process:

```text
millions of requests/sec
```

Need predictable memory.

---

# 39. Internal Data Structure

Window stores:

```text
entries array
current index
success count
failure count
slow count
```

---

# 40. Internal Structure ASCII

```text
SlidingWindow
 ├── entries[]
 ├── currentIndex
 ├── failureCount
 ├── successCount
 └── slowCallCount
```

---

# 41. Java Enum

```java
public enum CallResult {

    SUCCESS,
    FAILURE,
    SLOW_SUCCESS,
    SLOW_FAILURE
}
```

---

# 42. Java Sliding Window Model

```java
public class SlidingWindow {

    private final CallResult[] window;

    private int currentIndex = 0;

    public SlidingWindow(int size) {

        this.window = new CallResult[size];
    }
}
```

---

# 43. Java Insert Logic

```java
public void record(CallResult result) {

    window[currentIndex] = result;

    currentIndex =
            (currentIndex + 1)
                    % window.length;
}
```

---

# 44. Modulo Mental Model

Pointer rotation:

genui{"math_block_widget_always_prefetch_v2":{"content":"nextIndex = (currentIndex + 1) \\bmod size"}}

---

# 45. Why Modulo Important

Provides:

```text
circular movement
```

Example:

```text
0 → 1 → 2 → 3 → 4 → 0
```

---

# 46. Java Failure Count

```java
public int failureCount() {

    int count = 0;

    for (CallResult result : window) {

        if (result == CallResult.FAILURE
                || result == CallResult.SLOW_FAILURE) {

            count++;
        }
    }

    return count;
}
```

---

# 47. Why Above Example Not Fully Optimal

This implementation scans:

```text
entire window
```

every request.

Complexity:

```text
O(N)
```

Real systems optimize further.

---

# 48. Production Optimization

Real implementations maintain:

```text
incremental counters
```

instead of full scans.

---

# 49. Incremental Counter Example

When overwriting old entry:

```text
remove old metrics
add new metrics
```

Very efficient.

---

# 50. Java Incremental Model

```java
private int failureCount;

public void updateCounters(
        CallResult oldResult,
        CallResult newResult) {

    if (oldResult == CallResult.FAILURE) {
        failureCount--;
    }

    if (newResult == CallResult.FAILURE) {
        failureCount++;
    }
}
```

---

# 51. Dry Run — Window Rotation

Window size:

```text
5
```

Initial:

```text
[S][F][S][F][S]
```

Current index:

```text
0
```

Insert:

```text
FAILURE
```

Updated:

```text
[F][F][S][F][S]
```

Index becomes:

```text
1
```

---

# 52. Dry Run — Failure Rate

Window:

```text
[F][F][S][F][S]
```

Failures:

```text
3
```

Total:

```text
5
```

Failure rate:

```text
60%
```

Threshold:

```text
50%
```

Result:

```text
OPEN
```

---

# 53. Resilience4j Count-Based Config

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 100
```

Meaning:

```text
track last 100 requests
```

---

# 54. Why Count-Based Good For Stable Traffic

If traffic volume stable:

```text
count-based windows behave predictably
```

Very common choice.

---

# 55. When Time-Based Better

If traffic bursts heavily:

```text
count-based windows may react strangely
```

Time-based windows sometimes better.

---

# 56. Thread Safety Problem

Multiple threads may update window simultaneously.

Need:

```text
atomic counters
CAS
locks
LongAdder
```

for correctness.

---

# 57. Production Concurrency

Real implementations use:

```text
lock-free algorithms
atomic updates
striped counters
```

for very high throughput.

---

# 58. Common Mistakes

## Mistake 1

```text
Scanning entire window every request
```

Too expensive.

---

## Mistake 2

```text
Using dynamic lists
```

Memory growth risk.

---

## Mistake 3

```text
No minimum calls
```

False positives.

---

## Mistake 4

```text
Ignoring slow calls
```

Latency degradation dangerous.

---

## Mistake 5

```text
Non-thread-safe updates
```

Metrics become incorrect.

---

# 59. Most Important Insight

```text
Sliding windows provide recent statistical health evaluation efficiently.
```

This is core production resiliency engineering.

---

# 60. Distributed Systems Insight

At scale:

```text
every microsecond matters
```

Circuit breaker algorithms must be:

```text
memory efficient
CPU efficient
lock efficient
```

---

# 61. Interview Explanation

If interviewer asks:

```text
How does count-based sliding window work?
```

Strong answer:

```text
Count-based sliding windows track metrics for the most recent N calls
using circular buffers or ring buffers. Old entries are overwritten as
new requests arrive, allowing O(1) memory usage and efficient recent
health evaluation.
```

Senior addition:

```text
Production implementations optimize using incremental counters and
lock-free concurrency primitives to avoid expensive full-window scans.
```

---

# 62. Final Mental Model

```text
Fixed Circular History
        ↓
Continuous Rotation
        ↓
Recent Health Calculation
```

---

# 63. What To Remember

```text
Count-based windows track recent N requests.

Sliding windows automatically evict old entries.

Ring buffers provide O(1) memory.

Modulo enables circular rotation.

Incremental counters improve performance.

Slow calls tracked alongside failures.

Sliding windows enable automatic recovery.

Production systems optimize for concurrency and latency.
```

---

# 64. Next File

```text
009_Time_Based_Window.md
```

Next you learn:

```text
time bucket windows
bucket rotation
time-based metrics
burst traffic handling
window expiration
bucket aggregation
production time-window algorithms
```
