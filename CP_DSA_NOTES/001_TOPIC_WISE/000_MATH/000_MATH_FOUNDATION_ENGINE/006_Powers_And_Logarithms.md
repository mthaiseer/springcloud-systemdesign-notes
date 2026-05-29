# 006_Powers_And_Logarithms.md

# Powers And Logarithms For Competitive Programming

---

# 1. Introduction

Powers and logarithms are fundamental in CP.

They appear everywhere:
- binary search
- divide and conquer
- trees
- segment tree
- sparse table
- bitwise
- graph
- complexity analysis

Understanding them deeply helps recognize:
- exponential growth
- logarithmic optimization
- binary decomposition

---

# 2. What Is A Power?

Power means repeated multiplication.

Example:

```text
2^4 = 2 × 2 × 2 × 2 = 16
```

General form:

```text
a^b
```

where:
- a = base
- b = exponent

---

# 3. Growth Comparison

Important CP intuition:

| Complexity | Growth |
|---|---|
| O(1) | constant |
| O(log N) | slow growth |
| O(N) | linear |
| O(N log N) | sorting |
| O(N²) | quadratic |
| O(2^N) | exponential |

---

# 4. Why Exponential Is Dangerous

Example:

```text
2^10 = 1024
2^20 ≈ 10^6
2^30 ≈ 10^9
```

Observation:

```text
Exponential growth explodes quickly.
```

This is why brute force subset generation becomes impossible for large N.

---

# 5. Binary Representation And Powers

Binary powers:

```text
2^0 = 1
2^1 = 2
2^2 = 4
2^3 = 8
2^4 = 16
```

Important because:

```text
Binary system is based on powers of 2.
```

---

# 6. What Is Logarithm?

Logarithm answers:

```text
How many times can we divide?
```

Example:

```text
log₂(8) = 3
```

because:

```text
8 → 4 → 2 → 1
```

3 divisions by 2.

---

# 7. Most Important CP Log Intuition

```text
log₂(N)
```

means:

```text
How many binary splits are possible?
```

This appears in:
- binary search
- balanced trees
- heaps
- segment tree
- sparse table

---

# 8. Binary Search Complexity

Binary search repeatedly halves search space.

Example:

```text
N = 16
```

Steps:

```text
16 → 8 → 4 → 2 → 1
```

Total:

```text
log₂(16) = 4
```

Complexity:

```text
O(log N)
```

---

# 9. Divide And Conquer

Many algorithms repeatedly divide problem.

Examples:
- merge sort
- quick sort
- FFT
- binary lifting

Core idea:

```text
divide by 2 repeatedly
```

Thus:

```text
logarithmic depth
```

---

# 10. Power Of Two Recognition

Very common CP pattern.

Numbers:

```text
1 2 4 8 16 32
```

Recognition signals:
- binary splitting
- complete tree levels
- subset count
- bitmasking

---

# 11. Check Power Of Two

Observation:

```text
Power of two has exactly one set bit.
```

Formula:

```text
n & (n - 1) == 0
```

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isPowerOfTwo(long long n) {

    if (n <= 0) return false;

    return (n & (n - 1)) == 0;
}
```

---

# 12. Fast Power / Binary Exponentiation

Naive:

```text
a × a × a × ... b times
```

Complexity:

```text
O(b)
```

---

# Optimization

Use binary representation of exponent.

Complexity:

```text
O(log b)
```

---

# Step-by-Step Example

Compute:

```text
2^13
```

Binary of 13:

```text
1101
```

Meaning:

```text
2^13
=
2^8 × 2^4 × 2^1
```

---

# Binary Exponentiation Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long power(long long a, long long b) {

    long long result = 1;

    while (b > 0) {

        if (b & 1) {
            result *= a;
        }

        a *= a;

        b >>= 1;
    }

    return result;
}
```

Complexity:

```text
O(log b)
```

---

# 13. Logarithm In Trees

Balanced binary tree height:

```text
O(log N)
```

because every level doubles nodes.

Example:

```text
Level 0 → 1 node
Level 1 → 2 nodes
Level 2 → 4 nodes
Level 3 → 8 nodes
```

Total nodes after h levels:

```text
2^h
```

Thus:

```text
h ≈ log₂(N)
```

---

# 14. Subset Enumeration

Number of subsets of n elements:

```text
2^n
```

Reason:

```text
Every element:
take or not take
```

2 choices per element.

---

# 15. Logarithm And Bit Length

Number of bits in integer:

```text
floor(log₂(n)) + 1
```

Example:

```text
13 = 1101
```

4 bits.

---

# 16. CP / FAANG Problem Forms

---

# Form 1 — Fast Power

## Problem

Compute:

```text
a^b
```

efficiently.

---

## Observation

Exponent decomposes into binary powers.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long fastPower(long long a, long long b) {

    long long ans = 1;

    while (b > 0) {

        if (b & 1) {
            ans *= a;
        }

        a *= a;

        b >>= 1;
    }

    return ans;
}
```

---

# Form 2 — Power Of Two

## Problem

Check if number is power of 2.

---

## Observation

Power of two contains exactly one set bit.

---

## Step-by-Step Working

Example:

```text
8 = 1000
7 = 0111
```

AND:

```text
1000
0111
----
0000
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isPowerTwo(long long n) {

    return n > 0
        && (n & (n - 1)) == 0;
}
```

---

# Form 3 — Binary Search Complexity

## Problem

How many steps binary search needs?

---

## Observation

Every step halves search space.

---

## Example

```text
N = 32

32 → 16 → 8 → 4 → 2 → 1
```

Total:

```text
5 steps
```

because:

```text
log₂(32) = 5
```

---

# Form 4 — Number Of Subsets

## Problem

Find total subsets of n elements.

---

## Observation

Each element:
- take
- not take

2 choices each.

Total:

```text
2^n
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long subsetCount(int n) {

    return 1LL << n;
}
```

---

# Form 5 — Tree Height

## Problem

Height of balanced binary tree with N nodes.

---

## Observation

Every level doubles nodes.

Height:

```text
log₂(N)
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int treeHeight(int n) {

    return log2(n);
}
```

---

# 17. Real World Applications

| Real System | Usage |
|---|---|
| Google Search | logarithmic indexing |
| Databases | B-tree height |
| Compression | binary encoding |
| Cryptography | modular exponentiation |
| Segment tree | logarithmic queries |
| Networking | binary routing |
| Distributed systems | consistent hashing |

---

# 18. Real Engineering Insight

Logarithms usually indicate:

```text
Repeated halving
```

Powers usually indicate:

```text
Exponential growth
```

This distinction is extremely important in:
- scalability
- algorithm design
- distributed systems

---

# 19. Decision Tree

```text
Repeated multiplication?
→ powers

Repeated division by 2?
→ logarithm

Need fast exponent?
→ binary exponentiation

Need binary split?
→ log complexity

Subset states?
→ 2^N

Tree height?
→ log N
```

---

# 20. Common Traps

```text
1. Overflow in powers
2. Using pow() with integers
3. Floating precision issues
4. Forgetting logarithm base
5. Exponential brute force explosion
6. Wrong binary exponentiation
7. Infinite loops with shifts
8. Assuming log base irrelevant everywhere
```

---

# 21. Final Checklist

Before solving:

```text
1. Is growth exponential?
2. Can repeated halving optimize?
3. Is binary search possible?
4. Is divide-and-conquer present?
5. Can exponent decompose into bits?
6. Is complexity logarithmic?
7. Is subset count involved?
8. Is tree height logarithmic?
```

---

# 22. Final Mental Shortcut

```text
Powers
=
Repeated Multiplication

Logarithms
=
Repeated Division
+
Binary Splitting
```
