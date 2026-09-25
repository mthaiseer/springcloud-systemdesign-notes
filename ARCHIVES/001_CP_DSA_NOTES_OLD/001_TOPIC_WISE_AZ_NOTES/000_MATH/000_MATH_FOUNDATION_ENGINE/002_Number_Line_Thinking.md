# 002_Number_Line_Thinking.md

# Number Line Thinking

---

# 1. Introduction

Many Competitive Programming problems become much easier when visualized on a number line.

Instead of thinking only with formulas, imagine numbers placed on a line:

```text
-5 ---- -2 ---- 0 ---- 3 ---- 8
```

This helps with:
- distance
- intervals
- ordering
- movement
- greedy
- binary search
- absolute values

---

# 2. Why Number Line Thinking Matters

Number line thinking helps transform:

```text
Complex problem
→ Geometric intuition
→ Simpler solution
```

Very useful in:
- greedy
- two pointers
- interval problems
- scheduling
- median optimization
- coordinate compression

---

# 3. Distance Between Numbers

Distance between two numbers:

```text
distance(a, b) = |a - b|
```

Example:

```text
distance(2, 7) = 5
distance(-3, 4) = 7
```

---

# 4. Absolute Value Intuition

Absolute value:

```text
|x|
```

means:

```text
distance from zero
```

Examples:

```text
|-5| = 5
|7| = 7
```

---

# 5. Key Observation

```text
|a - b|
```

means:

```text
distance between a and b
```

This interpretation appears everywhere in CP.

---

# 6. Median Minimizes Distance

One of the most important CP observations:

```text
Median minimizes total absolute distance.
```

Example:

```text
1 2 3 100
```

Best meeting point:
```text
2 or 3
```

NOT:
```text
average
```

Used heavily in:
- greedy
- optimization
- load balancing
- logistics

---

# 7. Interval Thinking

Intervals are naturally represented on number line.

Example:

```text
[l1, r1]
[l2, r2]
```

---

# 8. Interval Overlap Condition

Two intervals overlap if:

```text
max(l1, l2) <= min(r1, r2)
```

Example:

```text
[1,5]
[4,8]
```

Overlap:
```text
[4,5]
```

---

# 9. Merge Interval Intuition

Sort intervals from left to right:

```text
[1,3]
[2,5]
[7,9]
```

Merge overlapping intervals.

Used in:
- scheduling
- calendar systems
- booking systems
- event processing

---

# 10. Greedy + Number Line

Many greedy problems become:

```text
Sort
→ process left to right
```

Examples:
- interval scheduling
- meeting rooms
- minimum arrows
- train platforms

---

# 11. Binary Search Visualization

Binary search naturally works on ordered number line.

```text
low -------- mid -------- high
```

Decision:

```text
Go left?
or
Go right?
```

This is geometric partitioning.

---

# 12. Coordinate Compression

Sometimes coordinates are huge:

```text
10^18
```

Compress into:

```text
0,1,2,3...
```

while preserving order.

Used in:
- segment tree
- Fenwick tree
- sweep line

---

# 13. Closest Number Problems

Recognition:

```text
nearest value
minimum difference
closest point
```

Usually:
- sorting
- binary search
- two pointers
- ordered set

---

# 14. CP / FAANG Problem Forms

---

# Form 1 — Minimum Distance

## Problem

Find minimum difference between any two numbers.

---

## Observation

After sorting:

```text
closest numbers become adjacent
```

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minDifference(vector<int>& a) {

    sort(a.begin(), a.end());

    int ans = INT_MAX;

    for (int i = 1; i < a.size(); i++) {

        ans = min(ans, a[i] - a[i - 1]);
    }

    return ans;
}
```

Complexity:

```text
O(N log N)
```

---

# Form 2 — Interval Overlap

## Problem

Check if two intervals overlap.

---

## Observation

Intervals overlap if:

```text
max(l1,l2) <= min(r1,r2)
```

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool overlap(
    int l1,
    int r1,
    int l2,
    int r2
) {

    return max(l1, l2)
        <= min(r1, r2);
}
```

---

# Form 3 — Meeting Point Optimization

## Problem

Find point minimizing total distance.

---

## Observation

Median minimizes:

```text
sum of absolute differences
```

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minTotalDistance(vector<int>& a) {

    sort(a.begin(), a.end());

    int median = a[a.size() / 2];

    int total = 0;

    for (int x : a) {

        total += abs(x - median);
    }

    return total;
}
```

---

# 15. Real World Applications

| Real System | Usage |
|---|---|
| Google Maps | shortest distance |
| Uber | nearest driver |
| Booking systems | interval overlap |
| CPU scheduling | interval management |
| Trading systems | ordered ranges |
| CDN routing | nearest edge selection |

---

# 16. Common Traps

```text
1. Off-by-one intervals
2. Inclusive vs exclusive ranges
3. Negative coordinates
4. Overflow in distance
5. Forgetting sorting
6. Wrong overlap logic
7. Choosing average instead of median
```

---

# 17. Final Checklist

Before solving ask:

```text
1. Can I visualize numbers on line?
2. Is this distance problem?
3. Is interval overlap involved?
4. Is sorting useful?
5. Is nearest value important?
6. Is median useful?
7. Can binary search help?
```

---

# 18. Final Mental Shortcut

```text
Distance
+
Intervals
+
Ordering
+
Movement
=
Number Line Thinking
```
