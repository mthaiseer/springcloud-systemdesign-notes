# 012_Longest_Subarray_With_Sum_K.md — MiniPrefixSumDifferenceEngine

# Longest Subarray With Sum K

> This is the **length version** of the Prefix Sum + HashMap pattern.
>
> Previous file:
>
> ```text
> 011_Subarray_Sum_Equal_K.md
> ```
>
> counted how many subarrays have sum `K`.
>
> This file finds:
>
> ```text
> longest subarray length whose sum is K
> ```

---

## Clickable Index

1. [What Is Longest Subarray With Sum K?](#1-what-is-longest-subarray-with-sum-k)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Problem Form](#3-core-problem-form)
4. [Brute Force Approach](#4-brute-force-approach)
5. [Prefix Sum Algebra](#5-prefix-sum-algebra)
6. [HashMap First Occurrence Idea](#6-hashmap-first-occurrence-idea)
7. [Why Store Earliest Index Only?](#7-why-store-earliest-index-only)
8. [Why `firstIndex[0] = -1` Is Needed](#8-why-firstindex0---1-is-needed)
9. [Step-by-Step Dry Run 1 — Positive + Negative](#9-step-by-step-dry-run-1--positive--negative)
10. [Step-by-Step Dry Run 2 — Subarray Starts At 0](#10-step-by-step-dry-run-2--subarray-starts-at-0)
11. [Sliding Window vs Prefix HashMap](#11-sliding-window-vs-prefix-hashmap)
12. [Problem Form 1 — Longest Subarray Sum K](#12-problem-form-1--longest-subarray-sum-k)
13. [Problem Form 2 — Longest Zero Sum Subarray](#13-problem-form-2--longest-zero-sum-subarray)
14. [Problem Form 3 — Longest Balanced 0/1 Subarray](#14-problem-form-3--longest-balanced-01-subarray)
15. [Problem Form 4 — Longest Net Transaction Window](#15-problem-form-4--longest-net-transaction-window)
16. [Problem Form 5 — Positive-Only Variant](#16-problem-form-5--positive-only-variant)
17. [Real World Model 1 — Longest Financial Net-Zero Window](#17-real-world-model-1--longest-financial-net-zero-window)
18. [Real World Model 2 — Kafka Event Recovery Window](#18-real-world-model-2--kafka-event-recovery-window)
19. [Real World Model 3 — Monitoring Stable Load Window](#19-real-world-model-3--monitoring-stable-load-window)
20. [Real World Model 4 — User Activity Balance Window](#20-real-world-model-4--user-activity-balance-window)
21. [Decision Tree](#21-decision-tree)
22. [Common Mistakes](#22-common-mistakes)
23. [Complexity](#23-complexity)
24. [Reusable C++ Templates](#24-reusable-c-templates)
25. [CP / FAANG Problem Forms](#25-cp--faang-problem-forms)
26. [Practice Checklist](#26-practice-checklist)
27. [Next Step](#27-next-step)

---

## 1. What Is Longest Subarray With Sum K?

Given an array:

```text
nums[]
```

Find the maximum length of a continuous subarray whose sum is:

```text
K
```

Example:

```text
nums = [1, -1, 5, -2, 3]
K = 3
```

Longest valid subarray:

```text
[1, -1, 5, -2]
```

Sum:

```text
1 - 1 + 5 - 2 = 3
```

Length:

```text
4
```

Answer:

```text
4
```

---

## 2. Why This Topic Matters

This pattern appears often because it combines:

```text
prefix sum
hashmap
first occurrence index
longest distance between prefix states
negative number handling
```

It is important for:

```text
FAANG interviews
Codeforces C/D
AtCoder
financial analytics
event stream windows
monitoring stability windows
balanced subarray problems
```

---

## 3. Core Problem Form

Problem usually says:

```text
Find longest subarray with sum K.
```

or:

```text
Find maximum length continuous segment where total equals target.
```

Important signal:

```text
array may contain negative numbers
```

When negative numbers exist:

```text
sliding window may fail
```

Use:

```text
Prefix Sum + HashMap of earliest index
```

---

## 4. Brute Force Approach

Try every subarray:

```cpp
int best = 0;

for (int i = 0; i < n; i++) {
    long long sum = 0;

    for (int j = i; j < n; j++) {
        sum += nums[j];

        if (sum == k) {
            best = max(best, j - i + 1);
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

## 5. Prefix Sum Algebra

Let:

```text
prefix[j] = nums[0] + nums[1] + ... + nums[j]
```

Subarray sum from `i` to `j`:

```text
prefix[j] - prefix[i-1]
```

We need:

```text
prefix[j] - prefix[i-1] = K
```

Rearrange:

```text
prefix[i-1] = prefix[j] - K
```

So at index `j`, if we already saw:

```text
prefix[j] - K
```

at some earlier index, then subarray after that earlier index to current index has sum `K`.

---

## 6. HashMap First Occurrence Idea

For counting subarrays, we stored:

```text
freq[prefixSum]
```

For longest subarray, we store:

```text
firstIndex[prefixSum] = earliest index where this prefix sum appeared
```

At each index:

```text
prefix += nums[i]
need = prefix - K
if need exists:
    candidateLength = i - firstIndex[need]
```

Then update answer.

---

## 7. Why Store Earliest Index Only?

For longest length, we want the farthest starting point.

If the same prefix sum appears multiple times, keeping the earliest index gives longest possible subarray.

Example:

```text
prefix sum 5 appears at index 2 and index 6
current index = 10
```

Length using earliest:

```text
10 - 2 = 8
```

Length using later:

```text
10 - 6 = 4
```

So never overwrite first occurrence.

---

## 8. Why `firstIndex[0] = -1` Is Needed

Before array starts:

```text
prefix = 0 at index -1
```

This represents empty prefix.

Example:

```text
nums = [3, 1, 2]
K = 6
```

At index 2:

```text
prefix = 6
need = 0
```

If:

```text
firstIndex[0] = -1
```

then length is:

```text
2 - (-1) = 3
```

Correct subarray:

```text
[3,1,2]
```

Without this, subarrays starting at index `0` are missed.

---

## 9. Step-by-Step Dry Run 1 — Positive + Negative

Input:

```text
nums = [1, -1, 5, -2, 3]
K = 3
```

Initialize:

```text
firstIndex = {0:-1}
prefix = 0
best = 0
```

---

### i = 0

```text
x = 1
prefix = 1
need = 1 - 3 = -2
not found
store firstIndex[1] = 0
best = 0
```

Map:

```text
{0:-1, 1:0}
```

---

### i = 1

```text
x = -1
prefix = 0
need = 0 - 3 = -3
not found
prefix 0 already exists at -1
do not overwrite
best = 0
```

Map remains:

```text
{0:-1, 1:0}
```

---

### i = 2

```text
x = 5
prefix = 5
need = 5 - 3 = 2
not found
store firstIndex[5] = 2
best = 0
```

---

### i = 3

```text
x = -2
prefix = 3
need = 3 - 3 = 0
firstIndex[0] = -1
candidate length = 3 - (-1) = 4
best = 4
store firstIndex[3] = 3
```

Subarray:

```text
index 0..3 = [1,-1,5,-2]
```

Sum:

```text
3
```

---

### i = 4

```text
x = 3
prefix = 6
need = 6 - 3 = 3
firstIndex[3] = 3
candidate length = 4 - 3 = 1
best remains 4
store firstIndex[6] = 4
```

Final answer:

```text
4
```

---

## 10. Step-by-Step Dry Run 2 — Subarray Starts At 0

Input:

```text
nums = [2, 1, 3]
K = 6
```

Initialize:

```text
firstIndex[0] = -1
prefix = 0
best = 0
```

At index 2:

```text
prefix = 6
need = 0
firstIndex[0] = -1
length = 2 - (-1) = 3
```

Answer:

```text
3
```

This proves why:

```text
firstIndex[0] = -1
```

is mandatory.

---

## 11. Sliding Window vs Prefix HashMap

### Sliding Window Works When

```text
all numbers are positive / non-negative
```

because expanding right increases sum and shrinking left decreases sum.

---

### Sliding Window Fails When

```text
negative numbers exist
```

Example:

```text
[1, -1, 5, -2, 3]
```

Adding an element can decrease the sum.

So:

```text
sum is not monotonic
```

Use:

```text
Prefix Sum + HashMap
```

---

## 12. Problem Form 1 — Longest Subarray Sum K

### Problem

Find longest subarray with sum `K`.

Input:

```text
nums = [1, -1, 5, -2, 3]
K = 3
```

Output:

```text
4
```

---

### Pattern Recognition

```text
longest subarray
sum equals target
negative numbers possible
```

Use:

```text
Prefix Sum + Earliest Prefix Index
```

---

### Step-by-Step Working

Target relation:

```text
prefix[j] - prefix[i-1] = K
```

So:

```text
prefix[i-1] = prefix[j] - K
```

At every index:

```text
need = prefix - K
```

If `need` appeared before:

```text
length = currentIndex - firstIndex[need]
```

Maximize length.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestSubarraySumK(vector<int>& nums, int k) {
    unordered_map<long long, int> firstIndex;

    // Empty prefix at index -1.
    firstIndex[0] = -1;

    long long prefix = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {
        prefix += nums[i];

        long long need = prefix - k;

        if (firstIndex.count(need)) {
            best = max(best, i - firstIndex[need]);
        }

        // Store only earliest occurrence.
        if (!firstIndex.count(prefix)) {
            firstIndex[prefix] = i;
        }
    }

    return best;
}

int main() {
    vector<int> nums = {1, -1, 5, -2, 3};
    int k = 3;

    cout << longestSubarraySumK(nums, k) << "\n";

    return 0;
}
```

---

## 13. Problem Form 2 — Longest Zero Sum Subarray

### Problem

Find longest subarray whose sum is zero.

Input:

```text
nums = [15, -2, 2, -8, 1, 7, 10, 23]
```

Output:

```text
5
```

Longest zero-sum subarray:

```text
[-2, 2, -8, 1, 7]
```

Sum:

```text
0
```

Length:

```text
5
```

---

### Pattern

```text
same prefix sum repeated
```

If same prefix appears at two indices:

```text
prefix[i] == prefix[j]
```

then subarray between them has sum:

```text
0
```

---

### Problem Simulation

Prefix sequence:

```text
0
15
13
15
7
8
15
25
48
```

Prefix `15` appears at:

```text
index 0
index 2
index 6
```

Longest distance:

```text
6 - 0 = 6
```

But due to prefix indexing with `firstIndex`, actual zero-sum subarray length is computed correctly.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestZeroSumSubarray(vector<int>& nums) {
    unordered_map<long long, int> firstIndex;

    firstIndex[0] = -1;

    long long prefix = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {
        prefix += nums[i];

        if (firstIndex.count(prefix)) {
            best = max(best, i - firstIndex[prefix]);
        } else {
            firstIndex[prefix] = i;
        }
    }

    return best;
}

int main() {
    vector<int> nums = {15, -2, 2, -8, 1, 7, 10, 23};

    cout << longestZeroSumSubarray(nums) << "\n";

    return 0;
}
```

---

## 14. Problem Form 3 — Longest Balanced 0/1 Subarray

### Problem

Given a binary array, find longest subarray with equal number of `0`s and `1`s.

Input:

```text
nums = [0, 1, 0, 1, 1, 0, 0]
```

---

### Transformation

Convert:

```text
0 -> -1
1 -> +1
```

Then equal number of 0 and 1 means:

```text
sum = 0
```

So problem becomes:

```text
longest zero-sum subarray
```

---

### Simulation

Original:

```text
[0, 1, 0, 1, 1, 0, 0]
```

Transformed:

```text
[-1, +1, -1, +1, +1, -1, -1]
```

Now find longest subarray with sum `0`.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int findMaxLength(vector<int>& nums) {
    unordered_map<int, int> firstIndex;

    firstIndex[0] = -1;

    int prefix = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {
        int val = (nums[i] == 0 ? -1 : 1);

        prefix += val;

        if (firstIndex.count(prefix)) {
            best = max(best, i - firstIndex[prefix]);
        } else {
            firstIndex[prefix] = i;
        }
    }

    return best;
}

int main() {
    vector<int> nums = {0, 1, 0, 1, 1, 0, 0};

    cout << findMaxLength(nums) << "\n";

    return 0;
}
```

---

## 15. Problem Form 4 — Longest Net Transaction Window

### Problem

Given financial transaction deltas, find longest window where net movement equals `K`.

Input:

```text
transactions = [100, -50, -50, 200, -100, -100]
K = 0
```

---

### Pattern

```text
Longest subarray sum K
```

---

### Simulation

Window:

```text
[100, -50, -50]
```

has sum:

```text
0
```

Another window:

```text
[200, -100, -100]
```

also has sum:

```text
0
```

The algorithm finds the longest.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestNetWindow(vector<int>& tx, int target) {
    unordered_map<long long, int> firstIndex;

    firstIndex[0] = -1;

    long long prefix = 0;
    int best = 0;

    for (int i = 0; i < tx.size(); i++) {
        prefix += tx[i];

        long long need = prefix - target;

        if (firstIndex.count(need)) {
            best = max(best, i - firstIndex[need]);
        }

        if (!firstIndex.count(prefix)) {
            firstIndex[prefix] = i;
        }
    }

    return best;
}

int main() {
    vector<int> tx = {100, -50, -50, 200, -100, -100};

    cout << longestNetWindow(tx, 0) << "\n";

    return 0;
}
```

---

## 16. Problem Form 5 — Positive-Only Variant

### Problem

If all numbers are positive, find longest subarray with sum `K`.

Input:

```text
nums = [1, 2, 1, 1, 1, 3]
K = 4
```

---

### Pattern

```text
Sliding Window
```

Because all values are positive.

---

### Simulation

Window grows while sum <= K.

Valid longest:

```text
[1,1,1,1]
```

Length:

```text
4
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestPositiveSubarraySumK(vector<int>& nums, int k) {
    int left = 0;
    long long sum = 0;
    int best = 0;

    for (int right = 0; right < nums.size(); right++) {
        sum += nums[right];

        while (sum > k && left <= right) {
            sum -= nums[left];
            left++;
        }

        if (sum == k) {
            best = max(best, right - left + 1);
        }
    }

    return best;
}

int main() {
    vector<int> nums = {1, 2, 1, 1, 1, 3};
    int k = 4;

    cout << longestPositiveSubarraySumK(nums, k) << "\n";

    return 0;
}
```

---

## 17. Real World Model 1 — Longest Financial Net-Zero Window

### Scenario

A banking system tracks transactions:

```text
deposit = positive
withdrawal = negative
refund = positive
charge = negative
```

Question:

```text
What is the longest continuous transaction window where net change is zero?
```

---

### Simulation

Transactions:

```text
[500, -200, -300, 100, -100, 50]
```

The first three transactions:

```text
500 - 200 - 300 = 0
```

So there is a net-zero window of length:

```text
3
```

---

### System Mapping

Useful for:

```text
ledger reconciliation
fraud pattern detection
cash-flow neutral period detection
accounting audits
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestNetZeroFinancialWindow(vector<int>& transactions) {
    unordered_map<long long, int> firstIndex;

    firstIndex[0] = -1;

    long long prefix = 0;
    int best = 0;

    for (int i = 0; i < transactions.size(); i++) {
        prefix += transactions[i];

        if (firstIndex.count(prefix)) {
            best = max(best, i - firstIndex[prefix]);
        } else {
            firstIndex[prefix] = i;
        }
    }

    return best;
}
```

---

## 18. Real World Model 2 — Kafka Event Recovery Window

### Scenario

A Kafka event stream stores deltas:

```text
+messages produced
-messages consumed
```

Question:

```text
What is the longest time window where net backlog change is K?
```

---

### Simulation

Backlog deltas:

```text
[100, -40, -60, 30, -10, -20]
K = 0
```

The first three:

```text
100 - 40 - 60 = 0
```

means backlog recovered to same level over that window.

---

### System Mapping

Useful for:

```text
consumer lag recovery analysis
incident replay
event stream stability windows
throughput balance windows
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestKafkaBacklogWindow(vector<int>& deltas, int target) {
    unordered_map<long long, int> firstIndex;

    firstIndex[0] = -1;

    long long prefix = 0;
    int best = 0;

    for (int i = 0; i < deltas.size(); i++) {
        prefix += deltas[i];

        long long need = prefix - target;

        if (firstIndex.count(need)) {
            best = max(best, i - firstIndex[need]);
        }

        if (!firstIndex.count(prefix)) {
            firstIndex[prefix] = i;
        }
    }

    return best;
}
```

---

## 19. Real World Model 3 — Monitoring Stable Load Window

### Scenario

A monitoring system records load deltas per minute.

Question:

```text
What is the longest window where net load change equals 0?
```

This identifies a stable period.

---

### Simulation

Load deltas:

```text
[5, -2, -3, 4, -1, -3]
```

First three:

```text
5 - 2 - 3 = 0
```

So load returned to baseline after 3 minutes.

---

### System Mapping

Used for:

```text
load stability detection
SLO analysis
incident recovery
autoscaling validation
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestStableLoadWindow(vector<int>& deltas) {
    unordered_map<long long, int> firstIndex;

    firstIndex[0] = -1;

    long long prefix = 0;
    int best = 0;

    for (int i = 0; i < deltas.size(); i++) {
        prefix += deltas[i];

        if (firstIndex.count(prefix)) {
            best = max(best, i - firstIndex[prefix]);
        } else {
            firstIndex[prefix] = i;
        }
    }

    return best;
}
```

---

## 20. Real World Model 4 — User Activity Balance Window

### Scenario

Product analytics assigns activity deltas:

```text
login = +1
purchase = +5
refund = -3
logout = -1
```

Question:

```text
Longest continuous user journey where activity score equals target K.
```

---

### Simulation

Activity stream:

```text
[1, 5, -3, -1, 2, -4]
K = 2
```

The algorithm detects the longest continuous segment whose total is exactly `2`.

---

### System Mapping

Useful for:

```text
engagement analysis
session scoring
behavior pattern mining
risk window detection
conversion journey analysis
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestActivityScoreWindow(vector<int>& activity, int target) {
    unordered_map<long long, int> firstIndex;

    firstIndex[0] = -1;

    long long prefix = 0;
    int best = 0;

    for (int i = 0; i < activity.size(); i++) {
        prefix += activity[i];

        long long need = prefix - target;

        if (firstIndex.count(need)) {
            best = max(best, i - firstIndex[need]);
        }

        if (!firstIndex.count(prefix)) {
            firstIndex[prefix] = i;
        }
    }

    return best;
}
```

---

## 21. Decision Tree

```text
Subarray problem?
|
+-- Need count of subarrays sum K?
|   |
|   +-- Use freq[prefix]
|
+-- Need longest length sum K?
|   |
|   +-- Use firstIndex[prefix]
|
+-- Need zero-sum longest?
|   |
|   +-- same prefix repeated
|
+-- Binary equal 0/1?
|   |
|   +-- transform 0 -> -1
|
+-- All values positive?
    |
    +-- Sliding window may work
```

---

## 22. Common Mistakes

### Mistake 1 — Overwriting Prefix Index

Wrong:

```cpp
firstIndex[prefix] = i;
```

every time.

Correct:

```cpp
if (!firstIndex.count(prefix)) {
    firstIndex[prefix] = i;
}
```

Keep earliest index.

---

### Mistake 2 — Forgetting `firstIndex[0] = -1`

Without it, subarrays starting from index 0 are missed.

---

### Mistake 3 — Using Frequency Instead Of First Index

For longest length, use:

```text
first occurrence index
```

not frequency.

---

### Mistake 4 — Sliding Window With Negative Numbers

Sliding window is unsafe with negative numbers.

---

### Mistake 5 — Not Using Long Long

Prefix sums may overflow `int`.

---

## 23. Complexity

Time:

```text
O(N)
```

Space:

```text
O(N)
```

---

## 24. Reusable C++ Templates

### Template 1 — Longest Subarray Sum K

```cpp
int longestSubarraySumK(vector<int>& nums, int k) {
    unordered_map<long long, int> firstIndex;

    firstIndex[0] = -1;

    long long prefix = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {
        prefix += nums[i];

        long long need = prefix - k;

        if (firstIndex.count(need)) {
            best = max(best, i - firstIndex[need]);
        }

        if (!firstIndex.count(prefix)) {
            firstIndex[prefix] = i;
        }
    }

    return best;
}
```

---

### Template 2 — Longest Zero Sum

```cpp
int longestZeroSum(vector<int>& nums) {
    unordered_map<long long, int> firstIndex;

    firstIndex[0] = -1;

    long long prefix = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {
        prefix += nums[i];

        if (firstIndex.count(prefix)) {
            best = max(best, i - firstIndex[prefix]);
        } else {
            firstIndex[prefix] = i;
        }
    }

    return best;
}
```

---

### Template 3 — Longest Balanced Binary Subarray

```cpp
int longestBalanced01(vector<int>& nums) {
    unordered_map<int, int> firstIndex;

    firstIndex[0] = -1;

    int prefix = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {
        prefix += (nums[i] == 0 ? -1 : 1);

        if (firstIndex.count(prefix)) {
            best = max(best, i - firstIndex[prefix]);
        } else {
            firstIndex[prefix] = i;
        }
    }

    return best;
}
```

---

## 25. CP / FAANG Problem Forms

### Problem 1 — Longest Subarray Sum K

Recognition:

```text
maximum length continuous subarray with sum K
```

Pattern:

```text
prefix + earliest index
```

---

### Problem 2 — Longest Zero Sum Subarray

Recognition:

```text
longest subarray sum 0
```

Pattern:

```text
repeated prefix sum
```

---

### Problem 3 — Contiguous Array

Recognition:

```text
equal number of 0 and 1
```

Pattern:

```text
0 -> -1, 1 -> +1
```

---

### Problem 4 — Longest Stable Metric Window

Recognition:

```text
net delta over window equals target
```

Pattern:

```text
prefix index map
```

---

### Problem 5 — Positive-Only Longest Sum K

Recognition:

```text
all positive values
```

Pattern:

```text
sliding window
```

---

## 26. Practice Checklist

Before coding:

```text
1. Need longest length or count?
2. If count -> frequency hashmap
3. If longest -> first index hashmap
4. Did I set firstIndex[0] = -1?
5. Did I avoid overwriting earliest prefix?
6. Are negatives possible?
7. Is sliding window invalid?
8. Did I compute need = prefix - K?
9. Did I use long long?
10. Did I test subarray starting at index 0?
```

---

## 27. Next Step

```text
013_Count_Subarrays_With_Target.md
```

Next file generalizes counting forms:

```text
target count
zero sum count
binary count
modulo count
prefix frequency counting
```
