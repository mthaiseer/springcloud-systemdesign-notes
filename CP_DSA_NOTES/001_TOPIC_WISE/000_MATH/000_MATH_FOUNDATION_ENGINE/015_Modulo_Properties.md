# 015_Modulo_Properties.md

# Modulo Properties For Competitive Programming

---

# 1. Introduction

Modulo properties are one of the MOST important mathematical tools in CP.

Without modulo properties:
- modular arithmetic
- combinatorics
- hashing
- DP counting
- binary exponentiation
- number theory

become impossible.

This chapter focuses on:

```text
How modulo behaves mathematically
```

NOT just:
```text
remainder operator
```

---

# 2. Core Modulo Idea

Modulo means:

```text
keep only remainder
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

# 3. Why Modulo Properties Matter

Huge numbers quickly overflow.

Example:

```text
1000000000 × 1000000000
```

Very large.

Modulo properties allow:

```text
break calculations safely
```

without changing final answer modulo m.

---

# 4. Addition Property

MOST IMPORTANT PROPERTY:

```text
(a + b) % m
=
((a % m) + (b % m)) % m
```

---

# Example

```text
(17 + 9) % 5

=
26 % 5
=
1
```

Now using property:

```text
17 % 5 = 2
9 % 5 = 4

(2 + 4) % 5
=
6 % 5
=
1
```

Same answer.

---

# 5. Subtraction Property

```text
(a - b) % m
=
((a % m) - (b % m) + m) % m
```

IMPORTANT:
```text
+ m
```

prevents negative modulo.

---

# Example

```text
(7 - 10) % 5
```

Safe formula:

```text
((7%5)-(10%5)+5)%5

=
(2-0+5)%5

=
7%5

=
2
```

---

# 6. Multiplication Property

```text
(a × b) % m
=
((a % m) × (b % m)) % m
```

VERY important.

Used everywhere in:
- combinatorics
- DP
- matrix exponentiation

---

# Example

```text
(17 × 9) % 5

=
153 % 5
=
3
```

Using property:

```text
(2 × 4) % 5
=
8 % 5
=
3
```

Same answer.

---

# 7. Division Does NOT Work Normally

IMPORTANT TRAP.

This is WRONG:

```text
(a / b) % m
=
((a%m)/(b%m))%m
```

NOT valid generally.

Need:

```text
Modular Inverse
```

---

# 8. Modular Inverse Idea

Instead of division:

```text
a / b
```

we compute:

```text
a × inverse(b)
```

where:

```text
b × inverse(b) ≡ 1 (mod m)
```

Very important in:
- combinatorics
- modular arithmetic
- probability

---

# 9. Safe Modulo Multiplication

Always do:

```cpp
(ans * x) % MOD
```

immediately.

Never allow huge multiplication to grow uncontrolled.

---

# 10. Modular Exponentiation

Need compute:

```text
a^b % MOD
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

# 11. Why MOD = 1e9+7?

Common CP modulus:

```text
1000000007
```

because:
- prime
- large
- safe
- modular inverse works nicely

---

# 12. Prefix Sum Modulo Property

Important observation:

If:

```text
prefix[i] % k
==
prefix[j] % k
```

then:

```text
subarray(i+1...j)
```

sum divisible by k.

Massive CP pattern.

---

# 13. Negative Modulo Trap

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

# 14. Modulo And Cycles

Modulo creates repeating cycles.

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

# 15. CP / FAANG Problem Forms

---

# Form 1 — Modular Addition

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

long long addModulo(
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

# Form 2 — Modular Multiplication

## Problem

Compute product modulo MOD.

---

## Observation

Take modulo immediately.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long multiplyModulo(
    vector<int>& a,
    long long MOD
) {

    long long ans = 1;

    for (int x : a) {

        ans =
            (ans * x) % MOD;
    }

    return ans;
}
```

---

# Form 3 — Fast Power Modulo

## Problem

Compute:

```text
a^b % MOD
```

---

## Observation

Binary exponentiation.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long powerModulo(
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

# Form 4 — Safe Negative Modulo

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

int safeModulo(int x, int MOD) {

    return ((x % MOD)
            + MOD) % MOD;
}
```

---

# Form 5 — Prefix Modulo Equal

## Problem

Count subarrays divisible by k.

---

## Observation

Equal prefix modulo values.

---

## Step-by-Step Working

If:

```text
prefix[i]%k
=
prefix[j]%k
```

then:

```text
subarray divisible by k
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int subarraysDivByK(
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

# Form 6 — Last Digit Cycle

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

# Form 7 — Modular Combination

## Problem

Compute:

```text
nCr % MOD
```

---

## Observation

Need modular inverse.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 1e9+7;

long long modPow(
    long long a,
    long long b
) {

    long long ans = 1;

    while (b > 0) {

        if (b & 1)
            ans = (ans * a) % MOD;

        a = (a * a) % MOD;

        b >>= 1;
    }

    return ans;
}

long long modInverse(long long x) {

    return modPow(x, MOD - 2);
}
```

---

# 16. Real World Applications

| Real System | Usage |
|---|---|
| Cryptography | modular arithmetic |
| Databases | hashing |
| Distributed systems | consistent hashing |
| Networking | cyclic counters |
| Compression | rolling hash |
| Blockchain | modular arithmetic |
| Game engines | circular indexing |

---

# 17. Real Engineering Insight

Modulo usually means:

```text
Cycles
Compression
Wraparound
Hashing
```

This mental mapping is very important.

---

# 18. Observation Recognition Signals

Look for:

```text
1. huge numbers
2. cyclic patterns
3. hashing
4. divisibility
5. wraparound
6. repeated powers
7. circular arrays
8. periodicity
```

---

# 19. Decision Tree

```text
Huge multiplication?
→ modulo multiplication

Huge powers?
→ modular exponentiation

Negative modulo?
→ safe formula

Repeated cycle?
→ modulo periodicity

Subarray divisibility?
→ prefix modulo

Need division?
→ modular inverse
```

---

# 20. Common Traps

```text
1. Negative modulo bug
2. Overflow before modulo
3. Forgetting modulo after multiplication
4. Wrong modular subtraction
5. Assuming division works normally
6. Missing cycles
7. Wrong modular inverse usage
8. Using pow() for modulo power
```

---

# 21. Final Checklist

Before solving:

```text
1. Are numbers huge?
2. Is modulo needed to prevent overflow?
3. Is there cyclic behavior?
4. Is prefix modulo useful?
5. Is modular exponentiation needed?
6. Is modular inverse needed?
7. Is subtraction safe?
8. Is negative modulo handled?
```

---

# 22. Final Mental Shortcut

```text
Modulo Properties
=
Safe Arithmetic
+
Cycles
+
Compression
+
Periodic Behavior
```
