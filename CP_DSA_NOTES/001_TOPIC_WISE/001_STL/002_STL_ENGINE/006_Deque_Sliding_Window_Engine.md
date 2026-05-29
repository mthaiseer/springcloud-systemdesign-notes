# 006_Deque_Sliding_Window_Engine.md

> MiniSTLEngine Phase 006  
> Topic: `deque` as a **Sliding Window / Stream Optimization Engine** for CP, DSA, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Deque Is An Engine](#2-why-deque-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Deque Core Behavior](#4-deque-core-behavior)
- [5. Deque Operations Cheat Sheet](#5-deque-operations-cheat-sheet)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Sliding Window Engine](#8-basic-sliding-window-engine)
- [9. Dry Run: Push Front And Back](#9-dry-run-push-front-and-back)
- [10. CP Pattern 1: Sliding Window Maximum](#10-cp-pattern-1-sliding-window-maximum)
- [11. CP Pattern 2: Sliding Window Minimum](#11-cp-pattern-2-sliding-window-minimum)
- [12. CP Pattern 3: First Negative Number In Window](#12-cp-pattern-3-first-negative-number-in-window)
- [13. CP Pattern 4: Monotonic Deque](#13-cp-pattern-4-monotonic-deque)
- [14. CP Pattern 5: Stream Processing Engine](#14-cp-pattern-5-stream-processing-engine)
- [15. Deque vs Queue vs Stack](#15-deque-vs-queue-vs-stack)
- [16. Common Mistakes](#16-common-mistakes)
- [17. Complexity Table](#17-complexity-table)
- [18. Real-World Mapping](#18-real-world-mapping)
- [19. Final Mental Model](#19-final-mental-model)
- [20. Next Step](#20-next-step)

---

# 1. Goal

Learn `deque` not only as double-ended queue syntax, but as a:

```text
Sliding Window / Stream Optimization Engine
```

It helps solve:

```text
sliding window maximum
sliding window minimum
stream processing
window analytics
real-time monitoring
range optimization
monotonic queue problems
```

---

# 2. Why Deque Is An Engine

Deque means:

```text
Double Ended Queue
```

You can:

```text
insert/remove from front
insert/remove from back
```

efficiently.

This makes deque perfect for:

```text
dynamic windows
stream processing
maintaining candidates
window extrema
```

Normal thinking:

```cpp
deque<int> dq;
```

Engine thinking:

```text
SlidingWindowEngine
    maintains active window
    removes expired items
    keeps only useful candidates
    supports O(1) window queries
```

---

# 3. Real-System Mental Model

Deque-like systems appear in:

```text
Prometheus sliding metrics
real-time dashboards
stream analytics
sensor monitoring
rolling averages
network traffic analysis
stock window analysis
```

Architecture:

```text
Incoming Stream
      |
      v
SlidingWindowEngine
      |
      +--> add new event
      +--> remove expired event
      +--> maintain best candidates
      |
      v
Current Window Result
```

---

# 4. Deque Core Behavior

Example:

```text
push_back 10
push_back 20
push_front 5
```

Deque:

```text
front -> 5 10 20 <- back
```

Possible operations:

```text
pop_front -> removes 5
pop_back  -> removes 20
```

Key advantage:

```text
efficient operations at both ends
```

---

# 5. Deque Operations Cheat Sheet

```cpp
deque<int> dq;

dq.push_back(10);
dq.push_front(5);

dq.pop_back();
dq.pop_front();

dq.front();
dq.back();

dq.empty();
dq.size();
```

All these operations are:

```text
O(1)
```

---

# 6. CP/DSA Recognition

Use deque when problem says:

```text
sliding window maximum
sliding window minimum
window queries
real-time max/min
stream optimization
maintain candidates dynamically
```

Hidden mapping:

| Problem clue | Deque pattern |
|---|---|
| sliding max/min | monotonic deque |
| dynamic window | deque |
| streaming metrics | deque |
| remove expired items | deque front removal |
| maintain best candidate | monotonic deque |

---

# 7. Engine Architecture

```text
MiniDequeSlidingWindowEngine
├── window insert
├── window expire
├── max candidate maintenance
├── min candidate maintenance
├── stream analytics
├── rolling metric engine
└── monotonic deque optimization
```

---

# 8. Basic Sliding Window Engine

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class SlidingWindowEngine {
private:
    deque<int> dq;

public:

    void addBack(int value) {

        // New event enters stream window.
        dq.push_back(value);
    }

    void addFront(int value) {

        dq.push_front(value);
    }

    void removeFront() {

        if (!dq.empty()) {
            dq.pop_front();
        }
    }

    void removeBack() {

        if (!dq.empty()) {
            dq.pop_back();
        }
    }

    int frontValue() {

        if (dq.empty()) {
            throw runtime_error("Deque empty");
        }

        return dq.front();
    }

    int backValue() {

        if (dq.empty()) {
            throw runtime_error("Deque empty");
        }

        return dq.back();
    }

    void printWindow() {

        cout << "Window: ";

        for (int x : dq) {
            cout << x << " ";
        }

        cout << endl;
    }
};

int main() {

    SlidingWindowEngine engine;

    engine.addBack(10);
    engine.addBack(20);
    engine.addFront(5);

    engine.printWindow();

    engine.removeFront();

    engine.printWindow();

    return 0;
}
```

---

# 9. Dry Run: Push Front And Back

Operations:

```text
push_back 10
push_back 20
push_front 5
```

Deque:

```text
front -> 5 10 20 <- back
```

Then:

```text
pop_front
```

Deque:

```text
front -> 10 20 <- back
```

Then:

```text
pop_back
```

Deque:

```text
front -> 10 <- back
```

---

# 10. CP Pattern 1: Sliding Window Maximum

## Problem Type

```text
Find maximum for every window of size K.
```

Example:

```text
nums = [1,3,-1,-3,5,3,6,7]
k = 3

answer:
[3,3,5,5,6,7]
```

---

## Core Idea

Deque stores:

```text
indices
```

not values.

Maintain deque in:

```text
decreasing order of values
```

Why?

```text
Smaller elements behind larger elements
can never become future maximums.
```

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> maxSlidingWindow(vector<int>& nums, int k) {

    deque<int> dq;

    vector<int> answer;

    for (int i = 0; i < (int)nums.size(); i++) {

        // Remove indices outside current window.
        while (!dq.empty() && dq.front() <= i - k) {
            dq.pop_front();
        }

        // Maintain decreasing deque.
        // Smaller elements behind current value
        // can never become future maximums.
        while (!dq.empty() &&
               nums[dq.back()] <= nums[i]) {

            dq.pop_back();
        }

        dq.push_back(i);

        // Window formed.
        if (i >= k - 1) {

            // Front stores maximum element index.
            answer.push_back(nums[dq.front()]);
        }
    }

    return answer;
}

int main() {

    vector<int> nums = {
        1,3,-1,-3,5,3,6,7
    };

    int k = 3;

    vector<int> ans =
        maxSlidingWindow(nums, k);

    for (int x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 11. CP Pattern 2: Sliding Window Minimum

Exactly same logic.

Difference:

```text
Maintain increasing deque
instead of decreasing deque.
```

Why?

```text
Larger elements behind smaller elements
can never become future minimums.
```

---

# 12. CP Pattern 3: First Negative Number In Window

## Problem Type

```text
For every window, find first negative number.
```

Deque stores:

```text
indices of negative numbers
```

Front always gives:

```text
earliest negative still inside window
```

---

# 13. CP Pattern 4: Monotonic Deque

Most important deque pattern.

## Monotonic Increasing

```text
front <= ... <= back
```

Useful for:

```text
window minimum
DP optimization
```

## Monotonic Decreasing

```text
front >= ... >= back
```

Useful for:

```text
window maximum
best candidate queries
```

---

# 14. CP Pattern 5: Stream Processing Engine

## Problem Type

```text
Continuous incoming data stream.
```

Examples:

```text
CPU metrics
network packets
sensor values
stock prices
```

Deque acts like:

```text
real-time rolling analytics engine
```

Window logic:

```text
add new data
remove expired data
maintain optimized candidates
```

---

# 15. Deque vs Queue vs Stack

| Feature | Queue | Stack | Deque |
|---|---|---|---|
| Ends usable | front/back limited | top only | both ends |
| Order | FIFO | LIFO | flexible |
| Main use | BFS/events | parsing | sliding windows |
| Window optimization | weak | weak | excellent |
| Stream analytics | limited | limited | strong |

Mental shortcut:

```text
Need both-end operations?
→ deque
```

---

# 16. Common Mistakes

## Mistake 1: Storing Values Instead Of Indices

Wrong:

```cpp
dq.push_back(nums[i]);
```

Correct:

```cpp
dq.push_back(i);
```

Why?

```text
Need to know whether element expired from window.
```

---

## Mistake 2: Forgetting Expired Elements

Wrong logic leaves old indices inside deque.

Always:

```cpp
while (!dq.empty() &&
       dq.front() <= i - k)
```

---

## Mistake 3: Wrong Monotonic Direction

For maximum:

```text
decreasing deque
```

For minimum:

```text
increasing deque
```

---

## Mistake 4: Thinking Complexity Is O(N*K)

Each element:

```text
enters deque once
leaves deque once
```

Total:

```text
O(N)
```

not:

```text
O(N*K)
```

---

# 17. Complexity Table

| Operation | Complexity |
|---|---:|
| push_front | O(1) |
| push_back | O(1) |
| pop_front | O(1) |
| pop_back | O(1) |
| front/back access | O(1) |
| sliding window max/min | O(N) |
| deque space | O(K) |

---

# 18. Real-World Mapping

| Deque Concept | Real-System Meaning |
|---|---|
| sliding window | rolling analytics |
| remove expired item | event expiration |
| monotonic deque | optimized candidate cache |
| window max/min | monitoring dashboards |
| stream analytics | Prometheus/Grafana style metrics |
| rolling statistics | real-time monitoring |
| candidate pruning | optimization engine |

---

# 19. Final Mental Model

Deque is:

```text
dynamic sliding window optimization engine
```

Best for:

```text
window max/min
stream processing
rolling analytics
candidate maintenance
real-time optimization
```

One-line CP rule:

```text
If window moves continuously and needs fast extrema, think monotonic deque.
```

One-line system rule:

```text
Deque powers real-time rolling analytics by efficiently maintaining active candidates.
```

---

# 20. Next Step

Next file:

```text
007_PriorityQueue_Scheduler_Engine.md
```

Then:

```text
008_Set_Ordered_Index_Engine.md
009_Map_KeyValue_Index_Engine.md
010_UnorderedMap_Hash_Index_Engine.md
```
