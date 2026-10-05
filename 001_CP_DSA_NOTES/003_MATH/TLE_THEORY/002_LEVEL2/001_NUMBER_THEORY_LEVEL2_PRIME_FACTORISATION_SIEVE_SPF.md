# Number Theory — Beginner Level 2
## Prime Factorisation, Sieve of Eratosthenes & SPF

> **Goal:** understand why each technique works, then derive the code.
>
> **Model:** definition → observation → derivation → one dry run → C++ → recognition.

---

# Table of Contents

- [0. Preliminary Mathematics](#0-preliminary-mathematics)
- [1. Trial Division — Prime Factorisation](#1-trial-division--prime-factorisation)
- [2. Sieve of Eratosthenes](#2-sieve-of-eratosthenes)
- [3. Smallest Prime Factor — SPF](#3-smallest-prime-factor--spf)
- [4. Fast Factorisation Using SPF](#4-fast-factorisation-using-spf)
- [5. Which Technique Should I Use?](#5-which-technique-should-i-use)
- [6. Final Don't-Memorize Model](#6-final-dont-memorize-model)
- [7. Compact Revision Card](#7-compact-revision-card)

---

# 0. Preliminary Mathematics

## 0.1 Divisibility

`d` divides `N` when division leaves no remainder.

```text
N % d == 0
```

Mathematically:

```text
d | N
```

Example:

```text
12 % 3 = 0  →  3 divides 12
12 % 5 = 2  →  5 does not divide 12
```

---

## 0.2 Factor vs Prime Factor

For:

```text
N = 12
```

All positive factors:

```text
1, 2, 3, 4, 6, 12
```

Prime factors:

```text
2, 3
```

Prime factorisation:

```text
12 = 2 × 2 × 3
   = 2² × 3
```

So:

```text
factor           = any number dividing N
prime factor     = a factor that is prime
prime factorisation = N written as a product of primes
```

---

## 0.3 Prime and Composite

A prime number has exactly two positive divisors:

```text
1 and itself
```

Examples:

```text
2, 3, 5, 7, 11, 13, ...
```

A composite number has another divisor besides `1` and itself.

```text
4  = 2 × 2
6  = 2 × 3
12 = 3 × 4
```

Important:

```text
1 is not prime.
```

---

## 0.4 Factor Pairs and Why √N Appears

If:

```text
d | N
```

then:

```text
d × (N/d) = N
```

So divisors occur in pairs.

Example:

```text
N = 36

1 × 36
2 × 18
3 × 12
4 × 9
6 × 6
```

Why do we only search up to `√N`?

Suppose:

```text
a × b = N
```

If both were greater than `√N`:

```text
a > √N
b > √N
```

then:

```text
a × b > √N × √N
      > N
```

But:

```text
a × b = N
```

Contradiction.

Therefore:

```text
In every factor pair, at least one factor <= √N.
```

### CP consequence

To find factors or search for a divisor:

```text
check only up to √N
```

Safer C++ condition:

```cpp
i <= n / i
```

instead of:

```cpp
i * i <= n
```

when overflow may matter.

---

# 1. Trial Division — Prime Factorisation

## 1.1 Problem

Given one number `N`, write it as:

```text
N = p1 × p2 × p3 × ...
```

where every `p` is prime.

Example:

```text
102 = 2 × 3 × 17
```

---

## 1.2 Core Idea

Try possible divisors from small to large.

Whenever `d` divides `N`:

```text
take d
N /= d
```

Keep dividing by the same `d` until it no longer divides `N`.

Why repeatedly?

```text
72 = 2 × 2 × 2 × 3 × 3
```

A prime factor can occur more than once.

So use:

```cpp
while (n % d == 0)
```

not only:

```cpp
if (n % d == 0)
```

---

## 1.3 Why Can We Stop at √N?

From the preliminary result:

```text
every composite number has a factor <= √N
```

Therefore, if the current `N` has no divisor up to `√N`, it cannot still be composite.

So after trial division:

```cpp
if (n > 1)
```

the remaining `n` is prime.

---

## 1.4 Dry Run — N = 102

Start:

```text
N = 102
factors = []
```

Try `2`:

```text
102 % 2 = 0

take 2
N = 102 / 2
  = 51

factors = [2]
```

Try `3`:

```text
51 % 3 = 0

take 3
N = 51 / 3
  = 17

factors = [2, 3]
```

Now:

```text
N = 17
```

There is no smaller factor left to remove.

So the leftover is prime:

```text
factors = [2, 3, 17]
```

Hence:

```text
102 = 2 × 3 × 17
```

---

## 1.5 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<long long> primeFactorization(long long n) {
    vector<long long> factors;

    for (long long d = 2; d <= n / d; ++d) {
        while (n % d == 0) {
            factors.push_back(d);
            n /= d;
        }
    }

    if (n > 1)
        factors.push_back(n);

    return factors;
}
```

Complexity:

```text
Worst case: O(√N)
```

---

## 1.6 Don't-Memorize Model

```text
Need prime factors of one/few numbers
              |
              v
composite N has a factor <= √N
              |
              v
try small divisors
              |
              v
found divisor?
              |
              v
remove it completely
              |
              v
N becomes smaller
              |
              v
leftover N > 1 → prime
```

Recognition:

```text
one/few factorisation queries
→ trial division
```

---

# 2. Sieve of Eratosthenes

## 2.1 Why Sieve?

Suppose we repeatedly ask:

```text
Is 37 prime?
Is 83 prime?
Is 91 prime?
...
```

and every value is bounded by `N`.

Running trial division separately repeats work.

Instead:

```text
precompute primality for every number 0..N once
```

---

## 2.2 Core Idea

If `p` is prime, then:

```text
2p, 3p, 4p, ...
```

cannot be prime because each has `p` as a divisor.

Example:

```text
p = 3

6  = 3 × 2
9  = 3 × 3
12 = 3 × 4
15 = 3 × 5
```

Therefore:

```text
prime p
   |
   v
mark multiples of p composite
```

---

## 2.3 Why Start Marking From p²?

For:

```text
p = 5
```

the smaller multiples:

```text
10 = 2 × 5
15 = 3 × 5
20 = 4 × 5
```

were already handled by smaller primes.

The first multiple that may not already be marked is:

```text
5 × 5 = 25
```

Therefore:

```text
start marking from p²
```

---

## 2.4 Dry Run — N = 16

Initial candidates:

```text
2 3 4 5 6 7 8 9 10 11 12 13 14 15 16
```

### p = 2

Start from:

```text
2² = 4
```

Mark:

```text
4, 6, 8, 10, 12, 14, 16
```

Remaining candidates:

```text
2, 3, 5, 7, 9, 11, 13, 15
```

### p = 3

Start from:

```text
3² = 9
```

Mark:

```text
9, 12, 15
```

Remaining primes:

```text
2, 3, 5, 7, 11, 13
```

Now:

```text
4² > 16
```

so we are done.

---

## 2.5 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<bool> buildSieve(int n) {
    vector<bool> isPrime(n + 1, true);

    if (n >= 0) isPrime[0] = false;
    if (n >= 1) isPrime[1] = false;

    for (long long p = 2; p * p <= n; ++p) {
        if (!isPrime[p])
            continue;

        for (long long multiple = p * p;
             multiple <= n;
             multiple += p) {

            isPrime[multiple] = false;
        }
    }

    return isPrime;
}
```

Complexity:

```text
O(N log log N)
```

Think of it as:

```text
near-linear precomputation
```

---

## 2.6 Don't-Memorize Model

```text
Need primality for MANY values <= N
              |
              v
checking each separately repeats work
              |
              v
prime p
              |
              v
multiples of p are composite
              |
              v
mark them once
              |
              v
reuse answers
```

Recognition:

```text
many primality queries + bounded MAXN
→ SIEVE
```

---

# 3. Smallest Prime Factor — SPF

## 3.1 What Is SPF?

`SPF[x]` stores:

```text
the smallest prime factor of x
```

Examples:

| `x` | `SPF[x]` |
|---:|---:|
| 2 | 2 |
| 3 | 3 |
| 4 | 2 |
| 6 | 2 |
| 9 | 3 |
| 12 | 2 |
| 15 | 3 |

For a prime:

```text
SPF[p] = p
```

Example:

```text
SPF[11] = 11
```

---

## 3.2 Sieve vs SPF

A normal sieve remembers:

```text
Is x prime?
```

SPF remembers:

```text
What is the smallest prime dividing x?
```

Mental model:

```text
Sieve → mark that x is composite

SPF   → mark that x is composite
        + remember its smallest prime divisor
```

---

## 3.3 How SPF Is Built

Initially:

```text
SPF[x] = x
```

This is already correct for primes.

When processing a prime `p`, visit its multiples.

If a multiple still has itself as SPF:

```text
SPF[multiple] == multiple
```

then no smaller prime has assigned it yet.

So:

```text
SPF[multiple] = p
```

Because primes are processed from small to large, the first prime assigned is the smallest one.

---

## 3.4 Small Dry Run

For values up to `15`:

```text
x      SPF[x]
--------------
2        2
3        3
4        2
5        5
6        2
7        7
8        2
9        3
10       2
11      11
12       2
13      13
14       2
15       3
```

Observe:

```text
SPF[12] = 2
SPF[15] = 3
SPF[13] = 13   // prime
```

---

## 3.5 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> buildSPF(int n) {
    vector<int> spf(n + 1);

    for (int i = 0; i <= n; ++i)
        spf[i] = i;

    for (long long p = 2; p * p <= n; ++p) {
        if (spf[p] != p)
            continue;  // not prime

        for (long long multiple = p * p;
             multiple <= n;
             multiple += p) {

            if (spf[multiple] == multiple)
                spf[multiple] = (int)p;
        }
    }

    return spf;
}
```

---

## 3.6 Don't-Memorize Model

```text
Need factors repeatedly
        |
        v
why search for the next factor every time?
        |
        v
precompute:
SPF[x] = smallest prime factor
```

Recognition:

```text
many factorisation queries
+ values <= MAXN
→ SPF
```

---

# 4. Fast Factorisation Using SPF

## 4.1 Core Idea

Once `SPF[]` exists:

```text
p = SPF[x]
```

immediately gives a prime factor.

Remove it:

```text
x /= p
```

and repeat:

```text
while x > 1:
    p = SPF[x]
    take p
    x /= p
```

There is no divisor search.

---

## 4.2 Dry Run — x = 36

Start:

```text
x = 36
```

First:

```text
SPF[36] = 2

take 2
x = 36 / 2
  = 18
```

Again:

```text
SPF[18] = 2

take 2
x = 18 / 2
  = 9
```

Next:

```text
SPF[9] = 3

take 3
x = 9 / 3
  = 3
```

Next:

```text
SPF[3] = 3

take 3
x = 3 / 3
  = 1
```

Collected:

```text
2, 2, 3, 3
```

Grouped:

```text
36 = 2² × 3²
```

---

## 4.3 C++ — Factors With Multiplicity

```cpp
vector<int> factorizeWithSPF(
    int x,
    const vector<int>& spf
) {
    vector<int> factors;

    while (x > 1) {
        int p = spf[x];
        factors.push_back(p);
        x /= p;
    }

    return factors;
}
```

For:

```text
x = 36
```

returns:

```text
2 2 3 3
```

---

## 4.4 C++ — `(prime, exponent)` Form

This form is often more useful.

```cpp
vector<pair<int,int>> factorizeCount(
    int x,
    const vector<int>& spf
) {
    vector<pair<int,int>> factors;

    while (x > 1) {
        int p = spf[x];
        int cnt = 0;

        while (x > 1 && spf[x] == p) {
            x /= p;
            ++cnt;
        }

        factors.push_back({p, cnt});
    }

    return factors;
}
```

For:

```text
36 = 2² × 3²
```

returns:

```text
(2, 2)
(3, 2)
```

---

## 4.5 Why O(log x)?

Each step divides `x` by a prime.

The smallest possible prime is `2`.

The maximum number of divisions happens roughly when:

```text
x = 2^k
```

Then:

```text
k = log₂(x)
```

Therefore factorisation after SPF precomputation is:

```text
O(log x)
```

Comparison:

```text
Trial division → search for factor → O(√x) worst case

SPF            → look up factor   → O(log x)
```

---

## 4.6 Don't-Memorize Model

```text
Trial Division
--------------
SEARCH for a factor
      |
      v
divide


SPF
---
LOOK UP factor directly
      |
      v
divide
      |
      v
repeat
```

That is the entire reason SPF is fast.

---

# 5. Which Technique Should I Use?

| Problem asks for | Technique | Cost |
|---|---|---|
| Factors of one `N` | factor-pair scan | `O(√N)` |
| Prime factors of one/few numbers | trial division | `O(√N)` worst case |
| All primes up to `N` | sieve | `O(N log log N)` preprocessing |
| Many primality queries | sieve | `O(1)` lookup after preprocessing |
| Smallest prime factor | SPF | precompute once |
| Many factorisation queries | SPF | `O(log x)` per query after preprocessing |

Decision model:

```text
                  What do I need?
                        |
             +----------+----------+
             |                     |
          one/few N             many N
             |                     |
             v                     v
       trial division       bounded MAXN?
                                   |
                                  YES
                                   |
                         +---------+---------+
                         |                   |
                    primality?          factorisation?
                         |                   |
                         v                   v
                       SIEVE                SPF
```

---

# 6. Final Don't-Memorize Model

The three techniques are closely related.

```text
                    NUMBER THEORY
                         |
        +----------------+----------------+
        |                                 |
   one/few numbers                    many numbers
        |                                 |
        v                                 v
 search only to √N                 precompute once
        |                                 |
        v                       +---------+---------+
 Trial Division                  |                   |
                           need primes?        need factors?
                                 |                   |
                                 v                   v
                               SIEVE                SPF
```

Ask these questions:

```text
1. What information does the problem need?

2. One query or many queries?

3. What is the maximum value?

4. Can factor-pair symmetry reduce N → √N?

5. Am I repeatedly searching for something
   that could be precomputed once?
```

Then derive the technique.

---

# 7. Compact Revision Card

```text
DIVISIBILITY
d | N  <=>  N % d == 0


FACTOR PAIR
d × (N/d) = N


√N OBSERVATION
Every composite N has a factor <= √N.


TRIAL DIVISION
Need prime factors of one/few numbers.

try d <= √N
while d divides N:
    take d
    N /= d

leftover N > 1 → prime

Worst case: O(√N)


SIEVE
Need primality for many values <= N.

prime p
→ multiples are composite
→ start marking at p²

Precompute: O(N log log N)


SPF
SPF[x] = smallest prime factor of x.

Sieve:
"composite"

SPF:
"composite + smallest prime divisor"


FAST FACTORISATION

while x > 1:
    p = SPF[x]
    take p
    x /= p

Query: O(log x)
```

> **Final memory anchor**
>
> **Trial division searches. Sieve marks. SPF remembers.**
