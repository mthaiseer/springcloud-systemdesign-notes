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

Given a static matrix, answer many queries:

```text
sumRegion(r1, c1, r2, c2)
```

Each query asks:

```text
sum of all cells inside rectangle from top-left (r1,c1)
to bottom-right (r2,c2)
```

---

### Pattern

```text
Static matrix + many rectangle sum queries
=> 2D Prefix Sum
```

---

### Step-by-Step Working

Matrix:

```text
1 2 3
4 5 6
7 8 9
```

Query:

```text
sumRegion(1,1,2,2)
```

This rectangle is:

```text
5 6
8 9
```

Manual sum:

```text
5 + 6 + 8 + 9 = 28
```

Build 2D prefix:

```text
pref =
0  0  0  0
0  1  3  6
0  5 12 21
0 12 27 45
```

Use formula:

```text
answer =
pref[r2+1][c2+1]
- pref[r1][c2+1]
- pref[r2+1][c1]
+ pref[r1][c1]
```

Substitute:

```text
answer =
pref[3][3]
- pref[1][3]
- pref[3][1]
+ pref[1][1]
```

```text
= 45 - 6 - 12 + 1
= 28
```

---

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

        // 1-indexed prefix matrix.
        // pref[r][c] = sum of rectangle (0,0) to (r-1,c-1)
        pref.assign(rows + 1, vector<long long>(cols + 1, 0));

        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {

                // top + left - overlap + current cell
                pref[r][c]
                    = pref[r - 1][c]
                    + pref[r][c - 1]
                    - pref[r - 1][c - 1]
                    + matrix[r - 1][c - 1];
            }
        }
    }

    long long sumRegion(int r1, int c1, int r2, int c2) {
        // big rectangle
        // - top strip
        // - left strip
        // + overlap removed twice
        return
            pref[r2 + 1][c2 + 1]
            - pref[r1][c2 + 1]
            - pref[r2 + 1][c1]
            + pref[r1][c1];
    }
};

int main() {
    vector<vector<int>> matrix = {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 9}
    };

    NumMatrix nm(matrix);

    cout << nm.sumRegion(1, 1, 2, 2) << "\n"; // 28
    cout << nm.sumRegion(0, 0, 2, 2) << "\n"; // 45
    cout << nm.sumRegion(0, 1, 1, 2) << "\n"; // 16

    return 0;
}
```

---

### Complexity

Build:

```text
O(rows * cols)
```

Each query:

```text
O(1)
```

---

## 13. Problem Form 2 — Matrix Block Sum

### Problem

For every cell `(i, j)` in a matrix, compute the sum of all cells inside distance `k`.

That means for each cell, we need rectangle:

```text
row range = [i-k, i+k]
col range = [j-k, j+k]
```

But because the rectangle can go outside matrix boundary, we clamp it:

```text
r1 = max(0, i-k)
c1 = max(0, j-k)
r2 = min(rows-1, i+k)
c2 = min(cols-1, j+k)
```

---

### Pattern

```text
many rectangle queries
```

For every cell, we ask:

```text
What is the sum of this rectangle?
```

So this becomes:

```text
2D Prefix Sum + Rectangle Query
```

---

### Why Brute Force Is Slow

For each cell:

```text
scan all cells inside k-distance block
```

If matrix size is:

```text
rows * cols
```

and block area is roughly:

```text
(2k+1) * (2k+1)
```

Then brute force is:

```text
O(rows * cols * k^2)
```

Too slow for large grid.

With 2D prefix:

```text
Build prefix once: O(rows * cols)
Each block query: O(1)
Total: O(rows * cols)
```

---

### Step-by-Step Working

Matrix:

```text
mat =
1 2 3
4 5 6
7 8 9
```

Let:

```text
k = 1
```

For cell:

```text
(i,j) = (1,1)
```

This is center cell value `5`.

Block range:

```text
rows = [1-1, 1+1] = [0,2]
cols = [1-1, 1+1] = [0,2]
```

So rectangle is the whole matrix:

```text
1 2 3
4 5 6
7 8 9
```

Sum:

```text
45
```

For cell:

```text
(i,j) = (0,0)
```

Top-left corner.

Raw block:

```text
rows = [-1,1]
cols = [-1,1]
```

Clamp inside matrix:

```text
rows = [0,1]
cols = [0,1]
```

Rectangle:

```text
1 2
4 5
```

Sum:

```text
12
```

So result at `(0,0)` is:

```text
12
```

---

### Rectangle Query Formula

Using 1-indexed 2D prefix:

```text
sum(r1,c1,r2,c2)
=
pref[r2+1][c2+1]
- pref[r1][c2+1]
- pref[r2+1][c1]
+ pref[r1][c1]
```

---

### Example Output For k = 1

Input:

```text
1 2 3
4 5 6
7 8 9
```

Output block sums:

```text
12 21 16
27 45 33
24 39 28
```

Why?

```text
cell (0,0) -> 1+2+4+5 = 12
cell (0,1) -> 1+2+3+4+5+6 = 21
cell (1,1) -> whole matrix = 45
cell (2,2) -> 5+6+8+9 = 28
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class MatrixBlockSum {
private:
    vector<vector<long long>> pref;
    int rows;
    int cols;

public:
    MatrixBlockSum(vector<vector<int>>& mat) {
        rows = mat.size();
        cols = mat[0].size();

        // 1-indexed prefix matrix.
        // pref[r][c] stores sum of rectangle:
        // (0,0) to (r-1,c-1)
        pref.assign(rows + 1, vector<long long>(cols + 1, 0));

        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {

                pref[r][c]
                    = pref[r - 1][c]
                    + pref[r][c - 1]
                    - pref[r - 1][c - 1]
                    + mat[r - 1][c - 1];
            }
        }
    }

    long long rectangleSum(int r1, int c1, int r2, int c2) {
        return
            pref[r2 + 1][c2 + 1]
            - pref[r1][c2 + 1]
            - pref[r2 + 1][c1]
            + pref[r1][c1];
    }

    vector<vector<long long>> buildBlockSum(int k) {
        vector<vector<long long>> ans(rows, vector<long long>(cols, 0));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                // Clamp block boundaries inside matrix.
                int r1 = max(0, i - k);
                int c1 = max(0, j - k);
                int r2 = min(rows - 1, i + k);
                int c2 = min(cols - 1, j + k);

                // Each block sum is one O(1) rectangle query.
                ans[i][j] = rectangleSum(r1, c1, r2, c2);
            }
        }

        return ans;
    }
};

int main() {
    vector<vector<int>> mat = {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 9}
    };

    int k = 1;

    MatrixBlockSum solver(mat);

    vector<vector<long long>> ans = solver.buildBlockSum(k);

    for (auto &row : ans) {
        for (long long x : row) {
            cout << x << " ";
        }
        cout << "\n";
    }

    return 0;
}
```

---

### Complexity

Build prefix:

```text
O(rows * cols)
```

For every cell query:

```text
O(1)
```

Total:

```text
O(rows * cols)
```

Space:

```text
O(rows * cols)
```

---

### CP / FAANG Recognition

Use this pattern when the problem says:

```text
For every cell, compute sum around it
```

or:

```text
sum of submatrix centered at each cell
```

or:

```text
many rectangle sum queries on static matrix
```

This is exactly:

```text
2D Prefix Sum
```

---

## 14. Problem Form 3 — Heatmap Aggregation

### Problem

You have a 2D grid where each cell stores traffic count:

```text
traffic[r][c] = number of requests from this region
```

You need to answer queries:

```text
How much traffic came from rectangular region:
(r1,c1) -> (r2,c2)?
```

---

### Pattern

```text
2D range aggregation
```

This is the same as:

```text
Rectangle Sum Query
```

---

### Step-by-Step Working

Traffic grid:

```text
10 20 30
40 50 60
70 80 90
```

Query:

```text
region (0,1) -> (1,2)
```

Rectangle:

```text
20 30
50 60
```

Manual sum:

```text
20 + 30 + 50 + 60 = 160
```

2D prefix:

```text
pref =
0   0   0   0
0  10  30  60
0  50 120 210
0 120 270 450
```

Formula:

```text
pref[2][3] - pref[0][3] - pref[2][1] + pref[0][1]
= 210 - 0 - 50 + 0
= 160
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class HeatmapAggregator {
private:
    vector<vector<long long>> pref;

public:
    HeatmapAggregator(vector<vector<int>>& traffic) {
        int rows = traffic.size();
        int cols = traffic[0].size();

        pref.assign(rows + 1, vector<long long>(cols + 1, 0));

        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {

                // Build cumulative traffic from top-left.
                pref[r][c]
                    = pref[r - 1][c]
                    + pref[r][c - 1]
                    - pref[r - 1][c - 1]
                    + traffic[r - 1][c - 1];
            }
        }
    }

    long long queryTraffic(int r1, int c1, int r2, int c2) {
        return
            pref[r2 + 1][c2 + 1]
            - pref[r1][c2 + 1]
            - pref[r2 + 1][c1]
            + pref[r1][c1];
    }
};

int main() {
    vector<vector<int>> traffic = {
        {10, 20, 30},
        {40, 50, 60},
        {70, 80, 90}
    };

    HeatmapAggregator agg(traffic);

    cout << agg.queryTraffic(0, 1, 1, 2) << "\n"; // 160

    return 0;
}
```

---

### Real System Mapping

This pattern maps to:

```text
CDN traffic heatmaps
geo traffic dashboards
user density maps
API request concentration maps
monitoring heatmaps
```

---

## 15. Problem Form 4 — Image Brightness Query

### Problem

An image is represented as a matrix.

Each cell contains brightness:

```text
image[r][c] = pixel brightness
```

Answer queries:

```text
total brightness inside rectangle
```

---

### Pattern

```text
2D prefix sum
```

In image processing, this is also called:

```text
Integral Image
Summed Area Table
```

---

### Step-by-Step Working

Image:

```text
5  10 15
20 25 30
35 40 45
```

Query:

```text
brightness inside (1,0) -> (2,1)
```

Rectangle:

```text
20 25
35 40
```

Manual sum:

```text
20 + 25 + 35 + 40 = 120
```

Use 2D prefix query:

```text
sum = pref[3][2] - pref[1][2] - pref[3][0] + pref[1][0]
```

Because `c1 = 0`, left strip is zero automatically using 1-indexed prefix.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class ImageBrightnessQuery {
private:
    vector<vector<long long>> pref;

public:
    ImageBrightnessQuery(vector<vector<int>>& image) {
        int rows = image.size();
        int cols = image[0].size();

        pref.assign(rows + 1, vector<long long>(cols + 1, 0));

        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {

                // Integral image construction.
                pref[r][c]
                    = pref[r - 1][c]
                    + pref[r][c - 1]
                    - pref[r - 1][c - 1]
                    + image[r - 1][c - 1];
            }
        }
    }

    long long brightness(int r1, int c1, int r2, int c2) {
        return
            pref[r2 + 1][c2 + 1]
            - pref[r1][c2 + 1]
            - pref[r2 + 1][c1]
            + pref[r1][c1];
    }
};

int main() {
    vector<vector<int>> image = {
        {5, 10, 15},
        {20, 25, 30},
        {35, 40, 45}
    };

    ImageBrightnessQuery q(image);

    cout << q.brightness(1, 0, 2, 1) << "\n"; // 120

    return 0;
}
```

---

### Real System Mapping

This appears in:

```text
image processing
computer vision
object detection pre-processing
brightness/contrast analysis
fast patch statistics
```

---

## 16. Problem Form 5 — Grid Population Query

### Problem

A grid stores population count per cell.

```text
population[r][c]
```

Answer:

```text
total population inside rectangular district
```

---

### Pattern

```text
static grid + many rectangular population queries
=> 2D prefix sum
```

---

### Step-by-Step Working

Population grid:

```text
100 200 300
400 500 600
700 800 900
```

Query:

```text
district (1,1) -> (2,2)
```

Rectangle:

```text
500 600
800 900
```

Manual sum:

```text
500 + 600 + 800 + 900 = 2800
```

2D prefix formula:

```text
answer =
pref[3][3]
- pref[1][3]
- pref[3][1]
+ pref[1][1]
```

The same rectangle query formula works for any grid values.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class PopulationGrid {
private:
    vector<vector<long long>> pref;

public:
    PopulationGrid(vector<vector<int>>& population) {
        int rows = population.size();
        int cols = population[0].size();

        pref.assign(rows + 1, vector<long long>(cols + 1, 0));

        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {

                pref[r][c]
                    = pref[r - 1][c]
                    + pref[r][c - 1]
                    - pref[r - 1][c - 1]
                    + population[r - 1][c - 1];
            }
        }
    }

    long long districtPopulation(int r1, int c1, int r2, int c2) {
        return
            pref[r2 + 1][c2 + 1]
            - pref[r1][c2 + 1]
            - pref[r2 + 1][c1]
            + pref[r1][c1];
    }
};

int main() {
    vector<vector<int>> population = {
        {100, 200, 300},
        {400, 500, 600},
        {700, 800, 900}
    };

    PopulationGrid grid(population);

    cout << grid.districtPopulation(1, 1, 2, 2) << "\n"; // 2800

    return 0;
}
```

---

### Real System Mapping

This maps to:

```text
population dashboards
regional analytics
city planning
delivery coverage estimation
driver/rider density grid
resource density query
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
