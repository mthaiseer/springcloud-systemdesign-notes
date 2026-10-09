# AlgoZenith Greedy Applications
## Optimized TLE-Style Notes — Running Minimum + Bottleneck Sorting + Top-K Heap

> **Goal:** understand *why* the greedy choice works, not just memorize `prefix minimum` or `sort + heap`.
>
> **Problem flow used throughout:**
>
> ```text
> What it asks
> → small dry run
> → core observation
> → proof with inline numbers
> → one detailed dry run
> → algorithm
> → C++17
> → complexity
> → recognition model
> ```
>
> **Variable rule**
>
> - In explanations/code: descriptive names such as `minimumPriceSeen`, `previousStrengthSum`.
> - In proofs: shorter but readable names such as `minPrice`, `dist`, `currEff`.

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
  - [1.10 Sorting Makes Candidates Eligible](#110-sorting-makes-candidates-eligible)
  - [1.11 Overflow and `long long`](#111-overflow-and-long-long)
  - [1.12 Recognition Checklist](#112-recognition-checklist)
- [2. Pattern 1 — Minimum Travel Cost on a Line](#2-pattern-1--minimum-travel-cost-on-a-line)
- [3. Pattern 2 — Maximum Team Performance](#3-pattern-2--maximum-team-performance)
- [4. Pattern Connection](#4-pattern-connection)
- [5. Compact Revision Card](#5-compact-revision-card)

---

# 0. Class Map

```text
GREEDY APPLICATIONS
│
├── Running Best So Far
│   └── line / segments
│       → use cheapest valid value seen so far
│
└── Fix a Bottleneck + Optimize Rest
    └── choose K students
        score = sum(strength) * min(efficiency)
        → sort by efficiency
        → keep strongest K-1 previous strengths
```

Common question:

```text
At the current position,
what part of the already-seen prefix
is sufficient to make the optimal choice?
```

For these two problems:

```text
Problem 1 → one best value      → running minimum
Problem 2 → best K-1 values     → min-heap + running sum
```

---

# 1. Prerequisites

## 1.1 Greedy Choice Must Be Safe

Greedy is not:

```text
"This looks best now."
```

Greedy is:

```text
local choice
+
proof that replacing a worse valid choice
cannot make the answer worse
```

Basic proof flow:

```text
another valid choice
        |
        v
replace with greedy choice
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
greedy choice is safe
```

---

## 1.2 Running / Prefix Minimum

Example:

```text
values:
[7, 5, 8, 4]

running minimum:
[7, 5, 5, 4]
```

Step by step:

```text
start:
minValue = 7

see 5:
min(7,5) = 5

see 8:
min(5,8) = 5

see 4:
min(5,4) = 4
```

Code pattern:

```cpp
long long minimumValueSeen = values[0];

for (int index = 1; index < values.size(); ++index) {
    minimumValueSeen =
        min(minimumValueSeen, values[index]);
}
```

Use this when:

```text
current decision
depends only on
the minimum among values seen so far
```

---

## 1.3 Why Future Values Cannot Help the Past

Example prices:

```text
station 1 = 7
station 2 = 5
station 3 = 8
station 4 = 2
```

While traveling:

```text
station 1 → station 2
```

the future price `2` at station 4 is not available yet.

So the valid set is:

```text
current + previous stations
```

not:

```text
all stations
```

ASCII:

```text
AVAILABLE PREFIX                     FUTURE

7 -------- 5 -------- 8 -------- 2
^          ^
|__________|                         X

can affect current segment     cannot affect past segment
```

Therefore:

```text
prefix minimum
```

not:

```text
global minimum
```

---

## 1.4 Local Exchange Argument

Suppose one unit can be bought at:

```text
usedPrice = 7
```

but another already-valid choice costs:

```text
minPrice = 5
```

Then:

```text
oldCost
=
1 * 7
=
7

greedyCost
=
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

If feasibility is unchanged, replacing `7` with `5` is always safe.

That is the exact proof shape used in the travel problem.

---

## 1.5 Bottleneck Minimum

Suppose:

```text
teamPerformance
=
sumSelectedStrength
*
minimumSelectedEfficiency
```

Example:

```text
strengths:
7, 5, 3

efficiencies:
10, 4, 8
```

Then:

```text
sumStrength
=
7 + 5 + 3
=
15

minEfficiency
=
min(10,4,8)
=
4

performance
=
15 * 4
=
60
```

Only the smallest efficiency controls the multiplier.

So that student is the team's:

```text
bottleneck
```

---

## 1.6 Fix the Bottleneck, Optimize the Rest

Suppose:

```text
currEff = 5
```

is fixed as the minimum efficiency.

Then:

```text
teamPerformance
=
selectedStrengthSum
*
5
```

The multiplier is fixed.

So maximizing performance is now equivalent to maximizing:

```text
selectedStrengthSum
```

among students satisfying:

```text
efficiency >= 5
```

Mental model:

```text
fix MIN
   |
   v
multiplier becomes constant
   |
   v
maximize SUM
```

---

## 1.7 Top-K Largest Values

Eligible strengths:

```text
[7, 3, 5, 2, 9]
```

Need top:

```text
K = 3
```

Answer:

```text
9, 7, 5
```

Sum:

```text
21
```

If values arrive one by one, maintain the best `K` instead of sorting the entire prefix every time.

---

## 1.8 Why a Min-Heap Maintains Top-K

To keep the **largest K values**, use a **min-heap**.

Reason:

```text
if selected size becomes K+1,
remove the smallest selected value
```

Example — keep top 2:

```text
stream:
7, 3, 5, 2
```

Process:

```text
7
heap = [7]

3
heap = [3,7]

5
heap = [3,7,5]
size > 2
remove 3

heap keeps:
5,7

2
temporary:
2,5,7
remove 2

heap still:
5,7
```

So the heap always contains:

```text
top K values seen so far
```

---

## 1.9 Heap + Running Sum

If we repeatedly need the heap sum, maintain it.

Example:

```text
insert 7:
sum = 7

insert 3:
sum = 10

insert 5:
sum = 15

remove 3:
sum = 12
```

Pattern:

```cpp
sum += insertedValue;

if (heap.size() > K) {
    sum -= heap.top();
    heap.pop();
}
```

Now both are available efficiently:

```text
top-K set
+
sum of top-K
```

---

## 1.10 Sorting Makes Candidates Eligible

For the team problem:

```text
performance
=
sumStrength
*
minEfficiency
```

Sort by efficiency descending.

Suppose current efficiency is:

```text
5
```

Then every previous student has:

```text
efficiency >= 5
```

ASCII:

```text
HIGH efficiency -------------------------- LOW

[ already processed ] [ current ] [ future ]
   all >= current          5        all < current
          |
          v
   eligible teammates
```

Sorting converts:

```text
"find everyone with efficiency >= current"
```

into:

```text
"all previously processed students"
```

---

## 1.11 Overflow and `long long`

These notes use:

```cpp
long long
```

Example:

```text
10^9 * 10^9
=
10^18
```

which fits in signed `long long`.

But always estimate the full expression:

```text
largest value
*
number of selected values
*
largest multiplier
```

before assuming `long long` is safe.

---

## 1.12 Recognition Checklist

```text
1. Current choice depends on minimum/maximum seen so far?
   → running min/max.

2. Future values cannot affect past?
   → prefix state may be sufficient.

3. Objective contains:
      SUM * MIN
   or:
      SUM * MAX
   → fix the bottleneck first.

4. After fixing the bottleneck,
   need the largest K values?
   → heap.

5. One property controls eligibility?
   → sort by that property.

6. Need top K largest dynamically?
   → min-heap of size K.

7. Need their sum repeatedly?
   → maintain running sum.
```

---

# 2. Pattern 1 — Minimum Travel Cost on a Line

## 2.1 What It Asks

There are `N` stations on a line.

Each station has:

```text
fuelPrice[station]
```

Each outgoing road segment has:

```text
segmentDistance[station]
```

The car moves:

```text
left → right
```

Fuel for the current segment can come from the current or any previously visited station.

Goal:

```text
minimize total travel cost
```

> **Class-model assumption:** fuel bought earlier can be carried forward without a restrictive tank-capacity constraint.

---

## 2.2 Small Dry Run First

```text
prices    = [7, 5, 8, 4]
distances = [2, 3, 1]
```

ASCII:

```text
price 7          price 5          price 8          price 4
   O----------------O----------------O----------------O
        dist 2           dist 3           dist 1
car →
```

For every segment, use the cheapest valid price seen so far:

| Segment | Available prices | `minPrice` | Cost |
|---|---|---:|---:|
| 1 | `[7]` | 7 | `2 * 7 = 14` |
| 2 | `[7,5]` | 5 | `3 * 5 = 15` |
| 3 | `[7,5,8]` | 5 | `1 * 5 = 5` |

Total:

```text
14 + 15 + 5
=
34
```

---

## 2.3 Core Observation

For the current segment:

```text
valid prices
=
all prices already seen
```

Among them:

```text
minimum price dominates every larger price
```

Therefore maintain:

```text
minimumPriceSeen
```

and charge:

```text
segmentDistance
*
minimumPriceSeen
```

---

## 2.4 Why Current Price Alone Is Wrong

Example:

```text
prices:
[7,5,8]
```

At the station with price `8`, we have already passed price `5`.

If earlier fuel can be carried:

```text
5 < 8
```

So the current price is not necessarily optimal.

Need:

```text
minimum price seen so far
```

---

## 2.5 Why Global Minimum Is Wrong

Example:

```text
prices:
[7,5,8,2]
```

Global minimum is:

```text
2
```

But station 4 is in the future.

It cannot pay for:

```text
station 1 → station 2
```

or:

```text
station 2 → station 3
```

before we reach it.

Therefore:

```text
valid set = prefix
```

and:

```text
best valid value = prefix minimum
```

---

## 2.6 Exchange Proof — Compact + Inline Example

Proof variables:

```text
dist      = current segment distance
usedPrice = price used by another valid solution
minPrice  = cheapest valid price so far
```

We know:

```text
minPrice <= usedPrice
```

Compare only this segment:

| General proof | Example: `dist=3`, `usedPrice=7`, `minPrice=5` |
|---|---|
| `oldCost = dist * usedPrice` | `3 * 7 = 21` |
| `greedyCost = dist * minPrice` | `3 * 5 = 15` |
| `oldCost - greedyCost` | `21 - 15 = 6` |
| `= dist * (usedPrice - minPrice)` | `= 3 * (7 - 5)` |
| `>= 0` | `= 6 >= 0` |

Why?

```text
dist >= 0
```

and:

```text
usedPrice - minPrice >= 0
```

Therefore:

```text
oldCost >= greedyCost
```

So replacing a more expensive valid price with `minPrice` can never make the answer worse.

ASCII:

```text
valid prices:

7     5     8
      ^
      |
   minPrice

another solution:
uses 7

exchange:
7 → 5

same segment remains feasible
cost decreases
```

---

## 2.7 Why This Proves the Whole Answer

Every segment must be traveled.

For each individual segment, using:

```text
minimumPriceSeen
```

is no more expensive than using any other valid price.

Therefore summing those locally optimal segment costs gives the global minimum:

```text
totalCost
=
segment1Cost
+
segment2Cost
+
...
```

No segment benefits from intentionally using a larger valid price.

---

## 2.8 Detailed Dry Run

Input:

```text
prices:
7       5       8       4

distances:
   2       3       1
```

Visual:

```text
      7              5              8              4
      O--------------O--------------O--------------O
car →      2                3              1
```

### Segment 1

```text
minPrice
=
7

cost
=
2 * 7
=
14
```

### Reach station 2

Update:

```text
minPrice
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

### Reach station 3

Update:

```text
minPrice
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

## 2.9 Algorithm

```text
minPrice
=
fuelPrice[0]

totalCost
=
0

for each segment:

    minPrice
    =
    min(
        minPrice,
        fuelPrice[currentStation]
    )

    totalCost
    +=
        minPrice
        *
        segmentDistance[currentStation]
```

---

## 2.10 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfStations;
    cin >> numberOfStations;

    vector<long long> fuelPrice(numberOfStations);
    vector<long long> segmentDistance(numberOfStations - 1);

    for (long long& price : fuelPrice) {
        cin >> price;
    }

    for (long long& distance : segmentDistance) {
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
            * segmentDistance[stationIndex];
    }

    cout << minimumTravelCost << '\n';

    return 0;
}
```

---

## 2.11 Complexity

```text
Time:
O(N)

Space:
O(N)
```

The greedy state itself uses:

```text
O(1)
```

extra space.

---

## 2.12 Recognition Model

When you see:

```text
move left → right
+
current step may reuse earlier choices
+
future choices are not available yet
+
want cheapest valid value
```

think:

```text
prefix minimum
```

Mental chain:

```text
current segment
→ valid candidates are prefix
→ cheapest prefix value dominates
→ maintain one running minimum
```

Do not memorize only:

```text
cost += distance * prefixMinimum
```

Remember *why the valid set is the prefix*.

---

# 3. Pattern 2 — Maximum Team Performance

## 3.1 What It Asks

There are `N` students.

Each student has:

```text
strength
efficiency
```

Choose exactly:

```text
K students
```

Team performance:

```text
teamPerformance
=
sum(selected strengths)
*
minimum(selected efficiencies)
```

Goal:

```text
maximize teamPerformance
```

---

## 3.2 Small Dry Run First

Class example:

```text
strengths    = [1, 100, 100]
efficiencies = [200, 1, 1]
K = 2
```

Team A:

```text
strengths:
1, 100

sum:
101

minEff:
1

score:
101 * 1
=
101
```

Team B:

```text
strengths:
100, 100

sum:
200

minEff:
1

score:
200 * 1
=
200
```

So:

```text
bestScore
=
200
```

This already shows:

```text
largest efficiency alone is not enough
```

and:

```text
largest strength alone is not a complete rule
```

because the objective combines:

```text
SUM * MIN
```

---

## 3.3 Core Observation — Every Team Has a Bottleneck

Every chosen team has a minimum efficiency.

Call the student achieving it:

```text
bottleneck
```

Suppose current student is the bottleneck.

Then every teammate must have:

```text
teammateEfficiency
>=
currentEfficiency
```

and:

```text
teamPerformance
=
(
    currentStrength
    +
    otherSelectedStrengths
)
*
currentEfficiency
```

For this candidate:

```text
currentEfficiency
```

is fixed.

Therefore we only need to maximize:

```text
strength sum
```

---

## 3.4 Why Sort by Efficiency

Sort students:

```text
efficiency descending
```

Example:

```text
200, 10, 7, 5, 2, 1
```

At current efficiency:

```text
5
```

all previous students satisfy:

```text
efficiency >= 5
```

ASCII:

```text
HIGH -------------------------------------------- LOW

[ previous students ][ current ][ future students ]
    all >= current         5         all < current
          |
          v
   eligible teammates
```

Thus sorting turns eligibility into:

```text
"already processed"
```

---

## 3.5 Why We Need the Strongest `K-1`

For current bottleneck:

```text
currEff
```

is fixed.

So maximizing:

```text
(
    currStrength
    +
    previousStrengthSum
)
*
currEff
```

means maximizing:

```text
previousStrengthSum
```

with exactly:

```text
K - 1
```

previous students.

Therefore choose:

```text
strongest K-1 previous strengths
```

---

## 3.6 Exchange Proof — Compact + Inline Example

Proof variables:

```text
currEff       = current bottleneck efficiency
smallStrength = weaker selected previous strength
largeStrength = stronger eligible previous strength
oldSum        = old team strength sum
```

Suppose:

```text
largeStrength > smallStrength
```

Swap them.

| General proof | Example: `currEff=4`, `smallStrength=3`, `largeStrength=5` |
|---|---|
| `newSum = oldSum - smallStrength + largeStrength` | `16 - 3 + 5 = 18` |
| `newSum - oldSum = largeStrength - smallStrength` | `18 - 16 = 2` |
| bottleneck stays `currEff` | bottleneck stays `4` |
| `newPerf - oldPerf = (largeStrength - smallStrength) * currEff` | `(5 - 3) * 4 = 8` |
| `>= 0` | `8 >= 0` |

Why does the bottleneck stay unchanged?

Both swapped students are previous students after sorting, so:

```text
their efficiency >= currEff
```

Therefore current student is still the minimum-efficiency member.

Hence replacing a weaker eligible strength with a stronger one never hurts.

So for fixed bottleneck:

```text
take top K-1 previous strengths
```

Visual:

```text
eligible previous strengths:

7   3   5

selected:
7,3

but:
5 > 3

swap:
7,3
  ↓
7,5

min efficiency unchanged
sum strength increases
performance increases
```

---

## 3.7 Why a Min-Heap of Size `K-1`

Need repeatedly:

```text
largest K-1 previous strengths
```

Use a min-heap.

Why min-heap?

```text
when size becomes K,
remove the smallest selected strength
```

Invariant:

```text
heap
=
largest K-1 strengths
among previous students
```

Also maintain:

```text
previousStrengthSum
=
sum of heap values
```

So both are immediately available:

```text
top K-1 set
+
their sum
```

---

## 3.8 Important Order of Operations

Current student is the bottleneck candidate.

Therefore candidate team must be:

```text
current student
+
K-1 PREVIOUS students
```

So:

```text
1. evaluate current candidate

2. then insert current strength
   for future bottleneck candidates
```

ASCII:

```text
previous top K-1             current
[ strengths ]             [ bottleneck ]
      |                         |
      +-----------+-------------+
                  |
                  v
             evaluate team
                  |
                  v
      insert current strength
         for future states
```

---

## 3.9 Detailed Dry Run

Input:

```text
strengths    = [1, 100, 100]
efficiencies = [200, 1, 1]
K = 2
```

Pair:

```text
A = (strength 1,   efficiency 200)
B = (strength 100, efficiency 1)
C = (strength 100, efficiency 1)
```

Already sorted by efficiency descending.

Heap capacity:

```text
K - 1
=
1
```

### Step A

Current:

```text
(1,200)
```

Heap before:

```text
[]
```

Not enough previous students to make a team of size 2.

Insert:

```text
1
```

Heap:

```text
[1]
```

Running sum:

```text
1
```

### Step B

Current:

```text
(100,1)
```

Heap:

```text
[1]
```

Candidate:

```text
strengthSum
=
1 + 100
=
101
```

Performance:

```text
101 * 1
=
101
```

Best:

```text
101
```

Now insert current strength `100`.

Temporary heap:

```text
[1,100]
```

Too large for capacity 1.

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

### Step C

Current:

```text
(100,1)
```

Heap:

```text
[100]
```

Candidate:

```text
strengthSum
=
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

Compact visualization:

```text
A(1,200)        B(100,1)        C(100,1)

Step A:
heap []
→ insert 1
heap [1]

Step B:
candidate = (1+100)*1 = 101
insert 100
heap [1,100]
remove 1
heap [100]

Step C:
candidate = (100+100)*1 = 200

ANSWER = 200
```

---

## 3.10 Algorithm

```text
1. Pair each student's:
      strength
      efficiency

2. Sort by:
      efficiency descending

3. Maintain min-heap:
      strongest K-1 previous strengths

4. Maintain:
      previousStrengthSum

5. For each current student:

      if heap contains K-1 values:

          currentStrengthSum
          =
          previousStrengthSum
          +
          current strength

          currentPerformance
          =
          currentStrengthSum
          *
          current efficiency

          update answer

      insert current strength

      if heap size > K-1:
          remove smallest strength
          update running sum
```

---

## 3.11 C++17

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
            long long currentStrengthSum =
                previousStrengthSum
                + currentStudent.strength;

            long long currentPerformance =
                currentStrengthSum
                * currentStudent.efficiency;

            maximumTeamPerformance =
                max(
                    maximumTeamPerformance,
                    currentPerformance
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

---

## 3.12 Complexity

Sorting:

```text
O(N log N)
```

Heap operations:

```text
O(N log K)
```

Overall:

```text
O(N log N)
```

Heap space:

```text
O(K)
```

---

## 3.13 Common Mistakes

| Wrong idea | Why it fails |
|---|---|
| Sort only by strength | one low-efficiency student may collapse the multiplier |
| Sort only by efficiency | ignores the strength-sum part |
| Use all previous strengths | only `K-1` previous teammates are needed |
| Insert current before evaluating | then current can incorrectly appear among its own previous teammates |
| Use max-heap for top-K largest maintenance | we need fast removal of the smallest selected strength |

---

## 3.14 Recognition Model

When the objective looks like:

```text
SUM(selected values)
*
MIN(selected key)
```

think:

```text
1. Which selected item determines the MIN?

2. Sort so every possible bottleneck
   becomes the current item.

3. Once MIN is fixed,
   maximize the SUM.

4. Need K total items?
   → keep best K-1 previous values.

5. Dynamic top K-1?
   → min-heap + running sum.
```

Do not memorize only:

```text
sort efficiency descending
+
min-heap
```

Remember the chain:

```text
team has a bottleneck
→ fix it
→ sorting creates eligible prefix
→ multiplier fixed
→ maximize strength sum
→ top K-1 heap
```

---

# 4. Pattern Connection

The two problems use the same higher-level idea:

```text
PROCESS IN A USEFUL ORDER
          |
          v
prefix becomes the valid candidate set
          |
          v
store only the summary needed from that prefix
```

Problem 1:

```text
need ONE best previous value
→ running minimum
```

Problem 2:

```text
need BEST K-1 previous values
→ min-heap + running sum
```

ASCII:

```text
PREFIX = VALID CANDIDATES
          |
     +----+----+
     |         |
     v         v
 one best   top K best
     |         |
     v         v
 min/max      heap
```

Contest question:

```text
What information from the past
is sufficient for the current decision?
```

That is the reusable greedy idea.

---

# 5. Compact Revision Card

```text
ALGOZENITH GREEDY APPLICATIONS
==============================

1. MINIMUM TRAVEL COST
----------------------

For each segment:

valid prices
=
current + previous stations

best price
=
minimumPriceSeen

cost:
totalCost
+=
segmentDistance
*
minimumPriceSeen

update:
minimumPriceSeen
=
min(
    minimumPriceSeen,
    currentPrice
)

proof:
oldCost - greedyCost
=
dist * (usedPrice - minPrice)
>= 0

pattern:
PREFIX MINIMUM

complexity:
O(N)


2. MAXIMUM TEAM PERFORMANCE
---------------------------

Choose K students.

score:
sumStrength
*
minimumEfficiency

Main transformation:

every team has a bottleneck
→ fix current student as bottleneck

sort:
efficiency descending

then previous students satisfy:
efficiency >= currentEfficiency

current efficiency fixed
→ maximize strength sum

need:
K-1 strongest previous strengths

maintain:
min-heap size K-1
+
running strength sum

candidate:
currentStrengthSum
=
previousStrengthSum
+
currentStrength

currentPerformance
=
currentStrengthSum
*
currentEfficiency

important:
evaluate current
BEFORE
inserting current into heap

proof:
newPerf - oldPerf
=
(largeStrength - smallStrength)
*
currEff
>= 0

complexity:
O(N log N)


MAIN RECOGNITION
----------------

prefix needs ONE best value
→ running min/max

prefix needs TOP K values
→ heap

objective:
SUM * MIN
→ fix MIN first

sorting turns:
"eligible candidates"
into:
"previously processed candidates"
```

---

# Final Mental Model

```text
Greedy application
      |
      v
identify valid candidate set
      |
      v
process so candidates form a prefix
      |
      v
keep only the summary you need
      |
 +----+----+
 |         |
min/max   heap
```

> **Core lesson:** first derive the valid candidate set and the greedy proof.  
> The running minimum and heap are implementation consequences of that reasoning.
