# TLE Eliminators — Binary Search on Answer
## Optimized TLE-Style Notes — Monotonic Predicate + Math Toolkit + Fair Workload

> **Source:** supplied TLE Eliminators lecture material for **Binary Search on Answer**.
>
> **Main idea**
>
> ```text
> answer is not directly known
>        |
>        v
> guess candidate answer X
>        |
>        v
> build can(X)
>        |
>        v
> prove can(X) is monotonic
>        |
>        v
> binary search the boundary
> ```
>
> **Problem flow**
>
> ```text
> What it asks
> → small dry run
> → candidate answer
> → search bounds
> → predicate can(X)
> → monotonicity proof
> → feasibility check
> → binary-search boundary
> → detailed dry run
> → C++17
> → complexity
> → recognition model
> ```
>
> **Important:** the lecture introduces `sqrt(x)` as a later application, but does not develop that problem here. This note keeps only the prerequisite math needed to recognize that form.

---

# Clickable Table of Contents

- [0. Class Map](#0-class-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 Binary Search + O(log N)](#11-binary-search--olog-n)
  - [1.2 Monotonicity](#12-monotonicity)
  - [1.3 Sorted-Array Boundary Search](#13-sorted-array-boundary-search)
  - [1.4 Count Values in L <= a[i] <= R](#14-count-values-in-l--ai--r)
  - [1.5 Predicate + First True / Last True](#15-predicate--first-true--last-true)
  - [1.6 Binary Search on Answer](#16-binary-search-on-answer)
  - [1.7 Math Toolkit for `can(X)`](#17-math-toolkit-for-canx)
    - [1.7.1 Inequalities + Ceil / Floor](#171-inequalities--ceil--floor)
    - [1.7.2 Rearranging Feasibility Conditions](#172-rearranging-feasibility-conditions)
    - [1.7.3 Product Constraints + Overflow](#173-product-constraints--overflow)
    - [1.7.4 Deriving LOW and HIGH](#174-deriving-low-and-high)
    - [1.7.5 Monotonic Implication](#175-monotonic-implication)
    - [1.7.6 Common Math Forms](#176-common-math-forms)
  - [1.8 Recognition Checklist](#18-recognition-checklist)
- [2. Problem 1 — Fair Workload](#2-problem-1--fair-workload)
  - [2.1 What It Asks](#21-what-it-asks)
  - [2.2 Small Dry Run First](#22-small-dry-run-first)
  - [2.3 Candidate Answer + Predicate](#23-candidate-answer--predicate)
  - [2.4 Search Space](#24-search-space)
  - [2.5 Why `can(X)` Is Monotonic](#25-why-canx-is-monotonic)
  - [2.6 Greedy Feasibility Check](#26-greedy-feasibility-check)
  - [2.7 Why the Greedy Check Is Correct](#27-why-the-greedy-check-is-correct)
  - [2.8 Lecture Dry Run — X = 30, 50, 70](#28-lecture-dry-run--x--30-50-70)
  - [2.9 Full Binary Search Dry Run](#29-full-binary-search-dry-run)
  - [2.10 Algorithm](#210-algorithm)
  - [2.11 C++17](#211-c17)
  - [2.12 Complexity](#212-complexity)
  - [2.13 Recognition Model](#213-recognition-model)
- [3. Binary Search Templates](#3-binary-search-templates)
  - [3.1 First True](#31-first-true)
  - [3.2 Last True](#32-last-true)
  - [3.3 Safe Midpoint](#33-safe-midpoint)
- [4. Common Mistakes](#4-common-mistakes)
- [5. Final Mental Model](#5-final-mental-model)

---

# 0. Class Map

```text
BINARY SEARCH
│
├── Sorted data
│   ├── exact value
│   ├── first / last valid index
│   └── count in range
│
└── Binary Search on Answer
    │
    ├── choose answer variable X
    ├── derive LOW / HIGH
    ├── define can(X)
    ├── prove monotonicity
    └── find boundary
        ├── first True
        └── last True
```

For **Fair Workload**:

```text
minimize maximum workload
        |
        v
guess maximum allowed load X
        |
        v
can we split in order
using <= K workers?
        |
        v
F F F F T T T T
        ^
        |
    first True
```

---

# 1. Prerequisites

## 1.1 Binary Search + O(log N)

Binary search repeatedly removes about half of the remaining search space.

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

N = 2^k

k = log2(N)
```

Therefore:

```text
Time = O(log N)
```

### Tiny dry run

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

discard the left half.

```text
10  20  35  [40]  50  55  60
              |
              v
            RIGHT
```

Now:

```text
L = 4
R = 6
mid = 5

a[mid] = 55
```

Found.

---

## 1.2 Monotonicity

Binary search needs an ordered behavior so that one whole side can be discarded.

### Increasing

```text
x increases
→ f(x) increases
```

Example from the lecture:

```text
f(x) = x^2 + 5x
```

for positive `x`.

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

### Decreasing

```text
x increases
→ f(x) decreases
```

Binary search still works; comparison directions reverse.

### Non-monotonic

```text
up
down
up
down
```

cannot be binary-searched by one simple boundary because a midpoint does not tell us which entire side is invalid.

---

## 1.3 Sorted-Array Boundary Search

A sorted array can be viewed as:

```text
f(index) = a[index]
```

which is monotonic in `index`.

A useful form is:

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

becomes:

```text
10  20  30  40  50  60  70  80
 T   T   T   T   F   F   F   F
                 ^
                 |
             last True
```

Answer:

```text
index = 3
```

Number of values `<= 42`:

```text
3 + 1
=
4
```

This is the same boundary idea later used for answer-space predicates.

---

## 1.4 Count Values in L <= a[i] <= R

Given sorted:

```text
1 2 3 5 6 7 8 10 11 16 20
```

and:

```text
L = 4
R = 12
```

valid values:

```text
5 6 7 8 10 11
```

Answer:

```text
6
```

### Derivation

Let:

```text
A = count(values <= R)
B = count(values <= L - 1)
```

Then:

```text
answer = A - B
```

For this example:

```text
A = count(values <= 12)
  = 9

B = count(values <= 3)
  = 3
```

So:

```text
answer
=
9 - 3
=
6
```

ASCII:

```text
1  2  3 | 5  6  7  8  10  11 | 16  20
--------+----------------------+--------
 <= 3       4 <= value <= 12       >12
```

### C++ STL

```cpp
long long countLessOrEqualR =
    upper_bound(a.begin(), a.end(), R)
    - a.begin();

long long countLessOrEqualLMinusOne =
    upper_bound(a.begin(), a.end(), L - 1)
    - a.begin();

long long answer =
    countLessOrEqualR
    - countLessOrEqualLMinusOne;
```

Per query:

```text
O(log N)
```

---

## 1.5 Predicate + First True / Last True

A predicate returns:

```text
True
or
False
```

Example:

```text
P(x) = (x > 10)
```

Dry run:

```text
x = 7   → False
x = 10  → False
x = 11  → True
x = 20  → True
```

A predicate is binary-searchable when its boolean values are monotonic.

### Shape A — Last True

```text
T T T T T F F F
        ^
        |
     last True
```

Typical question:

```text
What is the largest X
for which the condition holds?
```

### Shape B — First True

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

### One rule

```text
MINIMUM feasible X
→ FIRST TRUE

MAXIMUM feasible X
→ LAST TRUE
```

Do not separately memorize many binary-search forms.

First identify:

```text
which boolean boundary
the problem is asking for
```

---

## 1.6 Binary Search on Answer

Classic binary search:

```text
search inside sorted data
```

Binary Search on Answer:

```text
search inside ordered candidate answers
```

Suppose the original problem says:

```text
minimize some maximum value
```

Choose a candidate:

```text
X
```

and ask:

```text
"If I claim the answer is at most X,
is that possible?"
```

That creates:

```text
can(X)
```

If:

```text
can(X)
```

is monotonic, the optimization problem becomes a boundary-search problem.

### Core transformation

```text
optimization
     |
     v
candidate answer X
     |
     v
yes/no feasibility check
     |
     v
monotonic boolean sequence
     |
     v
binary search boundary
```

---

## 1.7 Math Toolkit for `can(X)`

The binary-search loop is usually simple.

The harder part is often:

```text
turn the statement
into a correct mathematical predicate
```

The math needed most often is:

```text
inequalities
floor / ceil
algebraic rearrangement
overflow-safe comparison
search-space bounds
monotonic implication
```

---

### 1.7.1 Inequalities + Ceil / Floor

Suppose:

```text
time * speed >= work
```

with:

```text
time = 5
work = 17
```

We want minimum integer `speed`.

Start:

```text
5 * speed >= 17
```

Divide by positive `5`:

```text
speed >= 17 / 5
```

Real value:

```text
17 / 5
=
3.4
```

But speed is integer:

```text
speed >= 4
```

Therefore:

```text
minimum speed
=
ceil(17 / 5)
=
4
```

Boolean shape:

```text
speed:

0   1   2   3   4   5   6
F   F   F   F   T   T   T
                ^
                |
            first feasible
```

### Integer ceil formula

For positive integers:

```text
ceil(a / b)
=
(a + b - 1) / b
```

Dry run:

```text
a = 17
b = 5
```

```text
ceil(17/5)
=
(17 + 5 - 1) / 5
=
21 / 5
=
4
```

Visual:

```text
17 items
capacity 5

[1 2 3 4 5]
[6 7 8 9 10]
[11 12 13 14 15]
[16 17]

groups = 4
```

### Floor example

How many complete pieces of length `4` can be cut from length `19`?

```text
19 / 4
=
4
```

because:

```text
4 + 4 + 4 + 4
=
16
```

with:

```text
3
```

remaining.

So:

```text
pieces = floor(19/4)
       = 4
```

### Sign reminder

If multiplying/dividing an inequality by a negative value:

```text
inequality direction flips
```

Example:

```text
-2x <= 10

x >= -5
```

Most answer-search quantities such as:

```text
capacity
time
speed
distance
```

are positive, so this issue appears less often.

---

### 1.7.2 Rearranging Feasibility Conditions

Suppose:

```text
speed * time >= work
```

with:

```text
work = 43
time = 6
```

Start:

```text
6 * speed >= 43
```

Rearrange:

```text
speed >= 43 / 6
```

Since speed is integer:

```text
speed >= ceil(43/6)
```

```text
43 / 6
=
7 remainder 1
```

Therefore:

```text
minimum speed = 8
```

Check:

```text
speed = 7

7 * 6
=
42
<
43

False
```

```text
speed = 8

8 * 6
=
48
>=
43

True
```

ASCII:

```text
speed:
1 2 3 4 5 6 7 8 9 10 ...
F F F F F F F T T T  ...
              ^
              |
          first True
```

### Avoid unnecessary floating point

Instead of:

```text
distance / speed <= timeLimit
```

for positive values, rearrange:

```text
distance <= speed * timeLimit
```

This often gives a cleaner integer predicate.

---

### 1.7.3 Product Constraints + Overflow

A common boundary:

```text
x * x <= N
```

appears in integer square-root style problems.

Naive:

```cpp
mid * mid <= n
```

can overflow for large `mid`.

For positive values, use:

```cpp
mid <= n / mid
```

### Dry run

```text
N = 30
```

Try:

```text
mid = 5
```

```text
5 <= 30/5
5 <= 6

True
```

Try:

```text
mid = 6
```

```text
6 <= 30/6
6 <= 5

False
```

So:

```text
largest x with x*x <= 30
=
5
```

Boolean shape:

```text
0 1 2 3 4 5 6 7 ...
T T T T T T F F ...
          ^
          |
       last True
```

Before writing:

```cpp
a * b <= c
```

ask:

```text
Can a*b overflow?
```

If yes, try an equivalent division-based comparison.

---

### 1.7.4 Deriving LOW and HIGH

Good answer-space bounds come from necessary and guaranteed-feasible conditions.

For Fair Workload:

```text
workloads:
10 20 30 10
```

### LOW

One workload cannot be split.

Largest job:

```text
30
```

Any:

```text
X < 30
```

fails immediately.

Therefore:

```text
LOW = max(a[i])
```

### HIGH

One worker can do everything.

```text
10 + 20 + 30 + 10
=
70
```

Therefore:

```text
HIGH = sum(a[i])
```

Search only:

```text
[30,70]
```

ASCII:

```text
impossible                    guaranteed feasible
    |                                  |
    v                                  v
----29----[30--------------------------70]----
          ^
          |
         LOW
```

### General rule

```text
LOW
=
smallest value that could possibly work

HIGH
=
some value guaranteed to work
```

`HIGH` does not need to be tight.

It only needs to be safely feasible.

---

### 1.7.5 Monotonic Implication

Before binary searching a predicate, prove an implication.

For minimum-feasible problems:

```text
can(X) = True
and
Y >= X

⇒

can(Y) = True
```

### Dry run

Suppose capacity:

```text
X = 50
```

works with:

```text
[10 20] | [30 10]
```

loads:

```text
30
40
```

Then capacity:

```text
Y = 60
```

also works because:

```text
30 <= 60
40 <= 60
```

The same solution remains valid.

So:

```text
can(50) = True
⇒
can(60) = True
```

This gives:

```text
F F F F T T T T
```

For maximum-feasible problems, the direction is often:

```text
can(X) = True
and
Y <= X

⇒

can(Y) = True
```

giving:

```text
T T T T F F F F
```

---

### 1.7.6 Common Math Forms

These patterns appear frequently inside `can(X)`.

#### A. Number of groups / trips

```text
groups =
ceil(items / capacity)
```

Example:

```text
items = 23
capacity = 6

groups
=
ceil(23/6)
=
4
```

#### B. Number of pieces

```text
pieces =
floor(length / pieceSize)
```

Example:

```text
length = 25
pieceSize = 6

pieces
=
25 / 6
=
4
```

#### C. Minimum rate

```text
rate * time >= work
```

becomes:

```text
rate >= ceil(work / time)
```

Example:

```text
work = 31
time = 5

rate >= 7
```

Check:

```text
6 * 5 = 30 → False
7 * 5 = 35 → True
```

#### D. Square / product boundary

```text
x*x <= N
```

prefer:

```text
x <= N/x
```

Example:

```text
N = 50

7 <= 50/7
7 <= 7
True

8 <= 50/8
8 <= 6
False
```

So:

```text
floor(sqrt(50))
=
7
```

#### E. Search-space bounds

```text
LOW  = necessary lower limit
HIGH = guaranteed-feasible upper limit
```

For Fair Workload:

```text
LOW  = max(a[i])
HIGH = sum(a[i])
```

---

## 1.8 Recognition Checklist

Binary Search on Answer should enter your mind when you see:

```text
minimize maximum
maximize minimum
minimum capacity
minimum time
minimum speed
maximum distance
maximum possible value
```

Before coding, answer:

```text
1. What exactly is candidate answer X?

2. What mathematical range contains X?

3. What is LOW?

4. What is HIGH?

5. What does can(X) mean?

6. How do I compute can(X) efficiently?

7. Why is can(X) monotonic?

8. Is the shape:
   F F F T T T
   or
   T T T F F F?

9. Do I need:
   first True
   or
   last True?

10. Does can(X) need:
    floor / ceil
    inequality rearrangement
    overflow-safe comparison
    greedy feasibility?
```

If these are clear, the binary-search loop is usually the easy part.

---

# 2. Problem 1 — Fair Workload

## 2.1 What It Asks

Given an ordered workload array:

```text
a[0], a[1], ..., a[n-1]
```

split it among:

```text
K workers
```

such that:

```text
1. workload order is preserved
2. each worker receives a contiguous block
3. every workload is assigned
```

Minimize:

```text
maximum total load
assigned to any one worker
```

Lecture example:

```text
10 20 30 40 50 | 60 70 | 80 90
```

Worker loads:

```text
150
130
170
```

For this partition:

```text
maximum load = 170
```

The optimization question is:

```text
Can we make that maximum smaller?
```

---

## 2.2 Small Dry Run First

Use the smaller workload array:

```text
10 20 30 10 10 20 30 20
```

with:

```text
K = 3
```

Try candidate maximum load:

```text
X = 70
```

Fill worker 1:

```text
10 + 20 + 30 + 10
=
70
```

So:

```text
[10 20 30 10]
```

Fill worker 2:

```text
10 + 20 + 30
=
60
```

So:

```text
[10 20 30]
```

Remaining:

```text
[20]
```

goes to worker 3.

Final split:

```text
10 20 30 10 | 10 20 30 | 20
------------+-----------+----
     70           60       20
```

Workers used:

```text
3
```

Since:

```text
3 <= K
```

we get:

```text
can(70) = True
```

---

## 2.3 Candidate Answer + Predicate

Original problem:

```text
minimize
maximum worker load
```

Choose candidate:

```text
X
=
maximum load allowed for any worker
```

Now ask:

```text
Can I split the workload array
into at most K contiguous groups
such that every group sum <= X?
```

Define:

```text
can(X)
```

as the answer to that question.

So:

```text
optimization problem
        |
        v
yes/no feasibility problem
```

We now want:

```text
smallest X
such that can(X) = True
```

Therefore:

```text
FIRST TRUE
```

---

## 2.4 Search Space

For:

```text
10 20 30 10 10 20 30 20
```

largest single workload:

```text
30
```

Total:

```text
10+20+30+10+10+20+30+20
=
150
```

So:

```text
LOW  = 30
HIGH = 150
```

Why?

```text
X < 30
```

is impossible because the workload `30` cannot be split.

And:

```text
X = 150
```

is always feasible because one worker can take everything.

ASCII:

```text
30                                      150
|-----------------------------------------|
^                                         ^
necessary lower                      guaranteed
bound                                feasible bound
```

---

## 2.5 Why `can(X)` Is Monotonic

Suppose:

```text
can(70) = True
```

So some valid partition has every group sum:

```text
<= 70
```

Now choose:

```text
Y = 80
```

The exact same partition still satisfies:

```text
groupSum <= 70 <= 80
```

Therefore:

```text
can(70) = True
⇒
can(80) = True
```

In general:

```text
can(X) = True
and
Y >= X

⇒
can(Y) = True
```

So the boolean shape is:

```text
F F F F F T T T T
          ^
          |
      first True
```

This proves binary search is valid.

---

## 2.6 Greedy Feasibility Check

For fixed candidate:

```text
X
```

scan left to right.

Maintain:

```text
workersUsed
currentWorkerLoad
```

Try to place each new workload on the current worker.

If:

```text
currentWorkerLoad + workload <= X
```

keep it there.

Otherwise:

```text
start a new worker
```

### Dry run

```text
workloads:
10 20 30 10

X = 50
```

Worker 1:

```text
10 + 20
=
30
```

Try adding `30`:

```text
30 + 30
=
60
>
50
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

        if workersUsed > K:
            return false

return true
```

---

## 2.7 Why the Greedy Check Is Correct

For a fixed `X`, the check fills each worker with the **longest possible prefix** whose sum does not exceed `X`.

Suppose:

```text
currentLoad = 50
nextWorkload = 20
X = 70
```

Then:

```text
50 + 20 = 70
```

So placing `20` on the current worker is allowed.

Would cutting before `20` help?

No.

An earlier cut:

```text
uses a new worker sooner
```

and leaves at least as much remaining work for later workers.

So an earlier cut cannot reduce the number of workers used.

### Tiny comparison

```text
10 20 30 10
X = 50
```

Greedy:

```text
[10 20] | [30 10]

workers = 2
```

Cut earlier:

```text
[10] | [20 30] | [10]

workers = 3
```

The greedy grouping uses no more workers than an earlier-cut strategy.

Therefore, for fixed `X`:

```text
greedy workersUsed <= K
→ feasible
```

and:

```text
greedy workersUsed > K
→ no valid split into <= K workers exists
```

This is the proof behind `can(X)`.

---

## 2.8 Lecture Dry Run — X = 30, 50, 70

Use:

```text
workloads:
10 20 30 10 10 20 30 20

K = 3
```

### Candidate X = 30

Greedy:

```text
[10 20] | [30] | [10 10] | [20] | [30] | [20]
```

Workers:

```text
6
```

Since:

```text
6 > 3
```

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

Workers:

```text
4
```

Since:

```text
4 > 3
```

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

Workers:

```text
3
```

So:

```text
can(70) = True
```

### Predicate picture

```text
30----------------50----------------70
F                 F                 T
                  |-----------------|
                    transition here
```

The minimum feasible answer lies in:

```text
(50,70]
```

---

## 2.9 Full Binary Search Dry Run

Search:

```text
LOW  = 30
HIGH = 150
```

Need:

```text
first X such that can(X) = True
```

### Iteration 1

```text
L = 30
R = 150

mid = 90
```

```text
can(90) = True
```

So:

```text
answer = 90
R = 89
```

Move left.

---

### Iteration 2

```text
L = 30
R = 89

mid = 59
```

Greedy split needs:

```text
4 workers
```

So:

```text
can(59) = False
```

Move right:

```text
L = 60
```

---

### Iteration 3

```text
L = 60
R = 89

mid = 74
```

```text
can(74) = True
```

So:

```text
answer = 74
R = 73
```

---

### Iteration 4

```text
L = 60
R = 73

mid = 66
```

Feasible with at most 3 workers:

```text
can(66) = True
```

So:

```text
answer = 66
R = 65
```

---

### Iteration 5

```text
L = 60
R = 65

mid = 62
```

Feasible:

```text
can(62) = True
```

So:

```text
answer = 62
R = 61
```

---

### Iteration 6

```text
L = 60
R = 61

mid = 60
```

Greedy split:

```text
[10 20 30] | [10 10 20] | [30 20]
```

Loads:

```text
60
40
50
```

Workers:

```text
3
```

Therefore:

```text
can(60) = True
```

Update:

```text
answer = 60
R = 59
```

Now:

```text
L = 60
R = 59
```

Stop.

Final answer:

```text
60
```

### Search visualization

```text
30........................................150
                     90 T
                     |
                     v
30..................89
         59 F
          |
          v
60.................89
       74 T
       |
       v
60.....73
   66 T
   |
   v
60..65
 62 T
 |
 v
60 61
60 T
 |
 v

answer = 60
```

---

## 2.10 Algorithm

```text
low =
    maximum workload

high =
    sum of workloads

answer = high

while low <= high:

    mid =
        low + (high-low)/2

    if can(mid):

        answer = mid

        // feasible
        // try smaller
        high = mid - 1

    else:

        // too small
        low = mid + 1

return answer
```

Feasibility:

```text
can(X):

    workersUsed = 1
    currentLoad = 0

    for workload:

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

Each feasibility check:

```text
O(N)
```

Binary-search iterations:

```text
O(log(S-M+1))
```

Total:

```text
O(
    N * log(S-M+1)
)
```

Common shorthand:

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

think:

```text
candidate X
=
maximum allowed part sum
```

Then:

```text
LOW  = max element
HIGH = total sum
```

Predicate:

```text
can(X)
=
can we split in order
into <= K groups
with each sum <= X?
```

Shape:

```text
X too small
→ False

X large enough
→ True

larger X
→ stays True
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

## 3.1 First True

Use for:

```text
minimum feasible X
```

Shape:

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
mid works
but maybe a smaller value also works
```

---

## 3.2 Last True

Use for:

```text
maximum feasible X
```

Shape:

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
mid works
but maybe a larger value also works
```

---

## 3.3 Safe Midpoint

Use:

```cpp
long long mid =
    low + (high - low) / 2;
```

instead of:

```cpp
(low + high) / 2;
```

because:

```text
low + high
```

may overflow.

ASCII:

```text
low----------------mid----------------high
 |                                      |
 +------------ safe difference ---------+
```

---

# 4. Common Mistakes

| Mistake | Fix |
|---|---|
| Binary searching just because `can(X)` can be computed | First prove `can(X)` is monotonic |
| Using weak/random bounds | Derive `LOW` from a necessary condition and `HIGH` from a guaranteed-feasible scenario |
| Searching the wrong boundary | Minimum feasible → first True; maximum feasible → last True |
| Sorting Fair Workload input | Order must remain unchanged |
| Cutting a worker's block too early | Fill the longest possible prefix within `X` |
| Forgetting `workload > X` | Immediate `False` |
| Using floating point when integer algebra works | Rearrange inequalities / use floor-ceil |
| Multiplying values that may overflow | Use safe algebra such as division-based comparison |
| Confusing classic BS with answer BS | Classic searches data; answer BS searches candidate answers |

---

# 5. Final Mental Model

```text
                BINARY SEARCH ON ANSWER
                          |
                          v
                choose answer variable X
                          |
                          v
                   derive LOW / HIGH
                          |
                          v
                     build can(X)
                          |
                          v
                  prove monotonicity
                          |
             +------------+------------+
             |                         |
             v                         v
      F F F T T T                 T T T F F F
             |                         |
             v                         v
        FIRST TRUE                 LAST TRUE
             |                         |
             +------------+------------+
                          |
                          v
                 binary search boundary
```

For Fair Workload:

```text
workloads
        |
        v
X = maximum allowed worker load
        |
        v
greedily pack longest possible
contiguous prefix per worker
        |
        v
workersUsed <= K ?
        |
        v
F F F F T T T T
        ^
        |
    minimum feasible X
```

## Math checklist inside `can(X)`

```text
inequality?
floor / ceil?
integer rearrangement?
overflow?
LOW?
HIGH?
which direction stays feasible?
```

## One recognition sentence

```text
If the answer is a number
and I can efficiently test
"Is candidate X possible?",
check whether that test is monotonic.
```

If yes:

```text
binary search on answer
```

## One coding sentence

```text
optimization
→ predicate
→ monotonic boundary
→ binary search
```
