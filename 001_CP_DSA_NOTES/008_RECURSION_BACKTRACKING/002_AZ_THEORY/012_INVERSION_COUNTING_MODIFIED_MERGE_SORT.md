# Inversion Counting — Modified Merge Sort
## Count Pairs While Merging
### TLE-Style Self-Study Note with Prerequisites, Recursion Trees, Merge Diagrams, Dry Run, Proof, and C++17

> **Core idea**
>
> An inversion is a pair:
>
> ```text
> i < j
> and
> a[i] > a[j]
> ```
>
> Brute force checks every pair:
>
> ```text
> O(n²)
> ```
>
> Modified Merge Sort counts many inversion pairs together:
>
> ```text
> O(n log n)
> ```
>
> The key observation is:
>
> ```text
> If left[i] > right[j],
> then every remaining element
> left[i], left[i+1], ..., left[mid]
> is also > right[j].
> ```
>
> So one comparison can count an entire suffix of inversions.

---

# Table of Contents

1. [What Is an Inversion?](#1-what-is-an-inversion)
2. [Prerequisite 1 — Pair Counting](#2-prerequisite-1--pair-counting)
3. [Prerequisite 2 — Why Brute Force Is O(n²)](#3-prerequisite-2--why-brute-force-is-on²)
4. [Prerequisite 3 — Merge Sort Recap](#4-prerequisite-3--merge-sort-recap)
5. [Prerequisite 4 — Recursive Contract](#5-prerequisite-4--recursive-contract)
6. [Divide Inversions into Three Groups](#6-divide-inversions-into-three-groups)
7. [Why Sorting the Halves Is Safe](#7-why-sorting-the-halves-is-safe)
8. [The Merge Observation](#8-the-merge-observation)
9. [Case 1 — Left Value ≤ Right Value](#9-case-1--left-value--right-value)
10. [Case 2 — Left Value > Right Value](#10-case-2--left-value--right-value)
11. [Why We Add `mid - i + 1`](#11-why-we-add-mid---i--1)
12. [Equality and Duplicate Values](#12-equality-and-duplicate-values)
13. [Modified Merge Sort Recursion Tree](#13-modified-merge-sort-recursion-tree)
14. [Complete Algorithm](#14-complete-algorithm)
15. [Complete C++17 Implementation](#15-complete-c17-implementation)
16. [Full Dry Run — `[3,1,5,2,6,3]`](#16-full-dry-run--315263)
17. [Merge Dry Run in Pointer Form](#17-merge-dry-run-in-pointer-form)
18. [Why Leftover Loops Add No Inversions](#18-why-leftover-loops-add-no-inversions)
19. [Why the Algorithm Is Correct](#19-why-the-algorithm-is-correct)
20. [Time Complexity](#20-time-complexity)
21. [Space Complexity](#21-space-complexity)
22. [Maximum Possible Inversions](#22-maximum-possible-inversions)
23. [Connection to Adjacent Swaps](#23-connection-to-adjacent-swaps)
24. [Inversion Parity](#24-inversion-parity)
25. [Common Mistakes](#25-common-mistakes)
26. [Useful Test Cases](#26-useful-test-cases)
27. [Recognition Checklist](#27-recognition-checklist)
28. [Final Mental Model](#28-final-mental-model)

---

# 1. What Is an Inversion?

Given an array:

```text
a[0], a[1], ..., a[n-1]
```

an inversion is a pair of indices:

```text
(i, j)
```

such that:

```text
i < j
```

and:

```text
a[i] > a[j]
```

---

## Meaning in simple words

A larger value appears **before** a smaller value.

Example:

```text
Array = [3, 1, 5, 2, 6, 3]
```

Indices:

```text
Index:  0  1  2  3  4  5
Array:  3  1  5  2  6  3
```

Valid inversion pairs are:

```text
(0,1) -> (3,1)
(0,3) -> (3,2)
(2,3) -> (5,2)
(2,5) -> (5,3)
(4,5) -> (6,3)
```

Therefore:

```text
answer = 5
```

---

## Visual picture

```text
Index:    0    1    2    3    4    5
Value:    3    1    5    2    6    3
          |\        |\        |
          | \       | \       |
          |  \      |  \      |
          ↓   ↓     ↓   ↓     ↓
          1   2     2   3     3
```

Conceptually:

```text
3 before 1  -> inversion
3 before 2  -> inversion
5 before 2  -> inversion
5 before 3  -> inversion
6 before 3  -> inversion
```

---

## Equal values are NOT inversions

Because the condition is:

```text
a[i] > a[j]
```

not:

```text
a[i] >= a[j]
```

Example:

```text
[3, 3]
```

There is no inversion.

---

## We count index pairs, not distinct values

Example:

```text
[2, 2, 1]
```

The final `1` forms an inversion with both `2`s:

```text
(index 0, index 2)
(index 1, index 2)
```

So:

```text
answer = 2
```

---

# 2. Prerequisite 1 — Pair Counting

With `n` elements, the number of unordered index pairs is:

```text
n(n - 1) / 2
```

Why?

For every `i`, possible `j` values are:

```text
i + 1, i + 2, ..., n - 1
```

Counts:

```text
n - 1
n - 2
...
1
```

Sum:

```text
(n - 1) + (n - 2) + ... + 1
=
n(n - 1) / 2
```

---

## Example

For:

```text
n = 4
```

pairs are:

```text
(0,1)
(0,2)
(0,3)
(1,2)
(1,3)
(2,3)
```

Total:

```text
6
```

Formula:

```text
4*3/2 = 6
```

---

# 3. Prerequisite 2 — Why Brute Force Is O(n²)

The direct solution is:

```cpp
long long bruteForce(const vector<long long>& a) {
    long long answer = 0;
    int n = a.size();

    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            if (a[i] > a[j]) {
                answer++;
            }
        }
    }

    return answer;
}
```

It checks all:

```text
n(n - 1) / 2
```

pairs.

Therefore:

```text
Time = Θ(n²)
```

---

## Why brute force is still useful

Even when too slow for the final solution, brute force is useful for:

```text
understanding the definition
checking small test cases
stress testing the optimized solution
```

---

# 4. Prerequisite 3 — Merge Sort Recap

Merge Sort works by:

```text
DIVIDE
RECURSE
MERGE
```

For range:

```text
a[l..r]
```

choose:

```text
mid = l + (r - l) / 2
```

Split:

```text
left  = a[l..mid]
right = a[mid+1..r]
```

Recursively sort both halves.

Then merge them.

---

## Recursion tree for 8 elements

```text
                         [0..7]
                       /        \
                  [0..3]        [4..7]
                 /     \        /     \
             [0..1]  [2..3] [4..5]  [6..7]
              / \      / \     / \      / \
             0   1    2   3   4   5    6   7
```

Each level processes all `n` elements during merging.

Number of levels:

```text
log₂ n
```

Therefore:

```text
O(n log n)
```

---

# 5. Prerequisite 4 — Recursive Contract

A powerful way to design recursive algorithms is to define exactly what each call returns.

For inversion counting, define:

```text
countAndSort(a, temp, l, r)
```

to do two things:

```text
1. Return the number of inversions originally inside a[l..r].

2. Leave a[l..r] sorted in nondecreasing order.
```

This second result is extremely important.

The parent call needs its children to return:

```text
sorted left half
sorted right half
```

so that it can count cross inversions efficiently.

---

## Base case

If:

```text
l >= r
```

then the range has at most one element.

So:

```text
inversions = 0
```

and the range is already sorted.

Return:

```text
0
```

---

# 6. Divide Inversions into Three Groups

Suppose range:

```text
a[l..r]
```

is divided into:

```text
Left  = a[l..mid]
Right = a[mid+1..r]
```

Every inversion belongs to exactly one of three categories:

```text
1. both indices in Left
2. both indices in Right
3. first index in Left, second index in Right
```

So:

```text
Total
=
Left Inversions
+
Right Inversions
+
Cross Inversions
```

ASCII:

```text
Original range:

[l ........ mid | mid+1 ........ r]
      LEFT       |      RIGHT
                 |

Inversions:

LEFT-LEFT
RIGHT-RIGHT
LEFT-RIGHT
```

There is no fourth category:

```text
RIGHT-LEFT
```

because an inversion requires:

```text
i < j
```

and all original left-half indices are before all original right-half indices.

---

# 7. Why Sorting the Halves Is Safe

This is an important conceptual point.

Suppose recursion sorts:

```text
left half
right half
```

Does sorting destroy inversion information?

No.

Why?

Because:

```text
internal inversions
```

have already been counted by the recursive calls.

For cross inversions, we only need to know:

```text
which values from the left half
are greater than
which values from the right half
```

All original left-half indices were before all original right-half indices.

That positional relationship remains known from the split.

Therefore sorting each half is safe.

---

## Important warning

Do NOT sort the entire array first.

If you transform:

```text
[3,1,5,2,6,3]
```

into:

```text
[1,2,3,3,5,6]
```

before counting anything, the original ordering information has been lost.

The trick works because we count inversions **during** the recursive sorting process.

---

# 8. The Merge Observation

Suppose the recursive calls return two sorted halves.

Let:

```text
Left  = sorted a[l..mid]
Right = sorted a[mid+1..r]
```

Use two pointers:

```text
i = l
j = mid + 1
```

Now compare:

```text
a[i]
a[j]
```

There are only two important cases.

---

# 9. Case 1 — Left Value ≤ Right Value

Suppose:

```text
a[i] <= a[j]
```

Because the right half is sorted:

```text
a[j] <= a[j+1] <= a[j+2] <= ...
```

Therefore:

```text
a[i] <= every remaining right value
```

So `a[i]` cannot form a cross inversion with any remaining right element.

Take the left value:

```text
temp[k] = a[i]
i++
k++
```

Add:

```text
0
```

inversions.

---

## Tiny example

```text
Left  = [1, 3, 5]
Right = [2, 3, 6]

i -> 1
j -> 2
```

Since:

```text
1 <= 2
```

and:

```text
2 <= 3 <= 6
```

we know:

```text
1
```

cannot be greater than any remaining right value.

So take `1`.

---

# 10. Case 2 — Left Value > Right Value

Suppose:

```text
a[i] > a[j]
```

Because the left half is sorted:

```text
a[i] <= a[i+1] <= ... <= a[mid]
```

If:

```text
a[i] > a[j]
```

then automatically:

```text
a[i+1] > a[j]
a[i+2] > a[j]
...
a[mid] > a[j]
```

So the current right value forms an inversion with **every remaining left value**.

That is the key optimization.

---

## Example

```text
Left remaining  = [3, 5, 8]
Current right   = 2
```

Since:

```text
3 > 2
```

and the left side is sorted:

```text
3 <= 5 <= 8
```

we instantly know:

```text
3 > 2
5 > 2
8 > 2
```

So this one comparison reveals:

```text
3 inversions
```

---

# 11. Why We Add `mid - i + 1`

Suppose the remaining left range is:

```text
a[i..mid]
```

Inclusive.

How many elements are there?

General inclusive range formula:

```text
count = end - start + 1
```

So:

```text
count = mid - i + 1
```

Therefore when:

```text
a[i] > a[j]
```

we do:

```cpp
cross += mid - i + 1;
```

---

## Tiny dry run

Suppose:

```text
i   = 4
mid = 7
```

remaining indices:

```text
4,5,6,7
```

Count:

```text
7 - 4 + 1
=
4
```

Correct.

---

# 12. Equality and Duplicate Values

This is one of the most important correctness details.

An inversion requires:

```text
a[i] > a[j]
```

So equal values must NOT be counted.

Correct merge condition:

```cpp
if (a[i] <= a[j]) {
    take left
} else {
    count inversions
}
```

---

## Why `<=` matters

Example:

```text
Left  = [3]
Right = [3]
```

Since:

```text
3 == 3
```

there is no inversion.

So the algorithm must enter the left branch:

```text
3 <= 3
```

and add:

```text
0
```

---

## Wrong form

If you used:

```cpp
if (a[i] < a[j]) {
    ...
} else {
    count inversion
}
```

then equality would enter `else`.

That would incorrectly count:

```text
3 and 3
```

as an inversion.

---

# 13. Modified Merge Sort Recursion Tree

For:

```text
[3,1,5,2,6,3]
```

the recursive structure is approximately:

```text
                         [3,1,5,2,6,3]
                         /           \
                    [3,1,5]        [2,6,3]
                    /    \          /    \
                 [3,1]   [5]     [2,6]   [3]
                 /  \             /  \
               [3]  [1]         [2]  [6]
```

Each leaf has:

```text
0 inversions
```

Now merge upward.

---

## Left subtree

```text
[3] + [1]
```

Since:

```text
3 > 1
```

count:

```text
1
```

and sorted result:

```text
[1,3]
```

Now merge:

```text
[1,3] + [5]
```

No new inversions.

So:

```text
left subtree count = 1
sorted left = [1,3,5]
```

---

## Right subtree

```text
[2] + [6]
```

No inversion.

Sorted:

```text
[2,6]
```

Merge:

```text
[2,6] + [3]
```

When comparing:

```text
6 > 3
```

count:

```text
1
```

Sorted:

```text
[2,3,6]
```

So:

```text
right subtree count = 1
```

---

## Root merge

Merge:

```text
[1,3,5]
```

with:

```text
[2,3,6]
```

Cross inversions:

```text
(3,2)
(5,2)
(5,3)
```

So:

```text
cross = 3
```

Total:

```text
1 + 1 + 3 = 5
```

---

# 14. Complete Algorithm

For every recursive range:

```text
countAndSort(l, r)
```

do:

```text
1. If l >= r:
       return 0

2. mid = split point

3. leftCount =
       countAndSort(l, mid)

4. rightCount =
       countAndSort(mid+1, r)

5. crossCount =
       mergeAndCount(l, mid, r)

6. return
       leftCount
       + rightCount
       + crossCount
```

---

## Mental model

```text
RECURSION handles:

left-only inversions
right-only inversions

MERGE handles:

cross inversions
```

---

# 15. Complete C++17 Implementation

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll mergeAndCount(vector<ll>& a, vector<ll>& temp, int left, int mid, int right) {
    int i = left;
    int j = mid + 1;
    int k = left;

    ll crossInversions = 0;

    while (i <= mid && j <= right) {
        if (a[i] <= a[j]) {
            temp[k++] = a[i++];
        } else {
            crossInversions += mid - i + 1;
            temp[k++] = a[j++];
        }
    }

    while (i <= mid) {
        temp[k++] = a[i++];
    }

    while (j <= right) {
        temp[k++] = a[j++];
    }

    for (int pos = left; pos <= right; pos++) {
        a[pos] = temp[pos];
    }

    return crossInversions;
}

ll countAndSort(vector<ll>& a, vector<ll>& temp, int left, int right) {
    if (left >= right) {
        return 0;
    }

    int mid = left + (right - left) / 2;

    ll answer = 0;

    answer += countAndSort(a, temp, left, mid);
    answer += countAndSort(a, temp, mid + 1, right);
    answer += mergeAndCount(a, temp, left, mid, right);

    return answer;
}

ll countInversions(vector<ll>& a) {
    int n = a.size();

    if (n < 2) {
        return 0;
    }

    vector<ll> temp(n);

    return countAndSort(a, temp, 0, n - 1);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<ll> a(n);

    for (ll& value : a) {
        cin >> value;
    }

    cout << countInversions(a) << '\n';

    return 0;
}
```

---

# 16. Full Dry Run — `[3,1,5,2,6,3]`

Input:

```text
n = 6

a = [3,1,5,2,6,3]
```

Expected:

```text
5
```

---

## Step 1 — Root split

```text
[3,1,5,2,6,3]

        split

[3,1,5]   [2,6,3]
```

---

## Step 2 — Process left half `[3,1,5]`

Split:

```text
[3,1]   [5]
```

Process:

```text
[3,1]
```

Split:

```text
[3]   [1]
```

Compare:

```text
3 > 1
```

So:

```text
cross += 1
```

Merged:

```text
[1,3]
```

Count so far in left branch:

```text
1
```

Now merge:

```text
[1,3]
```

with:

```text
[5]
```

Comparisons:

```text
1 <= 5 -> take 1
3 <= 5 -> take 3
copy 5
```

No new inversion.

So:

```text
left inversion count = 1
sorted left half = [1,3,5]
```

---

## Step 3 — Process right half `[2,6,3]`

Split:

```text
[2,6]   [3]
```

Process:

```text
[2,6]
```

Split:

```text
[2]   [6]
```

Compare:

```text
2 <= 6
```

No inversion.

Merged:

```text
[2,6]
```

Now merge:

```text
[2,6]
```

with:

```text
[3]
```

Comparisons:

```text
2 <= 3
```

take `2`.

Now:

```text
6 > 3
```

Remaining left:

```text
[6]
```

Count:

```text
1
```

Merged:

```text
[2,3,6]
```

Therefore:

```text
right inversion count = 1
sorted right half = [2,3,6]
```

---

## Step 4 — Root merge

Now we have:

```text
Left  = [1,3,5]
Right = [2,3,6]
```

Counts already known:

```text
leftCount  = 1
rightCount = 1
```

Need:

```text
crossCount
```

---

# 17. Merge Dry Run in Pointer Form

Start:

```text
Left  = [1,3,5]
          ^
          i

Right = [2,3,6]
          ^
          j

cross = 0
```

---

## Iteration 1

```text
1 <= 2
```

Take `1`.

```text
Merged = [1]

i moves
j stays

cross = 0
```

State:

```text
Left  = [1,3,5]
            ^
            i

Right = [2,3,6]
          ^
          j
```

---

## Iteration 2

Compare:

```text
3 > 2
```

Because left is sorted:

```text
remaining left = [3,5]
```

Both:

```text
3 > 2
5 > 2
```

So add:

```text
2
```

Formula:

```text
mid - i + 1 = 2
```

Take right `2`.

```text
Merged = [1,2]

cross = 2
```

Pairs counted:

```text
(3,2)
(5,2)
```

---

## Iteration 3

Compare:

```text
3 <= 3
```

Equality is not inversion.

Take left `3`.

```text
Merged = [1,2,3]

cross = 2
```

---

## Iteration 4

Compare:

```text
5 > 3
```

Remaining left:

```text
[5]
```

Count:

```text
1
```

Take right `3`.

```text
Merged = [1,2,3,3]

cross = 3
```

New pair:

```text
(5,3)
```

---

## Iteration 5

Compare:

```text
5 <= 6
```

Take `5`.

```text
Merged = [1,2,3,3,5]
```

No new inversion.

---

## Left side exhausted

Copy remaining:

```text
6
```

Final merged array:

```text
[1,2,3,3,5,6]
```

Cross count:

```text
3
```

---

## Final answer

```text
Total
=
leftCount
+
rightCount
+
crossCount

=
1 + 1 + 3

=
5
```

Answer:

```text
5
```

---

# 18. Why Leftover Loops Add No Inversions

After the main merge loop, one half may still contain elements.

---

## Case 1 — Right half exhausted

Suppose:

```text
Right has no elements left.
```

All right elements were already processed.

Whenever a right value was smaller than remaining left values, its inversion contribution was counted immediately.

So copying remaining left values creates no new uncounted cross pairs.

---

## Case 2 — Left half exhausted

Suppose:

```text
Left has no elements left.
```

Then there is no remaining left element available to form:

```text
left > right
```

pairs.

So copy the remaining right values.

No additional inversions.

---

# 19. Why the Algorithm Is Correct

We use the recursive contract:

```text
countAndSort(l, r)

1. returns exactly the number of inversions
   originally inside a[l..r]

2. leaves a[l..r] sorted
```

---

## Base case

If:

```text
l >= r
```

the range contains zero or one element.

There are no pairs.

Therefore:

```text
inversions = 0
```

and the range is already sorted.

Correct.

---

## Recursive step

Suppose both recursive calls are correct.

They return:

```text
leftCount
rightCount
```

and leave both halves sorted.

Every inversion in the parent is exactly one of:

```text
left-only
right-only
cross
```

The recursive calls count the first two groups.

The merge counts the cross group.

---

## Why merge counts all cross inversions

If:

```text
left[i] <= right[j]
```

then:

```text
left[i]
```

is not greater than `right[j]` or any later right value.

So no inversion involving this left value is missed.

If:

```text
left[i] > right[j]
```

then sortedness implies:

```text
left[i]
left[i+1]
...
left[mid]
```

are all greater than `right[j]`.

So all:

```text
mid - i + 1
```

cross inversions involving this right value are counted together.

---

## Why nothing is counted twice

Each cross inversion belongs to a unique merge level.

Think about any original pair:

```text
(i, j)
```

There is exactly one recursion node where the two positions first fall into different children.

At that node:

```text
i lies in left half
j lies in right half
```

If it is an inversion, it is counted there as a cross inversion.

It is not counted again at ancestors.

---

# 20. Time Complexity

The recurrence is:

```text
T(n)
=
2T(n/2)
+
Θ(n)
```

Why?

Two recursive calls:

```text
left half
right half
```

and a linear merge.

By the Master Theorem:

```text
T(n) = Θ(n log n)
```

---

## Level-wise intuition

```text
Level 0:
n work

Level 1:
n/2 + n/2 = n work

Level 2:
n/4 + n/4 + n/4 + n/4 = n work

...

Number of levels = log n
```

Therefore:

```text
n * log n
```

---

# 21. Space Complexity

We use:

```text
temporary array of size n
```

So:

```text
O(n)
```

space.

Recursion depth:

```text
O(log n)
```

So call stack:

```text
O(log n)
```

Total auxiliary space is dominated by:

```text
O(n)
```

---

# 22. Maximum Possible Inversions

Maximum inversions occur in a strictly decreasing array.

Example:

```text
[5,4,3,2,1]
```

Every pair is inverted.

Number of pairs:

```text
n(n - 1) / 2
```

So:

```text
Maximum inversions
=
n(n - 1)/2
```

---

## Why `long long` is required

For:

```text
n = 100000
```

maximum inversions:

```text
100000 * 99999 / 2
=
4,999,950,000
```

This exceeds signed 32-bit `int`.

So use:

```cpp
long long
```

for inversion counts.

---

# 23. Connection to Adjacent Swaps

An inversion measures disorder.

Suppose two adjacent elements are:

```text
x > y
```

Swapping them removes exactly one inversion:

```text
(x,y)
```

An adjacent swap changes inversion count by exactly:

```text
1
```

in magnitude when the values are unequal.

---

## Consequence

A sorted array has:

```text
0 inversions
```

If the initial array has:

```text
I inversions
```

then Bubble Sort performs exactly:

```text
I adjacent swaps
```

because every Bubble Sort swap removes exactly one inversion.

Therefore:

```text
minimum number of adjacent swaps
needed to sort
=
number of inversions
```

---

## Important distinction

This is about:

```text
adjacent swaps
```

not arbitrary swaps.

Example:

```text
[3,2,1]
```

Inversions:

```text
3
```

But with arbitrary swaps:

```text
swap 3 and 1
```

gives:

```text
[1,2,3]
```

in one swap.

So:

```text
inversions != minimum arbitrary swaps
```

---

# 24. Inversion Parity

Parity means:

```text
even or odd
```

For arrays of **distinct elements**, any swap of two different positions changes inversion parity.

So if:

```text
I = inversion count
s = number of swaps to reach sorted order
```

then:

```text
s mod 2 = I mod 2
```

---

## Why?

For a permutation of distinct values, swapping two positions changes the inversion count by an odd number.

Therefore the parity flips after every swap.

---

## Duplicate-value warning

With duplicates, arbitrary non-adjacent swaps do not always flip inversion parity.

Example:

```text
Before:
[2,1,1]

inversions = 2
```

Swap first and last:

```text
[1,1,2]

inversions = 0
```

Change:

```text
2
```

which is even.

So do not blindly use permutation parity arguments when duplicates are allowed.

---

# 25. Common Mistakes

## Mistake 1 — Counting equality

Wrong:

```text
a[i] >= a[j]
```

Correct inversion condition:

```text
a[i] > a[j]
```

Use:

```cpp
if (a[i] <= a[j])
```

for the no-inversion merge branch.

---

## Mistake 2 — Adding only one inversion

When:

```text
left[i] > right[j]
```

do not add:

```text
1
```

unless only one left element remains.

Add:

```text
mid - i + 1
```

because every remaining left element is greater.

---

## Mistake 3 — Forgetting `+1`

Remaining inclusive range:

```text
i..mid
```

contains:

```text
mid - i + 1
```

elements.

Not:

```text
mid - i
```

---

## Mistake 4 — Returning only cross inversions

The total is:

```text
leftCount
+
rightCount
+
crossCount
```

---

## Mistake 5 — Not sorting during merge

The parent relies on:

```text
sorted left child
sorted right child
```

Without sorting the range before returning, the parent observation becomes invalid.

---

## Mistake 6 — Sorting before recursion

Do not sort the whole input before counting.

You would destroy the original ordering that defines inversions.

---

## Mistake 7 — Using `int` for answer

Worst case is:

```text
O(n²)
```

in the value of the answer.

Use:

```cpp
long long
```

---

## Mistake 8 — Reallocating merge arrays repeatedly

You can allocate:

```text
temp[n]
```

once and reuse it throughout recursion.

This is cleaner and avoids unnecessary allocations.

---

## Mistake 9 — Thinking `O(n log n)` means there are only `O(n log n)` inversion pairs

The answer itself can be:

```text
Θ(n²)
```

But the algorithm does not enumerate every inversion individually.

It counts many at once.

---

# 26. Useful Test Cases

| Array | Expected inversions | Purpose |
|---|---:|---|
| `[]` | `0` | empty |
| `[7]` | `0` | base case |
| `[1,2,3]` | `0` | already sorted |
| `[3,2,1]` | `3` | every pair inverted |
| `[2,2,2]` | `0` | equality |
| `[2,2,1]` | `2` | duplicates counted by position |
| `[-1,-3,-2]` | `2` | negative values |
| `[3,1,5,2,6,3]` | `5` | lecture example |

---

## Stress-test idea

For many small random arrays:

```text
bruteForce(array)
```

and:

```text
mergeSortCount(array)
```

must return the same answer.

This is an excellent way to validate the implementation.

---

# 27. Recognition Checklist

When a problem asks:

```text
count pairs (i,j)
with i < j
and some order relation between a[i] and a[j]
```

ask:

### 1. Can I divide the index range?

```text
left half
right half
```

### 2. Can recursion count pairs entirely inside each half?

```text
yes
```

### 3. Can I count cross pairs efficiently after sorting?

For inversions:

```text
yes
```

because sorted order turns:

```text
a[i] > b[j]
```

into a whole-suffix count.

### 4. What useful structure should recursion return?

Not only:

```text
the count
```

but also:

```text
the range in sorted order
```

### 5. What is the central counting shortcut?

```text
right[j] < left[i]
```

implies:

```text
right[j]
<
left[i], left[i+1], ..., left[mid]
```

So add:

```text
mid - i + 1
```

at once.

---

# 28. Final Mental Model

Do not memorize inversion counting as just:

```text
"merge sort with a counter"
```

Understand the reasoning chain:

```text
1. An inversion is:
      i < j
      a[i] > a[j]

2. Brute force checks every pair:
      O(n²)

3. Split the array.

4. Every inversion is:
      left-only
      right-only
      cross

5. Recursion handles:
      left-only
      right-only

6. Sorted halves make cross counting easy.

7. During merge:

      if left[i] <= right[j]:
          no inversion for left[i]

      else:
          every remaining left element
          is greater than right[j]

          add:
          mid - i + 1

8. Merge still sorts the range.

9. Parent can use that sorted result.

10. Recurrence:
      T(n) = 2T(n/2) + O(n)

11. Final complexity:
      O(n log n)
```

ASCII summary:

```text
                     ORIGINAL RANGE
                           |
                         SPLIT
                    /              \
                   /                \
             LEFT HALF          RIGHT HALF
                 |                  |
         count + sort         count + sort
                 \                  /
                  \                /
                    SORTED HALVES
                         |
                      MERGE
                         |
             +----------------------+
             |                      |
      build sorted output     count cross inversions
                                     |
                         if L[i] > R[j]
                                     |
                         add all remaining L
                                     |
                            mid - i + 1
```

The reusable Divide-and-Conquer lesson is:

```text
Let recursion return not only the answer,
but also a useful structure.

For inversion counting,
the useful structure is:

sorted subarrays.
```

That sorted structure turns one comparison into the count of many pairs.
