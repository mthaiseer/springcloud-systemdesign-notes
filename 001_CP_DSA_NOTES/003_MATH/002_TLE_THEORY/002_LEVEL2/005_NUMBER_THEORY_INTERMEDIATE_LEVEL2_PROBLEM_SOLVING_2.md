# Number Theory — Intermediate Level 2
## Problem Solving 3 — Prime Subtraction & Divisor Analysis

> **Goal:** derive the solution from the mathematics, then remember the model — not the final formula.
>
> **Flow:** prerequisites → model → derivation → one or two dry runs → C++ → recognition.
>
> **Math rendering:** fenced `math` blocks only, to avoid Markdown/LaTeX rendering issues.

---

# Clickable Table of Contents

- [0. Prerequisites](#0-prerequisites)
  - [0.1 Prime Divisor Fact](#01-prime-divisor-fact)
  - [0.2 Prime Factorisation](#02-prime-factorisation)
  - [0.3 Independent Choices](#03-independent-choices)
  - [0.4 Geometric Progression](#04-geometric-progression)
  - [0.5 Modular Arithmetic](#05-modular-arithmetic)
  - [0.6 Binary Exponentiation](#06-binary-exponentiation)
  - [0.7 Modular Inverse](#07-modular-inverse)
  - [0.8 Fermat Exponent Reduction](#08-fermat-exponent-reduction)
- [1. Prime Subtraction — CF 1238A](#1-prime-subtraction--cf-1238a)
  - [1.1 Problem Model](#11-problem-model)
  - [1.2 Derivation](#12-derivation)
  - [1.3 Dry Runs](#13-dry-runs)
  - [1.4 C++](#14-c)
  - [1.5 Don't-Memorize Model](#15-dont-memorize-model)
- [2. Divisor Analysis — CSES 2182](#2-divisor-analysis--cses-2182)
  - [2.1 Divisor Exponent Model](#21-divisor-exponent-model)
  - [2.2 Number of Divisors](#22-number-of-divisors)
  - [2.3 Sum of Divisors](#23-sum-of-divisors)
  - [2.4 Product of Divisors](#24-product-of-divisors)
  - [2.5 Why Exponents Use MOD-1](#25-why-exponents-use-mod-1)
  - [2.6 Full Dry Run — N = 12](#26-full-dry-run--n--12)
  - [2.7 C++](#27-c)
  - [2.8 Don't-Memorize Model](#28-dont-memorize-model)
- [3. Final Revision Card](#3-final-revision-card)

---

# 0. Prerequisites

Keep only the mathematics needed for these two problems.

---

## 0.1 Prime Divisor Fact

A prime number has exactly two positive divisors:

```text
1 and itself
```

Examples:

```text
2, 3, 5, 7, 11, 13, ...
```

Important:

```text
1 is NOT prime.
```

### Key theorem

Every integer greater than `1` has at least one prime divisor.

Example:

```text
18 = 2 × 9
```

So `2` is a prime divisor.

Also:

```text
18 = 3 × 6
```

So `3` is another prime divisor.

This theorem is exactly what makes **Prime Subtraction** collapse to an O(1) check.

---

## 0.2 Prime Factorisation

Every integer greater than `1` can be written as:

```math
N=p_1^{k_1}p_2^{k_2}\cdots p_m^{k_m}
```

where:

```text
p_i = distinct prime factor
k_i = exponent of that prime
```

Example:

```text
12 = 2² × 3¹
```

Another example:

```text
360 = 2³ × 3² × 5
```

CSES Divisor Analysis gives `N` directly in this prime-factor form.

---

## 0.3 Independent Choices

If one independent decision has:

```text
A choices
```

and another has:

```text
B choices
```

then total combinations:

```text
A × B
```

Example for:

```text
12 = 2² × 3
```

A divisor chooses exponent of `2`:

```text
0,1,2
→ 3 choices
```

and exponent of `3`:

```text
0,1
→ 2 choices
```

Total:

```text
3 × 2 = 6 divisors
```

This is the counting principle behind the divisor-count formula.

---

## 0.4 Geometric Progression

For one prime power `p^k`, divisor contributions are:

```text
1, p, p², ..., p^k
```

Their sum is:

```math
S=1+p+p^2+\cdots+p^k
```

Multiply by `p`:

```math
pS=p+p^2+\cdots+p^{k+1}
```

Subtract:

```math
pS-S=p^{k+1}-1
```

Factor:

```math
S(p-1)=p^{k+1}-1
```

Therefore:

```math
S=\frac{p^{k+1}-1}{p-1}
```

### Example

For:

```text
p = 2
k = 2
```

Direct:

```text
1 + 2 + 4 = 7
```

Formula:

```math
\frac{2^3-1}{2-1}=7
```

---

## 0.5 Modular Arithmetic

CSES asks for answers modulo:

```text
MOD = 1,000,000,007
```

Addition:

```math
(A+B)\bmod M
=
((A\bmod M)+(B\bmod M))\bmod M
```

Multiplication:

```math
(AB)\bmod M
=
((A\bmod M)(B\bmod M))\bmod M
```

Subtraction:

```cpp
(a - b + MOD) % MOD
```

The `+MOD` prevents a negative remainder.

---

## 0.6 Binary Exponentiation

We repeatedly need:

```text
p^k mod MOD
```

Naive:

```text
O(k)
```

Binary exponentiation:

```text
O(log k)
```

Core identities:

```text
even exponent:
a^b = (a^(b/2))²

odd exponent:
a^b = (a^(b/2))² × a
```

### C++

```cpp
long long binpow(long long a, long long b, long long mod) {
    long long ans = 1;
    a %= mod;

    while (b > 0) {
        if (b & 1)
            ans = (__int128)ans * a % mod;

        a = (__int128)a * a % mod;
        b >>= 1;
    }

    return ans;
}
```

---

## 0.7 Modular Inverse

Normal division does not work directly under modulo.

Instead:

```text
divide by b
→ multiply by inverse of b
```

The inverse satisfies:

```math
b\cdot b^{-1}\equiv1\pmod M
```

For prime `M`, Fermat gives:

```math
b^{M-1}\equiv1\pmod M
```

Hence:

```math
b^{-1}\equiv b^{M-2}\pmod M
```

C++:

```cpp
long long inv = binpow(b, MOD - 2, MOD);
```

This is used for:

```text
(p^(k+1)-1)/(p-1)
```

in the divisor-sum formula.

---

## 0.8 Fermat Exponent Reduction

For prime modulus `M` and base not divisible by `M`:

```math
a^{M-1}\equiv1\pmod M
```

Therefore:

```math
a^E
\equiv
a^{E\bmod(M-1)}
\pmod M
```

So:

```text
value modulo M
→ exponent may be reduced modulo M-1
```

### Example

```text
2^10 mod 7
```

Since:

```text
7 - 1 = 6
10 mod 6 = 4
```

we can use:

```text
2^4 mod 7
= 16 mod 7
= 2
```

Important for CSES:

```text
MOD = 1e9+7
exponent cycle = MOD-1 = 1e9+6
```

---

# 1. Prime Subtraction — CF 1238A

**Problem Link:** https://codeforces.com/problemset/problem/1238/A

---

## 1.1 Problem Model

Given:

```text
x > y
```

Choose **one prime** `p`.

Subtract the same `p` from `x` any number of times.

Question:

```text
Can we reach y?
```

Example:

```text
x = 42
y = 32
```

Choose:

```text
p = 5
```

Then:

```text
42 - 5 - 5 = 32
```

YES.

---

## 1.2 Derivation

Suppose we subtract prime `p` exactly `k` times.

Then:

```math
x-kp=y
```

Move `y`:

```math
x-y=kp
```

Define:

```text
d = x-y
```

Then:

```math
d=kp
```

So:

```text
d must be divisible by some prime p
```

Now only two cases matter.

---

### Case 1 — `d = 1`

We would need:

```math
1=kp
```

But:

```text
p >= 2
k >= 1
```

so:

```text
k × p >= 2
```

Impossible.

Therefore:

```text
d = 1
→ NO
```

---

### Case 2 — `d > 1`

Every integer greater than `1` has a prime divisor.

Let that divisor be `p`.

Then:

```math
d=kp
```

for some positive integer `k`.

Therefore:

```math
x-kp=y
```

So:

```text
d > 1
→ YES
```

---

### Final Condition

```text
x-y == 1
→ NO

otherwise
→ YES
```

No factorisation is required.

No primality test is required.

---

## 1.3 Dry Runs

### Dry Run 1 — YES

```text
x = 42
y = 32
```

Difference:

```text
d = 10
```

One prime divisor:

```text
p = 5
```

Then:

```text
k = 2
```

Check:

```text
42 - 2×5
= 32
```

YES.

---

### Dry Run 2 — NO

```text
x = 41
y = 40
```

Difference:

```text
d = 1
```

`1` has no prime divisor.

Therefore:

```text
NO
```

---

## 1.4 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        long long x, y;
        cin >> x >> y;

        cout << (x - y == 1 ? "NO\n" : "YES\n");
    }
}
```

Complexity:

```text
Time  : O(1) per test case
Space : O(1)
```

---

## 1.5 Don't-Memorize Model

```text
subtract SAME prime p repeatedly
              |
              v
total subtraction = k×p
              |
              v
x-y = k×p
              |
              v
difference needs a prime divisor
              |
        +-----+-----+
        |           |
       1           >1
        |           |
 no prime       always has
 divisor        prime divisor
        |           |
       NO          YES
```

Memory anchor:

```text
Repeated subtraction
→ model the total difference.
```

---

# 2. Divisor Analysis — CSES 2182

**Problem Link:** https://cses.fi/problemset/task/2182

Given:

```math
N=p_1^{k_1}p_2^{k_2}\cdots p_m^{k_m}
```

compute modulo `1e9+7`:

```text
1. number of divisors
2. sum of divisors
3. product of divisors
```

---

## 2.1 Divisor Exponent Model

Every divisor has the form:

```math
d=p_1^{e_1}p_2^{e_2}\cdots p_m^{e_m}
```

where:

```text
0 <= e_i <= k_i
```

### Example — `N = 12`

```text
12 = 2² × 3
```

Exponent choices:

```text
for 2:
0,1,2

for 3:
0,1
```

Combinations:

```text
2^0 × 3^0 = 1
2^1 × 3^0 = 2
2^2 × 3^0 = 4
2^0 × 3^1 = 3
2^1 × 3^1 = 6
2^2 × 3^1 = 12
```

This one model gives all three formulas.

---

## 2.2 Number of Divisors

For each prime `p_i^k_i`, exponent choices are:

```text
0,1,...,k_i
```

Count:

```text
k_i + 1
```

Choices are independent.

Therefore:

```math
D(N)
=
\prod_i(k_i+1)
```

### Example — `12 = 2² × 3`

```text
2 exponent:
0,1,2
→ 3 choices

3 exponent:
0,1
→ 2 choices
```

So:

```text
D = 3 × 2
  = 6
```

C++ update:

```cpp
numDiv = numDiv * (k + 1) % MOD;
```

---

## 2.3 Sum of Divisors

For one prime power:

```text
p^0, p^1, ..., p^k
```

sum:

```math
1+p+p^2+\cdots+p^k
=
\frac{p^{k+1}-1}{p-1}
```

Across independent prime factors, multiply the contributions:

```math
S(N)
=
\prod_i
\frac{p_i^{k_i+1}-1}{p_i-1}
```

---

### Why multiplication creates all divisors

Take:

```text
N = 6 = 2 × 3
```

Prime contributions:

```text
(1+2)
(1+3)
```

Expand:

```text
1×1 = 1
2×1 = 2
1×3 = 3
2×3 = 6
```

Exactly the divisors:

```text
1,2,3,6
```

So:

```text
sum = 12
```

---

### Modulo implementation

Division by:

```text
p - 1
```

becomes multiplication by its modular inverse.

```cpp
long long numerator =
    (binpow(p, k + 1, MOD) - 1 + MOD) % MOD;

long long inverse =
    binpow(p - 1, MOD - 2, MOD);

long long geometric =
    (__int128)numerator * inverse % MOD;

sumDiv =
    (__int128)sumDiv * geometric % MOD;
```

---

## 2.4 Product of Divisors

This is the part worth understanding carefully.

---

### 2.4.1 Pairing Intuition

Divisors pair as:

```text
d
and
N/d
```

Their product:

```math
d\cdot\frac Nd=N
```

For:

```text
N = 12
```

pairs:

```text
1 × 12 = 12
2 × 6  = 12
3 × 4  = 12
```

Thus:

```text
product = 12³ = 1728
```

This gives intuition, but implementation is easier with the incremental prime-factor model below.

---

### 2.4.2 Incremental Model

Suppose we have already processed some primes.

Let:

```text
C = number of old divisors
P = product of old divisors
```

Now add:

```math
p^k
```

For each old divisor `d`, new divisors are:

```text
d×p^0
d×p^1
...
d×p^k
```

---

### Part A — Old divisor product

The whole old divisor set appears once for every exponent:

```text
0,1,...,k
```

That is:

```text
k+1 times
```

So old product contributes:

```math
P^{k+1}
```

---

### Part B — Contribution of p

For every old divisor:

```text
exponent 0 contributes p^0
exponent 1 contributes p^1
...
exponent k contributes p^k
```

There are `C` old divisors.

Total exponent of `p`:

```math
C(0+1+2+\cdots+k)
```

Use:

```math
0+1+\cdots+k
=
\frac{k(k+1)}2
```

Therefore:

```math
\text{p-exponent}
=
C\cdot\frac{k(k+1)}2
```

---

### Product Recurrence

```math
P_{\text{new}}
=
P_{\text{old}}^{k+1}
\cdot
p^{C_{\text{old}}k(k+1)/2}
```

New divisor count:

```math
C_{\text{new}}
=
C_{\text{old}}(k+1)
```

That is the formula implemented in code.

---

## 2.5 Why Exponents Use MOD-1

We calculate powers like:

```text
p^E mod MOD
```

with:

```text
MOD = 1e9+7
```

Because `MOD` is prime and `p` is not divisible by `MOD`:

```math
p^{MOD-1}\equiv1\pmod{MOD}
```

Therefore:

```math
p^E
\equiv
p^{E\bmod(MOD-1)}
\pmod{MOD}
```

So exponent values should be tracked modulo:

```text
MOD-1
```

not modulo `MOD`.

---

### Important detail — triangular number

We need:

```text
k(k+1)/2
```

`MOD-1` is not prime, so do not divide by `2` through a modular inverse modulo `MOD-1`.

Compute the integer division first:

```cpp
long long triangular =
    (__int128)k * (k + 1) / 2 % (MOD - 1);
```

Then multiply with the old divisor count modulo `MOD-1`.

---

## 2.6 Full Dry Run — N = 12

```text
12 = 2² × 3
```

Divisors:

```text
1,2,3,4,6,12
```

---

### A. Number of divisors

For `2²`:

```text
3 exponent choices
```

For `3¹`:

```text
2 exponent choices
```

Therefore:

```text
D = 3 × 2
  = 6
```

---

### B. Sum of divisors

For `2²`:

```text
1 + 2 + 4 = 7
```

For `3¹`:

```text
1 + 3 = 4
```

So:

```text
S = 7 × 4
  = 28
```

---

### C. Product of divisors

Start:

```text
C = 1
P = 1
```

#### Add `2²`

```text
p = 2
k = 2
```

Old product:

```text
P^(k+1)
= 1³
= 1
```

Prime exponent:

```text
C × (0+1+2)
= 1 × 3
= 3
```

So:

```text
P = 1 × 2³
  = 8
```

New count:

```text
C = 1 × 3
  = 3
```

Current divisors:

```text
1,2,4
```

---

#### Add `3¹`

Old:

```text
C = 3
P = 8
```

Old product contribution:

```text
8² = 64
```

Prime exponent:

```text
C × (0+1)
= 3 × 1
= 3
```

Prime contribution:

```text
3³ = 27
```

Therefore:

```text
P = 64 × 27
  = 1728
```

Final:

```text
count   = 6
sum     = 28
product = 1728
```

---

## 2.7 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;
using i128 = __int128_t;

const int64 MOD = 1'000'000'007LL;
const int64 EXP_MOD = MOD - 1;

int64 binpow(int64 a, int64 b, int64 mod) {
    int64 ans = 1;
    a %= mod;

    while (b > 0) {
        if (b & 1)
            ans = (i128)ans * a % mod;

        a = (i128)a * a % mod;
        b >>= 1;
    }

    return ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    int64 numDiv = 1;
    int64 sumDiv = 1;
    int64 prodDiv = 1;

    // Number of divisors of already processed factors,
    // modulo MOD-1 because it is used as an exponent.
    int64 countExp = 1;

    for (int i = 0; i < n; ++i) {
        int64 p, k;
        cin >> p >> k;

        // 1. Number of divisors
        numDiv =
            (i128)numDiv * ((k + 1) % MOD) % MOD;

        // 2. Sum of divisors
        int64 numerator =
            (binpow(p, k + 1, MOD) - 1 + MOD) % MOD;

        int64 inverse =
            binpow(p - 1, MOD - 2, MOD);

        int64 geometric =
            (i128)numerator * inverse % MOD;

        sumDiv =
            (i128)sumDiv * geometric % MOD;

        // 3. Product of divisors
        int64 triangular =
            (i128)k * (k + 1) / 2 % EXP_MOD;

        int64 exponent =
            (i128)countExp * triangular % EXP_MOD;

        int64 oldPart =
            binpow(prodDiv, k + 1, MOD);

        int64 primePart =
            binpow(p, exponent, MOD);

        prodDiv =
            (i128)oldPart * primePart % MOD;

        // Update old divisor count for future exponents.
        countExp =
            (i128)countExp * ((k + 1) % EXP_MOD) % EXP_MOD;
    }

    cout << numDiv << ' '
         << sumDiv << ' '
         << prodDiv << '\n';
}
```

---

## 2.8 Don't-Memorize Model

### Number of divisors

```text
For each p^k:
choose exponent 0..k
      |
      v
k+1 choices
      |
      v
multiply choices
```

---

### Sum of divisors

```text
For each p^k:
1 + p + ... + p^k
      |
      v
geometric progression
      |
      v
multiply across primes
```

---

### Product of divisors

```text
Old state:
C divisors, product P
       |
       v
Add p^k
       |
       +----------------------+
       |                      |
old set repeats          p powers added
k+1 times               to all C divisors
       |                      |
       v                      v
P^(k+1)             C × (0+1+...+k)
                              |
                              v
                    C × k(k+1)/2
```

Therefore:

```text
new product
=
P^(k+1)
×
p^(C*k(k+1)/2)
```

Memory anchor:

```text
COUNT   → exponent choices
SUM     → geometric series
PRODUCT → old product repeats + new prime contribution
```

---

# 3. Final Revision Card

```text
PRIME SUBTRACTION
-----------------
x - kp = y

=> x-y = kp

difference = 1
→ NO

difference > 1
→ has prime divisor
→ YES


DIVISOR ANALYSIS
----------------
N = Π p_i^k_i

DIVISOR
d = Π p_i^e_i
where 0 <= e_i <= k_i


COUNT
Π(k_i+1)


SUM
Π(1+p_i+...+p_i^k_i)

GP:
(p^(k+1)-1)/(p-1)


PRODUCT
old:
C divisors
product P

add p^k:

newP
=
P^(k+1)
×
p^(C*k(k+1)/2)

newC
=
C(k+1)


MODULAR TOOLS
-------------
p^k mod MOD
→ binpow

division mod prime
→ modular inverse

huge exponent mod MOD
→ reduce exponent mod (MOD-1)
```

> **Core habit:** when the input is a prime factorisation, think in terms of **prime-exponent choices and contributions**, not by reconstructing `N`.
