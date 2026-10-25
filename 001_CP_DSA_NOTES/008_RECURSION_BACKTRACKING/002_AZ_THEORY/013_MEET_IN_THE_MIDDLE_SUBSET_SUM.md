# Meet-in-the-Middle — Subset Sum Counting
## Count Subsets with Sum ≤ X
### TLE-Style Self-Study Note with Prerequisites, Bitmask Diagrams, Split Visualization, Binary Search, Full Dry Run, Proof, and C++17

> **Core problem**
>
> Given `N` numbers and a value `X`, count how many subsets have:
>
> ```text
> subsetSum <= X
> ```
>
> A direct subset enumeration needs:
>
> ```text
> 2^N
> ```
>
> possibilities.
>
> Meet-in-the-Middle reduces this to roughly:
>
> ```text
> 2^(N/2)
> ```
>
> work on each half.

---

# Table of Contents

1. [Problem We Are Solving](#1-problem-we-are-solving)
2. [Prerequisite 1 — What Is a Subset?](#2-prerequisite-1--what-is-a-subset)
3. [Prerequisite 2 — Why There Are `2^N` Subsets](#3-prerequisite-2--why-there-are-2n-subsets)
4. [Prerequisite 3 — Representing a Subset with a Bitmask](#4-prerequisite-3--representing-a-subset-with-a-bitmask)
5. [Prerequisite 4 — Generating All Subset Sums](#5-prerequisite-4--generating-all-subset-sums)
6. [Why Brute Force Stops Working](#6-why-brute-force-stops-working)
7. [The Meet-in-the-Middle Idea](#7-the-meet-in-the-middle-idea)
8. [Why Splitting Helps](#8-why-splitting-helps)
9. [Generate Subset Sums for Both Halves](#9-generate-subset-sums-for-both-halves)
10. [How Every Full Subset Is Reconstructed](#10-how-every-full-subset-is-reconstructed)
11. [Turn the Condition into a Complement Query](#11-turn-the-condition-into-a-complement-query)
12. [Prerequisite 5 — Why Sorting Helps](#12-prerequisite-5--why-sorting-helps)
13. [Prerequisite 6 — `upper_bound`](#13-prerequisite-6--upper_bound)
14. [The Final Counting Formula](#14-the-final-counting-formula)
15. [Complete Lecture-Style Algorithm](#15-complete-lecture-style-algorithm)
16. [C++17 Implementation](#16-c17-implementation)
17. [Full Dry Run — `{1,2,3}`, `X = 5`](#17-full-dry-run--123-x--5)
18. [Another Dry Run — Complement Search](#18-another-dry-run--complement-search)
19. [Why the Algorithm Is Correct](#19-why-the-algorithm-is-correct)
20. [Time Complexity](#20-time-complexity)
21. [Space Complexity](#21-space-complexity)
22. [Why Meet-in-the-Middle Helps Around `N ≈ 40`](#22-why-meet-in-the-middle-helps-around-n--40)
23. [Common Mistakes](#23-common-mistakes)
24. [Recognition Checklist](#24-recognition-checklist)
25. [Useful Variations](#25-useful-variations)
26. [Final Mental Model](#26-final-mental-model)

---

# 1. Problem We Are Solving

We are given:

```text
N numbers
X
```

We need to count the number of subsets whose total sum is:

```text
<= X
```

Example:

```text
Numbers = {1, 2, 3}
X = 5
```

All subsets:

```text
{}
{1}
{2}
{3}
{1,2}
{1,3}
{2,3}
{1,2,3}
```

Their sums:

```text
{}        -> 0
{1}       -> 1
{2}       -> 2
{3}       -> 3
{1,2}     -> 3
{1,3}     -> 4
{2,3}     -> 5
{1,2,3}   -> 6
```

Valid sums:

```text
0,1,2,3,3,4,5
```

So:

```text
answer = 7
```

---

# 2. Prerequisite 1 — What Is a Subset?

For each element, there are exactly two choices:

```text
TAKE it
or
DO NOT TAKE it
```

For:

```text
{1,2,3}
```

a subset may be:

```text
{}
{1}
{2,3}
{1,2,3}
```

The original order is irrelevant to the subset itself.

---

# 3. Prerequisite 2 — Why There Are `2^N` Subsets

Every element has:

```text
2 choices
```

For `N` independent elements:

```text
2 * 2 * 2 * ... * 2
```

`N` times.

Therefore:

```text
number of subsets = 2^N
```

## Small example

For:

```text
N = 3
```

we have:

```text
2^3 = 8
```

subsets.

Decision tree:

```text
                    start
                  /       \
               skip 1     take 1
              /    \       /    \
          skip 2 take 2 skip 2 take 2
           / \      / \    / \      / \
          0   3    2   5  1   4    3   6
```

The leaf values above are subset sums for `{1,2,3}`.

---

# 4. Prerequisite 3 — Representing a Subset with a Bitmask

A bitmask gives one bit to every element.

Example:

```text
arr = [a0, a1, a2, a3]
```

Mask:

```text
bit:     3 2 1 0
mask:    1 1 0 1
```

means:

```text
take arr[3]
take arr[2]
skip arr[1]
take arr[0]
```

Subset sum:

```text
arr[3] + arr[2] + arr[0]
```

## Check whether element `j` is chosen

Use:

```cpp
(mask >> j) & 1
```

If result is:

```text
1 -> chosen
0 -> not chosen
```

Equivalent form:

```cpp
mask & (1 << j)
```

## Tiny example

```text
arr = [10,20,30,40]
mask = 1101₂
```

Selected:

```text
bit 0 -> 1 -> take 10
bit 1 -> 0 -> skip 20
bit 2 -> 1 -> take 30
bit 3 -> 1 -> take 40
```

Sum:

```text
10 + 30 + 40 = 80
```

---

# 5. Prerequisite 4 — Generating All Subset Sums

For a vector of size `n`, masks run from:

```text
0
to
(1 << n) - 1
```

For each mask:

```text
calculate the sum of selected elements
```

## Lecture-style helper

```cpp
vector<long long> generate(const vector<long long>& arr) {
    int n = arr.size();
    vector<long long> subVals;

    for (int mask = 0; mask < (1 << n); mask++) {
        long long sum = 0;

        for (int j = 0; j < n; j++) {
            if ((mask >> j) & 1) {
                sum += arr[j];
            }
        }

        subVals.push_back(sum);
    }

    sort(subVals.begin(), subVals.end());
    return subVals;
}
```

## Dry run

```text
arr = [1,2]
```

Masks:

```text
00 -> {}
01 -> {1}
10 -> {2}
11 -> {1,2}
```

Sums:

```text
0,1,2,3
```

After sorting:

```text
[0,1,2,3]
```

---

# 6. Why Brute Force Stops Working

Directly generating all subsets of `N` elements costs:

```text
2^N
```

For each subset, the straightforward bitmask implementation scans up to `N` bits.

So:

```text
O(N * 2^N)
```

## Scale intuition

For:

```text
N = 20
```

```text
2^20 ≈ 1,048,576
```

This is often manageable.

But:

```text
N = 40
```

```text
2^40 ≈ 1.1 trillion
```

That is far too large.

So we need a different idea.

---

# 7. The Meet-in-the-Middle Idea

Instead of solving the whole `N`-element subset space at once:

```text
split the array into two halves
```

ASCII:

```text
Original array

[a0 a1 a2 a3 a4 a5 ... aN-1]

          split

Left half                  Right half
[a0 ...]                   [... aN-1]

~N/2 elements              ~N/2 elements
```

Now generate subset sums independently for both halves.

---

# 8. Why Splitting Helps

If each half has roughly:

```text
N/2
```

elements, then each half has:

```text
2^(N/2)
```

subsets.

Instead of:

```text
2^N
```

we work with two collections of size approximately:

```text
2^(N/2)
```

## Numerical intuition

For:

```text
N = 40
```

direct:

```text
2^40 ≈ 1.1 * 10^12
```

Meet-in-the-Middle:

```text
2^20 ≈ 10^6
```

on each side.

That is the key improvement.

---

# 9. Generate Subset Sums for Both Halves

Suppose:

```text
arr = [1,2,3]
```

We may split as:

```text
Left  = [1,2]
Right = [3]
```

Subset sums of Left:

```text
{}
{1}
{2}
{1,2}
```

So:

```text
A = [0,1,2,3]
```

Subset sums of Right:

```text
{}
{3}
```

So:

```text
B = [0,3]
```

## Diagram

```text
                [1,2,3]
                /     \
               /       \
          [1,2]        [3]
            |            |
        all sums       all sums
            |            |
      [0,1,2,3]        [0,3]
```

---

# 10. How Every Full Subset Is Reconstructed

Every subset of the original array is uniquely formed by:

```text
one subset from Left
+
one subset from Right
```

Therefore every full subset sum is:

```text
leftSum + rightSum
```

## Set-product view

If:

```text
A = all left subset sums
B = all right subset sums
```

then conceptually:

```text
all full subset sums
=
{ a + b | a in A, b in B }
```

For:

```text
A = [0,1,2,3]
B = [0,3]
```

conceptual pair sums:

```text
0+0 = 0
0+3 = 3

1+0 = 1
1+3 = 4

2+0 = 2
2+3 = 5

3+0 = 3
3+3 = 6
```

Exactly the 8 original subsets appear.

---

# 11. Turn the Condition into a Complement Query

We need:

```text
leftSum + rightSum <= X
```

Fix one `leftSum`.

Call it:

```text
v
```

Then:

```text
v + rightSum <= X
```

Move `v` to the other side:

```text
rightSum <= X - v
```

This is the most important transformation.

## Mental model

```text
Choose one sum from Left.

Now ask:

How many sums in Right
are <= X - leftSum ?
```

That is now a binary-search counting problem.

---

# 12. Prerequisite 5 — Why Sorting Helps

Suppose right-side subset sums are:

```text
[7,0,3,1,5,2]
```

Without sorting, counting values:

```text
<= K
```

needs scanning the whole vector.

After sorting:

```text
[0,1,2,3,5,7]
```

all valid values form one prefix.

Example:

```text
K = 3
```

```text
[0,1,2,3 | 5,7]
 ^ valid   ^ invalid
```

So the question becomes:

```text
Where does the valid prefix end?
```

Binary search answers this.

---

# 13. Prerequisite 6 — `upper_bound`

C++:

```cpp
upper_bound(v.begin(), v.end(), target)
```

returns an iterator to the first value:

```text
> target
```

Therefore:

```cpp
upper_bound(v.begin(), v.end(), target) - v.begin()
```

equals the number of values:

```text
<= target
```

## Tiny example

```text
v = [0,1,2,3,5,7]
target = 3
```

`upper_bound` points to `5`.

Index:

```text
4
```

So there are:

```text
4
```

values `<= 3`:

```text
0,1,2,3
```

## Why not `lower_bound`?

`lower_bound(target)` gives first:

```text
>= target
```

But we need to include values exactly equal to:

```text
target
```

So for condition:

```text
<= target
```

`upper_bound` is the natural choice.

---

# 14. The Final Counting Formula

Let:

```text
A = all subset sums of first half
B = all subset sums of second half
```

Sort:

```text
B
```

For each:

```text
v in A
```

we need:

```text
b <= X - v
```

Number of valid `b` values:

```cpp
upper_bound(B.begin(), B.end(), X - v) - B.begin()
```

Therefore:

```text
answer
=
sum over all v in A
of
count(B <= X - v)
```

---

# 15. Complete Lecture-Style Algorithm

```text
1. Split the original array into two halves.

2. Generate all subset sums of the first half.
   Call this list A.

3. Generate all subset sums of the second half.
   Call this list B.

4. Sort B.

5. answer = 0

6. For every value v in A:

       remaining = X - v

       count how many values in B
       are <= remaining

       add that count to answer

7. Return answer.
```

## Lecture-style split

The lecture implementation can distribute elements into two vectors using parity:

```cpp
half[i & 1].push_back(arr[i]);
```

That gives two groups with sizes differing by at most one.

A contiguous split also works:

```text
first N/2 elements
remaining elements
```

Meet-in-the-Middle only needs:

```text
two disjoint groups
whose union is the original set
```

and preferably balanced sizes.

---

# 16. C++17 Implementation

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

vector<ll> generate(const vector<ll>& arr) {
    int n = arr.size();
    vector<ll> subVals;

    for (int mask = 0; mask < (1 << n); mask++) {
        ll sum = 0;

        for (int j = 0; j < n; j++) {
            if ((mask >> j) & 1) {
                sum += arr[j];
            }
        }

        subVals.push_back(sum);
    }

    sort(subVals.begin(), subVals.end());
    return subVals;
}

ll countSubsets(const vector<ll>& arr, ll x) {
    vector<ll> half[2];

    for (int i = 0; i < (int)arr.size(); i++) {
        half[i & 1].push_back(arr[i]);
    }

    vector<ll> leftSums = generate(half[0]);
    vector<ll> rightSums = generate(half[1]);

    ll answer = 0;

    for (ll leftSum : leftSums) {
        answer += upper_bound(rightSums.begin(), rightSums.end(), x - leftSum) - rightSums.begin();
    }

    return answer;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    ll x;
    cin >> n >> x;

    vector<ll> arr(n);

    for (ll& value : arr) {
        cin >> value;
    }

    cout << countSubsets(arr, x) << '\n';
    return 0;
}
```

---

# 17. Full Dry Run — `{1,2,3}`, `X = 5`

Original:

```text
arr = [1,2,3]
X = 5
```

Use:

```text
Left  = [1,2]
Right = [3]
```

## Generate Left subset sums

```text
mask  subset    sum

00    {}        0
01    {1}       1
10    {2}       2
11    {1,2}     3
```

So:

```text
A = [0,1,2,3]
```

## Generate Right subset sums

```text
mask  subset    sum

0     {}        0
1     {3}       3
```

So:

```text
B = [0,3]
```

Already sorted.

## Count combinations

We need:

```text
a + b <= 5
```

### `a = 0`

Need:

```text
b <= 5
```

Valid:

```text
0,3
```

Count:

```text
2
```

Running answer:

```text
2
```

### `a = 1`

Need:

```text
b <= 4
```

Valid:

```text
0,3
```

Count:

```text
2
```

Running answer:

```text
4
```

### `a = 2`

Need:

```text
b <= 3
```

Valid:

```text
0,3
```

Count:

```text
2
```

Running answer:

```text
6
```

### `a = 3`

Need:

```text
b <= 2
```

Valid:

```text
0
```

Count:

```text
1
```

Running answer:

```text
7
```

Final:

```text
answer = 7
```

This matches direct enumeration.

---

# 18. Another Dry Run — Complement Search

Suppose:

```text
Left sums:
A = [0,4,7]

Right sums:
B = [0,1,3,6,8]

X = 10
```

`B` is sorted.

Iter 1: `leftSum=0` -> need `rightSum<=10` -> 5 values -> answer=5

Iter 2: `leftSum=4` -> need `rightSum<=6` -> `[0,1,3,6]` -> +4 -> answer=9

Iter 3: `leftSum=7` -> need `rightSum<=3` -> `[0,1,3]` -> +3 -> answer=12

Final:

```text
12
```

---

# 19. Why the Algorithm Is Correct

We prove two facts.

## Fact 1 — Every original subset appears exactly once

The original array is partitioned into two disjoint halves.

Any subset of the full array can be uniquely decomposed into:

```text
elements chosen from Left
+
elements chosen from Right
```

Therefore it corresponds to exactly one pair:

```text
(leftSubset, rightSubset)
```

and exactly one pair of sums:

```text
(leftSum, rightSum)
```

So no subset is missed and none is represented twice.

## Fact 2 — We count exactly the pairs with sum ≤ X

For fixed:

```text
leftSum = v
```

condition:

```text
v + rightSum <= X
```

is equivalent to:

```text
rightSum <= X - v
```

Because `rightSums` is sorted:

```cpp
upper_bound(rightSums.begin(), rightSums.end(), X - v)
```

finds the first value greater than the allowed maximum.

Therefore the distance from the beginning gives exactly the number of valid right-side subset sums.

Summing this for every left-side subset counts every valid full subset exactly once.

---

# 20. Time Complexity

Let each half contain approximately:

```text
N/2
```

elements.

Let:

```text
M = 2^(N/2)
```

## Generating one half

There are:

```text
M
```

masks.

For each mask, the straightforward implementation checks about:

```text
N/2
```

bits.

So generation costs:

```text
O(N * 2^(N/2))
```

up to a constant factor.

## Sorting

Sorting `M` values:

```text
O(M log M)
```

Since:

```text
log M = log(2^(N/2)) = N/2
```

sorting is also:

```text
O(N * 2^(N/2))
```

## Combining

For each left subset sum:

```text
2^(N/2)
```

we perform one `upper_bound`:

```text
O(log M) = O(N)
```

So combination is:

```text
O(N * 2^(N/2))
```

## Overall

```text
Time = O(N * 2^(N/2))
```

The main exponential term is:

```text
2^(N/2)
```

instead of:

```text
2^N
```

---

# 21. Space Complexity

We store all subset sums from both halves.

Each has approximately:

```text
2^(N/2)
```

values.

Therefore:

```text
Space = O(2^(N/2))
```

---

# 22. Why Meet-in-the-Middle Helps Around `N ≈ 40`

Compare:

```text
2^40
```

with:

```text
2^20
```

Direct:

```text
2^40 ≈ 1,099,511,627,776
```

Half enumeration:

```text
2^20 ≈ 1,048,576
```

So an input size around:

```text
35–45
```

combined with a subset problem is a strong signal to consider Meet-in-the-Middle.

This is a heuristic, not a universal cutoff. Always check the actual time and memory limits.

---

# 23. Common Mistakes

## Mistake 1 — Forgetting the empty subset

Mask:

```text
0
```

means:

```text
{}
```

with sum `0`.

Unless excluded by the problem, it must be included.

## Mistake 2 — Using `lower_bound` for `<=`

We need:

```text
rightSum <= limit
```

So use:

```cpp
upper_bound(...)
```

## Mistake 3 — Forgetting to sort the searched side

Binary search requires sorted input.

## Mistake 4 — Explicitly generating all `A[i] + B[j]`

That recreates:

```text
2^(N/2) * 2^(N/2) = 2^N
```

work.

The whole point is to avoid forming the Cartesian product explicitly.

## Mistake 5 — Using `int` for subset sums

Use:

```cpp
long long
```

when values or subset sums can be large.

## Mistake 6 — Extremely unbalanced split

Bad:

```text
1 element
N-1 elements
```

The large side still has:

```text
2^(N-1)
```

subsets.

Split as evenly as possible.

## Mistake 7 — Thinking the split must be contiguous

It does not.

The lecture's even/odd-index split works because the two groups are:

```text
disjoint
complete together
balanced
```

---

# 24. Recognition Checklist

Think **Meet-in-the-Middle** when:

```text
1. N is too large for 2^N.

2. 2^(N/2) is feasible.

3. A full solution splits naturally into
   a contribution from two groups.

4. The final condition combines them, e.g.

      left + right <= X
      left + right = X
      left + right >= X
      minimize |left + right - X|

5. One side can be sorted / hashed / binary-searched.
```

Strong trigger:

```text
N ≈ 40
+
subset enumeration
+
sum condition
```

Ask immediately:

```text
Can I Meet-in-the-Middle this?
```

---

# 25. Useful Variations

## Form 1 — Count subset sums `<= X`

Current form:

```text
left + right <= X
```

Use:

```text
upper_bound(X - left)
```

## Form 2 — Count subset sums exactly `X`

Need:

```text
left + right = X
```

For fixed `left`:

```text
right = X - left
```

Count occurrences using:

```text
lower_bound
upper_bound
```

## Form 3 — Maximum subset sum `<= X`

For fixed `left`:

```text
right <= X - left
```

Use `upper_bound`, move one step back, and maximize:

```text
left + right
```

## Form 4 — Minimum-difference partition

Generate subset sums and search for values near:

```text
totalSum / 2
```

---

# 26. Final Mental Model

Do not memorize Meet-in-the-Middle as only:

```text
split the array into two halves
```

Remember the full transformation:

```text
FULL PROBLEM
N elements
2^N subsets
        |
        | too large
        v
SPLIT INTO TWO BALANCED GROUPS
        |
        +--------------------+
        |                    |
        v                    v
      LEFT                 RIGHT
     N/2 elems            N/2 elems
        |                    |
        v                    v
   generate all          generate all
   subset sums           subset sums
        |                    |
   2^(N/2)              2^(N/2)
        |                    |
        +---------+----------+
                  |
                  v
        combine WITHOUT building
         the full Cartesian product
                  |
                  v
     leftSum + rightSum <= X
                  |
                  v
     rightSum <= X - leftSum
                  |
                  v
           sort right side
                  |
                  v
             upper_bound
```

The reusable idea is:

```text
Do exponential work on HALF the state dimension,
then combine the two halves intelligently.
```

For this problem:

```text
2^N
```

becomes approximately:

```text
2^(N/2)
```

with sorting and binary search layered on top.

That is the essence of **Meet-in-the-Middle**.
