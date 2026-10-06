# Greedy Algorithms — Level 3
## Greedy 2 — Activity Selection, Fractional Knapsack, Kadane, Job Sequencing & Maximum Perimeter Triangle

> **Goal:** understand the *greedy choice*, the *reason it is safe*, and the exact implementation pattern.
>
> **Problem flow:**  
> **prerequisites → mathematical notation → concept simplified → what it asks → greedy observation → proof + dry run → visual model → C++17 → complexity → recognition**
>
> **Proof style:** whenever useful, the symbolic proof and numerical dry run are shown together.
>
> **Source scope:** these notes follow the uploaded **Greedy Algorithms 2** lecture and expand its handwritten diagrams into self-study explanations.
>
> **Math rendering:** display equations use fenced `math` blocks to avoid Markdown rendering problems.

---

# Clickable Table of Contents

- [0. Prerequisites](#0-prerequisites)
  - [0.1 Greedy Claim + Proof](#01-greedy-claim--proof)
  - [0.2 Exchange Argument](#02-exchange-argument)
  - [0.3 Interval Notation](#03-interval-notation)
  - [0.4 Sorting by a Decision Key](#04-sorting-by-a-decision-key)
  - [0.5 Ratio / Density](#05-ratio--density)
  - [0.6 Running Sum](#06-running-sum)
  - [0.7 Deadline, Slot and Profit](#07-deadline-slot-and-profit)
  - [0.8 Set / upper_bound / Predecessor](#08-set--upper_bound--predecessor)
  - [0.9 Triangle Inequality](#09-triangle-inequality)
- [1. Activity Selection](#1-activity-selection)
- [2. Fractional Knapsack](#2-fractional-knapsack)
- [3. Kadane's Algorithm](#3-kadanes-algorithm)
- [4. Job Sequencing](#4-job-sequencing)
- [5. Maximum Perimeter Triangle](#5-maximum-perimeter-triangle)
- [6. Advantages and Limitations of Greedy](#6-advantages-and-limitations-of-greedy)
- [7. Proof Pattern Summary](#7-proof-pattern-summary)
- [8. Greedy 2 Recognition Checklist](#8-greedy-2-recognition-checklist)
- [9. Compact Revision Card](#9-compact-revision-card)

---

# 0. Prerequisites

# 0.1 Greedy Claim + Proof

A greedy solution has two parts:

```text
1. CLAIM
   "This is the best choice to make now."

2. PROOF
   "Making this choice cannot destroy an optimal answer."
```

Do not think:

```text
Greedy = sorting
```

Think:

```text
Greedy = safe local commitment
```

Visual model:

```text
Current state
     |
     v
Best-looking choice
     |
     v
Can I prove it is safe?
   /   \
 YES    NO
  |      |
commit   do not trust it yet
```

---

## 0.2 Exchange Argument

This is the main proof tool for Activity Selection, Fractional Knapsack, and Job Sequencing.

### General Template

```text
Greedy wants choice G
Optimal solution uses choice O
            |
            v
Replace O by G
            |
            v
Still feasible?
            |
           YES
            |
            v
Objective non-worse?
            |
           YES
            |
            v
An optimal solution can contain G
```

### Mathematical Comparison

For maximization:

```math
G-O\ge0
```

For minimization:

```math
G-O\le0
```

### Tiny Example

Other solution uses value:

```text
7
```

Greedy can use:

```text
10
```

Then:

```math
10-7=3\ge0
```

If the swap preserves all constraints, the greedy choice is safe.

---

## 0.3 Interval Notation

An activity is:

```math
[s_i,e_i]
```

where:

```text
s_i = start time
e_i = finish/end time
```

Visual:

```text
time ------------------------------------------------->

Activity i:
          [----------]
          s_i        e_i
```

Two activities are compatible if the next one starts after the previous one ends.

For the standard interval-scheduling model:

```math
s_{\text{next}}\ge e_{\text{last}}
```

Visual:

```text
Activity A:   [------]
Activity B:           [------]
               eA <= sB
```

Overlapping:

```text
Activity A:   [----------]
Activity B:       [----------]
                    overlap
```

---

## 0.4 Sorting by a Decision Key

Greedy often uses sorting, but the key question is:

```text
WHAT should I sort by?
```

Examples in this lecture:

| Problem | Sort key |
|---|---|
| Activity Selection | earliest finish time |
| Fractional Knapsack | highest `value / weight` |
| Job Sequencing | highest profit |
| Maximum Perimeter Triangle | side length |

C++ comparator example:

```cpp
sort(v.begin(), v.end(),
     [](const auto& a, const auto& b) {
         return a.second < b.second;
     });
```

---

## 0.5 Ratio / Density

Fractional Knapsack uses:

```math
density_i=\frac{value_i}{weight_i}
```

Interpretation:

```text
How much value do I get
for one unit of weight?
```

Example:

```text
item A:
value = 12
weight = 1

density = 12/1 = 12
```

```text
item B:
value = 10
weight = 5

density = 10/5 = 2
```

If fractions are allowed, one unit of weight taken from A is far more valuable.

---

## 0.6 Running Sum

Kadane maintains a current sum:

```math
cur
```

After reading `a[i]`:

```math
cur=cur+a_i
```

If `cur` becomes negative:

```text
carrying this prefix into the future is harmful
```

so reset:

```math
cur=0
```

But update the global answer **before** resetting so that all-negative arrays are handled.

---

## 0.7 Deadline, Slot and Profit

In Job Sequencing each job has:

```text
deadline d_i
profit   p_i
duration = 1
```

A job with deadline `d` can occupy a slot no later than `d`.

Example:

```text
deadline = 3

legal slots:
1,2,3
```

Visual:

```text
slot:      1     2     3     4
           [ ]   [ ]   [ ]   [ ]

deadline 3:
           <----------->
            legal area
```

---

## 0.8 Set / upper_bound / Predecessor

For Job Sequencing, the lecture diagrams use the idea:

```text
find the latest free slot <= deadline
```

Suppose free slots are:

```text
{1,3,5,7,8}
```

and:

```text
deadline = 6
```

We need:

```text
largest free slot <= 6
```

Answer:

```text
5
```

In an ordered `set`:

```cpp
auto it = freeSlots.upper_bound(deadline);
```

`upper_bound(d)` gives:

```text
first value > d
```

Move one step back:

```cpp
--it;
```

to obtain the largest value:

```text
<= d
```

Visual:

```text
free:      1   3   5   7   8
deadline:              6
                       |
upper_bound(6) ------> 7
previous ------------> 5
```

---

## 0.9 Triangle Inequality

For three positive lengths:

```math
a\le b\le c
```

a non-degenerate triangle requires:

```math
a+b>c
```

Why is this the only condition we need after sorting?

Because:

```text
b + c > a
a + c > b
```

are automatically true for positive sides when `c` is largest.

So only check:

```math
a+b>c
```

> **Lecture clarification:** the slide wording uses `A + B >= C`. For the standard non-degenerate triangle problem, the strict condition is `A + B > C`.

---

# 1. Activity Selection

**Problem link from lecture:**  
https://cses.fi/problemset/task/1629

The lecture asks us to choose the maximum number of non-overlapping activities. Its example uses:

```text
[1,5], [2,3], [4,6]
```

with answer:

```text
[2,3], [4,6]
```

---

## 1.1 What It Asks

Given:

```text
N activities
```

each with:

```text
start time
finish time
```

select the maximum number such that only one activity is performed at a time.

Objective:

```math
\max(\text{number of selected activities})
```

Constraint:

```text
selected intervals cannot overlap
```

---

## 1.2 Wrong Claims to Test

The handwritten lecture pages explore several natural ideas before reaching the correct one.

### Wrong Idea 1 — Shortest Duration First

```text
sort by:
finish - start
```

Why suspicious?

A short activity placed badly can still block several future activities.

Duration alone does not tell us how much timeline remains afterward.

---

### Wrong Idea 2 — Fewest Intersections First

This tries to choose an interval overlapping the fewest others.

Problem:

```text
calculating overlap counts is expensive
and local overlap count does not directly prove
maximum future capacity
```

The lecture notes this can lead to higher complexity.

---

### Wrong Idea 3 — Earliest Start First

Starting early does not mean finishing early.

A very long activity can start first and block many short activities.

Visual counterexample:

```text
time ------------------------------------------------->

Long:
[--------------------------------------]

Shorts:
    [---] [---] [---] [---]
```

Earliest-start chooses the long interval.

Bad.

---

## 1.3 Correct Greedy Observation

Choose:

```text
the compatible activity
with the EARLIEST FINISH TIME
```

Why?

Because it leaves the largest possible remaining timeline for future activities.

Visual:

```text
Choice A:
[---------]
          end late

Choice G:
[----]
     end early

Remaining future after G:
     ---------------------------->

Remaining future after A:
          ----------------------->
```

The earlier finish never leaves *less* future room.

---

## 1.4 Greedy Algorithm

```text
1. Sort activities by finish time ascending.
2. Pick the earliest-finishing activity.
3. Scan in that order.
4. Pick activity i if:
      start[i] >= lastFinish
5. Update lastFinish.
```

---

## 1.5 Exchange Proof + Dry Run Side by Side

Let:

```text
G = greedy first activity
O = first activity of some optimal solution
```

Greedy chooses earliest finish, so:

```math
e_G\le e_O
```

Suppose optimal solution after `O` can choose:

```text
y future activities
```

Since `G` ends no later than `O`, every activity that starts after `e_O` also starts after `e_G`.

So replacing:

```text
O → G
```

does not destroy any future activity.

### Side-by-Side Example

Consider:

```text
G = [2,3]
O = [1,5]

future activity:
F = [5,7]
```

| Proof idea | Symbolic | Dry run |
|---|---|---|
| Greedy ends earlier | `e_G <= e_O` | `3 <= 5` |
| Future job starts after O | `s_F >= e_O` | `5 >= 5` |
| Therefore also after G | `s_F >= e_G` | `5 >= 3` |
| Exchange | `O → G` | `[1,5] → [2,3]` |
| Future feasibility | preserved | `[5,7]` still fits |

Therefore an optimal solution can start with the earliest-finishing activity.

After choosing it, the remaining problem is identical:

```text
choose maximum non-overlapping activities
after lastFinish
```

So repeat the same rule.

---

## 1.6 Full Dry Run

Activities:

```text
[1,5]
[2,3]
[4,6]
```

Sort by end:

```text
[2,3]
[1,5]
[4,6]
```

Timeline:

```text
[2,3]      [4,6]
   ✓          ✓

[1,5]
  overlaps
```

Process:

```text
Pick [2,3]
lastFinish = 3

[1,5]:
start = 1 < 3
skip

[4,6]:
start = 4 >= 3
pick
```

Answer:

```text
2 activities
```

---

## 1.7 Visual Recognition Diagram

```text
Maximum number of
non-overlapping intervals
          |
          v
Which first interval
leaves most future room?
          |
          v
Earliest finishing interval
          |
          v
sort by end time
          |
          v
scan + take compatible
```

---

## 1.8 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxActivities(vector<pair<int,int>> activities) {
    sort(activities.begin(), activities.end(),
         [](const auto& a, const auto& b) {
             if (a.second != b.second)
                 return a.second < b.second;
             return a.first < b.first;
         });

    int count = 0;
    int lastFinish = INT_MIN;

    for (auto [start, finish] : activities) {
        if (start >= lastFinish) {
            ++count;
            lastFinish = finish;
        }
    }

    return count;
}
```

For CSES Movie Festival-style input, the same logic applies.

---

## 1.9 Complexity

Sorting:

```text
O(N log N)
```

Scanning:

```text
O(N)
```

Total:

```text
O(N log N)
```

---

## 1.10 Recognition Model

```text
maximize COUNT
of non-overlapping intervals
        |
        v
earliest finish first
```

Do **not** confuse with:

```text
earliest start
shortest duration
fewest overlaps
```

---

# 2. Fractional Knapsack

The lecture defines items with:

```text
value[i]
weight[i]
```

and capacity:

```text
W
```

Fractions of items are allowed.

---

## 2.1 What It Asks

Maximize total value:

```math
\max\sum_i value_i\cdot fraction_i
```

subject to:

```math
\sum_i weight_i\cdot fraction_i\le W
```

where:

```math
0\le fraction_i\le1
```

---

## 2.2 Concept Simplified

The resource is:

```text
weight capacity
```

So ask:

```text
Which item gives the most VALUE per one unit of WEIGHT?
```

That is:

```math
\frac{value_i}{weight_i}
```

Call it:

```text
density
```

Take highest density first.

---

## 2.3 Why Maximum Value Alone Is Wrong

Suppose capacity:

```text
W = 1
```

Items:

```text
A: value=10, weight=5
B: value=6,  weight=1
```

Maximum raw value:

```text
A = 10
```

But only `1/5` of A fits:

```text
value gained = 10 × 1/5 = 2
```

B fits fully:

```text
value gained = 6
```

So raw value is not the correct key.

Density:

```text
A: 10/5 = 2
B: 6/1  = 6
```

Now the correct choice is obvious.

---

## 2.4 Greedy Claim

Sort items descending by:

```math
\frac{value_i}{weight_i}
```

Then:

```text
take as much as possible from each item
before moving to lower density
```

---

## 2.5 Exchange Proof + Dry Run Side by Side

Suppose:

```text
item H has higher density
item L has lower density
```

So:

```math
\frac{v_H}{w_H}\ge\frac{v_L}{w_L}
```

Assume another solution uses some weight amount:

```text
delta
```

from `L` while some `H` is still available.

Exchange that same weight `delta`:

```text
remove delta weight of L
add    delta weight of H
```

Feasibility is unchanged because total weight stays the same.

Value changes by:

```math
\Delta
=
\delta
\left(
\frac{v_H}{w_H}
-
\frac{v_L}{w_L}
\right)
```

Since higher density is at least lower density:

```math
\Delta\ge0
```

So the exchange cannot decrease value.

### Dry Run

```text
H:
value = 12
weight = 1
density = 12

L:
value = 6
weight = 3
density = 2
```

Compare one unit of weight:

| | Higher density H | Lower density L |
|---|---:|---:|
| Value per 1 weight | `12` | `2` |
| Exchange 1 weight | gain `12` | give up `2` |
| Improvement | | `+10` |

Therefore using lower-density weight while higher-density weight remains cannot be optimal.

---

## 2.6 Lecture-Style Example

Values:

```text
[10,2,1,3,4,12,6]
```

Weights:

```text
[1,2,1,2,1,1,3]
```

Densities:

```text
10/1 = 10
2/2  = 1
1/1  = 1
3/2  = 1.5
4/1  = 4
12/1 = 12
6/3  = 2
```

Sorted density order:

```text
12, 10, 4, 2, 1.5, 1, 1
```

If:

```text
W = 1
```

take the item:

```text
value=12, weight=1
```

Total value:

```text
12
```

---

## 2.7 Fractional Dry Run

Items:

```text
A: value=60,  weight=10, density=6
B: value=100, weight=20, density=5
C: value=120, weight=30, density=4
```

Capacity:

```text
W = 50
```

Take A:

```text
weight used = 10
value = 60
remaining = 40
```

Take B:

```text
weight used = 20
value += 100
remaining = 20
```

Only `20/30` of C fits.

Fraction:

```math
\frac{20}{30}=\frac23
```

Value gained:

```math
120\cdot\frac23=80
```

Final value:

```text
60 + 100 + 80 = 240
```

Visual:

```text
Capacity 50:

[A:10][B:20][  20 of C  ]
|-----|----------|--------|
  60      100        80
```

---

## 2.8 Important Contrast — 0/1 Knapsack

Fractional Knapsack:

```text
fraction allowed
→ greedy by density works
```

0/1 Knapsack:

```text
must take whole item or skip it
→ density greedy is NOT generally correct
```

Why?

The exchange proof depends on being able to replace an arbitrary amount of weight.

Without fractions, that exchange may be impossible.

---

## 2.9 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Item {
    double value;
    double weight;
};

double fractionalKnapsack(
    vector<Item> items,
    double capacity
) {
    sort(items.begin(), items.end(),
         [](const Item& a, const Item& b) {
             return a.value / a.weight >
                    b.value / b.weight;
         });

    double ans = 0.0;

    for (const Item& item : items) {
        if (capacity == 0)
            break;

        double take = min(capacity, item.weight);

        ans += take * (item.value / item.weight);
        capacity -= take;
    }

    return ans;
}
```

---

## 2.10 Complexity

Sorting:

```text
O(N log N)
```

Scan:

```text
O(N)
```

Total:

```text
O(N log N)
```

---

## 2.11 Recognition Model

```text
maximize value
under capacity
+
fractions allowed
        |
        v
value per unit resource
        |
        v
sort by value/weight descending
```

---

# 3. Kadane's Algorithm

The lecture asks for the maximum possible sum of a contiguous subarray.

Example:

```text
[1,2,-9,2,3,-1,4]
```

Best:

```text
[2,3,-1,4]
```

Sum:

```text
8
```

---

## 3.1 What It Asks

Find:

```math
\max_{L\le R}
\sum_{i=L}^{R}a_i
```

Important:

```text
subarray = contiguous
```

Not:

```text
subsequence
```

---

## 3.2 Concept Simplified

Suppose the running prefix you are carrying has sum:

```text
negative
```

Example:

```text
prefix sum = -5
```

Future value:

```text
x = 10
```

If we keep the negative prefix:

```text
-5 + 10 = 5
```

If we start fresh at `10`:

```text
10
```

Starting fresh is better.

Therefore:

```text
a negative running sum can never help a future subarray
```

So discard it.

---

## 3.3 Greedy Observation

At each position:

```text
1. add a[i] to current sum
2. update global maximum
3. if current sum < 0:
      reset current sum to 0
```

Visual:

```text
current prefix sum
       |
       v
   negative?
   /      \
 YES       NO
  |         |
discard   carry it forward
```

---

## 3.4 Why Discarding a Negative Prefix Is Safe

Suppose a prefix has sum:

```math
P<0
```

and some future continuation has sum:

```math
F
```

Keeping the prefix gives:

```math
P+F
```

Starting after the negative prefix gives:

```math
F
```

Because:

```math
P<0
```

we know:

```math
P+F<F
```

So any future subarray is strictly better without that negative prefix.

### Side-by-Side Dry Run

Let:

```text
P = -4
F = 9
```

| Choice | Formula | Value |
|---|---|---:|
| Keep negative prefix | `P+F` | `-4+9=5` |
| Drop prefix | `F` | `9` |

Therefore:

```text
drop negative prefix
```

is always safe.

This is the key greedy argument.

---

## 3.5 Full Dry Run

Array:

```text
[1,2,-9,2,3,-1,4]
```

Use:

```text
cur  = current candidate sum
best = best sum seen
```

| `a[i]` | `cur += a[i]` | `best` | Reset? |
|---:|---:|---:|---|
| 1 | 1 | 1 | no |
| 2 | 3 | 3 | no |
| -9 | -6 | 3 | yes → `cur=0` |
| 2 | 2 | 3 | no |
| 3 | 5 | 5 | no |
| -1 | 4 | 5 | no |
| 4 | 8 | 8 | no |

Answer:

```text
8
```

Subarray:

```text
[2,3,-1,4]
```

---

## 3.6 Visual Timeline

```text
[ 1   2  -9 ][ 2   3  -1   4 ]
  \___/
   +3

after adding -9:
cur = -6
        X
discard whole harmful prefix

restart:
            [ 2   3  -1   4 ]
              2 → 5 → 4 → 8
```

---

## 3.7 Why a Negative Element Can Still Be Included

Important:

```text
negative ELEMENT
is not the same as
negative RUNNING SUM
```

Example:

```text
[5,-2,6]
```

Running sum:

```text
5
3
9
```

Although `-2` is negative, the running sum stays positive.

Keeping `-2` is fine because it connects two profitable parts.

So never memorize:

```text
"remove negative numbers"
```

Correct rule:

```text
discard a PREFIX only when its TOTAL contribution becomes negative
```

---

## 3.8 All-Negative Array Warning

Array:

```text
[-5,-2,-8]
```

Correct answer:

```text
-2
```

Therefore:

```text
update best BEFORE resetting cur to 0
```

Use:

```text
best = -infinity
```

not:

```text
best = 0
```

unless the empty subarray is explicitly allowed.

---

## 3.9 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long kadane(const vector<long long>& a) {
    long long best = LLONG_MIN;
    long long cur = 0;

    for (long long x : a) {
        cur += x;

        best = max(best, cur);

        if (cur < 0)
            cur = 0;
    }

    return best;
}
```

Equivalent DP form:

```cpp
long long kadaneDP(const vector<long long>& a) {
    long long cur = a[0];
    long long best = a[0];

    for (int i = 1; i < (int)a.size(); ++i) {
        cur = max(a[i], cur + a[i]);
        best = max(best, cur);
    }

    return best;
}
```

---

## 3.10 Complexity

Time:

```text
O(N)
```

Extra space:

```text
O(1)
```

---

## 3.11 Recognition Model

```text
maximum sum
+
contiguous subarray
        |
        v
running sum
        |
        v
negative prefix?
        |
       YES
        |
        v
discard it
```

---

# 4. Job Sequencing

The lecture problem gives each job:

```text
deadline
profit
duration = 1
```

and asks us to maximize total profit while doing only one job at a time.

Example:

```text
deadline = [1,2,2]
profit   = [10,20,30]
```

Best profit:

```text
50
```

by taking the jobs with profit:

```text
30 and 20
```

---

## 4.1 What It Asks

Each job `i`:

```text
deadline = d_i
profit   = p_i
duration = 1
```

If selected, it must occupy one slot:

```text
slot <= deadline
```

Goal:

```math
\max(\text{total profit})
```

---

## 4.2 Greedy Observation 1 — Consider High Profit First

If a job has the largest available profit, we want to include it whenever a legal slot exists.

So:

```text
sort jobs by profit descending
```

---

## 4.3 Greedy Observation 2 — Put It as Late as Possible

If job deadline is:

```text
d
```

and we schedule it early unnecessarily, we may block another job with a tighter deadline.

So place the selected job in:

```text
latest available slot <= d
```

Visual:

```text
slots:     1    2    3    4    5
           [ ]  [ ]  [ ]  [ ]  [ ]

job deadline = 4

Bad:
           [J]  [ ]  [ ]  [ ]
            ^
            consumes early flexible slot

Better:
           [ ]  [ ]  [ ]  [J]
                          ^
                     latest legal slot
```

This preserves earlier slots for jobs that may need them.

---

## 4.4 Why Highest Profit First Is Safe — Exchange Idea

Let:

```text
G = currently highest-profit job
```

Suppose an optimal schedule does not contain `G`, but there is some scheduled lower-profit job `O` occupying a slot that `G` can legally use.

Because:

```math
profit_G\ge profit_O
```

replace:

```text
O → G
```

The number of jobs stays the same.

If the chosen slot is legal for `G`, feasibility remains valid.

Profit changes by:

```math
profit_G-profit_O\ge0
```

So the schedule is not worse.

### Dry Run

```text
G:
profit=30
deadline=2

O:
profit=20
scheduled at slot 2
```

Swap:

```text
slot 2:
20 → 30
```

Profit improvement:

```text
30-20=10
```

Feasibility:

```text
slot 2 <= deadline 2
```

Still valid.

---

## 4.5 Why Latest Available Slot Is Safe

Suppose selected job `J` can be placed in either:

```text
early slot s
```

or:

```text
later free slot t
```

with:

```text
s < t <= deadline(J)
```

Putting `J` at `t` leaves `s` free.

That cannot reduce future flexibility because:

```text
a future tight-deadline job may need s,
while J already has the flexibility to use t
```

Visual:

```text
Before:

slot:      1    2    3    4
           [ ]  [ ]  [ ]  [ ]

J deadline = 4

Early placement:
           [J]  [ ]  [ ]  [ ]
            X
tight job with deadline 1 may lose its only slot

Late placement:
           [ ]  [ ]  [ ]  [J]
            ^
tight slot remains available
```

So:

```text
profit chooses WHICH job
latest slot chooses WHERE to place it
```

---

## 4.6 Full Dry Run

Jobs:

```text
J1: deadline=1, profit=10
J2: deadline=2, profit=20
J3: deadline=2, profit=30
```

Sort by profit:

```text
J3: 30, d=2
J2: 20, d=2
J1: 10, d=1
```

Free slots:

```text
{1,2}
```

### J3

Latest slot `<=2`:

```text
2
```

Schedule:

```text
slot 2 → J3
```

Free:

```text
{1}
```

### J2

Latest slot `<=2`:

```text
1
```

Schedule:

```text
slot 1 → J2
```

Free:

```text
{}
```

### J1

No free slot.

Skip.

Total:

```text
30+20 = 50
```

Visual:

```text
time slot:   1      2
             |      |
             J2     J3
profit:      20     30

total = 50
```

---

## 4.7 Set / upper_bound Implementation

Initialize available slots:

```text
1,2,...,maxDeadline
```

For each job in descending profit:

```cpp
auto it = freeSlots.upper_bound(deadline);
```

If:

```text
it == begin()
```

there is no free slot `<= deadline`.

Otherwise:

```cpp
--it;
```

gives the latest free slot.

Schedule the job there and erase the slot.

---

## 4.8 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Job {
    int deadline;
    long long profit;
};

long long maxJobProfit(vector<Job> jobs) {
    sort(jobs.begin(), jobs.end(),
         [](const Job& a, const Job& b) {
             return a.profit > b.profit;
         });

    int maxDeadline = 0;

    for (const Job& job : jobs)
        maxDeadline = max(maxDeadline, job.deadline);

    set<int> freeSlots;

    for (int t = 1; t <= maxDeadline; ++t)
        freeSlots.insert(t);

    long long ans = 0;

    for (const Job& job : jobs) {
        auto it = freeSlots.upper_bound(job.deadline);

        if (it == freeSlots.begin())
            continue;

        --it;

        ans += job.profit;
        freeSlots.erase(it);
    }

    return ans;
}
```

---

## 4.9 Complexity

Sorting jobs:

```text
O(N log N)
```

For each job:

```text
upper_bound + erase
= O(log N)
```

Total:

```text
O(N log N)
```

Space:

```text
O(N)
```

---

## 4.10 Recognition Model

```text
unit-time jobs
+
deadline
+
profit
+
maximize total profit
        |
        v
highest profit first
        |
        v
place as late as possible
before deadline
```

---

# 5. Maximum Perimeter Triangle

The lecture asks us to choose three positive integers that form a triangle with maximum perimeter.

Example:

```text
[2,3,15,5,1,7]
```

Best:

```text
[3,5,7]
```

Perimeter:

```text
15
```

---

## 5.1 What It Asks

Choose:

```text
a,b,c
```

such that they form a valid triangle and maximize:

```math
a+b+c
```

After sorting:

```math
a\le b\le c
```

validity reduces to:

```math
a+b>c
```

---

## 5.2 Concept Simplified

To maximize perimeter, we naturally want large sides.

So sort the array.

Fix the largest candidate side:

```text
c
```

Which two other sides should we try first?

Answer:

```text
the two largest available sides below c
```

Why?

They maximize:

```text
a+b
```

and therefore give the best chance to satisfy:

```text
a+b>c
```

while also maximizing perimeter.

---

## 5.3 Key Proof

Suppose ascending array:

```math
x_1\le x_2\le\cdots\le x_n
```

Fix:

```text
c = x_i
```

The largest possible pair below `c` is:

```text
x_(i-2), x_(i-1)
```

If even these fail:

```math
x_{i-2}+x_{i-1}\le x_i
```

then any smaller pair:

```text
x_p <= x_(i-2)
x_q <= x_(i-1)
```

satisfies:

```math
x_p+x_q
\le
x_{i-2}+x_{i-1}
\le
x_i
```

So **no triangle using `x_i` as largest side can exist**.

That is the crucial greedy reduction.

---

## 5.4 Proof + Dry Run Side by Side

Sorted:

```text
[1,2,3,5,7,15]
```

First fix largest:

```text
c = 15
```

Best possible two smaller sides:

```text
5 and 7
```

Check:

```text
5+7 = 12
12 <= 15
```

Fails.

Because `5` and `7` are the **largest** available pair below `15`, every other pair has sum `<=12`.

So no triangle with side `15`.

Move to:

```text
c = 7
```

Best lower pair:

```text
3 and 5
```

Check:

```text
3+5 = 8 > 7
```

Valid.

Perimeter:

```text
3+5+7 = 15
```

Since we are scanning from the largest side downward, this first valid consecutive triple has maximum perimeter.

---

## 5.5 Visual Diagram

```text
sorted:
1   2   3   5   7   15
                ^    ^
              best   c
              pair

for c = 15:
5 + 7 <= 15
        X

No smaller pair can beat 5+7,
so discard 15 as largest side.

Next:

1   2   3   5   7
        ^   ^   ^
        a   b   c

3 + 5 > 7
    ✓

first valid triple from the right
→ maximum perimeter
```

---

## 5.6 Why Consecutive Elements Are Enough

For a fixed largest side `x_i`:

```text
the two immediately previous elements
are the largest possible companions
```

So:

```text
if they fail → all other pairs fail
if they succeed → they maximize perimeter for x_i
```

Therefore check only:

```text
(x[i-2], x[i-1], x[i])
```

while scanning from right to left.

---

## 5.7 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long maxTrianglePerimeter(
    vector<long long> a
) {
    sort(a.begin(), a.end());

    for (int i = (int)a.size() - 1; i >= 2; --i) {
        if (a[i - 2] + a[i - 1] > a[i]) {
            return a[i - 2] + a[i - 1] + a[i];
        }
    }

    return -1; // no valid non-degenerate triangle
}
```

---

## 5.8 Complexity

Sorting:

```text
O(N log N)
```

Scan:

```text
O(N)
```

Total:

```text
O(N log N)
```

---

## 5.9 Recognition Model

```text
choose 3 sides
+
maximize perimeter
+
triangle inequality
        |
        v
sort
        |
        v
scan largest triples
        |
        v
first consecutive valid triple
```

---

# 6. Advantages and Limitations of Greedy

The lecture ends by emphasizing both sides.

---

## 6.1 Advantages

### 1. Avoids Trying Every Possibility

Instead of:

```text
all subsets
all permutations
all schedules
```

greedy narrows the search dramatically.

Example:

```text
Activity Selection:
sort + one scan
```

instead of exploring all subsets of intervals.

---

### 2. Usually Easy to Implement

Many greedy algorithms become:

```text
sort
+
single scan
```

Examples here:

```text
Activity Selection
Fractional Knapsack
Maximum Perimeter Triangle
```

---

### 3. Often Fast

Typical complexity:

```text
O(N log N)
```

because sorting dominates.

Kadane is even:

```text
O(N)
```

---

## 6.2 Limitations

### 1. Lacks Global Awareness

A local choice can look best now but be globally bad.

Classic example from Greedy 1:

```text
coins [1,8,10]
target 16
```

largest-first fails.

---

### 2. Correctness Can Be Hard to See

The implementation may be only ten lines.

The proof may be the difficult part.

Example:

```text
Why earliest finish?
Why latest slot?
Why density?
```

These require reasoning about future flexibility.

---

### 3. Counterexample Search Is Important

Before trusting a claim, test:

```text
small cases
extreme values
ties
one huge interval/item/job
many tiny intervals/items/jobs
```

---

### 4. Greedy Does Not Explore All Possibilities

This is both:

```text
its strength
and
its risk
```

If the greedy property is false, the algorithm can miss the optimum completely.

---

# 7. Proof Pattern Summary

| Problem | Greedy choice | Proof idea |
|---|---|---|
| Activity Selection | earliest finish | exchange: leaves at least as much future room |
| Fractional Knapsack | highest density | exchange equal weight from low density to high density |
| Kadane | discard negative prefix | negative prefix only reduces any future continuation |
| Job Sequencing | highest profit + latest slot | exchange profit; latest placement preserves early slots |
| Maximum Perimeter Triangle | largest valid consecutive triple | for fixed largest side, previous two maximize companion sum |

---

## 7.1 Future-Space Proof

Used in:

```text
Activity Selection
Job Sequencing placement
```

Question:

```text
Which choice preserves the most options for later?
```

Activity:

```text
finish earlier
→ leave more future timeline
```

Job:

```text
place later
→ preserve earlier slots
```

Interesting contrast:

```text
Intervals:
EARLIEST finish

Jobs:
LATEST legal slot
```

Both are really the same principle:

```text
preserve scarce future flexibility
```

---

## 7.2 Density Exchange Proof

Used in:

```text
Fractional Knapsack
```

```text
same resource amount
+
higher value per unit
→ never worse
```

---

## 7.3 Harmful Prefix Proof

Used in:

```text
Kadane
```

```math
P<0
```

then for any future sum `F`:

```math
P+F<F
```

So discard `P`.

---

## 7.4 Dominating Candidate Proof

Used in:

```text
Maximum Perimeter Triangle
```

For fixed largest side:

```text
choose the two largest smaller sides
```

If even they fail:

```text
all smaller pairs fail
```

---

# 8. Greedy 2 Recognition Checklist

When reading a contest problem, ask:

```text
1. Is the objective count, value, profit, or sum?

2. What resource is scarce?
   - timeline?
   - capacity?
   - slots?
   - running sum?

3. What choice preserves the most future flexibility?

4. Is there a natural "value per unit resource" ratio?

5. Is some current prefix permanently harmful?

6. Can I sort candidates by:
   - finish time?
   - density?
   - profit?
   - size?

7. Can I exchange an optimal solution's choice
   with the greedy choice?

8. Does the swap preserve feasibility?

9. Can I write a one-line inequality proving
   the objective is non-worse?

10. Can I construct a counterexample?
```

---

# 9. Compact Revision Card

```text
GREEDY 2
========


ACTIVITY SELECTION
------------------
Goal:
max count of non-overlapping intervals

Greedy:
earliest FINISH first

Why:
ends earlier
→ leaves at least as much future room

Implementation:
sort by end
scan compatible intervals


FRACTIONAL KNAPSACK
-------------------
Goal:
max value under weight W

Fractions allowed

density:
value / weight

Greedy:
highest density first

Proof:
replace equal weight
of lower density
with higher density


KADANE
------
Goal:
maximum contiguous subarray sum

Greedy observation:
negative running prefix is harmful

If:
P < 0

then:
P + F < F

So:
discard P

Implementation:
cur += x
best = max(best, cur)
if cur < 0:
    cur = 0


JOB SEQUENCING
--------------
unit-time jobs
deadline + profit

Greedy:
1. highest profit first
2. place job in latest free slot <= deadline

Why late?
preserves early slots
for tighter-deadline jobs

Data structure:
set of free slots
upper_bound(deadline)
then previous iterator


MAX PERIMETER TRIANGLE
----------------------
sort ascending

for largest side c:
best companions are
two largest sides below c

if:
a + b <= c

then every smaller pair also fails

scan from right
first valid consecutive triple
→ maximum perimeter


MASTER IDEA
-----------
Activity:
finish EARLY
to preserve future time

Job scheduling:
place LATE
to preserve early slots

Fractional:
take highest VALUE PER UNIT

Kadane:
discard NEGATIVE HISTORY

Triangle:
try LARGEST FEASIBLE triple
```

---

# Final Visual Mental Map

```text
                    GREEDY 2
                       |
     +-----------------+------------------+
     |                 |                  |
  Timeline          Capacity           Sequence
     |                 |                  |
     v                 v                  v
Activity          Fractional          Kadane
Selection         Knapsack
     |                 |                  |
earliest          max value/unit      negative prefix
finish            resource            is harmful
     |
     +-----------------------------+
                                   |
                                Deadlines
                                   |
                                   v
                              Job Sequencing
                                   |
                           high profit first
                           place as late as possible

Triangle:
sort → largest candidate sides → first feasible triple
```

> **Core lesson:** Greedy choices often look different, but the proof usually asks the same question:  
> **“Why does this choice preserve or improve every opportunity an optimal solution could still use?”**
