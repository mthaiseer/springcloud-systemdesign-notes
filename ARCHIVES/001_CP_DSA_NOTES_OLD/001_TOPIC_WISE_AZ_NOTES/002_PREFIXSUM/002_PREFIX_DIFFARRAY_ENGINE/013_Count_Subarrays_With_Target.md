# 013_Count_Subarrays_With_Target.md — MiniPrefixSumDifferenceEngine

# Count Subarrays With Target

> This file generalizes the prefix-hash counting pattern.
>
> In `011_Subarray_Sum_Equal_K.md`, we counted subarrays with sum exactly `K`.
>
> Here we build a broader decision engine for:
>
> ```text
> count subarrays with target property
> ```
>
> Target can mean:
>
> ```text
> sum = K
> sum = 0
> binary sum = goal
> net delta = target
> transformed balance = 0
> ```

---

## Clickable Index

1. [What Is Count Subarrays With Target?](#1-what-is-count-subarrays-with-target)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Pattern](#3-core-pattern)
4. [Prefix Difference Algebra](#4-prefix-difference-algebra)
5. [Frequency HashMap Model](#5-frequency-hashmap-model)
6. [Count vs Longest Difference](#6-count-vs-longest-difference)
7. [Why `freq[0] = 1` Matters](#7-why-freq0--1-matters)
8. [Step-by-Step Dry Run — Count Target Sum](#8-step-by-step-dry-run--count-target-sum)
9. [Step-by-Step Dry Run — Count Zero Sum](#9-step-by-step-dry-run--count-zero-sum)
10. [Step-by-Step Dry Run — Binary Target](#10-step-by-step-dry-run--binary-target)
11. [Problem Form 1 — Count Subarrays Sum K](#11-problem-form-1--count-subarrays-sum-k)
12. [Problem Form 2 — Count Zero Sum Subarrays](#12-problem-form-2--count-zero-sum-subarrays)
13. [Problem Form 3 — Count Binary Subarrays With Sum Goal](#13-problem-form-3--count-binary-subarrays-with-sum-goal)
14. [Problem Form 4 — Count Balanced 0/1 Subarrays](#14-problem-form-4--count-balanced-01-subarrays)
15. [Problem Form 5 — Count Transaction Windows With Target](#15-problem-form-5--count-transaction-windows-with-target)
16. [Real World Model 1 — Financial Net Movement Windows](#16-real-world-model-1--financial-net-movement-windows)
17. [Real World Model 2 — Kafka Event Delta Target Windows](#17-real-world-model-2--kafka-event-delta-target-windows)
18. [Real World Model 3 — Monitoring Threshold Windows](#18-real-world-model-3--monitoring-threshold-windows)
19. [Real World Model 4 — Product Activity Score Windows](#19-real-world-model-4--product-activity-score-windows)
20. [Decision Tree](#20-decision-tree)
21. [Common Mistakes](#21-common-mistakes)
22. [Complexity](#22-complexity)
23. [Reusable C++ Templates](#23-reusable-c-templates)
24. [CP / FAANG Problem Forms](#24-cp--faang-problem-forms)
25. [Practice Checklist](#25-practice-checklist)
26. [Next Step](#26-next-step)

---

## 1. What Is Count Subarrays With Target?

A subarray is a continuous segment:

```text
nums[l..r]
```

Counting subarrays with target means:

```text
How many continuous segments satisfy a target condition?
```

Example target conditions:

```text
sum == K
sum == 0
number of ones == goal
equal 0 and 1
net transaction movement == target
metric delta == threshold
```

---

## 2. Why This Topic Matters

This is one of the most reusable CP/FAANG patterns.

It appears in:

```text
subarray sum equals K
zero sum subarray count
binary subarray with sum
balanced binary subarray count
financial transaction windows
event stream delta windows
monitoring metric threshold windows
```

The key skill is recognizing that:

```text
count target subarrays
```

often becomes:

```text
count previous prefix states
```

---

## 3. Core Pattern

At current index:

```text
prefix = sum from 0 to current
```

We need previous prefix:

```text
previous = prefix - target
```

So:

```text
answer += freq[prefix - target]
freq[prefix]++
```

---

## 4. Prefix Difference Algebra

Subarray sum from `l` to `r`:

```text
sum(l,r) = prefix[r] - prefix[l-1]
```

We want:

```text
prefix[r] - prefix[l-1] = target
```

Rearrange:

```text
prefix[l-1] = prefix[r] - target
```

So for every current prefix:

```text
count how many old prefixes equal currentPrefix - target
```

---

## 5. Frequency HashMap Model

Use:

```text
unordered_map<prefixSum, frequency>
```

Why frequency?

Because if the same prefix appeared multiple times, each occurrence can start a different valid subarray.

Example:

```text
prefix 0 appears 3 times
```

Number of zero-sum pairs from those appearances:

```text
C(3,2)
```

The online hashmap method counts this incrementally.

---

## 6. Count vs Longest Difference

For counting:

```text
store frequency of prefix
```

For longest:

```text
store earliest index of prefix
```

Comparison:

| Goal | Store |
|---|---|
| Count subarrays | frequency |
| Longest subarray | first index |
| Existence only | set |

---

## 7. Why `freq[0] = 1` Matters

Before array starts:

```text
prefix = 0
```

This empty prefix must be counted once.

Without:

```cpp
freq[0] = 1;
```

you miss subarrays that start at index `0`.

Example:

```text
nums = [3]
target = 3
```

At index `0`:

```text
prefix = 3
need = 0
```

So we need `freq[0]`.

---

## 8. Step-by-Step Dry Run — Count Target Sum

Input:

```text
nums = [1, 2, 3]
target = 3
```

Initialize:

```text
freq = {0:1}
prefix = 0
answer = 0
```

### i = 0

```text
x = 1
prefix = 1
need = 1 - 3 = -2
freq[-2] = 0
answer = 0
freq[1]++
```

### i = 1

```text
x = 2
prefix = 3
need = 3 - 3 = 0
freq[0] = 1
answer = 1
freq[3]++
```

Valid:

```text
[1,2]
```

### i = 2

```text
x = 3
prefix = 6
need = 6 - 3 = 3
freq[3] = 1
answer = 2
freq[6]++
```

Valid:

```text
[3]
```

Final:

```text
2
```

---

## 9. Step-by-Step Dry Run — Count Zero Sum

Input:

```text
nums = [1, -1, 1, -1]
target = 0
```

Prefix sequence:

```text
0 initially
1
0
1
0
```

Every repeated prefix creates zero-sum subarrays.

Table:

| i | nums[i] | prefix | need | freq[need] before | answer added |
|---|---:|---:|---:|---:|---:|
| 0 | 1  | 1 | 1 | 0 | 0 |
| 1 | -1 | 0 | 0 | 1 | 1 |
| 2 | 1  | 1 | 1 | 1 | 1 |
| 3 | -1 | 0 | 0 | 2 | 2 |

Total:

```text
4
```

---

## 10. Step-by-Step Dry Run — Binary Target

Input:

```text
nums = [1, 0, 1, 0, 1]
goal = 2
```

Prefix sums:

```text
0
1
1
2
2
3
```

At each index:

```text
need = prefix - goal
```

The algorithm counts all subarrays containing exactly two `1`s.

Answer:

```text
4
```

---

## 11. Problem Form 1 — Count Subarrays Sum K

### Problem

Given an integer array and target `K`, count subarrays with sum `K`.

Input:

```text
nums = [1, 2, 3]
K = 3
```

Output:

```text
2
```

Valid subarrays:

```text
[1,2]
[3]
```

---

### Pattern Recognition

Use this when:

```text
continuous subarray
sum equals target
count all
negative numbers possible
```

Pattern:

```text
Prefix Sum + Frequency HashMap
```

---

### Step-by-Step Working

```text
freq[0] = 1
prefix = 0
answer = 0
```

Process:

```text
1 -> prefix=1, need=-2, no match
2 -> prefix=3, need=0, one match => answer=1
3 -> prefix=6, need=3, one match => answer=2
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countSubarraysSumK(vector<int>& nums, int k) {
    unordered_map<long long, long long> freq;

    // Empty prefix handles subarrays starting at index 0.
    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int x : nums) {
        prefix += x;

        // Need old prefix = current prefix - k.
        long long need = prefix - k;

        answer += freq[need];

        // Current prefix can be used by future subarrays.
        freq[prefix]++;
    }

    return answer;
}

int main() {
    vector<int> nums = {1, 2, 3};

    cout << countSubarraysSumK(nums, 3) << "\n";

    return 0;
}
```

---

### Complexity

```text
Time  : O(N)
Space : O(N)
```

---

## 12. Problem Form 2 — Count Zero Sum Subarrays

### Problem

Count subarrays whose sum is zero.

Input:

```text
nums = [3, 4, -7, 1, 3, -4]
```

---

### Pattern Recognition

Zero-sum means:

```text
prefix repeats
```

If:

```text
prefix[i] == prefix[j]
```

then:

```text
subarray i+1..j has sum 0
```

---

### Step-by-Step Simulation

Prefix sequence:

```text
initial 0
3
7
0
1
4
0
```

Prefix `0` appears multiple times.

Each time a prefix appears again, it forms zero-sum subarrays with all previous occurrences of same prefix.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countZeroSumSubarrays(vector<int>& nums) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int x : nums) {
        prefix += x;

        // Any previous same prefix creates zero-sum subarray.
        answer += freq[prefix];

        freq[prefix]++;
    }

    return answer;
}

int main() {
    vector<int> nums = {3, 4, -7, 1, 3, -4};

    cout << countZeroSumSubarrays(nums) << "\n";

    return 0;
}
```

---

### Complexity

```text
O(N)
```

---

## 13. Problem Form 3 — Count Binary Subarrays With Sum Goal

### Problem

Given a binary array, count subarrays with sum equal to `goal`.

Input:

```text
nums = [1, 0, 1, 0, 1]
goal = 2
```

Output:

```text
4
```

---

### Pattern Recognition

Binary array still supports prefix sum.

Use:

```text
Prefix Sum + Frequency HashMap
```

Alternative possible method:

```text
atMost(goal) - atMost(goal-1)
```

But prefix hashmap is direct.

---

### Step-by-Step Simulation

Prefix sequence:

```text
0
1
1
2
2
3
```

Need:

```text
prefix - goal
```

At each index, previous matching prefix count gives number of valid subarrays ending here.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int numSubarraysWithSum(vector<int>& nums, int goal) {
    unordered_map<int, int> freq;

    freq[0] = 1;

    int prefix = 0;
    int answer = 0;

    for (int x : nums) {
        prefix += x;

        answer += freq[prefix - goal];

        freq[prefix]++;
    }

    return answer;
}

int main() {
    vector<int> nums = {1, 0, 1, 0, 1};

    cout << numSubarraysWithSum(nums, 2) << "\n";

    return 0;
}
```

---

### Complexity

```text
O(N)
```

---

## 14. Problem Form 4 — Count Balanced 0/1 Subarrays

### Problem

Count subarrays with equal number of `0`s and `1`s.

Input:

```text
nums = [0, 1, 0, 1]
```

---

### Pattern Recognition

Transform:

```text
0 -> -1
1 -> +1
```

Then equal number of `0` and `1` becomes:

```text
sum = 0
```

So problem becomes:

```text
Count zero-sum subarrays
```

---

### Step-by-Step Simulation

Original:

```text
[0, 1, 0, 1]
```

Transform:

```text
[-1, +1, -1, +1]
```

Prefix:

```text
0
-1
0
-1
0
```

Repeated prefixes create balanced subarrays.

Count:

```text
4
```

Valid:

```text
[0,1]
[1,0]
[0,1]
[0,1,0,1]
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countBalanced01(vector<int>& nums) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int balance = 0;
    long long answer = 0;

    for (int x : nums) {
        if (x == 0) {
            balance -= 1;
        } else {
            balance += 1;
        }

        answer += freq[balance];

        freq[balance]++;
    }

    return answer;
}

int main() {
    vector<int> nums = {0, 1, 0, 1};

    cout << countBalanced01(nums) << "\n";

    return 0;
}
```

---

### Complexity

```text
O(N)
```

---

## 15. Problem Form 5 — Count Transaction Windows With Target

### Problem

Given transaction deltas, count windows whose net movement equals target.

Input:

```text
transactions = [100, -20, -30, 50, -100]
target = 50
```

---

### Pattern Recognition

This is identical to:

```text
count subarrays with sum K
```

Transactions are just an array of deltas.

---

### Step-by-Step Simulation

Valid windows include:

```text
[100, -20, -30] = 50
[50] = 50
```

The prefix hashmap counts them automatically.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countTransactionWindows(vector<int>& tx, int target) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int delta : tx) {
        prefix += delta;

        answer += freq[prefix - target];

        freq[prefix]++;
    }

    return answer;
}

int main() {
    vector<int> tx = {100, -20, -30, 50, -100};

    cout << countTransactionWindows(tx, 50) << "\n";

    return 0;
}
```

---

## 16. Real World Model 1 — Financial Net Movement Windows

### Scenario

A fintech system stores transaction deltas:

```text
deposit    = positive
withdrawal = negative
refund     = positive
charge     = negative
```

Question:

```text
How many continuous transaction windows have net movement exactly K?
```

---

### Problem Simulation

Transactions:

```text
[100, -40, 20, -80, 50]
K = 60
```

Window:

```text
100 - 40 = 60
```

So there is at least one matching window.

The prefix-hash algorithm counts all such windows.

---

### System Mapping

Used for:

```text
fraud detection
ledger analysis
cash-flow windows
net spending target detection
transaction anomaly discovery
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countNetMovementWindows(vector<int>& transactions, int target) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long windows = 0;

    for (int delta : transactions) {
        prefix += delta;

        windows += freq[prefix - target];

        freq[prefix]++;
    }

    return windows;
}
```

---

## 17. Real World Model 2 — Kafka Event Delta Target Windows

### Scenario

Kafka topic stores event deltas:

```text
+messages produced
-messages consumed
+active users joined
-active users left
```

Question:

```text
How many continuous event segments have net delta K?
```

---

### Problem Simulation

Events:

```text
[5, -2, 3, -1, 2]
K = 6
```

Valid segment:

```text
5 - 2 + 3 = 6
```

The algorithm counts all such event windows.

---

### System Mapping

Useful for:

```text
consumer lag analysis
event replay
stream stability analysis
incident root cause windows
state-delta auditing
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
    long long answer = 0;

    for (int delta : events) {
        prefix += delta;

        answer += freq[prefix - targetDelta];

        freq[prefix]++;
    }

    return answer;
}
```

---

## 18. Real World Model 3 — Monitoring Threshold Windows

### Scenario

Monitoring system stores metric deltas per minute:

```text
error delta
CPU pressure delta
request load delta
memory pressure delta
```

Question:

```text
How many continuous time windows accumulate exactly threshold K?
```

---

### Problem Simulation

Metric deltas:

```text
[2, 3, -1, 4, -2]
K = 4
```

Valid windows include:

```text
[4]
[3, -1, 4, -2]
```

---

### System Mapping

Used for:

```text
SLO burn-rate analysis
alert investigation
error burst detection
capacity planning
monitoring anomaly windows
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countMetricThresholdWindows(vector<int>& deltas, int threshold) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int delta : deltas) {
        prefix += delta;

        answer += freq[prefix - threshold];

        freq[prefix]++;
    }

    return answer;
}
```

---

## 19. Real World Model 4 — Product Activity Score Windows

### Scenario

A product analytics system assigns activity deltas:

```text
login    = +1
purchase = +5
refund   = -3
logout   = -1
```

Question:

```text
How many continuous user activity windows have score K?
```

---

### Problem Simulation

Activity stream:

```text
[1, 5, -3, -1, 2]
K = 3
```

The prefix-hash algorithm counts all windows whose score is exactly `3`.

---

### System Mapping

Useful for:

```text
engagement scoring
conversion journey analysis
risk scoring intervals
behavior analytics
session quality analysis
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
    long long answer = 0;

    for (int x : activity) {
        prefix += x;

        answer += freq[prefix - score];

        freq[prefix]++;
    }

    return answer;
}
```

---

## 20. Decision Tree

```text
Subarray counting problem?
|
+-- Target is sum K?
|   |
|   +-- Prefix Sum + Frequency HashMap
|
+-- Target is zero?
|   |
|   +-- Count repeated prefix sums
|
+-- Binary array exact goal?
|   |
|   +-- Prefix frequency
|
+-- Equal 0/1 count?
|   |
|   +-- Transform 0 -> -1, count zero-sum
|
+-- Need longest instead of count?
    |
    +-- Use firstIndex map, not frequency
```

---

## 21. Common Mistakes

### Mistake 1 — Forgetting `freq[0] = 1`

This misses subarrays starting at index `0`.

---

### Mistake 2 — Updating Frequency Before Counting

Correct order:

```cpp
answer += freq[prefix - target];
freq[prefix]++;
```

Wrong order may overcount when target is `0`.

---

### Mistake 3 — Using Sliding Window With Negatives

Sliding window is unsafe when values can be negative.

---

### Mistake 4 — Using Set Instead Of Frequency Map

For count, multiple same prefixes matter.

Use frequency.

---

### Mistake 5 — Using `int` Prefix

Use:

```cpp
long long
```

for large sums.

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

---

## 23. Reusable C++ Templates

### Template 1 — Generic Count Target

```cpp
long long countTarget(vector<int>& nums, int target) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int x : nums) {
        prefix += x;

        ans += freq[prefix - target];

        freq[prefix]++;
    }

    return ans;
}
```

---

### Template 2 — Count Zero Sum

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

### Template 3 — Count Balanced Binary

```cpp
long long countBalancedBinary(vector<int>& nums) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int balance = 0;
    long long ans = 0;

    for (int x : nums) {
        balance += (x == 0 ? -1 : 1);

        ans += freq[balance];

        freq[balance]++;
    }

    return ans;
}
```

---

## 24. CP / FAANG Problem Forms

---

### Problem 1 — Subarray Sum Equals K

#### Recognition

```text
count continuous subarrays whose sum equals K
```

#### Pattern

```text
Prefix Sum + Frequency HashMap
```

#### Step-by-Step Working

Input:

```text
nums = [1, 2, 3]
K = 3
```

Valid:

```text
[1,2]
[3]
```

Algorithm:

```text
freq[0]=1
prefix=0
answer=0
for each x:
    prefix += x
    answer += freq[prefix-K]
    freq[prefix]++
```

#### Commented C++ Code

```cpp
long long subarraySumEqualsK(vector<int>& nums, int k) {
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

### Problem 2 — Count Zero Sum Subarrays

#### Recognition

```text
count subarrays with sum 0
```

#### Pattern

```text
same prefix repeated
```

#### Step-by-Step Working

```text
If prefix appears before, subarray between previous prefix and current has sum 0.
```

#### Commented C++ Code

```cpp
long long zeroSumCount(vector<int>& nums) {
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

### Problem 3 — Binary Subarrays With Sum

#### Recognition

```text
binary array
count subarrays with exactly goal ones
```

#### Pattern

```text
prefix count on binary sum
```

#### Commented C++ Code

```cpp
int binarySubarraySum(vector<int>& nums, int goal) {
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
```

---

### Problem 4 — Balanced 0/1 Count

#### Recognition

```text
equal number of zeros and ones
```

#### Pattern

```text
0 -> -1
1 -> +1
count zero-sum
```

#### Commented C++ Code

```cpp
long long countBalanced01Subarrays(vector<int>& nums) {
    unordered_map<int, long long> freq;
    freq[0] = 1;

    int balance = 0;
    long long ans = 0;

    for (int x : nums) {
        balance += (x == 0 ? -1 : 1);

        ans += freq[balance];

        freq[balance]++;
    }

    return ans;
}
```

---

### Problem 5 — Event Delta Target Count

#### Recognition

```text
event stream deltas
count windows with target net delta
```

#### Pattern

```text
prefix sum over deltas
```

#### Commented C++ Code

```cpp
long long countEventWindows(vector<int>& deltas, int target) {
    unordered_map<long long, long long> freq;
    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int d : deltas) {
        prefix += d;

        ans += freq[prefix - target];

        freq[prefix]++;
    }

    return ans;
}
```

---

## 25. Practice Checklist

Before solving:

```text
1. Is it asking count of subarrays?
2. Is target based on sum/delta/balance?
3. Can I express subarray value as prefix difference?
4. Do I need frequency hashmap?
5. Did I initialize freq[0]=1?
6. Did I count before inserting current prefix?
7. Are negative values possible?
8. Should prefix be long long?
9. Is it count or longest?
10. If balanced binary, did I transform 0 -> -1?
```

---

## 26. Next Step

```text
014_Subarray_Divisible_By_K.md
```

Next file covers:

```text
prefix modulo
remainder frequency
count subarrays divisible by K
negative modulo normalization
