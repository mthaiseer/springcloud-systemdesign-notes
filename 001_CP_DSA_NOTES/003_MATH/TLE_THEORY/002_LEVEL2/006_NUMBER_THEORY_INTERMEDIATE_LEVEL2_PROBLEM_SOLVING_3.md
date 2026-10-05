# Number Theory — Intermediate Level 2
## Problem Solving 4 — No Prime Differences & Division

> **Goal:** understand the observation that creates the solution. Do not memorize the final construction or loop.
>
> **Study flow:** prerequisites → what the problem asks → model → derivation → dry run → C++ → recognition.
>
> **Math rendering:** display equations use fenced `math` blocks only. Risky LaTeX macros and raw display delimiters are avoided.

---

# Clickable Table of Contents

- [0. Prerequisites](#0-prerequisites)
  - [0.1 Prime vs Composite](#01-prime-vs-composite)
  - [0.2 Difference and Multiples](#02-difference-and-multiples)
  - [0.3 Grid Adjacency](#03-grid-adjacency)
  - [0.4 Prime Factorisation](#04-prime-factorisation)
  - [0.5 Divisibility Through Prime Exponents](#05-divisibility-through-prime-exponents)
  - [0.6 Breaking Divisibility](#06-breaking-divisibility)
- [1. No Prime Differences — CF 1838C](#1-no-prime-differences--cf-1838c)
  - [1.1 What the Problem Asks](#11-what-the-problem-asks)
  - [1.2 Start With the Simplest Grid](#12-start-with-the-simplest-grid)
  - [1.3 Case 1 — m Is Composite](#13-case-1--m-is-composite)
  - [1.4 Why Row-Major Fails When m Is Prime](#14-why-row-major-fails-when-m-is-prime)
  - [1.5 Reorder Whole Row Blocks](#15-reorder-whole-row-blocks)
  - [1.6 Why the Construction Works](#16-why-the-construction-works)
  - [1.7 Dry Runs](#17-dry-runs)
  - [1.8 C++](#18-c)
  - [1.9 Don't-Memorize Model](#19-dont-memorize-model)
- [2. Division — CF 1444A](#2-division--cf-1444a)
  - [2.1 What the Problem Asks](#21-what-the-problem-asks)
  - [2.2 First Easy Case](#22-first-easy-case)
  - [2.3 Hard Case — q Divides p](#23-hard-case--q-divides-p)
  - [2.4 Prime-Exponent Model](#24-prime-exponent-model)
  - [2.5 How to Break Divisibility by q](#25-how-to-break-divisibility-by-q)
  - [2.6 Candidate Formula](#26-candidate-formula)
  - [2.7 Simpler Implementation Model](#27-simpler-implementation-model)
  - [2.8 Dry Runs](#28-dry-runs)
  - [2.9 C++](#29-c)
  - [2.10 Don't-Memorize Model](#210-dont-memorize-model)
- [3. Final Recognition Sheet](#3-final-recognition-sheet)
- [4. Master Mental Model](#4-master-mental-model)

---

# 0. Prerequisites

The two lecture problems train two different skills:

```text
No Prime Differences
→ constructive grid
→ control adjacent differences

Division
→ prime factorisation
→ prime exponents
→ remove the minimum necessary factor
```

---

## 0.1 Prime vs Composite

A **prime** number has exactly two positive divisors:

```text
1 and itself
```

Examples:

```text
2, 3, 5, 7, 11, 13, ...
```

A **composite** number has more than two positive divisors.

Examples:

```text
4  = 2 × 2
6  = 2 × 3
8  = 2 × 4
9  = 3 × 3
10 = 2 × 5
```

Important for the first problem:

```text
1 is NOT prime.
```

So a difference of:

```text
1
```

is always safe.

---

## 0.2 Difference and Multiples

If two numbers are:

```text
x
and
x + d
```

their absolute difference is:

```text
d
```

If:

```text
d = a × b
```

with:

```text
a > 1
b > 1
```

then `d` is composite.

Example:

```text
d = 10
10 = 2 × 5
```

So `10` cannot be prime.

This gives a useful constructive idea:

```text
Instead of checking primality of every adjacent difference,
force every difference to be obviously composite.
```

---

## 0.3 Grid Adjacency

In an `n × m` grid, two cells are adjacent when they share a side.

For:

```text
a[i][j]
```

possible neighbors are:

```text
a[i-1][j]   // up
a[i+1][j]   // down
a[i][j-1]   // left
a[i][j+1]   // right
```

There are only two kinds of edges we need to control:

```text
horizontal edges
vertical edges
```

This separation is very useful in constructive grid problems.

---

## 0.4 Prime Factorisation

Every positive integer greater than `1` can be written as:

```math
N=p_1^{a_1}p_2^{a_2}\cdots p_k^{a_k}
```

Example:

```text
72
= 8 × 9
= 2³ × 3²
```

So:

```text
72 = 2³ × 3²
```

Another example:

```text
12 = 2² × 3
```

---

## 0.5 Divisibility Through Prime Exponents

Suppose:

```math
A=\prod p_i^{x_i}
```

and:

```math
B=\prod p_i^{y_i}
```

Then:

```text
B divides A
```

exactly when every prime exponent needed by `B` is available in `A`.

In other words:

```text
for every prime p:

exponent of p in A
>=
exponent of p in B
```

---

### Example

```text
A = 72 = 2³ × 3²
B = 12 = 2² × 3¹
```

Compare exponents:

```text
prime 2:
A has 3
B needs 2

3 >= 2
```

and:

```text
prime 3:
A has 2
B needs 1

2 >= 1
```

Therefore:

```text
12 divides 72
```

---

### Counterexample

```text
A = 18 = 2 × 3²
B = 12 = 2² × 3
```

For prime `2`:

```text
A has 1
B needs 2
```

So:

```text
12 does NOT divide 18
```

This exponent comparison is the key to **Division**.

---

## 0.6 Breaking Divisibility

Suppose:

```text
q divides p
```

and we want to create a divisor `x` of `p` such that:

```text
q does NOT divide x
```

If:

```math
q=r_1^{a_1}r_2^{a_2}\cdots
```

then to make `q` fail to divide `x`, we only need to break **one** required prime exponent.

Example:

```text
q = 12 = 2² × 3
```

To make a number not divisible by `12`, either:

```text
exponent of 2 < 2
```

or:

```text
exponent of 3 < 1
```

We do **not** need to break every prime condition.

This becomes the main optimization in Problem 2.

---

# 1. No Prime Differences — CF 1838C

**Problem Link:** https://codeforces.com/contest/1838/problem/C

The lecture first studies a simple row-major grid, then rearranges whole row blocks when the vertical difference would otherwise be prime.

---

## 1.1 What the Problem Asks

Given:

```text
n, m
```

fill an:

```text
n × m
```

grid with every integer:

```text
1, 2, 3, ..., n×m
```

exactly once.

For every pair of side-adjacent cells:

```text
absolute difference must NOT be prime
```

We need any valid construction.

Official constraints guarantee:

```text
n >= 4
m >= 4
```

That lower bound is useful in the construction.

---

# 1.2 Start With the Simplest Grid

The simplest arrangement is row-major order.

For:

```text
n = 4
m = 4
```

write:

```text
1   2   3   4
5   6   7   8
9  10  11  12
13 14  15  16
```

Now inspect the two edge types.

---

## Horizontal Difference

Inside every row:

```text
2 - 1 = 1
3 - 2 = 1
4 - 3 = 1
```

So every horizontal difference is:

```text
1
```

And:

```text
1 is not prime
```

Therefore all horizontal edges are safe.

---

## Vertical Difference

Same column, consecutive rows:

```text
5 - 1 = 4
9 - 5 = 4
13 - 9 = 4
```

So vertical difference is:

```text
m
```

General row-major fact:

```text
horizontal difference = 1
vertical difference   = m
```

This observation nearly solves the problem.

---

# 1.3 Case 1 — m Is Composite

If `m` is composite:

```text
horizontal difference = 1
→ safe

vertical difference = m
→ composite
→ safe
```

Therefore ordinary row-major order works immediately.

---

## Example — n = 4, m = 6

Grid:

```text
1   2   3   4   5   6
7   8   9  10  11  12
13 14  15  16  17  18
19 20  21  22  23  24
```

Horizontal:

```text
difference = 1
```

Vertical:

```text
difference = 6
```

Since:

```text
6 = 2 × 3
```

it is composite.

So the grid is valid.

---

# 1.4 Why Row-Major Fails When m Is Prime

Suppose:

```text
m = 5
```

Row-major:

```text
1   2   3   4   5
6   7   8   9  10
11 12  13  14  15
16 17  18  19  20
```

Horizontal difference:

```text
1
```

safe.

But vertical difference:

```text
6 - 1 = 5
11 - 6 = 5
16 - 11 = 5
```

So:

```text
vertical difference = 5
```

and `5` is prime.

Invalid.

---

## What Should We Change?

Do we need to rearrange every number?

No.

Inside each row, consecutive numbers already give:

```text
difference = 1
```

which is perfect.

So keep each row **as one intact block**.

Only change the order of the row blocks.

This is the key constructive simplification.

---

# 1.5 Reorder Whole Row Blocks

For:

```text
n = 4
m = 5
```

the natural row blocks are:

```text
R0 =  1  2  3  4  5
R1 =  6  7  8  9 10
R2 = 11 12 13 14 15
R3 = 16 17 18 19 20
```

Natural row order:

```text
R0
R1
R2
R3
```

has neighboring row-index difference:

```text
1
```

Therefore vertical value difference:

```text
1 × m = m
```

Bad when `m` is prime.

---

## New Row Order

Use:

```text
R2
R0
R3
R1
```

Grid:

```text
11 12 13 14 15
1   2  3  4  5
16 17 18 19 20
6   7  8  9 10
```

Check row-index jumps:

```text
R2 -> R0
difference in row index = 2

R0 -> R3
difference = 3

R3 -> R1
difference = 2
```

Therefore vertical value differences are:

```text
2m
3m
2m
```

For `m = 5`:

```text
10
15
10
```

All composite.

---

## General Row Order

Let:

```text
h = floor(n/2)
```

Use original row indices in this order:

```text
h, 0, h+1, 1, h+2, 2, ...
```

Equivalent generation:

```text
output row i:

if i is even:
    original row = h + i/2

if i is odd:
    original row = i/2
```

This is exactly the compact constructive pattern.

---

# 1.6 Why the Construction Works

We must prove both edge directions.

---

## Horizontal Edges

We never change the order inside a row.

Every row remains:

```text
x, x+1, x+2, ...
```

Therefore:

```text
horizontal difference = 1
```

Safe.

---

## Vertical Edges

Suppose two adjacent output rows came from original rows:

```text
r1
and
r2
```

Corresponding values in the same column differ by:

```math
|r_1-r_2|\cdot m
```

Why?

Original row `r` starts at:

```text
r×m + 1
```

So same-column entries differ by exactly:

```text
row-index difference × m
```

---

## Why Is the Row-Index Difference at Least 2?

Let:

```text
h = floor(n/2)
```

The pattern alternates between:

```text
second-half row
first-half row
```

For `n >= 4`:

```text
h >= 2
```

Neighboring row-index gaps become:

```text
h
or
h+1
```

Both are at least `2`.

Therefore:

```text
vertical difference
=
m × k
```

where:

```text
k >= 2
```

So vertical difference has at least two factors:

```text
m
and
k
```

Both greater than `1`.

Hence it is composite.

---

## Important Recognition

We did not try to construct arbitrary numbers.

We preserved the property that was already good:

```text
horizontal difference = 1
```

and modified only the bad dimension:

```text
vertical difference
```

This is a common constructive strategy.

---

# 1.7 Dry Runs

## Dry Run 1 — Composite m

```text
n = 4
m = 4
```

Use row-major:

```text
1   2   3   4
5   6   7   8
9  10  11  12
13 14  15  16
```

Horizontal:

```text
1
```

Vertical:

```text
4
```

Neither is prime.

Valid.

---

## Dry Run 2 — Prime m

```text
n = 4
m = 5
```

Natural blocks:

```text
R0 = 1..5
R1 = 6..10
R2 = 11..15
R3 = 16..20
```

Reorder:

```text
R2
R0
R3
R1
```

Output:

```text
11 12 13 14 15
1   2  3  4  5
16 17 18 19 20
6   7  8  9 10
```

Horizontal differences:

```text
1
```

Vertical differences:

```text
10, 15, 10
```

All safe.

---

## Dry Run 3 — Odd n

```text
n = 5
m = 5
```

Here:

```text
h = floor(5/2)
  = 2
```

Row order:

```text
2, 0, 3, 1, 4
```

Row-index gaps:

```text
2
3
2
3
```

Vertical differences:

```text
2×5 = 10
3×5 = 15
2×5 = 10
3×5 = 15
```

All composite.

---

# 1.8 C++

This version follows the lecture's two-case idea:

```text
m composite
→ row-major

m prime
→ reorder row blocks
```

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isPrime(int x) {
    if (x < 2)
        return false;

    for (int d = 2; d * d <= x; ++d) {
        if (x % d == 0)
            return false;
    }

    return true;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int n, m;
        cin >> n >> m;

        if (!isPrime(m)) {

            // Simple row-major construction.
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < m; ++j) {
                    cout << i * m + j + 1 << ' ';
                }
                cout << '\n';
            }

        } else {

            // Reorder complete row blocks.
            int h = n / 2;

            for (int outRow = 0; outRow < n; ++outRow) {

                int originalRow;

                if (outRow % 2 == 0)
                    originalRow = h + outRow / 2;
                else
                    originalRow = outRow / 2;

                for (int j = 0; j < m; ++j) {
                    cout << originalRow * m + j + 1 << ' ';
                }

                cout << '\n';
            }
        }
    }
}
```

Complexity:

```text
Output itself contains n×m numbers.

Time:
O(n×m)

Extra space:
O(1)
```

The primality test for `m` is tiny compared with printing the grid.

---

# 1.9 Don't-Memorize Model

Do not memorize a strange row formula.

Derive it:

```text
Need all adjacent differences non-prime
              |
              v
try row-major
              |
       +------+------+
       |             |
 horizontal       vertical
 difference       difference
    = 1              = m
       |             |
     safe       composite?
                     |
             +-------+-------+
             |               |
            YES              NO
             |               |
        row-major        m is prime
                            |
                            v
                  keep rows internally
                            |
                            v
                  reorder row blocks
                            |
                            v
               vertical diff = k×m
                            |
                            v
                     force k >= 2
```

Memory anchor:

```text
Preserve the good dimension.
Fix only the bad dimension.
```

---

# 2. Division — CF 1444A

**Problem Link:** https://codeforces.com/contest/1444/problem/A

---

## 2.1 What the Problem Asks

Given:

```text
p
q
```

find the largest integer:

```text
x
```

such that:

```text
x divides p
```

but:

```text
q does NOT divide x
```

In symbols:

```text
x | p
```

and:

```text
x % q != 0
```

We want the **largest** possible `x`.

---

# 2.2 First Easy Case

Ask:

```text
Does q divide p?
```

If:

```text
p % q != 0
```

then `p` itself already satisfies both conditions.

Why?

```text
p divides p
```

and:

```text
q does not divide p
```

Also, no divisor of `p` can be larger than `p`.

Therefore:

```text
answer = p
```

---

## Example

```text
p = 10
q = 4
```

Check:

```text
10 % 4 = 2
```

So `q` does not divide `p`.

Largest divisor of `p`:

```text
p itself = 10
```

Answer:

```text
10
```

This is why the first test should always be:

```cpp
if (p % q != 0)
    answer = p;
```

---

# 2.3 Hard Case — q Divides p

Now suppose:

```text
p % q == 0
```

Then:

```text
x = p
```

is invalid.

We need to divide `p` by something.

But we want `x` as large as possible.

So the real question is:

```text
What is the minimum factor we must remove from p
so that q stops dividing the result?
```

This is a prime-exponent problem.

---

# 2.4 Prime-Exponent Model

Suppose:

```math
q=r_1^{a_1}r_2^{a_2}\cdots r_k^{a_k}
```

Since:

```text
q divides p
```

`p` contains every required prime with enough exponent:

```math
p=r_1^{b_1}r_2^{b_2}\cdots
```

where:

```text
b_i >= a_i
```

for every prime factor of `q`.

---

## Example

```text
p = 72
q = 12
```

Factor:

```text
p = 72 = 2³ × 3²
q = 12 = 2² × 3¹
```

Compare:

```text
prime    p exponent    q needs
2            3            2
3            2            1
```

Both requirements are satisfied.

Therefore:

```text
12 divides 72
```

---

# 2.5 How to Break Divisibility by q

To make:

```text
q does not divide x
```

we only need **one** prime requirement to fail.

For:

```text
q = 2² × 3
```

we can either force:

```text
exponent of 2 < 2
```

or:

```text
exponent of 3 < 1
```

We should try breaking each prime factor separately and keep the largest result.

---

## Break Using Prime 2

For:

```text
p = 72 = 2³ × 3²
```

current exponent of `2`:

```text
3
```

`q` needs:

```text
2
```

To make divisibility fail, exponent must become:

```text
1
```

So remove:

```text
3 - 1 = 2 copies of 2
```

Equivalent formula:

```text
remove:
b - a + 1
```

Here:

```text
b = 3
a = 2
```

So:

```text
3 - 2 + 1 = 2
```

Candidate:

```text
72 / 2²
= 72 / 4
= 18
```

Check:

```text
18 % 12 != 0
```

Valid.

---

## Break Using Prime 3

Current exponent in `p`:

```text
2
```

`q` needs:

```text
1
```

To fail:

```text
exponent < 1
```

so exponent must become `0`.

Remove:

```text
2 - 1 + 1
= 2 copies of 3
```

Candidate:

```text
72 / 3²
= 72 / 9
= 8
```

Valid.

Compare:

```text
18
8
```

Largest:

```text
18
```

So answer is:

```text
18
```

---

# 2.6 Candidate Formula

For a prime factor `r` of `q`:

```text
a = exponent of r in q
b = exponent of r in p
```

Since `q | p`:

```text
b >= a
```

To make `q` fail, the exponent of `r` in `x` must become at most:

```text
a - 1
```

So remove:

```text
b - (a-1)
```

copies.

Simplify:

```text
b - a + 1
```

Therefore candidate:

```math
x_r
=
\frac{p}{r^{b-a+1}}
```

Try this for every **distinct prime factor of q**.

Final answer:

```text
maximum candidate
```

---

# 2.7 Simpler Implementation Model

We do not actually need to calculate `a` and `b` explicitly.

For each distinct prime factor `r` of `q`:

```text
cur = p
```

While `q` still divides `cur`:

```text
cur /= r
```

The first moment:

```text
cur % q != 0
```

we have removed exactly enough copies of `r`.

So:

```cpp
long long cur = p;

while (cur % q == 0) {
    cur /= r;
}
```

This produces the same candidate as the exponent formula.

Try it for every prime divisor `r` of `q`.

Take maximum.

---

## Why This Greedy Division Is Safe

For one chosen prime `r`, we want to remove as little as possible.

If:

```text
cur % q == 0
```

we have not broken divisibility yet.

So another division by `r` is necessary.

The first time:

```text
cur % q != 0
```

we stop immediately.

Any additional division would only make `cur` smaller.

Therefore this is the maximum candidate obtained by breaking `q` through prime `r`.

---

# 2.8 Dry Runs

## Dry Run 1 — Easy Case

```text
p = 10
q = 4
```

Check:

```text
10 % 4 != 0
```

Therefore:

```text
answer = 10
```

No factorisation needed.

---

## Dry Run 2 — p = 12, q = 6

Factor:

```text
q = 2 × 3
```

Since:

```text
12 % 6 = 0
```

we must break divisibility.

---

### Try prime 2

```text
cur = 12
```

Still divisible by `6`:

```text
12 % 6 = 0
```

Divide by `2`:

```text
cur = 6
```

Still:

```text
6 % 6 = 0
```

Divide by `2` again:

```text
cur = 3
```

Now:

```text
3 % 6 != 0
```

Candidate:

```text
3
```

---

### Try prime 3

```text
cur = 12
```

Divide by `3`:

```text
cur = 4
```

Now:

```text
4 % 6 != 0
```

Candidate:

```text
4
```

Take maximum:

```text
max(3,4)=4
```

Answer:

```text
4
```

---

## Dry Run 3 — p = 72, q = 12

Factor:

```text
q = 2² × 3
```

Distinct prime factors:

```text
2, 3
```

---

### Prime 2

```text
72 % 12 = 0
72 / 2 = 36

36 % 12 = 0
36 / 2 = 18

18 % 12 != 0
```

Candidate:

```text
18
```

---

### Prime 3

```text
72 % 12 = 0
72 / 3 = 24

24 % 12 = 0
24 / 3 = 8

8 % 12 != 0
```

Candidate:

```text
8
```

Answer:

```text
18
```

This exactly matches the prime-exponent derivation.

---

## Dry Run 4 — q Has One Prime Factor

```text
p = 64
q = 8
```

Factor:

```text
q = 2³
```

Only prime factor:

```text
2
```

Start:

```text
64
```

Divide while still divisible by `8`:

```text
64 / 2 = 32
32 / 2 = 16
16 / 2 = 8
8  / 2 = 4
```

Now:

```text
4 % 8 != 0
```

Answer:

```text
4
```

Exponent view:

```text
p = 2^6
q = 2^3

remove:
6 - 3 + 1
= 4 copies of 2

2^6 / 2^4
= 2^2
= 4
```

Same result.

---

# 2.9 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;

vector<int64> distinctPrimeFactors(int64 q) {
    vector<int64> primes;

    for (int64 d = 2; d * d <= q; ++d) {
        if (q % d != 0)
            continue;

        primes.push_back(d);

        while (q % d == 0) {
            q /= d;
        }
    }

    if (q > 1) {
        primes.push_back(q);
    }

    return primes;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int64 p, q;
        cin >> p >> q;

        // p itself is already valid.
        if (p % q != 0) {
            cout << p << '\n';
            continue;
        }

        vector<int64> primes =
            distinctPrimeFactors(q);

        int64 answer = 1;

        for (int64 prime : primes) {
            int64 cur = p;

            while (cur % q == 0) {
                cur /= prime;
            }

            answer = max(answer, cur);
        }

        cout << answer << '\n';
    }
}
```

---

## Complexity

Factorising `q`:

```text
O(sqrt(q))
```

For each distinct prime factor, repeated divisions are very few.

With:

```text
p <= 10^18
```

a prime can divide `p` only a logarithmic number of times.

Overall:

```text
O(sqrt(q) + small logarithmic work)
```

which is easily fast enough.

---

# 2.10 Don't-Memorize Model

Do not memorize:

```text
factor q
divide p by each prime until p%q != 0
```

Derive it:

```text
Need largest x
such that x | p
but q does not divide x
             |
             v
Is p already valid?
             |
      +------+------+
      |             |
     YES            NO
      |             |
   answer p       q | p
                    |
                    v
           Why does q divide p?
                    |
                    v
          every required prime
          exponent is present
                    |
                    v
        Break ONE prime requirement
                    |
                    v
       for each prime factor r of q
                    |
                    v
        remove minimum copies of r
                    |
                    v
             candidate x
                    |
                    v
            take maximum
```

Memory anchor:

```text
To break divisibility,
make one required prime exponent insufficient.
```

---

# 3. Final Recognition Sheet

| Problem signal | Think |
|---|---|
| constructive grid + adjacent differences | separate horizontal and vertical edges |
| consecutive numbers in a row | horizontal difference = 1 |
| row-major grid | vertical difference = number of columns |
| one dimension already works | preserve it |
| vertical difference is prime | reorder whole row blocks |
| want obviously non-prime difference | force product of two values > 1 |
| largest divisor `x` of `p` | start from `p` |
| condition `x % q != 0` | break divisibility by `q` |
| `q | p` | compare prime exponents |
| need `q` not to divide | make one prime exponent too small |
| maximize remaining divisor | remove minimum necessary prime power |

---

# 4. Master Mental Model

## No Prime Differences

```text
Try natural construction
        |
        v
analyze edge types separately
        |
    +---+---+
    |       |
 horizontal vertical
    |       |
   diff 1   diff m
    |       |
  safe    composite?
            |
      +-----+-----+
      |           |
     yes         no
      |           |
    done       m prime
                  |
                  v
         keep each row intact
                  |
                  v
         reorder row blocks
                  |
                  v
        vertical = k × m
                  |
                  v
             force k >= 2
```

---

## Division

```text
Want largest valid divisor of p
            |
            v
Is p already valid?
            |
       +----+----+
       |         |
      yes       no
       |         |
    answer p    q | p
                 |
                 v
       prime-factor model
                 |
                 v
      q requires several
      prime exponents
                 |
                 v
     break only ONE requirement
                 |
                 v
   remove the minimum necessary
   power for each prime factor
                 |
                 v
          take maximum
```

---

# Compact Revision Card

```text
NO PRIME DIFFERENCES
--------------------
row-major:

horizontal diff = 1
vertical diff   = m

if m composite:
    row-major works

if m prime:
    keep rows intact
    reorder original row indices:

    h, 0, h+1, 1, h+2, 2, ...

    h = floor(n/2)

vertical diff:
|r1-r2| × m

n >= 4
=> row gap >= 2
=> vertical difference composite


DIVISION
--------
Need maximum x:

x | p
q does not divide x

if p % q != 0:
    answer = p

otherwise:

factor q into distinct primes

for each prime r:
    cur = p

    while cur % q == 0:
        cur /= r

    answer = max(answer, cur)

WHY?
-----
q | x means:
every prime exponent required by q
is present in x.

To make q not divide x:
break one prime exponent.

To maximize x:
remove the minimum necessary amount.
```

> **Core habit:** first identify the exact property that makes the naive construction invalid. Then change only that property instead of rebuilding the whole solution.
