# 027_Number_Of_Submatrices_Target.md — MiniPrefixSumDifferenceEngine

# Number Of Submatrices With Target Sum

> This is a classic FAANG-hard matrix prefix problem.
>
> Core trick:
>
> ```text
> Convert 2D submatrix problem into many 1D subarray-sum-equals-target problems.
> ```
>
> Pattern:
>
> ```text
> Fix two row boundaries
> compress columns
> count subarrays with target using prefix hashmap
> ```

---

# Clickable Index

1. [What Is Number Of Submatrices Target?](#1-what-is-number-of-submatrices-target)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Mental Model](#3-core-mental-model)
4. [Brute Force Idea](#4-brute-force-idea)
5. [2D To 1D Reduction](#5-2d-to-1d-reduction)
6. [Row Compression Technique](#6-row-compression-technique)
7. [Column Compression Technique](#7-column-compression-technique)
8. [Prefix HashMap Counting](#8-prefix-hashmap-counting)
9. [Main Formula](#9-main-formula)
10. [Step-by-Step Dry Run — Small Matrix](#10-step-by-step-dry-run--small-matrix)
11. [Step-by-Step Dry Run — HashMap Count](#11-step-by-step-dry-run--hashmap-count)
12. [Problem Form 1 — Count Submatrices Sum Target](#12-problem-form-1--count-submatrices-sum-target)
13. [Problem Form 2 — Count Zero-Sum Submatrices](#13-problem-form-2--count-zero-sum-submatrices)
14. [Problem Form 3 — Binary Matrix Target Count](#14-problem-form-3--binary-matrix-target-count)
15. [Problem Form 4 — Rectangle Event Windows](#15-problem-form-4--rectangle-event-windows)
16. [Problem Form 5 — Optimized Smaller Dimension Compression](#16-problem-form-5--optimized-smaller-dimension-compression)
17. [Real World Model 1 — Heatmap Target Region Count](#17-real-world-model-1--heatmap-target-region-count)
18. [Real World Model 2 — Financial Grid Region Detection](#18-real-world-model-2--financial-grid-region-detection)
19. [Real World Model 3 — Image Region Pattern Matching](#19-real-world-model-3--image-region-pattern-matching)
20. [Real World Model 4 — Geo Demand Rectangle Analysis](#20-real-world-model-4--geo-demand-rectangle-analysis)
21. [Decision Tree](#21-decision-tree)
22. [Common Mistakes](#22-common-mistakes)
23. [Complexity](#23-complexity)
24. [Reusable C++ Templates](#24-reusable-c-templates)
25. [CP / FAANG Problem Forms](#25-cp--faang-problem-forms)
26. [Practice Checklist](#26-practice-checklist)
27. [Next Step](#27-next-step)

---

# 1. What Is Number Of Submatrices Target?

Given a matrix:

```text
matrix[n][m]
```

and target:

```text
target
```

Count how many submatrices have sum exactly equal to target.

A submatrix is a rectangle:

```text
top row -> bottom row
left column -> right column
```

Example:

```text
matrix =
0 1 0
1 1 1
0 1 0

target = 0
```

Every single zero cell is a valid submatrix.

---

# 2. Why This Topic Matters

This is one of the most important advanced prefix patterns because it combines:

```text
2D prefix thinking
1D prefix hashmap
row/column compression
subarray sum equals K
matrix optimization
```

It appears in:

```text
LeetCode hard
Google interviews
Meta interviews
Amazon OA
Codeforces matrix problems
AtCoder grid DP/prefix problems
```

---

# 3. Core Mental Model

A 2D rectangle can be reduced to 1D if we fix two boundaries.

Example:

```text
fix top row
fix bottom row
```

Then every column becomes:

```text
sum of values between those two rows
```

Now the problem becomes:

```text
count subarrays in compressed column array with sum target
```

That is exactly:

```text
Subarray Sum Equal K
```

---

# 4. Brute Force Idea

Brute force chooses:

```text
top row
bottom row
left column
right column
```

Then computes rectangle sum.

Number of rectangles:

```text
O(N^2 * M^2)
```

If each sum is computed naively:

```text
O(N*M)
```

With 2D prefix:

```text
O(N^2 * M^2)
```

Still too slow for many constraints.

---

# 5. 2D To 1D Reduction

Fix:

```text
top
bottom
```

Build compressed array:

```text
colSum[c] = matrix[top][c] + matrix[top+1][c] + ... + matrix[bottom][c]
```

Now any submatrix between these rows is just:

```text
a subarray of colSum
```

So count:

```text
subarrays of colSum with sum target
```

---

# 6. Row Compression Technique

For every `top` row:

```text
initialize colSum = 0
```

For every `bottom >= top`:

```text
add matrix[bottom][c] into colSum[c]
```

Then run:

```text
countSubarraySumTarget(colSum, target)
```

---

# 7. Column Compression Technique

If columns are fewer than rows, you can instead fix:

```text
left column
right column
```

and compress rows.

Choose the smaller dimension to square.

Optimization:

```text
O(min(n,m)^2 * max(n,m))
```

---

# 8. Prefix HashMap Counting

For compressed 1D array:

```text
colSum[]
```

Need count subarrays equal target.

Use:

```text
prefix += x
answer += freq[prefix - target]
freq[prefix]++
```

Initialize:

```text
freq[0] = 1
```

---

# 9. Main Formula

For fixed row pair:

```math
colSum[c] = \sum_{r=top}^{bottom} matrix[r][c]
```

Then for every column range `[l..r]`:

```math
submatrixSum = \sum_{c=l}^{r} colSum[c]
```

So:

```text
2D target rectangle
=
1D target subarray on compressed columns
```

---

# 10. Step-by-Step Dry Run — Small Matrix

Matrix:

```text
1 -1
-1 1
```

Target:

```text
0
```

Fix top = 0.

## bottom = 0

Compressed:

```text
[1, -1]
```

Subarrays sum 0:

```text
[1, -1]
```

Count:

```text
1
```

## bottom = 1

Add row 1:

```text
[1 + -1, -1 + 1]
= [0, 0]
```

Subarrays sum 0:

```text
[0]
[0]
[0,0]
```

Count:

```text
3
```

Fix top = 1.

## bottom = 1

Compressed:

```text
[-1, 1]
```

Subarrays sum 0:

```text
[-1,1]
```

Count:

```text
1
```

Total:

```text
1 + 3 + 1 = 5
```

---

# 11. Step-by-Step Dry Run — HashMap Count

Compressed array:

```text
[0, 0]
```

Target:

```text
0
```

Initialize:

```text
freq[0] = 1
prefix = 0
answer = 0
```

First element:

```text
prefix = 0
need = 0
answer += freq[0] = 1
freq[0] = 2
```

Second element:

```text
prefix = 0
need = 0
answer += freq[0] = 2
freq[0] = 3
```

Answer:

```text
3
```

Subarrays:

```text
[0] first
[0] second
[0,0]
```

---

# 12. Problem Form 1 — Count Submatrices Sum Target

## Problem

Count submatrices whose sum equals target.

Input:

```text
matrix =
0 1 0
1 1 1
0 1 0

target = 0
```

Output:

```text
4
```

Each zero cell is a valid submatrix.

## Pattern Recognition

Use when:

```text
matrix
rectangle sum
count target
negative numbers possible
```

Pattern:

```text
row compression + prefix hashmap
```

## Step-by-Step Working

```text
1. Fix top row
2. Expand bottom row
3. Compress columns into colSum
4. Count subarrays of colSum with target
5. Add to answer
```

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int countSubarrayTarget(vector<int>& arr, int target) {
    unordered_map<int, int> freq;

    freq[0] = 1;

    int prefix = 0;
    int ans = 0;

    for (int x : arr) {
        prefix += x;

        ans += freq[prefix - target];

        freq[prefix]++;
    }

    return ans;
}

int numSubmatrixSumTarget(
    vector<vector<int>>& matrix,
    int target
) {
    int n = matrix.size();
    int m = matrix[0].size();

    int ans = 0;

    for (int top = 0; top < n; top++) {
        vector<int> colSum(m, 0);

        for (int bottom = top; bottom < n; bottom++) {
            for (int c = 0; c < m; c++) {
                colSum[c] += matrix[bottom][c];
            }

            ans += countSubarrayTarget(colSum, target);
        }
    }

    return ans;
}

int main() {
    vector<vector<int>> matrix = {
        {0, 1, 0},
        {1, 1, 1},
        {0, 1, 0}
    };

    cout << numSubmatrixSumTarget(matrix, 0) << "\n";

    return 0;
}
```

---

# 13. Problem Form 2 — Count Zero-Sum Submatrices

## Problem

Count submatrices with sum zero.

## Pattern Recognition

Target is:

```text
0
```

So compressed 1D problem becomes:

```text
count zero-sum subarrays
```

## Simulation

Matrix:

```text
1 -1
-1 1
```

Target:

```text
0
```

Answer:

```text
5
```

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int countZeroSumSubarrays(vector<int>& arr) {
    unordered_map<int, int> freq;

    freq[0] = 1;

    int prefix = 0;
    int ans = 0;

    for (int x : arr) {
        prefix += x;

        ans += freq[prefix];

        freq[prefix]++;
    }

    return ans;
}

int countZeroSumSubmatrices(vector<vector<int>>& matrix) {
    int n = matrix.size();
    int m = matrix[0].size();

    int ans = 0;

    for (int top = 0; top < n; top++) {
        vector<int> colSum(m, 0);

        for (int bottom = top; bottom < n; bottom++) {
            for (int c = 0; c < m; c++) {
                colSum[c] += matrix[bottom][c];
            }

            ans += countZeroSumSubarrays(colSum);
        }
    }

    return ans;
}
```

---

# 14. Problem Form 3 — Binary Matrix Target Count

## Problem

Given binary matrix, count submatrices whose sum is target.

## Pattern Recognition

Binary matrix still works with row compression.

Common targets:

```text
number of ones exactly K
empty/zero rectangle
area with exact count
```

## Simulation

Matrix:

```text
1 0
0 1
```

Target:

```text
1
```

Valid submatrices include every single `1` cell and some rectangles with exactly one `1`.

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int countBinarySubmatricesTarget(
    vector<vector<int>>& matrix,
    int target
) {
    int n = matrix.size();
    int m = matrix[0].size();

    int ans = 0;

    for (int top = 0; top < n; top++) {
        vector<int> colSum(m, 0);

        for (int bottom = top; bottom < n; bottom++) {
            for (int c = 0; c < m; c++) {
                colSum[c] += matrix[bottom][c];
            }

            unordered_map<int, int> freq;
            freq[0] = 1;

            int prefix = 0;

            for (int x : colSum) {
                prefix += x;

                ans += freq[prefix - target];

                freq[prefix]++;
            }
        }
    }

    return ans;
}
```

---

# 15. Problem Form 4 — Rectangle Event Windows

## Problem

Grid stores event deltas by:

```text
time bucket x service shard
```

Count rectangles whose total event delta equals target.

## Pattern Recognition

This is still:

```text
submatrix sum target
```

## Simulation

Rows:

```text
time windows
```

Columns:

```text
services
```

A rectangle means:

```text
time range + service range
```

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int countEventRectangles(
    vector<vector<int>>& grid,
    int target
) {
    int n = grid.size();
    int m = grid[0].size();

    int ans = 0;

    for (int top = 0; top < n; top++) {
        vector<int> compressed(m, 0);

        for (int bottom = top; bottom < n; bottom++) {
            for (int c = 0; c < m; c++) {
                compressed[c] += grid[bottom][c];
            }

            unordered_map<int, int> freq;
            freq[0] = 1;

            int prefix = 0;

            for (int x : compressed) {
                prefix += x;

                ans += freq[prefix - target];

                freq[prefix]++;
            }
        }
    }

    return ans;
}
```

---

# 16. Problem Form 5 — Optimized Smaller Dimension Compression

## Problem

Optimize matrix target counting for rectangular matrices.

## Pattern Recognition

If:

```text
rows <= cols
```

compress columns using row pairs.

If:

```text
cols < rows
```

compress rows using column pairs.

## Complexity

```text
O(min(n,m)^2 * max(n,m))
```

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int countTargetOptimized(
    vector<vector<int>>& matrix,
    int target
) {
    int n = matrix.size();
    int m = matrix[0].size();

    // Standard row-pair version.
    // Good when n <= m.
    int ans = 0;

    for (int top = 0; top < n; top++) {
        vector<int> colSum(m, 0);

        for (int bottom = top; bottom < n; bottom++) {
            for (int c = 0; c < m; c++) {
                colSum[c] += matrix[bottom][c];
            }

            unordered_map<int, int> freq;
            freq[0] = 1;

            int prefix = 0;

            for (int x : colSum) {
                prefix += x;

                ans += freq[prefix - target];

                freq[prefix]++;
            }
        }
    }

    return ans;
}
```

---

# 17. Real World Model 1 — Heatmap Target Region Count

## Scenario

A product heatmap stores user clicks per grid cell.

Need count of rectangular regions with exactly target clicks.

## Simulation

Grid:

```text
click counts
```

Target:

```text
100 clicks
```

Question:

```text
How many screen regions have exactly 100 clicks?
```

## System Mapping

Used in:

```text
analytics dashboards
UX heatmaps
ad placement analysis
A/B testing
```

## C++ Model

```cpp
int countHeatmapRegions(
    vector<vector<int>>& heat,
    int target
) {
    return countTargetOptimized(heat, target);
}
```

---

# 18. Real World Model 2 — Financial Grid Region Detection

## Scenario

Rows represent months.

Columns represent departments.

Cell value:

```text
profit/loss delta
```

Need count regions with net target value.

## Simulation

Rectangle:

```text
month range + department range
```

Target:

```text
0 net delta
```

## System Mapping

Used in:

```text
financial analytics
profit/loss windows
department budget analysis
business intelligence
```

## C++ Model

```cpp
int countFinancialRegions(
    vector<vector<int>>& delta,
    int target
) {
    return countTargetOptimized(delta, target);
}
```

---

# 19. Real World Model 3 — Image Region Pattern Matching

## Scenario

Image matrix stores intensity deltas.

Need count regions with exact brightness sum.

## Simulation

Target:

```text
specific brightness signature
```

## System Mapping

Used in:

```text
image processing
computer vision
object patch detection
feature extraction
```

## C++ Model

```cpp
int countImagePatches(
    vector<vector<int>>& pixels,
    int target
) {
    return countTargetOptimized(pixels, target);
}
```

---

# 20. Real World Model 4 — Geo Demand Rectangle Analysis

## Scenario

Map is divided into grid cells.

Each cell stores demand delta.

Need count regions with exact demand target.

## Simulation

Rectangle:

```text
city region
```

Target:

```text
high demand threshold
```

## System Mapping

Used in:

```text
ride sharing
delivery apps
geo analytics
surge zone detection
capacity planning
```

## C++ Model

```cpp
int countGeoDemandRegions(
    vector<vector<int>>& demand,
    int target
) {
    return countTargetOptimized(demand, target);
}
```

---

# 21. Decision Tree

```text
Matrix problem?
|
+-- Need rectangle query only?
|   |
|   +-- 2D prefix
|
+-- Need count submatrices with exact target?
|   |
|   +-- row/column compression + 1D prefix hashmap
|
+-- Negative numbers present?
|   |
|   +-- prefix hashmap required
|
+-- All positive?
|   |
|   +-- some sliding-window variants may work
|
+-- Rectangular matrix?
    |
    +-- compress smaller dimension
```

---

# 22. Common Mistakes

## Mistake 1 — Trying Pure 2D Prefix Only

2D prefix can compute one rectangle sum, but counting all target rectangles still needs many rectangle choices.

Compression reduces complexity.

## Mistake 2 — Forgetting `freq[0] = 1`

This misses subarrays starting at first compressed column.

## Mistake 3 — Reinitializing `colSum` At Wrong Time

Correct:

```text
new colSum for every top
extend it for each bottom
```

## Mistake 4 — Using Sliding Window With Negatives

If matrix has negative values, sliding window fails.

Use prefix hashmap.

## Mistake 5 — Not Optimizing Dimension

Use:

```text
min(n,m)^2 * max(n,m)
```

when possible.

---

# 23. Complexity

Standard row compression:

```text
O(N^2 * M)
```

Column compression:

```text
O(M^2 * N)
```

Optimized:

```text
O(min(N,M)^2 * max(N,M))
```

Space:

```text
O(max(N,M))
```

---

# 24. Reusable C++ Templates

## Template 1 — Count Subarray Target

```cpp
int countSubarrayTarget(vector<int>& arr, int target) {
    unordered_map<int, int> freq;

    freq[0] = 1;

    int prefix = 0;
    int ans = 0;

    for (int x : arr) {
        prefix += x;

        ans += freq[prefix - target];

        freq[prefix]++;
    }

    return ans;
}
```

## Template 2 — Row Compression Matrix Count

```cpp
int countSubmatrixTarget(
    vector<vector<int>>& matrix,
    int target
) {
    int n = matrix.size();
    int m = matrix[0].size();

    int ans = 0;

    for (int top = 0; top < n; top++) {
        vector<int> colSum(m, 0);

        for (int bottom = top; bottom < n; bottom++) {
            for (int c = 0; c < m; c++) {
                colSum[c] += matrix[bottom][c];
            }

            ans += countSubarrayTarget(colSum, target);
        }
    }

    return ans;
}
```

---

# 25. CP / FAANG Problem Forms

## Problem 1 — Number Of Submatrices Sum Target

### Recognition

```text
count rectangles with exact sum
negative values possible
```

### Pattern

```text
row compression + prefix hashmap
```

### Steps

```text
1. Fix top row
2. Extend bottom row
3. Compress columns
4. Count target subarrays
```

### Commented C++ Code

```cpp
int numSubmatrixSumTarget(
    vector<vector<int>>& matrix,
    int target
) {
    int n = matrix.size();
    int m = matrix[0].size();

    int ans = 0;

    for (int top = 0; top < n; top++) {
        vector<int> colSum(m, 0);

        for (int bottom = top; bottom < n; bottom++) {
            for (int c = 0; c < m; c++) {
                colSum[c] += matrix[bottom][c];
            }

            unordered_map<int, int> freq;
            freq[0] = 1;

            int prefix = 0;

            for (int x : colSum) {
                prefix += x;

                ans += freq[prefix - target];

                freq[prefix]++;
            }
        }
    }

    return ans;
}
```

---

## Problem 2 — Zero-Sum Submatrices

### Recognition

```text
target = 0
```

### Pattern

```text
same prefix repeated after compression
```

### Commented C++ Code

```cpp
int countZeroSubmatrices(vector<vector<int>>& matrix) {
    return numSubmatrixSumTarget(matrix, 0);
}
```

---

## Problem 3 — Binary Matrix Exact Ones

### Recognition

```text
binary matrix
count rectangles with exactly K ones
```

### Pattern

```text
same compression method
```

### Commented C++ Code

```cpp
int countExactOnes(
    vector<vector<int>>& matrix,
    int k
) {
    return numSubmatrixSumTarget(matrix, k);
}
```

---

## Problem 4 — Event Grid Target Rectangle

### Recognition

```text
time x service matrix
target event delta
```

### Pattern

```text
2D to 1D compression
```

### Commented C++ Code

```cpp
int countEventTarget(
    vector<vector<int>>& events,
    int target
) {
    return numSubmatrixSumTarget(events, target);
}
```

---

## Problem 5 — Dimension-Optimized Matrix Target

### Recognition

```text
one dimension much smaller than other
```

### Pattern

```text
square smaller dimension
```

### Commented C++ Code

```cpp
// Choose row-pair or column-pair compression
// depending on which dimension is smaller.
```

---

# 26. Practice Checklist

```text
1. Is this a submatrix/rectangle target problem?
2. Are negative values possible?
3. Can I fix two boundaries?
4. Can I compress the other dimension?
5. Does compressed problem become subarray sum K?
6. Did I use freq[0] = 1?
7. Did I reset colSum only when top changes?
8. Is O(N^2*M) acceptable?
9. Should I compress smaller dimension?
10. Did I test target = 0?
```

---

# 27. Next Step

```text
028_Max_Sum_Rectangle_No_Larger_K.md
```

Next file covers:

```text
max submatrix sum no larger than K
ordered set
row compression
TreeSet/lower_bound
FAANG hard optimization pattern
