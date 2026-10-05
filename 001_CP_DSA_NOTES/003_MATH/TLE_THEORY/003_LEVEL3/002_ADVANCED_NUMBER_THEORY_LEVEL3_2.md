# Advanced Number Theory — Level 3
## Number Theory 2 — Modular Arithmetic, Euclidean Algorithm, GCD, Euler Totient, Euler/Fermat & Modular Inverse

> **Goal:** understand the mathematical reason behind each formula before using it in code.
>
> **Study flow:** prerequisites → theorem/observation → derivation → dry run → C++ → recognition.
>
> **Math rendering:** display equations use fenced `math` blocks only to avoid Markdown/LaTeX rendering issues.
>
> **Source scope:** this note follows the lecture topics: modular arithmetic revision, binary exponentiation revision, Euclidean algorithm, GCD properties, Euler's Totient Function, Euler's Theorem, Fermat's Theorem, and modular inverse using Euler/Fermat.

---

# Clickable Table of Contents

- [0. Prerequisites](#0-prerequisites)
  - [0.1 Quotient, Remainder and Modulo](#01-quotient-remainder-and-modulo)
  - [0.2 Divisibility](#02-divisibility)
  - [0.3 GCD and Coprime Numbers](#03-gcd-and-coprime-numbers)
  - [0.4 Prime Factorisation](#04-prime-factorisation)
  - [0.5 Exponent Rules](#05-exponent-rules)
- [1. Modular Arithmetic — Revision](#1-modular-arithmetic--revision)
  - [1.1 Addition](#11-addition)
  - [1.2 Subtraction](#12-subtraction)
  - [1.3 Multiplication](#13-multiplication)
  - [1.4 Division Needs an Inverse](#14-division-needs-an-inverse)
  - [1.5 Don't-Memorize Model](#15-dont-memorize-model)
- [2. Binary Exponentiation — Revision](#2-binary-exponentiation--revision)
  - [2.1 Why Brute Force Is Slow](#21-why-brute-force-is-slow)
  - [2.2 Binary Representation Idea](#22-binary-representation-idea)
  - [2.3 Iterative Dry Run](#23-iterative-dry-run)
  - [2.4 C++](#24-c)
- [3. Euclidean Algorithm](#3-euclidean-algorithm)
  - [3.1 GCD Definition](#31-gcd-definition)
  - [3.2 Why Subtraction Preserves GCD](#32-why-subtraction-preserves-gcd)
  - [3.3 From Repeated Subtraction to Modulo](#33-from-repeated-subtraction-to-modulo)
  - [3.4 Dry Run — gcd(17,2)](#34-dry-run--gcd172)
  - [3.5 Complexity Intuition](#35-complexity-intuition)
  - [3.6 C++](#36-c)
- [4. Important GCD and LCM Properties](#4-important-gcd-and-lcm-properties)
  - [4.1 Basic GCD Properties](#41-basic-gcd-properties)
  - [4.2 GCD and Prime Exponents](#42-gcd-and-prime-exponents)
  - [4.3 LCM and Prime Exponents](#43-lcm-and-prime-exponents)
  - [4.4 GCD × LCM = a × b](#44-gcd--lcm--a--b)
  - [4.5 Example — 18 and 36](#45-example--18-and-36)
- [5. Euler's Totient Function](#5-eulers-totient-function)
  - [5.1 Definition](#51-definition)
  - [5.2 Prime Case](#52-prime-case)
  - [5.3 Prime-Power Case](#53-prime-power-case)
  - [5.4 Multiplicative Property](#54-multiplicative-property)
  - [5.5 General Formula](#55-general-formula)
  - [5.6 Example — phi(12)](#56-example--phi12)
  - [5.7 C++](#57-c)
- [6. Euler's Theorem](#6-eulers-theorem)
  - [6.1 Statement](#61-statement)
  - [6.2 Why It Creates an Exponent Cycle](#62-why-it-creates-an-exponent-cycle)
  - [6.3 Example](#63-example)
- [7. Fermat's Little Theorem](#7-fermats-little-theorem)
  - [7.1 Statement](#71-statement)
  - [7.2 Fermat as a Special Case of Euler](#72-fermat-as-a-special-case-of-euler)
  - [7.3 Example](#73-example)
- [8. Exponent Reduction](#8-exponent-reduction)
  - [8.1 General Euler Reduction](#81-general-euler-reduction)
  - [8.2 Prime Modulus Reduction](#82-prime-modulus-reduction)
  - [8.3 What Is Wrong](#83-what-is-wrong)
  - [8.4 Nested Exponents](#84-nested-exponents)
- [9. Modular Inverse](#9-modular-inverse)
  - [9.1 Definition](#91-definition)
  - [9.2 Inverse Using Euler](#92-inverse-using-euler)
  - [9.3 Inverse Using Fermat](#93-inverse-using-fermat)
  - [9.4 Dry Run — Inverse of 3 mod 7](#94-dry-run--inverse-of-3-mod-7)
  - [9.5 C++](#95-c)
- [10. Practice Connection — CSES Exponentiation II](#10-practice-connection--cses-exponentiation-ii)
- [11. Bonus Topics Mentioned in the Lecture](#11-bonus-topics-mentioned-in-the-lecture)
- [12. Technique Selection](#12-technique-selection)
- [13. Final Don't-Memorize Model](#13-final-dont-memorize-model)

---

# 0. Prerequisites

Before the Level 3 results, keep these five ideas clear:

```text
Modulo
Divisibility
GCD / Coprime
Prime factorisation
Exponent rules
```

They are reused throughout the lecture.

---

## 0.1 Quotient, Remainder and Modulo

For positive `a` and `m`:

```text
a = q × m + r
```

where:

```text
0 <= r < m
```

Then:

```text
a % m = r
```

### Example

```text
17 = 3 × 5 + 2
```

Therefore:

```math
17\bmod5=2
```

### Repeated-subtraction view

Modulo can also be seen as repeatedly subtracting the modulus until the remaining value is smaller than it.

Example:

```text
15 mod 2

15 → 13 → 11 → 9 → 7 → 5 → 3 → 1

answer = 1
```

The lecture uses this intuition before moving to the algebraic form.

---

## 0.2 Divisibility

```math
d\mid n
```

means:

```text
n % d == 0
```

Example:

```text
3 | 12
```

because:

```text
12 % 3 = 0
```

Divisibility is central to GCD and prime factorisation.

---

## 0.3 GCD and Coprime Numbers

`gcd(a,b)` is the greatest positive integer dividing both `a` and `b`.

Example:

```text
gcd(12,18) = 6
```

Two numbers are **coprime** when:

```math
\gcd(a,b)=1
```

Example:

```text
gcd(8,15)=1
```

Coprimality is the key condition in Euler's Theorem.

---

## 0.4 Prime Factorisation

Write a number as:

```math
n=p_1^{a_1}p_2^{a_2}\cdots p_k^{a_k}
```

Example:

```text
18 = 2¹ × 3²
36 = 2² × 3²
```

GCD and LCM become especially simple in this representation:

```text
GCD → minimum exponents
LCM → maximum exponents
```

---

## 0.5 Exponent Rules

Useful identities:

```math
a^{x+y}=a^xa^y
```

For even exponent:

```math
a^{2k}=(a^k)^2
```

For odd exponent:

```math
a^{2k+1}=(a^k)^2a
```

These identities produce binary exponentiation.

---

# 1. Modular Arithmetic — Revision

The lecture first revises the standard operations under modulo.

---

## 1.1 Addition

Formula:

```math
(A+B)\bmod M
=
((A\bmod M)+(B\bmod M))\bmod M
```

### Why?

Write:

```text
A = xM + rA
B = yM + rB
```

Then:

```math
A+B
=
(x+y)M+(r_A+r_B)
```

The multiple of `M` disappears under modulo.

Only:

```text
rA + rB
```

matters.

---

### Example

```text
A = 17
B = 8
M = 5
```

Reduce:

```text
17 % 5 = 2
8  % 5 = 3
```

Then:

```text
(2 + 3) % 5
= 5 % 5
= 0
```

So:

```math
(17+8)\bmod5=0
```

---

## 1.2 Subtraction

Formula:

```math
(A-B)\bmod M
=
((A\bmod M)-(B\bmod M)+M)\bmod M
```

Why `+M`?

To normalize a negative remainder.

### Example

```text
A = 11
B = 8
M = 5
```

Reduce:

```text
11 % 5 = 1
8  % 5 = 3
```

Subtract:

```text
1 - 3 = -2
```

Normalize:

```text
-2 + 5 = 3
```

Therefore:

```math
(11-8)\bmod5=3
```

---

## 1.3 Multiplication

Formula:

```math
(AB)\bmod M
=
((A\bmod M)(B\bmod M))\bmod M
```

### Example

```text
A = 17
B = 13
M = 5
```

Reduce:

```text
17 % 5 = 2
13 % 5 = 3
```

Then:

```text
2 × 3 = 6
6 % 5 = 1
```

So:

```math
(17\cdot13)\bmod5=1
```

---

## 1.4 Division Needs an Inverse

The lecture emphasizes that division is different.

This is **not** a normal modular rule:

```text
(A/B) % M
```

Instead:

```text
division by B
→ multiplication by B inverse
```

So:

```math
\frac AB
\equiv
A\cdot B^{-1}
\pmod M
```

The modular inverse is covered later using Euler/Fermat.

---

## 1.5 Don't-Memorize Model

```text
+, -, ×
→ complete multiples of M disappear
→ keep only remainders

/
→ cannot simply divide remainders
→ need modular inverse
```

---

# 2. Binary Exponentiation — Revision

Goal:

```text
compute a^b
```

efficiently.

---

## 2.1 Why Brute Force Is Slow

Naive:

```cpp
product = 1;

for (int i = 1; i <= b; ++i)
    product *= a;
```

Complexity:

```text
O(b)
```

For:

```text
b = 10^9
```

this is too slow.

The lecture highlights that binary exponentiation needs only about:

```text
log2(b)
```

steps.

---

## 2.2 Binary Representation Idea

Example:

```text
b = 7
```

Binary:

```text
7 = 4 + 2 + 1
```

So:

```math
a^7=a^4a^2a
```

We can generate:

```text
a¹
a²
a⁴
a⁸
...
```

by repeatedly squaring.

Only the powers corresponding to set bits of `b` are multiplied into the answer.

---

## 2.3 Iterative Dry Run

Compute:

```text
3^13
```

Binary:

```text
13 = 8 + 4 + 1
```

Start:

```text
res = 1
base = 3
exp = 13
```

### Step 1 — exp = 13, odd

Use current base:

```text
res = 1 × 3 = 3
```

Square:

```text
base = 9
```

Halve:

```text
exp = 6
```

### Step 2 — exp = 6, even

Do not multiply `res`.

Square:

```text
base = 81
```

Halve:

```text
exp = 3
```

### Step 3 — exp = 3, odd

```text
res = 3 × 81
```

Square base, halve exponent.

### Step 4 — exp = 1, odd

Use the final required power.

Then:

```text
exp = 0
```

Stop.

The exponent is halved every iteration:

```text
O(log b)
```

---

## 2.4 C++

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

Recognition:

```text
huge exponent
→ binary exponentiation
```

---

# 3. Euclidean Algorithm

The lecture next moves to fast GCD computation.

---

## 3.1 GCD Definition

```text
gcd(a,b)
```

means:

```text
greatest common divisor of a and b
```

Example:

```text
gcd(18,36)=18
```

---

## 3.2 Why Subtraction Preserves GCD

Suppose:

```text
g = gcd(a,b)
```

Then:

```text
g divides a
g divides b
```

So we can write:

```text
a = xg
b = yg
```

Subtract:

```text
a-b
= xg-yg
= (x-y)g
```

So `g` also divides:

```text
a-b
```

Therefore:

```math
\gcd(a,b)=\gcd(b,a-b)
```

for the appropriate ordering.

This is the core Euclidean observation.

---

## 3.3 From Repeated Subtraction to Modulo

Suppose:

```text
a > b
```

Repeated subtraction:

```text
a-b
a-2b
a-3b
...
```

until the remainder is less than `b`.

That final remainder is:

```text
a % b
```

So instead of many subtractions:

```math
\gcd(a,b)=\gcd(b,a\bmod b)
```

This is the Euclidean Algorithm.

Base case:

```math
\gcd(a,0)=a
```

---

## 3.4 Dry Run — gcd(17,2)

Using repeated subtraction would be slow:

```text
17 → 15 → 13 → 11 → 9 → 7 → 5 → 3 → 1
```

Euclid compresses all those subtractions:

```text
17 % 2 = 1
```

So:

```text
gcd(17,2)
= gcd(2,1)
```

Next:

```text
2 % 1 = 0
```

So:

```text
gcd(2,1)
= gcd(1,0)
= 1
```

Answer:

```text
1
```

---

## 3.5 Complexity Intuition

The lecture notes that the values shrink very quickly.

Euclidean Algorithm runs in:

```text
O(log(min(a,b)))
```

A useful intuition is that after a small number of remainder operations, the larger value loses at least a constant fraction of its size.

---

## 3.6 C++

### Recursive

```cpp
long long gcdRecursive(long long a, long long b) {
    if (b == 0)
        return a;

    return gcdRecursive(b, a % b);
}
```

### Iterative

```cpp
long long gcdIterative(long long a, long long b) {
    while (b != 0) {
        a %= b;
        swap(a, b);
    }

    return a;
}
```

In competitive programming:

```cpp
std::gcd(a, b)
```

is usually enough.

---

# 4. Important GCD and LCM Properties

The lecture collects several results useful across problems.

---

## 4.1 Basic GCD Properties

Symmetry:

```math
\gcd(a,b)=\gcd(b,a)
```

Zero:

```math
\gcd(a,0)=a
```

Associativity:

```math
\gcd(a,b,c)
=
\gcd(\gcd(a,b),c)
```

So array GCD can be accumulated:

```cpp
long long g = 0;

for (long long x : a)
    g = std::gcd(g, x);
```

Adding more numbers cannot increase the GCD:

```math
\gcd(a,b)
\ge
\gcd(a,b,c)
```

---

## 4.2 GCD and Prime Exponents

Suppose:

```math
a=\prod p_i^{x_i}
```

and:

```math
b=\prod p_i^{y_i}
```

Then the GCD contains:

```text
minimum exponent of each prime
```

So:

```math
\gcd(a,b)
=
\prod p_i^{\min(x_i,y_i)}
```

---

## 4.3 LCM and Prime Exponents

LCM needs enough of every prime to be divisible by both numbers.

So it keeps:

```text
maximum exponent of each prime
```

```math
\mathrm{lcm}(a,b)
=
\prod p_i^{\max(x_i,y_i)}
```

---

## 4.4 GCD × LCM = a × b

For each prime `p`:

```text
GCD exponent = min(x,y)
LCM exponent = max(x,y)
```

Their sum:

```text
min(x,y)+max(x,y)
=
x+y
```

which is exactly the exponent in:

```text
a × b
```

Therefore:

```math
\gcd(a,b)\cdot\mathrm{lcm}(a,b)
=
ab
```

Hence:

```math
\mathrm{lcm}(a,b)
=
\frac{ab}{\gcd(a,b)}
```

Safer C++:

```cpp
long long lcm(long long a, long long b) {
    return (a / std::gcd(a, b)) * b;
}
```

---

## 4.5 Example — 18 and 36

Prime forms:

```text
18 = 2¹ × 3²
36 = 2² × 3²
```

GCD takes minimum exponents:

```text
2¹ × 3²
= 18
```

LCM takes maximum:

```text
2² × 3²
= 36
```

Check:

```text
gcd × lcm
= 18 × 36

a × b
= 18 × 36
```

Correct.

---

# 5. Euler's Totient Function

The lecture defines:

```text
phi(n)
```

as the count of integers up to `n` that are coprime with `n`.

---

## 5.1 Definition

```math
\phi(n)
=
\#\{x:1\le x\le n,\ \gcd(x,n)=1\}
```

Example:

```text
n = 12
```

Numbers:

```text
1,2,3,4,5,6,7,8,9,10,11,12
```

Coprime with `12`:

```text
1,5,7,11
```

Therefore:

```math
\phi(12)=4
```

---

## 5.2 Prime Case

If `p` is prime:

```text
1,2,...,p-1
```

are all coprime with `p`.

Only `p` itself shares factor `p`.

Therefore:

```math
\phi(p)=p-1
```

Example:

```text
p = 7
```

Coprime values:

```text
1,2,3,4,5,6
```

So:

```math
\phi(7)=6
```

---

## 5.3 Prime-Power Case

Consider:

```math
p^k
```

Numbers not coprime with `p^k` are exactly those divisible by `p`.

From:

```text
1 ... p^k
```

number divisible by `p`:

```math
\frac{p^k}{p}
=
p^{k-1}
```

So:

```math
\phi(p^k)
=
p^k-p^{k-1}
```

Factor:

```math
\phi(p^k)
=
p^k\left(1-\frac1p\right)
```

### Example

```text
5^3 = 125
```

Multiples of `5` from `1...125`:

```text
125 / 5 = 25
```

Therefore:

```text
125 - 25 = 100
```

So:

```math
\phi(125)=100
```

---

## 5.4 Multiplicative Property

The lecture states that `phi` is multiplicative.

If:

```math
\gcd(a,b)=1
```

then:

```math
\phi(ab)=\phi(a)\phi(b)
```

This lets us combine distinct prime-power components.

---

## 5.5 General Formula

If:

```math
n=p_1^{a_1}p_2^{a_2}\cdots p_k^{a_k}
```

then:

```math
\phi(n)
=
n
\left(1-\frac1{p_1}\right)
\left(1-\frac1{p_2}\right)
\cdots
\left(1-\frac1{p_k}\right)
```

Only **distinct prime factors** appear in the product.

---

## 5.6 Example — phi(12)

Factorise:

```text
12 = 2² × 3
```

Distinct primes:

```text
2,3
```

Formula:

```math
\phi(12)
=
12
\left(1-\frac12\right)
\left(1-\frac13\right)
```

Simplify first fraction:

```math
1-\frac12
=
\frac22-\frac12
=
\frac12
```

Second:

```math
1-\frac13
=
\frac33-\frac13
=
\frac23
```

Therefore:

```math
\phi(12)
=
12\cdot\frac12\cdot\frac23
```

Calculate left to right:

```math
12\cdot\frac12=6
```

Then:

```math
6\cdot\frac23=4
```

So:

```math
\phi(12)=4
```

Matches direct counting:

```text
1,5,7,11
```

---

## 5.7 C++

For one number using trial factorisation:

```cpp
long long phi(long long n) {
    long long result = n;
    long long x = n;

    for (long long p = 2; p <= x / p; ++p) {
        if (x % p != 0)
            continue;

        while (x % p == 0)
            x /= p;

        result -= result / p;
    }

    if (x > 1)
        result -= result / x;

    return result;
}
```

Why:

```cpp
result -= result / p;
```

?

Because multiplying by:

```math
1-\frac1p
```

means:

```math
result
-
\frac{result}{p}
```

Complexity for one number:

```text
O(sqrt(n))
```

The lecture also notes that SPF precomputation can speed up many bounded queries.

---

# 6. Euler's Theorem

## 6.1 Statement

If:

```math
\gcd(a,m)=1
```

then:

```math
a^{\phi(m)}
\equiv
1
\pmod m
```

The coprimality condition is essential.

---

## 6.2 Why It Creates an Exponent Cycle

Suppose exponent:

```text
E
```

is very large.

Write:

```text
E = q × phi(m) + r
```

where:

```text
r = E % phi(m)
```

Then:

```math
a^E
=
a^{q\phi(m)+r}
```

Split:

```math
a^E
=
(a^{\phi(m)})^q
a^r
```

Euler gives:

```math
a^{\phi(m)}
\equiv1\pmod m
```

Therefore:

```math
a^E
\equiv
1^q a^r
\pmod m
```

So:

```math
a^E
\equiv
a^{E\bmod\phi(m)}
\pmod m
```

when:

```math
\gcd(a,m)=1
```

---

## 6.3 Example

Take:

```text
a = 5
m = 12
```

Check:

```text
gcd(5,12)=1
```

From earlier:

```text
phi(12)=4
```

Euler says:

```math
5^4\equiv1\pmod{12}
```

Check:

```text
5² = 25
25 % 12 = 1
```

So:

```text
5⁴
= (5²)²
≡ 1²
≡ 1
```

Correct.

---

# 7. Fermat's Little Theorem

## 7.1 Statement

When modulus `p` is prime and:

```text
p does not divide a
```

then:

```math
a^{p-1}\equiv1\pmod p
```

This is the prime-modulus version emphasized in the lecture.

---

## 7.2 Fermat as a Special Case of Euler

For prime `p`:

```math
\phi(p)=p-1
```

Euler:

```math
a^{\phi(p)}\equiv1\pmod p
```

becomes:

```math
a^{p-1}\equiv1\pmod p
```

which is Fermat's Little Theorem.

---

## 7.3 Example

Take:

```text
a = 2
p = 5
```

Then:

```math
2^{5-1}=2^4=16
```

and:

```text
16 % 5 = 1
```

Therefore:

```math
2^4\equiv1\pmod5
```

---

# 8. Exponent Reduction

The lecture explicitly warns against reducing exponents by the wrong modulus.

---

## 8.1 General Euler Reduction

When:

```math
\gcd(a,m)=1
```

use:

```math
a^b
\equiv
a^{b\bmod\phi(m)}
\pmod m
```

The exponent cycle is:

```text
phi(m)
```

not `m`.

---

## 8.2 Prime Modulus Reduction

If `m` is prime:

```math
\phi(m)=m-1
```

So:

```math
a^b
\equiv
a^{b\bmod(m-1)}
\pmod m
```

when the Fermat condition applies.

---

## 8.3 What Is Wrong

The lecture marks the following style as wrong:

```text
reduce the exponent modulo m
```

For powers modulo a prime `m`, the useful cycle from Fermat is:

```text
m - 1
```

So think:

```text
value modulus:
m

exponent cycle:
m-1
```

For a general coprime modulus:

```text
value modulus:
m

exponent cycle:
phi(m)
```

---

## 8.4 Nested Exponents

Suppose:

```text
a^(b^c) mod p
```

and `p` is prime with the required coprimality condition.

Outer exponent only matters modulo:

```text
p-1
```

So first compute:

```text
E = b^c mod (p-1)
```

Then compute:

```text
a^E mod p
```

This is the exact structure behind the lecture's CSES practice connection.

---

# 9. Modular Inverse

## 9.1 Definition

The modular inverse of `a` modulo `m` is a value:

```text
a^-1
```

such that:

```math
a\cdot a^{-1}
\equiv
1
\pmod m
```

Then division becomes:

```math
\frac ba
\equiv
b\cdot a^{-1}
\pmod m
```

An inverse exists in the Euler setting when:

```math
\gcd(a,m)=1
```

---

## 9.2 Inverse Using Euler

Euler:

```math
a^{\phi(m)}
\equiv
1
\pmod m
```

Rewrite:

```math
a\cdot a^{\phi(m)-1}
\equiv
1
\pmod m
```

Compare with inverse definition:

```math
a\cdot a^{-1}
\equiv
1
\pmod m
```

Therefore:

```math
a^{-1}
\equiv
a^{\phi(m)-1}
\pmod m
```

under the Euler coprimality condition.

---

## 9.3 Inverse Using Fermat

If `m` is prime:

```math
a^{m-1}
\equiv
1
\pmod m
```

Rewrite:

```math
a\cdot a^{m-2}
\equiv
1
\pmod m
```

Therefore:

```math
a^{-1}
\equiv
a^{m-2}
\pmod m
```

This is the standard competitive-programming formula for prime modulus.

---

## 9.4 Dry Run — Inverse of 3 mod 7

Since `7` is prime:

```text
inverse
= 3^(7-2) mod 7
= 3^5 mod 7
```

Calculate:

```text
3² = 9
9 % 7 = 2
```

```text
3⁴ mod 7
= 2²
= 4
```

```text
3⁵ mod 7
= 4 × 3
= 12 mod 7
= 5
```

So:

```text
inverse = 5
```

Check:

```text
3 × 5 = 15
15 % 7 = 1
```

Correct.

---

## 9.5 C++

Prime modulus:

```cpp
long long modInversePrime(long long a, long long mod) {
    return binpow(a, mod - 2, mod);
}
```

Usage:

```cpp
long long divideMod(
    long long numerator,
    long long denominator,
    long long mod
) {
    long long inv =
        modInversePrime(denominator, mod);

    return (__int128)(numerator % mod) * inv % mod;
}
```

Use only when the theorem's conditions hold.

---

# 10. Practice Connection — CSES Exponentiation II

The lecture points to:

```text
CSES 1712 — Exponentiation II
```

Target shape:

```text
a^(b^c) mod MOD
```

For prime modulus:

```text
MOD
```

Fermat gives exponent cycle:

```text
MOD - 1
```

So:

```text
Step 1:
e = b^c mod (MOD-1)

Step 2:
answer = a^e mod MOD
```

Both powers are computed with binary exponentiation.

### Recognition

```text
power tower / nested exponent
+ prime modulus
→ reduce inner exponent modulo MOD-1
```

---

# 11. Bonus Topics Mentioned in the Lecture

The final lecture slide lists these as further study rather than core material:

```text
Extended Euclidean Algorithm
Linear Diophantine Equations
Modular Inverse for general M
Chinese Remainder Theorem
Miller-Rabin primality testing
```

These are natural next topics after this note, but they are not developed in this lecture.

---

# 12. Technique Selection

| Signal | Think |
|---|---|
| `(A+B) % M`, `(A-B) % M`, `(A*B) % M` | modular rules |
| division under modulo | modular inverse |
| huge exponent | binary exponentiation |
| GCD of two numbers | Euclidean algorithm |
| array GCD | associativity of GCD |
| GCD/LCM from prime factors | min/max exponents |
| count values coprime with `n` | Euler totient |
| `a^E mod m`, `gcd(a,m)=1` | Euler theorem |
| prime modulus | Fermat's theorem |
| exponent reduction under prime modulus | modulo `m-1` |
| modular inverse under prime modulus | `a^(m-2)` |
| nested exponent `a^(b^c)` mod prime | inner exponent mod `m-1` |

---

# 13. Final Don't-Memorize Model

```text
MODULAR ARITHMETIC
------------------
+, -, ×
→ reduce operands

/
→ inverse


BINARY EXPONENTIATION
---------------------
huge exponent
→ square repeatedly
→ use set bits
→ O(log exponent)


EUCLIDEAN ALGORITHM
-------------------
gcd(a,b)
=
gcd(b,a%b)

Why?
subtracting multiples
does not change common divisors


GCD / LCM
---------
GCD
→ minimum prime exponents

LCM
→ maximum prime exponents


TOTIENT
-------
phi(n)
=
count of values coprime with n

prime p:
phi(p)=p-1

prime power:
phi(p^k)
=
p^k-p^(k-1)

general:
n × product(1-1/p)


EULER
-----
gcd(a,m)=1

a^phi(m)
≡ 1 mod m


FERMAT
------
m prime

a^(m-1)
≡ 1 mod m


EXPONENT REDUCTION
------------------
general coprime modulus:
exponent mod phi(m)

prime modulus:
exponent mod (m-1)


MODULAR INVERSE
---------------
Euler:
a^(-1)
≡ a^(phi(m)-1)

Fermat:
a^(-1)
≡ a^(m-2)
```

> **Final memory anchor:**  
> **Euclid reduces numbers. Totient counts coprimes. Euler/Fermat create exponent cycles. Modular inverse turns division into multiplication.**
