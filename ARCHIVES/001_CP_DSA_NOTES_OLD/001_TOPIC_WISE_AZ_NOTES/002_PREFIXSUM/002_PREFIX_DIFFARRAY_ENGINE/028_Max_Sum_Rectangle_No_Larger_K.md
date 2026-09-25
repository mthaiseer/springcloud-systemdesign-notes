# 028_Max_Sum_Rectangle_No_Larger_K.md — MiniPrefixSumDifferenceEngine

# Max Sum Rectangle No Larger Than K

> Core Pattern:
>
> ```text
> 2D Matrix
> ->
> Row Compression
> ->
> 1D Prefix Sum
> ->
> Ordered Set / lower_bound
> ```
>
> This is one of the most important FAANG-hard matrix optimization patterns.

---

# Clickable Index

1. [What Is This Problem?](#1-what-is-this-problem)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Mental Model](#3-core-mental-model)
4. [Brute Force Idea](#4-brute-force-idea)
5. [2D To 1D Compression](#5-2d-to-1d-compression)
6. [Ordered Set Insight](#6-ordered-set-insight)
7. [Lower Bound Formula](#7-lower-bound-formula)
8. [Step-by-Step Dry Run](#8-step-by-step-dry-run)
9. [Problem Form 1 — Max Rectangle <= K](#9-problem-form-1--max-rectangle--k)
10. [Problem Form 2 — 1D Max Subarray <= K](#10-problem-form-2--1d-max-subarray--k)
11. [Problem Form 3 — Negative Values Matrix](#11-problem-form-3--negative-values-matrix)
12. [Problem Form 4 — Budget Limited Region](#12-problem-form-4--budget-limited-region)
13. [Problem Form 5 — Dimension Optimization](#13-problem-form-5--dimension-optimization)
14. [Real World Model 1 — Budget Allocation Grid](#14-real-world-model-1--budget-allocation-grid)
15. [Real World Model 2 — Heatmap Threshold Region](#15-real-world-model-2--heatmap-threshold-region)
16. [Real World Model 3 — Geo Capacity Planning](#16-real-world-model-3--geo-capacity-planning)
17. [Real World Model 4 — Cloud Cost Optimization](#17-real-world-model-4--cloud-cost-optimization)
18. [Decision Tree](#18-decision-tree)
19. [Common Mistakes](#19-common-mistakes)
20. [Complexity](#20-complexity)
21. [Reusable C++ Templates](#21-reusable-c-templates)
22. [CP / FAANG Problem Forms](#22-cp--faang-problem-forms)
23. [Practice Checklist](#23-practice-checklist)
24. [Next Step](#24-next-step)

---

# 1. What Is This Problem?

Given:

```text
matrix[n][m]
```

and integer:

```text
K
```

Find the maximum rectangle sum such that:

```text
sum <= K
```

Example:

```text
1  0  1
0 -2  3
```

K:

```text
2
```

Answer:

```text
2
```

---

# 2. Why This Topic Matters

This combines:

```text
2D prefix concepts
row compression
prefix sums
ordered set
lower_bound
optimization under constraints
```

Appears in:

```text
Google interviews
Meta interviews
Amazon OA
LeetCode hard
Codeforces advanced prefix problems
```

---

# 3. Core Mental Model

Fix:

```text
top row
bottom row
```

Compress matrix into:

```text
colSum[]
```

where:

```text
colSum[c]
=
sum of matrix[top..bottom][c]
```

Now every rectangle becomes:

```text
subarray of colSum
```

So:

```text
2D problem
->
1D max subarray <= K
```

---

# 4. Brute Force Idea

Choose:

```text
top
bottom
left
right
```

Complexity:

```text
O(N^2 * M^2)
```

Too slow.

Need row compression optimization.

---

# 5. 2D To 1D Compression

For every:

```text
top row
```

Initialize:

```text
colSum = 0
```

For every:

```text
bottom >= top
```

Update:

```text
colSum[c] += matrix[bottom][c]
```

Now solve:

```text
max subarray sum <= K
```

on:

```text
colSum[]
```

---

# 6. Ordered Set Insight

Subarray sum:

```text
currentPrefix - previousPrefix
```

Need:

```text
currentPrefix - previousPrefix <= K
```

Rearrange:

```text
previousPrefix >= currentPrefix - K
```

Need smallest prefix satisfying condition.

Use:

```cpp
lower_bound(currentPrefix - K)
```

---

# 7. Lower Bound Formula

Markdown-safe math:

```math
cur - prev \le K
```

Rearrange:

```math
prev \ge cur - K
```

Candidate answer:

```math
cur - prev
```

---

# 8. Step-by-Step Dry Run

Matrix:

```text
1  0  1
0 -2  3
```

K:

```text
2
```

Fix:

```text
top = 0
bottom = 0
```

Compressed:

```text
[1,0,1]
```

Best subarray <= 2:

```text
2
```

Next:

```text
top = 0
bottom = 1
```

Compressed:

```text
[1,-2,4]
```

Best <= 2:

```text
1
```

Final answer:

```text
2
```

---

# 9. Problem Form 1 — Max Rectangle <= K

## Pattern Recognition

Use when:

```text
matrix
maximum rectangle
constraint <= K
negative values possible
```

Pattern:

```text
row compression + ordered set
```

---

## Step-by-Step Working

```text
1. Fix top row
2. Expand bottom row
3. Compress columns
4. Solve 1D max subarray <= K
5. Update answer
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxSubarrayNoLargerThanK(
    vector<int>& arr,
    int k
) {
    set<int> prefixes;

    prefixes.insert(0);

    int prefix = 0;
    int best = INT_MIN;

    for (int x : arr) {
        prefix += x;

        auto it =
            prefixes.lower_bound(prefix - k);

        if (it != prefixes.end()) {
            best =
                max(best, prefix - *it);
        }

        prefixes.insert(prefix);
    }

    return best;
}

int maxSumSubmatrix(
    vector<vector<int>>& matrix,
    int k
) {
    int n = matrix.size();
    int m = matrix[0].size();

    int answer = INT_MIN;

    for (int top = 0; top < n; top++) {

        vector<int> colSum(m, 0);

        for (int bottom = top;
             bottom < n;
             bottom++) {

            for (int c = 0; c < m; c++) {
                colSum[c] +=
                    matrix[bottom][c];
            }

            answer = max(
                answer,
                maxSubarrayNoLargerThanK(
                    colSum,
                    k
                )
            );
        }
    }

    return answer;
}
```

---

# 10. Problem Form 2 — 1D Max Subarray <= K

## Pattern Recognition

Need:

```text
max subarray sum
with upper limit K
negative numbers allowed
```

Use:

```text
prefix sum + ordered set
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxSubarrayLEK(
    vector<int>& arr,
    int k
) {
    set<int> s;

    s.insert(0);

    int prefix = 0;
    int ans = INT_MIN;

    for (int x : arr) {

        prefix += x;

        auto it =
            s.lower_bound(prefix - k);

        if (it != s.end()) {
            ans = max(ans,
                      prefix - *it);
        }

        s.insert(prefix);
    }

    return ans;
}
```

---

# 11. Problem Form 3 — Negative Values Matrix

## Problem

Matrix may contain:

```text
positive
negative
zero
```

Kadane alone fails because of:

```text
constraint <= K
```

Need:

```text
ordered set approach
```

---

## Simulation

Compressed array:

```text
[3,-2,5,-1]
```

K:

```text
4
```

Best valid subarray:

```text
4
```

---

## Commented C++ Code

```cpp
// Same ordered-set approach works
// even with negative values.
```

---

# 12. Problem Form 4 — Budget Limited Region

## Problem

Find largest region cost not exceeding budget.

---

## Simulation

Grid:

```text
department expenses
```

Need:

```text
largest rectangle <= budget
```

---

## Pattern Recognition

Use:

```text
max rectangle <= K
```

---

## Commented C++ Code

```cpp
int budgetRegion(
    vector<vector<int>>& cost,
    int budget
) {
    return maxSumSubmatrix(cost, budget);
}
```

---

# 13. Problem Form 5 — Dimension Optimization

## Problem

Optimize for rectangular matrix.

---

## Pattern Recognition

If:

```text
rows < cols
```

compress rows.

Else:

```text
compress columns.
```

---

## Complexity

```text
O(min(n,m)^2 * max(n,m) * log(max(n,m)))
```

---

## Commented C++ Code

```cpp
// Choose smaller dimension
// for squaring complexity.
```

---

# 14. Real World Model 1 — Budget Allocation Grid

## Scenario

Rows:

```text
months
```

Columns:

```text
departments
```

Need:

```text
largest region
under spending limit
```

---

## System Mapping

Used in:

```text
financial analytics
budget planning
BI systems
```

---

## Commented C++ Model

```cpp
int budgetPlanning(
    vector<vector<int>>& spend,
    int limit
) {
    return maxSumSubmatrix(spend, limit);
}
```

---

# 15. Real World Model 2 — Heatmap Threshold Region

## Scenario

Heatmap grid stores traffic density.

Need largest region under threshold.

---

## System Mapping

Used in:

```text
UX analytics
heatmap optimization
ad analytics
```

---

## Commented C++ Model

```cpp
int heatmapRegion(
    vector<vector<int>>& heat,
    int limit
) {
    return maxSumSubmatrix(heat, limit);
}
```

---

# 16. Real World Model 3 — Geo Capacity Planning

## Scenario

Grid stores ride demand.

Need region demand not exceeding capacity.

---

## System Mapping

Used in:

```text
ride sharing
delivery systems
capacity planning
geo analytics
```

---

## Commented C++ Model

```cpp
int geoCapacity(
    vector<vector<int>>& demand,
    int cap
) {
    return maxSumSubmatrix(demand, cap);
}
```

---

# 17. Real World Model 4 — Cloud Cost Optimization

## Scenario

Rows:

```text
time windows
```

Columns:

```text
services
```

Need largest region cost within budget.

---

## System Mapping

Used in:

```text
cloud analytics
resource optimization
distributed systems
cost dashboards
```

---

## Commented C++ Model

```cpp
int cloudCost(
    vector<vector<int>>& cost,
    int budget
) {
    return maxSumSubmatrix(cost, budget);
}
```

---

# 18. Decision Tree

```text
Matrix problem?
|
+-- Need rectangle query?
|   |
|   +-- 2D prefix
|
+-- Need rectangle count?
|   |
|   +-- row compression + hashmap
|
+-- Need maximum rectangle <= K?
|   |
|   +-- row compression + ordered set
|
+-- Negative numbers present?
|   |
|   +-- ordered set needed
|
+-- Rectangular matrix?
    |
    +-- compress smaller dimension
```

---

# 19. Common Mistakes

## Mistake 1 — Using Kadane Directly

Kadane ignores:

```text
upper limit K
```

---

## Mistake 2 — Wrong lower_bound Logic

Need:

```text
prefix >= currentPrefix - K
```

---

## Mistake 3 — Forgetting Initial Zero Prefix

Must insert:

```cpp
0
```

initially.

---

## Mistake 4 — Reinitializing colSum Incorrectly

Reset only when:

```text
top changes
```

---

## Mistake 5 — Ignoring Dimension Optimization

Use:

```text
min(n,m)^2
```

when possible.

---

# 20. Complexity

Standard:

```text
O(N^2 * M * log M)
```

Optimized:

```text
O(min(n,m)^2 * max(n,m) * log(max(n,m)))
```

Space:

```text
O(max(n,m))
```

---

# 21. Reusable C++ Templates

## Template 1 — Max Subarray <= K

```cpp
int maxSubarrayLEK(
    vector<int>& arr,
    int k
) {
    set<int> s;

    s.insert(0);

    int prefix = 0;
    int best = INT_MIN;

    for (int x : arr) {

        prefix += x;

        auto it =
            s.lower_bound(prefix - k);

        if (it != s.end()) {
            best =
                max(best,
                    prefix - *it);
        }

        s.insert(prefix);
    }

    return best;
}
```

---

## Template 2 — Matrix Compression

```cpp
for (int top = 0; top < n; top++) {

    vector<int> colSum(m, 0);

    for (int bottom = top;
         bottom < n;
         bottom++) {

        for (int c = 0; c < m; c++) {
            colSum[c] +=
                matrix[bottom][c];
        }

        answer = max(
            answer,
            maxSubarrayLEK(colSum, k)
        );
    }
}
```

---

# 22. CP / FAANG Problem Forms

## Problem 1 — Max Rectangle <= K

### Recognition

```text
maximum rectangle
constraint <= K
negative numbers possible
```

### Pattern

```text
row compression + ordered set
```

### Steps

```text
1. Fix row boundaries
2. Compress columns
3. Solve 1D constrained subarray
```

---

## Problem 2 — Max Subarray <= K

### Recognition

```text
1D constrained maximum subarray
```

### Pattern

```text
prefix sum + lower_bound
```

---

## Problem 3 — Budget Region

### Recognition

```text
largest valid region under limit
```

### Pattern

```text
same rectangle optimization
```

---

## Problem 4 — Geo Capacity

### Recognition

```text
demand region <= capacity
```

### Pattern

```text
matrix optimization
```

---

## Problem 5 — Negative Values Matrix

### Recognition

```text
negative values break sliding window
```

### Pattern

```text
ordered set
```

---

# 23. Practice Checklist

```text
1. Is this a rectangle optimization problem?
2. Is there upper bound K?
3. Are negative numbers present?
4. Can matrix be compressed to 1D?
5. Do I need ordered set?
6. Did I insert 0 prefix?
7. Is lower_bound formula correct?
8. Am I compressing smaller dimension?
9. Did I test negative values?
10. Did I avoid Kadane mistake?
```

---

# 24. Next Step

```text
029_Prefix_Deque_Shortest_Subarray_K.md
```

Next file covers:

```text
prefix + deque
monotonic queue
shortest subarray >= K
advanced sliding window optimization
```
