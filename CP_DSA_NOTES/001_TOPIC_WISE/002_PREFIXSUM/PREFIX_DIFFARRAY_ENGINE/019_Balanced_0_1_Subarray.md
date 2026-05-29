# 019_Balanced_0_1_Subarray.md — MiniPrefixSumDifferenceEngine

# Balanced 0/1 Subarray

> Balanced 0/1 subarray means:
>
> ```text
> number of 0s == number of 1s
> ```
>
> Core transformation:
>
> ```text
> 0 -> -1
> 1 -> +1
> ```
>
> Then balanced subarray becomes:
>
> ```text
> subarray sum = 0
> ```

---

## Clickable Index

1. [What Is Balanced 0/1 Subarray?](#1-what-is-balanced-01-subarray)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Transformation](#3-core-transformation)
4. [Why 0 Becomes -1](#4-why-0-becomes--1)
5. [Prefix Balance Model](#5-prefix-balance-model)
6. [Same Balance Rule](#6-same-balance-rule)
7. [Count vs Longest Balanced Subarray](#7-count-vs-longest-balanced-subarray)
8. [Step-by-Step Dry Run — Longest Balanced](#8-step-by-step-dry-run--longest-balanced)
9. [Step-by-Step Dry Run — Count Balanced](#9-step-by-step-dry-run--count-balanced)
10. [Sliding Window Warning](#10-sliding-window-warning)
11. [Problem Form 1 — Longest Equal 0 and 1](#11-problem-form-1--longest-equal-0-and-1)
12. [Problem Form 2 — Count Equal 0 and 1 Subarrays](#12-problem-form-2--count-equal-0-and-1-subarrays)
13. [Problem Form 3 — Balanced Prefix Score](#13-problem-form-3--balanced-prefix-score)
14. [Problem Form 4 — Longest Balanced User Action Window](#14-problem-form-4--longest-balanced-user-action-window)
15. [Problem Form 5 — Count Balanced Event Windows](#15-problem-form-5--count-balanced-event-windows)
16. [Real World Model 1 — Login/Logout Balance](#16-real-world-model-1--loginlogout-balance)
17. [Real World Model 2 — Success/Failure Stability Window](#17-real-world-model-2--successfailure-stability-window)
18. [Real World Model 3 — Buy/Sell Signal Balance](#18-real-world-model-3--buysell-signal-balance)
19. [Real World Model 4 — Positive/Negative Feedback Balance](#19-real-world-model-4--positivenegative-feedback-balance)
20. [Decision Tree](#20-decision-tree)
21. [Common Mistakes](#21-common-mistakes)
22. [Complexity](#22-complexity)
23. [Reusable C++ Templates](#23-reusable-c-templates)
24. [CP / FAANG Problem Forms](#24-cp--faang-problem-forms)
25. [Practice Checklist](#25-practice-checklist)
26. [Next Step](#26-next-step)

---

## 1. What Is Balanced 0/1 Subarray?

Given a binary array:

```text
nums = [0, 1, 0, 1, 1, 0]
```

A balanced subarray has:

```text
count(0) == count(1)
```

Example:

```text
[0, 1]
[1, 0]
[0, 1, 1, 0]
```

All are balanced.

---

## 2. Why This Topic Matters

This is a classic FAANG prefix-sum transformation problem.

It appears as:

```text
Contiguous Array
Longest equal 0 and 1
Count equal 0 and 1 subarrays
Balanced binary subarray
Equal success/failure windows
Equal login/logout windows
```

It teaches a core transformation trick:

```text
turn balance/equality problem into zero-sum problem
```

---

## 3. Core Transformation

Convert:

```text
0 -> -1
1 -> +1
```

Then:

```text
equal number of 0 and 1
```

means:

```text
sum = 0
```

Example:

```text
[0, 1, 1, 0]
```

Transform:

```text
[-1, +1, +1, -1]
```

Sum:

```text
0
```

So it is balanced.

---

## 4. Why 0 Becomes -1

If we kept:

```text
0 -> 0
1 -> 1
```

then sum only counts ones.

It cannot compare zero count and one count.

By converting:

```text
0 -> -1
1 -> +1
```

we get:

```text
sum = count(1) - count(0)
```

Balanced means:

```text
count(1) - count(0) = 0
```

So:

```text
sum = 0
```

---

## 5. Prefix Balance Model

Define balance:

```text
balance = number_of_ones - number_of_zeros
```

When reading each element:

```cpp
if nums[i] == 1:
    balance += 1
else:
    balance -= 1
```

If same balance appears twice:

```text
subarray between them has net balance 0
```

So:

```text
equal zeros and ones
```

---

## 6. Same Balance Rule

If:

```text
prefixBalance[i] == prefixBalance[j]
```

then:

```text
balance change from i+1 to j is 0
```

That means:

```text
same number of 0 and 1 in that subarray
```

This is exactly the same as:

```text
zero-sum subarray after transformation
```

---

## 7. Count vs Longest Balanced Subarray

| Goal | Store |
|---|---|
| Longest balanced subarray | first index of balance |
| Count balanced subarrays | frequency of balance |
| Existence | set of balances |

For longest:

```text
keep earliest index
```

For count:

```text
keep frequency
```

---

## 8. Step-by-Step Dry Run — Longest Balanced

Input:

```text
nums = [0, 1, 0, 1, 1, 0, 0]
```

Initialize:

```text
firstIndex[0] = -1
balance = 0
best = 0
```

Table:

| i | nums[i] | value | balance | seen before? | best |
|---|---:|---:|---:|---|---:|
| 0 | 0 | -1 | -1 | no, store 0 | 0 |
| 1 | 1 | +1 | 0 | yes at -1 | 2 |
| 2 | 0 | -1 | -1 | yes at 0 | 2 |
| 3 | 1 | +1 | 0 | yes at -1 | 4 |
| 4 | 1 | +1 | 1 | no, store 4 | 4 |
| 5 | 0 | -1 | 0 | yes at -1 | 6 |
| 6 | 0 | -1 | -1 | yes at 0 | 6 |

Answer:

```text
6
```

Subarray:

```text
[0, 1, 0, 1, 1, 0]
```

has:

```text
3 zeros and 3 ones
```

---

## 9. Step-by-Step Dry Run — Count Balanced

Input:

```text
nums = [0, 1, 0, 1]
```

Transform:

```text
[-1, +1, -1, +1]
```

Initialize:

```text
freq[0] = 1
balance = 0
answer = 0
```

Table:

| i | nums[i] | balance | freq before | answer added |
|---|---:|---:|---:|---:|
| 0 | 0 | -1 | 0 | 0 |
| 1 | 1 | 0 | 1 | 1 |
| 2 | 0 | -1 | 1 | 1 |
| 3 | 1 | 0 | 2 | 2 |

Total:

```text
4
```

Balanced subarrays:

```text
[0,1]
[1,0]
[0,1]
[0,1,0,1]
```

---

## 10. Sliding Window Warning

Balanced 0/1 problems are not generally solved by normal sliding window.

Why?

Because after transformation:

```text
0 -> -1
1 -> +1
```

values include negative numbers.

So sum is not monotonic.

Use:

```text
Prefix Balance + HashMap
```

---

## 11. Problem Form 1 — Longest Equal 0 and 1

### Problem

Given a binary array, find the longest subarray with equal number of `0`s and `1`s.

Input:

```text
nums = [0, 1, 0, 1, 1, 0, 0]
```

Output:

```text
6
```

---

### Pattern Recognition

Use when:

```text
binary array
equal zeros and ones
longest length
```

Pattern:

```text
0 -> -1
1 -> +1
Prefix balance + first index
```

---

### Step-by-Step Working

```text
1. balance = 0
2. firstIndex[0] = -1
3. For each number:
   0 => balance--
   1 => balance++
4. If balance seen before:
   length = i - firstIndex[balance]
5. Else store firstIndex[balance] = i
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int findMaxLength(vector<int>& nums) {
    unordered_map<int, int> firstIndex;

    // Balance 0 exists before array starts.
    firstIndex[0] = -1;

    int balance = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {
        if (nums[i] == 1) {
            balance += 1;
        } else {
            balance -= 1;
        }

        if (firstIndex.count(balance)) {
            best = max(best, i - firstIndex[balance]);
        } else {
            firstIndex[balance] = i;
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

### Complexity

```text
Time  : O(N)
Space : O(N)
```

---

## 12. Problem Form 2 — Count Equal 0 and 1 Subarrays

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

Use when:

```text
binary array
count all balanced subarrays
```

Pattern:

```text
0 -> -1
1 -> +1
Prefix balance + frequency
```

---

### Step-by-Step Working

```text
freq[0] = 1
balance = 0
answer = 0

For each x:
    if x == 0: balance--
    else: balance++

    answer += freq[balance]
    freq[balance]++
```

Same balance means the subarray between two occurrences is balanced.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countBalancedSubarrays(vector<int>& nums) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int balance = 0;
    long long answer = 0;

    for (int x : nums) {
        if (x == 1) {
            balance += 1;
        } else {
            balance -= 1;
        }

        answer += freq[balance];

        freq[balance]++;
    }

    return answer;
}

int main() {
    vector<int> nums = {0, 1, 0, 1};

    cout << countBalancedSubarrays(nums) << "\n";

    return 0;
}
```

---

## 13. Problem Form 3 — Balanced Prefix Score

### Problem

Given a stream of binary values, compute prefix balance score at every index.

Balance score:

```text
ones - zeros
```

---

### Pattern Recognition

Use when:

```text
need running balance
need detect repeated balance later
need visualize binary stream trend
```

---

### Problem Simulation

Input:

```text
nums = [0, 1, 1, 0, 1]
```

Balance:

```text
-1, 0, 1, 0, 1
```

Repeated balance `0` means subarray between those positions is balanced.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> buildBalancePrefix(vector<int>& nums) {
    vector<int> balancePrefix;

    int balance = 0;

    for (int x : nums) {
        if (x == 1) {
            balance += 1;
        } else {
            balance -= 1;
        }

        balancePrefix.push_back(balance);
    }

    return balancePrefix;
}

int main() {
    vector<int> nums = {0, 1, 1, 0, 1};

    vector<int> pref = buildBalancePrefix(nums);

    for (int x : pref) {
        cout << x << " ";
    }

    return 0;
}
```

---

## 14. Problem Form 4 — Longest Balanced User Action Window

### Problem

A product system records:

```text
0 = negative action
1 = positive action
```

Find longest window where positives and negatives are balanced.

---

### Pattern Recognition

This is the same as:

```text
longest equal 0 and 1
```

Transform:

```text
negative -> -1
positive -> +1
```

---

### Problem Simulation

Actions:

```text
[1, 0, 1, 0, 0, 1]
```

Whole array has:

```text
3 positives
3 negatives
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

int longestBalancedActions(vector<int>& actions) {
    unordered_map<int, int> firstIndex;

    firstIndex[0] = -1;

    int balance = 0;
    int best = 0;

    for (int i = 0; i < actions.size(); i++) {
        balance += (actions[i] == 1 ? 1 : -1);

        if (firstIndex.count(balance)) {
            best = max(best, i - firstIndex[balance]);
        } else {
            firstIndex[balance] = i;
        }
    }

    return best;
}

int main() {
    vector<int> actions = {1, 0, 1, 0, 0, 1};

    cout << longestBalancedActions(actions) << "\n";

    return 0;
}
```

---

## 15. Problem Form 5 — Count Balanced Event Windows

### Problem

A system records two event types:

```text
0 = event A
1 = event B
```

Count windows where event A and event B occur equally often.

---

### Pattern Recognition

Use:

```text
0 -> -1
1 -> +1
count zero-sum subarrays
```

---

### Problem Simulation

Events:

```text
[0, 1, 1, 0]
```

Balanced windows:

```text
[0,1]
[1,0]
[0,1,1,0]
```

Answer:

```text
3
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countBalancedEventWindows(vector<int>& events) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int balance = 0;
    long long answer = 0;

    for (int e : events) {
        balance += (e == 1 ? 1 : -1);

        answer += freq[balance];

        freq[balance]++;
    }

    return answer;
}

int main() {
    vector<int> events = {0, 1, 1, 0};

    cout << countBalancedEventWindows(events) << "\n";

    return 0;
}
```

---

## 16. Real World Model 1 — Login/Logout Balance

### Scenario

A system records user activity:

```text
login  = 1
logout = 0
```

A balanced window means:

```text
number of logins == number of logouts
```

---

### Problem Simulation

Events:

```text
[1, 0, 1, 0, 1, 0]
```

The whole stream is balanced:

```text
3 logins
3 logouts
```

Longest balanced window:

```text
6
```

---

### System Mapping

Used in:

```text
session consistency checks
login/logout audit
activity stream validation
state transition analysis
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestLoginLogoutBalanced(vector<int>& events) {
    unordered_map<int, int> firstIndex;

    firstIndex[0] = -1;

    int balance = 0;
    int best = 0;

    for (int i = 0; i < events.size(); i++) {
        balance += (events[i] == 1 ? 1 : -1);

        if (firstIndex.count(balance)) {
            best = max(best, i - firstIndex[balance]);
        } else {
            firstIndex[balance] = i;
        }
    }

    return best;
}
```

---

## 17. Real World Model 2 — Success/Failure Stability Window

### Scenario

A monitoring system records request results:

```text
success = 1
failure = 0
```

A balanced window means:

```text
success count == failure count
```

This can identify unstable periods.

---

### Problem Simulation

Results:

```text
[1, 1, 0, 0, 1, 0]
```

Whole window:

```text
3 successes
3 failures
```

Balanced length:

```text
6
```

---

### System Mapping

Used for:

```text
SLO analysis
success/failure trend balancing
incident window detection
quality stability checks
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestSuccessFailureBalance(vector<int>& results) {
    unordered_map<int, int> firstIndex;

    firstIndex[0] = -1;

    int balance = 0;
    int best = 0;

    for (int i = 0; i < results.size(); i++) {
        balance += (results[i] == 1 ? 1 : -1);

        if (firstIndex.count(balance)) {
            best = max(best, i - firstIndex[balance]);
        } else {
            firstIndex[balance] = i;
        }
    }

    return best;
}
```

---

## 18. Real World Model 3 — Buy/Sell Signal Balance

### Scenario

A trading system records signals:

```text
buy  = 1
sell = 0
```

Balanced window means:

```text
buy signals == sell signals
```

---

### Problem Simulation

Signals:

```text
[1, 0, 0, 1, 1, 0]
```

The whole signal sequence is balanced:

```text
3 buys
3 sells
```

---

### System Mapping

Used for:

```text
trading signal analysis
market action balance
strategy behavior testing
buy/sell pressure windows
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countBalancedBuySellWindows(vector<int>& signals) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int balance = 0;
    long long answer = 0;

    for (int s : signals) {
        balance += (s == 1 ? 1 : -1);

        answer += freq[balance];

        freq[balance]++;
    }

    return answer;
}
```

---

## 19. Real World Model 4 — Positive/Negative Feedback Balance

### Scenario

A product system records feedback:

```text
positive = 1
negative = 0
```

Balanced window means:

```text
positive feedback count == negative feedback count
```

---

### Problem Simulation

Feedback:

```text
[1, 0, 1, 1, 0, 0]
```

The whole window has:

```text
3 positive
3 negative
```

---

### System Mapping

Used for:

```text
customer sentiment windows
product feedback analysis
review stream analysis
quality signal balancing
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestFeedbackBalance(vector<int>& feedback) {
    unordered_map<int, int> firstIndex;

    firstIndex[0] = -1;

    int balance = 0;
    int best = 0;

    for (int i = 0; i < feedback.size(); i++) {
        balance += (feedback[i] == 1 ? 1 : -1);

        if (firstIndex.count(balance)) {
            best = max(best, i - firstIndex[balance]);
        } else {
            firstIndex[balance] = i;
        }
    }

    return best;
}
```

---

## 20. Decision Tree

```text
Binary subarray problem?
|
+-- Need equal 0 and 1?
|   |
|   +-- Transform 0 -> -1, 1 -> +1
|
+-- Need longest?
|   |
|   +-- firstIndex[balance]
|
+-- Need count?
|   |
|   +-- freq[balance]
|
+-- Need only running score?
|   |
|   +-- prefix balance
|
+-- More than two categories?
    |
    +-- Need vector balance / hashmap state
```

---

## 21. Common Mistakes

### Mistake 1 — Not Transforming 0 To -1

If you keep:

```text
0 as 0
```

you only count ones.

You must use:

```text
0 -> -1
```

---

### Mistake 2 — Forgetting Initial Balance

For longest:

```cpp
firstIndex[0] = -1;
```

For count:

```cpp
freq[0] = 1;
```

---

### Mistake 3 — Overwriting Earliest Index

For longest, never overwrite first occurrence.

---

### Mistake 4 — Using Sliding Window

This is a zero-sum style problem after transformation.

Sliding window is usually wrong.

---

### Mistake 5 — Confusing Count And Longest

Count:

```text
frequency
```

Longest:

```text
first index
```

---

## 22. Complexity

For longest and count versions:

```text
Time  : O(N)
Space : O(N)
```

---

## 23. Reusable C++ Templates

### Template 1 — Longest Balanced 0/1

```cpp
int longestBalanced01(vector<int>& nums) {
    unordered_map<int, int> firstIndex;

    firstIndex[0] = -1;

    int balance = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {
        balance += (nums[i] == 1 ? 1 : -1);

        if (firstIndex.count(balance)) {
            best = max(best, i - firstIndex[balance]);
        } else {
            firstIndex[balance] = i;
        }
    }

    return best;
}
```

---

### Template 2 — Count Balanced 0/1

```cpp
long long countBalanced01(vector<int>& nums) {
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

### Template 3 — Balance Prefix Array

```cpp
vector<int> balancePrefix(vector<int>& nums) {
    vector<int> pref;

    int balance = 0;

    for (int x : nums) {
        balance += (x == 1 ? 1 : -1);
        pref.push_back(balance);
    }

    return pref;
}
```

---

## 24. CP / FAANG Problem Forms

---

### Problem 1 — Contiguous Array

#### Recognition

```text
binary array
longest subarray with equal 0 and 1
```

#### Pattern

```text
0 -> -1
1 -> +1
prefix balance + first index
```

#### Step-by-Step Working

```text
balance += nums[i] == 1 ? 1 : -1
if balance seen:
    best = max(best, i - firstIndex[balance])
else:
    firstIndex[balance] = i
```

#### Commented C++ Code

```cpp
int findMaxLength(vector<int>& nums) {
    unordered_map<int, int> firstIndex;
    firstIndex[0] = -1;

    int balance = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {
        balance += (nums[i] == 1 ? 1 : -1);

        if (firstIndex.count(balance)) {
            best = max(best, i - firstIndex[balance]);
        } else {
            firstIndex[balance] = i;
        }
    }

    return best;
}
```

---

### Problem 2 — Count Balanced Binary Subarrays

#### Recognition

```text
count all subarrays with equal 0 and 1
```

#### Pattern

```text
prefix balance + frequency
```

#### Commented C++ Code

```cpp
long long countBalanced(vector<int>& nums) {
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

### Problem 3 — Equal Positive/Negative Events

#### Recognition

```text
two event types must appear equally
```

#### Pattern

```text
map one event to -1, other to +1
```

#### Commented C++ Code

```cpp
int longestEqualEvents(vector<int>& events) {
    unordered_map<int, int> first;
    first[0] = -1;

    int balance = 0;
    int best = 0;

    for (int i = 0; i < events.size(); i++) {
        balance += (events[i] == 1 ? 1 : -1);

        if (first.count(balance)) {
            best = max(best, i - first[balance]);
        } else {
            first[balance] = i;
        }
    }

    return best;
}
```

---

### Problem 4 — Running Balance Prefix

#### Recognition

```text
need score trend of binary stream
```

#### Pattern

```text
prefix balance
```

#### Commented C++ Code

```cpp
vector<int> buildBalance(vector<int>& nums) {
    vector<int> pref;

    int balance = 0;

    for (int x : nums) {
        balance += (x == 1 ? 1 : -1);
        pref.push_back(balance);
    }

    return pref;
}
```

---

### Problem 5 — Count Equal Login/Logout Windows

#### Recognition

```text
login/logout or start/stop balanced windows
```

#### Pattern

```text
count zero-balance subarrays
```

#### Commented C++ Code

```cpp
long long countEqualLoginLogout(vector<int>& events) {
    unordered_map<int, long long> freq;
    freq[0] = 1;

    int balance = 0;
    long long ans = 0;

    for (int e : events) {
        balance += (e == 1 ? 1 : -1);

        ans += freq[balance];

        freq[balance]++;
    }

    return ans;
}
```

---

## 25. Practice Checklist

Before solving:

```text
1. Is the input binary?
2. Need equal 0 and 1?
3. Did I transform 0 -> -1?
4. Need longest or count?
5. Longest -> firstIndex[balance]
6. Count -> freq[balance]
7. Did I initialize balance 0?
8. Did I avoid overwriting earliest index?
9. Did I avoid sliding window?
10. Did I test all 0s / all 1s / entire array balanced?
```

---

## 26. Next Step

```text
020_Prefix_Frequency_Count.md
```

Next file covers:

```text
prefix frequency maps
count previous states
frequency-based prefix patterns
hashmap counting framework
