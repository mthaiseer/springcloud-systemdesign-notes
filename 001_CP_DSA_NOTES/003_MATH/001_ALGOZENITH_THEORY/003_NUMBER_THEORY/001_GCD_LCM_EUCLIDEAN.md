# GCD & LCM — Euclidean Algorithm: Don't Memorize, Model It

> **Goal:** Don't memorize `gcd(a,b) = gcd(b,a%b)` blindly. Understand why keeping the remainder preserves the GCD.

## Table of Contents
1. [GCD From Zero](#1-gcd-from-zero)
2. [The Common-Block Model](#2-the-common-block-model)
3. [The Euclidean Observation](#3-the-euclidean-observation)
4. [Why the Remainder Works](#4-why-the-remainder-works)
5. [Euclidean Algorithm + Dry Run](#5-euclidean-algorithm--dry-run)
6. [C++](#6-c)
7. [Coprime Numbers](#7-coprime-numbers)
8. [LCM — Derive, Don't Memorize](#8-lcm--derive-dont-memorize)
9. [GCD/LCM of an Array](#9-gcdlcm-of-an-array)
10. [Extended Euclid — Where It Fits](#10-extended-euclid--where-it-fits)
11. [Contest Recognition](#11-contest-recognition)
12. [Memory Card](#12-memory-card)

---

# 1. GCD From Zero

**GCD = largest positive number that divides both numbers exactly.**

Example:

```text
12 → 1, 2, 3, 4, 6, 12
18 → 1, 2, 3, 6, 9, 18

common → 1, 2, 3, 6
largest → 6

gcd(12,18) = 6
```

Also:

```text
gcd(4,8) = 4
gcd(4,5) = 1
```

If:

```text
gcd(a,b) = 1
```

then `a` and `b` are **coprime**.

---

# 2. The Common-Block Model

Instead of thinking **"search all factors"**, ask:

> What is the largest equal block size that fits both numbers exactly?

For `12` and `18`:

```text
12: [---6---][---6---]

18: [---6---][---6---][---6---]
```

A block of size `6` divides both exactly.

```text
gcd(12,18) = 6
```

This is the useful mental model:

```text
largest common equal unit
          ↓
         GCD
```

---

# 3. The Euclidean Observation

Take:

```text
48 and 18
```

Fit as many complete `18`s as possible inside `48`:

```text
48 = 18×2 + 12
```

Visual:

```text
48
┌────────18────────┬────────18────────┬────12────┐
│   complete copy  │   complete copy  │ remainder│
└──────────────────┴──────────────────┴───────────┘
```

The important observation:

```text
gcd(48,18)
=
gcd(18,12)
```

Why?

The two complete copies of `18` add no new information about the common divisor.

Only the leftover `12` matters.

So think:

```text
larger number
      ↓
remove complete copies of smaller
      ↓
keep remainder
      ↓
same GCD, smaller problem
```

---

# 4. Why the Remainder Works

Division gives:

```text
a = q×b + r
```

Therefore:

```text
r = a - q×b
```

Suppose `d` divides both `a` and `b`:

```text
d | a
d | b
```

Then `d` also divides:

```text
a - q×b
```

But:

```text
a - q×b = r
```

Therefore:

```text
d | r
```

So the common divisors are preserved when we replace `a` by the remainder.

Hence:

```text
gcd(a,b) = gcd(b, a%b)
```

### Don't memorize the equation

Remember this:

```text
a = whole copies of b + remainder

remove whole copies
        ↓
keep remainder
        ↓
common divisor does not change
```

---

# 5. Euclidean Algorithm + Dry Run

Repeat:

```text
(a,b)
  ↓
(b, a%b)
```

until the remainder becomes `0`.

For:

```text
gcd(48,18)
```

### Step 1

```text
48 = 18×2 + 12

gcd(48,18)
→ gcd(18,12)
```

### Step 2

```text
18 = 12×1 + 6

gcd(18,12)
→ gcd(12,6)
```

### Step 3

```text
12 = 6×2 + 0

gcd(12,6)
→ gcd(6,0)
```

Now:

```text
gcd(6,0) = 6
```

Complete trace:

```text
(48,18)
    ↓ remainder 12
(18,12)
    ↓ remainder 6
(12,6)
    ↓ remainder 0
(6,0)
    ↓
GCD = 6
```

Why stop at zero?

```text
gcd(x,0) = |x|
```

The last non-zero number is the GCD.

**Time:** `O(log(min(a,b)))`.

---

# 6. C++

## Recursive

```cpp
long long gcdEuclid(long long a, long long b) {
    if (b == 0) return a;
    return gcdEuclid(b, a % b);
}
```

The code directly follows the model:

```text
gcd(48,18)
→ gcd(18,12)
→ gcd(12,6)
→ gcd(6,0)
→ 6
```

## Iterative

```cpp
long long gcdEuclid(long long a, long long b) {
    while (b != 0) {
        long long r = a % b;
        a = b;
        b = r;
    }
    return a;
}
```

In C++17:

```cpp
#include <numeric>

long long g = std::gcd(a, b);
```

---

# 7. Coprime Numbers

Two integers are **coprime** when:

```text
gcd(a,b) = 1
```

Example:

```text
8  = 2³
15 = 3×5

no shared prime factor
        ↓
gcd(8,15) = 1
```

Important:

```text
coprime ≠ both numbers are prime
```

`8` and `15` are composite but coprime.

---

# 8. LCM — Derive, Don't Memorize

**LCM = smallest positive number divisible by both numbers.**

For `4` and `6`:

```text
multiples of 4:  4   8  12  16  20  24 ...
multiples of 6:  6      12      18  24 ...
                        ↑
                  first meeting

lcm(4,6) = 12
```

Mental model:

```text
two repeating cycles
        ↓
first common meeting
        ↓
        LCM
```

## Why GCD and LCM are related

Take:

```text
12 = 2² × 3
18 = 2  × 3²
```

GCD keeps the shared prime copies:

```text
gcd = 2 × 3 = 6
```

LCM keeps enough copies to contain both:

```text
lcm = 2² × 3² = 36
```

This gives:

```text
gcd(a,b) × lcm(a,b) = a × b
```

Therefore:

```text
lcm(a,b) = (a / gcd(a,b)) × b
```

### Why divide first in code?

Prefer:

```cpp
(a / gcd(a,b)) * b
```

over:

```cpp
a * b / gcd(a,b)
```

because `a*b` may overflow before the division.

## Dry Run

```text
a = 12
b = 18

gcd(12,18) = 6

lcm
= (12/6) × 18
= 2 × 18
= 36
```

C++:

```cpp
long long lcmSafe(long long a, long long b) {
    if (a == 0 || b == 0) return 0;
    return (a / std::gcd(a, b)) * b;
}
```

---

# 9. GCD/LCM of an Array

For:

```text
[24, 36, 60]
```

Don't search for a new formula.

Reduce the array one value at a time:

```text
g = 24

g = gcd(24,36)
  = 12

g = gcd(12,60)
  = 12
```

Therefore:

```text
gcd(24,36,60) = 12
```

C++:

```cpp
long long g = a[0];

for (int i = 1; i < n; ++i) {
    g = std::gcd(g, a[i]);
}
```

LCM uses the same reduction:

```cpp
long long l = a[0];

for (int i = 1; i < n; ++i) {
    long long g = std::gcd(l, a[i]);
    l = (l / g) * a[i];
}
```

Watch for overflow because LCM can grow quickly.

---

# 10. Extended Euclid — Where It Fits

Ordinary Euclid answers:

```text
gcd(a,b) = ?
```

Extended Euclid additionally finds `x` and `y` such that:

```text
a×x + b×y = gcd(a,b)
```

Model:

```text
Euclidean Algorithm
        ↓
      find GCD

Extended Euclidean Algorithm
        ↓
      find GCD
        +
find coefficients x,y
```

It is useful later for:

```text
modular inverse
linear Diophantine equations
number-theory equations
```

Learn Extended Euclid after ordinary Euclid is automatic.

---

# 11. Contest Recognition

## Think GCD

Clues such as:

```text
largest equal piece
largest common block
largest step dividing all values
divide every value exactly
common divisor
make values coprime
```

Model:

```text
largest common equal unit
          ↓
         GCD
```

## Think LCM

Clues such as:

```text
when will two cycles meet?
first common time
smallest value divisible by all
events repeat every a and b units
synchronize repeating events
```

Model:

```text
smallest common meeting point
            ↓
           LCM
```

Example:

```text
A repeats every 4 sec:
4, 8, 12, 16, ...

B repeats every 6 sec:
6, 12, 18, 24, ...

first meeting = 12
              = lcm(4,6)
```

---

# 12. Memory Card

```text
GCD
===

Meaning:
largest equal unit dividing both exactly

Core equation:
a = q×b + r

Don't memorize:
gcd(a,b)=gcd(b,a%b)

Understand:
remove complete copies of b
        ↓
keep remainder r
        ↓
common divisors stay unchanged

Algorithm:
(a,b)
  ↓
(b,a%b)
  ↓
repeat
  ↓
remainder = 0
  ↓
last non-zero = GCD
```

```text
LCM
===

Meaning:
smallest positive value divisible by both

Model:
repeating multiples / cycles
        ↓
first common meeting

Relationship:
gcd × lcm = a × b

Safe computation:
lcm = (a/gcd) × b
```

## Final Recognition

```text
largest common equal unit / step
              ↓
             GCD

smallest common meeting / cycle
              ↓
             LCM
```

> **The one Euclid observation to remember:**
>
> ```text
> Remove whole copies → keep the remainder → the GCD does not change.
> ```
