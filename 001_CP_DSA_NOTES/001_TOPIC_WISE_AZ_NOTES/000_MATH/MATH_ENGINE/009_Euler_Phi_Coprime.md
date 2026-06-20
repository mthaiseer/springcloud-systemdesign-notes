# 009_Euler_Phi_Coprime.md

# MiniMathEngine — Euler Phi Function and Coprime Intuition

> Goal: build deep intuition for:
>
> ```text
> coprime numbers
> Euler Phi
> multiplicative counting
> ```
>
> This topic connects:
>
> ```text
> GCD
> → Prime Factorization
> → Modular Arithmetic
> → Euler Theorem
> → Cryptography
> ```
>
> Euler Phi is one of the MOST IMPORTANT functions in number theory.

---

# Clickable Index

- [1. Why This Topic Matters](#1-why-this-topic-matters)
- [2. What Are Coprime Numbers](#2-what-are-coprime-numbers)
- [3. Core Mental Model](#3-core-mental-model)
- [4. What Is Euler Phi Function](#4-what-is-euler-phi-function)
- [5. Step-by-Step Coprime Intuition](#5-step-by-step-coprime-intuition)
- [6. Phi Of Prime Number](#6-phi-of-prime-number)
- [7. Phi Of Prime Power](#7-phi-of-prime-power)
- [8. General Euler Phi Formula](#8-general-euler-phi-formula)
- [9. Why Euler Phi Formula Works](#9-why-euler-phi-formula-works)
- [10. Step-by-Step Working — phi(12)](#10-step-by-step-working--phi12)
- [11. Step-by-Step Working — phi(36)](#11-step-by-step-working--phi36)
- [12. Euler's Theorem](#12-eulers-theorem)
- [13. Pattern Detector](#13-pattern-detector)
- [14. C++ Code 1 — Naive Euler Phi](#14-c-code-1--naive-euler-phi)
- [15. C++ Code 2 — Optimized Euler Phi](#15-c-code-2--optimized-euler-phi)
- [16. C++ Code 3 — Euler Phi Sieve](#16-c-code-3--euler-phi-sieve)
- [17. C++ Code 4 — Count Coprimes In Range](#17-c-code-4--count-coprimes-in-range)
- [18. C++ Code 5 — Euler Theorem Demo](#18-c-code-5--euler-theorem-demo)
- [19. C++ Code 6 — Modular Inverse Using Euler](#19-c-code-6--modular-inverse-using-euler)
- [20. Dry Run Table](#20-dry-run-table)
- [21. Java Logic Comments](#21-java-logic-comments)
- [22. Common Mistakes](#22-common-mistakes)
- [23. CP / FAANG Problem Forms](#23-cp--faang-problem-forms)
- [24. Real World Mapping](#24-real-world-mapping)
- [25. Practice Problems](#25-practice-problems)
- [26. Final Mental Summary](#26-final-mental-summary)
- [27. Next Step](#27-next-step)

---

# 1. Why This Topic Matters

Euler Phi appears in:

- modular arithmetic
- modular inverse
- cryptography
- RSA
- combinatorics
- number theory
- advanced CP

Very important concept.

Phi teaches:

```text
how many numbers are "compatible"
with n under modular arithmetic
```

---

# 2. What Are Coprime Numbers

Two numbers are coprime if:

```cpp
gcd(a,b)=1
```

Examples:

```cpp
8 and 15
```

Because:

```cpp
gcd(8,15)=1
```

Not coprime:

```cpp
12 and 18
```

Because:

```cpp
gcd(12,18)=6
```

---

# 3. Core Mental Model

Coprime means:

```text
no shared prime factors
```

Example:

```cpp
12 = 2^2 * 3
25 = 5^2
```

No common prime factor.

Therefore:

```text
coprime
```

Very important intuition.

---

# 4. What Is Euler Phi Function

Euler Phi function:

```cpp
phi(n)
```

counts:

```text
how many numbers from 1 to n
are coprime with n
```

Example:

```cpp
phi(10)
```

Numbers from 1 to 10:

```text
1 2 3 4 5 6 7 8 9 10
```

Coprime with 10:

```text
1 3 7 9
```

Count:

```cpp
4
```

So:

```cpp
phi(10)=4
```

---

# 5. Step-by-Step Coprime Intuition

Example:

```cpp
n = 12
```

Prime factors:

```cpp
12 = 2^2 * 3
```

A number is NOT coprime if divisible by:

```text
2 or 3
```

Numbers:

```text
1 2 3 4 5 6 7 8 9 10 11 12
```

Remove multiples of 2 and 3:

Remaining:

```text
1 5 7 11
```

Count:

```cpp
4
```

So:

```cpp
phi(12)=4
```

---

# 6. Phi Of Prime Number

If `p` is prime:

```cpp
phi(p)=p-1
```

Why?

Because all numbers:

```text
1 to p-1
```

are coprime with prime `p`.

Example:

```cpp
phi(7)=6
```

---

# 7. Phi Of Prime Power

Formula:

```cpp
phi(p^k)=p^k - p^(k-1)
```

or:

```cpp
phi(p^k)=p^k(1-1/p)
```

Example:

```cpp
phi(8)
```

```cpp
8 = 2^3
```

Numbers not coprime:

```text
2,4,6,8
```

Count:

```text
4
```

Therefore:

```cpp
phi(8)=4
```

Formula:

```cpp
8*(1-1/2)=4
```

Correct.

---

# 8. General Euler Phi Formula

If:

```cpp
n = p1^a1 * p2^a2 * ...
```

then:

```cpp
phi(n)=n*(1-1/p1)*(1-1/p2)*...
```

Only DISTINCT primes matter.

Example:

```cpp
36 = 2^2 * 3^2
```

Formula:

```cpp
36*(1-1/2)*(1-1/3)
=
36*(1/2)*(2/3)
=
12
```

---

# 9. Why Euler Phi Formula Works

We remove numbers divisible by prime factors.

Example:

```cpp
n=36
```

Remove multiples of:

```text
2
```

Then remove multiples of:

```text
3
```

This is inclusion-exclusion intuition.

Phi counts numbers NOT divisible by any prime factor.

Very important idea.

---

# 10. Step-by-Step Working — phi(12)

Prime factorization:

```cpp
12 = 2^2 * 3
```

Start:

```cpp
result = 12
```

Process prime 2:

```cpp
result = 12 - 12/2
       = 6
```

Process prime 3:

```cpp
result = 6 - 6/3
       = 4
```

Answer:

```cpp
phi(12)=4
```

---

# 11. Step-by-Step Working — phi(36)

Prime factorization:

```cpp
36 = 2^2 * 3^2
```

Start:

```cpp
result = 36
```

Process 2:

```cpp
36 - 18 = 18
```

Process 3:

```cpp
18 - 6 = 12
```

Answer:

```cpp
phi(36)=12
```

---

# 12. Euler's Theorem

Very important theorem:

If:

```cpp
gcd(a,n)=1
```

then:

```cpp
a^phi(n) % n = 1
```

This generalizes Fermat's theorem.

Fermat:

```cpp
a^(p-1)%p=1
```

when `p` is prime.

Euler works for general `n`.

---

# 13. Pattern Detector

If you see:

```text
count coprimes
```

think:

```text
Euler Phi
```

If you see:

```text
modular exponent cycle
```

think:

```text
Euler theorem
```

If you see:

```text
inverse under non-prime MOD
```

think:

```text
Euler theorem
```

If you see:

```text
remove numbers divisible by prime factors
```

think:

```text
phi formula
```

---

# 14. C++ Code 1 — Naive Euler Phi

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Naive phi:
// Count numbers coprime with n
//
// Time:
// O(n log n)
// ---------------------------------------------------
ll phiNaive(ll n) {

    ll count = 0;

    for (ll x = 1; x <= n; x++) {

        if (__gcd(x, n) == 1) {
            count++;
        }
    }

    return count;
}

int main() {

    cout << phiNaive(12) << "\n";

    return 0;
}
```

---

# 15. C++ Code 2 — Optimized Euler Phi

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Optimized Euler Phi
//
// Formula:
// phi(n)=n*(1-1/p)
//
// for every distinct prime p
// ---------------------------------------------------
ll phi(ll n) {

    ll result = n;

    for (ll p = 2; p * p <= n; p++) {

        // Prime factor found
        if (n % p == 0) {

            // Remove all copies of p
            while (n % p == 0) {
                n /= p;
            }

            // Apply phi formula
            result -= result / p;
        }
    }

    // Remaining prime factor
    if (n > 1) {
        result -= result / n;
    }

    return result;
}

int main() {

    cout << phi(12) << "\n";
    cout << phi(36) << "\n";

    return 0;
}
```

---

# 16. C++ Code 3 — Euler Phi Sieve

```cpp
#include <bits/stdc++.h>
using namespace std;

// ---------------------------------------------------
// Compute phi for all numbers
// from 1 to n
//
// Similar to prime sieve
// ---------------------------------------------------
vector<int> phiSieve(int n) {

    vector<int> phi(n + 1);

    // Initialize
    for (int i = 0; i <= n; i++) {
        phi[i] = i;
    }

    // Sieve-like processing
    for (int p = 2; p <= n; p++) {

        // p is prime
        if (phi[p] == p) {

            for (int multiple = p; multiple <= n; multiple += p) {

                phi[multiple] -= phi[multiple] / p;
            }
        }
    }

    return phi;
}

int main() {

    vector<int> phi = phiSieve(20);

    for (int i = 1; i <= 20; i++) {

        cout << "phi("
             << i
             << ") = "
             << phi[i]
             << "\n";
    }

    return 0;
}
```

---

# 17. C++ Code 4 — Count Coprimes In Range

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Count numbers in [1,n]
// coprime with n
// ---------------------------------------------------
ll phi(ll n) {

    ll result = n;

    for (ll p = 2; p * p <= n; p++) {

        if (n % p == 0) {

            while (n % p == 0) {
                n /= p;
            }

            result -= result / p;
        }
    }

    if (n > 1)
        result -= result / n;

    return result;
}

int main() {

    cout << phi(100) << "\n";

    return 0;
}
```

---

# 18. C++ Code 5 — Euler Theorem Demo

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll powerMod(ll base, ll exp, ll mod) {

    ll result = 1;

    base %= mod;

    while (exp > 0) {

        if (exp & 1) {
            result = (result * base) % mod;
        }

        base = (base * base) % mod;

        exp >>= 1;
    }

    return result;
}

ll phi(ll n) {

    ll result = n;

    for (ll p = 2; p * p <= n; p++) {

        if (n % p == 0) {

            while (n % p == 0)
                n /= p;

            result -= result / p;
        }
    }

    if (n > 1)
        result -= result / n;

    return result;
}

int main() {

    ll a = 5;
    ll n = 12;

    // gcd(5,12)=1
    cout << powerMod(a, phi(n), n) << "\n";

    return 0;
}
```

Output:

```text
1
```

---

# 19. C++ Code 6 — Modular Inverse Using Euler

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll powerMod(ll base, ll exp, ll mod) {

    ll result = 1;

    base %= mod;

    while (exp > 0) {

        if (exp & 1)
            result = (result * base) % mod;

        base = (base * base) % mod;

        exp >>= 1;
    }

    return result;
}

ll phi(ll n) {

    ll result = n;

    for (ll p = 2; p * p <= n; p++) {

        if (n % p == 0) {

            while (n % p == 0)
                n /= p;

            result -= result / p;
        }
    }

    if (n > 1)
        result -= result / n;

    return result;
}

// ---------------------------------------------------
// inverse(a)=a^(phi(mod)-1)
// when gcd(a,mod)=1
// ---------------------------------------------------
ll modularInverse(ll a, ll mod) {

    return powerMod(a, phi(mod) - 1, mod);
}

int main() {

    cout << modularInverse(3, 7) << "\n";

    return 0;
}
```

---

# 20. Dry Run Table

Find:

```cpp
phi(12)
```

| Step | result |
|---|---:|
| start | 12 |
| process prime 2 | 12 - 6 = 6 |
| process prime 3 | 6 - 2 = 4 |

Answer:

```cpp
4
```

---

# 21. Java Logic Comments

```text
result = n

for each distinct prime p:
    result -= result/p
```

Mental model:

```text
Remove numbers divisible by each prime factor.
```

---

# 22. Common Mistakes

## Mistake 1 — Using ALL Prime Powers

Wrong:

```cpp
n*(1-1/p^k)
```

Correct:

```cpp
n*(1-1/p)
```

Only DISTINCT primes matter.

---

## Mistake 2 — Forgetting Remaining Prime

After factorization:

```cpp
if (n > 1)
```

remaining number itself is prime.

---

## Mistake 3 — Euler Theorem Requires Coprime

Need:

```cpp
gcd(a,n)=1
```

Otherwise theorem fails.

---

## Mistake 4 — Confusing Phi With Divisor Count

Phi:

```text
counts coprimes
```

Divisor count:

```text
counts divisors
```

Completely different.

---

# 23. CP / FAANG Problem Forms

This section includes:

```text
Problem
→ Pattern
→ Step-by-step working
→ C++ code
```

---

## Form 1 — Count Coprimes

### Problem

Find:

```cpp
phi(18)
```

---

### Pattern

Use Euler Phi formula.

---

### Step-by-Step Working

```cpp
18 = 2 * 3^2
```

Formula:

```cpp
18*(1-1/2)*(1-1/3)
=
18*(1/2)*(2/3)
=
6
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll phi(ll n) {

    ll result = n;

    for (ll p = 2; p * p <= n; p++) {

        if (n % p == 0) {

            while (n % p == 0)
                n /= p;

            result -= result / p;
        }
    }

    if (n > 1)
        result -= result / n;

    return result;
}

int main() {

    cout << phi(18) << "\n";

    return 0;
}
```

---

## Form 2 — Euler Theorem

### Problem

Compute:

```cpp
5^phi(12) % 12
```

---

### Pattern

Euler theorem.

---

### Step-by-Step Working

```cpp
phi(12)=4
```

Compute:

```cpp
5^4 % 12
=
625 % 12
=
1
```

Correct.

---

## Form 3 — Modular Inverse Under Non-Prime MOD

### Problem

Find inverse of:

```cpp
3 mod 10
```

---

### Pattern

Use Euler theorem.

---

### Step-by-Step Working

```cpp
phi(10)=4
```

Formula:

```cpp
inverse(a)=a^(phi(mod)-1)
```

Compute:

```cpp
3^3 % 10
=
27 % 10
=
7
```

Check:

```cpp
3*7 % 10 = 1
```

Correct.

---

## Form 4 — Count Numbers Not Divisible By Prime Factors

### Problem

How many numbers from:

```text
1 to 30
```

are coprime with 30?

---

### Pattern

Euler Phi.

---

### Step-by-Step Working

```cpp
30 = 2 * 3 * 5
```

Formula:

```cpp
30*(1-1/2)*(1-1/3)*(1-1/5)
=
8
```

Answer:

```cpp
8
```

---

# 24. Real World Mapping

## Cryptography

Euler Phi is core to RSA encryption.

---

## Secure Communication

Public/private key systems use modular arithmetic cycles.

---

## Distributed Hashing

Coprime cycles help avoid synchronization collisions.

---

## Cyclic Systems

Phi measures valid cyclic states.

---

# 25. Practice Problems

Easy:

```text
1. Compute phi(n)
2. Count coprimes
3. Phi of prime
4. Phi of prime powers
```

Medium:

```text
5. Euler theorem
6. Modular inverse using phi
7. Phi sieve
8. Coprime pairs
```

Hard:

```text
9. Mobius
10. CRT
11. RSA
12. Primitive roots
```

---

# 26. Final Mental Summary

Euler Phi counts:

```text
numbers compatible with n
under modular arithmetic
```

Key formula:

```cpp
phi(n)=n*(1-1/p1)*(1-1/p2)...
```

Big intuition:

```text
Remove numbers divisible by prime factors.
```

Euler theorem:

```cpp
a^phi(n)%n=1
```

when:

```cpp
gcd(a,n)=1
```

---

# 27. Next Step

```text
010_nCr_Basic.md
```

Next intuition:

```text
Counting selections and combinations.
```
