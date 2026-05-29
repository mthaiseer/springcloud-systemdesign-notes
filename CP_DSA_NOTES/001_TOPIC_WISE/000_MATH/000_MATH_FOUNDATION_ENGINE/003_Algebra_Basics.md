# 003_Algebra_Basics.md

# Algebra Basics For Competitive Programming

---

# 1. Why Algebra Matters

Algebra in CP is NOT about solving huge textbook equations.

It is mainly about:

```text
Simplification
Transformation
Optimization
Pattern Recognition
```

Strong algebra intuition helps:
- reduce complexity
- simplify formulas
- optimize DP states
- derive transitions
- avoid brute force

---

# 2. Where Algebra Appears

| Topic | Usage |
|---|---|
| DP | transition simplification |
| Greedy | proof/optimization |
| Binary Search | equation transformation |
| Geometry | formulas |
| Prefix Sum | AP/GP formulas |
| Number Theory | modular equations |
| Graph | path cost formulas |

---

# 3. Core Algebraic Properties

---

# A. Commutative Property

Addition:

```text
a + b = b + a
```

Multiplication:

```text
a * b = b * a
```

Useful in:
- rearrangements
- simplification
- proofs

---

# B. Associative Property

```text
(a + b) + c = a + (b + c)
```

```text
(a * b) * c = a * (b * c)
```

Used in:
- prefix sums
- segment trees
- accumulation

---

# C. Distributive Property

```text
a * (b + c) = ab + ac
```

Very important in:
- algebra simplification
- polynomial expansion
- DP transitions

---

# 4. Difference Of Squares

Formula:

```text
a² - b² = (a - b)(a + b)
```

Example:

```text
25 - 9
= (5 - 3)(5 + 3)
= 2 * 8
= 16
```

Used in:
- factorization
- constructive math
- optimization

---

# 5. Square Expansion

Formula:

```text
(a + b)² = a² + 2ab + b²
```

Example:

```text
(2 + 3)²
= 4 + 12 + 9
= 25
```

---

# 6. Arithmetic Progression (AP)

Sequence:

```text
2 4 6 8 10
```

Common difference:

```text
d = 2
```

---

# AP Sum Formula

```text
1 + 2 + 3 + ... + n = n(n + 1) / 2
```

---

# Example

Find:

```text
1 + 2 + 3 + 4 + 5
```

Using formula:

```text
5 * 6 / 2 = 15
```

---

# 7. Geometric Progression (GP)

Sequence:

```text
1 2 4 8 16
```

Common ratio:

```text
r = 2
```

Used in:
- logarithms
- divide and conquer
- binary lifting

---

# 8. Logarithm Intuition

```text
log₂(n)
```

means:

```text
How many times can we divide by 2?
```

Example:

```text
log₂(8) = 3
```

because:

```text
8 → 4 → 2 → 1
```

Used in:
- binary search
- trees
- sparse table
- segment tree

---

# 9. Equation Rearrangement

Very important CP skill.

Example:

Instead of:

```text
x + y = n
```

store only:

```text
x
```

because:

```text
y = n - x
```

This reduces dimensions in DP.

---

# 10. Constraint Simplification

Suppose:

```text
a + b + c = n
```

You only need:
- a
- b

because:

```text
c = n - a - b
```

Very useful in:
- DP
- brute force reduction
- combinatorics

---

# 11. Symmetry Observation

Example:

```text
distance(a,b) = distance(b,a)
```

Many problems contain symmetry.

Recognition:
```text
same answer after swapping
```

Used in:
- geometry
- graph
- counting
- DP

---

# 12. CP / FAANG Problem Forms

---

# Form 1 — Formula Optimization

## Problem

Find:

```text
1 + 2 + 3 + ... + n
```

---

## Brute Force

```cpp
long long sum = 0;

for (int i = 1; i <= n; i++) {
    sum += i;
}
```

Complexity:

```text
O(N)
```

---

## Observation

Arithmetic Progression.

Formula:

```text
n(n + 1) / 2
```

---

## Optimized Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long sumN(long long n) {

    return n * (n + 1) / 2;
}
```

Complexity:

```text
O(1)
```

---

# Form 2 — Constraint Reduction

## Problem

Count solutions:

```text
a + b + c = n
```

---

## Observation

If:
- a chosen
- b chosen

then:

```text
c = n - a - b
```

No need for third loop.

---

## Brute Force

```cpp
for (a)
  for (b)
    for (c)
```

Complexity:

```text
O(N³)
```

---

## Optimized

```cpp
for (a)
  for (b)
```

Compute:

```text
c = n - a - b
```

Complexity:

```text
O(N²)
```

---

# Form 3 — Binary Search Equation

## Problem

Find smallest x such that:

```text
x² >= n
```

---

## Observation

Monotonic equation.

Binary search possible.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long sqrtCeil(long long n) {

    long long low = 0;
    long long high = 1e9;

    while (low < high) {

        long long mid =
            low + (high - low) / 2;

        if (mid * mid >= n) {
            high = mid;
        } else {
            low = mid + 1;
        }
    }

    return low;
}
```

---

# 13. Real World Applications

| Real System | Algebra Usage |
|---|---|
| Google Maps | route cost formulas |
| Finance systems | interest calculations |
| Analytics systems | aggregations |
| Databases | query optimization |
| Distributed systems | load balancing formulas |
| ML systems | optimization equations |

---

# 14. Common Traps

```text
1. Integer overflow
2. Wrong operator precedence
3. Floating precision issues
4. Unnecessary variables
5. Missing simplification
6. Using brute force when formula exists
7. Off-by-one errors
8. Forgetting logarithmic growth
```

---

# 15. Decision Tree

```text
Repeated sum?
→ AP formula

Repeated multiplication?
→ GP/logarithm

Equation monotonic?
→ Binary search

Extra variables?
→ Reduce dimensions

Symmetry present?
→ Simplify states
```

---

# 16. Final Checklist

Before solving ask:

```text
1. Is there formula simplification?
2. Can dimensions reduce?
3. Is there symmetry?
4. Is equation monotonic?
5. Is logarithm involved?
6. Is there AP/GP pattern?
7. Can brute force reduce mathematically?
```

---

# 17. Final Mental Shortcut

```text
Algebra In CP
=
Simplification
+
Transformation
+
Optimization
```
