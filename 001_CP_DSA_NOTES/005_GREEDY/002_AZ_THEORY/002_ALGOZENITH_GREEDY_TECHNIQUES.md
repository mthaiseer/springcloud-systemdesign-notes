# AlgoZenith Greedy Techniques — Class 1
## Optimized TLE-Style Notes — Full Variable Names + Inline Examples + ASCII + C++17

> **Goal:** keep the TLE-style clarity—**example first → derivation → proof → dry run → code → recognition**—without repeating the same idea in multiple sections.
>
> **Variable rule:** formulas use descriptive names such as `smallerFirstValue`, `firstDecayPerTime`, `targetValue`, and `meetingPosition`.
>
> **Rendering rule:** conceptual mathematics uses fenced `math` blocks for clean GitHub rendering. Long step-by-step algebra keeps descriptive variable names in aligned text blocks when that is easier to read.

---

# Clickable TOC

- [0. Greedy Pattern Map](#0-greedy-pattern-map)
- [1. Prerequisites](#1-prerequisites)
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
  - [1.16 Overflow / `long long`](#116-overflow--long-long)
  - [1.17 Quick Greedy Proof Checklist](#117-quick-greedy-proof-checklist)
- [2. Minimum Dot Product — Rearrangement](#2-minimum-dot-product--rearrangement)
- [3. Score–Decay–Time Scheduling](#3-scoredecaytime-scheduling)
- [4. Minimum Operations From 0 to Target](#4-minimum-operations-from-0-to-target)
- [5. Median — Sum of Absolute Distances](#5-median--sum-of-absolute-distances)
- [6. Weighted Median](#6-weighted-median)
- [7. Manhattan Meeting Point](#7-manhattan-meeting-point)
- [8. Proof Pattern Summary](#8-proof-pattern-summary)
- [9. Recognition Checklist](#9-recognition-checklist)
- [10. Compact Revision Card](#10-compact-revision-card)

---

# 0. Greedy Pattern Map

The class first groups greedy problems into families:

```text
GREEDY
│
├── Sorting-Based
│   ├── Interval ordering
│   │    └── Sweep-line related forms
│   ├── Priority-based ordering
│   ├── Rearrangement / pairing
│   └── Ratio ordering
│
├── Operation Handling
│   ├── choose best / forced operation
│   └── reverse the process when backward is easier
│
└── Classical Mathematical Forms
    ├── Median
    ├── Weighted Median
    ├── Manhattan Distance
    └── other known greedy structures
```

The supplied class develops these concrete forms:

```text
Rearrangement
→ minimum dot product

Sorting + exchange
→ score-decay-time scheduling

Operation handling
→ +1 / ×2
→ reverse greedy

Classical
→ median
→ weighted median
→ Manhattan
```

`Interval`, `Sweep Line`, and `Priority` are kept here as **recognition families** because the screenshots only list them; their detailed class derivations are not present in the supplied material.

---

# 1. Prerequisites

> **Purpose:** understand the small set of ideas repeatedly used in the class proofs.
>
> Use this learning order:
>
> ```text
> tiny example
>     ↓
> understand the local choice
>     ↓
> write the general form
>     ↓
> prove the choice is safe
> ```

---

## 1.1 What Greedy Means

A greedy algorithm makes the **best-looking safe local choice now** and does not go back.

But:

```text
"this looks best"
```

is not a proof.

The real model is:

```text
GREEDY
=
LOCAL CHOICE
+
PROOF THAT THE CHOICE IS SAFE
```

### Tiny Example

Suppose two pairings are possible:

```text
small value = 2
large value = 5

small weight = 3
large weight = 7
```

One local choice:

```text
small with small
large with large
```

gives:

```text
2*3 + 5*7
=
41
```

Another local choice:

```text
small with large
large with small
```

gives:

```text
2*7 + 5*3
=
29
```

For minimization:

```text
29 < 41
```

So the crossed pairing **looks better**.

The greedy proof must now show:

```text
this remains safe for arbitrary values,
not only for 2, 5, 3, 7
```

### Mental Model

```text
Current state
     |
     v
Choose candidate greedy action
     |
     v
Can I prove it is safe?
    / \
  YES  NO
   |    |
 take   keep analyzing
```

---

## 1.2 Local Choice vs Global Optimum

A greedy algorithm makes a **local** decision, but the problem asks for the **global** optimum.

Example full objective:

```text
pair1Contribution
+
pair2Contribution
+
pair3Contribution
+
...
+
pairNContribution
```

A swap may change only:

```text
pairIContribution
pairJContribution
```

Everything else stays identical.

ASCII:

```text
FULL SOLUTION

[ same ][ same ][ LOCAL CHANGE ][ same ][ same ]
                      |
                      v
             prove only this part
```

### Tiny Example

Suppose:

```text
wholeAnswerBefore
=
10 + 41 + 8
=
59
```

and after changing one local pair:

```text
wholeAnswerAfter
=
10 + 29 + 8
=
47
```

The `10` and `8` never changed.

So the global comparison is really:

```text
41
vs
29
```

This is why many greedy proofs become **two-item proofs**.

---

## 1.3 Exchange / Swapping Proof

This is one of the most reusable greedy proofs.

Suppose an optimal solution uses:

```text
OtherChoice
```

while greedy wants:

```text
GreedyChoice
```

Try exchanging them.

```text
OPT uses OtherChoice
        |
        v
Greedy wants GreedyChoice
        |
        v
replace / swap locally
        |
        v
still feasible?
        |
       YES
        |
        v
objective same or better?
        |
       YES
        |
        v
an optimal solution can use GreedyChoice
```

### What Must Be Checked?

```text
1. Feasibility
   → after the swap, is the solution still legal?

2. Objective
   → after the swap, is the answer non-worse?
```

### Tiny Numerical Example

For a maximization problem:

```text
OtherContribution
=
20

GreedyContribution
=
30
```

Exchange:

```text
20 → 30
```

Difference:

```text
GreedyContribution - OtherContribution

=
30 - 20

=
10

>= 0
```

So the local exchange is non-worse.

### Important

Not every exchange proof needs long algebra.

Sometimes the proof is simply:

```text
anything possible after OtherChoice
is also possible after GreedyChoice
```

That is enough.

---

## 1.4 Why We Compare Only Two Positions

Suppose two arrangements differ only at positions:

```text
firstIndex
secondIndex
```

Before:

```text
... + oldFirstContribution + ... + oldSecondContribution + ...
```

After:

```text
... + newFirstContribution + ... + newSecondContribution + ...
```

All other terms are equal.

Therefore:

```text
wholeDifference

=
oldFirstContribution
+
oldSecondContribution
-
newFirstContribution
-
newSecondContribution
```

### Dot-Product Example

Same-order pair:

```text
smallerFirstValue * smallerSecondValue
+
largerFirstValue * largerSecondValue
```

Crossed pair:

```text
smallerFirstValue * largerSecondValue
+
largerFirstValue * smallerSecondValue
```

Only these four products matter.

### Numerical Example

```text
same:
2*3 + 5*7
=
41

crossed:
2*7 + 5*3
=
29
```

Difference:

```text
41 - 29
=
12
```

We never need to recompute the untouched part of the array.

---

## 1.5 Inversions

An inversion means a pair violates the desired order.

For ascending order:

```text
earlierIndex < laterIndex

but

earlierValue > laterValue
```

Example:

```text
[1, 7, 4, 9]
    ^  ^
    inversion
```

because:

```text
7 > 4
```

### Why Inversions Matter in Greedy Proofs

A common proof is:

```text
find one inversion
→ swap it
→ prove objective does not get worse
→ repeat
→ no inversions remain
→ greedy sorted order
```

### Tiny Example

```text
[1,7,4,9]

swap 7 and 4

[1,4,7,9]
```

One inversion disappeared.

If every such swap is safe, an optimal sorted solution exists.

---

## 1.6 Sorting as a Greedy Tool

Sorting itself is not the proof.

Sorting is useful when the proof determines the preferred relative order between **any two items**.

Examples from this class:

```text
Minimum Dot Product:
Which value should pair with which?

Score–Decay–Time:
Should firstJob come before secondJob?
```

If the two-item proof says:

```text
whenever pair/order is "wrong",
swapping it is non-worse
```

then sorting globally applies that local rule.

### Mental Model

```text
two-item comparison
       |
       v
derive preferred order
       |
       v
remove all inversions
       |
       v
sort
```

---

## 1.7 Cross Multiplication for Ratio Comparators

Suppose the derivation gives:

```math
\frac{\mathrm{firstDecayPerTime}}
     {\mathrm{firstTimeNeeded}}
\ge
\frac{\mathrm{secondDecayPerTime}}
     {\mathrm{secondTimeNeeded}}
```

If both times are positive, compare:

```math
\mathrm{firstDecayPerTime}
\cdot
\mathrm{secondTimeNeeded}
\ge
\mathrm{secondDecayPerTime}
\cdot
\mathrm{firstTimeNeeded}
```

This avoids floating-point precision problems.

### Inline Example

```text
firstDecayPerTime = 1
firstTimeNeeded   = 2

secondDecayPerTime = 2
secondTimeNeeded   = 3
```

Ratios:

```text
first ratio
=
1/2
=
0.5

second ratio
=
2/3
≈
0.667
```

Cross multiplication:

```text
first side
=
1*3
=
3

second side
=
2*2
=
4
```

Since:

```text
3 < 4
```

the second job has the larger ratio.

### C++ Comparator Pattern

```cpp
long long firstCrossProduct =
    firstJob.decayPerUnitTime * secondJob.timeNeeded;

long long secondCrossProduct =
    secondJob.decayPerUnitTime * firstJob.timeNeeded;

return firstCrossProduct > secondCrossProduct;
```

Use this only when the products fit in `long long`.

---

## 1.8 Completion Time

Do not confuse:

```text
timeNeeded
```

with:

```text
completionTime
```

### Meaning

```text
timeNeeded
=
how long one job itself takes

completionTime
=
total elapsed time when that job finishes
```

### Example

```text
firstTimeNeeded  = 2
secondTimeNeeded = 3
```

Order:

```text
firstJob → secondJob
```

Then:

```text
firstCompletionTime
=
2

secondCompletionTime
=
2+3
=
5
```

ASCII:

```text
time
0---------2----------------5
| first   |     second     |
      ^              ^
 first ends       second ends
```

The second job waits for the first job.

That waiting time is exactly why ordering affects the score.

---

## 1.9 Reverse Greedy

Some forward processes have many possible next choices.

Example:

```text
currentValue → currentValue + 1
currentValue → 2 * currentValue
```

Forward question:

```text
Should I +1 now?
Should I double now?
```

Backward from the target, the previous move may be forced.

### Odd Target

If:

```text
targetValue
```

is odd, doubling could not create it because:

```text
2 * integer
```

is always even.

So the previous forward move must have been:

```text
+1
```

Backward:

```text
targetValue--
```

Example:

```text
13 is odd

13
→
12
```

because:

```text
12 + 1
=
13
```

### Even Target

If:

```text
targetValue
```

is even, undo a doubling:

```text
targetValue /= 2
```

Example:

```text
12 → 6
```

### Mental Model

```text
FORWARD
many possible choices

BACKWARD
last move may be forced
```

---

## 1.10 Binary View of `+1` and `×2`

Multiplication by 2 is a binary left shift.

Example:

```text
5
=
101₂
```

Double:

```text
10
=
1010₂
```

So:

```text
×2
→ shift existing bits left
```

The `+1` operation creates a required `1` bit while constructing the number.

### Example — Target 12

```text
12
=
1100₂
```

Construct from left to right:

```text
0
→ +1  = 1
→ ×2  = 10
→ +1  = 11
→ ×2  = 110
→ ×2  = 1100
```

Operations:

```text
+1, ×2, +1, ×2, ×2
```

Count:

```text
5
```

This explains the formula:

```text
minimumOperations
=
bitLength(targetValue) - 1
+
popcount(targetValue)
```

for positive `targetValue`.

---

## 1.11 Absolute Distance

Distance between:

```text
meetingPosition
```

and:

```text
pointPosition
```

is:

```math
\left|
\mathrm{meetingPosition}
-
\mathrm{pointPosition}
\right|
```

### Example

```text
meetingPosition = 7
pointPosition   = 3
```

Then:

```text
|7-3|
=
4
```

For many points:

```math
\mathrm{totalDistance}
=
\sum_i
\left|
\mathrm{meetingPosition}
-
\mathrm{pointPosition}_i
\right|
```

This absolute-distance structure is the signal for a median.

---

## 1.12 Convex / V-Shaped Cost

One term:

```math
\left|
\mathrm{meetingPosition}
-
\mathrm{pointPosition}
\right|
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
 +-----pointPosition--------> meetingPosition
```

A sum of absolute-value terms remains convex / piecewise linear.

So total cost behaves like:

```text
decreasing
→ minimum / flat region
→ increasing
```

### Tiny Example

Points:

```text
1,3,7
```

Costs:

```text
meeting at 1 → 8
meeting at 2 → 7
meeting at 3 → 6   minimum
meeting at 4 → 7
meeting at 5 → 8
```

ASCII:

```text
cost
 ^
 |\
 | \
 |  \_/
 |
 +--------------------------> meetingPosition
        3
```

The median is where the slope changes sign.

---

## 1.13 Median

For sorted points:

```text
point1 <= point2 <= ... <= pointN
```

a median minimizes:

```math
\sum_i
\left|
\mathrm{meetingPosition}
-
\mathrm{pointPosition}_i
\right|
```

### Odd Number of Points

Example:

```text
[1,3,7]
```

Median:

```text
3
```

Cost:

```text
2 + 0 + 4
=
6
```

### Even Number of Points

Example:

```text
[1,3,5,8]
```

The two middle values are:

```text
3
and
5
```

Every continuous point in:

```text
[3,5]
```

is optimal.

For integer positions:

```text
3,4,5
```

are all optimal.

Number of optimal integer positions:

```text
upperMedian - lowerMedian + 1

=
5 - 3 + 1

=
3
```

---

## 1.14 Weighted Median

Now each point has:

```text
pointPosition
pointWeight
```

Objective:

```math
\sum_i
\mathrm{pointWeight}_i
\cdot
\left|
\mathrm{meetingPosition}
-
\mathrm{pointPosition}_i
\right|
```

Interpret:

```text
pointWeight = 3

behaves conceptually like

3 copies of that point
```

### Example

```text
positions = [1,3,7]
weights   = [1,1,3]
```

Conceptually:

```text
[1,3,7,7,7]
```

Median:

```text
7
```

So weighted median:

```text
7
```

### Efficient Recognition

Do not expand the points.

Instead:

```text
sort by position
→ prefix / cumulative weight
→ find the half-weight crossing
```

---

## 1.15 Manhattan Distance Separability

For one point:

```text
(pointX, pointY)
```

and a meeting point:

```text
(meetingX, meetingY)
```

Manhattan distance is:

```math
\left|
\mathrm{meetingX}
-
\mathrm{pointX}
\right|
+
\left|
\mathrm{meetingY}
-
\mathrm{pointY}
\right|
```

For many points:

```math
\sum_i
\left(
\left|
\mathrm{meetingX}
-
\mathrm{pointX}_i
\right|
+
\left|
\mathrm{meetingY}
-
\mathrm{pointY}_i
\right|
\right)
```

Split the sum:

```math
=
\sum_i
\left|
\mathrm{meetingX}
-
\mathrm{pointX}_i
\right|
+
\sum_i
\left|
\mathrm{meetingY}
-
\mathrm{pointY}_i
\right|
```

So:

```text
x-coordinate problem
and
y-coordinate problem
are independent
```

Therefore:

```text
meetingX
=
median of x-coordinates

meetingY
=
median of y-coordinates
```

### Tiny Example

Points:

```text
(1,1)
(3,4)
(7,2)
```

X values:

```text
[1,3,7]
→ median = 3
```

Y values:

```text
[1,4,2]
→ sorted [1,2,4]
→ median = 2
```

Optimal meeting point:

```text
(3,2)
```

---

## 1.16 Overflow / `long long`

These notes use:

```cpp
long long
```

in the C++ templates as requested.

A signed `long long` is roughly safe up to:

```text
9.22 * 10^18
```

### Example — Product

```text
10^9 * 10^9
=
10^18
```

One such product fits in `long long`.

But always check the **sum** too.

Example:

```text
N = 100000

each contribution ≈ 10^18
```

Worst-case sum would be much larger than `long long`.

So the code templates below assume:

```text
the problem guarantees
the final answer and comparator products
fit in long long
```

If the actual constraints do not guarantee that, a wider integer type is required.

### Safe Contest Habit

Before coding:

```text
maximum single term
×
maximum number of terms
```

Estimate whether the final answer fits.

---

## 1.17 Quick Greedy Proof Checklist

```text
1. What exactly is minimized / maximized?

2. What must stay feasible?

3. What is the greedy local choice?

4. Can I compare only two choices?

5. Which terms remain unchanged?

6. Can I subtract the two local contributions?

7. Can I group / factor the result?

8. What signs do the factors have?

9. Does this imply a sorting order?

10. Did a ratio appear?
    → cross multiply.

11. Is forward reasoning ambiguous?
    → reverse the operations.

12. Is the objective sum of absolute distances?
    → median.

13. Is it weighted?
    → weighted median.

14. Is it 2D Manhattan?
    → separate x and y.
```

---

# 2. Minimum Dot Product — Rearrangement

## 2.1 What It Asks

Given:

```text
firstValues
secondValues
```

rearrange pairings to minimize:

```text
sum(
    firstValues[index]
    *
    secondValues[index]
)
```

---

## 2.2 Concept Simplified

For minimum:

```text
small from one array
pairs with
large from the other
```

Therefore:

```text
firstValues  ascending
secondValues descending
```

---

## 2.3 Tiny Example First

```text
firstValues  = [1,2,3]
secondValues = [1,2,3]
```

Same order:

```text
1*1 + 2*2 + 3*3
=
14
```

Opposite order:

```text
1*3 + 2*2 + 3*1
=
10
```

So opposite order is better for minimization.

---

## 2.4 Local Variables

Assume:

```text
smallerFirstValue <= largerFirstValue

smallerSecondValue <= largerSecondValue
```

Same-order contribution:

```text
sameOrderContribution
=
smallerFirstValue * smallerSecondValue
+
largerFirstValue * largerSecondValue
```

Crossed contribution:

```text
crossedContribution
=
smallerFirstValue * largerSecondValue
+
largerFirstValue * smallerSecondValue
```

We want to prove:

```text
crossedContribution
<=
sameOrderContribution
```

---

## 2.5 Proof — Step by Step

Start:

```text
sameOrderContribution
-
crossedContribution
```

Substitute:

```text
=
smallerFirstValue * smallerSecondValue
+
largerFirstValue * largerSecondValue
-
smallerFirstValue * largerSecondValue
-
largerFirstValue * smallerSecondValue
```

Group:

```text
=
largerFirstValue * largerSecondValue
-
largerFirstValue * smallerSecondValue
-
smallerFirstValue * largerSecondValue
+
smallerFirstValue * smallerSecondValue
```

Factor:

```text
=
largerFirstValue
*
(largerSecondValue - smallerSecondValue)

-
smallerFirstValue
*
(largerSecondValue - smallerSecondValue)
```

Factor again:

```text
sameOrderContribution
-
crossedContribution

=
(largerFirstValue - smallerFirstValue)
*
(largerSecondValue - smallerSecondValue)
```

Both factors are non-negative:

```text
largerFirstValue - smallerFirstValue >= 0

largerSecondValue - smallerSecondValue >= 0
```

Therefore:

```text
sameOrderContribution
-
crossedContribution
>=
0
```

Hence:

```text
sameOrderContribution
>=
crossedContribution
```

So crossed/opposite pairing is safe for minimization.

---

## 2.6 Same Derivation With Numbers

Use:

```text
smallerFirstValue  = 2
largerFirstValue   = 5

smallerSecondValue = 3
largerSecondValue  = 7
```

Same:

```text
2*3 + 5*7
=
41
```

Crossed:

```text
2*7 + 5*3
=
29
```

Difference:

```text
41 - 29
=
12
```

Factored proof:

```text
(5-2)*(7-3)

=
3*4

=
12
```

Same result.

---

## 2.7 Why This Proves the Whole Array

Fix:

```text
firstValues
```

ascending.

Whenever `secondValues` has a pair aligned in the same direction, swap that pair.

ASCII:

```text
before:

smallFirst -------- smallSecond
largeFirst -------- largeSecond

after:

smallFirst -------- largeSecond
largeFirst -------- smallSecond
```

The local proof says the answer cannot increase.

Repeat until `secondValues` is descending.

Thus:

```text
MIN → opposite order
MAX → same order
```

---

## 2.8 Dry Run

```text
firstValues
=
[4,1,3]

secondValues
=
[2,5,1]
```

Sort:

```text
firstValues
=
[1,3,4]

secondValues
=
[5,2,1]
```

Answer:

```text
1*5 + 3*2 + 4*1

=
5 + 6 + 4

=
15
```

---

## 2.9 Algorithm + C++17

```text
sort firstValues ascending
sort secondValues descending
sum pair products
```

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfValues;
    cin >> numberOfValues;

    vector<long long> firstValues(numberOfValues);
    vector<long long> secondValues(numberOfValues);

    for (long long& value : firstValues)
        cin >> value;

    for (long long& value : secondValues)
        cin >> value;

    sort(firstValues.begin(), firstValues.end());
    sort(secondValues.rbegin(), secondValues.rend());

    long long minimumDotProduct = 0;

    for (int index = 0; index < numberOfValues; ++index) {
        minimumDotProduct +=
            firstValues[index] * secondValues[index];
    }
}
```

Complexity:

```text
O(N log N)
```

---

## 2.10 Recognition / Don't Memorize

Recognize:

```text
two arrays
+
rearrange
+
sum of products
```

Remember the proof:

```text
same - crossed
=
(largerFirst-smallerFirst)
*
(largerSecond-smallerSecond)
>= 0
```

Not just the sorting direction.

---

# 3. Score–Decay–Time Scheduling

## 3.1 What It Asks

Each job has:

```text
initialScore
decayPerUnitTime
timeNeeded
```

If it finishes at `completionTime`:

```text
finalScore
=
initialScore
-
decayPerUnitTime * completionTime
```

Choose the order maximizing total score.

---

## 3.2 Tiny Example

First job:

```text
firstInitialScore = 10
firstDecayPerTime = 1
firstTimeNeeded   = 2
```

Second job:

```text
secondInitialScore = 5
secondDecayPerTime = 2
secondTimeNeeded   = 3
```

### First → Second

```text
first completion = 2
first score      = 10 - 1*2 = 8

second completion = 2+3 = 5
second score      = 5 - 2*5 = -5

total = 3
```

### Second → First

```text
second completion = 3
second score      = 5 - 2*3 = -1

first completion = 3+2 = 5
first score      = 10 - 1*5 = 5

total = 4
```

So second job should be earlier.

---

## 3.3 Greedy Claim

Sort by:

```text
decayPerUnitTime / timeNeeded
```

descending.

Now derive it.

---

## 3.4 Two-Job Derivation

First → Second:

```text
scoreFirstThenSecond

=
firstInitialScore
-
firstDecayPerTime * firstTimeNeeded

+
secondInitialScore
-
secondDecayPerTime
*
(firstTimeNeeded + secondTimeNeeded)
```

Second → First:

```text
scoreSecondThenFirst

=
secondInitialScore
-
secondDecayPerTime * secondTimeNeeded

+
firstInitialScore
-
firstDecayPerTime
*
(secondTimeNeeded + firstTimeNeeded)
```

Prefer first job first if:

```text
scoreFirstThenSecond
>=
scoreSecondThenFirst
```

Expand both sides and cancel common terms:

```text
-firstDecayPerTime * firstTimeNeeded
```

appears on both sides.

Also:

```text
-secondDecayPerTime * secondTimeNeeded
```

appears on both sides.

Both initial scores also cancel.

Remain:

```text
-secondDecayPerTime * firstTimeNeeded

>=

-firstDecayPerTime * secondTimeNeeded
```

Multiply by `-1` and reverse the inequality:

```text
secondDecayPerTime * firstTimeNeeded

<=

firstDecayPerTime * secondTimeNeeded
```

Equivalent:

```text
firstDecayPerTime * secondTimeNeeded

>=

secondDecayPerTime * firstTimeNeeded
```

Since times are positive:

```text
firstDecayPerTime / firstTimeNeeded

>=

secondDecayPerTime / secondTimeNeeded
```

That is the comparator.

---

## 3.5 Example Inside the Comparator

```text
first ratio
=
1/2
=
0.5

second ratio
=
2/3
≈
0.667
```

So second comes first.

Cross multiplication:

```text
firstDecayPerTime * secondTimeNeeded
=
1*3
=
3

secondDecayPerTime * firstTimeNeeded
=
2*2
=
4

3 < 4
```

Again, second comes first.

---

## 3.6 Exchange Proof for the Whole Schedule

If adjacent jobs violate descending ratio order:

```text
smaller ratio first
larger ratio second
```

swap them.

All jobs before the pair are unchanged.

All jobs after the pair see the same combined duration:

```text
firstTimeNeeded + secondTimeNeeded
```

So only the local pair contribution changes.

The two-job proof shows the swap is non-worse.

Repeat until all ratios are descending.

---

## 3.7 ASCII

```text
larger decay/time
=
more urgent

large ratio -------------------- small ratio
EARLY                                LATE
```

---

## 3.8 Dry Run

Three jobs:

```text
Job A: initial=10, decay=1, time=2
Job B: initial=5,  decay=2, time=3
Job C: initial=20, decay=1, time=1
```

Ratios:

```text
A = 1/2 = 0.5
B = 2/3 ≈ 0.667
C = 1/1 = 1
```

Order:

```text
C → B → A
```

Completion times:

```text
C = 1
B = 1+3 = 4
A = 1+3+2 = 6
```

Scores:

```text
C = 20 - 1*1 = 19
B = 5  - 2*4 = -3
A = 10 - 1*6 = 4
```

Total:

```text
19 - 3 + 4
=
20
```

---

## 3.9 Algorithm + C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Job {
    long long initialScore;
    long long decayPerUnitTime;
    long long timeNeeded;
};

bool comesEarlier(
    const Job& firstJob,
    const Job& secondJob
) {
    long long firstCrossProduct =
        firstJob.decayPerUnitTime * secondJob.timeNeeded;

    long long secondCrossProduct =
        secondJob.decayPerUnitTime * firstJob.timeNeeded;

    if (firstCrossProduct != secondCrossProduct)
        return firstCrossProduct > secondCrossProduct;

    return firstJob.timeNeeded < secondJob.timeNeeded;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfJobs;
    cin >> numberOfJobs;

    vector<Job> jobs(numberOfJobs);

    for (Job& job : jobs) {
        cin >> job.initialScore
            >> job.decayPerUnitTime
            >> job.timeNeeded;
    }

    sort(jobs.begin(), jobs.end(), comesEarlier);

    long long elapsedTime = 0;
    long long totalScore = 0;

    for (const Job& job : jobs) {
        elapsedTime += job.timeNeeded;

        totalScore +=
            job.initialScore
            - job.decayPerUnitTime * elapsedTime;
    }

    cout << totalScore << '\n';
    return 0;
}
```

Complexity:

```text
O(N log N)
```

---

## 3.10 Recognition / Don't Memorize

When you see:

```text
order jobs
+
time
+
per-time penalty / decay
```

compare **two jobs**.

Do not memorize the ratio before deriving it.

Mental meaning:

```text
high decay
+
short time
=
dangerous to delay
```

---

# 4. Minimum Operations From 0 to Target

## 4.1 What It Asks

Start:

```text
currentValue = 0
```

Operations:

```text
currentValue = currentValue + 1
currentValue = 2 * currentValue
```

Reach:

```text
targetValue
```

in minimum operations.

---

## 4.2 Why Reverse Greedy

Forward choice can be unclear.

Backward:

```text
odd target
→ cannot be produced by doubling
→ previous operation was +1
→ subtract 1

even target
→ undo doubling
→ divide by 2
```

---

## 4.3 Dry Run — Target 12

```text
12 even → 6
6  even → 3
3  odd  → 2
2  even → 1
1  odd  → 0
```

Count:

```text
5
```

Forward reconstruction:

```text
0 → 1 → 2 → 3 → 6 → 12

+1
×2
+1
×2
×2
```

---

## 4.4 ASCII

```text
FORWARD                     BACKWARD

0                           12
|                            |
+1                           /2
v                            v
1                            6
|                            |
×2                           /2
v                            v
2                            3
|                            |
+1                           -1
v                            v
3                            2
|                            |
×2                           /2
v                            v
6                            1
|                            |
×2                           -1
v                            v
12                           0
```

---

## 4.5 Binary Interpretation

```text
12
=
1100
```

Build:

```text
0
→ +1  = 1
→ ×2  = 10
→ +1  = 11
→ ×2  = 110
→ ×2  = 1100
```

So for positive target:

```text
numberOfDoublings
=
bitLength(targetValue) - 1

numberOfIncrements
=
popcount(targetValue)
```

Therefore:

```text
minimumOperations
=
bitLength(targetValue) - 1
+
popcount(targetValue)
```

For 12:

```text
bitLength = 4
popcount  = 2

(4-1)+2
=
5
```

---

## 4.6 Algorithm + C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    unsigned long long targetValue;
    cin >> targetValue;

    long long minimumOperations = 0;

    while (targetValue > 0) {
        if (targetValue & 1ULL)
            --targetValue;
        else
            targetValue >>= 1;

        ++minimumOperations;
    }

    cout << minimumOperations << '\n';
    return 0;
}
```

Complexity:

```text
O(log targetValue)
```

---

## 4.7 Recognition / Don't Memorize

Recognize:

```text
forward operations grow value
+
target is huge
+
choice is unclear
```

Try reversing.

Remember the reason:

```text
odd
→ doubling impossible

even
→ /2 removes one doubling
```

---

# 5. Median — Sum of Absolute Distances

## 5.1 What It Asks

Choose:

```text
meetingPosition
```

to minimize:

```math
\sum_i
\left|
\mathrm{meetingPosition}
-
\mathrm{pointPosition}_i
\right|
```

Answer:

```text
median
```

---

## 5.2 Tiny Example

Points:

```text
[1,3,7]
```

Costs:

```text
meeting at 1:
0+2+6 = 8

meeting at 3:
2+0+4 = 6

meeting at 7:
6+4+0 = 10
```

Minimum:

```text
meetingPosition = 3
```

---

## 5.3 Derivation

Move the meeting position one unit right.

Let:

```text
numberOfPointsOnLeft
numberOfPointsOnRight
```

Each left distance increases by 1:

```text
+numberOfPointsOnLeft
```

Each right distance decreases by 1:

```text
-numberOfPointsOnRight
```

Therefore:

```text
costChangeWhenMovingRight
=
numberOfPointsOnLeft
-
numberOfPointsOnRight
```

---

## 5.4 Inline Example

Points:

```text
[1,3,7]
```

At meeting position 2:

```text
left:
[1]
→ 1 point

right:
[3,7]
→ 2 points
```

Predicted change when moving to 3:

```text
1 - 2
=
-1
```

Actual:

```text
cost(2)
=
1+1+5
=
7

cost(3)
=
2+0+4
=
6

6-7
=
-1
```

Matches.

---

## 5.5 Why Median Is Optimal

Before median:

```text
more points on right
→ moving right decreases cost
```

After median:

```text
more points on left
→ moving right increases cost
```

Therefore the turning point is the median region.

---

## 5.6 Even Count

Example:

```text
[1,3,5,8]
```

Median interval:

```text
[3,5]
```

Every integer:

```text
3,4,5
```

is optimal.

Count:

```text
upperMedian - lowerMedian + 1

=
5 - 3 + 1

=
3
```

---

## 5.7 ASCII

```text
cost
 ^
 |\
 | \
 |  \
 |   \________
 |            \
 |             \
 +----------------------> meetingPosition
       3      5
       <------>
      minimum region
```

---

## 5.8 Algorithm + C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfPoints;
    cin >> numberOfPoints;

    vector<long long> pointPositions(numberOfPoints);

    for (long long& pointPosition : pointPositions)
        cin >> pointPosition;

    sort(pointPositions.begin(), pointPositions.end());

    long long lowerMedian =
        pointPositions[(numberOfPoints - 1) / 2];

    long long upperMedian =
        pointPositions[numberOfPoints / 2];

    long long meetingPosition = upperMedian;

    long long minimumTotalDistance = 0;

    for (long long pointPosition : pointPositions) {
        minimumTotalDistance +=
            llabs(pointPosition - meetingPosition);
    }

    long long numberOfOptimalIntegerPositions =
        upperMedian - lowerMedian + 1;

    cout << minimumTotalDistance << '\n';
    cout << numberOfOptimalIntegerPositions << '\n';
    return 0;
}
```

Complexity:

```text
O(N log N)
```

---

## 5.9 Recognition / Don't Memorize

Recognize:

```text
sum of absolute distances
→ median
```

Remember why:

```text
move right:

cost change
=
points on left
-
points on right
```

---

# 6. Weighted Median

## 6.1 What It Asks

Minimize:

```math
\sum_i
\mathrm{pointWeight}_i
\cdot
\left|
\mathrm{meetingPosition}
-
\mathrm{pointPosition}_i
\right|
```

---

## 6.2 Concept Simplified

Example:

```text
positions = [1,3,7]
weights   = [1,1,3]
```

Conceptual expansion:

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

## 6.3 Verify

Meeting at 3:

```text
1*|3-1|
+
1*|3-3|
+
3*|3-7|

=
2+0+12
=
14
```

Meeting at 7:

```text
1*6
+
1*4
+
3*0

=
10
```

So 7 is better.

---

## 6.4 Weighted Balance Proof

Replace:

```text
number of points
```

with:

```text
total weight
```

Moving right changes cost according to:

```text
weightOnLeft
-
weightOnRight
```

Thus the optimum is where total weight is balanced.

For positive integer weights:

```text
weighted median
=
first position where cumulative weight
reaches at least half the total weight
```

---

## 6.5 Prefix Example

```text
totalWeight
=
1+1+3
=
5

requiredPrefixWeight
=
ceil(5/2)
=
3
```

Cumulative:

```text
position 1 → 1
position 3 → 2
position 7 → 5
```

First cumulative value `>= 3`:

```text
position 7
```

---

## 6.6 Algorithm + C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

struct WeightedPoint {
    long long pointPosition;
    long long pointWeight;
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfPoints;
    cin >> numberOfPoints;

    vector<WeightedPoint> points(numberOfPoints);

    for (WeightedPoint& point : points) {
        cin >> point.pointPosition
            >> point.pointWeight;
    }

    sort(
        points.begin(),
        points.end(),
        [](const WeightedPoint& firstPoint,
           const WeightedPoint& secondPoint) {
            return firstPoint.pointPosition
                 < secondPoint.pointPosition;
        }
    );

    long long totalWeight = 0;

    for (const WeightedPoint& point : points)
        totalWeight += point.pointWeight;

    long long requiredPrefixWeight =
        (totalWeight + 1) / 2;

    long long currentPrefixWeight = 0;
    long long weightedMedianPosition = 0;

    for (const WeightedPoint& point : points) {
        currentPrefixWeight += point.pointWeight;

        if (currentPrefixWeight >= requiredPrefixWeight) {
            weightedMedianPosition = point.pointPosition;
            break;
        }
    }

    long long minimumWeightedDistance = 0;

    for (const WeightedPoint& point : points) {
        minimumWeightedDistance +=
            point.pointWeight
            * llabs(
                point.pointPosition
                - weightedMedianPosition
            );
    }

    cout << weightedMedianPosition << '\n';
    cout << minimumWeightedDistance << '\n';
    return 0;
}
```

Complexity:

```text
O(N log N)
```

---

## 6.7 Recognition / Don't Memorize

```text
weight * absolute distance
→ weighted median
```

Remember:

```text
weight
=
conceptual number of copies / votes
```

---

# 7. Manhattan Meeting Point

## 7.1 What It Asks

For points:

```text
(pointX, pointY)
```

choose:

```text
(meetingX, meetingY)
```

minimizing:

```text
sum(
    |meetingX - pointX|
    +
    |meetingY - pointY|
)
```

---

## 7.2 Key Derivation

Start:

```math
\mathrm{totalDistance}
=
\sum_i
\left(
\left|
\mathrm{meetingX}
-
\mathrm{pointX}_i
\right|
+
\left|
\mathrm{meetingY}
-
\mathrm{pointY}_i
\right|
\right)
```

Separate the two sums:

```math
\mathrm{totalDistance}
=
\sum_i
\left|
\mathrm{meetingX}
-
\mathrm{pointX}_i
\right|
+
\sum_i
\left|
\mathrm{meetingY}
-
\mathrm{pointY}_i
\right|
```

The first part depends only on `meetingX`.

The second depends only on `meetingY`.

Therefore:

```text
meetingX
=
median of x-coordinates

meetingY
=
median of y-coordinates
```

---

## 7.3 Tiny Example

Points:

```text
(1,1)
(3,4)
(7,2)
```

X:

```text
[1,3,7]
→ medianX = 3
```

Y:

```text
[1,4,2]
→ sorted [1,2,4]
→ medianY = 2
```

Meeting point:

```text
(3,2)
```

Distances:

```text
to (1,1):
2+1 = 3

to (3,4):
0+2 = 2

to (7,2):
4+0 = 4
```

Total:

```text
9
```

---

## 7.4 ASCII

```text
y
^
|
|         Point B
|          (3,4)
|            *
|            |
|         Meeting -------- Point C
|          (3,2)           (7,2)
|            *
|            |
| Point A ---+
|  (1,1)
|
+---------------------------------> x
```

---

## 7.5 Even Count — Rectangle of Optima

Suppose:

```text
x median interval = [3,5]
y median interval = [4,7]
```

Every point inside:

```text
3 <= meetingX <= 5
4 <= meetingY <= 7
```

is optimal.

Number of optimal integer points:

```text
(upperMedianX - lowerMedianX + 1)
*
(upperMedianY - lowerMedianY + 1)
```

Example:

```text
x choices:
3,4,5
→ 3

y choices:
4,5,6,7
→ 4

total:
3*4
=
12
```

---

## 7.6 Algorithm + C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfPoints;
    cin >> numberOfPoints;

    vector<long long> xCoordinates(numberOfPoints);
    vector<long long> yCoordinates(numberOfPoints);

    for (int index = 0; index < numberOfPoints; ++index) {
        cin >> xCoordinates[index]
            >> yCoordinates[index];
    }

    sort(xCoordinates.begin(), xCoordinates.end());
    sort(yCoordinates.begin(), yCoordinates.end());

    long long lowerMedianX =
        xCoordinates[(numberOfPoints - 1) / 2];

    long long upperMedianX =
        xCoordinates[numberOfPoints / 2];

    long long lowerMedianY =
        yCoordinates[(numberOfPoints - 1) / 2];

    long long upperMedianY =
        yCoordinates[numberOfPoints / 2];

    long long meetingX = upperMedianX;
    long long meetingY = upperMedianY;

    long long minimumTotalDistance = 0;

    for (long long xCoordinate : xCoordinates)
        minimumTotalDistance +=
            llabs(xCoordinate - meetingX);

    for (long long yCoordinate : yCoordinates)
        minimumTotalDistance +=
            llabs(yCoordinate - meetingY);

    long long numberOfOptimalMeetingPoints =
        (upperMedianX - lowerMedianX + 1)
        * (upperMedianY - lowerMedianY + 1);

    cout << meetingX << ' ' << meetingY << '\n';
    cout << minimumTotalDistance << '\n';
    cout << numberOfOptimalMeetingPoints << '\n';
    return 0;
}
```

Complexity:

```text
O(N log N)
```

---

## 7.7 Recognition / Don't Memorize

Recognize:

```text
2D Manhattan distance
+
choose one meeting point
```

Think:

```text
separate dimensions
→ median x
→ median y
```

Remember the separation formula, not only the final answer.

---

# 8. Proof Pattern Summary

| Pattern | Local Question | Proof | Greedy Result |
|---|---|---|---|
| Minimum Dot Product | same pair or crossed pair? | exchange + factorization | opposite sorting |
| Score–Decay–Time | which of two jobs comes first? | two-job exchange | `decay/time` descending |
| `+1`, `×2` | what was the last operation? | reverse forced choice | odd `-1`, even `/2` |
| Median | should meeting point move right? | left/right balance | median |
| Weighted Median | which side has more total weight? | weighted balance | weighted median |
| Manhattan | can coordinates separate? | algebraic separation | median x + median y |

---

# 9. Recognition Checklist

```text
1. Rearranging two arrays?
   → exchange / rearrangement.

2. Sum of pair products?
   → MAX same order, MIN opposite order.

3. Scheduling with duration + time penalty?
   → compare two jobs.

4. Ratio derived?
   → cross multiply.

5. Forward operations unclear?
   → reverse.

6. Odd/even determines previous move?
   → forced reverse greedy.

7. Sum of absolute distances?
   → median.

8. Weight * absolute distance?
   → weighted median.

9. Manhattan in 2D?
   → separate x and y.

10. Even number of points?
    → interval / rectangle of optimal answers.

11. Interval / sweep line / priority mentioned only as a class family?
    → keep as recognition category until its specific derivation is learned.
```

---

# 10. Compact Revision Card

```text
ALGOZENITH GREEDY TECHNIQUES — CLASS 1
=======================================

PATTERN MAP
-----------
Sorting-Based
├── interval / sweep line
├── priority
├── rearrangement
└── ratio ordering

Operation Handling
└── forward / reverse

Classical
├── median
├── weighted median
└── Manhattan


1. MIN DOT PRODUCT
------------------
same-crossed
=
(largerFirst-smallerFirst)
*
(largerSecond-smallerSecond)
>= 0

MIN → opposite order
MAX → same order


2. SCORE–DECAY–TIME
-------------------
first before second iff:

firstDecay*secondTime
>=
secondDecay*firstTime

Therefore:

sort decay/time descending


3. +1 / ×2
-----------
reverse:

odd  → target--
even → target/=2

positive target formula:

bitLength(target)-1
+
popcount(target)


4. MEDIAN
---------
minimize:

sum |meeting-position|

move right:

change
=
pointsOnLeft
-
pointsOnRight

answer:
median


5. WEIGHTED MEDIAN
------------------
minimize:

sum(
 weight
 *
 |meeting-position|
)

weight
=
conceptual copies

first cumulative weight
crossing half total
→ weighted median


6. MANHATTAN
------------
sum(
 |meetingX-pointX|
 +
 |meetingY-pointY|
)

=

sum |meetingX-pointX|
+
sum |meetingY-pointY|

Therefore:

meetingX = median X
meetingY = median Y
```

---

# Final Mental Model

```text
                    GREEDY
                       |
        +--------------+--------------+
        |              |              |
    REORDER         OPERATIONS      LOCATION
        |              |              |
        v              v              v
 compare 2 items    reverse         |distance|
        |              |              |
        v              v              v
 exchange / ratio   parity          median
                                      |
                                      v
                           weighted / Manhattan
```

> **Core rule:** derive the greedy choice from a small proof.  
> Do not memorize a sorting key before understanding the two-choice comparison that creates it.
