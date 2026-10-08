# AlgoZenith Greedy — Class 1
## Exchange Proofs, Ratio Scheduling, Median, Weighted Median, and Bottleneck × Top-K

> **Goal:** convert the class-board derivations into a self-study note that explains **where every greedy rule comes from**.
>
> **Study flow used for every pattern:**
>
> ```text
> prerequisites
> → what the problem/model asks
> → variables
> → tiny numerical example
> → greedy observation
> → exchange / algebraic proof
> → every equation mapped to numbers
> → ASCII visualization
> → algorithm
> → C++17
> → complexity
> → edge cases
> → recognition model
> → don't-memorize model
> ```
>
> The class covers four important greedy forms:
>
> ```text
> 1. Rearrangement / Maximum Dot Product
>    → sort both sequences in the same order
>    → swapping / exchange proof
>
> 2. Score–Decay–Time Scheduling
>    → order by decreasing D/T
>    → two-job exchange proof
>
> 3. Median / Weighted Median
>    → minimize sum of absolute distances
>
> 4. Team Performance
>    → additive sum × bottleneck minimum
>    → sort bottleneck descending + keep top-K additive values
> ```

---

# Clickable Table of Contents

- [0. How to Use This Note](#0-how-to-use-this-note)
- [1. Shared Prerequisites](#1-shared-prerequisites)
  - [1.1 What Greedy Actually Needs](#11-what-greedy-actually-needs)
  - [1.2 Exchange / Swapping Proof](#12-exchange--swapping-proof)
  - [1.3 Local Pair Comparison](#13-local-pair-comparison)
  - [1.4 Sorting and Inversions](#14-sorting-and-inversions)
  - [1.5 Cross Multiplication Instead of Floating Point](#15-cross-multiplication-instead-of-floating-point)
  - [1.6 Completion Time](#16-completion-time)
  - [1.7 Absolute Distance](#17-absolute-distance)
  - [1.8 Median](#18-median)
  - [1.9 Weighted Median](#19-weighted-median)
  - [1.10 Min-Heap for Top-K](#110-min-heap-for-top-k)
  - [1.11 Bottleneck × Additive-Sum Objectives](#111-bottleneck--additive-sum-objectives)
  - [1.12 Overflow and `__int128`](#112-overflow-and-__int128)
  - [1.13 Universal Greedy Proof Checklist](#113-universal-greedy-proof-checklist)
- [2. Pattern 1 — Maximum Dot Product / Rearrangement Inequality](#2-pattern-1--maximum-dot-product--rearrangement-inequality)
- [3. Pattern 2 — Score–Decay–Time Job Ordering](#3-pattern-2--scoredecaytime-job-ordering)
- [4. Pattern 3 — Median Minimizes Absolute Distance](#4-pattern-3--median-minimizes-absolute-distance)
- [5. Pattern 4 — Weighted Median](#5-pattern-4--weighted-median)
- [6. Pattern 5 — Team Performance: Sum × Minimum](#6-pattern-5--team-performance-sum--minimum)
- [7. Pattern Comparison](#7-pattern-comparison)
- [8. Final Recognition Checklist](#8-final-recognition-checklist)
- [9. Compact Revision Card](#9-compact-revision-card)

---

# 0. How to Use This Note

For each pattern:

```text
1. Understand the objective.
2. Identify what may be reordered / selected.
3. Try a 2-item example.
4. Compare:
      greedy local arrangement
      vs
      swapped arrangement.
5. Cancel everything that is unchanged.
6. Factor the remaining expression.
7. Use sign reasoning.
8. Only then memorize the sorting rule / data structure.
```

The recurring theme is:

```text
Global optimization
        |
        v
compare only two local choices
        |
        v
prove one local order is never worse
        |
        v
remove inversions / repeat exchange
        |
        v
global optimum
```

---

# 1. Shared Prerequisites

## 1.1 What Greedy Actually Needs

A greedy idea is not:

```text
"this feels best now"
```

A useful greedy solution needs:

```text
CHOICE
+
PROOF
```

Typical proof forms in this class:

```text
Exchange / swapping proof
Ratio comparison
Convex / median argument
Bottleneck-threshold argument
```

---

## 1.2 Exchange / Swapping Proof

### Worked example — why a swap can prove a greedy rule

Suppose `A=[2,5]`, with assigned partners `B=[7,3]`.

```text
Before (crossed): 2×7 + 5×3 = 14+15 = 29
After  (aligned): 2×3 + 5×7 =  6+35 = 41
Improvement: 41-29 = 12

Algebra: (5-2)×(7-3) = 3×4 = 12 >= 0
```

**Proof logic:** find a wrongly ordered pair → exchange it → show the objective does not worsen → repeat until no wrongly ordered pairs remain. A worked example reveals the idea; the algebra proves it for every valid pair.


Suppose an optimal solution does not have the order we want.

Find a local bad pair:

```text
... X ... Y ...
```

Swap only those two:

```text
... Y ... X ...
```

Then prove:

```text
new objective >= old objective
```

for maximization,

or:

```text
new objective <= old objective
```

for minimization.

If the swap never hurts, we can repeatedly remove bad pairs until the structure becomes greedy.

ASCII:

```text
OPT has inversion

... [bad pair] ...
        |
        v
      swap
        |
        v
objective non-worse
        |
        v
one inversion removed
        |
        v
repeat
        |
        v
greedy order exists among optimal solutions
```

---

## 1.3 Local Pair Comparison

**Idea:** if we swap only two pairings, every unaffected contribution is identical. Compare the two changed contributions, not the entire sum.

### Numerical example — 4 positions

We have fixed `A = [1, 2, 5, 8]`. We can rearrange partners in `B` to maximize `sum(A[i] * B[i])`.

```text
Position       1       2       3       4
A              1       2       5       8
B (OLD)        3       7       4       6
               |       |       |       |
Products       3      14      20      48

OLD total = 3 + 14 + 20 + 48 = 85
```

Only swap the partners `7` and `4` at positions `i=2` and `j=3`:

```text
Position       1       2       3       4
A              1       2       5       8
B (NEW)        3       4       7       6
Products       3       8      35      48

NEW total = 3 +  8 + 35 + 48 = 94
```

| Contribution | OLD | NEW | Why? |
|---|---:|---:|---|
| Position 1 | `1*3 = 3` | `1*3 = 3` | Unchanged |
| Position 2 | `2*7 = 14` | `2*4 = 8` | Swapped |
| Position 3 | `5*4 = 20` | `5*7 = 35` | Swapped |
| Position 4 | `8*6 = 48` | `8*6 = 48` | Unchanged |

**Compare only the changed positions:**

```text
OLD pair = 2*7 + 5*4 = 14 + 20 = 34
NEW pair = 2*4 + 5*7 =  8 + 35 = 43

NEW - OLD = 43 - 34 = +9
Full totals: 94 - 85 = +9  (same result!)
```

### Why the other terms cancel — step by step

```text
NEW - OLD
= (3 + 8 + 35 + 48) - (3 + 14 + 20 + 48)
= (3-3) + (8-14) + (35-20) + (48-48)
= 0 - 6 + 15 + 0
= +9
```

Generalize with `a_i <= a_j` and `b_i <= b_j`:

```text
Aligned:  ai*bi + aj*bj
Crossed:  ai*bj + aj*bi

Aligned - Crossed
= ai*bi + aj*bj - ai*bj - aj*bi
= aj*(bj-bi) - ai*(bj-bi)
= (aj-ai)*(bj-bi) >= 0
```

**Map formula to numbers:** `(5-2)*(7-4) = 3*3 = 9`.

**Recognition:** *If only two assignments change, cancel the unaffected terms. For a sum of independent pair contributions, the entire comparison reduces to those two positions.* For scheduling, use **adjacent** swaps so completion times of later jobs remain unchanged (see §1.6).

---

## 1.4 Sorting and Inversions

### Worked example — remove inversions

```text
B before: [1, 7, 4, 9]  (7 > 4 is an inversion)
B after:  [1, 4, 7, 9]  (adjacent 7,4 swapped)
A fixed:  [1, 2, 5, 8]

Before dot product = 1×1 + 2×7 + 5×4 + 8×9 = 107
After dot product  = 1×1 + 2×4 + 5×7 + 8×9 = 116
Gain = (5-2)×(7-4) = 9
```

A single adjacent inversion swap fixes the local ordering without hurting the objective. Repeating this gives sorted pairing.


For an ascending sequence:

```text
x1 <= x2 <= x3 <= ...
```

an inversion is a pair:

```text
i < j
but
x[i] > x[j]
```

Example:

```text
[1, 7, 4, 9]

7 > 4
```

so `(7,4)` is an inversion.

Many greedy proofs show:

```text
if an inversion exists,
swap it without hurting the answer
```

Therefore:

```text
an optimal solution can be sorted
```

---

## 1.5 Cross Multiplication Instead of Floating Point

Suppose we want descending order by:

```text
D / T
```

Do not compare using:

```cpp
(double)D / T
```

when integers may be large.

Instead compare:

```math
\frac{D_1}{T_1}
\ge
\frac{D_2}{T_2}
```

For positive `T1,T2`, cross multiply:

```math
D_1T_2
\ge
D_2T_1
```

### Example

```text
D1 = 20, T1 = 5
D2 = 50, T2 = 3
```

Ratios:

```text
20/5 = 4
50/3 ≈ 16.67
```

Cross multiplication:

```text
20×3 = 60
50×5 = 250

60 < 250
```

So job 2 has the larger `D/T`.

No floating-point precision issue.

---

## 1.6 Completion Time

**Meaning:** a job's completion time is the clock time when that job finishes, including time spent waiting for all earlier jobs.

### Numerical example — two jobs

```text
P1 takes T1 = 5 minutes
P2 takes T2 = 3 minutes
Both start from clock time 0; only one runs at a time.
```

**Order A: P1 then P2**

```text
Clock:  0                 5          8
        |------ P1 -------|--- P2 ---|
        start          P1 finishes  P2 finishes

C1 = T1      = 5
C2 = T1 + T2 = 5 + 3 = 8
```

**Order B: P2 then P1**

```text
Clock:  0         3                       8
        |--- P2 --|---------- P1 ---------|
        start  P2 finishes            P1 finishes

C2 = T2      = 3
C1 = T2 + T1 = 3 + 5 = 8
```

| Job | Duration | Finish in P1 → P2 | Finish in P2 → P1 |
|---|---:|---:|---:|
| P1 | 5 | `C1=5` | `C1=8` |
| P2 | 3 | `C2=8` | `C2=3` |

**Important:** total duration is always `5+3=8`, but *who finishes early* changes. This matters when score decreases according to completion time.

### Add a concrete score-decay example

Suppose each job starts with a base score of `100`:

```text
P1 loses D1 = 2 points per minute until it finishes.
P2 loses D2 = 10 points per minute until it finishes.

score(job) = 100 - D * completion_time
```

| Order | P1 score | P2 score | Total |
|---|---|---|---:|
| P1 → P2 | `100 - 2*5 = 90` | `100 - 10*8 = 20` | **110** |
| P2 → P1 | `100 - 2*8 = 84` | `100 - 10*3 = 70` | **154** |

So `P2 → P1` is better by `154-110 = 44`: the fast-decaying job P2 finishes sooner.

### Local comparison gives the scheduling rule

For adjacent jobs P1 and P2 beginning at time `0`:

```text
Score(P1 → P2) = (S1 - D1*T1) + (S2 - D2*(T1+T2))
Score(P2 → P1) = (S2 - D2*T2) + (S1 - D1*(T2+T1))

Score(P1 → P2) - Score(P2 → P1)
= D1*T2 - D2*T1
= 2*3 - 10*5
= 6 - 50 = -44
```

Negative means **P2 first** is better. Equivalently:

```text
P1 first is at least as good iff D1*T2 >= D2*T1
For T1,T2 > 0:                D1/T1 >= D2/T2

Here: 2/5 = 0.4 < 10/3 = 3.33 → P2 first.
```

**Why compare only two adjacent jobs?** Jobs before this pair are already finished, and jobs after the pair see the same total time `T1+T2=8` regardless of order. Only P1's and P2's completion times change. This is the exchange-proof argument used in Pattern 2.

---

## 1.7 Absolute Distance

### Worked example — absolute value means nonnegative distance

```text
Point at 2, meeting at 6: |6-2| = 4
Point at 9, meeting at 6: |6-9| = 3
Total distance = 4+3 = 7

Number line:
2 ----4---- 6 ---3--- 9
             ^ meeting
```

Unlike signed subtraction, `|x-a|` measures distance no matter which side `a` lies on.


Distance on a number line:

```math
|x-a|
```

Example:

```text
x = 5
a = 2

|5-2|
= 3
```

The objective:

```math
\sum_i |x-x_i|
```

means:

```text
choose one location x
that minimizes total distance
to all points
```

---

## 1.8 Median

### Worked example — median beats the average for total absolute distance

```text
Locations = [1, 2, 10]
Candidate x=2  -> |2-1|+|2-2|+|2-10| = 1+0+8 = 9
Candidate x=4  -> |4-1|+|4-2|+|4-10| = 3+2+6 = 11
Candidate x=10 -> |10-1|+|10-2|+0     = 9+8+0 = 17
```

The **median is 2**. Moving right from 2 lengthens the distances to two left-side points but shortens the distance to only one right-side point; the total increases.


For sorted values:

```text
x1 <= x2 <= ... <= xn
```

a median minimizes:

```math
\sum_i |x-x_i|
```

Odd count:

```text
[1,3,7]

median = 3
```

Even count:

```text
[1,3,7,10]
```

Any:

```text
x in [3,7]
```

minimizes the continuous objective.

For an integer answer, any integer in that interval is optimal.

---

## 1.9 Weighted Median

### Worked example — one heavily weighted location changes the answer

```text
Locations x:  [1, 3, 7]
Weights   w:  [1, 1, 3]
Expanded conceptual positions: [1, 3, 7, 7, 7]

Meet at 3: 1×|3-1| + 1×|3-3| + 3×|3-7| = 2+0+12 = 14
Meet at 7: 1×|7-1| + 1×|7-3| + 3×|7-7| = 6+4+0 = 10

Total weight = 5; half-weight crossing occurs at 7.
```

Do **not** expand the positions in code: sort pairs `(x,w)` and scan cumulative weights.


If point `x_i` has weight:

```text
k_i
```

the objective is:

```math
\sum_i k_i|x-x_i|
```

Think conceptually:

```text
x_i is repeated k_i times
```

Example:

```text
x = [1,3,7]
k = [1,1,3]
```

Expanded conceptual list:

```text
[1,3,7,7,7]
```

Median:

```text
7
```

So weighted median is:

```text
the point where cumulative weight
reaches at least half of total weight
```

Do not actually expand when weights are large.

---

## 1.10 Min-Heap for Top-K

### Worked example — keep the three largest seen so far

| Incoming | Heap contents (shown sorted for clarity) | Sum | Action |
|---|---|---:|---|
| `5` | `[5]` | 5 | Push |
| `2` | `[2,5]` | 7 | Push |
| `9` | `[2,5,9]` | 16 | Push |
| `7` | `[5,7,9]` | 21 | Push 7, pop smallest 2 |
| `1` | `[5,7,9]` | 21 | Push 1, pop smallest 1 |

```text
K=3; seen: 5,2,9,7,1
Largest three: 9,7,5 -> sum=21
Min-heap top = 5 (easiest item to discard)
```

The table lists heap elements in sorted order *for explanation*; a binary heap's internal array is not necessarily sorted.


To maintain the largest `K` values seen so far:

```text
use a min-heap of size at most K
```

Why min-heap?

Because when we have `K+1` candidates:

```text
remove the smallest one
```

C++:

```cpp
priority_queue<
    long long,
    vector<long long>,
    greater<long long>
> pq;
```

Pattern:

```cpp
pq.push(x);
sum += x;

if ((int)pq.size() > K) {
    sum -= pq.top();
    pq.pop();
}
```

Afterward:

```text
heap contains the K largest values
seen in the processed prefix
```

---

## 1.11 Bottleneck × Additive-Sum Objectives

### Worked example — threshold first, top-K second

```text
People: (speed, efficiency)
A=(6,4), B=(4,10), C=(2,20); choose K=2

Threshold speed >= 6: [A]       -> cannot form team
Threshold speed >= 4: [A,B]     -> sum=4+10=14; candidate=4×14=56
Threshold speed >= 2: [A,B,C]   -> best efficiencies 20,10
                                  sum=30; candidate=2×30=60

Best team: B,C -> minimum speed=2, efficiency sum=30, score=60
```

Why not take the two fastest? They score `56`, while the slightly slower bottleneck allows a much larger additive sum and scores `60`.


A common structure:

```math
score
=
(\text{sum of one attribute})
\times
(\text{minimum of another attribute})
```

Example:

```text
team score
=
(sum of efficiencies)
×
(minimum speed)
```

The difficulty:

```text
one part is additive
one part is a bottleneck
```

Greedy approach:

```text
fix / enumerate the bottleneck threshold
```

Then optimize the additive part among all items allowed by that threshold.

This is the core of the team problem.

---

## 1.12 Overflow and `__int128`

### Worked example — intermediate multiplication can overflow

```text
N=100000, each term = 10^9 × 10^9 = 10^18
Sum of N terms = 10^5 × 10^18 = 10^23

signed long long maximum ≈ 9.22 × 10^18
Therefore 10^23 does not fit.
```

Cast **before** multiplying, not after:

```cpp
__int128 safe = (__int128)a * b;
__int128 total = 0;
total += (__int128)a * b;  // safe product and accumulator
```


The class board shows values potentially around:

```text
10^9
```

Products:

```text
10^9 × 10^9
= 10^18
```

A single product is near `long long` range.

But a sum of up to `10^5` such products can be:

```text
10^23
```

which does **not** fit in signed 64-bit.

Likewise:

```text
topKSum × bottleneck
```

may exceed `long long`.

When constraints allow this, use:

```cpp
__int128
```

Helper for printing:

```cpp
void printInt128(__int128 x) {
    if (x == 0) {
        cout << 0;
        return;
    }

    if (x < 0) {
        cout << '-';
        x = -x;
    }

    string s;

    while (x > 0) {
        s.push_back('0' + x % 10);
        x /= 10;
    }

    reverse(s.begin(), s.end());
    cout << s;
}
```

---

## 1.13 Universal Greedy Proof Checklist

### Mini proof checklist applied to an example

```text
Objective: maximize 2×partner1 + 5×partner2
Bad pairing: [7,3] -> score=29
Proposed swap: [3,7] -> score=41
Difference: (5-2)×(7-3)=12 >= 0
Rule: align smaller with smaller and larger with larger
Global argument: repeatedly remove inversions
```

**Recognition sequence:** identify *what changes* → cancel *what does not* → factor the difference → check its sign → justify repeating exchanges.


Before coding, ask:

```text
1. What is being optimized?

2. What part can be sorted / reordered?

3. If two elements are in the "wrong" order,
   can I swap them?

4. When I compare before vs after,
   which terms cancel?

5. Can the remaining difference be factored?

6. What signs do the factors have?

7. Is the rule really a ratio?
   If yes, can I compare by cross multiplication?

8. Is the objective sum of absolute distances?
   If yes, think median.

9. Are distances weighted?
   If yes, think weighted median.

10. Is the score:
      additive sum × minimum bottleneck?
    If yes:
      sort by bottleneck
      + maintain top-K additive values.
```

---

# 2. Pattern 1 — Maximum Dot Product / Rearrangement Inequality

## 2.1 What the Model Asks

Given two arrays:

```text
A = [a1,a2,...,an]
B = [b1,b2,...,bn]
```

We may permute the pairing.

Goal:

```math
A\cdot B
=
\sum_{i=1}^{n} a_i b_i
```

maximize it.

The greedy claim:

```text
sort A and B in the SAME order
```

For example:

```text
ascending A
+
ascending B
```

maximizes the dot product.

Opposite order minimizes it.

---

## 2.2 Tiny Example First

```text
A = [1,2,3]
B = [1,2,3]
```

Same-order pairing:

```text
1×1 + 2×2 + 3×3

= 1 + 4 + 9

= 14
```

A crossed pairing:

```text
A = [1,2,3]
B = [1,3,2]
```

gives:

```text
1×1 + 2×3 + 3×2

= 1 + 6 + 6

= 13
```

So same order is better here.

We need a proof for every input.

---

## 2.3 Two-Pair Proof — Numbers First

Take:

```text
small A = 2
large A = 5

small B = 3
large B = 7
```

Same-order contribution:

```text
2×3 + 5×7

= 6 + 35

= 41
```

Crossed contribution:

```text
2×7 + 5×3

= 14 + 15

= 29
```

Difference:

```text
41-29
= 12
```

Now derive why this must be non-negative.

---

## 2.4 General Two-Pair Derivation

Assume:

```math
a_i\le a_j
```

and:

```math
b_i\le b_j
```

Same-order contribution:

```math
G
=
a_ib_i+a_jb_j
```

Crossed contribution:

```math
O
=
a_ib_j+a_jb_i
```

Want:

```math
G\ge O
```

Subtract:

```math
G-O
=
a_ib_i+a_jb_j-a_ib_j-a_jb_i
```

Group terms:

```math
G-O
=
a_jb_j-a_jb_i-a_ib_j+a_ib_i
```

Factor `a_j` from first pair:

```math
=
a_j(b_j-b_i)-a_i(b_j-b_i)
```

Factor common `(b_j-b_i)`:

```math
G-O
=
(a_j-a_i)(b_j-b_i)
```

Now:

```math
a_j-a_i\ge0
```

and:

```math
b_j-b_i\ge0
```

Therefore:

```math
G-O\ge0
```

Hence:

```math
G\ge O
```

---

## 2.5 Same Algebra With the Actual Numbers

Use:

```text
ai = 2
aj = 5
bi = 3
bj = 7
```

Start:

```text
G-O

= 2×3 + 5×7
  - 2×7 - 5×3
```

Evaluate:

```text
= 6 + 35 - 14 - 15

= 12
```

Factored formula:

```text
(aj-ai)(bj-bi)

= (5-2)(7-3)

= 3×4

= 12
```

Exactly the same difference.

---

## 2.6 Why This Proves the Whole Sorting Rule

Suppose `A` is sorted ascending.

If `B` is not sorted ascending, it contains an inversion:

```text
i < j
but
b_i > b_j
```

Because:

```text
a_i <= a_j
```

swap `b_i` and `b_j`.

The two-pair proof says this swap cannot decrease the dot product.

ASCII:

```text
before — crossed:

small A -------- large B
large A -------- small B

     \          /
      \        /
       crossing


after — aligned:

small A -------- small B
large A -------- large B
```

Every safe swap removes at least one inversion.

Repeat until:

```text
B is sorted in the same order as A
```

Therefore an optimal arrangement exists with both arrays sorted the same way.

---

## 2.7 Exchange Proof in "OPT" Language

Suppose OPT contains:

```text
(ai, bj)
(aj, bi)
```

with:

```text
ai <= aj
bi <= bj
```

OPT local contribution:

```math
a_ib_j+a_jb_i
```

Swap the `B` partners.

New contribution:

```math
a_ib_i+a_jb_j
```

Difference:

```math
(a_j-a_i)(b_j-b_i)\ge0
```

Therefore:

```text
swap does not hurt OPT
```

So an optimal solution can be transformed into sorted pairing.

---

## 2.8 Negative Numbers Still Work

Example:

```text
A = [-5,-1,4]
B = [-3,2,7]
```

Same-order pairing:

```text
(-5)(-3) + (-1)(2) + 4(7)

= 15 - 2 + 28

= 41
```

The pair proof did not assume the values themselves are positive.

It only used:

```text
aj-ai >= 0
bj-bi >= 0
```

So sorting same order still maximizes the dot product even with negative values.

---

## 2.9 ASCII Visualization

```text
A sorted:
a1 <= a2 <= a3 <= ... <= an

B sorted:
b1 <= b2 <= b3 <= ... <= bn

pair:

a1 ---- b1
a2 ---- b2
a3 ---- b3
...
an ---- bn

No crossing inversions.
```

---

## 2.10 Algorithm

```text
1. Sort A ascending.
2. Sort B ascending.
3. Compute:
      sum += A[i] * B[i].
```

For minimum dot product:

```text
sort one ascending
sort the other descending
```

---

## 2.11 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

void printInt128(__int128 x) {
    if (x == 0) {
        cout << 0;
        return;
    }

    if (x < 0) {
        cout << '-';
        x = -x;
    }

    string s;

    while (x > 0) {
        s.push_back(
            char('0' + x % 10)
        );
        x /= 10;
    }

    reverse(s.begin(), s.end());
    cout << s;
}

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
    sort(b.begin(), b.end());

    __int128 ans = 0;

    for (int i = 0; i < n; ++i) {
        ans += (__int128)a[i] * b[i];
    }

    printInt128(ans);
    cout << '\n';
}
```

---

## 2.12 Complexity

```text
sorting:
O(N log N)

dot product:
O(N)

total:
O(N log N)
```

---

## 2.13 Recognition Model

When you see:

```text
two sequences
+
you may rearrange pairing
+
objective is Σ ai*bi
```

think:

```text
rearrangement inequality
+
exchange proof
```

For maximum:

```text
same order
```

For minimum:

```text
opposite order
```

---

## 2.14 Don't-Memorize Model

Do not memorize only:

```text
sort both ascending
```

Remember the local fact:

```text
small×small + large×large
>=
small×large + large×small
```

because:

```text
difference
=
(largeA-smallA)
×
(largeB-smallB)
>= 0
```

That one equation generates the entire sorting rule.

---

# 3. Pattern 2 — Score–Decay–Time Job Ordering

## 3.1 Model From the Class

Each problem/job `i` has:

```text
S_i = base score
D_i = score decay per unit time
T_i = time needed
```

If job `i` finishes at time:

```text
C_i
```

its score is:

```math
S_i-D_iC_i
```

Total score:

```math
\sum_i (S_i-D_iC_i)
```

Goal:

```text
choose an order maximizing total score
```

---

## 3.2 First Simplification

Total:

```math
\sum_i S_i
-
\sum_i D_iC_i
```

The first part:

```text
Σ S_i
```

does not depend on the order.

Therefore maximizing score is equivalent to minimizing:

```math
\sum_i D_iC_i
```

So this is really a:

```text
weighted completion-time scheduling problem
```

where:

```text
D_i = importance / decay weight
T_i = processing time
```

---

## 3.3 Why Only Two Jobs Are Enough for the Proof

**Real-world model:** one computer processes four tasks, one at a time. `Q` and `R` stay in place; swap only the two neighboring tasks `P1` and `P2`.

| Task | Q | P1 | P2 | R |
|---|---:|---:|---:|---:|
| Duration (minutes) | 2 | 5 | 3 | 4 |

```text
OLD: 0 --Q-- 2 -----P1----- 7 ---P2--- 10 ----R---- 14
NEW: 0 --Q-- 2 ---P2--- 5 -----P1----- 10 ----R---- 14
                ^ swapped adjacent tasks ^
```

| Task | OLD finishes | NEW finishes | Changes? |
|---|---:|---:|---|
| Q | 2 | 2 | No |
| **P1** | **7** | **10** | **Yes** |
| **P2** | **10** | **5** | **Yes** |
| R | 14 | 14 | No |

**Observation:** `P1 + P2` always takes `5 + 3 = 8` minutes, whichever comes first. So `R` still starts at minute `10` and ends at `14`; `Q` is untouched. **Only compare the score/penalty of P1 and P2.**

**Exchange-proof rule:** swap *adjacent* jobs → everything outside the pair has the same completion time → compare just the pair → derive the better order (`D/T`, in this problem).

---

## 3.4 Order `P1 → P2`

Completion times:

```text
C1 = T1
C2 = T1+T2
```

Score:

```math
G
=
(S_1-T_1D_1)
+
(S_2-(T_1+T_2)D_2)
```

---

## 3.5 Order `P2 → P1`

Completion times:

```text
C2 = T2
C1 = T2+T1
```

Score:

```math
O
=
(S_2-T_2D_2)
+
(S_1-(T_2+T_1)D_1)
```

We want to know when:

```math
G\ge O
```

meaning:

```text
P1 before P2 is at least as good
```

---

## 3.6 Algebra Derivation — Every Step

Start:

```math
(S_1-T_1D_1)
+
(S_2-(T_1+T_2)D_2)
\ge
(S_2-T_2D_2)
+
(S_1-(T_1+T_2)D_1)
```

Expand both sides.

Left:

```math
S_1+S_2
-
T_1D_1
-
T_1D_2
-
T_2D_2
```

Right:

```math
S_1+S_2
-
T_2D_2
-
T_1D_1
-
T_2D_1
```

Cancel common terms:

```text
S1
S2
-T1D1
-T2D2
```

Remain:

```math
-T_1D_2
\ge
-T_2D_1
```

Multiply by `-1`, reversing inequality:

```math
T_1D_2
\le
T_2D_1
```

Divide by positive `T1T2`:

```math
\frac{D_1}{T_1}
\ge
\frac{D_2}{T_2}
```

Therefore:

```text
P1 should come before P2
when D1/T1 >= D2/T2
```

So sort:

```text
D_i / T_i
```

in descending order.

---

## 3.7 Inline Numerical Example From the Board Style

Take:

```text
P1:
S1 = 100
D1 = 20
T1 = 5

P2:
S2 = 100
D2 = 50
T2 = 3
```

Ratios:

```text
D1/T1
= 20/5
= 4

D2/T2
= 50/3
≈ 16.67
```

So greedy says:

```text
P2 first
```

Let's verify directly.

---

## 3.8 Compute `P1 → P2`

P1 finishes at:

```text
5
```

P1 score:

```text
100 - 20×5

= 100 - 100

= 0
```

P2 finishes at:

```text
5+3
= 8
```

P2 score:

```text
100 - 50×8

= 100 - 400

= -300
```

Total:

```text
-300
```

---

## 3.9 Compute `P2 → P1`

P2 finishes at:

```text
3
```

Score:

```text
100 - 50×3

= 100 - 150

= -50
```

P1 finishes at:

```text
3+5
= 8
```

Score:

```text
100 - 20×8

= 100 - 160

= -60
```

Total:

```text
-110
```

Compare:

```text
-110 > -300
```

So `P2 → P1` is better.

Exactly as the ratio rule predicted.

---

## 3.10 Exchange Proof for the Whole Schedule

Suppose a schedule contains adjacent jobs:

```text
P1, P2
```

but:

```math
\frac{D_1}{T_1}
<
\frac{D_2}{T_2}
```

Then the pair is in the wrong order.

Swap them:

```text
P2, P1
```

The two-job derivation shows:

```text
total score does not decrease
```

Repeatedly swap all ratio inversions.

Eventually the schedule is sorted by:

```text
D/T descending
```

Therefore an optimal schedule exists in that order.

---

## 3.11 Avoid Division in the Comparator

Want:

```math
\frac{D_a}{T_a}
>
\frac{D_b}{T_b}
```

For positive times:

```math
D_aT_b
>
D_bT_a
```

C++:

```cpp
bool cmp(const Job& a, const Job& b) {
    return (__int128)a.d * b.t
         > (__int128)b.d * a.t;
}
```

If equal ratio:

```text
either order gives the same pair contribution
```

So any deterministic tie-break is okay.

---

## 3.12 ASCII Visualization

```text
High decay / short time
should move LEFT.

Low decay / long time
can wait longer.

ratio:
D/T

larger ratio --------------------> earlier
```

Pair view:

```text
P1 before P2 is better iff:

D1/T1 >= D2/T2
```

---

## 3.13 Algorithm

```text
1. Read jobs:
      S, D, T.

2. Sort descending by D/T
   using cross multiplication.

3. timeTaken = 0
   answer = 0

4. For every job in order:
      timeTaken += T
      answer += S - D*timeTaken
```

---

## 3.14 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Job {
    long long s;
    long long d;
    long long t;
};

bool cmp(const Job& a, const Job& b) {
    __int128 left =
        (__int128)a.d * b.t;

    __int128 right =
        (__int128)b.d * a.t;

    if (left != right)
        return left > right;

    return a.t < b.t; // arbitrary stable tie-break
}

void printInt128(__int128 x) {
    if (x == 0) {
        cout << 0;
        return;
    }

    if (x < 0) {
        cout << '-';
        x = -x;
    }

    string s;

    while (x > 0) {
        s.push_back(
            char('0' + x % 10)
        );
        x /= 10;
    }

    reverse(s.begin(), s.end());
    cout << s;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<Job> jobs(n);

    for (auto& job : jobs) {
        cin >> job.s
            >> job.d
            >> job.t;
    }

    sort(
        jobs.begin(),
        jobs.end(),
        cmp
    );

    long long timeTaken = 0;
    __int128 answer = 0;

    for (const Job& job : jobs) {
        timeTaken += job.t;

        answer +=
            (__int128)job.s
            -
            (__int128)job.d
            * timeTaken;
    }

    printInt128(answer);
    cout << '\n';
}
```

---

## 3.15 Complexity

```text
sorting:
O(N log N)

score computation:
O(N)

total:
O(N log N)
```

---

## 3.16 Recognition Model

When you see:

```text
jobs
+
processing time T_i
+
penalty/decay D_i per waiting/completion time
+
choose order
```

think:

```text
two-job exchange
```

Compare:

```text
1 before 2
vs
2 before 1
```

This usually reveals a ratio.

Here:

```text
descending D/T
```

---

## 3.17 Don't-Memorize Model

Do not memorize:

```text
sort by D/T
```

Remember the two-job question:

```text
Which is worse to delay?

A job with:
high decay
and
small duration
```

The exchange derivation turns that intuition into:

```text
D/T descending
```

---

# 4. Pattern 3 — Median Minimizes Absolute Distance

## 4.1 What the Model Asks

Given points:

```text
x1, x2, ..., xn
```

on a line.

Choose one location:

```text
x
```

to minimize:

```math
F(x)
=
\sum_{i=1}^{n}|x-x_i|
```

Answer:

```text
a median
```

---

## 4.2 Tiny Example

Points:

```text
1,3,7
```

Objective:

```math
F(x)
=
|x-1|
+
|x-3|
+
|x-7|
```

Try:

```text
x = 1:
0+2+6
= 8

x = 3:
2+0+4
= 6

x = 7:
6+4+0
= 10
```

Minimum:

```text
x = 3
```

which is the median.

---

## 4.3 Why Moving Toward the Median Helps

Suppose `x` is not sitting on any point.

Let:

```text
L = number of points left of x
R = number of points right of x
```

Move `x` one unit to the right.

For each left point:

```text
distance increases by 1
```

Total increase:

```text
+L
```

For each right point:

```text
distance decreases by 1
```

Total decrease:

```text
-R
```

Net change:

```math
\Delta
=
L-R
```

---

## 4.4 Inline Example

Points:

```text
1,3,7
```

Take:

```text
x = 2
```

Left points:

```text
[1]
L = 1
```

Right points:

```text
[3,7]
R = 2
```

Move:

```text
2 → 3
```

Net change predicted:

```text
L-R

= 1-2

= -1
```

Actual:

```text
F(2)
= |2-1|+|2-3|+|2-7|
= 1+1+5
= 7

F(3)
= 2+0+4
= 6
```

Change:

```text
6-7
= -1
```

Exactly.

---

## 4.5 Why the Minimum Is at the Median

Before the median:

```text
more points are on the right
```

so:

```text
L < R
```

and:

```text
L-R < 0
```

Moving right decreases cost.

After the median:

```text
more points are on the left
```

so:

```text
L > R
```

and moving right increases cost.

Therefore the turning point is where neither side dominates:

```text
the median
```

---

## 4.6 ASCII Graph

### Cost curve with actual values (correct V-shaped minimum)

For points `[1,3,7]`, `F(x)=|x-1|+|x-3|+|x-7|`:

| x | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| F(x) | 11 | 8 | 7 | **6** | 7 | 8 | 9 | 10 | 13 |

```text
x:        1   2   3   4   5   6   7
cost:     8   7   6   7   8   9  10
                 ^ minimum at median 3
```

The curve decreases toward the median then increases afterward (piecewise-linear and convex).


The numerical table above shows the shape; the slope changes at data points.

---

## 4.7 Pairing Intuition

Sorted points:

```text
x1 <= x2 <= ... <= xn
```

Pair extremes:

```text
(x1, xn)
(x2, x_(n-1))
...
```

For one pair:

```text
a <= b
```

the quantity:

```math
|x-a|+|x-b|
```

is minimized by any:

```text
x in [a,b]
```

For all pairs simultaneously, the common intersection collapses toward the middle point(s).

Thus the median region minimizes the total.

---

## 4.8 Odd and Even N

### Odd

```text
[1,3,7]

median = 3
```

Unique median point.

### Even

```text
[1,3,7,10]
```

Middle points:

```text
3 and 7
```

Every:

```text
x in [3,7]
```

has the same minimum continuous cost.

For integer `x`:

```text
3,4,5,6,7
```

are all optimal.

---

## 4.9 Algorithm

```text
1. Sort points.
2. Choose:
      x[n/2]
   as one valid median.
3. Sum absolute distances.
```

No need to search all possible `x`.

---

## 4.10 C++17

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

    long long median =
        x[n / 2];

    __int128 cost = 0;

    for (long long v : x) {
        cost += llabs(v - median);
    }

    // Use a printer for __int128 if constraints require.
}
```

---

## 4.11 Recognition Model

When you see:

```text
choose one point
+
minimize sum of absolute distances
```

immediately think:

```text
median
```

Not mean.

Mean minimizes:

```text
sum of squared distances
```

Median minimizes:

```text
sum of absolute distances
```

---

## 4.12 Don't-Memorize Model

Remember the movement argument:

```text
move x one step right

cost change
=
#left - #right
```

Before median:

```text
#right > #left
→ move right helps
```

After median:

```text
#left > #right
→ move right hurts
```

So median is the turning point.

---

# 5. Pattern 4 — Weighted Median

## 5.1 What Changes?

Now each position:

```text
x_i
```

has weight:

```text
k_i
```

Objective:

```math
F(x)
=
\sum_i k_i|x-x_i|
```

Interpretation:

```text
moving away from a high-weight point
is more expensive
```

---

## 5.2 Class Example

Positions:

```text
x = [1,3,7]
```

Weights:

```text
k = [1,1,3]
```

Objective:

```math
F(x)
=
|x-1|
+
|x-3|
+
3|x-7|
```

Conceptually expand:

```text
[1,3,7,7,7]
```

Median of expanded values:

```text
7
```

Therefore weighted median:

```text
7
```

---

## 5.3 Verify Numerically

At:

```text
x = 3
```

cost:

```text
|3-1|
+
|3-3|
+
3|3-7|

= 2 + 0 + 3×4

= 14
```

At:

```text
x = 7
```

cost:

```text
|7-1|
+
|7-3|
+
3|7-7|

= 6 + 4 + 0

= 10
```

So the heavy point at `7` pulls the optimum toward itself.

---

## 5.4 Weighted Movement Argument

### Worked example — a meeting point moves right

Imagine `1` person at location `1`, `1` at `3`, and `3` at `7`.

```text
1 (1 person)   3 (1 person)   4 -> 5   7 (3 people)
|--------------|--------------->------|
         2 people left            3 right
```

| Move meeting point | Left people's added cost | Right people's saved cost | Total change |
|---|---:|---:|---:|
| `4 → 5` | `+2 × 1 = +2` | `−3 × 1 = −3` | **−1** |

Check: `F(4)=13`, `F(5)=12`. Moving right **helps** because more weight is on the right (`3 > 2`).

For a move **within an interval containing no data locations**, `Δ = W_left − W_right` per unit moved. At a location, account for its weight when determining the next interval.

Let:

```text
W_left
=
total weight strictly left of x

W_right
=
total weight strictly right of x
```

Move `x` one unit right.

Left-side weighted distances increase by:

```text
W_left
```

Right-side weighted distances decrease by:

```text
W_right
```

Net change:

```math
\Delta
=
W_{left}-W_{right}
```

Exactly the same median logic, but counts are replaced by weights.

---

## 5.5 Weighted Median Condition

Let total weight:

```math
W=\sum_i k_i
```

A weighted median is a point where:

```text
weight strictly to the left <= W/2
```

and:

```text
weight strictly to the right <= W/2
```

For positive integer weights, one convenient implementation is:

```text
sort by x_i

target =
(W+1)/2

first x_i whose cumulative weight >= target
```

---

## 5.6 Inline Prefix-Weight Example

```text
x = [1,3,7]
k = [1,1,3]
```

Total:

```text
W
= 1+1+3
= 5
```

Target:

```text
(W+1)/2
= 6/2
= 3
```

Prefix weights:

```text
at x=1:
1

at x=3:
1+1
= 2

at x=7:
1+1+3
= 5
```

First prefix reaching at least `3`:

```text
x = 7
```

So weighted median:

```text
7
```

---

## 5.7 ASCII Visualization

```text
weight 1        weight 1             weight 3
   |               |               |||
   v               v               vvv

---1---------------3-----------------7------>

Conceptually:

1 copy at 1
1 copy at 3
3 copies at 7

expanded:
[1, 3, 7, 7, 7]

middle:
      7
```

---

## 5.8 Do Not Expand Large Weights

Bad:

```text
k_i = 10^9

repeat x_i one billion times
```

Impossible.

Instead:

```text
sort pairs (x_i,k_i)
compute prefix weight
stop when prefix >= half total
```

---

## 5.9 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<pair<long long,long long>> p(n);

    for (auto& [x, w] : p) {
        cin >> x >> w;
    }

    sort(p.begin(), p.end());

    long long totalWeight = 0;

    for (auto [x, w] : p)
        totalWeight += w;

    long long target =
        (totalWeight + 1) / 2;

    long long prefix = 0;
    long long median = 0;

    for (auto [x, w] : p) {
        prefix += w;

        if (prefix >= target) {
            median = x;
            break;
        }
    }

    __int128 answer = 0;

    for (auto [x, w] : p) {
        answer +=
            (__int128)w
            * llabs(x - median);
    }

    // Print answer with __int128 helper if needed.
}
```

---

## 5.10 Complexity

```text
sorting:
O(N log N)

prefix scan:
O(N)

cost:
O(N)

total:
O(N log N)
```

---

## 5.11 Recognition Model

When you see:

```text
minimize:
Σ weight_i × |x-x_i|
```

think:

```text
weighted median
```

Then:

```text
sort positions
+
prefix weights
+
cross half total weight
```

---

## 5.12 Don't-Memorize Model

Unweighted median:

```text
every point has weight 1
```

Weighted median:

```text
a point with weight k
behaves like k copies
```

That is the whole generalization.

---

# 6. Pattern 5 — Team Performance: Sum × Minimum

## 6.1 Model From the Class

There are `N` people.

Each person has:

```text
speed_i
efficiency_i
```

Choose exactly:

```text
K people
```

Team metric:

```math
performance
=
\left(
\sum efficiency_i
\right)
\times
\min(speed_i)
```

Goal:

```text
maximize performance
```

The two parts behave differently:

```text
sum efficiency
→ additive

minimum speed
→ bottleneck
```

---

## 6.2 Class-Style Example

People:

```text
(speed, efficiency)

(3, 7)
(2, 100)
(5, 7)
```

Choose:

```text
K = 2
```

Possible team:

```text
(3,7) and (5,7)
```

Sum efficiency:

```text
7+7
= 14
```

Minimum speed:

```text
3
```

Performance:

```text
14×3
= 42
```

Another team:

```text
(2,100) and (5,7)
```

Sum:

```text
107
```

Minimum speed:

```text
2
```

Performance:

```text
107×2
= 214
```

So a lower bottleneck may still win if additive sum becomes much larger.

We cannot greedily maximize speed alone or efficiency alone.

---

## 6.3 Key Transformation — Fix the Bottleneck

Suppose we temporarily say:

```text
minimum team speed must be at least S
```

Then only people with:

```text
speed >= S
```

are eligible.

For this fixed threshold, the bottleneck factor is at least:

```text
S
```

So to maximize:

```text
S × sumEfficiency
```

we should choose the:

```text
K largest efficiencies
```

among eligible people.

Therefore:

```text
sort people by speed descending
```

and sweep the speed threshold.

---

## 6.4 Why Sort Speed Descending?

Sorted:

```text
speed1 >= speed2 >= ... >= speedN
```

At index `i`, everyone processed so far has:

```text
speed >= speed_i
```

So the processed prefix is exactly the candidate pool for threshold:

```text
S = speed_i
```

ASCII:

```text
speed descending:

[ very fast ][ fast ][ ... ][ current ][ slower ... ]
<------------ eligible ------------->

current speed = threshold S
```

---

## 6.5 What Must Be Maintained?

For every prefix:

```text
choose K largest efficiencies
```

We need their sum quickly.

Use:

```text
min-heap of size K
```

because when a new efficiency arrives:

```text
push it
```

If size becomes:

```text
K+1
```

remove the smallest efficiency.

Then heap contains:

```text
top K efficiencies in the prefix
```

---

## 6.6 Dry Run of the Class Example

**Real-world analogy:** choose **2 workers**. Each has a speed and an efficiency. Team score = `(sum of efficiencies) × (slowest worker's speed)`.

| Worker | Speed | Efficiency |
|---|---:|---:|
| A | 5 | 7 |
| B | 3 | 7 |
| C | 2 | 100 |

Process workers by **descending speed**, and keep the top **K = 2** efficiencies in a min-heap.

| Minimum speed threshold | Eligible | Heap: best 2 efficiencies | Candidate score |
|---|---|---|---:|
| 5 | A | `[7]` | Not enough workers |
| 3 | A, B | `[7,7]` | `3 × 14 = 42` |
| 2 | A, B, C | `[7,100]` (discard one `7`) | `2 × 107 = 214` |

```text
Speed threshold:  5 --------> 3 --------> 2
Eligible count:   1           2           3
Best score:       —          42         214  <-- maximum
```

**Observation:** a lower speed threshold can win because it allows a much larger efficiency sum. **Sort by bottleneck descending + maintain the best K values.**

---

## 6.7 ASCII Heap View

After processing speed threshold `2`:

```text
eligible efficiencies:

7, 7, 100

need K=2 largest

sort mentally:
7, 7, 100
   ^    ^
 keep 7,100

min-heap stores:
[7,100]

top = 7
```

If another efficiency `50` arrives:

```text
push 50

[7,100,50]

size > 2
→ pop smallest 7

remain:
[50,100]
```

---

## 6.8 Correctness Proof — Threshold View

Let the true optimal team be:

```text
OPT
```

Its minimum speed is:

```text
S*
```

At the sweep iteration with threshold:

```text
S = S*
```

every member of `OPT` has:

```text
speed >= S*
```

so every OPT member is in the processed prefix.

The heap keeps the `K` largest efficiencies in this prefix.

Therefore its efficiency sum:

```math
heapSum
\ge
OPT\_efficiencySum
```

Candidate computed:

```math
S^*\cdot heapSum
```

So:

```math
S^*\cdot heapSum
\ge
S^*\cdot OPT\_efficiencySum
```

But:

```math
S^*
=
OPT's minimum speed
```

so the right-hand side is exactly:

```text
OPT performance
```

Thus at this threshold, the sweep reaches at least the optimum value.

At the same time, the heap-selected people all have speed at least `S*`, so their actual minimum speed is at least `S*`.

Therefore their actual performance is at least the candidate we computed.

So the candidate is feasible, and no candidate can exceed the true optimum.

Hence the maximum sweep candidate equals the optimal answer.

---

## 6.9 A More Intuitive Proof

The team score contains:

```text
min(speed)
```

So every team naturally has a weakest-speed member.

Imagine guessing that weakest speed.

Once it is fixed:

```text
all chosen people must be at least that fast
```

The remaining problem becomes trivial:

```text
pick K largest efficiencies
```

The sweep simply tries every possible bottleneck speed efficiently.

---

## 6.10 Why Sorting by Efficiency Alone Fails

Example:

```text
(2,100)
(3,7)
(5,7)
```

Highest efficiency person:

```text
100
```

but has speed:

```text
2
```

which may reduce the bottleneck for the whole team.

So:

```text
largest efficiency alone
```

is not sufficient.

Similarly:

```text
largest speed alone
```

may give too little efficiency sum.

We need:

```text
enumerate bottleneck
+
optimize additive part
```

---

## 6.11 Algorithm

```text
1. Store people as:
      (speed, efficiency).

2. Sort speed descending.

3. minHeap = empty
   sumEfficiency = 0
   answer = 0

4. For each person in sorted order:
      push efficiency
      add to sum

      if heap size > K:
          subtract heap minimum
          pop minimum

      if heap size == K:
          candidate =
              current speed
              ×
              sumEfficiency

          answer = max(answer, candidate)

5. Return answer.
```

---

## 6.12 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Person {
    long long speed;
    long long efficiency;
};

void printInt128(__int128 x) {
    if (x == 0) {
        cout << 0;
        return;
    }

    if (x < 0) {
        cout << '-';
        x = -x;
    }

    string s;

    while (x > 0) {
        s.push_back(
            char('0' + x % 10)
        );
        x /= 10;
    }

    reverse(s.begin(), s.end());
    cout << s;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k;
    cin >> n >> k;

    vector<Person> people(n);

    for (auto& p : people) {
        cin >> p.speed
            >> p.efficiency;
    }

    sort(
        people.begin(),
        people.end(),
        [](const Person& a, const Person& b) {
            if (a.speed != b.speed)
                return a.speed > b.speed;

            return a.efficiency > b.efficiency;
        }
    );

    priority_queue<
        long long,
        vector<long long>,
        greater<long long>
    > pq;

    __int128 sumEfficiency = 0;
    __int128 best = 0;

    for (const Person& p : people) {
        pq.push(p.efficiency);
        sumEfficiency += p.efficiency;

        if ((int)pq.size() > k) {
            sumEfficiency -= pq.top();
            pq.pop();
        }

        if ((int)pq.size() == k) {
            __int128 candidate =
                (__int128)p.speed
                * sumEfficiency;

            best = max(best, candidate);
        }
    }

    printInt128(best);
    cout << '\n';
}
```

---

## 6.13 Complexity

Sorting:

```text
O(N log N)
```

For each person:

```text
heap push/pop:
O(log K)
```

Total:

```text
O(N log N)
```

Space:

```text
O(N + K)
```

---

## 6.14 Exactly K vs At Most K

The board/code pattern checks:

```text
heap size == K
```

so this note models:

```text
choose exactly K people
```

If a variant says:

```text
choose at most K
```

and all additive values are positive, the same sweep often uses as many helpful people as possible, but the exact condition should be read from that problem statement.

Do not silently interchange:

```text
exactly K
```

and:

```text
at most K
```

---

## 6.15 Recognition Model

When you see:

```text
choose K items
+
score =
(sum of additive attribute)
×
(minimum bottleneck attribute)
```

think:

```text
sort bottleneck descending
```

Then:

```text
sweep threshold
+
keep top-K additive values
using min-heap
```

---

## 6.16 Don't-Memorize Model

Do not memorize:

```text
sort speed
minheap efficiency
```

Remember:

```text
Every team has a minimum speed.

Fix that minimum speed S.

Then everyone with speed >= S
is eligible.

For fixed S,
only one thing remains:

maximize efficiency sum

→ choose top K efficiencies.
```

That derivation explains both:

```text
the sorting key
and
the heap.
```

---

# 7. Pattern Comparison

| Pattern | Objective | Greedy Structure | Core Proof |
|---|---|---|---|
| Maximum dot product | maximize `Σ ai*bi` | same-order sorting | swap / rearrangement |
| Job ordering | maximize decaying total score | sort `D/T` descending | adjacent two-job exchange |
| Median | minimize `Σ|x-xi|` | choose median | left-vs-right slope |
| Weighted median | minimize `Σki|x-xi|` | weighted median | weighted left-vs-right slope |
| Team performance | maximize `Σeff × min(speed)` | threshold + top-K | bottleneck enumeration |

---

# 8. Final Recognition Checklist

```text
1. Can elements be permuted?
   Is objective Σ product?
   → rearrangement inequality.

2. Is there an ordering problem
   with time and per-time penalty?
   → compare two adjacent jobs.

3. Did a ratio appear?
   → derive it by exchange;
      compare with cross multiplication.

4. Is objective:
      Σ|x-x_i|?
   → median.

5. Is objective:
      Σk_i|x-x_i|?
   → weighted median.

6. Is objective:
      additive sum × minimum?
   → enumerate minimum as threshold.

7. For each threshold,
   do I need top K values?
   → min-heap of size K.

8. Are products/sums potentially > 9e18?
   → use __int128.
```

---

# 9. Compact Revision Card

```text
ALGOZENITH GREEDY — CLASS 1
===========================


1. REARRANGEMENT / DOT PRODUCT
------------------------------
maximize:
Σ ai*bi

sort both same order

pair proof:

ai <= aj
bi <= bj

same:
ai*bi + aj*bj

crossed:
ai*bj + aj*bi

difference:
(aj-ai)(bj-bi) >= 0

therefore:
same-order pairing is optimal


2. SCORE–DECAY–TIME SCHEDULING
------------------------------
job i:
score S_i
decay D_i
time T_i

finish at C_i:
score = S_i - D_i*C_i

compare:
P1→P2
vs
P2→P1

P1 first is better iff:

T1*D2 <= T2*D1

equivalent:

D1/T1 >= D2/T2

therefore:
sort D/T descending

use cross multiplication


3. MEDIAN
---------
minimize:
Σ|x-x_i|

move x right by 1:

cost change:
#left - #right

before median:
more on right
→ moving right helps

after median:
more on left
→ moving right hurts

answer:
median


4. WEIGHTED MEDIAN
------------------
minimize:
Σ k_i |x-x_i|

weight k_i behaves like
k_i copies of x_i

sort by x_i

total weight W

first prefix weight >= ceil(W/2)
gives a weighted median


5. TEAM PERFORMANCE
-------------------
choose K people

score:
(sum efficiency)
×
minimum speed

sort speed descending

current speed = threshold

among people with
speed >= threshold:

keep top K efficiencies

data structure:
min-heap size K

candidate:
threshold * topKSum
```

---

# Final Mental Model

```text
                 ALGOZENITH GREEDY CLASS 1
                            |
      +---------------------+--------------------+
      |                     |                    |
   reordering            location             selection
      |                     |                    |
      v                     v                    v
exchange proof        absolute distance     bottleneck threshold
      |                     |                    |
      v                     v                    v
sort / ratio          median / weighted      top-K min-heap
```

> **Main lesson:** Greedy becomes much easier when you stop asking *"What should I pick?"* and instead ask *"If two local choices are in the wrong order, can I swap them and prove the objective does not get worse?"*  
> For non-ordering problems, identify whether the objective is controlled by a **median** or by a **bottleneck threshold**.
