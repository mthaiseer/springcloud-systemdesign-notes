# 002_Building_1D_Prefix_Sum.md — MiniPrefixSumDifferenceEngine

# Building 1D Prefix Sum

> This file focuses only on **how to build prefix sum correctly**.  
> If `001_What_Is_Prefix_Sum.md` explains the idea, this file explains the implementation discipline.

---

## Clickable Index

1. [What This File Teaches](#1-what-this-file-teaches)
2. [Why Prefix Building Needs Its Own File](#2-why-prefix-building-needs-its-own-file)
3. [Core Build Formula](#3-core-build-formula)
4. [1-Indexed Prefix Build](#4-1-indexed-prefix-build)
5. [0-Indexed Prefix Build](#5-0-indexed-prefix-build)
6. [Why 1-Indexed Prefix Is Better For CP](#6-why-1-indexed-prefix-is-better-for-cp)
7. [Index Mapping](#7-index-mapping)
8. [Step-by-Step Dry Run — Build Prefix](#8-step-by-step-dry-run--build-prefix)
9. [Step-by-Step Dry Run — Query After Build](#9-step-by-step-dry-run--query-after-build)
10. [In-Place Prefix Sum](#10-in-place-prefix-sum)
11. [Prefix Build From Stream](#11-prefix-build-from-stream)
12. [Debugging Prefix Arrays](#12-debugging-prefix-arrays)
13. [Problem Form 1 — Build Prefix Array](#13-problem-form-1--build-prefix-array)
14. [Problem Form 2 — Query Using Built Prefix](#14-problem-form-2--query-using-built-prefix)
15. [Problem Form 3 — In-Place Running Sum](#15-problem-form-3--in-place-running-sum)
16. [Problem Form 4 — Prefix For Negative Numbers](#16-problem-form-4--prefix-for-negative-numbers)
17. [Problem Form 5 — Prefix For Long Long Sums](#17-problem-form-5--prefix-for-long-long-sums)
18. [Real World Model 1 — Query Engine Preprocessing](#18-real-world-model-1--query-engine-preprocessing)
19. [Real World Model 2 — Metrics Rollup Precompute](#19-real-world-model-2--metrics-rollup-precompute)
20. [Real World Model 3 — Ledger Balance Materialization](#20-real-world-model-3--ledger-balance-materialization)
21. [Decision Tree](#21-decision-tree)
22. [Common Mistakes](#22-common-mistakes)
23. [Complexity](#23-complexity)
24. [Reusable C++ Templates](#24-reusable-c-templates)
25. [CP / FAANG Problem Forms](#25-cp--faang-problem-forms)
26. [Practice Checklist](#26-practice-checklist)
27. [Next Step](#27-next-step)

---

## 1. What This File Teaches

This file teaches how to build prefix sum arrays correctly.

You will learn:

```text
1. 1-indexed prefix construction
2. 0-indexed prefix construction
3. index mapping
4. in-place prefix construction
5. prefix debugging
6. why long long is important
7. real-world precomputation models
```

---

## 2. Why Prefix Building Needs Its Own File

Prefix sum is simple, but many bugs come from:

```text
off-by-one errors
wrong prefix size
wrong query formula
integer overflow
mixing 0-indexed and 1-indexed logic
```

In CP and FAANG interviews, prefix sum bugs are usually not logic bugs.

They are:

```text
indexing bugs
```

So this file focuses on building clean muscle memory.

---

## 3. Core Build Formula

For an array:

```text
a = [a0, a1, a2, ..., a(n-1)]
```

1-indexed prefix:

```text
pref[0] = 0
pref[1] = a0
pref[2] = a0 + a1
pref[3] = a0 + a1 + a2
```

Formula:

```text
pref[i] = pref[i-1] + a[i-1]
```

for:

```text
i = 1 to n
```

---

## 4. 1-Indexed Prefix Build

### Formula

```text
pref[0] = 0
pref[i] = pref[i-1] + a[i-1]
```

### Code

```cpp
vector<long long> pref(n + 1, 0);

for (int i = 1; i <= n; i++) {
    pref[i] = pref[i - 1] + a[i - 1];
}
```

### Meaning

```text
pref[i] stores sum of first i elements
```

So:

```text
pref[0] = sum of first 0 elements = 0
pref[1] = sum of first 1 element
pref[2] = sum of first 2 elements
```

---

## 5. 0-Indexed Prefix Build

### Formula

```text
pref[0] = a[0]
pref[i] = pref[i-1] + a[i]
```

### Code

```cpp
vector<long long> pref(n, 0);

pref[0] = a[0];

for (int i = 1; i < n; i++) {
    pref[i] = pref[i - 1] + a[i];
}
```

### Query

```text
sum(l,r) = pref[r] - pref[l-1]
```

Special case:

```text
if l == 0:
    sum = pref[r]
```

---

## 6. Why 1-Indexed Prefix Is Better For CP

1-indexed prefix is usually better because:

```text
1. No special case for l = 0
2. Query formula is consistent
3. Works cleanly for 2D prefix later
4. Easier to debug
5. Easier to use in contest templates
```

The query formula always becomes:

```text
sum(l,r) = pref[r+1] - pref[l]
```

This works for:

```text
l = 0
```

because:

```text
pref[0] = 0
```

---

## 7. Index Mapping

This is the most important part.

Array index:

```text
a[0] a[1] a[2] ... a[n-1]
```

Prefix index:

```text
pref[0] pref[1] pref[2] ... pref[n]
```

Mapping:

```text
pref[i] = sum of a[0] to a[i-1]
```

So:

```text
pref[3] = a[0] + a[1] + a[2]
```

Not:

```text
a[3]
```

Remember:

```text
pref index is shifted by 1
```

---

## 8. Step-by-Step Dry Run — Build Prefix

Input:

```text
a = [5, -2, 7, 3]
```

Initial:

```text
index a   :  0   1   2   3
a         : [5, -2,  7,  3]

index pref:  0   1   2   3   4
pref      : [0,  0,  0,  0,  0]
```

### i = 1

```text
Use:
a[i-1] = a[0] = 5

pref[1] = pref[0] + a[0]
        = 0 + 5
        = 5
```

State:

```text
pref = [0, 5, 0, 0, 0]
```

### i = 2

```text
Use:
a[i-1] = a[1] = -2

pref[2] = pref[1] + a[1]
        = 5 + (-2)
        = 3
```

State:

```text
pref = [0, 5, 3, 0, 0]
```

### i = 3

```text
Use:
a[i-1] = a[2] = 7

pref[3] = pref[2] + a[2]
        = 3 + 7
        = 10
```

State:

```text
pref = [0, 5, 3, 10, 0]
```

### i = 4

```text
Use:
a[i-1] = a[3] = 3

pref[4] = pref[3] + a[3]
        = 10 + 3
        = 13
```

Final:

```text
pref = [0, 5, 3, 10, 13]
```

Meaning:

```text
pref[4] = sum of all 4 elements = 13
```

---

## 9. Step-by-Step Dry Run — Query After Build

Array:

```text
a = [5, -2, 7, 3]
```

Prefix:

```text
pref = [0, 5, 3, 10, 13]
```

Query:

```text
sum(1,2)
```

Meaning:

```text
a[1] + a[2]
= -2 + 7
= 5
```

Using prefix:

```text
sum(1,2) = pref[2+1] - pref[1]
         = pref[3] - pref[1]
         = 10 - 5
         = 5
```

Query:

```text
sum(0,3)
```

Using prefix:

```text
pref[4] - pref[0]
= 13 - 0
= 13
```

No special case needed.

---

## 10. In-Place Prefix Sum

Sometimes you can modify the original array.

Example:

```text
a = [1, 2, 3, 4]
```

Convert in-place:

```text
a = [1, 3, 6, 10]
```

Code:

```cpp
for (int i = 1; i < n; i++) {
    a[i] += a[i - 1];
}
```

### When To Use

Use in-place prefix when:

```text
1. Original array is no longer needed
2. You want O(1) extra space
3. Problem asks to return running sum
```

### When NOT To Use

Do not use in-place prefix when:

```text
1. You still need original values later
2. You need clean 1-indexed query formula
3. You want safer contest template
```

---

## 11. Prefix Build From Stream

Sometimes values come one by one.

Example:

```text
logs per minute stream:
10, 20, 5, 15
```

We can maintain:

```text
running += value
pref.push_back(running)
```

Code:

```cpp
vector<long long> pref;
pref.push_back(0);

long long running = 0;

for (int x : streamValues) {
    running += x;
    pref.push_back(running);
}
```

This is useful in:

```text
monitoring systems
log analytics
stream processing
financial ledgers
```

---

## 12. Debugging Prefix Arrays

To debug prefix sum, print:

```text
array
prefix
query l,r
pref[r+1]
pref[l]
answer
```

Example debug:

```cpp
cerr << "l=" << l << " r=" << r << "\n";
cerr << "pref[r+1]=" << pref[r+1] << "\n";
cerr << "pref[l]=" << pref[l] << "\n";
cerr << "ans=" << pref[r+1] - pref[l] << "\n";
```

Most prefix bugs are found by checking:

```text
Did I use r+1?
Did I allocate n+1?
Did I initialize pref[0]=0?
```

---

## 13. Problem Form 1 — Build Prefix Array

### Problem

Given:

```text
a = [2, 4, 6]
```

Build 1-indexed prefix.

### Step-by-Step Working

```text
pref[0] = 0
pref[1] = 2
pref[2] = 2 + 4 = 6
pref[3] = 2 + 4 + 6 = 12
```

Answer:

```text
[0, 2, 6, 12]
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {2, 4, 6};

    int n = a.size();

    // n+1 because pref[0] represents empty prefix
    vector<long long> pref(n + 1, 0);

    for (int i = 1; i <= n; i++) {
        // pref[i] = sum of first i elements
        // current array element is a[i-1]
        pref[i] = pref[i - 1] + a[i - 1];
    }

    for (long long x : pref) {
        cout << x << " ";
    }

    return 0;
}
```

---

## 14. Problem Form 2 — Query Using Built Prefix

### Problem

Given:

```text
a = [2, 4, 6, 8]
```

Find:

```text
sum(1,3)
```

### Step-by-Step Working

Build prefix:

```text
pref = [0,2,6,12,20]
```

Query:

```text
sum(1,3) = pref[4] - pref[1]
         = 20 - 2
         = 18
```

Check manually:

```text
4 + 6 + 8 = 18
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {2, 4, 6, 8};

    int n = a.size();

    vector<long long> pref(n + 1, 0);

    for (int i = 1; i <= n; i++) {
        pref[i] = pref[i - 1] + a[i - 1];
    }

    int l = 1;
    int r = 3;

    // 1-indexed prefix query formula
    long long ans = pref[r + 1] - pref[l];

    cout << ans << endl;

    return 0;
}
```

---

## 15. Problem Form 3 — In-Place Running Sum

### Problem

Convert:

```text
[1, 2, 3, 4]
```

into:

```text
[1, 3, 6, 10]
```

### Step-by-Step Working

```text
i = 1: a[1] = a[1] + a[0] = 2 + 1 = 3
i = 2: a[2] = a[2] + a[1] = 3 + 3 = 6
i = 3: a[3] = a[3] + a[2] = 4 + 6 = 10
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {1, 2, 3, 4};

    for (int i = 1; i < a.size(); i++) {
        // a[i-1] already contains prefix sum up to previous index
        a[i] += a[i - 1];
    }

    for (int x : a) {
        cout << x << " ";
    }

    return 0;
}
```

---

## 16. Problem Form 4 — Prefix For Negative Numbers

Prefix sum works with negative numbers also.

### Example

```text
a = [5, -2, 7, -3]
```

Prefix:

```text
pref[0] = 0
pref[1] = 5
pref[2] = 3
pref[3] = 10
pref[4] = 7
```

Range:

```text
sum(1,3)
= -2 + 7 - 3
= 2
```

Prefix query:

```text
pref[4] - pref[1]
= 7 - 5
= 2
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, -2, 7, -3};

    vector<long long> pref(a.size() + 1, 0);

    for (int i = 1; i <= a.size(); i++) {
        pref[i] = pref[i - 1] + a[i - 1];
    }

    cout << pref[4] - pref[1] << endl; // sum(1,3)

    return 0;
}
```

---

## 17. Problem Form 5 — Prefix For Long Long Sums

If values are large:

```text
n = 200000
a[i] = 1000000000
```

Total sum can be:

```text
2e14
```

This exceeds `int`.

Use:

```cpp
long long
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n = 200000;

    vector<int> a(n, 1000000000);

    vector<long long> pref(n + 1, 0);

    for (int i = 1; i <= n; i++) {
        pref[i] = pref[i - 1] + a[i - 1];
    }

    cout << pref[n] << endl; // 200000000000000

    return 0;
}
```

---

## 18. Real World Model 1 — Query Engine Preprocessing

Imagine a backend service receives many queries:

```text
GET /sales?from=day10&to=day200
```

Without prefix:

```text
scan day10 to day200 every time
```

With prefix:

```text
precompute cumulative sales
answer range query using two reads
```

This is similar to:

```text
materialized aggregation
```

---

## 19. Real World Model 2 — Metrics Rollup Precompute

Monitoring systems collect metrics every minute:

```text
requests_per_minute
cpu_usage
memory_usage
error_count
```

Prefix-like precomputation helps answer:

```text
total requests between 10:00 and 10:30
```

quickly.

This is a simplified version of:

```text
metrics rollup
time-series aggregation
```

---

## 20. Real World Model 3 — Ledger Balance Materialization

Financial systems store transaction events:

```text
+100
-30
+50
-10
```

Running balance is prefix sum:

```text
100
70
120
110
```

To query net movement over a transaction range:

```text
prefix[r+1] - prefix[l]
```

This maps to:

```text
ledger materialization
financial analytics
account balance snapshots
```

---

## 21. Decision Tree

```text
Need to build prefix?
|
+-- Do you need original array later?
|   |
|   +-- Yes -> build separate pref[n+1]
|   |
|   +-- No -> in-place prefix may work
|
+-- Need many range queries?
|   |
|   +-- Yes -> build 1-indexed prefix
|
+-- Need safe CP implementation?
|   |
|   +-- Yes -> use pref[0]=0 and size n+1
|
+-- Values can be large?
    |
    +-- Use long long
```

---

## 22. Common Mistakes

### Mistake 1 — Allocating `pref(n)` instead of `pref(n+1)`

Wrong:

```cpp
vector<long long> pref(n);
```

Correct:

```cpp
vector<long long> pref(n + 1, 0);
```

---

### Mistake 2 — Using `a[i]` instead of `a[i-1]`

In this loop:

```cpp
for (int i = 1; i <= n; i++)
```

Correct:

```cpp
pref[i] = pref[i - 1] + a[i - 1];
```

Wrong:

```cpp
pref[i] = pref[i - 1] + a[i];
```

---

### Mistake 3 — Querying with wrong formula

Correct:

```cpp
pref[r + 1] - pref[l]
```

Wrong:

```cpp
pref[r] - pref[l]
```

---

### Mistake 4 — Forgetting negative values are allowed

Prefix works with:

```text
positive
negative
zero
```

But sliding window may fail with negatives.

---

### Mistake 5 — Using int

Use:

```cpp
long long
```

for prefix sums.

---

## 23. Complexity

Build separate prefix:

```text
Time : O(n)
Space: O(n)
```

Query:

```text
O(1)
```

In-place prefix:

```text
Time : O(n)
Space: O(1) extra
```

---

## 24. Reusable C++ Templates

### Template 1 — Standard 1-Indexed Prefix

```cpp
vector<long long> buildPrefix(const vector<int>& a) {
    int n = a.size();

    vector<long long> pref(n + 1, 0);

    for (int i = 1; i <= n; i++) {
        pref[i] = pref[i - 1] + a[i - 1];
    }

    return pref;
}

long long rangeSum(const vector<long long>& pref, int l, int r) {
    return pref[r + 1] - pref[l];
}
```

### Template 2 — In-Place Prefix

```cpp
void buildInPlacePrefix(vector<long long>& a) {
    for (int i = 1; i < a.size(); i++) {
        a[i] += a[i - 1];
    }
}
```

### Template 3 — Debug Printer

```cpp
void printPrefixDebug(const vector<int>& a, const vector<long long>& pref) {
    cout << "Array:\n";
    for (int x : a) cout << x << " ";
    cout << "\nPrefix:\n";
    for (long long x : pref) cout << x << " ";
    cout << "\n";
}
```

---

## 25. CP / FAANG Problem Forms

---

### Problem 1 — Build Prefix For Static Query Class

#### Recognition

```text
constructor preprocesses array
sumRange queries later
```

#### Pattern

```text
build prefix once
query many times
```

#### Commented C++ Code

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

### Problem 2 — Running Sum In-Place

#### Recognition

```text
return runningSum array
```

#### Pattern

```text
in-place prefix build
```

#### Commented C++ Code

```cpp
vector<int> runningSum(vector<int>& nums) {
    for (int i = 1; i < nums.size(); i++) {
        nums[i] += nums[i - 1];
    }

    return nums;
}
```

---

### Problem 3 — Large Range Sum Queries

#### Recognition

```text
n and q are large
array static
many L R queries
```

#### Pattern

```text
long long prefix
```

#### Commented C++ Code

```cpp
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

        cout << pref[r + 1] - pref[l] << '\n';
    }

    return 0;
}
```

---

### Problem 4 — Prefix Build With Negative Numbers

#### Recognition

```text
array may contain negative values
need exact range sums
```

#### Pattern

```text
prefix still works
sliding window may not
```

#### Commented C++ Code

```cpp
vector<long long> pref(n + 1, 0);

for (int i = 1; i <= n; i++) {
    pref[i] = pref[i - 1] + a[i - 1];
}
```

---

### Problem 5 — Backend Analytics Precompute

#### Scenario

Daily revenue:

```text
revenue[i]
```

Many dashboard queries:

```text
sum revenue from day L to day R
```

#### Pattern

```text
materialized prefix aggregation
```

#### Code Model

```cpp
class RevenueQueryEngine {
private:
    vector<long long> pref;

public:
    RevenueQueryEngine(const vector<int>& revenue) {
        pref.assign(revenue.size() + 1, 0);

        for (int i = 1; i <= revenue.size(); i++) {
            pref[i] = pref[i - 1] + revenue[i - 1];
        }
    }

    long long queryRevenue(int l, int r) {
        return pref[r + 1] - pref[l];
    }
};
```

---

## 26. Practice Checklist

Before coding prefix build, ask:

```text
1. Am I using 1-indexed prefix?
2. Did I allocate n+1?
3. Is pref[0] initialized to 0?
4. Is loop from i=1 to n?
5. Am I adding a[i-1]?
6. Is query pref[r+1] - pref[l]?
7. Do I need long long?
8. Do I still need original array?
9. Should I avoid in-place mutation?
10. Did I test l=0?
```

---

## 27. Next Step

```text
003_Range_Sum_Query.md
```

That file focuses on:

```text
query processing
multiple range queries
range query APIs
FAANG immutable range query class
database/analytics query model
