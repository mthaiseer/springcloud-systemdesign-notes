# 007_GCD_LCM_Basics.md

# GCD And LCM Basics For Competitive Programming

---

# 1. Introduction

GCD and LCM are among the MOST important mathematical concepts in CP.

They appear everywhere:
- number theory
- divisibility
- fractions
- modulo problems
- cyclic patterns
- array transformations
- constructive problems

Understanding GCD deeply is extremely high ROI.

---

# 2. What Is GCD?

GCD means:

```text
Greatest Common Divisor
```

Definition:

```text
Largest number dividing both a and b.
```

Example:

```text
gcd(12, 18) = 6
```

because:

Divisors of 12:
```text
1 2 3 4 6 12
```

Divisors of 18:
```text
1 2 3 6 9 18
```

Largest common divisor:
```text
6
```

---

# 3. What Is LCM?

LCM means:

```text
Least Common Multiple
```

Definition:

```text
Smallest number divisible by both a and b.
```

Example:

```text
lcm(4, 6) = 12
```

because:

Multiples of 4:
```text
4 8 12 16 ...
```

Multiples of 6:
```text
6 12 18 ...
```

First common multiple:
```text
12
```

---

# 4. Most Important Formula

Relationship:

```text
gcd(a,b) * lcm(a,b) = a * b
```

Thus:

```text
lcm(a,b) = (a * b) / gcd(a,b)
```

Very important in CP.

---

# 5. Why GCD Matters In CP

GCD usually indicates:
- divisibility
- reduction
- normalization
- repeated subtraction
- invariant
- cyclic behavior

Recognition signals:
- common factor
- divide equally
- reduce fraction
- operations preserving gcd

---

# 6. Euclidean Algorithm

Most important GCD algorithm.

Observation:

```text
gcd(a,b) = gcd(b, a % b)
```

Repeat until:

```text
b = 0
```

Then:

```text
gcd = a
```

---

# 7. Step-by-Step Example

Find:

```text
gcd(48,18)
```

Steps:

```text
48 % 18 = 12
18 % 12 = 6
12 % 6 = 0
```

Answer:

```text
6
```

---

# 8. Euclidean Algorithm Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long gcdEuclid(long long a, long long b) {

    while (b != 0) {

        long long r = a % b;

        a = b;
        b = r;
    }

    return a;
}
```

Complexity:

```text
O(log(min(a,b)))
```

---

# 9. Recursive GCD

```cpp
#include <bits/stdc++.h>
using namespace std;

long long gcdRec(long long a, long long b) {

    if (b == 0) return a;

    return gcdRec(b, a % b);
}
```

---

# 10. LCM Using GCD

Formula:

```text
lcm(a,b) = (a/gcd(a,b)) * b
```

Use division first to avoid overflow.

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long lcmValue(long long a, long long b) {

    return (a / gcd(a, b)) * b;
}
```

---

# 11. Coprime Numbers

Two numbers are coprime if:

```text
gcd(a,b) = 1
```

Example:

```text
8 and 15
```

Important in:
- modular inverse
- number theory
- probability
- fractions

---

# 12. Fraction Reduction

Example:

```text
18 / 24
```

gcd:

```text
6
```

Reduced:

```text
3 / 4
```

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

pair<int,int> reduceFraction(int a, int b) {

    int g = gcd(a, b);

    return {a / g, b / g};
}
```

---

# 13. GCD In Arrays

Common patterns:

```text
gcd of entire array
gcd after removing element
maximum gcd pair
```

---

# Array GCD Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int arrayGCD(vector<int>& a) {

    int g = 0;

    for (int x : a) {

        g = gcd(g, x);
    }

    return g;
}
```

---

# 14. LCM In Cyclic Problems

LCM often appears when:

```text
multiple cycles align
```

Example:
- traffic lights
- repeated events
- periodic processes

---

# Example

One event every:
```text
4 seconds
```

Another:
```text
6 seconds
```

Together every:

```text
lcm(4,6) = 12
```

---

# 15. GCD And Repeated Subtraction

Important observation:

```text
Repeated subtraction preserves gcd.
```

Example:

```text
gcd(a,b) = gcd(a-b,b)
```

Used in:
- constructive
- games
- invariants

---

# 16. CP / FAANG Problem Forms

---

# Form 1 — Compute GCD

## Problem

Find gcd of two numbers.

---

## Step-by-Step Working

Example:

```text
gcd(24,18)

24 % 18 = 6
18 % 6 = 0

Answer = 6
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long gcdValue(long long a, long long b) {

    while (b) {

        a %= b;

        swap(a, b);
    }

    return a;
}
```

---

# Form 2 — Compute LCM

## Problem

Find lcm of two numbers.

---

## Observation

Formula:

```text
lcm = (a * b) / gcd
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long lcmValue(long long a, long long b) {

    return (a / gcd(a, b)) * b;
}
```

---

# Form 3 — Fraction Reduction

## Problem

Reduce fraction.

---

## Step-by-Step Working

Example:

```text
18 / 24
```

gcd:

```text
6
```

Reduced:

```text
3 / 4
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

pair<int,int> simplify(int a, int b) {

    int g = gcd(a, b);

    return {a / g, b / g};
}
```

---

# Form 4 — Array GCD

## Problem

Find gcd of array.

---

## Observation

GCD is associative.

```text
gcd(a,b,c)
=
gcd(gcd(a,b),c)
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int gcdArray(vector<int>& a) {

    int g = 0;

    for (int x : a) {

        g = gcd(g, x);
    }

    return g;
}
```

---

# Form 5 — Cyclic Synchronization

## Problem

Two repeating events occur every a and b seconds.
When together again?

---

## Observation

LCM.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long synchronize(long long a, long long b) {

    return lcm(a, b);
}
```

---

# 17. Real World Applications

| Real System | Usage |
|---|---|
| Cryptography | modular arithmetic |
| Networking | packet scheduling |
| Distributed systems | periodic synchronization |
| Video/audio systems | frame/sample alignment |
| Databases | shard balancing |
| Scheduling systems | repeated tasks |
| Compression | normalization |

---

# 18. Real Engineering Insight

GCD usually means:

```text
shared structure
```

LCM usually means:

```text
cycle synchronization
```

This mental mapping is extremely useful.

---

# 19. Decision Tree

```text
Common divisor?
→ GCD

Common cycle?
→ LCM

Need simplification?
→ divide by GCD

Repeated events?
→ LCM

Fraction reduction?
→ GCD

Array normalization?
→ GCD
```

---

# 20. Common Traps

```text
1. Overflow in LCM
2. Forgetting divide before multiply
3. gcd(0,x) confusion
4. Negative numbers
5. Assuming coprime incorrectly
6. Infinite recursion
7. Using brute force divisors
8. Forgetting gcd associativity
```

---

# 21. Final Checklist

Before solving:

```text
1. Is divisibility involved?
2. Is there common factor?
3. Is there repeating cycle?
4. Can Euclidean algorithm help?
5. Is normalization needed?
6. Are fractions reducible?
7. Is synchronization involved?
8. Can gcd simplify problem?
```

---

# 22. Final Mental Shortcut

```text
GCD
=
Shared Structure

LCM
=
Cycle Alignment
```
