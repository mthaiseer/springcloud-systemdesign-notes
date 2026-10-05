# Number Theory — Intermediate Level 2

> **Goal:** understand the prerequisites and derive each formula instead of memorizing it.
>
> **Study flow:** prerequisite → intuition → derivation → example → C++ → recognition.
>
> **Math rendering:** display equations use fenced `math` blocks to avoid Markdown/LaTeX rendering issues.

---

## Clickable TOC

- [1. Prerequisites](#1-prerequisites)
  - [1.1 Quotient, Remainder and Modulo](#11-quotient-remainder-and-modulo)
  - [1.2 Congruence](#12-congruence)
  - [1.3 Divisibility](#13-divisibility)
  - [1.4 Exponent Rules](#14-exponent-rules)
  - [1.5 Prime and Coprime](#15-prime-and-coprime)
- [2. Modular Operations](#2-modular-operations)
  - [2.1 Addition](#21-addition)
  - [2.2 Subtraction](#22-subtraction)
  - [2.3 Multiplication](#23-multiplication)
- [3. Modular Division and Inverse](#3-modular-division-and-inverse)
- [4. Binary Exponentiation](#4-binary-exponentiation)
  - [4.1 Why Halving Works](#41-why-halving-works)
  - [4.2 Recursive Model](#42-recursive-model)
  - [4.3 Iterative Model](#43-iterative-model)
  - [4.4 Modular Binary Exponentiation](#44-modular-binary-exponentiation)
- [5. GCD](#5-gcd)
  - [5.1 Euclidean Algorithm](#51-euclidean-algorithm)
  - [5.2 Why Euclid Works](#52-why-euclid-works)
  - [5.3 GCD of Many Numbers](#53-gcd-of-many-numbers)
- [6. LCM](#6-lcm)
  - [6.1 Why GCD × LCM = a × b](#61-why-gcd--lcm--a--b)
  - [6.2 LCM of Many Numbers](#62-lcm-of-many-numbers)
- [7. Pigeonhole Principle](#7-pigeonhole-principle)
- [8. Application — Subarray Sum Divisible by N](#8-application--subarray-sum-divisible-by-n)
- [9. Final Recognition Model](#9-final-recognition-model)

---

# 1. Prerequisites

## 1.1 Quotient, Remainder and Modulo

When positive integer `A` is divided by positive integer `M`:

```text
A = q × M + r
```

where:

```text
q = quotient
r = remainder

0 <= r < M
```

### Example

```text
A = 17
M = 5

17 = 3 × 5 + 2
```

So:

```text
q = 3
r = 2
```

and:

```math
17 \bmod 5 = 2
```

### Memory model

```text
number = complete groups of M + leftover
       = q × M                + r
```

Modulo keeps only the leftover.

---

## 1.2 Congruence

Two numbers are congruent modulo `M` if they leave the same remainder.

```math
A \equiv B \pmod M
```

### Example

```text
17 mod 5 = 2
7  mod 5 = 2
```

Therefore:

```math
17 \equiv 7 \pmod 5
```

Why?

```text
17 - 7 = 10
```

and:

```text
10 is divisible by 5
```

So another useful model is:

```math
A \equiv B \pmod M
```

means:

```math
M \mid (A-B)
```

---

## 1.3 Divisibility

`a` divides `b` if division leaves no remainder.

```math
a \mid b
```

is equivalent to:

```math
b \bmod a = 0
```

### Example

```text
3 divides 12
because
12 mod 3 = 0
```

But:

```text
5 does not divide 12
because
12 mod 5 = 2
```

---

## 1.4 Exponent Rules

### Product of equal bases

```math
a^x \cdot a^y = a^{x+y}
```

Example:

```math
2^3 \cdot 2^4 = 2^7
```

because:

```text
(2×2×2)(2×2×2×2)
=
2×2×2×2×2×2×2
```

---

### Even exponent

If:

```text
b = 2k
```

then:

```math
a^b = a^{2k}
```

and:

```math
a^{2k} = (a^k)^2
```

Example:

```math
3^6 = (3^3)^2
```

---

### Odd exponent

If:

```text
b = 2k+1
```

then:

```math
a^b = a^{2k+1}
```

and:

```math
a^{2k+1} = (a^k)^2 \cdot a
```

Example:

```math
3^7 = (3^3)^2 \cdot 3
```

These two identities are exactly why binary exponentiation works.

---

## 1.5 Prime and Coprime

A prime number has exactly two positive divisors:

```text
1 and itself
```

Examples:

```text
2, 3, 5, 7, 11, 13, ...
```

Two numbers are coprime when:

```math
\gcd(a,b)=1
```

Example:

```text
gcd(8,15) = 1
```

Coprimality matters for modular inverses.

---

# 2. Modular Operations

The main idea:

```text
Complete multiples of M do not affect the remainder.
Only remainders matter.
```

---

## 2.1 Addition

Formula:

```math
(A+B)\bmod M
=
((A\bmod M)+(B\bmod M))\bmod M
```

### Why?

Write:

```text
A = q1×M + r1
B = q2×M + r2
```

Add:

```text
A + B
=
q1M + r1 + q2M + r2
```

Group:

```text
A + B
=
(q1+q2)M + (r1+r2)
```

The first part is divisible by `M`.

So only:

```text
r1 + r2
```

matters.

Therefore:

```math
(A+B)\bmod M
=
(r_1+r_2)\bmod M
```

and since:

```math
r_1=A\bmod M
```

```math
r_2=B\bmod M
```

we get the formula.

---

### Example — `(7+6) mod 5`

Direct:

```text
7 + 6 = 13
13 mod 5 = 3
```

Using remainders:

```text
7 mod 5 = 2
6 mod 5 = 1

2 + 1 = 3

3 mod 5 = 3
```

Same answer:

```text
3
```

---

## 2.2 Subtraction

Formula:

```math
(A-B)\bmod M
=
((A\bmod M)-(B\bmod M)+M)\bmod M
```

Why `+M`?

Because subtraction can become negative.

### Example — `(11-8) mod 5`

First reduce:

```text
11 mod 5 = 1
8  mod 5 = 3
```

Subtract:

```text
1 - 3 = -2
```

But we want a normalized remainder in:

```text
0 ... M-1
```

Add one modulus:

```text
-2 + 5 = 3
```

Therefore:

```math
(11-8)\bmod5=3
```

### Safe C++

```cpp
long long subMod(long long a, long long b, long long mod) {
    return ((a % mod - b % mod) + mod) % mod;
}
```

---

## 2.3 Multiplication

Formula:

```math
(AB)\bmod M
=
((A\bmod M)(B\bmod M))\bmod M
```

### Derivation

Again:

```text
A = q1M + r1
B = q2M + r2
```

Multiply:

```math
AB=(q_1M+r_1)(q_2M+r_2)
```

Expand:

```math
AB
=
q_1q_2M^2
+
q_1Mr_2
+
q_2Mr_1
+
r_1r_2
```

Factor `M` from the first three terms:

```math
AB
=
M(q_1q_2M+q_1r_2+q_2r_1)
+
r_1r_2
```

The first part is divisible by `M`.

So:

```math
AB\bmod M
=
(r_1r_2)\bmod M
```

Hence:

```math
(AB)\bmod M
=
((A\bmod M)(B\bmod M))\bmod M
```

### Example — `7 × 8 mod 5`

```text
7 mod 5 = 2
8 mod 5 = 3

2 × 3 = 6
6 mod 5 = 1
```

Therefore:

```math
(7\cdot8)\bmod5=1
```

### C++

```cpp
long long mulMod(long long a, long long b, long long mod) {
    return (a % mod) * (b % mod) % mod;
}
```

---

# 3. Modular Division and Inverse

Normal division is the special case.

We cannot generally do:

```text
(A / B) mod M
=
(A mod M) / (B mod M)
```

That is wrong.

Instead:

```text
division
→ multiplication by modular inverse
```

So:

```math
\frac AB
\equiv
A\cdot B^{-1}
\pmod M
```

---

## 3.1 What Is a Modular Inverse?

`B^-1` modulo `M` is a value satisfying:

```math
B\cdot B^{-1}\equiv1\pmod M
```

### Example — inverse of 3 modulo 7

Try:

```text
3 × 1 = 3 mod 7
3 × 2 = 6 mod 7
3 × 3 = 9 mod 7 = 2
3 × 4 = 12 mod 7 = 5
3 × 5 = 15 mod 7 = 1
```

Therefore:

```math
3^{-1}\equiv5\pmod7
```

---

## 3.2 Fermat's Little Theorem

When `M` is prime and `B` is not divisible by `M`:

```math
B^{M-1}\equiv1\pmod M
```

Rewrite:

```math
B\cdot B^{M-2}\equiv1\pmod M
```

Compare with inverse definition:

```math
B\cdot B^{-1}\equiv1\pmod M
```

Therefore:

```math
B^{-1}\equiv B^{M-2}\pmod M
```

---

## 3.3 Example — `10 / 3 mod 7`

We need:

```text
inverse of 3 modulo 7
```

From above:

```text
inverse = 5
```

Then:

```text
10 mod 7 = 3
```

So:

```math
10/3
\equiv
3\cdot5
\pmod7
```

```math
3\cdot5=15
```

```math
15\bmod7=1
```

Answer:

```text
1
```

---

## 3.4 C++ With Fermat

```cpp
long long binpow(long long a, long long b, long long mod) {
    long long res = 1;
    a %= mod;

    while (b > 0) {
        if (b & 1)
            res = (res * a) % mod;

        a = (a * a) % mod;
        b >>= 1;
    }

    return res;
}

long long modInversePrime(long long b, long long mod) {
    return binpow(b, mod - 2, mod);
}
```

Use only when the conditions are valid.

---

# 4. Binary Exponentiation

Goal:

```text
compute a^b
```

Naive:

```text
multiply a, b times
```

Complexity:

```math
O(b)
```

Binary exponentiation reduces the exponent by half each step:

```math
O(\log b)
```

---

## 4.1 Why Halving Works

If `b` is even:

```math
a^b=(a^{b/2})^2
```

If `b` is odd:

```math
a^b=(a^{\lfloor b/2\rfloor})^2a
```

Example:

```math
3^7
=
(3^3)^2\cdot3
```

Instead of computing seven separate multiplications, we reuse squared values.

---

## 4.2 Recursive Model

```text
power(a,b)

b = 0
→ 1

b even
→ power(a,b/2)^2

b odd
→ power(a,b/2)^2 × a
```

### C++

```cpp
long long binpowRecursive(long long a, long long b) {
    if (b == 0)
        return 1;

    long long half = binpowRecursive(a, b / 2);
    long long ans = half * half;

    if (b & 1)
        ans *= a;

    return ans;
}
```

---

## 4.3 Iterative Model

Maintain:

```text
result = accumulated answer
base   = current power
b      = remaining exponent
```

At each step:

```text
if b is odd:
    result *= base

base *= base
b /= 2
```

---

### Detailed Dry Run — `3^6`

Start:

```text
result = 1
base   = 3
b      = 6
```

### Step 1

```text
b = 6 = even
```

Do not multiply result.

Square base:

```text
base = 3² = 9
```

Halve exponent:

```text
b = 3
```

State:

```text
result = 1
base   = 9
b      = 3
```

### Step 2

```text
b = 3 = odd
```

Multiply result:

```text
result = 1 × 9 = 9
```

Square base:

```text
base = 9² = 81
```

Halve:

```text
b = 1
```

### Step 3

```text
b = 1 = odd
```

Multiply result:

```text
result = 9 × 81 = 729
```

Halve:

```text
b = 0
```

Stop.

Answer:

```math
3^6=729
```

---

## 4.4 Modular Binary Exponentiation

When the answer is required modulo `M`, reduce after every multiplication.

```cpp
long long binpow(long long a, long long b, long long mod) {
    long long res = 1;
    a %= mod;

    while (b > 0) {
        if (b & 1)
            res = (res * a) % mod;

        a = (a * a) % mod;
        b >>= 1;
    }

    return res;
}
```

---

### Example — `2^10 mod 1000`

```text
2^10 = 1024
1024 mod 1000 = 24
```

Binary exponentiation gets the same result without building huge intermediate powers.

---

## Don't-Memorize Model

```text
huge exponent
      |
      v
can exponent be split into halves?
      |
      v
YES
      |
      +--> even: square half
      |
      +--> odd : square half × base
      |
      v
O(log b)
```

---

# 5. GCD

`gcd(a,b)` is the greatest positive integer dividing both.

Example:

```text
a = 8
b = 12
```

Divisors:

```text
8 : 1,2,4,8
12: 1,2,3,4,6,12
```

Greatest common divisor:

```math
\gcd(8,12)=4
```

---

## 5.1 Euclidean Algorithm

Key property:

```math
\gcd(a,b)=\gcd(a+kb,b)
```

for any integer `k`.

Choose `k` so that we remove as many multiples of `b` as possible.

That is exactly remainder:

```text
a mod b
```

So:

```math
\gcd(a,b)=\gcd(b,a\bmod b)
```

---

### Dry Run — `gcd(48,18)`

```text
48 = 2×18 + 12
```

So:

```math
gcd(48,18)=gcd(18,12)
```

Next:

```text
18 = 1×12 + 6
```

So:

```math
gcd(18,12)=gcd(12,6)
```

Next:

```text
12 = 2×6 + 0
```

So:

```math
gcd(12,6)=6
```

Final:

```text
gcd(48,18)=6
```

---

## 5.2 Why Euclid Works

Suppose `d` divides both `a` and `b`.

Then:

```text
a = xd
b = yd
```

Now:

```text
a - kb
=
xd - kyd
```

Factor `d`:

```text
a - kb
=
d(x-ky)
```

Therefore `d` also divides `a-kb`.

So subtracting any multiple of `b` from `a` does not change the common divisors.

That is the mathematical reason Euclid works.

---

## 5.3 GCD of Many Numbers

Because GCD is associative:

```math
\gcd(a,b,c)
=
\gcd(a,\gcd(b,c))
```

For an array:

```cpp
long long g = 0;

for (long long x : a)
    g = std::gcd(g, x);
```

Why start with `0`?

Because:

```math
\gcd(x,0)=x
```

---

# 6. LCM

`lcm(a,b)` is the smallest positive integer divisible by both.

Example:

```text
multiples of 4:
4, 8, 12, 16, ...

multiples of 6:
6, 12, 18, ...

first common multiple = 12
```

So:

```text
lcm(4,6)=12
```

---

## 6.1 Why GCD × LCM = a × b

Use prime exponents.

Suppose one prime `p` appears as:

```text
p^x in a
p^y in b
```

GCD keeps:

```text
minimum exponent
```

LCM keeps:

```text
maximum exponent
```

So exponent of `p` in:

```text
gcd × lcm
```

is:

```text
min(x,y) + max(x,y)
```

But:

```text
min(x,y) + max(x,y)
=
x + y
```

which is exactly the exponent of `p` in:

```text
a × b
```

Therefore:

```math
\mathrm{LCM}(a,b)\cdot\gcd(a,b)=ab
```

Hence:

```math
\mathrm{LCM}(a,b)
=
\frac{ab}{\gcd(a,b)}
```

---

### Example — `12` and `18`

Prime factorization:

```text
12 = 2² × 3
18 = 2 × 3²
```

GCD:

```text
min exponents:

2^1 × 3^1 = 6
```

LCM:

```text
max exponents:

2² × 3² = 36
```

Check:

```text
gcd × lcm
= 6 × 36
= 216

12 × 18
= 216
```

Correct.

---

## 6.2 LCM of Many Numbers

Do not use:

```math
\mathrm{LCM}(a,b,c)
=
\frac{abc}{\gcd(a,b,c)}
```

That is not generally true.

Instead combine pairwise:

```math
\mathrm{LCM}(a,b,c)
=
\mathrm{LCM}(a,\mathrm{LCM}(b,c))
```

### C++

```cpp
long long lcm(long long a, long long b) {
    return (a / std::gcd(a, b)) * b;
}
```

---

# 7. Pigeonhole Principle

Basic statement:

```text
If more objects than boxes exist,
at least one box gets at least two objects.
```

Example:

```text
3 balls
2 boxes
```

At least one box contains at least two balls.

---

## 7.1 CP Recognition

In CP, objects are often:

```text
prefix sums
numbers
indices
states
```

Boxes are often:

```text
remainders
categories
possible states
```

Ask:

```text
How many objects?
How many possible states?
```

If:

```text
objects > states
```

then some state repeats.

That repetition often creates the answer.

---

# 8. Application — Subarray Sum Divisible by N

Problem from the lecture:

> Given an array containing `N` elements, determine whether there exists a non-empty subarray whose sum is divisible by `N`.

The lecture uses prefix sums modulo `N` together with the Pigeonhole Principle.

---

## 8.1 Prefix Sum Prerequisite

Define:

```math
P_i=a_1+a_2+\cdots+a_i
```

Then sum from `l` to `r` is:

```math
P_r-P_{l-1}
```

We want:

```math
(P_r-P_{l-1})\bmod N=0
```

This happens when:

```math
P_r\bmod N
=
P_{l-1}\bmod N
```

Why?

Suppose both have remainder `r`:

```text
P_r     = q1×N + r
P_(l-1) = q2×N + r
```

Subtract:

```text
P_r - P_(l-1)
=
(q1-q2)N
```

which is divisible by `N`.

---

## 8.2 Pigeonhole Argument

There are `N` prefix sums:

```text
P1, P2, ..., PN
```

Each remainder modulo `N` is one of:

```text
0, 1, 2, ..., N-1
```

Now two cases.

### Case 1 — Some prefix remainder is 0

If:

```math
P_i\bmod N=0
```

then:

```text
a[1...i]
```

already has sum divisible by `N`.

Done.

---

### Case 2 — No remainder is 0

Then every prefix remainder belongs only to:

```text
1, 2, ..., N-1
```

Number of prefix sums:

```text
N
```

Number of available non-zero remainders:

```text
N-1
```

So:

```text
N objects
N-1 boxes
```

By Pigeonhole Principle, two prefix sums must have the same remainder.

Suppose:

```math
P_i\bmod N=P_j\bmod N
```

Then:

```math
(P_j-P_i)\bmod N=0
```

Therefore:

```text
a[i+1 ... j]
```

has sum divisible by `N`.

---

## 8.3 Detailed Dry Run — `[5,5,1]`

```text
N = 3
a = [5,5,1]
```

Prefix sums:

```text
P1 = 5
P2 = 10
P3 = 11
```

Remainders modulo `3`:

```text
P1 % 3 = 2
P2 % 3 = 1
P3 % 3 = 2
```

We get:

```text
2, 1, 2
```

Remainder `2` repeats.

So:

```math
P_3\bmod3=P_1\bmod3
```

Therefore:

```math
(P_3-P_1)\bmod3=0
```

Calculate:

```text
P3 - P1
= 11 - 5
= 6
```

This is subarray:

```text
[5,1]
```

and:

```text
6 % 3 = 0
```

Success.

---

## 8.4 Another Example — Prefix Itself Works

```text
N = 4
a = [2,2,7,1]
```

Prefix sums:

```text
2, 4, 11, 12
```

Modulo `4`:

```text
2, 0, 3, 0
```

At prefix 2:

```text
remainder = 0
```

So:

```text
[2,2]
```

has sum:

```text
4
```

which is divisible by `4`.

No repeated non-zero remainder is needed.

---

## 8.5 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

bool existsDivisibleSubarray(const vector<long long>& a) {
    int n = (int)a.size();

    vector<bool> seen(n, false);
    long long prefix = 0;

    for (long long x : a) {
        prefix = (prefix + x) % n;

        if (prefix < 0)
            prefix += n;

        if (prefix == 0)
            return true;

        if (seen[prefix])
            return true;

        seen[prefix] = true;
    }

    return false;
}
```

Complexity:

```text
Time  : O(N)
Space : O(N)
```

---

## 8.6 Don't-Memorize Model

```text
Need subarray sum divisible by N
              |
              v
subarray = difference of two prefix sums
              |
              v
difference % N = 0
              |
              v
two prefixes have same remainder
              |
              v
remainders are limited states
              |
              v
Pigeonhole Principle
```

---

# 9. Final Recognition Model

| Signal in problem | Think |
|---|---|
| huge arithmetic under modulo | reduce operands |
| subtraction modulo M | normalize with `+M` |
| division modulo prime | modular inverse |
| modular inverse with prime modulus | Fermat |
| huge exponent | binary exponentiation |
| exponent repeatedly halved | `O(log b)` |
| greatest common divisor | Euclidean algorithm |
| smallest common multiple | LCM |
| GCD/LCM relation | prime exponents min/max |
| more objects than states | Pigeonhole Principle |
| subarray divisibility | prefix sums modulo |
| equal prefix remainders | subarray sum divisible |

---

# Master Don't-Memorize Map

```text
MODULO
  |
  +-- addition/subtraction/multiplication
  |
  +-- division
        |
        v
     inverse
        |
        v
     Fermat
        |
        v
 binary exponentiation


GCD / LCM
  |
  +-- common divisor → GCD
  |
  +-- common multiple → LCM
  |
  +-- Euclid removes multiples


PIGEONHOLE
  |
  +-- limited number of states
  |
  +-- more objects than states
  |
  +-- some state repeats
  |
  +-- repeated prefix remainder
        |
        v
  divisible subarray
```

> **Core habit:** derive the relation first. The code should be the final step, not the first step.
