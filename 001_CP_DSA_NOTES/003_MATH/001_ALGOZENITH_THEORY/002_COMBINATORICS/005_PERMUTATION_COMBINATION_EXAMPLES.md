# Permutations & Combinations — Visual Math Modelling Examples

> **Goal:** Do not start with a formula. Convert the story into **choices**, identify what uniquely defines one answer, then count.

## 1. Core Math-Modelling Habit

```text
STORY / OBJECT
      ↓
What uniquely defines one answer?
      ↓
Convert it into choices
      ↓
Does order matter?
      ↓
Choose / multiply / sum
      ↓
Formula
```

Key question:

```text
"What minimum information uniquely determines
 one object I am counting?"
```

Examples:

```text
intersection of 2 lines → choose the 2 lines
subarray                → choose 2 boundaries
rectangle               → choose 2 horizontal + 2 vertical lines
square                  → choose size + position
```

---

# 2. Maximum Intersections — 8 Lines & 4 Circles

Assume general position so all allowable intersection points are distinct.

## A. Line ↔ Line

One intersection is determined by:

```text
choosing 2 lines
```

Order does not matter, so:

```text
8C2 = (8 × 7) / 2 = 28
```

```text
Line A  ─────────╲
                  ╲
                   X  ← one intersection
                  ╱
Line B  ─────────╱

choose 2 lines
      ↓
     8C2
      ↓
      28
```

## B. Circle ↔ Circle

Choose a pair of circles:

```text
4C2 = 6
```

Each pair can intersect at most twice:

```text
       ___     ___
     /     \ X /   \
    |       \ /     |
    |       / \     |
     \ ___ / X \___/

       up to 2 points
```

Therefore:

```text
4C2 × 2
= 6 × 2
= 12
```

## C. Line ↔ Circle

Choose:

```text
1 line AND 1 circle
```

Ways:

```text
8 × 4
```

Each pair can intersect at most twice:

```text
        _______
     .-'       '-.
----X-------------X----
     '-._______.-'

       2 points
```

Therefore:

```text
8 × 4 × 2 = 64
```

## D. Combine

```text
                 INTERSECTIONS
                       │
          ┌────────────┼────────────┐
          ↓            ↓            ↓
      line-line   circle-circle  line-circle
          │            │            │
         8C2          4C2×2        8×4×2
          │            │            │
         28           12           64
          └────────────┼────────────┘
                       ↓
                  28+12+64
                       ↓
                      104
```

### Recognition

```text
Every PAIR creates something
        ↓
       nC2

Every pair creates K outcomes
        ↓
      nC2 × K
```

---

# 3. Total Subarrays of an Array

A subarray is a **contiguous** segment.

Instead of choosing elements, choose its boundaries.

For `N = 4`:

```text
    a1      a2      a3      a4

|-------|-------|-------|-------|
0       1       2       3       4
↑                               ↑
        N+1 boundaries
```

One subarray is uniquely determined by:

```text
LEFT boundary + RIGHT boundary
```

There are `N+1` boundaries, so choose `2`:

```text
Total = C(N+1,2)
```

Algebra:

```text
C(N+1,2)
=
(N+1)N / 2
```

Therefore:

```text
Total subarrays = N(N+1)/2
```

## Example — N = 4

```text
C(5,2) = 10
```

Another view:

```text
length 1 → 4
length 2 → 3
length 3 → 2
length 4 → 1

4 + 3 + 2 + 1 = 10
```

So:

```text
1 + 2 + ... + N
=
N(N+1)/2
=
C(N+1,2)
```

### Recognition

```text
CONTIGUOUS segment
       ↓
choose two boundaries
       ↓
N+1 boundaries
       ↓
C(N+1,2)
```

---

# 4. Rectangles in an N × N Grid

An `N × N` board of cells has:

```text
N+1 horizontal lines
N+1 vertical lines
```

Example `3 × 3`:

```text
+---+---+---+
|   |   |   |
+---+---+---+
|   |   |   |
+---+---+---+
|   |   |   |
+---+---+---+

4 horizontal
4 vertical
```

One rectangle needs:

```text
2 horizontal boundaries
AND
2 vertical boundaries
```

Therefore:

```text
Rectangles
=
C(N+1,2) × C(N+1,2)
=
[C(N+1,2)]²
```

Since:

```text
C(N+1,2) = N(N+1)/2
```

we get:

```text
Rectangles = [N(N+1)/2]²
```

## Example — N = 3

```text
horizontal pairs = 4C2 = 6
vertical pairs   = 4C2 = 6

Total = 6 × 6 = 36
```

### Model

```text
choose 2 horizontal lines
          ↓
       C(N+1,2)
          │
         AND
          │
choose 2 vertical lines
          ↓
       C(N+1,2)
          │
          ↓
 [C(N+1,2)]²
```

General rectangular grid:

```text
R × C cells

rectangles
=
C(R+1,2) × C(C+1,2)
```

---

# 5. Squares in an N × N Grid

A square has the extra constraint:

```text
height = width
```

So counting arbitrary rectangles is not enough.

## A. Fix the Size

Possible square sizes:

```text
1×1, 2×2, 3×3, ..., N×N
```

Fix a `k × k` square.

Its top-left corner has:

```text
N-k+1 horizontal positions
N-k+1 vertical positions
```

Therefore:

```text
number of k×k squares
=
(N-k+1)²
```

Example `N=4`, `k=2`:

```text
●---●---●---+
|   |   |   |
●---●---●---+
|   |   |   |
●---●---●---+
|   |   |   |
+---+---+---+

3 positions horizontally
3 positions vertically

3 × 3 = 9
```

And:

```text
(4-2+1)² = 3² = 9
```

## B. Sum All Sizes

```text
Total
=
Σ (N-k+1)²
```

for `k = 1...N`.

Expand:

```text
N² + (N-1)² + ... + 1²
```

Reorder:

```text
1² + 2² + ... + N²
```

Use:

```text
1² + 2² + ... + N²
=
N(N+1)(2N+1) / 6
```

Therefore:

```text
Total squares
=
N(N+1)(2N+1) / 6
```

## Example — N = 3

```text
1×1 → 3² = 9
2×2 → 2² = 4
3×3 → 1² = 1

Total = 9 + 4 + 1 = 14
```

### Recognition

```text
different possible sizes
        ↓
fix size k
        ↓
count positions for k
        ↓
(N-k+1)²
        ↓
sum over all k
```

This gives a major CP modelling pattern:

```text
FIX A PARAMETER
      ↓
COUNT FOR IT
      ↓
SUM OVER ALL VALID VALUES
```

---

# 6. Pattern Comparison

| Problem | What uniquely defines one object? | Model |
|---|---|---|
| Line-line intersection | 2 lines | `C(L,2)` |
| Circle-circle intersections | 2 circles + up to 2 points | `C(C,2) × 2` |
| Line-circle intersections | 1 line + 1 circle | `L × C × 2` |
| Subarray | 2 boundaries | `C(N+1,2)` |
| Rectangle | 2 horizontal + 2 vertical lines | `C(R+1,2) × C(C+1,2)` |
| Square | size `k` + position | `Σ(N-k+1)²` |

The deeper habit:

```text
DON'T ASK:
"Which formula is this?"

ASK:
"What choices uniquely create one object?"
```

---

# 7. Final Recognition Card

```text
PAIR OF OBJECTS
      ↓
     nC2
```

```text
CONTIGUOUS SUBARRAY
      ↓
choose 2 of N+1 boundaries
      ↓
C(N+1,2)
      ↓
N(N+1)/2
```

```text
RECTANGLE
      ↓
2 horizontal boundaries
AND
2 vertical boundaries
      ↓
C(R+1,2) × C(C+1,2)
```

```text
SQUARE
      ↓
height = width
      ↓
fix size k
      ↓
count positions
      ↓
(N-k+1)²
      ↓
sum over k
```

## Contest Math-Modelling Checklist

```text
1. Remove story nouns.
2. Ask what uniquely determines one answer.
3. Convert it into choices.
4. Decide whether order matters.
5. AND → multiply.
6. Separate alternatives → add.
7. If a size/value varies:
      fix it → count it → sum it.
8. Simplify algebra only after the model is correct.
```

> **Core habit:** **Object → defining choices → count choices → combine.** The formula comes after the model.
