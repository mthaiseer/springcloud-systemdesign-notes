# Prefix Sum Phase-Wise Problem Practice Guide — ALL Dry Runs in Array Diagram Style

> Goal: Learn prefix sum from beginner to advanced by solving problems phase by phase.
>
> Each problem includes:
>
> - Problem statement
> - Example input / output
> - Pattern recognition
> - Result
> - C++ code
> - Step-by-step dry run using array / matrix diagrams exactly like: index line, array line, pointer `^`, state before, action, state after, answer/result

---

# Clickable Index

## Phase 0 — Core Formula

- [0.1 Prefix Sum Formula](#01-prefix-sum-formula)
- [0.2 Range Sum Formula](#02-range-sum-formula)

## Phase 1 — Basic Prefix Sum

- [1. Running Sum of Array](#1-running-sum-of-array)
- [2. Range Sum Query Immutable](#2-range-sum-query-immutable)
- [3. Find Pivot Index](#3-find-pivot-index)
- [4. Maximum Average Subarray I](#4-maximum-average-subarray-i)

## Phase 2 — Prefix Sum + HashMap

- [5. Subarray Sum Equals K](#5-subarray-sum-equals-k)
- [6. Maximum Size Subarray Sum Equals K](#6-maximum-size-subarray-sum-equals-k)
- [7. Count Binary Subarrays With Sum](#7-count-binary-subarrays-with-sum)

## Phase 3 — Prefix Modulo

- [8. Subarray Sums Divisible by K](#8-subarray-sums-divisible-by-k)
- [9. Continuous Subarray Sum](#9-continuous-subarray-sum)
- [10. Make Sum Divisible by P](#10-make-sum-divisible-by-p)

## Phase 4 — Prefix Balance / Parity

- [11. Contiguous Array](#11-contiguous-array)
- [12. Count Number of Nice Subarrays](#12-count-number-of-nice-subarrays)
- [13. Longest Well-Performing Interval](#13-longest-well-performing-interval)

## Phase 5 — Difference Array

- [14. Range Addition](#14-range-addition)
- [15. Corporate Flight Bookings](#15-corporate-flight-bookings)
- [16. Car Pooling](#16-car-pooling)

## Phase 6 — 2D Prefix Sum

- [17. Range Sum Query 2D Immutable](#17-range-sum-query-2d-immutable)
- [18. Matrix Block Sum](#18-matrix-block-sum)
- [19. Largest Magic Square](#19-largest-magic-square)

## Phase 7 — Advanced Prefix

- [20. Shortest Subarray With Sum At Least K](#20-shortest-subarray-with-sum-at-least-k)
- [21. Count of Range Sum](#21-count-of-range-sum)
- [22. Number of Wonderful Substrings](#22-number-of-wonderful-substrings)

---

# Phase 0 — Core Formula

## 0.1 Prefix Sum Formula

```text
pref[i] = a[0] + a[1] + ... + a[i]
```

Most contest code uses 1-indexed prefix:

```text
pref[0] = 0
pref[i] = pref[i - 1] + a[i - 1]
```

Example:

```text
a    = [4, 2, 3, 1]
pref = [0, 4, 6, 9, 10]
```

---

## 0.2 Range Sum Formula

For 0-indexed array and 1-indexed prefix:

```text
sum(l, r) = pref[r + 1] - pref[l]
```

Example:

```text
a = [4, 2, 3, 1]
query sum(1, 3)
answer = 2 + 3 + 1 = 6
using prefix = pref[4] - pref[1] = 10 - 4 = 6
```

---

# Phase 1 — Basic Prefix Sum

Goal:

```text
Use cumulative sum to answer direct range questions.
```

Recognition:

```text
range sum
subarray sum
left sum / right sum
total before index
```

---

# 1. Running Sum of Array

## Problem Statement

Given an array `nums`, return the running sum where:

```text
runningSum[i] = nums[0] + nums[1] + ... + nums[i]
```

## Example

```text
Input : nums = [1, 2, 3, 4]
Output: [1, 3, 6, 10]
```

## Pattern

```text
Basic prefix construction.
```

## Result

```text
[1, 3, 6, 10]
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> runningSum(vector<int>& nums) {
    for (int i = 1; i < nums.size(); i++) {
        nums[i] += nums[i - 1];
    }
    return nums;
}

int main() {
    vector<int> nums = {1, 2, 3, 4};
    vector<int> ans = runningSum(nums);

    for (int x : ans) cout << x << " ";
    return 0;
}
```

## Dry Run — Array Diagram

```text
nums = [1, 2, 3, 4]
Goal: convert nums[i] into sum from 0..i

Initial:
index:   0   1   2   3
nums :  [1,  2,  3,  4]

Previous prefix is always stored at nums[i - 1]
```

```text
i = 1, current = nums[1] = 2
index:   0   1   2   3
nums :  [1,  2,  3,  4]
             ^

before      : nums[1] = 2
previous sum: nums[0] = 1
action      : nums[1] = nums[1] + nums[0]
calculation : 2 + 1 = 3
after       : [1, 3, 3, 4]
```

```text
i = 2, current = nums[2] = 3
index:   0   1   2   3
nums :  [1,  3,  3,  4]
                 ^

before      : nums[2] = 3
previous sum: nums[1] = 3
action      : nums[2] = nums[2] + nums[1]
calculation : 3 + 3 = 6
after       : [1, 3, 6, 4]
```

```text
i = 3, current = nums[3] = 4
index:   0   1   2   3
nums :  [1,  3,  6,  4]
                     ^

before      : nums[3] = 4
previous sum: nums[2] = 6
action      : nums[3] = nums[3] + nums[2]
calculation : 4 + 6 = 10
after       : [1, 3, 6, 10]

Final answer: [1, 3, 6, 10]
```

---

# 2. Range Sum Query Immutable

## Problem Statement

Given an integer array `nums`, answer many queries:

```text
sumRange(left, right)
```

Return the sum from index `left` to `right` inclusive.

## Example

```text
nums = [-2, 0, 3, -5, 2, -1]
query = sumRange(0, 2)
output = 1
```

Because:

```text
-2 + 0 + 3 = 1
```

## Pattern

```text
Build prefix once, answer each query in O(1).
```

## Result

```text
sumRange(0, 2) = 1
sumRange(2, 5) = -1
sumRange(0, 5) = -3
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class NumArray {
public:
    vector<long long> pref;

    NumArray(vector<int>& nums) {
        int n = nums.size();
        pref.assign(n + 1, 0);

        for (int i = 1; i <= n; i++) {
            pref[i] = pref[i - 1] + nums[i - 1];
        }
    }

    long long sumRange(int left, int right) {
        return pref[right + 1] - pref[left];
    }
};

int main() {
    vector<int> nums = {-2, 0, 3, -5, 2, -1};
    NumArray obj(nums);

    cout << obj.sumRange(0, 2) << "\n";
    cout << obj.sumRange(2, 5) << "\n";
    cout << obj.sumRange(0, 5) << "\n";
}
```

## Dry Run — Prefix Array Diagram

```text
nums = [-2, 0, 3, -5, 2, -1]
pref stores sum of first i elements.

index nums:   0    1   2    3   4    5
nums      :  [-2,  0,  3,  -5,  2,  -1]

index pref:   0    1    2   3    4    5    6
pref      :  [0,  -2,  -2,  1,  -4,  -2,  -3]
```

```text
Build prefix step by step:

i = 1 uses nums[0] = -2
pref[1] = pref[0] + nums[0] = 0 + (-2) = -2
pref = [0, -2]

i = 2 uses nums[1] = 0
pref[2] = pref[1] + nums[1] = -2 + 0 = -2
pref = [0, -2, -2]

i = 3 uses nums[2] = 3
pref[3] = -2 + 3 = 1
pref = [0, -2, -2, 1]

i = 4 uses nums[3] = -5
pref[4] = 1 - 5 = -4
pref = [0, -2, -2, 1, -4]

i = 5 uses nums[4] = 2
pref[5] = -4 + 2 = -2
pref = [0, -2, -2, 1, -4, -2]

i = 6 uses nums[5] = -1
pref[6] = -2 - 1 = -3
pref = [0, -2, -2, 1, -4, -2, -3]
```

```text
Query 1: sumRange(0, 2)
index nums:   0    1   2    3   4    5
nums      :  [-2,  0,  3,  -5,  2,  -1]
             ^---------^

formula    : pref[right + 1] - pref[left]
calculation: pref[3] - pref[0] = 1 - 0 = 1
answer     : 1
```

```text
Query 2: sumRange(2, 5)
index nums:   0    1   2    3   4    5
nums      :  [-2,  0,  3,  -5,  2,  -1]
                     ^----------------^

calculation: pref[6] - pref[2] = -3 - (-2) = -1
answer     : -1
```

```text
Query 3: sumRange(0, 5)
calculation: pref[6] - pref[0] = -3 - 0 = -3
answer     : -3
```

---

# 3. Find Pivot Index

## Problem Statement

Given an array `nums`, return the leftmost pivot index.

A pivot index is an index where:

```text
sum of elements on left == sum of elements on right
```

## Example

```text
Input : nums = [1, 7, 3, 6, 5, 6]
Output: 3
```

Because:

```text
left sum  = 1 + 7 + 3 = 11
right sum = 5 + 6 = 11
```

## Pattern

```text
Use total sum and running left sum.
```

## Result

```text
pivot index = 3
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int pivotIndex(vector<int>& nums) {
    long long total = 0;
    for (int x : nums) total += x;

    long long left = 0;

    for (int i = 0; i < nums.size(); i++) {
        long long right = total - left - nums[i];

        if (left == right) return i;

        left += nums[i];
    }

    return -1;
}

int main() {
    vector<int> nums = {1, 7, 3, 6, 5, 6};
    cout << pivotIndex(nums) << "\n";
}
```

## Dry Run — Array Diagram

```text
nums = [1, 7, 3, 6, 5, 6]
total = 28
left = 0

At each i:
right = total - left - nums[i]
if left == right, i is pivot.
```

```text
i = 0, current = 1
index:   0   1   2   3   4   5
nums :  [1,  7,  3,  6,  5,  6]
         ^

left before : 0
right       : 28 - 0 - 1 = 27
condition   : left == right? 0 == 27 no
action      : left += nums[0]
left after  : 1
```

```text
i = 1, current = 7
index:   0   1   2   3   4   5
nums :  [1,  7,  3,  6,  5,  6]
             ^

left before : 1
right       : 28 - 1 - 7 = 20
condition   : 1 == 20? no
action      : left += 7
left after  : 8
```

```text
i = 2, current = 3
index:   0   1   2   3   4   5
nums :  [1,  7,  3,  6,  5,  6]
                 ^

left before : 8
right       : 28 - 8 - 3 = 17
condition   : 8 == 17? no
action      : left += 3
left after  : 11
```

```text
i = 3, current = 6
index:   0   1   2   3   4   5
nums :  [1,  7,  3,  6,  5,  6]
                     ^

left before : 11
right       : 28 - 11 - 6 = 11
condition   : 11 == 11? yes
action      : return 3

Visual:
[1, 7, 3, 6, 5, 6]
 ^------^  |  ^---^
 left=11   |  right=11
          pivot
```

---

# 4. Maximum Average Subarray I

## Problem Statement

Given an array `nums` and integer `k`, find the maximum average value of any contiguous subarray of length `k`.

## Example

```text
Input : nums = [1, 12, -5, -6, 50, 3], k = 4
Output: 12.75
```

Because max sum window is:

```text
[12, -5, -6, 50] sum = 51
average = 51 / 4 = 12.75
```

## Pattern

```text
Fixed-size range sum using prefix.
```

## Result

```text
maximum average = 12.75
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double findMaxAverage(vector<int>& nums, int k) {
    int n = nums.size();
    vector<long long> pref(n + 1, 0);

    for (int i = 1; i <= n; i++) {
        pref[i] = pref[i - 1] + nums[i - 1];
    }

    long long best = LLONG_MIN;

    for (int i = 0; i + k <= n; i++) {
        long long sum = pref[i + k] - pref[i];
        best = max(best, sum);
    }

    return (double)best / k;
}

int main() {
    vector<int> nums = {1, 12, -5, -6, 50, 3};
    int k = 4;
    cout << fixed << setprecision(2) << findMaxAverage(nums, k) << "\n";
}
```

## Dry Run — Window Diagram

```text
nums = [1, 12, -5, -6, 50, 3]
k = 4

index:   0    1    2    3    4   5
nums :  [1,  12,  -5,  -6,  50,  3]
pref :  [0,   1,  13,   8,   2, 52, 55]
```

```text
Window start i = 0, window [0..3]
index:   0    1    2    3    4   5
nums :  [1,  12,  -5,  -6,  50,  3]
         ^--------------^

sum calculation: pref[4] - pref[0] = 2 - 0 = 2
best before    : -infinity
best after     : 2
```

```text
Window start i = 1, window [1..4]
index:   0    1    2    3    4   5
nums :  [1,  12,  -5,  -6,  50,  3]
              ^---------------^

sum calculation: pref[5] - pref[1] = 52 - 1 = 51
best before    : 2
best after     : 51
```

```text
Window start i = 2, window [2..5]
index:   0    1    2    3    4   5
nums :  [1,  12,  -5,  -6,  50,  3]
                   ^---------------^

sum calculation: pref[6] - pref[2] = 55 - 13 = 42
best before    : 51
best after     : 51

Final:
best sum = 51
average  = 51 / 4 = 12.75
```

---

# Phase 2 — Prefix Sum + HashMap

Goal:

```text
Count or find subarrays using previous prefix values.
```

Core formula:

```text
prefix[j] - prefix[i] = k
=> prefix[i] = prefix[j] - k
```

Recognition:

```text
count subarrays with sum k
longest subarray with sum k
number of binary subarrays
```

---

# 5. Subarray Sum Equals K

## Problem Statement

Given an array `nums` and integer `k`, return the total number of subarrays whose sum equals `k`.

## Example

```text
Input : nums = [1, 1, 1], k = 2
Output: 2
```

Subarrays:

```text
[1, 1] at index 0..1
[1, 1] at index 1..2
```

## Pattern

```text
prefix + frequency map
```

## Result

```text
count = 2
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int subarraySum(vector<int>& nums, int k) {
    unordered_map<long long, int> freq;
    freq[0] = 1;

    long long pref = 0;
    int ans = 0;

    for (int x : nums) {
        pref += x;

        if (freq.count(pref - k)) {
            ans += freq[pref - k];
        }

        freq[pref]++;
    }

    return ans;
}

int main() {
    vector<int> nums = {1, 1, 1};
    int k = 2;
    cout << subarraySum(nums, k) << "\n";
}
```

## Dry Run — Prefix HashMap Diagram

```text
nums = [1, 1, 1], k = 2
freq stores old prefix sums.

Initial:
freq = {0:1}
pref = 0, ans = 0
```

```text
i = 0, current = 1
index:   0   1   2
nums :  [1,  1,  1]
         ^

pref before : 0
pref after  : 0 + 1 = 1
need        : pref - k = 1 - 2 = -1
condition   : freq[-1] exists? no
action      : freq[1]++
freq after  : {0:1, 1:1}
ans         : 0
```

```text
i = 1, current = 1
index:   0   1   2
nums :  [1,  1,  1]
             ^

pref before : 1
pref after  : 2
need        : 2 - 2 = 0
condition   : freq[0] = 1 found
action      : ans += 1, freq[2]++
freq after  : {0:1, 1:1, 2:1}
ans         : 1

Visual:
[1, 1, 1]
 ^--^
 sum = 2
```

```text
i = 2, current = 1
index:   0   1   2
nums :  [1,  1,  1]
                 ^

pref before : 2
pref after  : 3
need        : 3 - 2 = 1
condition   : freq[1] = 1 found
action      : ans += 1, freq[3]++
ans         : 2

Visual:
[1, 1, 1]
     ^--^
     sum = 2

Final answer = 2
```

---

# 6. Maximum Size Subarray Sum Equals K

## Problem Statement

Given an array `nums` and integer `k`, return the maximum length of a subarray that sums to `k`.

## Example

```text
Input : nums = [1, -1, 5, -2, 3], k = 3
Output: 4
```

Because:

```text
[1, -1, 5, -2] sum = 3, length = 4
```

## Pattern

```text
prefix + first occurrence index
```

## Result

```text
maximum length = 4
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxSubArrayLen(vector<int>& nums, int k) {
    unordered_map<long long, int> first;
    first[0] = -1;

    long long pref = 0;
    int ans = 0;

    for (int i = 0; i < nums.size(); i++) {
        pref += nums[i];

        if (first.count(pref - k)) {
            ans = max(ans, i - first[pref - k]);
        }

        if (!first.count(pref)) {
            first[pref] = i;
        }
    }

    return ans;
}

int main() {
    vector<int> nums = {1, -1, 5, -2, 3};
    int k = 3;
    cout << maxSubArrayLen(nums, k) << "\n";
}
```

## Dry Run — Prefix Earliest Index Diagram

```text
nums = [1, -1, 5, -2, 3], k = 3
first stores earliest index of each prefix sum.

Initial:
first = {0:-1}
pref = 0, ans = 0
```

```text
i = 0, current = 1
index:   0    1   2    3   4
nums :  [1,  -1,  5,  -2,  3]
         ^

pref after : 1
need       : 1 - 3 = -2
condition  : first[-2] found? no
action     : store first[1] = 0
first      : {0:-1, 1:0}
ans        : 0
```

```text
i = 1, current = -1
index:   0    1   2    3   4
nums :  [1,  -1,  5,  -2,  3]
              ^

pref after : 0
need       : -3 not found
action     : first[0] already exists, keep earliest -1
ans        : 0
```

```text
i = 2, current = 5
index:   0    1   2    3   4
nums :  [1,  -1,  5,  -2,  3]
                   ^

pref after : 5
need       : 2 not found
action     : store first[5] = 2
ans        : 0
```

```text
i = 3, current = -2
index:   0    1   2    3   4
nums :  [1,  -1,  5,  -2,  3]
                         ^

pref after : 3
need       : 0
condition  : first[0] = -1 found
length     : i - first[0] = 3 - (-1) = 4
action     : ans = 4, store first[3] = 3

Visual:
[1, -1, 5, -2, 3]
 ^-----------^
 sum = 3, length = 4
```

```text
i = 4, current = 3
pref after : 6
need       : 3 found at index 3
length     : 4 - 3 = 1
ans remains: 4

Final answer = 4
```

---

# 7. Count Binary Subarrays With Sum

## Problem Statement

Given a binary array `nums` and integer `goal`, return the number of non-empty subarrays with sum equal to `goal`.

## Example

```text
Input : nums = [1, 0, 1, 0, 1], goal = 2
Output: 4
```

Valid subarrays:

```text
[1,0,1]
[1,0,1,0]
[0,1,0,1]
[1,0,1]
```

## Pattern

```text
Same as Subarray Sum Equals K.
```

## Result

```text
count = 4
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int numSubarraysWithSum(vector<int>& nums, int goal) {
    unordered_map<int, int> freq;
    freq[0] = 1;

    int pref = 0;
    int ans = 0;

    for (int x : nums) {
        pref += x;
        ans += freq[pref - goal];
        freq[pref]++;
    }

    return ans;
}

int main() {
    vector<int> nums = {1, 0, 1, 0, 1};
    int goal = 2;
    cout << numSubarraysWithSum(nums, goal) << "\n";
}
```

## Dry Run — Binary Prefix Diagram

```text
nums = [1, 0, 1, 0, 1], goal = 2
freq stores count of previous prefix sums.

Initial:
freq = {0:1}
pref = 0, ans = 0
```

```text
i = 0, current = 1
index:   0   1   2   3   4
nums :  [1,  0,  1,  0,  1]
         ^

pref after : 1
need       : 1 - 2 = -1
freq[need] : 0
ans        : 0
action     : freq[1] = 1
```

```text
i = 1, current = 0
index:   0   1   2   3   4
nums :  [1,  0,  1,  0,  1]
             ^

pref after : 1
need       : -1
freq[need] : 0
ans        : 0
action     : freq[1] = 2
```

```text
i = 2, current = 1
index:   0   1   2   3   4
nums :  [1,  0,  1,  0,  1]
                 ^

pref after : 2
need       : 0
freq[0]    : 1
action     : ans += 1, freq[2] = 1
ans        : 1

Visual valid subarray:
[1, 0, 1, 0, 1]
 ^------^
 sum = 2
```

```text
i = 3, current = 0
pref after : 2
need       : 0
freq[0]    : 1
ans        : 2
freq[2]    : 2

Visual new valid subarray:
[1, 0, 1, 0, 1]
 ^---------^
 sum = 2
```

```text
i = 4, current = 1
pref after : 3
need       : 1
freq[1]    : 2
ans        : 2 + 2 = 4
freq[3]    : 1

Two new valid subarrays end at i = 4:
[1, 0, 1, 0, 1]
     ^------^
        ^---^

Final answer = 4
```

---

# Phase 3 — Prefix Modulo

Goal:

```text
Group prefix sums by remainder.
```

Core idea:

```text
(prefix[j] - prefix[i]) % k == 0
=> prefix[j] % k == prefix[i] % k
```

Recognition:

```text
divisible by k
same remainder
remove shortest subarray to fix modulo
```

---

# 8. Subarray Sums Divisible by K

## Problem Statement

Given an integer array `nums` and integer `k`, return the number of subarrays whose sum is divisible by `k`.

## Example

```text
Input : nums = [4, 5, 0, -2, -3, 1], k = 5
Output: 7
```

## Pattern

```text
prefix modulo + frequency map
```

## Result

```text
count = 7
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int subarraysDivByK(vector<int>& nums, int k) {
    vector<int> freq(k, 0);
    freq[0] = 1;

    int pref = 0;
    int ans = 0;

    for (int x : nums) {
        pref = (pref + x) % k;
        if (pref < 0) pref += k;

        ans += freq[pref];
        freq[pref]++;
    }

    return ans;
}

int main() {
    vector<int> nums = {4, 5, 0, -2, -3, 1};
    int k = 5;
    cout << subarraysDivByK(nums, k) << "\n";
}
```

## Dry Run — Modulo Bucket Diagram

```text
nums = [4, 5, 0, -2, -3, 1], k = 5
freq[rem] stores how many previous prefix sums had this remainder.

Initial:
freq = [1, 0, 0, 0, 0]
pref = 0, ans = 0
```

```text
i = 0, current = 4
index:   0   1   2    3    4   5
nums :  [4,  5,  0,  -2,  -3,  1]
         ^

pref remainder: (0 + 4) % 5 = 4
same rem count: freq[4] = 0
ans           : 0
action        : freq[4]++
freq          : [1,0,0,0,1]
```

```text
i = 1, current = 5
index:   0   1   2    3    4   5
nums :  [4,  5,  0,  -2,  -3,  1]
             ^

pref remainder: (4 + 5) % 5 = 4
same rem count: freq[4] = 1
action        : ans += 1, freq[4]++
ans           : 1

Visual:
prefix remainder repeated 4 -> subarray between them divisible by 5
```

```text
i = 2, current = 0
pref remainder: 4
same rem count: freq[4] = 2
action        : ans += 2
ans           : 3
freq[4]       : 3
```

```text
i = 3, current = -2
pref remainder: (4 - 2) % 5 = 2
same rem count: freq[2] = 0
ans           : 3
freq[2]       : 1
```

```text
i = 4, current = -3
raw remainder : (2 - 3) % 5 = -1
fixed rem     : -1 + 5 = 4
same rem count: freq[4] = 3
action        : ans += 3
ans           : 6
freq[4]       : 4
```

```text
i = 5, current = 1
pref remainder: (4 + 1) % 5 = 0
same rem count: freq[0] = 1
action        : ans += 1
ans           : 7

Final answer = 7
```

---

# 9. Continuous Subarray Sum

## Problem Statement

Given an array `nums` and integer `k`, return true if the array has a continuous subarray of size at least 2 whose sum is a multiple of `k`.

## Example

```text
Input : nums = [23, 2, 4, 6, 7], k = 6
Output: true
```

Because:

```text
[2, 4] sum = 6
```

## Pattern

```text
prefix modulo + earliest index
```

## Result

```text
true
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool checkSubarraySum(vector<int>& nums, int k) {
    unordered_map<int, int> first;
    first[0] = -1;

    int pref = 0;

    for (int i = 0; i < nums.size(); i++) {
        pref = (pref + nums[i]) % k;

        if (first.count(pref)) {
            if (i - first[pref] >= 2) return true;
        } else {
            first[pref] = i;
        }
    }

    return false;
}

int main() {
    vector<int> nums = {23, 2, 4, 6, 7};
    int k = 6;
    cout << boolalpha << checkSubarraySum(nums, k) << "\n";
}
```

## Dry Run — Remainder Earliest Index Diagram

```text
nums = [23, 2, 4, 6, 7], k = 6
first stores earliest index for each remainder.
Need same remainder with distance >= 2.

Initial:
first = {0:-1}
pref = 0
```

```text
i = 0, current = 23
index:   0    1   2   3   4
nums :  [23,  2,  4,  6,  7]
         ^

pref rem : (0 + 23) % 6 = 5
seen?    : no
action   : first[5] = 0
first    : {0:-1, 5:0}
```

```text
i = 1, current = 2
index:   0    1   2   3   4
nums :  [23,  2,  4,  6,  7]
              ^

pref rem : (5 + 2) % 6 = 1
seen?    : no
action   : first[1] = 1
```

```text
i = 2, current = 4
index:   0    1   2   3   4
nums :  [23,  2,  4,  6,  7]
                  ^

pref rem : (1 + 4) % 6 = 5
seen?    : yes, first[5] = 0
length   : 2 - 0 = 2
condition: length >= 2? yes
action   : return true

Visual:
[23, 2, 4, 6, 7]
      ^--^
      sum = 6, multiple of 6
```

---

# 10. Make Sum Divisible by P

## Problem Statement

Given an array `nums` and integer `p`, remove the shortest subarray so that the remaining sum is divisible by `p`.

Return the length of the shortest subarray to remove. If impossible, return `-1`.

## Example

```text
Input : nums = [3, 1, 4, 2], p = 6
Output: 1
```

Because total sum is `10`, remainder is `4`. Remove `[4]`.

## Pattern

```text
Need subarray whose sum % p == total % p.
```

## Result

```text
minimum length = 1
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minSubarray(vector<int>& nums, int p) {
    long long total = 0;
    for (int x : nums) total += x;

    int need = total % p;
    if (need == 0) return 0;

    unordered_map<int, int> last;
    last[0] = -1;

    int pref = 0;
    int ans = nums.size();

    for (int i = 0; i < nums.size(); i++) {
        pref = (pref + nums[i]) % p;

        int target = (pref - need + p) % p;

        if (last.count(target)) {
            ans = min(ans, i - last[target]);
        }

        last[pref] = i;
    }

    return ans == nums.size() ? -1 : ans;
}

int main() {
    vector<int> nums = {3, 1, 4, 2};
    int p = 6;
    cout << minSubarray(nums, p) << "\n";
}
```

## Dry Run — Remainder Removal Diagram

```text
nums = [3, 1, 4, 2], p = 6
total = 10
need = total % p = 4

We need to remove shortest subarray whose sum % 6 = 4.
last stores latest index of each prefix remainder.

Initial:
last = {0:-1}
pref = 0, ans = 4
```

```text
i = 0, current = 3
index:   0   1   2   3
nums :  [3,  1,  4,  2]
         ^

pref rem : 3
target   : (pref - need + p) % p = (3 - 4 + 6) % 6 = 5
found?   : no
action   : last[3] = 0
ans      : 4
```

```text
i = 1, current = 1
index:   0   1   2   3
nums :  [3,  1,  4,  2]
             ^

pref rem : 4
target   : (4 - 4 + 6) % 6 = 0
found?   : last[0] = -1
length   : 1 - (-1) = 2
action   : ans = 2, last[4] = 1

Visual removable candidate:
[3, 1, 4, 2]
 ^--^
 sum = 4, length = 2
```

```text
i = 2, current = 4
index:   0   1   2   3
nums :  [3,  1,  4,  2]
                 ^

pref rem : (4 + 4) % 6 = 2
target   : (2 - 4 + 6) % 6 = 4
found?   : last[4] = 1
length   : 2 - 1 = 1
action   : ans = 1, last[2] = 2

Visual best removable candidate:
[3, 1, 4, 2]
         ^
 remove [4], length = 1
```

```text
i = 3, current = 2
pref rem : 4
target   : 0
length   : 3 - (-1) = 4
ans stays: 1

Final answer = 1
```

---

# Phase 4 — Prefix Balance / Parity

Goal:

```text
Convert values into balance scores.
```

Recognition:

```text
equal count of 0 and 1
odd/even count
more tiring days than non-tiring days
character parity
```

---

# 11. Contiguous Array

## Problem Statement

Given a binary array `nums`, return the maximum length of a contiguous subarray with equal number of `0` and `1`.

## Example

```text
Input : nums = [0, 1, 0]
Output: 2
```

Because `[0, 1]` or `[1, 0]` has equal 0 and 1.

## Pattern

```text
Convert 0 to -1 and 1 to +1.
Equal 0/1 means sum = 0.
```

## Result

```text
maximum length = 2
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int findMaxLength(vector<int>& nums) {
    unordered_map<int, int> first;
    first[0] = -1;

    int balance = 0;
    int ans = 0;

    for (int i = 0; i < nums.size(); i++) {
        if (nums[i] == 0) balance--;
        else balance++;

        if (first.count(balance)) {
            ans = max(ans, i - first[balance]);
        } else {
            first[balance] = i;
        }
    }

    return ans;
}

int main() {
    vector<int> nums = {0, 1, 0};
    cout << findMaxLength(nums) << "\n";
}
```

## Dry Run — Balance Array Diagram

```text
nums = [0, 1, 0]
convert: 0 -> -1, 1 -> +1
Need longest subarray with balance repeated.

first = {0:-1}
balance = 0, ans = 0
```

```text
i = 0, current = 0 => -1
index:   0   1   2
nums :  [0,  1,  0]
         ^

balance before: 0
balance after : -1
seen?         : no
action        : first[-1] = 0
ans           : 0
```

```text
i = 1, current = 1 => +1
index:   0   1   2
nums :  [0,  1,  0]
             ^

balance before: -1
balance after : 0
seen?         : yes, first[0] = -1
length        : 1 - (-1) = 2
action        : ans = 2

Visual:
[0, 1, 0]
 ^--^
equal 0 and 1
```

```text
i = 2, current = 0 => -1
balance after : -1
seen?         : yes, first[-1] = 0
length        : 2 - 0 = 2
ans           : 2

Final answer = 2
```

---

# 12. Count Number of Nice Subarrays

## Problem Statement

Given an array `nums` and integer `k`, return the number of subarrays with exactly `k` odd numbers.

## Example

```text
Input : nums = [1, 1, 2, 1, 1], k = 3
Output: 2
```

Valid subarrays:

```text
[1,1,2,1]
[1,2,1,1]
```

## Pattern

```text
Odd number = 1
Even number = 0
Then count binary subarrays with sum k.
```

## Result

```text
count = 2
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int numberOfSubarrays(vector<int>& nums, int k) {
    unordered_map<int, int> freq;
    freq[0] = 1;

    int oddCount = 0;
    int ans = 0;

    for (int x : nums) {
        oddCount += (x % 2);
        ans += freq[oddCount - k];
        freq[oddCount]++;
    }

    return ans;
}

int main() {
    vector<int> nums = {1, 1, 2, 1, 1};
    int k = 3;
    cout << numberOfSubarrays(nums, k) << "\n";
}
```

## Dry Run — Odd Count Prefix Diagram

```text
nums = [1, 1, 2, 1, 1], k = 3
odd version = [1, 1, 0, 1, 1]
freq stores previous oddCount values.

freq = {0:1}
oddCount = 0, ans = 0
```

```text
i = 0, current = 1 odd
index:   0   1   2   3   4
nums :  [1,  1,  2,  1,  1]
         ^

oddCount after: 1
need          : 1 - 3 = -2
freq[need]    : 0
ans           : 0
action        : freq[1] = 1
```

```text
i = 1, current = 1 odd
oddCount after: 2
need          : -1
ans           : 0
freq[2]       : 1
```

```text
i = 2, current = 2 even
oddCount after: 2
need          : -1
ans           : 0
freq[2]       : 2
```

```text
i = 3, current = 1 odd
index:   0   1   2   3   4
nums :  [1,  1,  2,  1,  1]
                     ^

oddCount after: 3
need          : 0
freq[0]       : 1
action        : ans += 1
ans           : 1

Visual valid subarray:
[1, 1, 2, 1, 1]
 ^---------^
 exactly 3 odds
```

```text
i = 4, current = 1 odd
oddCount after: 4
need          : 1
freq[1]       : 1
action        : ans += 1
ans           : 2

Visual valid subarray:
[1, 1, 2, 1, 1]
     ^---------^
 exactly 3 odds

Final answer = 2
```

---

# 13. Longest Well-Performing Interval

## Problem Statement

A tiring day is a day with `hours[i] > 8`.

Return the length of the longest interval where tiring days are strictly more than non-tiring days.

## Example

```text
Input : hours = [9, 9, 6, 0, 6, 6, 9]
Output: 3
```

Because `[9, 9, 6]` has 2 tiring days and 1 non-tiring day.

## Pattern

```text
hours > 8  -> +1
hours <= 8 -> -1
Need longest subarray with sum > 0.
```

## Result

```text
maximum length = 3
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestWPI(vector<int>& hours) {
    unordered_map<int, int> first;
    int score = 0;
    int ans = 0;

    for (int i = 0; i < hours.size(); i++) {
        score += (hours[i] > 8 ? 1 : -1);

        if (score > 0) {
            ans = i + 1;
        } else {
            if (!first.count(score)) first[score] = i;

            if (first.count(score - 1)) {
                ans = max(ans, i - first[score - 1]);
            }
        }
    }

    return ans;
}

int main() {
    vector<int> hours = {9, 9, 6, 0, 6, 6, 9};
    cout << longestWPI(hours) << "\n";
}
```

## Dry Run — Score Balance Diagram

```text
hours = [9, 9, 6, 0, 6, 6, 9]
convert: >8 => +1, <=8 => -1
values = [+1, +1, -1, -1, -1, -1, +1]
Need longest subarray with score > 0.

score = 0, ans = 0, first = {}
```

```text
i = 0, hours[0] = 9 => +1
index :   0   1   2   3   4   5   6
hours :  [9,  9,  6,  0,  6,  6,  9]
          ^

score after : 1
condition   : score > 0 yes
action      : ans = i + 1 = 1
```

```text
i = 1, hours[1] = 9 => +1
score after : 2
score > 0   : yes
ans         : 2
```

```text
i = 2, hours[2] = 6 => -1
score after : 1
score > 0   : yes
ans         : 3

Visual:
[9, 9, 6, 0, 6, 6, 9]
 ^-----^
 +1 +1 -1 = +1, valid length 3
```

```text
i = 3, hours[3] = 0 => -1
score after : 0
score > 0   : no
store       : first[0] = 3
need        : score - 1 = -1 not found
ans         : 3
```

```text
i = 4, hours[4] = 6 => -1
score after : -1
store       : first[-1] = 4
need        : -2 not found
ans         : 3
```

```text
i = 5, hours[5] = 6 => -1
score after : -2
store       : first[-2] = 5
need        : -3 not found
ans         : 3
```

```text
i = 6, hours[6] = 9 => +1
score after : -1
first[-1] already exists at 4
need        : -2 found at 5
candidate   : 6 - 5 = 1
ans remains : 3

Final answer = 3
```

---

# Phase 5 — Difference Array

Goal:

```text
Apply many range updates in O(1) each.
```

Core rule:

```text
add x on [l, r]
diff[l] += x
diff[r + 1] -= x
```

Then build prefix of `diff`.

---

# 14. Range Addition

## Problem Statement

You are given length `n` initialized with zeros and updates:

```text
[l, r, x]
```

Add `x` to every index from `l` to `r`.

Return the final array.

## Example

```text
n = 5
updates = [[1,3,2], [2,4,3], [0,2,-2]]
Output = [-2, 0, 3, 5, 3]
```

## Pattern

```text
1D difference array
```

## Result

```text
[-2, 0, 3, 5, 3]
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> getModifiedArray(int n, vector<vector<int>>& updates) {
    vector<int> diff(n + 1, 0);

    for (auto &u : updates) {
        int l = u[0], r = u[1], x = u[2];
        diff[l] += x;
        diff[r + 1] -= x;
    }

    vector<int> ans(n);
    int cur = 0;

    for (int i = 0; i < n; i++) {
        cur += diff[i];
        ans[i] = cur;
    }

    return ans;
}

int main() {
    int n = 5;
    vector<vector<int>> updates = {{1,3,2}, {2,4,3}, {0,2,-2}};
    vector<int> ans = getModifiedArray(n, updates);

    for (int x : ans) cout << x << " ";
}
```

## Dry Run — Difference Array Diagram

```text
n = 5
updates = [[1,3,2], [2,4,3], [0,2,-2]]

diff has one extra slot to stop effect at r + 1.
diff = [0, 0, 0, 0, 0, 0]
```

```text
Update 1: add +2 on [1..3]
index:   0   1   2   3   4   5
 diff:  [0,  0,  0,  0,  0,  0]
             ^-------^   stop

action      : diff[1] += 2, diff[4] -= 2
diff after  : [0, 2, 0, 0, -2, 0]
```

```text
Update 2: add +3 on [2..4]
index:   0   1   2   3   4   5
 diff:  [0,  2,  0,  0, -2,  0]
                 ^-------^   stop

action      : diff[2] += 3, diff[5] -= 3
diff after  : [0, 2, 3, 0, -2, -3]
```

```text
Update 3: add -2 on [0..2]
index:   0   1   2   3   4   5
 diff:  [0,  2,  3,  0, -2, -3]
         ^-------^   stop

action      : diff[0] += -2, diff[3] -= -2
diff after  : [-2, 2, 3, 2, -2, -3]
```

```text
Build final array by prefix over diff:

index:       0   1   2   3   4
diff used:  -2   2   3   2  -2

cur before i=0: 0
cur += diff[0] = -2 -> ans[0] = -2

cur before i=1: -2
cur += diff[1] = 0 -> ans[1] = 0

cur before i=2: 0
cur += diff[2] = 3 -> ans[2] = 3

cur before i=3: 3
cur += diff[3] = 5 -> ans[3] = 5

cur before i=4: 5
cur += diff[4] = 3 -> ans[4] = 3

Final answer = [-2, 0, 3, 5, 3]
```

---

# 15. Corporate Flight Bookings

## Problem Statement

There are `n` flights labeled from `1` to `n`.

Each booking is:

```text
[first, last, seats]
```

It means `seats` seats are booked on every flight from `first` to `last`.

Return total seats booked for each flight.

## Example

```text
bookings = [[1,2,10], [2,3,20], [2,5,25]], n = 5
Output = [10, 55, 45, 25, 25]
```

## Pattern

```text
Range update using difference array.
```

## Result

```text
[10, 55, 45, 25, 25]
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> corpFlightBookings(vector<vector<int>>& bookings, int n) {
    vector<int> diff(n + 1, 0);

    for (auto &b : bookings) {
        int l = b[0] - 1;
        int r = b[1] - 1;
        int seats = b[2];

        diff[l] += seats;
        diff[r + 1] -= seats;
    }

    vector<int> ans(n);
    int cur = 0;

    for (int i = 0; i < n; i++) {
        cur += diff[i];
        ans[i] = cur;
    }

    return ans;
}

int main() {
    vector<vector<int>> bookings = {{1,2,10}, {2,3,20}, {2,5,25}};
    int n = 5;
    vector<int> ans = corpFlightBookings(bookings, n);

    for (int x : ans) cout << x << " ";
}
```

## Dry Run — Flight Difference Diagram

```text
bookings = [[1,2,10], [2,3,20], [2,5,25]], n = 5
Flights are 1-indexed in input, convert to 0-indexed.

diff = [0, 0, 0, 0, 0, 0]
```

```text
Booking [1,2,10] => 0-index range [0..1]
flight index:  0   1   2   3   4   5
 diff      :  [0,  0,  0,  0,  0,  0]
              ^---^ stop

action     : diff[0] += 10, diff[2] -= 10
diff after : [10, 0, -10, 0, 0, 0]
```

```text
Booking [2,3,20] => range [1..2]
action     : diff[1] += 20, diff[3] -= 20
diff after : [10, 20, -10, -20, 0, 0]
```

```text
Booking [2,5,25] => range [1..4]
action     : diff[1] += 25, diff[5] -= 25
diff after : [10, 45, -10, -20, 0, -25]
```

```text
Prefix reconstruction:

flight 1, i=0: cur = 0 + 10 = 10  -> ans[0] = 10
flight 2, i=1: cur = 10 + 45 = 55 -> ans[1] = 55
flight 3, i=2: cur = 55 - 10 = 45 -> ans[2] = 45
flight 4, i=3: cur = 45 - 20 = 25 -> ans[3] = 25
flight 5, i=4: cur = 25 + 0 = 25  -> ans[4] = 25

Final answer = [10, 55, 45, 25, 25]
```

---

# 16. Car Pooling

## Problem Statement

You are given trips:

```text
[numPassengers, from, to]
```

Passengers get in at `from` and get out at `to`.

Return true if the car never exceeds capacity.

## Example

```text
trips = [[2,1,5], [3,3,7]], capacity = 4
Output = false
```

## Pattern

```text
Difference array over timeline.
```

## Result

```text
false
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool carPooling(vector<vector<int>>& trips, int capacity) {
    vector<int> diff(1001, 0);

    for (auto &t : trips) {
        int passengers = t[0];
        int from = t[1];
        int to = t[2];

        diff[from] += passengers;
        diff[to] -= passengers;
    }

    int cur = 0;
    for (int i = 0; i <= 1000; i++) {
        cur += diff[i];
        if (cur > capacity) return false;
    }

    return true;
}

int main() {
    vector<vector<int>> trips = {{2,1,5}, {3,3,7}};
    int capacity = 4;
    cout << boolalpha << carPooling(trips, capacity) << "\n";
}
```

## Dry Run — Timeline Difference Diagram

```text
trips = [[2,1,5], [3,3,7]], capacity = 4
trip format = [passengers, from, to]
Passenger effect starts at from and stops at to.
```

```text
Trip 1: [2,1,5]
timeline:  0   1   2   3   4   5   6   7
           .   +2          active       -2

action: diff[1] += 2, diff[5] -= 2
```

```text
Trip 2: [3,3,7]
timeline:  0   1   2   3   4   5   6   7
                       +3          active -3

action: diff[3] += 3, diff[7] -= 3
```

```text
Scan timeline with current passengers:

position 0:
cur = 0
cur <= 4 ok

position 1:
cur = 0 + 2 = 2
cur <= 4 ok

position 2:
cur = 2
cur <= 4 ok

position 3:
cur = 2 + 3 = 5
condition: cur > capacity? 5 > 4 yes
action   : return false

Visual overlap:
position: 1   2   3   4   5   6
trip1   : +---+---+---+---)
trip2   :         +---+---+---+---)
                    overlap = 2 + 3 = 5 > 4
```

---

# Phase 6 — 2D Prefix Sum

Goal:

```text
Answer rectangle sum queries in O(1).
```

Formula:

```text
sum = big rectangle - top extra - left extra + overlap
```

---

# 17. Range Sum Query 2D Immutable

## Problem Statement

Given a matrix, answer queries:

```text
sumRegion(row1, col1, row2, col2)
```

Return sum of rectangle from `(row1, col1)` to `(row2, col2)`.

## Example

```text
matrix =
[
  [3,0,1,4,2],
  [5,6,3,2,1],
  [1,2,0,1,5],
  [4,1,0,1,7],
  [1,0,3,0,5]
]

sumRegion(2,1,4,3) = 8
```

## Pattern

```text
2D prefix sum
```

## Result

```text
8
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class NumMatrix {
public:
    vector<vector<int>> pref;

    NumMatrix(vector<vector<int>>& matrix) {
        int n = matrix.size();
        int m = matrix[0].size();

        pref.assign(n + 1, vector<int>(m + 1, 0));

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                pref[i][j] = matrix[i - 1][j - 1]
                           + pref[i - 1][j]
                           + pref[i][j - 1]
                           - pref[i - 1][j - 1];
            }
        }
    }

    int sumRegion(int row1, int col1, int row2, int col2) {
        return pref[row2 + 1][col2 + 1]
             - pref[row1][col2 + 1]
             - pref[row2 + 1][col1]
             + pref[row1][col1];
    }
};

int main() {
    vector<vector<int>> matrix = {
        {3,0,1,4,2},
        {5,6,3,2,1},
        {1,2,0,1,5},
        {4,1,0,1,7},
        {1,0,3,0,5}
    };

    NumMatrix nm(matrix);
    cout << nm.sumRegion(2,1,4,3) << "\n";
}
```

## Dry Run — Matrix Rectangle Diagram

```text
matrix:
row\col   0  1  2  3  4
0        [3, 0, 1, 4, 2]
1        [5, 6, 3, 2, 1]
2        [1, 2, 0, 1, 5]
3        [4, 1, 0, 1, 7]
4        [1, 0, 3, 0, 5]

Query: sumRegion(2,1,4,3)
Rows 2..4, Cols 1..3
```

```text
Highlight rectangle:
row\col   0  1  2  3  4
0         3  0  1  4  2
1         5  6  3  2  1
2         1 [2  0  1] 5
3         4 [1  0  1] 7
4         1 [0  3  0] 5

Manual sum:
row 2: 2 + 0 + 1 = 3
row 3: 1 + 0 + 1 = 2
row 4: 0 + 3 + 0 = 3
total = 8
```

```text
Using 1-indexed prefix:
answer = pref[row2 + 1][col2 + 1]
       - pref[row1][col2 + 1]
       - pref[row2 + 1][col1]
       + pref[row1][col1]

answer = pref[5][4]
       - pref[2][4]
       - pref[5][1]
       + pref[2][1]

Meaning:
1. Take big rectangle from (0,0) to (4,3)
2. Remove rows above row 2
3. Remove columns left of col 1
4. Add back overlap removed twice

Final answer = 8
```

---

# 18. Matrix Block Sum

## Problem Statement

Given matrix `mat` and integer `k`, return matrix `answer` where:

```text
answer[i][j] = sum of all mat[r][c]
where |r - i| <= k and |c - j| <= k
```

## Example

```text
mat = [[1,2,3],
       [4,5,6],
       [7,8,9]]
k = 1

Output:
[[12,21,16],
 [27,45,33],
 [24,39,28]]
```

## Pattern

```text
For every cell, query rectangle using 2D prefix.
```

## Result

```text
[[12,21,16], [27,45,33], [24,39,28]]
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> matrixBlockSum(vector<vector<int>>& mat, int k) {
    int n = mat.size();
    int m = mat[0].size();

    vector<vector<int>> pref(n + 1, vector<int>(m + 1, 0));

    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= m; j++) {
            pref[i][j] = mat[i - 1][j - 1]
                       + pref[i - 1][j]
                       + pref[i][j - 1]
                       - pref[i - 1][j - 1];
        }
    }

    auto query = [&](int r1, int c1, int r2, int c2) {
        return pref[r2 + 1][c2 + 1]
             - pref[r1][c2 + 1]
             - pref[r2 + 1][c1]
             + pref[r1][c1];
    };

    vector<vector<int>> ans(n, vector<int>(m));

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            int r1 = max(0, i - k);
            int c1 = max(0, j - k);
            int r2 = min(n - 1, i + k);
            int c2 = min(m - 1, j + k);

            ans[i][j] = query(r1, c1, r2, c2);
        }
    }

    return ans;
}

int main() {
    vector<vector<int>> mat = {{1,2,3},{4,5,6},{7,8,9}};
    int k = 1;
    auto ans = matrixBlockSum(mat, k);

    for (auto &row : ans) {
        for (int x : row) cout << x << " ";
        cout << "\n";
    }
}
```

## Dry Run — Matrix Block Diagram

```text
mat:
row\col  0  1  2
0       [1, 2, 3]
1       [4, 5, 6]
2       [7, 8, 9]

k = 1
For every cell (i,j), take rectangle:
rows [i-k .. i+k], cols [j-k .. j+k], clipped inside matrix.
```

```text
Cell (0,0):
r1 = max(0, 0-1) = 0
c1 = max(0, 0-1) = 0
r2 = min(2, 0+1) = 1
c2 = min(2, 0+1) = 1

Selected block:
[1, 2]
[4, 5]

sum = 1 + 2 + 4 + 5 = 12
ans[0][0] = 12
```

```text
Cell (0,1):
r1 = 0, c1 = 0, r2 = 1, c2 = 2

Selected block:
[1, 2, 3]
[4, 5, 6]

sum = 21
ans[0][1] = 21
```

```text
Cell (1,1):
r1 = 0, c1 = 0, r2 = 2, c2 = 2

Selected block:
[1, 2, 3]
[4, 5, 6]
[7, 8, 9]

sum = 45
ans[1][1] = 45
```

```text
Final answer:
12 21 16
27 45 33
24 39 28
```

---

# 19. Largest Magic Square

## Problem Statement

Given a grid, return the size of the largest magic square.

A magic square is a square where every row, every column, and both diagonals have the same sum.

## Example

```text
Input:
grid = [[7,1,4,5,6],
        [2,5,1,6,4],
        [1,5,4,3,2],
        [1,2,7,3,4]]

Output: 3
```

## Pattern

```text
row prefix + column prefix + diagonal checking
```

## Result

```text
largest magic square size = 3
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int largestMagicSquare(vector<vector<int>>& grid) {
    int n = grid.size();
    int m = grid[0].size();

    vector<vector<int>> row(n, vector<int>(m + 1, 0));
    vector<vector<int>> col(n + 1, vector<int>(m, 0));

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            row[i][j + 1] = row[i][j] + grid[i][j];
            col[i + 1][j] = col[i][j] + grid[i][j];
        }
    }

    auto rowSum = [&](int r, int c1, int c2) {
        return row[r][c2 + 1] - row[r][c1];
    };

    auto colSum = [&](int c, int r1, int r2) {
        return col[r2 + 1][c] - col[r1][c];
    };

    int best = 1;

    for (int len = min(n, m); len >= 2; len--) {
        for (int r = 0; r + len <= n; r++) {
            for (int c = 0; c + len <= m; c++) {
                int target = rowSum(r, c, c + len - 1);
                bool ok = true;

                for (int i = r; i < r + len; i++) {
                    if (rowSum(i, c, c + len - 1) != target) ok = false;
                }

                for (int j = c; j < c + len; j++) {
                    if (colSum(j, r, r + len - 1) != target) ok = false;
                }

                int d1 = 0, d2 = 0;
                for (int k = 0; k < len; k++) {
                    d1 += grid[r + k][c + k];
                    d2 += grid[r + k][c + len - 1 - k];
                }

                if (d1 != target || d2 != target) ok = false;

                if (ok) return len;
            }
        }
    }

    return best;
}

int main() {
    vector<vector<int>> grid = {
        {7,1,4,5,6},
        {2,5,1,6,4},
        {1,5,4,3,2},
        {1,2,7,3,4}
    };

    cout << largestMagicSquare(grid) << "\n";
}
```

## Dry Run — Candidate Square Diagram

```text
grid:
row\col  0  1  2  3  4
0       [7, 1, 4, 5, 6]
1       [2, 5, 1, 6, 4]
2       [1, 5, 4, 3, 2]
3       [1, 2, 7, 3, 4]

Try larger square sizes first.
```

```text
Candidate len = 3
Assume candidate starts at (1,1):

selected 3x3:
[5, 1, 6]
[5, 4, 3]
[2, 7, 3]

Check target row sum from first row:
target = 5 + 1 + 6 = 12
```

```text
Row checks:
row 1: 5 + 1 + 6 = 12 ok
row 2: 5 + 4 + 3 = 12 ok
row 3: 2 + 7 + 3 = 12 ok
```

```text
Column checks:
col 1: 5 + 5 + 2 = 12 ok
col 2: 1 + 4 + 7 = 12 ok
col 3: 6 + 3 + 3 = 12 ok
```

```text
Diagonal checks:
main diagonal     : 5 + 4 + 3 = 12 ok
opposite diagonal : 6 + 4 + 2 = 12 ok

All rows, columns, diagonals match.
return 3
```

---

# Phase 7 — Advanced Prefix

Goal:

```text
Use prefix with deque, merge sort, or bitmask.
```

Recognition:

```text
shortest subarray at least k
range sum count
odd/even character parity
```

---

# 20. Shortest Subarray With Sum At Least K

## Problem Statement

Given an integer array `nums` and integer `k`, return the length of the shortest non-empty subarray with sum at least `k`.

If no such subarray exists, return `-1`.

## Example

```text
Input : nums = [2, -1, 2], k = 3
Output: 3
```

## Pattern

```text
prefix sum + monotonic deque
```

## Result

```text
shortest length = 3
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int shortestSubarray(vector<int>& nums, int k) {
    int n = nums.size();
    vector<long long> pref(n + 1, 0);

    for (int i = 0; i < n; i++) {
        pref[i + 1] = pref[i] + nums[i];
    }

    deque<int> dq;
    int ans = n + 1;

    for (int i = 0; i <= n; i++) {
        while (!dq.empty() && pref[i] - pref[dq.front()] >= k) {
            ans = min(ans, i - dq.front());
            dq.pop_front();
        }

        while (!dq.empty() && pref[i] <= pref[dq.back()]) {
            dq.pop_back();
        }

        dq.push_back(i);
    }

    return ans == n + 1 ? -1 : ans;
}

int main() {
    vector<int> nums = {2, -1, 2};
    int k = 3;
    cout << shortestSubarray(nums, k) << "\n";
}
```

## Dry Run — Prefix Deque Diagram

```text
nums = [2, -1, 2], k = 3
pref = [0, 2, 1, 3]

dq stores prefix indices.
Invariant: pref values in dq are increasing.
```

```text
i = 0, pref[0] = 0
index pref:  0   1   2   3
pref     : [0,  2,  1,  3]
          ^

dq before : []
front check: none
back check : none
action     : push 0
dq after   : [0:0]
ans        : INF
```

```text
i = 1, pref[1] = 2
index pref:  0   1   2   3
pref     : [0,  2,  1,  3]
              ^

dq before : [0:0]
front check: pref[1] - pref[0] = 2 - 0 = 2 < 3
back check : pref[1] <= pref[0]? 2 <= 0 no
action     : push 1
dq after   : [0:0, 1:2]
ans        : INF
```

```text
i = 2, pref[2] = 1
index pref:  0   1   2   3
pref     : [0,  2,  1,  3]
                  ^

dq before : [0:0, 1:2]
front check: pref[2] - pref[0] = 1 < 3
back check : pref[2] <= pref[1]? 1 <= 2 yes -> pop 1
action     : push 2
dq after   : [0:0, 2:1]
ans        : INF

Why pop 1?
pref[2] is smaller and later than pref[1], so index 1 is never better.
```

```text
i = 3, pref[3] = 3
index pref:  0   1   2   3
pref     : [0,  2,  1,  3]
                      ^

dq before : [0:0, 2:1]
front check: pref[3] - pref[0] = 3 - 0 = 3 >= 3
candidate  : length = 3 - 0 = 3
ans        : 3
pop front  : remove 0

next front : pref[3] - pref[2] = 3 - 1 = 2 < 3
action     : push 3
dq after   : [2:1, 3:3]

Final answer = 3
```

---

# 21. Count of Range Sum

## Problem Statement

Given an integer array `nums` and two integers `lower` and `upper`, return the number of range sums that lie in `[lower, upper]`.

Range sum is:

```text
sum(i, j) = nums[i] + nums[i+1] + ... + nums[j]
```

## Example

```text
Input : nums = [-2, 5, -1], lower = -2, upper = 2
Output: 3
```

Valid range sums:

```text
[0,0] = -2
[2,2] = -1
[0,2] = 2
```

## Pattern

```text
prefix sum + merge sort counting
```

## Result

```text
count = 3
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    long long lower, upper;

    int countWhileMergeSort(vector<long long>& sums, int left, int right) {
        if (right - left <= 1) return 0;

        int mid = left + (right - left) / 2;
        int count = countWhileMergeSort(sums, left, mid)
                  + countWhileMergeSort(sums, mid, right);

        int j = mid, k = mid;

        for (int i = left; i < mid; i++) {
            while (k < right && sums[k] - sums[i] < lower) k++;
            while (j < right && sums[j] - sums[i] <= upper) j++;
            count += j - k;
        }

        inplace_merge(sums.begin() + left, sums.begin() + mid, sums.begin() + right);
        return count;
    }

    int countRangeSum(vector<int>& nums, int low, int up) {
        lower = low;
        upper = up;

        vector<long long> sums(nums.size() + 1, 0);
        for (int i = 0; i < nums.size(); i++) {
            sums[i + 1] = sums[i] + nums[i];
        }

        return countWhileMergeSort(sums, 0, sums.size());
    }
};

int main() {
    vector<int> nums = {-2, 5, -1};
    Solution sol;
    cout << sol.countRangeSum(nums, -2, 2) << "\n";
}
```

## Dry Run — Array Diagram

```text
nums = [-2, 5, -1]
lower = -2, upper = 2

Build prefix sums first:
index:   0    1    2    3
pref :  [0,  -2,   3,   2]

Rule:
subarray sum from nums[i..j-1] = pref[j] - pref[i]
Need: -2 <= pref[j] - pref[i] <= 2
```

```text
i = 0, pref[i] = 0
index:   0    1    2    3
pref :  [0,  -2,   3,   2]
         ^
         i

count before: 0
```

```text
j = 1, pref[j] = -2
index:   0    1    2    3
pref :  [0,  -2,   3,   2]
         ^    ^
         i    j

condition   : -2 <= pref[1] - pref[0] <= 2
calculation : -2 - 0 = -2
result      : valid
range       : nums[0..0] = [-2]
action      : count++
count after : 1
```

```text
j = 2, pref[j] = 3
index:   0    1    2    3
pref :  [0,  -2,   3,   2]
         ^         ^
         i         j

condition   : -2 <= pref[2] - pref[0] <= 2
calculation : 3 - 0 = 3
result      : invalid
count after : 1
```

```text
j = 3, pref[j] = 2
index:   0    1    2    3
pref :  [0,  -2,   3,   2]
         ^              ^
         i              j

condition   : -2 <= pref[3] - pref[0] <= 2
calculation : 2 - 0 = 2
result      : valid
range       : nums[0..2] = [-2, 5, -1]
action      : count++
count after : 2
```

```text
i = 1, pref[i] = -2
index:   0    1    2    3
pref :  [0,  -2,   3,   2]
              ^
              i

count before: 2
```

```text
j = 2
index:   0    1    2    3
pref :  [0,  -2,   3,   2]
              ^    ^
              i    j

calculation : pref[2] - pref[1] = 3 - (-2) = 5
result      : invalid
count after : 2
```

```text
j = 3
index:   0    1    2    3
pref :  [0,  -2,   3,   2]
              ^         ^
              i         j

calculation : pref[3] - pref[1] = 2 - (-2) = 4
result      : invalid
count after : 2
```

```text
i = 2, pref[i] = 3
index:   0    1    2    3
pref :  [0,  -2,   3,   2]
                   ^
                   i

j = 3
index:   0    1    2    3
pref :  [0,  -2,   3,   2]
                   ^    ^
                   i    j

calculation : pref[3] - pref[2] = 2 - 3 = -1
result      : valid
range       : nums[2..2] = [-1]
action      : count++
count after : 3
```

```text
Final valid ranges:
nums[0..0] = [-2]
nums[0..2] = [-2, 5, -1]
nums[2..2] = [-1]

Final answer = 3

Merge sort optimization:
The real code avoids checking all pairs.
It keeps prefix sums sorted in each half and counts valid j positions using two pointers.
```

---

# 22. Number of Wonderful Substrings

## Problem Statement

A wonderful string is a string where at most one character appears odd number of times.

Given a string `word` containing only first 10 lowercase letters `a` to `j`, return the number of wonderful non-empty substrings.

## Example

```text
Input : word = "aba"
Output: 4
```

Wonderful substrings:

```text
"a"
"b"
"a"
"aba"
```

## Pattern

```text
prefix bitmask parity
```

## Result

```text
count = 4
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long wonderfulSubstrings(string word) {
    vector<long long> freq(1 << 10, 0);
    freq[0] = 1;

    int mask = 0;
    long long ans = 0;

    for (char ch : word) {
        int bit = ch - 'a';
        mask ^= (1 << bit);

        ans += freq[mask];

        for (int b = 0; b < 10; b++) {
            ans += freq[mask ^ (1 << b)];
        }

        freq[mask]++;
    }

    return ans;
}

int main() {
    string word = "aba";
    cout << wonderfulSubstrings(word) << "\n";
}
```

## Dry Run — Bitmask Prefix Diagram

```text
word = "aba"
Only letters a..j are considered.
mask bit = 1 means that character count is odd.
Wonderful substring means at most one odd bit.

Initial:
mask = 0000000000
freq[0] = 1
ans = 0
```

```text
i = 0, char = 'a'
word:  a   b   a
       ^

bit toggled : a-bit
mask before : 0000000000
mask after  : 0000000001

same mask count: freq[0000000001] = 0
one odd allowed:
  toggle a -> 0000000000, freq = 1
  other toggles -> 0

action     : ans += 1
freq update: freq[0000000001]++
ans        : 1

Visual valid substring:
"a"
```

```text
i = 1, char = 'b'
word:  a   b   a
           ^

mask before : 0000000001
mask after  : 0000000011

same mask count: 0
one odd allowed:
  toggle a -> 0000000010, freq = 0
  toggle b -> 0000000001, freq = 1

action     : ans += 1
freq update: freq[0000000011]++
ans        : 2

Visual valid substring:
"b"
```

```text
i = 2, char = 'a'
word:  a   b   a
               ^

mask before : 0000000011
mask after  : 0000000010

same mask count: freq[0000000010] = 0
one odd allowed:
  toggle a -> 0000000011, freq = 1  => substring "a"
  toggle b -> 0000000000, freq = 1  => substring "aba"

action     : ans += 2
freq update: freq[0000000010]++
ans        : 4

Final wonderful substrings:
"a", "b", "a", "aba"
Final answer = 4
```

---

# Final Revision Map

```text
Basic range sum
→ prefix array

Count subarray sum k
→ prefix + hashmap frequency

Longest subarray sum k
→ prefix + earliest index

Divisible by k
→ prefix modulo

Equal 0 and 1
→ convert 0 to -1, use prefix balance

Odd count exactly k
→ odd = 1, even = 0, use prefix count

Many range updates
→ difference array

Matrix rectangle query
→ 2D prefix sum

Shortest subarray at least k with negatives
→ prefix + monotonic deque

Character odd/even parity
→ prefix bitmask
```

---

# Must-Solve Order

```text
1. Running Sum of Array
2. Range Sum Query Immutable
3. Find Pivot Index
4. Maximum Average Subarray I
5. Subarray Sum Equals K
6. Maximum Size Subarray Sum Equals K
7. Binary Subarrays With Sum
8. Subarray Sums Divisible by K
9. Continuous Subarray Sum
10. Make Sum Divisible by P
11. Contiguous Array
12. Count Number of Nice Subarrays
13. Range Addition
14. Corporate Flight Bookings
15. Car Pooling
16. Range Sum Query 2D Immutable
17. Matrix Block Sum
18. Shortest Subarray With Sum At Least K
19. Count of Range Sum
20. Number of Wonderful Substrings
```

---

# One-Line Mental Model

```text
Prefix sum remembers the past so the current index can answer a range question instantly.
```

