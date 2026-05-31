# 034_CheatSheet.md

# 000_MATH_FOUNDATION_ENGINE — FINAL CHEATSHEET

> Ultimate Last Minute Revision For CP + FAANG Interviews

---

# Table Of Contents

1. [Algebra Basics](#1-algebra-basics)
2. [Bounds Thinking](#2-bounds-thinking)
3. [Absolute Value](#3-absolute-value)
4. [Powers & Logarithms](#4-powers-and-logarithms)
5. [GCD / LCM](#5-gcd--lcm)
6. [Divisibility](#6-divisibility-rules)
7. [Floor / Ceil](#7-floor--ceil)
8. [Parity](#8-odd-even-parity)
9. [Invariants](#9-invariants)
10. [Modulo](#10-modulo)
11. [Counting](#11-counting-basics)
12. [Combinations](#12-pigeonhole-principle)
13. [Pigeonhole Principle](#12-pigeonhole-principle)
14. [Inclusion Exclusion](#13-inclusion-exclusion)
15. [Probability](#14-probability)
16. [Expected Value](#15-expected-value)
17. [Binary & Bitwise](#16-binary-number-system)
18. [XOR Observations](#18-xor-observations)
19. [Geometry](#19-geometry-basics)
20. [CP Math Patterns](#20-cp-math-patterns)
21. [FAANG Math Patterns](#21-faang-math-patterns)
22. [Common Traps](#22-common-traps)
23. [Final Checklist](#23-final-checklist)
24. [Ultimate C++ Templates](#24-ultimate-c-templates)

---


# 1. Algebra Basics

## Sum Of First N Numbers

```text
1 + 2 + ... + n
= n(n+1)/2
```

## Sum Of Squares

```text
1² + 2² + ... + n²
= n(n+1)(2n+1)/6
```

## Geometric Progression

```text
1 + r + r² + ... + r^(n-1)
= (r^n - 1)/(r - 1)
```

---

# 2. Bounds Thinking

## Minimum Sum Of N Positive Integers

```text
Minimum = n
```

Because smallest positive integer is 1.

## Interval Overlap

```text
max(l1, l2) <= min(r1, r2)
```

---

# 3. Absolute Value

```text
|a - b|
```

Distance on number line.

---

# 4. Powers And Logarithms

```text
2^10 ≈ 10^3
```

```text
log₂(n)
```

Means how many times divide by 2.

---

# 5. GCD / LCM

```text
gcd(a,b) * lcm(a,b) = a*b
```

```cpp
long long gcdll(long long a, long long b) {
    while (b) {
        a %= b;
        swap(a, b);
    }
    return a;
}
```

```cpp
long long lcmll(long long a, long long b) {
    return a / gcdll(a, b) * b;
}
```

---

# 6. Divisibility Rules

- Divisible by 2 → last digit even
- Divisible by 3 → digit sum divisible by 3
- Divisible by 5 → ends with 0 or 5
- Divisible by 9 → digit sum divisible by 9

---

# 7. Floor / Ceil

```cpp
(a + b - 1) / b
```

Ceil division for positive integers.

---

# 8. Odd Even Parity

```text
odd + odd = even
odd + even = odd
even + even = even
```

---

# 9. Invariants

Invariant means:

```text
Something that never changes
```

---

# 10. Modulo

```text
(a+b)%m = ((a%m)+(b%m))%m
```

```text
(a*b)%m = ((a%m)*(b%m))%m
```

```cpp
long long modpow(long long a, long long b, long long mod) {

    long long ans = 1;

    while (b > 0) {

        if (b & 1)
            ans = (ans * a) % mod;

        a = (a * a) % mod;
        b >>= 1;
    }

    return ans;
}
```

---

# 11. Counting Basics

```text
nPr = n! / (n-r)!
```

```text
nCr = n! / (r! * (n-r)!)
```

---

# 12. Pigeonhole Principle

```text
n+1 objects into n boxes
=> at least one box has >=2 objects
```

---

# 13. Inclusion Exclusion

```text
|A U B|
= |A| + |B| - |A ∩ B|
```

---

# 14. Probability

```text
Probability = favorable / total
```

At least one event:

```text
1 - P(no event)
```

---

# 15. Expected Value

```text
E(X) = Σ(value * probability)
```

---

# 16. Binary Number System

```text
1011₂ = 8 + 2 + 1 = 11
```

---

# 17. Bitwise Observations

```cpp
bool isPowerOfTwo(long long n) {
    return n > 0 && (n & (n - 1)) == 0;
}
```

```cpp
__builtin_popcount(x)
```

---

# 18. XOR Observations

```text
x ^ x = 0
x ^ 0 = x
```

```cpp
a ^= b;
b ^= a;
a ^= b;
```

---

# 19. Geometry Basics

```text
Distance:
sqrt((x2-x1)^2 + (y2-y1)^2)
```

```text
Area of triangle:
1/2 * base * height
```

---

# 20. CP Math Patterns

## Prefix Modulo Observation

If two prefix sums have same modulo:

```text
Their difference divisible by modulo
```

## Famous Pigeonhole Observation

For n prefix sums modulo n:

```text
Either one remainder = 0
OR
two prefix sums share same remainder
```

Thus:

```text
Subarray divisible by n exists
```

---

# 21. FAANG Math Patterns

Common patterns:

- prefix sums
- modulo hashing
- overflow handling
- binary search bounds
- combinatorics
- parity
- probability

---

# 22. Common Traps

## Overflow

Wrong:

```cpp
int x = a * b;
```

Correct:

```cpp
long long x = 1LL * a * b;
```

## Negative Modulo

```cpp
((x % mod) + mod) % mod
```

---

# 23. Final Checklist

Before solving:

- constraints?
- brute force?
- parity?
- modulo?
- invariant?
- prefix sum?
- greedy?
- binary search?
- overflow?

---

# 24. Ultimate C++ Templates

## Fast IO

```cpp
ios::sync_with_stdio(false);
cin.tie(nullptr);
```

## Prefix Sum

```cpp
vector<long long> pref(n + 1);

for (int i = 0; i < n; i++) {
    pref[i + 1] = pref[i] + a[i];
}
```

## Binary Search

```cpp
int l = 0;
int r = 1e9;

while (l <= r) {

    int mid = l + (r - l) / 2;

    if (check(mid))
        r = mid - 1;
    else
        l = mid + 1;
}
```

## Sieve

```cpp
vector<bool> prime(n + 1, true);

prime[0] = prime[1] = false;

for (int i = 2; i * i <= n; i++) {

    if (prime[i]) {

        for (int j = i * i; j <= n; j += i) {
            prime[j] = false;
        }
    }
}
```

---

# END
