# Factorisation Techniques — Don't Memorize, Model It

> **Goal:** Don't memorize separate tricks for prime checking, divisors, and factorisation. Start from one model: `N = a × b`.

## Table of Contents
1. [Core Model](#1-core-model)
2. [Prime Factorisation](#2-prime-factorisation)
3. [Why Only Check to sqrt(N)?](#3-why-only-check-to-sqrtn)
4. [Prime Check](#4-prime-check)
5. [Finding All Divisors](#5-finding-all-divisors)
6. [Prime Factorisation in O(sqrt(N))](#6-prime-factorisation-in-osqrtn)
7. [Why the Leftover Is Prime](#7-why-the-leftover-is-prime)
8. [Number of Divisors](#8-number-of-divisors)
9. [Contest Recognition](#9-contest-recognition)
10. [Complexity Summary](#10-complexity-summary)
11. [Final Memory Model](#11-final-memory-model)

---

# 1. Core Model

Suppose:

```text
N = a × b
```

Example:

```text
36 = 4 × 9
```

Divisors appear in pairs:

```text
1 × 36
2 × 18
3 × 12
4 ×  9
6 ×  6   ← sqrt(36)
```

After `sqrt(N)`, the same pairs only reverse.

So:

```text
d divides N
     ↓
N/d is also a divisor

d × (N/d) = N
```

This is why factor problems often need only `O(sqrt(N))` candidate checks.

---

# 2. Prime Factorisation

Every integer `N > 1` has a unique prime-factor representation:

```text
N = p1^a1 × p2^a2 × p3^a3 × ...
```

Example:

```text
360
= 2 × 180
= 2 × 2 × 90
= 2 × 2 × 2 × 45
= 2³ × 3² × 5
```

Visual:

```text
360
 │
 ├─ /2 → 180
 ├─ /2 →  90
 ├─ /2 →  45     => 2³
 │
 ├─ /3 →  15
 ├─ /3 →   5     => 3²
 │
 └──────── 5      => 5¹

360 = 2³ × 3² × 5
```

Mental model:

```text
find a prime divisor
       ↓
divide it out repeatedly
       ↓
number of divisions = exponent
```

---

# 3. Why Only Check to sqrt(N)?

Suppose:

```text
N = a × b
```

Assume:

```text
a ≤ b
```

If both were greater than `sqrt(N)`:

```text
a > sqrt(N)
b > sqrt(N)
```

then:

```text
a × b > sqrt(N) × sqrt(N)
      > N
```

Impossible, because `a × b = N`.

Therefore, if `N` is composite, at least one factor must satisfy:

```text
factor ≤ sqrt(N)
```

Example:

```text
N = 100
sqrt(N) = 10

1  × 100
2  × 50
4  × 25
5  × 20
10 × 10
```

So remember:

```text
N = a × b
    ↓
one factor must be ≤ sqrt(N)
    ↓
search only up to sqrt(N)
```

---

# 4. Prime Check

A prime number has no positive divisors except:

```text
1 and itself
```

If `N` were composite, it would have some factor `≤ sqrt(N)`.

Therefore:

```text
no divisor from 2...sqrt(N)
            ↓
          N is prime
```

## Dry Run — 37

```text
sqrt(37) ≈ 6.08

37 % 2 != 0
37 % 3 != 0
37 % 4 != 0
37 % 5 != 0
37 % 6 != 0

=> prime
```

## Dry Run — 91

```text
sqrt(91) ≈ 9.5

91 % 7 = 0

91 = 7 × 13

=> composite
```

## C++

```cpp
bool isPrime(long long n) {
    if (n < 2) return false;

    for (long long i = 2; i <= n / i; ++i) {
        if (n % i == 0)
            return false;
    }

    return true;
}
```

`i <= n/i` is an overflow-safe version of `i*i <= n`.

**Time:** `O(sqrt(N))`.

For a small number of values around `10^12`, this can be practical; many queries may require better preprocessing/algorithms.

---

# 5. Finding All Divisors

Use divisor pairs:

```text
d × (N/d) = N
```

Whenever:

```text
N % d == 0
```

we obtain:

```text
d
N/d
```

## Dry Run — N = 36

```text
i=1 → 1 × 36
i=2 → 2 × 18
i=3 → 3 × 12
i=4 → 4 × 9
i=5 → not divisor
i=6 → 6 × 6
```

Visual:

```text
small                 large

 1  <---------------> 36
 2  <---------------> 18
 3  <---------------> 12
 4  <--------------->  9
 6  <--------------->  6
           ↑
        sqrt(36)
```

At `6 × 6`, add `6` only once.

Result:

```text
1, 2, 3, 4, 6, 9, 12, 18, 36
```

## C++

```cpp
vector<long long> getDivisors(long long n) {
    vector<long long> ans;

    for (long long d = 1; d <= n / d; ++d) {
        if (n % d == 0) {
            ans.push_back(d);

            if (d != n / d)
                ans.push_back(n / d);
        }
    }

    return ans;
}
```

If sorted order is needed:

```cpp
sort(ans.begin(), ans.end());
```

**Search:** `O(sqrt(N))`.

---

# 6. Prime Factorisation in O(sqrt(N))

Instead of only detecting a divisor, repeatedly divide it out.

## Dry Run — N = 360

Start:

```text
x = 360
```

Factor `2`:

```text
360 / 2 = 180   count=1
180 / 2 = 90    count=2
90  / 2 = 45    count=3

=> 2³
```

Factor `3`:

```text
45 / 3 = 15     count=1
15 / 3 = 5      count=2

=> 3²
```

Remaining:

```text
x = 5
```

So:

```text
=> 5¹
```

Final:

```text
360 = 2³ × 3² × 5
```

## C++

```cpp
vector<pair<long long,int>> primeFactors(long long n) {
    vector<pair<long long,int>> factors;

    for (long long p = 2; p <= n / p; ++p) {
        if (n % p == 0) {
            int exponent = 0;

            while (n % p == 0) {
                n /= p;
                ++exponent;
            }

            factors.push_back({p, exponent});
        }
    }

    if (n > 1)
        factors.push_back({n, 1});

    return factors;
}
```

**Worst case:** `O(sqrt(N))`.

---

# 7. Why the Leftover Is Prime

Why do we do this?

```cpp
if (n > 1)
    factors.push_back({n, 1});
```

Suppose after removing all discovered small factors:

```text
n > 1
```

If this remaining `n` were composite, it would have a factor:

```text
≤ sqrt(n)
```

But such a factor would have been discovered by the loop.

Therefore the remaining value must be prime.

Example:

```text
42
 ↓ /2
21
 ↓ /3
7
```

Remaining:

```text
7
```

So:

```text
42 = 2 × 3 × 7
```

Mental model:

```text
remove every small factor
          ↓
something > 1 remains
          ↓
it cannot still contain a small factor
          ↓
remaining value is prime
```

---

# 8. Number of Divisors

Suppose:

```text
N = p1^a1 × p2^a2 × ... × pk^ak
```

A divisor can choose the exponent of each prime.

For:

```text
p1^a1
```

possible exponents are:

```text
0, 1, 2, ..., a1
```

So there are:

```text
a1 + 1 choices
```

Each prime exponent is chosen independently.

Therefore:

```text
number of divisors
=
(a1+1)(a2+1)...(ak+1)
```

## Example — 360

```text
360 = 2³ × 3² × 5¹
```

Choices:

```text
power of 2 → 0,1,2,3 → 4 choices
power of 3 → 0,1,2   → 3 choices
power of 5 → 0,1     → 2 choices
```

Hence:

```text
#divisors
= 4 × 3 × 2
= 24
```

Don't memorize the formula. Model it:

```text
construct one divisor
        ↓
choose power of 2
AND choose power of 3
AND choose power of 5
        ↓
multiply number of choices
```

### Important distinction

The `sqrt(N)` trick means divisor **search/enumeration** needs only `O(sqrt(N))` candidate checks.

A simple pairing upper bound is:

```text
#divisors ≤ 2 × floor(sqrt(N))
```

but this is not the exact maximum divisor count; actual counts are generally much smaller.

## C++ — Count Divisors Directly

The implementation follows the same model:

```text
N = p1^a1 × p2^a2 × ...
        ↓
count each prime exponent
        ↓
answer *= (exponent + 1)
```

```cpp
long long countDivisors(long long n) {
    long long ans = 1;

    for (long long p = 2; p <= n / p; ++p) {
        if (n % p == 0) {
            int exponent = 0;

            while (n % p == 0) {
                n /= p;
                ++exponent;
            }

            ans *= (exponent + 1);
        }
    }

    // Remaining prime factor has exponent 1.
    if (n > 1)
        ans *= 2;

    return ans;
}
```

### Dry Run — `N = 360`

```text
360 = 2³ × 3² × 5¹

2³ → ans = 1 × (3+1) = 4
3² → ans = 4 × (2+1) = 12
5¹ → ans = 12 × (1+1) = 24

answer = 24
```

**Time:** `O(sqrt(N))` worst case.

---

# 9. Contest Recognition

| Problem clue | Think |
|---|---|
| Is `N` prime? | Search divisors to `sqrt(N)` |
| Find all divisors | Pair `d` with `N/d` |
| Prime factors | Repeatedly divide each factor |
| Exponent of prime `p` | Count repeated divisions |
| Number of divisors | Product of `(exponent+1)` |
| GCD/LCM structure | Compare prime exponents |
| Many factor queries | Consider sieve / SPF |

Quick recognition:

```text
N = a × b
    │
    ├── prime check
    │     └─ does any a ≤ sqrt(N) exist?
    │
    ├── divisors
    │     └─ find pair (a, N/a)
    │
    └── prime factorisation
          └─ repeatedly divide each factor
```

---

# 10. Complexity Summary

| Task | Technique | Complexity |
|---|---|---:|
| Prime check | Trial division | `O(sqrt(N))` |
| Find all divisors | Pair divisors | `O(sqrt(N))` checks |
| Prime factorisation | Trial division | `O(sqrt(N))` worst case |
| Count divisors after factorisation | Multiply `(ai+1)` | `O(# distinct primes)` |

Constraint model:

```text
one / few large N
       ↓
trial division may be enough

many queries
       ↓
repeating sqrt(N) may be expensive
       ↓
consider Sieve / SPF preprocessing
```

---

# 11. Final Memory Model

Start with only:

```text
N = a × b
```

Then derive everything:

```text
                    N = a × b
                        │
            ┌───────────┴───────────┐
            │                       │
       a ≤ sqrt(N)              b ≥ sqrt(N)
            │
            ↓
     search only to sqrt(N)
            │
    ┌───────┼────────────┐
    │       │            │
    ↓       ↓            ↓
  PRIME   DIVISORS    FACTORISE
    │       │            │
 no d    d + N/d     divide p
 found    pair       repeatedly
    │                    │
  prime              exponent
```

And after factorisation:

```text
N = p1^a1 × p2^a2 × ...

number of divisors
        ↓
choose exponent of each prime
        ↓
(a1+1)(a2+1)...
```

> **One contest habit:** Don't memorize prime check, divisor enumeration, and factorisation as unrelated algorithms. Start from `N = a × b`; the `sqrt(N)` boundary and factor-pair structure explain all three.
