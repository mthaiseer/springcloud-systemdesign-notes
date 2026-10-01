# Powers in Modulo — Modular Exponentiation

> **Goal:** Compute `a^b mod m` efficiently even when the exponent is very large.

---

## Table of Contents

1. [Problem](#1-problem)
2. [Core Observation](#2-core-observation)
3. [Binary Exponentiation](#3-binary-exponentiation)
4. [Step-by-Step Algorithm](#4-step-by-step-algorithm)
5. [Dry Run — 3^10 mod 7](#5-dry-run--310-mod-7)
6. [C++ Implementation](#6-c-implementation)
7. [Reducing a Huge Exponent](#7-reducing-a-huge-exponent)
8. [Complexity](#8-complexity)
9. [Recognition](#9-recognition)
10. [Final Memory Card](#10-final-memory-card)

---

# 1. Problem

Suppose we need:

```text
a^b mod m
```

Example:

```text
3^10 mod 7
```

For a small exponent, repeated multiplication works.

But for:

```text
2^1000000000 mod 1000000007
```

performing one billion multiplications is too slow.

We need **Binary Exponentiation**.

---

# 2. Core Observation

Instead of multiplying `a` exactly `b` times, repeatedly **square the base and halve the exponent**.

Example:

```text
3^10
```

Since:

```text
10 = 1010₂
```

we can use powers:

```text
3^1
3^2
3^4
3^8
```

Only the powers corresponding to `1` bits are needed.

For:

```text
10 = 8 + 2
```

therefore:

```text
3^10 = 3^8 × 3^2
```

This changes the work from:

```text
O(b)
```

to:

```text
O(log b)
```

---

# 3. Binary Exponentiation

Maintain:

```text
ans  = current answer
base = current power of a
exp  = remaining exponent
```

Initially:

```text
ans  = 1
base = a % m
exp  = b
```

At every step:

```text
Is exp odd?

YES → ans = (ans × base) % m

Then:

base = (base × base) % m
exp  = exp / 2
```

Diagram:

```text
            exp
             │
       ┌─────┴─────┐
       │           │
      odd         even
       │           │
       ↓           │
ans *= base        │
       └─────┬─────┘
             ↓
      base = base²
             ↓
        exp = exp/2
             ↓
           repeat
```

---

# 4. Step-by-Step Algorithm

For:

```text
powerMod(a, b, m)
```

use:

```text
1. ans  = 1
2. base = a % m

3. while b > 0:

      if b is odd:
          ans = (ans × base) % m

      base = (base × base) % m

      b = b / 2

4. return ans
```

Why does checking odd/even work?

The last binary bit tells us whether the current power contributes.

```text
b odd  → last bit = 1 → use current base
b even → last bit = 0 → skip current base
```

---

# 5. Dry Run — `3^10 mod 7`

We need:

```text
3^10 mod 7
```

Initialize:

```text
ans  = 1
base = 3
exp  = 10
```

Dry run:

```text
exp = 10  even
ans  = 1
base = 3² % 7 = 2
exp  = 5
```

```text
exp = 5   odd

ans = 1 × 2 % 7
    = 2

base = 2² % 7
     = 4

exp = 2
```

```text
exp = 2   even

ans = 2

base = 4² % 7
     = 16 % 7
     = 2

exp = 1
```

```text
exp = 1   odd

ans = 2 × 2 % 7
    = 4

base = 2² % 7
     = 4

exp = 0
```

Stop.

```text
3^10 mod 7 = 4
```

Quick verification:

```text
3^10 = 59049

59049 % 7 = 4
```

---

# 6. C++ Implementation

```cpp
long long modPow(long long base, long long exp, long long mod) {

    long long ans = 1 % mod;
    base %= mod;

    while (exp > 0) {

        if (exp & 1LL) {
            ans = (ans * base) % mod;
        }

        base = (base * base) % mod;
        exp >>= 1;
    }

    return ans;
}
```

Usage:

```cpp
cout << modPow(3, 10, 7);
```

Output:

```text
4
```

### Meaning of the bit operations

```cpp
exp & 1LL
```

checks:

```text
Is exp odd?
```

and:

```cpp
exp >>= 1;
```

means:

```text
exp = exp / 2;
```

---

# 7. Reducing a Huge Exponent

This is a **separate optimization** from binary exponentiation.

Do **not** automatically do:

```text
exp %= (m - 1)
```

for every modulus.

When `m` is prime and:

```text
gcd(a, m) = 1
```

Fermat's Little Theorem gives:

```text
a^(m-1) ≡ 1 (mod m)
```

Therefore the exponent can be reduced modulo:

```text
m - 1
```

Example:

```text
2^100 mod 7
```

Since `7` is prime:

```text
2^6 ≡ 1 (mod 7)
```

Reduce exponent:

```text
100 % 6 = 4
```

Therefore:

```text
2^100
≡ 2^4
≡ 16
≡ 2 (mod 7)
```

So:

```text
2^100 mod 7 = 2
```

### Important

```text
Base reduction:
a → a % m
```

is standard in modular exponentiation.

But:

```text
Exponent reduction:
b → b % (m-1)
```

requires the appropriate number-theory conditions.

Do not mix these two ideas.

---

# 8. Complexity

Each iteration halves the exponent:

```text
b
↓
b/2
↓
b/4
↓
b/8
↓
...
↓
0
```

Therefore:

```text
Time  = O(log b)
Space = O(1)
```

Example:

```text
b ≈ 1,000,000,000
```

needs only about:

```text
30 iterations
```

because:

```text
2^30 ≈ 10^9
```

---

# 9. Recognition

Think **modular exponentiation / binary exponentiation** when you see:

```text
a^b mod m
huge exponent
power under modulo
repeated squaring
last digits of a huge power
large exponent in number theory
```

Mental transformation:

```text
a^b mod m
     ↓
reduce base
     ↓
read exponent in binary
     ↓
square base
     ↓
use base when bit = 1
     ↓
O(log b)
```

---

# 10. Final Memory Card

```text
PROBLEM

a^b mod m
```

```text
INITIALIZE

ans  = 1
base = a % m
```

```text
LOOP

while b > 0:

    if b is odd:
        ans = ans × base % m

    base = base × base % m

    b /= 2
```

```text
COMPLEXITY

O(log b)
```

```text
KEY IDEA

Odd exponent  → use current base
Square base   → move to next power
Halve exp     → process next binary bit
```

```text
CAUTION

base %= m        → standard

exp %= (m - 1)   → NOT always valid
                   requires number-theory conditions
```

> **Contest memory:** `POWER + HUGE EXPONENT + MODULO` → think **Binary Exponentiation**.
