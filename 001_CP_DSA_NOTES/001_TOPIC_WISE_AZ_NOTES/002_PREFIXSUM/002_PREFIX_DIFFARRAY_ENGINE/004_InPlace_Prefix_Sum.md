# 004_InPlace_Prefix_Sum.md — MiniPrefixSumDifferenceEngine

# In-Place Prefix Sum

> In-place prefix sum means converting the original array itself into its prefix sum array.  
> This saves extra memory, but it destroys the original values.

---

## Clickable Index

1. [What Is In-Place Prefix Sum?](#1-what-is-in-place-prefix-sum)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Idea](#3-core-idea)
4. [In-Place Build Formula](#4-in-place-build-formula)
5. [Normal Prefix vs In-Place Prefix](#5-normal-prefix-vs-in-place-prefix)
6. [When To Use In-Place Prefix](#6-when-to-use-in-place-prefix)
7. [When NOT To Use In-Place Prefix](#7-when-not-to-use-in-place-prefix)
8. [Step-by-Step Dry Run](#8-step-by-step-dry-run)
9. [Range Query With In-Place Prefix](#9-range-query-with-in-place-prefix)
10. [Handling l = 0 Case](#10-handling-l--0-case)
11. [Problem Form 1 — Running Sum](#11-problem-form-1--running-sum)
12. [Problem Form 2 — Memory Optimized Prefix](#12-problem-form-2--memory-optimized-prefix)
13. [Problem Form 3 — In-Place Query After Mutation](#13-problem-form-3--in-place-query-after-mutation)
14. [Problem Form 4 — Prefix For Large Array](#14-problem-form-4--prefix-for-large-array)
15. [Problem Form 5 — Streaming Style Running Total](#15-problem-form-5--streaming-style-running-total)
16. [Real World Model 1 — Memory-Constrained Aggregation](#16-real-world-model-1--memory-constrained-aggregation)
17. [Real World Model 2 — Log Batch Transformation](#17-real-world-model-2--log-batch-transformation)
18. [Real World Model 3 — Financial Running Balance](#18-real-world-model-3--financial-running-balance)
19. [Real World Model 4 — Metrics Rollup Mutation](#19-real-world-model-4--metrics-rollup-mutation)
20. [Decision Tree](#20-decision-tree)
21. [Common Mistakes](#21-common-mistakes)
22. [Complexity](#22-complexity)
23. [Reusable C++ Templates](#23-reusable-c-templates)
24. [CP / FAANG Problem Forms](#24-cp--faang-problem-forms)
25. [Practice Checklist](#25-practice-checklist)
26. [Next Step](#26-next-step)

---

## 1. What Is In-Place Prefix Sum?

In-place prefix sum means:

```text
Use the same array to store prefix sums.
```

Original:

```text
a = [1, 2, 3, 4]
```

After in-place prefix:

```text
a = [1, 3, 6, 10]
```

Now:

```text
a[i] = sum from index 0 to i
```

---

## 2. Why This Topic Matters

Normal prefix sum uses extra memory:

```text
O(n)
```

In-place prefix sum uses:

```text
O(1) extra memory
```

This matters when:

```text
1. memory is constrained
2. original array is not needed later
3. problem asks to return running sum
4. transformation is allowed
```

---

## 3. Core Idea

At index `i`, the previous index already stores:

```text
sum from 0 to i-1
```

So:

```text
a[i] = a[i] + a[i-1]
```

After this update:

```text
a[i] stores sum from 0 to i
```

---

## 4. In-Place Build Formula

Formula:

```text
a[i] = a[i] + a[i-1]
```

for:

```text
i = 1 to n-1
```

C++:

```cpp
for (int i = 1; i < n; i++) {
    a[i] += a[i - 1];
}
```

---

## 5. Normal Prefix vs In-Place Prefix

### Normal Prefix

```text
Original array remains unchanged.
Extra prefix array is created.
```

Example:

```text
a    = [1, 2, 3, 4]
pref = [0, 1, 3, 6, 10]
```

### In-Place Prefix

```text
Original array is modified.
No separate prefix array.
```

Example:

```text
a before = [1, 2, 3, 4]
a after  = [1, 3, 6, 10]
```

---

## 6. When To Use In-Place Prefix

Use it when:

```text
1. Problem asks for running sum
2. Original array is no longer needed
3. You want O(1) extra memory
4. You only need prefix values afterward
5. Mutation is allowed
```

Good examples:

```text
Running Sum of 1D Array
Convert array to cumulative totals
Build running balance
```

---

## 7. When NOT To Use In-Place Prefix

Do not use it when:

```text
1. Original array is needed later
2. You need clean 1-indexed query formula
3. Query l = 0 special case becomes annoying
4. You are using array values for other logic
5. Interviewer expects immutable input
```

In interviews, always mention:

```text
This mutates the input array.
If mutation is not allowed, use separate prefix array.
```

---

## 8. Step-by-Step Dry Run

Input:

```text
a = [1, 2, 3, 4]
```

Initial:

```text
index:  0   1   2   3
a    : [1,  2,  3,  4]
```

### i = 1

```text
a[1] = a[1] + a[0]
     = 2 + 1
     = 3
```

State:

```text
a = [1, 3, 3, 4]
```

Meaning:

```text
a[1] now stores sum(0..1)
```

---

### i = 2

```text
a[2] = a[2] + a[1]
     = 3 + 3
     = 6
```

State:

```text
a = [1, 3, 6, 4]
```

Meaning:

```text
a[2] now stores sum(0..2)
```

---

### i = 3

```text
a[3] = a[3] + a[2]
     = 4 + 6
     = 10
```

Final:

```text
a = [1, 3, 6, 10]
```

---

## 9. Range Query With In-Place Prefix

After in-place prefix:

```text
a[i] = sum from 0 to i
```

So query:

```text
sum(l,r)
```

Formula:

```text
if l == 0:
    answer = a[r]
else:
    answer = a[r] - a[l-1]
```

Example:

```text
original = [4, 2, 3, 1]
after    = [4, 6, 9, 10]
```

Query:

```text
sum(1,3)
```

Answer:

```text
a[3] - a[0]
= 10 - 4
= 6
```

---

## 10. Handling l = 0 Case

With in-place prefix, query has special case:

```cpp
if (l == 0) return a[r];
return a[r] - a[l - 1];
```

This is one reason 1-indexed separate prefix is often cleaner:

```cpp
return pref[r + 1] - pref[l];
```

No special case.

---

## 11. Problem Form 1 — Running Sum

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
in-place prefix
```

### Step-by-Step Working

```text
i=1: nums[1] += nums[0] -> 2+1=3
i=2: nums[2] += nums[1] -> 3+3=6
i=3: nums[3] += nums[2] -> 4+6=10
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> runningSum(vector<int>& nums) {
    for (int i = 1; i < nums.size(); i++) {
        // nums[i-1] already contains sum from 0 to i-1
        nums[i] += nums[i - 1];
    }

    return nums;
}

int main() {
    vector<int> nums = {1, 2, 3, 4};

    vector<int> ans = runningSum(nums);

    for (int x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

## 12. Problem Form 2 — Memory Optimized Prefix

### Problem

Given a large array, convert it to prefix sum using no extra array.

### Pattern

```text
mutate original array
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<long long> a = {5, -2, 7, 3};

    for (int i = 1; i < a.size(); i++) {
        a[i] += a[i - 1];
    }

    for (long long x : a) {
        cout << x << " ";
    }

    return 0;
}
```

Output:

```text
5 3 10 13
```

---

## 13. Problem Form 3 — In-Place Query After Mutation

### Problem

Convert array to prefix sum in-place and answer:

```text
sum(l,r)
```

### Step-by-Step Working

Original:

```text
a = [4, 2, 3, 1]
```

After mutation:

```text
a = [4, 6, 9, 10]
```

Query:

```text
sum(1,3)
```

Answer:

```text
a[3] - a[0]
= 10 - 4
= 6
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long rangeSumInPlacePrefix(const vector<long long>& pref, int l, int r) {
    if (l == 0) return pref[r];

    return pref[r] - pref[l - 1];
}

int main() {
    vector<long long> a = {4, 2, 3, 1};

    for (int i = 1; i < a.size(); i++) {
        a[i] += a[i - 1];
    }

    cout << rangeSumInPlacePrefix(a, 1, 3) << endl;

    return 0;
}
```

---

## 14. Problem Form 4 — Prefix For Large Array

### Problem

Array values can be large.

Need in-place prefix safely.

### Key Point

Use:

```cpp
long long
```

not:

```cpp
int
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<long long> a = {
        1000000000LL,
        1000000000LL,
        1000000000LL
    };

    for (int i = 1; i < a.size(); i++) {
        a[i] += a[i - 1];
    }

    for (long long x : a) {
        cout << x << " ";
    }

    return 0;
}
```

Output:

```text
1000000000 2000000000 3000000000
```

---

## 15. Problem Form 5 — Streaming Style Running Total

### Problem

Values arrive one by one.

Need running total.

### Pattern

```text
single running variable
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> stream = {10, 20, -5, 15};

    long long running = 0;

    for (int x : stream) {
        running += x;

        cout << "running total = " << running << "\n";
    }

    return 0;
}
```

This is not exactly array mutation, but it is the same prefix idea.

---

## 16. Real World Model 1 — Memory-Constrained Aggregation

Imagine a service processing a large batch of metrics:

```text
[minute0, minute1, minute2, ...]
```

If the original raw values are not needed after transformation, the service can overwrite each value with cumulative total.

This saves memory.

Trade-off:

```text
less memory
but original data lost
```

---

## 17. Real World Model 2 — Log Batch Transformation

Suppose logs are grouped by minute:

```text
logsPerMinute = [5, 10, 3, 7]
```

In-place prefix transforms it into:

```text
cumulativeLogs = [5, 15, 18, 25]
```

Then:

```text
logs up to minute 2 = 18
```

This is useful for cumulative reporting.

---

## 18. Real World Model 3 — Financial Running Balance

Transactions:

```text
+100, -30, +50, -10
```

In-place transformation:

```text
100, 70, 120, 110
```

Now each index stores:

```text
account balance after that transaction
```

This is exactly running balance.

---

## 19. Real World Model 4 — Metrics Rollup Mutation

In monitoring systems, raw per-minute counters may be converted into cumulative counters for faster range calculations.

Example:

```text
requests: [20, 30, 40]
cumulative: [20, 50, 90]
```

This is conceptually in-place prefix if raw minute counters are overwritten.

---

## 20. Decision Tree

```text
Need prefix sum?
|
+-- Can input array be modified?
|   |
|   +-- No -> use separate pref[n+1]
|   |
|   +-- Yes
|       |
|       +-- Need original values later?
|           |
|           +-- Yes -> use separate pref[n+1]
|           |
|           +-- No -> in-place prefix is OK
|
+-- Need clean range query formula?
|   |
|   +-- Yes -> separate 1-indexed prefix is cleaner
|
+-- Need only running sum output?
    |
    +-- In-place prefix is ideal
```

---

## 21. Common Mistakes

### Mistake 1 — Forgetting That Original Array Is Destroyed

After:

```cpp
a[i] += a[i - 1];
```

`a[i]` no longer stores original value.

It stores cumulative sum.

---

### Mistake 2 — Using In-Place Prefix When Original Values Are Needed

Example:

```text
Need both original values and prefix values later.
```

Do not mutate.

Use separate prefix array.

---

### Mistake 3 — Wrong Range Query Formula

For in-place prefix:

```cpp
if (l == 0) ans = a[r];
else ans = a[r] - a[l - 1];
```

Do not use:

```cpp
a[r + 1] - a[l]
```

That formula is for 1-indexed separate prefix.

---

### Mistake 4 — Integer Overflow

Use:

```cpp
long long
```

if values are large.

---

### Mistake 5 — Modifying Input In Interview Without Saying It

In interviews, mention:

```text
This solution mutates the input.
If mutation is not allowed, I will build a separate prefix array.
```

---

## 22. Complexity

In-place prefix build:

```text
Time: O(n)
Extra Space: O(1)
```

Range query after in-place prefix:

```text
O(1)
```

---

## 23. Reusable C++ Templates

### Template 1 — In-Place Prefix Build

```cpp
void buildInPlacePrefix(vector<long long>& a) {
    for (int i = 1; i < a.size(); i++) {
        a[i] += a[i - 1];
    }
}
```

### Template 2 — Range Query On In-Place Prefix

```cpp
long long rangeSumInPlace(const vector<long long>& pref, int l, int r) {
    if (l == 0) return pref[r];

    return pref[r] - pref[l - 1];
}
```

### Template 3 — Safe Wrapper

```cpp
class InPlacePrefix {
private:
    vector<long long> pref;

public:
    InPlacePrefix(vector<long long> a) {
        pref = move(a);

        for (int i = 1; i < pref.size(); i++) {
            pref[i] += pref[i - 1];
        }
    }

    long long query(int l, int r) {
        if (l == 0) return pref[r];

        return pref[r] - pref[l - 1];
    }
};
```

---

## 24. CP / FAANG Problem Forms

---

### Problem 1 — Running Sum Of Array

#### Recognition

```text
return cumulative sum array
```

#### Pattern

```text
in-place prefix
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

### Problem 2 — Mutate Array To Prefix

#### Recognition

```text
convert array into cumulative totals
```

#### Pattern

```text
in-place mutation
```

#### Commented C++ Code

```cpp
for (int i = 1; i < n; i++) {
    a[i] += a[i - 1];
}
```

---

### Problem 3 — Range Query After Mutation

#### Recognition

```text
array already stores cumulative values
```

#### Pattern

```text
0-indexed prefix query
```

#### Commented C++ Code

```cpp
long long query(vector<long long>& pref, int l, int r) {
    if (l == 0) return pref[r];

    return pref[r] - pref[l - 1];
}
```

---

### Problem 4 — Memory Optimization Interview Discussion

#### Recognition

```text
Can we reduce extra space?
```

#### Answer

```text
Yes, if mutation is allowed.
Convert nums into prefix in-place.
Extra space becomes O(1).
```

---

### Problem 5 — Running Balance

#### Recognition

```text
transaction stream becomes balance after each transaction
```

#### Pattern

```text
prefix sum / running total
```

---

## 25. Practice Checklist

Before using in-place prefix, ask:

```text
1. Am I allowed to mutate input?
2. Do I need original values later?
3. Is O(1) extra space useful here?
4. Am I using the correct query formula?
5. Did I handle l = 0?
6. Are values large enough for long long?
7. Should I mention mutation trade-off in interview?
8. Is separate 1-indexed prefix cleaner?
```

---

## 26. Next Step

```text
005_2D_Prefix_Sum.md
```

That file extends prefix sum from:

```text
1D range sum
```

to:

```text
2D rectangle sum
```

using inclusion-exclusion on grids.
