# Modular Arithmetic — CP Foundation

> **Goal:** Understand what modulo means, how programming languages calculate `%`, how negative values behave, and how to recognize modulo patterns in DSA/CP.

---

## Table of Contents

1. [What Is Modulo?](#1-what-is-modulo)
2. [Dividend, Divisor, Quotient, Remainder](#2-dividend-divisor-quotient-remainder)
3. [How `%` Is Calculated](#3-how--is-calculated)
4. [Cyclic Nature of Modulo](#4-cyclic-nature-of-modulo)
5. [Congruence](#5-congruence)
6. [Negative Modulo](#6-negative-modulo)
7. [Modulo Arithmetic Rules](#7-modulo-arithmetic-rules)
8. [Why Modulo Is Used in CP](#8-why-modulo-is-used-in-cp)
9. [Why `10^9 + 7`?](#9-why-109--7)
10. [Clock Arithmetic](#10-clock-arithmetic)
11. [Recognition Cheat Sheet](#11-recognition-cheat-sheet)

---

# 1. What Is Modulo?

Modulo gives the **remainder after division**.

```text
a % m = remainder when a is divided by m
```

Example:

```text
7 % 3 = 1
```

because:

```text
7 = 3 × 2 + 1
        ↑     ↑
    quotient remainder
```

So:

```text
Dividend = Divisor × Quotient + Remainder
```

or mathematically:

```text
a = q × m + r
```

---

# 2. Dividend, Divisor, Quotient, Remainder

For:

```text
17 % 5
```

we have:

```text
17 = 5 × 3 + 2

17 → dividend
 5 → divisor / modulus
 3 → quotient
 2 → remainder
```

Therefore:

```text
17 % 5 = 2
```

---

# 3. How `%` Is Calculated

## Mathematical modulo

For positive modulus `m`:

```text
q = floor(a / m)

r = a - q × m
```

Therefore:

```text
a mod m = a - floor(a/m) × m
```

### Example

```text
a = 7
m = 3

7 / 3 = 2.333...

floor(7/3) = 2

r = 7 - 2×3
  = 1
```

Hence:

```text
7 mod 3 = 1
```

## C++

```cpp
int a = 7;
int m = 3;

cout << a % m;   // 1
```

> **Important:** For positive numbers, C++ `%` behaves exactly as expected. Negative operands need special care.

---

# 4. Cyclic Nature of Modulo

Modulo wraps values after reaching the modulus.

For modulo `4`:

```text
0 % 4 = 0
1 % 4 = 1
2 % 4 = 2
3 % 4 = 3

4 % 4 = 0   ← wrap
5 % 4 = 1
6 % 4 = 2
7 % 4 = 3
8 % 4 = 0
```

Visual model:

```text
0 → 1 → 2 → 3
↑           ↓
└───────────┘
    mod 4
```

The possible normalized remainders are:

```text
0, 1, 2, ..., m-1
```

---

# 5. Congruence

Two integers are **congruent modulo `m`** when they have the same normalized remainder.

Notation:

```text
a ≡ b (mod m)
```

Equivalent condition:

```text
m divides (a - b)
```

or:

```text
(a - b) % m = 0
```

### Example

```text
17 ≡ 5 (mod 12)
```

because:

```text
17 % 12 = 5
5  % 12 = 5
```

Also:

```text
17 - 5 = 12
```

which is divisible by `12`.

### CP meaning

```text
same remainder
      ↓
same modulo class
      ↓
can often group with hashmap / frequency array
```

---

# 6. Negative Modulo

This is especially important in C++.

## 6.1 Mathematical modulo

Mathematically, with positive modulus `m`, we normally want:

```text
0 <= r < m
```

Example:

```text
-8 mod 7
```

Find:

```text
-8 = 7 × (-2) + 6
```

Therefore:

```text
-8 mod 7 = 6
```

---

## 6.2 C++ `%` with negative values

C++ integer division truncates toward zero.

```cpp
cout << -8 % 7;
```

produces:

```text
-1
```

because C++ effectively uses:

```text
-8 = 7 × (-1) + (-1)
```

So:

```text
C++ remainder = -1
Mathematical normalized modulo = 6
```

### Visual

```text
-8 % 7

C++:
-8 → -1

Normalize:
-1 + 7 = 6

Answer:
6
```

---

## 6.3 Safe normalization formula

Use:

```cpp
((x % m) + m) % m
```

Example:

```cpp
int x = -8;
int m = 7;

int r = ((x % m) + m) % m;

cout << r;   // 6
```

### Why the second `% m`?

Suppose:

```text
x % m = 3
```

Then:

```text
3 + 7 = 10
```

but we need the answer inside:

```text
[0, 6]
```

Therefore:

```text
10 % 7 = 3
```

So the general normalization is:

```text
((x % m) + m) % m
```

---

## 6.4 Negative modulo examples

```text
m = 5

 x     C++ x%m     normalized
-----------------------------
 7        2            2
 2        2            2
 0        0            0
-1       -1            4
-2       -2            3
-5        0            0
-6       -1            4
```

Pattern:

```text
... -2  -1   0   1   2   3   4   5 ...
     ↓   ↓   ↓   ↓   ↓   ↓   ↓   ↓
     3   4   0   1   2   3   4   0     mod 5
```

---

# 7. Modulo Arithmetic Rules

Let:

```text
A = a % m
B = b % m
```

## Addition

```text
(a + b) % m
=
((a % m) + (b % m)) % m
```

Example:

```text
(17 + 19) % 5

= (2 + 4) % 5
= 6 % 5
= 1
```

---

## Subtraction

```text
(a - b) mod m
```

In C++, normalize it:

```cpp
((a % m - b % m) + m) % m
```

or commonly:

```cpp
(a - b + m) % m
```

when `a` and `b` are already normalized.

Example:

```text
2 - 4 = -2

-2 mod 5 = 3
```

---

## Multiplication

```text
(a × b) % m
=
((a % m) × (b % m)) % m
```

Example:

```text
17 × 19 mod 5

= 2 × 4 mod 5
= 8 mod 5
= 3
```

---

## Division — WARNING

This is generally **wrong**:

```text
(a / b) % m
≠
((a % m) / (b % m)) % m
```

Modular division requires a **modular inverse** when the inverse exists:

```text
a / b  (mod m)
→
a × b^(-1) (mod m)
```

Learn this with modular inverse / Fermat's Little Theorem.

---

# 8. Why Modulo Is Used in CP

## 1. Keep huge results manageable

Counting problems may produce enormous values:

```text
2^100000
100000!
number of DP ways
```

Problems therefore often ask:

```text
answer modulo 1,000,000,007
```

We can reduce intermediate values repeatedly:

```cpp
ans = (ans + value) % MOD;
```

---

## 2. Cyclic behavior

Modulo naturally models:

```text
clock
circular array
days of week
rotations
repeating patterns
```

Example circular next index:

```cpp
next = (i + 1) % n;
```

For:

```text
n = 5
i = 4
```

we get:

```text
(4 + 1) % 5 = 0
```

so we wrap back to the beginning.

---

## 3. Divisibility

```text
x % k == 0
```

means:

```text
x is divisible by k
```

---

## 4. Group by remainder

A frequent CP transformation is:

```text
value
  ↓
value % k
  ↓
remainder class
  ↓
frequency / hashmap
```

Example:

```text
2, 7, 12, 17
```

all satisfy:

```text
x % 5 = 2
```

so:

```text
2 ≡ 7 ≡ 12 ≡ 17 (mod 5)
```

---

## Important correction: modulo does not magically prevent overflow

This can still overflow **before** `% MOD` is applied:

```cpp
int x = (a * b) % MOD;
```

if `a * b` exceeds the type's range.

Use a sufficiently wide type when the constraints permit it:

```cpp
long long x = (1LL * a * b) % MOD;
```

For values beyond even `long long`, specialized multiplication techniques may be required.

---

# 9. Why `10^9 + 7`?

A very common CP modulus is:

```cpp
const long long MOD = 1000000007LL;
```

That is:

```text
10^9 + 7
```

Important reasons:

- it is **prime**,
- it is large enough to give many residue classes,
- it is convenient for modular inverses and combinatorics,
- products of two already-reduced residues fit within signed 64-bit `long long`.

For example:

```text
(MOD - 1)^2 ≈ 10^18
```

which fits inside the maximum signed 64-bit value of about:

```text
9.22 × 10^18
```

> Do **not** assume every problem uses `10^9+7`. Always use the modulus given in the problem.

---

# 10. Clock Arithmetic

A clock is the easiest mental model for modulo.

With modulo `12`:

```text
... → 10 → 11 → 0 → 1 → 2 → ...
```

On a conventional clock, residue `0` corresponds to `12`.

### Example

```text
19 mod 12 = 7
```

Therefore:

```text
19 ≡ 7 (mod 12)
```

Both represent the same clock position.

### Negative example

```text
-4 mod 12 = 8
```

because:

```text
-4 + 12 = 8
```

Therefore:

```text
-4 ≡ 8 (mod 12)
```

Visual:

```text
             0/12
          11      1
       10            2
       9              3
        8            4
          7        5
              6
```

Modulo is therefore best viewed as:

```text
LINEAR NUMBERS
      ↓
wrap every m positions
      ↓
CYCLIC NUMBER SYSTEM
```

---

# 11. Recognition Cheat Sheet

When you see these clues:

```text
remainder
divisible
cyclic / circular
wrap around
same remainder
every k-th
periodic pattern
large answer mod M
pair remainder
clock / days
```

think:

```text
             MODULO
                │
     ┌──────────┼──────────┐
     │          │          │
 remainder   divisibility  cycles
     │          │          │
     └──────────┼──────────┘
                │
         congruence classes
                │
       frequency / grouping
```

## Contest checklist

Before coding:

```text
1. What is the modulus m?
2. Do I need x % m or normalized modulo?
3. Can x become negative?
4. Can an intermediate multiplication overflow?
5. Is the problem grouping equal remainders?
6. Is there cyclic/wrap-around behavior?
7. Is division involved? → modular inverse may be needed.
```

---

# Final Memory Card

```text
CORE DEFINITION

a = q × m + r

a mod m = r
```

```text
POSITIVE NORMALIZED RANGE

0 <= r < m
```

```text
C++ NEGATIVE NORMALIZATION

((x % m) + m) % m
```

```text
CONGRUENCE

a ≡ b (mod m)
        ⇕
m divides (a - b)
```

```text
SAFE RULES

Addition       → (a%m + b%m) % m
Subtraction    → ((a%m - b%m) + m) % m
Multiplication → (1LL * (a%m) * (b%m)) % m
Division       → requires modular inverse
```

> **Main idea:** Modulo is not just `%`. It converts integers into **remainder classes**, making divisibility, cycles, huge computations, and many CP transformations easier to model.
