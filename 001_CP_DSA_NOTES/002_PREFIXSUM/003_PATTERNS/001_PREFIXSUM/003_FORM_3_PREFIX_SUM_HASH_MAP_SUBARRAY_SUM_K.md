# Form 3 — Prefix Sum + Hash Map: Subarray Sum = K

## Table of Contents

- [Pattern Overview](#pattern-overview)
- [Recognition Signals](#recognition-signals)
- [Core Derivation](#core-derivation)
- [Generic Pseudocode](#generic-pseudocode)
- [1. Subarray Sum Equals K](#1-subarray-sum-equals-k-prefix-sum--hash-map--leetcode--medium)
- [2. Binary Subarrays With Sum](#2-binary-subarrays-with-sum-prefix-sum--hash-map--leetcode--medium)
- [3. Maximum Size Subarray Sum Equals k](#3-maximum-size-subarray-sum-equals-k-prefix-sum--hash-map--leetcode--medium)
- [4. Subarray Sums II](#4-subarray-sums-ii-prefix-sum--hash-map--cses--medium)
- [5. Good Subarrays](#5-good-subarrays-prefix-transformation--frequency-map--codeforces--1600)
- [Pattern Comparison](#pattern-comparison)

---

## Pattern Overview

This form is used when we need to find or count **contiguous subarrays whose sum satisfies an exact target condition**, especially when the array may contain negative numbers and a sliding window is therefore unsafe.

For a prefix sum array:

```text
pref[i] = a[1] + a[2] + ... + a[i]
```

The sum of subarray `[l..r]` is:

```text
sum(l, r) = pref[r] - pref[l - 1]
```

If we want:

```text
sum(l, r) = K
```

then:

```text
pref[r] - pref[l - 1] = K
pref[l - 1] = pref[r] - K
```

So while scanning `r`, we ask:

> **How many earlier prefix sums are equal to `currentPrefix - K`?**

A hash map answers this in expected `O(1)` time.

### Recognition Signals

```text
"number of subarrays with sum K"
"find a subarray whose sum is exactly X"
"longest subarray with sum K"
negative values are possible
many possible left boundaries for each right boundary
condition can be rewritten using two prefix states
```

### Core Derivation

```text
Current prefix = P
Target sum     = K

Need:
P - oldPrefix = K

Therefore:
oldPrefix = P - K
```

For **counting**, store frequencies:

```text
freq[prefix] = how many times this prefix has appeared
answer += freq[currentPrefix - K]
```

For **maximum length**, store the earliest index:

```text
first[prefix] = earliest index where prefix appeared
length = i - first[currentPrefix - K]
```

Initialize the empty prefix before processing the array:

```text
freq[0] = 1
first[0] = 0
```

This allows a valid subarray beginning at index `1` to be counted naturally.

### Generic Pseudocode

```text
freq[0] = 1
prefix = 0
answer = 0

for each value x:
    prefix += x

    need = prefix - K
    answer += freq[need]

    freq[prefix]++

return answer
```

**Complexity:** `O(n)` expected time and `O(n)` extra space.

---

Problem Link: [Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/)

**Problem Summary:** Given an integer array and integer `k`, count the number of contiguous non-empty subarrays whose sum is exactly `k`.

### 1. Subarray Sum Equals K (Prefix Sum / Hash Map / LeetCode / Medium)

* **Core Invariant / Key Insight:** At current prefix sum `P`, every previous prefix equal to `P - k` creates one subarray ending here with sum `k`. Therefore add `freq[P - k]` before inserting the current prefix.

* **Step-by-Step Logic:**
1. Initialize `freq[0] = 1`, `prefix = 0`, and `answer = 0`.
2. For every number, update `prefix`, compute `need = prefix - k`, and add `freq[need]` to the answer.
3. Increment `freq[prefix]`; after one scan, output `answer` in expected `O(n)` time.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        nums = [1, 1, 1], k = 2
                freq = {0:1}
                prefix = 0, answer = 0

Step 1:         x = 1
                prefix = 1
                need = 1 - 2 = -1
                freq[-1] = 0
                answer = 0
                freq[1]++

Step 2:         x = 1
                prefix = 2
                need = 2 - 2 = 0
                freq[0] = 1
                answer = 1
                freq[2]++

Step 3:         x = 1
                prefix = 3
                need = 3 - 2 = 1
                freq[1] = 1
                answer = 2
                freq[3]++

Final Answer:   2
Final State:    freq = {0:1, 1:1, 2:1, 3:1}
```

* **Pseudocode:**

```text
freq[0] = 1
prefix = 0
answer = 0

for x in nums:
    prefix += x
    answer += freq[prefix - k]
    freq[prefix]++

return answer
```

---

Problem Link: [Binary Subarrays With Sum](https://leetcode.com/problems/binary-subarrays-with-sum/)

**Problem Summary:** Given a binary array and target `goal`, count contiguous subarrays whose element sum is exactly `goal`.

### 2. Binary Subarrays With Sum (Prefix Sum / Hash Map / LeetCode / Medium)

* **Core Invariant / Key Insight:** The binary restriction does not change the prefix equation: for current prefix `P`, a valid left prefix must equal `P - goal`. Repeated prefix sums are especially common because zeros do not change the running sum.

* **Step-by-Step Logic:**
1. Start with `freq[0] = 1`, then scan the binary array while maintaining the running prefix sum.
2. At each index, add `freq[prefix - goal]` because each matching earlier prefix defines a valid subarray ending at the current index.
3. Record the current prefix frequency and return the total in expected `O(n)` time.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        nums = [1, 0, 1, 0, 1], goal = 2
                freq = {0:1}
                prefix = 0, answer = 0

Step 1:         x = 1 --> prefix = 1
                need = -1 --> +0
                freq[1] = 1

Step 2:         x = 0 --> prefix = 1
                need = -1 --> +0
                freq[1] = 2

Step 3:         x = 1 --> prefix = 2
                need = 0 --> +1
                answer = 1
                freq[2] = 1

Step 4:         x = 0 --> prefix = 2
                need = 0 --> +1
                answer = 2
                freq[2] = 2

Step 5:         x = 1 --> prefix = 3
                need = 1 --> +2
                answer = 4

Final Answer:   4
Final State:    repeated prefix sums created multiple valid left boundaries
```

* **Pseudocode:**

```text
freq[0] = 1
prefix = 0
answer = 0

for bit in nums:
    prefix += bit
    answer += freq[prefix - goal]
    freq[prefix]++

return answer
```

---

Problem Link: [Maximum Size Subarray Sum Equals k](https://leetcode.com/problems/maximum-size-subarray-sum-equals-k/)

**Problem Summary:** Given an integer array and target `k`, find the maximum length of a contiguous subarray whose sum equals `k`.

### 3. Maximum Size Subarray Sum Equals k (Prefix Sum / Hash Map / LeetCode / Medium)

* **Core Invariant / Key Insight:** We still need an earlier prefix equal to `P - k`, but now we want the **earliest** such index because it gives the longest subarray. Store the first occurrence of every prefix and never overwrite it.

* **Step-by-Step Logic:**
1. Store `first[0] = 0` and process the array with 1-indexed positions while maintaining `prefix`.
2. If `prefix - k` exists at index `j`, update `best = max(best, i - j)`.
3. Store `first[prefix] = i` only if this prefix has never appeared before; return `best` in expected `O(n)` time.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        nums = [1, -1, 5, -2, 3], k = 3
                first = {0:0}
                prefix = 0, best = 0

Step 1:         i=1, x=1  --> prefix=1
                need=-2 not found
                first[1]=1

Step 2:         i=2, x=-1 --> prefix=0
                need=-3 not found
                keep first[0]=0

Step 3:         i=3, x=5  --> prefix=5
                need=2 not found
                first[5]=3

Step 4:         i=4, x=-2 --> prefix=3
                need=0 found at index 0
                length = 4 - 0 = 4
                best = 4

Step 5:         i=5, x=3  --> prefix=6
                need=3 found at index 4
                length = 1
                best remains 4

Final Answer:   4
Final State:    longest valid subarray = [1, -1, 5, -2]
```

* **Pseudocode:**

```text
first[0] = 0
prefix = 0
best = 0

for i = 1 to n:
    prefix += nums[i]

    need = prefix - k
    if need exists in first:
        best = max(best, i - first[need])

    if prefix not in first:
        first[prefix] = i

return best
```

---

Problem Link: [Subarray Sums II](https://cses.fi/problemset/task/1661)

**Problem Summary:** Given `n`, target `x`, and an integer array that may contain negative values, count all contiguous subarrays whose sum is exactly `x`.

### 4. Subarray Sums II (Prefix Sum / Hash Map / CSES / Medium)

* **Core Invariant / Key Insight:** For current prefix `P`, each earlier occurrence of `P - x` gives one subarray with sum `x`. Negative values make ordinary two pointers unreliable, so prefix frequencies are the natural `O(n)` solution.

* **Step-by-Step Logic:**
1. Initialize `freq[0] = 1`, then scan the array and accumulate `prefix` using 64-bit integers.
2. For each element, add `freq[prefix - x]` to the answer before recording the current prefix.
3. Increment `freq[prefix]` and output the final 64-bit count in expected `O(n)` time.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        arr = [2, -1, 3, 5, -2], x = 7
                freq = {0:1}
                prefix = 0, answer = 0

Step 1:         +2  --> prefix = 2
                need = -5 --> +0

Step 2:         -1  --> prefix = 1
                need = -6 --> +0

Step 3:         +3  --> prefix = 4
                need = -3 --> +0

Step 4:         +5  --> prefix = 9
                need = 2
                prefix 2 occurred once
                answer = 1       // [-1, 3, 5]

Step 5:         -2  --> prefix = 7
                need = 0
                prefix 0 occurred once
                answer = 2       // whole array

Final Answer:   2
Final State:    every valid subarray came from a previous prefix = currentPrefix - 7
```

* **Pseudocode:**

```text
freq[0] = 1
prefix = 0
answer = 0

for x_i in arr:
    prefix += x_i
    answer += freq[prefix - target]
    freq[prefix]++

print answer
```

---

Problem Link: [Good Subarrays](https://codeforces.com/problemset/problem/1398/C)

**Problem Summary:** Given a digit array, count subarrays whose sum equals their length. Transform the condition so that valid subarrays correspond to equal transformed prefix values.

### 5. Good Subarrays (Prefix Transformation / Frequency Map / Codeforces / 1600)

* **Core Invariant / Key Insight:** A subarray `[l..r]` is good when `pref[r] - pref[l-1] = r-l+1`. Rearranging gives `pref[r] - r = pref[l-1] - (l-1)`, so two equal values of `pref[i] - i` define a good subarray. citeturn0search9

* **Step-by-Step Logic:**
1. Maintain digit prefix sum `pref` and initialize `freq[0] = 1`, representing `pref[0] - 0`.
2. At position `i`, compute transformed prefix `key = pref - i`; every previous occurrence of the same `key` creates one good subarray ending at `i`.
3. Add `freq[key]` to the answer, increment `freq[key]`, and process all positions in expected `O(n)` time.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        digits = [1, 2, 0]
                Condition: subarray sum = subarray length
                freq = {0:1}
                pref = 0, answer = 0

Transformation:
                pref[r] - pref[l-1] = r-l+1

                pref[r] - r
                    =
                pref[l-1] - (l-1)

Step 1:         i=1, digit=1
                pref = 1
                key = pref-i = 1-1 = 0
                freq[0] = 1 --> answer = 1
                freq[0] = 2

Step 2:         i=2, digit=2
                pref = 3
                key = 3-2 = 1
                freq[1] = 0 --> answer = 1
                freq[1] = 1

Step 3:         i=3, digit=0
                pref = 3
                key = 3-3 = 0
                freq[0] = 2 --> answer = 3
                freq[0] = 3

Valid:          [1]
                [2,0]
                [1,2,0]

Final Answer:   3
Final State:    equal transformed prefixes pref[i]-i identify good subarrays
```

* **Pseudocode:**

```text
freq[0] = 1
pref = 0
answer = 0

for i = 1 to n:
    pref += digit[i]
    key = pref - i

    answer += freq[key]
    freq[key]++

print answer
```

---

## Pattern Comparison

| Problem | What the map stores | Lookup condition | Result |
|---|---|---|---|
| Subarray Sum Equals K | Frequency of prefix sums | `P - k` | Count |
| Binary Subarrays With Sum | Frequency of prefix sums | `P - goal` | Count |
| Maximum Size Subarray Sum Equals k | Earliest prefix index | `P - k` | Maximum length |
| Subarray Sums II | Frequency of prefix sums | `P - x` | Count |
| Good Subarrays | Frequency of `pref[i] - i` | Same transformed value | Count |

## Final Recognition Rule

```text
If a subarray condition can be rewritten as:

STATE(r) = STATE(l - 1)

or

PREFIX(l - 1) = PREFIX(r) - TARGET

think:

PREFIX SUM + HASH MAP
```

The most important contest question is not *"Can I build a prefix sum?"* but:

```text
What previous prefix state must exist
for a subarray ending HERE to be valid?
```
