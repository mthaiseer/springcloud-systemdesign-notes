# Sieve Applications — Don't Memorize, Model It

> **Goal:** Use sieve thinking to precompute number-theoretic properties for every number in `[1, N]`.

## Table of Contents
1. [Core Idea](#1-core-idea)
2. [Divisor Count — S0(n)](#2-divisor-count--s0n)
3. [Divisor Sum — S1(n)](#3-divisor-sum--s1n)
4. [Euler Totient — phi(n)](#4-euler-totient--phin)
5. [Totient Sieve](#5-totient-sieve)
6. [Useful Totient Properties](#6-useful-totient-properties)
7. [When to Think Sieve](#7-when-to-think-sieve)
8. [Final Memory Model](#8-final-memory-model)

---

# 1. Core Idea

A sieve is not only for finding primes.

If a property depends on prime factors/divisors and we need it for **many or all numbers `1...N`**, think:

```text
process prime/divisor
        |
        v
update its multiples
        |
        v
precompute property[1...N]
```

---

# 2. Divisor Count — S0(n)

`S0(n)` = number of positive divisors of `n`.

If:

```text
N = p1^a1 × p2^a2 × ... × pk^ak
```

a divisor chooses an exponent independently for every prime:

```text
p1: 0...a1  -> a1+1 choices
p2: 0...a2  -> a2+1 choices
...
```

Therefore:

```text
S0(N) = (a1+1)(a2+1)...(ak+1)
```

## Example — 12

```text
12 = 2² × 3¹

2 exponent: 0,1,2 -> 3 choices
3 exponent: 0,1   -> 2 choices

S0(12) = 3 × 2 = 6
```

Divisors:

```text
1, 2, 3, 4, 6, 12
```

### Recognition example — exactly 3 divisors

We need:

```text
(a1+1)(a2+1)... = 3
```

Since `3` is prime, the only exponent pattern is:

```text
a1 = 2
```

Hence:

```text
N = p², where p is prime
```

---

# 3. Divisor Sum — S1(n)

`S1(n)` = **sum of all positive divisors of `n`**.

> **Don't memorize the formula first.**  
> Model every divisor as a **choice of one exponent from each prime factor**.

## Step 1 — Start with one prime power

Suppose:

```text
N = p^a
```

A divisor can contain `p` with exponent:

```text
0, 1, 2, ..., a
```

So the possible divisors are:

```text
p^0, p^1, p^2, ..., p^a
=
1, p, p², ..., p^a
```

Therefore:

```text
S1(p^a) = 1 + p + p² + ... + p^a
```

This is a geometric series:

```text
1 + p + p² + ... + p^a
= (p^(a+1) - 1) / (p - 1)
```

So:

```text
S1(p^a) = (p^(a+1) - 1) / (p - 1)
```

## Step 2 — What changes when there are multiple primes?

Take:

```text
12 = 2² × 3¹
```

A divisor of `12` chooses:

```text
power of 2:  2⁰, 2¹, 2²  ->  1, 2, 4
power of 3:  3⁰, 3¹      ->  1, 3
```

Every choice from the first row combines with every choice from the second.

```text
                     Power of 3
                  3⁰ = 1     3¹ = 3
                +----------+----------+
2⁰ = 1          |    1     |    3     |
2¹ = 2          |    2     |    6     |
2² = 4          |    4     |   12     |
                +----------+----------+

Each cell = one divisor of 12
```

Therefore all divisors are:

```text
1, 3, 2, 6, 4, 12
```

## Step 3 — Why do we multiply the prime sums?

The sum of all cells is:

```text
1 + 3 + 2 + 6 + 4 + 12
```

But distributive multiplication gives exactly the same terms:

```text
(1 + 2 + 4)(1 + 3)

= 1(1+3) + 2(1+3) + 4(1+3)

= 1 + 3 + 2 + 6 + 4 + 12

= 28
```

So:

```text
S1(12)
= (1 + 2 + 4)(1 + 3)
= 7 × 4
= 28
```

Check directly:

```text
divisors = 1, 2, 3, 4, 6, 12

1 + 2 + 3 + 4 + 6 + 12 = 28
```

### Key observation

```text
Prime factorization
        |
        v
choose one power of each prime
        |
        v
each combination creates one divisor
        |
        v
sum all combinations
        |
        v
multiply the power-sums
```

## Step 4 — Generalize

If:

```text
N = p1^a1 × p2^a2 × ... × pk^ak
```

then each prime contributes:

```text
p1: 1 + p1 + p1² + ... + p1^a1
p2: 1 + p2 + p2² + ... + p2^a2
...
pk: 1 + pk + pk² + ... + pk^ak
```

Every divisor is obtained by choosing **one term from every bracket**.

Therefore:

```text
S1(N)
= (1+p1+...+p1^a1)
  × (1+p2+...+p2^a2)
  × ...
  × (1+pk+...+pk^ak)
```

Now compress each geometric series:

```text
1 + p + p² + ... + p^a
= (p^(a+1)-1)/(p-1)
```

Hence:

```text
S1(N) = Π [(p_i^(a_i+1)-1)/(p_i-1)]
```

## Quick combined example — 72

```text
72 = 2³ × 3²
```

Prime-power contributions:

```text
2³ -> 1+2+4+8 = 15
3² -> 1+3+9   = 13
```

Therefore:

```text
S1(72) = 15 × 13 = 195
```


## C++ — Divisor Count and Divisor Sum from Prime Factorization

For one number `n`, factorize it and build both values at the same time:

```cpp
#include <bits/stdc++.h>
using namespace std;

pair<long long, long long> divisorCountAndSum(long long n) {
    long long divisorCount = 1; // S0(n)
    long long divisorSum = 1;   // S1(n)

    for (long long p = 2; p * p <= n; ++p) {
        if (n % p != 0) continue;

        int exponent = 0;
        long long power = 1;
        long long powerSum = 1; // 1 + p + p^2 + ...

        while (n % p == 0) {
            n /= p;
            ++exponent;

            power *= p;
            powerSum += power;
        }

        // p^exponent contributes:
        // S0 -> exponent + 1 choices
        // S1 -> 1 + p + ... + p^exponent
        divisorCount *= (exponent + 1);
        divisorSum *= powerSum;
    }

    // One prime factor may remain.
    // Remaining n = p^1.
    if (n > 1) {
        divisorCount *= 2;      // exponents: 0 or 1
        divisorSum *= (1 + n);  // 1 + p
    }

    return {divisorCount, divisorSum};
}

int main() {
    long long n;
    cin >> n;

    auto [s0, s1] = divisorCountAndSum(n);

    cout << "S0 = " << s0 << '\n';
    cout << "S1 = " << s1 << '\n';
}
```

### Dry run — `n = 12`

```text
12 = 2² × 3¹

Start:
S0 = 1
S1 = 1

p = 2:
exponent = 2
powerSum = 1 + 2 + 4 = 7

S0 = 1 × (2+1) = 3
S1 = 1 × 7     = 7

remaining n = 3:
S0 = 3 × 2     = 6
S1 = 7 × (1+3) = 28

Answer:
S0(12) = 6
S1(12) = 28
```

Complexity:

```text
Time   : O(sqrt(N))
Memory : O(1)
```

> **Memory model:** `S0` counts exponent choices; `S1` sums the values produced by those exponent choices.

---

# 4. Euler Totient — phi(n)

`phi(n)` counts numbers in `1...n` that are coprime with `n`.

```text
gcd(a,n) = 1
```

## Example — 10

```text
1 2 3 4 5 6 7 8 9 10
```

Coprime with `10`:

```text
1, 3, 7, 9
```

Therefore:

```text
phi(10) = 4
```

If:

```text
n = p1^a1 × p2^a2 × ... × pk^ak
```

then:

```text
phi(n)
= n × (1-1/p1)
    × (1-1/p2)
    × ...
    × (1-1/pk)
```

Each **distinct prime factor** is used once.

## Example — 12

```text
12 = 2² × 3
```

Distinct primes:

```text
2, 3
```

Therefore:

```text
phi(12)
= 12 × (1 - 1/2) × (1 - 1/3)
```

### Step 1 — Simplify each prime contribution

For prime `2`:

```text
1 - 1/2
= 2/2 - 1/2
= 1/2
```

Meaning: multiples of `2` occupy `1/2` of the numbers, so we keep the other `1/2`.

For prime `3`:

```text
1 - 1/3
= 3/3 - 1/3
= 2/3
```

Meaning: multiples of `3` occupy `1/3`, so we keep the other `2/3`.

Now:

```text
phi(12)
= 12 × 1/2 × 2/3
```

### Step 2 — Calculate left to right

```text
12 × 1/2 = 6

6 × 2/3
= (6 / 3) × 2
= 2 × 2
= 4
```

So:

```text
phi(12) = 4
```

### Step 3 — See what the math is doing

Start with all numbers `1...12`:

```text
1  2  3  4  5  6  7  8  9  10  11  12
```

`12 = 2² × 3`, so the distinct prime factors are `2` and `3`.

First remove numbers sharing factor `2`:

```text
12 × (1 - 1/2)
= 12 × 1/2
= 6 numbers remain

remain: 1, 3, 5, 7, 9, 11
```

Then remove the fraction sharing factor `3`:

```text
6 × (1 - 1/3)
= 6 × 2/3
= 4 numbers remain

remain: 1, 5, 7, 11
```

ASCII model:

```text
12 numbers
    |
    | prime factor 2: keep (1 - 1/2) = 1/2
    v
 6 numbers
    |
    | prime factor 3: keep (1 - 1/3) = 2/3
    v
 4 numbers
```

Therefore the numbers coprime with `12` are:


```text
1, 5, 7, 11
```

---

# 5. Totient Sieve

Suppose we need:

```text
phi(1), phi(2), ..., phi(N)
```

Instead of factorizing every number separately, let every prime update its multiples.

## Step 1 — Initialize

```cpp
phi[i] = i;
```

## Step 2 — Process each prime `p`

Every multiple of `p` needs:

```text
× (1 - 1/p)
```

Avoid floating point:

```cpp
phi[x] -= phi[x] / p;
```

Visual:

```text
p = 2
 |
 +--> 2
 +--> 4
 +--> 6
 +--> 8
 +--> ...

p = 3
 |
 +--> 3
 +--> 6
 +--> 9
 +--> 12
 +--> ...
```

## Dry run — phi(12)

Start:

```text
phi[12] = 12
```

Prime `2`:

```text
12 - 12/2 = 6
```

Prime `3`:

```text
6 - 6/3 = 4
```

So:

```text
phi(12) = 4
```

## C++ Implementation

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> buildPhi(int n) {
    vector<int> phi(n + 1);

    for (int i = 0; i <= n; ++i)
        phi[i] = i;

    for (int p = 2; p <= n; ++p) {

        // Unchanged => p is prime
        if (phi[p] == p) {

            for (int x = p; x <= n; x += p) {
                phi[x] -= phi[x] / p;
            }
        }
    }

    return phi;
}
```

Complexity:

```text
Time   : O(N log log N)
Memory : O(N)
```

---

# 6. Useful Totient Properties

### Prime `p`

```text
phi(p) = p-1
```

because every number `1...p-1` is coprime with prime `p`.

### Prime power

```text
phi(p^k)
= p^k - p^(k-1)
= p^k(1-1/p)
```

### Multiplicative property

If:

```text
gcd(a,b) = 1
```

then:

```text
phi(a×b) = phi(a) × phi(b)
```

Example:

```text
phi(4) = 2
phi(3) = 2

gcd(4,3) = 1

phi(12) = 2 × 2 = 4
```

---

# 7. When to Think Sieve

```text
Need property of ONE number?
        |
        v
factorize that number
```

But:

```text
Need property for MANY numbers 1...N?
        |
        v
Does it depend on primes/divisors?
        |
       YES
        |
        v
SIEVE / PRECOMPUTATION
```

| Problem asks for | Think |
|---|---|
| All primes `<= N` | Normal sieve |
| Many factorizations | SPF sieve |
| Number of divisors | Prime exponents / divisor sieve |
| Sum of divisors | Prime powers / divisor sieve |
| `phi(1...N)` | Totient sieve |
| Prime-factor property for all `1...N` | Prime -> multiples |

---

# 8. Final Memory Model

Start from prime factorization:

```text
             N = p1^a1 × p2^a2 × ...
                         |
             +-----------+-----------+
             |           |           |
             v           v           v
           S0(N)       S1(N)       phi(N)
             |           |           |
             v           v           v
        exponent      divisor      remove
         choices       sums       prime-factor
                                   multiples
```

### Divisor count

```text
p^a -> exponent choices 0...a
     -> a+1 choices

S0(N) = Π(ai+1)
```

### Divisor sum

```text
p^a -> 1+p+p²+...+p^a

S1(N) = Π (p^(a+1)-1)/(p-1)
```

### Totient

```text
For each distinct prime p dividing N:

multiply by (1-1/p)

phi(N) = N × Π(1-1/p)
```

### Sieve connection

```text
Need these properties for many numbers?
              |
              v
Don't repeatedly factor everything
              |
              v
process prime/divisor once
              |
              v
update all its multiples
```

> **Contest habit:** First model the property using prime factorization. If the same property is required across a whole range, ask: **Can each prime push its contribution to all of its multiples?**
