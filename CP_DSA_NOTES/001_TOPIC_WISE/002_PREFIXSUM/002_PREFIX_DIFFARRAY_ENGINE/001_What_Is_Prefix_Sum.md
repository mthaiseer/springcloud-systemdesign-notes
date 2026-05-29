# 001_What_Is_Prefix_Sum.md — MiniPrefixSumDifferenceEngine

# What Is Prefix Sum?

> Prefix Sum is one of the highest ROI patterns in CP, FAANG interviews, analytics systems, monitoring systems, and backend range aggregation problems.

---

## Clickable Index

1. [What Is Prefix Sum?](#1-what-is-prefix-sum)
2. [Why Prefix Sum Matters](#2-why-prefix-sum-matters)
3. [Core Mental Model](#3-core-mental-model)
4. [Normal Array vs Prefix Array](#4-normal-array-vs-prefix-array)
5. [Prefix Sum Formula](#5-prefix-sum-formula)
6. [Range Sum Formula](#6-range-sum-formula)
7. [0-Indexed Prefix vs 1-Indexed Prefix](#7-0-indexed-prefix-vs-1-indexed-prefix)
8. [Visual Intuition](#8-visual-intuition)
9. [Step-by-Step Dry Run](#9-step-by-step-dry-run)
10. [Why Query Becomes O(1)](#10-why-query-becomes-o1)
11. [Brute Force vs Prefix Sum](#11-brute-force-vs-prefix-sum)
12. [Problem Form 1 — Running Sum](#12-problem-form-1--running-sum)
13. [Problem Form 2 — Static Range Sum Query](#13-problem-form-2--static-range-sum-query)
14. [Problem Form 3 — Left Sum / Right Sum](#14-problem-form-3--left-sum--right-sum)
15. [Problem Form 4 — Fixed Window Sum](#15-problem-form-4--fixed-window-sum)
16. [Problem Form 5 — Count Range Aggregates](#16-problem-form-5--count-range-aggregates)
17. [Real World Model 1 — CPU Metrics Aggregation](#17-real-world-model-1--cpu-metrics-aggregation)
18. [Real World Model 2 — Banking Ledger Running Balance](#18-real-world-model-2--banking-ledger-running-balance)
19. [Real World Model 3 — Analytics Dashboard Query](#19-real-world-model-3--analytics-dashboard-query)
20. [Real World Model 4 — Log Count Aggregation](#20-real-world-model-4--log-count-aggregation)
21. [Decision Tree](#21-decision-tree)
22. [Common Mistakes](#22-common-mistakes)
23. [Complexity](#23-complexity)
24. [Reusable C++ Template](#24-reusable-c-template)
25. [CP / FAANG Problem Forms](#25-cp--faang-problem-forms)
26. [Practice Checklist](#26-practice-checklist)
27. [Next Step](#27-next-step)

---

## 1. What Is Prefix Sum?

Prefix sum means:

```text
prefix[i] = sum of all elements from start up to index i
```

For array:

```text
a = [4, 2, 3, 1, -5, 6]
```

Prefix sum is:

```text
pref = [4, 6, 9, 10, 5, 11]
```

Because:

```text
pref[0] = 4
pref[1] = 4 + 2 = 6
pref[2] = 4 + 2 + 3 = 9
pref[3] = 4 + 2 + 3 + 1 = 10
pref[4] = 4 + 2 + 3 + 1 - 5 = 5
pref[5] = 4 + 2 + 3 + 1 - 5 + 6 = 11
```

---

## 2. Why Prefix Sum Matters

Many problems ask:

```text
What is the sum from index L to R?
```

Without prefix sum, each query may take:

```text
O(R-L+1)
```

With prefix sum, each query takes:

```text
O(1)
```

after preprocessing.

This matters when:

```text
n = 200000
q = 200000
```

Brute force can become:

```text
O(n*q)
```

which is too slow.

Prefix sum gives:

```text
O(n+q)
```

---

## 3. Core Mental Model

Think like this:

```text
Normal array tells value at one index.
Prefix array tells total value from the start up to that index.
```

If you know:

```text
sum from 0 to r
```

and remove:

```text
sum from 0 to l-1
```

then what remains is:

```text
sum from l to r
```

This is the whole idea.

---

## 4. Normal Array vs Prefix Array

Normal array:

```text
a[i] = value at index i
```

Prefix array:

```text
pref[i] = total from index 0 to i
```

Example:

```text
index:   0   1   2   3   4   5
a    :  [4,  2,  3,  1, -5,  6]
pref :  [4,  6,  9, 10,  5, 11]
```

Meaning:

```text
pref[3] = a[0] + a[1] + a[2] + a[3]
        = 4 + 2 + 3 + 1
        = 10
```

---

## 5. Prefix Sum Formula

For 0-indexed prefix:

```text
pref[0] = a[0]
pref[i] = pref[i-1] + a[i]
```

For 1-indexed prefix:

```text
pref[0] = 0
pref[i] = pref[i-1] + a[i-1]
```

Most CP code prefers 1-indexed prefix because it avoids special cases.

---

## 6. Range Sum Formula

For 0-indexed prefix:

```text
sum(l,r) = pref[r] - pref[l-1]
```

If:

```text
l = 0
```

then:

```text
sum(0,r) = pref[r]
```

For 1-indexed prefix:

```text
sum(l,r) = pref[r+1] - pref[l]
```

This works even when:

```text
l = 0
```

because:

```text
pref[0] = 0
```

---

## 7. 0-Indexed Prefix vs 1-Indexed Prefix

### 0-Indexed Prefix

```cpp
pref[0] = a[0];

for (int i = 1; i < n; i++) {
    pref[i] = pref[i - 1] + a[i];
}
```

Query:

```cpp
if (l == 0) ans = pref[r];
else ans = pref[r] - pref[l - 1];
```

### 1-Indexed Prefix

```cpp
pref[0] = 0;

for (int i = 1; i <= n; i++) {
    pref[i] = pref[i - 1] + a[i - 1];
}
```

Query:

```cpp
ans = pref[r + 1] - pref[l];
```

### Recommended For CP

Use:

```text
1-indexed prefix
```

because:

```text
No special case for l = 0.
```

---

## 8. Visual Intuition

Array:

```text
index:   0   1   2   3   4   5
a    :  [4,  2,  3,  1, -5,  6]
```

1-indexed prefix:

```text
index:   0   1   2   3   4   5   6
pref :  [0,  4,  6,  9, 10,  5, 11]
```

Query:

```text
sum(1,3)
```

means:

```text
a[1] + a[2] + a[3]
= 2 + 3 + 1
= 6
```

Using prefix:

```text
pref[4] - pref[1]
= 10 - 4
= 6
```

---

## 9. Step-by-Step Dry Run

Input:

```text
a = [4, 2, 3, 1, -5, 6]
```

Build 1-indexed prefix.

Initial:

```text
index a   :  0   1   2   3   4   5
a         : [4,  2,  3,  1, -5,  6]

index pref:  0   1   2   3   4   5   6
pref      : [0,  0,  0,  0,  0,  0,  0]
```

### i = 1

```text
Current element = a[0] = 4

pref[1] = pref[0] + a[0]
        = 0 + 4
        = 4
```

State:

```text
pref = [0, 4, 0, 0, 0, 0, 0]
```

### i = 2

```text
Current element = a[1] = 2

pref[2] = pref[1] + a[1]
        = 4 + 2
        = 6
```

State:

```text
pref = [0, 4, 6, 0, 0, 0, 0]
```

### i = 3

```text
Current element = a[2] = 3

pref[3] = pref[2] + a[2]
        = 6 + 3
        = 9
```

State:

```text
pref = [0, 4, 6, 9, 0, 0, 0]
```

### i = 4

```text
Current element = a[3] = 1

pref[4] = pref[3] + a[3]
        = 9 + 1
        = 10
```

State:

```text
pref = [0, 4, 6, 9, 10, 0, 0]
```

### i = 5

```text
Current element = a[4] = -5

pref[5] = pref[4] + a[4]
        = 10 - 5
        = 5
```

State:

```text
pref = [0, 4, 6, 9, 10, 5, 0]
```

### i = 6

```text
Current element = a[5] = 6

pref[6] = pref[5] + a[5]
        = 5 + 6
        = 11
```

Final:

```text
pref = [0, 4, 6, 9, 10, 5, 11]
```

---

## 10. Why Query Becomes O(1)

Once prefix is built, every range query uses only two array reads:

```text
pref[r+1]
pref[l]
```

Then one subtraction:

```text
pref[r+1] - pref[l]
```

So query is:

```text
O(1)
```

Example:

```text
sum(2,4)
```

Array:

```text
a[2] + a[3] + a[4]
= 3 + 1 - 5
= -1
```

Prefix:

```text
pref[5] - pref[2]
= 5 - 6
= -1
```

---

## 11. Brute Force vs Prefix Sum

### Brute Force

```cpp
long long rangeSumBrute(vector<int>& a, int l, int r) {
    long long ans = 0;

    for (int i = l; i <= r; i++) {
        ans += a[i];
    }

    return ans;
}
```

Complexity:

```text
O(length of range)
```

### Prefix Sum

```cpp
long long rangeSumPrefix(vector<long long>& pref, int l, int r) {
    return pref[r + 1] - pref[l];
}
```

Complexity:

```text
O(1)
```

---

## 12. Problem Form 1 — Running Sum

### Problem

Given:

```text
nums = [1,2,3,4]
```

Return:

```text
[1,3,6,10]
```

### Pattern

```text
Prefix construction
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> runningSum(vector<int>& nums) {
    for (int i = 1; i < nums.size(); i++) {
        nums[i] += nums[i - 1];
    }

    return nums;
}
```

---

## 13. Problem Form 2 — Static Range Sum Query

### Problem

Given array and many queries:

```text
sum(l,r)
```

answer each quickly.

### Pattern

```text
Build prefix once.
Answer query in O(1).
```

### C++ Code

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
```

---

## 14. Problem Form 3 — Left Sum / Right Sum

### Problem

Find pivot index where:

```text
left sum == right sum
```

### Pattern

```text
total sum + running prefix
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int pivotIndex(vector<int>& nums) {
    long long total = 0;

    for (int x : nums) {
        total += x;
    }

    long long left = 0;

    for (int i = 0; i < nums.size(); i++) {
        long long right = total - left - nums[i];

        if (left == right) {
            return i;
        }

        left += nums[i];
    }

    return -1;
}
```

---

## 15. Problem Form 4 — Fixed Window Sum

### Problem

Find maximum average of subarray of size `k`.

### Pattern

```text
fixed range sum using prefix
```

### C++ Code

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

    for (int l = 0; l + k <= n; l++) {
        long long sum = pref[l + k] - pref[l];
        best = max(best, sum);
    }

    return (double)best / k;
}
```

---

## 16. Problem Form 5 — Count Range Aggregates

### Problem

Given daily request counts:

```text
[10, 20, 5, 15, 30]
```

Answer:

```text
How many requests from day L to day R?
```

### Pattern

```text
Prefix range aggregation
```

Example:

```text
L = 1, R = 3
20 + 5 + 15 = 40
```

Using prefix:

```text
pref = [0,10,30,35,50,80]
pref[4] - pref[1] = 50 - 10 = 40
```

---

## 17. Real World Model 1 — CPU Metrics Aggregation

Suppose you collect CPU usage per minute:

```text
minute:  0   1   2   3   4
cpu   : 30  40  35  50  45
```

Question:

```text
What is total CPU usage from minute 1 to 3?
```

Brute force:

```text
40 + 35 + 50 = 125
```

Prefix:

```text
pref = [0,30,70,105,155,200]
sum(1,3) = pref[4] - pref[1]
         = 155 - 30
         = 125
```

This is exactly how monitoring systems pre-aggregate metrics.

---

## 18. Real World Model 2 — Banking Ledger Running Balance

Transactions:

```text
+100, -20, +50, -10
```

Running balance:

```text
100, 80, 130, 120
```

This is prefix sum.

If a bank wants:

```text
Net change between transaction 1 and 3
```

Use:

```text
prefix[4] - prefix[1]
```

This is range aggregation over ledger entries.

---

## 19. Real World Model 3 — Analytics Dashboard Query

Suppose an analytics dashboard stores daily active users:

```text
day:  0    1    2    3    4
dau: 100  150  120  170  200
```

Dashboard query:

```text
Total users from day 1 to day 3
```

Prefix:

```text
pref = [0,100,250,370,540,740]
```

Query:

```text
pref[4] - pref[1]
= 540 - 100
= 440
```

This is similar to OLAP range aggregation.

---

## 20. Real World Model 4 — Log Count Aggregation

Logs per hour:

```text
hour:  0   1   2   3   4
logs: 50  80  60  90  70
```

Query:

```text
How many logs between hour 1 and hour 3?
```

Prefix:

```text
pref = [0,50,130,190,280,350]
```

Answer:

```text
pref[4] - pref[1]
= 280 - 50
= 230
```

This maps to:

```text
ELK / observability / log analytics
```

---

## 21. Decision Tree

```text
Array / sequence problem?
|
+-- Need sum from L to R many times?
|   |
|   +-- Yes -> Prefix Sum
|
+-- Need running total?
|   |
|   +-- Yes -> Prefix Sum
|
+-- Need left sum and right sum?
|   |
|   +-- Yes -> Prefix Sum / running sum
|
+-- Need fixed window sum?
|   |
|   +-- Yes -> Prefix Sum or Sliding Window
|
+-- Need many range updates?
    |
    +-- Use Difference Array, not prefix sum directly
```

---

## 22. Common Mistakes

### Mistake 1 — Off-by-one error

Wrong:

```cpp
pref[r] - pref[l]
```

Correct for 1-indexed prefix:

```cpp
pref[r + 1] - pref[l]
```

### Mistake 2 — Forgetting `pref[0] = 0`

Always initialize:

```cpp
vector<long long> pref(n + 1, 0);
```

### Mistake 3 — Using `int` for large sums

If:

```text
n = 2e5
a[i] = 1e9
```

sum can be:

```text
2e14
```

Use:

```cpp
long long
```

### Mistake 4 — Using prefix sum for updates

Prefix sum answers range queries.

For many range updates, use:

```text
difference array
```

---

## 23. Complexity

Build prefix:

```text
O(n)
```

Each query:

```text
O(1)
```

Total for `q` queries:

```text
O(n + q)
```

Space:

```text
O(n)
```

If in-place:

```text
O(1) extra space
```

---

## 24. Reusable C++ Template

```cpp
#include <bits/stdc++.h>
using namespace std;

class PrefixSum {
private:
    vector<long long> pref;

public:
    PrefixSum(const vector<int>& a) {
        int n = a.size();

        pref.assign(n + 1, 0);

        for (int i = 1; i <= n; i++) {
            pref[i] = pref[i - 1] + a[i - 1];
        }
    }

    long long rangeSum(int l, int r) {
        return pref[r + 1] - pref[l];
    }
};

int main() {
    vector<int> a = {4, 2, 3, 1, -5, 6};

    PrefixSum ps(a);

    cout << ps.rangeSum(1, 3) << "\n"; // 6
    cout << ps.rangeSum(2, 4) << "\n"; // -1

    return 0;
}
```

---

## 25. CP / FAANG Problem Forms

### Problem 1 — Range Sum Query Immutable

Recognition:

```text
many sumRange(left,right) queries
array does not change
```

Pattern:

```text
Prefix Sum
```

Steps:

```text
1. Build pref[0] = 0
2. pref[i] = pref[i-1] + nums[i-1]
3. Query answer = pref[right+1] - pref[left]
```

### Problem 2 — Find Pivot Index

Recognition:

```text
left sum equals right sum
```

Pattern:

```text
Total sum + running prefix
```

### Problem 3 — Maximum Average Subarray I

Recognition:

```text
fixed length subarray sum
```

Pattern:

```text
Prefix Sum / Sliding Window
```

### Problem 4 — Product Analytics Range Query

Given daily orders:

```text
orders[i]
```

Answer many queries:

```text
total orders from day L to day R
```

Pattern:

```text
Prefix aggregation
```

Backend mapping:

```text
analytics dashboard range aggregation
```

### Problem 5 — Log Count Between Two Timestamps

Given logs per minute, answer:

```text
number of logs between minute L and minute R
```

Pattern:

```text
Prefix count aggregation
```

Production mapping:

```text
ELK / metrics / monitoring dashboard
```

---

## 26. Practice Checklist

Before solving, ask:

```text
1. Is array static?
2. Are there many range sum queries?
3. Do I need running total?
4. Do I need left/right sum?
5. Can I precompute once?
6. Is each query asking L..R?
7. Do I need O(1) query?
8. Are values large enough for long long?
9. Is this range update instead? If yes, use difference array.
```

---

## 27. Next Step

```text
002_Building_1D_Prefix_Sum.md
```

This file will go deeper into:

```text
prefix construction
1-indexed implementation
in-place prefix
debugging prefix arrays
query engine analogy
production aggregation model
```
