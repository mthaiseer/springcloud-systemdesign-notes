# AlgoZenith Greedy Techniques — Class 1
## TLE-Style Self-Study Notes — Prerequisites + Derivations + Inline Examples + ASCII Diagrams + C++17

> **Goal:** understand how the greedy rule is *derived* and *proved* instead of memorizing a sorting key or a formula.
>
> **Structure used throughout:**
>
> ```text
> prerequisite
> → what the model asks
> → variables
> → tiny example
> → observation
> → greedy claim
> → proof / derivation
> → actual numbers beside the derivation
> → ASCII visualization
> → algorithm
> → C++17
> → complexity
> → recognition model
> → don't-memorize model
> ```
>
> **Important:** the supplied lecture screenshots explicitly develop rearrangement/swapping, score-decay-time ordering, reverse operation handling, median/weighted median, and Manhattan-distance minimization.  
> The board also lists intervals / sweep line / priority as greedy categories, but those topics are only mentioned in the supplied screenshots, so they are not expanded into unsupported lecture content here.
>
> **Math rendering:** display math uses fenced `math` blocks only.

---

# Clickable Table of Contents

- [0. Lecture Map](#0-lecture-map)
- [1. Shared Greedy Prerequisites](#1-shared-greedy-prerequisites)
  - [1.1 What Greedy Means](#11-what-greedy-means)
  - [1.2 Local Choice vs Global Optimum](#12-local-choice-vs-global-optimum)
  - [1.3 Exchange / Swapping Proof](#13-exchange--swapping-proof)
  - [1.4 Why We Compare Only Two Positions](#14-why-we-compare-only-two-positions)
  - [1.5 Inversions](#15-inversions)
  - [1.6 Sorting as a Greedy Tool](#16-sorting-as-a-greedy-tool)
  - [1.7 Cross Multiplication for Ratio Comparators](#17-cross-multiplication-for-ratio-comparators)
  - [1.8 Completion Time](#18-completion-time)
  - [1.9 Reverse Greedy](#19-reverse-greedy)
  - [1.10 Binary View of `+1` and `×2`](#110-binary-view-of-1-and-2)
  - [1.11 Absolute Distance](#111-absolute-distance)
  - [1.12 Convex / V-Shaped Cost](#112-convex--v-shaped-cost)
  - [1.13 Median](#113-median)
  - [1.14 Weighted Median](#114-weighted-median)
  - [1.15 Manhattan Distance Separability](#115-manhattan-distance-separability)
  - [1.16 Overflow](#116-overflow)
  - [1.17 Universal Greedy Proof Checklist](#117-universal-greedy-proof-checklist)
- [2. Pattern 1 — Minimum Dot Product / Rearrangement](#2-pattern-1--minimum-dot-product--rearrangement)
- [3. Pattern 2 — Score–Decay–Time Scheduling](#3-pattern-2--scoredecaytime-scheduling)
- [4. Pattern 3 — Minimum Operations From 0 to Y](#4-pattern-3--minimum-operations-from-0-to-y)
- [5. Pattern 4 — Median Minimizes Sum of Absolute Distances](#5-pattern-4--median-minimizes-sum-of-absolute-distances)
- [6. Pattern 5 — Weighted Median](#6-pattern-5--weighted-median)
- [7. Pattern 6 — Manhattan Meeting Point in 2D](#7-pattern-6--manhattan-meeting-point-in-2d)
- [8. Pattern Comparison](#8-pattern-comparison)
- [9. Final Recognition Checklist](#9-final-recognition-checklist)
- [10. Compact Revision Card](#10-compact-revision-card)

---

# 0. Lecture Map

The board begins with a broad greedy map:

```text
GREEDY
  |
  +-- Sorting
  |     |
  |     +-- Interval-type ordering
  |     +-- Priority-based ordering
  |     +-- Sweep-line related forms
  |
  +-- Operation handling
  |     |
  |     +-- decide which operation is forced / best
  |     +-- sometimes solve backward
  |
  +-- Classical ideas
        |
        +-- Rearrangement / swapping
        +-- Median
        +-- Manhattan distance
        +-- ...
```

The important lesson is:

```text
Greedy is NOT one algorithm.

It is a proof style:
choose a locally best/forced action
and prove that an optimal solution can contain it.
```

---

# 1. Shared Greedy Prerequisites

## 1.1 What Greedy Means

A greedy solution usually has:

```text
STATE
+
LOCAL CHOICE
+
PROOF THAT THE CHOICE IS SAFE
```

Example:

```text
State:
two values still need to be paired

Choice:
pair small with large

Proof:
swapping from same-order pairing
cannot make the minimization worse
```

Without the proof:

```text
"sort this way"
```

is just a guess.

---

## 1.2 Local Choice vs Global Optimum

Suppose the full answer is:

```text
term1 + term2 + term3 + ... + termN
```

A greedy proof often changes only a tiny local part:

```text
term_i
term_j
```

All other terms stay unchanged.

So:

```text
global proof
can reduce to
2-item local proof
```

ASCII:

```text
FULL SOLUTION

[ same ][ same ][ LOCAL ][ same ][ same ]
                     |
                     v
            compare only this
```

That is the key reason exchange proofs are powerful.

---

## 1.3 Exchange / Swapping Proof

Typical structure:

```text
OPT contains a locally "wrong" pair

        X ... Y
          |
          v
        swap
          |
          v
        Y ... X

Question 1:
Is the new solution still feasible?

Question 2:
Is the objective same or better?

If YES:
the swap is safe.
```

Repeat safe swaps:

```text
remove inversion
→ remove inversion
→ remove inversion
→ greedy order
```

So an optimal solution can be transformed into the greedy structure.

---

## 1.4 Why We Compare Only Two Positions

Suppose only positions `i` and `j` change.

Old:

```text
... + a_i b_i + ... + a_j b_j + ...
```

Swapped:

```text
... + a_i b_j + ... + a_j b_i + ...
```

Everything outside `i,j` is identical.

Therefore compare only:

```text
a_i b_i + a_j b_j

vs

a_i b_j + a_j b_i
```

This local comparison is enough.

---

## 1.5 Inversions

For an ascending order:

```text
x1 <= x2 <= x3 <= ...
```

an inversion is:

```text
i < j
but
x[i] > x[j]
```

Example:

```text
[1, 7, 4, 9]
```

`7,4` is an inversion.

Many greedy sorting proofs show:

```text
if inversion exists
→ swap it
→ objective does not get worse
```

Then an optimal sorted arrangement exists.

---

## 1.6 Sorting as a Greedy Tool

Sorting is useful when the proof says:

```text
relative order between ANY two items
can be decided locally
```

Examples in this class:

```text
dot product:
decide relative pairing by value order

scheduling:
decide P1 before P2
by comparing D1/T1 vs D2/T2
```

Sorting then applies this local rule globally.

---

## 1.7 Cross Multiplication for Ratio Comparators

Suppose we derive:

```math
\frac{D_1}{T_1}
>
\frac{D_2}{T_2}
```

For positive times, compare:

```math
D_1T_2
>
D_2T_1
```

instead of using floating point.

### Inline Example

```text
D1 = 1
T1 = 2

D2 = 2
T2 = 3
```

Ratios:

```text
D1/T1
= 1/2
= 0.5

D2/T2
= 2/3
≈ 0.667
```

Cross products:

```text
D1*T2
= 1×3
= 3

D2*T1
= 2×2
= 4
```

Because:

```text
3 < 4
```

job 2 has the larger ratio.

---

## 1.8 Completion Time

If jobs execute one after another:

```text
P1 duration = T1
P2 duration = T2
```

Order:

```text
P1 → P2
```

Completion:

```text
P1 finishes at T1

P2 finishes at T1+T2
```

ASCII:

```text
time:
0---------------T1----------------T1+T2

|------ P1 ------|------- P2 -------|
        ^                   ^
       C1                  C2
```

This distinction is essential:

```text
own duration != completion time
```

A later job waits for earlier jobs.

---

## 1.9 Reverse Greedy

Some forward operations are hard to choose.

Example:

```text
from x:
1. x → x+1
2. x → 2x
```

Goal:

```text
reach target y
with minimum operations
```

Forward choice can be ambiguous:

```text
Should I +1 now?
Should I double now?
```

But backward from `y`, the last operation may become obvious.

This is a general technique:

```text
forward:
many choices

reverse:
last move may be forced
```

---

## 1.10 Binary View of `+1` and `×2`

Binary connection:

```text
x × 2
=
left shift by one bit
```

Example:

```text
5 = 101

5×2
= 10
= 1010
```

Building a binary number from left to right:

```text
×2
→ shift existing bits

+1
→ create a needed 1 bit
```

This gives another way to understand the minimum-operation problem later.

---

## 1.11 Absolute Distance

Distance between `x` and point `a`:

```math
|x-a|
```

Example:

```text
x = 7
a = 3

|7-3|
= 4
```

For many points:

```math
F(x)
=
\sum_i |x-x_i|
```

This creates a piecewise-linear V-shaped / convex cost.

---

## 1.12 Convex / V-Shaped Cost

One term:

```math
|x-a|
```

looks like:

```text
cost
 ^
 | \       /
 |  \     /
 |   \   /
 |    \ /
 |     V
 +-----a----------> x
```

A sum of such terms remains convex:

```text
decreasing
→ flat/turning region
→ increasing
```

So the minimum can be found by understanding where the slope changes sign.

---

## 1.13 Median

For sorted:

```text
x1 <= x2 <= ... <= xn
```

a median minimizes:

```math
\sum_i |x-x_i|
```

Odd `n`:

```text
one middle point
```

Even `n`:

```text
every point between the two middle values
is optimal
```

For integer `x`, every integer in that interval is optimal.

---

## 1.14 Weighted Median

Weighted objective:

```math
\sum_i k_i|x-x_i|
```

Interpret:

```text
point x_i with weight k_i
behaves conceptually like
k_i copies of x_i
```

So the ordinary median becomes:

```text
weighted median
```

Implementation uses cumulative weights, not literal expansion.

---

## 1.15 Manhattan Distance Separability

For two points:

```text
P = (x,y)
Q = (a,b)
```

Manhattan distance:

```math
|x-a|+|y-b|
```

For many points:

```math
\sum_i
(
|x-x_i|
+
|y-y_i|
)
```

Separate:

```math
=
\sum_i |x-x_i|
+
\sum_i |y-y_i|
```

So:

```text
optimize x independently
optimize y independently
```

Each is a 1D median problem.

---

## 1.16 Overflow

The board shows constraints large enough that products deserve attention.

For example:

```text
10^9 × 10^9
= 10^18
```

This is near signed 64-bit range.

For comparators such as:

```text
D1*T2
vs
D2*T1
```

use:

```cpp
__int128
```

if constraints can make `long long` unsafe.

---

## 1.17 Universal Greedy Proof Checklist

Before coding ask:

```text
1. What exactly am I minimizing/maximizing?

2. Can I compare two local choices?

3. If I swap two items,
   which terms stay unchanged?

4. Can I factor:
   Greedy - Other
   or
   Other - Greedy?

5. What signs do the factors have?

6. Does that imply a sorted order?

7. Did a ratio appear?
   Use cross multiplication.

8. Is forward decision ambiguous?
   Try reversing the operations.

9. Is objective:
   Σ|x-x_i|?
   Think median.

10. Is it weighted?
    Think weighted median.

11. Is it 2D Manhattan distance?
    Separate x and y.
```

---

# 2. Pattern 1 — Minimum Dot Product / Rearrangement

## 2.1 What the Model Asks

Two arrays:

```text
A = [a1,a2,...,an]
B = [b1,b2,...,bn]
```

We may rearrange one or both arrays.

Goal:

```math
\min
\sum_{i=1}^{n} a_i b_i
```

The board's greedy idea is:

```text
pair large values from one array
with small values from the other
```

Therefore:

```text
sort one ascending
sort the other descending
```

---

## 2.2 Tiny Example First

```text
A = [1,2,3]
B = [1,2,3]
```

Same order:

```text
1×1 + 2×2 + 3×3

= 1+4+9

= 14
```

Opposite order:

```text
A = [1,2,3]
B = [3,2,1]
```

Cost:

```text
1×3 + 2×2 + 3×1

= 3+4+3

= 10
```

So opposite order is smaller.

Now prove it for every input.

---

## 2.3 Two-Item Proof With Numbers First

Take:

```text
small A = 2
large A = 5

small B = 3
large B = 7
```

Same-order:

```text
2×3 + 5×7

= 6+35

= 41
```

Crossed/opposite pairing:

```text
2×7 + 5×3

= 14+15

= 29
```

So:

```text
crossed < same
```

Difference:

```text
41-29
= 12
```

---

## 2.4 General Algebra Derivation

Assume:

```math
a_1\le a_2
```

and:

```math
b_1\le b_2
```

Same-order contribution:

```math
S
=
a_1b_1+a_2b_2
```

Opposite/crossed contribution:

```math
C
=
a_1b_2+a_2b_1
```

For minimization, we want to show:

```math
C\le S
```

Compute:

```math
S-C
=
a_1b_1+a_2b_2-a_1b_2-a_2b_1
```

Group:

```math
=
a_2b_2-a_2b_1-a_1b_2+a_1b_1
```

Factor:

```math
=
a_2(b_2-b_1)-a_1(b_2-b_1)
```

Factor common term:

```math
S-C
=
(a_2-a_1)(b_2-b_1)
```

Because:

```math
a_2-a_1\ge0
```

and:

```math
b_2-b_1\ge0
```

we get:

```math
S-C\ge0
```

Therefore:

```math
S\ge C
```

So opposite pairing is never worse for minimization.

---

## 2.5 Same Derivation With Actual Numbers

Use:

```text
a1 = 2
a2 = 5
b1 = 3
b2 = 7
```

Start:

```text
S-C

= 2×3 + 5×7
  - 2×7 - 5×3
```

Calculate:

```text
= 6+35-14-15

= 12
```

Factored form:

```text
(a2-a1)(b2-b1)

= (5-2)(7-3)

= 3×4

= 12
```

Same value.

That is the important algebra pattern:

```text
four terms
→ group
→ factor
→ sign reasoning
```

---

## 2.6 Exchange Proof for the Whole Arrays

Suppose `A` is sorted ascending:

```text
a1 <= a2 <= ... <= an
```

For minimum dot product, `B` should be descending.

If `B` has a same-direction pair:

```text
i < j
and
b_i < b_j
```

then:

```text
small A is paired with small B
large A is paired with large B
```

Swap `b_i` and `b_j`.

The two-item proof says the dot product cannot increase.

Repeat until `B` is descending.

ASCII:

```text
BAD FOR MINIMUM:

small A -------- small B
large A -------- large B


SWAP:

small A -------- large B
large A -------- small B


small × large
large × small
→ smaller/equal total
```

---

## 2.7 Maximum vs Minimum

The same proof gives both directions.

### Maximum

```text
sort both in same order
```

### Minimum

```text
sort in opposite order
```

Mental rule:

```text
MAX:
large with large

MIN:
large with small
```

---

## 2.8 Negative Values

The proof still works because it only needs:

```text
a1 <= a2
b1 <= b2
```

Then:

```text
a2-a1 >= 0
b2-b1 >= 0
```

The actual values may be negative.

Example:

```text
A = [-4,2]
B = [-3,5]
```

Same:

```text
(-4)(-3)+2×5
= 12+10
= 22
```

Opposite:

```text
(-4)(5)+2(-3)
= -20-6
= -26
```

For minimization:

```text
-26 < 22
```

Opposite order still wins.

---

## 2.9 ASCII Visualization

```text
A ascending:

a1 <= a2 <= a3 <= ... <= an

B descending:

bn >= ... >= b3 >= b2 >= b1


pair:

smallest A  -------- largest B
next A      -------- next-largest B
...
largest A   -------- smallest B
```

---

## 2.10 Algorithm

```text
1. Sort A ascending.
2. Sort B descending.
3. Compute Σ A[i]*B[i].
```

---

## 2.11 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> a(n), b(n);

    for (auto& x : a)
        cin >> x;

    for (auto& x : b)
        cin >> x;

    sort(a.begin(), a.end());
    sort(b.rbegin(), b.rend());

    __int128 answer = 0;

    for (int i = 0; i < n; ++i) {
        answer +=
            (__int128)a[i] * b[i];
    }

    // Print __int128 if the problem constraints require it.
}
```

---

## 2.12 Complexity

```text
sorting:
O(N log N)

sum:
O(N)

total:
O(N log N)
```

---

## 2.13 Recognition Model

When you see:

```text
two arrays
+
rearrange pairing
+
objective Σ ai*bi
```

think:

```text
Rearrangement inequality
```

Then:

```text
maximize → same order
minimize → opposite order
```

---

## 2.14 Don't-Memorize Model

Remember only the local proof:

```text
same
=
smallA×smallB
+
largeA×largeB

cross
=
smallA×largeB
+
largeA×smallB
```

Difference:

```text
same-cross
=
(largeA-smallA)
×
(largeB-smallB)
>= 0
```

Everything else follows from repeated swaps.

---

# 3. Pattern 2 — Score–Decay–Time Scheduling

## 3.1 What the Model Asks

There are `N` questions/jobs.

For each job `i`:

```text
S_i = initial score
d_i = score decay per unit time
t_i = time required
```

If job `i` finishes at time:

```text
C_i
```

score becomes:

```math
S_i-d_iC_i
```

Goal:

```text
choose the order
that maximizes total score
```

---

## 3.2 Important Variable Meaning

Do not confuse:

```text
t_i
=
duration of job i
```

with:

```text
C_i
=
time at which job i finishes
```

If a job runs second:

```text
completion time
=
time of first job
+
its own time
```

---

## 3.3 Class-Style Example

Use the values visible in the lecture derivation:

```text
P1:
S1 = 10
d1 = 1
t1 = 2

P2:
S2 = 5
d2 = 2
t2 = 3
```

We compare both orders.

---

## 3.4 Order `P1 → P2`

Timeline:

```text
0-------2------------5
|  P1   |     P2     |
    ^           ^
   C1          C2
```

Completion times:

```text
C1 = 2

C2 = 2+3
   = 5
```

Score P1:

```text
10 - 1×2

= 8
```

Score P2:

```text
5 - 2×5

= 5-10

= -5
```

Total:

```text
8 + (-5)

= 3
```

---

## 3.5 Order `P2 → P1`

Timeline:

```text
0----------3---------5
|    P2    |   P1    |
      ^          ^
     C2         C1
```

P2 finishes:

```text
3
```

P2 score:

```text
5 - 2×3

= -1
```

P1 finishes:

```text
3+2
= 5
```

P1 score:

```text
10 - 1×5

= 5
```

Total:

```text
-1+5

= 4
```

So:

```text
P2 → P1
```

is better:

```text
4 > 3
```

---

## 3.6 What Should Determine the Order?

P2 has:

```text
higher decay rate
```

but also takes:

```text
more time
```

We need one comparison combining both.

This is where the two-job exchange derivation produces a ratio.

---

## 3.7 General Two-Job Derivation

Suppose only two adjacent jobs matter:

```text
P1 and P2
```

Any time spent before them is the same in both orders, so it cancels from the comparison.

### Order `P1 → P2`

```math
Score_{12}
=
(S_1-d_1t_1)
+
(S_2-d_2(t_1+t_2))
```

### Order `P2 → P1`

```math
Score_{21}
=
(S_2-d_2t_2)
+
(S_1-d_1(t_2+t_1))
```

We prefer `P1 → P2` when:

```math
Score_{12}\ge Score_{21}
```

Substitute:

```math
(S_1-d_1t_1)
+
(S_2-d_2(t_1+t_2))
\ge
(S_2-d_2t_2)
+
(S_1-d_1(t_2+t_1))
```

Expand left:

```math
S_1+S_2
-d_1t_1
-d_2t_1
-d_2t_2
```

Expand right:

```math
S_1+S_2
-d_2t_2
-d_1t_2
-d_1t_1
```

Cancel common terms:

```text
S1
S2
-d1*t1
-d2*t2
```

Remain:

```math
-d_2t_1
\ge
-d_1t_2
```

Multiply by `-1`, so the inequality reverses:

```math
d_2t_1
\le
d_1t_2
```

Rearrange:

```math
d_1t_2
\ge
d_2t_1
```

Divide by positive `t1*t2`:

```math
\frac{d_1}{t_1}
\ge
\frac{d_2}{t_2}
```

Therefore:

```text
higher d/t should come earlier
```

Equivalent:

```text
lower t/d should come earlier
```

---

## 3.8 Put the Example Into the Formula

For P1:

```text
d1/t1
= 1/2
= 0.5
```

For P2:

```text
d2/t2
= 2/3
≈ 0.667
```

So:

```text
d2/t2 > d1/t1
```

Therefore:

```text
P2 before P1
```

The direct score calculation gave:

```text
P2→P1 = 4
P1→P2 = 3
```

So the formula matches the example.

---

## 3.9 Cross Multiplication in Code

Instead of:

```cpp
(double)a.d / a.t
```

compare:

```text
a before b
iff
a.d * b.t > b.d * a.t
```

Example:

```text
P1:
1×3 = 3

P2 side:
2×2 = 4

3 < 4
```

so P2 comes first.

---

## 3.10 Whole-Schedule Exchange Proof

Suppose a schedule contains adjacent jobs:

```text
P1, P2
```

but:

```math
\frac{d_1}{t_1}
<
\frac{d_2}{t_2}
```

Then they are in the wrong order.

Swap them:

```text
P2, P1
```

The two-job proof says total score cannot decrease.

Repeat until there are no inversions in `d/t`.

So an optimal schedule exists sorted by:

```text
d/t descending
```

---

## 3.11 ASCII Mental Model

```text
Question:
"Which job is more dangerous to delay?"

high decay d
+
small time t
=
large d/t
=
do earlier
```

Timeline priority:

```text
large d/t ----------------------> small d/t
EARLY                               LATE
```

---

## 3.12 Algorithm

```text
1. Store each job:
      S, d, t.

2. Sort by d/t descending
   using cross multiplication.

3. timeTaken = 0
   totalScore = 0

4. For every job:
      timeTaken += t
      totalScore += S - d*timeTaken
```

---

## 3.13 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Job {
    long long score;
    long long decay;
    long long time;
};

bool cmp(const Job& a, const Job& b) {
    __int128 left =
        (__int128)a.decay * b.time;

    __int128 right =
        (__int128)b.decay * a.time;

    if (left != right)
        return left > right;

    return a.time < b.time;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<Job> jobs(n);

    for (auto& job : jobs) {
        cin >> job.score
            >> job.decay
            >> job.time;
    }

    sort(
        jobs.begin(),
        jobs.end(),
        cmp
    );

    long long elapsed = 0;
    __int128 total = 0;

    for (const Job& job : jobs) {
        elapsed += job.time;

        total +=
            (__int128)job.score
            -
            (__int128)job.decay
            * elapsed;
    }

    // Print total using an __int128 helper if needed.
}
```

---

## 3.14 Complexity

```text
sorting:
O(N log N)

simulation:
O(N)

total:
O(N log N)
```

---

## 3.15 Recognition Model

When you see:

```text
jobs/questions
+
time to complete
+
penalty/decay while waiting
+
choose an order
```

do not guess the ratio.

Instead:

```text
take two jobs
→ compute order 1→2
→ compute order 2→1
→ cancel common terms
→ derive comparator
```

Here it becomes:

```text
d/t descending
```

---

## 3.16 Don't-Memorize Model

Remember this meaning:

```text
d/t
=
"damage caused by delaying this job"
relative to
"time consumed by doing it"
```

Large:

```text
damage / time
```

means:

```text
handle it earlier
```

---

# 4. Pattern 3 — Minimum Operations From 0 to Y

## 4.1 What the Model Asks

Start:

```text
x = 0
```

Allowed operations:

```text
1. x = x + 1
2. x = 2x
```

Given large target:

```text
y
```

find minimum operations needed to reach `y`.

The board emphasizes:

```text
"see backward"
```

because backward decisions are much more forced.

---

## 4.2 Why Forward Greedy Is Awkward

Suppose target:

```text
y = 12
```

At:

```text
x = 3
```

you could:

```text
+1 → 4
```

or:

```text
×2 → 6
```

Which is globally best?

Not obvious from only the current state.

Backward:

```text
12
```

is even.

The previous value could naturally be:

```text
6
```

by undoing a doubling.

This shrinks the target dramatically.

---

## 4.3 Reverse Operations

Forward:

```text
x → x+1
x → 2x
```

Backward:

```text
y → y-1
```

and when `y` is even:

```text
y → y/2
```

Now parity tells us what can be forced.

---

## 4.4 Odd Target — Last Move Is Forced

If:

```text
y is odd
```

could the last forward move have been doubling?

No.

Because:

```text
2×anything
```

is even.

Therefore if `y` is odd:

```text
last forward move MUST have been +1
```

So backward:

```math
y\rightarrow y-1
```

is forced.

### Inline Example

```text
y = 13
```

13 is odd.

Last move cannot be:

```text
2x = 13
```

for integer `x`.

Therefore last move was:

```text
12 + 1 = 13
```

Backward:

```text
13 → 12
```

---

## 4.5 Even Target — Undo Doubling

If:

```text
y is even
```

we can reverse a doubling:

```math
y\rightarrow y/2
```

This removes a binary shift in one step.

Example:

```text
12 → 6
```

instead of:

```text
12 → 11 → 10 → ...
```

The division makes the remaining magnitude much smaller.

---

## 4.6 Full Reverse Dry Run — `y = 12`

Start:

```text
12
```

Even:

```text
12 → 6
```

Even:

```text
6 → 3
```

Odd:

```text
3 → 2
```

Even:

```text
2 → 1
```

Odd:

```text
1 → 0
```

Total:

```text
5 steps
```

Reverse the path:

```text
0 → 1 → 2 → 3 → 6 → 12
```

Forward operations:

```text
+1
×2
+1
×2
×2
```

Also:

```text
5 steps
```

---

## 4.7 ASCII Visualization

```text
FORWARD:

0
|
+1
v
1
|
×2
v
2
|
+1
v
3
|
×2
v
6
|
×2
v
12


BACKWARD:

12
 |
 /2
 v
 6
 |
 /2
 v
 3
 |
 -1
 v
 2
 |
 /2
 v
 1
 |
 -1
 v
 0
```

Backward has the clearer rule:

```text
odd  → -1
even → /2
```

---

## 4.8 Binary Interpretation

Take:

```text
12
```

Binary:

```text
1100
```

Build it from left to right.

Start:

```text
0
```

First `1`:

```text
+1
→ 1
```

Next bit `1`:

```text
×2
→ 10

+1
→ 11
```

Next bit `0`:

```text
×2
→ 110
```

Next bit `0`:

```text
×2
→ 1100
```

Operations:

```text
+1, ×2, +1, ×2, ×2
```

Count:

```text
5
```

---

## 4.9 Direct Formula From Binary

For `y > 0`:

```text
number of ×2 operations
=
bit_length(y)-1
```

Every `1` bit needs one `+1`:

```text
number of +1 operations
=
popcount(y)
```

Therefore:

```math
minimumSteps
=
(bitLength(y)-1)
+
popcount(y)
```

### Inline Example — `y=12`

Binary:

```text
1100
```

Bit length:

```text
4
```

Popcount:

```text
2
```

So:

```text
steps
= (4-1)+2
= 3+2
= 5
```

Matches the reverse greedy dry run.

---

## 4.10 Why the Binary Formula Makes Sense

`×2`:

```text
append a 0 bit
```

`+1`:

```text
introduce a required 1
```

To construct:

```text
b_k b_(k-1) ... b_0
```

from the most-significant bit:

```text
for each next bit:
    shift left (×2)

if the new bit is 1:
    +1
```

This constructs exactly the target with no wasted operations.

---

## 4.11 Reverse Greedy Algorithm

```text
steps = 0

while y > 0:

    if y is odd:
        y--
    else:
        y /= 2

    steps++
```

---

## 4.12 C++17 — Reverse Greedy

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    unsigned long long y;
    cin >> y;

    long long steps = 0;

    while (y > 0) {
        if (y & 1ULL)
            --y;
        else
            y >>= 1;

        ++steps;
    }

    cout << steps << '\n';
}
```

---

## 4.13 C++17 — Bit Formula

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    unsigned long long y;
    cin >> y;

    if (y == 0) {
        cout << 0 << '\n';
        return 0;
    }

    int bitLength =
        64 - __builtin_clzll(y);

    int ones =
        __builtin_popcountll(y);

    long long answer =
        (bitLength - 1LL) + ones;

    cout << answer << '\n';
}
```

---

## 4.14 Complexity

Reverse simulation:

```text
O(log y)
```

because division by 2 repeatedly shrinks the number.

Space:

```text
O(1)
```

---

## 4.15 Recognition Model

When you see:

```text
start from small x
+
operations increase value
+
target huge
+
forward choice seems ambiguous
```

ask:

```text
Can I reverse the operations?
```

Especially when one operation becomes:

```text
division
```

backward.

---

## 4.16 Don't-Memorize Model

Do not memorize only:

```text
odd -> -1
even -> /2
```

Understand:

```text
odd target
cannot come from doubling
→ +1 was forced

even target
can undo a doubling
→ /2 removes one binary shift
```

---

# 5. Pattern 4 — Median Minimizes Sum of Absolute Distances

## 5.1 What the Model Asks

Given points on a line:

```text
x1,x2,...,xn
```

Choose one point `x` minimizing:

```math
F(x)
=
\sum_i |x-x_i|
```

This is the classical median form shown on the board.

---

## 5.2 Tiny Example

Points:

```text
1,3,7
```

Try:

```text
x=1:
|1-1|+|1-3|+|1-7|
= 0+2+6
= 8
```

```text
x=3:
|3-1|+|3-3|+|3-7|
= 2+0+4
= 6
```

```text
x=7:
|7-1|+|7-3|+|7-7|
= 6+4+0
= 10
```

Best:

```text
x=3
```

the median.

---

## 5.3 Step / Slope Derivation

Suppose `x` moves one unit to the right.

Let:

```text
L = number of points left of x
R = number of points right of x
```

For every left point:

```text
distance increases by 1
```

Total:

```text
+L
```

For every right point:

```text
distance decreases by 1
```

Total:

```text
-R
```

So net cost change is:

```math
F(x+1)-F(x)
=
L-R
```

---

## 5.4 Inline Example of the Slope Formula

Points:

```text
1,3,7
```

Take:

```text
x=2
```

Left:

```text
[1]

L=1
```

Right:

```text
[3,7]

R=2
```

Formula:

```text
F(3)-F(2)
= L-R
= 1-2
= -1
```

Calculate directly:

```text
F(2)
= |2-1|+|2-3|+|2-7|
= 1+1+5
= 7
```

```text
F(3)
= 2+0+4
= 6
```

Actual change:

```text
6-7
= -1
```

Matches.

---

## 5.5 Why Median Is the Turning Point

Before the median:

```text
more points lie to the right
```

Therefore:

```text
R > L
```

so:

```text
L-R < 0
```

Moving right decreases cost.

After the median:

```text
L > R
```

so moving right increases cost.

Therefore:

```text
minimum occurs where left/right balance
```

which is the median region.

---

## 5.6 ASCII Cost Graph

Example points:

```text
1,3,5,8
```

Cost:

```text
F(x)
=
|x-1|
+
|x-3|
+
|x-5|
+
|x-8|
```

Shape:

```text
cost
 ^
 |\
 | \
 |  \
 |   \________
 |            \
 |             \
 +------------------------> x
      1   3   5      8

          <--->
       minimum interval
          [3,5]
```

With an even number of points, the bottom may be flat.

---

## 5.7 Odd Number of Points

Example:

```text
[1,3,8]
```

Middle:

```text
3
```

Unique median:

```text
x=3
```

For integer locations:

```text
one optimal x
```

---

## 5.8 Even Number of Points

Example:

```text
[1,3,5,8]
```

Middle values:

```text
3 and 5
```

Every real:

```text
x in [3,5]
```

is optimal.

For integer `x`, optimal values:

```text
3,4,5
```

Number of integer minimizers:

```text
5-3+1

= 3
```

---

## 5.9 Number of Optimal Integer Solutions

Sorted array.

Let:

```text
leftMedian  = x[(n-1)/2]
rightMedian = x[n/2]
```

Then all integer minimizers are:

```text
leftMedian
...
rightMedian
```

Count:

```math
rightMedian-leftMedian+1
```

### Example

```text
[1,3,5,8]
```

Then:

```text
leftMedian = 3
rightMedian = 5
```

Count:

```text
5-3+1
= 3
```

---

## 5.10 Minimum Cost

Choose any median, e.g.:

```text
m = x[n/2]
```

Then:

```math
minimumCost
=
\sum_i |x_i-m|
```

For even `n`, every point in the median interval produces the same minimum.

---

## 5.11 ASCII Number-Line Proof

```text
points:

----●--------●--------●-------------●----
    1        3        5             8

choose x:

before 3:
more weight/points on right
→ moving right helps

between 3 and 5:
same amount left/right
→ cost flat

after 5:
more points on left
→ moving right hurts
```

---

## 5.12 Algorithm

```text
1. Sort points.

2. leftMedian  = x[(n-1)/2]
   rightMedian = x[n/2]

3. One optimal location:
      rightMedian

4. Minimum cost:
      Σ |x[i]-rightMedian|

5. Number of optimal integer locations:
      rightMedian-leftMedian+1
```

---

## 5.13 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> x(n);

    for (auto& v : x)
        cin >> v;

    sort(x.begin(), x.end());

    long long leftMedian =
        x[(n - 1) / 2];

    long long rightMedian =
        x[n / 2];

    long long chosen =
        rightMedian;

    __int128 minimumCost = 0;

    for (long long v : x) {
        minimumCost +=
            llabs(v - chosen);
    }

    long long numberOfIntegerMinimizers =
        rightMedian
        -
        leftMedian
        +
        1;

    // Print values as required by the actual problem.
}
```

---

## 5.14 Complexity

```text
sorting:
O(N log N)

cost:
O(N)

total:
O(N log N)
```

If values are already sorted:

```text
O(N)
```

---

## 5.15 Recognition Model

When you see:

```text
choose one location
+
cost is sum of absolute distances
```

think:

```text
median
```

Also remember:

```text
even N
→ interval of minimizers
```

---

## 5.16 Don't-Memorize Model

Do not memorize:

```text
answer = middle element
```

Remember the slope:

```text
move x right by 1

cost change
=
#left - #right
```

The sign changes at the median.

That is why median appears.

---

# 6. Pattern 5 — Weighted Median

## 6.1 What the Variant Asks

Each point has:

```text
position x_i
weight k_i
```

Cost:

```math
F(x)
=
\sum_i k_i|x-x_i|
```

Example from the lecture form:

```text
x_i:
1, 3, 7

k_i:
1, 1, 3
```

---

## 6.2 Simplest Mental Model

Weight means:

```text
importance / multiplicity
```

A point:

```text
(x=7, weight=3)
```

behaves conceptually like:

```text
7,7,7
```

So:

```text
x = [1,3,7]
k = [1,1,3]
```

becomes conceptually:

```text
[1,3,7,7,7]
```

Median:

```text
7
```

Therefore weighted median:

```text
7
```

---

## 6.3 Verify With Actual Cost

At:

```text
x=3
```

```text
1×|3-1|
+
1×|3-3|
+
3×|3-7|
```

```text
= 1×2 + 1×0 + 3×4
```

```text
= 2+0+12
```

```text
= 14
```

At:

```text
x=7
```

```text
1×|7-1|
+
1×|7-3|
+
3×|7-7|
```

```text
= 6+4+0
```

```text
= 10
```

So heavier point `7` pulls the optimum to the right.

---

## 6.4 Weighted Slope Derivation

Let:

```text
W_L
=
total weight left of x

W_R
=
total weight right of x
```

Move `x` one unit right.

Left-weight distances increase by:

```text
W_L
```

Right-weight distances decrease by:

```text
W_R
```

Therefore:

```math
F(x+1)-F(x)
=
W_L-W_R
```

This is the same proof as ordinary median, with:

```text
number of points
```

replaced by:

```text
total weight
```

---

## 6.5 Weighted Median Condition

Total weight:

```math
W
=
\sum_i k_i
```

For positive integer weights, one convenient weighted median is:

```text
first position where cumulative weight
reaches at least ceil(W/2)
```

---

## 6.6 Inline Prefix Example

```text
positions:
1,3,7

weights:
1,1,3
```

Total:

```text
W
= 1+1+3
= 5
```

Need at least:

```text
ceil(5/2)
= 3
```

Prefix weights:

```text
at 1:
1

at 3:
2

at 7:
5
```

First prefix reaching `3`:

```text
7
```

So:

```text
weighted median = 7
```

---

## 6.7 ASCII Visualization

```text
position:
----1----------3----------------7----

weight:
    *          *               ***

conceptual copies:

[1, 3, 7, 7, 7]
       ^
      median = 7
```

---

## 6.8 Why We Do Not Actually Expand

Weights may be huge:

```text
k_i = 10^9
```

Expanding into a billion copies is impossible.

Instead:

```text
sort (x_i,k_i)
→ prefix sum of k_i
→ find half-total crossing
```

---

## 6.9 Algorithm

```text
1. Sort pairs by position x.

2. totalWeight = Σ k_i.

3. target = ceil(totalWeight/2).

4. Walk left to right:
      prefix += k_i

5. First x_i with:
      prefix >= target
   is a weighted median.

6. Compute:
      Σ k_i * |x_i-median|
```

---

## 6.10 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<pair<long long,long long>> p(n);

    for (auto& [x, weight] : p) {
        cin >> x >> weight;
    }

    sort(p.begin(), p.end());

    long long totalWeight = 0;

    for (auto [x, weight] : p) {
        totalWeight += weight;
    }

    long long target =
        (totalWeight + 1) / 2;

    long long prefix = 0;
    long long median = 0;

    for (auto [x, weight] : p) {
        prefix += weight;

        if (prefix >= target) {
            median = x;
            break;
        }
    }

    __int128 cost = 0;

    for (auto [x, weight] : p) {
        cost +=
            (__int128)weight
            *
            llabs(x - median);
    }

    // Print as required.
}
```

---

## 6.11 Complexity

```text
sorting:
O(N log N)

prefix:
O(N)

cost:
O(N)

total:
O(N log N)
```

---

## 6.12 Recognition Model

When you see:

```text
Σ weight_i × |x-x_i|
```

think:

```text
weighted median
```

Mental conversion:

```text
weight k
≈
k conceptual copies
```

---

## 6.13 Don't-Memorize Model

Ordinary median:

```text
each point votes once
```

Weighted median:

```text
point i votes k_i times
```

The optimum is where approximately half of total voting weight lies on each side.

---

# 7. Pattern 6 — Manhattan Meeting Point in 2D

## 7.1 What the Model Asks

There are points in 2D:

```text
P_i = (x_i,y_i)
```

Choose one meeting point:

```text
P = (x,y)
```

Minimize total Manhattan distance:

```math
\sum_i
(
|x-x_i|
+
|y-y_i|
)
```

The lecture board also asks the natural extensions:

```text
1. Where should we meet?
2. What is the minimum total distance?
3. How many optimal locations exist?
```

---

## 7.2 Key Separation

Start:

```math
F(x,y)
=
\sum_i
(
|x-x_i|
+
|y-y_i|
)
```

Distribute the sum:

```math
F(x,y)
=
\sum_i |x-x_i|
+
\sum_i |y-y_i|
```

Define:

```math
F_x(x)
=
\sum_i |x-x_i|
```

and:

```math
F_y(y)
=
\sum_i |y-y_i|
```

Then:

```math
F(x,y)
=
F_x(x)+F_y(y)
```

So:

```text
x and y are independent
```

Therefore:

```text
optimal x = median of all x-coordinates

optimal y = median of all y-coordinates
```

---

## 7.3 Tiny Example

Points:

```text
A = (1,1)
B = (3,4)
C = (7,2)
```

x-coordinates:

```text
[1,3,7]
```

Median x:

```text
3
```

y-coordinates:

```text
[1,4,2]
```

Sorted:

```text
[1,2,4]
```

Median y:

```text
2
```

Optimal meeting point:

```text
(3,2)
```

---

## 7.4 Verify the Example

Distance from `(3,2)` to A `(1,1)`:

```text
|3-1| + |2-1|

= 2+1

= 3
```

To B `(3,4)`:

```text
|3-3| + |2-4|

= 0+2

= 2
```

To C `(7,2)`:

```text
|3-7| + |2-2|

= 4+0

= 4
```

Total:

```text
3+2+4
= 9
```

The reason this point is optimal is not trial-and-error:

```text
x=3 minimizes all horizontal distance
y=2 minimizes all vertical distance
```

independently.

---

## 7.5 ASCII City-Block Visualization

```text
y
^
|
|          B(3,4)
|          *
|
|  meeting *
|    (3,2)              C(7,2)
|                       *
|
| A(1,1)
| *
+---------------------------------> x
```

Manhattan movement:

```text
horizontal distance
+
vertical distance
```

No diagonal shortcut is used.

---

## 7.6 Even Number of Points — Rectangle of Optima

Suppose x-coordinates:

```text
[1,3,5,8]
```

Optimal x interval:

```text
[3,5]
```

Suppose y-coordinates:

```text
[2,4,7,9]
```

Optimal y interval:

```text
[4,7]
```

Then every point inside:

```text
x in [3,5]
y in [4,7]
```

is optimal.

ASCII:

```text
y
^
|
|          +-------------+
|          | optimal     |
|          | rectangle   |
|          +-------------+
|
+------------------------------> x
           3           5

vertical range:
4 ... 7
```

---

## 7.7 Number of Optimal Integer Meeting Points

For x:

```text
xL = lower median
xR = upper median
```

Number of integer optimal x-values:

```math
xR-xL+1
```

For y:

```text
yL = lower median
yR = upper median
```

Number of integer optimal y-values:

```math
yR-yL+1
```

Since choices are independent:

```math
numberOfOptimalPoints
=
(xR-xL+1)
(yR-yL+1)
```

---

## 7.8 Inline Counting Example

x interval:

```text
[3,5]
```

Integer x:

```text
3,4,5
```

Count:

```text
5-3+1
= 3
```

y interval:

```text
[4,7]
```

Integer y:

```text
4,5,6,7
```

Count:

```text
7-4+1
= 4
```

Total optimal integer meeting points:

```text
3×4
= 12
```

---

## 7.9 Minimum Manhattan Sum

Choose any optimal medians:

```text
mx
my
```

Then:

```math
minimum
=
\sum_i |x_i-mx|
+
\sum_i |y_i-my|
```

No 2D search is required.

This is the key modeling simplification.

---

## 7.10 ASCII Derivation

```text
2D problem:

Σ ( |x-x_i| + |y-y_i| )

          |
          v

split dimensions

Σ |x-x_i|    +    Σ |y-y_i|

     |                    |
     v                    v

1D median problem     1D median problem

     |                    |
     +---------+----------+
               |
               v

        combine (x,y)
```

---

## 7.11 Algorithm

```text
1. Store all x-coordinates.
2. Store all y-coordinates.

3. Sort x.
4. Sort y.

5. Find lower/upper median interval
   independently for x and y.

6. One optimal point:
      (x[n/2], y[n/2])

7. Minimum distance:
      Σ|x_i-mx|
      +
      Σ|y_i-my|

8. Number of integer optima:
      (xR-xL+1)
      *
      (yR-yL+1)
```

---

## 7.12 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> xs(n);
    vector<long long> ys(n);

    for (int i = 0; i < n; ++i) {
        cin >> xs[i] >> ys[i];
    }

    sort(xs.begin(), xs.end());
    sort(ys.begin(), ys.end());

    long long xL =
        xs[(n - 1) / 2];

    long long xR =
        xs[n / 2];

    long long yL =
        ys[(n - 1) / 2];

    long long yR =
        ys[n / 2];

    long long mx = xR;
    long long my = yR;

    __int128 minimumDistance = 0;

    for (int i = 0; i < n; ++i) {
        minimumDistance +=
            llabs(xs[i] - mx);

        minimumDistance +=
            llabs(ys[i] - my);
    }

    __int128 numberOfIntegerOptima =
        (__int128)(xR - xL + 1)
        *
        (yR - yL + 1);

    // Print values according to the actual problem.
}
```

---

## 7.13 Complexity

```text
sorting:
O(N log N)

distance sum:
O(N)

total:
O(N log N)
```

---

## 7.14 Recognition Model

When you see:

```text
points in grid
+
Manhattan distance
+
choose one meeting location
```

think:

```text
separate dimensions
```

Then:

```text
x → median
y → median
```

For even count:

```text
median intervals
→ rectangle of optimal locations
```

---

## 7.15 Don't-Memorize Model

Do not memorize:

```text
take median x and median y
```

Derive:

```text
Σ(
  |x-x_i|
  +
  |y-y_i|
)

=
Σ|x-x_i|
+
Σ|y-y_i|
```

Once separated, both are ordinary 1D median problems.

---

# 8. Pattern Comparison

| Pattern | Main Signal | Greedy / Math Rule | Proof Style |
|---|---|---|---|
| Minimum Dot Product | rearrange two arrays, minimize product sum | opposite sorting order | exchange / swapping |
| Score–Decay–Time | order jobs with decay and duration | sort `d/t` descending | two-job exchange |
| `+1`, `×2` Operations | reach huge target in min steps | solve backward by parity | forced last operation |
| 1D Absolute Distance | minimize `Σ|x-x_i|` | median | slope / convexity |
| Weighted Absolute Distance | minimize `Σk_i|x-x_i|` | weighted median | weighted slope |
| 2D Manhattan | minimize total city-block distance | median independently in x,y | separability |

---

# 9. Final Recognition Checklist

When reading a new problem, ask:

```text
1. Can I reorder elements?

2. Is the objective:
      Σ a_i*b_i ?
   → rearrangement / swap proof.

3. Is it MAX or MIN?
   MAX → same order
   MIN → opposite order.

4. Is there a scheduling order
   with time + penalty/decay?
   → compare two jobs.

5. Did algebra produce a ratio?
   → use cross multiplication.

6. Do forward operations feel ambiguous?
   → reverse the process.

7. Does parity make the previous move forced?

8. Does ×2 correspond to a binary shift?

9. Is objective:
      Σ|x-x_i| ?
   → median.

10. Is objective:
      Σk_i|x-x_i| ?
    → weighted median.

11. Is distance Manhattan in 2D?
    → split x and y.

12. Is n even?
    → there may be an interval/rectangle
       of optimal answers, not one point.
```

---

# 10. Compact Revision Card

```text
ALGOZENITH GREEDY TECHNIQUES — CLASS 1
=======================================


1. MIN DOT PRODUCT
------------------
goal:
min Σ ai*bi

sort:
A ascending
B descending

proof for:
a1 <= a2
b1 <= b2

same:
a1b1 + a2b2

cross:
a1b2 + a2b1

same-cross
=
(a2-a1)(b2-b1)
>= 0

therefore:
cross <= same

MIN:
opposite order

MAX:
same order


2. SCORE–DECAY–TIME
-------------------
job i:
S_i = score
d_i = decay
t_i = duration

finish at C_i:
score = S_i-d_i*C_i

compare P1→P2 vs P2→P1

P1 first is better iff:

d1*t2 >= d2*t1

therefore:

d1/t1 >= d2/t2

sort:
d/t descending

code:
cross multiply


3. +1 AND ×2 OPERATIONS
-----------------------
start:
x=0

operations:
x=x+1
x=2x

reverse from y:

odd:
y--
because odd cannot come from doubling

even:
y/=2

binary:

×2 = left shift

minimum steps:
(bitLength-1)
+
popcount(y)

for y>0


4. MEDIAN
---------
minimize:

Σ|x-x_i|

move x right:

cost change
=
#left - #right

minimum:
median

even n:
all x between
two middle values are optimal

integer solution count:

rightMedian
-
leftMedian
+
1


5. WEIGHTED MEDIAN
------------------
minimize:

Σ k_i|x-x_i|

weight k_i
=
k_i conceptual copies

sort by x

first cumulative weight
reaching half total
→ weighted median


6. MANHATTAN 2D
---------------
minimize:

Σ(
 |x-x_i|
 +
 |y-y_i|
)

separate:

Σ|x-x_i|
+
Σ|y-y_i|

optimal:
x = median of x's
y = median of y's

even n:
optimal rectangle

integer answer count:

(xR-xL+1)
×
(yR-yL+1)
```

---

# Final Mental Model

```text
                 GREEDY TECHNIQUES
                        |
      +-----------------+-----------------+
      |                 |                 |
   REORDER           OPERATIONS         LOCATION
      |                 |                 |
      v                 v                 v
 exchange proof     reverse process      |x-a|
      |                 |                 |
      v                 v                 v
sort / ratio      forced parity step    median
      |                                   |
      v                                   v
dot product / jobs                 weighted / Manhattan
```

> **Core lesson:** first identify the structure behind the story.  
> If the problem is about **ordering**, compare two items.  
> If it is about **operations**, try reversing them.  
> If it is about **absolute distance**, look for a median.  
> The greedy rule should come from the proof—not from memorizing a template.
