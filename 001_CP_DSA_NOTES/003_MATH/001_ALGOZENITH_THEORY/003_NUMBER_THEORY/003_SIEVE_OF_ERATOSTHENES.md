# Sieve of Eratosthenes — Don't Memorize, Model It

> **Goal:** Find **all prime numbers from `1...N`** efficiently.  
> Don't think: *“check every number separately.”*  
> Think: **“start with all candidates, then eliminate composites.”**

---

## Table of Contents

1. [When Do We Need a Sieve?](#1-when-do-we-need-a-sieve)
2. [Core Model](#2-core-model)
3. [Step-by-Step Example](#3-step-by-step-example)
4. [Why Strike Multiples?](#4-why-strike-multiples)
5. [Why Start from i²?](#5-why-start-from-i²)
6. [Why Only Process i up to √N?](#6-why-only-process-i-up-to-n)
7. [C++ Implementation](#7-c-implementation)
8. [Dry Run of the Code](#8-dry-run-of-the-code)
9. [Complexity](#9-complexity)
10. [Contest Recognition](#10-contest-recognition)
11. [Final Memory Model](#11-final-memory-model)

---

# 1. When Do We Need a Sieve?

Suppose we need:

```text
all primes ≤ N
```

One approach is:

```text
2 → check prime
3 → check prime
4 → check prime
...
N → check prime
```

If each prime check costs roughly `O(√N)`, doing it for many numbers becomes expensive.

Instead:

```text
mark all numbers as possible primes
              ↓
use known primes to eliminate their multiples
              ↓
whatever survives is prime
```

That is the **Sieve of Eratosthenes**.

---

# 2. Core Model

A composite number has a prime factor.

Example:

```text
12 = 2 × 6
15 = 3 × 5
20 = 2 × 10
21 = 3 × 7
```

So if `2` is prime:

```text
4, 6, 8, 10, 12, ...
```

cannot be prime.

If `3` is prime:

```text
6, 9, 12, 15, 18, ...
```

cannot be prime.

Therefore:

```text
prime p
   │
   └── all multiples of p except p itself are composite
```

The sieve repeatedly applies this observation.

---

# 3. Step-by-Step Example

Find all primes up to:

```text
N = 20
```

Initially:

```text
2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20
✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓   ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓
```

`0` and `1` are not prime.

## Step 1 — `i = 2`

`2` is still marked prime.

Strike its composite multiples:

```text
4, 6, 8, 10, 12, 14, 16, 18, 20
```

State:

```text
2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20
✓  ✓  ×  ✓  ×  ✓  ×  ✓   ×  ✓  ×  ✓  ×  ✓  ×  ✓  ×  ✓  ×
```

---

## Step 2 — `i = 3`

`3` is still marked prime.

Strike:

```text
9, 12, 15, 18
```

Some values such as `12` and `18` were already removed. That is fine.

```text
2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20
✓  ✓  ×  ✓  ×  ✓  ×  ×   ×  ✓  ×  ✓  ×  ×  ×  ✓  ×  ✓  ×
```

---

## Result

Numbers still marked:

```text
2, 3, 5, 7, 11, 13, 17, 19
```

These are exactly the primes `≤ 20`.

---

# 4. Why Strike Multiples?

Suppose `p` is prime.

Any number:

```text
p × k       where k ≥ 2
```

has at least two factors:

```text
p and k
```

Therefore it is composite.

Example:

```text
p = 5

5 × 2 = 10
5 × 3 = 15
5 × 4 = 20
5 × 5 = 25
...
```

So the model is simply:

```text
discover prime p
       ↓
multiples of p cannot be prime
       ↓
mark them false
```

---

# 5. Why Start from i²?

A basic implementation could start crossing multiples from:

```text
2 × i
```

But many of those values have already been handled by smaller factors.

Example for:

```text
i = 5
```

Multiples before `5²`:

```text
5 × 2 = 10   ← already removed by 2
5 × 3 = 15   ← already removed by 3
5 × 4 = 20   ← already removed by 2
5 × 5 = 25   ← first useful starting point
```

Visual:

```text
10       15       20       25       30 ...
│        │        │        │
×2       ×3       ×2       first new work
                           ↑
                          5²
```

General model:

```text
i × k, where k < i
```

already has the smaller factor `k`.

Therefore we can start at:

```text
i × i
```

This avoids unnecessary work.

---

# 6. Why Only Process i up to √N?

From factorisation:

```text
composite = a × b
```

At least one factor must satisfy:

```text
factor ≤ √N
```

Therefore every composite number `≤ N` has already been reached by a factor `≤ √N`.

Example:

```text
N = 50
√50 ≈ 7.07
```

We only need prime bases:

```text
2, 3, 5, 7
```

Once:

```text
i² > N
```

there are no new multiples starting from `i²` within the range.

So:

```text
process marking while i² ≤ N
```

---

# 7. C++ Implementation

## Clean Optimized Sieve

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> sieve(int n) {
    vector<bool> isPrime(n + 1, true);

    if (n >= 0) isPrime[0] = false;
    if (n >= 1) isPrime[1] = false;

    for (long long i = 2; i * i <= n; ++i) {

        // If i survived, it is prime.
        if (isPrime[i]) {

            // Smaller multiples were already handled.
            for (long long j = i * i; j <= n; j += i) {
                isPrime[j] = false;
            }
        }
    }

    vector<int> primes;

    for (int i = 2; i <= n; ++i) {
        if (isPrime[i])
            primes.push_back(i);
    }

    return primes;
}

int main() {
    int n;
    cin >> n;

    vector<int> primes = sieve(n);

    for (int p : primes)
        cout << p << ' ';
}
```

### Why `long long` for `i*i`?

For larger integer limits:

```text
i × i
```

can overflow an `int`.

Using:

```cpp
long long i
```

keeps the multiplication safe for normal sieve constraints.

---

# 8. Dry Run of the Code

Take:

```text
N = 20
```

Initial:

```text
isPrime[2...20] = true
```

### `i = 2`

```text
2² = 4
```

Loop:

```text
j = 4, 6, 8, 10, 12, 14, 16, 18, 20
```

Mark all false.

### `i = 3`

```text
3² = 9
```

Loop:

```text
j = 9, 12, 15, 18
```

Mark false.

### Next

```text
4² = 16
```

but:

```text
isPrime[4] = false
```

so skip it.

Then the outer condition eventually fails because:

```text
i² > 20
```

Survivors:

```text
2 3 5 7 11 13 17 19
```

---

# 9. Complexity

For every prime `p`, approximately:

```text
N/p
```

multiples are processed.

Conceptually:

```text
N/2 + N/3 + N/5 + N/7 + ...
```

over primes.

The prime harmonic sum grows like `log log N`, giving:

```text
Time = O(N log log N)
```

Memory:

```text
O(N)
```

Typical use:

```text
N around 10^6 – 10^7
```

is common in competitive programming, subject to the problem's memory/time limits and representation used.

---

# 10. Contest Recognition

| Problem clue | Think |
|---|---|
| Find every prime up to `N` | Sieve |
| Many primality queries within a bounded range | Precompute sieve |
| Count primes up to `N` | Sieve + count/prefix |
| Need prime factors for many numbers | SPF sieve |
| Only one large number | `O(√N)` prime check may be simpler |

The important distinction:

```text
ONE / FEW numbers
      ↓
trial division O(√N)

ALL / MANY numbers up to N
      ↓
Sieve O(N log log N)
```

---

# 11. Final Memory Model

Don't memorize the loop first.

Start from:

```text
Composite numbers contain prime factors.
```

Then:

```text
          assume candidates 2...N
                    │
                    ↓
              first survivor
                 is prime
                    │
                    ↓
        eliminate its multiples
                    │
             start from i²
                    │
                    ↓
             next survivor
                    │
                    ↓
                  repeat
```

And connect it to the previous factorisation model:

```text
          N = a × b
              │
              ↓
 one factor of a composite
       must be ≤ √N
              │
              ↓
     only small primes are
     needed to eliminate all
        composites ≤ N
              │
              ↓
      SIEVE OF ERATOSTHENES
```

> **Contest habit:** If the problem asks about primes for **many values in a bounded range**, don't repeatedly ask *“is this number prime?”*. Reverse the thinking: **precompute all primes once by eliminating composites.**
