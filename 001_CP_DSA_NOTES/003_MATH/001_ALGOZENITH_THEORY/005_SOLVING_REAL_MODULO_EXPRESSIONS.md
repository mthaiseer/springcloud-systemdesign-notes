# Solving Real Expressions in Modular Arithmetic

> **Goal:** Evaluate complete arithmetic expressions under modulo by reducing intermediate results safely and systematically.

## 1. Core Strategy

```text
Expression
    ↓
solve smallest parts
    ↓
reduce each part % m
    ↓
combine results
    ↓
normalize if negative
    ↓
final % m
```

Prefer:

```text
reduce → calculate → reduce → calculate → reduce
```

instead of carrying unnecessarily large intermediate values.

---

## 2. Addition & Subtraction

Given:

```text
a = 10, b = 15, m = 7
```

Find:

```text
(a + b) mod 7
```

Reduce first:

```text
10 % 7 = 3
15 % 7 = 1

(3 + 1) % 7 = 4
```

Therefore:

```text
(10 + 15) mod 7 = 4
```

For subtraction:

```text
(10 - 15) mod 7

10 % 7 = 3
15 % 7 = 1

(3 - 1) % 7 = 2
```

If an intermediate result is negative, normalize:

```text
((x % m) + m) % m
```

---

## 3. Multiplication

Given:

```text
a = 8, b = 6, m = 5
```

Find:

```text
(a × b) mod 5
```

Reduce:

```text
8 % 5 = 3
6 % 5 = 1

3 × 1 = 3
```

Therefore:

```text
(8 × 6) mod 5 = 3
```

Verification:

```text
48 % 5 = 3
```

---

## 4. Exponentiation

Given:

```text
a = 3, b = 4, m = 7
```

Find:

```text
3^4 mod 7
```

For this small exponent:

```text
3^4 = 81

81 % 7 = 4
```

Therefore:

```text
3^4 mod 7 = 4
```

For a large exponent:

```text
a^b mod m
```

use **binary exponentiation**:

```text
POWER + LARGE EXPONENT + MOD
              ↓
      Binary Exponentiation
```

---

## 5. Inverse Expression

Given:

```text
a = 3
m = 11
```

Find:

```text
3⁻¹ mod 11
```

Since `11` is prime, Fermat gives:

```text
a⁻¹ ≡ a^(m-2) (mod m)
```

Therefore:

```text
3⁻¹ ≡ 3^9 (mod 11)
```

Reduce while calculating:

```text
3² = 9

3⁴ = 9²
   = 81
   ≡ 4 (mod 11)

3⁸ ≡ 4²
   = 16
   ≡ 5 (mod 11)
```

Since:

```text
9 = 8 + 1
```

then:

```text
3^9
= 3^8 × 3
≡ 5 × 3
≡ 15
≡ 4 (mod 11)
```

Therefore:

```text
3⁻¹ ≡ 4 (mod 11)
```

Verify:

```text
3 × 4 = 12

12 % 11 = 1 ✓
```

---

## 6. Composite Expression

Evaluate:

```text
(5 + 3) - (6 × 2)  (mod 10)
```

Break it into parts:

```text
        (5 + 3) - (6 × 2)
            │          │
            ↓          ↓
          part A     part B
```

### Step 1 — Addition

```text
A = (5 + 3) % 10
  = 8
```

### Step 2 — Multiplication

```text
B = (6 × 2) % 10
  = 12 % 10
  = 2
```

### Step 3 — Subtraction

```text
A - B
= 8 - 2
= 6
```

Therefore:

```text
(5 + 3) - (6 × 2)
≡ 6 (mod 10)
```

Expression tree:

```text
             -
           /   \
          +     ×
         / \   / \
        5   3 6   2
         \ /   \ /
          8     2      ← reduce mod 10
           \   /
             6
             ↓
          6 mod 10
             ↓
             6
```

---

## 7. General Expression Pattern

Suppose:

```text
E = (a + b) × (c - d) + x^k
```

and we need:

```text
E mod m
```

Work from the inside out:

```text
A = (a + b) mod m

B = (c - d) mod m
B = normalize(B)

C = x^k mod m

D = (A × B) mod m

E = (D + C) mod m
```

Mental order:

```text
PARENTHESES
    ↓
POWERS
    ↓
MULTIPLICATION
    ↓
ADDITION / SUBTRACTION
    ↓
MODULO THROUGHOUT
```

---

## 8. Compact C++ Template

```cpp
using int64 = long long;

int64 norm(int64 x, int64 mod) {
    return ((x % mod) + mod) % mod;
}

int64 modAdd(int64 a, int64 b, int64 mod) {
    return (norm(a, mod) + norm(b, mod)) % mod;
}

int64 modSub(int64 a, int64 b, int64 mod) {
    return norm(norm(a, mod) - norm(b, mod), mod);
}

int64 modMul(int64 a, int64 b, int64 mod) {
    return (norm(a, mod) * norm(b, mod)) % mod;
}

int64 modPow(int64 base, int64 exp, int64 mod) {
    int64 ans = 1 % mod;
    base = norm(base, mod);

    while (exp > 0) {
        if (exp & 1LL)
            ans = (ans * base) % mod;

        base = (base * base) % mod;
        exp >>= 1;
    }

    return ans;
}
```

Composite example:

```cpp
long long mod = 10;

long long left  = modAdd(5, 3, mod);
long long right = modMul(6, 2, mod);

long long ans = modSub(left, right, mod);

cout << ans;  // 6
```

---

## 9. Final Memory Card

```text
REAL MODULAR EXPRESSION

1. Break into smaller parts
2. Follow operator precedence
3. Reduce intermediate values % m
4. Normalize negative values
5. Large power → binary exponentiation
6. Modular division → multiply by inverse
```

```text
           COMPLEX EXPRESSION
                  ↓
             break apart
                  ↓
        ┌─────────┼─────────┐
        ↓         ↓         ↓
      + / -       ×        power
        ↓         ↓         ↓
       %m        %m      modPow()
        └─────────┼─────────┘
                  ↓
               combine
                  ↓
              normalize
                  ↓
                answer
```

> **Contest habit:** **Break → reduce → combine → reduce.**
