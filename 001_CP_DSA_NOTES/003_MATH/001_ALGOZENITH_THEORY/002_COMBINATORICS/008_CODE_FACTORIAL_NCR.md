# Factorial & nCr — Compact Constraint Guide

> **Goal:** Read the constraints first, then choose the correct `nCr` method.

---

## Table of Contents

- [1. Factorial](#1-factorial)
- [2. nCr Core Formula](#2-ncr-core-formula)
- [3. Constraint Decision Map](#3-constraint-decision-map)
- [4. Setup 1 — One Query, Prime Modulo](#4-setup-1)
- [5. Setup 2 — Huge n, Small r](#5-setup-2)
- [6. Setup 3 — Exact nCr](#6-setup-3)
- [7. Setup 4 — Composite / Arbitrary Modulo](#7-setup-4)
- [8. Setup 5 — Many Queries](#8-setup-5)
- [9. Setup 6 — O(1) Queries with invFact](#9-setup-6)
- [10. Important Conditions & Mistakes](#10-important-conditions)
- [11. Final Cheat Sheet](#11-final-cheat-sheet)

---

<a id="1-factorial"></a>
# 1. Factorial

```text
n! = n × (n-1) × ... × 2 × 1

0! = 1
1! = 1
```

`n!` = number of ways to arrange `n` distinct objects.

Example:

```text
3! = 3 × 2 × 1 = 6
```

## Factorial Modulo

Instead of computing a huge factorial first:

```text
ans = 1
for i = 2..n:
    ans = ans × i mod M
```

Dry run: `5! mod 7`

```text
ans=1
  │ ×2 %7
  ▼
  2
  │ ×3 %7
  ▼
  6
  │ ×4 %7
  ▼
  3
  │ ×5 %7
  ▼
  1
```

```cpp
long long factorialMod(int n, long long mod) {
    long long ans = 1;

    for (int i = 2; i <= n; ++i)
        ans = ans * i % mod;

    return ans;
}
```

```text
Time: O(n)
```

---

<a id="2-ncr-core-formula"></a>
# 2. nCr Core Formula

`C(n,r)` = choose `r` objects from `n` where **order does not matter**.

```text
             n!
C(n,r) = -----------
          r!(n-r)!
```

Example:

```text
             5!
C(5,2) = ----------
           2! × 3!

         5×4×3!
       = ---------
         2×1×3!

       cancel 3!
           ↓

         5×4
       = -----
         2×1

       = 10
```

## Symmetry

```text
C(n,r) = C(n,n-r)
```

Therefore:

```cpp
r = min(r, n - r);
```

Example:

```text
C(10,8) = C(10,2)
```

Choosing `8` to take is equivalent to choosing `2` to leave.

---

<a id="3-constraint-decision-map"></a>
# 3. Constraint Decision Map

```text
                    Need C(n,r)
                        │
              ┌─────────┴─────────┐
              ▼                   ▼
            EXACT               MODULO
              │                   │
              ▼                   ▼
       answer fits type?      Is MOD prime?
              │              /            \
             YES           YES          NO / unsure
              │             │                │
              ▼             │                ▼
      multiplicative        │            Pascal DP
          O(r)              │
                            ▼
                  one/few or many queries?
                    /               \
                 FEW                MANY
                  │                   │
          n huge, r small?            ▼
             /      \           fact + invFact
           YES      NO                │
            │        │                ▼
            ▼        ▼            O(1)/query
        O(r)      factorial
       product    + inverse
```

> **Main habit:** constraints choose the implementation.

---

<a id="4-setup-1"></a>
# 4. Setup 1 — One Query, Prime Modulo

For prime `MOD`:

```text
a⁻¹ ≡ a^(MOD-2) (mod MOD)
```

Therefore:

```text
C(n,r)
=
n! × inverse(r!(n-r)!)
mod MOD
```

Visual:

```text
fact(n)
   │
   ├──────────────┐
   ▼              ▼
fact(r)       fact(n-r)
   │              │
   └──────×───────┘
          │
          ▼
      denominator
          │
       inverse
          │
          ▼
fact(n) × inverse(denominator)
          │
          ▼
        answer
```

```cpp
const long long MOD = 1'000'000'007LL;

long long modPow(long long a, long long e) {
    long long ans = 1;

    while (e) {
        if (e & 1) ans = ans * a % MOD;
        a = a * a % MOD;
        e >>= 1;
    }
    return ans;
}

long long inverse(long long x) {
    return modPow(x, MOD - 2);
}

long long factorial(int n) {
    long long ans = 1;
    for (int i = 2; i <= n; ++i)
        ans = ans * i % MOD;
    return ans;
}

long long nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    long long den =
        factorial(r) * factorial(n-r) % MOD;

    return factorial(n) * inverse(den) % MOD;
}
```

```text
Time: O(n + log MOD)
```

For many queries, do not recompute factorials—use Setup 5/6.

---

<a id="5-setup-2"></a>
# 5. Setup 2 — Huge n, Small r

Example constraints:

```text
n ≤ 10^9
r ≤ 20
MOD = prime
```

Never calculate `n!`.

Cancel `(n-r)!`:

```text
             n!
C(n,r) = -----------
          r!(n-r)!

       n(n-1)...(n-r+1)
     = -----------------
              r!
```

Example: `C(10,3)`

```text
10! / (3!7!)

10×9×8×7!
------------
   3!×7!

cancel 7!
    ↓

10×9×8
-------
 1×2×3
```

Dry run:

```text
i      numerator      denominator
1      10             1
2      90             2
3      720            6

answer = 720 × inverse(6)
```

```cpp
long long nCrSmallR(long long n, long long r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n-r);

    long long num = 1, den = 1;

    for (long long i = 1; i <= r; ++i) {
        num = num * ((n-i+1) % MOD) % MOD;
        den = den * (i % MOD) % MOD;
    }

    return num * inverse(den) % MOD;
}
```

```text
Time: O(min(r,n-r) + log MOD)
```

The denominator must be invertible modulo `MOD`.

---

<a id="6-setup-3"></a>
# 6. Setup 3 — Exact nCr

When the exact result fits the chosen integer type:

```cpp
long long exactNCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n-r);

    long long ans = 1;

    for (int i = 1; i <= r; ++i)
        ans = ans * (n-i+1) / i;

    return ans;
}
```

Dry run: `C(5,2)`

```text
ans = 1
   │ ×5 /1
   ▼
   5
   │ ×4 /2
   ▼
  10
```

```text
Time: O(min(r,n-r))
```

Important:

```text
Do NOT memorize "n ≤ 40 always fits long long".

The final and intermediate values must actually fit
the chosen integer type.
```

Use big integers when necessary.

---

<a id="7-setup-4"></a>
# 7. Setup 4 — Composite / Arbitrary Modulo

Example:

```text
MOD = 10^9
```

`10^9` is composite, so Fermat's inverse formula cannot be blindly used.

Use Pascal's identity:

```text
C(n,r)
=
C(n-1,r-1) + C(n-1,r)
```

Why?

```text
Choose r from n
      │
      ▼
focus on one item
   /           \
 TAKE          SKIP
  │              │
choose r-1     choose r
from n-1      from n-1
  │              │
C(n-1,r-1)   C(n-1,r)
   \            /
    ----- + -----
          │
          ▼
        C(n,r)
```

Pascal triangle:

```text
          1
        1   1
      1   2   1
    1   3   3   1
  1   4   6   4   1
          ↑
        3 + 3
```

```cpp
long long nCrAnyModulo(int n, int r, long long mod) {
    if (r < 0 || r > n) return 0;

    r = min(r, n-r);

    vector<long long> dp(r + 1);
    dp[0] = 1;

    for (int i = 1; i <= n; ++i) {
        for (int j = min(i, r); j >= 1; --j) {
            dp[j] = (dp[j] + dp[j-1]) % mod;
        }
    }

    return dp[r];
}
```

```text
Time  : O(nr)
Memory: O(r)
```

Iterate `j` backwards so values from the previous Pascal row are not overwritten too early.

---

<a id="8-setup-5"></a>
# 8. Setup 5 — Many Queries

Example:

```text
q,n,r ≤ 10^6
MOD = 1e9+7
```

Precompute:

```text
fact[i] = i! mod MOD
```

Then:

```text
C(n,r)
=
fact[n] × inverse(fact[r] × fact[n-r])
mod MOD
```

Factorial construction:

```text
fact[0] = 1
    │ ×1
    ▼
fact[1] = 1
    │ ×2
    ▼
fact[2] = 2
    │ ×3
    ▼
fact[3] = 6
    │ ×4
    ▼
fact[4] = 24
```

```cpp
const int MAXN = 1'000'000;
vector<long long> fact(MAXN + 1);

void precomputeFact() {
    fact[0] = 1;

    for (int i = 1; i <= MAXN; ++i)
        fact[i] = fact[i-1] * i % MOD;
}

long long nCrQuery(int n, int r) {
    if (r < 0 || r > n) return 0;

    long long den =
        fact[r] * fact[n-r] % MOD;

    return fact[n] * inverse(den) % MOD;
}
```

```text
Precompute : O(MAXN)
Per query  : O(log MOD)
```

---

<a id="9-setup-6"></a>
# 9. Setup 6 — O(1) Queries with invFact

For many queries, also precompute:

```text
invFact[i] = (i!)⁻¹
```

Then:

```text
C(n,r)
=
fact[n]
× invFact[r]
× invFact[n-r]
mod MOD
```

## Why Can invFact Be Built Backwards?

```text
i! = i × (i-1)!
```

Therefore:

```text
invFact[i-1]
=
i × invFact[i]
mod MOD
```

Visual:

```text
fact[] goes FORWARD

fact[0]
   │ ×1
   ▼
fact[1]
   │ ×2
   ▼
...
fact[MAXN]
```

Calculate only one expensive inverse:

```text
invFact[MAXN]
=
inverse(fact[MAXN])
```

Then:

```text
invFact[] goes BACKWARD

invFact[MAXN]
      │ ×MAXN
      ▼
invFact[MAXN-1]
      │ ×(MAXN-1)
      ▼
     ...
      ▼
invFact[0]
```

## Standard CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;

const int MAXN = 1'000'000;
const int64 MOD = 1'000'000'007LL;

vector<int64> fact(MAXN + 1);
vector<int64> invFact(MAXN + 1);

int64 modPow(int64 a, int64 e) {
    int64 ans = 1;

    while (e) {
        if (e & 1) ans = ans * a % MOD;
        a = a * a % MOD;
        e >>= 1;
    }

    return ans;
}

void precompute() {
    fact[0] = 1;

    for (int i = 1; i <= MAXN; ++i)
        fact[i] = fact[i-1] * i % MOD;

    invFact[MAXN] = modPow(fact[MAXN], MOD - 2);

    for (int i = MAXN; i >= 1; --i)
        invFact[i-1] = invFact[i] * i % MOD;
}

int64 nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    return fact[n]
         * invFact[r] % MOD
         * invFact[n-r] % MOD;
}
```

```text
Precompute : O(MAXN + log MOD)
Per query  : O(1)
Memory     : O(MAXN)
```

---

<a id="10-important-conditions"></a>
# 10. Important Conditions & Mistakes

## 1. Do Not Use Normal Division Under Modulo

Wrong:

```cpp
ans = numerator / denominator;
```

Correct when inverse exists:

```text
a / b mod M
=
a × b⁻¹ mod M
```

---

## 2. Fermat Inverse Requires Prime Modulo

```text
a⁻¹ ≡ a^(MOD-2) mod MOD
```

requires a prime modulus and `a` not divisible by that modulus.

Do not blindly use it for a composite modulus such as:

```text
10^9
```

---

## 3. Watch n ≥ MOD

For the standard factorial/inverse-factorial setup:

```text
MAXN < MOD
```

is the common safe case.

If:

```text
n ≥ MOD
```

then `n!` contains `MOD` as a factor:

```text
n! ≡ 0 (mod MOD)
```

so `fact[n]` cannot simply be inverted.

A different technique may be required.

---

## 4. Always Check r

```cpp
if (r < 0 || r > n)
    return 0;
```

---

## 5. Use Symmetry

```cpp
r = min(r, n-r);
```

Example:

```text
C(1,000,000, 999,998)
=
C(1,000,000, 2)
```

---

<a id="11-final-cheat-sheet"></a>
# 11. Final Cheat Sheet

## Formula

```text
             n!
C(n,r) = -----------
          r!(n-r)!
```

## Symmetry

```text
C(n,r) = C(n,n-r)
```

## Small-r Form

```text
          n(n-1)...(n-r+1)
C(n,r) = -------------------
                   r!
```

## Pascal

```text
C(n,r)
=
C(n-1,r-1) + C(n-1,r)
```

## Prime-Mod Inverse

```text
a⁻¹
≡
a^(MOD-2)
(mod MOD)
```

## Fast Many-Query nCr

```text
C(n,r)
=
fact[n]
× invFact[r]
× invFact[n-r]
mod MOD
```

## Constraint → Method

| Situation | Method | Complexity |
|---|---|---:|
| `n! mod M` | iterative factorial | `O(n)` |
| One/few `nCr`, manageable `n`, prime MOD | factorial + inverse | `O(n + log MOD)` |
| Huge `n`, small `r`, valid inverse | multiplicative | `O(r + log MOD)` |
| Exact `nCr`, answer fits | multiplicative exact | `O(r)` |
| Composite/arbitrary MOD, moderate `n,r` | Pascal DP | `O(nr)` |
| Many queries, prime MOD | `fact[]` | `O(log MOD)` / query |
| Many queries, fastest | `fact[] + invFact[]` | `O(1)` / query |

## Contest Recognition

```text
Huge n + tiny r
→ cancel factorials
→ process only r terms

Composite modulus
→ inverse may not exist
→ think Pascal DP

Many queries + prime modulus
→ precompute fact + invFact
→ O(1) nCr

Exact answer
→ multiplicative formula
→ no modulo
```

> **Final habit:** Don't memorize one `nCr` implementation.  
> **Read the constraints → identify the setup → choose the method.**
