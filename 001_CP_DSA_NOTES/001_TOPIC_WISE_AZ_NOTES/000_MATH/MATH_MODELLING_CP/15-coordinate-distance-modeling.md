# CP Mathematical Modeling Mini-Course

## 15. Coordinate & Distance Modeling

> **Goal:** Convert movement, position, grid, meeting, reachability, and geometry-like stories into simple coordinate equations.
>
> Core habit:
>
> ```text
> Draw a number line or coordinate grid first.
> Then ask: what must change in x and y?
> ```

---

# Chapter Tree

```text
15. COORDINATE & DISTANCE MODELING
│
├── 15.1 Position as a number
├── 15.2 Distance on a number line
├── 15.3 Direction and signed movement
├── 15.4 Position after k moves
├── 15.5 Reachability on a line
├── 15.6 Coordinates in 2D
├── 15.7 Horizontal and vertical displacement
├── 15.8 Manhattan distance
├── 15.9 Euclidean distance intuition
├── 15.10 Chebyshev distance
├── 15.11 Movement sets determine distance
├── 15.12 Parity in grid movement
├── 15.13 Meeting and relative coordinates
├── 15.14 Translation / normalization
├── 15.15 Bounding boxes and extremes
├── 15.16 Symmetry
├── 15.17 Transforming coordinates
└── 15.18 CF-style modeling workflow
```

Central engine:

```text
MOVEMENT STORY
      ↓
choose coordinate system
      ↓
start = (x1,y1)
target = (x2,y2)
      ↓
dx = x2-x1
dy = y2-y1
      ↓
What moves are legal?
      ↓
What does one move do to dx/dy?
      ↓
distance / feasibility / minimum moves
```

---

# 15.1 Number-Line Modeling

If a problem involves positions along one dimension, draw:

```text
<------------------------------------>
... -2 -1  0  1  2  3  4  5  6 ...
```

Suppose:

```text
A = 2
B = 7
```

Distance is:

```text
|A-B|
=
|2-7|
=
5
```

Why absolute value?

Because distance is non-negative regardless of direction.

```text
2 -> 7 : +5
7 -> 2 : -5 displacement

distance = 5 in both cases
```

---

# 15.2 Displacement vs Distance

These are different.

```text
displacement = target - start
distance     = |target - start|
```

Example:

```text
start = 8
target = 3
```

Displacement:

```text
3-8 = -5
```

Meaning:

```text
move 5 units left
```

Distance:

```text
|-5| = 5
```

Use signed displacement when direction matters.

Use absolute distance when only magnitude matters.

---

# 15.3 Position After Movement

Starting at:

```text
x
```

Move right by:

```text
d
```

New position:

```text
x+d
```

Move left:

```text
x-d
```

If signed movement is `m`:

```text
new_position = x+m
```

After movements:

```text
m1,m2,...,mk
```

final position:

```text
x + m1 + m2 + ... + mk
```

So many movement stories are just sum-of-deltas problems.

---

# 15.4 Repeated Fixed Move

Suppose operation:

```text
x -> x+d
```

After `k` moves:

```text
x+kd
```

To reach `y`:

```text
x+kd = y
```

Thus:

```text
y-x = kd
```

This immediately gives divisibility and direction conditions.

This is the coordinate version of Chapter 12 operation modeling.

---

# 15.5 Moving Both Directions

Suppose each move is either:

```text
+d
-d
```

After `k` moves, let:

```text
R = number of +d moves
L = number of -d moves
```

Then:

```text
R+L = k
```

and displacement:

```text
d(R-L)
```

To reach target:

```text
y-x = d(R-L)
```

Now a movement sequence becomes equations in move counts.

---

# 15.6 Exact k Moves and Parity

Simplest case:

```text
each move is +1 or -1
```

After exactly `k` moves:

```text
R+L = k
R-L = y-x
```

Adding:

```text
2R = k + (y-x)
```

So reachability requires:

```text
|y-x| <= k
```

and:

```text
k - |y-x|
```

is even.

Why parity?

After reaching the target in the minimum distance, extra moves must often be wasted in pairs:

```text
right + left
```

which costs:

```text
2 moves
```

This is a common CF modeling pattern.

---

# 15.7 Example — Exact Moves

Start:

```text
0
```

Target:

```text
3
```

Exactly:

```text
5 moves
```

Possible:

```text
R R R R L
```

Net:

```text
+3
```

because:

```text
distance = 3 <= 5
extra = 2
```

even.

Exactly 4 moves?

```text
extra = 4-3 = 1
```

odd.

Impossible with only `+1/-1`.

---

# 15.8 Move At Most k Times

If each move changes position by exactly one and you may use **at most** `k` moves, reaching `y` from `x` only requires:

```text
|y-x| <= k
```

Parity is irrelevant because you can stop early.

This shows why wording matters:

```text
exactly k
```

and:

```text
at most k
```

produce different mathematics.

---

# 15.9 2D Coordinates

Represent a point as:

```text
(x,y)
```

Example:

```text
start  = (2,3)
target = (7,1)
```

Coordinate changes:

```text
dx = 7-2 = 5
dy = 1-3 = -2
```

Meaning:

```text
5 units right
2 units down
```

Often the entire geometry story reduces to:

```text
dx
dy
```

---

# 15.10 Normalize the Start to Origin

Instead of working with:

```text
start  = (100,50)
target = (107,44)
```

subtract the starting coordinates from everything.

Then:

```text
start  = (0,0)
target = (7,-6)
```

Nothing about relative movement changes.

This is **translation invariance**.

Mental habit:

```text
If only relative positions matter,
move the origin to a convenient point.
```

---

# 15.11 Manhattan Distance

Suppose legal moves are:

```text
up    : (x,y) -> (x,y+1)
down  : (x,y) -> (x,y-1)
right : (x,y) -> (x+1,y)
left  : (x,y) -> (x-1,y)
```

To move from:

```text
(x1,y1)
```

to:

```text
(x2,y2)
```

you must fix horizontal difference:

```text
|x2-x1|
```

and vertical difference:

```text
|y2-y1|
```

Each move fixes only one coordinate by 1.

Therefore minimum moves:

```text
|x2-x1| + |y2-y1|
```

This is Manhattan distance.

---

# 15.12 Why Manhattan Distance Is a Lower Bound

Required horizontal progress:

```text
|dx|
```

Required vertical progress:

```text
|dy|
```

A legal move contributes to only one.

Therefore any path needs at least:

```text
|dx|+|dy|
```

moves.

And we can achieve it:

```text
perform |dx| horizontal moves
perform |dy| vertical moves
```

So:

```text
lower bound = achievable
```

therefore it is the exact minimum.

This is a standard way to prove minimum moves.

---

# 15.13 Example — Grid Movement

Start:

```text
(1,2)
```

Target:

```text
(5,7)
```

Differences:

```text
dx = 4
dy = 5
```

Minimum orthogonal moves:

```text
4+5 = 9
```

One valid path:

```text
RRRR
UUUUU
```

Order does not matter for the minimum count.

---

# 15.14 Diagonal Moves Change the Metric

Suppose one move can change both coordinates by one:

```text
(x,y) -> (x±1,y±1)
```

and perhaps orthogonal moves are also allowed.

Then one move can reduce both:

```text
|dx|
and
|dy|
```

simultaneously.

If all 8 neighboring moves are allowed, minimum moves are:

```text
max(|dx|,|dy|)
```

This is Chebyshev distance.

Example:

```text
dx = 5
dy = 2
```

Use 2 diagonal moves:

```text
remaining:
dx = 3
dy = 0
```

then 3 horizontal moves.

Total:

```text
5 = max(5,2)
```

---

# 15.15 Movement Rules Determine the Formula

Do not memorize:

```text
grid -> Manhattan
```

Instead ask:

```text
What can one legal move change?
```

If one move changes one coordinate:

```text
|dx|+|dy|
```

If one move can change both by one:

```text
max(|dx|,|dy|)
```

If movement is unrestricted straight-line geometry:

```text
sqrt(dx^2+dy^2)
```

The allowed moves define the correct notion of distance.

---

# 15.16 Euclidean Distance

For straight-line geometric distance:

```text
d =
sqrt(
    (x2-x1)^2
    +
    (y2-y1)^2
)
```

This comes from the Pythagorean theorem.

ASCII:

```text
target *
       |\
    dy | \ distance
       |  \
       |___\
        dx
      start
```

In many CF problems, avoid square roots when only comparing distances.

Instead compare:

```text
dx^2 + dy^2
```

because square root is increasing.

---

# 15.17 Compare Squared Distances

Instead of:

```text
sqrt(A) < sqrt(B)
```

compare:

```text
A < B
```

Example:

Point distance from origin:

```text
x^2+y^2
```

If only asking:

```text
which point is closer?
```

there is often no need for floating point.

This improves simplicity and avoids precision issues.

Watch integer overflow when squaring large coordinates.

---

# 15.18 Grid Reachability and Parity

Suppose each move is exactly one orthogonal step.

Each move changes:

```text
x+y
```

by:

```text
+1 or -1
```

Therefore parity of:

```text
x+y
```

flips every move.

After exactly `k` moves:

```text
parity(final x+y)
=
parity(initial x+y + k)
```

Equivalent condition:

```text
ManhattanDistance <= k
```

and:

```text
(k - ManhattanDistance) is even
```

Again, unused extra moves can be spent in 2-step backtracking cycles.

---

# 15.19 Chessboard Coloring View

Color grid cell by:

```text
(x+y) % 2
```

Then orthogonal movement always changes color.

```text
B W B W
W B W B
B W B W
```

After:

```text
even number of moves
```

you are on the same color.

After:

```text
odd number
```

you are on the opposite color.

This turns coordinate parity into a visual invariant.

---

# 15.20 Diagonal Movement and Parity

Suppose a move changes:

```text
x += ±1
y += ±1
```

Then:

```text
x+y
```

changes by:

```text
-2, 0, or +2
```

Therefore:

```text
(x+y) % 2
```

is invariant.

So diagonal-only movement cannot move between opposite chessboard colors.

Again:

```text
legal move
   ↓
coordinate delta
   ↓
parity effect
   ↓
reachability
```

---

# 15.21 Meeting on a Number Line

Objects:

```text
A at x1
B at x2
```

Instead of tracking both absolute positions, track:

```text
gap = x2-x1
```

Suppose velocities:

```text
v1
v2
```

Then:

```text
gap(T)
=
(x2+v2*T)
-
(x1+v1*T)
```

So:

```text
gap(T)
=
(x2-x1)
+
(v2-v1)T
```

Meeting means:

```text
gap(T)=0
```

This is relative-coordinate modeling.

---

# 15.22 Relative Coordinates

Many two-object problems simplify if one object becomes the origin.

Instead of:

```text
A = (x1,y1)
B = (x2,y2)
```

represent B relative to A:

```text
(dx,dy)
=
(x2-x1, y2-y1)
```

Now A is effectively:

```text
(0,0)
```

and B is:

```text
(dx,dy)
```

You have reduced two positions to one displacement vector.

---

# 15.23 Translation Does Not Change Distance

If you add the same vector:

```text
(tx,ty)
```

to both points:

```text
A' = A + T
B' = B + T
```

then:

```text
B'-A'
=
(B+T)-(A+T)
=
B-A
```

So relative displacement is unchanged.

Therefore distances based only on coordinate differences are translation-invariant.

This justifies normalizing convenient points to `(0,0)`.

---

# 15.24 Bounding Box

Given many points, sometimes only extremes matter:

```text
minX
maxX
minY
maxY
```

They define an axis-aligned bounding box.

Width:

```text
maxX-minX
```

Height:

```text
maxY-minY
```

ASCII:

```text
(minX,maxY) +------------+ (maxX,maxY)
            |   points   |
            |  *   *     |
            |     *      |
(minX,minY) +------------+ (maxX,minY)
```

If a problem asks for the smallest axis-aligned rectangle containing all points, these four extremes completely determine it.

---

# 15.25 Extremes Instead of All Pairs

Suppose points lie on a number line.

Maximum distance between any two points is:

```text
maxX-minX
```

You do not need to test all pairs.

Why?

For any:

```text
a <= b
```

we have:

```text
minX <= a <= b <= maxX
```

therefore:

```text
b-a <= maxX-minX
```

and the extreme pair achieves the bound.

This connects coordinate modeling with Chapter 16 extremal modeling.

---

# 15.26 Midpoint Modeling

For points:

```text
a
b
```

on a number line, midpoint:

```text
(a+b)/2
```

But integer problems require care.

If:

```text
a+b
```

is odd, midpoint is not an integer.

Example:

```text
a = 2
b = 7
```

midpoint:

```text
4.5
```

If target must be an integer coordinate, you may need floor/ceil candidates:

```text
4 and 5
```

Always distinguish continuous geometry from integer lattice geometry.

---

# 15.27 Equal Distance on a Line

Find `x` such that:

```text
|x-a| = |x-b|
```

Geometrically:

```text
x
```

must be the midpoint.

Equation:

```text
x = (a+b)/2
```

If integer `x` is required:

```text
a+b
```

must be even.

A geometry story becomes parity.

---

# 15.28 Symmetry

If a problem is unchanged by reflecting the number line:

```text
x -> -x
```

you can sometimes assume:

```text
a <= b
```

or normalize one direction.

Example distance:

```text
|a-b|
```

does not care which point is left.

So instead of handling:

```text
a < b
a > b
```

separately, use absolute difference.

Symmetry reduces casework.

---

# 15.29 Coordinate Compression — Conceptual Note

Sometimes coordinates are huge:

```text
10
10^9
10^18
```

but only their relative order matters.

Then map sorted unique coordinates to:

```text
0,1,2,...
```

Example:

```text
[100, 500000, 20]
```

Sorted unique:

```text
20,100,500000
```

Compressed:

```text
20     -> 0
100    -> 1
500000 -> 2
```

Important:

```text
compression preserves ORDER,
not physical distance.
```

So do not replace:

```text
500000-100
```

with:

```text
2-1
```

when actual distance matters.

---

# 15.30 Transforming Manhattan Expressions

Sometimes expressions contain:

```text
|x1-x2| + |y1-y2|
```

For more advanced problems, absolute values can be transformed using sign choices.

A useful identity:

```text
|a|+|b|
=
max(
 a+b,
 a-b,
-a+b,
-a-b
)
```

Why?

Choose signs matching the signs of `a` and `b`.

This can turn a distance maximum into extrema of transformed coordinates such as:

```text
x+y
x-y
```

This idea appears in harder CF coordinate problems.

---

# 15.31 Example — Manhattan Maximum

For two points:

```text
P1=(x1,y1)
P2=(x2,y2)
```

Manhattan distance:

```text
|x1-x2| + |y1-y2|
```

Let:

```text
a = x1-x2
b = y1-y2
```

Then:

```text
|a|+|b|
=
max(±a ±b)
```

This suggests considering transformed values:

```text
x+y
x-y
```

and their extrema.

You do not need this transformation for basic Manhattan movement, but it is valuable around the upper end of the target rating range.

---

# 15.32 Complete CF-Style Example 1 — Minimum Grid Moves

Start:

```text
(3,5)
```

Target:

```text
(-2,8)
```

Orthogonal moves.

```text
dx = -5
dy = +3
```

Minimum:

```text
|dx|+|dy|
=
5+3
=
8
```

---

# 15.33 Complete CF-Style Example 2 — Exact k Steps

Start:

```text
(0,0)
```

Target:

```text
(2,3)
```

Minimum Manhattan distance:

```text
5
```

Can reach in exactly:

```text
k = 7
```

because:

```text
7 >= 5
7-5 = 2
```

extra moves can be wasted as one back-and-forth pair.

Can reach in exactly:

```text
k = 6?
```

No:

```text
6-5 = 1
```

wrong parity.

---

# 15.34 Complete CF-Style Example 3 — Diagonal Only

Start:

```text
(0,0)
```

Target:

```text
(3,2)
```

Each move:

```text
(±1,±1)
```

Start color:

```text
(0+0)%2 = 0
```

Target:

```text
(3+2)%2 = 1
```

Different parity.

Impossible.

The obstruction appears before any path simulation.

---

# 15.35 Complete CF-Style Example 4 — Maximum Spread

Positions:

```text
[7,2,11,4,9]
```

Minimum:

```text
2
```

Maximum:

```text
11
```

Maximum distance:

```text
11-2 = 9
```

No pair enumeration needed.

---

# 15.36 Complete CF-Style Example 5 — Normalize Coordinates

Two points:

```text
A=(1000003,999999)
B=(1000010,1000004)
```

Subtract A:

```text
A'=(0,0)
B'=(7,5)
```

Orthogonal distance:

```text
7+5 = 12
```

Large absolute coordinates were irrelevant.

Only differences mattered.

---

# 15.37 Modeling Checklist

When a problem contains movement or coordinates:

```text
1. DRAW A NUMBER LINE / GRID.

2. DEFINE START AND TARGET.

3. COMPUTE:
   dx = targetX-startX
   dy = targetY-startY

4. CAN I MOVE START TO ORIGIN?

5. WHAT DOES ONE LEGAL MOVE CHANGE?

6. DOES ONE MOVE AFFECT:
   one coordinate?
   both coordinates?
   arbitrary direction?

7. WHAT IS THE MINIMUM NECESSARY PROGRESS?

8. CAN THAT LOWER BOUND BE ACHIEVED?

9. EXACTLY k OR AT MOST k MOVES?

10. CHECK PARITY / DIVISIBILITY.

11. CAN I TRACK RELATIVE POSITION
    INSTEAD OF TWO OBJECTS?

12. DO ONLY EXTREME COORDINATES MATTER?

13. ARE COORDINATES INTEGER OR CONTINUOUS?

14. CAN I AVOID FLOATING POINT
    USING SQUARED DISTANCES?
```

---

# 15.38 Common Mistakes

## Mistake 1 — Using distance when direction matters

Use:

```text
target-start
```

for signed displacement.

Use:

```text
|target-start|
```

for magnitude.

---

## Mistake 2 — Automatically using Manhattan distance

First inspect legal moves.

Diagonal moves change the formula.

---

## Mistake 3 — Forgetting exact-step parity

Minimum distance being <= `k` may not be enough when exactly `k` moves are required.

---

## Mistake 4 — Tracking absolute positions unnecessarily

If only relative position matters, subtract one object/point from everything.

---

## Mistake 5 — Using sqrt for comparisons

Compare squared distances when possible.

---

## Mistake 6 — Coordinate compression destroying distance

Compression preserves ordering, not original gaps.

---

# 15.39 Translation Drills

Do not code.

### Drill 1

```text
start = 10
target = 3
```

Displacement:

```text
-7
```

Distance:

```text
7
```

### Drill 2

Orthogonal grid movement:

```text
dx = -4
dy = 6
```

Minimum moves:

```text
10
```

### Drill 3

8-direction king-like movement:

```text
dx = 4
dy = 6
```

Minimum:

```text
6
```

### Drill 4

Exactly `k` orthogonal unit moves:

```text
D = |dx|+|dy|
```

Need:

```text
D <= k
```

and:

```text
(k-D)%2 == 0
```

### Drill 5

Two moving objects:

```text
track B-A
```

rather than both absolute positions.

---

# 15.40 Practice Set

For each problem write:

```text
COORDINATE SYSTEM:
START:
TARGET:
DISPLACEMENT:
LEGAL MOVE DELTA:
LOWER BOUND:
PARITY/DIVISIBILITY:
CAN NORMALIZE?:
FINAL CONDITION:
```

### Practice A

Move on a line using `+1/-1`. Can you reach `y` from `x` in exactly `k` moves?

### Practice B

Move on a grid only U/D/L/R. Find minimum moves.

### Practice C

Move to any of 8 neighboring grid cells. Find minimum moves.

### Practice D

Find maximum distance among points on a number line.

### Practice E

A point can move only diagonally `(±1,±1)`. Give a quick necessary reachability condition.

---

# 15.41 Practice Answers

## A

Let:

```text
D = |y-x|
```

Reachable exactly in `k` if:

```text
D <= k
```

and:

```text
(k-D)%2 == 0
```

## B

```text
|dx|+|dy|
```

## C

```text
max(|dx|,|dy|)
```

## D

```text
max_position - min_position
```

## E

Since every move preserves parity of:

```text
x+y
```

start and target must satisfy:

```text
(startX+startY)%2
=
(targetX+targetY)%2
```

Additional conditions may depend on the exact problem.

---

# 15.42 Chapter Mastery Test

You are ready for the next chapter when a movement story makes you immediately write:

```text
dx = targetX-startX
dy = targetY-startY
```

Then ask:

```text
What can ONE move do to dx and dy?
```

You should recognize:

```text
line distance
-> |dx|

orthogonal grid
-> |dx|+|dy|

8-direction grid
-> max(|dx|,|dy|)

exact moves
-> minimum distance + parity check
```

Most importantly, you should derive these from the move rules rather than memorizing them blindly.

---

# 15.43 Final Mental Engine

```text
                MOVEMENT STORY
                      │
                      ▼
               CHOOSE COORDINATES
                      │
                      ▼
               NORMALIZE IF USEFUL
                      │
                      ▼
                COMPUTE dx, dy
                      │
                      ▼
              ANALYZE ONE LEGAL MOVE
                      │
          ┌───────────┼───────────┐
          │           │           │
      one axis     both axes    arbitrary
          │           │           │
          ▼           ▼           ▼
      Manhattan    max-type    Euclidean/
       behavior    behavior    other metric
          │           │           │
          └───────────┼───────────┘
                      ▼
               LOWER BOUND
                      │
                      ▼
              IS IT ACHIEVABLE?
                      │
                      ▼
         parity / divisibility / bounds
                      │
                      ▼
          distance / reachability /
             minimum number moves
```

The core habit:

```text
Do not memorize a distance formula
before understanding the legal moves.

The MOVE SET creates the geometry.
```

---

# Next Chapter

```text
15. COORDINATE & DISTANCE MODELING
                 ↓
16. MIN/MAX & EXTREMAL MODELING
```

Chapter 16 will focus on recognizing when an entire problem is controlled by a few extreme values:

```text
minimum
maximum
range = max-min
smallest/largest candidates
worst-case constraint
bottleneck
extremal arguments
top two / bottom two
```
