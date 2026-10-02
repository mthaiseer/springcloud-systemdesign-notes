# Permutations & Combinations — Visual Math Modelling Examples

> **Goal:** Learn how to **discover the counting model** instead of memorizing formulas.

---

# 1. The Modelling Method

For every problem, use:

```text
1. What are we counting?
        ↓
2. Draw a tiny example
        ↓
3. What choices create ONE answer?
        ↓
4. Why nCr / × / + / Σ?
        ↓
5. Derive the formula
        ↓
6. Dry run
        ↓
7. Recognition pattern
```

The important question is not:

```text
Which formula should I use?
```

Instead ask:

```text
What choices create ONE object?
```

---

# 2. Maximum Intersections — 8 Lines & 4 Circles

## What are we counting?

```text
distinct intersection points
```

There are three types:

```text
line ↔ line
circle ↔ circle
line ↔ circle
```

For the maximum, assume all allowable intersection points are distinct.

---

## A. Line ↔ Line

### Tiny picture

```text
Line 1  ───────╲
                ╲
                 X
                ╱
Line 2  ───────╱
```

Two lines create at most:

```text
1 intersection
```

So to create one line-line intersection we choose:

```text
2 lines
```

From `8` lines:

```text
C(8,2)
= 8×7 / 2
= 28
```

Why combination?

```text
Line 1 + Line 2
=
Line 2 + Line 1
```

Order does not matter.

---

## B. Circle ↔ Circle

Two circles can intersect at most twice:

```text
       ___     ___
     /     \ X /   \
    |       \ /     |
    |       / \     |
     \ ___ / X \___/

         2 points
```

First choose the circle pair:

```text
C(4,2) = 6
```

Each pair contributes at most `2` points:

```text
6 × 2 = 12
```

So:

```text
circle-circle intersections
=
C(4,2) × 2
=
12
```

---

## C. Line ↔ Circle

Choose:

```text
1 line from 8
AND
1 circle from 4
```

Number of pairs:

```text
8 × 4
```

Each pair can intersect twice:

```text
        _______
     .-'       '-.
----X-------------X----
     '-._______.-'
```

Therefore:

```text
8 × 4 × 2
= 64
```

---

## D. Final Count

```text
line-line      = 28
circle-circle  = 12
line-circle    = 64
```

These are separate cases:

```text
28 + 12 + 64
= 104
```

### Recognition

```text
Every PAIR creates something
        ↓
       C(N,2)

Every pair creates K outcomes
        ↓
      C(N,2) × K
```

---

# 3. Total Subarrays of an Array

## What are we counting?

```text
all contiguous segments
```

For:

```text
[a1, a2, a3, a4]
```

we have `N = 4`.

---

## Step 1 — Look at Boundaries

Draw boundaries around the elements:

```text
      a1      a2      a3      a4

   |-------|-------|-------|-------|
   0       1       2       3       4
```

There are:

```text
4 elements
5 boundaries

N elements
N+1 boundaries
```

---

## Step 2 — How Does One Subarray Form?

Take:

```text
[a2, a3]
```

Picture:

```text
      a1     [a2      a3]     a4

   |-------|-------|-------|-------|
   0       1       2       3       4
           ↑               ↑
        left             right
```

The subarray is completely determined by:

```text
LEFT boundary
AND
RIGHT boundary
```

So one subarray corresponds to:

```text
choosing 2 boundaries
```

---

## Step 3 — Count

Number of boundaries:

```text
N + 1
```

Choose any `2`:

```text
Total
=
C(N+1,2)
```

Expand:

```text
C(N+1,2)
=
(N+1)N / 2
```

Therefore:

```text
Total subarrays
=
N(N+1)/2
```

---

## Dry Run — N = 4

By length:

```text
length 1 → 4 subarrays
length 2 → 3 subarrays
length 3 → 2 subarrays
length 4 → 1 subarray
```

Total:

```text
4 + 3 + 2 + 1
= 10
```

Formula:

```text
C(5,2)
=
5×4/2
=
10
```

Same answer.

### Recognition

```text
CONTIGUOUS segment
       ↓
start + end
       ↓
think boundaries
       ↓
choose 2 from N+1
       ↓
C(N+1,2)
```

---

# 4. Rectangles in an N × N Grid

## What are we counting?

```text
all rectangles
```

Take a `3 × 3` grid:

```text
+---+---+---+
|   |   |   |
+---+---+---+
|   |   |   |
+---+---+---+
|   |   |   |
+---+---+---+
```

Although there are `3 × 3` cells, there are:

```text
4 horizontal boundary lines
4 vertical boundary lines
```

In general:

```text
N × N cells
      ↓
N+1 horizontal lines
N+1 vertical lines
```

---

## Step 1 — What Creates One Rectangle?

Example:

```text
+---+---+---+
|   |   |   |
+===+===+---+  ← top
|   |   |   |
+===+===+---+  ← bottom
|   |   |   |
+---+---+---+
↑       ↑
left   right
```

A rectangle needs:

```text
top + bottom
=
2 horizontal lines
```

and:

```text
left + right
=
2 vertical lines
```

---

## Step 2 — Count Choices

Horizontal:

```text
choose 2 from N+1

→ C(N+1,2)
```

Vertical:

```text
choose 2 from N+1

→ C(N+1,2)
```

We need both:

```text
horizontal choice
AND
vertical choice
```

Therefore multiply:

```text
Rectangles
=
C(N+1,2) × C(N+1,2)
```

So:

```text
Rectangles
=
[C(N+1,2)]²
```

---

## Dry Run — N = 3

```text
horizontal choices
= C(4,2)
= 6

vertical choices
= C(4,2)
= 6
```

Therefore:

```text
6 × 6
= 36
```

### General R × C Grid

For:

```text
R rows × C columns
```

there are:

```text
R+1 horizontal lines
C+1 vertical lines
```

Therefore:

```text
rectangles
=
C(R+1,2) × C(C+1,2)
```

### Recognition

```text
RECTANGLE
    ↓
needs 4 boundaries
    ↓
2 horizontal AND 2 vertical
    ↓
C(R+1,2) × C(C+1,2)
```

---

# 5. Squares in an N × N Grid

## What changes from rectangles?

A rectangle can have:

```text
height ≠ width
```

But a square requires:

```text
height = width
```

So choosing arbitrary horizontal and vertical boundary pairs would also count non-squares.

Instead:

```text
fix the square size
```

---

## Step 1 — Fix Size k

Suppose:

```text
N = 4
k = 2
```

We want a:

```text
2 × 2 square
```

First look only horizontally:

```text
4 cells:

[1][2][3][4]
```

A width-2 square can start at:

```text
start 1 → [1][2]       ✓
start 2 →    [2][3]    ✓
start 3 →       [3][4] ✓
start 4 → impossible   ✗
```

So:

```text
3 horizontal starts
```

Why `3`?

```text
N - k + 1

= 4 - 2 + 1
= 3
```

This is the key derivation.

---

## Step 2 — Move to 2D

A `2 × 2` square has:

```text
3 horizontal starting positions
AND
3 vertical starting positions
```

Therefore:

```text
3 × 3
= 9
```

Generalizing:

```text
horizontal starts = N-k+1
vertical starts   = N-k+1
```

So:

```text
k×k squares
=
(N-k+1) × (N-k+1)

=
(N-k+1)²
```

---

## Step 3 — Count Every Possible Size

Possible sizes:

```text
k = 1, 2, 3, ..., N
```

For each size:

```text
k=1 → N²

k=2 → (N-1)²

k=3 → (N-2)²

...

k=N → 1²
```

Therefore:

```text
Total
=
N² + (N-1)² + ... + 1²
```

Same as:

```text
1² + 2² + ... + N²
```

Using the sum-of-squares formula:

```text
Total squares
=
N(N+1)(2N+1) / 6
```

---

## Dry Run — N = 3

```text
3 × 3 board
```

Count by size:

```text
1×1:
3 horizontal starts
3 vertical starts

3×3 = 9
```

```text
2×2:
2 horizontal starts
2 vertical starts

2×2 = 4
```

```text
3×3:
1 horizontal start
1 vertical start

1×1 = 1
```

Total:

```text
9 + 4 + 1
= 14
```

### Recognition

```text
Object has different sizes
        ↓
fix one size k
        ↓
derive valid starting positions
        ↓
count for this k
        ↓
sum over every k
```

This is a major CP pattern:

```text
FIX
 ↓
COUNT
 ↓
SUM
```

---

# 6. Pattern Comparison

| Problem | First Observation | Choices | Formula |
|---|---|---|---|
| Line-line intersection | 2 lines create one point | choose 2 lines | `C(L,2)` |
| Circle-circle | 2 circles can create 2 points | choose pair × 2 | `C(C,2)×2` |
| Line-circle | need one of each | line × circle × 2 | `L×C×2` |
| Subarray | segment has 2 boundaries | choose 2 of `N+1` | `C(N+1,2)` |
| Rectangle | needs 4 boundary lines | 2 horizontal × 2 vertical | `C(R+1,2)C(C+1,2)` |
| Square | equal height/width | fix `k`, count starts | `Σ(N-k+1)²` |

---

# 7. Final Math-Modelling Card

Do not begin with:

```text
Which formula is this?
```

Use:

```text
WHAT AM I COUNTING?
        ↓
DRAW SMALL EXAMPLE
        ↓
WHAT CREATES ONE ANSWER?
        ↓
WHAT ARE MY CHOICES?
        ↓
ORDER MATTERS?
        ↓
COUNT EACH CHOICE
        ↓
AND → multiply
OR  → add
varying k → sum
```

### Four Patterns from This Note

```text
PAIR
→ choose 2
→ C(N,2)
```

```text
CONTIGUOUS SEGMENT
→ choose boundaries
→ C(N+1,2)
```

```text
RECTANGLE
→ choose horizontal boundaries
AND vertical boundaries
→ multiply
```

```text
VARIABLE SIZE
→ fix k
→ count for k
→ sum over k
```

> **Contest habit:** **Draw → identify choices → derive → formula.** Do not try to recall the formula before understanding what is being counted.
