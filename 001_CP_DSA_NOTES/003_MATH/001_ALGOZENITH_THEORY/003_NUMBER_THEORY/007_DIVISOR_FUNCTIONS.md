# Factorization-Based Counting --- Don't Memorize, Model It

> **Goal:** Once a number is written as prime powers, count divisors and
> their contributions by making independent exponent choices.

## Table of Contents

0.  [Preliminary --- Geometric
    Progression](#0-preliminary--geometric-progression)
1.  [Core Factorization Model](#1-core-factorization-model)
2.  [Divisors of a Number](#2-divisors-of-a-number)
3.  [Number of Divisors --- S0](#3-number-of-divisors--s0)
4.  [Sum of Divisors --- S1](#4-sum-of-divisors--s1)
5.  [Product of Divisors](#5-product-of-divisors)
6.  [Generalized Divisor Sum --- Sk](#6-generalized-divisor-sum--sk)
7.  [Multiplicative Functions](#7-multiplicative-functions)
8.  [C++ Implementation](#8-c-implementation)
9.  [Final Memory Model](#9-final-memory-model)

------------------------------------------------------------------------

# 0. Preliminary --- Geometric Progression

> **Why learn this first?** The sum-of-divisors formula is just a
> geometric progression applied to the powers of each prime.

## 0.1 What is a GP?

A geometric progression has a constant multiplication ratio.

``` text
1, p, p², p³, ..., p^a

each next term = previous term × p
```

Example with `p = 2`:

``` text
1, 2, 4, 8, 16

1 × 2 = 2
2 × 2 = 4
4 × 2 = 8
8 × 2 = 16
```

So the common ratio is `2`.

## 0.2 GP sum formula

For:

``` text
1 + p + p² + ... + p^a
```

the sum is:

$$\frac{p^{a+1}-1}{p-1}$$

### Don't memorize --- derive it

Let:

``` text
S = 1 + p + p² + ... + p^a
```

Multiply by `p`:

``` text
pS = p + p² + p³ + ... + p^(a+1)
```

Subtract the first equation from the second:

``` text
pS - S
=
(p + p² + ... + p^(a+1))
-
(1 + p + p² + ... + p^a)
```

Everything in the middle cancels:

``` text
pS - S = p^(a+1) - 1
```

Factor `S`:

``` text
S(p - 1) = p^(a+1) - 1
```

Divide by `p - 1`:

$$S=\frac{p^{a+1}-1}{p-1}$$

## 0.3 Example

Find:

``` text
1 + 2 + 2² + 2³
```

Here:

``` text
p = 2
a = 3
```

Using the formula:

$$\frac{2^{3+1}-1}{2-1}$$

Step by step:

``` text
= (2⁴ - 1) / 1
= (16 - 1) / 1
= 15
```

Check:

``` text
1 + 2 + 4 + 8 = 15
```

> **Memory model:** prime powers form a GP. This is why GP appears
> naturally in divisor sums.

------------------------------------------------------------------------

# 1. Core Factorization Model

Suppose:

``` text
x = p1^a1 × p2^a2 × ... × pk^ak
```

Example:

``` text
72 = 2³ × 3²
```

A divisor is created by choosing an exponent for **each** prime:

``` text
2 exponent: 0, 1, 2, 3
3 exponent: 0, 1, 2
```

ASCII model:

``` text
             x = 2³ × 3²
                  |
        +---------+---------+
        |                   |
        v                   v
 choose power of 2     choose power of 3
   0,1,2,3               0,1,2
        |                   |
        +---------+---------+
                  |
                  v
              one divisor
```

Examples:

``` text
2⁰ × 3⁰ = 1
2¹ × 3⁰ = 2
2⁰ × 3¹ = 3
2² × 3¹ = 12
2³ × 3² = 72
```

This single model explains `S0`, `S1`, and the generalized divisor sums.

------------------------------------------------------------------------

# 2. Divisors of a Number

`d` is a divisor of `x` when:

``` text
x % d == 0
```

Example:

``` text
x = 12

divisors      = 1, 2, 3, 4, 6, 12
prime factors = 2, 3
```

Divisors can be enumerated in `O(sqrt(x))` by pairing:

``` text
d × (x/d) = x
```

For `12`:

``` text
1 ↔ 12
2 ↔ 6
3 ↔ 4
```

Prime factorization by trial division also takes `O(sqrt(x))`.

With multiplicity, a number has at most `O(log2 x)` prime factors
because the smallest possible prime factor is `2`.

------------------------------------------------------------------------

# 3. Number of Divisors --- S0

Let:

``` text
x = p1^a1 × p2^a2 × ... × pk^ak
```

For prime `p1`, its exponent inside a divisor can be:

``` text
0, 1, 2, ..., a1
```

Number of choices:

``` text
a1 + 1
```

The choices for different primes are independent, so multiply them:

$$S_0(x)=(a_1+1)(a_2+1)\cdots(a_k+1)$$

## Example --- 72

``` text
72 = 2³ × 3²
```

Choices:

``` text
power of 2: 0,1,2,3 -> 4 choices
power of 3: 0,1,2   -> 3 choices
```

Therefore:

``` text
S0(72)
= 4 × 3
= 12
```

### Don't memorize

``` text
prime exponent ai
       |
       v
possible exponent choices
0 ... ai
       |
       v
ai + 1 choices
       |
       v
multiply independent choices
```

------------------------------------------------------------------------

# 4. Sum of Divisors --- S1

Now we want the **sum of the values** of every divisor.

Start with one prime power:

``` text
p^a
```

Possible contributions to a divisor:

``` text
p⁰, p¹, p², ..., p^a
```

Their sum is:

``` text
1 + p + p² + ... + p^a
```

From the preliminary GP section:

$$1+p+p^2+\cdots+p^a=\frac{p^{a+1}-1}{p-1}$$

For multiple distinct primes, multiply their independent contribution
sums:

$$S_1(x)=\prod_i\frac{p_i^{a_i+1}-1}{p_i-1}$$

## Example --- 12

Factorize:

``` text
12 = 2² × 3¹
```

### Prime `2²`

Possible powers:

``` text
2⁰, 2¹, 2²
= 1, 2, 4
```

Contribution sum:

``` text
1 + 2 + 4 = 7
```

Using GP:

``` text
(2^(2+1) - 1) / (2 - 1)
= (2³ - 1) / 1
= 7
```

### Prime `3¹`

Possible powers:

``` text
3⁰, 3¹
= 1, 3
```

Contribution sum:

``` text
1 + 3 = 4
```

### Combine

``` text
S1(12)
= (1+2+4)(1+3)
= 7 × 4
= 28
```

Why multiplication works:

``` text
(1+2+4)(1+3)

= 1 + 3
  + 2 + 6
  + 4 + 12

= sum of every divisor
= 28
```

Check:

``` text
1 + 2 + 3 + 4 + 6 + 12 = 28
```

### Don't memorize

``` text
p^a
 |
 v
possible powers
1,p,p²,...,p^a
 |
 v
sum them using GP
 |
 v
multiply contribution of every prime
```

------------------------------------------------------------------------


## C++ — Sum of Divisors

```cpp
long long divisorSum(long long n) {
    long long ans = 1;

    for (long long p = 2; p <= n / p; ++p) {
        if (n % p != 0) continue;

        long long power = 1;
        long long contribution = 1;

        while (n % p == 0) {
            n /= p;
            power *= p;
            contribution += power;
        }

        ans *= contribution;
    }

    if (n > 1)
        ans *= (1 + n);

    return ans;
}
```

For `12 = 2² × 3`:

```text
2² -> 1+2+4 = 7
3¹ -> 1+3   = 4

answer = 7 × 4 = 28
```

# 5. Product of Divisors

Let:

``` text
D = S0(x)
```

Every divisor `d` can be paired with:

``` text
x / d
```

and:

``` text
d × (x/d) = x
```

## Example --- 12

Divisors:

``` text
1, 2, 3, 4, 6, 12
```

Pair them:

``` text
1 × 12 = 12
2 ×  6 = 12
3 ×  4 = 12
```

There are:

``` text
D = 6 divisors
D/2 = 3 pairs
```

Therefore:

``` text
product = 12 × 12 × 12
        = 12³
        = 12^(6/2)
```

So when `D` is even:

$$\prod_{d\mid x}d=x^{D/2}$$

## What if `x` is a perfect square?

Example:

``` text
x = 36
```

Divisors:

``` text
1, 2, 3, 4, 6, 9, 12, 18, 36
```

`6` pairs with itself:

``` text
6 × 6 = 36
```

There are `D = 9` divisors, so the compact mathematical identity is:

$$\prod_{d\mid x}d=x^{D/2}$$

where `D/2` may be a half-integer mathematically.

For integer computation, avoid fractional exponents. Since a perfect
square has odd `D`:

$$\prod_{d\mid x}d=x^{(D-1)/2}\sqrt{x}$$

For `36`:

``` text
= 36^4 × 6
```

> **Contest caution:** under modulo arithmetic, do not blindly compute a
> fractional exponent. Handle the perfect-square/odd-`D` case
> appropriately.

------------------------------------------------------------------------

# 6. Generalized Divisor Sum --- Sk

The generalized divisor sum is:

$$S_k(x)=\sum_{d\mid x}d^k$$

The same exponent-choice model works.

For:

``` text
x = p1^a1 × ... × pm^am
```

each prime contributes:

``` text
1 + p^k + p^(2k) + ... + p^(ak)
```

Hence:

$$S_k(x)=\prod_i\left(1+p_i^k+p_i^{2k}+\cdots+p_i^{a_i k}\right)$$

This is again a GP.

Special cases:

``` text
k = 0 -> S0(x) = number of divisors
k = 1 -> S1(x) = sum of divisors
```

------------------------------------------------------------------------


## C++ — Generalized Divisor Sum

```cpp
long long intPow(long long a, int k) {
    long long result = 1;
    while (k--)
        result *= a;
    return result;
}

long long divisorPowerSum(long long n, int k) {
    long long ans = 1;

    for (long long p = 2; p <= n / p; ++p) {
        if (n % p != 0) continue;

        long long pk = intPow(p, k);
        long long term = 1;
        long long contribution = 1;

        while (n % p == 0) {
            n /= p;
            term *= pk;
            contribution += term;
        }

        ans *= contribution;
    }

    if (n > 1)
        ans *= (1 + intPow(n, k));

    return ans;
}
```

```text
k = 0 -> number of divisors
k = 1 -> sum of divisors
k = 2 -> sum of squares of divisors
```

# 7. Multiplicative Functions

A function `f` is multiplicative when:

``` text
gcd(a,b) = 1
```

implies:

``` text
f(a × b) = f(a) × f(b)
```

Examples:

``` text
S0(n)   number of divisors
S1(n)   sum of divisors
phi(n)  Euler's totient
```

## Why does factorization help?

Distinct prime powers are coprime:

``` text
x = p1^a1 × p2^a2 × ...
```

So each prime-power block contributes independently.

Example:

``` text
12 = 4 × 3
gcd(4,3) = 1
```

For divisor count:

``` text
S0(4) = 3   -> {1,2,4}
S0(3) = 2   -> {1,3}

S0(12)
= S0(4) × S0(3)
= 3 × 2
= 6
```

### Recognition model

``` text
prime factorization
       |
       v
independent prime-power blocks
       |
       v
calculate each contribution
       |
       v
multiply contributions
```

------------------------------------------------------------------------


# 8. Final Memory Model

Do not start from formulas. Start from:

``` text
x = p1^a1 × p2^a2 × ...
             |
             v
    choose exponent of each prime
             |
     +-------+---------+----------+
     |                 |          |
     v                 v          v
   S0(x)             S1(x)      product
     |                 |          |
 count choices      sum values   pair d
     |                 |        with x/d
     v                 v          |
 Π(ai+1)        multiply GP sums  v
                              each pair = x
```

### Formula sheet

``` text
x = Π p_i^a_i

Number of divisors:
S0(x) = Π(a_i + 1)

Prime-power GP:
1+p+...+p^a = (p^(a+1)-1)/(p-1)

Sum of divisors:
S1(x) = Π[(p_i^(a_i+1)-1)/(p_i-1)]

Product of divisors:
even D -> x^(D/2)
odd D  -> x^((D-1)/2) × sqrt(x)

Generalized divisor sum:
Sk(x) = Σ(d|x) d^k
```

> **Contest habit:** When a problem gives or suggests prime
> factorization, ask: **What choices or contribution does each prime
> exponent make independently?**
