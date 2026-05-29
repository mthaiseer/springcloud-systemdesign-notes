# 003_Range_Sum_Query.md — MiniPrefixSumDifferenceEngine

# Range Sum Query

> Range Sum Query is the most direct and common use of prefix sum.  
> Pattern:
>
> ```text
> Static array + many sum(L,R) queries
> ```
>
> Solution:
>
> ```text
> Build prefix once, answer every query in O(1)
> ```

---

## Clickable Index

1. [What Is Range Sum Query?](#1-what-is-range-sum-query)
2. [Why This Problem Matters](#2-why-this-problem-matters)
3. [Core Problem Form](#3-core-problem-form)
4. [Brute Force Approach](#4-brute-force-approach)
5. [Prefix Sum Optimization](#5-prefix-sum-optimization)
6. [Range Query Formula](#6-range-query-formula)
7. [Index Mapping](#7-index-mapping)
8. [Step-by-Step Dry Run — Build Prefix](#8-step-by-step-dry-run--build-prefix)
9. [Step-by-Step Dry Run — Query 1](#9-step-by-step-dry-run--query-1)
10. [Step-by-Step Dry Run — Query 2](#10-step-by-step-dry-run--query-2)
11. [Range Query Class Design](#11-range-query-class-design)
12. [Handling Many Queries In CP](#12-handling-many-queries-in-cp)
13. [Problem Form 1 — Immutable Range Sum](#13-problem-form-1--immutable-range-sum)
14. [Problem Form 2 — Multiple Offline Queries](#14-problem-form-2--multiple-offline-queries)
15. [Problem Form 3 — Revenue Between Days](#15-problem-form-3--revenue-between-days)
16. [Problem Form 4 — Logs Between Timestamps](#16-problem-form-4--logs-between-timestamps)
17. [Problem Form 5 — Fixed Window Using Range Query](#17-problem-form-5--fixed-window-using-range-query)
18. [Real World Model 1 — Analytics Dashboard](#18-real-world-model-1--analytics-dashboard)
19. [Real World Model 2 — Database Range Aggregation](#19-real-world-model-2--database-range-aggregation)
20. [Real World Model 3 — Monitoring Metrics Query](#20-real-world-model-3--monitoring-metrics-query)
21. [Real World Model 4 — Billing Usage Query](#21-real-world-model-4--billing-usage-query)
22. [Decision Tree](#22-decision-tree)
23. [Common Mistakes](#23-common-mistakes)
24. [Complexity](#24-complexity)
25. [Reusable C++ Templates](#25-reusable-c-templates)
26. [CP / FAANG Problem Forms](#26-cp--faang-problem-forms)
27. [Practice Checklist](#27-practice-checklist)
28. [Next Step](#28-next-step)

---

## 1. What Is Range Sum Query?

Range Sum Query means:

```text
Given an array, answer sum of elements from index L to R.
```

Example:

```text
a = [4, 2, 3, 1, -5, 6]
```

Query:

```text
sum(1,3)
```

Means:

```text
a[1] + a[2] + a[3]
= 2 + 3 + 1
= 6
```

---

## 2. Why This Problem Matters

A single range sum is easy.

But many range sum queries are expensive if solved repeatedly by loops.

If:

```text
n = 200000
q = 200000
```

Brute force may become:

```text
O(n*q)
```

Prefix sum converts it to:

```text
O(n + q)
```

This is one of the first big optimization patterns in DSA.

---

## 3. Core Problem Form

Input:

```text
array a[]
q queries
each query = l r
```

Output:

```text
sum of a[l..r]
```

Constraints usually indicate prefix sum when:

```text
n is large
q is large
array does not change
queries ask range sum
```

---

## 4. Brute Force Approach

For every query:

```cpp
long long ans = 0;

for (int i = l; i <= r; i++) {
    ans += a[i];
}
```

If each query scans a big range, this is slow.

Complexity:

```text
O(q*n) worst case
```

---

## 5. Prefix Sum Optimization

Build:

```text
pref[0] = 0
pref[i] = pref[i-1] + a[i-1]
```

Then answer:

```text
sum(l,r) = pref[r+1] - pref[l]
```

This uses two prefix values and subtracts.

---

## 6. Range Query Formula

Using 1-indexed prefix:

```text
sum(l,r) = pref[r+1] - pref[l]
```

Why?

```text
pref[r+1] = sum of a[0] to a[r]
pref[l]   = sum of a[0] to a[l-1]
```

Subtract:

```text
a[l] + a[l+1] + ... + a[r]
```

---

## 7. Index Mapping

Array:

```text
index a   : 0   1   2   3   4   5
a         : 4   2   3   1  -5   6
```

Prefix:

```text
index pref: 0   1   2   3   4   5   6
pref      : 0   4   6   9  10   5  11
```

Important mapping:

```text
pref[i] = sum of first i array elements
```

So:

```text
pref[4] = a[0] + a[1] + a[2] + a[3]
```

---

## 8. Step-by-Step Dry Run — Build Prefix

Array:

```text
a = [4, 2, 3, 1, -5, 6]
```

Initial:

```text
pref = [0, 0, 0, 0, 0, 0, 0]
```

### i = 1

```text
pref[1] = pref[0] + a[0]
        = 0 + 4
        = 4
```

```text
pref = [0, 4, 0, 0, 0, 0, 0]
```

### i = 2

```text
pref[2] = pref[1] + a[1]
        = 4 + 2
        = 6
```

```text
pref = [0, 4, 6, 0, 0, 0, 0]
```

### i = 3

```text
pref[3] = pref[2] + a[2]
        = 6 + 3
        = 9
```

```text
pref = [0, 4, 6, 9, 0, 0, 0]
```

### i = 4

```text
pref[4] = pref[3] + a[3]
        = 9 + 1
        = 10
```

```text
pref = [0, 4, 6, 9, 10, 0, 0]
```

### i = 5

```text
pref[5] = pref[4] + a[4]
        = 10 + (-5)
        = 5
```

```text
pref = [0, 4, 6, 9, 10, 5, 0]
```

### i = 6

```text
pref[6] = pref[5] + a[5]
        = 5 + 6
        = 11
```

Final:

```text
pref = [0, 4, 6, 9, 10, 5, 11]
```

---

## 9. Step-by-Step Dry Run — Query 1

Query:

```text
sum(1,3)
```

Manual:

```text
a[1] + a[2] + a[3]
= 2 + 3 + 1
= 6
```

Prefix:

```text
sum(1,3) = pref[3+1] - pref[1]
         = pref[4] - pref[1]
         = 10 - 4
         = 6
```

Visual:

```text
pref[4] = 4 + 2 + 3 + 1
pref[1] = 4

remove first part:
(4 + 2 + 3 + 1) - 4
= 2 + 3 + 1
```

---

## 10. Step-by-Step Dry Run — Query 2

Query:

```text
sum(2,4)
```

Manual:

```text
a[2] + a[3] + a[4]
= 3 + 1 + (-5)
= -1
```

Prefix:

```text
sum(2,4) = pref[4+1] - pref[2]
         = pref[5] - pref[2]
         = 5 - 6
         = -1
```

---

## 11. Range Query Class Design

FAANG-style immutable range query often uses a class.

Example:

```text
Constructor builds prefix once.
sumRange(left,right) answers query.
```

This maps to LeetCode:

```text
Range Sum Query - Immutable
```

Code:

```cpp
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

## 12. Handling Many Queries In CP

Typical input:

```text
n q
a0 a1 a2 ... a(n-1)
l1 r1
l2 r2
...
```

Use:

```cpp
ios::sync_with_stdio(false);
cin.tie(nullptr);
```

because `q` can be large.

---

## 13. Problem Form 1 — Immutable Range Sum

### Problem

Design a data structure that supports:

```text
sumRange(left,right)
```

for a static array.

### Pattern

```text
prefix preprocessing
```

### Step-by-Step Working

```text
1. Build pref[n+1]
2. pref[0] = 0
3. pref[i] = pref[i-1] + nums[i-1]
4. Query = pref[right+1] - pref[left]
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class NumArray {
private:
    vector<long long> pref;

public:
    NumArray(vector<int>& nums) {
        int n = nums.size();

        // pref[0] stores empty prefix
        pref.assign(n + 1, 0);

        for (int i = 1; i <= n; i++) {
            // pref[i] stores sum of first i elements
            pref[i] = pref[i - 1] + nums[i - 1];
        }
    }

    long long sumRange(int left, int right) {
        // sum from left to right inclusive
        return pref[right + 1] - pref[left];
    }
};

int main() {
    vector<int> nums = {-2, 0, 3, -5, 2, -1};

    NumArray obj(nums);

    cout << obj.sumRange(0, 2) << "\n"; // 1
    cout << obj.sumRange(2, 5) << "\n"; // -1
    cout << obj.sumRange(0, 5) << "\n"; // -3

    return 0;
}
```

---

## 14. Problem Form 2 — Multiple Offline Queries

### Problem

Given `n`, `q`, array, and `q` queries:

```text
l r
```

Print range sum for each query.

### Pattern

```text
static array + many queries
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    cin >> n >> q;

    vector<long long> pref(n + 1, 0);

    for (int i = 1; i <= n; i++) {
        long long x;
        cin >> x;

        pref[i] = pref[i - 1] + x;
    }

    while (q--) {
        int l, r;
        cin >> l >> r;

        // Assumes input l and r are 0-indexed inclusive
        cout << pref[r + 1] - pref[l] << '\n';
    }

    return 0;
}
```

---

## 15. Problem Form 3 — Revenue Between Days

### Problem

Daily revenue:

```text
[100, 200, 150, 300, 250]
```

Query:

```text
Revenue from day 1 to day 3
```

### Step-by-Step Working

Manual:

```text
200 + 150 + 300 = 650
```

Prefix:

```text
pref = [0,100,300,450,750,1000]
```

Query:

```text
pref[4] - pref[1]
= 750 - 100
= 650
```

### Code Model

```cpp
long long revenueBetweenDays(const vector<long long>& pref, int l, int r) {
    return pref[r + 1] - pref[l];
}
```

---

## 16. Problem Form 4 — Logs Between Timestamps

### Problem

Logs per minute:

```text
[5, 7, 3, 10, 4]
```

Query:

```text
logs from minute 1 to minute 3
```

Manual:

```text
7 + 3 + 10 = 20
```

Prefix:

```text
pref = [0,5,12,15,25,29]
pref[4] - pref[1] = 25 - 5 = 20
```

---

## 17. Problem Form 5 — Fixed Window Using Range Query

### Problem

Find maximum sum of subarray of length `k`.

### Pattern

```text
range sum query inside loop
```

### Step-by-Step Working

For each start:

```text
l = 0, r = k-1
l = 1, r = k
...
```

Use:

```text
sum = pref[r+1] - pref[l]
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long maxWindowSum(vector<int>& nums, int k) {
    int n = nums.size();

    vector<long long> pref(n + 1, 0);

    for (int i = 1; i <= n; i++) {
        pref[i] = pref[i - 1] + nums[i - 1];
    }

    long long best = LLONG_MIN;

    for (int l = 0; l + k <= n; l++) {
        int r = l + k - 1;

        long long sum = pref[r + 1] - pref[l];

        best = max(best, sum);
    }

    return best;
}

int main() {
    vector<int> nums = {1, 12, -5, -6, 50, 3};

    cout << maxWindowSum(nums, 4) << "\n";

    return 0;
}
```

---

## 18. Real World Model 1 — Analytics Dashboard

A dashboard may store daily sales:

```text
day:   0    1    2    3    4
sales: 50   80   40   100  70
```

User query:

```text
total sales from day 1 to day 3
```

Instead of scanning every day, backend uses:

```text
prefix aggregation
```

Query:

```text
pref[4] - pref[1]
```

This is conceptually similar to precomputed rollups in analytics systems.

---

## 19. Real World Model 2 — Database Range Aggregation

SQL query:

```sql
SELECT SUM(amount)
FROM orders
WHERE day BETWEEN L AND R;
```

If this type of query is very frequent, systems may precompute cumulative aggregates.

Conceptual equivalent:

```text
prefix[R+1] - prefix[L]
```

This is not exactly how every database works internally, but it is a useful mental model for materialized range aggregation.

---

## 20. Real World Model 3 — Monitoring Metrics Query

Metrics:

```text
requests per minute
errors per minute
cpu per minute
```

Query:

```text
total requests between minute 1000 and 2000
```

Prefix model:

```text
requestPrefix[2001] - requestPrefix[1000]
```

This is useful in:

```text
Grafana-like dashboards
Prometheus-style rollups
log analytics
monitoring systems
```

---

## 21. Real World Model 4 — Billing Usage Query

Cloud billing may track:

```text
compute usage per hour
storage usage per day
network egress per minute
```

To compute usage in a billing window:

```text
usage(start,end)
```

Prefix model:

```text
prefix[end+1] - prefix[start]
```

This is a simplified version of cumulative usage accounting.

---

## 22. Decision Tree

```text
Range query problem?
|
+-- Is array static?
|   |
|   +-- Yes
|       |
|       +-- Query asks sum(L,R)?
|           |
|           +-- Yes -> Prefix Sum
|
+-- Does array change between queries?
|   |
|   +-- Yes
|       |
|       +-- Point updates? -> Fenwick / Segment Tree
|       |
|       +-- Range updates? -> Difference Array / Segment Tree
|
+-- Query asks min/max?
|   |
|   +-- Use Sparse Table / Segment Tree, not prefix sum
```

---

## 23. Common Mistakes

### Mistake 1 — Using Prefix Sum When Array Updates

Prefix sum works best when:

```text
array is static
```

If values change after queries:

```text
use Fenwick Tree or Segment Tree
```

---

### Mistake 2 — Wrong Query Formula

Correct:

```cpp
pref[r + 1] - pref[l]
```

Wrong:

```cpp
pref[r] - pref[l]
```

---

### Mistake 3 — Input Is 1-Indexed But Code Assumes 0-Indexed

If input query is 1-indexed:

```text
l r
```

Convert:

```cpp
l--;
r--;
```

or use formula carefully.

---

### Mistake 4 — Overflow

Use:

```cpp
long long
```

for prefix sums.

---

### Mistake 5 — Forgetting Negative Numbers

Prefix sum works with negative numbers.

But if you use sliding window as replacement, negative numbers may break sliding window logic.

---

## 24. Complexity

Preprocessing:

```text
O(n)
```

Each query:

```text
O(1)
```

Total:

```text
O(n + q)
```

Space:

```text
O(n)
```

---

## 25. Reusable C++ Templates

### Template 1 — Range Sum Query Class

```cpp
class RangeSumQuery {
private:
    vector<long long> pref;

public:
    RangeSumQuery(const vector<int>& a) {
        int n = a.size();

        pref.assign(n + 1, 0);

        for (int i = 1; i <= n; i++) {
            pref[i] = pref[i - 1] + a[i - 1];
        }
    }

    long long query(int l, int r) const {
        return pref[r + 1] - pref[l];
    }
};
```

### Template 2 — CP Multiple Queries

```cpp
int n, q;
cin >> n >> q;

vector<long long> pref(n + 1, 0);

for (int i = 1; i <= n; i++) {
    long long x;
    cin >> x;
    pref[i] = pref[i - 1] + x;
}

while (q--) {
    int l, r;
    cin >> l >> r;

    cout << pref[r + 1] - pref[l] << '\n';
}
```

### Template 3 — If Input Queries Are 1-Indexed

```cpp
while (q--) {
    int l, r;
    cin >> l >> r;

    l--;
    r--;

    cout << pref[r + 1] - pref[l] << '\n';
}
```

---

## 26. CP / FAANG Problem Forms

---

### Problem 1 — LeetCode Range Sum Query Immutable

#### Recognition

```text
class with constructor + sumRange method
```

#### Pattern

```text
precompute prefix
```

#### Step-by-Step Working

```text
1. Constructor receives nums
2. Build pref[n+1]
3. sumRange returns pref[right+1] - pref[left]
```

---

### Problem 2 — CSES Static Range Sum Queries

#### Recognition

```text
n q
many l r queries
array static
```

#### Pattern

```text
prefix sum
```

#### Important

CSES uses 1-indexed input often.

Convert or use adjusted formula.

---

### Problem 3 — Maximum Fixed Window Sum

#### Recognition

```text
maximum sum of exactly k length
```

#### Pattern

```text
range sum repeated over all windows
```

#### Note

Can also be solved by sliding window.

Prefix version is easier to generalize.

---

### Problem 4 — Analytics Revenue Query

#### Recognition

```text
many dashboard range queries
static historical data
```

#### Pattern

```text
materialized cumulative aggregation
```

---

### Problem 5 — Billing Period Usage

#### Recognition

```text
usage between start and end
```

#### Pattern

```text
prefix usage array
```

---

## 27. Practice Checklist

Before solving range sum query:

```text
1. Is array static?
2. Are there many queries?
3. Is query asking sum(L,R)?
4. Are indexes 0-based or 1-based?
5. Did I build pref[n+1]?
6. Did I use long long?
7. Did I use pref[r+1] - pref[l]?
8. Did I test l=0?
9. Did I test negative numbers?
10. Does problem include updates? If yes, prefix alone is not enough.
```

---

## 28. Next Step

```text
004_InPlace_Prefix_Sum.md
```

That file focuses on:

```text
space optimization
mutating original array
running sum problems
when in-place is safe
when in-place is dangerous
```
