# 007_Prime_Factorization.md

# MiniMathEngine — Prime Factorization

> Goal: build deep intuition for:
>
> ```text
> breaking numbers into prime powers
> ```
>
> This is one of the MOST important concepts in:
>
> - Number Theory
> - Divisor Problems
> - Euler Phi
> - Combinatorics
> - Modular Arithmetic
> - Cryptography
> - CP Math Problems

---

# Clickable Index

- [1. Why This Topic Matters](#1-why-this-topic-matters)
- [2. What Is Prime Factorization](#2-what-is-prime-factorization)
- [3. Fundamental Theorem Of Arithmetic](#3-fundamental-theorem-of-arithmetic)
- [4. Core Mental Model](#4-core-mental-model)
- [5. Why Prime Factors Matter](#5-why-prime-factors-matter)
- [6. Trial Division Intuition](#6-trial-division-intuition)
- [7. Why We Check Till sqrt(N)](#7-why-we-check-till-sqrtn)
- [8. Step-by-Step Working — Factorize 60](#8-step-by-step-working--factorize-60)
- [9. Step-by-Step Working — Factorize 84](#9-step-by-step-working--factorize-84)
- [10. Prime Power Representation](#10-prime-power-representation)
- [11. Pattern Detector](#11-pattern-detector)
- [12. C++ Code 1 — Basic Prime Factorization](#12-c-code-1--basic-prime-factorization)
- [13. C++ Code 2 — Prime Factorization Using SPF](#13-c-code-2--prime-factorization-using-spf)
- [14. C++ Code 3 — Count Total Prime Factors](#14-c-code-3--count-total-prime-factors)
- [15. C++ Code 4 — Count Distinct Prime Factors](#15-c-code-4--count-distinct-prime-factors)
- [16. C++ Code 5 — Build Divisors Using Factors](#16-c-code-5--build-divisors-using-factors)
- [17. C++ Code 6 — Check Perfect Square Using Factors](#17-c-code-6--check-perfect-square-using-factors)
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

Prime factorization converts:

```text
a big number
```

into:

```text
small prime building blocks
```

Example:

```cpp
60 = 2^2 * 3^1 * 5^1
```

Once numbers become prime powers:

many problems become easier.

This is foundation for:

- divisor count
- sum of divisors
- Euler Phi
- LCM/GCD formulas
- modular arithmetic
- combinatorics

---

# 2. What Is Prime Factorization

Prime factorization means:

```text
expressing a number as product of primes
```

Example:

```cpp
84
```

Factorization:

```cpp
84 = 2^2 * 3^1 * 7^1
```

Prime numbers only.

---

# 3. Fundamental Theorem Of Arithmetic

Very important theorem:

```text
Every integer > 1
has a UNIQUE prime factorization.
```

Example:

```cpp
60 = 2^2 * 3 * 5
```

No other prime representation exists.

This uniqueness is the backbone of number theory.

---

# 4. Core Mental Model

Think:

```text
Prime numbers are atoms.
Composite numbers are molecules.
```

Prime factorization breaks a molecule into atoms.

Example:

```cpp
360
```

becomes:

```cpp
2^3 * 3^2 * 5
```

Now many mathematical properties become visible.

---

# 5. Why Prime Factors Matter

Once you know prime factors:

you can derive:

```text
divisor count
sum of divisors
gcd
lcm
perfect square
perfect cube
coprime structure
```

Example:

```cpp
72 = 2^3 * 3^2
```

Divisor count formula:

```cpp
(3+1)*(2+1)=12
```

Very powerful.

---

# 6. Trial Division Intuition

Basic idea:

```text
Keep dividing by smallest possible divisor.
```

Example:

```cpp
60
```

Check:

```text
2 divides 60
2 divides 30
3 divides 15
5 divides 5
```

Factors:

```text
2 2 3 5
```

---

# 7. Why We Check Till sqrt(N)

Suppose after removing small factors:

```cpp
n > 1
```

Then remaining `n` must be prime.

Why?

Because if it had a factor:

```text
<= sqrt(n)
```

it would already have been removed.

So factorization loop only needs:

```cpp
p*p <= n
```

Very important optimization.

---

# 8. Step-by-Step Working — Factorize 60

Start:

```cpp
n = 60
```

Check `2`:

```text
60 % 2 == 0
60 -> 30
30 % 2 == 0
30 -> 15
```

Count of 2:

```cpp
2^2
```

Check `3`:

```text
15 % 3 == 0
15 -> 5
```

Count of 3:

```cpp
3^1
```

Remaining:

```cpp
5
```

Prime.

Final:

```cpp
60 = 2^2 * 3^1 * 5^1
```

---

# 9. Step-by-Step Working — Factorize 84

Start:

```cpp
84
```

Check `2`:

```text
84 -> 42 -> 21
```

Count:

```cpp
2^2
```

Check `3`:

```text
21 -> 7
```

Count:

```cpp
3^1
```

Remaining:

```cpp
7
```

Prime.

Final:

```cpp
84 = 2^2 * 3^1 * 7^1
```

---

# 10. Prime Power Representation

Best representation:

```cpp
n = p1^a1 * p2^a2 * ...
```

Example:

```cpp
360 = 2^3 * 3^2 * 5^1
```

This representation helps derive:

| Property | Formula |
|---|---|
| divisor count | (a1+1)(a2+1)... |
| sum divisors | geometric series |
| perfect square | all exponents even |
| gcd | min exponents |
| lcm | max exponents |

Very important intuition.

---

# 11. Pattern Detector

If you see:

```text
divisor count
```

think:

```text
prime factorization
```

If you see:

```text
sum of divisors
```

think:

```text
prime powers
```

If you see:

```text
perfect square
```

think:

```text
all exponents even
```

If you see:

```text
many factorization queries
```

think:

```text
SPF
```

---

# 12. C++ Code 1 — Basic Prime Factorization

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Prime factorization using trial division
//
// Time:
// O(sqrt(N))
// ---------------------------------------------------
vector<pair<ll,int>> factorize(ll n) {

    vector<pair<ll,int>> factors;

    // Step 1:
    // Try every divisor from 2 onwards
    for (ll p = 2; p * p <= n; p++) {

        // If p divides n
        if (n % p == 0) {

            int count = 0;

            // Remove all copies of p
            while (n % p == 0) {

                n /= p;
                count++;
            }

            factors.push_back({p, count});
        }
    }

    // Remaining number is prime
    if (n > 1) {
        factors.push_back({n, 1});
    }

    return factors;
}

int main() {

    ll n = 60;

    auto factors = factorize(n);

    for (auto [prime, power] : factors) {

        cout << prime
             << "^"
             << power
             << " ";
    }

    return 0;
}
```

---

# 13. C++ Code 2 — Prime Factorization Using SPF

```cpp
#include <bits/stdc++.h>
using namespace std;

// ---------------------------------------------------
// Build SPF array
// ---------------------------------------------------
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

// ---------------------------------------------------
// Factorize using SPF
//
// Much faster for many queries
// ---------------------------------------------------
vector<pair<int,int>> factorizeSPF(int x, vector<int>& spf) {

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

    vector<int> spf = buildSPF(100);

    auto factors = factorizeSPF(84, spf);

    for (auto [prime, power] : factors) {

        cout << prime
             << "^"
             << power
             << " ";
    }

    return 0;
}
```

---

# 14. C++ Code 3 — Count Total Prime Factors

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int countTotalPrimeFactors(ll n) {

    int total = 0;

    for (ll p = 2; p * p <= n; p++) {

        while (n % p == 0) {

            n /= p;
            total++;
        }
    }

    if (n > 1)
        total++;

    return total;
}

int main() {

    // 60 = 2 * 2 * 3 * 5
    cout << countTotalPrimeFactors(60) << "\n";

    return 0;
}
```

Output:

```text
4
```

---

# 15. C++ Code 4 — Count Distinct Prime Factors

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int countDistinctPrimeFactors(ll n) {

    int distinct = 0;

    for (ll p = 2; p * p <= n; p++) {

        if (n % p == 0) {

            distinct++;

            while (n % p == 0) {
                n /= p;
            }
        }
    }

    if (n > 1)
        distinct++;

    return distinct;
}

int main() {

    // 60 = 2^2 * 3 * 5
    // distinct primes = 2,3,5
    cout << countDistinctPrimeFactors(60) << "\n";

    return 0;
}
```

Output:

```text
3
```

---

# 16. C++ Code 5 — Build Divisors Using Factors

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

vector<pair<ll,int>> factorize(ll n) {

    vector<pair<ll,int>> factors;

    for (ll p = 2; p * p <= n; p++) {

        if (n % p == 0) {

            int count = 0;

            while (n % p == 0) {

                n /= p;
                count++;
            }

            factors.push_back({p, count});
        }
    }

    if (n > 1) {
        factors.push_back({n, 1});
    }

    return factors;
}

// ---------------------------------------------------
// Generate all divisors recursively
// ---------------------------------------------------
void generateDivisors(
    int index,
    ll current,
    vector<pair<ll,int>>& factors,
    vector<ll>& divisors
) {

    // Base case
    if (index == factors.size()) {

        divisors.push_back(current);
        return;
    }

    auto [prime, power] = factors[index];

    ll value = 1;

    for (int p = 0; p <= power; p++) {

        generateDivisors(
            index + 1,
            current * value,
            factors,
            divisors
        );

        value *= prime;
    }
}

int main() {

    ll n = 60;

    auto factors = factorize(n);

    vector<ll> divisors;

    generateDivisors(0, 1, factors, divisors);

    sort(divisors.begin(), divisors.end());

    for (ll d : divisors) {
        cout << d << " ";
    }

    return 0;
}
```

---

# 17. C++ Code 6 — Check Perfect Square Using Factors

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// A number is perfect square
// if ALL prime exponents are even
// ---------------------------------------------------
bool isPerfectSquareByFactors(ll n) {

    for (ll p = 2; p * p <= n; p++) {

        int count = 0;

        while (n % p == 0) {

            n /= p;
            count++;
        }

        // Odd exponent
        if (count % 2 == 1) {
            return false;
        }
    }

    // Remaining prime has exponent 1
    if (n > 1)
        return false;

    return true;
}

int main() {

    cout << isPerfectSquareByFactors(36) << "\n";
    cout << isPerfectSquareByFactors(72) << "\n";

    return 0;
}
```

---

# 18. Dry Run Table

Factorize:

```cpp
60
```

| divisor p | n before | divisions | n after | factor |
|---:|---:|---|---:|---|
| 2 | 60 | 60→30→15 | 15 | 2^2 |
| 3 | 15 | 15→5 | 5 | 3^1 |
| remaining | 5 | prime | 1 | 5^1 |

Final:

```cpp
60 = 2^2 * 3^1 * 5^1
```

---

# 19. Java Logic Comments

```text
function factorize(n):

    for p from 2 while p*p <= n:

        if n % p == 0:

            count = 0

            while n % p == 0:
                n /= p
                count++

            save (p, count)

    if n > 1:
        save (n,1)
```

Mental model:

```text
Keep removing smallest prime blocks.
```

---

# 20. Common Mistakes

## Mistake 1 — Forgetting Remaining Prime

After loop:

```cpp
if (n > 1)
```

remaining number itself is prime.

Very important.

---

## Mistake 2 — Looping Till Original N

Wrong:

```cpp
for (p=2; p<=original_n; p++)
```

Correct:

```cpp
p*p <= n
```

---

## Mistake 3 — Forgetting Repeated Division

Wrong:

```cpp
if (n % p == 0)
    n /= p;
```

Correct:

```cpp
while (n % p == 0)
    n /= p;
```

---

## Mistake 4 — Confusing Distinct And Total Prime Factors

Example:

```cpp
60 = 2^2 * 3 * 5
```

Distinct:

```text
3
```

Total:

```text
4
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

## Form 1 — Prime Factorization

### Problem

Factorize:

```cpp
360
```

---

### Pattern

Repeated trial division.

---

### Step-by-Step Working

```text
360 -> divide by 2:
360→180→90→45

count of 2 = 3

45 -> divide by 3:
45→15→5

count of 3 = 2

remaining 5 is prime
```

Final:

```cpp
360 = 2^3 * 3^2 * 5^1
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

vector<pair<ll,int>> factorize(ll n) {

    vector<pair<ll,int>> result;

    for (ll p = 2; p * p <= n; p++) {

        if (n % p == 0) {

            int count = 0;

            while (n % p == 0) {

                n /= p;
                count++;
            }

            result.push_back({p, count});
        }
    }

    if (n > 1)
        result.push_back({n,1});

    return result;
}

int main() {

    auto factors = factorize(360);

    for (auto [prime, power] : factors) {

        cout << prime
             << "^"
             << power
             << " ";
    }

    return 0;
}
```

---

## Form 2 — Count Divisors

### Problem

Find number of divisors of:

```cpp
72
```

---

### Pattern

Use prime exponents.

---

### Step-by-Step Working

```cpp
72 = 2^3 * 3^2
```

Formula:

```cpp
(3+1)*(2+1)=12
```

Answer:

```text
12 divisors
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int divisorCount(ll n) {

    int answer = 1;

    for (ll p = 2; p * p <= n; p++) {

        if (n % p == 0) {

            int count = 0;

            while (n % p == 0) {

                n /= p;
                count++;
            }

            answer *= (count + 1);
        }
    }

    if (n > 1)
        answer *= 2;

    return answer;
}

int main() {

    cout << divisorCount(72) << "\n";

    return 0;
}
```

---

## Form 3 — Perfect Square Check

### Problem

Check whether:

```cpp
144
```

is perfect square.

---

### Pattern

All prime exponents must be even.

---

### Step-by-Step Working

```cpp
144 = 2^4 * 3^2
```

All exponents even.

Therefore:

```text
perfect square
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

bool perfectSquare(ll n) {

    for (ll p = 2; p * p <= n; p++) {

        int count = 0;

        while (n % p == 0) {

            n /= p;
            count++;
        }

        if (count % 2 == 1)
            return false;
    }

    if (n > 1)
        return false;

    return true;
}

int main() {

    cout << perfectSquare(144) << "\n";

    return 0;
}
```

---

## Form 4 — GCD Using Prime Powers

### Problem

Find:

```cpp
gcd(72,120)
```

---

### Pattern

Take minimum exponent of common primes.

---

### Step-by-Step Working

```cpp
72  = 2^3 * 3^2
120 = 2^3 * 3^1 * 5
```

Minimum exponents:

```cpp
2^3 * 3^1
```

Answer:

```cpp
24
```

---

## Form 5 — LCM Using Prime Powers

### Problem

Find:

```cpp
lcm(72,120)
```

---

### Pattern

Take maximum exponent.

---

### Step-by-Step Working

```cpp
72  = 2^3 * 3^2
120 = 2^3 * 3^1 * 5^1
```

Maximum exponents:

```cpp
2^3 * 3^2 * 5^1
```

Answer:

```cpp
360
```

---

# 22. Real World Mapping

## Cryptography

Prime factorization difficulty is core to RSA security.

---

## Compression / Encoding

Factorization concepts appear in transforms and number encoding.

---

## Distributed Systems

Hashing and modular arithmetic rely on prime structure.

---

## Search / Analytics

Divisor-like decomposition patterns appear in indexing and partitioning.

---

# 23. Practice Problems

Easy:

```text
1. Prime factorization
2. Count divisors
3. Sum divisors
4. Distinct prime factors
```

Medium:

```text
5. SPF factorization
6. Perfect square / cube
7. GCD via prime powers
8. LCM via prime powers
```

Hard:

```text
9. Mobius
10. Euler Phi
11. Pollard Rho
12. Miller Rabin
```

---

# 24. Final Mental Summary

Prime factorization means:

```text
breaking numbers into prime atoms
```

Everything becomes easier in prime-power form.

Key representation:

```cpp
n = p1^a1 * p2^a2 * ...
```

This unlocks:

- divisor formulas
- gcd/lcm formulas
- perfect square checks
- Euler Phi
- combinatorics

---

# 25. Next Step

```text
008_Divisors_Count_Sum.md
```

Next intuition:

```text
How prime exponents generate all divisors.
```
