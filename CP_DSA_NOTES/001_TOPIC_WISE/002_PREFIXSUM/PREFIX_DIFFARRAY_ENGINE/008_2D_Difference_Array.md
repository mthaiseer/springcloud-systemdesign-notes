# 008_2D_Difference_Array.md — MiniPrefixSumDifferenceEngine

# 2D Difference Array

> 2D Difference Array extends 1D Difference Array to matrices.
>
> It allows:
>
> ```text
> O(1) rectangle updates
> ```
>
> instead of:
>
> ```text
> O(area of rectangle)
> ```

---

## Clickable Index

1. [What Is 2D Difference Array?](#1-what-is-2d-difference-array)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Mental Model](#3-core-mental-model)
4. [Rectangle Update Problem](#4-rectangle-update-problem)
5. [Brute Force Rectangle Update](#5-brute-force-rectangle-update)
6. [2D Difference Array Formula](#6-2d-difference-array-formula)
7. [Why Four Corners Work](#7-why-four-corners-work)
8. [Visual Intuition](#8-visual-intuition)
9. [Step-by-Step Dry Run — Single Rectangle Update](#9-step-by-step-dry-run--single-rectangle-update)
10. [Step-by-Step Dry Run — Multiple Rectangle Updates](#10-step-by-step-dry-run--multiple-rectangle-updates)
11. [Rebuilding Final Matrix Using 2D Prefix](#11-rebuilding-final-matrix-using-2d-prefix)
12. [Problem Form 1 — Rectangle Range Addition](#12-problem-form-1--rectangle-range-addition)
13. [Problem Form 2 — Matrix Painting](#13-problem-form-2--matrix-painting)
14. [Problem Form 3 — Heatmap Updates](#14-problem-form-3--heatmap-updates)
15. [Problem Form 4 — Grid Damage Simulation](#15-problem-form-4--grid-damage-simulation)
16. [Problem Form 5 — Rectangle Increment Queries](#16-problem-form-5--rectangle-increment-queries)
17. [Real World Model 1 — Geo Traffic Injection](#17-real-world-model-1--geo-traffic-injection)
18. [Real World Model 2 — CDN Heatmap Aggregation](#18-real-world-model-2--cdn-heatmap-aggregation)
19. [Real World Model 3 — Video Stream Load Zones](#19-real-world-model-3--video-stream-load-zones)
20. [Real World Model 4 — Distributed Grid Simulation](#20-real-world-model-4--distributed-grid-simulation)
21. [Decision Tree](#21-decision-tree)
22. [Common Mistakes](#22-common-mistakes)
23. [Complexity](#23-complexity)
24. [Reusable C++ Templates](#24-reusable-c-templates)
25. [CP / FAANG Problem Forms](#25-cp--faang-problem-forms)
26. [Practice Checklist](#26-practice-checklist)
27. [Next Step](#27-next-step)

---

## 1. What Is 2D Difference Array?

2D Difference Array allows efficient rectangle updates on a matrix.

Suppose:

```text
Add +5 to all cells inside rectangle:
(r1,c1) -> (r2,c2)
```

Brute force:

```text
touch every cell in rectangle
```

2D Difference Array:

```text
touch only 4 corners
```

Then a 2D prefix rebuild materializes the final matrix.

---

## 2. Why This Topic Matters

Suppose:

```text
matrix size = 2000 x 2000
updates = 200000
```

Brute force rectangle updates:

```text
O(updates * rectangle area)
```

Too slow.

2D difference array:

```text
each update -> O(1)
final rebuild -> O(rows * cols)
```

Massive optimization.

---

## 3. Core Mental Model

1D Difference Array:

```text
mark where change starts/stops
```

2D Difference Array:

```text
mark where rectangle influence starts/stops
```

Rectangle updates spread automatically during 2D prefix rebuild.

---

## 4. Rectangle Update Problem

Problem:

```text
Matrix initially all zeros.
Apply many rectangle updates.
Need final matrix.
```

Example:

```text
add +5 to rectangle:
(1,1) -> (2,3)
```

---

## 5. Brute Force Rectangle Update

Brute force:

```cpp
for r in [r1,r2]:
    for c in [c1,c2]:
        grid[r][c] += val;
```

If rectangles are large:

```text
too slow
```

---

## 6. 2D Difference Array Formula

For rectangle:

```text
(r1,c1) -> (r2,c2)
```

Add:

```text
+X
```

Do:

```cpp
diff[r1][c1] += X;
diff[r1][c2 + 1] -= X;
diff[r2 + 1][c1] -= X;
diff[r2 + 1][c2 + 1] += X;
```

This is 2D inclusion-exclusion.

---

## 7. Why Four Corners Work

We:

```text
start effect at top-left
cancel to right
cancel downward
restore overlap cancellation
```

Exactly like:

```text
2D prefix inclusion-exclusion
```

but reversed.

---

## 8. Visual Intuition

Rectangle:

```text
(r1,c1) -> (r2,c2)
```

Mark:

```text
+X at top-left
-X after right boundary
-X after bottom boundary
+X at bottom-right overlap
```

During 2D prefix accumulation:

```text
effect spreads across rectangle
```

---

## 9. Step-by-Step Dry Run — Single Rectangle Update

Matrix:

```text
3 x 4
all zeros
```

Update:

```text
add +5 on rectangle:
(1,1) -> (2,3)
```

Initial diff:

```text
0 0 0 0 0
0 0 0 0 0
0 0 0 0 0
0 0 0 0 0
```

Apply:

```text
diff[1][1] += 5
diff[1][4] -= 5
diff[3][1] -= 5
diff[3][4] += 5
```

Diff becomes:

```text
0  0  0  0   0
0  5  0  0  -5
0  0  0  0   0
0 -5  0  0   5
```

After 2D prefix rebuild:

```text
0 0 0 0
0 5 5 5
0 5 5 5
```

Exactly the rectangle got +5.

---

## 10. Step-by-Step Dry Run — Multiple Rectangle Updates

Updates:

```text
1) +2 on (0,0)->(1,1)
2) +3 on (1,1)->(2,2)
```

Apply first:

```text
diff[0][0]+=2
diff[0][2]-=2
diff[2][0]-=2
diff[2][2]+=2
```

Apply second:

```text
diff[1][1]+=3
diff[1][3]-=3
diff[3][1]-=3
diff[3][3]+=3
```

Then perform 2D prefix rebuild.

Final matrix:

```text
2 2 0
2 5 3
0 3 3
```

---

## 11. Rebuilding Final Matrix Using 2D Prefix

Rebuild formula:

```cpp
grid[r][c]
=
diff[r][c]
+
top
+
left
-
overlap
```

Code:

```cpp
for (int r = 0; r < rows; r++) {
    for (int c = 0; c < cols; c++) {

        if (r > 0)
            diff[r][c] += diff[r - 1][c];

        if (c > 0)
            diff[r][c] += diff[r][c - 1];

        if (r > 0 && c > 0)
            diff[r][c] -= diff[r - 1][c - 1];
    }
}
```

Now `diff` becomes final matrix.

---

## 12. Problem Form 1 — Rectangle Range Addition

### Problem

Apply many updates:

```text
add X inside rectangle
```

Need final matrix.

---

### Pattern

```text
2D Difference Array
```

---

### Step-by-Step Working

Grid:

```text
3 x 3
all zeros
```

Update:

```text
+4 on rectangle:
(0,1)->(2,2)
```

Apply:

```text
diff[0][1]+=4
diff[0][3]-=4
diff[3][1]-=4
diff[3][3]+=4
```

2D prefix rebuild gives:

```text
0 4 4
0 4 4
0 4 4
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    int rows = 3;
    int cols = 3;

    vector<vector<long long>> diff(
        rows + 1,
        vector<long long>(cols + 1, 0)
    );

    // Add +4 to rectangle (0,1)->(2,2)
    int r1 = 0, c1 = 1;
    int r2 = 2, c2 = 2;
    int val = 4;

    diff[r1][c1] += val;
    diff[r1][c2 + 1] -= val;
    diff[r2 + 1][c1] -= val;
    diff[r2 + 1][c2 + 1] += val;

    // Rebuild final matrix using 2D prefix.
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {

            if (r > 0)
                diff[r][c] += diff[r - 1][c];

            if (c > 0)
                diff[r][c] += diff[r][c - 1];

            if (r > 0 && c > 0)
                diff[r][c] -= diff[r - 1][c - 1];
        }
    }

    // Print final matrix.
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            cout << diff[r][c] << " ";
        }
        cout << "\n";
    }

    return 0;
}
```

---

## 13. Problem Form 2 — Matrix Painting

### Problem

Paint many rectangles on a canvas.

Need final color/intensity grid.

---

### Pattern

```text
2D Difference Array
```

---

### Example

Operations:

```text
Paint rectangle A with +1
Paint rectangle B with +1
```

Final prefix rebuild tells:

```text
how many coats each cell received
```

---

### Real Mapping

```text
image processing
canvas painting
tile updates
pixel overlays
```

---

## 14. Problem Form 3 — Heatmap Updates

### Problem

Many geo regions receive traffic boosts.

Need final heatmap.

---

### Pattern

```text
Rectangle updates on grid
```

---

### Step-by-Step Working

Region update:

```text
+100 traffic on city block rectangle
```

Mark only 4 corners.

After all updates:

```text
2D prefix rebuild
```

gives final traffic density map.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Heatmap {
private:
    vector<vector<long long>> diff;
    int rows, cols;

public:
    Heatmap(int r, int c) {
        rows = r;
        cols = c;

        diff.assign(rows + 1,
                    vector<long long>(cols + 1, 0));
    }

    void addTraffic(
        int r1, int c1,
        int r2, int c2,
        long long val
    ) {

        diff[r1][c1] += val;
        diff[r1][c2 + 1] -= val;
        diff[r2 + 1][c1] -= val;
        diff[r2 + 1][c2 + 1] += val;
    }

    vector<vector<long long>> build() {

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                if (r > 0)
                    diff[r][c] += diff[r - 1][c];

                if (c > 0)
                    diff[r][c] += diff[r][c - 1];

                if (r > 0 && c > 0)
                    diff[r][c] -= diff[r - 1][c - 1];
            }
        }

        vector<vector<long long>> ans(
            rows,
            vector<long long>(cols)
        );

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                ans[r][c] = diff[r][c];
            }
        }

        return ans;
    }
};
```

---

## 15. Problem Form 4 — Grid Damage Simulation

### Problem

Game attacks affect rectangles.

Need final HP reduction grid.

---

### Pattern

```text
2D Difference Array
```

---

### Example

Attack:

```text
rectangle:
(1,1)->(3,3)
damage = 50
```

Instead of damaging every cell:

```text
mark boundaries
```

Then one rebuild computes all damages.

---

### Real Mapping

```text
strategy games
simulation engines
physics grids
AoE attacks
terrain modification
```

---

## 16. Problem Form 5 — Rectangle Increment Queries

### Problem

Many rectangle increments.

Need final grid.

---

### Pattern

```text
offline rectangle updates
```

---

### Recognition

If problem says:

```text
apply many rectangle operations
print final matrix
```

Think:

```text
2D Difference Array
```

---

## 17. Real World Model 1 — Geo Traffic Injection

### Scenario

Traffic spikes occur in geo regions.

Example:

```text
+100 requests/sec in Europe region
+50 requests/sec in US-East region
```

Represent world as grid.

Rectangle updates inject load into regions.

2D difference array stores:

```text
where traffic increase starts/stops
```

2D prefix rebuild gives final geo traffic map.

---

### Backend Mapping

```text
CDN planning
regional traffic estimation
network balancing
geo analytics
```

---

## 18. Real World Model 2 — CDN Heatmap Aggregation

### Scenario

A CDN tracks:

```text
request density across regions
```

Many campaigns increase traffic in rectangular zones.

Instead of updating every cell:

```text
store compact rectangle deltas
```

Later:

```text
materialize heatmap
```

---

### Distributed Systems Insight

Large systems often prefer:

```text
compact delta operations
```

instead of eagerly updating huge grids.

2D difference array teaches exactly this mindset.

---

## 19. Real World Model 3 — Video Stream Load Zones

### Scenario

Streaming platform tracks:

```text
viewer concentration
```

across geo regions.

Events:

```text
sports event increases viewers in region
concert increases viewers elsewhere
```

Each event becomes rectangle update.

Final load grid reconstructed via 2D prefix.

---

### Backend Mapping

```text
video CDN scaling
regional cache pressure
stream replication planning
edge load estimation
```

---

## 20. Real World Model 4 — Distributed Grid Simulation

### Scenario

Large simulation engine maintains:

```text
world grid
```

Events affect rectangular areas:

```text
weather
damage
resource growth
pollution spread
```

Instead of updating every cell immediately:

```text
store rectangle deltas
```

Then batch rebuild world state.

---

### System Insight

This is:

```text
lazy bulk update thinking
```

Very important for:

```text
distributed simulations
game engines
physics systems
grid analytics
```

---

## 21. Decision Tree

```text
Grid problem?
|
+-- Many rectangle sum queries?
|   |
|   +-- 2D Prefix Sum
|
+-- Many rectangle updates?
|   |
|   +-- Need final matrix only?
|       |
|       +-- 2D Difference Array
|
+-- Dynamic updates + queries?
|   |
|   +-- Fenwick 2D / Segment Tree 2D
```

---

## 22. Common Mistakes

### Mistake 1 — Forgetting Bottom-Right Restore

Wrong:

```text
+ top-left
- right
- bottom
```

Correct:

```text
+ top-left
- right
- bottom
+ bottom-right overlap
```

---

### Mistake 2 — Wrong Matrix Size

Allocate:

```text
rows+1 x cols+1
```

or:

```text
rows+2 x cols+2
```

to safely access:

```text
r2+1
c2+1
```

---

### Mistake 3 — Forgetting Final 2D Prefix Rebuild

2D difference array is not final matrix.

Need:

```text
2D prefix accumulation
```

---

### Mistake 4 — Mixing Inclusive/Exclusive Rectangle

Usually:

```text
(r1,c1)->(r2,c2)
```

is inclusive.

---

### Mistake 5 — Using For Online Queries

2D difference array is best for:

```text
offline batch rectangle updates
```

For dynamic queries:

```text
Fenwick 2D / Segment Tree 2D
```

---

## 23. Complexity

For:

```text
U rectangle updates
matrix size rows*cols
```

Complexity:

```text
Apply updates -> O(U)
Rebuild matrix -> O(rows*cols)
```

Space:

```text
O(rows*cols)
```

---

## 24. Reusable C++ Templates

### Template 1 — Rectangle Update

```cpp
void rectangleAdd(
    vector<vector<long long>>& diff,
    int r1, int c1,
    int r2, int c2,
    long long val
) {

    diff[r1][c1] += val;
    diff[r1][c2 + 1] -= val;
    diff[r2 + 1][c1] -= val;
    diff[r2 + 1][c2 + 1] += val;
}
```

---

### Template 2 — Build Final Matrix

```cpp
void build2DPrefix(
    vector<vector<long long>>& diff,
    int rows,
    int cols
) {

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {

            if (r > 0)
                diff[r][c] += diff[r - 1][c];

            if (c > 0)
                diff[r][c] += diff[r][c - 1];

            if (r > 0 && c > 0)
                diff[r][c] -= diff[r - 1][c - 1];
        }
    }
}
```

---

## 25. CP / FAANG Problem Forms

### Problem 1 — Rectangle Add Updates

#### Recognition

```text
many rectangle additions
need final matrix
```

#### Pattern

```text
2D Difference Array
```

---

### Problem 2 — Matrix Painting

#### Recognition

```text
paint many rectangles
```

#### Pattern

```text
rectangle delta accumulation
```

---

### Problem 3 — Heatmap Build

#### Recognition

```text
many region boosts
```

#### Pattern

```text
2D diff + 2D prefix rebuild
```

---

### Problem 4 — Damage Simulation

#### Recognition

```text
AoE rectangle attacks
```

#### Pattern

```text
offline rectangle updates
```

---

### Problem 5 — Rectangle Increment Grid

#### Recognition

```text
apply many rectangle operations
```

#### Pattern

```text
2D Difference Array
```

---

## 26. Practice Checklist

Before using 2D difference array:

```text
1. Many rectangle updates?
2. Need final matrix only?
3. Did I use 4-corner formula?
4. Did I restore bottom-right overlap?
5. Did I allocate rows+1 x cols+1?
6. Did I rebuild with 2D prefix?
7. Inclusive rectangle?
8. Need long long?
9. Static/offline or dynamic?
10. Would Fenwick 2D be better?
```

---

## 27. Next Step

```text
009_Prefix_XOR.md
```

That file introduces:

```text
XOR prefix
range XOR queries
bitwise accumulation
parity tricks
xor inversion
