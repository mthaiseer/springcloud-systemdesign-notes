# Fast Factorization — Don't Memorize, Model It

> **Goal:** Answer many prime-factorization queries quickly when `X` is bounded, typically `X <= 10^6`.

## Table of Contents
1. [Why Fast Factorization?](#1-why-fast-factorization)
2. [Core Model](#2-core-model)
3. [Why O(log X) Per Query?](#3-why-olog-x-per-query)
4. [Smallest Prime Factor (SPF)](#4-smallest-prime-factor-spf)
5. [Build SPF](#5-build-spf)
6. [SPF Dry Run](#6-spf-dry-run)
7. [Factorization Using SPF](#7-factorization-using-spf)
8. [Complete Dry Run — 60](#8-complete-dry-run--60)
9. [C++ Implementation](#9-c-implementation)
10. [Multiple Queries](#10-multiple-queries)
11. [Complexity](#11-complexity)
12. [Contest Recognition](#12-contest-recognition)
13. [Final Memory Model](#13-final-memory-model)

---

# 1. Why Fast Factorization?

Suppose:

```text
N = 10^6
Q = 10^5 queries
```

Every query asks for the prime factors of `X`.

Example:

```text
60 = 2 x 2 x 3 x 5
```

Factoring every query independently repeats the same divisor-search work.

Better model:

```text
precompute once
      |
      v
smallest prime factor for every x
      |
      v
answer each query by repeated division
```

This is the classic **precompute once, query many times** pattern.

---

# 2. Core Model

Store:

```text
spf[x] = smallest prime factor of x
```

Examples:

```text
spf[4]  = 2
spf[6]  = 2
spf[9]  = 3
spf[15] = 3
spf[25] = 5
```

For a prime:

```text
spf[p] = p
```

Now factorization becomes a chain.

For `60`:

```text
60 --spf=2--> 30
30 --spf=2--> 15
15 --spf=3-->  5
 5 --spf=5-->  1
```

Collected factors:

```text
2, 2, 3, 5
```

So:

```text
60 = 2^2 x 3 x 5
```

---

# 3. Why O(log X) Per Query?

The smallest possible prime factor is `2`.

The maximum number of divisions happens for powers of two:

```text
64 = 2^6

64 -> 32 -> 16 -> 8 -> 4 -> 2 -> 1
```

If:

```text
X = 2^k
```

then:

```text
k = log2(X)
```

So the number of extracted prime factors, counting repetitions, is at most:

```text
O(log X)
```

---

# 4. Smallest Prime Factor (SPF)

Example table:

| `x` | Factorization | `spf[x]` |
|---:|---|---:|
| 2 | `2` | 2 |
| 3 | `3` | 3 |
| 4 | `2 x 2` | 2 |
| 6 | `2 x 3` | 2 |
| 9 | `3 x 3` | 3 |
| 10 | `2 x 5` | 2 |
| 15 | `3 x 5` | 3 |
| 25 | `5 x 5` | 5 |

The important idea:

```text
spf[x]
   |
   v
gives one prime factor immediately
```

No divisor search is required during a query.

---

# 5. Build SPF

## Step 1 — Assume every number is prime

```cpp
for (int i = 2; i <= N; i++) {
    spf[i] = i;
}
```

For `N = 15`:

```text
x   : 2  3  4  5  6  7  8  9 10 11 12 13 14 15
spf : 2  3  4  5  6  7  8  9 10 11 12 13 14 15
```

## Step 2 — Process primes in increasing order

If:

```text
spf[i] == i
```

then `i` has not been changed by any smaller prime, so `i` is prime.

Visit its multiples:

```cpp
for (int j = 2 * i; j <= N; j += i) {
    if (spf[j] == j) {
        spf[j] = i;
    }
}
```

Why does this store the **smallest** factor?

```text
primes are processed:

2 -> 3 -> 5 -> 7 -> ...
```

The first prime that reaches a composite is its smallest prime factor.

Later primes do not overwrite it.

---

# 6. SPF Dry Run

Build SPF up to `15`.

Initial:

```text
x   : 2  3  4  5  6  7  8  9 10 11 12 13 14 15
spf : 2  3  4  5  6  7  8  9 10 11 12 13 14 15
```

## Process `2`

Multiples:

```text
4, 6, 8, 10, 12, 14
```

After marking:

```text
x   : 2  3  4  5  6  7  8  9 10 11 12 13 14 15
spf : 2  3  2  5  2  7  2  9  2 11  2 13  2 15
```

## Process `3`

Multiples:

```text
6, 9, 12, 15
```

But `6` and `12` already have SPF `2`, so do not overwrite them.

Set:

```text
spf[9]  = 3
spf[15] = 3
```

Final relevant table:

```text
x   : 2  3  4  5  6  7  8  9 10 11 12 13 14 15
spf : 2  3  2  5  2  7  2  3  2 11  2 13  2  3
```

Notice:

```text
spf[prime] = prime
```

for:

```text
2, 3, 5, 7, 11, 13
```

---

# 7. Factorization Using SPF

Once `spf[]` is ready:

```cpp
vector<int> primeFactors(int x) {
    vector<int> factors;

    while (x > 1) {
        int p = spf[x];
        factors.push_back(p);
        x /= p;
    }

    return factors;
}
```

Read it as:

```text
x
|
v
take p = spf[x]
|
v
record p
|
v
x = x / p
|
v
repeat until x = 1
```

---

# 8. Complete Dry Run — 60

Suppose:

```text
spf[60] = 2
spf[30] = 2
spf[15] = 3
spf[5]  = 5
```

Dry run:

```text
x = 60
p = spf[60] = 2
factor = [2]
x = 30
```

```text
x = 30
p = spf[30] = 2
factor = [2,2]
x = 15
```

```text
x = 15
p = spf[15] = 3
factor = [2,2,3]
x = 5
```

```text
x = 5
p = spf[5] = 5
factor = [2,2,3,5]
x = 1
```

Visual:

```text
60
| /2
v
30
| /2
v
15
| /3
v
 5
| /5
v
 1
```

Answer:

```text
60 = 2 x 2 x 3 x 5
```

---

# 9. C++ Implementation

```cpp
#include <bits/stdc++.h>
using namespace std;

const int MAXN = 1'000'000;
vector<int> spf(MAXN + 1);

void buildSPF() {
    for (int i = 2; i <= MAXN; i++) {
        spf[i] = i;
    }

    for (int i = 2; i <= MAXN; i++) {
        if (spf[i] == i) { // i is prime

            for (long long j = 2LL * i; j <= MAXN; j += i) {
                if (spf[j] == j) {
                    spf[j] = i;
                }
            }
        }
    }
}

vector<int> primeFactors(int x) {
    vector<int> factors;

    while (x > 1) {
        int p = spf[x];
        factors.push_back(p);
        x /= p;
    }

    return factors;
}

int main() {
    buildSPF();

    int x;
    cin >> x;

    vector<int> factors = primeFactors(x);

    for (int p : factors) {
        cout << p << ' ';
    }
}
```

Example:

```text
Input:
60

Output:
2 2 3 5
```

---

# 10. Multiple Queries

This technique becomes especially useful for many queries.

Example:

```text
Q = 4

60
84
25
13
```

Precompute only once:

```text
buildSPF()
```

Then:

```text
60 -> 2 2 3 5
84 -> 2 2 3 7
25 -> 5 5
13 -> 13
```

Query structure:

```text
            build SPF once
                  |
        +---------+---------+
        |         |         |
        v         v         v
      X1        X2        X3 ...
      |          |          |
   O(logX)    O(logX)    O(logX)
```

---

# 11. Complexity

Let `N` be the maximum possible query value.

### Precomputation

For this sieve-style SPF construction:

```text
Time   ~ O(N log log N)
Memory = O(N)
```

### One query

```text
O(log X)
```

### Q queries

Useful model:

```text
precompute once
+
Q cheap factorizations
```

```text
~ O(N log log N + Q log N)
```

For:

```text
N <= 10^6
```

the SPF array is practical.

---

# 12. Contest Recognition

| Clue | Think |
|---|---|
| Factorize one number | Trial division |
| One `X` around `10^12` | `O(sqrt(X))` factorization |
| Many queries, `X <= 10^6` | **SPF precomputation** |
| Need smallest prime factor repeatedly | **SPF** |
| Need all primes up to moderate `N` | Normal sieve |
| Huge `R`, narrow `[L,R]` | Segmented sieve |

Example constraint:

```text
Q <= 2 x 10^5
X <= 10^6

For each query, print the prime factors of X.
```

Observation:

```text
many queries
    +
same bounded range
    +
same expensive operation repeated
        |
        v
PRECOMPUTE
        |
        v
SPF
```

---

# 13. Final Memory Model

Don't memorize the code.

Remember the transformation:

```text
Many factorization queries
          |
          v
Don't search for factors repeatedly
          |
          v
Precompute spf[x] for every x
          |
          v
spf[x] gives next prime factor
          |
          v
x /= spf[x]
          |
          v
repeat until x = 1
```

For `60`:

```text
60 --2--> 30 --2--> 15 --3--> 5 --5--> 1
```

Therefore:

```text
60 = 2 x 2 x 3 x 5
```

## Two things to remember

```text
spf[x] = smallest prime factor of x
```

and:

```cpp
while (x > 1) {
    int p = spf[x];
    x /= p;
}
```

> **Contest habit:** When you see **many factorization queries over a bounded range**, don't ask *"How do I factor each number faster?"* Ask: **"What factor information can I precompute once and reuse?"**
