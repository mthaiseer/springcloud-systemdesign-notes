# Rules of Modulo — CP Notes

> **Goal:** Learn the core modulo calculation rules, residue replacement, exponentiation, and important edge cases.

---

## Table of Contents

1. [Core Idea](#1-core-idea)
2. [Sign of the Result](#2-sign-of-the-result)
3. [Addition](#3-addition)
4. [Subtraction](#4-subtraction)
5. [Multiplication](#5-multiplication)
6. [Exponentiation](#6-exponentiation)
7. [Residue Replacement](#7-residue-replacement)
8. [Example 1 — Addition & Multiplication](#8-example-1--addition--multiplication)
9. [Example 2 — Huge Power](#9-example-2--huge-power)
10. [Special Cases](#10-special-cases)
11. [Final Cheat Sheet](#11-final-cheat-sheet)

---

# 1. Core Idea

For modulus `m`, a number can be replaced by its remainder:

```text
a  →  a % m
```

Example:

```text
17 % 5 = 2

Therefore:

17 ≡ 2 (mod 5)
```

This allows us to reduce numbers **before** calculations.

```text
Huge numbers
     ↓
take % m
     ↓
small residues
     ↓
perform calculation
     ↓
take % m again
```

---

# 2. Sign of the Result

For positive numbers:

```text
a >= 0, m > 0

a % m ∈ [0, m-1]
```

Example:

```text
17 % 5 = 2
```

For negative dividends, programming-language behavior matters.

In C++:

```cpp
-8 % 7 == -1
```

If we need a normalized non-negative modulo:

```cpp
((x % m) + m) % m
```

Example:

```text
x = -8
m = 7

-8 % 7 = -1

(-1 + 7) % 7
= 6
```

So normalized:

```text
-8 mod 7 = 6
```

---

# 3. Addition

## Rule

```text
(a + b) mod m
=
((a mod m) + (b mod m)) mod m
```

### Example

Find:

```text
(15 + 27) mod 10
```

Reduce first:

```text
15 mod 10 = 5
27 mod 10 = 7

(5 + 7) mod 10
= 12 mod 10
= 2
```

Directly:

```text
42 mod 10 = 2
```

Same answer.

### C++

```cpp
long long mod_add(long long a, long long b, long long m) {
    return ((a % m) + (b % m)) % m;
}
```

For possibly negative inputs:

```cpp
long long norm(long long x, long long m) {
    return ((x % m) + m) % m;
}

long long mod_add(long long a, long long b, long long m) {
    return (norm(a, m) + norm(b, m)) % m;
}
```

---

# 4. Subtraction

## Rule

```text
(a - b) mod m
=
((a mod m) - (b mod m) + m) mod m
```

Why `+m`?

Because subtraction may produce a negative value.

### Example 1

```text
(8 - 3) mod 5

8 mod 5 = 3
3 mod 5 = 3

(3 - 3 + 5) mod 5
= 5 mod 5
= 0
```

### Example 2 — negative intermediate result

```text
(7 - 9) mod 5

7 mod 5 = 2
9 mod 5 = 4

(2 - 4) = -2
```

Normalize:

```text
(-2 + 5) mod 5
= 3
```

Therefore:

```text
(7 - 9) mod 5 = 3
```

### C++

```cpp
long long mod_sub(long long a, long long b, long long m) {
    return ((a % m - b % m) + m) % m;
}
```

For arbitrary negative inputs, normalize both values first.

---

# 5. Multiplication

## Rule

```text
(a × b) mod m
=
((a mod m) × (b mod m)) mod m
```

### Example

```text
(14 × 16) mod 10
```

Reduce:

```text
14 mod 10 = 4
16 mod 10 = 6
```

Multiply:

```text
4 × 6 = 24
```

Take modulo:

```text
24 mod 10 = 4
```

Therefore:

```text
(14 × 16) mod 10 = 4
```

### C++

```cpp
long long mod_mul(long long a, long long b, long long m) {
    return ((a % m) * (b % m)) % m;
}
```

> Use a sufficiently wide integer type so the multiplication itself does not overflow before `% m` is applied.

---

# 6. Exponentiation

## Rule

```text
a^b mod m
=
(a mod m)^b mod m
```

This means we can reduce the **base** first.

### Example

Find:

```text
13^4 mod 5
```

Reduce the base:

```text
13 mod 5 = 3
```

Therefore:

```text
13^4 mod 5
=
3^4 mod 5
=
81 mod 5
=
1
```

For very large `b`, use **binary exponentiation**:

```cpp
long long mod_pow(long long a, long long b, long long m) {
    long long ans = 1 % m;
    a = ((a % m) + m) % m;

    while (b > 0) {
        if (b & 1)
            ans = (ans * a) % m;

        a = (a * a) % m;
        b >>= 1;
    }

    return ans;
}
```

Complexity:

```text
O(log b)
```

---

# 7. Residue Replacement

This is one of the most useful ideas in modular arithmetic.

If:

```text
a ≡ r (mod m)
```

then during addition and multiplication modulo `m`, we can replace:

```text
a → r
```

Example:

```text
3333 ≡ 3 (mod 5)
4444 ≡ 4 (mod 5)
```

So instead of working with:

```text
3333 + 4444
```

we can work with:

```text
3 + 4
```

Diagram:

```text
3333 ──%5──> 3
                  \
                   → calculate with residues
                  /
4444 ──%5──> 4
```

---

# 8. Example 1 — Addition & Multiplication

Find the remainders when:

```text
3333 + 4444
```

and

```text
3333 × 4444
```

are divided by `5`.

First reduce:

```text
3333 ≡ 3 (mod 5)
4444 ≡ 4 (mod 5)
```

## Addition

```text
3333 + 4444
≡ 3 + 4
≡ 7
≡ 2 (mod 5)
```

Answer:

```text
2
```

## Multiplication

```text
3333 × 4444
≡ 3 × 4
≡ 12
≡ 2 (mod 5)
```

Answer:

```text
2
```

### Key observation

```text
Large numbers
     ↓
replace by residues
     ↓
3 and 4
     ↓
tiny calculation
```

---

# 9. Example 2 — Huge Power

Find:

```text
2015^2015 mod 2014
```

Direct computation is unnecessary.

Observe:

```text
2015 = 2014 + 1
```

Therefore:

```text
2015 ≡ 1 (mod 2014)
```

Now replace the base:

```text
2015^2015
≡ 1^2015
≡ 1
(mod 2014)
```

Answer:

```text
1
```

### Recognition

Whenever you see:

```text
(base very close to modulus)^huge_power
```

first calculate:

```text
base % modulus
```

Sometimes the entire problem collapses immediately.

---

# 10. Special Cases

## Negative Dividend

C++ may produce a negative remainder:

```cpp
-8 % 7   // -1
```

Normalize when a value in `[0, m-1]` is required:

```cpp
((-8 % 7) + 7) % 7   // 6
```

---

## Negative Divisor

Programming-language behavior can differ from the mathematical convention.

In CP, the modulus is normally taken as:

```text
m > 0
```

Prefer working with a positive modulus.

---

## Zero Divisor

Never do:

```cpp
a % 0
```

Modulo by zero is undefined.

```text
a % 0  → INVALID
```

---

## Division Is NOT a Normal Modulo Rule

Addition, subtraction and multiplication distribute cleanly through modulo.

Ordinary division does not:

```text
(a / b) mod m
```

cannot generally be transformed into:

```text
(a mod m) / (b mod m)
```

For modular division, we usually need a **modular inverse**.

---

# 11. Final Cheat Sheet

```text
ADDITION

(a+b) % m
=
((a%m) + (b%m)) % m
```

```text
SUBTRACTION

(a-b) mod m
=
((a%m) - (b%m) + m) % m
```

```text
MULTIPLICATION

(a*b) % m
=
((a%m) * (b%m)) % m
```

```text
EXPONENTIATION

a^b mod m
=
(a mod m)^b mod m
```

```text
NEGATIVE NORMALIZATION

((x % m) + m) % m
```

```text
CONGRUENCE

a ≡ b (mod m)

means

a and b belong to the same remainder class
```

```text
ZERO MODULUS

a % 0 → undefined
```

```text
DIVISION

No ordinary modulo division rule
→ learn Modular Inverse
```

## Mental Model

```text
        BIG EXPRESSION
              │
              ↓
       reduce each value
            % m
              │
              ↓
      SMALL RESIDUES
              │
     ┌────────┼─────────┐
     ↓        ↓         ↓
    ADD      SUB       MUL
     │        │         │
     └────────┼─────────┘
              ↓
            % m
              ↓
       FINAL REMAINDER
```

> **Core CP habit:** When a problem asks for an answer modulo `m`, reduce values early and repeatedly instead of carrying unnecessarily large numbers.
