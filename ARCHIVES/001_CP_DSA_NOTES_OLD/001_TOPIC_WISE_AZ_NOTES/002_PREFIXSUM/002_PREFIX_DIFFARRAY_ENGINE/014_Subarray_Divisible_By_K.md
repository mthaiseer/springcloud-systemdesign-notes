# 014_Subarray_Divisible_By_K.md — MiniPrefixSumDifferenceEngine

# Subarray Divisible By K

> This is one of the most important **Prefix Sum + Modulo + Frequency HashMap** patterns.
>
> Core idea:
>
> ```text
> If two prefix sums have the same remainder modulo K,
> then the subarray between them has sum divisible by K.
> ```

---

## Clickable Index

1. [What Is Subarray Divisible By K?](#1-what-is-subarray-divisible-by-k)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Problem Form](#3-core-problem-form)
4. [Brute Force Approach](#4-brute-force-approach)
5. [Prefix Modulo Algebra](#5-prefix-modulo-algebra)
6. [Same Remainder Rule](#6-same-remainder-rule)
7. [Frequency HashMap / Array Model](#7-frequency-hashmap--array-model)
8. [Why `freq[0] = 1` Is Needed](#8-why-freq0--1-is-needed)
9. [Negative Modulo Normalization](#9-negative-modulo-normalization)
10. [Step-by-Step Dry Run 1 — Positive Values](#10-step-by-step-dry-run-1--positive-values)
11. [Step-by-Step Dry Run 2 — Negative Values](#11-step-by-step-dry-run-2--negative-values)
12. [Problem Form 1 — Count Subarrays Divisible By K](#12-problem-form-1--count-subarrays-divisible-by-k)
13. [Problem Form 2 — Count Subarrays With Sum Multiple Of K](#13-problem-form-2--count-subarrays-with-sum-multiple-of-k)
14. [Problem Form 3 — Longest Subarray Divisible By K](#14-problem-form-3--longest-subarray-divisible-by-k)
15. [Problem Form 4 — Remove Shortest Subarray To Make Sum Divisible By P](#15-problem-form-4--remove-shortest-subarray-to-make-sum-divisible-by-p)
16. [Problem Form 5 — Pair / Group By Remainder Frequency](#16-problem-form-5--pair--group-by-remainder-frequency)
17. [Real World Model 1 — Batch Size Divisibility](#17-real-world-model-1--batch-size-divisibility)
18. [Real World Model 2 — Kafka Partition Remainder Windows](#18-real-world-model-2--kafka-partition-remainder-windows)
19. [Real World Model 3 — Billing Cycle Grouping](#19-real-world-model-3--billing-cycle-grouping)
20. [Real World Model 4 — Load Balancer Bucket Windows](#20-real-world-model-4--load-balancer-bucket-windows)
21. [Decision Tree](#21-decision-tree)
22. [Common Mistakes](#22-common-mistakes)
23. [Complexity](#23-complexity)
24. [Reusable C++ Templates](#24-reusable-c-templates)
25. [CP / FAANG Problem Forms](#25-cp--faang-problem-forms)
26. [Practice Checklist](#26-practice-checklist)
27. [Next Step](#27-next-step)

---

## 1. What Is Subarray Divisible By K?

Given an array:

```text
nums[]
```

and integer:

```text
K
```

Count subarrays whose sum is divisible by `K`.

That means subarray sum satisfies:

```text
sum % K == 0
```

Example:

```text
nums = [4, 5, 0, -2, -3, 1]
K = 5
```

Answer:

```text
7
```

---

## 2. Why This Topic Matters

This pattern appears in:

```text
LeetCode hard/medium
Google OA
Meta OA
Amazon OA
Codeforces C/D
AtCoder
modulo grouping problems
stream batching problems
partitioning problems
```

It teaches a key CP idea:

```text
Use modulo state instead of raw sum.
```

---

## 3. Core Problem Form

Problem usually says:

```text
count subarrays whose sum is divisible by K
```

or:

```text
count continuous windows whose total is multiple of K
```

The direct prefix-sum target formula changes into:

```text
same remainder matching
```

---

## 4. Brute Force Approach

Try every subarray:

```cpp
for (int i = 0; i < n; i++) {
    long long sum = 0;

    for (int j = i; j < n; j++) {
        sum += nums[j];

        if (sum % k == 0) {
            answer++;
        }
    }
}
```

Complexity:

```text
O(N^2)
```

Too slow for large constraints.

---

## 5. Prefix Modulo Algebra

Let:

```text
prefix[j] = nums[0] + nums[1] + ... + nums[j]
```

Subarray sum:

```text
sum(i..j) = prefix[j] - prefix[i-1]
```

We want:

```text
(prefix[j] - prefix[i-1]) % K == 0
```

This means:

```text
prefix[j] % K == prefix[i-1] % K
```

So:

```text
same remainder => subarray between them divisible by K
```

---

## 6. Same Remainder Rule

If two prefix sums have the same remainder:

```text
prefixA % K = r
prefixB % K = r
```

Then:

```text
(prefixB - prefixA) % K = 0
```

Therefore:

```text
subarray between A and B is divisible by K
```

This is the complete trick.

---

## 7. Frequency HashMap / Array Model

We store:

```text
freq[remainder] = how many prefix sums had this remainder
```

At each index:

```text
prefix += nums[i]
rem = prefix % K
answer += freq[rem]
freq[rem]++
```

Because every previous prefix with same remainder forms a valid divisible subarray.

If `K` is small, use:

```cpp
vector<long long> freq(k, 0);
```

If `K` is large or unusual, use:

```cpp
unordered_map<int,long long>
```

---

## 8. Why `freq[0] = 1` Is Needed

Before array starts:

```text
prefix = 0
```

Its remainder is:

```text
0
```

So initialize:

```cpp
freq[0] = 1;
```

This counts subarrays starting at index `0`.

Example:

```text
nums = [5]
K = 5
```

At index `0`:

```text
prefix = 5
rem = 0
answer += freq[0] = 1
```

Correct.

---

## 9. Negative Modulo Normalization

In C++, negative modulo can be negative:

```cpp
-2 % 5 = -2
```

But we need remainder in:

```text
0..K-1
```

Normalize:

```cpp
rem = ((prefix % k) + k) % k;
```

Example:

```text
prefix = -2
k = 5
```

C++ gives:

```text
-2
```

Normalized:

```text
((-2 % 5) + 5) % 5
= (-2 + 5) % 5
= 3
```

Correct positive remainder:

```text
3
```

---

## 10. Step-by-Step Dry Run 1 — Positive Values

Input:

```text
nums = [4, 5, 0, -2, -3, 1]
K = 5
```

Initialize:

```text
freq[0] = 1
prefix = 0
answer = 0
```

Table:

| i | x | prefix | rem | freq[rem] before | answer |
|---|---:|---:|---:|---:|---:|
| 0 | 4  | 4 | 4 | 0 | 0 |
| 1 | 5  | 9 | 4 | 1 | 1 |
| 2 | 0  | 9 | 4 | 2 | 3 |
| 3 | -2 | 7 | 2 | 0 | 3 |
| 4 | -3 | 4 | 4 | 3 | 6 |
| 5 | 1  | 5 | 0 | 1 | 7 |

Final answer:

```text
7
```

---

## 11. Step-by-Step Dry Run 2 — Negative Values

Input:

```text
nums = [-1, 2, 9]
K = 2
```

Subarrays divisible by 2:

```text
[-1,2,9] = 10
[2] = 2
```

Answer:

```text
2
```

Dry run:

```text
freq[0] = 1
prefix = 0
```

### i = 0

```text
x = -1
prefix = -1
raw rem = -1 % 2 = -1
normalized rem = 1
answer += freq[1] = 0
freq[1]++
```

### i = 1

```text
x = 2
prefix = 1
rem = 1
answer += freq[1] = 1
```

Subarray:

```text
[2]
```

### i = 2

```text
x = 9
prefix = 10
rem = 0
answer += freq[0] = 1
```

Subarray:

```text
[-1,2,9]
```

Final:

```text
2
```

---

## 12. Problem Form 1 — Count Subarrays Divisible By K

### Problem

Count subarrays whose sum is divisible by `K`.

Input:

```text
nums = [4, 5, 0, -2, -3, 1]
K = 5
```

Output:

```text
7
```

---

### Pattern Recognition

Use this when:

```text
subarray sum
divisible by K
count all
negative numbers possible
```

Pattern:

```text
Prefix Modulo + Remainder Frequency
```

---

### Step-by-Step Working

```text
1. prefix starts at 0
2. freq[0] = 1
3. For each number:
   prefix += x
   rem = normalized(prefix % K)
   answer += freq[rem]
   freq[rem]++
```

Same remainder means divisible difference.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long subarraysDivByK(vector<int>& nums, int k) {
    vector<long long> freq(k, 0);

    // Empty prefix remainder 0.
    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int x : nums) {
        prefix += x;

        // Normalize to handle negative numbers.
        int rem = ((prefix % k) + k) % k;

        answer += freq[rem];

        freq[rem]++;
    }

    return answer;
}

int main() {
    vector<int> nums = {4, 5, 0, -2, -3, 1};
    int k = 5;

    cout << subarraysDivByK(nums, k) << "\n";

    return 0;
}
```

---

### Complexity

```text
Time  : O(N)
Space : O(K)
```

---

## 13. Problem Form 2 — Count Subarrays With Sum Multiple Of K

### Problem

Count subarrays whose sum is any multiple of `K`.

This is the same as:

```text
sum % K == 0
```

---

### Problem Simulation

Input:

```text
nums = [2, 3, 1, 6]
K = 3
```

Valid subarrays:

```text
[3]
[2,3,1]
[6]
[3,1,6]
```

Answer:

```text
4
```

---

### Pattern Recognition

Keywords:

```text
multiple of K
divisible by K
modulo K equals 0
```

All mean:

```text
same remainder prefix grouping
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countMultipleOfK(vector<int>& nums, int k) {
    vector<long long> freq(k, 0);

    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int x : nums) {
        prefix += x;

        int rem = ((prefix % k) + k) % k;

        ans += freq[rem];

        freq[rem]++;
    }

    return ans;
}

int main() {
    vector<int> nums = {2, 3, 1, 6};
    int k = 3;

    cout << countMultipleOfK(nums, k) << "\n";

    return 0;
}
```

---

## 14. Problem Form 3 — Longest Subarray Divisible By K

### Problem

Find the longest subarray whose sum is divisible by `K`.

Input:

```text
nums = [2, 7, 6, 1, 4, 5]
K = 3
```

---

### Pattern Recognition

For count:

```text
freq[rem]
```

For longest:

```text
firstIndex[rem]
```

Same remainder at two indices means subarray between them divisible by K.

---

### Step-by-Step Simulation

Prefix remainders:

```text
initial rem 0 at index -1
```

Track earliest index of each remainder.

If same remainder appears again:

```text
length = currentIndex - firstIndex[rem]
```

Maximize length.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestSubarrayDivByK(vector<int>& nums, int k) {
    unordered_map<int, int> firstIndex;

    firstIndex[0] = -1;

    long long prefix = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {
        prefix += nums[i];

        int rem = ((prefix % k) + k) % k;

        if (firstIndex.count(rem)) {
            best = max(best, i - firstIndex[rem]);
        } else {
            firstIndex[rem] = i;
        }
    }

    return best;
}

int main() {
    vector<int> nums = {2, 7, 6, 1, 4, 5};
    int k = 3;

    cout << longestSubarrayDivByK(nums, k) << "\n";

    return 0;
}
```

---

## 15. Problem Form 4 — Remove Shortest Subarray To Make Sum Divisible By P

### Problem

Given array and integer `p`, remove shortest subarray so remaining sum is divisible by `p`.

This is a harder FAANG-style modulo-prefix form.

---

### Key Idea

Let total sum remainder be:

```text
totalRem = totalSum % p
```

We need remove a subarray whose sum remainder is:

```text
totalRem
```

Then remaining sum becomes divisible by `p`.

At current prefix remainder `cur`, we need previous prefix:

```text
needed = (cur - totalRem + p) % p
```

Minimize:

```text
i - previousIndex[needed]
```

---

### Problem Simulation

Input:

```text
nums = [3, 1, 4, 2]
p = 6
```

Total:

```text
10
```

Remainder:

```text
10 % 6 = 4
```

Need remove subarray with sum remainder:

```text
4
```

Subarray:

```text
[4]
```

Length:

```text
1
```

Answer:

```text
1
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minSubarray(vector<int>& nums, int p) {
    long long total = 0;

    for (int x : nums) {
        total += x;
    }

    int target = total % p;

    if (target == 0) {
        return 0;
    }

    unordered_map<int, int> lastIndex;

    // Prefix remainder 0 before array starts.
    lastIndex[0] = -1;

    long long prefix = 0;
    int ans = nums.size();

    for (int i = 0; i < nums.size(); i++) {
        prefix += nums[i];

        int cur = prefix % p;

        // Need previous remainder.
        int need = (cur - target + p) % p;

        if (lastIndex.count(need)) {
            ans = min(ans, i - lastIndex[need]);
        }

        // For shortest subarray, store latest index.
        lastIndex[cur] = i;
    }

    if (ans == nums.size()) {
        return -1;
    }

    return ans;
}

int main() {
    vector<int> nums = {3, 1, 4, 2};
    int p = 6;

    cout << minSubarray(nums, p) << "\n";

    return 0;
}
```

---

## 16. Problem Form 5 — Pair / Group By Remainder Frequency

### Problem

Given prefix sums, count pairs with same remainder.

This is another way to view subarrays divisible by K.

---

### Pattern Recognition

If remainder `r` appears `cnt` times, then number of pairs is:

```text
cnt * (cnt - 1) / 2
```

Each pair corresponds to a subarray divisible by K.

---

### Problem Simulation

Remainders:

```text
0, 4, 4, 4, 2, 4, 0
```

Counts:

```text
rem 0 -> 2
rem 4 -> 4
rem 2 -> 1
```

Pairs:

```text
C(2,2) + C(4,2) + C(1,2)
= 1 + 6 + 0
= 7
```

Answer:

```text
7
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countByRemainderPairs(vector<int>& nums, int k) {
    vector<long long> freq(k, 0);

    long long prefix = 0;

    // Empty prefix remainder.
    freq[0]++;

    for (int x : nums) {
        prefix += x;

        int rem = ((prefix % k) + k) % k;

        freq[rem]++;
    }

    long long ans = 0;

    for (long long cnt : freq) {
        ans += cnt * (cnt - 1) / 2;
    }

    return ans;
}

int main() {
    vector<int> nums = {4, 5, 0, -2, -3, 1};
    int k = 5;

    cout << countByRemainderPairs(nums, k) << "\n";

    return 0;
}
```

---

## 17. Real World Model 1 — Batch Size Divisibility

### Scenario

A backend batches events for processing.

Each minute receives:

```text
events[i]
```

Question:

```text
How many continuous time windows contain total events divisible by batch size K?
```

---

### Problem Simulation

Events per minute:

```text
[4, 5, 0, -2, -3, 1]
batchSize = 5
```

A valid window has total events:

```text
multiple of 5
```

The prefix-remainder method counts all valid windows.

---

### System Mapping

Useful for:

```text
batch processing
ETL windows
stream processing
message bundling
log compaction windows
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countBatchAlignedWindows(vector<int>& events, int batchSize) {
    vector<long long> freq(batchSize, 0);

    freq[0] = 1;

    long long prefix = 0;
    long long windows = 0;

    for (int x : events) {
        prefix += x;

        int rem = ((prefix % batchSize) + batchSize) % batchSize;

        windows += freq[rem];

        freq[rem]++;
    }

    return windows;
}
```

---

## 18. Real World Model 2 — Kafka Partition Remainder Windows

### Scenario

Kafka messages are often partitioned by hash modulo partition count.

Suppose message deltas arrive over time.

Question:

```text
How many continuous windows align evenly with partition count K?
```

This is a conceptual model for modulo-bucket windows.

---

### Problem Simulation

Message count deltas:

```text
[10, -3, 8, 5, -5]
partitions = 5
```

A valid window has sum divisible by:

```text
5
```

This means the window can be evenly grouped into partition-sized batches.

---

### System Mapping

Useful for:

```text
partition balancing analysis
batch flush windows
consumer batch sizing
event grouping
modulo bucket reasoning
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countPartitionAlignedWindows(vector<int>& deltas, int partitions) {
    vector<long long> freq(partitions, 0);

    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int d : deltas) {
        prefix += d;

        int rem = ((prefix % partitions) + partitions) % partitions;

        ans += freq[rem];

        freq[rem]++;
    }

    return ans;
}
```

---

## 19. Real World Model 3 — Billing Cycle Grouping

### Scenario

A billing system groups usage into billing units.

Example:

```text
billing unit = 100 credits
```

Given usage deltas per time window:

```text
usage[i]
```

Question:

```text
How many continuous windows total exactly a multiple of billing unit?
```

---

### Problem Simulation

Usage:

```text
[40, 60, 30, 70, -20, 20]
billingUnit = 100
```

Valid windows include:

```text
[40,60]
[30,70]
[40,60,30,70]
```

---

### System Mapping

Useful for:

```text
billing aggregation
usage accounting
credit systems
cloud cost grouping
quota bucket analysis
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countBillingAlignedWindows(vector<int>& usage, int unit) {
    vector<long long> freq(unit, 0);

    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int u : usage) {
        prefix += u;

        int rem = ((prefix % unit) + unit) % unit;

        ans += freq[rem];

        freq[rem]++;
    }

    return ans;
}
```

---

## 20. Real World Model 4 — Load Balancer Bucket Windows

### Scenario

A load balancer distributes requests into `K` buckets.

Given request deltas over time:

```text
requests[i]
```

Question:

```text
How many continuous windows have total requests divisible by K?
```

Such windows can be evenly distributed across buckets.

---

### Problem Simulation

Requests:

```text
[6, 4, 5, 10, -5]
K = 5
```

Valid windows have total request count multiple of `5`.

---

### System Mapping

Useful for:

```text
load distribution analysis
bucket balancing
rate-limit window sizing
consistent hashing bucket reasoning
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countBucketAlignedWindows(vector<int>& requests, int buckets) {
    vector<long long> freq(buckets, 0);

    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int r : requests) {
        prefix += r;

        int rem = ((prefix % buckets) + buckets) % buckets;

        ans += freq[rem];

        freq[rem]++;
    }

    return ans;
}
```

---

## 21. Decision Tree

```text
Subarray problem?
|
+-- Need sum divisible by K?
|   |
|   +-- Use prefix modulo frequency
|
+-- Need count?
|   |
|   +-- freq[remainder]
|
+-- Need longest?
|   |
|   +-- firstIndex[remainder]
|
+-- Need shortest removal?
|   |
|   +-- lastIndex[remainder]
|
+-- Negative values possible?
    |
    +-- normalize modulo
```

---

## 22. Common Mistakes

### Mistake 1 — Not Normalizing Negative Modulo

Wrong:

```cpp
int rem = prefix % k;
```

Safe:

```cpp
int rem = ((prefix % k) + k) % k;
```

---

### Mistake 2 — Forgetting `freq[0] = 1`

This misses subarrays starting at index `0`.

---

### Mistake 3 — Confusing Target Sum With Remainder

For divisible by K, we do not need:

```text
prefix - K
```

We need:

```text
same remainder
```

---

### Mistake 4 — Using `int` For Large Prefix

Use:

```cpp
long long
```

---

### Mistake 5 — Wrong Map Type For Goal

For count:

```text
freq[rem]
```

For longest:

```text
firstIndex[rem]
```

For shortest removal:

```text
latest index
```

---

## 23. Complexity

For count version:

```text
Time  : O(N)
Space : O(K)
```

If using unordered_map:

```text
Space : O(number of distinct remainders)
```

---

## 24. Reusable C++ Templates

### Template 1 — Count Subarrays Divisible By K

```cpp
long long countDivisibleByK(vector<int>& nums, int k) {
    vector<long long> freq(k, 0);

    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int x : nums) {
        prefix += x;

        int rem = ((prefix % k) + k) % k;

        ans += freq[rem];

        freq[rem]++;
    }

    return ans;
}
```

---

### Template 2 — Longest Divisible By K

```cpp
int longestDivisibleByK(vector<int>& nums, int k) {
    unordered_map<int, int> firstIndex;

    firstIndex[0] = -1;

    long long prefix = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {
        prefix += nums[i];

        int rem = ((prefix % k) + k) % k;

        if (firstIndex.count(rem)) {
            best = max(best, i - firstIndex[rem]);
        } else {
            firstIndex[rem] = i;
        }
    }

    return best;
}
```

---

### Template 3 — Shortest Removal To Make Total Divisible

```cpp
int shortestRemovalForDivisibility(vector<int>& nums, int p) {
    long long total = 0;

    for (int x : nums) total += x;

    int target = total % p;

    if (target == 0) return 0;

    unordered_map<int, int> lastIndex;
    lastIndex[0] = -1;

    long long prefix = 0;
    int ans = nums.size();

    for (int i = 0; i < nums.size(); i++) {
        prefix += nums[i];

        int cur = prefix % p;
        int need = (cur - target + p) % p;

        if (lastIndex.count(need)) {
            ans = min(ans, i - lastIndex[need]);
        }

        lastIndex[cur] = i;
    }

    return ans == nums.size() ? -1 : ans;
}
```

---

## 25. CP / FAANG Problem Forms

---

### Problem 1 — Subarray Sums Divisible By K

#### Recognition

```text
count subarrays
sum divisible by K
```

#### Pattern

```text
Prefix modulo + remainder frequency
```

#### Step-by-Step Working

```text
prefix += nums[i]
rem = normalized(prefix % K)
answer += freq[rem]
freq[rem]++
```

#### Commented C++ Code

```cpp
long long subarraysDivByK(vector<int>& nums, int k) {
    vector<long long> freq(k, 0);
    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int x : nums) {
        prefix += x;

        int rem = ((prefix % k) + k) % k;

        ans += freq[rem];

        freq[rem]++;
    }

    return ans;
}
```

---

### Problem 2 — Longest Subarray Divisible By K

#### Recognition

```text
maximum length subarray whose sum is divisible by K
```

#### Pattern

```text
first occurrence of remainder
```

#### Commented C++ Code

```cpp
int longestSubarrayDivByK(vector<int>& nums, int k) {
    unordered_map<int, int> firstIndex;
    firstIndex[0] = -1;

    long long prefix = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {
        prefix += nums[i];

        int rem = ((prefix % k) + k) % k;

        if (firstIndex.count(rem)) {
            best = max(best, i - firstIndex[rem]);
        } else {
            firstIndex[rem] = i;
        }
    }

    return best;
}
```

---

### Problem 3 — Make Sum Divisible By P

#### Recognition

```text
remove shortest subarray
remaining sum divisible by P
```

#### Pattern

```text
prefix modulo + latest index
```

#### Commented C++ Code

```cpp
int minSubarray(vector<int>& nums, int p) {
    long long total = 0;

    for (int x : nums) total += x;

    int target = total % p;

    if (target == 0) return 0;

    unordered_map<int, int> lastIndex;
    lastIndex[0] = -1;

    long long prefix = 0;
    int ans = nums.size();

    for (int i = 0; i < nums.size(); i++) {
        prefix += nums[i];

        int cur = prefix % p;
        int need = (cur - target + p) % p;

        if (lastIndex.count(need)) {
            ans = min(ans, i - lastIndex[need]);
        }

        lastIndex[cur] = i;
    }

    return ans == nums.size() ? -1 : ans;
}
```

---

### Problem 4 — Remainder Pair Counting

#### Recognition

```text
same modulo group creates valid pairs/subarrays
```

#### Pattern

```text
frequency combination
```

#### Commented C++ Code

```cpp
long long countPairsFromFreq(vector<long long>& freq) {
    long long ans = 0;

    for (long long c : freq) {
        ans += c * (c - 1) / 2;
    }

    return ans;
}
```

---

### Problem 5 — Batch-Aligned Window

#### Recognition

```text
continuous window total divisible by batch size
```

#### Pattern

```text
prefix remainder frequency
```

#### Commented C++ Code

```cpp
long long countBatchAligned(vector<int>& nums, int batch) {
    vector<long long> freq(batch, 0);

    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int x : nums) {
        prefix += x;

        int rem = ((prefix % batch) + batch) % batch;

        ans += freq[rem];

        freq[rem]++;
    }

    return ans;
}
```

---

## 26. Practice Checklist

Before coding:

```text
1. Is the condition divisible by K?
2. Can I use prefix modulo?
3. Do same remainders define a valid subarray?
4. Am I counting or finding longest?
5. For count, use freq[rem]
6. For longest, use firstIndex[rem]
7. For shortest removal, use latest index
8. Did I set freq[0]=1 or firstIndex[0]=-1?
9. Did I normalize negative modulo?
10. Did I use long long prefix?
```

---

## 27. Next Step

```text
015_Prefix_Modulo_Problems.md
```

Next file generalizes modulo-prefix patterns:

```text
divisibility
remainder grouping
circular modulo logic
remove shortest subarray
mod class transformations
```
