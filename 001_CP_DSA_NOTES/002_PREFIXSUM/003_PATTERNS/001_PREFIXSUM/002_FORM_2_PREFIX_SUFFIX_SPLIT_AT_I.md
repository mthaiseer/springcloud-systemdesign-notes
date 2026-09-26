# PREFIX SUM PATTERNS

## Pattern 2 — Prefix + Suffix / Split at `i`

---

## Table of Contents

- [Pattern Overview](#pattern-overview)
- [Core Formula and Derivation](#core-formula-and-derivation)
- [Generic Dry Run](#generic-dry-run)
- [Problem 1 — Find Pivot Index](#problem-1--find-pivot-index)
- [Problem 2 — Minimum Average Difference](#problem-2--minimum-average-difference)
- [Problem 3 — Sum of Absolute Differences in a Sorted Array](#problem-3--sum-of-absolute-differences-in-a-sorted-array)
- [Problem 4 — Number of Ways to Split Array](#problem-4--number-of-ways-to-split-array)
- [Problem 5 — Array Division](#problem-5--array-division)
- [Fast Revision Model](#fast-revision-model)

---

## Pattern Overview

### What kind of problem is this?

Use this pattern when a problem asks you to evaluate an index or split using information from **both sides**.

```text
choose i / split
      +
need LEFT and RIGHT information
      ↓
PREFIX + TOTAL
```

Typical forms:

```text
strictly around i:

[0 ... i-1]  i  [i+1 ... n-1]

split after i:

[0 ... i] | [i+1 ... n-1]
```

Usually you do **not** need a separate suffix array:

```text
right = total - left
```

### Recognition Signals

```text
left sum / right sum
split after i
choose an index i
compare two sides
count valid splits
left/right contribution
```

### Complexity

```text
Compute total/prefix: O(n)
Scan all i:          O(n)
Total:               O(n)
```

---

## Core Formula and Derivation

For a split after `i`:

```text
total
= leftSum + rightSum

Therefore:

rightSum
= total - leftSum
```

If `i` itself is excluded from both side sums:

```text
total
= leftSum + a[i] + rightSum

Therefore:

rightSum
= total - leftSum - a[i]
```

Mental model:

```text
KNOW TOTAL
   +
MAINTAIN LEFT
   ↓
RIGHT = TOTAL - USED PART
```

---

## Generic Dry Run

```text
a = [2,4,1,5,3]
total = 15

split after i = 2

LEFT            RIGHT
[2,4,1]       | [5,3]

leftSum = 7

rightSum
= total - leftSum
= 15 - 7
= 8
```

### Generic Pseudocode

```text
total = sum(a)
left = 0

for each valid i:
    left += a[i]
    right = total - left

    evaluate(left, right, i)
```

---

# Problem 1 — Find Pivot Index

Problem Link: [Find Pivot Index](https://leetcode.com/problems/find-pivot-index/)

### What is the problem asking?

Given an integer array, find the leftmost index where the sum of all elements strictly to its left equals the sum of all elements strictly to its right. Return `-1` if no such index exists.

### Observation

```text
INDEX + SUM STRICTLY LEFT == SUM STRICTLY RIGHT → TOTAL + RUNNING LEFT
```

### Compact Algebra Derivation

```text
At index i:

total
= leftSum + nums[i] + rightSum

Therefore:

rightSum
= total - leftSum - nums[i]

Pivot condition:

leftSum = rightSum

leftSum
= total - leftSum - nums[i]
```

### Simple Dry Run

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

### Pseudocode

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

### C++

```cpp
class Solution {
public:
    int pivotIndex(vector<int>& nums) {
        long long total = accumulate(nums.begin(), nums.end(), 0LL);
        long long left = 0;

        for (int i = 0; i < (int)nums.size(); ++i) {
            long long right = total - left - nums[i];
            if (left == right) return i;
            left += nums[i];
        }
        return -1;
    }
};
```

### Complexity

```text
Time:  O(n)
Space: O(1) extra in the basic running-sum forms
       O(n) where output/frequency storage is required
```

---


# Problem 2 — Minimum Average Difference

Problem Link: [Minimum Average Difference](https://leetcode.com/problems/minimum-average-difference/)

### What is the problem asking?

For every index `i`, split the array into `[0..i]` and `[i+1..n-1]`, compute the integer average of both parts, and take their absolute difference. Return the index with the smallest difference.

### Observation

```text
TRY EVERY SPLIT + COMPARE LEFT/RIGHT AVERAGES → PREFIX/TOTAL
```

### Compact Algebra Derivation

```text
Split after i:

leftCount  = i + 1
rightCount = n - i - 1

leftSum  = sum(0..i)
rightSum = total - leftSum

leftAvg  = leftSum / (i+1)

if rightCount > 0:
    rightAvg = rightSum / rightCount
else:
    rightAvg = 0

difference
= |leftAvg - rightAvg|
```

### Simple Dry Run

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

### Pseudocode

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

### C++

```cpp
class Solution {
public:
    int minimumAverageDifference(vector<int>& nums) {
        int n = nums.size();
        long long total = accumulate(nums.begin(), nums.end(), 0LL);
        long long left = 0, best = LLONG_MAX;
        int ans = 0;

        for (int i = 0; i < n; ++i) {
            left += nums[i];
            long long right = total - left;

            long long leftAvg = left / (i + 1);
            long long rightAvg = (i == n - 1) ? 0 : right / (n - i - 1);
            long long diff = llabs(leftAvg - rightAvg);

            if (diff < best) {
                best = diff;
                ans = i;
            }
        }
        return ans;
    }
};
```

### Complexity

```text
Time:  O(n)
Space: O(1) extra in the basic running-sum forms
       O(n) where output/frequency storage is required
```

---


# Problem 3 — Sum of Absolute Differences in a Sorted Array

Problem Link: [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/)

### What is the problem asking?

Given a sorted array, for every index `i`, compute the sum of `|nums[i] - nums[j]|` over all indices `j`. Return the resulting array.

### Observation

```text
SORTED ARRAY + SUM OF |a[i]-a[j]| → LEFT/RIGHT CONTRIBUTION
```

### Compact Algebra Derivation

```text
x = nums[i]

Because nums is sorted:

LEFT values <= x
RIGHT values >= x

Left contribution:

(x-a[0]) + ... + (x-a[i-1])

= x*i - (a[0]+...+a[i-1])

= x*i - leftSum

Right contribution:

(a[i+1]-x) + ... + (a[n-1]-x)

= rightSum - x*(n-i-1)

answer[i]
= leftCost + rightCost
```

### Simple Dry Run

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

### Pseudocode

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

### C++

```cpp
class Solution {
public:
    vector<int> getSumAbsoluteDifferences(vector<int>& nums) {
        int n = nums.size();
        long long total = accumulate(nums.begin(), nums.end(), 0LL);
        long long leftSum = 0;
        vector<int> ans(n);

        for (int i = 0; i < n; ++i) {
            long long x = nums[i];
            long long rightSum = total - leftSum - x;

            long long leftCost = x * i - leftSum;
            long long rightCost = rightSum - x * (n - i - 1);

            ans[i] = (int)(leftCost + rightCost);
            leftSum += x;
        }
        return ans;
    }
};
```

### Complexity

```text
Time:  O(n)
Space: O(1) extra in the basic running-sum forms
       O(n) where output/frequency storage is required
```

---


# Problem 4 — Number of Ways to Split Array

Problem Link: [Number of Ways to Split Array](https://leetcode.com/problems/number-of-ways-to-split-array/)

### What is the problem asking?

Count the split positions where both parts are non-empty and the sum of the left part is greater than or equal to the sum of the right part.

### Observation

```text
COUNT SPLITS WHERE LEFT SUM >= RIGHT SUM → RUNNING LEFT + TOTAL
```

### Compact Algebra Derivation

```text
Split after i:

leftSum  = sum(0..i)
rightSum = total - leftSum

Need:

leftSum >= rightSum

Substitute rightSum:

leftSum >= total - leftSum

2*leftSum >= total
```

### Simple Dry Run

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

### Pseudocode

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

### C++

```cpp
class Solution {
public:
    int waysToSplitArray(vector<int>& nums) {
        long long total = accumulate(nums.begin(), nums.end(), 0LL);
        long long left = 0;
        int ans = 0;

        for (int i = 0; i < (int)nums.size() - 1; ++i) {
            left += nums[i];
            long long right = total - left;
            if (left >= right) ++ans;
        }
        return ans;
    }
};
```

### Complexity

```text
Time:  O(n)
Space: O(1) extra in the basic running-sum forms
       O(n) where output/frequency storage is required
```

---


# Problem 5 — Array Division

Problem Link: [Array Division](https://codeforces.com/problemset/problem/808/D)

### What is the problem asking?

Determine whether an array can be divided into two contiguous parts with equal sums after moving at most one element from one part to the other while preserving the relative structure of the remaining elements. Output `YES` if it is possible, otherwise `NO`.

### Observation

```text
EQUAL-SUM SPLIT + MOVE AT MOST ONE VALUE → TARGET S/2 + FREQUENCY LOOKUP
```

### Compact Algebra Derivation

```text
Let total sum = S.

Equal final parts require:

leftFinal = rightFinal = S/2

So S must be even.

target = S/2

Case 1:
leftSum < target

Need a value x from RIGHT:

leftSum + x = target

x = target - leftSum

Case 2:
leftSum > target

Need a value x from LEFT:

leftSum - x = target

x = leftSum - target
```

### Simple Dry Run

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

### Pseudocode

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

### C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> a(n);
    long long total = 0;
    unordered_map<long long, int> rightFreq, leftFreq;

    for (auto &x : a) {
        cin >> x;
        total += x;
        rightFreq[x]++;
    }

    if (total % 2 != 0) {
        cout << "NO\n";
        return 0;
    }

    long long target = total / 2;
    long long leftSum = 0;

    for (long long x : a) {
        if (--rightFreq[x] == 0) rightFreq.erase(x);

        leftSum += x;
        leftFreq[x]++;

        if (leftSum == target) {
            cout << "YES\n";
            return 0;
        }

        if (leftSum < target) {
            long long need = target - leftSum;
            if (rightFreq.count(need)) {
                cout << "YES\n";
                return 0;
            }
        } else {
            long long extra = leftSum - target;
            if (leftFreq.count(extra)) {
                cout << "YES\n";
                return 0;
            }
        }
    }

    cout << "NO\n";
}
```

### Complexity

```text
Time:  O(n)
Space: O(1) extra in the basic running-sum forms
       O(n) where output/frequency storage is required
```

---


# Fast Revision Model

## One Core Transformation

```text
split after i:

leftSum  = sum(0..i)
rightSum = total - leftSum
```

For a pivot where `a[i]` belongs to neither side:

```text
rightSum = total - leftSum - a[i]
```

## What changes between the problems?

| Problem | What is actually being asked? | Extra idea |
|---|---|---|
| Find Pivot Index | Find `i` where strict left sum = strict right sum | Exclude `a[i]` |
| Minimum Average Difference | Compare averages after every split | Divide by side counts |
| Sum of Absolute Differences | Sum distance from `a[i]` to all values | Sorted contribution algebra |
| Number of Ways to Split Array | Count splits with `left >= right` | Scan only to `n-2` |
| Array Division | Make both sides equal by moving at most one value | `total/2` + frequency lookup |

## 5-Minute Recognition

```text
1. WHAT?
   Problem asks about every index / every split.

2. BOTH SIDES?
   Need information from LEFT and RIGHT.

3. ADDITIVE?
   Sum / count / contribution can be maintained.

4. MODEL
   Maintain LEFT.
   Derive RIGHT from TOTAL.

5. FORMULA
   right = total - left
```

## Recognition Rule

```text
EVERY INDEX / SPLIT
       +
NEED LEFT + RIGHT
       ↓
PREFIX / RUNNING SUM + TOTAL
```
