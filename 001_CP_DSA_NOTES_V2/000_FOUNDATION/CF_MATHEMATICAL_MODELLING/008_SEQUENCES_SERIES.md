# Part 7. Sequences & Series

> **Core idea:** A sequence follows a rule. In Codeforces, two patterns appear constantly: **add the same amount each step** → Arithmetic Progression (AP), and **multiply by the same amount each step** → Geometric Progression (GP).

## Table of Contents

- [7.1 Arithmetic Progression (AP)](#71-arithmetic-progression-ap)
- [7.2 Geometric Progression (GP)](#72-geometric-progression-gp)
- [7.3 Other Sums to Remember](#73-other-sums-to-remember)
- [Section Summary](#section-summary)
- [30-Second Revision](#30-second-revision)

---

## 7.1 Arithmetic Progression (AP)

### ASCII / Structural Visual

```text
2      5      8      11      14
│      │      │       │       │
└─ +3 ─┴─ +3 ─┴─ +3 ──┴─ +3 ──┘

first term a = 2
common difference d = 3

index:   1   2   3    4    5
value:   2   5   8   11   14
```

### Core Formula / Rule

```text
n-th term:
a_n = a + (n-1)d

sum of n terms:
S_n = n(first + last) / 2

equivalently:
S_n = n(2a + (n-1)d) / 2

special:
1+2+...+n = n(n+1)/2
```

| Goal | Formula |
|---|---|
| n-th AP term | `a + (n-1)*d` |
| AP sum | `n*(first+last)/2` |
| sum `1..n` | `n*(n+1)/2` |
| triangular threshold | find smallest `k` with `k(k+1)/2 >= n` |

### Short Derivation — n-th Term

```text
term 1 = a
term 2 = a + d
term 3 = a + 2d
...
term n = a + (n-1)d
```

Why `n-1`?

```text
From term 1 to term n
there are exactly n-1 jumps.
```

### Short Derivation — Sum Formula

Write the same AP forward and backward:

```text
S = 1   + 2  + 3  + ... + 99 + 100
S = 100 + 99 + 98 + ... +  2 +   1
    --------------------------------
2S=101 +101 +101 + ... +101 + 101
```

There are `100` copies of `101`:

```text
2S = 100 × 101
S  = 100 × 101 / 2
   = 5050
```

General form:

```text
2S = n(first + last)

S = n(first + last)/2
```

### Visual Dry Run — Sum `1..100`

```text
first = 1
last  = 100
n     = 100

pair from ends:

1  + 100 = 101
2  +  99 = 101
3  +  98 = 101
...
50 +  51 = 101

50 pairs × 101
= 5050
```

Formula:

```text
100 × 101 / 2
= 5050 ✓
```

### Visual Dry Run — Growing Daily Amount

```text
Day:     1   2   3   4   5
Amount:  1   2   3   4   5

total after 5 days:

1+2+3+4+5
= 5×6/2
= 15
```

### Statement → Mathematical Model

```text
"Day 1 gets 1 item,
 day 2 gets 2,
 day 3 gets 3, ..."

            ↓

1 + 2 + ... + k

            ↓

k(k+1)/2
```

Threshold form:

```text
"minimum days needed
to collect at least n items"

            ↓

find minimum k such that

k(k+1)/2 >= n
```

The source notes:

```text
k is approximately sqrt(2n)
```

### Codeforces Recognition

```text
"adds the same amount each step"
"1,2,3,..."
"each day one more"
"triangular number"
"minimum k with cumulative growth >= n"
            ↓
ARITHMETIC PROGRESSION
```

### Minimal C++

```cpp
long long nth = a + 1LL * (n - 1) * d;
long long sum = 1LL * n * (first + last) / 2;
```

### Common Traps / Edge Cases

- AP indexing usually starts at term `1`, hence `(n-1)d`.
- `n*(n+1)` may overflow `int`; use `long long`.
- For threshold problems, the approximate square-root value is not automatically the exact answer; verify the inequality.

> **Real-World Engineering Case:** Suppose a migration processes 1,000 records on day 1 and increases throughput by 200 records every day. Daily throughput forms an AP: `1000,1200,1400,...`. The AP term formula gives a particular day's capacity, while the AP sum gives total records migrated after `n` days without simulating every day.

---

## 7.2 Geometric Progression (GP)

### ASCII / Structural Visual

```text
1       2       4       8       16
│       │       │       │        │
└─ ×2 ──┴─ ×2 ──┴─ ×2 ──┴─ ×2 ──┘

first term a = 1
common ratio r = 2

growth:
1 → 2 → 4 → 8 → 16 → 32 → ...
```

### Core Formula / Rule

```text
n-th term:
a_n = a * r^(n-1)

sum:
S_n = a(r^n - 1)/(r - 1)    when r != 1

special:
1+2+4+...+2^(k-1)
= 2^k - 1
```

| Goal | Formula |
|---|---|
| n-th GP term | `a*r^(n-1)` |
| GP sum | `a*(r^n-1)/(r-1)` |
| powers of two sum | `2^k-1` |
| number of doublings | logarithmic in target |

### Short Derivation — n-th Term

```text
term 1 = a
term 2 = ar
term 3 = ar²
term 4 = ar³
...
term n = ar^(n-1)
```

Again, reaching term `n` requires `n-1` multiplications by `r`.

### Short Derivation — GP Sum

Let:

```text
S  = a + ar + ar² + ... + ar^(n-1)

rS =     ar + ar² + ... + ar^(n-1) + ar^n
```

Subtract:

```text
rS - S = ar^n - a

S(r-1) = a(r^n-1)

S = a(r^n-1)/(r-1)
```

### Visual Dry Run — Powers of Two

```text
1 + 2 + 4 + 8 + 16

k = 5

formula:
2^5 - 1
= 32 - 1
= 31

direct:
1+2+4+8+16
= 31 ✓
```

### Visual Dry Run — Doubling Steps

```text
start = 1

step 0:   1
step 1:   2
step 2:   4
step 3:   8
step 4:  16
step 5:  32

Only 5 doublings
already reach 32.
```

The value grows exponentially while the number of steps grows logarithmically.

### Statement → Mathematical Model

```text
"value doubles every step"

        ↓

1,2,4,8,16,...

        ↓

after k steps:
approximately 2^k

        ↓

steps to reach value N:
approximately log2(N)
```

### Codeforces Recognition

```text
"doubles each step"
"multiplies by r"
"exponential growth"
"repeatedly double until >= x"
            ↓
GEOMETRIC PROGRESSION / LOGARITHMIC STEPS
```

### Minimal C++

```cpp
long long x = 1;
int steps = 0;
while (x < target) {
    x *= 2;
    ++steps;
}
```

### Common Traps / Edge Cases

- The GP sum formula shown assumes `r != 1`.
- The source warns that signed 64-bit powers of two overflow at `2^63`; do not keep doubling blindly.
- Exponential value growth does **not** mean exponential number of loop iterations when repeatedly multiplying toward a target.

> **Real-World Engineering Case:** Dynamic arrays and hash tables often grow capacity geometrically—e.g. `1K → 2K → 4K → 8K`. Reaching roughly one million slots needs only about 20 doublings, explaining why geometric resizing requires relatively few reallocations despite very large final capacity.

---

## 7.3 Other Sums to Remember

### ASCII / Structural Visual

Two useful patterns from the source:

```text
SUM OF SQUARES

1² + 2² + 3² + ... + n²
            ↓
n(n+1)(2n+1) / 6
```

and:

```text
HARMONIC-LIKE WORK

n/1 + n/2 + n/3 + ... + n/n

large work first
      ↓
n + n/2 + n/3 + ...
      ↓
approximately n ln(n)
```

### Core Formula / Rule

```text
1² + 2² + ... + n²
=
n(n+1)(2n+1)/6
```

Source approximation:

```text
n/1 + n/2 + ... + n/n
is about
n ln(n)
```

This is the source's intuition for why certain nested divisor/sieve-style loops have `O(n log n)` total work.

### Short Derivation / Intuition

For the harmonic-like sum:

```text
n/1 + n/2 + ... + n/n

= n(1 + 1/2 + 1/3 + ... + 1/n)

harmonic sum:
H_n ≈ ln(n)

therefore:
≈ n ln(n)
```

### Visual Dry Run — Sum of Squares

For `n=4`:

```text
direct:

1² + 2² + 3² + 4²
= 1 + 4 + 9 + 16
= 30
```

Formula:

```text
4×5×9 / 6
= 180 / 6
= 30 ✓
```

### Visual Dry Run — Divisor-Style Loop Count

Take `n=12`.

Imagine loops doing roughly:

```text
i=1  → 12/1  = 12 iterations
i=2  → 12/2  =  6
i=3  → 12/3  =  4
i=4  → 12/4  =  3
i=5  → 12/5  =  2
i=6  → 12/6  =  2
...
```

Total work behaves like:

```text
12 × (1 + 1/2 + 1/3 + ... + 1/12)

≈ 12 ln(12)
```

The key modeling observation is not the exact approximation—it is that the inner-loop sizes shrink as `n/i`.

### Statement → Mathematical Model

```text
"sum of square costs:
1²+2²+...+n²"
        ↓
closed formula
        ↓
n(n+1)(2n+1)/6
```

Complexity recognition:

```text
for i = 1..n
    process about n/i items

        ↓

total work:
Σ n/i

        ↓

≈ n ln n
```

### Codeforces Recognition

```text
sum of squares           → closed formula
loop over multiples      → harmonic-style counting
n/i work for each i      → think O(n log n)
```

### Minimal C++

```cpp
long long squareSum =
    1LL * n * (n + 1) * (2 * n + 1) / 6;
```

### Common Traps / Edge Cases

- Intermediate multiplication in the square-sum formula can overflow even when the final mathematical expression is conceptually valid.
- `n ln n` here is an asymptotic approximation, not an exact equality.
- Integer division `n/i` introduces floors; use the approximation for complexity reasoning, not as an exact numerical formula.

> **Real-World Engineering Case:** Consider a maintenance job where worker `i` scans every `i`-th record among `n` records. Worker 1 scans about `n`, worker 2 about `n/2`, worker 3 about `n/3`, and so on. The total number of visits follows the harmonic-style sum, which explains why many “iterate over all multiples” algorithms are around `O(n log n)` rather than `O(n²)`.

---

## Section Summary

| Statement Clue | Pattern | Formula / Model |
|---|---|---|
| add constant each step | AP | `a+(n-1)d` |
| sum an AP | AP sum | `n(first+last)/2` |
| `1+2+...+n` | triangular number | `n(n+1)/2` |
| multiply by constant | GP | `a*r^(n-1)` |
| doubles each step | powers of two | logarithmic steps |
| `1+2+4+...` | GP sum | `2^k-1` |
| squares from `1..n` | sum of squares | `n(n+1)(2n+1)/6` |
| about `n/i` work for each `i` | harmonic behavior | about `n ln n` |

## 30-Second Revision

```text
┌──────────────────────────────────────────────────────────────┐
│           SEQUENCES & SERIES — 30 SECOND REVISION            │
├──────────────────────────────────────────────────────────────┤
│ constant ADD             → AP                                │
│ AP n-th term             → a+(n-1)d                          │
│ AP sum                   → n(first+last)/2                   │
│ 1+2+...+n                → n(n+1)/2                          │
│ constant MULTIPLY        → GP                                │
│ GP n-th term             → a*r^(n-1)                         │
│ GP sum                   → a(r^n-1)/(r-1)                    │
│ powers of 2 sum          → 2^k-1                             │
│ doubling to target       → O(log target) steps               │
│ sum of squares           → n(n+1)(2n+1)/6                   │
│ Σ n/i                    → about n ln n                      │
└──────────────────────────────────────────────────────────────┘
```
