# Prefix Sum Problem Solving & Difference Arrays
### Step-by-Step Visual Notes with Prerequisites, ASCII Diagrams and C++17

> Based on the uploaded TLE Level 2 lecture:
> **Prefix Sums Problem Solving & Difference Arrays**
>
> Lecture flow:
>
> 1. Prefix Sum Problem 1 — weighted range query
> 2. Prefix Sum Problem 2 — Contiguous Array
> 3. Difference Arrays
> 4. Practice reference — Greg and Array

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Quick Prefix Sum Refresher](#2-quick-prefix-sum-refresher)
3. [Problem 1 — Weighted Range Sum Query](#3-problem-1--weighted-range-sum-query)
4. [Step 1 — Understand the Query](#4-step-1--understand-the-query)
5. [Step 2 — Convert the Pattern into Mathematics](#5-step-2--convert-the-pattern-into-mathematics)
6. [Step 3 — Split the Formula](#6-step-3--split-the-formula)
7. [Step 4 — Build Two Prefix Arrays](#7-step-4--build-two-prefix-arrays)
8. [Step 5 — Query Formula](#8-step-5--query-formula)
9. [Weighted Query — Dry Run](#9-weighted-query--dry-run)
10. [Weighted Query — C++17](#10-weighted-query--c17)
11. [Problem 2 — Contiguous Array](#11-problem-2--contiguous-array)
12. [Step 1 — Convert 0 and 1](#12-step-1--convert-0-and-1)
13. [Step 2 — Equal 0s and 1s Means Sum Zero](#13-step-2--equal-0s-and-1s-means-sum-zero)
14. [Step 3 — Prefix Sum Repetition](#14-step-3--prefix-sum-repetition)
15. [Step 4 — Hash Map Technique](#15-step-4--hash-map-technique)
16. [Contiguous Array — Dry Run](#16-contiguous-array--dry-run)
17. [Contiguous Array — C++17](#17-contiguous-array--c17)
18. [Difference Arrays — Core Idea](#18-difference-arrays--core-idea)
19. [Why Normal Range Updates Are Slow](#19-why-normal-range-updates-are-slow)
20. [Difference Array Meaning](#20-difference-array-meaning)
21. [Range Update Formula](#21-range-update-formula)
22. [Why the Formula Works](#22-why-the-formula-works)
23. [Difference Array — Full Dry Run](#23-difference-array--full-dry-run)
24. [Reconstruct the Final Array](#24-reconstruct-the-final-array)
25. [Difference Array — C++17](#25-difference-array--c17)
26. [Can We Use O(1) Extra Space?](#26-can-we-use-o1-extra-space)
27. [Greg and Array — Pattern Recognition](#27-greg-and-array--pattern-recognition)
28. [Common Mistakes](#28-common-mistakes)
29. [Prefix Sum vs Difference Array](#29-prefix-sum-vs-difference-array)
30. [Complexity Cheat Sheet](#30-complexity-cheat-sheet)
31. [Recognition Checklist](#31-recognition-checklist)
32. [Final Mental Model](#32-final-mental-model)

---

# 1. Prerequisites

Before this note, you should understand the following.

## 1.1 Array indexing

We will mainly use **1-based indexing** because it makes the formulas from the lecture easier to read.

```text
index:   1   2   3   4   5
array:   2   4   3   6   9
```

So:

```text
A[1] = 2
A[3] = 3
A[5] = 9
```

---

## 1.2 Subarray / Range

A range `[L, R]` means:

```text
all positions from L to R inclusive
```

Example:

```text
A = [2, 4, 3, 6, 9]

L = 2
R = 4

range = [4, 3, 6]
```

---

## 1.3 Basic prefix sum

If:

```text
prefix[i] = A[1] + A[2] + ... + A[i]
```

then:

```text
sum(L, R)
=
prefix[R] - prefix[L - 1]
```

ASCII:

```text
1 ........ L-1 | L ........ R
---------------+-------------
   remove      |    keep
```

---

## 1.4 Algebraic splitting

A very important skill for Problem 1 is:

```text
sum(a + b)
=
sum(a) + sum(b)
```

And:

```text
sum(c * A[i])
=
c * sum(A[i])
```

when `c` is constant with respect to `i`.

Example:

```text
Σ (i - L + 1) * A[i]
```

can be rewritten as:

```text
Σ i*A[i] - (L - 1) * Σ A[i]
```

This transformation is the key idea.

---

## 1.5 Hash map idea

A hash map lets us remember:

```text
value -> first position where it appeared
```

Example:

```text
prefix sum 3 first appeared at index 5
```

Store:

```text
firstIndex[3] = 5
```

This is used in the Contiguous Array problem.

---

## 1.6 Range updates

A range update says:

```text
Add X to every position from L to R.
```

Example:

```text
array = [0, 0, 0, 0, 0]

update:
L = 2
R = 4
X = 3

result:
[0, 3, 3, 3, 0]
```

Difference arrays are designed for many such updates.

---

# 2. Quick Prefix Sum Refresher

Suppose:

```text
A = [2, 4, 3, 6, 9]
```

Build:

```text
prefix[1] = 2
prefix[2] = 2 + 4 = 6
prefix[3] = 6 + 3 = 9
prefix[4] = 9 + 6 = 15
prefix[5] = 15 + 9 = 24
```

So:

```text
index:    0   1   2   3   4   5
prefix:   0   2   6   9  15  24
```

Query:

```text
sum(2,4)
=
prefix[4] - prefix[1]

=
15 - 2

=
13
```

Check:

```text
4 + 3 + 6 = 13
```

---

# 3. Problem 1 — Weighted Range Sum Query

The lecture gives:

```text
Given an array of N elements.

For every query (L, R), calculate:

A[L]
+ 2 * A[L + 1]
+ 3 * A[L + 2]
+ ...
+ (R - L + 1) * A[R]
```

This is not a normal range sum.

The coefficient changes:

```text
A[L]       gets coefficient 1
A[L + 1]   gets coefficient 2
A[L + 2]   gets coefficient 3
...
A[R]       gets coefficient R - L + 1
```

---

# 4. Step 1 — Understand the Query

Take:

```text
A = [2, 4, 3, 6, 9]
```

Query:

```text
L = 2
R = 4
```

Then:

```text
answer
=
1 * A[2]
+ 2 * A[3]
+ 3 * A[4]
```

Substitute values:

```text
=
1 * 4
+ 2 * 3
+ 3 * 6

=
4 + 6 + 18

=
28
```

ASCII:

```text
index:      1     2     3     4     5
A:          2     4     3     6     9
                  |     |     |
weight:            1     2     3

query result:
1*4 + 2*3 + 3*6
```

---

# 5. Step 2 — Convert the Pattern into Mathematics

For any position `i` inside `[L, R]`, what is its weight?

Check:

```text
i = L       -> weight = 1
i = L + 1   -> weight = 2
i = L + 2   -> weight = 3
```

So:

```text
weight = i - L + 1
```

Therefore:

```text
answer
=
Σ A[i] * (i - L + 1)
```

for:

```text
i = L ... R
```

---

## Verify the formula

If:

```text
i = L
```

then:

```text
i - L + 1
=
L - L + 1
=
1
```

If:

```text
i = L + 2
```

then:

```text
i - L + 1
=
L + 2 - L + 1
=
3
```

Correct.

---

# 6. Step 3 — Split the Formula

We have:

```text
A[i] * (i - L + 1)
```

Rewrite:

```text
i - L + 1
=
i - (L - 1)
```

So:

```text
A[i] * (i - L + 1)

=
A[i] * i
-
A[i] * (L - 1)
```

Now sum from `L` to `R`:

```text
answer
=
Σ i*A[i]
-
Σ (L - 1)*A[i]
```

Since `(L - 1)` is constant:

```text
answer
=
Σ i*A[i]
-
(L - 1) * Σ A[i]
```

This is the main observation.

---

## Visual algebra

```text
Original:

Σ A[i] * (i - L + 1)

                |
                v

Σ A[i] * (i - (L - 1))

                |
                v

Σ (i*A[i] - (L-1)*A[i])

                |
                v

Σ i*A[i] - (L-1) * Σ A[i]
```

Now both pieces can be answered using prefix sums.

---

# 7. Step 4 — Build Two Prefix Arrays

We need:

```text
Σ A[i]
```

and:

```text
Σ i*A[i]
```

So build two prefix arrays.

---

## Prefix 1 — Normal prefix

```text
prefixA[i]
=
A[1] + A[2] + ... + A[i]
```

---

## Prefix 2 — Weighted prefix

```text
prefixWeighted[i]
=
1*A[1]
+ 2*A[2]
+ ...
+ i*A[i]
```

Example:

```text
A = [2, 4, 3, 6, 9]
```

Normal:

```text
prefixA:

2
2+4
2+4+3
...
```

Weighted:

```text
prefixWeighted:

1*2
1*2 + 2*4
1*2 + 2*4 + 3*3
...
```

---

## ASCII table

```text
i                1    2    3    4    5

A[i]             2    4    3    6    9

i*A[i]           2    8    9   24   45

prefixA          2    6    9   15   24

prefixWeighted   2   10   19   43   88
```

---

# 8. Step 5 — Query Formula

For range `[L,R]`:

```text
sumA
=
prefixA[R] - prefixA[L - 1]
```

And:

```text
sumWeighted
=
prefixWeighted[R]
-
prefixWeighted[L - 1]
```

Therefore:

```text
answer
=
sumWeighted
-
(L - 1) * sumA
```

Final formula:

```text
answer
=
(prefixWeighted[R] - prefixWeighted[L - 1])
-
(L - 1) * (prefixA[R] - prefixA[L - 1])
```

---

# 9. Weighted Query — Dry Run

Take:

```text
A = [2, 4, 3, 6, 9]
L = 2
R = 4
```

We already know the direct answer:

```text
1*4 + 2*3 + 3*6
=
4 + 6 + 18
=
28
```

Now use prefix sums.

From the table:

```text
prefixA[4] = 15
prefixA[1] = 2

prefixWeighted[4] = 43
prefixWeighted[1] = 2
```

So:

```text
sumA
=
15 - 2
=
13
```

And:

```text
sumWeighted
=
43 - 2
=
41
```

Now:

```text
answer
=
41 - (2 - 1)*13

=
41 - 13

=
28
```

Correct.

---

# 10. Weighted Query — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    cin >> n >> q;

    vector<long long> a(n + 1);
    vector<long long> prefixA(n + 1, 0);
    vector<long long> prefixWeighted(n + 1, 0);

    for (int i = 1; i <= n; i++) {
        cin >> a[i];

        prefixA[i] = prefixA[i - 1] + a[i];
        prefixWeighted[i] = prefixWeighted[i - 1] + 1LL * i * a[i];
    }

    while (q--) {
        int l, r;
        cin >> l >> r;

        long long sumA = prefixA[r] - prefixA[l - 1];
        long long sumWeighted = prefixWeighted[r] - prefixWeighted[l - 1];

        long long answer = sumWeighted - 1LL * (l - 1) * sumA;

        cout << answer << '\n';
    }

    return 0;
}
```

Complexity:

```text
precomputation = O(N)
each query     = O(1)
all queries    = O(Q)

total          = O(N + Q)
```

---

# 11. Problem 2 — Contiguous Array

The lecture's second prefix-sum problem is the classic idea:

```text
Find the longest contiguous subarray
containing equal numbers of 0s and 1s.
```

Example:

```text
0 1 0 0 1 1 0 0
```

We need the longest continuous segment where:

```text
number of 0s
=
number of 1s
```

---

# 12. Step 1 — Convert 0 and 1

The trick is:

```text
0 -> -1
1 -> +1
```

Example:

```text
original:

0   1   0   1   0   0   1

convert:

-1 +1  -1  +1  -1  -1  +1
```

Why?

Because if a subarray has equal 0s and 1s:

```text
each 0 contributes -1
each 1 contributes +1
```

So the total becomes:

```text
0
```

---

# 13. Step 2 — Equal 0s and 1s Means Sum Zero

Suppose:

```text
subarray = [0, 1, 0, 1]
```

Convert:

```text
[-1, +1, -1, +1]
```

Sum:

```text
-1 + 1 - 1 + 1
=
0
```

Therefore:

```text
equal number of 0s and 1s
        <=>
converted subarray sum = 0
```

Now the problem becomes:

```text
Find the longest subarray with sum 0.
```

---

# 14. Step 3 — Prefix Sum Repetition

Very important prefix-sum property:

If the same prefix sum appears at positions `i` and `j`:

```text
prefix[i] = prefix[j]
```

then the sum between them is zero.

Because:

```text
prefix[j] - prefix[i] = 0
```

So:

```text
sum(i + 1 ... j) = 0
```

ASCII:

```text
start ........ i | i+1 ........ j
-----------------+---------------
   prefix = X    |  sum = 0

prefix at j is also X
```

Therefore:

```text
same prefix sum seen twice
        =>
subarray between them has sum 0
```

---

# 15. Step 4 — Hash Map Technique

We want the **longest** subarray.

So for every prefix sum, store the **first** index where it appeared.

Why first?

Because:

```text
currentIndex - earliestIndex
```

gives the longest possible length.

---

## Initialization

Before processing any element:

```text
prefix sum = 0
```

at virtual index:

```text
-1
```

So store:

```cpp
firstIndex[0] = -1;
```

This handles subarrays starting from index `0`.

---

## Processing

For each index:

```text
1. Convert:
      0 -> -1
      1 -> +1

2. Add to current prefix sum.

3. If sum has not appeared:
      remember this index.

4. Else:
      length = currentIndex - firstIndex[sum]

5. Update maximum length.
```

---

# 16. Contiguous Array — Dry Run

Take:

```text
nums = [0, 1, 0, 1]
```

Convert mentally:

```text
[-1, +1, -1, +1]
```

Start:

```text
sum = 0
firstIndex[0] = -1
answer = 0
```

---

## i = 0

Value:

```text
0 -> -1
```

So:

```text
sum = -1
```

First time seeing `-1`.

Store:

```text
firstIndex[-1] = 0
```

---

## i = 1

Value:

```text
1 -> +1
```

Now:

```text
sum = 0
```

We saw `0` at:

```text
-1
```

Length:

```text
1 - (-1)
=
2
```

So:

```text
answer = 2
```

Subarray:

```text
[0,1]
```

---

## i = 2

Value:

```text
0 -> -1
```

Now:

```text
sum = -1
```

We first saw `-1` at:

```text
0
```

Length:

```text
2 - 0
=
2
```

---

## i = 3

Value:

```text
1 -> +1
```

Now:

```text
sum = 0
```

First `0` was at:

```text
-1
```

Length:

```text
3 - (-1)
=
4
```

So:

```text
answer = 4
```

Entire array has equal 0s and 1s.

---

# 17. Contiguous Array — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestBalancedSubarray(const vector<int>& nums) {
    unordered_map<int, int> firstIndex;

    firstIndex[0] = -1;

    int prefixSum = 0;
    int answer = 0;

    for (int i = 0; i < (int)nums.size(); i++) {
        if (nums[i] == 0) {
            prefixSum -= 1;
        } else {
            prefixSum += 1;
        }

        if (firstIndex.find(prefixSum) != firstIndex.end()) {
            answer = max(answer, i - firstIndex[prefixSum]);
        } else {
            firstIndex[prefixSum] = i;
        }
    }

    return answer;
}
```

Complexity:

```text
Time  = O(N)
Space = O(N)
```

---

# 18. Difference Arrays — Core Idea

The lecture defines a difference array as a technique for:

```text
many range updates
+
we only need the final array after all updates
```

Typical operation:

```text
For query (L, R, X):

add X to every A[i]
where L <= i <= R
```

Difference array goal:

```text
process each range update in O(1)
```

Then after all queries:

```text
one O(N) prefix pass
```

reconstructs the final changes.

---

# 19. Why Normal Range Updates Are Slow

Suppose:

```text
N = 100000
Q = 100000
```

For each query:

```text
(L, R, X)
```

If we directly do:

```cpp
for (int i = l; i <= r; i++) {
    a[i] += x;
}
```

one update can cost:

```text
O(N)
```

Total:

```text
O(N * Q)
```

Worst case:

```text
10^5 * 10^5
=
10^10
```

Too slow.

We want:

```text
each update -> O(1)
```

---

# 20. Difference Array Meaning

The lecture's visual intuition is:

```text
diff[i]
=
how much the value changes
when we enter index i
```

Think of it as **change markers**.

Example:

```text
diff = [0, +3, 0, 0, 0, -3, 0]
```

Prefix sum of `diff`:

```text
0, 3, 3, 3, 3, 0, 0
```

So:

```text
+3 begins at index 1
-3 cancels it after index 4
```

ASCII:

```text
index:  0   1   2   3   4   5   6

diff:   0  +3   0   0   0  -3   0
            |               |
            start +3        stop +3

prefix:
        0   3   3   3   3   0   0
```

---

# 21. Range Update Formula

For one update:

```text
add X to every index from L to R
```

do:

```text
diff[L] += X
diff[R + 1] -= X
```

if `R + 1` is inside the allocated range.

That is the entire update.

So:

```text
O(1)
```

per query.

---

# 22. Why the Formula Works

Suppose:

```text
L = 2
R = 5
X = 4
```

We want:

```text
index:  0  1  2  3  4  5  6
change: 0  0  4  4  4  4  0
```

Instead of writing `+4` four times, place two markers:

```text
diff[2] += 4
diff[6] -= 4
```

So:

```text
index:  0   1   2   3   4   5   6
diff:   0   0  +4   0   0   0  -4
```

Take prefix sum:

```text
index 0:
0

index 1:
0

index 2:
0 + 4 = 4

index 3:
4 + 0 = 4

index 4:
4 + 0 = 4

index 5:
4 + 0 = 4

index 6:
4 - 4 = 0
```

Result:

```text
0 0 4 4 4 4 0
```

Exactly what we wanted.

---

## Mental model

```text
diff[L] += X
```

means:

```text
"Start adding X from here."
```

And:

```text
diff[R + 1] -= X
```

means:

```text
"Stop adding X after R."
```

---

# 23. Difference Array — Full Dry Run

Start with all zeros:

```text
N = 8

A:
index:  0  1  2  3  4  5  6  7
        0  0  0  0  0  0  0  0
```

Queries:

```text
1) L=1, R=4, X=3
2) L=2, R=5, X=5
```

---

## Query 1

```text
L = 1
R = 4
X = 3
```

Apply:

```text
diff[1] += 3
diff[5] -= 3
```

Now:

```text
index:  0   1   2   3   4   5   6   7
diff:   0  +3   0   0   0  -3   0   0
```

---

## Query 2

```text
L = 2
R = 5
X = 5
```

Apply:

```text
diff[2] += 5
diff[6] -= 5
```

Now:

```text
index:  0   1   2   3   4   5   6   7
diff:   0  +3  +5   0   0  -3  -5   0
```

---

## Prefix sum of diff

Start:

```text
running = 0
```

Index 0:

```text
running = 0
```

Index 1:

```text
running = 0 + 3 = 3
```

Index 2:

```text
running = 3 + 5 = 8
```

Index 3:

```text
running = 8
```

Index 4:

```text
running = 8
```

Index 5:

```text
running = 8 - 3 = 5
```

Index 6:

```text
running = 5 - 5 = 0
```

Index 7:

```text
running = 0
```

Final added values:

```text
index:  0  1  2  3  4  5  6  7
add:    0  3  8  8  8  5  0  0
```

Check manually:

```text
query 1 adds 3 to 1..4
query 2 adds 5 to 2..5

index 1 -> 3
index 2 -> 3+5 = 8
index 3 -> 8
index 4 -> 8
index 5 -> 5
```

Correct.

---

# 24. Reconstruct the Final Array

The lecture example starts from all zeros, but we may also have an existing array.

Suppose:

```text
A = [10, 20, 30, 40, 50]
```

After all updates, `diff[i]` represents:

```text
total amount that should be added at index i
```

after converting it to prefix form.

So:

```cpp
for (int i = 1; i < n; i++) {
    diff[i] += diff[i - 1];
}
```

Then:

```cpp
for (int i = 0; i < n; i++) {
    a[i] += diff[i];
}
```

---

# 25. Difference Array — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> a(n);

    for (int i = 0; i < n; i++) {
        cin >> a[i];
    }

    vector<long long> diff(n + 1, 0);

    int q;
    cin >> q;

    while (q--) {
        int l, r;
        long long x;

        cin >> l >> r >> x;

        diff[l] += x;

        if (r + 1 < n) {
            diff[r + 1] -= x;
        }
    }

    for (int i = 1; i < n; i++) {
        diff[i] += diff[i - 1];
    }

    for (int i = 0; i < n; i++) {
        a[i] += diff[i];
    }

    for (int i = 0; i < n; i++) {
        cout << a[i] << ' ';
    }

    cout << '\n';

    return 0;
}
```

---

## Code flow

```text
read array
    |
    v
create diff[]
    |
    v
for each query:
    diff[L] += X
    diff[R+1] -= X
    |
    v
prefix sum diff[]
    |
    v
add diff[i] to A[i]
    |
    v
final array
```

---

# 26. Can We Use O(1) Extra Space?

The lecture asks:

```text
Can difference array be done in O(1) extra space?
```

The lecture's answer is essentially:

```text
Yes, if modifying the original vector is allowed.
```

Instead of storing a separate `diff` array, the original storage can be reused to hold the change markers.

The exact implementation depends on whether:

```text
the original array values are still needed
```

and:

```text
whether modification is allowed
```

For learning and contest safety, a separate `diff` array is usually easier to reason about.

---

# 27. Greg and Array — Pattern Recognition

The lecture ends by pointing to:

```text
Greg and Array
```

as a problem related to difference-array thinking.

The important recognition idea is:

```text
there can be many range operations
and operations themselves may be applied multiple times
```

So instead of executing every operation repeatedly:

```text
count how many times each operation is applied
```

and then:

```text
apply their total effect efficiently
```

This is a classic situation where:

```text
difference arrays can be layered
```

Conceptually:

```text
queries about operations
        |
        v
difference array over operation indices
        |
        v
frequency of each operation
        |
        v
difference array over the main array
        |
        v
final values
```

The uploaded lecture only references the problem; it does not provide a full worked solution on the final slide.

---

# 28. Common Mistakes

## Mistake 1 — Wrong weighted coefficient

For Problem 1:

```text
weight at index i
=
i - L + 1
```

not simply:

```text
i
```

That is why we need the correction:

```text
-(L - 1) * sum(A[i])
```

---

## Mistake 2 — Only building weighted prefix

You need both:

```text
prefixA
```

and:

```text
prefixWeighted
```

because the formula contains:

```text
Σ i*A[i]
```

and:

```text
(L-1) * Σ A[i]
```

---

## Mistake 3 — In Contiguous Array, storing latest occurrence

For maximum length, store:

```text
first occurrence
```

of each prefix sum.

Do not overwrite it later.

---

## Mistake 4 — Forgetting:

```cpp
firstIndex[0] = -1;
```

Without this, a balanced subarray beginning at index `0` may be missed.

---

## Mistake 5 — Difference array boundary

For:

```text
diff[R + 1] -= X
```

make sure:

```text
R + 1
```

is inside the array.

---

## Mistake 6 — Printing diff directly

`diff` is not the final added value yet.

You must first compute:

```text
prefix sum of diff
```

---

## Mistake 7 — Using `int` for large accumulated values

Range updates may accumulate.

Prefer:

```cpp
long long
```

when values or query counts can make totals large.

---

# 29. Prefix Sum vs Difference Array

## Prefix Sum

Use when:

```text
array is mostly static
+
many range queries
```

Think:

```text
"Tell me the sum/value of this range."
```

Pattern:

```text
preprocess array
then query quickly
```

---

## Difference Array

Use when:

```text
many range updates
+
final array needed later
```

Think:

```text
"Add X to this entire range."
```

Pattern:

```text
record update boundaries quickly
then reconstruct once
```

---

## Visual comparison

```text
PREFIX SUM
==========

array
  |
  v
precompute cumulative values
  |
  v
many fast range queries


DIFFERENCE ARRAY
================

many range updates
  |
  v
store only boundary changes
  |
  v
prefix once at end
  |
  v
final array
```

---

# 30. Complexity Cheat Sheet

```text
Normal range sum query:
one query                     O(N)

1D prefix sum:
build                         O(N)
one query                     O(1)
Q queries                     O(Q)
total                         O(N + Q)


Weighted prefix query:
build 2 prefix arrays         O(N)
one query                     O(1)
Q queries                     O(Q)
total                         O(N + Q)


Contiguous Array:
single pass + hash map        O(N)
space                         O(N)


Normal range updates:
one update                    O(N)
Q updates                     O(N * Q)


Difference array:
each update                   O(1)
Q updates                     O(Q)
final reconstruction          O(N)
total                         O(N + Q)
space                         O(N)
```

---

# 31. Recognition Checklist

## Weighted prefix query

If you see:

```text
A[L]
+ 2*A[L+1]
+ 3*A[L+2]
+ ...
```

ask:

```text
Can the coefficient be written using i and L?
```

Usually:

```text
i - L + 1
```

Then split algebraically.

---

## Prefix sum + map

If a problem asks:

```text
longest subarray
with some balancing condition
```

ask:

```text
Can I transform values so the wanted subarray sum becomes 0?
```

Then:

```text
same prefix sum at two positions
```

may solve it.

---

## Difference array

If you see:

```text
many queries:
add X to [L,R]
```

and only the final array is required:

```text
difference array
```

should immediately come to mind.

---

# 32. Final Mental Model

```text
PROBLEM 1
=========

weighted range query
        |
        v
weight = i - L + 1
        |
        v
Σ i*A[i] - (L-1)ΣA[i]
        |
        v
two prefix arrays
        |
        v
O(1) per query
```

```text
PROBLEM 2
=========

equal number of 0 and 1
        |
        v
0 -> -1
1 -> +1
        |
        v
wanted subarray sum = 0
        |
        v
same prefix sum repeats
        |
        v
use first occurrence map
```

```text
DIFFERENCE ARRAY
================

update [L,R] by +X
        |
        v
diff[L] += X
diff[R+1] -= X
        |
        v
repeat for every query
        |
        v
prefix sum diff
        |
        v
final array
```

The most important distinction is:

```text
Prefix Sum:
    many RANGE QUERIES

Difference Array:
    many RANGE UPDATES
```
