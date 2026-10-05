# Math for CP — Intermediate Level 1
## Primes, Primality Testing, Divisors & Prime Factorisation

> Compact self-study notes. Goal: understand the reasoning, not memorize formulas.

## Clickable Table of Contents

- [0. Preliminary Mathematics](#0-preliminary-mathematics)
  - [0.1 Divisibility](#01-divisibility)
  - [0.2 Quotient and Remainder](#02-quotient-and-remainder)
  - [0.3 Factor Pairs](#03-factor-pairs)
  - [0.4 Prime and Composite Numbers](#04-prime-and-composite-numbers)
  - [0.5 Prime Factorisation](#05-prime-factorisation)
- [1. Why Modulo Appears in CP](#1-why-modulo-appears-in-cp)
- [2. Primality Testing](#2-primality-testing)
- [3. Why Only sqrt(N)?](#3-why-only-sqrtn)
- [4. O(sqrt(N)) Primality Test](#4-osqrtn-primality-test)
- [5. Finding All Divisors in O(sqrt(N))](#5-finding-all-divisors-in-osqrtn)
- [6. Prime Factorisation by Trial Division](#6-prime-factorisation-by-trial-division)
- [7. Trial Division Dry Run](#7-trial-division-dry-run)
  - [7.1 Why the Remaining n > 1 Is Prime](#71-why-the-remaining-n--1-is-prime)
- [8. C++ — Prime Factors with Multiplicity](#8-c--prime-factors-with-multiplicity)
- [9. C++ — (Prime, Exponent) Form](#9-c--prime-exponent-form)
- [10. Why We Keep Dividing n](#10-why-we-keep-dividing-n)
- [11. Common Mistakes](#11-common-mistakes)
- [12. Recognition Model](#12-recognition-model)
- [13. Final Cheat Sheet](#13-final-cheat-sheet)
- [14. Contest Recognition](#14-contest-recognition)

> GitHub generates heading anchors automatically. The links above follow GitHub-style Markdown anchors.

---


# 0. Preliminary Mathematics

## 0.1 Divisibility

`a | b` means **a divides b exactly**.

```text
3 | 12 because 12 = 3 × 4
5 does not divide 12 because 12 % 5 != 0
```

In C++:

```cpp
if (n % d == 0) {
    // d is a divisor of n
}
```

## 0.2 Quotient and Remainder

For positive integers `a` and `m`:

```text
a = q × m + r
0 <= r < m
```

Therefore `a % m = r`.

Example:

```text
17 = 3 × 5 + 2
17 % 5 = 2
```

So modulo `M` maps a non-negative integer into `[0, M-1]`.

## 0.3 Factor Pairs

If `N = A × B`, then `A` and `B` are a factor pair.

Example:

```text
N = 24

1 × 24
2 × 12
3 × 8
4 × 6
```

After `sqrt(24)`, the same pairs only repeat in reverse. This observation powers primality testing, divisor enumeration, and trial division.

## 0.4 Prime and Composite Numbers

A **prime** has exactly two positive divisors: `1` and itself.

```text
2, 3, 5, 7, 11, 13, ...
```

A **composite** number has additional divisors.

```text
12 -> 1, 2, 3, 4, 6, 12
```

`1` is neither prime nor composite.

## 0.5 Prime Factorisation

Every integer greater than `1` can be written as a product of primes.

```text
N = p1^a1 × p2^a2 × ... × pk^ak
```

Example:

```text
24 = 2 × 2 × 2 × 3
   = 2^3 × 3
```

---

# 1. Why Modulo Appears in CP

Large answers can exceed normal integer ranges, so a problem may ask for `answer % M`.

Example from the lecture:

```text
answer = 123456
M      = 103

123456 % 103 = 62
```

The possible residues are:

```text
0, 1, 2, ..., 102
```

## 1.1 Modulo Is Many-to-One

Different values can have the same remainder:

```text
123456 % 103 = 62
123559 % 103 = 62
```

because:

```text
123559 - 123456 = 103
```

General pattern:

```text
a
a + M
a + 2M
...
```

all have the same remainder modulo `M`.

## 1.2 Why Large Prime Moduli Are Common

A larger `M` gives more possible residue values. Common CP moduli include:

```text
1,000,000,007
998,244,353
```

Prime moduli are especially useful because many number-theory operations have clean properties under a prime modulus.

### Why primes come next

```text
prime moduli are useful
        |
        v
understand prime numbers
        |
        +--> Is N prime?
        +--> What divides N?
        +--> What are the prime factors of N?
```

This is the learning chain for the rest of the note.

---

# 2. Primality Testing

Given `N`, determine whether `N` is prime.

### Recognition

```text
ONE number
+
"Is N prime?"
        |
        v
look for a divisor
        |
        v
search only up to sqrt(N)
```

## 2.1 Brute Force

Try every divisor from `2` through `N-1`.

```cpp
bool isPrime(long long n) {
    if (n < 2) return false;

    for (long long i = 2; i < n; ++i) {
        if (n % i == 0) return false;
    }
    return true;
}
```

```text
Time  : O(N)
Space : O(1)
```

---

# 3. Why Only sqrt(N)?

Suppose `N` is composite.

Then it can be written as:

```text
N = A × B
```

## Visual Example — N = 24

```text
small factor     paired factor

    1       ×        24
    2       ×        12
    3       ×         8
    4       ×         6
---------------------------
       sqrt(24) ≈ 4.89
---------------------------
    6       ×         4   <- same pair reversed
    8       ×         3
   12       ×         2
   24       ×         1
```

So after `sqrt(N)`, we only rediscover factor pairs in reverse.

## Step-by-Step Proof

Assume both factors were larger than `sqrt(N)`:

```text
A > sqrt(N)
B > sqrt(N)
```

Multiply both inequalities:

```text
A × B > sqrt(N) × sqrt(N)
```

But:

```text
sqrt(N) × sqrt(N) = N
```

Therefore:

```text
A × B > N
```

However, we started with:

```text
A × B = N
```

Contradiction.

So for every composite `N`:

```text
at least one factor <= sqrt(N)
```

### Don't-memorize model

```text
N = A × B
     |
     v
Can both A and B be > sqrt(N)?
     |
     v
No, because their product would be > N
     |
     v
Therefore one factor must be <= sqrt(N)
```

## Boundary Dry Run — N = 49

```text
sqrt(49) = 7

49 = 7 × 7
```

Check:

```text
i = 2 -> no
i = 3 -> no
i = 4 -> no
i = 5 -> no
i = 6 -> no
i = 7 -> YES
```

Therefore the boundary must be included:

```text
i <= sqrt(N)
```

not:

```text
i < sqrt(N)
```

---

# 4. O(sqrt(N)) Primality Test

```cpp
bool isPrime(long long n) {
    if (n < 2) return false;

    for (long long i = 2; i <= n / i; ++i) {
        if (n % i == 0) return false;
    }
    return true;
}
```

`i <= n / i` is the overflow-safe form of `i * i <= n`.

```text
Time  : O(sqrt(N))
Space : O(1)
```

---

# 5. Finding All Divisors in O(sqrt(N))

### Recognition

```text
ONE number N
+
need all divisors
        |
        v
scan i up to sqrt(N)
        |
        v
if i divides N:
    i and N/i are a factor pair
```

If `i` divides `N`, then `N/i` is its paired divisor.

## Detailed Dry Run — N = 24

We only test:

```text
i <= sqrt(24)

sqrt(24) ≈ 4.89

So:
i = 1, 2, 3, 4
```

Now process each `i`:

| `i` | `24 % i` | Divides? | Pair added |
|---:|---:|:---:|:---|
| 1 | 0 | Yes | `1, 24` |
| 2 | 0 | Yes | `2, 12` |
| 3 | 0 | Yes | `3, 8` |
| 4 | 0 | Yes | `4, 6` |

Collected:

```text
1, 24, 2, 12, 3, 8, 4, 6
```

After sorting:

```text
1, 2, 3, 4, 6, 8, 12, 24
```

### Visual model

```text
24
|
+-- 1 × 24
+-- 2 × 12
+-- 3 ×  8
+-- 4 ×  6
```

## Perfect-Square Edge Case

For `N = 36` and `i = 6`:

```text
i     = 6
N / i = 6
```

Add it only once.

## C++

```cpp
vector<long long> getDivisors(long long n) {
    vector<long long> divisors;

    for (long long i = 1; i <= n / i; ++i) {
        if (n % i == 0) {
            divisors.push_back(i);

            if (i != n / i)
                divisors.push_back(n / i);
        }
    }

    sort(divisors.begin(), divisors.end());
    return divisors;
}
```

```text
Search : O(sqrt(N))
Sort   : O(D log D)
```

---

# 6. Prime Factorisation by Trial Division

### Recognition

```text
ONE number N
+
need prime factors / prime exponents
        |
        v
trial division
```

Goal:

```text
N = p1^a1 × p2^a2 × ... × pk^ak
```

Example:

```text
24 = 2^3 × 3
```

## 6.1 Why the Smallest Divisor Is Prime

Suppose the smallest divisor `d > 1` were composite:

```text
d = a × b
```

Then `a` is smaller than `d` and also divides `N`, contradicting that `d` was the smallest divisor greater than `1`.

So the smallest divisor must be prime.

---

# 7. Trial Division Dry Run

## Detailed Dry Run — N = 24

Start:

```text
n = 24
factors = []
```

Try the smallest possible prime factor:

```text
p = 2
```

| Step | Current `n` | `n % 2` | Action | Factors |
|---:|---:|---:|---|---|
| 1 | 24 | 0 | divide by 2 | `2` |
| 2 | 12 | 0 | divide by 2 | `2, 2` |
| 3 | 6 | 0 | divide by 2 | `2, 2, 2` |
| 4 | 3 | 1 | stop dividing by 2 | `2, 2, 2` |

State transformation:

```text
24
 |
 | /2   take factor 2
 v
12
 |
 | /2   take factor 2
 v
 6
 |
 | /2   take factor 2
 v
 3
```

Now the remaining value is:

```text
n = 3
```

No smaller factor remains, so `3` is the final prime factor.

Final factors:

```text
2, 2, 2, 3
```

Compressed prime-power form:

```text
24 = 2^3 × 3
```

### What changed during the algorithm?

```text
original n : 24
after /2   : 12
after /2   : 6
after /2   : 3
```

We keep shrinking `n`; we do not repeatedly factor the original `24`.

## Detailed Dry Run — N = 52

```text
start: n = 52
```

| Step | Current `n` | Factor | New `n` |
|---:|---:|---:|---:|
| 1 | 52 | 2 | 26 |
| 2 | 26 | 2 | 13 |

Now:

```text
n = 13
```

So:

```text
factors = 2, 2, 13
```

Compressed:

```text
52 = 2^2 × 13
```

## 7.1 Why the Remaining `n > 1` Is Prime

The implementation eventually does:

```cpp
if (n > 1)
    factors.push_back(n);
```

Do not memorize this line without the reason.

Suppose the remaining `n` were composite:

```text
n = A × B
```

From the factor-pair model, a composite number must have at least one factor:

```text
<= sqrt(n)
```

But trial division has already tested and removed every possible factor up to that boundary.

So such a factor cannot still exist.

Therefore:

```text
after trial division:

n > 1
   |
   v
remaining n is prime
```

For `52`:

```text
52
 |
 /2
 v
26
 |
 /2
 v
13

No smaller factor remains.

=> 13 is the final prime factor
```

---

# 8. C++ — Prime Factors with Multiplicity

For `24`, return `2 2 2 3`.

```cpp
vector<long long> primeFactors(long long n) {
    vector<long long> factors;

    for (long long d = 2; d <= n / d; ++d) {
        while (n % d == 0) {
            factors.push_back(d);
            n /= d;
        }
    }

    if (n > 1)
        factors.push_back(n);

    return factors;
}
```

---

# 9. C++ — (Prime, Exponent) Form

For `24`, return:

```text
(2, 3)
(3, 1)
```

```cpp
vector<pair<long long, int>> factorize(long long n) {
    vector<pair<long long, int>> factors;

    for (long long p = 2; p <= n / p; ++p) {
        if (n % p != 0) continue;

        int exponent = 0;

        while (n % p == 0) {
            n /= p;
            ++exponent;
        }

        factors.push_back({p, exponent});
    }

    if (n > 1)
        factors.push_back({n, 1});

    return factors;
}
```

```text
Worst-case time : O(sqrt(N))
```

---

# 10. Why We Keep Dividing n

For `N = 24`:

```text
24 -> 12 -> 6 -> 3
```

We remove each discovered prime factor immediately. This shrinks the remaining number and naturally exposes the next prime factor.

---

# 11. Common Mistakes

### 1. Treating 1 as prime

```text
Prime numbers start from 2.
```

### 2. Missing sqrt(N)

Wrong:

```cpp
i * i < n
```

This misses `7` for `49`.

Use the equivalent overflow-safe condition:

```cpp
i <= n / i
```

### 3. Duplicating the square-root divisor

For `36`, the pair `6 × 6` contributes only one divisor `6`.

### 4. Forgetting the leftover prime

For:

```text
52 = 2 × 2 × 13
```

after removing the `2`s, `13` remains. Therefore finish factorisation with:

```cpp
if (n > 1)
    factors.push_back(...);
```

---

# 12. Recognition Model

Do not memorize three unrelated algorithms. Start with:

```text
N = A × B
```

Both `A` and `B` cannot be greater than `sqrt(N)`.

Therefore:

```text
             N = A × B
                 |
                 v
      one factor <= sqrt(N)
                 |
       +---------+---------+
       |                   |
       v                   v
 prime check          divisor pairs
 O(sqrt(N))           O(sqrt(N))
       |
       v
 trial division
       |
       v
 prime factorisation
```

One mathematical observation gives all three techniques.

---

# 13. Final Cheat Sheet

## Prime check

```text
Try divisors 2 ... sqrt(N).
No divisor -> prime.
```

## All divisors

```text
For each i <= sqrt(N):

if i divides N:
    i is a divisor
    N/i is its paired divisor
```

## Prime factorisation

```text
for p from 2 while p <= n/p:

    while p divides n:
        count p
        n /= p

if n > 1:
    n is the final prime factor
```

| Technique | Time | Space |
|---|---:|---:|
| Brute prime check | `O(N)` | `O(1)` |
| SQRT prime check | `O(sqrt(N))` | `O(1)` |
| Divisor-pair search | `O(sqrt(N))` | output dependent |
| Trial division | `O(sqrt(N))` worst case | factor output |

---

# 14. Contest Recognition

```text
ONE number: is it prime?
    -> sqrt primality test

ONE number: need all divisors?
    -> factor pairs up to sqrt(N)

ONE number: need prime factors?
    -> trial division

MANY numbers in a bounded range?
    -> think sieve / precomputation
```

Final chain:

```text
factor pair
   -> sqrt boundary
   -> primality
   -> divisor enumeration
   -> prime factorisation
```

Understand this chain rather than memorizing implementations independently.
