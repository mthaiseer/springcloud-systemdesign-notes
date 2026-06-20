# 005_Prime_Checking.md

# MiniMathEngine — Prime Checking

> Goal: build bottom-up intuition for:
>
> ```text
> prime numbers
> factors
> divisibility
> sqrt optimization
> ```
>
> Prime checking is the bridge from:
>
> ```text
> GCD / LCM
> → Prime Numbers
> → Sieve
> → SPF Factorization
> → Euler Phi
> → Combinatorics under MOD
> ```

---

# Clickable Index

- [1. Why This Topic Matters](#1-why-this-topic-matters)
- [2. What Is A Prime Number](#2-what-is-a-prime-number)
- [3. What Is A Composite Number](#3-what-is-a-composite-number)
- [4. Core Mental Model](#4-core-mental-model)
- [5. Factor Pair Intuition](#5-factor-pair-intuition)
- [6. Naive Prime Checking](#6-naive-prime-checking)
- [7. Square Root Optimization](#7-square-root-optimization)
- [8. Why sqrt(N) Works](#8-why-sqrtn-works)
- [9. Step-by-Step Working — isPrime(29)](#9-step-by-step-working--isprime29)
- [10. Step-by-Step Working — isPrime(36)](#10-step-by-step-working--isprime36)
- [11. Pattern Detector](#11-pattern-detector)
- [12. C++ Code 1 — Basic Prime Check](#12-c-code-1--basic-prime-check)
- [13. C++ Code 2 — Optimized Prime Check](#13-c-code-2--optimized-prime-check)
- [14. C++ Code 3 — Count Primes In Range](#14-c-code-3--count-primes-in-range)
- [15. C++ Code 4 — Print Divisors](#15-c-code-4--print-divisors)
- [16. C++ Code 5 — Prime Factorization Preview](#16-c-code-5--prime-factorization-preview)
- [17. Dry Run Table](#17-dry-run-table)
- [18. Java Logic Comments](#18-java-logic-comments)
- [19. Common Mistakes](#19-common-mistakes)
- [20. CP / FAANG Problem Forms](#20-cp--faang-problem-forms)
- [21. Real World Mapping](#21-real-world-mapping)
- [22. Practice Problems](#22-practice-problems)
- [23. Final Mental Summary](#23-final-mental-summary)
- [24. Next Step](#24-next-step)

---

# 1. Why This Topic Matters

Prime numbers are the atoms of integers.

Every positive integer greater than `1` is either:

```text
prime
```

or can be broken into:

```text
product of primes
```

Example:

```cpp
60 = 2 * 2 * 3 * 5
```

This is why prime checking is foundation for:

- sieve
- factorization
- divisors
- Euler Phi
- modular inverse
- cryptography
- hashing
- number theory problems

---

# 2. What Is A Prime Number

A prime number is:

```text
a number greater than 1
with exactly two positive divisors
```

Those divisors are:

```text
1 and itself
```

Examples:

```cpp
2, 3, 5, 7, 11, 13, 17
```

Special note:

```cpp
2
```

is the only even prime.

---

# 3. What Is A Composite Number

A composite number is:

```text
a number greater than 1
with more than two divisors
```

Examples:

```cpp
4, 6, 8, 9, 10, 12
```

Example:

```cpp
12
```

Divisors:

```text
1, 2, 3, 4, 6, 12
```

More than 2 divisors, so composite.

---

# 4. Core Mental Model

Prime checking asks:

```text
Does n have any divisor other than 1 and itself?
```

If yes:

```text
not prime
```

If no:

```text
prime
```

The optimization comes from factor pairs.

---

# 5. Factor Pair Intuition

Factors come in pairs.

Example:

```cpp
36
```

Factor pairs:

```text
1 * 36
2 * 18
3 * 12
4 * 9
6 * 6
```

After `6`, pairs start reversing.

Since:

```cpp
sqrt(36)=6
```

we only need to check up to sqrt.

---

# 6. Naive Prime Checking

Naive approach:

```text
Check every divisor from 2 to n-1.
```

Example:

```cpp
isPrime(29)
```

Check:

```text
29%2
29%3
29%4
...
29%28
```

Time complexity:

```text
O(N)
```

Too slow for large numbers.

---

# 7. Square Root Optimization

Better approach:

```text
Check only from 2 to sqrt(n)
```

Because if `n` has a factor larger than sqrt, it must also have a matching factor smaller than sqrt.

Time complexity:

```text
O(sqrt(N))
```

This is enough for single-number primality checks in many interview problems.

---

# 8. Why sqrt(N) Works

Suppose:

```cpp
n = a * b
```

If both:

```cpp
a > sqrt(n)
b > sqrt(n)
```

then:

```cpp
a * b > n
```

Impossible.

So at least one factor must be:

```cpp
<= sqrt(n)
```

Therefore, if no divisor exists from:

```cpp
2 to sqrt(n)
```

then the number is prime.

---

# 9. Step-by-Step Working — isPrime(29)

Check:

```cpp
n = 29
```

sqrt(29):

```text
around 5
```

Check divisors:

```text
29 % 2 != 0
29 % 3 != 0
29 % 4 != 0
29 % 5 != 0
```

No divisor found.

Answer:

```text
prime
```

---

# 10. Step-by-Step Working — isPrime(36)

Check:

```cpp
n = 36
```

sqrt(36):

```text
6
```

Check:

```text
36 % 2 == 0
```

Divisor found immediately.

Answer:

```text
not prime
```

No need to check further.

---

# 11. Pattern Detector

If you see:

```text
Check if number is prime
```

use:

```text
sqrt prime check
```

If you see:

```text
many prime queries up to N
```

use:

```text
sieve
```

If you see:

```text
factorization for many numbers
```

use:

```text
SPF sieve
```

If you see:

```text
large n around 1e12 and few queries
```

use:

```text
sqrt prime check
```

If you see:

```text
n up to 1e7 and many queries
```

use:

```text
sieve
```

---

# 12. C++ Code 1 — Basic Prime Check

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Basic prime check
//
// Time Complexity:
// O(N)
//
// This is only for learning.
// ---------------------------------------------------
bool isPrimeBasic(ll n) {

    // 0 and 1 are not prime
    if (n < 2)
        return false;

    // Check all numbers from 2 to n-1
    for (ll d = 2; d <= n - 1; d++) {

        // If d divides n, n is not prime
        if (n % d == 0) {
            return false;
        }
    }

    return true;
}

int main() {

    ll n = 29;

    cout << (isPrimeBasic(n) ? "Prime" : "Not Prime") << "\n";

    return 0;
}
```

---

# 13. C++ Code 2 — Optimized Prime Check

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Optimized prime check using sqrt(n)
//
// Instead of:
// d <= sqrt(n)
//
// We use:
// d*d <= n
//
// Time Complexity:
// O(sqrt(n))
// ---------------------------------------------------
bool isPrime(ll n) {

    // 0 and 1 are not prime
    if (n < 2)
        return false;

    // 2 is prime
    if (n == 2)
        return true;

    // Even numbers greater than 2 are not prime
    if (n % 2 == 0)
        return false;

    // Check only odd divisors
    for (ll d = 3; d * d <= n; d += 2) {

        if (n % d == 0) {
            return false;
        }
    }

    return true;
}

int main() {

    vector<ll> nums = {1, 2, 3, 4, 29, 36, 97};

    for (ll n : nums) {

        cout << n << " -> "
             << (isPrime(n) ? "Prime" : "Not Prime")
             << "\n";
    }

    return 0;
}
```

---

# 14. C++ Code 3 — Count Primes In Range

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

bool isPrime(ll n) {

    if (n < 2)
        return false;

    if (n == 2)
        return true;

    if (n % 2 == 0)
        return false;

    for (ll d = 3; d * d <= n; d += 2) {

        if (n % d == 0)
            return false;
    }

    return true;
}

// ---------------------------------------------------
// Count primes from L to R
//
// Good for small ranges.
// For huge ranges/many queries,
// use sieve later.
// ---------------------------------------------------
int countPrimesInRange(ll L, ll R) {

    int count = 0;

    for (ll x = L; x <= R; x++) {

        if (isPrime(x)) {
            count++;
        }
    }

    return count;
}

int main() {

    cout << countPrimesInRange(1, 20) << "\n";

    return 0;
}
```

Expected primes:

```text
2, 3, 5, 7, 11, 13, 17, 19
```

Count:

```text
8
```

---

# 15. C++ Code 4 — Print Divisors

This helps understand factor pairs.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Print divisors using sqrt(n)
// ---------------------------------------------------
void printDivisors(ll n) {

    vector<ll> small;
    vector<ll> large;

    for (ll d = 1; d * d <= n; d++) {

        if (n % d == 0) {

            small.push_back(d);

            // Avoid duplicate for perfect square
            if (d != n / d) {
                large.push_back(n / d);
            }
        }
    }

    for (ll x : small)
        cout << x << " ";

    reverse(large.begin(), large.end());

    for (ll x : large)
        cout << x << " ";

    cout << "\n";
}

int main() {

    printDivisors(36);

    return 0;
}
```

Output:

```text
1 2 3 4 6 9 12 18 36
```

---

# 16. C++ Code 5 — Prime Factorization Preview

This is a preview of the next number theory layer.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Prime factorization using trial division
//
// Example:
// 60 = 2^2 * 3^1 * 5^1
// ---------------------------------------------------
vector<pair<ll, int>> primeFactorization(ll n) {

    vector<pair<ll, int>> factors;

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

    // Whatever remains is prime
    if (n > 1) {
        factors.push_back({n, 1});
    }

    return factors;
}

int main() {

    ll n = 60;

    auto factors = primeFactorization(n);

    for (auto [prime, power] : factors) {
        cout << prime << "^" << power << " ";
    }

    cout << "\n";

    return 0;
}
```

---

# 17. Dry Run Table

Check:

```cpp
isPrime(29)
```

| divisor d | d*d <= 29? | 29 % d | result |
|---:|---|---:|---|
| 2 | yes | 1 | continue |
| 3 | yes | 2 | continue |
| 4 | yes | 1 | continue |
| 5 | yes | 4 | continue |
| 6 | no | stop | prime |

Answer:

```text
Prime
```

---

# 18. Java Logic Comments

```text
function isPrime(n):
    if n < 2:
        return false

    if n == 2:
        return true

    if n is even:
        return false

    d = 3

    while d*d <= n:
        if n%d == 0:
            return false

        d += 2

    return true
```

Mental model:

```text
Try to find one divisor.
If no divisor exists up to sqrt(n), number is prime.
```

---

# 19. Common Mistakes

## Mistake 1 — Treating 1 As Prime

Wrong:

```cpp
1 is prime
```

Correct:

```cpp
1 is not prime
```

Prime must have exactly two divisors.

---

## Mistake 2 — Checking Till n

Wrong for large n:

```cpp
for (int d = 2; d < n; d++)
```

Better:

```cpp
for (int d = 2; d*d <= n; d++)
```

---

## Mistake 3 — Overflow In d*d

If `n` is very large, `d*d` may overflow.

Safer:

```cpp
d <= n / d
```

Use this for very large `long long`.

---

## Mistake 4 — Forgetting 2 Is Prime

2 is the only even prime.

---

## Mistake 5 — Using sqrt(n) Function Repeatedly

Avoid:

```cpp
d <= sqrt(n)
```

inside loop.

Better:

```cpp
d*d <= n
```

or:

```cpp
d <= n/d
```

---

# 20. CP / FAANG Problem Forms

This section includes:

```text
Problem
→ Pattern
→ Step-by-step working
→ C++ code
```

---

## Form 1 — Check If Number Is Prime

### Problem

Given:

```cpp
n = 97
```

Check whether it is prime.

---

### Pattern

Use:

```text
sqrt prime check
```

---

### Step-by-Step Working

```text
sqrt(97) is around 9

Check odd divisors:
3, 5, 7, 9

97 % 3 != 0
97 % 5 != 0
97 % 7 != 0
97 % 9 != 0
```

No divisor found.

Answer:

```text
prime
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

bool isPrime(ll n) {

    if (n < 2)
        return false;

    if (n == 2)
        return true;

    if (n % 2 == 0)
        return false;

    for (ll d = 3; d * d <= n; d += 2) {

        if (n % d == 0)
            return false;
    }

    return true;
}

int main() {

    cout << (isPrime(97) ? "Prime" : "Not Prime") << "\n";

    return 0;
}
```

---

## Form 2 — Count Primes In A Small Range

### Problem

Count primes from:

```cpp
1 to 20
```

---

### Pattern

For small range:

```text
call isPrime(x) for every x
```

For large range:

```text
use sieve
```

---

### Step-by-Step Working

Numbers:

```text
1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20
```

Primes:

```text
2 3 5 7 11 13 17 19
```

Count:

```cpp
8
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

bool isPrime(ll n) {

    if (n < 2)
        return false;

    for (ll d = 2; d * d <= n; d++) {

        if (n % d == 0)
            return false;
    }

    return true;
}

int main() {

    int count = 0;

    for (int x = 1; x <= 20; x++) {

        if (isPrime(x))
            count++;
    }

    cout << count << "\n";

    return 0;
}
```

---

## Form 3 — Print Divisors Of Number

### Problem

Print all divisors of:

```cpp
36
```

---

### Pattern

Use:

```text
factor pair search till sqrt(n)
```

---

### Step-by-Step Working

Check:

```text
1 divides 36 -> pair 1,36
2 divides 36 -> pair 2,18
3 divides 36 -> pair 3,12
4 divides 36 -> pair 4,9
6 divides 36 -> pair 6,6
```

Divisors:

```text
1 2 3 4 6 9 12 18 36
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void printDivisors(int n) {

    vector<int> left;
    vector<int> right;

    for (int d = 1; d * d <= n; d++) {

        if (n % d == 0) {

            left.push_back(d);

            if (d != n / d) {
                right.push_back(n / d);
            }
        }
    }

    for (int x : left)
        cout << x << " ";

    reverse(right.begin(), right.end());

    for (int x : right)
        cout << x << " ";
}

int main() {

    printDivisors(36);

    return 0;
}
```

---

## Form 4 — Prime Factorization

### Problem

Factorize:

```cpp
60
```

---

### Pattern

Repeatedly divide by small prime/divisor.

---

### Step-by-Step Working

```text
60 divisible by 2:
60 -> 30 -> 15
count of 2 = 2

15 divisible by 3:
15 -> 5
count of 3 = 1

remaining 5 is prime
```

Answer:

```cpp
60 = 2^2 * 3^1 * 5^1
```

---

### C++ Code

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

int main() {

    auto factors = factorize(60);

    for (auto [p, count] : factors) {
        cout << p << "^" << count << " ";
    }

    return 0;
}
```

---

## Form 5 — Many Prime Queries

### Problem

You are given many queries:

```text
Is 17 prime?
Is 25 prime?
Is 97 prime?
Is 100 prime?
```

---

### Pattern

If many queries and max value is not huge:

```text
use Sieve
```

This will be next file.

For now:

```text
single query -> sqrt check
many queries -> sieve
```

---

### Step-by-Step Working

Instead of checking from scratch each time:

```text
Precompute prime/not-prime once
Answer each query in O(1)
```

---

### C++ Code Preview

```cpp
// Full version comes in 006_Sieve_And_SPF.md

vector<bool> isPrimeSieve(int n) {

    vector<bool> prime(n + 1, true);

    prime[0] = false;
    prime[1] = false;

    for (int p = 2; p * p <= n; p++) {

        if (prime[p]) {

            for (int multiple = p * p; multiple <= n; multiple += p) {
                prime[multiple] = false;
            }
        }
    }

    return prime;
}
```

---

# 21. Real World Mapping

## Cryptography

Large prime numbers are core to RSA-like systems.

---

## Hash Tables

Prime bucket sizes can reduce bad collision patterns.

---

## Bloom Filters

Hashing and prime-related modular arithmetic appear in probabilistic data structures.

---

## Randomization

Prime moduli are used in rolling hash and randomized algorithms.

---

## Database / Search Systems

Prime-based hashing appears in indexing, sharding, and lookup structures.

---

# 22. Practice Problems

Easy:

```text
1. Check if n is prime
2. Count primes in [L, R]
3. Print all divisors
4. Check if number has exactly 3 divisors
```

Medium:

```text
5. Prime factorization
6. Count divisors
7. Sum of divisors
8. Product of divisors
```

Hard later:

```text
9. Sieve
10. SPF factorization
11. Segmented sieve
12. Miller Rabin primality test
```

---

# 23. Final Mental Summary

Prime checking asks:

```text
Can I find a divisor?
```

Naive:

```text
Check till n
```

Optimized:

```text
Check till sqrt(n)
```

Reason:

```text
Factors come in pairs.
```

Key idea:

```text
If n has no factor up to sqrt(n), n is prime.
```

---

# 24. Next Step

```text
006_Sieve_And_SPF.md
```

Next intuition:

```text
Instead of checking prime repeatedly,
precompute all primes efficiently.
```
