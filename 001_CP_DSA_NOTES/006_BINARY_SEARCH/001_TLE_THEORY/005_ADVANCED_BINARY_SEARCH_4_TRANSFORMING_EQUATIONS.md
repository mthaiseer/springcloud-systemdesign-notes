# TLE Eliminators — Advanced Binary Search 4
## Transforming Equations for Binary Search
### Maximum Average Segment + Pair Selection
### TLE-Style Self-Study Notes

> **Source:** supplied TLE Eliminators lecture **Advanced Binary Search 4**.
>
> The lecture develops one powerful idea:
>
> ```text
> difficult ratio / average condition
>        |
>        v
> guess candidate answer x
>        |
>        v
> algebraically transform the condition
>        |
>        v
> obtain an O(N) / O(N log N) checker
>        |
>        v
> prove checker(x) is monotonic
>        |
>        v
> binary search x
> ```
>
> **Problems covered by the lecture**
>
> ```text
> Problem 1 — Maximum Average Segment
> Problem 2 — Pair Selection
> Problem 3 [HW] — Average & Median
> ```
>
> **Teaching flow**
>
> ```text
> prerequisites
> → tiny dry run
> → what the problem asks
> → expected result
> → mathematical transformation
> → checker(x)
> → monotonicity
> → detailed dry run
> → binary search
> → compact C++17
> → recognition model
> ```
>
> **Note:** numerical examples marked **Teaching Dry Run** are illustrative examples added only to make the lecture equations easier to understand. The algorithmic ideas and transformations follow the supplied lecture.

---

# Clickable Table of Contents

- [0. Class Map](#0-class-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 Binary Search on Answer](#11-binary-search-on-answer)
  - [1.2 Last True Pattern](#12-last-true-pattern)
  - [1.3 Decimal Binary Search](#13-decimal-binary-search)
  - [1.4 Mean / Average Formula](#14-mean--average-formula)
  - [1.5 Transform `average >= x`](#15-transform-average--x)
  - [1.6 The Subtract-x Trick](#16-the-subtract-x-trick)
  - [1.7 Prefix Sum](#17-prefix-sum)
  - [1.8 Subarray Sum Using Prefix](#18-subarray-sum-using-prefix)
  - [1.9 Length-at-Least-d Subarray](#19-length-at-least-d-subarray)
  - [1.10 Maintain Minimum Prefix](#110-maintain-minimum-prefix)
  - [1.11 Transform a Ratio `>= x`](#111-transform-a-ratio--x)
  - [1.12 Why Choose the Top K Transformed Values](#112-why-choose-the-top-k-transformed-values)
  - [1.13 Recognition Checklist](#113-recognition-checklist)
- [2. Problem 1 — Maximum Average Segment](#2-problem-1--maximum-average-segment)
  - [2.1 What It Asks](#21-what-it-asks)
  - [2.2 What Result Is Expected](#22-what-result-is-expected)
  - [2.3 Why Brute Force Is Too Slow](#23-why-brute-force-is-too-slow)
  - [2.4 Choose Candidate Answer x](#24-choose-candidate-answer-x)
  - [2.5 Derive the Checker Equation](#25-derive-the-checker-equation)
  - [2.6 Transform the Array](#26-transform-the-array)
  - [2.7 Checker Becomes a Subarray-Sum Problem](#27-checker-becomes-a-subarray-sum-problem)
  - [2.8 Prefix-Sum Optimization](#28-prefix-sum-optimization)
  - [2.9 Why the Predicate Is Monotonic](#29-why-the-predicate-is-monotonic)
  - [2.10 Teaching Dry Run — x = 5.5](#210-teaching-dry-run--x--55)
  - [2.11 Teaching Dry Run — x = 5.6](#211-teaching-dry-run--x--56)
  - [2.12 Binary Search Dry Run](#212-binary-search-dry-run)
  - [2.13 Algorithm](#213-algorithm)
  - [2.14 C++17](#214-c17)
  - [2.15 Complexity](#215-complexity)
  - [2.16 Recognition Model](#216-recognition-model)
- [3. Problem 2 — Pair Selection](#3-problem-2--pair-selection)
  - [3.1 What It Asks](#31-what-it-asks)
  - [3.2 What Result Is Expected](#32-what-result-is-expected)
  - [3.3 Choose Candidate Ratio x](#33-choose-candidate-ratio-x)
  - [3.4 Derive the Ratio Transformation](#34-derive-the-ratio-transformation)
  - [3.5 Transform Every Pair](#35-transform-every-pair)
  - [3.6 Why We Take the K Largest Values](#36-why-we-take-the-k-largest-values)
  - [3.7 Build `canRatio(x)`](#37-build-canratiox)
  - [3.8 Why the Predicate Is Monotonic](#38-why-the-predicate-is-monotonic)
  - [3.9 Teaching Dry Run — x = 3.5](#39-teaching-dry-run--x--35)
  - [3.10 Teaching Dry Run — x = 4](#310-teaching-dry-run--x--4)
  - [3.11 Binary Search Dry Run](#311-binary-search-dry-run)
  - [3.12 Algorithm](#312-algorithm)
  - [3.13 C++17](#313-c17)
  - [3.14 Complexity](#314-complexity)
  - [3.15 Recognition Model](#315-recognition-model)
- [4. Maximum Average Segment vs Pair Selection](#4-maximum-average-segment-vs-pair-selection)
- [5. Common Mistakes](#5-common-mistakes)
- [6. Problem 3 [HW] — Average & Median](#6-problem-3-hw--average--median)
- [7. Final Mental Model](#7-final-mental-model)

---

# 0. Class Map

```text
ADVANCED BINARY SEARCH 4
│
├── Problem 1: Maximum Average Segment
│   │
│   ├── answer = maximum average x
│   ├── average >= x
│   ├── transform a[i] → a[i] - x
│   ├── need subarray length >= d
│   │   with transformed sum >= 0
│   ├── prefix sum + minimum previous prefix
│   └── T T T T F F F
│               ^
│               |
│           last True
│
└── Problem 2: Pair Selection
    │
    ├── answer = maximum ratio x
    ├── ΣA / ΣB >= x
    ├── transform pair i:
    │   c[i] = a[i] - x*b[i]
    ├── choose K indices
    ├── take K largest c[i]
    ├── sum(top K) >= 0 ?
    └── T T T T F F F
                ^
                |
            last True
```

Both problems use the same deeper pattern:

```text
ratio / average
     |
     v
compare with candidate x
     |
     v
remove division
     |
     v
move x inside every term
     |
     v
linear transformed values
     |
     v
easy checker
```

---

# 1. Prerequisites

## 1.1 Binary Search on Answer

Binary search can search a numeric **answer space**, not only a sorted array.

Suppose we are maximizing a value:

```text
x
```

Instead of directly finding the optimum, ask:

```text
"If the answer were at least x,
is there a valid choice?"
```

This creates:

```text
can(x)
```

### Tiny Dry Run

Suppose:

```text
x:      1 2 3 4 5 6 7
can(x): T T T T F F F
```

Then:

```text
maximum feasible x = 4
```

The difficult part is usually not the binary-search loop.

It is:

```text
inventing can(x)
```

---

## 1.2 Last True Pattern

Both main lecture problems maximize a value.

Their predicates have the form:

```text
T T T T T F F F
        ^
        |
     last True
```

Why?

For a maximum-average problem:

```text
if average >= 5.5 exists
```

then certainly:

```text
average >= 5.0 exists
```

For a maximum-ratio problem:

```text
if ratio >= 4 exists
```

then certainly:

```text
ratio >= 3 exists
```

### Mental Rule

```text
maximize threshold x
+
smaller x is easier
=
LAST TRUE
```

---

## 1.3 Decimal Binary Search

Average and ratio answers may be fractional:

```text
5.5
3.75
2.428571...
```

So the search space is real-valued.

Typical pattern:

```cpp
for (int iteration = 0; iteration < 100; ++iteration) {
    long double mid = (low + high) / 2.0L;

    if (can(mid)) {
        low = mid;
    } else {
        high = mid;
    }
}
```

### Tiny Dry Run

Suppose:

```text
low = 4
high = 6
```

Mid:

```text
mid = 5
```

If:

```text
can(5) = True
```

and we are maximizing:

```text
low = 5
```

New interval:

```text
[5,6]
```

If next:

```text
mid = 5.5
```

and:

```text
can(5.5) = False
```

then:

```text
high = 5.5
```

New interval:

```text
[5,5.5]
```

Continue narrowing.

---

## 1.4 Mean / Average Formula

For a subarray containing:

```text
k
```

elements:

```text
a[i], a[i+1], ..., a[i+k-1]
```

its average is:

```text
average = (a[i] + a[i+1] + ... + a[i+k-1]) / k
```

### Tiny Dry Run

Subarray:

```text
[5,6,5,6]
```

Length:

```text
k = 4
```

Sum:

```text
5 + 6 + 5 + 6 = 22
```

Average:

```text
22 / 4 = 5.5
```

So:

```text
mean([5,6,5,6]) = 5.5
```

---

## 1.5 Transform `average >= x`

This is the first important algebraic transformation.

Suppose:

```text
(a1 + a2 + ... + ak) / k >= x
```

Since:

```text
k > 0
```

multiply both sides by `k`:

```text
a1 + a2 + ... + ak >= k*x
```

Now move `k*x` to the left.

Remember:

```text
k*x = x + x + ... + x
      k times
```

So:

```text
(a1-x) + (a2-x) + ... + (ak-x) >= 0
```

### Tiny Dry Run

Subarray:

```text
[5,6,5,6]
```

Candidate:

```text
x = 5.5
```

Original check:

```text
(5+6+5+6)/4 >= 5.5
22/4 >= 5.5
5.5 >= 5.5
True
```

Transformed check:

```text
(5-5.5) + (6-5.5) + (5-5.5) + (6-5.5)
=
-0.5 + 0.5 - 0.5 + 0.5
=
0
```

So:

```text
sum transformed >= 0
```

is exactly equivalent.

---

## 1.6 The Subtract-x Trick

For a candidate average:

```text
x
```

transform every array value:

```text
b[i] = a[i] - x
```

Then:

```text
original:
average of a subarray >= x
```

becomes:

```text
transformed:
sum of corresponding b-subarray >= 0
```

### Tiny Dry Run

Original:

```text
A = [4,7,6]
x = 5
```

Transform:

```text
B = [4-5, 7-5, 6-5]
  = [-1, 2, 1]
```

Whole-array sum:

```text
-1 + 2 + 1 = 2 >= 0
```

Therefore:

```text
average(A) >= 5
```

Check original:

```text
(4+7+6)/3 = 17/3 ≈ 5.67
```

Correct.

### Recognition

Whenever you see:

```text
average >= x
```

try:

```text
value -= x
```

and turn it into:

```text
sum >= 0
```

---

## 1.7 Prefix Sum

Prefix sums let us get any subarray sum in `O(1)`.

Use:

```text
prefix[0] = 0
prefix[i] = a[0] + a[1] + ... + a[i-1]
```

### Tiny Dry Run

Array:

```text
A = [2,5,3,4]
```

Prefix:

```text
prefix[0] = 0
prefix[1] = 2
prefix[2] = 7
prefix[3] = 10
prefix[4] = 14
```

Table:

```text
i:         0  1  2   3   4
prefix:    0  2  7  10  14
```

---

## 1.8 Subarray Sum Using Prefix

For 0-based subarray:

```text
[L ... R]
```

sum is:

```text
prefix[R+1] - prefix[L]
```

Equivalent 1-based notation from the lecture idea:

```text
P[R] - P[L-1]
```

### Tiny Dry Run

Array:

```text
[2,5,3,4]
```

Find sum:

```text
[5,3]
```

This is indices:

```text
L = 1
R = 2
```

Using prefix:

```text
prefix[3] - prefix[1]
=
10 - 2
=
8
```

Correct:

```text
5 + 3 = 8
```

---

## 1.9 Length-at-Least-d Subarray

The Maximum Average Segment problem does **not** require exactly length `d`.

It requires:

```text
length >= d
```

For a fixed right endpoint `R`, valid left endpoints must satisfy:

```text
subarray length >= d
```

Using prefix indices, if the subarray ends at prefix position `r`, we may subtract any earlier prefix:

```text
prefix[j]
```

with:

```text
j <= r-d
```

So transformed subarray sum is:

```text
prefix[r] - prefix[j]
```

and we want to know whether this can be:

```text
>= 0
```

### Tiny Dry Run

Let:

```text
d = 3
r = 5
```

Then valid starting-prefix positions are:

```text
j <= 5-3
j <= 2
```

So allowed `j`:

```text
0,1,2
```

We need the best one.

---

## 1.10 Maintain Minimum Prefix

For fixed:

```text
prefix[r]
```

we want to maximize:

```text
prefix[r] - prefix[j]
```

where:

```text
j <= r-d
```

`prefix[r]` is fixed.

Therefore maximize the difference by minimizing:

```text
prefix[j]
```

So maintain:

```text
minPrefix = minimum prefix value seen among valid j
```

Then:

```text
best transformed sum ending at r = prefix[r] - minPrefix
```

If:

```text
prefix[r] - minPrefix >= 0
```

then some valid subarray of length at least `d` has transformed sum at least zero.

### Tiny Dry Run

Suppose eligible prefixes are:

```text
3, -2, 4
```

and current:

```text
prefix[r] = 1
```

Minimum eligible prefix:

```text
minPrefix = -2
```

Best possible difference:

```text
1 - (-2) = 3
```

Positive.

So a valid subarray exists.

### Why Not Use the Largest Prefix?

Because:

```text
prefix[r] - prefix[j]
```

gets larger when:

```text
prefix[j]
```

gets smaller.

---

## 1.11 Transform a Ratio `>= x`

Now the second major equation.

Suppose selected indices are:

```text
i1, i2, ..., ik
```

and ratio is:

```text
(a[i1] + a[i2] + ... + a[ik])
--------------------------------
(b[i1] + b[i2] + ... + b[ik])
```

We want to know whether this ratio is at least:

```text
x
```

Assuming the selected denominator sum is positive, as in the lecture problem:

```text
Σa / Σb >= x
```

Multiply by:

```text
Σb
```

```text
Σa >= x * Σb
```

Expand:

```text
a[i1] + a[i2] + ... + a[ik]
>=
x*b[i1] + x*b[i2] + ... + x*b[ik]
```

Move everything left:

```text
(a[i1] - x*b[i1])
+
(a[i2] - x*b[i2])
+
...
+
(a[ik] - x*b[ik])
>= 0
```

### Tiny Dry Run

Choose two pairs:

```text
A = [10,9]
B = [2,3]
```

Candidate:

```text
x = 3.5
```

Original ratio:

```text
(10+9)/(2+3)
=
19/5
=
3.8
```

Check:

```text
3.8 >= 3.5
True
```

Transformed:

```text
(10 - 3.5*2) + (9 - 3.5*3)
=
3 + (-1.5)
=
1.5
```

```text
1.5 >= 0
```

Same answer.

---

## 1.12 Why Choose the Top K Transformed Values

After fixing candidate ratio:

```text
x
```

define:

```text
c[i] = a[i] - x*b[i]
```

Now the checker asks:

```text
Can we choose exactly K indices
whose transformed sum is >= 0?
```

To maximize the sum of exactly `K` chosen values, choose the:

```text
K largest c[i]
```

### Tiny Dry Run

Transformed values:

```text
c = [3, -1.5, -6, 0.5]
K = 2
```

Sorted descending:

```text
3, 0.5, -1.5, -6
```

Best two:

```text
3 + 0.5 = 3.5
```

Any other pair is no better.

So:

```text
if sum of top K >= 0
→ some selection works

if sum of top K < 0
→ no selection of K can work
```

This turns the ratio-selection problem into:

```text
transform
→ sort
→ sum top K
```

---

## 1.13 Recognition Checklist

When a problem contains:

```text
maximum average
maximum ratio
minimum average
minimum ratio
```

ask:

```text
1. Can I binary-search candidate x?

2. Can I compare:
   average >= x
   or
   ratio >= x?

3. Can I remove the division?

4. Does that produce:
   a[i] - x
   or
   a[i] - x*b[i]?

5. After transformation, what is the checker?

6. Is the checker monotonic?

7. Is the answer decimal?

8. Do I need:
   prefix minimum?
   top K values?
```

---

# 2. Problem 1 — Maximum Average Segment

## 2.1 What It Asks

The lecture gives:

```text
N
d
array A
```

We need a contiguous subarray whose:

```text
length >= d
```

and whose:

```text
average is maximum
```

The lecture first observes that for **exactly** length `d`, there are:

```text
n - d + 1
```

candidate subarrays.

But the actual optimization considers:

```text
all lengths >= d
```

which is why a direct enumeration of every length becomes expensive.

---

## 2.2 What Result Is Expected

We need:

```text
maximum achievable average
```

over every contiguous subarray with:

```text
length >= d
```

### Teaching Dry Run

Use:

```text
A = [1,1,5,6,5,6,1,1]
d = 4
```

Consider:

```text
[5,6,5,6]
```

Length:

```text
4
```

Average:

```text
(5+6+5+6)/4 = 22/4 = 5.5
```

For this teaching example, the important boundary is:

```text
5.5
```

because this central segment achieves that average.

---

## 2.3 Why Brute Force Is Too Slow

### Fixed length d

There are:

```text
n-d+1
```

subarrays of exact length `d`.

With prefix sum, each sum is `O(1)`.

So fixed length:

```text
O(n)
```

### But length can be:

```text
d
d+1
d+2
...
n
```

If we separately inspect all starts for all lengths, total work becomes quadratic in the worst case:

```text
O(n^2)
```

This motivates a different question:

```text
Instead of directly finding the best average,
can we check whether average >= x is possible?
```

That leads to binary search.

---

## 2.4 Choose Candidate Answer x

Let:

```text
x = candidate average
```

Define:

```text
canAverage(x)
=
does there exist a contiguous subarray
of length at least d
whose average >= x?
```

We want:

```text
maximum x
such that canAverage(x) = True
```

So this is:

```text
LAST TRUE
```

---

## 2.5 Derive the Checker Equation

Suppose a candidate subarray is:

```text
a[i], a[i+1], ..., a[i+k-1]
```

where:

```text
k >= d
```

We need:

```text
(a[i] + ... + a[i+k-1]) / k >= x
```

Multiply by positive `k`:

```text
a[i] + ... + a[i+k-1] >= k*x
```

Move `x` into every term:

```text
(a[i]-x)
+
(a[i+1]-x)
+
...
+
(a[i+k-1]-x)
>= 0
```

Therefore:

```text
average >= x
```

is equivalent to:

```text
transformed subarray sum >= 0
```

---

## 2.6 Transform the Array

For fixed candidate:

```text
x
```

construct conceptually:

```text
b[i] = a[i] - x
```

We do not need to permanently modify `A`; the checker can build transformed prefix sums directly.

### Teaching Dry Run

```text
A = [5,6,5,6]
x = 5.5
```

Transform:

```text
B = [-0.5, 0.5, -0.5, 0.5]
```

Sum:

```text
0
```

So:

```text
average(A) >= 5.5
```

is True.

If:

```text
x = 5.6
```

then:

```text
B = [-0.6, 0.4, -0.6, 0.4]
```

Sum:

```text
-0.4
```

So that same segment does not reach average `5.6`.

---

## 2.7 Checker Becomes a Subarray-Sum Problem

After transformation, `canAverage(x)` becomes:

```text
Does B contain a subarray
with length >= d
and sum >= 0?
```

That is no longer an average problem.

It is now a:

```text
prefix-sum problem
```

This is the key transformation in the lecture.

---

## 2.8 Prefix-Sum Optimization

Define transformed prefix:

```text
prefix[0] = 0
prefix[i] = Σ(a[j] - x) for j = 0 ... i-1
```

For a subarray corresponding to prefix endpoints:

```text
j ... r-1
```

transformed sum is:

```text
prefix[r] - prefix[j]
```

Length condition:

```text
r - j >= d
```

therefore:

```text
j <= r-d
```

For each `r`, choose the smallest prefix among:

```text
prefix[0 ... r-d]
```

because that maximizes:

```text
prefix[r] - prefix[j]
```

Maintain:

```text
minPrefix = min(minPrefix, prefix[r-d])
```

Then test:

```text
prefix[r] - minPrefix >= 0
```

If yes:

```text
canAverage(x) = True
```

### ASCII

```text
prefix positions:

0   1   2   3   4   5   6   7
|---|---|---|---|---|---|---|

                    r
                    |
                    v

eligible j values:
0 ... r-d

choose:
minimum prefix[j]

then:
prefix[r] - minPrefix
```

---

## 2.9 Why the Predicate Is Monotonic

Suppose:

```text
canAverage(x) = True
```

So some subarray has:

```text
average >= x
```

Take any:

```text
y < x
```

The same subarray also satisfies:

```text
average >= y
```

Therefore:

```text
canAverage(x) = True
⇒
canAverage(y) = True for y <= x
```

Predicate:

```text
T T T T T T F F F
          ^
          |
      last True
```

So decimal binary search is valid.

---

## 2.10 Teaching Dry Run — x = 5.5

Use:

```text
A = [1,1,5,6,5,6,1,1]
d = 4
x = 5.5
```

Transform:

```text
B = [-4.5,-4.5,-0.5,0.5,-0.5,0.5,-4.5,-4.5]
```

Build prefix:

```text
prefix[0] = 0
prefix[1] = -4.5
prefix[2] = -9.0
prefix[3] = -9.5
prefix[4] = -9.0
prefix[5] = -9.5
prefix[6] = -9.0
prefix[7] = -13.5
prefix[8] = -18.0
```

Now process:

```text
r >= d
```

At:

```text
r = 6
```

eligible starting-prefix positions are:

```text
j <= 6-4 = 2
```

Eligible prefix values:

```text
prefix[0] = 0
prefix[1] = -4.5
prefix[2] = -9.0
```

Minimum:

```text
minPrefix = -9.0
```

Check:

```text
prefix[6] - minPrefix = -9.0 - (-9.0) = 0
```

So:

```text
0 >= 0
```

True.

This corresponds exactly to subarray:

```text
A[2 ... 5] = [5,6,5,6]
```

Therefore:

```text
canAverage(5.5) = True
```

---

## 2.11 Teaching Dry Run — x = 5.6

Same:

```text
A = [1,1,5,6,5,6,1,1]
d = 4
x = 5.6
```

For the strongest central segment:

```text
[5,6,5,6]
```

transformed:

```text
[-0.6,0.4,-0.6,0.4]
```

Sum:

```text
-0.4
```

So its average is below `5.6`.

For this teaching array, no longer segment improves the average beyond the central block.

Therefore:

```text
canAverage(5.6) = False
```

So the boundary lies between:

```text
5.5 and 5.6
```

and in this example is:

```text
5.5
```

---

## 2.12 Binary Search Dry Run

Suppose initial answer range is:

```text
low = min(A) = 1
high = max(A) = 6
```

Why is this safe?

Any average of array elements lies between:

```text
minimum element
and
maximum element
```

### Iteration 1

```text
mid = (1+6)/2 = 3.5
```

A segment with average at least `3.5` exists.

So:

```text
canAverage(3.5) = True
```

Maximizing:

```text
low = 3.5
```

---

### Iteration 2

```text
mid = (3.5+6)/2 = 4.75
```

Still feasible:

```text
low = 4.75
```

---

### Later

The interval narrows around:

```text
5.5
```

Conceptually:

```text
[1,6]
→ [3.5,6]
→ [4.75,6]
→ ...
→ [5.49...,5.50...]
```

After enough iterations:

```text
low ≈ maximum average
```

---

## 2.13 Algorithm

```text
canAverage(x):

    prefix[0] = 0

    for i = 1..n:
        prefix[i] = prefix[i-1] + (a[i-1] - x)

    minPrefix = 0

    for r = d..n:

        minPrefix =
            min(minPrefix, prefix[r-d])

        if prefix[r] - minPrefix >= 0:
            return true

    return false
```

Binary search:

```text
low = minimum array value
high = maximum array value

repeat 100 times:

    mid = (low + high) / 2

    if canAverage(mid):
        low = mid
    else:
        high = mid

answer ≈ low
```

---

## 2.14 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canAverage(long double x, const vector<long double>& a, int d) {
    int n = (int)a.size();
    vector<long double> prefix(n + 1, 0.0L);

    for (int i = 1; i <= n; ++i) {
        prefix[i] = prefix[i - 1] + (a[i - 1] - x);
    }

    long double minPrefix = 0.0L;

    for (int r = d; r <= n; ++r) {
        minPrefix = min(minPrefix, prefix[r - d]);

        if (prefix[r] - minPrefix >= 0.0L) {
            return true;
        }
    }

    return false;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, d;
    cin >> n >> d;

    vector<long double> a(n);

    for (long double& value : a) {
        cin >> value;
    }

    long double low = *min_element(a.begin(), a.end());
    long double high = *max_element(a.begin(), a.end());

    for (int iteration = 0; iteration < 100; ++iteration) {
        long double mid = (low + high) / 2.0L;

        if (canAverage(mid, a, d)) {
            low = mid;
        } else {
            high = mid;
        }
    }

    cout << fixed << setprecision(10) << low << '\n';
    return 0;
}
```

### Code Mapping

```text
a[i] - x
→ transformed value

prefix[r]
→ transformed prefix sum

prefix[r-d]
→ newest prefix allowed as a start

minPrefix
→ minimum eligible starting prefix

prefix[r] - minPrefix >= 0
→ some length >= d subarray has average >= x
```

---

## 2.15 Complexity

Let:

```text
N = array size
I = decimal binary-search iterations
```

One checker:

```text
O(N)
```

With:

```text
I = 100
```

total:

```text
O(100N)
```

Practically:

```text
O(N)
```

with a fixed precision loop.

General form:

```text
O(N log(answerRange / precision))
```

Extra space in this implementation:

```text
O(N)
```

for transformed prefix sums.

---

## 2.16 Recognition Model

When you see:

```text
maximum average subarray
+
length >= d
```

think:

```text
candidate average x
```

Then:

```text
average >= x
→ Σ(a[i]-x) >= 0
```

Now the problem is:

```text
Does transformed array contain
a subarray of length >= d
with sum >= 0?
```

Solve using:

```text
prefix sum
+
minimum eligible prefix
```

Predicate:

```text
T T T T T F F F
        ^
        |
     last True
```

---

# 3. Problem 2 — Pair Selection

## 3.1 What It Asks

The lecture shows two arrays:

```text
A
B
```

and selects the same set of indices from both.

For chosen indices:

```text
i1, i2, ..., iK
```

the objective is the ratio:

```text
a[i1] + a[i2] + ... + a[iK]
--------------------------------
b[i1] + b[i2] + ... + b[iK]
```

We want the:

```text
maximum achievable ratio
```

over a choice of exactly `K` indices/pairs.

The lecture illustrates selecting positions such as:

```text
1, 5, 9
```

from both arrays.

---

## 3.2 What Result Is Expected

We need:

```text
maximum possible ratio
```

not the individual maximum:

```text
a[i] / b[i]
```

because exactly `K` pairs are selected and their sums are combined.

### Teaching Dry Run

Use:

```text
A = [10,9,8]
B = [ 2,3,4]
K = 2
```

Possible choices:

```text
indices 1,2:
(10+9)/(2+3) = 19/5 = 3.8

indices 1,3:
(10+8)/(2+4) = 18/6 = 3

indices 2,3:
(9+8)/(3+4) = 17/7 ≈ 2.43
```

Expected optimum:

```text
3.8
```

---

## 3.3 Choose Candidate Ratio x

Let:

```text
x = candidate ratio
```

Define:

```text
canRatio(x)
=
is there a choice of exactly K indices
whose ratio is >= x?
```

We want:

```text
maximum x
such that canRatio(x) = True
```

So again:

```text
LAST TRUE
```

---

## 3.4 Derive the Ratio Transformation

For selected indices:

```text
S = {i1, i2, ..., iK}
```

Need:

```text
Σ a[i] / Σ b[i] >= x
```

For the positive denominator used by this problem:

```text
Σ a[i] >= x * Σ b[i]
```

Expand:

```text
a[i1] + a[i2] + ... + a[iK]
>=
x*b[i1] + x*b[i2] + ... + x*b[iK]
```

Move right side left:

```text
(a[i1] - x*b[i1])
+
(a[i2] - x*b[i2])
+
...
+
(a[iK] - x*b[iK])
>= 0
```

So define:

```text
c[i] = a[i] - x*b[i]
```

The ratio problem becomes:

```text
Can we choose K c-values
whose sum is >= 0?
```

---

## 3.5 Transform Every Pair

For every pair:

```text
(a[i], b[i])
```

compute:

```text
c[i] = a[i] - x*b[i]
```

### Teaching Dry Run

```text
A = [10,9,8]
B = [ 2,3,4]
x = 3.5
```

Then:

```text
c[0] = 10 - 3.5*2 = 3
c[1] =  9 - 3.5*3 = -1.5
c[2] =  8 - 3.5*4 = -6
```

So:

```text
C = [3, -1.5, -6]
```

---

## 3.6 Why We Take the K Largest Values

We must choose exactly:

```text
K
```

transformed values.

To maximize their sum, take the:

```text
K largest values
```

If even the maximum possible K-value sum is negative:

```text
no selection can work
```

If the top-K sum is non-negative:

```text
that top-K selection proves feasibility
```

### Tiny Proof

Suppose sorted:

```text
c1 >= c2 >= ... >= cn
```

For any set of `K` indices:

```text
selected sum <= c1 + c2 + ... + cK
```

Therefore:

```text
maximum K-element sum = sum of K largest c-values
```

So checker only needs the top `K`.

---

## 3.7 Build `canRatio(x)`

For fixed:

```text
x
```

Step 1:

```text
c[i] = a[i] - x*b[i]
```

Step 2:

```text
sort c descending
```

Step 3:

```text
best = sum of first K values
```

Step 4:

```text
best >= 0
→ True

best < 0
→ False
```

Compactly:

```text
canRatio(x) = [sum(top K of a[i]-x*b[i]) >= 0]
```

---

## 3.8 Why the Predicate Is Monotonic

Suppose:

```text
canRatio(x) = True
```

Then there exists a selection whose ratio is at least `x`.

For any:

```text
y < x
```

the same selection also has:

```text
ratio >= y
```

So:

```text
canRatio(x) = True
⇒
canRatio(y) = True for y <= x
```

Another transformed view:

```text
c[i] = a[i] - x*b[i]
```

As `x` increases and `b[i] > 0`:

```text
c[i] decreases
```

so satisfying:

```text
sum(top K) >= 0
```

becomes harder.

Predicate:

```text
T T T T T F F F
        ^
        |
     last True
```

---

## 3.9 Teaching Dry Run — x = 3.5

Use:

```text
A = [10,9,8]
B = [ 2,3,4]
K = 2
x = 3.5
```

Transform:

```text
C = [3, -1.5, -6]
```

Sort descending:

```text
3, -1.5, -6
```

Top 2:

```text
3 + (-1.5) = 1.5
```

Check:

```text
1.5 >= 0
```

True.

Therefore:

```text
canRatio(3.5) = True
```

The corresponding selected pairs are:

```text
(10,2)
(9,3)
```

Actual ratio:

```text
19/5 = 3.8
```

which is indeed at least `3.5`.

---

## 3.10 Teaching Dry Run — x = 4

Same:

```text
A = [10,9,8]
B = [ 2,3,4]
K = 2
x = 4
```

Transform:

```text
c[0] = 10 - 4*2 = 2
c[1] =  9 - 4*3 = -3
c[2] =  8 - 4*4 = -8
```

Sorted:

```text
2, -3, -8
```

Top 2 sum:

```text
2 + (-3) = -1
```

Check:

```text
-1 >= 0
```

False.

So:

```text
canRatio(4) = False
```

Hence the optimum lies between:

```text
3.5 and 4
```

For this teaching example:

```text
answer = 3.8
```

---

## 3.11 Binary Search Dry Run

For positive `B`, a safe derived bound is that the selected ratio lies between the minimum and maximum individual ratios:

```text
a[i] / b[i]
```

For teaching example:

```text
10/2 = 5
9/3  = 3
8/4  = 2
```

So search:

```text
[2,5]
```

### Iteration 1

```text
mid = 3.5
```

We know:

```text
canRatio(3.5) = True
```

Maximizing:

```text
low = 3.5
```

---

### Iteration 2

```text
mid = (3.5+5)/2 = 4.25
```

This is too large.

So:

```text
high = 4.25
```

---

### Later

The interval narrows around:

```text
3.8
```

Conceptually:

```text
[2,5]
→ [3.5,5]
→ [3.5,4.25]
→ ...
→ [3.799...,3.800...]
```

---

## 3.12 Algorithm

```text
canRatio(x):

    transformed = empty

    for i = 0..n-1:
        transformed.push(a[i] - x*b[i])

    sort descending

    sum = first K values

    return sum >= 0
```

Binary search:

```text
low = safe lower ratio bound
high = safe upper ratio bound

repeat 100 times:

    mid = (low + high) / 2

    if canRatio(mid):
        low = mid
    else:
        high = mid

answer ≈ low
```

---

## 3.13 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canRatio(long double x, const vector<long double>& a, const vector<long double>& b, int k) {
    int n = (int)a.size();
    vector<long double> transformed(n);

    for (int i = 0; i < n; ++i) {
        transformed[i] = a[i] - x * b[i];
    }

    sort(transformed.begin(), transformed.end(), greater<long double>());

    long double bestSum = 0.0L;

    for (int i = 0; i < k; ++i) {
        bestSum += transformed[i];
    }

    return bestSum >= 0.0L;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k;
    cin >> n >> k;

    vector<long double> a(n), b(n);

    for (long double& value : a) {
        cin >> value;
    }

    for (long double& value : b) {
        cin >> value;
    }

    long double low = a[0] / b[0];
    long double high = a[0] / b[0];

    for (int i = 1; i < n; ++i) {
        long double ratio = a[i] / b[i];
        low = min(low, ratio);
        high = max(high, ratio);
    }

    for (int iteration = 0; iteration < 100; ++iteration) {
        long double mid = (low + high) / 2.0L;

        if (canRatio(mid, a, b, k)) {
            low = mid;
        } else {
            high = mid;
        }
    }

    cout << fixed << setprecision(10) << low << '\n';
    return 0;
}
```

### Code Mapping

```text
a[i] - x*b[i]
→ transformed contribution of selecting pair i

sort descending
→ arrange best contributions first

sum first K
→ maximum possible transformed sum for exactly K choices

bestSum >= 0
→ some K-pair ratio is at least x
```

---

## 3.14 Complexity

Let:

```text
N = number of pairs
K = number selected
I = binary-search iterations
```

Transform:

```text
O(N)
```

Sort:

```text
O(N log N)
```

Top-K sum:

```text
O(K)
```

So one checker:

```text
O(N log N)
```

With fixed `100` iterations:

```text
O(100 * N log N)
```

Usually written:

```text
O(N log N)
```

with the decimal-iteration constant understood.

Extra space:

```text
O(N)
```

for transformed values.

---

## 3.15 Recognition Model

When you see:

```text
choose exactly K items
+
maximize
(sum of A) / (sum of B)
```

think:

```text
binary search candidate ratio x
```

Transform:

```text
ΣA / ΣB >= x
→ Σ(A - xB) >= 0
```

For each item:

```text
c[i] = a[i] - x*b[i]
```

Need best `K`:

```text
take K largest c[i]
```

Checker:

```text
sum(top K) >= 0
```

Predicate:

```text
T T T T T F F F
        ^
        |
     last True
```

---

# 4. Maximum Average Segment vs Pair Selection

| Idea | Maximum Average Segment | Pair Selection |
|---|---|---|
| Optimize | maximum average | maximum ratio |
| Candidate | average `x` | ratio `x` |
| Original form | `Σa / length` | `ΣA / ΣB` |
| Transform | `a[i]-x` | `a[i]-x*b[i]` |
| Need after transform | subarray sum `>= 0` | selected sum `>= 0` |
| Structure | contiguous subarray | choose exactly `K` indices |
| Checker trick | prefix + min prefix | sort + top `K` |
| Predicate | `TTTTFFFF` | `TTTTFFFF` |
| Boundary | last True | last True |
| Answer type | decimal | decimal |

### One-Line Difference

```text
Maximum Average Segment:
find a GOOD CONTIGUOUS GROUP after transformation.

Pair Selection:
find the BEST K ITEMS after transformation.
```

---

# 5. Common Mistakes

## Mistake 1 — Binary Searching Without Transforming the Equation

For Maximum Average Segment, checking candidate `x` directly by recalculating every average is too slow.

Transform:

```text
average >= x
→ sum(a[i]-x) >= 0
```

---

## Mistake 2 — Checking Only Length Exactly d

The lecture problem needs:

```text
length >= d
```

not:

```text
length = d
```

The checker must allow every earlier prefix satisfying:

```text
j <= r-d
```

not only:

```text
j = r-d
```

That is why we keep:

```text
minimum prefix among all eligible j
```

---

## Mistake 3 — Using Maximum Prefix Instead of Minimum Prefix

We want to maximize:

```text
prefix[r] - prefix[j]
```

For fixed `prefix[r]`, choose:

```text
minimum prefix[j]
```

not maximum.

---

## Mistake 4 — Updating minPrefix With an Invalid Future Prefix

At endpoint `r`, only prefixes satisfying:

```text
j <= r-d
```

are legal.

Update with:

```text
prefix[r-d]
```

as `r` advances.

Do not include a prefix that would create a subarray shorter than `d`.

---

## Mistake 5 — Forgetting Why Division Can Be Removed

For average:

```text
length k > 0
```

For pair ratio, the lecture setting uses a positive denominator sum.

That positivity is what allows multiplying an inequality without reversing its sign.

---

## Mistake 6 — Pair Selection: Choosing Top K by Original A

Wrong:

```text
pick largest a[i]
```

The candidate ratio changes the contribution.

Correct transformed score:

```text
c[i] = a[i] - x*b[i]
```

Choose top `K` by:

```text
c[i]
```

---

## Mistake 7 — Pair Selection: Checking Individual Ratios Only

We optimize:

```text
ΣA / ΣB
```

for a group.

That is not generally the same as simply selecting the `K` largest:

```text
a[i] / b[i]
```

The checker must use:

```text
a[i] - x*b[i]
```

for the current candidate `x`.

---

## Mistake 8 — Wrong Binary Search Direction

Both problems maximize.

Predicate:

```text
T T T T F F F
```

Therefore:

```text
True  → try larger  → low = mid
False → try smaller → high = mid
```

---

## Mistake 9 — Too Few Decimal Iterations

Use a fixed sufficiently large iteration count such as:

```text
100
```

for comfortable precision with `long double`.

---

# 6. Problem 3 [HW] — Average & Median

The supplied lecture ends with:

```text
Problem 3 [HW]: Average & Median
```

The source only provides the homework title slide and does not include its derivation or solution.

Therefore this note deliberately does **not** invent a solution for that homework problem.

When the corresponding lecture/problem explanation is supplied, it should be written as a separate detailed section using the same structure:

```text
What It Asks
→ Expected Result
→ Equation / Predicate
→ Transformation
→ Monotonicity
→ Dry Run
→ C++17
```

---

# 7. Final Mental Model

## Pattern A — Maximum Average Segment

```text
MAXIMUM AVERAGE x
        |
        v
Does a subarray length >= d
have average >= x?
        |
        v
Σa / k >= x
        |
        v
Σ(a-x) >= 0
        |
        v
transformed array
        |
        v
need subarray length >= d
with sum >= 0
        |
        v
prefix[r] - minEligiblePrefix >= 0
        |
        v
T T T T T F F F
        ^
        |
     last True
```

Core checker formula:

```text
b[i] = a[i] - x
```

and:

```text
prefix[r] - min(prefix[0 ... r-d]) >= 0
```

---

## Pattern B — Pair Selection

```text
MAXIMUM RATIO x
        |
        v
Can some K selected pairs
have ratio >= x?
        |
        v
ΣA / ΣB >= x
        |
        v
Σ(A-xB) >= 0
        |
        v
c[i] = a[i] - x*b[i]
        |
        v
choose K largest c[i]
        |
        v
sum(top K) >= 0
        |
        v
T T T T T F F F
        ^
        |
     last True
```

---

## The Shared Algebra Pattern

```text
        FRACTION / AVERAGE >= x
                  |
                  v
          multiply denominator
                  |
                  v
            move x to left
                  |
                  v
      transformed linear expression
                  |
                  v
           easy feasibility check
                  |
                  v
             binary search x
```

## 60-Second Recognition

```text
MAX/MIN AVERAGE OR RATIO?
          |
          v
GUESS x
          |
          v
CAN I WRITE "VALUE >= x"?
          |
          v
REMOVE DIVISION
          |
          v
DO I GET:
a[i]-x
or
a[i]-x*b[i] ?
          |
          v
WHAT STRUCTURE REMAINS?
    /                     \
subarray                choose K
   |                       |
prefix/min-prefix       top-K sum
```

## One Sentence to Remember

```text
When the answer is an average or ratio,
binary search the answer and move the guessed value x
inside every term until the checker becomes a sum problem.
```
