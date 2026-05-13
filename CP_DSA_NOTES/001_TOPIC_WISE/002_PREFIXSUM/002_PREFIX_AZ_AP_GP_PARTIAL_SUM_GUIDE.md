# AP & GP on Prefix Sum / Partial Sum — Formula-to-Solution Guide

> Focus topics:
>
> 1. AP on Prefix Sum
> 2. GP on Prefix Sum
> 3. AP on Partial Sum
> 4. GP on Partial Sum
>
> Goal: Do not memorize. Learn how to derive the formula and convert it into code.

---

# Clickable Index

## 0. Core Mental Model
- [0.1 What AP/GP Means](#01-what-apgp-means)
- [0.2 Prefix Sum vs Partial Sum](#02-prefix-sum-vs-partial-sum)
- [0.3 Formula-to-Code Thinking](#03-formula-to-code-thinking)

## Part A — AP on Prefix Sum
- [1. AP Weighted Range Sum Query](#1-ap-weighted-range-sum-query)
- [2. AP Query Formula Derivation](#2-ap-query-formula-derivation)
- [3. AP Prefix Sum Code](#3-ap-prefix-sum-code)
- [4. AP Prefix Sum Index-by-Index Dry Run](#4-ap-prefix-sum-index-by-index-dry-run)

## Part B — GP on Prefix Sum
- [5. GP Weighted Range Sum Query](#5-gp-weighted-range-sum-query)
- [6. GP Query Formula Derivation](#6-gp-query-formula-derivation)
- [7. GP Prefix Sum Code](#7-gp-prefix-sum-code)
- [8. GP Prefix Sum Index-by-Index Dry Run](#8-gp-prefix-sum-index-by-index-dry-run)

## Part C — AP on Partial Sum
- [9. AP Range Update Problem](#9-ap-range-update-problem)
- [10. AP Partial Sum Formula Derivation](#10-ap-partial-sum-formula-derivation)
- [11. AP Partial Sum Code](#11-ap-partial-sum-code)
- [12. AP Partial Sum Index-by-Index Dry Run](#12-ap-partial-sum-index-by-index-dry-run)

## Part D — GP on Partial Sum
- [13. GP Range Update Problem](#13-gp-range-update-problem)
- [14. GP Partial Sum Formula Derivation](#14-gp-partial-sum-formula-derivation)
- [15. GP Partial Sum Code](#15-gp-partial-sum-code)
- [16. GP Partial Sum Index-by-Index Dry Run](#16-gp-partial-sum-index-by-index-dry-run)

## Final Revision
- [17. Recognition Table](#17-recognition-table)
- [18. Formula Summary](#18-formula-summary)
- [19. Contest Templates](#19-contest-templates)

---

# 0. Core Mental Model

## 0.1 What AP/GP Means

### Arithmetic Progression

Values increase by constant difference `d`.

```text
a, a+d, a+2d, a+3d, ...
```

Example:

```text
3, 5, 7, 9
a = 3, d = 2
```

### Geometric Progression

Values multiply by constant ratio `r`.

```text
a, a*r, a*r^2, a*r^3, ...
```

Example:

```text
2, 6, 18, 54
a = 2, r = 3
```

---

## 0.2 Prefix Sum vs Partial Sum

### Prefix Sum

Use when array is fixed and you need many queries.

```text
Given array a[]
Answer sum over l..r quickly.
```

### Partial Sum / Difference Array

Use when array starts empty or existing values need many range updates.

```text
Apply many updates quickly.
Build final array later.
```

Normal difference array handles constant update:

```text
add x on [l, r]
```

AP/GP partial sum handles pattern update:

```text
add AP on [l, r]
add GP on [l, r]
```

---

## 0.3 Formula-to-Code Thinking

Do not memorize.

Always ask:

```text
1. What is the value at index i?
2. Can I rewrite it using i?
3. Can I split it into known prefix arrays?
4. Can I mark start/stop using difference arrays?
```

---

# Part A — AP on Prefix Sum

---

# 1. AP Weighted Range Sum Query

## Problem Statement

Given an array `a[]`.

For each query `[l, r]`, compute:

```text
a[l] * 1 + a[l+1] * 2 + a[l+2] * 3 + ... + a[r] * (r-l+1)
```

The weights form AP:

```text
1, 2, 3, 4, ...
```

## Input Format

```text
n q
a0 a1 ... a(n-1)
l1 r1
l2 r2
...
```

## Sample Input

```text
5 2
3 1 4 2 5
1 3
0 4
```

## Sample Output

```text
15
50
```

## Explanation

Query `[1,3]`:

```text
a[1]*1 + a[2]*2 + a[3]*3
= 1*1 + 4*2 + 2*3
= 1 + 8 + 6
= 15
```

Query `[0,4]`:

```text
3*1 + 1*2 + 4*3 + 2*4 + 5*5
= 3 + 2 + 12 + 8 + 25
= 50
```

---

# 2. AP Query Formula Derivation

## Brute Force

```cpp
long long ans = 0;
long long weight = 1;

for (int i = l; i <= r; i++) {
    ans += a[i] * weight;
    weight++;
}
```

Complexity:

```text
O(r-l+1) per query
```

Too slow for many queries.

## Derive Formula

For index `i` in `[l,r]`, weight is:

```text
weight = i - l + 1
```

So:

```text
answer = sum a[i] * (i - l + 1)
```

Rewrite:

```text
i - l + 1 = (i + 1) - l
```

So:

```text
a[i] * (i - l + 1)
= a[i] * (i + 1) - a[i] * l
```

Now sum over `i = l..r`:

```text
answer = sum(a[i] * (i + 1)) - l * sum(a[i])
```

So we need two prefix arrays:

```text
prefA[i]  = sum of a[]
prefIA[i] = sum of a[i] * (i + 1)
```

Range query:

```text
sumA  = prefA[r+1]  - prefA[l]
sumIA = prefIA[r+1] - prefIA[l]

answer = sumIA - l * sumA
```

## Why This Is Optimized

Brute force does every element.

Formula compresses range into two prefix queries.

```text
O(n) preprocessing
O(1) per query
```

---

# 3. AP Prefix Sum Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long rangeAPWeightedSum(
    const vector<long long>& prefA,
    const vector<long long>& prefIA,
    int l,
    int r
) {
    long long sumA = prefA[r + 1] - prefA[l];
    long long sumIA = prefIA[r + 1] - prefIA[l];

    return sumIA - 1LL * l * sumA;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    cin >> n >> q;

    vector<long long> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];

    vector<long long> prefA(n + 1, 0);
    vector<long long> prefIA(n + 1, 0);

    for (int i = 0; i < n; i++) {
        prefA[i + 1] = prefA[i] + a[i];
        prefIA[i + 1] = prefIA[i] + a[i] * (i + 1);
    }

    while (q--) {
        int l, r;
        cin >> l >> r;
        cout << rangeAPWeightedSum(prefA, prefIA, l, r) << '\n';
    }

    return 0;
}
```

---

# 4. AP Prefix Sum Index-by-Index Dry Run

Input:

```text
a = [3, 1, 4, 2, 5]
```

Build:

```text
prefA  = sum a[i]
prefIA = sum a[i] * (i+1)
```

Initial:

```text
index :   0   1   2   3   4
a     :  [3,  1,  4,  2,  5]

prefA : [0, 0, 0, 0, 0, 0]
prefIA: [0, 0, 0, 0, 0, 0]
```

## i = 0

```text
a[0] = 3
a[0] * (0+1) = 3 * 1 = 3

prefA[1]  = prefA[0] + 3  = 3
prefIA[1] = prefIA[0] + 3 = 3

prefA  = [0, 3, 0, 0, 0, 0]
prefIA = [0, 3, 0, 0, 0, 0]
```

## i = 1

```text
a[1] = 1
a[1] * (1+1) = 1 * 2 = 2

prefA[2]  = 3 + 1 = 4
prefIA[2] = 3 + 2 = 5

prefA  = [0, 3, 4, 0, 0, 0]
prefIA = [0, 3, 5, 0, 0, 0]
```

## i = 2

```text
a[2] = 4
a[2] * (2+1) = 4 * 3 = 12

prefA[3]  = 4 + 4 = 8
prefIA[3] = 5 + 12 = 17

prefA  = [0, 3, 4, 8, 0, 0]
prefIA = [0, 3, 5, 17, 0, 0]
```

## i = 3

```text
a[3] = 2
a[3] * (3+1) = 2 * 4 = 8

prefA[4]  = 8 + 2 = 10
prefIA[4] = 17 + 8 = 25

prefA  = [0, 3, 4, 8, 10, 0]
prefIA = [0, 3, 5, 17, 25, 0]
```

## i = 4

```text
a[4] = 5
a[4] * (4+1) = 5 * 5 = 25

prefA[5]  = 10 + 5 = 15
prefIA[5] = 25 + 25 = 50

prefA  = [0, 3, 4, 8, 10, 15]
prefIA = [0, 3, 5, 17, 25, 50]
```

## Query Dry Run: l = 1, r = 3

Wanted:

```text
a[1]*1 + a[2]*2 + a[3]*3
= 1*1 + 4*2 + 2*3
= 15
```

Formula:

```text
sumA  = prefA[4] - prefA[1]
      = 10 - 3
      = 7

sumIA = prefIA[4] - prefIA[1]
      = 25 - 3
      = 22

answer = sumIA - l * sumA
       = 22 - 1 * 7
       = 15
```

## Query Dry Run: l = 0, r = 4

```text
sumA  = prefA[5] - prefA[0] = 15
sumIA = prefIA[5] - prefIA[0] = 50

answer = 50 - 0 * 15 = 50
```

## Do Not Memorize

When weight is:

```text
1,2,3,...
```

Convert it to:

```text
i-l+1 = i+1-l
```

Then split:

```text
sum a[i]*(i+1) - l*sum a[i]
```

---

# Part B — GP on Prefix Sum

---

# 5. GP Weighted Range Sum Query

## Problem Statement

Given an array `a[]` and ratio `R`.

For each query `[l,r]`, compute:

```text
a[l] * R^0 + a[l+1] * R^1 + a[l+2] * R^2 + ... + a[r] * R^(r-l)
```

Use modulo `MOD`.

## Input Format

```text
n q R
a0 a1 ... a(n-1)
l1 r1
...
```

## Sample Input

```text
5 2 2
3 1 4 2 5
1 3
0 4
```

## Sample Output

```text
25
93
```

## Explanation

Query `[1,3]`:

```text
1*2^0 + 4*2^1 + 2*2^2
= 1 + 8 + 8
= 17
```

Wait, this gives `17`.

If query wants weights starting from global index power:

```text
a[l]*R^l + a[l+1]*R^(l+1) + ...
```

Then `[1,3]`:

```text
1*2^1 + 4*2^2 + 2*2^3
= 2 + 16 + 16
= 34
```

So we define the exact query clearly:

This section uses **local GP weight**:

```text
weight at l = R^0
weight at l+1 = R^1
```

For sample:

```text
[1,3] = 17
[0,4] = 3 + 2 + 16 + 16 + 80 = 117
```

Correct Sample Output:

```text
17
117
```

---

# 6. GP Query Formula Derivation

## Brute Force

```cpp
long long ans = 0;
long long power = 1;

for (int i = l; i <= r; i++) {
    ans += a[i] * power;
    power *= R;
}
```

Complexity:

```text
O(r-l+1)
```

## Derive Formula

Wanted:

```text
sum a[i] * R^(i-l)
```

Rewrite:

```text
R^(i-l) = R^i / R^l
```

Under modulo:

```text
R^(i-l) = R^i * inverse(R^l)
```

So:

```text
answer = inverse(R^l) * sum(a[i] * R^i)
```

Precompute:

```text
powR[i] = R^i
invPowR[i] = inverse(R^i)

prefGP[i+1] = prefGP[i] + a[i] * powR[i]
```

Query:

```text
raw = prefGP[r+1] - prefGP[l]
answer = raw * invPowR[l]
```

## Why This Is Optimized

Instead of building local powers for every query, we store global weighted prefix.

Then normalize by dividing by `R^l`.

```text
O(n) preprocessing
O(1) query
```

---

# 7. GP Prefix Sum Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 1000000007;

long long modPow(long long a, long long e) {
    long long res = 1;
    while (e > 0) {
        if (e & 1) res = res * a % MOD;
        a = a * a % MOD;
        e >>= 1;
    }
    return res;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    long long R;
    cin >> n >> q >> R;

    vector<long long> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];

    vector<long long> powR(n + 1, 1), invPowR(n + 1, 1);
    long long invR = modPow(R, MOD - 2);

    for (int i = 1; i <= n; i++) {
        powR[i] = powR[i - 1] * R % MOD;
        invPowR[i] = invPowR[i - 1] * invR % MOD;
    }

    vector<long long> prefGP(n + 1, 0);

    for (int i = 0; i < n; i++) {
        prefGP[i + 1] = (prefGP[i] + a[i] * powR[i]) % MOD;
    }

    while (q--) {
        int l, r;
        cin >> l >> r;

        long long raw = (prefGP[r + 1] - prefGP[l] + MOD) % MOD;
        long long ans = raw * invPowR[l] % MOD;

        cout << ans << '\n';
    }

    return 0;
}
```

---

# 8. GP Prefix Sum Index-by-Index Dry Run

Use small numbers without modulo for understanding.

```text
a = [3, 1, 4, 2, 5]
R = 2
```

Powers:

```text
i    : 0  1  2  3   4
R^i  : 1  2  4  8  16
```

Build:

```text
prefGP[i+1] = prefGP[i] + a[i] * R^i
```

Initial:

```text
prefGP = [0, 0, 0, 0, 0, 0]
```

## i = 0

```text
a[0] = 3
R^0 = 1
a[0]*R^0 = 3

prefGP[1] = 0 + 3 = 3

prefGP = [0, 3, 0, 0, 0, 0]
```

## i = 1

```text
a[1] = 1
R^1 = 2
a[1]*R^1 = 2

prefGP[2] = 3 + 2 = 5

prefGP = [0, 3, 5, 0, 0, 0]
```

## i = 2

```text
a[2] = 4
R^2 = 4
a[2]*R^2 = 16

prefGP[3] = 5 + 16 = 21

prefGP = [0, 3, 5, 21, 0, 0]
```

## i = 3

```text
a[3] = 2
R^3 = 8
a[3]*R^3 = 16

prefGP[4] = 21 + 16 = 37

prefGP = [0, 3, 5, 21, 37, 0]
```

## i = 4

```text
a[4] = 5
R^4 = 16
a[4]*R^4 = 80

prefGP[5] = 37 + 80 = 117

prefGP = [0, 3, 5, 21, 37, 117]
```

## Query Dry Run: l = 1, r = 3

Wanted local GP:

```text
a[1]*2^0 + a[2]*2^1 + a[3]*2^2
= 1*1 + 4*2 + 2*4
= 1 + 8 + 8
= 17
```

Using global prefix:

```text
raw = prefGP[4] - prefGP[1]
    = 37 - 3
    = 34
```

But raw is:

```text
a[1]*2^1 + a[2]*2^2 + a[3]*2^3
= 1*2 + 4*4 + 2*8
= 34
```

Normalize by `2^l = 2^1 = 2`:

```text
answer = raw / 2
       = 34 / 2
       = 17
```

Under modulo:

```text
answer = raw * invPowR[l] % MOD
```

## Query Dry Run: l = 0, r = 4

```text
raw = prefGP[5] - prefGP[0] = 117

normalize by 2^0 = 1

answer = 117
```

## Do Not Memorize

GP query:

```text
local power = R^(i-l)
```

Convert to global power:

```text
R^(i-l) = R^i / R^l
```

So precompute global:

```text
a[i] * R^i
```

Then divide by `R^l`.

---

# Part C — AP on Partial Sum

---

# 9. AP Range Update Problem

## Problem Statement

You have an array of size `n`, initially zero.

Each update is:

```text
l r a d
```

Add AP to range `[l,r]`:

```text
index: l      l+1      l+2      ... r
add  : a,     a+d,     a+2d,        a+(r-l)d
```

After all updates, print final array.

## Sample Input

```text
5 2
1 3 2 1
0 2 3 2
```

## Sample Output

```text
3 7 11 4 0
```

Explanation:

Update 1 on `[1,3]`:

```text
[0, 2, 3, 4, 0]
```

Update 2 on `[0,2]`:

```text
[3, 5, 7, 0, 0]
```

Final:

```text
[3, 7, 10, 4, 0]
```

Correction: `3+?`

Let's calculate carefully:

```text
Update 1: index 1=2, index 2=3, index 3=4
Update 2: index 0=3, index 1=5, index 2=7

Final:
index 0: 3
index 1: 2+5 = 7
index 2: 3+7 = 10
index 3: 4
index 4: 0
```

Correct Sample Output:

```text
3 7 10 4 0
```

---

# 10. AP Partial Sum Formula Derivation

For one update:

```text
add at index i = a + (i-l)d
```

Rewrite:

```text
a + (i-l)d
= a + i*d - l*d
= d*i + (a - l*d)
```

So each update adds a linear function:

```text
d*i + c
```

where:

```text
c = a - l*d
```

Therefore, maintain two difference arrays:

```text
diffD for coefficient of i
diffC for constant term
```

For update `[l,r,a,d]`:

```text
c = a - l*d

diffD[l] += d
diffD[r+1] -= d

diffC[l] += c
diffC[r+1] -= c
```

After prefix rebuild:

```text
D = active coefficient
C = active constant

final[i] = D*i + C
```

## Why This Works

AP is linear in index.

So instead of storing every AP term, store:

```text
coefficient of i
constant part
```

Difference array activates/deactivates those coefficients over range.

---

# 11. AP Partial Sum Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    cin >> n >> q;

    vector<long long> diffD(n + 1, 0);
    vector<long long> diffC(n + 1, 0);

    while (q--) {
        long long l, r, a, d;
        cin >> l >> r >> a >> d;

        long long c = a - l * d;

        diffD[l] += d;
        diffC[l] += c;

        if (r + 1 < n) {
            diffD[r + 1] -= d;
            diffC[r + 1] -= c;
        }
    }

    long long curD = 0;
    long long curC = 0;

    for (int i = 0; i < n; i++) {
        curD += diffD[i];
        curC += diffC[i];

        long long value = curD * i + curC;
        cout << value << ' ';
    }

    cout << '\n';
    return 0;
}
```

---

# 12. AP Partial Sum Index-by-Index Dry Run

Input:

```text
n = 5
updates:
1) l=1, r=3, a=2, d=1
2) l=0, r=2, a=3, d=2
```

Initial:

```text
diffD = [0, 0, 0, 0, 0]
diffC = [0, 0, 0, 0, 0]
```

## Update 1: l=1, r=3, a=2, d=1

Formula:

```text
value at i = a + (i-l)d
           = 2 + (i-1)*1
           = i + 1
```

So:

```text
D = 1
C = 1
```

Using formula:

```text
c = a - l*d = 2 - 1*1 = 1
```

Mark active range `[1,3]`:

```text
diffD[1] += 1
diffD[4] -= 1

diffC[1] += 1
diffC[4] -= 1
```

After update 1:

```text
diffD = [0, 1, 0, 0, -1]
diffC = [0, 1, 0, 0, -1]
```

## Update 2: l=0, r=2, a=3, d=2

Formula:

```text
value at i = 3 + (i-0)*2
           = 2i + 3
```

So:

```text
D = 2
C = 3
```

Using formula:

```text
c = a - l*d = 3 - 0*2 = 3
```

Mark active range `[0,2]`:

```text
diffD[0] += 2
diffD[3] -= 2

diffC[0] += 3
diffC[3] -= 3
```

After update 2:

```text
diffD = [2, 1, 0, -2, -1]
diffC = [3, 1, 0, -3, -1]
```

Now rebuild.

## i = 0

```text
curD = 0 + diffD[0] = 2
curC = 0 + diffC[0] = 3

final[0] = curD*i + curC
         = 2*0 + 3
         = 3
```

## i = 1

```text
curD = 2 + diffD[1] = 3
curC = 3 + diffC[1] = 4

final[1] = 3*1 + 4 = 7
```

Why?

```text
Update 1 gives 2
Update 2 gives 5
Total = 7
```

## i = 2

```text
curD = 3 + diffD[2] = 3
curC = 4 + diffC[2] = 4

final[2] = 3*2 + 4 = 10
```

Why?

```text
Update 1 gives 3
Update 2 gives 7
Total = 10
```

## i = 3

```text
curD = 3 + diffD[3] = 1
curC = 4 + diffC[3] = 1

final[3] = 1*3 + 1 = 4
```

Only update 1 active:

```text
index 3 value = 4
```

## i = 4

```text
curD = 1 + diffD[4] = 0
curC = 1 + diffC[4] = 0

final[4] = 0
```

Final:

```text
[3, 7, 10, 4, 0]
```

## Do Not Memorize

AP update is:

```text
a + (i-l)d
```

Rewrite as:

```text
d*i + (a-l*d)
```

So maintain difference arrays for:

```text
coefficient of i
constant
```

---

# Part D — GP on Partial Sum

---

# 13. GP Range Update Problem

## Problem Statement

You have an array of size `n`, initially zero.

Each update is:

```text
l r a R
```

Add GP to range `[l,r]`:

```text
index: l      l+1      l+2      ... r
add  : a,     aR,      aR^2,        aR^(r-l)
```

After all updates, print final array modulo `MOD`.

## Sample Input

```text
5 2 2
1 3 3
0 2 1
```

Interpretation:

```text
n = 5, q = 2, R = 2
update format: l r a
```

Updates:

```text
1) add [3,6,12] on indices 1..3
2) add [1,2,4] on indices 0..2
```

## Sample Output

```text
1 5 10 12 0
```

---

# 14. GP Partial Sum Formula Derivation

For one update:

```text
value at index i = a * R^(i-l)
```

Rewrite using global power:

```text
R^(i-l) = R^i / R^l
```

So:

```text
value = a * R^i * inverse(R^l)
```

For fixed update:

```text
coef = a * inverse(R^l)
```

Then:

```text
value at i = coef * R^i
```

So maintain one difference array over coefficient:

```text
diffCoef[l] += coef
diffCoef[r+1] -= coef
```

After prefix rebuild:

```text
activeCoef = sum of coefficients active at i
final[i] = activeCoef * R^i
```

## Why This Works

GP is exponential in index.

We normalize the local GP to global power `R^i`.

```text
local:  a * R^(i-l)
global: coef * R^i
coef = a / R^l
```

---

# 15. GP Partial Sum Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 1000000007;

long long modPow(long long a, long long e) {
    long long res = 1;
    while (e > 0) {
        if (e & 1) res = res * a % MOD;
        a = a * a % MOD;
        e >>= 1;
    }
    return res;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    long long R;
    cin >> n >> q >> R;

    vector<long long> powR(n + 1, 1), invPowR(n + 1, 1);
    long long invR = modPow(R, MOD - 2);

    for (int i = 1; i <= n; i++) {
        powR[i] = powR[i - 1] * R % MOD;
        invPowR[i] = invPowR[i - 1] * invR % MOD;
    }

    vector<long long> diffCoef(n + 1, 0);

    while (q--) {
        int l, r;
        long long a;
        cin >> l >> r >> a;

        long long coef = a * invPowR[l] % MOD;

        diffCoef[l] = (diffCoef[l] + coef) % MOD;

        if (r + 1 < n) {
            diffCoef[r + 1] = (diffCoef[r + 1] - coef + MOD) % MOD;
        }
    }

    long long activeCoef = 0;

    for (int i = 0; i < n; i++) {
        activeCoef = (activeCoef + diffCoef[i]) % MOD;
        long long value = activeCoef * powR[i] % MOD;
        cout << value << ' ';
    }

    cout << '\n';
    return 0;
}
```

---

# 16. GP Partial Sum Index-by-Index Dry Run

Use small numbers without modulo.

```text
n = 5
R = 2

updates:
1) l=1, r=3, a=3
2) l=0, r=2, a=1
```

Powers:

```text
i   : 0  1  2  3   4
R^i : 1  2  4  8  16
```

Initial:

```text
diffCoef = [0, 0, 0, 0, 0]
```

## Update 1: l=1, r=3, a=3

Wanted values:

```text
i=1: 3 * 2^(1-1) = 3
i=2: 3 * 2^(2-1) = 6
i=3: 3 * 2^(3-1) = 12
```

Rewrite:

```text
value = coef * 2^i
coef = a / 2^l = 3 / 2
```

For dry run with fractions:

```text
coef = 1.5
```

Mark:

```text
diffCoef[1] += 1.5
diffCoef[4] -= 1.5
```

After update 1:

```text
diffCoef = [0, 1.5, 0, 0, -1.5]
```

## Update 2: l=0, r=2, a=1

Wanted values:

```text
i=0: 1
i=1: 2
i=2: 4
```

Rewrite:

```text
coef = a / 2^0 = 1
value = 1 * 2^i
```

Mark:

```text
diffCoef[0] += 1
diffCoef[3] -= 1
```

After update 2:

```text
diffCoef = [1, 1.5, 0, -1, -1.5]
```

Now rebuild.

## i = 0

```text
activeCoef = 0 + diffCoef[0] = 1
value = activeCoef * 2^0
      = 1 * 1
      = 1
```

## i = 1

```text
activeCoef = 1 + 1.5 = 2.5
value = 2.5 * 2^1
      = 2.5 * 2
      = 5
```

Why?

```text
Update 1 gives 3
Update 2 gives 2
Total = 5
```

## i = 2

```text
activeCoef = 2.5 + 0 = 2.5
value = 2.5 * 2^2
      = 2.5 * 4
      = 10
```

Why?

```text
Update 1 gives 6
Update 2 gives 4
Total = 10
```

## i = 3

```text
activeCoef = 2.5 + (-1) = 1.5
value = 1.5 * 2^3
      = 1.5 * 8
      = 12
```

Only update 1 active:

```text
value = 12
```

## i = 4

```text
activeCoef = 1.5 + (-1.5) = 0
value = 0
```

Final:

```text
[1, 5, 10, 12, 0]
```

## Do Not Memorize

GP update is:

```text
a * R^(i-l)
```

Rewrite:

```text
a * R^i / R^l
```

So:

```text
coef = a / R^l
value = coef * R^i
```

Difference array tracks active `coef`.

---

# 17. Recognition Table

| Problem Signal | Technique |
|---|---|
| Query sum with weights 1,2,3... from l | AP on prefix sum |
| Query sum with weights R^0,R^1... from l | GP on prefix sum |
| Add a,a+d,a+2d on range | AP on partial sum |
| Add a,aR,aR² on range | GP on partial sum |
| Term looks like linear function of index | Split into coefficient and constant |
| Term looks like power depending on index | Normalize using global power |

---

# 18. Formula Summary

## AP Prefix Query

Wanted:

```text
sum a[i] * (i-l+1)
```

Rewrite:

```text
i-l+1 = i+1-l
```

Formula:

```text
answer = sum(a[i]*(i+1)) - l*sum(a[i])
```

Need:

```text
prefA
prefIA
```

---

## GP Prefix Query

Wanted:

```text
sum a[i] * R^(i-l)
```

Rewrite:

```text
R^(i-l) = R^i / R^l
```

Formula:

```text
answer = inverse(R^l) * sum(a[i] * R^i)
```

Need:

```text
powR
invPowR
prefGP
```

---

## AP Partial Update

Wanted:

```text
add a + (i-l)d
```

Rewrite:

```text
a + (i-l)d = d*i + (a-l*d)
```

Need difference arrays:

```text
diffD
diffC
```

Final:

```text
value[i] = activeD*i + activeC
```

---

## GP Partial Update

Wanted:

```text
add a * R^(i-l)
```

Rewrite:

```text
a * R^(i-l) = (a / R^l) * R^i
```

Need:

```text
diffCoef
powR
invPowR
```

Final:

```text
value[i] = activeCoef * R^i
```

---

# 19. Contest Templates

## AP Prefix Query Template

```cpp
vector<long long> prefA(n + 1), prefIA(n + 1);

for (int i = 0; i < n; i++) {
    prefA[i + 1] = prefA[i] + a[i];
    prefIA[i + 1] = prefIA[i] + a[i] * (i + 1);
}

auto query = [&](int l, int r) {
    long long sumA = prefA[r + 1] - prefA[l];
    long long sumIA = prefIA[r + 1] - prefIA[l];
    return sumIA - 1LL * l * sumA;
};
```

## GP Prefix Query Template

```cpp
vector<long long> powR(n + 1, 1), invPowR(n + 1, 1);
long long invR = modPow(R, MOD - 2);

for (int i = 1; i <= n; i++) {
    powR[i] = powR[i - 1] * R % MOD;
    invPowR[i] = invPowR[i - 1] * invR % MOD;
}

vector<long long> prefGP(n + 1);

for (int i = 0; i < n; i++) {
    prefGP[i + 1] = (prefGP[i] + a[i] * powR[i]) % MOD;
}

auto query = [&](int l, int r) {
    long long raw = (prefGP[r + 1] - prefGP[l] + MOD) % MOD;
    return raw * invPowR[l] % MOD;
};
```

## AP Partial Sum Template

```cpp
vector<long long> diffD(n + 1), diffC(n + 1);

auto addAP = [&](int l, int r, long long a, long long d) {
    long long c = a - 1LL * l * d;

    diffD[l] += d;
    diffC[l] += c;

    if (r + 1 < n) {
        diffD[r + 1] -= d;
        diffC[r + 1] -= c;
    }
};

long long curD = 0, curC = 0;

for (int i = 0; i < n; i++) {
    curD += diffD[i];
    curC += diffC[i];

    long long value = curD * i + curC;
}
```

## GP Partial Sum Template

```cpp
vector<long long> diffCoef(n + 1);

auto addGP = [&](int l, int r, long long a) {
    long long coef = a * invPowR[l] % MOD;

    diffCoef[l] = (diffCoef[l] + coef) % MOD;

    if (r + 1 < n) {
        diffCoef[r + 1] = (diffCoef[r + 1] - coef + MOD) % MOD;
    }
};

long long activeCoef = 0;

for (int i = 0; i < n; i++) {
    activeCoef = (activeCoef + diffCoef[i]) % MOD;
    long long value = activeCoef * powR[i] % MOD;
}
```

---

# Final Note

These four topics are not four separate tricks.

They are the same idea:

```text
Convert range behavior into a formula in i.
```

Then:

```text
Prefix Sum  = precompute formula pieces for fast queries.
Partial Sum = mark formula pieces for fast updates.
```
