# Segmented Sieve — Don't Memorize, Model It

> **Goal:** Find primes in a **small interval `[L,R]`** even when `R` is huge.

Typical constraint:
```text
1 ≤ L ≤ R ≤ 10^12
R-L ≤ 10^6
```

## Table of Contents
1. [Why Segmented Sieve?](#1-why-segmented-sieve)
2. [Core Model](#2-core-model)
3. [Two Phases](#3-two-phases)
4. [Segment Mapping](#4-segment-mapping)
5. [First Multiple in the Segment](#5-first-multiple-in-the-segment)
6. [Why Start at max(p², first)?](#6-why-start-at-maxp-first)
7. [Complete Dry Run](#7-complete-dry-run)
8. [C++ Implementation](#8-c-implementation)
9. [Complexity](#9-complexity)
10. [Contest Recognition](#10-contest-recognition)
11. [Final Memory Model](#11-final-memory-model)

---

# 1. Why Segmented Sieve?

If:
```text
R = 10^12
```
a normal sieve would try to store/process:
```text
1 ------------------------------------------ 10^12
```
But if:
```text
R-L ≤ 10^6
```
we care about only:
```text
L ================= R
      ~10^6 values
```

So don't sieve `1...R`.

```text
Normal Sieve      → store 1...R
Segmented Sieve   → store only L...R
```

---

# 2. Core Model

From factorisation:
```text
composite x = a × b
```
At least one prime factor is:
```text
≤ √x
```
Since every `x` in our interval satisfies:
```text
x ≤ R
```
every composite in `[L,R]` has a prime factor:
```text
≤ √R
```

Therefore:
```text
R may be huge
     ↓
find small primes only up to √R
     ↓
use them to eliminate composites in [L,R]
     ↓
survivors are prime
```

That is the whole segmented-sieve idea.

---

# 3. Two Phases

## Phase 1 — Base primes

Run the normal sieve only up to:
```text
√R
```

Example:
```text
R = 50
√50 ≈ 7.07

base primes = 2, 3, 5, 7
```

## Phase 2 — Sieve `[L,R]`

```text
base primes
2  3  5  7
│  │  │  │
└──┴──┴──┴─────────┐
                   ↓
             [ L ........ R ]
                   ↓
          remove their multiples
                   ↓
               survivors
                   ↓
                 primes
```

---

# 4. Segment Mapping

Instead of:
```cpp
isPrime[R + 1]
```
allocate only:
```cpp
vector<bool> isPrime(R - L + 1, true);
```

Mapping:
```text
number       L   L+1  L+2  L+3 ... R
             │    │    │    │
index        0    1    2    3 ... R-L
```

Formula:
```text
index = number - L
```

So to mark number `x`:
```cpp
isPrime[x - L] = false;
```

Example for `L=10`:
```text
10 → index 0
11 → index 1
17 → index 7
```

---

# 5. First Multiple in the Segment

For prime `p`, find the smallest multiple satisfying:
```text
multiple ≥ L
```

Model:
```text
first = ceil(L/p) × p
```

For positive integers:
```text
ceil(L/p) = (L+p-1)/p
```

Therefore:
```cpp
long long first = ((L + p - 1) / p) * p;
```

### Example

```text
L = 17
p = 5

multiples:
5, 10, 15, 20, 25...
           ↑
           first ≥ 17
```

Formula:
```text
ceil(17/5) × 5
= 4 × 5
= 20
```

---

# 6. Why Start at max(p², first)?

Suppose:
```text
L = 2
p = 3
```

The first multiple in `[L,R]` is:
```text
3
```
but `3` itself is prime—we must not remove it.

Also, multiples below `p²` already contain a smaller factor.

For `p=5`:
```text
5×2 = 10  → handled by 2
5×3 = 15  → handled by 3
5×4 = 20  → handled by 2
5×5 = 25  → first new work
```

Therefore:
```text
start = max(
    p²,
    ceil(L/p) × p
)
```

ASCII model:
```text
       first multiple ≥ L
               │
               ├──────────┐
               ↓          ↓
        ceil(L/p)×p       p²
               │          │
               └────┬─────┘
                    ↓
                  max()
                    ↓
           first useful multiple
```

---

# 7. Complete Dry Run

Find primes in:
```text
[10,30]
```

### Step 1: base primes

```text
√30 ≈ 5.47
base primes = 2,3,5
```

Initial segment:
```text
10 11 12 13 14 15 16 17 18 19 20
✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓

21 22 23 24 25 26 27 28 29 30
✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓
```

### `p = 2`

```text
first = ceil(10/2)×2 = 10
p² = 4
start = 10
```

Remove:
```text
10 12 14 16 18 20 22 24 26 28 30
```

### `p = 3`

```text
first = ceil(10/3)×3 = 12
p² = 9
start = 12
```

Remove:
```text
12 15 18 21 24 27 30
```

### `p = 5`

```text
first = ceil(10/5)×5 = 10
p² = 25

start = max(10,25) = 25
```

Remove:
```text
25 30
```

Final:
```text
10  11  12  13  14  15  16  17  18  19
×   ✓   ×   ✓   ×   ×   ×   ✓   ×   ✓

20  21  22  23  24  25  26  27  28  29  30
×   ×   ×   ✓   ×   ×   ×   ×   ×   ✓   ×
```

Answer:
```text
11, 13, 17, 19, 23, 29
```

---

# 8. C++ Implementation

## Normal sieve for base primes

```cpp
vector<int> simpleSieve(long long limit) {
    vector<bool> isPrime(limit + 1, true);

    if (limit >= 0) isPrime[0] = false;
    if (limit >= 1) isPrime[1] = false;

    for (long long i = 2; i * i <= limit; ++i) {
        if (isPrime[i]) {
            for (long long j = i * i; j <= limit; j += i)
                isPrime[j] = false;
        }
    }

    vector<int> primes;
    for (int i = 2; i <= limit; ++i)
        if (isPrime[i])
            primes.push_back(i);

    return primes;
}
```

## Segmented sieve

```cpp
vector<long long> segmentedSieve(long long L, long long R) {
    long long limit = sqrtl((long double)R);

    // Correct possible floating-point rounding.
    while ((limit + 1) <= R / (limit + 1)) ++limit;
    while (limit > 0 && limit > R / limit) --limit;

    vector<int> basePrimes = simpleSieve(limit);

    // index i represents number L+i
    vector<bool> isPrime(R - L + 1, true);

    // For the stated positive-L constraints:
    if (L == 1)
        isPrime[0] = false;

    for (long long p : basePrimes) {
        long long first =
            ((L + p - 1) / p) * p;

        long long start =
            max(p * p, first);

        for (long long x = start; x <= R; x += p)
            isPrime[x - L] = false;
    }

    vector<long long> primes;

    for (long long i = 0; i <= R - L; ++i)
        if (isPrime[i])
            primes.push_back(L + i);

    return primes;
}
```

## Complete use

```cpp
#include <bits/stdc++.h>
using namespace std;

// simpleSieve(...) here
// segmentedSieve(...) here

int main() {
    long long L, R;
    cin >> L >> R;

    vector<long long> primes = segmentedSieve(L, R);

    for (long long p : primes)
        cout << p << ' ';
}
```

Use `long long` because:
```text
L,R can be as large as 10^12
```

---

# 9. Complexity

Let:
```text
W = R-L+1
```

Phase 1:
```text
Sieve up to √R

Time   = O(√R log log √R)
Memory = O(√R)
```

Phase 2 approximately:
```text
W/2 + W/3 + W/5 + ...
```

So:
```text
Time ≈ O(W log log R)
Memory = O(W)
```

Overall useful model:
```text
O(√R log log √R + W log log R)
```

Memory:
```text
O(√R + W)
```

For:
```text
R ≤ 10^12
W ≤ about 10^6
```
we store around millions of values—not `10^12`.

---

# 10. Contest Recognition

| Clue | Think |
|---|---|
| All primes up to moderate `N` | Normal Sieve |
| One/few primality checks | Trial division |
| `R` huge, interval narrow | **Segmented Sieve** |
| `R ≤ 10^12`, `R-L ≤ 10^6` | Classic segmented sieve |
| Many factorization queries | SPF sieve |

Visual comparison:
```text
NORMAL SIEVE
1 =============================== N
          store all


SEGMENTED SIEVE
1 ===== √R             L ======== R
base primes              target
```

---

# 11. Final Memory Model

Start from one fact:
```text
composite x = a × b
       ↓
one prime factor ≤ √x
       ↓
x ≤ R
       ↓
one prime factor ≤ √R
```

Therefore:
```text
        huge R
          │
          ↓
normal sieve only to √R
          │
          ↓
      small primes
          │
          ↓
     [ L ......... R ]
          │
   mark their multiples
          │
          ↓
       survivors
          │
          ↓
        primes
```

Only two formulas matter:

```text
segment index = number - L
```

```text
start =
max(
    p²,
    ceil(L/p) × p
)
```

where:
```text
ceil(L/p) = (L+p-1)/p
```

> **Contest habit:** When `R` is enormous but `[L,R]` is narrow, don't ask *“How do I sieve to R?”* Ask: **“Which small primes can eliminate composites only inside the interval I care about?”**
