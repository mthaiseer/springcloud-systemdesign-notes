# 027_Distance_Formula.md

# Distance Formula For Competitive Programming

---

# 1. Introduction

Distance Formula is one of the MOST important geometry foundations in CP.

It appears heavily in:
- geometry
- graphs
- grids
- shortest paths
- nearest points
- clustering
- game maps
- spatial algorithms

Strong contestants think distance formula as:

```text
Pythagorean distance between points
```

This intuition is critical.

---

# 2. What Is Distance Formula?

Distance formula computes:

```text
straight-line distance
```

between two points.

Suppose points:

```text
(x1, y1)
(x2, y2)
```

Distance:

```text
sqrt((x2-x1)^2 + (y2-y1)^2)
```

---

# 3. Why Formula Works

Based on:

```text
Pythagorean theorem
```

Observation:

Horizontal difference:
```text
dx = x2 - x1
```

Vertical difference:
```text
dy = y2 - y1
```

Forms right triangle.

Thus:

```text
distance²
=
dx² + dy²
```

---

# 4. Simple Example

Points:

```text
(1,2)
(4,6)
```

Step-by-step:

```text
dx = 4-1 = 3
dy = 6-2 = 4
```

Distance:

```text
sqrt(3² + 4²)
=
sqrt(9+16)
=
sqrt(25)
=
5
```

---

# 5. Squared Distance Observation

VERY IMPORTANT CP OPTIMIZATION.

Often:

```text
sqrt not needed
```

Instead compare:

```text
distance²
```

because:

```text
sqrt is monotonic
```

Huge optimization.

---

# Example

Compare distances:

```text
25 vs 36
```

No need compute:
```text
5 vs 6
```

---

# 6. Manhattan Distance

Another important distance.

Formula:

```text
|x1-x2| + |y1-y2|
```

Represents:
- grid movement
- taxi movement

Very common in:
- BFS
- grids
- greedy

---

# Example

Points:

```text
(1,2)
(4,6)
```

Manhattan distance:

```text
|4-1| + |6-2|
=
3 + 4
=
7
```

---

# 7. Euclidean Vs Manhattan

Euclidean:
```text
straight line
```

Manhattan:
```text
grid path
```

Very important distinction.

---

# 8. Distance In Higher Dimensions

3D distance:

```text
sqrt(dx² + dy² + dz²)
```

Generalizes naturally.

Very important in:
- geometry
- ML
- spatial systems

---

# 9. Geometry And Graph Connection

Distance often becomes:
- edge weight
- shortest path cost
- nearest node query

Very important in:
- Dijkstra
- MST
- geometry graphs

---

# 10. Nearest Point Problems

Common CP pattern:

```text
minimum distance
```

between points.

Often solved using:
- sorting
- sweep line
- geometry observations

---

# 11. Distance And Circles

Distance determines:
- inside circle
- outside circle
- touching circle

Observation:

```text
distance² <= radius²
```

Often avoid sqrt entirely.

---

# 12. Floating Point Precision

Very important geometry trap.

Avoid:
```text
direct double comparison
```

Use:
```text
EPS precision
```

or compare squared distances.

---

# 13. CP Observation Mindset

Strong contestants think:

```text
Can squared distance avoid sqrt?
Can Manhattan simplify?
Can geometry become graph problem?
```

This is critical geometry intuition.

---

# 14. CP / FAANG Problem Forms

---

# Form 1 — Euclidean Distance

## Problem

Find straight-line distance.

---

## Observation

Pythagorean theorem.

Formula:

```text
sqrt(dx² + dy²)
```

---

## Step-by-Step Working

Points:

```text
(1,2)
(4,6)
```

```text
dx = 3
dy = 4
```

Distance:

```text
sqrt(9+16)
=
5
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double distance2D(
    int x1,
    int y1,
    int x2,
    int y2
) {

    int dx = x2 - x1;
    int dy = y2 - y1;

    return sqrt(
        dx * dx
      + dy * dy
    );
}
```

---

# Form 2 — Squared Distance

## Problem

Compare distances efficiently.

---

## Observation

Avoid sqrt.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long squaredDistance(
    int x1,
    int y1,
    int x2,
    int y2
) {

    long long dx = x2 - x1;
    long long dy = y2 - y1;

    return dx * dx
         + dy * dy;
}
```

---

# Form 3 — Manhattan Distance

## Problem

Find grid distance.

---

## Observation

Horizontal + vertical moves.

---

## Step-by-Step Working

```text
|x1-x2| + |y1-y2|
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

# Form 4 — Inside Circle

## Problem

Check if point inside circle.

---

## Observation

Compare squared distances.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool insideCircle(
    int x,
    int y,
    int cx,
    int cy,
    int r
) {

    long long dx = x - cx;
    long long dy = y - cy;

    return dx * dx
         + dy * dy
         <= 1LL * r * r;
}
```

---

# Form 5 — Nearest Point

## Problem

Find closest point.

---

## Observation

Minimum squared distance.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int nearestPoint(
    vector<pair<int,int>>& p,
    int x,
    int y
) {

    long long best = LLONG_MAX;

    int idx = -1;

    for (int i = 0;
         i < p.size();
         i++) {

        long long dx =
            p[i].first - x;

        long long dy =
            p[i].second - y;

        long long d =
            dx * dx + dy * dy;

        if (d < best) {

            best = d;
            idx = i;
        }
    }

    return idx;
}
```

---

# Form 6 — Distance In 3D

## Problem

Compute 3D distance.

---

## Observation

Extend Pythagorean theorem.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double distance3D(
    int x1,
    int y1,
    int z1,
    int x2,
    int y2,
    int z2
) {

    int dx = x2 - x1;
    int dy = y2 - y1;
    int dz = z2 - z1;

    return sqrt(
        dx * dx
      + dy * dy
      + dz * dz
    );
}
```

---

# Form 7 — Geometry Graph Edge Weight

## Problem

Use distances as graph weights.

---

## Observation

Distance becomes edge cost.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double edgeWeight(
    pair<int,int> a,
    pair<int,int> b
) {

    int dx =
        a.first - b.first;

    int dy =
        a.second - b.second;

    return sqrt(
        dx * dx
      + dy * dy
    );
}
```

---

# 15. Real World Applications

| Real System | Usage |
|---|---|
| Google Maps | route distances |
| Uber | nearest driver |
| Games | collision detection |
| GIS systems | spatial queries |
| Robotics | movement planning |
| Machine learning | clustering |
| Search systems | nearest neighbor |

---

# 16. Real Engineering Insight

Distance formulas usually mean:

```text
Spatial Relationship
Between Points
```

This mindset is extremely important.

---

# 17. Observation Recognition Signals

Look for:

```text
1. nearest point
2. geometry
3. shortest distance
4. coordinates
5. grids
6. circles
7. spatial queries
8. movement costs
```

---

# 18. Decision Tree

```text
Straight-line distance?
→ Euclidean

Grid movement?
→ Manhattan

Only comparison needed?
→ squared distance

Circle containment?
→ compare squared values

Spatial graph?
→ distance as edge weight
```

---

# 19. Common Traps

```text
1. Forgetting squared optimization
2. Overflow in dx²
3. Floating-point precision
4. Wrong Manhattan formula
5. Using sqrt unnecessarily
6. Coordinate sign mistakes
7. Wrong dimensional formula
8. Precision comparison bugs
```

---

# 20. Final Checklist

Before solving:

```text
1. Is geometry involved?
2. Is Manhattan or Euclidean needed?
3. Can squared distance optimize?
4. Is floating-point precision safe?
5. Are coordinates large?
6. Is nearest point required?
7. Is graph interpretation possible?
8. Can distance become edge weight?
```

---

# 21. Final Mental Shortcut

```text
Distance Formula
=
Pythagorean Geometry
+
Spatial Relationships
+
Optimization Using Squared Distance
```
