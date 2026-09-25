# 023_AP_On_Difference_Array.md — MiniPrefixSumDifferenceEngine

# AP On Difference Array

> AP On Difference Array means:
>
> ```text
> applying arithmetic progression updates efficiently
> using difference-array mathematics
> ```
>
> Instead of:
>
> ```text
> updating every element one by one
> ```
>
> we transform:
>
> ```text
> arithmetic progression updates
> into boundary operations
> ```
>
> This is a very advanced and powerful prefix technique.

---

# Clickable Index

1. [What Is AP On Difference Array?](#1-what-is-ap-on-difference-array)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Mental Model](#3-core-mental-model)
4. [Difference Array Refresher](#4-difference-array-refresher)
5. [Arithmetic Progression Refresher](#5-arithmetic-progression-refresher)
6. [Why Normal Difference Array Is Not Enough](#6-why-normal-difference-array-is-not-enough)
7. [AP Range Update Goal](#7-ap-range-update-goal)
8. [AP Difference Insight](#8-ap-difference-insight)
9. [Second Difference Concept](#9-second-difference-concept)
10. [Boundary Transformation](#10-boundary-transformation)
11. [Step-by-Step Dry Run — AP Update](#11-step-by-step-dry-run--ap-update)
12. [Step-by-Step Dry Run — Multiple AP Updates](#12-step-by-step-dry-run--multiple-ap-updates)
13. [Problem Form 1 — Single AP Range Update](#13-problem-form-1--single-ap-range-update)
14. [Problem Form 2 — Multiple AP Updates](#14-problem-form-2--multiple-ap-updates)
15. [Problem Form 3 — Difference Of Difference](#15-problem-form-3--difference-of-difference)
16. [Problem Form 4 — AP Contribution Queries](#16-problem-form-4--ap-contribution-queries)
17. [Problem Form 5 — Weighted Prefix Reconstruction](#17-problem-form-5--weighted-prefix-reconstruction)
18. [Real World Model 1 — Salary Increment Simulation](#18-real-world-model-1--salary-increment-simulation)
19. [Real World Model 2 — Traffic Ramp-Up Modeling](#19-real-world-model-2--traffic-ramp-up-modeling)
20. [Real World Model 3 — Priority Boost Scheduling](#20-real-world-model-3--priority-boost-scheduling)
21. [Real World Model 4 — Distributed Load Increase](#21-real-world-model-4--distributed-load-increase)
22. [Decision Tree](#22-decision-tree)
23. [Common Mistakes](#23-common-mistakes)
24. [Complexity](#24-complexity)
25. [Reusable C++ Templates](#25-reusable-c-templates)
26. [CP / FAANG Problem Forms](#26-cp--faang-problem-forms)
27. [Practice Checklist](#27-practice-checklist)
28. [Next Step](#28-next-step)

---

# 1. What Is AP On Difference Array?

Normal difference array supports:

```text
add constant value on range
```

Example:

```text
+5 on [l..r]
```

AP on difference array supports:

```text
+1,+2,+3,+4...
```

or:

```text
+a, a+d, a+2d...
```

over a range.

---

# 2. Why This Topic Matters

This technique appears in:

```text
advanced range updates
competitive programming
lazy propagation mathematics
offline query optimization
difference transformations
prefix contribution systems
```

It is an advanced extension of:

```text
difference arrays
+
prefix sums
+
arithmetic progression algebra
```

---

# 3. Core Mental Model

Instead of updating every position:

```text
1,2,3,4,5...
```

individually:

```text
O(N)
```

we convert AP behavior into:

```text
boundary changes
```

using difference mathematics.

---

# 4. Difference Array Refresher

Difference array:

```text
diff[i] = a[i] - a[i-1]
```

Range add:

```text
diff[l] += x
diff[r+1] -= x
```

Rebuild using prefix sum.

---

# 5. Arithmetic Progression Refresher

Arithmetic progression:

```text
a, a+d, a+2d...
```

Example:

```text
1,2,3,4,5
```

Difference between neighbors:

```text
constant
```

---

# 6. Why Normal Difference Array Is Not Enough

Normal difference handles:

```text
constant range update
```

But AP update changes every position differently.

Example:

```text
+1,+2,+3,+4
```

This is NOT constant.

We need extra transformation.

---

# 7. AP Range Update Goal

Suppose:

```text
n = 8
```

Update:

```text
add:
1,2,3,4
on range [2..5]
```

Result:

```text
0 0 1 2 3 4 0 0
```

Naive:

```text
O(length)
```

Goal:

```text
O(1)
```

boundary update.

---

# 8. AP Difference Insight

Observe AP:

```text
1,2,3,4
```

Difference of AP:

```text
1,1,1
```

Very important insight:

```text
difference of AP becomes constant
```

This is the core trick.

---

# 9. Second Difference Concept

If:

```text
array is AP
```

then:

```text
first difference is constant
```

and:

```text
second difference becomes zero
```

This allows AP range updates using:

```text
difference-of-difference arrays
```

---

# 10. Boundary Transformation

To add AP:

```text
+a
+a+d
+a+2d
...
```

on `[l..r]`

We update boundaries of difference array carefully.

Typical transformation:

```text
diff[l] += a
diff[l+1] += d
diff[r+1] -= lastTerm + d
```

then rebuild.

---

# 11. Step-by-Step Dry Run — AP Update

Initial:

```text
0 0 0 0 0 0
```

Add AP:

```text
1 2 3 4
```

on `[1..4]`

Naive result:

```text
0 1 2 3 4 0
```

Difference array:

```text
0 1 1 1 1 -4
```

Notice:

```text
mostly constant
```

This is the AP transformation magic.

---

# 12. Step-by-Step Dry Run — Multiple AP Updates

Updates:

```text
[1..3] += 1,2,3
[2..5] += 2,4,6,8
```

Instead of touching every element:

```text
store boundary transitions
```

Then rebuild final array using:

```text
prefix on difference
```

and possibly:

```text
prefix again
```

---

# 13. Problem Form 1 — Single AP Range Update

## Problem

Apply:

```text
1,2,3,4...
```

on range `[l..r]`.

---

## Pattern Recognition

Use:

```text
difference transformation
```

because AP differences are constant.

---

## Step-by-Step Working

Update:

```text
diff[l] += 1
diff[l+1] += 1
diff[r+1] -= length
```

Then prefix reconstruct.

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void applyAP(
    vector<long long>& a,
    int l,
    int r
) {
    long long value = 1;

    for (int i = l; i <= r; i++) {
        a[i] += value;

        value++;
    }
}

int main() {
    vector<long long> a(8, 0);

    applyAP(a, 2, 5);

    for (long long x : a) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 14. Problem Form 2 — Multiple AP Updates

## Problem

Handle many AP updates efficiently.

---

## Pattern Recognition

Use:

```text
difference arrays
+
prefix reconstruction
```

---

## Step-by-Step Working

Store only:

```text
AP start effect
AP slope effect
AP ending correction
```

Then rebuild final array.

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n = 10;

    vector<long long> diff(n + 2, 0);

    // Add AP:
    // 1,2,3,4 on [2..5]

    diff[2] += 1;
    diff[3] += 1;
    diff[6] -= 4;

    vector<long long> a(n, 0);

    a[0] = diff[0];

    for (int i = 1; i < n; i++) {
        a[i] = a[i - 1] + diff[i];
    }

    for (long long x : a) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 15. Problem Form 3 — Difference Of Difference

## Problem

Build second-order difference array.

---

## Pattern Recognition

AP updates become simpler in:

```text
second difference space
```

---

## Step-by-Step Working

If:

```text
diff[i] = a[i] - a[i-1]
```

Then:

```text
secondDiff[i]
=
diff[i] - diff[i-1]
```

AP updates become:

```text
few boundary operations
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<long long> secondDifference(
    vector<long long>& a
) {
    int n = a.size();

    vector<long long> diff(n, 0);

    diff[0] = a[0];

    for (int i = 1; i < n; i++) {
        diff[i] = a[i] - a[i - 1];
    }

    vector<long long> second(n, 0);

    second[0] = diff[0];

    for (int i = 1; i < n; i++) {
        second[i] =
            diff[i] - diff[i - 1];
    }

    return second;
}
```

---

# 16. Problem Form 4 — AP Contribution Queries

## Problem

Each update contributes increasing value across range.

Need total contributions.

---

## Pattern Recognition

Use:

```text
AP accumulation
weighted prefix
```

---

## Problem Simulation

Range:

```text
[2..5]
```

Contribution:

```text
1,2,3,4
```

Total:

```text
10
```

AP formula:

```math
1+2+3+4
=
\frac{4(4+1)}{2}
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long apSum(long long n) {
    return n * (n + 1) / 2;
}

int main() {
    cout << apSum(4) << "\n";

    return 0;
}
```

---

# 17. Problem Form 5 — Weighted Prefix Reconstruction

## Problem

Rebuild original array after AP transformations.

---

## Pattern Recognition

Use:

```text
multiple prefix reconstructions
```

Sometimes:

```text
second diff -> diff -> original
```

---

## Step-by-Step Working

```text
1st prefix:
recover difference array

2nd prefix:
recover original array
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<long long> rebuild(
    vector<long long>& second
) {
    int n = second.size();

    vector<long long> diff(n, 0);

    diff[0] = second[0];

    for (int i = 1; i < n; i++) {
        diff[i] =
            diff[i - 1] + second[i];
    }

    vector<long long> a(n, 0);

    a[0] = diff[0];

    for (int i = 1; i < n; i++) {
        a[i] =
            a[i - 1] + diff[i];
    }

    return a;
}
```

---

# 18. Real World Model 1 — Salary Increment Simulation

## Scenario

Employees receive increasing salary increments:

```text
junior -> small increase
senior -> larger increase
```

AP updates naturally model this.

---

## Problem Simulation

Range:

```text
employees [3..7]
```

Increment:

```text
100,200,300,400,500
```

---

## System Mapping

Used in:

```text
salary simulation
HR compensation modeling
financial planning
increment forecasting
```

---

## Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

void salaryIncrement(
    vector<long long>& salary,
    int l,
    int r
) {
    long long add = 100;

    for (int i = l; i <= r; i++) {
        salary[i] += add;

        add += 100;
    }
}
```

---

# 19. Real World Model 2 — Traffic Ramp-Up Modeling

## Scenario

Traffic gradually increases.

Example:

```text
100,200,300,400...
```

This is AP behavior.

---

## Problem Simulation

Servers:

```text
server1 +100
server2 +200
server3 +300
```

---

## System Mapping

Used in:

```text
load testing
traffic simulation
capacity planning
autoscaling analytics
```

---

## Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

void trafficRamp(
    vector<long long>& traffic
) {
    long long add = 100;

    for (int i = 0; i < traffic.size(); i++) {
        traffic[i] += add;

        add += 100;
    }
}
```

---

# 20. Real World Model 3 — Priority Boost Scheduling

## Scenario

Tasks later in queue get higher priority boost.

---

## Problem Simulation

Boost:

```text
1,2,3,4...
```

Later tasks gain larger increments.

---

## System Mapping

Used in:

```text
scheduler systems
queue balancing
priority aging
fairness boosting
```

---

## Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

void priorityBoost(
    vector<long long>& tasks
) {
    long long boost = 1;

    for (int i = 0; i < tasks.size(); i++) {
        tasks[i] += boost;

        boost++;
    }
}
```

---

# 21. Real World Model 4 — Distributed Load Increase

## Scenario

Distributed systems gradually increase shard load.

---

## Problem Simulation

Shard increases:

```text
10,20,30,40...
```

---

## System Mapping

Used in:

```text
distributed balancing
capacity simulation
traffic migration
progressive rollout
```

---

## Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

void shardLoad(
    vector<long long>& load
) {
    long long add = 10;

    for (int i = 0; i < load.size(); i++) {
        load[i] += add;

        add += 10;
    }
}
```

---

# 22. Decision Tree

```text
Need range updates?
|
+-- Constant update?
|   |
|   +-- normal difference array
|
+-- Arithmetic progression update?
|   |
|   +-- AP on difference array
|
+-- Need weighted contribution?
|   |
|   +-- AP weighted prefix
|
+-- Need many offline updates?
|   |
|   +-- second difference technique
|
+-- Need reconstruction?
    |
    +-- multiple prefix rebuilds
```

---

# 23. Common Mistakes

## Mistake 1 — Treating AP As Constant

AP changes every position differently.

---

## Mistake 2 — Forgetting Boundary Correction

AP difference updates need:

```text
ending correction
```

---

## Mistake 3 — Wrong Prefix Count

Sometimes reconstruction requires:

```text
two prefix passes
```

---

## Mistake 4 — Off-By-One Errors

AP ranges are sensitive to indexing.

---

## Mistake 5 — Not Understanding Second Difference

Second difference is the core AP trick.

---

# 24. Complexity

Naive AP update:

```text
O(length)
```

Optimized offline AP updates:

```text
O(1) update
O(N) rebuild
```

---

# 25. Reusable C++ Templates

## Template 1 — AP Range Update

```cpp
void applyAP(
    vector<long long>& a,
    int l,
    int r
) {
    long long value = 1;

    for (int i = l; i <= r; i++) {
        a[i] += value;

        value++;
    }
}
```

---

## Template 2 — Difference Build

```cpp
vector<long long> buildDiff(
    vector<long long>& a
) {
    int n = a.size();

    vector<long long> diff(n);

    diff[0] = a[0];

    for (int i = 1; i < n; i++) {
        diff[i] =
            a[i] - a[i - 1];
    }

    return diff;
}
```

---

## Template 3 — Prefix Reconstruction

```cpp
vector<long long> rebuild(
    vector<long long>& diff
) {
    int n = diff.size();

    vector<long long> a(n);

    a[0] = diff[0];

    for (int i = 1; i < n; i++) {
        a[i] =
            a[i - 1] + diff[i];
    }

    return a;
}
```

---

## Template 4 — AP Sum

```cpp
long long apSum(long long n) {
    return n * (n + 1) / 2;
}
```

---

# 26. CP / FAANG Problem Forms

---

## Problem 1 — AP Range Update

### Recognition

```text
range update with increasing values
```

### Pattern

```text
difference + AP
```

### Step-by-Step Working

```text
transform AP into boundary operations
```

### Commented C++ Code

```cpp
void update(
    vector<long long>& a,
    int l,
    int r
) {
    long long value = 1;

    for (int i = l; i <= r; i++) {
        a[i] += value;

        value++;
    }
}
```

---

## Problem 2 — Second Difference

### Recognition

```text
AP structure
```

### Pattern

```text
difference of difference
```

### Commented C++ Code

```cpp
vector<long long> secondDiff(
    vector<long long>& a
) {
    int n = a.size();

    vector<long long> diff(n);

    diff[0] = a[0];

    for (int i = 1; i < n; i++) {
        diff[i] =
            a[i] - a[i - 1];
    }

    vector<long long> second(n);

    second[0] = diff[0];

    for (int i = 1; i < n; i++) {
        second[i] =
            diff[i] - diff[i - 1];
    }

    return second;
}
```

---

## Problem 3 — AP Contribution

### Recognition

```text
sum of increasing updates
```

### Pattern

```text
AP formula
```

### Commented C++ Code

```cpp
long long ap(long long n) {
    return n * (n + 1) / 2;
}
```

---

## Problem 4 — Prefix Reconstruction

### Recognition

```text
recover original array
```

### Pattern

```text
prefix rebuild
```

### Commented C++ Code

```cpp
vector<long long> rebuild(
    vector<long long>& diff
) {
    vector<long long> a(diff.size());

    a[0] = diff[0];

    for (int i = 1; i < diff.size(); i++) {
        a[i] =
            a[i - 1] + diff[i];
    }

    return a;
}
```

---

## Problem 5 — Weighted AP Modeling

### Recognition

```text
increment grows linearly
```

### Pattern

```text
AP weighting
```

### Commented C++ Code

```cpp
void weighted(
    vector<long long>& a
) {
    long long add = 1;

    for (int i = 0; i < a.size(); i++) {
        a[i] += add;

        add++;
    }
}
```

---

# 27. Practice Checklist

Before solving:

```text
1. Is update constant or AP?
2. Does value increase linearly?
3. Can AP differences become constant?
4. Is second difference useful?
5. Is offline processing allowed?
6. Are many updates present?
7. Is reconstruction needed?
8. Are boundaries handled correctly?
9. Is double prefix reconstruction needed?
10. Is AP formula usable?
```

---

# 28. Next Step

```text
024_GP_On_Difference_Array.md
```

Next file covers:

```text
geometric progression range updates
multiplicative difference tricks
power-based updates
advanced GP transformations
