# Math for CP --- Modulo, GCD & LCM

> **Goal:** Understand the model. Do not memorize formulas blindly.
>
> **Rendering rule:** This note intentionally avoids LaTeX. All formulas
> use plain Markdown/Unicode so they render correctly in GitHub and
> normal Markdown viewers.

## Table of Contents

1.  [Modulo Basics](#1-modulo-basics)
2.  [Modular Addition](#2-modular-addition)
3.  [Modular Subtraction](#3-modular-subtraction)
4.  [Modular Multiplication](#4-modular-multiplication)
5.  [GCD](#5-gcd)
6.  [Euclidean Algorithm](#6-euclidean-algorithm)
7.  [LCM](#7-lcm)
8.  [Prime-Exponent Model](#8-prime-exponent-model)
9.  [Recognition Sheet](#9-recognition-sheet)

------------------------------------------------------------------------

# 0. Math Preliminaries

These are the small algebra ideas needed for the derivations later.

## 0.1 Quotient and Remainder

When `A` is divided by `M`:

``` text
A = q × M + r

q = quotient
r = remainder

0 ≤ r < M
```

Example:

``` text
17 ÷ 5

17 = 3 × 5 + 2

q = 3
r = 2

17 mod 5 = 2
```

Think:

``` text
number = complete groups + leftover
A      = q × M           + r
```

------------------------------------------------------------------------

## 0.2 Variables and Subscripts

In:

``` text
A = q1M + r1
B = q2M + r2
```

`q1`, `r1` belong to `A`.

`q2`, `r2` belong to `B`.

Example for `A = 17`, `B = 13`, `M = 5`:

``` text
17 = 3 × 5 + 2

q1 = 3
r1 = 2
```

and:

``` text
13 = 2 × 5 + 3

q2 = 2
r2 = 3
```

So:

``` text
A = q1M + r1
B = q2M + r2
```

is only a compact way of writing quotient + remainder for two numbers.

------------------------------------------------------------------------

## 0.3 Distributive Law

Basic rule:

``` text
a(b + c)
=
ab + ac
```

Example:

``` text
2(3 + 4)
=
2×3 + 2×4
=
6 + 8
=
14
```

For two brackets:

``` text
(a + b)(c + d)
```

multiply every term in the first bracket by every term in the second:

``` text
= ac + ad + bc + bd
```

Example:

``` text
(2 + 3)(4 + 5)

= 2×4 + 2×5 + 3×4 + 3×5

= 8 + 10 + 12 + 15

= 45
```

This is exactly what we use later for:

``` text
(q1M + r1)(q2M + r2)
```

------------------------------------------------------------------------

## 0.4 Factoring Out a Common Factor

Factoring is the reverse of distribution.

``` text
Ma + Mb + Mc
=
M(a + b + c)
```

Example:

``` text
5×3 + 5×7 + 5×2

= 5(3 + 7 + 2)
```

Why useful for modulo?

If we can rewrite something as:

``` text
M × integer
```

then it is completely divisible by `M`.

Therefore:

``` text
(M × integer) mod M = 0
```

------------------------------------------------------------------------

## 0.5 Why Multiples of M Disappear

Examples with `M = 5`:

``` text
5 mod 5  = 0
10 mod 5 = 0
15 mod 5 = 0
20 mod 5 = 0
```

General form:

``` text
kM mod M = 0
```

because:

``` text
kM = k × M + 0
```

The remainder is `0`.

This is the central idea behind modular arithmetic derivations.

------------------------------------------------------------------------

## 0.6 Same Remainder / Congruence Idea

Suppose:

``` text
17 mod 5 = 2
7 mod 5  = 2
```

Both numbers have the same remainder modulo `5`.

We can say:

``` text
17 ≡ 7 (mod 5)
```

Why?

Their difference is:

``` text
17 - 7 = 10
```

and:

``` text
10 mod 5 = 0
```

So their difference is divisible by `5`.

Recognition:

``` text
A ≡ B (mod M)

means

A and B have the same remainder modulo M
```

------------------------------------------------------------------------

## 0.7 Exponents

``` text
M² = M × M
M³ = M × M × M
```

Example:

``` text
5² = 5 × 5 = 25
```

So:

``` text
q1q2M²
```

means:

``` text
q1 × q2 × M × M
```

and it clearly contains `M` as a factor.

------------------------------------------------------------------------

## 0.8 Minimum and Maximum

For two values:

``` text
min(3,5) = 3
max(3,5) = 5
```

This becomes important for prime exponents:

``` text
GCD → minimum exponent
LCM → maximum exponent
```

Example:

``` text
A contains 2³
B contains 2⁵

GCD can contain only 2³
LCM must contain 2⁵
```

------------------------------------------------------------------------

## 0.9 Divisibility Notation

``` text
a divides b
```

means:

``` text
b % a = 0
```

Example:

``` text
4 divides 12

12 % 4 = 0
```

But:

``` text
5 does not divide 12

12 % 5 = 2
```

This idea is needed for understanding GCD and LCM.

------------------------------------------------------------------------

## 0.10 Preliminary Cheat Sheet

``` text
A = qM + r
→ quotient/remainder model

r = A mod M
→ modulo gives remainder

(a+b)(c+d)
→ ac + ad + bc + bd

Ma + Mb
→ M(a+b)

kM mod M
→ 0

A ≡ B (mod M)
→ same remainder modulo M

M²
→ M × M

a divides b
→ b % a = 0

min exponent
→ GCD

max exponent
→ LCM
```

------------------------------------------------------------------------

# 1. Modulo Basics

`A mod M` means: **divide A by M and keep the remainder**.

Division model:

``` text
A = q × M + r

q = quotient
r = remainder

0 ≤ r < M
A mod M = r
```

### Example

``` text
17 = 3 × 5 + 2

17 mod 5 = 2
```

Important idea:

``` text
M mod M     = 0
2M mod M    = 0
3M mod M    = 0
kM mod M    = 0
```

So complete multiples of `M` disappear when taking modulo `M`.

------------------------------------------------------------------------

# 2. Modular Addition

Formula:

``` text
(A + B) mod M
=
((A mod M) + (B mod M)) mod M
```

### Why?

Write:

``` text
A = q1 × M + r1
B = q2 × M + r2
```

Add:

``` text
A + B
= q1M + r1 + q2M + r2

= (q1 + q2)M + (r1 + r2)
```

`(q1 + q2)M` is divisible by `M`, so only the remainders matter.

Therefore:

``` text
(A + B) mod M
=
(r1 + r2) mod M
```

### Example

``` text
(7 + 5) mod 4

7 mod 4 = 3
5 mod 4 = 1

(3 + 1) mod 4
= 4 mod 4
= 0
```

### C++

``` cpp
long long addMod(long long a, long long b, long long mod) {
    return ((a % mod) + (b % mod)) % mod;
}
```

------------------------------------------------------------------------

# 3. Modular Subtraction

Formula:

``` text
(A - B) mod M
=
((A mod M) - (B mod M) + M) mod M
```

Why `+ M`?

Subtraction may produce a negative value.

### Example

``` text
(9 - 11) mod 3

9 mod 3  = 0
11 mod 3 = 2

0 - 2 = -2
-2 + 3 = 1

answer = 1
```

Adding `M` does not change the modular remainder because it adds one
complete group of `M`.

### C++

``` cpp
long long subMod(long long a, long long b, long long mod) {
    return ((a % mod - b % mod) + mod) % mod;
}
```

------------------------------------------------------------------------

# 4. Modular Multiplication

Formula:

``` text
(A × B) mod M
=
((A mod M) × (B mod M)) mod M
```

## Derivation

Start with the division model:

``` text
A = q1M + r1
B = q2M + r2
```

Multiply:

``` text
A × B
=
(q1M + r1)(q2M + r2)
```

Use:

``` text
(a + b)(c + d)
=
ac + ad + bc + bd
```

Therefore:

``` text
A × B
=
q1q2M²
+ q1Mr2
+ q2Mr1
+ r1r2
```

Factor `M` from the first three terms:

``` text
A × B
=
M(q1q2M + q1r2 + q2r1)
+ r1r2
```

The first part is a multiple of `M`, so:

``` text
multiple of M mod M = 0
```

Only this survives:

``` text
r1 × r2
```

Hence:

``` text
(A × B) mod M
=
(r1 × r2) mod M
```

Since:

``` text
r1 = A mod M
r2 = B mod M
```

we get:

``` text
(A × B) mod M
=
((A mod M) × (B mod M)) mod M
```

## Numerical Dry Run --- 17 × 13 mod 5

First reduce each number:

``` text
17 mod 5 = 2
13 mod 5 = 3
```

Multiply the remainders:

``` text
2 × 3 = 6
```

Reduce again:

``` text
6 mod 5 = 1
```

Therefore:

``` text
17 × 13 mod 5 = 1
```

Direct check:

``` text
17 × 13 = 221
221 = 44 × 5 + 1

remainder = 1
```

### C++

``` cpp
long long mulMod(long long a, long long b, long long mod) {
    return (__int128)(a % mod) * (b % mod) % mod;
}
```

------------------------------------------------------------------------

# 5. GCD

**GCD = Greatest Common Divisor**

It is the largest positive number dividing both numbers.

### Example

``` text
8  divisors: 1, 2, 4, 8
12 divisors: 1, 2, 3, 4, 6, 12

common: 1, 2, 4

gcd(8,12) = 4
```

If:

``` text
gcd(a,b) = 1
```

then `a` and `b` are **coprime**.

------------------------------------------------------------------------

# 6. Euclidean Algorithm

Core identity:

``` text
gcd(a,b) = gcd(b, a mod b)
```

### Example --- gcd(48,18)

``` text
48 mod 18 = 12
gcd(48,18) = gcd(18,12)

18 mod 12 = 6
gcd(18,12) = gcd(12,6)

12 mod 6 = 0
gcd(12,6) = gcd(6,0)

answer = 6
```

### Why does it work?

Suppose:

``` text
a = q × b + r
```

Then:

``` text
r = a - q × b
```

Any number dividing both `a` and `b` also divides:

``` text
a - q × b
```

which is `r`.

Therefore:

``` text
gcd(a,b) = gcd(b,r)
```

and since:

``` text
r = a mod b
```

we get:

``` text
gcd(a,b) = gcd(b, a mod b)
```

### C++

``` cpp
long long gcdEuclid(long long a, long long b) {
    while (b != 0) {
        long long r = a % b;
        a = b;
        b = r;
    }
    return a;
}
```

Or:

``` cpp
long long g = std::gcd(a, b);
```

Complexity:

``` text
O(log(min(a,b)))
```

------------------------------------------------------------------------

# 7. LCM

**LCM = Least Common Multiple**

It is the smallest positive number divisible by both numbers.

### Example

``` text
multiples of 8 : 8, 16, 24, 32, ...
multiples of 12: 12, 24, 36, ...

lcm(8,12) = 24
```

Formula:

``` text
gcd(a,b) × lcm(a,b) = a × b
```

Therefore:

``` text
lcm(a,b) = (a / gcd(a,b)) × b
```

### C++

``` cpp
long long lcm(long long a, long long b) {
    return (a / std::gcd(a, b)) * b;
}
```

Divide first to reduce overflow risk.

------------------------------------------------------------------------

# 8. Prime-Exponent Model

Suppose:

``` text
A = 2³ × 3² × 5
B = 2² × 3⁴ × 7
```

For every prime:

``` text
GCD → take MIN exponent
LCM → take MAX exponent
```

Example:

    Prime   A exponent   B exponent   GCD   LCM
  ------- ------------ ------------ ----- -----
        2            3            2     2     3
        3            2            4     2     4
        5            1            0     0     1
        7            0            1     0     1

Therefore:

``` text
GCD = 2² × 3²

LCM = 2³ × 3⁴ × 5 × 7
```

### Why?

``` text
GCD:
must divide BOTH
→ cannot take more copies than the smaller exponent
→ MIN

LCM:
must contain BOTH
→ must have enough copies for the larger exponent
→ MAX
```

This also explains:

``` text
gcd(A,B) × lcm(A,B) = A × B
```

because for every prime:

``` text
min(a,b) + max(a,b) = a + b
```

------------------------------------------------------------------------

# 9. Recognition Sheet

``` text
A mod M
→ remainder after division

(A + B) mod M
→ reduce A and B
→ add
→ reduce again

(A - B) mod M
→ reduce
→ subtract
→ +M if needed
→ reduce again

(A × B) mod M
→ reduce factors
→ multiply remainders
→ reduce again

largest number dividing both
→ GCD

smallest number divisible by both
→ LCM

gcd(a,b)
→ gcd(b, a mod b)

prime exponents:
GCD → MIN
LCM → MAX

lcm(a,b)
→ (a / gcd(a,b)) × b
```

## Final Mental Model

``` text
MODULO
  |
  +-- complete multiples of M disappear
  |
  +-- only remainder matters


GCD / LCM
  |
  +-- represent numbers by prime powers
       |
       +-- GCD = MIN exponent
       |
       +-- LCM = MAX exponent
```

> **Contest habit:** First understand what survives mathematically. Then
> remember the short formula.
