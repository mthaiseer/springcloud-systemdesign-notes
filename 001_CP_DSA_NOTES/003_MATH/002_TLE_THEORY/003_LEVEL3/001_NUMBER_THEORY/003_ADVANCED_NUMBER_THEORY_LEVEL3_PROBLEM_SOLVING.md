# Advanced Number Theory — Level 3
## Problem Solving — Reducing Fractions, Valuable Cards & Divisor Analysis

> **Goal:** derive the idea, then code it.
>
> **Per problem:** **what it asks → observation → derivation → step-by-step dry run → C++ → complexity → recognition model**.
>
> Display equations use fenced `math` blocks to avoid LaTeX rendering errors.

---

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Reducing Fractions — CF 222C](#1-reducing-fractions--cf-222c)
- [2. Valuable Cards — CF 1992F](#2-valuable-cards--cf-1992f)
- [3. Divisor Analysis — CSES 2182](#3-divisor-analysis--cses-2182)
- [4. Final Recognition Sheet](#4-final-recognition-sheet)

---

# 0. Prerequisites

## 0.1 Prime Factorisation

Every integer greater than `1` can be written as:

```math
N=p_1^{e_1}p_2^{e_2}\cdots p_k^{e_k}
```

Example:

```text
100 = 2² × 5²
50  = 2 × 5²
```

Think:

```text
prime → exponent
```

---

## 0.2 GCD in Prime-Exponent Form

For:

```math
A=\prod p^{x_p}
```

and:

```math
B=\prod p^{y_p}
```

the GCD keeps the smaller exponent:

```math
\gcd(A,B)
=
\prod p^{\min(x_p,y_p)}
```

Example:

```text
100 = 2² × 5²
50  = 2¹ × 5²

gcd:
2^min(2,1) × 5^min(2,2)
= 2 × 25
= 50
```

---

## 0.3 SPF — Smallest Prime Factor

`spf[x]` is the smallest prime dividing `x`.

Examples:

```text
spf[12] = 2
spf[15] = 3
spf[25] = 5
spf[17] = 17
```

After precomputation:

```text
x
↓
p = spf[x]
↓
divide by p
↓
repeat
```

Build:

```cpp
vector<int> buildSPF(int n) {
    vector<int> spf(n + 1);

    for (int i = 0; i <= n; ++i)
        spf[i] = i;

    for (long long p = 2; p * p <= n; ++p) {
        if (spf[p] != p)
            continue;

        for (long long j = p * p; j <= n; j += p) {
            if (spf[j] == j)
                spf[j] = (int)p;
        }
    }

    return spf;
}
```

---

## 0.4 Divisors as Exponent Choices

If:

```math
N=p_1^{e_1}\cdots p_k^{e_k}
```

then every divisor is:

```math
d=p_1^{f_1}\cdots p_k^{f_k}
```

with:

```text
0 <= f_i <= e_i
```

Example:

```text
12 = 2² × 3

2 exponent: 0,1,2
3 exponent: 0,1

divisors:
1,2,3,4,6,12
```

---

## 0.5 Subset-Product State

For a segment, maintain products achievable by selecting some elements.

Initially:

```text
reachable = {1}
```

When new value `a` arrives:

```text
for every old product d:
    d × a can become reachable
```

For **Valuable Cards**, only products dividing target `x` matter.

---

## 0.6 Modular Tools

For CSES:

```text
MOD = 1e9+7
```

Binary exponentiation:

```cpp
long long binpow(long long a, long long b, long long mod) {
    long long ans = 1;
    a %= mod;

    while (b > 0) {
        if (b & 1)
            ans = (__int128)ans * a % mod;

        a = (__int128)a * a % mod;
        b >>= 1;
    }

    return ans;
}
```

Prime-modulus inverse:

```math
a^{-1}\equiv a^{MOD-2}\pmod{MOD}
```

Exponent cycle:

```math
a^E\equiv a^{E\bmod(MOD-1)}\pmod{MOD}
```

when Fermat applies.

---

# 1. Reducing Fractions — CF 222C

**Problem:** https://codeforces.com/contest/222/problem/C

---

## 1.1 What It Asks

Arrays:

```text
a1 ... an
b1 ... bm
```

represent:

```math
\frac{a_1a_2\cdots a_n}
     {b_1b_2\cdots b_m}
```

Reduce the fraction and output valid numerator/denominator arrays.

Constraints include:

```text
n,m <= 1e5
a[i],b[i] <= 1e7
```

The products are far too large to store.

---

## 1.2 Observation

Ordinary reduction is:

```text
G = gcd(numerator, denominator)

numerator   /= G
denominator /= G
```

But we cannot construct the products.

So:

```text
represent each product by total prime exponents
```

---

## 1.3 Derivation

Let:

```text
A[p] = total exponent of prime p in numerator
B[p] = total exponent of prime p in denominator
```

Then common factor contains:

```math
C[p]=\min(A[p],B[p])
```

copies of `p`.

Therefore:

```text
1. SPF-factorise every input value.
2. Accumulate numerator/denominator exponents.
3. common[p] = min(A[p],B[p]).
4. Remove exactly common[p] copies from numerator.
5. Remove the same number from denominator.
```

No huge product is ever created.

---

## 1.4 Step-by-Step Dry Run

```text
A = [100,5,2]
B = [50,10]
```

Factor numerator:

```text
100 = 2² × 5²
5   = 5
2   = 2

total:
2 → 3
5 → 3
```

Factor denominator:

```text
50 = 2 × 5²
10 = 2 × 5

total:
2 → 2
5 → 3
```

Common:

```text
2 → min(3,2) = 2
5 → min(3,3) = 3
```

So common product is:

```math
2^2\cdot5^3=500
```

After cancellation, one valid representation is:

```text
numerator:
[2,1,1]

denominator:
[1,1]
```

giving:

```text
2 / 1
```

---

## 1.5 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> buildSPF(int n) {
    vector<int> spf(n + 1);

    for (int i = 0; i <= n; ++i)
        spf[i] = i;

    for (long long p = 2; p * p <= n; ++p) {
        if (spf[p] != p)
            continue;

        for (long long j = p * p; j <= n; j += p) {
            if (spf[j] == j)
                spf[j] = (int)p;
        }
    }

    return spf;
}

void addCounts(
    int x,
    const vector<int>& spf,
    vector<int>& cnt
) {
    while (x > 1) {
        int p = spf[x];
        int e = 0;

        while (x % p == 0) {
            x /= p;
            ++e;
        }

        cnt[p] += e;
    }
}

void cancel(
    vector<int>& arr,
    const vector<int>& spf,
    vector<int>& remain
) {
    for (int& value : arr) {
        int x = value;

        while (x > 1) {
            int p = spf[x];
            int e = 0;

            while (x % p == 0) {
                x /= p;
                ++e;
            }

            int take = min(e, remain[p]);

            for (int c = 0; c < take; ++c)
                value /= p;

            remain[p] -= take;
        }
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<int> a(n), b(m);
    int maxV = 1;

    for (int& x : a) {
        cin >> x;
        maxV = max(maxV, x);
    }

    for (int& x : b) {
        cin >> x;
        maxV = max(maxV, x);
    }

    vector<int> spf = buildSPF(maxV);

    vector<int> num(maxV + 1);
    vector<int> den(maxV + 1);
    vector<int> common(maxV + 1);

    for (int x : a) addCounts(x, spf, num);
    for (int x : b) addCounts(x, spf, den);

    for (int p = 2; p <= maxV; ++p)
        common[p] = min(num[p], den[p]);

    cancel(a, spf, common);

    // Restore same common factor for denominator.
    for (int p = 2; p <= maxV; ++p)
        common[p] = min(num[p], den[p]);

    cancel(b, spf, common);

    cout << n << ' ' << m << '\n';

    for (int x : a) cout << x << ' ';
    cout << '\n';

    for (int x : b) cout << x << ' ';
    cout << '\n';
}
```

---

## 1.6 Complexity

Let:

```text
V = max(a[i], b[i])
```

SPF:

```text
O(V log log V)
```

Factor/cancel work:

```text
proportional to total prime factors
```

Memory:

```text
O(V)
```

---

## 1.7 Recognition Model

```text
Huge product / huge fraction
        |
        v
cannot store product
        |
        v
store prime exponents
        |
        v
GCD exponent = minimum
        |
        v
cancel prime copies
```

**Memory anchor:** huge products → prime-exponent space.

---

# 2. Valuable Cards — CF 1992F

**Problem:** https://codeforces.com/contest/1992/problem/F

---

## 2.1 What It Asks

Split array into the minimum number of segments.

A segment is **bad** if no subset of its cards has product exactly:

```text
x
```

Every final segment must be bad.

---

## 2.2 Observation

Greedy:

```text
keep the current bad segment as long as possible
```

The first time adding a card makes product `x` reachable:

```text
cut before that card
```

and start the next segment with it.

---

### Only divisors of x matter

If:

```text
x % a[i] != 0
```

then `a[i]` cannot participate in a positive-integer subset product equal to `x`.

So ignore it in the subset-product state.

---

## 2.3 Derivation

Maintain:

```text
reachable divisors of x
```

Initially:

```text
{1}
```

For new value `a`:

```text
for every OLD reachable d:
    if d*a divides x:
        d*a becomes reachable
```

Use only old states so the same card is not used twice.

If:

```text
x becomes reachable
```

then the segment including the current card is no longer bad.

So:

```text
answer++
reset state
start new segment from current card
```

---

## 2.4 Step-by-Step Dry Run

```text
x = 4
a = [2,3,6,2,1,2]
```

Start:

```text
answer = 1
reachable = {1}
```

### `2`

```text
1×2 = 2
```

State:

```text
{1,2}
```

No `4`.

---

### `3`

```text
3 does not divide 4
```

Ignore.

---

### `6`

```text
6 does not divide 4
```

Ignore.

---

### next `2`

Old states:

```text
1,2
```

Products:

```text
1×2 = 2
2×2 = 4
```

`4` becomes reachable.

Cut before this card:

```text
answer = 2
```

New segment begins with current `2`:

```text
reachable = {1,2}
```

---

### `1`

No useful new state.

---

### final `2`

Again:

```text
2×2 = 4
```

Cut:

```text
answer = 3
```

Final answer:

```text
3
```

---

## 2.5 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int n, x;
        cin >> n >> x;

        vector<int> a(n);
        for (int& v : a) cin >> v;

        vector<char> isDiv(x + 1, false);

        for (int d = 1; 1LL * d * d <= x; ++d) {
            if (x % d != 0)
                continue;

            isDiv[d] = true;
            isDiv[x / d] = true;
        }

        vector<char> used(x + 1, false);
        vector<int> reachable{1};

        used[1] = true;

        int answer = 1;

        for (int value : a) {
            if (x % value != 0)
                continue;

            int oldSize = reachable.size();
            vector<int> added;

            bool reachesX = false;

            for (int i = 0; i < oldSize; ++i) {
                long long prod =
                    1LL * reachable[i] * value;

                if (prod > x)
                    continue;

                int v = (int)prod;

                if (!isDiv[v])
                    continue;

                if (!used[v]) {
                    used[v] = true;
                    added.push_back(v);
                }

                if (v == x)
                    reachesX = true;
            }

            for (int v : added)
                reachable.push_back(v);

            if (reachesX) {
                ++answer;

                for (int v : reachable)
                    used[v] = false;

                reachable.clear();
                reachable.push_back(1);
                used[1] = true;

                if (value != 1) {
                    reachable.push_back(value);
                    used[value] = true;
                }
            }
        }

        cout << answer << '\n';
    }
}
```

---

## 2.6 Complexity

Let:

```text
tau(x) = number of divisors of x
```

Reachable states are only divisors of `x`.

Time:

```text
O(n × tau(x))
```

with the boolean-state implementation.

Memory:

```text
O(x)
```

---

## 2.7 Recognition Model

```text
Minimum valid segments
        |
        v
extend current segment greedily
        |
        v
track forbidden subset-product state
        |
        v
only divisors of x matter
        |
        v
x becomes reachable?
        |
       YES
        |
        v
cut before current card
```

**Memory anchor:** cut exactly when the forbidden state first appears.

---

# 3. Divisor Analysis — CSES 2182

**Problem:** https://cses.fi/problemset/task/2182

---

## 3.1 What It Asks

Input gives:

```math
N=p_1^{k_1}\cdots p_m^{k_m}
```

Compute modulo `1e9+7`:

```text
1. number of divisors
2. sum of divisors
3. product of divisors
```

---

## 3.2 Observation

A divisor is created by independently choosing an exponent:

```text
0 ... k_i
```

for every prime.

That one model gives all three answers.

---

## 3.3 Derivation

### Number of divisors

Prime `p_i^k_i` gives:

```text
k_i + 1
```

choices.

Therefore:

```math
D(N)=\prod_i(k_i+1)
```

---

### Sum of divisors

For one prime:

```text
1,p,p²,...,p^k
```

Sum:

```math
1+p+\cdots+p^k
=
\frac{p^{k+1}-1}{p-1}
```

So:

```math
S(N)
=
\prod_i
\frac{p_i^{k_i+1}-1}{p_i-1}
```

Use modular inverse for `(p-1)`.

---

### Product of divisors

Maintain:

```text
C = current divisor count
P = current product of divisors
```

Add new prime power:

```text
p^k
```

Old divisor set appears:

```text
k+1 times
```

so contributes:

```math
P^{k+1}
```

Total exponent of `p`:

```math
C(0+1+\cdots+k)
=
C\frac{k(k+1)}2
```

Therefore:

```math
P_{\text{new}}
=
P_{\text{old}}^{k+1}
\cdot
p^{C_{\text{old}}k(k+1)/2}
```

Update count:

```math
C_{\text{new}}
=
C_{\text{old}}(k+1)
```

Huge exponents are reduced modulo:

```text
MOD - 1
```

when used in powers modulo prime `MOD`.

---

## 3.4 Step-by-Step Dry Run — N = 12

```text
12 = 2² × 3
```

### Count

```text
(2+1)(1+1)
= 3×2
= 6
```

---

### Sum

```text
(1+2+4)(1+3)
= 7×4
= 28
```

---

### Product

Start:

```text
C = 1
P = 1
```

Add `2²`:

```text
P = 1³ × 2^(1×3)
  = 8

C = 1×3
  = 3
```

Add `3¹`:

```text
P = 8² × 3^(3×1)
  = 64 × 27
  = 1728
```

Final:

```text
count   = 6
sum     = 28
product = 1728
```

---

## 3.5 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;
using i128 = __int128_t;

const int64 MOD = 1'000'000'007LL;
const int64 EXP_MOD = MOD - 1;

int64 binpow(int64 a, int64 b, int64 mod) {
    int64 ans = 1;
    a %= mod;

    while (b > 0) {
        if (b & 1)
            ans = (i128)ans * a % mod;

        a = (i128)a * a % mod;
        b >>= 1;
    }

    return ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    int64 numDiv = 1;
    int64 sumDiv = 1;
    int64 prodDiv = 1;

    int64 countExp = 1;

    while (n--) {
        int64 p, k;
        cin >> p >> k;

        // Count
        numDiv =
            (i128)numDiv * (k + 1) % MOD;

        // Sum
        int64 numerator =
            (binpow(p, k + 1, MOD) - 1 + MOD) % MOD;

        int64 inverse =
            binpow(p - 1, MOD - 2, MOD);

        int64 gp =
            (i128)numerator * inverse % MOD;

        sumDiv =
            (i128)sumDiv * gp % MOD;

        // Product
        int64 triangular =
            (i128)k * (k + 1) / 2 % EXP_MOD;

        int64 exponent =
            (i128)countExp * triangular % EXP_MOD;

        prodDiv =
            (i128)binpow(prodDiv, k + 1, MOD)
            * binpow(p, exponent, MOD)
            % MOD;

        countExp =
            (i128)countExp
            * ((k + 1) % EXP_MOD)
            % EXP_MOD;
    }

    cout << numDiv << ' '
         << sumDiv << ' '
         << prodDiv << '\n';
}
```

---

## 3.6 Complexity

For `n` distinct input primes:

```text
O(n log MOD)
```

because each factor performs a constant number of binary exponentiations.

Memory:

```text
O(1)
```

---

## 3.7 Recognition Model

```text
Input already in prime powers
        |
        v
divisor = independent exponent choices
        |
   +----+----+----+
   |         |    |
 count      sum product
   |         |    |
(k+1)       GP   recurrence
```

**Memory anchor:** prime exponents turn divisor problems into independent choices.

---

# 4. Final Recognition Sheet

| Signal | Think |
|---|---|
| huge products cannot be stored | prime-exponent representation |
| reduce fraction of products | minimum common prime exponents |
| many values `<= 1e7` need factorisation | SPF |
| forbidden subset product | subset-product states |
| only product `x` matters | keep only divisors of `x` |
| minimum segments | greedy: extend until invalid |
| number given as prime powers | work directly with exponents |
| count divisors | product of `(k+1)` |
| sum divisors | geometric progression |
| product divisors | contribution recurrence |
| huge exponent modulo prime | reduce exponent modulo `MOD-1` |

---

# Final Mental Map

```text
REDUCING FRACTIONS
huge products
→ prime exponents
→ min exponents
→ cancel


VALUABLE CARDS
bad segment
→ reachable divisor-products
→ x reached?
→ cut


DIVISOR ANALYSIS
prime powers
→ exponent choices
→ count / sum / product
```

> **Final memory anchor:** when the real product is too large, move to **prime exponents** or **divisor states**.
