# AlgoZenith — Binary Search Foundations
## Recognition, Classical Search, Lower/Upper Bound, Rotated Array, Peak Finding
### TLE-Style Self-Study Notes

> **Source:** supplied AlgoZenith Binary Search / Formulation Fundamentals lecture screenshots.
>
> The lecture builds Binary Search from two separate skills:
>
> ```text
> 1. RECOGNIZE
>    → sorted
>    → monotonic
>    → optimization / minimum / maximum
>
> 2. SOLVE
>    → design a check / predicate
>    → identify 0...01...1 or 1...10...0
>    → binary-search the boundary
> ```
>
> The screenshots cover:
>
> ```text
> Classical Binary Search
> First True in a monotonic Boolean array
> Lower Bound
> Upper Bound
> Rotated Sorted Array / rotation point
> Peak Finding in a Bitonic Array
> ```
>
> **Teaching flow used in this note**
>
> ```text
> prerequisite
> → tiny dry run
> → what the problem asks
> → expected result
> → predicate / invariant
> → detailed dry run
> → compact C++17
> → complexity
> → recognition model
> ```
>
> Numerical examples labelled **Teaching Dry Run** are added only to make the lecture idea easier to understand.

---

# Clickable Table of Contents

- [0. Class Map](#0-class-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 What Binary Search Really Needs](#11-what-binary-search-really-needs)
  - [1.2 Sorted vs Monotonic](#12-sorted-vs-monotonic)
  - [1.3 Search-Space Invariant](#13-search-space-invariant)
  - [1.4 Midpoint Formula](#14-midpoint-formula)
  - [1.5 Why Binary Search Is O(log N)](#15-why-binary-search-is-olog-n)
  - [1.6 Boolean View — 00001111](#16-boolean-view--00001111)
  - [1.7 First True](#17-first-true)
  - [1.8 Last True / Last False](#18-last-true--last-false)
  - [1.9 Designing a Check Function](#19-designing-a-check-function)
  - [1.10 Lower Bound Meaning](#110-lower-bound-meaning)
  - [1.11 Upper Bound Meaning](#111-upper-bound-meaning)
  - [1.12 Why `ans = n` Is Useful](#112-why-ans--n-is-useful)
  - [1.13 Rotated Sorted Array Structure](#113-rotated-sorted-array-structure)
  - [1.14 Bitonic Array Structure](#114-bitonic-array-structure)
  - [1.15 Recognition Checklist](#115-recognition-checklist)
- [2. Form 1 — Classical Binary Search](#2-form-1--classical-binary-search)
  - [2.1 What It Asks](#21-what-it-asks)
  - [2.2 Small Dry Run First](#22-small-dry-run-first)
  - [2.3 Decision at Mid](#23-decision-at-mid)
  - [2.4 Detailed Dry Run](#24-detailed-dry-run)
  - [2.5 C++17](#25-c17)
  - [2.6 Complexity](#26-complexity)
  - [2.7 Recognition Model](#27-recognition-model)
- [3. Form 2 — First True in a Monotonic Array](#3-form-2--first-true-in-a-monotonic-array)
  - [3.1 What It Asks](#31-what-it-asks)
  - [3.2 Small Dry Run](#32-small-dry-run)
  - [3.3 Predicate Shape](#33-predicate-shape)
  - [3.4 Detailed Dry Run](#34-detailed-dry-run)
  - [3.5 Generic C++17](#35-generic-c17)
  - [3.6 Recognition Model](#36-recognition-model)
- [4. Form 3 — Lower Bound](#4-form-3--lower-bound)
  - [4.1 What It Asks](#41-what-it-asks)
  - [4.2 Mathematical Meaning](#42-mathematical-meaning)
  - [4.3 Transform Into 00001111](#43-transform-into-00001111)
  - [4.4 Detailed Dry Run](#44-detailed-dry-run)
  - [4.5 C++17](#45-c17)
  - [4.6 Useful Counting Meaning](#46-useful-counting-meaning)
  - [4.7 Recognition Model](#47-recognition-model)
- [5. Form 4 — Upper Bound](#5-form-4--upper-bound)
  - [5.1 What It Asks](#51-what-it-asks)
  - [5.2 Mathematical Meaning](#52-mathematical-meaning)
  - [5.3 Transform Into 00001111](#53-transform-into-00001111)
  - [5.4 Detailed Dry Run](#54-detailed-dry-run)
  - [5.5 C++17](#55-c17)
  - [5.6 Useful Counting Meaning](#56-useful-counting-meaning)
  - [5.7 Recognition Model](#57-recognition-model)
- [6. Lower Bound vs Upper Bound](#6-lower-bound-vs-upper-bound)
- [7. Form 5 — Rotated Sorted Array / Rotation Point](#7-form-5--rotated-sorted-array--rotation-point)
  - [7.1 What It Asks](#71-what-it-asks)
  - [7.2 Observe the Two Sorted Blocks](#72-observe-the-two-sorted-blocks)
  - [7.3 Build a Monotonic Predicate](#73-build-a-monotonic-predicate)
  - [7.4 Detailed Dry Run](#74-detailed-dry-run)
  - [7.5 C++17](#75-c17)
  - [7.6 Recognition Model](#76-recognition-model)
- [8. Form 6 — Peak Finding in a Bitonic Array](#8-form-6--peak-finding-in-a-bitonic-array)
  - [8.1 What It Asks](#81-what-it-asks)
  - [8.2 Why the Array Is Not Sorted](#82-why-the-array-is-not-sorted)
  - [8.3 Convert Slope Into a Boolean Array](#83-convert-slope-into-a-boolean-array)
  - [8.4 Why First True Is the Peak](#84-why-first-true-is-the-peak)
  - [8.5 Detailed Dry Run](#85-detailed-dry-run)
  - [8.6 C++17](#86-c17)
  - [8.7 Recognition Model](#87-recognition-model)
- [9. Application Forms Map](#9-application-forms-map)
- [10. Common Mistakes](#10-common-mistakes)
- [11. Final Mental Model](#11-final-mental-model)

---

# 0. Class Map

The lecture's main message is:

```text
BINARY SEARCH
│
├── RECOGNIZE
│   │
│   ├── sorted data
│   ├── monotonic predicate
│   ├── minimum / maximum
│   └── optimization
│
└── SOLVE
    │
    ├── define search space
    ├── design check(mid)
    ├── understand True/False direction
    └── search the transition
```

Main forms in the screenshots:

```text
1. Exact Search
   sorted array
   → compare arr[mid] with x

2. First True
   0 0 0 0 1 1 1 1
           ^
           boundary

3. Lower Bound
   check(i) = arr[i] >= x

4. Upper Bound
   check(i) = arr[i] > x

5. Rotated Sorted Array
   convert two sorted blocks into
   a monotonic Boolean pattern

6. Peak Finding
   compare arr[i] with arr[i+1]
   → increasing side = 0
   → decreasing side = 1
```

---

# 1. Prerequisites

## 1.1 What Binary Search Really Needs

A beginner often thinks:

```text
Binary Search
=
search x in a sorted array
```

That is only the classical form.

The broader idea is:

```text
Binary Search
=
there is a search space
and information at mid lets us
discard one side safely
```

The lecture emphasizes recognizing:

```text
sorted
monotonic
minimum / maximum
optimization
```

### Tiny Dry Run

Suppose the answer space behaves like:

```text
0 0 0 0 1 1 1 1
```

Even though this is not a normal sorted numeric array problem, we can search for:

```text
first 1
```

because the predicate changes only once.

---

## 1.2 Sorted vs Monotonic

### Sorted Numeric Array

Example:

```text
[2,5,9,13,21,35,100]
```

For target:

```text
35
```

comparison with `mid` gives direction.

---

### Monotonic Boolean Array

Example:

```text
[0,0,0,0,1,1,1,1,1]
```

There is a single transition:

```text
0 → 1
```

We can binary-search that transition.

### Key Difference

```text
Sorted:
values themselves are ordered.

Monotonic predicate:
check(index) values are ordered.
```

Binary Search can work on either.

---

## 1.3 Search-Space Invariant

At every step maintain:

```text
low
high
```

representing the only region where the answer can still exist.

ASCII:

```text
discarded       possible answer        discarded
<--------|--------------------------|-------->
         low                      high
```

After evaluating `mid`, prove one side cannot contain the answer.

Then discard it.

That proof is the heart of Binary Search.

---

## 1.4 Midpoint Formula

Simple:

```text
mid = (low + high) / 2
```

Safer integer form:

```text
mid = low + (high-low)/2
```

Why safer?

If:

```text
low
and
high
```

are very large, `low + high` can overflow an integer type.

So prefer:

```cpp
int mid = low + (high - low) / 2;
```

### Tiny Dry Run

```text
low = 4
high = 8
```

```text
mid = 4 + (8-4)/2 = 6
```

---

## 1.5 Why Binary Search Is O(log N)

Every iteration approximately halves the remaining search space.

```text
N
→ N/2
→ N/4
→ N/8
→ ...
→ 1
```

After `k` steps:

```text
remaining ≈ N / 2^k
```

Stop near:

```text
N / 2^k <= 1
```

Therefore:

```text
2^k >= N
```

So:

```text
k ≈ log2(N)
```

### Tiny Dry Run

```text
N = 8
```

```text
8 → 4 → 2 → 1
```

Only:

```text
3
```

halvings.

---

## 1.6 Boolean View — 00001111

A huge number of Binary Search applications can be converted to:

```text
0 0 0 0 1 1 1 1
```

or:

```text
1 1 1 1 0 0 0 0
```

The lecture visualizes:

```text
[0,0,1,1,1,1,1,1,1]
```

with:

```text
low
high
mid
```

and searches for the transition.

### Core Question

Instead of asking:

```text
Where is x?
```

ask:

```text
Where does check(i) change?
```

---

## 1.7 First True

Predicate:

```text
F F F F T T T T
        ^
        |
    first True
```

Generic strategy:

```text
if check(mid) is True:
    mid could be the answer
    save mid
    search LEFT

else:
    first True must be RIGHT
```

Code movement:

```text
True  → ans = mid, high = mid-1
False → low = mid+1
```

### Tiny Dry Run

```text
0 0 0 1 1 1
      ^
```

First True index:

```text
3
```

---

## 1.8 Last True / Last False

Sometimes the required boundary is the other side.

### Last True

```text
T T T T F F F
      ^
```

If `check(mid)` is True and we want the last True:

```text
save mid
search RIGHT
```

### Last False in 00001111

```text
F F F F T T T
      ^
```

The last False is one position before first True.

Often:

```text
lastFalse = firstTrue - 1
```

when first True exists.

---

## 1.9 Designing a Check Function

The lecture emphasizes:

```text
designing check
```

A good `check(mid)` should answer a yes/no question whose answers are monotonic.

Examples:

```text
Lower Bound:
check(i) = arr[i] >= x
```

```text
Upper Bound:
check(i) = arr[i] > x
```

```text
Peak:
check(i) = arr[i] > arr[i+1]
```

```text
Rotated Array:
check(i) = arr[i] < arr[0]
```

for the distinct, rotated case shown in the lecture.

The real skill is:

```text
turn the original array/problem
into a monotonic Boolean array
```

---

## 1.10 Lower Bound Meaning

For sorted array:

```text
lower_bound(arr, x)
```

means:

```text
first index i such that arr[i] >= x
```

### Teaching Dry Run

```text
arr = [1,1,3,5,5,5,7,7,9]
x = 5
```

First element:

```text
>= 5
```

is at index:

```text
3
```

So:

```text
lower_bound(5) = 3
```

---

## 1.11 Upper Bound Meaning

For sorted array:

```text
upper_bound(arr, x)
```

means:

```text
first index i such that arr[i] > x
```

Same example:

```text
arr = [1,1,3,5,5,5,7,7,9]
x = 5
```

First value:

```text
> 5
```

is:

```text
7
```

at index:

```text
6
```

So:

```text
upper_bound(5) = 6
```

---

## 1.12 Why `ans = n` Is Useful

What if no element satisfies the condition?

Example:

```text
arr = [1,3,5,7]
x = 10
```

There is no element:

```text
>= 10
```

For lower bound, standard answer is:

```text
n
```

meaning:

```text
position after the last element
```

So initialize:

```cpp
int ans = n;
```

If a valid True is found, overwrite it.

Otherwise:

```text
ans remains n
```

This matches C++ iterator-style behavior:

```text
end()
```

---

## 1.13 Rotated Sorted Array Structure

Start sorted:

```text
[1,2,3,5,9]
```

Rotate:

```text
[9,1,2,3,5]
[5,9,1,2,3]
[3,5,9,1,2]
[2,3,5,9,1]
```

A rotated sorted array consists of two sorted blocks.

Example:

```text
[3,5,9 | 1,2]
```

The minimum / rotation point is:

```text
1
```

at the start of the second block.

The lecture turns this into a monotonic predicate.

---

## 1.14 Bitonic Array Structure

A bitonic array:

```text
strictly increases
then
strictly decreases
```

Example:

```text
[1,5,9,11,9,2,1]
```

ASCII:

```text
          11
        /    \
       9      9
      /        \
     5          2
    /            \
   1              1
```

Peak:

```text
11
```

We can identify which side `mid` lies on by comparing:

```text
arr[mid]
and
arr[mid+1]
```

---

## 1.15 Recognition Checklist

Before writing Binary Search, ask:

```text
1. What is my search space?
   index?
   value?
   answer?

2. Is the array sorted?

3. If not sorted, can I build
   a monotonic check(mid)?

4. What Boolean shape do I get?
   00001111?
   11110000?

5. Which boundary do I need?
   first True?
   last True?
   first False?
   last False?

6. What does check(mid) mean?

7. If check(mid) is True,
   which side can I safely discard?

8. What should the default answer be
   if no valid position exists?
```

---

# 2. Form 1 — Classical Binary Search

## 2.1 What It Asks

Given a sorted array and target:

```text
x
```

determine whether `x` exists, or return its index.

Example:

```text
arr = [1,5,10,15,21,35,100]
x = 35
```

Expected:

```text
index = 5
```

---

## 2.2 Small Dry Run First

```text
arr = [1,5,10,15,21,35,100]
x = 35
```

Initial:

```text
low = 0
high = 6
```

Mid:

```text
mid = 3
arr[mid] = 15
```

Since:

```text
15 < 35
```

target must be to the right.

New range:

```text
[4,6]
```

---

## 2.3 Decision at Mid

Three cases:

```text
arr[mid] == x
→ found
```

```text
arr[mid] < x
→ all values at or left of mid are too small
→ low = mid+1
```

```text
arr[mid] > x
→ all values at or right of mid are too large
→ high = mid-1
```

Why can we discard an entire side?

Because the array is sorted.

---

## 2.4 Detailed Dry Run

```text
arr = [1,5,10,15,21,35,100]
x = 35
```

### Iteration 1

```text
low = 0
high = 6
mid = 3
arr[mid] = 15
```

```text
15 < 35
```

So:

```text
low = 4
```

### Iteration 2

```text
low = 4
high = 6
mid = 5
arr[mid] = 35
```

Found:

```text
answer = 5
```

ASCII:

```text
[1,5,10,15,21,35,100]
 0 1  2  3  4  5   6
          ^
        mid=3

discard left side

            [21,35,100]
              4  5  6
                 ^
               mid=5
               found
```

---

## 2.5 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int binarySearch(const vector<int>& arr, int x) {
    int low = 0;
    int high = (int)arr.size() - 1;

    while (low <= high) {
        int mid = low + (high - low) / 2;

        if (arr[mid] == x) {
            return mid;
        }

        if (arr[mid] < x) {
            low = mid + 1;
        } else {
            high = mid - 1;
        }
    }

    return -1;
}

int main() {
    vector<int> arr = {1, 5, 10, 15, 21, 35, 100};
    int x = 35;

    cout << binarySearch(arr, x) << '\n';
}
```

---

## 2.6 Complexity

Each step halves the search range.

Time:

```text
O(log N)
```

Extra space:

```text
O(1)
```

---

## 2.7 Recognition Model

```text
sorted array
+
find exact x
```

Think:

```text
arr[mid] == x → done
arr[mid] < x  → right
arr[mid] > x  → left
```

---

# 3. Form 2 — First True in a Monotonic Array

## 3.1 What It Asks

Given:

```text
0 0 0 0 1 1 1 1 1
```

find:

```text
first 1
```

The lecture uses this Boolean transition as the foundation for many later forms.

---

## 3.2 Small Dry Run

```text
arr = [0,0,0,0,1,1,1,1,1]
```

Expected:

```text
index = 4
```

Initial:

```text
low = 0
high = 8
```

Mid:

```text
mid = 4
arr[4] = 1
```

`4` could be the first 1.

But maybe another `1` exists earlier.

So:

```text
ans = 4
high = 3
```

Continue left.

---

## 3.3 Predicate Shape

Think:

```text
False False False False True True True True
                        ^
                        |
                    first True
```

If:

```text
check(mid) = True
```

then:

```text
answer <= mid
```

So search left.

If:

```text
check(mid) = False
```

then:

```text
answer > mid
```

So search right.

---

## 3.4 Detailed Dry Run

```text
[0,0,0,0,1,1,1,1,1]
 0 1 2 3 4 5 6 7 8
```

### Iteration 1

```text
low = 0
high = 8
mid = 4
```

```text
arr[4] = 1
```

Save:

```text
ans = 4
```

Go left:

```text
high = 3
```

### Iteration 2

```text
low = 0
high = 3
mid = 1
```

```text
arr[1] = 0
```

Go right:

```text
low = 2
```

### Iteration 3

```text
low = 2
high = 3
mid = 2
```

```text
0
```

Go right:

```text
low = 3
```

### Iteration 4

```text
mid = 3
arr[3] = 0
```

Go right:

```text
low = 4
```

Stop.

Answer:

```text
4
```

---

## 3.5 Generic C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int firstTrue(const vector<int>& arr) {
    int n = (int)arr.size();
    int low = 0;
    int high = n - 1;
    int answer = n;

    while (low <= high) {
        int mid = low + (high - low) / 2;

        if (arr[mid] == 1) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    return answer;
}
```

Core template:

```cpp
if (check(mid)) {
    answer = mid;
    high = mid - 1;
} else {
    low = mid + 1;
}
```

---

## 3.6 Recognition Model

Whenever you can create:

```text
0 0 0 0 1 1 1
```

think:

```text
find first True
```

The original problem may not mention Boolean values at all.

Your job is to invent them through:

```text
check(mid)
```

---

# 4. Form 3 — Lower Bound

## 4.1 What It Asks

For sorted array and value:

```text
x
```

find:

```text
first index i
such that
arr[i] >= x
```

This is:

```text
lower_bound
```

---

## 4.2 Mathematical Meaning

```text
lower_bound(x)
=
first element >= x
```

It also gives the number of elements:

```text
< x
```

because every index before lower bound contains a value smaller than `x`.

If lower-bound index is:

```text
p
```

then:

```text
#elements < x = p
```

for 0-based indexing.

---

## 4.3 Transform Into 00001111

Use:

```text
check(i) = (arr[i] >= x)
```

For:

```text
arr = [1,1,3,5,5,5,7,7,9]
x = 5
```

Evaluate:

```text
arr[i]:   1 1 3 5 5 5 7 7 9
check:    0 0 0 1 1 1 1 1 1
index:    0 1 2 3 4 5 6 7 8
                ^
                |
            first True
```

So lower bound is simply:

```text
first True of arr[i] >= x
```

---

## 4.4 Detailed Dry Run

```text
arr = [1,1,3,5,5,5,7,7,9]
x = 5
```

Initial:

```text
low = 0
high = 8
answer = 9
```

### Iteration 1

```text
mid = 4
arr[4] = 5
```

Check:

```text
5 >= 5
True
```

Save:

```text
answer = 4
```

Search left:

```text
high = 3
```

### Iteration 2

```text
mid = 1
arr[1] = 1
```

```text
1 >= 5
False
```

Search right:

```text
low = 2
```

### Iteration 3

```text
low = 2
high = 3
mid = 2
arr[2] = 3
```

False:

```text
low = 3
```

### Iteration 4

```text
mid = 3
arr[3] = 5
```

True:

```text
answer = 3
high = 2
```

Stop.

Result:

```text
lower_bound = 3
```

---

## 4.5 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int lowerBound(const vector<int>& arr, int x) {
    int n = (int)arr.size();
    int low = 0;
    int high = n - 1;
    int answer = n;

    while (low <= high) {
        int mid = low + (high - low) / 2;

        if (arr[mid] >= x) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    return answer;
}

int main() {
    vector<int> arr = {1, 1, 3, 5, 5, 5, 7, 7, 9};
    int x = 5;

    cout << lowerBound(arr, x) << '\n';
}
```

---

## 4.6 Useful Counting Meaning

Example:

```text
arr = [1,1,3,5,5,5,7,7,9]
x = 5
```

Lower bound:

```text
3
```

Therefore number of elements:

```text
< 5
```

is:

```text
3
```

They are:

```text
1,1,3
```

---

## 4.7 Recognition Model

When statement asks:

```text
first value >= x
```

or:

```text
first position where condition becomes valid
```

think:

```text
Lower Bound
=
first True of arr[i] >= x
```

---

# 5. Form 4 — Upper Bound

## 5.1 What It Asks

For sorted array and value:

```text
x
```

find:

```text
first index i
such that
arr[i] > x
```

This is:

```text
upper_bound
```

---

## 5.2 Mathematical Meaning

```text
upper_bound(x)
=
first element > x
```

It also gives number of elements:

```text
<= x
```

because every element before the upper-bound index is at most `x`.

If upper-bound index is:

```text
p
```

then:

```text
#elements <= x = p
```

---

## 5.3 Transform Into 00001111

Use:

```text
check(i) = (arr[i] > x)
```

Example:

```text
arr = [1,1,3,5,5,5,7,7,9]
x = 5
```

Transform:

```text
arr[i]:   1 1 3 5 5 5 7 7 9
check:    0 0 0 0 0 0 1 1 1
index:    0 1 2 3 4 5 6 7 8
                      ^
                      |
                  first True
```

So:

```text
upper_bound(5) = 6
```

---

## 5.4 Detailed Dry Run

```text
low = 0
high = 8
answer = 9
```

### Iteration 1

```text
mid = 4
arr[4] = 5
```

Check:

```text
5 > 5
False
```

Go right:

```text
low = 5
```

### Iteration 2

```text
low = 5
high = 8
mid = 6
arr[6] = 7
```

```text
7 > 5
True
```

Save:

```text
answer = 6
```

Go left:

```text
high = 5
```

### Iteration 3

```text
mid = 5
arr[5] = 5
```

False:

```text
low = 6
```

Stop.

Result:

```text
upper_bound = 6
```

---

## 5.5 C++17

This matches the lecture's visible `check(mid)` idea:

```cpp
#include <bits/stdc++.h>
using namespace std;

int upperBound(const vector<int>& arr, int x) {
    int n = (int)arr.size();
    int low = 0;
    int high = n - 1;
    int answer = n;

    while (low <= high) {
        int mid = low + (high - low) / 2;

        if (arr[mid] > x) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    return answer;
}

int main() {
    vector<int> arr = {1, 1, 3, 5, 5, 5, 7, 7, 9};
    int x = 5;

    cout << upperBound(arr, x) << '\n';
}
```

---

## 5.6 Useful Counting Meaning

Same example:

```text
upper_bound(5) = 6
```

Therefore number of elements:

```text
<= 5
```

is:

```text
6
```

They are:

```text
1,1,3,5,5,5
```

---

## 5.7 Recognition Model

When statement asks:

```text
first value > x
```

think:

```text
Upper Bound
=
first True of arr[i] > x
```

---

# 6. Lower Bound vs Upper Bound

Use:

```text
arr = [1,1,3,5,5,5,7,7,9]
x = 5
```

| Operation | Predicate | Result index | Counting meaning |
|---|---|---:|---|
| Lower Bound | `arr[i] >= x` | `3` | `3` elements `< x` |
| Upper Bound | `arr[i] > x` | `6` | `6` elements `<= x` |

ASCII:

```text
arr:
1 1 3 5 5 5 7 7 9
0 1 2 3 4 5 6 7 8
      ^     ^
      |     |
      LB    UB
```

Therefore number of occurrences of `x` is:

```text
upper_bound(x) - lower_bound(x)
```

For `5`:

```text
6 - 3 = 3
```

Correct:

```text
5 appears 3 times
```

---

# 7. Form 5 — Rotated Sorted Array / Rotation Point

## 7.1 What It Asks

The lecture shows rotations of a sorted array.

Start:

```text
[1,2,3,5,9]
```

Possible rotations:

```text
[9,1,2,3,5]
[5,9,1,2,3]
[3,5,9,1,2]
[2,3,5,9,1]
```

The useful boundary is:

```text
where the smaller sorted block starts
```

That index is the:

```text
rotation point
```

and contains the minimum.

---

## 7.2 Observe the Two Sorted Blocks

Take:

```text
arr = [3,5,9,1,2]
```

ASCII:

```text
first block      second block
[3,5,9]          [1,2]
         \        /
          rotation
```

Relative to:

```text
arr[0] = 3
```

first block values are:

```text
>= 3
```

second block values are:

```text
< 3
```

for the distinct-value rotated case shown.

---

## 7.3 Build a Monotonic Predicate

Define:

```text
check(i) = arr[i] < arr[0]
```

For:

```text
arr = [3,5,9,1,2]
```

we get:

```text
arr[i]:   3 5 9 1 2
check:    0 0 0 1 1
index:    0 1 2 3 4
                ^
                |
            first True
```

First True:

```text
3
```

And:

```text
arr[3] = 1
```

which is the minimum / rotation point.

This is exactly the lecture's formulation idea:

```text
rotated array
→ create monotonic Boolean array
→ first True
```

---

## 7.4 Detailed Dry Run

```text
arr = [3,5,9,1,2]
```

Initialize:

```text
low = 0
high = 4
answer = 0
```

Since this array is rotated and:

```text
arr[4] < arr[0]
```

a second block exists.

Search first `arr[i] < arr[0]`.

### Iteration 1

```text
mid = 2
arr[2] = 9
```

Check:

```text
9 < 3
False
```

Go right:

```text
low = 3
```

### Iteration 2

```text
low = 3
high = 4
mid = 3
arr[3] = 1
```

Check:

```text
1 < 3
True
```

Save:

```text
answer = 3
```

Go left:

```text
high = 2
```

Stop.

Result:

```text
rotation index = 3
minimum = 1
```

---

## 7.5 C++17

For the distinct-value case illustrated in the lecture:

```cpp
#include <bits/stdc++.h>
using namespace std;

int rotationPoint(const vector<int>& arr) {
    int n = (int)arr.size();

    if (n == 0) {
        return -1;
    }

    if (arr[n - 1] >= arr[0]) {
        return 0;
    }

    int low = 0;
    int high = n - 1;
    int answer = n - 1;

    while (low <= high) {
        int mid = low + (high - low) / 2;

        if (arr[mid] < arr[0]) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    return answer;
}

int main() {
    vector<int> arr = {3, 5, 9, 1, 2};

    int pivot = rotationPoint(arr);

    cout << "pivot = " << pivot << '\n';
    cout << "minimum = " << arr[pivot] << '\n';
}
```

### Important Scope

This predicate relies on the clean distinct-value rotated structure shown in the lecture.

Duplicates can require additional handling because the Boolean separation may become ambiguous.

---

## 7.6 Recognition Model

When you see:

```text
sorted array
rotated at one point
```

think:

```text
two sorted blocks
```

Then ask:

```text
Can I label one block 0
and the other block 1?
```

Lecture predicate:

```text
arr[i] < arr[0]
```

becomes:

```text
0 0 0 1 1
      ^
      |
  rotation point
```

---

# 8. Form 6 — Peak Finding in a Bitonic Array

## 8.1 What It Asks

Given a bitonic array:

```text
increasing
then
decreasing
```

find the peak.

Lecture-style example:

```text
[1,5,9,11,9,2,1]
```

Expected:

```text
peak index = 3
peak value = 11
```

---

## 8.2 Why the Array Is Not Sorted

The entire array is not increasing.

It looks like:

```text
          peak
           11
         /    \
        9      9
       /        \
      5          2
     /            \
    1              1
```

So classical:

```text
arr[mid] < x
```

search logic does not apply directly.

But the **slope direction** is monotonic.

---

## 8.3 Convert Slope Into a Boolean Array

Define:

```text
check(i) = arr[i] > arr[i+1]
```

Interpretation:

```text
False
→ still going upward

True
→ going downward
```

For:

```text
arr = [1,5,9,11,9,2,1]
```

Compare neighbors:

```text
1  > 5 ? 0
5  > 9 ? 0
9  > 11? 0
11 > 9 ? 1
9  > 2 ? 1
2  > 1 ? 1
```

So:

```text
check:
0 0 0 1 1 1
      ^
      |
   first True
```

The first descending edge begins at the peak.

---

## 8.4 Why First True Is the Peak

Before peak:

```text
arr[i] < arr[i+1]
```

So:

```text
check(i) = False
```

At peak:

```text
arr[peak] > arr[peak+1]
```

So:

```text
check(peak) = True
```

After peak:

```text
arr[i] > arr[i+1]
```

also True.

Therefore:

```text
0 0 0 1 1 1
      ^
      |
 first True = peak
```

This is the core lecture transformation.

---

## 8.5 Detailed Dry Run

```text
arr = [1,5,9,11,9,2,1]
index  0 1 2  3 4 5 6
```

Search:

```text
low = 0
high = 6
```

### Iteration 1

```text
mid = 3
```

Check:

```text
arr[3] > arr[4]
11 > 9
True
```

So peak is:

```text
at mid or left of mid
```

Save:

```text
answer = 3
```

Search left:

```text
high = 2
```

### Iteration 2

```text
low = 0
high = 2
mid = 1
```

```text
arr[1] > arr[2]
5 > 9
False
```

Still climbing.

Peak must be right:

```text
low = 2
```

### Iteration 3

```text
mid = 2
```

```text
9 > 11
False
```

Go right:

```text
low = 3
```

Stop.

Answer:

```text
index 3
value 11
```

---

## 8.6 C++17

Compact lecture-style formulation:

```cpp
#include <bits/stdc++.h>
using namespace std;

int findPeak(const vector<int>& arr) {
    int n = (int)arr.size();
    int low = 0;
    int high = n - 1;
    int answer = n - 1;

    while (low <= high) {
        int mid = low + (high - low) / 2;

        bool descending = (mid == n - 1 || arr[mid] > arr[mid + 1]);

        if (descending) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    return answer;
}

int main() {
    vector<int> arr = {1, 5, 9, 11, 9, 2, 1};

    int peak = findPeak(arr);

    cout << "index = " << peak << '\n';
    cout << "value = " << arr[peak] << '\n';
}
```

The key line is:

```cpp
arr[mid] > arr[mid + 1]
```

It converts:

```text
shape / slope
```

into:

```text
False ... False True ... True
```

---

## 8.7 Recognition Model

When you see:

```text
increasing then decreasing
```

do not search the values directly.

Search the:

```text
slope change
```

Use:

```text
arr[mid] > arr[mid+1]
```

Interpret:

```text
False → climbing → peak right
True  → descending → peak at mid or left
```

Therefore:

```text
first True = peak
```

---

# 9. Application Forms Map

The opening lecture screenshot groups Binary Search recognition around several ideas.

A useful mental map is:

| Situation | What to binary-search |
|---|---|
| Sorted array, exact value | index of `x` |
| `00001111` | first True |
| `11110000` | last True / first False |
| First value `>= x` | lower bound |
| First value `> x` | upper bound |
| Rotated sorted array | boundary between the two sorted blocks |
| Bitonic array | first descending edge |
| Minimum feasible answer | first True in answer space |
| Maximum feasible answer | last True in answer space |
| Optimization with huge values | answer value, using a monotonic checker |

The lecture overview also points to constraints such as:

```text
N around 10^5
values around 10^9 / 10^18
```

as a signal that:

```text
linear search over VALUE SPACE
```

may be impossible while:

```text
logarithmic search over VALUE SPACE
```

may be practical.

---

# 10. Common Mistakes

## Mistake 1 — Thinking Binary Search Requires a Sorted Array

Sorted arrays are only one form.

Binary Search fundamentally requires:

```text
a safe way to discard one side
```

Usually from monotonic information.

---

## Mistake 2 — Writing the Loop Before Defining `check`

For application-style problems, first write:

```text
What does check(mid) mean?
```

Then verify:

```text
00001111
or
11110000
```

Only then write Binary Search.

---

## Mistake 3 — Wrong Boundary Direction

For first True:

```text
True
→ save mid
→ go left
```

For last True:

```text
True
→ save mid
→ go right
```

Do not memorize movement without knowing which boundary you need.

---

## Mistake 4 — Lower Bound vs Upper Bound Confusion

Lower Bound:

```text
>= x
```

Upper Bound:

```text
> x
```

One character changes the boundary.

---

## Mistake 5 — Forgetting the No-Answer Case

If no element satisfies lower/upper-bound predicate:

```text
answer = n
```

is useful.

Example:

```text
arr = [1,3,5]
x = 10
```

Both lower and upper bound return:

```text
3
```

---

## Mistake 6 — Infinite Loop / No Progress

Every iteration must shrink the interval.

Typical inclusive search:

```text
low = mid + 1
```

or:

```text
high = mid - 1
```

Not:

```text
low = mid
```

in this template, because it can get stuck.

---

## Mistake 7 — Wrong Mid Formula

Prefer:

```cpp
int mid = low + (high - low) / 2;
```

especially when bounds may be large.

---

## Mistake 8 — Rotated Array Without Checking Assumptions

The lecture predicate:

```text
arr[i] < arr[0]
```

creates a clean boundary for the distinct-value rotated structure shown.

Duplicates may destroy that clean distinction.

---

## Mistake 9 — Peak: Checking Only `arr[mid]` Value

Peak search is not:

```text
is arr[mid] globally largest?
```

The efficient local signal is:

```text
arr[mid] > arr[mid+1]
```

which tells whether we are on the descending side.

---

## Mistake 10 — Boundary Out-of-Range in Peak Search

When checking:

```text
arr[mid+1]
```

handle:

```text
mid == n-1
```

The code above treats the last index as descending / True.

---

# 11. Final Mental Model

## Binary Search Is Boundary Search

```text
ORIGINAL PROBLEM
      |
      v
WHAT CAN I CHECK AT mid?
      |
      v
CAN I DISCARD ONE SIDE?
      |
      v
CREATE MONOTONIC VIEW
      |
      +----------------------+
      |                      |
      v                      v
0 0 0 1 1 1            1 1 1 0 0 0
      |                      |
      v                      v
FIRST TRUE              LAST TRUE /
                       FIRST FALSE
```

---

## Classical Search

```text
sorted array

arr[mid] < x
→ right

arr[mid] > x
→ left

arr[mid] == x
→ found
```

---

## Lower Bound

```text
check(i) = arr[i] >= x

0 0 0 1 1 1
      ^
      |
first True
```

---

## Upper Bound

```text
check(i) = arr[i] > x

0 0 0 0 1 1
        ^
        |
    first True
```

---

## Rotated Sorted Array

```text
[large block | small block]

check(i) = arr[i] < arr[0]

0 0 0 1 1
      ^
      |
rotation point / minimum
```

---

## Bitonic Peak

```text
          peak
         /    \
        /      \
       /        \

check(i) = arr[i] > arr[i+1]

0 0 0 1 1 1
      ^
      |
   peak index
```

---

## 60-Second Recognition

```text
IS IT SORTED?
    |
   YES
    |
    +--> exact value?
    |      → classical BS
    |
    +--> first >= x?
    |      → lower_bound
    |
    +--> first > x?
           → upper_bound
```

```text
NOT FULLY SORTED?
    |
    v
CAN I CREATE A MONOTONIC CHECK?
    |
   YES
    |
    +--> rotated array
    |      → identify block boundary
    |
    +--> bitonic array
    |      → identify slope boundary
    |
    +--> optimization
           → feasibility boundary
```

---

## One Sentence to Remember

```text
Do not ask "Is this a Binary Search problem?"
Ask "Can I define a monotonic check(mid)
that lets me safely discard one side?"
```
