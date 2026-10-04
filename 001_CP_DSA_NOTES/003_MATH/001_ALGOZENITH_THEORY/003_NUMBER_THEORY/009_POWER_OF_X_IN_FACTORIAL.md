# Power of `x` in `N!` — Don't Memorize, Model It

> **Goal:** Find the maximum integer `y` such that `x^y` divides `N!`.

## Table of Contents
1. [Problem Model](#1-problem-model)
2. [Preliminary — Prime Exponents](#2-preliminary--prime-exponents)
3. [Prime Power in N! — Legendre Formula](#3-prime-power-in-n--legendre-formula)
4. [General Case](#4-general-case)
5. [Example — Highest Power of 12 in 10!](#5-example--highest-power-of-12-in-10)
6. [Trailing Zeros](#6-trailing-zeros)
7. [Example — Highest Power of 54 in N!](#7-example--highest-power-of-54-in-n)
8. [C++ Implementation](#8-c-implementation)
9. [Complexity](#9-complexity)
10. [Final Recognition Model](#10-final-recognition-model)

---

# 1. Problem Model

Given `N` and `x`, find the largest `y` such that:

```text
x^y | N!
```

Do **not** calculate `N!`.

```text
factorize x
    ↓
find how many copies of each required prime are inside N!
    ↓
find how many complete copies of x can be built
    ↓
take the minimum
```

---

# 2. Preliminary — Prime Exponents

Every `x > 1` has a prime factorization:

```text
x = p1^a1 × p2^a2 × ... × pk^ak
```

Example:

```text
12 = 2² × 3
```

One copy of `12` requires:

```text
2 copies of prime 2
1 copy  of prime 3
```

For `y` copies:

```text
12^y
= (2² × 3)^y
= 2^(2y) × 3^y
```

So `12^y | N!` requires at least:

```text
2y copies of 2
 y copies of 3
```

---

# 3. Prime Power in N! — Legendre Formula

Let `v_p(N!)` mean the exponent of prime `p` in `N!`.

```text
v_p(N!)
= floor(N/p)
+ floor(N/p²)
+ floor(N/p³)
+ ...
```

Stop when the quotient becomes `0`.

## Why?

Example: count `2`s in `10!`.

```text
10! = 1×2×3×4×5×6×7×8×9×10
```

First count numbers containing at least one `2`:

```text
2, 4, 6, 8, 10

floor(10/2) = 5
```

Numbers containing an **extra** `2` because they are divisible by `2² = 4`:

```text
4, 8

floor(10/4) = 2
```

Numbers containing another extra `2` because they are divisible by `2³ = 8`:

```text
8

floor(10/8) = 1
```

Therefore:

```text
v_2(10!)
= 5 + 2 + 1
= 8
```

Direct check:

```text
2  = 2       → 1 copy
4  = 2²      → 2 copies
6  = 2×3     → 1 copy
8  = 2³      → 3 copies
10 = 2×5     → 1 copy
                -------
                8 copies
```

## C++ — Prime Power in `N!`

```cpp
long long primePowerInFactorial(long long N, long long p) {
    long long cnt = 0;

    while (N > 0) {
        N /= p;
        cnt += N;
    }

    return cnt;
}
```

Dry run for `(N,p) = (10,2)`:

```text
10 / 2 = 5   cnt = 5
 5 / 2 = 2   cnt = 7
 2 / 2 = 1   cnt = 8
 1 / 2 = 0   stop

answer = 8
```

Time: `O(log_p N)`.

---

# 4. General Case

Suppose:

```text
x = p1^a1 × p2^a2 × ... × pk^ak
```

Then:

```text
x^y
= p1^(a1·y) × p2^(a2·y) × ...
```

Suppose `N!` contains `ei = v_pi(N!)` copies of prime `pi`.

For each prime:

```text
ai × y <= ei
```

Divide by `ai`:

```text
y <= floor(ei / ai)
```

All prime requirements must be satisfied, so the **scarcest prime ingredient wins**:

```text
answer
= min over every pi:
  floor(v_pi(N!) / ai)
```

### Don't memorize — model it

```text
x has a prime-factor recipe
          ↓
N! provides the ingredients
          ↓
for each prime:
available / needed-per-x
          ↓
take MIN
```

---

# 5. Example — Highest Power of 12 in 10!

Find the maximum `y` such that:

```text
12^y | 10!
```

### Step 1 — Factorize

```text
12 = 2² × 3
```

### Step 2 — Count available `2`s

```text
v_2(10!)
= floor(10/2) + floor(10/4) + floor(10/8)
= 5 + 2 + 1
= 8
```

One `12` needs `2` copies of `2`:

```text
8 / 2 = 4
```

### Step 3 — Count available `3`s

```text
v_3(10!)
= floor(10/3) + floor(10/9)
= 3 + 1
= 4
```

One `12` needs one `3`:

```text
4 / 1 = 4
```

### Step 4 — Take minimum

```text
from 2 → 4 copies of 12
from 3 → 4 copies of 12

answer = min(4,4)
       = 4
```

Therefore `12^4` divides `10!`, but `12^5` does not.

---

# 6. Trailing Zeros

A trailing zero requires one factor:

```text
10 = 2 × 5
```

Therefore:

```text
zeros = min(v_2(N!), v_5(N!))
```

Factorials contain more `2`s than `5`s, so `5` is the limiting ingredient:

```text
TrailingZeros(N!) = v_5(N!)
```

Example for `100!`:

```text
v_5(100!)
= floor(100/5)
+ floor(100/25)
+ floor(100/125)

= 20 + 4 + 0
= 24
```

So `100!` has `24` trailing zeros.

## C++ — Trailing Zeros

```cpp
long long trailingZeros(long long N) {
    long long ans = 0;

    while (N > 0) {
        N /= 5;
        ans += N;
    }

    return ans;
}
```

---

# 7. Example — Highest Power of 54 in N!

Factorize:

```text
54 = 2 × 3³
```

Therefore:

```text
54^y = 2^y × 3^(3y)
```

Let:

```text
A = v_2(N!)
B = v_3(N!)
```

From prime `2`:

```text
y <= A / 1
y <= A
```

From prime `3`:

```text
3y <= B

y <= floor(B/3)
```

Both must hold:

```text
answer
= min(A, floor(B/3))
```

Visual model:

```text
one 54 needs       N! has        complete 54s
------------------------------------------------
1 copy of 2        A copies      A / 1
3 copies of 3      B copies      B / 3

answer = minimum
```

---

# 8. C++ Implementation

## Factorize `x`

```cpp
vector<pair<long long, int>> factorize(long long x) {
    vector<pair<long long, int>> factors;

    for (long long p = 2; p <= x / p; ++p) {
        if (x % p != 0) continue;

        int exponent = 0;

        while (x % p == 0) {
            x /= p;
            ++exponent;
        }

        factors.push_back({p, exponent});
    }

    if (x > 1)
        factors.push_back({x, 1});

    return factors;
}
```

Example:

```text
54 → (2,1), (3,3)
```

## Highest Power of `x` in `N!`

```cpp
long long highestPower(long long N, long long x) {
    auto factors = factorize(x);

    long long ans = LLONG_MAX;

    for (auto [p, need] : factors) {
        long long have = primePowerInFactorial(N, p);

        long long completeCopies = have / need;

        ans = min(ans, completeCopies);
    }

    return ans;
}
```

Complete idea:

```text
x = 2^a × 3^b × ...

for each (prime, requiredExponent):
    have = exponent of prime in N!
    copies = have / requiredExponent

answer = minimum copies
```

> Assume `x > 1`. For `x = 1`, every power of `1` divides `N!`, so there is no finite maximum `y`.

---

# 9. Complexity

For the given constraints:

```text
N <= 10^18
x <= 10^12
```

Trial-division factorization:

```text
O(sqrt(x))
```

Legendre calculation for each prime `p`:

```text
O(log_p N)
```

Most importantly:

```text
We NEVER construct N!
```

---

# 10. Final Recognition Model

When you see:

```text
largest y such that x^y | N!
```

think:

```text
              x^y | N!
                  |
                  v
             factorize x
                  |
                  v
      x = p1^a1 × p2^a2 × ...
                  |
                  v
    count each prime inside N!
                  |
                  v
       ei = v_pi(N!)
       using Legendre
                  |
                  v
      copies from prime pi
        = floor(ei / ai)
                  |
                  v
           take MINIMUM
```

## One-line mental model

```text
Factorize x
→ Count each prime in N!
→ Divide available by required
→ Take minimum
```

Why minimum?

```text
x needs ALL prime ingredients.

The ingredient that runs out first
limits the number of complete copies of x.
```

## Formula Sheet

```text
v_p(N!)
=
floor(N/p)
+ floor(N/p²)
+ floor(N/p³)
+ ...
```

If:

```text
x = p1^a1 × p2^a2 × ... × pk^ak
```

then:

```text
y =
min(
    floor(v_p1(N!) / a1),
    floor(v_p2(N!) / a2),
    ...
)
```

> **Don't memorize the formula first.** Ask: **How many complete copies of the prime-factor recipe of `x` can be built from the prime factors available inside `N!`?**
