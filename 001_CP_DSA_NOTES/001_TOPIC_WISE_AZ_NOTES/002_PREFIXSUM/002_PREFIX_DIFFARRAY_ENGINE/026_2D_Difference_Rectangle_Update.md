# 026_2D_Difference_Rectangle_Update.md — MiniPrefixSumDifferenceEngine

# 2D Difference Rectangle Update

> 2D Difference Array is used when we have many rectangle updates on a grid and only need the final grid after all updates.

```text
2D Prefix Sum       -> many rectangle queries
2D Difference Array -> many rectangle updates
```

---

# Clickable Index

1. [What Is 2D Difference Array?](#1-what-is-2d-difference-array)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Mental Model](#3-core-mental-model)
4. [1D Difference Refresher](#4-1d-difference-refresher)
5. [2D Rectangle Update Formula](#5-2d-rectangle-update-formula)
6. [Why Four Corners Work](#6-why-four-corners-work)
7. [Rebuilding Final Grid](#7-rebuilding-final-grid)
8. [Step-by-Step Dry Run — One Rectangle](#8-step-by-step-dry-run--one-rectangle)
9. [Step-by-Step Dry Run — Multiple Rectangles](#9-step-by-step-dry-run--multiple-rectangles)
10. [Problem Form 1 — Rectangle Add Queries](#10-problem-form-1--rectangle-add-queries)
11. [Problem Form 2 — Range Increment On Grid](#11-problem-form-2--range-increment-on-grid)
12. [Problem Form 3 — Count Covered Cells](#12-problem-form-3--count-covered-cells)
13. [Problem Form 4 — Heatmap Updates](#13-problem-form-4--heatmap-updates)
14. [Problem Form 5 — Offline Grid Painting](#14-problem-form-5--offline-grid-painting)
15. [Real World Model 1 — Map Region Load Update](#15-real-world-model-1--map-region-load-update)
16. [Real World Model 2 — Image Brightness Batch Update](#16-real-world-model-2--image-brightness-batch-update)
17. [Real World Model 3 — Geo Surge Pricing Zones](#17-real-world-model-3--geo-surge-pricing-zones)
18. [Real World Model 4 — Warehouse Inventory Adjustment](#18-real-world-model-4--warehouse-inventory-adjustment)
19. [Decision Tree](#19-decision-tree)
20. [Common Mistakes](#20-common-mistakes)
21. [Complexity](#21-complexity)
22. [Reusable C++ Templates](#22-reusable-c-templates)
23. [CP / FAANG Problem Forms](#23-cp--faang-problem-forms)
24. [Practice Checklist](#24-practice-checklist)
25. [Next Step](#25-next-step)

---

# 1. What Is 2D Difference Array?

2D Difference Array lets us add a value to every cell inside a rectangle in:

```text
O(1)
```

per update.

Example:

```text
add +5 to rectangle (r1,c1) -> (r2,c2)
```

Naive way:

```text
loop all cells inside rectangle
```

Optimized way:

```text
touch only four corner markers
```

---

# 2. Why This Topic Matters

This pattern appears in:

```text
grid painting
matrix updates
heatmap updates
coverage counting
image processing
geo-zone updates
offline rectangle operations
```

It converts:

```text
O(Q * rectangle_area)
```

into:

```text
O(Q + N*M)
```

---

# 3. Core Mental Model

A rectangle update should start at the top-left corner and stop after the rectangle boundary.

We use four corner changes:

```text
+ at top-left
- after right edge
- after bottom edge
+ after bottom-right overflow
```

The final 2D prefix rebuild spreads those markers into actual values.

---

# 4. 1D Difference Refresher

1D range add:

```text
add val on [l,r]
```

Do:

```cpp
diff[l] += val;
diff[r + 1] -= val;
```

Then prefix sum reconstructs the final array.

2D difference is the same idea but applied in two directions.

---

# 5. 2D Rectangle Update Formula

Use 1-indexed coordinates.

For rectangle:

```text
(r1,c1) -> (r2,c2)
```

Add `val`:

```cpp
diff[r1][c1] += val;
diff[r1][c2 + 1] -= val;
diff[r2 + 1][c1] -= val;
diff[r2 + 1][c2 + 1] += val;
```

Pattern of signs:

```text
+ -
- +
```

---

# 6. Why Four Corners Work

The `+val` at top-left spreads to the right and downward during reconstruction.

But it spreads too far, so we cancel:

```text
right overflow
bottom overflow
```

The bottom-right overflow is cancelled twice, so we add it back.

This is inclusion-exclusion for updates.

---

# 7. Rebuilding Final Grid

After all updates:

```text
grid[i][j]
=
diff[i][j]
+
grid[i-1][j]
+
grid[i][j-1]
-
grid[i-1][j-1]
```

Markdown-safe formula:

```math
grid[i][j] =
diff[i][j] + grid[i-1][j] + grid[i][j-1] - grid[i-1][j-1]
```

---

# 8. Step-by-Step Dry Run — One Rectangle

Grid:

```text
3 x 3
```

Update:

```text
+5 on rectangle (1,1) -> (2,2)
```

Markers:

```text
diff[1][1] += 5
diff[1][3] -= 5
diff[3][1] -= 5
diff[3][3] += 5
```

Diff marker grid:

```text
 5  0 -5
 0  0  0
-5  0  5
```

After 2D prefix rebuild:

```text
5 5 0
5 5 0
0 0 0
```

---

# 9. Step-by-Step Dry Run — Multiple Rectangles

Grid:

```text
4 x 4
```

Updates:

```text
+2 on (1,1) -> (2,2)
+3 on (2,2) -> (4,4)
```

Expected:

```text
cell (2,2) receives +2 and +3
so cell (2,2) = 5
```

Overlaps naturally accumulate because the diff markers are added together before rebuild.

---

# 10. Problem Form 1 — Rectangle Add Queries

## Problem

Given many rectangle updates:

```text
r1 c1 r2 c2 val
```

Return final grid.

## Pattern Recognition

Use when:

```text
many offline rectangle updates
final grid only
```

Pattern:

```text
2D Difference Array
```

## Step-by-Step Working

```text
1. Allocate diff with n+2 rows and m+2 columns
2. For each update, modify four corners
3. Rebuild using 2D prefix
4. Print final grid
```

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void rectangleAdd(
    vector<vector<long long>>& diff,
    int r1,
    int c1,
    int r2,
    int c2,
    long long val
) {
    diff[r1][c1] += val;
    diff[r1][c2 + 1] -= val;
    diff[r2 + 1][c1] -= val;
    diff[r2 + 1][c2 + 1] += val;
}

int main() {
    int n = 4, m = 4;

    vector<vector<long long>> diff(
        n + 2,
        vector<long long>(m + 2, 0)
    );

    rectangleAdd(diff, 1, 1, 2, 2, 2);
    rectangleAdd(diff, 2, 2, 4, 4, 3);

    vector<vector<long long>> grid(
        n + 1,
        vector<long long>(m + 1, 0)
    );

    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= m; j++) {
            grid[i][j] =
                diff[i][j]
                + grid[i - 1][j]
                + grid[i][j - 1]
                - grid[i - 1][j - 1];

            cout << grid[i][j] << " ";
        }
        cout << "\n";
    }

    return 0;
}
```

---

# 11. Problem Form 2 — Range Increment On Grid

## Problem

Each query increments all cells in a rectangle by `1`.

## Problem Simulation

Updates:

```text
(1,1) -> (2,2)
(2,2) -> (3,3)
```

Cell `(2,2)` is covered twice.

Final value:

```text
2
```

## Pattern Recognition

This is coverage frequency.

Use:

```text
2D difference + rebuild
```

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void addOne(
    vector<vector<int>>& diff,
    int r1,
    int c1,
    int r2,
    int c2
) {
    diff[r1][c1]++;
    diff[r1][c2 + 1]--;
    diff[r2 + 1][c1]--;
    diff[r2 + 1][c2 + 1]++;
}
```

---

# 12. Problem Form 3 — Count Covered Cells

## Problem

After many rectangle updates, count cells covered at least once.

## Problem Simulation

If final grid cell value is:

```text
> 0
```

then the cell is covered.

## Pattern Recognition

Use:

```text
2D difference
then count positive cells
```

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countCovered(
    vector<vector<long long>>& grid,
    int n,
    int m
) {
    long long ans = 0;

    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= m; j++) {
            if (grid[i][j] > 0) {
                ans++;
            }
        }
    }

    return ans;
}
```

---

# 13. Problem Form 4 — Heatmap Updates

## Problem

Every event increases heat in a rectangular region.

## Problem Simulation

Event:

```text
user drag over rectangle
```

Update:

```text
+1 heat to rectangle
```

Final grid:

```text
heat intensity per cell
```

## Pattern Recognition

Use:

```text
2D difference heat accumulation
```

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void addHeat(
    vector<vector<long long>>& diff,
    int r1,
    int c1,
    int r2,
    int c2,
    long long heat
) {
    diff[r1][c1] += heat;
    diff[r1][c2 + 1] -= heat;
    diff[r2 + 1][c1] -= heat;
    diff[r2 + 1][c2 + 1] += heat;
}
```

---

# 14. Problem Form 5 — Offline Grid Painting

## Problem

Apply many paint operations to rectangular regions and return final grid.

## Pattern Recognition

Use when:

```text
updates are offline
only final grid is needed
```

Pattern:

```text
2D difference array
```

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<long long>> rebuild2D(
    vector<vector<long long>>& diff,
    int n,
    int m
) {
    vector<vector<long long>> grid(
        n + 1,
        vector<long long>(m + 1, 0)
    );

    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= m; j++) {
            grid[i][j] =
                diff[i][j]
                + grid[i - 1][j]
                + grid[i][j - 1]
                - grid[i - 1][j - 1];
        }
    }

    return grid;
}
```

---

# 15. Real World Model 1 — Map Region Load Update

## Scenario

A city map is divided into grid cells.

Incident impact:

```text
+50 traffic load on downtown rectangle
```

## Simulation

Multiple incidents overlap.

Final grid stores:

```text
total load per region
```

## Backend Mapping

Used in:

```text
traffic analytics
Uber-like geo systems
incident impact modeling
map heatmaps
```

## C++ Model

```cpp
void addTrafficRegion(
    vector<vector<long long>>& diff,
    int r1,
    int c1,
    int r2,
    int c2,
    long long load
) {
    diff[r1][c1] += load;
    diff[r1][c2 + 1] -= load;
    diff[r2 + 1][c1] -= load;
    diff[r2 + 1][c2 + 1] += load;
}
```

---

# 16. Real World Model 2 — Image Brightness Batch Update

## Scenario

Image is a pixel matrix.

Operation:

```text
increase brightness in rectangle
```

## Simulation

```text
+20 brightness to selected region
```

After all operations, rebuild final image.

## Backend Mapping

Used in:

```text
image processing
graphics engines
photo editing
computer vision preprocessing
```

## C++ Model

```cpp
void brightnessUpdate(
    vector<vector<int>>& diff,
    int r1,
    int c1,
    int r2,
    int c2,
    int delta
) {
    diff[r1][c1] += delta;
    diff[r1][c2 + 1] -= delta;
    diff[r2 + 1][c1] -= delta;
    diff[r2 + 1][c2 + 1] += delta;
}
```

---

# 17. Real World Model 3 — Geo Surge Pricing Zones

## Scenario

Ride-sharing surge applies over rectangular city zones.

## Simulation

```text
+2 surge units on airport zone
+1 surge unit on downtown zone
```

Overlap zones receive combined surge.

## Backend Mapping

Used in:

```text
ride sharing
geo pricing
demand-supply heatmaps
dynamic pricing
```

## C++ Model

```cpp
void addSurgeZone(
    vector<vector<long long>>& diff,
    int r1,
    int c1,
    int r2,
    int c2,
    long long surge
) {
    diff[r1][c1] += surge;
    diff[r1][c2 + 1] -= surge;
    diff[r2 + 1][c1] -= surge;
    diff[r2 + 1][c2 + 1] += surge;
}
```

---

# 18. Real World Model 4 — Warehouse Inventory Adjustment

## Scenario

Warehouse shelf grid receives bulk adjustments.

## Simulation

```text
+100 units in zone rectangle
-20 units in damaged region
```

Final grid gives corrected inventory.

## Backend Mapping

Used in:

```text
warehouse systems
inventory planning
robotic storage grids
logistics analytics
```

## C++ Model

```cpp
void inventoryAdjust(
    vector<vector<long long>>& diff,
    int r1,
    int c1,
    int r2,
    int c2,
    long long delta
) {
    diff[r1][c1] += delta;
    diff[r1][c2 + 1] -= delta;
    diff[r2 + 1][c1] -= delta;
    diff[r2 + 1][c2 + 1] += delta;
}
```

---

# 19. Decision Tree

```text
Grid problem?
|
+-- Many rectangle queries?
|   |
|   +-- 2D Prefix Sum
|
+-- Many rectangle updates?
|   |
|   +-- 2D Difference Array
|
+-- Need final grid only?
|   |
|   +-- Offline 2D difference
|
+-- Updates and queries interleaved?
|   |
|   +-- 2D Fenwick / Segment Tree
|
+-- Need count covered cells?
    |
    +-- 2D difference + final scan
```

---

# 20. Common Mistakes

## Mistake 1 — Forgetting Fourth Corner

Correct four corners:

```text
+ -
- +
```

## Mistake 2 — Wrong Signs

Formula must be:

```cpp
diff[r1][c1] += val;
diff[r1][c2 + 1] -= val;
diff[r2 + 1][c1] -= val;
diff[r2 + 1][c2 + 1] += val;
```

## Mistake 3 — No Padding

Always allocate:

```text
n + 2
m + 2
```

## Mistake 4 — Querying Before Rebuild

Diff grid contains markers, not final values.

## Mistake 5 — Index Confusion

Prefer 1-indexed coordinates.

---

# 21. Complexity

For:

```text
N x M grid
Q updates
```

Complexity:

```text
Updates : O(Q)
Rebuild : O(N*M)
Total   : O(Q + N*M)
Space   : O(N*M)
```

---

# 22. Reusable C++ Templates

## Template 1 — Rectangle Add

```cpp
void rectangleAdd(
    vector<vector<long long>>& diff,
    int r1,
    int c1,
    int r2,
    int c2,
    long long val
) {
    diff[r1][c1] += val;
    diff[r1][c2 + 1] -= val;
    diff[r2 + 1][c1] -= val;
    diff[r2 + 1][c2 + 1] += val;
}
```

## Template 2 — Rebuild

```cpp
vector<vector<long long>> rebuild(
    vector<vector<long long>>& diff,
    int n,
    int m
) {
    vector<vector<long long>> grid(
        n + 1,
        vector<long long>(m + 1, 0)
    );

    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= m; j++) {
            grid[i][j] =
                diff[i][j]
                + grid[i - 1][j]
                + grid[i][j - 1]
                - grid[i - 1][j - 1];
        }
    }

    return grid;
}
```

---

# 23. CP / FAANG Problem Forms

## Problem 1 — Apply Rectangle Updates

### Recognition

```text
many rectangle add operations
final grid needed
```

### Pattern

```text
2D Difference Array
```

### Steps

```text
1. Four-corner update
2. Repeat for all operations
3. Rebuild final grid
```

### Commented C++ Code

```cpp
void update(
    vector<vector<long long>>& diff,
    int r1,
    int c1,
    int r2,
    int c2,
    long long val
) {
    diff[r1][c1] += val;
    diff[r1][c2 + 1] -= val;
    diff[r2 + 1][c1] -= val;
    diff[r2 + 1][c2 + 1] += val;
}
```

---

## Problem 2 — Count Covered Cells

### Recognition

```text
many rectangles
count cells covered at least once
```

### Pattern

```text
2D difference + count positive cells
```

### Commented C++ Code

```cpp
long long coveredCells(
    vector<vector<long long>>& grid,
    int n,
    int m
) {
    long long count = 0;

    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= m; j++) {
            if (grid[i][j] > 0) {
                count++;
            }
        }
    }

    return count;
}
```

---

## Problem 3 — Heatmap Rectangle Updates

### Recognition

```text
rectangle intensity updates
```

### Pattern

```text
2D difference accumulation
```

### Commented C++ Code

```cpp
void addHeat(
    vector<vector<long long>>& diff,
    int r1,
    int c1,
    int r2,
    int c2,
    long long heat
) {
    diff[r1][c1] += heat;
    diff[r1][c2 + 1] -= heat;
    diff[r2 + 1][c1] -= heat;
    diff[r2 + 1][c2 + 1] += heat;
}
```

---

## Problem 4 — Offline Grid Painting

### Recognition

```text
many paint rectangles
only final grid requested
```

### Pattern

```text
offline 2D difference
```

### Commented C++ Code

```cpp
vector<vector<long long>> finalGrid(
    vector<vector<long long>>& diff,
    int n,
    int m
) {
    return rebuild(diff, n, m);
}
```

---

## Problem 5 — Online Rectangle Updates

### Recognition

```text
updates and queries are interleaved
```

### Pattern

```text
2D Fenwick / Segment Tree
```

2D Difference is best for offline updates. Online problems need dynamic structures.

---

# 24. Practice Checklist

```text
1. Is the problem grid-based?
2. Are there many rectangle updates?
3. Is only final grid needed?
4. Are operations offline?
5. Did I use four-corner update?
6. Are signs correct?
7. Did I allocate n+2 and m+2?
8. Did I rebuild using 2D prefix?
9. Am I using 1-indexed coordinates?
10. Do I need 2D Fenwick instead?
```

---

# 25. Next Step

```text
027_Number_Of_Submatrices_Target.md
```

Next file covers:

```text
submatrix sum target
row compression
2D to 1D reduction
prefix hashmap
FAANG hard matrix pattern
```
