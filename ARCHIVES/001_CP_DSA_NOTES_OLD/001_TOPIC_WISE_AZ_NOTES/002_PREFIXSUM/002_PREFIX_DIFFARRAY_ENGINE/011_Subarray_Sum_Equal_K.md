# 011_Subarray_Sum_Equal_K.md — MiniPrefixSumDifferenceEngine

# Subarray Sum Equal K

> This is one of the most important Prefix Sum + HashMap patterns for FAANG and CP.
>
> Core idea:
>
> ```text
> currentPrefix - previousPrefix = K
> ```
>
> So:
>
> ```text
> previousPrefix = currentPrefix - K
> ```

---

## Clickable Index

1. [What Is Subarray Sum Equal K?](#1-what-is-subarray-sum-equal-k)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Problem Form](#3-core-problem-form)
4. [Brute Force Approach](#4-brute-force-approach)
5. [Prefix Sum Algebra](#5-prefix-sum-algebra)
6. [HashMap Frequency Idea](#6-hashmap-frequency-idea)
7. [Why `freq[0] = 1` Is Needed](#7-why-freq0--1-is-needed)
8. [Step-by-Step Dry Run 1 — Positive Numbers](#8-step-by-step-dry-run-1--positive-numbers)
9. [Step-by-Step Dry Run 2 — Negative Numbers](#9-step-by-step-dry-run-2--negative-numbers)
10. [Sliding Window vs Prefix HashMap](#10-sliding-window-vs-prefix-hashmap)
11. [Problem Form 1 — Count Subarrays Equal K](#11-problem-form-1--count-subarrays-equal-k)
12. [Problem Form 2 — Array With Negative Numbers](#12-problem-form-2--array-with-negative-numbers)
13. [Problem Form 3 — Binary Array Sum Goal](#13-problem-form-3--binary-array-sum-goal)
14. [Problem Form 4 — Count Zero Sum Subarrays](#14-problem-form-4--count-zero-sum-subarrays)
15. [Problem Form 5 — Count Net Balance Windows](#15-problem-form-5--count-net-balance-windows)
16. [Real World Model 1 — Financial Transaction Windows](#16-real-world-model-1--financial-transaction-windows)
17. [Real World Model 2 — Kafka Event Delta Stream](#17-real-world-model-2--kafka-event-delta-stream)
18. [Real World Model 3 — Metrics Threshold Window](#18-real-world-model-3--metrics-threshold-window)
19. [Real World Model 4 — User Activity Score Window](#19-real-world-model-4--user-activity-score-window)
20. [Decision Tree](#20-decision-tree)
21. [Common Mistakes](#21-common-mistakes)
22. [Complexity](#22-complexity)
23. [Reusable C++ Templates](#23-reusable-c-templates)
24. [CP / FAANG Problem Forms](#24-cp--faang-problem-forms)
25. [Practice Checklist](#25-practice-checklist)
26. [Next Step](#26-next-step)

---

## 1. What Is Subarray Sum Equal K?

Given an array:

```text
nums[]
```

Count how many continuous subarrays have sum exactly:

```text
K
```

Example:

```text
nums = [1, 1, 1]
K = 2
```

Valid subarrays:

```text
[1,1] at index 0..1
[1,1] at index 1..2
```

Answer:

```text
2
```

---

## 2. Why This Topic Matters

This is a high-frequency pattern because it combines:

```text
prefix sum
hashmap
subarray transformation
negative number handling
counting previous states
```

It appears in:

```text
LeetCode
Google OA
Meta OA
Amazon OA
Codeforces C/D
AtCoder
financial transaction windows
event stream analysis
metrics window detection
```

---

## 3. Core Problem Form

Problem usually says:

```text
count subarrays with sum equal to k
```

or:

```text
find number of continuous windows where total is target
```

Important signal:

```text
array may contain negative numbers
```

If negative numbers exist, sliding window usually fails.

Use:

```text
Prefix Sum + HashMap
```

---

## 4. Brute Force Approach

Try all subarrays:

```cpp
for (int i = 0; i < n; i++) {
    int sum = 0;

    for (int j = i; j < n; j++) {
        sum += nums[j];

        if (sum == k) {
            answer++;
        }
    }
}
```

Complexity:

```text
O(N^2)
```

This may pass small constraints, but not large ones.

---

## 5. Prefix Sum Algebra

Let:

```text
prefix[j] = nums[0] + nums[1] + ... + nums[j]
```

Sum of subarray:

```text
i..j
```

is:

```text
prefix[j] - prefix[i-1]
```

We want:

```text
prefix[j] - prefix[i-1] = K
```

Rearrange:

```text
prefix[i-1] = prefix[j] - K
```

So while standing at current index `j`, if we know:

```text
how many previous prefix sums equal currentPrefix - K
```

then we know how many valid subarrays end at `j`.

---

## 6. HashMap Frequency Idea

Maintain:

```text
freq[prefixSum] = how many times this prefix sum appeared before
```

At each element:

```text
currentPrefix += nums[i]
need = currentPrefix - K
answer += freq[need]
freq[currentPrefix]++
```

Why frequency?

Because the same prefix sum may appear multiple times.

Each previous occurrence can create a valid subarray.

---

## 7. Why `freq[0] = 1` Is Needed

Before reading any element:

```text
prefix = 0
```

This represents the empty prefix.

Example:

```text
nums = [3]
K = 3
```

At index 0:

```text
prefix = 3
need = prefix - K = 0
```

If:

```text
freq[0] = 1
```

then we count subarray:

```text
[3]
```

Without `freq[0] = 1`, subarrays starting at index 0 are missed.

---

## 8. Step-by-Step Dry Run 1 — Positive Numbers

Input:

```text
nums = [1, 1, 1]
K = 2
```

Initialize:

```text
freq = {0:1}
prefix = 0
answer = 0
```

---

### i = 0

```text
x = 1
prefix = 0 + 1 = 1
need = 1 - 2 = -1
freq[-1] = 0
answer = 0
freq[1]++
```

State:

```text
freq = {0:1, 1:1}
answer = 0
```

---

### i = 1

```text
x = 1
prefix = 1 + 1 = 2
need = 2 - 2 = 0
freq[0] = 1
answer += 1
```

Valid subarray:

```text
[1,1] from 0..1
```

Update:

```text
freq[2]++
```

State:

```text
freq = {0:1, 1:1, 2:1}
answer = 1
```

---

### i = 2

```text
x = 1
prefix = 2 + 1 = 3
need = 3 - 2 = 1
freq[1] = 1
answer += 1
```

Valid subarray:

```text
[1,1] from 1..2
```

Final:

```text
answer = 2
```

---

## 9. Step-by-Step Dry Run 2 — Negative Numbers

Input:

```text
nums = [1, -1, 1, -1, 1]
K = 0
```

Initialize:

```text
freq = {0:1}
prefix = 0
answer = 0
```

Prefix sequence:

```text
i=0: prefix=1
i=1: prefix=0
i=2: prefix=1
i=3: prefix=0
i=4: prefix=1
```

Whenever:

```text
currentPrefix - K = currentPrefix
```

has appeared before, we found zero-sum subarrays.

---

### Detailed Table

| i | nums[i] | prefix | need | freq[need] before | answer added |
|---|---:|---:|---:|---:|---:|
| 0 | 1  | 1 | 1 | 0 | 0 |
| 1 | -1 | 0 | 0 | 1 | 1 |
| 2 | 1  | 1 | 1 | 1 | 1 |
| 3 | -1 | 0 | 0 | 2 | 2 |
| 4 | 1  | 1 | 1 | 2 | 2 |

Total:

```text
answer = 6
```

This shows why frequency matters.

---

## 10. Sliding Window vs Prefix HashMap

### Sliding Window Works When

```text
all numbers are non-negative
```

because increasing right pointer increases sum.

---

### Sliding Window Fails When

```text
negative numbers exist
```

because adding a number can decrease the sum.

Example:

```text
[1, -1, 1]
```

The sum is not monotonic.

---

### Prefix HashMap Works With

```text
positive numbers
negative numbers
zero
mixed values
```

So for general subarray sum equals K:

```text
use Prefix Sum + HashMap
```

---

## 11. Problem Form 1 — Count Subarrays Equal K

### Problem

Count subarrays with sum equal to `K`.

Input:

```text
nums = [1, 2, 3]
K = 3
```

Valid subarrays:

```text
[1,2]
[3]
```

Answer:

```text
2
```

---

### Pattern Recognition

```text
count subarrays
sum equals target
array may contain any integer
```

Use:

```text
Prefix Sum + HashMap Frequency
```

---

### Step-by-Step Working

Initialize:

```text
freq[0] = 1
prefix = 0
answer = 0
```

At index 0:

```text
prefix = 1
need = -2
not found
freq[1]++
```

At index 1:

```text
prefix = 3
need = 0
freq[0] = 1
answer = 1
freq[3]++
```

At index 2:

```text
prefix = 6
need = 3
freq[3] = 1
answer = 2
freq[6]++
```

Final:

```text
2
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int subarraySum(vector<int>& nums, int k) {
    unordered_map<long long, int> freq;

    // Empty prefix exists once.
    freq[0] = 1;

    long long prefix = 0;
    int answer = 0;

    for (int x : nums) {
        prefix += x;

        // Need previousPrefix = currentPrefix - k.
        long long need = prefix - k;

        if (freq.count(need)) {
            answer += freq[need];
        }

        // Store current prefix for future subarrays.
        freq[prefix]++;
    }

    return answer;
}

int main() {
    vector<int> nums = {1, 2, 3};
    int k = 3;

    cout << subarraySum(nums, k) << "\n";

    return 0;
}
```

---

## 12. Problem Form 2 — Array With Negative Numbers

### Problem

Count subarrays with sum equal to target when array has negatives.

Input:

```text
nums = [1, -1, 1, -1, 1]
K = 0
```

---

### Why This Is Important

Sliding window fails because values can decrease.

Prefix hash works.

---

### Simulation

Prefix values:

```text
0 initially
1
0
1
0
1
```

Equal prefix sums mean:

```text
subarray between them has sum 0
```

Answer:

```text
6
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int countZeroTarget(vector<int>& nums, int k) {
    unordered_map<long long, int> freq;

    freq[0] = 1;

    long long prefix = 0;
    int ans = 0;

    for (int x : nums) {
        prefix += x;

        ans += freq[prefix - k];

        freq[prefix]++;
    }

    return ans;
}

int main() {
    vector<int> nums = {1, -1, 1, -1, 1};

    cout << countZeroTarget(nums, 0) << "\n";

    return 0;
}
```

---

## 13. Problem Form 3 — Binary Array Sum Goal

### Problem

Given binary array:

```text
nums = [1, 0, 1, 0, 1]
goal = 2
```

Count subarrays with sum equal to `goal`.

---

### Pattern

```text
Prefix Sum + HashMap
```

Because binary sum still creates prefix states.

---

### Simulation

Valid subarrays with two ones include:

```text
[1,0,1]
[1,0,1,0]
[0,1,0,1]
[1,0,1]
```

Answer:

```text
4
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int numSubarraysWithSum(vector<int>& nums, int goal) {
    unordered_map<int, int> freq;

    freq[0] = 1;

    int prefix = 0;
    int ans = 0;

    for (int x : nums) {
        prefix += x;

        ans += freq[prefix - goal];

        freq[prefix]++;
    }

    return ans;
}

int main() {
    vector<int> nums = {1, 0, 1, 0, 1};
    int goal = 2;

    cout << numSubarraysWithSum(nums, goal) << "\n";

    return 0;
}
```

---

## 14. Problem Form 4 — Count Zero Sum Subarrays

### Problem

Count subarrays whose sum is zero.

Input:

```text
nums = [3, 4, -7, 1, 3, -4]
```

---

### Pattern

```text
same prefix sum repeated
```

If:

```text
prefix[i] == prefix[j]
```

then:

```text
sum(i+1..j) = 0
```

---

### Simulation

Prefix:

```text
0
3
7
0
1
4
0
```

Prefix `0` appears 3 times.

That alone contributes:

```text
C(3,2) = 3
```

Other repeated values also contribute.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countZeroSumSubarrays(vector<int>& nums) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int x : nums) {
        prefix += x;

        // If same prefix appeared before,
        // subarray between old index and current has sum zero.
        ans += freq[prefix];

        freq[prefix]++;
    }

    return ans;
}

int main() {
    vector<int> nums = {3, 4, -7, 1, 3, -4};

    cout << countZeroSumSubarrays(nums) << "\n";

    return 0;
}
```

---

## 15. Problem Form 5 — Count Net Balance Windows

### Problem

Given transaction deltas:

```text
+income
-expense
```

Count windows whose net balance change equals target.

Example:

```text
transactions = [100, -20, -30, 50, -100]
target = 50
```

---

### Pattern

```text
Prefix Sum + HashMap
```

---

### Simulation

We need intervals where:

```text
sum = 50
```

This could be:

```text
[100, -20, -30] = 50
[50] = 50
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countTransactionWindows(vector<int>& tx, int target) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int delta : tx) {
        prefix += delta;

        ans += freq[prefix - target];

        freq[prefix]++;
    }

    return ans;
}

int main() {
    vector<int> tx = {100, -20, -30, 50, -100};
    int target = 50;

    cout << countTransactionWindows(tx, target) << "\n";

    return 0;
}
```

---

## 16. Real World Model 1 — Financial Transaction Windows

### Scenario

A banking or fintech system stores transaction deltas:

```text
+100 deposit
-40 purchase
+20 refund
-80 withdrawal
```

Question:

```text
How many continuous transaction windows have net change K?
```

---

### Simulation

Transactions:

```text
[100, -40, 20, -80, 50]
K = 60
```

Window:

```text
100 - 40 = 60
```

Another possible window may appear later.

This is the same as:

```text
Subarray Sum Equal K
```

---

### Backend Mapping

This can model:

```text
fraud detection windows
net spending target
cash-flow segment analysis
ledger interval queries
transaction anomaly detection
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countNetChangeWindows(vector<int>& transactions, int target) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long count = 0;

    for (int delta : transactions) {
        prefix += delta;

        count += freq[prefix - target];

        freq[prefix]++;
    }

    return count;
}
```

---

## 17. Real World Model 2 — Kafka Event Delta Stream

### Scenario

A Kafka topic stores event deltas:

```text
+5 users joined
-2 users left
+3 users joined
```

Question:

```text
How many continuous event segments changed active users by exactly K?
```

---

### Simulation

Events:

```text
[5, -2, 3, -1, 2]
K = 6
```

Segment:

```text
5 - 2 + 3 = 6
```

This is:

```text
subarray sum equal K over event deltas
```

---

### System Mapping

Useful for:

```text
event stream analytics
state delta analysis
consumer lag patterns
windowed stream inspection
incident replay
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countEventDeltaWindows(vector<int>& events, int targetDelta) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int delta : events) {
        prefix += delta;

        ans += freq[prefix - targetDelta];

        freq[prefix]++;
    }

    return ans;
}
```

---

## 18. Real World Model 3 — Metrics Threshold Window

### Scenario

A monitoring system records metric deltas per minute:

```text
CPU score changes
error count changes
request load deltas
```

Question:

```text
How many continuous time windows accumulate exactly threshold K?
```

---

### Simulation

Metric deltas:

```text
[2, 3, -1, 4, -2]
K = 4
```

Valid windows:

```text
[4]
[3,-1,4,-2]
```

---

### System Mapping

Used in:

```text
monitoring analytics
alert window analysis
error burst detection
capacity planning
SLO burn-rate investigation
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countMetricWindows(vector<int>& deltas, int threshold) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int d : deltas) {
        prefix += d;

        ans += freq[prefix - threshold];

        freq[prefix]++;
    }

    return ans;
}
```

---

## 19. Real World Model 4 — User Activity Score Window

### Scenario

A product analytics system assigns user activity scores:

```text
login = +1
purchase = +5
refund = -3
logout = -1
```

Question:

```text
How many continuous sessions/windows have activity score K?
```

---

### Simulation

Activity stream:

```text
[1, 5, -3, -1, 2]
K = 3
```

Window:

```text
5 - 3 + 1? 
```

The exact detection is handled by prefix hashmap.

---

### Product Mapping

This helps model:

```text
engagement windows
session scoring
risk scoring intervals
behavior analytics
conversion journey analysis
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countActivityScoreWindows(vector<int>& activity, int score) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int x : activity) {
        prefix += x;

        ans += freq[prefix - score];

        freq[prefix]++;
    }

    return ans;
}
```

---

## 20. Decision Tree

```text
Subarray problem?
|
+-- Need sum exactly K?
|   |
|   +-- Array has negative/zero/mixed values?
|       |
|       +-- Prefix Sum + HashMap
|
+-- All values positive and need shortest/longest?
|   |
|   +-- Sliding Window may work
|
+-- Need count of subarrays?
|   |
|   +-- HashMap frequency
|
+-- Need existence only?
    |
    +-- HashSet of previous prefixes may work
```

---

## 21. Common Mistakes

### Mistake 1 — Forgetting `freq[0] = 1`

Without it, subarrays starting at index 0 are missed.

---

### Mistake 2 — Updating Frequency Before Counting

Wrong order:

```cpp
freq[prefix]++;
ans += freq[prefix - k];
```

Correct order:

```cpp
ans += freq[prefix - k];
freq[prefix]++;
```

---

### Mistake 3 — Using Sliding Window With Negatives

Sliding window can fail if numbers are negative.

Use prefix hashmap.

---

### Mistake 4 — Using `int` For Large Prefix

Use:

```cpp
long long
```

when values are large.

---

### Mistake 5 — Storing Only Seen Prefix Instead Of Frequency

For counting, we need:

```text
frequency
```

not just existence.

---

## 22. Complexity

Time:

```text
O(N)
```

Space:

```text
O(N)
```

Because hashmap can store many prefix sums.

---

## 23. Reusable C++ Templates

### Template 1 — Count Subarrays Equal K

```cpp
long long countSubarraysEqualK(vector<int>& nums, int k) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int x : nums) {
        prefix += x;

        ans += freq[prefix - k];

        freq[prefix]++;
    }

    return ans;
}
```

---

### Template 2 — Count Zero Sum Subarrays

```cpp
long long countZeroSum(vector<int>& nums) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int x : nums) {
        prefix += x;

        ans += freq[prefix];

        freq[prefix]++;
    }

    return ans;
}
```

---

### Template 3 — Existence Of Subarray Sum K

```cpp
bool existsSubarraySumK(vector<int>& nums, int k) {
    unordered_set<long long> seen;

    seen.insert(0);

    long long prefix = 0;

    for (int x : nums) {
        prefix += x;

        if (seen.count(prefix - k)) {
            return true;
        }

        seen.insert(prefix);
    }

    return false;
}
```

---

## 24. CP / FAANG Problem Forms

### Problem 1 — Subarray Sum Equals K

Recognition:

```text
count continuous subarrays with sum k
```

Pattern:

```text
prefix sum + hashmap frequency
```

---

### Problem 2 — Zero Sum Subarrays

Recognition:

```text
subarray sum equals 0
```

Pattern:

```text
same prefix repeated
```

---

### Problem 3 — Binary Subarray With Sum

Recognition:

```text
binary array, count subarrays with goal
```

Pattern:

```text
prefix count
```

---

### Problem 4 — Event Delta Window

Recognition:

```text
continuous event segment has target total
```

Pattern:

```text
prefix delta hashmap
```

---

### Problem 5 — Financial Net Change Window

Recognition:

```text
transaction interval sum equals target
```

Pattern:

```text
prefix sum + hashmap
```

---

## 25. Practice Checklist

Before coding:

```text
1. Is it a subarray problem?
2. Need sum exactly K?
3. Need count, not just existence?
4. Are negatives possible?
5. Did I initialize freq[0] = 1?
6. Did I compute need = prefix - K?
7. Did I count before inserting current prefix?
8. Should prefix be long long?
9. Is sliding window invalid here?
10. Did I test K = 0?
```

---

## 26. Next Step

```text
012_Longest_Subarray_With_Sum_K.md
```

Next file covers:

```text
longest length
earliest prefix index
hashmap storing first occurrence
positive-only vs mixed-number logic
