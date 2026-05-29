# 011_nCr_Modulo_Prime.md

# MiniMathEngine — nCr Modulo Prime

> Goal: build deep intuition for:
>
> ```text
> nCr under MOD
> factorial precomputation
> modular inverse
> inverse factorial
> fast combination queries
> ```
>
> This file connects:
>
> ```text
> 002_Fast_Power_Binary_Exponentiation
> 003_Modular_Inverse
> 010_nCr_Basic
> ```
>
> Now we solve the real CP/FAANG form:
>
> ```text
> compute nCr % MOD for large n and many queries
> ```

---

# Clickable Index

- [1. Why This Topic Matters](#1-why-this-topic-matters)
- [2. Problem With Normal nCr](#2-problem-with-normal-ncr)
- [3. Core Mental Model](#3-core-mental-model)
- [4. nCr Formula Under MOD](#4-ncr-formula-under-mod)
- [5. Why Division Becomes Modular Inverse](#5-why-division-becomes-modular-inverse)
- [6. Why MOD Must Be Prime For Fermat](#6-why-mod-must-be-prime-for-fermat)
- [7. Factorial Precomputation](#7-factorial-precomputation)
- [8. Inverse Factorial Precomputation](#8-inverse-factorial-precomputation)
- [9. Step-by-Step Working — 5C2 mod 1e9+7](#9-step-by-step-working--5c2-mod-1e97)
- [10. Step-by-Step Working — Factorial Arrays](#10-step-by-step-working--factorial-arrays)
- [11. Pattern Detector](#11-pattern-detector)
- [12. C++ Code 1 — Single nCr Using Modular Inverse](#12-c-code-1--single-ncr-using-modular-inverse)
- [13. C++ Code 2 — Precompute Factorial And Inverse Factorial](#13-c-code-2--precompute-factorial-and-inverse-factorial)
- [14. C++ Code 3 — Combination Engine Class](#14-c-code-3--combination-engine-class)
- [15. C++ Code 4 — Many nCr Queries](#15-c-code-4--many-ncr-queries)
- [16. C++ Code 5 — Grid Paths Under MOD](#16-c-code-5--grid-paths-under-mod)
- [17. C++ Code 6 — Count Teams With Constraints](#17-c-code-6--count-teams-with-constraints)
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

In many CP problems, answer becomes very large.

So problem says:

```text
Print answer modulo 1e9+7
```

For counting problems, you often need:

```cpp
nCr % MOD
```

But normal formula has division:

```cpp
nCr = n! / (r! * (n-r)!)
```

Division under modulo is not direct.

So we use:

```text
modular inverse
```

This is why previous files matter.

---

# 2. Problem With Normal nCr

Normal:

```cpp
nCr = n! / (r! * (n-r)!)
```

Problems:

```text
1. factorial becomes huge
2. overflow happens
3. division under MOD is invalid
4. many queries become slow
```

Example:

```cpp
1000000C500000
```

Impossible with normal integer factorial.

Need modular method.

---

# 3. Core Mental Model

Think:

```text
nCr under MOD = factorials + modular inverse
```

Formula becomes:

```cpp
nCr % MOD
=
fact[n] * invFact[r] * invFact[n-r] % MOD
```

Where:

```cpp
invFact[x] = inverse(fact[x])
```

So:

```text
division becomes multiplication by inverse factorial
```

---

# 4. nCr Formula Under MOD

Normal formula:

```cpp
nCr = n! / (r! * (n-r)!)
```

Modulo formula:

```cpp
nCr % MOD =
fact[n] * inverse(fact[r]) * inverse(fact[n-r]) % MOD
```

Optimized with inverse factorial:

```cpp
nCr % MOD =
fact[n] * invFact[r] % MOD * invFact[n-r] % MOD
```

---

# 5. Why Division Becomes Modular Inverse

From modular inverse:

```cpp
a / b % MOD
```

becomes:

```cpp
a * inverse(b) % MOD
```

So:

```cpp
n! / (r! * (n-r)!)
```

becomes:

```cpp
fact[n] * inverse(fact[r]) * inverse(fact[n-r])
```

This is the key connection.

---

# 6. Why MOD Must Be Prime For Fermat

Fermat inverse:

```cpp
inverse(a)=a^(MOD-2)%MOD
```

works when:

```text
MOD is prime
```

Common CP moduli:

```cpp
1e9+7
998244353
```

Both are prime.

If MOD is not prime:

```text
this method may fail
```

Use Extended Euclid or other methods.

---

# 7. Factorial Precomputation

We precompute:

```cpp
fact[i] = i! % MOD
```

Example:

```text
fact[0] = 1
fact[1] = 1
fact[2] = 2
fact[3] = 6
fact[4] = 24
fact[5] = 120
```

Then `fact[n]` is O(1).

---

# 8. Inverse Factorial Precomputation

We also precompute:

```cpp
invFact[i] = inverse(fact[i])
```

Then every nCr query is:

```cpp
O(1)
```

After:

```cpp
O(N log MOD)
```

or optimized:

```cpp
O(N + log MOD)
```

precomputation.

---

# 9. Step-by-Step Working — 5C2 mod 1e9+7

Normal:

```cpp
5C2 = 5! / (2! * 3!)
```

Values:

```text
fact[5] = 120
fact[2] = 2
fact[3] = 6
```

Modulo formula:

```cpp
120 * inverse(2) * inverse(6) % MOD
```

Since result is small:

```cpp
5C2 = 10
```

Under MOD:

```cpp
10
```

---

# 10. Step-by-Step Working — Factorial Arrays

For `N = 5`:

```text
fact[0] = 1
fact[1] = 1
fact[2] = 2
fact[3] = 6
fact[4] = 24
fact[5] = 120
```

Inverse factorials:

```text
invFact[5] = inverse(120)
invFact[4] = inverse(24)
...
```

Then:

```cpp
nCr(5,2)
=
fact[5] * invFact[2] * invFact[3]
```

---

# 11. Pattern Detector

If you see:

```text
nCr % MOD
```

think:

```text
factorial + inverse factorial
```

If you see:

```text
many combination queries
```

think:

```text
precompute fact and invFact
```

If you see:

```text
grid paths modulo MOD
```

think:

```text
nCr under MOD
```

If you see:

```text
choose k from n with large answer
```

think:

```text
nCr modulo prime
```

---

# 12. C++ Code 1 — Single nCr Using Modular Inverse

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll MOD = 1e9 + 7;

// ---------------------------------------------------
// Fast power:
// computes base^exp % MOD
// ---------------------------------------------------
ll modPower(ll base, ll exp) {

    ll result = 1;

    base %= MOD;

    while (exp > 0) {

        if (exp & 1) {
            result = (result * base) % MOD;
        }

        base = (base * base) % MOD;

        exp >>= 1;
    }

    return result;
}

// ---------------------------------------------------
// Modular inverse using Fermat
// MOD must be prime
// ---------------------------------------------------
ll modInverse(ll x) {

    return modPower(x, MOD - 2);
}

// ---------------------------------------------------
// Compute nCr for single query
//
// Good for small n or few queries.
// ---------------------------------------------------
ll nCrSingle(ll n, ll r) {

    if (r < 0 || r > n)
        return 0;

    ll numerator = 1;
    ll denominator = 1;

    // numerator = n*(n-1)*...(n-r+1)
    for (ll i = 0; i < r; i++) {
        numerator = (numerator * (n - i)) % MOD;
    }

    // denominator = r!
    for (ll i = 1; i <= r; i++) {
        denominator = (denominator * i) % MOD;
    }

    // Divide by denominator using modular inverse
    return (numerator * modInverse(denominator)) % MOD;
}

int main() {

    cout << nCrSingle(5, 2) << "\n";

    return 0;
}
```

---

# 13. C++ Code 2 — Precompute Factorial And Inverse Factorial

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll MOD = 1e9 + 7;
const int MAXN = 1000000;

vector<ll> fact(MAXN + 1);
vector<ll> invFact(MAXN + 1);

ll modPower(ll base, ll exp) {

    ll result = 1;

    base %= MOD;

    while (exp > 0) {

        if (exp & 1) {
            result = (result * base) % MOD;
        }

        base = (base * base) % MOD;

        exp >>= 1;
    }

    return result;
}

ll modInverse(ll x) {

    return modPower(x, MOD - 2);
}

// ---------------------------------------------------
// Precompute factorial and inverse factorial
// ---------------------------------------------------
void precompute() {

    // Step 1:
    // fact[0] = 1
    fact[0] = 1;

    // Step 2:
    // Build factorial array
    for (int i = 1; i <= MAXN; i++) {
        fact[i] = (fact[i - 1] * i) % MOD;
    }

    // Step 3:
    // Compute inverse factorial of MAXN
    invFact[MAXN] = modInverse(fact[MAXN]);

    // Step 4:
    // Build inverse factorial backwards
    for (int i = MAXN - 1; i >= 0; i--) {
        invFact[i] = (invFact[i + 1] * (i + 1)) % MOD;
    }
}

// ---------------------------------------------------
// O(1) nCr query after precomputation
// ---------------------------------------------------
ll nCr(int n, int r) {

    if (r < 0 || r > n)
        return 0;

    return fact[n] * invFact[r] % MOD * invFact[n - r] % MOD;
}

int main() {

    precompute();

    cout << nCr(5, 2) << "\n";
    cout << nCr(1000000, 500000) << "\n";

    return 0;
}
```

---

# 14. C++ Code 3 — Combination Engine Class

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

class CombinationEngine {

private:
    ll MOD;
    int maxN;
    vector<ll> fact;
    vector<ll> invFact;

    ll modPower(ll base, ll exp) {

        ll result = 1;

        base %= MOD;

        while (exp > 0) {

            if (exp & 1) {
                result = (result * base) % MOD;
            }

            base = (base * base) % MOD;

            exp >>= 1;
        }

        return result;
    }

    ll modInverse(ll x) {

        return modPower(x, MOD - 2);
    }

public:
    CombinationEngine(int n, ll mod) {

        maxN = n;
        MOD = mod;

        fact.assign(maxN + 1, 1);
        invFact.assign(maxN + 1, 1);

        build();
    }

    void build() {

        for (int i = 1; i <= maxN; i++) {
            fact[i] = (fact[i - 1] * i) % MOD;
        }

        invFact[maxN] = modInverse(fact[maxN]);

        for (int i = maxN - 1; i >= 0; i--) {
            invFact[i] = (invFact[i + 1] * (i + 1)) % MOD;
        }
    }

    ll nCr(int n, int r) {

        if (r < 0 || r > n)
            return 0;

        return fact[n] * invFact[r] % MOD * invFact[n - r] % MOD;
    }
};

int main() {

    CombinationEngine engine(1000000, 1000000007);

    cout << engine.nCr(5, 2) << "\n";

    return 0;
}
```

---

# 15. C++ Code 4 — Many nCr Queries

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll MOD = 1e9 + 7;
const int MAXN = 1000000;

vector<ll> fact(MAXN + 1);
vector<ll> invFact(MAXN + 1);

ll modPower(ll base, ll exp) {

    ll result = 1;
    base %= MOD;

    while (exp > 0) {

        if (exp & 1)
            result = result * base % MOD;

        base = base * base % MOD;

        exp >>= 1;
    }

    return result;
}

void precompute() {

    fact[0] = 1;

    for (int i = 1; i <= MAXN; i++) {
        fact[i] = fact[i - 1] * i % MOD;
    }

    invFact[MAXN] = modPower(fact[MAXN], MOD - 2);

    for (int i = MAXN - 1; i >= 0; i--) {
        invFact[i] = invFact[i + 1] * (i + 1) % MOD;
    }
}

ll nCr(int n, int r) {

    if (r < 0 || r > n)
        return 0;

    return fact[n] * invFact[r] % MOD * invFact[n - r] % MOD;
}

int main() {

    precompute();

    vector<pair<int,int>> queries = {
        {5,2},
        {10,3},
        {100,50}
    };

    for (auto [n, r] : queries) {
        cout << nCr(n, r) << "\n";
    }

    return 0;
}
```

---

# 16. C++ Code 5 — Grid Paths Under MOD

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll MOD = 1e9 + 7;
const int MAXN = 2000000;

vector<ll> fact(MAXN + 1);
vector<ll> invFact(MAXN + 1);

ll modPower(ll base, ll exp) {

    ll result = 1;
    base %= MOD;

    while (exp > 0) {

        if (exp & 1)
            result = result * base % MOD;

        base = base * base % MOD;
        exp >>= 1;
    }

    return result;
}

void precompute() {

    fact[0] = 1;

    for (int i = 1; i <= MAXN; i++) {
        fact[i] = fact[i - 1] * i % MOD;
    }

    invFact[MAXN] = modPower(fact[MAXN], MOD - 2);

    for (int i = MAXN - 1; i >= 0; i--) {
        invFact[i] = invFact[i + 1] * (i + 1) % MOD;
    }
}

ll nCr(int n, int r) {

    if (r < 0 || r > n)
        return 0;

    return fact[n] * invFact[r] % MOD * invFact[n - r] % MOD;
}

// ---------------------------------------------------
// Grid path count:
// rows x cols
//
// moves = (rows-1) down + (cols-1) right
// answer = totalMoves C downMoves
// ---------------------------------------------------
ll gridPaths(int rows, int cols) {

    int down = rows - 1;
    int right = cols - 1;

    int total = down + right;

    return nCr(total, down);
}

int main() {

    precompute();

    cout << gridPaths(3, 3) << "\n";

    return 0;
}
```

---

# 17. C++ Code 6 — Count Teams With Constraints

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll MOD = 1e9 + 7;
const int MAXN = 1000000;

vector<ll> fact(MAXN + 1);
vector<ll> invFact(MAXN + 1);

ll modPower(ll base, ll exp) {

    ll result = 1;
    base %= MOD;

    while (exp > 0) {

        if (exp & 1)
            result = result * base % MOD;

        base = base * base % MOD;
        exp >>= 1;
    }

    return result;
}

void precompute() {

    fact[0] = 1;

    for (int i = 1; i <= MAXN; i++) {
        fact[i] = fact[i - 1] * i % MOD;
    }

    invFact[MAXN] = modPower(fact[MAXN], MOD - 2);

    for (int i = MAXN - 1; i >= 0; i--) {
        invFact[i] = invFact[i + 1] * (i + 1) % MOD;
    }
}

ll nCr(int n, int r) {

    if (r < 0 || r > n)
        return 0;

    return fact[n] * invFact[r] % MOD * invFact[n - r] % MOD;
}

int main() {

    precompute();

    int boys = 5;
    int girls = 4;

    // Choose 2 boys and 1 girl
    ll ways = nCr(boys, 2) * nCr(girls, 1) % MOD;

    cout << ways << "\n";

    return 0;
}
```

---

# 18. Dry Run Table

Compute:

```cpp
5C2
```

| Part | Value |
|---|---:|
| fact[5] | 120 |
| fact[2] | 2 |
| fact[3] | 6 |
| invFact[2] | inverse(2) |
| invFact[3] | inverse(6) |
| result | 10 |

---

# 19. Java Logic Comments

```text
precompute factorial:
    fact[0] = 1
    for i = 1 to maxN:
        fact[i] = fact[i-1] * i % MOD

precompute inverse factorial:
    invFact[maxN] = power(fact[maxN], MOD-2)
    for i = maxN-1 downto 0:
        invFact[i] = invFact[i+1] * (i+1) % MOD

query:
    nCr(n,r) = fact[n] * invFact[r] * invFact[n-r] % MOD
```

---

# 20. Common Mistakes

## Mistake 1 — Direct Division Under MOD

Wrong:

```cpp
fact[n] / (fact[r] * fact[n-r])
```

Correct:

```cpp
fact[n] * invFact[r] * invFact[n-r] % MOD
```

---

## Mistake 2 — MOD Not Prime

Fermat inverse requires:

```text
MOD is prime
```

---

## Mistake 3 — Wrong MAXN

If query has:

```cpp
n > MAXN
```

your factorial arrays fail.

Precompute enough.

---

## Mistake 4 — Forgetting r > n Case

Always handle:

```cpp
if (r < 0 || r > n) return 0;
```

---

## Mistake 5 — Overflow Before Mod

Use:

```cpp
long long
```

for multiplication.

---

# 21. CP / FAANG Problem Forms

This section includes:

```text
Problem
→ Pattern
→ Step-by-step working
→ C++ code with comments
```

---

## Form 1 — Many Combination Queries

### Problem

Answer many queries:

```text
5C2
10C3
100C50
```

under:

```cpp
MOD = 1e9+7
```

---

### Pattern

Use:

```text
factorial + inverse factorial precomputation
```

---

### Step-by-Step Working

Precompute once:

```text
fact[i]
invFact[i]
```

Then:

```cpp
nCr(n,r)=fact[n]*invFact[r]*invFact[n-r]
```

Each query becomes:

```text
O(1)
```

---

### C++ Code

```cpp
// See Code 4 — Many nCr Queries above.
// Use same engine for all such problems.
```

---

## Form 2 — Grid Paths Modulo MOD

### Problem

Find number of paths in large grid:

```text
1000 x 1000
```

Only right/down moves.

---

### Pattern

Use:

```cpp
(totalMoves) C (downMoves)
```

---

### Step-by-Step Working

```text
down = rows-1
right = cols-1
total = down+right
answer = total C down
```

Use nCr modulo prime.

---

### C++ Code

```cpp
// See Code 5 — Grid Paths Under MOD above.
```

---

## Form 3 — Team Selection With Categories

### Problem

Choose:

```text
2 boys from 5
1 girl from 4
```

---

### Pattern

Independent choices multiply:

```cpp
5C2 * 4C1
```

---

### Step-by-Step Working

```text
5C2 = 10
4C1 = 4
answer = 10 * 4 = 40
```

---

### C++ Code

```cpp
// See Code 6 — Count Teams With Constraints above.
```

---

## Form 4 — Count Subsets Of Size K Under MOD

### Problem

Array size:

```cpp
n = 100000
```

Count subsets of size:

```cpp
k = 500
```

---

### Pattern

Use:

```cpp
nCk % MOD
```

with factorial precomputation.

---

### Step-by-Step Working

```text
precompute fact up to 100000
answer = fact[n] * invFact[k] * invFact[n-k]
```

---

## Form 5 — Probability Under MOD

### Problem

Probability:

```text
favorable / total
```

under MOD.

---

### Pattern

Use modular inverse:

```cpp
favorable * inverse(total) % MOD
```

If favorable/total comes from combinations:

```text
use nCr for favorable and total
```

---

# 22. Real World Mapping

## Probability Engines

Combinatorics under modulo appears in probability simulations and counting systems.

---

## Analytics

Counting possible combinations of user events.

---

## ML Feature Selection

Choosing subsets of features.

---

## Distributed Systems

Quorum combinations and replica selection math.

---

## Competitive Programming

This is one of the most reused templates.

---

# 23. Practice Problems

Easy:

```text
1. nCr % MOD
2. Many nCr queries
3. Grid paths modulo MOD
4. Choose team categories
```

Medium:

```text
5. Count subsets
6. Probability modulo
7. DP with nCr transitions
8. Combinatorics with constraints
```

Hard:

```text
9. Lucas theorem
10. Stars and bars modulo
11. Inclusion-exclusion modulo
12. Combinatorial DP
```

---

# 24. Final Mental Summary

Normal:

```cpp
nCr = n! / (r!(n-r)!)
```

Modulo prime:

```cpp
nCr =
fact[n] * invFact[r] * invFact[n-r] % MOD
```

Why?

```text
division under MOD becomes multiplication by modular inverse
```

Core engine:

```text
fast power
→ modular inverse
→ factorial
→ inverse factorial
→ O(1) nCr query
```

---

# 25. Next Step

```text
012_Permutations_Repetition.md
```

Next intuition:

```text
Arrangements, order, repetition, and duplicate objects.
```
