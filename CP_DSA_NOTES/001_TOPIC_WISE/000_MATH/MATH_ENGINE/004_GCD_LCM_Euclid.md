# 004_GCD_LCM_Euclid.md

# MiniMathEngine — GCD, LCM and Euclidean Algorithm

> Goal: build deep intuition for:
>
> ```text
> divisibility
> common factors
> repeating cycles
> ```
>
> This file is one of the MOST important foundations in:
>
> - Number Theory
> - Modular Arithmetic
> - CRT
> - Fractions
> - Scheduling Problems
> - Prime Theory
> - Competitive Programming

---

# Clickable Index

- [1. Why This Topic Matters](#1-why-this-topic-matters)
- [2. What Is GCD](#2-what-is-gcd)
- [3. What Is LCM](#3-what-is-lcm)
- [4. Core Mental Model](#4-core-mental-model)
- [5. Divisor Intuition](#5-divisor-intuition)
- [6. Why Euclidean Algorithm Works](#6-why-euclidean-algorithm-works)
- [7. Euclid Step-by-Step Working](#7-euclid-step-by-step-working)
- [8. Binary View Of Euclid](#8-binary-view-of-euclid)
- [9. GCD And LCM Relationship](#9-gcd-and-lcm-relationship)
- [10. Coprime Intuition](#10-coprime-intuition)
- [11. Pattern Detector](#11-pattern-detector)
- [12. C++ Code 1 — Iterative GCD](#12-c-code-1--iterative-gcd)
- [13. C++ Code 2 — Recursive GCD](#13-c-code-2--recursive-gcd)
- [14. C++ Code 3 — LCM](#14-c-code-3--lcm)
- [15. C++ Code 4 — GCD Of Array](#15-c-code-4--gcd-of-array)
- [16. C++ Code 5 — Fraction Simplification](#16-c-code-5--fraction-simplification)
- [17. Dry Run Table](#17-dry-run-table)
- [18. Java Logic Comments](#18-java-logic-comments)
- [19. Common Mistakes](#19-common-mistakes)
- [20. CP / FAANG Problem Forms](#20-cp--faang-problem-forms)
- [21. Real World Mapping](#21-real-world-mapping)
- [22. Practice Problems](#22-practice-problems)
- [23. Final Mental Summary](#23-final-mental-summary)
- [24. Next Step](#24-next-step)

---

# 1. Why This Topic Matters

GCD and LCM teach the structure of numbers.

This topic appears everywhere:

```text
Fractions
Divisibility
Scheduling
Prime theory
CRT
Modular arithmetic
Combinatorics
```

This topic builds intuition for:

```text
How numbers interact with each other.
```

Very important foundation.

---

# 2. What Is GCD

GCD means:

```cpp
Greatest Common Divisor
```

Largest number dividing both numbers.

Example:

```cpp
GCD(12,18)=6
```

Divisors of 12:

```text
1 2 3 4 6 12
```

Divisors of 18:

```text
1 2 3 6 9 18
```

Largest common divisor:

```cpp
6
```

---

# 3. What Is LCM

LCM means:

```cpp
Least Common Multiple
```

Smallest number divisible by both numbers.

Example:

```cpp
LCM(4,6)=12
```

Multiples of 4:

```text
4 8 12 16 20
```

Multiples of 6:

```text
6 12 18 24
```

First common multiple:

```cpp
12
```

---

# 4. Core Mental Model

GCD tells:

```text
How much structure two numbers share.
```

LCM tells:

```text
When repeating cycles synchronize.
```

Very important mental model.

---

# 5. Divisor Intuition

Suppose:

```cpp
24
```

Prime factorization:

```cpp
24 = 2^3 * 3
```

All divisors come from combinations:

```text
2^0 * 3^0 = 1
2^1 * 3^0 = 2
2^2 * 3^0 = 4
2^3 * 3^0 = 8
...
```

Now:

```cpp
18 = 2 * 3^2
```

Common prime powers:

```cpp
2^1 * 3^1 = 6
```

That becomes GCD.

---

# 6. Why Euclidean Algorithm Works

Core formula:

```cpp
gcd(a,b)=gcd(b,a%b)
```

This is EXTREMELY important.

Suppose:

```cpp
a = b*q + r
```

Then:

```cpp
r = a%b
```

Any divisor of:

```cpp
a and b
```

also divides:

```cpp
r
```

Therefore common divisors remain same.

So:

```cpp
gcd(a,b)=gcd(b,a%b)
```

---

# 7. Euclid Step-by-Step Working

Find:

```cpp
gcd(48,18)
```

Step 1:

```cpp
48 % 18 = 12
```

Now:

```cpp
gcd(48,18)
=
gcd(18,12)
```

---

Step 2:

```cpp
18 % 12 = 6
```

Now:

```cpp
gcd(18,12)
=
gcd(12,6)
```

---

Step 3:

```cpp
12 % 6 = 0
```

Now:

```cpp
gcd(12,6)
=
gcd(6,0)
```

Answer:

```cpp
6
```

---

# 8. Binary View Of Euclid

Each modulo operation dramatically reduces numbers.

Example:

```text
48 -> 18 -> 12 -> 6 -> 0
```

This is why Euclid is extremely fast.

Time complexity:

```text
O(log(min(a,b)))
```

One of the oldest efficient algorithms.

---

# 9. GCD And LCM Relationship

Very important formula:

```cpp
GCD(a,b) * LCM(a,b) = a*b
```

Therefore:

```cpp
LCM(a,b) = (a*b)/gcd(a,b)
```

Safer form:

```cpp
(a/gcd)*b
```

Avoids overflow.

---

# 10. Coprime Intuition

Two numbers are coprime if:

```cpp
gcd(a,b)=1
```

Example:

```cpp
8 and 15
```

Because:

```cpp
gcd(8,15)=1
```

Coprimes are VERY important in:

- modular inverse
- Euler Phi
- CRT
- cryptography

---

# 11. Pattern Detector

If you see:

```text
largest common divisor
```

think:

```text
GCD
```

If you see:

```text
smallest synchronized multiple
```

think:

```text
LCM
```

If you see:

```text
fraction simplification
```

think:

```text
divide numerator and denominator by GCD
```

If you see:

```text
coprime
```

think:

```text
gcd(a,b)=1
```

---

# 12. C++ Code 1 — Iterative GCD

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Euclidean Algorithm
//
// gcd(a,b)=gcd(b,a%b)
//
// Time Complexity:
// O(log(min(a,b)))
// ---------------------------------------------------
ll gcdIterative(ll a, ll b) {

    // Continue until remainder becomes 0
    while (b != 0) {

        // Compute remainder
        ll remainder = a % b;

        // Move smaller number upward
        a = b;

        // Move remainder downward
        b = remainder;
    }

    // Final non-zero value becomes gcd
    return a;
}

int main() {

    ll a = 48;
    ll b = 18;

    cout << gcdIterative(a, b) << "\n";

    return 0;
}
```

---

# 13. C++ Code 2 — Recursive GCD

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Recursive Euclidean Algorithm
// ---------------------------------------------------
ll gcdRecursive(ll a, ll b) {

    // Base case
    if (b == 0)
        return a;

    // Recursive reduction
    return gcdRecursive(b, a % b);
}

int main() {

    cout << gcdRecursive(48, 18) << "\n";

    return 0;
}
```

---

# 14. C++ Code 3 — LCM

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll gcdValue(ll a, ll b) {

    while (b != 0) {

        ll rem = a % b;

        a = b;
        b = rem;
    }

    return a;
}

// ---------------------------------------------------
// LCM using:
// gcd*lcm=a*b
// ---------------------------------------------------
ll lcmValue(ll a, ll b) {

    return (a / gcdValue(a, b)) * b;
}

int main() {

    cout << lcmValue(4, 6) << "\n";

    return 0;
}
```

---

# 15. C++ Code 4 — GCD Of Array

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll gcdValue(ll a, ll b) {

    while (b != 0) {

        ll rem = a % b;

        a = b;
        b = rem;
    }

    return a;
}

// ---------------------------------------------------
// GCD of entire array
// ---------------------------------------------------
ll gcdArray(vector<ll>& arr) {

    ll answer = arr[0];

    for (int i = 1; i < arr.size(); i++) {

        answer = gcdValue(answer, arr[i]);
    }

    return answer;
}

int main() {

    vector<ll> arr = {24, 36, 60};

    cout << gcdArray(arr) << "\n";

    return 0;
}
```

---

# 16. C++ Code 5 — Fraction Simplification

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll gcdValue(ll a, ll b) {

    while (b != 0) {

        ll rem = a % b;

        a = b;
        b = rem;
    }

    return a;
}

// ---------------------------------------------------
// Reduce fraction:
// numerator/denominator
// ---------------------------------------------------
void simplifyFraction(ll numerator, ll denominator) {

    ll g = gcdValue(numerator, denominator);

    numerator /= g;
    denominator /= g;

    cout << numerator
         << "/"
         << denominator
         << "\n";
}

int main() {

    simplifyFraction(20, 30);

    return 0;
}
```

---

# 17. Dry Run Table

Find:

```cpp
gcd(48,18)
```

| a | b | a%b |
|---:|---:|---:|
| 48 | 18 | 12 |
| 18 | 12 | 6 |
| 12 | 6 | 0 |

Answer:

```cpp
6
```

---

# 18. Java Logic Comments

```text
while b != 0:
    remainder = a % b

    a = b
    b = remainder

return a
```

Mental model:

```text
Keep shrinking numbers
until remainder becomes 0.
```

---

# 19. Common Mistakes

## Mistake 1 — Overflow In LCM

Wrong:

```cpp
a*b/gcd
```

Safer:

```cpp
(a/gcd)*b
```

---

## Mistake 2 — Forgetting gcd(a,0)=a

Very important base case.

---

## Mistake 3 — Confusing GCD And LCM

Remember:

```text
GCD -> common divisor
LCM -> common multiple
```

---

## Mistake 4 — Negative Numbers

Usually use:

```cpp
gcd(abs(a), abs(b))
```

---

# 20. CP / FAANG Problem Forms

## Form 1 — GCD Of Array

Very common.

---

## Form 2 — Fraction Simplification

Use gcd.

---

## Form 3 — Synchronization Problems

Use LCM.

---

## Form 4 — Coprime Counting

Use gcd.

---

## Form 5 — Divisibility Queries

Use gcd properties.

---

## Form 6 — CRT / Modular Inverse

Require coprime intuition.

---

# 21. Real World Mapping

## Task Scheduling

Task A every:

```text
6 sec
```

Task B every:

```text
8 sec
```

Both align after:

```text
LCM(6,8)=24 sec
```

---

## Audio / Video Synchronization

Frame cycle synchronization.

---

## Cryptography

RSA uses gcd heavily.

---

## Networking

Packet interval alignment.

---

## Distributed Systems

Periodic task coordination.

---

# 22. Practice Problems

Easy:

```text
1. gcd(a,b)
2. lcm(a,b)
3. gcd of array
4. simplify fraction
```

Medium:

```text
5. coprime count
6. gcd subsequence
7. gcd graph
8. lcm optimization
```

Hard:

```text
9. CRT
10. Euler Phi
11. Mobius
12. Diophantine equations
```

---

# 23. Final Mental Summary

Remember:

```text
GCD measures shared structure.
```

And:

```text
LCM measures synchronization point.
```

Core identity:

```cpp
gcd(a,b)=gcd(b,a%b)
```

Most important idea:

```text
Modulo reduction preserves common divisors.
```

---

# 24. Next Step

```text
005_Prime_Checking.md
```

Next intuition:

```text
Prime numbers are the atoms of integers.
```
