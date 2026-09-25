# 029_Area_And_Orientation.md

# Area And Orientation For Competitive Programming

---

# 1. Introduction

Area and Orientation are one of the MOST important geometry foundations in CP.

They appear heavily in:
- computational geometry
- convex hull
- polygon problems
- line intersection
- geometry graphs
- orientation tests
- sweep line algorithms

Strong contestants think orientation as:

```text
Which direction does a turn make?
```

and area as:

```text
signed geometric space
```

This intuition is critical.

---

# 2. Triangle Area Formula

Given points:

```text
(x1,y1)
(x2,y2)
(x3,y3)
```

Triangle area formula:

```text
| x1(y2-y3)
+ x2(y3-y1)
+ x3(y1-y2) |
/ 2
```

Very important geometry formula.

---

# 3. Why Area Formula Works

Area is based on:

```text
cross product geometry
```

It measures:
- spatial spread
- orientation
- direction

Very important geometric intuition.

---

# 4. Doubled Area Observation

VERY IMPORTANT CP OPTIMIZATION.

Often:
```text
division by 2 unnecessary
```

Use:

```text
2 × area
```

instead.

Avoids:
- floating point
- precision errors

Huge optimization.

---

# Example

Instead of:
```text
12.5
```

store:
```text
25
```

Very common CP trick.

---

# 5. Orientation Meaning

Orientation tells:

```text
left turn
right turn
or straight line
```

between 3 points.

MOST IMPORTANT GEOMETRY OBSERVATION.

---

# 6. Orientation Formula

Given:

```text
A(x1,y1)
B(x2,y2)
C(x3,y3)
```

Compute:

```text
(x2-x1)*(y3-y1)
-
(y2-y1)*(x3-x1)
```

This is:

```text
cross product
```

---

# 7. Orientation Results

If:

```text
value > 0
```

→ counterclockwise turn

---

If:

```text
value < 0
```

→ clockwise turn

---

If:

```text
value = 0
```

→ collinear points

---

# 8. Cross Product Intuition

Cross product measures:

```text
signed area
```

and:
```text
rotation direction
```

This is the MOST important geometry intuition.

---

# 9. Collinearity Observation

Three points collinear if:

```text
orientation = 0
```

Very important geometry trick.

---

# 10. Convex Hull Connection

Convex hull algorithms heavily use:
- orientation
- left turn tests

Very important advanced geometry topic.

---

# 11. Polygon Area Observation

Polygon area can be computed using:

```text
Shoelace Formula
```

based on:
- orientation
- cross products

Very important geometry pattern.

---

# 12. Floating Point Precision Trap

Never compare floating-point areas directly.

Prefer:
- doubled area
- integer orientation

Very important CP optimization.

---

# 13. Geometry As Vector Math

Strong contestants think geometry as:

```text
vector algebra
```

NOT:
```text
drawing pictures
```

This mindset is critical.

---

# 14. CP Observation Mindset

Strong contestants think:

```text
Can orientation solve this?
Can cross product replace geometry?
Can signed area simplify?
```

This is core computational geometry intuition.

---

# 15. CP / FAANG Problem Forms

---

# Form 1 — Triangle Area

## Problem

Compute triangle area.

---

## Observation

Use determinant formula.

---

## Step-by-Step Working

Points:

```text
(0,0)
(4,0)
(0,3)
```

Area:

```text
4×3 / 2
=
6
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double triangleArea(
    int x1,
    int y1,
    int x2,
    int y2,
    int x3,
    int y3
) {

    double area = abs(
          x1 * (y2 - y3)
        + x2 * (y3 - y1)
        + x3 * (y1 - y2)
    );

    return area / 2.0;
}
```

---

# Form 2 — Doubled Area

## Problem

Avoid floating-point area.

---

## Observation

Use:
```text
2 × area
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long doubledArea(
    int x1,
    int y1,
    int x2,
    int y2,
    int x3,
    int y3
) {

    return abs(
          1LL * x1 * (y2 - y3)
        + 1LL * x2 * (y3 - y1)
        + 1LL * x3 * (y1 - y2)
    );
}
```

---

# Form 3 — Orientation Test

## Problem

Determine turn direction.

---

## Observation

Cross product sign.

---

## Step-by-Step Working

Positive:
```text
left turn
```

Negative:
```text
right turn
```

Zero:
```text
collinear
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long orientation(
    int x1,
    int y1,
    int x2,
    int y2,
    int x3,
    int y3
) {

    return 1LL * (x2 - x1)
         * (y3 - y1)

         -

           1LL * (y2 - y1)
         * (x3 - x1);
}
```

---

# Form 4 — Collinearity Check

## Problem

Check if points lie on same line.

---

## Observation

Orientation = 0

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

    return orientation(
        x1,y1,
        x2,y2,
        x3,y3
    ) == 0;
}
```

---

# Form 5 — Clockwise Or Counterclockwise

## Problem

Determine turn type.

---

## Observation

Orientation sign.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string turnType(
    int x1,
    int y1,
    int x2,
    int y2,
    int x3,
    int y3
) {

    long long o =
        orientation(
            x1,y1,
            x2,y2,
            x3,y3
        );

    if (o > 0)
        return "CCW";

    if (o < 0)
        return "CW";

    return "COLLINEAR";
}
```

---

# Form 6 — Polygon Area

## Problem

Compute polygon area.

---

## Observation

Shoelace formula.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double polygonArea(
    vector<pair<int,int>>& p
) {

    long long area = 0;

    int n = p.size();

    for (int i = 0;
         i < n;
         i++) {

        int j = (i + 1) % n;

        area +=
            1LL * p[i].first
          * p[j].second;

        area -=
            1LL * p[i].second
          * p[j].first;
    }

    return abs(area) / 2.0;
}
```

---

# Form 7 — Convex Hull Turn Check

## Problem

Maintain convex turn direction.

---

## Observation

Convex hull uses orientation.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool badTurn(
    pair<int,int> A,
    pair<int,int> B,
    pair<int,int> C
) {

    return orientation(
        A.first, A.second,
        B.first, B.second,
        C.first, C.second
    ) <= 0;
}
```

---

# 16. Real World Applications

| Real System | Usage |
|---|---|
| GIS systems | polygon geometry |
| CAD software | shape computations |
| Robotics | movement orientation |
| Game engines | collision geometry |
| Google Maps | route geometry |
| Graphics engines | polygon rendering |
| Navigation systems | turn direction |

---

# 17. Real Engineering Insight

Orientation usually means:

```text
Direction Of Rotation
```

Area usually means:

```text
Signed Spatial Spread
```

This mindset is extremely important.

---

# 18. Observation Recognition Signals

Look for:

```text
1. geometry
2. polygon area
3. turn direction
4. convex hull
5. collinearity
6. line intersection
7. coordinate geometry
8. vectors
```

---

# 19. Decision Tree

```text
Need turn direction?
→ orientation

Need collinearity?
→ orientation = 0

Need polygon area?
→ shoelace formula

Need convex hull?
→ orientation tests

Need precision-safe geometry?
→ doubled area
```

---

# 20. Common Traps

```text
1. Wrong point order
2. Sign confusion
3. Overflow in multiplication
4. Floating-point precision
5. Forgetting absolute value
6. Clockwise/CCW confusion
7. Incorrect polygon indexing
8. Using division unnecessarily
```

---

# 21. Final Checklist

Before solving:

```text
1. Is orientation useful?
2. Can cross product simplify?
3. Can floating-point be avoided?
4. Is doubled area sufficient?
5. Is polygon geometry involved?
6. Are points ordered correctly?
7. Is turn direction important?
8. Can geometry become vector math?
```

---

# 22. Final Mental Shortcut

```text
Orientation
=
Cross Product
=
Signed Area
=
Turn Direction
```
