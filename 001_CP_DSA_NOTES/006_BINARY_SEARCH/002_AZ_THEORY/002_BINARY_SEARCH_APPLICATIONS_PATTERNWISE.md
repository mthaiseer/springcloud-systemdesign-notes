# AlgoZenith — Binary Search Applications
## Pattern-Wise Binary Search on Answer
### TLE-Style Self-Study Notes

> **Source:** supplied AlgoZenith Binary Search Applications lecture screenshots.
>
> The lecture's main idea is that most application-style Binary Search is **Binary Search on Answer**. The screenshots group the common checker styles as:
>
> ```text
> Binary Search Applications
> │
> ├── Binary Search on Answer
> │   ├── Atomic Item Contribution
> │   ├── Sweep / Coverage
> │   └── 2D Variations
> │
> └── Smaller set: BS from every start
>     → often combines with another technique
> ```
>
> Problems/forms visible in the lecture:
>
> ```text
> Painter Partition
> CSES Array Division
> CSES Factory Machines
> CSES Multiplication Table
> AlgoZenith Kth Sum Value
> ```
>
> **Teaching flow**
>
> ```text
> prerequisite
> → recognize pattern
> → what the problem asks
> → expected result
> → derive check(mid)
> → prove monotonicity
> → compact dry run
> → C++17
> → complexity
> → recognition model
> ```

---

# Clickable Table of Contents

- [0. Pattern Map](#0-pattern-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 Search an Answer, Not an Index](#11-search-an-answer-not-an-index)
  - [1.2 Feasibility Function](#12-feasibility-function)
  - [1.3 First True = Minimum Feasible](#13-first-true--minimum-feasible)
  - [1.4 Choosing Search Bounds](#14-choosing-search-bounds)
  - [1.5 Why Greedy Appears Inside Check](#15-why-greedy-appears-inside-check)
  - [1.6 Atomic Contribution](#16-atomic-contribution)
  - [1.7 Sweep / Coverage](#17-sweep--coverage)
  - [1.8 Counting Instead of Constructing](#18-counting-instead-of-constructing)
  - [1.9 Overflow Checklist](#19-overflow-checklist)
  - [1.10 Recognition Checklist](#110-recognition-checklist)
- [2. Pattern 1 — Sweep / Contiguous Partition](#2-pattern-1--sweep--contiguous-partition)
  - [2.1 Painter Partition](#21-painter-partition)
  - [2.2 CSES Array Division](#22-cses-array-division)
- [3. Pattern 2 — Atomic Item Contribution](#3-pattern-2--atomic-item-contribution)
  - [3.1 CSES Factory Machines](#31-cses-factory-machines)
- [4. Pattern 3 — 2D Counting](#4-pattern-3--2d-counting)
  - [4.1 CSES Multiplication Table](#41-cses-multiplication-table)
  - [4.2 AlgoZenith Kth Sum Value](#42-algozenith-kth-sum-value)
- [5. Pattern Comparison](#5-pattern-comparison)
- [6. Common Mistakes](#6-common-mistakes)
- [7. Practice Order](#7-practice-order)
- [8. Final Mental Model](#8-final-mental-model)

---

# 0. Pattern Map

The outer Binary Search is almost identical across these problems.

What changes is:

```text
check(mid)
```

Pattern map:

| Pattern | `mid` means | `check(mid)` |
|---|---|---|
| Sweep / Coverage | allowed max capacity/time | greedily scan and count groups/resources |
| Atomic Contribution | total available time/value | sum independent contributions |
| 2D Counting | candidate kth value | count implicit values `<= mid` |

Master view:

```text
candidate answer = mid
        |
        v
     check(mid)
        |
   +----+----+
   |         |
 false      true
   |         |
 too small   maybe answer
```

For minimum-answer problems:

```text
False False False False True True True
                        ^
                        |
                  first feasible
```

So we binary-search the **first True**.

---

# 1. Prerequisites

## 1.1 Search an Answer, Not an Index

Classical Binary Search:

```text
search space = indices
```

Binary Search on Answer:

```text
search space = possible answer values
```

Example:

```text
Find minimum time needed.
```

Guess:

```text
T = mid
```

Ask:

```text
Can the work be completed within T?
```

Conceptually:

```text
T:      8  9 10 11 12 13 14 15
check:  0  0  0  0  1  1  1  1
                   ^
                   |
              minimum time
```

---

## 1.2 Feasibility Function

Before coding, say `check(mid)` as one English sentence.

### Painter Partition

```text
check(T)
=
Can all boards be painted within T time
using at most K painters?
```

### Factory Machines

```text
check(T)
=
Can the machines produce at least TARGET
products within T seconds?
```

### Multiplication Table

```text
check(X)
=
Are there at least K table entries <= X?
```

### Kth Sum Value

```text
check(X)
=
Are there at least K pair sums <= X?
```

If this sentence is unclear, do not write Binary Search yet.

---

## 1.3 First True = Minimum Feasible

If a value works, every larger value also works:

```text
0 0 0 0 1 1 1 1
        ^
        |
    first True
```

Template:

```cpp
if (check(mid)) {
    answer = mid;
    high = mid - 1;
} else {
    low = mid + 1;
}
```

Meaning:

```text
check(mid) = true
→ mid works
→ maybe smaller also works
→ search LEFT
```

```text
check(mid) = false
→ mid is too small
→ search RIGHT
```

---

## 1.4 Choosing Search Bounds

A Binary Search on Answer needs:

```text
low  = safe lower bound
high = safe upper bound
```

The bounds do not need to be tight.
They only need to contain the answer.

### Partition problems

For positive array:

```text
low = max(arr)
high = sum(arr)
```

Why lower bound?

```text
largest item cannot be split
```

Why upper bound?

```text
one group can contain everything
```

So:

```text
max(arr) <= answer <= sum(arr)
```

### Factory Machines

If fastest machine needs:

```text
fastest
```

seconds per product and target is `T` products:

```text
high = fastest * T
```

because the fastest machine alone can finish all products by then.

### Kth-value problems

Use the minimum and maximum possible generated values.

Example pair sums:

```text
low  = min(A) + min(B)
high = max(A) + max(B)
```

---

## 1.5 Why Greedy Appears Inside Check

Binary Search answers:

```text
Which candidate value should we test?
```

The checker answers:

```text
Is that value feasible?
```

For contiguous partition problems, the checker is usually a greedy sweep.

For a fixed limit:

```text
put as much as possible in current group
```

Only create a new group when the next item would exceed the limit.

### Teaching Dry Run

```text
arr = [1,3,8,7,2,5]
limit = 12
```

```text
[1,3,8] [7,2] [5]
   12      9     5
```

Groups used:

```text
3
```

---

## 1.6 Atomic Contribution

Sometimes every item contributes independently.

Factory Machines is the lecture example.

If machine `i` needs:

```text
machineTime[i]
```

seconds per product, then in `T` seconds:

```text
contribution_i(T)
=
floor(T / machineTime[i])
```

Total:

```text
totalProduced(T)
=
Σ floor(T / machineTime[i])
```

### Teaching Dry Run

```text
machines = [2,3,7]
T = 6
```

```text
6/2 = 3
6/3 = 2
6/7 = 0
```

Total:

```text
3 + 2 + 0 = 5
```

This is the lecture's **Atomic Item Contribution** form.

---

## 1.7 Sweep / Coverage

The lecture also highlights:

```text
Sweep / Coverage
```

General checker shape:

```text
scan left → right
maintain current state
start a new resource/block only when necessary
```

Painter Partition:

```text
currentLoad + arr[i] <= limit
→ keep same painter

otherwise
→ new painter
```

---

## 1.8 Counting Instead of Constructing

For 2D / kth-value problems, avoid constructing all values.

Instead guess:

```text
X
```

and count:

```text
# generated values <= X
```

Then:

```text
count >= K
→ kth smallest <= X
→ X is feasible
```

So:

```text
find kth smallest
```

becomes:

```text
find first X such that
count(values <= X) >= K
```

Again:

```text
first True
```

---

## 1.9 Overflow Checklist

These problems often use values near:

```text
10^9
10^18
```

Use:

```cpp
long long
```

for:

```text
low
high
mid
sum
answer
count
time
```

Safe midpoint:

```cpp
long long mid = low + (high - low) / 2;
```

Stop contribution counting early when possible:

```cpp
if (count >= target) {
    return true;
}
```

---

## 1.10 Recognition Checklist

When you see:

```text
minimum X
maximum X
minimum possible maximum
minimum time
kth smallest implicit value
```

ask:

```text
1. Can I guess X?

2. Can I write check(X)?

3. Is check(X) monotonic?

4. What is inside the checker?
   - sweep?
   - atomic contribution?
   - 2D counting?

5. What are safe low/high bounds?

6. First True or Last True?
```

---

# 2. Pattern 1 — Sweep / Contiguous Partition

General form:

```text
ordered array
+
partition into <= K contiguous groups
+
cost of group = sum/load/time
+
minimize the maximum group cost
```

Core transformation:

```text
optimization
→ feasibility
```

---

# 2.1 Painter Partition

## Closest Standard Practice

[CSES — Array Division](https://cses.fi/problemset/task/1085)

---

## What It Asks

You have:

```text
N boards
K painters
arr[i] = board work/length
```

Each painter receives a **contiguous** range.

Find the minimum possible:

```text
maximum painter workload
```

---

## Expected Result

For:

```text
arr = [1,3,8,7,2,5]
K = 3
```

One partition:

```text
[1,3] [8] [7,2,5]

sum:
  4    8     14
```

Maximum:

```text
14
```

But better:

```text
[1,3,8] [7,2] [5]

sum:
   12      9    5
```

Maximum:

```text
12
```

So the optimum is at most `12`.

---

## Story Removed

```text
Given:
arr[0...N-1]
K

Split arr into at most K
contiguous groups.

Group cost = sum.

Find:
minimize(maximum group sum)
```

Recognition phrase:

```text
minimum possible maximum
```

---

## Search Space

Largest single item:

```text
max(arr) = 8
```

Total:

```text
sum(arr) = 26
```

Therefore:

```text
answer ∈ [8,26]
```

---

## Design `check(limit)`

Plain English:

```text
Can we partition the array into
at most K contiguous groups
such that every group sum <= limit?
```

---

## Greedy Sweep

For:

```text
limit = 12
```

```text
1          current=1
+3         current=4
+8         current=12
+7         would become 19 > 12
           → new painter

7          current=7
+2         current=9
+5         would become 14 > 12
           → new painter

5          current=5
```

Result:

```text
Painter 1: [1,3,8] → 12
Painter 2: [7,2]   → 9
Painter 3: [5]     → 5
```

Painters used:

```text
3
```

If:

```text
K = 3
```

then:

```text
check(12) = true
```

---

## Why It Is Monotonic

If limit `12` works, then all larger limits also work:

```text
limit:  8  9 10 11 12 13 14 15
check:  0  0  0  0  1  1  1  1
                    ^
```

We need:

```text
first True
```

---

## Compact Dry Run

```text
arr = [1,3,8,7,2,5]
K = 3
search = [8,26]

Iter 1: low=8, high=26 → mid=17 → feasible → ans=17 → high=16
Iter 2: low=8, high=16 → mid=12 → feasible → ans=12 → high=11
Iter 3: low=8, high=11 → mid=9  → not feasible → low=10
Iter 4: low=10, high=11 → mid=10 → not feasible → low=11
Iter 5: low=11, high=11 → mid=11 → not feasible → low=12

answer = 12
```

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canPartition(
    const vector<long long>& arr,
    int maxPainters,
    long long limit
) {
    int paintersUsed = 1;
    long long currentLoad = 0;

    for (long long board : arr) {
        if (board > limit) {
            return false;
        }

        if (currentLoad + board <= limit) {
            currentLoad += board;
        } else {
            paintersUsed++;
            currentLoad = board;

            if (paintersUsed > maxPainters) {
                return false;
            }
        }
    }

    return true;
}

long long minimumTime(
    const vector<long long>& arr,
    int k
) {
    long long low = *max_element(arr.begin(), arr.end());
    long long high = accumulate(arr.begin(), arr.end(), 0LL);
    long long answer = high;

    while (low <= high) {
        long long mid = low + (high - low) / 2;

        if (canPartition(arr, k, mid)) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    return answer;
}

int main() {
    vector<long long> arr = {1, 3, 8, 7, 2, 5};
    int k = 3;

    cout << minimumTime(arr, k) << '\n';
}
```

---

## Complexity

Checker:

```text
O(N)
```

Binary Search:

```text
O(log(sum(arr)))
```

Total:

```text
O(N log(sum(arr)))
```

Extra space:

```text
O(1)
```

---

## Recognition Model

```text
split array into K contiguous parts
+
minimize largest part sum
```

Think:

```text
BS on answer
+
greedy sweep checker
```

---

# 2.2 CSES Array Division

**Problem:** [CSES — Array Division](https://cses.fi/problemset/task/1085)

This is the same form as Painter Partition.

---

## What It Asks

Divide a positive array into:

```text
K contiguous subarrays
```

and minimize:

```text
maximum subarray sum
```

---

## Tiny Dry Run

```text
arr = [2,4,7,3,5]
K = 3
limit = 8
```

Sweep:

```text
[2,4] → 6

next 7:
6+7 > 8
→ new segment

[7]

next 3:
7+3 > 8
→ new segment

[3,5] → 8
```

Segments:

```text
[2,4] [7] [3,5]
```

Count:

```text
3
```

So:

```text
check(8) = true
```

---

## Pattern Connection

These are the same form:

```text
Painter Partition
Array Division
Book Allocation
Split Array Largest Sum
```

Mental model:

```text
contiguous partition
+
minimize maximum sum
```

---

# 3. Pattern 2 — Atomic Item Contribution

General form:

```text
for each item:
    total += contribution(item, mid)

check(mid):
    total >= target ?
```

Each item contributes independently.

---

# 3.1 CSES Factory Machines

**Problem:** [CSES — Factory Machines](https://cses.fi/problemset/task/1620)

---

## What It Asks

There are `N` machines.

Machine `i` needs:

```text
machineTime[i]
```

seconds to make one product.

All machines work simultaneously.

Find the minimum time to produce:

```text
target
```

products.

---

## Story Removed

```text
For candidate time T:

machine i produces:
floor(T / machineTime[i])

Find minimum T such that:

Σ floor(T / machineTime[i])
>= target
```

That equation is the complete modelling step.

---

## Why the Formula Works

Suppose one machine needs:

```text
3 sec/product
```

In:

```text
10 sec
```

it finishes at:

```text
3, 6, 9
```

So it makes:

```text
3 products
```

And:

```text
floor(10 / 3) = 3
```

---

## Atomic Contribution Dry Run

```text
machines = [2,3,7]
T = 6
```

```text
2-sec machine → 6/2 = 3
3-sec machine → 6/3 = 2
7-sec machine → 6/7 = 0
```

Total:

```text
5 products
```

ASCII:

```text
2s machine: |--P--|--P--|--P--|  → 3
3s machine: |---P---|---P---|     → 2
7s machine: |------not done-----|  → 0
```

---

## Design `check(T)`

```text
Can all machines together produce
at least target products in T seconds?
```

Mathematically:

```text
check(T)
=
Σ floor(T / machineTime[i]) >= target
```

---

## Why It Is Monotonic

If enough products can be made in `T`, then enough can also be made in any larger time.

```text
T:      ... 5 6 7 8 ...
check:  ... 0 1 1 1 ...
            ^
```

Need:

```text
first True
```

---

## Search Bounds

```text
low = 0
```

Safe high:

```text
high = fastestMachine * target
```

---

## Compact Dry Run

```text
machines = [2,3,7]
target = 5

T=5 → 5/2 + 5/3 + 5/7 = 2+1+0 = 3 → false
T=6 → 6/2 + 6/3 + 6/7 = 3+2+0 = 5 → true

minimum time = 6
```

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canProduce(
    const vector<long long>& machineTime,
    long long target,
    long long time
) {
    long long produced = 0;

    for (long long machine : machineTime) {
        produced += time / machine;

        if (produced >= target) {
            return true;
        }
    }

    return false;
}

long long minimumProductionTime(
    const vector<long long>& machineTime,
    long long target
) {
    long long fastest =
        *min_element(machineTime.begin(), machineTime.end());

    long long low = 0;
    long long high = fastest * target;
    long long answer = high;

    while (low <= high) {
        long long mid = low + (high - low) / 2;

        if (canProduce(machineTime, target, mid)) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    return answer;
}
```

---

## Complexity

Checker:

```text
O(N)
```

Total:

```text
O(N log(answer range))
```

Extra space:

```text
O(1)
```

---

## Recognition Model

When you see:

```text
many independent machines/workers
+
all operate simultaneously
+
minimum time to hit a target
```

think:

```text
contribution of each item by T
+
sum contributions
+
BS first feasible T
```

---

# 4. Pattern 3 — 2D Counting

The lecture's third family is:

```text
2D Variations
```

Typical structure:

```text
many values generated by (i,j)
```

The conceptual matrix may be too large to construct.

So:

```text
guess X
→ count how many generated values <= X
→ check count >= K
```

---

# 4.1 CSES Multiplication Table

**Problem:** [CSES — Multiplication Table](https://cses.fi/problemset/task/2422)

---

## What It Asks

Consider an `N x N` multiplication table.

Entry:

```text
(i,j) = i*j
```

For odd `N`, find the middle element after all `N^2` values are sorted.

### Example

```text
N = 3
```

Table:

```text
1 2 3
2 4 6
3 6 9
```

Sorted:

```text
1,2,2,3,3,4,6,6,9
```

Middle:

```text
3
```

---

## Story Removed

Total values:

```text
N^2
```

Median rank:

```text
K = (N^2 + 1) / 2
```

Need:

```text
Kth smallest value
```

Do not build all `N^2` values.

---

## Search on Value

Guess:

```text
X
```

Ask:

```text
How many table entries are <= X?
```

If:

```text
count >= K
```

then:

```text
Kth smallest <= X
```

So:

```text
check(X) = count(<=X) >= K
```

Need:

```text
first True
```

---

## Derive Row Contribution

Row `i` contains:

```text
i, 2i, 3i, ..., Ni
```

Need:

```text
i*j <= X
```

Divide by `i`:

```text
j <= X/i
```

So number of valid columns is:

```text
floor(X/i)
```

but there are at most `N` columns.

Therefore row contribution:

```text
min(N, floor(X/i))
```

Total:

```text
count(X)
=
Σ min(N, floor(X/i))
```

This is a 2D problem solved by row-wise atomic counting.

---

## Compact Dry Run

```text
N = 3
K = 5
```

Try:

```text
X = 2
```

```text
row1: min(3,2/1)=2
row2: min(3,2/2)=1
row3: min(3,2/3)=0

count=3 < 5
→ false
```

Try:

```text
X = 3
```

```text
row1: min(3,3/1)=3
row2: min(3,3/2)=1
row3: min(3,3/3)=1

count=5 >= 5
→ true
```

Therefore:

```text
answer = 3
```

---

## ASCII Visualization

```text
            j
        1   2   3
      +---+---+---+
i=1   | 1 | 2 | 3 |
      +---+---+---+
i=2   | 2 | 4 | 6 |
      +---+---+---+
i=3   | 3 | 6 | 9 |
      +---+---+---+

For X=3:
values <=3 = 5
```

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

bool enoughValues(
    long long n,
    long long kth,
    long long x
) {
    long long count = 0;

    for (long long row = 1; row <= n; row++) {
        count += min(n, x / row);

        if (count >= kth) {
            return true;
        }
    }

    return false;
}

long long multiplicationTableMedian(long long n) {
    long long kth = (n * n + 1) / 2;

    long long low = 1;
    long long high = n * n;
    long long answer = high;

    while (low <= high) {
        long long mid = low + (high - low) / 2;

        if (enoughValues(n, kth, mid)) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    return answer;
}
```

---

## Complexity

Checker:

```text
O(N)
```

Search range:

```text
[1,N^2]
```

Total:

```text
O(N log(N^2))
=
O(N log N)
```

Extra space:

```text
O(1)
```

---

## Recognition Model

When a problem describes a conceptual matrix and asks:

```text
kth smallest
median
```

ask:

```text
For candidate X,
can I count matrix entries <= X
without constructing the matrix?
```

If yes:

```text
BS on value
```

---

# 4.2 AlgoZenith Kth Sum Value

The lecture screenshot shows **Kth Sum Value**:

```text
A of size N
B of size M
C contains all A[i] + B[j]
find Kth smallest element in C
```

The screenshot gives the problem statement. The checker below is the direct 2D-counting formulation of that pattern.

---

## What It Asks

Conceptually create:

```text
N*M
```

pair sums:

```text
A[i] + B[j]
```

Find:

```text
Kth smallest pair sum
```

---

## Why Brute Force Is Bad

Building all values requires:

```text
O(N*M)
```

memory/time before sorting.

Instead:

```text
Binary Search candidate sum X
```

and count pairs satisfying:

```text
A[i] + B[j] <= X
```

---

## Derive the Checker

For fixed `A[i]`:

```text
A[i] + B[j] <= X
```

Move `A[i]`:

```text
B[j] <= X - A[i]
```

So for each `A[i]`, count values in sorted `B` that are:

```text
<= X-A[i]
```

Using:

```cpp
upper_bound(...)
```

Total:

```text
countPairs(X)
=
Σ count(B[j] <= X-A[i])
```

Then:

```text
check(X)
=
countPairs(X) >= K
```

Need:

```text
first True
```

---

## Teaching Dry Run

```text
A = [1,4]
B = [2,5,7]
K = 4
```

Conceptual pair sums:

```text
1+2 = 3
1+5 = 6
1+7 = 8
4+2 = 6
4+5 = 9
4+7 = 11
```

Sorted only for verification:

```text
3,6,6,8,9,11
```

4th smallest:

```text
8
```

### Test X=7

For `A=1`:

```text
B <= 6
→ [2,5]
→ 2 values
```

For `A=4`:

```text
B <= 3
→ [2]
→ 1 value
```

Total:

```text
3 < K
→ false
```

### Test X=8

For `A=1`:

```text
B <= 7
→ 3 values
```

For `A=4`:

```text
B <= 4
→ 1 value
```

Total:

```text
4 >= K
→ true
```

Therefore:

```text
answer = 8
```

---

## 2D Visualization

```text
           B
          2   5   7
        +---+---+---+
A = 1   | 3 | 6 | 8 |
        +---+---+---+
A = 4   | 6 | 9 |11 |
        +---+---+---+
```

For:

```text
X = 8
```

there are:

```text
4 cells <= 8
```

So the 4th smallest value is at most `8`.

---

## Search Bounds

After sorting:

```text
low = A.front() + B.front()
high = A.back() + B.back()
```

These are the minimum and maximum possible pair sums.

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

bool enoughPairSums(
    const vector<long long>& a,
    const vector<long long>& b,
    long long k,
    long long x
) {
    long long count = 0;

    for (long long valueA : a) {
        long long maxB = x - valueA;

        count += upper_bound(
            b.begin(),
            b.end(),
            maxB
        ) - b.begin();

        if (count >= k) {
            return true;
        }
    }

    return false;
}

long long kthPairSum(
    vector<long long> a,
    vector<long long> b,
    long long k
) {
    sort(a.begin(), a.end());
    sort(b.begin(), b.end());

    long long low = a.front() + b.front();
    long long high = a.back() + b.back();
    long long answer = high;

    while (low <= high) {
        long long mid = low + (high - low) / 2;

        if (enoughPairSums(a, b, k, mid)) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    return answer;
}
```

---

## Complexity

Sort:

```text
O(N log N + M log M)
```

Checker:

```text
O(N log M)
```

Outer Binary Search:

```text
O(log(value range))
```

Overall checker-driven part:

```text
O(N log M * log(value range))
```

---

## Recognition Model

When you see:

```text
all pair values A[i] + B[j]
+
Kth smallest
```

think:

```text
Do NOT build N*M values.

BS candidate X
+
count pairs <= X
```

---

# 5. Pattern Comparison

| Problem | Pattern | Candidate `mid` | `check(mid)` |
|---|---|---|---|
| Painter Partition | Sweep / Coverage | max workload | painters used `<= K` |
| Array Division | Sweep / Coverage | max segment sum | segments used `<= K` |
| Factory Machines | Atomic Contribution | total time | `Σ floor(mid/machine[i]) >= target` |
| Multiplication Table | 2D Counting | candidate table value | `Σ min(N,mid/i) >= K` |
| Kth Sum Value | 2D Counting | candidate pair sum | count pairs `<= mid` is at least `K` |

The outer Binary Search stays almost the same.

Only this changes:

```text
check(mid)
```

---

# 6. Common Mistakes

## Mistake 1 — Searching the Array Instead of the Answer

Painter Partition does not search:

```text
board index
```

It searches:

```text
maximum allowed painter load
```

---

## Mistake 2 — Not Defining `check(mid)` First

Always write one sentence first:

```text
Can we finish within mid?
Can we partition with max sum mid?
Are at least K generated values <= mid?
```

Then code.

---

## Mistake 3 — Wrong Direction for Minimum Feasible

If:

```text
check(mid) = true
```

then:

```text
mid works
maybe smaller works
→ go LEFT
```

---

## Mistake 4 — Wrong Partition Lower Bound

For positive indivisible items:

```text
low = max(arr)
```

not `0` if you want a tighter natural bound.

---

## Mistake 5 — Breaking Contiguity

Painter / Array Division groups must be contiguous.

Valid:

```text
[1,3,8] [7,2] [5]
```

Invalid:

```text
Painter 1 gets indices 0,3,5
Painter 2 gets indices 1,4
```

---

## Mistake 6 — Using Exactly K Inside the Checker

Standard feasibility is:

```text
groupsUsed <= K
```

For positive arrays, if fewer groups work, groups can be split further without increasing the maximum load.

---

## Mistake 7 — Constructing the Entire 2D Space

Avoid:

```text
N*N table values
N*M pair sums
```

Count values:

```text
<= X
```

instead.

---

## Mistake 8 — Forgetting Integer Division

Factory Machines:

```text
T / machineTime[i]
```

counts only completed products.

---

## Mistake 9 — Overflow

Use `long long` for:

```text
sum
high
mid
counts
products
pair sums
```

and stop counting early once the target is reached.

---

## Mistake 10 — Assuming Every Kth Problem Has the Same Counter

The outer pattern may be the same:

```text
BS on X
```

but counting differs:

```text
Multiplication Table
→ arithmetic row count

Kth Sum
→ upper_bound / two-pointer count
```

The skill is deriving:

```text
count(values <= X)
```

---

# 7. Practice Order

Recommended order from the lecture forms:

```text
1. Painter Partition
   → minimum maximum
   → greedy sweep checker

2. CSES Array Division
   → same form, different story

3. CSES Factory Machines
   → atomic contribution

4. CSES Multiplication Table
   → implicit 2D counting

5. AlgoZenith Kth Sum Value
   → pairwise 2D counting
```

Before coding, you should be able to say:

```text
Painter:
"Can <=K painters finish with max load mid?"

Factory:
"Can machines make >=target products by time mid?"

Multiplication Table:
"Are >=K table cells <= mid?"

Kth Sum:
"Are >=K pair sums <= mid?"
```

---

# 8. Final Mental Model

## Pattern 1 — Sweep / Coverage

```text
candidate capacity = mid
        |
        v
scan items left → right
        |
        v
how many groups/resources needed?
        |
        v
needed <= K ?
```

Examples:

```text
Painter Partition
Array Division
```

---

## Pattern 2 — Atomic Item Contribution

```text
candidate time = mid
        |
        v
item 1 contributes f1(mid)
item 2 contributes f2(mid)
...
        |
        v
sum contributions
        |
        v
sum >= target ?
```

Factory Machines:

```text
fi(T) = floor(T / machineTime[i])
```

---

## Pattern 3 — 2D Counting

```text
candidate kth value = mid
        |
        v
count implicit values <= mid
        |
        v
count >= K ?
```

Multiplication Table:

```text
row i contribution
=
min(N, mid/i)
```

Kth Sum Value:

```text
for each A[i]:
count B[j] <= mid-A[i]
```

---

## One Master Template

```cpp
long long low = ...;
long long high = ...;
long long answer = high;

while (low <= high) {
    long long mid = low + (high - low) / 2;

    if (check(mid)) {
        answer = mid;
        high = mid - 1;
    } else {
        low = mid + 1;
    }
}
```

Do not memorize only the loop.

Memorize the modelling pipeline:

```text
OPTIMIZATION / KTH PROBLEM
        |
        v
GUESS ANSWER = mid
        |
        v
FORMULATE check(mid)
        |
        v
PROVE MONOTONICITY
        |
        v
IDENTIFY CHECKER FORM
   /        |        \
sweep   contribution   2D count
   \        |        /
        |
        v
BINARY SEARCH FIRST TRUE
```

---

## 60-Second Recognition

```text
"minimum possible maximum"
        ↓
Sweep / Partition
```

```text
"minimum time with many machines"
        ↓
Atomic Contribution
```

```text
"kth smallest among huge implicit values"
        ↓
Count <= X
        ↓
2D / Pair Counting
```

---

## Final Sentence

```text
Binary Search on Answer is not mainly about the loop.

The real problem is designing check(mid)
so that its result is monotonic.
```
