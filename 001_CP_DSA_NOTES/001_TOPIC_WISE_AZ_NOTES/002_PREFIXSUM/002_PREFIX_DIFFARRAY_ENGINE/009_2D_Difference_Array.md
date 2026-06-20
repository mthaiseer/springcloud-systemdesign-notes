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

You are given a matrix of size:

```text
rows x cols
```

Initially all cells are zero.

You are given many updates:

```text
(r1, c1, r2, c2, val)
```

Each update means:

```text
add val to every cell inside rectangle (r1,c1) -> (r2,c2)
```

Return the final matrix.

---

### Pattern

```text
Offline rectangle updates
=> 2D Difference Array
```

---

### Problem Simulation

Grid:

```text
rows = 3
cols = 4

initial =
0 0 0 0
0 0 0 0
0 0 0 0
```

Updates:

```text
1) add +5 on (1,1)->(2,3)
2) add +2 on (0,0)->(1,1)
```

---

### Step 1 — Apply Update 1

Update:

```text
+5 on (1,1)->(2,3)
```

Four-corner marking:

```text
diff[1][1] += 5
diff[1][4] -= 5
diff[3][1] -= 5
diff[3][4] += 5
```

---

### Step 2 — Apply Update 2

Update:

```text
+2 on (0,0)->(1,1)
```

Four-corner marking:

```text
diff[0][0] += 2
diff[0][2] -= 2
diff[2][0] -= 2
diff[2][2] += 2
```

---

### Step 3 — Rebuild With 2D Prefix

After all updates, rebuild using:

```text
cell = diff[r][c]
     + top
     + left
     - overlap
```

Final matrix:

```text
2 2 0 0
2 7 5 5
0 5 5 5
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void addRectangle(vector<vector<long long>>& diff,
                  int r1, int c1,
                  int r2, int c2,
                  long long val) {

    // Start adding val from top-left corner.
    diff[r1][c1] += val;

    // Stop val after right boundary.
    diff[r1][c2 + 1] -= val;

    // Stop val after bottom boundary.
    diff[r2 + 1][c1] -= val;

    // Add back overlap that was removed twice.
    diff[r2 + 1][c2 + 1] += val;
}

int main() {
    int rows = 3;
    int cols = 4;

    vector<vector<long long>> diff(
        rows + 1,
        vector<long long>(cols + 1, 0)
    );

    addRectangle(diff, 1, 1, 2, 3, 5);
    addRectangle(diff, 0, 0, 1, 1, 2);

    // Rebuild final matrix using 2D prefix accumulation.
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

A canvas is represented as a grid.

Each operation paints a rectangle and increases the paint intensity by `1`.

After all painting operations, find:

```text
how many coats of paint each cell received
```

---

### Pattern

```text
Rectangle increment updates
=> 2D Difference Array
```

---

### Problem Simulation

Canvas:

```text
4 x 4
```

Paint operations:

```text
1) paint (0,0)->(1,1)
2) paint (1,1)->(3,3)
3) paint (2,0)->(3,2)
```

Initial:

```text
0 0 0 0
0 0 0 0
0 0 0 0
0 0 0 0
```

After all operations, final coats:

```text
1 1 0 0
1 2 1 1
1 2 2 1
1 2 2 1
```

---

### Step-by-Step Logic

Each paint operation is:

```text
add +1 to rectangle
```

So each operation uses four-corner update:

```text
diff[r1][c1] += 1
diff[r1][c2+1] -= 1
diff[r2+1][c1] -= 1
diff[r2+1][c2+1] += 1
```

After all paint operations:

```text
2D prefix rebuild gives final paint count
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class MatrixPainter {
private:
    int rows, cols;
    vector<vector<int>> diff;

public:
    MatrixPainter(int r, int c) {
        rows = r;
        cols = c;

        diff.assign(rows + 1, vector<int>(cols + 1, 0));
    }

    void paint(int r1, int c1, int r2, int c2) {
        diff[r1][c1] += 1;
        diff[r1][c2 + 1] -= 1;
        diff[r2 + 1][c1] -= 1;
        diff[r2 + 1][c2 + 1] += 1;
    }

    vector<vector<int>> buildFinalCanvas() {
        vector<vector<int>> canvas(rows, vector<int>(cols, 0));

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                if (r > 0)
                    diff[r][c] += diff[r - 1][c];

                if (c > 0)
                    diff[r][c] += diff[r][c - 1];

                if (r > 0 && c > 0)
                    diff[r][c] -= diff[r - 1][c - 1];

                canvas[r][c] = diff[r][c];
            }
        }

        return canvas;
    }
};

int main() {
    MatrixPainter painter(4, 4);

    painter.paint(0, 0, 1, 1);
    painter.paint(1, 1, 3, 3);
    painter.paint(2, 0, 3, 2);

    vector<vector<int>> canvas = painter.buildFinalCanvas();

    for (auto &row : canvas) {
        for (int x : row) cout << x << " ";
        cout << "\n";
    }

    return 0;
}
```

---

## 14. Problem Form 3 — Heatmap Updates

### Problem

A heatmap grid stores traffic or user activity.

Each region update increases traffic count in a rectangle.

After many updates, build the final heatmap.

---

### Pattern

```text
Region boost updates
=> 2D Difference Array
```

---

### Problem Simulation

Heatmap:

```text
5 x 5
```

Traffic campaigns:

```text
1) +100 on region (1,1)->(3,3)
2) +50 on region (0,0)->(2,2)
3) +30 on region (2,2)->(4,4)
```

Final heatmap conceptually:

```text
cells inside overlapping regions accumulate all boosts
```

For example:

```text
cell (2,2) receives:
+100
+50
+30
= 180
```

---

### Step-by-Step Logic

Each traffic boost is:

```text
rectangle add
```

So we mark four corners for each campaign.

Then one 2D prefix pass gives final traffic density.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class HeatmapDiff {
private:
    int rows, cols;
    vector<vector<long long>> diff;

public:
    HeatmapDiff(int r, int c) {
        rows = r;
        cols = c;

        diff.assign(rows + 1, vector<long long>(cols + 1, 0));
    }

    void addRegionTraffic(int r1, int c1, int r2, int c2, long long traffic) {
        diff[r1][c1] += traffic;
        diff[r1][c2 + 1] -= traffic;
        diff[r2 + 1][c1] -= traffic;
        diff[r2 + 1][c2 + 1] += traffic;
    }

    vector<vector<long long>> buildHeatmap() {
        vector<vector<long long>> heat(rows, vector<long long>(cols, 0));

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                if (r > 0)
                    diff[r][c] += diff[r - 1][c];

                if (c > 0)
                    diff[r][c] += diff[r][c - 1];

                if (r > 0 && c > 0)
                    diff[r][c] -= diff[r - 1][c - 1];

                heat[r][c] = diff[r][c];
            }
        }

        return heat;
    }
};

int main() {
    HeatmapDiff heatmap(5, 5);

    heatmap.addRegionTraffic(1, 1, 3, 3, 100);
    heatmap.addRegionTraffic(0, 0, 2, 2, 50);
    heatmap.addRegionTraffic(2, 2, 4, 4, 30);

    vector<vector<long long>> result = heatmap.buildHeatmap();

    for (auto &row : result) {
        for (long long x : row) cout << x << " ";
        cout << "\n";
    }

    return 0;
}
```

---

## 15. Problem Form 4 — Grid Damage Simulation

### Problem

A game map is a grid.

Each attack damages a rectangular area.

After all attacks, find total damage received by each cell.

---

### Pattern

```text
Area-of-effect rectangle updates
=> 2D Difference Array
```

---

### Problem Simulation

Grid:

```text
4 x 5
```

Attacks:

```text
1) damage 50 on (1,1)->(2,3)
2) damage 20 on (0,2)->(3,4)
3) heal -10 on (2,0)->(3,1)
```

Cell `(2,2)` receives:

```text
50 from attack 1
20 from attack 2
= 70 damage
```

Cell `(3,0)` receives:

```text
-10 heal
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class GridDamage {
private:
    int rows, cols;
    vector<vector<long long>> diff;

public:
    GridDamage(int r, int c) {
        rows = r;
        cols = c;

        diff.assign(rows + 1, vector<long long>(cols + 1, 0));
    }

    void applyEffect(int r1, int c1, int r2, int c2, long long damage) {
        diff[r1][c1] += damage;
        diff[r1][c2 + 1] -= damage;
        diff[r2 + 1][c1] -= damage;
        diff[r2 + 1][c2 + 1] += damage;
    }

    vector<vector<long long>> buildDamageGrid() {
        vector<vector<long long>> damage(rows, vector<long long>(cols, 0));

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                if (r > 0)
                    diff[r][c] += diff[r - 1][c];

                if (c > 0)
                    diff[r][c] += diff[r][c - 1];

                if (r > 0 && c > 0)
                    diff[r][c] -= diff[r - 1][c - 1];

                damage[r][c] = diff[r][c];
            }
        }

        return damage;
    }
};

int main() {
    GridDamage sim(4, 5);

    sim.applyEffect(1, 1, 2, 3, 50);
    sim.applyEffect(0, 2, 3, 4, 20);
    sim.applyEffect(2, 0, 3, 1, -10);

    vector<vector<long long>> damage = sim.buildDamageGrid();

    for (auto &row : damage) {
        for (long long x : row) cout << x << " ";
        cout << "\n";
    }

    return 0;
}
```

---

## 16. Problem Form 5 — Rectangle Increment Queries

### Problem

Given a grid and many queries:

```text
r1 c1 r2 c2
```

Each query increments the rectangle by `1`.

Return the final grid.

---

### Pattern

```text
2D Difference Array with val = 1
```

---

### Problem Simulation

Grid:

```text
3 x 3
```

Queries:

```text
(0,0)->(1,1)
(1,1)->(2,2)
(0,2)->(2,2)
```

Final grid:

```text
1 1 1
1 2 2
0 1 2
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int rows = 3;
    int cols = 3;

    vector<vector<int>> queries = {
        {0, 0, 1, 1},
        {1, 1, 2, 2},
        {0, 2, 2, 2}
    };

    vector<vector<int>> diff(rows + 1, vector<int>(cols + 1, 0));

    for (auto &q : queries) {
        int r1 = q[0];
        int c1 = q[1];
        int r2 = q[2];
        int c2 = q[3];

        diff[r1][c1] += 1;
        diff[r1][c2 + 1] -= 1;
        diff[r2 + 1][c1] -= 1;
        diff[r2 + 1][c2 + 1] += 1;
    }

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {

            if (r > 0) diff[r][c] += diff[r - 1][c];
            if (c > 0) diff[r][c] += diff[r][c - 1];
            if (r > 0 && c > 0) diff[r][c] -= diff[r - 1][c - 1];

            cout << diff[r][c] << " ";
        }
        cout << "\n";
    }

    return 0;
}
```

---

## 17. Real World Model 1 — Geo Traffic Injection

### Scenario

A system divides the world map into a grid.

Each cell stores traffic pressure.

Regional events inject extra traffic:

```text
Event A:
+100 traffic in rectangle (1,1)->(3,3)

Event B:
+60 traffic in rectangle (0,2)->(2,4)
```

---

### Problem Simulation

Initial geo traffic:

```text
0 0 0 0 0
0 0 0 0 0
0 0 0 0 0
0 0 0 0 0
```

After region boosts, overlapping cells accumulate both values.

Cell `(2,3)` receives:

```text
+100 from Event A
+60 from Event B
= 160
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class GeoTrafficGrid {
private:
    int rows, cols;
    vector<vector<long long>> diff;

public:
    GeoTrafficGrid(int r, int c) {
        rows = r;
        cols = c;
        diff.assign(rows + 1, vector<long long>(cols + 1, 0));
    }

    void injectTraffic(int r1, int c1, int r2, int c2, long long value) {
        diff[r1][c1] += value;
        diff[r1][c2 + 1] -= value;
        diff[r2 + 1][c1] -= value;
        diff[r2 + 1][c2 + 1] += value;
    }

    vector<vector<long long>> materialize() {
        vector<vector<long long>> grid(rows, vector<long long>(cols));

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                if (r > 0) diff[r][c] += diff[r - 1][c];
                if (c > 0) diff[r][c] += diff[r][c - 1];
                if (r > 0 && c > 0) diff[r][c] -= diff[r - 1][c - 1];

                grid[r][c] = diff[r][c];
            }
        }

        return grid;
    }
};

int main() {
    GeoTrafficGrid grid(4, 5);

    grid.injectTraffic(1, 1, 3, 3, 100);
    grid.injectTraffic(0, 2, 2, 4, 60);

    auto result = grid.materialize();

    for (auto &row : result) {
        for (auto x : row) cout << x << " ";
        cout << "\n";
    }

    return 0;
}
```

---

## 18. Real World Model 2 — CDN Heatmap Aggregation

### Scenario

A CDN provider tracks traffic density over regions.

Multiple campaigns and live events create traffic pressure in rectangular zones.

Need final heatmap for scaling decisions.

---

### Problem Simulation

CDN grid:

```text
rows = 5
cols = 5
```

Events:

```text
sports event: +500 on (1,1)->(3,3)
concert:      +300 on (2,2)->(4,4)
news spike:   +200 on (0,0)->(1,4)
```

Important overlap:

```text
cell (2,2):
sports + concert = 800
```

cell `(1,2)`:

```text
sports + news = 700
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Event {
    int r1, c1, r2, c2;
    long long load;
};

int main() {
    int rows = 5, cols = 5;

    vector<Event> events = {
        {1, 1, 3, 3, 500},
        {2, 2, 4, 4, 300},
        {0, 0, 1, 4, 200}
    };

    vector<vector<long long>> diff(rows + 1, vector<long long>(cols + 1, 0));

    for (auto &e : events) {
        diff[e.r1][e.c1] += e.load;
        diff[e.r1][e.c2 + 1] -= e.load;
        diff[e.r2 + 1][e.c1] -= e.load;
        diff[e.r2 + 1][e.c2 + 1] += e.load;
    }

    long long maxLoad = 0;

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {

            if (r > 0) diff[r][c] += diff[r - 1][c];
            if (c > 0) diff[r][c] += diff[r][c - 1];
            if (r > 0 && c > 0) diff[r][c] -= diff[r - 1][c - 1];

            maxLoad = max(maxLoad, diff[r][c]);

            cout << diff[r][c] << " ";
        }
        cout << "\n";
    }

    cout << "Peak CDN load = " << maxLoad << "\n";

    return 0;
}
```

---

## 19. Real World Model 3 — Video Stream Load Zones

### Scenario

A streaming platform tracks load across geographic edge zones.

Events:

```text
football match
concert stream
breaking news
```

Each event increases demand over a region.

Need:

```text
final edge-cache pressure map
```

---

### Problem Simulation

Grid:

```text
4 x 4
```

Events:

```text
football +1000 on (0,0)->(2,2)
concert  +700  on (1,1)->(3,3)
```

Overlap:

```text
(1,1), (1,2), (2,1), (2,2)
```

receive:

```text
1700
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int rows = 4, cols = 4;

    vector<vector<long long>> diff(rows + 1, vector<long long>(cols + 1, 0));

    auto addLoad = [&](int r1, int c1, int r2, int c2, long long load) {
        diff[r1][c1] += load;
        diff[r1][c2 + 1] -= load;
        diff[r2 + 1][c1] -= load;
        diff[r2 + 1][c2 + 1] += load;
    };

    addLoad(0, 0, 2, 2, 1000);
    addLoad(1, 1, 3, 3, 700);

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {

            if (r > 0) diff[r][c] += diff[r - 1][c];
            if (c > 0) diff[r][c] += diff[r][c - 1];
            if (r > 0 && c > 0) diff[r][c] -= diff[r - 1][c - 1];

            cout << diff[r][c] << " ";
        }
        cout << "\n";
    }

    return 0;
}
```

---

## 20. Real World Model 4 — Distributed Grid Simulation

### Scenario

A game/simulation world is a huge grid.

Events affect rectangle areas:

```text
rain increases water level
fire reduces health
pollution spreads
resource grows
```

Updating every affected cell immediately can be expensive.

---

### Problem Simulation

World grid:

```text
5 x 5
```

Events:

```text
rain +10 on (0,0)->(4,4)
fire -30 on (1,1)->(3,3)
resource +5 on (2,0)->(4,2)
```

Cell `(2,2)` receives:

```text
+10 rain
-30 fire
+5 resource
= -15
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RectEvent {
    int r1, c1, r2, c2;
    long long delta;
};

int main() {
    int rows = 5, cols = 5;

    vector<RectEvent> events = {
        {0, 0, 4, 4, 10},
        {1, 1, 3, 3, -30},
        {2, 0, 4, 2, 5}
    };

    vector<vector<long long>> diff(rows + 1, vector<long long>(cols + 1, 0));

    for (auto &e : events) {
        diff[e.r1][e.c1] += e.delta;
        diff[e.r1][e.c2 + 1] -= e.delta;
        diff[e.r2 + 1][e.c1] -= e.delta;
        diff[e.r2 + 1][e.c2 + 1] += e.delta;
    }

    vector<vector<long long>> world(rows, vector<long long>(cols, 0));

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {

            if (r > 0) diff[r][c] += diff[r - 1][c];
            if (c > 0) diff[r][c] += diff[r][c - 1];
            if (r > 0 && c > 0) diff[r][c] -= diff[r - 1][c - 1];

            world[r][c] = diff[r][c];
        }
    }

    for (auto &row : world) {
        for (long long x : row) cout << x << " ";
        cout << "\n";
    }

    return 0;
}
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
