# 006_Sieve_And_SPF.md

# MiniMathEngine — Sieve Of Eratosthenes and SPF

> Goal: build bottom-up intuition for:
>
> ```text
> prime precomputation
> marking multiples
> smallest prime factor
> fast factorization
> ```
>
> This file continues from:
>
> ```text
> 005_Prime_Checking.md
> ```
>
> There we checked one number using:
>
> ```text
> O(sqrt(N))
> ```
>
> Now we solve:
>
> ```text
> many prime queries efficiently
> ```

---

# Clickable Index

- [1. Why This Topic Matters](#1-why-this-topic-matters)
- [2. Problem With Repeated Prime Checking](#2-problem-with-repeated-prime-checking)
- [3. Core Sieve Intuition](#3-core-sieve-intuition)
- [4. Sieve Mental Model](#4-sieve-mental-model)
- [5. Step-by-Step Sieve Working For N = 30](#5-step-by-step-sieve-working-for-n--30)
- [6. Why Start Marking From p*p](#6-why-start-marking-from-pp)
- [7. Time Complexity Intuition](#7-time-complexity-intuition)
- [8. SPF Intuition](#8-spf-intuition)
- [9. Why SPF Is Powerful](#9-why-spf-is-powerful)
- [10. Step-by-Step SPF Working](#10-step-by-step-spf-working)
- [11. Pattern Detector](#11-pattern-detector)
- [12. C++ Code 1 — Basic Sieve](#12-c-code-1--basic-sieve)
- [13. C++ Code 2 — Prime Query Engine](#13-c-code-2--prime-query-engine)
- [14. C++ Code 3 — Count Primes Prefix](#14-c-code-3--count-primes-prefix)
- [15. C++ Code 4 — SPF Precomputation](#15-c-code-4--spf-precomputation)
- [16. C++ Code 5 — Factorization Using SPF](#16-c-code-5--factorization-using-spf)
- [17. C++ Code 6 — Count Distinct Prime Factors](#17-c-code-6--count-distinct-prime-factors)
- [18. Dry Run Table](#18-dry-run-table)
- [19. Java Logic Comments](#19-java-logic-comments)
- [20. Common Mistakes](#20-common-mistakes)
- [21. CP / FAANG Problem Forms](#21-cp--faang-problem-forms)
- [22. Real World Mapping](#22-real-world-mapping)
- [23. Practice Problems](#23-practice-problems)
- [24. Final Mental Summary](#24-final-mental-summary)
- [25. Next Step](#25-next-step)

---

# 1. Why This Topic Matters

Prime checking one number is fine:

```text
O(sqrt(N))
```

But if you have:

```text
100000 queries
```

then checking each number separately becomes slow.

Sieve helps you precompute all primes up to `N` once.

Then each query becomes:

```text
O(1)
```

SPF helps factorize numbers fast.

This is important for:

- many prime queries
- factorization
- divisor count
- Euler Phi
- coprime problems
- number theory DP
- Codeforces / AtCoder math

---

# 2. Problem With Repeated Prime Checking

Suppose:

```cpp
N = 1e6
Q = 1e5
```

If each query uses sqrt prime check:

```text
O(Q * sqrt(N))
```

That is too much.

Better:

```text
Precompute once
Answer quickly
```

This is exactly what sieve does.

---

# 3. Core Sieve Intuition

Sieve idea:

```text
Start by assuming every number is prime.
Then remove multiples of each prime.
```

Example:

```text
2 is prime
remove multiples of 2

3 is prime
remove multiples of 3

5 is prime
remove multiples of 5
```

At the end, unmarked numbers are primes.

---

# 4. Sieve Mental Model

Think like a filter:

```text
Numbers enter as possibly prime.
Composite numbers are filtered out by their smallest prime divisor.
```

Example:

```text
12 is removed by 2
15 is removed by 3
25 is removed by 5
```

A prime number survives because no smaller number divides it.

---

# 5. Step-by-Step Sieve Working For N = 30

Initial:

```text
2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30
```

Start with `2`:

```text
2 is prime
mark 4,6,8,10,12,14,16,18,20,22,24,26,28,30 as composite
```

Next `3`:

```text
3 is prime
mark 9,12,15,18,21,24,27,30 as composite
```

Next `5`:

```text
5 is prime
mark 25,30 as composite
```

Next `7`:

```text
7*7 > 30
stop marking
```

Remaining primes:

```text
2 3 5 7 11 13 17 19 23 29
```

---

# 6. Why Start Marking From p*p

For prime `p`, we mark multiples:

```text
p*p, p*(p+1), p*(p+2), ...
```

Why not start from:

```text
2*p
```

Because smaller multiples were already marked by smaller primes.

Example for `p = 5`:

```text
10 = 2*5 already marked by 2
15 = 3*5 already marked by 3
20 = 4*5 already marked by 2
```

First new unmarked composite:

```text
25 = 5*5
```

So start at:

```cpp
p*p
```

---

# 7. Time Complexity Intuition

Sieve complexity:

```text
O(N log log N)
```

You do not need to deeply prove it now.

Intuition:

```text
You mark multiples of primes only.
Each number is touched limited number of times.
```

For interview/CP:

```text
Sieve up to 1e7 is usually fine in C++.
```

Memory:

```text
O(N)
```

---

# 8. SPF Intuition

SPF means:

```text
Smallest Prime Factor
```

For every number `x`, store the smallest prime that divides it.

Example:

```text
spf[12] = 2
spf[15] = 3
spf[25] = 5
spf[49] = 7
```

For prime numbers:

```text
spf[p] = p
```

---

# 9. Why SPF Is Powerful

Once you have SPF:

```text
factorization becomes fast
```

Example:

```cpp
60
```

Use SPF:

```text
spf[60] = 2
60 / 2 = 30

spf[30] = 2
30 / 2 = 15

spf[15] = 3
15 / 3 = 5

spf[5] = 5
5 / 5 = 1
```

Factors:

```text
2, 2, 3, 5
```

This is much faster than trial division for many queries.

---

# 10. Step-by-Step SPF Working

Initialize:

```cpp
spf[i] = i
```

Then for each prime `p`, update multiples:

```cpp
if (spf[multiple] == multiple)
    spf[multiple] = p;
```

Meaning:

```text
first prime that marks it becomes smallest prime factor
```

Example:

```text
12 initially spf[12]=12

2 marks 12 first:
spf[12]=2

3 later also divides 12,
but we do not overwrite because smallest is already 2.
```

---

# 11. Pattern Detector

If you see:

```text
many prime checks
```

think:

```text
sieve
```

If you see:

```text
factorize many numbers
```

think:

```text
SPF
```

If you see:

```text
count primes <= x for many queries
```

think:

```text
sieve + prefix count
```

If you see:

```text
count divisors / sum divisors
```

think:

```text
SPF factorization
```

---

# 12. C++ Code 1 — Basic Sieve

```cpp
#include <bits/stdc++.h>
using namespace std;

// ---------------------------------------------------
// Basic Sieve of Eratosthenes
//
// Returns:
// prime[x] = true if x is prime
//
// Time:
// O(N log log N)
// ---------------------------------------------------
vector<bool> buildSieve(int n) {

    // Step 1:
    // Initially assume every number is prime
    vector<bool> prime(n + 1, true);

    // Step 2:
    // 0 and 1 are not prime
    if (n >= 0) prime[0] = false;
    if (n >= 1) prime[1] = false;

    // Step 3:
    // Start from 2 and mark multiples
    for (long long p = 2; p * p <= n; p++) {

        // If p is still prime
        if (prime[p]) {

            // Step 4:
            // Mark multiples from p*p
            for (long long multiple = p * p; multiple <= n; multiple += p) {
                prime[multiple] = false;
            }
        }
    }

    return prime;
}

int main() {

    int n = 30;

    vector<bool> prime = buildSieve(n);

    for (int i = 2; i <= n; i++) {

        if (prime[i]) {
            cout << i << " ";
        }
    }

    return 0;
}
```

---

# 13. C++ Code 2 — Prime Query Engine

```cpp
#include <bits/stdc++.h>
using namespace std;

class PrimeQueryEngine {

private:
    vector<bool> prime;

public:
    PrimeQueryEngine(int maxN) {

        prime.assign(maxN + 1, true);

        if (maxN >= 0) prime[0] = false;
        if (maxN >= 1) prime[1] = false;

        for (long long p = 2; p * p <= maxN; p++) {

            if (prime[p]) {

                for (long long multiple = p * p; multiple <= maxN; multiple += p) {
                    prime[multiple] = false;
                }
            }
        }
    }

    // O(1) query
    bool isPrime(int x) {

        if (x < 0 || x >= prime.size())
            return false;

        return prime[x];
    }
};

int main() {

    PrimeQueryEngine engine(100);

    cout << engine.isPrime(17) << "\n";
    cout << engine.isPrime(25) << "\n";

    return 0;
}
```

---

# 14. C++ Code 3 — Count Primes Prefix

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<bool> buildSieve(int n) {

    vector<bool> prime(n + 1, true);

    if (n >= 0) prime[0] = false;
    if (n >= 1) prime[1] = false;

    for (long long p = 2; p * p <= n; p++) {

        if (prime[p]) {

            for (long long multiple = p * p; multiple <= n; multiple += p) {
                prime[multiple] = false;
            }
        }
    }

    return prime;
}

// ---------------------------------------------------
// prefixPrimeCount[x] = number of primes <= x
// ---------------------------------------------------
vector<int> buildPrimePrefix(vector<bool>& prime) {

    int n = prime.size() - 1;

    vector<int> prefix(n + 1, 0);

    for (int i = 1; i <= n; i++) {

        prefix[i] = prefix[i - 1] + (prime[i] ? 1 : 0);
    }

    return prefix;
}

int main() {

    int n = 100;

    vector<bool> prime = buildSieve(n);
    vector<int> prefix = buildPrimePrefix(prime);

    // Number of primes <= 30
    cout << prefix[30] << "\n";

    // Number of primes in [10, 30]
    cout << prefix[30] - prefix[9] << "\n";

    return 0;
}
```

---

# 15. C++ Code 4 — SPF Precomputation

```cpp
#include <bits/stdc++.h>
using namespace std;

// ---------------------------------------------------
// Smallest Prime Factor precomputation
//
// spf[x] = smallest prime factor of x
// ---------------------------------------------------
vector<int> buildSPF(int n) {

    vector<int> spf(n + 1);

    // Step 1:
    // Initially every number points to itself
    for (int i = 0; i <= n; i++) {
        spf[i] = i;
    }

    // Step 2:
    // 0 and 1 special cases
    if (n >= 0) spf[0] = 0;
    if (n >= 1) spf[1] = 1;

    // Step 3:
    // Mark smallest prime factor
    for (long long p = 2; p * p <= n; p++) {

        // If spf[p] == p, p is prime
        if (spf[p] == p) {

            // Start from p*p
            for (long long multiple = p * p; multiple <= n; multiple += p) {

                // Only update if not already marked
                if (spf[multiple] == multiple) {
                    spf[multiple] = p;
                }
            }
        }
    }

    return spf;
}

int main() {

    vector<int> spf = buildSPF(100);

    cout << spf[60] << "\n"; // 2
    cout << spf[49] << "\n"; // 7

    return 0;
}
```

---

# 16. C++ Code 5 — Factorization Using SPF

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> buildSPF(int n) {

    vector<int> spf(n + 1);

    for (int i = 0; i <= n; i++) {
        spf[i] = i;
    }

    if (n >= 0) spf[0] = 0;
    if (n >= 1) spf[1] = 1;

    for (long long p = 2; p * p <= n; p++) {

        if (spf[p] == p) {

            for (long long multiple = p * p; multiple <= n; multiple += p) {

                if (spf[multiple] == multiple) {
                    spf[multiple] = p;
                }
            }
        }
    }

    return spf;
}

// ---------------------------------------------------
// Factorize x using SPF
//
// Example:
// 60 -> 2^2 3^1 5^1
// ---------------------------------------------------
vector<pair<int,int>> factorizeUsingSPF(int x, vector<int>& spf) {

    vector<pair<int,int>> result;

    while (x > 1) {

        int prime = spf[x];
        int count = 0;

        while (x % prime == 0) {
            x /= prime;
            count++;
        }

        result.push_back({prime, count});
    }

    return result;
}

int main() {

    int maxN = 100;
    vector<int> spf = buildSPF(maxN);

    int x = 60;

    auto factors = factorizeUsingSPF(x, spf);

    for (auto [prime, power] : factors) {
        cout << prime << "^" << power << " ";
    }

    return 0;
}
```

---

# 17. C++ Code 6 — Count Distinct Prime Factors

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> buildSPF(int n) {

    vector<int> spf(n + 1);

    for (int i = 0; i <= n; i++) {
        spf[i] = i;
    }

    for (long long p = 2; p * p <= n; p++) {

        if (spf[p] == p) {

            for (long long multiple = p * p; multiple <= n; multiple += p) {

                if (spf[multiple] == multiple) {
                    spf[multiple] = p;
                }
            }
        }
    }

    return spf;
}

int countDistinctPrimeFactors(int x, vector<int>& spf) {

    int count = 0;

    while (x > 1) {

        int prime = spf[x];

        count++;

        while (x % prime == 0) {
            x /= prime;
        }
    }

    return count;
}

int main() {

    vector<int> spf = buildSPF(100);

    cout << countDistinctPrimeFactors(60, spf) << "\n";

    return 0;
}
```

---

# 18. Dry Run Table

Sieve for `N = 20`.

| p | prime[p]? | multiples marked |
|---:|---|---|
| 2 | yes | 4,6,8,10,12,14,16,18,20 |
| 3 | yes | 9,12,15,18 |
| 4 | no | skip |
| 5 | p*p > 20 | stop |

Primes left:

```text
2 3 5 7 11 13 17 19
```

---

# 19. Java Logic Comments

```text
function sieve(n):
    prime = boolean array of size n+1
    fill true

    prime[0] = false
    prime[1] = false

    for p from 2 while p*p <= n:
        if prime[p] is true:
            for multiple from p*p to n step p:
                prime[multiple] = false

    return prime
```

SPF:

```text
function buildSPF(n):
    spf[i] = i

    for p from 2 while p*p <= n:
        if spf[p] == p:
            for multiple from p*p to n step p:
                if spf[multiple] == multiple:
                    spf[multiple] = p
```

---

# 20. Common Mistakes

## Mistake 1 — Forgetting 0 And 1

```cpp
prime[0] = false;
prime[1] = false;
```

---

## Mistake 2 — Starting From 2*p Instead Of p*p

It still works, but slower.

Better:

```cpp
p*p
```

---

## Mistake 3 — Integer Overflow In p*p

Use:

```cpp
long long p
```

inside loop.

---

## Mistake 4 — Using Sieve For Huge N Without Memory Check

```text
N = 1e9
```

cannot use normal sieve due to memory.

Need segmented sieve.

---

## Mistake 5 — SPF Out Of Range

SPF only works for numbers:

```text
<= max precomputed N
```

---

# 21. CP / FAANG Problem Forms

This section includes:

```text
Problem
→ Pattern
→ Step-by-step working
→ C++ code
```

---

## Form 1 — Many Prime Queries

### Problem

Given many queries:

```text
17, 25, 97, 100
```

Tell whether each is prime.

---

### Pattern

Use:

```text
Sieve precompute once
O(1) per query
```

---

### Step-by-Step Working

Precompute primes up to:

```text
100
```

Then answer:

```text
17 -> prime
25 -> not prime
97 -> prime
100 -> not prime
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<bool> sieve(int n) {

    vector<bool> prime(n + 1, true);

    prime[0] = false;
    prime[1] = false;

    for (long long p = 2; p * p <= n; p++) {

        if (prime[p]) {

            for (long long multiple = p * p; multiple <= n; multiple += p) {
                prime[multiple] = false;
            }
        }
    }

    return prime;
}

int main() {

    vector<bool> prime = sieve(100);

    vector<int> queries = {17, 25, 97, 100};

    for (int q : queries) {
        cout << q << " -> "
             << (prime[q] ? "Prime" : "Not Prime")
             << "\n";
    }

    return 0;
}
```

---

## Form 2 — Count Primes Up To N

### Problem

Count primes:

```text
<= 30
```

---

### Pattern

Use:

```text
sieve + count
```

---

### Step-by-Step Working

Primes up to 30:

```text
2 3 5 7 11 13 17 19 23 29
```

Count:

```text
10
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<bool> sieve(int n) {

    vector<bool> prime(n + 1, true);

    prime[0] = false;
    prime[1] = false;

    for (long long p = 2; p * p <= n; p++) {

        if (prime[p]) {

            for (long long multiple = p * p; multiple <= n; multiple += p) {
                prime[multiple] = false;
            }
        }
    }

    return prime;
}

int main() {

    int n = 30;

    vector<bool> prime = sieve(n);

    int count = 0;

    for (int i = 2; i <= n; i++) {

        if (prime[i])
            count++;
    }

    cout << count << "\n";

    return 0;
}
```

---

## Form 3 — Count Primes In Range

### Problem

Count primes in:

```text
[10, 30]
```

---

### Pattern

Use:

```text
sieve + prefix count
```

---

### Step-by-Step Working

Primes in range:

```text
11 13 17 19 23 29
```

Count:

```text
6
```

Use:

```cpp
prefix[30] - prefix[9]
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<bool> sieve(int n) {

    vector<bool> prime(n + 1, true);

    prime[0] = false;
    prime[1] = false;

    for (long long p = 2; p * p <= n; p++) {

        if (prime[p]) {

            for (long long multiple = p * p; multiple <= n; multiple += p) {
                prime[multiple] = false;
            }
        }
    }

    return prime;
}

int main() {

    int n = 100;

    vector<bool> prime = sieve(n);

    vector<int> prefix(n + 1, 0);

    for (int i = 1; i <= n; i++) {
        prefix[i] = prefix[i - 1] + (prime[i] ? 1 : 0);
    }

    int L = 10;
    int R = 30;

    cout << prefix[R] - prefix[L - 1] << "\n";

    return 0;
}
```

---

## Form 4 — Fast Factorization

### Problem

Factorize many numbers like:

```text
60, 84, 97
```

---

### Pattern

Use:

```text
SPF precompute
```

---

### Step-by-Step Working

For 60:

```text
spf[60]=2 -> 60/2=30
spf[30]=2 -> 30/2=15
spf[15]=3 -> 15/3=5
spf[5]=5  -> 5/5=1
```

Factors:

```text
2^2 3^1 5^1
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> buildSPF(int n) {

    vector<int> spf(n + 1);

    for (int i = 0; i <= n; i++) {
        spf[i] = i;
    }

    for (long long p = 2; p * p <= n; p++) {

        if (spf[p] == p) {

            for (long long multiple = p * p; multiple <= n; multiple += p) {

                if (spf[multiple] == multiple) {
                    spf[multiple] = p;
                }
            }
        }
    }

    return spf;
}

vector<pair<int,int>> factorize(int x, vector<int>& spf) {

    vector<pair<int,int>> factors;

    while (x > 1) {

        int p = spf[x];
        int count = 0;

        while (x % p == 0) {
            x /= p;
            count++;
        }

        factors.push_back({p, count});
    }

    return factors;
}

int main() {

    vector<int> spf = buildSPF(100);

    auto factors = factorize(60, spf);

    for (auto [p, count] : factors) {
        cout << p << "^" << count << " ";
    }

    return 0;
}
```

---

## Form 5 — Count Distinct Prime Factors

### Problem

Count distinct prime factors of:

```cpp
60
```

---

### Pattern

Use:

```text
SPF factorization
```

---

### Step-by-Step Working

```cpp
60 = 2^2 * 3^1 * 5^1
```

Distinct primes:

```text
2, 3, 5
```

Count:

```text
3
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> buildSPF(int n) {

    vector<int> spf(n + 1);

    for (int i = 0; i <= n; i++) {
        spf[i] = i;
    }

    for (long long p = 2; p * p <= n; p++) {

        if (spf[p] == p) {

            for (long long multiple = p * p; multiple <= n; multiple += p) {

                if (spf[multiple] == multiple) {
                    spf[multiple] = p;
                }
            }
        }
    }

    return spf;
}

int countDistinct(int x, vector<int>& spf) {

    int count = 0;

    while (x > 1) {

        int p = spf[x];

        count++;

        while (x % p == 0) {
            x /= p;
        }
    }

    return count;
}

int main() {

    vector<int> spf = buildSPF(100);

    cout << countDistinct(60, spf) << "\n";

    return 0;
}
```

---

# 22. Real World Mapping

## Precomputation Engine

Sieve teaches:

```text
do work once
answer many queries fast
```

This maps to:

- cache warming
- indexing
- materialized views
- lookup tables

---

## Search / Database Indexing

SPF is like storing metadata for fast future queries.

---

## Cryptography

Prime generation and primality are foundational.

---

## Hashing

Prime moduli are common in rolling hash and modular hashing.

---

## Analytics Systems

Prefix prime counts mirror prefix aggregation / precomputed analytics.

---

# 23. Practice Problems

Easy:

```text
1. Sieve primes up to N
2. Answer prime queries
3. Count primes <= N
4. Count primes in [L,R]
```

Medium:

```text
5. SPF factorization
6. Count distinct prime factors
7. Count total prime factors
8. Factorize many numbers
```

Hard later:

```text
9. Segmented sieve
10. Linear sieve
11. Euler Phi using SPF
12. Mobius function
```

---

# 24. Final Mental Summary

Prime checking one number:

```text
sqrt check
```

Many prime checks:

```text
sieve
```

Many factorization queries:

```text
SPF
```

Core idea:

```text
Precompute number structure once.
Use it many times.
```

---

# 25. Next Step

```text
007_Prime_Factorization.md
```

Next intuition:

```text
Break every number into prime powers.
```
