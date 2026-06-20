# 028_Slope_And_Line_Basics.md

# Slope And Line Basics For Competitive Programming

---

# 1. Introduction

Slope and line concepts are one of the MOST important geometry foundations in CP.

They appear heavily in:
- computational geometry
- collinearity problems
- graphs
- grids
- convex hull
- line sweep
- coordinate geometry

Strong contestants think slope as:

```text
rate of change between points
```

This intuition is critical.

---

# 2. What Is Slope?

Slope measures:

```text
how steep a line is
```

Suppose points:

```text
(x1, y1)
(x2, y2)
```

Slope formula:

```text
(y2 - y1)
/
(x2 - x1)
```

---

# 3. Core Slope Intuition

Slope means:

```text
vertical change
/
horizontal change
```

Also called:

```text
rise / run
```

---

# 4. Positive Slope

If:

```text
y increases
when x increases
```

then slope positive.

Example:

```text
(1,2)
(3,6)
```

Slope:

```text
(6-2)/(3-1)
=
4/2
=
2
```

---

# 5. Negative Slope

If:
```text
y decreases
when x increases
```

then slope negative.

Example:

```text
(1,5)
(3,1)
```

Slope:

```text
(1-5)/(3-1)
=
-4/2
=
-2
```

---

# 6. Zero Slope

Horizontal line.

Observation:

```text
y does not change
```

Thus:

```text
dy = 0
```

Slope:

```text
0
```

---

# 7. Infinite Slope

Vertical line.

Observation:

```text
x does not change
```

Thus:

```text
dx = 0
```

Division by zero:
```text
undefined slope
```

Very important geometry case.

---

# 8. Collinearity Observation

MOST IMPORTANT GEOMETRY OBSERVATION.

Three points collinear if:
```text
same slope
```

Instead of division:

Use:

```text
(y2-y1)*(x3-x2)
=
(y3-y2)*(x2-x1)
```

Avoids:
- floating-point precision
- division

Very important CP trick.

---

# 9. Equation Of Line

Common form:

```text
y = mx + c
```

Where:
- m = slope
- c = intercept

Very important geometry foundation.

---

# 10. Parallel Lines

Parallel lines have:

```text
same slope
```

Very important observation.

---

# 11. Perpendicular Lines

Perpendicular slopes satisfy:

```text
m1 * m2 = -1
```

Very important geometry relation.

---

# 12. Direction Vector Observation

Slope actually represents:

```text
direction
```

Direction vector:

```text
(dx, dy)
```

Very important in:
- geometry
- graphs
- computational geometry

---

# 13. Floating Point Precision Trap

Never compare:

```text
double slopes directly
```

Use:
- cross multiplication
- integer comparisons

Very important CP optimization.

---

# 14. CP Observation Mindset

Strong contestants think:

```text
Can division be avoided?
Can slope become vector relation?
Can geometry become algebra?
```

This is critical geometry intuition.

---

# 15. CP / FAANG Problem Forms

---

# Form 1 — Compute Slope

## Problem

Find slope between two points.

---

## Observation

Rise over run.

Formula:

```text
(y2-y1)/(x2-x1)
```

---

## Step-by-Step Working

Points:

```text
(1,2)
(3,6)
```

```text
(6-2)/(3-1)
=
4/2
=
2
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double slope(
    int x1,
    int y1,
    int x2,
    int y2
) {

    return (double)(y2 - y1)
         / (x2 - x1);
}
```

---

# Form 2 — Collinearity Check

## Problem

Check if three points lie on same line.

---

## Observation

Equal slopes.

Use cross multiplication.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool collinear(
    int x1,
    int y1,
    int x2,
    int y2,
    int x3,
    int y3
) {

    return 1LL * (y2 - y1)
         * (x3 - x2)

        ==

           1LL * (y3 - y2)
         * (x2 - x1);
}
```

---

# Form 3 — Parallel Lines

## Problem

Check if lines parallel.

---

## Observation

Same slope.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool parallel(
    int dx1,
    int dy1,
    int dx2,
    int dy2
) {

    return 1LL * dy1 * dx2
        == 1LL * dy2 * dx1;
}
```

---

# Form 4 — Vertical Line

## Problem

Check if line vertical.

---

## Observation

dx = 0

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool verticalLine(
    int x1,
    int x2
) {

    return x1 == x2;
}
```

---

# Form 5 — Horizontal Line

## Problem

Check if line horizontal.

---

## Observation

dy = 0

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool horizontalLine(
    int y1,
    int y2
) {

    return y1 == y2;
}
```

---

# Form 6 — Perpendicular Lines

## Problem

Check perpendicular slopes.

---

## Observation

m1*m2 = -1

Use vector dot product:
```text
dx1*dx2 + dy1*dy2 = 0
```

Better integer approach.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool perpendicular(
    int dx1,
    int dy1,
    int dx2,
    int dy2
) {

    return dx1 * dx2
         + dy1 * dy2
         == 0;
}
```

---

# Form 7 — Direction Vector

## Problem

Represent line direction.

---

## Observation

Use:
```text
(dx, dy)
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

pair<int,int> direction(
    int x1,
    int y1,
    int x2,
    int y2
) {

    return {
        x2 - x1,
        y2 - y1
    };
}
```

---

# 16. Real World Applications

| Real System | Usage |
|---|---|
| Google Maps | route geometry |
| CAD systems | line calculations |
| Computer graphics | rendering |
| Robotics | movement direction |
| GIS systems | spatial analysis |
| Physics engines | trajectory calculations |
| Game engines | collision geometry |

---

# 17. Real Engineering Insight

Slope usually means:

```text
Direction
+
Rate Of Change
```

This mindset is extremely important.

---

# 18. Observation Recognition Signals

Look for:

```text
1. collinearity
2. geometry
3. straight lines
4. direction
5. parallel lines
6. coordinate points
7. vectors
8. line equations
```

---

# 19. Decision Tree

```text
Need steepness?
→ slope

Need collinearity?
→ cross multiplication

Need direction?
→ vector

Need parallel check?
→ equal slopes

Need perpendicular check?
→ dot product = 0
```

---

# 20. Common Traps

```text
1. Division by zero
2. Floating-point precision
3. Wrong slope comparison
4. Forgetting vertical lines
5. Integer overflow in multiplication
6. Sign mistakes
7. Comparing doubles directly
8. Wrong coordinate order
```

---

# 21. Final Checklist

Before solving:

```text
1. Is geometry involved?
2. Can slope simplify?
3. Should division be avoided?
4. Is collinearity needed?
5. Are vectors more stable?
6. Is precision safe?
7. Are vertical lines handled?
8. Can algebra replace geometry?
```

---

# 22. Final Mental Shortcut

```text
Slope
=
Direction
+
Rate Of Change
+
Geometry To Algebra Conversion
```
