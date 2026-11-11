# Prefix Sum & Difference Array — Patternwise Visual Self-Study Notes

## Table of Contents

- [1. Prerequisites](#1-prerequisites)
- [2. Master Recognition Map](#2-master-recognition-map)
- [3. How to Study Each Pattern](#3-how-to-study-each-pattern)
- [4. Pattern 1 — Basic Range Sum / Static Queries](#pattern-1-basic-range-sum-static-queries)
- [5. Pattern 2 — Prefix + Suffix / Split at i](#pattern-2-prefix-and-suffix-split-at-i)
- [6. Pattern 3 — Prefix Sum + Hash Map: Subarray Sum = K](#pattern-3-prefix-sum-and-hash-map-subarray-sum-k)
- [7. Pattern 4 — Prefix Modulo / Divisibility](#pattern-4-prefix-modulo-divisibility)
- [8. Pattern 5 — Transform Values, Then Prefix](#pattern-5-transform-values-then-prefix)
- [9. Pattern 6 — Prefix XOR / Prefix State](#pattern-6-prefix-xor-prefix-state)
- [10. Pattern 7 — Weighted Prefix Sum / Index * A\[i\]](#pattern-7-weighted-prefix-sum-index-times-a-i)
- [11. Pattern 8 — Prefix of Prefix / Double Prefix](#pattern-8-prefix-of-prefix-double-prefix)
- [12. Pattern 9 — 2D Prefix Sum](#pattern-9-2d-prefix-sum)
- [13. Pattern 10 — Basic Range Addition](#pattern-10-basic-range-addition)
- [14. Pattern 11 — Difference Array as Event / Sweep Line](#pattern-11-difference-array-as-event-sweep-line)
- [15. Pattern 12 — Difference + Prefix + Another Prefix](#pattern-12-difference-prefix-another-prefix)
- [16. Pattern 13 — Difference Array + Coordinate Compression](#pattern-13-difference-array-coordinate-compression)
- [17. Pattern 14 — Difference of a Prefix Array / Reconstruct Original](#pattern-14-difference-of-a-prefix-array-reconstruct-original)
- [18. Pattern 15 — AP Range Update (Linear Difference)](#pattern-15-ap-range-update-linear-difference)
- [19. Pattern 16 — GP / Recurrence-Based Range Update](#pattern-16-gp-recurrence-based-range-update)
- [20. Pattern 17 — Prefix Sum + Binary Search / K-th Value](#pattern-17-prefix-sum-binary-search-kth-value)
- [21. Pattern 18 — Prefix Sum + Monotonic Stack / Boundaries](#pattern-18-prefix-sum-monotonic-stack-boundaries)
- [22. Pattern 19 — Prefix on Trees / Paths](#pattern-19-prefix-on-trees-paths)
- [23. 60-Second Contest Checklist](#60-second-contest-checklist)
- [24. Suggested Mastery Order](#suggested-mastery-order)
- [25. Final Memory Sheet](#final-memory-sheet)

> **Purpose:** convert the uploaded pattern roadmap into a self-learning note.
>
> For every pattern, use the same contest-thinking loop:
>
> ```text
> PROBLEM STORY
>      ↓
> WHAT CHANGES / WHAT IS ASKED?
>      ↓
> DEFINE PREFIX / EVENT / STATE
>      ↓
> WRITE ONE EQUATION OR INVARIANT
>      ↓
> DRY RUN
>      ↓
> CODE
> ```
>
> Do not memorize code first. Try to recall:
>
> ```text
> Recognition → State Meaning → Equation → Transition → Complexity
> ```

---

# 1. Prerequisites

## 1.1 Subarray vs subsequence

A **subarray** is contiguous.

```text
A = [2, 7, 1, 5, 3]

subarray:
        [7, 1, 5]        ✓ contiguous

subsequence:
        [2,    1,    3]  ✓ order preserved, gaps allowed
```

Prefix-sum range formulas normally talk about **contiguous ranges**.

---

## 1.2 Prefix means "everything before or through here"

Using 1-indexing:

```text
A:       3   1   4   2
index:   1   2   3   4

pref: 0  3   4   8  10
      ^
      extra zero
```

Meaning:

```text
pref[i] = A[1] + A[2] + ... + A[i]
```

Core identity:

```text
sum(L,R) = pref[R] - pref[L-1]
```

Visual:

```text
pref[R]   = [ unwanted ][      wanted      ]
pref[L-1] = [ unwanted ]

subtract
              ↓
             wanted
```

---

## 1.3 Difference array means "change starts/stops here"

To add `X` on `[L,R]`:

```text
diff[L]   += X
diff[R+1] -= X
```

Then prefix `diff`.

```text
             L                 R
             |<--- +X active -->|

diff:       +X                 -X
              \________________/
                  prefix
```

Think:

```text
PREFIX     = accumulated VALUE
DIFFERENCE = accumulated CHANGE
```

They are inverse views.

---

## 1.4 Hash map as memory of old prefix states

Some problems ask:

```text
Does an earlier cumulative state exist?
How many times did it exist?
What was its earliest index?
```

Use a map depending on the question:

```text
counting        -> frequency
longest length  -> earliest index
shortest length -> latest/use problem-specific logic
existence       -> set/map
```

---

## 1.5 Modulo normalization

In C++ negative modulo may stay negative.

Use:

```cpp
long long rem = ((value % k) + k) % k;
```

---

## 1.6 XOR cancellation

Remember:

```text
x ^ x = 0
x ^ 0 = x
```

So:

```text
range XOR = prefixXor[R] ^ prefixXor[L-1]
```

---

## 1.7 Sort + unique + lower_bound

Used heavily in coordinate compression.

```cpp
sort(v.begin(), v.end());
v.erase(unique(v.begin(), v.end()), v.end());

int id = lower_bound(v.begin(), v.end(), x) - v.begin();
```

Mental model:

```text
huge value -> rank among sorted unique values
```

---

## 1.8 Binary search needs monotonicity

Typical searchable condition:

```text
pref[i] >= target
```

works only when the prefix array is monotone, e.g. all frequencies/counts are nonnegative.

---

## 1.9 Monotonic stack prerequisites

You should be comfortable finding:

```text
previous smaller
next smaller
previous greater
next greater
```

The stack normally finds **boundaries**.

Prefix sums then evaluate something inside those boundaries.

---

## 1.10 Tree prerequisites

For tree-prefix patterns, know:

```text
DFS
parent/child
root-to-node path
Euler tour idea
LCA idea
backtracking state
```

---

## 1.11 Numeric limits

Prefer `long long` when sums, products, coordinates, costs, or number of subarrays can be large.

```text
int        ≈ up to 2.1e9
long long  ≈ up to 9e18
```

No `__int128` is needed for the standard examples in this note.

---

# 2. Master Recognition Map

| What the statement says | First pattern to test |
|---|---|
| Many static `[L,R]` sums/counts | Pattern 1 |
| Split/remove at one index | Pattern 2 |
| Subarray sum exactly `K` | Pattern 3 |
| Divisible / remainder condition | Pattern 4 |
| Equal categories / awkward condition | Pattern 5 |
| XOR / parity / toggles | Pattern 6 |
| Index-weighted contribution | Pattern 7 |
| Sum of prefix sums | Pattern 8 |
| Rectangle/submatrix queries | Pattern 9 |
| Many constant range additions | Pattern 10 |
| Starts/ends/overlaps on a line | Pattern 11 |
| Update → materialize → query again | Pattern 12 |
| Huge sparse coordinates | Pattern 13 |
| Given cumulative values, recover local | Pattern 14 |
| AP range update | Pattern 15 |
| GP/Fibonacci/recurrence update | Pattern 16 |
| Find k-th / first cumulative threshold | Pattern 17 |
| Element controls subarrays until boundary | Pattern 18 |
| Prefix-like logic on tree paths | Pattern 19 |

---

# 3. How to Study Each Pattern

For each pattern below, do this before reading the code:

```text
1. Read Recognition Signal.
2. Say the state meaning aloud.
3. Re-derive the equation.
4. Draw the ASCII picture yourself.
5. Dry-run one tiny example.
6. Only then read/write the C++ template.
7. Solve 3–5 direct problems from the preserved practice set.
```

A pattern is **not mastered** when you can copy its template.

It is mastered when this happens:

```text
new problem
    ↓
recognize family
    ↓
define state yourself
    ↓
derive formula
    ↓
code without notes
```

---


# Pattern 1 Basic Range Sum Static Queries

## Prerequisites for this pattern

Array indexing, subarray `[L,R]`, addition, 1-indexed prefix convention.

## Recognition signal

Many static range queries ask for sum/count/cost on `[L,R]`.

## State meaning

`pref[i]` = total contribution from index `1` through `i`.

## Step-by-step derivation

1. Put an extra zero before the array: `pref[0] = 0`.
2. Build `pref[i] = pref[i-1] + A[i]`.
3. For `[L,R]`, take everything through `R`.
4. Remove everything before `L`.
5. Answer `pref[R] - pref[L-1]`.

## ASCII memory diagram

```text
A:       [3] [1] [4] [2] [5]
index:    1   2   3   4   5

pref:  0--3---4---8--10--15
             |<------>|
             L        R

sum(2,4)
= pref[4] - pref[1]
= 10 - 3
= 7
```

## C++17 template

```cpp
vector<long long> pref(n + 1, 0);

for (int i = 1; i <= n; i++) {
    pref[i] = pref[i - 1] + a[i];
}

auto rangeSum = [&](int L, int R) -> long long {
    return pref[R] - pref[L - 1];
};
```

## Common mistake

> Forgetting `pref[0]=0`, or mixing 0-indexed and 1-indexed formulas.

## Practice from the original roadmap

1. [Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/)
2. [K Radius Subarray Averages](https://leetcode.com/problems/k-radius-subarray-averages/)
3. [Kuriyama Mirai's Stones](https://codeforces.com/problemset/problem/433/B)
4. [Static Range Sum Queries](https://cses.fi/problemset/task/1646)

---


# Pattern 2 Prefix and Suffix Split at i

## Prerequisites for this pattern

Pattern 1, total sum, ability to express left/right parts algebraically.

## Recognition signal

Choose/split/remove at index `i`; compare everything left of `i` with everything right of `i`.

## State meaning

Prefix gives the left side; `total - prefix` gives the right side.

## Step-by-step derivation

1. Freeze one candidate index `i`.
2. Define exactly whether `A[i]` belongs to the left, right, or neither.
3. Compute the left side from prefix.
4. Compute the right side from total minus prefix.
5. Evaluate the split in O(1).
6. Move `i` and repeat.

## ASCII memory diagram

```text
A = [2][5] | [1] | [4][2]
             i

LEFT  = 2 + 5 = 7
RIGHT = 4 + 2 = 6

prefix before i ------^
total - prefix[i] ---------------->
```

## C++17 template

```cpp
vector<long long> pref(n + 1, 0);

for (int i = 1; i <= n; i++) {
    pref[i] = pref[i - 1] + a[i];
}

long long total = pref[n];

for (int i = 1; i <= n; i++) {
    long long left = pref[i - 1];
    long long right = total - pref[i];

    // evaluate split at i
}
```

## Common mistake

> Not defining whether the pivot element belongs to either side.

## Practice from the original roadmap

1. [Find Pivot Index](https://leetcode.com/problems/find-pivot-index/)
2. [Minimum Average Difference](https://leetcode.com/problems/minimum-average-difference/)
3. [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/)
4. [Number of Ways to Split Array](https://leetcode.com/problems/number-of-ways-to-split-array/)
5. [Array Division](https://codeforces.com/problemset/problem/808/D)

---


# Pattern 3 Prefix Sum and Hash Map Subarray Sum K

## Prerequisites for this pattern

Pattern 1, hash map/frequency map, algebra with prefix differences.

## Recognition signal

Count/find subarrays with sum exactly `K`; negatives may exist, so sliding window is unsafe.

## State meaning

At current prefix `P`, we need an earlier prefix equal to `P-K`.

## Step-by-step derivation

1. Start from `sum(L..R) = K`.
2. Replace range sum by prefixes: `pref[R] - pref[L-1] = K`.
3. Rearrange: `pref[L-1] = pref[R] - K`.
4. While scanning `R`, compute current prefix.
5. Ask how many previous prefixes equal `current-K`.
6. Add that frequency to the answer.
7. Insert the current prefix for future positions.

## ASCII memory diagram

```text
old prefix                        current prefix
    P -------------------------------- P + K
      |<--------- subarray K -------->|

current - old = K

old = current - K

So at every R:
"Have I seen currentPrefix - K?"
```

## C++17 template

```cpp
unordered_map<long long, long long> freq;
freq[0] = 1;

long long pref = 0;
long long ans = 0;

for (long long x : a) {
    pref += x;
    ans += freq[pref - K];
    freq[pref]++;
}
```

## Common mistake

> Updating `freq[pref]` before querying can count an empty/current-position interval incorrectly.

## Practice from the original roadmap

1. [Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/)
2. [Binary Subarrays With Sum](https://leetcode.com/problems/binary-subarrays-with-sum/)
3. [Maximum Size Subarray Sum Equals k](https://leetcode.com/problems/maximum-size-subarray-sum-equals-k/)
4. [Subarray Sums II](https://cses.fi/problemset/task/1661)
5. [Good Subarrays](https://codeforces.com/problemset/problem/1398/C)

---


# Pattern 4 Prefix Modulo Divisibility

## Prerequisites for this pattern

Prefix sums, modulo arithmetic, normalized modulo for negative values.

## Recognition signal

Subarray sum must be divisible by `K`, or only the prefix remainder matters.

## State meaning

Two prefixes with the same remainder differ by a multiple of `K`.

## Step-by-step derivation

1. Write `(pref[R] - pref[L-1]) % K = 0`.
2. Rearrange to `pref[R] % K = pref[L-1] % K`.
3. Scan prefixes.
4. Normalize each remainder.
5. Count how many previous prefixes have the same remainder.
6. Add that count to the answer.

## ASCII memory diagram

```text
Modulo-K states:

prefix A -----> remainder r
                  |
                  | same clock position
                  |
prefix B -----> remainder r

difference between A and B
= whole multiples of K

=> subarray sum divisible by K
```

## C++17 template

```cpp
vector<long long> cnt(k, 0);
cnt[0] = 1;

long long pref = 0;
long long ans = 0;

for (long long x : a) {
    pref += x;

    int rem = (int)((pref % k + k) % k);

    ans += cnt[rem];
    cnt[rem]++;
}
```

## Common mistake

> Using `pref % K` directly when prefix sums can be negative.

## Practice from the original roadmap

1. [Subarray Sums Divisible by K](https://leetcode.com/problems/subarray-sums-divisible-by-k/)
2. [Continuous Subarray Sum](https://leetcode.com/problems/continuous-subarray-sum/)
3. [Make Sum Divisible by P](https://leetcode.com/problems/make-sum-divisible-by-p/)
4. [Subarray Divisibility](https://cses.fi/problemset/task/1662)

---


# Pattern 5 Transform Values Then Prefix

## Prerequisites for this pattern

Pattern 3, ability to encode story values into numeric contributions.

## Recognition signal

Condition says equal counts, average equals something, good/bad balance, odd/even categories, etc.

## State meaning

Transform each element so the awkward condition becomes a normal prefix equation.

## Step-by-step derivation

1. Ignore the original story labels temporarily.
2. Decide what contribution each type should make.
3. Transform the array.
4. Rewrite the desired property as `sum = 0`, `sum = K`, or repeated prefix state.
5. Apply the appropriate prefix pattern.

## ASCII memory diagram

```text
Story:
0   1   0   0   1   1

Transform:
-1 +1  -1  -1  +1  +1

If transformed subarray sum = 0:

(+1 contributions)
cancel
(-1 contributions)

=> equal number of the two categories
```

## C++17 template

```cpp
unordered_map<long long, int> first;
first[0] = -1;

long long pref = 0;
int best = 0;

for (int i = 0; i < n; i++) {
    long long value = transform(a[i]);
    pref += value;

    if (first.count(pref)) {
        best = max(best, i - first[pref]);
    } else {
        first[pref] = i;
    }
}
```

## Common mistake

> Memorizing a transformation instead of deriving what contribution makes the target condition algebraically simple.

## Practice from the original roadmap

1. [Contiguous Array](https://leetcode.com/problems/contiguous-array/)
2. [Count Number of Nice Subarrays](https://leetcode.com/problems/count-number-of-nice-subarrays/)
3. [Good Subarrays](https://codeforces.com/problemset/problem/1398/C)
4. [Longest Well-Performing Interval](https://leetcode.com/problems/longest-well-performing-interval/)
5. [Count Subarrays With Median K](https://leetcode.com/problems/count-subarrays-with-median-k/)

---


# Pattern 6 Prefix XOR Prefix State

## Prerequisites for this pattern

Bitwise XOR, `x ^ x = 0`, prefix-state idea.

## Recognition signal

Range XOR, parity/toggle state, or categories where only odd/even occurrence matters.

## State meaning

`px[i]` = XOR of elements `1..i`; range XOR is `px[R] ^ px[L-1]`.

## Step-by-step derivation

1. Build prefix XOR.
2. Notice that the common left prefix appears twice.
3. XORing the two prefix states cancels it.
4. For parity-mask variants, one bit represents one category's odd/even count.
5. Repeated masks mean the interval changed every tracked category an even number of times.

## ASCII memory diagram

```text
prefix R:
[5][2][7][2]

prefix L-1:
[5]

XOR:
[5][2][7][2]
 ^ [5]
------------
    [2][7][2]

because:
5 ^ 5 = 0
```

## C++17 template

```cpp
vector<int> px(n + 1, 0);

for (int i = 1; i <= n; i++) {
    px[i] = px[i - 1] ^ a[i];
}

auto rangeXor = [&](int L, int R) {
    return px[R] ^ px[L - 1];
};
```

## Common mistake

> Treating XOR like ordinary addition/subtraction; use cancellation properties instead.

## Practice from the original roadmap

1. [XOR Queries of a Subarray](https://leetcode.com/problems/xor-queries-of-a-subarray/)
2. [Count Triplets That Can Form Two Arrays of Equal XOR](https://leetcode.com/problems/count-triplets-that-can-form-two-arrays-of-equal-xor/)
3. [Find the Longest Substring Containing Vowels in Even Counts](https://leetcode.com/problems/find-the-longest-substring-containing-vowels-in-even-counts/)
4. [Number of Wonderful Substrings](https://leetcode.com/problems/number-of-wonderful-substrings/)
5. [Beautiful Subarrays](https://leetcode.com/problems/count-the-number-of-beautiful-subarrays/)

---


# Pattern 7 Weighted Prefix Sum Index Times A i

## Prerequisites for this pattern

Pattern 1, algebraic expansion, global index vs local position.

## Recognition signal

Contribution contains position/frequency weights such as `1*A[L] + 2*A[L+1] + ...`.

## State meaning

Maintain both ordinary prefix `P0 = ΣA[i]` and weighted prefix `P1 = Σi*A[i]`.

## Step-by-step derivation

1. Write the local coefficient at global index `i`.
2. Convert `local = i-L+1 = i-(L-1)`.
3. Expand the sum.
4. Separate terms involving `ΣA[i]` and `Σi*A[i]`.
5. Precompute both prefix arrays.
6. Answer each weighted range in O(1).

## ASCII memory diagram

```text
Query [L..R]

global i:       L      L+1      L+2
local weight:   1       2        3
value:         A[L]   A[L+1]   A[L+2]

local = i - (L-1)

Σ A[i]*(i-(L-1))
      =
Σ i*A[i] - (L-1)*ΣA[i]
```

## C++17 template

```cpp
vector<long long> p0(n + 1, 0);
vector<long long> p1(n + 1, 0);

for (int i = 1; i <= n; i++) {
    p0[i] = p0[i - 1] + a[i];
    p1[i] = p1[i - 1] + 1LL * i * a[i];
}

auto weighted = [&](int L, int R) -> long long {
    long long s0 = p0[R] - p0[L - 1];
    long long s1 = p1[R] - p1[L - 1];

    return s1 - 1LL * (L - 1) * s0;
};
```

## Common mistake

> Using local query positions directly without converting them to global-index algebra.

## Practice from the original roadmap

1. [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/)
2. [Minimum Operations to Make All Array Elements Equal](https://leetcode.com/problems/minimum-operations-to-make-all-array-elements-equal/)
3. [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)
4. [Maximum Sum Obtained of Any Permutation](https://leetcode.com/problems/maximum-sum-obtained-of-any-permutation/)
5. [Sum of Total Strength of Wizards](https://leetcode.com/problems/sum-of-total-strength-of-wizards/)

---


# Pattern 8 Prefix of Prefix Double Prefix

## Prerequisites for this pattern

Pattern 1, nested sums, comfort with counting how many times a prefix participates.

## Recognition signal

Need sum of many prefix sums, weighted subarray contributions, or a formula containing `Σ pref[i]`.

## State meaning

Build prefix once, then prefix the prefix array again.

## Step-by-step derivation

1. Build `pref[i] = Σ A`.
2. Recognize the query wants a sum over many `pref[i]`.
3. Build `pref2[i] = pref2[i-1] + pref[i]`.
4. Use `pref2[R] - pref2[L-1]` to obtain a range sum of prefix values.
5. Adjust boundary terms according to the problem's exact formula.

## ASCII memory diagram

```text
A:
a1   a2   a3   a4

first prefix:
P1   P2   P3   P4

second prefix:
Q1   Q2   Q3   Q4

where:
P[i] = P[i-1] + A[i]
Q[i] = Q[i-1] + P[i]

A --prefix--> P --prefix--> Q
```

## C++17 template

```cpp
vector<long long> pref(n + 1, 0);
vector<long long> pref2(n + 1, 0);

for (int i = 1; i <= n; i++) {
    pref[i] = pref[i - 1] + a[i];
    pref2[i] = pref2[i - 1] + pref[i];
}

auto sumOfPrefixes = [&](int L, int R) -> long long {
    return pref2[R] - pref2[L - 1];
};
```

## Common mistake

> Assuming every double-prefix problem has the same final boundary correction; derive the exact expression.

## Practice from the original roadmap

1. [Sum of Total Strength of Wizards](https://leetcode.com/problems/sum-of-total-strength-of-wizards/)
2. [Greg and Array](https://codeforces.com/problemset/problem/295/A)
3. [Karen and Coffee](https://codeforces.com/problemset/problem/816/B)
4. [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)

---


# Pattern 9 2D Prefix Sum

## Prerequisites for this pattern

1D prefix sums, matrix coordinates, inclusion-exclusion.

## Recognition signal

Many rectangle/submatrix sum or count queries.

## State meaning

`pref[r][c]` stores the total inside rectangle `(1,1)` to `(r,c)`.

## Step-by-step derivation

1. Build 2D prefix totals.
2. For query rectangle `(r1,c1)..(r2,c2)`, take the big prefix to `(r2,c2)`.
3. Remove the rectangle above.
4. Remove the rectangle left.
5. The top-left overlap was removed twice, so add it back once.

## ASCII memory diagram

```text
Query rectangle:

+----------------------+
|  A  |      B         |
|-----+----------------|
|  C  |   TARGET D     |
|     |                |
+----------------------+

pref[r2][c2] gives A+B+C+D

subtract top:  A+B
subtract left: A+C

A was subtracted twice
=> add A back once
```

## C++17 template

```cpp
vector<vector<long long>> pref(n + 1, vector<long long>(m + 1, 0));

for (int r = 1; r <= n; r++) {
    for (int c = 1; c <= m; c++) {
        pref[r][c] =
            a[r][c]
            + pref[r - 1][c]
            + pref[r][c - 1]
            - pref[r - 1][c - 1];
    }
}

auto rectSum = [&](int r1, int c1, int r2, int c2) -> long long {
    return pref[r2][c2]
        - pref[r1 - 1][c2]
        - pref[r2][c1 - 1]
        + pref[r1 - 1][c1 - 1];
};
```

## Common mistake

> Forgetting the overlap correction `+ pref[r1-1][c1-1]`.

## Practice from the original roadmap

1. [Range Sum Query 2D - Immutable](https://leetcode.com/problems/range-sum-query-2d-immutable/)
2. [Matrix Block Sum](https://leetcode.com/problems/matrix-block-sum/)
3. [Number of Submatrices That Sum to Target](https://leetcode.com/problems/number-of-submatrices-that-sum-to-target/)
4. [Forest Queries](https://cses.fi/problemset/task/1652)
5. [Counting Rectangles](https://codeforces.com/problemset/problem/1722/E)

---


# Pattern 10 Basic Range Addition

## Prerequisites for this pattern

Prefix reconstruction, range notation, boundary index `R+1`.

## Recognition signal

Many offline range-add updates; applying each update element-by-element is too slow.

## State meaning

`diff[i]` stores how much the running value changes when we enter index `i`.

## Step-by-step derivation

1. For add `X` on `[L,R]`, start the effect at `L`.
2. Stop the effect just after the range at `R+1`.
3. Write `diff[L] += X`.
4. Write `diff[R+1] -= X`.
5. Prefix the difference array once.
6. The running prefix is the total addition at each index.

## ASCII memory diagram

```text
Add +5 on [2,5]

index:  1   2   3   4   5   6
diff:   0  +5   0   0   0  -5

prefix:
        0   5   5   5   5   0

        |<--- active --->|
```

## C++17 template

```cpp
vector<long long> diff(n + 2, 0);

for (auto [L, R, x] : updates) {
    diff[L] += x;
    diff[R + 1] -= x;
}

for (int i = 1; i <= n; i++) {
    diff[i] += diff[i - 1];
}
```

## Common mistake

> Subtracting at `R` instead of `R+1` for an inclusive range.

## Practice from the original roadmap

1. [Range Addition](https://leetcode.com/problems/range-addition/)
2. [Corporate Flight Bookings](https://leetcode.com/problems/corporate-flight-bookings/)
3. [Shifting Letters II](https://leetcode.com/problems/shifting-letters-ii/)
4. [Greg and Array](https://codeforces.com/problemset/problem/295/A)
5. [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)

---


# Pattern 11 Difference Array as Event Sweep Line

## Prerequisites for this pattern

Pattern 10, sorting, event processing, interval endpoints.

## Recognition signal

Intervals start/end at different coordinates; need maximum overlap, active cost, population, bookings, etc.

## State meaning

Start/end points are events; between consecutive events, the active state is constant.

## Step-by-step derivation

1. Convert every interval into start and stop events.
2. Sort event coordinates.
3. Keep a running `active` value.
4. Between `prev` and current event `x`, nothing changes.
5. Process the constant segment contribution.
6. Apply all changes at `x`.
7. Continue sweeping left to right.

## ASCII memory diagram

```text
timeline:

----+----------+---------+-------->
   x1         x2        x3

events:
x1: +A
x2: -A +B
x3: -B

Between x1 and x2:
active state is constant.

event points = places where state changes
```

## C++17 template

```cpp
map<long long, long long> event;

for (auto [L, R, x] : ranges) {
    event[L] += x;
    event[R + 1] -= x;
}

long long active = 0;
long long prev = 0;
bool first = true;

for (auto [x, delta] : event) {
    if (!first) {
        // segment [prev, x) has constant 'active'
    }

    active += delta;
    prev = x;
    first = false;
}
```

## Common mistake

> Applying the current event before accounting for the segment `[prev,x)` when your convention says the old state governs that segment.

## Practice from the original roadmap

1. [Car Pooling](https://leetcode.com/problems/car-pooling/)
2. [Maximum Population Year](https://leetcode.com/problems/maximum-population-year/)
3. [Describe the Painting](https://leetcode.com/problems/describe-the-painting/)
4. [My Calendar III](https://leetcode.com/problems/my-calendar-iii/)
5. [Restaurant Customers](https://cses.fi/problemset/task/1619)

---


# Pattern 12 Difference Prefix Another Prefix

## Prerequisites for this pattern

Patterns 1 and 10, thinking in pipelines, indicator arrays.

## Recognition signal

First compute coverage/frequency/net effect; then queries are asked on the derived array.

## State meaning

One prefix materializes updates; another prefix answers range queries over the materialized property.

## Step-by-step derivation

1. Use a difference array for range updates/events.
2. Prefix once to recover coverage/frequency/state at every index.
3. Transform that state into the property actually queried.
4. Build another prefix over the transformed array.
5. Answer final queries in O(1).

## ASCII memory diagram

```text
range updates
     |
     v
DIFF ARRAY
     |
   prefix
     v
coverage / frequency
     |
 transform
     v
good[i] / weighted[i]
     |
   prefix again
     v
query answers
```

## C++17 template

```cpp
vector<long long> diff(n + 2, 0);

for (auto [L, R] : intervals) {
    diff[L]++;
    diff[R + 1]--;
}

vector<int> coverage(n + 1, 0);
vector<int> good(n + 1, 0);
vector<int> prefGood(n + 1, 0);

for (int i = 1; i <= n; i++) {
    coverage[i] = coverage[i - 1] + diff[i];
    good[i] = (coverage[i] >= k);
    prefGood[i] = prefGood[i - 1] + good[i];
}

auto query = [&](int L, int R) {
    return prefGood[R] - prefGood[L - 1];
};
```

## Common mistake

> Stopping after the first prefix even though the queries are about a transformed property of the recovered state.

## Practice from the original roadmap

1. [Karen and Coffee](https://codeforces.com/problemset/problem/816/B)
2. [Greg and Array](https://codeforces.com/problemset/problem/295/A)
3. [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)
4. [Maximum Sum Obtained of Any Permutation](https://leetcode.com/problems/maximum-sum-obtained-of-any-permutation/)
5. [Shifting Letters II](https://leetcode.com/problems/shifting-letters-ii/)

---


# Pattern 13 Difference Array Coordinate Compression

## Prerequisites for this pattern

Pattern 11, sorting + unique, `lower_bound`, preserving real coordinate gaps.

## Recognition signal

Coordinates are huge (`1e9`, `1e18`) but only O(N) endpoints actually matter.

## State meaning

Compress only change-points, but keep their real coordinate values so segment lengths remain correct.

## Step-by-step derivation

1. Collect every important endpoint.
2. For inclusive intervals, often collect `L` and `R+1`.
3. Sort and remove duplicates.
4. Map real coordinates to dense indices.
5. Perform difference/event logic on compressed indices.
6. Prefix to recover active state.
7. Multiply by the **real** gap `xs[i+1]-xs[i]` when needed.

## ASCII memory diagram

```text
REAL LINE:

10 -------- 500 --- 901 ---------------- 1,000,001
^            ^      ^                         ^
event        event   event                     event

COMPRESSED:

0 ---------- 1 ----- 2 ------------------------ 3

Important:
compressed gap 1
!=
real distance 1
```

## C++17 template

```cpp
vector<long long> xs;

for (auto [L, R] : segments) {
    xs.push_back(L);
    xs.push_back(R + 1);
}

sort(xs.begin(), xs.end());
xs.erase(unique(xs.begin(), xs.end()), xs.end());

auto id = [&](long long x) {
    return (int)(lower_bound(xs.begin(), xs.end(), x) - xs.begin());
};

vector<long long> diff(xs.size() + 1, 0);

// Apply compressed updates here.
// Recover state by prefix.
// Use xs[i + 1] - xs[i] for real segment length.
```

## Common mistake

> Treating adjacent compressed indices as if their real coordinates were one unit apart.

## Practice from the original roadmap

1. [Describe the Painting](https://leetcode.com/problems/describe-the-painting/)
2. [Brightest Position on Street](https://leetcode.com/problems/brightest-position-on-street/)
3. [Covered Points Count](https://codeforces.com/problemset/problem/1000/C)
4. [Snuke Prime](https://atcoder.jp/contests/abc188/tasks/abc188_d)

---


# Pattern 14 Difference of a Prefix Array Reconstruct Original

## Prerequisites for this pattern

Pattern 1, inverse operations, consecutive differences.

## Recognition signal

Given cumulative/prefix data and asked to reconstruct or validate the original local sequence.

## State meaning

Prefix is discrete integration; consecutive difference is discrete differentiation.

## Step-by-step derivation

1. Start from `pref[i] = pref[i-1] + A[i]`.
2. Rearrange to `A[i] = pref[i] - pref[i-1]`.
3. Difference consecutive cumulative values.
4. Validate recovered values against the problem constraints.
5. If a prefix is missing/corrupted, reason about which local differences are affected.

## ASCII memory diagram

```text
Original moves:
  3     5     2     7
  |     |     |     |
  v     v     v     v

prefix checkpoints:
0 ---- 3 ---- 8 ---- 10 ---- 17

recover:
3-0  = 3
8-3  = 5
10-8 = 2
17-10= 7
```

## C++17 template

```cpp
vector<long long> a(n + 1, 0);

for (int i = 1; i <= n; i++) {
    a[i] = pref[i] - pref[i - 1];
}
```

## Common mistake

> Using differences mechanically without checking whether the reconstructed values satisfy permutation/order/nonnegative constraints.

## Practice from the original roadmap

1. [Prefix Permutation Sums](https://codeforces.com/problemset/problem/1851/D)
2. [Prefix Sum Addicts](https://codeforces.com/problemset/problem/1738/B)
3. [Array Recovery](https://codeforces.com/problemset/problem/1739/B)

---


# Pattern 15 AP Range Update Linear Difference

## Prerequisites for this pattern

Difference arrays, arithmetic progression, linear function algebra.

## Recognition signal

A range update adds `x, x+d, x+2d, ...`; update value changes linearly with position.

## State meaning

An AP can be written as `p*i + q`; maintain boundary changes for slope/intercept (or an equivalent higher-order difference).

## Step-by-step derivation

1. Write contribution at index `i`: `x + (i-L)*d`.
2. Expand: `d*i + (x-d*L)`.
3. Recognize this is a linear function of global index `i`.
4. Store range activation/deactivation for the linear coefficients.
5. Prefix the coefficient-difference arrays.
6. Reconstruct each position's total linear contribution.
7. For problem-specific variants, a lazy segment tree may be more convenient.

## ASCII memory diagram

```text
Update [2,5]:
start = 3
d     = 2

index: 2   3   4   5
add:   3   5   7   9
         +2  +2  +2

Formula:
add(i) = 3 + (i-2)*2
       = 2*i - 1

The update itself has a SLOPE.
```

## C++17 template

```cpp
// Generic offline linear-function idea:
// contribution on [L,R] is p*i + q.

vector<long long> diffP(n + 3, 0);
vector<long long> diffQ(n + 3, 0);

auto addLinear = [&](int L, int R, long long p, long long q) {
    diffP[L] += p;
    diffP[R + 1] -= p;

    diffQ[L] += q;
    diffQ[R + 1] -= q;
};

long long p = 0, q = 0;

for (int i = 1; i <= n; i++) {
    p += diffP[i];
    q += diffQ[i];

    long long add = p * i + q;
    // a[i] += add;
}
```

## Common mistake

> Trying to use the basic constant difference-array update directly for a value that changes across the range.

## Practice from the original roadmap

1. [Polynomial Queries](https://cses.fi/problemset/task/1736)
2. [Greg and Array](https://codeforces.com/problemset/problem/295/A)
3. [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/)

---


# Pattern 16 GP Recurrence Based Range Update

## Prerequisites for this pattern

Recurrences, Pattern 10, boundary cancellation, modular arithmetic when required.

## Recognition signal

Range update follows GP/Fibonacci/another recurrence; each next contribution depends on previous contribution(s).

## State meaning

Store the small recurrence state at range boundaries rather than every interior value.

## Step-by-step derivation

1. Write the recurrence explicitly.
2. Determine the minimal state needed to continue it.
3. Seed that state at `L`.
4. Let the recurrence propagate while scanning.
5. Compute what the recurrence would contribute just after `R`.
6. Add cancellation state there.
7. For Fibonacci/other recurrences, cancellation may require multiple state components.

## ASCII memory diagram

```text
GP example, ratio = 2

range [2,5], start = 3

index: 1   2   3   4    5    6
value: 0   3   6   12   24   0

seed:
        +3
         |
         v
        3 -> 6 -> 12 -> 24 -> 48 ...
                                |
                              cancel -48

recurrence state propagates itself
```

## C++17 template

```cpp
// Simple fixed-ratio GP illustration.
// Problem-specific recurrence updates may require more state.

long long ratio;
vector<long long> event(n + 3, 0);
vector<long long> powR(n + 3, 1);

for (int i = 1; i <= n + 2; i++) {
    powR[i] = powR[i - 1] * ratio;
}

// For an update [L,R] with first value start:
// event[L] += start;
// event[R+1] -= start * ratio^(R-L+1);
//
// During reconstruction:
// current = event[i] + ratio * previousCurrent;
```

## Common mistake

> Assuming one scalar difference is always enough. Fibonacci-like recurrences need enough boundary state to represent their order.

## Practice from the original roadmap

1. [Polynomial Queries](https://cses.fi/problemset/task/1736)
2. [DZY Loves Fibonacci Numbers](https://codeforces.com/problemset/problem/446/C)
3. [Fibonacci Segment](https://codeforces.com/problemset/problem/365/C)

---


# Pattern 17 Prefix Sum Binary Search Kth Value

## Prerequisites for this pattern

Prefix counts/sums, monotonicity, binary search (`lower_bound`).

## Recognition signal

Need the first position where cumulative count/sum reaches a target, or the k-th item in a cumulative distribution.

## State meaning

Prefix array becomes a monotone search space when contributions are nonnegative.

## Step-by-step derivation

1. Build cumulative counts or sums.
2. Verify the prefix is monotone.
3. Convert the question to: find the smallest index with `pref[i] >= target`.
4. Binary search that condition.
5. Map the found prefix position back to the requested value/index.

## ASCII memory diagram

```text
prefix counts:

index:   1   2   3   4   5
pref:    2   5   5   9  12

Need k = 8

2   5   5   9   12
            ^
            first pref >= 8

answer index = 4
```

## C++17 template

```cpp
vector<long long> pref(n + 1, 0);

for (int i = 1; i <= n; i++) {
    pref[i] = pref[i - 1] + freq[i];
}

long long target;
int pos = (int)(
    lower_bound(pref.begin() + 1, pref.end(), target)
    - pref.begin()
);
```

## Common mistake

> Binary searching a prefix array that is not monotone because negative contributions are present.

## Practice from the original roadmap

1. [Interesting drink](https://codeforces.com/problemset/problem/706/B)
2. [K-th Not Divisible by n](https://codeforces.com/problemset/problem/1352/C)
3. [K-th Beautiful String](https://codeforces.com/problemset/problem/1328/B)

---


# Pattern 18 Prefix Sum Monotonic Stack Boundaries

## Prerequisites for this pattern

Prefix sums, monotonic stack, previous/next smaller-greater boundaries.

## Recognition signal

Every element acts as min/max over many subarrays; need sum/min-product/weighted contribution over its valid span.

## State meaning

The stack finds where an element is responsible; prefix sums evaluate the sum/value over that span.

## Step-by-step derivation

1. Decide whether each element is treated as minimum or maximum.
2. Use a monotonic stack to find previous and next boundary where that role breaks.
3. The boundaries define the range of subarrays for which this element is responsible.
4. Use prefix or double-prefix sums to evaluate the needed aggregate efficiently.
5. Sum each element's contribution.
6. Handle equal values with a consistent strict/non-strict boundary convention.

## ASCII memory diagram

```text
Array:
... [smaller] [  A[i]  ] [smaller] ...

             <--- valid span --->

stack gives:
L = previous blocking boundary
R = next blocking boundary

Within that span,
A[i] can be the chosen min/max.

PREFIX answers sums inside the span.
STACK finds the span.
```

## C++17 template

```cpp
vector<int> left(n), right(n);
stack<int> st;

for (int i = 0; i < n; i++) {
    while (!st.empty() && a[st.top()] >= a[i]) {
        st.pop();
    }

    left[i] = st.empty() ? -1 : st.top();
    st.push(i);
}

while (!st.empty()) st.pop();

for (int i = n - 1; i >= 0; i--) {
    while (!st.empty() && a[st.top()] > a[i]) {
        st.pop();
    }

    right[i] = st.empty() ? n : st.top();
    st.push(i);
}

// Combine these boundaries with the prefix formula
// required by the specific problem.
```

## Common mistake

> Using the same strictness on both sides with duplicates and double-counting/omitting equal-value subarrays.

## Practice from the original roadmap

1. [Maximum Subarray Min-Product](https://leetcode.com/problems/maximum-subarray-min-product/)
2. [Sum of Subarray Minimums](https://leetcode.com/problems/sum-of-subarray-minimums/)
3. [Sum of Total Strength of Wizards](https://leetcode.com/problems/sum-of-total-strength-of-wizards/)
4. [Imbalanced Array](https://codeforces.com/problemset/problem/817/D)

---


# Pattern 19 Prefix on Trees Paths

## Prerequisites for this pattern

Tree DFS, root-to-node paths, Euler tour/LCA for general path queries, prefix-state maps for path counting.

## Recognition signal

Path/subtree query on a tree that resembles an array prefix problem after rooting the tree.

## State meaning

A root-to-node prefix stores cumulative information along the DFS path; general path values can combine root-prefix states and LCA.

## Step-by-step derivation

1. Root the tree.
2. Define what `pref[u]` means from root to node `u`.
3. DFS while maintaining that state.
4. For root-to-node queries, use `pref[u]` directly.
5. For path `u-v`, combine root prefixes and correct the overlap at `LCA(u,v)`.
6. For path-sum counting, maintain a frequency map of prefix states on the **current DFS path**.
7. Backtrack the map when returning from DFS.

## ASCII memory diagram

```text
root
           |
       pref[root]
        /      \
       a        b
      /          \
     u            v

root -> u has pref[u]
root -> v has pref[v]

General path u -> v:
combine the two root prefixes
and remove the shared prefix around LCA.

DFS prefix-map rule:
ENTER node -> add prefix state
RECURSE
LEAVE node -> remove prefix state
```

## C++17 template

```cpp
unordered_map<long long, int> freq;
freq[0] = 1;

long long ans = 0;

function<void(int, int, long long)> dfs =
    [&](int u, int parent, long long pref) {
        pref += value[u];

        ans += freq[pref - K];
        freq[pref]++;

        for (int v : graph[u]) {
            if (v == parent) continue;
            dfs(v, u, pref);
        }

        freq[pref]--;
    };

dfs(root, -1, 0);
```

## Common mistake

> For path-prefix hash-map problems, forgetting to remove the current prefix during DFS backtracking.

## Practice from the original roadmap

1. [Path Sum III](https://leetcode.com/problems/path-sum-iii/)
2. [Count Paths That Can Form a Palindrome in a Tree](https://leetcode.com/problems/count-paths-that-can-form-a-palindrome-in-a-tree/)
3. [Minimum Edge Weight Equilibrium Queries in a Tree](https://leetcode.com/problems/minimum-edge-weight-equilibrium-queries-in-a-tree/)
4. [Military Problem](https://codeforces.com/problemset/problem/1006/E)
5. [Tree Cutting](https://codeforces.com/problemset/problem/1118/F1)
6. [Blood Cousins](https://codeforces.com/problemset/problem/208/E)

---



# 23. 60-Second Contest Checklist

When a new problem mentions ranges, subarrays, intervals, coverage, or cumulative values:

```text
QUESTION 1
Is the array/static data queried many times?
    YES -> prefix?

QUESTION 2
Is the problem about a subarray equation?
    YES -> write:
           pref[R] - pref[L-1] = target

QUESTION 3
Does only modulo/parity/state matter?
    YES -> compress the prefix state:
           remainder / XOR / bitmask

QUESTION 4
Are there many offline range updates?
    YES -> difference array?

QUESTION 5
Does state change only at endpoints?
    YES -> event sweep?

QUESTION 6
Are coordinates huge but endpoints few?
    YES -> coordinate compression?

QUESTION 7
Does the update vary with index?
    linear      -> AP / weighted prefix
    recurrence  -> recurrence-state update

QUESTION 8
Need first cumulative threshold / k-th?
    YES -> prefix + binary search

QUESTION 9
Does each element own a span until smaller/greater blocker?
    YES -> monotonic stack + prefix

QUESTION 10
Is the same cumulative idea on a tree path?
    YES -> tree prefix / DFS state / LCA
```

---


# 24. Suggested Mastery Order

## Stage 1 — Automatic fundamentals

Make these almost reflexive:

```text
1  Basic Range Sum
2  Prefix + Suffix
3  Prefix + Hash Map
4  Prefix Modulo
5  Transform + Prefix
6  Prefix XOR
10 Basic Difference Array
```

Target:

```text
recognition < 60 seconds
implementation without notes
```

---

## Stage 2 — Strong Div2 B/C toolkit

Then master:

```text
7  Weighted Prefix
8  Double Prefix
9  2D Prefix
11 Event / Sweep
12 Difference → Prefix → Prefix
13 Coordinate Compression
14 Reconstruct from Prefix
17 Prefix + Binary Search
```

These teach you to combine a basic prefix idea with another technique.

---

## Stage 3 — Expert → CM extensions

Finally become comfortable with:

```text
15 AP Range Update
16 Recurrence-Based Update
18 Prefix + Monotonic Stack
19 Tree Prefix / Paths
```

These are less about memorizing one template and more about deriving the correct state.

---


# 25. Final Memory Sheet

```text
PREFIX SUM
==========
store accumulated VALUE

range:
pref[R] - pref[L-1]


DIFFERENCE ARRAY
================
store accumulated CHANGE

add X on [L,R]:
diff[L]   += X
diff[R+1] -= X


HASHED PREFIX
=============
current prefix asks for
a REQUIRED OLD PREFIX


MODULO PREFIX
=============
same remainder
=> difference divisible by K


TRANSFORM + PREFIX
==================
change story values
until condition becomes numeric


PREFIX XOR
==========
same/cancelled prefix state
isolates the interval


WEIGHTED PREFIX
===============
expand coefficient:
need ΣA and Σ(i*A)


DOUBLE PREFIX
=============
prefix the prefix
when query needs sums of cumulative sums


2D PREFIX
=========
big rectangle
- top
- left
+ overlap


EVENT SWEEP
===========
state changes only at events
between events state is constant


COORDINATE COMPRESSION
======================
huge coordinate universe
but few important points

preserve ORDER,
not DISTANCE


RECONSTRUCT PREFIX
==================
A[i] = pref[i] - pref[i-1]


AP UPDATE
=========
update is linear in index


RECURRENCE UPDATE
=================
update carries recurrence STATE


PREFIX + BINARY SEARCH
======================
monotone cumulative state
find first threshold


STACK + PREFIX
==============
stack finds BOUNDARIES
prefix evaluates the SPAN


TREE PREFIX
===========
root the tree
carry cumulative state during DFS
backtrack correctly
```

The central habit across all 19 patterns is:

```text
DO NOT START FROM CODE.

Start from:

"What does my prefix/event/state MEAN?"

Then derive the equation.
```
