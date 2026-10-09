# AlgoZenith Greedy Applications
## TLE-Style Notes — Running Minimum + Bottleneck Sorting + Top-K Heap

> **Source:** the supplied AlgoZenith Greedy Applications class screenshots.
>
> **Goal:** understand the *reason* behind the greedy choice instead of memorizing:
>
> ```text
> "take prefix minimum"
>
> or
>
> "sort by efficiency and use a heap"
> ```
>
> **Format used for each problem:**
>
> ```text
> what it asks
> → concept simplified
> → prerequisites
> → variables with full names
> → tiny example
> → observation
> → greedy claim
> → proof / exchange argument
> → derivation with actual numbers
> → ASCII visualization
> → step-by-step dry run
> → C++17 using long long
> → complexity
> → recognition model
> → don't-memorize model
> ```
>
> **Variable rule:** descriptive names are used throughout, such as:
>
> ```text
> minimumPriceSeen
> distanceToNextStation
> selectedStrengthSum
> currentMinimumEfficiency
> teamPerformance
> ```
>
> rather than cryptic one-letter names wherever a full name improves understanding.

---

# Clickable Table of Contents

- [0. Class Map](#0-class-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 Greedy Choice Must Be Safe](#11-greedy-choice-must-be-safe)
  - [1.2 Running / Prefix Minimum](#12-running--prefix-minimum)
  - [1.3 Why Future Values Cannot Help the Past](#13-why-future-values-cannot-help-the-past)
  - [1.4 Local Exchange Argument](#14-local-exchange-argument)
  - [1.5 Bottleneck Minimum](#15-bottleneck-minimum)
  - [1.6 Fix the Bottleneck, Optimize the Rest](#16-fix-the-bottleneck-optimize-the-rest)
  - [1.7 Top-K Largest Values](#17-top-k-largest-values)
  - [1.8 Why a Min-Heap Maintains Top-K](#18-why-a-min-heap-maintains-top-k)
  - [1.9 Heap + Running Sum](#19-heap--running-sum)
  - [1.10 Sorting One Dimension to Make Candidates Eligible](#110-sorting-one-dimension-to-make-candidates-eligible)
  - [1.11 Overflow and `long long`](#111-overflow-and-long-long)
  - [1.12 Recognition Checklist](#112-recognition-checklist)
- [2. Pattern 1 — Minimum Travel Cost on a Line](#2-pattern-1--minimum-travel-cost-on-a-line)
- [3. Pattern 2 — Maximum Team Performance](#3-pattern-2--maximum-team-performance)
- [4. How the Two Problems Are Related](#4-how-the-two-problems-are-related)
- [5. Final Pattern Recognition](#5-final-pattern-recognition)
- [6. Compact Revision Card](#6-compact-revision-card)

---

# 0. Class Map

The supplied screenshots contain two important greedy application forms.

```text
GREEDY APPLICATIONS
│
├── 1. Running Best So Far
│      |
│      └── line of stations / segments
│          → every new segment can use
│            the cheapest valid price seen so far
│
└── 2. Fix a Bottleneck + Optimize Remaining Choices
       |
       └── team of K students
           performance
           =
           sum of strengths
           *
           minimum efficiency

           sort by efficiency
           +
           maintain strongest K-1 previous students
```

The common idea is:

```text
At the current position/candidate,
what information from the past is sufficient
to make the optimal local decision?
```

For Problem 1:

```text
only the minimum price seen so far
```

For Problem 2:

```text
the K-1 largest strengths
among students already eligible
```

---

# 1. Prerequisites

## 1.1 Greedy Choice Must Be Safe

A greedy choice is not:

```text
"I think this is the best option."
```

It is:

```text
"I can replace any worse local choice
with this choice
without harming feasibility,
and the objective becomes same or better."
```

Typical flow:

```text
current candidate
      |
      v
make greedy choice
      |
      v
is it still legal?
      |
     YES
      |
      v
is the answer non-worse?
      |
     YES
      |
      v
choice is safe
```

Both problems in this class use this idea.

---

## 1.2 Running / Prefix Minimum

Given values:

```text
[7, 5, 8, 4]
```

the running minimum is:

```text
index:               0   1   2   3
value:               7   5   8   4

minimum seen so far: 7   5   5   4
```

Calculation:

```text
minimumPriceSeen = 7

see 5:
min(7,5)
=
5

see 8:
min(5,8)
=
5

see 4:
min(5,4)
=
4
```

Code pattern:

```cpp
long long minimumValueSeen = values[0];

for (...) {
    minimumValueSeen =
        min(minimumValueSeen, values[index]);
}
```

### Why This Is Useful

If the current decision only needs:

```text
minimum among everything already seen
```

we do **not** need to scan all previous values again.

Instead maintain one state:

```text
minimumValueSeen
```

---

## 1.3 Why Future Values Cannot Help the Past

This is critical in left-to-right greedy problems.

Suppose prices are:

```text
station 1 price = 7
station 2 price = 5
station 3 price = 8
station 4 price = 2
```

When traveling:

```text
station 1 → station 2
```

the future cheap price:

```text
2 at station 4
```

cannot pay for a segment already traveled.

So for each segment, valid candidates are only:

```text
current station
+
previous stations
```

not future stations.

Visual:

```text
PAST / AVAILABLE                  FUTURE / NOT AVAILABLE YET

station1 ---- station2 ---- station3 ---- station4
   7             5             8             2
   ^             ^
   |_____________|
 can affect current segment

                                             X
                                  cannot affect old segments
```

Therefore the correct structure is:

```text
prefix minimum
```

not:

```text
global minimum
```

---

## 1.4 Local Exchange Argument

Suppose a local requirement can be satisfied by either:

```text
expensive valid choice
```

or:

```text
cheaper valid choice
```

and changing the source does not break feasibility.

Then replace:

```text
expensive choice
→
cheaper choice
```

### Tiny Example

One unit can be bought at:

```text
price 7
```

or at an already visited station for:

```text
price 5
```

Old local cost:

```text
1 * 7
=
7
```

Greedy local cost:

```text
1 * 5
=
5
```

Difference:

```text
7 - 5
=
2
>= 0
```

Therefore buying that unit at price `7` cannot be better.

This is the proof idea behind the running-minimum travel problem.

---

## 1.5 Bottleneck Minimum

Suppose team performance is:

```text
teamPerformance
=
sumOfSelectedStrengths
*
minimumSelectedEfficiency
```

Example team:

```text
strengths:
7, 5, 3

efficiencies:
10, 4, 8
```

Then:

```text
sumOfSelectedStrengths
=
7 + 5 + 3
=
15
```

Minimum efficiency:

```text
min(10,4,8)
=
4
```

Performance:

```text
15 * 4
=
60
```

The important part is:

```text
only the SMALLEST efficiency
affects the efficiency factor
```

So that student is the team's:

```text
bottleneck
```

---

## 1.6 Fix the Bottleneck, Optimize the Rest

This is one of the most useful greedy transformations.

Suppose we temporarily say:

```text
currentMinimumEfficiency
=
5
```

Then team performance becomes:

```text
teamPerformance
=
selectedStrengthSum
*
5
```

The multiplier `5` is now fixed.

Therefore maximizing performance is exactly the same as maximizing:

```text
selectedStrengthSum
```

among students whose efficiency is at least `5`.

So:

```text
fix minimum efficiency
        |
        v
efficiency factor becomes constant
        |
        v
maximize only strength sum
```

This is the key modeling step in the team problem.

---

## 1.7 Top-K Largest Values

Suppose eligible strengths are:

```text
[7, 3, 5, 2, 9]
```

and we need the largest:

```text
K = 3
```

The answer is:

```text
9, 7, 5
```

Sum:

```text
9 + 7 + 5
=
21
```

If values arrive one by one, repeatedly sorting everything is unnecessary.

We can maintain the current largest `K` values with a heap.

---

## 1.8 Why a Min-Heap Maintains Top-K

To keep the largest `K` values, use a **min-heap**.

At first this may feel backward:

```text
"Why min-heap when I want maximum values?"
```

Because when we have too many selected values, we need to remove:

```text
the smallest among the selected values
```

A min-heap gives exactly that in:

```text
O(log K)
```

### Example — Keep Top 2

Stream:

```text
7, 3, 5, 2
```

Start:

```text
heap = []
```

Insert `7`:

```text
heap = [7]
```

Insert `3`:

```text
heap = [3,7]
```

Insert `5`:

```text
heap = [3,7,5]

size = 3 > 2
```

Remove smallest:

```text
remove 3
```

Now:

```text
heap contains:
5,7
```

These are the top 2 values seen so far.

Insert `2`:

```text
heap temporarily:
2,5,7
```

Remove smallest:

```text
2
```

Still:

```text
5,7
```

---

## 1.9 Heap + Running Sum

If we repeatedly need:

```text
sum of values stored in the heap
```

do not recalculate the heap sum every time.

Maintain:

```text
selectedStrengthSum
```

When inserting:

```text
selectedStrengthSum += newStrength
```

When removing the smallest:

```text
selectedStrengthSum -= smallestStrength
```

Example:

```text
heap keeps top 2

insert 7:
sum = 7

insert 3:
sum = 10

insert 5:
sum = 15

remove 3:
sum = 12
```

Now top-2 strengths are:

```text
7 and 5
```

and:

```text
selectedStrengthSum
=
12
```

available in O(1).

---

## 1.10 Sorting One Dimension to Make Candidates Eligible

For team performance:

```text
performance
=
sumStrength
*
minimumEfficiency
```

Suppose students are processed by efficiency from high to low.

At a current student with:

```text
currentEfficiency = 5
```

every previously processed student has:

```text
efficiency >= 5
```

Therefore any previous student can join a team whose minimum efficiency is `5`.

ASCII:

```text
efficiency:

HIGH ------------------------------------------ LOW

processed already      current       future
[ >= current ]           5         [ < current ]
       |                  |
       +------------------+
        eligible partners
```

This sorting step converts:

```text
"find students whose efficiency is at least current"
```

into:

```text
"all previously processed students"
```

That is why sorting is so powerful here.

---

## 1.11 Overflow and `long long`

These notes use:

```cpp
long long
```

as requested.

Always estimate:

```text
maximum multiplier
*
maximum sum
```

Example:

```text
selectedStrengthSum = 10^9
minimumEfficiency   = 10^9

performance
=
10^18
```

This fits in signed `long long`.

But if the actual constraints allow a larger strength sum, check again.

Safe contest habit:

```text
largest single value
×
number of values
×
largest multiplier
```

before deciding the numeric type.

---

## 1.12 Recognition Checklist

When reading a new problem, ask:

```text
1. Does the current decision depend on
   minimum/maximum among everything seen so far?
   → running min/max.

2. Can future values affect past decisions?
   If NO:
   → prefix state may be enough.

3. Is the objective:
      SUM * MIN
   or
      SUM * MAX?
   → try fixing the bottleneck.

4. Once the bottleneck is fixed,
   does the rest become "choose largest K values"?
   → top-K heap.

5. Do candidates become valid after sorting
   one dimension?
   → sort + sweep.

6. Need top K largest values dynamically?
   → min-heap of size K.

7. Need heap sum repeatedly?
   → maintain running sum.
```

---

# 2. Pattern 1 — Minimum Travel Cost on a Line

## 2.1 What the Class Model Shows

The board shows:

```text
N stations / points arranged on a line
```

Each station has a price:

```text
priceAtStation
```

Each consecutive pair has a distance:

```text
distanceToNextStation
```

A car starts from the left and travels to station `N`.

Visual form from the class:

```text
Station 1      Station 2      Station 3       ...      Station N
 price_1        price_2        price_3
    O-------------O--------------O-------------------------O
        dist_1          dist_2          dist_3 ...

    car →
```

The board's greedy expression is the important part:

```text
segment distance
*
minimum valid previous price
```

So each segment uses the cheapest price seen before entering that segment.

> **Model assumption needed by this formula:** fuel bought at an earlier station can be carried forward without a restrictive tank-capacity constraint. If a separate problem adds tank capacity or limited purchase, the greedy rule must be reconsidered.

---

## 2.2 What It Asks

For each road segment:

```text
distanceToNextStation[index]
```

we need enough fuel to travel that segment.

At station `index`, the fuel could have been bought from any already visited station.

Goal:

```text
minimize total travel cost
```

---

## 2.3 Variables — Full Names

```text
numberOfStations

fuelPrice[index]

distanceToNextStation[index]

minimumPriceSeen

minimumTravelCost
```

If there are `N` stations, there are normally:

```text
N - 1
```

segments.

---

## 2.4 Concept Simplified

Suppose current segment needs:

```text
3 units of fuel
```

Available previous prices:

```text
7, 5, 8
```

Why buy those 3 units at:

```text
8
```

if we could have bought them earlier at:

```text
5
```

?

So the segment should be charged at:

```text
minimumPriceSeen
=
min(all prices available so far)
```

---

## 2.5 Tiny Example

Stations:

```text
fuel prices:

[7, 5, 8, 4]
```

Distances:

```text
[2, 3, 1]
```

ASCII:

```text
price 7          price 5          price 8          price 4
   O----------------O----------------O----------------O
        dist 2           dist 3           dist 1
```

### Segment 1

Available prices:

```text
[7]
```

Cheapest:

```text
7
```

Cost:

```text
2 * 7
=
14
```

### Segment 2

Available prices:

```text
[7,5]
```

Cheapest:

```text
5
```

Cost:

```text
3 * 5
=
15
```

### Segment 3

Available prices:

```text
[7,5,8]
```

Cheapest:

```text
5
```

Notice:

```text
current station price = 8
```

but fuel bought earlier at price `5` is cheaper.

Cost:

```text
1 * 5
=
5
```

Total:

```text
14 + 15 + 5
=
34
```

---

## 2.6 Greedy Claim

For every segment:

```text
minimum segment cost
=
distanceToNextStation
*
minimumPriceSeen
```

where:

```text
minimumPriceSeen
=
minimum fuel price among stations
already reachable before this segment
```

Therefore:

```text
minimumTravelCost

=
sum over every segment
(
    distanceToNextStation
    *
    runningMinimumFuelPrice
)
```

---

## 2.7 Why the Current Station Price Alone Is Not Enough

Wrong idea:

```text
pay each segment using
the price at the station
where the segment begins
```

Example:

```text
prices:
[7,5,8]

current segment begins at price 8
```

But we already passed a station with:

```text
price 5
```

If earlier fuel can be carried:

```text
5 < 8
```

so buying earlier is better.

Thus we need:

```text
minimum price seen so far
```

not:

```text
current price
```

---

## 2.8 Why the Global Minimum Is Also Wrong

Suppose:

```text
prices:
[7,5,8,2]
```

Global minimum:

```text
2
```

But station `4` lies in the future.

We cannot use its price for:

```text
station 1 → station 2
```

or:

```text
station 2 → station 3
```

before reaching it.

So the correct valid set is:

```text
prefix of stations
```

This gives:

```text
prefix minimum
```

---

## 2.9 Exchange Proof — Step by Step

Consider one specific segment.

Suppose its distance is:

```text
currentSegmentDistance
```

and a solution pays using:

```text
somePreviousPrice
```

but we know:

```text
minimumPriceSeen
<=
somePreviousPrice
```

Old segment cost:

```text
oldSegmentCost
=
currentSegmentDistance
*
somePreviousPrice
```

Greedy segment cost:

```text
greedySegmentCost
=
currentSegmentDistance
*
minimumPriceSeen
```

Subtract:

```text
oldSegmentCost
-
greedySegmentCost

=
currentSegmentDistance
*
somePreviousPrice

-
currentSegmentDistance
*
minimumPriceSeen
```

Factor:

```text
=
currentSegmentDistance
*
(
    somePreviousPrice
    -
    minimumPriceSeen
)
```

We know:

```text
currentSegmentDistance >= 0
```

and:

```text
somePreviousPrice - minimumPriceSeen >= 0
```

Therefore:

```text
oldSegmentCost
-
greedySegmentCost
>=
0
```

So:

```text
greedySegmentCost
<=
oldSegmentCost
```

Hence using the cheapest valid previous price is never worse.

---

## 2.10 Same Proof With Actual Numbers

Suppose:

```text
currentSegmentDistance = 3

somePreviousPrice = 7

minimumPriceSeen = 5
```

Old cost:

```text
3 * 7
=
21
```

Greedy cost:

```text
3 * 5
=
15
```

Difference:

```text
21 - 15
=
6
```

Factored form:

```text
3 * (7 - 5)

=
3 * 2

=
6
```

Since:

```text
6 >= 0
```

the cheaper previous price is safe.

---

## 2.11 Why the Segment Decisions Combine Globally

Every segment must be traveled.

For each segment, we have proved:

```text
using minimumPriceSeen
is no more expensive than
using any other valid previous price
```

Therefore choosing the cheapest valid price for **every segment** minimizes the sum:

```text
minimumTravelCost
=
segment1Cost
+
segment2Cost
+
...
```

No segment benefits from intentionally paying a higher valid price.

---

## 2.12 Running-Minimum State

We do not need:

```text
min(
    fuelPrice[0],
    fuelPrice[1],
    ...,
    fuelPrice[index]
)
```

from scratch for every segment.

Maintain:

```text
minimumPriceSeen
```

Update:

```text
minimumPriceSeen
=
min(
    minimumPriceSeen,
    fuelPrice[currentStation]
)
```

Then segment cost:

```text
minimumPriceSeen
*
distanceToNextStation[currentStation]
```

---

## 2.13 ASCII Dry Run

Example:

```text
prices:
7       5       8       4

dist:
   2       3       1


      7              5              8              4
      O--------------O--------------O--------------O
car →      2                3              1
```

Running minimum:

```text
station 1:
minimumPriceSeen = 7

segment 1:
2 * 7
=
14
```

Move to station 2:

```text
minimumPriceSeen
=
min(7,5)
=
5
```

Segment 2:

```text
3 * 5
=
15
```

Move to station 3:

```text
minimumPriceSeen
=
min(5,8)
=
5
```

Segment 3:

```text
1 * 5
=
5
```

Total:

```text
14 + 15 + 5
=
34
```

---

## 2.14 Algorithm

```text
minimumPriceSeen
=
fuelPrice[0]

minimumTravelCost
=
0

for each outgoing segment:

    minimumPriceSeen
    =
    min(
        minimumPriceSeen,
        fuelPrice[currentStation]
    )

    minimumTravelCost
    +=
        minimumPriceSeen
        *
        distanceToNextStation[currentStation]
```

---

## 2.15 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfStations;
    cin >> numberOfStations;

    vector<long long> fuelPrice(numberOfStations);
    vector<long long> distanceToNextStation(
        numberOfStations - 1
    );

    for (long long& price : fuelPrice) {
        cin >> price;
    }

    for (long long& distance : distanceToNextStation) {
        cin >> distance;
    }

    long long minimumPriceSeen = fuelPrice[0];
    long long minimumTravelCost = 0;

    for (
        int stationIndex = 0;
        stationIndex < numberOfStations - 1;
        ++stationIndex
    ) {
        minimumPriceSeen =
            min(
                minimumPriceSeen,
                fuelPrice[stationIndex]
            );

        minimumTravelCost +=
            minimumPriceSeen
            * distanceToNextStation[stationIndex];
    }

    cout << minimumTravelCost << '\n';

    return 0;
}
```

---

## 2.16 Complexity

```text
Time:
O(N)

Space:
O(N)
```

If input can be streamed appropriately, the greedy state itself only needs:

```text
O(1)
```

extra space.

---

## 2.17 Recognition Model

When you see:

```text
move left → right
+
every step has a cost
+
a previously seen cheaper option
can still be reused
```

think:

```text
running minimum
```

Typical form:

```text
answer
+=
currentRequirement
*
bestValueSeenSoFar
```

---

## 2.18 Don't-Memorize Model

Do not memorize only:

```text
cost += distance * prefixMinimumPrice
```

Remember why:

```text
For this segment,
all previous prices are valid.

Among valid prices,
the smallest price dominates every larger price.

Future prices are not valid yet.

Therefore:
use the minimum price in the prefix.
```

That reasoning recreates the formula.

---

# 3. Pattern 2 — Maximum Team Performance

## 3.1 What the Class Model Shows

The screenshots show:

```text
N students
choose K students
```

Every student has:

```text
strength
efficiency
```

Team performance is:

```text
(
    sum of selected strengths
)
*
(
    minimum selected efficiency
)
```

Goal:

```text
maximize performance
```

This is the key formula from the board.

---

## 3.2 Variables — Full Names

For each student:

```text
studentStrength
studentEfficiency
```

Global:

```text
numberOfStudents
teamSize

selectedStrengthSum

currentMinimumEfficiency

currentTeamPerformance

maximumTeamPerformance
```

Data structure:

```text
min-heap of strengths
```

The heap stores the strongest:

```text
teamSize - 1
```

previous eligible students.

---

## 3.3 Why This Problem Is Not "Take Largest Strengths"

Suppose:

```text
strengths:
[100, 90]

efficiencies:
[1, 1]
```

Strength sum is huge:

```text
190
```

but minimum efficiency:

```text
1
```

Performance:

```text
190 * 1
=
190
```

Another team might have lower strength but much larger minimum efficiency.

Therefore:

```text
strength alone is not enough
```

---

## 3.4 Why This Problem Is Not "Take Largest Efficiencies"

Suppose:

```text
strength = 1
efficiency = 200
```

This student has huge efficiency.

But if the rest of the team has tiny total strength:

```text
performance
```

may still be small.

Therefore:

```text
efficiency alone is not enough
```

The objective mixes:

```text
SUM
*
MINIMUM
```

That structure is the key.

---

## 3.5 Screenshot Example

The class shows:

```text
strengths:
[1, 100, 100]

efficiencies:
[200, 1, 1]

teamSize:
2
```

Consider team:

```text
strengths:
1 and 100
```

Strength sum:

```text
1 + 100
=
101
```

Minimum efficiency:

```text
min(200,1)
=
1
```

Performance:

```text
101 * 1
=
101
```

Consider the two strength-100 students:

```text
strength sum:
100 + 100
=
200
```

Minimum efficiency:

```text
1
```

Performance:

```text
200 * 1
=
200
```

So:

```text
200
>
101
```

The high efficiency `200` does not automatically make the first team optimal because team efficiency is controlled by the **minimum**.

---

## 3.6 Core Observation — Every Team Has a Bottleneck Student

Take any selected team.

Among its efficiencies:

```text
one value is the minimum
```

Call it:

```text
currentMinimumEfficiency
```

At least one selected student has exactly that efficiency.

That student can be treated as:

```text
the bottleneck student
```

Now imagine enumerating:

```text
which student is the bottleneck?
```

Once that is fixed, the difficult product becomes much easier.

---

## 3.7 Fix the Bottleneck

Suppose current student's efficiency is:

```text
currentMinimumEfficiency
```

If this student is the team's minimum-efficiency member, all other selected students must satisfy:

```text
otherStudentEfficiency
>=
currentMinimumEfficiency
```

The performance is:

```text
currentTeamPerformance

=
(
    currentStudentStrength
    +
    sumOfOtherSelectedStrengths
)
*
currentMinimumEfficiency
```

Now:

```text
currentMinimumEfficiency
```

is fixed.

So maximizing performance means maximizing:

```text
currentStudentStrength
+
sumOfOtherSelectedStrengths
```

We need:

```text
teamSize - 1
```

other students.

Therefore choose:

```text
the strongest teamSize - 1 eligible students
```

---

## 3.8 Why Sort by Efficiency

Sort students by:

```text
efficiency descending
```

Example:

```text
efficiency:

200, 10, 7, 5, 2, 1
```

When processing a student with:

```text
currentEfficiency = 5
```

all previous students have:

```text
efficiency >= 5
```

So every previous student is eligible to join a team whose minimum efficiency is `5`.

ASCII:

```text
sorted by efficiency descending

[ higher efficiency students ][ current ][ lower efficiency students ]
             eligible              5             not eligible
                   \_______________/
                      choose from here
```

This removes the need to search for eligible students.

Eligibility becomes:

```text
"already processed"
```

---

## 3.9 Why We Need the Strongest `K-1`

For the current bottleneck student:

```text
currentEfficiency
```

is fixed.

Performance:

```text
(
    currentStrength
    +
    previousSelectedStrengthSum
)
*
currentEfficiency
```

Since:

```text
currentEfficiency
```

is a positive constant for this candidate, the best team is obtained by maximizing:

```text
previousSelectedStrengthSum
```

with exactly:

```text
teamSize - 1
```

previous students.

Therefore:

```text
take top K-1 strengths
among previous eligible students
```

---

## 3.10 Exchange Proof for Top `K-1` Strengths

Suppose our candidate team contains an eligible previous student with:

```text
selectedSmallerStrength
```

but another eligible unselected student has:

```text
unselectedLargerStrength
```

where:

```text
unselectedLargerStrength
>
selectedSmallerStrength
```

Swap them.

Old strength sum:

```text
oldStrengthSum
```

New strength sum:

```text
newStrengthSum

=
oldStrengthSum
-
selectedSmallerStrength
+
unselectedLargerStrength
```

Difference:

```text
newStrengthSum
-
oldStrengthSum

=
unselectedLargerStrength
-
selectedSmallerStrength
```

Since:

```text
unselectedLargerStrength
>
selectedSmallerStrength
```

we get:

```text
newStrengthSum
>
oldStrengthSum
```

What happens to minimum efficiency?

Nothing bad.

Both students are from the previously processed eligible set, so both satisfy:

```text
efficiency
>=
currentMinimumEfficiency
```

The current bottleneck remains:

```text
currentMinimumEfficiency
```

Therefore:

```text
newPerformance
>
oldPerformance
```

So any solution that does not use the top `K-1` eligible strengths can be improved.

Hence:

```text
top K-1 strengths are optimal
for a fixed bottleneck
```

---

## 3.11 Put Numbers Into the Exchange Proof

Suppose:

```text
currentMinimumEfficiency = 4

currentStudentStrength = 6
```

We need two previous students.

Selected strengths:

```text
7 and 3
```

but an unselected eligible strength is:

```text
5
```

Old sum:

```text
6 + 7 + 3
=
16
```

Old performance:

```text
16 * 4
=
64
```

Swap:

```text
3 → 5
```

New sum:

```text
6 + 7 + 5
=
18
```

New performance:

```text
18 * 4
=
72
```

Difference:

```text
72 - 64
=
8
```

or from the strength difference:

```text
(5 - 3) * 4

=
2 * 4

=
8
```

The bottleneck remained `4`, so replacing the weaker eligible strength is always better.

---

## 3.12 Why a Min-Heap of Size `K-1`

We need to repeatedly know:

```text
the largest K-1 strengths
among previous students
```

A min-heap keeps exactly this set.

Why minimum on top?

Because if a new strength enters and we now have too many values, remove:

```text
the smallest selected strength
```

That preserves the largest ones.

Heap invariant:

```text
heap
=
largest K-1 strengths
among all students processed before current
```

Running sum invariant:

```text
selectedStrengthSum
=
sum of all strengths currently in heap
```

---

## 3.13 Heap Utility — TLE-Style

We can encapsulate the idea:

```cpp
class TopStrengths {
private:
    int maximumCount;

    priority_queue<
        long long,
        vector<long long>,
        greater<long long>
    > minimumHeap;

    long long selectedStrengthSum = 0;

public:
    explicit TopStrengths(int maximumCount)
        : maximumCount(maximumCount) {}

    void insert(long long strength) {
        if (maximumCount == 0) {
            return;
        }

        minimumHeap.push(strength);
        selectedStrengthSum += strength;

        if (
            static_cast<int>(minimumHeap.size())
            > maximumCount
        ) {
            selectedStrengthSum -= minimumHeap.top();
            minimumHeap.pop();
        }
    }

    int size() const {
        return static_cast<int>(minimumHeap.size());
    }

    long long getSum() const {
        return selectedStrengthSum;
    }
};
```

Meaning:

```text
TopStrengths(K-1)
```

always keeps:

```text
largest K-1 strengths seen so far
```

---

## 3.14 Important Order of Operations

When current student is the bottleneck, we need:

```text
current student
+
K-1 PREVIOUS students
```

So:

```text
1. evaluate candidate using current student

2. only after that,
   insert current student's strength
   into the heap for future candidates
```

ASCII:

```text
previous students              current
[ top K-1 strengths ]       [ bottleneck ]
          |                       |
          +----------+------------+
                     |
                     v
              evaluate team
                     |
                     v
          insert current strength
             for later teams
```

This matches the structure shown in the class code.

---

## 3.15 Screenshot Example — Full Dry Run

Input from the board:

```text
strengths:
[1, 100, 100]

efficiencies:
[200, 1, 1]

teamSize:
2
```

Pair students:

```text
Student A:
strength = 1
efficiency = 200

Student B:
strength = 100
efficiency = 1

Student C:
strength = 100
efficiency = 1
```

Sort by efficiency descending:

```text
A: (strength 1,   efficiency 200)
B: (strength 100, efficiency 1)
C: (strength 100, efficiency 1)
```

Need:

```text
teamSize - 1
=
1
```

previous strength in the heap.

### Process A

Current:

```text
strength = 1
efficiency = 200
```

Heap:

```text
[]
```

Not enough previous students to form a team of size 2.

Insert strength:

```text
heap:
[1]

selectedStrengthSum:
1
```

### Process B

Current:

```text
strength = 100
efficiency = 1
```

Heap already has one previous strength:

```text
[1]
```

Candidate team strength:

```text
1 + 100
=
101
```

Current efficiency is the bottleneck:

```text
1
```

Performance:

```text
101 * 1
=
101
```

Best so far:

```text
101
```

Now insert current strength `100`.

Temporary heap:

```text
[1,100]
```

Capacity:

```text
1
```

Remove smallest:

```text
1
```

Heap becomes:

```text
[100]
```

Running sum:

```text
100
```

### Process C

Current:

```text
strength = 100
efficiency = 1
```

Heap:

```text
[100]
```

Candidate strength sum:

```text
100 + 100
=
200
```

Performance:

```text
200 * 1
=
200
```

Best:

```text
max(101,200)
=
200
```

Final answer:

```text
200
```

---

## 3.16 ASCII Dry Run

```text
SORTED BY EFFICIENCY DESCENDING

Student A          Student B          Student C

strength 1         strength 100       strength 100
eff 200            eff 1             eff 1

     |
     v

K = 2
heap capacity = K-1 = 1


Step A:
current = (1,200)

heap before:
[]

not enough previous students

insert 1

heap:
[1]


Step B:
current = (100,1)

heap:
[1]

candidate:
(1 + 100) * 1
=
101

insert 100

heap temporarily:
[1,100]

remove smallest 1

heap:
[100]


Step C:
current = (100,1)

heap:
[100]

candidate:
(100 + 100) * 1
=
200

ANSWER:
200
```

---

## 3.17 General Algorithm

```text
1. Pair every student's:
      strength
      efficiency

2. Sort students by:
      efficiency descending

3. Maintain a min-heap containing:
      top teamSize-1 strengths
   among PREVIOUS students.

4. Maintain:
      selectedStrengthSum

5. For each current student:

      if heap has teamSize-1 students:

          currentTotalStrength
          =
          selectedStrengthSum
          +
          currentStudent.strength

          currentTeamPerformance
          =
          currentTotalStrength
          *
          currentStudent.efficiency

          update maximum

      insert currentStudent.strength
      into top-(teamSize-1) heap

6. Return maximum performance.
```

---

## 3.18 C++17 — Clean Full-Name Version

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Student {
    long long strength;
    long long efficiency;
};

class TopStrengths {
private:
    int maximumCount;

    priority_queue<
        long long,
        vector<long long>,
        greater<long long>
    > minimumHeap;

    long long selectedStrengthSum = 0;

public:
    explicit TopStrengths(int maximumCount)
        : maximumCount(maximumCount) {}

    void insert(long long strength) {
        if (maximumCount == 0) {
            return;
        }

        minimumHeap.push(strength);
        selectedStrengthSum += strength;

        if (
            static_cast<int>(minimumHeap.size())
            > maximumCount
        ) {
            selectedStrengthSum -= minimumHeap.top();
            minimumHeap.pop();
        }
    }

    int size() const {
        return static_cast<int>(minimumHeap.size());
    }

    long long getSum() const {
        return selectedStrengthSum;
    }
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfStudents;
    int teamSize;

    cin >> numberOfStudents >> teamSize;

    vector<Student> students(numberOfStudents);

    for (Student& student : students) {
        cin >> student.strength >> student.efficiency;
    }

    sort(
        students.begin(),
        students.end(),
        [](const Student& firstStudent,
           const Student& secondStudent) {
            if (
                firstStudent.efficiency
                !=
                secondStudent.efficiency
            ) {
                return firstStudent.efficiency
                     > secondStudent.efficiency;
            }

            return firstStudent.strength
                 > secondStudent.strength;
        }
    );

    TopStrengths strongestPreviousStudents(
        teamSize - 1
    );

    long long maximumTeamPerformance = 0;

    for (const Student& currentStudent : students) {
        if (
            strongestPreviousStudents.size()
            ==
            teamSize - 1
        ) {
            long long currentTotalStrength =
                strongestPreviousStudents.getSum()
                +
                currentStudent.strength;

            long long currentTeamPerformance =
                currentTotalStrength
                *
                currentStudent.efficiency;

            maximumTeamPerformance =
                max(
                    maximumTeamPerformance,
                    currentTeamPerformance
                );
        }

        strongestPreviousStudents.insert(
            currentStudent.strength
        );
    }

    cout << maximumTeamPerformance << '\n';

    return 0;
}
```

---

## 3.19 Same Algorithm Without a Helper Class

If you prefer the contest version:

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Student {
    long long strength;
    long long efficiency;
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfStudents;
    int teamSize;

    cin >> numberOfStudents >> teamSize;

    vector<Student> students(numberOfStudents);

    for (Student& student : students) {
        cin >> student.strength >> student.efficiency;
    }

    sort(
        students.begin(),
        students.end(),
        [](const Student& firstStudent,
           const Student& secondStudent) {
            return firstStudent.efficiency
                 > secondStudent.efficiency;
        }
    );

    priority_queue<
        long long,
        vector<long long>,
        greater<long long>
    > strongestPreviousStrengths;

    long long previousStrengthSum = 0;
    long long maximumTeamPerformance = 0;

    for (const Student& currentStudent : students) {
        if (
            static_cast<int>(
                strongestPreviousStrengths.size()
            )
            ==
            teamSize - 1
        ) {
            long long currentTotalStrength =
                previousStrengthSum
                +
                currentStudent.strength;

            long long currentTeamPerformance =
                currentTotalStrength
                *
                currentStudent.efficiency;

            maximumTeamPerformance =
                max(
                    maximumTeamPerformance,
                    currentTeamPerformance
                );
        }

        if (teamSize > 1) {
            strongestPreviousStrengths.push(
                currentStudent.strength
            );

            previousStrengthSum +=
                currentStudent.strength;

            if (
                static_cast<int>(
                    strongestPreviousStrengths.size()
                )
                >
                teamSize - 1
            ) {
                previousStrengthSum -=
                    strongestPreviousStrengths.top();

                strongestPreviousStrengths.pop();
            }
        }
    }

    cout << maximumTeamPerformance << '\n';

    return 0;
}
```

The helper-class version is better for learning.

The direct version is shorter for contests.

---

## 3.20 Complexity

Sorting:

```text
O(N log N)
```

Each student enters the heap once and may cause one removal:

```text
O(log K)
```

Total heap work:

```text
O(N log K)
```

Overall:

```text
O(N log N)
```

because sorting dominates.

Heap memory:

```text
O(K)
```

---

## 3.21 Why Sorting + Heap Is the Right Combination

We have two requirements:

```text
1. Respect minimum efficiency.

2. Maximize strength sum.
```

Sorting solves requirement 1:

```text
previous students
=
students with efficiency
>=
current efficiency
```

Heap solves requirement 2:

```text
among those previous students,
keep only strongest K-1
```

Together:

```text
SORT
fixes eligibility

HEAP
optimizes strength
```

---

## 3.22 Common Wrong Approach — Sort Only by Strength

Suppose we simply choose the largest strengths.

Problem:

```text
one very low-efficiency student
can reduce the entire team's
minimum efficiency
```

Because:

```text
teamPerformance
=
strengthSum
*
minimumEfficiency
```

A large strength may not compensate for collapsing the minimum efficiency.

---

## 3.23 Common Wrong Approach — Sort Only by Efficiency

Choosing the largest efficiencies ignores:

```text
strength sum
```

A high-efficiency student with tiny strength may be worse than a slightly lower-efficiency bottleneck paired with huge strengths.

We need to test each possible bottleneck.

---

## 3.24 Common Wrong Approach — Heap of Size K Before Fixing Bottleneck

The important reasoning is:

```text
current student is the bottleneck candidate
```

So we need:

```text
current student
+
K-1 previous students
```

This makes the proof direct.

The class code follows this structure:

```text
evaluate current
then
insert current for future candidates
```

Do not lose this invariant while coding.

---

## 3.25 Recognition Model

When you see an objective like:

```text
(
    SUM of selected values
)
*
(
    MINIMUM selected key
)
```

think:

```text
1. Which selected item determines the minimum?

2. Enumerate that bottleneck by sorting.

3. Once bottleneck is fixed,
   maximize the SUM.

4. If choosing K items:
   maintain top K-1 values
   among eligible previous items.
```

This is much more reusable than memorizing a specific team problem.

---

## 3.26 Don't-Memorize Model

Do not memorize only:

```text
sort efficiency descending
+
min-heap
```

Remember the derivation:

```text
Every team has a minimum-efficiency student.
                |
                v
Pretend current student is that bottleneck.
                |
                v
All teammates must have
efficiency >= current efficiency.
                |
                v
Sort descending:
all such candidates are in the prefix.
                |
                v
Current efficiency is now fixed.
                |
                v
Maximize only strength sum.
                |
                v
Take strongest K-1 previous strengths.
                |
                v
Min-heap maintains them efficiently.
```

If you remember this chain, you can recreate the algorithm in a contest.

---

# 4. How the Two Problems Are Related

At first the problems look unrelated:

```text
car + petrol prices
```

versus:

```text
students + team performance
```

But the greedy modeling is similar.

## Problem 1

At each segment:

```text
all previous prices are candidates
```

We need:

```text
BEST ONE
=
minimum
```

So maintain:

```text
running minimum
```

## Problem 2

At each bottleneck student:

```text
all previous students are eligible candidates
```

We need:

```text
BEST K-1
=
largest strengths
```

So maintain:

```text
top K-1 min-heap
```

General pattern:

```text
PROCESS LEFT TO RIGHT
        |
        v
prefix becomes eligible set
        |
        v
keep only sufficient summary of prefix
        |
        +----------------------+
        |                      |
        v                      v
 one best value          top K best values
        |                      |
        v                      v
 running min/max             heap
```

This is an important contest-recognition pattern.

---

# 5. Final Pattern Recognition

## 5.1 Running Best So Far

Signal:

```text
For the current position,
I may use any earlier option.
```

If objective wants:

```text
cheapest
```

maintain:

```text
prefix minimum
```

If objective wants:

```text
largest
```

maintain:

```text
prefix maximum
```

---

## 5.2 Sort + Sweep + Heap

Signal:

```text
Each item has two properties.

One property controls eligibility / bottleneck.

The other property should be maximized among eligible items.
```

Think:

```text
sort by bottleneck property
+
sweep
+
heap for best K values
```

---

## 5.3 SUM × MIN Form

Signal:

```text
score
=
SUM(selected contribution)
*
MIN(selected bottleneck)
```

Model:

```text
fix MIN
→ SUM becomes the only part left to optimize
```

This is the key transformation.

---

## 5.4 Questions to Ask in Contest

```text
1. What part of the objective acts as a bottleneck?

2. Can I fix/enumerate that bottleneck by sorting?

3. After fixing it,
   what remains to maximize/minimize?

4. Do I need:
      one best previous value?
   or:
      top K previous values?

5. Can the prefix be summarized by:
      min
      max
      sum
      heap
      multiset?

6. Can an exchange argument prove
   replacing a worse prefix choice
   with a better one is safe?
```

---

# 6. Compact Revision Card

```text
ALGOZENITH GREEDY APPLICATIONS
==============================


1. LINE / PETROL COST
---------------------

Stations:

price_1 --distance_1-- price_2 --distance_2-- ...

For each segment:

valid prices
=
prices already seen

best valid price
=
minimumPriceSeen

cost:

minimumTravelCost
+=
distanceToNextStation
*
minimumPriceSeen

Update:

minimumPriceSeen
=
min(
    minimumPriceSeen,
    currentFuelPrice
)

WHY?

If:

somePreviousPrice
>
minimumPriceSeen

then:

distance
*
somePreviousPrice

>=

distance
*
minimumPriceSeen

Future prices cannot pay for past segments.

Pattern:
PREFIX MINIMUM

Complexity:
O(N)


2. TEAM PERFORMANCE
-------------------

Choose K students.

Each student:

strength
efficiency

Performance:

sumSelectedStrength
*
minimumSelectedEfficiency

CORE TRANSFORMATION:

Every team has a bottleneck:
minimum efficiency.

Fix current student as bottleneck.

Then:

currentEfficiency
=
fixed

So maximize:

strength sum

Eligible teammates:

efficiency
>=
currentEfficiency

Sort:

efficiency descending

Now all previous students are eligible.

Need:

K-1 strongest previous students.

Maintain:

min-heap of size K-1
+
running strength sum

Candidate:

currentTotalStrength
=
previousTopStrengthSum
+
currentStrength

currentPerformance
=
currentTotalStrength
*
currentEfficiency

Evaluate BEFORE inserting current.

Then insert current strength
for future bottlenecks.

Complexity:

sorting:
O(N log N)

heap:
O(N log K)

overall:
O(N log N)


MAIN RECOGNITION
----------------

prefix needs ONE best value
→ running min/max

prefix needs TOP K best values
→ heap

objective:
SUM * MIN
→ fix the MIN bottleneck first

sorting makes:
"eligible values"
become:
"previously processed values"
```

---

# Final Mental Model

```text
                 GREEDY APPLICATION
                        |
              process in useful order
                        |
                        v
               PREFIX = CANDIDATES
                        |
          +-------------+-------------+
          |                           |
          v                           v
 need one best                  need top K best
          |                           |
          v                           v
 running min/max                  heap + sum
          |                           |
          v                           v
 line travel cost               team performance
                                      |
                                      v
                              fix bottleneck first
```

> **Core lesson:** the most important step is not the data structure.  
> First determine **which candidates are valid at the current step** and **what summary of those candidates is sufficient**.  
> The running minimum and the heap are consequences of that reasoning.
