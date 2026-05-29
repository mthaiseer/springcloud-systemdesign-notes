# 005_2D_Prefix_Sum.md — MiniPrefixSumDifferenceEngine

# 2D Prefix Sum

> 2D Prefix Sum extends normal prefix sum from arrays to matrices.
>
> It allows:
>
> ```text
> O(1) rectangle sum queries
> ```
>
> after:
>
> ```text
> O(rows * cols) preprocessing
> ```

---

## Clickable Index

1. [What Is 2D Prefix Sum?](#1-what-is-2d-prefix-sum)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Mental Model](#3-core-mental-model)
4. [What Does pref[r][c] Mean?](#4-what-does-prefrc-mean)
5. [2D Prefix Formula](#5-2d-prefix-formula)
6. [Why Inclusion-Exclusion Is Needed](#6-why-inclusion-exclusion-is-needed)
7. [Rectangle Query Formula](#7-rectangle-query-formula)
8. [Visual Intuition](#8-visual-intuition)
9. [Step-by-Step Dry Run — Build](#9-step-by-step-dry-run--build)
10. [Step-by-Step Dry Run — Rectangle Query](#10-step-by-step-dry-run--rectangle-query)
11. [1-Indexed 2D Prefix Build](#11-1-indexed-2d-prefix-build)
12. [Problem Form 1 — Rectangle Sum Query](#12-problem-form-1--rectangle-sum-query)
13. [Problem Form 2 — Matrix Block Sum](#13-problem-form-2--matrix-block-sum)
14. [Problem Form 3 — Heatmap Aggregation](#14-problem-form-3--heatmap-aggregation)
15. [Problem Form 4 — Image Brightness Query](#15-problem-form-4--image-brightness-query)
16. [Problem Form 5 — Grid Population Query](#16-problem-form-5--grid-population-query)
17. [Real World Model 1 — CDN Traffic Heatmap](#17-real-world-model-1--cdn-traffic-heatmap)
18. [Real World Model 2 — Geo Density Analytics](#18-real-world-model-2--geo-density-analytics)
19. [Real World Model 3 — Image Processing](#19-real-world-model-3--image-processing)
20. [Real World Model 4 — Game Map Aggregation](#20-real-world-model-4--game-map-aggregation)
21. [Decision Tree](#21-decision-tree)
22. [Common Mistakes](#22-common-mistakes)
23. [Complexity](#23-complexity)
24. [Reusable C++ Templates](#24-reusable-c-templates)
25. [CP / FAANG Problem Forms](#25-cp--faang-problem-forms)
26. [Practice Checklist](#26-practice-checklist)
27. [Next Step](#27-next-step)

---

## 1. What Is 2D Prefix Sum?

2D Prefix Sum stores cumulative sums for a matrix.

Instead of:

```text
sum from index 0 to i
```

we now store:

```text
sum of rectangle from (0,0) to (r,c)
```

Example matrix:

```text
1 2 3
4 5 6
7 8 9
```

2D prefix allows fast rectangle queries like:

```text
sum of rectangle:
(r1,c1) -> (r2,c2)
```

---

## 2. Why This Topic Matters

Without 2D prefix:

```text
each rectangle query scans all cells
```

Complexity:

```text
O(area of rectangle)
```

With 2D prefix:

```text
O(1) per rectangle query
```

This appears in:

```text
CP
FAANG
image processing
heatmaps
analytics systems
geo aggregation
grid problems
```

---

## 3. Core Mental Model

1D prefix:

```text
prefix stores cumulative sum from left
```

2D prefix:

```text
prefix stores cumulative sum from top-left rectangle
```

Think:

```text
pref[r][c]
=
everything from (0,0) to (r,c)
```

---

## 4. What Does pref[r][c] Mean?

Example:

```text
pref[2][3]
```

means:

```text
sum of all cells inside rectangle:
(0,0) -> (2,3)
```

Visual:

```text
X X X X
X X X X
X X X X
```

Everything marked `X` is included.

---

## 5. 2D Prefix Formula

Using 1-indexed prefix matrix:

```text
pref[r][c]
=
pref[r-1][c]
+
pref[r][c-1]
-
pref[r-1][c-1]
+
grid[r-1][c-1]
```

---

## 6. Why Inclusion-Exclusion Is Needed

When adding:

```text
top rectangle
+
left rectangle
```

the overlap rectangle gets counted twice.

So we subtract overlap once.

Formula:

```text
top + left - overlap + current
```

This is classic:

```text
inclusion-exclusion
```

---

## 7. Rectangle Query Formula

Rectangle:

```text
(r1,c1) -> (r2,c2)
```

Answer:

```text
pref[r2+1][c2+1]
- pref[r1][c2+1]
- pref[r2+1][c1]
+ pref[r1][c1]
```

Pattern:

```text
big rectangle
- top strip
- left strip
+ overlap
```

---

## 8. Visual Intuition

Suppose we want:

```text
rectangle:
(r1,c1) -> (r2,c2)
```

We first take:

```text
whole big rectangle
```

Then remove:

```text
top unwanted area
left unwanted area
```

But overlap got removed twice.

So:

```text
add overlap back
```

This is exactly like 2D inclusion-exclusion.

---

## 9. Step-by-Step Dry Run — Build

Matrix:

```text
1 2 3
4 5 6
7 8 9
```

Create 1-indexed prefix:

Initial:

```text
pref =
0 0 0 0
0 0 0 0
0 0 0 0
0 0 0 0
```

---

### Cell (1,1)

```text
pref[1][1]
=
pref[0][1]
+
pref[1][0]
-
pref[0][0]
+
grid[0][0]

=
0 + 0 - 0 + 1
=
1
```

---

### Cell (1,2)

```text
=
pref[0][2]
+
pref[1][1]
-
pref[0][1]
+
grid[0][1]

=
0 + 1 - 0 + 2
=
3
```

---

### Cell (1,3)

```text
=
0 + 3 - 0 + 3
=
6
```

---

### Cell (2,1)

```text
=
1 + 0 - 0 + 4
=
5
```

---

### Cell (2,2)

```text
=
3 + 5 - 1 + 5
=
12
```

---

Continue similarly.

Final prefix:

```text
0  0  0  0
0  1  3  6
0  5 12 21
0 12 27 45
```

---

## 10. Step-by-Step Dry Run — Rectangle Query

Matrix:

```text
1 2 3
4 5 6
7 8 9
```

Query rectangle:

```text
(1,1) -> (2,2)
```

Meaning:

```text
5 6
8 9
```

Answer:

```text
5 + 6 + 8 + 9 = 28
```

Using prefix:

```text
pref[3][3]
- pref[1][3]
- pref[3][1]
+ pref[1][1]

=
45 - 6 - 12 + 1
=
28
```

---

## 11. 1-Indexed 2D Prefix Build

### Code

```cpp
vector<vector<long long>> pref(rows + 1,
                               vector<long long>(cols + 1, 0));

for (int r = 1; r <= rows; r++) {
    for (int c = 1; c <= cols; c++) {

        pref[r][c]
        =
        pref[r - 1][c]
        +
        pref[r][c - 1]
        -
        pref[r - 1][c - 1]
        +
        grid[r - 1][c - 1];
    }
}
```

---

## 12. Problem Form 1 — Rectangle Sum Query

### Problem

Answer many rectangle sum queries.

### Pattern

```text
2D prefix sum
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class NumMatrix {
private:
    vector<vector<long long>> pref;

public:
    NumMatrix(vector<vector<int>>& matrix) {

        int rows = matrix.size();
        int cols = matrix[0].size();

        pref.assign(rows + 1,
                    vector<long long>(cols + 1, 0));

        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {

                pref[r][c]
                =
                pref[r - 1][c]
                +
                pref[r][c - 1]
                -
                pref[r - 1][c - 1]
                +
                matrix[r - 1][c - 1];
            }
        }
    }

    long long sumRegion(int r1, int c1, int r2, int c2) {

        return
        pref[r2 + 1][c2 + 1]
        - pref[r1][c2 + 1]
        - pref[r2 + 1][c1]
        + pref[r1][c1];
    }
};

int main() {

    vector<vector<int>> matrix = {
        {1,2,3},
        {4,5,6},
        {7,8,9}
    };

    NumMatrix obj(matrix);

    cout << obj.sumRegion(1,1,2,2) << endl;

    return 0;
}
```

---

## 13. Problem Form 2 — Matrix Block Sum

### Problem

For every cell:

```text
compute sum of nearby k-distance block
```

### Pattern

```text
many rectangle queries
```

### Idea

Each block query becomes:

```text
O(1)
```

with 2D prefix.

---

## 14. Problem Form 3 — Heatmap Aggregation

### Problem

Grid stores traffic:

```text
users per region
```

Need:

```text
total users inside rectangle region
```

### Pattern

```text
2D range aggregation
```

### Example

```text
north-west city block traffic
```

This maps naturally to 2D prefix.

---

## 15. Problem Form 4 — Image Brightness Query

### Problem

Image pixels:

```text
brightness values
```

Need:

```text
sum brightness inside rectangle
```

### Pattern

```text
integral image / summed area table
```

2D prefix is heavily used in image processing.

---

## 16. Problem Form 5 — Grid Population Query

### Problem

Population map:

```text
people per cell
```

Need:

```text
population inside district rectangle
```

### Pattern

```text
rectangle aggregation
```

---

## 17. Real World Model 1 — CDN Traffic Heatmap

Imagine a world map divided into cells.

Each cell stores:

```text
traffic count
```

Need:

```text
traffic inside a geo rectangle
```

2D prefix supports:

```text
O(1) aggregation
```

after preprocessing.

---

## 18. Real World Model 2 — Geo Density Analytics

Ride-sharing systems may maintain:

```text
driver density grid
```

Need:

```text
drivers inside rectangular region
```

2D prefix is conceptually similar to spatial aggregation.

---

## 19. Real World Model 3 — Image Processing

In image processing:

```text
pixel intensities
```

Need fast rectangle brightness queries.

2D prefix is called:

```text
Integral Image
Summed Area Table
```

Used in:

```text
computer vision
object detection
```

---

## 20. Real World Model 4 — Game Map Aggregation

Game map:

```text
resources per tile
```

Need:

```text
total resources inside area
```

2D prefix supports fast rectangle aggregation.

---

## 21. Decision Tree

```text
Grid / matrix problem?
|
+-- Need rectangle sum queries?
|   |
|   +-- Yes -> 2D Prefix Sum
|
+-- Matrix static?
|   |
|   +-- Yes -> 2D Prefix works great
|
+-- Matrix updates dynamically?
|   |
|   +-- Use Fenwick 2D / Segment Tree 2D
|
+-- Need rectangle min/max?
    |
    +-- 2D prefix is NOT enough
```

---

## 22. Common Mistakes

### Mistake 1 — Forgetting Overlap Subtraction

Wrong:

```text
top + left + current
```

Correct:

```text
top + left - overlap + current
```

---

### Mistake 2 — Wrong Rectangle Query Formula

Correct:

```text
big
- top
- left
+ overlap
```

---

### Mistake 3 — Mixing 0-Indexed and 1-Indexed

Use:

```text
pref dimensions = rows+1 x cols+1
```

This avoids boundary issues.

---

### Mistake 4 — Negative Numbers

2D prefix works perfectly with negatives also.

---

### Mistake 5 — Forgetting Long Long

Large grids can overflow int.

Use:

```cpp
long long
```

---

## 23. Complexity

Build:

```text
O(rows * cols)
```

Each rectangle query:

```text
O(1)
```

Space:

```text
O(rows * cols)
```

---

## 24. Reusable C++ Templates

### Template 1 — Build 2D Prefix

```cpp
vector<vector<long long>>
build2DPrefix(const vector<vector<int>>& grid) {

    int rows = grid.size();
    int cols = grid[0].size();

    vector<vector<long long>> pref(
        rows + 1,
        vector<long long>(cols + 1, 0)
    );

    for (int r = 1; r <= rows; r++) {
        for (int c = 1; c <= cols; c++) {

            pref[r][c]
            =
            pref[r - 1][c]
            +
            pref[r][c - 1]
            -
            pref[r - 1][c - 1]
            +
            grid[r - 1][c - 1];
        }
    }

    return pref;
}
```

### Template 2 — Rectangle Query

```cpp
long long rectangleSum(
    const vector<vector<long long>>& pref,
    int r1,
    int c1,
    int r2,
    int c2
) {

    return
    pref[r2 + 1][c2 + 1]
    - pref[r1][c2 + 1]
    - pref[r2 + 1][c1]
    + pref[r1][c1];
}
```

---

## 25. CP / FAANG Problem Forms

---

### Problem 1 — Range Sum Query 2D Immutable

#### Recognition

```text
many rectangle sum queries
matrix static
```

#### Pattern

```text
2D prefix
```

#### Core Formula

```text
big
- top
- left
+ overlap
```

---

### Problem 2 — Matrix Block Sum

#### Recognition

```text
many nearby square queries
```

#### Pattern

```text
rectangle query repeated many times
```

---

### Problem 3 — Image Brightness Rectangle

#### Recognition

```text
sum inside image rectangle
```

#### Pattern

```text
integral image
```

---

### Problem 4 — Heatmap Analytics

#### Recognition

```text
count inside geo rectangle
```

#### Pattern

```text
2D aggregation
```

---

### Problem 5 — Grid Population Query

#### Recognition

```text
population in rectangular district
```

#### Pattern

```text
2D prefix rectangle query
```

---

## 26. Practice Checklist

Before coding 2D prefix:

```text
1. Did I allocate rows+1 x cols+1?
2. Did I use inclusion-exclusion?
3. Did I subtract overlap once?
4. Is query formula correct?
5. Did I use long long?
6. Did I test single-cell rectangle?
7. Did I test full matrix rectangle?
8. Did I test negative values?
9. Is matrix static?
10. Is rectangle query O(1)?
```

---

## 27. Next Step

```text
006_What_Is_Difference_Array.md
```

That file introduces:

```text
range updates
delta arrays
lazy accumulation idea
batch update optimization
event sweep foundation
```
