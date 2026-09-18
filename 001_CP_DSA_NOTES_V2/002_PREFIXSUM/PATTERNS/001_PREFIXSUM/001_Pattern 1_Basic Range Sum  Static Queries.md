# Pattern 1 — Basic Range Sum / Static Queries

## Pattern Overview
The **Basic Range Sum / Static Queries** pattern utilizes a precomputed **Prefix Sum Array** to answer range sum queries over an immutable array in $O(1)$ time complexity per query after an initial $O(N)$ preprocessing phase.

Given an array $A$ of $N$ numbers, the prefix sum array $P$ is defined such that $P[i]$ represents the sum of elements from index $0$ to index $i-1$ (or index $1$ to $i$ in 1-indexed representation):
$$P[i] = \sum_{j=1}^{i} A[j]$$

Using this structure, the sum of elements in any range $[L, R]$ (1-indexed) can be computed in constant time as:
$$\text{RangeSum}(L, R) = P[R] - P[L - 1]$$

This approach replaces the naive $O(N)$ per-query scanning technique with an efficient balance: $O(N)$ preprocessing time and space, followed by $O(1)$ query processing.

---

## 1. Range Sum Query - Immutable

**Problem Link:** [Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/description/)

### Range Sum Query - Immutable (Prefix Sum / LeetCode / Easy)

* **Core Invariant / Key Insight:** Precompute a 1-indexed prefix sum array where `pref[i]` stores the sum of the first `i` elements. Any contiguous subarray sum from index `left` to `right` (0-indexed) can then be calculated instantly using `pref[right + 1] - pref[left]`.

* **Step-by-Step Logic:**
  * Read the input array `nums` of length `N` and construct a 1-indexed prefix sum array `pref` of size `N + 1` where `pref[i] = pref[i - 1] + nums[i - 1]`.
  * For each query `sumRange(left, right)`, compute the range sum in $O(1)$ time using the formula `pref[right + 1] - pref[left]`.
  * Return the computed sum result for each query.

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        arr = [-2, 0, 3, -5, 2, -1]

Step 1:         Build 1-indexed Prefix Array
                pref = [0, -2, -2, 1, -4, -2, -3]

Step 2:         Query (left = 0, right = 2) --> Range [0, 2]
                pref[3] - pref[0] = 1 - 0 = 1

Step 3:         Query (left = 2, right = 5) --> Range [2, 5]
                pref[6] - pref[2] = -3 - (-2) = -1

Final Answer:   [1, -1]
Final State:    pref = [0, -2, -2, 1, -4, -2, -3]
```

---

## 2. K Radius Subarray Averages

**Problem Link:** [K Radius Subarray Averages](https://leetcode.com/problems/k-radius-subarray-averages/)

### K Radius Subarray Averages (Prefix Sum / LeetCode / Medium)

* **Core Invariant / Key Insight:** A $k$-radius subarray centered at index `i` spans from index `i - k` to `i + k`, containing a total of `2 * k + 1` elements. If $i - k < 0$ or $i + k \ge N$, the average is impossible and defaults to `-1`; otherwise, the window sum is `pref[i + k + 1] - pref[i - k]`.

* **Step-by-Step Logic:**
  * Construct a 1-indexed prefix sum array `pref` of size `N + 1` using 64-bit integers to prevent integer overflow.
  * Initialize an output array `avgs` of size `N` filled with `-1`.
  * Loop through each index `i` from `k` to `N - 1 - k`, calculate the window sum using `pref[i + k + 1] - pref[i - k]`, compute the integer division by `2 * k + 1`, and assign it to `avgs[i]`.

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        arr = [7, 4, 3, 9, 1, 8, 5, 2, 6], k = 3
                Window size = 2 * 3 + 1 = 7

Step 1:         Build 1-indexed Prefix Array
                pref = [0, 7, 11, 14, 23, 24, 32, 37, 39, 45]

Step 2:         Evaluate indices < k and > N - 1 - k
                avgs[0..2] = -1, avgs[6..8] = -1

Step 3:         Center i = 3 (Range [0, 6]):
                sum = pref[7] - pref[0] = 37 - 0 = 37
                avg = 37 / 7 = 5 --> avgs[3] = 5

Step 4:         Center i = 4 (Range [1, 7]):
                sum = pref[8] - pref[1] = 39 - 7 = 32
                avg = 32 / 7 = 4 --> avgs[4] = 4

Final Answer:   [-1, -1, -1, 5, 4, 5, -1, -1, -1]
Final State:    avgs = [-1, -1, -1, 5, 4, 5, -1, -1, -1]
```

---

## 3. Kuriyama Mirai's Stones

**Problem Link:** [Kuriyama Mirai's Stones](https://codeforces.com/problemset/problem/433/B)

### Kuriyama Mirai's Stones (Prefix Sum & Sorting / Codeforces / 1200)

* **Core Invariant / Key Insight:** To handle queries on both the original array sequence and the non-decreasing sorted sequence efficiently, construct two separate 1-indexed prefix sum arrays: `pref1` for the original array and `pref2` for the sorted array.

* **Step-by-Step Logic:**
  * Read $N$ stone costs into array `v`. Build a 1-indexed prefix array `pref1` from `v`.
  * Create a copy of array `v`, sort it in non-decreasing order to obtain array `u`, and build a second 1-indexed prefix sum array `pref2` from `u`.
  * For each query `(type, l, r)`: if `type == 1`, output `pref1[r] - pref1[l - 1]`; if `type == 2`, output `pref2[r] - pref2[l - 1]`.

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        v = [6, 4, 2, 7, 2, 7]
                u (sorted) = [2, 2, 4, 6, 7, 7]

Step 1:         Build 1-indexed Prefix Arrays
                pref1 = [0, 6, 10, 12, 19, 21, 28]
                pref2 = [0, 2,  4,  8, 14, 21, 28]

Step 2:         Query (type = 2, l = 2, r = 8) --> Error/Out of bound adjustment
                Query (type = 2, l = 2, r = 5) on sorted array:
                pref2[5] - pref2[1] = 21 - 2 = 19

Step 3:         Query (type = 1, l = 1, r = 3) on original array:
                pref1[3] - pref1[0] = 12 - 0 = 12

Final Answer:   [19, 12]
Final State:    pref1 = [0, 6, 10, 12, 19, 21, 28]
                pref2 = [0, 2,  4,  8, 14, 21, 28]
```

---

## 4. Static Range Sum Queries

**Problem Link:** [Static Range Sum Queries](https://cses.fi/problemset/task/1646)

### Static Range Sum Queries (Prefix Sum / CSES / Introductory)

* **Core Invariant / Key Insight:** With static input arrays and $Q$ independent range sum queries, build a 1-indexed 64-bit prefix sum array to perform per-query range calculations via `pref[b] - pref[a - 1]` in $O(1)$ time per query.

* **Step-by-Step Logic:**
  * Read array size $N$ and number of queries $Q$, followed by $N$ elements.
  * Compute a 1-indexed prefix sum array `pref` using 64-bit integers (`long long` in C++ / `long` in Java).
  * Loop $Q$ times: read 1-indexed query bounds $a$ and $b$, then print `pref[b] - pref[a - 1]`.

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        arr = [3, 2, 4, 5, 1, 1, 5, 3]

Step 1:         Build 1-indexed Prefix Array
                pref = [0, 3, 5, 9, 14, 15, 16, 21, 24]

Step 2:         Query (a = 2, b = 4) --> Range [2, 4]
                pref[4] - pref[1] = 14 - 3 = 11

Step 3:         Query (a = 5, b = 6) --> Range [5, 6]
                pref[6] - pref[4] = 16 - 14 = 2

Final Answer:   [11, 2]
Final State:    pref = [0, 3, 5, 9, 14, 15, 16, 21, 24]
```