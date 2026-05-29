# 024_TwoHeaps_Median_Stream_Engine.md

> MiniSTLEngine Advanced Phase 024  
> Topic: Two Heaps as a **Median Stream / Real-Time Analytics Engine** for CP, DSA, FAANG interviews, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Two Heaps Is An Engine](#2-why-two-heaps-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Core Two-Heaps Behavior](#4-core-two-heaps-behavior)
- [5. Invariants](#5-invariants)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Median Stream Engine](#8-basic-median-stream-engine)
- [9. Dry Run: Running Median](#9-dry-run-running-median)
- [10. CP Pattern 1: Find Median From Data Stream](#10-cp-pattern-1-find-median-from-data-stream)
- [11. CP Pattern 2: Running Median After Each Insert](#11-cp-pattern-2-running-median-after-each-insert)
- [12. CP Pattern 3: Sliding Window Median Preview](#12-cp-pattern-3-sliding-window-median-preview)
- [13. CP Pattern 4: Real-Time Latency Median](#13-cp-pattern-4-real-time-latency-median)
- [14. CP Pattern 5: Percentile Thinking](#14-cp-pattern-5-percentile-thinking)
- [15. Two Heaps vs Multiset](#15-two-heaps-vs-multiset)
- [16. Common Mistakes](#16-common-mistakes)
- [17. Complexity Table](#17-complexity-table)
- [18. Real-World Mapping](#18-real-world-mapping)
- [19. Final Mental Model](#19-final-mental-model)
- [20. Next Step](#20-next-step)

---

# 1. Goal

Learn two heaps not only as:

```text
median trick
```

but as a:

```text
Median Stream / Real-Time Analytics Engine
```

It helps solve:

```text
running median
stream median
online statistics
sliding window median foundation
latency monitoring
ranking split
low-half / high-half partitioning
```

---

# 2. Why Two Heaps Is An Engine

Median means:

```text
middle value of sorted data
```

But sorting after every insert is expensive.

Brute force:

```text
insert value
sort all values
take middle
```

Cost:

```text
O(N log N) per query
```

Two heaps maintain the middle dynamically.

Engine thinking:

```text
TwoHeapsMedianStreamEngine
    lower half stored in max heap
    upper half stored in min heap
    median available in O(1)
    insert in O(log N)
```

---

# 3. Real-System Mental Model

Real systems need medians:

```text
API latency median
payment processing time median
CPU load median
stock price median
sensor value median
request duration monitoring
real-time dashboards
```

Architecture:

```text
Incoming Metric Stream
        |
        v
TwoHeapsMedianStreamEngine
        |
        +--> insert metric
        +--> rebalance heaps
        +--> query median
        |
        v
Current Median Metric
```

---

# 4. Core Two-Heaps Behavior

Use two heaps:

```text
leftMaxHeap  = smaller half
rightMinHeap = larger half
```

Example sorted data:

```text
[1, 2, 3, 4, 5]
```

Split:

```text
leftMaxHeap  = [1, 2, 3]
rightMinHeap = [4, 5]
```

Median:

```text
max(leftMaxHeap) = 3
```

For even size:

```text
[1, 2, 3, 4]
```

Split:

```text
leftMaxHeap  = [1, 2]
rightMinHeap = [3, 4]
```

Median:

```text
(max(left) + min(right)) / 2
= (2 + 3) / 2
```

---

# 5. Invariants

Always maintain these rules:

```text
Invariant 1:
All values in leftMaxHeap <= all values in rightMinHeap.

Invariant 2:
leftMaxHeap.size() == rightMinHeap.size()
OR
leftMaxHeap.size() == rightMinHeap.size() + 1.

Invariant 3:
If total size is odd:
    median = leftMaxHeap.top()

If total size is even:
    median = average(leftMaxHeap.top(), rightMinHeap.top())
```

Why keep left one larger?

```text
It makes odd median simple.
```

---

# 6. CP/DSA Recognition

Use two heaps when problem says:

```text
running median
median from stream
online median
dynamic middle
insert numbers and query median
real-time statistic
```

Hidden mapping:

| Problem clue | Pattern |
|---|---|
| median after every insert | two heaps |
| online stream | two heaps |
| dynamic middle value | two heaps |
| sliding median | two heaps + lazy deletion |
| percentile approximation | heap/quantile structures |
| lower half / upper half | two heaps |

---

# 7. Engine Architecture

```text
MiniTwoHeapsMedianStreamEngine
├── left max heap
├── right min heap
├── insert router
├── rebalance engine
├── median query
├── stream analytics API
└── sliding window extension
```

---

# 8. Basic Median Stream Engine

## Step-by-Step Approach Before Code

```text
Step 1: Create max heap for left half.

Step 2: Create min heap for right half.

Step 3: When new value x arrives:
        if left is empty OR x <= left.top():
            push x into left.
        else:
            push x into right.

Step 4: Rebalance:
        if left has more than one extra element:
            move left.top() to right.
        if right has more elements:
            move right.top() to left.

Step 5: Query median:
        if sizes equal:
            median = average(left.top(), right.top()).
        else:
            median = left.top().
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class MedianStreamEngine {
private:

    // Max heap:
    // stores smaller half.
    priority_queue<int> leftMaxHeap;

    // Min heap:
    // stores larger half.
    priority_queue<
        int,
        vector<int>,
        greater<int>
    > rightMinHeap;

    void rebalance() {

        // Invariant:
        // left size can be equal to right size
        // OR exactly one greater.
        if (leftMaxHeap.size() >
            rightMinHeap.size() + 1) {

            rightMinHeap.push(leftMaxHeap.top());
            leftMaxHeap.pop();
        }

        if (rightMinHeap.size() >
            leftMaxHeap.size()) {

            leftMaxHeap.push(rightMinHeap.top());
            rightMinHeap.pop();
        }
    }

public:

    void addNumber(int x) {

        // Step 1:
        // Decide which half should receive x.
        if (leftMaxHeap.empty() ||
            x <= leftMaxHeap.top()) {

            leftMaxHeap.push(x);

        } else {

            rightMinHeap.push(x);
        }

        // Step 2:
        // Restore size balance.
        rebalance();
    }

    double getMedian() {

        if (leftMaxHeap.empty() &&
            rightMinHeap.empty()) {

            throw runtime_error("No numbers available");
        }

        if (leftMaxHeap.size() ==
            rightMinHeap.size()) {

            return (
                (double)leftMaxHeap.top() +
                (double)rightMinHeap.top()
            ) / 2.0;
        }

        return leftMaxHeap.top();
    }
};

int main() {

    MedianStreamEngine engine;

    engine.addNumber(1);
    cout << engine.getMedian() << endl;

    engine.addNumber(5);
    cout << engine.getMedian() << endl;

    engine.addNumber(3);
    cout << engine.getMedian() << endl;

    return 0;
}
```

---

# 9. Dry Run: Running Median

Input stream:

```text
1, 5, 3, 8
```

Initial:

```text
left = []
right = []
```

Insert `1`:

```text
left = [1]
right = []

median = 1
```

Insert `5`:

```text
5 > left.top(1)
push to right

left = [1]
right = [5]

median = (1 + 5) / 2 = 3
```

Insert `3`:

```text
3 > left.top(1)
push to right

right has more elements
move right.top(3) to left

left = [3,1]
right = [5]

median = left.top = 3
```

Insert `8`:

```text
8 > left.top(3)
push to right

left = [3,1]
right = [5,8]

median = (3 + 5) / 2 = 4
```

---

# 10. CP Pattern 1: Find Median From Data Stream

## Problem Type

```text
Design data structure:
addNum(num)
findMedian()
```

## Step-by-Step Approach Before Code

```text
Step 1: Keep smaller half in max heap.

Step 2: Keep larger half in min heap.

Step 3: Insert value into correct heap.

Step 4: Rebalance sizes.

Step 5: Median comes from heap tops.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class MedianFinder {
private:
    priority_queue<int> left;

    priority_queue<
        int,
        vector<int>,
        greater<int>
    > right;

public:
    void addNum(int num) {

        if (left.empty() || num <= left.top()) {
            left.push(num);
        } else {
            right.push(num);
        }

        if (left.size() > right.size() + 1) {
            right.push(left.top());
            left.pop();
        }

        if (right.size() > left.size()) {
            left.push(right.top());
            right.pop();
        }
    }

    double findMedian() {

        if (left.size() == right.size()) {
            return (
                (double)left.top() +
                (double)right.top()
            ) / 2.0;
        }

        return left.top();
    }
};

int main() {

    MedianFinder mf;

    mf.addNum(1);
    mf.addNum(2);

    cout << mf.findMedian() << endl;

    mf.addNum(3);

    cout << mf.findMedian() << endl;

    return 0;
}
```

---

# 11. CP Pattern 2: Running Median After Each Insert

## Problem Type

```text
Print median after every new number.
```

## Step-by-Step Approach Before Code

```text
Step 1: Initialize MedianFinder.

Step 2: For every number in stream:
        add number.

Step 3: Immediately query median.

Step 4: Store or print median.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class RunningMedianEngine {
private:
    priority_queue<int> left;

    priority_queue<
        int,
        vector<int>,
        greater<int>
    > right;

    void rebalance() {

        if (left.size() > right.size() + 1) {
            right.push(left.top());
            left.pop();
        }

        if (right.size() > left.size()) {
            left.push(right.top());
            right.pop();
        }
    }

public:
    void add(int x) {

        if (left.empty() || x <= left.top()) {
            left.push(x);
        } else {
            right.push(x);
        }

        rebalance();
    }

    double median() {

        if (left.size() == right.size()) {
            return (
                (double)left.top() +
                (double)right.top()
            ) / 2.0;
        }

        return left.top();
    }
};

int main() {

    vector<int> stream = {
        5, 15, 1, 3
    };

    RunningMedianEngine engine;

    for (int x : stream) {

        engine.add(x);

        cout << "after adding "
             << x
             << ", median = "
             << engine.median()
             << endl;
    }

    return 0;
}
```

---

# 12. CP Pattern 3: Sliding Window Median Preview

## Problem Type

```text
Median for every sliding window of size K.
```

Two heaps alone are not enough because:

```text
expired elements may be buried inside heap
```

Need:

```text
two heaps + lazy deletion
```

That is why next engine is:

```text
025_LazyDeletion_Heap_Set_Engine.md
```

## Step-by-Step Idea

```text
Step 1: Add new number to one of two heaps.

Step 2: Mark expired number for deletion.

Step 3: When expired number reaches heap top,
        physically remove it.

Step 4: Rebalance valid heap sizes.

Step 5: Query median.
```

This is advanced but very important.

---

# 13. CP Pattern 4: Real-Time Latency Median

## Real-System Type

```text
Monitor median API latency from incoming request durations.
```

Example stream:

```text
120ms, 90ms, 300ms, 110ms
```

Median tells:

```text
typical user experience
```

Unlike average, median is robust against spikes.

## Step-by-Step Approach Before Code

```text
Step 1: Each latency sample enters stream.

Step 2: Add latency to MedianStreamEngine.

Step 3: Query current median.

Step 4: Emit metric to dashboard.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class LatencyMedianMonitor {
private:
    priority_queue<int> lower;

    priority_queue<
        int,
        vector<int>,
        greater<int>
    > upper;

    void rebalance() {

        if (lower.size() > upper.size() + 1) {
            upper.push(lower.top());
            lower.pop();
        }

        if (upper.size() > lower.size()) {
            lower.push(upper.top());
            upper.pop();
        }
    }

public:
    void recordLatency(int latencyMs) {

        if (lower.empty() ||
            latencyMs <= lower.top()) {

            lower.push(latencyMs);

        } else {

            upper.push(latencyMs);
        }

        rebalance();
    }

    double currentMedianLatency() {

        if (lower.size() == upper.size()) {
            return (
                (double)lower.top() +
                (double)upper.top()
            ) / 2.0;
        }

        return lower.top();
    }
};

int main() {

    LatencyMedianMonitor monitor;

    vector<int> latencies = {
        120, 90, 300, 110
    };

    for (int latency : latencies) {

        monitor.recordLatency(latency);

        cout << "current median latency = "
             << monitor.currentMedianLatency()
             << " ms\n";
    }

    return 0;
}
```

---

# 14. CP Pattern 5: Percentile Thinking

Median is:

```text
50th percentile
```

Two heaps exactly maintain:

```text
middle split
```

For advanced systems, percentiles like:

```text
p90
p95
p99
```

are often estimated using:

```text
histograms
TDigest
HDR histogram
quantile sketches
```

But the mental model starts here:

```text
split stream into lower half and upper half
```

Real systems:

```text
latency dashboards
SLO monitoring
payment processing time
search response time
```

---

# 15. Two Heaps vs Multiset

| Feature | Two Heaps | Multiset |
|---|---|---|
| Running median | excellent | okay |
| Median query | O(1) | O(N) if using next(begin,k) |
| Insert | O(log N) | O(log N) |
| Delete arbitrary | hard | easy |
| Sliding window median | needs lazy deletion | easier with multiset |
| Interview popularity | very high | medium |

Rule:

```text
Only insert + median?
→ two heaps

Insert + delete arbitrary value?
→ multiset or lazy deletion
```

---

# 16. Common Mistakes

## Mistake 1: Not Rebalancing

If heap sizes drift too far:

```text
median becomes wrong
```

Always keep:

```text
left.size() == right.size()
or
left.size() == right.size() + 1
```

---

## Mistake 2: Wrong Heap Types

Left heap must be:

```text
max heap
```

Right heap must be:

```text
min heap
```

---

## Mistake 3: Integer Overflow In Average

Wrong:

```cpp
(left.top() + right.top()) / 2.0
```

If values are large, sum can overflow int.

Safer:

```cpp
((double)left.top() + (double)right.top()) / 2.0
```

---

## Mistake 4: Trying Sliding Window Median Without Lazy Deletion

Expired element may not be at heap top.

So direct removal is not possible from `priority_queue`.

Need:

```text
lazy deletion
```

or:

```text
multiset
```

---

# 17. Complexity Table

| Operation | Complexity |
|---|---:|
| add number | O(log N) |
| rebalance | O(log N) |
| find median | O(1) |
| space | O(N) |
| running median for N values | O(N log N) |

---

# 18. Real-World Mapping

| Two Heaps Concept | Real-System Meaning |
|---|---|
| left max heap | lower half of metrics |
| right min heap | upper half of metrics |
| median query | real-time p50 |
| rebalance | distribution maintenance |
| stream insert | incoming telemetry |
| latency monitor | API dashboard |
| sliding median | rolling analytics |
| lazy deletion | expired event cleanup |

---

# 19. Final Mental Model

Two heaps are:

```text
real-time median partition engine
```

Best for:

```text
running median
online statistics
stream analytics
real-time dashboards
FAANG design problems
```

One-line CP rule:

```text
If numbers arrive one by one and median is queried repeatedly, think two heaps.
```

One-line system rule:

```text
Two heaps maintain real-time p50 metrics by splitting the stream into lower and upper halves.
```

---

# 20. Next Step

Next file:

```text
025_LazyDeletion_Heap_Set_Engine.md
```

That completes the advanced heap/median stream story.
