# Fermat's Little Theorem & Euler's Theorem --- Don't Memorize, Model It

> **Goal:** Understand **why powers repeat under modulo**, then use that
> cycle to reduce huge exponents.

## Table of Contents

0.  [Preliminary --- Congruence & Exponent
    Rules](#0-preliminary--congruence--exponent-rules)
1.  [Fermat's Little Theorem](#1-fermats-little-theorem)
2.  [Why Fermat Reduces Exponents](#2-why-fermat-reduces-exponents)
3.  [Computing Huge Powers](#3-computing-huge-powers)
4.  [Euler's Theorem](#4-eulers-theorem)
5.  [Euler Totient Needed Here](#5-euler-totient-needed-here)
6.  [Why Euler Reduces Exponents](#6-why-euler-reduces-exponents)
7.  [Fermat vs Euler](#7-fermat-vs-euler)
8.  [Final Recognition Model](#8-final-recognition-model)

------------------------------------------------------------------------

# 0. Preliminary --- Congruence & Exponent Rules

## 0.1 What does congruence mean?

```text
a ≡ b (mod m)
```

means:

```text
a and b leave the same remainder when divided by m
```

Equivalent meaning:

```text
m divides (a - b)
```

Example:

```text
17 ≡ 2 (mod 5)

17 % 5 = 2
 2 % 5 = 2
```

So `17` and `2` are equivalent under modulo `5`.

------------------------------------------------------------------------

## 0.2 Exponent rule we will use

Basic exponent law:

```text
a^(x+y) = a^x × a^y
```

Example:

```text
2^(3+2)
= 2³ × 2²
= 8 × 4
= 32
```

Also:

```text
a^(qk+r)
= (a^k)^q × a^r
```

This is the key algebra behind exponent reduction.

Example:

```text
2^11

11 = 2×4 + 3

2^11
= 2^(2×4+3)
= (2⁴)² × 2³
```

If `2⁴ ≡ 1 (mod m)`, then:

```text
(2⁴)² × 2³
≡ 1² × 2³
≡ 2³
```

So the large exponent can collapse to its remainder.

------------------------------------------------------------------------

# 1. Fermat's Little Theorem

## 1.1 Statement

If:

```text
p is prime
p does NOT divide a
```

then:

$$a^{p-1} \equiv 1 \pmod{p}$$

Equivalent condition:

```text
gcd(a,p) = 1
```

Because `p` is prime, every `a` not divisible by `p` is coprime with
`p`.

------------------------------------------------------------------------

## 1.2 Example --- a = 2, p = 5

Fermat says:

```text
2^(5-1) ≡ 1 (mod 5)
```

Step by step:

```text
2⁴
= 16

16 % 5
= 1
```

Therefore:

$$2^4\equiv1\pmod5$$

Visual cycle:

```text
2¹ % 5 = 2
2² % 5 = 4
2³ % 5 = 3
2⁴ % 5 = 1
             ↑
          cycle reset
```

That `1` is what makes exponent reduction possible.

------------------------------------------------------------------------

## 1.3 Alternative Fermat form

Start with:

```text
a^(p-1) ≡ 1 (mod p)
```

Multiply both sides by `a`:

```text
a × a^(p-1) ≡ a × 1 (mod p)
```

Use:

```text
a^x × a^y = a^(x+y)
```

Therefore:

```text
a^p ≡ a (mod p)
```

So another common form is:

$$a^p \equiv a \pmod{p}$$

This identity holds for every integer `a` when `p` is prime, including
the case `p | a`.

Example:

```text
a = 10
p = 5

10⁵ ≡ 0 (mod 5)
10  ≡ 0 (mod 5)

therefore:
10⁵ ≡ 10 (mod 5)
```

------------------------------------------------------------------------

## C++ — Binary Exponentiation

Fermat problems usually require fast modular exponentiation.

```cpp
long long binpow(long long a, long long b, long long mod) {
    long long ans = 1;
    a %= mod;

    while (b > 0) {
        if (b & 1)
            ans = (ans * a) % mod;

        a = (a * a) % mod;
        b >>= 1;
    }

    return ans;
}
```

Time:

```text
O(log b)
```

------------------------------------------------------------------------

# 2. Why Fermat Reduces Exponents

Suppose:

```text
gcd(a,p) = 1
p is prime
```

Fermat gives:

```text
a^(p-1) ≡ 1 (mod p)
```

Now suppose the exponent is `E`.

Divide `E` by `p-1`:

```text
E = q(p-1) + r
```

where:

```text
r = E % (p-1)
```

Then:

```text
a^E
= a^[q(p-1)+r]

= a^[q(p-1)] × a^r

= (a^(p-1))^q × a^r
```

Apply Fermat:

```text
a^(p-1) ≡ 1
```

Therefore:

```text
a^E
≡ 1^q × a^r
≡ a^r
(mod p)
```

Hence:

$$a^E \equiv a^{\,E\bmod(p-1)} \pmod{p}$$

provided `gcd(a,p)=1`.

------------------------------------------------------------------------

## Example — 2^103 mod 5

We know:

```text
p - 1 = 4
```

Reduce the exponent:

```text
103 % 4 = 3
```

Therefore:

```text
2^103 mod 5
= 2³ mod 5
= 8 mod 5
= 3
```

### Don't memorize

```text
a^(p-1) ≡ 1
      |
      v
split E into:
E = q(p-1) + r
      |
      v
a^E = (a^(p-1))^q × a^r
      |
      v
    1^q × a^r
      |
      v
      a^r
```

------------------------------------------------------------------------

# 3. Computing Huge Powers

Suppose we need:

$$a^{\,b^c}\bmod p$$

where `p` is prime and:

```text
gcd(a,p) = 1
```

Let:

```text
E = b^c
```

From Fermat:

```text
a^E mod p
```

only needs:

```text
E mod (p-1)
```

Therefore:

```text
reducedExponent = b^c mod (p-1)
answer          = a^reducedExponent mod p
```

> We are **not saying the original and reduced exponents are equal**. They give the same result for the outer power modulo `p` because complete blocks of length `p-1` contribute `1`.

Mathematically:

```text
a^(b^c) mod p
=
a^(b^c mod (p-1)) mod p
```

Equivalent modular statement:

$$a^{b^c} \equiv a^{\,b^c\bmod(p-1)} \pmod{p}$$

under the coprimality condition above.

------------------------------------------------------------------------

## Example — 2^(3^4) mod 5

### Step 1 --- identify Fermat cycle length

```text
p = 5

p - 1 = 4
```

### Step 2 --- reduce the huge exponent

```text
3⁴ = 81

81 % 4 = 1
```

### Step 3 --- use the reduced exponent

```text
2^(3⁴) mod 5
= 2^81 mod 5

2^81 ≡ 2¹ (mod 5)

2¹ mod 5
= 2
```

ASCII model:

```text
2^(3^4) mod 5
       |
       v
outer base = 2
modulus    = 5 prime
       |
       v
reduce exponent modulo 4
       |
       v
3^4 mod 4 = 1
       |
       v
2^1 mod 5
       |
       v
       2
```

------------------------------------------------------------------------

## C++ — Huge Exponent of the Form a^(b^c) mod p

```cpp
long long binpow(long long a, long long b, long long mod) {
    long long ans = 1;
    a %= mod;

    while (b > 0) {
        if (b & 1)
            ans = (ans * a) % mod;

        a = (a * a) % mod;
        b >>= 1;
    }

    return ans;
}

long long hugePower(long long a,
                    long long b,
                    long long c,
                    long long p) {

    // Valid exponent reduction here when gcd(a,p) = 1.
    long long exponent = binpow(b, c, p - 1);

    return binpow(a, exponent, p);
}
```

> **Important:** Do not blindly reduce the exponent modulo `p-1` when
> `a` is divisible by `p`. The simple reduction above relies on the
> Fermat coprimality condition.

------------------------------------------------------------------------

# 4. Euler's Theorem

Fermat handles a **prime modulus**.

Euler generalizes the idea to a modulus that does not have to be prime.

## 4.1 Statement

If:

```text
gcd(a,m) = 1
```

then:

$$a^{\phi(m)} \equiv 1 \pmod{m}$$

where `phi(m)` is Euler's Totient Function.

------------------------------------------------------------------------

## 4.2 Example --- a = 5, m = 12

First check:

```text
gcd(5,12) = 1
```

So Euler can be used.

Now:

```text
phi(12) = 4
```

Euler says:

```text
5⁴ ≡ 1 (mod 12)
```

Check:

```text
5² = 25
25 % 12 = 1

5⁴
= (5²)²
≡ 1²
≡ 1
(mod 12)
```

Correct.

------------------------------------------------------------------------

# 5. Euler Totient Needed Here

`phi(m)` counts integers from `1` to `m` that are coprime with `m`.

Example:

```text
m = 12

numbers:
1 2 3 4 5 6 7 8 9 10 11 12

coprime with 12:
1, 5, 7, 11

count = 4
```

Therefore:

```text
phi(12) = 4
```

If:

```text
m = p1^a1 × p2^a2 × ... × pk^ak
```

then:

$$\phi(m)=m\left(1-\frac1{p_1}\right)\left(1-\frac1{p_2}\right)\cdots\left(1-\frac1{p_k}\right)$$

Only the **distinct prime factors** matter in these factors.

------------------------------------------------------------------------

## Example — phi(12)

Factorize:

```text
12 = 2² × 3
```

Distinct primes:

```text
2, 3
```

Formula:

```text
phi(12)
= 12 × (1 - 1/2) × (1 - 1/3)
```

Do the fractions step by step:

```text
1 - 1/2
= 2/2 - 1/2
= 1/2
```

and:

```text
1 - 1/3
= 3/3 - 1/3
= 2/3
```

Therefore:

```text
phi(12)
= 12 × 1/2 × 2/3

= 6 × 2/3

= 12/3

= 4
```

------------------------------------------------------------------------

## C++ — Euler Totient

```cpp
long long phi(long long n) {
    long long result = n;

    for (long long p = 2; p <= n / p; ++p) {
        if (n % p != 0) continue;

        while (n % p == 0)
            n /= p;

        result -= result / p;
    }

    if (n > 1)
        result -= result / n;

    return result;
}
```

Time:

```text
O(sqrt(n))
```

------------------------------------------------------------------------

# 6. Why Euler Reduces Exponents

Euler gives:

```text
a^phi(m) ≡ 1 (mod m)
```

provided:

```text
gcd(a,m) = 1
```

For exponent `E`, write:

```text
E = q × phi(m) + r
```

where:

```text
r = E % phi(m)
```

Then:

```text
a^E
= a^[q×phi(m)+r]

= (a^phi(m))^q × a^r
```

Euler gives:

```text
a^phi(m) ≡ 1
```

So:

```text
a^E
≡ 1^q × a^r
≡ a^r
(mod m)
```

Therefore:

$$a^E \equiv a^{\,E\bmod\phi(m)} \pmod{m}$$

when `gcd(a,m)=1`.

------------------------------------------------------------------------

## Example — 5^100 mod 12

Check:

```text
gcd(5,12) = 1
```

Find:

```text
phi(12) = 4
```

Reduce exponent:

```text
100 % 4 = 0
```

Therefore:

```text
5^100 ≡ 5^0 (mod 12)

5^0 mod 12
= 1
```

Why is exponent `0` okay here?

```text
100 = 25 × 4

5^100
= (5⁴)^25

5⁴ ≡ 1 (mod 12)

therefore:

5^100
≡ 1^25
≡ 1
```

------------------------------------------------------------------------

## C++ — Euler Exponent Reduction

```cpp
long long binpow(long long a, long long b, long long mod) {
    long long ans = 1;
    a %= mod;

    while (b > 0) {
        if (b & 1)
            ans = (ans * a) % mod;

        a = (a * a) % mod;
        b >>= 1;
    }

    return ans;
}

long long phi(long long n) {
    long long result = n;

    for (long long p = 2; p <= n / p; ++p) {
        if (n % p != 0) continue;

        while (n % p == 0)
            n /= p;

        result -= result / p;
    }

    if (n > 1)
        result -= result / n;

    return result;
}
```

Usage when `gcd(a,m)=1`:

```cpp
long long cycle = phi(m);
long long reducedExponent = exponent % cycle;

long long answer = binpow(a, reducedExponent, m);
```

------------------------------------------------------------------------

# 7. Fermat vs Euler

| Situation | Theorem | Exponent cycle |
|---|---|---|
| `p` is prime and `gcd(a,p)=1` | Fermat | `p - 1` |
| General `m` and `gcd(a,m)=1` | Euler | `phi(m)` |

Connection:

```text
p is prime
   |
   v
phi(p) = p - 1
   |
   v
Euler:
a^phi(p) ≡ 1
   |
   v
a^(p-1) ≡ 1
   |
   v
Fermat
```

So Fermat is Euler's theorem specialized to a prime modulus.

------------------------------------------------------------------------

# 8. Final Recognition Model

## Fermat

When you see:

```text
huge power mod p
p is prime
gcd(base,p) = 1
```

think:

```text
a^(p-1) ≡ 1
     |
     v
reduce exponent modulo (p-1)
```

## Euler

When you see:

```text
huge power mod m
m may be composite
gcd(base,m) = 1
```

think:

```text
factorize m
     |
     v
calculate phi(m)
     |
     v
a^phi(m) ≡ 1
     |
     v
reduce exponent modulo phi(m)
```

## Master model

```text
           huge exponent
                 |
                 v
        can powers cycle?
                 |
        +--------+--------+
        |                 |
   prime modulus      general modulus
        |                 |
     Fermat             Euler
        |                 |
      p-1              phi(m)
        |                 |
        +--------+--------+
                 |
                 v
       reduce exponent
                 |
                 v
      binary exponentiation
```

> **Do not memorize only `p-1` or `phi(m)`.** Remember the reason: a
> complete exponent block evaluates to `1` modulo the modulus, so
> complete blocks disappear and only the remainder exponent matters.
