# Prefix Sums, Difference Arrays & Coordinate Compression — Level 2 Master Tutorial

> A problem-solving notebook built from the uploaded TLE Level-2 lecture PDFs, the linked `PriyamTLE/Level-2-Lecture-Codes` GitHub module, and extra AP/GP extensions requested for competitive programming.
>
> **Indexing convention:** most algebra below uses **1-based indexing** because it makes weighted-range formulas easier to read. Code explicitly states its indexing.

---

## Table of Contents

1. [The Core Mental Model](#1-the-core-mental-model)
2. [1D Prefix Sum — Range Sum in O(1)](#2-1d-prefix-sum--range-sum-in-o1)
3. [2D Prefix Sum — Rectangle Sum in O(1)](#3-2d-prefix-sum--rectangle-sum-in-o1)
4. [AP on Prefix Sum — Weighted Range Query](#4-ap-on-prefix-sum--weighted-range-query)
5. [GP on Prefix Sum — Geometric Weighted Query](#5-gp-on-prefix-sum--geometric-weighted-query)
6. [Prefix-State Pattern — Contiguous Array](#6-prefix-state-pattern--contiguous-array)
7. [Ordinary Difference Array — Range Addition](#7-ordinary-difference-array--range-addition)
8. [Difference Array Problem 1 — Greg and Array](#8-difference-array-problem-1--greg-and-array)
9. [AP on Partial Sum / Difference Array — AP Range Updates](#9-ap-on-partial-sum--difference-array--ap-range-updates)
10. [GP on Partial Sum / Difference Array — GP Range Updates](#10-gp-on-partial-sum--difference-array--gp-range-updates)
11. [Coordinate Compression](#11-coordinate-compression)
12. [Difference Array Problem 2 — Snuke Prime](#12-difference-array-problem-2--snuke-prime)
13. [Kth Val — Difference Array + Sorting + Frequency Prefix](#13-kth-val--difference-array--sorting--frequency-prefix)
14. [Pattern Recognition Cheat Sheet](#14-pattern-recognition-cheat-sheet)
15. [Complexity Cheat Sheet](#15-complexity-cheat-sheet)
16. [Practice Checklist](#16-practice-checklist)

---

# 1. The Core Mental Model

Prefix sums and difference arrays are two sides of the same idea.

```text
Original values --prefix--> cumulative values
Changes/edges  --prefix--> actual values
```

### Prefix sum

Use it when many queries ask:

```text
"What is the total/state between L and R?"
```

### Difference array

Use it when many operations say:

```text
"Add/change something on every position from L to R"
```

but you only need the final array after all updates.

### Coordinate compression

Use it when positions are huge, for example `1 ... 10^9`, but only a small number of positions are important.

```text
Huge coordinate line:
1 ---------------------------------------------- 10^9

Only events:
      2      4         6               9
      ^      ^         ^               ^

Compress to:
      0      1         2               3
```

The uploaded lecture states the same high-level goal: prefix sums preprocess arrays for fast range queries; difference arrays process range updates in O(1) each and recover the final state with a prefix pass; coordinate compression keeps only important points.

---

# 2. 1D Prefix Sum — Range Sum in O(1)

## Problem

Given an array `A` and many queries `[L,R]`, return:

```text
A[L] + A[L+1] + ... + A[R]
```

A direct loop for every query is O(NQ), which is too slow when both are large.

## Step 1 — Build cumulative sums

For 1-based indexing define:

```text
P[i] = A[1] + A[2] + ... + A[i]
```

and `P[0]=0`.

Then:

```text
P[i] = P[i-1] + A[i]
```

### Dry run

```text
index:  1   2   3   4   5   6   7
A:      2   9   6   3   7   2   1

P[0] = 0
P[1] = 2
P[2] = 2 + 9     = 11
P[3] = 11 + 6    = 17
P[4] = 17 + 3    = 20
P[5] = 20 + 7    = 27
P[6] = 27 + 2    = 29
P[7] = 29 + 1    = 30

P:      0   2  11  17  20  27  29  30
index:  0   1   2   3   4   5   6   7
```

Suppose query is `[3,5]`.

```text
A = 2  9 | 6  3  7 | 2  1
            <------>

P[5] = 2+9+6+3+7 = 27
P[2] = 2+9         = 11

answer = P[5] - P[2]
       = 27 - 11
       = 16
```

## Why `P[R] - P[L-1]` works

```text
P[R] = A[1] + ... + A[L-1] + A[L] + ... + A[R]
```

```text
P[L-1] = A[1] + ... + A[L-1]
```

Subtract:

```text
P[R] - P[L-1] = A[L] + ... + A[R]
```

## C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    cin >> n >> q;

    vector<long long> a(n + 1), pref(n + 1, 0);

    for (int i = 1; i <= n; ++i) {
        cin >> a[i];
        pref[i] = pref[i - 1] + a[i];
    }

    while (q--) {
        int l, r;
        cin >> l >> r;
        cout << pref[r] - pref[l - 1] << '\n';
    }
}
```

### Complexity

```text
Build:   O(N)
Query:   O(1)
Total:   O(N + Q)
Space:   O(N)
```

---

# 3. 2D Prefix Sum — Rectangle Sum in O(1)

For a grid, define:

```text
P[i][j] = sum of rectangle from (1,1) to (i,j)
```

## Building the table

At `(i,j)`:

```text
             left area
       <------------------>
       +-------------------+
       |                   |
       |       overlap     | ^
       |                   | | upper area
       +--------------+----+ v
       |              | A[i][j]
       +--------------+----+
```

If we add the rectangle above and the rectangle left, the top-left overlap is counted twice. Therefore:

```text
P[i][j] = A[i][j] + P[i-1][j] + P[i][j-1] - P[i-1][j-1]
```

## Rectangle query

Suppose we want `(r1,c1)` to `(r2,c2)`.

```text
+------------------------------+
|                              |
|       subtract TOP           |
|                              |
+---------+--------------------+
| subtract|                    |
|  LEFT   |      WANTED        |
|         |                    |
+---------+--------------------+
```

Start with everything up to `(r2,c2)`:

```text
P[r2][c2]
```

Subtract top and left. Their overlap was subtracted twice, so add it back:

```text
ans = P[r2][c2] - P[r1-1][c2] - P[r2][c1-1] + P[r1-1][c1-1]
```

## C++

```cpp
long long rectangleSum(
    const vector<vector<long long>>& p,
    int r1, int c1, int r2, int c2
) {
    return p[r2][c2]
         - p[r1 - 1][c2]
         - p[r2][c1 - 1]
         + p[r1 - 1][c1 - 1];
}
```

Full construction:

```cpp
vector<vector<long long>> p(n + 1, vector<long long>(m + 1, 0));

for (int i = 1; i <= n; ++i) {
    for (int j = 1; j <= m; ++j) {
        long long x;
        cin >> x;
        p[i][j] = x + p[i - 1][j] + p[i][j - 1] - p[i - 1][j - 1];
    }
}
```

### Recognition signal

```text
static grid + many rectangle sum/count queries
                    |
                    v
              2D prefix sum
```

This is the exact pattern used in problems such as **CSES Forest Queries**.

---

# 4. AP on Prefix Sum — Weighted Range Query

This is one of the most useful algebraic prefix-sum patterns.

## Problem form

For each query `[L,R]`, compute:

```text
1*A[L] + 2*A[L+1] + 3*A[L+2] + ... + (R-L+1)*A[R]
```

The lecture/GitHub solution uses **two prefix sums**.

## Step 1 — Find the local weight formula

At global index `i`, the local position inside `[L,R]` is:

```text
L      L+1    L+2             i
|       |      |              |
1       2      3        ...   ?
```

Distance from `L` is `i-L`.

Since `L` itself must have weight `1`:

```text
weight(i) = i - L + 1
```

Therefore:

```text
ans = Σ(i=L..R) A[i] * (i - L + 1)
```

## Step 2 — Expand the bracket

```text
i - L + 1 = i - (L - 1)
```

So:

```text
ans = Σ(i=L..R) A[i] * [i - (L - 1)]
```

Distribute `A_i`:

```text
ans = Σ(i=L..R) i*A[i] - (L-1) * Σ(i=L..R) A[i]
```

This is the key transformation.

We need only two prefix arrays:

```text
P0[i] = Σ(j=1..i) A[j]
```

```text
P1[i] = Σ(j=1..i) j*A[j]
```

Then:

```text
S0 = P0[R] - P0[L-1]
```

```text
S1 = P1[R] - P1[L-1]
```

and:

```text
FINAL: ans = S1 - (L - 1) * S0
```

## Dry run

```text
A:      [2, 4, 3, 6, 9]
index:   1  2  3  4  5

Query: L=2, R=4

Wanted:
1*A[2] + 2*A[3] + 3*A[4]
= 1*4 + 2*3 + 3*6
= 4 + 6 + 18
= 28
```

Build:

```text
A[i]:       2    4    3    6    9

i*A[i]:    2    8    9   24   45

P0:         0    2    6    9   15   24
P1:         0    2   10   19   43   88
             ^ 1-based prefix with P[0]=0
```

For `[2,4]`:

```text
S0 = P0[4] - P0[1]
   = 15 - 2
   = 13

S1 = P1[4] - P1[1]
   = 43 - 2
   = 41

ans = S1 - (L-1)*S0
    = 41 - 1*13
    = 28
```

## General AP weights

Suppose weights are:

```text
a, a+d, a+2d, ...
```

At index `i`:

```text
w[i] = a + (i - L) * d
```

Expand:

```text
w[i] = d*i + (a - d*L)
```

Therefore:

```text
ans = Σ A[i] * [d*i + (a - d*L)]
```

```text
FINAL: ans = d * Σ(i*A[i]) + (a - d*L) * Σ(A[i])
```

Again: only two prefix sums.

## C++

```cpp
long long weightedQuery(
    int l, int r,
    const vector<long long>& p0,
    const vector<long long>& p1
) {
    long long sumA  = p0[r] - p0[l - 1];
    long long sumIA = p1[r] - p1[l - 1];
    return sumIA - 1LL * (l - 1) * sumA;
}
```

### Pattern signal

Whenever the query has a weight that is a **linear expression in the index**:

```text
(c*i + d) * A[i]
```

think:

```text
prefix(A[i]) + prefix(i*A[i])
```

---

# 5. GP on Prefix Sum — Geometric Weighted Query

Now suppose the local weights are geometric:

```text
1, r, r^2, r^3, ...
```

For query `[L,R]`:

```text
ans = Σ(i=L..R) A[i] * r^(i-L)
```

## Algebra

Because:

```text
r^(i-L) = r^i * r^(-L)
```

we can write:

```text
ans = r^(-L) * Σ(i=L..R) A[i] * r^i
```

Define:

```text
G[i] = Σ(j=1..i) A[j] * r^j
```

Then:

```text
Σ(i=L..R) A[i] * r^i = G[R] - G[L-1]
```

So:

```text
FINAL: ans = (G[R] - G[L-1]) * r^(-L)
```

## Important modular condition

In CP this is usually done modulo a prime `MOD`.

`r^{-L}` means modular inverse:

```text
r^(-L) = (r^(-1))^L mod MOD
```

This requires `gcd(r,MOD)=1`.

For prime MOD and `r % MOD != 0`:

```text
r^(-1) = r^(MOD-2) mod MOD   (when MOD is prime)
```

## Example

Let:

```text
A = [3, 5, 2, 4]
r = 2
Query [2,4]
```

Wanted:

```text
5*1 + 2*2 + 4*4
= 5 + 4 + 16
= 25
```

Transform:

```text
A[i] * 2^i:

i=1: 3*2  = 6
i=2: 5*4  = 20
i=3: 2*8  = 16
i=4: 4*16 = 64

sum transformed [2..4] = 100
multiply by 2^(-2):
100 / 4 = 25
```

## C++ modulo prime

```cpp
long long modPow(long long a, long long e, long long mod) {
    long long ans = 1;
    while (e) {
        if (e & 1) ans = ans * a % mod;
        a = a * a % mod;
        e >>= 1;
    }
    return ans;
}

// Precompute powR[i] = r^i and invPowR[i] = r^(-i)
// G[i] = G[i-1] + A[i] * powR[i]

long long gpQuery(int l, int r,
                  const vector<long long>& G,
                  const vector<long long>& invPowR,
                  long long MOD) {
    long long transformed = (G[r] - G[l - 1] + MOD) % MOD;
    return transformed * invPowR[l] % MOD;
}
```

### Recognition signal

```text
Weights depend on r^(i-L)
        |
        v
Shift exponent to global i
        |
        v
prefix(A[i] * r^i)
```

---

# 6. Prefix-State Pattern — Contiguous Array

This is not a normal range-sum query. It uses the more general prefix idea:

> If the same prefix state appears at two positions, the segment between them has net contribution zero.

## Problem

For a binary array, find the longest subarray with equal numbers of `0` and `1`.

Example:

```text
A = 0  1  0  1  1  0  0  1
```

## Step 1 — Convert the condition into algebra

We want:

```text
count(1) = count(0)
```

Move one side:

```text
count(1) - count(0) = 0
```

So map:

```text
0 -> -1
1 -> +1
```

Now we need the **longest subarray with sum 0**.

## Step 2 — Prefix equality

If:

```text
prefix[j] = prefix[i]
```

then:

```text
prefix[j] - prefix[i] = 0
```

so subarray `(i+1 ... j)` has sum 0.

### Dry run

```text
A:        0   1   0   1   1   0   0   1
mapped:  -1  +1  -1  +1  +1  -1  -1  +1

index:    0   1   2   3   4   5   6   7
prefix:  -1   0  -1   0   1   0  -1   0
```

Use conceptual prefix state `0` before the array at index `-1`.

```text
first[0] = -1
```

At index `7`, prefix is again `0`:

```text
length = 7 - (-1) = 8
```

So the whole array is balanced.

## C++ O(N)

```cpp
int findMaxLength(vector<int>& nums) {
    unordered_map<int, int> first;
    first[0] = -1;

    int pref = 0;
    int ans = 0;

    for (int i = 0; i < (int)nums.size(); ++i) {
        pref += (nums[i] == 0 ? -1 : 1);

        if (first.count(pref)) {
            ans = max(ans, i - first[pref]);
        } else {
            first[pref] = i; // keep earliest only
        }
    }

    return ans;
}
```

### General pattern

```text
condition on subarray
      |
      v
convert each element to a contribution
      |
      v
condition becomes subarray sum/state = X
      |
      v
use prefix state + hash map
```

---

# 7. Ordinary Difference Array — Range Addition

## Problem

Start with an array. Perform many updates:

```text
add X to every A[L..R]
```

Doing every update directly costs O(length of range), possibly O(NQ).

## The boundary idea

Instead of touching every element, record only where the effect starts and where it stops.

For update `[L,R] += X`:

```text
D[L]     += X
D[R + 1] -= X
```

Then prefix `D` once.

### Why?

Suppose:

```text
N = 8
update [2,5] += 3
```

Boundary representation:

```text
index:  1   2   3   4   5   6   7   8
D:      0  +3   0   0   0  -3   0   0
```

Prefix:

```text
index:  1   2   3   4   5   6   7   8
add:    0   3   3   3   3   0   0   0
```

Exactly the desired range.

## Multiple updates dry run

Updates:

```text
[2,5] += 3
[3,6] += 2
```

Marks:

```text
index:  1   2   3   4   5   6   7   8
D:      0  +3  +2   0   0  -3  -2   0
```

Prefix:

```text
add:    0   3   5   5   5   2   0   0
```

## C++

```cpp
vector<long long> diff(n + 2, 0);

while (q--) {
    int l, r;
    long long x;
    cin >> l >> r >> x;

    diff[l] += x;
    diff[r + 1] -= x;
}

for (int i = 1; i <= n; ++i) {
    diff[i] += diff[i - 1];
    a[i] += diff[i];
}
```

### Complexity

```text
Each update: O(1)
Recovery:    O(N)
Total:       O(N + Q)
```

---

# 8. Difference Array Problem 1 — Greg and Array

This problem is important because it uses **difference arrays twice**.

## Structure

You have:

1. An initial array `A`.
2. `M` operations. Operation `i` adds `d[i]` to array range `[l[i],r[i]]`.
3. `K` queries. Each query `[x,y]` says: execute operations `x..y` one more time.

The trap is to simulate operation ranges for every query.

## Layer 1 — How many times is each operation executed?

Suppose operations are numbered:

```text
op:      1   2   3   4   5
```

Query `[2,4]` means:

```text
times:   0   1   1   1   0
```

So make a difference array over **operation indices**:

```text
opDiff[2] += 1
opDiff[5] -= 1
```

After all K queries, prefix it:

```text
cnt[i] = number of times operation i must run
```

## Layer 2 — Apply all operations to the actual array

Operation `i`:

```text
[l[i], r[i]] += d[i] * cnt[i]
```

Again, do not touch the whole interval.

```text
arrDiff[l[i]]     += d[i] * cnt[i]
arrDiff[r[i] + 1] -= d[i] * cnt[i]
```

Prefix `arrDiff` and add it to original `A`.

## ASCII pipeline

```text
K query ranges over operations
          |
          v
  difference array #1
          |
          v
cnt[operation]
          |
          v
scale each operation by cnt
          |
          v
  difference array #2
          |
          v
 final change at each A[i]
          |
          v
      final array
```

## Mini dry run

Initial:

```text
A = [1, 2, 3, 4]
```

Operations:

```text
1: [1,2] += 10
2: [2,4] += 5
3: [3,3] += 7
```

Queries over operations:

```text
[1,2]
[2,3]
```

Operation counts:

```text
op 1 -> 1 time
op 2 -> 2 times
op 3 -> 1 time
```

Scaled operations:

```text
[1,2] += 10
[2,4] += 10
[3,3] += 7
```

Final added values:

```text
index:    1   2   3   4
change:  10  20  17  10
```

Final:

```text
[11, 22, 20, 14]
```

## C++

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Op {
    int l, r;
    long long d;
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m, k;
    cin >> n >> m >> k;

    vector<long long> a(n + 1);
    for (int i = 1; i <= n; ++i) cin >> a[i];

    vector<Op> ops(m + 1);
    for (int i = 1; i <= m; ++i) {
        cin >> ops[i].l >> ops[i].r >> ops[i].d;
    }

    // Difference over operation indices.
    vector<long long> opDiff(m + 2, 0);

    while (k--) {
        int x, y;
        cin >> x >> y;
        opDiff[x] += 1;
        opDiff[y + 1] -= 1;
    }

    vector<long long> cnt(m + 1, 0);
    for (int i = 1; i <= m; ++i) {
        cnt[i] = cnt[i - 1] + opDiff[i];
    }

    // Difference over actual array positions.
    vector<long long> arrDiff(n + 2, 0);

    for (int i = 1; i <= m; ++i) {
        long long add = ops[i].d * cnt[i];
        arrDiff[ops[i].l] += add;
        arrDiff[ops[i].r + 1] -= add;
    }

    long long running = 0;
    for (int i = 1; i <= n; ++i) {
        running += arrDiff[i];
        cout << a[i] + running << (i == n ? '\n' : ' ');
    }
}
```

### Recognition signal

```text
range queries choose ranges of range operations
                |
                v
       "difference of operations"
                +
       "difference of array"
```

---

# 9. AP on Partial Sum / Difference Array — AP Range Updates

Here “partial sum” means reconstructing the final values from deferred range-update information.

## Problem form

For update `[L,R]`, add an arithmetic progression:

```text
A[L]     += a
A[L+1]   += a+d
A[L+2]   += a+2d
...
A[R]     += a+(R-L)d
```

Doing this directly is O(length).

## Key algebra

At global index `i`:

```text
add(i) = a + (i - L) * d
```

Expand:

```text
add(i) = d*i + (a - d*L)
```

This is a linear function:

```text
add(i) = C*i + B
```

where:

```text
C = d
B = a - d*L
```

So maintain two difference arrays:

```text
diffC -> coefficient of i
diffB -> constant term
```

For `[L,R]`:

```text
diffC[L]     += d
diffC[R + 1] -= d

diffB[L]     += a - d*L
diffB[R + 1] -= a - d*L
```

After prefixing both arrays:

```text
add(i) = C[i]*i + B[i]
```

## Dry run

Update:

```text
L=3, R=6
a=5, d=2
```

Wanted additions:

```text
index:  1  2 | 3  4  5  6 | 7
add:    0  0 | 5  7  9 11 | 0
```

Algebra:

```text
C = 2
B = a - d*L
  = 5 - 2*3
  = -1

add(i) = 2*i - 1
```

Check:

```text
i=3 -> 6-1  = 5
i=4 -> 8-1  = 7
i=5 -> 10-1 = 9
i=6 -> 12-1 = 11
```

## C++

```cpp
vector<long long> diffC(n + 2, 0);
vector<long long> diffB(n + 2, 0);

// add AP a, a+d, ... on [l,r]
auto addAP = [&](int l, int r, long long a, long long d) {
    long long C = d;
    long long B = a - d * l;

    diffC[l] += C;
    diffC[r + 1] -= C;

    diffB[l] += B;
    diffB[r + 1] -= B;
};

long long C = 0, B = 0;
for (int i = 1; i <= n; ++i) {
    C += diffC[i];
    B += diffB[i];
    long long addition = C * i + B;
    a[i] += addition;
}
```

### Why two arrays?

Because every AP update is a degree-1 polynomial in `i`.

```text
AP update
  |
  v
linear function c*i + b
  |
  +--> range-add coefficient c
  |
  +--> range-add constant b
```

This idea generalizes: polynomial updates of degree `k` can often be represented with several coefficient/difference layers.

---

# 10. GP on Partial Sum / Difference Array — GP Range Updates

Now update `[L,R]` with:

```text
a, a*r, a*r^2, ...
```

So contribution at `i` is:

```text
x[i] = a * r^(i-L)
```

A normal difference `x_i-x_{i-1}` is not constant. But a GP has a recurrence:

```text
x[i] = r * x[i-1]
```

That tells us what “difference” to store.

## Define a GP-difference

Let:

```text
D[i] = x[i] - r*x[i-1]
```

Inside a pure GP:

```text
D[i] = 0
```

except at boundaries.

For an update `[L,R]`:

```text
D[L]     += a
D[R + 1] -= a * r^(R-L+1)
```

Then reconstruct with:

```text
x[i] = D[i] + r*x[i-1]
```

## Dry run

```text
L=2, R=5
a=3, r=2
```

Wanted:

```text
index:  1 | 2   3   4   5 | 6
x:      0 | 3   6  12  24 | 0
```

Marks:

```text
D[2] += 3

length = 5-2+1 = 4
next GP value would be 3 * 2^4 = 48
D[6] -= 48
```

So:

```text
D:      0   3   0   0   0  -48
```

Reconstruct:

```text
x[1] = 0 + 2*0  = 0
x[2] = 3 + 2*0  = 3
x[3] = 0 + 2*3  = 6
x[4] = 0 + 2*6  = 12
x[5] = 0 + 2*12 = 24
x[6] = -48 + 2*24 = 0   <- effect stops
```

Perfect.

## Multiple GP updates

Because recurrence is linear, the boundary marks from different updates can simply be added together.

## C++ modulo MOD

```cpp
vector<long long> d(n + 2, 0);

// powers[k] = r^k mod MOD

auto addGP = [&](int l, int r, long long a) {
    int len = r - l + 1;
    d[l] = (d[l] + a) % MOD;
    d[r + 1] = (d[r + 1] - a * powers[len]) % MOD;
};

long long cur = 0;
for (int i = 1; i <= n; ++i) {
    cur = (d[i] + r * cur) % MOD;
    if (cur < 0) cur += MOD;
    a[i] = (a[i] + cur) % MOD;
}
```

### Recognition signal

```text
sequence obeys x[i] = r*x[i-1]
               |
               v
store deviation from recurrence:
D[i] = x[i] - r*x[i-1]
```

This is the GP analogue of a difference array.

---

# 11. Coordinate Compression

## Why compression is needed

Suppose coordinates can be up to `10^12`.

You cannot allocate:

```cpp
vector<long long> a(1e12);
```

But maybe only `2e5` coordinates actually appear.

The goal is **not** to preserve distances by replacing values with arbitrary small numbers. The goal is to preserve ordering and event boundaries.

## Basic compression

Example:

```text
original = [40, 2, 10^11, 40, 2]
```

Unique sorted values:

```text
[2, 40, 10^11]
```

Ranks:

```text
2      -> 0
40     -> 1
10^11  -> 2
```

Compressed array:

```text
[1, 0, 2, 1, 0]
```

## Standard implementation

```cpp
vector<long long> vals = a;
sort(vals.begin(), vals.end());
vals.erase(unique(vals.begin(), vals.end()), vals.end());

for (long long x : a) {
    int rank = lower_bound(vals.begin(), vals.end(), x) - vals.begin();
    cout << rank << ' ';
}
```

## Interval problems: important points

For interval-add problems, common important points are:

```text
L
R+1
```

Why `R+1`?

Because `[L,R]` is active starting at `L` and stops starting at `R+1`.

### Event-line diagram

```text
service active on [2,5]

coordinate:  1   2   3   4   5   6   7
                 +-----------+
                 active

important points:
                 2           6 (=5+1)
```

The lecture summarizes coordinate-compression use as:

```text
1. Find important points.
2. Map important points.
3. Solve only on those points.
4. Recover/contribute the spans between points.
```

The last step is crucial. If compressed coordinates are `[2,4,6,9]`, the segment between 6 and 9 represents **three original coordinate units**, not one.

---

# 12. Difference Array Problem 2 — Snuke Prime

This problem combines:

```text
coordinate compression + difference array + segment lengths
```

## Problem model

Each service `i` is active on days `[a_i,b_i]` and costs `c_i` per day.

You can instead pay a daily cap `C`.

For each day:

```text
cost(day) = min(C, sum of c[i] over all active services)
```

Coordinates can be very large, so iterating every day is impossible.

## Key observation

The total active service cost changes only at:

```text
a_i
b_i + 1
```

Therefore those are the important coordinates.

## Example

Suppose:

```text
C = 10
service 1: [2,4], cost 6
service 2: [4,5], cost 5
```

Events:

```text
2: +6
5: -6       (4+1)
4: +5
6: -5       (5+1)
```

Sorted important coordinates:

```text
2, 4, 5, 6
```

Running cost on spans:

```text
[2,4): cost 6  -> days 2,3 -> 2 days
[4,5): cost 11 -> day 4    -> cap to 10
[5,6): cost 5  -> day 5
```

Total:

```text
2*6 + 1*10 + 1*5 = 27
```

## Sweep-line interpretation

You do not even need an explicit rank map. A sorted event map is enough:

```text
(time, change)
```

Between two event times, active cost is constant.

## C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    long long C;
    cin >> n >> C;

    map<long long, long long> event;

    for (int i = 0; i < n; ++i) {
        long long a, b, c;
        cin >> a >> b >> c;
        event[a] += c;
        event[b + 1] -= c;
    }

    long long ans = 0;
    long long active = 0;
    long long prev = 0;
    bool first = true;

    for (auto [day, delta] : event) {
        if (!first) {
            long long len = day - prev;
            ans += len * min(active, C);
        }

        active += delta;
        prev = day;
        first = false;
    }

    cout << ans << '\n';
}
```

### Complexity

```text
2N event points
Sorting/map: O(N log N)
Sweep:       O(N)
```

---

# 13. Kth Val — Difference Array + Sorting + Frequency Prefix

## Problem restatement

You have array:

```text
A[1], A[2], ..., A[N]
```

and `M` ranges `[l_i,r_i]`.

For each range, put every `A[l_i..r_i]` into a big multiset `S`.

Then sort `S`.

For each query `K`, return the K-th smallest value, or `-1` if `S` has fewer than `K` elements.

Constraints are large:

```text
N, M, Q <= 1e5
K <= 1e18
```

We can never build `S` explicitly.

## Decode the statement mathematically

Ask:

> How many times does each original index `i` get inserted into S?

Let:

```text
f[i] = number of ranges covering index i
```

Then `A[i]` appears exactly `f_i` times in `S`.

That turns the problem into:

```text
value A[i] with frequency f[i]
```

## Step 1 — Compute coverage frequencies with a difference array

For every range `[L,R]`:

```text
diff[L]     += 1
diff[R + 1] -= 1
```

Prefix it:

```text
freq[i] = freq[i-1] + diff[i]
```

### Dry run

```text
A = [8, 3, 5, 3, 10]

ranges:
[1,3]
[2,4]
[2,2]
```

Difference marks:

```text
range [1,3]: +1 at 1, -1 at 4
range [2,4]: +1 at 2, -1 at 5
range [2,2]: +1 at 2, -1 at 3

index:  1   2   3   4   5   6
D:     +1  +2  -1  -1  -1   0
```

Prefix:

```text
index:  1   2   3   4   5
freq:   1   3   2   1   0
```

Meaning:

```text
A[1]=8  appears 1 time
A[2]=3  appears 3 times
A[3]=5  appears 2 times
A[4]=3  appears 1 time
A[5]=10 appears 0 times
```

So conceptual multiset is:

```text
8, 3,3,3, 5,5, 3
```

Sort:

```text
3,3,3,3,5,5,8
```

But we do not actually create these copies.

## Step 2 — Sort `(value, frequency)` pairs

Pairs before sorting:

```text
(8,1)
(3,3)
(5,2)
(3,1)
(10,0)
```

Sort by value:

```text
(3,3)
(3,1)
(5,2)
(8,1)
(10,0)
```

Merge equal values:

```text
value   total frequency
  3          4
  5          2
  8          1
 10          0
```

## Step 3 — Prefix frequencies over sorted values

```text
value:       3    5    8
frequency:   4    2    1
cumulative:  4    6    7
```

Interpretation:

```text
positions 1..4 -> value 3
positions 5..6 -> value 5
position  7    -> value 8
```

ASCII:

```text
sorted S positions:

1  2  3  4 | 5  6 | 7
3  3  3  3 | 5  5 | 8
-----------   ----   -
cum <= 4      <= 6  <=7
```

## Step 4 — Answer K using lower_bound

We need the first cumulative frequency `>= K`.

For example:

```text
K=1 -> first cumulative >=1 is 4 -> answer 3
K=4 -> first cumulative >=4 is 4 -> answer 3
K=5 -> first cumulative >=5 is 6 -> answer 5
K=7 -> first cumulative >=7 is 7 -> answer 8
K=8 -> total size is 7          -> answer -1
```

## Full algorithm

```text
M ranges
   |
   v
index difference array
   |
   v
freq[i] = number of ranges covering i
   |
   v
pairs (A[i], freq[i])
   |
   v
sort by A[i]
   |
   v
merge same values
   |
   v
prefix frequency by sorted value
   |
   v
for each K: lower_bound(cumulative >= K)
```

## Correct C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int N, M, Q;
        cin >> N >> M >> Q;

        vector<int64> A(N + 1);
        for (int i = 1; i <= N; ++i) cin >> A[i];

        // Step 1: coverage count of every original index.
        vector<int64> diff(N + 2, 0);

        for (int i = 0; i < M; ++i) {
            int l, r;
            cin >> l >> r;
            diff[l] += 1;
            diff[r + 1] -= 1;
        }

        vector<int64> freq(N + 1, 0);
        for (int i = 1; i <= N; ++i) {
            freq[i] = freq[i - 1] + diff[i];
        }

        // Step 2: value + multiplicity.
        vector<pair<int64, int64>> items;
        items.reserve(N);

        for (int i = 1; i <= N; ++i) {
            if (freq[i] > 0) {
                items.push_back({A[i], freq[i]});
            }
        }

        sort(items.begin(), items.end());

        // Step 3: merge equal values and build cumulative frequencies.
        vector<int64> values;
        vector<int64> cumulative;

        for (auto [value, count] : items) {
            if (values.empty() || values.back() != value) {
                values.push_back(value);
                cumulative.push_back(count);
            } else {
                cumulative.back() += count;
            }
        }

        for (int i = 1; i < (int)cumulative.size(); ++i) {
            cumulative[i] += cumulative[i - 1];
        }

        // Step 4: answer each K-th query.
        for (int qi = 0; qi < Q; ++qi) {
            int64 K;
            cin >> K;

            int64 answer = -1;

            if (!cumulative.empty() && K <= cumulative.back()) {
                int pos = lower_bound(cumulative.begin(), cumulative.end(), K)
                        - cumulative.begin();
                answer = values[pos];
            }

            cout << answer << (qi + 1 == Q ? '\n' : ' ');
        }
    }
}
```

## Why `long long` is required

Maximum total size of conceptual `S` can be roughly:

```text
M * N = 10^5 * 10^5 = 10^10
```

So 32-bit `int` is unsafe for cumulative frequency.

`long long` is enough for these stated `N,M` limits, and queries themselves can be up to `10^18`.

## Complexity

For each test case:

```text
Read ranges + diff marks:   O(M)
Coverage prefix:            O(N)
Sort values/frequencies:    O(N log N)
Merge/prefix frequencies:   O(N)
Q lower_bound queries:      O(Q log N)

Total: O(M + N log N + Q log N)
Space: O(N)
```

## Why the obvious approaches fail

### Wrong/slow idea 1 — Explicitly build S

Worst conceptual size is about `10^10` elements.

Impossible in memory/time.

### Wrong/slow idea 2 — For every range, increment every index

```text
for every range [L,R]:
    for i=L..R:
        freq[i]++
```

Worst case O(NM) = O(10^10).

Use a difference array instead.

### Wrong idea 3 — Sort only A and ignore coverage

An element may appear zero times, once, or many times. Its multiplicity is the entire problem.

---

# 14. Pattern Recognition Cheat Sheet

## Prefix-sum patterns

| Signal in statement | Transformation | Data to precompute |
|---|---|---|
| many `sum(L,R)` | subtract prefixes | `ΣA[i]` |
| weighted `1,2,3,...` | `i-L+1 = i-(L-1)` | `ΣA[i]`, `ΣiA[i]` |
| AP weights `a+(i-L)d` | `di + (a-dL)` | `ΣA[i]`, `ΣiA[i]` |
| GP weights `r^(i-L)` | `r^i * r^-L` | `ΣA[i]r^i` |
| equal counts of two categories | map to `+1/-1` | prefix state + first occurrence |
| rectangle sums | inclusion-exclusion | 2D prefix |

## Difference-array patterns

| Signal in statement | Technique |
|---|---|
| many `[L,R] += X` updates | `diff[L]+=X`, `diff[R+1]-=X` |
| queries activate ranges of operations | difference array over operations, then another over array |
| add AP on a range | range-add linear coefficients `C*i+B` |
| add GP on a range | recurrence difference `D[i]=x[i]-r*x[i-1]` |
| huge coordinates, few changes | event points + compression/sweep |
| K-th value after repeated ranges | coverage diff + sort by value + cumulative frequency |

## The 60-second decoding checklist

When you read a problem, ask in this order:

```text
1. What is repeated?
   - queries?
   - range updates?
   - range selections?

2. Can I replace a whole range by two boundary events?
   YES -> difference array candidate

3. Can I replace a range answer by total-to-R minus total-before-L?
   YES -> prefix sum candidate

4. Are weights based on position inside [L,R]?
   Write local position = i-L+1
   Then algebraically expand.

5. Does the same prefix/state repeating imply something about the middle?
   YES -> prefix-state + map

6. Are coordinates huge but only boundaries matter?
   YES -> coordinate compression / sweep line

7. Is the answer asking for K-th after multiplicities?
   Convert to value + frequency, cumulative counts, lower_bound.
```

---

# 15. Complexity Cheat Sheet

```text
1D prefix build                      O(N)
1D range query                      O(1)
2D prefix build                     O(NM)
2D rectangle query                  O(1)
Weighted AP prefix build            O(N)
Weighted AP query                   O(1)
Difference update                   O(1)
Recover diff array                  O(N)
Greg and Array                      O(N + M + K)
Coordinate compression              O(N log N)
Snuke Prime                         O(N log N)
Kth Val                             O(M + N log N + Q log N)
```

---

# 16. Practice Checklist

After studying this notebook, you should be able to derive these without memorizing code:

- [ ] `sum(L,R) = P[R]-P[L-1]`
- [ ] 2D rectangle inclusion-exclusion formula
- [ ] Why weighted local index is `i-L+1`
- [ ] Why `i-L+1 = i-(L-1)` creates two prefix sums
- [ ] General AP query formula `d*Σ(iA[i]) + (a-dL)*ΣA[i]`
- [ ] GP exponent shifting `r^(i-L)=r^i*r^-L`
- [ ] Convert equal 0/1 counts to zero-sum using `0 -> -1`, `1 -> +1`
- [ ] Boundary update `diff[L]+=X`, `diff[R+1]-=X`
- [ ] Two-layer difference arrays in Greg and Array
- [ ] AP range update as linear function `C*i+B`
- [ ] GP range update via recurrence difference
- [ ] Why interval compression keeps `L` and `R+1`
- [ ] Why compressed spans must multiply by coordinate distance
- [ ] Kth Val: ranges -> coverage frequency -> sort -> cumulative -> lower_bound

---

# Source / Lecture Mapping

This tutorial was organized around the materials you supplied:

- **TLE Level 2 — Prefix Sums**: 1D prefix sums, O(1) subarray queries, 2D prefix sums, rectangle inclusion-exclusion, Forest Queries.
- **TLE Level 2 — Prefix Sums Problem Solving & Difference Arrays**: weighted prefix query, Contiguous Array, ordinary difference arrays, Greg and Array.
- **TLE Level 2 — Coordinate Compression**: compression principles and Snuke Prime.
- **GitHub — `PriyamTLE/Level-2-Lecture-Codes`, Module 01**: lecture implementations for 1D prefix sums, weighted prefix sums, 2D prefix sums, Contiguous Array, range-add difference arrays, and Snuke Prime.
- **Requested extensions**: AP/GP weighted prefix queries, AP/GP deferred range updates, and the full **Kth Val** derivation/solution.

GitHub module:
`https://github.com/PriyamTLE/Level-2-Lecture-Codes/tree/main/Module%2001%20-%20Prefix%20Sums%20%26%20Difference%20Arrays`

---

## Final mental picture

```text
PREFIX SUM
==========
values ----------------------------> accumulated information
                                         |
                                         +--> subtract two prefixes
                                         +--> answer range query fast

DIFFERENCE ARRAY
================
range updates --> boundary changes --> prefix reconstruction --> final values

COORDINATE COMPRESSION
======================
huge domain --> keep change points --> solve compressed spans --> multiply by span length

KTH VALUE
=========
ranges --> index coverage --> value frequencies --> sort values --> cumulative counts --> lower_bound(K)
```

The goal is not to memorize formulas. The goal is to recognize what should be **accumulated**, what should be represented only by **boundaries**, and which local-index expression can be rewritten in terms of the global index.
