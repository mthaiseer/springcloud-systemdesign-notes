# Advanced Number Theory — Level 3
## Number Theory 1 — Factorisation, Sieve, Prime Factorisation, SPF & Divisor Functions

> **Goal:** understand *why* each technique works. Do not memorize code first.
>
> **Flow:** prerequisites → observation → derivation → dry run → C++ → recognition.
>
> **Math rendering:** display equations use fenced `math` blocks only to avoid LaTeX rendering issues.

---

# Clickable Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Factorisation — Find All Factors](#1-factorisation--find-all-factors)
- [2. Prime Factorisation](#2-prime-factorisation)
- [3. Sieve of Eratosthenes](#3-sieve-of-eratosthenes)
- [4. Smallest Prime Factor — SPF](#4-smallest-prime-factor--spf)
- [5. Number of Divisors](#5-number-of-divisors)
- [6. Sum of Divisors](#6-sum-of-divisors)
- [7. Modular Arithmetic Revision](#7-modular-arithmetic-revision)
- [8. Binary Modular Exponentiation](#8-binary-modular-exponentiation)
- [9. Technique Selection](#9-technique-selection)
- [10. Final Don't-Memorize Model](#10-final-dont-memorize-model)

---

# 0. Prerequisites

The lecture connects these ideas:

```text
Factors
   ↓
Prime factors
   ↓
Sieve
   ↓
SPF
   ↓
Number/Sum of divisors
```

It also revises modular arithmetic and exponentiation because these tools appear frequently in number-theory problems.

---

## 0.1 Divisibility

`d` divides `N` when:

```cpp
N % d == 0
```

Mathematically:

```math
d \mid N
```

Example:

```text
18 % 3 = 0
→ 3 divides 18
```

But:

```text
18 % 4 = 2
→ 4 does not divide 18
```

---

## 0.2 Prime vs Composite

A prime has exactly two positive divisors:

```text
1 and itself
```

Examples:

```text
2, 3, 5, 7, 11, 13, ...
```

Composite examples:

```text
4  = 2 × 2
6  = 2 × 3
18 = 2 × 9
```

Important:

```text
1 is NOT prime.
```

---

## 0.3 Factor Pairs

If:

```math
p \mid N
```

then:

```text
q = N / p
```

is also a factor.

Therefore:

```math
p\cdot q=N
```

Example:

```text
N = 18

1 × 18
2 × 9
3 × 6
```

This pairing is what reduces factor search from `N` to `sqrt(N)`.

---

## 0.4 Why sqrt(N) Appears

Suppose:

```math
p\cdot q=N
```

Assume both are larger than `sqrt(N)`:

```text
p > sqrt(N)
q > sqrt(N)
```

Then:

```math
p\cdot q
>
\sqrt{N}\cdot\sqrt{N}
```

So:

```math
p\cdot q>N
```

But:

```math
p\cdot q=N
```

Contradiction.

Therefore:

```text
In every factor pair,
at least one factor <= sqrt(N).
```

### Recognition

```text
divisors / factors of one number
→ search only to sqrt(N)
```

---

## 0.5 Prime-Factor Representation

Every integer `N > 1` can be written as:

```math
N=p_1^{k_1}p_2^{k_2}\cdots p_m^{k_m}
```

Example:

```text
24
= 2 × 2 × 2 × 3
= 2³ × 3
```

Another:

```text
18
= 2 × 3 × 3
= 2¹ × 3²
```

This representation is the base of:

```text
number of divisors
sum of divisors
SPF factorisation
```

---

## 0.6 Modular Arithmetic Basics

For modulus `M`:

### Addition

```math
(A+B)\bmod M
=
((A\bmod M)+(B\bmod M))\bmod M
```

### Subtraction

```math
(A-B)\bmod M
=
((A\bmod M)-(B\bmod M)+M)\bmod M
```

### Multiplication

```math
(AB)\bmod M
=
((A\bmod M)(B\bmod M))\bmod M
```

Division is special:

```text
A / B mod M
```

usually needs a modular inverse.

---

## 0.7 Exponent Basics

If exponent is even:

```math
a^{2k}=(a^k)^2
```

If exponent is odd:

```math
a^{2k+1}=(a^k)^2a
```

These identities allow us to halve the exponent repeatedly.

That gives binary exponentiation:

```text
O(log exponent)
```

---

# 1. Factorisation — Find All Factors

Goal:

```text
Given N, find every positive divisor.
```

Example:

```text
N = 18
```

Divisors:

```text
1, 2, 3, 6, 9, 18
```

---

## 1.1 Naive Way

Try:

```text
1,2,3,...,N
```

and test:

```cpp
if (N % i == 0)
```

Complexity:

```text
O(N)
```

For large `N`, this is unnecessary.

---

## 1.2 Efficient Way — Use Factor Pairs

For every:

```text
i <= sqrt(N)
```

if:

```text
N % i == 0
```

then both are divisors:

```text
i
N/i
```

---

### Perfect-Square Warning

For:

```text
N = 36
```

when:

```text
i = 6
```

the pair is:

```text
6 × 6
```

Do not insert `6` twice.

---

## 1.3 Dry Run — N = 18

```text
sqrt(18) ≈ 4.24
```

Check:

```text
i = 1,2,3,4
```

| `i` | `18 % i` | Pair |
|---:|---:|---|
| 1 | 0 | `1, 18` |
| 2 | 0 | `2, 9` |
| 3 | 0 | `3, 6` |
| 4 | 2 | none |

All factors:

```text
1,2,3,6,9,18
```

---

## 1.4 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<long long> findFactors(long long n) {
    vector<long long> factors;

    for (long long d = 1; d <= n / d; ++d) {
        if (n % d != 0)
            continue;

        factors.push_back(d);

        long long other = n / d;

        if (other != d)
            factors.push_back(other);
    }

    sort(factors.begin(), factors.end());
    return factors;
}
```

Complexity:

```text
O(sqrt(N))
```

---

## 1.5 Don't-Memorize Model

```text
Need all factors
      |
      v
N = p × q
      |
      v
one factor <= sqrt(N)
      |
      v
scan only smaller side
      |
      v
paired factor = N/p
```

Memory anchor:

```text
Factor pairs reduce the search.
```

---

# 2. Prime Factorisation

Goal:

```text
Represent N as primes and their powers.
```

Example:

```text
18 = 2 × 3 × 3
   = 2 × 3²
```

---

## 2.1 Why the Smallest Factor Greater Than 1 Is Prime

Let `x > 1` be the smallest factor of `N`.

Suppose `x` were composite:

```math
x=a\cdot b
```

with:

```text
1 < a < x
```

Since `a` divides `x` and `x` divides `N`, `a` also divides `N`.

But:

```text
a < x
```

which contradicts that `x` was the smallest factor greater than `1`.

Therefore:

```text
smallest factor > 1 is always prime
```

This is why trial division works naturally.

---

## 2.2 Trial Division

Start:

```text
d = 2
```

If:

```text
n % d == 0
```

then `d` is a prime factor.

Keep dividing:

```cpp
while (n % d == 0)
```

because the same prime may occur several times.

Example:

```text
24
→ /2 = 12
→ /2 = 6
→ /2 = 3
```

So:

```text
24 = 2³ × 3
```

---

## 2.3 Why the Remaining n > 1 Is Prime

After removing all possible small factors, suppose:

```text
n > 1
```

If this remaining `n` were composite, it would have a factor:

```text
<= sqrt(n)
```

But such a factor would already have been found.

Contradiction.

Therefore:

```text
remaining n > 1
→ remaining n is prime
```

---

## 2.4 Distinct vs Multiplicity

For:

```text
18 = 2 × 3 × 3
```

With multiplicity:

```text
2, 3, 3
```

Distinct only:

```text
2, 3
```

Prime-power form:

```text
(2,1)
(3,2)
```

---

## 2.5 Dry Run — N = 24

Start:

```text
n = 24
```

Take `2`:

```text
24 → 12
12 → 6
6  → 3
```

So exponent of `2` is:

```text
3
```

Remaining:

```text
3
```

which is prime.

Final:

```text
24 = 2³ × 3
```

---

## 2.6 C++ — Prime + Exponent

```cpp
vector<pair<long long,int>> primeFactorization(long long n) {
    vector<pair<long long,int>> ans;

    for (long long d = 2; d <= n / d; ++d) {
        if (n % d != 0)
            continue;

        int count = 0;

        while (n % d == 0) {
            n /= d;
            ++count;
        }

        ans.push_back({d, count});
    }

    if (n > 1)
        ans.push_back({n, 1});

    return ans;
}
```

Worst case:

```text
O(sqrt(N))
```

---

## 2.7 Don't-Memorize Model

```text
Need prime representation
      |
      v
find smallest divisor
      |
      v
smallest divisor > 1 is prime
      |
      v
remove all copies
      |
      v
repeat
      |
      v
leftover > 1 is prime
```

---

# 3. Sieve of Eratosthenes

Goal:

```text
Find primality for all values 1...N.
```

---

## 3.1 Why We Need a Sieve

Suppose there are many queries:

```text
is x prime?
```

and every:

```text
x <= 10^6
```

Trial division for every query repeats work.

Instead:

```text
precompute primality once
```

Then each query becomes an array lookup.

---

## 3.2 Core Idea

Initially:

```text
2,3,4,...,N
```

are candidates.

`2` is prime.

Therefore:

```text
4,6,8,10,...
```

are composite.

Next unmarked value:

```text
3
```

is prime.

Mark:

```text
6,9,12,15,...
```

Continue.

Mental model:

```text
prime p
   |
   v
multiples of p > p
   |
   v
composite
```

---

## 3.3 Why Start From p²

When processing prime `p`, smaller multiples:

```text
2p,3p,...,(p-1)p
```

have already been marked by smaller prime factors.

Example for `p = 5`:

```text
10 = 2×5
15 = 3×5
20 = 4×5
```

already handled.

First possibly new multiple:

```text
25 = 5²
```

Therefore:

```text
start from p²
```

---

## 3.4 Dry Run — N = 20

Prime `2` marks:

```text
4,6,8,10,12,14,16,18,20
```

Prime `3` marks from `9`:

```text
9,12,15,18
```

Next useful prime is `5`, but:

```text
5² > 20
```

so all composites are already covered.

Primes:

```text
2,3,5,7,11,13,17,19
```

---

## 3.5 C++

```cpp
vector<bool> buildSieve(int n) {
    vector<bool> isPrime(n + 1, true);

    if (n >= 0) isPrime[0] = false;
    if (n >= 1) isPrime[1] = false;

    for (long long p = 2; p * p <= n; ++p) {
        if (!isPrime[p])
            continue;

        for (long long j = p * p; j <= n; j += p) {
            isPrime[j] = false;
        }
    }

    return isPrime;
}
```

Complexity:

```text
O(N log log N)
```

Space:

```text
O(N)
```

---

## 3.6 Don't-Memorize Model

```text
Many primality queries
       |
       v
individual checks repeat work
       |
       v
prime p
       |
       v
mark all multiples composite
       |
       v
reuse result
```

Memory anchor:

```text
Sieve marks.
```

---

# 4. Smallest Prime Factor — SPF

## 4.1 What SPF Stores

```text
spf[x] = smallest prime factor of x
```

Examples:

```text
spf[4]  = 2
spf[9]  = 3
spf[15] = 3
spf[17] = 17
```

For a prime:

```text
spf[p] = p
```

---

## 4.2 Why SPF Helps

Suppose we must factorise many values:

```text
q queries
x <= 10^6
```

Trial division per query can be too slow.

With SPF:

```text
prime = spf[x]
```

gives the next factor immediately.

Then:

```text
x /= prime
```

Repeat.

So:

```text
search
```

becomes:

```text
lookup
```

---

## 4.3 Build SPF Using Sieve

Initialize:

```text
spf[i] = i
```

Then process primes from small to large.

For each multiple:

```text
if spf[multiple] is unchanged:
    spf[multiple] = current prime
```

Because primes are processed smallest first, the first assigned prime is the smallest factor.

---

## 4.4 Dry Run — X = 42

Assume SPF table exists.

```text
x = 42
```

```text
spf[42] = 2
42 / 2 = 21
```

```text
spf[21] = 3
21 / 3 = 7
```

```text
spf[7] = 7
7 / 7 = 1
```

Therefore:

```text
42 = 2 × 3 × 7
```

---

## 4.5 C++

### Build SPF

```cpp
vector<int> buildSPF(int n) {
    vector<int> spf(n + 1);

    for (int i = 0; i <= n; ++i)
        spf[i] = i;

    for (long long p = 2; p * p <= n; ++p) {
        if (spf[p] != p)
            continue;

        for (long long j = p * p; j <= n; j += p) {
            if (spf[j] == j)
                spf[j] = (int)p;
        }
    }

    return spf;
}
```

---

### Factorise With SPF

```cpp
vector<pair<int,int>> factorizeWithSPF(
    int x,
    const vector<int>& spf
) {
    vector<pair<int,int>> ans;

    while (x > 1) {
        int p = spf[x];
        int count = 0;

        while (x > 1 && spf[x] == p) {
            x /= p;
            ++count;
        }

        ans.push_back({p, count});
    }

    return ans;
}
```

Precompute:

```text
O(N log log N)
```

Per factorisation:

```text
O(log x)
```

in terms of removed prime factors.

---

## 4.6 Don't-Memorize Model

```text
Many factorisation queries
       |
       v
trial division searches repeatedly
       |
       v
precompute smallest prime factor
       |
       v
spf[x]
       |
       v
lookup → divide → repeat
```

Memory anchor:

```text
Trial division searches.
SPF remembers.
```

---

# 5. Number of Divisors

Let:

```math
N=p_1^{e_1}p_2^{e_2}\cdots p_k^{e_k}
```

A divisor chooses an exponent for every prime.

For:

```text
p_i^e_i
```

possible exponents are:

```text
0,1,...,e_i
```

Number of choices:

```text
e_i + 1
```

Since choices are independent:

```math
d(N)
=
(e_1+1)(e_2+1)\cdots(e_k+1)
```

---

## Example — N = 18

```text
18 = 2¹ × 3²
```

For `2`:

```text
0 or 1
→ 2 choices
```

For `3`:

```text
0,1,2
→ 3 choices
```

Total:

```text
2 × 3 = 6
```

Divisors:

```text
1,2,3,6,9,18
```

---

## C++

```cpp
long long numberOfDivisors(
    const vector<pair<long long,int>>& factors
) {
    long long ans = 1;

    for (auto [p, e] : factors) {
        ans *= (e + 1);
    }

    return ans;
}
```

---

# 6. Sum of Divisors

Again:

```math
N=p_1^{e_1}p_2^{e_2}\cdots p_k^{e_k}
```

For one prime power:

```text
p^e
```

a divisor may contain:

```text
1, p, p², ..., p^e
```

So contribution:

```math
1+p+p^2+\cdots+p^e
```

This is a geometric progression.

Closed form:

```math
1+p+p^2+\cdots+p^e
=
\frac{p^{e+1}-1}{p-1}
```

Across prime factors, multiply contributions:

```math
\sigma(N)
=
\prod_i
\left(
1+p_i+p_i^2+\cdots+p_i^{e_i}
\right)
```

---

## Example — N = 18

```text
18 = 2¹ × 3²
```

Contribution from `2`:

```text
1 + 2 = 3
```

Contribution from `3²`:

```text
1 + 3 + 9 = 13
```

Multiply:

```text
3 × 13 = 39
```

Check:

```text
1 + 2 + 3 + 6 + 9 + 18
= 39
```

So:

```text
sum of divisors = 39
```

---

## C++

```cpp
long long sumOfDivisors(
    const vector<pair<long long,int>>& factors
) {
    long long ans = 1;

    for (auto [p, e] : factors) {
        long long contribution = 1;
        long long power = 1;

        for (int i = 1; i <= e; ++i) {
            power *= p;
            contribution += power;
        }

        ans *= contribution;
    }

    return ans;
}
```

For modular problems, combine this idea with modular multiplication and exponentiation.

---

# 7. Modular Arithmetic Revision

For modulus `M`:

```math
(A+B)\bmod M
=
((A\bmod M)+(B\bmod M))\bmod M
```

```math
(A-B)\bmod M
=
((A\bmod M)-(B\bmod M)+M)\bmod M
```

```math
(AB)\bmod M
=
((A\bmod M)(B\bmod M))\bmod M
```

Division:

```text
special case
→ modular inverse when valid
```

Recognition:

```text
+, -, ×
→ reduce operands safely

/
→ inverse
```

---

# 8. Binary Modular Exponentiation

Goal:

```text
a^n mod M
```

Base:

```math
a^0=1
```

Even exponent:

```math
a^n=(a^{n/2})^2
```

Odd exponent:

```math
a^n=(a^{(n-1)/2})^2a
```

Each step halves the exponent.

Complexity:

```text
O(log n)
```

---

## Dry Run — 3^13

Start:

```text
res = 1
base = 3
exp = 13
```

`13` odd:

```text
res = 3
base = 9
exp = 6
```

`6` even:

```text
res = 3
base = 81
exp = 3
```

`3` odd:

```text
res = 3 × 81
base = 81²
exp = 1
```

`1` odd:

```text
multiply once more
exp = 0
```

Stop.

Under modulo, apply `% M` after every multiplication.

---

## C++

```cpp
long long binpow(
    long long a,
    long long b,
    long long mod
) {
    long long res = 1;
    a %= mod;

    while (b > 0) {
        if (b & 1)
            res = (__int128)res * a % mod;

        a = (__int128)a * a % mod;
        b >>= 1;
    }

    return res;
}
```

---

# 9. Technique Selection

| Problem asks for | Technique |
|---|---|
| all divisors of one `N` | factor-pair scan |
| prime factors of one/few `N` | trial division |
| primality for many values up to `MAXN` | sieve |
| factorisation for many bounded values | SPF |
| divisor count | prime exponents + `(e+1)` |
| divisor sum | geometric sum per prime |
| huge power | binary exponentiation |
| arithmetic modulo `M` | modular rules |

Decision model:

```text
ONE / FEW VALUES
----------------
factors?
→ sqrt scan

prime factors?
→ trial division


MANY BOUNDED VALUES
-------------------
primality?
→ sieve

factorisation?
→ SPF
```

---

# 10. Final Don't-Memorize Model

```text
FACTORISATION
N = p × q
→ one factor <= sqrt(N)
→ scan only the smaller side


PRIME FACTORISATION
smallest factor > 1 is prime
→ remove all copies
→ repeat


SIEVE
prime p
→ multiples are composite
→ mark once globally


SPF
sieve + remember smallest prime factor
→ lookup instead of search


NUMBER OF DIVISORS
N = Π p_i^e_i
→ choose exponent 0..e_i
→ multiply (e_i+1)


SUM OF DIVISORS
for p^e:
1+p+...+p^e
→ geometric progression
→ multiply contributions


BINARY EXPONENTIATION
even → square half
odd  → square half × base
→ halve exponent every step
```

> **Final memory anchor:**  
> **Factor pairs reduce the search. Sieve precomputes primality. SPF precomputes the next factor. Prime exponents turn divisor problems into counting formulas.**
