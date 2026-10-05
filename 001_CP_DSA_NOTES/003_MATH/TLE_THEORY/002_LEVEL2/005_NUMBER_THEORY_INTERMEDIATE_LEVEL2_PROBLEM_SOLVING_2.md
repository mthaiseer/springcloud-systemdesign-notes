# Number Theory — Intermediate Level 2
## Problem Solving 3 — Prime Subtraction & Divisor Analysis

> **Goal:** derive the solution from the mathematics. Do not memorize the final condition or formula first.
>
> **Study flow:** prerequisites → what the problem asks → remove the story → derive → dry run → C++ → recognition.
>
> **Math rendering:** display equations use fenced `math` blocks only. This avoids unsupported display delimiters and macros.

---

# Clickable Table of Contents

- [0. Prerequisites](#0-prerequisites)
  - [0.1 Prime Numbers and Prime Divisors](#01-prime-numbers-and-prime-divisors)
  - [0.2 Prime Factorisation](#02-prime-factorisation)
  - [0.3 Counting Independent Choices](#03-counting-independent-choices)
  - [0.4 Geometric Progression](#04-geometric-progression)
  - [0.5 Modular Arithmetic](#05-modular-arithmetic)
  - [0.6 Binary Exponentiation](#06-binary-exponentiation)
  - [0.7 Modular Inverse](#07-modular-inverse)
  - [0.8 Fermat Exponent Reduction](#08-fermat-exponent-reduction)
- [1. Prime Subtraction — CF 1238A](#1-prime-subtraction--cf-1238a)
  - [1.1 What the Problem Asks](#11-what-the-problem-asks)
  - [1.2 Remove the Story](#12-remove-the-story)
  - [1.3 Key Observation](#13-key-observation)
  - [1.4 Dry Runs](#14-dry-runs)
  - [1.5 C++](#15-c)
  - [1.6 Don't-Memorize Model](#16-dont-memorize-model)
- [2. Divisor Analysis — CSES 2182](#2-divisor-analysis--cses-2182)
  - [2.1 What the Problem Asks](#21-what-the-problem-asks)
  - [2.2 Divisor Representation](#22-divisor-representation)
  - [2.3 Number of Divisors](#23-number-of-divisors)
  - [2.4 Sum of Divisors](#24-sum-of-divisors)
  - [2.5 Product of Divisors — Basic Pairing Intuition](#25-product-of-divisors--basic-pairing-intuition)
  - [2.6 Product of Divisors — Incremental Derivation](#26-product-of-divisors--incremental-derivation)
  - [2.7 Why Exponents Use MOD-1](#27-why-exponents-use-mod-1)
  - [2.8 Full Dry Run — N = 12](#28-full-dry-run--n--12)
  - [2.9 C++](#29-c)
  - [2.10 Don't-Memorize Model](#210-dont-memorize-model)
- [3. Final Recognition Sheet](#3-final-recognition-sheet)

---

# 0. Prerequisites

The two problems use very different ideas:

```text
Prime Subtraction
→ difference
→ repeated subtraction of ONE prime
→ prime divisor existence

Divisor Analysis
→ prime factorisation
→ exponent choices
→ counting / GP / modular inverse / exponent reduction
```

---

## 0.1 Prime Numbers and Prime Divisors

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

---

### Every Integer Greater Than 1 Has a Prime Divisor

Take any integer:

```text
d > 1
```

If `d` itself is prime, we are done.

If `d` is composite, it has a divisor:

```text
1 < a < d
```

If `a` is prime, we found a prime divisor.

If `a` is composite, factor it again.

Eventually we reach a prime.

So:

```text
d > 1
→ d has at least one prime divisor
```

Example:

```text
d = 18

18 = 2 × 9
```

Prime divisor:

```text
2
```

Also:

```text
18 = 3 × 6
```

Prime divisor:

```text
3
```

This tiny theorem is the whole reason **Prime Subtraction** becomes O(1).

---

## 0.2 Prime Factorisation

Every integer greater than `1` has a unique prime-factor representation:

```math
N=p_1^{k_1}p_2^{k_2}\cdots p_m^{k_m}
```

where:

```text
p1, p2, ... = distinct prime factors
k1, k2, ... = their exponents
```

Example:

```text
12 = 2² × 3¹
```

So:

```text
p1 = 2, k1 = 2
p2 = 3, k2 = 1
```

Another example:

```text
360
= 2³ × 3² × 5¹
```

The CSES Divisor Analysis problem gives `N` directly in this form.

---

## 0.3 Counting Independent Choices

Suppose one decision has:

```text
A choices
```

and another independent decision has:

```text
B choices
```

Then total combinations:

```text
A × B
```

Example:

```text
shirt choices = 3
pant choices  = 2

outfits = 3 × 2 = 6
```

For divisors, each prime exponent is an independent choice.

If:

```text
N = 2² × 3¹
```

a divisor may choose exponent of `2` from:

```text
0, 1, 2
```

That is:

```text
3 choices
```

For `3`:

```text
0, 1
```

That is:

```text
2 choices
```

Total divisors:

```text
3 × 2 = 6
```

---

## 0.4 Geometric Progression

For one prime `p`:

```text
1 + p + p² + ... + p^k
```

is a geometric progression.

Let:

```math
S=1+p+p^2+\cdots+p^k
```

Multiply by `p`:

```math
pS=p+p^2+p^3+\cdots+p^{k+1}
```

Subtract the first equation from the second:

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

This becomes the contribution of one prime to the **sum of divisors**.

---

### Example — `p = 2`, `k = 2`

Direct:

```text
1 + 2 + 4 = 7
```

Formula:

```math
\frac{2^3-1}{2-1}
=
\frac{8-1}{1}
=
7
```

---

## 0.5 Modular Arithmetic

The CSES problem asks for answers modulo:

```text
MOD = 1,000,000,007
```

For addition:

```math
(A+B)\bmod M
=
((A\bmod M)+(B\bmod M))\bmod M
```

For multiplication:

```math
(AB)\bmod M
=
((A\bmod M)(B\bmod M))\bmod M
```

For subtraction:

```text
(a - b) may become negative
```

Normalize:

```cpp
(a - b + MOD) % MOD
```

---

## 0.6 Binary Exponentiation

We frequently need:

```text
p^k mod MOD
```

where `k` can be very large.

Naive multiplication:

```text
O(k)
```

Binary exponentiation:

```text
O(log k)
```

Core idea:

```text
if exponent is even:
    a^b = (a^(b/2))²

if exponent is odd:
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

The modular inverse of `b` modulo `M` is a value `x` satisfying:

```math
bx\equiv1\pmod M
```

For prime `M`, Fermat gives:

```math
b^{M-1}\equiv1\pmod M
```

Therefore:

```math
b^{-1}\equiv b^{M-2}\pmod M
```

So:

```cpp
inverse = binpow(b, MOD - 2, MOD);
```

This is needed for the geometric-series denominator:

```text
p - 1
```

in the sum-of-divisors formula.

---

## 0.8 Fermat Exponent Reduction

For prime modulus `M`, when the base is not divisible by `M`:

```math
a^{M-1}\equiv1\pmod M
```

Therefore powers repeat in blocks of:

```text
M - 1
```

So:

```math
a^E
\equiv
a^{E\bmod(M-1)}
\pmod M
```

### Important

For an exponent under modulus `M`:

```text
reduce exponent modulo M-1
```

not:

```text
modulo M
```

---

### Example — `2^10 mod 7`

Here:

```text
M = 7
M - 1 = 6
```

Reduce exponent:

```text
10 mod 6 = 4
```

So:

```math
2^{10}\equiv2^4\pmod7
```

Calculate:

```text
2^4 = 16
16 mod 7 = 2
```

Direct:

```text
2^10 = 1024
1024 mod 7 = 2
```

Same answer.

This is critical for the **product of divisors**, where exponents become enormous.

---

# 1. Prime Subtraction — CF 1238A

**Problem Link:** https://codeforces.com/problemset/problem/1238/A

---

## 1.1 What the Problem Asks

We are given:

```text
x > y
```

We choose **one prime number** `p`.

Then we may subtract the same `p` from `x` any number of times.

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

Subtract twice:

```text
42 - 5 - 5 = 32
```

So:

```text
YES
```

---

## 1.2 Remove the Story

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
d = x - y
```

Then the problem becomes:

```math
d=kp
```

So we need:

```text
a prime p that divides d
```

That is the entire mathematical model.

---

## 1.3 Key Observation

Because:

```text
x > y
```

we know:

```text
d = x-y >= 1
```

Now only two cases exist.

---

### Case 1 — `d = 1`

We need:

```math
1=kp
```

But:

```text
p >= 2
k >= 1
```

So:

```text
k × p >= 2
```

It can never equal `1`.

Therefore:

```text
d = 1
→ NO
```

---

### Case 2 — `d > 1`

Every integer greater than `1` has a prime divisor.

Suppose `p` is any prime divisor of `d`.

Then:

```math
d=kp
```

for some positive integer `k`.

Therefore subtract `p` exactly `k` times:

```math
x-kp
=
x-d
=
y
```

So:

```text
d > 1
→ YES
```

---

### Final Condition

```text
x - y == 1
→ NO

otherwise
→ YES
```

No primality test is needed.

No factorisation is needed.

---

## 1.4 Dry Runs

### Example 1 — `100, 98`

```text
x = 100
y = 98
```

Difference:

```text
d = 100 - 98
  = 2
```

Since:

```text
2 > 1
```

answer:

```text
YES
```

Choose prime:

```text
p = 2
```

Subtract once:

```text
100 - 2 = 98
```

---

### Example 2 — `42, 32`

Difference:

```text
42 - 32 = 10
```

Factor:

```text
10 = 2 × 5
```

Choose:

```text
p = 5
k = 2
```

Then:

```text
42 - 5 - 5
= 32
```

Answer:

```text
YES
```

Important:

```text
We choose ONE prime and reuse it.
```

---

### Example 3 — Very Large Difference

```text
x = 1,000,000,000,000,000,000
y = 1
```

Difference:

```text
999,999,999,999,999,999
```

This is greater than `1`.

So immediately:

```text
YES
```

No need to factor this huge number.

For example it is divisible by `3`, so one valid choice is:

```text
p = 3
```

repeated:

```text
333,333,333,333,333,333 times
```

---

### Example 4 — Impossible

```text
x = 41
y = 40
```

Difference:

```text
d = 1
```

No prime divides `1`.

Answer:

```text
NO
```

---

### Example 5 — Another YES

```text
x = 20
y = 14
```

Difference:

```text
d = 6
```

Possible prime divisor:

```text
p = 2
```

Number of subtractions:

```text
k = 3
```

Check:

```text
20 - 2 - 2 - 2
= 14
```

YES.

We could also choose:

```text
p = 3
k = 2
```

---

## 1.5 C++

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

        if (x - y == 1)
            cout << "NO\n";
        else
            cout << "YES\n";
    }
}
```

Complexity:

```text
Time  : O(1) per test case
Space : O(1)
```

---

## 1.6 Don't-Memorize Model

Do not memorize:

```text
if x-y == 1 → NO
```

Derive it:

```text
subtract same prime p, k times
            |
            v
x - kp = y
            |
            v
x - y = kp
            |
            v
difference must have a prime divisor
            |
       +----+----+
       |         |
   d = 1       d > 1
       |         |
 no prime      always has
 divisor       prime divisor
       |         |
      NO        YES
```

Memory anchor:

```text
Repeated subtraction
→ look at total difference.
```

---

# 2. Divisor Analysis — CSES 2182

**Problem Link:** https://cses.fi/problemset/task/2182

We are given `N` by its prime factorisation.

We must output modulo:

```text
MOD = 1,000,000,007
```

three values:

```text
1. number of divisors
2. sum of divisors
3. product of divisors
```

---

## 2.1 What the Problem Asks

Input describes:

```math
N=p_1^{k_1}p_2^{k_2}\cdots p_m^{k_m}
```

Example:

```text
2 2
3 1
```

means:

```math
N=2^2\cdot3^1=12
```

Divisors of `12`:

```text
1, 2, 3, 4, 6, 12
```

Therefore:

```text
count   = 6
sum     = 28
product = 1728
```

The problem is to compute these without constructing `N` or listing all divisors.

---

## 2.2 Divisor Representation

Suppose:

```math
N=p_1^{k_1}p_2^{k_2}\cdots p_m^{k_m}
```

Any divisor `d` must look like:

```math
d=p_1^{e_1}p_2^{e_2}\cdots p_m^{e_m}
```

where:

```text
0 <= e1 <= k1
0 <= e2 <= k2
...
0 <= em <= km
```

### Example — `N = 12`

```text
12 = 2² × 3¹
```

For prime `2` choose exponent:

```text
0, 1, 2
```

For prime `3` choose:

```text
0, 1
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

These are exactly all divisors.

This **exponent-choice model** powers all three formulas.

---

## 2.3 Number of Divisors

For:

```math
N=p_1^{k_1}p_2^{k_2}\cdots p_m^{k_m}
```

For prime `p1`, exponent can be:

```text
0,1,...,k1
```

Number of choices:

```text
k1 + 1
```

For `p2`:

```text
k2 + 1 choices
```

Choices are independent.

Therefore:

```math
D(N)
=
(k_1+1)(k_2+1)\cdots(k_m+1)
```

---

### Example — `12 = 2² × 3`

For `2`:

```text
0,1,2
→ 3 choices
```

For `3`:

```text
0,1
→ 2 choices
```

So:

```text
number of divisors
= 3 × 2
= 6
```

Correct:

```text
1,2,3,4,6,12
```

---

### C++ Update

```cpp
numDiv = numDiv * (k + 1) % MOD;
```

---

## 2.4 Sum of Divisors

Again:

```math
N=p_1^{k_1}p_2^{k_2}\cdots p_m^{k_m}
```

For one prime `p^k`, a divisor may contain:

```text
p^0, p^1, p^2, ..., p^k
```

Its sum of possible contributions is:

```math
1+p+p^2+\cdots+p^k
```

For multiple independent primes, multiply these sums:

```math
S(N)
=
(1+p_1+\cdots+p_1^{k_1})
(1+p_2+\cdots+p_2^{k_2})
\cdots
```

Why multiplication?

Because expanding the product creates every possible divisor exactly once.

---

### Example — `N = 6 = 2 × 3`

For prime `2`:

```text
1 + 2
```

For prime `3`:

```text
1 + 3
```

Multiply:

```math
(1+2)(1+3)
```

Expand:

```math
1\cdot1
+
2\cdot1
+
1\cdot3
+
2\cdot3
```

Values:

```text
1 + 2 + 3 + 6
```

These are exactly all divisors of `6`.

Sum:

```text
12
```

---

### Geometric-Series Formula

For one prime:

```math
1+p+p^2+\cdots+p^k
=
\frac{p^{k+1}-1}{p-1}
```

Therefore:

```math
S(N)
=
\prod_i
\frac{p_i^{k_i+1}-1}{p_i-1}
```

---

### Example — `12 = 2² × 3`

Prime `2` contribution:

```text
1 + 2 + 4
= 7
```

Prime `3` contribution:

```text
1 + 3
= 4
```

Multiply:

```text
7 × 4
= 28
```

Check:

```text
1+2+3+4+6+12
= 28
```

---

### Modulo Division

We cannot use normal division after taking modulo.

For:

```math
\frac{p^{k+1}-1}{p-1}
```

compute:

```text
numerator
× modular inverse of (p-1)
```

Since `MOD` is prime:

```math
(p-1)^{-1}
\equiv
(p-1)^{MOD-2}
\pmod{MOD}
```

C++ contribution:

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

## 2.5 Product of Divisors — Basic Pairing Intuition

Divisors naturally pair:

```text
d
and
N/d
```

Their product is:

```math
d\cdot\frac Nd=N
```

Example for `12`:

```text
1 × 12 = 12
2 × 6  = 12
3 × 4  = 12
```

There are:

```text
6 divisors
→ 3 pairs
```

So product:

```text
12 × 12 × 12
= 12³
= 1728
```

This gives intuition for:

```text
product of divisors ≈ N^(D/2)
```

But when the divisor count is odd, `N` is a perfect square and the middle divisor `sqrt(N)` needs special treatment.

For implementation, the lecture develops a cleaner **incremental prime-factor recurrence**, which avoids awkward square-root cases.

---

## 2.6 Product of Divisors — Incremental Derivation

Suppose we have already processed some prime factors.

Let:

```text
C = number of divisors generated so far
P = product of those divisors
```

Now add a new prime factor:

```math
p^k
```

For every old divisor `d`, new divisors are:

```text
d × p^0
d × p^1
...
d × p^k
```

---

### Part 1 — What happens to old product P?

For exponent `0`, all old divisors appear once.

For exponent `1`, all old divisors appear again, multiplied by `p`.

...

For exponent `k`, all old divisors appear again.

So the old divisor product `P` appears:

```text
k + 1 times
```

Contribution:

```math
P^{k+1}
```

---

### Part 2 — How many p factors appear?

For exponent `0`:

```text
each of C old divisors gets p^0
```

Total `p` exponent:

```text
0 × C
```

For exponent `1`:

```text
1 × C
```

For exponent `2`:

```text
2 × C
```

...

For exponent `k`:

```text
k × C
```

Total:

```math
C(0+1+2+\cdots+k)
```

We know:

```math
0+1+2+\cdots+k
=
\frac{k(k+1)}2
```

Therefore total exponent of `p`:

```math
C\cdot\frac{k(k+1)}2
```

---

### Product Recurrence

So after adding `p^k`:

```math
P_{\text{new}}
=
P_{\text{old}}^{k+1}
\cdot
p^{C_{\text{old}}k(k+1)/2}
```

And divisor count updates as:

```math
C_{\text{new}}
=
C_{\text{old}}(k+1)
```

This is the main product-of-divisors formula used in the implementation.

---

### Small Example — Add `2²`

Initially no primes processed:

```text
old divisors = {1}

C = 1
P = 1
```

Add:

```text
2²
```

New divisors:

```text
1,2,4
```

Formula:

```math
P_{\text{new}}
=
1^3
\cdot
2^{1(0+1+2)}
```

```math
P_{\text{new}}
=
2^3
=
8
```

Indeed:

```text
1 × 2 × 4
= 8
```

New count:

```text
1 × 3 = 3
```

---

### Add `3¹`

Old divisors:

```text
1,2,4
```

Old:

```text
C = 3
P = 8
```

Add:

```text
3¹
```

New divisor groups:

```text
old × 3^0:
1,2,4

old × 3^1:
3,6,12
```

Formula:

```math
P_{\text{new}}
=
8^2
\cdot
3^{3(0+1)}
```

```math
P_{\text{new}}
=
64\cdot27
=
1728
```

Correct product:

```text
1×2×3×4×6×12
= 1728
```

---

## 2.7 Why Exponents Use MOD-1

In the product recurrence, exponent:

```math
C\cdot\frac{k(k+1)}2
```

can become enormous.

We need:

```text
p^E mod MOD
```

Since:

```text
MOD is prime
```

and the CSES prime factors satisfy:

```text
p < MOD
```

so `p` is not divisible by `MOD`.

Fermat gives:

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

So maintain the previous divisor count twice conceptually:

```text
numDiv mod MOD
→ for the answer

countExp mod (MOD-1)
→ when used inside an exponent
```

---

### Very Important

Do **not** do:

```text
exponent % MOD
```

for Fermat exponent reduction.

Use:

```text
exponent % (MOD-1)
```

---

### Computing `k(k+1)/2`

`MOD-1` is not prime, so do not try to divide by `2` using a modular inverse modulo `MOD-1`.

Compute the integer division first:

```cpp
long long tri =
    (__int128)k * (k + 1) / 2 % (MOD - 1);
```

Then:

```cpp
long long exponent =
    (__int128)countExp * tri % (MOD - 1);
```

---

## 2.8 Full Dry Run — N = 12

Input prime factorisation:

```text
2² × 3¹
```

We want:

```text
number of divisors
sum of divisors
product of divisors
```

---

### A. Number of Divisors

For `2²`:

```text
exponent choices:
0,1,2

count = 3
```

For `3¹`:

```text
0,1

count = 2
```

Therefore:

```text
D = 3 × 2
  = 6
```

---

### B. Sum of Divisors

For `2²`:

```text
1 + 2 + 4
= 7
```

For `3¹`:

```text
1 + 3
= 4
```

Multiply:

```text
S = 7 × 4
  = 28
```

---

### C. Product of Divisors

Start:

```text
P = 1
C = 1
```

---

#### Process `2²`

```text
p = 2
k = 2
```

Triangular exponent:

```text
0+1+2 = 3
```

New product:

```math
P
=
1^3
\cdot
2^{1\cdot3}
```

```text
P = 8
```

New divisor count:

```text
C = 1 × 3
  = 3
```

Current divisors:

```text
1,2,4
```

---

#### Process `3¹`

```text
p = 3
k = 1
```

Triangular sum:

```text
0+1 = 1
```

Old:

```text
P = 8
C = 3
```

New product:

```math
P
=
8^2
\cdot
3^{3\cdot1}
```

Calculate:

```text
8² = 64
3³ = 27
```

So:

```text
P = 64 × 27
  = 1728
```

New count:

```text
C = 3 × 2
  = 6
```

Final:

```text
number  = 6
sum     = 28
product = 1728
```

Matches the divisor list:

```text
1,2,3,4,6,12
```

---

## 2.9 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;
using i128 = __int128_t;

const int64 MOD = 1'000'000'007LL;
const int64 PHI = MOD - 1;  // Fermat exponent cycle

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

    // Number of divisors of already processed primes,
    // stored modulo MOD-1 because it is used in exponents.
    int64 countExp = 1;

    for (int i = 0; i < n; ++i) {
        int64 p, k;
        cin >> p >> k;

        // -------------------------------------------------
        // 1. Number of divisors
        // -------------------------------------------------
        numDiv =
            (i128)numDiv * ((k + 1) % MOD) % MOD;

        // -------------------------------------------------
        // 2. Sum of divisors
        //
        // 1 + p + ... + p^k
        // = (p^(k+1)-1)/(p-1)
        // -------------------------------------------------
        int64 numerator =
            (binpow(p, k + 1, MOD) - 1 + MOD) % MOD;

        int64 inverse =
            binpow(p - 1, MOD - 2, MOD);

        int64 geometric =
            (i128)numerator * inverse % MOD;

        sumDiv =
            (i128)sumDiv * geometric % MOD;

        // -------------------------------------------------
        // 3. Product of divisors
        //
        // newProd =
        // oldProd^(k+1)
        // *
        // p^(oldCount * k(k+1)/2)
        // -------------------------------------------------

        // Compute k(k+1)/2 as an integer first,
        // then reduce modulo MOD-1.
        int64 triangular =
            (i128)k * (k + 1) / 2 % PHI;

        int64 exponent =
            (i128)countExp * triangular % PHI;

        int64 oldPart =
            binpow(prodDiv, k + 1, MOD);

        int64 primePart =
            binpow(p, exponent, MOD);

        prodDiv =
            (i128)oldPart * primePart % MOD;

        // Update divisor count for future exponents.
        countExp =
            (i128)countExp * ((k + 1) % PHI) % PHI;
    }

    cout << numDiv << ' '
         << sumDiv << ' '
         << prodDiv << '\n';
}
```

---

### Complexity

Let:

```text
n = number of distinct prime factors
```

Each factor uses a constant number of binary exponentiations.

Each binary exponentiation costs:

```text
O(log k)
or
O(log MOD)
```

So the overall complexity is approximately:

```text
O(n log MOD + n log k)
```

which is easily efficient for the problem constraints.

Memory:

```text
O(1)
```

besides input variables.

---

## 2.10 Don't-Memorize Model

The formulas become much easier if you start from **how a divisor is formed**.

---

### Number of Divisors

```text
N = p1^k1 p2^k2 ...
        |
        v
for each prime choose exponent
        |
        v
0 ... ki
        |
        v
ki + 1 choices
        |
        v
multiply independent choices
```

Result:

```text
Π(ki+1)
```

---

### Sum of Divisors

```text
for prime p^k
possible contributions:
1, p, p², ..., p^k
        |
        v
sum them
        |
        v
geometric progression
        |
        v
(p^(k+1)-1)/(p-1)
        |
        v
multiply across primes
```

---

### Product of Divisors

```text
already processed:
C divisors
product = P
        |
        v
add new prime p^k
        |
        v
old divisor set appears
k+1 times
        |
        v
P^(k+1)
        |
        +
        |
p exponent across all groups
        |
        v
C × (0+1+...+k)
        |
        v
C × k(k+1)/2
```

Result:

```text
newP
=
P^(k+1)
×
p^(C*k(k+1)/2)
```

For the huge exponent:

```text
Fermat
→ reduce exponent modulo MOD-1
```

Memory anchor:

```text
COUNT = choices
SUM   = geometric series
PRODUCT = repeated old product + exponent contribution
```

---

# 3. Final Recognition Sheet

| Problem signal | Think |
|---|---|
| subtract the same value repeatedly | total difference |
| repeated value must be prime | difference must have a prime divisor |
| positive integer `> 1` | has a prime divisor |
| number given by prime factorisation | work directly with prime exponents |
| count divisors | independent exponent choices |
| sum divisors | geometric progression per prime |
| divide under prime modulus | modular inverse |
| huge power modulo prime | binary exponentiation + Fermat |
| product of all divisors | divisor pairing / contribution recurrence |
| exponent used modulo `1e9+7` | reduce exponent modulo `1e9+6` when Fermat applies |

---

# Master Mental Model

```text
PRIME SUBTRACTION
-----------------
x -> y by subtracting p repeatedly
          |
          v
x - y = k*p
          |
          v
difference needs prime divisor
          |
     +----+----+
     |         |
    1         >1
     |         |
    NO        YES


DIVISOR ANALYSIS
----------------
N = Π p_i^k_i
       |
       v
A divisor chooses exponent
0..k_i for each prime
       |
       +--------------------------+
       |             |            |
     COUNT          SUM        PRODUCT
       |             |            |
 choices        geometric     contribution
 multiply         series       recurrence
```

> **Core habit:** when a problem gives prime factorisation, think in terms of **choosing prime exponents**, not in terms of generating the original number.
