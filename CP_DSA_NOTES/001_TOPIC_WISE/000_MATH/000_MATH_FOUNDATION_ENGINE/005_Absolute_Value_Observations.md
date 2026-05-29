# 005_Absolute_Value_Observations.md

# Absolute Value Observations For Competitive Programming

---

# 1. Introduction

Absolute value is one of the most important mathematical ideas in CP.

Definition:

```text
|x|
```

means:

```text
distance from zero
```

Examples:

```text
|5| = 5
|-5| = 5
|0| = 0
```

---

# 2. Core Interpretation

Most important interpretation:

```text
|a - b|
```

means:

```text
distance between a and b
```

This single observation appears everywhere:
- greedy
- binary search
- geometry
- median problems
- DP
- graphs
- intervals

---

# 3. Number Line Visualization

Example:

```text
1 -------- 7
```

Distance:

```text
|1 - 7| = 6
```

Similarly:

```text
|-2 - 5| = 7
```

Always think geometrically.

---

# 4. Important Properties

---

# A. Non-Negative

```text
|x| >= 0
```

Absolute value can never be negative.

---

# B. Symmetry

```text
|a - b| = |b - a|
```

Distance is symmetric.

---

# C. Zero Condition

```text
|x| = 0
```

only if:

```text
x = 0
```

---

# D. Triangle Inequality

```text
|a + b| <= |a| + |b|
```

Very important in:
- geometry
- proofs
- optimization

---

# 5. Piecewise Definition

```text
|x| =
x      if x >= 0
-x     if x < 0
```

Useful when:
- removing abs manually
- deriving formulas
- handling intervals

---

# 6. Why Absolute Value Is Important In CP

Absolute value often indicates:

```text
distance
movement
difference
cost
optimization
```

Recognition signals:
- minimum distance
- nearest point
- equalize array
- movement cost
- median optimization

---

# 7. Median Minimizes Absolute Distance

One of the MOST important observations.

Problem:

```text
Choose point minimizing:
Σ |x - ai|
```

Optimal answer:

```text
median
```

NOT:
```text
average
```

---

# Example

Array:

```text
1 2 3 100
```

Median:

```text
2 or 3
```

Total distance minimal there.

---

# 8. Why Median Works

Moving away from median increases distance on one side faster than decreases on other side.

This appears heavily in:
- greedy
- load balancing
- logistics
- clustering

---

# 9. Absolute Difference Minimization

Classic problem:

```text
Find minimum |a[i] - a[j]|
```

Observation:

```text
Closest values become adjacent after sorting.
```

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minAbsoluteDifference(vector<int>& a) {

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

# 10. Equalizing Array Problems

Problem:

```text
Minimum moves to make all elements equal.
```

Cost:

```text
|x - ai|
```

Optimal target:

```text
median
```

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long minMoves(vector<int>& a) {

    sort(a.begin(), a.end());

    int median = a[a.size() / 2];

    long long ans = 0;

    for (int x : a) {

        ans += abs(x - median);
    }

    return ans;
}
```

---

# 11. Manhattan Distance

Very common in grids.

Formula:

```text
|x1 - x2| + |y1 - y2|
```

Used in:
- grid BFS
- nearest point
- geometry
- shortest movement

---

# Example

Points:

```text
(1,2)
(4,5)
```

Distance:

```text
|1-4| + |2-5|
= 3 + 3
= 6
```

---

# 12. Absolute Value And Binary Search

Sometimes:

```text
|x - a|
```

creates monotonic behavior.

Used in:
- nearest element
- optimization
- ternary/binary search

---

# 13. Removing Absolute Values

Important trick.

Example:

```text
|x - 5|
```

becomes:

If:
```text
x >= 5
```

then:
```text
x - 5
```

Else:
```text
5 - x
```

Used in:
- proofs
- equations
- geometry

---

# 14. CP / FAANG Problem Forms

---

# Form 1 — Minimum Absolute Difference

## Problem

Find minimum:

```text
|a[i] - a[j]|
```

---

## Observation

Closest numbers become adjacent after sorting.

---

## Step-by-Step Working

Example:

```text
8 1 15 10
```

Sort:

```text
1 8 10 15
```

Adjacent differences:

```text
7
2
5
```

Answer:

```text
2
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minAbsDiff(vector<int>& a) {

    sort(a.begin(), a.end());

    int ans = INT_MAX;

    for (int i = 1; i < a.size(); i++) {

        ans = min(ans,
                  abs(a[i] - a[i - 1]));
    }

    return ans;
}
```

---

# Form 2 — Minimum Moves Equal Array

## Problem

Minimum moves to make all numbers equal.

Move cost:

```text
|x - ai|
```

---

## Observation

Median minimizes total absolute distance.

---

## Step-by-Step Working

Array:

```text
1 2 10
```

Choose:

```text
2
```

Cost:

```text
1 + 0 + 8 = 9
```

Choosing average not always optimal.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long minMoves(vector<int>& a) {

    sort(a.begin(), a.end());

    int median = a[a.size()/2];

    long long ans = 0;

    for (int x : a) {

        ans += abs(x - median);
    }

    return ans;
}
```

---

# Form 3 — Manhattan Distance

## Problem

Find Manhattan distance between two points.

---

## Formula

```text
|x1 - x2| + |y1 - y2|
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int manhattan(
    int x1,
    int y1,
    int x2,
    int y2
) {

    return abs(x1 - x2)
         + abs(y1 - y2);
}
```

---

# Form 4 — Closest Element

## Problem

Find array element closest to target.

---

## Observation

Use sorting + binary search.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int closestElement(
    vector<int>& a,
    int target
) {

    sort(a.begin(), a.end());

    auto it =
        lower_bound(a.begin(),
                    a.end(),
                    target);

    if (it == a.begin()) return *it;

    if (it == a.end()) return a.back();

    int right = *it;
    int left = *(it - 1);

    if (abs(left - target)
        <= abs(right - target))
        return left;

    return right;
}
```

---

# 15. Real World Applications

| Real System | Usage |
|---|---|
| Uber | nearest driver |
| Google Maps | route distance |
| CDN routing | nearest edge |
| ML clustering | centroid/median |
| Databases | nearest range query |
| Image processing | pixel distance |
| Robotics | movement cost |

---

# 16. Real Engineering Insight

Absolute value usually means:

```text
distance
```

Distance usually means:
- geometry
- graph
- greedy
- nearest search
- optimization

This mental mapping is extremely important.

---

# 17. Decision Tree

```text
Absolute value present?
→ think distance

Need minimize total distance?
→ median

Need nearest element?
→ sorting + binary search

Grid movement?
→ Manhattan distance

Closest pair?
→ sort first
```

---

# 18. Common Traps

```text
1. Forgetting abs()
2. Using average instead of median
3. Overflow with abs(INT_MIN)
4. Not sorting before nearest logic
5. Wrong Manhattan formula
6. Confusing Euclidean vs Manhattan
7. Ignoring symmetry
8. Removing abs incorrectly
```

---

# 19. Final Checklist

Before solving:

```text
1. Does abs mean distance?
2. Is nearest element needed?
3. Can sorting help?
4. Is median optimal?
5. Is Manhattan distance involved?
6. Can abs be removed piecewise?
7. Is symmetry useful?
8. Is binary search possible?
```

---

# 20. Final Mental Shortcut

```text
Absolute Value
=
Distance
+
Optimization
+
Nearest Search
+
Median Thinking
```
