# Part 3. Number Theory Foundations

> **Core modeling idea:** When a Codeforces statement says **divisible, factor, prime, equal groups, common divisor, common multiple, coprime**, translate the words into divisibility mathematics before choosing an algorithm.

## Table of Contents

- [3.1 Divisors and Multiples](#31-divisors-and-multiples)
- [3.2 Prime Numbers and Factorization](#32-prime-numbers-and-factorization)
- [3.3 GCD and LCM](#33-gcd-and-lcm)
- [3.4 Extra Facts](#34-extra-facts)
- [Section Summary](#section-summary)
- [30-Second Revision](#30-second-revision)

---

## 3.1 Divisors and Multiples

### ASCII / Structural Visual

```text
12 items split into groups of 4

┌────────┐ ┌────────┐ ┌────────┐
│ oooo   │ │ oooo   │ │ oooo   │
└────────┘ └────────┘ └────────┘
     4          4          4

12 = 4 × 3 + 0
         ↑
     no remainder

Therefore:
4 divides 12
12 is a multiple of 4
```

### Core Formula / Rule

```text
d divides n
    ⇕
n % d == 0
    ⇕
n = d × k   for some integer k
```

| Concept | Meaning | Test |
|---|---|---|
| `d` divides `n` | `n` forms equal groups of size `d` | `n % d == 0` |
| divisor | a number that divides `n` exactly | remainder `0` |
| multiple | `n = d*k` | multiplication form |
| factor pair | `d` and `n/d` | found together |

### Short Derivation — Why Only Check to `sqrt(n)`?

```text
n = d × q

If BOTH d and q were > sqrt(n):

d × q > sqrt(n) × sqrt(n)
      > n

Impossible.

So every factor pair has at least
one member <= sqrt(n).
```

### Visual Dry Run — Divisors of `12`

```text
d = 1 → 12 % 1 = 0 → pair (1,12)
d = 2 → 12 % 2 = 0 → pair (2,6)
d = 3 → 12 % 3 = 0 → pair (3,4)

Stop after sqrt(12).

Divisors:
1, 2, 3, 4, 6, 12
```

### Statement → Mathematical Model

```text
"Can n items be split into equal groups of size d?"
                         ↓
               remainder must be 0
                         ↓
                    n % d == 0
                         ↓
                    d divides n
```

### Codeforces Recognition

```text
"divisible by"
"equal groups"
"every k-th"
"factor / divisor"
"period"
       ↓
DIVISIBILITY / MULTIPLES
```

### Minimal C++

```cpp
bool divides = (n % d == 0);
for (long long d = 1; d <= n / d; ++d) if (n % d == 0) { /* d, n/d */ }
```

### Common Traps / Edge Cases

- Do not count `sqrt(n)` twice when `n` is a perfect square.
- Dividing by `0` is invalid.
- For positive-divisor problems, normally use positive `d`.
- `d <= n / d` avoids possible overflow from `d*d <= n`.

> **Real-World Engineering Case:** Sharding `n` records equally across `d` workers requires `n % d == 0`.

---

## 3.2 Prime Numbers and Factorization

### ASCII / Structural Visual

```text
60
│
├─ 2 × 30
│      │
│      └─ 2 × 15
│             │
│             └─ 3 × 5
│
└───────────────

60 = 2² × 3¹ × 5¹
```

### Core Formula / Rule

If

```text
n = p1^e1 × p2^e2 × ... × pk^ek
```

then:

```text
number of divisors
= (e1+1)(e2+1)...(ek+1)
```

and:

```text
n is a perfect square
⇔ every prime exponent is even
⇔ n has an odd number of positive divisors
```

| Concept | Rule |
|---|---|
| Prime | exactly two positive divisors: `1` and itself |
| Composite | has a non-trivial divisor |
| `1` | neither prime nor composite |
| Prime factorization | product of prime powers |
| Divisor count | product of `(exponent + 1)` |

### Short Derivation — Divisor Count

For:

```text
60 = 2² × 3¹ × 5¹
```

a divisor can choose:

```text
power of 2 → 0,1,2    = 3 choices
power of 3 → 0,1      = 2 choices
power of 5 → 0,1      = 2 choices

total = 3 × 2 × 2 = 12 divisors
```

### Visual Dry Run — Factorize `60`

```text
n = 60

p=2:
60 / 2 = 30     exponent of 2 = 1
30 / 2 = 15     exponent of 2 = 2

p=3:
15 / 3 = 5      exponent of 3 = 1

loop stops because p*p > remaining n

remaining n = 5 > 1
→ 5 is a prime factor

60 = 2² × 3 × 5
```

Divisor count:

```text
(2+1)(1+1)(1+1)
= 3 × 2 × 2
= 12
```

### Statement → Mathematical Model

```text
"How many divisors does n have?"
               ↓
prime-factorize n
               ↓
n = Π pi^ei
               ↓
answer = Π(ei+1)
```

### Codeforces Recognition

```text
"prime"
"factorization"
"number of divisors"
"odd divisor"
"perfect square"
       ↓
PRIME EXPONENTS
```

### Minimal C++

```cpp
for (long long p = 2; p <= n / p; ++p)
    while (n % p == 0) n /= p;
```

### Common Traps / Edge Cases

- `1` is **not prime**.
- After trial division, if `n > 1`, that remaining value is a prime factor.
- Preserve the original number if it is needed later because factorization repeatedly modifies `n`.
- Use `p <= n/p` instead of `p*p <= n` when overflow matters.

> **Real-World Engineering Case:** Prime-factor decomposition exposes multiplicative structure, similar to decomposing a large configuration into independent primitive components.

---

## 3.3 GCD and LCM

### ASCII / Structural Visual

```text
a = 4
b = 6

Multiples of 4:
4, 8, 12, 16, 20, 24, ...

Multiples of 6:
6, 12, 18, 24, ...

          first common
               ↓
              12
          lcm(4,6)=12

Divisors of 4: 1,2,4
Divisors of 6: 1,2,3,6
                 ↑
          largest common
          gcd(4,6)=2
```

### Core Formula / Rule

```text
gcd(a,b) = gcd(b, a % b)

gcd(a,b) × lcm(a,b) = a × b

lcm(a,b) = a / gcd(a,b) × b
```

For subtraction:

```text
gcd(a,b) = gcd(a, b-a)
```

| Statement Pattern | Mathematical Model |
|---|---|
| divisible by both `a` and `b` | multiples of `lcm(a,b)` |
| largest equal group size | `gcd(a,b)` |
| repeated subtraction | gcd invariant |
| exact common cycle | LCM |
| coprime | `gcd(a,b) == 1` |

### Short Derivation — Euclid

Let:

```text
a = qb + r
```

Any number dividing both `a` and `b` also divides:

```text
a - qb = r
```

Therefore:

```text
gcd(a,b) = gcd(b,r)
         = gcd(b, a % b)
```

### Visual Dry Run — `gcd(48,18)`

```text
48 = 2×18 + 12
gcd(48,18)
      ↓
gcd(18,12)

18 = 1×12 + 6
      ↓
gcd(12,6)

12 = 2×6 + 0
      ↓
gcd(6,0)

answer = 6
```

LCM example:

```text
lcm(6,8)

gcd(6,8) = 2

lcm = 6 / 2 × 8
     = 3 × 8
     = 24
```

### Statement → Mathematical Model

```text
"largest size that divides both quantities"
                    ↓
                 gcd(a,b)

"smallest positive value divisible by both"
                    ↓
                 lcm(a,b)
```

### Codeforces Recognition

```text
"greatest common divisor"
"equal groups for both"
       ↓
GCD

"first time two cycles meet"
"divisible by both"
       ↓
LCM
```

### Minimal C++

```cpp
long long g = std::gcd(a, b);
long long l = a / g * b;  // for positive a,b
```

### Common Traps / Edge Cases

- Prefer `a / gcd(a,b) * b` over `a*b/gcd(a,b)` to reduce overflow risk.
- The final LCM can still overflow `long long`.
- With positive inputs, `gcd(a,b)` is positive.
- For a zero input, C++ `std::gcd(a,0)` returns `abs(a)`.

> **Real-World Engineering Case:** LCM models when two periodic jobs next align; GCD models the largest uniform block size shared by two capacities.

---

## 3.4 Extra Facts

### ASCII / Structural Visual

```text
NUMBER THEORY CONNECTION MAP

prime factorization
      │
      ├──→ divisor count
      │
      ├──→ sum of divisors
      │
      └──→ gcd / lcm structure

gcd(a,b)
      │
      ├── = 1 → coprime
      │
      └── divides c
              ↓
        ax + by = c
        may have integer solutions
```

### Core Formula / Rule

| Fact | Formula / Rule |
|---|---|
| Sum of divisors | product of `(p^(e+1)-1)/(p-1)` |
| Bézout / linear equation feasibility | `ax+by=c` has integer solutions iff `gcd(a,b)` divides `c` |
| Coprime | `gcd(a,b)=1` |
| Consecutive integers | always coprime |

### Short Derivation — Sum of Divisors

If:

```text
n = p^e
```

its divisors are:

```text
1, p, p², ..., p^e
```

Their sum is the geometric series:

```text
1 + p + p² + ... + p^e
= (p^(e+1)-1)/(p-1)
```

For independent prime powers, multiply these sums.

### Visual Dry Run — Sum of Divisors of `12`

```text
12 = 2² × 3¹

2-part:
1 + 2 + 4 = 7

3-part:
1 + 3 = 4

sum of divisors:
7 × 4 = 28

Direct check:
1 + 2 + 3 + 4 + 6 + 12
= 28 ✓
```

Coprime example:

```text
gcd(8,9) = 1
→ 8 and 9 are coprime

Consecutive numbers:
gcd(n,n+1)=1
```

### Statement → Mathematical Model

```text
"sum all divisors"
        ↓
prime factorization
        ↓
geometric series per prime power
        ↓
multiply

"can ax + by equal c using integers?"
        ↓
g = gcd(a,b)
        ↓
c % g == 0
```

### Codeforces Recognition

```text
"coprime"          → gcd == 1
"sum of divisors"  → prime powers + geometric series
"ax + by = c"      → gcd divisibility
```

### Minimal C++

```cpp
bool coprime = (std::gcd(a, b) == 1);
bool solvable = (c % std::gcd(a, b) == 0);
```

### Common Traps / Edge Cases

- The sum-of-divisors formula assumes a valid prime factorization.
- Large powers and divisor sums can overflow fixed-width integers.
- For `ax+by=c`, divisibility gives integer-solution feasibility; extra constraints such as `x >= 0` and `y >= 0` need additional analysis.
- Consecutive positive integers are coprime.

> **Real-World Engineering Case:** Coprime cycle lengths avoid frequent synchronization, while shared factors cause periodic alignment.

---

## Section Summary

| Statement Clue | Think | Core Test / Formula |
|---|---|---|
| divisible / equal groups | divisors | `n % d == 0` |
| enumerate divisors | factor pairs | check to `sqrt(n)` |
| prime / factors | prime factorization | divide by candidate primes |
| number of divisors | prime exponents | product of `(ei+1)` |
| perfect square | exponent parity | every exponent even |
| largest common group | GCD | `std::gcd(a,b)` |
| divisible by both | LCM | `a/gcd(a,b)*b` |
| coprime | GCD | `gcd(a,b)==1` |
| `ax+by=c` | Bézout / Diophantine | `gcd(a,b)` divides `c` |
| sum of divisors | geometric series | product over prime powers |

## 30-Second Revision

```text
┌────────────────────────────────────────────────────────────┐
│            NUMBER THEORY — 30 SECOND REVISION              │
├────────────────────────────────────────────────────────────┤
│ d divides n            → n % d == 0                        │
│ divisor enumeration    → factor pairs up to sqrt(n)        │
│ prime factorization    → n = Π p^e                         │
│ divisor count          → Π(e+1)                            │
│ perfect square         → all prime exponents even          │
│ gcd                    → Euclidean algorithm               │
│ lcm                    → a/gcd(a,b)*b                      │
│ coprime                → gcd(a,b) == 1                     │
│ ax+by=c                → gcd(a,b) divides c                │
│ sum of divisors        → geometric series per prime power  │
└────────────────────────────────────────────────────────────┘
```
