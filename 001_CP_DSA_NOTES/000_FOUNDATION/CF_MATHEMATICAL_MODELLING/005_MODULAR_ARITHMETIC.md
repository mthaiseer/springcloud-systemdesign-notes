# Part 4. Modular Arithmetic

> **Core idea:** Modular arithmetic works with **remainders instead of full values**. In Codeforces, translate words such as **cyclic, remainder, divisible, last digit, same remainder, answer modulo** into modulo expressions.

## Table of Contents

- [4.1 The Idea: Clock Arithmetic](#41-the-idea-clock-arithmetic)
- [4.2 Fast Exponentiation](#42-fast-exponentiation)
- [4.3 Division Modulo a Prime (Inverse)](#43-division-modulo-a-prime-inverse)
- [4.4 Prefix Remainders and Pigeonhole](#44-prefix-remainders-and-pigeonhole)
- [Section Summary](#section-summary)
- [30-Second Revision](#30-second-revision)

---

## 4.1 The Idea: Clock Arithmetic

### ASCII / Structural Visual

```text
12-hour clock

          12
      11      1
   10            2
  9                3
   8              4
      7        5
          6

Start = 9
Move  = +5

9 + 5 = 14
14 % 12 = 2

Final position = 2
```

### Core Formula / Rule

```text
a ≡ b (mod m)

means:

a % m == b % m

equivalently:

m divides (a-b)
```

| Operation | Modular Rule |
|---|---|
| Addition | `(a+b) % m = ((a%m)+(b%m)) % m` |
| Multiplication | `(a*b) % m = ((a%m)*(b%m)) % m` |
| Subtraction | normalize the result into `[0,m)` |
| Same remainder | `(a-b)` is divisible by `m` |
| Cycle position | `(start + movement) % n` |

### Short Derivation

If:

```text
a = q1*m + r1
b = q2*m + r2
```

then:

```text
a+b
= (q1+q2)m + (r1+r2)

Therefore only r1 and r2 matter modulo m.
```

So:

```text
(a+b) % m
=
((a%m)+(b%m)) % m
```

### Visual Dry Run

```text
m = 7

Compute:
(23 + 40) % 7

23 % 7 = 2
40 % 7 = 5

        2 + 5
          ↓
          7
          ↓ %7
          0

Check:
23 + 40 = 63
63 % 7 = 0 ✓
```

Negative normalization:

```text
-3 % 5 in C++ = -3

Normalize:

(-3 % 5 + 5) % 5
= (-3 + 5) % 5
= 2
```

### Statement → Mathematical Model

```text
"Position starts at s.
Move d positions for t steps
on a cycle of size n."

                ↓

total movement = t*d

                ↓

position = (s + t*d) % n
```

### Codeforces Recognition

```text
"remainder"              → %
"divisible"              → x % m == 0
"last digit"             → % 10
"even / odd"             → % 2
"cyclic / circular"      → modulo
"same remainder"         → difference divisible by m
"answer modulo p"        → reduce during computation
```

### Minimal C++

```cpp
long long r = ((x % m) + m) % m;
long long pos = (s + t * d) % n;
```

### Common Traps / Edge Cases

```text
C++ negative remainder:

-3 % 5 = -3
```

Normalize with:

```cpp
((x % m) + m) % m
```

Also ensure multiplication does not overflow **before** `% m`.

> **Real-World Engineering Case:** Circular buffers map arbitrary logical positions back into a fixed-size array using modulo.

---

## 4.2 Fast Exponentiation

### ASCII / Structural Visual

```text
Compute 3^13

13 in binary:

13 = 8 + 4 + 1
   = 1101₂

Therefore:

3^13
= 3^(8+4+1)
= 3^8 × 3^4 × 3^1

Instead of multiplying 3 thirteen times:

3 → 3² → 3⁴ → 3⁸
      repeated squaring
```

### Core Formula / Rule

```text
If exponent bit is 1:
    result = result * base

Every step:
    base = base²
    exponent /= 2
```

| Property | Result |
|---|---|
| Naive exponentiation | `O(e)` |
| Binary exponentiation | `O(log e)` |
| Exponent even | `a^e = (a^(e/2))²` |
| Exponent odd | use current power once |

### Short Derivation

```text
e = binary bits

Example:
13 = 1101₂
   = 8 + 4 + 1

a^13
= a^8 × a^4 × a

Only powers corresponding to set bits are multiplied.
```

### Visual Dry Run — `3^13 mod 17`

```text
Start:
result = 1
base   = 3
e      = 13

e=13 odd:
result = 1×3 %17 = 3
base   = 3² %17  = 9
e      = 6

e=6 even:
result = 3
base   = 9² %17 = 13
e      = 3

e=3 odd:
result = 3×13 %17 = 5
base   = 13² %17 = 16
e      = 1

e=1 odd:
result = 5×16 %17 = 12
base   = 16² %17 = 1
e      = 0

Answer:
3^13 mod 17 = 12
```

### Statement → Mathematical Model

```text
"Compute a^e mod m"
"e may be as large as 10^18"

                ↓

O(e) multiplication impossible

                ↓

write exponent in binary

                ↓

repeated squaring

                ↓

O(log e)
```

### Codeforces Recognition

```text
"huge exponent"
"power modulo m"
"exponent up to 1e18"
        ↓
BINARY EXPONENTIATION
```

### Minimal C++

```cpp
if (e & 1) result = result * a % mod;
a = a * a % mod; e >>= 1;
```

### Common Traps / Edge Cases

- `a^0 = 1`; modulo `m`, initialize `result = 1 % m`.
- Reduce the base first with `a %= mod`.
- Multiplication can overflow if the modulus/value range is too large for `long long`.

> **Real-World Engineering Case:** Cryptographic-style modular exponentiation relies on repeated squaring because direct exponentiation is infeasible for huge exponents.

---

## 4.3 Division Modulo a Prime (Inverse)

### ASCII / Structural Visual

```text
Normal arithmetic:

a / b

Modular arithmetic:
division is replaced by multiplication
by an inverse.

a / b (mod p)
       ↓
a × b⁻¹ (mod p)

Need:

b × b⁻¹ ≡ 1 (mod p)
```

### Core Formula / Rule

For prime `p` and `b` not divisible by `p`:

```text
Fermat:

b^(p-1) ≡ 1 (mod p)

Divide conceptually by b:

b^(p-2) ≡ b⁻¹ (mod p)

Therefore:

a / b (mod p)
=
a × b^(p-2) (mod p)
```

| Goal | Formula |
|---|---|
| inverse of `b` | `b^(p-2) mod p` |
| modular division | `a * inverse(b) mod p` |
| requirement | `p` prime and `b % p != 0` |

### Short Derivation

```text
Fermat:
b^(p-1) ≡ 1 (mod p)

Rewrite:
b × b^(p-2) ≡ 1 (mod p)

Definition of inverse:
b × b⁻¹ ≡ 1 (mod p)

Therefore:
b⁻¹ ≡ b^(p-2) (mod p)
```

### Visual Dry Run — `10 / 3 mod 7`

```text
p = 7
b = 3

inverse(3)
= 3^(7-2) mod 7
= 3^5 mod 7
= 5

Check:
3 × 5 = 15
15 % 7 = 1 ✓

Now:

10 / 3 mod 7
= 10 × 5 mod 7
= 50 % 7
= 1
```

### Statement → Mathematical Model

```text
"answer contains division"
+
"answer modulo prime p"

            ↓

DO NOT use ordinary integer division

            ↓

replace /b with × inverse(b)

            ↓

inverse(b) = b^(p-2) mod p
```

### Codeforces Recognition

```text
"divide under modulo"
"fraction modulo 1e9+7"
"combination formula modulo prime"
        ↓
MODULAR INVERSE
```

### Minimal C++

```cpp
long long inv = power(b, p - 2, p);
long long ans = (a % p) * inv % p;
```

### Common Traps / Edge Cases

- Fermat's `b^(p-2)` inverse formula requires prime modulus `p`.
- `b % p == 0` has no multiplicative inverse modulo `p`.
- Ordinary `a / b % p` is generally **not** modular division.

> **Real-World Engineering Case:** Modular inverses let exact combinatorial ratios be computed under a prime modulus without floating-point division.

---

## 4.4 Prefix Remainders and Pigeonhole

### ASCII / Structural Visual

Let:

```text
A = [2, 3, 1, 4]
m = 5
```

Prefix sums:

```text
P0 = 0
P1 = 2
P2 = 5
P3 = 6
P4 = 10

Remainders mod 5:

P0 → 0
P1 → 2
P2 → 0
P3 → 1
P4 → 0

Remainder 0 repeats.
```

### Core Formula / Rule

For prefix sums:

```text
subarray sum (l,r]
= P[r] - P[l]
```

It is divisible by `m` when:

```text
(P[r] - P[l]) % m = 0
```

which means:

```text
P[r] % m = P[l] % m
```

| Observation | Meaning |
|---|---|
| same prefix remainder | subarray between them divisible by `m` |
| `n+1` prefixes | `P0...Pn` |
| only `m` remainder classes | `0...m-1` |
| `n >= m` | two prefixes must share a remainder |

### Short Derivation

```text
P[r] % m = P[l] % m

            ↓

P[r] - P[l] ≡ 0 (mod m)

            ↓

subarray sum is divisible by m
```

### Visual Dry Run

```text
A = [2,3,1,4]
m = 5

i       0   1   2   3   4
P[i]    0   2   5   6  10
P%m     0   2   0   1   0
        ↑       ↑
        same remainder

Use P0 and P2:

P2 - P0
= 5 - 0
= 5

5 % 5 = 0 ✓

Subarray:
[2,3]
```

### Pigeonhole Model

```text
n >= m

prefixes:
P0, P1, ..., Pn

count = n+1

remainder boxes:
0,1,...,m-1

count = m

n+1 > m
        ↓
at least two prefixes enter same box
        ↓
their difference is divisible by m
        ↓
a non-empty divisible subarray exists
```

### Statement → Mathematical Model

```text
"Find a subarray whose sum is divisible by m"

                  ↓

subarray sum = P[r]-P[l]

                  ↓

(P[r]-P[l]) % m = 0

                  ↓

P[r] % m = P[l] % m

                  ↓

look for repeated prefix remainder
```

### Codeforces Recognition

```text
"subarray sum divisible by m"
        ↓
PREFIX REMAINDERS

"n >= m"
+
"prove such subarray exists"
        ↓
PIGEONHOLE PRINCIPLE
```

### Minimal C++

```cpp
pref = ((pref + a[i]) % m + m) % m;
if (seen[pref]) { /* repeated remainder */ }
```

### Common Traps / Edge Cases

- Include `P0 = 0`; it detects a prefix already divisible by `m`.
- Normalize remainders when array values may be negative.
- Store the first occurrence when you need to reconstruct a subarray.
- The pigeonhole guarantee here relies on the stated `n >= m` condition.

> **Real-World Engineering Case:** Prefix remainder classes compress large cumulative totals into a small set of states, making repeated modular states easy to detect.

---

## Section Summary

| Statement Clue | Mathematical Pattern | Safe Implementation |
|---|---|---|
| cyclic position | modulo | `(s+movement)%n` |
| negative remainder | normalize | `((x%m)+m)%m` |
| same remainder | congruence | difference divisible by `m` |
| huge exponent | binary exponentiation | repeated squaring |
| divide modulo prime | modular inverse | `a*power(b,p-2,p)%p` |
| subarray divisible by `m` | equal prefix remainders | store `prefix % m` |
| `n >= m` existence argument | pigeonhole | `n+1` prefixes, `m` boxes |

## 30-Second Revision

```text
┌──────────────────────────────────────────────────────────────┐
│             MODULAR ARITHMETIC — 30 SECOND REVISION          │
├──────────────────────────────────────────────────────────────┤
│ cyclic / wrap around       → modulo                          │
│ same remainder             → difference divisible by m       │
│ negative C++ remainder     → ((x%m)+m)%m                     │
│ huge power mod m           → binary exponentiation           │
│ divide modulo prime        → multiply by b^(p-2)             │
│ divisible subarray         → equal prefix remainders          │
│ n+1 prefixes, m classes    → pigeonhole when n >= m          │
└──────────────────────────────────────────────────────────────┘
```
