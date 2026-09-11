# Prefix Sum & Difference Array Pattern Roadmap — Codeforces + LeetCode (to CM)

> Goal: recognize the pattern quickly, derive the invariant/formula yourself, and solve without reading a solution.
>
> **Hints are intentionally short. No full solutions are included.**
>
> Renderer-safe notation is used throughout (no LaTeX dependency).

---

# How to use this sheet

For every problem:

1. Spend 3–5 minutes decoding the statement into an array/subarray/range model.
2. Ask: **what does one prefix represent?**
3. Write the algebra before coding.
4. If stuck after ~20–30 minutes, read only Hint 1.
5. Re-solve failed problems 2–3 days later.
6. For CF, prioritize problems around your current level, then gradually push toward 1600–1900.

Core identity:

```text
pref[0] = 0
pref[i] = A[1] + ... + A[i]

sum(L,R) = pref[R] - pref[L-1]
```

Difference-array identity:

```text
add X to [L,R]:

diff[L]   += X
diff[R+1] -= X

A[i] = A[i-1] + diff[i]
```

---

# PREFIX SUM PATTERNS

## Pattern 1 — Basic Range Sum / Static Queries

### Recognition signal

```text
many queries
sum/value/count over [L,R]
array does not change
```

Think:

```text
whole prefix to R
-------------------->
          unwanted
<-------- L-1

answer = pref[R] - pref[L-1]
```

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/) | LC | Store one extra zero before the array. |
| 2 | [Find Pivot Index](https://leetcode.com/problems/find-pivot-index/) | LC | Right sum = total - left - A[i]. |
| 3 | [Left and Right Sum Differences](https://leetcode.com/problems/left-and-right-sum-differences/) | LC | Precompute total or prefix/suffix sums. |
| 4 | [K Radius Subarray Averages](https://leetcode.com/problems/k-radius-subarray-averages/) | LC | Every valid answer is one fixed-length range sum. |
| 5 | [Number of Ways to Split Array](https://leetcode.com/problems/number-of-ways-to-split-array/) | LC | Compare prefix with total-prefix. |
| 6 | [Kuriyama Mirai's Stones](https://codeforces.com/problemset/problem/433/B) | CF | Build prefix sums for original and sorted arrays. |
| 7 | [Little Girl and Problem on Sticks](https://codeforces.com/problemset/problem/451/A) | CF | Warm-up; model what remains after repeated operations. |
| 8 | [Interesting drink](https://codeforces.com/problemset/problem/706/B) | CF | Sorted prefix/count boundary; binary search the last affordable value. |
| 9 | [Karen and Coffee](https://codeforces.com/problemset/problem/816/B) | CF | Difference array first, then a prefix over “good” positions. |
| 10 | [Static Range Sum Queries](https://cses.fi/problemset/task/1646) | Bonus | Pure `pref[R]-pref[L-1]` drill. |

**Mastery:** You should identify this family almost immediately.

---

## Pattern 2 — Prefix + Suffix / Split at i

### Recognition signal

```text
left of i vs right of i
split array
remove one position
contribution from both sides
```

ASCII:

```text
[ LEFT PART ][i][ RIGHT PART ]
     pref         total-pref
```

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Find Pivot Index](https://leetcode.com/problems/find-pivot-index/) | LC | Need equality of left and right sums. |
| 2 | [Ways to Make a Fair Array](https://leetcode.com/problems/ways-to-make-a-fair-array/) | LC | Removing i flips parity of all suffix indices. |
| 3 | [Minimum Average Difference](https://leetcode.com/problems/minimum-average-difference/) | LC | Prefix gives left sum; total-prefix gives right. |
| 4 | [Product of Array Except Self](https://leetcode.com/problems/product-of-array-except-self/) | LC | Prefix/suffix idea, but multiplication instead of addition. |
| 5 | [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/) | LC | Sorted order lets you split absolute values into left/right algebra. |
| 6 | [Number of Ways to Split Array](https://leetcode.com/problems/number-of-ways-to-split-array/) | LC | `left >= right`. |
| 7 | [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C) | CF | Count how often each index is used, then rearrange greedily. |
| 8 | [Array Division](https://codeforces.com/problemset/problem/808/D) | CF | Prefix sums + membership lookup on the opposite side. |
| 9 | [Equal Sums](https://codeforces.com/problemset/problem/988/C) | CF | Total minus one element becomes a signature. |
| 10 | [Alice and the List of Presents](https://codeforces.com/problemset/problem/1119/C) | CF | Train algebraic decomposition and invariants. |

---

## Pattern 3 — Prefix Sum + Hash Map: Subarray Sum = K

This is one of the most important prefix patterns.

Derivation:

```text
sum(L..R) = K

pref[R] - pref[L-1] = K

pref[L-1] = pref[R] - K
```

So while standing at `R`, ask:

```text
How many previous prefixes equal pref[R] - K?
```

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/) | LC | Frequency map of previous prefix sums. |
| 2 | [Binary Subarrays With Sum](https://leetcode.com/problems/binary-subarrays-with-sum/) | LC | Same equation; binary values are not essential. |
| 3 | [Count Number of Nice Subarrays](https://leetcode.com/problems/count-number-of-nice-subarrays/) | LC | Convert odd→1, even→0. |
| 4 | [Maximum Size Subarray Sum Equals k](https://leetcode.com/problems/maximum-size-subarray-sum-equals-k/) | LC | Store earliest index, not frequency. |
| 5 | [Path Sum III](https://leetcode.com/problems/path-sum-iii/) | LC | Same prefix-frequency idea on a DFS path. |
| 6 | [Subarray Sums II](https://cses.fi/problemset/task/1661) | Bonus | Canonical arbitrary-integer version. |
| 7 | [Good Subarrays](https://codeforces.com/problemset/problem/1398/C) | CF | Transform so required subarray sum becomes zero. |
| 8 | [Zero Remainder Array](https://codeforces.com/problemset/problem/1374/D) | CF | Practice converting a condition into a normalized key. |
| 9 | [Yet Another Counting Problem](https://codeforces.com/problemset/problem/1342/C) | CF | Count valid positions per repeating prefix/block. |
| 10 | [Non-zero](https://codeforces.com/problemset/problem/1300/A) | CF | Warm-up for transforming sum constraints. |

**CM skill:** Don't memorize `map[prefix-k]`; derive it from `pref[R]-pref[L-1]=K`.

---

## Pattern 4 — Prefix Modulo / Divisibility

Derivation:

```text
sum(L..R) divisible by K

(pref[R] - pref[L-1]) % K = 0

pref[R] % K = pref[L-1] % K
```

Same remainder ⇒ divisible subarray between them.

Important normalization:

```cpp
rem = ((sum % k) + k) % k;
```

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Subarray Sums Divisible by K](https://leetcode.com/problems/subarray-sums-divisible-by-k/) | LC | Count equal prefix remainders. |
| 2 | [Continuous Subarray Sum](https://leetcode.com/problems/continuous-subarray-sum/) | LC | Store earliest index for each remainder; enforce length >= 2. |
| 3 | [Make Sum Divisible by P](https://leetcode.com/problems/make-sum-divisible-by-p/) | LC | What remainder must the removed subarray have? |
| 4 | [Check if Array Pairs Are Divisible by k](https://leetcode.com/problems/check-if-array-pairs-are-divisible-by-k/) | LC | Remainder r pairs with k-r. |
| 5 | [Subarray Divisibility](https://cses.fi/problemset/task/1662) | Bonus | Handle negative modulo carefully. |
| 6 | [Divisibility by Eight](https://codeforces.com/problemset/problem/550/C) | CF | Divisibility structure reduces the search dramatically. |
| 7 | [Prefix Sum Addicts](https://codeforces.com/problemset/problem/1738/B) | CF | Reconstruct constraints from differences of prefix sums. |
| 8 | [Prefix Permutation Sums](https://codeforces.com/problemset/problem/1851/D) | CF | Differences between consecutive prefixes reveal missing permutation values. |
| 9 | [Remainders Game](https://codeforces.com/problemset/problem/687/A) | CF/Stretch | Focus on modeling modular constraints before implementation. |
| 10 | [Counting Rhyme](https://codeforces.com/problemset/problem/1899/G) | CF/Stretch | Train counting by divisibility classes/inclusion ideas. |

---

## Pattern 5 — Transform Values, Then Prefix

Very common CF trick:

```text
original condition looks difficult
        ↓
replace each A[i] by contribution/value
        ↓
condition becomes a normal prefix-sum condition
```

Examples:

```text
0 -> -1
1 -> +1

equal zeros and ones
=> transformed subarray sum = 0
```

or

```text
B[i] = A[i] - 1

sum(A[L..R]) = length
=> sum(B[L..R]) = 0
```

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Contiguous Array](https://leetcode.com/problems/contiguous-array/) | LC | Convert 0→-1. Find repeated prefix sum. |
| 2 | [Count Number of Nice Subarrays](https://leetcode.com/problems/count-number-of-nice-subarrays/) | LC | Odd/even becomes binary. |
| 3 | [Good Subarrays](https://codeforces.com/problemset/problem/1398/C) | CF | Digit sum = length; subtract 1 from every digit. |
| 4 | [Wonderful Coloring - 1](https://codeforces.com/problemset/problem/1551/B1) | CF | Reduce the statement to frequency contributions. |
| 5 | [Balanced Substring](https://codeforces.com/problemset/problem/1234/B2) | CF/Stretch | Search for a state/signature rather than raw substring. |
| 6 | [Longest Well-Performing Interval](https://leetcode.com/problems/longest-well-performing-interval/) | LC | >8 hours → +1, otherwise -1. Need positive subarray sum. |
| 7 | [Count Subarrays With Median K](https://leetcode.com/problems/count-subarrays-with-median-k/) | LC | Relative to k: smaller=-1, larger=+1. |
| 8 | [Count the Number of Beautiful Subarrays](https://leetcode.com/problems/count-the-number-of-beautiful-subarrays/) | LC | Transform to prefix XOR state. |
| 9 | [Number of Wonderful Substrings](https://leetcode.com/problems/number-of-wonderful-substrings/) | LC | Prefix parity mask. |
| 10 | [XOR Queries of a Subarray](https://leetcode.com/problems/xor-queries-of-a-subarray/) | LC | Prefix operation need not be addition. |

---

## Pattern 6 — Prefix XOR / Prefix State

Generalization:

```text
sum: pref[R] - pref[L-1]
xor: px[R] ^ px[L-1]
```

because:

```text
x ^ x = 0
```

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [XOR Queries of a Subarray](https://leetcode.com/problems/xor-queries-of-a-subarray/) | LC | Direct prefix XOR. |
| 2 | [Count Triplets That Can Form Two Arrays of Equal XOR](https://leetcode.com/problems/count-triplets-that-can-form-two-arrays-of-equal-xor/) | LC | Equal prefix XOR means the middle split can vary. |
| 3 | [Find the Longest Substring Containing Vowels in Even Counts](https://leetcode.com/problems/find-the-longest-substring-containing-vowels-in-even-counts/) | LC | Five parity bits form the prefix state. |
| 4 | [Number of Wonderful Substrings](https://leetcode.com/problems/number-of-wonderful-substrings/) | LC | Equal mask or masks differing by one bit. |
| 5 | [Beautiful Subarrays](https://leetcode.com/problems/count-the-number-of-beautiful-subarrays/) | LC | Operation condition collapses to equal prefix XOR. |
| 6 | [XOR and Favorite Number](https://codeforces.com/problemset/problem/617/E) | CF Stretch | Prefix XOR + frequency, then Mo's algorithm. |
| 7 | [Little Girl and Problem on Sticks](https://codeforces.com/problemset/problem/451/A) | CF Warm-up | Train invariant thinking. |
| 8 | [XOR Equation](https://codeforces.com/problemset/problem/635/C) | CF Stretch | Think in terms of XOR algebra/state. |
| 9 | [DZY Loves Sequences](https://codeforces.com/problemset/problem/446/A) | CF | Prefix/suffix lengths of a local property. |
| 10 | [Maximum XOR for Each Query](https://leetcode.com/problems/maximum-xor-for-each-query/) | LC | Maintain cumulative XOR rather than recomputing. |

---

## Pattern 7 — Weighted Prefix Sum / Index * A[i]

Recognition:

```text
A[L] + 2*A[L+1] + ... + len*A[R]
index-dependent coefficient
weighted range queries
```

Derive:

```text
weight(i) = i-L+1 = i-(L-1)

answer
= Σ A[i]*(i-(L-1))
= Σ i*A[i] - (L-1)*Σ A[i]
```

Precompute:

```text
P0[i] = Σ A[j]
P1[i] = Σ j*A[j]
```

Then:

```text
S0 = P0[R]-P0[L-1]
S1 = P1[R]-P1[L-1]

answer = S1 - (L-1)*S0
```

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/) | LC | Algebra creates `i*A[i] - prefix`. |
| 2 | [Minimum Operations to Make All Array Elements Equal](https://leetcode.com/problems/minimum-operations-to-make-all-array-elements-equal/) | LC | Sort + prefix; split at lower_bound(query). |
| 3 | [Movement of Robots](https://codeforces.com/problemset/problem/1850/G) | CF/Stretch | Seek contribution counting after sorting/transformation. |
| 4 | [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C) | CF | Frequency is effectively a weight per position. |
| 5 | [Kuriyama Mirai's Stones](https://codeforces.com/problemset/problem/433/B) | CF | Two different prefix representations. |
| 6 | [Maximum Sum Obtained of Any Permutation](https://leetcode.com/problems/maximum-sum-obtained-of-any-permutation/) | LC | Difference array computes position weights. |
| 7 | [Maximum Sum of an Hourglass](https://leetcode.com/problems/maximum-sum-of-an-hourglass/) | LC Warm-up | Practice fixed coefficient/contribution decomposition. |
| 8 | [Sum of Total Strength of Wizards](https://leetcode.com/problems/sum-of-total-strength-of-wizards/) | LC Hard | Prefix-of-prefix sums are needed for weighted interval contributions. |
| 9 | [Count of Range Sum](https://leetcode.com/problems/count-of-range-sum/) | LC Hard | Prefix values become objects you count/order. |
| 10 | [Maximum Subarray Min-Product](https://leetcode.com/problems/maximum-subarray-min-product/) | LC | Prefix sum + boundaries from monotonic stack. |

---

## Pattern 8 — Prefix of Prefix / Double Prefix

Think:

```text
A
 ↓ prefix
P
 ↓ prefix again
PP
```

Useful when the formula asks for:

```text
P[L] + P[L+1] + ... + P[R]
```

or when contributions themselves contain range sums.

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Sum of Total Strength of Wizards](https://leetcode.com/problems/sum-of-total-strength-of-wizards/) | LC Hard | Need sums of prefix sums on left/right of each minimum. |
| 2 | [Product of the Last K Numbers](https://leetcode.com/problems/product-of-the-last-k-numbers/) | LC | Prefix products; reset around zero. |
| 3 | [Range Sum of Sorted Subarray Sums](https://leetcode.com/problems/range-sum-of-sorted-subarray-sums/) | LC | Think about cumulative sums of generated subarray sums. |
| 4 | [Maximum Sum of 3 Non-Overlapping Subarrays](https://leetcode.com/problems/maximum-sum-of-3-non-overlapping-subarrays/) | LC | Prefix range sums become DP building blocks. |
| 5 | [Arithmetic Slices II](https://leetcode.com/problems/arithmetic-slices-ii-subsequence/) | LC Stretch | State by difference; useful progression toward weighted-state thinking. |
| 6 | [Greg and Array](https://codeforces.com/problemset/problem/295/A) | CF | Difference/prefix once for operation counts, again for array changes. |
| 7 | [Karen and Coffee](https://codeforces.com/problemset/problem/816/B) | CF | Diff → prefix temperatures → prefix “good” indicator. |
| 8 | [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C) | CF | Diff → prefix usage counts → weighted sum. |
| 9 | [Imbalanced Array](https://codeforces.com/problemset/problem/817/D) | CF Stretch | Contribution decomposition; combine with prefix-style thinking. |
| 10 | [Prefix Permutation Sums](https://codeforces.com/problemset/problem/1851/D) | CF | Differentiate a prefix array to recover local values. |

---

## Pattern 9 — 2D Prefix Sum

Definition:

```text
P[r][c] = sum of rectangle (1,1) -> (r,c)
```

Query:

```text
             c1        c2
          +-----------+
      r1  |  TARGET   |
          |           |
      r2  +-----------+

ans =
P[r2][c2]
- P[r1-1][c2]
- P[r2][c1-1]
+ P[r1-1][c1-1]
```

Last term restores the area subtracted twice.

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Range Sum Query 2D - Immutable](https://leetcode.com/problems/range-sum-query-2d-immutable/) | LC | Canonical 2D prefix. |
| 2 | [Matrix Block Sum](https://leetcode.com/problems/matrix-block-sum/) | LC | Every cell asks one clipped rectangle query. |
| 3 | [Number of Submatrices That Sum to Target](https://leetcode.com/problems/number-of-submatrices-that-sum-to-target/) | LC Hard | Fix two row boundaries; collapse columns into 1D. |
| 4 | [Max Sum of Rectangle No Larger Than K](https://leetcode.com/problems/max-sum-of-rectangle-no-larger-than-k/) | LC Hard | Compress one dimension, then ordered prefix sums. |
| 5 | [Stamping the Grid](https://leetcode.com/problems/stamping-the-grid/) | LC Hard | 2D prefix checks emptiness; 2D diff marks coverage. |
| 6 | [Forest Queries](https://cses.fi/problemset/task/1652) | Bonus | Pure 2D prefix drill. |
| 7 | [The Meeting Place Cannot Be Changed](https://codeforces.com/problemset/problem/782/B) | CF Warm-up | Not 2D prefix; useful boundary modeling exercise. |
| 8 | [Stars Drawing](https://codeforces.com/problemset/problem/1019/A) | CF/Stretch | Think about preprocessing directional/grid information. |
| 9 | [Greg and Graph](https://codeforces.com/problemset/problem/295/B) | CF Stretch | Matrix cumulative-state thinking. |
| 10 | [Counting Rectangles](https://codeforces.com/problemset/problem/1722/E) | CF | Build a 2D table by height/width, then rectangle-query it. |

---

# DIFFERENCE ARRAY PATTERNS

## Pattern 10 — Basic Range Addition

Recognition:

```text
many operations:
add X to every A[L..R]

only final array is needed
```

One operation:

```text
L                 R
|-----------------|
       +X

diff:
       +X          -X
        |           |
        L          R+1
```

Prefixing `diff` spreads X through the range.

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Range Addition](https://leetcode.com/problems/range-addition/) | LC | Canonical diff array. |
| 2 | [Corporate Flight Bookings](https://leetcode.com/problems/corporate-flight-bookings/) | LC | Each booking is one range addition. |
| 3 | [Car Pooling](https://leetcode.com/problems/car-pooling/) | LC | Pickup = +passengers, drop-off = -passengers. |
| 4 | [Maximum Population Year](https://leetcode.com/problems/maximum-population-year/) | LC | Birth starts contribution; death ends it. |
| 5 | [Shifting Letters II](https://leetcode.com/problems/shifting-letters-ii/) | LC | Convert each shift to +1/-1 range update. |
| 6 | [Greg and Array](https://codeforces.com/problemset/problem/295/A) | CF | Two layers of difference arrays. |
| 7 | [Karen and Coffee](https://codeforces.com/problemset/problem/816/B) | CF | Range coverage first; answer queries with another prefix. |
| 8 | [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C) | CF | Difference counts query frequency per index. |
| 9 | [Ciel and Duel](https://codeforces.com/problemset/problem/321/B) | CF Stretch | Contribution/frequency reasoning. |
| 10 | [Range Updates and Sums](https://cses.fi/problemset/task/1735) | Bonus Stretch | When offline diff stops working, learn why lazy propagation is needed. |

---

## Pattern 11 — Difference Array as Event / Sweep Line

Intervals become events:

```text
[start] += X
[end+1] -= X
```

Then sweep from left to right.

This is conceptually the same as a difference array, even when coordinates represent **time**, **position**, **year**, or **temperature**.

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Car Pooling](https://leetcode.com/problems/car-pooling/) | LC | Capacity is maximum active load during the sweep. |
| 2 | [Corporate Flight Bookings](https://leetcode.com/problems/corporate-flight-bookings/) | LC | Flight indices are coordinates. |
| 3 | [Maximum Population Year](https://leetcode.com/problems/maximum-population-year/) | LC | Treat lifespan endpoints as events. |
| 4 | [Describe the Painting](https://leetcode.com/problems/describe-the-painting/) | LC | Coordinate events; maintain active color sum. |
| 5 | [My Calendar III](https://leetcode.com/problems/my-calendar-iii/) | LC | Start +1, end -1; maximum prefix is max overlap. |
| 6 | [Karen and Coffee](https://codeforces.com/problemset/problem/816/B) | CF | Temperature ranges are intervals on a coordinate line. |
| 7 | [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C) | CF | Query endpoints create usage-frequency events. |
| 8 | [Covered Points Count](https://codeforces.com/problemset/problem/1000/C) | CF | Sweep compressed endpoints and track number of active segments. |
| 9 | [The Meeting Place Cannot Be Changed](https://codeforces.com/problemset/problem/782/B) | CF | Interval feasibility at time t; good sweep/boundary thinking. |
| 10 | [Restaurant Customers](https://cses.fi/problemset/task/1619) | Bonus | Classic +1 arrival / -1 departure sweep. |

---

## Pattern 12 — Difference + Prefix + Another Prefix

Pipeline:

```text
range updates
     ↓
difference
     ↓ prefix
actual frequency/value
     ↓ transform
0/1 or weighted value
     ↓ prefix
range queries
```

This is a major CF pattern.

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Karen and Coffee](https://codeforces.com/problemset/problem/816/B) | CF | Coverage count → mark coverage>=k → prefix again. |
| 2 | [Greg and Array](https://codeforces.com/problemset/problem/295/A) | CF | First diff counts operation applications; second diff applies weighted operations. |
| 3 | [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C) | CF | Query diff → usage counts → sort with A. |
| 4 | [Maximum Sum Obtained of Any Permutation](https://leetcode.com/problems/maximum-sum-obtained-of-any-permutation/) | LC | Same usage-frequency idea. |
| 5 | [Shifting Letters II](https://leetcode.com/problems/shifting-letters-ii/) | LC | Diff shifts → prefix net shift → transform chars. |
| 6 | [Stamping the Grid](https://leetcode.com/problems/stamping-the-grid/) | LC Hard | Prefix checks possible stamps; diff paints their coverage. |
| 7 | [Flight Bookings](https://leetcode.com/problems/corporate-flight-bookings/) | LC | Basic version before multi-layer problems. |
| 8 | [Car Pooling](https://leetcode.com/problems/car-pooling/) | LC | Prefix of event changes gives current load. |
| 9 | [Maximum Population Year](https://leetcode.com/problems/maximum-population-year/) | LC | Prefix of event changes gives population. |
| 10 | [Covered Points Count](https://codeforces.com/problemset/problem/1000/C) | CF | Active count after events determines contribution length. |

---

## Pattern 13 — Difference Array + Coordinate Compression

Use when coordinates are huge:

```text
coordinate <= 1e9 / 1e18
but only O(N) endpoints matter
```

Do **not** allocate the entire coordinate line.

Pipeline:

```text
collect important coordinates
        ↓
sort + unique
        ↓
map each endpoint to compressed index
        ↓
difference/event sweep
        ↓
remember actual distance between coordinates
```

Important:

```text
compressed index distance != original coordinate distance
```

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Describe the Painting](https://leetcode.com/problems/describe-the-painting/) | LC | Endpoints are the only places active sum changes. |
| 2 | [My Calendar III](https://leetcode.com/problems/my-calendar-iii/) | LC | Ordered map can act as implicit compressed coordinates. |
| 3 | [Amount of New Area Painted Each Day](https://leetcode.com/problems/amount-of-new-area-painted-each-day/) | LC | Large coordinate intervals; avoid touching every point repeatedly. |
| 4 | [Brightest Position on Street](https://leetcode.com/problems/brightest-position-on-street/) | LC | Light creates +1/-1 events. |
| 5 | [Falling Squares](https://leetcode.com/problems/falling-squares/) | LC Hard | Compress left/right boundaries. |
| 6 | [Covered Points Count](https://codeforces.com/problemset/problem/1000/C) | CF | Sweep sorted endpoints and multiply active count by gap length. |
| 7 | [The Great Hero](https://codeforces.com/problemset/problem/1480/B) | CF Warm-up | Contribution/event modeling practice. |
| 8 | [Points on Line](https://codeforces.com/problemset/problem/251/A) | CF | Sorted coordinates; exploit only relevant positions. |
| 9 | [Duff in the Army](https://codeforces.com/problemset/problem/587/C) | CF Stretch | Coordinate/state compression mindset. |
| 10 | [Snuke Prime](https://atcoder.jp/contests/abc188/tasks/abc188_d) | Bonus | Canonical coordinate-compressed difference/sweep problem. |

---

## Pattern 14 — Difference of a Prefix Array / Reconstruct Original

Prefix sums can be reversed:

```text
pref[i] = pref[i-1] + A[i]

therefore

A[i] = pref[i] - pref[i-1]
```

This is discrete differentiation.

Recognition:

```text
given prefix sums
some prefixes missing
restore/check original sequence
```

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Prefix Permutation Sums](https://codeforces.com/problemset/problem/1851/D) | CF | Consecutive differences should mostly be unused values 1..n. |
| 2 | [Prefix Sum Addicts](https://codeforces.com/problemset/problem/1738/B) | CF | Differences of known suffix-prefix values impose monotonic constraints. |
| 3 | [Find the Middle Index in Array](https://leetcode.com/problems/find-the-middle-index-in-array/) | LC Warm-up | Practice moving between total/prefix/local values. |
| 4 | [Running Sum of 1d Array](https://leetcode.com/problems/running-sum-of-1d-array/) | LC Warm-up | Build prefix, then mentally reverse it. |
| 5 | [Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/) | LC | Understand prefix as an invertible representation. |
| 6 | [Find Pivot Index](https://leetcode.com/problems/find-pivot-index/) | LC | Local condition derived from global prefix information. |
| 7 | [Maximum Prefix Sums](https://codeforces.com/problemset/problem/2231/D) | CF Stretch | Reconstruct an array consistent with information about its prefix sums. |
| 8 | [Array Recovery](https://codeforces.com/problemset/problem/1739/B) | CF | Reconstruct local values from an encoded difference-like array. |
| 9 | [Recover an RBS](https://codeforces.com/problemset/problem/1709/C) | CF | Reconstruct sequence under prefix constraints. |
| 10 | [Recovering BST](https://codeforces.com/problemset/problem/1025/D) | CF Stretch | Reconstruction mindset; later combine with DP. |

---

## Pattern 15 — AP Range Update (Linear Difference)

Suppose every update adds:

```text
x, x+d, x+2d, ..., x+(R-L)d
```

to `[L,R]`.

Write contribution at position i:

```text
x + (i-L)*d
= d*i + (x-d*L)
```

So every AP update is a linear function:

```text
p*i + q
```

Maintain two difference arrays:

```text
diffP -> coefficient of i
diffQ -> constant
```

For update `[L,R]`:

```text
p = d
q = x - d*L

diffP[L]   += p
diffP[R+1] -= p

diffQ[L]   += q
diffQ[R+1] -= q
```

After prefixing:

```text
A[i] += P[i]*i + Q[i]
```

### Practice set

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Shifting Letters II](https://leetcode.com/problems/shifting-letters-ii/) | LC Prep | Master constant range updates first. |
| 2 | [Corporate Flight Bookings](https://leetcode.com/problems/corporate-flight-bookings/) | LC Prep | Constant coefficient = degree-0 polynomial update. |
| 3 | [Greg and Array](https://codeforces.com/problemset/problem/295/A) | CF Prep | Layer difference arrays before adding linear coefficients. |
| 4 | [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C) | CF Prep | Think of frequency as a coefficient. |
| 5 | [Polynomial Queries](https://cses.fi/problemset/task/1736) | Bonus Core | Range update adds `1,2,3,...`; canonical AP update problem. |
| 6 | [Horrible Queries](https://www.spoj.com/problems/HORRIBLE/) | Bonus | Constant range update/query before polynomial lazy ideas. |
| 7 | [Range Updates and Sums](https://cses.fi/problemset/task/1735) | Bonus | Learn when offline diff must become lazy propagation. |
| 8 | [Sum of Total Strength of Wizards](https://leetcode.com/problems/sum-of-total-strength-of-wizards/) | LC Hard | Weighted interval sums require linear coefficients. |
| 9 | [Maximum Sum Obtained of Any Permutation](https://leetcode.com/problems/maximum-sum-obtained-of-any-permutation/) | LC | Contribution coefficient practice. |
| 10 | [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/) | LC | Derive linear terms involving index and prefix sum. |

---

## Pattern 16 — GP / Recurrence-Based Range Update

GP updates look like:

```text
x, x*r, x*r^2, ...
```

Unlike AP, the next value depends multiplicatively on the previous value.

Recognition:

```text
range update follows a recurrence
Fibonacci / GP / linear recurrence
```

Main idea:

```text
Do not store every term.
Store where a recurrence contribution STARTS
and where its influence must be CANCELLED.
```

For advanced problems this often becomes:

```text
difference-like boundary state
+ recurrence propagation
```

### Practice progression

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Range Addition](https://leetcode.com/problems/range-addition/) | LC Prep | Degree-0 update. |
| 2 | [Shifting Letters II](https://leetcode.com/problems/shifting-letters-ii/) | LC Prep | Signed constant updates. |
| 3 | [Polynomial Queries](https://cses.fi/problemset/task/1736) | Bonus Prep | AP is the next step before recurrence updates. |
| 4 | [DZY Loves Fibonacci Numbers](https://codeforces.com/problemset/problem/446/C) | CF Advanced | Range updates add Fibonacci sequence; propagate recurrence lazily. |
| 5 | [Fibonacci Segment](https://codeforces.com/problemset/problem/365/C) | CF Advanced | Treat sequence update as recurrence state, not independent values. |
| 6 | [Kefa and Watch](https://codeforces.com/problemset/problem/580/E) | CF Advanced | Segment state must combine algebraically under updates. |
| 7 | [Interesting Array](https://codeforces.com/problemset/problem/482/B) | CF | Range constraints + reconstruction; boundary/state mindset. |
| 8 | [Multiplication Table](https://codeforces.com/problemset/problem/448/D) | CF | Algebraic counting progression toward harder modeling. |
| 9 | [Product of the Last K Numbers](https://leetcode.com/problems/product-of-the-last-k-numbers/) | LC | Multiplicative prefix structure and zero reset. |
| 10 | [Range Product Queries of Powers](https://leetcode.com/problems/range-product-queries-of-powers/) | LC | Prefix-like handling of multiplicative/exponent structure. |

---

# Pattern 17 — Prefix Sum + Binary Search / K-th Value

Recognition:

```text
K-th item
cumulative frequency
first position whose prefix count >= K
```

ASCII:

```text
value:       2    5    8    10
frequency:   3    2    4     1

prefix:      3    5    9    10

K = 7
              first prefix >= 7
                       |
                       v
                       9

answer = 8
```

Core:

```cpp
idx = lower_bound(pref.begin(), pref.end(), K) - pref.begin();
```

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Find K-th Smallest Pair Distance](https://leetcode.com/problems/find-k-th-smallest-pair-distance/) | LC Hard | Binary search answer; count pairs <= X. |
| 2 | [Kth Smallest Element in a Sorted Matrix](https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/) | LC | Count how many values <= mid. |
| 3 | [K-th Smallest Prime Fraction](https://leetcode.com/problems/k-th-smallest-prime-fraction/) | LC | K-th selection by ordered counting. |
| 4 | [Find K-th Smallest Pair Distance](https://leetcode.com/problems/find-k-th-smallest-pair-distance/) | LC | Practice “first cumulative count reaches K”. |
| 5 | [K-th Not Divisible by n](https://codeforces.com/problemset/problem/1352/C) | CF | Count valid numbers up to X / derive direct formula. |
| 6 | [K-th Number](https://codeforces.com/problemset/problem/1436/C) | CF | Binary-search process translated into combinatorial counts. |
| 7 | [Interesting drink](https://codeforces.com/problemset/problem/706/B) | CF | Count how many values <= query using upper_bound. |
| 8 | [K-th Beautiful String](https://codeforces.com/problemset/problem/1328/B) | CF | Cumulative combinatorial blocks determine the K-th object. |
| 9 | [K-th Excluded](https://codeforces.com/problemset/problem/1234/F) | CF Stretch | Think in cumulative counts of valid/missing states. |
| 10 | **Kth Val (your lecture problem)** | Course | Diff range coverage → `(A[i],freq[i])` → sort/merge → prefix frequency → lower_bound(K). |

---

# Pattern 18 — Prefix Sum + Monotonic Stack / Boundaries

At higher CF/LC levels, prefix sum often does **not** solve the whole problem. Another technique finds boundaries, while prefix sums evaluate the chosen range.

Typical architecture:

```text
monotonic stack -> find L/R boundary
prefix sum      -> calculate sum(L,R)
combine         -> contribution / score
```

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Maximum Subarray Min-Product](https://leetcode.com/problems/maximum-subarray-min-product/) | LC | Stack finds maximal range where A[i] is minimum; prefix gives its sum. |
| 2 | [Sum of Subarray Minimums](https://leetcode.com/problems/sum-of-subarray-minimums/) | LC | Count how many subarrays choose each element as minimum. |
| 3 | [Sum of Total Strength of Wizards](https://leetcode.com/problems/sum-of-total-strength-of-wizards/) | LC Hard | Stack boundaries + double prefix. |
| 4 | [Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/) | LC | Learn boundary computation first. |
| 5 | [Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water/) | LC | Prefix/suffix maxima variant. |
| 6 | [Imbalanced Array](https://codeforces.com/problemset/problem/817/D) | CF | Count each element's contribution as max minus min. |
| 7 | [Histogram Ugliness](https://codeforces.com/problemset/problem/1534/B) | CF | Local contribution changes. |
| 8 | [Maximum Subarray](https://codeforces.com/problemset/problem/1796/D) | CF | Transform and reason about best prefix/subarray state. |
| 9 | [Yet Another Subarray Problem](https://codeforces.com/problemset/problem/1197/D) | CF | Combine prefix/subarray optimization with a periodic cost. |
| 10 | [Maximum White Subtree](https://codeforces.com/problemset/problem/1324/F) | CF Stretch | Prefix-style accumulated contribution generalized to trees. |

---

# Pattern 19 — Prefix on Trees / Paths

Prefix is not limited to arrays.

Root-to-node prefix:

```text
root
 |
 +---- u ---- ... ---- v

path sum can often be built from root-prefix states
```

For additive tree path queries:

```text
path(u,v)
= pref[u] + pref[v]
- 2*pref[lca]
+ value[lca]
```

### Problems

| # | Problem | Site | Hint |
|---|---|---|---|
| 1 | [Path Sum III](https://leetcode.com/problems/path-sum-iii/) | LC | Prefix-frequency map along current DFS path. |
| 2 | [Count Paths That Can Form a Palindrome in a Tree](https://leetcode.com/problems/count-paths-that-can-form-a-palindrome-in-a-tree/) | LC Hard | Root-to-node parity masks; compare masks. |
| 3 | [Minimum Edge Weight Equilibrium Queries in a Tree](https://leetcode.com/problems/minimum-edge-weight-equilibrium-queries-in-a-tree/) | LC Hard | Prefix frequency vector from root to each node + LCA. |
| 4 | [Tree Queries](https://codeforces.com/problemset/problem/1328/E) | CF | Euler/depth prefix-style ancestry representation. |
| 5 | [Maximum White Subtree](https://codeforces.com/problemset/problem/1324/F) | CF | Tree DP accumulation/rerooting. |
| 6 | [Military Problem](https://codeforces.com/problemset/problem/1006/E) | CF | Flatten subtree into contiguous Euler-tour interval. |
| 7 | [Tree Cutting](https://codeforces.com/problemset/problem/1118/F1) | CF | Subtree prefix/count contribution. |
| 8 | [Blood Cousins](https://codeforces.com/problemset/problem/208/E) | CF Stretch | Euler intervals + depth-indexed prefix/count structures. |
| 9 | [Distance in Tree](https://codeforces.com/problemset/problem/161/D) | CF Stretch | Accumulate path-length counts. |
| 10 | [Tree and Queries](https://codeforces.com/problemset/problem/375/D) | CF Advanced | Euler flattening turns tree queries into array queries. |

---

# CM-Level Recognition Map

```text
QUESTION SIGNAL                           FIRST THOUGHT
---------------------------------------------------------------
many static sum queries                  prefix sum
left vs right                            prefix + total/suffix
subarray sum = K                         prefix + hashmap
subarray divisible by K                  prefix modulo
equal counts / strange balance           transform + prefix
XOR/parity state                         prefix XOR / bitmask
index-weighted range sum                 multiple weighted prefixes
sum of prefix sums                       prefix of prefix
rectangle queries                        2D prefix
many offline range additions             difference array
interval start/end events                difference / sweep line
huge coordinates                         compression + sweep/diff
given prefix, recover values              consecutive differences
AP range addition                        coefficient diff arrays
Fibonacci/GP-like update                 recurrence boundary state
K-th by frequencies                      cumulative prefix + lower_bound
range boundary + range score             stack/two pointers + prefix
tree path/subtree                         root prefix / Euler tour
```

---

# Suggested Order to CM

## Stage 1 — automatic fundamentals

```text
Pattern 1  Basic prefix
Pattern 2  Prefix/suffix
Pattern 3  Prefix + hashmap
Pattern 4  Prefix modulo
Pattern 10 Basic difference
```

Target: solve these without notes.

## Stage 2 — Div2 B/C strength

```text
Pattern 5  Transform + prefix
Pattern 6  Prefix XOR/state
Pattern 7  Weighted prefix
Pattern 9  2D prefix
Pattern 11 Sweep/event diff
Pattern 12 Multi-stage diff/prefix
Pattern 14 Recover from prefix
Pattern 17 K-th/cumulative frequency
```

## Stage 3 — Expert → CM

```text
Pattern 8  Double prefix
Pattern 13 Compression + diff
Pattern 15 AP/polynomial updates
Pattern 16 Recurrence updates
Pattern 18 Stack + prefix
Pattern 19 Tree prefix/Euler
```

At CM level the important jump is:

```text
NOT:
"This is a prefix-sum problem."

BUT:
"I can transform this condition into a prefix invariant,
then combine prefix with hashing / modulo / sorting /
binary search / stack / sweep / tree flattening."
```

---

# 60-Second Contest Checklist

When you see an array/range problem, ask in this order:

```text
1. Is the answer about a contiguous range?

2. Can I express it as:
      prefix[R] OP prefix[L-1] ?

3. Is the condition:
      sum = K?
      modulo?
      equal counts?
      XOR/parity?

4. Can I transform A[i] first?

5. Are there many range UPDATES rather than queries?
      -> difference array

6. Are coordinates huge?
      -> coordinate compression / map events

7. Does coefficient depend on index?
      -> weighted prefixes / AP decomposition

8. Do I need K-th / first position?
      -> cumulative count + binary search

9. Does another structure find the range?
      -> prefix + stack/two pointers/binary search

10. Is the array actually a tree/grid?
      -> Euler/root prefix/2D prefix
```

---

# High-value CF core set

If you want a compact first pass before doing every table, prioritize:

1. CF 433B — Kuriyama Mirai's Stones
2. CF 276C — Little Girl and Maximum Sum
3. CF 295A — Greg and Array
4. CF 816B — Karen and Coffee
5. CF 1398C — Good Subarrays
6. CF 1738B — Prefix Sum Addicts
7. CF 1851D — Prefix Permutation Sums
8. CF 1000C — Covered Points Count
9. CF 817D — Imbalanced Array
10. CF 617E — XOR and Favorite Number

Then move to the advanced combination problems.

---

# Final rule

Do not memorize 19 independent templates.

Compress them mentally into four questions:

```text
PREFIX:
What cumulative information makes a range removable by subtraction/XOR?

TRANSFORM:
What should each element represent so the condition becomes cumulative?

DIFFERENCE:
Can I mark only where an update starts and stops?

COMBINATION:
What second technique finds the boundary/state that prefix evaluates?
```

That is the progression from basic prefix sums toward Expert/CM-level problem solving.
