# PREFIX SUM PATTERNS

## Pattern 1 — Basic Range Sum / Static Queries

Pattern Link: [Pattern 1 — Basic Range Sum / Static Queries](https://github.com/mthaiseer/springcloud-systemdesign-notes/blob/main/001_CP_DSA_NOTES_V2/002_PREFIXSUM/002_PREFIXSUM_DIFF_ARRAY_PATTERNWISE_PROBLEMS.md#pattern-1)

### Pattern Overview

- **When to Recognize:** The array does **not change**, but you must answer many queries asking for the sum of a contiguous range `[l, r]`. A direct loop over every query can become too slow.
- **Core Idea:** Precompute cumulative sums once. Let `pref[i]` store the sum of the first `i` elements. Then any range sum is obtained by subtracting the prefix before the range: `sum(l, r) = pref[r] - pref[l - 1]`.
- **Why It Works:** `pref[r]` contains everything from index `1..r`; subtracting `pref[l - 1]` removes everything before `l`, leaving exactly `l..r`.
- **Standard Form:** Use a 1-indexed prefix array with `pref[0] = 0` and `pref[i] = pref[i - 1] + a[i]`.
- **Complexity:** Prefix construction takes `O(n)`. Each range query takes `O(1)`, so `q` queries take `O(n + q)` total instead of `O(nq)`.
- **Contest Signal:** Words such as **static array**, **many queries**, **sum from l to r**, **average of a fixed-radius window**, or **original vs sorted range sums** strongly suggest this pattern.

### Generic Visual

```text
Array index:    1   2   3   4   5
arr:            2   4   1   5   3

pref[0] = 0
pref:           0   2   6   7  12  15

Query [2, 4]:

pref[4] - pref[1]
   12   -    2
       = 10

Equivalent range:
4 + 1 + 5 = 10
```

### Generic Pseudocode

```text
pref[0] = 0

for i = 1 to n:
    pref[i] = pref[i - 1] + a[i]

for each query (l, r):
    answer = pref[r] - pref[l - 1]
    print answer
```

---

Problem Link: [Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/description/)

**Problem Summary:** Given an immutable integer array, repeatedly return the sum of elements between indices `left` and `right`, inclusive. Preprocess the array so each `sumRange(left, right)` query is fast.

### Range Sum Query - Immutable (Prefix Sum / LeetCode / Easy)

- **Core Invariant / Key Insight:** Store `pref[i + 1] = nums[0] + ... + nums[i]`. Then the zero-indexed range `[left, right]` is `pref[right + 1] - pref[left]`, giving each query in `O(1)`.

- **Step-by-Step Logic:**

1. Create `pref` of size `n + 1` with `pref[0] = 0`, then build `pref[i + 1] = pref[i] + nums[i]`.
2. For every `sumRange(left, right)`, take the cumulative sum through `right` and subtract everything before `left`.
3. Return `pref[right + 1] - pref[left]` in `O(1)` time after `O(n)` preprocessing.

- **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        nums = [-2, 0, 3, -5, 2, -1]

Step 1:         Build Prefix Array
                pref = [0, -2, -2, 1, -4, -2, -3]

Step 2:         Query (left = 0, right = 2) --> Range [0, 2]
                pref[3] - pref[0] = 1 - 0 = 1

Step 3:         Query (left = 2, right = 5) --> Range [2, 5]
                pref[6] - pref[2] = -3 - (-2) = -1

Final Answer:   sumRange(0,2) = 1
                sumRange(2,5) = -1

Final State:    pref = [0, -2, -2, 1, -4, -2, -3]
```

- **Pseudocode:**

```text
constructor(nums):
    pref[0] = 0

    for i = 0 to n - 1:
        pref[i + 1] = pref[i] + nums[i]

sumRange(left, right):
    return pref[right + 1] - pref[left]
```

---

Problem Link: [K Radius Subarray Averages](https://leetcode.com/problems/k-radius-subarray-averages/)

**Problem Summary:** For every index `i`, find the integer average of the subarray centered at `i` with radius `k`, containing `2k + 1` elements. If that complete window does not fit inside the array, output `-1` for that index.

### K Radius Subarray Averages (Prefix Sum / LeetCode / Medium)

- **Core Invariant / Key Insight:** A valid center `i` needs `i - k >= 0` and `i + k < n`. For a valid center, compute the window sum in `O(1)` as `pref[i + k + 1] - pref[i - k]`, then divide by `2k + 1`.

- **Step-by-Step Logic:**

1. Initialize every answer to `-1`, set `window = 2k + 1`, and build a prefix sum array using a wide integer type.
2. For each center `i` where the full interval `[i-k, i+k]` exists, compute its sum using the two prefix boundaries.
3. Set `ans[i] = sum / window`; invalid centers remain `-1`, giving `O(n)` total time.

- **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        nums = [7, 4, 3, 9, 1]
                k = 1
                window = 3

Step 1:         Build Prefix Array
                pref = [0, 7, 11, 14, 23, 24]

Step 2:         Center i = 1 --> Range [0, 2]
                pref[3] - pref[0] = 14 - 0 = 14
                average = 14 / 3 = 4

Step 3:         Center i = 2 --> Range [1, 3]
                pref[4] - pref[1] = 23 - 7 = 16
                average = 16 / 3 = 5

Step 4:         Center i = 3 --> Range [2, 4]
                pref[5] - pref[2] = 24 - 11 = 13
                average = 13 / 3 = 4

Final Answer:   [-1, 4, 5, 4, -1]
Final State:    pref = [0, 7, 11, 14, 23, 24]
```

- **Pseudocode:**

```text
ans = array of size n filled with -1
window = 2 * k + 1

pref[0] = 0
for i = 0 to n - 1:
    pref[i + 1] = pref[i] + nums[i]

for i = k to n - k - 1:
    left = i - k
    right = i + k

    sum = pref[right + 1] - pref[left]
    ans[i] = sum / window

return ans
```

---

Problem Link: [Kuriyama Mirai's Stones](https://codeforces.com/problemset/problem/433/B)

**Problem Summary:** Given stone costs and many queries, each query asks for the sum in `[l, r]` either in the original order or after sorting all costs. Output the requested range sum for every query.

### Kuriyama Mirai's Stones (Two Prefix Arrays / Codeforces / 1200)

- **Core Invariant / Key Insight:** Build one prefix array for the original costs and another for a sorted copy. A type `1` query uses `prefOriginal[r] - prefOriginal[l - 1]`; a type `2` query uses `prefSorted[r] - prefSorted[l - 1]`.

- **Step-by-Step Logic:**

1. Read the original array, copy and sort it, then build a 1-indexed prefix sum for both arrays.
2. For each query `(type, l, r)`, select the original prefix when `type = 1` or the sorted prefix when `type = 2`.
3. Output the chosen `pref[r] - pref[l - 1]` in `O(1)` per query; total complexity is `O(n log n + q)` because of sorting.

- **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        original = [6, 4, 2, 7]
                sorted   = [2, 4, 6, 7]

Step 1:         Build Prefix Arrays
                prefOriginal = [0, 6, 10, 12, 19]
                prefSorted   = [0, 2,  6, 12, 19]

Step 2:         Query (type = 1, l = 2, r = 3)
                Use original array
                prefOriginal[3] - prefOriginal[1]
                = 12 - 6 = 6

Step 3:         Query (type = 2, l = 2, r = 3)
                Use sorted array
                prefSorted[3] - prefSorted[1]
                = 12 - 2 = 10

Final Answer:   6
                10

Final State:    prefOriginal = [0, 6, 10, 12, 19]
                prefSorted   = [0, 2, 6, 12, 19]
```

- **Pseudocode:**

```text
read n
read original[1..n]

sorted = copy(original)
sort(sorted)

prefOriginal[0] = 0
prefSorted[0] = 0

for i = 1 to n:
    prefOriginal[i] = prefOriginal[i - 1] + original[i]
    prefSorted[i] = prefSorted[i - 1] + sorted[i]

read q

repeat q times:
    read type, l, r

    if type == 1:
        print prefOriginal[r] - prefOriginal[l - 1]
    else:
        print prefSorted[r] - prefSorted[l - 1]
```

---

Problem Link: [Static Range Sum Queries](https://cses.fi/problemset/task/1646)

**Problem Summary:** Given a static array of `n` values and `q` queries, each query asks for the sum of values from position `a` through `b`. Output every queried range sum.

### Static Range Sum Queries (Prefix Sum / CSES / Introductory)

- **Core Invariant / Key Insight:** Precompute `pref[i] = pref[i - 1] + x[i]`. Every 1-indexed query `[a, b]` is then exactly `pref[b] - pref[a - 1]`, avoiding a fresh loop through the range.

- **Step-by-Step Logic:**

1. Read the `n` values and construct a 1-indexed prefix array with `pref[0] = 0`.
2. For each query `(a, b)`, use the prefix value at `b` and subtract the prefix immediately before `a`.
3. Output `pref[b] - pref[a - 1]` in `O(1)` per query, for `O(n + q)` total time.

- **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        arr = [3, 2, 4, 5, 1]

Step 1:         Build 1-indexed Prefix Array
                pref = [0, 3, 5, 9, 14, 15]

Step 2:         Query (a = 2, b = 4) --> Range [2, 4]
                pref[4] - pref[1] = 14 - 3 = 11

Step 3:         Query (a = 1, b = 3) --> Range [1, 3]
                pref[3] - pref[0] = 9 - 0 = 9

Final Answer:   11
                9

Final State:    pref = [0, 3, 5, 9, 14, 15]
```

- **Pseudocode:**

```text
read n, q

pref[0] = 0

for i = 1 to n:
    read x
    pref[i] = pref[i - 1] + x

repeat q times:
    read a, b
    answer = pref[b] - pref[a - 1]
    print answer
```
