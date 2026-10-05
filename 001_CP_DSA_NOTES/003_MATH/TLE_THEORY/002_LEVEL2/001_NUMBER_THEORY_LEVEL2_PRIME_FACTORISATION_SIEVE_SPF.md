# Number Theory — Beginner Level 2
## Prime Factorisation, Sieve of Eratosthenes & SPF

> **Goal:** understand *why* each technique works. Do not memorize code first.  
> Model: **definition → observation → derivation → dry run → C++ → recognition**.

---

# Table of Contents

- [0. Preliminary Mathematics](#0-preliminary-mathematics)
  - [0.1 Divisibility](#01-divisibility)
  - [0.2 Factor vs Prime Factor](#02-factor-vs-prime-factor)
  - [0.3 Prime and Composite Numbers](#03-prime-and-composite-numbers)
  - [0.4 Factor Pairs](#04-factor-pairs)
  - [0.5 Why sqrt(N) Appears](#05-why-sqrtn-appears)
  - [0.6 Prime Factorisation](#06-prime-factorisation)
- [1. Finding Factors in O(sqrt(N))](#1-finding-factors-in-osqrtn)
- [2. Prime Factorisation Using Trial Division](#2-prime-factorisation-using-trial-division)
  - [2.1 Core Idea](#21-core-idea)
  - [2.2 Why the Divisors We Extract Are Prime](#22-why-the-divisors-we-extract-are-prime)
  - [2.3 Why a Prime Can Repeat](#23-why-a-prime-can-repeat)
  - [2.4 Why a Remaining N Greater Than 1 Is Prime](#24-why-a-remaining-n-greater-than-1-is-prime)
  - [2.5 Dry Runs](#25-dry-runs)
  - [2.6 C++](#26-c)
  - [2.7 Don't-Memorize Model](#27-dont-memorize-model)
- [3. Sieve of Eratosthenes](#3-sieve-of-eratosthenes)
  - [3.1 Why We Need a Sieve](#31-why-we-need-a-sieve)
  - [3.2 Core Idea](#32-core-idea)
  - [3.3 Why Mark Multiples](#33-why-mark-multiples)
  - [3.4 Why Start From p²](#34-why-start-from-p²)
  - [3.5 Dry Run](#35-dry-run)
  - [3.6 C++](#36-c)
  - [3.7 Complexity Intuition](#37-complexity-intuition)
  - [3.8 Don't-Memorize Model](#38-dont-memorize-model)
- [4. Smallest Prime Factor — SPF](#4-smallest-prime-factor--spf)
  - [4.1 Definition](#41-definition)
  - [4.2 Building SPF](#42-building-spf)
  - [4.3 Dry Run](#43-dry-run)
  - [4.4 C++](#44-c)
  - [4.5 Don't-Memorize Model](#45-dont-memorize-model)
- [5. Fast Prime Factorisation Using SPF](#5-fast-prime-factorisation-using-spf)
  - [5.1 Core Idea](#51-core-idea)
  - [5.2 Why Repeated Division Works](#52-why-repeated-division-works)
  - [5.3 Dry Runs](#53-dry-runs)
  - [5.4 C++](#54-c)
  - [5.5 Complexity](#55-complexity)
  - [5.6 Don't-Memorize Model](#56-dont-memorize-model)
- [6. Trial Division vs Sieve vs SPF](#6-trial-division-vs-sieve-vs-spf)
- [7. Final Recognition Model](#7-final-recognition-model)

---

# 0. Preliminary Mathematics

## 0.1 Divisibility

A number `d` divides `N` when there is no remainder.

```text
N % d == 0
```

Mathematical notation:

```text
d | N
```

Example:

```text
12 % 3 = 0
```

Therefore:

```text
3 | 12
```

But:

```text
12 % 5 = 2
```

Therefore `5` is not a factor of `12`.

---

## 0.2 Factor vs Prime Factor

### Factor

A factor is any integer that divides a number exactly.

For:

```text
N = 12
```

the positive factors are:

```text
1, 2, 3, 4, 6, 12
```

because:

```text
12 = 1 × 12
12 = 2 × 6
12 = 3 × 4
```

### Prime factor

A **prime factor** is a factor that is also prime.

Prime factors of `12` are:

```text
2 and 3
```

With multiplicity:

```text
12 = 2 × 2 × 3
```

or:

```text
12 = 2² × 3
```

Do not confuse:

```text
all factors:       1, 2, 3, 4, 6, 12
prime factors:     2, 3
factorisation:     2 × 2 × 3
```

---

## 0.3 Prime and Composite Numbers

A prime number has exactly two positive divisors:

```text
1 and itself
```

Examples:

```text
2, 3, 5, 7, 11, 13, 17, ...
```

A composite number has another divisor besides `1` and itself.

Examples:

```text
4 = 2 × 2
6 = 2 × 3
12 = 3 × 4
```

Important:

```text
1 is NOT prime.
```

---

## 0.4 Factor Pairs

If:

```text
d | N
```

then there is another factor:

```text
N / d
```

because:

```text
d × (N/d) = N
```

For `N = 12`:

```text
1 × 12
2 × 6
3 × 4
```

Notice:

```text
small factor × large factor = N
```

The factors naturally occur in pairs.

---

## 0.5 Why sqrt(N) Appears

This is one of the most important number-theory observations.

Suppose:

```text
a × b = N
```

Can both `a` and `b` be greater than `sqrt(N)`?

If:

```text
a > sqrt(N)
b > sqrt(N)
```

then:

```text
a × b > sqrt(N) × sqrt(N)
```

and:

```text
sqrt(N) × sqrt(N) = N
```

so:

```text
a × b > N
```

But we started with:

```text
a × b = N
```

Contradiction.

Therefore, in every factor pair:

```text
at least one factor <= sqrt(N)
```

### Example — N = 36

```text
sqrt(36) = 6
```

Factor pairs:

```text
1 × 36
2 × 18
3 × 12
4 × 9
6 × 6
```

Every pair has a member `<= 6`.

Therefore we only need to test:

```text
1 ... sqrt(N)
```

to discover all factor pairs.

### Safer C++ condition

Instead of:

```cpp
i * i <= n
```

for very large integers, use:

```cpp
i <= n / i
```

to avoid multiplication overflow.

---

## 0.6 Prime Factorisation

Prime factorisation means expressing a number as a product of primes.

Example:

```text
36
= 2 × 18
= 2 × 2 × 9
= 2 × 2 × 3 × 3
```

Therefore:

```text
36 = 2² × 3²
```

Another example:

```text
102
= 2 × 51
= 2 × 3 × 17
```

All final components are prime.

The fundamental model is:

```text
composite number
      |
      v
split using a prime divisor
      |
      v
smaller number
      |
      v
repeat
      |
      v
only primes remain
```

---

# 1. Finding Factors in O(sqrt(N))

A naive factor search checks:

```text
1, 2, 3, ..., N
```

Complexity:

```text
O(N)
```

But factor pairs let us stop at `sqrt(N)`.

For every `i`:

```text
if N % i == 0
```

then both are factors:

```text
i
N / i
```

### Example — N = 16

Check only through:

```text
sqrt(16) = 4
```

```text
i = 1 -> 1 and 16
i = 2 -> 2 and 8
i = 3 -> not a divisor
i = 4 -> 4 and 4
```

Factors:

```text
1, 2, 4, 8, 16
```

Be careful at a perfect square:

```text
4 × 4 = 16
```

Do not insert `4` twice.

### C++

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<long long> factors(long long n) {
    vector<long long> ans;

    for (long long i = 1; i <= n / i; ++i) {
        if (n % i != 0)
            continue;

        ans.push_back(i);

        if (i != n / i)
            ans.push_back(n / i);
    }

    sort(ans.begin(), ans.end());
    return ans;
}
```

Complexity:

```text
O(sqrt(N))
```

---

# 2. Prime Factorisation Using Trial Division

## 2.1 Core Idea

We want:

```text
N = p1 × p2 × p3 × ...
```

where every `p` is prime.

Try possible divisors starting from `2`.

Whenever `d` divides `N`:

```text
record d
N = N / d
```

and keep dividing by the same `d` while possible.

Pseudo-model:

```text
d = 2

while d*d <= N:
    while N % d == 0:
        take d
        N /= d

    d++
```

Finally:

```text
if N > 1:
    take N
```

---

## 2.2 Why the Divisors We Extract Are Prime

Suppose we scan upward:

```text
2, 3, 4, 5, ...
```

Assume `d` is the first number that divides the current `N`.

Could `d` be composite?

Suppose:

```text
d = a × b
```

with:

```text
1 < a < d
```

If composite `d` divides `N`, one of its prime factors would already have been encountered earlier and removed.

Therefore the useful new divisor encountered by this process behaves as a prime factor.

Example:

```text
N = 36
```

We first find:

```text
2
```

and remove every `2`:

```text
36 / 2 = 18
18 / 2 = 9
```

Current:

```text
N = 9
```

We never need `4` as a prime factor because its `2`s have already been removed.

Next:

```text
3
```

removes:

```text
9 -> 3 -> 1
```

Answer:

```text
2, 2, 3, 3
```

---

## 2.3 Why a Prime Can Repeat

Consider:

```text
72
```

Factorisation:

```text
72 = 2 × 2 × 2 × 3 × 3
```

If we divide by `2` only once:

```text
72 / 2 = 36
```

`36` is still divisible by `2`.

Therefore we need:

```cpp
while (n % d == 0)
```

not:

```cpp
if (n % d == 0)
```

Dry run:

```text
72
 |
 /2
 v
36
 |
 /2
 v
18
 |
 /2
 v
9
 |
 /3
 v
3
 |
 /3
 v
1
```

Collected:

```text
2, 2, 2, 3, 3
```

---

## 2.4 Why a Remaining N Greater Than 1 Is Prime

Consider:

```text
N = 102
```

Start:

```text
102 / 2 = 51
51  / 3 = 17
```

Now current:

```text
N = 17
```

The loop eventually stops before explicitly testing `17`.

Why can we append it?

Suppose the remaining `N > 1` were composite.

Then it would have some prime factor:

```text
p <= sqrt(N)
```

But our trial-division process would already have found and removed such a factor.

Contradiction.

Therefore the leftover value must be prime.

So:

```cpp
if (n > 1)
    factors.push_back(n);
```

is essential.

---

## 2.5 Dry Runs

### Example 1 — N = 36

Start:

```text
N = 36
```

Try `d = 2`:

```text
36 % 2 = 0
```

Take `2`:

```text
factors = [2]
N = 36 / 2 = 18
```

Still divisible:

```text
18 % 2 = 0
```

Take another `2`:

```text
factors = [2, 2]
N = 18 / 2 = 9
```

Now:

```text
9 % 2 != 0
```

Move forward.

Try `d = 3`:

```text
9 % 3 = 0
```

```text
factors = [2, 2, 3]
N = 3
```

Again:

```text
3 % 3 = 0
```

```text
factors = [2, 2, 3, 3]
N = 1
```

Final:

```text
36 = 2 × 2 × 3 × 3
```

---

### Example 2 — N = 102

```text
102
```

Take `2`:

```text
102 / 2 = 51
```

Take `3`:

```text
51 / 3 = 17
```

Now the remaining `17` is prime.

Final:

```text
102 = 2 × 3 × 17
```

---

### Example 3 — Prime Input

```text
N = 17
```

No divisor from:

```text
2 ... sqrt(17)
```

divides it.

So the remaining value is:

```text
17
```

and:

```text
17 = 17
```

---

## 2.6 C++

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
Worst case: O(sqrt(N))
```

---

## 2.7 Don't-Memorize Model

Do not memorize the loop.

Derive it:

```text
Need prime factors of ONE number
        |
        v
Every composite has a factor <= sqrt(N)
        |
        v
try small divisors
        |
        v
when divisor found:
remove it completely
        |
        v
N becomes smaller
        |
        v
after small factors are removed,
leftover N > 1 must be prime
```

Recognition:

```text
one/few factorisation queries
        |
        v
trial division is often enough
```

---

# 3. Sieve of Eratosthenes

## 3.1 Why We Need a Sieve

Suppose we need to know whether many numbers up to `N` are prime.

Running trial division independently for every number would repeat a lot of work.

Instead:

```text
precompute primality for every value 0..N once
```

That is the role of the Sieve of Eratosthenes.

---

## 3.2 Core Idea

Initially assume:

```text
2, 3, 4, ..., N
```

are prime candidates.

Start with:

```text
2
```

`2` is prime.

Therefore all larger multiples of `2` are composite:

```text
4, 6, 8, 10, ...
```

Next unmarked number:

```text
3
```

is prime.

Mark its multiples:

```text
6, 9, 12, 15, ...
```

Next unmarked candidate:

```text
5
```

and continue.

Mental model:

```text
prime p
   |
   v
p × 2
p × 3
p × 4
...
   |
   v
all composite
```

---

## 3.3 Why Mark Multiples

If:

```text
x = p × k
```

where:

```text
k > 1
```

then `x` has divisors:

```text
1, p, x
```

so `x` cannot be prime.

Example with `p = 3`:

```text
6  = 3 × 2
9  = 3 × 3
12 = 3 × 4
15 = 3 × 5
```

All are composite.

So once `p` is known prime, its multiples can be crossed out.

---

## 3.4 Why Start From p²

A basic implementation might mark:

```text
2p, 3p, 4p, ...
```

But when processing prime `p`, smaller multiples have already been handled by smaller prime factors.

Example:

```text
p = 5
```

Consider:

```text
2 × 5 = 10
3 × 5 = 15
4 × 5 = 20
```

They were already marked:

```text
10 by 2
15 by 3
20 by 2
```

The first multiple of `5` that may not already have been handled is:

```text
5 × 5 = 25
```

Therefore start from:

```text
p²
```

General reasoning:

For a multiple:

```text
p × k
```

if:

```text
k < p
```

then `k` contains a prime factor smaller than `p`.

That smaller prime already marked the number.

So:

```text
start = p²
```

avoids redundant work.

---

## 3.5 Dry Run

Find primes up to:

```text
N = 16
```

Initial candidates:

```text
2 3 4 5 6 7 8 9 10 11 12 13 14 15 16
```

### p = 2

`2` is prime.

Start marking from:

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

`3` is prime.

Start from:

```text
3² = 9
```

Mark:

```text
9, 12, 15
```

`12` was already composite; that is fine.

Remaining primes:

```text
2, 3, 5, 7, 11, 13
```

Since:

```text
4² > 16
```

we are done.

Final:

```text
2, 3, 5, 7, 11, 13
```

---

## 3.6 C++

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

Example:

```cpp
int main() {
    int n = 100;

    vector<bool> isPrime = buildSieve(n);

    for (int x = 2; x <= n; ++x) {
        if (isPrime[x])
            cout << x << ' ';
    }
}
```

---

## 3.7 Complexity Intuition

For prime `2`, we mark roughly:

```text
N / 2
```

numbers.

For prime `3`:

```text
N / 3
```

For prime `5`:

```text
N / 5
```

So the work resembles:

```text
N/2 + N/3 + N/5 + N/7 + ...
```

over primes.

Factor out `N`:

```text
N × (1/2 + 1/3 + 1/5 + 1/7 + ...)
```

The sum of reciprocals of primes grows approximately like:

```text
log log N
```

Hence the classic complexity:

```text
O(N log log N)
```

For CP, remember the interpretation:

```text
near-linear precomputation
```

rather than treating the complexity formula as magic.

---

## 3.8 Don't-Memorize Model

```text
Need primality for MANY values <= N
        |
        v
checking each independently repeats work
        |
        v
if p is prime
        |
        v
all multiples of p are composite
        |
        v
cross them out
        |
        v
start at p² because smaller multiples
were already handled
```

Recognition:

```text
many prime queries over a bounded range
        |
        v
SIEVE
```

---

# 4. Smallest Prime Factor — SPF

## 4.1 Definition

`SPF[x]` means:

```text
smallest prime factor of x
```

Examples:

```text
SPF[2]  = 2
SPF[3]  = 3
SPF[4]  = 2
SPF[6]  = 2
SPF[9]  = 3
SPF[15] = 3
```

For a prime number:

```text
SPF[p] = p
```

because its only positive prime divisor is itself.

Example:

```text
SPF[11] = 11
```

---

## 4.2 Building SPF

The sieve only answers:

```text
Is x prime?
```

SPF stores more information:

```text
What is x's smallest prime divisor?
```

Initialize:

```text
spf[x] = x
```

Then for each prime `p`, visit its multiples.

If a multiple has not received a smaller prime factor yet, assign:

```text
spf[multiple] = p
```

### Example

For:

```text
12
```

prime divisors are:

```text
2, 3
```

The smallest is:

```text
2
```

Therefore:

```text
SPF[12] = 2
```

For:

```text
15
```

prime divisors:

```text
3, 5
```

Therefore:

```text
SPF[15] = 3
```

---

## 4.3 Dry Run

Build SPF up to `15`.

Result:

| x | SPF[x] |
|---:|---:|
| 2 | 2 |
| 3 | 3 |
| 4 | 2 |
| 5 | 5 |
| 6 | 2 |
| 7 | 7 |
| 8 | 2 |
| 9 | 3 |
| 10 | 2 |
| 11 | 11 |
| 12 | 2 |
| 13 | 13 |
| 14 | 2 |
| 15 | 3 |

Pattern:

```text
even composite -> SPF = 2

multiples of 3 not already caught by 2
-> SPF = 3

prime p
-> SPF[p] = p
```

---

## 4.4 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> buildSPF(int n) {
    vector<int> spf(n + 1);

    for (int i = 0; i <= n; ++i)
        spf[i] = i;

    if (n >= 0) spf[0] = 0;
    if (n >= 1) spf[1] = 1;

    for (long long p = 2; p * p <= n; ++p) {
        if (spf[p] != p)
            continue;  // p is not prime

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

## 4.5 Don't-Memorize Model

Do not memorize an SPF array as a special trick.

Think:

```text
Sieve:
"this number is composite"

SPF:
"this number is composite,
 and THIS is its smallest prime divisor"
```

So SPF is essentially:

```text
sieve + remember the first prime that divides x
```

Recognition:

```text
many factorisation queries
+
values bounded by MAXN
        |
        v
precompute SPF
```

---

# 5. Fast Prime Factorisation Using SPF

## 5.1 Core Idea

Suppose we already know:

```text
SPF[x]
```

Then immediately:

```text
p = SPF[x]
```

is a prime factor of `x`.

Remove it:

```text
x = x / p
```

Then repeat.

Algorithm:

```text
while x > 1:
    p = SPF[x]
    take p
    x /= p
```

No trial search is required.

---

## 5.2 Why Repeated Division Works

Suppose:

```text
x = 28
```

We know:

```text
SPF[28] = 2
```

Therefore:

```text
28 = 2 × 14
```

After removing `2`:

```text
x = 14
```

Again:

```text
SPF[14] = 2
```

so:

```text
14 = 2 × 7
```

After removing `2`:

```text
x = 7
```

Since `7` is prime:

```text
SPF[7] = 7
```

Remove:

```text
7 / 7 = 1
```

Collected:

```text
2, 2, 7
```

Therefore:

```text
28 = 2² × 7
```

Each step removes at least one prime factor.

---

## 5.3 Dry Runs

### Example 1 — N = 28

```text
x = 28
SPF[28] = 2
```

Take:

```text
2
```

Update:

```text
x = 28 / 2 = 14
```

Next:

```text
SPF[14] = 2
```

Take:

```text
2
```

Update:

```text
x = 14 / 2 = 7
```

Next:

```text
SPF[7] = 7
```

Take:

```text
7
```

Update:

```text
x = 7 / 7 = 1
```

Stop.

Answer:

```text
[2, 2, 7]
```

---

### Example 2 — N = 36

```text
36
 |
 SPF = 2
 v
18
 |
 SPF = 2
 v
9
 |
 SPF = 3
 v
3
 |
 SPF = 3
 v
1
```

Factors:

```text
2, 2, 3, 3
```

Grouped form:

```text
36 = 2² × 3²
```

---

## 5.4 C++

### Return factors with multiplicity

```cpp
vector<int> factorizeWithSPF(int x, const vector<int>& spf) {
    vector<int> factors;

    while (x > 1) {
        int p = spf[x];
        factors.push_back(p);
        x /= p;
    }

    return factors;
}
```

### Return `(prime, exponent)`

Often this form is more useful in number-theory problems:

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

Example:

```text
x = 36
```

returns:

```text
(2,2)
(3,2)
```

meaning:

```text
36 = 2² × 3²
```

---

## 5.5 Complexity

### Precomputation

For the sieve-style SPF construction:

```text
O(N log log N)
```

### Each factorisation query

Each division removes a prime factor.

A number `x` can contain at most about:

```text
log2(x)
```

prime factors with multiplicity, because the smallest prime is `2`.

For example:

```text
x = 2^k
```

requires `k` divisions and:

```text
k = log2(x)
```

So factorisation using SPF is:

```text
O(log x)
```

after precomputation.

Compare:

```text
Trial division:
O(sqrt(x)) per query

SPF:
precompute once
then O(log x) per query
```

This is why SPF becomes valuable when there are many queries.

---

## 5.6 Don't-Memorize Model

```text
Need factorisation of MANY numbers
        |
        v
trial division repeatedly searches
for the next divisor
        |
        v
Can we store the next divisor beforehand?
        |
        v
SPF[x] = smallest prime divisor of x
        |
        v
take SPF[x]
        |
        v
x /= SPF[x]
        |
        v
repeat until x = 1
```

The key idea is:

```text
Trial division:
SEARCH for a factor each time.

SPF:
LOOK UP the factor directly.
```

---

# 6. Trial Division vs Sieve vs SPF

| Need | Technique | Main Cost |
|---|---|---|
| Find factors of one `N` | factor-pair scan | `O(sqrt(N))` |
| Prime-factorise one/few numbers | trial division | `O(sqrt(N))` worst case |
| Know all primes up to `N` | sieve | `O(N log log N)` preprocessing |
| Many primality queries | sieve | `O(1)` lookup after preprocessing |
| Many factorisation queries | SPF | precompute + `O(log x)` per query |
| Need smallest prime divisor | SPF | direct lookup |

Decision model:

```text
What does the problem ask?
          |
          +----------------------+
          |                      |
     ONE number             MANY numbers
          |                      |
          v                      v
   trial division       precomputation useful
                                 |
                       +---------+---------+
                       |                   |
                  primality?          factorisation?
                       |                   |
                       v                   v
                     SIEVE                SPF
```

---

# 7. Final Recognition Model

## Pattern 1 — Factor pairs

When you see:

```text
find divisors of N
```

think:

```text
d × (N/d) = N
        |
        v
one member of every pair <= sqrt(N)
        |
        v
iterate only to sqrt(N)
```

---

## Pattern 2 — Prime factorisation

When you see:

```text
break N into primes
```

think:

```text
find small prime divisor
        |
        v
divide it out completely
        |
        v
N becomes smaller
        |
        v
repeat
```

---

## Pattern 3 — Sieve

When you see:

```text
many primality questions
for values <= MAXN
```

think:

```text
prime p
   |
   v
multiples of p cannot be prime
   |
   v
mark them once
   |
   v
reuse answers
```

---

## Pattern 4 — SPF

When you see:

```text
many prime-factorisation queries
```

think:

```text
instead of SEARCHING for a factor
        |
        v
PRECOMPUTE the smallest factor
        |
        v
SPF[x]
        |
        v
divide and repeat
```

---

# Master Don't-Memorize Model

```text
                 NUMBER THEORY QUERY
                         |
          +--------------+--------------+
          |                             |
      one/few N                      many N
          |                             |
          v                             v
  Can sqrt(N) work?             bounded maximum?
          |                             |
         YES                           YES
          |                             |
          v                    +--------+--------+
   Trial Division              |                 |
                         need primes?      need factors?
                              |                 |
                              v                 v
                            SIEVE              SPF
```

Always derive from these questions:

```text
1. What information do I need?
2. Is this one query or many queries?
3. What is the maximum value?
4. Can factor-pair symmetry reduce N -> sqrt(N)?
5. Am I repeatedly searching for information
   that could be precomputed once?
```

Then choose the technique.

---

# Compact Revision Card

```text
FACTOR
d | N  <=>  N % d == 0

FACTOR PAIR
d × (N/d) = N

SQRT OBSERVATION
Every composite N has a factor <= sqrt(N).

TRIAL DIVISION
Try divisors -> divide found prime completely.
Worst case: O(sqrt(N)).

SIEVE
Prime p -> mark multiples composite.
Start marking at p².
Precompute: O(N log log N).

SPF
SPF[x] = smallest prime factor of x.
Prime p -> SPF[p] = p.

FAST FACTORISATION
while x > 1:
    p = SPF[x]
    take p
    x /= p

Query: O(log x)
```

> **Final memory anchor:**  
> **Trial division searches. Sieve marks. SPF remembers.**
