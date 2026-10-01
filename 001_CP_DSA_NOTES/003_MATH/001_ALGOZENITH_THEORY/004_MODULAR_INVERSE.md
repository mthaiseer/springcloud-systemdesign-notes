# Modular Inverse — Fermat's Little Theorem & Existence

> **Goal:** Understand why division under modulo needs an inverse, when that inverse exists, and how to calculate it using Fermat's Little Theorem or Extended Euclidean Algorithm.

---

## Table of Contents

1. [Why Modular Inverse?](#1-why-modular-inverse)
2. [Definition](#2-definition)
3. [When Does an Inverse Exist?](#3-when-does-an-inverse-exist)
4. [Fermat's Little Theorem](#4-fermats-little-theorem)
5. [Deriving the Inverse Formula](#5-deriving-the-inverse-formula)
6. [Modular Division](#6-modular-division)
7. [Dry Run — 8 / 3 mod 5](#7-dry-run--8--3-mod-5)
8. [Method 1 — Fermat](#8-method-1--fermat)
9. [Method 2 — Extended GCD](#9-method-2--extended-gcd)
10. [Method Selection](#10-method-selection)
11. [Why Prime Modulus Helps](#11-why-prime-modulus-helps)
12. [Common Mistakes](#12-common-mistakes)
13. [Final Memory Card](#13-final-memory-card)

---

# 1. Why Modular Inverse?

Ordinary division does **not** work directly under modulo:

```text
(a / b) mod m
```

Instead:

```text
divide by b
    ↓
multiply by inverse of b

a / b ≡ a × b⁻¹ (mod m)
```

So modular division is mainly a problem of **finding `b⁻¹`**.

---

# 2. Definition

The modular inverse of `a` modulo `m` is a number `x` satisfying:

```text
a × x ≡ 1 (mod m)
```

We write:

```text
x = a⁻¹
```

Therefore:

```text
a × a⁻¹ ≡ 1 (mod m)
```

### Example — inverse of 3 modulo 7

```text
3 × 1 % 7 = 3
3 × 2 % 7 = 6
3 × 3 % 7 = 2
3 × 4 % 7 = 5
3 × 5 % 7 = 1  ✓
```

Therefore:

```text
3⁻¹ ≡ 5 (mod 7)
```

---

# 3. When Does an Inverse Exist?

An inverse exists **if and only if**:

```text
gcd(a,m) = 1
```

That means `a` and `m` must be **coprime**.

```text
Need inverse(a,m)
        │
        ↓
   gcd(a,m) == 1?
      /       \
    YES        NO
     │          │
     ↓          ↓
  exists    no inverse
```

### Exists

```text
a = 3, m = 7

gcd(3,7) = 1
```

So the inverse exists.

### Does not exist

```text
a = 6, m = 9

gcd(6,9) = 3
```

So no modular inverse exists.

---

# 4. Fermat's Little Theorem

If:

```text
p is prime
```

and `a` is not divisible by `p`, then:

```text
a^(p-1) ≡ 1 (mod p)
```

### Example

```text
a = 2
p = 5

2^(5-1)
= 2^4
= 16

16 % 5 = 1
```

Therefore:

```text
2^4 ≡ 1 (mod 5)
```

---

# 5. Deriving the Inverse Formula

Start with Fermat:

```text
a^(p-1) ≡ 1 (mod p)
```

Split one `a`:

```text
a × a^(p-2) ≡ 1 (mod p)
```

Definition of inverse:

```text
a × a⁻¹ ≡ 1 (mod p)
```

Compare:

```text
a × a^(p-2) ≡ 1
a × a⁻¹     ≡ 1
```

Therefore:

```text
a⁻¹ ≡ a^(p-2) (mod p)
```

### Formula

```text
inverse(a) = a^(p-2) mod p
```

Conditions:

```text
p is prime
a % p != 0
```

Visual:

```text
a^(p-1) ≡ 1
      ↓
a × a^(p-2) ≡ 1
      ↓
compare with
a × a⁻¹ ≡ 1
      ↓
a⁻¹ ≡ a^(p-2)
```

---

# 6. Modular Division

To calculate:

```text
a / b (mod m)
```

find:

```text
b⁻¹
```

then:

```text
a / b
≡
a × b⁻¹
(mod m)
```

Workflow:

```text
a / b mod m
     ↓
Does inverse(b) exist?
     ↓
find b⁻¹
     ↓
(a × b⁻¹) % m
```

---

# 7. Dry Run — `8 / 3 mod 5`

Find:

```text
8 / 3 (mod 5)
```

Since `5` is prime:

```text
3⁻¹ ≡ 3^(5-2)
```

So:

```text
3⁻¹ ≡ 3^3
     ≡ 27
     ≡ 2 (mod 5)
```

Now replace division:

```text
8 / 3
≡ 8 × 2
≡ 16
≡ 1 (mod 5)
```

Answer:

```text
1
```

Verification:

```text
3 × 1 ≡ 3 (mod 5)
8       ≡ 3 (mod 5)
```

---

# 8. Method 1 — Fermat

Use when the modulus `p` is **prime**.

```text
inverse(a)
=
a^(p-2) mod p
```

Use binary exponentiation to calculate the power.

```cpp
long long modPow(long long base, long long exp, long long mod) {
    long long ans = 1;
    base %= mod;

    while (exp > 0) {
        if (exp & 1LL)
            ans = (ans * base) % mod;

        base = (base * base) % mod;
        exp >>= 1;
    }

    return ans;
}

long long modInversePrime(long long a, long long p) {
    a %= p;

    if (a == 0)
        return -1;

    return modPow(a, p - 2, p);
}

long long modDividePrime(long long a, long long b, long long p) {
    long long inv = modInversePrime(b, p);

    if (inv == -1)
        return -1;

    return ((a % p) * inv) % p;
}
```

Example:

```cpp
cout << modDividePrime(8, 3, 5);
```

Output:

```text
1
```

Complexity:

```text
O(log p)
```

---

# 9. Method 2 — Extended GCD

For a general modulus, the inverse exists when:

```text
gcd(a,m) = 1
```

Extended Euclid finds:

```text
a×x + m×y = gcd(a,m)
```

When the GCD is `1`:

```text
a×x + m×y = 1
```

Taking modulo `m`:

```text
a×x ≡ 1 (mod m)
```

Therefore:

```text
x = a⁻¹ mod m
```

after normalization.

```cpp
long long extendedGcd(long long a, long long b,
                      long long &x, long long &y) {
    if (b == 0) {
        x = 1;
        y = 0;
        return a;
    }

    long long x1, y1;
    long long g = extendedGcd(b, a % b, x1, y1);

    x = y1;
    y = x1 - (a / b) * y1;

    return g;
}

long long modInverse(long long a, long long m) {
    long long x, y;

    long long g = extendedGcd(a, m, x, y);

    if (g != 1)
        return -1;

    return ((x % m) + m) % m;
}
```

---

# 10. Method Selection

```text
Need inverse(a,m)
        │
        ↓
Is gcd(a,m) = 1?
     /       \
   NO         YES
   │           │
   ↓           ↓
No inverse   Is m prime?
             /       \
           YES        NO
            │          │
            ↓          ↓
         Fermat     Extended GCD
       a^(m-2)      general method
```

Remember:

```text
Prime modulus
→ Fermat is convenient

General modulus + gcd(a,m)=1
→ Extended Euclidean Algorithm
```

---

# 11. Why Prime Modulus Helps

If `p` is prime, every non-zero residue:

```text
1, 2, 3, ..., p-1
```

is coprime with `p`.

Therefore every:

```text
a % p != 0
```

has an inverse modulo `p`.

For:

```text
p = 7
```

all of:

```text
1, 2, 3, 4, 5, 6
```

have modular inverses.

This makes prime moduli convenient for:

```text
modular division
factorials
nCr
combinatorics
probability
```

---

# 12. Common Mistakes

### 1. Using ordinary division

Wrong idea:

```cpp
(a / b) % MOD
```

For modular division:

```text
a × inverse(b) mod MOD
```

---

### 2. Assuming every inverse exists

Remember:

```text
inverse exists
⇕
gcd(a,m) = 1
```

---

### 3. Using Fermat for every modulus

Do not blindly use:

```text
a^(m-2) mod m
```

The Fermat shortcut requires a **prime modulus** and a non-zero residue.

---

### 4. Trying to invert zero

For prime `p`:

```text
a % p == 0
```

has no inverse modulo `p`.

---

# 13. Final Memory Card

```text
DEFINITION

a × a⁻¹ ≡ 1 (mod m)
```

```text
EXISTENCE

a⁻¹ exists
⇕
gcd(a,m) = 1
```

```text
FERMAT

a^(p-1) ≡ 1 (mod p)

p = prime
a % p != 0
```

```text
DERIVATION

a^(p-1) ≡ 1
      ↓
a × a^(p-2) ≡ 1
      ↓
a⁻¹ ≡ a^(p-2)
```

```text
MODULAR DIVISION

a / b
≡
a × b⁻¹
(mod m)
```

```text
METHOD

Prime modulus
→ Fermat + Binary Exponentiation

General coprime modulus
→ Extended GCD
```

## Contest Recognition

```text
DIVISION UNDER MODULO
          ↓
    MODULAR INVERSE
          ↓
      check gcd
          ↓
   ┌──────┴──────┐
 prime         general
   ↓               ↓
Fermat        Extended GCD
   ↓               ↓
 multiply by inverse
```

> **Core idea:** In modular arithmetic, replace **division** with **multiplication by the modular inverse**.
