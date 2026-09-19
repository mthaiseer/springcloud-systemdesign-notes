# Form 4 — Prefix Modulo / Divisibility

## Table of Contents

- [Pattern Overview](#pattern-overview)
- [Recognition Signals](#recognition-signals)
- [Core Mathematics](#core-mathematics)
- [Generic Template](#generic-template)
- [1. Subarray Sums Divisible by K](#1-subarray-sums-divisible-by-k-prefix-modulo--leetcode--medium)
- [2. Continuous Subarray Sum](#2-continuous-subarray-sum-prefix-modulo--leetcode--medium)
- [3. Make Sum Divisible by P](#3-make-sum-divisible-by-p-prefix-modulo--leetcode--medium)
- [4. Subarray Divisibility](#4-subarray-divisibility-prefix-modulo--cses--sorting-and-searching)
- [Quick Comparison](#quick-comparison)

---

## Pattern Overview

**Prefix Modulo / Divisibility** is the prefix-sum pattern used when a problem asks whether a subarray sum is **divisible by `K`**, has a particular remainder, or must be removed so the remaining sum becomes divisible by some number.

For a prefix sum:

```text
pref[i] = a[0] + a[1] + ... + a[i]
```

The sum of subarray `[l ... r]` is:

```text
sum(l, r) = pref[r] - pref[l - 1]
```

If this subarray must be divisible by `K`:

```text
(pref[r] - pref[l - 1]) % K = 0
```

Therefore:

```text
pref[r] % K = pref[l - 1] % K
```

This gives the central invariant:

> **Two prefix sums having the same remainder modulo `K` imply that the sum between them is divisible by `K`.**

Instead of storing the complete prefix sum, store only:

```text
remainder = prefix % K
```

This reduces the problem to finding **equal or related prefix remainders**.

### Visual Idea

```text
Prefix positions:      0      1      2      3      4
Prefix sum:            0      4      9      9     11
Modulo K = 5:          0      4      4      4      1
                              ^             ^
                              |             |
                         same remainder = 4

Difference of prefix sums:
9 - 4 = 5
5 % 5 = 0

Therefore the elements between those prefix positions
form a subarray divisible by 5.
```

### Important Variations

```text
COUNT divisible subarrays
    -> store frequency of each remainder

EXISTENCE of divisible subarray
    -> store earliest index of each remainder

MINIMUM subarray to remove
    -> search for a required previous remainder

Negative remainder possible
    -> normalize with:
       ((prefix % K) + K) % K
```

---

## Recognition Signals

Think **Prefix Modulo** when the statement contains signals such as:

```text
"subarray sum divisible by K"
"sum is a multiple of K"
"remainder after division by K"
"make total sum divisible by P"
"remove the shortest subarray"
"count subarrays whose sum % K == 0"
```

Mental mapping:

```text
Subarray sum condition
        ↓
pref[r] - pref[l-1]
        ↓
Take modulo K
        ↓
Relation between prefix remainders
        ↓
Hash Map / Frequency Array
```

---

## Core Mathematics

### Case 1 — Subarray sum divisible by K

```text
(pref[r] - pref[l-1]) % K = 0

pref[r] % K = pref[l-1] % K
```

So we count previous prefixes having the **same remainder**.

### Case 2 — Remove a subarray so remaining sum is divisible by P

Let:

```text
total % P = target
```

We need the removed subarray to satisfy:

```text
subarraySum % P = target
```

For current prefix remainder `cur` and previous prefix remainder `prev`:

```text
(cur - prev) % P = target
```

Therefore:

```text
prev = (cur - target + P) % P
```

This is the key transformation used by **Make Sum Divisible by P**.

### Initial State

Always think about the empty prefix:

```text
prefix sum before processing anything = 0
remainder = 0
```

For counting:

```text
freq[0] = 1
```

For index-based problems:

```text
firstIndex[0] = -1
```

This allows subarrays beginning at index `0` to be handled naturally.

---

## Generic Template

### Counting divisible subarrays

```text
freq[0] = 1
prefix = 0
answer = 0

for each x in array:
    prefix += x
    rem = ((prefix % K) + K) % K

    answer += freq[rem]
    freq[rem]++

return answer
```

Why `answer += freq[rem]`?

```text
Every previous prefix with the same remainder
creates one new subarray ending at the current position
whose sum is divisible by K.
```

**Complexity:** `O(N)` time and `O(K)` or `O(N)` space depending on storage.

---

# Problems

---

Problem Link: [Subarray Sums Divisible by K](https://leetcode.com/problems/subarray-sums-divisible-by-k/)

**Problem Summary:** Given an integer array `nums` and an integer `k`, count the number of non-empty contiguous subarrays whose sum is divisible by `k`.

### 1. Subarray Sums Divisible by K (Prefix Modulo / LeetCode / Medium)

* **Core Invariant / Key Insight:** A subarray `[l ... r]` is divisible by `k` when its two boundary prefix sums have the same remainder: `pref[r] % k == pref[l - 1] % k`. Maintain the frequency of every remainder seen so far.

* **Step-by-Step Logic:**
1. Initialize `freq[0] = 1`, `prefix = 0`, and `ans = 0` so subarrays starting from index `0` are counted.
2. For each number, update `prefix`, compute normalized remainder `rem = ((prefix % k) + k) % k`, and add `freq[rem]` to `ans`.
3. Increment `freq[rem]`; after processing all elements, return `ans` in `O(N)` time.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        nums = [4, 5, 0, -2, -3, 1]
                k = 5

                freq[0] = 1
                prefix = 0
                ans = 0

Step 1:         x = 4
                prefix = 4
                rem = 4
                freq[4] = 0
                ans = 0
                freq[4] becomes 1

Step 2:         x = 5
                prefix = 9
                rem = 9 % 5 = 4

                Previous remainder 4 exists once
                ans += 1
                ans = 1

                Why?
                previous prefix = 4
                current prefix  = 9
                9 - 4 = 5 --> divisible by 5

                freq[4] becomes 2

Step 3:         x = 0
                prefix = 9
                rem = 4

                freq[4] = 2
                ans += 2
                ans = 3

Step 4:         x = -2
                prefix = 7
                rem = 2
                freq[2] = 0
                ans = 3
                freq[2] becomes 1

Step 5:         x = -3
                prefix = 4
                rem = 4

                freq[4] = 3
                ans += 3
                ans = 6

Step 6:         x = 1
                prefix = 5
                rem = 0

                freq[0] = 1
                ans += 1
                ans = 7

Final Answer:   7

Final State:    Same prefix remainder --> divisible difference

Pseudocode:

freq[0] = 1
prefix = 0
ans = 0

for x in nums:
    prefix += x
    rem = ((prefix % k) + k) % k
    ans += freq[rem]
    freq[rem]++

return ans
```

---

Problem Link: [Continuous Subarray Sum](https://leetcode.com/problems/continuous-subarray-sum/)

**Problem Summary:** Given `nums` and `k`, determine whether there exists a contiguous subarray of length at least `2` whose sum is a multiple of `k`.

### 2. Continuous Subarray Sum (Prefix Modulo / LeetCode / Medium)

* **Core Invariant / Key Insight:** Equal prefix remainders imply the elements between those prefix positions sum to a multiple of `k`. Because the subarray must have length at least `2`, store the **earliest index** of each remainder and require `i - first[rem] >= 2`.

* **Step-by-Step Logic:**
1. Initialize `first[0] = -1`, representing an empty prefix before the array starts.
2. Scan the array while maintaining `prefix % k`; if the remainder was seen before and the index distance is at least `2`, return `true`.
3. If a remainder is new, store only its earliest index because an earlier position gives the largest possible subarray length; return `false` if no valid pair is found.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        nums = [23, 2, 4, 6, 7]
                k = 6

                first[0] = -1
                prefix = 0

Step 1:         i = 0, x = 23
                prefix = 23
                rem = 23 % 6 = 5

                remainder 5 not seen
                first[5] = 0

Step 2:         i = 1, x = 2
                prefix = 25
                rem = 25 % 6 = 1

                remainder 1 not seen
                first[1] = 1

Step 3:         i = 2, x = 4
                prefix = 29
                rem = 29 % 6 = 5

                remainder 5 was first seen at index 0

                distance = 2 - 0 = 2
                distance >= 2 --> VALID

                Prefix difference:
                29 - 23 = 6
                6 % 6 = 0

                Corresponding subarray:
                nums[1 ... 2] = [2, 4]
                sum = 6

Final Answer:   true

Final State:    first occurrence of remainder 5 stays at index 0

Pseudocode:

first[0] = -1
prefix = 0

for i = 0 to n - 1:
    prefix += nums[i]
    rem = prefix % k

    if rem exists in first:
        if i - first[rem] >= 2:
            return true
    else:
        first[rem] = i

return false
```

---

Problem Link: [Make Sum Divisible by P](https://leetcode.com/problems/make-sum-divisible-by-p/)

**Problem Summary:** Given a positive integer array `nums` and integer `p`, remove the shortest contiguous subarray so that the sum of the remaining elements is divisible by `p`. Removing the entire array is not allowed.

### 3. Make Sum Divisible by P (Prefix Modulo / LeetCode / Medium)

* **Core Invariant / Key Insight:** If `total % p = target`, the removed subarray must also have remainder `target`. For current prefix remainder `cur`, search for a previous remainder `need = (cur - target + p) % p`; storing the **latest index** minimizes removal length.

* **Step-by-Step Logic:**
1. Compute `target = totalSum % p`; if `target == 0`, the array is already divisible and the answer is `0`.
2. Scan prefix remainders. For each current remainder `cur`, calculate `need = (cur - target + p) % p` and check whether `need` has appeared before.
3. Minimize `i - last[need]`, then store the latest index of `cur`; return the minimum length unless it equals `n`, in which case return `-1`.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        nums = [3, 1, 4, 2]
                p = 6

                total = 10
                target = 10 % 6 = 4

Goal:           Remove a subarray whose sum % 6 = 4

Why?
                remaining = total - removed

                remaining % 6 = 0
                (10 - removed) % 6 = 0

                Therefore:
                removed % 6 = 4

Initial Map:    last[0] = -1
                prefix = 0
                ans = 4

Step 1:         i = 0, x = 3
                prefix = 3
                cur = 3

                need = (3 - 4 + 6) % 6
                     = 5

                remainder 5 not found
                last[3] = 0

Step 2:         i = 1, x = 1
                prefix = 4
                cur = 4

                need = (4 - 4 + 6) % 6
                     = 0

                last[0] = -1
                length = 1 - (-1) = 2
                ans = 2

                Candidate removed subarray:
                [3, 1]
                sum = 4

                last[4] = 1

Step 3:         i = 2, x = 4
                prefix = 8
                cur = 8 % 6 = 2

                need = (2 - 4 + 6) % 6
                     = 4

                last[4] = 1
                length = 2 - 1 = 1
                ans = 1

                Candidate removed subarray:
                [4]
                sum = 4

                last[2] = 2

Step 4:         i = 3, x = 2
                prefix = 10
                cur = 4

                need = 0
                candidate length = 3 - (-1) = 4
                ans remains 1

Final Answer:   1

Final State:    Remove [4]
                remaining = [3, 1, 2]
                sum = 6 --> divisible by 6

Pseudocode:

target = totalSum(nums) % p

if target == 0:
    return 0

last[0] = -1
prefix = 0
ans = n

for i = 0 to n - 1:
    prefix = (prefix + nums[i]) % p

    need = (prefix - target + p) % p

    if need exists in last:
        ans = min(ans, i - last[need])

    last[prefix] = i

if ans == n:
    return -1

return ans
```

---

Problem Link: [Subarray Divisibility](https://cses.fi/problemset/task/1662)

**Problem Summary:** Given an array of `n` integers, count the number of contiguous subarrays whose sum is divisible by `n`.

### 4. Subarray Divisibility (Prefix Modulo / CSES / Sorting and Searching)

* **Core Invariant / Key Insight:** Here the divisor is exactly `n`. If two prefix sums have the same normalized remainder modulo `n`, their difference is divisible by `n`; therefore count equal prefix remainders using a frequency array.

* **Step-by-Step Logic:**
1. Initialize `freq[0] = 1`, `prefix = 0`, and use a 64-bit answer because the number of subarrays can be `O(N²)`.
2. For each element, update the prefix sum and normalize `rem = ((prefix % n) + n) % n`, which is essential because C++ `%` can produce negative remainders.
3. Add `freq[rem]` to the answer and increment `freq[rem]`; output the final count in `O(N)` time.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        n = 5
                arr = [3, 1, 2, 7, 4]

                divisor = n = 5
                freq[0] = 1
                prefix = 0
                ans = 0

Step 1:         x = 3
                prefix = 3
                rem = 3

                ans += freq[3] = 0
                ans = 0
                freq[3] = 1

Step 2:         x = 1
                prefix = 4
                rem = 4

                ans += freq[4] = 0
                ans = 0
                freq[4] = 1

Step 3:         x = 2
                prefix = 6
                rem = 1

                ans += freq[1] = 0
                ans = 0
                freq[1] = 1

Step 4:         x = 7
                prefix = 13
                rem = 3

                freq[3] = 1
                ans += 1
                ans = 1

                Equal remainder pair:
                earlier prefix remainder = 3
                current prefix remainder = 3

                Difference:
                13 - 3 = 10
                10 % 5 = 0

Step 5:         x = 4
                prefix = 17
                rem = 2

                ans += freq[2] = 0
                ans = 1
                freq[2] = 1

Final Answer:   1

Final State:    Count pairs of equal prefix remainders

Negative-value reminder:

                prefix = -2
                n = 5

                C++: -2 % 5 = -2

                Normalize:
                ((-2 % 5) + 5) % 5
                = 3

Pseudocode:

freq[0] = 1
prefix = 0
ans = 0

for x in arr:
    prefix += x
    rem = ((prefix % n) + n) % n

    ans += freq[rem]
    freq[rem]++

print ans
```

---

## Quick Comparison

| Problem | What is required? | Map stores | Remainder relation |
|---|---|---|---|
| Subarray Sums Divisible by K | Count subarrays | Frequency | `currentRem == previousRem` |
| Continuous Subarray Sum | Check existence + length >= 2 | Earliest index | `currentRem == previousRem` |
| Make Sum Divisible by P | Minimum removal | Latest index | `previousRem = (currentRem - target + p) % p` |
| Subarray Divisibility | Count subarrays | Frequency | `currentRem == previousRem` |

### Final Recognition Rule

```text
SUBARRAY + DIVISIBILITY / MODULO
              ↓
        Think PREFIX SUM
              ↓
       Take prefix % K
              ↓
    Ask what relation is needed
              ↓
   SAME remainder?
       |
       +--> count      --> frequency map
       |
       +--> existence  --> earliest index

   DIFFERENT required remainder?
       |
       +--> derive algebraically
            need = (current - target + K) % K
```

The most important equation to remember is:

```text
(pref[r] - pref[l-1]) % K = 0

              ⇕

pref[r] % K = pref[l-1] % K
```

That single transformation is the foundation of **Prefix Modulo / Divisibility** problems.
