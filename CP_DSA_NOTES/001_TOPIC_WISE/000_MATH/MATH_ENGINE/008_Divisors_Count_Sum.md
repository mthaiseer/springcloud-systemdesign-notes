# 008_Divisors_Count_Sum.md

# MiniMathEngine — Divisors Count and Sum

> Goal: build deep intuition for:
>
> ```text
> how prime exponents generate divisors
> ```
>
> This topic is one of the MOST IMPORTANT bridges between:
>
> ```text
> Prime Factorization
> → Divisor Mathematics
> → Number Theory
> → Combinatorics
> ```
>
> Almost every advanced math problem eventually uses:
>
> - divisor count
> - divisor sum
> - prime exponents
> - geometric series

---

# Clickable Index

- [1. Why This Topic Matters](#1-why-this-topic-matters)
- [2. What Is A Divisor](#2-what-is-a-divisor)
- [3. Core Mental Model](#3-core-mental-model)
- [4. Divisors From Prime Powers](#4-divisors-from-prime-powers)
- [5. Divisor Count Formula](#5-divisor-count-formula)
- [6. Why Divisor Count Formula Works](#6-why-divisor-count-formula-works)
- [7. Divisor Sum Formula](#7-divisor-sum-formula)
- [8. Why Divisor Sum Formula Works](#8-why-divisor-sum-formula-works)
- [9. Step-by-Step Working — Divisors Of 60](#9-step-by-step-working--divisors-of-60)
- [10. Step-by-Step Working — Sum Of Divisors Of 72](#10-step-by-step-working--sum-of-divisors-of-72)
- [11. Pattern Detector](#11-pattern-detector)
- [12. C++ Code 1 — Count Divisors](#12-c-code-1--count-divisors)
- [13. C++ Code 2 — Sum Of Divisors](#13-c-code-2--sum-of-divisors)
- [14. C++ Code 3 — Generate All Divisors](#14-c-code-3--generate-all-divisors)
- [15. C++ Code 4 — Divisor Count Using SPF](#15-c-code-4--divisor-count-using-spf)
- [16. C++ Code 5 — Perfect Number Check](#16-c-code-5--perfect-number-check)
- [17. C++ Code 6 — Highly Composite Number Idea](#17-c-code-6--highly-composite-number-idea)
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

Once numbers are written as:

```cpp
n = p1^a1 * p2^a2 * ...
```

many powerful formulas become possible.

This topic teaches:

```text
how combinations of prime powers create divisors
```

Used in:

- divisor problems
- combinatorics
- multiplicative functions
- Euler Phi
- Mobius
- advanced CP math

---

# 2. What Is A Divisor

A divisor of `n` is a number that divides `n` exactly.

Example:

```cpp
12
```

Divisors:

```text
1 2 3 4 6 12
```

Because:

```cpp
12 % divisor == 0
```

---

# 3. Core Mental Model

Every divisor is formed by choosing powers from prime factorization.

Example:

```cpp
60 = 2^2 * 3^1 * 5^1
```

Any divisor can choose:

```text
power of 2 -> 0,1,2
power of 3 -> 0,1
power of 5 -> 0,1
```

Every combination creates one divisor.

This is the BIG intuition.

---

# 4. Divisors From Prime Powers

Example:

```cpp
60 = 2^2 * 3^1 * 5^1
```

Choices:

| Prime | Exponent Choices |
|---|---|
| 2 | 0,1,2 |
| 3 | 0,1 |
| 5 | 0,1 |

Possible divisor:

```cpp
2^1 * 3^0 * 5^1 = 10
```

Another:

```cpp
2^2 * 3^1 * 5^0 = 12
```

All divisors are generated this way.

---

# 5. Divisor Count Formula

If:

```cpp
n = p1^a1 * p2^a2 * ...
```

then:

```cpp
divisor_count =
(a1+1)(a2+1)...
```

Example:

```cpp
60 = 2^2 * 3^1 * 5^1
```

Count:

```cpp
(2+1)(1+1)(1+1)
= 3*2*2
= 12
```

---

# 6. Why Divisor Count Formula Works

For each prime:

you choose exponent independently.

Example:

```cpp
2^2
```

Choices:

```text
2^0
2^1
2^2
```

Total:

```text
3 choices
```

Similarly:

```cpp
3^1 -> 2 choices
5^1 -> 2 choices
```

Total combinations:

```cpp
3 * 2 * 2 = 12
```

Exactly like combinatorics multiplication principle.

---

# 7. Divisor Sum Formula

If:

```cpp
n = p1^a1 * p2^a2 * ...
```

then:

```cpp
sum_divisors =
(1+p1+p1^2+...)
(1+p2+p2^2+...)
...
```

Geometric series.

Example:

```cpp
60 = 2^2 * 3^1 * 5^1
```

Sum:

```cpp
(1+2+4)
(1+3)
(1+5)

= 7 * 4 * 6
= 168
```

---

# 8. Why Divisor Sum Formula Works

Each divisor is product of chosen powers.

Example:

```cpp
2^a * 3^b * 5^c
```

All possible combinations are produced by multiplying:

```cpp
(1+2+4)
(1+3)
(1+5)
```

This expands into every divisor exactly once.

Very important intuition.

---

# 9. Step-by-Step Working — Divisors Of 60

Prime factorization:

```cpp
60 = 2^2 * 3^1 * 5^1
```

Choices:

```text
2 -> 1,2,4
3 -> 1,3
5 -> 1,5
```

Generate combinations:

```text
1
2
4
3
6
12
5
10
20
15
30
60
```

Total:

```cpp
12 divisors
```

---

# 10. Step-by-Step Working — Sum Of Divisors Of 72

Factorization:

```cpp
72 = 2^3 * 3^2
```

Formula:

```cpp
(1+2+4+8)
(1+3+9)

= 15 * 13
= 195
```

Sum of all divisors:

```cpp
195
```

---

# 11. Pattern Detector

If you see:

```text
count divisors
```

think:

```text
prime exponents
```

If you see:

```text
sum divisors
```

think:

```text
geometric series
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
generate all divisors
```

think:

```text
recursive combinations of exponents
```

---

# 12. C++ Code 1 — Count Divisors

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Count divisors using prime factorization
//
// Formula:
// (a1+1)(a2+1)...
// ---------------------------------------------------
ll divisorCount(ll n) {

    ll answer = 1;

    for (ll p = 2; p * p <= n; p++) {

        if (n % p == 0) {

            int exponent = 0;

            while (n % p == 0) {

                n /= p;
                exponent++;
            }

            // Multiply choices
            answer *= (exponent + 1);
        }
    }

    // Remaining prime contributes exponent 1
    if (n > 1) {
        answer *= 2;
    }

    return answer;
}

int main() {

    cout << divisorCount(60) << "\n";

    return 0;
}
```

Output:

```text
12
```

---

# 13. C++ Code 2 — Sum Of Divisors

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Sum of divisors using geometric series
// ---------------------------------------------------
ll divisorSum(ll n) {

    ll answer = 1;

    for (ll p = 2; p * p <= n; p++) {

        if (n % p == 0) {

            ll term = 1;
            ll sum = 1;

            while (n % p == 0) {

                n /= p;

                term *= p;
                sum += term;
            }

            answer *= sum;
        }
    }

    // Remaining prime
    if (n > 1) {

        answer *= (1 + n);
    }

    return answer;
}

int main() {

    cout << divisorSum(60) << "\n";

    return 0;
}
```

Output:

```text
168
```

---

# 14. C++ Code 3 — Generate All Divisors

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

vector<pair<ll,int>> factorize(ll n) {

    vector<pair<ll,int>> factors;

    for (ll p = 2; p * p <= n; p++) {

        if (n % p == 0) {

            int exponent = 0;

            while (n % p == 0) {

                n /= p;
                exponent++;
            }

            factors.push_back({p, exponent});
        }
    }

    if (n > 1) {
        factors.push_back({n,1});
    }

    return factors;
}

// ---------------------------------------------------
// Generate divisors recursively
// ---------------------------------------------------
void buildDivisors(
    int index,
    ll current,
    vector<pair<ll,int>>& factors,
    vector<ll>& divisors
) {

    if (index == factors.size()) {

        divisors.push_back(current);
        return;
    }

    auto [prime, exponent] = factors[index];

    ll value = 1;

    for (int power = 0; power <= exponent; power++) {

        buildDivisors(
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

    buildDivisors(0, 1, factors, divisors);

    sort(divisors.begin(), divisors.end());

    for (ll d : divisors) {
        cout << d << " ";
    }

    return 0;
}
```

---

# 15. C++ Code 4 — Divisor Count Using SPF

```cpp
#include <bits/stdc++.h>
using namespace std;

// ---------------------------------------------------
// Build SPF
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
// Count divisors using SPF
// ---------------------------------------------------
int divisorCountSPF(int x, vector<int>& spf) {

    int answer = 1;

    while (x > 1) {

        int prime = spf[x];
        int exponent = 0;

        while (x % prime == 0) {

            x /= prime;
            exponent++;
        }

        answer *= (exponent + 1);
    }

    return answer;
}

int main() {

    vector<int> spf = buildSPF(100);

    cout << divisorCountSPF(60, spf) << "\n";

    return 0;
}
```

---

# 16. C++ Code 5 — Perfect Number Check

A perfect number equals sum of proper divisors.

Example:

```cpp
6 = 1+2+3
```

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll divisorSum(ll n) {

    ll original = n;
    ll answer = 1;

    for (ll p = 2; p * p <= n; p++) {

        if (n % p == 0) {

            ll term = 1;
            ll sum = 1;

            while (n % p == 0) {

                n /= p;

                term *= p;
                sum += term;
            }

            answer *= sum;
        }
    }

    if (n > 1) {
        answer *= (1 + n);
    }

    return answer;
}

// ---------------------------------------------------
// perfect if:
// sum(all divisors) - n == n
// ---------------------------------------------------
bool isPerfect(ll n) {

    return divisorSum(n) - n == n;
}

int main() {

    cout << isPerfect(6) << "\n";
    cout << isPerfect(28) << "\n";

    return 0;
}
```

---

# 17. C++ Code 6 — Highly Composite Number Idea

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll divisorCount(ll n) {

    ll answer = 1;

    for (ll p = 2; p * p <= n; p++) {

        if (n % p == 0) {

            int exponent = 0;

            while (n % p == 0) {

                n /= p;
                exponent++;
            }

            answer *= (exponent + 1);
        }
    }

    if (n > 1)
        answer *= 2;

    return answer;
}

// ---------------------------------------------------
// Find number with maximum divisors
// in range [1,n]
// ---------------------------------------------------
int main() {

    int n = 100;

    ll bestNumber = 1;
    ll bestDivisors = 1;

    for (int x = 1; x <= n; x++) {

        ll count = divisorCount(x);

        if (count > bestDivisors) {

            bestDivisors = count;
            bestNumber = x;
        }
    }

    cout << bestNumber
         << " -> "
         << bestDivisors
         << "\n";

    return 0;
}
```

---

# 18. Dry Run Table

For:

```cpp
60 = 2^2 * 3^1 * 5^1
```

Divisor count:

| Prime | Exponent | Choices |
|---|---:|---:|
| 2 | 2 | 3 |
| 3 | 1 | 2 |
| 5 | 1 | 2 |

Total:

```cpp
3 * 2 * 2 = 12
```

---

# 19. Java Logic Comments

```text
factorize n

for each prime exponent:
    answer *= (exponent + 1)
```

Sum:

```text
for each prime:
    compute:
    1+p+p^2+...

multiply all sums
```

Mental model:

```text
Each divisor is combination of prime powers.
```

---

# 20. Common Mistakes

## Mistake 1 — Forgetting Remaining Prime

After factorization loop:

```cpp
if (n > 1)
```

remaining number is prime.

---

## Mistake 2 — Wrong Divisor Count Formula

Wrong:

```cpp
a1*a2*a3
```

Correct:

```cpp
(a1+1)(a2+1)(a3+1)
```

---

## Mistake 3 — Confusing Divisor Count And Sum

Count:

```text
multiply exponent choices
```

Sum:

```text
multiply geometric sums
```

---

## Mistake 4 — Duplicate Divisors

When generating divisors recursively:

careful with repeated generation.

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

## Form 1 — Count Divisors

### Problem

Find divisor count of:

```cpp
72
```

---

### Pattern

Prime factorization.

---

### Step-by-Step Working

```cpp
72 = 2^3 * 3^2
```

Formula:

```cpp
(3+1)(2+1)
= 4*3
= 12
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll divisorCount(ll n) {

    ll answer = 1;

    for (ll p = 2; p * p <= n; p++) {

        if (n % p == 0) {

            int exponent = 0;

            while (n % p == 0) {

                n /= p;
                exponent++;
            }

            answer *= (exponent + 1);
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

## Form 2 — Sum Of Divisors

### Problem

Find sum of divisors of:

```cpp
60
```

---

### Pattern

Geometric series.

---

### Step-by-Step Working

```cpp
60 = 2^2 * 3^1 * 5^1
```

Formula:

```cpp
(1+2+4)
(1+3)
(1+5)

= 7*4*6
= 168
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll divisorSum(ll n) {

    ll answer = 1;

    for (ll p = 2; p * p <= n; p++) {

        if (n % p == 0) {

            ll term = 1;
            ll sum = 1;

            while (n % p == 0) {

                n /= p;

                term *= p;
                sum += term;
            }

            answer *= sum;
        }
    }

    if (n > 1)
        answer *= (1 + n);

    return answer;
}

int main() {

    cout << divisorSum(60) << "\n";

    return 0;
}
```

---

## Form 3 — Generate All Divisors

### Problem

Generate all divisors of:

```cpp
36
```

---

### Pattern

Recursive exponent combinations.

---

### Step-by-Step Working

```cpp
36 = 2^2 * 3^2
```

Choices:

```text
2^0,2^1,2^2
3^0,3^1,3^2
```

Generate all combinations.

---

## Form 4 — Perfect Square Check

### Problem

Check whether:

```cpp
900
```

is perfect square.

---

### Pattern

All prime exponents even.

---

### Step-by-Step Working

```cpp
900 = 2^2 * 3^2 * 5^2
```

All exponents even.

Therefore:

```text
perfect square
```

---

## Form 5 — Perfect Number

### Problem

Check whether:

```cpp
28
```

is perfect number.

---

### Pattern

```cpp
sum(divisors)-n == n
```

---

### Step-by-Step Working

Divisors:

```text
1 2 4 7 14 28
```

Proper divisor sum:

```text
1+2+4+7+14 = 28
```

Therefore:

```text
perfect number
```

---

# 22. Real World Mapping

## Combinatorics

Divisor count uses multiplication principle.

---

## Analytics Systems

Prefix aggregations and combinational expansion ideas appear in OLAP systems.

---

## Cryptography

Prime powers appear everywhere in modular arithmetic.

---

## Search / Query Engines

Precomputed factor metadata behaves like indexing.

---

# 23. Practice Problems

Easy:

```text
1. Count divisors
2. Sum divisors
3. Generate divisors
4. Perfect square
```

Medium:

```text
5. Perfect cube
6. Distinct divisor count
7. Divisor product
8. Highly composite numbers
```

Hard:

```text
9. Mobius
10. Multiplicative functions
11. Dirichlet convolution
12. Divisor summatory function
```

---

# 24. Final Mental Summary

Every divisor comes from:

```text
choosing exponents from prime powers
```

Key formulas:

Count:

```cpp
(a1+1)(a2+1)...
```

Sum:

```cpp
(1+p+p^2+...)
```

Big intuition:

```text
Divisors are combinations of prime exponents.
```

---

# 25. Next Step

```text
009_Euler_Phi_Coprime.md
```

Next intuition:

```text
Count numbers coprime with n efficiently.
```
