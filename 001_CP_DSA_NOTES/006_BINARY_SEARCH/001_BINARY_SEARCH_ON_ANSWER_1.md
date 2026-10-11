# TLE Eliminators — Binary Search on Answer
## TLE-Style Notes — Monotonic Predicate + Feasibility Check + Fair Workload

> **Source:** supplied TLE Eliminators lecture PDF **Binary Search on Answer** by Priyansh Agarwal.
>
> **Main idea**
>
> ```text
> answer is not directly known
>        |
>        v
> guess a candidate answer X
>        |
>        v
> can(X) = True / False
>        |
>        v
> prove can(X) is monotonic
>        |
>        v
> binary search the boundary
> ```
>
> **Problem flow used in this note**
>
> ```text
> What it asks
> → small dry run
> → search space
> → predicate
> → monotonicity
> → feasibility check
> → binary-search boundary
> → detailed dry run
> → C++17
> → complexity
> → recognition model
> ```
>
> **Important:** the final slide introduces `sqrt(x)` as the next problem/class, but this PDF does not develop its solution. It is therefore not solved in these notes.

---

# Clickable Table of Contents

- [0. Class Map](#0-class-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 What Binary Search Does](#11-what-binary-search-does)
  - [1.2 Why Binary Search Is O(log N)](#12-why-binary-search-is-olog-n)
  - [1.3 Binary Search Requires Monotonicity](#13-binary-search-requires-monotonicity)
  - [1.4 Increasing vs Decreasing Monotonic Functions](#14-increasing-vs-decreasing-monotonic-functions)
  - [1.5 Classic Binary Search on a Sorted Array](#15-classic-binary-search-on-a-sorted-array)
  - [1.6 Boundary Search — Last Value <= X](#16-boundary-search--last-value--x)
  - [1.7 Count Values in L <= a[i] <= R](#17-count-values-in-l--ai--r)
  - [1.8 Predicate Functions](#18-predicate-functions)
  - [1.9 Monotonic Predicate Functions](#19-monotonic-predicate-functions)
  - [1.10 Two Boolean Shapes](#110-two-boolean-shapes)
  - [1.11 Binary Search on Answer](#111-binary-search-on-answer)
  - [1.12 First True vs Last True](#112-first-true-vs-last-true)
  - [1.13 Search Space Bounds](#113-search-space-bounds)
  - [1.14 Feasibility Check](#114-feasibility-check)
  - [1.15 Recognition Checklist](#115-recognition-checklist)
- [2. Problem 1 — Fair Workload](#2-problem-1--fair-workload)
  - [2.1 What It Asks](#21-what-it-asks)
  - [2.2 Small Dry Run First](#22-small-dry-run-first)
  - [2.3 Convert Optimization Into a Yes-No Question](#23-convert-optimization-into-a-yes-no-question)
  - [2.4 Search Space](#24-search-space)
  - [2.5 Why the Predicate Is Monotonic](#25-why-the-predicate-is-monotonic)
  - [2.6 Greedy Feasibility Check](#26-greedy-feasibility-check)
  - [2.7 Why the Greedy Check Is Correct](#27-why-the-greedy-check-is-correct)
  - [2.8 Lecture Dry Run — X = 30, 50, 70](#28-lecture-dry-run--x--30-50-70)
  - [2.9 Binary Search Dry Run](#29-binary-search-dry-run)
  - [2.10 Algorithm](#210-algorithm)
  - [2.11 C++17](#211-c17)
  - [2.12 Complexity](#212-complexity)
  - [2.13 Recognition Model](#213-recognition-model)
- [3. Binary Search Templates](#3-binary-search-templates)
  - [3.1 First True — F F F T T T](#31-first-true--f-f-f-t-t-t)
  - [3.2 Last True — T T T F F F](#32-last-true--t-t-t-f-f-f)
  - [3.3 Common Midpoint Rule](#33-common-midpoint-rule)
- [4. Common Mistakes](#4-common-mistakes)
- [5. Final Mental Model](#5-final-mental-model)

---

# 0. Class Map

```text
BINARY SEARCH
│
├── Search in sorted data
│   ├── exact value
│   ├── first / last position
│   └── count in range
│
└── Binary Search on Answer
    │
    ├── candidate answer X
    │
    ├── predicate can(X)
    │
    ├── monotonic True / False pattern
    │
    └── search boundary
        ├── first True
        └── last True
```

For **Fair Workload**:

```text
minimize maximum workload
        |
        v
guess maximum allowed workload X
        |
        v
can we split into <= K workers?
        |
        v
F F F F T T T T
        ^
        |
   first True
```

---

# 1. Prerequisites

## 1.1 What Binary Search Does

Binary search repeatedly cuts the current search space approximately in half.

```text
whole search space
        |
        v
      half
        |
        v
      half
        |
        v
      half
        |
        v
      answer
```

The lecture describes it as:

```text
divide and conquer
+
reduce search-space size to half at every step
```

### Tiny example

Sorted array:

```text
index:  0   1   2   3   4   5   6
value: 10  20  35  40  50  55  60
```

Find:

```text
55
```

Start:

```text
L = 0
R = 6

mid = 3
a[mid] = 40
```

Since:

```text
40 < 55
```

discard:

```text
0..3
```

Search:

```text
index: 4   5   6
value: 50  55  60
```

Now:

```text
mid = 5
a[mid] = 55
```

Found.

ASCII:

```text
10  20  35  [40]  50  55  60
              |
              | 40 < 55
              v
             RIGHT

                50 [55] 60
                    |
                  FOUND
```

---

## 1.2 Why Binary Search Is O(log N)

The lecture gives:

```text
T(N) = T(N / 2) + 1
T(1) = 1
```

Search-space sizes:

```text
N
↓
N/2
↓
N/4
↓
N/8
↓
...
↓
1
```

After `k` halvings:

```text
N / 2^k = 1
```

Multiply both sides by:

```text
2^k
```

So:

```text
N = 2^k
```

Take log base 2:

```text
k = log2(N)
```

Therefore:

```text
Time = O(log N)
```

### Tiny numeric example

If:

```text
N = 1024
```

then:

```text
1024 = 2^10
```

So only about:

```text
10
```

halving decisions are needed.

---

## 1.3 Binary Search Requires Monotonicity

The lecture's key requirement is:

```text
MONOTONICITY
```

Binary search works because after checking the middle, we must know that one entire side can be discarded.

If behavior goes:

```text
up
down
up
down
```

there is no reliable side to discard.

### Monotonic

```text
y

|                    *
|                *
|            *
|        *
|    *
+--------------------------> x

x increases
y increases
```

Binary search can exploit this order.

### Non-monotonic

```text
y

|       /\          /
|      /  \        /
|     /    \______/
|____/
+--------------------------> x
```

A target value may occur in several disconnected places.

Knowing one midpoint does not identify one side to discard.

---

## 1.4 Increasing vs Decreasing Monotonic Functions

The lecture shows both directions.

### Increasing

As:

```text
x increases
```

we have:

```text
f(x) increases
```

Example used in the lecture:

```text
f(x) = x^2 + 5x
```

for positive `x`.

Values:

```text
x:      1   2   3   4   5   6   7   8
f(x):   6  14  24  36  50  66  84 104
```

ASCII:

```text
f(x)

104 |                         *
 84 |                     *
 66 |                 *
 50 |             *
 36 |          *
 24 |       *
 14 |    *
  6 | *
    +----------------------------> x
      1  2  3  4  5  6  7  8
```

For a target output, we can move left/right based on whether the current function value is too small or too large.

---

### Decreasing

```text
x increases
f(x) decreases
```

ASCII:

```text
f(x)

high | *
     |   *
     |      *
     |         *
low  |             *
     +----------------------> x
```

Binary search still works.

The direction of the comparisons simply reverses.

---

## 1.5 Classic Binary Search on a Sorted Array

A sorted array itself is a monotonic function of its index:

```text
f(i) = a[i]
```

Example:

```text
index: 0   1   2   3   4   5   6   7   8
value:10  11  17  18  20  22  30  50  60
```

As:

```text
index increases
```

the value does not decrease.

So:

```text
index
→
array value
```

is monotonic.

This is why binary search on a sorted array works.

---

## 1.6 Boundary Search — Last Value <= X

A very important binary-search form is not:

```text
find X exactly
```

but:

```text
find the last index i
such that a[i] <= X
```

Example:

```text
index: 0   1   2   3   4   5   6   7
value:10  20  30  40  50  60  70  80

X = 42
```

Predicate:

```text
a[i] <= 42
```

Boolean sequence:

```text
10  20  30  40  50  60  70  80
 T   T   T   T   F   F   F   F
                 ^
                 |
             last True
```

Answer index:

```text
3
```

Number of values `<= 42`:

```text
3 + 1
=
4
```

This boundary-search thinking leads directly to binary search on answer.

---

## 1.7 Count Values in L <= a[i] <= R

The lecture asks:

```text
Given a sorted array,
how many indices satisfy:

L <= a[i] <= R ?
```

Example:

```text
array:
1 2 3 5 6 7 8 10 11 16 20

L = 4
R = 12
```

Valid values:

```text
5 6 7 8 10 11
```

Answer:

```text
6
```

### Step-by-step derivation

Let:

```text
A = number of values <= R
B = number of values <= L - 1
```

Then:

```text
answer = A - B
```

For:

```text
L = 4
R = 12
```

we have:

```text
A = count(values <= 12)
  = 9

B = count(values <= 3)
  = 3
```

Therefore:

```text
answer
=
9 - 3
=
6
```

ASCII:

```text
array:
1  2  3 | 5  6  7  8  10  11 | 16  20
--------+----------------------+--------
 <= 3       4 <= value <= 12       >12

B = 3
A = 9

answer = A - B
       = 9 - 3
       = 6
```

### C++ STL form

```cpp
long long countLessOrEqualR =
    upper_bound(a.begin(), a.end(), R)
    -
    a.begin();

long long countLessOrEqualLMinusOne =
    upper_bound(a.begin(), a.end(), L - 1)
    -
    a.begin();

long long answer =
    countLessOrEqualR
    -
    countLessOrEqualLMinusOne;
```

Complexity per query:

```text
O(log N)
```

---

## 1.8 Predicate Functions

The lecture defines a predicate function as a function returning:

```text
True
or
False
```

for every input.

Examples from the lecture:

```text
P(x) = True if x > 10
       False otherwise
```

Another example asks whether a value satisfies some property such as:

```text
x^2 is odd
```

### Tiny dry run

Take:

```text
P(x) = (x > 10)
```

Then:

```text
x = 7   → False
x = 10  → False
x = 11  → True
x = 20  → True
```

A predicate by itself is not enough for binary search.

It must also be:

```text
monotonic
```

over the search space.

---

## 1.9 Monotonic Predicate Functions

The lecture highlights two valid boolean patterns.

### Shape A

```text
T T T T T F F F F
```

There is one transition:

```text
True → False
```

### Shape B

```text
F F F F F T T T T
```

There is one transition:

```text
False → True
```

ASCII:

```text
Shape A

T T T T T | F F F F
          ^
       boundary
```

```text
Shape B

F F F F F | T T T T
          ^
       boundary
```

Binary search is really searching for that:

```text
boundary
```

---

## 1.10 Two Boolean Shapes

### Last True

```text
T T T T T F F F
        ^
        |
     last True
```

Typical question:

```text
What is the largest X
for which the condition still holds?
```

---

### First True

```text
F F F F T T T T
        ^
        |
     first True
```

Typical question:

```text
What is the smallest X
for which the condition becomes possible?
```

The **Fair Workload** problem has exactly this second shape.

---

## 1.11 Binary Search on Answer

Normal binary search:

```text
search for an element
inside sorted data
```

Binary search on answer:

```text
search over possible ANSWERS
```

The lecture describes the search space as an:

```text
ordered set of candidate answers
```

For every candidate:

```text
X
```

define:

```text
can(X)
```

Then binary search works if `can(X)` is monotonic.

### Transformation

Suppose the original problem asks:

```text
minimize some maximum value
```

Do not immediately search for the optimum.

Ask:

```text
"If I claim the answer is at most X,
is that possible?"
```

That gives:

```text
can(X)
```

Now optimization becomes a sequence of yes/no questions.

---

## 1.12 First True vs Last True

This is the most important distinction.

### Minimize an answer

Very often:

```text
small X → impossible
large X → possible
```

So:

```text
F F F F T T T T
```

We need:

```text
FIRST TRUE
```

Example:

```text
minimum possible maximum workload
```

---

### Maximize an answer

Very often:

```text
small X → possible
large X → impossible
```

So:

```text
T T T T F F F F
```

We need:

```text
LAST TRUE
```

### Mental rule

```text
MINIMUM feasible X
→ FIRST TRUE

MAXIMUM feasible X
→ LAST TRUE
```

---

## 1.13 Search Space Bounds

For binary search on answer, choosing valid bounds is part of the solution.

For Fair Workload:

```text
LOW = maximum single workload
HIGH = sum of all workloads
```

Why?

### Lower bound

One workload cannot be split.

If:

```text
max workload = 60
```

then an answer smaller than:

```text
60
```

is impossible.

Therefore:

```text
answer >= max(a[i])
```

---

### Upper bound

Worst case:

```text
one worker does everything
```

So:

```text
answer <= sum(a[i])
```

Hence:

```text
max(a[i])
<=
answer
<=
sum(a[i])
```

ASCII:

```text
max element                            sum of all
    |                                      |
    v                                      v
----[--------------------------------------]----
        valid candidate-answer space
```

---

## 1.14 Feasibility Check

For a candidate maximum workload:

```text
X
```

we ask:

```text
Can the ordered workload array be split
among at most K workers
such that every worker gets total workload <= X?
```

This is the predicate:

```text
can(X)
```

For a fixed `X`, scan left to right.

Maintain:

```text
currentWorkerLoad
workersUsed
```

Try to place the next workload on the current worker.

If:

```text
currentWorkerLoad + workload <= X
```

keep it with the same worker.

Otherwise:

```text
start a new worker
```

### Tiny dry run

```text
workloads:
10 20 30 10

X = 50
```

Start:

```text
worker 1:
10
10+20 = 30 <= 50
```

Try `30`:

```text
30 + 30 = 60 > 50
```

So cut:

```text
[10 20] | [30 10]
```

Loads:

```text
30
40
```

Workers used:

```text
2
```

---

## 1.15 Recognition Checklist

Binary search on answer should enter your mind when you see:

```text
minimize maximum
maximize minimum
minimum capacity
minimum time
minimum speed
maximum distance
maximum possible value
```

Then ask:

```text
1. What exactly is the answer X?

2. What is the smallest possible X?

3. What is the largest possible X?

4. Can I write can(X)?

5. Is can(X) monotonic?

6. Do I need:
   FIRST TRUE
   or
   LAST TRUE?
```

---

# 2. Problem 1 — Fair Workload

## 2.1 What It Asks

The lecture problem:

```text
Given an array of workloads,
split it among K workers.

The order of workloads cannot be changed.

Minimize:
the maximum total work assigned
to any one worker.
```

Lecture example:

```text
[10,20,30,40,50,60,70,80,90]
```

with a shown partition:

```text
10 20 30 40 50 | 60 70 | 80 90
```

Worker loads:

```text
150
130
170
```

So this partition has:

```text
maximum worker load = 170
```

The question is:

```text
Can we do better than 170?
```

That is exactly where binary search on answer begins.

---

## 2.2 Small Dry Run First

Use the smaller lecture dry-run array:

```text
workloads:
10 20 30 10 10 20 30 20

K = 3
```

Try candidate:

```text
X = 70
```

Greedy split:

```text
10 + 20 + 30 + 10
=
70
```

Worker 1:

```text
[10 20 30 10]
```

Next:

```text
10 + 20 + 30
=
60
```

Worker 2:

```text
[10 20 30]
```

Remaining:

```text
[20]
```

Worker 3.

So:

```text
workersUsed = 3
```

and:

```text
3 <= K
```

Therefore:

```text
can(70) = True
```

ASCII:

```text
10 20 30 10 | 10 20 30 | 20
------------+-----------+----
     70           60       20

K = 3
X = 70

TRUE
```

---

## 2.3 Convert Optimization Into a Yes-No Question

Original optimization:

```text
minimize
maximum worker load
```

Convert it to:

```text
For a candidate X:

"Can I split the array into
at most K contiguous groups
such that every group sum <= X?"
```

Define:

```text
can(X) = True
```

if such a split exists.

Otherwise:

```text
can(X) = False
```

Now the problem is not:

```text
directly find optimum
```

It is:

```text
find the first X
for which can(X) becomes True
```

---

## 2.4 Search Space

For:

```text
workloads =
10 20 30 10 10 20 30 20
```

Maximum element:

```text
30
```

Total sum:

```text
10+20+30+10+10+20+30+20
=
150
```

Therefore:

```text
LOW  = 30
HIGH = 150
```

ASCII:

```text
30                                      150
|-----------------------------------------|
^                                         ^
smallest possible                   always possible
candidate                           with one worker
```

So binary search only needs to inspect:

```text
[30,150]
```

not arbitrary integers outside the valid range.

---

## 2.5 Why the Predicate Is Monotonic

Suppose:

```text
can(70) = True
```

That means we already have a partition where every worker receives at most:

```text
70
```

Then with a larger capacity:

```text
71
80
100
...
```

the exact same partition is still valid.

Therefore:

```text
can(70) = True
⇒
can(71) = True
⇒
can(72) = True
⇒
...
```

So once the predicate becomes `True`, it stays `True`.

Boolean shape:

```text
X:

30 31 32 ...          answer          ... 150
F  F  F  F  ...  F | T  T  T  T  T
                      ^
                      |
                  first True
```

This is the monotonicity required for binary search.

### Key proof

```text
If capacity X is feasible,
then any capacity Y >= X is also feasible
because every group sum <= X <= Y.
```

Therefore:

```text
can(X)
```

has the form:

```text
F F F F ... T T T T
```

---

## 2.6 Greedy Feasibility Check

For a fixed candidate:

```text
X
```

scan workloads from left to right.

Keep adding to the current worker while possible.

If the next workload would exceed `X`:

```text
cut here
start another worker
```

### Pseudocode

```text
workersUsed = 1
currentLoad = 0

for workload in workloads:

    if workload > X:
        return false

    if currentLoad + workload <= X:
        currentLoad += workload

    else:
        workersUsed++
        currentLoad = workload

return workersUsed <= K
```

### ASCII

```text
current worker
      |
      v

[10 20 30] next=20

current sum = 60
X = 70

60 + 20 = 80 > 70

so:

[10 20 30] | [20 ...]
              ^
              new worker
```

---

## 2.7 Why the Greedy Check Is Correct

For fixed `X`, we want to know whether the array can be split using:

```text
<= K workers
```

The greedy check fills each worker with the **longest possible prefix** whose sum does not exceed `X`.

Suppose current worker has:

```text
sum = 50
```

and the next workload is:

```text
20
```

with:

```text
X = 70
```

Then:

```text
50 + 20 = 70
```

So placing `20` on the current worker is safe.

Why not cut earlier?

If we cut before `20`, we use a new worker sooner:

```text
earlier cut
→ never reduces workersUsed
```

Keeping as many consecutive jobs as possible in the current worker leaves:

```text
the fewest possible remaining jobs
```

for later workers.

Therefore the greedy scan uses the minimum number of workers possible for that fixed `X`.

So:

```text
greedy workersUsed <= K
```

means a valid split exists.

And:

```text
greedy workersUsed > K
```

means no other earlier-cut strategy can rescue it.

### Tiny comparison

Workloads:

```text
10 20 30 10
X = 50
```

Greedy:

```text
[10 20] | [30 10]

workers = 2
```

Cutting earlier:

```text
[10] | [20 30] | [10]

workers = 3
```

The earlier cut did not help.

---

## 2.8 Lecture Dry Run — X = 30, 50, 70

Lecture array:

```text
10 20 30 10 10 20 30 20
```

Workers:

```text
K = 3
```

---

### Candidate X = 30

Process:

```text
[10 20] | [30] | [10 10] | [20] | [30] | [20]
```

Loads:

```text
30
30
20
20
30
20
```

Workers needed:

```text
6
```

But:

```text
K = 3
```

Therefore:

```text
can(30) = False
```

---

### Candidate X = 50

Greedy:

```text
[10 20] | [30 10 10] | [20 30] | [20]
```

Loads:

```text
30
50
50
20
```

Workers needed:

```text
4
```

Since:

```text
4 > 3
```

we get:

```text
can(50) = False
```

---

### Candidate X = 70

Greedy:

```text
[10 20 30 10] | [10 20 30] | [20]
```

Loads:

```text
70
60
20
```

Workers needed:

```text
3
```

Since:

```text
3 <= 3
```

we get:

```text
can(70) = True
```

---

### Predicate picture

```text
X = 30
FALSE

X = 50
FALSE

X = 70
TRUE
```

Therefore the transition is somewhere inside:

```text
(50,70]
```

ASCII:

```text
30----------------50----------------70
F                 F                 T
                  |-----------------|
                    answer is here
```

This is exactly what binary search exploits.

---

## 2.9 Binary Search Dry Run

Use:

```text
workloads =
10 20 30 10 10 20 30 20

K = 3

LOW  = 30
HIGH = 150
```

We want:

```text
first X such that can(X) = True
```

---

### Iteration 1

```text
L = 30
R = 150

mid = 30 + (150-30)/2
    = 90
```

Check:

```text
can(90)
```

A valid split certainly exists with at most 3 workers, so:

```text
True
```

Since we want the **first** True:

```text
answer = 90
R = 89
```

Search left.

---

### Iteration 2

```text
L = 30
R = 89

mid = 59
```

Greedy split:

```text
[10 20]       = 30
[30 10 10]    = 50
[20 30]       = 50
[20]          = 20
```

Workers:

```text
4
```

So:

```text
can(59) = False
```

Therefore:

```text
L = 60
```

Search right.

---

### Iteration 3

```text
L = 60
R = 89

mid = 74
```

Greedy can fit the jobs in at most 3 workers.

So:

```text
can(74) = True
```

Move left:

```text
answer = 74
R = 73
```

---

The process continues until:

```text
L > R
```

and `answer` stores the first feasible capacity.

### Search visualization

```text
30........................................150
L                    90                    R
                     T
                     |
         discard everything > 90
         for finding FIRST True

30..................89
L        59          R
         F
         |
         discard <= 59

60.................89
L      74           R
       T
       |
       move left
```

---

## 2.10 Algorithm

```text
low =
    maximum workload

high =
    sum of all workloads

answer = high

while low <= high:

    mid =
        low + (high-low)/2

    if can(mid):

        answer = mid

        // mid is feasible,
        // but maybe a smaller answer works
        high = mid - 1

    else:

        // mid is too small
        low = mid + 1

return answer
```

Feasibility:

```text
can(X):

    workersUsed = 1
    currentLoad = 0

    for each workload:

        if workload > X:
            return false

        if currentLoad + workload <= X:
            currentLoad += workload

        else:
            workersUsed++
            currentLoad = workload

            if workersUsed > K:
                return false

    return true
```

---

## 2.11 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canSplit(
    const vector<long long>& workloads,
    int workerLimit,
    long long maximumAllowedLoad
) {
    int workersUsed = 1;
    long long currentWorkerLoad = 0;

    for (long long workload : workloads) {

        if (workload > maximumAllowedLoad)
            return false;

        if (currentWorkerLoad + workload
            <= maximumAllowedLoad) {

            currentWorkerLoad += workload;
        }
        else {
            workersUsed++;
            currentWorkerLoad = workload;

            if (workersUsed > workerLimit)
                return false;
        }
    }

    return true;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfWorkloads;
    int numberOfWorkers;

    cin >> numberOfWorkloads
        >> numberOfWorkers;

    vector<long long> workloads(numberOfWorkloads);

    long long low = 0;
    long long high = 0;

    for (long long& workload : workloads) {
        cin >> workload;

        low = max(low, workload);
        high += workload;
    }

    long long answer = high;

    while (low <= high) {

        long long mid =
            low + (high - low) / 2;

        if (canSplit(
                workloads,
                numberOfWorkers,
                mid
            )) {

            answer = mid;
            high = mid - 1;
        }
        else {
            low = mid + 1;
        }
    }

    cout << answer << '\n';
    return 0;
}
```

---

## 2.12 Complexity

Let:

```text
N = number of workloads
S = sum of workloads
M = maximum workload
```

Search space:

```text
[M,S]
```

Each feasibility check scans the array:

```text
O(N)
```

Number of binary-search iterations:

```text
O(log(S-M+1))
```

Therefore:

```text
Time:
O(
    N * log(S-M+1)
)
```

Often written simply as:

```text
O(N log S)
```

Extra space:

```text
O(1)
```

apart from the input array.

---

## 2.13 Recognition Model

When you see:

```text
split array into K contiguous parts

minimize:
maximum part sum
```

immediately think:

```text
ANSWER = maximum allowed part sum
```

Then:

```text
low  = max element
high = total sum
```

Predicate:

```text
can(X)
=
can we split into <= K parts
with every part sum <= X?
```

Monotonicity:

```text
X too small
→ False

X large enough
→ True

larger X
→ remains True
```

So:

```text
F F F F T T T T
        ^
        |
    first True
```

---

# 3. Binary Search Templates

## 3.1 First True — F F F T T T

Use when asking for:

```text
minimum feasible X
```

Boolean shape:

```text
F F F F F T T T T
          ^
          |
      first True
```

Template:

```cpp
long long firstTrue(
    long long low,
    long long high
) {
    long long answer = high;

    while (low <= high) {

        long long mid =
            low + (high - low) / 2;

        if (predicate(mid)) {
            answer = mid;
            high = mid - 1;
        }
        else {
            low = mid + 1;
        }
    }

    return answer;
}
```

Mental rule:

```text
TRUE
→ store mid
→ search LEFT
```

because:

```text
mid works,
but maybe something smaller also works
```

---

## 3.2 Last True — T T T F F F

Use when asking for:

```text
maximum feasible X
```

Boolean shape:

```text
T T T T T F F F F
        ^
        |
     last True
```

Template:

```cpp
long long lastTrue(
    long long low,
    long long high
) {
    long long answer = low;

    while (low <= high) {

        long long mid =
            low + (high - low) / 2;

        if (predicate(mid)) {
            answer = mid;
            low = mid + 1;
        }
        else {
            high = mid - 1;
        }
    }

    return answer;
}
```

Mental rule:

```text
TRUE
→ store mid
→ search RIGHT
```

because:

```text
mid works,
but maybe something larger also works
```

---

## 3.3 Common Midpoint Rule

Use:

```cpp
long long mid =
    low + (high - low) / 2;
```

rather than:

```cpp
(low + high) / 2
```

Reason:

```text
low + high
```

may overflow for large bounds.

ASCII:

```text
low----------------mid----------------high
 |                                      |
 +------------ safe difference ---------+
```

---

# 4. Common Mistakes

### Mistake 1 — Binary searching without proving monotonicity

Wrong thought:

```text
"I can test X,
so I can binary search X."
```

Not enough.

You need:

```text
all False then all True
```

or:

```text
all True then all False
```

---

### Mistake 2 — Wrong search bounds

For Fair Workload:

```text
low = 0
```

works mathematically but wastes search space and hides the key observation.

Better:

```text
low = max(workload)
```

because any lower value is impossible.

---

### Mistake 3 — Searching the wrong boundary

If the predicate is:

```text
F F F T T T
```

and the problem says:

```text
minimum feasible
```

you need:

```text
first True
```

not last True.

---

### Mistake 4 — Reordering workloads

The lecture explicitly says:

```text
can't change order of workloads
```

So this is invalid:

```text
sort workloads
```

The feasibility check must preserve original order.

---

### Mistake 5 — Starting a new worker too early

For fixed `X`, fill the current worker as much as possible.

Do not create unnecessary cuts.

```text
longest possible prefix per worker
```

minimizes workers used.

---

### Mistake 6 — Forgetting one item may already exceed X

If:

```text
workload > X
```

then:

```text
can(X) = False
```

immediately.

One indivisible workload cannot fit into any worker's capacity.

---

### Mistake 7 — Confusing exact binary search with binary search on answer

Classic:

```text
find value 55
```

Answer search:

```text
find minimum capacity X
such that can(X) is True
```

Same binary-search engine.

Different search space.

---

# 5. Final Mental Model

```text
                 BINARY SEARCH ON ANSWER
                          |
                          v
               define candidate answer X
                          |
                          v
                 build predicate can(X)
                          |
                          v
                 prove monotonicity
                          |
              +-----------+-----------+
              |                       |
              v                       v
      F F F F T T T           T T T T F F F
              |                       |
              v                       v
          FIRST TRUE               LAST TRUE
              |                       |
              +-----------+-----------+
                          |
                          v
                 binary search boundary
```

For Fair Workload:

```text
workloads
10 20 30 10 10 20 30 20

K = 3

        |
        v

candidate X
"maximum load any worker may receive"

        |
        v

can(X)?
split in original order
using <= K workers?

        |
        v

greedy feasibility check

        |
        v

F F F F F T T T T
          ^
          |
      minimum feasible X
```

## One Recognition Sentence

```text
If the answer is a number
and I can efficiently check
"Is answer X possible?",
then test whether that check is monotonic.
```

If yes:

```text
binary search on answer
```

## One Coding Sentence

```text
Optimization problem
→ convert to predicate
→ binary search the transition.
```
