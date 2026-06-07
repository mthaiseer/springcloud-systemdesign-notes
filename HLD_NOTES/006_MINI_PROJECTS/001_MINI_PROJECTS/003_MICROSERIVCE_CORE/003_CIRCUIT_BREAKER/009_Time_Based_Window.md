# 009_Time_Based_Window.md

# MiniCircuitBreaker — 009 Time Based Window

---

# 1. Why This File Exists

Previous file explained:

```text
count-based sliding window
```

Count-based window tracks:

```text
last N calls
```

Example:

```text
last 100 requests
```

But real microservice traffic is not always smooth.

Traffic can be:

```text
bursty
uneven
low during night
high during peak time
spiky during incidents
```

In these systems, count-based windows may react too quickly or too slowly.

So circuit breakers also support:

```text
time-based sliding windows
```

A time-based window tracks:

```text
recent time duration
```

Example:

```text
last 60 seconds
```

This file explains:

```text
time-based sliding window
time buckets
bucket rotation
rolling metrics
window expiration
failure rate calculation
slow call rate calculation
burst traffic handling
Resilience4j config
Java implementation
dry runs
production tradeoffs
```

---

# 2. One-Line Definition

```text
Time-based sliding window tracks circuit breaker metrics over the most recent time duration.
```

Example:

```text
last 60 seconds
```

NOT:

```text
last 60 requests
```

---

# 3. Biggest Mental Model

```text
Recent Time
    ↓
Split Into Buckets
    ↓
Record Calls Per Bucket
    ↓
Expire Old Buckets
    ↓
Calculate Current Health
```

---

# 4. Count-Based vs Time-Based

## Count-Based Window

```text
last N calls
```

Example:

```text
last 100 calls
```

Good when traffic is stable.

---

## Time-Based Window

```text
last N seconds
```

Example:

```text
last 60 seconds
```

Good when traffic is bursty or uneven.

---

# 5. Why Count-Based Can Be Problematic

Suppose count window:

```text
last 100 calls
```

During burst:

```text
100 calls arrive in 1 second
```

The full window is replaced very quickly.

Circuit breaker may react too aggressively.

---

# 6. Burst Traffic ASCII

```text
Normal:
10 calls / second

Burst:
1000 calls / second
```

Count-based window can change instantly.

Time-based window smooths over:

```text
actual time duration
```

---

# 7. Time-Based Window Example

Config:

```text
window = 60 seconds
```

Circuit breaker tracks:

```text
all calls during last 60 seconds
```

As time moves forward:

```text
old seconds expire
new seconds enter
```

---

# 8. Time Window ASCII

```text
Last 60 Seconds

|------------------------------------------------------------|
t-59                                                       now
```

Only calls inside this range count.

---

# 9. Bucket Concept

Tracking every individual timestamp is expensive.

Instead, time window is divided into:

```text
buckets
```

Example:

```text
60-second window
1-second buckets
```

This gives:

```text
60 buckets
```

---

# 10. Bucket Layout ASCII

```text
Window = 10 seconds

[0][1][2][3][4][5][6][7][8][9]

Each bucket = 1 second
```

Each bucket stores metrics for one small time slice.

---

# 11. What Each Bucket Stores

Each bucket stores:

```text
total calls
successful calls
failed calls
slow calls
```

---

# 12. Bucket Data ASCII

```text
Bucket
 ├── totalCalls
 ├── successCalls
 ├── failedCalls
 └── slowCalls
```

---

# 13. Rolling Window

As time moves:

```text
old bucket expires
new bucket starts
```

Example:

```text
[10:00:01][10:00:02][10:00:03]
```

After one second:

```text
[10:00:02][10:00:03][10:00:04]
```

---

# 14. Rolling Window ASCII

```text
Time →
[A][B][C][D][E]

Next second:

[B][C][D][E][F]
```

Old bucket `A` expires.

New bucket `F` starts.

---

# 15. Why Bucket Expiration Important

Old failures should not affect health forever.

If service failed earlier but is now healthy:

```text
old failure buckets expire
failure rate decreases
```

This allows natural recovery.

---

# 16. Recovery Example

Old window:

```text
[F][F][F][S][S]
```

After healthy traffic:

```text
[S][S][S][S][S]
```

Failure rate becomes:

```text
0%
```

---

# 17. Time-Based Failure Rate

Failure rate is calculated from active buckets only.

Formula:

```text
Failure Rate = (Failed Calls / Total Calls) × 100
```

---

# 18. Failure Rate Example

Window:

```text
last 60 seconds
```

Metrics:

```text
Total Calls = 1000
Failed Calls = 200
```

Calculation:

```text
Failure Rate = (200 / 1000) × 100 = 20%
```

---

# 19. Threshold Decision Example

Config:

```text
failure threshold = 50%
```

Current:

```text
failure rate = 20%
```

Result:

```text
stay CLOSED
```

---

# 20. Unhealthy Example

Metrics:

```text
Total Calls = 1000
Failed Calls = 700
```

Calculation:

```text
Failure Rate = (700 / 1000) × 100 = 70%
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

# 21. Slow Call Rate In Time Window

Circuit breaker also tracks slow calls.

Formula:

```text
Slow Call Rate = (Slow Calls / Total Calls) × 100
```

---

# 22. Slow Call Example

Metrics:

```text
Total Calls = 1000
Slow Calls = 600
```

Calculation:

```text
Slow Call Rate = (600 / 1000) × 100 = 60%
```

If threshold:

```text
50%
```

Result:

```text
OPEN
```

---

# 23. Why Slow Calls Matter

Slow calls can cause:

```text
thread blocking
connection pool exhaustion
latency amplification
queue growth
```

A service can be technically successful but operationally dangerous.

---

# 24. Time-Based Window Execution Flow

```text
Request Completes
      ↓
Find Current Time Bucket
      ↓
Record Success / Failure / Slow
      ↓
Expire Old Buckets
      ↓
Aggregate Metrics
      ↓
Evaluate Thresholds
```

---

# 25. Bucket Rotation

When time advances:

```text
current bucket changes
```

Old bucket is reset.

---

# 26. Bucket Rotation ASCII

```text
Current second = 10:00:05

[01][02][03][04][05]
                 ^

Next second = 10:00:06

[02][03][04][05][06]
                 ^
```

---

# 27. Circular Bucket Array

Instead of creating infinite buckets:

```text
reuse fixed array
```

Example:

```text
bucket index = timestamp % bucketCount
```

---

# 28. Circular Array ASCII

```text
Index:
0 1 2 3 4

Buckets:
[A][B][C][D][E]

After index 4:
go back to 0
```

---

# 29. Circular Formula

```text
bucketIndex = currentSecond % bucketCount
```

Example:

```text
currentSecond = 125
bucketCount = 60

bucketIndex = 125 % 60 = 5
```

---

# 30. Why Circular Buckets Are Powerful

They provide:

```text
fixed memory
fast access
automatic reuse
predictable performance
```

This is critical in high-QPS systems.

---

# 31. Bucket Timestamp Problem

If bucket index is reused, we must know:

```text
is this bucket from current time window?
or old stale time?
```

So each bucket stores:

```text
bucket timestamp
```

---

# 32. Timestamp Example

Bucket index 5 may contain old data from:

```text
10:00:05
```

Later it may be reused for:

```text
10:01:05
```

Need to reset stale data.

---

# 33. Bucket Reset Rule

If bucket timestamp does not match current time slice:

```text
reset bucket
set new timestamp
```

---

# 34. Reset ASCII

```text
Old Bucket:
timestamp = 10:00:05
failedCalls = 10

Now:
timestamp = 10:01:05

Reset:
failedCalls = 0
```

---

# 35. Aggregation Strategy

Two ways:

```text
scan buckets when needed
maintain pre-aggregated counters
```

---

# 36. Simple Aggregation

Simple method:

```text
sum all active buckets
```

Cost:

```text
O(bucket count)
```

Usually fine if bucket count small.

---

# 37. Optimized Aggregation

Production method:

```text
maintain rolling totals
```

When bucket expires:

```text
subtract old bucket metrics
```

When new call arrives:

```text
add new metrics
```

This gives:

```text
O(1)
```

updates.

---

# 38. Aggregation ASCII

```text
Old Bucket Expires
        ↓
Subtract Old Metrics

New Call Arrives
        ↓
Add New Metrics
```

---

# 39. Time-Based Window Internal Data

```text
TimeBasedWindow
 ├── buckets[]
 ├── bucketCount
 ├── bucketDuration
 ├── rollingTotalCalls
 ├── rollingFailedCalls
 ├── rollingSlowCalls
 └── currentBucketIndex
```

---

# 40. Java Bucket Model

```java
public class TimeBucket {

    long bucketSecond;

    int totalCalls;

    int successCalls;

    int failedCalls;

    int slowCalls;

    public void reset(long newSecond) {

        this.bucketSecond = newSecond;
        this.totalCalls = 0;
        this.successCalls = 0;
        this.failedCalls = 0;
        this.slowCalls = 0;
    }
}
```

---

# 41. Java Time Window Constructor

```java
public class TimeBasedWindow {

    private final TimeBucket[] buckets;

    public TimeBasedWindow(int bucketCount) {

        this.buckets =
                new TimeBucket[bucketCount];

        for (int i = 0; i < bucketCount; i++) {

            buckets[i] =
                    new TimeBucket();

            buckets[i].reset(-1);
        }
    }
}
```

---

# 42. Java Bucket Index Calculation

```java
private int bucketIndex(
        long epochSecond) {

    return (int) (
            epochSecond % buckets.length
    );
}
```

---

# 43. Java Get Current Bucket

```java
private TimeBucket currentBucket() {

    long nowSecond =
            System.currentTimeMillis()
                    / 1000;

    int index =
            bucketIndex(nowSecond);

    TimeBucket bucket =
            buckets[index];

    if (bucket.bucketSecond != nowSecond) {

        bucket.reset(nowSecond);
    }

    return bucket;
}
```

---

# 44. Java Record Success

```java
public void recordSuccess() {

    TimeBucket bucket =
            currentBucket();

    bucket.totalCalls++;
    bucket.successCalls++;
}
```

---

# 45. Java Record Failure

```java
public void recordFailure() {

    TimeBucket bucket =
            currentBucket();

    bucket.totalCalls++;
    bucket.failedCalls++;
}
```

---

# 46. Java Record Slow Call

```java
public void recordSlowCall() {

    TimeBucket bucket =
            currentBucket();

    bucket.totalCalls++;
    bucket.slowCalls++;
}
```

---

# 47. Java Aggregate Total Calls

```java
public int totalCalls() {

    int total = 0;

    long nowSecond =
            System.currentTimeMillis()
                    / 1000;

    for (TimeBucket bucket : buckets) {

        if (nowSecond - bucket.bucketSecond
                < buckets.length) {

            total += bucket.totalCalls;
        }
    }

    return total;
}
```

---

# 48. Java Aggregate Failures

```java
public int failedCalls() {

    int total = 0;

    long nowSecond =
            System.currentTimeMillis()
                    / 1000;

    for (TimeBucket bucket : buckets) {

        if (nowSecond - bucket.bucketSecond
                < buckets.length) {

            total += bucket.failedCalls;
        }
    }

    return total;
}
```

---

# 49. Java Failure Rate

```java
public double failureRate() {

    int total =
            totalCalls();

    if (total == 0) {

        return 0.0;
    }

    return (failedCalls() * 100.0)
            / total;
}
```

---

# 50. Java Dry Run

Window:

```text
5 seconds
```

Buckets:

```text
second 1: total=10 failed=2
second 2: total=10 failed=1
second 3: total=10 failed=0
second 4: total=10 failed=2
second 5: total=10 failed=5
```

Totals:

```text
total calls = 50
failed calls = 10
```

Failure rate:

```text
(10 / 50) × 100 = 20%
```

---

# 51. Dry Run — Old Bucket Expired

At time:

```text
second 10
```

Window size:

```text
5 seconds
```

Valid buckets:

```text
6, 7, 8, 9, 10
```

Bucket from second 4 is ignored.

---

# 52. Resilience4j Time-Based Config

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        slidingWindowType: TIME_BASED
        slidingWindowSize: 60
        minimumNumberOfCalls: 20
        failureRateThreshold: 50
        slowCallRateThreshold: 50
```

---

# 53. Config Meaning

```text
Track last 60 seconds
Need at least 20 calls
Open if 50% failed
Open if 50% slow calls
```

---

# 54. When To Use Time-Based Window

Use when traffic is:

```text
bursty
uneven
seasonal
peak/off-peak
batch-driven
```

---

# 55. When Count-Based Is Better

Use count-based when:

```text
traffic is stable
you want fixed sample size
you want simpler reasoning
```

---

# 56. Bucket Granularity

Bucket size matters.

Large buckets:

```text
less CPU
less precision
```

Small buckets:

```text
more precision
more overhead
```

---

# 57. Production Recommendation

Common approach:

```text
1-second buckets
60-second window
```

Good balance between:

```text
precision
performance
memory
```

---

# 58. Thread Safety Problem

Multiple threads may update same bucket.

Need:

```text
atomic counters
LongAdder
locks
CAS
```

Without thread safety:

```text
metrics become wrong
```

---

# 59. Production Optimization

Real systems optimize using:

```text
LongAdder counters
bucket-level locks
pre-aggregated totals
lock-free structures
```

---

# 60. Common Mistakes

## Mistake 1

```text
No bucket timestamp
```

Old stale buckets reused incorrectly.

---

## Mistake 2

```text
No bucket expiration
```

Old failures remain forever.

---

## Mistake 3

```text
Creating new buckets constantly
```

Causes GC overhead.

---

## Mistake 4

```text
Too many tiny buckets
```

High CPU overhead.

---

## Mistake 5

```text
Non-thread-safe updates
```

Incorrect failure rate.

---

# 61. Most Important Insight

```text
Time-based windows give operational health over real time.
```

They are ideal for:

```text
monitoring-style resiliency decisions
```

---

# 62. Distributed Systems Insight

At scale, traffic is rarely smooth.

Time windows give better behavior for:

```text
bursts
traffic spikes
incident detection
recovery monitoring
```

---

# 63. Interview Explanation

If interviewer asks:

```text
How does a time-based sliding window work?
```

Strong answer:

```text
A time-based sliding window divides a recent time duration into buckets,
such as one bucket per second. Each bucket records metrics like success,
failure, and slow calls. As time advances, old buckets expire and new
buckets are reused, allowing recent health calculation over rolling time.
```

Senior addition:

```text
Production implementations use circular bucket arrays, timestamps,
pre-aggregated counters, and thread-safe updates to avoid excessive CPU
and memory overhead.
```

---

# 64. Final Mental Model

```text
Rolling Time Buckets
         ↓
Old Buckets Expire
         ↓
Recent Health Calculated
```

---

# 65. What To Remember

```text
Time-based windows track recent time duration.

They use buckets.

Buckets store call metrics.

Old buckets expire automatically.

Circular arrays reuse memory.

Bucket timestamps prevent stale data bugs.

Better for bursty traffic.

Failure/slow-call rates are calculated over active buckets.

Thread safety is critical.

Production systems optimize for O(1) updates.
```

---

# 66. Next File

```text
010_Failure_Rate_And_Slow_Call_Rate.md
```

Next you learn:

```text
combined health signals
failure vs slow-call analysis
latency degradation detection
early warning signals
real production outage patterns
advanced threshold strategies
```
