# Application Idea 2 — Harmonic Lemma-Based Summation Optimisation

> **Problem:** Efficiently compute sums containing repeated values of
>
> $$
> \left\lfloor \frac{N}{i} \right\rfloor
> $$
>
> such as
>
> $$
> \sum_{i=1}^{N}\left\lfloor\frac{N}{i}\right\rfloor^k
> $$
>
> when \(N\) is too large for an \(O(N)\) loop.

> **Don't memorize `last = N / (N / i)`.** Understand what it means: find the entire interval where integer division gives the **same quotient**, and process that interval at once.

---

## Table of Contents

1. [Problem Model](#1-problem-model)
2. [Preliminary — Floor and Integer Division](#2-preliminary--floor-and-integer-division)
3. [First Observation — Quotients Repeat](#3-first-observation--quotients-repeat)
4. [Full Dry Run — N = 10](#4-full-dry-run--n--10)
5. [How to Find the Last Index](#5-how-to-find-the-last-index)
6. [Derivation of last = N / q](#6-derivation-of-last--n--q)
7. [Range Contribution](#7-range-contribution)
8. [Complete Summation Dry Run](#8-complete-summation-dry-run)
9. [Why Only O(sqrt(N)) Groups?](#9-why-only-osqrtn-groups)
10. [C++ — Generic k](#10-c--generic-k)
11. [C++ — Cube Example](#11-c--cube-example)
12. [Overflow and Modulo](#12-overflow-and-modulo)
13. [Recognition Model](#13-recognition-model)

---

# 1. Problem Model

Suppose we need:

$$ \boxed{S=\sum_{i=1}^{N}\left\lfloor\frac{N}{i}\right\rfloor^k} $$

A direct loop calculates:

```text
i = 1, 2, 3, ..., N
```

so the complexity is:

$$ O(N) $$

For:

```text
N = 10^12
```

that is far too many iterations.

The important observation is that:

$$ \left\lfloor\frac{N}{i}\right\rfloor $$

does **not** change at every `i`.

Many consecutive values of `i` produce the same quotient.

So instead of:

```text
process one i
process next i
process next i
...
```

we want:

```text
find a whole equal-quotient range
            ↓
process the entire range once
            ↓
jump to the next range
```

---

# 2. Preliminary — Floor and Integer Division

The floor function:

$$ \left\lfloor x\right\rfloor $$

means:

```text
largest integer <= x
```

Examples:

$$ \left\lfloor 3.9\right\rfloor=3 $$

$$ \left\lfloor 2.1\right\rfloor=2 $$

$$ \left\lfloor 5\right\rfloor=5 $$

For positive integers in C++:

```cpp
N / i
```

already performs integer division.

Example:

```text
10 / 3 = 3
```

because mathematically:

$$ \frac{10}{3}=3.333\ldots $$

and:

$$ \left\lfloor\frac{10}{3}\right\rfloor=3 $$

Therefore:

```cpp
long long q = N / i;
```

represents:

$$ q=\left\lfloor\frac{N}{i}\right\rfloor $$

---

# 3. First Observation — Quotients Repeat

Take:

```text
N = 10
```

Calculate:

$$ \left\lfloor\frac{10}{i}\right\rfloor $$

for every `i`.

| `i` | `10 / i` | Floor value |
|---:|---:|---:|
| 1 | 10 | 10 |
| 2 | 5 | 5 |
| 3 | 3.33... | 3 |
| 4 | 2.5 | 2 |
| 5 | 2 | 2 |
| 6 | 1.66... | 1 |
| 7 | 1.42... | 1 |
| 8 | 1.25 | 1 |
| 9 | 1.11... | 1 |
| 10 | 1 | 1 |

Notice:

```text
i:       1   2   3   4   5   6   7   8   9   10
N / i:  10   5   3   2   2   1   1   1   1    1
                         └───┘   └──────────────┘
```

We have equal-value ranges:

```text
[1,1]   -> 10
[2,2]   -> 5
[3,3]   -> 3
[4,5]   -> 2
[6,10]  -> 1
```

Instead of 10 iterations, we can process only 5 groups.

This is the central idea.

---

# 4. Full Dry Run — `N = 10`

Suppose current index is:

```text
i = 4
```

Then:

$$ q=\left\lfloor\frac{10}{4}\right\rfloor=2 $$

Now ask:

> How far can we move while the quotient remains `2`?

Check manually:

```text
10 / 4 = 2
10 / 5 = 2
10 / 6 = 1
```

Therefore the equal-quotient range is:

```text
[4,5]
```

So:

```text
i    = 4
q    = 2
last = 5
```

We want to calculate `last` directly without checking `5`, `6`, `7`, ... one by one.

That is where the harmonic grouping formula comes from.

---

# 5. How to Find the Last Index

At index `i`, calculate:

$$ q=\left\lfloor\frac{N}{i}\right\rfloor $$

We need the largest index `x` satisfying:

$$ \left\lfloor\frac{N}{x}\right\rfloor=q $$

The answer is:

$$ \boxed{\text{last}=\left\lfloor\frac{N}{q}\right\rfloor} $$

Since:

$$ q=\left\lfloor\frac{N}{i}\right\rfloor $$

we can also write:

$$ \boxed{\text{last}=\left\lfloor\frac{N}{\left\lfloor N/i\right\rfloor}\right\rfloor} $$

In C++:

```cpp
long long q = N / i;
long long last = N / q;
```

or compactly:

```cpp
long long last = N / (N / i);
```

The two-line version is usually easier to understand.

---

# 6. Derivation of `last = N / q`

This is the most important part to understand.

We know:

$$ \left\lfloor\frac{N}{x}\right\rfloor=q $$

What does that mean?

By the definition of floor:

$$ q\le\frac{N}{x}<q+1 $$

Focus first on:

$$ q\le\frac{N}{x} $$

Multiply by positive `x`:

$$ qx\le N $$

Divide by positive `q`:

$$ x\le\frac{N}{q} $$

Since `x` must be an integer:

$$ x\le\left\lfloor\frac{N}{q}\right\rfloor $$

Therefore the **largest possible `x`** is:

$$ \boxed{\text{last}=\left\lfloor\frac{N}{q}\right\rfloor} $$

### Example

For:

```text
N = 10
i = 4
```

First:

$$ q=\left\lfloor\frac{10}{4}\right\rfloor=2 $$

Then:

$$ \text{last}=\left\lfloor\frac{10}{2}\right\rfloor=5 $$

Therefore:

```text
[4,5]
```

has quotient `2`.

Check:

$$ \left\lfloor\frac{10}{4}\right\rfloor=2 $$

$$ \left\lfloor\frac{10}{5}\right\rfloor=2 $$

Next:

$$ \left\lfloor\frac{10}{6}\right\rfloor=1 $$

So `5` is exactly the last index.

---

# 7. Range Contribution

Suppose:

```text
current index = i
last index    = last
quotient      = q
```

Every index in:

```text
[i, last]
```

has the same value `q`.

Number of indices in this range:

$$ \boxed{\text{count}=\text{last}-i+1} $$

Each contributes:

$$ q^k $$

Therefore total contribution of this entire range is:

$$ \boxed{(\text{last}-i+1)\times q^k} $$

Then jump directly to:

```cpp
i = last + 1;
```

---

# 8. Complete Summation Dry Run

Evaluate:

$$ S=\sum_{i=1}^{10}\left\lfloor\frac{10}{i}\right\rfloor^3 $$

We process groups.

## Group 1

```text
i = 1
```

$$ q=10/1=10 $$

$$ \text{last}=10/10=1 $$

Count:

$$ 1-1+1=1 $$

Contribution:

$$ 1\times10^3=1000 $$

Jump:

```text
i = 2
```

---

## Group 2

```text
i = 2
```

$$ q=10/2=5 $$

$$ \text{last}=10/5=2 $$

Contribution:

$$ 1\times5^3=125 $$

Running sum:

$$ 1000+125=1125 $$

---

## Group 3

```text
i = 3
```

$$ q=10/3=3 $$

$$ \text{last}=10/3=3 $$

Contribution:

$$ 1\times3^3=27 $$

Running sum:

$$ 1125+27=1152 $$

---

## Group 4

```text
i = 4
```

$$ q=10/4=2 $$

$$ \text{last}=10/2=5 $$

Range:

```text
[4,5]
```

Count:

$$ 5-4+1=2 $$

Contribution:

$$ 2\times2^3=16 $$

Running sum:

$$ 1152+16=1168 $$

---

## Group 5

```text
i = 6
```

$$ q=10/6=1 $$

$$ \text{last}=10/1=10 $$

Range:

```text
[6,10]
```

Count:

$$ 10-6+1=5 $$

Contribution:

$$ 5\times1^3=5 $$

Final:

$$ \boxed{S=1173} $$

### Entire grouping

| Range | Quotient `q` | Count | Contribution for `k=3` |
|---|---:|---:|---:|
| `[1,1]` | 10 | 1 | \(1\times10^3=1000\) |
| `[2,2]` | 5 | 1 | \(1\times5^3=125\) |
| `[3,3]` | 3 | 1 | \(1\times3^3=27\) |
| `[4,5]` | 2 | 2 | \(2\times2^3=16\) |
| `[6,10]` | 1 | 5 | \(5\times1^3=5\) |

Thus:

$$ 1000+125+27+16+5=1173 $$

---

# 9. Why Only `O(sqrt(N))` Groups?

The harmonic lemma says the number of distinct values of:

$$ \left\lfloor\frac{N}{i}\right\rfloor $$

is:

$$ O(\sqrt N) $$

A useful way to see it is to split indices around \(\sqrt N\).

## Part 1 — Small `i`

For:

$$ i\le\sqrt N $$

there are at most:

$$ \sqrt N $$

indices.

So this side contributes at most \(O(\sqrt N)\) groups.

## Part 2 — Large `i`

For:

$$ i>\sqrt N $$

we have:

$$ \frac{N}{i}<\sqrt N $$

Therefore:

$$ \left\lfloor\frac{N}{i}\right\rfloor<\sqrt N $$

So there are at most another \(O(\sqrt N)\) possible quotient values.

Hence the total number of groups is bounded by approximately:

$$ 2\sqrt N $$

Therefore:

$$ \boxed{O(\sqrt N)} $$

rather than:

$$ O(N) $$

---

# 10. C++ — Generic `k`

Avoid using floating-point `pow()` for integer powers.

Use integer exponentiation.

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;
using i128 = __int128_t;

i128 intPow(i128 base, long long exp) {
    i128 ans = 1;

    while (exp > 0) {
        if (exp & 1)
            ans *= base;

        base *= base;
        exp >>= 1;
    }

    return ans;
}

i128 harmonicSum(long long N, long long k) {
    i128 ans = 0;

    for (long long i = 1; i <= N; ) {

        long long q = N / i;

        // Last index having the same quotient q.
        long long last = N / q;

        long long count = last - i + 1;

        ans += (i128)count * intPow(q, k);

        // Jump directly to the next group.
        i = last + 1;
    }

    return ans;
}
```

Core logic:

```cpp
q = N / i;
last = N / q;

ans += (last - i + 1) * q^k;

i = last + 1;
```

---

# 11. C++ — Cube Example

If the problem specifically asks:

$$ \sum_{i=1}^{N}\left\lfloor\frac{N}{i}\right\rfloor^3 $$

we do not need generic exponentiation.

```cpp
#include <bits/stdc++.h>
using namespace std;

using i128 = __int128_t;

i128 cubeHarmonicSum(long long N) {
    i128 ans = 0;

    for (long long i = 1; i <= N; ) {

        long long q = N / i;
        long long last = N / q;

        long long count = last - i + 1;

        i128 cube = (i128)q * q * q;

        ans += (i128)count * cube;

        i = last + 1;
    }

    return ans;
}
```

The grouping itself is:

$$ O(\sqrt N) $$

---

# 12. Overflow and Modulo

For large `N`, values such as:

$$ q^3 $$

can easily overflow `long long`.

For example, if:

```text
N = 10^12
q = 10^12
```

then:

$$ q^3=10^{36} $$

which does not fit in signed 64-bit integer storage.

So the exact implementation depends on the problem.

## If the answer is required modulo `MOD`

Use modular multiplication/exponentiation:

```cpp
long long modPow(long long base, long long exp, long long MOD) {
    long long ans = 1 % MOD;
    base %= MOD;

    while (exp > 0) {
        if (exp & 1)
            ans = (__int128)ans * base % MOD;

        base = (__int128)base * base % MOD;
        exp >>= 1;
    }

    return ans;
}
```

Then each range contributes:

```cpp
long long contribution =
    (__int128)(count % MOD) * modPow(q, k, MOD) % MOD;
```

> Never use `pow(div, k)` from `<cmath>` for exact integer number-theory calculations. It uses floating-point arithmetic.

---

# 13. Recognition Model

When you see:

$$ \sum_{i=1}^{N}F\left(\left\lfloor\frac{N}{i}\right\rfloor\right) $$

think:

```text
N is huge
   |
   v
O(N) loop too slow
   |
   v
Does floor(N / i) repeat?
   |
   v
YES
   |
   v
q = N / i
   |
   v
last = N / q
   |
   v
all i...last have same q
   |
   v
process whole range
   |
   v
i = last + 1
```

## Core Range Formula

At current `i`:

$$ q=\left\lfloor\frac{N}{i}\right\rfloor $$

The last index with the same quotient is:

$$ \boxed{\text{last}=\left\lfloor\frac{N}{q}\right\rfloor} $$

Range length:

$$ \boxed{\text{count}=\text{last}-i+1} $$

For the original sum, range contribution:

$$ \boxed{\text{count}\times q^k} $$

Then:

```cpp
i = last + 1;
```

## One-Line Mental Model

```text
floor(N/i) repeats
      ↓
group equal quotients
      ↓
q = N/i
      ↓
last = N/q
      ↓
process [i,last] once
      ↓
O(sqrt(N))
```

> **Don't memorize**
>
> ```cpp
> last = N / (N / i);
> ```
>
> as a magic formula.
>
> First name the quotient:
>
> ```cpp
> q = N / i;
> ```
>
> Then ask:
>
> **“What is the largest index whose quotient is still `q`?”**
>
> From \(qx\le N\), the answer is:
>
> $$ x\le\left\lfloor\frac{N}{q}\right\rfloor $$
>
> so:
>
> ```cpp
> last = N / q;
> ```
