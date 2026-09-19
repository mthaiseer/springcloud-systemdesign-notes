# Form 2 — Prefix + Suffix / Split at `i`

## Table of Contents

- [Pattern Overview](#pattern-overview)
- [Pattern Recognition Signals](#pattern-recognition-signals)
- [Core Formulas](#core-formulas)
- [Decision Framework](#decision-framework)
- [1. Find Pivot Index](#1-find-pivot-index-prefix--suffix--leetcode--easy)
- [2. Minimum Average Difference](#2-minimum-average-difference-prefix--suffix--leetcode--medium)
- [3. Sum of Absolute Differences in a Sorted Array](#3-sum-of-absolute-differences-in-a-sorted-array-prefix-contribution--leetcode--medium)
- [4. Number of Ways to Split Array](#4-number-of-ways-to-split-array-prefix--suffix--leetcode--medium)
- [5. Array Division](#5-array-division-prefix-sum--codeforces--1400)

---

## Pattern Overview

**Form 2 — Prefix + Suffix / Split at `i`** appears when an array must be viewed as two parts around an index or split point.

```text
Array:

[ a0  a1  a2 ... ai ... an-1 ]
                  ^
                  i

Typical view:

LEFT                  RIGHT
[0 ........ i-1] | [i+1 ........ n-1]

or, for a split after i:

LEFT                  RIGHT
[0 ........ i]   | [i+1 ........ n-1]
```

Instead of repeatedly summing the left and right portions for every possible `i`, precompute or maintain prefix information so both sides can be obtained in `O(1)`.

The central transformation is:

```text
total = sum(all elements)

leftSum  = prefix information up to the split
rightSum = total - leftSum
```

This converts an `O(n²)` approach into `O(n)` in many problems.

### Pattern Recognition Signals

Look for wording such as:

- "left sum" and "right sum"
- "split the array"
- "choose an index `i`"
- "elements before/after `i`"
- "minimum difference between two parts"
- "number of valid splits"
- "sum of distances/differences to elements on the left and right"
- "can the array be divided into equal-sum parts?"

A useful contest question is:

```text
If I choose index i,
can I express the answer using:

prefix before/through i
+
total - prefix ?

If YES -> Prefix + Suffix / Split-at-i is a strong candidate.
```

### Core Formulas

For a 0-indexed array and prefix array

`pref[i + 1] = pref[i] + nums[i]`

we have:

`sum(0..i) = pref[i + 1]`

`sum(0..i-1) = pref[i]`

`sum(i+1..n-1) = pref[n] - pref[i + 1]`

For a split **after `i`**:

`leftSum = pref[i + 1]`

`rightSum = pref[n] - pref[i + 1]`

Often a full suffix array is unnecessary. One prefix sum plus `total` is enough.

### Decision Framework

```text
Problem mentions index/split
        |
        v
Do I need information from BOTH sides?
        |
      YES
        |
        v
Is that information additive?
(sum/count/contribution)
        |
      YES
        |
        v
Compute prefix / total
        |
        v
At each i:
left  = prefix(...)
right = total - left
        |
        v
Check condition / compute contribution
```

### Generic Pseudocode

```text
total = sum(array)
left = 0

for each valid index i:
    update/derive left
    right = total - left

    use(left, right, i)
```

**Typical Complexity**

- Prefix construction: `O(n)`
- Scan all split positions: `O(n)`
- Extra space: `O(n)` with a prefix array, or sometimes `O(1)` using a running sum

---

## 1. Find Pivot Index (Prefix + Suffix / LeetCode / Easy)

Problem Link: [Find Pivot Index](https://leetcode.com/problems/find-pivot-index/)

**Problem Summary:** Given an integer array, find the leftmost index where the sum of all elements strictly to its left equals the sum of all elements strictly to its right. Return `-1` if no such index exists.

### Find Pivot Index (Prefix + Suffix / LeetCode / Easy)

* **Core Invariant / Key Insight:** At index `i`, if `leftSum` is already known, then `rightSum = totalSum - leftSum - nums[i]`. A pivot exists when `leftSum == rightSum`.

* **Step-by-Step Logic:**
1. Compute `totalSum` of the entire array and initialize `leftSum = 0`.
2. For each index `i`, compute `rightSum = totalSum - leftSum - nums[i]` and compare it with `leftSum`.
3. If equal, return `i`; otherwise add `nums[i]` to `leftSum` and continue. The scan is `O(n)`.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        nums = [1, 7, 3, 6, 5, 6]
                totalSum = 28
                leftSum = 0

i = 0:          value = 1
                rightSum = 28 - 0 - 1 = 27
                0 != 27
                leftSum = 1

i = 1:          value = 7
                rightSum = 28 - 1 - 7 = 20
                1 != 20
                leftSum = 8

i = 2:          value = 3
                rightSum = 28 - 8 - 3 = 17
                8 != 17
                leftSum = 11

i = 3:          value = 6
                rightSum = 28 - 11 - 6 = 11

                LEFT          PIVOT       RIGHT
                [1,7,3]         6         [5,6]
                   11                         11

                leftSum == rightSum

Final Answer:   3
```

**Pseudocode:**

```text
totalSum = sum(nums)
leftSum = 0

for i = 0 to n - 1:
    rightSum = totalSum - leftSum - nums[i]

    if leftSum == rightSum:
        return i

    leftSum += nums[i]

return -1
```

---

## 2. Minimum Average Difference (Prefix + Suffix / LeetCode / Medium)

Problem Link: [Minimum Average Difference](https://leetcode.com/problems/minimum-average-difference/)

**Problem Summary:** For every index `i`, split the array into `[0..i]` and `[i+1..n-1]`, compute the integer average of both parts, and take their absolute difference. Return the index with the smallest difference.

### Minimum Average Difference (Prefix + Suffix / LeetCode / Medium)

* **Core Invariant / Key Insight:** Once `prefixSum` and `totalSum` are known, both side averages can be computed in `O(1)`: `leftAvg = leftSum / (i + 1)` and `rightAvg = rightSum / (n - i - 1)`. For the final index, the empty right side has average `0`.

* **Step-by-Step Logic:**
1. Compute `totalSum`, then scan from left to right while maintaining `leftSum`.
2. At each `i`, calculate `leftAvg`; derive `rightSum = totalSum - leftSum` and calculate `rightAvg`, using `0` when no right elements remain.
3. Compute `abs(leftAvg - rightAvg)` and keep the earliest index with the minimum difference. Overall complexity is `O(n)`.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        nums = [2, 5, 3, 9, 5, 3]
                totalSum = 27

i = 0:          leftSum  = 2
                leftAvg  = 2 / 1 = 2
                rightSum = 27 - 2 = 25
                rightAvg = 25 / 5 = 5
                diff     = |2 - 5| = 3

i = 1:          leftSum  = 7
                leftAvg  = 7 / 2 = 3
                rightSum = 20
                rightAvg = 20 / 4 = 5
                diff     = 2

i = 2:          leftSum  = 10
                leftAvg  = 10 / 3 = 3
                rightSum = 17
                rightAvg = 17 / 3 = 5
                diff     = 2

i = 3:          leftSum  = 19
                leftAvg  = 19 / 4 = 4
                rightSum = 8
                rightAvg = 8 / 2 = 4
                diff     = 0

                [2,5,3,9] | [5,3]
                  avg=4      avg=4

Final Answer:   3
Final Minimum:  0
```

**Pseudocode:**

```text
totalSum = sum(nums)
leftSum = 0

bestDiff = INF
answer = 0

for i = 0 to n - 1:
    leftSum += nums[i]
    leftAvg = leftSum / (i + 1)

    rightSum = totalSum - leftSum

    if i == n - 1:
        rightAvg = 0
    else:
        rightAvg = rightSum / (n - i - 1)

    diff = abs(leftAvg - rightAvg)

    if diff < bestDiff:
        bestDiff = diff
        answer = i

return answer
```

---

## 3. Sum of Absolute Differences in a Sorted Array (Prefix Contribution / LeetCode / Medium)

Problem Link: [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/)

**Problem Summary:** Given a sorted array, for every index `i`, compute the sum of `|nums[i] - nums[j]|` over all indices `j`. Return the resulting array.

### Sum of Absolute Differences in a Sorted Array (Prefix Contribution / LeetCode / Medium)

* **Core Invariant / Key Insight:** Because the array is sorted, every value on the left is `<= nums[i]` and every value on the right is `>= nums[i]`. Therefore absolute values disappear by side: `leftCost = nums[i] * i - leftSum` and `rightCost = rightSum - nums[i] * (n - i - 1)`.

* **Step-by-Step Logic:**
1. Compute the total sum and maintain `leftSum`, the sum of elements strictly before `i`.
2. At each index, derive `rightSum = totalSum - leftSum - nums[i]`, then compute the left and right contributions using counts instead of iterating over every element.
3. Set `ans[i] = leftCost + rightCost`, then add `nums[i]` to `leftSum`. Total complexity is `O(n)`.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        nums = [2, 3, 5]
                totalSum = 10
                leftSum = 0

i = 0, x = 2:
                leftCount  = 0
                leftCost   = 2 * 0 - 0 = 0

                rightSum   = 10 - 0 - 2 = 8
                rightCount = 2
                rightCost  = 8 - 2 * 2 = 4

                ans[0] = 0 + 4 = 4

                |2-2| + |2-3| + |2-5|
                  0   +   1   +   3 = 4

i = 1, x = 3:
                leftSum    = 2
                leftCost   = 3 * 1 - 2 = 1

                rightSum   = 10 - 2 - 3 = 5
                rightCost  = 5 - 3 * 1 = 2

                ans[1] = 1 + 2 = 3

i = 2, x = 5:
                leftSum    = 5
                leftCost   = 5 * 2 - 5 = 5
                rightCost  = 0

                ans[2] = 5

Final Answer:   [4, 3, 5]
```

**Pseudocode:**

```text
totalSum = sum(nums)
leftSum = 0

for i = 0 to n - 1:
    x = nums[i]

    leftCount = i
    leftCost = x * leftCount - leftSum

    rightSum = totalSum - leftSum - x
    rightCount = n - i - 1
    rightCost = rightSum - x * rightCount

    ans[i] = leftCost + rightCost

    leftSum += x

return ans
```

---

## 4. Number of Ways to Split Array (Prefix + Suffix / LeetCode / Medium)

Problem Link: [Number of Ways to Split Array](https://leetcode.com/problems/number-of-ways-to-split-array/)

**Problem Summary:** Count the split positions where both parts are non-empty and the sum of the left part is greater than or equal to the sum of the right part.

### Number of Ways to Split Array (Prefix + Suffix / LeetCode / Medium)

* **Core Invariant / Key Insight:** For a split after `i`, `leftSum` is the running prefix and `rightSum = totalSum - leftSum`. The split is valid exactly when `leftSum >= rightSum`.

* **Step-by-Step Logic:**
1. Compute `totalSum` and initialize `leftSum = 0`.
2. Scan only `i = 0..n-2` so that the right side remains non-empty; add `nums[i]` to `leftSum` and derive `rightSum`.
3. Increment the answer whenever `leftSum >= rightSum`. The algorithm runs in `O(n)` time and `O(1)` extra space.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        nums = [10, 4, -8, 7]
                totalSum = 13
                leftSum = 0

Split after i=0:
                leftSum  = 10
                rightSum = 13 - 10 = 3

                [10] | [4,-8,7]
                 10  >=  3       -> VALID

Split after i=1:
                leftSum  = 14
                rightSum = 13 - 14 = -1

                [10,4] | [-8,7]
                  14   >=   -1   -> VALID

Split after i=2:
                leftSum  = 6
                rightSum = 13 - 6 = 7

                [10,4,-8] | [7]
                     6    >=  7   -> FALSE

Final Answer:   2
```

**Pseudocode:**

```text
totalSum = sum(nums)
leftSum = 0
answer = 0

for i = 0 to n - 2:
    leftSum += nums[i]
    rightSum = totalSum - leftSum

    if leftSum >= rightSum:
        answer++

return answer
```

---

## 5. Array Division (Prefix Sum / Codeforces / 1400)

Problem Link: [Array Division](https://codeforces.com/problemset/problem/808/D)

**Problem Summary:** Determine whether an array can be divided into two contiguous parts with equal sums after moving at most one element from one part to the other while preserving the relative structure of the remaining elements. Output `YES` if it is possible, otherwise `NO`.

### Array Division (Prefix Sum / Codeforces / 1400)

* **Core Invariant / Key Insight:** If the total sum is odd, equal halves are impossible. For even total `S`, the target is `S/2`; while scanning a split, if one side exceeds the target by value `x`, success requires an occurrence of exactly `x` on the heavier side that can be moved across the split.

* **Step-by-Step Logic:**
1. Compute `totalSum`; if it is odd, output `NO`. Otherwise set `target = totalSum / 2`.
2. Scan split positions while maintaining `leftSum` and frequency information for values on the left and right. If `leftSum == target`, the array already has a valid division.
3. If `leftSum < target`, check whether the right side contains `target - leftSum`; if `leftSum > target`, check whether the left side contains `leftSum - target`. If any check succeeds output `YES`; otherwise output `NO` after the scan.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        arr = [1, 2, 3, 2]
                totalSum = 8
                target = 4

Start:
                left  = []
                right = [1,2,3,2]

Move 1 into left:

                left  = [1]       leftSum  = 1
                right = [2,3,2]   rightSum = 7

                left needs:
                target - leftSum
                = 4 - 1
                = 3

                Does RIGHT contain 3?
                YES

Move value 3 from right to left conceptually:

                left  sum = 1 + 3 = 4
                right sum = 7 - 3 = 4

                4 == 4

Final Answer:   YES
```

**Pseudocode:**

```text
totalSum = sum(arr)

if totalSum is odd:
    print NO
    stop

target = totalSum / 2

rightFreq = frequencies of all array values
leftFreq = empty frequency map
leftSum = 0

for i = 0 to n - 1:
    remove arr[i] from rightFreq

    leftSum += arr[i]
    add arr[i] to leftFreq

    if leftSum == target:
        print YES
        stop

    if leftSum < target:
        need = target - leftSum

        if rightFreq contains need:
            print YES
            stop

    else:
        extra = leftSum - target

        if leftFreq contains extra:
            print YES
            stop

print NO
```

---

## Pattern Revision Summary

```text
FORM 2: PREFIX + SUFFIX / SPLIT AT i

Core idea:

                 split
                   |
                   v
[ left portion ] | [ right portion ]

Know total / prefix information.

Then:

right = total - left
```

| Problem | Recognition Signal | Main Transformation |
|---|---|---|
| Find Pivot Index | left sum = right sum | `right = total - left - a[i]` |
| Minimum Average Difference | compare averages of two sides | derive both sums from prefix/total |
| Sum of Absolute Differences | contribution from values left/right | sorted order removes `abs()` by side |
| Number of Ways to Split Array | count valid left/right splits | `left >= total - left` |
| Array Division | equal halves after moving one value | target `total/2` + frequency lookup |

### Final Recognition Rule

```text
Whenever the problem says:

"For every index/split..."

and asks something involving:

LEFT side + RIGHT side

ask immediately:

Can I maintain LEFT with a prefix/running sum
and obtain RIGHT from TOTAL - LEFT?

If yes:
    think PREFIX + SUFFIX / SPLIT AT i.
```
