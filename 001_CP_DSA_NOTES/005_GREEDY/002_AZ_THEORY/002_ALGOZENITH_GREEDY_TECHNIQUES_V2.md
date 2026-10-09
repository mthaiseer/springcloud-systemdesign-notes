# AlgoZenith Greedy Techniques — Class 1
## Optimized TLE-Style Notes — Full Variable Names + Inline Examples + ASCII + C++17

> **Goal:** keep the TLE-style clarity—**example first → derivation → proof → dry run → code → recognition**—without repeating the same idea in multiple sections.
>
> **Variable rule:** formulas use descriptive names such as `smallerFirstValue`, `firstDecayPerTime`, `targetValue`, and `meetingPosition`.
>
> **Rendering rule:** long formulas use plain code blocks so full variable names remain readable and no LaTeX macro errors appear.

---

# Clickable TOC

- [0. Greedy Pattern Map](#0-greedy-pattern-map)
- [1. Prerequisites](#1-prerequisites)
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

## 1.1 Greedy = Choice + Proof

A greedy choice is not correct because it “looks best.”

```text
Greedy
=
local choice
+
proof that the choice is safe
```

Safe means:

```text
an optimal solution can contain this choice

or

replacing another local choice by this one
cannot make the answer worse
```

---

## 1.2 Constraint vs Objective

```text
CONSTRAINT
→ what must remain legal

OBJECTIVE
→ what is minimized / maximized
```

Example — dot product:

```text
Constraint:
every value is paired exactly once

Objective:
minimize total pair-product sum
```

A valid exchange must preserve the constraint while keeping the objective same or better.

---

## 1.3 Exchange / Swapping Proof

```text
OPT has a local choice
        |
        v
Greedy wants another choice
        |
        v
swap / replace locally
        |
        v
still feasible?
        |
       YES
        |
        v
answer non-worse?
        |
       YES
        |
        v
greedy choice is safe
```

If the swap removes one inversion, repeat until the whole solution has greedy order.

---

## 1.4 Compare Only Changed Terms

If two candidate solutions differ only at two positions:

```text
wholeAnswer
=
unchanged
+
LOCAL
+
unchanged
```

all unchanged terms cancel.

This is why a global sorting proof often reduces to **two items**.

---

## 1.5 Inversion → Sorting

For desired ascending order, an inversion is:

```text
earlierIndex < laterIndex

but

earlierValue > laterValue
```

Example:

```text
[1,7,4,9]
   ^ ^
```

If every inversion can be swapped without worsening the objective:

```text
repeated swaps
→ no inversions
→ sorted optimal structure
```

---

## 1.6 Ratio Comparison

If a proof gives:

```text
firstDecayPerTime / firstTimeNeeded
>=
secondDecayPerTime / secondTimeNeeded
```

compare without floating point:

```text
firstDecayPerTime * secondTimeNeeded
>=
secondDecayPerTime * firstTimeNeeded
```

Example:

```text
first:  decay=1, time=2
second: decay=2, time=3

1*3 = 3
2*2 = 4

3 < 4
→ second ratio is larger
```

---

## 1.7 Completion Time

```text
timeNeeded
=
duration of one job

completionTime
=
total elapsed time when it finishes
```

If order is:

```text
firstJob → secondJob
```

then:

```text
firstCompletionTime  = firstTimeNeeded
secondCompletionTime = firstTimeNeeded + secondTimeNeeded
```

ASCII:

```text
0---------firstTime----------firstTime+secondTime
| first job |    second job   |
      ^               ^
 first ends       second ends
```

---

## 1.8 Reverse Greedy

When forward actions are ambiguous, reverse them.

Example forward operations:

```text
currentValue + 1
2 * currentValue
```

Backward:

```text
odd target
→ doubling could not create it
→ previous move was +1
→ subtract 1

even target
→ undo doubling
→ divide by 2
```

---

## 1.9 Median / Weighted Median

```text
minimize:
sum |meetingPosition - pointPosition|

→ median
```

Weighted:

```text
minimize:
sum(
    pointWeight
    *
    |meetingPosition - pointPosition|
)

→ weighted median
```

Mental model:

```text
weight 3
≈
3 conceptual copies
```

---

## 1.10 Manhattan Separability

```text
sum(
    |meetingX - pointX|
    +
    |meetingY - pointY|
)

=
sum |meetingX - pointX|
+
sum |meetingY - pointY|
```

Therefore:

```text
meetingX → median of x
meetingY → median of y
```

---

## 1.11 Overflow

Check products before coding:

```text
10^9 * 10^9
=
10^18
```

Use `__int128` when cross products or sums may exceed `long long`.

---

## 1.12 Greedy Proof Checklist

```text
1. What is the objective?
2. What must remain valid?
3. What is the local greedy choice?
4. Can I compare only two choices?
5. What cancels?
6. Can I factor the remaining expression?
7. What are the signs?
8. Does that imply a sorting order?
9. Did a ratio appear? Cross multiply.
10. Are forward operations unclear? Reverse them.
11. Sum of absolute distances? Median.
12. Weighted absolute distance? Weighted median.
13. Manhattan 2D? Separate x and y.
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

    __int128 minimumDotProduct = 0;

    for (int index = 0; index < numberOfValues; ++index) {
        minimumDotProduct +=
            (__int128)firstValues[index]
            * secondValues[index];
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
    __int128 firstCrossProduct =
        (__int128)firstJob.decayPerUnitTime
        * secondJob.timeNeeded;

    __int128 secondCrossProduct =
        (__int128)secondJob.decayPerUnitTime
        * firstJob.timeNeeded;

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
    __int128 totalScore = 0;

    for (const Job& job : jobs) {
        elapsedTime += job.timeNeeded;

        totalScore +=
            (__int128)job.initialScore
            -
            (__int128)job.decayPerUnitTime
            * elapsedTime;
    }
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

```text
sum(
    |meetingPosition - pointPosition|
)
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

    __int128 minimumTotalDistance = 0;

    for (long long pointPosition : pointPositions) {
        minimumTotalDistance +=
            llabs(pointPosition - meetingPosition);
    }

    long long numberOfOptimalIntegerPositions =
        upperMedian - lowerMedian + 1;
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

```text
sum(
    pointWeight
    *
    |meetingPosition - pointPosition|
)
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

    __int128 minimumWeightedDistance = 0;

    for (const WeightedPoint& point : points) {
        minimumWeightedDistance +=
            (__int128)point.pointWeight
            *
            llabs(
                point.pointPosition
                -
                weightedMedianPosition
            );
    }
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

```text
totalDistance
=
sum(
    |meetingX - pointX|
    +
    |meetingY - pointY|
)
```

Separate:

```text
=
sum |meetingX - pointX|

+

sum |meetingY - pointY|
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

    __int128 minimumTotalDistance = 0;

    for (long long xCoordinate : xCoordinates)
        minimumTotalDistance +=
            llabs(xCoordinate - meetingX);

    for (long long yCoordinate : yCoordinates)
        minimumTotalDistance +=
            llabs(yCoordinate - meetingY);

    __int128 numberOfOptimalMeetingPoints =
        (__int128)(upperMedianX - lowerMedianX + 1)
        *
        (upperMedianY - lowerMedianY + 1);
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
