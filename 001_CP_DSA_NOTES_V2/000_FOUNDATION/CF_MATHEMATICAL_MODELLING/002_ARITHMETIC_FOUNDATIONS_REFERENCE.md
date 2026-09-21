# Part 1. Arithmetic Foundations

## Table of Contents

- [1.0 Reading the Symbols (Cheat Sheet)](#10-reading-the-symbols-cheat-sheet)
- [1.1 Quotient and Remainder](#11-quotient-and-remainder)
- [1.2 Floor, Ceiling, and Ceil-Division](#12-floor-ceiling-and-ceil-division)
- [1.3 Absolute Value, Min, and Max](#13-absolute-value-min-and-max)
- [1.4 Intervals and Inequalities](#14-intervals-and-inequalities)
- [1.5 Powers, Logarithms, and Size Estimates](#15-powers-logarithms-and-size-estimates)
- [1.6 Overflow: Choosing `int` or `long long`](#16-overflow-choosing-int-or-long-long)
- [1.7 Rounding Without Decimals](#17-rounding-without-decimals)
- [1.8 Section Summary](#18-section-summary)

---

## 1.0 Reading the Symbols (Cheat Sheet)

```text
             floor(3.5)       ceil(3.5)
                  ↓                ↓
0 ─── 1 ─── 2 ─── 3 ─── ● ─── 4 ─── 5
                        3.5

floor(3.5) = 3
ceil(3.5)  = 4
```

| Symbol / Syntax | Meaning | Rule | Example |
|---|---|---|---|
| `a % b` | remainder | `a = b*q + r` | `17 % 5 = 2` |
| `a / b` | integer quotient for positive integers | fractional part discarded | `17 / 5 = 3` |
| `floor(x)` | round toward negative infinity | greatest integer `<= x` | `floor(3.9) = 3` |
| `ceil(x)` | round toward positive infinity | smallest integer `>= x` | `ceil(3.1) = 4` |
| `abs(x)` | distance from zero | non-negative magnitude | `abs(-5) = 5` |
| `a divides b` | exact divisibility | `b % a == 0` | `3` divides `12` |
| `Σ` | summation | repeated addition | `Σ A[i]` |
| `a ≡ b (mod m)` | same remainder mod `m` | `(a-b) % m == 0` | `17 ≡ 2 (mod 5)` |
| `=>` | implies | left condition guarantees right | `x%2==0 => x is even` |
| `<=>` | iff | both directions hold | `x%2==0 <=> x even` |
| `n!` | factorial | `n*(n-1)*...*1` | `4! = 24` |

```cpp
long long q = a / b;
long long r = a % b;
```

> **Real-World Case:** Pagination and fixed-size batches use quotient, remainder, and ceil-division to map item counts to pages.

### Codeforces Recognition

```text
"remainder"       → modulo
"divisible"       → a % b == 0
"distance"        → abs(a-b)
"minimum groups"  → ceil division
```

---

## 1.1 Quotient and Remainder

```text
17 items, group size = 5

┌─────┐ ┌─────┐ ┌─────┐ ┌───┐
│  5  │ │  5  │ │  5  │ │ 2 │
└─────┘ └─────┘ └─────┘ └───┘
 Group1  Group2  Group3   Rem

17 = 3 × 5 + 2
     ↑       ↑
     q       r
```

| Concept | Mathematical Rule | C++ | Example |
|---|---|---|---|
| Division identity | `a = b*q + r` | — | `17 = 5*3 + 2` |
| Quotient | complete groups | `a / b` | `17/5 = 3` |
| Remainder | leftover | `a % b` | `17%5 = 2` |
| Positive modulo normalization | remainder in `[0,m)` | `((a%m)+m)%m` | `-7 mod 5 → 3` |

For mathematical Euclidean division with positive `b`, `0 <= r < b`. C++ integer division instead truncates toward zero, so negative operands require care.

```cpp
long long q = a / b, r = a % b;
long long mod = ((a % m) + m) % m;
```

> **Real-World Case:** A circular buffer normalizes a negative index with `((i % n) + n) % n`.

### Codeforces Recognition

```text
"complete groups" → a / b
"left over"       → a % b
"wrap around"     → modulo
"same remainder"  → congruence
```

### Common Trap

```text
C++: -7 / 5 = -1 and -7 % 5 = -2
Mathematical non-negative modulo: ((-7 % 5) + 5) % 5 = 3
```

---

## 1.2 Floor, Ceiling, and Ceil-Division

```text
23 items, capacity = 5

[#####] [#####] [#####] [#####] [###..]
   1       2       3       4       5

23 = 4×5 + 3
           ↑
        leftover
```

| Goal | Formula | Safe integer code for `a >= 0, b > 0` |
|---|---|---|
| floor division | `floor(a/b)` | `a / b` |
| ceil division | `ceil(a/b)` | `(a + b - 1) / b` |
| overflow-safer ceil division | same | `a / b + (a % b != 0)` |

### Derivation

```text
a = q*b + r

r = 0  → exactly q groups
r > 0  → q full groups + 1 partial group

therefore:

ceil(a/b) = q + [r > 0]
          = (a+b-1)/b        for a >= 0, b > 0
```

### Statement → Math

```text
"Each operation changes the difference by at most k."

D = abs(a-b)

k * moves >= D
moves >= D/k

minimum integer moves = ceil(D/k)
```

```cpp
long long d = std::llabs(a - b);
long long moves = d / k + (d % k != 0);
```

> **Real-World Case:** 103 API records at 20 records per page require `ceil(103/20) = 6` pages.

### Codeforces Recognition

```text
"minimum trips / boxes / moves"
+
"each handles at most k"
        ↓
CEIL DIVISION
```

---

## 1.3 Absolute Value, Min, and Max

```text
a = -2                              b = 5
   ●────────────────────────────────●
                7 units

distance = abs(a-b) = 7
```

| Concept | Formula / Rule | Example |
|---|---|---|
| absolute value | `abs(x)` | `abs(-8)=8` |
| distance | `abs(a-b)` | `abs(-2-5)=7` |
| maximum | `(a+b+abs(a-b))/2` | `max(3,8)=8` |
| minimum | `(a+b-abs(a-b))/2` | `min(3,8)=3` |
| identity | `max(a,b)+min(a,b)=a+b` | `8+3=11` |

```cpp
long long d = std::llabs(a - b);
long long clamped = std::max(L, std::min(x, R));
```

> **Real-World Case:** UI coordinates are clamped between screen boundaries with `max(L, min(x, R))`.

### Codeforces Recognition

```text
"distance / difference / how far" → abs(a-b)
"largest / smallest"              → max / min
"force value into range"          → clamp
```

### Common Trap

`abs(LLONG_MIN)` is not representable as a signed `long long`; contest constraints normally avoid this extreme.

---

## 1.4 Intervals and Inequalities

```text
Interval A: [3 ───────────── 10]
Interval B:       [6 ───────────── 15]
Intersection:     [6 ─────── 10]

L = max(3,6)  = 6
R = min(10,15)= 10
```

| Concept | Rule |
|---|---|
| closed interval | `L <= x <= R` |
| intersection left bound | `max(L1,L2)` |
| intersection right bound | `min(R1,R2)` |
| non-empty closed intersection | `max(L1,L2) <= min(R1,R2)` |
| integer count in `[L,R]` | `R-L+1` when `L <= R` |

```cpp
long long L = std::max(L1, L2), R = std::min(R1, R2);
bool overlap = (L <= R);
```

> **Real-World Case:** Calendar conflict detection is an interval-intersection problem.

### Statement → Math

```text
"x is at least 6"  → x >= 6
"x is at most 10"  → x <= 10

Together:
6 <= x <= 10
```

### Codeforces Recognition

```text
"at least"           → lower bound
"at most"            → upper bound
"both constraints"   → intersection
"how many integers"  → R-L+1
```

---

## 1.5 Powers, Logarithms, and Size Estimates

```text
1 → 2 → 4 → 8 → 16 → 32 → ...

each arrow = ×2

after k doublings → 2^k
number of doublings to reach N ≈ log2(N)
```

| Scale | Approximation | Useful intuition |
|---|---|---|
| `2^10` | `≈ 10^3` | thousand |
| `2^20` | `≈ 10^6` | million |
| `2^30` | `≈ 10^9` | billion |
| `2^60` | `≈ 10^18` | 64-bit scale |

| Concept | Rule |
|---|---|
| repeated doubling | grows as `2^k` |
| repeated halving | takes `O(log2 N)` steps |
| positive integer bit length | `floor(log2(N))+1` |

```cpp
int bits = (n == 0) ? 1 : 64 - __builtin_clzll(n);
```

> **Real-World Case:** Binary search halves the remaining search range each step, giving logarithmic scaling.

### Codeforces Recognition

```text
"doubles each operation" → powers of 2
"halve repeatedly"       → logarithm
"N up to 1e18"           → about 60 binary steps
```

---

## 1.6 Overflow: Choosing `int` or `long long`

```text
int:
≈ -2.1e9 ───────── 0 ───────── +2.1e9

long long:
≈ -9.22e18 ─────── 0 ─────── +9.22e18
```

| Quantity | Possible size | Recommended type |
|---|---:|---|
| one value up to `1e9` | `1e9` | `int` may fit |
| `2e5 * 1e9` sum | `2e14` | `long long` |
| pair count for `2e5` | `≈2e10` | `long long` |
| `1e9 * 1e9` | `1e18` | `long long` |

```cpp
long long good = 1LL * a * b;
long long mid = low + (high - low) / 2;
```

> **Real-World Case:** Large counters, timestamps, byte totals, and financial minor-unit totals commonly exceed 32-bit range.

### Why Promotion Matters

```text
a and b are int

a * b
  ↓
computed as int FIRST
  ↓
may overflow
  ↓
then corrupted value is assigned to long long

1LL * a * b
  ↓
64-bit multiplication from the beginning
```

### Common Trap

```cpp
long long bad  = a * b;       // may overflow before assignment
long long good = 1LL * a * b; // promoted first
```

---

## 1.7 Rounding Without Decimals

```text
17 / 5 = 3.4

floor   → 3
nearest → 3
ceil    → 4

18 / 5 = 3.6

floor   → 3
nearest → 4
ceil    → 4
```

| Goal | Integer formula for `a >= 0, b > 0` |
|---|---|
| floor | `a / b` |
| ceil | `a / b + (a % b != 0)` |
| nearest, half-up | `(a + b/2) / b` |

```cpp
long long nearest = (a + b / 2) / b;
long long ceil_q = a / b + (a % b != 0);
```

> **Real-World Case:** Integer minor units such as cents avoid floating-point representation error in exact accounting logic.

### Common Trap

The nearest-integer formula above assumes non-negative integers and **half-up** tie behavior. Other rounding policies require different formulas.

---

## 1.8 Section Summary

| Goal / Statement Clue | Mathematical Pattern | Safe C++ |
|---|---|---|
| complete groups | quotient | `a / b` |
| leftovers | remainder | `a % b` |
| positive modulo | normalize remainder | `((a%m)+m)%m` |
| minimum fixed-capacity groups | ceil division | `a/b + (a%b!=0)` |
| distance | absolute difference | `llabs(a-b)` |
| common valid range | interval intersection | `max(L1,L2) <= min(R1,R2)` |
| repeated doubling/halving | powers / logarithms | `O(log N)` |
| potentially large product | 64-bit promotion | `1LL*a*b` |
| safe midpoint | avoid `low+high` overflow | `low+(high-low)/2` |
| nearest non-negative integer | half-up rounding | `(a+b/2)/b` |

### 30-Second Revision

```text
┌───────────────────────────────────────────────────────┐
│                ARITHMETIC FOUNDATIONS                 │
├───────────────────────────────────────────────────────┤
│ complete groups      → quotient                       │
│ leftovers            → modulo                         │
│ minimum groups       → ceil division                  │
│ distance             → abs(a-b)                       │
│ at least / at most   → inequalities                   │
│ common range         → max(left), min(right)          │
│ repeated ×2 or ÷2    → powers / logarithms            │
│ large sum/product    → long long / 1LL promotion      │
│ integer rounding     → explicit rounding policy       │
└───────────────────────────────────────────────────────┘
```
