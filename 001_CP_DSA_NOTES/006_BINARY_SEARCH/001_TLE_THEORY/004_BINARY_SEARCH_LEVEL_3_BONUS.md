# TLE Eliminators — Binary Search Level 3 [Bonus]
## Problem Solving — Ropes + Jumping Through Segment
### TLE-Style Self-Study Notes

> **Source:** supplied TLE Eliminators lecture **Problem Solving [Bonus] | Binary Search | Level 3**.
>
> The lecture first revises **binary search on decimals / precision**, then applies binary search to:
>
> ```text
> Problem 1 — Ropes
> Problem 2 — Jumping Through Segment
> ```
>
> **Goal of this note**
>
> ```text
> prerequisites
> → tiny dry run
> → problem asks
> → expected result
> → mathematical model
> → monotonic predicate
> → checker
> → binary search
> → detailed dry run
> → C++17
> → recognition model
> ```
>
> Code and compact math blocks follow the same formatting style as the previous `001_` and `002_` notes.

---

# Clickable Table of Contents

- [0. Class Map](#0-class-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 Binary Search on Answer](#11-binary-search-on-answer)
  - [1.2 First True vs Last True](#12-first-true-vs-last-true)
  - [1.3 Integer vs Decimal Search Space](#13-integer-vs-decimal-search-space)
  - [1.4 Precision and Search-Space Size](#14-precision-and-search-space-size)
  - [1.5 Floor Division — How Many Pieces?](#15-floor-division--how-many-pieces)
  - [1.6 Monotonic Piece Count](#16-monotonic-piece-count)
  - [1.7 Expanding a Reachable Interval](#17-expanding-a-reachable-interval)
  - [1.8 Intersection of Intervals](#18-intersection-of-intervals)
  - [1.9 Carrying a Reachable Interval Forward](#19-carrying-a-reachable-interval-forward)
  - [1.10 Recognition Checklist](#110-recognition-checklist)
- [2. Problem 1 — Ropes](#2-problem-1--ropes)
  - [2.1 What It Asks](#21-what-it-asks)
  - [2.2 What Result Is Expected](#22-what-result-is-expected)
  - [2.3 Small Dry Run First](#23-small-dry-run-first)
  - [2.4 Choose the Binary-Search Answer](#24-choose-the-binary-search-answer)
  - [2.5 Build the Predicate](#25-build-the-predicate)
  - [2.6 Why `floor(length / x)`](#26-why-floorlength--x)
  - [2.7 Monotonicity](#27-monotonicity)
  - [2.8 Search Space](#28-search-space)
  - [2.9 Detailed Dry Run](#29-detailed-dry-run)
  - [2.10 Binary Search Dry Run](#210-binary-search-dry-run)
  - [2.11 Algorithm](#211-algorithm)
  - [2.12 C++17](#212-c17)
  - [2.13 Complexity](#213-complexity)
  - [2.14 Recognition Model](#214-recognition-model)
- [3. Problem 2 — Jumping Through Segment](#3-problem-2--jumping-through-segment)
  - [3.1 What It Asks](#31-what-it-asks)
  - [3.2 What Result Is Expected](#32-what-result-is-expected)
  - [3.3 Small Dry Run First](#33-small-dry-run-first)
  - [3.4 Choose the Binary-Search Answer](#34-choose-the-binary-search-answer)
  - [3.5 What Positions Are Reachable After One Jump?](#35-what-positions-are-reachable-after-one-jump)
  - [3.6 Intersect With the Next Segment](#36-intersect-with-the-next-segment)
  - [3.7 Build `canJump(x)`](#37-build-canjumpx)
  - [3.8 Why the Predicate Is Monotonic](#38-why-the-predicate-is-monotonic)
  - [3.9 Detailed Dry Run — x = 1](#39-detailed-dry-run--x--1)
  - [3.10 Detailed Dry Run — x = 2](#310-detailed-dry-run--x--2)
  - [3.11 Binary Search Dry Run](#311-binary-search-dry-run)
  - [3.12 Algorithm](#312-algorithm)
  - [3.13 C++17](#313-c17)
  - [3.14 Complexity](#314-complexity)
  - [3.15 Recognition Model](#315-recognition-model)
- [4. Ropes vs Jumping Through Segment](#4-ropes-vs-jumping-through-segment)
- [5. Common Mistakes](#5-common-mistakes)
- [6. Final Mental Model](#6-final-mental-model)

---

# 0. Class Map

```text
BINARY SEARCH LEVEL 3
│
├── Real / decimal answer
│   │
│   └── ROPES
│       ├── candidate = piece length x
│       ├── count pieces
│       ├── can(x) = pieces >= k
│       └── T T T T F F F
│                   ^
│                   |
│               last True
│
└── Integer answer
    │
    └── JUMPING THROUGH SEGMENT
        ├── candidate = max jump x
        ├── maintain reachable interval
        ├── can(x) = reach every segment
        └── F F F F T T T
                    ^
                    |
                first True
```

The two problems look different, but both follow:

```text
optimization
    |
    v
guess answer x
    |
    v
write checker(x)
    |
    v
prove monotonicity
    |
    v
binary search boundary
```

---

# 1. Prerequisites

## 1.1 Binary Search on Answer

Binary search is not only for searching a value in a sorted array.

It can also search:

```text
possible answers
```

Suppose the problem asks:

```text
maximum possible X
```

or:

```text
minimum possible X
```

Instead of directly constructing the optimum, ask:

```text
"If the answer were X,
would it be possible?"
```

That creates:

```text
can(X)
```

### Tiny Dry Run

Suppose:

```text
X:      1 2 3 4 5 6 7
can(X): T T T T F F F
```

If the problem asks:

```text
maximum feasible X
```

expected result:

```text
4
```

Binary search finds the boundary.

---

## 1.2 First True vs Last True

Two important monotonic forms:

### Last True

```text
T T T T T F F F
        ^
        |
     last True
```

Use when:

```text
maximize X
```

Example:

```text
maximum rope-piece length
```

---

### First True

```text
F F F F T T T T
        ^
        |
     first True
```

Use when:

```text
minimize X
```

Example:

```text
minimum allowed jump length
```

### Tiny Dry Run

```text
can(1) = False
can(2) = False
can(3) = True
can(4) = True
```

If minimizing:

```text
answer = 3
```

---

## 1.3 Integer vs Decimal Search Space

Some answers are integers:

```text
1, 2, 3, 4, ...
```

Then normal binary search can use:

```cpp
low = mid + 1;
high = mid - 1;
```

Some answers are real numbers:

```text
4.5
5.01
5.000001
...
```

Then there is no unique:

```text
next real value
```

### Tiny Dry Run

Suppose the true answer is:

```text
5.0
```

and current midpoint is:

```text
4.75
```

For a decimal search, jumping to:

```text
4.75 + 1 = 5.75
```

would skip the boundary.

So decimal binary search narrows a real interval instead of moving by one integer.

---

## 1.4 Precision and Search-Space Size

The lecture revises the idea:

```text
real search range
+
required decimal precision
```

can be thought of as a finite number of precision-sized slots.

Suppose:

```text
search range = [0, 10^7]
precision = 10^-6
```

Approximate number of distinguishable slots:

```text
10^7 / 10^-6 = 10^13
```

Binary search needs only:

```text
O(log2(10^13))
```

iterations.

Since:

```text
2^43 ≈ 8.8 × 10^12
2^44 ≈ 1.76 × 10^13
```

roughly:

```text
44 halvings
```

already give that scale of precision.

### Tiny Dry Run

Start width:

```text
8
```

After three halvings:

```text
8 → 4 → 2 → 1
```

After `t` halvings:

```text
remaining width = initialWidth / 2^t
```

That is why decimal binary search is still fast.

---

## 1.5 Floor Division — How Many Pieces?

If a rope has length:

```text
L
```

and every required piece has length:

```text
x
```

the number of **complete** pieces is:

```text
floor(L / x)
```

We cannot count a partial leftover as another full piece.

### Tiny Dry Run

```text
rope length = 10
piece length = 3
```

Cut:

```text
[3][3][3] + leftover 1
```

Pieces:

```text
floor(10 / 3) = 3
```

Not:

```text
3.333...
```

because pieces must be complete.

---

## 1.6 Monotonic Piece Count

If we reduce the requested piece length:

```text
x
```

we can cut the same number or more pieces.

Example using one rope:

```text
rope = 10
```

```text
x = 5 → floor(10/5) = 2
x = 4 → floor(10/4) = 2
x = 3 → floor(10/3) = 3
x = 2 → floor(10/2) = 5
```

So as `x` becomes smaller:

```text
piece count never decreases
```

This creates a monotonic predicate.

### Shape

For:

```text
can(x) = can cut at least k pieces?
```

we get:

```text
small x                            large x
  |                                  |
  v                                  v

T T T T T T F F F F
          ^
          |
      maximum feasible x
```

---

## 1.7 Expanding a Reachable Interval

Suppose your current position may be anywhere in:

```text
[L,R]
```

and your next jump can have absolute distance at most:

```text
x
```

From the leftmost point `L`, you can go as far left as:

```text
L - x
```

From the rightmost point `R`, you can go as far right as:

```text
R + x
```

So before applying the next segment restriction:

```text
new reachable range = [L-x, R+x]
```

### Tiny Dry Run

Current reachable positions:

```text
[3,5]
```

Allowed jump:

```text
x = 2
```

Expanded reach:

```text
[3-2, 5+2] = [1,7]
```

ASCII:

```text
current:
        [3---------5]

after jump <= 2:
    [1-----------------7]
```

---

## 1.8 Intersection of Intervals

Suppose we can reach:

```text
[A,B]
```

but the next required segment is:

```text
[L,R]
```

We must land in both.

So valid positions are:

```text
[A,B] ∩ [L,R]
```

Intersection:

```text
newLeft = max(A,L)
newRight = min(B,R)
```

Valid iff:

```text
newLeft <= newRight
```

### Dry Run

Reachable:

```text
[1,7]
```

Next segment:

```text
[3,6]
```

Intersection:

```text
[max(1,3), min(7,6)] = [3,6]
```

ASCII:

```text
Reachable: [1----------------7]
Segment:       [3--------6]

Result:        [3--------6]
```

### No Intersection

```text
Reachable = [0,2]
Segment   = [3,6]
```

```text
max(0,3) = 3
min(2,6) = 2
```

Since:

```text
3 > 2
```

the intersection is empty.

---

## 1.9 Carrying a Reachable Interval Forward

For a sequence of segments, do not keep every possible position separately.

Keep only:

```text
[L,R]
```

representing:

```text
all positions that are still reachable
after processing the current prefix
```

For the next segment:

```text
[segmentL, segmentR]
```

Step 1 — expand by maximum jump `x`:

```text
[L-x, R+x]
```

Step 2 — intersect with next segment:

```text
L = max(L-x, segmentL)
R = min(R+x, segmentR)
```

If:

```text
L > R
```

then that `x` fails.

### Tiny Dry Run

Start:

```text
[0,0]
```

Allowed jump:

```text
x = 2
```

First segment:

```text
[1,2]
```

Expand:

```text
[-2,2]
```

Intersect:

```text
[-2,2] ∩ [1,2] = [1,2]
```

This compact interval contains **all valid places** we could occupy after the first segment.

---

## 1.10 Recognition Checklist

Before solving either problem, ask:

```text
1. What numeric answer am I optimizing?

2. Can I guess candidate x?

3. Can I check x in O(N)?

4. Is checker(x) monotonic?

5. Am I maximizing?
   → last True

6. Am I minimizing?
   → first True

7. Is x integer or decimal?

8. Does checker use:
   floor/counting?
   interval expansion?
   intersection?
```

---

# 2. Problem 1 — Ropes

## 2.1 What It Asks

We have:

```text
N ropes
```

with lengths:

```text
a[0], a[1], ..., a[n-1]
```

We want to cut at least:

```text
K
```

pieces.

Every produced piece must have the **same length**:

```text
x
```

Find the:

```text
maximum possible x
```

such that at least `K` complete pieces can be cut.

The lecture illustrates rope lengths such as:

```text
8, 4, 6, 10
```

with:

```text
K = 4
```

---

## 2.2 What Result Is Expected

Input idea:

```text
ropes = [8,4,6,10]
K = 4
```

We want:

```text
largest equal piece length
```

For:

```text
x = 5
```

pieces:

```text
8  → 1 piece
4  → 0 pieces
6  → 1 piece
10 → 2 pieces
```

Total:

```text
1 + 0 + 1 + 2 = 4
```

So:

```text
x = 5
```

works.

If:

```text
x > 5
```

slightly, the rope of length `10` gives only one complete piece instead of two.

Then the total falls below `4`.

So the expected boundary for this example is:

```text
5
```

---

## 2.3 Small Dry Run First

Use:

```text
ropes = [8,4,6,10]
K = 4
```

Try:

```text
x = 4
```

Count:

```text
floor(8/4)  = 2
floor(4/4)  = 1
floor(6/4)  = 1
floor(10/4) = 2
```

Total:

```text
2 + 1 + 1 + 2 = 6
```

Since:

```text
6 >= 4
```

we can make at least `4` pieces.

So:

```text
can(4) = True
```

Try:

```text
x = 6
```

Count:

```text
floor(8/6)  = 1
floor(4/6)  = 0
floor(6/6)  = 1
floor(10/6) = 1
```

Total:

```text
1 + 0 + 1 + 1 = 3
```

Since:

```text
3 < 4
```

```text
can(6) = False
```

Therefore the answer lies between:

```text
4 and 6
```

---

## 2.4 Choose the Binary-Search Answer

Candidate:

```text
x = equal length of every piece
```

We are maximizing:

```text
x
```

So the binary-search question is:

```text
Can we cut at least K
complete pieces
of length x?
```

That is:

```text
can(x)
```

The final answer is:

```text
maximum x such that can(x) = True
```

So we need:

```text
LAST TRUE
```

---

## 2.5 Build the Predicate

For each rope:

```text
length = a[i]
```

pieces of length `x`:

```text
piecesFromThisRope = floor(a[i] / x)
```

Total:

```text
totalPieces = Σ floor(a[i] / x)
```

Predicate:

```text
can(x) = (totalPieces >= K)
```

### Compact Formula

```text
can(x) = [Σ floor(a[i] / x) >= K]
```

---

## 2.6 Why `floor(length / x)`

Suppose:

```text
rope = 8
x = 3
```

Then:

```text
8 / 3 = 2.666...
```

But we cannot produce:

```text
0.666...
```

of a complete piece.

Actual cut:

```text
[3][3] + leftover 2
```

So:

```text
floor(8/3) = 2
```

### Another Example

```text
rope = 10
x = 5
```

```text
floor(10/5) = 2
```

Exact cut:

```text
[5][5]
```

---

## 2.7 Monotonicity

Suppose a piece length:

```text
x
```

works.

Then any smaller piece length:

```text
y < x
```

also works because each rope can produce the same number or more pieces.

Example:

```text
rope = 10
```

```text
x = 5 → 2 pieces
x = 4 → 2 pieces
x = 2 → 5 pieces
```

So if:

```text
can(5) = True
```

then:

```text
can(4) = True
can(3) = True
can(2) = True
```

Predicate shape:

```text
T T T T T T F F F F
          ^
          |
      last True
```

This proves binary search is valid.

---

## 2.8 Search Space

The candidate length cannot be negative.

So:

```text
low = 0
```

The piece length cannot exceed the largest rope.

A tight upper bound is:

```text
high = max(a[i])
```

The lecture also uses the constraint-based upper bound:

```text
10^7
```

for the shown limits.

### Example

```text
ropes = [8,4,6,10]
```

So:

```text
low = 0
high = 10
```

ASCII:

```text
0--------------------------------10
|                                 |
smallest                      largest
possible                       rope
```

---

## 2.9 Detailed Dry Run

Use:

```text
ropes = [8,4,6,10]
K = 4
```

### Candidate x = 4

```text
8/4  → 2
4/4  → 1
6/4  → 1
10/4 → 2
```

```text
total = 6
```

So:

```text
6 >= 4
```

```text
can(4) = True
```

---

### Candidate x = 5

```text
floor(8/5)  = 1
floor(4/5)  = 0
floor(6/5)  = 1
floor(10/5) = 2
```

```text
total = 4
```

So:

```text
can(5) = True
```

---

### Candidate x = 5.1

```text
floor(8/5.1)  = 1
floor(4/5.1)  = 0
floor(6/5.1)  = 1
floor(10/5.1) = 1
```

```text
total = 3
```

So:

```text
can(5.1) = False
```

Therefore:

```text
maximum feasible x = 5
```

### Visual

```text
piece length x:

0 -------------------- 5 ---------------------- 10
T T T T T T T T T T   |   F F F F F F F F F
                       ^
                       |
                    boundary
```

---

## 2.10 Binary Search Dry Run

For easier arithmetic, start with:

```text
low = 0
high = 10
```

### Iteration 1

```text
mid = (0 + 10) / 2 = 5
```

We already know:

```text
can(5) = True
```

We are maximizing.

So search right:

```text
low = 5
```

Range:

```text
[5,10]
```

---

### Iteration 2

```text
mid = 7.5
```

Pieces:

```text
floor(8/7.5)  = 1
floor(4/7.5)  = 0
floor(6/7.5)  = 0
floor(10/7.5) = 1
```

Total:

```text
2
```

False.

Move left:

```text
high = 7.5
```

Range:

```text
[5,7.5]
```

---

### Iteration 3

```text
mid = 6.25
```

Pieces:

```text
1 + 0 + 0 + 1 = 2
```

False.

New range:

```text
[5,6.25]
```

---

### Iteration 4

```text
mid = 5.625
```

Pieces:

```text
1 + 0 + 1 + 1 = 3
```

False.

New range:

```text
[5,5.625]
```

The interval keeps shrinking toward:

```text
5
```

---

## 2.11 Algorithm

```text
low = 0
high = max rope length

repeat enough times:

    mid = (low + high) / 2

    pieces = 0

    for each rope:
        pieces += floor(rope / mid)

    if pieces >= K:
        // mid works
        // try a longer piece
        low = mid

    else:
        // mid is too long
        high = mid

answer ≈ low
```

---

## 2.12 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canCut(double pieceLength, const vector<int>& ropes, long long k) {
    long long pieces = 0;

    for (int ropeLength : ropes) {
        pieces += (long long)(ropeLength / pieceLength);

        if (pieces >= k) {
            return true;
        }
    }

    return false;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    long long k;
    cin >> n >> k;

    vector<int> ropes(n);

    double low = 0.0;
    double high = 0.0;

    for (int& ropeLength : ropes) {
        cin >> ropeLength;
        high = max(high, (double)ropeLength);
    }

    for (int iteration = 0; iteration < 100; ++iteration) {
        double mid = (low + high) / 2.0;

        if (canCut(mid, ropes, k)) {
            low = mid;
        } else {
            high = mid;
        }
    }

    cout << fixed << setprecision(10) << low << '\n';

    return 0;
}
```

### Code Dry Run

At:

```text
mid = 5
```

checker computes:

```text
pieces = 1 + 0 + 1 + 2 = 4
```

So:

```text
pieces >= k
```

is true.

Code performs:

```cpp
low = mid;
```

because we want a **larger** feasible piece length.

---

## 2.13 Complexity

Let:

```text
N = number of ropes
I = number of binary-search iterations
```

One checker:

```text
O(N)
```

With fixed:

```text
I = 100
```

total:

```text
O(100N)
```

Practically:

```text
O(N)
```

with a constant number of decimal-search iterations.

General view:

```text
O(N log(searchRange / precision))
```

Extra space:

```text
O(1)
```

besides input storage.

---

## 2.14 Recognition Model

When you see:

```text
cut items
into equal-size pieces

need at least K pieces

maximize piece size
```

think:

```text
candidate x = piece size
```

Checker:

```text
Σ floor(a[i]/x) >= K
```

Monotonicity:

```text
smaller x
→ more pieces
→ easier

larger x
→ fewer pieces
→ harder
```

So:

```text
T T T T F F F
      ^
      |
   last True
```

Technique:

```text
Binary Search on Decimal Answer
```

---

# 3. Problem 2 — Jumping Through Segment

## 3.1 What It Asks

The lecture's second problem is:

```text
Jumping Through Segment
[CP-31 1400: P3]
```

We have an ordered sequence of segments:

```text
[L1,R1]
[L2,R2]
...
[Ln,Rn]
```

We begin at:

```text
position 0
```

For every step, we must land somewhere inside the next segment.

Suppose the maximum allowed jump distance is:

```text
x
```

Find the:

```text
minimum x
```

that allows us to reach the final segment while visiting the segments in order.

---

## 3.2 What Result Is Expected

The result is **not**:

```text
the exact landing positions
```

It is:

```text
minimum possible maximum jump distance x
```

### Lecture-Style Example

Use:

```text
Segment 1 = [1,2]
Segment 2 = [3,6]
Segment 3 = [3,4]
```

Start:

```text
position = 0
```

For:

```text
x = 1
```

we cannot reach the second segment.

For:

```text
x = 2
```

we can process all segments.

So expected answer:

```text
2
```

---

## 3.3 Small Dry Run First

Start:

```text
reachable = [0,0]
```

Try:

```text
x = 2
```

### Segment 1 = [1,2]

Expand current interval by `2`:

```text
[0-2, 0+2] = [-2,2]
```

Intersect:

```text
[-2,2] ∩ [1,2] = [1,2]
```

So after segment 1:

```text
reachable = [1,2]
```

---

### Segment 2 = [3,6]

Expand:

```text
[1-2, 2+2] = [-1,4]
```

Intersect:

```text
[-1,4] ∩ [3,6] = [3,4]
```

So:

```text
reachable = [3,4]
```

---

### Segment 3 = [3,4]

Expand:

```text
[3-2, 4+2] = [1,6]
```

Intersect:

```text
[1,6] ∩ [3,4] = [3,4]
```

Still non-empty.

Therefore:

```text
canJump(2) = True
```

---

## 3.4 Choose the Binary-Search Answer

Candidate:

```text
x = maximum allowed distance for one jump
```

We are minimizing:

```text
x
```

Question:

```text
Can we visit every segment
if every jump has distance <= x?
```

That is:

```text
canJump(x)
```

We need:

```text
minimum x such that canJump(x) = True
```

So this is:

```text
FIRST TRUE
```

---

## 3.5 What Positions Are Reachable After One Jump?

Suppose before the next jump we may currently be anywhere inside:

```text
[L,R]
```

With maximum jump:

```text
x
```

from `L` we can reach left to:

```text
L-x
```

from `R` we can reach right to:

```text
R+x
```

So all positions reachable in one jump lie in:

```text
[L-x, R+x]
```

### Dry Run

```text
current = [3,4]
x = 2
```

Expanded:

```text
[3-2, 4+2] = [1,6]
```

ASCII:

```text
current:
        [3---4]

after jump <= 2:
    [1-----------6]
```

---

## 3.6 Intersect With the Next Segment

The next landing must satisfy two conditions:

```text
1. reachable from previous positions
2. inside the required segment
```

Therefore:

```text
nextReachable = expandedRange ∩ nextSegment
```

If:

```text
expandedRange = [A,B]
segment       = [L,R]
```

then:

```text
nextLeft = max(A,L)
nextRight = min(B,R)
```

Feasible iff:

```text
nextLeft <= nextRight
```

### Dry Run

```text
expanded = [-1,4]
segment  = [3,6]
```

Intersection:

```text
[max(-1,3), min(4,6)] = [3,4]
```

ASCII:

```text
expanded: [-1----------------4]
segment:             [3------------6]

result:              [3----4]
```

---

## 3.7 Build `canJump(x)`

Start before any segment:

```text
left = 0
right = 0
```

For every segment:

```text
[segmentLeft, segmentRight]
```

### Step 1 — Expand

```text
left  = left - x
right = right + x
```

### Step 2 — Intersect

```text
left  = max(left, segmentLeft)
right = min(right, segmentRight)
```

### Step 3 — Check

If:

```text
left > right
```

then no valid landing position remains.

Return:

```text
False
```

If every segment is processed:

```text
True
```

### Compact Transition

```text
[L,R] → [L-x,R+x] → intersection with [Li,Ri]
```

Equivalent update:

```text
L = max(L-x, Li)
R = min(R+x, Ri)
```

---

## 3.8 Why the Predicate Is Monotonic

Suppose:

```text
canJump(x) = True
```

Now allow a larger maximum jump:

```text
y >= x
```

Every jump that was valid with maximum distance `x` is still valid with limit `y`.

Also, every expanded reachable range becomes the same size or larger:

```text
[L-x,R+x]
```

becomes:

```text
[L-y,R+y]
```

where:

```text
y >= x
```

So increasing the allowed jump cannot destroy an existing path.

Therefore:

```text
canJump(x) = True
⇒
canJump(y) = True for y >= x
```

Predicate:

```text
F F F F F T T T T
          ^
          |
      first True
```

Binary search is valid.

---

## 3.9 Detailed Dry Run — x = 1

Segments:

```text
[1,2]
[3,6]
[3,4]
```

Start:

```text
reachable = [0,0]
```

### Segment 1

Expand:

```text
[0-1, 0+1] = [-1,1]
```

Intersect:

```text
[-1,1] ∩ [1,2] = [1,1]
```

Still valid:

```text
reachable = [1,1]
```

---

### Segment 2

Expand:

```text
[1-1, 1+1] = [0,2]
```

Intersect with:

```text
[3,6]
```

Compute:

```text
newLeft = max(0,3) = 3
newRight = min(2,6) = 2
```

Now:

```text
3 > 2
```

Intersection is empty.

Therefore:

```text
canJump(1) = False
```

ASCII:

```text
reachable before segment 2:
[0------2]

segment 2:
          [3----------------6]

no overlap
```

---

## 3.10 Detailed Dry Run — x = 2

Same segments:

```text
[1,2]
[3,6]
[3,4]
```

Start:

```text
[0,0]
```

### Segment 1

Expand:

```text
[-2,2]
```

Intersect:

```text
[-2,2] ∩ [1,2] = [1,2]
```

---

### Segment 2

Expand:

```text
[1-2, 2+2] = [-1,4]
```

Intersect:

```text
[-1,4] ∩ [3,6] = [3,4]
```

---

### Segment 3

Expand:

```text
[3-2, 4+2] = [1,6]
```

Intersect:

```text
[1,6] ∩ [3,4] = [3,4]
```

Never empty.

Therefore:

```text
canJump(2) = True
```

### Full Visual

```text
start:
                    ● 0

jump <= 2:
              [-2---------2]

segment 1:
                         [1---2]

reachable:
                         [1---2]

next jump <= 2:
                   [-1-------------4]

segment 2:
                                  [3-----------6]

reachable:
                                  [3---4]

next jump <= 2:
                         [1----------------6]

segment 3:
                                  [3---4]

reachable:
                                  [3---4]
```

---

## 3.11 Binary Search Dry Run

We know:

```text
canJump(1) = False
canJump(2) = True
```

Suppose integer search space is:

```text
[0,10]
```

We want:

```text
first True
```

### Iteration 1

```text
low = 0
high = 10
mid = 5
```

A jump limit of `5` is feasible.

So:

```text
answer = 5
high = 4
```

---

### Iteration 2

```text
low = 0
high = 4
mid = 2
```

```text
canJump(2) = True
```

So:

```text
answer = 2
high = 1
```

---

### Iteration 3

```text
low = 0
high = 1
mid = 0
```

Cannot even reach:

```text
[1,2]
```

So:

```text
canJump(0) = False
```

Move:

```text
low = 1
```

---

### Iteration 4

```text
mid = 1
```

We already know:

```text
canJump(1) = False
```

Move:

```text
low = 2
```

Stop.

Answer:

```text
2
```

---

## 3.12 Algorithm

```text
canJump(x):

    left = 0
    right = 0

    for each segment [Li,Ri]:

        // expand reachable positions
        left -= x
        right += x

        // must land inside this segment
        left = max(left, Li)
        right = min(right, Ri)

        if left > right:
            return false

    return true
```

Binary search:

```text
low = 0
high = safe upper bound
answer = high

while low <= high:

    mid = low + (high-low)/2

    if canJump(mid):
        answer = mid
        high = mid - 1

    else:
        low = mid + 1

return answer
```

---

## 3.13 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canJump(long long maxJump, const vector<pair<long long, long long>>& segments) {
    long long left = 0;
    long long right = 0;

    for (auto [segmentLeft, segmentRight] : segments) {
        left -= maxJump;
        right += maxJump;

        left = max(left, segmentLeft);
        right = min(right, segmentRight);

        if (left > right) {
            return false;
        }
    }

    return true;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<pair<long long, long long>> segments(n);

    long long high = 0;

    for (auto& [left, right] : segments) {
        cin >> left >> right;
        high = max(high, max(llabs(left), llabs(right)));
    }

    long long low = 0;
    long long answer = high;

    while (low <= high) {
        long long mid = low + (high - low) / 2;

        if (canJump(mid, segments)) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    cout << answer << '\n';

    return 0;
}
```

### Code Dry Run

For:

```text
maxJump = 2
current = [1,2]
next segment = [3,6]
```

Code does:

```cpp
left -= maxJump;
right += maxJump;
```

so:

```text
[-1,4]
```

Then:

```cpp
left = max(left, segmentLeft);
right = min(right, segmentRight);
```

becomes:

```text
[max(-1,3), min(4,6)] = [3,4]
```

Exactly the mathematical transition.

---

## 3.14 Complexity

Let:

```text
N = number of segments
M = integer search-space upper bound
```

One checker:

```text
O(N)
```

Binary-search iterations:

```text
O(log M)
```

Total:

```text
O(N log M)
```

Extra checker space:

```text
O(1)
```

---

## 3.15 Recognition Model

When you see:

```text
start from a point

visit segments in order

each move has maximum distance X

minimize X
```

think:

```text
candidate x = allowed jump
```

Do **not** track one exact position.

Track:

```text
all currently reachable positions
=
interval [L,R]
```

Transition:

```text
[L,R]
→ expand by x
→ [L-x,R+x]
→ intersect with next segment
```

Checker:

```text
does reachable interval remain non-empty
through all segments?
```

Monotonicity:

```text
bigger x
→ larger reachable range
→ easier
```

So:

```text
F F F F T T T
        ^
        |
    first True
```

Technique:

```text
Binary Search on Answer
+
Interval DP / Reachable Range
```

---

# 4. Ropes vs Jumping Through Segment

| Idea | Ropes | Jumping Through Segment |
|---|---|---|
| Answer | piece length `x` | max jump `x` |
| Optimize | maximize | minimize |
| Search type | decimal | integer |
| Checker | count pieces | propagate reachable interval |
| Core formula | `Σ floor(a[i]/x)` | `[L-x,R+x] ∩ [Li,Ri]` |
| True region | small `x` | large `x` |
| Predicate shape | `TTTTFFFF` | `FFFFTTTT` |
| Boundary | last True | first True |
| Checker time | `O(N)` | `O(N)` |

### One-Line Difference

```text
Ropes:
"Can I still make enough pieces?"

Jumping Through Segment:
"Can I still keep at least one reachable position?"
```

---

# 5. Common Mistakes

## Mistake 1 — Wrong Boundary for Ropes

Ropes asks:

```text
maximum feasible piece length
```

So:

```text
LAST TRUE
```

not first True.

---

## Mistake 2 — Summing Raw Division Without Floor Meaning

For:

```text
10 / 3 = 3.333...
```

the rope gives:

```text
3 complete pieces
```

not `3.333` pieces.

Checker needs:

```text
floor(length/x)
```

---

## Mistake 3 — Forgetting to Stop Piece Count Early

If:

```text
pieces >= K
```

we already know the checker is True.

No need to let the sum grow unnecessarily.

---

## Mistake 4 — Tracking One Position in Jumping Through Segment

After each segment, there may be many valid landing positions.

Example:

```text
reachable = [3,4]
```

Choosing only:

```text
3
```

or only:

```text
4
```

can lose valid future paths.

Keep the entire interval:

```text
[3,4]
```

---

## Mistake 5 — Intersect Before Expanding

Correct order:

```text
current interval
→ expand by x
→ intersect with next segment
```

Not:

```text
intersect first
→ expand later
```

The jump happens **before landing in the next segment**.

---

## Mistake 6 — Forgetting Empty Intersection

After:

```text
left = max(left-x, Li)
right = min(right+x, Ri)
```

always check:

```text
left <= right
```

If false:

```text
candidate x is impossible
```

---

## Mistake 7 — Wrong Monotonic Direction

Ropes:

```text
larger piece length
→ harder
```

So:

```text
TTTTFFFF
```

Jumping:

```text
larger max jump
→ easier
```

So:

```text
FFFFTTTT
```

---

## Mistake 8 — Mixing Integer and Decimal Binary Search

Ropes:

```text
answer may be decimal
```

Jumping Through Segment:

```text
answer is an integer jump limit
```

Use the appropriate search style.

---

# 6. Final Mental Model

## Problem 1 — Ropes

```text
MAXIMIZE PIECE LENGTH x
          |
          v
for each rope:
pieces = floor(length/x)
          |
          v
total pieces >= K ?
          |
      +---+---+
      |       |
     YES      NO
      |       |
      v       v
   x works   too long
      |
      v
T T T T T F F F
        ^
        |
     last True
```

Core formula:

```text
can(x) = [Σ floor(a[i]/x) >= K]
```

---

## Problem 2 — Jumping Through Segment

```text
MINIMIZE MAX JUMP x
          |
          v
start reachable = [0,0]
          |
          v
expand:
[L-x,R+x]
          |
          v
intersect next segment:
[max(L-x,Li), min(R+x,Ri)]
          |
          v
empty?
    |
+---+---+
|       |
YES     NO
|       |
v       v
fail   continue
          |
          v
F F F F T T T
        ^
        |
    first True
```

Core transition:

```text
L = max(L-x, Li)
R = min(R+x, Ri)
```

Feasible iff:

```text
L <= R
```

---

## 60-Second Recognition

```text
OPTIMIZATION?
     |
     v
GUESS ANSWER x
     |
     v
CAN I CHECK x IN O(N)?
     |
     v
IS CHECK MONOTONIC?
     |
     +---------------------------+
     |                           |
     v                           v
MAXIMIZE                      MINIMIZE
     |                           |
     v                           v
LAST TRUE                     FIRST TRUE
```

Then ask what the checker really is:

```text
count complete pieces?
→ floor division

maintain possible positions?
→ interval expansion + intersection
```

## One Sentence to Remember

```text
Binary Search Level 3 is mostly about inventing the checker:
once `can(x)` is correct and monotonic, the binary search itself is routine.
```
