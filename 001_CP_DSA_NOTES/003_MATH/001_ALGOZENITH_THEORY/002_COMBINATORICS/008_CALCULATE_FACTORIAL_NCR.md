# Factorial & nCr — Constraint-Based CP Guide

> **Goal:** Given constraints, quickly decide **how to calculate `n!` or `nCr`**, then implement the correct method.

---

# 1. Factorial

## Definition

For a non-negative integer `n`:

```text
n! = n × (n-1) × (n-2) × ... × 2 × 1
```

Also:

```text
0! = 1
1! = 1
```

### Meaning

`n!` is the number of ways to arrange `n` distinct objects.

Example:

```text
3 objects: A B C

ABC
ACB
BAC
BCA
CAB
CBA

Total = 6 = 3!
```

---

# 2. Factorial Modulo M

Often the exact factorial becomes huge, so the problem asks for:

```text
n! mod M
```

Example:

```text
5! = 120

120 mod 7 = 1
```

Instead of building a huge number first:

```text
ans = 1

i=2 → ans = 1×2 % 7 = 2
i=3 → ans = 2×3 % 7 = 6
i=4 → ans = 6×4 % 7 = 3
i=5 → ans = 3×5 % 7 = 1
```

The value stays small after every multiplication.

## C++ — Factorial Modulo

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;

int64 factorialMod(int n, int64 mod) {
    int64 ans = 1;

    for (int i = 2; i <= n; ++i) {
        ans = (ans * i) % mod;
    }

    return ans;
}

int main() {
    int n;
    cin >> n;

    const int64 MOD = 1'000'000'007LL;
    cout << factorialMod(n, MOD) << '\n';
}
```

Complexity:

```text
Time  : O(n)
Memory: O(1)
```

---

# 3. What Is nCr?

`nCr` means:

```text
choose r objects
from n objects
where order does NOT matter
```

Formula:

```text
          n!
C(n,r) = ---------
         r!(n-r)!
```

Example:

```text
C(5,2)

= 5! / (2!3!)

= (5×4)/(2×1)

= 10
```

---

# 4. Why Can We Use the Smaller of r and n-r?

A useful identity:

```text
C(n,r) = C(n,n-r)
```

Example:

```text
C(10,8) = C(10,2)
```

Choosing:

```text
8 people to TAKE
```

is equivalent to choosing:

```text
2 people to LEAVE
```

Therefore always consider:

```cpp
r = min(r, n - r);
```

This is especially useful when `n` is huge but `r` or `n-r` is small.

---

# 5. Constraint Decision Map

Use the constraints before choosing the implementation.

```text
Need nCr?
   |
   +-- Exact answer?
   |      |
   |      +-- small n / answer fits integer
   |             → multiplicative exact method
   |
   +-- Answer modulo M?
          |
          +-- M is prime?
          |      |
          |      +-- one/few queries,
          |      |   min(r,n-r) small
          |      |      → O(r) multiplicative + inverse
          |      |
          |      +-- many queries,
          |          max n manageable
          |             → precompute fact + invFact
          |
          +-- M may be composite?
                 |
                 +-- n small/moderate
                        → Pascal DP
```

The six common setups below follow this map.

---

# 6. Visual Model — What nCr Is Doing

Before choosing an implementation, keep this picture in mind:

```text
C(n,r)

Choose r items from n
(order does not matter)

Example: C(5,2)

Items:
[A] [B] [C] [D] [E]

Choose any 2:
AB AC AD AE
BC BD BE
CD CE
DE

Total = 10
```

Factorial formula:

```text
             n!
C(n,r) = -----------
          r!(n-r)!
```

For `C(5,2)`:

```text
             5!
C(5,2) = ----------
           2! × 3!

         5 × 4 × 3!
       = -----------
           2! × 3!

             cancel 3!
                  ↓

           5 × 4
       = ---------
           2 × 1

       = 20 / 2
       = 10
```

This cancellation is the key idea behind the **small-r multiplicative method**.

---

# 8. Setup 1 — One nCr, Manageable n, Prime Modulo

Suppose we need:

```text
C(n,r) mod 1e9+7
```

and `n` is small enough that computing factorials up to `n` for this query is acceptable.

Start from:

```text
          n!
C(n,r) = ---------
         r!(n-r)!
```

But under modulo we should **not** write:

```cpp
fact[n] / (fact[r] * fact[n-r])
```

Modular division is performed using a modular inverse.

For prime `MOD`:

```text
x⁻¹ ≡ x^(MOD-2) (mod MOD)
```

when `x` is not divisible by `MOD`.

So:

```text
C(n,r)
=
n! × (r!)⁻¹ × ((n-r)!)⁻¹
mod MOD
```

## Binary Exponentiation

```cpp
long long modPow(long long a, long long e, long long mod) {
    long long ans = 1;

    while (e > 0) {
        if (e & 1) {
            ans = ans * a % mod;
        }

        a = a * a % mod;
        e >>= 1;
    }

    return ans;
}
```

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;

const int64 MOD = 1'000'000'007LL;

int64 modPow(int64 a, int64 e) {
    int64 ans = 1;

    while (e > 0) {
        if (e & 1) ans = ans * a % MOD;
        a = a * a % MOD;
        e >>= 1;
    }

    return ans;
}

int64 modInverse(int64 x) {
    return modPow(x, MOD - 2);
}

int64 factorial(int n) {
    int64 ans = 1;

    for (int i = 2; i <= n; ++i) {
        ans = ans * i % MOD;
    }

    return ans;
}

int64 nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    int64 numerator = factorial(n);
    int64 denominator =
        factorial(r) * factorial(n - r) % MOD;

    return numerator * modInverse(denominator) % MOD;
}
```

### Dry Run — C(5,2)

```text
             5!
C(5,2) = ----------
           2! × 3!

fact(5) = 120
fact(2) =   2
fact(3) =   6

denominator
     │
     ▼
2 × 6 = 12

Normal arithmetic:
120 / 12 = 10

Modulo arithmetic:
120 × inverse(12) mod MOD
              │
              └── division becomes multiplication
                  by the modular inverse
```

So the code flow is:

```text
fact(n)
   │
   ├──────────────┐
   ▼              ▼
fact(r)       fact(n-r)
   │              │
   └──────×───────┘
          │
          ▼
      denominator
          │
       inverse()
          │
          ▼
fact(n) × inverse(denominator)
          │
          ▼
        answer
```

### Complexity

The factorial work is linear in `n`, plus one modular exponentiation:

```text
O(n + log MOD)
```

For repeated queries, do **not** recompute factorials each time; use Setup 5 or 6.

---

# 8. Setup 2 — Huge n, Small r, Prime Modulo

Suppose:

```text
n ≤ 10^9
r ≤ 20
MOD = 1e9+7
```

Computing:

```text
n!
```

would require up to `10^9` iterations.

Too slow.

Instead cancel `(n-r)!` algebraically:

```text
          n!
C(n,r) = ---------
         r!(n-r)!
```

Expand `n!`:

```text
n!
=
n(n-1)...(n-r+1)(n-r)!
```

Cancel `(n-r)!`:

```text
          n(n-1)...(n-r+1)
C(n,r) = -------------------
                   r!
```

Only `r` numerator terms remain.

---

## Dry Run — C(10,3)

First cancel the unused factorial:

```text
              10!
C(10,3) = -----------
            3! × 7!

          10×9×8×7!
        = -----------
            3! × 7!

             cancel 7!
                  ↓

          10 × 9 × 8
        = ------------
           1 × 2 × 3
```

Now only `r = 3` terms are processed:

```text
┌─────┬──────────────┬───────────┐
│  i  │ numerator    │ denominator│
├─────┼──────────────┼───────────┤
│  1  │ 1×10 = 10   │ 1×1 = 1   │
│  2  │ 10×9 = 90   │ 1×2 = 2   │
│  3  │ 90×8 = 720  │ 2×3 = 6   │
└─────┴──────────────┴───────────┘

          720
           │
           │ × inverse(6)
           ▼
          120
```

Key visual:

```text
n may be huge
    │
    ▼
DO NOT build n!
    │
    ▼
keep only r numerator terms
    │
    ▼
O(r) work
```

---

## C++

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;

const int64 MOD = 1'000'000'007LL;

int64 modPow(int64 a, int64 e) {
    int64 ans = 1;

    while (e > 0) {
        if (e & 1) ans = ans * a % MOD;
        a = a * a % MOD;
        e >>= 1;
    }

    return ans;
}

int64 modInverse(int64 x) {
    return modPow(x, MOD - 2);
}

int64 nCrSmallR(int64 n, int64 r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);

    int64 numerator = 1;
    int64 denominator = 1;

    for (int64 i = 1; i <= r; ++i) {
        numerator = numerator * ((n - i + 1) % MOD) % MOD;
        denominator = denominator * (i % MOD) % MOD;
    }

    return numerator * modInverse(denominator) % MOD;
}
```

### Complexity

```text
building products : O(min(r,n-r))
inverse           : O(log MOD)
```

So:

```text
O(min(r,n-r) + log MOD)
```

### Important Condition

This simple inverse approach requires the denominator to be invertible modulo `MOD`.

For the common setup:

```text
MOD = 1e9+7
r < MOD
```

this condition holds.

---

# 9. Setup 3 — Exact nCr for Small Values

Sometimes we need the **exact integer**, not a modular answer.

Use:

```text
          n(n-1)...(n-r+1)
C(n,r) = -------------------
                   r!
```

We can multiply and divide incrementally.

## C++

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;

int64 exactNCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);

    int64 ans = 1;

    for (int i = 1; i <= r; ++i) {
        ans = ans * (n - i + 1) / i;
    }

    return ans;
}

int main() {
    cout << exactNCr(10, 3) << '\n';
}
```

## Dry Run — C(5,2)

```text
C(5,2)
=
5×4
---
2×1
```

Process one numerator/denominator pair at a time:

```text
start
ans = 1
   │
   │ i=1: ×5 /1
   ▼
ans = 5
   │
   │ i=2: ×4 /2
   ▼
ans = 10
```

Trace:

```text
┌─────┬─────────────┬────────┐
│  i  │ operation   │ ans    │
├─────┼─────────────┼────────┤
│  1  │ 1×5 / 1    │ 5      │
│  2  │ 5×4 / 2    │ 10     │
└─────┴─────────────┴────────┘
```

Final:

```text
C(5,2) = 10
```

The intermediate quotient is integral for this recurrence.

### Important

Do not memorize "`n ≤ 40` means every nCr fits in `long long`."

The actual requirement is:

```text
all intermediate values and the final answer
must fit the chosen integer type
```

For example, `40!` does **not** fit in `long long`, even though many combinations with `n=40` do.

For larger exact answers, use a big-integer type/library.

---

# 10. Setup 4 — Composite / Arbitrary Modulo, n ≤ 1000

Suppose:

```text
C(n,r) mod 10^9
```

Here:

```text
MOD = 10^9
```

is composite.

Fermat's inverse formula:

```text
x^(MOD-2)
```

does not generally apply.

Instead use Pascal's identity:

```text
C(n,r)
=
C(n-1,r)
+
C(n-1,r-1)
```

---

## Why Does Pascal's Identity Work?

Choose `r` objects from `n`.

Focus on one particular object.

There are two disjoint cases:

```text
Case 1:
take it
↓
choose remaining r-1
from n-1

→ C(n-1,r-1)
```

```text
Case 2:
do not take it
↓
choose all r
from n-1

→ C(n-1,r)
```

Therefore:

```text
C(n,r)
=
C(n-1,r-1) + C(n-1,r)
```

---

## Small Dry Run — Pascal Triangle

Every inner cell comes from the **two cells above it**:

```text
                 1
              1     1
           1     2     1
        1     3     3     1
     1     4     6     4     1
                  ↑
                3 + 3
```

So:

```text
C(4,2)
   ▲
   │
   ├── C(3,1) = 3
   │
   └── C(3,2) = 3

C(4,2) = 3 + 3 = 6
```

Decision interpretation:

```text
Choose 2 from 4
       │
       ▼
focus on one item
   /             TAKE IT        SKIP IT
   │               │
choose 1 of 3   choose 2 of 3
   │               │
 C(3,1)          C(3,2)
   \               /
    ------ + ------
           │
           ▼
           6
```

---

## C++ — O(nr) DP

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;

int64 nCrAnyModulo(int n, int r, int64 mod) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);

    vector<int64> dp(r + 1);
    dp[0] = 1;

    for (int i = 1; i <= n; ++i) {
        for (int j = min(i, r); j >= 1; --j) {
            dp[j] = (dp[j] + dp[j - 1]) % mod;
        }
    }

    return dp[r];
}
```

### Why Iterate `j` Backwards?

We need values from the previous Pascal row.

If we iterate forward, we would overwrite values before using them.

```text
backwards
→ old dp[j-1] is still available
```

### Complexity

```text
Time  : O(nr)
Memory: O(r)
```

No modular division is required, so this works with prime or composite modulus.

---

# 11. Setup 5 — Many Queries, Prime Modulo

Suppose:

```text
q ≤ 10^6
n,r ≤ 10^6
MOD = 1e9+7
```

Recomputing factorials for every query is wasteful.

Precompute once:

```text
fact[i] = i! mod MOD
```

Then:

```text
C(n,r)
=
fact[n]
× inverse(fact[r] × fact[n-r])
mod MOD
```

---

## Factorial Precomputation Dry Run

For `MAXN = 5`:

```text
fact[0] = 1
    │ ×1
    ▼
fact[1] = 1
    │ ×2
    ▼
fact[2] = 2
    │ ×3
    ▼
fact[3] = 6
    │ ×4
    ▼
fact[4] = 24
    │ ×5
    ▼
fact[5] = 120
```

For a query such as `C(5,2)`:

```text
fact[5] ───────────────┐
                       │
fact[2] ──┐            │
          ├─ multiply  │
fact[3] ──┘            │
      │                │
      ▼                │
 denominator           │
      │                │
   inverse             │
      │                │
      └──────×─────────┘
             │
             ▼
           C(5,2)
```

The expensive factorial construction is done **once**, not once per query.

---

## C++

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;

const int MAXN = 1'000'000;
const int64 MOD = 1'000'000'007LL;

vector<int64> fact(MAXN + 1);

int64 modPow(int64 a, int64 e) {
    int64 ans = 1;

    while (e > 0) {
        if (e & 1) ans = ans * a % MOD;
        a = a * a % MOD;
        e >>= 1;
    }

    return ans;
}

int64 modInverse(int64 x) {
    return modPow(x, MOD - 2);
}

void precomputeFactorials() {
    fact[0] = 1;

    for (int i = 1; i <= MAXN; ++i) {
        fact[i] = fact[i - 1] * i % MOD;
    }
}

int64 nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    int64 denominator =
        fact[r] * fact[n - r] % MOD;

    return fact[n] * modInverse(denominator) % MOD;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    precomputeFactorials();

    int q;
    cin >> q;

    while (q--) {
        int n, r;
        cin >> n >> r;

        cout << nCr(n, r) << '\n';
    }
}
```

### Complexity

Precomputation:

```text
O(MAXN)
```

Each query:

```text
O(log MOD)
```

because one modular inverse is calculated using binary exponentiation.

We can make each query `O(1)` with Setup 6.

---

# 12. Setup 6 — Many Queries in O(1)

Precompute both:

```text
fact[i]    = i!
invFact[i] = (i!)⁻¹
```

Then:

```text
C(n,r)
=
fact[n]
× invFact[r]
× invFact[n-r]
mod MOD
```

Every required value is already available.

---

# 13. How Do We Precompute invFact?

First compute:

```text
fact[0], fact[1], ..., fact[MAXN]
```

Then calculate only one expensive inverse:

```text
invFact[MAXN]
=
inverse(fact[MAXN])
```

Now derive the rest backwards.

Since:

```text
i! = i × (i-1)!
```

take modular inverses:

```text
(i!)⁻¹
=
i⁻¹ × ((i-1)!)⁻¹
```

Multiply both sides by `i`:

```text
((i-1)!)⁻¹
=
i × (i!)⁻¹
```

Therefore:

```text
invFact[i-1]
=
i × invFact[i] mod MOD
```

This lets us fill the entire inverse-factorial array backwards in `O(n)`.

---

## Small Dry Run — Why We Move Backwards

First the factorial array goes **forward**:

```text
fact[0]
   │ ×1
   ▼
fact[1]
   │ ×2
   ▼
fact[2]
   │ ×3
   ▼
fact[3]
   │ ×4
   ▼
fact[4]
   │ ×5
   ▼
fact[5]
```

Calculate only one expensive inverse:

```text
invFact[5] = inverse(fact[5])
```

Then walk **backwards**:

```text
invFact[5]
    │ ×5
    ▼
invFact[4]
    │ ×4
    ▼
invFact[3]
    │ ×3
    ▼
invFact[2]
    │ ×2
    ▼
invFact[1]
    │ ×1
    ▼
invFact[0]
```

Why does one step work?

```text
5! = 5 × 4!

Take reciprocals:

1/4! = 5/5!

Therefore:

invFact[4]
=
5 × invFact[5]
```

General rule:

```text
invFact[i-1]
=
i × invFact[i] mod MOD
```

So we pay for only **one** binary-exponentiation inverse.

---

# 14. C++ — Standard Competitive Programming Template

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;

const int MAXN = 1'000'000;
const int64 MOD = 1'000'000'007LL;

vector<int64> fact(MAXN + 1);
vector<int64> invFact(MAXN + 1);

int64 modPow(int64 a, int64 e) {
    int64 ans = 1;

    while (e > 0) {
        if (e & 1) {
            ans = ans * a % MOD;
        }

        a = a * a % MOD;
        e >>= 1;
    }

    return ans;
}

void precompute() {
    fact[0] = 1;

    for (int i = 1; i <= MAXN; ++i) {
        fact[i] = fact[i - 1] * i % MOD;
    }

    invFact[MAXN] = modPow(fact[MAXN], MOD - 2);

    for (int i = MAXN; i >= 1; --i) {
        invFact[i - 1] = invFact[i] * i % MOD;
    }
}

int64 nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    return fact[n]
           * invFact[r] % MOD
           * invFact[n - r] % MOD;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    precompute();

    int q;
    cin >> q;

    while (q--) {
        int n, r;
        cin >> n >> r;

        cout << nCr(n, r) << '\n';
    }
}
```

### Complexity

Precomputation:

```text
factorials         → O(MAXN)
one inverse        → O(log MOD)
inverse factorials → O(MAXN)
```

Overall:

```text
O(MAXN + log MOD)
```

Each query:

```text
O(1)
```

Memory:

```text
O(MAXN)
```

This is the standard setup when there are many `nCr` queries under a suitable prime modulus.

---

# 15. Important Modulo Condition

The factorial + inverse-factorial method above assumes the required factorial values are invertible modulo `MOD`.

For the common CP setup:

```text
MOD = 1e9+7
MAXN < MOD
```

we have:

```text
fact[i] ≠ 0 (mod MOD)
```

for all precomputed `i`, so the inverses exist.

But if:

```text
n ≥ MOD
```

then:

```text
n!
```

contains `MOD` as a factor, so:

```text
n! ≡ 0 (mod MOD)
```

and you cannot simply invert that factorial.

So:

```text
huge n ≥ prime MOD
```

may require a different technique depending on the problem.

---

# 16. Which Method Should I Use?

| Constraints / Setup | Best Basic Method | Cost |
|---|---|---:|
| Need `n! mod M` | iterative factorial | `O(n)` |
| One/few `nCr`, manageable `n`, prime MOD | factorial + inverse | `O(n + log MOD)` |
| Huge `n`, small `r`, prime MOD and denominator invertible | multiplicative formula | `O(min(r,n-r)+log MOD)` |
| Exact `nCr`, value fits integer type | multiplicative exact | `O(min(r,n-r))` |
| Composite/arbitrary MOD, `n,r` moderate | Pascal DP | `O(nr)` |
| Many queries, prime MOD | precompute factorials | `O(log MOD)` / query |
| Many queries, prime MOD, fastest | `fact + invFact` | `O(1)` / query |

---

# 17. Common Mistakes

## Mistake 1 — Ordinary Division Under Modulo

Wrong:

```cpp
answer = numerator / denominator;
```

For modular arithmetic, division requires an inverse when that inverse exists:

```text
a / b mod M
=
a × b⁻¹ mod M
```

---

## Mistake 2 — Fermat Inverse with Composite Modulus

This:

```text
a⁻¹ = a^(MOD-2)
```

is the Fermat method for a **prime modulus** under the required non-divisibility condition.

Do not blindly use it for:

```text
MOD = 10^9
```

because `10^9` is composite.

---

## Mistake 3 — Computing n! When n Is Huge

If:

```text
n = 10^9
r = 10
```

do not iterate to `n`.

Use:

```text
n(n-1)...(n-r+1) / r!
```

so only about `r` terms are processed.

---

## Mistake 4 — Forgetting Invalid r

Always handle:

```text
r < 0
or
r > n
```

as:

```text
C(n,r) = 0
```

---

## Mistake 5 — Forgetting Symmetry

Before an `O(r)` calculation:

```cpp
r = min(r, n - r);
```

Example:

```text
C(1,000,000, 999,998)
=
C(1,000,000, 2)
```

Huge reduction in work.

---

# 18. Final Contest Memory Card

```text
FIRST READ CONSTRAINTS
        ↓
Need exact or modulo?
        ↓
Is modulo prime?
        ↓
One query or many queries?
        ↓
How large are n and r?
```

### Case A — Huge n, tiny r

```text
cancel (n-r)!
      ↓
only r numerator terms
      ↓
O(r)
```

### Case B — Many queries + prime MOD

```text
precompute fact[]
precompute invFact[]
        ↓
C(n,r)
=
fact[n]
× invFact[r]
× invFact[n-r]
        ↓
O(1) per query
```

### Case C — Composite / arbitrary MOD

```text
modular inverse may not exist
        ↓
if n is moderate
        ↓
Pascal DP
```

### Case D — Exact small answer

```text
r = min(r,n-r)

ans = 1

for i = 1..r:
    ans *= n-i+1
    ans /= i
```

---

# 19. One-Screen Constraint Flow

```text
                    Need C(n,r)
                        │
                        ▼
               Exact or modulo?
                /             \
             EXACT           MODULO
               │                │
               ▼                ▼
       Does answer fit?      Is MOD prime?
          │                     /    \
         YES                  YES     NO / unsure
          │                    │          │
          ▼                    │          ▼
 multiplicative exact          │      n moderate?
 O(min(r,n-r))                 │          │
                               │         YES
                               │          ▼
                               │      Pascal DP
                               │
                     one/few or many queries?
                         /             \
                    ONE / FEW          MANY
                       │                 │
            n huge, small r?            ▼
                /       \          precompute
              YES       NO         fact + invFact
               │         │              │
               ▼         ▼              ▼
          O(r) product  factorial     O(1)/query
          + inverse     + inverse
```

> This is the main contest decision: **constraints choose the implementation.**

---

# 20. Formula Sheet

```text
FACTORIAL

n!
=
n(n-1)...1
```

```text
COMBINATION

C(n,r)
=
n! / [r!(n-r)!]
```

```text
SYMMETRY

C(n,r)
=
C(n,n-r)
```

```text
MULTIPLICATIVE FORM

C(n,r)
=
n(n-1)...(n-r+1) / r!
```

```text
PASCAL

C(n,r)
=
C(n-1,r)
+
C(n-1,r-1)
```

```text
PRIME MOD INVERSE

a⁻¹
≡
a^(MOD-2)
(mod MOD)
```

```text
FAST MANY-QUERY nCr

C(n,r)
=
fact[n]
× invFact[r]
× invFact[n-r]
(mod MOD)
```

> **Contest habit:** Do not choose the `nCr` implementation from the formula alone. **Choose it from the constraints.**
