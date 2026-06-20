# 020_Prefix_Frequency_Count.md — MiniPrefixSumDifferenceEngine

# Prefix Frequency Count

> Prefix Frequency Count is the general framework behind many prefix-hash problems.
>
> Instead of only storing a prefix value, we store:
>
> ```text
> how many times each prefix state appeared before
> ```
>
> This lets us count:
>
> ```text
> subarrays
> pairs of prefix states
> repeated states
> target windows
> zero-sum windows
> modulo windows
> XOR windows
> balanced windows
> ```

---

## Clickable Index

1. [What Is Prefix Frequency Count?](#1-what-is-prefix-frequency-count)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Mental Model](#3-core-mental-model)
4. [Prefix State Definition](#4-prefix-state-definition)
5. [Frequency Map Pattern](#5-frequency-map-pattern)
6. [Count Previous Compatible States](#6-count-previous-compatible-states)
7. [Generic Formula](#7-generic-formula)
8. [Frequency vs First Index vs Last Index](#8-frequency-vs-first-index-vs-last-index)
9. [Why Initial State Matters](#9-why-initial-state-matters)
10. [Step-by-Step Dry Run — Sum Target](#10-step-by-step-dry-run--sum-target)
11. [Step-by-Step Dry Run — Modulo Target](#11-step-by-step-dry-run--modulo-target)
12. [Problem Form 1 — Count Subarrays Sum K](#12-problem-form-1--count-subarrays-sum-k)
13. [Problem Form 2 — Count Zero Sum Subarrays](#13-problem-form-2--count-zero-sum-subarrays)
14. [Problem Form 3 — Count Subarrays Divisible By K](#14-problem-form-3--count-subarrays-divisible-by-k)
15. [Problem Form 4 — Count Subarrays XOR K](#15-problem-form-4--count-subarrays-xor-k)
16. [Problem Form 5 — Count Balanced 0/1 Subarrays](#16-problem-form-5--count-balanced-01-subarrays)
17. [Real World Model 1 — Transaction State Frequency](#17-real-world-model-1--transaction-state-frequency)
18. [Real World Model 2 — Event Stream State Counting](#18-real-world-model-2--event-stream-state-counting)
19. [Real World Model 3 — Monitoring State Repetition](#19-real-world-model-3--monitoring-state-repetition)
20. [Real World Model 4 — Feature Toggle State Windows](#20-real-world-model-4--feature-toggle-state-windows)
21. [Decision Tree](#21-decision-tree)
22. [Common Mistakes](#22-common-mistakes)
23. [Complexity](#23-complexity)
24. [Reusable C++ Templates](#24-reusable-c-templates)
25. [CP / FAANG Problem Forms](#25-cp--faang-problem-forms)
26. [Practice Checklist](#26-practice-checklist)
27. [Next Step](#27-next-step)

---

## 1. What Is Prefix Frequency Count?

Prefix Frequency Count means:

```text
while scanning left to right,
store how many times each prefix state occurred.
```

A prefix state can be:

```text
sum
modulo remainder
xor value
balance value
bitmask parity
```

At each index, we ask:

```text
How many previous prefix states can combine with current state
to form a valid subarray?
```

---

## 2. Why This Topic Matters

This is the common engine behind many problems:

```text
Subarray Sum Equals K
Count Zero Sum Subarrays
Subarrays Divisible By K
Subarray XOR Equals K
Balanced 0/1 Count
Wonderful Substrings
Even Vowel Substrings
```

If you master this mental model, many medium/hard prefix problems become the same pattern.

---

## 3. Core Mental Model

A subarray is a difference between two prefix states.

```text
subarray(l..r) = prefixStateBeforeL combined with prefixStateAtR
```

So counting subarrays becomes:

```text
count pairs of prefix states
```

The frequency map tells:

```text
how many valid left endpoints exist for current right endpoint
```

---

## 4. Prefix State Definition

Different problems use different prefix states:

| Problem | Prefix State |
|---|---|
| Sum equals K | prefix sum |
| Sum divisible by K | prefix sum % K |
| XOR equals K | prefix XOR |
| Equal 0 and 1 | balance = ones - zeros |
| Wonderful substring | parity bitmask |
| Even vowels | vowel parity mask |

The code skeleton is similar.

---

## 5. Frequency Map Pattern

Generic pattern:

```cpp
freq[initialState] = 1;

state = initialState;
answer = 0;

for each element:
    update state

    compatibleState = computeCompatibleState(state)

    answer += freq[compatibleState]

    freq[state]++
```

The important part is:

```text
computeCompatibleState()
```

That depends on the problem.

---

## 6. Count Previous Compatible States

For each current state, ask:

```text
Which previous state makes the subarray valid?
```

Examples:

### Sum Equals K

```text
previous = currentSum - K
```

### Divisible By K

```text
previous remainder = current remainder
```

### XOR Equals K

```text
previous = currentXor ^ K
```

### Balanced 0/1

```text
previous balance = current balance
```

### Wonderful Substring

```text
previous mask = current mask
or current mask with one bit flipped
```

---

## 7. Generic Formula

Most prefix frequency problems follow:

```text
answer += number of previous compatible states
```

Then:

```text
record current state
```

Order matters:

```text
query first
insert current state after
```

This avoids counting empty subarray incorrectly in many problems.

---

## 8. Frequency vs First Index vs Last Index

| Goal | Store |
|---|---|
| Count valid subarrays | frequency |
| Longest valid subarray | earliest index |
| Shortest removable subarray | latest index |
| Existence only | set |

This file focuses on:

```text
frequency
```

because we count number of valid subarrays/windows.

---

## 9. Why Initial State Matters

Before scanning:

```text
prefix sum = 0
prefix xor = 0
balance = 0
mask = 0
```

So we initialize:

```cpp
freq[0] = 1;
```

This counts subarrays starting at index `0`.

Without this, many answers become too small.

---

## 10. Step-by-Step Dry Run — Sum Target

Input:

```text
nums = [1, 2, 3]
K = 3
```

Initialize:

```text
freq[0] = 1
prefix = 0
answer = 0
```

Table:

| i | x | prefix | need | freq[need] before | answer |
|---|---:|---:|---:|---:|---:|
| 0 | 1 | 1 | -2 | 0 | 0 |
| 1 | 2 | 3 | 0 | 1 | 1 |
| 2 | 3 | 6 | 3 | 1 | 2 |

Answer:

```text
2
```

Valid:

```text
[1,2]
[3]
```

---

## 11. Step-by-Step Dry Run — Modulo Target

Input:

```text
nums = [4, 5, 0, -2, -3, 1]
K = 5
```

State:

```text
prefix % K
```

Initialize:

```text
freq[0] = 1
```

Same remainder means divisible subarray.

Table:

| i | x | prefix | rem | freq[rem] before | answer |
|---|---:|---:|---:|---:|---:|
| 0 | 4 | 4 | 4 | 0 | 0 |
| 1 | 5 | 9 | 4 | 1 | 1 |
| 2 | 0 | 9 | 4 | 2 | 3 |
| 3 | -2 | 7 | 2 | 0 | 3 |
| 4 | -3 | 4 | 4 | 3 | 6 |
| 5 | 1 | 5 | 0 | 1 | 7 |

Answer:

```text
7
```

---

## 12. Problem Form 1 — Count Subarrays Sum K

### Problem

Count continuous subarrays whose sum equals `K`.

Input:

```text
nums = [1, 2, 3]
K = 3
```

Output:

```text
2
```

---

### Pattern Recognition

Use when:

```text
count subarrays
sum equals target
negative values possible
```

Pattern:

```text
Prefix Sum + Frequency Map
```

---

### Step-by-Step Working

For each current prefix:

```text
need = prefix - K
answer += freq[need]
freq[prefix]++
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countSubarraysSumK(vector<int>& nums, int k) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int x : nums) {
        prefix += x;

        long long need = prefix - k;

        answer += freq[need];

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

## 13. Problem Form 2 — Count Zero Sum Subarrays

### Problem

Count subarrays whose sum is zero.

Input:

```text
nums = [1, -1, 1, -1]
```

Output:

```text
4
```

---

### Pattern Recognition

Zero-sum means:

```text
same prefix sum repeated
```

So:

```text
answer += freq[prefix]
```

---

### Step-by-Step Simulation

Prefix sequence:

```text
0, 1, 0, 1, 0
```

Repeated prefix states create zero-sum subarrays.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countZeroSum(vector<int>& nums) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int x : nums) {
        prefix += x;

        answer += freq[prefix];

        freq[prefix]++;
    }

    return answer;
}

int main() {
    vector<int> nums = {1, -1, 1, -1};

    cout << countZeroSum(nums) << "\n";

    return 0;
}
```

---

## 14. Problem Form 3 — Count Subarrays Divisible By K

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

Use when:

```text
sum % K == 0
```

Pattern:

```text
Prefix Modulo + Remainder Frequency
```

---

### Step-by-Step Working

```text
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

long long countDivisibleByK(vector<int>& nums, int k) {
    vector<long long> freq(k, 0);

    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int x : nums) {
        prefix += x;

        int rem = ((prefix % k) + k) % k;

        answer += freq[rem];

        freq[rem]++;
    }

    return answer;
}

int main() {
    vector<int> nums = {4, 5, 0, -2, -3, 1};

    cout << countDivisibleByK(nums, 5) << "\n";

    return 0;
}
```

---

## 15. Problem Form 4 — Count Subarrays XOR K

### Problem

Count subarrays whose XOR equals `K`.

Input:

```text
nums = [4, 2, 2, 6, 4]
K = 6
```

Output:

```text
4
```

---

### Pattern Recognition

Use when:

```text
subarray XOR equals target
```

Pattern:

```text
Prefix XOR + Frequency Map
```

---

### Step-by-Step Working

```text
prefix ^= x
need = prefix ^ K
answer += freq[need]
freq[prefix]++
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countXorK(vector<int>& nums, int k) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int prefix = 0;
    long long answer = 0;

    for (int x : nums) {
        prefix ^= x;

        int need = prefix ^ k;

        answer += freq[need];

        freq[prefix]++;
    }

    return answer;
}

int main() {
    vector<int> nums = {4, 2, 2, 6, 4};

    cout << countXorK(nums, 6) << "\n";

    return 0;
}
```

---

## 16. Problem Form 5 — Count Balanced 0/1 Subarrays

### Problem

Count subarrays with equal number of `0`s and `1`s.

Input:

```text
nums = [0, 1, 0, 1]
```

Output:

```text
4
```

---

### Pattern Recognition

Transform:

```text
0 -> -1
1 -> +1
```

Then count zero-sum subarrays.

---

### Step-by-Step Working

```text
balance += x == 1 ? 1 : -1
answer += freq[balance]
freq[balance]++
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
        balance += (x == 1 ? 1 : -1);

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

## 17. Real World Model 1 — Transaction State Frequency

### Scenario

A fintech system stores transaction deltas:

```text
deposit = positive
withdrawal = negative
```

Question:

```text
How many continuous transaction windows have net movement K?
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
[100, -40] = 60
```

The prefix frequency map counts all matching windows.

---

### System Mapping

Used for:

```text
fraud detection
ledger window analysis
cash-flow pattern detection
financial reconciliation
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countTransactionTargetWindows(vector<int>& tx, int target) {
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
```

---

## 18. Real World Model 2 — Event Stream State Counting

### Scenario

A Kafka-like stream stores event deltas:

```text
+messages produced
-messages consumed
+users joined
-users left
```

Question:

```text
How many continuous event windows reach target state change?
```

---

### Problem Simulation

Events:

```text
[5, -2, 3, -1, 2]
target = 6
```

Window:

```text
[5, -2, 3] = 6
```

---

### System Mapping

Used for:

```text
event stream analytics
consumer lag analysis
incident replay
state-delta auditing
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countEventStateWindows(vector<int>& events, int target) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int delta : events) {
        prefix += delta;

        answer += freq[prefix - target];

        freq[prefix]++;
    }

    return answer;
}
```

---

## 19. Real World Model 3 — Monitoring State Repetition

### Scenario

Monitoring stores metric deltas:

```text
CPU pressure delta
error count delta
request load delta
```

Repeated prefix state means:

```text
net change between two points is zero
```

---

### Problem Simulation

Deltas:

```text
[5, -2, -3, 4, -4]
```

Prefix:

```text
0, 5, 3, 0, 4, 0
```

Prefix `0` repeats.

This shows multiple stable zero-net windows.

---

### System Mapping

Used for:

```text
incident recovery detection
stable load windows
SLO burn analysis
metric anomaly investigation
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countStableMetricWindows(vector<int>& deltas) {
    unordered_map<long long, long long> freq;

    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int d : deltas) {
        prefix += d;

        answer += freq[prefix];

        freq[prefix]++;
    }

    return answer;
}
```

---

## 20. Real World Model 4 — Feature Toggle State Windows

### Scenario

Feature toggles are stored as bitmasks.

Question:

```text
How many continuous event windows create target toggle state?
```

This is prefix XOR frequency.

---

### Problem Simulation

Events:

```text
A, B, A
```

Masks:

```text
[1, 2, 1]
```

Target:

```text
2
```

Window:

```text
[A, B, A]
```

XOR:

```text
1 ^ 2 ^ 1 = 2
```

---

### System Mapping

Used for:

```text
feature-flag auditing
permission toggle analysis
configuration replay
state transition debugging
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countToggleTargetWindows(vector<int>& masks, int target) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int prefix = 0;
    long long answer = 0;

    for (int mask : masks) {
        prefix ^= mask;

        answer += freq[prefix ^ target];

        freq[prefix]++;
    }

    return answer;
}
```

---

## 21. Decision Tree

```text
Need count of subarrays/windows?
|
+-- Sum target?
|   |
|   +-- freq[prefix - K]
|
+-- Sum zero?
|   |
|   +-- freq[prefix]
|
+-- Divisible by K?
|   |
|   +-- freq[prefix % K]
|
+-- XOR target?
|   |
|   +-- freq[prefixXor ^ K]
|
+-- Balanced 0/1?
|   |
|   +-- freq[balance]
|
+-- Odd/even parity mask?
    |
    +-- freq[mask] and maybe freq[mask ^ bit]
```

---

## 22. Common Mistakes

### Mistake 1 — Forgetting Initial State

Always consider:

```cpp
freq[0] = 1;
```

or equivalent initial state.

---

### Mistake 2 — Using First Index When Counting

For count:

```text
frequency
```

For longest:

```text
first index
```

---

### Mistake 3 — Insert Before Query

Usually correct order is:

```text
query compatible previous states
then insert current state
```

---

### Mistake 4 — Wrong Compatible State Formula

Examples:

```text
sum target -> prefix - K
xor target -> prefix ^ K
divisible -> same remainder
balanced -> same balance
```

---

### Mistake 5 — Not Using Long Long

Count of subarrays can be:

```text
O(N^2)
```

Use:

```cpp
long long
```

for answer and sometimes prefix.

---

## 23. Complexity

Most prefix frequency count problems:

```text
Time  : O(N)
Space : O(N)
```

Modulo with small `K`:

```text
Space : O(K)
```

Bitmask with small alphabet `B`:

```text
Space : O(2^B)
```

---

## 24. Reusable C++ Templates

### Template 1 — Generic Sum Target Count

```cpp
long long countSumTarget(vector<int>& nums, int target) {
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

### Template 2 — Generic Same-State Count

```cpp
long long countSameState(vector<int>& states) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int state = 0;
    long long ans = 0;

    for (int x : states) {
        state += x;

        ans += freq[state];

        freq[state]++;
    }

    return ans;
}
```

---

### Template 3 — Generic XOR Target Count

```cpp
long long countXorTarget(vector<int>& nums, int target) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int prefix = 0;
    long long ans = 0;

    for (int x : nums) {
        prefix ^= x;

        ans += freq[prefix ^ target];

        freq[prefix]++;
    }

    return ans;
}
```

---

### Template 4 — Generic Modulo Count

```cpp
long long countModuloZero(vector<int>& nums, int k) {
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

## 25. CP / FAANG Problem Forms

---

### Problem 1 — Subarray Sum Equals K

#### Recognition

```text
count subarrays sum K
```

#### Pattern

```text
prefix frequency of sums
```

#### Step-by-Step Working

```text
prefix += x
need = prefix - K
answer += freq[need]
freq[prefix]++
```

#### Commented C++ Code

```cpp
long long solve(vector<int>& nums, int k) {
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

### Problem 2 — Subarrays Divisible By K

#### Recognition

```text
count subarrays divisible by K
```

#### Pattern

```text
prefix remainder frequency
```

#### Commented C++ Code

```cpp
long long solve(vector<int>& nums, int k) {
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

### Problem 3 — Subarray XOR Equals K

#### Recognition

```text
count subarrays XOR K
```

#### Pattern

```text
prefix XOR frequency
```

#### Commented C++ Code

```cpp
long long solve(vector<int>& nums, int k) {
    unordered_map<int, long long> freq;
    freq[0] = 1;

    int prefix = 0;
    long long ans = 0;

    for (int x : nums) {
        prefix ^= x;
        ans += freq[prefix ^ k];
        freq[prefix]++;
    }

    return ans;
}
```

---

### Problem 4 — Count Balanced 0/1

#### Recognition

```text
equal number of 0 and 1
```

#### Pattern

```text
balance frequency
```

#### Commented C++ Code

```cpp
long long solve(vector<int>& nums) {
    unordered_map<int, long long> freq;
    freq[0] = 1;

    int balance = 0;
    long long ans = 0;

    for (int x : nums) {
        balance += (x == 1 ? 1 : -1);

        ans += freq[balance];

        freq[balance]++;
    }

    return ans;
}
```

---

### Problem 5 — Wonderful Substrings

#### Recognition

```text
at most one odd frequency
```

#### Pattern

```text
prefix bitmask frequency
```

#### Commented C++ Code

```cpp
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
```

---

## 26. Practice Checklist

Before solving:

```text
1. Am I counting subarrays/windows?
2. Can the property be expressed using prefix states?
3. What is the prefix state?
4. What previous state is compatible?
5. Do I need frequency, first index, or latest index?
6. Did I initialize empty state?
7. Did I query before inserting current state?
8. Does answer need long long?
9. Is modulo normalization needed?
10. Is XOR or sum formula being used correctly?
```

---

## 27. Next Step

```text
021_AP_Weighted_Prefix_Sum.md
```

Next file starts weighted prefix patterns:

```text
arithmetic progression weights
weighted sums
index-weighted prefix
contribution formulas
