# 024_GP_On_Difference_Array.md — MiniPrefixSumDifferenceEngine

# GP On Difference Array

> GP On Difference Array means:
>
> ```text
> applying geometric progression updates efficiently
> using difference transformation mathematics
> ```
>
> Instead of:
>
> ```text
> updating powers one by one
> ```
>
> we convert:
>
> ```text
> multiplicative growth
> ```
>
> into:
>
> ```text
> prefix/difference boundary operations
> ```
>
> This is one of the most advanced prefix transformation techniques.

---

# Clickable Index

1. [What Is GP On Difference Array?](#1-what-is-gp-on-difference-array)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Mental Model](#3-core-mental-model)
4. [Difference Array Refresher](#4-difference-array-refresher)
5. [Geometric Progression Refresher](#5-geometric-progression-refresher)
6. [Why GP Updates Are Hard](#6-why-gp-updates-are-hard)
7. [GP Range Update Goal](#7-gp-range-update-goal)
8. [Multiplicative Difference Insight](#8-multiplicative-difference-insight)
9. [Ratio Transformation](#9-ratio-transformation)
10. [Boundary Compression](#10-boundary-compression)
11. [Step-by-Step Dry Run — GP Update](#11-step-by-step-dry-run--gp-update)
12. [Step-by-Step Dry Run — Multiple GP Updates](#12-step-by-step-dry-run--multiple-gp-updates)
13. [Problem Form 1 — Single GP Range Update](#13-problem-form-1--single-gp-range-update)
14. [Problem Form 2 — Multiple GP Updates](#14-problem-form-2--multiple-gp-updates)
15. [Problem Form 3 — Ratio Difference Array](#15-problem-form-3--ratio-difference-array)
16. [Problem Form 4 — GP Contribution Queries](#16-problem-form-4--gp-contribution-queries)
17. [Problem Form 5 — Power Prefix Reconstruction](#17-problem-form-5--power-prefix-reconstruction)
18. [Real World Model 1 — Exponential Traffic Growth](#18-real-world-model-1--exponential-traffic-growth)
19. [Real World Model 2 — Viral Content Spread](#19-real-world-model-2--viral-content-spread)
20. [Real World Model 3 — Distributed Cache Replication](#20-real-world-model-3--distributed-cache-replication)
21. [Real World Model 4 — Financial Compound Growth](#21-real-world-model-4--financial-compound-growth)
22. [Decision Tree](#22-decision-tree)
23. [Common Mistakes](#23-common-mistakes)
24. [Complexity](#24-complexity)
25. [Reusable C++ Templates](#25-reusable-c-templates)
26. [CP / FAANG Problem Forms](#26-cp--faang-problem-forms)
27. [Practice Checklist](#27-practice-checklist)
28. [Next Step](#28-next-step)

---

# 1. What Is GP On Difference Array?

Normal difference array supports:

```text
constant updates
```

AP difference extension supports:

```text
linearly increasing updates
```

GP on difference array supports:

```text
1,2,4,8,16...
```

or:

```text
a, ar, ar²...
```

range updates efficiently.

---

# 2. Why This Topic Matters

This appears in:

```text
advanced math CP
power contribution problems
rolling-hash style transformations
probabilistic growth modeling
financial simulations
distributed scaling analytics
```

This combines:

```text
prefix sums
+
difference arrays
+
modular exponentiation
+
GP algebra
```

---

# 3. Core Mental Model

AP updates rely on:

```text
additive difference
```

GP updates rely on:

```text
multiplicative ratio
```

Instead of:

```text
constant delta
```

we work with:

```text
constant ratio
```

---

# 4. Difference Array Refresher

Difference array:

```text
diff[i] = a[i] - a[i-1]
```

Rebuild:

```text
prefix sum
```

This works naturally for additive behavior.

---

# 5. Geometric Progression Refresher

GP:

```text
a, ar, ar², ar³...
```

Example:

```text
1,2,4,8,16
```

Ratio:

```text
2
```

GP sum:

```math
1 + r + r^2 + \dots + r^n
=
\frac{r^{n+1}-1}{r-1}
```

---

# 6. Why GP Updates Are Hard

In AP:

```text
difference becomes constant
```

But in GP:

```text
ratio becomes constant
```

Additive difference arrays alone are not enough.

Need multiplicative thinking.

---

# 7. GP Range Update Goal

Suppose:

```text
add:
1,2,4,8
```

on range `[2..5]`

Result:

```text
0 0 1 2 4 8 0 0
```

Naive:

```text
O(length)
```

Goal:

```text
compress update behavior
```

---

# 8. Multiplicative Difference Insight

Observe GP:

```text
1,2,4,8
```

Ratios:

```text
2,2,2
```

Key insight:

```text
multiplicative relation becomes stable
```

similar to AP additive difference.

---

# 9. Ratio Transformation

Instead of storing:

```text
difference
```

we can think about:

```text
ratio transformation
```

This leads to:

```text
power-based reconstruction
```

and modular inverse usage.

---

# 10. Boundary Compression

For GP updates:

```text
a, ar, ar²...
```

We try to store:

```text
start contribution
ending cancellation
ratio propagation
```

This reduces repeated operations.

---

# 11. Step-by-Step Dry Run — GP Update

Initial:

```text
0 0 0 0 0 0
```

Add GP:

```text
1 2 4 8
```

on `[1..4]`

Result:

```text
0 1 2 4 8 0
```

Observe ratios:

```text
2 2 2
```

The multiplicative structure is preserved.

---

# 12. Step-by-Step Dry Run — Multiple GP Updates

Updates:

```text
[1..3] += 1,2,4
[2..5] += 3,6,12,24
```

Instead of applying each individually:

```text
store compressed GP behavior
```

Then reconstruct later.

---

# 13. Problem Form 1 — Single GP Range Update

## Problem

Apply:

```text
1,2,4,8...
```

on range.

---

## Pattern Recognition

Use:

```text
power propagation
```

---

## Step-by-Step Working

```text
value = 1

for each index:
    add value
    value *= ratio
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void applyGP(
    vector<long long>& a,
    int l,
    int r,
    long long ratio
) {
    long long value = 1;

    for (int i = l; i <= r; i++) {
        a[i] += value;

        value *= ratio;
    }
}

int main() {
    vector<long long> a(8, 0);

    applyGP(a, 2, 5, 2);

    for (long long x : a) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 14. Problem Form 2 — Multiple GP Updates

## Problem

Handle many GP updates efficiently.

---

## Pattern Recognition

Use:

```text
offline reconstruction
power precompute
compressed propagation
```

---

## Step-by-Step Working

Store:

```text
GP start
ratio
ending cancellation
```

Rebuild later.

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n = 10;

    vector<long long> a(n, 0);

    // GP update:
    // 1,2,4,8 on [2..5]

    long long value = 1;

    for (int i = 2; i <= 5; i++) {
        a[i] += value;

        value *= 2;
    }

    for (long long x : a) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 15. Problem Form 3 — Ratio Difference Array

## Problem

Represent multiplicative progression compactly.

---

## Pattern Recognition

AP uses:

```text
difference
```

GP uses:

```text
ratio
```

---

## Step-by-Step Working

Store:

```text
a[i] / a[i-1]
```

instead of:

```text
a[i] - a[i-1]
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<double> ratioArray(
    vector<double>& a
) {
    int n = a.size();

    vector<double> ratio(n);

    ratio[0] = a[0];

    for (int i = 1; i < n; i++) {
        ratio[i] =
            a[i] / a[i - 1];
    }

    return ratio;
}
```

---

# 16. Problem Form 4 — GP Contribution Queries

## Problem

Compute GP contribution efficiently.

---

## Pattern Recognition

Use:

```text
GP sum formulas
```

---

## Problem Simulation

Contribution:

```text
1 + 2 + 4 + 8
```

Sum:

```math
1+2+4+8
=
15
```

Formula:

```math
2^4 - 1
=
15
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long gpSum(
    long long ratio,
    long long n
) {
    if (ratio == 1)
        return n;

    return
        (pow(ratio, n) - 1)
        / (ratio - 1);
}

int main() {
    cout << gpSum(2, 4) << "\n";

    return 0;
}
```

---

# 17. Problem Form 5 — Power Prefix Reconstruction

## Problem

Recover GP-based transformed array.

---

## Pattern Recognition

Use:

```text
power propagation
prefix multiplication
```

---

## Step-by-Step Working

Rebuild:

```text
current *= ratio
```

while traversing.

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<long long> rebuildGP(
    long long start,
    long long ratio,
    int n
) {
    vector<long long> result(n);

    long long value = start;

    for (int i = 0; i < n; i++) {
        result[i] = value;

        value *= ratio;
    }

    return result;
}
```

---

# 18. Real World Model 1 — Exponential Traffic Growth

## Scenario

Traffic doubles every interval.

---

## Problem Simulation

Traffic:

```text
100,200,400,800
```

GP ratio:

```text
2
```

---

## System Mapping

Used in:

```text
traffic forecasting
viral growth simulation
capacity planning
autoscaling prediction
```

---

## Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

void trafficGrowth(
    vector<long long>& load
) {
    long long value = 100;

    for (int i = 0; i < load.size(); i++) {
        load[i] += value;

        value *= 2;
    }
}
```

---

# 19. Real World Model 2 — Viral Content Spread

## Scenario

Shares multiply rapidly.

---

## Problem Simulation

Shares:

```text
10,20,40,80
```

---

## System Mapping

Used in:

```text
social-media analytics
viral modeling
engagement prediction
growth simulation
```

---

## Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

void viralSpread(
    vector<long long>& shares
) {
    long long value = 10;

    for (int i = 0; i < shares.size(); i++) {
        shares[i] += value;

        value *= 2;
    }
}
```

---

# 20. Real World Model 3 — Distributed Cache Replication

## Scenario

Cache replication fans out exponentially.

---

## Problem Simulation

Replicas:

```text
1,2,4,8
```

---

## System Mapping

Used in:

```text
distributed systems
replication analytics
fan-out modeling
broadcast simulation
```

---

## Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

void replication(
    vector<long long>& replicas
) {
    long long count = 1;

    for (int i = 0; i < replicas.size(); i++) {
        replicas[i] += count;

        count *= 2;
    }
}
```

---

# 21. Real World Model 4 — Financial Compound Growth

## Scenario

Investment compounds yearly.

---

## Problem Simulation

Money:

```text
1000
1200
1440
1728
```

Ratio:

```text
1.2
```

---

## System Mapping

Used in:

```text
compound interest
financial forecasting
portfolio analytics
investment simulation
```

---

## Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

void compoundGrowth(
    vector<double>& money
) {
    double value = 1000;

    for (int i = 0; i < money.size(); i++) {
        money[i] += value;

        value *= 1.2;
    }
}
```

---

# 22. Decision Tree

```text
Need range updates?
|
+-- Constant growth?
|   |
|   +-- normal difference array
|
+-- Linear growth?
|   |
|   +-- AP difference techniques
|
+-- Multiplicative growth?
|   |
|   +-- GP transformations
|
+-- Power contribution?
|   |
|   +-- GP weighted prefix
|
+-- String hashing?
    |
    +-- rolling hash
```

---

# 23. Common Mistakes

## Mistake 1 — Using Additive Thinking

GP is multiplicative.

---

## Mistake 2 — Overflow

Powers grow very fast.

Use:

```cpp
long long
```

or modulo.

---

## Mistake 3 — Forgetting Power Precompute

Repeated exponentiation is expensive.

---

## Mistake 4 — No Modulo Handling

Rolling hash requires modulo.

---

## Mistake 5 — Confusing AP And GP

AP:

```text
difference constant
```

GP:

```text
ratio constant
```

---

# 24. Complexity

Naive GP updates:

```text
O(length)
```

Optimized offline approaches:

```text
O(1) update metadata
O(N) rebuild
```

---

# 25. Reusable C++ Templates

## Template 1 — GP Update

```cpp
void applyGP(
    vector<long long>& a,
    int l,
    int r,
    long long ratio
) {
    long long value = 1;

    for (int i = l; i <= r; i++) {
        a[i] += value;

        value *= ratio;
    }
}
```

---

## Template 2 — GP Sum

```cpp
long long gpSum(
    long long ratio,
    long long n
) {
    if (ratio == 1)
        return n;

    return
        (pow(ratio, n) - 1)
        / (ratio - 1);
}
```

---

## Template 3 — Power Build

```cpp
vector<long long> buildPower(
    int n,
    long long ratio
) {
    vector<long long> power(n);

    power[0] = 1;

    for (int i = 1; i < n; i++) {
        power[i] =
            power[i - 1] * ratio;
    }

    return power;
}
```

---

## Template 4 — GP Reconstruction

```cpp
vector<long long> rebuildGP(
    long long start,
    long long ratio,
    int n
) {
    vector<long long> result(n);

    long long value = start;

    for (int i = 0; i < n; i++) {
        result[i] = value;

        value *= ratio;
    }

    return result;
}
```

---

# 26. CP / FAANG Problem Forms

---

## Problem 1 — GP Range Update

### Recognition

```text
range update with powers
```

### Pattern

```text
GP propagation
```

### Step-by-Step Working

```text
multiply by ratio each step
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

        value *= 2;
    }
}
```

---

## Problem 2 — GP Contribution

### Recognition

```text
power-based contribution
```

### Pattern

```text
GP sum
```

### Commented C++ Code

```cpp
long long gp(long long n) {
    return (1LL << n) - 1;
}
```

---

## Problem 3 — Rolling Hash

### Recognition

```text
string hashing
```

### Pattern

```text
GP weighted prefix
```

### Commented C++ Code

```cpp
long long hashString(string s) {
    long long hashValue = 0;
    long long power = 1;

    for (char ch : s) {
        hashValue +=
            (ch - 'a' + 1) * power;

        power *= 31;
    }

    return hashValue;
}
```

---

## Problem 4 — Power Reconstruction

### Recognition

```text
recover powers
```

### Pattern

```text
multiplicative prefix
```

### Commented C++ Code

```cpp
vector<long long> build(
    int n,
    long long ratio
) {
    vector<long long> a(n);

    a[0] = 1;

    for (int i = 1; i < n; i++) {
        a[i] =
            a[i - 1] * ratio;
    }

    return a;
}
```

---

## Problem 5 — Compound Growth

### Recognition

```text
exponential scaling
```

### Pattern

```text
GP growth
```

### Commented C++ Code

```cpp
double compound(
    double start,
    double ratio,
    int years
) {
    double value = start;

    for (int i = 0; i < years; i++) {
        value *= ratio;
    }

    return value;
}
```

---

# 27. Practice Checklist

Before solving:

```text
1. Is growth multiplicative?
2. Is GP behavior present?
3. Are powers reused repeatedly?
4. Should powers be precomputed?
5. Is modulo needed?
6. Is rolling hash involved?
7. Is power overflow possible?
8. Is ratio constant?
9. Is reconstruction multiplicative?
10. Can GP formulas simplify computation?
```

---

# 28. Next Step

```text
025_2D_Prefix_Grid_Problems.md
```

Next file covers:

```text
advanced 2D prefix patterns
matrix contribution
rectangle aggregation
submatrix transformations
grid optimization techniques
