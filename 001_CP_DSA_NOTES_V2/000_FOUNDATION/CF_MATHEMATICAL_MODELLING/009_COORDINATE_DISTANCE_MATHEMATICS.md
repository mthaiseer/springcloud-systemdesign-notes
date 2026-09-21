# Part 8. Coordinate & Distance Mathematics

> **Core idea:** Convert words such as **distance, overlap, meeting point, grid steps, exact moves** into formulas on coordinates. The geometry often disappears once the correct metric or interval formula is identified.

## Table of Contents

- [8.1 Distance Formulas](#81-distance-formulas)
- [8.2 Interval Overlap](#82-interval-overlap)
- [8.3 Median Minimizes Total Distance](#83-median-minimizes-total-distance)
- [8.4 Grid Movement](#84-grid-movement)
- [8.5 Rotating Coordinates](#85-rotating-coordinates)
- [Section Summary](#section-summary)
- [30-Second Revision](#30-second-revision)

---

## 8.1 Distance Formulas

### ASCII / Structural Visual

```text
NUMBER LINE

x=2                    y=7
 ●----------------------●

distance = abs(7-2) = 5


GRID

(1,2) ●
       │
       │ 4 vertical
       │
       └──────────● (4,6)
          3 horizontal

Manhattan = 3+4 = 7
Chebyshev = max(3,4) = 4
```

### Core Formula / Rule

| Movement Model | Distance |
|---|---|
| number line | `abs(x-y)` |
| Manhattan — 4 directions | `abs(x1-x2)+abs(y1-y2)` |
| Chebyshev — king moves | `max(abs(dx),abs(dy))` |
| Euclidean squared | `dx*dx + dy*dy` |

For comparisons, squared Euclidean distance often avoids `sqrt`.

### Short Derivation

For Manhattan movement:

```text
horizontal moves needed = abs(x1-x2)
vertical moves needed   = abs(y1-y2)

total
= horizontal + vertical
```

For king/Chebyshev movement, one diagonal step reduces both coordinate differences simultaneously, so the larger difference determines the number of moves.

### Visual Dry Run

Points:

```text
A = (1,2)
B = (4,6)

dx = abs(4-1) = 3
dy = abs(6-2) = 4
```

Therefore:

```text
Manhattan:
3+4 = 7

Chebyshev:
max(3,4) = 4

Euclidean²:
3²+4² = 25
```

### Statement → Mathematical Model

```text
"move only up/down/left/right"
              ↓
Manhattan distance

"king-like 8-direction movement"
              ↓
Chebyshev distance

"compare which point is closer"
              ↓
compare squared Euclidean distances
```

### Codeforces Recognition

```text
line distance          → abs difference
4-direction grid       → Manhattan
8-direction / king     → Chebyshev
ordinary geometric comparison → squared Euclidean
```

### Minimal C++

```cpp
long long dx = llabs(x1-x2), dy = llabs(y1-y2);
long long manhattan = dx + dy;
long long chebyshev = max(dx,dy);
long long euclid2 = dx*dx + dy*dy;
```

### Common Traps / Edge Cases

- Identify the allowed movement before choosing a metric.
- Avoid unnecessary floating-point `sqrt` when only comparing distances.
- Coordinate differences and squares may require `long long`.

> **Real-World Engineering Case:** A warehouse robot restricted to horizontal and vertical aisles should use Manhattan distance, while a robot allowed diagonal motion behaves more like Chebyshev distance. Using ordinary straight-line distance would underestimate the actual number of movement steps.

---

## 8.2 Interval Overlap

### ASCII / Structural Visual

```text
Interval A:
      [----------]
      l1        r1

Interval B:
            [----------]
            l2        r2

Overlap:
            [----]
            ↑    ↑
       max(left) min(right)
```

### Core Formula / Rule

For closed intervals:

```text
A = [l1,r1]
B = [l2,r2]

intersection:
[max(l1,l2), min(r1,r2)]
```

Non-empty exactly when:

```text
max(l1,l2) <= min(r1,r2)
```

### Short Derivation

Any common point must satisfy all four constraints:

```text
x >= l1
x >= l2
x <= r1
x <= r2
```

Combine lower bounds:

```text
x >= max(l1,l2)
```

Combine upper bounds:

```text
x <= min(r1,r2)
```

A valid `x` exists iff lower bound does not exceed upper bound.

### Visual Dry Run

```text
A = [2,7]
B = [5,10]

L = max(2,5)  = 5
R = min(7,10) = 7

L <= R
5 <= 7 ✓

intersection = [5,7]
```

No-overlap case:

```text
A = [2,4]
B = [6,9]

L = 6
R = 4

6 <= 4 ✗

empty intersection
```

### Statement → Mathematical Model

```text
"find times when both are available"
                ↓
availability intervals
                ↓
intersection
                ↓
L = max(starts)
R = min(ends)
                ↓
valid iff L <= R
```

### Codeforces Recognition

```text
"both ranges"
"common time"
"intersection"
"simultaneously valid"
        ↓
MAX LEFT + MIN RIGHT
```

### Minimal C++

```cpp
long long L = max(l1,l2);
long long R = min(r1,r2);
bool overlap = (L <= R);
```

### Common Traps / Edge Cases

- For **closed** intervals, touching at one endpoint counts as overlap.
- Half-open intervals such as `[l,r)` use different endpoint logic.
- Normalize malformed intervals first if the problem does not guarantee `l <= r`.

> **Real-World Engineering Case:** If service A has a maintenance window `[02:00,04:00]` and service B `[03:30,05:00]`, their simultaneous downtime is found by taking the later start and earlier end: `[03:30,04:00]`.

---

## 8.3 Median Minimizes Total Distance

### ASCII / Structural Visual

```text
sorted values:

1------2----------------10
       ↑
     median

Choose x to minimize:

abs(x-1) + abs(x-2) + abs(x-10)
```

### Core Formula / Rule

For:

```text
minimize Σ abs(x-Ai)
```

choose a **median** of the sorted values.

The source also notes:

```text
mean minimizes Σ (x-Ai)²
```

### Short Derivation

For two sorted points `a <= b`:

```text
abs(x-a) + abs(x-b) >= b-a
```

Equality holds for any:

```text
a <= x <= b
```

Now pair extremes:

```text
smallest ↔ largest
2nd smallest ↔ 2nd largest
...
```

A median lies inside all relevant central pair intervals, so the pairwise lower bounds can be achieved simultaneously.

### Visual Dry Run

```text
A = [1,2,10]

median = 2

cost at x=2:
abs(2-1)  = 1
abs(2-2)  = 0
abs(2-10) = 8
total     = 9
```

Try `x=5`:

```text
abs(5-1)  = 4
abs(5-2)  = 3
abs(5-10) = 5
total     = 12

9 < 12
```

### Statement → Mathematical Model

```text
"choose meeting position x"
"total walking distance should be minimum"
                  ↓
minimize Σ abs(x-Ai)
                  ↓
sort values
                  ↓
choose median
```

### Codeforces Recognition

```text
minimize total ABSOLUTE distance
        ↓
MEDIAN

minimize total SQUARED distance
        ↓
MEAN
```

### Minimal C++

```cpp
sort(a.begin(),a.end());
long long median = a[a.size()/2];
```

### Common Traps / Edge Cases

- Median corresponds to absolute-distance minimization, not squared-distance minimization.
- For even `n`, any point between the two middle values minimizes the continuous absolute-distance objective; choosing either middle value works for integer-coordinate problems.
- Use `long long` when summing many distances.

> **Real-World Engineering Case:** If several servers send data to one regional aggregation point and network cost is proportional to absolute coordinate distance on a line, placing the aggregator at the median server location minimizes total distance. Moving toward an extreme helps some servers but increases the combined cost for others.

---

## 8.4 Grid Movement

### ASCII / Structural Visual

```text
Target = (3,2)

(0,0)
  │
  │ up 2
  │
  └──────────→ right 3
             (3,2)

minimum steps:
abs(3)+abs(2)=5
```

Exact-step problem:

```text
minimum needed = D

extra steps = k-D

extra movement must be
wasted in pairs:

go → then ←

2 extra steps
```

### Core Formula / Rule

From `(0,0)` to `(a,b)` using 4-direction unit moves:

```text
D = abs(a)+abs(b)
```

Reachable in **exactly `k` steps** iff:

```text
D <= k
AND
(k-D) is even
```

Each step also flips parity of `x+y`.

### Short Derivation

At least `D` steps are necessary.

After reaching the target, extra movement that returns to the same cell can be:

```text
right + left
up + down
```

which consumes `2` steps.

Therefore any extra count must be even:

```text
k-D = 2t
```

### Visual Dry Run

Target:

```text
(a,b) = (2,1)

D = abs(2)+abs(1)
  = 3
```

Case `k=5`:

```text
k-D = 5-3 = 2
even ✓

example path:
R R U R L
        └─┘
      waste 2

reachable
```

Case `k=4`:

```text
k-D = 1
odd ✗

cannot waste exactly one step
and remain at target
```

### Statement → Mathematical Model

```text
"reach (a,b) in exactly k moves"
              ↓
minimum = abs(a)+abs(b)
              ↓
need minimum <= k
              ↓
extra = k-minimum
              ↓
extra % 2 == 0
```

### Codeforces Recognition

```text
grid + exact number of moves
        ↓
DISTANCE + PARITY

"at most k moves"
        ↓
usually only distance bound

"exactly k moves"
        ↓
distance bound + parity
```

### Minimal C++

```cpp
long long d = llabs(a)+llabs(b);
bool ok = (d <= k && (k-d)%2 == 0);
```

### Common Traps / Edge Cases

- Do not stop after checking `D <= k` when the statement says **exactly** `k` moves.
- The parity condition comes from the fact that harmless detours consume moves in pairs.
- The formula assumes standard 4-direction unit moves.

> **Real-World Engineering Case:** A grid robot scheduled to execute exactly 9 movement commands cannot simply stop after reaching its destination in 8. If every harmless detour requires an out-and-back pair of commands, one extra command cannot be consumed safely; two extra commands can.

---

## 8.5 Rotating Coordinates

### ASCII / Structural Visual

Transform:

```text
original coordinates:

(x,y)

   ↓

u = x+y
v = x-y

   ↓

rotated/transformed space
```

For two points:

```text
du = (x1+y1) - (x2+y2)
dv = (x1-y1) - (x2-y2)

Manhattan in (x,y)
        ↓
max(abs(du),abs(dv))
in transformed coordinates
```

### Core Formula / Rule

```text
u = x+y
v = x-y
```

Then:

```text
abs(dx)+abs(dy)
=
max(abs(du),abs(dv))
```

So Manhattan distance becomes Chebyshev distance in the transformed coordinates.

### Short Derivation

Let:

```text
dx = x1-x2
dy = y1-y2

du = dx+dy
dv = dx-dy
```

Identity:

```text
abs(dx)+abs(dy)
=
max(abs(dx+dy), abs(dx-dy))
```

Therefore:

```text
Manhattan
=
max(abs(du),abs(dv))
```

### Visual Dry Run

Use source-style points:

```text
A = (1,2)
B = (4,6)

Manhattan:
abs(1-4)+abs(2-6)
= 3+4
= 7
```

Transform:

```text
A:
u1 = 1+2 = 3
v1 = 1-2 = -1

B:
u2 = 4+6 = 10
v2 = 4-6 = -2
```

Differences:

```text
abs(du) = abs(3-10)   = 7
abs(dv) = abs(-1-(-2))= 1

max(7,1)=7 ✓
```

### Statement → Mathematical Model

```text
"many Manhattan-distance constraints"
              ↓
abs(dx)+abs(dy)
              ↓
transform:
u=x+y
v=x-y
              ↓
distance becomes:
max(abs(du),abs(dv))
```

### Codeforces Recognition

```text
Manhattan geometry
+
awkward diamond-shaped regions
        ↓
TRY:
u=x+y
v=x-y

diamonds can become
axis-aligned / Chebyshev-style constraints
```

### Minimal C++

```cpp
long long u = x+y;
long long v = x-y;
```

### Common Traps / Edge Cases

- Transform **both** points before comparing transformed differences.
- `x+y` and `x-y` may overflow narrower integer types for large coordinates.
- This is a coordinate transformation; do not assume every geometry problem becomes simpler with it.

> **Real-World Engineering Case:** A dispatch system using city-block distance naturally produces diamond-shaped reachable regions in ordinary coordinates. Transforming to `u=x+y` and `v=x-y` can turn those constraints into max-coordinate bounds, making range checks and geometric reasoning much simpler.

---

## Section Summary

| Statement Clue | Mathematical Model |
|---|---|
| distance on a line | `abs(x-y)` |
| 4-direction grid | Manhattan |
| king / 8-direction movement | Chebyshev |
| compare straight-line distances | squared Euclidean |
| common part of intervals | `max(left), min(right)` |
| minimize sum of absolute distances | median |
| minimize sum of squared distances | mean |
| exact grid moves | Manhattan bound + parity |
| Manhattan transformation | `u=x+y`, `v=x-y` |

## 30-Second Revision

```text
┌───────────────────────────────────────────────────────────────┐
│      COORDINATE & DISTANCE MATH — 30 SECOND REVISION         │
├───────────────────────────────────────────────────────────────┤
│ number line       → abs(x-y)                                 │
│ Manhattan         → abs(dx)+abs(dy)                           │
│ Chebyshev         → max(abs(dx),abs(dy))                      │
│ Euclidean compare → dx²+dy²                                  │
│ overlap           → max(left) <= min(right)                   │
│ min Σ abs         → MEDIAN                                   │
│ min Σ square      → MEAN                                     │
│ exact k grid      → D<=k AND (k-D) even                      │
│ rotate coords     → u=x+y, v=x-y                             │
│ Manhattan rotated → max(abs(du),abs(dv))                      │
└───────────────────────────────────────────────────────────────┘
```
