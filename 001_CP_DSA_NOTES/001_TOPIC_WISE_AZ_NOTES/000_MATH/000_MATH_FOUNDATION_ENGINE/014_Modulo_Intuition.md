# 014_Modulo_Intuition.md

# Modulo Intuition For Competitive Programming

---

# 1. Introduction

Modulo is one of the MOST important concepts in CP.

It appears everywhere:
- cyclic patterns
- hashing
- number theory
- DP
- combinatorics
- rolling arrays
- binary exponentiation
- graph problems

Strong contestants think of modulo as:

```text
Cyclic behavior
```

NOT just:

```text
remainder operator
```

This intuition is extremely important.

---

# 2. What Is Modulo?

Modulo means:

```text
remainder after division
```

Example:

```text
17 % 5 = 2
```

because:

```text
17 = 5×3 + 2
```

---

# 3. Core Modulo Interpretation

Most important mental model:

```text
Modulo creates cycles.
```

Example:

Clock:

```text
0 1 2 3 4 5 6 7 8 9 10 11
```

After 11:

```text
comes back to 0
```

This is modulo.

---

# 4. Why Modulo Is Powerful

Modulo helps:
- keep numbers small
- detect cycles
- compress states
- optimize huge computations
- model repeating behavior

Very important in:
- CP
- distributed systems
- cryptography
- hashing

---

# 5. Modulo Arithmetic Rules

---

# Addition

```text
(a + b) % m
=
((a % m) + (b % m)) % m
```

---

# Subtraction

```text
(a - b) % m
=
((a % m) - (b % m) + m) % m
```

---

# Multiplication

```text
(a * b) % m
=
((a % m) * (b % m)) % m
```

---

# 6. Why We Use Modulo In CP

Numbers can become huge.

Example:

```text
2^100000
```

Impossible to store directly.

Instead:

```text
compute modulo MOD
```

Usually:

```text
10^9 + 7
```

---

# 7. Why 1e9+7?

Properties:
- prime
- large
- prevents overflow
- supports modular inverse

Very common CP modulus.

---

# 8. Modulo As Cycle Detection

Example:

Last digit of powers of 2:

```text
2 4 8 6
2 4 8 6
```

Cycle length:

```text
4
```

Use:

```text
n % 4
```

Huge optimization.

---

# 9. Negative Modulo Trap

In C++:

```cpp
-3 % 5 = -3
```

But mathematically:

```text
should be 2
```

Safe formula:

```cpp
((x % m) + m) % m
```

VERY IMPORTANT.

---

# 10. Modular Exponentiation

Need compute:

```text
a^b % m
```

efficiently.

Use:

```text
Binary Exponentiation
```

Complexity:

```text
O(log b)
```

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long modPow(
    long long a,
    long long b,
    long long mod
) {

    long long ans = 1;

    a %= mod;

    while (b > 0) {

        if (b & 1) {

            ans =
                (ans * a) % mod;
        }

        a =
            (a * a) % mod;

        b >>= 1;
    }

    return ans;
}
```

---

# 11. Modulo And Divisibility

Observation:

```text
a divisible by b
⇔
a % b == 0
```

Foundation of:
- divisibility
- gcd
- modular arithmetic

---

# 12. Modulo Classes

Modulo groups numbers.

Example:

Modulo 3:

```text
0:
0 3 6 9 ...

1:
1 4 7 10 ...

2:
2 5 8 11 ...
```

This classification is extremely important.

---

# 13. Prefix Sum Modulo

Very important observation:

If:

```text
prefix[i] % k
==
prefix[j] % k
```

then subarray sum divisible by k.

Massive CP pattern.

---

# 14. Modulo In Hashing

Hashing uses modulo to:
- compress keys
- map ranges
- distribute buckets

Very important in:
- unordered_map
- distributed systems
- databases

---

# 15. Modulo In Distributed Systems

Consistent hashing:

```text
server = hash(key) % n
```

Very common engineering pattern.

---

# 16. CP / FAANG Problem Forms

---

# Form 1 — Large Sum Modulo

## Problem

Compute huge sum modulo MOD.

---

## Observation

Take modulo during addition.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long sumModulo(
    vector<int>& a,
    long long MOD
) {

    long long ans = 0;

    for (int x : a) {

        ans =
            (ans + x) % MOD;
    }

    return ans;
}
```

---

# Form 2 — Fast Power Modulo

## Problem

Compute:

```text
a^b % MOD
```

efficiently.

---

## Observation

Binary exponentiation.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long modPower(
    long long a,
    long long b,
    long long MOD
) {

    long long ans = 1;

    a %= MOD;

    while (b > 0) {

        if (b & 1) {

            ans =
                (ans * a) % MOD;
        }

        a =
            (a * a) % MOD;

        b >>= 1;
    }

    return ans;
}
```

---

# Form 3 — Last Digit Cycle

## Problem

Find last digit of:

```text
2^n
```

---

## Observation

Cycle:

```text
2 4 8 6
```

Length:
```text
4
```

Use:

```text
n % 4
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int lastDigit(long long n) {

    vector<int> cycle = {6,2,4,8};

    return cycle[n % 4];
}
```

---

# Form 4 — Divisibility Check

## Problem

Check divisibility.

---

## Observation

```text
n % k == 0
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool divisible(int n, int k) {

    return n % k == 0;
}
```

---

# Form 5 — Prefix Sum Divisible By K

## Problem

Count subarrays divisible by k.

---

## Observation

Equal prefix modulo values.

---

## Step-by-Step Working

If:

```text
prefix[i] % k
=
prefix[j] % k
```

then:

```text
subarray(i+1...j)
```

divisible by k.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int countSubarrays(
    vector<int>& a,
    int k
) {

    unordered_map<int,int> freq;

    freq[0] = 1;

    int prefix = 0;

    int ans = 0;

    for (int x : a) {

        prefix =
            ((prefix + x) % k + k) % k;

        ans += freq[prefix];

        freq[prefix]++;
    }

    return ans;
}
```

---

# Form 6 — Negative Modulo Fix

## Problem

Convert negative modulo safely.

---

## Observation

Use:

```text
((x%m)+m)%m
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int safeModulo(int x, int m) {

    return ((x % m) + m) % m;
}
```

---

# Form 7 — Circular Array

## Problem

Access circular index.

---

## Observation

Use modulo wraparound.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int circularIndex(int i, int n) {

    return i % n;
}
```

---

# 17. Real World Applications

| Real System | Usage |
|---|---|
| Databases | hashing/sharding |
| Distributed systems | consistent hashing |
| Cryptography | modular arithmetic |
| Networking | packet cycles |
| Load balancing | modulo routing |
| Game engines | circular states |
| Compression | rolling hash |

---

# 18. Real Engineering Insight

Modulo usually means:

```text
Cycles
Wraparound
Compression
Grouping
```

This mental mapping is extremely important.

---

# 19. Observation Recognition Signals

When solving, look for:

```text
1. repeating pattern
2. cycles
3. wraparound
4. huge numbers
5. divisibility
6. circular behavior
7. hashing
8. periodicity
```

---

# 20. Decision Tree

```text
Repeating pattern?
→ modulo cycle

Huge powers?
→ modular exponentiation

Divisibility?
→ modulo

Circular array?
→ wraparound modulo

Equal prefix modulo?
→ divisible subarray

Need compression?
→ hashing modulo
```

---

# 21. Common Traps

```text
1. Negative modulo bug
2. Overflow before modulo
3. Forgetting modulo after multiplication
4. Wrong cycle indexing
5. Missing prefix modulo observation
6. Using pow() directly
7. Wrong modulo subtraction
8. Assuming modulo preserves division
```

---

# 22. Final Checklist

Before solving:

```text
1. Is there cycle?
2. Is there wraparound?
3. Are numbers huge?
4. Is divisibility involved?
5. Can modulo compress states?
6. Is prefix modulo useful?
7. Is hashing involved?
8. Is modular exponentiation needed?
```

---

# 23. Final Mental Shortcut

```text
Modulo
=
Cycles
+
Wraparound
+
Compression
+
Periodic Behavior
```
